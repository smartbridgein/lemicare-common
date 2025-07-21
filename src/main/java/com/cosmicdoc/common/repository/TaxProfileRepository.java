package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.TaxProfile;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface TaxProfileRepository  {
    List<TaxProfile> findAllByOrganizationId(String organizationId);
    TaxProfile save(String organizationId, TaxProfile taxProfile);
    Optional<TaxProfile> findById(String organizationId, String taxProfileId);
    List<DocumentSnapshot> getAll(Transaction transaction, String orgId, List<String> taxProfileIds) throws ExecutionException, InterruptedException;
    Optional<TaxProfile> findById(Transaction transaction, String orgId, String taxProfileId) throws ExecutionException, InterruptedException;
    Optional<TaxProfile> findByProfileNameIgnoreCaseExcludingId(String organizationId, String profileName, String excludeTaxProfileId);
    void deleteById(String organizationId, String taxProfileId);
}
