package com.example.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties("mydemo")
@Component
@RefreshScope
public class MydemoConfigProperties {
    private String nacosTest;
}
