package com.vts.pfms.committee.dao;

import java.math.BigInteger;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vts.pfms.committee.dto.ActionAssignDto;
import com.vts.pfms.committee.model.ActionAssign;
import com.vts.pfms.committee.model.ActionAttachment;
import com.vts.pfms.committee.model.ActionMain;
import com.vts.pfms.committee.model.ActionMainAttachment;
import com.vts.pfms.committee.model.ActionSelf;
import com.vts.pfms.committee.model.ActionSub;
import com.vts.pfms.committee.model.FavouriteList;
import com.vts.pfms.committee.model.OldRfaUpload;
import com.vts.pfms.committee.model.PfmsNotification;
import com.vts.pfms.committee.model.RfaAction;
import com.vts.pfms.committee.model.RfaAssign;
import com.vts.pfms.committee.model.RfaAttachment;
import com.vts.pfms.committee.model.RfaCC;
import com.vts.pfms.committee.model.RfaInspection;
import com.vts.pfms.committee.model.RfaTransaction;


@Transactional
@Repository
public class ActionDaoImpl implements ActionDao{
	
	private SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final Logger logger=LogManager.getLogger(ActionDaoImpl.class);
	
	private static final String EMPLOYEELIST="select a.emp_id,CONCAT(IFNULL(CONCAT(a.title,' '),''), a.emp_name) as 'empname' ,b.designation,a.lab_code FROM employee a,employee_desig b WHERE a.is_active='1' AND a.desig_id=b.desig_id AND a.lab_code=:LabCode AND a.emp_status NOT IN ('N') ORDER BY a.sr_no=0,a.sr_no ";
	private static final String ASSIGNEDLIST="SELECT a.actionmainid,CONCAT(IFNULL(CONCAT(ab.title,' '),''), ab.emp_name) AS emp,dc.designation,a.actiondate,aas.enddate,a.actionitem,aas.actionstatus,aas.progress, aas.IsSeen , aas.actionassignid , (SELECT COUNT(am.actionmainid) FROM action_main am WHERE am.ParentActionid = a.actionmainid ) AS 'ChildActionCount',aas.actionno FROM action_main a,  employee ab ,employee_desig dc ,action_assign aas WHERE aas.actionmainid=a.actionmainid AND aas.assignee=ab.emp_id AND ab.is_active='1' AND dc.desig_id=ab.desig_id  AND aas.actionstatus<>'C' AND aas.assigneelabcode <> '@EXP' AND aas.assignor=:empid UNION SELECT a.actionmainid,CONCAT(IFNULL(CONCAT(ab.title,' '),''), ab.expertname) AS emp,'Expert' AS 'designation',a.actiondate,aas.enddate,a.actionitem,aas.actionstatus,aas.progress, aas.IsSeen , aas.actionassignid , (SELECT COUNT(am.actionmainid) FROM action_main am WHERE am.ParentActionid = a.actionmainid ) AS 'ChildActionCount',aas.actionno FROM action_main a,  expert ab , action_assign aas WHERE aas.actionmainid=a.actionmainid AND aas.assignee=ab.expertid AND ab.isactive='1' AND aas.actionstatus<>'C' AND aas.assigneelabcode = '@EXP' AND aas.assignor=:empid ORDER BY actionassignid DESC";
	//isseen column has been removed  04-08-2021
	//private static final String ASSIGNEDLIST="SELECT a.actionmainid,ab.empname,dc.designation,a.actiondate,a.enddate,a.actionitem,a.actionstatus,a.actionflag,a.createdby,a.createddate,(SELECT MAX(b.actionsubid) FROM action_sub b WHERE b.actionmainid = a.actionmainid) AS subid,(SELECT c.progress FROM action_sub c  WHERE c.actionmainid = a.actionmainid AND c.actionsubid = (SELECT MAX(b.actionsubid) FROM action_sub b WHERE b.actionmainid = a.actionmainid) )  AS progress, (SELECT c.remarks FROM action_sub c  WHERE c.actionmainid = a.actionmainid AND c.actionsubid = (SELECT MAX(b.actionsubid) FROM action_sub b WHERE b.actionmainid = a.actionmainid) )  AS remarks,a.revision FROM action_main a,  employee ab ,employee_desig dc WHERE a.assignee=ab.empid AND ab.isactive='1' AND dc.desigid=ab.desigid AND a.assignor=:empid and a.actionflag<>'Y'";
	private static final String ASSIGNEELIST="SELECT a.actionmainid,CONCAT(IFNULL(CONCAT(b.title,' '),''), b.emp_name) AS 'empname',c.designation,a.actiondate,d.enddate,a.actionitem,d.actionstatus,d.remarks,a.actionlinkid,d.actionno ,d.actionassignid ,d.assignee ,d.assignor , a.actionlevel ,a.projectid  FROM  action_main a, employee b ,employee_desig c , action_assign d WHERE a.actionmainid=d.actionmainid AND d.assignor=b.emp_id AND b.is_active='1' AND c.desig_id=b.desig_id AND (d.assignee=:empid ) AND d.actionstatus IN ('I','A','B') AND d.assigneelabcode<>'@EXP' ORDER BY d.actionassignid DESC";
//	private static final String ASSIGNEEDATA="SELECT a.actionmainid,CONCAT(IFNULL(CONCAT(b.title,' '),''), b.empname) AS 'assignor' ,c.designation,a.actiondate,e.enddate,a.actionitem,e.actionstatus,a.scheduleminutesid,a.actionlinkid,e.actionno,e.revision,CONCAT(IFNULL(CONCAT(d.title,' '),''), d.empname) AS 'assignee',a.actiontype,e.pdcorg,e.pdc1,e.pdc2 ,e.assignorlabcode,e.assigneelabcode , e.actionassignid , (SELECT COUNT(actionmainid) FROM  action_main WHERE (actionmainid=a.actionmainid OR parentActionid=a.actionmainid)) AS 'levelcount' , a.projectid , a.type,e.assignor AS actionassignor,e.Assignee AS 'AssigneeMain' FROM  action_main a, employee b ,employee_desig c ,employee d ,action_assign e WHERE e.assignor=b.empid AND b.isactive='1' AND c.desigid=b.desigid AND  e.assignee=d.empid AND e.assigneelabcode <>'@EXP'  AND a.actionmainid=e.actionmainid AND e.actionassignid=:actionassignid UNION SELECT a.actionmainid,CONCAT(IFNULL(CONCAT(b.title,' '),''), b.empname) AS 'assignor' ,c.designation,a.actiondate,e.enddate,a.actionitem,e.actionstatus,a.scheduleminutesid,a.actionlinkid,e.actionno,e.revision,CONCAT(IFNULL(CONCAT(d.title,' '),''), d.expertname) AS 'assignee',a.actiontype,e.pdcorg,e.pdc1,e.pdc2 ,e.assignorlabcode,e.assigneelabcode , e.actionassignid , (SELECT COUNT(actionmainid) FROM  action_main WHERE (actionmainid=a.actionmainid OR parentActionid=a.actionmainid)) AS 'levelcount' , a.projectid , a.type,e.assignor AS actionassignor,e.Assignee AS 'AssigneeMain' FROM  action_main a, employee b ,employee_desig c ,expert d ,action_assign e WHERE e.assignor=b.empid AND b.isactive='1' AND c.desigid=b.desigid AND  e.assignee=d.expertid AND e.assigneelabcode = '@EXP'  AND a.actionmainid=e.actionmainid AND e.actionassignid=:actionassignid";
	private static final String ASSIGNEEDATA="SELECT a.actionmainid,CONCAT(IFNULL(CONCAT(b.title,' '),''), b.emp_name) AS 'assignor' ,c.designation,a.actiondate,e.enddate,a.actionitem,e.actionstatus,a.scheduleminutesid,a.actionlinkid,e.actionno,e.revision,CONCAT(IFNULL(CONCAT(d.title,' '),''), d.emp_name) AS 'assignee',a.actiontype,e.pdcorg,e.pdc1,e.pdc2 ,e.assignorlabcode,e.assigneelabcode , e.actionassignid , (SELECT COUNT(actionmainid) FROM  action_main WHERE (actionmainid=a.actionmainid OR parentActionid=a.actionmainid)) AS 'levelcount' , a.projectid , a.type,e.assignor AS actionassignor,e.Assignee AS 'AssigneeMain',e.progress FROM  action_main a, employee b ,employee_desig c ,employee d ,action_assign e WHERE e.assignor=b.emp_id AND b.is_active='1' AND c.desig_id=b.desig_id AND  e.assignee=d.emp_id AND e.assigneelabcode <>'@EXP'  AND a.actionmainid=e.actionmainid AND e.actionassignid=:actionassignid UNION SELECT a.actionmainid,CONCAT(IFNULL(CONCAT(b.title,' '),''), b.emp_name) AS 'assignor' ,c.designation,a.actiondate,e.enddate,a.actionitem,e.actionstatus,a.scheduleminutesid,a.actionlinkid,e.actionno,e.revision,CONCAT(IFNULL(CONCAT(d.title,' '),''), d.expertname) AS 'assignee',a.actiontype,e.pdcorg,e.pdc1,e.pdc2 ,e.assignorlabcode,e.assigneelabcode , e.actionassignid , (SELECT COUNT(actionmainid) FROM  action_main WHERE (actionmainid=a.actionmainid OR parentActionid=a.actionmainid)) AS 'levelcount' , a.projectid , a.type,e.assignor AS actionassignor,e.Assignee AS 'AssigneeMain',e.progress FROM  action_main a, employee b ,employee_desig c ,expert d ,action_assign e WHERE e.assignor=b.emp_id AND b.is_active='1' AND c.desig_id=b.desig_id AND  e.assignee=d.expertid AND e.assigneelabcode = '@EXP'  AND a.actionmainid=e.actionmainid AND e.actionassignid=:actionassignid";
	private static final String SUBLIST="SELECT a.actionsubid,a.actionassignid,a.progress,a.progressdate,a.remarks,b.actionattachid,b.attachname,(SELECT e.emp_name FROM employee e ,login l WHERE e.emp_id=l.empid AND l.username=a.createdby)AS'Progress By' FROM action_sub a LEFT JOIN action_attachment b ON (a.actionsubid=b.actionsubid) WHERE a.actionassignid=:assignid AND a.isactive='1' ORDER BY actionsubid ASC";
//	private static final String SUBLIST="SELECT a.actionsubid,a.actionassignid,a.progress,a.progressdate,a.remarks,b.actionattachid,b.attachname FROM action_sub a LEFT JOIN action_attachment b ON (a.actionsubid=b.actionsubid) WHERE a.actionassignid=:assignid ORDER BY actionsubid ASC";
	private static final String SUBDELETE="DELETE FROM action_sub WHERE ActionSubId =:subid";
	private static final String MAINFORWARD="UPDATE action_assign SET  actionstatus=:actionstatus, ModifiedBy=:modifiedby, ModifiedDate=:modifieddate WHERE actionassignid=:assign"; //actionflag=:flag,

	private static final String FORWARDLIST="SELECT a.actionmainid,CONCAT(IFNULL(CONCAT(ab.title,' '),''), ab.emp_name) AS 'empname' ,dc.designation,a.actiondate,aas.enddate,a.actionitem,aas.actionstatus,aas.actionstatus as 'status' ,a.createdby,a.createddate,(SELECT MAX(b.actionsubid) FROM action_sub b WHERE b.actionassignid = aas.actionassignid) AS subid,(SELECT c.progress FROM action_sub c  WHERE c.actionassignid = aas.actionassignid AND c.actionsubid = (SELECT MAX(b.actionsubid) FROM action_sub b WHERE b.actionassignid = aas.actionassignid) )  AS progress, (SELECT c.remarks FROM action_sub c  WHERE c.actionassignid = aas.actionassignid AND c.actionsubid = (SELECT MAX(b.actionsubid) FROM action_sub b WHERE b.actionassignid = aas.actionassignid) )  AS remarks,a.actionlinkid,aas.actionno, aas.actionassignid ,a.projectid FROM action_main a,  employee ab ,employee_desig dc , action_assign aas WHERE a.actionmainid=aas.actionmainid AND aas.assignee=ab.emp_id AND ab.is_active='1' AND dc.desig_id=ab.desig_id AND aas.actionstatus IN ('F','A','B','I','C') AND aas.assignor=:empid AND aas.assigneelabcode <> '@EXP' UNION SELECT a.actionmainid,CONCAT(IFNULL(CONCAT(ab.title,' '),''), ab.expertname) AS 'empname' ,'Expert' AS 'designation',a.actiondate,aas.enddate,a.actionitem,aas.actionstatus,aas.actionstatus as 'status',a.createdby,a.createddate,(SELECT MAX(b.actionsubid) FROM action_sub b WHERE b.actionassignid = aas.actionassignid) AS subid,(SELECT c.progress FROM action_sub c  WHERE c.actionassignid = aas.actionassignid AND c.actionsubid = (SELECT MAX(b.actionsubid) FROM action_sub b WHERE b.actionassignid = aas.actionassignid) )  AS progress, (SELECT c.remarks FROM action_sub c  WHERE c.actionassignid = aas.actionassignid AND c.actionsubid = (SELECT MAX(b.actionsubid) FROM action_sub b WHERE b.actionassignid = aas.actionassignid) )  AS remarks,a.actionlinkid,aas.actionno, aas.actionassignid ,a.projectid FROM action_main a,  expert ab , action_assign aas WHERE a.actionmainid=aas.actionmainid AND aas.assignee=ab.expertid AND ab.isactive='1'  AND aas.actionstatus IN ('F','A','B','I','C') AND aas.assignor=:empid AND aas.assigneelabcode = '@EXP'ORDER BY actionassignid DESC";
	private static final String ACTIONLIST ="SELECT DISTINCT(a.scheduleid), a.committeeid, a.committeemainid, GetCommitteeName(a.scheduledate,b.committeeshortname,b.committeename) AS committeename, a.scheduledate, a.schedulestarttime  FROM committee_schedule a, committee b, committee_schedules_minutes_details c,committee_main d ,committee_member cm WHERE a.committeeid=b.committeeid AND a.scheduledate<=CURDATE() AND a.scheduleid=c.scheduleid AND c.idarck IN('A','K','I','R')  AND a.committeemainid=d.committeemainid  AND d.isactive=1 AND a.isactive=1   AND d.committeemainid=cm.committeemainid AND cm.membertype IN ('CS','PS','CC','CH') AND cm.empid=:empid";
	private static final String COMMITTEEDATA="CALL Pfms_Action_List(:scheduleid)";
	private static final String COMMITTEESCHEDULEEDITDATA="SELECT a.committeeid,a.committeemainid,a.scheduledate,a.schedulestarttime,a.scheduleflag,a.schedulesub,a.scheduleid,GetCommitteeName(a.scheduledate,b.committeeshortname,b.committeename) AS committeename,GetCommitteeShortName(a.scheduledate,b.committeeshortname) AS committeeshortname,a.projectid FROM committee_schedule a,committee b WHERE scheduleid=:committeescheduleid AND a.committeeid=b.committeeid AND a.isactive=1 ";
	private static final String SCHEDULELIST="SELECT a.actionmainid,CONCAT(IFNULL(CONCAT(b.title,' '),''), b.emp_name) AS 'empname',c.designation,a.actiondate,d.enddate,a.actionitem,d.actionstatus,d.actionstatus as 'status' ,d.assigneelabcode,d.actionno,d.pdcorg,d.assignee,d.actionassignid,(SELECT cs.ScheduleId FROM committee_schedules_minutes_details cs WHERE cs.scheduleminutesid=:schid) AS 'scheduleid',a.projectid FROM  action_main a, employee b ,employee_desig c ,action_assign d WHERE d.assigneelabcode <> '@EXP' AND a.actionmainid=d.actionmainid AND d.assignee=b.emp_id AND b.is_active='1' AND c.desig_id=b.desig_id AND a.scheduleminutesid=:schid UNION SELECT a.actionmainid,CONCAT(IFNULL(CONCAT(b.title,' '),''), b.expertname) AS 'empname','Expert' AS 'designation',a.actiondate,d.enddate,a.actionitem,d.actionstatus,d.actionstatus as 'status' ,'Expert' as 'assigneelabcode',d.actionno,d.pdcorg,d.assignee,d.actionassignid,(SELECT cs.ScheduleId FROM committee_schedules_minutes_details cs WHERE cs.scheduleminutesid=:schid) AS 'scheduleid',a.projectid FROM  action_main a, expert b ,action_assign d WHERE d.assigneelabcode = '@EXP' AND a.actionmainid=d.actionmainid AND d.assignee=b.expertid AND b.isactive='1' AND a.scheduleminutesid=:schid ";

    private static final String CONTENT="SELECT a.actionmainid,b.details,b.minutesid,b.minutessubid,b.minutessubofsubid,b.minutesunitid,b.idarck FROM action_main a, committee_schedules_minutes_details b WHERE a.scheduleminutesid=b.scheduleminutesid AND a.actionmainid=:aid";
    private static final String ACTIONSEARCH="SELECT a.actionmainid,aas.actionno,CONCAT(IFNULL(CONCAT(ab.title,' '),''), ab.emp_name) AS 'empname',dc.designation FROM action_main a,  employee ab ,employee_desig dc,action_assign  aas WHERE aas.actionmainid=a.actionmainid AND aas.assignor=ab.emp_id AND ab.isactive='1' AND dc.desig_id=ab.desig_id AND aas.AssigneeLabCode <> '@EXP' AND aas.actionno LIKE :actionno union SELECT a.actionmainid,aas.actionno,CONCAT(IFNULL(CONCAT(ab.title,' '),''), ab.expertname) AS 'empname','Expert' AS 'designation' FROM action_main a,  expert ab ,action_assign  aas  WHERE aas.actionmainid=a.actionmainid AND aas.assignor=ab.expertid AND ab.isactive='1' AND aas.AssigneeLabCode = '@EXP' AND aas.actionno LIKE :actionno";
    private static final String STATUSLIST="SELECT a.actionmainid,CONCAT(IFNULL(CONCAT(b.title,' '),''), b.emp_name) AS 'assignoremp',c.designation,a.actiondate,f.enddate,a.actionitem,f.actionstatus, CONCAT(IFNULL(CONCAT(d.title,' '),''), d.emp_name) AS 'assigneemp',e.designation AS desig,f.actionno,a.actionlinkid,(SELECT g.progress FROM action_sub g  WHERE g.actionassignid = f.actionassignid AND g.actionsubid = (SELECT MAX(s.actionsubid) FROM action_sub s WHERE s.actionassignid = f.actionassignid) )  AS progress , f.actionassignid  FROM  action_main a, employee b ,employee_desig c, employee d ,employee_desig e ,action_assign f WHERE a.actionmainid = f.actionmainid  AND f.assignor=b.emp_id AND b.is_active='1' AND c.desig_id=b.desig_id AND f.assignee=d.emp_id AND d.is_active='1' AND d.desig_id=e.desig_id AND f.assignee=:empid AND a.actiondate BETWEEN :fdate AND :tdate AND f.AssigneeLabCode <> '@EXP' ORDER BY a.actionmainid DESC";
	private static final String LABDETAILS = "select * from lab_master"; 
    //private static final String ACTIONGENCOUNT="SELECT COUNT(*) FROM action_main a , action_assign b WHERE a.actionmainid=b.actionmainid AND (CASE WHEN :projectid=0 THEN a.projectid=:projectid AND DATE_FORMAT(CURDATE(), \"%Y\")=DATE_FORMAT(a.actiondate, \"%Y\") ELSE a.projectid=:projectid END ) AND a.isactive='1' AND a.type=:type";
	private static final String ACTIONGENCOUNT="SELECT COUNT(DISTINCT a.actionmainid)AS 'count' FROM action_main a , action_assign b WHERE a.actionmainid=b.actionmainid AND (CASE WHEN :projectid=0 THEN a.projectid=:projectid AND DATE_FORMAT(CURDATE(), \"%Y\")=DATE_FORMAT(a.actiondate, \"%Y\") ELSE a.projectid=:projectid END ) AND a.isactive='1' AND a.type=:type";
	private static final String ASSIGNEEDETAILS="SELECT assignor,assignee,actionno, assigneelabcode, assignorlabcode FROM action_assign WHERE actionassignid=:assignid";
	private static final String SCHEDULEITEM="SELECT a.scheduleminutesid,a.details FROM  committee_schedules_minutes_details a WHERE  a.scheduleminutesid=:schid";
    private static final String ACTIONSEARCHNO="CALL Pfms_ActionNo_Search(:empid,:no,:position)";
	private static final String PROJECTLIST="SELECT projectid,projectmainid,projectcode,projectname,ProjectShortName FROM project_master WHERE isactive=1";
    private static final String ACTIONCOUNT="CALL Pfms_Action_PD_Chart(:projectid)";
    private static final String LOGINPROJECTIDLIST="SELECT a.projectid,a.projectcode,a.projectname,a.ProjectMainId,a.ProjectDescription,a.UnitCode,a.ProjectType,a.ProjectCategory,a.SanctionNo,a.SanctionDate,a.PDC,a.ProjectDirector FROM project_master a,project_employee b WHERE a.isactive=1 and a.projectid=b.projectid and b.empid=:empid";
    private static final String ALLPROJECTDETAILSLIST ="SELECT a.project_id,a.project_code,a.project_name,a.project_main_id,a.project_description,a.unit_code,a.project_type,a.project_category,a.sanction_no,a.sanction_date,a.pdc,a.project_director,a.project_short_name FROM project_master a WHERE a.is_active=1 ";
	private static final String ACTIONWISE="CALL Pfms_Action_Wise_Reports(:term,:ProjectId)";
    private static final String ACTIONNOTIFIC="SELECT ab.emp_name AS emp,dc.designation AS desig,aas.assignee,b.emp_name,c.designation,aas.assignor,aas.revision,aas.actionno , a.type FROM action_main a,  employee ab ,employee_desig dc,employee b ,employee_desig c ,action_assign aas   WHERE   aas.assignor=b.emp_id  AND b.is_active='1' AND c.desig_id=b.desig_id AND aas.assignee=ab.emp_id  AND ab.is_active='1' AND aas.AssigneeLabCode <> '@EXP' AND dc.desig_id=ab.desig_id  AND aas.actionassignid=:actionassignid AND a.actionmainid=:mainid UNION SELECT ab.expertname AS emp,'Expert' AS desig,aas.assignee,b.emp_name,c.designation,aas.assignor,aas.revision,aas.actionno , a.type FROM action_main a, expert ab ,employee b ,employee_desig c ,action_assign aas   WHERE   aas.assignor=b.emp_id  AND b.is_active='1' AND c.desig_id=b.desig_id AND aas.assignee=ab.expertid  AND ab.isactive='1' AND aas.AssigneeLabCode = '@EXP'   AND aas.actionassignid=:actionassignid AND a.actionmainid=:mainid";
	private static final String ACTIONPDC="CALL Pfms_Action_PDC_Report(:empid,:ProjectId,:Position,:From,:To)";
    private static final String PROJECTCODE="SELECT project_code FROM project_master WHERE   project_id=:ProjectId  AND is_active='1'";
	private static final String SELFASSIGNEDLIST="SELECT a.actionmainid,CONCAT(IFNULL(CONCAT(ab.title,' '),''), ab.emp_name) as 'empname' ,dc.designation,a.actiondate,aas.enddate,a.actionitem,aas.actionstatus,a.createdby,a.createddate,(SELECT MAX(b.actionsubid) FROM action_sub b WHERE b.actionassignid = aas.actionassignid) AS subid,(SELECT c.progress FROM action_sub c  WHERE c.actionassignid = aas.actionassignid AND c.actionsubid = (SELECT MAX(b.actionsubid) FROM action_sub b WHERE b.actionassignid = aas.actionassignid) )  AS progress, (SELECT c.remarks FROM action_sub c  WHERE c.actionassignid = aas.actionassignid AND c.actionsubid = (SELECT MAX(b.actionsubid) FROM action_sub b WHERE b.actionassignid = aas.actionassignid) )  AS remarks,aas.revision  FROM action_main a,  employee ab ,employee_desig dc , action_assign aas WHERE a.actionmainid=aas.actionmainid AND aas.assignee=ab.emp_id AND ab.is_active='1' AND dc.desig_id=ab.desig_id AND aas.assignor=:empid AND aas.assignee=:empid AND aas.actionstatus<>'C'";
	private static final String SEARCHDATA="SELECT a.actionmainid,CONCAT(IFNULL(CONCAT(b.title,' '),''), b.emp_name) as 'empname',c.designation,a.actiondate,d.enddate,a.actionitem,d.actionstatus,d.actionstatus as 'status',a.scheduleminutesid,a.actionlinkid,d.actionno,d.revision FROM  action_main a, employee b ,employee_desig c , action_assign d WHERE a.actionmainid=d.actionmainid AND d.assignor=b.emp_id AND b.is_active='1' AND c.desig_id=b.desig_id  AND a.actionmainid=:mainid AND d.actionassignid=:assignid";
//	private static final String ACTIONWISEALLREPORT="CALL Pfms_Action_Wise_All_Reports (:term,:empid,:projectid);";
	private static final String ACTIONALERT="CALL Pfms_Actions_Msg()";
	private static final String ACTIONTODAY="SELECT b.actionno,a.projectid FROM action_main a , action_assign b WHERE  b.enddate=CURDATE() AND a.actionmainid=b.actionmainid  AND b.actionstatus IN('A','B','F') AND (CASE WHEN 'AI'=:AI THEN a.Actiontype IN ('S','N') ELSE a.Actiontype IN ('A','B','C') END) AND  b.assignee=:empid AND b.AssigneeLabCode <> '@EXP'  ";
	private static final String ACTIONTOMMO="SELECT b.actionno,a.projectid FROM action_main a , action_assign b WHERE  b.enddate=CURDATE()+1 AND a.actionmainid=b.actionmainid  AND b.actionstatus IN('A','B','F') AND (CASE WHEN 'AI'=:AI THEN a.Actiontype IN ('S','N') ELSE a.Actiontype IN ('A','B','C') END) AND b.assignee=:empid AND b.AssigneeLabCode <> '@EXP' ";
	private static final String MEETINGALERT="CALL Pfms_Meeting_Msg()";
	private static final String MEETINGTODAY="CALL Pfms_Meeting_Msg_Today(:empid)";
	private static final String MEETINGTOMMO="CALL Pfms_Meeting_Msg_Tommo(:empid)";
	private static final String ACTIONSELFREMINDERLIST="SELECT ActionId,EmpId,ActionItem,ActionDate,ActionTime,ActionType FROM pfms_action_self WHERE isactive='1' AND actiondate BETWEEN :fromdate AND :todate AND empid=:empid AND labcode=(SELECT lab_code FROM employee WHERE emp_id= :empid ) ORDER BY ActionDate DESC";
	private static final String ALLEMPNAMEDESIGLIST="SELECT e.emp_id ,CONCAT(IFNULL(CONCAT(e.title,' '),''), e.emp_name) as 'empname' , ed.designation FROM employee e, employee_desig ed WHERE e.desig_id=ed.desig_id";
	private static final String COMMITTEESHORTNAME="SELECT cs.committeeid, GetCommitteeShortName(cs.scheduledate,c.committeeshortname) AS committeeshortname  FROM committee c,committee_schedule cs , committee_schedules_minutes_details cmd WHERE c.committeeid=cs.committeeid AND cs.scheduleid=cmd.scheduleid AND cmd.scheduleminutesid=:scheduleid AND cs.isactive=1 ";
	private static final String ASSIGNEESEENUPDATE="UPDATE action_assign SET isseen='1' WHERE assignee=:empid ";
	private static final String PROJECTEMPLIST="SELECT a.emp_id,CONCAT(IFNULL(CONCAT(a.title,' '),''), a.emp_name) as 'empname' ,b.designation FROM employee a,employee_desig b,project_employee pe  WHERE a.is_active='1' AND a.desig_id=b.desig_id  AND pe.empid=a.emp_id AND pe.projectid=:projectid ORDER BY a.sr_no=0,a.sr_no";
	 
	 
    @PersistenceContext
	EntityManager manager;
	
	@Autowired
	ActionDaoRepo amrepo;
	
	@Override
	public Object[] LabDetails()throws Exception
	{
		Query query=manager.createNativeQuery(LABDETAILS);
		Object[] Labdetails =(Object[])query.getResultList().get(0);
		return Labdetails ;
	}
	
	@Override
	public List<Object[]> EmployeeList(String LabCode) throws Exception {
		Query query=manager.createNativeQuery(EMPLOYEELIST);
		query.setParameter("LabCode", LabCode);
		List<Object[]> EmployeeList=(List<Object[]>)query.getResultList();
		if(EmployeeList==null|| EmployeeList.size()==0) {
			return new ArrayList<>();
		}
		
		return EmployeeList;
	}

	@Override
	public List<Object[]> AssignedList(String EmpId ) throws Exception {
        Query query=manager.createNativeQuery(ASSIGNEDLIST);
		query.setParameter("empid", Long.parseLong(EmpId));
		
		List<Object[]> AssignedList=(List<Object[]>)query.getResultList();	
		return AssignedList;
	}

	@Override
	public long ActionMainInsert(ActionMain main) throws Exception {
		
		manager.persist(main);
		manager.flush();
		return main.getActionMainId();
	}
	
	@Override
	public long ActionAssignInsert(ActionAssign assign) throws Exception {
		
		manager.persist(assign);
		manager.flush();
		return assign.getActionAssignId();
	}
	

	@Override
	public List<Object[]> AssigneeList(String EmpId) throws Exception {
		
		Query query=manager.createNativeQuery(ASSIGNEELIST);
		query.setParameter("empid", Long.parseLong(EmpId));
		List<Object[]> AssignedList=(List<Object[]>)query.getResultList();	
		return AssignedList;
	}
	
	
	
	@Override
	public int AssigneeSeenUpdate(String EmpId) throws Exception 
	{		
		Query query=manager.createNativeQuery(ASSIGNEESEENUPDATE);
		query.setParameter("empid", Long.parseLong(EmpId));
		return query.executeUpdate();
	}
	
	
	
	@Override
	public List<Object[]> AssigneeData(String mainid ,String assignid) throws Exception {
		try {
		Query query=manager.createNativeQuery(ASSIGNEEDATA);
//		query.setParameter("mainid", mainid);
		query.setParameter("actionassignid", Long.parseLong(assignid));
		
		List<Object[]> AssignedList=(List<Object[]>)query.getResultList();	
		
		System.out.println("list---->"+mainid +" ==="+assignid+"---"+AssignedList.size());
		return AssignedList;
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	@Override
	public List<Object[]> SubList(String assignid) throws Exception {
		Query query=manager.createNativeQuery(SUBLIST);
		query.setParameter("assignid", Long.parseLong(assignid));
		List<Object[]> AssignedList=(List<Object[]>)query.getResultList();	
		return AssignedList;
	}
	
	@Override
	public long ActionSubInsert(ActionSub main) throws Exception {
		
		manager.persist(main);
		manager.flush();
		return main.getActionSubId();
	}
	
	@Override
	public long ActionAttachInsert(ActionAttachment main) throws Exception {
		
		manager.persist(main);
		manager.flush();
		return main.getActionAttachId();
	}
	
	@Override
	public ActionAttachment ActionAttachmentDownload(String achmentid) throws Exception
	{
		ActionAttachment Attachment= manager.find(ActionAttachment.class,Long.parseLong(achmentid));
		return Attachment;
	}

	@Override
	public int ActionSubDelete(String id) throws Exception {
		
		Query query=manager.createNativeQuery(SUBDELETE);
		query.setParameter("subid", Long.parseLong(id));
		int result=query.executeUpdate();
		return result;
	}

	@Override
	public ActionAssign getActionAssign(String actionassignId) throws Exception 
	{
		ActionAssign actionassign = manager.find(ActionAssign.class, Long.parseLong(actionassignId));
		return actionassign;
	}
	@Override
	public long AssignUpdate(ActionAssign assign) throws Exception 
	{
//		Query query=manager.createNativeQuery(ASSIGNUPDATE);
//		query.setParameter("assignid",assign.getActionAssignId());
//		query.setParameter("flag", assign.getActionFlag());
//		query.setParameter("status",assign.getActionStatus());
//		query.setParameter("modifiedby",assign.getModifiedBy());
//		query.setParameter("modifieddate",assign.getModifiedDate());
//		int result=query.executeUpdate();
		
		manager.merge(assign);
		manager.flush();
		return assign.getActionAssignId();
	}

	@Override
	public int MainForward(ActionAssign assign) throws Exception {
		
		Query query=manager.createNativeQuery(MAINFORWARD);
		query.setParameter("assign",assign.getActionAssignId());
		query.setParameter("actionstatus", assign.getActionStatus());
		query.setParameter("modifiedby",assign.getModifiedBy());
		query.setParameter("modifieddate",assign.getModifiedDate());
		int result=query.executeUpdate();
		return result;
	}

	@Override
	public List<Object[]> ForwardList(String EmpId) throws Exception {
		
		Query query=manager.createNativeQuery(FORWARDLIST);
		query.setParameter("empid", Long.parseLong(EmpId));
		List<Object[]> AssignedList=(List<Object[]>)query.getResultList();	
		return AssignedList;
	}
//     private static final String MAINSENDBACK="UPDATE action_assign SET remarks=:remarks,actionstatus=:status,actionflag=:flag,ModifiedBy=:modifiedby, ModifiedDate=:modifieddate WHERE actionassignid=:assignid";

	@Override
	public long MainSendBack(ActionAssign assign) throws Exception {
		
//		Query query=manager.createNativeQuery(MAINSENDBACK);
//		query.setParameter("assignid",assign.getActionAssignId());
//		query.setParameter("remarks", assign.getRemarks());
//		query.setParameter("flag", assign.getActionFlag());
//		query.setParameter("status",assign.getActionStatus());
//		query.setParameter("modifiedby",assign.getModifiedBy());
//		query.setParameter("modifieddate",assign.getModifiedDate());
//		int result=query.executeUpdate();
		
		manager.merge(assign);
		
		return assign.getActionAssignId();
	}

	@Override
	public List<Object[]> StatusList(String EmpId,String fdate, String tdate) throws Exception {
		
		Query query=manager.createNativeQuery(STATUSLIST);
		query.setParameter("empid", Long.parseLong(EmpId));
		query.setParameter("fdate", fdate);
		query.setParameter("tdate", tdate);
		List<Object[]> AssignedList=(List<Object[]>)query.getResultList();	
		return AssignedList;
	}

	@Override
	public List<Object[]> ActionList(String EmpId) throws Exception {
		
		Query query=manager.createNativeQuery(ACTIONLIST);
		query.setParameter("empid", Long.parseLong(EmpId));
		List<Object[]> AssignedList=(List<Object[]>)query.getResultList();	
		return AssignedList;
	}

	@Override
	public List<Object[]> CommitteeActionList(String EmpId) throws Exception {
		
		Query query=manager.createNativeQuery(COMMITTEEDATA);
		query.setParameter("scheduleid", Long.parseLong(EmpId));
		List<Object[]> AssignedList=(List<Object[]>)query.getResultList();	
		return AssignedList;
	}
	
	@Override
	public Object[] CommitteeScheduleEditData(String CommitteeScheduleId) throws Exception 
	{		
		Query query=manager.createNativeQuery(COMMITTEESCHEDULEEDITDATA);
		query.setParameter("committeescheduleid", Long.parseLong(CommitteeScheduleId));
		Object[] CommitteeScheduleEditData=(Object[])query.getSingleResult();
		return CommitteeScheduleEditData;
	}
				
	@Override
	public List<Object[]> ScheduleActionList(String ScheduleId) throws Exception {
		
		Query query=manager.createNativeQuery(SCHEDULELIST);
		query.setParameter("schid", Long.parseLong(ScheduleId));
		List<Object[]> AssignedList=(List<Object[]>)query.getResultList();	
		return AssignedList;
	}
	
	@Override
	public List<Object[]> MeetingContent(String ScheduleId) throws Exception {
		
		Query query=manager.createNativeQuery(CONTENT);
		query.setParameter("aid", Long.parseLong(ScheduleId));
		List<Object[]> AssignedList=(List<Object[]>)query.getResultList();	
		return AssignedList;
	}

	@Override
	public List<Object[]>  ActionNoSearch(String ActionMainId) throws Exception {

		Query query=manager.createNativeQuery(ACTIONSEARCH);
		query.setParameter("actionno",ActionMainId);
		List<Object[]> ActionDetails=(List<Object[]>)query.getResultList();
		
		return ActionDetails;
	}

	@Override
	public int ActionGenCount(String ProjectId, String type) throws Exception {
		
		Query query = manager.createNativeQuery(ACTIONGENCOUNT);
		query.setParameter("projectid", Long.parseLong(ProjectId));
		query.setParameter("type", type);
		Long count=(Long)query.getSingleResult();
		
		return count.intValue();
	}
	
	@Override
	public long ActionNotificationInsert(PfmsNotification main) throws Exception {
		
		manager.persist(main);
		manager.flush();
		return main.getNotificationId();
	}

	@Override
	public List<Object[]> AssigneeDetails(String assignid) throws Exception {
		
		Query query=manager.createNativeQuery(ASSIGNEEDETAILS);
		query.setParameter("assignid", Long.parseLong(assignid));
		List<Object[]> AssigneeDetails=(List<Object[]>)query.getResultList();
		
		return AssigneeDetails;
	}
	
	@Override
	public List<Object[]> ScheduleActionItem(String ScheduleId) throws Exception {
		
		Query query=manager.createNativeQuery(SCHEDULEITEM);
		query.setParameter("schid", Long.parseLong(ScheduleId));
		List<Object[]> AssignedList=(List<Object[]>)query.getResultList();	
		return AssignedList;
	}
    private static final String ACTIONREPORT="CALL Pfms_Action_Reports(:empid,:term,:position,:type)";

	@Override
	public List<Object[]> ActionReports(String EmpId,String Term,String Position,String Type,String LabCode) throws Exception {
		
		Query query=manager.createNativeQuery(ACTIONREPORT);
		query.setParameter("empid", EmpId);
		query.setParameter("term",Term);
		query.setParameter("position",Position);
		query.setParameter("type",Type);
		//query.setParameter("LabCode",LabCode);
		
		List<Object[]> AssignedList=(List<Object[]>)query.getResultList();	
		return AssignedList;
	}
	@Override
	public List<Object[]> ActionSearch(String EmpId,String No,String Position) throws Exception {
		
		Query query=manager.createNativeQuery(ACTIONSEARCHNO);
		query.setParameter("empid", Long.parseLong(EmpId));
		query.setParameter("no",No);
		query.setParameter("position",Position);
		List<Object[]> AssignedList=(List<Object[]>)query.getResultList();	
		return AssignedList;
	}

	@Override
	public List<Object[]> ProjectList() throws Exception {
		
		Query query=manager.createNativeQuery(PROJECTLIST);
		List<Object[]> AssignedList=(List<Object[]>)query.getResultList();	
		return AssignedList;
	}

	@Override
	public List<Object[]> ActionCountList(String ProjectId) throws Exception {
		
		Query query=manager.createNativeQuery(ACTIONCOUNT);
		query.setParameter("projectid", Long.parseLong(ProjectId));
		List<Object[]> AssignedList=(List<Object[]>)query.getResultList();	
		return AssignedList;
	}

	@Override
	public List<Object[]> projectdetailsList(String EmpId) throws Exception {
		
		Query query=manager.createNativeQuery(LOGINPROJECTIDLIST);
		query.setParameter("empid", Long.parseLong(EmpId));
		return (List<Object[]>)query.getResultList();
	}
	
	@Override
	public List<Object[]> allprojectdetailsList() throws Exception {
		
		Query query=manager.createNativeQuery(ALLPROJECTDETAILSLIST);		
		return (List<Object[]>)query.getResultList();
	}

	@Override
	public List<Object[]> ActionWiseReports(String Term, String ProjectId) throws Exception {
		
		Query query=manager.createNativeQuery(ACTIONWISE);
		query.setParameter("term",Term);
		query.setParameter("ProjectId", Long.parseLong(ProjectId));
		List<Object[]> AssignedList=(List<Object[]>)query.getResultList();	
		return AssignedList;
	}
	@Override
	public List<Object[]> ActionNotification( String MainId , String assignid) throws Exception {
		
		Query query=manager.createNativeQuery(ACTIONNOTIFIC);
		query.setParameter("mainid",Long.parseLong(MainId));
		query.setParameter("actionassignid", Long.parseLong(assignid));
		List<Object[]> AssignedList=(List<Object[]>)query.getResultList();	
		return AssignedList;
	}

	@Override
	public List<Object[]> ActionPdcReports(String Emp, String ProjectId,String Position, java.sql.Date From, java.sql.Date To)
			throws Exception {
		Query query=manager.createNativeQuery(ACTIONPDC);
		query.setParameter("empid",Emp);
		query.setParameter("ProjectId",ProjectId);
		query.setParameter("From",From);
		query.setParameter("To",To);
		query.setParameter("Position",Position);
		List<Object[]> AssignedList=(List<Object[]>)query.getResultList();	
		return AssignedList;
	}
	
	

	@Override
	public String ProjectCode(String ProjectId) throws Exception {
		
		Query query=manager.createNativeQuery(PROJECTCODE);
		query.setParameter("ProjectId", Long.parseLong(ProjectId));
		String ProjectCode=(String)query.getSingleResult();	
		return ProjectCode;
	}
	@Override
	public List<Object[]> ActionSelfList(String EmpId) throws Exception {
	        Query query=manager.createNativeQuery(SELFASSIGNEDLIST);
			query.setParameter("empid", Long.parseLong(EmpId));
			List<Object[]> AssignedList=(List<Object[]>)query.getResultList();	
			return AssignedList;
		}

	@Override
	public List<Object[]> SearchDetails(String MainId , String assignid) throws Exception {
		Query query=manager.createNativeQuery(SEARCHDATA);
		query.setParameter("mainid", Long.parseLong(MainId));
		query.setParameter("assignid", Long.parseLong(assignid));
		List<Object[]> SearchDetails=(List<Object[]>)query.getResultList();	
		return SearchDetails;
	}	
	
	
	private static final String ACTIONWISEALLREPORT="SELECT aas.actionassignid,a.actionmainid,aas.actionno,ab.emp_name,dc.designation,ab.ext_no,ab.mobile_no,a.actiondate,aas.enddate,a.actionitem,aas.actionstatus,a.actiontype,a.type,\r\n"
			+ "a.actionlinkid,(SELECT c.progress FROM action_sub c  WHERE c.actionassignid = aas.actionassignid AND c.actionsubid = (SELECT MAX(f.actionsubid) FROM action_sub f WHERE f.actionassignid = aas.actionassignid) )  AS progress,  a.projectid\r\n"
			+ "FROM action_main a,  employee ab ,employee_desig dc, action_assign aas\r\n"
			+ "WHERE a.actionmainid=aas.actionmainid AND aas.assignee=ab.emp_id AND dc.desig_id=ab.desig_id AND ab.is_active<>0 AND aas.AssigneeLabCode <> '@EXP' AND a.projectid=:projectid AND a.isactive='1' \r\n";
	@Override
	public List<Object[]> ActionWiseAllReport(String ProjectId) throws Exception {
		
		Query query=manager.createNativeQuery(ACTIONWISEALLREPORT);
		query.setParameter("projectid", Long.parseLong(ProjectId));
		List<Object[]> ActionWiseAllReport=(List<Object[]>)query.getResultList();	
		return ActionWiseAllReport;
	}
	
	
	
	@Override
	public long ActionSelfReminderAddSubmit(ActionSelf actionself) throws Exception {
		manager.persist(actionself);
		manager.flush();	
		return actionself.getActionId();
	}
	
	
	@Override
	public List<Object[]> ActionSelfReminderList(String empid,String fromdate,String todate) throws Exception {
		
		Query query=manager.createNativeQuery(ACTIONSELFREMINDERLIST);		
		query.setParameter("empid", Long.parseLong(empid));
		query.setParameter("fromdate",fromdate);
		query.setParameter("todate",todate);
		List<Object[]> ActionSelfReminderList=(List<Object[]>)query.getResultList();	
		return ActionSelfReminderList;
	}
	public static final String ACTIONSELFREMINDERDELETE="UPDATE pfms_action_self SET isactive=0"
			+ " WHERE actionid=:actionid";

	@Override
	public int ActionSelfReminderDelete(String actionid) throws Exception {		
		ActionSelf ExistingActionSelf = manager.find(ActionSelf.class, Long.parseLong(actionid));
		if(ExistingActionSelf != null) {
			ExistingActionSelf.setIsActive(0);
			System.err.println("Working");
			return 1;
		}
		else {
			return 0;
		}
			
	}

	@Override
	public List<Object[]> getActionAlertList() throws Exception {
		Query query=manager.createNativeQuery(ACTIONALERT);		
		List<Object[]> getActionAlertList=(List<Object[]>)query.getResultList();	
		return getActionAlertList;
	}

	@Override
	public List<Object[]> getActionToday(String empid,String ai) throws Exception {
		Query query=manager.createNativeQuery(ACTIONTODAY);	
		query.setParameter("empid", Long.parseLong(empid));
		query.setParameter("AI",ai);
		List<Object[]> getActionToday=(List<Object[]>)query.getResultList();	
		return getActionToday;
	}

	@Override
	public List<Object[]> getActionTommo(String empid,String ai) throws Exception {
		Query query=manager.createNativeQuery(ACTIONTOMMO);	
		query.setParameter("empid",Long.parseLong(empid));
		query.setParameter("AI",ai);
		List<Object[]> getActionTommo=(List<Object[]>)query.getResultList();	
		return getActionTommo;
	}

	@Override
	public List<Object[]> getMeetingAlertList() throws Exception {
		Query query=manager.createNativeQuery(MEETINGALERT);		
		List<Object[]> getMeetingAlertList=(List<Object[]>)query.getResultList();	
		return getMeetingAlertList;
	}

	@Override
	public List<Object[]> getMeetingToday(String empid) throws Exception {
		Query query=manager.createNativeQuery(MEETINGTODAY);	
		query.setParameter("empid", Long.parseLong(empid));
		List<Object[]> getMeetingToday=(List<Object[]>)query.getResultList();	
		return getMeetingToday;
	}

	@Override
	public List<Object[]> getMeetingTommo(String empid) throws Exception {
		Query query=manager.createNativeQuery(MEETINGTOMMO);	
		query.setParameter("empid", Long.parseLong(empid));
		List<Object[]> getMeetingTommo=(List<Object[]>)query.getResultList();	
		return getMeetingTommo;
	}
	
	@Override
	public List<Object[]> LoginProjectDetailsList(String empid,String Logintype , String LabCode)throws Exception
	{
		Query query=manager.createNativeQuery("CALL Pfms_Emp_ProjectList(:empid,:logintype,:labcode);");
		query.setParameter("empid", Long.parseLong(empid));
		query.setParameter("logintype", Logintype);
		query.setParameter("labcode", LabCode);
		List<Object[]> LoginProjectIdList=(List<Object[]>)query.getResultList();
		return LoginProjectIdList;
	}

	@Override
	public List<Object[]> AllEmpNameDesigList()throws Exception
	{
		Query query=manager.createNativeQuery(ALLEMPNAMEDESIGLIST);
		List<Object[]> AllEmpNameDesigList=(List<Object[]>)query.getResultList();
		return AllEmpNameDesigList;
	}
	private static final String GETREASSIGNEDDATA="SELECT  b.actionassignid,b.Actionmainid ,a.Actionitem , b.Actionno , a.actionlevel,a.projectid ,b.enddate,a.mainid ,a.actiontype ,a.ScheduleMinutesId FROM action_main a , action_assign b WHERE a.actionmainid=b.actionmainid AND b.actionassignid=:actionassignid";
	@Override
	public Object[] GetActionReAssignData(String Actionassignid)throws Exception
	{
		Object[] assigndata = null;
		Query query=manager.createNativeQuery(GETREASSIGNEDDATA);
		query.setParameter("actionassignid", Long.parseLong(Actionassignid));
		List<Object[]> list=(List<Object[]>)query.getResultList();
		if(list!=null && list.size()>0) {
			assigndata=list.get(0);
		}
		return assigndata;
	}
	private static final String PROJECTDATA="SELECT project_id ,project_code , project_name, project_short_name FROM project_master WHERE project_id=:projectid";
	@Override
	public Object[] GetProjectData(String projectid)throws Exception
	{
		Object[] assigndata = null;
		Query query=manager.createNativeQuery(PROJECTDATA);
		query.setParameter("projectid", Long.parseLong(projectid));
		List<Object[]> list=(List<Object[]>)query.getResultList();
		if(list!=null && list.size()>0) {
			assigndata=list.get(0);
		}
		return assigndata;
	}
	
	@Override
	public List<Object[]> ProjectEmpList(String projectid)throws Exception
	{
		Query query=manager.createNativeQuery(PROJECTEMPLIST);
		query.setParameter("projectid", Long.parseLong(projectid));
		List<Object[]> ProjectEmpList=(List<Object[]>)query.getResultList();
		return ProjectEmpList;
	}
	
	
	
	@Override
	public Object[] CommitteeShortName(String scheduleid)throws Exception
	{
		Query query=manager.createNativeQuery(COMMITTEESHORTNAME);
		query.setParameter("scheduleid", Long.parseLong(scheduleid));
		Object[] CommitteeShortName=(Object[])query.getResultList().get(0);
		return CommitteeShortName;
	}
	
	
	
	@Override
	public List<Object[]> EmployeeDropdown(String empid,String logintype,String projectid)throws Exception
	{
		Query query=manager.createNativeQuery("CALL Employee_Dropdown(:empid,:logintype,:projectid);");
		query.setParameter("empid", Long.parseLong(empid));
		query.setParameter("logintype", logintype);
		query.setParameter("projectid", projectid);
		List<Object[]> EmployeeDropdown=(List<Object[]>)query.getResultList();
		return EmployeeDropdown;
	}
	
	
	private static final String  ACTIONDETAILSAJAX = "SELECT a.actionmainid,a.actionitem, b.assignee,CAST( DATE_FORMAT(b.enddate,'%d-%m-%Y') AS CHAR) AS 'enddate',b.revision ,b.assigneelabcode ,b.ActionAssignId FROM action_main a , action_assign b WHERE a.actionmainid=b.actionmainid AND a.actionmainid=:actionid and b.actionassignid=:assignid";
	
	@Override
	public Object[] ActionDetailsAjax(String actionid ,String assignid) throws Exception
	{		
		Query query=manager.createNativeQuery(ACTIONDETAILSAJAX);
		query.setParameter("actionid", Long.parseLong(actionid));
		query.setParameter("assignid", Long.parseLong(assignid));
		List<Object[]> AssignedList=(List<Object[]>)query.getResultList();	
		if(AssignedList.size()>0) {
			return AssignedList.get(0);
		}else {
			return null;
		}
	}
	
	@Override
	public int ActionMainEdit(ActionMain main) throws Exception
	{	
		ActionMain detach = manager.find(ActionMain.class, main.getActionMainId());
		detach.setActionItem(main.getActionItem());
		
		if(manager.merge(detach)!=null) {
			return 1;
		}
		return 0;
		
	}
	 @Override
	 public int ActionAssignEdit(ActionAssign assign) throws Exception
	 {
		 ActionAssign detach = manager.find(ActionAssign.class, assign.getActionAssignId());
		 	detach.setModifiedDate(assign.getModifiedDate());
		 	detach.setModifiedBy(assign.getModifiedBy());
		 	if(assign.getPDCOrg()!=null) {
		 	detach.setPDCOrg(assign.getPDCOrg());
		 	}
		 	if(assign.getEndDate()!=null) {
		 	detach.setEndDate(assign.getEndDate());
		 	}
		 	detach.setAssignee(assign.getAssignee());
			detach.setAssigneeLabCode(assign.getAssigneeLabCode());
			if(manager.merge(detach)!=null) {
				return 1;
			}
			return 0;
	 }
	
	 private static final String CLUSTERLABLIST="SELECT lab_id,cluster_id,lab_name,lab_code FROM cluster_lab";
	
	@Override
	public List<Object[]> AllLabList() throws Exception 
	{
		Query query=manager.createNativeQuery(CLUSTERLABLIST);
		List<Object[]> ClusterLabList=(List<Object[]>)query.getResultList();
		return ClusterLabList;
	}
	
	private static final String ACTIONMAINDATA="SELECT Actionmainid,empid,actiondate,actionitem,actiontype,projectid,scheduleminutesid FROM action_main WHERE actionstatus='A' AND isactive=1 AND Actionmainid=:actionmainid";
	@Override
	public Object[] GetActionMainData(String actionmainid) throws Exception {
		Object[] actiondata=null;
		Query query = manager.createNativeQuery(ACTIONMAINDATA);
		query.setParameter("actionmainid", Long.parseLong(actionmainid));
		List<Object[]> result = (List<Object[]>)query.getResultList();
		if(result.size()>0) {
			actiondata=result.get(0);
		}
		return actiondata;
	}
	
	private static final String CLUSTEREXPERTSLIST ="SELECT e.expertid,CONCAT(IFNULL(CONCAT(e.title,' '),''),e.expertname) as 'expertname' ,e.expertno,'Expert' AS designation,e.organization FROM expert e WHERE e.isactive=1 ";
	@Override
	public List<Object[]> ClusterExpertsList() throws Exception
	{
		try {
			Query query=manager.createNativeQuery(CLUSTEREXPERTSLIST);
			List<Object[]> DGEmpData=(List<Object[]>)query.getResultList();
			return DGEmpData;
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new java.util.Date() +" Inside DAO ClusterExpertsList"+ e);
			return new ArrayList<Object[]>();
		}
	}
	private static final String CLUSTERFILTEREMPLIST="SELECT e.expertid,CONCAT(IFNULL(CONCAT(e.title,' '),''),e.expertname) as 'expertname',e.expertno,'Expert' AS designation FROM expert e  WHERE e.isactive=1 AND e.expertid NOT IN (SELECT b.assignee FROM  action_main a, action_assign b WHERE a.actionmainid=b.actionmainid AND b.assigneelabcode=:labcode AND (a.mainid=:mainid OR a.actionmainid=:mainid))";
	
	@Override
	public List<Object[]> ClusterFilterExpertsList(String Labcode , String MainId)throws Exception
	{
		try {
			Query query=manager.createNativeQuery(CLUSTERFILTEREMPLIST);
			query.setParameter("labcode", Labcode);
			query.setParameter("mainid", Long.parseLong(MainId));
			List<Object[]> DGEmpData=(List<Object[]>)query.getResultList();
			return DGEmpData;
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new java.util.Date() +" Inside DAO ClusterFilterExpertsList "+ e);
			return new ArrayList<Object[]>();
		}
	}
	private static final String LABINFOCLUSTERLAB = "SELECT labid,clusterid,labname,labcode FROM cluster_lab WHERE labcode =:labcode";
	@Override
	public Object[] LabInfoClusterLab(String LabCode) throws Exception 
	{
		Query query=manager.createNativeQuery(LABINFOCLUSTERLAB);
		query.setParameter("labcode", LabCode);
		Object[] LabInfoClusterLab=(Object[])query.getSingleResult();
		return LabInfoClusterLab;
	}
	
	private static final String LABEMPLOYEELIST="SELECT a.emp_id,CONCAT(IFNULL(CONCAT(a.title,' '),''), a.emp_name) as 'empname',a.emp_no,b.designation FROM employee a,employee_desig b WHERE a.is_active=1 AND a.emp_status IN ('P') AND a.desig_id=b.desig_id AND a.lab_code=:labcode ORDER BY a.sr_no;";
	@Override
	public List<Object[]> LabEmployeeList(String LabCode) throws Exception 
	{
		Query query=manager.createNativeQuery(LABEMPLOYEELIST);
		query.setParameter("labcode", LabCode);
		List<Object[]> ChairpersonEmployeeListFormation=(List<Object[]>)query.getResultList();
		return ChairpersonEmployeeListFormation;
	}
	private static final String LABEMPFILTERFORACTION="SELECT a.emp_id,CONCAT(IFNULL(CONCAT(a.title,' '),''), a.emp_name) as 'empname',a.emp_no,b.designation FROM employee a,employee_desig b WHERE a.is_active=1 AND a.desig_id=b.desig_id AND a.emp_status IN ('P') AND a.emp_id NOT IN (SELECT b.assignee FROM  action_main a, action_assign b WHERE a.actionmainid=b.actionmainid AND b.assigneelabcode=:labcode AND (a.mainid=:mainid OR a.actionmainid=:mainid)) AND a.lab_code=:labcode";
	@Override
	public List<Object[]> LabEmpListFilterForAction(String LabCode , String MainId) throws Exception
	{
		Query query=manager.createNativeQuery(LABEMPFILTERFORACTION);
		query.setParameter("labcode", LabCode);
		query.setParameter("mainid", Long.parseLong(MainId));
		List<Object[]> ChairpersonEmployeeListFormation=(List<Object[]>)query.getResultList();
		return ChairpersonEmployeeListFormation;
	}
	 @Override
	 public int ActionAssignRevisionEdit(ActionAssign assign) throws Exception
	 {
		 ActionAssign detach = manager.find(ActionAssign.class, assign.getActionAssignId());
			detach.setRevision(assign.getRevision());
			detach.setModifiedBy(assign.getModifiedBy());
			detach.setModifiedDate(assign.getModifiedDate());
			// newly added
			detach.setEndDate(assign.getEndDate());
			if(assign.getRevision()==1) {
				detach.setPDC1(assign.getEndDate());
			}else if(assign.getRevision()==2) {
				detach.setPDC2(assign.getEndDate());
			}
			if(manager.merge(detach)!=null) {
				return 1;
			}
			return 0;
	 }
	 
	 
	 private static final String ACTIONSUBLEVELSLIST="CALL pfms_action_level_list (:ActionAssignId ); ";
			 
	 @Override
	 public List<Object[]> ActionSubLevelsList(String ActionAssignId) throws Exception
	 {
		Query query=manager.createNativeQuery(ACTIONSUBLEVELSLIST);
		query.setParameter("ActionAssignId", Long.parseLong(ActionAssignId));
		List<Object[]> ActionSubLevelsList=(List<Object[]>)query.getResultList();
		return ActionSubLevelsList;
	 }
	 
	 private static final String ACTIONSUBLEVELSLISTFORCLOSE="CALL pfms_action_level_list (:ActionAssignId ); ";
	 
	 @Override
	 public List<Object[]> ActionSubLevelsListForClose(String ActionAssignId) throws Exception
	 {
		Query query=manager.createNativeQuery(ACTIONSUBLEVELSLISTFORCLOSE);
		query.setParameter("ActionAssignId", Long.parseLong(ActionAssignId));
		List<Object[]> ActionSubLevelsList=(List<Object[]>)query.getResultList();
		return ActionSubLevelsList;
	 }
	 private static final String ACTIONSUBLIST="SELECT a.actionsubid,a.actionassignid,a.progress,a.progressdate,a.remarks,b.actionattachid FROM action_sub a LEFT JOIN action_attachment b ON (a.actionsubid=b.actionsubid) WHERE a.actionassignid=:assignid ORDER BY actionsubid ASC";
	 
	 @Override
		public List<Object[]> ActionSubList(String assignid) throws Exception {
			Query query=manager.createNativeQuery(ACTIONSUBLIST);
			query.setParameter("assignid",  Long.parseLong(assignid));
			List<Object[]> AssignedList=(List<Object[]>)query.getResultList();	
			return AssignedList;
		}
	
		private static final String ISSULIST ="SELECT  am.actionmainid, am.actionitem, am.projectid, aas.actionstatus,am.type,am.scheduleminutesId , aas.actionassignid , aas.actionno , (SELECT c.progress FROM action_sub c  WHERE c.actionassignid = aas.actionassignid AND c.actionsubid = (SELECT MAX(b.actionsubid) FROM action_sub b WHERE b.actionassignid = aas.actionassignid) )  AS progress ,aas.actionstatus as 'status' ,aas.assignee , aas.assignor FROM action_main am , action_assign aas WHERE aas.actionmainid=am.actionmainid AND am.type='I' AND aas.actionstatus<>'C' AND ((aas.assignee=:empid AND aas.AssigneeLabCode <> '@EXP' ) OR aas.assignor=:empid)";
		@Override
		public List<Object[]> GetIssueList(String empid )throws Exception
		{
			Query query = manager.createNativeQuery(ISSULIST);
			
			query.setParameter("empid", Long.parseLong(empid));
			return (List<Object[]>)query.getResultList();
		}
		private static final String GETRECOMENDATIONLIST="SELECT b.scheduleminutesid, a.scheduleid ,a.meetingid, b.details , b.remarks FROM committee_schedule a, committee_schedules_minutes_details b WHERE idarck='R' AND a.scheduleid = b.scheduleid AND (CASE WHEN 'A'=:projectid THEN 1=1 ELSE a.projectid=:projectid END)  AND (CASE WHEN 'A'=:committeid THEN 1=1 ELSE a.committeeid=:committeid END) ";
		@Override
		public List<Object[]> GetRecomendationList(String projectid ,  String committeid)throws Exception
		{
			Query query = manager.createNativeQuery(GETRECOMENDATIONLIST);
			query.setParameter("projectid", projectid);
			query.setParameter("committeid", committeid);
			return (List<Object[]>)query.getResultList();
		}
		
		private static final String GETDECISIONLIST="SELECT b.scheduleminutesid, a.scheduleid ,a.meetingid, b.details , b.remarks FROM committee_schedule a, committee_schedules_minutes_details b WHERE idarck='D' AND a.scheduleid = b.scheduleid AND (CASE WHEN 'A'=:projectid THEN 1=1 ELSE a.projectid=:projectid END)  AND (CASE WHEN 'A'=:committeid THEN 1=1 ELSE a.committeeid=:committeid END) ";
		@Override
		public List<Object[]> GetDecisionList(String projectid ,  String committeid)throws Exception
		{
			Query query = manager.createNativeQuery(GETDECISIONLIST);
			query.setParameter("projectid", projectid);
			query.setParameter("committeid", committeid);
			return (List<Object[]>)query.getResultList();
		}

	
	private static final String ACTIONASSIGNDATAAJAX="SELECT aas.ActionAssignId,am.actionitem,aas.ActionNo,am.actionmainid,aas.progress,am.actiondate, aas.enddate,aas.PDCOrg,CONCAT(IFNULL(asn.title,''), asn.emp_name) AS  'assignor name',CASE WHEN aas.assigneelabcode <> '@EXP' THEN (SELECT CONCAT(IFNULL(asi.title,''), asi.emp_name) FROM employee asi WHERE aas.assignee= asi.emp_id)  ELSE (SELECT CONCAT(IFNULL(asi.title,''), asi.expertname)  FROM expert asi WHERE aas.assignee= asi.expertid )END AS 'assignee name' ,am.type,aas.Assignor,aas.Assignee,aas.ActionStatus AS 'assignedstatus' FROM action_main am, action_assign aas, employee asn WHERE am.actionmainid=aas.actionmainid  AND am.isactive=1 AND aas.isactive=1 AND  aas.assignor= asn.emp_id AND aas.actionassignid=:assignid";
	
	@Override
	public Object[] ActionAssignDataAjax(String assignid) throws Exception 
	{
		Query query=manager.createNativeQuery(ACTIONASSIGNDATAAJAX);
		query.setParameter("assignid", Long.parseLong(assignid));
		return (Object[])query.getResultList().get(0);	
	}
	private static final String GETDECISIONSOUGHT="SELECT DISTINCT (a.scheduleid) ,a.meetingid, a.pmrcdecisions , a.reference  FROM committee_schedule a, committee_schedules_minutes_details b WHERE idarck=:type AND a.scheduleid = b.scheduleid AND (CASE WHEN 'A'=:projectid THEN 1=1 ELSE a.projectid=:projectid END)  AND (CASE WHEN 'A'=:committeeid THEN 1=1 ELSE a.committeeid=:committeeid END)";
	@Override
	public List<Object[]> GetRecDecSoughtList(String projectid,String  committeeid , String type)throws Exception
	{
		Query query = manager.createNativeQuery(GETDECISIONSOUGHT);
		query.setParameter("projectid", projectid);
		query.setParameter("committeeid", committeeid);
		query.setParameter("type", type);
		return (List<Object[]>)query.getResultList();
	}
	
	private static final String RECDECSOUGHT="SELECT a.recdecid , a.point FROM  pfms_recdec_point a WHERE a.isactive=1 AND a.type=:type and a.scheduleid=:scheduleid ORDER BY a.recdecid DESC";
	@Override
	public List<Object[]> getActualDecOrRecSought(String scheduleid, String type)throws Exception
	{
		Query query = manager.createNativeQuery(RECDECSOUGHT);
		query.setParameter("scheduleid", Long.parseLong(scheduleid));
		query.setParameter("type", type);
		return (List<Object[]>)query.getResultList();
	}
	
	private static final String ACTUALDECISIONSOUGHT="SELECT a.details , a.scheduleid FROM committee_schedules_minutes_details a WHERE a.idarck=:type AND a.scheduleid=:scheduleid ORDER BY a.ScheduleMinutesId DESC";
	@Override
	public List<Object[]> getDecOrRecSought(String scheduleid , String type)throws Exception
	{
		Query query = manager.createNativeQuery(ACTUALDECISIONSOUGHT);
		query.setParameter("scheduleid", Long.parseLong(scheduleid));
		query.setParameter("type", type);
		return (List<Object[]>)query.getResultList();
	}

	private static final String ALLACTIONLIST="SELECT a.actionmainid,CONCAT(IFNULL(CONCAT(b.title,' '),''), b.emp_name) AS 'assignorempname',c.designation,a.actiondate,d.enddate,a.actionitem,d.actionstatus,d.actionstatus as 'status',d.remarks,d.actionno ,d.actionassignid ,d.assignee ,d.assignor ,a.actionlevel ,(SELECT c.progress FROM action_sub c  WHERE  c.actionassignid = d.actionassignid AND c.actionsubid =(SELECT MAX(b.actionsubid) FROM action_sub b WHERE b.actionassignid = d.actionassignid))  AS progress , d.pdcorg, CONCAT(IFNULL(CONCAT(e.title,' '),''), e.emp_name) AS 'assigneeempname' , a.type FROM  action_main a, employee b ,employee_desig c , action_assign d , employee e WHERE a.actionmainid=d.actionmainid AND d.assignor=b.emp_id AND b.is_active='1' AND d.assignee=e.emp_id AND c.desig_id=b.desig_id AND (d.assignee=:empid OR d.assignor=:empid) AND d.actionstatus IN ('A','B','F') ORDER BY d.actionassignid DESC";
	@Override
	public List<Object[]> GetActionList(String empid)throws Exception
	{
		Query query = manager.createNativeQuery(ALLACTIONLIST);
		query.setParameter("empid", Long.parseLong(empid));
		return (List<Object[]>)query.getResultList();
	}
	
	private static final String ACTIONMONITORING="CALL Pfms_Action_Monitoring (:ProjectId ,:Status);";
	@Override
	public List<Object[]> ActionMonitoring(String ProjectId , String Status)throws Exception
	{
		Query query = manager.createNativeQuery(ACTIONMONITORING);
		query.setParameter("ProjectId", Long.parseLong(ProjectId));
		query.setParameter("Status", Status);
		return (List<Object[]>)query.getResultList();
	}
	private static final String GETACTIONFORFEVORITE="SELECT a.actionmainid,CONCAT(IFNULL(CONCAT(b.title,' '),''), b.emp_name) AS 'assignorempname',c.designation,a.actiondate,d.enddate,a.actionitem,d.actionstatus,d.actionstatus as 'status',d.remarks,d.actionno ,d.actionassignid ,d.assignee ,d.assignor ,a.actionlevel ,(SELECT c.progress FROM action_sub c  WHERE  c.actionassignid = d.actionassignid AND c.actionsubid =(SELECT MAX(b.actionsubid) FROM action_sub b WHERE b.actionassignid = d.actionassignid))  AS progress ,d.pdcorg, CONCAT(IFNULL(CONCAT(e.title,' '),''), e.emp_name) AS 'assigneeempname' , a.type FROM  action_main a, employee b ,employee_desig c , action_assign d , employee e WHERE a.actionmainid=d.actionmainid AND d.assignor=b.emp_id AND b.is_active='1' AND d.assignee=e.emp_id AND c.desig_id=b.desig_id   AND d.actionstatus NOT IN ('C') AND d.actionassignid NOT IN (SELECT actionassignid FROM pfms_Favourite_List WHERE empid=:empid) AND c.desig_sr <=(SELECT ed.desig_sr FROM employee e, employee_desig ed WHERE e.desig_id=ed.desig_id AND e.emp_id=:empid) AND a.projectid=:projectid AND a.actiondate BETWEEN :fromdate AND :todate ORDER BY d.actionassignid DESC";
	@Override
	public List<Object[]> GetActionListForFevorite(Date fromdate , Date todate , String projectid , String  empid)throws Exception
	{
		Query query = manager.createNativeQuery(GETACTIONFORFEVORITE);
		query.setParameter("fromdate", fromdate);
		query.setParameter("todate", todate);
		query.setParameter("projectid", Long.parseLong(projectid));
		query.setParameter("empid", Long.parseLong(empid));
		return (List<Object[]>)query.getResultList();
	}
	
	@Override
	public Long AddFavouriteList(FavouriteList fav) throws Exception
	{
		manager.persist(fav);
		manager.flush();
		return fav.getFavouriteId();
	}
	private static final String GETFAVOURITE="SELECT a.actionmainid,CONCAT(IFNULL(CONCAT(b.title,' '),''), b.emp_name) AS 'assignorempname',c.designation,a.actiondate,d.enddate,a.actionitem,d.actionno ,d.actionassignid ,d.assignee ,d.assignor ,a.actionlevel ,(SELECT c.progress FROM action_sub c  WHERE  c.actionassignid = d.actionassignid AND c.actionsubid =(SELECT MAX(b.actionsubid) FROM action_sub b WHERE b.actionassignid = d.actionassignid))  AS progress ,d.pdcorg, CONCAT(IFNULL(CONCAT(e.title,' '),''), e.emp_name) AS 'assigneeempname' , a.type FROM  action_main a, employee b ,employee_desig c , action_assign d , employee e , pfms_Favourite_List fl WHERE a.actionmainid=d.actionmainid AND d.assignor=b.emp_id AND b.is_active='1' AND d.assignee=e.emp_id AND fl.actionassignid = d.actionassignid AND c.desig_id=b.desig_id AND d.actionstatus NOT IN ('C') AND fl.empid=:empid order by fl.FavouriteId desc";
	@Override
	public List<Object[]> GetFavouriteList(String empid)throws Exception
	{
		Query query = manager.createNativeQuery(GETFAVOURITE);
		query.setParameter("empid", Long.parseLong(empid));
		return (List<Object[]>)query.getResultList();
	}
	private static final String PROJECTTYPELIST="select classificationid,classification from pfms_security_classification order by classification";
	@Override
	public List<Object[]> ProjectTypeList() throws Exception {	
		
		Query query=manager.createNativeQuery(PROJECTTYPELIST);
		List<Object[]> ProjectTypeList=(List<Object[]>)query.getResultList();		

		return ProjectTypeList;
	}
	
	
	private static final String RFAACTIONLIST="SELECT DISTINCT a.rfaid,a.labcode,CASE WHEN a.projectType ='P' THEN d.projectcode ELSE h.projectShortName END AS projectCode,a.rfano,a.rfadate,b.priority,\r\n"
			+ "f.classification AS category,a.statement,a.description,a.reference,a.isactive,a.createdby,a.createddate,a.projectid,a.rfastatus,a.AssignorId,\r\n"
			+ "(SELECT COUNT(Remarks) FROM pfms_rfa_action_transaction trans WHERE a.RfaId=trans.RfaId) AS Remarks,g.rfastatusdetails,a.TypeOfRfa ,a.projectType\r\n"
			+ "FROM pfms_rfa_action a\r\n"
			+ "INNER JOIN pfms_rfa_priority b ON a.priorityid = b.priorityid\r\n"
			+ "LEFT JOIN project_master d ON a.projectid = d.projectid\r\n"
			+ "LEFT JOIN pfms_initiation h ON a.projectid = h.initiationId\r\n"
			+ "INNER JOIN pfms_security_classification f \r\n"
			+ "ON(\r\n"
			+ "(a.projectType = 'P' AND d.projectcategory = f.classificationid)\r\n"
			+ " OR (a.projectType <> 'P' AND h.classificationId = f.classificationid)\r\n"
			+ ")\r\n"
			+ "INNER JOIN pfms_rfa_status g ON a.rfastatus = g.rfastatus\r\n"
			+ "WHERE a.projectType = :projectType AND\r\n"
			+ "(\r\n"
			+ " (:projectType = 'P' AND (:projectId = 'A' OR a.projectId = :projectId))\r\n"
			+ "  OR\r\n"
			+ " (:projectType = 'I' AND (:initiationId = 'A' OR a.projectId = :initiationId))\r\n"
			+ ")\r\n"
			+ "AND a.assignorid=:empId AND a.rfadate BETWEEN :fdate AND :tdate ORDER BY rfaid DESC";
	@Override
	public List<Object[]> GetRfaActionList(String EmpId,String projectType,String projectId,String initiationId,String fdate,String tdate) throws Exception 
	{
		Query query = manager.createNativeQuery(RFAACTIONLIST);
		query.setParameter("fdate", fdate);
		query.setParameter("tdate", tdate);
		query.setParameter("projectType", projectType);
		query.setParameter("projectId", projectId);
		query.setParameter("initiationId", initiationId);
		query.setParameter("empId", EmpId);
		return (List<Object[]>)query.getResultList();
	}

	
	private static final String PRIORITYLIST="SELECT priorityid,priority FROM pfms_rfa_priority";
	@Override
	public List<Object[]> PriorityList() throws Exception 
	{
		Query query = manager.createNativeQuery(PRIORITYLIST);
		return (List<Object[]>)query.getResultList();
	}
	
	@Override
	public Long RfaActionSubmit(RfaAction rfa) throws Exception {
		logger.info( "Inside Service RfaActionSubmit");
		try {
		manager.persist(rfa);
		manager.flush();
		return rfa.getRfaId();
	} catch (Exception e) {
		e.printStackTrace();
		logger.error( "Inside Service RfaActionSubmit", e);
		return null;
	}
	}

	private static final String GETDIVISIONCODE="SELECT d.division_id, d.division_code FROM division_master d WHERE d.division_id=(SELECT divisionid FROM employee WHERE empid=:empid)";
	@Override
	public Object[] GetDivisionCode(String Division) throws Exception {
		
		Query query = manager.createNativeQuery(GETDIVISIONCODE);
		query.setParameter("empid", Long.parseLong(Division)); // passing EmpId as Division  
		return (Object[])query.getSingleResult();
		
	}
    
	private static final String GETRFACOUNT="SELECT COUNT(RfaId) FROM pfms_rfa_action WHERE IsActive='1' AND rfatypeid=:rfatypeid AND projectId=:projectId AND TypeOfRfa=:TypeOfRfa AND VendorCode=:VendorCode";
	@Override
	public Long GetRfaCount(String rfatypeid, Long projectId,String type,String vendor) throws Exception {
		
		Query query = manager.createNativeQuery(GETRFACOUNT);
		query.setParameter("rfatypeid", Long.parseLong(rfatypeid));
		query.setParameter("projectId", projectId);
		query.setParameter("TypeOfRfa", type);
		query.setParameter("VendorCode", vendor);
		return (Long)query.getSingleResult();
	}
	
	private static final String RFAEDITDATA="SELECT rfaid,labcode,projectid,rfano,rfadate,priorityid,statement,description,reference,rfastatus,createdby,createddate,modifiedby,modifieddate,isactive,TypeOfRfa,BoxNo,SWdate,FPGA,RigVersion,RfaRaisedByName,projectType FROM pfms_rfa_action WHERE rfaid=:rfaid";
	@Override
	public Object[] RfaActionEdit(String rfaid) throws Exception {
		
		Query query = manager.createNativeQuery(RFAEDITDATA);
		query.setParameter("rfaid", Long.parseLong(rfaid));
		return (Object[])query.getSingleResult();
	}


	private static final String RFAEDITSUBMIT="UPDATE pfms_rfa_action SET rfadate=:rfadate,priorityid=:priority,statement=:statement,description=:description,reference=:reference,ModifiedBy=:modifiedby , ModifiedDate=:modidifeddate,boxno=:boxno,SWdate=:SWdate,FPGA=:FPGA,RigVersion=:RigVersion,RfaRaisedByName=:RfaRaisedByName WHERE rfaid=:rfaid";
	@Override
	public Long RfaEditSubmit(RfaAction rfa) throws Exception {
		
		Query query = manager.createNativeQuery(RFAEDITSUBMIT);
			query.setParameter("rfadate", rfa.getRfaDate());
			query.setParameter("priority", rfa.getPriorityId());
			query.setParameter("statement", rfa.getStatement());
			query.setParameter("description", rfa.getDescription());
			query.setParameter("reference", rfa.getReference());
			query.setParameter("modifiedby", rfa.getModifiedBy());
			query.setParameter("modidifeddate", rfa.getModifiedDate());
			query.setParameter("boxno", rfa.getBoxNo());
			query.setParameter("SWdate", rfa.getSWdate());
			query.setParameter("FPGA", rfa.getFPGA());
			query.setParameter("RigVersion", rfa.getRigVersion());
			query.setParameter("rfaid", rfa.getRfaId());
			query.setParameter("RfaRaisedByName", rfa.getRfaRaisedByName());
		
		Long result = (long) query.executeUpdate();
		return result;
	}


	private static final String RFALABDETAILS="SELECT a.labid,a.labcode,a.labname,a.labaddress,a.labcity,a.labpin FROM lab_master a WHERE a.labcode=:labcode";
	@Override
	public Object[] RfaLabDetails(String LabCode) throws Exception {
       
		Query query = manager.createNativeQuery(RFALABDETAILS);
		query.setParameter("labcode", LabCode);
		return (Object[])query.getSingleResult();
	}
    
	private static final String RFAPRINT="CALL pfms_Rfa_Print(:rfaid)";
	@Override
	public Object[] RfaPrintData(String rfaid) throws Exception {
		
		Query query = manager.createNativeQuery(RFAPRINT);
		query.setParameter("rfaid", Long.parseLong(rfaid));
		return (Object[])query.getSingleResult();
	}
    
	private static final String RFAFORWARDLIST="CALL pfms_RfaForward_List(:EmpId)";
	@Override
	public List<Object[]> RfaForwardList(String EmpId) throws Exception {
		Query query = manager.createNativeQuery(RFAFORWARDLIST);
		query.setParameter("EmpId", Long.parseLong(EmpId));
		return (List<Object[]>)query.getResultList();
	}
	
	private static final String RFAINSPECTIONAPPROVALLIST="CALL pfms_RfaInspectionForward_List(:EmpId)";
	@Override
	public List<Object[]> RfaInspectionApprovalList(String EmpId) throws Exception {
		Query query = manager.createNativeQuery(RFAINSPECTIONAPPROVALLIST);
		query.setParameter("EmpId", Long.parseLong(EmpId));
		return (List<Object[]>)query.getResultList();
	}
	
	
	private static final String RFAINSPECTIONLIST="CALL pfms_RfaInspection_List(:EmpId)"; 
	@Override
	public List<Object[]> RfaInspectionList(String EmpId) throws Exception {
		Query query = manager.createNativeQuery(RFAINSPECTIONLIST);
		query.setParameter("EmpId", Long.parseLong(EmpId));
		return (List<Object[]>)query.getResultList();
	}
	
	
	private static final String RFAFORWARDAPPROVALLIST="CALL pfms_RfaForwardApproved_List(:EmpId)"; 
	@Override
	public List<Object[]> RfaForwardApprovedList(String EmpId) throws Exception {
		Query query = manager.createNativeQuery(RFAFORWARDAPPROVALLIST);
		query.setParameter("EmpId", EmpId);
		return (List<Object[]>)query.getResultList();
	}
	
	private static final String RFAINSPECTIONAPPROVEDLIST="CALL pfms_RfaInspectionApproved_List(:EmpId)"; 
	@Override
	public List<Object[]> RfaInspectionApprovedList(String EmpId) throws Exception {
		Query query = manager.createNativeQuery(RFAINSPECTIONAPPROVEDLIST);
		query.setParameter("EmpId", EmpId);
		return (List<Object[]>)query.getResultList();
	}
	
	
	private static final String EMPS="SELECT a.projectdirector FROM project_master a WHERE a.projectid=:projectid UNION SELECT a.division_head_id FROM division_master a ,employee b WHERE a.division_id=b.divisionid AND b.empno=:empno UNION SELECT c.group_head_id FROM division_master a ,employee b,division_group c WHERE a.division_id=b.divisionid AND a.group_id=c.group_id AND b.empno=:empno";
	@Override
	public List<String> ListEmps(String UserId, String projectid) throws Exception {
		
		Query query =manager.createNativeQuery(EMPS);
		query.setParameter("empno", UserId);
		query.setParameter("projectid", Long.parseLong(projectid));
		
	 List<BigInteger>x=(List<BigInteger>)query.getResultList();
	 List<String>emps=new ArrayList<>();
	 
	 for(int i=0;i<x.size();i++) {
		 emps.add(x.get(i)+"");
	 }
	 
	 return emps;
	}
	
	private static final String UPDATESTATUS="UPDATE pfms_rfa_action SET rfastatus=:rfastatus WHERE rfaid=:rfa AND isactive=1 ";
	@Override
	public long RfaActionForward(List<PfmsNotification> x, RfaAction rf, List<RfaTransaction> trans, String rfa)throws Exception {
	
		for(PfmsNotification notification:x) {
			manager.persist(notification);
		}
		for(RfaTransaction rfaTransaction:trans) {
			manager.persist(rfaTransaction);
		}
		Query query = manager.createNativeQuery(UPDATESTATUS);
		query.setParameter("rfastatus", rf.getRfaStatus());
		query.setParameter("rfa", Long.parseLong(rfa));
		long a= query.executeUpdate();
		return a;
	}

	private static final String RFALIST="SELECT a.rfaid,a.labcode,a.rfano,a.rfadate,a.rfastatus,(SELECT CONCAT(e.empname,',' ,c.designation) FROM employee e, employee_desig c WHERE empid=:EmpId AND e.desigid=c.desigid)AS emp,a.TypeOfRfa,a.AssignorId FROM pfms_rfa_action a WHERE rfaid=:rfa";
    @Override
	public Object[] RfaList(String rfa,String EmpId) throws Exception {
		Query query = manager.createNativeQuery(RFALIST);
		query.setParameter("rfa", Long.parseLong(rfa));
		query.setParameter("EmpId", Long.parseLong(EmpId));
		return (Object[])query.getSingleResult();
	}
	
	private static final String GETUSERID="SELECT DISTINCT a.AssignorId  FROM pfms_rfa_action a,pfms_rfa_action_transaction b WHERE a.RfaId=b.RfaId  AND b.RfaId=:rfa";
	@Override
	public String getUserId(String rfa) throws Exception {
		Query query = manager.createNativeQuery(GETUSERID);
		query.setParameter("rfa",Long.parseLong(rfa));
		return query.getSingleResult().toString();
	}
	
	private static final String GETASSINEID="SELECT DISTINCT a.AssigneeId  FROM pfms_rfa_action a,pfms_rfa_action_transaction b WHERE a.RfaId=b.RfaId  AND b.RfaId=:rfa";
	@Override
	public String getAssineeId(String rfa) throws Exception {
		Query query = manager.createNativeQuery(GETASSINEID);
		query.setParameter("rfa", Long.parseLong(rfa));
		return query.getSingleResult().toString();
	}
	
	private static final String RFAACTION="SELECT RfaAssignId,LabCode,RfaId,RfaNo,CompletionDate,Observation,Clarification,ActionRequired,EmpId,RfaStatus,CreatedBy,CreatedDate,ModifiedBy,ModifiedDate FROM pfms_rfa_inspection WHERE rfaid=:rfa AND isactive=1";	
	@Override
	public Object[] getRfaAssign(String rfa) throws Exception {
		
		Query query = manager.createNativeQuery(RFAACTION);
		query.setParameter("rfa", Long.parseLong(rfa));
		Object[] assign=null;
		try {
			assign=(Object[])query.getSingleResult();
			}catch(Exception e) {
				
			}
		
		return assign;
	}
	
	@Override
	public Long RfaModalSubmit(RfaInspection inspection) throws Exception {
		
		manager.persist(inspection);
		
		return inspection.getRfaInspectionId();
	}
	
	
	private static final String RFAASSIGNDATA="SELECT rfainspectionid,labcode,rfaid,rfano,completiondate,observation,clarification,actionrequired,(SELECT AssigneeAttachment FROM pfms_rfa_attachment WHERE rfaid=:rfaid AND isactive='1')AS 'Assigneeattach'  FROM pfms_rfa_inspection WHERE rfaid=:rfaid AND isactive=1";
	@Override
	public Object[] RfaAssignAjax(String rfaId) throws Exception {
		
		Query query = manager.createNativeQuery(RFAASSIGNDATA);
		query.setParameter("rfaid", Long.parseLong(rfaId));
		
		Object[] RfaAssignAjax=null;
		try {
			RfaAssignAjax=(Object[])query.getSingleResult();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return RfaAssignAjax;
	}
	
	private static final String ASSIGNUPDATE="UPDATE pfms_rfa_inspection SET completiondate=:fdate , observation=:observation , clarification=:clarification , actionrequired=:Rfaaction,ModifiedBy=:ModifiedBy ,ModifiedDate=:ModifiedDate WHERE rfaid=:rfaid";
	@Override
	public Long RfaModalUpdate(RfaInspection inspection) throws Exception {
		
		
		Query query = manager.createNativeQuery(ASSIGNUPDATE);
		query.setParameter("rfaid", inspection.getRfaId());
		query.setParameter("fdate", inspection.getCompletionDate());
		query.setParameter("observation", inspection.getObservation());
		query.setParameter("clarification", inspection.getClarification());
		query.setParameter("Rfaaction", inspection.getActionRequired());
		query.setParameter("ModifiedBy", inspection.getModifiedBy());
		query.setParameter("ModifiedDate", inspection.getModifiedDate());
		
		
		return Long.valueOf(query.executeUpdate());
	}

	private final static String RFARETURNLIST="UPDATE pfms_rfa_action SET rfastatus=:rfastatus WHERE rfaid=:rfa AND isactive=1 ";

	@Override
	public Long RfaReturnList(List<PfmsNotification> x, RfaAction rf, RfaTransaction tr, String rfa) throws Exception {
		for(PfmsNotification notification:x) {
			manager.persist(notification);
		}
		manager.persist(tr);
		Query query = manager.createNativeQuery(UPDATESTATUS);
		query.setParameter("rfastatus", rf.getRfaStatus());
		query.setParameter("rfa", Long.parseLong(rfa));
		query.executeUpdate();
		return tr.getRfaTransactionId();
	}
	
	private static final String GETASSIGNRFAID ="SELECT COUNT(RfaId) FROM pfms_rfa_inspection WHERE RfaId=:rfaId AND IsActive='1'";
	@Override
	public String getAssignDetails(String empId, Long rfaId) throws Exception {
		logger.info( "Inside DAO getAssignDetails");
		
		try {
		Query query=manager.createNativeQuery(GETASSIGNRFAID);
		
		//query.setParameter("empId", empId);
		query.setParameter("rfaId", rfaId);
		Object executeUpdate = query.getSingleResult();
		return  executeUpdate.toString();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Inside DaoImpl getAssignDetails", e);
			return "0";
		}
	}
	
	private static final String GETRFAADDDATA = "SELECT DISTINCT pm.ProjectCode,CONCAT('(',pp.PriorityId,')',pp.Priority) AS priority,pa.RfaDate,pa.rfano,pa.Statement,pa.Description,pa.Reference FROM pfms_rfa_action pa,pfms_rfa_priority pp,employee e,project_master pm,employee_desig c WHERE pa.RfaId=:rfaId AND pa.IsActive='1' AND pa.PriorityId=pp.PriorityId AND e.desigid=c.desigid AND pm.ProjectId=pa.ProjectId;";
	@Override
	public Object[] getRfaAddData(String rfaId) throws Exception {
		logger.info( "Inside DAO getRfaAddData");
		try {
		Query query=manager.createNativeQuery(GETRFAADDDATA);
		
		query.setParameter("rfaId", Long.parseLong(rfaId));
		Object[] executeUpdate = (Object[])query.getSingleResult();
		return executeUpdate ;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Inside DaoImpl getRfaAddData", e);
			return null;
		}
	}
	
	private static final String GETRFAINSPECTIONDATA = "SELECT RfaNo,CompletionDate,Observation,Clarification,ActionRequired FROM pfms_rfa_inspection WHERE RfaId=:rfaId";
	@Override
	public Object[] getRfaInspectionData(String rfaId) throws Exception {
		logger.info( "Inside DAO getRfaAddData");
		try {
		Query query=manager.createNativeQuery(GETRFAINSPECTIONDATA);
		
		query.setParameter("rfaId", Long.parseLong(rfaId));
		Object[] executeUpdate = (Object[])query.getSingleResult();
		return executeUpdate ;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Inside DaoImpl getRfaAddData", e);
			return null;
		}
	}
	
	
	private static final String GETRFAREMARKS = "SELECT CONCAT (e.empname,',' ,c.designation) AS emp,t.Remarks,t.ActionDate,t.RfaStatus FROM pfms_rfa_action_transaction t,employee e,employee_desig c WHERE t.RfaId=:rfaId  AND t.actionby=e.EmpId AND e.desigid=c.desigid GROUP BY e.EmpName, c.Designation, t.Remarks, t.ActionDate, t.RfaStatus";
	@Override
	public List<Object[]> getrfaRemarks(String rfaId,String status) throws Exception {
		Query query=manager.createNativeQuery(GETRFAREMARKS);
		query.setParameter("rfaId", Long.parseLong(rfaId));
		List<Object[]> remarksList=(List<Object[]>)query.getResultList();	
		return remarksList;
	}
	
	public static final String PROJECTAPPLICABLECOMMITTEELIST="SELECT  b.committeeid,a.projectid, a.autoschedule,b.committeeshortname,b.committeename,b.projectapplicable FROM committee_project a,committee b WHERE a.committeeid=b.committeeid AND b.projectapplicable='P' AND a.projectid=:projectid";
	@Override
	public List<Object[]> ProjectApplicableCommitteeList(String projectid)throws Exception
	{
		Query query=manager.createNativeQuery(PROJECTAPPLICABLECOMMITTEELIST);
		query.setParameter("projectid", Long.parseLong(projectid));
		return (List<Object[]>)query.getResultList();
	}

	public static final String MEETTINGCOUNT="SELECT a.scheduleid,a.meetingid,a.scheduledate,GetCommitteeShortName(a.scheduledate,b.committeeshortname) AS committeeshortname,b.committeeid FROM committee_schedule a, committee b WHERE a.committeeid=:committeeid AND a.committeeid=b.committeeid AND a.scheduleflag IN('MKV','MMR','MMF','MMS','MMA') AND a.projectid=:projectid AND a.isactive=1";
	@Override
	public List<Object[]> MeettingCount(String committeeid, String projectid) throws Exception {
		
		Query query = manager.createNativeQuery(MEETTINGCOUNT);
		query.setParameter("committeeid", Long.parseLong(committeeid));
		query.setParameter("projectid", Long.parseLong(projectid));
		return (List<Object[]>)query.getResultList();
	}

	public static final String MEETINGLIST="CALL Pfms_Meeting_Action_Reports(:projectid,:committeeid,:scheduleid,:status)";
	@Override
	public List<Object[]> MeettingList(String committeeid, String projectid, String scheduleid,String status) throws Exception {
		Query query = manager.createNativeQuery(MEETINGLIST);
		query.setParameter("committeeid",Long.parseLong(committeeid));
		query.setParameter("projectid", Long.parseLong(projectid));
		query.setParameter("scheduleid", Long.parseLong(scheduleid));
		query.setParameter("status", status);		
		return (List<Object[]>)query.getResultList();
	}
	
	public static final String MEETINGACTIONLIST="CALL Pfms_Meeting_AllAction_List(:projectid,:committeeid,:scheduleid,:empId)";
	@Override
	public List<Object[]> MeettingActionList(String committeeid, String projectid, String scheduleid, String empId)
			throws Exception {
		Query query = manager.createNativeQuery(MEETINGACTIONLIST);
		query.setParameter("committeeid", Long.parseLong(committeeid));
		query.setParameter("projectid", Long.parseLong(projectid));
		query.setParameter("scheduleid", Long.parseLong(scheduleid));
		query.setParameter("empId", Long.parseLong(empId));
		return (List<Object[]>)query.getResultList();
	}
	
	@Override// newlyadded
	public List<Object[]> getAllEmployees(String flag) throws Exception {
		String allGh="SELECT DISTINCT a.group_head_id,CONCAT(IFNULL(CONCAT(e.title,' '),''), e.empname) AS 'employee',b.designation FROM division_group a, employee e, employee_desig b WHERE a.group_head_id=e.empid AND e.isactive='1' AND b.desigid=e.desigid";
		String allDh="SELECT DISTINCT a.division_head_id,CONCAT(IFNULL(CONCAT(e.title,' '),''), e.empname) AS 'employee',b.designation FROM division_master a, employee e, employee_desig b WHERE a.division_head_id=e.empid AND e.isactive='1' AND b.desigid=e.desigid";
		String allTd="SELECT DISTINCT a.tdheadid,CONCAT(IFNULL(CONCAT(e.title,' '),''), e.empname) AS 'employee',b.designation FROM division_td a, employee e, employee_desig b WHERE a.tdheadid=e.empid AND e.isactive='1' AND b.desigid=e.desigid";
		String allPd="SELECT DISTINCT a.ProjectDirector,CONCAT(IFNULL(CONCAT(e.title,' '),''), e.empname) AS 'employee',b.designation FROM project_master a, employee e, employee_desig b WHERE a.ProjectDirector=e.empid AND e.isactive='1' AND b.desigid=e.desigid";
		if(flag.equalsIgnoreCase("T")) {
			Query query=manager.createNativeQuery(allTd);
			return (List<Object[]>)query.getResultList();
		}else if(flag.equalsIgnoreCase("D")) {
			Query query=manager.createNativeQuery(allDh);
			return (List<Object[]>)query.getResultList();
		}else if(flag.equalsIgnoreCase("G")) {
			Query query=manager.createNativeQuery(allGh);
			return (List<Object[]>)query.getResultList();
		}else if(flag.equalsIgnoreCase("p"))  {
			Query query=manager.createNativeQuery(allPd);
			return (List<Object[]>)query.getResultList();
		}else {
			List<Object[]>allEmployees=new ArrayList<>();
			return allEmployees;
		}
		
	}

	
	public static final String RFAMODALEMPLIST="CALL pfms_Rfa_ModalEmplist(:labCode)";
	@Override
	public List<Object[]> getRfaModalEmpList(String labCode) throws Exception {
		Query query = manager.createNativeQuery(RFAMODALEMPLIST);
		query.setParameter("labCode", labCode);
		return (List<Object[]>)query.getResultList();
	}

	
	//public static final String RFATDLIST="SELECT DISTINCT a.empid,CONCAT(IFNULL(CONCAT(a.title,' '),''), a.empname) AS 'empname' ,b.designation,a.srNo FROM employee a,employee_desig b,division_td c WHERE a.isactive='1' AND a.DesigId=b.DesigId AND c.tdheadid=a.empid ORDER BY srno";
	public static final String RFATDLIST="CALL pfms_Rfa_ModalEmplist(:labCode)"; // all emp list will come
	@Override
	public List<Object[]> getRfaTDList(String labCode) throws Exception {
		Query query = manager.createNativeQuery(RFATDLIST);
		query.setParameter("labCode", labCode);
		return (List<Object[]>)query.getResultList();
	}

	public static final String RFATRANSACTIONLIST="SELECT a.rfatransactionid,c.rfano,e.empid,e.empname,d.designation,a.actiondate,a.remarks,b.rfastatusdetails,b.rfastatuscolor,a.rfastatus,(SELECT CONCAT(IFNULL(CONCAT(em.title,' '),''), em.empname,',', ed.designation)  AS 'empname' FROM employee em,employee_desig ed WHERE a.empid=em.empid AND a.rfaid=:rfaTransId AND em.desigid = ed.desigid )AS 'Forward To',a.empid AS 'RfaEmpId',a.actionby FROM pfms_rfa_action_transaction a,pfms_rfa_status b,pfms_rfa_action c,employee_desig d,employee e WHERE c.rfaid=a.rfaid AND a.rfastatus=b.rfastatus AND a.actionby=e.empid AND e.desigid = d.desigid AND c.rfaid=:rfaTransId ORDER BY a.rfatransactionid";
	@Override
	public List<Object[]> getRfaTransList(String rfaTransId) throws Exception {
		try {
			Query query = manager.createNativeQuery(RFATRANSACTIONLIST);
			query.setParameter("rfaTransId", Long.parseLong(rfaTransId));
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public Long RfaAttachment(RfaAttachment rfaAttach)throws Exception{
		
		logger.info( "Inside DaoImpl RfaAttachment");
		try {
		manager.persist(rfaAttach);
		manager.flush();
		return rfaAttach.getRfaAttachmentId();
	} catch (Exception e) {
		e.printStackTrace();
		logger.error( "Inside DaoImpl RfaAttachment", e);
		return null;
	}
		
	}

	public static final String RFAATTACHMENTDOWNLOAD="SELECT a.rfaattachmentid,a.rfaid,a.filespath,a.assignorattachment,a.assigneeattachment,b.rfano,a.CloseAttachment,\r\n"
			+ "CASE WHEN b.projectType = 'P' THEN p.projectcode ELSE h.projectShortName END AS projectCode \r\n"
			+ "FROM pfms_rfa_attachment a\r\n"
			+ "LEFT JOIN pfms_rfa_action b ON a.rfaid = b.rfaid\r\n"
			+ "LEFT JOIN project_master p ON p.projectid = b.projectid\r\n"
			+ "LEFT JOIN pfms_initiation h ON h.initiationId = b.projectid\r\n"
			+ "WHERE a.rfaid =:rfaid AND a.isactive = 1;";
	@Override
	public Object[] RfaAttachmentDownload(String rfaid) throws Exception {
		try {
			Query query = manager.createNativeQuery(RFAATTACHMENTDOWNLOAD);
			query.setParameter("rfaid", rfaid);
			return (Object[])query.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static final String DELETERFAATTACH="UPDATE pfms_rfa_attachment SET isactive='0' WHERE rfaid=:rfaid";
	@Override
	public int deleterfaAttachment(Long rfaId) throws Exception {
		
		Query query=manager.createNativeQuery(DELETERFAATTACH);
		query.setParameter("rfaid", rfaId);
		return query.executeUpdate();
	}
	
	public static final String UPDATERFAATTACH="UPDATE pfms_rfa_attachment SET AssigneeAttachment=:AssigneeAttachment,ModifiedBy=:modifiedby, ModifiedDate=:modifieddate  WHERE rfaid=:rfaid AND isactive='1'";
	@Override
	public long updateRfaAttachment(RfaAttachment rfaAttach) {
	
		Query query=manager.createNativeQuery(UPDATERFAATTACH);
		query.setParameter("rfaid", rfaAttach.getRfaId());
		query.setParameter("AssigneeAttachment", rfaAttach.getAssigneeAttachment());
		query.setParameter("modifiedby", rfaAttach.getModifiedBy());
		query.setParameter("modifieddate", rfaAttach.getModifiedDate());
		return query.executeUpdate();
	}

	@Override
	public long RfaAssignInsert(RfaAssign assign) throws Exception {

		logger.info( "Inside DaoImpl RfaAssignInsert");
		try {
		manager.persist(assign);
		manager.flush();
		return assign.getRfaAssignId();
	} catch (Exception e) {
		e.printStackTrace();
		logger.error( "Inside DaoImpl RfaAssignInsert", e);
		return 0;
	}
	}

	//public static final String ASSIGNEEEMPLIST="SELECT a.rfaid,CONCAT(IFNULL(CONCAT(e.title,' '),''), e.empname) AS 'empname',d.designation,a.assigneeid FROM  pfms_rfa_assign a,pfms_rfa_action b,employee e,employee_desig d WHERE a.rfaid=b.rfaid AND a.AssigneeId=e.empid AND e.desigid=d.desigid AND a.isactive='1'";
	public static final String ASSIGNEEEMPLIST="SELECT a.rfaid,CONCAT(IFNULL(CONCAT(e.title,' '),''), e.empname) AS 'empname',d.designation,\r\n"
			+ "a.assigneeid ,e.labcode FROM  pfms_rfa_assign a,pfms_rfa_action b,employee e,employee_desig d\r\n"
			+ " WHERE a.rfaid=b.rfaid AND a.AssigneeId=e.empid AND e.desigid=d.desigid AND a.isactive='1' AND a.labcode=e.labcode\r\n"
			+ " UNION\r\n"
			+ "SELECT a.rfaid,CONCAT(IFNULL(CONCAT(e.title,' '),''), e.expertname) AS 'empname',''AS'designation',\r\n"
			+ "a.assigneeid ,e.organization AS 'Labcode' FROM  pfms_rfa_assign a,pfms_rfa_action b,expert e,employee_desig d\r\n"
			+ " WHERE a.rfaid=b.rfaid AND a.AssigneeId=e.expertid AND e.desigid=d.desigid AND a.isactive='1' AND a.labcode=e.organization";
	
	@Override
	public List<Object[]> AssigneeEmpList() throws Exception {
		
		try {
			Query query = manager.createNativeQuery(ASSIGNEEEMPLIST);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static final String UPDATEASSIGNEEDATA="UPDATE pfms_rfa_assign SET isactive='0' WHERE rfaid=:rfaid";
	@Override
	public Long UpdateAssigneeData(String rfaid) throws Exception {
		
		Query query = manager.createNativeQuery(UPDATEASSIGNEEDATA);
		query.setParameter("rfaid", Long.parseLong(rfaid));		
		return (long) query.executeUpdate();
	}

	/*
	 * private static final String
	 * CCASSIGNEELIST="SELECT CONCAT(IFNULL(CONCAT(e.title,' '),''), e.empname) AS 'empname',d.designation FROM employee e,employee_desig d WHERE e.desigid=d.desigid AND e.empid IN (SELECT c.tdheadid FROM employee em,division_td c,pfms_rfa_assign a,division_master dm,division_group dg WHERE a.isactive='1' AND em.divisionid=dm.divisionid AND dm.groupid=dg.groupid AND dg.tdid=c.tdid AND a.assigneeid=em.empid AND a.rfaid=:rfaid)"
	 * ;
	 * 
	 * @Override public List<String> CCAssigneeList(String rfaid) throws Exception {
	 * 
	 * try { Query query = manager.createNativeQuery(CCASSIGNEELIST);
	 * query.setParameter("rfaid", rfaid); return
	 * (List<String>)query.getResultList(); }catch (Exception e) {
	 * e.printStackTrace(); return null; } }
	 */
	
	private static final String CCASSIGNORLIST="CALL pfms_RfaCCEmp_List(:rfaid)";
	@Override
	public List<String> CCAssignorList(String rfaid) throws Exception {
		
		try {
			Query query = manager.createNativeQuery(CCASSIGNORLIST);
			query.setParameter("rfaid", Long.parseLong(rfaid));
			return (List<String>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static final String RFAACTIONUPDATE="UPDATE pfms_rfa_action SET rfastatus='AAA' WHERE rfaid=:rfaId";
	@Override
	public int RfaActionUpdate(String rfaId) throws Exception {
		RfaAction ExistingRfaAction= manager.find(RfaAction.class, Long.parseLong(rfaId));
		if(ExistingRfaAction != null) {
			ExistingRfaAction.setRfaStatus("AAA");
			return 1;
		}
		else {
			 return 0;
		}
		
	}
	
	
	@Override
	public Long updateRfaTransaction(RfaTransaction tr) throws Exception {
		
		logger.info( "Inside DaoImpl updateRfaTransaction");
		try {
		manager.persist(tr);
		manager.flush();
		return tr.getRfaTransactionId();
	} catch (Exception e) {
		e.printStackTrace();
		logger.error( "Inside DaoImpl updateRfaTransaction", e);
		return 0l;
	}
	}
	
	private static final String RFAACTIONLIST1="SELECT DISTINCT a.rfaid,a.labcode,CASE WHEN a.projectType = 'P' THEN d.projectcode ELSE h.projectShortName END AS projectCode,a.rfano,a.rfadate,b.priority,\r\n"
			+ "f.classification AS category,a.statement,a.description,a.reference,a.isactive,a.createdby,a.createddate,a.projectid,a.rfastatus,a.AssignorId,\r\n"
			+ "(SELECT COUNT(Remarks) FROM pfms_rfa_action_transaction trans WHERE a.RfaId=trans.RfaId) AS Remarks,g.rfastatusdetails,a.TypeOfRfa,a.projectType\r\n"
			+ "FROM pfms_rfa_action a\r\n"
			+ "INNER JOIN pfms_rfa_priority b ON a.priorityid = b.priorityid\r\n"
			+ "LEFT JOIN project_master d ON a.projectid = d.projectid\r\n"
			+ "LEFT JOIN pfms_initiation h ON a.projectId = h.initiationId\r\n"
			+ "INNER JOIN pfms_security_classification f \r\n"
			+ "ON(\r\n"
			+ "(a.projectType = 'P' AND d.projectcategory = f.classificationid)\r\n"
			+ " OR (a.projectType <> 'P' AND h.classificationId = f.classificationid)\r\n"
			+ ")\r\n"
			+ "INNER JOIN pfms_rfa_status g ON a.rfastatus = g.rfastatus\r\n"
			+ "WHERE a.projectType = :projectType AND\r\n"
			+ "(\r\n"
			+ " (:projectType = 'P' AND (:projectId = 'A' OR a.projectId = :projectId))\r\n"
			+ "  OR\r\n"
			+ " (:projectType = 'I' AND (:initiationId = 'A' OR a.projectId = :initiationId))\r\n"
			+ ")\r\n"
			+ "AND a.rfadate BETWEEN :fdate AND :tdate AND a.rfastatus IN ('AA','AF','AC','RC','AX','REV','AP','RV','ARC','RFC') ORDER BY rfaid DESC"; 
	@Override
	public List<Object[]> GetRfaActionList1(String projectType, String projectId, String initiationId, String fdate, String tdate) throws Exception {
		
		Query query = manager.createNativeQuery(RFAACTIONLIST1);
		query.setParameter("projectType", projectType);
		query.setParameter("projectId", projectId);
		query.setParameter("initiationId", initiationId);
		query.setParameter("fdate", fdate);
		query.setParameter("tdate", tdate);
		return (List<Object[]>)query.getResultList();
	}
	
	
	private static final String RFAPROJECTWISE="CALL pfms_RfaProjectWise_List(:empId,:projectType,:projectId,:initiationId,:fdate,:tdate)";
	@Override
	public List<Object[]> RfaProjectwiseList(String empId, String projectType, String projectId, String initiationId, String fdate, String tdate) throws Exception {
		
		Query query = manager.createNativeQuery(RFAPROJECTWISE);
		query.setParameter("empId", empId);
		query.setParameter("projectType", projectType);
		query.setParameter("projectId", projectId);
		query.setParameter("initiationId", initiationId);
		query.setParameter("fdate", fdate);
		query.setParameter("tdate", tdate);
		return (List<Object[]>)query.getResultList();
	}
	
	@Override
	public List<Object[]> getmodifieddate(String userId, int pid)throws Exception {
		// TODO Auto-generated method stub
		Query query = manager.createNativeQuery("SELECT max(a.modifieddate),max(m.projectid) FROM action_assign a JOIN action_main m ON m.actionmainid=a.actionmainid WHERE a.createdby=:person AND m.projectid=:pid");
		// SELECT dateofupdate, projectId  FROM pfms_weekly_update WHERE pfms_weekly_update.role=:roal Limit :limitcount
		query.setParameter("person", userId);
		query.setParameter("pid", pid );
		return query.getResultList();
	}

	@Override
	public List<Object[]> getProjectByDirectorID(String empId)throws Exception {
		// TODO Auto-generated method stub
		Query query = manager.createNativeQuery("SELECT project_main_id, project_code FROM project_main WHERE project_director=:id");
		query.setParameter("id", Long.parseLong(empId));
		return query.getResultList();
	}

	@Override
	public List<Object[]> getRecentWeeklyUpdateDate(String string)throws Exception {
		// TODO Auto-generated method stub
		Query query = manager.createNativeQuery("SELECT UpdatedDate, projectId FROM pfms_weekly_update WHERE projectid=:id order by UpdatedDate desc");
		query.setParameter("id", Integer.parseInt(string));
		return query.getResultList();
	}
	
	

	@Override
	public List<Object[]> getRiskDetails(String LabCode, int pid)throws Exception {
		// TODO Auto-generated method stub
		Query query = manager.createNativeQuery("SELECT MAX(aas.progressdate) AS 'MAXprogressdate', IFNULL(MAX(am.projectid),0) AS 'MAXprojectid' FROM action_assign aas, action_main am WHERE aas.actionmainid = am.actionmainid AND am.type='K' AND am.projectid=:projectid");
		query.setParameter("projectid", pid );
		return query.getResultList();
	}

	@Override
	public List<Object[]> getMilestoneDate( int pid)throws Exception {
		// TODO Auto-generated method stub
		Query query = manager.createNativeQuery("SELECT MAX(modifieddate),IFNULL( max(projectid),0) AS 'MAXprojectid' FROM milestone_activity WHERE projectid=:projectid");
		query.setParameter("projectid", pid );
		return query.getResultList();
	}

	@Override
	public List<Object[]> getMeetingDate(int pid)throws Exception {
		// TODO Auto-generated method stub
		Query query = manager.createNativeQuery("SELECT MAX(modifieddate), max(projectid) FROM committee_schedule WHERE projectid=:projectid");
		query.setParameter("projectid", pid );
		return query.getResultList();
	}

	@Override
	public List<Object[]> getProdurementDate(int pid)throws Exception {
		// TODO Auto-generated method stub
		 String ProcurementLastUpdateDate = "SELECT MAX(modifieddate), max(projectid) FROM pfts_file WHERE projectid=:projectid";
		Query query = manager.createNativeQuery(ProcurementLastUpdateDate);
		query.setParameter("projectid", pid );
		return query.getResultList();
	}

	@Override
	public List<Object[]> getAllRecentUpdateList(String projectId)throws Exception {
		// TODO Auto-generated method stub
		Query query = manager.createNativeQuery("select * from pfms_weekly_update where projectid=:projectid order by UpdatedDate desc");
		query.setParameter("projectid", Integer.parseInt(projectId));
		return query.getResultList();
	}

	@Override
	public Object getEmpnameById(int empId)throws Exception {
		// TODO Auto-generated method stub
		Query query = manager.createNativeQuery("select emp_name from employee where emp_id=:empId");
		query.setParameter("empId", empId );
		return query.getResultList();
	}

	@Override
	public Object getProjectCodeById(int projectId)throws Exception {
		// TODO Auto-generated method stub
		Query query = manager.createNativeQuery("SELECT project_code FROM project_master WHERE project_id=:projectId");
		query.setParameter("projectId", projectId );
		return query.getResultList();
	}

	@Override
	public Object getProjectShortNameById(int projectId)throws Exception {
		// TODO Auto-generated method stub
		Query query = manager.createNativeQuery("SELECT project_short_name FROM project_master WHERE project_id=:projectId");
		query.setParameter("projectId", projectId );
		return query.getResultList();
	}
	
	private static final String SUBDELETEUPDATE="UPDATE action_assign SET progress=:progress, progressdate=:progressdate, ProgressRemark=:remarks WHERE actionassignid=:actionassignid";
	private static final String SUBDELETEUPDATE1="UPDATE action_assign SET progress='0', progressdate=NULL, ProgressRemark=NULL WHERE actionassignid=:actionassignid";
	@Override
	public int ActionSubDeleteUpdate(String ActionAssignId, String progress, String progressDate, String progressRemarks) throws Exception {
		
		if(progress!=null && progressDate!=null && progressRemarks!=null) {
			
			ActionAssign ExistingActionAssign = manager.find(ActionAssign.class, Long.parseLong(ActionAssignId));
			if(ExistingActionAssign !=null) {
				ExistingActionAssign.setProgress(Integer.parseInt(progress));
				ExistingActionAssign.setProgressDate(progressDate);
				ExistingActionAssign.setProgressRemark(progressRemarks);
				return 1;
		}else {
			return 0;
				}
			
		}else {
			ActionAssign ExistingActionAssign1 = manager.find(ActionAssign.class,Long.parseLong(ActionAssignId));
			if(ExistingActionAssign1 !=null) {
				ExistingActionAssign1.setProgress(0);
				ExistingActionAssign1.setProgressDate(null);
				ExistingActionAssign1.setProgressRemark(null);
				return 1;
		}else {
			return 0;
				}
		}
		
	}
	
	private static final String ACTIONREMARKUPDATE="UPDATE action_assign SET progress=:progress, ProgressRemark=:remarks,"
			+ "ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate WHERE actionassignid=:actionassignid";
	@Override
	public int ActionRemarksEdit(String actionAssignId, String progress, String progressRemarks, String UserId) throws Exception {
		ActionAssign ExistingActionAssign= manager.find(ActionAssign.class, Long.parseLong(actionAssignId));
		if(ExistingActionAssign != null) {
			ExistingActionAssign.setProgress(Integer.parseInt(progress));
			ExistingActionAssign.setProgressRemark(progressRemarks);
			ExistingActionAssign.setModifiedBy(UserId);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String formattedDate = LocalDateTime.now().format(formatter);
			ExistingActionAssign.setModifiedDate(formattedDate);
			return 1;
		}
		else {
			return 0;
		}
		
	}
	
	private static final String SUBREMARKSUPDATE="UPDATE action_sub SET progress=:progress, Remarks=:remarks,"
			+ "ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate WHERE actionsubid=:actionsubid";
	@Override
	public int actionSubRemarksEdit(String actionSubId, String progress, String progressRemarks, String UserId) throws Exception {
		ActionSub ExistingActionSub = manager.find(ActionSub.class, Long.parseLong(actionSubId));
		if(ExistingActionSub != null) {
			ExistingActionSub.setProgress(Integer.parseInt(progress));
			ExistingActionSub.setRemarks(progressRemarks);
			ExistingActionSub.setModifiedBy(UserId);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String formattedDate = LocalDateTime.now().format(formatter);
			ExistingActionSub.setModifiedDate(formattedDate);
			return 1;
		}
		else {
			return 0;
		}
		
	}
	
	private static final String RFAMAILSENDING="CALL pfms_Rfa_Mail(:rfa)";
	@Override
	public List<String> rfaMailSend(String rfa) throws Exception {
		
		Query query = manager.createNativeQuery(RFAMAILSENDING);
		query.setParameter("rfa", Long.parseLong(rfa));
		
		return (List<String>)query.getResultList();
	}

	// Prudhvi - 13/03/2024
	private static final String RODSHORTNAME = "SELECT a.RODNameId, a.RODName,a.RODShortName FROM pfms_rod_master a,committee_schedule b , committee_schedules_minutes_details c WHERE a.RODNameId=b.RODNameId AND b.scheduleid=c.scheduleid AND c.scheduleminutesid=:scheduleid AND b.isactive=1";
	@Override
	public Object[] rodShortName(String scheduleid)throws Exception
	{
		Query query=manager.createNativeQuery(RODSHORTNAME);
		query.setParameter("scheduleid", Long.parseLong(scheduleid));
		Object[] rodShortName=(Object[])query.getResultList().get(0);
		return rodShortName;
	}
	
	private static final String RFANOTYPE="SELECT RfaTypeId,RfaTypeName FROM pfms_rfa_type";
	@Override
	public List<Object[]> getRfaType() throws Exception 
	{
		Query query=manager.createNativeQuery(RFANOTYPE);
		return (List<Object[]>)query.getResultList();
	}
	
	@Override
	public long rfaCCInsert(RfaCC rfaCC) throws Exception {
		
		logger.info( "Inside DaoImpl rfaCCInsert");
		try {
		manager.persist(rfaCC);
		manager.flush();
		return rfaCC.getRfaCCId();
	    } catch (Exception e) {
		e.printStackTrace();
		logger.error( "Inside DaoImpl rfaCCInsert", e);
		return 0;
	    }
	}
	
	private static final String UPDATERFACCDATA="UPDATE pfms_rfa_cc SET isactive='0' WHERE rfaid=:rfaid";
	@Override
	public Long updateRfaCC(String rfaid) throws Exception {
		Query query = manager.createNativeQuery(UPDATERFACCDATA);
		query.setParameter("rfaid", Long.parseLong(rfaid));		
		return (long) query.executeUpdate();
	}
	
	private static final String GETRFACCLIST=" SELECT a.rfaid,CONCAT(IFNULL(CONCAT(e.title,' '),''), e.empname) AS 'empname',d.designation,a.ccempid,a.labcode  FROM  pfms_rfa_cc a,pfms_rfa_action b,employee e,employee_desig d WHERE a.rfaid=b.rfaid AND a.ccempid=e.empid AND e.desigid=d.desigid AND a.isactive='1'";
	@Override
	public List<Object[]> RfaCCList() throws Exception {
		try {
			Query query = manager.createNativeQuery(GETRFACCLIST);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static final String RFAACTIONLISTS="SELECT a.RfaId, a.RfaNo,a.RfaDate,d.Priority,a.projectId,a.Statement,a.Description,a.Reference,\r\n"
			+ "c.RfaStatusDetails,insp.CompletionDate,insp.Observation,insp.Clarification,insp.ActionRequired,\r\n"
			+ "a.rfastatus,a.rfaRaisedByName AS 'industryPartner',\r\n"
			+ "CONCAT(COALESCE(emp.title, ''), ' ', emp.empname, ', ', desig.designation) AS raisedBy\r\n"
			+ "FROM pfms_rfa_action a\r\n"
			+ "JOIN pfms_rfa_status c ON a.RfaStatus = c.RfaStatus\r\n"
			+ "JOIN pfms_rfa_priority d ON a.PriorityId = d.PriorityId\r\n"
			+ "LEFT JOIN pfms_rfa_inspection insp ON insp.rfaid = a.rfaid\r\n"
			+ "LEFT JOIN login l ON l.username = a.createdBy\r\n"
			+ "LEFT JOIN employee emp ON l.empId = emp.empId\r\n"
			+ "LEFT JOIN employee_desig desig ON emp.desigid = desig.desigid\r\n"
			+ "WHERE a.projectid =:projectid AND a.projectType =:projectType AND a.RfaNo LIKE :rfatypeid\r\n"
			+ "AND a.RfaDate BETWEEN :fdate AND :tdate ORDER BY a.rfaid";
    @Override
	public List<Object[]> rfaTotalActionList(String projectType,String projectid, String rfatypeid, String fdate, String tdate) {
		Query query = manager.createNativeQuery(RFAACTIONLISTS);
		rfatypeid="%"+rfatypeid+"%";
		query.setParameter("projectType", projectType);
		query.setParameter("projectid", projectid);
		query.setParameter("rfatypeid", rfatypeid);
		query.setParameter("fdate", fdate);
		query.setParameter("tdate", tdate);
		return (List<Object[]>)query.getResultList();
	}
	
	
	private static final String COMMITTACTIONEDIT="UPDATE action_assign SET pdcorg=:pdcorg,enddate=:enddate,"
			+ "assignee=:assignee,assignor=:assignor,assignorlabcode=:assignorlabcode,assigneelabcode=:assigneelabcode"
			+ ",modifiedby=:modifiedby,modifieddate=:modifieddate WHERE actionassignid=:actionassignid";
	@Override
	public int CommitteActionEdit(ActionAssignDto actionAssign) throws Exception {
		ActionAssign ExistingActionAssign = manager.find(ActionAssign.class, actionAssign.getActionAssignId());
		if(ExistingActionAssign != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String pdcOrgString = actionAssign.getPDCOrg(); 
			java.util.Date utilDate = formatter.parse(pdcOrgString); 
			java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
			ExistingActionAssign.setPDCOrg(sqlDate);
			String endDate= actionAssign.getEndDate();
			java.util.Date utilendDate = formatter.parse(endDate); 
			java.sql.Date sqlEndDate = new java.sql.Date(utilendDate.getTime());
			ExistingActionAssign.setEndDate(sqlEndDate);
			ExistingActionAssign.setAssignee(actionAssign.getAssignee());
			ExistingActionAssign.setAssignor(actionAssign.getAssignor());
			ExistingActionAssign.setAssignorLabCode(actionAssign.getAssignorLabCode());
			ExistingActionAssign.setAssigneeLabCode(actionAssign.getAssigneeLabCode());
			ExistingActionAssign.setModifiedBy(actionAssign.getModifiedBy());
			ExistingActionAssign.setModifiedDate(actionAssign.getModifiedDate());
			return 1;
		}
		else {
			return 0;
		}
		
	}
	
	private static final String RFAPENDINGCOUNT="SELECT rfaid,rfastatus,empid,actionby FROM pfms_rfa_action_transaction WHERE empid=:empId";
	@Override
	public List<Object[]> RfaPendingCount(String empId) throws Exception {
		Query query = manager.createNativeQuery(RFAPENDINGCOUNT);
		query.setParameter("empId", Long.parseLong(empId));
		return (List<Object[]>)query.getResultList();
	}
	
	private static String ACTIONMAINIDS=" SELECT aas.actionMainId,aas.ActionAssignId,aas.ActionNo FROM action_assign aas,action_main am WHERE am.scheduleminutesid=:scheduleMinutesId AND am.ActionMainId=aas.ActionMainId AND aas.isactive='1' ORDER BY aas.ActionAssignId  DESC";

	@Override
	public List<Object[]> getMainIds(String scheduleMinutesId) throws Exception {
		
		
		Query query = manager.createNativeQuery(ACTIONMAINIDS);
		query.setParameter("scheduleMinutesId", Long.parseLong(scheduleMinutesId));		
		List<Object[]>main = new ArrayList<>();
	
		 try {
			main=(List<Object[]>)query.getResultList();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return main;
	}
	
	private static final String ActionReportsNew="CALL Pfms_Action_Reports_new(:empid,:term,:position,:type,:loginType)";

	@Override
	public List<Object[]> ActionReportsNew(String EmpId,String Term,String Position,String Type,String LabCode,String loginType) throws Exception {
		
		Query query=manager.createNativeQuery(ActionReportsNew);
		query.setParameter("empid",Long.parseLong(EmpId));
		query.setParameter("term",Term);
		query.setParameter("position",Position);
		query.setParameter("type",Type);
		query.setParameter("loginType",loginType);
		//query.setParameter("LabCode",LabCode);
		
		List<Object[]> AssignedList=(List<Object[]>)query.getResultList();	
		return AssignedList;
	}
	
	@Override
	public Long oldRfaUploadSubmit(OldRfaUpload rfaUpload) throws Exception {
		
		logger.info( "Inside DaoImpl oldRfaUploadSubmit");
		try {
		manager.persist(rfaUpload);
		manager.flush();
		return rfaUpload.getRfaFileUploadId();
	    } catch (Exception e) {
		e.printStackTrace();
		logger.error( "Inside DaoImpl oldRfaUploadSubmit", e);
		return 0l;
	    }
	}
	
	private static final String OLDRFAUPLOADLIST="SELECT RfaFileUploadId,RfaNo,RfaDate,RfaFile,ClosureFile,Path FROM pfms_rfa_oldfile WHERE LabCode=:labCode AND ProjectId=:projectId AND IsActive='1'";
	@Override
	public List<Object[]> getoldRfaUploadList(String labCode, String projectId) throws Exception {
		
		try {
			Query query = manager.createNativeQuery(OLDRFAUPLOADLIST);
			query.setParameter("labCode", labCode);
			query.setParameter("projectId", Long.parseLong(projectId));
			return (List<Object[]>)query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public OldRfaUpload getOldRfaDetails(Long rfaFileUploadId) throws Exception {
		try {
			return manager.find(OldRfaUpload.class, rfaFileUploadId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static final String UPDATEOLDRFADATA="UPDATE pfms_rfa_oldfile SET RfaNo=:RfaNo,RfaDate=:RfaDate,RfaFile=:RfaFile,"
			+ "ClosureFile=:ClosureFile,Path=:Path,ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate "
			+ "WHERE RfaFileUploadId=:RfaFileUploadId";
	@Override
	public long oldRfaUploadEditSubmit(OldRfaUpload rfaModel) throws Exception {
		Long count=0L;
		try {
			OldRfaUpload ExistingOldRfaUpload = manager.find(OldRfaUpload.class, rfaModel.getRfaFileUploadId());
			if(ExistingOldRfaUpload != null) {
				ExistingOldRfaUpload.setRfaNo(rfaModel.getRfaNo());
				ExistingOldRfaUpload.setRfaDate(rfaModel.getRfaDate());
				ExistingOldRfaUpload.setRfaFile(rfaModel.getRfaFile());
				ExistingOldRfaUpload.setClosureFile(rfaModel.getClosureFile());
				ExistingOldRfaUpload.setPath(rfaModel.getPath());
				ExistingOldRfaUpload.setModifiedBy(rfaModel.getModifiedBy());
				ExistingOldRfaUpload.setModifiedDate(rfaModel.getModifiedDate());
				count+=1L;
			}
			return count;
			
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		
	}

	private static final String  ACTIONCOUNTBYCOMMITTEE = "SELECT COUNT(DISTINCT a.actionmainid)AS 'count' FROM action_main a, committee_schedules_minutes_details b, committee_schedule c\r\n"
			+ "WHERE a.projectid=:ProjectId AND a.isactive='1' AND a.type=:Type AND a.ScheduleMinutesId=b.ScheduleMinutesId AND b.ScheduleId=c.ScheduleId AND c.CommitteeId IN (SELECT d.CommitteeId FROM committee d WHERE d.CommitteeShortName=:CommitteeShortName AND d.IsActive=1)\r\n"
			+ "AND DATE_FORMAT(CURDATE(), \"%Y\")=DATE_FORMAT(a.actiondate, \"%Y\")";
	@Override
	public int getActionCountByCommittee(String projectId, String type, String committeeShortName) throws Exception {
		
		Query query = manager.createNativeQuery(ACTIONCOUNTBYCOMMITTEE);
		query.setParameter("ProjectId", Long.parseLong(projectId));
		query.setParameter("Type", type);
		query.setParameter("CommitteeShortName", committeeShortName);
		Long count=(Long)query.getSingleResult();
		return count.intValue();
	}
	
	
	private static final String getMeetingList="SELECT cs.scheduleid,cs.MeetingId,cs.scheduledate,cs.ScheduleStartTime,cs.MeetingVenue, c.committeeshortname,e.emp_name,csi.EmpId,csi.MemberType\r\n"
			+ "  FROM committee_schedule cs,committee c,committee_schedules_invitation csi, employee e WHERE  cs.ScheduleId= csi.CommitteeScheduleId AND cs.committeeid=c.committeeid\r\n"
			+ " AND cs.isActive=1 AND cs.projectid=:projectId AND csi.MemberType='CS' AND csi.EmpId=e.emp_id  AND cs.ScheduleDate BETWEEN :fromDate AND :toDate ORDER BY cs.scheduledate\r\n"
			+ " ";
	@Override
	public List<Object[]> getMeetingList(long projectId, String fromDate, String toDate) throws Exception {
		try {
			Query query = manager.createNativeQuery(getMeetingList);
			query.setParameter("projectId", projectId);
			query.setParameter("fromDate", fromDate);
			query.setParameter("toDate", toDate);
			
			return (List<Object[]>)query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static final String getMeetingAction="SELECT DISTINCT aas.actionno,CONCAT(IFNULL(CONCAT(ab.title,' '),''), ab.emp_name) AS 'assigneemp',dc.designation, CONCAT(IFNULL(CONCAT(d.title,' '),''), d.emp_name) AS 'assignoremp' ,\r\n"
			+ "e.designation AS desig,a.actiondate,aas.enddate,a.actionitem,aas.actionstatus,aas.actionflag,a.actionmainid,\r\n"
			+ "(SELECT c.progress FROM action_sub c  WHERE c.actionassignid = aas.actionassignid AND c.actionsubid = (SELECT MAX(f.actionsubid) FROM action_sub f WHERE f.actionassignid = aas.actionassignid) )AS progress ,\r\n"
			+ "aas.actionassignid,a.actionlinkid,a.actionlevel ,a.projectid,aas.assignee,aas.assignor \r\n"
			+ "FROM action_main a,  employee ab ,employee_desig dc ,  employee d  ,employee_desig e , action_assign aas,\r\n"
			+ "committee_main cc, committee_schedule cs, committee_schedules_minutes_details csm\r\n"
			+ "WHERE aas.assignee=ab.emp_id AND ab.is_active='1' AND dc.desig_id=ab.desig_id \r\n"
			+ "AND  aas.assignor=d.emp_id  AND a.actionmainid=aas.actionmainid AND d.is_active='1' AND e.desig_id=d.desig_id \r\n"
			+ "AND a.scheduleminutesid=csm.scheduleminutesid AND csm.scheduleid=cs.scheduleid\r\n"
			+ "AND cs.committeeid=cc.committeeid AND  cs.projectid=cc.projectid AND cc.projectid=a.projectid \r\n"
			+ " AND cs.scheduleid=:meetingId  AND (CASE \r\n"
			+ "WHEN :loginType IN ('A','Z','E','L','P') \r\n"
			+ "THEN 1=1 ELSE aas.assignee=:empId END)     \r\n"
			+ "ORDER BY  actionmainid DESC;";
	@Override
	public List<Object[]> getMeetingAction(long meetingId, String loginType, String empId) throws Exception {
		try {
			Query query = manager.createNativeQuery(getMeetingAction);
			query.setParameter("meetingId", meetingId);
			query.setParameter("loginType", loginType);
			query.setParameter("empId", Long.parseLong(empId));
			
			return (List<Object[]>)query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static final String ACTIONDELETE="DELETE FROM action_assign WHERE ActionAssignId=:ActionAssignId";
	@Override
	public int CommitteActionDelete(ActionAssignDto actionAssign) throws Exception {
		try {
			Query query = manager.createNativeQuery(ACTIONDELETE);
			query.setParameter("ActionAssignId", actionAssign.getActionAssignId());
			return query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private static final String VENDORLIST="SELECT LabCode,LabName,LabAddress,LabCode as 'Vendor' FROM lab_master\r\n"
			+ "UNION \r\n"
			+ "SELECT Organization AS 'LabCode' ,Organization as 'LabName',Organization as 'LabAddress','@EXP' AS 'Vendor'  FROM expert where isactive='1' ORDER BY Labcode ASC";
	@Override
	public List<Object[]> getVendorList() throws Exception {
		Query query = manager.createNativeQuery(VENDORLIST);
		return (List<Object[]>)query.getResultList();
	}
	public RfaAttachment getAttachmentByRfaId(Long rfaId) {
		 try {
		        String jpql = "SELECT r FROM RfaAttachment r WHERE r.RfaId = :rfaId";
		        TypedQuery<RfaAttachment> query = manager.createQuery(jpql, RfaAttachment.class);
		        query.setParameter("rfaId", rfaId);
		        return query.getSingleResult();
		    } catch (Exception e) {
		        return null; // Explicitly handle no result case
		    }
	}
	
	@Override
	public void saveActionMainAttachment(ActionMainAttachment attachment) throws Exception {
		// TODO Auto-generated method stub
		manager.persist(attachment);
		manager.flush();
	}
	
	private static final String ACTIONMAINATTACH="SELECT AttachmentId,FileName FROM action_main_attachment WHERE ActionMainId=:ActionMainId AND isactive='1'";
	
	@Override
	public Object[] getActionMainAttachMent(String mainid) throws Exception {

			try {
				Query query = manager.createNativeQuery(ACTIONMAINATTACH);
				query.setParameter("ActionMainId", Long.parseLong(mainid));
				
				return (Object[])query.getSingleResult();
				
			}catch(Exception e){
				
			}
		
		return null;
	}
	
	public static final String TOTALACTIONS ="SELECT a.actionmainid,CONCAT(IFNULL(CONCAT(ab.title,' '),''), ab.empname) AS 'empname' ,dc.designation,a.actiondate,aas.enddate,\r\n"
			+ "a.actionitem,aas.actionstatus,aas.actionstatus AS 'status' , aas.isactive,\r\n"
			+ "aas.progress , \r\n"
			+ "aas.remarks ,a.actionlinkid,aas.actionno, aas.actionassignid ,a.projectid,aas.assignor,aas.assignee \r\n"
			+ "FROM action_main a,  employee ab ,employee_desig dc , action_assign aas WHERE a.actionmainid=aas.actionmainid AND aas.assignee=ab.empid\r\n"
			+ "AND ab.isactive='1' AND dc.desigid=ab.desigid AND aas.actionstatus IN ('F','A','B','I') \r\n"
			+ "AND aas.assigneelabcode <> '@EXP' \r\n"
			+ "UNION \r\n"
			+ "SELECT a.actionmainid,CONCAT(IFNULL(CONCAT(ab.title,' '),''), ab.expertname) AS 'empname' ,'Expert' AS 'designation',a.actiondate,aas.enddate,a.actionitem,aas.actionstatus,aas.actionstatus AS 'status',\r\n"
			+ "aas.isactive,\r\n"
			+ "aas.progress , \r\n"
			+ "aas.remarks ,a.actionlinkid,aas.actionno, aas.actionassignid ,a.projectid,aas.assignor,aas.assignee FROM action_main a,  expert ab , action_assign aas\r\n"
			+ "WHERE a.actionmainid=aas.actionmainid AND aas.assignee=ab.expertid AND ab.isactive='1'\r\n"
			+ "AND aas.actionstatus IN ('F','A','B','I') AND aas.assigneelabcode = '@EXP'ORDER BY assignee ASC";
	@Override
	public List<Object[]> TotalActions() throws Exception {
		Query query = manager.createNativeQuery(TOTALACTIONS);
		return (List<Object[]>)query.getResultList();
	}
	
	
	
	private final String PROJECTS="SELECT project_id,project_main_id,project_code,project_name FROM project_master WHERE project_director= :empid";
	
	@Override
	public List<Object[]> getProjects(String empId) throws Exception {
		Query query = manager.createNativeQuery(PROJECTS);
		query.setParameter("empid", empId);
		return (List<Object[]>)query.getResultList();
	}
	private static final String ASSIGNEREDIT ="UPDATE action_assign SET Assignor =:Assignor WHERE ActionAssignId =:ActionAssignId";
	@Override
	public int ActionAssignerEdit(ActionAssign assign) throws Exception {
		Query query = manager.createNativeQuery(ASSIGNEREDIT);
		query.setParameter("Assignor", assign.getAssignor());
		query.setParameter("ActionAssignId", assign.getActionAssignId());
		return query.executeUpdate();
	}
	
	private static final String GETPROJECTCODE = "SELECT CASE WHEN 'P'=:projectType THEN (SELECT d.projectcode FROM project_master d WHERE d.projectid=:projectId AND d.isactive='1') ELSE (SELECT h.projectShortName FROM pfms_initiation h WHERE h.initiationId=:projectId AND h.isactive='1') END AS projectCode";
	@Override
	public String getProjectCode(Long projectId, String projectType) throws Exception {
		Query query=manager.createNativeQuery(GETPROJECTCODE);
		query.setParameter("projectId",projectId);
		query.setParameter("projectType",projectType);
		String projectCode=(String)query.getSingleResult();	
		return projectCode;
	}

	private static final String ASSIGNEELISTFORFROMEXTERNAL = """
			SELECT a.rfaid,CONCAT(IFNULL(CONCAT(e.title,' '),''), e.empname) AS 'empname',d.designation, 
			a.assigneeid ,e.labcode, a.labcode FROM  pfms_rfa_assign a,pfms_rfa_action b,employee e,employee_desig d 
			 WHERE a.rfaid=b.rfaid AND a.AssigneeId=e.empid AND e.desigid=d.desigid AND a.isactive='1' 
			 UNION 
			SELECT a.rfaid,CONCAT(IFNULL(CONCAT(e.title,' '),''), e.expertname) AS 'empname',''AS'designation', 
			a.assigneeid ,e.organization, a.labcode AS 'Labcode' FROM  pfms_rfa_assign a,pfms_rfa_action b,expert e,employee_desig d 
			 WHERE a.rfaid=b.rfaid AND a.AssigneeId=e.expertid AND e.desigid=d.desigid AND a.isactive='1' AND a.labcode=e.organization
			""";
	@Override
	public List<Object[]> assigneeListForFromExternal() throws Exception{
		try {
			Query query = manager.createNativeQuery(ASSIGNEELISTFORFROMEXTERNAL);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Object[]> divisionList() throws Exception {
		try {
			Query query = manager.createNativeQuery("SELECT * FROM division_master");
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static final String DIVISIONEMPLOYEESLIST = "SELECT DISTINCT e.EmpId, CONCAT(IFNULL(CONCAT(e.Title,' '),''), e.Empname) AS 'empname', b.designation FROM division_master d LEFT JOIN employee e ON e.divisionId = d.divisionId LEFT JOIN employee_desig b ON b.desigId = e.desigId WHERE d.DivisionId = :divisionId AND e.EmpId IS NOT NULL";
	@Override
	public List<Object[]> getDivisionEmployeesList(String divisionId) throws Exception {
		try {
			Query query = manager.createNativeQuery(DIVISIONEMPLOYEESLIST);
			query.setParameter("divisionId", Long.parseLong(divisionId));
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static final String RFAACTIONSONRFATYPE = """
			SELECT DISTINCT a.RfaId, a.RfaNo,a.RfaDate,d.Priority,a.projectId,a.Statement,a.Description,a.Reference, 
			c.RfaStatusDetails,insp.CompletionDate,insp.Observation,insp.Clarification,insp.ActionRequired, 
			a.rfastatus,a.rfaRaisedByName AS 'industryPartner', 
			CONCAT(COALESCE(emp.title, ''), ' ', emp.empname, ', ', desig.designation) AS raisedBy 
			FROM pfms_rfa_action a JOIN pfms_rfa_assign z ON z.RfaId = a.RfaId
			JOIN pfms_rfa_status c ON a.RfaStatus = c.RfaStatus 
			JOIN pfms_rfa_priority d ON a.PriorityId = d.PriorityId 
			LEFT JOIN pfms_rfa_inspection insp ON insp.rfaid = a.rfaid 
			LEFT JOIN login l ON l.username = a.createdBy 
			LEFT JOIN employee emp ON l.empId = emp.empId 
			LEFT JOIN employee_desig desig ON emp.desigid = desig.desigid 
			WHERE a.projectid =:projectid AND a.projectType =:projectType AND a.RfaNo LIKE :rfatypeid 
			AND a.TypeOfRfa = :rfatype  AND (:empId IS NULL OR :empId =z.AssigneeId) AND (( :rfaStatus = 'ALL' AND a.RfaStatus NOT IN ('RFC', 'ARC'))OR( :rfaStatus <> 'ALL' AND a.RfaStatus = :rfaStatus))
			AND a.RfaDate BETWEEN :fdate AND :tdate ORDER BY a.rfaid
			""";
	@Override
	public List<Object[]> getRfaActionsOnRFAtype(String projectType,String projectid,String rfatypeid,String rfatype,String empId, String fdate, String tdate,String rfaStatus) throws Exception {
		Query query = manager.createNativeQuery(RFAACTIONSONRFATYPE);
		rfatypeid="%"+rfatypeid+"%";
		query.setParameter("projectType", projectType);
		query.setParameter("projectid", projectid);
		query.setParameter("rfatypeid", rfatypeid);
		query.setParameter("rfatype", rfatype);
		Long empIdLong = null;
		if (empId != null && !empId.trim().isEmpty() && !"null".equalsIgnoreCase(empId)) {
		    empIdLong = Long.valueOf(empId);
		}
		query.setParameter("empId", empIdLong);
		query.setParameter("fdate", fdate);
		query.setParameter("tdate", tdate);
		query.setParameter("rfaStatus", rfaStatus);
		return (List<Object[]>)query.getResultList();
	}	
}