package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.PersistableEntity;
import com.cosmicdoc.common.repository.TransactionalRepository;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;

import java.util.UUID;

public abstract class AbstractTransactionalRepositoryImpl<T extends PersistableEntity, ID>
        extends BaseRepositoryImpl<T, ID> implements TransactionalRepository<T, ID> {

    public AbstractTransactionalRepositoryImpl(Firestore firestore) {
        super(firestore); // Pass Firestore to BaseRepositoryImpl's constructor
    }

    @Override
    public void saveInTransaction(WriteBatch batch, T entity) {
        String docId = entity.getId();
        if (docId == null || docId.isEmpty()) {
            String newId = UUID.randomUUID().toString();
            entity.setId((String) newId); // Update the entity with the new ID
            docId = newId;
        }
        DocumentReference docRef = getCollection().document(docId);
        batch.set(docRef, entity);
    }

    @Override
    public void updateInTransaction(WriteBatch batch, T entity) {
        String docId = entity.getId();
        if (docId == null || docId.isEmpty()) {
            throw new IllegalArgumentException("Entity ID cannot be null or empty for updateInTransaction.");
        }
        DocumentReference docRef = getCollection().document(docId);
        batch.set(docRef, entity); // Default: Overwrite the document (Firestore's set is upsert)
        // If you need partial updates, consider batch.update(docRef, Map<String, Object>)
    }
}
