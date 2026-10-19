package com.logistics.logisticsbackend;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/route-assignments")
@CrossOrigin(origins = "*")
public class RouteAssignmentController {

    private final RouteAssignmentService assignmentService;

    public RouteAssignmentController(
            RouteAssignmentService assignmentService) {

        this.assignmentService = assignmentService;
    }

    @GetMapping
    public ResponseEntity<List<RouteAssignment>>
    getAllAssignments() {

        return ResponseEntity.ok(
                assignmentService.getAllAssignments()
        );
    }

    @GetMapping("/{assignmentId}")
    public ResponseEntity<?> getAssignmentById(
            @PathVariable Integer assignmentId) {

        RouteAssignment assignment =
                assignmentService
                        .getAssignmentById(assignmentId);

        if (assignment == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Route assignment not found.");
        }

        return ResponseEntity.ok(assignment);
    }

    @GetMapping("/shipment/{shipmentId}")
    public ResponseEntity<List<RouteAssignment>>
    getAssignmentsByShipmentId(
            @PathVariable Integer shipmentId) {

        return ResponseEntity.ok(
                assignmentService
                        .getAssignmentsByShipmentId(shipmentId)
        );
    }

    @GetMapping("/assigned-to/{assignedTo}")
    public ResponseEntity<List<RouteAssignment>>
    getAssignmentsByAssignedTo(
            @PathVariable String assignedTo) {

        return ResponseEntity.ok(
                assignmentService
                        .getAssignmentsByAssignedTo(assignedTo)
        );
    }

    @PostMapping
    public ResponseEntity<RouteAssignment>
    createAssignment(
            @RequestBody RouteAssignment assignment) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        assignmentService
                                .createAssignment(assignment)
                );
    }

    @PutMapping("/{assignmentId}")
    public ResponseEntity<?> updateAssignment(
            @PathVariable Integer assignmentId,
            @RequestBody RouteAssignment assignment) {

        RouteAssignment updated =
                assignmentService.updateAssignment(
                        assignmentId,
                        assignment
                );

        if (updated == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Route assignment not found.");
        }

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<?> deleteAssignment(
            @PathVariable Integer assignmentId) {

        boolean deleted =
                assignmentService
                        .deleteAssignment(assignmentId);

        if (!deleted) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Route assignment not found.");
        }

        return ResponseEntity.ok(
                "Route assignment deleted successfully."
        );
    }
}
