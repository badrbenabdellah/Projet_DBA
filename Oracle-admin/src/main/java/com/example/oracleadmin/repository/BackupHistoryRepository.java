package com.example.oracleadmin.repository;

import com.example.oracleadmin.entity.BackupHistory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BackupHistoryRepository extends JpaRepository<BackupHistory, Long> {
}


