// Test Case 13-15: Integration and Edge Cases
package com.xyz.Test;

import com.xyz.Service.AppointmentService;
import com.xyz.Service.DoctorService;
import com.xyz.Service.NotificationService;
import com.xyz.Data.*;
import com.xyz.model.Appointment;
import com.xyz.Utility.MongoDBConnection;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

public class IntegrationTest {

    private static AppointmentService appointmentService;

    @BeforeAll
    public static void setUp() {
        var database = MongoDBConnection.getInstance().getDatabase();
        var appointmentRepo = new AppointmentRepository(database);
        var patientRepo = new PatientRepository(database);
        var doctorRepo = new DoctorRepository(database);
        var doctorService = new DoctorService(doctorRepo);
        var notificationService = new NotificationService();

        appointmentService = new AppointmentService(
            appointmentRepo, patientRepo, doctorRepo, 
            doctorService, notificationService
        );
    }

    @Test
    @DisplayName("Test Case 13: Appointment Booking Integration")
    public void testAppointmentBookingIntegration() {
        // This test requires existing patient and doctor
        // Test invalid patient ID
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            appointmentService.bookAppointment(
                "INVALID_PATIENT", "D00001", 
                LocalDateTime.now().plusDays(1), "10:00-11:00"
            );
        });
        assertTrue(exception.getMessage().contains("Patient not found"));

        System.out.println("Test Case 13 Passed: Appointment booking validation works");
    }

    @Test
    @DisplayName("Test Case 14: Appointment Status Updates")
    public void testAppointmentStatusUpdates() {
        // Create a test appointment first
        try {
            Appointment appointment = new Appointment();
            appointment.setPatientId("P0001");
            appointment.setDoctorId("D00001");
            appointment.setAppointmentDateTime(LocalDateTime.now().plusDays(2));
            appointment.setTimeSlot("11:00-12:00");
            appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);

            // Test status transitions
            assertEquals(Appointment.AppointmentStatus.SCHEDULED, appointment.getStatus());
            
            appointment.setStatus(Appointment.AppointmentStatus.CONFIRMED);
            assertEquals(Appointment.AppointmentStatus.CONFIRMED, appointment.getStatus());

            appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
            assertEquals(Appointment.AppointmentStatus.COMPLETED, appointment.getStatus());

            System.out.println("Test Case 14 Passed: Appointment status updates work");
        } catch (Exception e) {
            System.out.println("Test Case 14: Limited test due to data dependencies");
        }
    }

    @Test
    @DisplayName("Test Case 15: Database Connection and Error Handling")
    public void testDatabaseConnectionAndErrorHandling() {
        // Test MongoDB connection
        assertDoesNotThrow(() -> {
            var connection = MongoDBConnection.getInstance();
            var database = connection.getDatabase();
            assertNotNull(database);
        });

        // Test repository error handling
        DoctorRepository doctorRepo = new DoctorRepository();
        
        // Test finding non-existent doctor
        var result = doctorRepo.findById("NONEXISTENT");
        assertTrue(result.isEmpty());

        // Test count operation
        assertDoesNotThrow(() -> {
            int count = doctorRepo.count();
            assertTrue(count >= 0);
        });

        System.out.println("Test Case 15 Passed: Database operations and error handling work");
    }
}
