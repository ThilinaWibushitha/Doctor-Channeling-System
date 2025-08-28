package com.xyz.Utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputHelper {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static String readNonEmptyString(String prompt) {
        String input;
        do {
            input = readString(prompt);
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Please try again.");
            }
        } while (input.isEmpty());
        return input;
    }

    public static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please enter a valid integer.");
            }
        }
    }

    public static int readInt(String prompt, int min, int max) {
        int value;
        do {
            value = readInt(prompt);
            if (value < min || value > max) {
                System.out.printf("Value must be between %d and %d. Please try again.%n", min, max);
            }
        } while (value < min || value > max);
        return value;
    }

    public static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please enter a valid decimal number.");
            }
        }
    }

    public static double readPositiveDouble(String prompt) {
        double value;
        do {
            value = readDouble(prompt);
            if (value < 0) {
                System.out.println("Value must be positive. Please try again.");
            }
        } while (value < 0);
        return value;
    }

    public static LocalDate readDate(String prompt) {
        while (true) {
            try {
                String input = readString(prompt + " (yyyy-MM-dd): ");
                return LocalDate.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd format.");
            }
        }
    }

    public static LocalDate readFutureDate(String prompt) {
        LocalDate date;
        do {
            date = readDate(prompt);
            if (date.isBefore(LocalDate.now())) {
                System.out.println("Date must be today or in the future. Please try again.");
            }
        } while (date.isBefore(LocalDate.now()));
        return date;
    }

    public static void waitForEnter() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public static boolean readConfirmation(String prompt) {
        String response;
        do {
            response = readString(prompt + " (y/n): ").toLowerCase();
            if (response.equals("y") || response.equals("yes")) {
                return true;
            } else if (response.equals("n") || response.equals("no")) {
                return false;
            } else {
                System.out.println("Please enter 'y' for yes or 'n' for no.");
            }
        } while (true);
    }
}