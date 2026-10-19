package com.logistics.logisticsbackend;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "shipment_exceptions")
public class ShipmentException {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exception_id")
    private Integer exceptionId;

    @Column(name = "shipment_id", nullable = false)
    private Integer shipmentId;

    @Column(name = "exception_type", nullable = false, length = 100)
    private String exceptionType;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "priority", nullable = false, length = 20)
    private String priority;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "reported_date", nullable = false)
    private LocalDateTime reportedDate;

    public ShipmentException() {
    }

    @PrePersist
    public void onCreate() {
        if (reportedDate == null) {
            reportedDate = LocalDateTime.now();
        }

        if (status == null) {
            status = "OPEN";
        }
    }

    public Integer getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(Integer exceptionId) {
        this.exceptionId = exceptionId;
    }

    public Integer getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Integer shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getReportedDate() {
        return reportedDate;
    }

    public void setReportedDate(LocalDateTime reportedDate) {
        this.reportedDate = reportedDate;
    }
}
