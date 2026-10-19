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
@RequestMapping("/api/exceptions")
@CrossOrigin(origins = "*")
public class ShipmentExceptionController {

    private final ShipmentExceptionService exceptionService;
    private final ExceptionDetectionService detectionService;

    public ShipmentExceptionController(
            ShipmentExceptionService exceptionService,
            ExceptionDetectionService detectionService) {

        this.exceptionService = exceptionService;
        this.detectionService = detectionService;
    }

    // GET ALL EXCEPTIONS
    @GetMapping
    public ResponseEntity<List<ShipmentException>> getAllExceptions() {
        return ResponseEntity.ok(
                exceptionService.getAllExceptions()
        );
    }

    // AUTOMATICALLY DETECT OVERDUE SHIPMENTS
    @PostMapping("/detect")
    public ResponseEntity<?> detectExceptions() {
        try {
            List<ShipmentException> detected =
                    detectionService.detectExceptions();

            return ResponseEntity.ok(detected);

        } catch (Exception e) {
            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR
            ).body(
                    "Exception detection failed: " + e.getMessage()
            );
        }
    }

    // GET EXCEPTION BY ID
    @GetMapping("/{exceptionId}")
    public ResponseEntity<?> getExceptionById(
            @PathVariable Integer exceptionId) {

        ShipmentException exception =
                exceptionService.getExceptionById(exceptionId);

        if (exception == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Exception not found.");
        }

        return ResponseEntity.ok(exception);
    }

    // GET EXCEPTIONS BY SHIPMENT
    @GetMapping("/shipment/{shipmentId}")
    public ResponseEntity<List<ShipmentException>>
    getExceptionsByShipment(
            @PathVariable Integer shipmentId) {

        return ResponseEntity.ok(
                exceptionService.getExceptionsByShipmentId(shipmentId)
        );
    }

    // GET EXCEPTIONS BY STATUS
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ShipmentException>>
    getExceptionsByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                exceptionService.getExceptionsByStatus(status)
        );
    }

    // CREATE EXCEPTION MANUALLY
    @PostMapping
    public ResponseEntity<ShipmentException> createException(
            @RequestBody ShipmentException exception) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        exceptionService.createException(exception)
                );
    }

    // UPDATE EXCEPTION
    @PutMapping("/{exceptionId}")
    public ResponseEntity<?> updateException(
            @PathVariable Integer exceptionId,
            @RequestBody ShipmentException exception) {

        ShipmentException updated =
                exceptionService.updateException(
                        exceptionId,
                        exception
                );

        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Exception not found.");
        }

        return ResponseEntity.ok(updated);
    }

    // DELETE EXCEPTION
    @DeleteMapping("/{exceptionId}")
    public ResponseEntity<?> deleteException(
            @PathVariable Integer exceptionId) {

        boolean deleted =
                exceptionService.deleteException(exceptionId);

        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Exception not found.");
        }

        return ResponseEntity.ok(
                "Exception deleted successfully."
        );
    }
}
