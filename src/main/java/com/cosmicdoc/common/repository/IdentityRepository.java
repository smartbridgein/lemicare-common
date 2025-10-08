package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.Identities;
import com.google.cloud.firestore.WriteBatch;

import java.util.Optional;

public interface IdentityRepository extends TransactionalRepository<Identities, String> {
    Optional<Identities> findByEmail(String email);
    Optional<Identities> findByLinkedUserId(String userId);
    Optional<Identities> findByLinkedCustomerId(String customerId);
    void updateLastLogin(String identityId);
    Optional<Identities> findByMobileNumber(String mobileNumber);

    // NEW: Find by either email OR mobile number
    // This is a common pattern for login/signup where either identifier can be used.
    // The implementation will handle the priority (e.g., email first, then mobile).
    Optional<Identities> findByEmailOrMobileNumber(String email, String mobileNumber);
 }
