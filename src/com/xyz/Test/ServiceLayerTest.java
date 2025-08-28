// Test Case 10-12: Service Layer Tests
package com.xyz.Test;

import com.xyz.Service.DoctorService;
import com.xyz.Service.PatientService;
import com.xyz.Data.DoctorRepository;
import com.xyz.Data.PatientRepository;
import com.xyz.model.Doctor;
import com.xyz.model.Patient;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.List;

public class ServiceLayerTest {

    private static DoctorService doctorService;
    private static PatientService patientService;

    @BeforeAll
    public static void setUp() {
        doctorService = new DoctorService(new DoctorRepository());
        patientService = new PatientService(new PatientRepository());
    }

    @Test
    @DisplayName("Test Case 10: Register Doctor with Validation")
    public void testRegisterDoctorValidation() {
        // Test valid registration
        assertDoesNotThrow(() -> {
            Doctor doctor = doctorService.registerDoctor(
                "Michael", "Smith", "Neurology", 
                "michael.smith@test.com", "0771234567", 
                "LIC789012", 6000.0, "MBBS, MS", 8
            );
            assertNotNull(doctor.getDoctorId());
        });

        // Test duplicate email validation
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            doctorService.registerDoctor(
                "John", "Test", "General", 
                "michael.smith@test.com", "0771234568", 
                "LIC789013", 4000.0, "MBBS", 5
            );
        });
        assertTrue(exception.getMessage().contains("email already exists"));

        System.out.println("Test Case 10 Passed: Doctor registration validation works");
    }

    @Test
    @DisplayName("Test Case 11: Patient Registration Validation")
    public void testPatientRegistrationValidation() {
        // Test valid registration
        assertDoesNotThrow(() -> {
            Patient patient = patientService.registerPatient(
                "Alice", "Brown", "alice.brown@test.com", 
                "0771234569", LocalDate.of(1985, 8, 20), 
                "456 Oak Street"
            );
            assertNotNull(patient.getPatientId());
        });

        // Test invalid email format
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            patientService.registerPatient(
                "Bob", "Invalid", "invalid-email", 
                "0771234570", LocalDate.of(1990, 1, 1), 
                "789 Pine Street"
            );
        });
        assertTrue(exception.getMessage().contains("email") || 
                  exception.getMessage().contains("Invalid"));

        System.out.println("Test Case 11 Passed: Patient registration validation works");
    }

    @Test
    @DisplayName("Test Case 12: Doctor Time Slot Management")
    public void testDoctorTimeSlotManagement() {
        // First, get a doctor
        List<Doctor> doctors = doctorService.getAllDoctors();
        assertFalse(doctors.isEmpty());
        
        String doctorId = doctors.get(0).getDoctorId();
        String testSlot = "18:00-19:00";

        // Add time slot
        assertDoesNotThrow(() -> {
            doctorService.addTimeSlot(doctorId, testSlot);
        });

        // Check if slot is available
        assertTrue(doctorService.isSlotAvailable(doctorId, testSlot));

        // Remove time slot
        assertDoesNotThrow(() -> {
            doctorService.removeTimeSlot(doctorId, testSlot);
        });

        System.out.println("Test Case 12 Passed: Time slot management works");
    }
}