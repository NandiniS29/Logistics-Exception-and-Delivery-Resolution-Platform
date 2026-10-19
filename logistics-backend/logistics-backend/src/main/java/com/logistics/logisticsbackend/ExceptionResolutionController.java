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
@RequestMapping("/api/resolutions")
@CrossOrigin(origins = "*")
public class ExceptionResolutionController {

    private final ExceptionResolutionService resolutionService;

    public ExceptionResolutionController(
            ExceptionResolutionService resolutionService) {
        this.resolutionService = resolutionService;
    }

    @GetMapping
    public ResponseEntity<List<ExceptionResolution>>
    getAllResolutions() {

        return ResponseEntity.ok(
                resolutionService.getAllResolutions()
        );
    }

    @GetMapping("/{resolutionId}")
    public ResponseEntity<?> getResolutionById(
            @PathVariable Integer resolutionId) {

        ExceptionResolution resolution =
                resolutionService.getResolutionById(resolutionId);

        if (resolution == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Resolution not found.");
        }

        return ResponseEntity.ok(resolution);
    }

    @GetMapping("/exception/{exceptionId}")
    public ResponseEntity<List<ExceptionResolution>>
    getResolutionsByExceptionId(
            @PathVariable Integer exceptionId) {

        return ResponseEntity.ok(
                resolutionService
                        .getResolutionsByExceptionId(exceptionId)
        );
    }

    @PostMapping
    public ResponseEntity<ExceptionResolution>
    createResolution(
            @RequestBody ExceptionResolution resolution) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        resolutionService
                                .createResolution(resolution)
                );
    }

    @PutMapping("/{resolutionId}")
    public ResponseEntity<?> updateResolution(
            @PathVariable Integer resolutionId,
            @RequestBody ExceptionResolution resolution) {

        ExceptionResolution updated =
                resolutionService.updateResolution(
                        resolutionId,
                        resolution
                );

        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Resolution not found.");
        }

        return ResponseEntity.ok(updated);
    }
}
