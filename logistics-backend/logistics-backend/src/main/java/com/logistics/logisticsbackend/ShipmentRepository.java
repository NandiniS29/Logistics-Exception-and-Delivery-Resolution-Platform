package com.logistics.logisticsbackend;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {

    List<Shipment> findByCustomerId(Integer customerId);

    Shipment findByTrackingNumber(String trackingNumber);
}
