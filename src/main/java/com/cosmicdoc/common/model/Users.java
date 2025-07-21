package com.cosmicdoc.common.model;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {
    @DocumentId
    private String userId;
    private String email;
    private String hashedPassword;
    private UserStatus status;
    private String displayName;
    private String mobileNumber;
    private List<String> organizations;
    private com.google.cloud.Timestamp lastLoginAt;
    private com.google.cloud.Timestamp createdAt;
    
    /**
     * Added for compatibility with BaseRepositoryImpl.save() which uses reflection to call getId()
     * @return the user ID
     */
    public String getId() {
        return userId;
    }
}
