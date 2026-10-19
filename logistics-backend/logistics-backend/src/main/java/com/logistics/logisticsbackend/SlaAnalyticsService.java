package com.logistics.logisticsbackend;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class SlaAnalyticsService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentExceptionRepository exceptionRepository;

    public SlaAnalyticsService(
            ShipmentRepository shipmentRepository,
            ShipmentExceptionRepository exceptionRepository) {

        this.shipmentRepository = shipmentRepository;
        this.exceptionRepository = exceptionRepository;
    }

    public Map<String, Object> getSlaAnalytics() {

        List<Shipment> shipments =
                shipmentRepository.findAll();

        LocalDate today = LocalDate.now();

        long totalShipments = shipments.size();

        long overdueShipments =
                shipments.stream()
                        .filter(shipment ->
                                shipment.getExpectedDeliveryDate() != null
                                && shipment.getExpectedDeliveryDate()
                                        .isBefore(today)
                                && !"DELIVERED".equalsIgnoreCase(
                                        shipment.getStatus()))
                        .count();

        long onTimeShipments =
                totalShipments - overdueShipments;

        double slaCompliancePercentage = 0.0;

        if (totalShipments > 0) {
            slaCompliancePercentage =
                    ((double) onTimeShipments
                            / totalShipments) * 100;
        }

        long totalExceptions =
                exceptionRepository.count();

        long openExceptions =
                exceptionRepository
                        .findByStatus("OPEN")
                        .size();

        long closedExceptions =
                exceptionRepository
                        .findByStatus("CLOSED")
                        .size();

        Map<String, Object> analytics =
                new LinkedHashMap<>();

        analytics.put(
                "totalShipments",
                totalShipments
        );

        analytics.put(
                "onTimeShipments",
                onTimeShipments
        );

        analytics.put(
                "overdueShipments",
                overdueShipments
        );

        analytics.put(
                "slaCompliancePercentage",
                Math.round(
                        slaCompliancePercentage * 100.0
                ) / 100.0
        );

        analytics.put(
                "totalExceptions",
                totalExceptions
        );

        analytics.put(
                "openExceptions",
                openExceptions
        );

        analytics.put(
                "closedExceptions",
                closedExceptions
        );

        return analytics;
    }
}
