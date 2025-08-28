package com.xyz.Ui;

import com.xyz.Utility.TimeSlotValidator;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;

/**
 * User-friendly dialog for selecting time slots with validation
 */
public class TimeSlotInputDialog extends JDialog {
    
    private String selectedTime = null;
    private final JComboBox<String> hourCombo;
    private final JComboBox<String> minuteCombo;
    private final JComboBox<String> periodCombo;
    private final JCheckBox use24HourCheck;
    private final JLabel businessHoursLabel;
    
    public TimeSlotInputDialog(JFrame parent, String title) {
        super(parent, title, true);
        
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Initialize components
        hourCombo = new JComboBox<>();
        minuteCombo = new JComboBox<>();
        periodCombo = new JComboBox<>(new String[]{"AM", "PM"});
        use24HourCheck = new JCheckBox("Use 24-hour format", false);
        businessHoursLabel = new JLabel("Business Hours: " + TimeSlotValidator.getBusinessHours());
        
        initializeUI();
        setupEventHandlers();
        populateTimeOptions();
        
        // Ensure combo boxes are populated
        if (hourCombo.getItemCount() == 0 || minuteCombo.getItemCount() == 0) {
            System.err.println("Warning: Time combo boxes not properly populated");
            // Force repopulation
            populateTimeOptions();
        }
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLabel = new JLabel("Select Appointment Time", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(new Color(25, 25, 112));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Time selection panel
        JPanel timePanel = createTimeSelectionPanel();
        mainPanel.add(timePanel, BorderLayout.CENTER);
        
        // Business hours info
        JPanel infoPanel = new JPanel(new BorderLayout(5, 5));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        businessHoursLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        businessHoursLabel.setForeground(new Color(70, 130, 180));
        infoPanel.add(businessHoursLabel, BorderLayout.CENTER);
        
        // Add 24-hour format checkbox
        use24HourCheck.setFont(new Font("Arial", Font.PLAIN, 12));
        infoPanel.add(use24HourCheck, BorderLayout.SOUTH);
        
        mainPanel.add(infoPanel, BorderLayout.SOUTH);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createTimeSelectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Time Selection"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Hour selection
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Hour:"), gbc);
        gbc.gridx = 1;
        hourCombo.setPreferredSize(new Dimension(80, 25));
        panel.add(hourCombo, gbc);
        
        // Minute selection
        gbc.gridx = 2;
        panel.add(new JLabel("Minute:"), gbc);
        gbc.gridx = 3;
        minuteCombo.setPreferredSize(new Dimension(80, 25));
        panel.add(minuteCombo, gbc);
        
        // Period selection (AM/PM)
        gbc.gridx = 4;
        panel.add(new JLabel("Period:"), gbc);
        gbc.gridx = 5;
        periodCombo.setPreferredSize(new Dimension(60, 25));
        panel.add(periodCombo, gbc);
        
        // Quick time buttons
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 6;
        gbc.insets = new Insets(15, 5, 5, 5);
        panel.add(createQuickTimeButtons(), gbc);
        
        return panel;
    }
    
    private JPanel createQuickTimeButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Quick Selection"));
        
        String[] quickTimes = {"9:00 AM", "10:00 AM", "11:00 AM", "2:00 PM", "3:00 PM", "4:00 PM"};
        
        for (String time : quickTimes) {
            JButton btn = new JButton(time);
            btn.setFont(new Font("Arial", Font.PLAIN, 11));
            btn.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent e) {
                    setQuickTime(time);
                }
            });
            panel.add(btn);
        }
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        
        okButton.setPreferredSize(new Dimension(80, 30));
        cancelButton.setPreferredSize(new Dimension(80, 30));
        
        okButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent e) {
                validateAndConfirm();
            }
        });
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        
        panel.add(okButton);
        panel.add(cancelButton);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        use24HourCheck.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent e) {
                boolean use24Hour = use24HourCheck.isSelected();
                periodCombo.setVisible(!use24Hour);
                populateTimeOptions();
            }
        });
        
        // Set initial state
        periodCombo.setVisible(!use24HourCheck.isSelected());
    }
    
    private void populateTimeOptions() {
        hourCombo.removeAllItems();
        minuteCombo.removeAllItems();
        
        boolean use24Hour = use24HourCheck.isSelected();
        
        if (use24Hour) {
            // 24-hour format: 8-18 (business hours)
            for (int i = 8; i <= 18; i++) {
                hourCombo.addItem(String.format("%02d", i));
            }
        } else {
            // 12-hour format: 1-12
            for (int i = 1; i <= 12; i++) {
                hourCombo.addItem(String.valueOf(i));
            }
        }
        
        // Minutes: 00, 15, 30, 45 (every 15 minutes)
        minuteCombo.addItem("00");
        minuteCombo.addItem("15");
        minuteCombo.addItem("30");
        minuteCombo.addItem("45");
        
        // Set default values
        hourCombo.setSelectedIndex(0);
        minuteCombo.setSelectedIndex(0);
        periodCombo.setSelectedIndex(0);
    }
    
    private void setQuickTime(String time) {
        try {
            if (TimeSlotValidator.isValid12HourFormat(time)) {
                String time24Hour = TimeSlotValidator.convert12To24Hour(time);
                LocalTime localTime = LocalTime.parse(time24Hour);
                
                // Set hour and minute
                String hour = String.format("%02d", localTime.getHour());
                String minute = String.format("%02d", localTime.getMinute());
                
                hourCombo.setSelectedItem(hour);
                minuteCombo.setSelectedItem(minute);
                
                // Set period
                if (localTime.getHour() >= 12) {
                    periodCombo.setSelectedItem("PM");
                } else {
                    periodCombo.setSelectedItem("AM");
                }
                
                // Switch to 12-hour format if needed
                use24HourCheck.setSelected(false);
                periodCombo.setVisible(true);
                
            } else if (TimeSlotValidator.isValid24HourFormat(time)) {
                LocalTime localTime = LocalTime.parse(time);
                
                String hour = String.format("%02d", localTime.getHour());
                String minute = String.format("%02d", localTime.getMinute());
                
                hourCombo.setSelectedItem(hour);
                minuteCombo.setSelectedItem(minute);
                
                // Switch to 24-hour format
                use24HourCheck.setSelected(true);
                periodCombo.setVisible(false);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error setting quick time: " + e.getMessage());
        }
    }
    
    private void validateAndConfirm() {
        try {
            // Check if combo boxes have items
            if (hourCombo.getItemCount() == 0 || minuteCombo.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this, 
                    "Time options not loaded. Please wait a moment and try again.",
                    "Initialization Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String time = buildTimeString();
            
            if (!TimeSlotValidator.isValidTimeFormat(time)) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid time format generated. Please try again.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!TimeSlotValidator.isWithinBusinessHours(time)) {
                JOptionPane.showMessageDialog(this, 
                    "Selected time is outside business hours!\n\n" +
                    "Business Hours: " + TimeSlotValidator.getBusinessHours() + "\n" +
                    "Please select a time within business hours.",
                    "Outside Business Hours", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            selectedTime = time;
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error validating time: " + e.getMessage() + "\n\nPlease ensure all time components are selected.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String buildTimeString() {
        String hour = (String) hourCombo.getSelectedItem();
        String minute = (String) minuteCombo.getSelectedItem();
        
        // Add null checks to prevent errors
        if (hour == null || minute == null) {
            throw new IllegalStateException("Please select both hour and minute values");
        }
        
        if (use24HourCheck.isSelected()) {
            // 24-hour format
            return hour + ":" + minute;
        } else {
            // 12-hour format
            String period = (String) periodCombo.getSelectedItem();
            if (period == null) {
                throw new IllegalStateException("Please select AM/PM period");
            }
            return hour + ":" + minute + " " + period;
        }
    }
    
    public String getSelectedTime() {
        return selectedTime;
    }
    
    public static String showTimeSlotDialog(JFrame parent, String title) {
        TimeSlotInputDialog dialog = new TimeSlotInputDialog(parent, title);
        dialog.setVisible(true);
        return dialog.getSelectedTime();
    }
}
