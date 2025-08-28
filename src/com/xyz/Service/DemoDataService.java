package com.xyz.Service;

import com.xyz.model.Doctor;
import com.xyz.model.Patient;
import com.xyz.model.Appointment;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class DemoDataService {
    
    private static final List<Doctor> demoDoctors = new ArrayList<>();
    private static final List<Patient> demoPatients = new ArrayList<>();
    private static final List<Appointment> demoAppointments = new ArrayList<>();
    
    static {
        initializeDemoData();
    }
    
    private static void initializeDemoData() {
        // Demo Doctors
        demoDoctors.add(new Doctor("D00001", "Dr. John", "Smith", "Cardiology", 
            "john.smith@hospital.com", "+1234567890", "LIC001", 150.0, "MD", 15));
        demoDoctors.add(new Doctor("D00002", "Dr. Sarah", "Johnson", "Pediatrics", 
            "sarah.johnson@hospital.com", "+1234567891", "LIC002", 120.0, "MD", 10));
        demoDoctors.add(new Doctor("D00003", "Dr. Michael", "Brown", "Orthopedics", 
            "michael.brown@hospital.com", "+1234567892", "LIC003", 180.0, "MD", 20));
        demoDoctors.add(new Doctor("D00004", "Dr. Emily", "Davis", "Neurology", 
            "emily.davis@hospital.com", "+1234567893", "LIC004", 200.0, "MD", 12));
        demoDoctors.add(new Doctor("D00005", "Dr. David", "Wilson", "Dermatology", 
            "david.wilson@hospital.com", "+1234567894", "LIC005", 130.0, "MD", 8));
            
        // Demo Patients
        demoPatients.add(new Patient("P00001", "Alice", "Johnson", "alice.johnson@email.com", 
            "+1987654321", LocalDate.of(1985, 5, 15), "123 Main St, City"));
        demoPatients.add(new Patient("P00002", "Bob", "Williams", "bob.williams@email.com", 
            "+1987654322", LocalDate.of(1990, 8, 22), "456 Oak Ave, Town"));
        demoPatients.add(new Patient("P00003", "Carol", "Miller", "carol.miller@email.com", 
            "+1987654323", LocalDate.of(1978, 12, 3), "789 Pine Rd, Village"));
        demoPatients.add(new Patient("P00004", "David", "Taylor", "david.taylor@email.com", 
            "+1987654324", LocalDate.of(1995, 3, 10), "321 Elm St, Borough"));
        demoPatients.add(new Patient("P00005", "Eva", "Anderson", "eva.anderson@email.com", 
            "+1987654325", LocalDate.of(1988, 7, 28), "654 Maple Dr, County"));
            
        // Demo Appointments
        demoAppointments.add(new Appointment("A00001", "P00001", "D00001", 
            LocalDateTime.of(2024, 1, 15, 10, 0), "10:00-11:00"));
        demoAppointments.add(new Appointment("A00002", "P00002", "D00002", 
            LocalDateTime.of(2024, 1, 16, 14, 0), "14:00-15:00"));
        demoAppointments.add(new Appointment("A00003", "P00003", "D00003", 
            LocalDateTime.of(2024, 1, 17, 9, 0), "09:00-10:00"));
    }
    
    public static List<Doctor> getDemoDoctors() {
        return new ArrayList<>(demoDoctors);
    }
    
    public static List<Patient> getDemoPatients() {
        return new ArrayList<>(demoPatients);
    }
    
    public static List<Appointment> getDemoAppointments() {
        return new ArrayList<>(demoAppointments);
    }
    
    public static Optional<Doctor> findDoctorById(String id) {
        return demoDoctors.stream()
            .filter(d -> d.getDoctorId().equals(id))
            .findFirst();
    }
    
    public static Optional<Patient> findPatientById(String id) {
        return demoPatients.stream()
            .filter(p -> p.getPatientId().equals(id))
            .findFirst();
    }
    
    public static Optional<Appointment> findAppointmentById(String id) {
        return demoAppointments.stream()
            .filter(a -> a.getAppointmentId().equals(id))
            .findFirst();
    }
    
    public static List<Doctor> searchDoctorsByName(String name) {
        return demoDoctors.stream()
            .filter(d -> d.getFirstName().toLowerCase().contains(name.toLowerCase()) ||
                        d.getLastName().toLowerCase().contains(name.toLowerCase()))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    public static List<Doctor> searchDoctorsBySpecialization(String specialization) {
        return demoDoctors.stream()
            .filter(d -> d.getSpecialization().toLowerCase().contains(specialization.toLowerCase()))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    public static void addDemoDoctor(Doctor doctor) {
        demoDoctors.add(doctor);
    }
    
    public static void addDemoPatient(Patient patient) {
        demoPatients.add(patient);
    }
    
    public static void addDemoAppointment(Appointment appointment) {
        demoAppointments.add(appointment);
    }
    
    public static void removeDemoDoctor(String doctorId) {
        demoDoctors.removeIf(d -> d.getDoctorId().equals(doctorId));
    }
    
    public static void removeDemoPatient(String patientId) {
        demoPatients.removeIf(p -> p.getPatientId().equals(patientId));
    }
    
    public static void removeDemoAppointment(String appointmentId) {
        demoAppointments.removeIf(a -> a.getAppointmentId().equals(appointmentId));
    }
}
