package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.Salesperson;
import com.cosmicdoc.common.model.StorefrontOrder;
import com.cosmicdoc.common.model.StorefrontProduct;
import com.cosmicdoc.common.repository.SalespersonRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class SalespersonRepositoryImpl extends AbstractTransactionalRepositoryImpl<Salesperson, String> implements SalespersonRepository {

    public SalespersonRepositoryImpl(Firestore firestore) {
        super(firestore);
        this.firestore = firestore;
    }

    @Override
    protected CollectionReference getCollection() {
        return firestore.collection("salespersons");
    }

    private CollectionReference getCollection(String organizationId, String branchId) {
        return firestore.collection("organizations").document(organizationId)
                .collection("branches").document(branchId)
                .collection("salespersons");
    }

    // OVERRIDE the default save logic from BaseRepositoryImpl to enforce ID presence
    @Override
    public Salesperson save(Salesperson salesperson) {
        try {
            // CRITICAL: Ensure the ID is provided by the caller (e.g., a service that created a Users profile)
            if (salesperson.getId() == null || salesperson.getId().isBlank()) {
                throw new IllegalArgumentException("Salesperson ID must be provided and correspond to a Users.userId.");
            }

            getCollection().document(salesperson.getId()).set(salesperson).get();
            log.debug("Saved/Updated salesperson: {}", salesperson.getId());
            return salesperson;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error saving salesperson {}: {}", salesperson.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to save salesperson", e);
        }
    }


    // All find methods remain largely the same, as they rely on `id` already being set

    @Override
    public Optional<Salesperson> findById(String orgId,String branchId,String id) {
        try {

            log.debug("Attempting to find salesperson by ID: {}", id);
            var doc = getCollection(orgId, branchId).document(id).get().get();
            return doc.exists() ? Optional.ofNullable(doc.toObject(Salesperson.class)) : Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Firestore operation failed while finding salesperson by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve salesperson by ID from Firestore.", e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while finding salesperson by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("An error occurred during salesperson data retrieval.", e);
        }
    }

    @Override
    public List<Salesperson> findByStoreCode(String orgId,String branchId,String storeCode) {
        try {
            log.debug("Attempting to find salespersons by store code: {}", storeCode);
            return getCollection(orgId, branchId).whereEqualTo("storeCode", storeCode)
                    .get().get().getDocuments().stream()
                    .map(doc -> doc.toObject(Salesperson.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            log.error("Firestore operation failed while finding salespersons by store code {}: {}", storeCode, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve salespersons by store code from Firestore.", e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while finding salespersons by store code {}: {}", storeCode, e.getMessage(), e);
            throw new RuntimeException("An error occurred during salesperson data retrieval.", e);
        }
    }

    @Override
    public List<Salesperson> findByAreaCode(String orgId,String branchId,String areaCode) {
        try {
            log.debug("Attempting to find salespersons by area code: {}", areaCode);
            return getCollection(orgId, branchId).whereEqualTo("areaCode", areaCode)
                    .get().get().getDocuments().stream()
                    .map(doc -> doc.toObject(Salesperson.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            log.error("Firestore operation failed while finding salespersons by area code {}: {}", areaCode, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve salespersons by area code from Firestore.", e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while finding salespersons by area code {}: {}", areaCode, e.getMessage(), e);
            throw new RuntimeException("An error occurred during salesperson data retrieval.", e);
        }
    }

    @Override
    public List<Salesperson> findByRegion(String orgId,String branchId,String region) {
        try {
            log.debug("Attempting to find salespersons by region: {}", region);
            return getCollection(orgId, branchId).whereEqualTo("region", region)
                    .get().get().getDocuments().stream()
                    .map(doc -> doc.toObject(Salesperson.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            log.error("Firestore operation failed while finding salespersons by region {}: {}", region, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve salespersons by region from Firestore.", e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while finding salespersons by region {}: {}", region, e.getMessage(), e);
            throw new RuntimeException("An error occurred during salesperson data retrieval.", e);
        }
    }

    @Override
    public Optional<Salesperson> findByEmail(String orgId,String branchId,String email) {
        try {
            log.debug("Attempting to find salesperson by email: {}", email);
            return getCollection(orgId, branchId).whereEqualTo("email", email)
                    .limit(1)
                    .get().get().getDocuments().stream()
                    .map(doc -> doc.toObject(Salesperson.class))
                    .findFirst();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Firestore operation failed while finding salesperson by email {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve salesperson by email from Firestore.", e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while finding salesperson by email {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("An error occurred during salesperson data retrieval.", e);
        }
    }

    @Override
    public Optional<Salesperson> findByJobNumber(String orgId,String branchId,String jobNumber) {
        try {
            log.debug("Attempting to find salesperson by job number: {}", jobNumber);
            return getCollection(orgId, branchId).whereEqualTo("jobNumber", jobNumber)
                    .limit(1)
                    .get().get().getDocuments().stream()
                    .map(doc -> doc.toObject(Salesperson.class))
                    .findFirst();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Firestore operation failed while finding salesperson by job number {}: {}", jobNumber, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve salesperson by job number from Firestore.", e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while finding salesperson by job number {}: {}", jobNumber, e.getMessage(), e);
            throw new RuntimeException("An error occurred during salesperson data retrieval.", e);
        }
    }

    @Override
    public List<Salesperson> findByIsStoreManager(String orgId,String branchId,Boolean isStoreManager) {
        try {
            log.debug("Attempting to find salespersons by isStoreManager: {}", isStoreManager);
            return getCollection(orgId, branchId).whereEqualTo("isStoreManager", isStoreManager)
                    .get().get().getDocuments().stream()
                    .map(doc -> doc.toObject(Salesperson.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            log.error("Firestore operation failed while finding salespersons by isStoreManager {}: {}", isStoreManager, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve salespersons by isStoreManager from Firestore.", e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while finding salespersons by isStoreManager {}: {}", isStoreManager, e.getMessage(), e);
            throw new RuntimeException("An error occurred during salesperson data retrieval.", e);
        }
    }

    public List<Salesperson> fetchAll(String orgId,String branchId) {
        try {

            List<QueryDocumentSnapshot> documents = getCollection(orgId,branchId).get().get().getDocuments();
            return documents.stream()
                    .map(doc -> doc.toObject(Salesperson.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException("Failed to retrieve salesperson by ID from Firestore.", e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while finding salesperson by ID {}: {}", e.getMessage(), e);
            throw new RuntimeException("An error occurred during salesperson data retrieval.", e);
        }
    }

    public void deleteById(String organizationId, String branchId,String id) {

        try {

            log.debug("Attempting to delete salesperson by ID: {}", id);
            getCollection(organizationId,branchId).document(id).delete().get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Firestore operation failed while finding salesperson by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete salesperson by ID from Firestore.", e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while finding salesperson by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("An error occurred during salesperson delete.", e);
        }
    }

    public List<Salesperson> search(String orgId,String branchId,String query, String storeCode) throws ExecutionException, InterruptedException {
        // Note: Firestore doesn't support full-text search natively
        // For production, consider using Algolia or Elasticsearch
        List<Salesperson> results = new ArrayList<>();
        List<Salesperson> allSalespeople;

        if (storeCode != null && !storeCode.isEmpty()) {
            allSalespeople = findByStoreCode(orgId,branchId,storeCode);
        } else {
            allSalespeople = fetchAll(orgId,branchId);
        }

        String lowerQuery = query.toLowerCase();
        for (Salesperson sp : allSalespeople) {
            if (sp.getName().toLowerCase().contains(lowerQuery) ||
                    sp.getJobNumber().contains(query) ||
                    (sp.getBriefCode() != null && sp.getBriefCode().contains(query))) {
                results.add(sp);
            }
        }

        log.info("Search returned {} results for query: {}", results.size(), query);
        return results;
    }

    public long countByStoreCode(String orgId, String branchId,String storeCode) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = getCollection(orgId,branchId)
                .whereEqualTo("storeCode", storeCode)
                .get();

        int count = future.get().size();
        log.info("Store {} has {} salespeople", storeCode, count);
        return count;
    }

}

