package com.logistics.logisticsbackend;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class RouteAssignmentService {

    private final RouteAssignmentRepository assignmentRepository;

    public RouteAssignmentService(
            RouteAssignmentRepository assignmentRepository) {

        this.assignmentRepository = assignmentRepository;
    }

    public List<RouteAssignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    public RouteAssignment getAssignmentById(
            Integer assignmentId) {

        return assignmentRepository
                .findById(assignmentId)
                .orElse(null);
    }

    public List<RouteAssignment> getAssignmentsByShipmentId(
            Integer shipmentId) {

        return assignmentRepository
                .findByShipmentId(shipmentId);
    }

    public List<RouteAssignment> getAssignmentsByAssignedTo(
            String assignedTo) {

        return assignmentRepository
                .findByAssignedTo(assignedTo);
    }

    public RouteAssignment createAssignment(
            RouteAssignment assignment) {

        return assignmentRepository.save(assignment);
    }

    public RouteAssignment updateAssignment(
            Integer assignmentId,
            RouteAssignment assignment) {

        RouteAssignment existing =
                assignmentRepository
                        .findById(assignmentId)
                        .orElse(null);

        if (existing == null) {
            return null;
        }

        existing.setShipmentId(
                assignment.getShipmentId());

        existing.setRouteName(
                assignment.getRouteName());

        existing.setOrigin(
                assignment.getOrigin());

        existing.setDestination(
                assignment.getDestination());

        existing.setAssignedTo(
                assignment.getAssignedTo());

        existing.setVehicleNumber(
                assignment.getVehicleNumber());

        existing.setAssignmentStatus(
                assignment.getAssignmentStatus());

        if (assignment.getAssignedAt() != null) {
            existing.setAssignedAt(assignment.getAssignedAt());
        }

        return assignmentRepository.save(existing);
    }

    public boolean deleteAssignment(
            Integer assignmentId) {

        if (!assignmentRepository
                .existsById(assignmentId)) {

            return false;
        }

        assignmentRepository.deleteById(assignmentId);

        return true;
    }
}
