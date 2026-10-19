package com.logistics.logisticsbackend;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "packages")
public class PackageItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    private Integer packageId;

    @Column(name = "shipment_id", nullable = false)
    private Integer shipmentId;

    @Column(name = "package_number", nullable = false, unique = true, length = 50)
    private String packageNumber;

    @Column(name = "package_type", length = 50)
    private String packageType;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "package_status", nullable = false, length = 30)
    private String packageStatus;

    public PackageItem() {
    }

    @PrePersist
    public void onCreate() {

        if (packageStatus == null) {
            packageStatus = "PACKED";
        }

        if (quantity == null) {
            quantity = 1;
        }
    }

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    public Integer getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Integer shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getPackageNumber() {
        return packageNumber;
    }

    public void setPackageNumber(String packageNumber) {
        this.packageNumber = packageNumber;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getPackageStatus() {
        return packageStatus;
    }

    public void setPackageStatus(String packageStatus) {
        this.packageStatus = packageStatus;
    }
}
