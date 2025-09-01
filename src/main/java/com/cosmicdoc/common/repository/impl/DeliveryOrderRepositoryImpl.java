package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.DeliveryOrder;
import com.cosmicdoc.common.model.DeliveryStatus;
import com.cosmicdoc.common.repository.DeliveryOrderRepository;
import com.google.cloud.firestore.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


@Slf4j
@Repository
public class DeliveryOrderRepositoryImpl implements DeliveryOrderRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "delivery_orders";

   public DeliveryOrderRepositoryImpl (Firestore firestore) {
       this.firestore = firestore;
   }
    // Helper to get the correctly nested collection based on your multi-tenant structure
    private CollectionReference getCollection(String organizationId, String branchId) {
        return firestore.collection("organizations").document(organizationId)
                .collection("branches").document(branchId)
                .collection(COLLECTION_NAME);
    }

    @Override
    public DeliveryOrder save(DeliveryOrder deliveryOrder) {
        if (deliveryOrder.getId() == null || deliveryOrder.getId().isBlank()) {
            throw new IllegalArgumentException("DeliveryOrder ID for saving cannot be null or empty.");
        }
        try {
            getCollection(deliveryOrder.getOrganizationId(), deliveryOrder.getBranchId())
                    .document(deliveryOrder.getId())
                    .set(deliveryOrder).get(); // .get() waits for the operation to complete
            return deliveryOrder;
        } catch (Exception e) {
            log.error("Failed to save delivery order with ID: {}", deliveryOrder.getId(), e);
            throw new RuntimeException("Error during Firestore save", e);
        }
    }

    @Override
    public Optional<DeliveryOrder> findByOrderIdAndOrganizationIdAndBranchId(String orderId, String organizationId, String branchId) {
        try {
            Query query = getCollection(organizationId, branchId)
                    .whereEqualTo("orderId", orderId)
                    .limit(1);

            QuerySnapshot querySnapshot = query.get().get();
            if (querySnapshot.isEmpty()) {
                return Optional.empty();
            }
            return Optional.ofNullable(querySnapshot.getDocuments().get(0).toObject(DeliveryOrder.class));
        } catch (Exception e) {
            log.error("Error finding by orderId '{}' in org '{}'", orderId, organizationId, e);
            throw new RuntimeException("Error during Firestore query", e);
        }
    }

    // This method is required by your WebhookService
    @Override
    public Optional<DeliveryOrder> findByPartnerTrackingId(String partnerTrackingId) {
        // This must be a Collection Group query because the webhook doesn't know the orgId.
        try {
            Query query = firestore.collectionGroup(COLLECTION_NAME)
                    .whereEqualTo("partnerTrackingId", partnerTrackingId)
                    .limit(1);

            var documents = query.get().get().getDocuments();
            if (documents.isEmpty()) {
                return Optional.empty();
            }
            return Optional.ofNullable(documents.get(0).toObject(DeliveryOrder.class));
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error finding delivery order by partner tracking ID: " + partnerTrackingId, e);
        }
    }

    @Override
    public List<DeliveryOrder> findByOrganizationIdAndBranchIdAndStatus(String organizationId, String branchId, DeliveryStatus status) {
        try {
            Query query = getCollection(organizationId, branchId)
                    .whereEqualTo("status", status.name()); // Enums are stored as Strings

            QuerySnapshot querySnapshot = query.get().get();
            if (querySnapshot.isEmpty()) {
                return Collections.emptyList();
            }
            return querySnapshot.getDocuments().stream()
                    .map(doc -> doc.toObject(DeliveryOrder.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding by status '{}' in org '{}'/branch '{}'", status, organizationId, branchId, e);
            throw new RuntimeException("Error during Firestore query", e);
        }
    }

    @Override
    public List<DeliveryOrder> findByOrganizationIdAndStatus(String organizationId, DeliveryStatus status) {
        // This also requires a Collection Group query to search across all branches of an organization.
        try {
            Query query = firestore.collectionGroup(COLLECTION_NAME)
                    .whereEqualTo("organizationId", organizationId)
                    .whereEqualTo("status", status.name());

            QuerySnapshot querySnapshot = query.get().get();
            if (querySnapshot.isEmpty()) {
                return Collections.emptyList();
            }
            return querySnapshot.getDocuments().stream()
                    .map(doc -> doc.toObject(DeliveryOrder.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding by status '{}' across organization '{}'", status, organizationId, e);
            throw new RuntimeException("Error during Firestore collection group query", e);
        }
    }

    @Override
    public List<DeliveryOrder> findByOrganizationIdAndBranchId(String organizationId, String branchId) {
        log.info("Fetching all delivery orders for organization '{}' and branch '{}'", organizationId, branchId);
        try {
            // 1. Get a reference to the specific, nested collection for the given branch.
            CollectionReference collectionRef = getCollection(organizationId, branchId);

            // 2. Asynchronously retrieve all documents in the collection.
            // The first .get() returns an ApiFuture, the second .get() waits for it to complete.
            QuerySnapshot querySnapshot = collectionRef.get().get();

            // 3. Handle the common case where the collection is empty.
            if (querySnapshot.isEmpty()) {
                return Collections.emptyList();
            }

            // 4. If documents exist, use a Java Stream to map each document to a DeliveryOrder object.
            return querySnapshot.getDocuments().stream()
                    .map(documentSnapshot -> documentSnapshot.toObject(DeliveryOrder.class))
                    .collect(Collectors.toList());

        } catch (InterruptedException | ExecutionException e) {
            // 5. Catch any low-level Firestore errors.
            log.error("Error fetching all delivery orders for organization '{}' and branch '{}'",
                    organizationId, branchId, e);

            // 6. Wrap in a RuntimeException to signal a failure in the data access layer.
            throw new RuntimeException("Failed to fetch delivery orders from Firestore", e);
        }
    }

    @Override
    public Optional<DeliveryOrder> findById(String organizationId, String branchId, String deliveryId) {
        // 1. Construct the full, specific path to the document.
        // This is the core of the logic for a nested data model.
        try {
            DocumentReference docRef = getCollection(organizationId, branchId)
                    .document(deliveryId);

            // 2. Asynchronously fetch the document snapshot from Firestore.
            // The first .get() returns an ApiFuture, the second .get() waits for it to complete.
            DocumentSnapshot documentSnapshot = docRef.get().get();

            // 3. Check if the document actually exists in the database.
            if (documentSnapshot.exists()) {
                // 4. If it exists, map the document data to our DeliveryOrder Java object.
                // Wrap the result in an Optional to signify success.
                DeliveryOrder deliveryOrder = documentSnapshot.toObject(DeliveryOrder.class);
                return Optional.ofNullable(deliveryOrder);
            } else {
                // 5. If no document was found at that path, return an empty Optional.
                // This is the expected behavior for a "find by ID" method.
                return Optional.empty();
            }
        } catch (InterruptedException | ExecutionException e) {
            // This block catches low-level errors with the Firestore connection or task execution.
            // It's crucial to log this for debugging production issues.
            log.error("Error retrieving document with ID '{}' from branch '{}' in org '{}'",
                    deliveryId, branchId, organizationId, e);

            // We re-throw as a RuntimeException to signal a failure in the data access layer.
            throw new RuntimeException("Error fetching document from Firestore", e);
        }
    }
}