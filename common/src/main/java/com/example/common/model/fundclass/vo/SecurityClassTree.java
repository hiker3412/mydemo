package com.example.common.model.fundclass.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class SecurityClassTree {
    private String classCode;
    private String className;
    private Map<String, SecurityClassTree> subClass = new HashMap<>();
}
