package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.PersistableEntity;
import com.google.cloud.firestore.WriteBatch;

public interface TransactionalRepository<T extends PersistableEntity, ID> extends BaseRepository<T, ID> {
    void saveInTransaction(WriteBatch batch, T entity);
    void updateInTransaction(WriteBatch batch, T entity);
}
