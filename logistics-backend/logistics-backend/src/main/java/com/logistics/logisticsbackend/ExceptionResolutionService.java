package com.logistics.logisticsbackend;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExceptionResolutionService {

    private final ExceptionResolutionRepository resolutionRepository;
    private final ShipmentExceptionRepository exceptionRepository;

    public ExceptionResolutionService(
        ExceptionResolutionRepository resolutionRepository,
        ShipmentExceptionRepository exceptionRepository) {

    this.resolutionRepository = resolutionRepository;
    this.exceptionRepository = exceptionRepository;
}

    public List<ExceptionResolution> getAllResolutions() {
        return resolutionRepository.findAll();
    }

    public ExceptionResolution getResolutionById(
            Integer resolutionId) {

        return resolutionRepository
                .findById(resolutionId)
                .orElse(null);
    }

    public List<ExceptionResolution> getResolutionsByExceptionId(
            Integer exceptionId) {

        return resolutionRepository.findByExceptionId(exceptionId);
    }

    @Transactional
    public ExceptionResolution createResolution(
            ExceptionResolution resolution) {

        if (resolution == null || resolution.getExceptionId() == null) {
            throw new IllegalArgumentException(
                    "Exception ID is required to create a resolution."
            );
        }

        List<ExceptionResolution> existingResolutions =
                resolutionRepository.findByExceptionId(
                        resolution.getExceptionId()
                );

        // Update an existing record instead of inserting another one.
        if (existingResolutions != null
                && !existingResolutions.isEmpty()) {

            ExceptionResolution existing = existingResolutions.get(0);

            if (resolution.getResolvedBy() != null) {
                existing.setResolvedBy(resolution.getResolvedBy());
            }

            if (resolution.getResolutionType() != null) {
                existing.setResolutionType(
                        resolution.getResolutionType()
                );
            }

            if (resolution.getResolutionDescription() != null) {
                existing.setResolutionDescription(
                        resolution.getResolutionDescription()
                );
            }

            if (resolution.getCompensationRequired() != null) {
                existing.setCompensationRequired(
                        resolution.getCompensationRequired()
                );
            }

            if (resolution.getCompensationAmount() != null) {
                existing.setCompensationAmount(
                        resolution.getCompensationAmount()
                );
            }

            if (resolution.getResolutionStatus() != null) {
                existing.setResolutionStatus(
                        resolution.getResolutionStatus()
                );
            }

            // Preserve the original resolved date unless a new date is supplied.
            if (resolution.getResolvedAt() != null) {
                existing.setResolvedAt(resolution.getResolvedAt());
            }

            return resolutionRepository.save(existing);
        }

        if (resolution.getResolvedAt() == null) {
            resolution.setResolvedAt(LocalDateTime.now());
        }

        return resolutionRepository.save(resolution);
    }

    @Transactional
    public ExceptionResolution updateResolution(
            Integer resolutionId,
            ExceptionResolution resolution) {

        ExceptionResolution existing =
                resolutionRepository
                        .findById(resolutionId)
                        .orElse(null);

        if (existing == null || resolution == null) {
            return null;
        }

        // Do not allow an update to move the record to another exception.
        // The exception ID remains the one associated with this record.

        if (resolution.getResolvedBy() != null) {
            existing.setResolvedBy(resolution.getResolvedBy());
        }

        if (resolution.getResolutionType() != null) {
            existing.setResolutionType(
                    resolution.getResolutionType()
            );
        }

        if (resolution.getResolutionDescription() != null) {
            existing.setResolutionDescription(
                    resolution.getResolutionDescription()
            );
        }

        if (resolution.getCompensationRequired() != null) {
            existing.setCompensationRequired(
                    resolution.getCompensationRequired()
            );
        }

        if (resolution.getCompensationAmount() != null) {
            existing.setCompensationAmount(
                    resolution.getCompensationAmount()
            );
        }

        if (resolution.getResolutionStatus() != null) {
            existing.setResolutionStatus(
                    resolution.getResolutionStatus()
            );
        }

        if (resolution.getResolvedAt() != null) {
            existing.setResolvedAt(resolution.getResolvedAt());
        }

        return resolutionRepository.save(existing);
    }
}
