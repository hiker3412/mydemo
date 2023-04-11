package com.example.common.model.fundclass.vo;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class SecurityClassTree {
    private String classCode;
    private String className;
    private Map<String, SecurityClassTree> subClass = new HashMap<>();
}
