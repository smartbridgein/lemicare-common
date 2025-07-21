package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.Branch;
import com.google.cloud.firestore.WriteBatch;

import java.util.List;
import java.util.Optional;

public interface BranchRepository extends BaseRepository <Branch,String>{
    public void saveInTransaction(WriteBatch batch, String organizationId, Branch branch);
    Branch save(String organizationId, Branch branch);
    Optional<Branch> findById(String organizationId, String branchId);
    Optional<Branch> findFirstByOrganizationId(String organizationId);
    List<Branch>findAllByOrganizationId(String organizationId);
}
