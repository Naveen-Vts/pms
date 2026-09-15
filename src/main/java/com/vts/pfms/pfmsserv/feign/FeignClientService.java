package com.vts.pfms.pfmsserv.feign;

import java.util.List;

import com.vts.pfms.login.CCMView;
import com.vts.pfms.login.ProjectHoa;
import com.vts.pfms.master.dto.DemandDetails;
import com.vts.pfms.master.dto.ProjectFinancialDetails;
import com.vts.pfms.master.dto.ProjectSanctionDetailsMaster;
import com.vts.pfms.model.FinanceChanges;
import com.vts.pfms.model.IbasLabMaster;
import com.vts.pfms.model.TotalDemand;
import com.vts.pfms.pfts.dto.DemandOrderDetails;

public interface FeignClientService {

	List<CCMView> getCCMViewData(String token, String LabCode);

	List<ProjectSanctionDetailsMaster> getDetailsOfSupplyOrder(String token, String inType, String employeeNo);

	List<ProjectFinancialDetails> financialStatusBriefing(String token, String ProjectCode, String rupess);

	List<TotalDemand> getTotalDemand(String token);

	List<DemandDetails> DemandsDetails(String token, String projectcode);

	List<DemandOrderDetails> DemandsOrderDetails(String token, String demandNo);

	List<FinanceChanges> PfmsFinanceChanges(String token, String projectCode, String interval);

	List<ProjectHoa> ProjectHoaData(String token, String labcode);

	List<IbasLabMaster> LabDetails(String token);
	
	
	
}
