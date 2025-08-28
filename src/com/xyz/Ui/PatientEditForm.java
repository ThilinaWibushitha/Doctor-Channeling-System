package com.xyz.Ui;

import com.xyz.Service.PatientService;
import com.xyz.model.Patient;
import com.xyz.Utility.Validator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PatientEditForm extends JFrame {
    private final Patient patient;
    private final PatientService patientService;
    private final PatientWindow parentWindow;

    // Form components
    private JTextField idField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField dobField;
    private JTextField addressField;
    private JTextField emergencyContactField;
    private JTextField emergencyPhoneField;
    private JTextArea medicalHistoryArea;
    private JComboBox<String> statusCombo;

    public PatientEditForm(Patient patient, PatientService patientService, PatientWindow parentWindow) {
        this.patient = patient;
        this.patientService = patientService;
        this.parentWindow = parentWindow;

        setTitle("Edit Patient - " + patient.getFullName());
        setSize(500, 600);
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

        JLabel titleLabel = new JLabel("✏️ Edit Patient Information", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(25, 25, 112));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JLabel subtitleLabel = new JLabel("Update patient details and medical information", SwingConstants.CENTER);
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

        // Patient ID (read-only)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Patient ID:"), gbc);
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

        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        formPanel.add(phoneField, gbc);

        // Date of Birth
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Date of Birth (DD/MM/YYYY):"), gbc);
        gbc.gridx = 1;
        dobField = new JTextField(20);
        formPanel.add(dobField, gbc);

        // Address
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        addressField = new JTextField(20);
        formPanel.add(addressField, gbc);

        // Emergency Contact
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Emergency Contact:"), gbc);
        gbc.gridx = 1;
        emergencyContactField = new JTextField(20);
        formPanel.add(emergencyContactField, gbc);

        // Emergency Phone
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Emergency Phone:"), gbc);
        gbc.gridx = 1;
        emergencyPhoneField = new JTextField(20);
        formPanel.add(emergencyPhoneField, gbc);

        // Status
        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(new String[]{"Active", "Inactive", "Suspended"});
        formPanel.add(statusCombo, gbc);

        // Medical History
        gbc.gridx = 0; gbc.gridy = 9;
        formPanel.add(new JLabel("Medical History:"), gbc);
        gbc.gridx = 1;
        medicalHistoryArea = new JTextArea(4, 20);
        medicalHistoryArea.setLineWrap(true);
        medicalHistoryArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(medicalHistoryArea);
        scrollPane.setPreferredSize(new Dimension(200, 80));
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
        idField.setText(patient.getPatientId());
        nameField.setText(patient.getFullName());
        emailField.setText(patient.getEmail());
        phoneField.setText(patient.getPhone());
        
        if (patient.getDateOfBirth() != null) {
            dobField.setText(patient.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
        
        addressField.setText(patient.getAddress());
        
        // Set default values for new fields
        emergencyContactField.setText("Not specified");
        emergencyPhoneField.setText("Not specified");
        medicalHistoryArea.setText("No medical history recorded");
        statusCombo.setSelectedItem("Active");
    }

    private void saveChanges(ActionEvent e) {
        try {
            // Validate required fields
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Full name is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
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

            // Parse date of birth
            LocalDate dateOfBirth = null;
            if (!dobField.getText().trim().isEmpty()) {
                try {
                    dateOfBirth = LocalDate.parse(dobField.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid date format. Use DD/MM/YYYY", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

                    // Parse first and last name from full name
        String fullName = nameField.getText().trim();
        String[] nameParts = fullName.split("\\s+", 2);
        String firstName = nameParts.length > 0 ? nameParts[0] : "";
        String lastName = nameParts.length > 1 ? nameParts[1] : "";
        
        // Update patient object
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setEmail(emailField.getText().trim());
        patient.setPhone(phoneField.getText().trim());
        patient.setDateOfBirth(dateOfBirth);
        patient.setAddress(addressField.getText().trim());

            // Save to database
            patientService.updatePatient(patient);

            JOptionPane.showMessageDialog(this, 
                "Patient information updated successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);

            // Refresh parent window and close this form
            if (parentWindow != null) {
                parentWindow.refreshTable();
            }
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error updating patient: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
