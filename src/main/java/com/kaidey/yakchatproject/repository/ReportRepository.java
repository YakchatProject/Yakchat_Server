package com.kaidey.yakchatproject.repository;

import com.kaidey.yakchatproject.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
}