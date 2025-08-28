package com.xyz.Utility;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Utility class for validating time slots in the Doctor Channelling System
 */
public class TimeSlotValidator {
    
    // Time format patterns
    private static final Pattern TIME_24HOUR_PATTERN = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
    private static final Pattern TIME_12HOUR_PATTERN = Pattern.compile("^(1[0-2]|0?[1-9]):[0-5][0-9]\\s?(AM|PM|am|pm)$");
    
    // Business hours (configurable)
    private static final LocalTime BUSINESS_START = LocalTime.of(8, 0);  // 8:00 AM
    private static final LocalTime BUSINESS_END = LocalTime.of(18, 0);   // 6:00 PM
    
    // Appointment duration in minutes
    private static final int APPOINTMENT_DURATION = 30;
    
    /**
     * Validates if a time string is in valid 24-hour format (HH:MM)
     */
    public static boolean isValid24HourFormat(String time) {
        if (time == null || time.trim().isEmpty()) {
            return false;
        }
        
        return TIME_24HOUR_PATTERN.matcher(time.trim()).matches();
    }
    
    /**
     * Validates if a time string is in valid 12-hour format (HH:MM AM/PM)
     */
    public static boolean isValid12HourFormat(String time) {
        if (time == null || time.trim().isEmpty()) {
            return false;
        }
        
        return TIME_12HOUR_PATTERN.matcher(time.trim()).matches();
    }
    
    /**
     * Validates if a time string is in any valid time format
     */
    public static boolean isValidTimeFormat(String time) {
        return isValid24HourFormat(time) || isValid12HourFormat(time);
    }
    
    /**
     * Converts 12-hour format to 24-hour format
     */
    public static String convert12To24Hour(String time12Hour) {
        if (!isValid12HourFormat(time12Hour)) {
            throw new IllegalArgumentException("Invalid 12-hour time format: " + time12Hour);
        }
        
        try {
            String[] parts = time12Hour.trim().split("\\s+");
            String timePart = parts[0];
            String period = parts[1].toUpperCase();
            
            String[] timeComponents = timePart.split(":");
            int hour = Integer.parseInt(timeComponents[0]);
            int minute = Integer.parseInt(timeComponents[1]);
            
            if (period.equals("PM") && hour != 12) {
                hour += 12;
            } else if (period.equals("AM") && hour == 12) {
                hour = 0;
            }
            
            return String.format("%02d:%02d", hour, minute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting time format: " + time12Hour);
        }
    }
    
    /**
     * Converts 24-hour format to 12-hour format
     */
    public static String convert24To12Hour(String time24Hour) {
        if (!isValid24HourFormat(time24Hour)) {
            throw new IllegalArgumentException("Invalid 24-hour time format: " + time24Hour);
        }
        
        try {
            LocalTime time = LocalTime.parse(time24Hour);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
            return time.format(formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Error parsing time: " + time24Hour);
        }
    }
    
    /**
     * Validates if a time is within business hours
     */
    public static boolean isWithinBusinessHours(String time) {
        if (!isValidTimeFormat(time)) {
            return false;
        }
        
        try {
            LocalTime timeObj = parseTime(time);
            return !timeObj.isBefore(BUSINESS_START) && !timeObj.isAfter(BUSINESS_END);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Validates if a time slot is available (not overlapping with existing appointments)
     */
    public static boolean isTimeSlotAvailable(String time, java.util.List<String> existingTimeSlots) {
        if (!isValidTimeFormat(time)) {
            return false;
        }
        
        try {
            LocalTime requestedTime = parseTime(time);
            
            for (String existingSlot : existingTimeSlots) {
                if (isValidTimeFormat(existingSlot)) {
                    LocalTime existingTime = parseTime(existingSlot);
                    
                    // Check if times overlap (within appointment duration)
                    if (Math.abs(java.time.Duration.between(requestedTime, existingTime).toMinutes()) < APPOINTMENT_DURATION) {
                        return false;
                    }
                }
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gets the next available time slot after a given time
     */
    public static String getNextAvailableTimeSlot(String time, java.util.List<String> existingTimeSlots) {
        if (!isValidTimeFormat(time)) {
            return null;
        }
        
        try {
            LocalTime currentTime = parseTime(time);
            LocalTime nextSlot = currentTime;
            
            // Try to find next available slot
            for (int i = 0; i < 24; i++) { // Limit to 24 attempts
                nextSlot = nextSlot.plusMinutes(APPOINTMENT_DURATION);
                
                // Check if we're still within business hours
                if (nextSlot.isAfter(BUSINESS_END)) {
                    nextSlot = BUSINESS_START.plusHours(24); // Next day
                }
                
                String nextSlotStr = nextSlot.format(DateTimeFormatter.ofPattern("HH:mm"));
                if (isTimeSlotAvailable(nextSlotStr, existingTimeSlots)) {
                    return nextSlotStr;
                }
            }
            
            return null; // No available slots found
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Validates if a time slot is at a valid interval (e.g., every 30 minutes)
     */
    public static boolean isValidTimeInterval(String time, int intervalMinutes) {
        if (!isValidTimeFormat(time)) {
            return false;
        }
        
        try {
            LocalTime timeObj = parseTime(time);
            int minutes = timeObj.getMinute();
            return minutes % intervalMinutes == 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Parses a time string to LocalTime object
     */
    private static LocalTime parseTime(String time) {
        if (isValid24HourFormat(time)) {
            return LocalTime.parse(time);
        } else if (isValid12HourFormat(time)) {
            String time24Hour = convert12To24Hour(time);
            return LocalTime.parse(time24Hour);
        } else {
            throw new IllegalArgumentException("Invalid time format: " + time);
        }
    }
    
    /**
     * Gets business hours as a formatted string
     */
    public static String getBusinessHours() {
        return String.format("%s - %s", 
            BUSINESS_START.format(DateTimeFormatter.ofPattern("h:mm a")),
            BUSINESS_END.format(DateTimeFormatter.ofPattern("h:mm a"))
        );
    }
    
    /**
     * Gets appointment duration in minutes
     */
    public static int getAppointmentDuration() {
        return APPOINTMENT_DURATION;
    }
    
    /**
     * Sets business hours (for configuration)
     */
    public static void setBusinessHours(LocalTime start, LocalTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        // Note: In a real application, you might want to make these configurable
        // through a configuration file or database
    }

    /**
     * Finds which time slot range contains a given appointment time
     * This bridges the gap between single appointment times and doctor time slot ranges
     */
    public static String findContainingTimeSlot(String appointmentTime, java.util.List<String> availableTimeSlots) {
        if (!isValidTimeFormat(appointmentTime)) {
            return null;
        }
        
        try {
            LocalTime requestedTime = parseTime(appointmentTime);
            
            for (String slotRange : availableTimeSlots) {
                if (isValidTimeSlotRange(slotRange)) {
                    String[] parts = slotRange.split("-");
                    if (parts.length == 2) {
                        LocalTime startTime = parseTime(parts[0]);
                        LocalTime endTime = parseTime(parts[1]);
                        
                        // Check if appointment time falls within this range
                        if (!requestedTime.isBefore(startTime) && requestedTime.isBefore(endTime)) {
                            return slotRange;
                        }
                    }
                }
            }
            
            return null; // No containing slot found
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Validates if a time slot range is in valid format (HH:MM-HH:MM)
     */
    public static boolean isValidTimeSlotRange(String timeSlotRange) {
        if (timeSlotRange == null || timeSlotRange.trim().isEmpty()) {
            return false;
        }
        
        String[] parts = timeSlotRange.split("-");
        if (parts.length != 2) {
            return false;
        }
        
        return isValidTimeFormat(parts[0]) && isValidTimeFormat(parts[1]);
    }
    
    /**
     * Converts a single appointment time to a time slot range
     * Default duration is 30 minutes
     */
    public static String convertToTimeSlotRange(String appointmentTime) {
        if (!isValidTimeFormat(appointmentTime)) {
            return null;
        }
        
        try {
            LocalTime time = parseTime(appointmentTime);
            LocalTime endTime = time.plusMinutes(APPOINTMENT_DURATION);
            
            String startStr = time.format(DateTimeFormatter.ofPattern("HH:mm"));
            String endStr = endTime.format(DateTimeFormatter.ofPattern("HH:mm"));
            
            return startStr + "-" + endStr;
        } catch (Exception e) {
            return null;
        }
    }
}
