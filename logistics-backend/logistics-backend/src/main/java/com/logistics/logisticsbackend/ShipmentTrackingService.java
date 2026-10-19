package com.logistics.logisticsbackend;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ShipmentTrackingService {

    private final ShipmentTrackingRepository trackingRepository;

    public ShipmentTrackingService(
            ShipmentTrackingRepository trackingRepository) {
        this.trackingRepository = trackingRepository;
    }

    public List<ShipmentTracking> getTrackingByShipmentId(
            Integer shipmentId) {

        return trackingRepository.findByShipmentId(shipmentId);
    }

    public ShipmentTracking createTracking(
            ShipmentTracking tracking) {

        return trackingRepository.save(tracking);
    }
}
