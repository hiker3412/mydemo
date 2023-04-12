package com.example.server;

import com.example.server.service.fundclass.FundClassTreeService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.DecimalFormat;

@SpringBootTest
class ServerApplicationTests {

	@Resource
	FundClassTreeService fundClassTreeService;

	@Test
	void contextLoads() {
		fundClassTreeService.readExcel();
	}

}
