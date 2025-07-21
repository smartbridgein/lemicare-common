package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.Appointment;
import com.cosmicdoc.common.repository.AppointmentRepository;
import com.cosmicdoc.common.util.JsonDataLoader;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class AppointmentRepositoryImpl implements AppointmentRepository {
    private final JsonDataLoader jsonDataLoader;
    private final Map<String, Appointment> appointmentMap = new ConcurrentHashMap<>();

    public AppointmentRepositoryImpl(JsonDataLoader jsonDataLoader) {
        this.jsonDataLoader = jsonDataLoader;
        loadAppointments();
    }

    private void loadAppointments() {
        Map<String, List<Appointment>> data = jsonDataLoader.loadDataAsMap("appointments.json", Appointment.class);
        List<Appointment> appointments = data.getOrDefault("appointments", new ArrayList<>());
        appointments.stream()
            .filter(appointment -> appointment != null && appointment.getAppointmentId() != null)
            .forEach(appointment -> appointmentMap.put(appointment.getAppointmentId(), appointment));
    }

    @Override
    public List<Appointment> findAll() {
        return new ArrayList<>(appointmentMap.values());
    }

    @Override
    public Optional<Appointment> findById(String id) {
        return Optional.ofNullable(appointmentMap.get(id));
    }

    @Override
    public Appointment save(Appointment appointment) {
        appointmentMap.put(appointment.getAppointmentId(), appointment);
        return appointment;
    }

    @Override
    public List<Appointment> findByPatientId(String patientId) {
        return appointmentMap.values().stream()
                .filter(appointment -> appointment.getUserId().equals(patientId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Appointment> findByDoctorIdAndDate(String doctorId, LocalDate date) {
        return appointmentMap.values().stream()
                .filter(appointment -> appointment.getDoctorId().equals(doctorId) &&
                        appointment.getDateAsLocalDateTime().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    @Override
    public List<Appointment> findByDoctorIdAndMonth(String doctorId, YearMonth month) {
        return appointmentMap.values().stream()
                .filter(appointment -> appointment.getDoctorId().equals(doctorId) &&
                        YearMonth.from(appointment.getDateAsLocalDateTime()).equals(month))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        appointmentMap.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return appointmentMap.containsKey(id);
    }

    @Override
    public List<Appointment> findByUserId(String userId) {
        return appointmentMap.values().stream()
                .filter(appointment -> appointment.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Appointment> findByDoctorId(String doctorId) {
        return appointmentMap.values().stream()
                .filter(appointment -> appointment.getDoctorId().equals(doctorId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Appointment> findByStatus(String status) {
        return appointmentMap.values().stream()
                .filter(appointment -> appointment.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    @Override
    public List<Appointment> findByAppointmentDateBetween(LocalDateTime start, LocalDateTime end) {
        return appointmentMap.values().stream()
                .filter(appointment -> {
                    LocalDateTime appointmentDate = appointment.getAppointmentDateAsLocalDateTime();
                    return !appointmentDate.isBefore(start) && !appointmentDate.isAfter(end);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Appointment> findByUserIdAndStatus(String userId, String status) {
        return appointmentMap.values().stream()
                .filter(appointment -> appointment.getUserId().equals(userId) 
                        && appointment.getStatus().equals(status))
                .collect(Collectors.toList());
    }
}
