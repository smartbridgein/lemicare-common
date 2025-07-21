package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.OrganizationMember;
import com.google.cloud.firestore.WriteBatch;

import java.util.List;
import java.util.Optional;

public interface OrganizationMemberRepository extends BaseRepository <OrganizationMember,String> {
     void saveInTransaction(WriteBatch batch, OrganizationMember org);
     Optional<OrganizationMember> findByUserIdAndOrgId(String userId, String organizationId);
     List<OrganizationMember> findAllByOrganizationId(String organizationId);

}
