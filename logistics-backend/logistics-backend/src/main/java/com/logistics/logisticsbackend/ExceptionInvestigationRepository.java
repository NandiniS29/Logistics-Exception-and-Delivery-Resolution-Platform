package com.logistics.logisticsbackend;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExceptionInvestigationRepository
        extends JpaRepository<ExceptionInvestigation, Integer> {

    List<ExceptionInvestigation> findByExceptionId(Integer exceptionId);
}
