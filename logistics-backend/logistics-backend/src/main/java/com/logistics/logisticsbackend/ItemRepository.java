package com.logistics.logisticsbackend;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository
        extends JpaRepository<Item, Integer> {

    List<Item> findByPackageId(Integer packageId);
}