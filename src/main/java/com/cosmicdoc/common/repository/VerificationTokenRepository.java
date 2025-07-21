package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.VerificationToken;
import com.google.cloud.firestore.WriteBatch;
import java.util.Optional;

public interface VerificationTokenRepository extends BaseRepository<VerificationToken, String> {
    Optional<VerificationToken> findByToken(String token);
    void deleteInTransaction(WriteBatch batch, String token);
    void saveInTransaction(WriteBatch batch, VerificationToken verificationToken);
}