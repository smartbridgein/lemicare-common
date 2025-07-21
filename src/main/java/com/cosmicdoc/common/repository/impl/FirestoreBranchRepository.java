package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.Branch;
import com.cosmicdoc.common.repository.BranchRepository;
import com.google.cloud.firestore.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Firestore implementation of the BranchRepository interface.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class FirestoreBranchRepository  {

    private static final String BRANCHES_COLLECTION = "branches";
    
    private final Firestore firestore;

   // @Override
    public List<Branch> findAllByOrganizationId(String organizationId) {
        try {
            QuerySnapshot querySnapshot = firestore.collection(BRANCHES_COLLECTION)
                    .whereEqualTo("organizationId", organizationId)
                    .get()
                    .get();
                    
            return querySnapshot.getDocuments().stream()
                    .map(this::convertToBranch)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error fetching branches for organization: {}", organizationId, e);
            return Collections.emptyList();
        }
    }

   // @Override
    public Optional<Branch> findById(String organizationId, String branchId) {
        try {
            DocumentSnapshot doc = firestore.collection(BRANCHES_COLLECTION)
                    .document(branchId)
                    .get()
                    .get();
                    
            if (doc.exists() && organizationId.equals(doc.getString("organizationId"))) {
                return Optional.of(convertToBranch(doc));
            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error fetching branch: {} for organization: {}", branchId, organizationId, e);
            return Optional.empty();
        }
    }

   // @Override
    public Branch save(String organizationId, Branch branch) {
        Map<String, Object> branchData = new HashMap<>();
        branchData.put("branchId", branch.getBranchId());
        branchData.put("organizationId", organizationId);
        branchData.put("name", branch.getName());
        branchData.put("address", branch.getAddress());
        
        try {
            DocumentReference docRef;
            if (branch.getBranchId() != null) {
                // Use the provided ID
                docRef = firestore.collection(BRANCHES_COLLECTION).document(branch.getBranchId());
            } else {
                // Generate a new ID
                docRef = firestore.collection(BRANCHES_COLLECTION).document();
                String branchId = docRef.getId();
                branch.setBranchId(branchId);
                branchData.put("branchId", branchId);
            }
            
            docRef.set(branchData).get();
            
            return branch;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error saving branch for organization: {}", organizationId, e);
            throw new RuntimeException("Failed to save branch", e);
        }
    }
    
    private Branch convertToBranch(DocumentSnapshot doc) {
        return Branch.builder()
                .branchId(doc.getString("branchId"))
                .name(doc.getString("name"))
                .address(doc.getString("address"))
                .build();
    }

  //  @Override
    public List<Branch> findAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

   // @Override
    public Optional<Branch> findById(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

  //  @Override
    public Branch save(Branch entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

   // @Override
    public void deleteById(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }

  //  @Override
    public boolean existsById(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'existsById'");
    }

  //  @Override
    public void saveInTransaction(WriteBatch batch, String organizationId, Branch branch) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveInTransaction'");
    }

  //  @Override
    public Optional<Branch> findFirstByOrganizationId(String organizationId) {
        try {
            // 1. Get a reference to the specific branches sub-collection.
            // 2. Use limit(1) to make the query highly efficient. It tells Firestore
            //    to stop searching as soon as it finds a single document.
            var documents = getBranchesCollection(organizationId)
                    .limit(1)
                    .get()
                    .get()
                    .getDocuments();

            // 3. Check if any document was returned.
            if (!documents.isEmpty()) {
                // If found, convert the first document to a Branch object and wrap in an Optional.
                // Using ofNullable is safer against malformed data.
                return Optional.ofNullable(documents.get(0).toObject(Branch.class));
            }

            // 4. If no branches exist for the organization, return an empty Optional.
            return Optional.empty();

        } catch (InterruptedException | ExecutionException e) {
            // Log the error and wrap in a runtime exception.
            throw new RuntimeException("Error finding first branch for organization: " + organizationId, e);
        }
    }
    private CollectionReference getBranchesCollection(String organizationId) {
        return firestore.collection("organizations").document(organizationId).collection("branches");
    }
}
