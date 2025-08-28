package com.xyz.Ui;

import com.xyz.Data.DoctorRepository;
import com.xyz.Service.DoctorService;
import com.xyz.model.Doctor;
import com.xyz.Utility.Validator;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class DoctorRegistrationForm extends JFrame {

    private final DoctorService doctorService;

    // Form fields
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JComboBox<String> specializationCombo;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField licenseNumberField;
    private JTextField consultationFeeField;
    private JComboBox<String> qualificationCombo;
    private JSpinner experienceSpinner;
    private JTextArea timeSlotArea;

    // Buttons
    private JButton registerButton;
    private JButton clearButton;
    private JButton cancelButton;

    // Validation labels
    private JLabel firstNameError;
    private JLabel lastNameError;
    private JLabel emailError;
    private JLabel phoneError;
    private JLabel feeError;

    public DoctorRegistrationForm(DoctorRepository doctorRepository) {
        this.doctorService = new DoctorService(doctorRepository);

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupValidation();

        setTitle("Doctor Registration Form");
        setSize(600, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    private void initializeComponents() {
        // Text fields
        firstNameField = createStyledTextField();
        lastNameField = createStyledTextField();
        emailField = createStyledTextField();
        phoneField = createStyledTextField();
        licenseNumberField = createStyledTextField();
        consultationFeeField = createStyledTextField();

        // Combo boxes
        String[] specializations = {
            "Cardiology", "Pediatrics", "Orthopedics", "Neurology", "Dermatology",
            "Oncology", "Psychiatry", "General Medicine", "Surgery", "Emergency Medicine",
            "Radiology", "Pathology", "Anesthesiology", "Gynecology", "Urology"
        };
        specializationCombo = new JComboBox<>(specializations);
        specializationCombo.setFont(new Font("Arial", Font.PLAIN, 12));

        String[] qualifications = {
            "MD", "MBBS", "DO", "PhD", "MBChB", "MBBCh", "BMBS", "MB BChir"
        };
        qualificationCombo = new JComboBox<>(qualifications);
        qualificationCombo.setFont(new Font("Arial", Font.PLAIN, 12));

        // Spinner for experience
        SpinnerNumberModel experienceModel = new SpinnerNumberModel(0, 0, 50, 1);
        experienceSpinner = new JSpinner(experienceModel);
        experienceSpinner.setFont(new Font("Arial", Font.PLAIN, 12));

        // Time slots area
        timeSlotArea = new JTextArea(6, 30);
        timeSlotArea.setText("09:00-10:00\n10:00-11:00\n11:00-12:00\n14:00-15:00\n15:00-16:00\n16:00-17:00");
        timeSlotArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        timeSlotArea.setBackground(new Color(248, 250, 252));
        timeSlotArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Buttons
        registerButton = createStyledButton("✅ Register Doctor", new Color(34, 139, 34));
        clearButton = createStyledButton("🔄 Clear Form", new Color(255, 165, 0));
        cancelButton = createStyledButton("❌ Cancel", new Color(220, 20, 60));

        // Error labels
        firstNameError = createErrorLabel();
        lastNameError = createErrorLabel();
        emailError = createErrorLabel();
        phoneError = createErrorLabel();
        feeError = createErrorLabel();
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 35));
        return button;
    }

    private JLabel createErrorLabel() {
        JLabel label = new JLabel("");
        label.setFont(new Font("Arial", Font.ITALIC, 10));
        label.setForeground(Color.RED);
        return label;
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

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
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("🏥 Doctor Registration", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JLabel subtitleLabel = new JLabel("Enter doctor information to register in the system", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        subtitleLabel.setForeground(Color.GRAY);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Personal Information Section
        JPanel personalPanel = createSectionPanel("Personal Information", new Color(70, 130, 180));
        addFormField(personalPanel, "First Name *:", firstNameField, firstNameError, 0);
        addFormField(personalPanel, "Last Name *:", lastNameField, lastNameError, 1);
        addFormField(personalPanel, "Email *:", emailField, emailError, 2);
        addFormField(personalPanel, "Phone *:", phoneField, phoneError, 3);

        // Professional Information Section
        JPanel professionalPanel = createSectionPanel("Professional Information", new Color(34, 139, 34));
        addFormField(professionalPanel, "Specialization *:", specializationCombo, null, 0);
        addFormField(professionalPanel, "License Number *:", licenseNumberField, null, 1);
        addFormField(professionalPanel, "Qualification *:", qualificationCombo, null, 2);
        addFormField(professionalPanel, "Experience (Years):", experienceSpinner, null, 3);
        addFormField(professionalPanel, "Consultation Fee ($) *:", consultationFeeField, feeError, 4);

        // Schedule Section
        JPanel schedulePanel = createSectionPanel("Available Time Slots", new Color(255, 140, 0));
        JLabel timeSlotLabel = new JLabel("Time Slots (one per line):");
        timeSlotLabel.setFont(new Font("Arial", Font.BOLD, 12));
        schedulePanel.add(timeSlotLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        
        JScrollPane timeSlotScroll = new JScrollPane(timeSlotArea);
        timeSlotScroll.setPreferredSize(new Dimension(400, 120));
        schedulePanel.add(timeSlotScroll, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        // Add sections to form panel
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(personalPanel, gbc);
        
        gbc.gridy = 1;
        formPanel.add(professionalPanel, gbc);
        
        gbc.gridy = 2;
        formPanel.add(schedulePanel, gbc);

        return formPanel;
    }

    private JPanel createSectionPanel(String title, Color color) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(color, 2),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            color
        ));
        panel.setBackground(Color.WHITE);
        return panel;
    }

    private void addFormField(JPanel panel, String labelText, JComponent field, JLabel errorLabel, int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(label, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(field, gbc);

        if (errorLabel != null) {
            gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
            panel.add(errorLabel, gbc);
        }
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);

        buttonPanel.add(registerButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    private void setupEventHandlers() {
        registerButton.addActionListener(this::registerDoctor);
        clearButton.addActionListener(this::clearForm);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent e) {
                dispose();
            }
        });

        // Add real-time validation
        setupRealTimeValidation();
    }

    private void setupRealTimeValidation() {
        firstNameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validateFirstName();
            }
        });

        lastNameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validateLastName();
            }
        });

        emailField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validateEmail();
            }
        });

        phoneField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validatePhone();
            }
        });

        consultationFeeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validateFee();
            }
        });
    }

    private void setupValidation() {
        // Initial validation
        validateFirstName();
        validateLastName();
        validateEmail();
        validatePhone();
        validateFee();
    }

    private void validateFirstName() {
        String firstName = firstNameField.getText().trim();
        if (!Validator.isValidName(firstName)) {
            firstNameError.setText("Invalid first name");
        } else {
            firstNameError.setText("");
        }
    }

    private void validateLastName() {
        String lastName = lastNameField.getText().trim();
        if (!Validator.isValidName(lastName)) {
            lastNameError.setText("Invalid last name");
        } else {
            lastNameError.setText("");
        }
    }

    private void validateEmail() {
        String email = emailField.getText().trim();
        if (!Validator.isValidEmail(email)) {
            emailError.setText("Invalid email format");
        } else {
            emailError.setText("");
        }
    }

    private void validatePhone() {
        String phone = phoneField.getText().trim();
        if (!Validator.isValidPhoneNumber(phone)) {
            phoneError.setText("Invalid phone format");
        } else {
            phoneError.setText("");
        }
    }

    private void validateFee() {
        String feeText = consultationFeeField.getText().trim();
        try {
            if (!feeText.isEmpty()) {
                double fee = Double.parseDouble(feeText);
                if (!Validator.isValidConsultationFee(fee)) {
                    feeError.setText("Fee must be non-negative");
                } else {
                    feeError.setText("");
                }
            } else {
                feeError.setText("");
            }
        } catch (NumberFormatException e) {
            feeError.setText("Invalid number format");
        }
    }

    private boolean isFormValid() {
        validateFirstName();
        validateLastName();
        validateEmail();
        validatePhone();
        validateFee();

        return firstNameError.getText().isEmpty() &&
               lastNameError.getText().isEmpty() &&
               emailError.getText().isEmpty() &&
               phoneError.getText().isEmpty() &&
               feeError.getText().isEmpty() &&
               !firstNameField.getText().trim().isEmpty() &&
               !lastNameField.getText().trim().isEmpty() &&
               !emailField.getText().trim().isEmpty() &&
               !phoneField.getText().trim().isEmpty() &&
               !consultationFeeField.getText().trim().isEmpty();
    }

    private void registerDoctor(ActionEvent e) {
        if (!isFormValid()) {
            JOptionPane.showMessageDialog(this,
                "Please fix all validation errors before registering.",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String specialization = (String) specializationCombo.getSelectedItem();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String licenseNumber = licenseNumberField.getText().trim();
            double consultationFee = Double.parseDouble(consultationFeeField.getText().trim());
            String qualification = (String) qualificationCombo.getSelectedItem();
            int experienceYears = (Integer) experienceSpinner.getValue();

            Doctor doctor = doctorService.registerDoctor(
                firstName, lastName, specialization, email, phone,
                licenseNumber, consultationFee, qualification, experienceYears
            );

            JOptionPane.showMessageDialog(this,
                "Doctor registered successfully!\n\n" +
                "Doctor ID: " + doctor.getDoctorId() + "\n" +
                "Name: " + doctor.getFullName() + "\n" +
                "Specialization: " + doctor.getSpecialization(),
                "Registration Successful", JOptionPane.INFORMATION_MESSAGE);

            dispose();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                "Registration failed: " + ex.getMessage(),
                "Registration Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Unexpected error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm(ActionEvent e) {
        firstNameField.setText("");
        lastNameField.setText("");
        specializationCombo.setSelectedIndex(0);
        emailField.setText("");
        phoneField.setText("");
        licenseNumberField.setText("");
        consultationFeeField.setText("");
        qualificationCombo.setSelectedIndex(0);
        experienceSpinner.setValue(0);
        timeSlotArea.setText("09:00-10:00\n10:00-11:00\n11:00-12:00\n14:00-15:00\n15:00-16:00\n16:00-17:00");
        
        // Clear error messages
        firstNameError.setText("");
        lastNameError.setText("");
        emailError.setText("");
        phoneError.setText("");
        feeError.setText("");
    }
}