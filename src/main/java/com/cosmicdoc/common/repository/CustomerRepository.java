package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.Customers;
import com.google.cloud.firestore.WriteBatch;

import java.util.Optional;

public interface CustomerRepository extends TransactionalRepository<Customers, String> {

    Optional<Customers> findByEmail(String email);

    Optional<Customers> findByMobileNumber(String mobileNumber);

    void updateLastLogin(String identityId);

    Optional<Customers> findByEmailOrMobileNumber(String email, String mobileNumber);

}
