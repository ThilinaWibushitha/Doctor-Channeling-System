// Test Case 4-6: Doctor Repository Tests
package com.xyz.Test;

import com.xyz.Data.DoctorRepository;
import com.xyz.model.Doctor;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DoctorRepositoryTest {

    private static DoctorRepository doctorRepository;
    private static String testDoctorId;

    @BeforeAll
    public static void setUp() {
        doctorRepository = new DoctorRepository();
    }

    @Test
    @Order(4)
    @DisplayName("Test Case 4: Save New Doctor")
    public void testSaveDoctor() {
        // Arrange
        Doctor doctor = new Doctor();
        doctor.setFirstName("Sarah");
        doctor.setLastName("Johnson");
        doctor.setSpecialization("Cardiology");
        doctor.setEmail("sarah.johnson@hospital.com");
        doctor.setPhoneNumber("0712345678");
        doctor.setLicenseNumber("LIC123456");
        doctor.setConsultationFee(5000.0);
        doctor.setQualification("MBBS, MD");
        doctor.setExperienceYears(10);

        // Act
        Doctor savedDoctor = doctorRepository.save(doctor);

        // Assert
        assertNotNull(savedDoctor.getDoctorId());
        assertTrue(savedDoctor.getDoctorId().matches("D\\d{5}"));
        assertEquals("Cardiology", savedDoctor.getSpecialization());
        testDoctorId = savedDoctor.getDoctorId();

        System.out.println("Test Case 4 Passed: Doctor saved with ID " + testDoctorId);
    }

    @Test
    @Order(5)
    @DisplayName("Test Case 5: Find Doctor by Email")
    public void testFindDoctorByEmail() {
        // Act
        Optional<Doctor> doctor = doctorRepository.findByEmail("sarah.johnson@hospital.com");

        // Assert
        assertTrue(doctor.isPresent());
        assertEquals("Sarah", doctor.get().getFirstName());
        assertEquals("Cardiology", doctor.get().getSpecialization());

        System.out.println("Test Case 5 Passed: Doctor found by email");
    }

    @Test
    @Order(6)
    @DisplayName("Test Case 6: Search Doctors by Specialization")
    public void testFindDoctorsBySpecialization() {
        // Act
        List<Doctor> doctors = doctorRepository.findBySpecialization("Cardiology");

        // Assert
        assertFalse(doctors.isEmpty());
        assertTrue(doctors.stream().anyMatch(d -> d.getSpecialization().equals("Cardiology")));

        System.out.println("Test Case 6 Passed: Found " + doctors.size() + " cardiologists");
    }
}
