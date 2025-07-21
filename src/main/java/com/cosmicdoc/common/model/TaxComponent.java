package com.cosmicdoc.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxComponent {
    private String name; // "CGST", "SGST", "IGST"
    private double rate; // 9.0, 9.0, 18.0
}