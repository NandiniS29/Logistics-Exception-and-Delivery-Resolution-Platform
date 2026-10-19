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
@Table(name = "exception_resolutions")
public class ExceptionResolution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resolution_id")
    private Integer resolutionId;

    @Column(name = "exception_id", nullable = false)
    private Integer exceptionId;

    @Column(name = "resolved_by", nullable = false)
    private Integer resolvedBy;

    @Column(name = "resolution_type", nullable = false, length = 100)
    private String resolutionType;

    @Column(name = "resolution_description", length = 1000)
    private String resolutionDescription;

    @Column(name = "compensation_required", nullable = false)
    private Boolean compensationRequired;

    @Column(name = "compensation_amount")
    private Double compensationAmount;

    @Column(name = "resolution_status", nullable = false, length = 30)
    private String resolutionStatus;

    @Column(name = "resolved_at", nullable = false)
    private LocalDateTime resolvedAt;

    public ExceptionResolution() {
    }

    @PrePersist
    public void onCreate() {
        if (resolvedAt == null) {
            resolvedAt = LocalDateTime.now();
        }

        if (resolutionStatus == null) {
            resolutionStatus = "RESOLVED";
        }

        if (compensationRequired == null) {
            compensationRequired = false;
        }
    }

    public Integer getResolutionId() {
        return resolutionId;
    }

    public void setResolutionId(Integer resolutionId) {
        this.resolutionId = resolutionId;
    }

    public Integer getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(Integer exceptionId) {
        this.exceptionId = exceptionId;
    }

    public Integer getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(Integer resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    public String getResolutionType() {
        return resolutionType;
    }

    public void setResolutionType(String resolutionType) {
        this.resolutionType = resolutionType;
    }

    public String getResolutionDescription() {
        return resolutionDescription;
    }

    public void setResolutionDescription(String resolutionDescription) {
        this.resolutionDescription = resolutionDescription;
    }

    public Boolean getCompensationRequired() {
        return compensationRequired;
    }

    public void setCompensationRequired(Boolean compensationRequired) {
        this.compensationRequired = compensationRequired;
    }

    public Double getCompensationAmount() {
        return compensationAmount;
    }

    public void setCompensationAmount(Double compensationAmount) {
        this.compensationAmount = compensationAmount;
    }

    public String getResolutionStatus() {
        return resolutionStatus;
    }

    public void setResolutionStatus(String resolutionStatus) {
        this.resolutionStatus = resolutionStatus;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
