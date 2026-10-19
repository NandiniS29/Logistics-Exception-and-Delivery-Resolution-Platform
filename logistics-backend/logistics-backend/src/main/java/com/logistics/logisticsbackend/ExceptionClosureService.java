package com.logistics.logisticsbackend;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExceptionClosureService {

    private final ExceptionClosureRepository closureRepository;
    private final ShipmentExceptionRepository exceptionRepository;

    public ExceptionClosureService(
            ExceptionClosureRepository closureRepository,
            ShipmentExceptionRepository exceptionRepository) {

        this.closureRepository = closureRepository;
        this.exceptionRepository = exceptionRepository;
    }

    public List<ExceptionClosure> getAllClosures() {
        return closureRepository.findAll();
    }

    public ExceptionClosure getClosureById(Integer closureId) {
        return closureRepository
                .findById(closureId)
                .orElse(null);
    }

    public List<ExceptionClosure> getClosuresByExceptionId(
            Integer exceptionId) {

        return closureRepository.findByExceptionId(exceptionId);
    }

    @Transactional
    public ExceptionClosure createClosure(ExceptionClosure closure) {

        if (closure == null || closure.getExceptionId() == null) {
            throw new IllegalArgumentException(
                    "Exception ID is required to close an exception."
            );
        }

        Integer exceptionId = closure.getExceptionId();

        ShipmentException exception =
                exceptionRepository.findById(exceptionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Exception not found: " + exceptionId
                                ));

        // Do not create another closure for an already-closed exception.
        List<ExceptionClosure> existingClosures =
                closureRepository.findByExceptionId(exceptionId);

        if (existingClosures != null && !existingClosures.isEmpty()) {
            return existingClosures.get(0);
        }

        if ("CLOSED".equalsIgnoreCase(exception.getStatus())) {
            throw new IllegalStateException(
                    "This exception is already CLOSED. "
                    + "No new closure record was created."
            );
        }

        if (closure.getFinalStatus() == null
                || closure.getFinalStatus().isBlank()) {
            closure.setFinalStatus("CLOSED");
        }

        if (!"CLOSED".equalsIgnoreCase(closure.getFinalStatus())) {
            throw new IllegalArgumentException(
                    "Final status must be CLOSED."
            );
        }

        if (closure.getClosedAt() == null) {
            closure.setClosedAt(LocalDateTime.now());
        }

        ExceptionClosure savedClosure =
                closureRepository.save(closure);

        exception.setStatus("CLOSED");
        exceptionRepository.save(exception);

        return savedClosure;
    }

    @Transactional
    public ExceptionClosure updateClosure(
            Integer closureId,
            ExceptionClosure closure) {

        if (closure == null) {
            return null;
        }

        ExceptionClosure existing =
                closureRepository.findById(closureId)
                        .orElse(null);

        if (existing == null) {
            return null;
        }

        // Do not allow an update to link this closure to another exception.
        if (closure.getExceptionId() != null
                && !closure.getExceptionId().equals(
                        existing.getExceptionId())) {
            throw new IllegalArgumentException(
                    "A closure cannot be reassigned to another exception."
            );
        }

        if (closure.getClosedBy() != null) {
            existing.setClosedBy(closure.getClosedBy());
        }

        if (closure.getClosureReason() != null) {
            existing.setClosureReason(closure.getClosureReason());
        }

        if (closure.getFinalStatus() != null) {
            if (!"CLOSED".equalsIgnoreCase(
                    closure.getFinalStatus())) {
                throw new IllegalArgumentException(
                        "Final status must be CLOSED."
                );
            }
            existing.setFinalStatus("CLOSED");
        }

        if (closure.getClosedAt() != null) {
            existing.setClosedAt(closure.getClosedAt());
        }

        ExceptionClosure updated =
                closureRepository.save(existing);

        ShipmentException exception =
                exceptionRepository.findById(
                        existing.getExceptionId()
                ).orElse(null);

        if (exception != null) {
            exception.setStatus("CLOSED");
            exceptionRepository.save(exception);
        }

        return updated;
    }
}
