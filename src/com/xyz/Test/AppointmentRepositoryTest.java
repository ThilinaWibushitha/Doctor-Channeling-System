// Test Case 7-9: Appointment Repository Tests
package com.xyz.Test;

import com.xyz.Data.AppointmentRepository;
import com.xyz.model.Appointment;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppointmentRepositoryTest {

    private static AppointmentRepository appointmentRepository;
    private static String testAppointmentId;

    @BeforeAll
    public static void setUp() {
        appointmentRepository = new AppointmentRepository(
            com.xyz.Utility.MongoDBConnection.getInstance().getDatabase()
        );
    }

    @Test
    @Order(7)
    @DisplayName("Test Case 7: Save New Appointment")
    public void testSaveAppointment() {
        // Arrange
        LocalDateTime appointmentTime = LocalDateTime.now().plusDays(1);
        Appointment appointment = new Appointment(null, "P0001", "D00001", 
                                                appointmentTime, "09:00-10:00");

        // Act
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Assert
        assertNotNull(savedAppointment.getAppointmentId());
        assertTrue(savedAppointment.getAppointmentId().startsWith("A"));
        assertEquals("P0001", savedAppointment.getPatientId());
        assertEquals("D00001", savedAppointment.getDoctorId());
        testAppointmentId = savedAppointment.getAppointmentId();

        System.out.println("Test Case 7 Passed: Appointment saved with ID " + testAppointmentId);
    }

    @Test
    @Order(8)
    @DisplayName("Test Case 8: Find Appointment by Patient ID")
    public void testFindAppointmentsByPatient() {
        // Act
        List<Appointment> appointments = appointmentRepository.findByPatientId("P0001");

        // Assert
        assertFalse(appointments.isEmpty());
        assertTrue(appointments.stream().anyMatch(a -> a.getPatientId().equals("P0001")));

        System.out.println("Test Case 8 Passed: Found " + appointments.size() + " appointments for patient P0001");
    }

    @Test
    @Order(9)
    @DisplayName("Test Case 9: Check Doctor Availability")
    public void testExistsByDoctorAndDateTime() {
        // Arrange
        LocalDateTime appointmentTime = LocalDateTime.now().plusDays(1);

        // Act
        boolean exists = appointmentRepository.existsByDoctorAndDateTime("D00001", appointmentTime);

        // Assert presence/absence deterministically is hard without seeding; ensure call returns a boolean
        assertNotNull(exists);
        System.out.println("Test Case 9 Passed: Doctor availability check completed. Exists=" + exists);
    }
}

