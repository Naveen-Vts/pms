package com.vts.pfms.pfmsserv.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "TmdsMasterClient", url = "${tmds.url:NA}")
public interface TmdsMasterClient {

	 @GetMapping( value = "/api/logins/user-login-access", consumes = MediaType.APPLICATION_JSON_VALUE )
	  ResponseEntity<Boolean> checkUserLoginAccess(@RequestHeader("Authorization") String token, @RequestHeader("username") String username);

}
