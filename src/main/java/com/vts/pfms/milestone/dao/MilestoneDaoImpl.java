	package com.vts.pfms.milestone.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.vts.pfms.committee.model.PfmsNotification;
import com.vts.pfms.milestone.dto.MileEditDto;
import com.vts.pfms.milestone.dto.MilestoneActivityDto;
import com.vts.pfms.milestone.model.ActivityTransaction;
import com.vts.pfms.milestone.model.FileDocAmendment;
import com.vts.pfms.milestone.model.FileDocMaster;
import com.vts.pfms.milestone.model.FileProjectDoc;
import com.vts.pfms.milestone.model.FileRepMaster;
import com.vts.pfms.milestone.model.FileRepMasterPreProject;
import com.vts.pfms.milestone.model.FileRepNew;
import com.vts.pfms.milestone.model.FileRepNewPreProject;
import com.vts.pfms.milestone.model.FileRepUploadNew;
import com.vts.pfms.milestone.model.FileRepUploadPreProject;
import com.vts.pfms.milestone.model.MilestoneActivity;
import com.vts.pfms.milestone.model.MilestoneActivityBriefing;
import com.vts.pfms.milestone.model.MilestoneActivityLevel;
import com.vts.pfms.milestone.model.MilestoneActivityLevelRemarks;
import com.vts.pfms.milestone.model.MilestoneActivityPredecessor;
import com.vts.pfms.milestone.model.MilestoneActivitySub;
import com.vts.pfms.milestone.model.MilestoneActivitySubRev;
import com.vts.pfms.milestone.model.MilestoneSchedule;
import com.vts.pfms.milestone.model.ProjectEconomicImpact;
import com.vts.pfms.milestone.model.ProjectEconomicImpactRev;
import com.vts.pfms.milestone.model.ProjectInfrastructureUtilization;
import com.vts.pfms.milestone.model.ProjectInfrastructureUtilizationRev;
import com.vts.pfms.milestone.model.ProjectManPowerUtilization;
import com.vts.pfms.milestone.model.ProjectManPowerUtilizationRev;
import com.vts.pfms.milestone.model.ProjectResourceUtilization;
import com.vts.pfms.milestone.model.ProjectTrainingUtilization;
import com.vts.pfms.milestone.model.ProjectTrainingUtilizationRev;
import com.vts.pfms.print.model.ProjectTechnicalWorkData;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;



@Transactional
@Repository
public class MilestoneDaoImpl implements MilestoneDao {

	private static final String MALIST="SELECT a.MilestoneActivityId, b.project_name, a.StartDate, a.EndDate, a.ActivityName, a.MilestoneNo,\r\n"
			+ "CONCAT(IFNULL(CONCAT(c.title,' '),(IFNULL(CONCAT(c.salutation, ' '), ''))), c.emp_name, ', ', e.Designation) AS 'OicEmpId1Name', \r\n"
			+ "CONCAT(IFNULL(CONCAT(d.title,' '),(IFNULL(CONCAT(d.salutation, ' '), ''))), d.emp_name, ', ', f.Designation) AS 'OicEmpId2Name',\r\n"
			+ "(SELECT MAX(e.revisionno) FROM milestone_activity_rev e WHERE a.milestoneactivityid=e.milestoneactivityid) AS rev,\r\n"
			+ "a.acceptedby,a.isaccepted,a.statusremarks,a.progressstatus,a.Weightage,a.activitystatusid,b.project_id,a.dateofcompletion, a.OICEmpId, a.financialOutlay,a.oicempid1, a.IsSunSet \r\n"
			+ "FROM milestone_activity a,project_master b, employee c,employee d, employee_desig e, employee_desig f \r\n"
			+ "WHERE a.projectid=b.project_id AND a.isactive = 1  AND a.oicempid=c.emp_id AND a.oicempid1=d.emp_id AND c.desig_id = e.desig_id AND d.desig_id = f.desig_id AND a.projectid=:ProjectId ORDER BY a.MilestoneNo";
	private static final String PROJECTMASTER="SELECT a.project_id, a.project_code, a.project_name, a.project_short_name FROM project_master a WHERE a.is_active='1'";
	private static final String EMPLOYEELISTALL="select a.emp_id,a.emp_name,b.designation,a.title,a.salutation FROM employee a,employee_desig b WHERE a.is_active='1' AND a.desig_id=b.desig_id AND a.emp_status IN ('P','R','T') ORDER BY a.sr_no=0,a.sr_no";
    private static final String MILESTONECOUNT="Select count(*) from milestone_activity where isactive='1' and projectid=:ProjectId";
	private static final String MA="SELECT a.milestoneactivityid,b.project_name,a.startdate,a.enddate,a.activityname,a.milestoneno,\r\n"
			+ "c.emp_name,d.emp_name AS emp,a.oicempid,a.oicempid1,a.projectid,a.progressstatus,a.revisionno,a.acceptedby,\r\n"
			+ "a.accepteddate,a.activitytype,a.Weightage,e.activitytype AS TYPE, c.lab_code AS 'LabCode1', d.lab_code AS 'LabCode2', (SELECT COALESCE(a.ModifiedDate, a.CreatedDate) > MAX(c.CreatedDate) FROM `milestone_activity_rev` c WHERE c.MilestoneActivityId=:id)AS 'IsChange' \r\n"
			+ "FROM milestone_activity a,project_master b, employee c,employee d, milestone_activity_type e WHERE a.activitytype=e.activitytypeid AND\r\n"
			+ "a.projectid=b.project_id AND a.oicempid=c.emp_id AND a.oicempid1=d.emp_id AND a.milestoneactivityid=:id";

	private static final String MILEACTIVITYLEVEL="CALL Pfms_Milestone_Level_List(:id,:levelid)";
    private static final String MAREVISION="SELECT MAX(revisionno) FROM milestone_activity_rev WHERE milestoneactivityid=:id ";
	private static final String MADETAILS="FROM MilestoneActivity WHERE MilestoneActivityId=:Id";
    private static final String MILEACTIVITYDATA="select a.milestoneactivityid,a.startdate,a.enddate,a.activityname,a.activitystatusid,a.progressstatus,a.statusremarks,a.oicempid,a.oicempid1,a.activitytype from milestone_activity a where  a.milestoneactivityid=:id";
    private static final String MILEACTIVITYLEVELDATA="select a.activityid,a.startdate,a.enddate,a.activityname,a.activitystatusid,a.progressstatus,a.statusremarks,a.Weightage from milestone_activity_level a where  a.activityid=:id";
	private static final String MILEACTIVITYTYPE="select activitytypeid,activitytype from milestone_activity_type";
	private static final String MAINUPDATE="UPDATE milestone_activity SET activityname=:name,Weightage=:Weightage,activitytype=:type,oicempid=:empid,oicempid1=:empid1,startdate=:from,enddate=:to,orgstartdate=:orgfrom,orgenddate=:orgto,ModifiedBy=:modifiedby, ModifiedDate=:modifieddate WHERE milestoneactivityid=:id";
	private static final String MILEACTIVITYLEVELEDIT="UPDATE milestone_activity_level SET activityname=:name,startdate=:from,enddate=:to,Weightage=:Weightage ,ModifiedBy=:modifiedby, ModifiedDate=:modifieddate WHERE activityid=:id";
	private static final String ACTIVITYLEVELFULLEDIT="UPDATE milestone_activity_level SET activityname=:name,Weightage=:Weightage,activitytype=:type,oicempid=:empid,oicempid1=:empid1,startdate=:from,enddate=:to,orgstartdate=:orgfrom,orgenddate=:orgto,ModifiedBy=:modifiedby, ModifiedDate=:modifieddate WHERE activityid=:id";
	private static final String MILEACTIVITYUPDATE="UPDATE milestone_activity  SET activityname=:name, startdate=:from,enddate=:to,Weightage=:Weightage,ModifiedBy=:modifiedby, ModifiedDate=:modifieddate WHERE milestoneactivityid=:id";
    private static final String MILELEVELCOMPARE="CALL Pfms_Milestone_Level_Compare(:id,:rev,:rev1,:levelid)";
	private static final String MILECOMPAREMAIN="SELECT a.milestoneactivityid,b.project_name,e.startdate,e.enddate,e.activityname,e.progressstatus as ps,c.emp_name,d.emp_name AS emp,e.revisionno,e.progressstatus as ps1,e.progressstatus as ps2,a.progressstatus as ps3,DATEDIFF(e.enddate,e.startdate) AS actual,(SELECT DATEDIFF(f.enddate,f.startdate) FROM milestone_activity_rev f WHERE  f.milestoneactivityid=:id  AND f.revisionno=:rev1 LIMIT 1) AS diff,a.dateofcompletion,g.activitystatus  FROM milestone_activity a,project_master b, employee c,employee d,milestone_activity_rev e,milestone_activity_status g WHERE a.activitystatusid=g.activitystatusid and a.projectid=b.project_id AND a.oicempid=c.emp_id AND a.oicempid1=d.emp_id AND a.milestoneactivityid=e.milestoneactivityid   AND a.milestoneactivityid=:id AND e.revisionno=:rev";
	private static final String MAEMPLIST="select a.milestoneactivityid,b.project_name,a.startdate,a.enddate,a.activityname,a.milestoneno,c.emp_name,d.emp_name as emp,(SELECT MAX(e.revisionno) FROM milestone_activity_rev e WHERE a.milestoneactivityid=e.milestoneactivityid) AS rev from milestone_activity a,project_master b, employee c,employee d,milestone_activity_rev e where  a.revisionno=e.revisionno  AND a.milestoneactivityid=e.milestoneactivityid and a.projectid=b.project_id and a.oicempid=c.emp_id and a.oicempid1=d.emp_id and (a.oicempid1=:EmpId or a.oicempid=:EmpId)";
	private static final String STATUSLIST="select a.activitystatusid,a.activitystatus FROM milestone_activity_status a ";
	private static final String PROACTIVITYUPDATE="UPDATE milestone_activity SET dateofcompletion=:doc,activitystatusid=:status,progressstatus=:progress,statusremarks=:remarks,ModifiedBy=:modifiedby, ModifiedDate=:modifieddate WHERE milestoneactivityid=:id";
	private static final String PROACTIVITYLEVELUPDATE="UPDATE milestone_activity_level SET dateofcompletion=:doc,activitystatusid=:status,progressstatus=:progress,statusremarks=:remarks,ModifiedBy=:modifiedby, ModifiedDate=:modifieddate WHERE activityid=:id";
	private static final String REPORTSLIST="SELECT a.milestoneactivityid,c.project_name,a.milestoneno,d.activitytype,a.activityname,b.startdate,b.enddate,a.progressstatus,a.projectid FROM milestone_activity a,milestone_activity_rev b,project_master c,milestone_activity_type d  WHERE a.projectid=c.project_id AND a.activitytype=d.activitytypeid AND a.revisionno=b.revisionno  AND a.milestoneactivityid=b.milestoneactivityid AND (a.oicempid=:empid OR a.oicempid1=:empid) ";
    private static final String SUBLIST="SELECT a.activitysubid,a.progress,a.progressdate,a.remarks,a.attachname,\r\n"
    		+ "	(SELECT CONCAT(IFNULL(CONCAT(e.title,' '),IFNULL(CONCAT(e.salutation,' '),'')), e.emp_name) AS 'empname' FROM employee e WHERE e.emp_id =\r\n"
    		+ "	(SELECT l.empid FROM login l WHERE l.username = a.createdby)) AS 'EmpName'\r\n"
    		+ "	 FROM milestone_activity_sub a WHERE a.activityid=:id AND a.isactive = 1";
    private static final String SUBDATA="FROM MilestoneActivitySub WHERE ActivitySubId=:id"; 
	private static final String PROJECTDETAILS="SELECT a.project_id,a.project_code,a.project_name,a.project_short_name FROM project_master a WHERE a.project_id=:projectid";
	private static final String MAASSIGNEELIST="CALL Pfms_Milestone_Oic_List(:ProjectId,:empid)";
	private static final String PROJECTEMPLIST="SELECT a.emp_id, CONCAT(IFNULL(CONCAT(a.title,' '),(IFNULL(CONCAT(a.salutation, ' '), ''))), a.emp_name) AS 'EmpName',b.designation FROM employee a,employee_desig b,project_employee pe  WHERE a.is_active='1' AND pe.isactive='1' AND a.emp_status IN ('P','R','T') AND a.desig_id=b.desig_id  AND pe.empid=a.emp_id AND pe.projectid=:projectid AND a.lab_code=:labcode ORDER BY a.sr_no=0,a.sr_no";
	private static final String PROJECTEMPLISTEDIT="SELECT a.empid, CONCAT(IFNULL(CONCAT(a.Title,' '),(IFNULL(CONCAT(a.Salutation, ' '), ''))), a.EmpName) AS 'EmpName',b.designation,a.srno as srno FROM employee a,employee_desig b,project_employee pe  WHERE a.isactive='1' AND a.emp_status IN ('P','R','T') AND a.DesigId=b.DesigId  AND pe.empid=a.empid AND pe.projectid=:projectid  union SELECT a.empid, CONCAT(IFNULL(CONCAT(a.Title,' '),(IFNULL(CONCAT(a.Salutation, ' '), ''))), a.EmpName) AS 'EmpName',b.designation,a.srno as srno FROM employee a,employee_desig b WHERE a.isactive='1' AND a.DesigId=b.DesigId  AND a.empid=:id ORDER BY srno=0, srno";
	private static final String PROJECTASSINEE="SELECT DISTINCT(a.projectid),a.projectcode,a.projectname FROM project_master a,milestone_activity b WHERE a.projectid=b.projectid and (b.oicempid=:empid or b.oicempid1=:empid) and   a.isactive='1'";
	private static final String ASSIGNUPDATE="UPDATE milestone_activity SET isaccepted='A',ModifiedBy=:modifiedby, ModifiedDate=:modifieddate WHERE milestoneactivityid=:id";
	private static final String ACCEPTUPDATE="UPDATE milestone_activity SET isaccepted='Y',acceptedby=:acceptedby,accepteddate=:accepteddate,ModifiedBy=:modifiedby, ModifiedDate=:modifieddate WHERE milestoneactivityid=:id";
	private static final String SENDBACKUPDATE="UPDATE milestone_activity SET isaccepted='B',statusremarks=:statusremarks,ModifiedBy=:modifiedby, ModifiedDate=:modifieddate WHERE milestoneactivityid=:id";
    private static final String ACTIONLIST="SELECT a.actionmainid,ab.emp_name,dc.designation,a.actiondate,aas.enddate,a.actionitem,aas.actionstatus,aas.actionstatus as 'status',a.createdby,a.createddate,(SELECT MAX(b.actionsubid) FROM action_sub b WHERE b.actionassignid = aas.actionassignid) AS subid,(SELECT c.progress FROM action_sub c  WHERE c.actionassignid = aas.actionassignid AND c.actionsubid = (SELECT MAX(b.actionsubid) FROM action_sub b WHERE b.actionassignid = aas.actionassignid) )  AS progress, (SELECT c.remarks FROM action_sub c  WHERE c.actionassignid = aas.actionassignid AND c.actionsubid = (SELECT MAX(b.actionsubid) FROM action_sub b WHERE b.actionassignid = aas.actionassignid) )  AS remarks,aas.revision FROM action_main a,  employee ab ,employee_desig dc , action_assign aas WHERE aas.actionmainid=a.actionmainid AND aas.assignee=ab.emp_id AND ab.is_active='1' AND dc.desig_id=ab.desig_id AND a.actiontype=:actiontype AND a.activityid=:activityid  ";//AND aas.actionflag<>'Y'
    private static final String MILESUM="SELECT IFNULL(SUM(weightage), 0) AS 'TotalWeightage' FROM milestone_activity WHERE milestoneactivityid<>:id AND projectid=:projectid";
    private static final String ACTIVITYLEVELSUM="SELECT SUM(weightage) FROM milestone_activity_level WHERE parentactivityid=:id AND activityid<>:activityid and activitylevelid=:levelid";
    private static final String BASELINEMAIN="SELECT a.milestoneactivityid AS obid,e.enddate,a.progressstatus,e.Weightage,b.activitystatus,a.activitystatusid,e.DateOfCompletion FROM milestone_activity_rev a,milestone_activity_status b,milestone_activity e  WHERE a.activitystatusid=b.activitystatusid  AND a.milestoneactivityid=e.milestoneactivityid AND a.revisionno=e.revisionno AND  a.milestoneactivityid=:inActivityId";
    private static final String BASELINELEVEL="SELECT a.activityid AS obid,c.enddate,a.progressstatus,c.Weightage,b.activitystatus,a.activitystatusid FROM milestone_activity_level a,milestone_activity_sub_rev c,milestone_activity_status b WHERE a.activitystatusid=b.activitystatusid AND a.revision=c.revision  AND   a.parentactivityid=:inActivityId  AND a.activityid=c.activityid and a.activitylevelid=:levelid";
    private static final String PROGRESSMAIN="UPDATE milestone_activity SET activitystatusid=:status,progressstatus=:progress, DateOfCompletion=:DateOfCompletion,ModifiedDate=:ModifiedDate WHERE milestoneactivityid=:id";
    private static final String PROGRESSLEVEL="UPDATE milestone_activity_level SET activitystatusid=:status,progressstatus=:progress,ModifiedDate=:ModifiedDate WHERE activityid=:id";
    private static final String REVMAINUPDATE="UPDATE milestone_activity SET revisionno=:rev WHERE milestoneactivityid=:id";
    private static final String REVLEVELUPDATE="UPDATE milestone_activity_level SET revision=:rev WHERE activityid=:id";
    private static final String FILEPASS="select password from login where username=:userid";
   
    private static final String FILEREPREV="UPDATE file_rep_new SET ReleaseDoc=:release,VersionDoc=:version where filerepid=:id";
    private static final String FILEDETAILS="SELECT * FROM(SELECT a.filerepid,b.filerepuploadid,b.filepath,b.filenameui,b.filename,b.filepass,b.ReleaseDoc,b.VersionDoc FROM file_rep_new a,file_rep_upload b WHERE a.filerepid=b.filerepid AND b.filerepuploadid=:fileid)AS a JOIN (SELECT MAX(DocAmendmentId) AS 'AmendmentDocId' FROM file_doc_amendment WHERE FileRepUploadId=:fileid ) AS b  ";
    private static final String ALLEMPNAMEDESIGLIST="SELECT e.emp_id , CONCAT(IFNULL(CONCAT(e.title,' '),(IFNULL(CONCAT(e.salutation, ' '), ''))), e.emp_name) AS 'EmpName', ed.designation FROM employee e, employee_desig ed WHERE e.is_active=1 AND e.emp_status IN ('P','R','T') AND e.desig_id=ed.desig_id and e.lab_code=:labcode ORDER BY e.sr_no = 0, e.sr_no";
	
    private static final String MILESTONESCHEDULELIST="SELECT milestonescheduleid,projectid,activityname,milestoneno,orgstartdate,orgenddate,startdate,enddate,statusremarks FROM milestone_schedule WHERE isactive=1 AND projectid=:projectid";
    private static final String MILESTONESCHEDULECOUNT="SELECT COUNT(*) FROM milestone_schedule WHERE isactive='1' AND projectid=:projectid";
    private static final String MILESTONEEXCEL="CALL Pfms_Milestone_Excel(:projectid)";
    private static final String MAINSYSTEM="SELECT a.filerepmasterid, a.parentlevelid, a.levelid,  a.levelname FROM file_rep_master a WHERE a.levelid='1' AND projectid=:projectid";
    private static final String MAINSYSTEMLEVEL="SELECT a.filerepmasterid, a.parentlevelid, a.levelid,  a.levelname FROM file_rep_master a WHERE a.parentlevelid=:parentid ";
	private static final String MILELEVEL="FROM MilestoneActivityLevel WHERE  ParentActivityId=:Id AND ActivityLevelId=:LevelId";
	private static final String DOCUMENTTYPELIST="SELECT fdm.fileuploadmasterid,fdm.parentlevelid,fdm.levelid,fdm.levelname,fdm.docshortname,'1' AS 'added' FROM file_doc_master fdm, file_project_doc fpd WHERE fdm.isactive=1 AND fpd.fileuploadmasterid=fdm.fileuploadmasterid AND fpd.projectid=:projectid AND LabCode=:LabCode UNION SELECT fileuploadmasterid,parentlevelid,levelid,levelname,docshortname,'0' AS 'added' FROM file_doc_master WHERE isactive=1  AND LabCode=:LabCode AND  fileuploadmasterid NOT IN (SELECT fileuploadmasterid FROM file_project_doc WHERE projectid=:projectid  AND LabCode=:LabCode ) ORDER BY 1 ";
	private static final String DOCUMENTTITLELIST="CALL Pfms_FileRepo_Doc (:projectid,:sub,:LabCode)";
	//private static final String VERSIONCHECKLIST="SELECT b.filerepuploadid,b.filenameui,b.ReleaseDoc,b.filerepid, b.versionDoc FROM file_rep_new a,file_rep_upload b WHERE a.projectid=:projectid AND  a.subl1=:subsysteml1 AND a.documentid=:documenttitle  AND a.filerepid=b.filerepid ORDER BY b.filerepuploadid DESC LIMIT 1 "; 
//	private static final String VERSIONCHECKLIST="SELECT b.filerepuploadid,b.filenameui,b.ReleaseDoc,b.filerepid, b.versionDoc FROM file_rep_new a,file_rep_upload b WHERE a.projectid=:projectid AND  a.subl1=:subsysteml1 AND a.documentid=:documenttitle  AND a.filerepid=b.filerepid AND a.IsActive=1 AND b.IsActive=1 ORDER BY b.filerepuploadid DESC LIMIT 1 ";  // prakash Change
	private static final String VERSIONCHECKLIST="SELECT b.filerepuploadid,b.filerepid,b.filenameui,b.ReleaseDoc,b.versionDoc FROM file_rep_new a,file_rep_upload b WHERE a.projectid=:projectId AND a.DocumentName=:docName AND a.filerepid=b.filerepid AND a.IsActive=1 AND b.IsActive=1 ORDER BY b.filerepuploadid DESC LIMIT 1"; 
	private static final String DOCUMENTSTAGELIST="SELECT a.fileuploadmasterid,a.parentlevelid,a.levelid,a.levelname FROM file_doc_master a WHERE a.isactive=1 AND a.levelid=:levelid AND a.parentlevelid=:documenttype";
	private static final String FILEHISTORYLIST="SELECT fru.filerepuploadid,frn.filerepid,fdm.docid,fdm.levelname,fru.versiondoc,fru.releasedoc,CAST(DATE_FORMAT(fru.createddate,'%d-%m-%Y') AS CHAR) AS 'createddate' FROM  file_rep_new frn,file_rep_upload fru,file_doc_master fdm WHERE frn.filerepid=fru.filerepid AND frn.documentid=fdm.fileuploadmasterid AND frn.filerepid=:filerepid ORDER BY fru.versiondoc DESC,fru.releasedoc DESC ";
	private static final String FILEREPMASTERLISTALL ="SELECT a.filerepmasterid,a.parentlevelid,a.levelid,a.levelname,COALESCE(b.project_code, 'General') AS ProjectCode FROM file_rep_master a LEFT JOIN project_master b ON a.projectid = b.project_id WHERE a.filerepmasterid > 0 AND a.projectid =:projectid AND a.LabCode =:LabCode";
	private static final String FILEDOCMASTERLISTALL ="SELECT fileuploadmasterid,parentlevelid,levelid,levelname,docid,docshortname FROM file_doc_master WHERE isactive=1  AND labcode=:LabCode AND fileuploadmasterid IN (SELECT parentlevelid FROM file_doc_master WHERE isactive=1 AND fileuploadmasterid IN (SELECT parentlevelid FROM file_project_doc   WHERE projectid=:projectid AND labcode=:LabCode ) )  UNION  SELECT fileuploadmasterid,parentlevelid,levelid,levelname,docid,docshortname FROM file_doc_master WHERE isactive=1 AND labcode=:LabCode AND fileuploadmasterid IN (SELECT parentlevelid FROM file_project_doc   WHERE projectid=:projectid AND labcode=:LabCode) ";
	private static final String PROJECTDOCUMETSADD ="SELECT DocAmendmentId,FileRepUploadId,FileName,Description,AmendVersion,FilePath,FilePass,Amendmentname FROM file_doc_amendment WHERE FileRepUploadId=:FileRepUploadId ORDER BY AmendVersion DESC";
	private static final String DOCUMENTAMENDMENTDATA ="SELECT DocAmendmentId,FileRepUploadId,FileName,Description,AmendVersion,FilePath,FilePass,Amendmentname FROM file_doc_amendment WHERE DocAmendmentId=:docammendmentid ORDER BY AmendVersion DESC";
	
	private static final String REPMASTERDATA ="SELECT a.filerepmasterid,a.projectid,a.levelname,a.parentLevelId,a.levelId,(SELECT m.levelname FROM file_rep_master m WHERE m.FileRepMasterId = a.parentLevelId) AS 'parentLevelName' FROM file_rep_master a WHERE a.FileRepMasterId=:filerepmasterid";
	private static final String PREPROJECTREPMASTER ="SELECT a.filerepmasterid,a.initiationId,a.levelname,a.parentLevelId,a.levelId,(SELECT m.levelname FROM file_rep_master_preproject m WHERE m.FileRepMasterId = a.parentLevelId) AS 'parentLevelName' FROM file_rep_master_preproject a WHERE a.FileRepMasterId=:filerepmasterid";
	private static final String REPMASTERALLDOCLISTS="CALL Pfms_FileRepo_SubLev_list(:filerepmasterid);";
	private static final String MAINSYSTEM1 ="SELECT a.filerepmasterid, a.parentlevelid, a.levelid,  a.levelname FROM file_rep_master a WHERE a.levelid='1' AND a.filerepmasterid=:filerepmasterid";
    private static final String WEIGHTAGELEVEL="SELECT a.activityid AS obid,a.enddate,a.progressstatus,a.Weightage FROM milestone_activity_level a WHERE    a.parentactivityid=:inActivityId   and a.activitylevelid=:levelid";

	
    @PersistenceContext
	EntityManager manager;
	
	private static final Logger logger=LogManager.getLogger(MilestoneDaoImpl.class);
	
	@Override
	public List<Object[]> MilestoneActivityList(String ProjectId) throws Exception {

		Query query=manager.createNativeQuery(MALIST);
		query.setParameter("ProjectId", ProjectId);
		List<Object[]> MilestoneActivityList=(List<Object[]>)query.getResultList();		

		return MilestoneActivityList;
	}

	@Override
	public List<Object[]> ProjectList() throws Exception {

		Query query=manager.createNativeQuery(PROJECTMASTER);
		
		List<Object[]> ProjectList=(List<Object[]>)query.getResultList();		

		return ProjectList;
	}
	
	@Override
	public List<Object[]> MilestoneActivityAssigneeList(String ProjectId,String EmpId) throws Exception {

		Query query=manager.createNativeQuery(MAASSIGNEELIST);
		query.setParameter("ProjectId", ProjectId);
		query.setParameter("empid", EmpId);
		List<Object[]> MilestoneActivityList=(List<Object[]>)query.getResultList();		

		return MilestoneActivityList;
	}

	@Override
	public List<Object[]> ProjectAssigneeList(String EmpId) throws Exception {

		Query query=manager.createNativeQuery(PROJECTASSINEE);
		query.setParameter("empid", EmpId);
		List<Object[]> ProjectList=(List<Object[]>)query.getResultList();		
		return ProjectList;
	}
	
	@Override
	public List<Object[]> EmployeeList() throws Exception {

		Query query=manager.createNativeQuery(EMPLOYEELISTALL);
		
		List<Object[]> EmployeeList=(List<Object[]>)query.getResultList();		

		return EmployeeList;
	}

	@Override
	public long MilestoneActivity(com.vts.pfms.milestone.model.MilestoneActivity Milestone) throws Exception {
		manager.persist(Milestone);
		manager.flush();
		return Milestone.getMilestoneActivityId();
	}
	
	@Override
	public int MilestoneCount(String ProjectId) throws Exception {
		
		Query query = manager.createNativeQuery(MILESTONECOUNT);
		query.setParameter("ProjectId", ProjectId);
		Long count=(Long)query.getSingleResult();
			return count.intValue();
	}

	@Override
	public List<Object[]> MilestoneActivity(String MilestoneActivityId) throws Exception {

		Query query=manager.createNativeQuery(MA);
		query.setParameter("id", MilestoneActivityId);
		List<Object[]> MilestoneActivityList=(List<Object[]>)query.getResultList();		

		return MilestoneActivityList;
	}

	@Override
	public long MilestoneActivityLevelInsert(MilestoneActivityLevel MileActivityA) throws Exception {
		manager.persist(MileActivityA);
		manager.flush();
		return MileActivityA.getActivityId();
	}

	@Override
	public List<Object[]> MilestoneActivityLevel(String MilestoneActivityId,String LevelId) throws Exception {
		Query query=manager.createNativeQuery(MILEACTIVITYLEVEL);
		query.setParameter("id", MilestoneActivityId);
		query.setParameter("levelid", LevelId);
		List<Object[]> MilestoneActivityList=(List<Object[]>)query.getResultList();		

		return MilestoneActivityList;
	}
	
	@Override
	public int MilestoneRevisionCount(String MileActivityId) throws Exception {
		
		Query query = manager.createNativeQuery(MAREVISION);
		query.setParameter("id", MileActivityId);
		Integer count=(Integer)query.getSingleResult();
			return count;
	}

	@Override
	public MilestoneActivity MileActivityDetails(Long Id) throws Exception {

        Query query=manager.createQuery(MADETAILS);
		query.setParameter("Id",Id);
		MilestoneActivity NoList=(MilestoneActivity)query.getSingleResult();
		return NoList;
	}
	
	
	@Override
	public long PfmsNotificationAdd(PfmsNotification notification) throws Exception {
		manager.persist(notification);
		manager.flush();
        return notification.getNotificationId();
	
	}

	@Override
	public long MilestoneActivityRev(com.vts.pfms.milestone.model.MilestoneActivityRev Milestone) throws Exception {
		manager.persist(Milestone);
		manager.flush();
		return Milestone.getMilestoneActivityRevId();
	}

	@Override
	public long MilestoneActivitySubRev(MilestoneActivitySubRev Milestone)throws Exception {
		manager.persist(Milestone);
		manager.flush();
		return Milestone.getActivityRevId();
	}


	@Override
	public List<Object[]> MilestoneActivityData(String ActivityId) throws Exception {
		Query query=manager.createNativeQuery(MILEACTIVITYDATA);
		query.setParameter("id", ActivityId);
		List<Object[]> MilestoneActivityList=(List<Object[]>)query.getResultList();		

		return MilestoneActivityList;
	}

	@Override
	public List<Object[]> ActivityLevelData(String ActivityId) throws Exception {
		Query query=manager.createNativeQuery(MILEACTIVITYLEVELDATA);
		query.setParameter("id", ActivityId);
		List<Object[]> MilestoneActivityList=(List<Object[]>)query.getResultList();		

		return MilestoneActivityList;
	}


	@Override
	public List<Object[]> ActivityTypeList() throws Exception {
		Query query=manager.createNativeQuery(MILEACTIVITYTYPE);
		List<Object[]> MilestoneActivityList=(List<Object[]>)query.getResultList();		

		return MilestoneActivityList;
	}

	@Override
	public int MilestoneActivityMainUpdate(MileEditDto dto) throws Exception {
		
		Query query=manager.createNativeQuery(MAINUPDATE);
		query.setParameter("id",dto.getMilestoneActivityId());
		query.setParameter("type", dto.getActivityTypeId());
		query.setParameter("empid",dto.getOicEmpId());
		query.setParameter("empid1",dto.getOicEmpId1());
		query.setParameter("name",dto.getActivityName());
		query.setParameter("from",dto.getStartDate());
		query.setParameter("to",dto.getEndDate());
		query.setParameter("orgfrom",dto.getStartDate());
		query.setParameter("orgto",dto.getEndDate());
		query.setParameter("Weightage",dto.getWeightage());
		query.setParameter("modifiedby",dto.getCreatedBy());
		query.setParameter("modifieddate",dto.getCreatedDate());
		int result=query.executeUpdate();
		return result;
	}
	
	@Override
	public int ActivityLevelFullEdit(MileEditDto dto) throws Exception {
		
		Query query=manager.createNativeQuery(ACTIVITYLEVELFULLEDIT);
		query.setParameter("id",dto.getActivityId());
		query.setParameter("type", dto.getActivityTypeId());
		query.setParameter("empid",dto.getOicEmpId());
		query.setParameter("empid1",dto.getOicEmpId1());
		query.setParameter("name",dto.getActivityName());
		query.setParameter("from",dto.getStartDate());
		query.setParameter("to",dto.getEndDate());
		query.setParameter("orgfrom",dto.getStartDate());
		query.setParameter("orgto",dto.getEndDate());
		query.setParameter("Weightage",dto.getWeightage());
		query.setParameter("modifiedby",dto.getCreatedBy());
		query.setParameter("modifieddate",dto.getCreatedDate());
		int result=query.executeUpdate();
		return result;
	}

	@Override
	public int ActivityLevelEditUpdate(MileEditDto dto) throws Exception {
		
		Query query=manager.createNativeQuery(MILEACTIVITYLEVELEDIT);
		query.setParameter("id",dto.getActivityId());
		query.setParameter("name",dto.getActivityName());
		query.setParameter("from",dto.getStartDate());
		query.setParameter("to",dto.getEndDate());
		query.setParameter("Weightage",dto.getWeightage());
		query.setParameter("modifiedby",dto.getCreatedBy());
		query.setParameter("modifieddate",dto.getCreatedDate());
		int result=query.executeUpdate();
		return result;
	}

	
	@Override
	public int MilestoneActivityUpdate(MileEditDto dto) throws Exception {
		
		Query query=manager.createNativeQuery(MILEACTIVITYUPDATE);
		query.setParameter("id",dto.getMilestoneActivityId());
		query.setParameter("from",dto.getStartDate());
		query.setParameter("to",dto.getEndDate());
		query.setParameter("Weightage",dto.getWeightage());
		query.setParameter("modifiedby",dto.getCreatedBy());
		query.setParameter("modifieddate",dto.getCreatedDate());
		query.setParameter("name",dto.getActivityName());
		query.setParameter("name",dto.getActivityName());
		int result=query.executeUpdate();
		return result;
	}

	@Override
	public List<Object[]> ActivityCompareMAin(String ActivityId,String Rev,String Rev1) throws Exception {
		Query query=manager.createNativeQuery(MILECOMPAREMAIN);
		query.setParameter("id", ActivityId);
		query.setParameter("rev", Rev);
		query.setParameter("rev1", Rev1);
		List<Object[]> MilestoneActivityList=(List<Object[]>)query.getResultList();		

		return MilestoneActivityList;
	}

	@Override
	public List<Object[]> ActivityLevelCompare(String ActivityId,String Rev,String Rev1,String LevelId) throws Exception {
		Query query=manager.createNativeQuery(MILELEVELCOMPARE);
		query.setParameter("id", ActivityId);
		query.setParameter("rev", Rev);
		query.setParameter("rev1", Rev1);
		query.setParameter("levelid", LevelId);
		List<Object[]> MilestoneActivityList=(List<Object[]>)query.getResultList();		

		return MilestoneActivityList;
	}



	@Override
	public List<Object[]> MilestoneActivityEmpIdList(String EmpId) throws Exception {
		Query query=manager.createNativeQuery(MAEMPLIST);
		query.setParameter("EmpId", EmpId);
		List<Object[]> MilestoneActivityList=(List<Object[]>)query.getResultList();		

		return MilestoneActivityList;
	
	}
	
	@Override
	public List<Object[]> StatusList() throws Exception {

		Query query=manager.createNativeQuery(STATUSLIST);
		
		List<Object[]> StatusList=(List<Object[]>)query.getResultList();		

		return StatusList;
	}
	
	@Override
	public int ActivityProgressMainUpdate(MileEditDto dto) throws Exception {
		
		Query query=manager.createNativeQuery(PROACTIVITYUPDATE);
		query.setParameter("id",dto.getMilestoneActivityId());
		query.setParameter("doc",dto.getDateOfCompletion());
		query.setParameter("remarks",dto.getStatusRemarks());
		query.setParameter("progress",dto.getProgressStatus());
		query.setParameter("status",dto.getActivityStatusId());
		query.setParameter("modifiedby",dto.getCreatedBy());
		query.setParameter("modifieddate",dto.getCreatedDate());
		int result=query.executeUpdate();
		return result;
	}

	@Override
	public int ActivityProgressUpdateLevel(MileEditDto dto) throws Exception {
		
		Query query=manager.createNativeQuery(PROACTIVITYLEVELUPDATE);
		query.setParameter("id",dto.getActivityId());
		query.setParameter("doc",dto.getDateOfCompletion());
		query.setParameter("remarks",dto.getStatusRemarks());
		query.setParameter("progress",dto.getProgressStatus());
		query.setParameter("status",dto.getActivityStatusId());
		query.setParameter("modifiedby",dto.getCreatedBy());
		query.setParameter("modifieddate",dto.getCreatedDate());
		int result=query.executeUpdate();
		return result;
	}


	@Override
	public List<Object[]> MilestoneReportsList(String ProjectId) throws Exception {

		Query query=manager.createNativeQuery(REPORTSLIST);
		query.setParameter("empid", ProjectId);
		List<Object[]> MilestoneActivityList=(List<Object[]>)query.getResultList();		

		return MilestoneActivityList;
	}

	@Override
	public long MilestoneActivitySubInsert(MilestoneActivitySub sub) throws Exception {
		manager.persist(sub);
		manager.flush();
		return sub.getActivitySubId();
	}

	@Override
	public List<Object[]> MilestoneActivitySub(String ActivityId,String Type) throws Exception {
		Query query=manager.createNativeQuery(SUBLIST);
		query.setParameter("id", ActivityId);
		//query.setParameter("type", ActivityId);
		List<Object[]> MilestoneActivityList=(List<Object[]>)query.getResultList();		

		return MilestoneActivityList;
	}

	@Override
	public com.vts.pfms.milestone.model.MilestoneActivitySub ActivityAttachmentDownload(Long ActivitySubId)
			throws Exception {
        Query query=manager.createQuery(SUBDATA);
		query.setParameter("id",ActivitySubId);
		MilestoneActivitySub NoList=(MilestoneActivitySub)query.getSingleResult();
		return NoList;
	}

	@Override
	public List<Object[]> ProjectDetails(String ProjectId) throws Exception {
		Query query=manager.createNativeQuery(PROJECTDETAILS);
		query.setParameter("projectid",ProjectId);
		List<Object[]> ProjectList=(List<Object[]>)query.getResultList();		

		return ProjectList;
	}

	@Override
	public int MilestoneActivityAssign(MilestoneActivityDto dto) throws Exception {
		Query query=manager.createNativeQuery(ASSIGNUPDATE);
		query.setParameter("id",dto.getActivityId());
		query.setParameter("modifiedby",dto.getCreatedBy());
		query.setParameter("modifieddate",dto.getCreatedDate());
		int result=query.executeUpdate();
		return result;
	}

	@Override
	public int MilestoneActivityAccept(MilestoneActivityDto dto, String dt) throws Exception {
		Query query=manager.createNativeQuery(ACCEPTUPDATE);
		query.setParameter("id",dto.getActivityId());
		query.setParameter("acceptedby",dto.getOicEmpId());
		query.setParameter("accepteddate",dt);
		query.setParameter("modifiedby",dto.getCreatedBy());
		query.setParameter("modifieddate",dto.getCreatedDate());
		int result=query.executeUpdate();
		return result;
	}

	@Override
	public int MilestoneActivityBack(MilestoneActivityDto dto) throws Exception {
		Query query=manager.createNativeQuery(SENDBACKUPDATE);
		query.setParameter("id",dto.getActivityId());
		query.setParameter("statusremarks",dto.getStatusRemarks());
		query.setParameter("modifiedby",dto.getCreatedBy());
		query.setParameter("modifieddate",dto.getCreatedDate());
		int result=query.executeUpdate();
		return result;
	}


	@Override
	public List<Object[]> ActionList(String actiontype,String activityid) throws Exception {
		Query query=manager.createNativeQuery(ACTIONLIST);
		query.setParameter("actiontype",actiontype);
		query.setParameter("activityid",activityid);
		List<Object[]> ActionList=(List<Object[]>)query.getResultList();		

		return ActionList;
	}

	@Override
	public long ActivityTransactionInsert(ActivityTransaction trans) throws Exception {
		manager.persist(trans);
		manager.flush();
		return trans.getActivityTransactionId();
	}
	@Override
	public int ActivityMainSum(String Id, String ActivityId) {
	    try {
	        Query query = manager.createNativeQuery(MILESUM);
	        query.setParameter("projectid", Long.parseLong(ActivityId));
	        query.setParameter("id", Long.parseLong(Id));

	        BigDecimal result = (BigDecimal) query.getSingleResult();
	        return result != null ? result.intValue() : 0;

	    } catch (Exception e) {
	        e.printStackTrace();
	        return 0;
	    }
	}

	@Override
	public int ActivityLevelSum(String Id, String ActivityId,String LevelId) throws Exception {
		try {
	        Query query = manager.createNativeQuery(ACTIVITYLEVELSUM);
	        query.setParameter("activityid", Long.parseLong(ActivityId));
	        query.setParameter("id", Long.parseLong(Id));
	        query.setParameter("levelid", Long.parseLong(LevelId));

	        Object result = query.getSingleResult();
	     
	        if (result == null) return 0;

	        return ((Number) result).intValue();  

	    } catch (Exception e) {
	        e.printStackTrace();
	        return 0;
	    }
	}
	


	@Override
	public List<Object[]> BaseLineMain(String ActivityId) throws Exception {
		Query query=manager.createNativeQuery(BASELINEMAIN);
		query.setParameter("inActivityId", ActivityId);
		List<Object[]> BaseLineC=(List<Object[]>)query.getResultList();		

		return BaseLineC;
	}

	@Override
	public List<Object[]> BaseLineLevel(String ActivityId,String LevelId) throws Exception {
		Query query=manager.createNativeQuery(BASELINELEVEL);
		query.setParameter("inActivityId", ActivityId);
		query.setParameter("levelid", LevelId);
		List<Object[]> BaseLineC=(List<Object[]>)query.getResultList();		

		return BaseLineC;
	}

	@Override
	public List<Object[]> WeightageLevel(String ActivityId,String LevelId) throws Exception {
		Query query=manager.createNativeQuery(WEIGHTAGELEVEL);
		query.setParameter("inActivityId", ActivityId);
		query.setParameter("levelid", LevelId);
		List<Object[]> BaseLineC=(List<Object[]>)query.getResultList();		

		return BaseLineC;
	}

	@Override
	public int ProgressMain(String ActivityId, String Status, int Progress,String DateOfCompletion,MileEditDto dto) throws Exception {
		Query query=manager.createNativeQuery(PROGRESSMAIN);
		query.setParameter("id",ActivityId);
		query.setParameter("status",Status);
		query.setParameter("progress",Progress);
		query.setParameter("DateOfCompletion",DateOfCompletion);
		query.setParameter("ModifiedDate", dto.getCreatedDate());
		int result=query.executeUpdate();
		return result;
	}

	@Override
	public int ProgressLevel(String ActivityId, String Status, int Progress,MileEditDto dto) throws Exception {
		Query query=manager.createNativeQuery(PROGRESSLEVEL);
		
		System.out.println("date of Completion - "+Progress);
		
		query.setParameter("id",ActivityId);
		query.setParameter("status",Status);
		query.setParameter("progress",Progress);
		query.setParameter("ModifiedDate", dto.getCreatedDate());
		
		int result=query.executeUpdate();
		
//		//newly added 
//		System.out.println("id - "+ActivityId);
//		if(Progress==100) {
//			String docUpdate="UPDATE milestone_activity_level SET DateOfCompletion=:DateOfCompletion,ModifiedDate=:ModifiedDate WHERE activityid=:id";
//			Query query1=manager.createNativeQuery(docUpdate);
//			query1.setParameter("ModifiedDate", dto.getCreatedDate());
//			query1.setParameter("id",ActivityId);
//			query1.setParameter("DateOfCompletion",dto.getDateOfCompletion());
//			query1.executeUpdate();
//			System.out.println("progress - "+Progress);
//		}
		return result;
	}



	@Override
	public int RevMainUpdate(String ActivityId, String Rev) throws Exception {
		Query query=manager.createNativeQuery(REVMAINUPDATE);
		query.setParameter("id",ActivityId);
		query.setParameter("rev",Rev);
		int result=query.executeUpdate();
		return result;
	}

	@Override
	public int RevLevelUpdate(String ActivityId, String Rev) throws Exception {
		Query query=manager.createNativeQuery(REVLEVELUPDATE);
		query.setParameter("id",ActivityId);
		query.setParameter("rev",Rev);
		int result=query.executeUpdate();
		return result;
	}

	
	
	@Override
	public int FileRepRevUpdate(String ActivityId, Long release,Long version) throws Exception {
		Query query=manager.createNativeQuery(FILEREPREV);
		query.setParameter("id",ActivityId);
		query.setParameter("release",release);
		query.setParameter("version",version);
		int result=query.executeUpdate();
		return result;
	}

	@Override
	public String FilePass(String Userid) throws Exception {
		Query query=manager.createNativeQuery(FILEPASS);
		query.setParameter("userid", Userid);
		String FilePass=(String)query.getSingleResult();

		return FilePass;
	}
	
	@Override
	public List<Object[]> FileDeatils(String FileId) throws Exception {
		Query query=manager.createNativeQuery(FILEDETAILS);
		query.setParameter("fileid", FileId);
		List<Object[]> FileSub=(List<Object[]>)query.getResultList();		

		return FileSub;
	}
	

	@Override
	public List<Object[]> MilestoneScheduleList(String ProjectId) throws Exception {

		Query query=manager.createNativeQuery(MILESTONESCHEDULELIST);
		query.setParameter("projectid", ProjectId);
		List<Object[]> MilestoneScheduleList=(List<Object[]>)query.getResultList();		

		return MilestoneScheduleList;
	}

	@Override
	public Long MilestoneScheduleInsert(MilestoneSchedule Milestone) throws Exception {

		manager.persist(Milestone);
		manager.flush();
		return Milestone.getMilestoneScheduleId();
	}
	
	@Override
	public int MilestoneScheduleCount(String ProjectId) throws Exception {
		
		Query query = manager.createNativeQuery(MILESTONESCHEDULECOUNT);
		query.setParameter("projectid", Long.parseLong(ProjectId));
		Long count=(Long)query.getSingleResult();
		return count.intValue();
	}

	@Override
	public List<Object[]> MilestoneExcel(String ProjectId) throws Exception {
		Query query=manager.createNativeQuery(MILESTONEEXCEL);
		query.setParameter("projectid", ProjectId);
		List<Object[]> MilestoneExcel=(List<Object[]>)query.getResultList();		

		return MilestoneExcel;
	}

	@Override
	public List<Object[]> MainSystem(String projectid) throws Exception {
		Query query=manager.createNativeQuery(MAINSYSTEM);
		query.setParameter("projectid", projectid);
		List<Object[]> MainSystem=(List<Object[]>)query.getResultList();		

		return MainSystem;
	}

	
	
	@Override
	public long RepMasterInsert(FileRepMaster RepMaster) throws Exception {
		manager.persist(RepMaster);
		manager.flush();
		return RepMaster.getFileRepMasterId();
	}

	@Override
	public List<Object[]> MainSystemLevel(String ParentId) throws Exception {
	
		Query query=manager.createNativeQuery(MAINSYSTEMLEVEL);
		query.setParameter("parentid", ParentId);
		List<Object[]> MainSystem=(List<Object[]>)query.getResultList();
		return MainSystem;
	}
	
	@Override
	public List<Object[]> LoginProjectDetailsList(String empid,String Logintype ,String LabCode)throws Exception
	{
		Query query=manager.createNativeQuery("CALL Pfms_Emp_ProjectList(:empid,:logintype,:labcode);");
		query.setParameter("empid", empid);
		query.setParameter("logintype", Logintype);
		query.setParameter("labcode", LabCode);
		List<Object[]> LoginProjectIdList=(List<Object[]>)query.getResultList();
		return LoginProjectIdList;
	}

	@Override
	public List<Object[]> ProjectEmpList(String projectid ,String Labcode)throws Exception
	{
		Query query=manager.createNativeQuery(PROJECTEMPLIST);
		query.setParameter("projectid", projectid);
		query.setParameter("labcode", Labcode);

		List<Object[]> ProjectEmpList=(List<Object[]>)query.getResultList();
		return ProjectEmpList;
	}
	
	@Override
	public List<Object[]> ProjectEmpListEdit(String projectid,String id)throws Exception
	{
		Query query=manager.createNativeQuery(PROJECTEMPLISTEDIT);
		query.setParameter("projectid", projectid);
		query.setParameter("id", id);
		List<Object[]> ProjectEmpList=(List<Object[]>)query.getResultList();
		return ProjectEmpList;
	}
	
	@Override
	public List<Object[]> AllEmpNameDesigList(String labcode)throws Exception
	{
		Query query=manager.createNativeQuery(ALLEMPNAMEDESIGLIST);
		query.setParameter("labcode", labcode);
		System.out.println(labcode);
		List<Object[]> AllEmpNameDesigList=(List<Object[]>)query.getResultList();
		return AllEmpNameDesigList;
	}

	@Override
	public List<MilestoneActivityLevel> ActivityLevelList(Long Id, Long LevelId) {
        Query query=manager.createQuery(MILELEVEL);
		query.setParameter("Id",Id);
		query.setParameter("LevelId",LevelId);
		List<MilestoneActivityLevel> NoList=(List<MilestoneActivityLevel>)query.getResultList();
		return NoList;
	}
	
	@Override
	public List<Object[]> DocumentTypeList(String projectid,String LabCode)throws Exception
	{
		Query query=manager.createNativeQuery(DOCUMENTTYPELIST);
		query.setParameter("projectid",projectid);
		query.setParameter("LabCode",LabCode);
		List<Object[]> DocumentTypeList=(List<Object[]>)query.getResultList();
		return DocumentTypeList;
	}
	
	@Override
	public List<Object[]> DocumentTitleList(String ProjectId,String Sub,String LabCode) throws Exception 
	{
		Query query=manager.createNativeQuery(DOCUMENTTITLELIST);
		query.setParameter("projectid", ProjectId);
		query.setParameter("sub", Sub);
		query.setParameter("LabCode", LabCode);
		List<Object[]> DocumentStageList=(List<Object[]>)query.getResultList();
		return DocumentStageList;
	}
	

	@Override
	public List<Object[]> DocumentStageList(String documenttype,String levelid) throws Exception {
		
		Query query=manager.createNativeQuery(DOCUMENTSTAGELIST);
		query.setParameter("documenttype", documenttype);
		query.setParameter("levelid", levelid);
	
		List<Object[]> DocumentStageList=(List<Object[]>)query.getResultList();
		return DocumentStageList;
	}
	
	
	@Override
	public long FileSubInsertNew(FileRepNew fileRepo) throws Exception {
		manager.persist(fileRepo);
		manager.flush();
		return fileRepo.getFileRepId();
	}
	
	
	@Override
	public long FileUploadInsertNew(FileRepUploadNew fileRepUplod) throws Exception {
		manager.persist(fileRepUplod);
		manager.flush();
		return fileRepUplod.getFileRepUploadId();
	}

	@Override
	public List<Object[]> VersionCheckList(String ProjectId, String SubsystemId,String documentName) throws Exception {
		
		Query query=manager.createNativeQuery(VERSIONCHECKLIST);
		query.setParameter("projectId", ProjectId);
//		query.setParameter("subsysteml1", SubsystemId);
		query.setParameter("docName", documentName);
		List<Object[]> VersionCheckList=(List<Object[]>)query.getResultList();
		return VersionCheckList;
	}
	
	
	@Override
	public List<Object[]> FileHistoryList(String filerepid) throws Exception 
	{		
		Query query=manager.createNativeQuery(FILEHISTORYLIST);
		query.setParameter("filerepid", filerepid);		
		List<Object[]> FileHistoryList=(List<Object[]>)query.getResultList();
		return FileHistoryList;
	}

	
	
	@Override
	public List<Object[]> FileRepMasterListAll(String projectid,String LabCode )throws Exception
	{
		Query query=manager.createNativeQuery(FILEREPMASTERLISTALL);
		query.setParameter("projectid", projectid);
		query.setParameter("LabCode", LabCode);
		List<Object[]> FileRepMasterListAll=(List<Object[]>)query.getResultList();
		return FileRepMasterListAll;
	}
	
	
	
	@Override
	public List<Object[]> FileDocMasterListAll(String projectid,String LabCode)throws Exception
	{
		Query query=manager.createNativeQuery(FILEDOCMASTERLISTALL);
		query.setParameter("projectid", projectid);
		query.setParameter("LabCode", LabCode);
		List<Object[]> FileDocMasterListAll=(List<Object[]>)query.getResultList();
		return FileDocMasterListAll;
	}
	
	@Override
	public long ProjectDocumetsAdd(FileProjectDoc model) throws Exception {
		manager.persist(model);
		manager.flush();
		return model.getProjectDocId();
	}
	
	
	
	@Override
	public List<Object[]> DocumentAmendment(String FileRepUploadId) throws Exception {
		Query query=manager.createNativeQuery(PROJECTDOCUMETSADD);
		query.setParameter("FileRepUploadId", FileRepUploadId);
		List<Object[]> FileDocMasterListAll=(List<Object[]>)query.getResultList();
			return FileDocMasterListAll;
	}
	
	@Override
	public long DocumetAmmendAdd(FileDocAmendment model) throws Exception {
		manager.persist(model);
		manager.flush();
		return model.getDocAmendmentId();
	}
	
	
	@Override
	public Object[] DocumentAmendmentData(String docammendmentid) throws Exception {
		Query query=manager.createNativeQuery(DOCUMENTAMENDMENTDATA);
		query.setParameter("docammendmentid", docammendmentid);
		List<Object[]> DocumentAmendmentData=(List<Object[]>)query.getResultList();
		if(DocumentAmendmentData.size()>0) {
			return DocumentAmendmentData.get(0);
		}else
		{
			return null;
		}
	}
	
	@Override
	public Object[] RepMasterData(String filerepmasterid) throws Exception {
		Query query=manager.createNativeQuery(REPMASTERDATA);
		query.setParameter("filerepmasterid", filerepmasterid);
		List<Object[]> RepMasterData=(List<Object[]>)query.getResultList();
		if(RepMasterData.size()>0) {
			return RepMasterData.get(0);
		}else
		{
			return null;
		}
	}
	
	@Override
	public List<Object[]> RepMasterAllDocLists(String filerepmasterid) throws Exception {
		Query query=manager.createNativeQuery(REPMASTERALLDOCLISTS);
		query.setParameter("filerepmasterid", filerepmasterid);
		List<Object[]> RepMasterAllDocLists=(List<Object[]>)query.getResultList();
		return RepMasterAllDocLists;
	}
	
	
	
 	@Override
	public List<Object[]> MainSystem1(String filerepmasterid) throws Exception
 	{
		Query query=manager.createNativeQuery(MAINSYSTEM1);
		query.setParameter("filerepmasterid", filerepmasterid);
		List<Object[]> MainSystem=(List<Object[]>)query.getResultList();		

		return MainSystem;
	}
	
	@Override
	public int fileRepMasterEditSubmit(String filerepmasterid,String levelname) throws Exception
 	{
		FileRepMaster repmaster=manager.find(FileRepMaster.class,Long.parseLong(filerepmasterid));	
		repmaster.setLevelName(levelname);
		repmaster=manager.merge(repmaster);
		if(repmaster!=null) {
			return 1;
		}else
		{
			return 0;
		}
	}
	
	private static final String FILEDOCMASTERLIST ="FROM FileDocMaster WHERE IsActive= 1 AND LabCode = :LabCode ";
	@Override
	public List<FileDocMaster> fileDocMasterList(String LabCode) throws Exception
 	{
		Query query = manager.createQuery(FILEDOCMASTERLIST);
		query.setParameter("LabCode", LabCode);
		return (List<FileDocMaster>)query.getResultList();
		
	}
	
	@Override
	public long FileDocMasterAdd(FileDocMaster model) throws Exception
	{
		manager.persist(model);
		manager.flush();
		return model.getFileUploadMasterId();
	}
	
	private static final String FILELEVELSUBLEVELNAMECHECK = "FROM FileDocMaster where IsActive=1 AND (LevelId=1 OR LevelId=2) AND LevelName=:LevelName AND LabCode=:LabCode";
	@Override
	public List<FileDocMaster> FileLevelSublevelNameCheck(String levelname,String LabCode) throws Exception
 	{
//		CriteriaBuilder cb = manager.getCriteriaBuilder();
//	    CriteriaQuery<FileDocMaster> cq = cb.createQuery(FileDocMaster.class);
//	    Root<FileDocMaster> rootEntry = cq.from(FileDocMaster.class);
//	    Predicate p1=cb.equal(rootEntry.get("LevelId"), 1);
//	    Predicate p2=cb.equal(rootEntry.get("LevelId"), 2);
//	    Predicate p3=cb.equal(rootEntry.get("LabCode"), LabCode);
//	    CriteriaQuery<FileDocMaster> all = cq.select(rootEntry).where(cb.equal(rootEntry.get("LevelName"), levelname),cb.or(p1,p2),p3);
//	    TypedQuery<FileDocMaster> allQuery = manager.createQuery(all);
//	    return allQuery.getResultList();
		
		Query query = manager.createQuery(FILELEVELSUBLEVELNAMECHECK);
		query.setParameter("LevelName", levelname);
		query.setParameter("LabCode", LabCode);
		return (List<FileDocMaster>) query.getResultList();
		
	}
	
	private static final String FILENAMECHECK = "SELECT (SELECT COUNT(levelname)  FROM file_doc_master WHERE levelname=:levelname AND levelid=3 AND parentlevelid =:parentlevelid  AND LabCode=:LabCode ) AS 'levelname' ,  (SELECT COUNT(DocShortName) FROM file_doc_master WHERE DocShortName=:shortname AND levelid=3 AND LabCode=:LabCode )AS 'DocShortName' , (SELECT COUNT(DocId)  FROM file_doc_master WHERE DocId=:docid AND levelid=3 AND LabCode=:LabCode   ) AS 'DocId' FROM DUAL";
	@Override
	public Object[] fileNameCheck(String levelname, String shortname, String docid, String parentlevelid,String LabCode ) throws Exception
 	{
		Query query=manager.createNativeQuery(FILENAMECHECK);
		query.setParameter("levelname", levelname);
		query.setParameter("shortname", shortname);
		query.setParameter("docid", docid);
		query.setParameter("parentlevelid", parentlevelid);
		query.setParameter("LabCode", LabCode);
		List<Object[]> MainSystem=(List<Object[]>)query.getResultList();		
		if(MainSystem.size()>0) {
			return MainSystem.get(0);
		}else
		{
			return null;
		}
	}
	

	private static final String MILESTONEACTIVITYLISTNEW = "SELECT a.milestoneactivityid AS obid,0 AS 'parentactivityid',a.startdate,a.enddate,a.activityname,a.progressstatus,a.Weightage,a.dateofcompletion, b.activitystatus,a.activitystatusid,a.revisionno AS 'rev' ,d.activitytypeid, d.activitytype ,a.oicempid,e.emp_name,a.oicempid1,0 AS 'activitylevelid' FROM milestone_activity a,milestone_activity_status b, milestone_activity_type d ,employee e WHERE a.activitystatusid=b.activitystatusid AND a.isactive = 1 AND a.activitytype=d.activitytypeid AND a.oicempid=e.emp_id AND a.projectid=:projectid";
	@Override
	public List<Object[]> MilestoneActivityListNew(String ProjectId) throws Exception 
	{
		Query query=manager.createNativeQuery(MILESTONEACTIVITYLISTNEW);
		query.setParameter("projectid", ProjectId);
		List<Object[]> MilestoneActivityList=(List<Object[]>)query.getResultList();		

		return MilestoneActivityList;
	}
	
	
	private static final String MILESTONEACTIVITYLEVELEXCEL = "SELECT a.activityid AS obid,a.parentactivityid,a.startdate,a.enddate,a.activityname,a.progressstatus,a.Weightage,a.dateofcompletion, b.activitystatus,a.activitystatusid,a.revision AS rev,d.activitytypeid, d.activitytype,a.oicempid,e.emp_name,a.oicempid1 ,a.activitylevelid FROM milestone_activity_level a,milestone_activity_status b, milestone_activity_type d ,employee e WHERE a.activitystatusid=b.activitystatusid AND a.activitytype=d.activitytypeid AND a.oicempid=e.emp_id AND   a.parentactivityid=:MilestoneActivityId AND a.activitylevelid=:levelid ";
	@Override
	public List<Object[]> MilestoneActivityLevelExcel(String MilestoneActivityId,String LevelId) throws Exception {
		Query query=manager.createNativeQuery(MILESTONEACTIVITYLEVELEXCEL);
		query.setParameter("MilestoneActivityId", MilestoneActivityId);
		query.setParameter("levelid", LevelId);
		List<Object[]> MilestoneActivityList=(List<Object[]>)query.getResultList();		

		return MilestoneActivityList;
	}
	
	private static final String MILEREMARKUPDATE="update milestone_activity set FinancialOutlay=:Financial , statusremarks=:remarks , modifiedby=:userid,modifieddate=sysdate() where milestoneactivityid=:id";

	@Override
	public int MilestoneRemarkUpdate(MilestoneActivityDto dto) throws Exception {
		Query query=manager.createNativeQuery(MILEREMARKUPDATE);
		query.setParameter("id",dto.getActivityId());
		query.setParameter("remarks",dto.getStatusRemarks());
		query.setParameter("Financial",dto.getFinancaOutlay());
		query.setParameter("userid",dto.getCreatedBy());
		int result=query.executeUpdate();
		return result;
	}
	
	@Override
	public long MileActivityDetailsUpdtae(MilestoneActivity mainmile) throws Exception {

		manager.merge(mainmile);
		manager.flush();
		return mainmile.getMilestoneActivityId();
	}
	//prakarsh--------------------------------------------------------------------
		private static final String isActive="DELETE FROM file_project_doc WHERE Projectid=:Projectid  AND FileUploadMasterId=:FileUploadMasterId  AND ParentLevelid=:ParentLevelid";
		@Override
		public void isActive(String project, int fileUploadMasterId, int parentLevelid) {
			// TODO Auto-generated method stub
			Query query=manager.createNativeQuery(isActive);
		
			query.setParameter("Projectid", project);
			query.setParameter("FileUploadMasterId", fileUploadMasterId);
			query.setParameter("ParentLevelid", parentLevelid);
			query.executeUpdate();
		}
		private static final String FileRepUploadId="SELECT FileRepUploadId FROM file_rep_upload WHERE FileRepId IN (SELECT FileRepId FROM file_rep_new WHERE FileRepMasterId IN (SELECT FileRepMasterId FROM file_rep_master WHERE ProjectId=:project AND IsActive=1)  AND DocumentId=:documentID AND IsActive=1) AND IsActive=1 ";
	    @Override
		public List<Object[]> FileRepUploadId(String project, int documentID) {
			
			Query query=manager.createNativeQuery(FileRepUploadId);
			query.setParameter("project", project);
			query.setParameter("documentID", documentID);
			List<Object[]>FileRepUploadId=query.getResultList();

			return FileRepUploadId;	
		}
		private static final String IsFileInActive="UPDATE file_rep_upload JOIN file_rep_new ON file_rep_upload.FileRepId = file_rep_new.FileRepId SET file_rep_upload.IsActive = 0,file_rep_new.IsActive = 0 WHERE file_rep_new.FileRepMasterId IN (SELECT FileRepMasterId FROM file_rep_master   WHERE ProjectId =:project AND IsActive = 1)AND file_rep_new.DocumentId = :documentID AND file_rep_new.IsActive=1 ";
				
		@Override
		public int IsFileInActive(String project, int documentID) {
			Query query=manager.createNativeQuery(IsFileInActive);
			
			query.setParameter("project", project);
			query.setParameter("documentID", documentID);
		  int count=query.executeUpdate();
		  return count;
		}
		private static final String DocumentListNameEdit="UPDATE file_doc_master SET LevelName =:levelname WHERE FileUploadMasterId =:filerepmasterid";
		@Override
		public int DocumentListNameEdit(String filerepmasterid, String levelname) {
			Query query=manager.createNativeQuery(DocumentListNameEdit);
			query.setParameter("filerepmasterid", filerepmasterid);
			query.setParameter("levelname", levelname);
		  int count=query.executeUpdate();
			return count;
		}
		
//		private static final String MSPROJECTLIST= "SELECT a.MilestoneId,a.SampleTableId,a.ProjectUID,a.ProjectId,e.EmpName,d.designation,a.TaskUID,a.TaskParentUID,a.TaskOutlineLevel,a.TaskOutlineNumber,\r\n"
//				+ "a.TaskName,a.StartDate,a.FinishDate,a.ActualStartDate,a.ActualFinishDate,a.TaskProgress,a.IsCritical FROM pfms_milestone_msprojectdata a,\r\n"
//				+ "employee e , employee_desig d WHERE a.projectid=:projectid AND e.empno = a.empno AND e.desigid = d.desigid ORDER BY \r\n"
//				+ "    CAST(SUBSTRING_INDEX(TaskOutlineNumber, '.', 1) AS UNSIGNED),\r\n"
//				+ "    CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(TaskOutlineNumber, '.', 2), '.', -1) AS UNSIGNED),\r\n"
//				+ "    CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(TaskOutlineNumber, '.', 3), '.', -1) AS UNSIGNED),\r\n"
//				+ "    CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(TaskOutlineNumber, '.', 4), '.', -1) AS UNSIGNED),\r\n"
//				+ "    CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(TaskOutlineNumber, '.', 5), '.', -1) AS UNSIGNED),\r\n"
//				+ "    CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(TaskOutlineNumber, '.', 6), '.', -1) AS UNSIGNED),\r\n"
//				+ "    CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(TaskOutlineNumber, '.', 7), '.', -1) AS UNSIGNED)";
		
		
		private static final String MSPROJECTLIST= "SELECT \r\n"
				+ "    a.MilestoneId,\r\n"
				+ "    a.SampleTableId,\r\n"
				+ "    a.ProjectUID,\r\n"
				+ "    a.ProjectId,\r\n"
				+ "    ANY_VALUE(e.EmpName) AS EmpName,\r\n"
				+ "    ANY_VALUE(d.Designation) AS Designation,\r\n"
				+ "    a.TaskUID,\r\n"
				+ "    a.TaskParentUID,\r\n"
				+ "    a.TaskOutlineLevel,\r\n"
				+ "    a.TaskOutlineNumber,\r\n"
				+ "    a.TaskName,\r\n"
				+ "    a.StartDate,\r\n"
				+ "    a.FinishDate,\r\n"
				+ "    a.ActualStartDate,\r\n"
				+ "    a.ActualFinishDate,\r\n"
				+ "    a.TaskProgress,\r\n"
				+ "    a.TaskIsCritical,\r\n"
				+ "    a.TaskIsMilestone,\r\n"
				+ "    a.TaskIsSummary, \r\n"
				+ "    a.DemandNo, \r\n"
				+ "    a.TaskIsProcurement, \r\n"
				+ "    a.PftsStatusId, \r\n"
				+ "    a.OrgStartDate, \r\n"
				+ "    a.OrgFinishDate, \r\n"
				+ "    a.Revision \r\n"
				+ "FROM pfms_milestone_msprojectdata a\r\n"
				+ "LEFT JOIN  employee e ON e.empno = a.empno\r\n"
				+ "LEFT JOIN  employee_desig d ON e.desigid = d.desigid\r\n"
				+ "WHERE  a.projectid = :projectid\r\n"
				+ "GROUP BY a.MilestoneId\r\n"
				+ "ORDER BY \r\n"
				+ "    CAST(SUBSTRING_INDEX(TaskOutlineNumber, '.', 1) AS UNSIGNED),\r\n"
				+ "    CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(TaskOutlineNumber, '.', 2), '.', -1) AS UNSIGNED),\r\n"
				+ "    CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(TaskOutlineNumber, '.', 3), '.', -1) AS UNSIGNED),\r\n"
				+ "    CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(TaskOutlineNumber, '.', 4), '.', -1) AS UNSIGNED),\r\n"
				+ "    CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(TaskOutlineNumber, '.', 5), '.', -1) AS UNSIGNED),\r\n"
				+ "    CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(TaskOutlineNumber, '.', 6), '.', -1) AS UNSIGNED),\r\n"
				+ "    CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(TaskOutlineNumber, '.', 7), '.', -1) AS UNSIGNED)\r\n"
				+ "";
		
		@Override
		public List<Object[]> getMsprojectTaskList(String ProjectId) throws Exception {
			try {
				System.out.println("ProjectId  "+ProjectId);
				Query query = manager.createNativeQuery(MSPROJECTLIST);
				query.setParameter("projectid", Long.parseLong(ProjectId));
				List<Object[]>mstaskList=query.getResultList();
				return mstaskList;
			}catch (Exception e) {
				// TODO: handle exception
				return null;
			}
		}
		
		private static final String GETATTACHMENTID="SELECT a.TechDataId,a.AttachmentId,(SELECT VersionDoc FROM file_rep_upload WHERE FileRepUploadId=a.AttachmentId)AS 'version',(SELECT ReleaseDoc FROM file_rep_upload WHERE FileRepUploadId=a.AttachmentId)AS 'release' FROM project_technical_work_data a WHERE a.ProjectId=:projectid AND a.IsActive='1'";
		@Override
		public List<Object[]> getAttachmentId(String projectid) throws Exception {
			Query query = manager.createNativeQuery(GETATTACHMENTID);
			query.setParameter("projectid", projectid);
			return (List<Object[]>) query.getResultList();
		}
		
		//private static final String SUBMITCHECKFILE="UPDATE project_technical_work_data SET AttachmentId=:AttachmentId,ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate,IsActive=:IsActive WHERE TechDataId=:TechDataId";
		@Override
		public ProjectTechnicalWorkData getProjectTechnicalWorkDataById(long techDataId) throws Exception {
			try {
				return manager.find(ProjectTechnicalWorkData.class, techDataId);
			}catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		private static final String GETFILEREPCHECKDATA="SELECT FileRepId,ProjectId,FileRepMasterId,SubL1,VersionDoc,ReleaseDoc FROM file_rep_new WHERE ProjectId=:projectId AND FileRepMasterId=:fileRepMasterId AND SubL1=:subL1 AND DocumentName=:docName AND IsActive='1'";
		@Override
		public List<Object[]> getFileRepData(String projectId, String fileRepMasterId, String subL1, String docName)
				throws Exception {
			Query query = manager.createNativeQuery(GETFILEREPCHECKDATA);
			query.setParameter("projectId", projectId);
			query.setParameter("fileRepMasterId", fileRepMasterId);
			query.setParameter("subL1", subL1);
			query.setParameter("docName", docName);
			return (List<Object[]>)query.getResultList();
		}
		
		private static final String FILEREPUPDATEDATA="UPDATE file_rep_new SET VersionDoc=:VersionDoc,ReleaseDoc=:ReleaseDoc,CreatedBy=:CreatedBy,CreatedDate=:CreatedDate WHERE FileRepId=:FileRepId";
		@Override
		public long FileRepUpdate(FileRepNew rep) throws Exception {
			Query query = manager.createNativeQuery(FILEREPUPDATEDATA);
			query.setParameter("FileRepId", rep.getFileRepId());
			query.setParameter("VersionDoc", rep.getVersionDoc());
			query.setParameter("ReleaseDoc", rep.getReleaseDoc());
			query.setParameter("CreatedBy", rep.getCreatedBy());
			query.setParameter("CreatedDate", rep.getCreatedDate());
			return query.executeUpdate();
		}
		
		private static final String MSPROJECTPROCUREMENTSTATUSLIST= "SELECT ANY_VALUE(a.MilestoneId) AS MilestoneId, a.DemandNo, MAX(CASE WHEN a.TaskProgress = 100 AND a.PftsStatusId BETWEEN 0 AND 25 THEN a.PftsStatusId ELSE NULL END) AS MaxPftsStatusId, (SELECT b.TaskName FROM pfms_milestone_msprojectdata b WHERE a.DemandNo=b.DemandNo AND b.TaskOutLineLevel = 3 LIMIT 1) AS MainTaskName FROM pfms_milestone_msprojectdata a  WHERE a.TaskIsMilestone=1 AND a.TaskIsProcurement=1 AND a.ProjectId =:ProjectId GROUP BY a.DemandNo ORDER BY a.DemandNo";
		@Override
		public List<Object[]> getMsprojectProcurementStatusList(String projectId) throws Exception {
			try {
				Query query = manager.createNativeQuery(MSPROJECTPROCUREMENTSTATUSLIST);
				query.setParameter("ProjectId", projectId);
				return (List<Object[]>)query.getResultList();
			}catch (Exception e) {
				e.printStackTrace();
				return new ArrayList<>();
			}
		}
		
		@Override
		public MilestoneActivityLevel getActivityLevelListById(String activitiId) {
	        try {
	        	return manager.find(MilestoneActivityLevel.class, Long.parseLong(activitiId));
	        }catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		public int mileStoneSerialNoUpdate(String newslno, String milestoneActivityId) {
			try {
				Query query = manager.createNativeQuery("UPDATE milestone_activity SET MilestoneNo=:MilestoneNo WHERE MilestoneActivityId=:MilestoneActivityId");
				query.setParameter("MilestoneActivityId", milestoneActivityId);
				query.setParameter("MilestoneNo", newslno);
				
				return query.executeUpdate();
			}catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		}
		
		private static final String ALLMILESTONEACTIVITYLIST = "SELECT a.MilestoneActivityId, a.ProjectId, a.MilestoneNo, a.Activityname, a.OrgStartDate, a.orgEndDate, a.StartDate, a.EndDate, a.ProgressStatus, a.RevisionNo, a.OicEmpId, a.OicEmpId1, a.ActivityStatusId, a.Weightage, b.project_code, b.project_short_name, b.project_name, a.Loading, a.StatusRemarks, a.DateOfCompletion FROM milestone_activity a LEFT JOIN project_master b ON a.ProjectId=b.project_id WHERE a.IsActive=1 ORDER BY a.ProjectId, a.MilestoneActivityId";
		@Override
		public List<Object[]> getAllMilestoneActivityList() throws Exception {
			try {
				Query query = manager.createNativeQuery(ALLMILESTONEACTIVITYLIST);
				return (List<Object[]>)query.getResultList();
			}catch (Exception e) {
				e.printStackTrace();
				return new ArrayList<>();
			}
		}
		
		private static final String ALLMILESTONEACTIVITYLEVELLIST = "SELECT a.ActivityId, a.ParentActivityId, a.ActivityLevelId, a.Activityname, a.OrgStartDate, a.orgEndDate, a.StartDate, a.EndDate, a.ProgressStatus, a.Revision, a.OicEmpId, a.OicEmpId1, a.ActivityStatusId, a.Weightage, a.Loading, a.StatusRemarks, a.IsMasterData, a.DateOfCompletion FROM milestone_activity_level a WHERE a.IsActive=1";
//		private static final String ALLMILESTONEACTIVITYLEVELLIST = "SELECT a.ActivityId AS obid, a.ParentActivityId, a.ActivityLevelId, a.Activityname, a.OrgStartDate, a.orgEndDate, a.StartDate, a.EndDate, a.ProgressStatus, a.Revision, a.OicEmpId, a.OicEmpId1, a.ActivityStatusId, a.Weightage, a.Loading, a.StatusRemarks FROM milestone_activity_level a WHERE a.IsActive=1 AND a.Revision=0\r\n"
//				+ "UNION\r\n"
//				+ "SELECT a.ActivityId AS obid, a.ParentActivityId, a.ActivityLevelId, a.Activityname, a.OrgStartDate, a.orgEndDate, a.StartDate, a.EndDate, a.ProgressStatus, a.Revision, a.OicEmpId, a.OicEmpId1, a.ActivityStatusId, a.Weightage, a.Loading, a.StatusRemarks FROM milestone_activity_level a, milestone_activity_sub_rev b WHERE a.IsActive=1 AND a.Revision=b.Revision AND a.ActivityId=b.ActivityId \r\n"
//				+ "ORDER BY obid ASC";
		@Override
		public List<Object[]> getAllMilestoneActivityLevelList() throws Exception {
			try {
				Query query = manager.createNativeQuery(ALLMILESTONEACTIVITYLEVELLIST);
				return (List<Object[]>)query.getResultList();
			}catch (Exception e) {
				e.printStackTrace();
				return new ArrayList<>();
			}
		}
		
		private static final String GETOLDDOCS="SELECT a.FileRepId,a.ProjectId,a.FileRepMasterId,a.SubL1,a.VersionDoc,a.ReleaseDoc,a.DocumentName, (SELECT b.FileRepUploadId FROM file_rep_upload b WHERE a.FileRepId = b.FileRepId ORDER BY b.FileRepUploadId DESC LIMIT 1) AS 'FileRepUploadId' FROM file_rep_new a WHERE a.ProjectId=:projectId AND a.IsActive='1' AND ((:fileType = 'mainLevel' AND a.FileRepMasterId = :fileId AND a.SubL1 = '0') OR (:fileType = 'subLevel' AND a.SubL1 = :fileId))";
		@Override
		public List<Object[]> getOldFileDocNames(String projectId,String fileType,String fileId) throws Exception {
			try {
				Query query = manager.createNativeQuery(GETOLDDOCS);
				query.setParameter("projectId", projectId);
				query.setParameter("fileType", fileType);
				query.setParameter("fileId", fileId);
				return (List<Object[]>)query.getResultList();
			}catch (Exception e) {
				e.printStackTrace();
				return new ArrayList<>();
			}
		}
		
		@Override
		public FileRepNew getFileRepById(long fileRepId) throws Exception {
			return manager.find(FileRepNew.class, fileRepId);
		}
		
		private static final String ALLFILEREPDOCSLIST="\r\n"
				+ "SELECT a.FileRepUploadId,a.FileRepId,a.FileNameUi,a.FileName,a.VersionDoc,a.ReleaseDoc,a.CreatedDate,\r\n"
				+ "CONCAT(IFNULL(CONCAT(e.title, ' '), ''), e.empname) AS empname,ed.designation\r\n"
				+ "FROM file_rep_upload a\r\n"
				+ "LEFT JOIN login c ON c.UserName = a.CreatedBy AND c.IsActive = 1\r\n"
				+ "LEFT JOIN employee e ON e.empId = c.EmpId AND e.isactive = 1\r\n"
				+ "LEFT JOIN employee_desig ed ON e.desigid = ed.desigid\r\n"
				+ "WHERE a.IsActive = 1 AND a.FileRepId = :fileRepId\r\n"
				+ "ORDER BY a.FileRepUploadId DESC";
		@Override
		public List<Object[]> FileRepDocsList(String fileRepId) throws Exception {
			try {
				Query query = manager.createNativeQuery(ALLFILEREPDOCSLIST);
				query.setParameter("fileRepId", fileRepId);
				return (List<Object[]>)query.getResultList();
			}catch (Exception e) {
				e.printStackTrace();
				return new ArrayList<>();
			}
		}
		
		@Override
		public Optional<FileRepUploadNew> getFileById(Long id) throws Exception {
		    try {
		    	FileRepUploadNew fileRepUploadNew = manager.find(FileRepUploadNew.class, id);
		    	return Optional.ofNullable(fileRepUploadNew);
			} catch (Exception e) {
				return Optional.empty();
			}
		}
		
		private static final String GETFILEREPNAME="SELECT COUNT(a.levelname) AS levelname\r\n"
				+ "FROM file_rep_master a "
				+ "WHERE a.ProjectId = :projectId AND a.IsActive = '1' "
				+ "  AND ("
				+ "    (:fileType = 'mainlevel' AND a.parentLevelId = :fileId AND a.levelId = '1' AND a.levelname = :fileName) "
				+ "    OR "
				+ "    (:fileType = 'sublevel' AND a.parentLevelId = :fileId AND a.levelId = '2' AND a.levelname = :fileName) "
				+ "  ); ";
		@Override
		public int getFileRepMasterNames(String projectId, String fileType, String fileId, String fileName)
				throws Exception {
			try {
				Query query = manager.createNativeQuery(GETFILEREPNAME);
				query.setParameter("projectId", projectId);
				query.setParameter("fileType", fileType);
				query.setParameter("fileId", fileId);
				query.setParameter("fileName", fileName);
			    Object result = query.getSingleResult();
			        if (result != null) {
			            Number count = (Number) result;
			            return count.intValue();
			        } else {
			            return 0;
			        }
				
			}catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		}
		
		
		@Override
		public Optional<FileRepMaster> getFileRepMasterById(long filerepmasterId) throws Exception {
			 try {
				    FileRepMaster fileRepMaster = manager.find(FileRepMaster.class, filerepmasterId);
			    	return Optional.ofNullable(fileRepMaster);
				} catch (Exception e) {
					return Optional.empty();
				}
		}
		
		
		private static final String GETFILENAME ="SELECT c.filerepuploadId, c.filerepId, c.filenameUi, c.fileName, c.filePath " +
			    "FROM file_rep_new b JOIN file_rep_upload c ON b.filerepId = c.filerepId " +
			    "WHERE b.projectId = :projectId AND b.isActive = '1' AND c.isActive = '1' AND " +
			    "(" +
			    "  (:levelType = 'mainlevel' AND b.filerepmasterId = :mainRepMasterId AND c.filePath LIKE :prefix) " +
			    "  OR " +
			    "  (:levelType = 'sublevel' AND b.filerepmasterId = :mainRepMasterId AND b.subL1 = :subRepMasterId AND c.filePath LIKE :prefix) " +
			    ")";
		@Override
		 public List<Object[]> findByFilePathStartingWith(String levelType, Long projectId, Long mainRepMasterId, Long subRepMasterId, String oldName) {
				try {
					Query query = manager.createNativeQuery(GETFILENAME);
					query.setParameter("prefix", "%" + oldName + "%");
					query.setParameter("levelType", levelType);
					query.setParameter("projectId", projectId);
					query.setParameter("mainRepMasterId", mainRepMasterId);
					query.setParameter("subRepMasterId", subRepMasterId);
					return (List<Object[]>)query.getResultList();
				} catch (Exception e) {
					e.printStackTrace();
					return Collections.emptyList();
				}
		    }
		 
		
		private static final String UPDATEFILEUPLOAD="UPDATE file_rep_upload SET filePath=:filePath WHERE filerepuploadId=:filerepuploadId";
		@Override
		public void updateFileRepUploadById(Long uploadId, String updatedPath) throws Exception {
              	try {
              		Query query = manager.createNativeQuery(UPDATEFILEUPLOAD);
					query.setParameter("filerepuploadId", uploadId);
					query.setParameter("filePath", updatedPath);
					query.executeUpdate();
				} catch (Exception e) {
					e.printStackTrace();
				}		
		}
		
		@Override
		public MilestoneActivityLevel getMilestoneActivityLevelById(String id) {
			
			try {
				return manager.find(MilestoneActivityLevel.class, Long.parseLong(id));
			} catch (Exception e) {
				logger.error( "Inside DAO getSystemLevelNameById() "+e);
				e.printStackTrace();
				return null;
			}
		
		}
		
		private static final String MAINLEVELID = "(SELECT ParentActivityId FROM milestone_activity_level WHERE ActivityId= :ActivityId AND ActivityLevelId = 1 OR ActivityId IN\r\n"
				+ "(SELECT ParentActivityId FROM milestone_activity_level WHERE ActivityId= :ActivityId AND ActivityLevelId = 2 OR ActivityId IN\r\n"
				+ "(SELECT ParentActivityId FROM milestone_activity_level WHERE ActivityId= :ActivityId AND ActivityLevelId = 3 OR ActivityId IN\r\n"
				+ "(SELECT ParentActivityId FROM milestone_activity_level WHERE ActivityId= :ActivityId AND ActivityLevelId = 4 OR ActivityId IN\r\n"
				+ "(SELECT ParentActivityId FROM milestone_activity_level WHERE ActivityId= :ActivityId AND ActivityLevelId = 5 ) ) ) ) )";
		
		@Override
		public String getMainLevelId(Long ActivityId) throws Exception {
			try {
          		Query query = manager.createNativeQuery(MAINLEVELID);
				query.setParameter("ActivityId", ActivityId);
			
				Object levelId = (Object)query.getSingleResult();
				
				return levelId.toString();
				
			} catch (Exception e) {
				e.printStackTrace();
				return "";
				
			}		
		}
		
		private static final String PROJECTID="SELECT projectid FROM milestone_activity WHERE MilestoneActivityId = \r\n"
				+ "(SELECT ParentActivityId FROM milestone_activity_level WHERE ActivityId= :ActivityId AND ActivityLevelId = 1 OR ActivityId IN\r\n"
				+ "	(SELECT ParentActivityId FROM milestone_activity_level WHERE ActivityId= :ActivityId AND ActivityLevelId = 2 OR ActivityId IN\r\n"
				+ "	(SELECT ParentActivityId FROM milestone_activity_level WHERE ActivityId= :ActivityId AND ActivityLevelId = 3 OR ActivityId IN\r\n"
				+ "	(SELECT ParentActivityId FROM milestone_activity_level WHERE ActivityId= :ActivityId AND ActivityLevelId = 4 OR ActivityId IN\r\n"
				+ "	(SELECT ParentActivityId FROM milestone_activity_level WHERE ActivityId= :ActivityId AND ActivityLevelId = 5 ) ) ) ) )";
		
		@Override
		public String getProjectIdByMainLevelId(String id) throws Exception {
			try {
          		Query query = manager.createNativeQuery(PROJECTID);
				query.setParameter("ActivityId", id);
			
				Object levelId = (Object)query.getSingleResult();
				
				return levelId.toString();
				
			} catch (Exception e) {
				e.printStackTrace();
				return "";
				
			}	
		}

	@Override
	public MilestoneActivity getMilestoneActivityById(String id) {
		
		try {
			return manager.find(MilestoneActivity.class, Long.parseLong(id));
		} catch (Exception e) {
			logger.error("Inside DAO getMilestoneActivityById() "+e);
			e.printStackTrace();
			return null;
		}
	}
		
	private static final String ACTIONASSIGNEELIST="SELECT a.ActionAssignId, a.ActionMainId, a.ActionNo, b.ActionItem, a.Assignee, b.ActionDate, a.EndDate, b.ProjectId, a.Loading, c.project_code, c.project_short_name, a.Progress FROM action_assign a JOIN action_main b ON a.ActionMainId=b.ActionMainId LEFT JOIN project_master c ON b.ProjectId=c.project_id WHERE a.IsActive=1 AND a.Assignee=:EmpId";
	@Override
	public List<Object[]> actionAssigneeList(String empId) throws Exception {
		try {
			Query query=manager.createNativeQuery(ACTIONASSIGNEELIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			return (List<Object[]>)query.getResultList();	
		}catch (Exception e) {
			logger.error( "Inside DAO actionAssigneeList "+e);
			e.printStackTrace();
			return null;
		}
	}

	private static final String DELETEMIL = "DELETE FROM milestone_activity_level WHERE ActivityId =:ActivityId ";
	@Override
	public int deleteMilsetone(String activityId) throws Exception {

		Query query = manager.createNativeQuery(DELETEMIL);
		query.setParameter("ActivityId", Long.parseLong(activityId));
		
		return (int)query.executeUpdate();

	
}
	
	
	private static final String MILESTONEACTIVITYPROGRESSLIST="SELECT a.ActivitySubId, a.ActivityId, a.Progress, a.ProgressDate, a.AttachName, a.AttachFile, a.Remarks, c.emp_id, CONCAT(IFNULL(CONCAT(c.title,' '),(IFNULL(CONCAT(c.salutation, ' '), ''))), c.emp_name) AS 'EmpName', d.Designation FROM milestone_activity_sub a LEFT JOIN login b ON a.CreatedBy=b.UserName LEFT JOIN employee c ON b.EmpId=c.emp_id LEFT JOIN employee_desig d ON c.desig_id=d.desig_id WHERE a.IsActive=1 ORDER BY a.ProgressDate DESC";
	@Override
	public List<Object[]> getMilestoneActivityProgressList() throws Exception {
		try {
			Query query=manager.createNativeQuery(MILESTONEACTIVITYPROGRESSLIST);
			return (List<Object[]>)query.getResultList();	
		}catch (Exception e) {
			logger.error( "Inside DAO getMilestoneActivityProgressList "+e);
			e.printStackTrace();
			return null;
		}
	}
	
	
	private static final String PREPROJECTMAINFOLDERLIST="SELECT a.FileRepMasterId,a.ParentLevelId,a.LevelId,a.LevelName,b.ProjectShortName FROM file_rep_master_preproject a LEFT JOIN pfms_initiation b ON a.InitiationId = b.InitiationId WHERE a.FileRepMasterId > 0 AND a.ParentLevelId=0 AND a.InitiationId =:initiationId AND a.LabCode =:labcode";
	@Override
	public List<Object[]> getPreProjectFolderList(String initiationId, String labcode) throws Exception {
		try {
			Query query=manager.createNativeQuery(PREPROJECTMAINFOLDERLIST);
			query.setParameter("initiationId", initiationId);
			query.setParameter("labcode", labcode);
			return (List<Object[]>)query.getResultList();	
		}catch (Exception e) {
			logger.error( "Inside DAO getPreProjectFolderList "+e);
			e.printStackTrace();
			return null;
		}
	}
	
	
	private static final String PREPROJECTSUBFOLDERLIST="SELECT a.FileRepMasterId,a.ParentLevelId,a.LevelId,a.LevelName,b.ProjectShortName FROM file_rep_master_preproject a LEFT JOIN pfms_initiation b ON a.InitiationId = b.InitiationId WHERE a.FileRepMasterId > 0 AND a.ParentLevelId=:mainLevelId AND a.InitiationId =:initiationId AND a.LabCode =:labcode";
	@Override
	public List<Object[]> getPreProjectSubFolderList(String initiationId, String mainLevelId, String labcode)
			throws Exception {
		try {
			Query query=manager.createNativeQuery(PREPROJECTSUBFOLDERLIST);
			query.setParameter("initiationId", initiationId);
			query.setParameter("mainLevelId", mainLevelId);
			query.setParameter("labcode", labcode);
			return (List<Object[]>)query.getResultList();	
		}catch (Exception e) {
			logger.error( "Inside DAO getPreProjectSubFolderList "+e);
			e.printStackTrace();
			return null;
		}
	}
	
	private static final String GETPREPROJECTFILEREPNAME="SELECT COUNT(a.levelname) AS levelname\r\n"
			+ "FROM file_rep_master_preproject a "
			+ "WHERE a.initiationId = :initiationId AND a.IsActive = '1' "
			+ "  AND ("
			+ "    (:fileType = 'mainLevel' AND a.parentLevelId = :fileId AND a.levelId = '1' AND a.levelname = :fileName) "
			+ "    OR "
			+ "    (:fileType = 'subLevel' AND a.parentLevelId = :fileId AND a.levelId = '2' AND a.levelname = :fileName) "
			+ "  ); ";
	@Override
	public int getPreProjectFileRepMasterNames(String initiationId, String fileType, String fileId, String fileName)
			throws Exception {
		try {
			Query query = manager.createNativeQuery(GETPREPROJECTFILEREPNAME);
			query.setParameter("initiationId", initiationId);
			query.setParameter("fileType", fileType);
			query.setParameter("fileId", fileId);
			query.setParameter("fileName", fileName);
		    Object result = query.getSingleResult();
		        if (result != null) {
		            Number count = (Number) result;
		            return count.intValue();
		        } else {
		            return 0;
		        }
			
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	@Override
	public long preProjectRepMasterInsert(FileRepMasterPreProject fileRepo) throws Exception {
		manager.persist(fileRepo);
		manager.flush();
		return fileRepo.getFileRepMasterId();
	}
	
	@Override
	public long preProjectFileRepMasterSubInsert(FileRepMasterPreProject fileRepo) throws Exception {
		manager.persist(fileRepo);
		manager.flush();
		return fileRepo.getFileRepMasterId();
	}
	
	@Override
	public Optional<FileRepMasterPreProject> getPreProjectFileRepById(long id) throws Exception {
		 try {
			 FileRepMasterPreProject fileRepMaster = manager.find(FileRepMasterPreProject.class, id);
		    	return Optional.ofNullable(fileRepMaster);
		} catch (Exception e) {
				return Optional.empty();
		}
	}
	
	@Override
	public Optional<FileRepMasterPreProject> getPreProjectMainFileById(Long parentId) throws Exception {
		 try {
			    FileRepMasterPreProject fileRepMaster = manager.find(FileRepMasterPreProject.class, parentId);
		    	return Optional.ofNullable(fileRepMaster);
		} catch (Exception e) {
				return Optional.empty();
		}
	}
	
	private static final String GETPREPROJECTFILENAME ="SELECT c.filerepuploadId, c.filerepId, c.filenameUi, c.fileName, c.filePath " +
		    "FROM file_rep_new_preproject b JOIN file_rep_upload_preproject c ON b.filerepId = c.filerepId " +
		    "WHERE b.initiationId = :initiationId AND b.isActive = '1' AND c.isActive = '1' AND " +
		    "(" +
		    "  (:levelType = 'mainlevel' AND b.filerepmasterId = :mainRepMasterId AND c.filePath LIKE :prefix) " +
		    "  OR " +
		    "  (:levelType = 'sublevel' AND b.filerepmasterId = :mainRepMasterId AND b.subL1 = :subRepMasterId AND c.filePath LIKE :prefix) " +
		    ")";
	@Override
	 public List<Object[]> findByPreProjectFilePath(String levelType, Long initiationId, Long mainRepMasterId, Long subRepMasterId, String oldName) {
			try {
				Query query = manager.createNativeQuery(GETPREPROJECTFILENAME);
				query.setParameter("prefix", "%" + oldName + "%");
				query.setParameter("levelType", levelType);
				query.setParameter("initiationId", initiationId);
				query.setParameter("mainRepMasterId", mainRepMasterId);
				query.setParameter("subRepMasterId", subRepMasterId);
				return (List<Object[]>)query.getResultList();
			} catch (Exception e) {
				e.printStackTrace();
				return Collections.emptyList();
			}
	    }
	 
	 @Override
	public long preProjectfileEditSubmit(String filerepmasterid, String levelname) throws Exception {
		 
		 FileRepMasterPreProject repmaster=manager.find(FileRepMasterPreProject.class,Long.parseLong(filerepmasterid));	
			repmaster.setLevelName(levelname);
			repmaster=manager.merge(repmaster);
			if(repmaster!=null) {
				return 1;
			}else
			{
				return 0;
			}
	}
	 
	 
	private static final String UPDATEPREPROJECTFILEUPLOAD="UPDATE file_rep_upload_preproject SET filePath=:filePath WHERE filerepuploadId=:filerepuploadId";
	@Override
	public void updatePreProjectFileById(Long uploadId, String updatedPath) throws Exception {
		try {
       		Query query = manager.createNativeQuery(UPDATEPREPROJECTFILEUPLOAD);
				query.setParameter("filerepuploadId", uploadId);
				query.setParameter("filePath", updatedPath);
				query.executeUpdate();
		} catch (Exception e) {
				e.printStackTrace();
		}		
	}
	
	private static final String GETPREPROJECTOLDDOCS="SELECT a.FileRepId,a.InitiationId,a.FileRepMasterId,a.SubL1,a.VersionDoc,a.ReleaseDoc,a.DocumentName, (SELECT b.FileRepUploadId FROM file_rep_upload_preproject b WHERE a.FileRepId = b.FileRepId ORDER BY b.FileRepUploadId DESC LIMIT 1) AS 'FileRepUploadId' FROM file_rep_new_preproject a WHERE a.initiationId=:initiationId AND a.IsActive='1' AND ((:fileType = 'mainLevel' AND a.FileRepMasterId = :fileId AND a.SubL1 = '0') OR (:fileType = 'subLevel' AND a.SubL1 = :fileId))";
	@Override
	public List<Object[]> getPreProjectOldFileNames(String initiationId, String fileType, String fileId)
			throws Exception {
		try {
			Query query = manager.createNativeQuery(GETPREPROJECTOLDDOCS);
			query.setParameter("initiationId", initiationId);
			query.setParameter("fileType", fileType);
			query.setParameter("fileId", fileId);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	@Override
	public FileRepNewPreProject getPreProjectFileById(long repId) throws Exception {


		 return manager.find(FileRepNewPreProject.class,repId);	
	}
	
	private static final String PREPROJECTFILEREPUPDATE="UPDATE file_rep_new_preproject SET VersionDoc=:VersionDoc,ReleaseDoc=:ReleaseDoc,CreatedBy=:CreatedBy,CreatedDate=:CreatedDate WHERE FileRepId=:FileRepId";
	@Override
	public long prepRojectFileUpdate(FileRepNewPreProject rep) throws Exception {
		Query query = manager.createNativeQuery(PREPROJECTFILEREPUPDATE);
		query.setParameter("FileRepId", rep.getFileRepId());
		query.setParameter("VersionDoc", rep.getVersionDoc());
		query.setParameter("ReleaseDoc", rep.getReleaseDoc());
		query.setParameter("CreatedBy", rep.getCreatedBy());
		query.setParameter("CreatedDate", rep.getCreatedDate());
		return query.executeUpdate();
	}
	
	@Override
	public long preProjectFileUploadInsert(FileRepUploadPreProject uploadNew) throws Exception {
		manager.persist(uploadNew);
		manager.flush();
		return uploadNew.getFileRepUploadId();
	}
	
	@Override
	public long preProjectFileSubInsert(FileRepNewPreProject fileRepNew) throws Exception {
		manager.persist(fileRepNew);
		manager.flush();
		return fileRepNew.getFileRepId();
	}
	
	@Override
	public Object[] preProjectRepMaster(String fileRepMasterId) throws Exception {
		Query query=manager.createNativeQuery(PREPROJECTREPMASTER);
		query.setParameter("filerepmasterid", fileRepMasterId);
		List<Object[]> RepMasterData=(List<Object[]>)query.getResultList();
		if(RepMasterData.size()>0) {
			return RepMasterData.get(0);
		}else
		{
			return null;
		}
	}
	
	@Override
	public Optional<FileRepUploadPreProject> getPreProjectUploadFileById(Long id) throws Exception {
		 try {
			 FileRepUploadPreProject fileRepUploadNew = manager.find(FileRepUploadPreProject.class, id);
		    	return Optional.ofNullable(fileRepUploadNew);
		 } catch (Exception e) {
				return Optional.empty();
		 }
	}
	
	
	private static final String PREPROJECTFILEREPDOCSLIST="""
				SELECT a.FileRepUploadId,a.FileRepId,a.FileNameUi,a.FileName,a.VersionDoc,a.ReleaseDoc,a.CreatedDate,
				CONCAT(IFNULL(CONCAT(e.title, ' '), ''), e.empname) AS empname,ed.designation
				FROM file_rep_upload_preproject a
				LEFT JOIN login c ON c.UserName = a.CreatedBy AND c.IsActive = 1
				LEFT JOIN employee e ON e.empId = c.EmpId AND e.isactive = 1
				LEFT JOIN employee_desig ed ON e.desigid = ed.desigid
				WHERE a.IsActive = 1 AND a.FileRepId = :fileRepId
				ORDER BY a.FileRepUploadId DESC
			""";
	@Override
	public List<Object[]> preProjectFileRepoDocsList(String fileRepId) throws Exception {
		try {
			Query query = manager.createNativeQuery(PREPROJECTFILEREPDOCSLIST);
			query.setParameter("fileRepId", fileRepId);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	
	private static final String PREPROJECTFILRREPALLLIST="SELECT a.filerepmasterid,a.parentlevelid,a.levelid,a.levelname,b.ProjectShortName FROM file_rep_master_preproject a LEFT JOIN pfms_initiation b ON a.initiationId = b.initiationId WHERE a.filerepmasterid > 0 AND a.initiationId =:initiationId AND a.labCode =:labCode";
	@Override
	public List<Object[]> getPreProjectFileRepMasterListAll(String initiationId, String labCode) throws Exception {
		try {
			Query query = manager.createNativeQuery(PREPROJECTFILRREPALLLIST);
			query.setParameter("initiationId", initiationId);
			query.setParameter("labCode", labCode);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}

	}
	
	@Override
	public int saveMilestoneActivityLevelRemarks(MilestoneActivityLevelRemarks cmd) throws Exception {
		manager.persist(cmd);
		manager.flush();
		
		return cmd.getRemarksId().intValue();
	}
	
	
	private static final String MILEREMARKS ="SELECT CONCAT(IFNULL(CONCAT(b.title,' '),IFNULL(CONCAT(b.salutation,' '),'')), b.emp_name) AS 'empname',c.designation , \r\n"
			+ "a.remarks , a.createdDate,a.empid, a.activityId  FROM employee b ,milestone_activity_level_remarks a, employee_desig c WHERE a.empid=b.emp_id AND b.desig_id=c.desig_id\r\n"
			+ "AND a.activityId=:activityId  AND a.isactive=1";
	
	@Override
	public List<Object[]> getMilestoneDraftRemarks(Long activityId) throws Exception {

		Query query   = manager.createNativeQuery(MILEREMARKS)	;
		query.setParameter("activityId", activityId);
		
		
		return (List<Object[]>)query.getResultList();
	}
	

	@Override
	public Long saveMilestoneActivityPredecessor(MilestoneActivityPredecessor mp) throws Exception {
		manager.persist(mp);
		manager.flush();
		return mp.getId();
	}
	
	
	private static String PREDECESSORLIST = "SELECT a.id,a.successorId,a.predecessorId FROM milestone_activity_predecessor a WHERE a.successorId=:successorId";
	@Override
	public List<Object[]> predecessorList(String successor) throws Exception {
		
		Query query = manager.createNativeQuery(PREDECESSORLIST);
		query.setParameter("successorId", successor);		
		return (List<Object[]>)query.getResultList() ;
	}
	private static String SUCCESSORLIST = "SELECT a.id,a.successorId,a.predecessorId FROM milestone_activity_predecessor a WHERE a.predecessorId=:predecessorId";

	@Override
	public List<Object[]> getsuccessorList(String predecessorId) throws Exception {
		
		Query query = manager.createNativeQuery(SUCCESSORLIST);
		
		query.setParameter("predecessorId", predecessorId);		
		
		return (List<Object[]>)query.getResultList();
		
		
	}
	
	private static String PREDECESSORDELETE = "DELETE FROM  milestone_activity_predecessor where successorId=:successorId";
	@Override
	public int deleteMilestoneActivityPredecessor(String successor) throws Exception {
		Query query = manager.createNativeQuery(PREDECESSORDELETE);
		query.setParameter("successorId",successor);
		return query.executeUpdate();
	}

	@Override
	public Object[] getprojectDetails(String projectId) throws Exception {
		Query query = manager.createNativeQuery("SELECT * from project_master WHERE project_id=:projectId");
		query.setParameter("projectId", Long.parseLong(projectId));
		List<Object[]> list =  (List<Object[]>) query.getResultList();
		if(list.size()>0) {
			return list.get(0);
		}
		return null;
	}

	private static final String FILEREPUPLOADDETAILS= "SELECT b.FileRepUploadId, b.FileRepId, b.FileName, b.ReportType FROM file_rep_new a LEFT JOIN file_rep_upload b ON a.FileRepId = b.FileRepId ORDER BY FileRepUploadId DESC LIMIT 1;"; 
	@Override
	public Object[] getFileRepUploadDetails(String projectId) throws Exception {
		Query query = manager.createNativeQuery(FILEREPUPLOADDETAILS);
//		query.setParameter("projectId", Long.parseLong(projectId));
		List<Object[]> list =  (List<Object[]>) query.getResultList();
		if(list.size()>0) {
			return list.get(0);
		}
		return null;
	}
	
	
	// Naveen 05-03-2026
	@Override
	public long saveMilestoneActivityBriefing(MilestoneActivityBriefing entity) throws Exception {
		try {
			manager.persist(entity);
			manager.flush();
			return entity.getMilestoneActivityBriefingId();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public MilestoneActivityBriefing getMilestoneActivityBriefing(String mileStoneActivityBriefingId) throws Exception  {
		try {
			if(mileStoneActivityBriefingId==null || mileStoneActivityBriefingId.isBlank()) return null;
			Long id = Long.parseLong(mileStoneActivityBriefingId);
			return manager.find(MilestoneActivityBriefing.class, id);
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static final String MILESTONEBRIEFINGBYID = "SELECT milestoneActivityBriefingId,briefingPointId,points,scheduleId FROM milestone_activity_briefing WHERE milestoneActivityBriefingId = :milestoneActivityBriefingId AND isActive = 1";
	@Override
	public Object[] getMilestoneActivityBriefingById(String mileStoneActivityBriefingId) throws Exception {
		try {
			if(mileStoneActivityBriefingId==null || mileStoneActivityBriefingId.isBlank()) return null;
			Query query = manager.createNativeQuery(MILESTONEBRIEFINGBYID);
			query.setParameter("milestoneActivityBriefingId", Long.parseLong(mileStoneActivityBriefingId));
			List<Object[]> list =  (List<Object[]>) query.getResultList();
			if(list.size()>0) {
				return list.get(0);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static final String MAINMILEDELETE = "UPDATE milestone_activity SET isactive = 0 WHERE MilestoneActivityId = :mainId";
	@Override
	public long deleteMainLevelMilsetone(String mainId) throws Exception {
		try {
			Query query = manager.createNativeQuery(MAINMILEDELETE);
			query.setParameter("mainId",mainId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	private static final String MILESTONEPROGRESSCHECK = """
			SELECT EXISTS (
				    SELECT 1
				    FROM milestone_activity_level l1
				
				    LEFT JOIN milestone_activity_level l2 ON l2.ParentActivityId = l1.ActivityId AND l2.ActivityLevelId = l1.ActivityLevelId + 1
				    
				    LEFT JOIN milestone_activity_level l3 ON l3.ParentActivityId = l2.ActivityId AND l3.ActivityLevelId = l2.ActivityLevelId + 1
				
				    LEFT JOIN milestone_activity_level l4 ON l4.ParentActivityId = l3.ActivityId AND l4.ActivityLevelId = l3.ActivityLevelId + 1
				
				    LEFT JOIN milestone_activity_level l5 ON l5.ParentActivityId = l4.ActivityId AND l5.ActivityLevelId = l4.ActivityLevelId + 1
				
				    WHERE l1.ActivityId = :activityId AND l1.ActivityLevelId = :levelId
				      AND (
				          l1.ProgressStatus > 0
				          OR l1.ActivityStatusId > 1
				          OR l1.Weightage > 0
				
				          OR l2.ProgressStatus > 0
				          OR l2.ActivityStatusId > 1
				          OR l2.Weightage > 0
				
				          OR l3.ProgressStatus > 0
				          OR l3.ActivityStatusId > 1
				          OR l3.Weightage > 0
				
				          OR l4.ProgressStatus > 0
				          OR l4.ActivityStatusId > 1
				          OR l4.Weightage > 0
				
				          OR l5.ProgressStatus > 0
				          OR l5.ActivityStatusId > 1
				          OR l5.Weightage > 0
				      )
				) AS hasDeletionRestriction
			""";
		@Override
		public long deleteSubLevelMilsetone(String subId) throws Exception {
			try {
	
			 Long activityId = Long.valueOf(subId);

			    // Get the level of the activity
			    Integer levelId = ((Number) manager.createNativeQuery("""
			                SELECT ActivityLevelId
			                FROM milestone_activity_level
			                WHERE ActivityId = :activityId
			                """)
			            .setParameter("activityId", activityId)
			            .getSingleResult())
			            .intValue();

			    Long hasRestriction = (Long) manager
			            .createNativeQuery(MILESTONEPROGRESSCHECK)
			            .setParameter("activityId", activityId)
			            .setParameter("levelId", levelId)
			            .getSingleResult();

			    if (hasRestriction == 1) {
			        return -1;
			    }
			    
			    String deleteQuerry = "DELETE FROM milestone_activity_level WHERE ActivityId = :activityId";

			    return manager.createNativeQuery(deleteQuerry)
			    	    .setParameter("activityId", activityId)
			    	    .executeUpdate();
			}catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		}
		
		private static final String SUNSETMILEUPDATE = "UPDATE milestone_activity SET IsSunSet = :isSunSet WHERE MilestoneActivityId = :mainId";
		private static final String SUNSETSUBMILEUPDATE = "UPDATE milestone_activity_level SET IsSunSet = :isSunSet WHERE ActivityId IN :subId";
		@Override
		@Transactional
		public long updateMainMileStoneSunSet(Long milestoneActivityId, String isSunSet) {
		    try {
		        MilestoneActivity activity = manager.find(MilestoneActivity.class, milestoneActivityId);
		        if (activity == null) {
		            return 0; // nothing to update
		        }

		        Set<Long> subIds = getSubMilestonesId(String.valueOf(milestoneActivityId));

		        // toggle based on current state
		        String newSunSetValue = "Y".equalsIgnoreCase(activity.getIsSunSet()) ? "N" : "Y";

		        long count = 0;

		        Query query = manager.createNativeQuery(SUNSETMILEUPDATE);
		        query.setParameter("mainId", milestoneActivityId);
		        query.setParameter("isSunSet", newSunSetValue);
		        count += query.executeUpdate();

		        if (subIds != null && !subIds.isEmpty()) {
		        	
		        	System.out.println(subIds);
		            Query subQuery = manager.createNativeQuery(SUNSETSUBMILEUPDATE);
		            subQuery.setParameter("subId", subIds);
		            subQuery.setParameter("isSunSet", newSunSetValue);
		            count += subQuery.executeUpdate();
		        }

		        return count;

		    } catch (Exception e) {
		        e.printStackTrace();
		        return 0;
		    }
		}

		@Override
		public long updateSubMileStoneSunSet(Long activityId, String isSunSet) {
			try {
				MilestoneActivityLevel activity = manager.find(MilestoneActivityLevel.class, activityId);
				
				isSunSet = "Y".equalsIgnoreCase(activity.getIsSunSet()) ? "N" : "Y";
				Query query = manager.createNativeQuery(SUNSETSUBMILEUPDATE);
				query.setParameter("subId",activityId);
				query.setParameter("isSunSet",isSunSet);
				return query.executeUpdate();
			}catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		}

		private static final String MILESTONESUBLEVELIDS = """				
					WITH levels AS (
					    SELECT 
					        a.ActivityId AS level1,
					        b.ActivityId AS level2,
					        c.ActivityId AS level3,
					        d.ActivityId AS level4,
					        e.ActivityId AS level5
					    FROM milestone_activity main 
					    LEFT JOIN milestone_activity_level a ON a.ParentActivityId = main.MilestoneActivityId AND a.ActivityLevelId = 1
					    LEFT JOIN milestone_activity_level b ON b.ParentActivityId = a.ActivityId AND b.ActivityLevelId = 2
					    LEFT JOIN milestone_activity_level c ON c.ParentActivityId = b.ActivityId AND c.ActivityLevelId = 3
					    LEFT JOIN milestone_activity_level d ON d.ParentActivityId = c.ActivityId AND d.ActivityLevelId = 4
					    LEFT JOIN milestone_activity_level e ON e.ParentActivityId = d.ActivityId AND e.ActivityLevelId = 5
					    WHERE main.MilestoneActivityId = :mainId
					)
					SELECT level1 AS SubLevelId FROM levels WHERE level1 IS NOT NULL
					UNION
					SELECT level2 FROM levels WHERE level2 IS NOT NULL
					UNION
					SELECT level3 FROM levels WHERE level3 IS NOT NULL
					UNION
					SELECT level4 FROM levels WHERE level4 IS NOT NULL
					UNION
					SELECT level5 FROM levels WHERE level5 IS NOT NULL;
				""";
		@Override
		public Set<Long> getSubMilestonesId(String milestoneActivityId) throws Exception {

		    Query query = manager.createNativeQuery(MILESTONESUBLEVELIDS);
		    query.setParameter("mainId", Long.parseLong(milestoneActivityId));

		    @SuppressWarnings("unchecked")
		    List<Object> results = query.getResultList();

		    Set<Long> subLevelIds = new LinkedHashSet<>();
		    for (Object result : results) {
		        if (result != null) {
		            subLevelIds.add(((Number) result).longValue());
		        }
		    }

		    return subLevelIds;
		}
		
		private static final String MANPOWERCOUNTS = """
				   SELECT a.resource_utilization_id,a.project_id,a.financial_year,a.quarter,b.revision_no, 
					    SUM(CASE WHEN b.desig_cadre = 'DRDS'
						     THEN b.man_power_count ELSE 0 END) AS scientist_count,
					    SUM(CASE WHEN b.desig_cadre = 'DTDC'
						     THEN b.man_power_count ELSE 0 END) AS technician_count,
					    SUM(CASE WHEN b.desig_cadre = 'Admin & Allied'
						     THEN b.man_power_count ELSE 0 END) AS admin_and_allience_count,
					    SUM(CASE WHEN b.desig_cadre = 'DRDS'
						     THEN b.man_power_days ELSE 0 END) AS scientist_days_count,
					    SUM(CASE WHEN b.desig_cadre = 'DTDC'
						     THEN b.man_power_days ELSE 0 END) AS technician_days_count,
					    SUM(CASE WHEN b.desig_cadre = 'Admin & Allied'
						     THEN b.man_power_days ELSE 0 END) AS admin_and_allience_days_count
					FROM project_resource_utilization a 
					INNER JOIN project_manpower_utilization b ON b.resource_utilization_id = a.resource_utilization_id
					WHERE a.project_id = :projectId AND a.financial_year = :finYear AND a.quarter = :quarter
					GROUP BY a.resource_utilization_id, a.project_id, a.financial_year, a.quarter, b.revision_no;
				"""; 
		@Override
		public Object[] getManPowerUtilizationCounts(String projectId, String finYear, String quarter) throws Exception {
			try {
				Query query = manager.createNativeQuery(MANPOWERCOUNTS);
			    query.setParameter("projectId", projectId);
			    query.setParameter("finYear", finYear);
			    query.setParameter("quarter", quarter);
			    
			    @SuppressWarnings("unchecked")
				List<Object[]> rows = query.getResultList();
				
			    return rows.isEmpty() ? null : rows.get(0);
			}catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		
		@Override
		public long addManPowerUtilization(ProjectManPowerUtilization entity) throws Exception {
			entity = manager.merge(entity);
			manager.flush();
			return entity.getManPowerUtilizationId();
		}
		
		@Override
		public long saveProjectResourceUtilization(ProjectResourceUtilization entity) throws Exception {
			entity = manager.merge(entity);
			manager.flush();

			return entity.getResourceUtilizationId();
		}
		
		@Override
		public ProjectResourceUtilization findOrCreateResourceUtilization(Long projectId, String finYear, String quarter, String username) throws Exception{

		    if (projectId == null) {
		        throw new IllegalArgumentException("Project is required.");
		    }
		    if (finYear == null || finYear.isBlank()) {
		        throw new IllegalArgumentException("Financial year is required.");
		    }
		    if (quarter == null || quarter.isBlank()) {
		        throw new IllegalArgumentException("Quarter is required.");
		    }

		    String createdBy = (username != null && !username.isBlank()) ? username : "SYSTEM";

		    String jpql = "SELECT r FROM ProjectResourceUtilization r WHERE r.projectId = :projectId AND r.financialYear = :fy AND r.quarter = :q";
		    try {
		        return manager.createQuery(jpql, ProjectResourceUtilization.class)
		                .setParameter("projectId", projectId)
		                .setParameter("fy", finYear)
		                .setParameter("q", quarter)
		                .getSingleResult();
		    } catch (NoResultException e) {
		    	ProjectResourceUtilization ru = new ProjectResourceUtilization();
		        ru.setProjectId(projectId);
		        ru.setFinancialYear(finYear);
		        ru.setQuarter(quarter);
		        ru.setCreatedBy(createdBy);
		        ru.setCreatedDate(LocalDateTime.now());
		        ru.setIsActive(1);
		        manager.persist(ru);
		        manager.flush();
		        return ru;
		    }
		}
		
		@Override
		public long saveProjectInfrastructureUtilization(ProjectInfrastructureUtilization entity) throws Exception{
			entity = manager.merge(entity);
			manager.flush();

			return entity.getInfrastructureUtilizationId();
		}
		
		private static final String INFRASTRUCTURELIST = """
					SELECT a.resource_utilization_id, a.project_id,a.financial_year,a.quarter,b.revision_no,b.infrastructure_utilization_id,b.name_of_infrastructure,b.days_utilized
					FROM project_resource_utilization a
					INNER JOIN project_infrastructure_utilization b ON b.resource_utilization_id =a.resource_utilization_id AND b.is_active = 1
					WHERE a.project_id = :projectId AND a.financial_year = :finYear AND a.quarter = :quarter
				""";
		@Override
		public List<Object[]> getInfrastructureItems(String projectId, String finYear, String quarter) throws Exception {
			try {
				Query query = manager.createNativeQuery(INFRASTRUCTURELIST);
			    query.setParameter("projectId", projectId);
			    query.setParameter("finYear", finYear);
			    query.setParameter("quarter", quarter);
			    
				return (List<Object[]>) query.getResultList();
			}catch (Exception e) {
				e.printStackTrace();
				return List.of();
			}
		}
		
		@Override
		public ProjectResourceUtilization findResourceUtilizationId(Long projectId, String finYear, String quarter) throws Exception {
		    try {
		        return manager.createQuery(
		                "SELECT r FROM ProjectResourceUtilization r WHERE r.projectId = :pid AND r.financialYear = :fy AND r.quarter = :q", ProjectResourceUtilization.class)
		                .setParameter("pid", projectId).setParameter("fy", finYear).setParameter("q", quarter)
		                .getSingleResult();
		    } catch (NoResultException e) {
		        return null;
		    }
		}

		@Override
		public List<ProjectInfrastructureUtilization> getActiveInfrastructure(Long resourceUtilizationId) throws Exception {
		    String jpql = "SELECT p FROM ProjectInfrastructureUtilization p WHERE p.resourceUtilizationId = :ruId AND p.isActive = 1";
		    return manager.createQuery(jpql, ProjectInfrastructureUtilization.class)
		            .setParameter("ruId", resourceUtilizationId)
		            .getResultList();
		}

		@Override
		public void deleteProjectInfrastructureUtilization(ProjectInfrastructureUtilization entity) throws Exception {
		    ProjectInfrastructureUtilization managed = manager.contains(entity) ? entity : manager.merge(entity);
		    manager.remove(managed);
		    manager.flush();
		}

		@Override
		public void updateProjectInfrastructureUtilization(ProjectInfrastructureUtilization entity) throws Exception {
			entity = manager.merge(entity);
		    manager.flush();
		}

		@Override
		public long saveProjectInfrastructureUtilizationRev(ProjectInfrastructureUtilizationRev entity) throws Exception {
			entity = manager.merge(entity);
		    manager.flush();
		    return entity.getInfrastructureUtilizationRevId();
		}
		
		@Override
		public List<ProjectManPowerUtilization> getManPowerUtilization(Long resourceUtilizationId) throws Exception {
			String jpql = "SELECT p FROM ProjectManPowerUtilization p WHERE p.resourceUtilizationId = :ruId AND p.isActive = 1";
		    return manager.createQuery(jpql, ProjectManPowerUtilization.class)
		            .setParameter("ruId", resourceUtilizationId)
		            .getResultList();
		}
		
		@Override
		public long addManPowerUtilizationRevision(ProjectManPowerUtilizationRev entity) throws Exception {
			entity = manager.merge(entity);
		    manager.flush();
		    return entity.getManPowerUtilizationRevId();
		}
		
		@Override
		public long saveProjectTrainingUtilization(ProjectTrainingUtilization entity) throws Exception {
		    entity = manager.merge(entity);
		    manager.flush();
		    return entity.getTrainingUtilizationId();
		}

		private static final String TRAININGLIST = """
		            SELECT a.resource_utilization_id, a.project_id, a.financial_year, a.quarter, b.revision_no, 
		                   b.training_utilization_id, b.name_of_training, NULL as no_of_participants, b.cost
		            FROM project_resource_utilization a
		            INNER JOIN project_training_utilization b ON b.resource_utilization_id = a.resource_utilization_id AND b.is_active = 1
		            WHERE a.project_id = :projectId AND a.financial_year = :finYear AND a.quarter = :quarter
		        """;

		@Override
		public List<Object[]> getTrainingItems(String projectId, String finYear, String quarter) throws Exception {
		    try {
		        Query query = manager.createNativeQuery(TRAININGLIST);
		        query.setParameter("projectId", projectId);
		        query.setParameter("finYear", finYear);
		        query.setParameter("quarter", quarter);
		        
		        return (List<Object[]>) query.getResultList();
		    } catch (Exception e) {
		        e.printStackTrace();
		        return List.of();
		    }
		}

		@Override
		public List<ProjectTrainingUtilization> getActiveTraining(Long resourceUtilizationId) throws Exception {
		    String jpql = "SELECT p FROM ProjectTrainingUtilization p WHERE p.resourceUtilizationId = :ruId AND p.isActive = 1";
		    return manager.createQuery(jpql, ProjectTrainingUtilization.class)
		            .setParameter("ruId", resourceUtilizationId)
		            .getResultList();
		}

		@Override
		public void deleteProjectTrainingUtilization(ProjectTrainingUtilization entity) throws Exception {
		    ProjectTrainingUtilization managed = manager.contains(entity) ? entity : manager.merge(entity);
		    manager.remove(managed);
		    manager.flush();
		}

		@Override
		public void updateProjectTrainingUtilization(ProjectTrainingUtilization entity) throws Exception {
		    entity = manager.merge(entity);
		    manager.flush();
		}

		@Override
		public long saveProjectTrainingUtilizationRev(ProjectTrainingUtilizationRev entity) throws Exception {
		    entity = manager.merge(entity);
		    manager.flush();
		    return entity.getTrainingUtilizationRevId();
		}
		

		@Override
		public long saveProjectEconomicImpact(ProjectEconomicImpact entity) throws Exception {
		    entity = manager.merge(entity);
		    manager.flush();
		    return entity.getEconomicImpactId();
		}

		@Override
		public long saveProjectEconomicImpactRev(ProjectEconomicImpactRev entity) throws Exception {
		    entity = manager.merge(entity);
		    manager.flush();
		    return entity.getEconomicImpactRevId();
		}
		
		@Override
		public List<ProjectEconomicImpact> getEconomicImpact(Long projectId) throws Exception {
		    String jpql = "SELECT p FROM ProjectEconomicImpact p WHERE p.projectId = :projectId AND p.isActive = 1";
		    return manager.createQuery(jpql, ProjectEconomicImpact.class)
		            .setParameter("projectId", projectId)
		            .getResultList();
		}
		
		@Override
		public ProjectEconomicImpact getEconomiImpactById(Long economicImpactId) {
			String jpql = "SELECT p FROM ProjectEconomicImpact p WHERE p.economicImpactId = :economicImpactId";
		    return manager.createQuery(jpql, ProjectEconomicImpact.class)
		            .setParameter("economicImpactId", economicImpactId)
		            .getSingleResult();
		}
		
		@Override
		public List<Object[]> getInfrastructureRevisionList(Long resourceUtilizationId) throws Exception {
			try {
				String sql = """
						SELECT COUNT(a.infrastructure_utilization_rev_id) AS 'revision_count', a.revision_no, a.created_by, a.revision_date
						FROM project_infrastructure_utilization_rev a
						WHERE a.resource_utilization_id = :resourceUtilizationId 
						GROUP BY a.revision_no, a.created_by, a.revision_date
						""";
				Query query = manager.createNativeQuery(sql);
			    query.setParameter("resourceUtilizationId", resourceUtilizationId);
			           
			    return (List<Object[]>) query.getResultList();
			}catch (Exception e) {
				e.printStackTrace();
				return List.of();
			}
		}
		
		
		@Override
		public List<Object[]> getInfrastructureRevisionItemsList(Long resourceUtilizationId,Long revisionNo) throws Exception {
			try {
				String sql = """
						SELECT a.infrastructure_utilization_rev_id, a.resource_utilization_id, a.name_of_infrastructure, a.days_utilized,a.revision_no
						FROM project_infrastructure_utilization_rev a
						WHERE a.resource_utilization_id = :resourceUtilizationId AND revision_no = :revisionNo
						""";
				Query query = manager.createNativeQuery(sql);
			    query.setParameter("resourceUtilizationId", resourceUtilizationId);
			    query.setParameter("revisionNo", revisionNo);
			           
			    return (List<Object[]>) query.getResultList();
			}catch (Exception e) {
				e.printStackTrace();
				return List.of();
			}
		}

		@Override
		public List<Object[]> getTrainingRevisionList(Long resourceUtilizationId) throws Exception {
			try {
				String sql = """
						SELECT COUNT(a.training_utilization_rev_id) AS 'revision_count', a.revision_no, a.created_by, a.revision_date
						FROM project_training_utilization_rev a
						WHERE a.resource_utilization_id = :resourceUtilizationId 
						GROUP BY a.revision_no, a.created_by, a.revision_date
						""";
				Query query = manager.createNativeQuery(sql);
			    query.setParameter("resourceUtilizationId", resourceUtilizationId);
			           
			    return (List<Object[]>) query.getResultList();
			}catch (Exception e) {
				e.printStackTrace();
				return List.of();
			}
		}
		
		@Override
		public List<Object[]> getManpowerRevisionList(Long resourceUtilizationId) throws Exception {
			try {
				String sql = """
						SELECT COUNT(DISTINCT a.revision_no) AS 'revision_count', a.revision_no, a.created_by, a.revision_date
						FROM project_manpower_utilization_rev a
						WHERE a.resource_utilization_id = :resourceUtilizationId 
						GROUP BY a.revision_no, a.created_by, a.revision_date
						""";
				Query query = manager.createNativeQuery(sql);
			    query.setParameter("resourceUtilizationId", resourceUtilizationId);
			           
			    return (List<Object[]>) query.getResultList();
			}catch (Exception e) {
				e.printStackTrace();
				return List.of();
			}
		}
		
		@Override
		public List<Object[]> getTrainingRevisionItemsList(Long resourceUtilizationId, long revisionNo) throws Exception {
			try {
				String sql = """
						SELECT a.training_utilization_rev_id, a.resource_utilization_id, a.name_of_training, a.cost,a.revision_no
						FROM project_training_utilization_rev a
						WHERE a.resource_utilization_id = :resourceUtilizationId AND revision_no = :revisionNo
						""";
				Query query = manager.createNativeQuery(sql);
			    query.setParameter("resourceUtilizationId", resourceUtilizationId);
			    query.setParameter("revisionNo", revisionNo);
			           
			    return (List<Object[]>) query.getResultList();
			}catch (Exception e) {
				e.printStackTrace();
				return List.of();
			}
		}
		
		@Override
		public Object[] getManpowerRevisionRow(Long resourceUtilizationId, Long revisionNo) {
		    String sql = """
						SELECT b.revision_no, 
						    SUM(CASE WHEN b.desig_cadre = 'DRDS'
							     THEN b.man_power_count ELSE 0 END) AS scientist_count,
						    SUM(CASE WHEN b.desig_cadre = 'DTDC'
							     THEN b.man_power_count ELSE 0 END) AS technician_count,
						    SUM(CASE WHEN b.desig_cadre = 'Admin & Allied'
							     THEN b.man_power_count ELSE 0 END) AS admin_and_allience_count,
						    SUM(CASE WHEN b.desig_cadre = 'DRDS'
							     THEN b.man_power_days ELSE 0 END) AS scientist_days_count,
						    SUM(CASE WHEN b.desig_cadre = 'DTDC'
							     THEN b.man_power_days ELSE 0 END) AS technician_days_count,
						    SUM(CASE WHEN b.desig_cadre = 'Admin & Allied'
							     THEN b.man_power_days ELSE 0 END) AS admin_and_allience_days_count
						FROM project_manpower_utilization_rev b 
						WHERE b.resource_utilization_id = :ruId AND b.revision_no = :revNo
						GROUP BY b.revision_no;
		             """;
		    Query query = manager.createNativeQuery(sql);
		    query.setParameter("ruId", resourceUtilizationId);
		    query.setParameter("revNo", revisionNo);
		           
		    List<Object[]> rev = (List<Object[]>) query.getResultList();

		    return rev.isEmpty() ? null : rev.get(0);
		}
		
		@Override
		public List<Object[]> getEconomicImpactRevisionList(Long projectId) throws Exception {
		    String sql = "SELECT r.revision_no, r.revision_date, COALESCE(r.modified_by, r.created_by) FROM project_economic_impact_rev r WHERE r.project_id = :pid ";
		    
		    Query query = manager.createNativeQuery(sql);
		    query.setParameter("pid", projectId);
		    
		    return (List<Object[]>) query.getResultList();
		}

		@Override
		public ProjectEconomicImpactRev getEconomicImpactRevByProjectAndRevision(Long projectId, Long revisionNo) throws Exception {
		    String jpql = "SELECT r FROM ProjectEconomicImpactRev r WHERE r.projectId = :pid AND r.revisionNo = :rev";
		    Query query = manager.createQuery(jpql, ProjectEconomicImpactRev.class);
		    query.setParameter("pid", projectId);
		    query.setParameter("rev", revisionNo);
		    List<ProjectEconomicImpactRev> list = (List<ProjectEconomicImpactRev>)query.getResultList();
		    
		    return list.isEmpty() ? null : list.get(0);
		}
		
		@Override
		public List<Object[]> getProjectResourceUtilizationByProjectId(Long projectId,String finYear) throws Exception {
			String jpql = """
					SELECT a.resource_utilization_id, a.project_id, a.financial_year, a.quarter,				
					    SUM(CASE WHEN b.desig_cadre = 'DRDS' THEN b.man_power_count ELSE 0 END) AS scientist_count,
					    SUM(CASE WHEN b.desig_cadre = 'DTDC' THEN b.man_power_count ELSE 0 END) AS technician_count,
					    SUM(CASE WHEN b.desig_cadre = 'Admin & Allied' THEN b.man_power_count ELSE 0 END) AS admin_and_alliance_count,				
					    SUM(CASE WHEN b.desig_cadre = 'DRDS' THEN b.man_power_days ELSE 0 END) AS scientist_days_count,				
					    SUM(CASE WHEN b.desig_cadre = 'DTDC' THEN b.man_power_days ELSE 0 END) AS technician_days_count,
					    SUM(CASE WHEN b.desig_cadre = 'Admin & Allied' THEN b.man_power_days ELSE 0 END) AS admin_and_alliance_days_count,
						SUM(CASE WHEN b.desig_cadre = 'DRDS' THEN (b.man_power_count * b.man_power_days) ELSE 0 END) AS scientist_total_person_days,
						SUM(CASE WHEN b.desig_cadre = 'DTDC' THEN (b.man_power_count * b.man_power_days) ELSE 0 END) AS technician_total_person_days,
						SUM(CASE WHEN b.desig_cadre = 'Admin & Allied' THEN (b.man_power_count * b.man_power_days) ELSE 0 END) AS admin_and_alliance_total_person_days
					FROM project_resource_utilization a 				
					INNER JOIN project_manpower_utilization b
				    ON b.resource_utilization_id = a.resource_utilization_id
					WHERE a.project_id = :pid AND a.financial_year = :finYear
					GROUP BY a.resource_utilization_id, a.project_id, a.financial_year, a.quarter
					ORDER BY a.quarter ASC;
					""";
		    Query query = manager.createNativeQuery(jpql);
		    query.setParameter("pid", projectId);
		    query.setParameter("finYear", finYear);
		    return (List<Object[]>)query.getResultList();
		}
		
		@Override
		public Object[] getManPowerTotalCounts(Long projectId) throws Exception {
			String jpql = """
				SELECT a.project_id,
				    SUM(CASE WHEN b.desig_cadre = 'DRDS' THEN (b.man_power_count * b.man_power_days) ELSE 0 END) AS scientist_total_person_days,				             
				    SUM(CASE WHEN b.desig_cadre = 'DTDC'  THEN (b.man_power_count * b.man_power_days) ELSE 0 END) AS technician_total_person_days,
				    SUM(CASE WHEN b.desig_cadre = 'Admin & Allied' THEN (b.man_power_count * b.man_power_days) ELSE 0 END) AS admin_and_alliance_total_person_days,
				    SUM(CASE WHEN b.desig_cadre = 'DRDS' THEN b.man_power_count ELSE 0 END) AS scientist_count,
				    SUM(CASE WHEN b.desig_cadre = 'DTDC' THEN b.man_power_count ELSE 0 END) AS technician_count,
				    SUM(CASE WHEN b.desig_cadre = 'Admin & Allied' THEN b.man_power_count ELSE 0 END) AS admin_and_alliance_count,
				    SUM(CASE WHEN b.desig_cadre = 'DRDS' THEN b.man_power_days ELSE 0 END) AS scientist_days,
				    SUM(CASE WHEN b.desig_cadre = 'DTDC' THEN b.man_power_days ELSE 0 END) AS technician_days,
				    SUM(CASE WHEN b.desig_cadre = 'Admin & Allied' THEN b.man_power_days ELSE 0 END) AS admin_and_alliance_days
				FROM project_resource_utilization a 
				INNER JOIN project_manpower_utilization b ON b.resource_utilization_id = a.resource_utilization_id
				WHERE a.project_id = :pid
				GROUP BY a.project_id;
					""";
		    Query query = manager.createNativeQuery(jpql);
		    query.setParameter("pid", projectId);
		    List<Object[]> result = query.getResultList();
		    return result.isEmpty() ? null : result.get(0);
		}
		
		@Override
		public List<Object[]> getInfrastructures(Long projectId,String finYear) throws Exception {
			try {
				String sql = """
						  SELECT  b.name_of_infrastructure,
						    SUM(CASE WHEN a.financial_year < :finYear THEN b.days_utilized ELSE 0 END) AS total_upto_last_year,
						    SUM(CASE WHEN a.financial_year = :finYear THEN b.days_utilized ELSE 0 END) AS total_till_date,
						    SUM(CASE WHEN a.financial_year = :finYear AND a.quarter = 'Q1' THEN b.days_utilized ELSE 0 END) AS q1_total,
						    SUM(CASE WHEN a.financial_year = :finYear AND a.quarter = 'Q2' THEN b.days_utilized ELSE 0 END) AS q2_total,
						    SUM(CASE WHEN a.financial_year = :finYear AND a.quarter = 'Q3' THEN b.days_utilized ELSE 0 END) AS q3_total,
						    SUM(CASE WHEN a.financial_year = :finYear AND a.quarter = 'Q4' THEN b.days_utilized ELSE 0 END) AS q4_total
						FROM project_resource_utilization a
						INNER JOIN project_infrastructure_utilization b ON b.resource_utilization_id = a.resource_utilization_id AND b.is_active = 1
						WHERE a.project_id = :pid
						GROUP BY b.name_of_infrastructure
						ORDER BY b.name_of_infrastructure ASC;
						""";
				Query query = manager.createNativeQuery(sql);
			    query.setParameter("pid", projectId);
			    query.setParameter("finYear", finYear);
			    return (List<Object[]>)query.getResultList();
			}catch (Exception e) {
				e.printStackTrace();
				return List.of();
			}
		}
		
		
		@Override
		public List<Object[]> getTrainings(Long projectId, String finYear) throws Exception {
			try {
				String sql = """
						  SELECT  b.name_of_training,
						    SUM(CASE WHEN a.financial_year < :finYear THEN b.cost ELSE 0 END) AS total_upto_last_year,
						    SUM(CASE WHEN a.financial_year = :finYear THEN b.cost ELSE 0 END) AS total_till_date,
						    SUM(CASE WHEN a.financial_year = :finYear AND a.quarter = 'Q1' THEN b.cost ELSE 0 END) AS q1_total,
						    SUM(CASE WHEN a.financial_year = :finYear AND a.quarter = 'Q2' THEN b.cost ELSE 0 END) AS q2_total,
						    SUM(CASE WHEN a.financial_year = :finYear AND a.quarter = 'Q3' THEN b.cost ELSE 0 END) AS q3_total,
						    SUM(CASE WHEN a.financial_year = :finYear AND a.quarter = 'Q4' THEN b.cost ELSE 0 END) AS q4_total
						FROM project_resource_utilization a
						INNER JOIN project_training_utilization b ON b.resource_utilization_id = a.resource_utilization_id AND b.is_active = 1
						WHERE a.project_id = :pid
						GROUP BY b.name_of_training
						ORDER BY b.name_of_training ASC;
						""";
				Query query = manager.createNativeQuery(sql);
			    query.setParameter("pid", projectId);
			    query.setParameter("finYear", finYear);
			    return (List<Object[]>)query.getResultList();
			}catch (Exception e) {
				e.printStackTrace();
				return List.of();
			}
		}
}