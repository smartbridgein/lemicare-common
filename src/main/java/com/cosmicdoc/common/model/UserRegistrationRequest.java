package com.cosmicdoc.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserRegistrationRequest {
    private String email;
    private String phone;
    private String name;
    @JsonProperty("password_hash")
    private String passwordHash;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
