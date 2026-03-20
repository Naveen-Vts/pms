package com.vts.pfms.timesheet.dao;

import java.util.ArrayList;
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

import com.vts.pfms.master.model.MilestoneActivityType;
import com.vts.pfms.timesheet.dto.ActionAnalyticsDTO;
import com.vts.pfms.timesheet.model.TimeSheet;
import com.vts.pfms.timesheet.model.TimesheetKeywords;

@Repository
@Transactional
public class TimeSheetDaoImpl implements TimeSheetDao {

	private static final Logger logger = LogManager.getLogger(TimeSheetDaoImpl.class);
	
	@PersistenceContext
	EntityManager manager;
	
	private static final String GETEMPACTIVITYASSIGNLIST = "SELECT a.actionmainid,CONCAT(IFNULL(CONCAT(b.title,' '),''), b.empname) AS 'empname',c.designation,a.actiondate,d.enddate,a.actionitem,d.actionstatus,d.remarks,a.actionlinkid,d.actionno ,d.actionassignid ,d.assignee ,d.assignor , a.actionlevel ,a.projectid FROM  action_main a, employee b ,employee_desig c , action_assign d WHERE a.actionmainid=d.actionmainid AND d.assignor=b.empid AND b.isactive='1' AND c.desigid=b.desigid AND CASE WHEN 'A'=:EmpId THEN 1=1 ELSE d.assignee=:EmpId END AND d.actionstatus IN ('I','A','B') AND d.assigneelabcode<>'@EXP' ORDER BY d.actionassignid DESC";
	@Override
	public List<Object[]> getEmpActivityAssignList(String empId) throws Exception {
		try {
			Query query = manager.createNativeQuery(GETEMPACTIVITYASSIGNLIST);
			query.setParameter("EmpId", empId);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside TimeSheetDaoImpl getEmpActivityAssignList() "+e);
			return new ArrayList<Object[]>();
		}
	}
	
	private static final String GETTIMESHEETBYDATE = "FROM TimeSheet WHERE EmpId=:EmpId AND ActivityFromDate=:ActivityDate AND IsActive=1";
	@Override
	public TimeSheet getTimeSheetByDateAndEmpId(String empId, String activityDate) throws Exception {
		try {
			Query query = manager.createQuery(GETTIMESHEETBYDATE);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("ActivityDate", activityDate);
			List<TimeSheet> list = (List<TimeSheet>)query.getResultList();
			return list.size()>0?list.get(0):null;
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside TimeSheetDaoImpl getTimeSheetByDate() "+e);
			return null;
		}
	}
	
	@Override
	public TimeSheet getTimeSheetById(String timeSheetId) throws Exception {
		try {
			return manager.find(TimeSheet.class, Long.parseLong(timeSheetId));
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside TimeSheetDaoImpl getTimeSheetById() "+e);
			return null;
		}
	}

	@Override
	public Long addTimeSheet(TimeSheet timeSheet) throws Exception {
		try {
			manager.persist(timeSheet);
			manager.flush();
			return timeSheet.getTimeSheetId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside TimeSheetDaoImpl addTimeSheet() "+e);
		}
		return null;
	}
	
	private static final String REMOVETIMESHEETACTIVITIES ="DELETE FROM pfms_timesheet_activity WHERE TimeSheetId=:TimeSheetId";
	@Override
	public int removeTimeSheetActivities(String timeSheetId) throws Exception {
		try {
			Query query = manager.createNativeQuery(REMOVETIMESHEETACTIVITIES);
			query.setParameter("TimeSheetId", Long.parseLong(timeSheetId));
			return query.executeUpdate();
		}catch (Exception e) {
			logger.error(new Date()+" Inside TimeSheetDaoImpl removeTimeSheetActivities() "+e);
			e.printStackTrace();
			return 0;
		}
	}
	
	//private static final String EMPALLTIMESHEETLIST = "SELECT a.TimeSheetId,a.EmpId,a.InitiationDate,a.ActivityFromDate,a.ActivityToDate,a.PunchInTime,a.PunchOutTime,a.TotalDuration,a.EmpStatus,a.TDRemarks,a.TimeSheetStatus  FROM pfms_timesheet a WHERE a.IsActive=1 AND a.ActivityFromDate BETWEEN DATE_SUB(:ActivityDate, INTERVAL 30 DAY) AND DATE_ADD(:ActivityDate , INTERVAL 30 DAY ) AND a.EmpId=:EmpId";
	private static final String EMPALLTIMESHEETLIST = "SELECT a.TimeSheetId,a.EmpId,a.InitiationDate,a.ActivityFromDate,a.ActivityToDate,a.PunchInTime,a.PunchOutTime,a.TotalDuration,a.EmpStatus,a.TDRemarks,a.TimeSheetStatus  FROM pfms_timesheet a WHERE a.IsActive=1 AND a.EmpId=:EmpId";
	@Override
	public List<Object[]> getEmpAllTimeSheetList(String empId) throws Exception {
		try {
			Query query = manager.createNativeQuery(EMPALLTIMESHEETLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside TimeSheetDaoImpl getEmpAllTimeSheetList() "+e);
			return new ArrayList<Object[]>();
		}
	}
	
	private static final String GETEMPLOYEESOFSUPERIOROFFICER = "SELECT a.EmpId,a.Title,a.Salutation,a.SrNo,a.EmpNo,a.EmpName,b.Designation FROM employee a, employee_desig b WHERE a.DesigId=b.DesigId AND a.LabCode=:LabCode AND a.SuperiorOfficer=:SuperiorOfficer ORDER BY CASE WHEN a.SrNo=0 THEN 1 ELSE 0 END,a.SrNo";
	@Override
	public List<Object[]> getEmployeesofSuperiorOfficer(String superiorOfficer, String labCode) throws Exception {
		try {
			Query query = manager.createNativeQuery(GETEMPLOYEESOFSUPERIOROFFICER);
			query.setParameter("SuperiorOfficer", Long.parseLong(superiorOfficer));
			query.setParameter("LabCode", labCode);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside TimeSheetDaoImpl getEmployeesofSuperiorOfficer() "+e);
			return new ArrayList<Object[]>();
		}
	}
	
	private static final String GETTIMESHEETLISTOFEMPLOYEEBYPERIOD = "FROM TimeSheet WHERE IsActive=1 AND TimeSheetStatus NOT IN ('INI') AND EmpId=:EmpId AND ActivityFromDate BETWEEN :FromDate AND :ToDate";
	@Override
	public List<TimeSheet> getTimeSheetListofEmployeeByPeriod(String empId, String fromDate, String toDate) throws Exception {
		try {
			Query query = manager.createQuery(GETTIMESHEETLISTOFEMPLOYEEBYPERIOD);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("FromDate", fromDate);
			query.setParameter("ToDate", toDate);
			return (List<TimeSheet>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside TimeSheetDaoImpl getEmployeesofSuperiorOfficer() "+e);
			return new ArrayList<TimeSheet>();
		}
	}
	
	@Override
	public List<MilestoneActivityType> getMilestoneActivityTypeList() throws Exception {
		try {
			Query query = manager.createQuery("FROM MilestoneActivityType WHERE IsActive=1");
			return (List<MilestoneActivityType>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside TimeSheetDaoImpl getMilestoneActivityTypeList() "+e);
			return new ArrayList<MilestoneActivityType>();
		}
	}
	
	private static final String PROJECTEMPACTIONANALYTICSLIST = "SELECT * FROM pfms_action_analytics WHERE (CASE WHEN 'A'=:ProjectId THEN 1=1 ELSE ProjectId=:ProjectId END) AND (CASE WHEN 'A'=:Assignee THEN 1=1 ELSE Assignee=:Assignee END) AND ActionDate BETWEEN :FromDate AND :ToDate";
	@Override
	public List<ActionAnalyticsDTO> actionAnalyticsList(String empId, String fromDate, String toDate, String projectId) throws Exception {
		try {
			Query query = manager.createNativeQuery(PROJECTEMPACTIONANALYTICSLIST);
			query.setParameter("ProjectId", projectId);
			query.setParameter("Assignee", empId);
			query.setParameter("FromDate", fromDate);
			query.setParameter("ToDate", toDate);
			List<Object[]> actionList = (List<Object[]>)query.getResultList();
			List<ActionAnalyticsDTO> dtoList = new ArrayList<>();
			actionList.stream().forEach(e -> 
			{
				ActionAnalyticsDTO dto = ActionAnalyticsDTO.builder()
										.ActionMainId(e[0]!=null?Long.parseLong(e[0].toString()):0L)
										.EmpName(e[1]!=null?e[1].toString():null)
										.Designation(e[2]!=null?e[2].toString():null)
										.ActionDate(e[3]!=null?e[3].toString():null)
										.EndDate(e[4]!=null?e[4].toString():null)
										.ActionItem(e[5]!=null?e[5].toString():null)
										.ActionStatus(e[6]!=null?e[6].toString():null)
										.ActionNo(e[7]!=null?e[7].toString():null)
										.ActionAssignId(e[8]!=null?Long.parseLong(e[8].toString()):0L)
										.Assignee(e[9]!=null?Long.parseLong(e[9].toString()):0L)
										.Assignor(e[10]!=null?Long.parseLong(e[10].toString()):0L)
										.ActionLevel(e[11]!=null?Long.parseLong(e[11].toString()):0L)
										.ProjectId(e[12]!=null?Long.parseLong(e[12].toString()):0L)
										.ClosedDate(e[13]!=null?e[13].toString():null)
										.build();
				dtoList.add(dto);
			});
			return dtoList;
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside TimeSheetDaoImpl actionAnalyticsList() "+e);
			return new ArrayList<ActionAnalyticsDTO>();
		}
	}
	
	private static final String ALLEMPLIST = "SELECT a.EmpId,CONCAT(IFNULL(CONCAT(a.title,' '),''), a.empname) AS 'empname',b.Designation,a.SuperiorOfficer FROM employee a,employee_desig b WHERE a.DesigId=b.DesigId AND a.IsActive=1 AND a.LabCode=:LabCode ORDER BY CASE WHEN a.SrNo=0 THEN 1 ELSE 0 END,a.SrNo";
	@Override
	public List<Object[]> getAllEmployeeList(String labCode) throws Exception {
		try {
			Query query = manager.createNativeQuery(ALLEMPLIST);
			query.setParameter("LabCode", labCode);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside TimeSheetDaoImpl getAllEmployeeList() "+e);
			return new ArrayList<>();
		}
	}

	//private static final String EMPACTIVITYWISEANALYTICSLIST = "SELECT * FROM pfms_activity_analytics WHERE EmpId=:EmpId AND (CASE WHEN 'A'=:ProjectId THEN 1=1 ELSE ProjectId=:ProjectId END) AND ActivityFromDate BETWEEN :FromDate AND :ToDate";
	private static final String EMPACTIVITYWISEANALYTICSLIST = "CALL pfms_timesheet_activity_analytics(:EmpId,:ProjectId,:FromDate,:ToDate)";
	@Override
	public List<Object[]> empActivityWiseAnalyticsList(String empId, String fromDate, String toDate, String projectId) throws Exception {
		try {
			Query query = manager.createNativeQuery(EMPACTIVITYWISEANALYTICSLIST);
			query.setParameter("EmpId", empId);
			query.setParameter("FromDate", fromDate);
			query.setParameter("ToDate", toDate);
			query.setParameter("ProjectId", projectId);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside TimeSheetDaoImpl empActivityWiseAnalyticsList() "+e);
			return new ArrayList<>();
		}
	}
	
	//private static final String PROJECTACTIVITYWISEANALYTICSLIST = "SELECT * FROM pfms_activity_analytics WHERE ProjectId=:ProjectId AND (CASE WHEN 'A'=:EmpId THEN 1=1 ELSE EmpId=:EmpId END) AND ActivityFromDate BETWEEN :FromDate AND :ToDate";
	private static final String PROJECTACTIVITYWISEANALYTICSLIST = "CALL pfms_timesheet_activity_analytics(:EmpId,:ProjectId,:FromDate,:ToDate)";
	@Override
	public List<Object[]> projectActivityWiseAnalyticsList(String empId, String fromDate, String toDate, String projectId) throws Exception {
		try {
			Query query = manager.createNativeQuery(PROJECTACTIVITYWISEANALYTICSLIST);
			query.setParameter("ProjectId", projectId);
			query.setParameter("EmpId", empId);
			query.setParameter("FromDate", fromDate);
			query.setParameter("ToDate", toDate);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside TimeSheetDaoImpl projectActivityWiseAnalyticsList() "+e);
			return new ArrayList<>();
		}
	}
	
	private static final String PROJECTACTIONANALYTICSLIST = "SELECT * FROM pfms_action_analytics WHERE ProjectId=:ProjectId AND ActionDate BETWEEN :FromDate AND :ToDate";
	@Override
	public List<Object[]> projectActionAnalyticsList(String projectId, String fromDate, String toDate) throws Exception {
		try {
			Query query = manager.createNativeQuery(PROJECTACTIONANALYTICSLIST);
			query.setParameter("ProjectId", Long.parseLong(projectId));
			query.setParameter("FromDate", fromDate);
			query.setParameter("ToDate", toDate);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside TimeSheetDaoImpl projectActionAnalyticsList() "+e);
			return new ArrayList<>();
		}
	}
	
	private static final String GETALLEMPTIMESHEETWORKINGHRSLIST = "CALL pfms_timesheet_list(:LabCode, :LoginType, :EmpId, :FromDate, :ToDate)";
	@Override
	public List<Object[]> getAllEmpTimeSheetWorkingHrsList(String labCode, String loginType, String empId, String fromDate, String toDate) throws Exception {
		try {
			Query query = manager.createNativeQuery(GETALLEMPTIMESHEETWORKINGHRSLIST);
			query.setParameter("LabCode", labCode);
			query.setParameter("LoginType", loginType);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("FromDate", fromDate);
			query.setParameter("ToDate", toDate);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside TimeSheetDaoImpl getAllEmpTimeSheetWorkingHrsList() "+e);
			return new ArrayList<>();
		}
	}
	
	private static final String GETPROJECTTIMESHEETWORKINGHRSLIST = "CALL pfms_project_timesheet(:LabCode, :LoginType, :ProjectId, :FromDate, :ToDate)";
	@Override
	public List<Object[]> getProjectTimeSheetWorkingHrsList(String labCode, String loginType, String empId, String fromDate, String toDate) throws Exception {
		try {
			Query query = manager.createNativeQuery(GETPROJECTTIMESHEETWORKINGHRSLIST);
			query.setParameter("LabCode", labCode);
			query.setParameter("LoginType", loginType);
			query.setParameter("ProjectId", empId);
			query.setParameter("FromDate", fromDate);
			query.setParameter("ToDate", toDate);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside TimeSheetDaoImpl getProjectTimeSheetWorkingHrsList() "+e);
			return new ArrayList<>();
		}
	}

	private static final String EMPEXTRAWORKINGDAYSLIST = "SELECT a.EmpId, CONCAT(IFNULL(CONCAT(a.title,' '),''), a.EmpName) AS 'EmpName', b.Designation, b.DesigCadre,CONCAT(c.ActivityFromDate,''),CONCAT(c.TotalDuration,'') FROM employee a, employee_desig b, pfms_timesheet c WHERE a.DesigId=b.DesigId AND a.EmpId=c.EmpId AND a.EmpId=:EmpId AND c.ActivityFromDate BETWEEN :FromDate AND :ToDate AND ((c.ActivityFromDate IN (SELECT d.HolidayDate FROM pfms_holiday_master d)) OR (DAYOFWEEK(c.ActivityFromDate) IN (1, 7))) ORDER BY c.ActivityFromDate";
	@Override
	public List<Object[]> empExtraWorkingDaysList(String empId, String fromDate, String toDate) throws Exception {
		try {
			Query query = manager.createNativeQuery(EMPEXTRAWORKINGDAYSLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("FromDate", fromDate);
			query.setParameter("ToDate", toDate);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside TimeSheetDaoImpl empExtraWorkingDaysList() "+e);
			return new ArrayList<>();
		}
	}
	
	private static final String HOLIDAYLIST = "SELECT HolidayId,CONCAT(HolidayDate,'') as HolidayDate,HolidayName,HolidayType FROM pfms_holiday_master WHERE IsActive=1";
	@Override
	public List<Object[]> getHolidayList() throws Exception {
		try {
			Query query = manager.createNativeQuery(HOLIDAYLIST);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside TimeSheetDaoImpl getHolidayList() "+e);
			return new ArrayList<>();
		}
	}
	
	private static final String PROJECTWISEEMPEXTRAWORKINGDAYSLIST = "SELECT a.EmpId,b.ProjectId,\r\n"
			+ "(CASE WHEN b.ProjectId='0' THEN 'General' ELSE (SELECT c.ProjectCode FROM project_master c WHERE c.ProjectId=b.ProjectId LIMIT 1) END) AS ProjectName,\r\n"
			+ "(SELECT COALESCE(CONCAT(SEC_TO_TIME(SUM(TIME_TO_SEC(b.ActivityDuration))),''), '00:00:00')) AS 'Duration'\r\n"
			+ "FROM pfms_timesheet a, pfms_timesheet_activity b\r\n"
			+ "WHERE a.TimeSheetId=b.TimeSheetId \r\n"
			+ "      AND ((ActivityFromDate IN (SELECT c.HolidayDate FROM pfms_holiday_master c WHERE c.HolidayType='G')) OR (DAYOFWEEK(a.ActivityFromDate) IN (1, 7)))\r\n"
			+ "      AND a.EmpId = :EmpId AND a.ActivityFromDate BETWEEN :FromDate AND :ToDate\r\n"
			+ "GROUP BY b.ProjectId";
	@Override
	public List<Object[]> projectWiseEmpExtraWorkingDaysList(String empId, String fromDate, String toDate) throws Exception {
		try {
			Query query = manager.createNativeQuery(PROJECTWISEEMPEXTRAWORKINGDAYSLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("FromDate", fromDate);
			query.setParameter("ToDate", toDate);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside TimeSheetDaoImpl projectWiseEmpExtraWorkingDaysList() "+e);
			return new ArrayList<>();
		}
	}
	
	private static final String ROLEWISEEMPLOYEELIST = "CALL pfms_rolewise_emplist(:LabCode, :LoginType, :EmpId)";
	@Override
	public List<Object[]> getRoleWiseEmployeeList(String labCode, String loginType, String empId) throws Exception {
		try {
			Query query = manager.createNativeQuery(ROLEWISEEMPLOYEELIST);
			query.setParameter("LabCode", labCode);
			query.setParameter("LoginType", loginType);
			query.setParameter("EmpId", Long.parseLong(empId));
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside TimeSheetDaoImpl getRoleWiseEmployeeList() "+e);
			return new ArrayList<>();
		}
	}
	
	private static final String GETEMPLOYEENEWTIMESHEETLIST = "SELECT a.TimeSheetId, a.EmpId, a.ActivityFromDate, b.TimeSheetActivityId, b.ActivityTypeId, e.ActivityType,e.ActivityCode, b.ProjectId, (CASE WHEN b.ProjectId=0 THEN 'General' ELSE CONCAT(g.project_code, ' (', g.project_short_name, ')') END) AS 'Project',\r\n"
			+ "	b.AssignedBy, CONCAT(IFNULL(CONCAT(c.Title,' '),(IFNULL(CONCAT(c.Salutation, ' '), ''))), c.emp_name) AS 'Assigner', d.Designation AS 'AssignerDesig', b.KeywordId, f.Keyword, b.WorkDone, b.WorkDoneon, b.ActivitySeqNo,\r\n"
			+ "	CONCAT(IFNULL(CONCAT(h.Title,' '),(IFNULL(CONCAT(h.Salutation, ' '), ''))), h.emp_name) AS 'Employee', i.Designation\r\n"
			+ "FROM pfms_timesheet a\r\n"
			+ "JOIN pfms_timesheet_activity b ON a.TimeSheetId=b.TimeSheetId AND b.IsActive=1\r\n"
			+ "LEFT JOIN employee c ON b.AssignedBy=c.emp_id\r\n"
			+ "LEFT JOIN employee_desig d ON c.desig_id=d.desig_id\r\n"
			+ "LEFT JOIN milestone_activity_type e ON b.ActivityTypeId=e.ActivityTypeId\r\n"
			+ "LEFT JOIN pfms_timesheet_keywords f ON b.KeywordId=f.KeywordId\r\n"
			+ "LEFT JOIN project_master g ON b.ProjectId = g.project_id\r\n"
			+ "LEFT JOIN employee h ON a.EmpId = h.emp_id\r\n"
			+ "LEFT JOIN employee_desig i ON h.desig_id=i.desig_id\r\n"
			+ "WHERE a.IsActive=1 AND (CASE WHEN 'A'=:EmpId THEN 1=1 ELSE a.EmpId=:EmpId END) AND a.ActivityFromDate BETWEEN :FromDate AND :ToDate ORDER BY a.ActivityFromDate";
	@Override
	public List<Object[]> getEmployeeNewTimeSheetList(String empId, String fromDate, String toDate) throws Exception {
		try {
			Query query = manager.createNativeQuery(GETEMPLOYEENEWTIMESHEETLIST);
			query.setParameter("EmpId",empId);
			query.setParameter("FromDate", fromDate);
			query.setParameter("ToDate", toDate);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside TimeSheetDaoImpl getEmployeeNewTimeSheetList() "+e);
			return new ArrayList<>();
		}
	}
	
	@Override
	public List<TimesheetKeywords> getTimesheetKeywordsList() throws Exception {
		try {
			Query query = manager.createQuery("FROM TimesheetKeywords WHERE IsActive=1");
			return (List<TimesheetKeywords>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside TimeSheetDaoImpl getTimesheetKeywordsList() "+e);
			return new ArrayList<>();
		}
		
	}
	
	private static final String GETEMPACTIVITYSUBMISSIONCOUNT = "SELECT COUNT(b.TimeSheetId) FROM pfms_timesheet_activity b, pfms_timesheet a  WHERE a.TimeSheetId=b.TimeSheetId AND b.IsActive=1 AND a.EmpId=:EmpId AND SUBSTR(ActivityFromDate,1,4)=:Year";
	@Override
	public long getEmployeeActivitySubmissionCount(String empId, String year) throws Exception {

		try {
			Query query =  manager.createNativeQuery(GETEMPACTIVITYSUBMISSIONCOUNT);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("Year", year);
			return (Long)query.getSingleResult();

		}catch ( NoResultException e ) {
			e.printStackTrace();
			logger.error(new Date() +" Inside TimeSheetDaoImpl getEmployeeActivitySubmissionCount "+ e);
			return 0;
		}
	}
	
	@Override
	public Long addTimesheetKeywords(TimesheetKeywords timeSheetKeywords) throws Exception {
		try {
			manager.persist(timeSheetKeywords);
			manager.flush();
			return timeSheetKeywords.getKeywordId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside TimeSheetDaoImpl addTimesheetKeywords "+e);
			return 0L;
		}
	}

}
