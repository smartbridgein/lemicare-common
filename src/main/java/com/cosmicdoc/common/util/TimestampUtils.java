package com.cosmicdoc.common.util;

import com.google.cloud.Timestamp; // Import Google's Timestamp
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A utility class for handling and formatting Firestore Timestamps.
 */
public final class TimestampUtils {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private TimestampUtils() {}

    /**
     * Converts Firestore's seconds and nanos into a formatted date-time string
     * for a specific timezone.
     *
     * @param seconds The epoch seconds from the Firestore Timestamp.
     * @param nanos The nanoseconds from the Firestore Timestamp.
     * @param timeZone The target timezone (e.g., "Asia/Kolkata", "UTC").
     * @param formatPattern The desired output format (e.g., "dd-MM-yyyy HH:mm:ss").
     * @return A formatted date-time string, or an empty string if inputs are invalid.
     */
    public static String formatTimestamp(long seconds, int nanos, String timeZone, String formatPattern) {
        if (timeZone == null || formatPattern == null) {
            return ""; // Or throw an IllegalArgumentException
        }
        try {
            Instant instant = Instant.ofEpochSecond(seconds, nanos);
            ZoneId zoneId = ZoneId.of(timeZone);
            ZonedDateTime zonedDateTime = instant.atZone(zoneId);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
            return formatter.format(zonedDateTime);
        } catch (Exception e) {
            // Log the exception in a real application

            return "Invalid Timestamp";
        }
    }

    /**
     * An overloaded convenience method that takes a com.google.cloud.Timestamp object directly.
     *
     * @param timestamp The com.google.cloud.Timestamp object from your model.
     * @param timeZone The target timezone.
     * @param formatPattern The desired output format.
     * @return A formatted date-time string.
     */
    public static String formatTimestamp(Timestamp timestamp, String timeZone, String formatPattern) {
        if (timestamp == null) {
            return "";
        }
        return formatTimestamp(timestamp.getSeconds(), timestamp.getNanos(), timeZone, formatPattern);
    }
}