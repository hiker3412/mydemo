package com.example.server2.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Arrays;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @RequestMapping("/sleep3")
    public String sleep3(@RequestBody String[] param) {
        try {
            Thread.sleep(Duration.ofSeconds(3).toMillis());
        } catch (InterruptedException e) {
            log.error("",e);
        }
        return param[0];
    }

    @RequestMapping("/sleep6")
    public String sleep6(@RequestBody String[] param) {
        String sre = "l";
        try {
            Thread.sleep(Duration.ofSeconds(6).toMillis());
        } catch (InterruptedException e) {
            log.error("",e);
        }
        return param[0];
    }
}
