package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.PatientRecord;

import java.util.List;

public interface PatientRecordRepository extends BaseRepository<PatientRecord, String> {
    List<PatientRecord> findByPatientId(String patientId);
    List<PatientRecord> findByDoctorId(String doctorId);
}
