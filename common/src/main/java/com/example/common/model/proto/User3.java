package com.example.common.model.proto;

import lombok.Data;

@Data
public class User3 {
    private String name;
    private Integer age;
    private User3 user3 = new User3();
}
