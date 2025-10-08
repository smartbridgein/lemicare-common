package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.CustomerStatus;
import com.cosmicdoc.common.model.Customers;
import com.cosmicdoc.common.repository.CustomerRepository;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
@Slf4j
public class CustomerRepositoryImpl extends AbstractTransactionalRepositoryImpl<Customers, String> implements CustomerRepository {

    public CustomerRepositoryImpl(Firestore firestore) {
        super(firestore);
        this.firestore = firestore;
    }

    @Override
    protected CollectionReference getCollection() {
        return firestore.collection("customers");
    }

    @Override
    public Optional<Customers> findByEmail(String email) {
        try {
            var query = getCollection().whereEqualTo("email", email);
            var querySnapshot = query.get().get();

            if (!querySnapshot.getDocuments().isEmpty()) {
                // Manually map to Customers object for consistency and debugging
                var doc = querySnapshot.getDocuments().get(0);
                Customers customer = new Customers();
                customer.setCustomerId(doc.getId());
                customer.setEmail((String) doc.get("email"));
                customer.setDisplayName((String) doc.get("displayName"));
                customer.setMobileNumber((String) doc.get("mobileNumber"));
                customer.setLinkedUserId((String) doc.get("linkedUserId"));

                if (doc.get("status") != null) {
                    try {
                        customer.setStatus(CustomerStatus.valueOf((String) doc.get("status")));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid customer status found, defaulting to GUEST: " + doc.get("status"));
                        customer.setStatus(CustomerStatus.GUEST); // Default status
                    }
                } else {
                    customer.setStatus(CustomerStatus.GUEST); // Default if not present
                }

                // Addresses and timestamps would also need manual mapping if not directly object-mappable
                // For brevity, assuming simple direct mapping via toObject() or manual gets

                return Optional.of(customer);
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Error finding customer by email", e);
        }
    }

    @Override
    public Optional<Customers> findByMobileNumber(String mobileNumber) {
        try {
            var query = getCollection().whereEqualTo("mobileNumber", mobileNumber);

            try {
                var future = query.get();
                var querySnapshot = future.get(15, java.util.concurrent.TimeUnit.SECONDS);

                if (!querySnapshot.getDocuments().isEmpty()) {
                    return Optional.ofNullable(querySnapshot.getDocuments().get(0).toObject(Customers.class));
                }
            } catch (java.util.concurrent.TimeoutException e) {
                throw new RuntimeException("Timeout when querying Firestore for customer by mobile number", e);
            } catch (com.google.api.gax.rpc.DeadlineExceededException e) {
                throw new RuntimeException("Connection deadline exceeded when finding customer by mobile number", e);
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Error finding customer by mobile number", e);
        }
    }


    public void updateLastLogin(String customerId) {
        try {
            var docRef = getCollection().document(customerId);
            docRef.update("lastLoginAt", new Timestamp(System.currentTimeMillis()));
        } catch (Exception e) {
            throw new RuntimeException("Error while updating last login for customer", e);
        }
    }

    /**
     * Finds a customer by either their email or mobile number.
     * Prioritizes email if both are provided and a customer is found by email.
     * @param email The email address to search for (can be null or blank).
     * @param mobileNumber The mobile number to search for (can be null or blank).
     * @return An Optional containing the found Customer, or empty if not found.
     * @throws RuntimeException if an underlying Firestore operation fails during a lookup.
     */
    @Override
    public Optional<Customers> findByEmailOrMobileNumber(String email, String mobileNumber) {
        // First, try finding by email if available
        if (email != null && !email.isBlank()) {
            try {
                Optional<Customers> customerByEmail = findByEmail(email);
                if (customerByEmail.isPresent()) {
                    log.debug("Found customer by email '{}'.", email);
                    return customerByEmail;
                }
            } catch (RuntimeException e) {
                log.warn("Error when trying to find customer by email '{}' as part of findByEmailOrMobileNumber. Proceeding to check mobile number. Error: {}", email, e.getMessage());
                // Don't rethrow, attempt mobile number lookup
            }
        }

        // If not found by email, or email was not provided/had an error, try finding by mobile number
        if (mobileNumber != null && !mobileNumber.isBlank()) {
            try {
                Optional<Customers> customerByMobile = findByMobileNumber(mobileNumber);
                if (customerByMobile.isPresent()) {
                    log.debug("Found customer by mobile number '{}'.", mobileNumber);
                    return customerByMobile;
                }
            } catch (RuntimeException e) {
                log.warn("Error when trying to find customer by mobile number '{}' as part of findByEmailOrMobileNumber. Error: {}", mobileNumber, e.getMessage());
                // Don't rethrow, just return empty
            }
        }

        log.debug("No customer found by email '{}' or mobile number '{}'.",
                (email != null ? email : "N/A"), (mobileNumber != null ? mobileNumber : "N/A"));
        return Optional.empty();
    }
}
