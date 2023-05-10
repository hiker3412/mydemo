package com.example.server.feign.server2;

import feign.Request;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@FeignClient(value = "mydemo-server2",url = "http://localhost:8089/mydemo-server2")
public interface TestClient {

    @RequestMapping("/test/sleep3")
    String sleep3(@RequestBody String[] param);

    @RequestMapping("/test/sleep6")
    String sleep6(@RequestBody String[] param, Request.Options options);
}
