package com.cosmicdoc.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.ServerTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Appointment {
    @JsonProperty("appointmentId")
    private String appointmentId;
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("doctorId")
    private String doctorId;
    @JsonProperty("appointment_date")
    private Timestamp appointmentDate;
    @JsonProperty("status")
    private String status;
    @JsonProperty("category")
    private String category;
    @JsonProperty("sub_category")
    private String subCategory;
    @JsonProperty("patient_gps_location")
    private GpsLocation patientGpsLocation;
    @JsonProperty("doctor_gps_location")
    private GpsLocation doctorGpsLocation;
    @JsonProperty("created_at")
    @ServerTimestamp
    private Timestamp createdAt;
    @JsonProperty("updated_at")
    @ServerTimestamp
    private Timestamp updatedAt;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("date")
    private Timestamp date;

    public static class GpsLocation {
        @JsonProperty("latitude")
        private double latitude;
        @JsonProperty("longitude")
        private double longitude;

        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }

        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
    }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    
    public Timestamp getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Timestamp appointmentDate) { this.appointmentDate = appointmentDate; }
    
    public LocalDateTime getAppointmentDateAsLocalDateTime() {
        return appointmentDate != null ? 
            LocalDateTime.ofInstant(Instant.ofEpochSecond(appointmentDate.getSeconds(), appointmentDate.getNanos()),
                ZoneId.systemDefault()) : null;
    }
    
    public void setAppointmentDateFromLocalDateTime(LocalDateTime dateTime) {
        if (dateTime != null) {
            this.appointmentDate = Timestamp.ofTimeSecondsAndNanos(
                dateTime.atZone(ZoneId.systemDefault()).toEpochSecond(),
                dateTime.getNano());
        } else {
            this.appointmentDate = null;
        }
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getSubCategory() { return subCategory; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }
    
    public GpsLocation getPatientGpsLocation() { return patientGpsLocation; }
    public void setPatientGpsLocation(GpsLocation patientGpsLocation) { this.patientGpsLocation = patientGpsLocation; }
    
    public GpsLocation getDoctorGpsLocation() { return doctorGpsLocation; }
    public void setDoctorGpsLocation(GpsLocation doctorGpsLocation) { this.doctorGpsLocation = doctorGpsLocation; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getCreatedAtAsLocalDateTime() {
        return createdAt != null ? 
            LocalDateTime.ofInstant(Instant.ofEpochSecond(createdAt.getSeconds(), createdAt.getNanos()),
                ZoneId.systemDefault()) : null;
    }
    
    public void setCreatedAtFromLocalDateTime(LocalDateTime dateTime) {
        if (dateTime != null) {
            this.createdAt = Timestamp.ofTimeSecondsAndNanos(
                dateTime.atZone(ZoneId.systemDefault()).toEpochSecond(),
                dateTime.getNano());
        } else {
            this.createdAt = null;
        }
    }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getUpdatedAtAsLocalDateTime() {
        return updatedAt != null ? 
            LocalDateTime.ofInstant(Instant.ofEpochSecond(updatedAt.getSeconds(), updatedAt.getNanos()),
                ZoneId.systemDefault()) : null;
    }
    
    public void setUpdatedAtFromLocalDateTime(LocalDateTime dateTime) {
        if (dateTime != null) {
            this.updatedAt = Timestamp.ofTimeSecondsAndNanos(
                dateTime.atZone(ZoneId.systemDefault()).toEpochSecond(),
                dateTime.getNano());
        } else {
            this.updatedAt = null;
        }
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Timestamp getDate() { return date; }
    public void setDate(Timestamp date) { this.date = date; }
    
    public LocalDateTime getDateAsLocalDateTime() {
        return date != null ? 
            LocalDateTime.ofInstant(Instant.ofEpochSecond(date.getSeconds(), date.getNanos()),
                ZoneId.systemDefault()) : null;
    }
    
    public void setDateFromLocalDateTime(LocalDateTime dateTime) {
        if (dateTime != null) {
            this.date = Timestamp.ofTimeSecondsAndNanos(
                dateTime.atZone(ZoneId.systemDefault()).toEpochSecond(), 
                dateTime.getNano());
        } else {
            this.date = null;
        }
    }
}
