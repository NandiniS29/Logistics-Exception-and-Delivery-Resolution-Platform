package com.logistics.logisticsbackend;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/investigations")
@CrossOrigin(origins = "*")
public class ExceptionInvestigationController {

    private final ExceptionInvestigationService investigationService;

    public ExceptionInvestigationController(
            ExceptionInvestigationService investigationService) {
        this.investigationService = investigationService;
    }

    @GetMapping
    public ResponseEntity<List<ExceptionInvestigation>>
    getAllInvestigations() {

        return ResponseEntity.ok(
                investigationService.getAllInvestigations()
        );
    }

    @GetMapping("/{investigationId}")
    public ResponseEntity<?> getInvestigationById(
            @PathVariable Integer investigationId) {

        ExceptionInvestigation investigation =
                investigationService
                        .getInvestigationById(investigationId);

        if (investigation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Investigation not found.");
        }

        return ResponseEntity.ok(investigation);
    }

    @GetMapping("/exception/{exceptionId}")
    public ResponseEntity<List<ExceptionInvestigation>>
    getInvestigationsByExceptionId(
            @PathVariable Integer exceptionId) {

        return ResponseEntity.ok(
                investigationService
                        .getInvestigationsByExceptionId(exceptionId)
        );
    }

    @PostMapping
    public ResponseEntity<ExceptionInvestigation>
    createInvestigation(
            @RequestBody ExceptionInvestigation investigation) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        investigationService
                                .createInvestigation(investigation)
                );
    }

    @PutMapping("/{investigationId}")
    public ResponseEntity<?> updateInvestigation(
            @PathVariable Integer investigationId,
            @RequestBody ExceptionInvestigation investigation) {

        ExceptionInvestigation updated =
                investigationService.updateInvestigation(
                        investigationId,
                        investigation
                );

        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Investigation not found.");
        }

        return ResponseEntity.ok(updated);
    }
}
