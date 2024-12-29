package com.example.oracleadmin.controller;

import com.example.oracleadmin.service.OracleUserService;
import com.example.oracleadmin.util.ConnectionParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private OracleUserService oracleUserService;

    @GetMapping("/test")
    public String test(){
        ConnectionParams params = new ConnectionParams(
                "172.17.0.2",
                1521,
                "free",
                "sys as sysdba",
                "12ze34RT"
        );

        oracleUserService.createUser(params, "Mohamed", "alinewaccount0");
        return "user created";
    }

    @GetMapping("/drop/user")
    public String dropUser(){
        ConnectionParams params = new ConnectionParams(
                "172.17.0.2",
                1521,
                "free",
                "sys as sysdba",
                "12ze34RT"
        );

        oracleUserService.deleteUser(params, "C##Ali");
        return "user deleted";
    }
}
