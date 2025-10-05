package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.PersistableEntity;
import com.cosmicdoc.common.repository.BaseRepository;
import com.google.cloud.firestore.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public abstract class  BaseRepositoryImpl<T extends PersistableEntity, ID> implements BaseRepository<T, ID> {

    private static final Logger logger = LoggerFactory.getLogger(BaseRepositoryImpl.class);

    protected Firestore firestore ;

    protected Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public BaseRepositoryImpl(Firestore firestore) {
        this.firestore = this.firestore;
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
            String docId = entity.getId(); // Direct call to getId() from PersistableEntity
            if (docId == null || docId.isEmpty()) {
                String newId = UUID.randomUUID().toString();
                entity.setId((String) newId); // Set ID using setId() from PersistableEntity
                docId = newId;
            }
            getCollection().document(docId).set(entity).get();
            // Instead of findById, you can just return the updated entity if you trust the write
            return entity; // More efficient than a re-fetch
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error saving document", e);
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
