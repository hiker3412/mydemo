package com.example.server;

import com.example.server.service.fundclass.FundClassTreeService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
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
