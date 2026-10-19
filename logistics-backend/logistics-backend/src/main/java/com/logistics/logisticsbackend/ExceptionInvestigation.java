package com.logistics.logisticsbackend;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "exception_investigations")
public class ExceptionInvestigation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer investigationId;

    private Integer exceptionId;

    private Integer investigatedBy;

    private String findings;

    private String rootCause;

    private String evidence;

    private String communication;

    private String investigationNotes;

    private String investigationStatus;

    private LocalDateTime investigatedAt;


    // -----------------------------
    // Getters and Setters
    // -----------------------------

    public Integer getInvestigationId() {
        return investigationId;
    }

    public void setInvestigationId(Integer investigationId) {
        this.investigationId = investigationId;
    }


    public Integer getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(Integer exceptionId) {
        this.exceptionId = exceptionId;
    }


    public Integer getInvestigatedBy() {
        return investigatedBy;
    }

    public void setInvestigatedBy(Integer investigatedBy) {
        this.investigatedBy = investigatedBy;
    }


    public String getFindings() {
        return findings;
    }

    public void setFindings(String findings) {
        this.findings = findings;
    }


    public String getRootCause() {
        return rootCause;
    }

    public void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }


    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }


    public String getCommunication() {
        return communication;
    }

    public void setCommunication(String communication) {
        this.communication = communication;
    }


    public String getInvestigationNotes() {
        return investigationNotes;
    }

    public void setInvestigationNotes(String investigationNotes) {
        this.investigationNotes = investigationNotes;
    }


    public String getInvestigationStatus() {
        return investigationStatus;
    }

    public void setInvestigationStatus(String investigationStatus) {
        this.investigationStatus = investigationStatus;
    }


    public LocalDateTime getInvestigatedAt() {
        return investigatedAt;
    }

    public void setInvestigatedAt(LocalDateTime investigatedAt) {
        this.investigatedAt = investigatedAt;
    }


    // -----------------------------
    // Automatically set date
    // -----------------------------

    @PrePersist
    public void onCreate() {

        if (investigatedAt == null) {
            investigatedAt = LocalDateTime.now();
        }
    }
}
