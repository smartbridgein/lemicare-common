package com.cosmicdoc.common.model;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxProfile {
    @DocumentId
    private String taxProfileId; // e.g., "gst_18", "gst_exempt"
    private String profileName;  // e.g., "GST 18%", "Exempt"
    private double totalRate;
    private String status;
    private List<TaxComponent> components;
}