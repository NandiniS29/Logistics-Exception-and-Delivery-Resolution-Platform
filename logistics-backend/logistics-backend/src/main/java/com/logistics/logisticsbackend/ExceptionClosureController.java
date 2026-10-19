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
@RequestMapping("/api/closures")
@CrossOrigin(origins = "*")
public class ExceptionClosureController {

    private final ExceptionClosureService closureService;

    public ExceptionClosureController(
            ExceptionClosureService closureService) {
        this.closureService = closureService;
    }

    @GetMapping
    public ResponseEntity<List<ExceptionClosure>>
    getAllClosures() {

        return ResponseEntity.ok(
                closureService.getAllClosures()
        );
    }

    @GetMapping("/{closureId}")
    public ResponseEntity<?> getClosureById(
            @PathVariable Integer closureId) {

        ExceptionClosure closure =
                closureService.getClosureById(closureId);

        if (closure == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Closure not found.");
        }

        return ResponseEntity.ok(closure);
    }

    @GetMapping("/exception/{exceptionId}")
    public ResponseEntity<List<ExceptionClosure>>
    getClosuresByExceptionId(
            @PathVariable Integer exceptionId) {

        return ResponseEntity.ok(
                closureService
                        .getClosuresByExceptionId(exceptionId)
        );
    }

    @PostMapping
    public ResponseEntity<ExceptionClosure>
    createClosure(
            @RequestBody ExceptionClosure closure) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        closureService.createClosure(closure)
                );
    }

    @PutMapping("/{closureId}")
    public ResponseEntity<?> updateClosure(
            @PathVariable Integer closureId,
            @RequestBody ExceptionClosure closure) {

        ExceptionClosure updated =
                closureService.updateClosure(
                        closureId,
                        closure
                );

        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Closure not found.");
        }

        return ResponseEntity.ok(updated);
    }
}
