package com.cosmicdoc.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class CursorPayload {
    private int version;
    private Long createdAtSeconds;
    private Integer createdAtNanos;
    private String productId;

}
