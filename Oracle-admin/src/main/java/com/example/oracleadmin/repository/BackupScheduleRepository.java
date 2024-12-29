package com.example.oracleadmin.repository;

import com.example.oracleadmin.entity.BackupSchedule;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BackupScheduleRepository extends JpaRepository<BackupSchedule, Long> {

}
