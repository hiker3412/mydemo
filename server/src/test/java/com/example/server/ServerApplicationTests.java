package com.example.server;

import com.example.common.model.abstracttest.vo.User1;
import com.example.common.model.abstracttest.vo.User2;
import com.example.server.service.fundclass.FundClassTreeService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@EnableDiscoveryClient
class ServerApplicationTests {

	@Resource
	FundClassTreeService fundClassTreeService;

	@Test
	void contextLoads() {
		try {
			fundClassTreeService.updateSecurityClass();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
