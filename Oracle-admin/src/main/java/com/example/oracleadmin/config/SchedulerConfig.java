package com.example.oracleadmin.config;

import com.example.oracleadmin.entity.BackupSchedule;
import com.example.oracleadmin.repository.BackupScheduleRepository;
import com.example.oracleadmin.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Autowired
    private BackupService backupService;

    @Autowired
    private BackupScheduleRepository backupScheduleRepository;

    // Planification automatique des sauvegardes, vérification toutes les minutes
    @Scheduled(fixedRate = 60000)  // Vérifie toutes les 60 secondes
    public void automaticBackup() {
        System.out.println("Checking scheduled backups at " + LocalDateTime.now());

        // Récupérer toutes les planifications de sauvegarde
        List<BackupSchedule> schedules = backupScheduleRepository.findAll();

        LocalDateTime now = LocalDateTime.now();

        for (BackupSchedule schedule : schedules) {
            // Vérifier si la sauvegarde est prévue pour maintenant ou est déjà due
            if (schedule.getNextRun().isBefore(now) || schedule.getNextRun().isEqual(now)) {
                try {
                    System.out.println("Executing scheduled backup: " + schedule.getBackupType());

                    // Exécuter la sauvegarde via le service
                    backupService.executeBackup(schedule.getBackupType());

                    // Mettre à jour la prochaine exécution en fonction de la fréquence
                    LocalDateTime nextRun = calculateNextRun(schedule);
                    schedule.setNextRun(nextRun);

                    // Sauvegarder la nouvelle planification
                    backupScheduleRepository.save(schedule);

                    System.out.println("Next scheduled run for " + schedule.getBackupType() + " set to " + nextRun);

                } catch (Exception e) {
                    System.err.println("Error executing backup for schedule ID " + schedule.getId() + ": " + e.getMessage());
                    // Optionnel : Enregistrer un historique des erreurs dans la base de données ou envoyer une notification
                }
            }
        }
    }

    // Calculer la prochaine exécution basée sur la fréquence
    private LocalDateTime calculateNextRun(BackupSchedule schedule) {
        switch (schedule.getFrequency().toUpperCase()) {
            case "DAILY":
                return schedule.getNextRun().plusDays(1);
            case "WEEKLY":
                return schedule.getNextRun().plusWeeks(1);
            case "MONTHLY":
                return schedule.getNextRun().plusMonths(1);
            case "HOURLY":
                return schedule.getNextRun().plusHours(1);
            default:
                System.err.println("Unknown frequency: " + schedule.getFrequency());
                return schedule.getNextRun(); // Retourne la date actuelle si la fréquence est inconnue
        }
    }
}
