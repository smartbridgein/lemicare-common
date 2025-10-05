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
public class Organization implements PersistableEntity {
   @DocumentId
    private String orgId;
    private String name;
    private String normalizedName;
    private String status;
    private boolean hasMultipleBranches;
    private Timestamp createdAt;

 @Override
 public String getId() {
  return orgId;
 }

 @Override
 public void setId(String id) {
  this.orgId = orgId;
 }
}
