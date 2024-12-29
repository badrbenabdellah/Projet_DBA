package com.example.oracleadmin.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "backup_history")
public class BackupHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "backup_date", nullable = false)
    private LocalDateTime backupDate;

    @Column(name = "backup_type", nullable = false)
    private String backupType;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "details")
    private String details;


    // Getters and Setters
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public LocalDateTime getBackupDate() {return backupDate;}
    public void setBackupDate(LocalDateTime backupDate) {this.backupDate = backupDate;}
    public String getBackupType() {return backupType;}
    public void setBackupType(String backupType) {this.backupType = backupType;}
    public String getStatus() {return status;}
    public void setStatus(String status) {this.status = status;}
    public String getDetails() {return details;}
    public void setDetails(String details) {this.details = details;}
}
