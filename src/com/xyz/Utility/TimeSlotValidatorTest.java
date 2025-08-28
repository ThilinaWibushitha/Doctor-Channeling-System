package com.xyz.Utility;

/**
 * Simple test class for TimeSlotValidator
 * Run this to test time slot validation functionality
 */
public class TimeSlotValidatorTest {
    
    public static void main(String[] args) {
        System.out.println("🕐 TimeSlotValidator Test Results");
        System.out.println("================================\n");
        
        // Test time format validation
        testTimeFormatValidation();
        
        // Test time conversion
        testTimeConversion();
        
        // Test business hours validation
        testBusinessHoursValidation();
        
        // Test time slot availability
        testTimeSlotAvailability();
        
        // Test find containing time slot
        testFindContainingTimeSlot();
        
        System.out.println("\n✅ All tests completed!");
    }
    
    private static void testTimeFormatValidation() {
        System.out.println("📋 Testing Time Format Validation:");
        
        String[] valid24HourTimes = {"08:00", "09:30", "14:15", "18:00", "23:45"};
        String[] valid12HourTimes = {"8:00 AM", "9:30 AM", "2:15 PM", "6:00 PM", "11:45 PM"};
        String[] invalidTimes = {"25:00", "12:60", "13:30 AM", "8:00 PM", "abc", ""};
        
        System.out.println("  Valid 24-hour formats:");
        for (String time : valid24HourTimes) {
            boolean isValid = TimeSlotValidator.isValid24HourFormat(time);
            System.out.println("    " + time + " -> " + (isValid ? "✅" : "❌"));
        }
        
        System.out.println("  Valid 12-hour formats:");
        for (String time : valid12HourTimes) {
            boolean isValid = TimeSlotValidator.isValid12HourFormat(time);
            System.out.println("    " + time + " -> " + (isValid ? "✅" : "❌"));
        }
        
        System.out.println("  Invalid formats:");
        for (String time : invalidTimes) {
            boolean isValid = TimeSlotValidator.isValidTimeFormat(time);
            System.out.println("    " + time + " -> " + (isValid ? "✅" : "❌"));
        }
        System.out.println();
    }
    
    private static void testTimeConversion() {
        System.out.println("🔄 Testing Time Conversion:");
        
        String[] times12Hour = {"8:00 AM", "9:30 AM", "2:15 PM", "6:00 PM", "11:45 PM"};
        String[] times24Hour = {"08:00", "09:30", "14:15", "18:00", "23:45"};
        
        System.out.println("  12-hour to 24-hour conversion:");
        for (String time : times12Hour) {
            try {
                String converted = TimeSlotValidator.convert12To24Hour(time);
                System.out.println("    " + time + " -> " + converted + " ✅");
            } catch (Exception e) {
                System.out.println("    " + time + " -> Error: " + e.getMessage() + " ❌");
            }
        }
        
        System.out.println("  24-hour to 12-hour conversion:");
        for (String time : times24Hour) {
            try {
                String converted = TimeSlotValidator.convert24To12Hour(time);
                System.out.println("    " + time + " -> " + converted + " ✅");
            } catch (Exception e) {
                System.out.println("    " + time + " -> Error: " + e.getMessage() + " ❌");
            }
        }
        System.out.println();
    }
    
    private static void testBusinessHoursValidation() {
        System.out.println("🏢 Testing Business Hours Validation:");
        
        String[] withinBusinessHours = {"08:00", "09:30", "14:15", "17:45", "18:00"};
        String[] outsideBusinessHours = {"07:30", "19:00", "23:00", "00:00"};
        
        System.out.println("  Within business hours:");
        for (String time : withinBusinessHours) {
            boolean isWithin = TimeSlotValidator.isWithinBusinessHours(time);
            System.out.println("    " + time + " -> " + (isWithin ? "✅" : "❌"));
        }
        
        System.out.println("  Outside business hours:");
        for (String time : outsideBusinessHours) {
            boolean isWithin = TimeSlotValidator.isWithinBusinessHours(time);
            System.out.println("    " + time + " -> " + (isWithin ? "✅" : "❌"));
        }
        System.out.println();
    }
    
    private static void testTimeSlotAvailability() {
        System.out.println("📅 Testing Time Slot Availability:");
        
        java.util.List<String> existingSlots = java.util.Arrays.asList("09:00", "10:30", "14:00");
        String[] testSlots = {"08:30", "09:15", "10:00", "11:00", "14:30"};
        
        System.out.println("  Existing slots: " + existingSlots);
        System.out.println("  Testing availability:");
        
        for (String slot : testSlots) {
            boolean isAvailable = TimeSlotValidator.isTimeSlotAvailable(slot, existingSlots);
            System.out.println("    " + slot + " -> " + (isAvailable ? "Available ✅" : "Not Available ❌"));
        }
        
        // Test next available slot
        String nextSlot = TimeSlotValidator.getNextAvailableTimeSlot("09:00", existingSlots);
        System.out.println("  Next available slot after 09:00: " + (nextSlot != null ? nextSlot : "None found"));
        System.out.println();
    }
    
    private static void testFindContainingTimeSlot() {
        System.out.println("🔍 Testing Find Containing Time Slot:");
        
        // Test with actual doctor time slots from the system
        java.util.List<String> doctorTimeSlots = java.util.Arrays.asList(
            "09:00-10:00", "10:00-11:00", "11:00-12:00",
            "14:00-15:00", "15:00-16:00", "16:00-17:00"
        );
        
        System.out.println("  Doctor time slots: " + doctorTimeSlots);
        System.out.println("  Testing appointment times:");
        
        String[] appointmentTimes = {"09:30", "10:15", "11:30", "14:30", "15:45", "16:30", "08:30", "18:30"};
        
        for (String appointmentTime : appointmentTimes) {
            String containingSlot = TimeSlotValidator.findContainingTimeSlot(appointmentTime, doctorTimeSlots);
            if (containingSlot != null) {
                System.out.println("    " + appointmentTime + " -> Found in slot: " + containingSlot + " ✅");
            } else {
                System.out.println("    " + appointmentTime + " -> No containing slot found ❌");
            }
        }
        System.out.println();
    }
}
