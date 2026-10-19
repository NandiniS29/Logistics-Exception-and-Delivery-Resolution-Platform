package com.logistics.logisticsbackend;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExceptionDetectionService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentExceptionRepository exceptionRepository;

    public ExceptionDetectionService(
            ShipmentRepository shipmentRepository,
            ShipmentExceptionRepository exceptionRepository) {

        this.shipmentRepository = shipmentRepository;
        this.exceptionRepository = exceptionRepository;
    }

    @Transactional
    public List<ShipmentException> detectExceptions() {

        List<ShipmentException> detectedExceptions =
                new ArrayList<>();

        List<Shipment> shipments =
                shipmentRepository.findAll();

        LocalDate today = LocalDate.now();

        for (Shipment shipment : shipments) {

            // Skip shipments without an expected delivery date.
            if (shipment.getExpectedDeliveryDate() == null) {
                continue;
            }

            // Skip shipments that have already been delivered.
            if ("DELIVERED".equalsIgnoreCase(shipment.getStatus())) {
                continue;
            }

            // Check whether the shipment is overdue.
            boolean isOverdue =
                    shipment.getExpectedDeliveryDate().isBefore(today);

            if (!isOverdue) {
                continue;
            }

            // Retrieve existing exceptions for this shipment.
            List<ShipmentException> existingExceptions =
                    exceptionRepository.findByShipmentId(
                            shipment.getShipmentId()
                    );

            // Do not create duplicate delayed-delivery exceptions,
            // including when an earlier exception was closed.
            boolean alreadyDetected =
                    existingExceptions != null
                    && existingExceptions.stream().anyMatch(exception ->
                            "Delayed Delivery".equalsIgnoreCase(
                                    exception.getExceptionType()
                            )
                    );

            if (alreadyDetected) {
                continue;
            }

            // Create a new delayed-delivery exception.
            ShipmentException exception =
                    new ShipmentException();

            exception.setShipmentId(
                    shipment.getShipmentId()
            );

            exception.setExceptionType(
                    "Delayed Delivery"
            );

            exception.setDescription(
                    "Shipment delivery date has been exceeded."
            );

            exception.setPriority("HIGH");

            exception.setStatus("OPEN");

            ShipmentException savedException =
                    exceptionRepository.save(exception);

            detectedExceptions.add(savedException);
        }

        return detectedExceptions;
    }
}
