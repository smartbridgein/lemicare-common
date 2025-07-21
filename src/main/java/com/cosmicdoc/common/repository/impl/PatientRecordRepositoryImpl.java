package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.PatientRecord;
import com.cosmicdoc.common.repository.PatientRecordRepository;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.ArrayList;

@Repository
public class PatientRecordRepositoryImpl extends BaseRepositoryImpl<PatientRecord, String> implements PatientRecordRepository {
    
    public PatientRecordRepositoryImpl(Firestore firestore) {
        super();
        this.firestore = firestore;
    }

    @Override
    protected com.google.cloud.firestore.CollectionReference getCollection() {
        return firestore.collection("patient_records");
    }

    @Override
    public List<PatientRecord> findByPatientId(String patientId) {
        try {
            List<PatientRecord> records = new ArrayList<>();
            var documents = getCollection().whereEqualTo("patientId", patientId).get().get().getDocuments();
            for (var document : documents) {
                records.add(document.toObject(PatientRecord.class));
            }
            return records;
        } catch (Exception e) {
            throw new RuntimeException("Error finding patient records by patientId", e);
        }
    }

    @Override
    public List<PatientRecord> findByDoctorId(String doctorId) {
        try {
            List<PatientRecord> records = new ArrayList<>();
            var documents = getCollection().whereEqualTo("doctorId", doctorId).get().get().getDocuments();
            for (var document : documents) {
                records.add(document.toObject(PatientRecord.class));
            }
            return records;
        } catch (Exception e) {
            throw new RuntimeException("Error finding patient records by doctorId", e);
        }
    }
}
