package com.vts.pfms.pfmsserv.feign;

import java.util.Collections;
import java.util.List;

import org.springframework.cloud.openfeign.FallbackFactory;

import com.vts.pfms.login.CCMView;
import com.vts.pfms.login.ProjectHoa;
import com.vts.pfms.master.dto.DemandDetails;
import com.vts.pfms.master.dto.ProjectFinancialDetails;
import com.vts.pfms.master.dto.ProjectSanctionDetailsMaster;
import com.vts.pfms.model.FinanceChanges;
import com.vts.pfms.model.IbasLabMaster;
import com.vts.pfms.model.TotalDemand;
import com.vts.pfms.pfts.dto.DemandOrderDetails;

public class PFMSServeFallbackFactory  implements FallbackFactory<PFMSServeFeignClient>  {
	@Override
	public PFMSServeFeignClient create(Throwable cause) {
		System.err.println("PFMS SERVICE FAILED: " + cause.getMessage());

        return new PFMSServeFeignClient() {

            @Override
            public List<CCMView> getCCMViewData(String LabCode) {
                return Collections.emptyList();
            }

            @Override
            public List<ProjectSanctionDetailsMaster>
            getDetailsOfSupplyOrder(String inType, String employeeNo) {
                return Collections.emptyList();
            }

            @Override
            public List<ProjectFinancialDetails>
            financialStatusBriefing(String ProjectCode, String rupess) {
                return Collections.emptyList();
            }

            @Override
            public List<TotalDemand> getTotalDemand() {
                return Collections.emptyList();
            }

            @Override
            public List<DemandDetails>
            DemandsDetails(String projectcode) {
                return Collections.emptyList();
            }

            @Override
            public List<DemandOrderDetails>
            DemandsOrderDetails(String demandNo) {
                return Collections.emptyList();
            }

            @Override
            public List<FinanceChanges>
            PfmsFinanceChanges(String projectCode, String interval) {
                return Collections.emptyList();
            }

            @Override
            public List<ProjectHoa>
            ProjectHoaData(String labcode) {
                return Collections.emptyList();
            }

            @Override
            public List<IbasLabMaster> LabDetails() {
                return Collections.emptyList();
            }
        };
    }
}
