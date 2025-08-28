package com.xyz.Data;

import com.xyz.model.Appointment;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;

public class AppointmentRepository {

    private final MongoCollection<Appointment> collection;

    public AppointmentRepository(MongoDatabase database) {
        this.collection = database.getCollection("appointments", Appointment.class);
    }


    public Appointment save(Appointment appointment) {
        if (appointment.getAppointmentId() == null || appointment.getAppointmentId().isEmpty()) {
            appointment.setAppointmentId(generateAppointmentId());
        }
        collection.replaceOne(
                eq("appointmentId", appointment.getAppointmentId()),
                appointment,
                new ReplaceOptions().upsert(true)
        );
        return appointment;
    }
    private String generateAppointmentId() {
        return "A" + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    }
    public Optional<Appointment> findById(String appointmentId) {
        Appointment appointment = collection.find(eq("appointmentId", appointmentId)).first();
        return Optional.ofNullable(appointment);
    }
    public List<Appointment> findAll() {
        return collection.find().into(new ArrayList<>());
    }
    public List<Appointment> findByPatientId(String patientId) {
        return collection.find(eq("patientId", patientId)).into(new ArrayList<>());
    }
    public List<Appointment> findByDoctorId(String doctorId) {
        return collection.find(eq("doctorId", doctorId)).into(new ArrayList<>());
    }
    public List<Appointment> findByStatus(Appointment.AppointmentStatus status) {
        return collection.find(eq("status", status)).into(new ArrayList<>());
    }
    public List<Appointment> findByDate(LocalDate date) {
        return collection.find().into(new ArrayList<>()).stream()
                .filter(a -> a.getAppointmentDateTime() != null && a.getAppointmentDateTime().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }
    public List<Appointment> findByDoctorAndDate(String doctorId, LocalDate date) {
        return collection.find(eq("doctorId", doctorId)).into(new ArrayList<>()).stream()
                .filter(a -> a.getAppointmentDateTime() != null && a.getAppointmentDateTime().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }
    public boolean existsByDoctorAndDateTime(String doctorId, LocalDateTime dateTime) {
        return collection.find(
                        and(
                                eq("doctorId", doctorId),
                                eq("appointmentDateTime", dateTime),
                                ne("status", Appointment.AppointmentStatus.CANCELLED) 
                        )
                )
                .limit(1)
                .first() != null;
    }
    public boolean existsById(String appointmentId) {
        return findById(appointmentId).isPresent();
    }
    public void deleteById(String appointmentId) {
        collection.deleteOne(eq("appointmentId", appointmentId));
    }
    public long count() {
        return collection.countDocuments();
    }
}
