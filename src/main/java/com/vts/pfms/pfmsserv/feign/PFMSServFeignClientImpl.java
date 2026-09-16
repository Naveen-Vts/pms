package com.vts.pfms.pfmsserv.feign;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.vts.pfms.login.CCMView;
import com.vts.pfms.login.ProjectHoa;
import com.vts.pfms.master.dto.DemandDetails;
import com.vts.pfms.master.dto.ProjectFinancialDetails;
import com.vts.pfms.master.dto.ProjectSanctionDetailsMaster;
import com.vts.pfms.model.FinanceChanges;
import com.vts.pfms.model.IbasLabMaster;
import com.vts.pfms.model.TotalDemand;
import com.vts.pfms.pfts.dto.DemandOrderDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PFMSServFeignClientImpl implements FeignClientService {
	
	@Value("${IsIbasConnected}")
	private String IsIbasConnected;
	
	private final PFMSServeFeignClientIbas client;

	@Override
	public List<CCMView> getCCMViewData(String token, String LabCode) {
		if("Y".equalsIgnoreCase(IsIbasConnected)) {
			return client.getCCMViewData(token,LabCode);
		}
		return Collections.emptyList();
	}

	@Override
	public List<ProjectSanctionDetailsMaster> getDetailsOfSupplyOrder(String token, String inType, String employeeNo) {
		if("Y".equalsIgnoreCase(IsIbasConnected)) {
			return client.getDetailsOfSupplyOrder(token,inType,employeeNo);
		}
		return Collections.emptyList();
	}

	@Override
	public List<ProjectFinancialDetails> financialStatusBriefing(String token, String ProjectCode, String rupess) {
		if("Y".equalsIgnoreCase(IsIbasConnected)) {
			return client.financialStatusBriefing(token,ProjectCode,rupess);
		}
		return Collections.emptyList();
	}

	@Override
	public List<TotalDemand> getTotalDemand(String token) {
		if("Y".equalsIgnoreCase(IsIbasConnected)) {
			return client.getTotalDemand(token);
		}
		return Collections.emptyList();
	}

	@Override
	public List<DemandDetails> DemandsDetails(String token, String projectcode) {
		if("Y".equalsIgnoreCase(IsIbasConnected)) {
			return client.DemandsDetails(token,projectcode);
		}
		return Collections.emptyList();
	}

	@Override
	public List<DemandOrderDetails> DemandsOrderDetails(String token, String demandNo) {
		if("Y".equalsIgnoreCase(IsIbasConnected)) {
			return client.DemandsOrderDetails(token, demandNo);
		}
		return Collections.emptyList();
	}

	@Override
	public List<FinanceChanges> PfmsFinanceChanges(String token, String projectCode, String interval) {
		if("Y".equalsIgnoreCase(IsIbasConnected)) {
			return client.PfmsFinanceChanges(token, projectCode, interval);
		}
		return Collections.emptyList();
	}

	@Override
	public List<ProjectHoa> ProjectHoaData(String token, String labcode) {
		if("Y".equalsIgnoreCase(IsIbasConnected)) {
			return client.ProjectHoaData(token, labcode);
		}
		return Collections.emptyList();
	}

	@Override
	public List<IbasLabMaster> LabDetails(String token) {
		if("Y".equalsIgnoreCase(IsIbasConnected)) {
			return client.LabDetails(token);
		}
		return Collections.emptyList();
	}

}
