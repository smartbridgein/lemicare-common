package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.DeliveryOrder;
import com.cosmicdoc.common.model.DeliveryStatus;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for the DeliveryOrder Firestore collection.
 * Extends FirestoreRepository.
 */
public interface DeliveryOrderRepository  {

    /**
     * Finds a delivery order by its unique LemiCare order ID within a specific branch.
     * Spring Data will generate the appropriate Firestore query for this method name.
     */
    Optional<DeliveryOrder> findByOrderIdAndOrganizationIdAndBranchId(String orderId, String organizationId);

    /**
     * Finds a delivery order by the external partner's tracking ID.
     * For this to be efficient, you must create an index on the 'partnerTrackingId' field in Firestore.
     */
    Optional<DeliveryOrder> findByPartnerTrackingId(String partnerTrackingId);

    /**
     * Finds all deliveries for a specific branch with a given status.
     * Requires a composite index on (organizationId, branchId, status) in Firestore.
     */
    List<DeliveryOrder> findByOrganizationIdAndBranchIdAndStatus(String organizationId, DeliveryStatus status,String customerId);

    /**
     * Finds all deliveries for an entire organization with a given status.
     * Requires a composite index on (organizationId, status) in Firestore.
     */
    List<DeliveryOrder> findByOrganizationIdAndStatus(String organizationId, DeliveryStatus status);

    List<DeliveryOrder> findByOrganizationIdAndBranchId(String orgId,String customerId);

    DeliveryOrder save(DeliveryOrder deliveryOrder);

    public Optional<DeliveryOrder> findById(String organizationId, String deliveryId);
}
