package com.vts.pfms.pfmsserv.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "DmsClient", url = "${dms.url}")
public interface DmsClient {

	 @GetMapping( value = "/user-login-access", consumes = MediaType.APPLICATION_JSON_VALUE )
	  ResponseEntity<Boolean> checkUserLoginAccess(@RequestHeader("Authorization") String token, @RequestHeader("username") String username);

}
