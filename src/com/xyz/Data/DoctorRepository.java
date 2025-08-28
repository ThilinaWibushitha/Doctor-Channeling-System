package com.xyz.Data;

import com.xyz.Utility.MongoDBConnection;
import com.xyz.model.Doctor;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import org.bson.Document;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DoctorRepository {

    private final MongoCollection<Document> doctorCollection;
    private final AtomicInteger nextId = new AtomicInteger(1);

    // Constructor with MongoDatabase parameter
    public DoctorRepository(MongoDatabase database) {
        this.doctorCollection = database.getCollection("doctors");
        initializeIdCounter();
    }

    // Default constructor uses singleton MongoDBConnection
    public DoctorRepository() {
        this.doctorCollection = MongoDBConnection.getInstance()
                .getDatabase()
                .getCollection("doctors");
        initializeIdCounter();
    }

    private void initializeIdCounter() {
        try {
            FindIterable<Document> docs = doctorCollection.find();
            int maxId = 0;
            for (Document doc : docs) {
                String doctorId = doc.getString("doctorId");
                if (doctorId != null && doctorId.startsWith("D")) {
                    try {
                        int id = Integer.parseInt(doctorId.substring(1));
                        maxId = Math.max(maxId, id);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            nextId.set(maxId + 1);
        } catch (Exception e) {
            System.err.println("Warning: Could not initialize ID counter: " + e.getMessage());
            nextId.set(1);
        }
    }

    public Optional<Doctor> findById(String id) {
        Document query = new Document("doctorId", id);
        Document doc = doctorCollection.find(query).first();
        return doc != null ? Optional.of(documentToDoctor(doc)) : Optional.empty();
    }

    public Optional<Doctor> findByEmail(String email) {
        Document query = new Document("email", email);
        Document doc = doctorCollection.find(query).first();
        return doc != null ? Optional.of(documentToDoctor(doc)) : Optional.empty();
    }

    public Optional<Doctor> findByLicenseNumber(String licenseNumber) {
        Document query = new Document("licenseNumber", licenseNumber);
        Document doc = doctorCollection.find(query).first();
        return doc != null ? Optional.of(documentToDoctor(doc)) : Optional.empty();
    }

    public List<Doctor> findAll() {
        List<Doctor> doctors = new ArrayList<>();
        FindIterable<Document> docs = doctorCollection.find();
        for (Document doc : docs) {
            doctors.add(documentToDoctor(doc));
        }
        return doctors;
    }

    public List<Doctor> findBySpecialization(String specialization) {
        List<Doctor> doctors = new ArrayList<>();
        Document query = new Document("specialization",
                new Document("$regex", specialization).append("$options", "i"));
        FindIterable<Document> docs = doctorCollection.find(query);
        for (Document doc : docs) {
            doctors.add(documentToDoctor(doc));
        }
        return doctors;
    }

    public List<Doctor> findByName(String name) {
        List<Doctor> doctors = new ArrayList<>();
        Document query = new Document("$or", Arrays.asList(
                new Document("firstName", new Document("$regex", name).append("$options", "i")),
                new Document("lastName", new Document("$regex", name).append("$options", "i"))
        ));
        FindIterable<Document> docs = doctorCollection.find(query);
        for (Document doc : docs) {
            doctors.add(documentToDoctor(doc));
        }
        return doctors;
    }

    public boolean existsById(String id) {
        Document query = new Document("doctorId", id);
        return doctorCollection.find(query).first() != null;
    }

    public void deleteById(String id) {
        Document query = new Document("doctorId", id);
        doctorCollection.deleteOne(query);
        System.out.println("🗑️ Deleted doctor: " + id);
    }

    public Doctor save(Doctor doctor) {
        if (doctor.getDoctorId() == null || doctor.getDoctorId().isEmpty()) {
            doctor.setDoctorId(generateId());
        }

        Document doctorDoc = doctorToDocument(doctor);

        Document query = new Document("doctorId", doctor.getDoctorId());
        Document existing = doctorCollection.find(query).first();

        if (existing != null) {
            doctorCollection.replaceOne(query, doctorDoc);
            System.out.println("✅ Updated doctor: " + doctor.getDoctorId());
        } else {
            doctorCollection.insertOne(doctorDoc);
            System.out.println("✅ Inserted doctor: " + doctor.getDoctorId());
        }

        return doctor;
    }

    private String generateId() {
        return "D" + String.format("%05d", nextId.getAndIncrement());
    }

    public int count() {
        return (int) doctorCollection.countDocuments();
    }

    // Helper methods

    private Document doctorToDocument(Doctor doctor) {
        Document doc = new Document()
                .append("doctorId", doctor.getDoctorId())
                .append("firstName", doctor.getFirstName())
                .append("lastName", doctor.getLastName())
                .append("specialization", doctor.getSpecialization())
                .append("email", doctor.getEmail())
                .append("phoneNumber", doctor.getPhoneNumber())
                .append("licenseNumber", doctor.getLicenseNumber())
                .append("consultationFee", doctor.getConsultationFee())
                .append("qualification", doctor.getQualification())
                .append("experienceYears", doctor.getExperienceYears())
                .append("availableTimeSlots", new ArrayList<>(doctor.getAvailableTimeSlots()));

        return doc;
    }

    @SuppressWarnings("unchecked")
    private Doctor documentToDoctor(Document doc) {
        Doctor doctor = new Doctor();
        doctor.setDoctorId(doc.getString("doctorId"));
        doctor.setFirstName(doc.getString("firstName"));
        doctor.setLastName(doc.getString("lastName"));
        doctor.setSpecialization(doc.getString("specialization"));
        doctor.setEmail(doc.getString("email"));
        doctor.setPhoneNumber(doc.getString("phoneNumber"));
        doctor.setLicenseNumber(doc.getString("licenseNumber"));
        doctor.setConsultationFee(doc.getDouble("consultationFee") != null ? doc.getDouble("consultationFee") : 0.0);
        doctor.setQualification(doc.getString("qualification"));
        doctor.setExperienceYears(doc.getInteger("experienceYears") != null ? doc.getInteger("experienceYears") : 0);

        List<String> timeSlots = (List<String>) doc.get("availableTimeSlots");
        if (timeSlots != null) {
            for (String slot : timeSlots) {
                doctor.addTimeSlot(slot);
            }
        }

        return doctor;
    }
}
