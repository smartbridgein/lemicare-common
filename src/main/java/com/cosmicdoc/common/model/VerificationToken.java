package com.cosmicdoc.common.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationToken {
    @DocumentId
    private String token; // The document ID is the token itself
    private String userId;
    private String email;
    private Timestamp expiresAt;
}

