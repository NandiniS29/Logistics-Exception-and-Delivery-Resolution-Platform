package com.logistics.logisticsbackend;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ShipmentExceptionService {

    private final ShipmentExceptionRepository exceptionRepository;

    public ShipmentExceptionService(
            ShipmentExceptionRepository exceptionRepository) {
        this.exceptionRepository = exceptionRepository;
    }

    public List<ShipmentException> getAllExceptions() {
        return exceptionRepository.findAll();
    }

    public ShipmentException getExceptionById(Integer exceptionId) {
        return exceptionRepository.findById(exceptionId).orElse(null);
    }

    public List<ShipmentException> getExceptionsByShipmentId(
            Integer shipmentId) {

        return exceptionRepository.findByShipmentId(shipmentId);
    }

    public List<ShipmentException> getExceptionsByStatus(
            String status) {

        return exceptionRepository.findByStatus(status);
    }

    public ShipmentException createException(
            ShipmentException exception) {

        return exceptionRepository.save(exception);
    }

    public ShipmentException updateException(
            Integer exceptionId,
            ShipmentException exception) {

        ShipmentException existing =
                exceptionRepository.findById(exceptionId).orElse(null);

        if (existing == null) {
            return null;
        }

        existing.setShipmentId(exception.getShipmentId());
        existing.setExceptionType(exception.getExceptionType());
        existing.setDescription(exception.getDescription());
        existing.setPriority(exception.getPriority());
        existing.setStatus(exception.getStatus());
        existing.setReportedDate(exception.getReportedDate());

        return exceptionRepository.save(existing);
    }

    public boolean deleteException(Integer exceptionId) {

        if (!exceptionRepository.existsById(exceptionId)) {
            return false;
        }

        exceptionRepository.deleteById(exceptionId);
        return true;
    }
}
