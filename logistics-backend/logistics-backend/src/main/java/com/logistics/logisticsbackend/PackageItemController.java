package com.logistics.logisticsbackend;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/packages")
@CrossOrigin(origins = "*")
public class PackageItemController {

    private final PackageItemService packageItemService;

    public PackageItemController(
            PackageItemService packageItemService) {

        this.packageItemService = packageItemService;
    }

    @GetMapping
    public ResponseEntity<List<PackageItem>>
    getAllPackages() {

        return ResponseEntity.ok(
                packageItemService.getAllPackages()
        );
    }

    @GetMapping("/{packageId}")
    public ResponseEntity<?> getPackageById(
            @PathVariable Integer packageId) {

        PackageItem packageItem =
                packageItemService
                        .getPackageById(packageId);

        if (packageItem == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Package not found.");
        }

        return ResponseEntity.ok(packageItem);
    }

    @GetMapping("/shipment/{shipmentId}")
    public ResponseEntity<List<PackageItem>>
    getPackagesByShipmentId(
            @PathVariable Integer shipmentId) {

        return ResponseEntity.ok(
                packageItemService
                        .getPackagesByShipmentId(shipmentId)
        );
    }

    @GetMapping("/number/{packageNumber}")
    public ResponseEntity<?> getPackageByNumber(
            @PathVariable String packageNumber) {

        PackageItem packageItem =
                packageItemService
                        .getPackageByNumber(packageNumber);

        if (packageItem == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Package not found.");
        }

        return ResponseEntity.ok(packageItem);
    }

    @PostMapping
    public ResponseEntity<PackageItem>
    createPackage(
            @RequestBody PackageItem packageItem) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        packageItemService
                                .createPackage(packageItem)
                );
    }

    @PutMapping("/{packageId}")
    public ResponseEntity<?> updatePackage(
            @PathVariable Integer packageId,
            @RequestBody PackageItem packageItem) {

        PackageItem updated =
                packageItemService.updatePackage(
                        packageId,
                        packageItem
                );

        if (updated == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Package not found.");
        }

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{packageId}")
    public ResponseEntity<?> deletePackage(
            @PathVariable Integer packageId) {

        boolean deleted =
                packageItemService
                        .deletePackage(packageId);

        if (!deleted) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Package not found.");
        }

        return ResponseEntity.ok(
                "Package deleted successfully."
        );
    }
}
