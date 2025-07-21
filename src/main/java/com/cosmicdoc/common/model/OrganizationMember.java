package com.cosmicdoc.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationMember {
    private String userId;
    private String organizationId;
    private String role;
    private String accessType;
    private List<Permission> permissions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Permission {
        private String branchId;
        private String role;
    }
}

