package com.xyz.Service;

import com.xyz.model.Appointment;
import com.xyz.model.Patient;
import com.xyz.model.Doctor;
import com.xyz.Data.AppointmentRepository;
import com.xyz.Data.PatientRepository;
import com.xyz.Data.DoctorRepository;
import com.xyz.Utility.Validator;
import com.xyz.Utility.TimeSlotValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorService doctorService;
    private final NotificationService notificationService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              DoctorService doctorService,
                              NotificationService notificationService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.doctorService = doctorService;
        this.notificationService = notificationService;
    }

    public Appointment bookAppointment(String patientId, String doctorId,
                                       LocalDateTime appointmentDateTime, String timeSlot) {

        if (appointmentDateTime == null || !Validator.isValidDateTime(appointmentDateTime)) {
            throw new IllegalArgumentException("Invalid appointment date/time. Must be in the future.");
        }

        if (timeSlot == null || !TimeSlotValidator.isValidTimeFormat(timeSlot)) {
            throw new IllegalArgumentException("Invalid time slot format: " + timeSlot + ". Please use HH:MM or HH:MM AM/PM format.");
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + patientId));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + doctorId));

        // Check if the doctor has a time slot that contains this appointment time
        String containingSlot = TimeSlotValidator.findContainingTimeSlot(timeSlot, new java.util.ArrayList<>(doctor.getAvailableTimeSlots()));
        
        if (containingSlot == null) {
            throw new IllegalArgumentException("Time slot " + timeSlot + " is not available for this doctor. Available slots: " + doctor.getAvailableTimeSlots());
        }

        if (appointmentRepository.existsByDoctorAndDateTime(doctorId, appointmentDateTime)) {
            throw new IllegalArgumentException("Doctor already has an active appointment at this time.");
        }

        Appointment appointment = new Appointment(null, patientId, doctorId, appointmentDateTime, timeSlot);
        appointment = appointmentRepository.save(appointment);

        // Remove the containing time slot from doctor's availability
        doctorService.removeTimeSlot(doctorId, containingSlot);

        notificationService.sendAppointmentConfirmation(patient, doctor, appointment);

        return appointment;
    }

    public Appointment cancelAppointment(String appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + appointmentId));

        Appointment.AppointmentStatus status = appointment.getStatus();
        if (status != Appointment.AppointmentStatus.SCHEDULED &&
            status != Appointment.AppointmentStatus.CONFIRMED) {
            throw new IllegalArgumentException("Cannot cancel appointment with status: " + status.getDisplayName());
        }

        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        appointment = appointmentRepository.save(appointment);

        doctorService.addTimeSlot(appointment.getDoctorId(), appointment.getTimeSlot());

        Optional<Patient> patientOpt = patientRepository.findById(appointment.getPatientId());
        Optional<Doctor> doctorOpt = doctorRepository.findById(appointment.getDoctorId());

        if (patientOpt.isPresent() && doctorOpt.isPresent()) {
            notificationService.sendCancellationNotification(patientOpt.get(), doctorOpt.get(), appointment);
        } else {
            System.err.println("Warning: Patient or Doctor not found for cancellation notification for appointment ID: " + appointmentId);
        }

        return appointment;
    }

    public Appointment rescheduleAppointment(String appointmentId, LocalDateTime newDateTime, String newTimeSlot) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + appointmentId));

        Appointment.AppointmentStatus status = appointment.getStatus();
        if (status != Appointment.AppointmentStatus.SCHEDULED &&
            status != Appointment.AppointmentStatus.CONFIRMED &&
            status != Appointment.AppointmentStatus.RESCHEDULED) {
            throw new IllegalArgumentException("Cannot reschedule appointment with status: " + status.getDisplayName());
        }

        if (newDateTime == null || !Validator.isValidDateTime(newDateTime)) {
            throw new IllegalArgumentException("Invalid new appointment date/time. Must be in the future.");
        }

        if (newTimeSlot == null || !TimeSlotValidator.isValidTimeFormat(newTimeSlot)) {
            throw new IllegalArgumentException("Invalid new time slot format: " + newTimeSlot + ". Please use HH:MM or HH:MM AM/PM format.");
        }

        String doctorId = appointment.getDoctorId();

        // Check if the doctor has a time slot that contains this new appointment time
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + doctorId));
        
        String newContainingSlot = TimeSlotValidator.findContainingTimeSlot(newTimeSlot, new java.util.ArrayList<>(doctor.getAvailableTimeSlots()));
        
        if (newContainingSlot == null) {
            throw new IllegalArgumentException("New time slot " + newTimeSlot + " is not available for this doctor. Available slots: " + doctor.getAvailableTimeSlots());
        }

        if (appointmentRepository.existsByDoctorAndDateTime(doctorId, newDateTime)) {
            throw new IllegalArgumentException("Doctor already has an active appointment at the new time.");
        }

        // Find the old containing slot to add back
        String oldContainingSlot = TimeSlotValidator.findContainingTimeSlot(appointment.getTimeSlot(), new java.util.ArrayList<>(doctor.getAvailableTimeSlots()));
        
        if (!appointment.getTimeSlot().equals(newTimeSlot)) {
            // Add back the old time slot if it was different
            if (oldContainingSlot != null) {
                doctorService.addTimeSlot(doctorId, oldContainingSlot);
            }
        }

        appointment.setAppointmentDateTime(newDateTime);
        appointment.setTimeSlot(newTimeSlot);
        appointment.setStatus(Appointment.AppointmentStatus.RESCHEDULED);
        appointment = appointmentRepository.save(appointment);

        // Remove the new containing time slot from doctor's availability
        doctorService.removeTimeSlot(doctorId, newContainingSlot);

        Optional<Patient> patientOpt = patientRepository.findById(appointment.getPatientId());
        Optional<Doctor> doctorOpt = doctorRepository.findById(appointment.getDoctorId());

        if (patientOpt.isPresent() && doctorOpt.isPresent()) {
            notificationService.sendReschedulingNotification(patientOpt.get(), doctorOpt.get(), appointment);
        } else {
            System.err.println("Warning: Patient or Doctor not found for rescheduling notification for appointment ID: " + appointmentId);
        }

        return appointment;
    }

    public Optional<Appointment> getAppointmentById(String appointmentId) {
        return appointmentRepository.findById(appointmentId);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointmentsByPatient(String patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<Appointment> getAppointmentsByDoctor(String doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    public List<Appointment> getAppointmentsByStatus(Appointment.AppointmentStatus status) {
        return appointmentRepository.findByStatus(status);
    }

    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        return appointmentRepository.findByDate(date);
    }

    public List<Appointment> getAppointmentsByDoctorAndDate(String doctorId, LocalDate date) {
        return appointmentRepository.findByDoctorAndDate(doctorId, date);
    }

    public Appointment markAppointmentCompleted(String appointmentId, String notes) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + appointmentId));

        Appointment.AppointmentStatus status = appointment.getStatus();
        if (status != Appointment.AppointmentStatus.SCHEDULED &&
            status != Appointment.AppointmentStatus.CONFIRMED &&
            status != Appointment.AppointmentStatus.RESCHEDULED) {
            throw new IllegalArgumentException("Cannot mark appointment as completed with status: " + status.getDisplayName());
        }

        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        appointment.setNotes(notes != null ? notes : "");

        return appointmentRepository.save(appointment);
    }

    public Appointment markAppointmentNoShow(String appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + appointmentId));

        Appointment.AppointmentStatus status = appointment.getStatus();
        if (status != Appointment.AppointmentStatus.SCHEDULED &&
            status != Appointment.AppointmentStatus.CONFIRMED &&
            status != Appointment.AppointmentStatus.RESCHEDULED) {
            throw new IllegalArgumentException("Cannot mark appointment as no-show with status: " + status.getDisplayName());
        }

        appointment.setStatus(Appointment.AppointmentStatus.NO_SHOW);

        doctorService.addTimeSlot(appointment.getDoctorId(), appointment.getTimeSlot());

        return appointmentRepository.save(appointment);
    }
}
