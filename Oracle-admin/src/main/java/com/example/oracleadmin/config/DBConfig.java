package com.example.oracleadmin.config;

import com.example.oracleadmin.util.ConnectionParams;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DBConfig {

    private  DataSource dynamicDataSource(String ip, int port, String serviceName, String username, String password) {
        String url = "jdbc:oracle:thin:@//" + ip + ":" + port + "/" + serviceName;
        System.out.println("db url " + url);
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    public JdbcTemplate createJdbcTemplate(ConnectionParams params) {
        DataSource dataSource = dynamicDataSource(
                params.getIp(),
                params.getPort(),
                params.getServiceName(),
                params.getUsername(),
                params.getPassword()
        );
        return new JdbcTemplate(dataSource);
    }

}

