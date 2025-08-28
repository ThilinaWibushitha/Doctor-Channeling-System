package com.xyz.Ui;

import com.xyz.Data.DoctorRepository;
import com.xyz.Service.DoctorService;
import com.xyz.model.Doctor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class DoctorWindow extends JFrame {

    private final Menu mainWindow;
    private final DoctorRepository doctorRepository;
    private final DoctorService doctorService;
    private JTable doctorTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public DoctorWindow(Menu mainWindow) {
        this.mainWindow = mainWindow;
        this.doctorRepository = new DoctorRepository();
        this.doctorService = new DoctorService(doctorRepository);

        setTitle("Doctor Management System");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);

        initializeUI();
        loadDoctors();
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
        JLabel titleLabel = new JLabel("🏥 Doctor Management", SwingConstants.LEFT);
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
        String[] columnNames = {"ID", "Name", "Specialization", "Email", "Phone", "License", "Fee ($)", "Experience (Years)", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        doctorTable = new JTable(tableModel);
        doctorTable.setFont(new Font("Arial", Font.PLAIN, 12));
        doctorTable.setRowHeight(25);
        doctorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        doctorTable.setGridColor(new Color(200, 200, 200));
        doctorTable.setShowGrid(true);

        // Style table header
        doctorTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        doctorTable.getTableHeader().setBackground(new Color(70, 130, 180));
        doctorTable.getTableHeader().setForeground(Color.WHITE);

        // Add double-click listener for editing
        doctorTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedDoctor();
                }
            }
        });

        // Add table sorter
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        doctorTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(doctorTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        return centerPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);

        // Create buttons
        JButton addButton = createStyledButton("➕ Add Doctor", new Color(34, 139, 34), () -> registerDoctor(null));
        JButton editButton = createStyledButton("✏️ Edit Doctor", new Color(255, 165, 0), () -> editSelectedDoctor());
        JButton deleteButton = createStyledButton("🗑️ Delete Doctor", new Color(220, 20, 60), () -> deleteDoctor(null));
        JButton refreshButton = createStyledButton("🔄 Refresh", new Color(70, 130, 180), () -> loadDoctors());
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

    private void loadDoctors() {
        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            updateTable(doctors);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading doctors: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Doctor> doctors) {
        tableModel.setRowCount(0);
        
        for (Doctor doctor : doctors) {
            Object[] row = {
                doctor.getDoctorId(),
                doctor.getFullName(),
                doctor.getSpecialization(),
                doctor.getEmail(),
                doctor.getPhoneNumber(),
                doctor.getLicenseNumber(),
                String.format("%.2f", doctor.getConsultationFee()),
                doctor.getExperienceYears(),
                "Active" // Status placeholder
            };
            tableModel.addRow(row);
        }
    }

    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadDoctors();
            return;
        }

        try {
            List<Doctor> results = doctorService.searchDoctorsByName(searchTerm);
            if (results.isEmpty()) {
                results = doctorService.searchDoctorsBySpecialization(searchTerm);
            }
            updateTable(results);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error searching: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registerDoctor(ActionEvent e) {
        new DoctorRegistrationForm(doctorRepository).setVisible(true);
        // Refresh table after registration
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(@SuppressWarnings("unused") ActionEvent evt) {
                loadDoctors();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void editSelectedDoctor() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a doctor to edit.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the actual row index (accounting for sorting)
        int modelRow = doctorTable.convertRowIndexToModel(selectedRow);
        String doctorId = (String) tableModel.getValueAt(modelRow, 0);

        try {
            var doctorOpt = doctorService.getDoctorById(doctorId);
            if (doctorOpt.isPresent()) {
                showEditDialog(doctorOpt.get());
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Doctor not found.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error fetching doctor: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showEditDialog(Doctor doctor) {
        new DoctorEditForm(doctor, doctorService, this).setVisible(true);
    }

    private void deleteDoctor(ActionEvent e) {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a doctor to delete.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the actual row index (accounting for sorting)
        int modelRow = doctorTable.convertRowIndexToModel(selectedRow);
        String doctorId = (String) tableModel.getValueAt(modelRow, 0);
        String doctorName = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete doctor:\n" + doctorName + " (ID: " + doctorId + ")?",
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                doctorService.deleteDoctorById(doctorId);
                JOptionPane.showMessageDialog(this, 
                    "Doctor deleted successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadDoctors(); // Refresh table
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting doctor: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Public method to refresh table (called from edit form)
    public void refreshTable() {
        loadDoctors();
    }

    // Legacy methods for backward compatibility
    @SuppressWarnings("unused")
    private void viewAllDoctors(ActionEvent e) {
        loadDoctors();
    }

    @SuppressWarnings("unused")
    private void searchDoctors(ActionEvent e) {
        performSearch();
    }

    @SuppressWarnings("unused")
    private void updateDoctor(ActionEvent e) {
        editSelectedDoctor();
    }
}
