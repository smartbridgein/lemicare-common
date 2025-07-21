package com.cosmicdoc.common.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PatientRecord {
    private String id;
    private String patientId;
    private String doctorId;
    private LocalDateTime visitDate;
    private String symptoms;
    private String diagnosis;
    private String prescription;
    private List<String> testResults;
    private String notes;
    private String followUpDate;
    private boolean isActive;
    private String treatmentPlan;
}
