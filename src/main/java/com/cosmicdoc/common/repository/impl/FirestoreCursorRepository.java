package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.util.CursorPage;
import com.cosmicdoc.common.response.CursorPayload;
import com.cosmicdoc.common.util.CursorUtil;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;

import java.util.List;

public abstract class FirestoreCursorRepository<T> {

    protected final Firestore firestore;

    protected FirestoreCursorRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    protected CursorPage<T> executePagedQuery(
            Query baseQuery,
            int pageSize,
            String nextPageToken,
            Class<T> clazz
    ) {

        try {

            Query query = baseQuery;

            if (nextPageToken != null && !nextPageToken.isBlank()) {

                CursorPayload cursor = CursorUtil.decode(nextPageToken);

                if (cursor.getCreatedAtSeconds() == null
                        || cursor.getCreatedAtNanos() == null
                        || cursor.getProductId() == null) {

                    throw new IllegalArgumentException("Invalid cursor payload");
                }

                query = query.startAfter(
                        Timestamp.ofTimeSecondsAndNanos(
                                cursor.getCreatedAtSeconds(),
                                cursor.getCreatedAtNanos()
                        ),
                        cursor.getProductId()
                );
            }

            query = query.limit(pageSize + 1);

            List<QueryDocumentSnapshot> docs =
                    query.get().get().getDocuments();

            boolean hasNext = docs.size() > pageSize;

            if (hasNext) {
                docs = docs.subList(0, pageSize);
            }

            List<T> content = docs.stream()
                    .map(doc -> doc.toObject(clazz))
                    .toList();

            String newToken = null;

            if (!docs.isEmpty()) {

                QueryDocumentSnapshot lastDoc =
                        docs.get(docs.size() - 1);

                Timestamp createdAt = lastDoc.getTimestamp("createdAt");

                if (createdAt == null) {
                    throw new RuntimeException(
                            "createdAt missing in document: " + lastDoc.getId()
                    );
                }

                CursorPayload payload = new CursorPayload(
                        1,
                        createdAt.getSeconds(),
                        createdAt.getNanos(),
                        lastDoc.getId()
                );

                newToken = CursorUtil.encode(payload);
            }

            return new CursorPage<>(content, newToken, hasNext);

        } catch (Exception e) {
            throw new RuntimeException("Pagination failed", e);
        }
    }
}
