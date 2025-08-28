package com.xyz.Ui;

import com.xyz.Data.AppointmentRepository;
import com.xyz.Data.DoctorRepository;
import com.xyz.Data.PatientRepository;
import com.xyz.Service.AppointmentService;
import com.xyz.Service.DoctorService;
import com.xyz.Service.PatientService;
import com.xyz.model.Appointment;
import com.xyz.model.Doctor;
import com.xyz.model.Patient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class SystemReportsWindow extends JFrame {
    private final Menu mainWindow;

    // UI Components
    private JTabbedPane tabbedPane;
    private JLabel totalPatientsLabel;
    private JLabel totalDoctorsLabel;
    private JLabel totalAppointmentsLabel;
    private JLabel todayAppointmentsLabel;
    private JLabel revenueLabel;
    private JTable appointmentTable;
    private JTable doctorTable;
    private JTable patientTable;
    
    // Services (not final to allow fallback to demo mode)
    private AppointmentService appointmentService;
    private DoctorService doctorService;
    private PatientService patientService;

    public SystemReportsWindow(Menu mainWindow) {
        this.mainWindow = mainWindow;
        
        // Initialize services with MongoDB connection
        try {
            com.xyz.Utility.MongoDBConnection mongoConnection = com.xyz.Utility.MongoDBConnection.getInstance();
            com.mongodb.client.MongoDatabase database = mongoConnection.getDatabase();
            
            this.appointmentService = new AppointmentService(
                new AppointmentRepository(database), 
                new PatientRepository(database), 
                new DoctorRepository(database),
                new DoctorService(new DoctorRepository(database)),
                null // NotificationService not needed for reports
            );
            this.doctorService = new DoctorService(new DoctorRepository(database));
            this.patientService = new PatientService(new PatientRepository(database));
        } catch (Exception e) {
            // Fallback to demo mode if MongoDB connection fails
            System.out.println("⚠️ Using demo mode for reports due to database connection issue: " + e.getMessage());
            this.appointmentService = null;
            this.doctorService = new DoctorService(new DoctorRepository());
            this.patientService = new PatientService(new PatientRepository());
        }

        setTitle("System Reports & Analytics");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);

        initializeUI();
        loadReports();
    }

    private void initializeUI() {
        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(248, 250, 252),
                    0, getHeight(), new Color(241, 245, 249)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Tabbed content
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("📊 Dashboard", createDashboardPanel());
        tabbedPane.addTab("📅 Appointments", createAppointmentsPanel());
        tabbedPane.addTab("🏥 Doctors", createDoctorsPanel());
        tabbedPane.addTab("👥 Patients", createPatientsPanel());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Footer
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("📊 System Reports & Analytics", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JLabel subtitleLabel = new JLabel("Comprehensive system statistics and performance metrics", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        subtitleLabel.setForeground(new Color(70, 130, 180));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        return headerPanel;
    }

    private JPanel createDashboardPanel() {
        JPanel dashboardPanel = new JPanel(new BorderLayout(15, 15));
        dashboardPanel.setOpaque(false);

        // Key metrics panel
        JPanel metricsPanel = createMetricsPanel();
        dashboardPanel.add(metricsPanel, BorderLayout.NORTH);

        // Charts and graphs panel
        JPanel chartsPanel = createChartsPanel();
        dashboardPanel.add(chartsPanel, BorderLayout.CENTER);

        return dashboardPanel;
    }

    private JPanel createMetricsPanel() {
        JPanel metricsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        metricsPanel.setOpaque(false);

        // Total Patients
        totalPatientsLabel = createMetricCard("👥 Total Patients", "0", new Color(70, 130, 180));
        metricsPanel.add(totalPatientsLabel);

        // Total Doctors
        totalDoctorsLabel = createMetricCard("🏥 Total Doctors", "0", new Color(34, 139, 34));
        metricsPanel.add(totalDoctorsLabel);

        // Total Appointments
        totalAppointmentsLabel = createMetricCard("📅 Total Appointments", "0", new Color(255, 140, 0));
        metricsPanel.add(totalAppointmentsLabel);

        // Today's Appointments
        todayAppointmentsLabel = createMetricCard("📅 Today's Appointments", "0", new Color(138, 43, 226));
        metricsPanel.add(todayAppointmentsLabel);

        // Revenue
        revenueLabel = createMetricCard("💰 Total Revenue", "$0", new Color(220, 20, 60));
        metricsPanel.add(revenueLabel);

        // System Status
        JLabel statusLabel = createMetricCard("🟢 System Status", "Online", new Color(34, 139, 34));
        metricsPanel.add(statusLabel);

        return metricsPanel;
    }

    private JLabel createMetricCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(color);
        card.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 18));
        valueLabel.setForeground(Color.BLACK);
        card.add(valueLabel, BorderLayout.CENTER);

        // Store reference to value label for updating
        if (title.contains("Total Patients")) totalPatientsLabel = valueLabel;
        else if (title.contains("Total Doctors")) totalDoctorsLabel = valueLabel;
        else if (title.contains("Total Appointments")) totalAppointmentsLabel = valueLabel;
        else if (title.contains("Today's Appointments")) todayAppointmentsLabel = valueLabel;
        else if (title.contains("Total Revenue")) revenueLabel = valueLabel;

        return valueLabel;
    }

    private JPanel createChartsPanel() {
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 15, 15));
        chartsPanel.setOpaque(false);

        // Appointment Status Chart
        JPanel statusChartPanel = createChartPanel("Appointment Status Distribution", "Status", "Count");
        chartsPanel.add(statusChartPanel);

        // Monthly Appointments Chart
        JPanel monthlyChartPanel = createChartPanel("Monthly Appointment Trends", "Month", "Appointments");
        chartsPanel.add(monthlyChartPanel);

        return chartsPanel;
    }

    private JPanel createChartPanel(String title, String xAxis, String yAxis) {
        JPanel chartPanel = new JPanel(new BorderLayout(10, 10));
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(25, 25, 112));
        chartPanel.add(titleLabel, BorderLayout.NORTH);

        // Placeholder for chart (in a real application, you'd use JFreeChart or similar)
        JPanel chartPlaceholder = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(240, 240, 240));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.setColor(Color.GRAY);
                g2d.setFont(new Font("Arial", Font.ITALIC, 12));
                String text = "Chart visualization coming soon...";
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = getHeight() / 2;
                g2d.drawString(text, x, y);
                g2d.dispose();
            }
        };
        chartPlaceholder.setPreferredSize(new Dimension(300, 200));
        chartPanel.add(chartPlaceholder, BorderLayout.CENTER);

        return chartPanel;
    }

    private JPanel createAppointmentsPanel() {
        JPanel appointmentsPanel = new JPanel(new BorderLayout(15, 15));
        appointmentsPanel.setOpaque(false);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setOpaque(false);

        JButton todayBtn = createStyledButton("Today", new Color(70, 130, 180), () -> filterAppointmentsByDate(LocalDate.now()));
        JButton weekBtn = createStyledButton("This Week", new Color(34, 139, 34), () -> filterAppointmentsByWeek());
        JButton monthBtn = createStyledButton("This Month", new Color(255, 140, 0), () -> filterAppointmentsByMonth());
        JButton allBtn = createStyledButton("All Time", new Color(138, 43, 226), () -> loadAllAppointments());

        filterPanel.add(todayBtn);
        filterPanel.add(weekBtn);
        filterPanel.add(monthBtn);
        filterPanel.add(allBtn);

        appointmentsPanel.add(filterPanel, BorderLayout.NORTH);

        // Appointments table
        String[] columns = {"ID", "Patient", "Doctor", "Date & Time", "Status", "Fee"};
        appointmentTable = new JTable(new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        appointmentTable.setRowHeight(25);
        appointmentTable.getTableHeader().setBackground(new Color(70, 130, 180));
        appointmentTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        appointmentsPanel.add(scrollPane, BorderLayout.CENTER);

        return appointmentsPanel;
    }

    private JPanel createDoctorsPanel() {
        JPanel doctorsPanel = new JPanel(new BorderLayout(15, 15));
        doctorsPanel.setOpaque(false);

        // Doctor statistics
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        statsPanel.setOpaque(false);

        JLabel activeDoctorsLabel = createStatCard("Active Doctors", "0", new Color(34, 139, 34));
        JLabel avgExperienceLabel = createStatCard("Avg Experience", "0 years", new Color(70, 130, 180));
        JLabel avgFeeLabel = createStatCard("Avg Consultation Fee", "$0", new Color(255, 140, 0));

        statsPanel.add(activeDoctorsLabel);
        statsPanel.add(avgExperienceLabel);
        statsPanel.add(avgFeeLabel);

        doctorsPanel.add(statsPanel, BorderLayout.NORTH);

        // Doctors table
        String[] columns = {"ID", "Name", "Specialization", "Experience", "Fee", "Status"};
        doctorTable = new JTable(new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        doctorTable.setRowHeight(25);
        doctorTable.getTableHeader().setBackground(new Color(34, 139, 34));
        doctorTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(doctorTable);
        doctorsPanel.add(scrollPane, BorderLayout.CENTER);

        return doctorsPanel;
    }

    private JLabel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(color);
        card.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        valueLabel.setForeground(Color.BLACK);
        card.add(valueLabel, BorderLayout.CENTER);

        return valueLabel;
    }

    private JPanel createPatientsPanel() {
        JPanel patientsPanel = new JPanel(new BorderLayout(15, 15));
        patientsPanel.setOpaque(false);

        // Patient statistics
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        statsPanel.setOpaque(false);

        JLabel activePatientsLabel = createStatCard("Active Patients", "0", new Color(70, 130, 180));
        JLabel avgAgeLabel = createStatCard("Average Age", "0 years", new Color(138, 43, 226));
        JLabel newPatientsLabel = createStatCard("New This Month", "0", new Color(255, 140, 0));

        statsPanel.add(activePatientsLabel);
        statsPanel.add(avgAgeLabel);
        statsPanel.add(newPatientsLabel);

        patientsPanel.add(statsPanel, BorderLayout.NORTH);

        // Patients table
        String[] columns = {"ID", "Name", "Age", "Email", "Phone", "Status"};
        patientTable = new JTable(new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        patientTable.setRowHeight(25);
        patientTable.getTableHeader().setBackground(new Color(70, 130, 180));
        patientTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(patientTable);
        patientsPanel.add(scrollPane, BorderLayout.CENTER);

        return patientsPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout(10, 10));
        footerPanel.setOpaque(false);

        JLabel timestampLabel = new JLabel("Last updated: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), SwingConstants.LEFT);
        timestampLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        timestampLabel.setForeground(Color.GRAY);
        footerPanel.add(timestampLabel, BorderLayout.WEST);

        JButton refreshButton = createStyledButton("🔄 Refresh Reports", new Color(70, 130, 180), () -> loadReports());
        JButton backButton = createStyledButton("⬅️ Back to Menu", Color.GRAY, () -> {
            dispose();
            mainWindow.returnToMain();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        footerPanel.add(buttonPanel, BorderLayout.EAST);

        return footerPanel;
    }

    private JButton createStyledButton(String text, Color backgroundColor, Runnable onClick) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        button.addActionListener(e -> onClick.run());
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(@SuppressWarnings("unused") java.awt.event.MouseEvent e) {
                button.setBackground(backgroundColor.brighter());
            }

            @Override
            public void mouseExited(@SuppressWarnings("unused") java.awt.event.MouseEvent e) {
                button.setBackground(backgroundColor);
            }
        });

        return button;
    }

    private void loadReports() {
        try {
            // Load dashboard metrics
            loadDashboardMetrics();
            
            // Load appointment data
            loadAllAppointments();
            
            // Load doctor data
            loadDoctorData();
            
            // Load patient data
            loadPatientData();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading reports: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadDashboardMetrics() {
        try {
            List<Patient> patients = patientService.getAllPatients();
            List<Doctor> doctors = doctorService.getAllDoctors();
            List<Appointment> appointments = appointmentService.getAllAppointments();
            
            // Calculate metrics
            int totalPatients = patients.size();
            int totalDoctors = doctors.size();
            int totalAppointments = appointments.size();
            
            // Today's appointments
            LocalDate today = LocalDate.now();
            long todayAppointments = appointments.stream()
                .filter(a -> a.getAppointmentDateTime().toLocalDate().equals(today))
                .count();
            
            // Calculate revenue
            double totalRevenue = appointments.stream()
                .filter(a -> a.getStatus() == Appointment.AppointmentStatus.COMPLETED)
                .mapToDouble(a -> {
                    Doctor doctor = doctors.stream()
                        .filter(d -> d.getDoctorId().equals(a.getDoctorId()))
                        .findFirst()
                        .orElse(null);
                    return doctor != null ? doctor.getConsultationFee() : 0.0;
                })
                .sum();
            
            // Update labels
            totalPatientsLabel.setText(String.valueOf(totalPatients));
            totalDoctorsLabel.setText(String.valueOf(totalDoctors));
            totalAppointmentsLabel.setText(String.valueOf(totalAppointments));
            todayAppointmentsLabel.setText(String.valueOf(todayAppointments));
            revenueLabel.setText("$" + String.format("%.2f", totalRevenue));
            
        } catch (Exception e) {
            System.err.println("Error loading dashboard metrics: " + e.getMessage());
        }
    }

    private void loadAllAppointments() {
        try {
            List<Appointment> appointments = appointmentService.getAllAppointments();
            updateAppointmentTable(appointments);
        } catch (Exception e) {
            System.err.println("Error loading appointments: " + e.getMessage());
        }
    }

    private void filterAppointmentsByDate(LocalDate date) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByDate(date);
            updateAppointmentTable(appointments);
        } catch (Exception e) {
            System.err.println("Error filtering appointments: " + e.getMessage());
        }
    }

    private void filterAppointmentsByWeek() {
        try {
            LocalDate today = LocalDate.now();
            LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
            List<Appointment> appointments = appointmentService.getAllAppointments().stream()
                .filter(a -> {
                    LocalDate apptDate = a.getAppointmentDateTime().toLocalDate();
                    return !apptDate.isBefore(weekStart) && !apptDate.isAfter(today);
                })
                .collect(Collectors.toList());
            updateAppointmentTable(appointments);
        } catch (Exception e) {
            System.err.println("Error filtering appointments by week: " + e.getMessage());
        }
    }

    private void filterAppointmentsByMonth() {
        try {
            LocalDate today = LocalDate.now();
            LocalDate monthStart = today.withDayOfMonth(1);
            List<Appointment> appointments = appointmentService.getAllAppointments().stream()
                .filter(a -> {
                    LocalDate apptDate = a.getAppointmentDateTime().toLocalDate();
                    return !apptDate.isBefore(monthStart) && !apptDate.isAfter(today);
                })
                .collect(Collectors.toList());
            updateAppointmentTable(appointments);
        } catch (Exception e) {
            System.err.println("Error filtering appointments by month: " + e.getMessage());
        }
    }

    private void updateAppointmentTable(List<Appointment> appointments) {
        DefaultTableModel model = (DefaultTableModel) appointmentTable.getModel();
        model.setRowCount(0);
        
        for (Appointment appointment : appointments) {
            // Get patient and doctor names
            String patientName = "Unknown";
            String doctorName = "Unknown";
            double fee = 0.0;
            
            try {
                var patientOpt = patientService.getPatientById(appointment.getPatientId());
                if (patientOpt.isPresent()) {
                    patientName = patientOpt.get().getFullName();
                }
                
                var doctorOpt = doctorService.getDoctorById(appointment.getDoctorId());
                if (doctorOpt.isPresent()) {
                    Doctor doctor = doctorOpt.get();
                    doctorName = doctor.getFullName();
                    fee = doctor.getConsultationFee();
                }
            } catch (Exception e) {
                System.err.println("Error getting patient/doctor details: " + e.getMessage());
            }
            
            Object[] row = {
                appointment.getAppointmentId(),
                patientName,
                doctorName,
                appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                appointment.getStatus().getDisplayName(),
                String.format("$%.2f", fee)
            };
            model.addRow(row);
        }
    }

    private void loadDoctorData() {
        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            DefaultTableModel model = (DefaultTableModel) doctorTable.getModel();
            model.setRowCount(0);
            
            for (Doctor doctor : doctors) {
                Object[] row = {
                    doctor.getDoctorId(),
                    doctor.getFullName(),
                    doctor.getSpecialization(),
                    doctor.getExperienceYears() + " years",
                    String.format("$%.2f", doctor.getConsultationFee()),
                    "Active"
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            System.err.println("Error loading doctor data: " + e.getMessage());
        }
    }

    private void loadPatientData() {
        try {
            List<Patient> patients = patientService.getAllPatients();
            DefaultTableModel model = (DefaultTableModel) patientTable.getModel();
            model.setRowCount(0);
            
            for (Patient patient : patients) {
                int age = 0;
                if (patient.getDateOfBirth() != null) {
                    age = LocalDate.now().getYear() - patient.getDateOfBirth().getYear();
                }
                
                Object[] row = {
                    patient.getPatientId(),
                    patient.getFullName(),
                    age + " years",
                    patient.getEmail(),
                    patient.getPhone(),
                    "Active"
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            System.err.println("Error loading patient data: " + e.getMessage());
        }
    }
}
