package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.repository.BaseRepository;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public abstract class BaseRepositoryImpl<T, ID> implements BaseRepository<T, ID> {

    @Autowired
    protected Firestore firestore;

    protected Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public BaseRepositoryImpl() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected abstract CollectionReference getCollection();

    @Override
    public List<T> findAll() {
        try {
            List<T> entities = new ArrayList<>();
            var documents = getCollection().get().get().getDocuments();
            for (var document : documents) {
                entities.add(document.toObject(entityClass));
            }
            return entities;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error finding all documents", e);
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        try {
            var document = getCollection().document(id.toString()).get().get();
            if (document.exists()) {
                return Optional.ofNullable(document.toObject(entityClass));
            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error finding document by id", e);
        }
    }

    @Override
    public T save(T entity) {
        try {
            String docId;
            try {
                docId = entityClass.getMethod("getId").invoke(entity).toString();
                getCollection().document(docId).set(entity).get();
                @SuppressWarnings("unchecked")
                ID id = (ID) docId;
                return findById(id).orElseThrow(() -> new RuntimeException("Error saving entity"));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                var docRef = getCollection().add(entity).get();
                @SuppressWarnings("unchecked")
                ID id = (ID) docRef.getId();
                return findById(id).orElseThrow(() -> new RuntimeException("Error saving entity"));
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error saving document", e);
        }
    }

    @Override
    public void deleteById(ID id) {
        try {
            getCollection().document(id.toString()).delete().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error deleting document", e);
        }
    }

    @Override
    public boolean existsById(ID id) {
        try {
            DocumentSnapshot document = getCollection().document(id.toString()).get().get();
            return document.exists();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error checking document existence", e);
        }
    }
}
