package com.vts.pfms.pfmsserv.feign;

import java.util.Map;
import java.util.function.BiFunction;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.vts.pfms.pfmsserv.feign.MasterClientController.ProjectCode;

import feign.FeignException;
import jakarta.annotation.PostConstruct;

@Service
public class MasterClientService {


	 	private final DmsClient dmsClient;
	    private final IbasClient ibasClient;
	    private final PftsClient pftsClient;
	    private final SisClient sisClient;
	    private final AmsClient amsClient;
	    private final HrmsClient hrmsClient;
	    private final EmsClient emsClient;
	    private final TmdsMasterClient tmdsMasterClient;
	    
	    private Map<ProjectCode, BiFunction<String, String, ResponseEntity<Boolean>>> accessCheckMap;
	    
		public MasterClientService(DmsClient dmsClient, IbasClient ibasClient, PftsClient pftsClient,
			SisClient sisClient, AmsClient amsClient, HrmsClient hrmsClient, EmsClient emsClient,
			TmdsMasterClient tmdsMasterClient) {
			this.dmsClient = dmsClient;
			this.ibasClient = ibasClient;
			this.pftsClient = pftsClient;
			this.sisClient = sisClient;
			this.amsClient = amsClient;
			this.hrmsClient = hrmsClient;
			this.emsClient = emsClient;
			this.tmdsMasterClient = tmdsMasterClient;
		}


		    @PostConstruct
		    public void init() {
		        accessCheckMap = Map.of(
		                ProjectCode.DMS, dmsClient::checkUserLoginAccess,
		                ProjectCode.IBAS, ibasClient::checkUserLoginAccess,
		                ProjectCode.PFTS, pftsClient::checkUserLoginAccess,
		               // ProjectCode.SIS, (token, username) -> sisClient.checkUserLoginAccess(apiKey, username),
		                ProjectCode.AMS, amsClient::checkUserLoginAccess,
		                ProjectCode.HRMS, hrmsClient::checkUserLoginAccess,
		                ProjectCode.EMS, emsClient::checkUserLoginAccess,
		                ProjectCode.TMDS, tmdsMasterClient::checkUserLoginAccess
		                
		        );
		    }
		
		public boolean checkUserLoginAccess(String token, String username, ProjectCode projectCode) {
			try {
	            var function = accessCheckMap.get(projectCode);
	            if (function == null) {
	                return false;
	            }
	            System.out.println("token inside service "+token);
	            ResponseEntity<Boolean> response = function.apply(token, username);
	            return response != null && Boolean.TRUE.equals(response.getBody());
	        } catch (FeignException e) {
	            e.printStackTrace();
	            return false;
	        }
		}
	    
	    
}
