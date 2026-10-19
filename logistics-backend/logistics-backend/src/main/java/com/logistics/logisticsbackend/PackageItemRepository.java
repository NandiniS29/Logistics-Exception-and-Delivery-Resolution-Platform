package com.logistics.logisticsbackend;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageItemRepository
        extends JpaRepository<PackageItem, Integer> {

    List<PackageItem> findByShipmentId(Integer shipmentId);

    PackageItem findByPackageNumber(String packageNumber);
}
