package com.vts.pfms.pfmsserv.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "SisClient", url = "${sis.url:NA}")
public interface SisClient {

	
	  @GetMapping( value = "/user-login-access", consumes = MediaType.APPLICATION_JSON_VALUE )
	  ResponseEntity<Boolean> checkUserLoginAccess(@RequestHeader("X-API-KEY") String apiKey, @RequestHeader("username") String username);

}
