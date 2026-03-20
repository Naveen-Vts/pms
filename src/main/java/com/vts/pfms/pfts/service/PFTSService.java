package com.vts.pfms.pfts.service;

import java.util.List;

import com.vts.pfms.master.dto.DemandDetails;
import com.vts.pfms.pfts.dto.DemandOrderDetails;
import com.vts.pfms.pfts.dto.PFTSFileDto;
import com.vts.pfms.pfts.model.PFTSFile;
import com.vts.pfms.pfts.model.PftsFileMilestone;
import com.vts.pfms.pfts.model.PftsFileMilestoneRev;
import com.vts.pfms.pfts.model.PftsFileOrder;

public interface PFTSService {
	
	public List<Object[]> ProjectsList() throws Exception;
	public List<Object[]> getFileStatusList(String projectId)  throws Exception;
	public List<DemandDetails> getprevDemandFile(String projectId)throws Exception;
	public Long addDemandfile(PFTSFile pf) throws Exception;
	public List<Object[]> getStatusList(String fileid)throws Exception;
	public int upadteDemandFile(String fileId, String statusId, String eventDate,String remarks)throws Exception;
	public long updateCostOnDemand(List<DemandOrderDetails> dd ,String fileId,String userid)throws Exception;
	public List<Object[]> LoginProjectDetailsList(String empid,String Logintype,String LabCode) throws Exception;
	public int FileInActive(String fileId, String userId)throws Exception;
	public Object[] ProjectData(String projectid) throws Exception;
	public Object[] getFilePDCInfo(String ileid) throws Exception;
	public long UpdateFilePDCInfo(String fileid, String PDCDate, String IntegrationDate, String UserId) throws Exception;
	public int getpftsFieldId(String fileId)throws Exception;
	long updateEnvi(PFTSFile pf, String userId) throws Exception;
	public Object[] getEnviData(String pftsFileId)throws Exception;
	public List<Object[]> getpftsStageList()throws Exception;
	public Object[] getpftsFileViewList(String procFileId)throws Exception;
	public List<Object[]> getOrderDetailsAjax(String fileId)throws Exception;
	public long ManualOrderSubmit(PftsFileOrder order, String orderid)throws Exception;
	public long manualDemandEditSubmit(PFTSFileDto pftsDto)throws Exception;
	public List<Object[]> getDemandNoList()throws Exception;
	public long addProcurementMilestone(PftsFileMilestone mile)throws Exception;
	public List<Object[]> getpftsMilestoneList()throws Exception;
	public long editProcurementMilestone(PftsFileMilestone mile)throws Exception;
	public PftsFileMilestone getEditMilestoneData(long pftsMilestoneId)throws Exception;
	public long addProcurementMilestoneRev(PftsFileMilestoneRev rev)throws Exception;
	public Object[] getActualStatus(String projectId, String demandId)throws Exception;
	public Object[] getpftsMileDemandList(String PftsFileId)throws Exception;
	public Object[] getpftsActualDate(String pftsFileId)throws Exception;
	public Object[] getpftsProjectDate(String projectId)throws Exception;
	public List<Object[]> getprocurementMilestoneDetails(String parameter)throws Exception;
	public Object[] ProjectDataByPrjCode(String projectCode) throws Exception;

}
