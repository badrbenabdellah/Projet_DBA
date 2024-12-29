package com.example.oracleadmin.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "backup_schedule")
public class BackupSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "backup_type", nullable = false)
    private String backupType;

    @Column(name = "frequency", nullable = false)
    private String frequency;

    @Column(name = "next_run", nullable = false)
    private LocalDateTime nextRun;


    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getBackupType() {
        return backupType;
    }
    public void setBackupType(String backupType) {
        this.backupType = backupType;
    }
    public String getFrequency() {
        return frequency;
    }
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
    public LocalDateTime getNextRun() {
        return nextRun;
    }
    public void setNextRun(LocalDateTime nextRun) {
        this.nextRun = nextRun;
    }
}
