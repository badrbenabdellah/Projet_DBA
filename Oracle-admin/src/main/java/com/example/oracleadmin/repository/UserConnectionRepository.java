package com.example.oracleadmin.repository;

import com.example.oracleadmin.entity.UserConnection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserConnectionRepository extends JpaRepository<UserConnection, Long> {
    Optional<UserConnection> findByConnectionName(String connectionName);
    void deleteByConnectionName(String connectionName);
    List<UserConnection> findAllByClientId(String clientId);
}
