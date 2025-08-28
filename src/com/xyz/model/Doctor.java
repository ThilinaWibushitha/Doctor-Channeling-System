// Doctor.java
package com.xyz.model;

import java.util.*;

public class Doctor {
    private String doctorId;
    private String firstName;
    private String lastName;
    private String specialization;
    private String email;
    private String phoneNumber;
    private String licenseNumber;
    private double consultationFee;
    private Set<String> availableTimeSlots = new HashSet<>();
    private String qualification;
    private int experienceYears;

    public Doctor() {
        initializeDefaultSlots();
    }

    public Doctor(String id, String fn, String ln, String spec,
                  String email, String phone, String lic,
                  double fee, String qual, int exp) {
        this.doctorId        = id;
        this.firstName       = fn;
        this.lastName        = ln;
        this.specialization  = spec;
        this.email           = email;
        this.phoneNumber     = phone;
        this.licenseNumber   = lic;
        this.consultationFee = fee;
        this.qualification   = qual;
        this.experienceYears = exp;
        initializeDefaultSlots();
    }

    private void initializeDefaultSlots() {
        availableTimeSlots.addAll(Arrays.asList(
            "09:00-10:00", "10:00-11:00", "11:00-12:00",
            "14:00-15:00", "15:00-16:00", "16:00-17:00"
        ));
    }

    // Getters
    public String getDoctorId() { return doctorId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getSpecialization() { return specialization; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getLicenseNumber() { return licenseNumber; }
    public double getConsultationFee() { return consultationFee; }
    public Set<String> getAvailableTimeSlots() { return new HashSet<>(availableTimeSlots); } // Return a copy
    public String getQualification() { return qualification; }
    public int getExperienceYears() { return experienceYears; }

    // Setters
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }
    // No setter for availableTimeSlots directly, use add/remove methods
    public void setQualification(String qualification) { this.qualification = qualification; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }

    // Utility methods for time slots
    public boolean addTimeSlot(String timeSlot) {
        return availableTimeSlots.add(timeSlot);
    }

    public boolean removeTimeSlot(String timeSlot) {
        return availableTimeSlots.remove(timeSlot);
    }

    public boolean isSlotAvailable(String timeSlot) {
        return availableTimeSlots.contains(timeSlot);
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Doctor doctor = (Doctor) o;
        return Objects.equals(doctorId, doctor.doctorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctorId);
    }

    @Override
    public String toString() {
        return String.format("Doctor[ID=%s, Name=%s %s, Specialization=%s, Email=%s]",
                doctorId, firstName, lastName, specialization, email);
    }
}