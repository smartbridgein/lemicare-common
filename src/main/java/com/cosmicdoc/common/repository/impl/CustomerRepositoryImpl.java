package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.CustomerStatus;
import com.cosmicdoc.common.model.Customers;
import com.cosmicdoc.common.repository.CustomerRepository;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
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
    public Optional<Customers> findByPhone(String mobileNumber) {
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
}
