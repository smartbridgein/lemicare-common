package com.cosmicdoc.common.model;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {
   @DocumentId
    private String orgId;
    private String name;
    private String normalizedName;
    private String status;
    private boolean hasMultipleBranches;
}
