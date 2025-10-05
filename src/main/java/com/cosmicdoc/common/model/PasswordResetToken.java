package com.cosmicdoc.common.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken implements PersistableEntity {
    @DocumentId
    private String token;
    private String identityId;
    private String email;
    private Timestamp expiresAt;
    private Timestamp createdAt;

    @Override
    public String getId() {
        return token;
    }

    @Override
    public void setId(String token) {
    this.token =token;
    }
}
