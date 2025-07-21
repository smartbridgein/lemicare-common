package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.OrganizationMember;
import com.cosmicdoc.common.repository.OrganizationMemberRepository;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.WriteBatch;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class OrganizationMemberRepositoryImpl extends BaseRepositoryImpl<OrganizationMember,String> implements OrganizationMemberRepository  {

    public OrganizationMemberRepositoryImpl(Firestore firestore) {
        super();
        this.firestore = firestore;
    }
    @Override
    protected CollectionReference getCollection() {
        return firestore.collection("organization_members");
    }


    @Override
    public void saveInTransaction(WriteBatch batch, OrganizationMember membership) {
        // --- This is the key logic ---

        // 1. Validate that the necessary IDs are present in the object.
        if (membership.getUserId() == null || membership.getOrganizationId() == null) {
            throw new IllegalArgumentException("UserId and OrganizationId cannot be null in OrganizationMember");
        }

        // 2. Construct the composite Document ID from the object's fields.
        String documentId = membership.getUserId() + "_" + membership.getOrganizationId();

        // 3. Get the DocumentReference using the constructed ID.
        var docRef = getCollection().document(documentId);

        // 4. Add the 'set' operation to the batch.
        batch.set(docRef, membership);
    }

    /**
     * Implementation for finding a membership by its composite key (userId and orgId).
     */
    @Override
    public Optional<OrganizationMember> findByUserIdAndOrgId(String userId, String organizationId) {
        try {
            // 1. Construct the exact Document ID from the provided parts.
            // This is the most efficient way to get a single document.
            String documentId = userId + "_" + organizationId;

            // 2. Perform a direct lookup using the document ID.
            var documentSnapshot = getCollection().document(documentId).get().get();

            // 3. Check if the document actually exists.
            if (documentSnapshot.exists()) {
                // If it exists, convert it to an object and wrap in an Optional.
                // Using ofNullable is safer in case the document is empty or malformed.
                return Optional.ofNullable(documentSnapshot.toObject(OrganizationMember.class));
            } else {
                // If the document does not exist, return an empty Optional.
                return Optional.empty();
            }
        } catch (InterruptedException | ExecutionException e) {
            // In a production app, log this exception for debugging.
            throw new RuntimeException("Error finding membership for user " + userId + " in org " + organizationId, e);
        }
    }

    /**
     * Implementation for finding all memberships by organization ID.
     */
    @Override
    public List<OrganizationMember> findAllByOrganizationId(String organizationId) {
        try {
            // 1. Build the query to find all documents where the 'organizationId' field
            //    matches the provided ID.
            List<QueryDocumentSnapshot> documents = getCollection()
                    .whereEqualTo("organizationId", organizationId)
                    .get()
                    .get()
                    .getDocuments();

            // 2. Use a Java Stream to map each document snapshot to an OrganizationMember object.
            return documents.stream()
                    .map(doc -> doc.toObject(OrganizationMember.class))
                    .collect(Collectors.toList());

        } catch (InterruptedException | ExecutionException e) {
            // In a production app, log this error for debugging purposes.
            throw new RuntimeException("Error finding all members for organization: " + organizationId, e);
        }
    }

}
