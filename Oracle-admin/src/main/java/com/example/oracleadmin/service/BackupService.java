package com.example.oracleadmin.service;

import com.example.oracleadmin.entity.BackupHistory;
import com.example.oracleadmin.entity.BackupSchedule;
import com.example.oracleadmin.repository.BackupHistoryRepository;
import com.example.oracleadmin.repository.BackupScheduleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import com.example.oracleadmin.util.RmanExecutor;

@Service
public class BackupService {

    private static final Logger logger = LoggerFactory.getLogger(BackupService.class);

    @Autowired
    private BackupHistoryRepository backupHistoryRepository;

    @Autowired
    private BackupScheduleRepository backupScheduleRepository;

    // Récupérer l'historique des sauvegardes
    public List<BackupHistory> getBackupHistory() {
        return backupHistoryRepository.findAll();
    }

    // Sauvegarder un historique de sauvegarde
    public void saveBackupHistory(BackupHistory backupHistory) {
        backupHistoryRepository.save(backupHistory);
    }

    // Planifier une sauvegarde
    public void scheduleBackup(BackupSchedule schedule) {
        backupScheduleRepository.save(schedule);
    }

    // Sauvegarde complète planifiée
    @Scheduled(cron = "0 0 0 * * ?")  // Exemple : tous les jours à minuit
    public void scheduledFullBackup() {
        executeBackup("FULL");
    }

    // Sauvegarde incrémentielle planifiée
    @Scheduled(cron = "0 0 3 * * ?")  // Exemple : sauvegarde incrémentielle tous les jours à 3h
    public void scheduledIncrementalBackup() {
        executeBackup("INCREMENTAL");
    }

    // Exécution de la sauvegarde (complète ou incrémentielle)
    @Transactional
    public void executeBackup(String backupType) {
        try {
            logger.info("Starting {} backup...", backupType);

            // Définir la commande RMAN en fonction du type de sauvegarde
            String command = defineRmanCommand(backupType);

            // Exécuter la commande RMAN
            RmanExecutor.executeCommand(command, backupHistoryRepository);
            logger.info("{} backup completed successfully.", backupType);

            // Sauvegarder l'historique de la sauvegarde réussie
            BackupHistory history = new BackupHistory();
            history.setBackupDate(LocalDateTime.now());
            history.setBackupType(backupType);
            history.setStatus("SUCCESS");
            backupHistoryRepository.save(history);
        } catch (Exception e) {
            // Sauvegarder l'historique en cas d'échec
            logger.error("Error during {} backup: {}", backupType, e.getMessage());

            BackupHistory history = new BackupHistory();
            history.setBackupDate(LocalDateTime.now());
            history.setBackupType(backupType);
            history.setStatus("FAILED");
            history.setDetails(e.getMessage());
            backupHistoryRepository.save(history);
        }
    }

    // Définir la commande RMAN en fonction du type de sauvegarde
    private String defineRmanCommand(String backupType) {
        String command;
        if ("FULL".equalsIgnoreCase(backupType)) {
            command = "rman target / cmdfile=backup_script_full.rman";
        } else if ("INCREMENTAL".equalsIgnoreCase(backupType)) {
            command = "rman target / cmdfile=backup_script_incremental.rman";
        } else {
            throw new IllegalArgumentException("Invalid backup type: " + backupType);
        }
        return command;
    }

    // Vérifier et exécuter la sauvegarde basée sur la planification définie
    public void checkAndExecuteScheduledBackups() {
        List<BackupSchedule> schedules = backupScheduleRepository.findAll();
        for (BackupSchedule schedule : schedules) {
            if (schedule.getNextRun().isBefore(LocalDateTime.now())) {
                // Exécuter la sauvegarde en fonction du type
                executeBackup(schedule.getBackupType());

                // Mettre à jour la prochaine exécution de la sauvegarde
                schedule.setNextRun(calculateNextRun(schedule.getFrequency()));
                backupScheduleRepository.save(schedule);
            }
        }
    }

    // Calculer la prochaine exécution basée sur la fréquence
    private LocalDateTime calculateNextRun(String frequency) {
        LocalDateTime now = LocalDateTime.now();
        switch (frequency.toUpperCase()) {
            case "DAILY":
                return now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0); // Fixe à minuit le jour suivant
            case "WEEKLY":
                return now.plusWeeks(1).withHour(0).withMinute(0).withSecond(0).withNano(0); // Fixe à minuit le même jour la semaine suivante
            case "MONTHLY":
                return now.plusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0); // Fixe au premier du mois suivant
            default:
                throw new IllegalArgumentException("Invalid frequency: " + frequency);
        }
    }
}
