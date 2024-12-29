package com.example.oracleadmin.service;

import com.example.oracleadmin.config.DBConfig;
import com.example.oracleadmin.util.ConnectionParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
//@RequiredArgsConstructor
public class OracleUserService {

    @Autowired
    private DBConfig dbConfig;

    // Méthode générique pour obtenir JdbcTemplate
    private JdbcTemplate createJdbcTemplate(ConnectionParams params) {
        return dbConfig.createJdbcTemplate(params);
    }

    public void createUser(ConnectionParams params, String newUsername, String newPassword) {
        JdbcTemplate jdbcTemplate = createJdbcTemplate(params);

        // Échapper les apostrophes dans le nom d'utilisateur et mot de passe
        String username = newUsername.replace("'", "''");
        String password = newPassword.replace("'", "''");

        // Construction de la requête SQL avec un mot de passe correctement échappé
        String sql = String.format("CREATE USER %s IDENTIFIED BY \"%s\"",
                username, password);

        System.out.println("Executing SQL: " + sql);

        jdbcTemplate.execute(sql); // Exécuter la requête SQL
    }



    public void modifyUserPassword(ConnectionParams params, String targetUsername, String newPassword) {
        JdbcTemplate jdbcTemplate = createJdbcTemplate(params);

        String sql = String.format("ALTER USER %s IDENTIFIED BY '%s'",
                targetUsername.replace("'", "''"),
                newPassword.replace("'", "''"));

        jdbcTemplate.execute(sql);
    }


    public void deleteUser(ConnectionParams params, String targetUsername) {
        JdbcTemplate jdbcTemplate = createJdbcTemplate(params);

        String sql = String.format("DROP USER %s CASCADE",
                targetUsername.replace("'", "''"));

        jdbcTemplate.execute(sql);
    }


    public void assignRole(ConnectionParams params, String targetUsername, String role) {
        JdbcTemplate jdbcTemplate = createJdbcTemplate(params);
        String sql = "GRANT ? TO ?";
        jdbcTemplate.update(sql, role, targetUsername);
    }

    public void revokeRole(ConnectionParams params, String targetUsername, String role) {
        JdbcTemplate jdbcTemplate = createJdbcTemplate(params);
        String sql = "REVOKE ? FROM ?";
        jdbcTemplate.update(sql, role, targetUsername);
    }

    public void setQuota(ConnectionParams params, String targetUsername, String tablespace, int quotaSizeMb) {
        JdbcTemplate jdbcTemplate = createJdbcTemplate(params);
        String sql = "ALTER USER ? QUOTA ?M ON ?";
        jdbcTemplate.update(sql, targetUsername, quotaSizeMb, tablespace);
    }

    public void setPasswordPolicy(ConnectionParams params, String targetUsername, boolean expirePassword, boolean lockAccount) {
        JdbcTemplate jdbcTemplate = createJdbcTemplate(params);

        if (expirePassword) {
            String expireSql = "ALTER USER ? PASSWORD EXPIRE";
            jdbcTemplate.update(expireSql, targetUsername);
        }

        if (lockAccount) {
            String lockSql = "ALTER USER ? ACCOUNT LOCK";
            jdbcTemplate.update(lockSql, targetUsername);
        } else {
            String unlockSql = "ALTER USER ? ACCOUNT UNLOCK";
            jdbcTemplate.update(unlockSql, targetUsername);
        }
    }
}
