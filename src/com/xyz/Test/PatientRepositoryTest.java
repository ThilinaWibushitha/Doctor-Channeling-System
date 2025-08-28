// Test Case 1: Patient Repository - Insert and Retrieve Patient
package com.xyz.Test;

import com.xyz.Data.PatientRepository;
import com.xyz.model.Patient;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PatientRepositoryTest {

    private static PatientRepository patientRepository;
    private static String testPatientId;

    @BeforeAll
    public static void setUp() {
        patientRepository = new PatientRepository();
    }

    @Test
    @Order(1)
    @DisplayName("Test Case 1: Insert New Patient")
    public void testInsertPatient() {
        // Arrange
        Patient patient = new Patient("John", "Doe", "john.doe@email.com", 
                                    "0771234567", LocalDate.of(1990, 5, 15), 
                                    "123 Main Street, Colombo");

        // Act
        Patient insertedPatient = patientRepository.insertPatient(patient);

        // Assert
        assertNotNull(insertedPatient.getPatientId());
        assertTrue(insertedPatient.getPatientId().matches("P\\d{4}"));
        assertEquals("John", insertedPatient.getFirstName());
        assertEquals("Doe", insertedPatient.getLastName());
        testPatientId = insertedPatient.getPatientId();

        System.out.println("Test Case 1 Passed: Patient inserted with ID " + testPatientId);
    }

    @Test
    @Order(2)
    @DisplayName("Test Case 2: Retrieve Patient by ID")
    public void testGetPatientById() {
        // Act
        Optional<Patient> retrievedPatient = patientRepository.findById(testPatientId);

        // Assert
        assertTrue(retrievedPatient.isPresent());
        assertEquals("John", retrievedPatient.get().getFirstName());
        assertEquals("john.doe@email.com", retrievedPatient.get().getEmail());

        System.out.println("Test Case 2 Passed: Patient retrieved successfully");
    }

    @Test
    @Order(3)
    @DisplayName("Test Case 3: Search Patients by Name")
    public void testSearchPatientsByName() {
        // Act
        List<Patient> results = patientRepository.searchPatientsByName("John");

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(p -> p.getFirstName().contains("John")));

        System.out.println("Test Case 3 Passed: Found " + results.size() + " patients matching 'John'");
    }
}
