package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.Patient;
import com.cosmicdoc.common.repository.PatientRepository;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PatientRepositoryImpl implements PatientRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "patients";

    /**
     * Private helper to get a reference to the 'patients' sub-collection for a specific organization.
     */
    private CollectionReference getCollection(String organizationId) {
        return firestore.collection("organizations").document(organizationId).collection(COLLECTION_NAME);
    }

    @Override
    public Patient save(Patient patient) {
        if (patient.getOrganizationId() == null || patient.getPatientId() == null) {
            throw new IllegalArgumentException("OrganizationId and PatientId must not be null to save a patient.");
        }
        try {
            getCollection(patient.getOrganizationId()).document(patient.getPatientId()).set(patient).get();
            return patient;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error saving patient with ID: " + patient.getPatientId(), e);
        }
    }

    @Override
    public Optional<Patient> findById(String organizationId, String patientId) {
        try {
            var document = getCollection(organizationId).document(patientId).get().get();
            if (document.exists()) {
                return Optional.ofNullable(document.toObject(Patient.class));
            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error finding patient by ID: " + patientId, e);
        }
    }

    @Override
    public List<Patient> searchByName(String organizationId, String nameQuery) {
        try {
            // Firestore does not support native partial string search (like SQL's LIKE).
            // This is a common workaround using a range filter on the search field.
            // It allows for "starts with" or prefix searches.
            // For full-text search, a dedicated service like Algolia or Elasticsearch is recommended.
            Query query = getCollection(organizationId)
                    .orderBy("firstName") // Assuming you want to search by first name
                    .startAt(nameQuery)
                    .endAt(nameQuery + "\uf8ff"); // \uf8ff is a high-value Unicode character

            return query.get().get().getDocuments().stream()
                    .map(doc -> doc.toObject(Patient.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error searching for patients by name: " + nameQuery, e);
        }
    }

    @Override
    public boolean existsByMobileNumber(String organizationId, String mobileNumber) {
        try {
            // To query a field within a map, you use dot notation.
            Query query = getCollection(organizationId)
                    .whereEqualTo("contactInfo.mobileNumber", mobileNumber)
                    .limit(1);

            return !query.get().get().isEmpty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error checking for patient by mobile number", e);
        }
    }
}