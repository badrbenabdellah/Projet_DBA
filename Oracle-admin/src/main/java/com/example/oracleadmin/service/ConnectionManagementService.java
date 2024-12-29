package com.example.oracleadmin.service;

import com.example.oracleadmin.entity.UserConnection;
import com.example.oracleadmin.repository.UserConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

@Service
public class ConnectionManagementService {

    @Autowired
    private UserConnectionRepository userConnectionRepository;

    public void saveConnection(UserConnection connection) {
        userConnectionRepository.save(connection);
    }

    public UserConnection getConnectionByName(String connectionName) {
        return userConnectionRepository.findByConnectionName(connectionName)
                .orElseThrow(() -> new RuntimeException("Connexion introuvable : " + connectionName));
    }

    public List<UserConnection> getAllConnections() {
        return userConnectionRepository.findAll();
    }

    public void deleteConnectionByName(String connectionName) {
        userConnectionRepository.deleteByConnectionName(connectionName);
    }

    public List<UserConnection> getConnectionsByClientId(String clientId) {
        return userConnectionRepository.findAllByClientId(clientId);
    }

    public Connection createConnection(UserConnection connectionDetails) throws SQLException {
        String jdbcUrl = String.format(
                "jdbc:oracle:thin:@//%s:%s/%s",
                connectionDetails.getIp(),
                connectionDetails.getPort(),
                connectionDetails.getServiceName()
        );

        System.out.println("connection url : " + jdbcUrl);

        return DriverManager.getConnection(
                jdbcUrl,
                connectionDetails.getUsername() + " as " + connectionDetails.getRole(),
                connectionDetails.getPassword()
        );
    }
}
