package com.vts.pfms.header.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.vts.pfms.header.model.ProjectDashBoardFavourite;
import com.vts.pfms.header.model.ProjectDashBoardFavouriteProjetcts;
import com.vts.pfms.login.LoginPasswordHistory;
import com.vts.pfms.roadmap.model.RoadMap;
@Transactional
@Repository
public class HeaderDaoImpl implements HeaderDao {

	private static final String LOGINTYPELIST = "select logintype,logindesc from login_type ";
	private static final String NOTIFICATIONLISTALL ="SELECT empid,notificationby,notificationdate,notificationmessage,notificationurl FROM rfpwizard_notification WHERE  empid=:empid ORDER BY notificationdate";
	private static final String EMPDETAILES="SELECT CONCAT(IFNULL(CONCAT(b.title,' '),''), b.emp_name) AS 'empname', c.formrolename, b.emp_no,b.lab_code FROM login a,employee b,form_role c WHERE a.empid=b.emp_id AND a.formroleid=c.formroleid AND a.loginid=:loginid";
	private static final String DIVISIONNAME="select division_code from division_master where division_id=:divisionid";
	
	//private static final String NOTIFICATIONLIST="select empid,notificationby,notificationdate,notificationmessage,notificationurl,notificationid from pfms_notification where isactive='1' and empid=:empid ORDER BY notificationdate DESC";
	// new query
	private static final String NOTIFICATIONLIST="select empid,notificationby,notificationdate,notificationmessage,notificationurl,notificationid from pfms_notification where isactive='1' and empid=:empid ORDER BY CreatedDate DESC";
	private static final String NOTIFICATIONUPDATE="update pfms_notification set isactive='0' where isactive='1' and notificationid=:notificationid ";
	private static final String OLDPASSWORD="select password from login where username=:username";
	private static final String PASSWORDUPDATECHANGE="update login set password=:newpassword,modifiedby=:modifiedby,modifieddate=:modifieddate where username=:username ";
	private static final String LOGINTYPENAME="SELECT logindesc FROM login_type WHERE logintype=:logintype";
	private static final String PROJECTLIST="SELECT projectid,projectcode,projectname,projectmainid,projecttype FROM project_master WHERE isactive=1";
	private static final String GANTTCHARTLIST="SELECT milestoneactivityid,projectid,activityname,milestoneno,orgstartdate,orgenddate,startdate,enddate,progressstatus,revisionno FROM milestone_activity WHERE isactive=1 AND projectid=:projectid ORDER BY MilestoneNo";

	private static final String PROJECTMASTER="SELECT a.project_id,a.project_code,a.project_name, a.project_short_name FROM project_master a WHERE  a.is_active='1' AND a.project_flag <> 'C'";
	private static final String PROJECTDETAILS="SELECT a.project_id,a.project_code,a.project_name, a.sanction_date, a.pdc FROM project_master a WHERE a.project_id=:projectid and  a.is_active='1'";
	private static final String LABDETAILS ="SELECT lab_id,cluster_id,lab_city FROM lab_master WHERE lab_code=:labcode"; 

	private static final String HEADERSCHEDULELIST ="SELECT a.formname,a.formurl FROM pfms_form_detail a , pfms_form_role_access b WHERE a.formdetailid=b.formdetailid AND a.formmoduleid=:formmoduleid AND b.logintype=:logintype AND b.isactive=1 AND CASE WHEN 'Y'= (SELECT is_cluster FROM lab_master WHERE lab_code=:labcode) THEN labhq IN ('H','B') ELSE labhq IN ('L','B') END AND a.isactive=1 ORDER BY a.formserialno";
	private static final String FROMMODULELIST = "SELECT DISTINCT a.formmoduleid, a.formmodulename, a.moduleurl, a.isnav  FROM pfms_form_module a, pfms_form_detail b, pfms_form_role_access c WHERE a.isactive='1' AND a.formmoduleid=b.formmoduleid AND b.formdetailid=c.formdetailid AND c.logintype=:logintype AND c.isactive=1 AND CASE WHEN 'Y'= (SELECT is_cluster FROM lab_master WHERE lab_code=:labcode) THEN labhq IN ('H','B') ELSE labhq IN ('L','B') END ORDER BY FIELD(a.FormModuleName, 'Road Map','Admin','Master','Approvals','Closure') , FIELD(a.formmoduleid, '18', a.formmoduleid), a.formmoduleid";
	private static final String PROJECTINTILIST="SELECT a.initiationid,a.projectprogramme,b.project_type_short,c.classification,a.projectshortname,a.projecttitle,a.projectcost,a.projectduration,a.ismain FROM pfms_initiation a,project_type b, pfms_security_classification c WHERE a.empid=:empid AND a.classificationid=c.classification_id  AND a.projecttypeid=b.project_type_id AND a.isactive='1' ";
	private static final String MALIST="SELECT a.milestoneactivityid,0 AS 'parentactivityid', a.activityname,a.orgstartdate,a.orgenddate,a.startdate,a.enddate,a.progressstatus,a.revisionno,(SELECT MAX(b.enddate) FROM milestone_activity b WHERE b.projectid= :ProjectId )AS 'lastEnddate' FROM milestone_activity a WHERE  a.isactive=1 AND a.projectid=:ProjectId";
	private static final String MILEACTIVITYLEVEL="SELECT a.activityid ,a.parentactivityid, a.activityname,a.orgstartdate,a.orgenddate , a.startdate, a.enddate,  a.progressstatus,a.revision  FROM milestone_activity_level a WHERE a.parentactivityid=:id AND a.activitylevelid=:levelid ";
	private static final String QUICKLINKLIST="SELECT a.formname,a.formurl FROM pfms_form_detail a , pfms_form_role_access b WHERE a.formdetailid=b.formdetailid AND a.formmoduleid=13 AND b.logintype=:logintype AND b.isactive=1";
	private static final String LABCODE= "SELECT b.lab_code FROM login a,employee b WHERE a.empid=b.emp_id AND a.username=:empid";
	private static final String LABMASTERLIST="SELECT a.lab_name,a.lab_code,a.is_cluster FROM lab_master a WHERE CASE WHEN :clusterid=9 THEN 1=1 AND  cluster_id<>9  ELSE cluster_id=:clusterid END ORDER BY lab_code";
	
	
	@PersistenceContext
	EntityManager manager;
	
	private static final Logger logger=LogManager.getLogger(HeaderDaoImpl.class);

	@Override
	public List<Object[]> FormModuleList(String LoginType,String LabCode) throws Exception {
		Query query = manager.createNativeQuery(FROMMODULELIST);
		query.setParameter("logintype", LoginType);
		query.setParameter("labcode", LabCode);
		List<Object[]> FormModuleList= query.getResultList();
		return FormModuleList;
	}

	@Override
	public List<Object[]> loginTypeList() throws Exception {
		Query query = manager.createNativeQuery(LOGINTYPELIST);
	
		List<Object[]> loginTypeList= query.getResultList();
		return loginTypeList;
	}

	@Override
	public List<Object[]> DashboardDemandCount() throws Exception {
		
		Query query=manager.createNativeQuery("CALL DashboardDemandCount()");
	
		List<Object[]> DashboardDemandCount=(List<Object[]>)query.getResultList();	
			
			return DashboardDemandCount;
	}

	@Override
	public List<Object[]> NotificationList(String Empid) throws Exception {
		
		
		Query query = manager.createNativeQuery(NOTIFICATIONLIST);
		
		query.setParameter("empid", Long.parseLong(Empid));
		
		List<Object[]> NotificationList= query.getResultList();
		return NotificationList;
	}

	@Override
	public int NotificationUpdate(String NotificationId) throws Exception {
		
		Query query = manager.createNativeQuery(NOTIFICATIONUPDATE);
		
		query.setParameter("notificationid", Long.parseLong(NotificationId));
		
		int count= (int)query.executeUpdate();
		return count;
	}

	@Override
	public List<Object[]> NotificationAllList(String Empid) throws Exception {
		
		Query query = manager.createNativeQuery(NOTIFICATIONLISTALL);
		query.setParameter("empid", Long.parseLong(Empid));
		List<Object[]> NotificationList= query.getResultList();
		return NotificationList;
	}

	@Override
	public List<Object[]> EmployeeDetailes(String LoginId) throws Exception {
		
		Query query = manager.createNativeQuery(EMPDETAILES);
		query.setParameter("loginid", Long.parseLong(LoginId));
		List<Object[]> EmployeeDetailes= query.getResultList();
		return EmployeeDetailes;
	}

	@Override
	public String DivisionName(String DivisionId) throws Exception {
		
		Query query = manager.createNativeQuery(DIVISIONNAME);
		query.setParameter("divisionid", Long.parseLong(DivisionId));
		String DivisionName= (String) query.getSingleResult();
		return DivisionName;
	}

	@Override
	public List<Object[]> TodaySchedulesList(String EmpId, String TodayDate) throws Exception {

		Query query=manager.createNativeQuery("CALL Pfms_Schedule_Today(:empid,:date)");
		query.setParameter("empid",Long.parseLong(EmpId));
		query.setParameter("date", TodayDate);
		
		return (List<Object[]>) query.getResultList();
	}

	@Override
	public List<Object[]> TodayActionList(String EmpId) throws Exception {
		Query query=manager.createNativeQuery("CALL Pfms_Action_PDC(:empid)");
		query.setParameter("empid", Long.parseLong(EmpId));
		
		return (List<Object[]>) query.getResultList();
	}

	@Override
	public String OldPassword(String UserId) throws Exception {
		Query query = manager.createNativeQuery(OLDPASSWORD);
		query.setParameter("username", UserId);
		
		String OldPassword = (String) query.getSingleResult();
		return   OldPassword;
	}

	@Override
	public int PasswordChange(String OldPassword, String NewPassword ,String UserName, String ModifiedDate)
			throws Exception {
		
		Query query = manager.createNativeQuery(PASSWORDUPDATECHANGE);
		
		query.setParameter("newpassword", NewPassword);
		query.setParameter("username", UserName);
		query.setParameter("modifiedby", UserName);
		query.setParameter("modifieddate", ModifiedDate);
		int PasswordChange = (int) query.executeUpdate();
		return  PasswordChange;
	}

	@Override
	public String FormRoleName(String LoginType) throws Exception {
		
		Query query=manager.createNativeQuery(LOGINTYPENAME);
		query.setParameter("logintype",LoginType);
		String FormRoleName=(String)query.getSingleResult();
		
		return FormRoleName;
	}

	@Override
	public List<Object[]> GanttChartList(String ProjectId) throws Exception {
		
		Query query = manager.createNativeQuery(GANTTCHARTLIST);
		query.setParameter("projectid", Long.parseLong(ProjectId));
		List<Object[]> GanttChartList= query.getResultList();
		return GanttChartList;
	}
	
	@Override
	public List<Object[]> FullGanttChartList(String ProjectId) throws Exception 
	{		
		Query query = manager.createNativeQuery("CALL Pfms_Milestone_All(:projectid);");
		query.setParameter("projectid", Long.parseLong(ProjectId));
		List<Object[]> FullGanttChartList= query.getResultList();
		return FullGanttChartList;
	}
	

	@Override
	public List<Object[]> ProjectList() throws Exception {

		Query query=manager.createNativeQuery(PROJECTMASTER);
		
		List<Object[]> ProjectList=(List<Object[]>)query.getResultList();		

		return ProjectList;
	}
	
	@Override
	public List<Object[]> ProjectDetails(String ProjectId) throws Exception {
		Query query=manager.createNativeQuery(PROJECTDETAILS);
		query.setParameter("projectid", Long.parseLong(ProjectId));
		List<Object[]> ProjectList=(List<Object[]>)query.getResultList();		

		return ProjectList;
	}

	
	@Override
	public Object[] LabDetails(String labcode) throws Exception {
		
		try {
		Query query=manager.createNativeQuery(LABDETAILS);
		query.setParameter("labcode", labcode);
		Object[] ProjectList=(Object[])query.getSingleResult();	
		
		return ProjectList;
		}
		catch (Exception e) {
			logger.error(new Date() +"Inside DAO LabDetails "+ e);
			return  null;
		}
		
	}

	@Override
	public List<Object[]> HeaderSchedulesList(String Logintype,String FormModuleId,String LabCode) throws Exception {

		Query query=manager.createNativeQuery(HEADERSCHEDULELIST);
		query.setParameter("logintype",Logintype);
		query.setParameter("formmoduleid", Long.parseLong(FormModuleId));
		query.setParameter("labcode", LabCode);
		List<Object[]> HeaderSchedulesList=(List<Object[]>)query.getResultList();		

		return HeaderSchedulesList;
	}

	@Override
	public List<Object[]> ProjectIntiationList(String Empid,String LoginType) throws Exception {
		
		Query query=manager.createNativeQuery(PROJECTINTILIST);
		query.setParameter("empid", Long.parseLong(Empid));
		//query.setParameter("logintype", LoginType);
		List<Object[]> ProjectIntiationList=(List<Object[]>)query.getResultList();		

		
		return ProjectIntiationList;
	}

	@Override
	public List<Object[]> MyTaskList(String EmpId) throws Exception {
		
		
		Query query=manager.createNativeQuery("CALL `Dashboard_Mytask` (:empid)");
		query.setParameter("empid", Long.parseLong(EmpId));
		List<Object[]> MyTaskList=(List<Object[]>)query.getResultList();		
		
		return MyTaskList;
	}
	
	@Override
	public List<Object[]> ApprovalList(String EmpId,String LoginType) throws Exception {
		
		
		Query query=manager.createNativeQuery("CALL `Dashboard_Approvals` (:empid,:logintype)");
		query.setParameter("empid", Long.parseLong(EmpId));
		query.setParameter("logintype", LoginType);
		List<Object[]> ApprovalList=(List<Object[]>)query.getResultList();		
		
		return ApprovalList;
	}
	
	@Override
	public List<Object[]> MyTaskDetails(String EmpId) throws Exception {
		
		
		Query query=manager.createNativeQuery("CALL `Dashboard_Mytask_Details` (:empid)");
		query.setParameter("empid", Long.parseLong(EmpId));
		List<Object[]> MyTaskDetails=(List<Object[]>)query.getResultList();		
		
		return MyTaskDetails;
	}
	
	@Override
	public List<Object[]> DashboardActionPdc(String EmpId,String LoginType) throws Exception {
		
		
		Query query=manager.createNativeQuery("CALL `Dashboard_Action_PDC` (:empid,:logintype)");
		query.setParameter("empid", Long.parseLong(EmpId));
		query.setParameter("logintype", LoginType);
		List<Object[]> DashboardActionPdc=(List<Object[]>)query.getResultList();		
		
		return DashboardActionPdc;
	}
	
	
	
	
	@Override
	public List<Object[]> MilestoneActivityList(String ProjectId) throws Exception {

		Query query=manager.createNativeQuery(MALIST);
		query.setParameter("ProjectId", Long.parseLong(ProjectId));
		List<Object[]> MilestoneActivityList=(List<Object[]>)query.getResultList();		

		return MilestoneActivityList;
	}
	
	
	@Override
	public List<Object[]> MilestoneActivityLevel(String MilestoneActivityId,String LevelId) throws Exception {
		Query query=manager.createNativeQuery(MILEACTIVITYLEVEL);
		query.setParameter("id", Long.parseLong(MilestoneActivityId));
		query.setParameter("levelid", Long.parseLong(LevelId));
		List<Object[]> MilestoneActivityList=(List<Object[]>)query.getResultList();		

		return MilestoneActivityList;
	}

	@Override
	public List<Object[]> QuickLinksList(String LoginType) throws Exception {
		
		Query query= manager.createNativeQuery(QUICKLINKLIST);
		query.setParameter("logintype", LoginType);
		List<Object[]> QuickLinkList= (List<Object[]>)query.getResultList();
		
		return QuickLinkList;
		
	}
	
	@Override
	public String getLabCode(String Empid) throws Exception {
		

		Query query = manager.createNativeQuery(LABCODE);
		query.setParameter("empid", Empid);
		
		return (String)query.getSingleResult();
	}
	
	@Override
	public List<Object[]> LabMasterList(String Clusterid) throws Exception {
		Query query = manager.createNativeQuery(LABMASTERLIST);
		query.setParameter("clusterid", Long.parseLong(Clusterid));
		List<Object[]> LabMasterList= (List<Object[]>)query.getResultList();
		return LabMasterList;
	}
	private static final String GETNOTIFICATIONID="select notificationid,EmpId from pfms_notification where isactive='1' and empid=:empid ORDER BY notificationdate DESC";
	@Override
	public List<Object[]> getNotificationId(String Empid) throws Exception {
		
		List<Object[]> NotificationList= new ArrayList<Object[]>();
		Query query = manager.createNativeQuery(GETNOTIFICATIONID);
		
		query.setParameter("empid", Long.parseLong(Empid));
		
		NotificationList=(List<Object[]>)query.getResultList();
		return NotificationList;
	}
	@Override
	public List<Object[]> getFormNameByName(String search) {
		// TODO Auto-generated method stub
		Query query = manager.createNativeQuery("SELECT * FROM pfms_form_detail WHERE formname LIKE :search and isactive='1'");
		query.setParameter("search", "%"+search+"%");
		
		return (List<Object[]>)query.getResultList();
	}

	@Override
	public Boolean getRoleAccess(String formModuleId, String logintype) {
		// TODO Auto-generated method stub
		Query query = manager.createNativeQuery("SELECT COUNT(FormRoleAccessId) FROM `pfms_form_role_access` WHERE LoginType=:logintype AND isactive=1 AND FormDetailId =:formModuleId");
		query.setParameter("logintype",logintype);
		query.setParameter("formModuleId", Long.parseLong(formModuleId));
		List<Long> result = query.getResultList();
		if (result.get(0).intValue()==0)return false;
		else return true;
	}
	
	private static final String ISACTIVEDB="UPDATE project_dashboard_favourite SET IsActive ='0' WHERE EmpId =:EmpId and LoginType=:LoginType";
	
	@Override
	public long isActiveDashBoard(String EmpId,String LoginType) throws Exception {

		Query query = manager.createNativeQuery(ISACTIVEDB);
		query.setParameter("EmpId", Long.parseLong(EmpId));
		query.setParameter("LoginType", LoginType);
		
		query.executeUpdate();
		return 2l;
	}
	
	@Override
	public long addDashBoardFav(ProjectDashBoardFavourite pd) throws Exception {
		isActiveDashBoard(pd.getEmpId()+"",pd.getLoginType());
		manager.persist(pd);
		return pd.getDashBoardId();
	}
	
	@Override
	public ProjectDashBoardFavourite findProjectDashBoardFavourite(long dashBoardId)throws Exception {
		try {
			return manager.find(ProjectDashBoardFavourite.class, dashBoardId);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getRoadMapDetails "+e);
			return null;
		}
	}
	
	private static final String DASHBOARDIDS="SELECT DashBoardId,DashBoardName,LoginType,EmpId,IsActive FROM project_dashboard_favourite WHERE EmpId=:empId AND LoginType=:LoginType";

	@Override
	public List<Object[]> getDashBoardId(Long empId, String LoginType) throws Exception {

		Query query = manager.createNativeQuery(DASHBOARDIDS);
		
		query.setParameter("empId", empId);
		query.setParameter("LoginType", LoginType);
		
		return (List<Object[]>)query.getResultList();
	}
	private static final String projecthealthtotalDashBoardwise="CALL Project_Health_Total_Data_dasboardwise(:InDashBoardId , :labCode)";
	@Override
	public Object[] projecthealthtotalDashBoardwise(String InDashBoardId, String labCode) throws Exception {
		Query query = manager.createNativeQuery(projecthealthtotalDashBoardwise);
		query.setParameter("InDashBoardId", Long.parseLong(InDashBoardId));
		query.setParameter("labCode", labCode);
		
		return (Object[])query.getSingleResult();
	}
	private static final String FINANCEPROJECTWISE="CALL Pfms_Dashboard_Finance_ProjectWise(:InDashBoardId ,:labCode)";
	
	@Override
	public List<Object[]> DashboardFinanceProjectWise(String InDashBoardId, String labCode) throws Exception {
		Query query = manager.createNativeQuery(FINANCEPROJECTWISE);
		query.setParameter("InDashBoardId", Long.parseLong(InDashBoardId));
		query.setParameter("labCode", labCode);
		
		return (List<Object[]>)query.getResultList();
	}
	
	
	private static final String UPDATEDASHBOARD ="UPDATE project_dashboard_favourite SET IsActive ='1' WHERE DashBoardId = :dashboardId";
	private static final String DELETEDASHBOARD ="DELETE FROM  project_dashboard_favourite_projects  WHERE DashBoardId = :dashboardId";
	@Override
	public long updateDashBoard(String dashboardId) throws Exception {
		Query query1 = manager.createNativeQuery(UPDATEDASHBOARD);
		query1.setParameter("dashboardId", Long.parseLong(dashboardId));
		query1.executeUpdate();
		
		Query query2 = manager.createNativeQuery(DELETEDASHBOARD);
		query2.setParameter("dashboardId", Long.parseLong(dashboardId));
		query2.executeUpdate();
		
		return 1l;
	}
	private static final String DASHBOARDPROJECTS = "SELECT a.FavouriteId,a.DashBoardId,a.ProjectId FROM project_dashboard_favourite_projects a WHERE a.DashBoardId = :InDashBoardId";
	@Override
	public List<Object[]> getProjectsBasedOnDashBoard(String InDashBoardId) throws Exception {
		Query query = manager.createNativeQuery(DASHBOARDPROJECTS);
		query.setParameter("InDashBoardId", Long.parseLong(InDashBoardId));
		return (List<Object[]>)query.getResultList();
	}
	
	@Override
	public long addProjectDashBoardFavouriteProjetcts(ProjectDashBoardFavouriteProjetcts pf) throws Exception {
		manager.persist(pf);
		manager.flush();
		return pf.getFavouriteId();
	}
	
	private static final String PASSWORDCHANGEHYSTORYCOUNT  ="SELECT COUNT(loginid) AS 'passwordCount' FROM pfms_login_password_history WHERE LoginId=:LoginId";
	@Override
	public long PasswordChangeHystoryCount(Long loginId) throws Exception
	{
		try {
			Query query = manager.createNativeQuery(PASSWORDCHANGEHYSTORYCOUNT);
			query.setParameter("LoginId", loginId);		
			return (Long)query.getSingleResult();
		}catch (Exception e) {
			logger.error(new Date() +" Inside DAO PasswordChangeHystoryCount"+ e);
			e.printStackTrace();
			return 0;
		}
		
		
	}

	@Override
	public long addLoginPasswordHistory(LoginPasswordHistory passwordHistory) {
		try {
			manager.persist(passwordHistory);
			manager.flush();
			return passwordHistory.getPasswordHistoryId();
		}catch (Exception e) {
			logger.error(new Date() +" Inside DAO addLoginPasswordHistory "+ e);
			e.printStackTrace();
			return 0;
		}
		
	}
}
