package com.vts.pfms.pfmsserv.feign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;



@RestController
public class MasterClientController {

	@Autowired
	MasterClientService masterClientService;
	
	
	 enum ProjectCode {
		    PMS, DMS, IBAS, PFTS, SIS, AMS, HRMS, EMS,TMDS
		}
		
		  @GetMapping("/user-login-app-access")
		    public ResponseEntity<Boolean> checkUserLoginAccess(HttpSession ses, HttpServletRequest req, @RequestParam ProjectCode projectCode) {
			  String username=(String)ses.getAttribute("Username");
			  String token=(String)ses.getAttribute("token");
		        try {

		            return ResponseEntity.ok(masterClientService.checkUserLoginAccess("Bearer "+token, username, projectCode));
		        	
		        } catch (Exception e) {
		         //
		            return ResponseEntity.ok(false);
		        }
		    }
}
