package com.vts.pfms.projectclosure.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.vts.pfms.project.model.ProjectMaster;
import com.vts.pfms.project.model.ProjectMasterRev;
import com.vts.pfms.projectclosure.model.ProjectClosure;
import com.vts.pfms.projectclosure.model.ProjectClosureACP;
import com.vts.pfms.projectclosure.model.ProjectClosureACPAchievements;
import com.vts.pfms.projectclosure.model.ProjectClosureACPConsultancies;
import com.vts.pfms.projectclosure.model.ProjectClosureACPProjects;
import com.vts.pfms.projectclosure.model.ProjectClosureACPTrialResults;
import com.vts.pfms.projectclosure.model.ProjectClosureCheckList;
import com.vts.pfms.projectclosure.model.ProjectClosureCheckListRev;
import com.vts.pfms.projectclosure.model.ProjectClosureSoC;
import com.vts.pfms.projectclosure.model.ProjectClosureTechnical;
import com.vts.pfms.projectclosure.model.ProjectClosureTechnicalAppendices;
import com.vts.pfms.projectclosure.model.ProjectClosureTechnicalChapters;
import com.vts.pfms.projectclosure.model.ProjectClosureTechnicalDocDistrib;
import com.vts.pfms.projectclosure.model.ProjectClosureTechnicalDocSumary;
import com.vts.pfms.projectclosure.model.ProjectClosureTechnicalSection;
import com.vts.pfms.projectclosure.model.ProjectClosureTrans;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;


@Repository
@Transactional
public class ProjectClosureDaoImpl implements ProjectClosureDao{

	private static final Logger logger = LogManager.getLogger(ProjectClosureDaoImpl.class);

	@PersistenceContext
	EntityManager manager;
	
	private static final String PROJECTCLOSURELIST = "SELECT a.project_id,a.project_main_id,a.project_code,a.project_short_name,a.project_name,a.total_sanction_cost,a.is_main_wc,a.sanction_date,a.pdc,b.emp_name,c.designation,\r\n"
			+ "	(SELECT d.ClosureStatusCode FROM pfms_closure cl,pfms_closure_approval_status d WHERE cl.ProjectId=a.project_id AND cl.ClosureStatusCode=d.ClosureStatusCode AND cl.IsActive=1 LIMIT 1) AS 'statuscode',\r\n"
			+ "	(SELECT d.ClosureStatus FROM pfms_closure cl,pfms_closure_approval_status d WHERE cl.ProjectId=a.project_id AND cl.ClosureStatusCode=d.ClosureStatusCode AND cl.IsActive=1 LIMIT 1) AS 'apprstatus',\r\n"
			+ "	(SELECT d.ClosureStatusColor FROM pfms_closure cl,pfms_closure_approval_status d WHERE cl.ProjectId=a.project_id AND cl.ClosureStatusCode=d.ClosureStatusCode AND cl.IsActive=1 LIMIT 1) AS 'statuscolor',\r\n"
			+ "	(SELECT cl.ClosureId FROM pfms_closure cl WHERE cl.ProjectId=a.project_id AND cl.IsActive=1 LIMIT 1) AS 'closureid',\r\n"
			+ "	(SELECT cl.ApprovalFor FROM pfms_closure cl WHERE cl.ProjectId=a.project_id AND cl.IsActive=1 LIMIT 1) AS 'approvalfor',\r\n"
			+ "	(SELECT cl.ClosureCategory FROM pfms_closure cl WHERE cl.ProjectId=a.project_id AND cl.IsActive=1 LIMIT 1) AS 'closurecategory'\r\n"
			+ "	FROM project_master a, employee b, employee_desig c \r\n"
			+ "	WHERE a.is_active=1 AND a.project_director=b.emp_id AND b.desig_id=c.desig_id AND a.lab_code=:LabCode AND (CASE WHEN :LoginType IN ('A','Z','E','L') THEN 1=1 ELSE a.project_director=:EmpId END) ORDER BY a.project_id DESC";
	@Override
	public List<Object[]> projectClosureList(String EmpId, String labcode, String LoginType) throws Exception {
		try {
			Query query = manager.createNativeQuery(PROJECTCLOSURELIST);
			query.setParameter("EmpId", Long.parseLong(EmpId));
			query.setParameter("LabCode", labcode);
			query.setParameter("LoginType", LoginType);
			return (List<Object[]>) query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO projectClosureList "+e);
			return new ArrayList<>();
		}

	}
	
	@Override
	public ProjectMaster getProjectMasterByProjectId(String projectId) throws Exception {
		try {
			return manager.find(ProjectMaster.class, Long.parseLong(projectId));
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getProjectMasterByProjectId "+e);
			return null;
		}
		
	}
	
	@Override
	public ProjectClosure getProjectClosureById(String closureId) throws Exception {
		try {
			return manager.find(ProjectClosure.class, Long.parseLong(closureId));
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getProjectClosureById "+e);
			return null;
		}
		
	}

	@Override
	public long addProjectClosure(ProjectClosure closure) throws Exception{
		try {
			manager.persist(closure);
			manager.flush();
			return closure.getClosureId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addProjectClosure "+e);
			return 0L;
		}
	}

	@Override
	public long editProjectClosure(ProjectClosure closure) throws Exception {
		try {
			manager.merge(closure);
			manager.flush();
			return closure.getClosureId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO editProjectClosure "+e);
			return 0L;
		}
	}
	
	@Override
	public ProjectClosureSoC getProjectClosureSoCByProjectId(String closureId) throws Exception {
		try {
			Query query = manager.createQuery("FROM ProjectClosureSoC WHERE ClosureId=:ClosureId AND IsActive=1");
			query.setParameter("ClosureId", Long.parseLong(closureId));
			List<ProjectClosureSoC> list = (List<ProjectClosureSoC>)query.getResultList();
			if(list.size()>0) {
				return list.get(0);
			}else {
				return null;
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getProjectClosureSoCByProjectId "+e);
			return null;
		}
		
	}
	
	@Override
	public long addProjectClosureSoC(ProjectClosureSoC soc) throws Exception{
		try {
			manager.persist(soc);
			manager.flush();
			return soc.getClosureSoCId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addProjectClosureSoC "+e);
			return 0L;
		}
	}

	@Override
	public long editProjectClosureSoC(ProjectClosureSoC soc) throws Exception {
		try {
			manager.merge(soc);
			manager.flush();
			return soc.getClosureSoCId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO editProjectClosureSoC "+e);
			return 0L;
		}
	}
	
	@Override
	public long addProjectClosureTransaction(ProjectClosureTrans transaction) throws Exception{
		try {
			manager.persist(transaction);
			manager.flush();
			return transaction.getClosureTransId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addProjectClosureTransaction "+e);
			return 0L;
		}
	}
	
	private static final String PROJECTCLOSURETRANSAPPROVALDATABYTYPE = "SELECT a.ClosureTransId,\r\n"
			+ "	(SELECT emp_no FROM pfms_closure_trans t , employee e  WHERE e.emp_id = t.ActionBy AND t.ClosureStatusCode =  b.ClosureStatusCode AND t.ClosureId=d.ClosureId ORDER BY t.ClosureTransId DESC LIMIT 1) AS 'empno',\r\n"
			+ "	(SELECT emp_name FROM pfms_closure_trans t , employee e  WHERE e.emp_id = t.ActionBy AND t.ClosureStatusCode =  b.ClosureStatusCode AND t.ClosureId=d.ClosureId ORDER BY t.ClosureTransId DESC LIMIT 1) AS 'empname',\r\n"
			+ "	(SELECT des.designation FROM pfms_closure_trans t , employee e,employee_desig des WHERE e.emp_id = t.ActionBy AND e.desig_id=des.desig_id AND t.ClosureStatusCode = b.ClosureStatusCode AND t.ClosureId=d.ClosureId ORDER BY t.ClosureTransId DESC LIMIT 1) AS 'Designation',\r\n"
			+ "	MAX(a.ActionDate) AS ActionDate,a.Remarks,b.ClosureStatus,b.ClosureStatusColor,b.ClosureStatusCode\r\n"
			+ "	FROM pfms_closure_trans a,pfms_closure_approval_status b,employee c,pfms_closure d\r\n"
			+ "	WHERE a.ClosureId=d.ClosureId AND a.ClosureStatusCode =b.ClosureStatusCode  AND a.Actionby=c.emp_id AND b.ClosureForward=:ClosureForward AND a.ClosureForm=:ClosureForm AND d.ClosureId=:ClosureId GROUP BY b.ClosureStatusCode,a.ClosureTransId,b.ClosureStatus,b.ClosureStatusColor ORDER BY a.ClosureTransId ASC";
	@Override
	public List<Object[]> projectClosureApprovalDataByType(String closureId, String closureForward, String closureForm) throws Exception{

		try {
			Query query = manager.createNativeQuery(PROJECTCLOSURETRANSAPPROVALDATABYTYPE);
			query.setParameter("ClosureId", Long.parseLong(closureId));
			query.setParameter("ClosureForward", closureForward);
			query.setParameter("ClosureForm", closureForm);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			logger.info(new Date()+"Inside DAO projectClosureApprovalDataByType "+e);
			e.printStackTrace();
			return null;
		}

	}
	
	private static final String PROJECTCLOSUREREMARKSHISTORYBYTYPE  ="SELECT b.ClosureId,b.Remarks,a.ClosureStatusCode,d.emp_name,e.Designation FROM pfms_closure_approval_status a,pfms_closure_trans b,pfms_closure c,employee d,employee_desig e WHERE b.ActionBy = d.emp_id AND d.desig_id = e.desig_id AND a.ClosureStatusCode = b.ClosureStatusCode AND c.ClosureId = b.ClosureId AND TRIM(b.Remarks)<>'' AND a.ClosureForward=:ClosureForward AND b.ClosureForm=:ClosureForm AND c.ClosureId=:ClosureId ORDER BY b.ClosureTransId ASC";
	@Override
	public List<Object[]> projectClosureRemarksHistoryByType(String closureId, String closureForward, String closureForm) throws Exception
	{
		List<Object[]> list =new ArrayList<Object[]>();
		try {
			Query query= manager.createNativeQuery(PROJECTCLOSUREREMARKSHISTORYBYTYPE);
			query.setParameter("ClosureId", Long.parseLong(closureId));
			query.setParameter("ClosureForward", closureForward);
			query.setParameter("ClosureForm", closureForm);
			list= (List<Object[]>)query.getResultList();

		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO projectClosureRemarksHistoryByType " + e);
			e.printStackTrace();
		}
		return list;
	}
	
	private static final String GETEMPGDDETAILS  ="SELECT a.emp_id,a.emp_name,b.Designation FROM employee a,employee_desig b WHERE a.desig_id = b.desig_id AND a.emp_id = (SELECT dm.division_head_id FROM employee emp,division_master dm WHERE dm.division_id = emp.division_id AND emp.emp_id=:EmpId AND emp.is_active=1 LIMIT 1)";
	@Override
	public Object[] getEmpGDDetails(String empId) throws Exception
	{
		try {			
			Query query= manager.createNativeQuery(GETEMPGDDETAILS);
			query.setParameter("EmpId", Long.parseLong(empId));
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			if(list.size()>0) {
				return list.get(0);
			}else {
				return null;
			}			
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO getEmpPDEmpId " + e);
			e.printStackTrace();
			return null;
		}		
	}
	
	private static final String PROJECTCLOSURESOCPENDINGLIST  ="CALL pfms_closure_soc_pending(:EmpId,:LabCode);";
	@Override
	public List<Object[]> projectClosureSoCPendingList(String empId,String labcode) throws Exception {
		try {			
			Query query= manager.createNativeQuery(PROJECTCLOSURESOCPENDINGLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("LabCode", labcode);
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO projectClosureSoCPendingList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}

	}

	private static final String PROJECTCLOSURESOCAPPROVEDLIST  ="CALL pfms_closure_soc_approved(:EmpId,:FromDate,:ToDate);";
	@Override
	public List<Object[]> projectClosureSoCApprovedList(String empId, String FromDate, String ToDate) throws Exception {

		try {			
			Query query= manager.createNativeQuery(PROJECTCLOSURESOCAPPROVEDLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("FromDate", FromDate);
			query.setParameter("ToDate", ToDate);
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO projectClosureSoCApprovedList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}
	
	private static final String PROJECTCLOSURESOCTRANSLISTBYTYPE = "SELECT a.ClosureTransId,c.EmpId,c.EmpName,d.Designation,a.ActionDate,a.Remarks,b.ClosureStatus,b.ClosureStatusColor FROM pfms_closure_trans a,pfms_closure_approval_status b,employee c,employee_desig d,pfms_closure e WHERE e.ClosureId = a.ClosureId AND a.ClosureStatusCode = b.ClosureStatusCode AND a.ActionBy=c.EmpId AND c.DesigId = d.DesigId AND b.ClosureStatusFor=:ClosureStatusFor AND a.ClosureForm=:ClosureForm AND e.ClosureId=:ClosureId ORDER BY a.ClosureTransId DESC";
	@Override
	public List<Object[]> projectClosureTransListByType(String closureId, String closureStatusFor, String closureForm) throws Exception {

		try {
			
			
			Query query = manager.createNativeQuery(PROJECTCLOSURESOCTRANSLISTBYTYPE);
			query.setParameter("ClosureId", Long.parseLong(closureId));
			query.setParameter("ClosureStatusFor", closureStatusFor);
			query.setParameter("ClosureForm", closureForm);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			logger.info(new Date()+"Inside DAO projectClosureTransListByType "+e);
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public ProjectClosureACP getProjectClosureACPByProjectId(String closureId) throws Exception {
		try {
			Query query = manager.createQuery("FROM ProjectClosureACP WHERE ClosureId=:ClosureId AND IsActive=1");
			query.setParameter("ClosureId", Long.parseLong(closureId));
			List<ProjectClosureACP> list = (List<ProjectClosureACP>)query.getResultList();
			if(list.size()>0) {
				return list.get(0);
			}else {
				return null;
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getProjectClosureACPByProjectId "+e);
			return null;
		}
		
	}
	
	@Override
	public long addProjectClosureACP(ProjectClosureACP acp) throws Exception{
		try {
			manager.persist(acp);
			manager.flush();
			return acp.getClosureACPId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addProjectClosureACP "+e);
			return 0L;
		}
	}

	@Override
	public long editProjectClosureACP(ProjectClosureACP acp) throws Exception {
		try {
			manager.merge(acp);
			manager.flush();
			return acp.getClosureACPId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO editProjectClosureACP "+e);
			return 0L;
		}
	}

	@Override
	public long addProjectClosureACPProjects(ProjectClosureACPProjects projects) throws Exception{
		try {
			manager.persist(projects);
			manager.flush();
			return projects.getClosureACPProjectId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addProjectClosureACPProjects "+e);
			return 0L;
		}
	}
	
	@Override
	public long addProjectClosureACPConsultancies(ProjectClosureACPConsultancies consultancies) throws Exception{
		try {
			manager.persist(consultancies);
			manager.flush();
			return consultancies.getConsultancyId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addProjectClosureACPConsultancies "+e);
			return 0L;
		}
	}
	
	@Override
	public long addProjectClosureACPAchievements(ProjectClosureACPAchievements achievements) throws Exception{
		try {
			manager.persist(achievements);
			manager.flush();
			return achievements.getAchievementsId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addProjectClosureACPAchievements "+e);
			return 0L;
		}
	}
	
	@Override
	public long addProjectClosureACPTrialResults(ProjectClosureACPTrialResults trialResults) throws Exception{
		try {
			manager.persist(trialResults);
			manager.flush();
			return trialResults.getTrialResultsId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addProjectClosureACPTrialResults "+e);
			return 0L;
		}
	}

	private static final String PROJECTCLOSUREACPPROJECTSBYPROJECTSBYID = "FROM ProjectClosureACPProjects WHERE ClosureId=:ClosureId AND IsActive=1";
	@Override
	public List<ProjectClosureACPProjects> getProjectClosureACPProjectsByProjectId(String closureId) throws Exception {
		try {
			Query query = manager.createQuery(PROJECTCLOSUREACPPROJECTSBYPROJECTSBYID);
			query.setParameter("ClosureId", Long.parseLong(closureId));
			return (List<ProjectClosureACPProjects>)query.getResultList();

		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getProjectClosureACPProjectsByProjectId "+e);
			return null;
		}
	}
	
	private static final String PROJECTCLOSUREACPPROJECTSBYCONSULTANTSBYID = "FROM ProjectClosureACPConsultancies WHERE ClosureId=:ClosureId AND IsActive=1";
	@Override
	public List<ProjectClosureACPConsultancies> getProjectClosureACPConsultanciesByProjectId(String closureId) throws Exception {
		try {
			Query query = manager.createQuery(PROJECTCLOSUREACPPROJECTSBYCONSULTANTSBYID);
			query.setParameter("ClosureId", Long.parseLong(closureId));
			return (List<ProjectClosureACPConsultancies>)query.getResultList();
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getProjectClosureACPConsultanciesByProjectId "+e);
			return null;
		}
	}
	
	private static final String PROJECTCLOSUREACPPROJECTSBYTRIALRESULTSBYID = "FROM ProjectClosureACPTrialResults WHERE ClosureId=:ClosureId AND IsActive=1";
	@Override
	public List<ProjectClosureACPTrialResults> getProjectClosureACPTrialResultsByProjectId(String closureId) throws Exception {
		try {
			Query query = manager.createQuery(PROJECTCLOSUREACPPROJECTSBYTRIALRESULTSBYID);
			query.setParameter("ClosureId", Long.parseLong(closureId));
			return (List<ProjectClosureACPTrialResults>)query.getResultList();
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getProjectClosureACPTrialResultsByProjectId "+e);
			return null;
		}
	}
	
	private static final String PROJECTCLOSUREACPPROJECTSBYACHIVEMENTSBYID = "FROM ProjectClosureACPAchievements WHERE ClosureId=:ClosureId AND IsActive=1";
	@Override
	public List<ProjectClosureACPAchievements> getProjectClosureACPAchievementsByProjectId(String closureId) throws Exception {
		try {
			Query query = manager.createQuery(PROJECTCLOSUREACPPROJECTSBYACHIVEMENTSBYID);
			query.setParameter("ClosureId", Long.parseLong(closureId));
			return (List<ProjectClosureACPAchievements>)query.getResultList();
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getProjectClosureACPAchievementsByProjectId "+e);
			return null;
		}
	}
	
	private static final String REMOVEPROJECTCLOSUREACPPROJECTDETAILSBYTYPE = "UPDATE pfms_closure_acp_projects SET IsActive=:IsActive WHERE ClosureId=:ClosureId AND (CASE WHEN 'S'=:ACPProjectType THEN ACPProjectType=:ACPProjectType ELSE ACPProjectType IN('R','P') END)";
	@Override
	public int removeProjectClosureACPProjectDetailsByType(long closureId, String acpProjectType) throws Exception{
		try {
			Query query = manager.createNativeQuery(REMOVEPROJECTCLOSUREACPPROJECTDETAILSBYTYPE);
			query.setParameter("IsActive", "0");
			query.setParameter("ClosureId", closureId);
			query.setParameter("ACPProjectType", acpProjectType);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO removeProjectClosureACPProjectDetailsByType "+e);
			return 0;
		}

	}

	private static final String REMOVEPROJECTCLOSUREACPCONSULTANCYDETAILSBYTYPE = "UPDATE pfms_closure_acp_consultancies SET IsActive=:IsActive WHERE ClosureId=:ClosureId";
	@Override
	public int removeProjectClosureACPConsultancyDetails(long closureId) throws Exception {
		try {
			Query query = manager.createNativeQuery(REMOVEPROJECTCLOSUREACPCONSULTANCYDETAILSBYTYPE);
			query.setParameter("IsActive", "0");
			query.setParameter("ClosureId", closureId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO removeProjectClosureACPProjectDetailsByType "+e);
			return 0;
		}

	}
	
	private static final String REMOVEPROJECTCLOSUREACPTRIALRESULTSDETAILSBYTYPE = "UPDATE pfms_closure_acp_trial_results SET IsActive=:IsActive WHERE ClosureId=:ClosureId";
	@Override
	public int removeProjectClosureACPTrialResultsDetails(long closureId) throws Exception {
		try {
			Query query = manager.createNativeQuery(REMOVEPROJECTCLOSUREACPTRIALRESULTSDETAILSBYTYPE);
			query.setParameter("IsActive", "0");
			query.setParameter("ClosureId", closureId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO removeProjectClosureACPTrialResultsDetails "+e);
			return 0;
		}
		
	}
	
	@Override
	public ProjectClosureACPTrialResults getProjectClosureACPTrialResultsById(String trialResultId) throws Exception {
		try {
			return manager.find(ProjectClosureACPTrialResults.class, Long.parseLong(trialResultId));
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getProjectClosureACPTrialResultsById "+e);
			return null;
		}
		
	}

	private static final String REMOVEPROJECTCLOSUREACPACHIVEMENTDETAILSBYTYPE = "UPDATE pfms_closure_acp_achievements SET IsActive=:IsActive WHERE ClosureId=:ClosureId";
	@Override
	public int removeProjectClosureACPAchievementDetails(long closureId) throws Exception {
		try {
			Query query = manager.createNativeQuery(REMOVEPROJECTCLOSUREACPACHIVEMENTDETAILSBYTYPE);
			query.setParameter("IsActive", "0");
			query.setParameter("ClosureId", closureId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO removeProjectClosureACPAchievementDetails "+e);
			return 0;
		}
		
	}

	private static final String PROJECTCLOSUREACPPENDINGLIST  ="CALL pfms_closure_acp_pending(:EmpId,:LabCode);";
	@Override
	public List<Object[]> projectClosureACPPendingList(String empId,String labcode) throws Exception {
		try {			
			Query query= manager.createNativeQuery(PROJECTCLOSUREACPPENDINGLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("LabCode", labcode);
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO projectClosureACPPendingList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}

	}

	private static final String PROJECTCLOSUREACPAPPROVEDLIST  ="CALL pfms_closure_acp_approved(:EmpId,:FromDate,:ToDate);";
	@Override
	public List<Object[]> projectClosureACPApprovedList(String empId, String FromDate, String ToDate) throws Exception {

		try {			
			Query query= manager.createNativeQuery(PROJECTCLOSUREACPAPPROVEDLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("FromDate", FromDate);
			query.setParameter("ToDate", ToDate);
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO projectClosureACPApprovedList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}
	
	private static final String PROJECTORIGINALANDREVISIONDETAILS = "SELECT pt.project_type AS Category, pm.sanction_cost_re AS pmSanctionCostRE, pm.sanction_cost_fe AS pmSanctionCostFE, pm.total_sanction_cost AS pmTotalSanctionCost,\r\n"
			+ "pmr.sanction_cost_re AS pmrevSanctionCostRE, pmr.sanction_cost_fe AS pmrevSanctionCostFE, pmr.total_sanction_cost AS pmrevTotalSanctionCost, pm.pdc AS pmPDC, pmr.pdc AS pmrevPDC,\r\n"
			+ "( SELECT COUNT(*) FROM project_master_rev WHERE project_id = :ProjectId ) AS NoOfRevisions\r\n"
			+ "FROM project_master pm\r\n"
			+ "LEFT JOIN project_type pt ON pt.project_type_id = pm.project_category\r\n"
			+ "LEFT JOIN ( SELECT * FROM project_master_rev WHERE project_id = :ProjectId ORDER BY project_rev_Id LIMIT 1 ) pmr ON 1 = 1\r\n"
			+ "WHERE pm.project_id = :ProjectId AND pm.is_active = 1";
	@Override
	public Object[] projectOriginalAndRevisionDetails(String projectId) throws Exception
	{
		try {			
			Query query= manager.createNativeQuery(PROJECTORIGINALANDREVISIONDETAILS);
			query.setParameter("ProjectId", Long.parseLong(projectId));
				List<Object[]> list =  (List<Object[]>)query.getResultList();
			if(list.size()>0) {
				return list.get(0);
			}else {
				return null;
			}			
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO projectOriginalAndRevisionDetails " + e);
			e.printStackTrace();
			return null;
		}		
	}
	
	private static final String PROJECTEXPENDITUREDETAILS = "SELECT SUM(CASE WHEN ReFe = 'RE' THEN Expenditure ELSE 0 END) AS 'ExpenditureRE',SUM(CASE WHEN ReFe = 'FE' THEN Expenditure ELSE 0 END) AS 'ExpenditureFE' FROM project_hoa WHERE ProjectId = :ProjectId";
	@Override
	public Object[] projectExpenditureDetails(String projectId) throws Exception
	{
		try {			
			Query query= manager.createNativeQuery(PROJECTEXPENDITUREDETAILS);
			query.setParameter("ProjectId", Long.parseLong(projectId));
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			if(list.size()>0) {
				return list.get(0);
			}else {
				return null;
			}			
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO projectExpenditureDetails " + e);
			e.printStackTrace();
			return null;
		}		
	}

	@Override
	public ProjectClosureCheckList getProjectClosureCheckListByProjectId(String closureId) throws Exception {
		
		try {
			Query query = manager.createQuery("FROM ProjectClosureCheckList a WHERE projectClosure.ClosureId=:ClosureId AND a.IsActive=1");
			query.setParameter("ClosureId", Long.parseLong(closureId));
			List<ProjectClosureCheckList> list = (List<ProjectClosureCheckList>)query.getResultList();
			if(list.size()>0) {
				return list.get(0);
			}else {
				return null;
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getProjectClosureSoCByProjectId "+e);
			return null;
		}
	}

	@Override
	public long addProjectClosureCheckList(ProjectClosureCheckList clist) throws Exception {
		
		
		
		try {
			manager.persist(clist);
			manager.flush();
			return clist.getClosureCheckListId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addProjectClosureCheckList "+e);
			return 0L;
		}
	}

	@Override
	public long editProjectClosureCheckList(ProjectClosureCheckList clist) throws Exception {
		
		
		try {
			manager.merge(clist);
			manager.flush();
			return clist.getClosureCheckListId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO editProjectClosureCheckList "+e);
			return 0L;
		}
	}

	@Override
	public long AddIssue(ProjectClosureTechnical tech) throws Exception {
		
		try {
			manager.persist(tech);
			manager.flush();
			return tech.getTechnicalClosureId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO AddIssue "+e);
			return 0L;
		}
	}

	private static final String TECHNICALCLOSURERECORDLIST="SELECT  a.TechnicalClosureId,a.Particulars,a.RevisionNo,a.IssueDate,a.StatusCode,b.ClosureStatus\r\n"
			+ " FROM  pfms_closure_technical a ,pfms_closure_approval_status b WHERE b.ClosureStatusCode=a.StatusCode AND \r\n"
			+ " a.isActive='1' AND a.ClosureId=:closureId ";
	@Override
	public List<Object[]> getTechnicalClosureRecord(String closureId) throws Exception {
		
		try {			
			Query query= manager.createNativeQuery(TECHNICALCLOSURERECORDLIST);
			query.setParameter("closureId", Long.parseLong(closureId));			
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO getTechnicalClosureRecord " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}

	@Override
	public long AddSection(ProjectClosureTechnicalSection sec) throws Exception {
		
		try {
			manager.persist(sec);
			manager.flush();
			return sec.getSectionId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO AddSection "+e);
			return 0L;
		}
	}

	private static final String TECHNICALCLOSURESECTIONLIST="SELECT  SectionId,ClosureId,SectionName ,'S' AS 'selected' FROM pfms_closure_technical_sections \r\n"
			+ "WHERE sectionid IN (SELECT sectionid FROM `pfms_closure_technical_chapters`) AND closureid=:closureId\r\n"
			+ "\r\n"
			+ "UNION \r\n"
			+ "\r\n"
			+ "SELECT  SectionId,ClosureId,SectionName, 'N' AS 'selected' FROM pfms_closure_technical_sections\r\n"
			+ "WHERE sectionid NOT IN (SELECT sectionid FROM `pfms_closure_technical_chapters`) AND closureid=:closureId";
	@Override
	public List<Object[]> getSectionList(String closureId) throws Exception {
		
		try {			
			Query query= manager.createNativeQuery(TECHNICALCLOSURESECTIONLIST);
			query.setParameter("closureId", Long.parseLong(closureId));	
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO getSectionList" + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}

	
	private static final String TECHNICALCLOSURECHAPTERLIST="SELECT a.ChapterId, a.ChapterParentId, a.SectionId, a.ChapterName, a.ChapterContent  FROM  pfms_closure_technical_chapters a ,pfms_closure_technical_sections b WHERE a.SectionId=b.SectionId AND b.ClosureId=:closureId";
	@Override
	public List<Object[]> getChapterList(String closureId) throws Exception {
	
		
		try {			
			Query query= manager.createNativeQuery(TECHNICALCLOSURECHAPTERLIST);
			query.setParameter("closureId", Long.parseLong(closureId));			
			
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO getChapterList" + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}

	@Override
	public long ChapterAdd(ProjectClosureTechnicalChapters chapter) throws Exception {
		
		try {
			manager.persist(chapter);
			manager.flush();
			return chapter.getChapterId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO ChapterAdd "+e);
			return 0L;
		}
	}

	@Override
	public ProjectClosureTechnicalSection getProjectClosureTechnicalSectionById(String id) throws Exception {
		
		try {
			return manager.find(ProjectClosureTechnicalSection.class, Long.parseLong(id));
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getProjectClosureTechnicalSectionById "+e);
			return null;
		}
		
	}

	@Override
	public long ChapterEdit(String chapterId, String chapterName,String ChapterContent) throws Exception {
		
		ProjectClosureTechnicalChapters chapter=manager.find(ProjectClosureTechnicalChapters.class,Long.parseLong(chapterId));	
		chapter.setChapterName(chapterName);
		chapter.setChapterContent(ChapterContent);
		chapter=manager.merge(chapter);
		if(chapter!=null) {
			return 1;
		}else
		{
			return 0;
		}
		
	}

	private static final String TECHNICALCLOSURECHAPTERCONTENT="SELECT chapterid,chaptercontent FROM pfms_closure_technical_chapters WHERE chapterid=:chapterId";
	@Override
	public Object[] getChapterContent(String chapterId) throws Exception {
		
		try {			
			Query query= manager.createNativeQuery(TECHNICALCLOSURECHAPTERCONTENT);
			query.setParameter("chapterId", Long.parseLong(chapterId));
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			if(list.size()>0) {
				return list.get(0);
			}else {
				return null;
			}			
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO getChapterContent " + e);
			e.printStackTrace();
			return null;
		}		
	}

	private static final String APPENDICESDOCLIST="SELECT AppndMasterId,AppndDocName FROM pfms_closure_appendices";
	@Override
	public List<Object[]> getAppndDocList() throws Exception {
		
		try {			
			Query query= manager.createNativeQuery(APPENDICESDOCLIST);
			
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO getAppndDocList" + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}

	@Override
	public long ProjectClosureAppendixDocSubmit(ProjectClosureTechnicalAppendices appnd) throws Exception {
		
		try {
			manager.persist(appnd);
			manager.flush();
			return appnd.getAppendicesId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO ProjectClosureAppendixDocSubmit "+e);
			return 0L;
		}
	}

	private static final String APPENDICESLIST="SELECT AppendicesId,Appendix,DocumentName,DocumentAttachment FROM pfms_closure_technical_appendices a,\r\n"
			+ "pfms_closure_technical_chapters  b, pfms_closure_technical_sections c WHERE a.isActive='1' AND a.ChapterId=b.ChapterId AND \r\n"
			+ "c.SectionId=b.SectionId AND c.ClosureId=:closureId";
	@Override
	public List<Object[]>  getAppendicesList(String closureId) throws Exception {
		
		
		try {			
			Query query= manager.createNativeQuery(APPENDICESLIST);
			query.setParameter("closureId", Long.parseLong(closureId));
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO getAppndDocList" + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}

	@Override
	public ProjectClosureTechnicalAppendices getProjectClosureTechnicalAppendicesById(String attachmentfile)
			throws Exception {
		
		try {
			return manager.find(ProjectClosureTechnicalAppendices.class, Long.parseLong(attachmentfile));
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getProjectClosureTechnicalAppendicesById "+e);
			return null;
		}
	}

	private static final String REMOVEPROJECTCLOSUREAPPENDIXDOC="UPDATE pfms_closure_technical_appendices SET isActive=:IsActive WHERE ChapterId=:chapterId";
	@Override
	public int removeProjectClosureProjectClosureAppendixDoc(long chapterId) throws Exception {
		try {
			Query query = manager.createNativeQuery(REMOVEPROJECTCLOSUREAPPENDIXDOC);
			query.setParameter("IsActive", "0");
			query.setParameter("chapterId", chapterId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO removeProjectClosureACPTrialResultsDetails "+e);
			return 0;
		}
		
	}

	private static final String TECHCLOSURECONTENT="SELECT DISTINCT a.ChapterId,a.ChapterParentId,a.SectionId,a.ChapterName,a.ChapterContent FROM \r\n"
			+ "pfms_closure_technical_chapters a,\r\n"
			+ "pfms_closure_technical_sections b,pfms_closure_technical c WHERE \r\n"
			+ "a.SectionId=b.SectionId AND b.ClosureId=c.ClosureId AND c.ClosureId=:closureId ORDER BY ChapterParentId";
	@Override
	public List<Object[]> getTechnicalClosureContent(String closureId) throws Exception {
		
		try {			
			Query query= manager.createNativeQuery(TECHCLOSURECONTENT);
			query.setParameter("closureId", Long.parseLong(closureId));
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO getTechnicalClosureContent" + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}

	@Override
	public long addDocSummary(ProjectClosureTechnicalDocSumary rs) throws Exception {
		
		try {
			manager.persist(rs);
			manager.flush();
			return rs.getSummaryId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addDocSummary "+e);
			return 0L;
		}
	}

	private static final String DOCSUMUPD="UPDATE pfms_closure_technical_docsumary SET AdditionalInformation=:AdditionalInformation,Abstract=:Abstract,Keywords=:Keywords,Distribution=:Distribution , reviewer=:reviewer,approver=:approver,ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate,PreparedBy=:PreparedBy WHERE SummaryId=:SummaryId AND isactive='1'";

	
	@Override
	public long editDocSummary(ProjectClosureTechnicalDocSumary rs) throws Exception {
		
		Query query = manager.createNativeQuery(DOCSUMUPD);
		query.setParameter("AdditionalInformation", rs.getAdditionalInformation());			
		query.setParameter("Abstract", rs.getAbstract());			
		query.setParameter("Keywords", rs.getKeywords());			
		query.setParameter("Distribution", rs.getDistribution());			
		query.setParameter("reviewer", rs.getReviewer());			
		query.setParameter("approver", rs.getApprover());			
		query.setParameter("ModifiedBy", rs.getModifiedBy());			
		query.setParameter("ModifiedDate", rs.getModifiedDate());			
		query.setParameter("SummaryId", rs.getSummaryId());	
		query.setParameter("PreparedBy", rs.getPreparedBy());	
		
		
		return query.executeUpdate();
	}

	private static final String TECHCLOSUREDOCUMENTSUMMARY="SELECT a.AdditionalInformation,a.Abstract,a.Keywords,a.Distribution,a.reviewer,a.approver,\r\n"
			+ "(SELECT CONCAT(IFNULL(CONCAT(e.title,' '),''), e.emp_name)FROM employee e WHERE e.emp_id=a.approver ) AS 'Approver1',\r\n"
			+ "(SELECT CONCAT(IFNULL(CONCAT(e.title,' '),''), e.emp_name)FROM employee e WHERE e.emp_id=a.reviewer) AS 'Reviewer1',\r\n"
			+ "a.summaryid,a.preparedby,(SELECT CONCAT(IFNULL(CONCAT(e.title,' '),''), e.emp_name)FROM \r\n"
			+ "employee e WHERE e.emp_id=a.PreparedBy) AS 'PreparedBy1',\r\n"
			+ "(SELECT p.project_description FROM project_master p WHERE p.project_id=b.ProjectId) AS 'ProjectNo',\r\n"
			+ "(SELECT p.project_name FROM project_master p WHERE p.project_id=b.ProjectId) AS 'ProjectName',\r\n"
			+ "\r\n"
			+ "(SELECT c.Classification FROM project_master p,pfms_security_classification c WHERE p.project_id=b.ProjectId AND p.project_category=c.classification_id) AS 'Projectcategory'\r\n"
			+ " FROM pfms_closure_technical_docsumary a,pfms_closure b  WHERE a.ClosureId=b.ClosureId AND \r\n"
			+ " a.ClosureId =:closureId AND a.isactive='1'";
	
	@Override
	public List<Object[]> getDocumentSummary(String closureId) throws Exception {
		
		try {			
			Query query= manager.createNativeQuery(TECHCLOSUREDOCUMENTSUMMARY);
			query.setParameter("closureId", Long.parseLong(closureId));
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO getTechnicalClosureContent" + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}

	@Override
	public ProjectClosureTechnical getProjectClosureTechnicalById(String closureId) throws Exception {
		
		try {
			return manager.find(ProjectClosureTechnical.class, Long.parseLong(closureId));
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getProjectClosureTechnicalById "+e);
			return null;
		}
	}

	@Override
	public long UpdateProjectClosureTechnical(ProjectClosureTechnical closure) throws Exception {
		
		try {
			manager.merge(closure);
			manager.flush();
			return closure.getTechnicalClosureId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO UpdateProjectClosureTechnical "+e);
			return 0L;
		}
	}

	private static String PROJECTCTECHLOSURETRANSLISTBYTYPE="SELECT a.ClosureTransId,c.EmpId,c.EmpName,d.Designation,a.ActionDate,a.Remarks,b.ClosureStatus,\r\n"
			+ "b.ClosureStatusColor FROM pfms_closure_trans a,pfms_closure_approval_status b,employee c,\r\n"
			+ "employee_desig d,pfms_closure_technical e WHERE e.TechnicalClosureId = a.ClosureId AND\r\n"
			+ " a.ClosureStatusCode = b.ClosureStatusCode AND a.ActionBy=c.EmpId AND c.DesigId = d.DesigId \r\n"
			+ "AND b.ClosureStatusFor=:ClosureStatusFor AND a.ClosureForm=:ClosureForm AND e.TechnicalClosureId=:techClosureId\r\n"
			+ "ORDER BY a.ClosureTransId DESC";
	@Override
	public List<Object[]> projectTechClosureTransListByType(String techClosureId, String closureStatusFor, String closureForm)
			throws Exception {
		
		 try {
			
			
			Query query = manager.createNativeQuery(PROJECTCTECHLOSURETRANSLISTBYTYPE);
			query.setParameter("techClosureId", Long.parseLong(techClosureId));
			query.setParameter("ClosureStatusFor", closureStatusFor);
			query.setParameter("ClosureForm", closureForm);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			logger.info(new Date()+"Inside DAO projectClosureTransListByType "+e);
			e.printStackTrace();
			return null;
		}
	}

	private static String TECHCLOSUREPENDINGLIST="CALL pfms_closure_tcr_pending(:EmpId,:LabCode)";
	@Override
	public List<Object[]> projectTechClosurePendingList(String empId, String labcode) throws Exception {
		
		try {			
			Query query= manager.createNativeQuery(TECHCLOSUREPENDINGLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("LabCode", labcode);
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO projectTechClosurePendingList" + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}

	private static String TECHCLOSUREAPPROVEDLIST="CALL pfms_closure_tcr_approved(:EmpId,:FromDate,:ToDate)";
	@Override
	public List<Object[]> projectTechClosureApprovedList(String empId, String fromdate, String todate) throws Exception {
		
		try {			
			Query query= manager.createNativeQuery(TECHCLOSUREAPPROVEDLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("FromDate", fromdate);
			query.setParameter("ToDate", todate);
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO projectTechClosureApprovedList" + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}

	@Override
	public long AddDocDistribMembers(ProjectClosureTechnicalDocDistrib r) throws Exception {
		
		try {
			manager.persist(r);
			manager.flush();
			return r.getDistrubtionId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO AddDocDistribMembers "+e);
			return 0L;
		}
	}

	private static final String TECHCLOSUREDOCDISTRIBLIST=" SELECT a.empid,CONCAT(IFNULL(CONCAT(a.title,' '),''), a.empname) AS 'empname' ,\r\n"
			+ " b.designation,a.labcode,b.desigid FROM employee a,employee_desig b,\r\n"
			+ " pfms_closure_technical_docdistr c WHERE a.isactive='1' AND a.DesigId=b.DesigId \r\n"
			+ " AND  a.empid = c.empid AND c.TechnicalClosureId =:techClosureId AND c.isactive =1 ORDER BY b.desigid ASC";
	@Override
	public List<Object[]> getDocSharingMemberList(String techClosureId) throws Exception {
		
		try {			
			Query query= manager.createNativeQuery(TECHCLOSUREDOCDISTRIBLIST);
			query.setParameter("techClosureId", Long.parseLong(techClosureId));
			
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO getDocSharingMemberList" + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}

	private static final String TCRFreeze="UPDATE pfms_closure_technical SET TCRFreeze=:TCRFreeze WHERE TechnicalClosureId=:techclosureId AND IsActive=1";
	@Override
	public int TCRFreeze(String techclosureId, String filepath) throws Exception {
		
		try {
			Query query = manager.createNativeQuery(TCRFreeze);
			query.setParameter("techclosureId", Long.parseLong(techclosureId));
			query.setParameter("TCRFreeze", filepath);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO TCRFreeze "+e);
			return 0;
		}
	}

	@Override
	public long AddProjectClosureCheckListRev(ProjectClosureCheckListRev rev)
			throws Exception {
		
		try {
			manager.persist(rev);
			manager.flush();
			return rev.getRevisionId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO AddDocDistribMembers "+e);
			return 0L;
		}
	}

	
	private static final String REMOVEPROJECTCLOSURECHECKLISTREV="UPDATE pfms_closure_checklist_rev SET isActive=:IsActive WHERE ClosureId=:ClosureId";
	@Override
	public long removeProjectClosureCheckListRev(long closureid) throws Exception {
		
		try {
			Query query = manager.createNativeQuery(REMOVEPROJECTCLOSURECHECKLISTREV);
			query.setParameter("IsActive", "0");
			query.setParameter("ClosureId", closureid);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO removeProjectClosureCheckListRev "+e);
			return 0;
		}
	}

	
	private static final String CHECHLISTREVISION="SELECT ClosureId,RevisionType,RequestedDate,GrantedDate,RevisionCost,RevisionPDC,Reason FROM pfms_closure_checklist_rev WHERE isActive='1' AND ClosureId=:closureId";
	@Override
	public List<Object[]> getProjectClosureCheckListRevByClosureId(String closureId) throws Exception {
		
		try {			
			Query query= manager.createNativeQuery(CHECHLISTREVISION);
			query.setParameter("closureId", Long.parseLong(closureId));
			
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO getProjectClosureCheckListRevByClosureId" + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
		
		
	}

	
	private static final String TCRTEMPLATESECTIONLIST="SELECT sectionid,SectionName FROM pfms_closure_tech_template_sections WHERE IsActive='1'";
	@Override
	public List<Object[]> TCRTemplateSectionList() throws Exception {
		
		try {			
			Query query= manager.createNativeQuery(TCRTEMPLATESECTIONLIST);
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO TCRTemplateSectionList" + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}

	private static final String TCRTEMPLATECHAPTERLISTBYSECTIONID="SELECT a.ChapterId, a.ChapterParentId, a.SectionId, a.ChapterName, a.ChapterContent FROM pfms_closure_tech_template_chapters a WHERE a.SectionId=:sectionid AND a.IsActive=1 ";
	@Override
	public List<Object[]> TCRTemplateChapterListBySectionId(String sectionid) throws Exception {
		
		
		try {			
			Query query= manager.createNativeQuery(TCRTEMPLATECHAPTERLISTBYSECTIONID);
			query.setParameter("sectionid", Long.parseLong(sectionid));
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO TCRTemplateChapterListBySectionId" + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}

	private static final String TCRTEMPLATECHAPTERLISTBYCHAPTERPARENTID="SELECT a.ChapterId, a.ChapterParentId, a.SectionId, a.ChapterName, a.ChapterContent FROM pfms_closure_tech_template_chapters a WHERE a.ChapterParentId=:chapterparentid AND a.IsActive=1";
	@Override
	public List<Object[]> TCRTemplateChapterListByChapterParentId(String chapterparentid) throws Exception {
		
		
		try {			
			Query query= manager.createNativeQuery(TCRTEMPLATECHAPTERLISTBYCHAPTERPARENTID);
			query.setParameter("chapterparentid", Long.parseLong(chapterparentid));
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO TCRTemplateChapterListByChapterParentId" + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}

	
	private static final String AMENDCLOSURELIST="UPDATE pfms_closure_technical SET StatusCode='TAM',StatusCodeNext='TAM' WHERE  TechnicalClosureId=:techClosureId";
	@Override
	public int AmendTechClosureList(String techClosureId) throws Exception {
		
		try {			
			Query query= manager.createNativeQuery(AMENDCLOSURELIST);
			query.setParameter("techClosureId", Long.parseLong(techClosureId));
			return query.executeUpdate();
			
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO AmendTechClosureList " + e);
			e.printStackTrace();
			return 0;
		}
	}
	
	@Override
	public List<ProjectMasterRev> getProjectMasterRevListByProjectId(String projectId) throws Exception {
		try {
			
			Query query = manager.createQuery("FROM ProjectMasterRev WHERE ProjectId=:ProjectId ORDER BY PDC");
			query.setParameter("ProjectId", Long.parseLong(projectId));
			return (List<ProjectMasterRev>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<ProjectMasterRev>();
		}
		
	}

}
