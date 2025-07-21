package com.cosmicdoc.common.repository;


import com.cosmicdoc.common.model.User;
import com.cosmicdoc.common.model.Users;
import com.google.cloud.firestore.WriteBatch;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends BaseRepository<Users, String> {
    Optional<Users> findByEmail(String email);
    Optional<Users> findByPhone(String phone);
    void saveInTransaction(WriteBatch batch, Users user);
    void updateLastLogin(String userId);
}
