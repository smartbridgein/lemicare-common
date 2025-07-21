package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.Appointment;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface AppointmentRepository extends BaseRepository<Appointment, String> {
    List<Appointment> findByUserId(String userId);
    List<Appointment> findByDoctorId(String doctorId);
    List<Appointment> findByStatus(String status);
    List<Appointment> findByAppointmentDateBetween(LocalDateTime start, LocalDateTime end);
    List<Appointment> findByUserIdAndStatus(String userId, String status);
    List<Appointment> findByPatientId(String patientId);
    List<Appointment> findByDoctorIdAndDate(String doctorId, LocalDate date);
    List<Appointment> findByDoctorIdAndMonth(String doctorId, YearMonth month);
}
