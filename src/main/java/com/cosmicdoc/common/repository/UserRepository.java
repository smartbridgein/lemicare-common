package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.User;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends BaseRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    List<User> findByRole(String role);
    List<User> findByMfaEnabled(boolean mfaEnabled);
    List<User> findByRoleAndMfaEnabled(String role, boolean mfaEnabled);
}
