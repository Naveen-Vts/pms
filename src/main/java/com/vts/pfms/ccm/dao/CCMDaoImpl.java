package com.vts.pfms.ccm.dao;

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

import com.vts.pfms.ccm.model.CCMASPStatus;
import com.vts.pfms.ccm.model.CCMAchievements;
import com.vts.pfms.ccm.model.CCMClosureStatus;
import com.vts.pfms.ccm.model.CCMPresentationSlides;
import com.vts.pfms.committee.model.CommitteeMember;
import com.vts.pfms.committee.model.CommitteeSchedule;
import com.vts.pfms.committee.model.CommitteeScheduleAgenda;
import com.vts.pfms.login.PFMSCCMData;

@Repository
@Transactional
public class CCMDaoImpl implements CCMDao{

	private static final Logger logger = LogManager.getLogger(CCMDaoImpl.class);
	
	@PersistenceContext
	EntityManager manager;
	
	@Override
	public CommitteeMember getCommitteeMemberById(String committeeMemberId) throws Exception {
		try {
			return manager.find(CommitteeMember.class, Long.parseLong(committeeMemberId));
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl getCommitteeMemberById "+e);
			return null;
		}
		
	}
	
	@Override
	public List<CommitteeSchedule> getScheduleListByYear(String year) throws Exception {
		try {
			Query query = manager.createQuery("FROM CommitteeSchedule WHERE YEAR(ScheduleDate)=:Year AND ScheduleType='C' AND IsActive=1");
			query.setParameter("Year", Integer.parseInt(year));
			return (List<CommitteeSchedule>)query.getResultList();
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl getScheduleListByYear "+e);
			return null;
		}
	}
	
	@Override
	public CommitteeSchedule getCCMScheduleById(String ccmScheduleId) throws Exception {
		try {
			return manager.find(CommitteeSchedule.class, Long.parseLong(ccmScheduleId));
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl getCCMScheduleById "+e);
			return null;
		}
		
	}

	@Override
	public long addCCMSchedule(CommitteeSchedule ccmSchedule) throws Exception {
		try {
			manager.persist(ccmSchedule);
			manager.flush();
			return ccmSchedule.getScheduleId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl addCCMSchedule "+e);
			return 0;
		}
	}
	
	private static final String GETMAXCCMSCHEDULEIDFORMONTH="SELECT IFNULL(COUNT(ScheduleId),0) AS 'MAX' FROM committee_schedule WHERE IsActive=1 AND MeetingId LIKE :Month";
	@Override
	public long getMaxCCMScheduleIdForMonth(String sequence) throws Exception {

		try {
			Query query =  manager.createNativeQuery(GETMAXCCMSCHEDULEIDFORMONTH);
			query.setParameter("Month", sequence+"%");
			return (Long)query.getSingleResult();
		}catch ( NoResultException e ) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CCMDaoImpl getMaxCCMScheduleIdForMonth "+ e);
			return 0;
		}
	}
	
	@Override
	public Long addCCMScheduleAgenda(CommitteeScheduleAgenda agenda) throws Exception {
		try {
			manager.persist(agenda);
			manager.flush();
			return agenda.getScheduleAgendaId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl addCCMScheduleAgenda "+e);
			return 0L;
		}
	}
	
	private static final String GETCCMSCHEDULEAGENDALISTBYCCMSCHEDULEID = "SELECT b.ScheduleAgendaId, b.ScheduleId, b.ParentScheduleAgendaId, b.AgendaPriority, b.AgendaItem, b.PresentorLabCode, b.PresenterId, b.Duration, b.FileName,\r\n"
			+ "	(CASE WHEN b.PresentorLabCode='@EXP' THEN (SELECT CONCAT(IFNULL(CONCAT(c.Title,' '), (IFNULL(CONCAT(c.Salutation, ' '), '')) ), c.ExpertName, ', Expert') FROM expert c, employee_desig d WHERE c.ExpertId=b.PresenterId AND c.DesigId=d.DesigId LIMIT 1)\r\n"
			+ "	ELSE (SELECT CONCAT(IFNULL(CONCAT(c.Title,' '),(IFNULL(CONCAT(c.Salutation, ' '), ''))), c.EmpName, ', ', d.Designation) FROM employee c, employee_desig d WHERE c.EmpId=b.PresenterId AND c.DesigId=d.DesigId LIMIT 1) END) AS 'Presenter',\r\n"
			+ "	(SELECT d.DesigId FROM employee c, employee_desig d WHERE c.emp_id=b.PresenterId AND c.desig_id=d.DesigId LIMIT 1) AS 'DesigId'\r\n"
			+ "	FROM committee_schedule a, committee_schedules_agenda b\r\n"
			+ "	WHERE a.ScheduleId=b.ScheduleId AND b.IsActive=1 AND a.ScheduleId=:ScheduleId ORDER BY b.AgendaPriority";
	@Override
	public List<Object[]> getCCMScheduleAgendaListByCCMScheduleId(String ccmScheduleId) throws Exception {
		try {
			Query query = manager.createNativeQuery(GETCCMSCHEDULEAGENDALISTBYCCMSCHEDULEID);
			query.setParameter("ScheduleId", Long.parseLong(ccmScheduleId));
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl getCCMScheduleAgendaListByCCMScheduleId "+e);
			return new ArrayList<Object[]>();
		}
	}
	
	@Override
	public CommitteeScheduleAgenda getCCMScheduleAgendaById(String ccmScheduleAgendaId) throws Exception {
		try {
			return manager.find(CommitteeScheduleAgenda.class, Long.parseLong(ccmScheduleAgendaId));
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl getCCMScheduleAgendaById "+e);
			return null;
		}
		
	}
	
	private static final String GETMAXAGENDAPRIORITY ="SELECT IFNULL(MAX(AgendaPriority),0) AS 'MAX' FROM committee_schedules_agenda WHERE ScheduleId=:ScheduleId AND ParentScheduleAgendaId=:ParentScheduleAgendaId";
	@Override
	public int getMaxAgendaPriority(String ccmScheduleId, String parentScheduleAgendaId) throws Exception {

		try {
			Query query =  manager.createNativeQuery(GETMAXAGENDAPRIORITY);
			query.setParameter("ScheduleId", Long.parseLong(ccmScheduleId));
			query.setParameter("ParentScheduleAgendaId", Long.parseLong(parentScheduleAgendaId));
			Long maxvalue=(Long)query.getSingleResult();
			return maxvalue.intValue();
		}catch ( NoResultException e ) {
			logger.error(new Date() +" Inside CCMDaoImpl getMaxAgendaPriority "+ e);
			e.printStackTrace();
			return 0;
		}
	}
	
	@Override
	public int ccmAgendaPriorityUpdate(String scheduleAgendaId,String agendaPriority) throws Exception
	{
		try {
			
			CommitteeScheduleAgenda ExistingCommitteeScheduleAgenda= manager.find(CommitteeScheduleAgenda.class, Long.parseLong(scheduleAgendaId));
			if(ExistingCommitteeScheduleAgenda != null) {
				ExistingCommitteeScheduleAgenda.setAgendaPriority(Integer.parseInt(agendaPriority));
				return 1;
			}
			else {
				return 0;
			}
			
		}catch ( Exception e ) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CCMDaoImpl ccmAgendaPriorityUpdate "+ e);
			return 0;
		}

	}
	
	private static final String GETCCMSCHEDULEAGENDASAFTER ="SELECT ScheduleAgendaId,AgendaPriority FROM committee_schedules_agenda WHERE ScheduleId=:ScheduleId AND ParentScheduleAgendaId=:ParentScheduleAgendaId AND AgendaPriority>:AgendaPriority ORDER BY AgendaPriority ASC";
	@Override
	public List<Object[]> getCCMScheduleAgendasAfter(String ccmScheduleId,String agendaPriority, String parentScheduleAgendaId) throws Exception
	{
		try {
			Query query=manager.createNativeQuery(GETCCMSCHEDULEAGENDASAFTER);
			query.setParameter("AgendaPriority", agendaPriority);
			query.setParameter("ScheduleId", Long.parseLong(ccmScheduleId));
			query.setParameter("ParentScheduleAgendaId", Long.parseLong(parentScheduleAgendaId));
			return (List<Object[]>)query.getResultList();
		}catch ( Exception e ) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CCMDaoImpl getCCMScheduleAgendasAfter "+ e);
			return new ArrayList<Object[]>();
		}

	}
	
	@Override
	public int ccmScheduleAgendaDelete(String scheduleAgendaId, String modifiedby ,String modifiedDate)throws Exception
	{
		try {
			
			CommitteeScheduleAgenda ExistingCommitteeScheduleAgenda= manager.find(CommitteeScheduleAgenda.class, Long.parseLong(scheduleAgendaId));
			if(ExistingCommitteeScheduleAgenda != null) {
				ExistingCommitteeScheduleAgenda.setModifiedBy(modifiedby);
				ExistingCommitteeScheduleAgenda.setModifiedDate(modifiedDate);
				ExistingCommitteeScheduleAgenda.setIsActive(0);
				ExistingCommitteeScheduleAgenda.setAgendaPriority(0);
				return 1;
			}
			else {
				return 0;
			}
			
		}catch ( Exception e ) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CCMDaoImpl ccmScheduleAgendaDelete "+ e);
			return 0;
		}
		
	}
	
	private static final String CCMSCHEDULESUBAGENDADELETE = "UPDATE committee_schedules_agenda SET ModifiedBy=:ModifiedBy, ModifiedDate=:ModifiedDate, IsActive=0, AgendaPriority=0 WHERE ParentScheduleAgendaId=:ScheduleAgendaId";
	@Override
	public int ccmScheduleSubAgendaDelete(String scheduleAgendaId, String modifiedby ,String modifiedDate)throws Exception
	{
		try {
			Query query = manager.createNativeQuery(CCMSCHEDULESUBAGENDADELETE);
			query.setParameter("ScheduleAgendaId", Long.parseLong(scheduleAgendaId));
			query.setParameter("ModifiedBy", modifiedby);
			query.setParameter("ModifiedDate", modifiedDate);
			return query.executeUpdate();
		}catch ( Exception e ) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CCMDaoImpl ccmScheduleAgendaDelete "+ e);
			return 0;
		}
		
	}
	
	private static final String UPDATECCMSCHEDULEAGENDADURATION = "UPDATE committee_schedules_agenda SET Duration=(Duration - :OrgDuration)+:Duration WHERE ScheduleAgendaId=:ScheduleAgendaId";
	@Override
	public int updateCCMScheduleAgendaDuration(int orgDuration, int duration, Long scheduleAgendaId) throws Exception {
		try {
			Query query = manager.createNativeQuery(UPDATECCMSCHEDULEAGENDADURATION);
			query.setParameter("ScheduleAgendaId", scheduleAgendaId);
			query.setParameter("OrgDuration", orgDuration);
			query.setParameter("Duration", duration);
			return query.executeUpdate();
		}catch ( Exception e ) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CCMDaoImpl updateCCMScheduleAgendaDuration "+ e);
			return 0;
		}
	}
	
	private static final String GETCOMMITTEEMAINIDBYCOMMITTEECODE = "SELECT a.CommitteeMainId FROM committee_main a, committee b WHERE a.CommitteeId=b.CommitteeId AND b.CommitteeShortName=:CommitteeShortName AND a.ProjectId='0' AND a.DivisionId='0' AND a.InitiationId='0' AND CURDATE() BETWEEN a.ValidFrom AND a.ValidTo AND a.IsActive=1 ORDER BY a.CommitteeMainId DESC LIMIT 1";
	@Override
	public Long getCommitteeMainIdByCommitteeCode(String committeeCode) throws Exception {
		try {
			Query query = manager.createNativeQuery(GETCOMMITTEEMAINIDBYCOMMITTEECODE);
			//query.setParameter("LabCode", labCode);
			query.setParameter("CommitteeShortName", committeeCode);
			return (Long)query.getSingleResult();
		}catch ( Exception e ) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CCMDaoImpl getCommitteeMainIdByCommitteeCode "+ e);
			return 0L;
		}
	}
	
	private static final String GETCOMMITTEEMIDBYCOMMITTEECODE = "SELECT a.committeeId FROM committee a WHERE a.CommitteeShortName=:CommitteeShortName AND a.IsActive=1";
	@Override
	public Long getCommitteeIdByCommitteeCode(String committeeCode) throws Exception {
		try {
			Query query = manager.createNativeQuery(GETCOMMITTEEMIDBYCOMMITTEECODE);
			//query.setParameter("LabCode", labCode);
			query.setParameter("CommitteeShortName", committeeCode);
			return (Long)query.getSingleResult();
		}catch ( Exception e ) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CCMDaoImpl getCommitteeIdByCommitteeCode "+ e);
			return 0L;
		}
	}
	
	private static final String GETLATESTSCHEDULEID = "SELECT a.ScheduleId FROM committee_schedule a WHERE a.ScheduleType=:ScheduleType ORDER BY ScheduleDate DESC LIMIT 1";
	@Override
	public Long getLatestScheduleId(String scheduleType) throws Exception {

		try {
			Query query = manager.createNativeQuery(GETLATESTSCHEDULEID);
			query.setParameter("ScheduleType", scheduleType);
			return (Long)query.getSingleResult();
		}catch ( Exception e ) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CCMDaoImpl getLatestScheduleId "+ e);
			return 0L;
		}
	}
	
	private static final String GETSECONDLATESTSCHEDULEID = "SELECT a.ScheduleId FROM committee_schedule a WHERE a.ScheduleType=:ScheduleType ORDER BY ScheduleDate DESC LIMIT 1 OFFSET 1";
	@Override
	public Long getSecondLatestScheduleId(String scheduleType) throws Exception {
		
		try {
			Query query = manager.createNativeQuery(GETSECONDLATESTSCHEDULEID);
			query.setParameter("ScheduleType", scheduleType);
			return (Long)query.getSingleResult();
		}catch ( Exception e ) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CCMDaoImpl getSecondLatestScheduleId "+ e);
			return 0L;
		}
	}
	
	private static final String GETLATESTSCHEDULEMINUTESTIDS = "SELECT (CAST(ScheduleMinutesId AS CHAR)) AS 'ScheduleMinutesId' FROM committee_schedules_minutes_details WHERE ScheduleId=:ScheduleId";
	@Override
	public List<String> getLatestScheduleMinutesIds(String scheduleId) throws Exception {
		List<String> list = new ArrayList<>();
		try {
			Query query = manager.createNativeQuery(GETLATESTSCHEDULEMINUTESTIDS);
			query.setParameter("ScheduleId", Long.parseLong(scheduleId));
			list =  (List<String>)query.getResultList();
			return list;
		}catch ( Exception e ) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CCMDaoImpl getLatestScheduleMinutesIds "+ e);
			return null;
		}
	}
	
	private static final String GETCLUSTERLABLISTBYCLUSTERID ="SELECT a.LabId, a.LabName, a.LabCode, b.IsCluster, a.ClusterId FROM cluster_lab a, lab_master b WHERE a.LabId=b.LabId AND a.ClusterId=:ClusterId ORDER BY a.LabCode";
	@Override
	public List<Object[]> getClusterLabListByClusterId(String clusterId) throws Exception
	{
		try {
			Query query=manager.createNativeQuery(GETCLUSTERLABLISTBYCLUSTERID);
			query.setParameter("ClusterId", Long.parseLong(clusterId));
			return (List<Object[]>)query.getResultList();
		}catch ( Exception e ) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CCMDaoImpl getClusterLabListByClusterId "+ e);
			return new ArrayList<Object[]>();
		}

	}
	
	@Override
	public List<CommitteeSchedule> getScheduleListByScheduleType(String scheduleType) throws Exception {
		try {
			Query query = manager.createQuery("FROM CommitteeSchedule WHERE ScheduleType=:ScheduleType AND IsActive=1");
			query.setParameter("ScheduleType", scheduleType);
			return (List<CommitteeSchedule>)query.getResultList();
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl getScheduleListByScheduleType "+e);
			return null;
		}
	}
	
	@Override
	public List<CCMAchievements> getCCMAchievementsByScheduleId(Long scheduleId, String topicType) throws Exception {
		try {
			Query query = manager.createQuery("FROM CCMAchievements WHERE TopicType=:TopicType AND ScheduleId =:ScheduleId AND IsActive=1");
			query.setParameter("ScheduleId", scheduleId);
			query.setParameter("TopicType", topicType);
			return (List<CCMAchievements>)query.getResultList();
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl getCCMAchievementsByScheduleId "+e);
			return null;
		}
	}
	
	@Override
	public CCMAchievements getCCMAchievementsById(String achievementId) throws Exception {
		try {
			return manager.find(CCMAchievements.class, Long.parseLong(achievementId));
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl getCCMAchievementsById "+e);
			return null;
		}
		
	}
	
	@Override
	public long addCCMAchievements(CCMAchievements achmnts) throws Exception {
		try {
			manager.persist(achmnts);
			manager.flush();
			return achmnts.getAchievementId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl addCCMAchievements "+e);
			return 0;
		}
	}
	
	@Override
	public int ccmAchievementDelete(String achievementId) throws Exception {
		try {
			
			CCMAchievements ExistingCCMAchievements = manager.find(CCMAchievements.class, Long.parseLong(achievementId));
			if(ExistingCCMAchievements != null) {
				ExistingCCMAchievements.setIsActive(0);
				return 1;
			}
			else {
				return 0;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl ccmAchievementDelete "+e);
			return 0;
		}
		
	}
	
	private static final String GETCASHOUTGOLIST = "SELECT a.CCMDataId, b.ClusterId, b.LabCode, a.ProjectId, a.ProjectCode, a.BudgetHeadId, a.BudgetHeadDescription, a.AllotmentCost, a.Expenditure, a.Balance, a.Q1CashOutGo, a.Q2CashOutGo, a.Q3CashOutGo, a.Q4CashOutGo, a.Required, a.CreatedDate, (a.Q1CashOutGo+a.Q2CashOutGo+a.Q3CashOutGo+a.Q4CashOutGo) AS 'TotalCOG' FROM lab_master b JOIN pfms_ccm_data a ON a.LabCode=b.LabCode WHERE b.IsCluster<>'Y' ORDER BY b.LabCode, FIELD(a.BudgetHeadDescription, 'Revenue','Capital','Miscellaneous'), TotalCOG DESC";
	@Override
	public List<Object[]> getCashOutGoList() throws Exception {
		
		try {
			
			Query query = manager.createNativeQuery(GETCASHOUTGOLIST);
			return (List<Object[]>)query.getResultList();
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl getCashOutGoList "+e);
			return new ArrayList<>();
		}
	}
	
	@Override
	public long addPFMSCCMData(PFMSCCMData ccmData) throws Exception {
		try {
			manager.persist(ccmData);
			manager.flush();
			return ccmData.getCCMDataId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl addPFMSCCMData "+e);
			return 0;
		}
	}

	private static final String GETPROJECTLISTBYLABCODE = "SELECT ProjectId,ProjectCode, ProjectShortName, ProjectName FROM project_master WHERE LabCode=:LabCode AND IsActive=1";
	@Override
	public List<Object[]> getProjectListByLabCode(String labCode) throws Exception {
		try {
			Query query = manager.createNativeQuery(GETPROJECTLISTBYLABCODE);
			query.setParameter("LabCode", labCode);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl getProjectListByLabCode "+e);
			return new ArrayList<Object[]>();
		}
	}

	@Override
	public CCMPresentationSlides getCCMPresentationSlidesByScheduleId(String scheduleId) throws Exception {
		try {
			Query query = manager.createQuery("FROM CCMPresentationSlides WHERE ScheduleId=:ScheduleId AND IsActive=1");
			query.setParameter("ScheduleId", Long.parseLong(scheduleId));
			
			List<CCMPresentationSlides> list = (List<CCMPresentationSlides>)query.getResultList();
			return list.size()>0?list.get(0):null;
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl getCCMPresentationSlidesByScheduleId "+e);
			return null;
		}
	}

	@Override
	public CCMPresentationSlides getCCMPresentationSlidesById(String ccmPresSlideId) throws Exception {
		try {
			return manager.find(CCMPresentationSlides.class, Long.parseLong(ccmPresSlideId));
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl getCCMPresentationSlidesById "+e);
			return null;
		}
		
	}

	@Override
	public long addCCMPresentationSlides(CCMPresentationSlides slide) throws Exception {
		try {
			manager.persist(slide);
			manager.flush();
			return slide.getCCMPresSlideId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl addCCMPresentationSlides "+e);
			return 0;
		}
	}

	private static final String GETLASTSCHEDULEIDFROMCURRENTSCHEDULEID = "SELECT a.ScheduleId FROM committee_schedule a WHERE a.ScheduleType='C' AND a.ScheduleDate<(SELECT b.ScheduleDate FROM committee_schedule b WHERE b.ScheduleId=:ScheduleId) ORDER BY a.ScheduleDate DESC LIMIT 1";
	@Override
	public Long getLastScheduleIdFromCurrentScheduleId(String ccmScheduleId) throws Exception {

		try {
			Query query = manager.createNativeQuery(GETLASTSCHEDULEIDFROMCURRENTSCHEDULEID);
			query.setParameter("ScheduleId", Long.parseLong(ccmScheduleId));
			return (Long)query.getSingleResult();
		}catch ( Exception e ) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CCMDaoImpl getLastScheduleIdFromCurrentScheduleId "+ e);
			return 0L;
		}
	}

	private static final String GETPREVIOUSSCHEDULEMINUTESTIDS = "SELECT (CAST(ScheduleMinutesId AS CHAR)) AS 'ScheduleMinutesId' FROM committee_schedules_minutes_details a, committee_schedule b WHERE a.ScheduleId=b.ScheduleId AND b.ScheduleType='C' AND b.ScheduleDate<(SELECT c.ScheduleDate FROM committee_schedule c WHERE c.ScheduleId=:ScheduleId)";
	@Override
	public List<String> getPreviousScheduleMinutesIds(String scheduleId) throws Exception {
		List<String> list = new ArrayList<>();
		try {
			Query query = manager.createNativeQuery(GETPREVIOUSSCHEDULEMINUTESTIDS);
			query.setParameter("ScheduleId", Long.parseLong(scheduleId));
			list =  (List<String>)query.getResultList();
			return list;
		}catch ( Exception e ) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CCMDaoImpl getPreviousScheduleMinutesIds "+ e);
			return null;
		}
	}

	private static final String GETPMRCEBCALENDARDATA = "CALL pfms_ccm_calendar_data(:MonthStartDate, :Meeting, :Days, :ClusterId)";
	@Override
	public List<Object[]> getEBPMRCCalendarData(String monthStartDate , String meeting, String clusterId) throws Exception {
		try {
			Query query = manager.createNativeQuery(GETPMRCEBCALENDARDATA);
			query.setParameter("MonthStartDate", monthStartDate);
			query.setParameter("Meeting", meeting);
			query.setParameter("Days", meeting.equalsIgnoreCase("EB")?180:90);
			query.setParameter("ClusterId", Long.parseLong(clusterId));
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl getEBPMRCCalendarData "+e);
			return new ArrayList<Object[]>();
		}
	}

	@Override
	public CCMClosureStatus getCCMClosureStatusById(String ccmClosureId) throws Exception {
		try {
			return manager.find(CCMClosureStatus.class, Long.parseLong(ccmClosureId));
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl getCCMClosureStatusById "+e);
			return null;
		}
		
	}

	@Override
	public long addCCMClosureStatus(CCMClosureStatus  closure) throws Exception {
		try {
			manager.persist(closure);
			manager.flush();
			return closure.getCCMClosureId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl addCCMClosureStatus "+e);
			return 0;
		}
	}

	private static final String GETCLOSURESTATUSLIST = "SELECT a.CCMClosureId, a.LabCode, b.ProjectCode, b.ProjectShortName, b.ProjectName, \r\n"
			+ "(SELECT c.ProjectTypeShort FROM project_type c WHERE b.ProjectType=c.ProjectTypeId LIMIT 1) AS 'Category', b.TotalSanctionCost,\r\n"
			+ "( SELECT CONCAT(IFNULL(CONCAT(d.title,' '),(IFNULL(CONCAT(d.Salutation, ' '), ''))), d.empname,', ', e.Designation) FROM cluster_lab_employee d,employee_desig e WHERE d.EmpId=b.ProjectDirector AND d.DesigId=e.DesigId AND d.LabCode=b.LabCode LIMIT 1) AS 'PD',\r\n"
			+ "b.SanctionDate, b.PDC, a.Recommendation, a.TCRStatus, a.ACRStatus, a.ActivityStatus\r\n"
			+ "FROM pfms_ccm_closure a, project_master b WHERE a.ProjectId=b.Projectid AND a.IsActive=1 AND a.ScheduleId=:ScheduleId ORDER BY a.LabCode, a.ProjectId\r\n"
			+ "";
	@Override
	public List<Object[]> getClosureStatusList(String scheduleId) throws Exception {
		
		try {
			
			Query query = manager.createNativeQuery(GETCLOSURESTATUSLIST);
			query.setParameter("ScheduleId", Long.parseLong(scheduleId));
			return (List<Object[]>)query.getResultList();
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl getClosureStatusList "+e);
			return new ArrayList<>();
		}

	}

	@Override
	public int ccmClosureStatusDelete(String ccmClosureId) throws Exception {
		try {
			
			CCMClosureStatus ExistingCCMClosureStatus = manager.find(CCMClosureStatus.class, Long.parseLong(ccmClosureId));
			if(ExistingCCMClosureStatus != null) {
				ExistingCCMClosureStatus.setIsActive(0);
				return 1;
			}
			else {
				return 0;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl ccmClosureStatusDelete "+e);
			return 0;
		}
		
	}
	
	private static final String GETCCMASPLIST = "CALL pfms_ccm_asp_status_list(:ScheduleId)";
	@Override
	public List<Object[]> getCCMASPList(String scheduleId) throws Exception {
		
		try {
			Query query = manager.createNativeQuery(GETCCMASPLIST);
			query.setParameter("ScheduleId", Long.parseLong(scheduleId));
			return (List<Object[]>)query.getResultList();
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl getCCMASPList "+e);
			
		}
		return new ArrayList<>();
	}

	@Override
	public long addCCMASPStatus(CCMASPStatus aspStatus) throws Exception {
		try {
			manager.persist(aspStatus);
			manager.flush();
			return aspStatus.getCCMASPStatusId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl addCCMASPStatus "+e);
			return 0;
		}
	}

	private static final String CCMCASHOUTGODELETE = "DELETE FROM pfms_ccm_data WHERE LabCode=:LabCode";
	@Override
	public int ccmCashoutGoDelete(String labCode) throws Exception {
		try {
			Query query = manager.createNativeQuery(CCMCASHOUTGODELETE);
			query.setParameter("LabCode", labCode);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl ccmCashoutGoDelete "+e);
			return 0;
		}
		
	}

	@Override
	public CCMASPStatus getCCMASPStatusById(String ccmASPStatusId) throws Exception {
		try {
			return manager.find(CCMASPStatus.class, Long.parseLong(ccmASPStatusId));
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl getCCMASPStatusById "+e);
			return null;
		}
		
	}

	private static final String GETSCHEDULELISTBYTYPE = "SELECT a.ScheduleId, a.LabCode, a.CommitteeId, a.CommitteeMainId, a.MeetingId, a.ScheduleDate, a.ScheduleStartTime, a.ScheduleFlag, a.MeetingVenue,\r\n"
			+ "COUNT(c.ActionMainId) AS 'TotalActions', SUM(CASE WHEN d.ActionStatus = 'C' THEN 1 ELSE 0 END) AS 'ClosedActions', SUM(CASE WHEN d.ActionStatus <> 'C' THEN 1 ELSE 0 END) AS 'PendingActions'\r\n"
			+ "FROM committee_schedule a LEFT JOIN committee_schedules_minutes_details b ON b.ScheduleId=a.ScheduleId AND b.IDARCK='A' LEFT JOIN action_main c ON c.ScheduleMinutesId=b.ScheduleMinutesId LEFT JOIN action_assign d ON d.ActionMainId=c.ActionMainId\r\n"
			+ "WHERE a.IsActive=1 AND a.ScheduleType=:ScheduleType GROUP BY a.ScheduleId, a.MeetingId";
	@Override
	public List<Object[]> getScheduleListByScheduleTypeTwo(String scheduleType) throws Exception {
		try {
			Query query = manager.createNativeQuery(GETSCHEDULELISTBYTYPE);
			query.setParameter("ScheduleType", scheduleType);
			return (List<Object[]>)query.getResultList();
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CCMDaoImpl getScheduleListByScheduleType "+e);
			return new ArrayList<Object[]>();
		}
	}
}
