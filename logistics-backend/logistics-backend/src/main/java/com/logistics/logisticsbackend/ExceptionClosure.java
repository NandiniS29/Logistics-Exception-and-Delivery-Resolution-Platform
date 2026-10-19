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
@Table(name = "exception_closures")
public class ExceptionClosure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "closure_id")
    private Integer closureId;

    @Column(name = "exception_id", nullable = false)
    private Integer exceptionId;

    @Column(name = "closed_by", nullable = false)
    private Integer closedBy;

    @Column(name = "closure_reason", nullable = false, length = 1000)
    private String closureReason;

    @Column(name = "final_status", nullable = false, length = 30)
    private String finalStatus;

    @Column(name = "closed_at", nullable = false)
    private LocalDateTime closedAt;

    public ExceptionClosure() {
    }

    @PrePersist
    public void onCreate() {
        if (closedAt == null) {
            closedAt = LocalDateTime.now();
        }

        if (finalStatus == null) {
            finalStatus = "CLOSED";
        }
    }

    public Integer getClosureId() {
        return closureId;
    }

    public void setClosureId(Integer closureId) {
        this.closureId = closureId;
    }

    public Integer getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(Integer exceptionId) {
        this.exceptionId = exceptionId;
    }

    public Integer getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(Integer closedBy) {
        this.closedBy = closedBy;
    }

    public String getClosureReason() {
        return closureReason;
    }

    public void setClosureReason(String closureReason) {
        this.closureReason = closureReason;
    }

    public String getFinalStatus() {
        return finalStatus;
    }

    public void setFinalStatus(String finalStatus) {
        this.finalStatus = finalStatus;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }
}
