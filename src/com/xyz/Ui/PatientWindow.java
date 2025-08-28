package com.xyz.Ui;

import com.xyz.Data.PatientRepository;
import com.xyz.Service.PatientService;
import com.xyz.model.Patient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PatientWindow extends JFrame {

    private final Menu mainWindow;
    private final PatientRepository patientRepository;
    private final PatientService patientService;
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public PatientWindow(Menu mainWindow) {
        this.mainWindow = mainWindow;
        this.patientRepository = new PatientRepository();
        this.patientService = new PatientService(patientRepository);

        setTitle("Patient Management System");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);

        initializeUI();
        loadPatients();
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

        // Header panel
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Center panel with table and search
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);

        // Button panel
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel("👥 Patient Management", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 12));
        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JButton searchButton = new JButton("🔍 Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 12));
        searchButton.setBackground(new Color(70, 130, 180));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setBorderPainted(false);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(@SuppressWarnings("unused") ActionEvent e) {
                performSearch();
            }
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);

        // Create table
        String[] columnNames = {"ID", "Name", "Email", "Phone", "Date of Birth", "Age", "Address", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        patientTable = new JTable(tableModel);
        patientTable.setFont(new Font("Arial", Font.PLAIN, 12));
        patientTable.setRowHeight(25);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientTable.setGridColor(new Color(200, 200, 200));
        patientTable.setShowGrid(true);

        // Style table header
        patientTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        patientTable.getTableHeader().setBackground(new Color(70, 130, 180));
        patientTable.getTableHeader().setForeground(Color.WHITE);

        // Add double-click listener for editing
        patientTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedPatient();
                }
            }
        });

        // Add table sorter
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        patientTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(patientTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        return centerPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);

        // Create buttons
        JButton addButton = createStyledButton("➕ Add Patient", new Color(34, 139, 34), () -> registerPatient(null));
        JButton editButton = createStyledButton("✏️ Edit Patient", new Color(255, 165, 0), () -> editSelectedPatient());
        JButton deleteButton = createStyledButton("🗑️ Delete Patient", new Color(220, 20, 60), () -> deletePatient(null));
        JButton refreshButton = createStyledButton("🔄 Refresh", new Color(70, 130, 180), () -> loadPatients());
        JButton backButton = createStyledButton("⬅️ Back to Menu", Color.GRAY, () -> {
            this.dispose();
            mainWindow.returnToMain();
        });

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        return buttonPanel;
    }

    private JButton createStyledButton(String text, Color backgroundColor, Runnable onClick) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(@SuppressWarnings("unused") ActionEvent e) {
                onClick.run();
            }
        });
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(@SuppressWarnings("unused") MouseEvent e) {
                button.setBackground(backgroundColor.brighter());
            }

            @Override
            public void mouseExited(@SuppressWarnings("unused") MouseEvent e) {
                button.setBackground(backgroundColor);
            }
        });

        return button;
    }

    private void loadPatients() {
        try {
            List<Patient> patients = patientService.getAllPatients();
            updateTable(patients);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading patients: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Patient> patients) {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Patient patient : patients) {
            LocalDate dob = patient.getDateOfBirth();
            int age = dob != null ? LocalDate.now().getYear() - dob.getYear() : 0;
            String dobStr = dob != null ? dob.format(formatter) : "N/A";
            
            Object[] row = {
                patient.getPatientId(),
                patient.getFullName(),
                patient.getEmail(),
                patient.getPhone(),
                dobStr,
                age,
                patient.getAddress(),
                "Active" // Status placeholder
            };
            tableModel.addRow(row);
        }
    }

    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadPatients();
            return;
        }

        try {
            List<Patient> results = patientService.searchPatientsByName(searchTerm);
            updateTable(results);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error searching: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registerPatient(ActionEvent e) {
        new PatientRegistrationForm(patientRepository).setVisible(true);
        // Refresh table after registration
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(@SuppressWarnings("unused") ActionEvent evt) {
                loadPatients();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void editSelectedPatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a patient to edit.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the actual row index (accounting for sorting)
        int modelRow = patientTable.convertRowIndexToModel(selectedRow);
        String patientId = (String) tableModel.getValueAt(modelRow, 0);

        try {
            var patientOpt = patientService.getPatientById(patientId);
            if (patientOpt.isPresent()) {
                showEditDialog(patientOpt.get());
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Patient not found.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error fetching patient: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showEditDialog(Patient patient) {
        new PatientEditForm(patient, patientService, this).setVisible(true);
    }

    private void deletePatient(ActionEvent e) {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a patient to delete.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the actual row index (accounting for sorting)
        int modelRow = patientTable.convertRowIndexToModel(selectedRow);
        String patientId = (String) tableModel.getValueAt(modelRow, 0);
        String patientName = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete patient:\n" + patientName + " (ID: " + patientId + ")?",
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                patientService.deletePatientById(patientId);
                JOptionPane.showMessageDialog(this, 
                    "Patient deleted successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPatients(); // Refresh table
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting patient: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Public method to refresh table (called from edit form)
    public void refreshTable() {
        loadPatients();
    }

    // Legacy methods for backward compatibility
    @SuppressWarnings("unused")
    private void viewAllPatients(ActionEvent e) {
        loadPatients();
    }

    @SuppressWarnings("unused")
    private void searchPatients(ActionEvent e) {
        performSearch();
    }

    @SuppressWarnings("unused")
    private void updatePatient(ActionEvent e) {
        editSelectedPatient();
    }
}
