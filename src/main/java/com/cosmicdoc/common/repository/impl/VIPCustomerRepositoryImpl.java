package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.VIPCustomer;
import com.cosmicdoc.common.repository.VIPCustomerRepository;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
@Slf4j

public class VIPCustomerRepositoryImpl extends AbstractTransactionalRepositoryImpl<VIPCustomer, String> implements VIPCustomerRepository {

    public VIPCustomerRepositoryImpl(Firestore firestore) {
        super(firestore);
        this.firestore = firestore;
    }

    @Override
    protected CollectionReference getCollection() {
        return firestore.collection("vip_customers");
    }

    // OVERRIDE the default save logic from BaseRepositoryImpl to enforce ID presence
    @Override
    public VIPCustomer save(VIPCustomer vipCustomer) {
        try {
            // CRITICAL: Ensure the ID is provided by the caller (e.g., a service that created a Customers profile)
            if (vipCustomer.getId() == null || vipCustomer.getId().isBlank()) {
                throw new IllegalArgumentException("VIPCustomer ID must be provided and correspond to a Customers.customerId.");
            }

            getCollection().document(vipCustomer.getId()).set(vipCustomer).get();
            log.debug("Saved/Updated VIP customer: {}", vipCustomer.getId());
            return vipCustomer;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error saving VIP customer {}: {}", vipCustomer.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to save VIP customer", e);
        }
    }

    // All find methods remain largely the same, as they rely on `id` already being set

    @Override
    public Optional<VIPCustomer> findById(String id) {
        try {
            log.debug("Attempting to find VIP customer by ID: {}", id);
            return Optional.ofNullable(getCollection().document(id).get().get().toObject(VIPCustomer.class));
        } catch (InterruptedException | ExecutionException e) {
            log.error("Firestore operation failed while finding VIP customer by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve VIP customer by ID from Firestore.", e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while finding VIP customer by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("An error occurred during VIP customer data retrieval.", e);
        }
    }

    @Override
    public Optional<VIPCustomer> findByPhoneNumber(String phoneNumber) {
        try {
            log.debug("Attempting to find VIP customer by phone number: {}", phoneNumber);
            return getCollection().whereEqualTo("phoneNumber", phoneNumber)
                    .limit(1)
                    .get().get().getDocuments().stream()
                    .map(doc -> doc.toObject(VIPCustomer.class))
                    .findFirst();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Firestore operation failed while finding VIP customer by phone number {}: {}", phoneNumber, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve VIP customer by phone number from Firestore.", e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while finding VIP customer by phone number {}: {}", phoneNumber, e.getMessage(), e);
            throw new RuntimeException("An error occurred during VIP customer data retrieval.", e);
        }
    }

    @Override
    public Optional<VIPCustomer> findByEmail(String email) {
        try {
            log.debug("Attempting to find VIP customer by email: {}", email);
            return getCollection().whereEqualTo("email", email)
                    .limit(1)
                    .get().get().getDocuments().stream()
                    .map(doc -> doc.toObject(VIPCustomer.class))
                    .findFirst();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Firestore operation failed while finding VIP customer by email {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve VIP customer by email from Firestore.", e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while finding VIP customer by email {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("An error occurred during VIP customer data retrieval.", e);
        }
    }

    @Override
    public List<VIPCustomer> findByVipTier(String vipTier) {
        try {
            log.debug("Attempting to find VIP customers by VIP tier: {}", vipTier);
            return getCollection().whereEqualTo("vipTier", vipTier)
                    .get().get().getDocuments().stream()
                    .map(doc -> doc.toObject(VIPCustomer.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            log.error("Firestore operation failed while finding VIP customers by VIP tier {}: {}", vipTier, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve VIP customers by VIP tier from Firestore.", e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while finding VIP customers by VIP tier {}: {}", vipTier, e.getMessage(), e);
            throw new RuntimeException("An error occurred during VIP customer data retrieval.", e);
        }
    }
}
