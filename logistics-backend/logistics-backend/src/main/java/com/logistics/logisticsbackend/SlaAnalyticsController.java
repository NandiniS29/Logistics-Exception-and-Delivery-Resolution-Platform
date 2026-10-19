package com.logistics.logisticsbackend;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sla")
@CrossOrigin(origins = "*")
public class SlaAnalyticsController {

    private final SlaAnalyticsService slaAnalyticsService;

    public SlaAnalyticsController(
            SlaAnalyticsService slaAnalyticsService) {

        this.slaAnalyticsService = slaAnalyticsService;
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>>
    getSlaAnalytics() {

        return ResponseEntity.ok(
                slaAnalyticsService.getSlaAnalytics()
        );
    }
}
