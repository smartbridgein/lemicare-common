package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.Identities;
import com.google.cloud.firestore.WriteBatch;

import java.util.Optional;

public interface IdentityRepository extends TransactionalRepository<Identities, String> {
    Optional<Identities> findByEmail(String email);
    Optional<Identities> findByLinkedUserId(String userId);
    Optional<Identities> findByLinkedCustomerId(String customerId);
    void updateLastLogin(String identityId);

 }
