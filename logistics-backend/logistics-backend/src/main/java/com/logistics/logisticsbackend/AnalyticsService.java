
package com.logistics.logisticsbackend;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentExceptionRepository exceptionRepository;
    private final ExceptionInvestigationRepository investigationRepository;
    private final ExceptionClosureRepository closureRepository;
    private final ExceptionResolutionRepository resolutionRepository;

    public AnalyticsService(
            ShipmentRepository shipmentRepository,
            ShipmentExceptionRepository exceptionRepository,
            ExceptionInvestigationRepository investigationRepository,
            ExceptionClosureRepository closureRepository,
            ExceptionResolutionRepository resolutionRepository) {

        this.shipmentRepository = shipmentRepository;
        this.exceptionRepository = exceptionRepository;
        this.investigationRepository = investigationRepository;
        this.closureRepository = closureRepository;
        this.resolutionRepository = resolutionRepository;
    }

    public Map<String, Object> getDashboardAnalytics() {

        long totalShipments = shipmentRepository.count();

        long totalExceptions = exceptionRepository.count();

        long totalInvestigations = investigationRepository.count();

        // Count unique exceptions with a resolution.
        Set<Integer> resolvedExceptionIds = new HashSet<>();

        // Keep one compensation amount per exception.
        Map<Integer, Double> compensationByException =
                new LinkedHashMap<>();

        resolutionRepository.findAll().forEach(resolution -> {

            Integer exceptionId = resolution.getExceptionId();

            if (exceptionId == null) {
                return;
            }

            if ("RESOLVED".equalsIgnoreCase(
                    resolution.getResolutionStatus())) {

                resolvedExceptionIds.add(exceptionId);
            }

            if (Boolean.TRUE.equals(
                    resolution.getCompensationRequired())) {

                Double amount = resolution.getCompensationAmount();

                if (amount != null) {
                    compensationByException.merge(
                            exceptionId,
                            amount,
                            Math::max
                    );
                }
            }
        });

        long totalResolutions = resolvedExceptionIds.size();

        long openExceptions = exceptionRepository.findAll()
                .stream()
                .filter(exception ->
                        "OPEN".equalsIgnoreCase(
                                exception.getStatus()))
                .count();

        // Count unique exceptions that have been closed.
        Set<Integer> closedExceptionIds = new HashSet<>();

        closureRepository.findAll().forEach(closure -> {
            if (closure.getExceptionId() != null
                    && "CLOSED".equalsIgnoreCase(
                            closure.getFinalStatus())) {

                closedExceptionIds.add(closure.getExceptionId());
            }
        });

        long closedExceptions = closedExceptionIds.size();

        double totalCompensation = compensationByException.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        Map<String, Object> analytics = new LinkedHashMap<>();

        analytics.put("totalShipments", totalShipments);
        analytics.put("totalExceptions", totalExceptions);
        analytics.put("openExceptions", openExceptions);
        analytics.put("closedExceptions", closedExceptions);
        analytics.put("totalInvestigations", totalInvestigations);
        analytics.put("totalResolutions", totalResolutions);
        analytics.put("totalCompensation", totalCompensation);

        return analytics;
    }
}
