package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.Doctor;
import com.cosmicdoc.common.repository.DoctorRepository;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class DoctorRepositoryImpl extends BaseRepositoryImpl<Doctor, String> implements DoctorRepository {
    
    public DoctorRepositoryImpl(Firestore firestore) {
        super();
        this.firestore = firestore;
    }

    @Override
    protected com.google.cloud.firestore.CollectionReference getCollection() {
        return firestore.collection("doctors");
    }

    @Override
    public Optional<Doctor> findByEmail(String email) {
        try {
            var documents = getCollection().whereEqualTo("email", email).get().get().getDocuments();
            if (!documents.isEmpty()) {
                return Optional.ofNullable(documents.get(0).toObject(Doctor.class));
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Error finding doctor by email", e);
        }
    }
}
