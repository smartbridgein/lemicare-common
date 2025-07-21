package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.Branch;
import com.cosmicdoc.common.repository.BranchRepository;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class BranchRepositoryImpl extends BaseRepositoryImpl<Branch,String> implements BranchRepository {

    public BranchRepositoryImpl(Firestore firestore) {
        super();
        this.firestore = firestore;
    }

       @Override
    protected CollectionReference getCollection() {
           return firestore.collection("branches");
       }
    @Override
    public void saveInTransaction(WriteBatch batch, String organizationId, Branch branch) {
        var docRef = firestore.collection("organizations").document(organizationId)
                .collection("branches").document(branch.getBranchId());
        batch.set(docRef, branch);
    }

    private CollectionReference getBranchesCollection(String organizationId) {
        return firestore.collection("organizations").document(organizationId).collection("branches");
    }

    @Override
    public Branch save(String organizationId, Branch branch) {
        try {
            // This is for standalone saves (create or update).
            getBranchesCollection(organizationId).document(branch.getBranchId()).set(branch).get();
            return branch;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error saving branch for organization " + organizationId, e);
        }
    }

    @Override
    public Optional<Branch> findById(String organizationId, String branchId) {
        try {
            var document = getBranchesCollection(organizationId).document(branchId).get().get();
            if (document.exists()) {
                return Optional.ofNullable(document.toObject(Branch.class));
            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error finding branch " + branchId + " for organization " + organizationId, e);
        }
    }

    @Override
    public List<Branch> findAllByOrganizationId(String organizationId) {
        try {
            var documents = getBranchesCollection(organizationId).get().get().getDocuments();
            return documents.stream()
                    .map(doc -> doc.toObject(Branch.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error finding all branches for organization " + organizationId, e);
        }
    }

    @Override
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

}
