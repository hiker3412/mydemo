package com.example.server.controller;

import com.example.server.feign.server2.TestClient;
import feign.Request;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@ResponseBody
@RequestMapping("/test")
@Slf4j
public class TestController {
    @Resource
    private TestClient testClient;

    @GetMapping("/arrayParam")
    public Object arrayParam(String[] aList){
        log.info("收到的参数是：");
        Arrays.stream(aList).forEach(System.out::println);
        return aList;
    }

    @GetMapping("/feign")
    public String feign(String clientId){
        switch (clientId){
            case "3" :
                return testClient.sleep3(new String[]{"3"});
            case "6":
                return testClient.sleep6(new String[]{"6"},new Request.Options(2, TimeUnit.SECONDS,60,TimeUnit.SECONDS,true));
            default:
                return "0";
        }
    }
}
