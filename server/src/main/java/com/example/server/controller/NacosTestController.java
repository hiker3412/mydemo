package com.example.server.controller;

import com.example.server.config.MydemoConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
public class NacosTestController {
    @Autowired
    private MydemoConfigProperties mydemoConfigProperties;

    @RequestMapping("/get")
    public String get() {
        return mydemoConfigProperties.getNacosTest();
    }
}
