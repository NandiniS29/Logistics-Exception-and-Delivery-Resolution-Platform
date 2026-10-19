package com.logistics.logisticsbackend;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentTrackingRepository
        extends JpaRepository<ShipmentTracking, Integer> {

    List<ShipmentTracking> findByShipmentId(Integer shipmentId);
}
