package com.cosmicdoc.common.util;

import com.cosmicdoc.common.response.CursorPayload;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;

public class CursorUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String encode(CursorPayload payload) {
        try {
            String json = mapper.writeValueAsString(payload);
            return Base64.getUrlEncoder().encodeToString(json.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Cursor encode failed");
        }
    }

    public static CursorPayload decode(String token) {
        try {
            String json = new String(Base64.getUrlDecoder().decode(token));
            return mapper.readValue(json, CursorPayload.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid cursor token");
        }
    }
}
