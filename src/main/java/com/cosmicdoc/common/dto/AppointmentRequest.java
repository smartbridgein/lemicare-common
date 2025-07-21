package com.cosmicdoc.common.dto;

import com.cosmicdoc.common.model.Appointment.GpsLocation;
import java.time.LocalDateTime;

public class AppointmentRequest {
    private String userId;
    private String doctorId;
    private LocalDateTime appointmentDate;
    private String category;
    private String subCategory;
    private GpsLocation patientGpsLocation;
    private GpsLocation doctorGpsLocation;

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    
    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDateTime appointmentDate) { this.appointmentDate = appointmentDate; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getSubCategory() { return subCategory; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }
    
    public GpsLocation getPatientGpsLocation() { return patientGpsLocation; }
    public void setPatientGpsLocation(GpsLocation patientGpsLocation) { this.patientGpsLocation = patientGpsLocation; }
    
    public GpsLocation getDoctorGpsLocation() { return doctorGpsLocation; }
    public void setDoctorGpsLocation(GpsLocation doctorGpsLocation) { this.doctorGpsLocation = doctorGpsLocation; }
}
