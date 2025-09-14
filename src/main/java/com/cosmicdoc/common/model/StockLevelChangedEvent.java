package com.cosmicdoc.common.model;

import com.cosmicdoc.common.util.IdGenerator; // Assuming you have this
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * An event DTO representing a change in a medicine's stock level.
 * <p>
 * This object is published to a message broker (like Kafka or RabbitMQ) by the
 * inventory-service whenever a transaction (purchase, sale, return) that
 * affects inventory is successfully completed.
 * <p>
 * Downstream services (like storefront-service) can consume this event to
 * update their own state, such as updating a product's visibility on an
 * e-commerce site.
 */
@Data
@NoArgsConstructor // Required for JSON deserialization by consumers
@AllArgsConstructor
@Builder
public class StockLevelChangedEvent {
/**
 * A unique identifier for this specific event instance (e.g., a ULID).
 * Crucial for tracing and ensuring idempotency in consumers.
 */
private String eventId;

 /**
 * The timestamp of when the event was generated.
 */
 private Instant eventTimestamp;
 /**
 * The organization (tenant) ID this event belongs to.
 * Essential for routing and multi-tenancy in consumers.
 */
 private String organizationId;
 /**
 * The branch ID where the stock change occurred.
 */
  private String branchId;

 /**
 * The unique ID of the medicine whose stock has changed.
 */
 private String medicineId;

/**
 * The amount by which the stock changed.
 * Positive for stock increases (e.g., purchases, sales returns).
 * Negative for stock decreases (e.g., sales, purchase returns).
 */
private int changeInStock;

/**
 * The new, final total stock quantity for the medicine after the change.
*/
private int newTotalStock;

private String medicineName;

private double mrp;

private String taxprofileId;

private String gstType;

private String category;


 /**
 * A static factory method for easily creating a new event.
  * @param organizationId The organization ID.
 * @param branchId The branch ID.* @param medicineId The medicine ID.
 * @param changeInStock The quantity change.
 * @param newTotalStock The final total stock.
 * @return A new StockLevelChangedEvent instance.
 */
public static StockLevelChangedEvent of(String organizationId, String branchId, String medicineId, int changeInStock, int newTotalStock,String medicineName, double mrp,String taxprofileId,String gstType,String category) {
return new StockLevelChangedEvent(
                IdGenerator.newId("evt"), // Generate a unique event ID
                Instant.now(), // Set the current timestamp
                organizationId,
                branchId,
                 medicineId,
                changeInStock,
                newTotalStock,
                medicineName,
                mrp,
                taxprofileId,
                gstType,
                category

                );
     }
}