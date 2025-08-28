package com.xyz.Utility;

import com.xyz.Data.DoctorRepository;
import com.xyz.model.Doctor;

public class TestMain {
    public static void main(String[] args) {
        DoctorRepository repo = new DoctorRepository();

        // Create a new Doctor object with minimal required fields
        Doctor doctor = new Doctor();
        doctor.setFirstName("Dr. Smith");
        doctor.setSpecialization("Cardiology");
        // You may want to set other required fields as well (lastName, email, etc.)
        doctor.setLastName("Smith");
        doctor.setEmail("dr.smith@example.com");
        doctor.setPhoneNumber("1234567890");
        doctor.setLicenseNumber("LIC12345");
        doctor.setConsultationFee(100.0);
        doctor.setQualification("MD");
        doctor.setExperienceYears(10);

        // Save the doctor to DB
        repo.save(doctor);

        System.out.println("Doctor inserted successfully.");
    }
}
