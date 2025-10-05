package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.OrganizationMember;
import com.cosmicdoc.common.model.OrganizationMemberStatus;
import com.google.cloud.firestore.WriteBatch;

import java.util.List;
import java.util.Optional;

public interface OrganizationMemberRepository extends TransactionalRepository <OrganizationMember,String> {
     void saveInTransaction(WriteBatch batch, OrganizationMember org);
     Optional<OrganizationMember> findByUserIdAndOrgId(String userId, String organizationId);
     List<OrganizationMember> findAllByOrganizationId(String organizationId);
     Optional<OrganizationMember> findByCustomerIdAndOrgId(String customerId, String organizationId);
     List<OrganizationMember> findAllByCustomerId(String customerId);
     List<OrganizationMember> findAllByUserId(String userId);
     public Optional<OrganizationMember> findByUserIdAndOrganizationId(String userId, String organizationId);
     List<OrganizationMember> findByUserIdAndStatus(String userId, OrganizationMemberStatus status);
}
