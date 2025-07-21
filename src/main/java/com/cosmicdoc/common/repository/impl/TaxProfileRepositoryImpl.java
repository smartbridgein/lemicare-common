package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.TaxProfile;
import com.cosmicdoc.common.repository.TaxProfileRepository;
import com.google.cloud.firestore.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Firestore implementation of the TaxProfileRepository.
 * Manages TaxProfile documents which are stored as a sub-collection
 * under a specific organization.
 */
@Repository

public class TaxProfileRepositoryImpl implements TaxProfileRepository {

    private final Firestore firestore;

    public TaxProfileRepositoryImpl (Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * A private helper method to get a reference to the 'tax_profiles' sub-collection
     * for a specific organization. This ensures all methods use the correct,
     * consistent path.
     */
    private CollectionReference getCollection(String organizationId) {
        return firestore.collection("organizations").document(organizationId).collection("tax_profiles");
    }

    /**
     * Finds all tax profiles for a specific organization.
     * This is useful for populating tax configuration UIs or dropdowns.
     */
    @Override
    public List<TaxProfile> findAllByOrganizationId(String organizationId) {
        try {
            // Get all documents from the specific organization's 'tax_profiles' collection
            var documents = getCollection(organizationId).get().get().getDocuments();

            // Use a modern Java Stream to map the Firestore documents to your TaxProfile model class
            return documents.stream()
                    .map(doc -> doc.toObject(TaxProfile.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            // In a production application, this error should be logged properly
            throw new RuntimeException("Error finding all tax profiles for organization: " + organizationId, e);
        }
    }


    @Override
    public TaxProfile save(String organizationId, TaxProfile taxProfile) {
        try {
            getCollection(organizationId).document(taxProfile.getTaxProfileId()).set(taxProfile).get();
            return taxProfile;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error saving tax profile: " + taxProfile.getProfileName(), e);
        }
    }

    /**
     * Finds a specific tax profile by its ID.
     * This performs a direct and efficient document lookup.
     */
    @Override
    public Optional<TaxProfile> findById(String organizationId, String taxProfileId) {
        try {
            // 1. Get a direct reference to the document using the full path context.
            var documentSnapshot = getCollection(organizationId).document(taxProfileId).get().get();

            // 2. Check if the document actually exists in the database.
            if (documentSnapshot.exists()) {
                // 3. If it exists, convert it to a TaxProfile object and wrap in an Optional.
                //    Using Optional.ofNullable is safer against malformed data.
                return Optional.ofNullable(documentSnapshot.toObject(TaxProfile.class));
            } else {
                // 4. If not found, return an empty Optional.
                return Optional.empty();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error finding tax profile with ID: " + taxProfileId, e);
        }
    }

    @Override
    public List<DocumentSnapshot> getAll(Transaction transaction, String orgId, List<String> taxProfileIds) throws ExecutionException, InterruptedException {
        List<DocumentReference> docRefs = taxProfileIds.stream()
                .map(id -> getCollection(orgId).document(id))
                .collect(Collectors.toList());
        return transaction.getAll(docRefs.toArray(new DocumentReference[0])).get();
    }

    @Override
    public Optional<TaxProfile> findById(Transaction transaction, String orgId, String taxProfileId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getCollection(orgId).document(taxProfileId);
        DocumentSnapshot snapshot = transaction.get(docRef).get();
        return snapshot.exists() ? Optional.ofNullable(snapshot.toObject(TaxProfile.class)) : Optional.empty();
    }

    @Override
    public Optional<TaxProfile> findByProfileNameIgnoreCaseExcludingId(String organizationId, String profileName, String excludeTaxProfileId) {
        // We use a normalized field for case-insensitive search.
        String normalizedName = profileName.toLowerCase().trim();

        try {
            Query query = getCollection(organizationId)
                    .whereEqualTo("normalizedProfileName", normalizedName) // Assuming you add this field to your model
                    .limit(1);

            List<QueryDocumentSnapshot> documents = query.get().get().getDocuments();
            if (documents.isEmpty()) {
                return Optional.empty();
            }

            TaxProfile foundProfile = documents.get(0).toObject(TaxProfile.class);
            // If the found profile is the same one we're updating, it's not a conflict.
            if (foundProfile.getTaxProfileId().equals(excludeTaxProfileId)) {
                return Optional.empty();
            }

            return Optional.of(foundProfile);

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error checking for tax profile by name", e);
        }
    }

    @Override
    public void deleteById(String organizationId, String taxProfileId) {
        try {
            // Get a direct reference to the document and call the delete() method.
            getCollection(organizationId).document(taxProfileId).delete().get();
        } catch (InterruptedException | ExecutionException e) {
            // In a production app, log this exception.
            throw new RuntimeException("Error deleting tax profile with ID: " + taxProfileId, e);
        }
    }

}
