package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.User;
import com.cosmicdoc.common.repository.UserRepository;
import com.cosmicdoc.common.util.JsonDataLoader;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final JsonDataLoader jsonDataLoader;
    private final Map<String, User> userMap = new ConcurrentHashMap<>();

    public UserRepositoryImpl(JsonDataLoader jsonDataLoader) {
        this.jsonDataLoader = jsonDataLoader;
        loadUsers();
    }

    private void loadUsers() {
        Map<String, List<User>> data = jsonDataLoader.loadDataAsMap("users.json", User.class);
        List<User> users = data.getOrDefault("users", new ArrayList<>());
        users.stream()
            .filter(user -> user != null && user.getId() != null)
            .forEach(user -> userMap.put(user.getId(), user));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(userMap.get(id));
    }

    @Override
    public User save(User user) {
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteById(String id) {
        userMap.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return userMap.containsKey(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userMap.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return userMap.values().stream()
                .filter(user -> user.getPhone().equals(phone))
                .findFirst();
    }

    @Override
    public List<User> findByRole(String role) {
        return userMap.values().stream()
                .filter(user -> user.getRole().equals(role))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findByMfaEnabled(boolean mfaEnabled) {
        return userMap.values().stream()
                .filter(user -> user.isMfaEnabled() == mfaEnabled)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findByRoleAndMfaEnabled(String role, boolean mfaEnabled) {
        return userMap.values().stream()
                .filter(user -> user.getRole().equals(role) && user.isMfaEnabled() == mfaEnabled)
                .collect(Collectors.toList());
    }
}
