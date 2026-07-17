package com.vts.pfms.pfts.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.vts.pfms.pfts.dto.PFTSFileDto;
import com.vts.pfms.pfts.model.PFTSFile;
import com.vts.pfms.pfts.model.PftsFileMilestone;
import com.vts.pfms.pfts.model.PftsFileMilestoneRev;
import com.vts.pfms.pfts.model.PftsFileOrder;

@Transactional
@Repository
public  class PFTSDaoImpl implements PFTSDao{

	
	@PersistenceContext
	EntityManager manager;
	
	
	private static final String PROJECTSLIST="SELECT projectid, projectcode, projectname FROM project_master";
	private static final String FILESTATUS="SELECT DISTINCT f.PftsFileId, f.DemandNo, f.DemandDate, f.EstimatedCost, f.ItemNomenclature, s.PftsStatus, s.PftsStageName, s.PftsStatusId,f.EnvisagedFlag,f.Remarks,f.demandtype,f.EPCDate,f.TocDate,f.OrderDate,f.PDRDate,f.DDRDate,f.CDRDate,f.FATDate,f.IntegrationDate,f.ProjectId,f.DeliveryDate,f.CriticalDate,f.AcceptanceDate,f.SATDate FROM pfts_file f, pfts_status s WHERE f.ProjectId =:projectid AND f.PftsStatusId = s.PftsStatusId AND f.isactive = '1' UNION SELECT  f.PftsFileId, f.DemandNo, f.DemandDate, f.EstimatedCost, f.ItemNomenclature, NULL , NULL , NULL ,f.EnvisagedFlag,f.Remarks,f.demandtype,f.EPCDate,f.TocDate,f.OrderDate,f.PDRDate,f.DDRDate,f.CDRDate,f.FATDate,f.IntegrationDate,f.ProjectId,f.DeliveryDate,f.CriticalDate,f.AcceptanceDate,f.SATDate FROM pfts_file f WHERE f.ProjectId =:projectid AND f.EnvisagedFlag='Y' AND f.isactive = '1'";
	private static final String PrevDemandFile ="SELECT ProjectId, DemandNo, DemandDate, ItemNomenclature, EstimatedCost FROM pfts_file WHERE ProjectId=:projectid";
	private static final String StatusList="SELECT s.PftsStatusId, s.PftsStatus, s.PftsStageName FROM pfts_status s WHERE s.PftsStatusId > (SELECT PftsStatusId FROM pfts_file WHERE PftsFileId=:fileid) AND CASE WHEN (SELECT PftsStatusId FROM pfts_file WHERE PftsFileId=:fileid) < 10 THEN s.PftsStatusId <= 10 ELSE TRUE END ORDER BY pftsstatusid ";
	private static final String updateCostDetails="UPDATE pfts_file SET OrderNo=:orderno, OrderCost=:ordercost, DpDate=:dpdate WHERE PftsFileId=:fileid";
	private static final String PROJECTDATA="SELECT projectid, projectcode, projectname FROM project_master WHERE projectid=:projectid";
	
	
	@Override
	public List<Object[]> ProjectsList() throws Exception {

		Query query=manager.createNativeQuery(PROJECTSLIST);	   
		List<Object[]> ProjectsList=(List<Object[]>)query.getResultList();	
		
		return ProjectsList;
	}
	
	@Override
	public Object[] ProjectData(String projectid) throws Exception {

		Query query=manager.createNativeQuery(PROJECTDATA);	   
		query.setParameter("projectid", Long.parseLong(projectid));
		Object[] Project=(Object[])query.getSingleResult();	
		
		return Project;
	}
	
	@Override
	public List<Object[]> LoginProjectDetailsList(String empid,String Logintype ,String LabCode)throws Exception
	{
		Query query=manager.createNativeQuery("CALL Pfms_Emp_ProjectList(:empid,:logintype,:labcode);");
		query.setParameter("empid", Long.parseLong(empid));
		query.setParameter("logintype", Logintype);
		query.setParameter("labcode", LabCode);
		List<Object[]> LoginProjectIdList=(List<Object[]>)query.getResultList();
		return LoginProjectIdList;
	}
	

	
	@Override
	public List<Object[]> getFileStatusList(String projectId) throws Exception {
	
		Query query=manager.createNativeQuery(FILESTATUS);
		query.setParameter("projectid", Integer.parseInt(projectId));
		List<Object[]> fileStatusList=(List<Object[]>)query.getResultList();	
		
		return fileStatusList;
	}
	
	
	@Override
	public List<Object[]> getprevDemandFile(String projectId)throws Exception{
		Query query=manager.createNativeQuery(PrevDemandFile);
		query.setParameter("projectid",  Integer.parseInt(projectId));
		List<Object[]> prevDemandFileList=(List<Object[]>)query.getResultList();	
		
		return prevDemandFileList;
	}
	

	@Override
	public Long addDemandfile(PFTSFile pf)throws Exception{
		
		manager.persist(pf);
		manager.flush();
		
		return pf.getPftsFileId();
	}
	
	
	@Override
	public List<Object[]> getStatusList(String fileid)throws Exception{
		Query query=manager.createNativeQuery(StatusList);
		query.setParameter("fileid", Long.parseLong(fileid));
		return (List<Object[]>)query.getResultList();
	}
	
	
	
	@Override
	public int upadteDemandFile(String fileId, String statusId, Date eventDateSql,String update,String remarks)throws Exception{
		
		String UpdateDemand="UPDATE pfts_file SET PftsStatusId=:statusid, "+update+"=:eventDate, Remarks=:remarks  WHERE PftsFileId=:fileid";
//		String UpdateDemandStatus="UPDATE pfts_file SET PftsStatusId=:statusid, Remarks=:remarks WHERE PftsFileId=:fileid ";
		int result = 0;
		if(statusId.equalsIgnoreCase("1"))
		{
			PFTSFile ExistingPFTSFile= manager.find(PFTSFile.class, Long.parseLong(fileId));
			if(ExistingPFTSFile != null) {
				ExistingPFTSFile.setPftsStatusId(Long.parseLong(statusId));
				ExistingPFTSFile.setRemarks(remarks);
				result=1;
			}
		}
		else {
			Query query=manager.createNativeQuery(UpdateDemand);
			query.setParameter("fileid", Long.parseLong(fileId));
			query.setParameter("statusid", statusId);
			query.setParameter("eventDate", eventDateSql);
			query.setParameter("remarks", remarks);
			result=query.executeUpdate();
		}
		return result;
	}
	
	
	private static final String updateCostDetails1="UPDATE pfts_file SET OrderNo=:orderno, OrderCost=:ordercost,"
			+ " DpDate=:dpdate WHERE PftsFileId=:fileid";
	@Override
	public int updateCostOnDemand(String orderNo, String oderCostD, String fileId,Date dpDateSql)throws Exception{
		
		PFTSFile ExistingPFTSFile = manager.find(PFTSFile.class, Long.parseLong(fileId));
		if(ExistingPFTSFile != null) {
			 ExistingPFTSFile.setOrderNo(orderNo);
			 ExistingPFTSFile.setOrderCost(Double.parseDouble(oderCostD));
			 ExistingPFTSFile.setDpDate(new java.sql.Date(dpDateSql.getTime()));
			 System.err.println(new java.sql.Date(dpDateSql.getTime()));
			 return 1;
		}
		else {
			return 0;
		}
		
	}
	

	@Override
	public int FileInActive(String fileId, String userId) throws Exception {
		
		PFTSFile ExistingPFTSFile = manager.find(PFTSFile.class,  Long.parseLong(fileId));
		if(ExistingPFTSFile != null) {
			ExistingPFTSFile.setIsActive(0);
			return 1;
		}
		else {
			 return 0;
		}
		
	}

	@Override
	public Long addDemandfileOrder(PftsFileOrder pfo) throws Exception {
//		if(!pfo.getIsPresent().equalsIgnoreCase("N")) {
//		String updateOrder="UPDATE pfts_file_order SET IsActive='0' WHERE PftsFileId=:PftsFileId";
//		Query query = manager.createNativeQuery(updateOrder);
//		query.setParameter("PftsFileId", pfo.getPftsFileId());
//		query.executeUpdate();
//		}
		manager.persist(pfo);
		manager.flush();
		
		return pfo.getPftsFileOrderId();
	}
	
	private static final String GETFILEPDCINFO ="SELECT f.PftsFileId, f.DemandNo, f.DemandDate, f.EstimatedCost, f.ItemNomenclature,f.PDC,f.IntegrationDate  FROM pfts_file f WHERE  f.PftsFileId=:fileid ";
	@Override
	public Object[] getFilePDCInfo(String fileid)throws Exception
	{
		Query query=manager.createNativeQuery(GETFILEPDCINFO);
		query.setParameter("fileid", Long.parseLong(fileid));
		return ( Object[])query.getSingleResult();
	}
	
	@Override
	public PFTSFile getPftsFile(String pftsFileId) throws Exception {
		PFTSFile file= manager.find(PFTSFile.class, Long.parseLong(pftsFileId));
		
		return file;
	}
	
	@Override
	public long editPftsFile(PFTSFile file) throws Exception {
		manager.merge(file);
		manager.flush();
		return file.getPftsFileId();
	}
	
	
	private static final String GETPFTSCOUNT = "SELECT COUNT(PftsFileId) FROM pfts_file WHERE PftsFileId=:pftsFileId AND IsActive='1'";
	@Override
	public int getpftsFieldId(String pftsFileId) throws Exception {
		Query query=manager.createNativeQuery(GETPFTSCOUNT);
		query.setParameter("pftsFileId", Long.parseLong(pftsFileId));
		Long result = (Long)query.getSingleResult();
		return result.intValue();
	}
	
	@Override
	public Long updatepftsEnvi(PFTSFile pf) throws Exception {
		
		PFTSFile ExistingPFTSFile = manager.find(PFTSFile.class, pf.getPftsFileId());
				if(ExistingPFTSFile !=null) {
					ExistingPFTSFile.setItemNomenclature(pf.getItemNomenclature());
					ExistingPFTSFile.setEstimatedCost(pf.getEstimatedCost());
					ExistingPFTSFile.setPrbDateOfInti(pf.getPrbDateOfInti());
					ExistingPFTSFile.setRemarks(pf.getRemarks());
					ExistingPFTSFile.setModifiedBy(pf.getModifiedBy());
					ExistingPFTSFile.setModifiedDate(pf.getModifiedDate());
					return 1L;
				}
				else {
					return 0L;
				}
		
	}
	private static final String GETENVIDATA ="SELECT PftsFileId,ItemNomenclature,EstimatedCost,PrbDateOfInti,EnvisagedStatus,Remarks FROM pfts_file WHERE PftsFileId=:PftsFileId";


	@Override
	public Object[] getEnviData(String PftsFileId)throws Exception
	{
		Query query=manager.createNativeQuery(GETENVIDATA);
		query.setParameter("PftsFileId", Long.parseLong(PftsFileId));
		return ( Object[])query.getSingleResult();
	}
	
	private static final String GETSTAGELIST="SELECT PftsStatusId, PftsStatus, PftsStageName FROM pfts_status  ORDER BY pftsstatusid ";
	@Override
	public List<Object[]> getpftsStageList() throws Exception {
		Query query=manager.createNativeQuery(GETSTAGELIST);
		return ( List<Object[]>)query.getResultList();
	}
	
	
	private static final String FILEVIEWLIST="SELECT f.PftsFileId, f.DemandNo, f.DemandDate, f.EstimatedCost, f.ItemNomenclature, s.PftsStatus,s.PftsStageName, s.PftsStatusId,f.EnvisagedFlag,f.Remarks,f.demandtype,f.projectid FROM pfts_file f, pfts_status s WHERE f.PftsFileId=:pftsfileid AND f.PftsStatusId = s.PftsStatusId";
	@Override
	public Object[] getpftsFileViewList(String procFileId) throws Exception {
		
		Query query = manager.createNativeQuery(FILEVIEWLIST);
		query.setParameter("pftsfileid", Long.parseLong(procFileId));
		
		return  (Object[])query.getSingleResult();
	}
	
	private static final String ORDERETAILSAJAX="SELECT a.PftsFileOrderId,b.PftsFileId,b.DemandType,b.DemandNo,b.PftsStatusId,b.projectid,a.OrderNo,a.OrderDate,a.DpDate,a.ItemFor,a.OrderCost,a.VendorName FROM pfts_file_order a, pfts_file b WHERE a.PftsFileId=b.PftsFileId AND a.IsActive='1' AND b.PftsFileId=:fileId";
	@Override
	public List<Object[]> getOrderDetailsAjax(String fileId) throws Exception {
		
		Query query = manager.createNativeQuery(ORDERETAILSAJAX);
		query.setParameter("fileId", Long.parseLong(fileId));
		return  (List<Object[]>)query.getResultList();
	}
	
	
	@Override
	public long ManualOrderSubmit(PftsFileOrder order, String orderid) throws Exception {
		
		PftsFileOrder ExistingPftsFileOrder = manager.find(PftsFileOrder.class, Long.parseLong(orderid));
		if(ExistingPftsFileOrder != null) {
			System.err.println("Working");
			ExistingPftsFileOrder.setOrderNo(order.getOrderNo());
			ExistingPftsFileOrder.setOrderDate(order.getOrderDate());
			ExistingPftsFileOrder.setOrderCost(order.getOrderCost());
			ExistingPftsFileOrder.setDpDate(order.getDpDate());
			ExistingPftsFileOrder.setItemFor(order.getItemFor());
			ExistingPftsFileOrder.setVendorName(order.getVendorName());
			ExistingPftsFileOrder.setModifiedBy(order.getModifiedBy());
			ExistingPftsFileOrder.setModifiedDate(order.getModifiedDate());
			return 1L;
		}
		else {
			return 0L;
		}
		
	}
	
	
	@Override
	public long manualDemandEditSubmit(PFTSFileDto pftsDto) throws Exception {
		PFTSFile existingPftsFile = manager.find(PFTSFile.class, pftsDto.getPftsFileId());
		if(existingPftsFile != null) {
			existingPftsFile.setDemandNo(pftsDto.getDemandNo());
			existingPftsFile.setDemandDate(pftsDto.getDemandDate());
			existingPftsFile.setEstimatedCost(pftsDto.getEstimatedCost());
			existingPftsFile.setItemNomenclature(pftsDto.getItemNomenclature());
			existingPftsFile.setModifiedBy(pftsDto.getModifiedBy());
			existingPftsFile.setModifiedDate(pftsDto.getModifiedDate());
			return 1L;
		}
		else {
			return 0L;
		}
		
	}
	
	private static final String GETDEMANDNO="SELECT demandno FROM pfts_file WHERE isactive='1'";
	@Override
	public List<Object[]> getDemandNoList() throws Exception {
		Query query = manager.createNativeQuery(GETDEMANDNO);
		return (List<Object[]>)query.getResultList();
	}
	
	@Override
	public void updatePftsFileId(String fileId) throws Exception {
		// TODO Auto-generated method stub
			String updateOrder="UPDATE pfts_file_order SET IsActive='0' WHERE PftsFileId=:PftsFileId";
			Query query = manager.createNativeQuery(updateOrder);
			query.setParameter("PftsFileId", Long.parseLong(fileId));
			query.executeUpdate();
	}
	
	@Override
	public long addProcurementMilestone(PftsFileMilestone mile) throws Exception {
		try {
			manager.persist(mile);
			manager.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mile.getPftsMilestoneId();
	}
	
//	private static final String GETPFTSMILEDATA="SELECT a.PftsMileStoneId,a.PftsFileId,b.DemandType,b.ProjectId,b.DemandNo,b.DemandDate,a.ProbableDate,a.PftsStatusId,b.PftsStatusId AS 'mainFileStatusId',a.SetBaseline,a.Revision,c.PftsStageName FROM pfts_file_ms a, pfts_file b, pfts_status c WHERE a.PftsFileId=b.PftsFileId AND a.PftsStatusId=c.PftsStatusId AND a.IsActive='1'";
	private static final String GETPFTSMILEDATA="SELECT * FROM pfts_file_ms WHERE IsActive='1'";
	@Override
	public List<Object[]> getpftsMilestoneList() throws Exception {
		try {
			Query query = manager.createNativeQuery(GETPFTSMILEDATA);
			return query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}
	
	@Override
	public long editProcurementMilestone(PftsFileMilestone mile) throws Exception {
		manager.merge(mile);
		manager.flush();
		return mile.getPftsFileId();
	}
	
	@Override
	public PftsFileMilestone getEditMilestoneData(long pftsMilestoneId) throws Exception {
		 return manager.find(PftsFileMilestone.class, pftsMilestoneId);
	}
	
	@Override
	public long addProcurementMilestoneRev(PftsFileMilestoneRev rev) throws Exception {
		try {
			manager.persist(rev);
			manager.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rev.getPftsMileStoneRevId();
	}
	
	private static final String ACTUALSTATUSDATE="SELECT DemandDate,EPCDate,TocDate,OrderDate,PDRDate,DDRDate,CDRDate,FATDate,IntegrationDate,CriticalDate,AcceptanceDate,SATDate FROM pfts_file WHERE ProjectId=:ProjectId AND DemandNo=:DemandNo AND IsActive='1'";
	@Override
	public Object[] getActualStatus(String projectId, String demandId) throws Exception {
		try {
			
			Query query = manager.createNativeQuery(ACTUALSTATUSDATE);
			query.setParameter("ProjectId", Long.parseLong(projectId));
			query.setParameter("DemandNo", demandId);
			return (Object[])query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static final String GETMILEDEMANDLIST="SELECT a.PftsMileStoneId,a.PftsFileId,b.DemandDate,a.EPCDate,a.TocDate,a.OrderDate,a.PDRDate,a.CriticalDate,a.DDRDate,a.CDRDate,a.AcceptanceDate,a.FATDate,a.DeliveryDate,a.SATDate,a.IntegrationDate FROM pfts_file_ms a, pfts_file b WHERE a.IsActive='1' AND a.PftsFileId=b.PftsFileId AND a.PftsFileId=:PftsFileId";
	@Override
	public Object[] getpftsMileDemandList(String PftsFileId) throws Exception {
		try {
			Query query = manager.createNativeQuery(GETMILEDEMANDLIST);
			query.setParameter("PftsFileId", Long.parseLong(PftsFileId));
			return (Object[])query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static final String GETPFTSSCTUALDATE="SELECT PftsFileId,DemandNo,DemandDate,EPCDate,TocDate,OrderDate,PDRDate,DDRDate,CDRDate,FATDate,IntegrationDate,CriticalDate,AcceptanceDate,SATDate,DeliveryDate FROM pfts_file WHERE PftsFileId=:pftsFileId AND IsActive='1'";
	@Override
	public Object[] getpftsActuallDate(String pftsFileId) throws Exception {
		try {
			Query query = manager.createNativeQuery(GETPFTSSCTUALDATE);
			query.setParameter("pftsFileId", Long.parseLong(pftsFileId));
			return (Object[])query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static final String GETPFTSPRIJECRDATE="SELECT ProjectCode,ProjectShortName,SanctionDate,PDC FROM project_master WHERE ProjectId=:ProjectId";
	@Override
	public Object[] getpftsProjectDate(String projectId) throws Exception {
		try {
			Query query = manager.createNativeQuery(GETPFTSPRIJECRDATE);
			query.setParameter("ProjectId", Long.parseLong(projectId));
			return (Object[]) query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static final String GETFILEORDERLIST="SELECT * FROM pfts_file_order WHERE PftsFileId=:PftsFileId AND IsActive='1'";
	@Override
	public List<Object[]> getpftsFileOrder(String fileId) throws Exception {
	    try {
	    	Query query = manager.createNativeQuery(GETFILEORDERLIST);
			query.setParameter("PftsFileId", Long.parseLong(fileId));
			return (List<Object[]>)query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}
	
	private static final String GETPROCUREMENTDETAILS="SELECT a.PftsFileId,b.PftsMileStoneId,a.DemandDate,a.EPCDate AS 'EPCactdate',a.TocDate AS 'Tocactdate',a.OrderDate AS 'Orderactdate',a.PDRDate AS 'PDRactdate',a.DDRDate AS 'DDRactdate',a.CDRDate AS 'CDRactdate',\r\n"
			+ "a.FATDate AS 'FATactdate',a.IntegrationDate AS 'Integrationactdate',a.CriticalDate AS 'Criticalactdate',a.AcceptanceDate AS 'Acceptanceactdate',a.SATDate AS 'SATactdate',a.DeliveryDate AS 'Deliveryactdate',\r\n"
			+ "b.EPCDate AS 'EPCprobdate',b.TocDate AS 'Tocprobdate',b.OrderDate AS 'Orderprobdate',b.PDRDate AS 'PDRprobdate',b.CriticalDate AS 'Criticalprobdate',b.DDRDate AS 'DDRprobdate',b.CDRDate AS 'CDRprobdate',b.AcceptanceDate AS 'Acceptanceprobdate',\r\n"
			+ "b.FATDate AS 'FATprobdate',b.DeliveryDate AS 'Deliveryprobdate',b.SATDate AS 'SATprobate',b.IntegrationDate AS 'Integrationprobdate',b.SetBaseline,b.Revision\r\n"
			+ "FROM pfts_file a LEFT JOIN pfts_file_ms b ON a.PftsFileId = b.PftsFileId WHERE a.PftsFileId=:PftsFileId AND a.IsActive = '1'AND (b.IsActive = '1' OR b.IsActive IS NULL)";
	@Override
	public List<Object[]> getprocurementMilestoneDetails(String pftsid) throws Exception {
		 try {
		    	Query query = manager.createNativeQuery(GETPROCUREMENTDETAILS);
				query.setParameter("PftsFileId", Long.parseLong(pftsid));
				return (List<Object[]>)query.getResultList();
			} catch (Exception e) {
				e.printStackTrace();
				return new ArrayList<Object[]>();
			}
	}
	
	private static final String PROJECTDATABYPRJCODE="SELECT projectid, projectcode, projectname FROM project_master WHERE projectcode=:projectcode";
	
	@Override
	public Object[] ProjectDataByPrjCode(String projectcode) throws Exception {
		
		Query query=manager.createNativeQuery(PROJECTDATABYPRJCODE);	   
		query.setParameter("projectcode",projectcode);
		Object[] Project=(Object[])query.getSingleResult();	
		
		return Project;
	}

	@Override
	public long upadteDemandFileRemarks(String fileId, String remarks) throws Exception {
		String UpdateDemand="UPDATE pfts_file SET Remarks=:remarks WHERE PftsFileId=:fileid";
		int result = 0;
		
		Query query=manager.createNativeQuery(UpdateDemand);
		query.setParameter("fileid", Long.parseLong(fileId));
		query.setParameter("remarks", remarks);
		result = query.executeUpdate();
	
		return result;
	}
}
