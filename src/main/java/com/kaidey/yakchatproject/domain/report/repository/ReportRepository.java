package com.kaidey.yakchatproject.domain.report.repository;

import com.kaidey.yakchatproject.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
}