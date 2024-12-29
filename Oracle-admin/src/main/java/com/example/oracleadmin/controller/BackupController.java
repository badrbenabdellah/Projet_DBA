package com.example.oracleadmin.controller;

import com.example.oracleadmin.entity.BackupHistory;
import com.example.oracleadmin.entity.BackupSchedule;
import com.example.oracleadmin.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/backup")
public class BackupController {

    @Autowired
    private BackupService backupService;

    // Récupérer l'historique des sauvegardes
    @GetMapping("/history")
    public ResponseEntity<List<BackupHistory>> getBackupHistory() {
        List<BackupHistory> historyList = backupService.getBackupHistory();
        if (historyList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(historyList);
    }

    // Planifier une sauvegarde
    @PostMapping("/schedule")
    public ResponseEntity<String> scheduleBackup(@RequestBody BackupSchedule schedule) {
        try {
            // Vérification de la validité de la fréquence et du type de sauvegarde
            if (schedule.getBackupType() == null || schedule.getFrequency() == null) {
                return ResponseEntity.badRequest().body("Backup type and frequency are required.");
            }

            // Sauvegarder la planification de la sauvegarde
            backupService.scheduleBackup(schedule);
            return ResponseEntity.ok("Backup schedule saved successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving backup schedule: " + e.getMessage());
        }
    }

    // Vérifier et exécuter les sauvegardes planifiées
    @GetMapping("/execute-scheduled")
    public ResponseEntity<String> executeScheduledBackups() {
        try {
            backupService.checkAndExecuteScheduledBackups();
            return ResponseEntity.ok("Scheduled backups executed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error executing scheduled backups: " + e.getMessage());
        }
    }

    // Exécuter une sauvegarde manuellement
    @PostMapping("/execute/{backupType}")
    public ResponseEntity<String> executeBackup(@PathVariable String backupType) {
        try {
            if (!"FULL".equalsIgnoreCase(backupType) && !"INCREMENTAL".equalsIgnoreCase(backupType)) {
                return ResponseEntity.badRequest().body("Invalid backup type. Use 'FULL' or 'INCREMENTAL'.");
            }

            // Exécuter la sauvegarde manuelle
            backupService.executeBackup(backupType);
            return ResponseEntity.ok(backupType + " backup executed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error executing " + backupType + " backup: " + e.getMessage());
        }
    }
}
