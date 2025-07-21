package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.Organization;
import com.cosmicdoc.common.model.Users;
import com.cosmicdoc.common.repository.OrganizationRepository;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class OrganizationRepositoryImpl extends BaseRepositoryImpl <Organization,String> implements OrganizationRepository {

    public OrganizationRepositoryImpl(Firestore firestore) {
       super();
       this.firestore = firestore;
    }

    @Override
    protected CollectionReference getCollection() {
        return firestore.collection("organizations");
    }


    @Override
    public Optional<Organization> findByOrganizationName(String orgName) {
        try {
            var documents = getCollection().whereEqualTo("name", orgName).get().get().getDocuments();
            if(!documents.isEmpty()) {
                return Optional.ofNullable(documents.get(0).toObject(Organization.class));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding by organization by organization name");
        }
        return Optional.empty();
    }
    @Override
    public void saveInTransaction(WriteBatch batch, Organization org) {
        var docRef = getCollection().document(org.getOrgId());
        batch.set(docRef, org);
    }
}


