package com.logistics.logisticsbackend;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExceptionClosureRepository
        extends JpaRepository<ExceptionClosure, Integer> {

    List<ExceptionClosure> findByExceptionId(Integer exceptionId);
}
