package com.xyz.Ui;

import com.xyz.Service.DoctorService;
import com.xyz.model.Doctor;
import com.xyz.Utility.Validator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DoctorEditForm extends JFrame {
    private final Doctor doctor;
    private final DoctorService doctorService;
    private final DoctorWindow parentWindow;

    // Form components
    private JTextField idField;
    private JTextField nameField;
    private JTextField specializationField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField licenseField;
    private JTextField feeField;
    private JTextField experienceField;
    private JTextField qualificationField;
    private JTextField hospitalField;
    private JTextArea scheduleArea;
    private JComboBox<String> statusCombo;

    public DoctorEditForm(Doctor doctor, DoctorService doctorService, DoctorWindow parentWindow) {
        this.doctor = doctor;
        this.doctorService = doctorService;
        this.parentWindow = parentWindow;

        setTitle("Edit Doctor - " + doctor.getFullName());
        setSize(550, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        initializeUI();
        populateFields();
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

        // Form panel
        mainPanel.add(createFormPanel(), BorderLayout.CENTER);

        // Button panel
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("✏️ Edit Doctor Information", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(25, 25, 112));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JLabel subtitleLabel = new JLabel("Update doctor details and professional information", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        subtitleLabel.setForeground(new Color(70, 130, 180));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Doctor ID (read-only)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Doctor ID:"), gbc);
        gbc.gridx = 1;
        idField = new JTextField(20);
        idField.setEditable(false);
        idField.setBackground(new Color(240, 240, 240));
        formPanel.add(idField, gbc);

        // Full Name
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Specialization
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Specialization:"), gbc);
        gbc.gridx = 1;
        specializationField = new JTextField(20);
        formPanel.add(specializationField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        formPanel.add(phoneField, gbc);

        // License Number
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("License Number:"), gbc);
        gbc.gridx = 1;
        licenseField = new JTextField(20);
        formPanel.add(licenseField, gbc);

        // Consultation Fee
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Consultation Fee ($):"), gbc);
        gbc.gridx = 1;
        feeField = new JTextField(20);
        formPanel.add(feeField, gbc);

        // Experience Years
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Experience (Years):"), gbc);
        gbc.gridx = 1;
        experienceField = new JTextField(20);
        formPanel.add(experienceField, gbc);

        // Qualification
        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Qualification:"), gbc);
        gbc.gridx = 1;
        qualificationField = new JTextField(20);
        formPanel.add(qualificationField, gbc);

        // Hospital/Clinic
        gbc.gridx = 0; gbc.gridy = 9;
        formPanel.add(new JLabel("Hospital/Clinic:"), gbc);
        gbc.gridx = 1;
        hospitalField = new JTextField(20);
        formPanel.add(hospitalField, gbc);

        // Status
        gbc.gridx = 0; gbc.gridy = 10;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(new String[]{"Active", "Inactive", "On Leave", "Suspended"});
        formPanel.add(statusCombo, gbc);

        // Schedule
        gbc.gridx = 0; gbc.gridy = 11;
        formPanel.add(new JLabel("Schedule:"), gbc);
        gbc.gridx = 1;
        scheduleArea = new JTextArea(3, 20);
        scheduleArea.setLineWrap(true);
        scheduleArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(scheduleArea);
        scrollPane.setPreferredSize(new Dimension(200, 60));
        formPanel.add(scrollPane, gbc);

        return formPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);

        JButton saveButton = createStyledButton("💾 Save Changes", new Color(34, 139, 34), this::saveChanges);
        JButton cancelButton = createStyledButton("❌ Cancel", new Color(220, 20, 60), new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent e) {
                dispose();
            }
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    private JButton createStyledButton(String text, Color backgroundColor, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        button.addActionListener(action);
        
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

    private void populateFields() {
        idField.setText(doctor.getDoctorId());
        nameField.setText(doctor.getFullName());
        specializationField.setText(doctor.getSpecialization());
        emailField.setText(doctor.getEmail());
        phoneField.setText(doctor.getPhoneNumber());
        licenseField.setText(doctor.getLicenseNumber());
        feeField.setText(String.format("%.2f", doctor.getConsultationFee()));
        experienceField.setText(String.valueOf(doctor.getExperienceYears()));
        
        // Set default values for new fields
        qualificationField.setText("MBBS, MD");
        hospitalField.setText("General Hospital");
        scheduleArea.setText("Monday-Friday: 9:00 AM - 5:00 PM\nSaturday: 9:00 AM - 1:00 PM\nSunday: Closed");
        statusCombo.setSelectedItem("Active");
    }

    private void saveChanges(ActionEvent e) {
        try {
            // Validate required fields
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Full name is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (specializationField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Specialization is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (emailField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!Validator.isValidEmail(emailField.getText().trim())) {
                JOptionPane.showMessageDialog(this, "Invalid email format", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Parse consultation fee
            double consultationFee;
            try {
                consultationFee = Double.parseDouble(feeField.getText().trim());
                if (consultationFee < 0) {
                    throw new NumberFormatException("Fee cannot be negative");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid consultation fee", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Parse experience years
            int experienceYears;
            try {
                experienceYears = Integer.parseInt(experienceField.getText().trim());
                if (experienceYears < 0) {
                    throw new NumberFormatException("Experience cannot be negative");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid experience years", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Parse first and last name from full name
            String fullName = nameField.getText().trim();
            String[] nameParts = fullName.split("\\s+", 2);
            String firstName = nameParts.length > 0 ? nameParts[0] : "";
            String lastName = nameParts.length > 1 ? nameParts[1] : "";

            // Update doctor object
            doctor.setFirstName(firstName);
            doctor.setLastName(lastName);
            doctor.setSpecialization(specializationField.getText().trim());
            doctor.setEmail(emailField.getText().trim());
            doctor.setPhoneNumber(phoneField.getText().trim());
            doctor.setLicenseNumber(licenseField.getText().trim());
            doctor.setConsultationFee(consultationFee);
            doctor.setExperienceYears(experienceYears);

            // Save to database
            doctorService.updateDoctor(
                doctor.getDoctorId(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getSpecialization(),
                doctor.getEmail(),
                doctor.getPhoneNumber(),
                doctor.getConsultationFee(),
                doctor.getQualification(),
                doctor.getExperienceYears()
            );

            JOptionPane.showMessageDialog(this, 
                "Doctor information updated successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);

            // Refresh parent window and close this form
            if (parentWindow != null) {
                parentWindow.refreshTable();
            }
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error updating doctor: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
