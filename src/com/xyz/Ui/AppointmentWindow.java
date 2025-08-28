package com.xyz.Ui;

import com.mongodb.client.MongoDatabase;
import com.xyz.Data.AppointmentRepository;
import com.xyz.Data.PatientRepository;
import com.xyz.Data.DoctorRepository;
import com.xyz.Service.AppointmentService;
import com.xyz.Service.DoctorService;
import com.xyz.Service.NotificationService;
import com.xyz.model.Appointment;
import com.xyz.Utility.MongoDBConnection;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import com.xyz.Utility.TimeSlotValidator;


public class AppointmentWindow extends JFrame {
    private final Menu mainWindow;

    private final MongoDatabase database;

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    private final DoctorService doctorService;
    private final NotificationService notificationService;
    private final AppointmentService appointmentService;

    public AppointmentWindow(Menu mainWindow) {
        this.mainWindow = mainWindow;
        setTitle("Appointment Management");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Use shared MongoDB connection
        database = MongoDBConnection.getInstance().getDatabase();

        // Initialize repositories
        appointmentRepository = new AppointmentRepository(database);
        patientRepository = new PatientRepository(database);
        doctorRepository = new DoctorRepository(database);

        // Initialize services
        doctorService = new DoctorService(doctorRepository);
        notificationService = new NotificationService();

        // Initialize appointment service with dependencies
        appointmentService = new AppointmentService(
                appointmentRepository,
                patientRepository,
                doctorRepository,
                doctorService,
                notificationService
        );

        setupUI();
    }

    private void setupUI() {
        JPanel panel = new JPanel(new GridLayout(10, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnBook = new JButton("Book New Appointment");
        JButton btnViewAll = new JButton("View All Appointments");
        JButton btnCancel = new JButton("Cancel Appointment");
        JButton btnReschedule = new JButton("Reschedule Appointment");
        JButton btnMarkCompleted = new JButton("Mark Appointment as Completed");
        JButton btnMarkNoShow = new JButton("Mark Appointment as No Show");
        JButton btnSearchByPatient = new JButton("Search by Patient ID");
        JButton btnSearchByDoctor = new JButton("Search by Doctor ID");
        JButton btnSearchByDate = new JButton("Search by Date");
        JButton btnBack = new JButton("Back to Main Menu");

        panel.add(btnBook);
        panel.add(btnViewAll);
        panel.add(btnCancel);
        panel.add(btnReschedule);
        panel.add(btnMarkCompleted);
        panel.add(btnMarkNoShow);
        panel.add(btnSearchByPatient);
        panel.add(btnSearchByDoctor);
        panel.add(btnSearchByDate);
        panel.add(btnBack);

        add(panel);

        btnBook.addActionListener(this::bookAppointment);
        btnViewAll.addActionListener(this::viewAllAppointments);
        btnCancel.addActionListener(this::cancelAppointment);
        btnReschedule.addActionListener(this::rescheduleAppointment);
        btnMarkCompleted.addActionListener(this::markAppointmentCompleted);
        btnMarkNoShow.addActionListener(this::markAppointmentNoShow);
        btnSearchByPatient.addActionListener(this::searchByPatient);
        btnSearchByDoctor.addActionListener(this::searchByDoctor);
        btnSearchByDate.addActionListener(this::searchByDate);
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent e) {
                dispose();
                mainWindow.returnToMain();
            }
        });
    }

    // Safely choose a time using the optional TimeSlotInputDialog when available,
    // otherwise fall back to a simple input dialog to avoid runtime class loading errors
    private String chooseTime(String title) {
        try {
            Class<?> dlgClass = Class.forName("com.xyz.Ui.TimeSlotInputDialog");
            java.lang.reflect.Method method = dlgClass.getMethod("showTimeSlotDialog", javax.swing.JFrame.class, String.class);
            Object result = method.invoke(null, this, title);
            return (String) result;
        } catch (Throwable t) {
            return JOptionPane.showInputDialog(this,
                "Enter time (24h e.g., 09:30 or 12h e.g., 9:30 AM):",
                title,
                JOptionPane.QUESTION_MESSAGE);
        }
    }

    private void cancelAppointment(ActionEvent e) {
        try {
            String appointmentId = JOptionPane.showInputDialog(this, "Enter Appointment ID to cancel:");
            if (appointmentId == null || appointmentId.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Appointment ID is required");
                return;
            }

            Appointment cancelledAppointment = appointmentService.cancelAppointment(appointmentId.trim());
            JOptionPane.showMessageDialog(this, 
                "Appointment cancelled successfully!\n\n" +
                "Appointment ID: " + cancelledAppointment.getAppointmentId() + "\n" +
                "Status: " + cancelledAppointment.getStatus().getDisplayName(),
                "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error cancelling appointment: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void rescheduleAppointment(ActionEvent e) {
        try {
            String appointmentId = JOptionPane.showInputDialog(this, "Enter Appointment ID to reschedule:");
            if (appointmentId == null || appointmentId.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Appointment ID is required");
                return;
            }

            String newDate = JOptionPane.showInputDialog(this, "Enter new date (YYYY-MM-DD):");
            // Use the new time slot dialog if available, otherwise fallback
            String newTime = chooseTime("Select New Appointment Time");
            
            if (newTime == null) {
                return; // User cancelled
            }

            if (newDate == null || newTime == null || newDate.trim().isEmpty() || newTime.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "New date and time are required");
                return;
            }

            // Validate time format
            if (!TimeSlotValidator.isValidTimeFormat(newTime.trim())) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid time format!\n\n" +
                    "Please use one of these formats:\n" +
                    "• 24-hour: 09:30, 14:00, 23:45\n" +
                    "• 12-hour: 9:30 AM, 2:30 PM, 11:00 AM\n\n" +
                    "Business Hours: " + TimeSlotValidator.getBusinessHours(),
                    "Invalid Time Format", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if time is within business hours
            if (!TimeSlotValidator.isWithinBusinessHours(newTime.trim())) {
                JOptionPane.showMessageDialog(this, 
                    "Time is outside business hours!\n\n" +
                    "Business Hours: " + TimeSlotValidator.getBusinessHours() + "\n" +
                    "Please select a time within business hours.",
                    "Outside Business Hours", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Convert to 24-hour format for consistency
            String time24Hour = TimeSlotValidator.isValid24HourFormat(newTime.trim()) ? 
                newTime.trim() : TimeSlotValidator.convert12To24Hour(newTime.trim());

            java.time.LocalDateTime newDateTime = java.time.LocalDateTime.parse(newDate + "T" + time24Hour);
            String newTimeSlot = time24Hour;

            Appointment rescheduledAppointment = appointmentService.rescheduleAppointment(
                appointmentId.trim(), newDateTime, newTimeSlot);

            JOptionPane.showMessageDialog(this, 
                "Appointment rescheduled successfully!\n\n" +
                "Appointment ID: " + rescheduledAppointment.getAppointmentId() + "\n" +
                "New Date/Time: " + rescheduledAppointment.getAppointmentDateTime() + "\n" +
                "Status: " + rescheduledAppointment.getStatus().getDisplayName(),
                "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date/time format.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error rescheduling appointment: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void markAppointmentCompleted(ActionEvent e) {
        try {
            String appointmentId = JOptionPane.showInputDialog(this, "Enter Appointment ID to mark as completed:");
            if (appointmentId == null || appointmentId.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Appointment ID is required");
                return;
            }

            String notes = JOptionPane.showInputDialog(this, "Enter completion notes (optional):");

            Appointment completedAppointment = appointmentService.markAppointmentCompleted(
                appointmentId.trim(), notes);

            JOptionPane.showMessageDialog(this, 
                "Appointment marked as completed!\n\n" +
                "Appointment ID: " + completedAppointment.getAppointmentId() + "\n" +
                "Status: " + completedAppointment.getStatus().getDisplayName() + "\n" +
                "Notes: " + completedAppointment.getNotes(),
                "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error marking appointment as completed: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void markAppointmentNoShow(ActionEvent e) {
        try {
            String appointmentId = JOptionPane.showInputDialog(this, "Enter Appointment ID to mark as no-show:");
            if (appointmentId == null || appointmentId.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Appointment ID is required");
                return;
            }

            Appointment noShowAppointment = appointmentService.markAppointmentNoShow(appointmentId.trim());

            JOptionPane.showMessageDialog(this, 
                "Appointment marked as no-show!\n\n" +
                "Appointment ID: " + noShowAppointment.getAppointmentId() + "\n" +
                "Status: " + noShowAppointment.getStatus().getDisplayName(),
                "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error marking appointment as no-show: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void searchByPatient(ActionEvent e) {
        try {
            String patientId = JOptionPane.showInputDialog(this, "Enter Patient ID:");
            if (patientId == null || patientId.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Patient ID is required");
                return;
            }

            List<Appointment> appointments = appointmentService.getAppointmentsByPatient(patientId.trim());

            if (appointments.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No appointments found for patient ID: " + patientId);
                return;
            }

            showAppointmentTable(appointments, "Appointments for Patient ID: " + patientId);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error searching appointments by patient: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void searchByDoctor(ActionEvent e) {
        try {
            String doctorId = JOptionPane.showInputDialog(this, "Enter Doctor ID:");
            if (doctorId == null || doctorId.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Doctor ID is required");
                return;
            }

            List<Appointment> appointments = appointmentService.getAppointmentsByDoctor(doctorId.trim());

            if (appointments.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No appointments found for doctor ID: " + doctorId);
                return;
            }

            showAppointmentTable(appointments, "Appointments for Doctor ID: " + doctorId);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error searching appointments by doctor: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void bookAppointment(ActionEvent e) {
        try {
            String patientId = JOptionPane.showInputDialog(this, "Enter Patient ID:");
            if (patientId == null || patientId.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Patient ID is required");
                return;
            }

            String doctorId = JOptionPane.showInputDialog(this, "Enter Doctor ID:");
            if (doctorId == null || doctorId.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Doctor ID is required");
                return;
            }

            String date = JOptionPane.showInputDialog(this, "Enter Appointment Date (YYYY-MM-DD):");
            // Use the new time slot dialog if available, otherwise fallback
            String time = chooseTime("Select Appointment Time");
            
            if (time == null) {
                return; // User cancelled
            }

            if (date == null || time == null || date.trim().isEmpty() || time.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Date and time are required");
                return;
            }

            // Validate time format
            if (!TimeSlotValidator.isValidTimeFormat(time.trim())) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid time format!\n\n" +
                    "Please use one of these formats:\n" +
                    "• 24-hour: 09:30, 14:00, 23:45\n" +
                    "• 12-hour: 9:30 AM, 2:30 PM, 11:00 AM\n\n" +
                    "Business Hours: " + TimeSlotValidator.getBusinessHours(),
                    "Invalid Time Format", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if time is within business hours
            if (!TimeSlotValidator.isWithinBusinessHours(time.trim())) {
                JOptionPane.showMessageDialog(this, 
                    "Time is outside business hours!\n\n" +
                    "Business Hours: " + TimeSlotValidator.getBusinessHours() + "\n" +
                    "Please select a time within business hours.",
                    "Outside Business Hours", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Convert to 24-hour format for consistency
            String time24Hour = TimeSlotValidator.isValid24HourFormat(time.trim()) ? 
                time.trim() : TimeSlotValidator.convert12To24Hour(time.trim());

            LocalDateTime appointmentDateTime = LocalDateTime.parse(date + "T" + time24Hour);

            // Use the validated time slot
            String timeSlot = time24Hour;

            Appointment appt = appointmentService.bookAppointment(patientId, doctorId, appointmentDateTime, timeSlot);

            JOptionPane.showMessageDialog(this, "Appointment booked! ID: " + appt.getAppointmentId());

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date/time format.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error booking appointment: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void viewAllAppointments(ActionEvent e) {
        try {
            List<Appointment> list = appointmentService.getAllAppointments();

            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No appointments found.", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            showAppointmentTable(list, "All Appointments");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading appointments: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void searchByDate(ActionEvent e) {
        try {
            String dateStr = JOptionPane.showInputDialog(this, "Enter date (YYYY-MM-DD):");
            if (dateStr == null || dateStr.trim().isEmpty()) return;

            LocalDate date = LocalDate.parse(dateStr.trim());
            List<Appointment> list = appointmentService.getAppointmentsByDate(date);

            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No appointments on " + dateStr);
                return;
            }

            showAppointmentTable(list, "Appointments on " + dateStr);

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error searching appointments: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void showAppointmentTable(List<Appointment> list, String title) {
        String[] cols = {"ID", "Patient", "Doctor", "DateTime", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        for (Appointment a : list) {
            model.addRow(new Object[]{
                    a.getAppointmentId(),
                    a.getPatientId(),
                    a.getDoctorId(),
                    a.getAppointmentDateTime(),
                    a.getStatus().getDisplayName()
            });
        }

        JTable table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(600, 300));

        JFrame f = new JFrame(title);
        f.add(sp);
        f.pack();
        f.setLocationRelativeTo(this);
        f.setVisible(true);
    }

    // Optional: close mongoClient when window closes
    @Override
    public void dispose() {
        super.dispose();
    }
}
