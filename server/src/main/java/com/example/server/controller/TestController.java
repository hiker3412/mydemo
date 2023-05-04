package com.example.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@ResponseBody
@RequestMapping("/test")
@Slf4j
public class TestController {

    @GetMapping("/arrayParam")
    public Object arrayParam(String[] aList){
        log.info("收到的参数是：");
        Arrays.stream(aList).forEach(System.out::println);
        return aList;
    }
}
