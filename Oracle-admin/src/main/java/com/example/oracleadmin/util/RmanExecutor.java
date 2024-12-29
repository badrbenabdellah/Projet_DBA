package com.example.oracleadmin.util;

import com.example.oracleadmin.entity.BackupHistory;
import com.example.oracleadmin.repository.BackupHistoryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

public class RmanExecutor {

    public static void executeCommand(String command, BackupHistoryRepository backupHistoryRepository) throws Exception {
        Process process = null;
        BufferedReader reader = null;
        BufferedReader errorReader = null;

        try {
            // Exécuter la commande RMAN
            process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor(); // Attendre que le processus se termine

            // Lire la sortie standard
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Lire les erreurs
            errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder errorOutput = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }

            // Si le processus échoue, lever une exception avec les détails des erreurs
            if (exitCode != 0) {
                String errorMessage = "RMAN command failed with exit code " + exitCode + ": " + errorOutput.toString();
                System.err.println(errorMessage); // Affichage des erreurs dans la console
                throw new Exception(errorMessage);
            }

            // Affichage de la sortie pour déboguer
            System.out.println("RMAN Output:\n" + output.toString());

            // Enregistrer l'historique de la sauvegarde dans la base de données
            BackupHistory history = new BackupHistory();
            history.setBackupDate(LocalDateTime.now());
            history.setBackupType("Full"); // Ajuster selon le type de sauvegarde
            history.setStatus("SUCCESS");
            backupHistoryRepository.save(history);

        } catch (Exception e) {
            // En cas d'exception, enregistrer l'erreur dans l'historique des sauvegardes
            BackupHistory history = new BackupHistory();
            history.setBackupDate(LocalDateTime.now());
            history.setBackupType("Full"); // Ajuster selon le type de sauvegarde
            history.setStatus("FAILED");
            history.setDetails(e.getMessage()); // Enregistrer les détails de l'erreur
            backupHistoryRepository.save(history);
            throw e; // Relancer l'exception après avoir enregistré l'erreur
        } finally {
            // Assurez-vous de fermer les flux
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    System.err.println("Error closing reader: " + e.getMessage());
                }
            }
            if (errorReader != null) {
                try {
                    errorReader.close();
                } catch (Exception e) {
                    System.err.println("Error closing errorReader: " + e.getMessage());
                }
            }
        }
    }

    // Exemple d'exécution d'une sauvegarde avec le type de sauvegarde spécifié
    public static void executeBackup(String backupType, BackupHistoryRepository backupHistoryRepository) {
        String command = "rman target / cmdfile=backup_script_full.rman";
        if (backupType.equalsIgnoreCase("INCREMENTAL")) {
            command = "rman target / cmdfile=incremental_backup_script.rman";
        }
        try {
            executeCommand(command, backupHistoryRepository);
        } catch (Exception e) {
            // Si la commande échoue, l'erreur est déjà gérée dans executeCommand.
            System.err.println("Backup execution failed for type: " + backupType);
        }
    }
}
