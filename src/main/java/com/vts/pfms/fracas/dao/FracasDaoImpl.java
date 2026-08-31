package com.vts.pfms.fracas.dao;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.vts.pfms.committee.model.PfmsNotification;
import com.vts.pfms.fracas.dto.PfmsFracasAssignDto;
import com.vts.pfms.fracas.dto.PfmsFracasMainDto;
import com.vts.pfms.fracas.model.PfmsFracasAssign;
import com.vts.pfms.fracas.model.PfmsFracasAttach;
import com.vts.pfms.fracas.model.PfmsFracasMain;
import com.vts.pfms.fracas.model.PfmsFracasSub;

@Transactional
@Repository
public class FracasDaoImpl implements FracasDao {
		
	private static final Logger logger=LogManager.getLogger(FracasDaoImpl.class);	
	@PersistenceContext EntityManager manager;
	
	private static final String LABDETAILS = "SELECT lab_master_id, lab_code, lab_name, lab_unit_code, lab_address, lab_city, lab_pin, lab_tel_no, lab_fax_no, lab_email, lab_authority, lab_authority_id, lab_rfp_email, lab_id, cluster_id, lab_logo FROM lab_master";
	private static final String EMPLOYEELIST="select a.emp_id,CONCAT(IFNULL(CONCAT(a.title,' '),''), a.emp_name) 'empname' ,b.designation FROM employee a,employee_desig b WHERE a.is_active='1' AND a.emp_status IN ('P','R','T') AND a.desig_id=b.desig_id and lab_code=:LabCode ORDER BY a.sr_no=0,a.sr_no";
	private final static String PROJECTSLIST="SELECT project_id,project_code,project_name FROM project_master";
	private static final String PROJECTSDATA="SELECT project_id,project_code,project_name FROM project_master WHERE project_id=:projectid";
	private final static String FRACASTYPELIST="SELECT fracastypeid, fracastype FROM pfms_fracas_type ";
	private static final String PROJECTFRACASITEMSLIST="SELECT * FROM (SELECT  pfm.fracasmainid, pfm.fracastypeid, pfm.fracasitem, pfm.fracasdate,pfm.projectid,pft.fracastype, CASE WHEN pfm.projectid>0 THEN pm.project_code ELSE 'General' END AS 'projectcode' FROM pfms_fracas_main pfm, pfms_fracas_type pft , project_master pm WHERE  CASE WHEN pfm.projectid>0 THEN pm.project_id=pfm.projectid ELSE 1=1 END AND pft.fracastypeid=pfm.fracastypeid AND pfm.isactive=1 AND pfm.projectid=:projectid AND pfm.LabCode=:LabCode  GROUP BY pfm.fracasmainid,pm.project_code   ) AS x  LEFT JOIN  (     SELECT pfa.fracasattachid,pfa.fracasmainid AS 'mainid',pfa.fracassubid AS 'subid' FROM pfms_fracas_attach pfa ) AS y  ON x.fracasmainid = y.mainid";
	private static final String FRACASITEMDATA="SELECT * FROM (SELECT  pfm.fracasmainid, pfm.fracastypeid, pfm.fracasitem, pfm.fracasdate,pfm.projectid,pft.fracastype,CASE WHEN pfm.projectid>0 THEN pm.project_code ELSE 'Gen' END AS 'projectcode' FROM pfms_fracas_main pfm, project_master pm, pfms_fracas_type pft WHERE  CASE WHEN pfm.projectid>0 THEN pm.project_id=pfm.projectid ELSE 1=1 END AND pft.fracastypeid=pfm.fracastypeid AND pfm.fracasmainid=:fracasmainid GROUP BY pfm.fracasmainid,pm.project_code) AS x  LEFT JOIN  (     SELECT pfa.fracasattachid,pfa.fracasmainid AS 'mainid',pfa.fracassubid AS 'subid' FROM pfms_fracas_attach pfa ) AS y  ON x.fracasmainid = y.mainid";
	
	private static final String FRACASASSIGNEDLIST = "SELECT pfa.fracasassignid,pfa.fracasmainid,pfa.remarks,pfa.pdc,pfa.assigner,pfa.assignee,pfa.assigneddate, pfa.fracasstatus, CONCAT(IFNULL(CONCAT(e1.title,' '),''), e1.emp_name)  AS 'assignername',ed1.designation AS 'assignerdesig', CONCAT(IFNULL(CONCAT(e2.title,' '),''), e2.emp_name) AS 'assigneename',  ed2.designation AS 'assigneedesig', pfm.fracasItem ,(SELECT MAX(pfs.progress) FROM  pfms_fracas_sub pfs WHERE pfa.fracasassignid=pfs.fracasassignid) AS 'Progress' FROM pfms_fracas_assign pfa,pfms_fracas_main pfm,employee e1,employee_desig ed1, employee e2, employee_desig ed2   WHERE pfa.assigner=e1.emp_id AND e1.desig_id=ed1.desig_id AND pfa.assignee=e2.emp_id   AND e2.desig_id=ed2.desig_id AND pfm.fracasmainid=pfa.fracasmainid AND pfa.isactive=1 AND pfa.assigner=:assignerempid AND pfa.fracasmainid=:fracasmainid";
	private static final String FRACASASSIGNEELIST = "SELECT * FROM(SELECT pfa.fracasassignid,pfa.fracasmainid,pfa.remarks,pfa.pdc,pfa.assigner,pfa.assignee,pfa.assigneddate, pfa.fracasstatus, CONCAT(IFNULL(CONCAT(e1.title,' '),''), e1.emp_name) AS 'assignername',ed1.designation AS 'assignerdesig', CONCAT(IFNULL(CONCAT(e2.title,' '),''), e2.emp_name) AS 'assigneename',  ed2.designation AS 'assigneedesig', pfm.fracasItem ,pfa.fracasassignno FROM pfms_fracas_assign pfa,pfms_fracas_main pfm,employee e1,employee_desig ed1, employee e2, employee_desig ed2   WHERE pfa.assigner=e1.emp_id AND pfa.isactive=1 AND e1.desig_id=ed1.desig_id AND pfa.assignee=e2.emp_id   AND e2.desig_id=ed2.desig_id AND pfm.fracasmainid=pfa.fracasmainid  AND pfa.fracasstatus IN ('A','B') AND pfa.assignee=:assigneeid ) AS x    LEFT JOIN     ( SELECT pfa.fracasattachid,pfa.fracasmainid AS 'mainid',pfa.fracassubid AS 'subid' FROM pfms_fracas_attach pfa )  AS y    ON x.fracasmainid=y.mainid  ";
	private static final String FRACASASSIGNDATA= "SELECT pfa.fracasassignid,pfa.fracasmainid,pfa.remarks,pfa.pdc,pfa.assigner,pfa.assignee,pfa.assigneddate, pfa.fracasstatus, CONCAT(IFNULL(CONCAT(e1.title,' '),''), e1.emp_name) AS 'assignername',ed1.designation AS 'assignerdesig', CONCAT(IFNULL(CONCAT(e2.title,' '),''), e2.emp_name) AS 'assigneename',  ed2.designation AS 'assigneedesig', pfm.fracasItem,pfa.fracasassignno  FROM pfms_fracas_assign pfa,pfms_fracas_main pfm,employee e1,employee_desig ed1, employee e2, employee_desig ed2   WHERE pfa.assigner=e1.emp_id AND e1.desig_id=ed1.desig_id AND pfa.assignee=e2.emp_id   AND e2.desig_id=ed2.desig_id AND pfm.fracasmainid=pfa.fracasmainid AND pfa.fracasassignid=:fracasassignid";
	private static final String FRACASSUBLIST="SELECT * FROM(SELECT pfs.fracassubid ,pfs.fracasassignid,pfs.progress,pfs.progressdate,pfs.remarks FROM pfms_fracas_sub pfs WHERE pfs.fracasassignid=:fracasassignid )  AS x  LEFT JOIN  ( SELECT pfa.fracasattachid,pfa.fracasmainid AS 'mainid',pfa.fracassubid AS 'subid' FROM pfms_fracas_attach pfa ) AS y  ON x.fracassubid = y.subid ";
	private static final String FRACASSUBDELETE= "DELETE FROM pfms_fracas_sub WHERE fracassubid=:fracassubid";
	private static final String FRACASTOREVIEWLIST= "SELECT pfa.fracasassignid,pfa.fracasmainid,pfa.remarks,pfa.pdc,pfa.assigner,pfa.assignee,pfa.assigneddate, pfa.fracasstatus,  CONCAT(IFNULL(CONCAT(e1.title,' '),''), e1.emp_name) AS 'assignername',ed1.designation AS 'assignerdesig', CONCAT(IFNULL(CONCAT(e2.title,' '),''), e2.emp_name) AS 'assigneename', ed2.designation AS 'assigneedesig',  pfm.fracasItem ,(SELECT MAX(pfs.progress) FROM  pfms_fracas_sub pfs WHERE pfa.fracasassignid=pfs.fracasassignid) AS 'Progress'  FROM pfms_fracas_assign pfa,pfms_fracas_main pfm,employee e1,employee_desig ed1, employee e2, employee_desig ed2  WHERE pfa.assigner=e1.emp_id AND pfa.isactive=1 AND e1.desig_id=ed1.desig_id AND pfa.assignee=e2.emp_id AND pfa.fracasstatus IN ('F')  AND e2.desig_id=ed2.desig_id AND pfm.fracasmainid=pfa.fracasmainid AND pfa.assigner=:assignerempid ";
	
	private static final String FRACASMAINASSIGNCOUNT ="SELECT COUNT(Fracasassignid) AS 'count','assignno' FROM pfms_fracas_assign WHERE fracasmainid=:fracasmainid";
	private static final String FRACASATTACHDELETE= "DELETE FROM pfms_fracas_attach WHERE fracasattachid=:fracasattachid";
	
	
	
	@Override
	public List<Object[]> EmployeeList(String LabCode) throws Exception 
	{
		Query query=manager.createNativeQuery(EMPLOYEELIST);
		query.setParameter("LabCode",LabCode);
		List<Object[]> EmployeeList=(List<Object[]>)query.getResultList();	
		return EmployeeList;
	}
	
	@Override
	public Object[] LabDetails()throws Exception
	{
		Query query=manager.createNativeQuery(LABDETAILS);
		Object[] Labdetails =(Object[])query.getResultList().get(0);
		return Labdetails ;
	}
	
	@Override
	public List<Object[]> ProjectsList(String empid,String Logintype, String LabCode)throws Exception
	{
		Query query=manager.createNativeQuery("CALL Pfms_Emp_ProjectList(:empid,:logintype,:labcode);");
		query.setParameter("empid", Long.parseLong(empid));
		query.setParameter("logintype", Logintype);
		query.setParameter("labcode", LabCode);
		List<Object[]> LoginProjectIdList=(List<Object[]>)query.getResultList();
		return LoginProjectIdList;
	}
	
	
	@Override
	public Object[] ProjectsData(String projectid) throws Exception
	{
		Object[] ProjectsData=null;		
		Query query=manager.createNativeQuery(PROJECTSDATA);
		query.setParameter("projectid", Long.parseLong(projectid));
		ProjectsData=(Object[]) query.getSingleResult();
		return ProjectsData;
	}
	
	
	@Override
	public List<Object[]> FracasTypeList() throws Exception
	{
		Query query=manager.createNativeQuery(FRACASTYPELIST);
		List<Object[]> FracasTypeList=(List<Object[]>) query.getResultList();
		return FracasTypeList;
	}
	
	@Override
	public long FracasMainAddSubmit(PfmsFracasMain model) throws Exception
	{
		manager.persist(model);
		manager.flush();
		return model.getFracasMainId();
	}
	
	@Override
	public long FracasAttachAdd(PfmsFracasAttach model) throws Exception
	{
		manager.persist(model);
		manager.flush();
		return model.getFracasAttachId();
	}
	
	
	@Override
	public List<Object[]> ProjectFracasItemsList(String projectid,String LabCode) throws Exception
	{
		Query query=manager.createNativeQuery(PROJECTFRACASITEMSLIST);
		query.setParameter("projectid", Long.parseLong(projectid));
		query.setParameter("LabCode",LabCode);
		List<Object[]> ProjectFracasItemsList=(List<Object[]>) query.getResultList();
		return ProjectFracasItemsList;
	}
	
	@Override
	public PfmsFracasAttach FracasAttachDownload(String fracasattachid) throws Exception
	{
		PfmsFracasAttach attach= manager.find(PfmsFracasAttach .class, Long.parseLong(fracasattachid));
		return attach;
	}
	
	@Override
	public Object[] FracasItemData(String fracasmainid) throws Exception
	{
		Query query=manager.createNativeQuery(FRACASITEMDATA);
		query.setParameter("fracasmainid", Long.parseLong(fracasmainid));
		Object[] FracasItemData=(Object[]) query.getSingleResult();
		return FracasItemData;
	}
	
	@Override
	public long FracasAssignSubmit(PfmsFracasAssign model) throws Exception
	{
		manager.persist(model);
		manager.flush();
		return model.getFracasAssignId();
	}
	
		
	@Override
	public List<Object[]> FracasAssignedList(String assignerempid,String fracasmainid) throws Exception
	{
		Query query=manager.createNativeQuery(FRACASASSIGNEDLIST);
		query.setParameter("assignerempid", Long.parseLong(assignerempid));
		query.setParameter("fracasmainid", Long.parseLong(fracasmainid));
		List<Object[]> FracasSubList=(List<Object[]>) query.getResultList();
		return FracasSubList;
	}
	
	
	@Override
	public List<Object[]> FracasAssigneeList(String assigneeid) throws Exception
	{
		Query query=manager.createNativeQuery(FRACASASSIGNEELIST);
		query.setParameter("assigneeid", Long.parseLong(assigneeid));
		List<Object[]> FracasAssigneeList=(List<Object[]>) query.getResultList();
		return FracasAssigneeList;
	}
				
	@Override
	public Object[] FracasAssignData(String fracasassignid) throws Exception
	{
		Query query=manager.createNativeQuery(FRACASASSIGNDATA);
		query.setParameter("fracasassignid", Long.parseLong(fracasassignid));
		Object[] FracasAssignData=(Object[]) query.getSingleResult();
		return FracasAssignData;
	}
	
	@Override
	public long FracasSubSubmit(PfmsFracasSub model) throws Exception
	{
		manager.persist(model);
		manager.flush();
		return model.getFracasSubId();
	}
	
	
	@Override
	public List<Object[]> FracasSubList(String fracasassignid) throws Exception
	{
		Query query=manager.createNativeQuery(FRACASSUBLIST);
		query.setParameter("fracasassignid", Long.parseLong(fracasassignid));
		List<Object[]> FracasSubList=(List<Object[]>) query.getResultList();
		return FracasSubList;
	}
	

	@Override
	public int FracasAssignForwardUpdate(PfmsFracasAssignDto dto) throws Exception
	{
		PfmsFracasAssign ExistingPfmsFracasAssign = manager.find(PfmsFracasAssign.class, dto.getFracasAssignId());
		if(ExistingPfmsFracasAssign != null) {
			ExistingPfmsFracasAssign.setFracasStatus(dto.getFracasStatus());
			ExistingPfmsFracasAssign.setModifiedBy(dto.getModifiedBy());
			ExistingPfmsFracasAssign.setModifiedDate(dto.getModifiedDate());
			ExistingPfmsFracasAssign.setRemarks(dto.getRemarks());
			ExistingPfmsFracasAssign.setIsActive(Integer.parseInt(dto.getIsActive()));
			return 1;
		}
		else {
			return 0;
		}
		
		
	}
	

	
	@Override
	public List<Object[]> FracasToReviewList(String assignerempid) throws Exception
	{
		Query query=manager.createNativeQuery(FRACASTOREVIEWLIST);
		query.setParameter("assignerempid", Long.parseLong(assignerempid));
		List<Object[]> FracasToReviewList=(List<Object[]>) query.getResultList();
		return FracasToReviewList;
	}
	
	
	@Override
	public int FracasSubDelete(String fracassubid) throws Exception
	{
		Query query=manager.createNativeQuery(FRACASSUBDELETE);
		query.setParameter("fracassubid", Long.parseLong(fracassubid));		
		return query.executeUpdate();
	}
	
	
	@Override
	public int FracasMainDelete(PfmsFracasMainDto dto) throws Exception
	{
		PfmsFracasMain ExistingPfmsFracasMain = manager.find(PfmsFracasMain.class, dto.getFracasMainId());
		if(ExistingPfmsFracasMain != null) {
			ExistingPfmsFracasMain.setIsActive(0);
			ExistingPfmsFracasMain.setModifiedBy(dto.getModifiedBy());
			ExistingPfmsFracasMain.setModifiedDate(dto.getModifiedDate());
			return 1;
		}
		else {
			return 0;
		}
		
	}
	
	
	@Override
	public Object[] FracasMainAssignCount(String fracasmainid) throws Exception
	{
		Query query=manager.createNativeQuery(FRACASMAINASSIGNCOUNT);
		query.setParameter("fracasmainid", Long.parseLong(fracasmainid));
		return ( Object[])query.getSingleResult();
	}
	

	@Override
	public int FracasMainEdit(PfmsFracasMainDto dto) throws Exception
	{
		PfmsFracasMain ExistingPfmsFracasMain= manager.find(PfmsFracasMain.class, dto.getFracasMainId());
		if(ExistingPfmsFracasMain != null) {
			ExistingPfmsFracasMain.setFracasTypeId(Integer.parseInt(dto.getFracasTypeId()));
			ExistingPfmsFracasMain.setFracasItem(dto.getFracasItem());
			ExistingPfmsFracasMain.setFracasDate(dto.getFracasDate());
			ExistingPfmsFracasMain.setProjectId(Long.parseLong(dto.getProjectId()));
			ExistingPfmsFracasMain.setModifiedBy(dto.getModifiedBy());
			ExistingPfmsFracasMain.setModifiedDate(dto.getModifiedDate());
			return 1;
		}
		else {
			return 0;
		}
		
	}
	
	
	@Override
	public int FracasAttachDelete(String fracasattachid) throws Exception
	{
		Query query=manager.createNativeQuery(FRACASATTACHDELETE);
		query.setParameter("fracasattachid", Long.parseLong(fracasattachid));	
		return query.executeUpdate();
	}
	
	@Override
	public long FRACASNotificationInsert(PfmsNotification notification) throws Exception {
		
		manager.persist(notification);
		manager.flush();
		return notification.getNotificationId();
	}
	
	
	@Override
	public List<Object[]> LoginProjectDetailsList(String empid,String Logintype,String LabCode)throws Exception
	{
		Query query=manager.createNativeQuery("CALL Pfms_Emp_ProjectList(:empid,:logintype,:labcode);");
		query.setParameter("empid", Long.parseLong(empid));
		query.setParameter("logintype", Logintype);
		query.setParameter("labcode", LabCode);
		List<Object[]> LoginProjectIdList=(List<Object[]>)query.getResultList();
		return LoginProjectIdList;
	}
	
	
}
