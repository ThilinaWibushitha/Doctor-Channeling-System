package com.xyz.Ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Menu extends JFrame {

    public Menu() {
        setTitle("Doctor Channelling System - Main Menu");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Create main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Create gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(240, 248, 255),
                    0, getHeight(), new Color(230, 230, 250)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center panel with buttons
        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Footer panel
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BorderLayout());

        // Title label
        JLabel titleLabel = new JLabel("Doctor Channelling System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Comprehensive Medical Appointment Management", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitleLabel.setForeground(new Color(70, 130, 180));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        return headerPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new GridLayout(2, 2, 20, 20));

        // Create menu buttons
        centerPanel.add(createMenuButton("👥 Patient Management", "Manage patient records and information", new Color(70, 130, 180)));
        centerPanel.add(createMenuButton("🏥 Doctor Management", "Manage doctor registrations and schedules", new Color(34, 139, 34)));
        centerPanel.add(createMenuButton("📅 Appointment Management", "Schedule and manage appointments", new Color(255, 140, 0)));
        centerPanel.add(createMenuButton("📊 System Reports", "View system statistics and reports", new Color(138, 43, 226)));

        return centerPanel;
    }

    private JPanel createMenuButton(String title, String description, Color color) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout(10, 10));
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Title label
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(color);
        buttonPanel.add(titleLabel, BorderLayout.NORTH);

        // Description label
        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>", SwingConstants.CENTER);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(Color.GRAY);
        buttonPanel.add(descLabel, BorderLayout.CENTER);

        // Add hover effect
        buttonPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                buttonPanel.setBackground(new Color(245, 245, 245));
                buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color, 3),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                buttonPanel.setBackground(Color.WHITE);
                buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color, 2),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                handleMenuClick(title);
            }
        });

        return buttonPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setLayout(new BorderLayout());

        // Status label
        JLabel statusLabel = new JLabel("System Status: Ready", SwingConstants.LEFT);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.GRAY);
        footerPanel.add(statusLabel, BorderLayout.WEST);

        // Exit button
        JButton exitButton = new JButton("Exit Application");
        exitButton.setFont(new Font("Arial", Font.BOLD, 12));
        exitButton.setBackground(new Color(220, 20, 60));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setBorderPainted(false);
        exitButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit the application?",
                "Confirm Exit", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        footerPanel.add(exitButton, BorderLayout.EAST);

        return footerPanel;
    }

    private void handleMenuClick(String menuTitle) {
        switch (menuTitle) {
            case "👥 Patient Management":
                openPatientWindow(null);
                break;
            case "🏥 Doctor Management":
                openDoctorWindow(null);
                break;
            case "📅 Appointment Management":
                openAppointmentWindow(null);
                break;
            case "📊 System Reports":
                showSystemReports();
                break;
        }
    }

    private void openPatientWindow(ActionEvent e) {
        new PatientWindow(this).setVisible(true);
        this.setVisible(false);
    }

    private void openDoctorWindow(ActionEvent e) {
        new DoctorWindow(this).setVisible(true);
        this.setVisible(false);
    }

    private void openAppointmentWindow(ActionEvent e) {
        new AppointmentWindow(this).setVisible(true);
        this.setVisible(false);
    }

    private void showSystemReports() {
        try {
            Class<?> clazz = Class.forName("com.xyz.Ui.SystemReportsWindow");
            java.lang.reflect.Constructor<?> ctor = clazz.getConstructor(Menu.class);
            javax.swing.JFrame wnd = (javax.swing.JFrame) ctor.newInstance(this);
            wnd.setVisible(true);
        } catch (Throwable t) {
            JOptionPane.showMessageDialog(this,
                "Reports module not available.\n" + t.getMessage(),
                "Module Unavailable", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Call this method to go back to main window
    public void returnToMain() {
        this.setVisible(true);
    }
}
