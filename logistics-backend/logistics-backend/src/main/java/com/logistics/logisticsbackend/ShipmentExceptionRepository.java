package com.logistics.logisticsbackend;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentExceptionRepository
        extends JpaRepository<ShipmentException, Integer> {

    List<ShipmentException> findByShipmentId(Integer shipmentId);

    List<ShipmentException> findByStatus(String status);
}
