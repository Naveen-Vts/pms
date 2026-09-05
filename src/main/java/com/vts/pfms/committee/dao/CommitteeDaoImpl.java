package com.vts.pfms.committee.dao;


import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.ibm.icu.text.SimpleDateFormat;
import com.vts.pfms.committee.dto.CommitteeConstitutionApprovalDto;
import com.vts.pfms.committee.dto.CommitteeMainDto;
import com.vts.pfms.committee.dto.CommitteeScheduleDto;
import com.vts.pfms.committee.dto.MeetingCheckDto;
import com.vts.pfms.committee.model.CommitteScheduleMinutesDraft;
import com.vts.pfms.committee.model.Committee;
import com.vts.pfms.committee.model.CommitteeAircraft;
import com.vts.pfms.committee.model.CommitteeCARS;
import com.vts.pfms.committee.model.CommitteeConstitutionApproval;
import com.vts.pfms.committee.model.CommitteeConstitutionHistory;
import com.vts.pfms.committee.model.CommitteeDefaultAgenda;
import com.vts.pfms.committee.model.CommitteeDivision;
import com.vts.pfms.committee.model.CommitteeInitiation;
import com.vts.pfms.committee.model.CommitteeInvitation;
import com.vts.pfms.committee.model.CommitteeLetter;
import com.vts.pfms.committee.model.CommitteeMain;
import com.vts.pfms.committee.model.CommitteeMeetingApproval;
import com.vts.pfms.committee.model.CommitteeMeetingDPFMFrozen;
import com.vts.pfms.committee.model.CommitteeMember;
import com.vts.pfms.committee.model.CommitteeMemberRep;
import com.vts.pfms.committee.model.CommitteeMinutesAttachment;
import com.vts.pfms.committee.model.CommitteeMomAttachment;
import com.vts.pfms.committee.model.CommitteeProject;
import com.vts.pfms.committee.model.CommitteeRepresentative;
import com.vts.pfms.committee.model.CommitteeSchedule;
import com.vts.pfms.committee.model.CommitteeScheduleAgenda;
import com.vts.pfms.committee.model.CommitteeScheduleAgendaDocs;
import com.vts.pfms.committee.model.CommitteeScheduleMinutesDetails;
import com.vts.pfms.committee.model.CommitteeSchedulesMomDraftRemarks;
import com.vts.pfms.committee.model.CommitteeSubSchedule;
import com.vts.pfms.committee.model.CommitteeSubSystem;
import com.vts.pfms.committee.model.PfmsNotification;
import com.vts.pfms.committee.model.PmsEnote;
import com.vts.pfms.committee.model.PmsEnoteTransaction;
import com.vts.pfms.committee.model.ProgrammeMaster;
import com.vts.pfms.committee.model.ProgrammeProjects;
import com.vts.pfms.milestone.model.FileRepUploadPreProject;
import com.vts.pfms.model.LabMaster;
import com.vts.pfms.print.model.CommitteeProjectBriefingFrozen;
import com.vts.pfms.print.model.MinutesFinanceList;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Transactional
@Repository
public class CommitteeDaoImpl  implements CommitteeDao {

	
	private static final Logger logger=LogManager.getLogger(CommitteeDaoImpl.class);
	
	private static final String EMPLOYEELIST="SELECT a.emp_id, CONCAT(IFNULL(CONCAT(a.title,' '),(IFNULL(CONCAT(a.salutation, ' '), ''))), a.emp_name) AS 'EmpName',b.Designation,a.emp_no  FROM employee a,employee_desig b WHERE a.is_active='1' AND a.desig_id=b.desig_id AND a.lab_code=:labcode AND a.emp_status IN ('P','R','T') ORDER BY a.sr_no=0,a.sr_no";
	private static final String LASTCOMMITTEEID="SELECT committeemainid FROM committee_main WHERE isactive=1 and committeeid=:committeeid AND projectid=:projectid and divisionid=:divisionid AND InitiationId=:initiationid AND CARSInitiationId=:CARSInitiationId AND ProgrammeId=:ProgrammeId";
	private static final String COMMITTEENAME="SELECT committeeid,committeename,committeeshortname,projectapplicable,periodicduration,isglobal FROM committee WHERE  committeeid=:committeeid";
	private static final String COMMITTEENAMESCHECK="SELECT SUM(IF(CommitteeShortName =:committeeshortname,1,0))   AS 'shortname',SUM(IF(CommitteeName = :committeename,1,0)) AS 'name'FROM committee where isactive=1 AND labcode=:labcode ";
	private static final String COMMITTEELISTALL="SELECT committeeid,committeeshortname,committeename,CommitteeType,projectapplicable,isactive FROM committee";
	private static final String COMMITTEELISTACTIVE="SELECT committeeid,committeeshortname,committeename,CommitteeType,projectapplicable,isactive,periodicnon,periodicduration,TechNonTech,Guidelines,Description,TermsOfReference,isglobal FROM committee WHERE isactive=1 AND isglobal=:isglobal AND projectapplicable=:projectapplicable  AND labcode=:labcode ;";
	private static final String COMMITTEEDETAILS="SELECT committeeid,committeeshortname,committeename,CommitteeType,projectapplicable,technontech,guidelines,periodicnon,periodicduration,isactive,Description,TermsOfReference,isglobal,labcode,IsBriefing FROM committee WHERE isactive=1 AND (CASE WHEN 'A'=:committeeid THEN committeeid=committeeid ELSE committeeid=:committeeid END)"; //added referenceNo
	private static final String COMMITTEEMAINLIST="SELECT a.committeemainid, a.committeeid,a.validfrom,a.validto, b.committeename,b.committeeshortname FROM committee_main a, committee b WHERE b.projectapplicable='N' AND a.isactive='1' AND a.committeeid=b.committeeid  AND a.divisionid=0 AND a.projectid=0 AND a.initiationid=0 AND TRIM(b.labcode)=:labcode" ;
	private static final String COMMITTEESCHEDULELIST="SELECT cs.scheduleid, cs.committeeid,cs.committeemainid,cs.scheduledate,cs.schedulestarttime,cs.projectid , GetCommitteeShortName(cs.scheduledate,c.committeeshortname) AS committeeshortname FROM committee_schedule cs,committee c WHERE cs.committeeid=c.committeeid AND  cs.divisionid=0 AND cs.committeeid=:committeeid AND cs.projectid=0 AND cs.divisionid=0 AND cs.initiationid=0 AND cs.isactive=1 ";
	private static final String COMMITTEESCHEDULEEDITDATA="SELECT a.committeeid,a.committeemainid,a.scheduledate,a.schedulestarttime,a.scheduleflag,a.schedulesub,a.scheduleid,b.committeename,b.committeeshortname,a.projectid,c.meetingstatusid,a.meetingid,a.meetingvenue,a.confidential,a.Reference,(SELECT d.classification FROM pfms_security_classification d WHERE a.confidential=d.classification_id) AS 'classification',a.divisionid  ,a.initiationid ,a.pmrcdecisions,a.kickoffotp ,(SELECT minutesattachmentid FROM committee_minutes_attachment WHERE scheduleid=a.scheduleid) AS 'attachid', b.periodicNon,a.MinutesFrozen,a.briefingpaperfrozen,a.labcode, a.CARSInitiationId, a.ProgrammeId FROM committee_schedule a,committee b ,committee_meeting_status c WHERE a.scheduleflag=c.MeetingStatus AND a.scheduleid=:committeescheduleid AND a.committeeid=b.committeeid";
	private static final String PROJECTLIST="SELECT project_id,project_main_id,project_code,project_name,project_short_name FROM project_master WHERE is_active=1 and lab_code=:labcode";
	//private static final String AGENDALIST = "SELECT a.scheduleagendaid,a.scheduleid,a.schedulesubid,a.agendaitem,b.projectname,b.projectid,a.remarks,b.projectcode,a.agendapriority,a.presenterid ,CONCAT(IFNULL(CONCAT(j.title,' '),''), j.empname) as 'empname' ,h.designation,a.duration,j.desigid, a.PresentorLabCode  FROM project_master b,employee j,employee_desig h,committee_schedules_agenda a  WHERE a.projectid=b.projectid AND a.scheduleid=:committeescheduleid AND a.isactive=1 AND a.projectid<>0 AND a.presenterid=j.empid AND j.desigid=h.desigid  UNION   SELECT a.scheduleagendaid,a.scheduleid,a.schedulesubid,a.agendaitem,cs.labcode AS 'projectname' , '0' AS projectid,a.remarks,'' AS projectcode,a.agendapriority,a.presenterid ,CONCAT(IFNULL(CONCAT(j.title,' '),''), j.empname) as 'empname' ,h.designation,a.duration,j.desigid, a.PresentorLabCode  FROM employee j,employee_desig h, committee_schedules_agenda a, committee_schedule cs   WHERE a.scheduleid=:committeescheduleid AND a.scheduleid=cs.scheduleid  AND a.isactive=1 AND a.projectid=0 AND a.presenterid=j.empid AND j.desigid=h.desigid ORDER BY 9   ";
	private static final String COMMITTEESPECLIST="SELECT a.minutesid,a.scheduleminutesid,a.schedulesubid,a.minutessubid,a.minutessubofsubid,a.details,a.scheduleid,a.idarck,b.outcomename, a.agendasubhead FROM committee_schedules_minutes_details a,committee_schedules_minutes_outcome b WHERE a.idarck=b.idarck and a.scheduleid=:scheduleid ORDER BY scheduleminutesid;";
	private static final String COMMITTEEMINUTESPEC="SELECT a.minutesid,a.description,b.agendasubid,b.subdescription,CONCAT(IFNULL(c.agendaitem, ''),CASE WHEN c.GroupName IS NOT NULL AND c.GroupName <> '' THEN CONCAT(' (', c.GroupName, ')') ELSE '' END) AS agendaItem FROM committee_schedules_minutes a, committee_schedules_minutes_sub b,committee_schedules_agenda c WHERE minutesid=:minutesid AND agendasubid=:agendasubid AND scheduleagendaid=:scheduleagendaid  ";
	private static final String COMMITTEEMINUTEEDIT="SELECT a.minutesid,a.details,a.scheduleid,a.scheduleminutesid,b.description,a.minutessubofsubid,a.minutessubid,c.subdescription,CONCAT(IFNULL(d.agendaitem, ''),CASE WHEN d.GroupName IS NOT NULL AND d.GroupName <> '' THEN CONCAT(' (', d.GroupName, ')') ELSE '' END) AS agendaItem,a.remarks,a.idarck  FROM committee_schedules_minutes_details a, committee_schedules_minutes b, committee_schedules_minutes_sub c,committee_schedules_agenda d WHERE a.minutesid=b.minutesid AND a.minutessubofsubid=c.agendasubid AND a.scheduleminutesid=:scheduleminutesid AND a.minutessubid=d.scheduleagendaid ";
	private static final String COMMITTEEMINUTEEDITFORADE="SELECT a.minutesid,a.details,a.scheduleid,a.scheduleminutesid,b.description,a.minutessubofsubid,a.minutessubid,c.subdescription,d.agendaitem,a.remarks,a.idarck,a.AircraftId,a.SubSystemId  FROM committee_schedules_minutes_details a, committee_schedules_minutes b, committee_schedules_minutes_sub c,committee_schedules_agenda d WHERE a.minutesid=b.minutesid AND a.minutessubofsubid=c.agendasubid AND a.scheduleminutesid=:scheduleminutesid AND a.minutessubid=d.scheduleagendaid ";
	private static final String COMMITTEESCHEDULEAGENDAPRIORITY="SELECT ScheduleAgendaId, ScheduleId,AgendaPriority FROM committee_schedules_agenda WHERE ScheduleId=:scheduleid ORDER BY AgendaPriority DESC";
	private static final String COMMITTEESCHEDULEGETAGENDASAFTER ="SELECT ScheduleAgendaId,AgendaPriority FROM committee_schedules_agenda WHERE ScheduleId=:scheduleid AND AgendaPriority>:AgendaPriority ORDER BY AgendaPriority ASC";
	private static final String COMMITTEESUBSCHEDULELIST="SELECT ScheduleSubId,ScheduleId,ScheduleDate,ScheduleStartTime,ScheduleFlag,IsActive FROM committee_schedule_sub WHERE ScheduleId=:scheduleid"; 
	private static final String COMMITTEEMINUTESSUB="SELECT * FROM  committee_schedules_minutes_sub WHERE  AgendaSubId >1";
	private static final String COMMITTEEMINUTESSPECDETAILS="SELECT * FROM committee_schedules_minutes";
	private static final String COMMITTEEATTENDANCE="SELECT a.empid,a.committeeinvitationid,a.committeescheduleid,a.membertype,a.attendance,b.emp_no,b.emp_name,c.designation FROM committee_schedules_invitation a,employee b,employee_desig c WHERE b.IsActive='1' AND a.empid = b.emp_id AND b.desig_id=c.desig_id AND a.committeescheduleid =:scheduleid AND a.membertype IN ('C','I' ) UNION SELECT a.empid,a.committeeinvitationid,a.committeescheduleid,a.membertype,a.attendance,b.expertno,b.expertname,c.designation FROM committee_schedules_invitation a,expert b,employee_desig c WHERE a.empid = b.expertid AND b.desigid=c.desig_id AND a.committeescheduleid =:scheduleid AND a.membertype = 'E' ORDER BY 4";
	private static final String COMMITTEESCHEDULEDATA = "SELECT a.ScheduleId, a.CommitteeMainId, a.ScheduleDate, a.ScheduleStartTime, a.ScheduleFlag, a.ScheduleSub, a.IsActive, a.committeeid ,GetCommitteeShortName(a.scheduledate,b.committeeshortname) AS committeeshortname, GetCommitteeName(a.scheduledate,b.committeeshortname,b.committeename) AS committeename, c.MeetingStatusId,a.projectid,a.meetingid, a.divisionid ,a.initiationid,a.labcode,a.CARSInitiationId, a.ProgrammeId FROM committee_schedule a,committee b,committee_meeting_status c WHERE a.committeeid=b.committeeid AND a.scheduleflag=c.MeetingStatus AND a.ScheduleId=:committeescheduleid";
	private static final String COMMITTEEATTENDANCETYPE="SELECT attendance from committee_schedules_invitation WHERE CommitteeInvitationId=:invitationid";
	private static final String COMMITTEEINVITATIONDELETE ="DELETE FROM committee_schedules_invitation WHERE CommitteeInvitationId = :committeeinvitationid";
	private static final String EXPERTLIST="SELECT a.expertid,a.expertname,b.designation FROM expert a,employee_desig b WHERE a.isactive='1' AND a.DesigId=b.DesigId";
	private static final String MINUTESUNITLIST="SELECT a.unitname,a.minutesspecunitid,b.minutesid,b.minutessubid,b.minutessubofsubid FROM committee_schedules_minutes_unit a,committee_schedules_minutes_details b WHERE a.minutesspecunitid= b.minutesunitid AND b.scheduleid=:committeescheduleid AND b.statusflag='I'";	
	private static final String COMMITTEEAGENDAPRESENTER="SELECT a.presenterid ,b.emp_name, c.designation FROM committee_schedules_agenda a, employee b, employee_desig c WHERE b.IsActive='1'AND a.presenterid=b.emp_id AND b.desig_id=c.desig_id AND a.scheduleid=:scheduleid GROUP BY 1";
	private static final String CHAIRPERSONEMAIL="SELECT email, emp_id FROM employee WHERE emp_id IN (SELECT empid FROM committee_member WHERE membertype IN ('CC','CS','PS','CH') AND committeemainid=:committeemainid AND labcode IN (SELECT lab_code FROM lab_master))  UNION SELECT email, empid FROM employee_external WHERE empid IN (SELECT empid FROM committee_member WHERE membertype IN ('CC','CS','PS','CH') AND committeemainid=:committeemainid AND labcode NOT IN (SELECT lab_code FROM lab_master)) UNION SELECT email, emp_id FROM employee WHERE emp_id IN (SELECT pm.project_director FROM project_master pm, committee_main cm WHERE cm.projectid=pm.project_id AND cm.committeemainid=:committeemainid ) ";
	private static final String PROJECTDIRECTOREMAIL="SELECT d.email,d.emp_name FROM employee d,project_master e WHERE d.isActive='1' AND e.project_id=:projectid AND e.project_director=d.emp_id";
	private static final String RTMDDOEMAIL="SELECT a.email,a.emp_name FROM employee a,pfms_initiation_approver b WHERE a.isActive='1' AND a.emp_id=b.empid AND b.isactive=1 AND b.type='DO-RTMD' ";
	private static final String KICKOFFOTP="SELECT kickoffotp FROM committee_schedule WHERE scheduleid=:scheduleid";
	private static final String PROJECTDETAILS="SELECT project_id, project_name, project_description,project_main_id,project_code,project_type,project_imms_cd, unit_code, sanction_no,pdc,project_category FROM project_master WHERE project_id=:projectid";
	private static final String PROJECTSCHEDULELISTALL ="SELECT cs.scheduleid, cs.committeeid,cs.committeemainid,cs.scheduledate,cs.schedulestarttime,cs.projectid , GetCommitteeShortName(cs.scheduledate,c.committeeshortname) AS committeeshortname,cs.scheduleflag FROM committee_schedule cs,committee c WHERE cs.committeeid=c.committeeid AND cs.isactive=1 AND cs.projectid=:projectid ORDER BY cs.scheduledate DESC";
	private static final String PROJECTAPPLICABLECOMMITTEELIST="SELECT  b.committeeid,a.projectid, a.autoschedule,b.committeeshortname,b.committeename,b.projectapplicable FROM committee_project a,committee b WHERE a.committeeid=b.committeeid AND b.projectapplicable='P' AND a.projectid=:projectid";
	private static final String PROJECTCOMMITTEESCHEDULELISTALL ="SELECT cs.scheduleid, cs.committeeid,cs.committeemainid,cs.scheduledate,cs.schedulestarttime,cs.projectid , GetCommitteeShortName(cs.scheduledate,c.committeeshortname) AS committeeshortname,cs.scheduleflag FROM committee_schedule cs,committee c WHERE cs.committeeid=c.committeeid AND cs.projectid=:projectid AND cs.CommitteeId=:committeeid AND cs.isactive=1 ORDER BY cs.scheduledate DESC";
	private static final String AGENDARETURNDATA="SELECT remarks,empid,meetingstatus FROM committee_meeting_approval WHERE meetingstatus IN ('MAR','MMR') AND scheduleid=:scheduleid ";
	private static final String LABDETAILS = "SELECT lab_master_id, lab_code, lab_name, lab_unit_code, lab_address, lab_city, lab_pin, lab_tel_no, lab_fax_no, lab_email, lab_authority, lab_authority_id, lab_rfp_email, lab_id, cluster_id, lab_logo FROM lab_master WHERE lab_code=:labcode ;";
	private static final String COMMITTEESCHEDULEDATAPRO = "SELECT a.ScheduleId, a.CommitteeMainId, a.ScheduleDate, a.ScheduleStartTime, a.ScheduleFlag, a.ScheduleSub, a.IsActive, a.committeeid ,GetCommitteeShortName(a.scheduledate,b.committeeshortname) AS committeeshortname, GetCommitteeName(a.scheduledate,b.committeeshortname,b.committeename) AS committeename, c.MeetingStatusId FROM committee_schedule a,committee b,committee_meeting_status c WHERE a.committeeid=b.committeeid AND a.scheduleflag=c.MeetingStatus AND a.ScheduleId=:committeescheduleid AND a.projectid=:projectid ";
	private static final String PROJECTMASTERLIST="SELECT b.committeename,b.committeeshortname,b.committeeid,a.committeeprojectid,b.periodicnon,b.periodicduration,a.autoschedule  FROM committee_project a,committee b WHERE a.committeeid=b.committeeid AND a.projectid=:projectid";
	private static final String COMMITTEEPROJECTDELETE="DELETE FROM committee_project WHERE committeeprojectid=:committeeprojectid";
	private static final String COMMITTEENONPROJECTLIST="SELECT committeeid,CommitteeShortName,CommitteeName,CommitteeType,ProjectApplicable,TechNonTech,Guidelines,PeriodicNon,PeriodicDuration,Description,TermsOfReference FROM committee  WHERE projectapplicable='N' AND isactive='1'";
	private static final String COMMITTEEAUTOSCHEDULELIST="SELECT a.scheduledate,a.schedulestarttime,GetCommitteeName(a.scheduledate,b.committeeshortname,b.committeename) AS committeename,GetCommitteeShortName(a.scheduledate,b.committeeshortname) AS committeeshortname,a.scheduleid,a.scheduleflag,c.statusdetail,c.meetingstatusid FROM committee_schedule a,committee b,committee_meeting_status c WHERE a.committeeid=b.committeeid AND a.projectid=:projectid AND a.divisionid=:divisionid AND a.initiationid=:initiationid AND a.scheduleflag=c.meetingstatus AND a.isactive=1 AND (CASE WHEN 'B'=:projectstatus THEN c.meetingstatusid >= 6 WHEN 'C'=:projectstatus THEN c.meetingstatusid<=5 ELSE 1=1 END) GROUP BY a.ScheduleId ORDER BY a.scheduledate DESC";
	private static final String COMMITTEEPROJECTUPDATE="UPDATE committee_project SET autoschedule='Y' WHERE projectid=:projectid AND committeeid=:committeeid";
	private static final String COMMITTEEAUTOSCHEDULELIST1="SELECT a.scheduledate,a.schedulestarttime,GetCommitteeName(a.scheduledate,b.committeeshortname,b.committeename) AS committeename,GetCommitteeShortName(a.scheduledate,b.committeeshortname) AS committeeshortname,a.scheduleid,a.scheduleflag,c.statusdetail,c.meetingstatusid FROM committee_schedule a,committee b,committee_meeting_status c WHERE a.committeeid=b.committeeid AND a.projectid=:projectid AND (CASE WHEN 'A'=:committeeid THEN a.committeeid=b.committeeid ELSE a.committeeid=:committeeid END) AND a.divisionid=:divisionid AND a.initiationid=:initiationid AND a.scheduleflag=c.meetingstatus AND a.isactive=1 AND (CASE WHEN 'B'=:projectstatus THEN c.meetingstatusid >= 6 WHEN 'C'=:projectstatus THEN c.meetingstatusid<=5 ELSE 1=1 END)  ORDER BY a.scheduledate DESC ";
	private static final String COMMITTEMAINMEMBERSDATA="SELECT cm.empid,cm.committeemainid, GetCommitteeShortName(cs.scheduledate,c.committeeshortname) AS committeeshortname FROM committee_member cm,committee c, committee_main cma, committee_schedule cs, employee e WHERE e.is_active='1' AND cm.membertype =:membertype AND cs.committeemainid=cma.committeemainid AND cma.committeeid=c.committeeid AND cma.committeemainid=cm.committeemainid AND cs.scheduleid=:scheduleid AND cm.empid=e.emp_id AND cm.labcode IN (SELECT lab_code FROM lab_master)";
	private static final String NOTIFICATIONDATA="SELECT a.empid, a.notificationby FROM pfms_notification a WHERE scheduleid=:scheduleid AND empid=:empid AND a.status=:status";	
	private static final String MEETINGCOUNT="SELECT COUNT(*) FROM committee_schedule WHERE YEAR(scheduledate)= :scheduledate AND projectid=:projectid AND isactive=1 ";
	private static final String MEETINGCOUNT1="SELECT COUNT(*) FROM committee_schedule WHERE projectid=:projectid AND isactive=1 ";
	private static final String MINUTESATTACHMENTDELETE="DELETE FROM committee_minutes_attachment WHERE MinutesAttachmentId=:attachid";
	private static final String MINUTESATTACHMENTLIST="SELECT MinutesAttachmentId,ScheduleId,AttachmentName FROM committee_minutes_attachment WHERE ScheduleId=:scheduleid";
	private static final String PROJECTCATEGORYLIST="select classification_id,classification from pfms_security_classification";
	private static final String COMMITTEEALLATTENDANCE="SELECT a.empid,a.committeeinvitationid,a.committeescheduleid,a.membertype,a.attendance,b.empno,b.empname,c.designation,b.email,'organization' FROM committee_schedules_invitation a,employee b,employee_desig c WHERE b.isActive='1' AND a.empid = b.empid AND b.desigid=c.desigid AND a.committeescheduleid =:scheduleid AND a.membertype IN ('CC','CS','C','I' ) UNION SELECT a.empid,a.committeeinvitationid,a.committeescheduleid,a.membertype,a.attendance,b.expertno,b.expertname,c.designation,b.email,b.organization FROM committee_schedules_invitation a,expert b,employee_desig c WHERE a.empid = b.expertid AND b.desigid=c.desigid AND a.committeescheduleid =:scheduleid AND a.membertype = 'E' ORDER BY FIELD(4,'CC','CS','C','I' ,'E')ASC";
	private static final String MEETINGREPORTTOTAL="SELECT a.meetingid,a.scheduledate,a.schedulestarttime,a.projectid,GetCommitteeName(a.scheduledate,b.committeeshortname,b.committeename) AS committeename,a.meetingvenue,a.scheduleid FROM committee_schedule a,committee b WHERE a.scheduledate BETWEEN :fdate AND :tdate AND (CASE WHEN 'A'=:ProjectId THEN 1=1 ELSE a.projectid=:ProjectId END) AND a.committeeid=b.committeeid AND a.isactive=1 ";
	private static final String PROJECTCOMMITTEESLISTNOTADDED="SELECT a.committeeid,a.committeeshortname,a.committeename,a.CommitteeType,a.projectapplicable,a.isactive,a.periodicnon,a.periodicduration,a.TechNonTech,a.Guidelines,a.Description,a.TermsOfReference FROM committee a WHERE a.projectapplicable='P' AND isglobal IN (0,:projectid)  AND a.committeeid NOT IN ( SELECT b.CommitteeId FROM committee_project b WHERE b.projectId = :projectid) AND a.labcode=:LabCode  ORDER BY a.committeeid,a.committeeshortname";
	private static final String PROJECTCOMMITTEESLIST="SELECT committeeid,committeeshortname,committeename,CommitteeType,projectapplicable,isactive,periodicnon,periodicduration,TechNonTech,Guidelines,Description,TermsOfReference FROM committee WHERE projectapplicable='P' AND LabCode=:LabCode AND isactive=1";
	private static final String MINUTESVIEWALLACTIONLIST="CALL Pfms_Meeting_Action_List(:scheduleid)";
	private static final String MEETINGSEARCHLIST="SELECT '0' committeemainid, 0 AS empid,a.scheduleid,a.scheduledate,a.schedulestarttime,a.scheduleflag,'NA' AS membertype ,a.meetingid, GetCommitteeName(a.scheduledate,b.committeeshortname,b.committeename) AS committeename, GetCommitteeShortName(a.scheduledate,b.committeeshortname) AS committeeshortname, a.meetingvenue FROM committee_schedule a,committee b WHERE a.committeeid=b.committeeid AND a.meetingid LIKE :meetingid AND a.isactive=1 and a.labcode=:labcode";
	private static final String CLUSTERLABLIST="SELECT lab_id,cluster_id,lab_name,lab_code FROM cluster_lab";
	private static final String EXTERNALMEMBERSNOTADDEDCOMMITTEE="SELECT a.expertid,a.expertname,b.designation  FROM expert a,employee_desig b WHERE a.isactive='1' AND a.desigid=b.desig_id AND a.expertid NOT IN (SELECT  empid FROM committee_member  WHERE isactive=1 AND labcode='@EXP' AND committeemainid=:committeemainid);";
	private static final String EXTERNALEMPLOYEELISTFORMATION="SELECT a.empid, a.empname,a.empno,b.designation FROM employee_external a,employee_desig b WHERE a.labid>0 AND a.labid=:labid AND a.desigid=b.desigid AND a.empid NOT IN (SELECT  empid FROM committee_member   WHERE isactive=1  AND labid=:labid AND committeemainid=:committeemainid)   ";
	private static final String EXTERNALEMPLOYEELISTINVITATIONS ="SELECT a.emp_id, a.emp_name,a.emp_no,b.designation, a.desig_id  FROM employee a,employee_desig b   WHERE a.is_active='1' AND lab_code=:labcode AND a.desig_id=b.desig_id AND a.emp_id NOT IN (SELECT empid  FROM committee_schedules_invitation WHERE  committeescheduleid=:scheduleid AND labcode=:labcode)  ";
	private static final String EMPLOYEELISTNOINVITEDMEMBERS="SELECT a.emp_id, CONCAT(IFNULL(CONCAT(a.title,' '),''), a.emp_name) as 'empname' ,b.designation,a.desig_id, a.lab_code FROM employee a,employee_desig b WHERE a.is_active='1' AND a.emp_status IN ('P','R','T') AND a.lab_code = :LabCode AND a.desig_id=b.desig_id AND a.emp_id NOT IN ( SELECT c.empid FROM committee_schedules_invitation c WHERE c.committeescheduleid=:scheduleid AND c.labcode=:LabCode ) ORDER BY a.sr_no=0,a.sr_no";
	private static final String EXPERTLISTNOINVITEDMEMBERS = "SELECT a.expertid,CONCAT(IFNULL(CONCAT(a.title,' '),''),a.expertname) as 'expertname'  ,b.designation,a.desigid FROM expert a,employee_desig b WHERE a.isactive='1' AND a.desigId=b.desig_id AND a.expertid NOT IN( SELECT empid FROM committee_schedules_invitation WHERE committeescheduleid=:scheduleid AND labcode='@EXP'  ) ORDER BY a.expertname ";
	private static final String ALLPROJECTDETAILSLIST ="SELECT a.project_id,a.project_code,a.project_name,a.project_main_id,a.project_description,a.unit_code,a.project_type,a.project_category,a.sanction_no,a.sanction_date,a.pdc,a.project_director, a.project_short_name FROM project_master a WHERE a.is_active=1 ";
	private static final String PROJECTCOMMITTEEDESCRIPTIONTOR="SELECT committeeprojectid,description , termsofreference, committeeid , projectid  FROM committee_project WHERE committeeid=:committeeid AND projectid=:projectid";
	private static final String PROJECTCOMMITTEEFORMATIONCHECKLIST="SELECT a.committeeprojectid,b.committeemainid FROM committee_project a LEFT JOIN committee_main b ON a.projectid = b.projectid AND a.committeeid = b.committeeid AND b.isactive=1 WHERE a.projectid = :projectid";
	private static final String UPDATECOMMITTEEINVITATIONEMAILSENT="UPDATE committee_schedules_invitation SET emailsent ='Y' WHERE membertype NOT IN ('CW','W','E','CO') AND committeescheduleid=:committeescheduleid";
	private static final String UPDATEUNIT="UPDATE committee_schedules_minutes_unit SET unitname=:unitname,createdby=:createdby,createddate=:createddate WHERE minutesspecunitid=:unitid";
	private static final String DIVISIONCOMMITTEEDESCRIPTIONTOR="SELECT committeedivisionid,description , termsofreference, committeeid , divisionid  FROM committee_division WHERE committeeid=:committeeid AND divisionid=:divisionid";
	private static final String DIVISIONLIST = "SELECT division_id, division_code,division_name,division_head_id,group_id FROM division_master";
	private static final String COMMITTEEDIVISIONASSIGNED = "SELECT b.committeename,b.committeeshortname,b.committeeid,a.committeedivisionid,b.periodicnon,b.periodicduration,a.autoschedule  FROM committee_division a,committee b WHERE a.committeeid=b.committeeid AND a.divisionid=:divisionid";
	private static final String COMMITTEEDIVISIONNOTASSIGNED = "SELECT a.committeeid,a.committeeshortname,a.committeename,a.CommitteeType,a.projectapplicable,a.isactive,a.periodicnon,a.periodicduration,a.TechNonTech,a.Guidelines,a.Description,a.TermsOfReference  FROM committee a WHERE a.projectapplicable='N' AND a.committeeid NOT IN ( SELECT b.CommitteeId FROM committee_division b WHERE b.divisionid = :divisionid ) AND a.labcode=:LabCode  ORDER BY a.committeeid,a.committeeshortname ";
	private static final String DIVISIONCOMMITTEEFORMATIONCHECKLIST = "SELECT a.committeedivisionid,b.committeemainid FROM committee_division a LEFT JOIN  committee_main b ON a.Divisionid = b.Divisionid AND b.divisionid>0 AND a.committeeid = b.committeeid AND b.isactive=1 WHERE a.Divisionid =:divisionid";
	private static final String DIVISIONCOMMITTEEDELETE="DELETE FROM committee_division WHERE committeedivisionid=:committeedivisionid";
	private static final String COMMITTEEACTIONDATA="CALL Pfms_Action_List(:scheduleid)";
	private static final String OUTCOMELIST="select idarck,outcomename from committee_schedules_minutes_outcome";
	private static final String INVITATIONMAXSERIALNO ="SELECT 'MaxSlNo', IFNULL(MAX(serialno),0) AS 'MAXserialno' FROM committee_schedules_invitation WHERE committeescheduleid=:scheduleid ";
	private static final String LOGINDIVISIONLIST ="CALL Pfms_Emp_DivisiontList(:empid);";
	private static final String COMMITTEEINVITATIONSERIALNOAFTER="SELECT committeeInvitationid, serialno FROM committee_schedules_invitation WHERE serialno> (SELECT serialno FROM committee_schedules_invitation WHERE committeeInvitationid=:committeeinvitationid ) AND committeescheduleid=(SELECT committeescheduleid FROM committee_schedules_invitation WHERE committeeInvitationid=:committeeinvitationid )  ";
	private static final String COMMITTEELASTSCHEDULEDATE="SELECT scheduledate,scheduleid FROM committee_schedule WHERE committeeid = :committeeid AND isactive = 1 ORDER BY scheduledate DESC LIMIT 1;";
	private static final String INTERNALEMPLOYEELISTFORMATION = "SELECT a.emp_id, a.emp_name,a.emp_no,b.designation FROM employee a,employee_desig b WHERE a.desig_id=b.desig_id AND lab_code=:labcode AND a.emp_id NOT IN (SELECT  empid FROM committee_member   WHERE isactive=1  AND labcode=:labcode AND committeemainid=:committeemainid) AND a.emp_status IN ('P','R','T')  ORDER BY a.sr_no=0,a.sr_no ";
	private static final String DIVISIONDATA = "SELECT division_id, division_code,division_name,division_head_id,group_id FROM division_master WHERE division_id=:divisionid";
	private static final String DIVISIONCOMMITTEEMAINLIST ="SELECT  b.committeeid,a.Divisionid, a.autoschedule,b.committeeshortname,b.committeename FROM committee_division a,committee b WHERE a.committeeid=b.committeeid AND b.projectapplicable='N' AND a.Divisionid = :divisionid";
	private static final String DIVISIONCOMMITTEESCHEDULELIST = "SELECT cs.scheduleid, cs.committeeid,cs.committeemainid,cs.scheduledate,cs.schedulestarttime,cs.projectid , c.committeeshortname FROM committee_schedule cs,committee c WHERE cs.committeeid=c.committeeid AND  cs.divisionid=:divisionid AND cs.CommitteeId=:committeeid AND cs.isactive=1 ";
	private static final String INITIATIONMASTERLIST="SELECT b.committeename,b.committeeshortname,b.committeeid,a.CommitteeInitiationId,b.periodicnon,b.periodicduration,a.autoschedule  FROM committee_initiation a,committee b WHERE a.committeeid=b.committeeid AND a.initiationid=:initiationid";
	private static final String INITIATIONCOMMITTEEFORMATIONCHECKLIST="SELECT a.CommitteeInitiationId,b.committeemainid FROM committee_initiation a LEFT JOIN committee_main b ON a.initiationid = b.initiationid AND a.committeeid = b.committeeid AND b.isactive=1 WHERE a.initiationid =:initiationid";
	private static final String INITIATIONCOMMITTEESLISTNOTADDED="SELECT a.committeeid,a.committeeshortname,a.committeename,a.CommitteeType,a.projectapplicable,a.isactive,a.periodicnon,a.periodicduration,a.TechNonTech,a.Guidelines,a.Description,a.TermsOfReference FROM committee a WHERE a.projectapplicable='P'  AND isglobal=0 AND a.committeeid NOT IN ( SELECT b.CommitteeId FROM committee_initiation b WHERE b.initiationid = :initiationid) AND a.labcode=:LabCode  ORDER BY a.committeeid,a.committeeshortname";
	private static final String INVITATIONSERIALNOUPDATE="UPDATE committee_schedules_invitation SET SerialNo=:newslno  WHERE CommitteeInvitationId=:invitationid";
	private static final String INITIATEDPROJECTDETAILSLIST="SELECT a.initiationid, a.projecttitle, a.divisionid, a.classificationid, a.projectshortname, a.projecttypeid FROM pfms_initiation a";
	private static final String COMMITTEEDIVISIONUPDATE="UPDATE committee_division SET autoschedule='Y' WHERE divisionid=:divisionid AND committeeid=:committeeid";
	private static final String DIVCOMMITTEEAUTOSCHEDULELIST="SELECT a.scheduledate,a.schedulestarttime,GetCommitteeName(a.scheduledate,b.committeeshortname,b.committeename) AS committeename,GetCommitteeShortName(a.scheduledate,b.committeeshortname) AS committeeshortname,a.scheduleid  FROM committee_schedule a,committee b  WHERE a.committeeid=b.committeeid AND a.divisionid=:divisionid AND a.isactive=1 ORDER BY a.scheduledate ";
	private static final String DIVISIONMASTERLIST="SELECT b.committeename,b.committeeshortname,b.committeeid,a.committeedivisionid,b.periodicnon,b.periodicduration,a.autoschedule FROM committee_division a,committee b  WHERE a.committeeid=b.committeeid AND a.divisionid=:divisionid";
	private static final String DIVISIONSCHEDULELISTALL = "SELECT cs.scheduleid, cs.committeeid,cs.committeemainid,cs.scheduledate,cs.schedulestarttime,cs.projectid , c.committeeshortname FROM committee_schedule cs,committee c WHERE cs.committeeid=c.committeeid AND cs.initiationid=0 AND cs.projectid=0 AND cs.divisionid=:divisionid AND cs.isactive=1 ";
	private static final String CHAIRPERSONEMPLOYEELISTFORMATION="SELECT a.emp_id, CONCAT(IFNULL(CONCAT(a.title,' '),''), a.emp_name) as 'emp' ,a.emp_no,b.designation FROM employee a,employee_desig b WHERE a.is_active=1 AND a.emp_status IN ('P','R','T') AND a.desig_id=b.desig_id AND a.lab_code=:labcode AND a.emp_id NOT IN (SELECT  empid FROM committee_member WHERE isactive=1  AND labcode IN (SELECT labcode FROM lab_master) AND committeemainid=:committeemainid AND membertype NOT IN ('CC','CS','PS','CH')) ORDER BY a.sr_no=0,a.sr_no;  ";
//	private static final String CHAIRPERSONEXTERNALEMPLOYEELIST = "SELECT a.empid, a.empname,a.empno,b.designation FROM employee_external a,employee_desig b WHERE a.isactive=1 AND a.labid=:labid AND a.desigid=b.desigid AND a.empid NOT IN (SELECT  empid FROM committee_member  WHERE isactive=1  AND labid=:labid AND committeemainid=:committeemainid AND membertype NOT IN ('CC','CS','PS','CH'))  ";
	private static final String COMMITTEEMEMBERREPDELETE ="DELETE FROM committee_member_rep WHERE memberrepid=:memberrepid";
	private static final String COMMITTEEREPNOTADDEDLIST="SELECT repid, repcode , repname FROM committee_rep WHERE repid NOT IN (SELECT repid FROM committee_member_rep WHERE committeemainid=:committeemainid)"; 
	private static final String COMMITTEEMEMBERREPLIST="SELECT cmr.memberrepid,cmr.repid, cr.repcode, cr.repname FROM committee_rep cr, committee_member_rep cmr WHERE cmr.repid=cr.repid AND cmr.committeemainid=:committeemainid"; 
	private static final String COMMITTREPLIST="SELECT repid, repcode , repname FROM committee_rep"; 
	private static final String EMPLOYEELISTWITHOUTMEMBERS="SELECT a.emp_id,a.emp_name,b.designation FROM employee a,employee_desig b WHERE a.is_active='1' AND a.desig_id=b.desig_id AND a.emp_id NOT IN (SELECT empid FROM committee_member WHERE labcode=(SELECT lab_code FROM lab_master WHERE lab_code=:labcode) AND committeemainid=:committeemainid AND isactive=1) AND lab_code=:labcode AND a.emp_status IN ('P','R','T')  ORDER BY a.sr_no=0,a.sr_no;";
	private static final String COMMITTEMAINDATA ="SELECT cm.committeemainid, cm.committeeid,cm.projectid, cm.divisionid,cm.initiationid, cm.validfrom, cm.validto, cm.isactive,c.committeeshortname,cm.status, c.labcode,cm.referenceno,cm.formationdate,cm.CARSInitiationId, cm.ProgrammeId,cm.preapproved FROM committee_main cm, committee c WHERE  cm.committeeid=c.committeeid AND committeemainid=:committeemainid ";
	private static final  String INITIATIONCOMMITTEEDELETE= "DELETE FROM committee_initiation WHERE CommitteeInitiationId=:committeeinitiationid";
	private static final String INITIATIONDETAILS ="SELECT InitiationId,ProjectShortName,ProjectTitle FROM pfms_initiation WHERE InitiationId=:initiationid";
	private static final String INITIATIONCOMMITTEEDESCRIPTIONTOR ="SELECT CommitteeInitiationId,description , termsofreference, committeeid , InitiationId FROM committee_initiation WHERE committeeid=:committeeid AND InitiationId=:initiationid";
	private final static String COMMITTEEINITIATIONUPDATE="UPDATE committee_initiation SET autoschedule='Y' WHERE initiationid=:initiationid AND committeeid=:committeeid";
	private static final String INITIATIONSCHEDULELISTALL = "SELECT cs.scheduleid, cs.committeeid,cs.committeemainid,cs.scheduledate,cs.schedulestarttime,cs.projectid , GetCommitteeShortName(cs.scheduledate,c.committeeshortname) AS committeeshortname FROM committee_schedule cs,committee c WHERE cs.committeeid=c.committeeid AND cs.projectid=0 AND cs.divisionid=0 AND cs.initiationid=:initiationid AND cs.isactive=1 ";
	private static final String INITIATIONCOMMITTEESCHEDULELIST = "SELECT cs.scheduleid, cs.committeeid,cs.committeemainid,cs.scheduledate,cs.schedulestarttime,cs.projectid , c.committeeshortname FROM committee_schedule cs,committee c WHERE cs.committeeid=c.committeeid AND cs.initiationid=:initiationid AND cs.CommitteeId=:committeeid AND cs.isactive=1 ORDER BY cs.scheduledate DESC";
	private final static String INITIATIONCOMMITTEEMAINLIST = "SELECT  b.committeeid,a.initiationid, a.autoschedule,b.committeeshortname,b.committeename FROM committee_initiation a,committee b WHERE a.committeeid=b.committeeid  AND a.initiationid =:initiationid";
	private static final String INITIAITIONMASTERLIST = "SELECT b.committeename,b.committeeshortname,b.committeeid,a.committeeinitiationid,b.periodicnon,b.periodicduration,a.autoschedule FROM committee_initiation a,committee b  WHERE a.committeeid=b.committeeid AND a.initiationid=:initiationid";
	private static final String PROPOSEDCOMMITTEEMAINID ="SELECT b.committeemainid,'mainid' FROM committee_main a , committee_main b WHERE a.committeeid=b.committeeid AND a.projectid=b.projectid AND a.divisionid=b.divisionid AND a.initiationid= b.initiationid AND b.status='P' AND b.isactive=0 AND a.committeemainid=:committeemainid";
	private static final String GETPROPOSEDCOMMITTEEMAINID="SELECT committeemainid,'mainid' FROM committee_main WHERE STATUS='P' AND committeeid=:committeeid AND projectid=:projectid AND divisionid=:divisionid AND initiationid=:initiationid";
	private static final String COMMITTEEMAINAPPROVALDATA="SELECT ca.constitutionapprovalid, ca.committeemainid, ca.emplabid, ca.empid, ca.remarks, ca.constitutionstatus, ca.approvalauthority,cas.statusdetail,cas.constitutionstatusid FROM committee_constitution_approval ca,committee_constitution_approval_status cas WHERE ca.constitutionstatus=cas.status AND ca.committeemainid=:committeemainid";
	private static final String COMMITTEEAPPROVAL="UPDATE committee_schedule SET scheduleflag=:flag, modifiedby=:modifiedby, modifieddate=:modifieddate WHERE scheduleid=:scheduleid";
	private static final String COMMITTEEAPPROVALUPDATE="UPDATE committee_constitution_approval SET ConstitutionStatus=:constitutionstatus , ActionBy=:actionby,ActionDate=:actiondate,Remarks=:remarks WHERE CommitteeMainId=:committeemainid"; //, EmpLabid=:emplabid, Empid=:empid
	private static final String UPDATECOMMITTEEAPPROVALAUTHORITY="UPDATE committee_constitution_approval SET ApprovalAuthority=:approvalauthority WHERE CommitteeMainId=:committeemainid";
	private static final String PROPOSEDCOMMITTELIST="SELECT cm.committeemainid,cm.committeeid, cm.projectid, cm.divisionid, cm.initiationid ,cm.status, cm.isactive ,c.committeeshortname,c.committeename FROM committee_main cm, committee c  ,committee_constitution_approval cca WHERE cm.status='P' AND cm.committeeid=c.committeeid AND cca.committeemainid=cm.committeemainid AND cca.ConstitutionStatus NOT IN ('0') ";
	private static final String APPROVALSTATUSLIST="SELECT constitutionstatusid,authorityid,STATUS, statusdetail FROM committee_constitution_approval_status WHERE authorityid = (SELECT cas.authorityid FROM committee_constitution_approval_status cas,committee_constitution_approval ca   WHERE ca.committeemainid=:committeemainid AND ca.constitutionstatus=cas.status)+1 ORDER BY  constitutionstatusid";
	private static final String NEWCOMMITTEEMAINISACTIVEUPDATE ="UPDATE committee_main SET isactive=1,STATUS='A',modifiedby=:modifiedby, modifieddate=:modifieddate WHERE committeemainid=:committeemainid";
	private static final String LOGINDATA ="SELECT loginid, username,empid, divisionid,logintype FROM login WHERE loginid=:loginid";
	private static final String DORTMDDATA ="SELECT rt.rtmddoid, rt.empid , rt.type FROM pfms_initiation_approver rt WHERE rt.isactive=1 AND rt.type='P&C DO'";
	private static final String COMCONSTITUTIONAPPROVALHISTORY ="SELECT cch.constitutionapprovalid, cch.committeemainid,cch.constitutionstatus,cch.remarks, cch.actionbylabid, cch.actionbyempid,cch.actiondate,ccs.statusdetail, e.empname,ed.designation,cl.labcode, cl.labname FROM committee_constitution_history cch, committee_constitution_approval_status ccs,  employee e, employee_desig ed, cluster_lab cl WHERE cch.constitutionstatus=ccs.status AND cch.actionbyempid=e.empid AND e.desigid=ed.desigid AND cch.actionbylabid=cl.labid AND cch.committeemainid=:committeemainid";
	private static final String COMCONSTITUTIONEMPDETAILS ="SELECT cca.empid ,e.emp_name,ed.designation,'Constituted By' FROM committee_constitution_approval cca, employee e ,employee_desig ed WHERE cca.empid=e.emp_id AND e.desig_id=ed.desig_id AND committeemainid=:committeemainid";
	private static final String DORTMDADEMPDATA=" SELECT pr.empid ,e.emp_name,ed.designation ,pr.type FROM pfms_initiation_approver pr, employee e ,employee_desig ed WHERE pr.empid=e.emp_id AND e.desig_id=ed.desig_id AND pr.isactive='1' AND pr.type='DO-RTMD' ";
	private static final String DIRECTOREMPDATA="SELECT l.lab_authority_id,e.emp_name,ed.designation ,'Director'  FROM lab_master l, employee e ,employee_desig ed   WHERE l.lab_authority_id=e.emp_id AND e.desig_id=ed.desig_id AND l.lab_code=:LabCode ";
	private static final String COMMITTEEMAINAPPROVALDODATA ="SELECT e1.emp_id,e1.emp_name,ed.designation,'Group Head' FROM employee e,employee e1,employee_desig ed, committee_constitution_approval cca,division_master dm ,division_group dg WHERE cca.empid=e.emp_id AND e.division_id=dm.division_id AND dm.group_id=dg.group_id AND dg.group_head_id=e1.emp_id AND e1.desig_id=ed.desig_id AND cca.committeemainid=:committeemainid";
	private static final String COMMITTEEMINUTESDELETE ="DELETE FROM committee_schedules_minutes_details WHERE scheduleminutesid=:scheduleminutesid";
	private static final String COMMITTEECONSTATUSDETAILS ="SELECT statusdetail,status FROM committee_constitution_approval_status WHERE status=:status";
	private static final String COMMITTEESCHEDULEAGENDADELETE ="UPDATE committee_schedules_agenda SET isactive=0, modifiedby=:modifiedby, modifieddate=:modifieddate WHERE scheduleid=:scheduleid";
	private static final String COMMITTEESCHEDULEINVITATIONDELETE ="DELETE FROM committee_schedules_invitation WHERE committeescheduleid=:scheduleid ";
	private static final String SCHEDULECOMMITTEEEMPCHECK ="SELECT cm.committeemainid,cme.empid FROM committee_schedule cs,committee_main cm,committee_member cme WHERE cs.committeeid=cm.committeeid AND cs.projectid=cm.projectid 	AND cs.divisionid=cm.divisionid AND cs.initiationid=cm.initiationid AND cm.isactive=1 AND cm.committeemainid=cme.committeemainid 	AND cme.labcode IN (SELECT lab_code FROM lab_master) AND cme.membertype IN ('CC','CS','PS','CH') AND cme.isactive=1 AND cs.scheduleid=:scheduleid AND cme.empid=:empid";
	private static final String SCHEDULECOMMITTEEEMPINVITEDCHECK ="SELECT csi.committeescheduleid, csi.empid, csi.membertype ,cms.meetingstatusid FROM committee_schedules_invitation csi ,committee_schedule cs, committee_meeting_status cms WHERE csi.committeescheduleid=cs.scheduleid  AND cs.scheduleflag=cms.meetingstatus AND   CASE WHEN cms.meetingstatusid < 7  	THEN  csi.membertype IN ('CC','CS','PS','CH') 	 WHEN  cms.meetingstatusid >= 7  THEN  csi.membertype IN ('CC','CS','PS','CH','CI','P','I') 	END AND csi.empid =:empid AND csi.committeescheduleid=:scheduleid";
	private static final String EMPSCHEDULEDATA="SELECT cs1.scheduleid,cs1.meetingid,cs1.scheduledate,csi.membertype,cs1.committeemainid,cs1.schedulestarttime FROM committee_schedule cs1, committee_schedules_invitation csi WHERE csi.labcode IN (SELECT lab_code FROM lab_master) AND cs1.scheduleid=csi.committeescheduleid AND csi.empid=:empid AND cs1.scheduledate=(SELECT cs2.scheduledate FROM committee_schedule cs2 WHERE cs2.scheduleid=:scheduleid) AND cs1.scheduleid  <> :scheduleid";
	private static final String ALLACTIONASSIGNEDCHECK="SELECT csm.scheduleminutesid AS 'csmid',csm.scheduleid ,csm.idarck,ass.assignee, am.scheduleminutesid AS 'ammid' FROM action_assign ass, committee_schedules_minutes_details csm LEFT JOIN action_main am ON csm.scheduleminutesid=am.scheduleminutesid  WHERE ass.actionmainid=am.actionmainid AND csm.idarck IN ('A','I','K') AND csm.scheduleid=:scheduleid";
	private static final String DEFAULTAGENDALIST="SELECT csa.DefaultAgendaid,csa.agendapriority,csa.agendaitem,csa.remarks,csa.duration,csa.committeeid FROM committee_default_agenda csa  WHERE isactive=1 AND csa.committeeid=:committeeid AND csa.LabCode = :LabCode";
	private static final String PROCUREMETSSTATUSLIST="SELECT f.PftsFileId, f.DemandNo, f.OrderNo, f.DemandDate, f.DpDate, ROUND(f.EstimatedCost/100000,2) AS 'EstimatedCost',ROUND(f.OrderCost/100000, 2) AS 'OrderCost', f.PDRDate ,f.ItemNomenclature, s.PftsStatus, s.PftsStageName, f.Remarks,'' AS vendorname,f.PftsStatusId  AS id ,f.spcdate FROM pfts_file f, pfts_status s  WHERE f.ProjectId=:projectid AND f.isactive='1' AND f.EstimatedCost>(SELECT proclimit FROM pfms_project_data WHERE ProjectId=:projectid )  AND f.PftsStatusId=s.PftsStatusId AND s.PftsStatusId<16 AND f.PftsFileId NOT IN(SELECT PftsFileId FROM pfts_file_order) UNION SELECT f.PftsFileId, f.DemandNo, o.OrderNo, f.DemandDate, o.DpDate, ROUND(f.EstimatedCost/100000,2) AS 'EstimatedCost',ROUND(o.OrderCost/100000, 2) AS 'OrderCost', f.PDRDate ,f.ItemNomenclature, s.PftsStatus, s.PftsStageName, f.Remarks,o.vendorname,f.PftsStatusId  AS id ,o.OrderDate  FROM pfts_file f, pfts_status s,pfts_file_order o  WHERE f.ProjectId=:projectid AND f.isactive='1' AND f.PftsFileId=o.PftsFileId  AND f.PftsStatusId=s.PftsStatusId AND s.PftsStatusId<16 AND o.OrderCost>(SELECT proclimit FROM pfms_project_data WHERE ProjectId=:projectid ) ORDER BY  DemandNo , id ASC";
	private static final String COMMITTEEMINUTESSPECNEW="SELECT minutesid,description FROM committee_schedules_minutes_new";
	private static final String MILESTONESUBSYSTEMS="SELECT maa.activityId, maa.Parentactivityid, maa.activityname, maa.orgenddate, maa.enddate,maa.activitystatusid,mas.activityshort, maa.ProgressStatus,ma.milestoneno, maa.StatusRemarks,maa.activitylevelid FROM milestone_activity ma,milestone_activity_level maa,milestone_activity_status mas WHERE ma.milestoneactivityid = maa.parentactivityid AND maa.activitylevelid='1' AND maa.activitystatusid=mas.activitystatusid  AND ma.projectid=:projectid ORDER BY ma.milestoneno ASC";
	private static final String FILEREPMASTERLISTALL ="SELECT a.filerepmasterid,a.parentlevelid,a.levelid,a.levelname,b.project_code FROM file_rep_master a,project_master b WHERE a.filerepmasterid>0 AND a.projectid=b.project_id AND a.projectid=:projectid AND a.LabCode=:LabCode UNION ALL SELECT a.filerepmasterid, a.parentlevelid, a.levelid, a.levelname, 'General' AS ProjectCode FROM file_rep_master a WHERE a.filerepmasterid > 0 AND a.projectid = :projectid ORDER BY parentlevelid";
	private static final String AGENDADOCLINKDOWNLOAD  ="SELECT b.filerepuploadid,b.filerepid,b.filepath,b.filenameui,b.filename,b.filepass,b.ReleaseDoc,b.VersionDoc FROM file_rep_upload b WHERE  b.filerepuploadid=:filerepid ";
	private static final String MALIST="SELECT a.milestoneactivityid,0 AS 'parentactivityid', a.activityname,a.orgstartdate,a.orgenddate,a.startdate,a.enddate,a.progressstatus, mas.activitystatus, e.empname AS 'OIC1',a.milestoneno, mas.activityshort, mas.activitystatusid,0 as level FROM milestone_activity a, milestone_activity_status mas,employee e WHERE  a.isactive=1 AND mas.activitystatusid=a.activitystatusid AND a.enddate BETWEEN CURDATE() AND DATE(DATE_ADD(CURDATE(),INTERVAL 180 DAY))  AND a.oicempid=e.empid AND a.projectid=:ProjectId";
	private static final String MILEACTIVITYLEVEL="SELECT a.activityid ,a.parentactivityid, a.activityname,a.orgstartdate,a.orgenddate , a.startdate, a.enddate,  a.progressstatus, mas.activitystatus, e.empname,0 as milestoneno, mas.activityshort, mas.activitystatusid,a.activitylevelid as level  FROM milestone_activity_level a, milestone_activity_status mas, employee e WHERE  a.enddate BETWEEN CURDATE() AND DATE(DATE_ADD(CURDATE(),INTERVAL 180 DAY)) AND mas.activitystatusid=a.activitystatusid AND a.oicempid=e.empid AND a.parentactivityid=:id AND a.activitylevelid=:levelid ";
//	private static final String AGENDALINKEDDOCLIST="SELECT sad.agendadocid,sad.agendaid,sad.filedocid,fru.filenameui,fru.VersionDoc,fru.ReleaseDoc FROM committee_schedule_agenda_docs sad, committee_schedules_agenda sa, file_rep_upload fru WHERE sad.agendaid=sa.scheduleagendaid AND sad.filedocid = fru.FileRepUploadId AND sad.isactive=1 AND sa.isactive=1 AND sa.scheduleid=:scheduleid";
	private static final String AGENDALINKEDDOCLIST="""
			SELECT sad.agendadocid,sad.agendaid,sad.filedocid,fru.filenameui,fru.VersionDoc,fru.ReleaseDoc 
			FROM committee_schedule_agenda_docs sad, committee_schedules_agenda sa, file_rep_upload fru,committee_schedule cs 
			WHERE sad.agendaid=sa.scheduleagendaid AND sad.filedocid = fru.FileRepUploadId AND sad.isactive=1 
			AND sa.isactive=1 AND sa.scheduleid=:scheduleid AND CASE WHEN cs.ProgrammeId<>0 THEN 1=1 ELSE sa.scheduleid=cs.scheduleid AND sa.projectid=cs.projectid END
			UNION
			SELECT sad.agendadocid,sad.agendaid,sad.filedocid,fru.filenameui,fru.VersionDoc,fru.ReleaseDoc 
			FROM committee_schedule_agenda_docs sad, committee_schedules_agenda sa, file_rep_upload_preproject fru,committee_schedule cs
			WHERE sad.agendaid=sa.scheduleagendaid AND sad.filedocid = fru.FileRepUploadId AND sad.isactive=1 AND sa.scheduleid=cs.scheduleid
			AND sa.isactive=1 AND sa.scheduleid=:scheduleid AND cs.initiationId<>0 AND sa.projectid<>0 AND sa.projectid=cs.initiationId """;
	
	@PersistenceContext
	EntityManager manager;
	
	@Override
	public long CommitteeNewAdd(Committee committeeModel)throws Exception
	{
		manager.persist(committeeModel);
		return committeeModel.getCommitteeId(); 
	}
	
	@Override
	public List<Object[]> CommitteeNamesCheck(String name,String sname,String projectid,String LabCode) throws Exception
	{
		Query query=manager.createNativeQuery(COMMITTEENAMESCHECK);		
		query.setParameter("committeeshortname", sname);
		query.setParameter("committeename", name);
		query.setParameter("labcode", LabCode);
		
		List<Object[]> CommitteeNamesCheck=(List<Object[]>)query.getResultList();

		return CommitteeNamesCheck;
	}
	
	@Override
	public List<Object[]> CommitteeListAll() throws Exception
	{
		Query query=manager.createNativeQuery(COMMITTEELISTALL);
		
		List<Object[]> CommitteeListAll=(List<Object[]>)query.getResultList();
		
		return CommitteeListAll;
	}
	
	@Override
	public List<Object[]> CommitteeListActive(String isglobal,String projectapplicable, String LabCode) throws Exception
	{
		Query query=manager.createNativeQuery(COMMITTEELISTACTIVE);
		query.setParameter("isglobal", isglobal);
		query.setParameter("projectapplicable", projectapplicable);
		query.setParameter("labcode", LabCode);
		List<Object[]> CommitteeListActives=(List<Object[]>)query.getResultList();		
		return CommitteeListActives;
	}
	@Override
	public Object[] CommitteeDetails(String committeeid) throws Exception
	{
		try {
			Query query=manager.createNativeQuery(COMMITTEEDETAILS);
			query.setParameter("committeeid", committeeid);		
			List<Object[]> list = (List<Object[]>)query.getResultList();	
			if(list!=null && list.size()>0) {
				return list.get(0);
			}else {
				return null;
			}
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public Long CommitteeEditSubmit(Committee committeemodel) throws Exception
	{	
		Committee ExistingCommittee = manager.find(Committee.class, committeemodel.getCommitteeId());
		if(ExistingCommittee != null) {
			ExistingCommittee.setCommitteeShortName(committeemodel.getCommitteeShortName());
			ExistingCommittee.setCommitteeName(committeemodel.getCommitteeName());
			ExistingCommittee.setCommitteeType(committeemodel.getCommitteeType());
			ExistingCommittee.setProjectApplicable(committeemodel.getProjectApplicable());
			ExistingCommittee.setModifiedBy(committeemodel.getModifiedBy());
			ExistingCommittee.setModifiedDate(committeemodel.getModifiedDate());
			ExistingCommittee.setPeriodicDuration(committeemodel.getPeriodicDuration());
			ExistingCommittee.setTechNonTech(committeemodel.getTechNonTech());
			ExistingCommittee.setGuidelines(committeemodel.getGuidelines());
			ExistingCommittee.setPeriodicNon(committeemodel.getPeriodicNon());
			ExistingCommittee.setDescription(committeemodel.getDescription());
			ExistingCommittee.setTermsOfReference(committeemodel.getTermsOfReference());
			ExistingCommittee.setIsGlobal(committeemodel.getIsGlobal());
			ExistingCommittee.setIsBriefing(committeemodel.getIsBriefing());
			return 1L;
		}
		else {
			return 0L;
		}
		
		
	}
	

	@Override
	public List<Object[]> EmployeeList(String LabCode) throws Exception {
		Query query=manager.createNativeQuery(EMPLOYEELIST);
		query.setParameter("labcode", LabCode);
		List<Object[]> EmployeeList=(List<Object[]>)query.getResultList();	
		return EmployeeList;
	}
	
	@Override
	public Object[] CommitteeName(String CommitteeId) throws Exception {
		Query query=manager.createNativeQuery(COMMITTEENAME);
		query.setParameter("committeeid", Long.parseLong(CommitteeId));
		Object[] CommitteeName=(Object[])query.getSingleResult();	
		return CommitteeName;
	}

	@Override
	public long CommitteeDetailsSubmit(CommitteeMain committeemain) throws Exception {
		
		manager.persist(committeemain);
		return committeemain.getCommitteeMainId();
	}
	

	


	@Override
	public Long LastCommitteeId(String CommitteeId,String projectid,String divisionid,String initiationid, String carsInitiationId, String programmeId) throws Exception {

		Query query=manager.createNativeQuery(LASTCOMMITTEEID);
		query.setParameter("committeeid", CommitteeId);
		query.setParameter("projectid",projectid != null ? Long.parseLong(projectid) : 0);
		query.setParameter("divisionid",divisionid != null ? Long.parseLong(divisionid) : 0);
		query.setParameter("initiationid",initiationid != null ?  Long.parseLong(initiationid) : 0);
		query.setParameter("CARSInitiationId",carsInitiationId != null ? Long.parseLong(carsInitiationId) : 0);
		query.setParameter("ProgrammeId",programmeId != null ?  Long.parseLong(programmeId) : 0);
		return Long.parseLong(query.getResultList().stream().findFirst().orElse(0).toString());
	}

	@Override
	public Long UpdateCommitteemainValidto(CommitteeMain committeemain) throws Exception {
		
		CommitteeMain ExisitingCommitteeMain=manager.find(CommitteeMain.class, committeemain.getCommitteeMainId());
		if(ExisitingCommitteeMain != null)
		{
			ExisitingCommitteeMain.setIsActive(0);
			ExisitingCommitteeMain.setStatus("E");
			ExisitingCommitteeMain.setValidTo(committeemain.getValidTo());
			ExisitingCommitteeMain.setModifiedBy(committeemain.getModifiedBy());
			ExisitingCommitteeMain.setModifiedDate(committeemain.getModifiedDate());
			return 1L;

		}
		else {
			return 0L;
		}
		
		
	}


	@Override
	public List<Object[]> CommitteeMainList(String labcode) throws Exception {

		Query query=manager.createNativeQuery(COMMITTEEMAINLIST);
		query.setParameter("labcode", labcode);
		List<Object[]> CommitteeMainList=(List<Object[]>)query.getResultList();
		
		return CommitteeMainList;
	}
	
	
	
	@Override
	public List<Object[]> CommitteeNonProjectList() throws Exception 
	{
		Query query=manager.createNativeQuery(COMMITTEENONPROJECTLIST);
		List<Object[]> CommitteeNonProjectList=(List<Object[]>)query.getResultList();
		
		return CommitteeNonProjectList;
	}



	@Override
	public int CommitteeMemberDelete(CommitteeMember committeemember) throws Exception
	{	
		CommitteeMember ExistingCommitteeMember = manager.find(CommitteeMember.class, committeemember.getCommitteeMemberId());
		if(ExistingCommitteeMember != null) {
			ExistingCommitteeMember.setIsActive(0);
			ExistingCommitteeMember.setModifiedBy(committeemember.getModifiedBy());
			ExistingCommitteeMember.setModifiedDate(committeemember.getModifiedDate());
			return 1;
		}
		else {
			return 0;
		}
		
	}
	
	
	@Override
	public Long CommitteeMainMembersAdd(CommitteeMember  committeemember)throws Exception
	{
		manager.persist(committeemember);
		manager.flush();
		return (Long)committeemember.getCommitteeMemberId();
	}
	
	
	
	@Override
	public long CommitteeScheduleAddSubmit(CommitteeSchedule committeeschedule )throws Exception
	{
		manager.persist(committeeschedule);
		return committeeschedule.getScheduleId();
	}
	
	@Override
	public List<Object[]> CommitteeScheduleListNonProject(String committeeid)throws Exception
	{
		Query query=manager.createNativeQuery(COMMITTEESCHEDULELIST);
		query.setParameter("committeeid", Long.parseLong(committeeid));
		List<Object[]> committeeschedulelist=(List<Object[]>)query.getResultList();
		return committeeschedulelist;
	}

	private static final String EDITDATA = """
			SELECT a.committeeid,a.committeemainid,a.scheduledate,a.schedulestarttime,a.scheduleflag,a.schedulesub,a.scheduleid, GetCommitteeName(a.scheduledate,b.committeeshortname,b.committeename) AS committeename, GetCommitteeShortName(a.scheduledate,b.committeeshortname) AS committeeshortname,
			a.projectid,c.meetingstatusid,a.meetingid,a.meetingvenue,a.confidential,a.Reference,(SELECT d.classification FROM pfms_security_classification d WHERE a.confidential=d.classification_id) 
			AS 'classification',a.divisionid  ,a.initiationid ,a.pmrcdecisions,a.kickoffotp ,(SELECT minutesattachmentid FROM committee_minutes_attachment WHERE scheduleid=a.scheduleid) AS 'attachid', 
			b.periodicNon,a.MinutesFrozen,a.briefingpaperfrozen,a.labcode, a.CARSInitiationId, a.ProgrammeId 
			FROM committee_schedule a,committee b ,committee_meeting_status c 
			WHERE a.scheduleflag=c.MeetingStatus 
			AND a.scheduleid=:committeescheduleid AND a.committeeid=b.committeeid
			""";
	@Override
	public Object[] CommitteeScheduleEditData(String CommitteeScheduleId) throws Exception {

//		Query query=manager.createNativeQuery(COMMITTEESCHEDULEEDITDATA);
		Query query=manager.createNativeQuery(EDITDATA);
		query.setParameter("committeescheduleid", Long.parseLong(CommitteeScheduleId));
//		query.setParameter("compareDate", "2026-04-01");
		Object[] CommitteeScheduleEditData=(Object[])query.getSingleResult();
		return CommitteeScheduleEditData;
	}
	
	@Override
	public List<Object[]> AgendaReturnData(String CommitteeScheduleId) throws Exception {

		Query query=manager.createNativeQuery(AGENDARETURNDATA);
		query.setParameter("scheduleid", Long.parseLong(CommitteeScheduleId));
		List<Object[]> AgendaReturnData=(List<Object[]>)query.getResultList();
		return AgendaReturnData;
	}
  

	@Override
	public List<Object[]> ProjectList(String LabCode) throws Exception {

		Query query=manager.createNativeQuery(PROJECTLIST);
		query.setParameter("labcode", LabCode);
		List<Object[]> ProjectList=(List<Object[]>)query.getResultList();
		return ProjectList;
	}



	@Override
	public Long CommitteeAgendaSubmit(CommitteeScheduleAgenda scheduleagenda) throws Exception 
	{
		manager.persist(scheduleagenda);
		manager.flush();
		
		return scheduleagenda.getScheduleAgendaId();
	}
	
	private static final String AGENDAADDEDDOCLINKIDLIST = " SELECT CAST( filedocid AS CHAR) AS filedocid  FROM committee_schedule_agenda_docs WHERE agendaid=:agendaid AND isactive=1 ";
	
	@Override
	public List<String> AgendaAddedDocLinkIdList(String agendaid) throws Exception 
	{
		Query query=manager.createNativeQuery(AGENDAADDEDDOCLINKIDLIST);
		query.setParameter("agendaid", Long.parseLong(agendaid));
		List<String> AgendaAddedDocLinkIdList=new ArrayList<String>();
		try {
			AgendaAddedDocLinkIdList=(List<String>)query.getResultList();
		}catch (Exception e) {
			logger.error(new java.util.Date() +"Inside DAO AgendaAddedDocLinkIdList "+e);
			e.printStackTrace();
			return AgendaAddedDocLinkIdList;
		}
		return AgendaAddedDocLinkIdList;
	}
	
	@Override
	public Long AgendaDocLinkAdd(CommitteeScheduleAgendaDocs doc) throws Exception {
		manager.persist(doc);
		manager.flush();		
		return doc.getAgendaDocid();
	}
	//changed as expert is not Coming 
//	private static final String AGENDALIST ="""
//		SELECT a.scheduleagendaid,a.scheduleid,a.schedulesubid,a.agendaitem,b.projectname,b.projectid,a.remarks,b.projectcode,
//		a.agendapriority,a.presenterid ,CONCAT(IFNULL(CONCAT(j.Title,' '),(IFNULL(CONCAT(j.Salutation, ' '), ''))), j.EmpName) AS 'EmpName' ,h.designation,a.duration,
//		j.desigid, a.PresentorLabCode  FROM project_master b,employee j,employee_desig h,committee_schedules_agenda a,committee_schedule cs
//		WHERE CASE WHEN cs.ProgrammeId<>0 THEN a.ProjectId=b.ProjectId ELSE a.projectid=cs.projectid AND cs.projectid=b.projectid AND a.scheduleid=cs.scheduleid END AND a.scheduleid=:committeescheduleid AND a.isactive=1 AND a.projectid<>0 AND a.presenterid=j.empid AND 
//		j.desigid=h.desigid AND a.PresentorLabCode<>'@EXP'
//		UNION
//		SELECT a.scheduleagendaid,a.scheduleid,a.schedulesubid,a.agendaitem,b.projectname,b.projectid,a.remarks,b.projectcode,
//		a.agendapriority,a.presenterid ,CONCAT(IFNULL(CONCAT(j.Title,' '),(IFNULL(CONCAT(j.Salutation, ' '), ''))), j.ExpertName) AS 'EmpName' ,h.designation,a.duration,
//		j.desigid, a.PresentorLabCode  FROM project_master b,expert j,employee_desig h,committee_schedules_agenda a,committee_schedule cs
//		WHERE a.projectid=cs.projectid AND cs.projectid=b.projectid AND a.scheduleid=cs.scheduleid AND a.scheduleid=:committeescheduleid AND a.isactive=1 AND a.projectid<>0 AND a.presenterid=j.expertid AND 
//		j.desigid=h.desigid AND a.PresentorLabCode='@EXP'
//		UNION  
//		SELECT a.scheduleagendaid,a.scheduleid,a.schedulesubid,a.agendaitem,cs.labcode AS 'projectname' , '0' AS projectid,
//		a.remarks,'' AS projectcode,a.agendapriority,a.presenterid ,CONCAT(IFNULL(CONCAT(j.Title,' '),(IFNULL(CONCAT(j.Salutation, ' '), ''))), j.EmpName) AS 'EmpName' ,
//		h.designation,a.duration,j.desigid, a.PresentorLabCode  FROM employee j,employee_desig h, committee_schedules_agenda a, committee_schedule cs
//		WHERE a.scheduleid=:committeescheduleid AND a.scheduleid=cs.scheduleid  AND a.isactive=1 AND a.projectid=0 
//		AND a.presenterid=j.empid AND j.desigid=h.desigid AND a.PresentorLabCode<>'@EXP' 
//		UNION
//		SELECT a.scheduleagendaid,a.scheduleid,a.schedulesubid,a.agendaitem,cs.labcode AS 'projectname' , '0' AS projectid,
//		a.remarks,'' AS projectcode,a.agendapriority,a.presenterid ,CONCAT(IFNULL(CONCAT(j.Title,' '),(IFNULL(CONCAT(j.Salutation, ' '), ''))), j.ExpertName) AS 'EmpName' ,
//		h.designation,a.duration,j.desigid, a.PresentorLabCode  FROM expert j,employee_desig h, committee_schedules_agenda a, committee_schedule cs
//		WHERE a.scheduleid=:committeescheduleid AND a.scheduleid=cs.scheduleid  AND a.isactive=1 AND a.projectid=0 
//		AND a.presenterid=j.expertid AND j.desigid=h.desigid AND a.PresentorLabCode='@EXP'
//		UNION
//		SELECT a.scheduleagendaid,a.scheduleid,a.schedulesubid,a.agendaitem,b.ProjectTitle,b.initiationId,a.remarks,b.ProjectShortName,
//		a.agendapriority,a.presenterid ,CONCAT(IFNULL(CONCAT(j.Title,' '),(IFNULL(CONCAT(j.Salutation, ' '), ''))), j.EmpName) AS 'EmpName' ,h.designation,a.duration,
//		j.desigid, a.PresentorLabCode  FROM pfms_initiation b,employee j,employee_desig h,committee_schedules_agenda a,committee_schedule cs
//		WHERE a.projectid=cs.initiationId AND cs.initiationId=b.initiationId AND a.scheduleid=cs.scheduleid AND a.scheduleid=:committeescheduleid AND a.isactive=1 AND a.projectid<>0 AND a.presenterid=j.empid AND 
//		j.desigid=h.desigid AND a.PresentorLabCode<>'@EXP'
//		ORDER BY agendapriority """;
	
	private static final String AGENDALIST="""
			SELECT a.scheduleagendaid,a.scheduleid,a.schedulesubid,a.agendaitem,b.project_name,b.project_id,a.remarks,b.project_code,
			a.agendapriority,a.presenterid ,CONCAT(IFNULL(CONCAT(j.title,' '),(IFNULL(CONCAT(j.Salutation, ' '), ''))), j.emp_name) AS 'EmpName' ,h.designation,a.duration,
			j.desig_id, a.PresentorLabCode,NULLIF(TRIM(a.GroupName), '') AS GroupName  FROM project_master b,employee j,employee_desig h,committee_schedules_agenda a,committee_schedule cs
			WHERE CASE WHEN cs.ProgrammeId<>0 THEN  a.ProjectId=b.project_id ELSE cs.InitiationId=0 and a.projectid=b.project_id AND a.scheduleid=cs.scheduleid END AND a.scheduleid=:committeescheduleid AND a.isactive=1 AND a.projectid NOT IN (0, -1) AND a.presenterid=j.emp_id AND 
			j.desig_id=h.desig_id AND a.PresentorLabCode<>'@EXP'
			UNION
			SELECT a.scheduleagendaid,a.scheduleid,a.schedulesubid,a.agendaitem,b.project_name,b.project_id,a.remarks,b.project_code,
			a.agendapriority,a.presenterid ,CONCAT(IFNULL(CONCAT(j.title,' '),(IFNULL(CONCAT(j.Salutation, ' '), ''))), j.ExpertName) AS 'EmpName' ,h.designation,a.duration,
			j.desigid, a.PresentorLabCode,NULLIF(TRIM(a.GroupName), '') AS GroupName  FROM project_master b,expert j,employee_desig h,committee_schedules_agenda a,committee_schedule cs
			WHERE a.projectid=cs.projectid AND cs.projectid=b.project_id AND a.scheduleid=cs.scheduleid AND a.scheduleid=:committeescheduleid AND a.isactive=1 AND a.projectid NOT IN (0, -1) AND a.presenterid=j.expertid AND j.desigid=h.desig_id AND a.PresentorLabCode='@EXP'
			UNION  
			SELECT a.scheduleagendaid,a.scheduleid,a.schedulesubid,a.agendaitem,cs.labcode AS 'projectname' , '0' AS projectid,
			a.remarks,'' AS projectcode,a.agendapriority,a.presenterid ,CONCAT(IFNULL(CONCAT(j.title,' '),(IFNULL(CONCAT(j.Salutation, ' '), ''))), j.emp_name) AS 'EmpName' ,
			h.designation,a.duration,j.desig_id, a.PresentorLabCode,NULLIF(TRIM(a.GroupName), '') AS GroupName  FROM employee j,employee_desig h, committee_schedules_agenda a, committee_schedule cs
			WHERE a.scheduleid=:committeescheduleid AND a.scheduleid=cs.scheduleid  AND a.isactive=1 AND a.projectid IN (0, -1)
			AND a.presenterid=j.emp_id AND j.desig_id=h.desig_id AND a.PresentorLabCode<>'@EXP' 
			UNION
			SELECT a.scheduleagendaid,a.scheduleid,a.schedulesubid,a.agendaitem,cs.labcode AS 'projectname' , '0' AS projectid,
			a.remarks,'' AS projectcode,a.agendapriority,a.presenterid ,CONCAT(IFNULL(CONCAT(j.title,' '),(IFNULL(CONCAT(j.Salutation, ' '), ''))), j.ExpertName) AS 'EmpName' ,
			h.designation,a.duration,j.desigid, a.PresentorLabCode,NULLIF(TRIM(a.GroupName), '') AS GroupName  FROM expert j,employee_desig h, committee_schedules_agenda a, committee_schedule cs
			WHERE a.scheduleid=:committeescheduleid AND a.scheduleid=cs.scheduleid  AND a.isactive=1 AND a.projectid IN (0, -1)
			AND a.presenterid=j.expertid AND j.desigid=h.desig_id AND a.PresentorLabCode='@EXP'
			UNION
			SELECT a.scheduleagendaid,a.scheduleid,a.schedulesubid,a.agendaitem,b.ProjectTitle,b.initiationId,a.remarks,b.ProjectShortName,
			a.agendapriority,a.presenterid ,CONCAT(IFNULL(CONCAT(j.title,' '),(IFNULL(CONCAT(j.Salutation, ' '), ''))), j.emp_name) AS 'EmpName' ,h.designation,a.duration,
			j.desig_id, a.PresentorLabCode,NULLIF(TRIM(a.GroupName), '') AS GroupName  FROM pfms_initiation b,employee j,employee_desig h,committee_schedules_agenda a,committee_schedule cs
			WHERE a.projectid=cs.initiationId AND cs.initiationId=b.initiationId AND a.scheduleid=cs.scheduleid AND a.scheduleid=:committeescheduleid AND a.isactive=1 AND a.projectid NOT IN (0, -1) AND a.presenterid=j.emp_id AND 
			j.desig_id=h.desig_id AND a.PresentorLabCode<>'@EXP'
			ORDER BY agendapriority""";
	
	@Override
	public List<Object[]> AgendaList(String CommitteeScheduleId) throws Exception {

		Query query=manager.createNativeQuery(AGENDALIST);
		query.setParameter("committeescheduleid", Long.parseLong(CommitteeScheduleId));
		List<Object[]> AgendaList=(List<Object[]>)query.getResultList();
		
		return AgendaList;
	}
	

	
	
	@Override
	public Long CommitteeScheduleUpdate(CommitteeSchedule committeeschedule) throws Exception {
		
		CommitteeSchedule ExistingCommitteeSchedule=manager.find(CommitteeSchedule.class, committeeschedule.getScheduleId());
		if(ExistingCommitteeSchedule != null) {
			ExistingCommitteeSchedule.setScheduleDate(committeeschedule.getScheduleDate());
			ExistingCommitteeSchedule.setScheduleStartTime(committeeschedule.getScheduleStartTime());
			ExistingCommitteeSchedule.setModifiedBy(committeeschedule.getModifiedBy());
			ExistingCommitteeSchedule.setModifiedDate(committeeschedule.getModifiedDate());
			ExistingCommitteeSchedule.setMeetingId(committeeschedule.getMeetingId());
			return 1L;
		}
		else {
			return 0L;
		}
		
	}


	@Override
	public List<Object[]> CommitteeMinutesSpecList(String CommitteeScheduleId) throws Exception 
	{
		Query query=manager.createNativeQuery(COMMITTEESPECLIST);
		query.setParameter("scheduleid", Long.parseLong(CommitteeScheduleId));
		List<Object[]> CommitteeMinutesSpecList=(List<Object[]>)query.getResultList();
		
		return CommitteeMinutesSpecList;
	}


	@Override
	public Long CommitteeMinutesInsert(CommitteeScheduleMinutesDetails committeescheduleminutesdetails)	throws Exception {

		manager.persist(committeescheduleminutesdetails);
		manager.flush();
		
		return committeescheduleminutesdetails.getScheduleMinutesId();
	}
	

	@Override
	public Object[] CommitteeMinutesSpecDesc(CommitteeScheduleMinutesDetails committeescheduleminutesdetails)throws Exception {
		Query query=manager.createNativeQuery(COMMITTEEMINUTESPEC);
		query.setParameter("minutesid", committeescheduleminutesdetails.getMinutesId());
		query.setParameter("agendasubid", committeescheduleminutesdetails.getMinutesSubId());
		query.setParameter("scheduleagendaid", committeescheduleminutesdetails.getMinutesSubOfSubId());
		
		Object[] CommitteeMinutesSpecDesc=(Object[])query.getSingleResult();		
		return CommitteeMinutesSpecDesc;
	}
	
	@Override
	public Object[] CommitteeMinutesSpecEdit(CommitteeScheduleMinutesDetails committeescheduleminutesdetails)throws Exception {
		
		Query query=manager.createNativeQuery(COMMITTEEMINUTEEDIT);
		query.setParameter("scheduleminutesid", committeescheduleminutesdetails.getScheduleMinutesId());		
		Object[] CommitteeMinutesSpecEdit=(Object[])query.getSingleResult();		
		return CommitteeMinutesSpecEdit;
	}
	
	@Override
	public Object[] CommitteeMinutesSpecEditForAde(CommitteeScheduleMinutesDetails committeescheduleminutesdetails) {
		Query query=manager.createNativeQuery(COMMITTEEMINUTEEDITFORADE);
		query.setParameter("scheduleminutesid", committeescheduleminutesdetails.getScheduleMinutesId());		
		Object[] CommitteeMinutesSpecEdit=(Object[])query.getSingleResult();		
		return CommitteeMinutesSpecEdit;
	}



	@Override
	public Long CommitteeMinutesUpdate(CommitteeScheduleMinutesDetails committeescheduleminutesdetails)	throws Exception {
		
		CommitteeScheduleMinutesDetails ExistingCommitteeScheduleMinutesDetails=manager.find(CommitteeScheduleMinutesDetails.class, committeescheduleminutesdetails.getScheduleMinutesId());
		if(ExistingCommitteeScheduleMinutesDetails != null) {
			ExistingCommitteeScheduleMinutesDetails.setScheduleId(committeescheduleminutesdetails.getScheduleId());
			ExistingCommitteeScheduleMinutesDetails.setScheduleSubId(committeescheduleminutesdetails.getScheduleSubId());
			ExistingCommitteeScheduleMinutesDetails.setMinutesId(committeescheduleminutesdetails.getMinutesId());
			ExistingCommitteeScheduleMinutesDetails.setDetails(committeescheduleminutesdetails.getDetails());
			ExistingCommitteeScheduleMinutesDetails.setIDARCK(committeescheduleminutesdetails.getIDARCK());
			ExistingCommitteeScheduleMinutesDetails.setModifiedBy(committeescheduleminutesdetails.getModifiedBy());
			ExistingCommitteeScheduleMinutesDetails.setModifiedDate(committeescheduleminutesdetails.getModifiedDate());
			ExistingCommitteeScheduleMinutesDetails.setRemarks(committeescheduleminutesdetails.getRemarks());
			if(committeescheduleminutesdetails.getAirCraftId()!=null) {
				ExistingCommitteeScheduleMinutesDetails.setAirCraftId(committeescheduleminutesdetails.getAirCraftId());
			}
			if(committeescheduleminutesdetails.getSubSystemId()!=null) {
				ExistingCommitteeScheduleMinutesDetails.setSubSystemId(committeescheduleminutesdetails.getSubSystemId());
			}
			// update query to update action_main based on detailsId
			String s="UPDATE action_main SET actionItem = :actionItem WHERE ScheduleMinutesId =:ScheduleMinutesId";
			Query query1= manager.createNativeQuery(s);
			query1.setParameter("actionItem", committeescheduleminutesdetails.getDetails());
			query1.setParameter("ScheduleMinutesId", committeescheduleminutesdetails.getScheduleMinutesId());
			query1.executeUpdate();
			return 1L;
		}
		else {
			return 0L;
		}
	
	}

	@Override
	public List<Object[]> CommitteeScheduleAgendaPriority(String Committeescheduleid)throws Exception
	{
		Query query=manager.createNativeQuery(COMMITTEESCHEDULEAGENDAPRIORITY);
		query.setParameter("scheduleid", Long.parseLong(Committeescheduleid));
		return  (List<Object[]>)query.getResultList();
	}

	

	@Override
	public long	CommitteeScheduleAgendaUpdate(CommitteeScheduleAgenda scheduleagenda) throws Exception   
	{	
		CommitteeScheduleAgenda ExistingCommitteeScheduleAgenda = manager.find(CommitteeScheduleAgenda.class, scheduleagenda.getScheduleAgendaId());
		if(ExistingCommitteeScheduleAgenda != null) {
			ExistingCommitteeScheduleAgenda.setAgendaItem(scheduleagenda.getAgendaItem());
			ExistingCommitteeScheduleAgenda.setProjectId(scheduleagenda.getProjectId());
			ExistingCommitteeScheduleAgenda.setRemarks(scheduleagenda.getRemarks());
			ExistingCommitteeScheduleAgenda.setModifiedBy(scheduleagenda.getModifiedBy());
			ExistingCommitteeScheduleAgenda.setModifiedDate(scheduleagenda.getModifiedDate());
			ExistingCommitteeScheduleAgenda.setPresentorLabCode(scheduleagenda.getPresentorLabCode());
			ExistingCommitteeScheduleAgenda.setPresenterId(scheduleagenda.getPresenterId());
			ExistingCommitteeScheduleAgenda.setDuration(scheduleagenda.getDuration());
			ExistingCommitteeScheduleAgenda.setGroupName(scheduleagenda.getGroupName());
			return 1L;
		}
		else {
			return 0L;
		}
		
	}

	@Override
	public int CommitteeAgendaPriorityUpdate(String agendaid,String agendapriority) throws Exception
	{		
			CommitteeScheduleAgenda ExistingCommitteeScheduleAgenda = manager.find(CommitteeScheduleAgenda.class, Long.parseLong(agendaid));
			if(ExistingCommitteeScheduleAgenda != null) {
				ExistingCommitteeScheduleAgenda.setAgendaPriority(Integer.parseInt(agendapriority));
				return 1;
				
			}
			else {
				return 0;
			}
			
	}

	@Override
	public List<Object[]> CommitteeScheduleGetAgendasAfter( String  scheduleid,String AgendaPriority) throws Exception
	{
		Query query=manager.createNativeQuery(COMMITTEESCHEDULEGETAGENDASAFTER);
		query.setParameter("scheduleid", Long.parseLong(scheduleid));
		query.setParameter("AgendaPriority",  AgendaPriority);
		List<Object[]> TccScheduleGetAgendasAfter=(List<Object[]>)query.getResultList();
		
		return TccScheduleGetAgendasAfter;
	}


	@Override
	public int CommitteeAgendaDelete(String committeescheduleagendaid,String Modifiedby ,String ModifiedDate)throws Exception
	{	
		CommitteeScheduleAgenda ExistingCommitteeScheduleAgenda = manager.find(CommitteeScheduleAgenda.class, Long.parseLong(committeescheduleagendaid));
		if(ExistingCommitteeScheduleAgenda != null) {
			ExistingCommitteeScheduleAgenda.setModifiedBy(Modifiedby);
			ExistingCommitteeScheduleAgenda.setModifiedDate(ModifiedDate);
			ExistingCommitteeScheduleAgenda.setIsActive(0);
			ExistingCommitteeScheduleAgenda.setAgendaPriority(0);
			return 1;
		}
		else {
			return 0;
		}
		
	}
	
	private static final String AGENDADOCUNLINK = "UPDATE committee_schedule_agenda_docs  SET isactive=0, modifiedby=:modifiedby , modifieddate=:modifieddate WHERE agendaid=:agendaid ";
	
	@Override
	public int AgendaDocUnlink(String agendaid,String Modifiedby ,String ModifiedDate)throws Exception
	{
		Query query =manager.createNativeQuery(AGENDADOCUNLINK);
		query.setParameter("agendaid", Long.parseLong(agendaid));
		query.setParameter("modifiedby",Modifiedby);
		query.setParameter("modifieddate", ModifiedDate);
		return query.executeUpdate();
	}

	@Override
	public long CommitteeSubScheduleSubmit(CommitteeSubSchedule committeesubschedule) throws Exception
	{
		manager.persist(committeesubschedule);
		return committeesubschedule.getScheduleSubId();
	}
		
	@Override
	public List<Object[]> CommitteeSubScheduleList(String scheduleid) throws Exception
	{
		Query query=manager.createNativeQuery(COMMITTEESUBSCHEDULELIST);
		query.setParameter("scheduleid", Long.parseLong(scheduleid));
		List<Object[]> CommitteeSubScheduleList=(List<Object[]> )query.getResultList();
		return CommitteeSubScheduleList;
	}

	
	private static final String AGENDASPECLIST="""
			SELECT  a.ScheduleMinutesId, a.Details,a.ScheduleId,a.MinutesId,a.ScheduleSubId,a.MinutesSubOfSubId,a.MinutesSubId,a.idarck,a.remarks,b.outcomename,c.agendaitem AS agenda, a.agendasubhead,c.scheduleAgendaId
			FROM committee_schedules_minutes_details a,committee_schedules_minutes_outcome b,committee_schedules_agenda c
			WHERE a.scheduleid =:InScheduleId  AND a.idarck=b.idarck AND c.scheduleagendaid=a.minutessubid AND a.minutesid='3'
			
			UNION
			
			SELECT  a.ScheduleMinutesId, a.Details,a.ScheduleId,a.MinutesId,a.ScheduleSubId,a.MinutesSubOfSubId,a.MinutesSubId,a.idarck,a.remarks,b.outcomename,
			CASE WHEN a.minutesid='4' THEN 'Other Discussion' ELSE 'Other Outcomes' END AS agenda, a.agendasubhead, NULL AS scheduleAgendaId
			FROM committee_schedules_minutes_details a,committee_schedules_minutes_outcome b
			WHERE a.scheduleid =:InScheduleId  AND a.idarck=b.idarck AND a.minutesid<>'3'
			ORDER BY  CASE WHEN minutesid = '3' THEN 1  ELSE 2 END, scheduleAgendaId, agenda,ScheduleMinutesId
			""";
	
	@Override
	public List<Object[]> CommitteeScheduleMinutes(String scheduleid) throws Exception{
		Query query=manager.createNativeQuery(AGENDASPECLIST);
		query.setParameter("InScheduleId", Long.parseLong(scheduleid));
		List<Object[]> CommitteeScheduleMinutes =(List<Object[]>)query.getResultList();
		return CommitteeScheduleMinutes;
	}

	@Override
	public List<Object[]> CommitteeMinutesSpecdetails()throws Exception
	{
		Query query=manager.createNativeQuery(COMMITTEEMINUTESSPECDETAILS);
		 List<Object[]> CommitteeMinutesSpecdetails=(List<Object[]>) query.getResultList();
		return CommitteeMinutesSpecdetails;
	}
	


	@Override
	public List<Object[]> CommitteeMinutesSub()throws Exception
	{
		Query query=manager.createNativeQuery(COMMITTEEMINUTESSUB);
		 List<Object[]> CommitteeMinutesSub=(List<Object[]>) query.getResultList();
		return CommitteeMinutesSub;
	}


	@Override
	public List<Object[]> CommitteeAttendance(String CommitteeScheduleId) throws Exception {

		Query query=manager.createNativeQuery(COMMITTEEATTENDANCE);
		query.setParameter("scheduleid", Long.parseLong(CommitteeScheduleId));
		List<Object[]> CommitteeAttendance=(List<Object[]>) query.getResultList();
		return CommitteeAttendance;
	}
	
	
	



	@Override
	public int MeetingAgendaApproval(CommitteeMeetingApproval approval, CommitteeSchedule schedule,List<PfmsNotification> notifications)
			throws Exception {
		manager.persist(approval);
		for(int i=0;i<notifications.size();i++) {
			manager.persist(notifications.get(i));
		}
		
		CommitteeSchedule ExistingCommitteeSchedule =manager.find(CommitteeSchedule.class, schedule.getScheduleId());
		if(ExistingCommitteeSchedule != null) {
			ExistingCommitteeSchedule.setScheduleFlag(schedule.getScheduleFlag());
			ExistingCommitteeSchedule.setModifiedBy(schedule.getModifiedBy());
			ExistingCommitteeSchedule.setModifiedDate(schedule.getModifiedDate());
			return 1;
		}
		else {
			return 0;
		}
		
	}

	@Override
	public List<Object[]> MeetingApprovalAgendaList(String EmpId) throws Exception {
				
		Query query=manager.createNativeQuery("CALL Pfms_Agenda_aproval_List(:empid);");
		query.setParameter("empid", Long.parseLong(EmpId));
		List<Object[]> MeetingApprovalAgendaList=(List<Object[]>) query.getResultList();
			
		return MeetingApprovalAgendaList;
		
	}
			
			


	@Override
	public int MeetingAgendaApprovalSubmit(CommitteeSchedule schedule, CommitteeMeetingApproval approval,PfmsNotification notification)throws Exception 
	{

		manager.persist(approval);
		manager.persist(notification);
		
		CommitteeSchedule ExistingCommitteeSchedule =manager.find(CommitteeSchedule.class, schedule.getScheduleId());
		if(ExistingCommitteeSchedule != null) {
			ExistingCommitteeSchedule.setScheduleFlag(schedule.getScheduleFlag());
			ExistingCommitteeSchedule.setModifiedBy(schedule.getModifiedBy());
			ExistingCommitteeSchedule.setModifiedDate(schedule.getModifiedDate());
			return 1;
		}
		else {
			return 0;
		}

	}

	
	@Override
	public Object[] CommitteeScheduleData(String committeescheduleid) throws Exception
	{
		Query query=manager.createNativeQuery(COMMITTEESCHEDULEDATA);
		query.setParameter("committeescheduleid", Long.parseLong(committeescheduleid));
		return (Object[] )query.getResultList().get(0);
	}
	@Override
	public List<Object[]> CommitteeAtendance(String committeescheduleid,String revisionNo) throws Exception
	{
		Query query= manager.createNativeQuery("Call Pfms_Committee_Invitation (:committeescheduleid,:revisionNo)");
		query.setParameter("committeescheduleid", Long.parseLong(committeescheduleid));
		query.setParameter("revisionNo", Long.parseLong(revisionNo));
		return (List<Object[]>)query.getResultList();
	}

	private static final String COMMITTEEINVITATIONCHECK="SELECT committeescheduleid, labcode, empid FROM committee_schedules_invitation WHERE committeescheduleid=:scheduleid AND labcode=:labcode AND empid=:empid";

	
	@Override
	public List<Object[]> CommitteeInvitationCheck(CommitteeInvitation committeeinvitation) throws Exception
	{
		Query query= manager.createNativeQuery(COMMITTEEINVITATIONCHECK);
		query.setParameter("scheduleid", committeeinvitation.getCommitteeScheduleId());
		query.setParameter("labcode", committeeinvitation.getLabCode() );
		query.setParameter("empid", committeeinvitation.getEmpId());
		
		return (List<Object[]>)query.getResultList();
	}
	
	
	@Override
	public long CommitteeInvitationCreate(CommitteeInvitation committeeinvitation) throws Exception {

	    CommitteeInvitation invitation = manager.merge(committeeinvitation);
	    manager.flush();

	    if (invitation.getRevisionNo() == 0 &&
	        (invitation.getParentInvitationId() == null || invitation.getParentInvitationId() == 0)) {
	        invitation.setParentInvitationId(invitation.getCommitteeInvitationId());
	    }

	    manager.flush();

	    return invitation.getCommitteeInvitationId();
	}
	
	 
	
	@Override
	public Object[] InvitationMaxSerialNo(String scheduleid) throws Exception
	{
		Query query=manager.createNativeQuery(INVITATIONMAXSERIALNO);
		query.setParameter("scheduleid", Long.parseLong(scheduleid));		
		Object[] InvitationMaxSerialNo=(Object[] )query.getResultList().get(0);
		return InvitationMaxSerialNo;		
	}
	
	@Override
	public Long CommitteeInvitationDelete(String committeeinvitationid) throws Exception
	{
		Query query=manager.createNativeQuery(COMMITTEEINVITATIONDELETE);
		query.setParameter("committeeinvitationid", Long.parseLong(committeeinvitationid));
		return (long) query.executeUpdate();
	}
	
	
	@Override
	public int CommitteeInvitationSerialNoUpdate(String committeeinvitationid,long serialno) throws Exception
	{
		
		CommitteeInvitation ExistingCommitteeInvitation = manager.find(CommitteeInvitation.class, Long.parseLong(committeeinvitationid));
		if(ExistingCommitteeInvitation != null) {
			ExistingCommitteeInvitation.setSerialNo(serialno);
			return 1;
		}
		else {
			return 0;
		}
	
	}
	
	
	
	
	@Override
	public List<Object[]> CommitteeInvitationSerialNoAfter(String committeeinvitationid) throws Exception
	{
		Query query=manager.createNativeQuery(COMMITTEEINVITATIONSERIALNOAFTER);
		query.setParameter("committeeinvitationid", Long.parseLong(committeeinvitationid));
		List<Object[]> CommitteeInvitationSerialNoAfter=(List<Object[]> )query.getResultList();
		return CommitteeInvitationSerialNoAfter;
	}
	
	

	@Override
	public List<String> CommitteeAttendanceList(String invitationId) throws Exception 
	{
		Query query=manager.createNativeQuery(COMMITTEEATTENDANCETYPE);
		query.setParameter("invitationid", Long.parseLong(invitationId));
		List<String> AttendanceList=(List<String>)query.getResultList();		
		return AttendanceList;
	}
	private static final String ATTENDANCEUPDATEN="UPDATE committee_schedules_invitation SET Attendance=:attendance "
			+ "WHERE CommitteeInvitationId=:invitationid";
	private static final String ATTENDANCEUPDATEP="UPDATE committee_schedules_invitation SET Attendance=:attendance "
			+ "WHERE CommitteeInvitationId=:invitationid";


	@Override
	public Long CommitteeAttendanceUpdate(String InvitationId, String Value) throws Exception 
	 {

		int count=0;
		
		if(Value.equalsIgnoreCase("P")) {
			
			CommitteeInvitation ExistingCommitteeInvitation = manager.find(CommitteeInvitation.class, Long.parseLong(InvitationId));
			if(ExistingCommitteeInvitation != null) {
				ExistingCommitteeInvitation.setAttendance("N");
				count=1;
			}
			else {
				count=0;
			}
			
		}
		if(Value.equalsIgnoreCase("N")) {
			CommitteeInvitation ExistingCommitteeInvitation = manager.find(CommitteeInvitation.class, InvitationId);
			if(ExistingCommitteeInvitation != null) {
				ExistingCommitteeInvitation.setAttendance("P");
				count=1;
			}
			else {
				count=0;
			}
		}
		
		return (long) count;
	}

	@Override
	public List<Object[]> ExpertList() throws Exception 
	{
		Query query=manager.createNativeQuery(EXPERTLIST);
		List<Object[]> ExpertList=(List<Object[]>)query.getResultList();
		return ExpertList;
	}



	@Override
	public long ScheduleMinutesUnitUpdate(CommitteeScheduleMinutesDetails minutesdetails) throws Exception {

		manager.persist(minutesdetails);
		manager.flush();
		
		return minutesdetails.getScheduleMinutesId();
	}


	@Override
	public List<Object[]> MinutesUnitList(String CommitteeScheduleId) throws Exception {

		Query query=manager.createNativeQuery(MINUTESUNITLIST);
		query.setParameter("committeescheduleid", Long.parseLong(CommitteeScheduleId));
		List<Object[]> MinutesUnitList=(List<Object[]> )query.getResultList();
		
		return MinutesUnitList;
	}
	
	@Override
	public List<Object[]> CommitteeAgendaPresenter(String scheduleid) throws Exception
	{
		Query query=manager.createNativeQuery(COMMITTEEAGENDAPRESENTER);
		query.setParameter("scheduleid", Long.parseLong(scheduleid));
		return (List<Object[]>)query.getResultList();
	}


	@Override
	public List<Object[]> ChaipersonEmailId(String CommitteeMainId) throws Exception {

		Query query=manager.createNativeQuery(CHAIRPERSONEMAIL);
		query.setParameter("committeemainid", Long.parseLong(CommitteeMainId));
		List<Object[]> ChaipersonEmailId=(List<Object[]>)query.getResultList();
		
		return ChaipersonEmailId;
	}
	
	@Override
	public Object[] ProjectDirectorEmail(String ProjectId) throws Exception {

		Query query=manager.createNativeQuery(PROJECTDIRECTOREMAIL);
		query.setParameter("projectid", Long.parseLong(ProjectId));
		Object[] ProjectDirectorEmail=(Object[])query.getSingleResult();
		
		return ProjectDirectorEmail;
	}
	
	@Override
	public Object[] RtmddoEmail() throws Exception {

		Query query=manager.createNativeQuery(RTMDDOEMAIL);
		try {
			Object[] RtmddoEmail=(Object[])query.getSingleResult();
			return RtmddoEmail;
		}catch (Exception e) {
			logger.error(new java.util.Date() +"Inside DAO RtmddoEmail "+e);
			return null;
		}
		
	}


	private static final String LASTPMRCDATEUPDATE ="UPDATE pfms_project_data SET lastpmrcdate =:lastpmrcdate WHERE projectid=:projectid ;";
	private static final String LASTEBDATEUPDATE ="UPDATE pfms_project_data SET lastebdate =:lastebdate WHERE projectid=:projectid ;";
	@Override
	public String UpdateOtp(CommitteeSchedule schedule) throws Exception {
		
		int ret=0;
		Query query;
		
		CommitteeSchedule ExistingCommitteeSchedule = manager.find(CommitteeSchedule.class, schedule.getScheduleId());
		if(ExistingCommitteeSchedule != null) {
			ExistingCommitteeSchedule.setKickOffOtp(schedule.getKickOffOtp());
			ExistingCommitteeSchedule.setScheduleFlag(schedule.getScheduleFlag());
			ret=1;
		}

		if(schedule.getScheduleFlag().equalsIgnoreCase("MKV")) 
		{
			Object[] scheduledata = CommitteeScheduleEditData(String.valueOf(schedule.getScheduleId()));
			if((scheduledata[8].toString().equalsIgnoreCase("PMRC") || scheduledata[8].toString().equalsIgnoreCase("PMRB")) && Long.parseLong(scheduledata[9].toString()) > 0) 
			{
				query = manager.createNativeQuery(LASTPMRCDATEUPDATE);
				query.setParameter("lastpmrcdate", scheduledata[2].toString());
				query.setParameter("projectid", scheduledata[9].toString());
				query.executeUpdate();
			}
			else if(scheduledata[8].toString().equalsIgnoreCase("EB") && Long.parseLong(scheduledata[9].toString()) > 0) 
			{
				query = manager.createNativeQuery(LASTEBDATEUPDATE);
				query.setParameter("lastebdate", scheduledata[2].toString());
				query.setParameter("projectid", scheduledata[9].toString());
				query.executeUpdate();
			}
		}
		
		
		if(schedule.getKickOffOtp()==null) {
			return String.valueOf(ret);
		}else {
			return schedule.getKickOffOtp();
		}
	}
	
	
	@Override
	public List<Object[]> LoginProjectDetailsList(String empid,String Logintype, String LabCode)throws Exception
	{
		Query query=manager.createNativeQuery("CALL Pfms_Emp_ProjectList(:empid,:logintype,:labcode);");
		query.setParameter("empid", Long.parseLong(empid));
		query.setParameter("logintype", Logintype);
		query.setParameter("labcode", LabCode);
		List<Object[]> LoginProjectIdList=(List<Object[]>)query.getResultList();
		return LoginProjectIdList;
	}
	
	
	
	@Override
	public Object[] projectdetails(String projectid) throws Exception
	{		
		Query query=manager.createNativeQuery(PROJECTDETAILS);
		query.setParameter("projectid",Long.parseLong(projectid));
		return (Object[]) query.getResultList().get(0);
	}
	
	@Override
	public List<Object[]> ProjectScheduleListAll(String projectid) throws Exception
	{
		Query query=manager.createNativeQuery(PROJECTSCHEDULELISTALL);
		query.setParameter("projectid",Long.parseLong(projectid));
		return (List<Object[]>) query.getResultList();
	}
	
	@Override
	public List<Object[]> ProjectApplicableCommitteeList(String projectid)throws Exception
	{
		Query query=manager.createNativeQuery(PROJECTAPPLICABLECOMMITTEELIST);
		query.setParameter("projectid",Long.parseLong(projectid));
		return (List<Object[]>)query.getResultList();
	}
	

	@Override
	public  int UpdateComitteeMainid(String committeemainid, String scheduleid ) throws Exception
	{
		CommitteeSchedule ExistingCommitteeSchedule = manager.find(CommitteeSchedule.class, Long.parseLong(scheduleid));
		if(ExistingCommitteeSchedule != null) {
			ExistingCommitteeSchedule.setCommitteeMainId(Long.parseLong(committeemainid));
			return 1;
		}
		else {
			return 0;
		}
		
	}
	
	@Override
	public List<Object[]> ProjectCommitteeScheduleListAll(String projectid,String committeeid) throws Exception
	{
		Query query=manager.createNativeQuery(PROJECTCOMMITTEESCHEDULELISTALL);
		query.setParameter("projectid", Long.parseLong(projectid));
		query.setParameter("committeeid", Long.parseLong(committeeid));
		return (List<Object[]>) query.getResultList();
	}


	@Override
	public String KickOffOtp(String CommitteeScheduleId) throws Exception {

		Query query=manager.createNativeQuery(KICKOFFOTP);
		query.setParameter("scheduleid", Long.parseLong(CommitteeScheduleId));
		String KickOffOtp=(String)query.getSingleResult();
		return KickOffOtp;
	}


	@Override
	public List<Object[]> UserSchedulesList(String EmpId,String MeetingId) throws Exception {

		Query query=manager.createNativeQuery("CALL Pfms_Schedule_Individual(:empid,:meetingid)");
		query.setParameter("empid",Long.parseLong(EmpId));
		query.setParameter("meetingid", MeetingId);
		
		
		return (List<Object[]>) query.getResultList();
	}
	
	@Override
	public List<Object[]> MeetingSearchList(String MeetingId ,String LabCode) throws Exception {

		Query query=manager.createNativeQuery(MEETINGSEARCHLIST);
		query.setParameter("meetingid",MeetingId);
		query.setParameter("labcode", LabCode);
		return (List<Object[]>) query.getResultList();
	}

	@Override
	public Object[] CommitteeScheduleDataPro(String committeescheduleid, String projectid) throws Exception
	{
		Query query=manager.createNativeQuery(COMMITTEESCHEDULEDATAPRO);
		query.setParameter("committeescheduleid", Long.parseLong(committeescheduleid));
		query.setParameter("projectid", Long.parseLong(projectid));
		return (Object[] )query.getResultList().get(0);
	}
	
	@Override
	public Object[] LabDetails(String LabCode)throws Exception
	{
		Query query=manager.createNativeQuery(LABDETAILS);
		query.setParameter("labcode", LabCode);
		Object[] Labdetails =(Object[])query.getSingleResult();
		return Labdetails ;
	}
	@Override
	public long ProjectCommitteeAdd(CommitteeProject committeeproject) throws Exception{
		
		manager.persist(committeeproject);
		manager.flush();
		return  committeeproject.getCommitteeProjectId();
	}

	@Override
	public long InitiationCommitteeAdd(CommitteeInitiation model) throws Exception
	{		
		manager.persist(model);
		manager.flush();
		return  model.getCommitteeInitiationId();
	}


	@Override
	public List<Object[]> ProjectMasterList(String ProjectId) throws Exception {
		
		Query query=manager.createNativeQuery(PROJECTMASTERLIST);
		query.setParameter("projectid", Long.parseLong(ProjectId));
		List<Object[]> ProjectMasterList=(List<Object[]>)query.getResultList();
		return ProjectMasterList;
	}
	
	
	
	@Override
	public  long ProjectCommitteeDelete(CommitteeProject committeeproject ) throws Exception
	{
		Query query=manager.createNativeQuery(COMMITTEEPROJECTDELETE);
		query.setParameter("committeeprojectid", committeeproject.getCommitteeProjectId());
		return query.executeUpdate();

	}

	@Override
	public List<Object[]> CommitteeAutoScheduleList(String ProjectId,String divisionid,String initiationid,String projectstatus) throws Exception {

		Query query=manager.createNativeQuery(COMMITTEEAUTOSCHEDULELIST);
		query.setParameter("projectid", ProjectId);
		query.setParameter("divisionid", divisionid);
		query.setParameter("initiationid", initiationid);
		query.setParameter("projectstatus", projectstatus  );
		List<Object[]> CommitteeAutoScheduleList=(List<Object[]>)query.getResultList();
		return CommitteeAutoScheduleList;
	}
	
	@Override
	public List<Object[]> CommitteeAutoScheduleList(String ProjectId,String committeeid, String divisionid, String initiationid,String projectstatus) throws Exception 
	{
		
		Query query=manager.createNativeQuery(COMMITTEEAUTOSCHEDULELIST1);
		query.setParameter("projectid", ProjectId);
		query.setParameter("committeeid", committeeid); 
		query.setParameter("divisionid", divisionid);
		query.setParameter("initiationid", initiationid);
		query.setParameter("projectstatus", projectstatus);
		List<Object[]> CommitteeAutoScheduleList=(List<Object[]>)query.getResultList();
		return CommitteeAutoScheduleList;
	}
	

	
	@Override
	public Object[] CommitteeLastScheduleDate(String committeeid) throws Exception 
	{
		Query query=manager.createNativeQuery(COMMITTEELASTSCHEDULEDATE);	
		query.setParameter("committeeid", Long.parseLong(committeeid));
		Object[] CommitteeLastScheduleDate=(Object[])query.getSingleResult();
		return CommitteeLastScheduleDate;
	}


	@Override
	public int CommitteeProjectUpdate(String ProjectId, String CommitteeId) throws Exception {

		Query query=manager.createNativeQuery(COMMITTEEPROJECTUPDATE);
		query.setParameter("projectid", Long.parseLong(ProjectId));
		query.setParameter("committeeid", Long.parseLong(CommitteeId));
		
		return query.executeUpdate();
	}


	@Override
	public Object[] CommitteMainMembersData(String CommitteeScheduleId, String membertype) throws Exception {

		Query query=manager.createNativeQuery(COMMITTEMAINMEMBERSDATA);
		query.setParameter("scheduleid", Long.parseLong(CommitteeScheduleId));
		query.setParameter("membertype", membertype);
		try {
		Object[] CommitteMainMembersData=(Object[])query.getSingleResult();
		return CommitteMainMembersData;
		}catch (Exception e) {
			logger.error(new java.util.Date() +"Inside DAO CommitteMainMembersData "+e);
			return null;
		}		
	}
	
	
	
	
	


	@Override
	public Object[] NotificationData(String ScheduleId, String EmpId,String Status) throws Exception {

		Query query=manager.createNativeQuery(NOTIFICATIONDATA);
		query.setParameter("scheduleid", Long.parseLong(ScheduleId));
		query.setParameter("empid", Long.parseLong(EmpId));
		query.setParameter("status", Status );
		Object[] NotificationData=null;
		
		try {
		 NotificationData=(Object[])query.getSingleResult();
		}catch (Exception e) {
			logger.error(new java.util.Date() +"Inside DAO NotificationData "+e);
			
		}
		
		return NotificationData;
	}
	

	@Override
	public Long MeetingCount(Date ScheduleDate,String ProjectId) throws Exception {

		if(ProjectId.equalsIgnoreCase("0")) {
			Query query=manager.createNativeQuery(MEETINGCOUNT);
			query.setParameter("scheduledate", ScheduleDate );
			query.setParameter("projectid", Long.parseLong(ProjectId));
			return (Long) query.getSingleResult();
			
		}
			
			Query query=manager.createNativeQuery(MEETINGCOUNT1);
			query.setParameter("projectid", ProjectId );
			return (Long) query.getSingleResult();
	}

	private static final String UPDATEMEETINGVENUE="UPDATE committee_schedule SET  meetingvenue=:meetingvenue ,"
			+ "confidential=:confidential, Reference=:reference, PMRCDecisions=:pmrcdecisions WHERE scheduleid=:scheduleid";

	@Override
	public int UpdateMeetingVenue(CommitteeScheduleDto csdto) throws Exception
	{
		
		CommitteeSchedule ExistingCommitteeSchedule = manager.find(CommitteeSchedule.class, csdto.getScheduleId());
		if(ExistingCommitteeSchedule != null) {
			ExistingCommitteeSchedule.setMeetingVenue(csdto.getMeetingVenue());
			ExistingCommitteeSchedule.setConfidential(csdto.getConfidential());
			ExistingCommitteeSchedule.setReference(csdto.getReferrence());
			ExistingCommitteeSchedule.setPMRCDecisions(csdto.getPMRCDecisions());
			return 1;
		}
		else {
			return 0;
		}
		
		
	}


	@Override
	public Long MinutesAttachmentAdd(CommitteeMinutesAttachment attachment) throws Exception {

		manager.persist(attachment);
		manager.flush();
		
		return attachment.getMinutesAttachmentId();
	}
	
	@Override
	public List<Object[]> MinutesAttachmentList(String scheduleid ) throws Exception 
	{
		Query query=manager.createNativeQuery(MINUTESATTACHMENTLIST);
		query.setParameter("scheduleid", Long.parseLong(scheduleid));
		List<Object[]> MinutesAttachmentList=null;
		MinutesAttachmentList=query.getResultList();
		return MinutesAttachmentList;
	}
	
	
	@Override
	public int MinutesAttachmentDelete(String attachid ) throws Exception 
	{
		Query query=manager.createNativeQuery(MINUTESATTACHMENTDELETE);
		query.setParameter("attachid", Long.parseLong(attachid));	
		return query.executeUpdate();
	}
	
	@Override
	public CommitteeMinutesAttachment MinutesAttachDownload(String attachmentid) throws Exception {

		CommitteeMinutesAttachment minutesattachment= manager.find(CommitteeMinutesAttachment.class, Long.parseLong(attachmentid));
		
		return minutesattachment;
		
	}
	
	@Override
	public List<Object[]> PfmsCategoryList() throws Exception {
		Query query=manager.createNativeQuery(PROJECTCATEGORYLIST);
		List<Object[]> PfmsCategoryList=(List<Object[]>)query.getResultList();		

		return PfmsCategoryList;
	}

	@Override
	public int MeetingMinutesApproval(CommitteeMeetingApproval approval, CommitteeSchedule schedule)
			throws Exception {
		manager.persist(approval);
		
		CommitteeSchedule ExistingCommitteeSchedule =manager.find(CommitteeSchedule.class, schedule.getScheduleId());
		if(ExistingCommitteeSchedule != null) {
			ExistingCommitteeSchedule.setScheduleFlag(schedule.getScheduleFlag());
			ExistingCommitteeSchedule.setModifiedBy(schedule.getModifiedBy());
			ExistingCommitteeSchedule.setModifiedDate(schedule.getModifiedDate());
			return 1;
		}
		else {
			return 0;
		}

	}

	@Override
	public long MeetingMinutesApprovalNotification(PfmsNotification notification)throws Exception
	{
		manager.persist(notification);
		return notification.getNotificationId();
	}
	
	
	
	@Override
	public List<Object[]> MeetingApprovalMinutesList(String EmpId) throws Exception {
		Query query=manager.createNativeQuery("CALL Pfms_Minutes_aproval_List(:empid)");
		query.setParameter("empid", Long.parseLong(EmpId));
		List<Object[]> MeetingApprovalMinutesList=(List<Object[]>) query.getResultList();			
		return MeetingApprovalMinutesList;
	}


	
	@Override
	public int MeetingMinutesApprovalSubmit(CommitteeSchedule schedule, CommitteeMeetingApproval approval,PfmsNotification notification)throws Exception {

		manager.persist(approval);
		manager.persist(notification);
		
		CommitteeSchedule ExistingCommitteeSchedule =manager.find(CommitteeSchedule.class, schedule.getScheduleId());
		if(ExistingCommitteeSchedule != null) {
			ExistingCommitteeSchedule.setScheduleFlag(schedule.getScheduleFlag());
			ExistingCommitteeSchedule.setModifiedBy(schedule.getModifiedBy());
			ExistingCommitteeSchedule.setModifiedDate(schedule.getModifiedDate());
			return 1;
		}
		else {
			return 0;
		}

	}
	
	@Override
	public List<Object[]> CommitteeAllAttendance(String CommitteeScheduleId) throws Exception {

		Query query=manager.createNativeQuery(COMMITTEEALLATTENDANCE);
		query.setParameter("scheduleid", Long.parseLong(CommitteeScheduleId));
		List<Object[]> CommitteeAttendance=(List<Object[]>) query.getResultList();
		return CommitteeAttendance;
	}


	@Override
	public List<Object[]> MeetingReports(String EmpId, String Term, String ProjectId,String divisionid,String initiationid,String logintype,String LabCode) throws Exception {

		Query query=manager.createNativeQuery("CALL Pfms_Meeting_Reports(:EmpId,:Term,:projectid,:divisionid, :initiationid,:logintype,:LabCode)");
		query.setParameter("EmpId", Long.parseLong(EmpId));
		query.setParameter("Term", Term);
		query.setParameter("projectid", ProjectId);
		query.setParameter("divisionid", divisionid);
		query.setParameter("initiationid", initiationid);
		query.setParameter("logintype", logintype);
		query.setParameter("LabCode", LabCode);
		
		List<Object[]> MeetingReports=(List<Object[]>) query.getResultList();
			
		return MeetingReports;
	}

	@Override
	public List<Object[]> MeetingReportListAll(String fdate, String tdate, String ProjectId) throws Exception {


		Query query=manager.createNativeQuery(MEETINGREPORTTOTAL);
		query.setParameter("fdate", fdate);
		query.setParameter("tdate", tdate);
		query.setParameter("ProjectId", ProjectId);

		List<Object[]> MeetingReportListAll=(List<Object[]>) query.getResultList();
		return MeetingReportListAll;
	}
	
	@Override
	public List<Object[]> MeetingReportListEmp(String fdate, String tdate, String ProjectId, String EmpId)	throws Exception {
		Query query=manager.createNativeQuery("CALL Pfms_Total_Meeting_Reports(:fdate,:tdate,:ProjectId,:EmpId)");
		query.setParameter("EmpId", Long.parseLong(EmpId));
		query.setParameter("fdate", fdate);
		query.setParameter("tdate", tdate);
		query.setParameter("ProjectId", ProjectId);
		

		List<Object[]> MeetingReportListEmp=(List<Object[]>) query.getResultList();
		return MeetingReportListEmp;
		
	}

	
	@Override
	public int UpdateCommitteeInvitationEmailSent(String committeescheduleid)throws Exception
	{
		Query query=manager.createNativeQuery(UPDATECOMMITTEEINVITATIONEMAILSENT);
		query.setParameter("committeescheduleid", Long.parseLong(committeescheduleid));
		int ret=0;
		ret=query.executeUpdate();
		return ret;
	}
	
	
	@Override
	public List<Object[]> MinutesViewAllActionList(String scheduleid) throws Exception {
		Query query=manager.createNativeQuery(MINUTESVIEWALLACTIONLIST);
		query.setParameter("scheduleid", Long.parseLong(scheduleid));
		List<Object[]>  MinutesViewAllActionList=(List<Object[]> )query.getResultList();

		return MinutesViewAllActionList;
	}
	
	
	@Override
	public List<Object[]> ProjectCommitteesList(String LabCode) throws Exception {
		Query query=manager.createNativeQuery(PROJECTCOMMITTEESLIST);		
		query.setParameter("LabCode", LabCode );
		List<Object[]>  ProjectCommitteesList=(List<Object[]> )query.getResultList();
		return ProjectCommitteesList;
	}
	
	@Override
	public List<Object[]> ProjectCommitteesListNotAdded(String projectid,String LabCode) throws Exception {
		Query query=manager.createNativeQuery(PROJECTCOMMITTEESLISTNOTADDED);
		query.setParameter("projectid", Long.parseLong(projectid));
		query.setParameter("LabCode", LabCode );
		List<Object[]>  ProjectCommitteesListNotAdded=(List<Object[]> )query.getResultList();
		return ProjectCommitteesListNotAdded;
	}
	
	@Override
	public List<Object[]> AllLabList() throws Exception 
	{
		Query query=manager.createNativeQuery(CLUSTERLABLIST);
		List<Object[]> ClusterLabList=(List<Object[]>)query.getResultList();
		return ClusterLabList;
	}
	
	private static final String CLUSTERLABS="SELECT cl.labid,cl.clusterid,cl.labname,cl.labcode FROM cluster_lab cl, cluster_lab cl2 WHERE cl2.clusterid=cl.clusterid AND  cl2.labcode=:LabCode ";
	
	@Override
	public List<Object[]> ClusterLabs(String LabCode) throws Exception 
	{
		Query query=manager.createNativeQuery(CLUSTERLABS);
		query.setParameter("LabCode", LabCode );
		List<Object[]> ClusterLabList=(List<Object[]>)query.getResultList();
		return ClusterLabList;
	}

	
	@Override
	public List<Object[]> ExternalEmployeeListFormation(String LabId ,String committeemainid) throws Exception {
		
		Query query=manager.createNativeQuery(EXTERNALEMPLOYEELISTFORMATION);
		query.setParameter("labid", LabId);
		query.setParameter("committeemainid", committeemainid);
		List<Object[]> ExternalEmployeeList=(List<Object[]>)query.getResultList();
		return ExternalEmployeeList;
	}
	

	
	@Override
	public List<Object[]> InternalEmployeeListFormation(String labcode ,String committeemainid) throws Exception {
		
		Query query=manager.createNativeQuery(INTERNALEMPLOYEELISTFORMATION);
		query.setParameter("labcode", labcode);
		query.setParameter("committeemainid", Long.parseLong(committeemainid));
		List<Object[]> ExternalEmployeeList=(List<Object[]>)query.getResultList();
		return ExternalEmployeeList;
	}
	
	
	
	@Override
	public List<Object[]> ChairpersonEmployeeList(String LabCode ,String committeemainid) throws Exception 
	{
		Query query=manager.createNativeQuery(CHAIRPERSONEMPLOYEELISTFORMATION);
		query.setParameter("labcode", LabCode);
		query.setParameter("committeemainid", Long.parseLong(committeemainid));
		List<Object[]> ChairpersonEmployeeListFormation=(List<Object[]>)query.getResultList();
		return ChairpersonEmployeeListFormation;
	}
	private static final String PRESENETERFORCOMMITTE="SELECT a.emp_id, CONCAT(IFNULL(CONCAT(a.title,' '),(IFNULL(CONCAT(a.salutation, ' '), ''))), a.emp_name) AS 'EmpName' ,a.emp_no,b.designation, a.lab_code FROM employee a,employee_desig b WHERE a.is_active=1 AND a.emp_status IN ('P','R','T') AND a.desig_id=b.desig_id AND CASE WHEN 'A'=:labcode THEN 1=1 ELSE a.lab_code=:labcode END ORDER BY a.sr_no = 0, a.sr_no";
	@Override
	public List<Object[]> PreseneterForCommitteSchedule(String LabCode)throws Exception
	{
		Query query=manager.createNativeQuery(PRESENETERFORCOMMITTE);
		query.setParameter("labcode", LabCode);
		List<Object[]> result=(List<Object[]>)query.getResultList();
		return result;
	}
	
	
	
	private static final String LABINFOCLUSTERLAB = "SELECT labid,clusterid,labname,labcode FROM cluster_lab WHERE labcode =:labcode";
	@Override
	public Object[] LabInfoClusterLab(String LabCode) throws Exception 
	{
		try {
			Query query=manager.createNativeQuery(LABINFOCLUSTERLAB);
			query.setParameter("labcode", LabCode);
			Object[] LabInfoClusterLab=(Object[])query.getSingleResult();
		
			return LabInfoClusterLab;
		}
		catch (NoResultException e) 
		{
			return null;
		}catch (Exception e) {
			logger.error(new java.util.Date() +"Inside DAO LabInfoClusterLab"+e);
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	private static final String CLUSTEREXPERTSLIST ="SELECT e.expertid,CONCAT(IFNULL(CONCAT(e.title,' '),''), e.expertname) AS 'expertname' ,e.expertno,'Expert' AS designation FROM expert e WHERE e.isactive=1  AND e.expertid NOT IN (SELECT  empid FROM committee_member  WHERE isactive=1 AND labcode='@EXP' AND committeemainid=:committeemainid AND membertype IN ('CH','CO')); ";
	@Override
	public List<Object[]> ClusterExpertsList(String committeemainid) throws Exception
	{
		try {
			Query query=manager.createNativeQuery(CLUSTEREXPERTSLIST);
			query.setParameter("committeemainid", Long.parseLong(committeemainid));
			List<Object[]> DGEmpData=(List<Object[]>)query.getResultList();
			return DGEmpData;
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new java.util.Date() +" Inside DAO ClusterExpertsList "+ e);
			return new ArrayList<Object[]>();
		}
	}
	
	private static final String CELISTFORCOMMITTESCHEDULE="SELECT e.expertid,e.expertname,e.expertno,'Expert' AS designation FROM expert e WHERE e.isactive=1  AND e.expertid NOT IN (SELECT  empid FROM committee_member  WHERE isactive=1 AND labcode='@EXP'  AND membertype IN ('CH','CO')); ";
	@Override
	public List<Object[]> ClusterExpertsListForCommitteeSchdule() throws Exception
	{
		try {
			Query query=manager.createNativeQuery(CELISTFORCOMMITTESCHEDULE);
		
			List<Object[]> DGEmpData=(List<Object[]>)query.getResultList();
			return DGEmpData;
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new java.util.Date() +" Inside DAO ClusterExpertsListForCommitteeSchdule "+ e);
			return new ArrayList<Object[]>();
		}
		
	}
	
//	@Override
//	public List<Object[]> ChairpersonExternalEmployeeList(String LabId ,String committeemainid) throws Exception {
//		
//		logger.info(new java.util.Date() +"Inside ChairpersonExternalEmployeeList");
//		Query query=manager.createNativeQuery(CHAIRPERSONEXTERNALEMPLOYEELIST);
//		query.setParameter("labid", LabId);
//		query.setParameter("committeemainid", committeemainid);
//		List<Object[]> ExternalEmployeeList=(List<Object[]>)query.getResultList();
//		return ExternalEmployeeList;
//	}
	
	
	@Override
	public List<Object[]> ExternalEmployeeListInvitations(String labcode ,String scheduleid) throws Exception {
		
		Query query=manager.createNativeQuery(EXTERNALEMPLOYEELISTINVITATIONS);
		query.setParameter("labcode", labcode);
		query.setParameter("scheduleid", Long.parseLong(scheduleid));
		List<Object[]> ExternalEmployeeList=(List<Object[]>)query.getResultList();
		return ExternalEmployeeList;
	}

	
	@Override
	public List<Object[]> ExternalMembersNotAddedCommittee(String committeemainid) throws Exception {
		Query query=manager.createNativeQuery(EXTERNALMEMBERSNOTADDEDCOMMITTEE);
		query.setParameter("committeemainid", Long.parseLong(committeemainid));
		List<Object[]>  ExternalMembersNotAddedCommittee=(List<Object[]> )query.getResultList();
		return ExternalMembersNotAddedCommittee;
	}
	
	

	@Override
	public List<Object[]> CommitteeAllMembers(String committeemainid) throws Exception {
		Query query=manager.createNativeQuery("CALL Pfms_Committee_All_Members(:committeemainid); ");
		query.setParameter("committeemainid", Long.parseLong(committeemainid));
		List<Object[]>  CommitteeAllMembers=(List<Object[]> )query.getResultList();
		return CommitteeAllMembers;
	}
	
	
	
	@Override
	public List<Object[]> EmployeeListNoInvitedMembers(String scheduleid,String LabCode) throws Exception {
		Query query=manager.createNativeQuery(EMPLOYEELISTNOINVITEDMEMBERS);
		query.setParameter("scheduleid", Long.parseLong(scheduleid));
		query.setParameter("LabCode", LabCode );
		List<Object[]>  EmployeeListNoInvitedMembers=(List<Object[]> )query.getResultList();
		return EmployeeListNoInvitedMembers;
	}
	
	
	@Override
	public List<Object[]> ExternalMembersNotInvited(String scheduleid) throws Exception {
		Query query=manager.createNativeQuery(EXPERTLISTNOINVITEDMEMBERS);
		query.setParameter("scheduleid", Long.parseLong(scheduleid));
		List<Object[]>  ExpertListNoInvitedMembers=(List<Object[]> )query.getResultList();
		return ExpertListNoInvitedMembers;
	}
	
	@Override
	public Object[] ProjectBasedMeetingStatusCount(String projectid) throws Exception
	{
		Query query=manager.createNativeQuery(" CALL Pfms_Meetings_Status_Count(:projectid);");
		query.setParameter("projectid", Long.parseLong(projectid));
		Object[] ProjectBasedMeetingStatusCount = (Object[]) query.getSingleResult();
		return ProjectBasedMeetingStatusCount;
	}
	
	 
	
		
	@Override
	public List<Object[]> allprojectdetailsList() throws Exception {
		
		Query query=manager.createNativeQuery(ALLPROJECTDETAILSLIST);		
		return (List<Object[]>)query.getResultList();
	}
	
	@Override
	public List<Object[]> PfmsMeetingStatusWiseReport(String projectid,String statustype) throws Exception
	{		
		Query query=manager.createNativeQuery("CALL Pfms_Meeting_Status_Wise_Report (:projectid,:statustype);");
		query.setParameter("projectid", Long.parseLong(projectid));
		query.setParameter("statustype", statustype);
		List<Object[]> PfmsMeetingStatusWiseReport = (List<Object[]>)query.getResultList();
		return PfmsMeetingStatusWiseReport;
	}
	
	
	
	@Override
	public List<Object[]> ProjectCommitteeFormationCheckList(String projectid) throws Exception
	{		
		Query query=manager.createNativeQuery(PROJECTCOMMITTEEFORMATIONCHECKLIST);
		query.setParameter("projectid", Long.parseLong(projectid));
		List<Object[]> ProjectCommitteeFormationCheckList = (List<Object[]>)query.getResultList();
		return ProjectCommitteeFormationCheckList;
	}
	
	
	@Override
	public Object[] ProjectCommitteeDescriptionTOR(String projectid,String Committeeid) throws Exception
	{		
		Query query=manager.createNativeQuery(PROJECTCOMMITTEEDESCRIPTIONTOR);
		query.setParameter("committeeid",Long.parseLong(Committeeid));
		query.setParameter("projectid", Long.parseLong(projectid));
		Object[] ProjectCommitteeFormationCheckList = (Object[])query.getSingleResult();
		return ProjectCommitteeFormationCheckList;
	}
	
	
	@Override
	public Object[] DivisionCommitteeDescriptionTOR(String divisionid,String Committeeid) throws Exception
	{		
		Query query=manager.createNativeQuery(DIVISIONCOMMITTEEDESCRIPTIONTOR);
		query.setParameter("committeeid", Long.parseLong(Committeeid));
		query.setParameter("divisionid", Long.parseLong(divisionid));
		Object[] DivisionCommitteeDescriptionTOR = (Object[])query.getSingleResult();
		return DivisionCommitteeDescriptionTOR;
	}
	

	@Override
	public int ProjectCommitteeDescriptionTOREdit( CommitteeProject  committeeproject ) throws Exception
	{	
		CommitteeProject ExistingCommitteeProject = manager.find(CommitteeProject.class, committeeproject.getCommitteeProjectId());
		if(ExistingCommitteeProject != null) {
			System.err.println("working");
			ExistingCommitteeProject.setDescription(committeeproject.getDescription());
			ExistingCommitteeProject.setTermsOfReference(committeeproject.getTermsOfReference());
			ExistingCommitteeProject.setModifiedBy(committeeproject.getModifiedBy());
			ExistingCommitteeProject.setModifiedDate(committeeproject.getModifiedDate());
			return 1;
		}
		else {
			return 0;
		}
		
		
	}
	

	@Override
	public int DivisionCommitteeDescriptionTOREdit(CommitteeDivision committeedivision) throws Exception
	{	
		CommitteeDivision ExistingCommitteeDivision= manager.find(CommitteeDivision.class, committeedivision.getCommitteeDivisionId());
		if(ExistingCommitteeDivision != null) {
			ExistingCommitteeDivision.setDescription(committeedivision.getDescription());
			ExistingCommitteeDivision.setTermsOfReference(committeedivision.getTermsOfReference());
			ExistingCommitteeDivision.setModifiedBy(committeedivision.getModifiedBy());
			ExistingCommitteeDivision.setModifiedDate(committeedivision.getModifiedDate());
			return 1;
		}
		else {
			return 0;
		}
		
		
	}	
	
	


	@Override
	public  CommitteeScheduleAgenda CommitteePreviousAgendaGet(String agendaid) throws Exception
	{
		CommitteeScheduleAgenda scheduleagenda= manager.find(CommitteeScheduleAgenda.class,Long.parseLong(agendaid));
		return scheduleagenda;
	}

	@Override
	public int ScheduleMinutesUnitUpdate(String UnitId, String Unit, String UserId,String dt) throws Exception {
		Query query=manager.createNativeQuery(UPDATEUNIT);
		query.setParameter("unitid", Long.parseLong(UnitId));
		query.setParameter("unitname",Unit);	
		query.setParameter("createdby", UserId);	
		query.setParameter("createddate", dt);	
		int ScheduleMinutesUnitUpdate = query.executeUpdate();
		return ScheduleMinutesUnitUpdate;
	}

	
	@Override
	public List<Object[]> divisionList() throws Exception {
		Query query=manager.createNativeQuery(DIVISIONLIST);
		
		List<Object[]> divisionList=(List<Object[]>)query.getResultList();	
		return divisionList;
	}
	
		@Override
	public List<Object[]> LoginDivisionList(String empid) throws Exception {
		Query query=manager.createNativeQuery(LOGINDIVISIONLIST);
		query.setParameter("empid", Long.parseLong(empid));
		List<Object[]> divisionList=(List<Object[]>)query.getResultList();	
		return divisionList;
	}
	
	@Override
	public List<Object[]> CommitteedivisionAssigned(String divisionid) throws Exception
	{
		Query query=manager.createNativeQuery(COMMITTEEDIVISIONASSIGNED);
		query.setParameter("divisionid", Long.parseLong(divisionid));	
		List<Object[]> CommitteedivisionAssigned=(List<Object[]>)query.getResultList();	
		return CommitteedivisionAssigned;
	}
	
	
	
	@Override
	public List<Object[]> CommitteedivisionNotAssigned(String divisionid, String LabCode ) throws Exception
	{
		Query query=manager.createNativeQuery(COMMITTEEDIVISIONNOTASSIGNED);
		query.setParameter("divisionid", Long.parseLong(divisionid));
		query.setParameter("LabCode", LabCode);
		List<Object[]> CommitteedivisionNotAssigned=(List<Object[]>)query.getResultList();	
		return CommitteedivisionNotAssigned;
		
	}
	
	@Override
	public long DivisionCommitteeAdd(CommitteeDivision committeedivision) throws Exception
	{		
		manager.persist(committeedivision);
		manager.flush();		
		return  committeedivision.getCommitteeDivisionId();
	}
	
	
	
	
	@Override
	public List<Object[]> DivisionCommitteeFormationCheckList(String divisionid) throws Exception
	{		
		Query query=manager.createNativeQuery(DIVISIONCOMMITTEEFORMATIONCHECKLIST);
		query.setParameter("divisionid", Long.parseLong(divisionid));
		List<Object[]> CommitteeFormationCheckList = (List<Object[]>)query.getResultList();
		return CommitteeFormationCheckList;
	}
	
	
	@Override
	public  long DivisionCommitteeDelete(CommitteeDivision committeedivision ) throws Exception
	{
		Query query=manager.createNativeQuery(DIVISIONCOMMITTEEDELETE);
		query.setParameter("committeedivisionid", committeedivision.getCommitteeDivisionId());				
		return query.executeUpdate();

	}
	

	@Override
	public  Object[] DivisionData(String divisionid) throws Exception
	{
		Query query=manager.createNativeQuery(DIVISIONDATA);
		try {
		query.setParameter("divisionid", Long.parseLong(divisionid));		
		return (Object[])query.getSingleResult();
		}catch (Exception e) {
			logger.error(new java.util.Date() +"Inside DAO DivisionData "+e);
			return null;
		}
	}
	

	

	
	@Override
	public List<Object[]> DivisionCommitteeMainList(String divisionid) throws Exception 
	{
		Query query=manager.createNativeQuery(DIVISIONCOMMITTEEMAINLIST);
		query.setParameter("divisionid", Long.parseLong(divisionid));
		List<Object[]> DivisionCommitteeMainList=(List<Object[]>)query.getResultList();
		return DivisionCommitteeMainList;
	}
	
	
	@Override
	public List<Object[]> DivisionScheduleListAll(String divisionid) throws Exception
	{
		Query query=manager.createNativeQuery(DIVISIONSCHEDULELISTALL);
		query.setParameter("divisionid", Long.parseLong(divisionid));
		return (List<Object[]>) query.getResultList();
	}

	
	@Override
	public List<Object[]> DivisionCommitteeScheduleList(String divisionid,String committeeid) throws Exception
	{
		Query query=manager.createNativeQuery(DIVISIONCOMMITTEESCHEDULELIST);
		query.setParameter("divisionid", Long.parseLong(divisionid));
		query.setParameter("committeeid", Long.parseLong(committeeid));
		return (List<Object[]>) query.getResultList();
	}

	
	
	@Override
	public List<Object[]> DivisionMasterList(String divisionid) throws Exception {
		
		Query query=manager.createNativeQuery(DIVISIONMASTERLIST);
		query.setParameter("divisionid", Long.parseLong(divisionid));
		List<Object[]> divisionmasterlist=null;
		try {
			divisionmasterlist=(List<Object[]>)query.getResultList();
		}catch (Exception e) {
			logger.error(new java.util.Date() +"Inside DAO DivisionMasterList "+e);
			return null;
		}
		return divisionmasterlist;
	}

	
	
	@Override
	public List<Object[]> DivCommitteeAutoScheduleList(String divisionid) throws Exception {

		Query query=manager.createNativeQuery(DIVCOMMITTEEAUTOSCHEDULELIST);
		query.setParameter("divisionid", Long.parseLong(divisionid));
		List<Object[]> divcommitteeautoschedulelist=(List<Object[]>)query.getResultList();
		return divcommitteeautoschedulelist;
	}
	
	
	@Override
	public List<Object[]> CommitteeActionList(String EmpId) throws Exception {
		
		Query query=manager.createNativeQuery(COMMITTEEACTIONDATA);
		query.setParameter("scheduleid", Long.parseLong(EmpId));
		List<Object[]> AssignedList=(List<Object[]>)query.getResultList();	
		return AssignedList;
	}

	
	@Override
	public int CommitteeDivisionUpdate(String divisionid, String CommitteeId) throws Exception 
	{
		Query query=manager.createNativeQuery(COMMITTEEDIVISIONUPDATE);
		query.setParameter("divisionid", Long.parseLong(divisionid));
		query.setParameter("committeeid", Long.parseLong(CommitteeId));	
		
		return query.executeUpdate();
	}
	
	


	@Override
	public List<Object[]> MinutesOutcomeList() throws Exception {
		Query query=manager.createNativeQuery(OUTCOMELIST);
		List<Object[]> AssignedList=(List<Object[]>)query.getResultList();	
		return AssignedList;
	}
	
	
	@Override
	public List<Object[]> InitiatedProjectDetailsList()throws Exception
	{
		Query query=manager.createNativeQuery(INITIATEDPROJECTDETAILSLIST);
		List<Object[]> InitiatedProjectDetailsList=(List<Object[]>)query.getResultList();
		return InitiatedProjectDetailsList;
	}
	
	
	
	
	@Override
	public List<Object[]> InitiationMasterList(String initiationid) throws Exception {
		
		Query query=manager.createNativeQuery(INITIATIONMASTERLIST);
		query.setParameter("initiationid", Long.parseLong(initiationid));
		List<Object[]> InitiationMasterList=(List<Object[]>)query.getResultList();
		return InitiationMasterList;
	}
	
	@Override
	public List<Object[]> InitiationCommitteeFormationCheckList(String initiationid) throws Exception
	{		
		Query query=manager.createNativeQuery(INITIATIONCOMMITTEEFORMATIONCHECKLIST);
		query.setParameter("initiationid", Long.parseLong(initiationid));		
		List<Object[]> InitiationCommitteeFormationCheckList = (List<Object[]>)query.getResultList();
		return InitiationCommitteeFormationCheckList;
	}
	
	
	@Override
	public List<Object[]> InitiationCommitteesListNotAdded(String initiationid,String LabCode) throws Exception {
		Query query=manager.createNativeQuery(INITIATIONCOMMITTEESLISTNOTADDED);
		query.setParameter("initiationid", Long.parseLong(initiationid));
		query.setParameter("LabCode", LabCode );
		List<Object[]>  InitiationCommitteesListNotAdded=(List<Object[]> )query.getResultList();
		return InitiationCommitteesListNotAdded;
	}
	
	
	
	
	@Override
	public int InvitationSerialnoUpdate(String invitationid,String newslno) throws Exception
	{
			Query query =manager.createNativeQuery(INVITATIONSERIALNOUPDATE);
			query.setParameter("newslno", Long.parseLong(newslno));
			query.setParameter("invitationid", Long.parseLong(invitationid));
			int count=(int)query.executeUpdate();
			
			return count;
	}
	
	
	@Override
	public List<Object[]> CommitteeRepList() throws Exception
	{
			Query query =manager.createNativeQuery(COMMITTREPLIST);
			List<Object[]> CommittRepList=(List<Object[]>)query.getResultList();
			return CommittRepList;
	}
	
	@Override
	public long CommitteeRepMembersSubmit(CommitteeMemberRep memreps) throws Exception {
		
		manager.persist(memreps);		
		return memreps.getMemberRepId();
	}
	
	
	
	@Override
	public List<Object[]> CommitteeMemberRepList(String committeemainid) throws Exception
	{
			Query query =manager.createNativeQuery(COMMITTEEMEMBERREPLIST);
			query.setParameter("committeemainid", Long.parseLong(committeemainid));
			List<Object[]> CommitteeMemberRepList=(List<Object[]>)query.getResultList();
			return CommitteeMemberRepList;
	}
	
	@Override
	public List<Object[]> CommitteeRepNotAddedList(String committeemainid) throws Exception
	{
			Query query =manager.createNativeQuery(COMMITTEEREPNOTADDEDLIST);
			query.setParameter("committeemainid", Long.parseLong(committeemainid));
			List<Object[]> CommitteeRepNotAddedList=(List<Object[]>)query.getResultList();
			return CommitteeRepNotAddedList;
	}
	
	
	
	@Override
	public int CommitteeMemberRepDelete(String memberrepid) throws Exception
	{
			Query query =manager.createNativeQuery(COMMITTEEMEMBERREPDELETE);
			query.setParameter("memberrepid", Long.parseLong(memberrepid));
			return query.executeUpdate();
	}
	
	@Override
	public List<Object[]> CommitteeAllMembersList(String committeemainid) throws Exception
	{
			Query query =manager.createNativeQuery("CALL Pfms_Committee_All_Members(:committeemainid);");
			query.setParameter("committeemainid", Long.parseLong(committeemainid));
			return query.getResultList();
	}
	
	
	@Override
	public List<Object[]> EmployeeListWithoutMembers(String committeemainid,String LabCode) throws Exception {
		Query query=manager.createNativeQuery(EMPLOYEELISTWITHOUTMEMBERS);
		query.setParameter("committeemainid",Long.parseLong(committeemainid));
		query.setParameter("labcode", LabCode);
		List<Object[]> EmployeeList=(List<Object[]>)query.getResultList();	
		return EmployeeList;
	}
	

	@Override
	public int CommitteeMemberUpdate(CommitteeMember model) throws Exception {
		CommitteeMember ExistingCommitteeMember = manager.find(CommitteeMember.class, model.getCommitteeMemberId());
		if(ExistingCommitteeMember != null) {
			ExistingCommitteeMember.setLabCode(model.getLabCode());
			ExistingCommitteeMember.setEmpId(model.getEmpId());
			ExistingCommitteeMember.setModifiedBy(model.getModifiedBy());
			ExistingCommitteeMember.setModifiedDate(model.getModifiedDate());
			return 1;
		}
		else {
			return 0;
		}
		
		
	}
	
	

	@Override
	public Object[] CommitteMainData(String committeemainid) throws Exception 
	{
		Query query=manager.createNativeQuery(COMMITTEMAINDATA);
		query.setParameter("committeemainid", Long.parseLong(committeemainid));		
		try {
		Object[] CommitteeAutoScheduleList=(Object[])query.getSingleResult();
		return CommitteeAutoScheduleList;
		}catch (Exception e) {
			logger.error(new java.util.Date() +"Inside DAO CommitteMainData "+e);
			return null;
		}		
	}
	
	
	
	@Override
	public  long InitiationCommitteeDelete(CommitteeInitiation model) throws Exception
	{
		Query query=manager.createNativeQuery(INITIATIONCOMMITTEEDELETE);
		query.setParameter("committeeinitiationid", model.getCommitteeInitiationId());
		return query.executeUpdate();
	}
	
	
	
	@Override
	public Object[] Initiationdetails(String initiationid) throws Exception
	{		
		Query query=manager.createNativeQuery(INITIATIONDETAILS);
		try {
		query.setParameter("initiationid", Long.parseLong(initiationid));
		return (Object[]) query.getResultList().get(0);
		}catch (Exception e) {
			logger.error(new java.util.Date() +"Inside DAO Initiationdetails "+ e);
			return null;	
		}
		
	}
	
	
	@Override
	public Object[] InitiationCommitteeDescriptionTOR(String initiationid,String Committeeid) throws Exception
	{		
		Query query=manager.createNativeQuery(INITIATIONCOMMITTEEDESCRIPTIONTOR);
		query.setParameter("committeeid", Long.parseLong(Committeeid));
		query.setParameter("initiationid", Long.parseLong(initiationid));
		Object[] InitiationCommitteeDescriptionTOR = (Object[])query.getSingleResult();
		return InitiationCommitteeDescriptionTOR;
	}
	
	@Override
	public int InitiationCommitteeDescriptionTOREdit(CommitteeInitiation committeedivision) throws Exception
	{		
		CommitteeInitiation ExistingCommitteeInitiation= manager.find(CommitteeInitiation.class, committeedivision.getCommitteeInitiationId());
		if(ExistingCommitteeInitiation != null) {
			ExistingCommitteeInitiation.setDescription(committeedivision.getDescription());
			ExistingCommitteeInitiation.setTermsOfReference(committeedivision.getTermsOfReference());
			ExistingCommitteeInitiation.setModifiedBy(committeedivision.getModifiedBy());
			ExistingCommitteeInitiation.setModifiedDate(committeedivision.getModifiedDate());
			return 1;
		}
		else {
			return 0;
		}
		
	}
	
	
	@Override
	public List<Object[]> InitiaitionMasterList(String initiationid) throws Exception 
	{		
		Query query=manager.createNativeQuery(INITIAITIONMASTERLIST);
		query.setParameter("initiationid",  Long.parseLong(initiationid));
		List<Object[]> InitiaitionMasterList=(List<Object[]>)query.getResultList();
		return InitiaitionMasterList;
	}
	
	
	@Override
	public int CommitteeInitiationUpdate(String initiationid, String CommitteeId) throws Exception 
	{
		Query query=manager.createNativeQuery(COMMITTEEINITIATIONUPDATE);
		query.setParameter("initiationid", Long.parseLong(initiationid));
		query.setParameter("committeeid", Long.parseLong(CommitteeId));		
		return query.executeUpdate();
	}
	
	@Override
	public List<Object[]> InitiationCommitteeMainList(String initiationid) throws Exception 
	{
		Query query=manager.createNativeQuery(INITIATIONCOMMITTEEMAINLIST);
		query.setParameter("initiationid", Long.parseLong(initiationid));	
		List<Object[]> InitiationCommitteeMainList=(List<Object[]>)query.getResultList();
		return InitiationCommitteeMainList;
	}
	
	
	@Override
	public List<Object[]> InitiationScheduleListAll(String initiationid) throws Exception
	{
		Query query=manager.createNativeQuery(INITIATIONSCHEDULELISTALL);
		query.setParameter("initiationid", Long.parseLong(initiationid));
		return (List<Object[]>) query.getResultList();
	}
	
	
	
	@Override
	public List<Object[]> InitiationCommitteeScheduleList(String initiationid,String committeeid) throws Exception
	{
		Query query=manager.createNativeQuery(INITIATIONCOMMITTEESCHEDULELIST);
		query.setParameter("initiationid",Long.parseLong(initiationid));
		query.setParameter("committeeid",Long.parseLong(committeeid));
		return (List<Object[]>) query.getResultList();
	}
	
	
	@Override
	public Object[] ProposedCommitteeMainId(String committeemainid) throws Exception
	{
		Query query=manager.createNativeQuery(PROPOSEDCOMMITTEEMAINID);
		try {
			query.setParameter("committeemainid", Long.parseLong(committeemainid));
			return (Object[]) query.getSingleResult();
		}catch (Exception e) {
			logger.error(new java.util.Date() +"Inside DAO ProposedCommitteeMainId "+e);
			return null;	
		}
		
	}
	
	
	@Override
	public Object[] GetProposedCommitteeMainId(String committeeid,String projectid,String divisionid,String initiationid) throws Exception
	{
		Query query=manager.createNativeQuery(GETPROPOSEDCOMMITTEEMAINID);
		try {
			query.setParameter("committeeid",Long.parseLong(committeeid));
			query.setParameter("projectid", Long.parseLong(projectid));
			query.setParameter("divisionid", Long.parseLong(divisionid));
			query.setParameter("initiationid", Long.parseLong(initiationid));
			return (Object[]) query.getResultList().get(0);
		}catch (Exception e) {
			logger.error(new java.util.Date() +"Inside DAO GetProposedCommitteeMainId "+e);
			return null;	
		}
		
	}
	
	
	
	@Override
	public Object[] CommitteeMainApprovalData(String committeemainid) throws Exception
	{
		Query query=manager.createNativeQuery(COMMITTEEMAINAPPROVALDATA);
		try {
			query.setParameter("committeemainid", Long.parseLong(committeemainid));
			return (Object[]) query.getResultList().get(0);
		}catch (Exception e) {
			logger.error(new java.util.Date() +"Inside DAO CommitteeMainApprovalData "+e);
			return null;	
		}		
	}	
	
	@Override
	public long CommitteeConstitutionApprovalAdd(CommitteeConstitutionApproval model) throws Exception
	{		
		manager.persist(model);		
		return model.getConstitutionApprovalId();
	}
	
	
	
	@Override
	public int CommitteeApprovalUpdate(CommitteeConstitutionApproval model) throws Exception
	{
		Query query=manager.createNativeQuery(COMMITTEEAPPROVALUPDATE);
		try {
			query.setParameter("committeemainid",model.getCommitteeMainId());
			query.setParameter("constitutionstatus",model.getConstitutionStatus());
			//query.setParameter("emplabid",model.getEmpLabid());
			//query.setParameter("empid",model.getEmpid());
			query.setParameter("actionby",model.getActionBy());
			query.setParameter("actiondate",model.getActionDate());
			query.setParameter("remarks",model.getRemarks());
			
			return query.executeUpdate();
		}catch (Exception e) {
			logger.error(new java.util.Date() +"Inside DAO CommitteeApprovalUpdate "+ e);
			return 0;	
		}
		
	}
	
	
	@Override
	public int updatecommitteeapprovalauthority(CommitteeConstitutionApproval model) throws Exception
	{
		
		try {
			Query query=manager.createNativeQuery(UPDATECOMMITTEEAPPROVALAUTHORITY);
			query.setParameter("approvalauthority",model.getApprovalAuthority());
			query.setParameter("committeemainid",model.getCommitteeMainId());
			
			return query.executeUpdate();
		}catch (Exception e) {
			logger.error(new java.util.Date() +"Inside DAO updatecommitteeapprovalauthority "+ e);
			return 0;	
		}
		
	}
		
	@Override
	public List<Object[]> ProposedCommitteList() throws Exception
	{
		Query query=manager.createNativeQuery(PROPOSEDCOMMITTELIST);
		return (List<Object[]>)query.getResultList();
	}
	
	
	@Override
	public List<Object[]> ApprovalStatusList(String committeemainid ) throws Exception
	{
		
		try {			
			Query query=manager.createNativeQuery(APPROVALSTATUSLIST);
			query.setParameter("committeemainid", Long.parseLong(committeemainid));
			List<Object[]> ApprovalStatusList =(List<Object[]>) query.getResultList();
			return ApprovalStatusList ;
		}catch (Exception e) {
			logger.error(new java.util.Date() +"Inside DAO ApprovalStatusList "+ e);
			return null;	
		}
		
		
		
	}
	
	
	
	@Override
	public int NewCommitteeMainIsActiveUpdate(CommitteeConstitutionApprovalDto dto ) throws Exception
	{
		try {	
			Query query=manager.createNativeQuery(NEWCOMMITTEEMAINISACTIVEUPDATE);
			query.setParameter("committeemainid",dto.getCommitteeMainId());
			query.setParameter("modifiedby",dto.getActionBy());
			query.setParameter("modifieddate",dto.getActionDate());
			return query.executeUpdate();
		}catch (Exception e) {
			logger.error(new java.util.Date() +"Inside DAO NewCommitteeMainIsActiveUpdate "+ e);
			e.printStackTrace();
			return 0;	
		}
	}
	
	
	@Override
	public List<Object[]> ProposedCommitteesApprovalList(String logintype,String empid) throws Exception
	{
		Query query=manager.createNativeQuery("CALL Pfms_Com_Con_Approval(:logintype, :empid);");
		query.setParameter("logintype",logintype);
		query.setParameter("empid", Long.parseLong(empid));
		return (List<Object[]>)query.getResultList();
	}
	
	@Override
	public Object[] LoginData(String loginid) throws Exception
	{
		Query query=manager.createNativeQuery(LOGINDATA);
		query.setParameter("loginid", Long.parseLong(loginid));
		return (Object[])query.getSingleResult();
	}
	
	@Override
	public List<Object[]> DORTMDData() throws Exception
	{
		Query query=manager.createNativeQuery(DORTMDDATA);
		return (List<Object[]>)query.getResultList();
	}
	
	@Override
	public long ConstitutionApprovalHistoryAdd(CommitteeConstitutionHistory model) throws Exception
	{		
		manager.persist(model);		
		return model.getCommitteeHistoryId();
	}
	
	@Override
	public List<Object[]>  ComConstitutionApprovalHistory(String committeemainid) throws Exception
	{
		Query query=manager.createNativeQuery(COMCONSTITUTIONAPPROVALHISTORY);
		query.setParameter("committeemainid", Long.parseLong(committeemainid));
		return (List<Object[]>)query.getResultList();
	}
	

	@Override
	public Object[]  ComConstitutionEmpdetails(String committeemainid) throws Exception
	{
		Query query=manager.createNativeQuery(COMCONSTITUTIONEMPDETAILS);
		query.setParameter("committeemainid", Long.parseLong(committeemainid));
		try {
			return (Object[])query.getSingleResult();
		}catch (Exception e) {
			logger.error(new java.util.Date() +"Inside DAO ComConstitutionEmpdetails "+ e);
			return null;
		}
	}
	
	
	@Override
	public Object[]  DoRtmdAdEmpData() throws Exception
	{
		Query query=manager.createNativeQuery(DORTMDADEMPDATA);
		try {
			return (Object[])query.getResultList().get(0);
		}catch (Exception e) {
			logger.error(new java.util.Date() +"Inside DAO DoRtmdAdEmpData "+ e);
			return null;
		}
	}
	
	
	@Override
	public Object[]  DirectorEmpData(String LabCode) throws Exception
	{
		try {
			Query query=manager.createNativeQuery(DIRECTOREMPDATA);
			query.setParameter("LabCode",LabCode);
			return (Object[])query.getResultList().get(0);
		}catch (Exception e) {
			logger.error(new java.util.Date() +"Inside DAO DirectorEmpData "+ e);
			return null;
		}
	}
	
	
	@Override
	public Object[]  CommitteeMainApprovalDoData(String committeemainid) throws Exception
	{
		Query query=manager.createNativeQuery(COMMITTEEMAINAPPROVALDODATA);
		query.setParameter("committeemainid", Long.parseLong(committeemainid));
		try {
			return (Object[])query.getSingleResult();
		}catch (Exception e) {
			logger.error(new java.util.Date() +"Inside DAO CommitteeMainApprovalDoData "+ e);
			return null;
		}
		
	}
	
	
	
	
	@Override
	public int  CommitteeMinutesDelete(String scheduleminutesid) throws Exception
	{
		Query query=manager.createNativeQuery(COMMITTEEMINUTESDELETE);
		query.setParameter("scheduleminutesid", Long.parseLong(scheduleminutesid));
		return query.executeUpdate();
	}
	
	
	@Override
	public Object[]  CommitteeConStatusDetails(String status) throws Exception
	{
		Query query=manager.createNativeQuery(COMMITTEECONSTATUSDETAILS);
		query.setParameter("status",status);
		return (Object[])query.getSingleResult();
	}
	
	

	@Override
	public int  CommitteeScheduleDelete(CommitteeScheduleDto dto) throws Exception
	{
		
		CommitteeSchedule ExistingCommitteeSchedule = manager.find(CommitteeSchedule.class, dto.getScheduleId());
		if(ExistingCommitteeSchedule != null) {
			ExistingCommitteeSchedule.setModifiedBy(dto.getModifiedBy());
			ExistingCommitteeSchedule.setModifiedDate(dto.getModifiedDate());
			ExistingCommitteeSchedule.setIsActive(0);
			return 1;
		}
		else {
			return 0;
		}
		
	}
	
	@Override
	public int  CommitteeScheduleAgendaDelete(CommitteeScheduleDto dto) throws Exception
	{
		Query query=manager.createNativeQuery(COMMITTEESCHEDULEAGENDADELETE);
		query.setParameter("modifiedby",dto.getModifiedBy());
		query.setParameter("modifieddate",dto.getModifiedDate());
		query.setParameter("scheduleid",dto.getScheduleId());
		return query.executeUpdate();
	}
	
	
	
	
	@Override
	public int  CommitteeScheduleInvitationDelete(CommitteeScheduleDto dto) throws Exception
	{
		Query query=manager.createNativeQuery(COMMITTEESCHEDULEINVITATIONDELETE);
		query.setParameter("scheduleid",dto.getScheduleId());
		return query.executeUpdate();
	}
	
	
	@Override
	public List<Object[]> ScheduleCommitteeEmpCheck(String scheduleid ,String empid) throws Exception
	{
		Query query=manager.createNativeQuery(SCHEDULECOMMITTEEEMPCHECK);
		query.setParameter("scheduleid", Long.parseLong(scheduleid));
		query.setParameter("empid", Long.parseLong(empid));
		return (List<Object[]>)query.getResultList();
	}
	
	
	@Override
	public List<Object[]> ScheduleCommitteeEmpinvitedCheck(String scheduleid ,String empid) throws Exception
	{
		Query query=manager.createNativeQuery(SCHEDULECOMMITTEEEMPINVITEDCHECK);
		query.setParameter("scheduleid", Long.parseLong(scheduleid));
		query.setParameter("empid", Long.parseLong(empid));
		return (List<Object[]>)query.getResultList();
	}
	
	
	@Override
	public List<Object[]> EmpScheduleData(String empid,String scheduleid) throws Exception 
	{
		Query query=manager.createNativeQuery(EMPSCHEDULEDATA);
		query.setParameter("empid",  Long.parseLong(empid));
		query.setParameter("scheduleid", Long.parseLong(scheduleid));
		List<Object[]> EmpScheduleData=(List<Object[]>)query.getResultList();
		return EmpScheduleData;
	}
	
	
	@Override
	public List<Object[]> AllActionAssignedCheck(String scheduleid) throws Exception 
	{
		Query query=manager.createNativeQuery(ALLACTIONASSIGNEDCHECK);
		query.setParameter("scheduleid", Long.parseLong(scheduleid));
		List<Object[]> AllActionAssignedCheck=(List<Object[]>)query.getResultList();
		return AllActionAssignedCheck;
	}
	
	
	@Override
	public List<Object[]> DefaultAgendaList(String committeeid,String LabCode) throws Exception 
	{
		Query query=manager.createNativeQuery(DEFAULTAGENDALIST);
		query.setParameter("committeeid", Long.parseLong(committeeid));
		query.setParameter("LabCode", LabCode );
		List<Object[]> DefaultAgendaList=(List<Object[]>)query.getResultList();
		return DefaultAgendaList;
	}
	
	
	@Override
	public List<Object[]> ProcurementStatusList(String projectid)throws Exception{
		Query query = manager.createNativeQuery(PROCUREMETSSTATUSLIST);
		query.setParameter("projectid", Long.parseLong(projectid));
		List<Object[]> ProcurementStatusList= query.getResultList();
		return ProcurementStatusList;
		
	}
	
	@Override
	public List<Object[]> ActionPlanSixMonths(String projectid)throws Exception
	{
		List<Object[]> ActionPlanThreeMonths=new ArrayList<Object[]>();
		Query query = manager.createNativeQuery("CALL Pfms_Milestone_PDC_New(:projectid, 180);");
		query.setParameter("projectid", Long.parseLong(projectid));
		try {
			ActionPlanThreeMonths= query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new java.util.Date() +" Inside DAO ActionPlanSixMonths " + e);
		}
		return ActionPlanThreeMonths;
	}
	
	private static final String PROJECTDATADETAILS="SELECT ppd.projectdataid,ppd.projectid,ppd.filespath,ppd.systemconfigimgname,ppd.SystemSpecsFileName,ppd.ProductTreeImgName,ppd.PEARLImgName,ppd.CurrentStageId,ppd.RevisionNo,pps.projectstagecode,pps.projectstage,pps.stagecolor,pm.project_code,ppd.proclimit/100000  FROM pfms_project_data ppd, pfms_project_stage pps,project_master pm WHERE ppd.projectid=pm.project_id AND ppd.CurrentStageId=pps.projectstageid AND ppd.projectid=:projectid";
	@Override
	public Object[] ProjectDataDetails(String projectid) throws Exception 
	{
		Query query=manager.createNativeQuery(PROJECTDATADETAILS);
		query.setParameter("projectid", Long.parseLong(projectid));
		Object[] ProjectStageDetails=null;
		try {
			ProjectStageDetails=(Object[])query.getSingleResult();
		}catch (Exception e) {
			logger.error(new java.util.Date() +" Inside DAO ProjectDataDetails "+ e);
			return null;
		}
		
		return ProjectStageDetails;
	}
	@Override
	public List<Object[]> LastPMRCActions(long scheduleid,String committeeid,String proid,String isFrozen) throws Exception 
	{
		Query query=manager.createNativeQuery("CALL last_pmrc_actions_list_new(:scheduleid,:committeeid,:proid)");	   
		query.setParameter("scheduleid", scheduleid);
		query.setParameter("committeeid", Long.parseLong(committeeid));
		query.setParameter("proid", Long.parseLong(proid));
//		query.setParameter("isFrozen",isFrozen);
		List<Object[]> LastPMRCActions=(List<Object[]>)query.getResultList();			
		return LastPMRCActions;		
	}
	
	@Override
	public List<Object[]> CommitteeMinutesSpecNew()throws Exception
	{
		Query query=manager.createNativeQuery(COMMITTEEMINUTESSPECNEW);
		 List<Object[]> CommitteeMinutesSpecNew=(List<Object[]>) query.getResultList();
		return CommitteeMinutesSpecNew;
	}
	
	
	@Override 
	public List<Object[]> MilestoneSubsystems(String projectid) throws Exception 
	{
		Query query=manager.createNativeQuery(MILESTONESUBSYSTEMS);	   
		query.setParameter("projectid", Long.parseLong(projectid));
		List<Object[]> MilestoneSubsystems=(List<Object[]>)query.getResultList();	
		return MilestoneSubsystems;
	}
	
	@Override 
	public List<Object[]> EmployeeScheduleReports(String empid, String fromdate,String todate) throws Exception 
	{
		Query query=manager.createNativeQuery("CALL Pfms_Emp_Report (:empid,:fromdate,:todate)");	   
		query.setParameter("empid", Long.parseLong(empid));
		query.setParameter("fromdate", fromdate);
		query.setParameter("todate", todate);
		List<Object[]> EmployeeScheduleList=(List<Object[]>)query.getResultList();	
		return EmployeeScheduleList;
	}
	
	
	@Override
	public List<Object[]> EmployeeDropdown(String empid,String logintype,String projectid)throws Exception
	{
		Query query=manager.createNativeQuery("CALL Employee_Dropdown(:empid,:logintype,:projectid);");
		query.setParameter("empid", Long.parseLong(empid));
		query.setParameter("logintype", logintype);
		query.setParameter("projectid", Long.parseLong(projectid));
		List<Object[]> EmployeeDropdown=(List<Object[]>)query.getResultList();
		return EmployeeDropdown;
	}
	
	
	@Override
	public List<Object[]> FileRepMasterListAll(String projectid,String LabCode)throws Exception
	{
		Query query=manager.createNativeQuery(FILEREPMASTERLISTALL);
		query.setParameter("projectid",Long.parseLong(projectid));
		query.setParameter("LabCode", LabCode);
		List<Object[]> FileRepMasterListAll=(List<Object[]>)query.getResultList();
		return FileRepMasterListAll;
	}
	
	
	@Override
	public Object[] AgendaDocLinkDownload(String filerepid)throws Exception
	{
		Query query=manager.createNativeQuery(AGENDADOCLINKDOWNLOAD);
		query.setParameter("filerepid", Long.parseLong(filerepid));
		List<Object[]> FileRepMasterListAll=(List<Object[]>)query.getResultList();
		if(FileRepMasterListAll.size()>0) {
		return FileRepMasterListAll.get(0);
		}else {
			return null;
		}
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
	public List<Object[]> AgendaLinkedDocList(String scheduleid) throws Exception {
		Query query=manager.createNativeQuery(AGENDALINKEDDOCLIST);
		query.setParameter("scheduleid",  Long.parseLong(scheduleid));
		List<Object[]> AgendaLinkedDocList=(List<Object[]>)query.getResultList();
		return AgendaLinkedDocList;
	}
	
	@Override
	public CommitteeScheduleAgendaDocs AgendaUnlinkDoc(CommitteeScheduleAgendaDocs agendadoc) throws Exception {
		CommitteeScheduleAgendaDocs detachedentity = manager.find(CommitteeScheduleAgendaDocs.class, agendadoc.getAgendaDocid());
		detachedentity.setIsActive(0);
		detachedentity.setModifiedBy(agendadoc.getModifiedBy());
		detachedentity.setModifiedDate(agendadoc.getModifiedDate());
		try {
			return manager.merge(detachedentity);
		}catch (Exception e) {
			logger.error(new java.util.Date() +"Inside DAO AgendaUnlinkDoc"+e);
			e.printStackTrace();
			return null;
		}
	}
	
	private static final String PREDEFAGENDAEDIT = "UPDATE committee_default_agenda SET agendaitem=:agendaitem ,"
			+ " remarks = :remarks , duration = :duration, modifiedby=:modifiedby , modifieddate=:modifieddate "
			+ "WHERE DefaultAgendaId= :DefaultAgendaId ";
	
	@Override
	public int PreDefAgendaEdit(CommitteeDefaultAgenda agenda) throws Exception 
	{
		CommitteeDefaultAgenda ExistingCommitteeDefaultAgenda=manager.find(CommitteeDefaultAgenda.class, agenda.getDefaultAgendaId());
		if(ExistingCommitteeDefaultAgenda != null) {
			ExistingCommitteeDefaultAgenda.setAgendaItem(agenda.getAgendaItem());
			ExistingCommitteeDefaultAgenda.setRemarks(agenda.getRemarks());
			ExistingCommitteeDefaultAgenda.setDuration(agenda.getDuration());
			ExistingCommitteeDefaultAgenda.setModifiedBy(agenda.getModifiedBy());
			ExistingCommitteeDefaultAgenda.setModifiedDate(agenda.getModifiedDate());
			return 1;
		}
		else {
			return 0;
		}
		
	}

	
	@Override
	public long PreDefAgendaAdd(CommitteeDefaultAgenda agenda) throws Exception 
	{
		manager.persist(agenda);
		manager.flush();
		return agenda.getDefaultAgendaId();
	}
	
	
	@Override
	public int PreDefAgendaDelete(String DefaultAgendaId) throws Exception 
	{	
		CommitteeDefaultAgenda ExistingCommitteeDefaultAgenda= manager.find(CommitteeDefaultAgenda.class, Long.parseLong(DefaultAgendaId));		if(ExistingCommitteeDefaultAgenda != null) {
			ExistingCommitteeDefaultAgenda.setIsActive(0);
			return 1;
		}
		else {
			return 0;
		}
		
	}
	
	private static final String COMMPROSCHEDULELIST = "SELECT COUNT(*)+1 FROM  committee_schedule cs ,committee_meeting_status ms WHERE cs.committeeid=:committeeid AND cs.projectid=:projectid AND cs.isactive=1 AND  ms.meetingstatus=cs.scheduleflag AND ms.meetingstatusid > 6 AND cs.scheduledate<:sdate ";
	@Override
	public int CommProScheduleList(String projectid,String committeeid,String sdate) throws Exception 
	{
		Query query=manager.createNativeQuery(COMMPROSCHEDULELIST);
		query.setParameter("projectid", Long.parseLong(projectid));
		query.setParameter("committeeid", Long.parseLong(committeeid));
		query.setParameter("sdate", sdate);
		Long CommProScheduleList=(Long)query.getSingleResult();
		return CommProScheduleList.intValue();
	}
	
	private static final String LASTPRMC="SELECT cs.scheduleid FROM  committee_schedule cs ,committee_meeting_status ms WHERE cs.committeeid=:committeeid AND cs.projectid=:projectid AND cs.isactive=1 AND  ms.meetingstatus=cs.scheduleflag AND ms.meetingstatusid > 6 AND cs.scheduleid<:scheduleId ORDER BY cs.scheduleid DESC LIMIT 1";
	@Override
	public long getLastPmrcId(String projectid,String committeeid,String scheduleId) throws Exception 
	{
		
		try {
			Query query=manager.createNativeQuery(LASTPRMC);
			query.setParameter("projectid", Long.parseLong(projectid));
			query.setParameter("committeeid", Long.parseLong(committeeid));
			query.setParameter("scheduleId", Long.parseLong(scheduleId));
			return (Long)query.getSingleResult();
		}catch (Exception e) {
			logger.error(new java.util.Date() +"Inside DAO getLastPmrcId "+ e);
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public long insertMinutesFinance(MinutesFinanceList finance) throws Exception {
		
		manager.persist(finance);
		manager.flush();
		return finance.getMinutesFinanceId();
	}


    
	@Override
	public int updateMinutesFrozen(String schduleid) throws Exception {
		
		CommitteeSchedule ExistingCommitteeSchedule= manager.find(CommitteeSchedule.class, Long.parseLong(schduleid));
		if(ExistingCommitteeSchedule != null) {
			ExistingCommitteeSchedule.setMinutesFrozen("Y");
			return 1;
		}
		else {
			return 0;
		}
		
	}

    private static final String FINANCELIST="FROM MinutesFinanceList WHERE CommiteeScheduleId=:scheduleid";
	@Override
	public List<MinutesFinanceList> getMinutesFinance(String scheduleid) throws Exception {
		Query query=manager.createQuery(FINANCELIST);
		query.setParameter("scheduleid", Long.parseLong(scheduleid));
		List<MinutesFinanceList> getMinutesFinance=(List<MinutesFinanceList>)query.getResultList();
		return getMinutesFinance;
	
	}

	private static final String CLUSTERLIST="SELECT cluster_id,cluster_name, cluster_short_name FROM cluster WHERE cluster_type='Technology Cluster' ";
	@Override
	public List<Object[]> ClusterList() throws Exception{
		Query query=manager.createNativeQuery(CLUSTERLIST);
		List<Object[]> ClusterList=( List<Object[]>)query.getResultList();
		return ClusterList;
		
		
	}
	

	private static final String GETDEFAULTAGENDASCOUNT="SELECT COUNT(defaultagendaid),'count' FROM committee_default_agenda WHERE committeeid=:committeeId AND labcode=:LabCode";
	@Override
	public Object[] getDefaultAgendasCount(String committeeId, String LabCode) throws Exception
	{
		Query query=manager.createNativeQuery(GETDEFAULTAGENDASCOUNT);
		query.setParameter("committeeId", Long.parseLong(committeeId));
		query.setParameter("LabCode", LabCode);
		Object[] ClusterList=(Object[])query.getResultList().get(0);
		return ClusterList;
	}
	
			
	@Override
	public LabMaster LabDetailes(String LabCode) throws Exception {
		logger.info(new java.util.Date() +"Inside LabDetailes");
		LabMaster LabDetailes=manager.find(LabMaster.class, 1);
		
		CriteriaBuilder cb= manager.getCriteriaBuilder();
		CriteriaQuery<LabMaster> cq= cb.createQuery(LabMaster.class);
		Root<LabMaster> root= cq.from(LabMaster.class);					
		Predicate p1=cb.equal(root.get("LabCode") , LabCode);
		cq=cq.select(root).where(p1);
		TypedQuery<LabMaster> allquery = manager.createQuery(cq);
		LabMaster lab= allquery.getResultList().get(0);
		
		
		return LabDetailes;
	}


	private static final String  COMMITTEEMAINDETAILS = "SELECT cm.committeemainid, cm.committeeid, c.committeeshortname, c.labcode FROM committee_main cm, committee c  WHERE cm.committeeid = c.committeeid AND cm.committeemainid=:CommitteeMainId ";
	@Override
	public Object[] CommitteeMainDetails(String CommitteeMainId) {
		
		Query query=manager.createNativeQuery(COMMITTEEMAINDETAILS);
		query.setParameter("CommitteeMainId", Long.parseLong(CommitteeMainId));
		Object[] ClusterList=(Object[])query.getResultList().get(0);
		return ClusterList;
		
	}
	
	@Override
	public long FreezeDPFMMinutesAdd(CommitteeMeetingDPFMFrozen dpfm)throws Exception
	{
		manager.persist(dpfm);
		return dpfm.getFrozenDPFMId();
	}
	
	private static final String GETFROZENDPFMMINUTES = "from CommitteeMeetingDPFMFrozen where ScheduleId=:scheduleId and IsActive=1 "; 
	@Override
	public CommitteeMeetingDPFMFrozen getFrozenDPFMMinutes(String scheduleId)throws Exception
	{
		Query query=manager.createQuery(GETFROZENDPFMMINUTES);
		query.setParameter("scheduleId", Long.parseLong(scheduleId));
		CommitteeMeetingDPFMFrozen dpfm=(CommitteeMeetingDPFMFrozen)query.getResultList().get(0);
		return dpfm;
	}
	
	
	
	@Override
	public List<Object[]> totalProjectMilestones(String projectid) throws Exception {
		List<Object[]> TotalMilestones=new ArrayList<Object[]>();
		Query query = manager.createNativeQuery("CALL pfms_total_project_milestones(:projectid);"); // to call all the milestones using projectid
		query.setParameter("projectid", Long.parseLong(projectid));
		try {
			TotalMilestones= query.getResultList(); 
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new java.util.Date() +" Inside DAO totalProjectMilestones " + e);
		}
		return TotalMilestones;
	}
	
	private static final String GETFROZENCOMMOM="from CommitteeProjectBriefingFrozen where ScheduleId=:scheduleId and IsActive=1";
	@Override
	public CommitteeProjectBriefingFrozen getFrozenCommitteeMOM(String committeescheduleid) throws Exception {
		Query query=manager.createQuery(GETFROZENCOMMOM);
		query.setParameter("scheduleId", Long.parseLong(committeescheduleid));
		CommitteeProjectBriefingFrozen cpf=(CommitteeProjectBriefingFrozen)query.getResultList().get(0);
		return cpf;
	}
	
	@Override
	public long FreezeBriefingAdd(CommitteeProjectBriefingFrozen briefing) throws Exception {
		manager.persist(briefing);
		manager.flush();
		return briefing.getFrozenBriefingId();
	}
	
	private static final String GETENVILIST = "SELECT PftsFileId,ProjectId,ROUND(EstimatedCost/100000,2) AS 'EstimatedCost',ItemNomenclature,Remarks,PrbDateOfInti,EnvisagedStatus FROM pfts_file WHERE ProjectId=:projectid AND EnvisagedFlag='Y' AND IsActive='1'";
	@Override
	public List<Object[]> getEnvisagedDemandList(String projectid) throws Exception {
		Query query = manager.createNativeQuery(GETENVILIST);
		query.setParameter("projectid", Integer.parseInt(projectid));
		try {
			return query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new java.util.Date() +" Inside DAO totalProjectMilestones " + e);
		}
		return null;
	}
	
	
	@Override
	public int MomFreezingUpdate(String committeescheduleid) throws Exception {
		
		String FROZENFILEUPDATE="UPDATE committee_meeting_dpfm_frozen SET isactive=0 WHERE scheduleid=:scheduleid";
		
		int mf=0;
		CommitteeSchedule ExistingCommitteeSchedule = manager.find(CommitteeSchedule.class, Long.parseLong(committeescheduleid));
		if(ExistingCommitteeSchedule != null) {
			ExistingCommitteeSchedule.setMinutesFrozen("N");
			mf=1;
		}
		
		Query query2=manager.createNativeQuery(FROZENFILEUPDATE);
		query2.setParameter("scheduleid", Long.parseLong(committeescheduleid));
		
		int count=mf+query2.executeUpdate();
		return count;
	}
	private static final String MEETINGS="SELECT cs.scheduleid,cs.projectid,cs.InitiationId,GetCommitteeShortName(cs.scheduledate,c.committeeshortname) AS committeeshortname,GetCommitteeName(cs.scheduledate,c.committeeshortname,c.committeename) AS committeename,cs.MeetingVenue,cs.ScheduleStartTime,pm.projectcode,pm.projectshortname FROM committee_schedule cs,committee c ,project_master pm WHERE  c.CommitteeId=cs.CommitteeId AND pm.projectid=cs.projectid AND  cs.ScheduleDate=:date AND cs.isactive='1'";
	@Override
	public List<Object[]> getTodaysMeetings(String date) throws Exception {
		Query query = manager.createNativeQuery(MEETINGS);
		query.setParameter("date", date);
		
		return (List<Object[]>)query.getResultList();
	}
	
	private static final String NONPROJECTACTIONS="SELECT d.ActionAssignId,d.ActionNo,CONCAT(IFNULL(CONCAT(e.title,' '),IFNULL(CONCAT(e.salutation,' '),'')), e.emp_name) AS 'empname',\r\n"
			+ "			d.ActionStatus,c.ActionMainId,c.ActionItem,b.ScheduleMinutesId,a.ScheduleId,a.MeetingId,a.ScheduleDate,d.PDCOrg,d.PDC1,d.PDC2,d.Progress,d.ProgressDate,a.projectId,a.InitiationId,a.Divisionid, a.CARSInitiationId, a.ProgrammeId, c.ProjectId AS actionprojecid\r\n"
			+ "			FROM committee_schedule a, committee_schedules_minutes_details b , action_main c,action_assign d, employee e\r\n"
			+ "			WHERE a.CommitteeId = :committeeId AND a.isactive = 1 AND a.ScheduleId=b.ScheduleId AND c.type='A'  \r\n"
			+ "			AND c.ScheduleMinutesId=b.ScheduleMinutesId AND c.ActionMainId=d.ActionMainId AND d.Assignee=e.emp_id AND d.AssigneeLabCode <> '@EXP'  \r\n"
			+ "			\r\n"
			+ "	UNION \r\n"
			+ "	\r\n"
			+ "SELECT d.ActionAssignId,d.ActionNo,CONCAT(IFNULL(CONCAT(e.title,' '),IFNULL(CONCAT(e.salutation,' '),'')), e.expertname) AS 'empname',\r\n"
			+ "			d.ActionStatus,c.ActionMainId,c.ActionItem,b.ScheduleMinutesId,a.ScheduleId,a.MeetingId,a.ScheduleDate,d.PDCOrg,d.PDC1,d.PDC2,d.Progress,d.ProgressDate,a.projectId,a.InitiationId,a.Divisionid, a.CARSInitiationId, a.ProgrammeId, c.ProjectId AS actionprojecid\r\n"
			+ "			FROM committee_schedule a, committee_schedules_minutes_details b , action_main c,action_assign d, expert e\r\n"
			+ "			WHERE a.CommitteeId = :committeeId AND a.isactive = 1 AND a.ScheduleId=b.ScheduleId AND c.type='A' \r\n"
			+ "			AND c.ScheduleMinutesId=b.ScheduleMinutesId AND c.ActionMainId=d.ActionMainId AND d.Assignee=e.expertid AND d.AssigneeLabCode = '@EXP'  ORDER BY ScheduleDate ASC";
	
	@Override
	public List<Object[]> actionDetailsForNonProject(String committeeId) throws Exception {
		Query query =manager.createNativeQuery(NONPROJECTACTIONS);
		query.setParameter("committeeId", Long.parseLong(committeeId));
		return (List<Object[]>)query.getResultList();
	}

	
	private static final String COMMITTEEOTHERSLIST="SELECT a.scheduledate,a.schedulestarttime,GetCommitteeName(a.scheduledate,b.committeeshortname,b.committeename) AS committeename,GetCommitteeShortName(a.scheduledate,b.committeeshortname) AS committeeshortname,a.scheduleid,a.scheduleflag,c.statusdetail,c.meetingstatusid FROM committee_schedule a,committee b,committee_meeting_status c WHERE a.committeeid=b.committeeid AND a.projectid=:projectid AND a.divisionid=:divisionid AND a.initiationid=:initiationid AND a.scheduleflag=c.meetingstatus AND a.isactive=1 AND (CASE WHEN 'B'=:projectstatus THEN c.meetingstatusid >= 6 WHEN 'C'=:projectstatus THEN c.meetingstatusid<=5 ELSE 1=1 END) AND b.CommitteeId NOT IN (1,2) ORDER BY a.scheduledate DESC";
	@Override
	public List<Object[]> CommitteeOthersList(String projectid, String divisionid, String initiationid, String projectstatus) throws Exception {
		
		Query query =manager.createNativeQuery(COMMITTEEOTHERSLIST);
		query.setParameter("projectid", Long.parseLong(projectid));
		query.setParameter("divisionid", Long.parseLong(divisionid));
		query.setParameter("initiationid", Long.parseLong(initiationid));
		query.setParameter("projectstatus", projectstatus  );
		List<Object[]> CommitteeOthersList=(List<Object[]>)query.getResultList();
		return CommitteeOthersList;

	}
	
	@Override
	public Long MomAttach(CommitteeMomAttachment cm) throws Exception {
		
		manager.persist(cm);
		manager.flush();
		return cm.getAttachmentId();
	}
	
	@Override
	public Long UpdateMomAttach(Long scheduleId) throws Exception {
		
		String s= "Update committee_mom_attachment set isactive='0' where ScheduleId=:ScheduleId";
		Query query = manager.createNativeQuery(s);
		query.setParameter("ScheduleId", scheduleId);
		
		return (long) query.executeUpdate();
	}
	
	private static final String MOMATTACH="SELECT AttachmentId,FilePath,AttachmentName FROM committee_mom_attachment WHERE ScheduleId=:ScheduleId AND isactive='1';";
	
	@Override
	public Object[] MomAttachmentFile(String committeescheduleid) throws Exception {
		
		Query query = manager.createNativeQuery(MOMATTACH);
		query.setParameter("ScheduleId", Long.parseLong(committeescheduleid));
		
		List<Object[]>momattach= (List<Object[]>)query.getResultList();
		
		if(momattach.size()>0) {
			return momattach.get(0);
		}
		
		
		return null;
	}
	
	private static final String MOMREPORTLIST="SELECT ScheduleId,CommitteeId,MeetingId,ScheduleDate,ScheduleStartTime,MeetingVenue,ScheduleFlag FROM committee_schedule WHERE ProjectId=:projectId AND CommitteeId=:committeeId AND ScheduleFlag IN('MKV','MMR','MMF','MMS','MMA')";
	
	@Override
	public List<Object[]> MomReportList(String projectId, String committeeId) throws Exception {
		Query query =manager.createNativeQuery(MOMREPORTLIST);
		query.setParameter("projectId", Long.parseLong(projectId));
		query.setParameter("committeeId", Long.parseLong(committeeId));
		List<Object[]> MomReportList=(List<Object[]>)query.getResultList();
		return MomReportList;
	}

	// Prudhvi 27/03/2024
	/* ------------------ start ----------------------- */
	private static final String INDUSTRYPARTNERREPLISTINVITATIONS ="SELECT a.IndustryPartnerRepId,a.RepName,'00' AS EmpNo,a.RepDesignation,'00' AS DesigId FROM pfms_industry_partner_rep a,pfms_industry_partner b WHERE a.IndustryPartnerId=b.IndustryPartnerId AND b.IndustryPartnerId=:IndustryPartnerId AND a.IsActive=1 AND a.IndustryPartnerRepId NOT IN (SELECT empid  FROM committee_member WHERE  CommitteeMainId=:CommitteeMainId AND labcode='@IP')";
	@Override
	public List<Object[]> IndustryPartnerRepListInvitationsMainMembers(String industryPartnerId, String committeemainid) throws Exception {
		try {
			Query query=manager.createNativeQuery(INDUSTRYPARTNERREPLISTINVITATIONS);
			query.setParameter("IndustryPartnerId", Long.parseLong(industryPartnerId));;
			query.setParameter("CommitteeMainId", Long.parseLong(committeemainid));
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
		
	}
	/* ------------------ end ----------------------- */
	
	private static final String FLOWDATA="SELECT e.emp_name , ed.designation,'Constituted By' FROM committee_main cm , employee e , login l,employee_desig ed WHERE cm.createdby=l.username AND l.empid=e.emp_id  AND e.desig_id=ed.desig_id AND cm.committeemainid=:committeemainid";
	
	@Override
	public List<Object[]> ConstitutionApprovalFlowData(String committeemainid) throws Exception {
		Query query = manager.createNativeQuery(FLOWDATA);
		query.setParameter("committeemainid", Long.parseLong(committeemainid));
		return (List<Object[]>)query.getResultList();
	}
	
	@Override
	public int MemberSerialNoUpdate(String memberId, String SerialNo) {
		
		CommitteeMember ExistingCommitteeMember = manager.find(CommitteeMember.class, Long.parseLong(memberId));
		if(ExistingCommitteeMember != null) {
			ExistingCommitteeMember.setSerialNo(Long.parseLong(SerialNo));
			return 1;
		}
		else
		{
			return 0;
		}
		
	}
	@Override
	public long saveCommitteeLetter(CommitteeLetter committeeLetter) throws Exception {
		manager.persist(committeeLetter);
		manager.flush();
		return committeeLetter.getLetterId();
	}
	
	private static final String COMMITTEELETTERS="SELECT letterid,Filepath,AttachmentName,DATE_FORMAT(createddate, '%d-%m-%Y') FROM committee_inivitationletter WHERE ProjectId=:projectId AND InitiationId=:initiationId AND DivisionId=:divisionId AND CommitteId=:commmitteeId AND isactive='1';";
	@Override
	public List<Object[]> getcommitteLetters(String commmitteeId, String projectId,
			String divisionId,String initiationId) throws Exception {
		
		Query query = manager.createNativeQuery(COMMITTEELETTERS);
		
		query.setParameter("commmitteeId", Long.parseLong(commmitteeId));
		query.setParameter("projectId", Long.parseLong(projectId));
		query.setParameter("divisionId", Long.parseLong(divisionId));
		query.setParameter("initiationId", Long.parseLong(initiationId));
		
		return (List<Object[]>)query.getResultList();
	}
	private static final String COMTLETTER ="  SELECT letterid,Filepath,AttachmentName,DATE_FORMAT(createddate, '%d-%m-%Y') AS formatted_date FROM committee_inivitationletter WHERE letterid=:letterid";
	@Override
	public Object[] getcommitteeLetter(String letterId) throws Exception {
		Query query = manager.createNativeQuery(COMTLETTER);
		query.setParameter("letterid", Long.parseLong(letterId));
		
		return (Object[])query.getSingleResult();
	}
	
	@Override
	public long UpdateCommitteLetter(String letterId) throws Exception {
		
		CommitteeLetter ExistingCommitteeLetter = manager.find(CommitteeLetter.class, Long.parseLong(letterId));
		if(ExistingCommitteeLetter != null) {
			ExistingCommitteeLetter.setIsActive(0);
			return 1L;
		}
		else {
			return 0L;
		}
		
	}

	
	@Override
	public int ReformationDate(CommitteeMainDto cmdd) throws Exception {
		int count=0;
		try {
		
		CommitteeMain ExistingCommitteeMain = manager.find(CommitteeMain.class, cmdd.getCommitteeMainId());
		if(ExistingCommitteeMain != null) {
			ExistingCommitteeMain.setReferenceNo(cmdd.getReferenceNo());
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date utilDate = sdf.parse(cmdd.getFormationDate());
			java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
			ExistingCommitteeMain.setFormationDate(sqlDate);
			count=1;
					}
		
		}
		catch(Exception e) {
		    e.printStackTrace();
		}
		
		return count;
	}
	
	
	//prakarsh
	private static final String MeettingList="SELECT cs.ScheduleId,cs.MeetingId,cs.ScheduleDate,cs.ScheduleStartTime,cs.MeetingVenue,GetCommitteeShortName(cs.scheduledate,c.committeeshortname) AS committeeshortname,e.emp_name,csi.EmpId,csi.MemberType FROM committee_schedule cs JOIN committee c ON cs.CommitteeId = c.CommitteeId,committee_schedules_invitation csi, employee e WHERE cs.ProjectId =:projectId AND cs.isactive=1 AND cs.CommitteeId = :committeeId AND csi.MemberType='P' AND csi.EmpId=e.emp_id AND cs.ScheduleId= csi.CommitteeScheduleId  ORDER BY cs.scheduledate ";
	@Override
	public List<Object[]> MeettingList(String committeeId, String projectId) throws Exception {
		try {
			Query query=manager.createNativeQuery(MeettingList);
			query.setParameter("projectId", Long.parseLong(projectId));
			query.setParameter("committeeId", Long.parseLong(committeeId));
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}
	
	private static final String MeettingResultAll="  SELECT cs.scheduleid,cs.MeetingId,cs.scheduledate,cs.ScheduleStartTime,cs.MeetingVenue, GetCommitteeShortName(cs.scheduledate,c.committeeshortname) AS committeeshortname,e.emp_name,csi.EmpId,csi.MemberType  FROM committee_schedule cs,committee c,committee_schedules_invitation csi, employee e WHERE  cs.ScheduleId= csi.CommitteeScheduleId AND cs.committeeid=c.committeeid AND cs.isActive=1 AND cs.projectid=:projectId AND csi.MemberType='P' AND csi.EmpId=e.emp_id ORDER BY cs.scheduledate";
	@Override
	public List<Object[]> MeettingList(String projectid) {
		try {
			Query query=manager.createNativeQuery(MeettingResultAll);
			query.setParameter("projectId", Long.parseLong(projectid));
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}
	
	@Override
	public List<Object[]> SpecialEmployeeListInvitations(String labCode,String scheduleid) throws Exception {
		
		String sql1="  SELECT a.emp_id, a.emp_name,a.emp_no,b.designation, a.desig_id  FROM employee a,employee_desig b WHERE lab_code=:labCode AND a.emp_status IN ('P','R','T') AND a.desig_id=b.desig_id AND a.emp_id NOT IN  (SELECT empid  FROM committee_schedules_invitation WHERE  committeescheduleid=:scheduleid AND membertype ='SPL')  ";
		
		String sql2 = "  SELECT a.expertid,a.expertname,a.expertno,b.designation,a.desigid FROM expert a ,employee_desig b WHERE a.desigid=b.desig_id  AND a.expertid NOT IN  (SELECT empid  FROM committee_schedules_invitation WHERE  committeescheduleid=:scheduleid AND membertype ='SPL')  AND a.isactive='1'";
		
		List<Object[]>list= new ArrayList<>();
		
		if(!labCode.equalsIgnoreCase("@EXP")) {
			Query query = manager.createNativeQuery(sql1);
			query.setParameter("labCode", labCode);
			query.setParameter("scheduleid", Long.parseLong(scheduleid));
			list = (List<Object[]>)query.getResultList();
			
		}else {
			Query query = manager.createNativeQuery(sql2);
			query.setParameter("scheduleid", Long.parseLong(scheduleid));
			list = (List<Object[]>)query.getResultList();
			
		}
	
		return list;
	}
	
	
	private static final String FLOWDATAS="SELECT e.emp_name,d.designation,a.ConstitutionStatus FROM committee_constitution_history a, employee e , employee_desig d WHERE a.CommitteeMainId =:CommitteeMainId  AND a.ActionByEmpid=e.emp_id AND e.desig_id=d.desig_id";
	@Override
	public List<Object[]> allconstitutionapprovalflowData(String CommitteeMainId) throws Exception {
	
		Query query  = manager.createNativeQuery(FLOWDATAS);
		query.setParameter("CommitteeMainId", Long.parseLong(CommitteeMainId));	
		
		return (List<Object[]>)query.getResultList();
	}
	
	
	//Committee Flow 
	
	@Override
	public long addPmsEnote(PmsEnote pmsenote) throws Exception {
		manager.persist(pmsenote);
		manager.flush();
		return pmsenote.getEnoteId();
	}
	
	private static final String COMMENOTELIST = "SELECT a.EnoteId , a.RefNo , a.RefDate , a.Subject , a.Comment , a.CommitteeMainId , a.ScheduleId ,a.Recommend1,a.Rec1_Role,a.Recommend2,a.Rec2_Role , a.Recommend3, a.Rec3_Role , a.ApprovingOfficer,a.Approving_Role,a.EnoteStatusCode,a.EnoteStatusCodeNext,a.InitiatedBy,e.emp_name , d.designation , ds.EnoteStatus,ds.EnoteStatusColor,a.ApprovingOfficerLabCode  FROM pms_enote a , employee e , employee_desig d , dak_enote_status ds WHERE a.CommitteeMainId = :CommitteeMainId AND a.EnoteStatusCode=ds.EnoteStatusCode AND a.ScheduleId=:ScheduleId AND a.InitiatedBy = e.emp_id AND e.desig_id = d.desig_id AND a.isactive ='1'";
	@Override
	public Object[] CommitteMainEnoteList(String CommitteeMainId,String ScheduleId) throws Exception {

		Query query = manager.createNativeQuery(COMMENOTELIST);
		query.setParameter("CommitteeMainId", Long.parseLong(CommitteeMainId));
		query.setParameter("ScheduleId", Long.parseLong(ScheduleId));
		List<Object[]> CommitteMainEnoteList = null;
		try {
		CommitteMainEnoteList = query.getResultList();
		if(CommitteMainEnoteList!=null && CommitteMainEnoteList.size()>0){
			return CommitteMainEnoteList.get(0);
		}else {
			return null;
		}
		}catch(Exception e) {
			
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public PmsEnote getPmsEnote(String enoteId) throws Exception {
		try {
			return manager.find(PmsEnote.class, Long.parseLong(enoteId)) ;
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(LocalDate.now()+" Inside DAO getPmsEnote "+e);
			return null;
		}
	}
	
	@Override
	public long addEnoteTrasaction(PmsEnoteTransaction transaction) throws Exception {
		manager.persist(transaction);
		manager.flush();
		return transaction.getEnoteTransId();
	}
	
	private static final String ENOTETRANSACTIONLIST="SELECT tra.EnoteTransId,emp.emp_id,emp.emp_name,des.Designation,tra.ActionDate,tra.Remarks,sta.EnoteStatus,sta.EnoteStatusColor FROM pms_enote_trans tra,dak_enote_status sta,employee emp,employee_desig des,pms_enote e WHERE e.EnoteId = tra.EnoteId AND tra.EnoteStatusCode = sta.EnoteStatusCode AND tra.ActionBy=emp.emp_id AND emp.desig_id = des.desig_id AND e.EnoteId=:enoteTrackId ORDER BY tra.ActionDate";
	@Override
	public List<Object[]> EnoteTransactionList(String enoteTrackId) throws Exception {
		logger.info(LocalDate.now() + "Inside EnoteTransactionList");
		try {
			Query query = manager.createNativeQuery(ENOTETRANSACTIONLIST);
			query.setParameter("enoteTrackId", Long.parseLong(enoteTrackId));
			List<Object[]> EnoteTransactionList = (List<Object[]>) query.getResultList();
				return EnoteTransactionList;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LocalDate.now() + "Inside DaoImpl EnoteTransactionList", e);
			return null;
		}
	}
	private static final String ENOTEPENDINGLIST="CALL Pms_ComEnote_PendingList(:EmpId,:Type)";
	private static final String MOMENOTEPENDINGLIST="CALL Pms_MomEnote_PendingList(:EmpId,:Type)";
	@Override
	public List<Object[]> eNotePendingList(long empId, String type) throws Exception {
		logger.info(LocalDate.now() + "Inside eNotePendingList");
		try {
			Query query = manager.createNativeQuery(ENOTEPENDINGLIST);
			if(type.equalsIgnoreCase("S")) {
				query=manager.createNativeQuery(MOMENOTEPENDINGLIST);
			}
			query.setParameter("EmpId", empId);
			query.setParameter("Type", type);
			 List<Object[]> eNotePendingList = (List<Object[]>) query.getResultList();
				return eNotePendingList;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LocalDate.now() + "Inside DaoImpl eNotePendingList", e);
			return null;
		}
		
		
	}
	
	private static final String NEWAPPROVALLIST ="SELECT \r\n"
			+ "(SELECT CONCAT(e.emp_name,', ',d.designation) FROM employee e,employee_desig d WHERE e.emp_id=p.InitiatedBy AND e.desig_id=d.desig_id )AS 'InitiatedByEmployee'\r\n"
			+ ",(SELECT CONCAT(e.emp_name,', ',d.designation) FROM employee e,employee_desig d WHERE e.emp_id=p.Recommend1 AND e.desig_id=d.desig_id )AS 'Recommend1Officer'\r\n"
			+ ",p.Rec1_Role\r\n"
			+ ",(SELECT CONCAT(e.emp_name,', ',d.designation) FROM employee e,employee_desig d WHERE e.emp_id=p.Recommend2 AND e.desig_id=d.desig_id )AS 'Recommend2Officer'\r\n"
			+ ",p.Rec2_Role\r\n"
			+ ",(SELECT CONCAT(e.emp_name,', ',d.designation) FROM employee e,employee_desig d WHERE e.emp_id=p.Recommend3 AND e.desig_id=d.desig_id )AS 'Recommend3Officer'\r\n"
			+ ",p.Recommend3\r\n"
			+ ",(SELECT CONCAT(e.emp_name,', ',d.designation) FROM employee e,employee_desig d WHERE e.emp_id=p.ApprovingOfficer AND e.desig_id=d.desig_id )AS 'Approving Officer'\r\n"
			+ ",p.Approving_Role,p.ApprovingOfficerLabCode\r\n"
			+ " FROM pms_enote p WHERE p.EnoteId=:EnoteId";
@Override
public Object[] NewApprovalList(String EnoteId) throws Exception {

	Query query = manager.createNativeQuery(NEWAPPROVALLIST);
	query.setParameter("EnoteId", Long.parseLong(EnoteId));
	Object[]NewApprovalList = null;
	try {
		NewApprovalList=(Object[])query.getSingleResult();
		return NewApprovalList;
	}catch (Exception e) {
		// TODO: handle exception
	}
	
	return null;
}

private static final String ENOTEAPPROVELIST="SELECT MAX(a.EnoteId) AS EnoteId,MAX(a.RefNo) AS RefNo,MAX(a.RefDate) AS RefDate,MAX(a.Subject) AS SUBJECT,MAX(a.Comment) AS COMMENT,MAX(a.InitiatedBy) AS InitiatedBy,MAX(c.ActionDate) AS ActionDate,\r\n"
		+ "MAX(d.EnoteStatus) AS EnoteStatus,MAX(d.EnoteStatusColor) AS EnoteStatusColor,MAX(d.EnoteStatusCode) AS EnoteStatusCode,MAX(cm.CommitteeShortName) AS CommitteeShortName,\r\n"
		+ "MAX(p.project_short_name) AS ProjectShortName,a.CommitteeMainId\r\n"
		+ "FROM pms_enote a,employee b,pms_enote_trans c,dak_enote_status d, committee_main m ,committee cm, project_master p \r\n"
		+ "WHERE a.InitiatedBy=b.emp_id\r\n"
		+ "AND a.EnoteStatusCode=d.EnoteStatusCode AND a.EnoteId=c.EnoteId AND  m.CommitteeMainId=a.CommitteeMainId AND m.CommitteeId =cm.CommitteeId AND m.projectid = p.project_id \r\n"
		+ "AND c.EnoteStatusCode IN ('RC1','RC2','RC3','RC4','RC5','EXT','APR') AND c.ActionBy=:empId AND DATE(a.CreatedDate) \r\n"
		+ "BETWEEN :fromDate AND :tdate GROUP BY a.EnoteId \r\n"
		+ "UNION\r\n"
		+ "SELECT MAX(a.EnoteId) AS EnoteId,MAX(a.RefNo) AS RefNo,MAX(a.RefDate) AS RefDate,MAX(a.Subject) AS SUBJECT,MAX(a.Comment) AS COMMENT,MAX(a.InitiatedBy) AS InitiatedBy,MAX(c.ActionDate) AS ActionDate,\r\n"
		+ "MAX(d.EnoteStatus) AS EnoteStatus,MAX(d.EnoteStatusColor) AS EnoteStatusColor,MAX(d.EnoteStatusCode) AS EnoteStatusCode,MAX(cm.CommitteeShortName) AS CommitteeShortName,\r\n"
		+ "'Non-Project' AS ProjectShortName ,a.CommitteeMainId \r\n"
		+ "FROM pms_enote a,employee b,pms_enote_trans c,dak_enote_status d, committee_main m ,committee cm \r\n"
		+ "WHERE a.InitiatedBy=b.emp_id\r\n"
		+ "AND a.EnoteStatusCode=d.EnoteStatusCode AND a.EnoteId=c.EnoteId AND  m.CommitteeMainId=a.CommitteeMainId AND m.CommitteeId =cm.CommitteeId \r\n"
		+ "AND c.EnoteStatusCode IN ('RC1','RC2','RC3','RC4','RC5','EXT','APR') AND c.ActionBy=:empId AND m.projectid='0' AND DATE(a.CreatedDate) \r\n"
		+ "BETWEEN :fromDate AND :tdate GROUP BY a.EnoteId ORDER BY EnoteId DESC";
	@Override
	public List<Object[]> eNoteApprovalList(long empId, String fromDate, String tdate) throws Exception {
	
		Query query = manager.createNativeQuery(ENOTEAPPROVELIST);
		
		query.setParameter("empId", empId);
		query.setParameter("fromDate", fromDate);
		query.setParameter("tdate", tdate);
		return (List<Object[]>)query.getResultList();
	}
	
	private static final String ENOTEPRINT=" SELECT tra.EnoteTransId,(SELECT emp_id FROM pms_enote_trans t ,\r\n"
			+ " employee e  WHERE e.emp_id = t.Actionby AND t.EnoteStatusCode =  sta.EnoteStatusCode AND\r\n"
			+ " t.EnoteId=par.EnoteId ORDER BY t.EnoteTransId DESC LIMIT 1) AS 'empid',\r\n"
			+ " (SELECT emp_name FROM pms_enote_trans t , employee e  WHERE e.emp_id = t.Actionby AND t.EnoteStatusCode =  sta.EnoteStatusCode AND t.EnoteId=par.EnoteId ORDER BY t.EnoteTransId DESC LIMIT 1) AS 'empname',\r\n"
			+ " (SELECT des.designation FROM pms_enote_trans t ,employee e,employee_desig des WHERE e.emp_id = t.Actionby AND e.desig_id=des.desig_id AND t.EnoteStatusCode =  sta.EnoteStatusCode AND t.EnoteId=par.EnoteId ORDER BY t.EnoteTransId DESC LIMIT 1) AS 'Designation', MAX(tra.ActionDate) AS ActionDate,(SELECT t.Remarks FROM pms_enote_trans t ,\r\n"
			+ " employee e  WHERE e.emp_id = t.Actionby AND t.EnoteStatusCode =  sta.EnoteStatusCode AND t.EnoteId=par.EnoteId ORDER BY t.EnoteTransId DESC LIMIT 1) AS 'Remarks',\r\n"
			+ " sta.EnoteStatus,sta.EnoteStatusColor,sta.EnoteStatusCode,par.InitiatedBy FROM \r\n"
			+ " pms_enote_trans tra,dak_enote_status sta,employee emp,pms_enote par WHERE par.EnoteId=tra.EnoteId\r\n"
			+ " AND tra.EnoteStatusCode =sta.EnoteStatusCode AND sta.EnoteStatusCode IN ('FWD','RFD','RC1','RC2','RC3','RC4','RC5','APR') \r\n"
			+ " AND tra.Actionby=emp.emp_id AND par.EnoteId=:enoteId AND tra.EnoteFrom =:type GROUP BY tra.EnoteStatusCode,tra.EnoteTransId,sta.EnoteStatus,sta.EnoteStatusColor ORDER BY ActionDate ASC";
	
	@Override
	public List<Object[]> EnotePrintDetails(long enoteId,String type) throws Exception {
		Query query = manager.createNativeQuery(ENOTEPRINT);
		query.setParameter("enoteId", enoteId);
		query.setParameter("type", type);
		return (List<Object[]>)query.getResultList();
	}
	
	private static final String GETATTACHMENTID="SELECT AgendaDocid,FileDocId FROM committee_schedule_agenda_docs WHERE AgendaId=:agendaid AND IsActive='1'";
	@Override
	public List<Object[]> getAgendaAttachId(String agendaid) throws Exception {
		Query query = manager.createNativeQuery(GETATTACHMENTID);
		query.setParameter("agendaid", Long.parseLong(agendaid));
		return (List<Object[]>) query.getResultList();
	}
	
	@Override
	public long addAgendaLinkFile(CommitteeScheduleAgendaDocs docs) throws Exception {
		manager.persist(docs);
		manager.flush();
		return docs.getAgendaDocid();
	}
	
	private static final String MOMAPRVLIST = "SELECT MAX(a.EnoteId) AS EnoteId,MAX(a.RefNo) AS RefNo,MAX(a.RefDate) AS RefDate,MAX(a.Subject) AS SUBJECT,MAX(a.Comment) AS COMMENT,MAX(a.InitiatedBy) AS InitiatedBy,MAX(c.ActionDate) AS ActionDate,\r\n"
			+ "MAX(d.EnoteStatus) AS EnoteStatus,MAX(d.EnoteStatusColor) AS EnoteStatusColor,MAX(d.EnoteStatusCode) AS EnoteStatusCode,MAX(cm.CommitteeShortName) AS CommitteeShortName,\r\n"
			+ "MAX(p.project_short_name) AS ProjectShortName,a.CommitteeMainId,a.ScheduleId \r\n"
			+ "FROM pms_enote a,employee b,pms_enote_trans c, dak_enote_status d, committee_main m ,committee cm, project_master p,committee_schedule cs \r\n"
			+ "WHERE a.InitiatedBy=b.emp_id \r\n"
			+ "AND a.EnoteStatusCode=d.EnoteStatusCode AND a.EnoteId=c.EnoteId  AND cs.ScheduleId=a.ScheduleId AND m.CommitteeMainId=cs.CommitteeMainId AND m.projectid = p.project_id \r\n"
			+ "AND c.EnoteStatusCode IN ('RC1','RC2','RC3','RC4','RC5','EXT','APR') AND c.ActionBy=:empId AND DATE(a.CreatedDate) BETWEEN :fromDate AND :tdate  AND a.EnoteFrom='S' GROUP BY a.EnoteId 	\r\n"
			+ "UNION\r\n"
			+ "SELECT MAX(a.EnoteId) AS EnoteId,MAX(a.RefNo) AS RefNo,MAX(a.RefDate) AS RefDate,MAX(a.Subject) AS SUBJECT,MAX(a.Comment) AS COMMENT,MAX(a.InitiatedBy) AS InitiatedBy,MAX(c.ActionDate) AS ActionDate,\r\n"
			+ "MAX(d.EnoteStatus) AS EnoteStatus,MAX(d.EnoteStatusColor) AS EnoteStatusColor,MAX(d.EnoteStatusCode) AS EnoteStatusCode,MAX(cm.CommitteeShortName) AS CommitteeShortName,\r\n"
			+ "'Non-Project' AS ProjectShortName ,a.CommitteeMainId,a.ScheduleId \r\n"
			+ "FROM pms_enote a,employee b,pms_enote_trans c,dak_enote_status d, committee_main m ,committee cm, committee_schedule cs \r\n"
			+ "WHERE a.InitiatedBy=b.emp_id\r\n"
			+ "AND a.EnoteStatusCode=d.EnoteStatusCode AND a.EnoteId=c.EnoteId  AND cs.ScheduleId=a.ScheduleId AND m.CommitteeMainId=cs.CommitteeMainId AND m.projectid = '0' \r\n"
			+ "AND c.EnoteStatusCode IN ('RC1','RC2','RC3','RC4','RC5','EXT','APR') AND c.ActionBy=:empId AND DATE(a.CreatedDate) \r\n"
			+ "BETWEEN :fromDate AND :tdate AND a.EnoteFrom='S'  GROUP BY a.EnoteId";
	
		@Override
		public List<Object[]> MomeNoteApprovalList(long empId, String fromDate, String toDate) throws Exception {
			Query query = manager.createNativeQuery(MOMAPRVLIST);
			
			query.setParameter("empId", empId);
			query.setParameter("fromDate", fromDate);
			query.setParameter("tdate", toDate);
			return (List<Object[]>)query.getResultList();
		}
		
		private static final String CARSSCHEDULELIST = "SELECT cs.ScheduleId, cs.CommitteeId, cs.CommitteeMainId, cs.ScheduleDate, cs.ScheduleStartTime, cs.CARSInitiationId, GetCommitteeShortName(cs.scheduledate,c.committeeshortname) AS committeeshortname FROM committee_schedule cs,committee c WHERE cs.CommitteeId=c.CommitteeId AND cs.ProjectId=0 AND cs.Divisionid=0 AND cs.InitiationId=0 AND cs.CARSInitiationId=:CARSInitiationId AND cs.IsActive=1";
		@Override
		public List<Object[]> carsScheduleList(String carsInitiationId) throws Exception
		{
			try {
				Query query=manager.createNativeQuery(CARSSCHEDULELIST);
				query.setParameter("CARSInitiationId", Long.parseLong(carsInitiationId));
				return (List<Object[]>)query.getResultList();
			}catch (Exception e) {
				e.printStackTrace();
				return new ArrayList<Object[]>();
			}
			
		}
		
		private static final String CARSMEETINGCOUNT = "SELECT COUNT(*) FROM committee_schedule WHERE CARSInitiationId=:CARSInitiationId AND IsActive=1 ";
		@Override
		public Long carsMeetingCount(String carsInitiationId) throws Exception {
			try {
				Query query=manager.createNativeQuery(CARSMEETINGCOUNT);
				query.setParameter("CARSInitiationId", Long.parseLong(carsInitiationId));
				return (Long) query.getSingleResult();
			}catch (Exception e) {
				e.printStackTrace();
				return 0L;
			}
		
		}
		
		@Override
		public void InvitationRoleoUpdate(String empMeetingRole, String committeeinvitationid) throws Exception {
			
			CommitteeInvitation ExistingCommitteeInvitation = manager.find(CommitteeInvitation.class, Long.parseLong(committeeinvitationid));
			if(ExistingCommitteeInvitation != null) {
				
				ExistingCommitteeInvitation.setEmpMeetingRole(empMeetingRole);
			}
			
		}
		
		
		private static final String MEETINGCHECK="""
				
				SELECT a.ScheduleId,a.MeetingId,a.MeetingVenue,b.empid,CONCAT(IFNULL(CONCAT(c.title,' '),IFNULL(CONCAT(c.salutation,' '),'')), c.emp_name) AS 'empname',b.labcode,a.ScheduleStartTime,d.Description
				FROM committee_schedule a, committee_member_type d,
				committee_member b ,employee c WHERE a.ScheduleDate = :adate
				AND b.CommitteeMainId = a.CommitteeMainId  AND b.empid=c.emp_id AND b.labcode=c.lab_code AND b.empid IN 
				(SELECT  EmpId FROM committee_member WHERE  CommitteeMainId =:committeemainid AND labcode<> '@EXP' AND labcode<> '@IP')  AND b.MemberType = d.MemberType AND a.isactive='1'
				UNION
				SELECT a.ScheduleId,a.MeetingId,a.MeetingVenue,b.empid,CONCAT(IFNULL(CONCAT(c.title,' '),IFNULL(CONCAT(c.salutation,' '),'')), c.ExpertName) AS 'empname',b.labcode,a.ScheduleStartTime,d.Description
				FROM committee_schedule a,  committee_member_type d,
				committee_member b ,expert c WHERE a.ScheduleDate = :adate
				AND b.CommitteeMainId = a.CommitteeMainId  AND b.empid=c.expertid  AND b.empid IN 
				(SELECT  EmpId FROM committee_member WHERE  CommitteeMainId =:committeemainid AND labcode= '@EXP') AND b.MemberType = d.MemberType AND b.labcode ='@EXP' AND a.isactive='1'
				UNION
				SELECT a.ScheduleId,a.MeetingId,a.MeetingVenue,b.empid,CONCAT(IFNULL(CONCAT(c.title,' '),IFNULL(CONCAT(c.salutation,' '),'')), c.emp_name) AS 'empname',b.labcode,a.ScheduleStartTime,d.Description
				FROM committee_schedule a,committee_member_type d,
				committee_schedules_invitation b ,employee c WHERE a.ScheduleDate = :adate
				AND b.CommitteeScheduleId = a.ScheduleId AND 
				b.EmpId IN 
				(SELECT  EmpId FROM committee_member 
				WHERE  CommitteeMainId =:committeemainid AND isactive=1 AND LabCode<>'@EXP' AND LabCode<>'@IP') AND b.MemberType = d.MemberType AND a.isactive='1'
				AND b.empid=c.emp_id  
				UNION 
				SELECT a.ScheduleId,a.MeetingId,a.MeetingVenue,b.empid,CONCAT(IFNULL(CONCAT(c.title,' '),IFNULL(CONCAT(c.salutation,' '),'')), c.ExpertName) AS 'empname' ,b.labcode,a.ScheduleStartTime,d.Description
				FROM committee_schedule a, committee_member_type d, committee_schedules_invitation b ,expert c WHERE a.ScheduleDate = :adate
				AND b.CommitteeScheduleId = a.ScheduleId AND 
				b.EmpId IN 
				(SELECT  EmpId FROM committee_member 
				WHERE  CommitteeMainId =:committeemainid AND isactive=1 AND LabCode='@EXP') AND b.MemberType = d.MemberType AND a.isactive='1'
				""";
		
		@Override
		public List<MeetingCheckDto> getMeetingCheckDto(String date, String committeemainid) throws Exception {
			
			List<MeetingCheckDto>dto = new ArrayList<>();
			try {
				
				Query query = manager.createNativeQuery(MEETINGCHECK);
				query.setParameter("adate", date);
				query.setParameter("committeemainid", committeemainid);

				
				
				List<Object[]>list = (List<Object[]>)query.getResultList();
				
				
			
				if(list!=null && !list.isEmpty()) {
					dto = list.stream()
							.map( e->MeetingCheckDto.builder()
									.ScheduleId( Long.parseLong(e[0].toString() ) )
									.empid(Long.parseLong(e[3].toString()))
									.MeetingId( e[1].toString())
									.MeetingVenue(e[2].toString() )
									.empname( e[4].toString())
									.labcode( e[5].toString())
									.ScheduleStartTime(e[6].toString())
									.description(e[7].toString())
									.build())
							.collect(Collectors.toList());			
					}
				
			}catch (Exception e) {
			
				e.printStackTrace();	
				
			}
			
			
			return dto;
		}
		
		
		private static final String MEETINGCHECKEMPWISE= "SELECT a.ScheduleId,a.MeetingId,a.MeetingVenue, a.ScheduleStartTime , c.Description\r\n"
				+ "FROM committee_schedule a,committee_schedules_invitation b\r\n"
				+ ",committee_member_type c\r\n"
				+ "WHERE a.ScheduleDate=(SELECT ScheduleDate FROM committee_schedule WHERE ScheduleId = :scheduleid) \r\n"
				+ "AND a.ScheduleId <> :scheduleid  AND \r\n"
				+ "b.CommitteeScheduleId = a.ScheduleId AND b.empid =:empid AND b.labcode= :labocode AND c.MemberType=b.MemberType";
		
		
		@Override
		public List<MeetingCheckDto> getMeetingCheckDto(String empid, String labocode,String scheduleid) throws Exception {
			List<MeetingCheckDto>dto = new ArrayList<>();
			try {
			Query query = manager.createNativeQuery(MEETINGCHECKEMPWISE);
			query.setParameter("empid", empid)	;	
			query.setParameter("labocode", labocode)	;	
			query.setParameter("scheduleid", scheduleid)	;	
			
			List<Object[]>list = (List<Object[]>)query.getResultList();
			
			if(list!=null && !list.isEmpty()) {
				dto = list.stream()
						.map( e->MeetingCheckDto.builder()
								.ScheduleId( Long.parseLong(e[0].toString() ) )
								.MeetingId( e[1].toString())
								.MeetingVenue(e[2].toString() )
								.ScheduleStartTime(e[3].toString())
								.description(e[4].toString())
								.build())
						.collect(Collectors.toList());			
				}
			
		}catch (Exception e) {
		
			e.printStackTrace();	
			
		}
		
		
		return dto;
		}
		
		private static final String PREVIOUSMEETINGS="SELECT a.ScheduleId,a.MeetingId,a.ScheduleDate,a.projectId,a.InitiationId,a.Divisionid,a.ScheduleFlag, GetCommitteeShortName(a.scheduledate,b.committeeshortname) AS committeeshortname, a.CARSInitiationId, a.ProgrammeId FROM committee_schedule a, committee b  \r\n"
				+ "			WHERE a.CommitteeId = :committeeid AND a.isactive = 1  AND a.ScheduleFlag IN ( 'MKV','MMR','MMF','MMS','MMA' ) AND a.CommitteeId = b.CommitteeId ORDER BY ScheduleDate ";	
		
	@Override
	public List<Object[]> previousMeetingHeld(String committeeid) throws Exception {
		
		Query query  = manager.createNativeQuery(PREVIOUSMEETINGS);
		query.setParameter("committeeid", committeeid);
		return (List<Object[]>)query.getResultList() ;
	}
	
	private static final String RECOMMENDATIONS = "SELECT d.ActionAssignId,d.ActionNo,CONCAT(IFNULL(CONCAT(e.title,' '),IFNULL(CONCAT(e.salutation,' '),'')), e.emp_name) AS 'empname',\r\n"
			+ "d.ActionStatus,c.ActionMainId,c.ActionItem,b.ScheduleMinutesId,a.ScheduleId,a.MeetingId,a.ScheduleDate,d.PDCOrg,d.PDC1,d.PDC2,d.Progress,d.ProgressDate,a.projectId,a.InitiationId,a.Divisionid, a.CARSInitiationId, a.ProgrammeId\r\n"
			+ "FROM committee_schedule a, committee_schedules_minutes_details b , action_main c,action_assign d, employee e\r\n"
			+ "WHERE a.CommitteeId = :committeeid AND a.isactive = 1 AND a.ScheduleId=b.ScheduleId AND c.type='R'   AND b.IDARCK = 'R'\r\n"
			+ "AND c.ScheduleMinutesId=b.ScheduleMinutesId AND c.ActionMainId=d.ActionMainId AND d.Assignee=e.emp_id AND d.AssigneeLabCode <> '@EXP'  					\r\n"
			+ "UNION 	\r\n"
			+ "SELECT d.ActionAssignId,d.ActionNo,CONCAT(IFNULL(CONCAT(e.title,' '),IFNULL(CONCAT(e.salutation,' '),'')), e.expertname) AS 'empname',\r\n"
			+ "d.ActionStatus,c.ActionMainId,c.ActionItem,b.ScheduleMinutesId,a.ScheduleId,a.MeetingId,a.ScheduleDate,d.PDCOrg,d.PDC1,d.PDC2,d.Progress,d.ProgressDate,a.projectId,a.InitiationId,a.Divisionid, a.CARSInitiationId, a.ProgrammeId\r\n"
			+ "FROM committee_schedule a, committee_schedules_minutes_details b , action_main c,action_assign d, expert e\r\n"
			+ "WHERE a.CommitteeId = :committeeid AND a.isactive = 1 AND a.ScheduleId=b.ScheduleId AND  c.type='R'   AND b.IDARCK = 'R'\r\n"
			+ "AND c.ScheduleMinutesId=b.ScheduleMinutesId AND c.ActionMainId=d.ActionMainId AND d.Assignee=e.expertid AND d.AssigneeLabCode = '@EXP'  ORDER BY ScheduleDate ASC";
	
	
	@Override
	public List<Object[]> getRecommendationsOfCommittee(String committeeid) throws Exception {

		Query query = manager.createNativeQuery(RECOMMENDATIONS);	
		query.setParameter("committeeid", committeeid);		
		
		return (List<Object[]>)query.getResultList();
	}
	
	
	private static final String DECESIONS = "SELECT b.ScheduleMinutesId,b.Details,a.scheduledate, a.projectId,a.InitiationId,a.Divisionid, a.CARSInitiationId, a.ProgrammeId FROM committee_schedule a,\r\n"
			+ "committee_schedules_minutes_details b WHERE b.idarck ='D' AND a.CommitteeId = :committeeid  AND a.isactive = 1 AND a.ScheduleId=b.ScheduleId ";
	@Override
	public List<Object[]> getDecisionsofCommittee(String committeeid) throws Exception {
		
		Query query  = manager.createNativeQuery(DECESIONS);
		query.setParameter("committeeid", committeeid);
		return (List<Object[]>)query.getResultList();
	}
	
	@Override
	public CommitteeSchedule getCommitteeScheduleById(Long scheduleId) throws Exception {
		
		CommitteeSchedule committeeSchedule= manager.find(CommitteeSchedule.class, scheduleId);
		return committeeSchedule;
	}

	/* ********************************************* Programme AD ************************************************ */
	@Override
	public List<ProgrammeMaster> getProgrammeMasterList() throws Exception {
		try {
			Query query = manager.createQuery("FROM ProgrammeMaster WHERE IsActive=1");
			return (List<ProgrammeMaster>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<ProgrammeMaster>();
		}
	}
	
	private static final String GETCOMMITTEEMAINIDBYCOMMITTEECODE = "SELECT COALESCE((SELECT a.CommitteeMainId FROM committee_main a INNER JOIN committee b ON a.CommitteeId = b.CommitteeId WHERE a.ProjectId = '0' AND a.DivisionId = '0' AND a.InitiationId = '0' AND a.ProgrammeId<>0 AND a.ProgrammeId =:ProgrammeId AND CURDATE() BETWEEN a.ValidFrom AND a.ValidTo AND a.IsActive = 1 ORDER BY a.CommitteeMainId DESC LIMIT 1), 0) AS CommitteeMainId";
	@Override
	public Long getCommitteeMainIdByProgrammeId(String programmeId) throws Exception {
		try {
			Query query = manager.createNativeQuery(GETCOMMITTEEMAINIDBYCOMMITTEECODE);
			//query.setParameter("LabCode", labCode);
			query.setParameter("ProgrammeId", Long.parseLong(programmeId));
			return (Long)query.getSingleResult();
		}catch ( Exception e ) {
			e.printStackTrace();
			return 0L;
		}
	}

	private static final String PROGRAMMESCHEDULELIST = "SELECT cs.ScheduleId, cs.CommitteeId, cs.CommitteeMainId, cs.ScheduleDate, cs.ScheduleStartTime, cs.ProgrammeId, c.CommitteeShortName FROM committee_schedule cs,committee c WHERE cs.CommitteeId=c.CommitteeId AND cs.ProjectId=0 AND cs.Divisionid=0 AND cs.InitiationId=0 AND cs.CARSInitiationId=0 AND cs.ProgrammeId=:ProgrammeId AND cs.IsActive=1 ORDER BY cs.ScheduleDate DESC";
	@Override
	public List<Object[]> prgmScheduleList(String programmeId) throws Exception {
		try {
			Query query=manager.createNativeQuery(PROGRAMMESCHEDULELIST);
			query.setParameter("ProgrammeId", programmeId);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
		
	}
	
	private static final String PRGMMEETINGCOUNT = "SELECT COUNT(*) FROM committee_schedule WHERE ProgrammeId=:ProgrammeId AND IsActive=1 ";
	@Override
	public Long prgmMeetingCount(String programmeId) throws Exception {
		try {
			Query query=manager.createNativeQuery(PRGMMEETINGCOUNT);
			query.setParameter("ProgrammeId", programmeId);
			return (Long) query.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	
	}
	
	@Override
	public List<ProgrammeProjects> getProgrammeProjectsList(String programmeId) throws Exception {
		try {
			Query query = manager.createQuery("FROM ProgrammeProjects WHERE IsActive=1 AND ProgrammeId=:ProgrammeId");
			query.setParameter("ProgrammeId", Long.parseLong(programmeId));
			return (List<ProgrammeProjects>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<ProgrammeProjects>();
		}
	}

	private static final String PROGRAMMEPROJECTLIST = "SELECT b.project_id, b.project_code, b.project_short_name, b.project_name, c.emp_id, CONCAT(IFNULL(CONCAT(c.title,' '),(IFNULL(CONCAT(c.salutation, ' '), ''))), c.emp_name) AS 'EmpName', d.Designation, c.lab_code FROM pfms_programme_projects a, project_master b, employee c, employee_desig d WHERE a.IsActive=1 AND a.ProjectId=b.project_id AND b.project_director=c.emp_id AND c.desig_Id=d.desig_Id AND a.ProgrammeId=:ProgrammeId";
	@Override
	public List<Object[]> prgmProjectList(String programmeId) throws Exception {
		try {
			Query query=manager.createNativeQuery(PROGRAMMEPROJECTLIST);
			query.setParameter("ProgrammeId", Long.parseLong(programmeId));
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
		
	}

	@Override
	public ProgrammeMaster getProgrammeMasterById(String programmeId) throws Exception {
		try {
			return manager.find(ProgrammeMaster.class, Long.parseLong(programmeId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/* ********************************************* Programme AD End ************************************************ */
	@Override
	public long sentMomDraft(CommitteScheduleMinutesDraft cmd) throws Exception {
		manager.persist(cmd);
		manager.flush();
		return cmd.getDraftId();
	}
	
	@Override
	public List<CommitteScheduleMinutesDraft> getCommitteScheduleMinutesDraftList()
			throws Exception {
	
		String jpql = "SELECT p FROM CommitteScheduleMinutesDraft p";
		
		  try {
		        return (List<CommitteScheduleMinutesDraft>)manager.createQuery(jpql, CommitteScheduleMinutesDraft.class)
		                 .getResultList();
		    } catch (NoResultException e) {
		        return new ArrayList<>(); // or throw a custom exception if preferred
		    }
	}
	

	@Override
	public List<Object[]> getdraftMomList(String empid, String pageSize) throws Exception {
			
		
		
		String dynamicQuery = "SELECT a.MeetingId, a.ProjectId, a.Divisionid, a.InitiationId, a.ScheduleDate, a.ScheduleStartTime, b.scheduleId "
			    + "FROM committee_schedule a "
			    + "JOIN committee_schedules_mom_draft b ON a.scheduleId = b.scheduleId "
			    + "WHERE b.empid = :empid AND a.ScheduleFlag = 'MKV' "
			    + "LIMIT " + pageSize;
		
		Query query = manager.createNativeQuery(dynamicQuery);
		query.setParameter("empid", empid);	
			
		return (List<Object[]>)query.getResultList() ;
	}
	
	
	@Override
	public long saveCommitteeSchedulesMomDraftRemarks(CommitteeSchedulesMomDraftRemarks cmd) throws Exception {
		manager.persist(cmd);	
		manager.flush();
		
		return cmd.getRemarksId();
	}
	
	
	private static final String DRAFTREMARKS = "SELECT CONCAT(IFNULL(CONCAT(b.title,' '),IFNULL(CONCAT(b.salutation,' '),'')), b.empname) AS 'empname',c.designation , \r\n"
			+ "a.remarks , a.createdDate,a.empid, a.scheduleid  FROM employee b ,committee_schedules_mom_draft_remarks a, employee_desig c WHERE a.empid=b.empid AND b.desigid=c.desigid\r\n"
			+ "AND a.scheduleid=:scheduleid  AND a.isactive=1";
	@Override
	public List<Object[]> getCommitteeSchedulesMomDraftRemarks(Long scheduleid) throws Exception {
	
		Query query   = manager.createNativeQuery(DRAFTREMARKS)	;
		query.setParameter("scheduleid", scheduleid);
		
		
		return (List<Object[]>)query.getResultList();
	}
	
	
	private static final String PREPROJECTLIST="SELECT InitiationId,MainId,ProjectProgramme,ProjectTitle,ProjectShortName FROM pfms_initiation WHERE isactive=1 and labcode=:labCode";
	@Override
	public List<Object[]> preProjectlist(String labCode) throws Exception {
		Query query=manager.createNativeQuery(PREPROJECTLIST);
		query.setParameter("labCode", labCode);
		List<Object[]> preProjectlist=(List<Object[]>)query.getResultList();
		return preProjectlist;
	}
	
	@Override
	public FileRepUploadPreProject getPreProjectAgendaDocById(String filerepid) throws Exception {
		FileRepUploadPreProject repmaster=manager.find(FileRepUploadPreProject.class,Long.parseLong(filerepid));
		return repmaster;
	}
	
	private static final String CARSCOMMITTEEDESCRIPTIONTOR ="SELECT ComCARSInitiationId, Description, TermsOfReference, CommitteeId, CARSInitiationId FROM committee_cars WHERE CommitteeId=:CommitteeId AND CARSInitiationId=:CARSInitiationId";
	@Override
	public Object[] carsCommitteeDescriptionTOR(String carsInitiationId, String committeeId) throws Exception
	{		
		try {
			Query query=manager.createNativeQuery(CARSCOMMITTEEDESCRIPTIONTOR);
			query.setParameter("CommitteeId", committeeId);	
			query.setParameter("CARSInitiationId", carsInitiationId);	
			return (Object[])query.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	@Override
	public CommitteeCARS getCommitteeCARSById(String comCARSInitiationId) throws Exception {
		try {
			return manager.find(CommitteeCARS.class, Long.parseLong(comCARSInitiationId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public Long addCommitteeCARS(CommitteeCARS committeeCARS) throws Exception {
		try {
			manager.persist(committeeCARS);
			manager.flush();
			return committeeCARS.getComCARSInitiationId();
		}catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	}
	
//	---------------------------------- Naveen R 3/9/25 MOM Check ------------------------------------------

	private static final String AGENDAACTIONLIST = """
		SELECT DISTINCT a.ScheduleMinutesId, a.Details, a.ScheduleId, a.MinutesId, a.ScheduleSubId, a.MinutesSubOfSubId, a.MinutesSubId, a.idarck, 
			a.remarks, b.outcomename, CASE WHEN a.MinutesId = 3 THEN c.agendaitem ELSE 'Other Outcomes' END AS agenda, a.agendasubhead, e.PDCOrg, 
			CASE  WHEN e.AssigneeLabCode = '@EXP' THEN CONCAT(IFNULL(CONCAT(j.Title, ' '), IFNULL(CONCAT(j.Salutation, ' '), '')), 
			j.ExpertName, ', ', g.Designation) ELSE CONCAT(IFNULL(CONCAT(f.Title, ' '), IFNULL(CONCAT(f.Salutation, ' '), '')), f.emp_name, ', ', g.Designation) 
			END AS Assignees, e.ActionNo, c.AgendaPriority , (SELECT CONCAT(IFNULL(CONCAT(z.Title, ' '), IFNULL(CONCAT(z.Salutation, ' '), '')), z.emp_name, ', ', w.Designation) 
			FROM employee z LEFT JOIN employee_desig w ON w.desig_id=z.desig_id WHERE z.emp_id = c.PresenterId ) AS Presenter, c.PresenterID, c. ScheduleAgendaId , c.GroupName, divi.division_id ,divi.division_name, divi.division_code
			FROM committee_schedules_minutes_details a JOIN committee_schedules_minutes_outcome b ON a.idarck = b.idarck JOIN committee_schedules_agenda c 	
			ON c.scheduleagendaid = a.minutessubid LEFT JOIN action_main d ON a.ScheduleMinutesId = d.ScheduleMinutesId LEFT JOIN action_assign e ON 
			d.ActionMainId = e.ActionMainId AND e.isActive = 1 LEFT JOIN employee f ON e.Assignee = f.emp_id LEFT JOIN employee_desig g ON g.desig_id = f.desig_id LEFT JOIN expert j 
			ON e.Assignee = j.ExpertId LEFT JOIN division_master divi ON divi.division_id = f.division_id 
			WHERE a.scheduleid = :InScheduleId ORDER BY CASE WHEN MinutesId = 3 THEN 1 ELSE 3 END, AgendaPriority, minutesSubOfSubId, agenda, ScheduleMinutesId ,idarck, ActionNo IS NULL;
			""";
	@Override
	public List<Object[]> CommitteeScheduleMinutesforAction(String committeescheduleid) throws Exception {
		Query query=manager.createNativeQuery(AGENDAACTIONLIST); 
		query.setParameter("InScheduleId", Long.parseLong(committeescheduleid));
		List<Object[]> CommitteeScheduleMinutes =(List<Object[]>)query.getResultList();
		return CommitteeScheduleMinutes;
	}

	@Override
	public Long addRepresentative(CommitteeRepresentative rep) throws Exception {
		try {
			manager.persist(rep);
			manager.flush();
			return rep.getRepId();
		}catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	}

	@Override
	public CommitteeRepresentative getRepresentativeById(String repId) throws Exception {
		try {
			return manager.find(CommitteeRepresentative.class, Long.parseLong(repId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static final String DUPLICATEREPNAMECOUNT = "SELECT COUNT(RepName) FROM committee_rep WHERE RepName=:RepName";
	@Override
	public Long getRepNameDuplicateCount(String repName) throws Exception {
		try {
			Query query = manager.createNativeQuery(DUPLICATEREPNAMECOUNT);
			query.setParameter("RepName", repName);
			return (Long)query.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	}

	private static final String DUPLICATEREPCODECOUNT = "SELECT COUNT(RepCode) FROM committee_rep WHERE RepCode=:RepCode";
	@Override
	public Long getRepCodeDuplicateCount(String repCode) throws Exception {
		try {
			Query query = manager.createNativeQuery(DUPLICATEREPCODECOUNT);
			query.setParameter("RepCode", repCode);
			return (Long)query.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	}

	private static final String AIRCRAFTLIST = "SELECT AircraftId, Aircraft FROM committee_aircraft ORDER BY AircraftId; ";
	@Override
	public List<Object[]> getAircraftList(String committeeScheduleId) throws Exception {
		Query query=manager.createNativeQuery(AIRCRAFTLIST);
		List<Object[]> aircraftList =(List<Object[]>)query.getResultList();
		return aircraftList;
	}

	private static final String SUBSYSTEMLIST = "SELECT SubSystemId, SubSystem FROM committee_subsystem ORDER BY SubSystemId; ";
	@Override
	public List<Object[]> getSubSystemList(String committeeScheduleId) throws Exception {
		Query query=manager.createNativeQuery(SUBSYSTEMLIST);
		List<Object[]> subsystemList =(List<Object[]>)query.getResultList();
		return subsystemList;
	}

	@Override
	public Long addAircraft(CommitteeAircraft aircraft) throws Exception {
		try {
			manager.persist(aircraft);
			manager.flush();
			return aircraft.getAircraftId();
		}catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	}

	@Override
	public CommitteeAircraft getAircraftById(String aircraftId) throws Exception {
		try {
			return manager.find(CommitteeAircraft.class, Long.parseLong(aircraftId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Long addSubSystem(CommitteeSubSystem sub) throws Exception {
		try {
			manager.persist(sub);
			manager.flush();
			return sub.getSubSystemId();
		}catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	}

	@Override
	public CommitteeSubSystem getSubSystemById(String subsystemId) throws Exception {
		try {
			return manager.find(CommitteeSubSystem.class, Long.parseLong(subsystemId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static final String ACTIONLISTFORMOMADE =" SELECT DISTINCT a.ScheduleMinutesId, a.Details, a.ScheduleId, a.MinutesId, a.ScheduleSubId, a.MinutesSubOfSubId, a.MinutesSubId, a.idarck, "
			+ "a.remarks, b.outcomename, CASE WHEN a.MinutesId = 3 THEN c.agendaitem ELSE 'Other Outcomes' END AS agenda, a.agendasubhead, e.PDCOrg, "
			+ "CASE  WHEN e.AssigneeLabCode = '@EXP' THEN CONCAT(IFNULL(CONCAT(j.Title, ' '), IFNULL(CONCAT(j.Salutation, ' '), '')), "
			+ "j.ExpertName, ', ', gd.Designation) ELSE CONCAT(IFNULL(CONCAT(f.Title, ' '), IFNULL(CONCAT(f.Salutation, ' '), '')), f.EmpName, ', ', g.Designation) "
			+ "END AS Assignees, e.ActionNo, h.Aircraft, i.SubSystem, c.AgendaPriority "
			+ "FROM committee_schedules_minutes_details a JOIN committee_schedules_minutes_outcome b ON a.idarck = b.idarck JOIN committee_schedules_agenda c "
			+ "ON c.scheduleagendaid = a.minutessubid LEFT JOIN action_main d ON a.ScheduleMinutesId = d.ScheduleMinutesId LEFT JOIN action_assign e ON "
			+ "d.ActionMainId = e.ActionMainId AND e.isActive = 1 LEFT JOIN employee f ON e.Assignee = f.EmpId LEFT JOIN employee_desig g ON g.DesigId = f.DesigId "
			+ "LEFT JOIN committee_aircraft h ON h.AircraftId = a.AircraftId LEFT JOIN committee_subsystem i ON i.SubSystemId = a.SubSystemId LEFT JOIN expert j "
			+ "ON e.Assignee = j.ExpertId LEFT JOIN employee_desig gd ON gd.DesigId = j.DesigId  WHERE a.scheduleid = :InScheduleId "
			+ "ORDER BY CASE WHEN MinutesId = 3 THEN 1 ELSE 3 END, AgendaPriority,agenda,idarck,ActionNo,ScheduleMinutesId";
	@Override
	public List<Object[]> committeeScheduleMinutesforActionForMomADE(String committeescheduleid)  throws Exception{
		Query query=manager.createNativeQuery(ACTIONLISTFORMOMADE);
		query.setParameter("InScheduleId", Long.parseLong(committeescheduleid));
		List<Object[]> CommitteeScheduleMinutes =(List<Object[]>)query.getResultList();
		return CommitteeScheduleMinutes;
	}

	private static final String COMMITTEESCHEDULEEDITFORMOM = """
			SELECT a.committeeid,a.committeemainid,a.scheduledate,a.schedulestarttime,a.scheduleflag,a.schedulesub,a.scheduleid, 
			GetCommitteeName(a.scheduledate,b.committeeshortname,b.committeename) AS committeename,GetCommitteeShortName(a.scheduledate,b.committeeshortname) AS committeeshortname,a.projectid,c.meetingstatusid,a.meetingid,a.meetingvenue,a.confidential,a.Reference, 
			(SELECT d.classification FROM pfms_security_classification d WHERE a.confidential=d.classification_id) AS 'classification',a.divisionid  ,
			a.initiationid ,a.pmrcdecisions,a.kickoffotp ,(SELECT minutesattachmentid 
			FROM committee_minutes_attachment WHERE scheduleid=a.scheduleid) AS 'attachid', 
			b.periodicNon,a.MinutesFrozen,a.briefingpaperfrozen,a.labcode, a.CARSInitiationId, a.ProgrammeId ,d.ReferenceNo, d.FormationDate 
			FROM committee_schedule a,committee b ,committee_meeting_status c,committee_main d WHERE a.scheduleflag=c.MeetingStatus AND 
			a.scheduleid=:committeescheduleid AND a.committeeid=b.committeeid AND d.committeeId = b.committeeid AND d.ProgrammeId = a.ProgrammeId;
			""";
	@Override
	public Object[] CommitteeScheduleEditDataforMom(String committeescheduleid) throws Exception {
		Query query=manager.createNativeQuery(COMMITTEESCHEDULEEDITFORMOM);
		query.setParameter("committeescheduleid", Long.parseLong(committeescheduleid));
		List<Object[]> CommitteeScheduleEditData=(List<Object[]>)query.getResultList();
		return CommitteeScheduleEditData!=null && !CommitteeScheduleEditData.isEmpty() ? CommitteeScheduleEditData.get(0) : null;
	}

	private static final String PRGMAGENDALIST = """
			SELECT a.scheduleagendaid,a.scheduleid,a.schedulesubid,a.agendaitem,b.project_name,b.project_id,a.remarks,b.project_code,
			a.agendapriority,a.presenterid ,CONCAT(IFNULL(CONCAT(j.title,' '),(IFNULL(CONCAT(j.salutation, ' '), ''))), j.emp_name) AS 'EmpName' ,h.designation,a.duration,
			j.desig_id, a.PresentorLabCode, a.GroupName FROM project_master b,employee j,employee_desig h,committee_schedules_agenda a,committee_schedule cs
			WHERE CASE WHEN cs.ProgrammeId<>0 THEN a.ProjectId=b.project_id ELSE a.projectid=cs.projectid AND cs.projectid=b.project_id AND a.scheduleid=cs.scheduleid END AND a.scheduleid=:committeescheduleid AND a.isactive=1 AND a.projectid NOT IN (0, -1) AND a.presenterid=j.emp_id AND 
			j.desig_id=h.desig_id AND a.PresentorLabCode<>'@EXP'
			UNION
			SELECT a.scheduleagendaid,a.scheduleid,a.schedulesubid,a.agendaitem,b.project_name,b.project_id,a.remarks,b.project_code,
			a.agendapriority,a.presenterid ,CONCAT(IFNULL(CONCAT(j.Title,' '),(IFNULL(CONCAT(j.Salutation, ' '), ''))), j.ExpertName) AS 'EmpName' ,h.designation,a.duration,
			j.desigid, a.PresentorLabCode, a.GroupName FROM project_master b,expert j,employee_desig h,committee_schedules_agenda a,committee_schedule cs
			WHERE a.projectid=cs.projectid AND cs.projectid=b.project_id AND a.scheduleid=cs.scheduleid AND a.scheduleid=:committeescheduleid AND a.isactive=1 AND a.projectid NOT IN (0, -1) AND a.presenterid=j.expertid AND 
			j.desigid=h.desig_id AND a.PresentorLabCode='@EXP'
			UNION  
			SELECT a.scheduleagendaid,a.scheduleid,a.schedulesubid,a.agendaitem,cs.labcode AS 'projectname' , '0' AS projectid,
			a.remarks,'' AS projectcode,a.agendapriority,a.presenterid ,CONCAT(IFNULL(CONCAT(j.Title,' '),(IFNULL(CONCAT(j.Salutation, ' '), ''))), j.emp_name) AS 'EmpName' ,
			h.designation,a.duration,j.desig_id, a.PresentorLabCode, a.GroupName FROM employee j,employee_desig h, committee_schedules_agenda a, committee_schedule cs
			WHERE a.scheduleid=:committeescheduleid AND a.scheduleid=cs.scheduleid  AND a.isactive=1 AND a.projectid IN (0, -1)
			AND a.presenterid=j.emp_id AND j.desig_id=h.desig_id AND a.PresentorLabCode<>'@EXP' 
			UNION
			SELECT a.scheduleagendaid,a.scheduleid,a.schedulesubid,a.agendaitem,cs.labcode AS 'projectname' , '0' AS projectid,
			a.remarks,'' AS projectcode,a.agendapriority,a.presenterid ,CONCAT(IFNULL(CONCAT(j.Title,' '),(IFNULL(CONCAT(j.Salutation, ' '), ''))), j.ExpertName) AS 'EmpName' ,
			h.designation,a.duration,j.desigid, a.PresentorLabCode, a.GroupName FROM expert j,employee_desig h, committee_schedules_agenda a, committee_schedule cs
			WHERE a.scheduleid=:committeescheduleid AND a.scheduleid=cs.scheduleid  AND a.isactive=1 AND a.projectid IN (0, -1)
			AND a.presenterid=j.expertid AND j.desigid=h.desig_id AND a.PresentorLabCode='@EXP'
			UNION
			SELECT a.scheduleagendaid,a.scheduleid,a.schedulesubid,a.agendaitem,b.ProjectTitle,b.initiationId,a.remarks,b.ProjectShortName,
			a.agendapriority,a.presenterid ,CONCAT(IFNULL(CONCAT(j.Title,' '),(IFNULL(CONCAT(j.Salutation, ' '), ''))), j.emp_name) AS 'EmpName' ,h.designation,a.duration,
			j.desig_id, a.PresentorLabCode, a.GroupName FROM pfms_initiation b,employee j,employee_desig h,committee_schedules_agenda a,committee_schedule cs
			WHERE a.projectid=cs.initiationId AND cs.initiationId=b.initiationId AND a.scheduleid=cs.scheduleid AND a.scheduleid=:committeescheduleid AND a.isactive=1 AND a.projectid NOT IN (0, -1) AND a.presenterid=j.emp_id AND 
			j.desig_id=h.desig_id AND a.PresentorLabCode<>'@EXP'
			ORDER BY agendapriority 
			""";
	@Override
	public List<Object[]> PrgmAgendaList(String CommitteeScheduleId) throws Exception {
		Query query=manager.createNativeQuery(PRGMAGENDALIST);
		query.setParameter("committeescheduleid", Long.parseLong(CommitteeScheduleId));
		List<Object[]> AgendaList=(List<Object[]>)query.getResultList();
		
		return AgendaList;
	}

	// Naveen R 05-03-2026
	private static final String MEETINGCOUNTPROJECT = """
			SELECT pm.project_short_name, pm.project_code, pm.project_name,cs.project_id,COUNT(cs.project_id) AS meetingcount, c.committeeShortName, c.committeeName
			FROM committee c LEFT JOIN committee_schedule cs ON c.committeeId = cs.committeeId
			LEFT JOIN project_master pm ON cs.projectId = pm.project_id
			WHERE cs.committeeId = :committeeId AND c.isBriefing = 'Y' AND c.isActive = 1 AND pm.project_id > 0 AND cs.isActive = 1 AND cs.ScheduleFlag IN ('MKV','MMR','MMF','MMS','MMA')
			GROUP BY c.committeeShortName, c.committeeName, cs.project_id, pm.project_name, pm.project_short_name;
			"""; 
	@Override
	public List<Object[]> getMeetingCountList(String committeeId) throws Exception {
		Query query=manager.createNativeQuery(MEETINGCOUNTPROJECT);
		query.setParameter("committeeId", Long.parseLong(committeeId));
		List<Object[]> meetingCounts=(List<Object[]>)query.getResultList();		
		return meetingCounts;
	}

	private static final String AGENDASPECLISTORDERWISE = """
			SELECT  a.ScheduleMinutesId, a.Details,a.ScheduleId,a.MinutesId,a.ScheduleSubId,a.MinutesSubOfSubId,a.MinutesSubId,a.idarck,a.remarks,b.outcomename,c.agendaitem AS agenda, a.agendasubhead,c.scheduleAgendaId,c.AgendaPriority
			FROM committee_schedules_minutes_details a,committee_schedules_minutes_outcome b,committee_schedules_agenda c
			WHERE a.scheduleid =:InScheduleId  AND a.idarck=b.idarck AND c.scheduleagendaid=a.minutessubid AND a.minutesid='3'
			
			UNION
			
			SELECT  a.ScheduleMinutesId, a.Details,a.ScheduleId,a.MinutesId,a.ScheduleSubId,a.MinutesSubOfSubId,a.MinutesSubId,a.idarck,a.remarks,b.outcomename,
			CASE WHEN a.minutesid='4' THEN 'Other Discussion' ELSE 'Other Outcomes' END AS agenda, a.agendasubhead, NULL AS scheduleAgendaId, NULL AS AgendaPriority
			FROM committee_schedules_minutes_details a,committee_schedules_minutes_outcome b
			WHERE a.scheduleid =:InScheduleId  AND a.idarck=b.idarck AND a.minutesid<>'3'
			ORDER BY  CASE WHEN minutesid = '3' THEN 1  ELSE 2 END, AgendaPriority, scheduleAgendaId, agenda,ScheduleMinutesId;
			""";
	@Override
	public List<Object[]> CommitteeScheduleMinutesMom(String committeescheduleid) throws Exception {
		Query query=manager.createNativeQuery(AGENDASPECLISTORDERWISE);
		query.setParameter("InScheduleId", Long.parseLong(committeescheduleid));
		List<Object[]> CommitteeScheduleMinutes =(List<Object[]>)query.getResultList();
		return CommitteeScheduleMinutes;
	}

//	private static final String REPUPDATE = """
//			UPDATE committee_schedules_invitation
//		    SET EmpId = :empId,
//		        DesigId = :designationId,
//		        LabCode = :labcode
//		    WHERE CommitteeInvitationId = :invitationId
//			""";
//	@Override
//	public long changeRepresentative(String invitationId, String newEmpNo, String newLabCode, String designationId) throws Exception {
//		try {
//			
//			Query query =manager.createNativeQuery(REPUPDATE);
//			query.setParameter("invitationId", invitationId);
//			query.setParameter("empId",newEmpNo);
//			query.setParameter("labcode", newLabCode != null ? newLabCode.toUpperCase() : null);
//			query.setParameter("designationId", designationId);
//			
//			return query.executeUpdate();
//			
//		}catch (Exception e) {
//			e.printStackTrace();
//			return 0l;
//		}
//	}
	
	
	@Override
	public CommitteeInvitation getcommitteeInvitation(String invitationId) throws Exception {
	    try {
	        Long id = (invitationId != null && !invitationId.isBlank())
	                ? Long.parseLong(invitationId)
	                : 0L;

	        return manager.find(CommitteeInvitation.class, id);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}

	@Override
	public List<Object[]> getCommitteeRepIsActiveList(String committeescheduleid) throws Exception {
		try {
			Query query=manager.createNativeQuery("CALL Pfms_Committee_Invitation_Rep(:InScheduleId)");
			query.setParameter("InScheduleId", Long.parseLong(committeescheduleid));
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return List.of();
	}

	@Override
	public List<CommitteeInvitation> getCommitteeInvitationList(Long scheduleId) throws Exception {
	    String jpql = "FROM CommitteeInvitation c WHERE c.CommitteeScheduleId = :scheduleId AND c.RevisionNo = 0 AND c.IsActive = 0";

	    TypedQuery<CommitteeInvitation> query = manager.createQuery(jpql, CommitteeInvitation.class);
	    query.setParameter("scheduleId", scheduleId);

	    return query.getResultList();
	}

	@Override
	public void CommitteeOnlineAttendanceToggle(String invitationid, String isOnlineAttendenc) throws Exception {
		CommitteeInvitation ExistingCommitteeInvitation = manager.find(CommitteeInvitation.class, Long.parseLong(invitationid));
		ExistingCommitteeInvitation.setIsOnlineAttendence(isOnlineAttendenc);
	}

	private static final String COMMITTEEMAINREPLIST = """
				SELECT b.RepId,b.RepCode,b.RepName,a.CommitteeMainId
				FROM committee_member_rep a
				INNER JOIN committee_rep b ON b.RepId = a.RepId
				WHERE a.CommitteeMainId = :committeeMainId;
			""";
	@Override
	public List<Object[]> getCommitteeMainRepList(String committeeMainId) {
		try {
			Query query=manager.createNativeQuery(COMMITTEEMAINREPLIST);
			query.setParameter("committeeMainId", Long.parseLong(committeeMainId));
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return List.of();
		}
	}
}


