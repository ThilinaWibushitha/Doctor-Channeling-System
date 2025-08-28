package com.xyz.Data;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xyz.Utility.MongoDBConnection;
import com.xyz.model.Patient;
import com.mongodb.client.model.Filters;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientRepository {
    private final MongoCollection<Patient> patientCollection;
    private int nextId = 1;

    public PatientRepository(MongoDatabase database) {
        this.patientCollection = database.getCollection("patients", Patient.class);
        initializeNextId();
    }

    public PatientRepository() {
        this(MongoDBConnection.getInstance().getDatabase());
    }

    private void initializeNextId() {
        try {
            FindIterable<Patient> patients = patientCollection.find();
            int maxId = 0;
            for (Patient p : patients) {
                String pid = p.getPatientId();
                if (pid != null && pid.startsWith("P")) {
                    try {
                        int idNum = Integer.parseInt(pid.substring(1));
                        if (idNum > maxId) maxId = idNum;
                    } catch (NumberFormatException ignored) {}
                }
            }
            nextId = maxId + 1;
        } catch (Exception e) {
            System.err.println("Could not initialize nextId: " + e.getMessage());
            nextId = 1;
        }
    }

    private String generatePatientId() {
        return "P" + String.format("%04d", nextId++);
    }

    public Patient insertPatient(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null");
        }
        // Assign a Pxxxx ID if not already set
        if (patient.getPatientId() == null || patient.getPatientId().isEmpty()) {
            patient.setPatientId(generatePatientId());
        }
        patientCollection.insertOne(patient);
        System.out.println("Inserted patient with ID: " + patient.getPatientId());
        return patient;
    }

    public List<Patient> getAllPatients() {
        return patientCollection.find().into(new ArrayList<>());
    }

    public Patient getPatientById(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return null;
        }
        return patientCollection.find(Filters.eq("patientId", patientId)).first();
    }

    public List<Patient> searchPatientsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String regex = ".*" + name + ".*";
        return patientCollection.find(
                Filters.or(
                        Filters.regex("firstName", regex, "i"),
                        Filters.regex("lastName", regex, "i")
                )
        ).into(new ArrayList<>());
    }

    public boolean updatePatient(Patient patient) {
        if (patient == null || patient.getPatientId() == null || patient.getPatientId().trim().isEmpty()) {
            return false;
        }
        var result = patientCollection.replaceOne(
            Filters.eq("patientId", patient.getPatientId()),
            patient
        );
        return result.getModifiedCount() > 0;
    }

    public boolean deletePatientById(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return false;
        }
        var result = patientCollection.deleteOne(Filters.eq("patientId", patientId));
        return result.getDeletedCount() > 0;
    }

    public long count() {
        return patientCollection.countDocuments();
    }

    public Optional<Patient> findById(String patientId) {
        return Optional.ofNullable(getPatientById(patientId));
    }
}