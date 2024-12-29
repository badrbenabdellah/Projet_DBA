package com.example.oracleadmin.util;


public class ConnectionParams {
    private String ip;
    private int port;
    private String serviceName;
    private String username;
    private String password;

    public ConnectionParams(String ip, int port, String serviceName, String username, String password) {
        this.ip = ip;
        this.port = port;
        this.serviceName = serviceName;
        this.username = username;
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public String getServiceName() {
        return serviceName;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

}