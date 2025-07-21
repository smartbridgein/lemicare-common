package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.Organization;
import com.cosmicdoc.common.model.Users;
import com.google.cloud.firestore.WriteBatch;

import java.util.Optional;

public interface OrganizationRepository extends BaseRepository <Organization,String>{
   Optional<Organization>  findByOrganizationName(String orgName);
   void saveInTransaction(WriteBatch batch, Organization org);
}
