package com.vts.pfms.print.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import com.vts.pfms.committee.model.Committee;
import com.vts.pfms.committee.model.CommitteeSchedule;
import com.vts.pfms.committee.model.PfmsNotification;
import com.vts.pfms.milestone.model.MilestoneActivityLevelConfiguration;
import com.vts.pfms.model.BriefingFinance;
import com.vts.pfms.model.BriefingHeading;
import com.vts.pfms.model.BriefingHeadingDetails;
import com.vts.pfms.model.LabMaster;
import com.vts.pfms.print.model.CommitteeProjectBriefingFrozen;
import com.vts.pfms.print.model.FavouriteSlidesModel;
import com.vts.pfms.print.model.InitiationSanction;
import com.vts.pfms.print.model.InitiationsanctionCopyAddr;
import com.vts.pfms.print.model.PfmsBriefingTransaction;
import com.vts.pfms.print.model.ProjectOverallFinance;
import com.vts.pfms.print.model.ProjectSlideFreeze;
import com.vts.pfms.print.model.ProjectSlides;
import com.vts.pfms.print.model.RecDecDetails;
import com.vts.pfms.print.model.TechImages;
import com.vts.pfms.project.model.PfmsProjectData;

import groovyjarjarantlr4.v4.parse.ANTLRParser.exceptionGroup_return;



@Transactional
@Repository
public class PrintDaoImpl implements PrintDao {

	private static final String LABLIST="select labcode, labname,labaddress, labcity,lablogo from lab_master where labcode=:labcode";
	private static final String PFMSINITLIST="SELECT a.initiationid,a.projectprogramme,b.projecttypeshort,c.classification,a.projectshortname,a.projecttitle,a.projectcost,a.projectduration,a.isplanned,a.ismultilab,a.createddate,a.deliverable,a.ismain,d.projecttitle AS initiatedproject,a.remarks,a.fecost,a.recost,a.labcode ,a.classificationid , a.projecttypeid FROM pfms_initiation a,project_type b,pfms_security_classification c ,pfms_initiation d WHERE a.classificationid=c.classificationid  AND a.projecttypeid=b.projecttypeid AND a.isactive='1' AND a.initiationid=:initiationid AND a.mainid=d.initiationid UNION SELECT a.initiationid,a.projectprogramme,b.projecttypeshort,c.classification,a.projectshortname,a.projecttitle,a.projectcost,a.projectduration, a.isplanned,a.ismultilab,a.createddate,a.deliverable,a.ismain,a.projecttitle AS initiatedproject,a.remarks,a.fecost,a.recost,a.labcode ,a.classificationid , a.projecttypeid FROM pfms_initiation a,project_type b,pfms_security_classification c WHERE a.classificationid=c.classificationid  AND a.projecttypeid=b.projecttypeid AND a.isactive='1' AND a.initiationid=:initiationid AND a.mainid=0";
	private static final String PROJECTDETAILSLIST= "SELECT a.Requirements,a.Objective,a.Scope,a.MultiLabWorkShare,a.EarlierWork,a.CompentencyEstablished,a.NeedOfProject,a.TechnologyChallanges,a.RiskMitigation,a.Proposal,a.RealizationPlan,a.initiationid,a.worldscenario,a.ReqBrief,a.ObjBrief,a.ScopeBrief,a.MultiLabBrief,a.EarlierWorkBrief,a.CompentencyBrief,a.NeedOfProjectBrief,a.TechnologyBrief,a.RiskMitigationBrief,a.ProposalBrief,a.RealizationBrief,a.WorldScenarioBrief FROM pfms_initiation_detail a WHERE a.initiationid=:initiationid ";	
	private static final String COSTDETAILSLIST="SELECT c.headofaccounts,CONCAT (c.majorhead,'-',c.minorhead,'-',c.subhead) AS headcode,a.itemdetail,a.itemcost,c.sanctionitemid,c.refe  FROM pfms_initiation_cost a,budget_item_sanc c WHERE a.budgetsancid=c.sanctionitemid AND a.isactive='1' AND a.initiationid=:initiationid AND a.budgetheadid=c.budgetheadid  ORDER BY sanctionitemid";
	private static final String PROJECTSCHEDULELIST="select milestoneno,milestoneactivity,milestonemonth,initiationscheduleid,milestoneremark,milestonestartedfrom,milestonetotalmonth from pfms_initiation_schedule where initiationid=:initiationid and isactive='1'";

	private static final String PROJECTSLIST="SELECT projectid, projectcode, projectname FROM project_master";
	
	private static final String MILESTONESUBSYSTEMS="SELECT maa.activityId, maa.Parentactivityid, maa.activityname, maa.orgenddate, maa.enddate,maa.activitystatusid,mas.activityshort,  maa.ProgressStatus,ma.milestoneno, maa.StatusRemarks FROM milestone_activity ma,milestone_activity_level maa,milestone_activity_status mas WHERE ma.milestoneactivityid = maa.parentactivityid AND maa.activitylevelid='1' AND maa.activitystatusid=mas.activitystatusid  AND ma.projectid=:projectid ORDER BY ma.milestoneno";
	private static final String PROJECTDETAILS="SELECT a.projectid,a.projectcode,a.projectname,a.projectname AS 'name',a.projectcode AS 'code',a.labcode, a.IsCCS, a.IsMainWC FROM project_master a WHERE a.projectid=:projectid AND  a.isactive=1";
	private static final String GANTTCHARTLIST="SELECT milestoneactivityid,projectid,activityname,milestoneno,orgstartdate,orgenddate,startdate,enddate,progressstatus,revisionno FROM milestone_activity WHERE isactive=1 AND projectid=:projectid";
//	private static final String MILESTONES="SELECT ma.milestoneactivityid,ma.projectid,ma.milestoneno,ma.activityname,ma.orgstartdate,ma.orgenddate,ma.startdate,ma.enddate, ma.activitytype AS 'activitytypeid' , mat.activitytype,ma.activitystatusid,mas.activityshort, ma.ProgressStatus,ma.StatusRemarks ,ma. dateofcompletion FROM milestone_activity ma, milestone_activity_type mat ,milestone_activity_status mas  WHERE ma.activitytype=mat.activitytypeid AND ma.activitystatusid=mas.activitystatusid AND ma.ProgressStatus>0 AND projectid=:projectid";
	private static final String MILESTONES="CALL Pfms_Milestone_Level_Prior(:projectid,:committeeid)";
	private static final String EBANDPMRCCOUNT="SELECT c.CommitteeShortName, COUNT(scheduleid) AS 'COUNT',MAX(scheduledate) AS scheduledate FROM committee_schedule a, committee_meeting_status b, committee c \r\n"
			+ "WHERE a.CommitteeId = c.CommitteeId AND a.ScheduleFlag=b.MeetingStatus AND MeetingStatusId>6 AND scheduledate<=CURDATE() AND c.IsBriefing='Y' AND a.IsActive=1\r\n"
			+ "AND CASE WHEN a.ProjectId=0 THEN a.ProgrammeId IN (SELECT pp.programmeid FROM pfms_programme_projects pp WHERE pp.projectid =:projectid AND pp.IsActive=1) ELSE a.ProjectId =:projectid END\r\n"
			+ "GROUP BY c.CommitteeShortName";
	private static final String PROJECTATTRIBUTES="SELECT pm.projectcode, pm.projectname, pm.ProjectDescription, pm.sanctiondate, pm.objective, pm.deliverable, pm.pdc,   ROUND(pm.TotalSanctionCost/100000,2) AS 'TotalSanctionCost',   ROUND(pm.SanctionCostRE/100000,2) AS 'SanctionCostRE', ROUND(pm.SanctionCostFE/100000,2) AS 'SanctionCostFE', pm.WorkCenter, pm.projectcategory,pc.classification,  pm.projecttype AS 'projecttypeid',pt.projecttype ,pma.labparticipating,pm.EndUser,pm.Scope,pm.UnitCode  FROM project_master pm, pfms_security_classification pc, project_type pt , project_main pma  WHERE pm.projectcategory=pc.classificationid AND pm.projecttype=pt.projecttypeid AND pm.projectmainid=pma.projectmainid AND projectid=:projectid";
	private static final String PROJECTDATADETAILS="SELECT ppd.projectdataid,ppd.projectid,ppd.filespath,ppd.systemconfigimgname,ppd.SystemSpecsFileName,ppd.ProductTreeImgName,ppd.PEARLImgName,ppd.CurrentStageId,ppd.RevisionNo,pps.projectstagecode,pps.projectstage,pps.stagecolor,pm.projectcode,ppd.proclimit/100000  FROM pfms_project_data ppd, pfms_project_stage pps,project_master pm WHERE ppd.projectid=pm.projectid AND ppd.CurrentStageId=pps.projectstageid AND ppd.projectid=:projectid";
	private static final String PROCUREMETSSTATUSLIST="SELECT f.PftsFileId, f.DemandNo, f.OrderNo, f.DemandDate, f.DpDate, ROUND(f.EstimatedCost/100000,2) AS 'EstimatedCost',ROUND(f.OrderCost/100000, 2) AS 'OrderCost', f.PDRDate ,f.ItemNomenclature, s.PftsStatus, s.PftsStageName, f.Remarks,'' AS vendorname,f.PftsStatusId,f.PDC, f.IntegrationDate,f.spcdate   FROM pfts_file f, pfts_status s  WHERE f.ProjectId=:projectid AND f.isactive='1' AND f.EstimatedCost> (SELECT ppd.proclimit FROM pfms_project_data ppd WHERE ppd.ProjectId=f.ProjectId )   AND f.PftsStatusId=s.PftsStatusId AND f.PftsFileId NOT IN(SELECT PftsFileId FROM pfts_file_order) UNION SELECT f.PftsFileId, f.DemandNo, o.OrderNo, f.DemandDate, o.DpDate, ROUND(f.EstimatedCost/100000,2) AS 'EstimatedCost',ROUND(o.OrderCost/100000, 2) AS 'OrderCost', f.PDRDate ,f.ItemNomenclature, s.PftsStatus, s.PftsStageName, f.Remarks,o.vendorname,f.PftsStatusId,f.PDC, f.IntegrationDate,o.orderdate FROM pfts_file f, pfts_status s,pfts_file_order o  WHERE f.ProjectId=:projectid AND f.isactive='1' AND o.isactive='1' AND f.PftsFileId=o.PftsFileId  AND f.PftsStatusId=s.PftsStatusId AND o.OrderCost>(SELECT ppd.proclimit FROM pfms_project_data ppd WHERE ppd.ProjectId=f.ProjectId ) ORDER BY  DemandNo , PftsStatusId ASC";
	private static final String RISKMATIRXDATA="SELECT pr.description, pr.severity, pr.probability, pr.mitigationplans, pr.revisionno,pr.projectid,pr.actionmainid , CONCAT(IFNULL(CONCAT(e.title,' '),''), e.empname) AS 'empname' ,ed.designation,asi.pdcorg, asi.pdc1,asi.pdc2,asi.revision, asi.actionno, am.actiondate,asi.actionstatus,asi.actionstatus AS 'actionflag',asi.enddate ,asi.progress,asi.progressremark,asi.progressdate,pr.impact,pr.RPN, pr.category, prt.riskcode,asi.actionassignid FROM pfms_risk pr, action_main am, employee e, employee_desig ed , action_assign asi ,pfms_risk_type prt WHERE prt.risktypeid=pr.risktypeid  AND pr.status='O' AND am.actionmainid=asi.actionmainid AND asi.actionmainid= pr.actionmainid AND asi.assignee = e.empid AND e.desigid=ed.desigid AND asi.assigneelabcode<>'@EXP' AND pr.projectid=:projectid UNION SELECT pr.description, pr.severity, pr.probability, pr.mitigationplans, pr.revisionno,pr.projectid,pr.actionmainid ,CONCAT(IFNULL(CONCAT(e.title,' '),''), e.expertname) AS 'empname' ,'Expert' AS 'designation',asi.pdcorg, asi.pdc1,asi.pdc2,asi.revision, asi.actionno, am.actiondate,asi.actionstatus,asi.actionstatus AS 'actionflag',asi.enddate ,asi.progress,asi.progressremark,asi.progressdate,pr.impact,pr.RPN, pr.category, prt.riskcode,asi.actionassignid FROM pfms_risk pr, action_main am, expert e,  action_assign asi ,pfms_risk_type prt WHERE prt.risktypeid=pr.risktypeid  AND pr.status='O' AND am.actionmainid=asi.actionmainid AND asi.actionmainid= pr.actionmainid AND asi.assignee = e.Expertid AND asi.assigneelabcode='@EXP' AND   pr.projectid=:projectid";
	private static final String LASTPMRCDECISIONS="SELECT cs.pmrcdecisions,cs.scheduleid,cs.scheduledate FROM committee_schedule cs ,committee_meeting_status cms  WHERE cs.committeeid=:committeeid AND cs.isactive=1 AND cs.projectid=:projectid AND cs.scheduleflag=cms.meetingstatus AND cms.meetingstatusid>=7 ORDER BY cs.scheduledate DESC LIMIT 1";
	private static final String MILESTONESCHANGE="SELECT ma.milestoneactivityid,ma.projectid,ma.milestoneno,ma.activityname,ma.orgstartdate,ma.orgenddate,ma.startdate,ma.enddate, ma.activitytype AS 'activitytypeid' ,mat.activitytype,ma.activitystatusid,mas.activityshort, ma.ProgressStatus,IFNULL(ma.StatusRemarks,'-') AS 'Status Remarks' FROM milestone_activity ma, milestone_activity_type mat ,milestone_activity_status mas WHERE ma.activitytype=mat.activitytypeid AND ma.activitystatusid=mas.activitystatusid AND projectid=:projectid AND CASE WHEN :milestoneactivitystatusid='A' THEN 1=1 ELSE ma.activitystatusid =:milestoneactivitystatusid END  AND ma.progressstatus <> 0";
	
	
	@PersistenceContext
	EntityManager manager;
	
	@Autowired
	Environment env;
	
	private static final Logger logger=LogManager.getLogger(PrintDaoImpl.class);
	
	@Override
	public Object[] LabList(String LabCode) throws Exception {

		Query query=manager.createNativeQuery(LABLIST);
		query.setParameter("labcode", LabCode);
		Object[] LabList=(Object[])query.getSingleResult();		

		return LabList;
	}

	@Override
	public List<Object[]> PfmsInitiationList(String InitiationId) throws Exception {
		
		Query query=manager.createNativeQuery(PFMSINITLIST);
		query.setParameter("initiationid", Long.parseLong(InitiationId));		
		List<Object[]> PfmsInitiationList=(List<Object[]>)query.getResultList();		

		return PfmsInitiationList;
	}
	private static final String COSTBREAK="SELECT SUM(a.itemcost),a.budgetsancid , b.refe,b.headofaccounts,b.majorhead FROM pfms_initiation_cost a , budget_item_sanc b WHERE a.isactive=1 AND a.budgetsancid=b.sanctionitemid AND a.initiationid=:initiationid AND b.projecttypeid=:projecttypeid GROUP BY budgetsancid";
	@Override
	public List<Object[]> GetCostBreakList(String InitiationId , String projecttypeid)throws Exception
	{
		Query query=manager.createNativeQuery(COSTBREAK);
		query.setParameter("initiationid", Long.parseLong(InitiationId));
		query.setParameter("projecttypeid", Integer.parseInt(projecttypeid));
		List<Object[]> PfmscostbreakList=(List<Object[]>)query.getResultList();		

		return PfmscostbreakList;
	}
	
	@Override
	public LabMaster LabDetailes(String LabCode) throws Exception {
		logger.info(new Date() +"Inside LabDetailes");
//		LabMaster LabDetailes=manager.find(LabMaster.class, 1);
	
		CriteriaBuilder cb= manager.getCriteriaBuilder();
		CriteriaQuery<LabMaster> cq= cb.createQuery(LabMaster.class);
		Root<LabMaster> root= cq.from(LabMaster.class);					
		Predicate p1=cb.equal(root.get("LabCode") , LabCode);
		cq=cq.select(root).where(p1);
		TypedQuery<LabMaster> allquery = manager.createQuery(cq);
		LabMaster lab= allquery.getResultList().get(0);
		
		
		return lab;
	}
	
	
	
	@Override
	public List<Object[]> ProjectIntiationDetailsList(String InitiationId) throws Exception {
		
		Query query=manager.createNativeQuery(PROJECTDETAILSLIST);
		query.setParameter("initiationid", Long.parseLong(InitiationId));
		List<Object[]> ProjectIntiationDetailsList=(List<Object[]> )query.getResultList();	
			
		
			return ProjectIntiationDetailsList;
	}

	@Override
	public List<Object[]> CostDetailsList(String InitiationId) throws Exception {

		Query query=manager.createNativeQuery(COSTDETAILSLIST);
		query.setParameter("initiationid", Long.parseLong(InitiationId));
		List<Object[]> CostDetailsList =(List<Object[]>)query.getResultList();
		
		return CostDetailsList;
	}

	@Override
	public List<Object[]> ProjectInitiationScheduleList(String InitiationId) throws Exception {

		Query query=manager.createNativeQuery(PROJECTSCHEDULELIST);
	    query.setParameter("initiationid", Long.parseLong(InitiationId));
		List<Object[]> ProjectIntiationScheduleList=(List<Object[]>)query.getResultList();		
		
		return ProjectIntiationScheduleList;
	}
	
	
	@Override
	public List<Object[]> ProjectsList() throws Exception {

		Query query=manager.createNativeQuery(PROJECTSLIST);	   
		List<Object[]> ProjectsList=(List<Object[]>)query.getResultList();	
		
		return ProjectsList;
	}
	
	
	@Override
	public List<Object[]> LoginProjectDetailsList(String empid,String Logintype ,String LabCode)throws Exception
	{
		Query query=manager.createNativeQuery("CALL Pfms_Emp_ProjectList(:empid,:logintype,:labcode);");
		query.setParameter("empid", Long.parseLong(empid));
		query.setParameter("logintype", Logintype);
		query.setParameter("labcode", LabCode);
		List<Object[]> LoginProjectIdList=(List<Object[]>)query.getResultList();
		return LoginProjectIdList;
	}
	
	
		
	@Override
	public Object[] ProjectAttributes(String projectid) throws Exception 
	{
		Query query=manager.createNativeQuery(PROJECTATTRIBUTES);	   
		query.setParameter("projectid", Long.parseLong(projectid));
		Object[] ProjectAttributes=null;
		try {
			ProjectAttributes=(Object[])query.getSingleResult();	
		}catch (NoResultException e) {
			logger.error(new Date() +" Inside DAO ProjectAttributes "+ e);
			ProjectAttributes=null;
		}catch (Exception e) {
			logger.error(new Date() +" Inside DAO ProjectAttributes "+ e);
			ProjectAttributes=null;
		}
		return ProjectAttributes;
	}
	
	
	
	
	@Override
	public List<Object[]> EBAndPMRCCount(String projectid) throws Exception {

		Query query=manager.createNativeQuery(EBANDPMRCCOUNT);	   
		query.setParameter("projectid", Long.parseLong(projectid));
		List<Object[]> EBAndPMRCCount=(List<Object[]>)query.getResultList();	
		
		return EBAndPMRCCount;
	}
	
	private static final String PROJECTCOMMITTEEMEETINGSCOUNT = "SELECT :CommitteeCode,COUNT(cs.scheduleid) AS 'COUNT' FROM committee_schedule cs , committee_meeting_status cms, committee c \r\n"
			+ "WHERE  cs.scheduledate<=CURDATE() AND cs.isactive=1 AND c.committeeShortname=:CommitteeCode AND cs.committeeid = c.committeeid AND cms.meetingstatus=cs.scheduleflag AND cms.meetingstatusid>6 \r\n"
			+ "AND ( (cs.ProjectId =:ProjectId AND cs.Divisionid=:Divisionid AND cs.InitiationId=:InitiationId AND cs.CARSInitiationId=:CARSInitiationId AND cs.ProgrammeId=:ProgrammeId) OR (cs.ProgrammeId IN (SELECT pp.programmeid FROM pfms_programme_projects pp WHERE pp.ProjectId =:ProjectId AND pp.IsActive=1)) )";
	@Override
	public Object[] ProjectCommitteeMeetingsCount(String projectId, String divisionId, String initiationId, String carsInitiationId, String programmeId, String committeeCode) throws Exception 
	{

		Query query=manager.createNativeQuery(PROJECTCOMMITTEEMEETINGSCOUNT);	   
		query.setParameter("ProjectId", Long.parseLong(projectId));
		query.setParameter("Divisionid", Long.parseLong(divisionId));
		query.setParameter("InitiationId", Long.parseLong(initiationId));
		query.setParameter("CARSInitiationId", Long.parseLong(carsInitiationId));
		query.setParameter("ProgrammeId", Long.parseLong(programmeId));
		query.setParameter("CommitteeCode", committeeCode);
		Object[] EBAndPMRCCount=(Object[])query.getSingleResult();	
		
		return EBAndPMRCCount;
	}
	
	
	
	
	@Override
	public List<Object[]> Milestones(String projectid, String committeeid) throws Exception {

		Query query=manager.createNativeQuery(MILESTONES);	   
		query.setParameter("projectid", Long.parseLong(projectid));
		query.setParameter("committeeid", Long.parseLong(committeeid));
		List<Object[]> Milestones=(List<Object[]>)query.getResultList();	
		
		return Milestones;
	}
	
	
	@Override
	public List<Object[]> Milestones(String projectid, String committeeid, String Date) throws Exception {
		String milestones ="CALL Pfms_Milestone_Level_Prior_meeting(:projectid,:committeeid,:date)";
		Query query=manager.createNativeQuery(milestones);	   
		query.setParameter("projectid", Long.parseLong(projectid));
		query.setParameter("committeeid", Long.parseLong(committeeid));
		query.setParameter("date", Date);
		List<Object[]> Milestones=(List<Object[]>)query.getResultList();	
		
		return Milestones;
	}
	
	@Override
	public List<Object[]> MilestonesChange(String projectid,String milestoneactivitystatusid) throws Exception {

		Query query=manager.createNativeQuery(MILESTONESCHANGE);	   
		query.setParameter("projectid", Long.parseLong(projectid));
		query.setParameter("milestoneactivitystatusid", milestoneactivitystatusid);
		List<Object[]> MilestonesChange=(List<Object[]>)query.getResultList();	
		
		return MilestonesChange;
	}
	
	@Override /* present status*/
	public List<Object[]> MilestoneSubsystems(String projectid) throws Exception {

		Query query=manager.createNativeQuery(MILESTONESUBSYSTEMS);	   
		query.setParameter("projectid", Long.parseLong(projectid));
		List<Object[]> MilestoneSubsystems=(List<Object[]>)query.getResultList();	
		
		return MilestoneSubsystems;
	}
	
	@Override /* last Pmrc action points*/
	public List<Object[]> LastPMRCActions(String projectid ,String committeeid) throws Exception 
	{
		Query query=manager.createNativeQuery("CALL Last_PMRC_Actions_List(:projectid,:committeeid);");	   
		query.setParameter("projectid", Long.parseLong(projectid));
		query.setParameter("committeeid", Long.parseLong(committeeid));
		//query.setParameter("lastpmrcdate", lastpmrcdate);
		List<Object[]> LastPMRCActions=(List<Object[]>)query.getResultList();			
		return LastPMRCActions;		
	}
	
	@Override /* old Pmrc action points*/
	public List<Object[]> OldPMRCActions(String projectid, String committeeid) throws Exception 
	{
		Query query=manager.createNativeQuery("CALL Old_PMRC_Actions_List(:projectid,:committeeid);");	   
		query.setParameter("projectid", Long.parseLong(projectid));
		query.setParameter("committeeid", Long.parseLong(committeeid));
		List<Object[]> OldPMRCActions=(List<Object[]>)query.getResultList();			
		return OldPMRCActions;		
	}
	
	@Override
	public List<Object[]> ProjectDetails(String ProjectId) throws Exception {
		Query query=manager.createNativeQuery(PROJECTDETAILS);
		query.setParameter("projectid", Long.parseLong(ProjectId));
		List<Object[]> ProjectList=(List<Object[]>)query.getResultList();		

		return ProjectList;
	}
	
	
	@Override
	public List<Object[]> GanttChartList(String ProjectId) throws Exception {
		
		Query query = manager.createNativeQuery(GANTTCHARTLIST);
		query.setParameter("projectid", Long.parseLong(ProjectId));
		List<Object[]> GanttChartList= query.getResultList();
		return GanttChartList;
	}
	
	@Override
	public Object[] ProjectDataDetails(String projectid) throws Exception {
		Query query=manager.createNativeQuery(PROJECTDATADETAILS);
		query.setParameter("projectid", Long.parseLong(projectid));
		Object[] ProjectStageDetails=null;
		try {
			ProjectStageDetails=(Object[])query.getSingleResult();
		}catch (Exception e) {
			logger.error(new Date() +" Inside DAO ProjectDataDetails "+ e);
			return null;
		}
		
		return ProjectStageDetails;
	}
	
	
	@Override   //unfinished or open issues only
	public List<Object[]> OldPMRCIssuesList(String projectid) throws Exception {  
		
		Query query = manager.createNativeQuery("CALL Old_Issues_List(:projectid);");
		query.setParameter("projectid", Long.parseLong(projectid));
		List<Object[]> OldPMRCIssuesList= query.getResultList();
		return OldPMRCIssuesList;
	}
	
	
	@Override
	public List<Object[]> ProcurementStatusList(String projectid)throws Exception{
		Query query = manager.createNativeQuery(PROCUREMETSSTATUSLIST);
		query.setParameter("projectid", Integer.parseInt(projectid));
		List<Object[]> ProcurementStatusList= query.getResultList();
		return ProcurementStatusList;
	}
	
	
	
	@Override
	public List<Object[]> RiskMatirxData(String projectid)throws Exception{
		Query query = manager.createNativeQuery(RISKMATIRXDATA);
		query.setParameter("projectid", Long.parseLong(projectid));
		List<Object[]> RiskMatirxData= query.getResultList();
		return RiskMatirxData;
		
	}
	
	
	
	@Override
	public Object[] LastPMRCDecisions(String committeeid,String projectid)throws Exception
	{
		Query query = manager.createNativeQuery(LASTPMRCDECISIONS);
		query.setParameter("projectid", Long.parseLong(projectid));
		query.setParameter("committeeid", Long.parseLong(committeeid));
		
		Object[] LastPMRCDecisions=null;
		try {
			LastPMRCDecisions= (Object[])query.getSingleResult();
		}catch (Exception e) {
			logger.error(new Date() +" Inside DAO LastPMRCDecisions "+ e);
			return LastPMRCDecisions;
		}		
		return LastPMRCDecisions;		
	}
	
	@Override
	public List<Object[]> ActionPlanSixMonths(String projectid, int interval)throws Exception
	{
		List<Object[]> ActionPlanThreeMonths=new ArrayList<Object[]>();
		//Query query = manager.createNativeQuery("CALL Pfms_Milestone_PDC(:projectid, 180);");
		Query query = manager.createNativeQuery("CALL Pfms_Milestone_PDC_New(:projectid, :interval)");
		query.setParameter("projectid", Long.parseLong(projectid));
		query.setParameter("interval", interval);
		try {
			ActionPlanThreeMonths= query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside DAO ActionPlanSixMonths "+ e);
		}
		return ActionPlanThreeMonths;
	}

	
	private static final String LASTPRMC="SELECT cs.scheduleid FROM  committee_schedule cs ,committee_meeting_status ms WHERE cs.committeeid=:committeeid AND cs.projectid=:projectid AND cs.isactive=1 AND  ms.meetingstatus=cs.scheduleflag AND ms.meetingstatusid > 6 AND cs.scheduleid<:scheduleId ORDER BY cs.scheduleid DESC LIMIT 1";
	@Override
	public long getLastPmrcId(String projectid,String committeeid,String scheduleId) throws Exception 
	{
		try {
			Query query=manager.createNativeQuery(LASTPRMC);
			query.setParameter("projectid",  Long.parseLong(projectid));
			query.setParameter("committeeid", Long.parseLong(committeeid));
			query.setParameter("scheduleId",  Long.parseLong(scheduleId));
			return (Long)query.getSingleResult();
		}catch (Exception e) {
			logger.error(new Date() +" Inside DAO getLastPmrcId "+ e);
			e.printStackTrace();
			return 0;
		}
	}
	@Override
	public List<Object[]> LastPMRCActions1(String projectid ,String committeeid) throws Exception 
	{
		Query query=manager.createNativeQuery("CALL last_pmrc_actions_list_bpaper(:projectid,:committeeid);");	   
		query.setParameter("projectid", Long.parseLong(projectid));
		query.setParameter("committeeid", Long.parseLong(committeeid));
		List<Object[]> LastPMRCActions=(List<Object[]>)query.getResultList();			
		return LastPMRCActions;		
	}
	
	private static final String PROJECTSUBPROJECTIDLIST ="SELECT CAST(a.projectid AS CHAR) FROM project_master a,project_master b WHERE b.projectid=:projectid AND  CASE WHEN b.ismainwc = 1 THEN a.projectid IN (SELECT projectid FROM project_master WHERE projectmainid= b.projectmainid AND isactive=1) WHEN  b.ismainwc = 0 THEN a.projectid=b.projectid END ORDER BY a.projectid=:projectid DESC  ";
	
	@Override
	public List<String> ProjectsubProjectIdList(String projectid ) throws Exception 
	{
		Query query=manager.createNativeQuery(PROJECTSUBPROJECTIDLIST);	   
		query.setParameter("projectid", Long.parseLong(projectid));
		List<String> Projectidlist=(List<String>)query.getResultList();			
		return Projectidlist;		
	}
	
	//private static final String REVIEWMEETINGLIST = "SELECT scheduleid, committeeshortname, committeename,scheduledate,meetingid FROM committee_schedule cs, committee c, committee_meeting_status cms WHERE (cs.scheduledate BETWEEN (SELECT MAX(cs.scheduledate) FROM committee_schedule cs, committee_meeting_status cms WHERE cs.committeeid=:committeeid AND cs.projectid=:projectid AND cs.scheduledate < CURDATE() AND cs.scheduleflag=cms.meetingstatus AND meetingstatusid > 6)   AND CURDATE() )  AND cs.committeeid=c.committeeid AND cs.scheduleflag=cms.meetingstatus AND cms.meetingstatusid > 6 AND cs.projectid=:projectid AND cs.committeeid <> :committeeid ";
	
	private static final String REVIEWMEETINGLIST ="SELECT cs.scheduleid, c.committeeshortname, c.committeename, cs.scheduledate, cs.meetingid\r\n"
			+ "FROM committee_schedule cs JOIN committee c ON cs.committeeid = c.committeeid JOIN committee_meeting_status cms ON cs.scheduleflag = cms.meetingstatus\r\n"
			+ "WHERE cms.meetingstatusid > 6 AND c.committeeShortName = :committeecode AND cs.scheduledate <= CURDATE()\r\n"
			+ "    AND ( cs.projectid = :projectid OR cs.programmeid IN (SELECT pp.programmeid FROM pfms_programme_projects pp WHERE pp.projectid = :projectid AND pp.IsActive=1))\r\n"
			+ "ORDER BY cs.scheduledate";
	@Override
	public List<Object[]> ReviewMeetingList(String projectid, String committeecode) throws Exception 
	{
		Query query=manager.createNativeQuery(REVIEWMEETINGLIST);	   
		query.setParameter("projectid", Long.parseLong(projectid));
		query.setParameter("committeecode", committeecode);
		List<Object[]> ReviewMeetingList=(List<Object[]>)query.getResultList();		
		return ReviewMeetingList;
	}
	
	private static final String TECHWORKDATALIST = "SELECT a.techdataid, a.projectid,a.relatedpoints, a.attachmentid, a.isactive,'0',b.filepath,b.filenameui,b.filename,b.filepass,b.ReleaseDoc,b.VersionDoc FROM project_technical_work_data a,file_rep_upload b WHERE a.isactive=1 AND a.projectid=:projectid and a.attachmentid=b.filerepuploadid union SELECT a.techdataid, a.projectid,a.relatedpoints, a.attachmentid, a.isactive,'0','' AS filepath,'' AS filenameui,'' AS filename,'' AS filepass,0 AS ReleaseDoc,0 AS VersionDoc FROM project_technical_work_data a WHERE a.isactive=1 AND a.projectid=:projectid AND a.attachmentid=0";
	
	@Override
	public Object[] TechWorkData(String projectid) throws Exception 
	{
		Query query=manager.createNativeQuery(TECHWORKDATALIST);	   
		query.setParameter("projectid", Long.parseLong(projectid));
		List<Object[]> ReviewMeetingList=(List<Object[]>)query.getResultList();	
		if(ReviewMeetingList.size()>0) 
		{
			return ReviewMeetingList.get(0);
		}else {
			return null;
		}
	 
	}
	
	private static final String PROJECTREVLIST = "SELECT pr.projectid, pr.revisionno,pm.projectcode AS 'ProjectMainCode', pr.projectcode, pr.projectname, pr.projectdescription, pr.unitcode, pt.projecttype,ps.classification,pr.sanctionno,pr.sanctiondate, CASE WHEN pr.totalsanctioncost>0 THEN ROUND(pr.totalsanctioncost/100000,2) ELSE pr.totalsanctioncost END AS 'TotalSanctionCost', pr.pdc, e.empname AS 'ProjectDirector' , ed.designation,  CAST(pr.createddate AS DATE),CASE WHEN pr.SanctionCostFE>0 THEN ROUND(pr.SanctionCostFE/100000,2) ELSE pr.SanctionCostFE END AS 'SanctionCostFE',CASE WHEN pr.SanctionCostRE>0 THEN ROUND(pr.SanctionCostRE/100000,2) ELSE pr.SanctionCostRE END AS 'SanctionCostRE'     FROM project_master p, project_master_rev pr , project_main pm, project_type pt, pfms_security_classification ps, employee e, employee_desig ed WHERE p.projectid=pr.projectid AND pr.projectmainid = pm.projectmainid AND pr.projecttype=pt.projecttypeid AND ps.classificationid =pr.projectcategory AND e.empid=pr.projectdirector AND e.desigid=ed.desigid  AND p.projectid=:projectid ORDER BY  revisionno ASC";
	
	@Override
	public List<Object[]> ProjectRevList(String projectid) throws Exception {
		Query query=manager.createNativeQuery(PROJECTREVLIST);
		query.setParameter("projectid", Long.parseLong(projectid));
		return (List<Object[]>)query.getResultList();
	}

	private static final String SCHEDULELIST="SELECT cs.scheduledate,cs.scheduleflag,cms.meetingstatusid,cs.meetingid,cs.scheduledate<DATE(SYSDATE()),(SELECT COUNT(*)+1 FROM  committee_schedule css ,committee_meeting_status mss WHERE css.committeeid=c.committeeid AND css.projectid=cs.projectid AND css.isactive=1 AND  mss.meetingstatus=css.scheduleflag AND mss.meetingstatusid > 6 AND css.scheduledate<cs.scheduledate) AS countid,cs.scheduleid FROM committee_schedule cs, committee c,committee_meeting_status cms WHERE c.committeeid=cs.committeeid  AND c.committeeShortName IN ('PMRC','EB') AND cs.scheduleflag=cms.meetingstatus AND cs.projectid=:ProjectId AND YEAR(cs.scheduledate)=:InYear AND MONTH(cs.scheduledate)=:InMonth AND cms.meetingstatusid > 6";
	
	@Override
	public List<Object[]> getMeetingSchedules(String ProjectId, String Month, String Year) throws Exception {
		Query query=manager.createNativeQuery(SCHEDULELIST);
		query.setParameter("ProjectId", Long.parseLong(ProjectId));
		query.setParameter("InMonth", Month);
		query.setParameter("InYear", Year);
		return (List<Object[]>)query.getResultList();
	}
	
	private static final String COMMITTEESCHEDULEEDITDATA="SELECT a.committeeid,a.committeemainid,a.scheduledate,a.schedulestarttime,a.scheduleflag,a.schedulesub,a.scheduleid,b.committeename,b.committeeshortname,a.projectid,c.meetingstatusid,a.meetingid,a.meetingvenue,a.confidential,a.Reference,d.classification ,a.divisionid  ,a.initiationid ,a.pmrcdecisions,a.kickoffotp ,(SELECT minutesattachmentid FROM committee_minutes_attachment WHERE scheduleid=a.scheduleid) AS 'attachid', b.periodicNon,a.MinutesFrozen,a.briefingpaperfrozen,a.labcode, a.ProgrammeId, a.ScheduleType FROM committee_schedule a,committee b ,committee_meeting_status c, pfms_security_classification d WHERE a.scheduleflag=c.MeetingStatus AND a.scheduleid=:committeescheduleid AND a.committeeid=b.committeeid AND a.confidential=d.classificationid";

	
	@Override
	public Object[] CommitteeScheduleEditData(String CommitteeScheduleId) throws Exception {

		Query query=manager.createNativeQuery(COMMITTEESCHEDULEEDITDATA);
		query.setParameter("committeescheduleid", Long.parseLong(CommitteeScheduleId));
		Object[] CommitteeScheduleEditData=(Object[])query.getSingleResult();
		return CommitteeScheduleEditData;
	}
	
//	private static final String NEXTSCHEDULEID="SELECT cs.scheduleid FROM  committee_schedule cs ,committee_meeting_status ms WHERE cs.committeeid=:committeeid AND cs.projectid=:projectid AND cs.isactive=1 AND  ms.meetingstatus=cs.scheduleflag AND ms.meetingstatusid < 7  ORDER BY cs.scheduledate ASC LIMIT 1";	
	private static final String NEXTSCHEDULEID="  SELECT cs.scheduleid FROM  committee_schedule cs WHERE cs.committeeid=:committeeid AND cs.projectid=:projectid AND cs.isactive=1  AND  scheduledate >= CURDATE() ORDER BY  scheduledate LIMIT 1";	
    @Override
	public long getNextScheduleId(String projectid,String committeeid) throws Exception 
	{
		try {
			Query query=manager.createNativeQuery(NEXTSCHEDULEID);
			query.setParameter("projectid",  Long.parseLong(projectid));
			query.setParameter("committeeid",  Long.parseLong(committeeid));
			return (Long)query.getSingleResult();
		}catch (Exception e) {
			logger.error(new Date() +" Inside DAO getNextScheduleId "+ e);
			e.printStackTrace();
			return 0;
		}
		
	}
    
    private static final String NEXTSCHEDULEFROZEN="SELECT briefingpaperfrozen FROM committee_schedule where scheduleid=:schduleid ";	
    @Override
	public String getNextScheduleFrozen(long schduleid) throws Exception 
	{
		Query query=manager.createNativeQuery(NEXTSCHEDULEFROZEN);
		query.setParameter("schduleid", schduleid);
		Object result = query.getSingleResult();
	    return result != null ? result.toString() : null;
	}

    private static final String UPDATEFROZEN="update committee_schedule set BriefingPaperFrozen='Y',PresentationFrozen='Y',MinutesFrozen='Y' where scheduleid=:schduleid";
	@Override
	public int updateBriefingPaperFrozen(long schduleid) throws Exception {
		
		CommitteeSchedule ExistingCommitteeSchedule = manager.find(CommitteeSchedule.class, schduleid);
		if(ExistingCommitteeSchedule != null) {
			ExistingCommitteeSchedule.setBriefingPaperFrozen("Y");
			ExistingCommitteeSchedule.setPresentationFrozen("Y");
			ExistingCommitteeSchedule.setMinutesFrozen("Y");
			return 1;
		}
		else {
			return 0;
		}
		
	}
	
	@Override
	public int updateBriefingPaperFrozen(long schduleid,String BriefingPaperFrozen, String PresentationFrozen, String MinutesFrozen) throws Exception {
		
		int count=0;
		if(BriefingPaperFrozen.equalsIgnoreCase("Y")) {
			CommitteeSchedule ExistingCommitteeSchedule = manager.find(CommitteeSchedule.class, schduleid);
			if(ExistingCommitteeSchedule != null) {
				ExistingCommitteeSchedule.setBriefingPaperFrozen("Y");
				count=1;
			}
			
		}
		 if(PresentationFrozen.equalsIgnoreCase("Y")) {
			 
			 CommitteeSchedule ExistingCommitteeSchedule = manager.find(CommitteeSchedule.class, schduleid);
				if(ExistingCommitteeSchedule != null) {
					ExistingCommitteeSchedule.setPresentationFrozen("Y");
					count=1;
				}
	
		}
		 if(MinutesFrozen.equalsIgnoreCase("Y")) {
			 
			 CommitteeSchedule ExistingCommitteeSchedule = manager.find(CommitteeSchedule.class, schduleid);
				if(ExistingCommitteeSchedule != null) {
					ExistingCommitteeSchedule.setMinutesFrozen("Y");
					count=1;
				}
			
		}
		return count;
	}
	
	
	
	private static final String MILESTONEACTIVITYSTATUS="SELECT activitystatusid,activitystatus,activityshort from milestone_activity_status ";
	
	@Override
	public List<Object[]> MilestoneActivityStatus() throws Exception {
		Query query=manager.createNativeQuery(MILESTONEACTIVITYSTATUS);
		
		return (List<Object[]>)query.getResultList();
	}
	
	private static final String GETPROJECTSANLIST="SELECT initiationid, projectshortname FROM pfms_initiation";
	@Override
	public List<Object[]> GetProjectInitiationSanList() throws Exception 
	{
		Query query = manager.createNativeQuery(GETPROJECTSANLIST);
		
		return  (List<Object[]>) query.getResultList();

	}
	private static final String MILESTONELEVELID="SELECT levelid,levelconfigurationid FROM milestone_activity_level_configuration WHERE projectid=:projectid AND committeeid=:committeeid";
	
	@Override 
	public Object[] MileStoneLevelId(String ProjectId, String Committeeid) throws Exception{

		try {
			Query query=manager.createNativeQuery(MILESTONELEVELID);
			query.setParameter("projectid", Long.parseLong(ProjectId));
			query.setParameter("committeeid", Long.parseLong(Committeeid));
			Object[] MileStoneLevelId = (Object[]) query.getSingleResult();
			return MileStoneLevelId;
	
		} catch(NoResultException e) {
			logger.error(new Date() +" Inside DAO MileStoneLevelId "+ e);
			return null;
		}	
	}
	
	@Override 
	public Long MilestoneLevelInsert(MilestoneActivityLevelConfiguration mod) throws Exception{

		manager.persist(mod);
		manager.flush();
	
		return mod.getLevelConfigurationId();
	}
	
	
	
	@Override 
	public Long MilestoneLevelUpdate(MilestoneActivityLevelConfiguration mod) throws Exception{

		MilestoneActivityLevelConfiguration ExistingMilestoneActivityLevelConfiguration= manager.find(MilestoneActivityLevelConfiguration.class, mod.getLevelConfigurationId());
		if(ExistingMilestoneActivityLevelConfiguration != null)
		{
			ExistingMilestoneActivityLevelConfiguration.setLevelid(mod.getLevelid());
			ExistingMilestoneActivityLevelConfiguration.setModifiedBy(mod.getModifiedBy());
			ExistingMilestoneActivityLevelConfiguration.setModifiedDate(mod.getModifiedDate());
			return 1L;
		}
		else {
			 return 0L;
		}
		
	}
	
	@Override 
	public List<Object[]> BreifingMilestoneDetails(String Projectid, String committeeid) throws Exception{

		Query query=manager.createNativeQuery("CALL Pfms_Milestone_Level_Prior(:projectid,:committeeid)");
		query.setParameter("projectid", Long.parseLong(Projectid));
		query.setParameter("committeeid", Long.parseLong(committeeid));
		return (List<Object[]>) query.getResultList();
	}

	private static final String PROJECTINITIATIONDATA="SELECT a.projecttitle  , a.indicativecost , a.projectcost , a.fecost , a.recost ,a.ismain ,a.deliverable , a.pcduration,a.isplanned,b.objective  , b.scope , c.labname ,a.projecttypeid FROM pfms_initiation a , pfms_initiation_detail b , cluster_lab c WHERE a.initiationid = b.initiationid and a.nodallab=c.labid and a.initiationid=:initiationid";
	@Override
	public Object[] GetProjectInitiationdata(String projectid) throws Exception {
		
			Query query=manager.createNativeQuery(PROJECTINITIATIONDATA);	   
			query.setParameter("initiationid", Long.parseLong(projectid));
			Object[] ProjectAttributes=null;
			try {
				ProjectAttributes=(Object[])query.getSingleResult();	
			}catch (Exception e) {
				logger.error(new Date() +" Inside DAO GetProjectInitiationdata "+ e);
				ProjectAttributes=null;
			}
			return ProjectAttributes;
		
	}
	
	private static final String ITEMLIST="SELECT a.minorhead, a.headofaccounts , b.itemdetail , b.itemcost  , c.budgetheaddescription FROM budget_item_sanc a,pfms_initiation_cost b ,budget_head c WHERE a.budgetheadid = b.budgetheadid AND a.budgetheadid=c.budgetheadid  AND b.budgetsancid=a.sanctionitemid AND b.initiationid=:initiationid";
	                    
	@Override
	public List<Object[]> GetItemList(String projectid)throws Exception
	{
		Query query = manager.createNativeQuery(ITEMLIST);
		query.setParameter("initiationid", Long.parseLong(projectid));
		return  (List<Object[]>) query.getResultList();
	}
	
	private static final String AUTHORITYLIST="SELECT authorityid , authorityname FROM initiation_authority";
	@Override
	public List<Object[]> GetAuthorityList()throws Exception
	{
		Query query = manager.createNativeQuery(AUTHORITYLIST);
		return (List<Object[]>)query.getResultList();
	}
	private static final String COPYADDR="SELECT  copyaddrid ,copyname, copyaddr , remarks FROM initiation_copyaddr";
	@Override
	public List<Object[]> GetinitiationCopyAddr() throws Exception
	{
		Query query = manager.createNativeQuery(COPYADDR);
		return (List<Object[]>)query.getResultList();
	}
	
	private static final String INITIATIONDEPT="SELECT deptaddrid, department , address , city , pin FROM initiation_dept_addr";
	@Override
	public List<Object[]> GetinitiationDeptList ()throws Exception
	{
		Query query = manager.createNativeQuery(INITIATIONDEPT);
		return (List<Object[]>)query.getResultList();
	}
	
	@Override
	public Long AddInitiationSanction(InitiationSanction initiationsac) throws Exception{
		manager.persist(initiationsac);
		manager.flush();
		return initiationsac.getInitiationSanctionId();
	}
	@Override
	public Long AddCopyAddress(InitiationsanctionCopyAddr copyaddress) throws Exception
	{	
		manager.persist(copyaddress);
		manager.flush();
		return copyaddress.getInitiationSanctionCopyId();
	}
	private static final String INITIATIONSANCTIONDATA="SELECT a.initiationid ,a.rdno , b.authorityname , c.department AS 'todepartment' , c.address AS 'toaddress' , c.city AS 'tocity' , c.pin  AS 'topin',d.department AS 'fromdepartment' , d.address AS 'fromaddress' , d.city AS 'fromcity' , d.pin AS 'frompin' ,a.startdate ,a.estimatefund , a.uac ,a.rddate ,a.videno , a.fromdate ,a.fromdeptid ,a.todeptid ,a.authorityid ,a.initiationsanctionid FROM  initiation_sanction a ,initiation_authority b ,initiation_dept_addr c , initiation_dept_addr d WHERE a.authorityid= b.authorityid AND a.fromdeptid= c.deptaddrid AND a.todeptid= c.deptaddrid  AND a.initiationid=:initiationid";
	@Override
	public Object[] GetInitiationSanctionData(String initiationId)throws Exception
	{
		Query query = manager.createNativeQuery(INITIATIONSANCTIONDATA);
		query.setParameter("initiationid", Long.parseLong(initiationId));
		List<Object[]> list =(List<Object[]>)query.getResultList();
		Object[] result = null;
		if(list!=null && list.size()>0) {
			result = list.get(0);
		}
		return result;
	}
	private static final String COPYADDRESSDATA="SELECT a.copyaddrid , a.copyname , a.copyaddr , a.remarks,b.initiationsanctioncopyid	 FROM initiation_copyaddr a , initiationsanction_copy_addr b WHERE a.copyaddrid=b.copyaddrid AND b.initiationid=:initiationid";
	@Override
	public List<Object[]> GetCopyAddressList (String initiationId)throws Exception
	{
		Query query = manager.createNativeQuery(COPYADDRESSDATA);
		query.setParameter("initiationid", Long.parseLong(initiationId));
		List<Object[]> list =(List<Object[]>)query.getResultList();
		return list;
	}
	private static final String DELETEINITIATIONSANCOPY="DELETE FROM initiationsanction_copy_addr WHERE InitiationSanctionCopyId=:initiationsancopyid";
	@Override  
	public int DeleteCopyAddress(String initiationsancopyid) throws Exception{

		Query query=manager.createNativeQuery(DELETEINITIATIONSANCOPY);
		query.setParameter("initiationsancopyid", Long.parseLong(initiationsancopyid));
		int count =(int)query.executeUpdate();
		
		return count;
	}
	
	 
		public Long EditInitiationSanction(InitiationSanction initiationsac) throws Exception{
			InitiationSanction ExistingInitiationSanction= manager.find(InitiationSanction.class, initiationsac.getInitiationSanctionId());
			if(ExistingInitiationSanction != null) {
				ExistingInitiationSanction.setRdNo(initiationsac.getRdNo());
				ExistingInitiationSanction.setAuthorityId(initiationsac.getAuthorityId());
				ExistingInitiationSanction.setFromDeptId(initiationsac.getFromDeptId());
				ExistingInitiationSanction.setFromDate(initiationsac.getFromDate());
				ExistingInitiationSanction.setToDeptId(initiationsac.getToDeptId());
				ExistingInitiationSanction.setStartDate(initiationsac.getStartDate());
				ExistingInitiationSanction.setEstimateFund(initiationsac.getEstimateFund());
				ExistingInitiationSanction.setUAC(initiationsac.getUAC());
				ExistingInitiationSanction.setRdDate(initiationsac.getRdDate());
				ExistingInitiationSanction.setVideNo(initiationsac.getVideNo());
				ExistingInitiationSanction.setModifiedBy(initiationsac.getModifiedBy());
				ExistingInitiationSanction.setModifiedDate(initiationsac.getModifiedDate());
				return 1L;
			}
			else {
				return 0L;
			}
			
		}

		@Override
		public long insertTechImage(TechImages image) throws Exception {
			manager.persist(image);
			manager.flush();
			return image.getTechImagesId();
		}
		
		
		private static final String TECHIMAGE="FROM TechImages WHERE ProjectId=:proId and IsActive=1";
		@Override
		public List<TechImages> getTechList(String proId)throws Exception
		{
			Query query = manager.createQuery(TECHIMAGE);
			query.setParameter("proId", Long.parseLong(proId));
			List<TechImages> list =(List<TechImages>)query.getResultList();
			return list;
		}
		
		@Override
		public List<Object[]> SpecialCommitteesList(String LabCode)throws Exception
		{
			
			String SPECIALCOMMITTEESLIST="SELECT committeeid,committeeshortname, committeename FROM committee WHERE isactive=1 AND LabCode=:LabCode AND IsBriefing='Y' AND isactive=1;";
			
			Query query = manager.createNativeQuery(SPECIALCOMMITTEESLIST);
			query.setParameter("LabCode", LabCode);
			List<Object[]> list =(List<Object[]>)query.getResultList();
			return list;
		}
		
		//private static final String GETCOMMITTEEDATA="FROM Committee WHERE committeeid=:committeeid";
		@Override
		public Committee getCommitteeData(String committeeid)throws Exception
		{
			try {
				return manager.find(Committee.class, Long.parseLong(committeeid));
			}catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}


		@Override
		public long FreezeBriefingAdd(CommitteeProjectBriefingFrozen briefing) throws Exception {
			
			manager.persist(briefing);
			manager.flush();
			return briefing.getFrozenBriefingId();
		}
		
		private static final String GETFROZENDPFMMINUTES = "from CommitteeProjectBriefingFrozen where ScheduleId=:scheduleId and IsActive=1 "; 
		@Override
		public CommitteeProjectBriefingFrozen getFrozenProjectBriefing(String scheduleId)throws Exception
		{
			try {
				Query query=manager.createQuery(GETFROZENDPFMMINUTES);
				query.setParameter("scheduleId", Long.parseLong(scheduleId));
				CommitteeProjectBriefingFrozen briefing=(CommitteeProjectBriefingFrozen)query.getResultList().get(0);
				return briefing;
			}
			catch (Exception e) {
				return null;
			}
		}
		
		private static final String AGENDALIST = """
				SELECT a.scheduleagendaid,a.scheduleid,a.schedulesubid,a.agendaitem,b.projectname,b.projectid,a.remarks,b.projectcode,a.agendapriority,
				a.presenterid ,CONCAT(IFNULL(CONCAT(j.title,' '),''), j.empname) AS 'empname' ,h.designation,a.duration,j.desigid, a.PresentorLabCode ,NULLIF(TRIM(a.GroupName), '') AS GroupName 
				FROM project_master b,employee j,employee_desig h,committee_schedules_agenda a  
				WHERE a.projectid=b.projectid AND a.scheduleid=:committeescheduleid AND a.isactive=1 AND a.projectid NOT IN (0, -1) AND a.presenterid=j.empid 
				AND j.desigid=h.desigid  UNION   SELECT a.scheduleagendaid,a.scheduleid,a.schedulesubid,a.agendaitem,cs.labcode AS 'projectname' ,
				'0' AS projectid,a.remarks,'' AS projectcode,a.agendapriority,a.presenterid ,CONCAT(IFNULL(CONCAT(j.title,' '),''), j.empname) AS 'empname' ,
				h.designation,a.duration,j.desigid, a.PresentorLabCode ,NULLIF(TRIM(a.GroupName), '') AS GroupName FROM employee j,employee_desig h, committee_schedules_agenda a, committee_schedule cs 
				WHERE a.scheduleid=:committeescheduleid  AND a.scheduleid=cs.scheduleid  AND a.isactive=1 AND a.projectid IN (0, -1) AND a.presenterid=j.empid AND j.desigid=h.desigid
				ORDER BY 9   """;
		@Override
		public List<Object[]> AgendaList(String scheduleId) throws Exception 
		{

			Query query=manager.createNativeQuery(AGENDALIST);
			query.setParameter("committeescheduleid", Long.parseLong(scheduleId));
			List<Object[]> AgendaList=(List<Object[]>)query.getResultList();
			
			return AgendaList;
		}
		
		private static final String AGENDALINKEDDOCLIST="SELECT sad.agendadocid,sad.agendaid,sad.filedocid,fru.filenameui FROM committee_schedule_agenda_docs sad, committee_schedules_agenda sa, file_rep_upload fru WHERE sad.agendaid=sa.scheduleagendaid AND sad.filedocid = fru.FileRepUploadId AND sad.isactive=1 AND sa.isactive=1 AND sa.scheduleid=:scheduleid";
		
		@Override
		public List<Object[]> AgendaLinkedDocList(String scheduleid) throws Exception 
		{
			Query query=manager.createNativeQuery(AGENDALINKEDDOCLIST);
			query.setParameter("scheduleid", Long.parseLong(scheduleid));
			List<Object[]> AgendaLinkedDocList=(List<Object[]>)query.getResultList();
			return AgendaLinkedDocList;
		}
		
	//	private static final String BRIEFINGSCHEDULELIST ="SELECT  DISTINCT cs.scheduleid, c.committeeid, c.committeeshortname, cs.projectid, cs.scheduledate, cs.schedulestarttime, cms.statusdetail,cs.BriefingPaperFrozen, cs.MinutesFrozen, cs.meetingid, pm.projectname, pm.projectcode, cs.PresentationFrozen, pm.ProjectDirector, pbs.BriefingStatusDesc, pbs.BriefingStatus, pbt.EmpId,(SELECT COUNT(Remarks) FROM pfms_briefing_transaction trans WHERE trans.ScheduleId=cs.ScheduleId) AS rmkCount FROM committee c INNER JOIN committee_schedule cs ON c.committeeid = cs.committeeid INNER JOIN project_master pm ON cs.projectid = pm.projectid INNER JOIN committee_meeting_status cms ON cms.meetingstatus = cs.scheduleflag INNER JOIN pfms_briefing_status pbs ON pbs.BriefingStatus = cs.BriefingStatus LEFT JOIN pfms_briefing_transaction pbt ON cs.ScheduleId = pbt.ScheduleId AND pbt.BriefingStatus='FWU'  WHERE cms.meetingstatusid >= 7 AND cs.isactive = 1 AND c.committeeshortname =:committeeshortname AND cs.labcode =:labcode AND cs.projectid =:projectid ORDER BY scheduledate DESC ";
		private static final String BRIEFINGSCHEDULELIST ="CALL BriefingList(:labcode,:committeeshortname,:projectid)";
	
		@Override
		public List<Object[]> BriefingScheduleList(String labcode,String committeeshortname, String projectid) throws Exception 
		{
			Query query=manager.createNativeQuery(BRIEFINGSCHEDULELIST);
			query.setParameter("labcode", labcode );
			query.setParameter("committeeshortname", committeeshortname );
			query.setParameter("projectid", Long.parseLong(projectid));
			List<Object[]> BriefingScheduleList=(List<Object[]>)query.getResultList();
			return BriefingScheduleList;
		}
		
		private static final String  BRIEFINGMEETINGVENUE ="SELECT cs.scheduleid, cs.meetingid,cs.scheduledate,cs.schedulestarttime,cs.scheduleflag, cs.MeetingVenue  FROM committee_schedule cs, committee_meeting_status  cms WHERE cs.isactive=1 AND cms.meetingstatus=cs.scheduleflag AND cs.committeeid=:committeeid AND CASE WHEN cs.ProjectId=0 THEN cs.ProgrammeId IN (SELECT pp.programmeid FROM pfms_programme_projects pp WHERE pp.projectid =:projectid AND pp.IsActive=1) ELSE cs.ProjectId =:projectid END AND ( CASE  WHEN cs.scheduledate<=CURDATE() THEN cs.scheduledate=CURDATE() ELSE cms.meetingstatusid < 7 END) ORDER BY cs.scheduledate ASC LIMIT 1";
		@Override
		public Object[] BriefingMeetingVenue( String projectid, String committeeid) throws Exception 
		{
			try {
				Query query=manager.createNativeQuery(BRIEFINGMEETINGVENUE);
				query.setParameter("committeeid", Long.parseLong(committeeid));
				query.setParameter("projectid", Long.parseLong(projectid));
				Object[] BriefingScheduleList=(Object[])query.getSingleResult();
				return BriefingScheduleList;
			}catch (NoResultException e) {
				return null;
			}
			
		}
		private static final String REQLIST="SELECT a.InitiationReqId, a.requirementid,a.reqtypeid,a.requirementbrief,a.requirementdesc FROM pfms_initiation_req a WHERE initiationid=:initiationid AND isActive='1'";
		@Override
		public Object RequirementList(String intiationId) throws Exception {
			// TODO Auto-generated method stub
			 Query query=manager.createNativeQuery(REQLIST);
			 query.setParameter("initiationid", Long.parseLong(intiationId));
			 
			 List<Object[]> RequirementList=(List<Object[]> )query.getResultList();	
			return RequirementList;
		}

		private static final String HEADOFACCOUNTSLIST= "SELECT DISTINCT (a.headofaccounts) FROM budget_item_sanc a WHERE projecttypeid=:projecttypeid";
		@Override
		public Object headofaccountsList(String projecttypeid) throws Exception {
			// TODO Auto-generated method stub
				Query query =manager.createNativeQuery(HEADOFACCOUNTSLIST);
				query.setParameter("projecttypeid", Integer.parseInt(projecttypeid));
				List<Object[]> headofaccountsList=(List<Object[]> )query.getResultList();	
	 
			return headofaccountsList;
		}
		private static final String GETRECDEC="SELECT a.recdecid , a.scheduleid , a.type , a.point FROM pfms_recdec_point a WHERE a.scheduleid=:scheduledid  and isactive='1' order by a.recdecid desc";
		@Override
		public List<Object[]> GetRecDecDetails(String scheduledid)throws Exception
		{
			 Query query=manager.createNativeQuery(GETRECDEC);
			 query.setParameter("scheduledid", Long.parseLong(scheduledid));
			 List<Object[]> RequirementList=(List<Object[]> )query.getResultList();	
			return RequirementList;
		}
		@Override
		public Long RedDecAdd(RecDecDetails recdec)throws Exception
		{
			manager.persist(recdec);
			manager.flush();
			return recdec.getRecDecId();
		}
		
		@Override
		public long RecDecUpdate(RecDecDetails recdec)throws Exception
		{
			RecDecDetails ExistingRecDecDetails= manager.find(RecDecDetails.class, recdec.getRecDecId());
			if(ExistingRecDecDetails != null) {
				System.err.println("Work");
				ExistingRecDecDetails.setModifiedBy(recdec.getModifiedBy());
				ExistingRecDecDetails.setModifiedDate(recdec.getModifiedDate());
				ExistingRecDecDetails.setType(recdec.getType());
				ExistingRecDecDetails.setPoint(recdec.getPoint());
				return 1L;
			}
			else {
				return 0L;
			}
			 
		}
		private static final String GETRECDECDATA="SELECT a.recdecid , a.scheduleid , a.type , a.point FROM pfms_recdec_point a WHERE a.recdecid=:recdecid ";
		@Override
		public Object[] GetRecDecData(String recdecid)throws Exception
		{
			 Query query=manager.createNativeQuery(GETRECDECDATA);
			 query.setParameter("recdecid", Long.parseLong(recdecid));
			 Object[] result=null;
			 List<Object[]> list=(List<Object[]> )query.getResultList();
			 if(list!=null && list.size()>0) {
				 result=list.get(0);
			 }
			return result;
		}
		private static final String PROJECTDATA="SELECT a.ProjectId, a.ProjectName, b.ProjectType, a.TotalSanctionCost, a.PDC, a.SanctionDate, a.EndUser, a.Objective, a.Deliverable, a.Scope, a.Application, a.ProjectDescription, a.ProjectCode, \r\n"
				+ "a.ProjectShortName, h.ProjectStage, e.Brief, f.Expenditure, f.OutCommitment, f.Dipl, f.Balance, e.Status AS 'CurrentStatus', a.IsMainWC AS 'isMain',  e.Status, e.Slide, e.ImageName, e.Path, e.SlideId,\r\n"
				+ "e.AttachmentName, e.Brief AS 'Brief2', e.ProjectId AS 'ProjectId2', e.VideoName, e.WayForward, f.ProjectType AS 'ProjectType2', CONCAT(IFNULL(CONCAT(c.Title,' '),''), c.EmpName) AS 'empname',d.Designation, i.PDC AS 'RevPDC' \r\n"
				+ "FROM project_master a\r\n"
				+ "JOIN project_type b ON a.ProjectType=b.projectTypeId\r\n"
				+ "LEFT JOIN employee c ON a.ProjectDirector=c.EmpId\r\n"
				+ "LEFT JOIN employee_desig d ON c.DesigId=d.DesigId\r\n"
				+ "LEFT JOIN pfms_project_slides e ON e.IsActive=1 AND e.ProjectId=a.ProjectId\r\n"
				+ "LEFT JOIN project_health f ON f.ProjectId=a.ProjectId\r\n"
				+ "LEFT JOIN pfms_project_data g ON g.ProjectId=a.ProjectId\r\n"
				+ "LEFT JOIN pfms_project_stage h ON g.CurrentStageId=h.ProjectStageId  \r\n"
				+ "LEFT JOIN project_master_rev i ON i.ProjectId=a.ProjectId AND i.RevisionNo='0'\r\n"
				+ "WHERE a.ProjectId=:ProjectId LIMIT 1";		
		@Override
		public Object[] GetProjectdata(String projectid)throws Exception
		{
			Query query=manager.createNativeQuery(PROJECTDATA);
			 query.setParameter("ProjectId", Long.parseLong(projectid));
			 Object[] result=null;
			 List<Object[]> list=(List<Object[]> )query.getResultList();
			 if(list!=null && list.size()>0) {
				 result=list.get(0);
			 }
			return result;
		}
		
		@Override
		public Long AddProjectSlideData( ProjectSlides slide )throws Exception
		{
			manager.persist(slide);
			manager.flush();
			return slide.getSlideId();
		}
		
		private static final String UPDATEPROJECTSLIDE="UPDATE pfms_project_slides SET Status=:Status , "
				+ "Brief=:Brief,WayForward=:wayfor,VideoName=:videoName, Slide=:Slide , AttachmentName=:AttachmentName ,"
				+ "Imagename=:ImageName  ,  Path=:Path , ModifiedBy=:ModifiedBy , ModifiedDate=:ModifiedDate WHERE SlideId=:SlideId";
		@Override
		public Long EditProjectSlideData(ProjectSlides slide)throws Exception
		{	
			ProjectSlides ExistingProjectSlides = manager.find(ProjectSlides.class, slide.getSlideId());
			if(ExistingProjectSlides != null) {
				ExistingProjectSlides.setStatus(slide.getStatus());
				ExistingProjectSlides.setBrief(slide.getBrief());
				ExistingProjectSlides.setWayForward(slide.getWayForward());
				ExistingProjectSlides.setVideoName(slide.getVideoName());
				ExistingProjectSlides.setSlide(slide.getSlide());
				ExistingProjectSlides.setAttachmentName(slide.getAttachmentName());
				ExistingProjectSlides.setImageName(slide.getImageName());
				ExistingProjectSlides.setPath(slide.getPath());
				ExistingProjectSlides.setModifiedBy(slide.getModifiedBy());
				ExistingProjectSlides.setModifiedDate(slide.getModifiedDate());
				return 1L;
			}
			else {
				return 0L;
			}
			
		}		
		
		private static final String PROJECTSLIDEDATA="SELECT a.status ,  a.slide , a.ImageName , a.path ,a.SlideId ,a.attachmentname, a.brief, a.projectid FROM pfms_project_slides a WHERE a.isactive=1 AND a.projectid=:projectid";
		@Override
		public Object[] GetProjectSildedata(String projectid)throws Exception
		{
			Query query=manager.createNativeQuery(PROJECTSLIDEDATA);
			 query.setParameter("projectid", Long.parseLong(projectid));
			 Object[] result=null;
			 List<Object[]> list=(List<Object[]> )query.getResultList();
			 if(list!=null && list.size()>0) {
				 result=list.get(0);
			 }
			return result;
		}
		
		@Override
		public ProjectSlides SlideAttachmentDownload(String achmentid) throws Exception
		{
			ProjectSlides Attachment= manager.find(ProjectSlides.class,Long.parseLong(achmentid));
			return Attachment;
		}

		@Override	
		public ProjectSlideFreeze FreezedSlideAttachmentDownload(String achmentid) throws Exception
		{
			ProjectSlideFreeze Attachment= manager.find(ProjectSlideFreeze.class,Long.parseLong(achmentid));
			return Attachment;
		}
		
		@Override
		public Long AddFreezeData (ProjectSlideFreeze freeze)throws Exception
		{
			manager.persist(freeze);
			manager.flush();
			return freeze.getFreezeId();
		}
		private static final String RISKTYPES = "SELECT Risktypeid,risktype,riskcode FROM pfms_risk_type";
		@Override
		public List<Object[]> RiskTypes() throws Exception 
		{
			Query query=manager.createNativeQuery(RISKTYPES);
			List<Object[]> RiskTypes=(List<Object[]> )query.getResultList();
			return RiskTypes;
		}
		private static final String PROJECTSLIDELIST="SELECT a.freezeid , a.reviewby , a.reviewdate , CONCAT(IFNULL(CONCAT(c.title,' '),''), c.empname) as 'empname' FROM pfms_project_slides_freeze a , project_master b , employee c WHERE a.projectid=b.projectid AND a.empid=c.empid AND a.projectid=:projectid ORDER BY  a.freezeid DESC";
		@Override
		public List<Object[]> getProjectSlideList(String projectid)throws Exception
		{
			Query query=manager.createNativeQuery(PROJECTSLIDELIST);
			query.setParameter("projectid", Long.parseLong(projectid));
			List<Object[]> RiskTypes=(List<Object[]> )query.getResultList();
			return RiskTypes;
		}
		private static final String ALLPROJECTSLIDEDATA="SELECT a.freezeid , a.path  ,a.attachname , a.projectid ,a.reviewby , ReviewDate , b.projectcode FROM pfms_project_slides_freeze a , project_master b WHERE a.projectid = :projectid  AND b.projectid=a.projectid  AND a.createddate = (SELECT MAX(a.createddate) FROM pfms_project_slides_freeze a WHERE a.projectid = :projectid)";
		@Override
		public List<Object[]> GetAllProjectSildedata(String projectid)throws Exception
		{
			Query query=manager.createNativeQuery(ALLPROJECTSLIDEDATA);
			query.setParameter("projectid", Long.parseLong(projectid));
			
			List<Object[]> RiskTypes=(List<Object[]> )query.getResultList();
			return RiskTypes;
		}
		
		public static final String GetFreezingHistoryQuery = "SELECT empid, Reviewby, reviewdate,path,  AttachName , freezeid FROM `pfms_project_slides_freeze` WHERE projectid=:projectid order by reviewdate desc";
		@Override
		public List<Object[]> GetFreezingHistory(String projectid)throws Exception
		{
			Query query=manager.createNativeQuery(GetFreezingHistoryQuery);
			query.setParameter("projectid", Long.parseLong(projectid));
			
			List<Object[]> RiskTypes=(List<Object[]> )query.getResultList();
			return RiskTypes;
		}
		
		private static final String TODATFREEZEDSLIDEDATA="SELECT a.freezeid , a.path  ,a.attachname , a.projectid ,a.reviewby , a.reviewdate FROM pfms_project_slides_freeze a WHERE a.projectid=:projectid AND a.reviewdate=CURRENT_DATE ORDER BY a.freezeid DESC";
		@Override
		public List<Object[]> GetTodayFreezedSlidedata(String projectid)throws Exception
		{
			Query query=manager.createNativeQuery(TODATFREEZEDSLIDEDATA);
			query.setParameter("projectid", Long.parseLong(projectid));
			List<Object[]> RiskTypes=(List<Object[]> )query.getResultList();
			return RiskTypes;
		}
		private static final String COSTLIST="SELECT c.headofaccounts,CONCAT (c.majorhead,'-',c.minorhead,'-',c.subhead) AS headcode,SUM(a.itemcost)FROM pfms_initiation_cost a,budget_item_sanc c WHERE a.budgetsancid=c.sanctionitemid AND a.isactive='1' AND a.initiationid=:initiationId AND a.budgetheadid=c.budgetheadid  GROUP BY headofaccounts,c.MajorHead,c.MinorHead,c.SubHead";
	@Override
		public List<Object[]> CostDetailsListSummary(String initiationId) throws Exception {
			// TODO Auto-generated method stub
			Query query = manager.createNativeQuery(COSTLIST);
			query.setParameter("initiationId", Long.parseLong(initiationId));
			return (List<Object[]>)query.getResultList();
		}	
		
	
		@Override
		public int ProjectImageDelete(String techImagesId) throws Exception {
			TechImages ExistingTechImages = manager.find(TechImages.class, Long.parseLong(techImagesId));
			if(ExistingTechImages != null) {
				ExistingTechImages.setIsActive(0);
				return 1;
			}
			else {
				return 0;
			}
			
		}
		
		
		@Override
		public List<Object[]> totalProjecMilestones(String projectid) throws Exception {
			List<Object[]> TotalMilestones=new ArrayList<Object[]>();
			Query query = manager.createNativeQuery("CALL pfms_total_project_milestones(:projectid);");
			query.setParameter("projectid", Long.parseLong(projectid));
			try {
				TotalMilestones= query.getResultList();
			}catch (Exception e) {
				e.printStackTrace();
				logger.error(new java.util.Date() +" Inside DAO totalProjectMilestones " + e);
			}
			return TotalMilestones;
		}
		
		@Override
		public int ProjectDecRecDelete(String recdecId) throws Exception {
			
			RecDecDetails ExistingRecDecDetails = manager.find(RecDecDetails.class,Long.parseLong(recdecId));
			if(ExistingRecDecDetails != null) {
				ExistingRecDecDetails.setIsActive(0);
				return 1;
			}
			else {
				return 0;
			}
			
		}
		
			@Override
		public int BriefingPointsUpdate(String point, String activityid, String status) throws Exception {
			String updateQuery="";
				if(status.equalsIgnoreCase("N")) {
				updateQuery="UPDATE milestone_activity_level set "+point+"='Y' where ActivityId=:ActivityId";
			}else {
				updateQuery="UPDATE milestone_activity_level set "+point+"='N' where ActivityId=:ActivityId";
			}
			Query query=manager.createNativeQuery(updateQuery);
			query.setParameter("ActivityId", Long.parseLong(activityid));
			int count=query.executeUpdate();
			return count;
		}
			private static final String DATAUPDATE=" UPDATE committee_project_briefing_frozen SET PresentationName=:presentationName WHERE scheduleid=:scheduleid ";
			@Override
			public int PresentationNameUpdate(String presentationName, String scheduleid) throws Exception {
			
				Query query = manager.createNativeQuery(DATAUPDATE);
				query.setParameter("presentationName", presentationName);
				query.setParameter("scheduleid", Long.parseLong(scheduleid));
				return (int)query.executeUpdate();
			}
			
			private static final String MOMUPDATE=" UPDATE committee_project_briefing_frozen SET Mom=:presentationName WHERE scheduleid=:scheduleid ";
			@Override
			public int MomUpdate(String presentationName, String scheduleid) throws Exception {
			
				Query query = manager.createNativeQuery(MOMUPDATE);
				query.setParameter("presentationName", presentationName);
				query.setParameter("scheduleid", Long.parseLong(scheduleid));
				return (int)query.executeUpdate();
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
			private static final String GETDIRECTORNAME = "SELECT a.EmpId,a.empname FROM employee a,lab_master b WHERE a.labcode=b.labcode AND  b.LabCode=:labCode AND b.LabAuthorityId=a.empid";
			@Override
			public Object[] getDirectorName(String labCode) throws Exception {
				Query query=manager.createNativeQuery(GETDIRECTORNAME);
				 query.setParameter("labCode", labCode);
				
				
				return (Object[])query.getSingleResult();
			}

			
			private static final String DORTMDADEMPDATA="SELECT pr.empid ,CONCAT(IFNULL(CONCAT(e.title,' '),''), e.empname) AS 'empname' ,ed.designation ,pr.type  FROM pfms_initiation_approver pr, employee e ,employee_desig ed WHERE pr.empid=e.empid AND e.desigid=ed.desigid AND pr.isactive='1' AND pr.LabCode=:Labcode and pr.type='DO-RTMD'";
			
			@Override
			public Object[]  DoRtmdAdEmpData(String Labcode) throws Exception
			{
				Query query=manager.createNativeQuery(DORTMDADEMPDATA);
				query.setParameter("Labcode", Labcode)	;	
				return (Object[])query.getSingleResult();
			}
			private static final String UPDATEBRIEFINGSTATUS = "UPDATE committee_schedule SET BriefingStatus=:briefingStatus WHERE ScheduleId=:sheduleId AND IsActive='1';";
			
			@Override
			public long updateBreifingStatus(String briefingStatus, String sheduleId)throws Exception {
				logger.info(new Date() + "Inside DAO updateBreifingStatus");
				
				try {
					long res=0;
					CommitteeSchedule ExistingCommitteeSchedule = manager.find(CommitteeSchedule.class,Long.parseLong(sheduleId));
					if(ExistingCommitteeSchedule != null) {
						ExistingCommitteeSchedule.setBriefingStatus(briefingStatus);
						res=1;
					}

					return res;	
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() + "Inside DaoImpl updateBreifingStatus", e);
					return  0;
				}
			}

			@Override
			public long insertBriefingTrans(PfmsBriefingTransaction briefingTransaction) throws Exception {
				logger.info(new Date() + "Inside DAO insertBriefingTrans");
				try {
					manager.persist(briefingTransaction);
					manager.flush();
					return briefingTransaction.getBriefingTransactionId();
					
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() + "Inside DaoImpl insertBriefingTrans", e);
					return  0;
				}
			}
			private static final String GETDIVISIONHEADLIST = "SELECT DivisionHeadId,DivisionId FROM division_master WHERE IsActive='1'";
			
			@Override
			public List<Object[]> getDivisionHeadList() throws Exception {
				Query query = manager.createNativeQuery(GETDIVISIONHEADLIST);
				try {
					return query.getResultList();
				}catch (Exception e) {
					e.printStackTrace();
					logger.error(new java.util.Date() +" Inside DAO totalProjectMilestones " + e);
				}
				return null;
			}
			private static final String GETDHID = "SELECT DivisionHeadId,DivisionId FROM division_master WHERE DivisionId IN (SELECT DivisionId FROM employee WHERE EmpId IN(SELECT ProjectDirector FROM  project_master WHERE ProjectId=:projectid))";
			
			@Override
			public Object[] getDHId(String projectid) throws Exception {
				try {
				Query query=manager.createNativeQuery(GETDHID);
				query.setParameter("projectid", Long.parseLong(projectid));	
				return (Object[])query.getSingleResult();	
				}catch (Exception e) {
					e.printStackTrace();
					logger.error(new java.util.Date() +" Inside DAO getDHId " + e);
					return null;
				}
			}
			private static final String GETGHID = "SELECT GroupHeadId,GroupId FROM division_group WHERE GroupId IN(SELECT GroupId FROM division_master WHERE DivisionId IN (SELECT DivisionId FROM employee WHERE EmpId IN(SELECT ProjectDirector FROM  project_master WHERE ProjectId=:projectid)))";
			@Override
			public Object getGHId(String projectid) throws Exception {
				try {
					Query query=manager.createNativeQuery(GETGHID);
					query.setParameter("projectid", Long.parseLong(projectid));	
					return (Object[])query.getSingleResult();	
					}catch (Exception e) {
						e.printStackTrace();
						logger.error(new java.util.Date() +" Inside DAO getGHId " + e);
						return null;
					}
				
				
			}
			@Override
			public long PfmsNotificationAdd(PfmsNotification notification) throws Exception {
				manager.persist(notification);
				manager.flush();
		        return notification.getNotificationId();
			
			}
			private static final String BRIEFINGSCHEDULEFWDLIST="CALL Briefing_Schedule_Forward_List(:empId,:labCode)";
			@Override
			public List<Object[]> BriefingScheduleFwdList(String labCode,String empId) throws Exception {
				Query query=manager.createNativeQuery(BRIEFINGSCHEDULEFWDLIST);
				query.setParameter("labCode", labCode );
				query.setParameter("empId", Long.parseLong(empId) );

				
				
				List<Object[]> BriefingScheduleList=(List<Object[]>)query.getResultList();
				return BriefingScheduleList;
			}
			
			private static final String BRIEFINGSCHEDULEFWDAPPROVEDLIST="CALL Briefing_Schedule_Fwd_Approved_List(:empId,:labCode,:projectid,:committeecode)";
			
			@Override
			public List<Object[]> BriefingScheduleFwdApprovedList(String labCode, String committeecode, String projectid,String empId) throws Exception {
				Query query=manager.createNativeQuery(BRIEFINGSCHEDULEFWDAPPROVEDLIST);
				query.setParameter("labCode", labCode );
				query.setParameter("committeecode", committeecode );
				query.setParameter("projectid", Long.parseLong(projectid));
				query.setParameter("empId", Long.parseLong(empId) );
				List<Object[]> BriefingScheduleList=(List<Object[]>)query.getResultList();
				return BriefingScheduleList;
			}
			private static final String GETBRIEFINGDATA = "SELECT MeetingId,ScheduleDate,ScheduleStartTime FROM committee_schedule WHERE ScheduleId=:sheduleId";
			@Override
			public Object[] getBriefingData(String sheduleId) throws Exception {
				try {
					Query query=manager.createNativeQuery(GETBRIEFINGDATA);
					query.setParameter("sheduleId", Long.parseLong(sheduleId));
					return (Object[])query.getSingleResult();	
					}catch (Exception e) {
						e.printStackTrace();
						logger.error(new java.util.Date() +" Inside DAO getBriefingData " + e);
						return null;
					}
			}
			private static final String GETFWDUSERID = "SELECT EmpId,MAX(ActionDate) FROM pfms_briefing_transaction WHERE ScheduleId=:sheduleId AND BriefingStatus='FWU' ;";
			
			@Override
			public Object[] getUserId(String sheduleId) throws Exception {
				logger.info(new Date() + "Inside DAO updateBreifingStatus");
				try {
				Query query=manager.createNativeQuery(GETFWDUSERID);
				query.setParameter("sheduleId", Long.parseLong(sheduleId));
				
				return (Object[]) query.getSingleResult();
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() + "Inside DaoImpl getUserId", e);
					return  null;
				}
			}
			private static final String GETEMPNAME = "SELECT EmpName FROM employee WHERE EmpId=:empId";
			@Override
			public String getEmpName(String empId) throws Exception {
				logger.info(new Date() + "Inside DAO getEmpName");
				try {
					Query query=manager.createNativeQuery(GETEMPNAME);
					query.setParameter("empId", Long.parseLong(empId));
					return query.getSingleResult().toString();
					
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() + "Inside DaoImpl getEmpName", e);
					return  null;
				}
			}
			private static final String GETBRIEFINGREMARKS = "SELECT CONCAT (e.empname,',' ,c.designation) AS emp,t.Remarks,t.ActionDate FROM pfms_briefing_transaction t,employee e,employee_desig c WHERE t.ScheduleId=:sheduleId AND t.BriefingStatus IN('RDH','RGH','RPD','RBD') AND t.EmpId=e.EmpId AND e.desigid=c.desigid";

			@Override
			public List<Object[]> getBriefingRemarks(String sheduleId) throws Exception {
				Query query=manager.createNativeQuery(GETBRIEFINGREMARKS);
				query.setParameter("sheduleId", Long.parseLong(sheduleId));
				List<Object[]> remarksList=(List<Object[]>)query.getResultList();	
				return remarksList;
			}
			
			
		@Override
		public List<Object[]> LastPMRCActions(String projectid, String committeeid, String date) throws Exception {
			// TODO Auto-generated method stub
			Query query=manager.createNativeQuery("CALL Last_PMRC_Actions_List_meeting(:projectid,:committeeid,:date);");	   
			query.setParameter("projectid", Long.parseLong(projectid));
			query.setParameter("committeeid", Long.parseLong(committeeid));
			query.setParameter("date", date);
			//query.setParameter("lastpmrcdate", lastpmrcdate);
			List<Object[]> LastPMRCActions=(List<Object[]>)query.getResultList();			
			return LastPMRCActions;
		}
		
		@Override
		public PfmsProjectData getPfmsProjectDataById(String projectId) throws Exception {
			try {
				return manager.find(PfmsProjectData.class, Long.parseLong(projectId));
			}catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		@Override
		public Long saveFavouriteSlides(FavouriteSlidesModel fSM)throws Exception
		{
			try {
				manager.persist(fSM);
				return fSM.getFavouriteSlidesId();
			}catch (Exception e) {
				// TODO: handle exception
			}
			return null;
		}
		
		@Override
		public List<Object[]> GETFavouriteSlides()throws Exception
		{
			try {
				Query query=manager.createNativeQuery("SELECT FavouriteSlidesId, FavouriteSlidesTitle, ProjectIds FROM pfms_favourite_slides");	
				List<Object[]> result =(List<Object[]>)query.getResultList();
				return result;
			}catch (Exception e) {
				// TODO: handle exception
			}
			return null;
		}

	

		@Override
		public Long EditFavouriteSlides(FavouriteSlidesModel fSM) throws Exception {
			Long result=0L;
			try {
				
				FavouriteSlidesModel ExistingFavouriteSlidesModel = manager.find(FavouriteSlidesModel.class, fSM.getFavouriteSlidesId());
				if(ExistingFavouriteSlidesModel != null) {
					ExistingFavouriteSlidesModel.setFavouriteSlidesTitle(fSM.getFavouriteSlidesTitle());
					ExistingFavouriteSlidesModel.setProjectIds(fSM.getProjectIds());
					ExistingFavouriteSlidesModel.setModifiedDate(fSM.getModifiedDate());
					ExistingFavouriteSlidesModel.setModifiedBy(fSM.getModifiedBy());
					result=1L;
				}
				
				return result;
			}catch (Exception e) {
				// TODO: handle exception
				System.out.println("there is an error bruh");
			}
			return null;
		}
		
		
		@Override
		public int editTechImage(TechImages image) throws Exception {
			TechImages ExistingTechImages = manager.find(TechImages.class, image.getTechImagesId());
			if(ExistingTechImages !=null) {
				ExistingTechImages.setImageName(image.getImageName());
				ExistingTechImages.setCreatedBy(image.getCreatedBy());
				ExistingTechImages.setCreatedDate(image.getCreatedDate());
				return 1;
			}
			else {
				return 0;
			}
			
		}
		
		
		@Override
		public long addOverallFinace(List<ProjectOverallFinance> list, String projectid) {
			String sql ="UPDATE pfms_overall_finance SET isactive='0' ,ModifiedBy =:ModifiedBy , ModifiedDate=:ModifiedDate WHERE ProjectId=:ProjectId";
			Query query = manager.createNativeQuery(sql);
			query.setParameter("ProjectId", Long.parseLong(projectid));
			query.setParameter("ModifiedBy", list.get(0).getCreatedBy());
			query.setParameter("ModifiedDate", LocalDate.now().toString());
			query.executeUpdate();
			
			for(ProjectOverallFinance pf:list) {
				manager.persist(pf);
				manager.flush();
			}
			return 2;
		}
		private static final String OVERALLFINANCE="SELECT a.ProjectFinanceId,a.LabCode,a.ProjectId,a.ProjectCode,a.BudgetHead,a.SanctionCostRE,\r\n"
				+ "a.SanctionCostFE,a.ExpenditureRE,a.ExpenditureFE,a.OutCommitmentRE,a.OutCommitmentFE,\r\n"
				+ "a.BalanceRE,a.BalanceFE,a.DiplRE,a.DiplFE,a.NotaionalBalRE,a.NotaionalBalFE,\r\n"
				+ "(SELECT SUM(SanctionCostRE) FROM pfms_overall_finance b WHERE b.projectid=a.projectid AND b.isactive='1') AS'TotalSanctionCostRE' ,\r\n"
				+ "(SELECT SUM(SanctionCostFE) FROM pfms_overall_finance b WHERE b.projectid=a.projectid AND b.isactive='1') AS 'TotalSanctionCostFE',\r\n"
				+ "(SELECT SUM(ExpenditureRE) FROM pfms_overall_finance b WHERE b.projectid=a.projectid AND b.isactive='1') AS 'TotalExpenditureRE',\r\n"
				+ "(SELECT SUM(ExpenditureFE) FROM pfms_overall_finance b WHERE b.projectid=a.projectid AND b.isactive='1') AS 'TotalExpenditureFE',\r\n"
				+ "(SELECT SUM(OutCommitmentRE) FROM pfms_overall_finance b WHERE b.projectid=a.projectid AND b.isactive='1') AS 'TotalOutCommitmentRE',\r\n"
				+ "(SELECT SUM(OutCommitmentFE) FROM pfms_overall_finance b WHERE b.projectid=a.projectid AND b.isactive='1') AS 'TotalOutCommitmentFE',\r\n"
				+ "(SELECT SUM(BalanceRE) FROM pfms_overall_finance b WHERE b.projectid=a.projectid AND b.isactive='1') AS 'TotalBalanceRE',\r\n"
				+ "(SELECT SUM(BalanceFE) FROM pfms_overall_finance b WHERE b.projectid=a.projectid AND b.isactive='1') AS 'TotalBalanceFE',\r\n"
				+ "(SELECT SUM(DiplRE) FROM pfms_overall_finance b WHERE b.projectid=a.projectid AND b.isactive='1') AS 'TotalDiplRE',\r\n"
				+ "(SELECT SUM(DiplFE) FROM pfms_overall_finance b WHERE b.projectid=a.projectid AND b.isactive='1') AS 'TotalDiplFE',\r\n"
				+ "(SELECT SUM(NotaionalBalRE) FROM pfms_overall_finance b WHERE b.projectid=a.projectid AND b.isactive='1') AS 'TotalNotaionalBalRE',\r\n"
				+ "(SELECT SUM(NotaionalBalFE) FROM pfms_overall_finance b WHERE b.projectid=a.projectid AND b.isactive='1') AS 'TotalNotaionalBalFE'\r\n"
				+ "FROM pfms_overall_finance a WHERE a.projectid=:projectid AND a.isactive='1'";
		@Override
		public List<Object[]> getrOverallFinance(String proid) throws Exception {
			Query query = manager.createNativeQuery(OVERALLFINANCE);
			query.setParameter("projectid", Long.parseLong(proid));
			
			return (List<Object[]>)query.getResultList();
		}
		
		private static final String OTHERMEETINGLIST="SELECT a.ScheduleId,a.ScheduleDate,b.CommitteeShortName,b.CommitteeName FROM committee_schedule a, committee b WHERE  a.CommitteeId= b.CommitteeId AND a.projectid=:projectid AND b.CommitteeId NOT IN (1,2)  AND a.ScheduleFlag IN ('MKV','MMR','MMF','MMS','MMA')   ORDER BY b.CommitteeShortName,a.ScheduleDate DESC";
		@Override
		public List<Object[]> otherMeetingList(String projectid) throws Exception {
			
			try {
			Query query = manager.createNativeQuery(OTHERMEETINGLIST);
			query.setParameter("projectid", Long.parseLong(projectid));
			
			return (List<Object[]>)query.getResultList();
			}catch (Exception e) {
					e.printStackTrace();
					return  new ArrayList<>();
			}
		}
		
		private static final String MILESTONEOPENACTIONLISTBYPROJECT ="SELECT  a.ActionAssignId, a.ActionMainId, a.ActionNo, a.EndDate, a.PDCOrg, a.AssignorLabCode, a.Assignor, a.AssigneeLabCode, a.Assignee, a.Remarks, a.ActionStatus, a.ClosedDate, a.Progress, a.ProgressDate, a.ProgressRemark, b.ActionDate, b.ActionItem, b.ActivityId, \r\n"
				+ "b.Type, b.ActionType, b.ProjectId, CONCAT(IFNULL(CONCAT(c.Title,' '),(IFNULL(CONCAT(c.Salutation, ' '), ''))), c.EmpName) AS 'EmpName', d.Designation, (SELECT  MAX(s.progressdate) FROM action_sub s  WHERE s.actionassignid=a.ActionAssignId) AS 'lastdate'\r\n"
				+ "FROM action_assign a  LEFT JOIN action_main b ON a.ActionMainId=b.ActionMainId LEFT JOIN employee c ON a.Assignee=c.EmpId LEFT JOIN employee_desig d ON c.DesigId=d.DesigId\r\n"
				+ "WHERE a.IsActive=1 AND b.IsActive=1 AND b.ActivityId<>0 AND b.ActionType IN ('A', 'B', 'C', 'D', 'E') AND a.ActionStatus<>'C' AND b.ProjectId=:ProjectId";
		@Override
		public List<Object[]> getMilestoneOpenActionListByProjectId(String projectId) throws Exception 
		{
			try {
				Query query=manager.createNativeQuery(MILESTONEOPENACTIONLISTBYPROJECT);	   
				query.setParameter("ProjectId", Long.parseLong(projectId));
				return (List<Object[]>)query.getResultList();
			}catch (Exception e) {
				e.printStackTrace();
				return new ArrayList<>();
			}
					
		}
		
		
		private static final String PROJECTMEETING = "SELECT p.projectid,p.projectcode,p.ProjectName,c.scheduledate,c.ScheduleId, MONTH(c.scheduledate)AS'schedulemonth',YEAR(c.scheduledate)AS'scheduleyear' ,c.ScheduleFlag  FROM  project_master p ,committee_schedule c WHERE c.projectid = p.projectid AND \r\n"
				+ "c.CommitteeId = (SELECT committeeId FROM committee WHERE CommitteeShortName = :CommitteeShortName LIMIT 1) \r\n"
				+ "AND c.isactive='1' ORDER BY p.projectid";
		
		@Override
		public List<Object[]> getProjectMeetings(String CommitteeShortName) throws Exception {
			
			Query query = manager.createNativeQuery(PROJECTMEETING);
			query.setParameter("CommitteeShortName", CommitteeShortName);
			
			return (List<Object[]>)query.getResultList();
		}
		
		private static final String PROJECTS = "SELECT p.projectid, p.projectname, p.projectcode,\r\n"
				+ "CONCAT(IFNULL(CONCAT(e.title,' '),IFNULL(CONCAT(e.salutation,' '),'')), e.empname) AS 'empname' ,b.ProjectTypeShort,\r\n"
				+ "p.ProjectDirector , e.MobileNo,e.DronaEmail,e.InternetEmail,d.designation FROM project_master p , employee e, project_type b,employee_desig d, project_main pm\r\n"
				+ "WHERE e.empid = p.ProjectDirector AND b.ProjectTypeId=p.ProjectType AND e.desigid=d.desigid AND pm.ProjectMainId=p.ProjectMainId ORDER BY p.ProjectDirector";
		
		@Override
		public List<Object[]> getprojectListProjectDirectorWise() throws Exception {
			Query query = manager.createNativeQuery(PROJECTS);
	
			
			return (List<Object[]>)query.getResultList();
		}
		
		private static final String CLOSUREREPORT ="SELECT p.projectid,p.projectname,p.projectcode,c.ClosureId,c.ClosureStatusCode,t.TechnicalClosureId,t.StatusCode,c.ClosureCategory,p.pdc\r\n"
				+ "FROM project_master p LEFT JOIN pfms_closure c ON p.projectid =c.projectid \r\n"
				+ "LEFT JOIN pfms_closure_technical t ON c.ClosureId=t.ClosureId";
		@Override
		public List<Object[]> getProjectClosureReport() throws Exception {
		
			Query query = manager.createNativeQuery(CLOSUREREPORT);			
			
			
			return (List<Object[]>)query.getResultList();
		}

		// Naveen R 06-03-2026
		private static final String MILESTONEBRIEFINGLIST = "SELECT milestoneActivityBriefingId,briefingPointId,points,scheduleId FROM milestone_activity_briefing WHERE scheduleId = :scheduleId AND isActive = 1;";
		@Override
		public List<Object[]> getMilestoneBriefingList(String scheduleId) throws Exception {
			try{
				Query query = manager.createNativeQuery(MILESTONEBRIEFINGLIST);
				query.setParameter("scheduleId", scheduleId);			
				return (List<Object[]>)query.getResultList();
			}catch (Exception e) {
				e.printStackTrace();
				return List.of();
			}
		}
		
		private static final String LASTMEETINGCREATED = """
				SELECT cs.scheduleid, cs.meetingid,cs.scheduledate,cs.schedulestarttime,cs.scheduleflag, cs.MeetingVenue  
				FROM committee_schedule cs, committee_meeting_status  cms WHERE cs.isactive=1 AND cms.meetingstatus=cs.scheduleflag 
				AND cs.committeeid=:committeeId AND CASE WHEN cs.ProjectId=0 THEN cs.ProgrammeId IN (SELECT pp.programmeid 
				FROM pfms_programme_projects pp WHERE pp.projectid =:projectId AND pp.IsActive=1) ELSE cs.ProjectId =:projectId END 
				ORDER BY cs.scheduledate DESC , cs.schedulestarttime DESC LIMIT 1
				""";
		@Override
		public Object[] getLastMeetingCreated(String projectId,String committeeId) throws Exception {
			try {
				Query query = manager.createNativeQuery(LASTMEETINGCREATED);
				query.setParameter("projectId", projectId);			
				query.setParameter("committeeId", committeeId);			
				return (Object[])query.getSingleResult();
			}catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		// Naveen R 08-04-2026
		@Override
		public List<BriefingFinance> getBriefingFinanceDetails(String scheduleId) throws Exception {
			try {
				Query query = manager.createQuery("SELECT e FROM BriefingFinance e WHERE e.ScheduleId = :scheduleId AND e.IsActive = 1",BriefingFinance.class);
				query.setParameter("scheduleId", scheduleId);
				
				return (List<BriefingFinance>)query.getResultList();
			}catch (Exception e) {
				e.printStackTrace();
				return List.of();
			}
		}

		@Override
		public long addOverallFinaceForPgad(List<BriefingFinance> list,String scheduleId) throws Exception {
			try {
				String sql ="UPDATE briefing_finance SET IsActive=0 WHERE ScheduleId=:scheduleId";
				Query query = manager.createNativeQuery(sql);
				query.setParameter("scheduleId", Long.parseLong(scheduleId));
				query.executeUpdate();
				
				for(BriefingFinance pf:list) {
					manager.persist(pf);
					manager.flush();
				}				
				
				return 1;
			}catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		}

		@Override
		public long addHeadings(List<BriefingHeading> list, String projectid) throws Exception {
			try {
//				String sql ="UPDATE briefing_heading SET IsActive=0,ModifiedBy =:ModifiedBy , ModifiedDate=:ModifiedDate WHERE ProjectId=:projectid";
//				Query query = manager.createNativeQuery(sql);
//				query.setParameter("projectid", Long.parseLong(projectid));
//				query.setParameter("ModifiedBy", list.get(0).getCreatedBy());
//				query.setParameter("ModifiedDate", LocalDate.now().toString());
//				query.executeUpdate();
				
				String sql = "SELECT HeadingId FROM briefing_heading WHERE projectId = :projectId AND IsActive = 1";

				Query query = manager.createNativeQuery(sql);
				query.setParameter("projectId", Long.parseLong(projectid));

				List<Long> idList = query.getResultList();
				Set<Long> receivedIds = new HashSet<>();
				
				String username = null;

				for (BriefingHeading h : list) {
					username = h.getModifiedBy();
				    if (h.getHeadingId() != null) {
				        receivedIds.add(h.getHeadingId());
				    }
				}
				for (BriefingHeading h : list) {					
				    manager.merge(h); // handles both insert + update
				}
				
				for (Long dbId : idList) {
				    if (!receivedIds.contains(dbId)) {

				        BriefingHeading old = manager.find(BriefingHeading.class, dbId);

				        if (old != null) {
				            old.setIsActive(0);
				            old.setModifiedDate(LocalDateTime.now());
				            old.setModifiedBy(username);

				            manager.merge(old);
				        }
				    }
				}
				return list.size();
			}catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		}

		@Override
		public List<BriefingHeading> getBriefingHeading(String projectid) throws Exception {
			try {
				Query query = manager.createQuery("SELECT e FROM BriefingHeading e WHERE e.ProjectId = :projectid AND e.IsActive = 1 ORDER BY e.Seniority ASC",BriefingHeading.class);
				query.setParameter("projectid", projectid);
				
				return (List<BriefingHeading>)query.getResultList();
			}catch (Exception e) {
				e.printStackTrace();
				return List.of();
			}
		}

		private static final String HEADERDETAILS = """
				SELECT a.DetailsId,a.Details, b.HeadingId, b.ProjectId, b.Heading
				FROM briefing_heading_details a LEFT JOIN briefing_heading b
				ON a.HeadingId = b.HeadingId 
				WHERE a.HeadingId = :headingId AND a.ScheduleId = :scheduleId AND a.IsActive = 1
				""";
		@Override
		public List<Object[]> getHeadingDetails(String headingid, String projectid, String scheduleid) throws Exception {
			try {
				Query query = manager.createNativeQuery(HEADERDETAILS);
				query.setParameter("headingId", headingid);
				//query.setParameter("projectid", projectid);
				query.setParameter("scheduleId", scheduleid);
				
				return (List<Object[]>)query.getResultList();
			}catch (Exception e) {
				e.printStackTrace();
				return new ArrayList<Object[]>();
			}
		}

		@Override
		public BriefingHeadingDetails getDetailsById(String detailid) throws Exception {
			try {
				Query query = manager.createQuery("SELECT e FROM BriefingHeadingDetails e WHERE e.DetailsId = :detailid AND e.IsActive = 1",BriefingHeadingDetails.class);
				query.setParameter("detailid", detailid);
				
				List<BriefingHeadingDetails> detail = (List<BriefingHeadingDetails>)query.getResultList();

				if(detail!=null && !detail.isEmpty()) return detail.get(0);
				return null;
			}catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		public long addDetails(BriefingHeadingDetails dto) throws Exception {
			try{
				manager.persist(dto);
				manager.flush();
				return dto.getDetailsId();
			}catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
			
		}
		private static final String HEADERDETAILSALL = """
				SELECT a.DetailsId,a.Details, b.HeadingId, b.ProjectId, b.Heading, a.ScheduleId
				FROM briefing_heading_details a LEFT JOIN briefing_heading b
				ON a.HeadingId = b.HeadingId 
				WHERE a.IsActive = 1 AND b.ProjectId = :projectid AND a.scheduleid = :scheduleid AND b.isActive = 1
				ORDER BY b.Seniority;
				""";
		@Override
		public List<Object[]> getAllHeadingDetails(String projectid,String scheduleid) throws Exception {
			try {
				Query query = manager.createNativeQuery(HEADERDETAILSALL);
				query.setParameter("projectid", projectid);
				query.setParameter("scheduleid", scheduleid);
				
				return (List<Object[]>)query.getResultList();
			}catch (Exception e) {
				e.printStackTrace();
				return new ArrayList<Object[]>();
			}
		}
		
		@Override
		public List<BriefingHeadingDetails> getDetailsByHeadingAndSCheduleId(String headingid,String scheduleid) throws Exception {
			try {
				Query query = manager.createQuery("SELECT e FROM BriefingHeadingDetails e WHERE e.HeadingId = :headingid AND e.ScheduleId = :scheduleid AND e.IsActive = 1",BriefingHeadingDetails.class);

		        query.setParameter("headingid", Long.parseLong(headingid));
		        query.setParameter("scheduleid", Long.parseLong(scheduleid));
				
				List<BriefingHeadingDetails> detail = (List<BriefingHeadingDetails>)query.getResultList();

				if(detail!=null && !detail.isEmpty()) return detail;
				return List.of();
			}catch (Exception e) {
				e.printStackTrace();
				return List.of();	
			}
		}

		@Override
		public long saveAllDetails(List<BriefingHeadingDetails> list) throws Exception {
			try {
		        for (int i = 0; i < list.size(); i++) {
		            manager.persist(list.get(i));

		        }
		        return list.size();
		    } catch (Exception e) {
		        e.printStackTrace();
		        return 0;
		    }
		}

	}
