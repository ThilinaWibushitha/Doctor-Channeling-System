package com.xyz.Ui;

import com.xyz.Data.PatientRepository;
import com.xyz.Service.PatientService;
import com.xyz.model.Patient;
import com.xyz.Utility.Validator;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PatientRegistrationForm extends JFrame {

    private final PatientService patientService;
    
    // Form fields
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField dateOfBirthField;
    private JTextField addressField;
    
    // Buttons
    private JButton registerButton;
    private JButton clearButton;
    private JButton cancelButton;

    public PatientRegistrationForm(PatientRepository patientRepository) {
        this.patientService = new PatientService(patientRepository);

        initializeComponents();
        setupLayout();
        setupEventHandlers();

        setTitle("Patient Registration Form");
        setSize(500, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    private void initializeComponents() {
        firstNameField = new JTextField(20);
        lastNameField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);
        dateOfBirthField = new JTextField(20);
        addressField = new JTextField(20);

        registerButton = new JButton("Register Patient");
        clearButton = new JButton("Clear Form");
        cancelButton = new JButton("Cancel");

        registerButton.setBackground(new Color(34, 139, 34));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 12));

        clearButton.setBackground(new Color(255, 165, 0));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFont(new Font("Arial", Font.BOLD, 12));

        cancelButton.setBackground(new Color(220, 20, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 12));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("Patient Registration Form");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        addFormField(mainPanel, gbc, "First Name:", firstNameField, 1);
        addFormField(mainPanel, gbc, "Last Name:", lastNameField, 2);
        addFormField(mainPanel, gbc, "Email:", emailField, 3);
        addFormField(mainPanel, gbc, "Phone Number:", phoneField, 4);
        addFormField(mainPanel, gbc, "Date of Birth (yyyy-MM-dd):", dateOfBirthField, 5);
        addFormField(mainPanel, gbc, "Address:", addressField, 6);

        gbc.gridx = 1;
        gbc.gridy = 7;
        JLabel noteLabel = new JLabel("Date format: yyyy-MM-dd");
        noteLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        noteLabel.setForeground(Color.GRAY);
        mainPanel.add(noteLabel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(registerButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        field.setPreferredSize(new Dimension(300, 25));
        panel.add(field, gbc);
    }

    private void setupEventHandlers() {
        registerButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent e) {
                handlePatientRegistration();
            }
        });

        clearButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent e) {
                clearForm();
            }
        });

        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent e) {
                dispose();
            }
        });
    }

    private void handlePatientRegistration() {
        try {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String dateOfBirthText = dateOfBirthField.getText().trim();
            String address = addressField.getText().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || 
                phone.isEmpty() || dateOfBirthText.isEmpty() || address.isEmpty()) {
                showErrorMessage("Please fill in all required fields.");
                return;
            }

            LocalDate dateOfBirth;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                dateOfBirth = LocalDate.parse(dateOfBirthText, formatter);
            } catch (DateTimeParseException ex) {
                showErrorMessage("Invalid date format. Please use yyyy-MM-dd format.");
                return;
            }

            String validationError = Validator.validatePatientData(firstName, lastName, email, phone, dateOfBirth);
            if (validationError != null) {
                showErrorMessage(validationError);
                return;
            }

            // Register the patient
            Patient patient = patientService.registerPatient(firstName, lastName, email, phone, dateOfBirth, address);

            showSuccessMessage("Patient registered successfully and saved to database!\n" +
                             "Patient ID: " + patient.getPatientId() + "\n" +
                             "Name: " + patient.getFullName());

            int option = JOptionPane.showConfirmDialog(this,
                    "Would you like to register another patient?",
                    "Registration Successful",
                    JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                clearForm();
            } else {
                dispose();
            }

        } catch (IllegalArgumentException ex) {
            showErrorMessage("Registration failed: " + ex.getMessage());
        } catch (Exception ex) {
            showErrorMessage("Database error occurred: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void clearForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        dateOfBirthField.setText("");
        addressField.setText("");
        firstNameField.requestFocus();
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            PatientRepository repository = new PatientRepository();
            new PatientRegistrationForm(repository).setVisible(true);
        });
    }
}