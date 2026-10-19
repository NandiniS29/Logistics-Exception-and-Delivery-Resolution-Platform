package com.logistics.logisticsbackend;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsigneeRepository
        extends JpaRepository<Consignee, Integer> {

    List<Consignee> findByCustomerId(Integer customerId);
}
