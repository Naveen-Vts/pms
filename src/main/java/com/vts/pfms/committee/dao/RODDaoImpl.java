package com.vts.pfms.committee.dao;

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

import com.vts.pfms.committee.model.CommitteeSchedule;
import com.vts.pfms.committee.model.RODMaster;

@Repository
@Transactional
public class RODDaoImpl implements RODDao{

	private static final Logger logger=LogManager.getLogger(RODDaoImpl.class);
			
	@PersistenceContext
	EntityManager manager;
	
	private static final String RODPROJECTSCHEDULELISTALL = "SELECT a.ScheduleId, a.RODNameId,a.MeetingId,a.ScheduleDate,a.ScheduleStartTime,a.ProjectId,b.RODName,a.ScheduleFlag,b.RODShortName,a.initiationid FROM committee_schedule a,pfms_rod_master b WHERE a.RODNameId=b.RODNameId AND a.IsActive=1 AND a.ProjectId=:ProjectId ORDER BY a.ScheduleDate";
	@Override
	public List<Object[]> rodProjectScheduleListAll(String projectId) throws Exception
	{
		try {
			Query query=manager.createNativeQuery(RODPROJECTSCHEDULELISTALL);
			query.setParameter("ProjectId", Long.parseLong(projectId));
			return (List<Object[]>) query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside RODDaoImpl rodProjectScheduleListAll "+e);
			return new ArrayList<>();
		}
		
	}
	
	private static final String RODMASTERDETAILS ="SELECT RODNameId,RODName,RODShortName FROM pfms_rod_master WHERE IsActive=1 AND (CASE WHEN 'A'=:RODNameId THEN 1=1 ELSE RODNameId=:RODNameId END)";
	@Override
	public Object[] getRODMasterDetails(String rodNameId) throws Exception {
		
		try {
			Query query = manager.createNativeQuery(RODMASTERDETAILS);
			query.setParameter("RODNameId", rodNameId);
			List<Object[]> list = (List<Object[]>)query.getResultList();
			if(list.size()>0) {
				return list.get(0);
			}else {
				return null;
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside RODDaoImpl getRODMasterDetails"+e);
			return null;
		}
	}
	
	private static final String RODPROJECTSCHEDULELISTALL2 = "SELECT a.ScheduleId, a.RODNameId,a.MeetingId,a.ScheduleDate,a.ScheduleStartTime,a.ProjectId,b.RODName,a.ScheduleFlag,b.RODShortName,a.initiationid FROM committee_schedule a,pfms_rod_master b WHERE a.RODNameId=b.RODNameId AND a.ProjectId=:ProjectId AND a.RODNameId=:RODNameId AND a.IsActive=1 ORDER BY a.ScheduleDate";
	@Override
	public List<Object[]> rodProjectScheduleListAll(String projectId, String rodNameId) throws Exception
	{
		try {
			Query query=manager.createNativeQuery(RODPROJECTSCHEDULELISTALL2);
			query.setParameter("ProjectId", Long.parseLong(projectId));
			query.setParameter("RODNameId", Long.parseLong(rodNameId));
			return (List<Object[]>) query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside RODDaoImpl rodProjectScheduleListAll "+e);
			return new ArrayList<>();
		}
		
	}
	
	private static final String RODNAMESLIST = "SELECT RODNameId,RODName,RODShortName FROM pfms_rod_master WHERE IsActive=1";
	@Override
	public List<Object[]> rodNamesList() throws Exception
	{
		try {
			Query query=manager.createNativeQuery(RODNAMESLIST);
//			query.setParameter("ProjectId",projectId);
			return (List<Object[]>) query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside RODDaoImpl rodNamesList "+e);
			return new ArrayList<>();
		}
		
	}
	@Override
	public Long addNewRODName(RODMaster master) throws Exception {
		try {
			manager.persist(master);
			manager.flush();
			return master.getRODNameId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside RODDaoImpl addNewRODName "+e);
			return 0L;
		}
		
	}
	
	private static final String RODSCHEDULEEDITDATA = "SELECT a.RODNameId,a.committeemainid,a.scheduledate,a.schedulestarttime,a.scheduleflag,a.schedulesub,a.scheduleid,b.RODName,b.RODShortName,a.projectid,\r\n"
			+ "c.meetingstatusid,a.meetingid,a.meetingvenue,a.confidential,a.Reference,'0' AS classification ,a.divisionid  ,a.initiationid ,a.pmrcdecisions,a.kickoffotp ,\r\n"
			+ "(SELECT minutesattachmentid FROM committee_minutes_attachment WHERE scheduleid=a.scheduleid) AS 'attachid',\r\n"
			+ "'' AS Test2,a.MinutesFrozen,a.briefingpaperfrozen,a.labcode \r\n"
			+ "FROM committee_schedule a,pfms_rod_master b ,committee_meeting_status c \r\n"
			+ "WHERE a.scheduleflag=c.MeetingStatus AND a.scheduleid=:committeescheduleid AND a.RODNameId=b.RODNameId ";

	@Override
	public Object[] RODScheduleEditData(String CommitteeScheduleId) throws Exception {
		try {
			Query query=manager.createNativeQuery(RODSCHEDULEEDITDATA);
			query.setParameter("committeescheduleid", Long.parseLong(CommitteeScheduleId) );
			List<Object[]> list = (List<Object[]>)query.getResultList();
			if(list.size()>0) {
				return list.get(0);
			}else {
				return null;
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside RODDaoImpl RODScheduleEditData "+e);
			return null;
		}
		
	}
	
	private static final String RODSCHEDULEDATA = "SELECT a.ScheduleId, a.CommitteeMainId, a.ScheduleDate, a.ScheduleStartTime, a.ScheduleFlag, a.ScheduleSub, a.IsActive,a.RODNameId ,\r\n"
			+ "       b.RODShortName, b.RODName, c.MeetingStatusId,a.projectid,a.meetingid, a.divisionid ,a.initiationid,a.labcode \r\n"
			+ "FROM committee_schedule a,pfms_rod_master b,committee_meeting_status c \r\n"
			+ "WHERE a.RODNameId=b.RODNameId AND a.scheduleflag=c.MeetingStatus AND a.ScheduleId=:committeescheduleid";
	@Override
	public Object[] RODScheduleData(String CommitteeScheduleId) throws Exception {
		try {
			Query query=manager.createNativeQuery(RODSCHEDULEDATA);
			query.setParameter("committeescheduleid", Long.parseLong(CommitteeScheduleId) );
			List<Object[]> list = (List<Object[]>)query.getResultList();
			if(list.size()>0) {
				return list.get(0);
			}else {
				return null;
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside RODDaoImpl RODScheduleEditData "+e);
			return null;
		}
		
	}
	
	private static final String RODMEETINGSEARCHLIST="SELECT '0' RODNameId, 0 AS empid,a.scheduleid,a.scheduledate,a.schedulestarttime,a.scheduleflag,'NA' AS membertype ,a.meetingid,b.RODName,b.RODShortName AS 'ShortName', a.meetingvenue FROM committee_schedule a,pfms_rod_master b WHERE a.RODNameId=b.RODNameId AND a.meetingid LIKE :meetingid AND a.isactive=1 AND a.labcode=:labcode";
	@Override
	public List<Object[]> RODMeetingSearchList(String MeetingId ,String LabCode) throws Exception {
		try {
			Query query=manager.createNativeQuery(RODMEETINGSEARCHLIST);
			query.setParameter("meetingid",MeetingId);
			query.setParameter("labcode", LabCode);
			return (List<Object[]>) query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside RODDaoImpl MeetingSearchList "+e);
			return new ArrayList<>();
		}
		
	}
	
	private static final String UPDATEOTP="UPDATE committee_schedule SET kickoffotp=:otp,scheduleflag=:scheduleflag WHERE scheduleid=:committeescheduleid";
	@Override
	public String UpdateOtp(CommitteeSchedule schedule) throws Exception {

		Query query=manager.createNativeQuery(UPDATEOTP);
		query.setParameter("otp", schedule.getKickOffOtp());
		query.setParameter("committeescheduleid", schedule.getScheduleId());
		query.setParameter("scheduleflag", schedule.getScheduleFlag());
		int ret=query.executeUpdate();

		if(schedule.getKickOffOtp()==null) {
			return String.valueOf(ret);
		}else {
			return schedule.getKickOffOtp();
		}
		
	}
	
	private static final String NONPROJECTACTIONS="SELECT d.ActionAssignId,d.ActionNo,CONCAT(IFNULL(CONCAT(e.title,' '),IFNULL(CONCAT(e.salutation,' '),'')), e.emp_name) AS 'empname',\r\n"
			+ "d.ActionStatus,c.ActionMainId,c.ActionItem,b.ScheduleMinutesId,a.ScheduleId,a.MeetingId,a.ScheduleDate,d.PDCOrg,d.PDC1,d.PDC2,d.Progress,d.ProgressDate,d.enddate,(select max(progressdate) from action_sub where actionassignId=d.actionassignId)as'lastdate'\r\n"
			+ "FROM committee_schedule a, committee_schedules_minutes_details b , action_main c,action_assign d, employee e\r\n"
			+ "WHERE a.RODNameId = :RODNameId AND a.isactive = 1 AND a.ScheduleId=b.ScheduleId\r\n"
			+ "AND c.ScheduleMinutesId=b.ScheduleMinutesId AND c.ActionMainId=d.ActionMainId AND d.Assignee=e.emp_id ORDER BY ScheduleDate";
	
	@Override
	public List<Object[]> RODActionDetails(String rodNameId) throws Exception {
		Query query =manager.createNativeQuery(NONPROJECTACTIONS);
		query.setParameter("RODNameId", Long.parseLong(rodNameId));
		return (List<Object[]>)query.getResultList();
	}
	
	private static final String COMMPROSCHEDULELIST = "SELECT COUNT(*)+1 FROM  committee_schedule cs ,committee_meeting_status ms WHERE cs.RODNameId=:RODNameId AND cs.projectid=:projectid AND cs.isactive=1 AND  ms.meetingstatus=cs.scheduleflag AND ms.meetingstatusid > 6 AND cs.scheduledate<:sdate ";
	@Override
	public int RODCommProScheduleList(String projectid,String rodNameId,String sdate) throws Exception 
	{
		Query query=manager.createNativeQuery(COMMPROSCHEDULELIST);
		query.setParameter("projectid", Long.parseLong(projectid));
		query.setParameter("RODNameId", Long.parseLong(rodNameId));
		query.setParameter("sdate", sdate);
		Long CommProScheduleList=(Long)query.getSingleResult();
		return CommProScheduleList.intValue();
	}
	
	private static final String INDUSTRYPARTNERREPLISTINVITATIONS ="SELECT a.IndustryPartnerRepId,a.RepName,'00' AS EmpNo,a.RepDesignation,'00' AS DesigId FROM pfms_industry_partner_rep a,pfms_industry_partner b WHERE a.IndustryPartnerId=b.IndustryPartnerId AND b.IndustryPartnerId=:IndustryPartnerId AND a.IsActive=1 AND a.IndustryPartnerRepId NOT IN (SELECT empid  FROM committee_schedules_invitation WHERE  committeescheduleid=:scheduleid AND labcode='@IP')";
	@Override
	public List<Object[]> industryPartnerRepListInvitations(String industryPartnerId, String scheduleid) throws Exception {
		try {
			Query query=manager.createNativeQuery(INDUSTRYPARTNERREPLISTINVITATIONS);
			query.setParameter("IndustryPartnerId", Long.parseLong(industryPartnerId));
			query.setParameter("scheduleid", Long.parseLong(scheduleid));
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
		
	}
}
