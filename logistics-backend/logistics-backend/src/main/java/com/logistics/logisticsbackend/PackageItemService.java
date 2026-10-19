package com.logistics.logisticsbackend;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class PackageItemService {

    private final PackageItemRepository packageItemRepository;

    public PackageItemService(
            PackageItemRepository packageItemRepository) {

        this.packageItemRepository = packageItemRepository;
    }

    public List<PackageItem> getAllPackages() {
        return packageItemRepository.findAll();
    }

    public PackageItem getPackageById(Integer packageId) {

        return packageItemRepository
                .findById(packageId)
                .orElse(null);
    }

    public List<PackageItem> getPackagesByShipmentId(
            Integer shipmentId) {

        return packageItemRepository
                .findByShipmentId(shipmentId);
    }

    public PackageItem getPackageByNumber(
            String packageNumber) {

        return packageItemRepository
                .findByPackageNumber(packageNumber);
    }

    public PackageItem createPackage(
            PackageItem packageItem) {

        return packageItemRepository.save(packageItem);
    }

    public PackageItem updatePackage(
            Integer packageId,
            PackageItem packageItem) {

        PackageItem existing =
                packageItemRepository
                        .findById(packageId)
                        .orElse(null);

        if (existing == null) {
            return null;
        }

        existing.setShipmentId(
                packageItem.getShipmentId());

        existing.setPackageNumber(
                packageItem.getPackageNumber());

        existing.setPackageType(
                packageItem.getPackageType());

        existing.setDescription(
                packageItem.getDescription());

        existing.setWeight(
                packageItem.getWeight());

        existing.setQuantity(
                packageItem.getQuantity());

        existing.setPackageStatus(
                packageItem.getPackageStatus());

        return packageItemRepository.save(existing);
    }

    public boolean deletePackage(Integer packageId) {

        if (!packageItemRepository
                .existsById(packageId)) {

            return false;
        }

        packageItemRepository.deleteById(packageId);

        return true;
    }
}
