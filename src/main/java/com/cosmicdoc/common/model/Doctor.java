package com.cosmicdoc.common.model;

import lombok.Data;

import java.util.List;

@Data
public class Doctor {
    public static class Schedule {
        private List<String> days;
        private List<String> timeSlots;
        
        public List<String> getDays() { return days; }
        public void setDays(List<String> days) { this.days = days; }
        
        public List<String> getTimeSlots() { return timeSlots; }
        public void setTimeSlots(List<String> timeSlots) { this.timeSlots = timeSlots; }
    }
    private String id;
    private String name;
    private String email;
    private String password;
    private String specialization;
    private String qualification;
    private String experience;
    private String phoneNumber;
    private String address;
    private List<String> availableDays;
    private List<String> availableTimeSlots;
    private boolean isActive;
    private Schedule schedule;
}
