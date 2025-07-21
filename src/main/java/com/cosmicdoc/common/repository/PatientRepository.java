package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.Patient;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Patient entities in Firestore.
 */
public interface PatientRepository {

    /**
     * Saves a new patient or updates an existing one.
     * The implementation will handle storing the document in the correct
     * sub-collection based on the organizationId within the Patient object.
     *
     * @param patient The Patient object to save.
     * @return The saved Patient object.
     */
    Patient save(Patient patient);

    /**
     * Finds a patient by their ID within a specific organization.
     *
     * @param organizationId The ID of the organization to search within.
     * @param patientId The ID of the patient to find.
     * @return An Optional containing the Patient if found, otherwise an empty Optional.
     */
    Optional<Patient> findById(String organizationId, String patientId);

    /**
     * Searches for patients within an organization by their name.
     * This uses a prefix search on the patient's name.
     *
     * @param organizationId The ID of the organization to search within.
     * @param nameQuery The name (or part of the name) to search for.
     * @return A list of matching Patient objects.
     */
    List<Patient> searchByName(String organizationId, String nameQuery);

    /**
     * Checks if a patient with the given mobile number already exists
     * within a specific organization.
     *
     * @param organizationId The ID of the organization to check.
     * @param mobileNumber The mobile number to check for.
     * @return true if a patient with the mobile number exists, false otherwise.
     */
    boolean existsByMobileNumber(String organizationId, String mobileNumber);

}