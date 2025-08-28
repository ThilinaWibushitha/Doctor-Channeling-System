// NotificationService.java
package com.xyz.Service;

import com.xyz.model.Patient;
import com.xyz.model.Doctor;
import com.xyz.model.Appointment;
import java.time.format.DateTimeFormatter;

public class NotificationService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void sendAppointmentConfirmation(Patient patient, Doctor doctor, Appointment appointment) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📧 APPOINTMENT CONFIRMATION EMAIL");
        System.out.println("=".repeat(60));
        System.out.printf("To: %s (%s)%n", patient.getFullName(), patient.getEmail());
        System.out.printf("Subject: Appointment Confirmation - %s%n", appointment.getAppointmentId());
        System.out.println();
        System.out.printf("Dear %s,%n%n", patient.getFirstName());
        System.out.println("Your appointment has been successfully scheduled:");
        System.out.println();
        System.out.printf("📅 Date & Time: %s%n", appointment.getAppointmentDateTime().format(FORMATTER));
        System.out.printf("👨‍⚕️ Doctor: %s%n", doctor.getFullName());
        System.out.printf("🏥 Specialization: %s%n", doctor.getSpecialization());
        System.out.printf("💰 Consultation Fee: $%.2f%n", doctor.getConsultationFee());
        System.out.printf("📞 Contact: %s%n", doctor.getPhoneNumber());
        System.out.println();
        System.out.println("Please arrive 15 minutes before your scheduled time.");
        System.out.println("Bring a valid ID and any relevant medical records.");
        System.out.println();
        System.out.println("Thank you for choosing our healthcare services!");
        System.out.println("=".repeat(60));
    }

    public void sendCancellationNotification(Patient patient, Doctor doctor, Appointment appointment) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📧 APPOINTMENT CANCELLATION NOTIFICATION");
        System.out.println("=".repeat(60));
        System.out.printf("To: %s (%s)%n", patient.getFullName(), patient.getEmail());
        System.out.printf("Subject: Appointment Cancelled - %s%n", appointment.getAppointmentId());
        System.out.println();
        System.out.printf("Dear %s,%n%n", patient.getFirstName());
        System.out.println("Your appointment has been cancelled:");
        System.out.println();
        System.out.printf("📅 Original Date & Time: %s%n", appointment.getAppointmentDateTime().format(FORMATTER));
        System.out.printf("👨‍⚕️ Doctor: %s%n", doctor.getFullName());
        System.out.println();
        System.out.println("If you need to reschedule, please contact us.");
        System.out.println("=".repeat(60));
    }

    public void sendReschedulingNotification(Patient patient, Doctor doctor, Appointment appointment) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📧 APPOINTMENT RESCHEDULED NOTIFICATION");
        System.out.println("=".repeat(60));
        System.out.printf("To: %s (%s)%n", patient.getFullName(), patient.getEmail());
        System.out.printf("Subject: Appointment Rescheduled - %s%n", appointment.getAppointmentId());
        System.out.println();
        System.out.printf("Dear %s,%n%n", patient.getFirstName());
        System.out.println("Your appointment has been rescheduled:");
        System.out.println();
        System.out.printf("📅 New Date & Time: %s%n", appointment.getAppointmentDateTime().format(FORMATTER));
        System.out.printf("👨‍⚕️ Doctor: %s%n", doctor.getFullName());
        System.out.printf("🏥 Specialization: %s%n", doctor.getSpecialization());
        System.out.printf("💰 Consultation Fee: $%.2f%n", doctor.getConsultationFee());
        System.out.println();
        System.out.println("Please arrive 15 minutes before your new scheduled time.");
        System.out.println("=".repeat(60));
    }

    public void sendReminder(Patient patient, Doctor doctor, Appointment appointment) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📧 APPOINTMENT REMINDER");
        System.out.println("=".repeat(60));
        System.out.printf("To: %s (%s)%n", patient.getFullName(), patient.getEmail());
        System.out.printf("Subject: Appointment Reminder - Tomorrow%n");
        System.out.println();
        System.out.printf("Dear %s,%n%n", patient.getFirstName());
        System.out.println("This is a reminder of your upcoming appointment:");
        System.out.println();
        System.out.printf("📅 Date & Time: %s%n", appointment.getAppointmentDateTime().format(FORMATTER));
        System.out.printf("👨‍⚕️ Doctor: %s%n", doctor.getFullName());
        System.out.printf("🏥 Specialization: %s%n", doctor.getSpecialization());
        System.out.println();
        System.out.println("Please arrive 15 minutes early with valid ID.");
        System.out.println("=".repeat(60));
    }
}