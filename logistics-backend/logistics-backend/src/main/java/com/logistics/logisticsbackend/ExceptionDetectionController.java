package com.logistics.logisticsbackend;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exception-detection")
@CrossOrigin(origins = "*")
public class ExceptionDetectionController {

    private final ExceptionDetectionService detectionService;

    public ExceptionDetectionController(
            ExceptionDetectionService detectionService) {

        this.detectionService = detectionService;
    }

    @PostMapping("/run")
    public ResponseEntity<List<ShipmentException>>
    runExceptionDetection() {

        List<ShipmentException> detectedExceptions =
                detectionService.detectExceptions();

        return ResponseEntity.ok(
                detectedExceptions
        );
    }
}
