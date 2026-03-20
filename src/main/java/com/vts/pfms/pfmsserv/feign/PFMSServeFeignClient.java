package com.vts.pfms.pfmsserv.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.vts.pfms.login.CCMView;
import com.vts.pfms.login.ProjectHoa;
import com.vts.pfms.master.dto.DemandDetails;
import com.vts.pfms.master.dto.ProjectFinancialDetails;
import com.vts.pfms.master.dto.ProjectSanctionDetailsMaster;
import com.vts.pfms.model.FinanceChanges;
import com.vts.pfms.model.IbasLabMaster;
import com.vts.pfms.model.TotalDemand;
import com.vts.pfms.pfts.dto.DemandOrderDetails;



@FeignClient(name = "PFMSServeFeignClient", 
url = "${pfms_serv_url:NA}",
fallbackFactory = PFMSServeFallbackFactory.class
)
public interface PFMSServeFeignClient {

	@GetMapping("/getCCMViewData")
    List<CCMView> getCCMViewData(@RequestHeader(name = "labcode") String LabCode);
    
	@GetMapping( value = "/pfms-chart-service", consumes = MediaType.APPLICATION_JSON_VALUE )
	List<ProjectSanctionDetailsMaster> getDetailsOfSupplyOrder(@RequestParam(name="inType")String inType, @RequestParam(name="employeeNo")String employeeNo
    );
	
	@GetMapping(value="/financialStatusBriefing",consumes = MediaType.APPLICATION_JSON_VALUE )
	List<ProjectFinancialDetails> financialStatusBriefing(@RequestParam(name="ProjectCode")String ProjectCode, @RequestParam(name="rupess")String rupess);

	@GetMapping(value="/getTotalDemand",consumes = MediaType.APPLICATION_JSON_VALUE )
	List<TotalDemand> getTotalDemand();
	

	@GetMapping(value="/newDemandsDetails",consumes = MediaType.APPLICATION_JSON_VALUE )
	List<DemandDetails> DemandsDetails(@RequestParam(name="projectcode")String projectcode);
	
	@GetMapping(value="/newDemandsDetails",consumes = MediaType.APPLICATION_JSON_VALUE )
	List<DemandOrderDetails> DemandsOrderDetails(@RequestParam(name="demandNo")String demandNo);
	
	@GetMapping(value="/pfms-finance-changes",consumes = MediaType.APPLICATION_JSON_VALUE )
	List<FinanceChanges> PfmsFinanceChanges(@RequestParam(name="projectCode")String projectCode,
	@RequestParam(name="interval")String interval);
	
	@GetMapping(value="/tblprojectdata",consumes = MediaType.APPLICATION_JSON_VALUE )
	List<ProjectHoa> ProjectHoaData(@RequestParam(name="labcode")String labcode);
	
	@GetMapping(value="/tblprojectdata",consumes = MediaType.APPLICATION_JSON_VALUE )
	List<IbasLabMaster>LabDetails();
}
