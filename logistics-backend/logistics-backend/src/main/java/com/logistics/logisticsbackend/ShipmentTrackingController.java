package com.logistics.logisticsbackend;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tracking")
@CrossOrigin(origins = "*")
public class ShipmentTrackingController {

    private final ShipmentTrackingService trackingService;

    public ShipmentTrackingController(
            ShipmentTrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @GetMapping("/shipment/{shipmentId}")
    public ResponseEntity<List<ShipmentTracking>> getTracking(
            @PathVariable Integer shipmentId) {

        return ResponseEntity.ok(
                trackingService.getTrackingByShipmentId(shipmentId)
        );
    }

    @PostMapping
    public ResponseEntity<ShipmentTracking> createTracking(
            @RequestBody ShipmentTracking tracking) {

        return ResponseEntity.ok(
                trackingService.createTracking(tracking)
        );
    }
}
