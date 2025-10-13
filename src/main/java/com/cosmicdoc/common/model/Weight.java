package com.cosmicdoc.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; // Use BigDecimal for precise measurements

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Weight {
    private BigDecimal value; // e.g., 0.750
    private String unit;      // e.g., "kg", "g", "lb", "oz". CRITICAL for interpretation.
}
