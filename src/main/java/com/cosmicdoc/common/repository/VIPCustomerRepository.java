package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.VIPCustomer;

import java.util.List;
import java.util.Optional;

public interface VIPCustomerRepository extends TransactionalRepository<VIPCustomer, String> {

    Optional<VIPCustomer> findById(String id); // ID here is the same as Customers.customerId

    Optional<VIPCustomer> findByPhoneNumber(String phoneNumber);

    Optional<VIPCustomer> findByEmail(String email);

    List<VIPCustomer> findByVipTier(String vipTier);
}
