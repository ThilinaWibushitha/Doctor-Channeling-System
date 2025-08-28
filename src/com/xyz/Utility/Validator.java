package com.xyz.Utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class Validator {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[\\+]?[1-9]?[0-9]{7,15}$");
    private static final Pattern TIME_SLOT_PATTERN =
            Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]-([01]?[0-9]|2[0-3]):[0-5][0-9]$");
    private static final Pattern SINGLE_TIME_PATTERN =
            Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.trim().length() >= 2;
    }

    public static boolean isValidTimeSlot(String timeSlot) {
        if (timeSlot == null) return false;
        
        // Check if it's a range format (HH:MM-HH:MM)
        if (TIME_SLOT_PATTERN.matcher(timeSlot).matches()) {
            return true;
        }
        
        // Check if it's a single time format (HH:MM)
        if (SINGLE_TIME_PATTERN.matcher(timeSlot).matches()) {
            return true;
        }
        
        return false;
    }

    public static boolean isValidDate(LocalDate date) {
        return date != null && !date.isBefore(LocalDate.now());
    }

    public static boolean isValidDateTime(LocalDateTime dateTime) {
        return dateTime != null && !dateTime.isBefore(LocalDateTime.now());
    }

    public static boolean isValidConsultationFee(double fee) {
        return fee >= 0;
    }

    public static boolean isValidAge(int age) {
        return age >= 0 && age <= 150;
    }

    public static boolean isValidExperience(int years) {
        return years >= 0 && years <= 60;
    }

    public static String validatePatientData(String firstName, String lastName, String email,
                                             String phone, LocalDate dateOfBirth) {
        if (!isValidName(firstName)) {
            return "Invalid first name. Must be at least 2 characters.";
        }
        if (!isValidName(lastName)) {
            return "Invalid last name. Must be at least 2 characters.";
        }
        if (!isValidEmail(email)) {
            return "Invalid email format.";
        }
        if (!isValidPhoneNumber(phone)) {
            return "Invalid phone number format.";
        }
        if (dateOfBirth == null || dateOfBirth.isAfter(LocalDate.now())) {
            return "Invalid date of birth.";
        }
        return null; // No validation errors
    }

    public static String validateDoctorData(String firstName, String lastName, String email,
                                            String phone, double fee, int experience) {
        if (!isValidName(firstName)) {
            return "Invalid first name. Must be at least 2 characters.";
        }
        if (!isValidName(lastName)) {
            return "Invalid last name. Must be at least 2 characters.";
        }
        if (!isValidEmail(email)) {
            return "Invalid email format.";
        }
        if (!isValidPhoneNumber(phone)) {
            return "Invalid phone number format.";
        }
        if (!isValidConsultationFee(fee)) {
            return "Invalid consultation fee. Must be non-negative.";
        }
        if (!isValidExperience(experience)) {
            return "Invalid experience years. Must be between 0 and 60.";
        }
        return null; // No validation errors
    }
}
