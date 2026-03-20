package com.vts.pfms.dao;

import java.util.Date;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.vts.pfms.login.PFMSCCMData;
import com.vts.pfms.login.ProjectHoa;
import com.vts.pfms.model.LabMaster;
import com.vts.pfms.model.LoginStamping;
import com.vts.pfms.model.Notice;
import com.vts.pfms.model.PfmsCommitteSmsTracking;
import com.vts.pfms.model.PfmsCommitteSmsTrackingInsights;
import com.vts.pfms.model.PfmsSmsTracking;
import com.vts.pfms.model.PfmsSmsTrackingInsights;
import com.vts.pfms.model.ProjectHoaChanges;
import com.vts.pfms.project.model.ProjectHealth;

@Transactional
@Repository
public class RfpMainDaoImpl implements RfpMainDao {

	private static final Logger logger=LogManager.getLogger(RfpMainDaoImpl.class);
	
	private static final String DASHBOARDFORMURLLIST = "select a.formdispname,a.formurl,a.formcolor from form_detail a,form_role_access b,login c where c.loginid=:loginid and a.formmoduleid=:formmoduleid AND b.formroleid=c.formroleid AND a.formdetailid=b.formdetailid AND a.isactive='1' AND b.isactive='1' ORDER BY a.FormSerialNo  ";
	private static final String USERMANAGELIST = "select a.loginid, a.username, b.division_name,c.formrolename  from login a , division_master b , formrole c where a.divisionid=b.division_id and a.formroleid=c.formroleid and a.isactive=1";
	private static final String LASTLOGINEMPID = "select a.auditstampingid from  auditstamping a where a.auditstampingid=(select max(b.auditstampingid) from auditstamping b WHERE b.loginid=:loginid)";
	private static final String DESGID="select desig_id from employee where  emp_id=:empid";
	private static final String LABDETAILS="select labmasterid, labcode, labname, lablogo from lab_master";
	
	private static final String NOTICEEDITDATA="SELECT * FROM pfms_notice WHERE NoticeId=:NOTICEID AND IsActive=1" ;
	private static final String SELFACTIONSLIST="SELECT ActionId,EmpId,ActionItem,ActionDate,ActionTime,ActionType FROM pfms_action_self WHERE isactive='1'AND actiondate=CURDATE() AND empid=:empid ORDER BY createddate ASC";	
	private static final String NOTICEDATEWOSE= "SELECT noticeid, notice, fromdate,todate,noticeby,createdby,createddate FROM pfms_notice WHERE NoticeBy=:EMPID AND IsActive=1 AND ( (FromDate BETWEEN :FROMDATE AND :TODATE) OR  (todate BETWEEN :FROMDATE  AND :TODATE )   OR( fromdate < :FROMDATE  AND todate > :TODATE  ))";
	private static final String NOTICELIST="SELECT * FROM pfms_notice WHERE NoticeBy=:empid AND IsActive=1 AND MONTH(CreatedDate) = MONTH(CURRENT_DATE())";
	private static final String NOTICE="SELECT n.noticeid,n.notice, e.emp_name FROM pfms_notice n, employee e   WHERE   DATE(NOW()) >=n.FromDate AND DATE(NOW()) <= n.ToDate AND e.emp_id=n.NoticeBy AND n.IsActive=1 AND n.labcode=:labcode ORDER BY n.NoticeId DESC";	
	private static final String getEmpNoQuery="SELECT emp_no FROM employee WHERE emp_id =:empid";	
	private static final String PROJECTLIST="SELECT a.project_id AS id,a.project_code,a.project_name,a.project_main_id,a.project_type,b.emp_name AS 'project_director',b.mobile_no,a.project_director,a.sanction_date,a.pdc FROM project_master a , employee b WHERE a.project_director=b.emp_id AND a.is_active=1 AND a.lab_code = (SELECT lab_code FROM employee WHERE emp_id=:empid)";
	private static final String QUATERS="SELECT a.sanctiondate, a.pdc, TIMESTAMPDIFF(YEAR,a.sanctiondate,a.pdc)+1 FROM project_master a WHERE a.projectid=:projectid";
	private static final String MILEQUATER="CALL Pfms_Milestone_Quarter(:proid,:Quater,:yr)"; 
	private static final String GANTTCHARTLIST="SELECT milestoneactivityid,projectid,activityname,milestoneno,orgstartdate,orgenddate,startdate,enddate,progressstatus,revisionno FROM milestone_activity WHERE isactive=1 ";
	private static final String PROJECTHEALTHDATA ="CALL Project_Health_Get_Data(:labcode)";
	private static final String PROJECTTOTALHEALTHDATA="CALL Project_Health_Total_Data(:projectid,:empid,:logintype,:labcode,:isall )";
	private static final String PROJECTHEALTHINSERTDATA="CALL Project_Health_Insert_Data(:projectid)";
	private static final String PROJECTHEALTHDELETE="DELETE FROM project_health where projectid=:projectid";
	private static final String PROJECTHOADELETE="DELETE FROM project_hoa WHERE labcode=:labcode";
	private static final String CHANGESTOTALCOUNTDATA="CALL Project_Changes_Count_Data(:projectid) ";
	private static final String MEETINGCHANGES="CALL Project_Changes_Meeting_Data(:projectid,:term,:labcode)";
	private static final String MILESTONECHANGES="CALL Project_Changes_Milestone_Data(:projectid,:term,:labcode)";
	private static final String ACTIONCHANGES="CALL Project_Changes_Action_Data(:projectid,:term,:labcode)";
	private static final String RISKCHANGES="CALL Project_Changes_Risk_Data(:projectid,:term,:labcode)";
	private static final String FINANCEDATAPARTA="SELECT pftsfileid,projectid,demandno,demanddate,estimatedcost,itemnomenclature FROM pfts_file WHERE isactive=1 AND CASE WHEN :projectid='A' THEN 1=1 ELSE projectid=:projectid END";
	private static final String PROJECTHOACHANGESDELETE="DELETE FROM project_hoa_changes where projectid=:projectid";
	
	
	@PersistenceContext
	EntityManager manager;

	
	@Override
	public List<String> getEmpNo(long empId) throws Exception {
		Query query = manager.createNativeQuery(getEmpNoQuery);
		query.setParameter("empid", empId);
		List<String> empnoList = query.getResultList();
		return empnoList;
	}
	
	@Override
	public List<Object[]> DashBoardFormUrlList(int FormModuleId, int loginId) throws Exception {

		Query query = manager.createNativeQuery(DASHBOARDFORMURLLIST);
		query.setParameter("loginid", loginId);
		query.setParameter("formmoduleid", FormModuleId);
		List<Object[]> DashBoardFormUrlList = query.getResultList();
		return DashBoardFormUrlList;
	}

	@Override
	public List<Object[]> UserManagerList() throws Exception {
		Query query = manager.createNativeQuery(USERMANAGELIST);

		List<Object[]> UserManagerList = query.getResultList();
		return UserManagerList;
	}

	@Override
	public Long LoginStampingInsert(LoginStamping Stamping) throws Exception {

		manager.persist(Stamping);

		manager.flush();

		return Stamping.getAuditStampingId();
	}

	@Override
	public Long LastLoginStampingId(String LoginId) throws Exception {
		Query query = manager.createNativeQuery(LASTLOGINEMPID);
		query.setParameter("loginid", LoginId);
		return (Long) query.getSingleResult();
	}

	private static final String LOGINSTAMPINGUPDATE="update auditstamping set logouttype=:logouttype,logoutdatetime=:logoutdatetime "
			+ "where auditstampingid=:auditstampingid";

	@Override
	public int LoginStampingUpdate(LoginStamping Stamping) throws Exception {
		
		LoginStamping ExistingLoginStamping = manager.find(LoginStamping.class, Stamping.getAuditStampingId());
		if(ExistingLoginStamping != null) {
			ExistingLoginStamping.setLogOutType(Stamping.getLogOutType());
			ExistingLoginStamping.setLogOutDateTime(Stamping.getLogOutDateTime());
			return 1;
		}
		else {
			return 0;
		}

	}
	
	
	@Override
	public LabMaster LabDetailes() throws Exception {
		LabMaster LabDetailes=manager.find(LabMaster.class, 1);
		return LabDetailes;
	}
	
	@Override
	public List<Object[]> LabDetails() throws Exception {
		
		Query query=manager.createNativeQuery(LABDETAILS);
		List<Object[]> LabDetails=(List<Object[]>)query.getResultList();
		
		return LabDetails;
	}

	@Override
	public String DesgId(String Empid) throws Exception {
		Query query = manager.createNativeQuery(DESGID);
		query.setParameter("empid", Long.parseLong(Empid));
		Long DesgId = (Long) query.getSingleResult();
		return DesgId.toString();
	}
	

	

	@Override
	public List<Object[]> getIndividualNoticeList(String userId)throws Exception{
		
		Query query = manager.createNativeQuery(NOTICELIST);
		query.setParameter("empid", userId);
		List<Object[]> NoticeList=(List<Object[]>)query.getResultList();
		
		return NoticeList;		
	}

	@Override
	public Long addNotice(Notice notice)throws Exception{
		
		try {
			manager.persist(notice);
			manager.flush();
			
		}catch(Exception e) {
			logger.info(new Date() +"Inside ActionLaunch.htm "+ e);	
			e.printStackTrace();
		}

		return notice.getNoticeId();
	}
	

	
	@Override
	public List<Object[]> AllActionsCount(String empid,String ProjectId) throws Exception {
		
		Query query=manager.createNativeQuery("CALL Pfms_All_Actions_Count (:projectid)");	
//		query.setParameter("empid", empid);
		query.setParameter("projectid", Long.parseLong(ProjectId));

		List<Object[]> AllActionsCount=(List<Object[]>)query.getResultList();	
		return AllActionsCount;
	}
	
	@Override
	public List<Object[]> GetNotice(String LabCode)throws Exception{
		
		Query query = manager.createNativeQuery(NOTICE);
		query.setParameter("labcode", LabCode);
		List<Object[]> NoticeList=(List<Object[]>)query.getResultList();
		
		return NoticeList;	
	}

	@Override
	public List<Object[]> getAllNotice()throws Exception{
		
		Query query = manager.createNativeQuery(NOTICE);
		List<Object[]> NoticeList=(List<Object[]>)query.getResultList();
		
		return NoticeList;	
		
	}
	
	@Override
    public List<Object> GetNoticeEligibility(String empId)throws Exception{
		
		Query query=manager.createNativeQuery("CALL Pfms_Notice_Eligibility(:EMPID)");	
		query.setParameter("EMPID", Long.parseLong(empId));
		List<Object> NoticeList=(List<Object>)query.getResultList();

		return NoticeList;	
    }
	
	
	@Override
    public List<Object> SelfActionsList(String empId)throws Exception {
		
		Query query=manager.createNativeQuery(SELFACTIONSLIST);	
		query.setParameter("empid", Long.parseLong(empId));
		List<Object> SelfActionsList=(List<Object>)query.getResultList();

		return SelfActionsList;	
    }

	@Override
	public List<Object[]> getIndividualNoticeList(String empId, java.sql.Date fdate, java.sql.Date tdate)throws Exception{
		
		Query query=manager.createNativeQuery(NOTICEDATEWOSE);	
		query.setParameter("EMPID",empId);
		query.setParameter("FROMDATE", fdate);
		query.setParameter("TODATE", tdate);
		List<Object[]> SelfActionsList=(List<Object[]>)query.getResultList();

		return SelfActionsList;	
		
	}
	
	@Override
	public List<Object[]> getnoticeEditData(String noticeId)throws Exception{
		Query query=manager.createNativeQuery(NOTICEEDITDATA);
		query.setParameter("NOTICEID", Long.parseLong(noticeId));
		List<Object[]> EditData=(List<Object[]>)query.getResultList();
        return EditData;
	}
	

	@Override
	public int noticeDelete(String noticeId)throws Exception{
		
		Notice ExistingNotice = manager.find(Notice.class, Long.parseLong(noticeId));
		if(ExistingNotice != null) {
			ExistingNotice.setIsActive(0);
			return 1;
		}
		else {
			return 0;
		}
		

		
	}
	
	 


	@Override
	public int editNotice(Notice notice)throws Exception{
		
		Notice ExistingNotice = manager.find(Notice.class, notice.getNoticeId());
		if(ExistingNotice != null) {
			ExistingNotice.setNotice(notice.getNotice());
			ExistingNotice.setFromDate(notice.getFromDate());
			ExistingNotice.setToDate(notice.getToDate());
			ExistingNotice.setNoticeBy(notice.getNoticeBy());
			return 1;
		}
		else {
			return 0;
		}
		
	}
	
	@Override
	public List<Object[]> ProjectBudgets() throws Exception {
		Query query=manager.createNativeQuery("CALL Pfms_Dashboard_Finance_Budgets();");
		List<Object[]> ProjectsBudgets=(List<Object[]>)query.getResultList();
		return ProjectsBudgets;
	}
	
	
	@Override
	public Object[] AllSchedulesCount(String loginid) throws Exception {
		Query query=manager.createNativeQuery("CALL Pfms_All_Meetings_Count(:loginid);");
		query.setParameter("loginid", Long.parseLong(loginid));
		Object[] AllSchedulesCount=(Object[])query.getSingleResult();
		return AllSchedulesCount;
	}

	@Override
	public List<Object[]> ProjectMeetingCount(String ProjectId) throws Exception {
		Query query=manager.createNativeQuery("CALL Pfms_All_Meetings_Count(:ProjectId);");
		query.setParameter("ProjectId", Long.parseLong(ProjectId));
		List<Object[]> ProjectMeetingCount=(List<Object[]>)query.getResultList();
		return ProjectMeetingCount;
	}

	@Override
	public List<Object[]> ProjectList(String EmpId) throws Exception {
		
		Query query=manager.createNativeQuery(PROJECTLIST);
		query.setParameter("empid", Long.parseLong(EmpId));

		return (List<Object[]>) query.getResultList();
	}


	@Override
	public List<Object[]> ProjectEmployeeList(String empid,String logintype,String LabCode) throws Exception {
		
		Query query=manager.createNativeQuery("CALL Pfms_Emp_ProjectList (:empid,:logintype,:labcode)");
		//Query query=manager.createNativeQuery(PROJECTEMPLOYEELIST);
		query.setParameter("empid", Long.parseLong(empid));
		query.setParameter("logintype", logintype);
		query.setParameter("labcode", LabCode);
		List<Object[]> ProjectEmployeeList=(List<Object[]>)query.getResultList();
		
		System.out.println(ProjectEmployeeList.size()+"%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		return ProjectEmployeeList;
	}
	
	
	
	@Override
	public List<Object[]> ProjectQuaters(String ProjectId) throws Exception {

		Query query=manager.createNativeQuery(QUATERS);
		query.setParameter("projectid", Long.parseLong(ProjectId));
		List<Object[]> ProjectQuaters=(List<Object[]>)query.getResultList();
		return ProjectQuaters;
	}

	@Override
	public List<Object[]> MileQuaters(String ProjectId, int Quater, int year) throws Exception {
		Query query=manager.createNativeQuery(MILEQUATER);
		query.setParameter("proid", Long.parseLong(ProjectId));
		query.setParameter("Quater",Quater);
		query.setParameter("yr",year);
		List<Object[]> ProjectQuaters=(List<Object[]>)query.getResultList();
		return ProjectQuaters;
	}
	
	@Override
	public List<Object[]> GanttChartList() throws Exception {
		
		Query query = manager.createNativeQuery(GANTTCHARTLIST);
		//query.setParameter("projectid", ProjectId);
		List<Object[]> GanttChartList= query.getResultList();
		return GanttChartList;
	}
	
	@Override
	public List<Object[]> ProjectHealthData(String LabCode) throws Exception {
		
		Query query = manager.createNativeQuery(PROJECTHEALTHDATA);
		query.setParameter("labcode", LabCode);
		List<Object[]> ProjectHealthData= query.getResultList();
		return ProjectHealthData;
	}
	
	@Override
	public Object[] ProjectHealthTotalData(String ProjectId,String EmpId, String LoginType,String LabCode,String IsAll) throws Exception {
		
		Query query = manager.createNativeQuery(PROJECTTOTALHEALTHDATA);
		query.setParameter("projectid", ProjectId);
		query.setParameter("empid", Long.parseLong(EmpId));
		query.setParameter("logintype", LoginType);
		query.setParameter("labcode", LabCode);
		query.setParameter("isall", IsAll);
		 	
		Object[] ProjectHealthTotalData= (Object[])query.getSingleResult();
		return ProjectHealthTotalData;
	}

	@Override
	public long ProjectHealthInsert(ProjectHealth health) throws Exception {
		manager.persist(health);

		manager.flush();

		return health.getProjectHealthId();
	}
	@Override
	public Object[] ProjectHealthInsertData(String projectId) throws Exception {
		
		Query query = manager.createNativeQuery(PROJECTHEALTHINSERTDATA);
		query.setParameter("projectid", Long.parseLong(projectId));
		Object[] ProjectHealthTotalData= (Object[])query.getSingleResult();
		return ProjectHealthTotalData;
	}

	@Override
	public int ProjectHealthDelete(String projectId) throws Exception {
		Query query = manager.createNativeQuery(PROJECTHEALTHDELETE);
		query.setParameter("projectid",Long.parseLong(projectId));
		int count =(int)query.executeUpdate();
		
		return count ;
	}
	
	@Override
	public long ProjectHoaUpdate(ProjectHoa hoa)throws Exception{
		
		manager.merge(hoa);
		manager.flush();
		
		return hoa.getProjectHoaId();
	}
	
	@Override
	public int ProjectHoaDelete(String LabCode) throws Exception{
		
		Query query = manager.createNativeQuery(PROJECTHOADELETE);
		query.setParameter("labcode", LabCode);

		return query.executeUpdate();
	}
	
	@Override
	public Object[] ChangesTotalCountData(String ProjectId) throws Exception
	{
		
		Query query = manager.createNativeQuery(CHANGESTOTALCOUNTDATA);
		query.setParameter("projectid", ProjectId);
		return (Object[]) query.getSingleResult();
		
	}
	
	@Override
	public List<Object[]> MeetingChanges(String ProjectId,String Interval,String LabCode) throws Exception
	{

		Query query = manager.createNativeQuery(MEETINGCHANGES);
		query.setParameter("projectid", ProjectId);
		query.setParameter("term", Interval);
		query.setParameter("labcode", LabCode);
		return (List<Object[]>) query.getResultList();
		
	}
	
	@Override
	public List<Object[]> MilestoneChanges(String ProjectId,String Interval,String LabCode) throws Exception
	{
		
		Query query = manager.createNativeQuery(MILESTONECHANGES);
		query.setParameter("projectid", ProjectId);
		query.setParameter("term", Interval);
		query.setParameter("labcode", LabCode);
		return (List<Object[]>) query.getResultList();
		
	}
	
	@Override
	public List<Object[]> ActionChanges(String ProjectId,String Interval,String LabCode) throws Exception
	{
		
		Query query = manager.createNativeQuery(ACTIONCHANGES);
		query.setParameter("projectid", ProjectId);
		query.setParameter("term", Interval);
		query.setParameter("labcode", LabCode);
		return (List<Object[]>) query.getResultList();
		
	}
	
	@Override
	public List<Object[]> RiskChanges(String ProjectId,String Interval,String LabCode) throws Exception
	{
		
		Query query = manager.createNativeQuery(RISKCHANGES);
		query.setParameter("projectid", ProjectId);
		query.setParameter("term", Interval);
		query.setParameter("labcode", LabCode);
		return (List<Object[]>) query.getResultList();
		
	}
	
	
	@Override
	public List<Object[]> FinanceDataPartA(String ProjectId,String Interval) throws Exception
	{
		
		Query query = manager.createNativeQuery(FINANCEDATAPARTA);
		query.setParameter("projectid", ProjectId);
		//query.setParameter("term", Interval);
		return (List<Object[]>) query.getResultList();
		
	}
	
	@Override
	public int ProjectHoaChangesDelete(String projectId) throws Exception {
		Query query = manager.createNativeQuery(PROJECTHOACHANGESDELETE);
		query.setParameter("projectid", Long.parseLong(projectId));
		int count =(int)query.executeUpdate();
		
		return count ;
	}
	
	@Override
	public long ProjectHoaChangesInsert(ProjectHoaChanges changes)throws Exception{
		
		manager.merge(changes);
		manager.flush();
		
		return 0L;
	}
	
	private static final String PROJECTDATA ="SELECT projectid, projectcode, projectname FROM project_master WHERE projectid=:projectid ";
	@Override
	public Object[] ProjectData(String projectid) throws Exception 
	{
		try {
			Query query=manager.createNativeQuery(PROJECTDATA);
			query.setParameter("projectid", Long.parseLong(projectid));
			return (Object[])query.getSingleResult();
		}catch (NoResultException e) {
			return null;
		}
		
	}
	
	private static final String CCMDATADELETE="DELETE FROM pfms_ccm_data where LabCode=:LabCode";
	@Override
	public int CCMDataDelete(String LabCode) throws Exception 
	{
		Query query = manager.createNativeQuery(CCMDATADELETE);
		query.setParameter("LabCode", LabCode);
		int count =(int)query.executeUpdate();
		
		return count ;
	}
	
	@Override
	public long CCMDataInsert(PFMSCCMData ccmdata )throws Exception
	{
		manager.persist(ccmdata);
		manager.flush();
		return ccmdata.getCCMDataId();
	}
	
	private static final String GETCCMDATA = "CALL Project_Health_Finance_Data (:EmpId, :LoginType, :LabCode );";
	@Override
	public List<Object[]> getCCMData(String EmpId,String LoginType,String LabCode)throws Exception
	{
		Query query = manager.createNativeQuery(GETCCMDATA);
		query.setParameter("EmpId", Long.parseLong(EmpId));
		query.setParameter("LoginType", LoginType);
		query.setParameter("LabCode", LabCode);
		List<Object[]> CCMData =(List<Object[]>)query.getResultList();
		
		return CCMData ;
	}
	
	private static final String DASHBOARDFINANCECASHOUTGO = "CALL Pfms_Dashboard_Finance_CashOutGo ( :LoginType,:EmpId, :LabCode, :ClusterId );";
	@Override
	public List<Object[]> DashboardFinanceCashOutGo(String LoginType,String EmpId,String LabCode,String ClusterId)throws Exception
	{
		Query query = manager.createNativeQuery(DASHBOARDFINANCECASHOUTGO);
		query.setParameter("LoginType", LoginType);
		query.setParameter("EmpId", Long.parseLong(EmpId));
		query.setParameter("LabCode", LabCode);
		query.setParameter("ClusterId", Long.parseLong(ClusterId));
		List<Object[]> CCMData =(List<Object[]>)query.getResultList();
		
		return CCMData ;
	}
	
	private static final String DASHBOARDFINANCE = "CALL Pfms_Dashboard_Finance( :LoginType,:EmpId, :LabCode, :ClusterId );";
	@Override
	public List<Object[]> DashboardFinance(String LoginType,String EmpId,String LabCode,String ClusterId)throws Exception
	{
		Query query = manager.createNativeQuery(DASHBOARDFINANCE);
		query.setParameter("LoginType", LoginType);
		query.setParameter("EmpId", Long.parseLong(EmpId));
		query.setParameter("LabCode", LabCode);
		query.setParameter("ClusterId", Long.parseLong(ClusterId));
		List<Object[]> CCMData =(List<Object[]>)query.getResultList();		
		return CCMData ;
	}
	
	
	private static final String DASHBOARDPROJECTFINANCECASHOUTGO = "CALL Pfms_Finance_CashOutGo_Project (:projectcode)";
	@Override
	public List<Object[]> DashboardProjectFinanceCashOutGo(String projectcode)throws Exception
	{
		Query query = manager.createNativeQuery(DASHBOARDPROJECTFINANCECASHOUTGO);
		query.setParameter("projectcode", projectcode);
		List<Object[]> CCMData =(List<Object[]>)query.getResultList();
		
		return CCMData ;
	}
	
	private static final String PROJECTATTRIBUTES = "SELECT pm.projectcode, pm.projectname, pm.ProjectDescription, pm.sanctiondate, pm.objective, pm.deliverable, pm.pdc,   ROUND(pm.TotalSanctionCost/100000,2) AS 'TotalSanctionCost',   ROUND(pm.SanctionCostRE/100000,2) AS 'SanctionCostRE', ROUND(pm.SanctionCostFE/100000,2) AS 'SanctionCostFE', pm.WorkCenter, pm.projectcategory,pc.classification,  pm.projecttype AS 'projecttypeid',pt.projecttype ,pma.labparticipating,  (SELECT ps.projectstage FROM pfms_project_stage ps, pfms_project_data pd WHERE ps.projectstageid=pd.CurrentStageId AND pd.projectid=pm.projectid ) AS 'projectstage'FROM project_master pm, pfms_security_classification pc, project_type pt , project_main pma  WHERE pm.projectcategory=pc.classificationid AND pm.projecttype=pt.projecttypeid AND pm.projectmainid=pma.projectmainid AND pm.projectcode=:projectcode  ";
	@Override
	public Object[] ProjectAttributes(String projectcode)throws Exception
	{
		Query query = manager.createNativeQuery(PROJECTATTRIBUTES);
		query.setParameter("projectcode", projectcode);
		Object[] ProMaster =(Object[])query.getSingleResult();
		return ProMaster ;
	}

	private static final String SMSINTIATEDCOUNT="SELECT COUNT(*) FROM pfms_sms_track WHERE CreatedDate = CURDATE() AND SmsTrackingType=:smsTrackingType";
	@Override
	public long GetSMSInitiatedCount(String smsTrackingType) throws Exception {
		logger.info(new Date() + "Inside GetSMSInitiatedCount");
		try {
		Query query = manager.createNativeQuery(SMSINTIATEDCOUNT);
	    query.setParameter("smsTrackingType", smsTrackingType);
	    return (Long) query.getSingleResult();
	  } catch (Exception e) {
		 e.printStackTrace();
			logger.error(new Date() + "Inside DaoImpl GetSMSInitiatedCount", e);
			return 0;
	    }
	}
	
	private static final String DAILYEXPECTEDPENDINGREPLYCOUNT="SELECT COUNT(DISTINCT m.Assignee) FROM action_assign m JOIN action_main a ON m.ActionMainId = a.ActionMainId WHERE m.ActionStatus='A' AND m.IsActive=1";
	@Override
	public long GetDailyExpectedPendingReplyCount() throws Exception {
		logger.info(new Date() + "Inside GetDailyExpectedPendingReplyCount");
		try {
		 Query query = manager.createNativeQuery(DAILYEXPECTEDPENDINGREPLYCOUNT);
		 return (Long) query.getSingleResult();
		 } catch (Exception e) {
		    e.printStackTrace();
			logger.error(new Date() + "Inside DaoImpl GetDailyExpectedPendingReplyCount", e);
			return 0;
		}
	}
	
	
	@Override
	public long InsertSmsTrackRow(PfmsSmsTracking model) throws Exception {
		logger.info(new Date() + "Inside DAO InsertSmsTrackRow");
		try {
			manager.persist(model);
			manager.flush();
			return model.getSmsTrackingId();
        } catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside DaoImpl InsertSmsTrackRow", e);
			return 0;
		}
	}
	
	
	private static final String DAILYPENDINGASSIGNEMPDATA="SELECT a.ActionAssignId,a.Assignee,empData.EmpName,empData.Email,empData.MobileNo,b.ActionItem,a.EndDate FROM action_assign a LEFT JOIN employee empData ON empData.EmpId=a.Assignee JOIN action_main b ON a.ActionMainId=b.ActionMainId WHERE a.ActionStatus='A' AND a.IsActive=1";
	@Override
	public List<Object[]> GetDailyPendingAssigneeEmpData() throws Exception {
		logger.info(new Date() + "Inside DAO GetDailyPendingAssigneeEmpData");
		try {
		Query query = manager.createNativeQuery(DAILYPENDINGASSIGNEMPDATA);
		List<Object[]> GetDailyPendingReplyEmpData = query.getResultList();
		return GetDailyPendingReplyEmpData;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside DaoImpl GetDailyPendingAssigneeEmpData", e);
			return null;
		}
	}
	
	private static final String ACTIONASSIGNCOUNTS="SELECT(SELECT COUNT(*) FROM action_assign a WHERE a.ActionStatus='A' AND a.Progress=0 AND a.Assignee=:empId AND a.IsActive=1) AS 'ActivityAssigned',(SELECT COUNT(*) FROM action_assign a WHERE a.ActionStatus='A' AND a.Progress>0 AND a.EndDate>=:PdcDate AND a.Assignee=:empId AND a.IsActive=1) AS 'ActivityOnGoing',(SELECT COUNT(*) FROM action_assign a WHERE a.ActionStatus='A' AND a.Progress>0 AND a.EndDate<:PdcDate AND a.Assignee=:empId AND a.IsActive=1) AS 'ActivityDelayOnGoing'";
	@Override
	public Object[] ActionAssignCounts(long empId, String PdcDate) throws Exception {
		try {
			Query query = manager.createNativeQuery(ACTIONASSIGNCOUNTS);
			query.setParameter("empId", empId);
			query.setParameter("PdcDate", PdcDate);
			return (Object[])query.getSingleResult();
			
		}
		catch(Exception e) {
			logger.error(new Date()  + "Inside DAO DakCounts " + e);
			return null;
		}
	}
	
	@Override
	public long InsertSmsTrackInsights(PfmsSmsTrackingInsights insights) throws Exception {
		logger.info(new Date() + "Inside DAO PfmsSmsTrackingInsights");
		try {
			manager.persist(insights);
			manager.flush();
			return insights.getSmsTrackingInsightsId();
        } catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside DaoImpl PfmsSmsTrackingInsights", e);
			return 0;
		}
	}
	
	private static final String UPDATEPARTICULAREMPSMSSTATUS="UPDATE pfms_sms_track_insights SET SmsStatus=:smsStatus,SmsSentDate= CAST(CURRENT_TIMESTAMP AS DATETIME),Message=:message WHERE SmsPurpose=:smsPurpose AND EmpId=:empId AND SmsTrackingId=:effectivelyFinalSmsTrackingId";
	@Override
	public long UpdateParticularEmpSmsStatus(String smsPurpose, String smsStatus, long empId,long effectivelyFinalSmsTrackingId, String message) throws Exception {
		logger.info(new Date() + "Inside DaoImpl UpdateParticularEmpSmsStatus");
		try {
			Query query = manager.createNativeQuery(UPDATEPARTICULAREMPSMSSTATUS);
		    query.setParameter("smsPurpose", smsPurpose);
		    query.setParameter("smsStatus", smsStatus);
		    query.setParameter("empId", empId);
		    query.setParameter("effectivelyFinalSmsTrackingId", effectivelyFinalSmsTrackingId);
		    query.setParameter("message", message);
			return query.executeUpdate();
			
		 } catch (Exception e) {
			 e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl UpdateParticularEmpSmsStatus", e);
				return 0;
		    }
	}
	
	private static final String UPADTEACTIONSMSTRACKROW="UPDATE pfms_sms_track SET SmsSentCount=:successCount,SmsSentStatus='S',SmsSentDateTime= CAST(CURRENT_TIMESTAMP AS DATETIME) WHERE SmsTrackingId=:smsTrackingId AND CreatedDate=CURDATE() AND SmsTrackingType=:trackingType ";
	@Override
	public long UpdateDakActionTrackRow(long smsTrackingId, long successCount, String trackingType) throws Exception {
		logger.info(new Date() + "Inside DAO UpdateDakSmsTrackRow");
		try {
			Query query = manager.createNativeQuery(UPADTEACTIONSMSTRACKROW);
			 query.setParameter("smsTrackingId", smsTrackingId);
		    query.setParameter("successCount", successCount);
		    query.setParameter("trackingType", trackingType);
			return query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside DaoImpl UpdateDakSmsTrackRow", e);
			return 0;
		}
	}
	
	private static final String UPDATENOSMSPENDINGREPLY="UPDATE pfms_sms_track SET SmsSentCount=0 AND SmsSentStatus='NA' WHERE CreatedDate=CURDATE() AND SmsTrackingType=:trackingType ";
	@Override
	public long UpdateNoSmsPendingReply(String trackingType) throws Exception {
		logger.info(new Date() + "Inside DAO UpdateNoSmsPendingReply");
		try {
			Query query = manager.createNativeQuery(UPDATENOSMSPENDINGREPLY);
		    query.setParameter("trackingType", trackingType);
			return query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside DaoImpl UpdateNoSmsPendingReply", e);
			return 0;

		}
	}
	
	private static final String DIRECTORDAILYPENDINGASSIGNEMPDATA="SELECT  e.empid,e.mobileno FROM employee e,lab_master b WHERE b.labcode=:Lab AND e.empid=b.LabAuthorityId AND e.isactive='1'";
	@Override
	public List<Object[]> GetDirectorDailyPendingAssignEmpData(String lab) throws Exception {
		logger.info(new Date() + "Inside DAO GetDirectorDailyPendingAssignEmpData");
		try {
		Query query = manager.createNativeQuery(DIRECTORDAILYPENDINGASSIGNEMPDATA);
		query.setParameter("Lab", lab);
		List<Object[]> GetDailyPendingReplyEmpData = query.getResultList();
		return GetDailyPendingReplyEmpData;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside DaoImpl GetDirectorDailyPendingAssignEmpData", e);
			return null;
		}
	}
	
	private static final String DIRECTORACTIONASSIGNCOUNTS="SELECT(SELECT COUNT(*) FROM action_assign a WHERE a.ActionStatus='A' AND a.Progress=0 AND a.IsActive=1) AS 'ActivityAssigned',(SELECT COUNT(*) FROM action_assign a WHERE a.ActionStatus='A' AND a.Progress>0 AND a.EndDate>=:pdcDate AND a.IsActive=1) AS 'ActivityOnGoing',(SELECT COUNT(*) FROM action_assign a WHERE a.ActionStatus='A' AND a.Progress>0 AND a.EndDate<:pdcDate AND a.IsActive=1) AS 'ActivityDelayOnGoing'";
	@Override
	public Object[] DirectorActionAssignCounts(String pdcDate) throws Exception {
		try {
			Query query = manager.createNativeQuery(DIRECTORACTIONASSIGNCOUNTS);
			query.setParameter("pdcDate", pdcDate);
			return (Object[])query.getSingleResult();
		}
		catch(Exception e) {
			logger.error(new Date()  + "Inside DAO DirectorDakCounts " + e);
			return null;
		}
	}
	
	private static final String COMMITTESMSINTIATEDCOUNT="SELECT COUNT(*) FROM pfms_sms_committe_track WHERE CreatedDate = CURDATE() AND SmsTrackingType=:smsTrackingType";
	@Override
	public long GetCommitteSMSInitiatedCount(String smsTrackingType) throws Exception {
		logger.info(new Date() + "Inside GetCommitteSMSInitiatedCount");
		try {
		Query query = manager.createNativeQuery(COMMITTESMSINTIATEDCOUNT);
	    query.setParameter("smsTrackingType", smsTrackingType);
	    return (Long) query.getSingleResult();
	  } catch (Exception e) {
		 e.printStackTrace();
			logger.error(new Date() + "Inside DaoImpl GetCommitteSMSInitiatedCount", e);
			return 0;
	    }
	}
	
	
	private static final String DAILYCOMMITTECOUNT="SELECT COUNT(DISTINCT a.EmpId) FROM committee_schedules_invitation a, committee_schedule b ,committee c WHERE a.CommitteeScheduleId=b.ScheduleId AND b.CommitteeId=c.CommitteeId AND b.ScheduleDate=:ScheduleDate AND b.IsActive=1";
	@Override
	public long dailyCommitteCount(String ScheduleDate) throws Exception {
		logger.info(new Date() + "Inside dailyCommitteCount");
		try {
		 Query query = manager.createNativeQuery(DAILYCOMMITTECOUNT);
		 query.setParameter("ScheduleDate", ScheduleDate);
		 return (Long) query.getSingleResult();
		 } catch (Exception e) {
			 e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl dailyCommitteCount", e);
				return 0;
		    }
	}
	
	@Override
	public long InsertCommitteSmsTrackRow(PfmsCommitteSmsTracking model) throws Exception {
		logger.info(new Date() + "Inside DAO InsertCommitteSmsTrackRow");
		try {
			manager.persist(model);
			manager.flush();
			return model.getCommitteSmsTrackingId();
        } catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside DaoImpl InsertCommitteSmsTrackRow", e);
			return 0;
		}
	}
	
	private static final String COMMITTEDETAILSTOSENDSMS="SELECT DISTINCT a.EmpId,e.MobileNo FROM committee_schedules_invitation a, committee_schedule b ,committee c ,employee e WHERE a.CommitteeScheduleId=b.ScheduleId AND b.CommitteeId=c.CommitteeId AND b.ScheduleDate=CURDATE() AND a.EmpId=e.EmpId AND b.IsActive=1 ";
	@Override
	public List<Object[]> GetCommitteEmpsDetailstoSendSms() throws Exception {
		logger.info(new Date() + "Inside DAO GetCommitteEmpsDetailstoSendSms");
		try {
		Query query = manager.createNativeQuery(COMMITTEDETAILSTOSENDSMS);
		List<Object[]> GetCommitteEmpsDetailstoSendSms = query.getResultList();
		return GetCommitteEmpsDetailstoSendSms;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside DaoImpl GetCommitteEmpsDetailstoSendSms", e);
			return null;
		}
	}
	
	private static final String GETCOMMITTEDATA="SELECT a.CommitteeScheduleId,b.ProjectId,c.CommitteeShortName,e.EmpName,e.MobileNo,b.ScheduleDate,b.ScheduleStartTime,c.ProjectApplicable FROM committee_schedules_invitation a,committee_schedule b,committee c,employee e WHERE a.CommitteeScheduleId=b.ScheduleId AND b.CommitteeId=c.CommitteeId AND b.ScheduleDate=CURDATE() AND a.EmpId=e.EmpId AND a.EmpId=:empId AND b.IsActive='1'";
	@Override
	public List<Object[]> getCommittedata(long empId) throws Exception {
		try {
			Query query = manager.createNativeQuery(GETCOMMITTEDATA);
			query.setParameter("empId", empId);
			List<Object[]> getCommittedata = (List<Object[]>)query.getResultList();
			return getCommittedata;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long InsertCommitteSmsTrackInsights(PfmsCommitteSmsTrackingInsights insights) throws Exception {
		logger.info(new Date() + "Inside DAO DakSmsTrackingInsights");
		try {
			manager.persist(insights);
			manager.flush();
			return insights.getCommitteSmsTrackingInsightsId();
        } catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside DaoImpl DakSmsTrackingInsights", e);
			return 0;
		}
	}
	
	private static final String UPDATEPARTICULARCOMMITTEEMPSMSSTATUS="UPDATE pfms_sms_committe_track_insights SET SmsStatus=:smsStatus,SmsSentDate= CAST(CURRENT_TIMESTAMP AS DATETIME),Message=:message WHERE SmsPurpose=:smsPurpose AND EmpId=:empId AND CommitteSmsTrackingId=:effectivelyFinalSmsTrackingId";
	@Override
	public long UpdateParticularCommitteEmpSmsStatus(String smsPurpose, String smsStatus, long empId,long effectivelyFinalSmsTrackingId, String message) throws Exception {
		logger.info(new Date() + "Inside UpdateParticularCommitteEmpSmsStatus");
		try {
			Query query = manager.createNativeQuery(UPDATEPARTICULARCOMMITTEEMPSMSSTATUS);
		    query.setParameter("smsPurpose", smsPurpose);
		    query.setParameter("smsStatus", smsStatus);
		    query.setParameter("empId", empId);
		    query.setParameter("effectivelyFinalSmsTrackingId", effectivelyFinalSmsTrackingId);
		    query.setParameter("message", message);
			return query.executeUpdate();
			
		 } catch (Exception e) {
			 e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl UpdateParticularCommitteEmpSmsStatus", e);
				return 0;
		    }
	}
	
	private static final String UPADTECOMMITTESMSTRACKROW="UPDATE pfms_sms_committe_track SET SmsSentCount=:successCount,SmsSentStatus='S',SmsSentDateTime= CAST(CURRENT_TIMESTAMP AS DATETIME) WHERE CommitteSmsTrackingId=:smsTrackingId AND CreatedDate=CURDATE() AND SmsTrackingType=:trackingType";
	@Override
	public long UpdateCommitteSmsTrackRow(long committeSmsTrackingId, long successCount, String trackingType) throws Exception {
		logger.info(new Date() + "Inside DAO UpdateCommitteSmsTrackRow");
		try {
			Query query = manager.createNativeQuery(UPADTECOMMITTESMSTRACKROW);
			 query.setParameter("smsTrackingId", committeSmsTrackingId);
		    query.setParameter("successCount", successCount);
		    query.setParameter("trackingType", trackingType);
			return query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside DaoImpl UpdateCommitteSmsTrackRow", e);
			return 0;
		}
	}
	
	private static final String UPDATECOMMITTENOSMSPENDING="UPDATE pfms_sms_committe_track SET SmsSentCount=0 AND SmsSentStatus='NA' WHERE CreatedDate=CURDATE() AND SmsTrackingType=:trackingType ";
	@Override
	public long UpdateCommitteNoSmsPending(String trackingType) throws Exception {
		logger.info(new Date() + "Inside DAO UpdateCommitteNoSmsPending");
		try {
			Query query = manager.createNativeQuery(UPDATECOMMITTENOSMSPENDING);
		    query.setParameter("trackingType", trackingType);
			return query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside DaoImpl UpdateCommitteNoSmsPending", e);
			return 0;

		}
	}
	
	private static final String ACTIONASSIGNEDCOUNTS="CALL Pfms_ActionSmsCount(:empId)";
	@Override
	public List<Object[]> ActionAssignedCounts(long empId) throws Exception {
		logger.info(new Date() + "Inside DAO ActionAssignedCounts");
		try {
		Query query = manager.createNativeQuery(ACTIONASSIGNEDCOUNTS);
		query.setParameter("empId", empId);
		List<Object[]> ActionAssignedCounts = query.getResultList();
		return ActionAssignedCounts;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside DaoImpl ActionAssignedCounts", e);
			return null;
		}
	}
	
	
	private static final String DIRECTORACTIONASSIGNEDCOUNTS="CALL Pfms_Director_ActionSmsCount()";
	@Override
	public List<Object[]> DirectorActionAssignedCounts() throws Exception {
		logger.info(new Date() + "Inside DAO DirectorActionAssignedCounts");
		try {
		Query query = manager.createNativeQuery(DIRECTORACTIONASSIGNEDCOUNTS);
		List<Object[]> DirectorActionAssignedCounts = query.getResultList();
		return DirectorActionAssignedCounts;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside DaoImpl DirectorActionAssignedCounts", e);
			return null;
		}
	}
	
	
	private static final String SMSREPORTLIST="SELECT e.EmpName,d.Designation,a.ActionItemP,a.ActionItemTP,a.ActionItemDP,a.MilestoneActionP,a.MilestoneActionTP,a.MilestoneActionDP,a.MeetingActionP,a.MeetingActionTP,a.MeetingActionDP,e.MobileNo,a.Message,DATE(a.SmsSentDate) FROM pfms_sms_track_insights a,employee e,employee_desig d WHERE a.EmpId=e.EmpId AND e.DesigId=d.DesigId AND DATE(a.SmsSentDate) BETWEEN :fromDate AND :toDate";
	@Override
	public List<Object[]> SmsReportList(String fromDate, String toDate) throws Exception {
		logger.info(new Date() +"Inside the SmsReportList");
		try {
			Query query=manager.createNativeQuery(SMSREPORTLIST);
			query.setParameter("fromDate", fromDate);
			query.setParameter("toDate", toDate);
			List<Object[]> smsreportlist=(List<Object[]>)query.getResultList();
			return smsreportlist;
		} catch (Exception e) {
			logger.error(new Date()+"Inside the SmsReportList");
			e.printStackTrace();
			return null;
		}
	}
	
	private static final String SMSCOMMITTEREPORTLIST="SELECT e.EmpName,d.Designation,e.MobileNo,a.Message,DATE(a.SmsSentDate) FROM pfms_sms_committe_track_insights a,employee e,employee_desig d WHERE a.EmpId=e.EmpId AND e.DesigId=d.DesigId AND DATE(a.SmsSentDate) BETWEEN :fromDate AND :toDate";
	@Override
	public List<Object[]> SmsCommitteReportList(String fromDate, String toDate) throws Exception {
		logger.info(new Date() +"Inside the SmsCommitteReportList dao");
		try {
			Query query=manager.createNativeQuery(SMSCOMMITTEREPORTLIST);
			query.setParameter("fromDate", fromDate);
			query.setParameter("toDate", toDate);
			List<Object[]> SmsCommitteReportList=(List<Object[]>)query.getResultList();
			return SmsCommitteReportList;
		} catch (Exception e) {
			logger.info(new Date() +"Inside the SmsCommitteReportList dao");
			e.printStackTrace();
			return null;
		}
	}
}

