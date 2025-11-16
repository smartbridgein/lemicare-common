package com.cosmicdoc.common.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.cloud.Timestamp;
import java.io.IOException;

public class FirestoreTimestampDeserializer extends JsonDeserializer<Timestamp> {

    @Override
    public Timestamp deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        JsonNode node = p.getCodec().readTree(p);

        if (node == null || node.isNull()) {
            return null;
        }

        // Case 1: ISO String value
        if (node.isTextual()) {
            return Timestamp.parseTimestamp(node.asText());
        }

        // Case 2: Epoch seconds only (number)
        if (node.isNumber()) {
            long seconds = node.asLong();
            return Timestamp.ofTimeSecondsAndNanos(seconds, 0);
        }

        // Case 3: Firestore Java SDK format: {_seconds, _nanoseconds}
        JsonNode secondsNode = node.get("_seconds");
        JsonNode nanosNode = node.get("_nanoseconds");

        if (secondsNode != null && nanosNode != null) {
            return Timestamp.ofTimeSecondsAndNanos(secondsNode.asLong(), nanosNode.asInt());
        }

        // Case 4: Google REST API format: {seconds, nanos}
        secondsNode = node.get("seconds");
        nanosNode = node.get("nanos");

        if (secondsNode != null && nanosNode != null) {
            return Timestamp.ofTimeSecondsAndNanos(secondsNode.asLong(), nanosNode.asInt());
        }

        // If nothing matches → timestamp missing or unknown format
        return null;
    }
}