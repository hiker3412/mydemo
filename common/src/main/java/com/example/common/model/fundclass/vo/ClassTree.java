package com.example.common.model.fundclass.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class ClassTree {
    private String classCode;
    private String className;
    private Map<String, ClassTree> subClass = new HashMap<>();
}
