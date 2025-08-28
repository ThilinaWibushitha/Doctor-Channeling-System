package com.xyz.Service;

import com.xyz.Data.DoctorRepository;
import com.xyz.Utility.Validator;
import com.xyz.model.Doctor;

import java.util.List;
import java.util.Optional;

public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public Doctor registerDoctor(String firstName, String lastName, String specialization,
                                 String email, String phone, String licenseNumber,
                                 double consultationFee, String qualification, int experienceYears) {

        // Validate input data
        String validationError = Validator.validateDoctorData(firstName, lastName, email, phone,
                consultationFee, experienceYears);
        if (validationError != null) {
            throw new IllegalArgumentException(validationError);
        }

        try {
            // Check for duplicate email
            if (doctorRepository.findByEmail(email).isPresent()) {
                throw new IllegalArgumentException("Doctor with this email already exists.");
            }

            // Check for duplicate license number
            if (doctorRepository.findByLicenseNumber(licenseNumber).isPresent()) {
                throw new IllegalArgumentException("Doctor with this license number already exists.");
            }

            Doctor doctor = new Doctor(null, firstName, lastName, specialization, email, phone,
                    licenseNumber, consultationFee, qualification, experienceYears);

            return doctorRepository.save(doctor);
        } catch (Exception e) {
            System.out.println("⚠️ Using demo mode due to database connection issue: " + e.getMessage());
            // Generate a demo ID
            String demoId = "D" + String.format("%05d", DemoDataService.getDemoDoctors().size() + 1);
            Doctor doctor = new Doctor(demoId, firstName, lastName, specialization, email, phone,
                    licenseNumber, consultationFee, qualification, experienceYears);
            DemoDataService.addDemoDoctor(doctor);
            return doctor;
        }
    }

    public List<Doctor> getAllDoctors() {
        try {
            return doctorRepository.findAll();
        } catch (Exception e) {
            System.out.println("⚠️ Using demo data due to database connection issue: " + e.getMessage());
            return DemoDataService.getDemoDoctors();
        }
    }

    public Optional<Doctor> getDoctorById(String doctorId) {
        try {
            return doctorRepository.findById(doctorId);
        } catch (Exception e) {
            System.out.println("⚠️ Using demo data due to database connection issue: " + e.getMessage());
            return DemoDataService.findDoctorById(doctorId);
        }
    }

    public List<Doctor> searchDoctorsBySpecialization(String specialization) {
        try {
            return doctorRepository.findBySpecialization(specialization);
        } catch (Exception e) {
            System.out.println("⚠️ Using demo data due to database connection issue: " + e.getMessage());
            return DemoDataService.searchDoctorsBySpecialization(specialization);
        }
    }

    public List<Doctor> searchDoctorsByName(String name) {
        try {
            return doctorRepository.findByName(name);
        } catch (Exception e) {
            System.out.println("⚠️ Using demo data due to database connection issue: " + e.getMessage());
            return DemoDataService.searchDoctorsByName(name);
        }
    }

    public Doctor updateDoctor(String doctorId, String firstName, String lastName,
                               String specialization, String email, String phone,
                               double consultationFee, String qualification, int experienceYears) {

        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            throw new IllegalArgumentException("Doctor not found with ID: " + doctorId);
        }

        // Validate input data
        String validationError = Validator.validateDoctorData(firstName, lastName, email, phone,
                consultationFee, experienceYears);
        if (validationError != null) {
            throw new IllegalArgumentException(validationError);
        }

        Doctor doctor = doctorOpt.get();

        // Check email uniqueness (excluding current doctor)
        Optional<Doctor> existingDoctor = doctorRepository.findByEmail(email);
        if (existingDoctor.isPresent() && !existingDoctor.get().getDoctorId().equals(doctorId)) {
            throw new IllegalArgumentException("Another doctor with this email already exists.");
        }

        doctor.setFirstName(firstName);
        doctor.setLastName(lastName);
        doctor.setSpecialization(specialization);
        doctor.setEmail(email);
        doctor.setPhoneNumber(phone);
        doctor.setConsultationFee(consultationFee);
        doctor.setQualification(qualification);
        doctor.setExperienceYears(experienceYears);

        return doctorRepository.save(doctor);
    }

    // Rename to deleteDoctorById for clarity (and match usage in your UI)
    public void deleteDoctorById(String doctorId) {
        try {
            if (!doctorRepository.existsById(doctorId)) {
                throw new IllegalArgumentException("Doctor not found with ID: " + doctorId);
            }
            doctorRepository.deleteById(doctorId);
        } catch (Exception e) {
            System.out.println("⚠️ Using demo mode due to database connection issue: " + e.getMessage());
            DemoDataService.removeDemoDoctor(doctorId);
        }
    }

    public void addTimeSlot(String doctorId, String timeSlot) {
        if (!Validator.isValidTimeSlot(timeSlot)) {
            throw new IllegalArgumentException("Invalid time slot format. Use HH:MM-HH:MM");
        }

        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            throw new IllegalArgumentException("Doctor not found with ID: " + doctorId);
        }

        Doctor doctor = doctorOpt.get();
        if (!doctor.addTimeSlot(timeSlot)) {
            System.out.println("Time slot " + timeSlot + " already exists for doctor " + doctorId);
        }
        doctorRepository.save(doctor);
    }

    public void removeTimeSlot(String doctorId, String timeSlot) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            throw new IllegalArgumentException("Doctor not found with ID: " + doctorId);
        }

        Doctor doctor = doctorOpt.get();
        if (!doctor.removeTimeSlot(timeSlot)) {
            System.out.println("Time slot " + timeSlot + " not found for doctor " + doctorId);
        }
        doctorRepository.save(doctor);
    }

    public boolean isSlotAvailable(String doctorId, String timeSlot) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        return doctorOpt.map(doctor -> doctor.isSlotAvailable(timeSlot)).orElse(false);
    }
}
