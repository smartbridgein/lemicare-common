package com.cosmicdoc.common.util;

import java.util.Collections;
import java.util.List;

import com.google.api.gax.paging.Page;
import com.google.firebase.database.annotations.Nullable;

// We'll rename it slightly to indicate it's custom and holds more info
public class FirestorePage<T> implements com.google.api.gax.paging.Page<T> {
    private final List<T> content;
    private final String nextPageToken;
    private final int pageSize;
    private final long totalElements; // Add total elements
    private final int totalPages;    // Add total pages
    private final boolean isLast;    // Add isLast

    // Update constructor to accept these new fields
    public FirestorePage(List<T> content, @Nullable String nextPageToken, int pageSize, long totalElements, int totalPages, boolean isLast) {
        this.content = content;
        this.nextPageToken = nextPageToken;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.isLast = isLast;
    }

    @Override
    public List<T> getValues() {
        return content;
    }

    @Override
    public String getNextPageToken() {
        return nextPageToken;
    }

    @Override
    public boolean hasNextPage() {
        return nextPageToken != null;
    }

    @Override
    public Page<T> getNextPage() {
        // This remains unsupported for direct Firestore paging
        throw new UnsupportedOperationException("Not implemented for direct Firestore paging. Use nextPageToken manually.");
    }


    public int getPageSize() {
        return pageSize;
    }


    public int getPageElementCount() {
        return content.size();
    }

    @Override
    public Iterable<T> iterateAll() {
        return (Iterable<T>) content.iterator(); // Simple iteration over current page
    }


    public Iterable<Page<T>> iteratePages() {
        // This is complex to implement generically without knowing the full pagination logic
        // For now, it might be simpler to handle page iteration client-side by passing next tokens.
        return Collections.singletonList(this); // Just return this single page
    }

    // New getters for the added properties
    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isLast() {
        return isLast;
    }
}