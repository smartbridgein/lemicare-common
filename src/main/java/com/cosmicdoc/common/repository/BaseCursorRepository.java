package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.util.CursorPage;

public interface BaseCursorRepository<T> {

    CursorPage<T> findPage(
            String organizationId,
            int pageSize,
            String nextPageToken
    );
}
