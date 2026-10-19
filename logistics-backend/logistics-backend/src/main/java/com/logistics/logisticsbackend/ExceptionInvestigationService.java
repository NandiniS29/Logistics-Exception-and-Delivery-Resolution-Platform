package com.logistics.logisticsbackend;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExceptionInvestigationService {

    private final ExceptionInvestigationRepository investigationRepository;
    private final ShipmentExceptionRepository exceptionRepository;

    public ExceptionInvestigationService(
            ExceptionInvestigationRepository investigationRepository,
            ShipmentExceptionRepository exceptionRepository) {

        this.investigationRepository = investigationRepository;
        this.exceptionRepository = exceptionRepository;
    }

    public List<ExceptionInvestigation> getAllInvestigations() {
        return investigationRepository.findAll();
    }

    public ExceptionInvestigation getInvestigationById(
            Integer investigationId) {

        return investigationRepository
                .findById(investigationId)
                .orElse(null);
    }

    public List<ExceptionInvestigation> getInvestigationsByExceptionId(
            Integer exceptionId) {

        return investigationRepository
                .findByExceptionId(exceptionId);
    }

    @Transactional
    public ExceptionInvestigation createInvestigation(
            ExceptionInvestigation investigation) {

        // Save the investigation first.
        ExceptionInvestigation savedInvestigation =
                investigationRepository.save(investigation);

        // Find the related exception.
        ShipmentException exception =
                exceptionRepository
                        .findById(investigation.getExceptionId())
                        .orElse(null);

        // Change OPEN to INVESTIGATING when the investigation is saved.
        if (exception != null
                && "OPEN".equalsIgnoreCase(exception.getStatus())) {

            exception.setStatus("INVESTIGATING");
            exceptionRepository.save(exception);
        }

        return savedInvestigation;
    }

    @Transactional
    public ExceptionInvestigation updateInvestigation(
            Integer investigationId,
            ExceptionInvestigation investigation) {

        ExceptionInvestigation existing =
                investigationRepository
                        .findById(investigationId)
                        .orElse(null);

        if (existing == null) {
            return null;
        }

        existing.setExceptionId(investigation.getExceptionId());
        existing.setInvestigatedBy(investigation.getInvestigatedBy());
        existing.setFindings(investigation.getFindings());
        existing.setRootCause(investigation.getRootCause());
        existing.setEvidence(investigation.getEvidence());
        existing.setInvestigationStatus(
                investigation.getInvestigationStatus()
        );

        if (investigation.getInvestigatedAt() != null) {
            existing.setInvestigatedAt(
                    investigation.getInvestigatedAt()
            );
        }

        return investigationRepository.save(existing);
    }
}