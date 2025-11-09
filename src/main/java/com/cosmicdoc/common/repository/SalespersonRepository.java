package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.Salesperson;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface SalespersonRepository extends TransactionalRepository<Salesperson, String> {

    Optional<Salesperson> findById(String orgId,String branchId,String id); // ID here is the same as Users.userId
    List<Salesperson> findByStoreCode(String orgId,String branchId,String storeCode);
    List<Salesperson> findByAreaCode(String orgId,String branchId,String areaCode);
    List<Salesperson> findByRegion(String orgId,String branchId,String region);
    Optional<Salesperson> findByEmail(String orgId,String branchId,String email);
    Optional<Salesperson> findByJobNumber(String orgId,String branchID,String jobNumber);
    List<Salesperson> findByIsStoreManager(String orgId,String branchId,Boolean isStoreManager);
    List<Salesperson> fetchAll (String orgId, String branchId);
    void deleteById(String organizationId, String branchId,String id);
    List<Salesperson> search(String orgId,String branchId,String query, String storeCode) throws ExecutionException, InterruptedException;
    public long countByStoreCode(String orgId, String branchId,String storeCode) throws ExecutionException, InterruptedException;

}
