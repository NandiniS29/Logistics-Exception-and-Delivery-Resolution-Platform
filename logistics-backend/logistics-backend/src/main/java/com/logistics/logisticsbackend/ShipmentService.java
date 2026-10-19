package com.logistics.logisticsbackend;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;

    public ShipmentService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    // Get all shipments
    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    // Get shipment by ID
    public Shipment getShipmentById(Integer id) {
        return shipmentRepository.findById(id).orElse(null);
    }

    // Get shipments belonging to a customer
    public List<Shipment> getShipmentsByCustomerId(Integer customerId) {
        return shipmentRepository.findByCustomerId(customerId);
    }

    // Get shipment by tracking number
    public Shipment getShipmentByTrackingNumber(String trackingNumber) {
        return shipmentRepository.findByTrackingNumber(trackingNumber);
    }

    // Create shipment
    public Shipment createShipment(Shipment shipment) {
        return shipmentRepository.save(shipment);
    }

    // Update shipment
    public Shipment updateShipment(Integer id, Shipment shipment) {

        Shipment existingShipment = shipmentRepository.findById(id)
                .orElse(null);

        if (existingShipment == null) {
            return null;
        }

        existingShipment.setCustomerId(shipment.getCustomerId());
        existingShipment.setTrackingNumber(shipment.getTrackingNumber());
        existingShipment.setOrigin(shipment.getOrigin());
        existingShipment.setDestination(shipment.getDestination());
        existingShipment.setShipmentDate(shipment.getShipmentDate());
        existingShipment.setExpectedDeliveryDate(
                shipment.getExpectedDeliveryDate()
        );
        existingShipment.setStatus(shipment.getStatus());

        return shipmentRepository.save(existingShipment);
    }

    // Delete shipment
    public boolean deleteShipment(Integer id) {

        if (!shipmentRepository.existsById(id)) {
            return false;
        }

        shipmentRepository.deleteById(id);
        return true;
    }
}
