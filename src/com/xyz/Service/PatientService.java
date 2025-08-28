package com.xyz.Service;

import com.xyz.Data.PatientRepository;
import com.xyz.model.Patient;
import com.xyz.Utility.Validator;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PatientService {
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Patient registerPatient(String firstName, String lastName, String email, String phoneNumber, 
                                 LocalDate dateOfBirth, String address) {
        String validationError = Validator.validatePatientData(firstName, lastName, email, phoneNumber, dateOfBirth);
        if (validationError != null) {
            throw new IllegalArgumentException(validationError);
        }

        Patient patient = new Patient(firstName, lastName, email, phoneNumber, dateOfBirth, address);
        patientRepository.insertPatient(patient);
        return patient;
    }

    public List<Patient> getAllPatients() {
        return patientRepository.getAllPatients();
    }

    public Optional<Patient> getPatientById(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient ID cannot be null or empty");
        }
        Patient patient = patientRepository.getPatientById(patientId);
        return Optional.ofNullable(patient);
    }

    public List<Patient> searchPatientsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Search term cannot be empty");
        }
        return patientRepository.searchPatientsByName(name.trim());
    }

    public void updatePatient(Patient patient) {
        if (patient == null || patient.getPatientId() == null || patient.getPatientId().trim().isEmpty()) {
            throw new IllegalArgumentException("Patient or Patient ID cannot be null or empty");
        }
        String validationError = Validator.validatePatientData(
                patient.getFirstName(),
                patient.getLastName(),
                patient.getEmail(),
                patient.getPhoneNumber(),
                patient.getDateOfBirth()
        );
        if (validationError != null) {
            throw new IllegalArgumentException(validationError);
        }
        if (!patientRepository.findById(patient.getPatientId()).isPresent()) {
            throw new IllegalArgumentException("Patient not found with ID: " + patient.getPatientId());
        }
        patientRepository.updatePatient(patient);
    }

    public void deletePatientById(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient ID cannot be null or empty");
        }
        if (!patientRepository.findById(patientId).isPresent()) {
            throw new IllegalArgumentException("Patient not found with ID: " + patientId);
        }
        patientRepository.deletePatientById(patientId);
    }
}