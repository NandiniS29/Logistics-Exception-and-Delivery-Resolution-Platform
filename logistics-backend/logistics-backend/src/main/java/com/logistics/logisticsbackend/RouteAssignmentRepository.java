package com.logistics.logisticsbackend;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteAssignmentRepository
        extends JpaRepository<RouteAssignment, Integer> {

    List<RouteAssignment> findByShipmentId(Integer shipmentId);

    List<RouteAssignment> findByAssignedTo(String assignedTo);
}
