package com.cosmicdoc.common.model;

public enum VIPStatus {
    ACTIVE,          // Currently active in the VIP program
    PENDING_UPGRADE, // Qualified for next tier, awaiting upgrade process
    PENDING_DOWNGRADE, // Close to losing current tier, or just downgraded
    EXPIRED,         // VIP membership has expired
    FROZEN,          // Membership temporarily suspended
    DEACTIVATED      // Permanently removed from VIP program
}
