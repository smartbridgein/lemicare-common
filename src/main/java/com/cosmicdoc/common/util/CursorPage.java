package com.cosmicdoc.common.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
public class CursorPage<T> {
    private final List<T> content;
    private final String nextPageToken;
    private final boolean hasNext;

    public CursorPage(List<T> content,
                      String nextPageToken,
                      boolean hasNext) {
        this.content = content;
        this.nextPageToken = nextPageToken;
        this.hasNext = hasNext;
    }

    public List<T> getContent() { return content; }
    public String getNextPageToken() { return nextPageToken; }
    public boolean isHasNext() { return hasNext; }
}
