package com.vts.pfms.project.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vts.pfms.committee.model.ActionAssign;
import com.vts.pfms.committee.model.CommitteeInitiation;
import com.vts.pfms.committee.model.PfmsNotification;
import com.vts.pfms.master.model.RoleMaster;
import com.vts.pfms.print.model.ProjectTechnicalWorkData;
import com.vts.pfms.project.dto.PfmsRiskDto;
import com.vts.pfms.project.model.InitiationAbbreviations;
import com.vts.pfms.project.model.PfmsApproval;
import com.vts.pfms.project.model.PfmsInitiation;
import com.vts.pfms.project.model.PfmsInitiationAppendix;
import com.vts.pfms.project.model.PfmsInitiationApproval;
import com.vts.pfms.project.model.PfmsInitiationAttachment;
import com.vts.pfms.project.model.PfmsInitiationAttachmentFile;
import com.vts.pfms.project.model.PfmsInitiationAuthority;
import com.vts.pfms.project.model.PfmsInitiationAuthorityFile;
import com.vts.pfms.project.model.PfmsInitiationChecklistData;
import com.vts.pfms.project.model.PfmsInitiationCost;
import com.vts.pfms.project.model.PfmsInitiationDetail;
import com.vts.pfms.project.model.PfmsInitiationLab;
import com.vts.pfms.project.model.PfmsInitiationMacroDetails;
import com.vts.pfms.project.model.PfmsInitiationMacroDetailsTwo;
import com.vts.pfms.project.model.PfmsInitiationMilestone;
import com.vts.pfms.project.model.PfmsInitiationMilestoneRev;
import com.vts.pfms.project.model.PfmsInitiationReqIntro;
import com.vts.pfms.project.model.PfmsInitiationSanctionData;
import com.vts.pfms.project.model.PfmsInitiationSchedule;
import com.vts.pfms.project.model.PfmsInititationRequirement;
import com.vts.pfms.project.model.PfmsOtherReq;
import com.vts.pfms.project.model.PfmsProcurementPlan;
import com.vts.pfms.project.model.PfmsProjectData;
import com.vts.pfms.project.model.PfmsProjectDataRev;
import com.vts.pfms.project.model.PfmsReqStatus;
import com.vts.pfms.project.model.PfmsRequirementApproval;
import com.vts.pfms.project.model.PfmsRisk;
import com.vts.pfms.project.model.PfmsRiskRev;
import com.vts.pfms.project.model.PlatformMaster;
import com.vts.pfms.project.model.PmsInitiationApprovalTransaction;
import com.vts.pfms.project.model.PreprojectFile;
import com.vts.pfms.project.model.ProjectAssign;
import com.vts.pfms.project.model.ProjectMactroDetailsBrief;
import com.vts.pfms.project.model.ProjectMain;
import com.vts.pfms.project.model.ProjectMajorCapsi;
import com.vts.pfms.project.model.ProjectMajorCars;
import com.vts.pfms.project.model.ProjectMajorConsultancy;
import com.vts.pfms.project.model.ProjectMajorManPowers;
import com.vts.pfms.project.model.ProjectMajorRequirements;
import com.vts.pfms.project.model.ProjectMajorWorkPackages;
import com.vts.pfms.project.model.ProjectMaster;
import com.vts.pfms.project.model.ProjectMasterAttach;
import com.vts.pfms.project.model.ProjectMasterRev;
import com.vts.pfms.project.model.ProjectOtherReqModel;
import com.vts.pfms.project.model.ProjectRequirementType;
import com.vts.pfms.project.model.ProjectSqrFile;
import com.vts.pfms.project.model.ReqTestExcelFile;
import com.vts.pfms.project.model.RequirementAcronyms;
import com.vts.pfms.project.model.RequirementMembers;
import com.vts.pfms.project.model.RequirementPerformanceParameters;
import com.vts.pfms.project.model.RequirementSummary;
import com.vts.pfms.project.model.RequirementVerification;
import com.vts.pfms.project.model.RequirementparaModel;
import com.vts.pfms.requirements.model.DocMembers;
import com.vts.pfms.requirements.model.SpecifcationProductTree;
import com.vts.pfms.requirements.model.Specification;
import com.vts.pfms.requirements.model.SpecificationContent;
import com.vts.pfms.requirements.model.SpecificationIntro;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Transactional
@Repository
public class ProjectDaoImpl implements ProjectDao {

	private static final Logger logger=LogManager.getLogger(ProjectDaoImpl.class);
	java.util.Date loggerdate=new java.util.Date();

	private static final String PROJECTINTILIST="SELECT a.initiationid,a.projectprogramme,b.project_type_short,c.classification,a.projectshortname,a.projecttitle,a.projectcost,a.projectduration,a.ismain,a.empid AS 'pdd',a.LabCode,a.projecttypeid FROM pfms_initiation a,project_type b, pfms_security_classification c WHERE (CASE WHEN :logintype IN ('Z','Y','A','E') THEN a.LabCode=:LabCode ELSE a.empid=:empid END ) AND a.classificationid=c.classification_id  AND a.projecttypeid=b.project_type_id AND a.isactive='1' AND a.projectstatus IN ('PIN','DOI','ADI','TCI','RTI','DRO','DRI')";
	private static final String PROJECTTYPELIST="select classification_id,classification from pfms_security_classification order by classification";
	private static final String PROJECTCATEGORYLIST="select project_type_id,project_type,project_type_short from project_type where is_active='1' ORDER BY project_type";
	private static final String PROJECTDELIVERABLELIST="select deliverableid,deliverable from pfms_deliverable order by deliverable ";
	private static final String LABLIST="SELECT a.labid,a.labname,a.labcode FROM cluster_lab a WHERE   a.labid NOT IN (SELECT b.labid FROM pfms_initiation_lab b  WHERE  b.initiationid=:initiationid AND b.isactive='1')";
	private static final String PROJECTSHORTNAMECHECK="SELECT count(*) from pfms_initiation where projectshortname=:projectshortname";
	private static final String BUDEGTITEM="select sanctionitemid,headofaccounts,refe,projecttypeid ,CONCAT (majorhead,'-',minorhead,'-',subhead) AS headcode from budget_item_sanc where budgetheadid=:budgetheadid and isactive='1' ORDER BY headofaccounts";
	private static final String PROJECTITEMLIST="SELECT a.initiationcostid,a.initiationid,c.budgetheaddescription,b.headofaccounts,a.itemdetail,a.itemcost ,b.refe , CONCAT (b.majorhead,'-',b.minorhead,'-',b.subhead) AS headcode FROM pfms_initiation_cost a,budget_item_sanc b,budget_head c WHERE a.initiationid=:initiationid AND a.budgetsancid=b.sanctionitemid AND a.budgetheadid=c.budgetheadid AND a.isactive='1' ORDER BY a.budgetheadid ASC";
	private static final String PROJECTLABLIST="select a.initiationid,a.InitiationLabId,b.lab_name,b.lab_code from pfms_initiation_lab a,cluster_lab b where a.initiationid=:initiationid and b.lab_id=a.labid and isactive='1'";
	private static final String BUDEGTHEADLIST="select budgetheadid,budgetheaddescription from budget_head where isproject='Y' order by budgetheaddescription asc ";
	private static final String PROJECTSCHEDULELIST="select milestoneno,milestoneactivity,milestonemonth,initiationscheduleid,milestoneremark,Milestonestartedfrom,MilestoneTotalMonth,StartDate,EndDate,COALESCE (FinancialOutlay,'0') AS 'outlay' from pfms_initiation_schedule where initiationid=:initiationid and isactive='1'";	private static final String PROJECTSCHEDULETOTALMONTHLIST="select MilestoneTotalMonth,milestoneno,Milestonestartedfrom from pfms_initiation_schedule where initiationid=:initiationid and isactive='1' " ;
	/*L.A*/private static final String MILESTONENOTOTALMONTHS="SELECT milestoneno,MilestoneTotalMonth,Milestonestartedfrom FROM pfms_initiation_schedule WHERE isactive='1' AND initiationid=:InitiationId AND milestonestartedfrom=:milestonestartedfrom ";
	private static final String PROJECTDETAILSLIST= "SELECT a.Requirements,a.Objective,a.Scope,a.MultiLabWorkShare,a.EarlierWork,a.CompentencyEstablished,a.NeedOfProject,a.TechnologyChallanges,a.RiskMitigation,a.Proposal,a.RealizationPlan,a.initiationid,a.worldscenario,a.ReqBrief,a.ObjBrief,a.ScopeBrief,a.MultiLabBrief,a.EarlierWorkBrief,a.CompentencyBrief,a.NeedOfProjectBrief,a.TechnologyBrief,a.RiskMitigationBrief,a.ProposalBrief,a.RealizationBrief,a.WorldScenarioBrief FROM pfms_initiation_detail a WHERE a.initiationid=:initiationid ";
	private static final String PROJECTCOSTLIST="SELECT b.budgetheaddescription,c.headofaccounts,a.itemdetail,a.itemcost,c.refe,c.sanctionitemid  FROM pfms_initiation_cost a,budget_head b,budget_item_sanc c WHERE a.budgetheadid=b.budgetheadid AND a.budgetsancid=c.sanctionitemid AND a.initiationid=:initiationid ORDER BY sanctionitemid ";
	private static final String PROJECTINTIEDITDATA="SELECT a.initiationid,a.empid,a.divisionid,a.projectprogramme,a.projecttypeid,a.classificationid,a.projectshortname,a.projecttitle,a.projectcost,a.projectduration,a.isplanned,a.ismultilab,a.deliverable,a.fecost,a.recost,a.nodallab,a.remarks,a.ismain,a.projecttitle AS 'initiatedproject',a.pcduration,a.indicativecost,a.pcremarks,a.StartDate  \r\n"
			+ "FROM pfms_initiation a \r\n"
			+ "WHERE a.initiationid=:initiationid  AND a.isactive='1' AND a.mainid=0 \r\n"
			+ "UNION \r\n"
			+ "SELECT a.initiationid,a.empid,a.divisionid,a.projectprogramme,a.projecttypeid,a.classificationid,a.projectshortname,a.projecttitle,a.projectcost,a.projectduration,a.isplanned,a.ismultilab,a.deliverable,a.fecost,a.recost,a.nodallab,a.remarks,a.ismain,b.projecttitle ,a.pcduration,a.indicativecost,a.pcremarks,a.StartDate \r\n"
			+ "FROM pfms_initiation a ,pfms_initiation b \r\n"
			+ "WHERE a.initiationid=:initiationid AND a.isactive='1' AND a.mainid=b.initiationid";
	private static final String PROJECTINTITOTALCOST="select sum(ItemCost) from pfms_initiation_cost where initiationid=:initiationid and isactive='1'";
	private static final String PROJECTINTICOSTDATA="select initiationcostid,initiationid,budgetheadid,budgetsancid,itemdetail,itemcost from pfms_initiation_cost where initiationcostid=:initiationcostid and isactive='1'";
	/*L.A*/private static final String MILESTONETOTALMONTHUPDATE="UPDATE pfms_initiation_schedule SET MilestoneTotalMonth=:newMilestoneTotalMonth  WHERE  initiationid=:InitiationId AND isactive='1' AND milestoneno=:milestoneno";
	private static final String PROJECTSHDULEUPDATE="update pfms_initiation_schedule set milestoneactivity=:milestoneactivity,milestonemonth=:milestonemonth,milestoneremark=:milestoneremark,modifiedby=:modifiedby,modifieddate=:modifieddate where initiationscheduleid=:initiationscheduleid and isactive='1'";
	private static final String PROJECTDETAILSREQUPDATE="update pfms_initiation_detail set requirements=:requirements, reqbrief=:reqbrief, modifiedby=:modifiedby, modifieddate=:modifieddate where initiationid=:initiationid";
	
	
	private static final String PROJECTDETAILSOBJUPDATE="update pfms_initiation_detail set objective=:objective, objbrief=:objbrief, modifiedby=:modifiedby, modifieddate=:modifieddate where initiationid=:initiationid";
	private static final String PROJECTDETAILSSCOPEUPDATE="update pfms_initiation_detail set scope=:scope, scopebrief=:scopebrief, modifiedby=:modifiedby, modifieddate=:modifieddate where initiationid=:initiationid";
	private static final String PROJECTDETAILSMULTIUPDATE="update pfms_initiation_detail set multilabworkshare=:multilab, multilabbrief=:multibrief, modifiedby=:modifiedby, modifieddate=:modifieddate where initiationid=:initiationid";
	private static final String PROJECTDETAILSEARLYUPDATE="update pfms_initiation_detail set earlierwork=:earlierwork, earlierworkbrief=:earlibrief, modifiedby=:modifiedby, modifieddate=:modifieddate where initiationid=:initiationid";
	private static final String PROJECTDETAILSCOMPETANCYUPDATE="update pfms_initiation_detail set compentencyestablished=:competancy, compentencybrief=:combrief, modifiedby=:modifiedby, modifieddate=:modifieddate where initiationid=:initiationid";
	private static final String PROJECTDETAILSNEEDOFUPDATE="update pfms_initiation_detail set needofproject=:needofproject, needofprojectbrief=:needbrief, modifiedby=:modifiedby, modifieddate=:modifieddate where initiationid=:initiationid";
	private static final String PROJECTDETAILSTECHUPDATE="update pfms_initiation_detail set technologychallanges=:technology, technologybrief=:techbrief, modifiedby=:modifiedby, modifieddate=:modifieddate where initiationid=:initiationid";
	private static final String PROJECTDETAILSRISKUPDATE="update pfms_initiation_detail set riskmitigation=:riskmitigation, riskmitigationbrief=:riskbrief, modifiedby=:modifiedby, modifieddate=:modifieddate where initiationid=:initiationid";
	private static final String PROJECTDETAILSPROPOSALUPDATE="update pfms_initiation_detail set proposal=:proposal, proposalbrief=:probrief, modifiedby=:modifiedby, modifieddate=:modifieddate where initiationid=:initiationid";
	private static final String PROJECTDETAILSREALIZATIONUPDATE="update pfms_initiation_detail set realizationplan=:realization, realizationbrief=:realizationbrief, modifiedby=:modifiedby, modifieddate=:modifieddate where initiationid=:initiationid";
	private static final String PROJECTDETAILSWORLDUPDATE="update pfms_initiation_detail set worldscenario=:worldscenario,worldscenariobrief=:worldbrief, modifiedby=:modifiedby, modifieddate=:modifieddate where initiationid=:initiationid";	
	private static final String PROJECTSCHTOTALMONTH="select sum(milestonemonth) from pfms_initiation_schedule where InitiationId=:InitiationId and isactive='1'";
	/*L.A*/private static final String PROJECTDURMONTH="SELECT IFNULL(MAX(MilestoneTotalMonth),0) AS 'MAX' FROM pfms_initiation_schedule WHERE InitiationId=:InitiationId AND isactive='1'";
	/*L.A*/ private static final String MILESCHMONTH="SELECT IFNULL(MAX(MilestoneTotalMonth),0) AS 'MAX' FROM pfms_initiation_schedule WHERE MilestoneTotalMonth !=(SELECT MilestoneTotalMonth FROM pfms_initiation_schedule WHERE InitiationScheduleId=:initiationscheduleid AND IsActive='1') AND InitiationId=:IntiationId AND IsActive='1';";	
	private static final String MILESTONENO="select IFNULL(MAX(MilestoneNo),0) AS 'MAX' from pfms_initiation_schedule where InitiationId=:InitiationId and isactive='1'";
	private static final String SCDULEMONTH="select milestonemonth from pfms_initiation_schedule where initiationscheduleid=:initiationscheduleid and isactive='1'";
	/*L.A*/private static final String PREVIOUSMONTH="SELECT milestonemonth FROM pfms_initiation_schedule WHERE milestoneno=:milestoneno AND initiationid=:IntiationId AND isactive='1'";
	/*L.A*/private static final String MILESTONENOTOTALMONTH="SELECT MilestoneTotalMonth FROM pfms_initiation_schedule WHERE milestoneno=:milestoneno AND initiationid=:IntiationId AND isactive='1'";
	private static final String PROJECTINTATTACH="SELECT a.initiationattachmentid,a.initiationid,a.filename,a.filenamepath,a.createdby,a.createddate,b.initiationattachmentfileid FROM pfms_initiation_attachment a,pfms_initiation_attachment_file b WHERE isactive='1' AND initiationid=:initiationid AND a.initiationattachmentid=b.initiationattachmentid";
	private static final String PROJECTINTATTACHFILENAME="select a.filename from pfms_initiation_attachment a where a.isactive='1' and a.initiationattachmentid=:initiationattachmentid ";
	private static final String PROJECTINTATTACHFILENAMEPATH="select a.filenamepath from pfms_initiation_attachment a where a.isactive='1' and a.initiationattachmentid=:initiationattachmentid ";
	private static final String PROJECTINTCOSTDELETE="update pfms_initiation_cost set isactive='0' ,modifiedby=:modifiedby, modifieddate=:modifieddate where initiationcostid=:initiationcostid ";
	private static final String PROJECTACTIONLIST="select projectauthorityid,status,statusaction from pfms_project_authority_actionlist where projectauthorityid=:projectauthorityid";
	private static final String EMPLOYEELIST="select a.emp_id,CONCAT(IFNULL(CONCAT(a.title,' '),''), a.emp_name) AS 'empname' ,b.designation FROM employee a,employee_desig b WHERE a.is_active='1' AND a.desig_id=b.desig_id AND a.lab_code=:LabCode ORDER BY a.sr_no=0,a.sr_no";
	private static final String PFMSINITIATIONREFESUM= "SELECT SUM(a.itemcost) AS 'recost'  FROM pfms_initiation_cost a, budget_item_sanc b  WHERE a.budgetsancid=b.sanctionitemid AND a.isactive=1 AND a.initiationid=:initiationid AND b.refe=:refe";
	private static final String PROJECTSTATUSLIST="SELECT b.project_type_short,c.classification,a.projecttitle,a.projectshortname,a.projectcost,a.projectduration,d.statusdetail,a.initiationid FROM pfms_initiation a,project_type b, pfms_security_classification c,pfms_project_authority_actionlist d WHERE (CASE WHEN :logintype IN ('Z','Y','A','E') THEN a.LabCode=:LabCode ELSE a.empid=:empid END ) AND a.classificationId=c.classification_id AND a.ProjectTypeId=b.project_type_id AND a.projectstatus=d.Status";
	private static final String PROJECTAPPROVALTRACKING="SELECT a.projectapprovalid,a.empid,c.empname,d.designation,e.division_name,a.actiondate,a.remarks,b.statusdetail FROM project_approval a, pfms_project_authority_actionlist b,employee c,employee_desig d,division_master e WHERE a.projectstatus=b.Status AND a.empid=c.empid  AND c.desigid=d.desigid AND c.divisionid=e.division_id AND a.initiationid=:initiationid";
	private static final String PROJECTINTIDATAPREVIEW="SELECT a.initiationid,d.emp_name,e.division_code,a.projectprogramme,b.project_type,c.classification,a.projectshortname,a.projecttitle,a.projectcost, a.projectduration,a.isplanned,a.ismultilab,a.deliverable,a.labcount,a.fecost,a.recost,a.ismain,f.projectshortname AS 'initiatedproject' FROM pfms_initiation a,project_type b,pfms_security_classification c,employee d,division_master e,pfms_initiation f WHERE a.initiationid=:initiationid AND a.classificationid=c.classification_id  AND a.projecttypeid=b.project_type_id AND a.empid=d.emp_id AND a.divisionid=e.division_id AND a.isactive='1' AND a.mainid=f.initiationid UNION SELECT a.initiationid,d.emp_name,e.division_code,a.projectprogramme,b.project_type,c.classification,a.projectshortname,a.projecttitle,a.projectcost, a.projectduration,a.isplanned,a.ismultilab,a.deliverable,a.labcount,a.fecost,a.recost,a.ismain,a.projecttitle AS 'initiatedproject' FROM pfms_initiation a,project_type b,pfms_security_classification c,employee d,division_master e WHERE a.initiationid=:initiationid AND a.classificationid=c.classification_id AND a.projecttypeid=b.project_type_id AND a.empid=d.emp_id AND a.divisionid=e.division_id AND a.isactive='1' AND a.mainid=0";
	private static final String PROJECTINTITOTALFECOST="select sum(a.ItemCost) from pfms_initiation_cost a,budget_item_sanc b where a.initiationid=:initiationid and a.isactive='1' and a.budgetsancid=b.sanctionitemid and a.refe='FE' ";
	private static final String PROJECTINTITOTALRECOST="select sum(a.ItemCost) from pfms_initiation_cost a,budget_item_sanc b where a.initiationid=:initiationid and a.isactive='1' and a.budgetsancid=b.sanctionitemid and a.refe='RE' ";
	private static final String PROJECTCOSTDATA="select fecost , recost from pfms_initiation where initiationid=:initiationid ";
	private static final String TCCPROJECTLIST="SELECT projecttitle,projectshortname, initiationid FROM pfms_initiation WHERE projectstatus='PTA' AND isactive='1' ";
	private static final String EXPERTLIST="SELECT a.expertid,a.expertname,b.designation FROM expert a,employee_desig b WHERE a.isactive='1' AND a.DesigId=b.DesigId";
	private static final String DIVISIONHEADID="SELECT a.division_head_id FROM division_master a, employee b WHERE a.division_id=b.division_id AND b.empid=:empid";
	private static final String RTMDDOID="SELECT empid FROM pfms_initiation_approver WHERE isactive=1 AND Type='DO-RTMD' " ;
	private static final String TCCCHAIRPERSONID="SELECT labauthorityid FROM lab_master WHERE labcode=:labcode";
	private static final String CCMCHAIRPERSONID="SELECT labdgid FROM lab_master WHERE labcode=:labcode";
	private static final String ADID="SELECT empid FROM pfms_initiation_approver WHERE isactive=1 AND TYPE='AD';";  
	private static final String APPSTATUSLIST="SELECT a.status, a.statusaction FROM pfms_project_authority_actionlist  a WHERE projectauthorityid=:AuthoId";
	private static final String PROJECTSTAGEDETAILSLIST ="SELECT projectstageid,projectstagecode,projectstage FROM pfms_project_stage";
	private static final String PROJECTDATADETAILS="SELECT ppd.projectdataid,ppd.projectid,ppd.filespath,ppd.systemconfigimgname,ppd.SystemSpecsFileName,ppd.ProductTreeImgName,ppd.PEARLImgName,ppd.CurrentStageId,ppd.RevisionNo,pps.projectstagecode,pps.projectstage,ppd.proclimit,ppd.LastPmrcDate,ppd.LastEBDate FROM pfms_project_data ppd, pfms_project_stage pps WHERE ppd.CurrentStageId=pps.projectstageid AND ppd.projectid=:projectid";
	private static final String PROJECTDATASPECSFILEDATA  ="SELECT projectdataid,projectid,filespath, systemspecsfilename,systemconfigimgname,producttreeimgname,pearlimgname FROM pfms_project_data WHERE projectdataid=:projectdataid";
	private static final String PROJECTDATASPECSREVFILEDATA  ="SELECT projectdatarevid,projectid,filespath, systemspecsfilename,systemconfigimgname,producttreeimgname,pearlimgname  FROM pfms_project_data_rev WHERE projectdatarevid=:projectdatarevid";
	private static final String PROJECTDATAREVLIST="SELECT ProjectDataRevId,ProjectId,RevisionNo,RevisionDate FROM pfms_project_data_rev WHERE ProjectId=:projectid ORDER BY RevisionNo DESC";
	private static final String INITIATEDPROJECT="SELECT initiationid,projecttitle,projectshortname,empid,divisionid,projectprogramme,projecttypeid,classificationid FROM pfms_initiation WHERE isactive=1 AND ismain='Y'";  
	private static final String INITIATEDPROJECTDETAILS="SELECT a.initiationid,a.projectprogramme,a.classificationid,b.project_type AS category, a.projecttypeid,c.classification AS securityclassification,d.lab_id,d.lab_code,d.lab_name, a.projectshortname, a.projecttitle FROM pfms_initiation a,project_type b,pfms_security_classification c ,cluster_lab d WHERE a.classificationid=c.classification_id AND a.projecttypeid=b.project_type_id AND a.isactive=1 AND a.initiationid=:initiationid AND a.nodallab=d.lab_id";
	private static final String NODALLABLIST="SELECT lab_id,cluster_id,lab_name,lab_code FROM cluster_lab";
	private static final String SUBPROJECTLIST="SELECT initiationid,projecttitle FROM pfms_initiation WHERE mainid=:initiationid AND isactive=1 AND ismain='N' ";
	private static final String PROJECTDATAREVDATA="SELECT ppdr.projectdatarevid,ppdr.projectid,ppdr.filespath,ppdr.systemconfigimgname,ppdr.SystemSpecsFileName,ppdr.ProductTreeImgName,ppdr.PEARLImgName,ppdr.CurrentStageId,ppdr.RevisionNo,pps.projectstagecode,pps.projectstage,ppdr.revisiondate FROM pfms_project_data_rev ppdr, pfms_project_stage pps WHERE ppdr.CurrentStageId=pps.projectstageid AND  ppdr.projectdatarevid=:projectdatarevid";
	private static final String STATUSDETAILS="SELECT statusdetail FROM pfms_project_authority_actionlist WHERE status=:status ";
	private static final String INTEMPID="select empid from pfms_initiation where initiationid=:id";
	// private static final String PROJECTRISKDATALIST="SELECT DISTINCT am.actionmainid, am.actionitem, am.projectid, aas.actionstatus,am.type,am.scheduleminutesId  ,aas.actionassignid FROM action_main am , action_assign aas WHERE aas.actionmainid=am.actionmainid AND am.type='K' AND  CASE WHEN :projectid > 0 THEN am.projectid=:projectid ELSE aas.assignorlabcode=:LabCode AND am.projectid=:projectid  END"; 
	private static final String PROJECTRISKDATALIST="SELECT DISTINCT am.actionmainid, am.actionitem, am.projectid, aas.actionstatus,am.type,am.scheduleminutesId,aas.actionassignid,aas.actionno,aas.enddate,CONCAT(IFNULL(CONCAT(b.title,' '),''), b.emp_name) AS 'empname',c.designation,am.actionlinkid,aas.assignee,aas.assignor,am.actionlevel FROM action_main am,action_assign aas,employee b ,employee_desig c WHERE aas.actionmainid=am.actionmainid AND am.type='K' AND c.desig_id=b.desig_id AND aas.assignee=b.emp_id AND CASE WHEN :projectid > 0 THEN am.projectid=:projectid ELSE aas.assignorlabcode=:LabCode AND am.projectid=:projectid END"; 
	private static final String PROJECTRISKDATA ="SELECT DISTINCT am.actionmainid, am.actionitem, am.projectid, aas.actionstatus,am.type,aas.pdcorg,aas.enddate,aas.actionno,aas.actionassignid FROM action_main am ,action_assign aas WHERE aas.actionmainid=am.actionmainid AND  am.type='K' AND aas.ActionAssignId=:actionassignid";
	private static final String AUTHORITYATTACHMENT="SELECT a.authorityid,a.initiationid,a.authorityname,a.letterdate,a.letterno,c.attachmentname,b.emp_name,c.initiationauthorityfileid FROM pfms_initiation_authority a,employee b,pfms_initiation_authority_file c WHERE a.initiationid=:initiationid AND a.authorityname=b.emp_id AND a.authorityid=c.authorityid";
	private static final String AUTHORITYUPDATE="UPDATE pfms_initiation_authority SET authorityname=:authorityname, letterdate=:letterdate,letterno=:letterno, modifiedby=:modifiedby,modifieddate=:modifieddate WHERE initiationid=:initiationid";
	private static final String PROJECTMAINLIST="SELECT a.project_main_id,b.project_type_id,b.project_type,a.project_code,a.project_name, a.project_description, a.unit_code, a.sanction_no, a.sanction_date, a.total_sanction_cost, a.pdc, a.revision_no,a.objective,a.deliverable, a.project_director FROM project_main a, project_type b WHERE a.project_type_id=b.project_type_id AND a.is_active='1' AND b.is_active='1' ORDER BY a.sanction_date DESC";
	private static final String OFFICERLIST="SELECT a.emp_id, a.emp_no, a.emp_name, b.designation, a.ext_no, a.email, c.division_name, a.desig_id, a.division_id,a.lab_code FROM employee a,employee_desig b, division_master c WHERE a.desig_id= b.desig_id AND a.division_id= c.division_id AND a.is_active='1' AND a.emp_status IN ('P','R') ORDER BY a.sr_no=0,a.sr_no ASC ";
	private static final String PROJECTMAINEDITDATA="SELECT a.project_main_id,b.project_type_id,b.project_type,a.project_code,a.project_name, a.project_description, a.unit_code, a.sanction_no, a.sanction_date, a.sanction_cost_re, a.sanction_cost_fe, a.total_sanction_cost, a.pdc,a.project_director,a.proj_sanc_authority,a.board_reference,a.is_main_wc,a.work_center, a.revision_no,a.objective,a.deliverable, a.lab_participating,a.category_id,a.scope ,a.end_user  ,a.application , a.project_short_name, a.platform_id FROM project_main a, project_type b WHERE a.project_type_id=b.project_type_id AND a.project_main_id=:promainid AND a.is_active='1' AND b.is_active='1' ORDER BY a.project_type_id, a.project_main_id"; 
	private static final String PROJECTLIST1="SELECT a.projectid,b.projectmainid,b.projectcode AS id,a.projectcode,a.projectname, a.projectdescription, a.unitcode, a.sanctionno, a.sanctiondate, a.totalsanctioncost, a.pdc, a.revisionno,a.objective,a.deliverable,a.labcode FROM project_main b, project_master a, project_type c WHERE c.projecttypeid=b.projecttypeid AND a.projectmainid=b.projectmainid AND a.isactive='1' AND b.isactive='1' ORDER BY a.sanctiondate DESC";
	private static final String PROJECTTYPEMAINLIST="SELECT b.project_main_id,b.project_code as id from  project_main b WHERE  b.is_active='1' ";
	private static final String PROJECTCATEGORY="select classification_id, classification from pfms_security_classification";
	private static final String PROJECTEDITDATA="SELECT a.project_id,b.project_main_id,c.project_type as id,a.project_code,a.project_name, a.project_description, a.unit_code, a.sanction_no, a.sanction_date, a.sanction_cost_re, a.sanction_cost_fe, a.total_sanction_cost, a.pdc,a.project_director,a.proj_sanc_authority,a.board_reference,a.is_main_wc,a.work_center, a.revision_no,a.objective,a.deliverable,a.project_category, a.project_type ,a.project_short_name ,a.end_user , a.scope ,a.application ,a.lab_participating, CONCAT(IFNULL(CONCAT(e.title,' '),''), e.emp_name) AS 'empname',d.designation,e.mobile_no,e.Email,(SELECT MAX(remarks) FROM project_master_rev WHERE project_id=:proid) AS 'Remarks', a.platform_id  FROM project_main b, project_master a, project_type c,employee e,employee_desig d WHERE c.project_type_id=b.project_type_id and a.project_id=:proid and a.project_main_id=b.project_main_id and a.is_active='1' and b.is_active='1'AND a.project_director=e.emp_id AND e.desig_id=d.desig_id  ORDER BY a.project_id, a.project_main_id"; 
	private static final String PROJECTITEMLIST11="SELECT a.project_id, a.project_code,a.project_name FROM project_master a WHERE is_active='1'";
	private static final String PROJECTASSIGNLIST="SELECT a.ProjectEmployeeId, a.EmpId, a.ProjectId, CONCAT(IFNULL(CONCAT(b.title,' '),(IFNULL(CONCAT(b.salutation, ' '), ''))), b.emp_name) AS 'EmpName', c.Designation, d.division_code, b.mobile_no, b.email, a.RoleMasterId, b.lab_code, d.division_name, e.RoleCode, e.RoleName \r\n"
			+ "FROM project_employee a LEFT JOIN employee b ON a.EmpId=b.emp_id LEFT JOIN employee_desig c ON b.desig_id=c.desig_id LEFT JOIN division_master d ON b.division_id=d.division_id LEFT JOIN pfms_role_master e ON a.RoleMasterId=e.RoleMasterId\r\n"
			+ "WHERE a.IsActive=1 AND a.ProjectId=:proid ORDER BY (a.RoleMasterId = 0), a.RoleMasterId"; 
	private static final String USERLIST="SELECT  b.empid, CONCAT(IFNULL(CONCAT(b.title,' '),''), b.empname) AS 'empname',b.labcode,c.designation FROM employee b, employee_desig c  WHERE  b.isactive=1 AND b.desigid=c.desigid AND b.EmpId NOT IN( SELECT EmpId FROM project_employee WHERE ProjectId=:projectid AND IsActive='1')";
	private static final String PROJECTDATA="SELECT a.project_id, a.project_code FROM project_master a WHERE a.project_id=:proid";
	private static final String PROJECTRISKMATRIXDATA="SELECT riskid,projectid,actionmainid,description, severity,probability,mitigationplans,revisionno,LabCode,RPN,Impact,Category,RiskTypeId , status , remarks FROM pfms_risk WHERE actionmainid=:actionmainid";
	private static final String PROJECTRISKMATRIXREVLIST="SELECT rr.riskrevisionid,rr.projectid,rr.actionmainid,rr.description, rr.severity,rr.probability,rr.mitigationplans,rr.revisionno,rr.revisiondate,rr.RPN,rr.Impact,rr.category,rr.RisktypeId, rt.risktype FROM pfms_risk_rev rr, pfms_risk_type rt WHERE rr.risktypeid=rt.risktypeid AND actionmainid=:actionmainid  ORDER BY revisionno DESC";		
	private static final String RISKDATAPRESENTLIST="SELECT actionmainid , status FROM pfms_risk WHERE projectid=:projectid ";  
	private final static String PROCATSECDETAILS ="SELECT project_type_id, category_id FROM project_main WHERE project_main_id=:projectmainid";
	private static final String DIRECTOREMPDATA  ="SELECT a.lab_authority_id, CONCAT(IFNULL(CONCAT(b.title,' '),''), b.emp_name) AS 'empname' ,c.designation,'TCM'  FROM lab_master a, employee b,employee_desig c WHERE a.lab_authority_id=b.emp_id AND b.desig_id=c.desig_id AND a.lab_code=:labcode ";
	private static final String EMPDIVHEADDATA ="SELECT e2.emp_id, CONCAT(IFNULL(CONCAT(e2.title,' '),''), e2.emp_name) AS 'empname' , ed.designation,'Division Head'  FROM employee e1, employee e2, employee_desig ed, division_master dm WHERE e1.division_id=dm.division_id AND dm.division_head_id=e2.emp_id AND e2.desig_id=ed.desig_id  AND e1.emp_id=:empid";
	private static final String INITCOMMDEFAULT="SELECT comminitdefaultid, committeeid FROM committee_initiation_default";
	private static final String PROJECTTYPEMAINLISTNOTADDED="SELECT b.project_main_id,b.project_code AS id FROM  project_main b WHERE  b.is_active='1' AND b.project_main_id NOT IN (SELECT a.project_main_id FROM project_master a WHERE a.is_active=1 AND project_main_id>0)";
	private static final String PROJECTREVLIST = "SELECT pr.projectid, pr.revisionno,pm.projectcode AS 'ProjectMainCode', pr.projectcode, pr.projectname, pr.projectdescription, pr.unitcode, pt.projecttype,ps.classification,pr.sanctionno,pr.sanctiondate, CASE WHEN pr.totalsanctioncost>0 THEN ROUND(pr.totalsanctioncost/100000,2) ELSE pr.totalsanctioncost END AS 'TotalSanctionCost', pr.pdc, e.empname AS 'ProjectDirector' , ed.designation  FROM project_master p, project_master_rev pr , project_main pm, project_type pt, pfms_security_classification ps, employee e, employee_desig ed WHERE p.projectid=pr.projectid AND pr.projectmainid = pm.projectmainid AND pr.projecttype=pt.projecttypeid AND ps.classificationid =pr.projectcategory AND e.empid=pr.projectdirector AND e.desigid=ed.desigid  AND p.projectid=:projectid UNION SELECT pr.projectid, pr.revisionno,pm.projectcode AS 'ProjectMainCode', pr.projectcode, pr.projectname, pr.projectdescription, pr.unitcode, pt.projecttype,ps.classification,pr.sanctionno,pr.sanctiondate,CASE WHEN pr.totalsanctioncost>0 THEN ROUND(pr.totalsanctioncost/100000,2) ELSE pr.totalsanctioncost END AS 'TotalSanctionCost', pr.pdc, e.empname AS 'ProjectDirector' , ed.designation  FROM project_master pr, project_main pm, project_type pt, pfms_security_classification ps, employee e, employee_desig ed WHERE  pr.projectmainid = pm.projectmainid AND pr.projecttype=pt.projecttypeid AND ps.classificationid =pr.projectcategory AND e.empid=pr.projectdirector AND e.desigid=ed.desigid  AND pr.projectid=:projectid ";
	private static final String PROJECTMASTERATTACHLIST = "SELECT project_attach_id,file_name  FROM project_master_attach  WHERE project_id=:projectid";
	private static final String PROJECTMASTERATTACHDATA = "SELECT project_attach_id,file_name,path,original_file_name,project_id  FROM project_master_attach  WHERE projectattachid= :projectattachid ";
	private static final String PROJECTMASTERATTACHDELETE = "DELETE FROM  project_master_attach WHERE project_attach_id=:projectattachid ";



	@Autowired
	ProjectMasterRepo pmrepo;

	@PersistenceContext
	EntityManager manager;

	@Override
	public List<Object[]> ProjectIntiationList(String Empid,String LoginType,String LabCode) throws Exception {

		Query query=manager.createNativeQuery(PROJECTINTILIST);
		query.setParameter("empid", Long.parseLong(Empid));
		query.setParameter("logintype", LoginType);
		query.setParameter("LabCode", LabCode);
		List<Object[]> ProjectIntiationList=(List<Object[]>)query.getResultList();		
		return ProjectIntiationList;
	}

	@Override
	public List<Object[]> ProjectTypeList() throws Exception {	

		Query query=manager.createNativeQuery(PROJECTTYPELIST);
		List<Object[]> ProjectTypeList=(List<Object[]>)query.getResultList();		

		return ProjectTypeList;
	}

	@Override
	public List<Object[]> OfficerList() throws Exception {
		Query query=manager.createNativeQuery(OFFICERLIST);
		List<Object[]> OfficerList=(List<Object[]>)query.getResultList();
		return OfficerList;
	}

	@Override
	public Long ProjectMainAdd(ProjectMain proType) throws Exception {
		manager.persist(proType);
		manager.flush();
		return proType.getProjectMainId();
	}



	@Override
	public List<Object[]> PfmsCategoryList() throws Exception {
		Query query=manager.createNativeQuery(PROJECTCATEGORYLIST);
		List<Object[]> PfmsCategoryList=(List<Object[]>)query.getResultList();		

		return PfmsCategoryList;
	}

	@Override
	public List<Object[]> PfmsDeliverableList() throws Exception {
		Query query=manager.createNativeQuery(PROJECTDELIVERABLELIST);
		List<Object[]> PfmsDeliverableList=(List<Object[]>)query.getResultList();		

		return PfmsDeliverableList;
	}

	@Override
	public List<Object[]> LabList(String IntiationId) throws Exception {
		Query query=manager.createNativeQuery(LABLIST);
		query.setParameter("initiationid", Long.parseLong(IntiationId));
		List<Object[]> LabList=(List<Object[]>)query.getResultList();		

		return LabList;
	}

	@Transactional
	@Override
	public Long ProjectIntiationAdd(PfmsInitiation pfmsinitiation,PfmsNotification notification)
			throws Exception {
		manager.persist(pfmsinitiation);
		manager.persist(notification);

		manager.flush();
		return pfmsinitiation.getInitiationId();
	}

	@Override
	public Long ProjectShortNameCount(String ProjectShortName) throws Exception {
		try {
			Query query=manager.createNativeQuery(PROJECTSHORTNAMECHECK);
			query.setParameter("projectshortname", ProjectShortName);

			return (Long)query.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}

	}

	@Override
	public List<Object[]> ProjectDetailes(Long IntiationId) throws Exception {
		Query query=manager.createNativeQuery("CALL Pfms_Project_Initiation_Data(:InitiationId)");
		query.setParameter("InitiationId", IntiationId);
		List<Object[]> ProjectDetailes=(List<Object[]>)query.getResultList();		

		return ProjectDetailes;
	}

	@Override
	public List<Object[]> ProjectDetailsPreview(Long IntiationId) throws Exception {
		Query query=manager.createNativeQuery(PROJECTINTIDATAPREVIEW);
		query.setParameter("initiationid", IntiationId);
		List<Object[]> ProjectDetailsPreview=(List<Object[]>)query.getResultList();		

		return ProjectDetailsPreview;
	}


	@Override
	public Long ProjectIntiationDetailAdd(PfmsInitiationDetail pfmsinitiationdetail) throws Exception {
		manager.persist(pfmsinitiationdetail);

		manager.flush();
		return pfmsinitiationdetail.getInitiationDetailId();
	}


	@Override
	public int ProjectLabAdd(List<PfmsInitiationLab> pfmsinitiationlablist,PfmsInitiation pfmsinitiation) throws Exception {
		for(PfmsInitiationLab lab:pfmsinitiationlablist) {
			manager.persist(lab);
		}
		PfmsInitiation ExistingPfmsInitiation = manager.find(PfmsInitiation.class, pfmsinitiation.getInitiationId());
		if(ExistingPfmsInitiation != null) {
			ExistingPfmsInitiation.setLabCount(pfmsinitiation.getLabCount());
			ExistingPfmsInitiation.setModifiedBy(pfmsinitiation.getModifiedBy());
			ExistingPfmsInitiation.setModifiedDate(pfmsinitiation.getModifiedDate());
			manager.flush();
			return 1;
		}
		else {
			return 0;
		}
		
		
	}

	@Override
	public List<Object[]> BudgetItem(String BudegtId) throws Exception {
		Query query=manager.createNativeQuery(BUDEGTITEM);
		query.setParameter("budgetheadid", Long.parseLong(BudegtId));
		List<Object[]> BudgetItem=(List<Object[]>)query.getResultList();		

		return BudgetItem;
	}

	@Override
	public List<Object[]> ProjectIntiationItemList(String InitiationId) throws Exception {
		Query query=manager.createNativeQuery(PROJECTITEMLIST);
		query.setParameter("initiationid", Long.parseLong(InitiationId));
		List<Object[]> ProjectIntiationItemList=(List<Object[]>)query.getResultList();		

		return ProjectIntiationItemList;
	}

	@Override
	public Long ProjectIntiationCostAdd(PfmsInitiationCost pfmsinitiationcost,String ReFe,PfmsInitiation pfmsinitiation) throws Exception {
		manager.persist(pfmsinitiationcost);
		manager.flush();

		return pfmsinitiationcost.getInitiationCostId();
	}

	@Override
	public List<Object[]> ProjectIntiationLabList(String InitiationId) throws Exception {
		Query query=manager.createNativeQuery(PROJECTLABLIST);
		query.setParameter("initiationid",Long.parseLong(InitiationId));
		List<Object[]> ProjectIntiationLabList=(List<Object[]>)query.getResultList();		

		return ProjectIntiationLabList;
	}

	@Override
	public List<Object[]> BudgetHead() throws Exception {
		Query query=manager.createNativeQuery(BUDEGTHEADLIST);
		List<Object[]> BudgetHead=(List<Object[]>)query.getResultList();		

		return BudgetHead;
	}
	private static final String PROJECTDURATIONUPDATE="update pfms_initiation set projectduration=:duration "
			+ "where initiationid=:initiationid";

	@Override
	public Long ProjectScheduleAdd(List<PfmsInitiationSchedule> pfmsinitiationschedulelist,PfmsInitiation pfmsinitiation) throws Exception {

		Long count=0L;
		for(PfmsInitiationSchedule schedule:pfmsinitiationschedulelist) {

			manager.persist(schedule);
			count=schedule.getInitiationId();		
		}
		PfmsInitiation ExistingPfmsInitiation=manager.find(PfmsInitiation.class,  pfmsinitiationschedulelist.get(0).getInitiationId());
		if(ExistingPfmsInitiation != null) {
			
			ExistingPfmsInitiation.setProjectDuration(pfmsinitiation.getProjectDuration());
			count+=1L;
		}
		
		return count;
	}

	@Override
	public List<Object[]> ProjectIntiationScheduleList(String InitiationId) throws Exception {

		Query query=manager.createNativeQuery(PROJECTSCHEDULELIST);
		query.setParameter("initiationid", Long.parseLong(InitiationId));
		List<Object[]> ProjectIntiationScheduleList=(List<Object[]>)query.getResultList();		

		return ProjectIntiationScheduleList;
	}

	private static final String INTIATIONSCHEDULELIST = "from PfmsInitiationSchedule where InitiationId=:InitiationId and IsActive=1 ORDER BY Milestonestartedfrom ASC";
	@Override
	public List<PfmsInitiationSchedule> IntiationScheduleList(String InitiationId) throws Exception {

		Query query=manager.createQuery(INTIATIONSCHEDULELIST);
		query.setParameter("InitiationId", Long.parseLong(InitiationId));
		return (List<PfmsInitiationSchedule>)query.getResultList();		

	}

	@Override
	public List<Object[]> ProjectScheduleTotalMonthList(String InitiationId) throws Exception {
		// TODO Auto-generated method stub
		Query query =manager.createNativeQuery(PROJECTSCHEDULETOTALMONTHLIST);
		query.setParameter("initiationid", Long.parseLong(InitiationId));
		List<Object[]>ProjectScheduleTotalMonthList=(List<Object[]>)query.getResultList();
		return ProjectScheduleTotalMonthList;
	}	
	@Override
	public List<Object[]> MileStonenoTotalMonths(String InitiationId, int msno) throws Exception {
		// TODO Auto-generated method stub
		Query query =manager.createNativeQuery(MILESTONENOTOTALMONTHS);
		query.setParameter("InitiationId", Long.parseLong(InitiationId));
		query.setParameter("milestonestartedfrom",msno);
		List<Object[]> MileStonenoTotalMonths=(List<Object[]>)query.getResultList();
		return  MileStonenoTotalMonths;
	}

	@Override
	public Object[] ProjectProgressCount(String InitiationId) throws Exception {
		Query query=manager.createNativeQuery("CALL ProjectProgressBar(:InitiationId)");
		query.setParameter("InitiationId", Long.parseLong(InitiationId));
		Object[] ProjectProgressCount=(Object[])query.getSingleResult();	

		return ProjectProgressCount;
	}

	@Override
	public List<Object[]> ProjectIntiationDetailsList(String InitiationId) throws Exception {
		Query query=manager.createNativeQuery(PROJECTDETAILSLIST);
		query.setParameter("initiationid", Long.parseLong(InitiationId));
		List<Object[]> ProjectIntiationDetailsList=(List<Object[]> )query.getResultList();	


		return ProjectIntiationDetailsList;
	}


	@Override
	public List<Object[]> ProjectIntiationCostList(String InitiationId) throws Exception {
		Query query=manager.createNativeQuery(PROJECTCOSTLIST);
		query.setParameter("initiationid", Long.parseLong(InitiationId));
		List<Object[]> ProjectIntiationCostList=(List<Object[]>)query.getResultList();		

		return ProjectIntiationCostList;
	}

	@Override
	public List<Object[]> ProjectEditData(String IntiationId) throws Exception {
		Query query=manager.createNativeQuery(PROJECTINTIEDITDATA);
		query.setParameter("initiationid", Long.parseLong(IntiationId));
		List<Object[]> ProjectEditData=(List<Object[]>)query.getResultList();		

		return ProjectEditData;
	}
	
	@Transactional
	@Override
	public int ProjectIntiationEdit(PfmsInitiation pfmsinitiation) throws Exception {

		PfmsInitiation ExistingPfmsInitiation = manager.find(PfmsInitiation.class, pfmsinitiation.getInitiationId());
		if(ExistingPfmsInitiation != null && ExistingPfmsInitiation.getIsActive()==1) {
			
			ExistingPfmsInitiation.setProjectProgramme(pfmsinitiation.getProjectProgramme());
			ExistingPfmsInitiation.setProjectTypeId(pfmsinitiation.getProjectTypeId());
			ExistingPfmsInitiation.setClassificationId(pfmsinitiation.getClassificationId());
			ExistingPfmsInitiation.setProjectTitle(pfmsinitiation.getProjectTitle());
			ExistingPfmsInitiation.setIsPlanned(pfmsinitiation.getIsPlanned());
			ExistingPfmsInitiation.setIsMultiLab(pfmsinitiation.getIsMultiLab());
			ExistingPfmsInitiation.setDeliverable(pfmsinitiation.getDeliverable());
			ExistingPfmsInitiation.setModifiedBy(pfmsinitiation.getModifiedBy());
			ExistingPfmsInitiation.setModifiedDate(pfmsinitiation.getModifiedDate());
			ExistingPfmsInitiation.setNodalLab(pfmsinitiation.getNodalLab());
			ExistingPfmsInitiation.setRemarks(pfmsinitiation.getRemarks());
			ExistingPfmsInitiation.setEmpId(pfmsinitiation.getEmpId());
			ExistingPfmsInitiation.setPCDuration(pfmsinitiation.getPCDuration());
			ExistingPfmsInitiation.setPCRemarks(pfmsinitiation.getPCRemarks());
			ExistingPfmsInitiation.setIndicativeCost(pfmsinitiation.getIndicativeCost());
			ExistingPfmsInitiation.setStartDate(pfmsinitiation.getStartDate());
			
			return 1;
		}
		else {
			return 0;
		}
		
	}

	@Override
	public Double TotalIntiationCost(String IntiationId) throws Exception {
		Query query=manager.createNativeQuery(PROJECTINTITOTALCOST);
		query.setParameter("initiationid", Long.parseLong(IntiationId));
		BigDecimal TotalIntiationCost=(BigDecimal)query.getSingleResult();		
		if(TotalIntiationCost==null) {
			return 0.00;
		}
		return TotalIntiationCost.doubleValue();
	}

	@Override
	public List<Object[]> ProjectCostEditData(String InitiationCostId) throws Exception {
		Query query=manager.createNativeQuery(PROJECTINTICOSTDATA);
		query.setParameter("initiationcostid", Long.parseLong(InitiationCostId));
		List<Object[]> ProjectCostEditData=(List<Object[]>)query.getResultList();		

		return ProjectCostEditData;
	}

	

	@Override
	public int ProjectIntiationCostEdit(PfmsInitiationCost pfmsinitiationcost) throws Exception {
		PfmsInitiationCost ExistingPfmsInitiationCost= manager.find(PfmsInitiationCost.class, pfmsinitiationcost.getInitiationCostId());
		if(ExistingPfmsInitiationCost != null && ExistingPfmsInitiationCost.getIsActive()==1) {
			ExistingPfmsInitiationCost.setBudgetSancId(pfmsinitiationcost.getBudgetSancId());
			ExistingPfmsInitiationCost.setItemDetail(pfmsinitiationcost.getItemDetail());
			ExistingPfmsInitiationCost.setItemCost(pfmsinitiationcost.getItemCost());
			ExistingPfmsInitiationCost.setModifiedBy(pfmsinitiationcost.getModifiedBy());
			ExistingPfmsInitiationCost.setModifiedDate(pfmsinitiationcost.getModifiedDate());
			return 1;
		}
		else {
			return 0;
		}
		
	}

	

	@Override
	public int ProjectIntiationCostsUpdate(PfmsInitiation pfmsinitiation) throws Exception 
	{
		
		PfmsInitiation ExistingPfmsInitiation= manager.find(PfmsInitiation.class, pfmsinitiation.getInitiationId());
		if(ExistingPfmsInitiation != null) {
			ExistingPfmsInitiation.setFeCost(pfmsinitiation.getFeCost());
			ExistingPfmsInitiation.setReCost(pfmsinitiation.getReCost());
			ExistingPfmsInitiation.setProjectCost(pfmsinitiation.getProjectCost());
			ExistingPfmsInitiation.setModifiedDate(pfmsinitiation.getModifiedDate());
			ExistingPfmsInitiation.setModifiedBy(pfmsinitiation.getModifiedBy());
			
			return 1;
		}
		else {
			return 0;
		}
		

	}


	@Override
	public PfmsInitiationSchedule getInitiationSchedule(String InitiationScheduleId) throws Exception 
	{
		return manager.find(PfmsInitiationSchedule.class,Long.parseLong(InitiationScheduleId));
	}


	@Override
	public long ProjectScheduleEdit(PfmsInitiationSchedule pfmsinitiationschedule,PfmsInitiation pfmsinitiation ) throws Exception 
	{
		manager.merge(pfmsinitiationschedule);
		manager.flush();
		
		PfmsInitiation ExistingPfmsInitiation=manager.find(PfmsInitiation.class, pfmsinitiation.getInitiationId());
		if(ExistingPfmsInitiation!= null) {
			ExistingPfmsInitiation.setProjectDuration(pfmsinitiation.getProjectDuration());
			return 1;
		}
		else {
			return 0;
		}
		
	}


	private static final String INITIATIONCLEARTOTALMONTH ="UPDATE pfms_initiation_schedule SET MilestoneTotalMonth = 0 WHERE InitiationId=:InitiationId";
	@Override
	public int InitiationClearTotalMonth(String InitiationId) throws Exception 
	{
		Query query1=manager.createNativeQuery(INITIATIONCLEARTOTALMONTH);
		query1.setParameter("InitiationId", Long.parseLong(InitiationId));
		return query1.executeUpdate();
	}

	//@Override
	//public int ProjectScheduleEdit(PfmsInitiationSchedule pfmsinitiationschedule,PfmsInitiation pfmsinitiation ) throws Exception {
	//
	//	Query query=manager.createNativeQuery(PROJECTSHDULEUPDATE);
	//    query.setParameter("initiationscheduleid", pfmsinitiationschedule.getInitiationScheduleId());
	//    query.setParameter("milestoneactivity", pfmsinitiationschedule.getMilestoneActivity());
	//    query.setParameter("milestonemonth", pfmsinitiationschedule.getMilestoneMonth());
	//    query.setParameter("milestoneremark", pfmsinitiationschedule.getMilestoneRemark());
	//    query.setParameter("modifiedby",   pfmsinitiationschedule.getModifiedBy());
	//	query.setParameter("modifieddate", pfmsinitiationschedule.getModifiedDate());
	//	int count1=query.executeUpdate();
	//	
	//Query query1=manager.createNativeQuery(PROJECTSHDULEINITUPDATE);
	//    query1.setParameter("initiationid", pfmsinitiation.getInitiationId());
	//    query1.setParameter("milestonemonth", pfmsinitiation.getProjectDuration());
	//    int count2=query1.executeUpdate();
	//    
	//    
	//return count1+count2;
	//}
	@Override
	public int MilestoneTotalMonthUpdate(int newMilestoneTotalMonth, String IntiationId, String milestoneno)
			throws Exception {
		

		Query query= manager.createNativeQuery(MILESTONETOTALMONTHUPDATE);
		query.setParameter("newMilestoneTotalMonth", newMilestoneTotalMonth);
		query.setParameter("InitiationId", Long.parseLong(IntiationId));
		query.setParameter("milestoneno", Integer.parseInt(milestoneno));
		int count=query.executeUpdate();

		return count;
	}

	


	@Override
	public int ProjectScheduleDelete(PfmsInitiationSchedule pfmsinitiationschedule,PfmsInitiation pfmsInitiation ) throws Exception {
		PfmsInitiationSchedule ExistingPfmsInitiationSchedule=manager.find(PfmsInitiationSchedule.class, pfmsinitiationschedule.getInitiationScheduleId());
		PfmsInitiation ExistingPfmsInitiation= manager.find(PfmsInitiation.class, pfmsInitiation.getInitiationId());
		if(ExistingPfmsInitiationSchedule != null && ExistingPfmsInitiationSchedule.getIsActive()==1 
				&& ExistingPfmsInitiation!=null && ExistingPfmsInitiation.getIsActive()==1) {
			ExistingPfmsInitiationSchedule.setModifiedBy(pfmsinitiationschedule.getModifiedBy());
			ExistingPfmsInitiationSchedule.setModifiedDate(pfmsinitiationschedule.getModifiedDate());
			ExistingPfmsInitiationSchedule.setIsActive(0);
			
			ExistingPfmsInitiation.setProjectDuration(pfmsInitiation.getProjectDuration());
			
			return 1;
		}
		else {
			return 0;
		}
	}
	@Override
	public Long ProjectInitiationDetailsUpdate (PfmsInitiationDetail pfmsinitiationdetail,String Details) throws Exception{

		if(Details.equalsIgnoreCase("requirement")) {
			Query query=manager.createNativeQuery(PROJECTDETAILSREQUPDATE);
			query.setParameter("requirements", pfmsinitiationdetail.getRequirements());
			query.setParameter("reqbrief", pfmsinitiationdetail.getReqBrief());
			query.setParameter("initiationid", pfmsinitiationdetail.getInitiationId());
			query.setParameter("modifiedby", pfmsinitiationdetail.getModifiedBy());
			query.setParameter("modifieddate", pfmsinitiationdetail.getModifiedDate());
			int count=(int)query.executeUpdate();
		}
		if(Details.equalsIgnoreCase("objective")) {
			Query query=manager.createNativeQuery(PROJECTDETAILSOBJUPDATE);
			query.setParameter("objective", pfmsinitiationdetail.getObjective());
			query.setParameter("objbrief", pfmsinitiationdetail.getObjBrief());
			query.setParameter("initiationid", pfmsinitiationdetail.getInitiationId());
			query.setParameter("modifiedby", pfmsinitiationdetail.getModifiedBy());
			query.setParameter("modifieddate", pfmsinitiationdetail.getModifiedDate());
			int count=(int)query.executeUpdate();
		}
		if(Details.equalsIgnoreCase("scope")) {
			Query query=manager.createNativeQuery(PROJECTDETAILSSCOPEUPDATE);
			query.setParameter("scope", pfmsinitiationdetail.getScope());
			query.setParameter("scopebrief",pfmsinitiationdetail.getScopeBrief() );
			query.setParameter("initiationid", pfmsinitiationdetail.getInitiationId());
			query.setParameter("modifiedby", pfmsinitiationdetail.getModifiedBy());
			query.setParameter("modifieddate", pfmsinitiationdetail.getModifiedDate());
			int count=(int)query.executeUpdate();
		}
		if(Details.equalsIgnoreCase("multilab")) {
			Query query=manager.createNativeQuery(PROJECTDETAILSMULTIUPDATE);
			query.setParameter("multilab", pfmsinitiationdetail.getMultiLabWorkShare());
			query.setParameter("multibrief", pfmsinitiationdetail.getMultiLabBrief());
			query.setParameter("initiationid", pfmsinitiationdetail.getInitiationId());
			query.setParameter("modifiedby", pfmsinitiationdetail.getModifiedBy());
			query.setParameter("modifieddate", pfmsinitiationdetail.getModifiedDate());
			int count=(int)query.executeUpdate();
		}
		if(Details.equalsIgnoreCase("earlierwork")) {
			Query query=manager.createNativeQuery(PROJECTDETAILSEARLYUPDATE);
			query.setParameter("earlierwork", pfmsinitiationdetail.getEarlierWork());
			query.setParameter("earlibrief",pfmsinitiationdetail.getEarlierWorkBrief() );
			query.setParameter("initiationid", pfmsinitiationdetail.getInitiationId());
			query.setParameter("modifiedby", pfmsinitiationdetail.getModifiedBy());
			query.setParameter("modifieddate", pfmsinitiationdetail.getModifiedDate());
			int count=(int)query.executeUpdate();
		}
		if(Details.equalsIgnoreCase("competency")) {
			Query query=manager.createNativeQuery(PROJECTDETAILSCOMPETANCYUPDATE);
			query.setParameter("competancy", pfmsinitiationdetail.getCompentencyEstablished());
			query.setParameter("combrief", pfmsinitiationdetail.getCompentencyBrief());
			query.setParameter("initiationid", pfmsinitiationdetail.getInitiationId());
			query.setParameter("modifiedby", pfmsinitiationdetail.getModifiedBy());
			query.setParameter("modifieddate", pfmsinitiationdetail.getModifiedDate());
			int count=(int)query.executeUpdate();
		}
		if(Details.equalsIgnoreCase("needofproject")) {
			Query query=manager.createNativeQuery(PROJECTDETAILSNEEDOFUPDATE);
			query.setParameter("needofproject", pfmsinitiationdetail.getNeedOfProject());
			query.setParameter("needbrief", pfmsinitiationdetail.getNeedOfProjectBrief());
			query.setParameter("initiationid", pfmsinitiationdetail.getInitiationId());
			query.setParameter("modifiedby", pfmsinitiationdetail.getModifiedBy());
			query.setParameter("modifieddate", pfmsinitiationdetail.getModifiedDate());
			int count=(int)query.executeUpdate();
		}
		if(Details.equalsIgnoreCase("technology")) {
			Query query=manager.createNativeQuery(PROJECTDETAILSTECHUPDATE);
			query.setParameter("technology", pfmsinitiationdetail.getTechnologyChallanges());
			query.setParameter("techbrief", pfmsinitiationdetail.getTechnologyBrief());
			query.setParameter("initiationid", pfmsinitiationdetail.getInitiationId());
			query.setParameter("modifiedby", pfmsinitiationdetail.getModifiedBy());
			query.setParameter("modifieddate", pfmsinitiationdetail.getModifiedDate());
			int count=(int)query.executeUpdate();
		}
		if(Details.equalsIgnoreCase("riskmitigation")) {
			Query query=manager.createNativeQuery(PROJECTDETAILSRISKUPDATE);
			query.setParameter("riskmitigation", pfmsinitiationdetail.getRiskMitigation());
			query.setParameter("riskbrief",pfmsinitiationdetail.getRiskMitigationBrief() );
			query.setParameter("initiationid", pfmsinitiationdetail.getInitiationId());
			query.setParameter("modifiedby", pfmsinitiationdetail.getModifiedBy());
			query.setParameter("modifieddate", pfmsinitiationdetail.getModifiedDate());
			int count=(int)query.executeUpdate();
		}
		if(Details.equalsIgnoreCase("proposal")) {
			Query query=manager.createNativeQuery(PROJECTDETAILSPROPOSALUPDATE);
			query.setParameter("proposal", pfmsinitiationdetail.getProposal());
			query.setParameter("probrief", pfmsinitiationdetail.getProposalBrief());
			query.setParameter("initiationid", pfmsinitiationdetail.getInitiationId());
			query.setParameter("modifiedby", pfmsinitiationdetail.getModifiedBy());
			query.setParameter("modifieddate", pfmsinitiationdetail.getModifiedDate());
			int count=(int)query.executeUpdate();
		}
		if(Details.equalsIgnoreCase("realization")) {
			Query query=manager.createNativeQuery(PROJECTDETAILSREALIZATIONUPDATE);
			query.setParameter("realization", pfmsinitiationdetail.getRealizationPlan());
			query.setParameter("realizationbrief", pfmsinitiationdetail.getRealizationBrief());
			query.setParameter("initiationid", pfmsinitiationdetail.getInitiationId());
			query.setParameter("modifiedby", pfmsinitiationdetail.getModifiedBy());
			query.setParameter("modifieddate", pfmsinitiationdetail.getModifiedDate());
			int count=(int)query.executeUpdate();
		}
		if(Details.equalsIgnoreCase("worldscenario")) {
			Query query=manager.createNativeQuery(PROJECTDETAILSWORLDUPDATE);
			query.setParameter("worldscenario", pfmsinitiationdetail.getWorldScenario());
			query.setParameter("worldbrief", pfmsinitiationdetail.getWorldScenarioBrief());
			query.setParameter("initiationid", pfmsinitiationdetail.getInitiationId());
			query.setParameter("modifiedby", pfmsinitiationdetail.getModifiedBy());
			query.setParameter("modifieddate", pfmsinitiationdetail.getModifiedDate());
			int count=(int)query.executeUpdate();
		}


		return pfmsinitiationdetail.getInitiationId();
	}

	//@Override
	//public Long ProjectInitiationDetailsUpdate (PfmsInitiationDetail pfmsinitiationdetail,String Details) throws Exception{
	//	
	//	if(Details.equalsIgnoreCase("requirement")) {
	//		Query query=manager.createNativeQuery(PROJECTDETAILSREQUPDATE);
	//		query.setParameter("requirements", pfmsinitiationdetail.getRequirements());
	//		query.setParameter("initiationid", pfmsinitiationdetail.getInitiationId());
	//		query.setParameter("modifiedby", pfmsinitiationdetail.getModifiedBy());
	//		query.setParameter("modifieddate", pfmsinitiationdetail.getModifiedDate());
	//		int count=(int)query.executeUpdate();
	//	}
	//	if(Details.equalsIgnoreCase("objective")) {
	//		Query query=manager.createNativeQuery(PROJECTDETAILSOBJUPDATE);
	//		query.setParameter("objective", pfmsinitiationdetail.getObjective());
	//		query.setParameter("initiationid", pfmsinitiationdetail.getInitiationId());
	//		query.setParameter("modifiedby", pfmsinitiationdetail.getModifiedBy());
	//		query.setParameter("modifieddate", pfmsinitiationdetail.getModifiedDate());
	//		int count=(int)query.executeUpdate();
	//	}
	//	if(Details.equalsIgnoreCase("scope")) {
	//		Query query=manager.createNativeQuery(PROJECTDETAILSSCOPEUPDATE);
	//		query.setParameter("scope", pfmsinitiationdetail.getScope());
	//		query.setParameter("initiationid", pfmsinitiationdetail.getInitiationId());
	//		query.setParameter("modifiedby", pfmsinitiationdetail.getModifiedBy());
	//		query.setParameter("modifieddate", pfmsinitiationdetail.getModifiedDate());
	//		int count=(int)query.executeUpdate();
	//	}
	//	if(Details.equalsIgnoreCase("multilab")) {
	//		Query query=manager.createNativeQuery(PROJECTDETAILSMULTIUPDATE);
	//		query.setParameter("multilab", pfmsinitiationdetail.getMultiLabWorkShare());
	//		query.setParameter("initiationid", pfmsinitiationdetail.getInitiationId());
	//		query.setParameter("modifiedby", pfmsinitiationdetail.getModifiedBy());
	//		query.setParameter("modifieddate", pfmsinitiationdetail.getModifiedDate());
	//		int count=(int)query.executeUpdate();
	//	}
	//	if(Details.equalsIgnoreCase("earlierwork")) {
	//		Query query=manager.createNativeQuery(PROJECTDETAILSEARLYUPDATE);
	//		query.setParameter("earlierwork", pfmsinitiationdetail.getEarlierWork());
	//		query.setParameter("initiationid", pfmsinitiationdetail.getInitiationId());
	//		query.setParameter("modifiedby", pfmsinitiationdetail.getModifiedBy());
	//		query.setParameter("modifieddate", pfmsinitiationdetail.getModifiedDate());
	//		int count=(int)query.executeUpdate();
	//	}
	//	if(Details.equalsIgnoreCase("competency")) {
	//		Query query=manager.createNativeQuery(PROJECTDETAILSCOMPETANCYUPDATE);
	//		query.setParameter("competancy", pfmsinitiationdetail.getCompentencyEstablished());
	//		query.setParameter("initiationid", pfmsinitiationdetail.getInitiationId());
	//		query.setParameter("modifiedby", pfmsinitiationdetail.getModifiedBy());
	//		query.setParameter("modifieddate", pfmsinitiationdetail.getModifiedDate());
	//		int count=(int)query.executeUpdate();
	//	}
	//	if(Details.equalsIgnoreCase("needofproject")) {
	//		Query query=manager.createNativeQuery(PROJECTDETAILSNEEDOFUPDATE);
	//		query.setParameter("needofproject", pfmsinitiationdetail.getNeedOfProject());
	//		query.setParameter("initiationid", pfmsinitiationdetail.getInitiationId());
	//		query.setParameter("modifiedby", pfmsinitiationdetail.getModifiedBy());
	//		query.setParameter("modifieddate", pfmsinitiationdetail.getModifiedDate());
	//		int count=(int)query.executeUpdate();
	//	}
	//	if(Details.equalsIgnoreCase("technology")) {
	//		Query query=manager.createNativeQuery(PROJECTDETAILSTECHUPDATE);
	//		query.setParameter("technology", pfmsinitiationdetail.getTechnologyChallanges());
	//		query.setParameter("initiationid", pfmsinitiationdetail.getInitiationId());
	//		query.setParameter("modifiedby", pfmsinitiationdetail.getModifiedBy());
	//		query.setParameter("modifieddate", pfmsinitiationdetail.getModifiedDate());
	//		int count=(int)query.executeUpdate();
	//	}
	//	if(Details.equalsIgnoreCase("riskmitigation")) {
	//		Query query=manager.createNativeQuery(PROJECTDETAILSRISKUPDATE);
	//		query.setParameter("riskmitigation", pfmsinitiationdetail.getRiskMitigation());
	//		query.setParameter("initiationid", pfmsinitiationdetail.getInitiationId());
	//		query.setParameter("modifiedby", pfmsinitiationdetail.getModifiedBy());
	//		query.setParameter("modifieddate", pfmsinitiationdetail.getModifiedDate());
	//		int count=(int)query.executeUpdate();
	//	}
	//	if(Details.equalsIgnoreCase("proposal")) {
	//		Query query=manager.createNativeQuery(PROJECTDETAILSPROPOSALUPDATE);
	//		query.setParameter("proposal", pfmsinitiationdetail.getProposal());
	//		query.setParameter("initiationid", pfmsinitiationdetail.getInitiationId());
	//		query.setParameter("modifiedby", pfmsinitiationdetail.getModifiedBy());
	//		query.setParameter("modifieddate", pfmsinitiationdetail.getModifiedDate());
	//		int count=(int)query.executeUpdate();
	//	}
	//	if(Details.equalsIgnoreCase("realization")) {
	//		Query query=manager.createNativeQuery(PROJECTDETAILSREALIZATIONUPDATE);
	//		query.setParameter("realization", pfmsinitiationdetail.getRealizationPlan());
	//		query.setParameter("initiationid", pfmsinitiationdetail.getInitiationId());
	//		query.setParameter("modifiedby", pfmsinitiationdetail.getModifiedBy());
	//		query.setParameter("modifieddate", pfmsinitiationdetail.getModifiedDate());
	//		int count=(int)query.executeUpdate();
	//	}
	//	if(Details.equalsIgnoreCase("worldscenario")) {
	//		Query query=manager.createNativeQuery(PROJECTDETAILSWORLDUPDATE);
	//		query.setParameter("worldscenario", pfmsinitiationdetail.getWorldScenario());
	//		query.setParameter("initiationid", pfmsinitiationdetail.getInitiationId());
	//		query.setParameter("modifiedby", pfmsinitiationdetail.getModifiedBy());
	//		query.setParameter("modifieddate", pfmsinitiationdetail.getModifiedDate());
	//		int count=(int)query.executeUpdate();
	//	}
	//	
	//
	//	return pfmsinitiationdetail.getInitiationId();
	//}

	@Override
	public Integer ProjectScheduleMonth(String InitiationId) throws Exception {
		try {
			Query query=manager.createNativeQuery(PROJECTSCHTOTALMONTH);
			query.setParameter("InitiationId", Long.parseLong(InitiationId));
			return (Integer)query.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override/*L.A*/
	public Integer ProjectDurationMonth(String InitiationId) throws Exception {

		try {
			Query query=manager.createNativeQuery(PROJECTDURMONTH);
			query.setParameter("InitiationId", Long.parseLong(InitiationId));
			return (Integer)query.getSingleResult();	
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
			

	}
	@Override/*L.A*/
	public Integer MilestoneScheduleMonth(String initiationscheduleid,String IntiationId) throws Exception {
		
		try {
			Query query=manager.createNativeQuery(MILESCHMONTH);
			query.setParameter("initiationscheduleid", Long.parseLong(initiationscheduleid));;
			query.setParameter("IntiationId", Long.parseLong(IntiationId));
			return (Integer)query.getSingleResult();	
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}


	@Override
	public int ProjectMileStoneNo(String InitiationId) throws Exception {
		try {
			Query query=manager.createNativeQuery(MILESTONENO);
			query.setParameter("InitiationId", Long.parseLong(InitiationId));
			return (Integer)query.getSingleResult();	
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int ProjectScheduleEditData(String InitiationScheduleId) throws Exception {
		try {
			Query query=manager.createNativeQuery(SCDULEMONTH);
			query.setParameter("initiationscheduleid", Long.parseLong(InitiationScheduleId));
			return (Integer)query.getSingleResult();	
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override/*L.A*/
	public int mileStonemonthprevious(String IntiationId, String milestoneno) throws Exception {
		try {

			Query query=manager.createNativeQuery(PREVIOUSMONTH);
			query.setParameter("IntiationId", Long.parseLong(IntiationId));
			query.setParameter("milestoneno", Integer.parseInt(milestoneno));
			return (Integer)query.getSingleResult();	
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}


	@Override/*L.A*/
	public int milestonenototalmonth(String IntiationId, String milestoneno) throws Exception {
		try {
			
			Query query=manager.createNativeQuery(MILESTONENOTOTALMONTH);
			query.setParameter("IntiationId", Long.parseLong(IntiationId));
			query.setParameter("milestoneno", Integer.parseInt(milestoneno));
			return (Integer)query.getSingleResult();	
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public Long ProjectInitiationAttachmentAdd(PfmsInitiationAttachment pfmsinitiationattachment,
			PfmsInitiationAttachmentFile pfmsinitiationattachmentfile) throws Exception {
		manager.persist(pfmsinitiationattachment);
		pfmsinitiationattachmentfile.setInitiationAttachmentId(pfmsinitiationattachment.getInitiationAttachmentId());
		manager.persist(pfmsinitiationattachmentfile);
		manager.flush();
		return pfmsinitiationattachmentfile.getInitiationAttachmentFileId();
	}

	@Override
	public List<Object[]> ProjectIntiationAttachment(String InitiationId) throws Exception {

		Query query=manager.createNativeQuery(PROJECTINTATTACH);
		query.setParameter("initiationid", Long.parseLong(InitiationId));
		List<Object[]> ProjectIntiationAttachment=(List<Object[]>)query.getResultList();		

		return ProjectIntiationAttachment;
	}

	@Override
	public List<Object[]> AuthorityAttachment(String InitiationId) throws Exception {

		Query query=manager.createNativeQuery(AUTHORITYATTACHMENT);
		query.setParameter("initiationid", Long.parseLong(InitiationId));

		List<Object[]> AuthorityAttachment=(List<Object[]>)query.getResultList();		

		return AuthorityAttachment;
	}

	@Override
	public PfmsInitiationAttachmentFile ProjectIntiationAttachmentFile(String InitiationAttachmentId) throws Exception {
		PfmsInitiationAttachmentFile ProjectIntiationAttachmentFile= manager.find(PfmsInitiationAttachmentFile.class,Long.parseLong(InitiationAttachmentId));
		return ProjectIntiationAttachmentFile;
	}

	@Override
	public Object[] ProjectIntiationFileName(long AttachmentId) throws Exception
	{
		Query query=manager.createNativeQuery("SELECT a.initiationattachmentid,a.initiationid,a.filename,a.filenamepath FROM pfms_initiation_attachment a WHERE isactive='1' AND a.initiationattachmentid=:AttachmentId");
		query.setParameter("AttachmentId", AttachmentId);
		List<Object[]> ProjectApprovePdList=(List<Object[]>)query.getResultList();
		Object[] result =null;
		if(ProjectApprovePdList!=null && ProjectApprovePdList.size()>0) {
			result=ProjectApprovePdList.get(0);
		}
		return result;
	}
	

	@Override
	public int ProjectInitiationAttachmentDelete(PfmsInitiationAttachment pfmsinitiationattachment) throws Exception {
		PfmsInitiationAttachment ExistingPfmsInitiationAttachment = manager.find(PfmsInitiationAttachment.class, pfmsinitiationattachment.getInitiationAttachmentId());
		if(ExistingPfmsInitiationAttachment != null) {
			ExistingPfmsInitiationAttachment.setIsActive(0);
			ExistingPfmsInitiationAttachment.setModifiedBy(pfmsinitiationattachment.getModifiedBy());
			ExistingPfmsInitiationAttachment.setModifiedDate(pfmsinitiationattachment.getModifiedDate());
			return 1;
		}
		else {
			return 0;
		}
		
	}
	
	@Override
	public int ProjectInitiationAttachmentUpdate(PfmsInitiationAttachment pfmsinitiationattachment) throws Exception {
		PfmsInitiationAttachment ExistingPfmsInitiationAttachment = manager.find(PfmsInitiationAttachment.class, pfmsinitiationattachment.getInitiationAttachmentId());
		if(ExistingPfmsInitiationAttachment != null) {
			ExistingPfmsInitiationAttachment.setFileName(pfmsinitiationattachment.getFileName());
			ExistingPfmsInitiationAttachment.setModifiedBy(pfmsinitiationattachment.getModifiedBy());
			ExistingPfmsInitiationAttachment.setModifiedDate(pfmsinitiationattachment.getModifiedDate());
			return 1;
		}
		else {
			return 0;
		}
		
	}

	@Override
	public String ProjectIntiationAttachmentFileName(String InitiationAttachmentId) throws Exception {

		Query query=manager.createNativeQuery(PROJECTINTATTACHFILENAME);
		query.setParameter("initiationattachmentid", Long.parseLong(InitiationAttachmentId));
		String ProjectIntiationAttachmentFileName=(String)query.getSingleResult();		

		return ProjectIntiationAttachmentFileName;
	}

	@Override
	public String ProjectIntiationAttachmentFileNamePath(String InitiationAttachmentId) throws Exception {

		Query query=manager.createNativeQuery(PROJECTINTATTACHFILENAMEPATH);
		query.setParameter("initiationattachmentid", Long.parseLong(InitiationAttachmentId));
		String ProjectIntiationAttachmentFileNamePath=(String)query.getSingleResult();		

		return ProjectIntiationAttachmentFileNamePath;
	}
	private static final String PROJECTINTIUPDATE="update  pfms_initiation set labcount=:labcount ,modifiedby=:modifiedby,"
			+ "modifieddate=:modifieddate where initiationid=:initiationid";
	
	private static final String PROJECTINTLABUPDATE="update pfms_initiation_lab set isactive='0' ,modifiedby=:modifiedby, "
			+ "modifieddate=:modifieddate where initiationlabid=:initiationlabid";

	@Override
	public int ProjectLabdelete(PfmsInitiationLab pfmsinitiationlab, PfmsInitiation pfmsinitiation) throws Exception {


		PfmsInitiation ExistingPfmsInitiation = manager.find(PfmsInitiation.class, pfmsinitiation.getInitiationId());
		PfmsInitiationLab ExistingPfmsInitiationLab = manager.find(PfmsInitiationLab.class, pfmsinitiationlab.getInitiationLabId());
		
		if(ExistingPfmsInitiation != null && ExistingPfmsInitiationLab != null) {
			ExistingPfmsInitiation.setLabCount(pfmsinitiation.getLabCount());
			ExistingPfmsInitiation.setModifiedBy(pfmsinitiation.getModifiedBy());
			ExistingPfmsInitiation.setModifiedDate(pfmsinitiation.getModifiedDate());
			
			ExistingPfmsInitiationLab.setIsActive(0);
			ExistingPfmsInitiationLab.setModifiedBy(pfmsinitiationlab.getModifiedBy());
			ExistingPfmsInitiationLab.setModifiedDate(pfmsinitiationlab.getModifiedDate());
			manager.flush();
			return 1;
		}
		else {
			return 0;
		}

	}

	@Override
	public int ProjectIntiationCostDelete(PfmsInitiationCost pfmsinitiationcost) throws Exception {
		Query query=manager.createNativeQuery(PROJECTINTCOSTDELETE);
		query.setParameter("initiationcostid", pfmsinitiationcost.getInitiationCostId());
		query.setParameter("modifiedby",   pfmsinitiationcost.getModifiedBy());
		query.setParameter("modifieddate", pfmsinitiationcost.getModifiedDate());


		return query.executeUpdate();
	}

	private static final String PROJECTSTATUSUPDATE="update pfms_initiation set projectstatus=:projectstatus,"
			+ "approvalid=:approvalid,modifiedby=:modifiedby, modifieddate=:modifieddate where initiationid=:initiationid ";

	@Override
	public int ProjectIntiationStatusUpdate(PfmsInitiation pfmsinitiation,PfmsApproval pfmsapproval,PfmsNotification notification) throws Exception {

		manager.persist(pfmsapproval);
		manager.persist(notification);
		
		PfmsInitiation ExistingPfmsInitiation = manager.find(PfmsInitiation.class, pfmsinitiation.getInitiationId());
		if(ExistingPfmsInitiation != null) {
			ExistingPfmsInitiation.setProjectStatus(pfmsinitiation.getProjectStatus());
			ExistingPfmsInitiation.setApprovalId(pfmsapproval.getProjectApprovalId());
			ExistingPfmsInitiation.setModifiedBy(pfmsinitiation.getModifiedBy());
			ExistingPfmsInitiation.setModifiedDate(pfmsinitiation.getModifiedDate());
			System.err.println("working");
			manager.flush();
			return 1;
		}
		else {
			return 0;
		}
	}
	private static final String REQSTATUSUPDATE="UPDATE pfms_initiation_req_status a SET a.status=:statuscode,a.approvalId=:approveid,a.modifiedby=:modifiedby,a.modifiedDate=:modifiedDate WHERE ReqInitiationId=:ReqInitiationId AND isactive=1";
	@Override
	public int ProjectRequirementStatusUpdate(PfmsReqStatus prs, PfmsRequirementApproval approval,PfmsNotification notification) throws Exception {
		
		manager.persist(notification);
		manager.persist(approval);
		Query query=manager.createNativeQuery(REQSTATUSUPDATE);
		query.setParameter("statuscode", prs.getStatus());
		query.setParameter("approveid", approval.getReqApprovalId());
		query.setParameter("modifiedby", prs.getModifiedBy());
		query.setParameter("modifiedDate", prs.getModifiedDate());
		query.setParameter("ReqInitiationId", prs.getReqInitiationId());
		int count=query.executeUpdate();
		manager.flush();
		return count;
	}


	@Override
	public Long ProjectForwardStatus(String InitiationId) throws Exception {
		try {
			Query query=manager.createNativeQuery("CALL ProjectForwardStatus(:InitiationId)");
			query.setParameter("InitiationId", Long.parseLong(InitiationId));
			return (Long)query.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
			
	}

	@Override
	public List<Object[]> ProjectActionList(String ProjectAuthorityId) throws Exception {

		Query query=manager.createNativeQuery(PROJECTACTIONLIST);
		query.setParameter("projectauthorityid", Long.parseLong(ProjectAuthorityId));
		List<Object[]> ProjectActionList=(List<Object[]>)query.getResultList();		

		return ProjectActionList;
	}

	@Override
	public List<Object[]> ProjectApprovePdList(String EmpId) throws Exception {

		Query query=manager.createNativeQuery("CALL Project_Approval_Pd(:EmpId)");
		query.setParameter("EmpId", Long.parseLong(EmpId));
		List<Object[]> ProjectApprovePdList=(List<Object[]>)query.getResultList();	
		return ProjectApprovePdList;
	}

	@Override
	public int ProjectApprove(PfmsInitiation pfmsinitiation, PfmsApproval pfmsapproval,PfmsNotification notification) throws Exception {
		manager.persist(pfmsapproval);
		manager.persist(notification);

		PfmsInitiation ExistingPfmsInitiation = manager.find(PfmsInitiation.class, pfmsinitiation.getInitiationId());
		if(ExistingPfmsInitiation != null) {
			ExistingPfmsInitiation.setProjectStatus(pfmsinitiation.getProjectStatus());
			ExistingPfmsInitiation.setApprovalId(pfmsapproval.getProjectApprovalId());
			ExistingPfmsInitiation.setModifiedBy(pfmsinitiation.getModifiedBy());
			ExistingPfmsInitiation.setModifiedDate(pfmsinitiation.getModifiedDate());
			manager.flush();
			return 1;
		}
		else {
			return 0;
		}
	}


	@Override
	public double PfmsInitiationRefeSum(String initiationid,String refe) throws Exception
	{
		Query query=manager.createNativeQuery(PFMSINITIATIONREFESUM);
		query.setParameter("initiationid", Long.parseLong(initiationid));
		query.setParameter("refe", refe);
		Object temp=query.getResultList().get(0);
		double costsum=0.00;
		if(!(temp==null)) 
		{		
			costsum=Double.parseDouble(temp.toString());
		}


		return costsum;
	}


	@Override
	public List<Object[]> EmployeeList(String LabCode) throws Exception {
		Query query=manager.createNativeQuery(EMPLOYEELIST);
		query.setParameter("LabCode", LabCode);
		List<Object[]> EmployeeList=(List<Object[]>)query.getResultList();	
		return EmployeeList;
	}



	@Override
	public List<Object[]> ProjectStatusList(String EmpId,String LoginType,String LabCode) throws Exception{

		Query query=manager.createNativeQuery(PROJECTSTATUSLIST);
		query.setParameter("empid", Long.parseLong(EmpId));
		query.setParameter("logintype", LoginType);
		query.setParameter("LabCode", LabCode);
		List <Object[]> ProjectStatusList= (List<Object[]>)query.getResultList();
		return ProjectStatusList;

	}

	@Override
	public List<Object[]> ProjectApprovalTracking(String InitiationId) throws Exception{

		Query query=manager.createNativeQuery(PROJECTAPPROVALTRACKING);
		query.setParameter("initiationid", Long.parseLong(InitiationId));
		List<Object[]> ProjectApprovalTracking=(List<Object[]>)query.getResultList();

		return ProjectApprovalTracking;
	}

	@Override
	public List<Object[]> ProjectApproveRtmddoList(String EmpId) throws Exception {

		Query query=manager.createNativeQuery("CALL Project_Approval_Rtmddo(:EmpId)");
		query.setParameter("EmpId", Long.parseLong(EmpId));
		List<Object[]> ProjectApproveRtmddoList=(List<Object[]>)query.getResultList();	
		return ProjectApproveRtmddoList;
	}

	@Override
	public List<Object[]> ProjectApproveTccList(String EmpId) throws Exception {

		Query query=manager.createNativeQuery("CALL Project_Approval_Tcc(:EmpId)");
		query.setParameter("EmpId", Long.parseLong(EmpId));
		List<Object[]> ProjectApproveTccList=(List<Object[]>)query.getResultList();	
		return ProjectApproveTccList;
	}



	@Override
	public Double TotalIntiationFeCost(String IntiationId) throws Exception {
		Query query=manager.createNativeQuery(PROJECTINTITOTALFECOST);
		query.setParameter("initiationid", Long.parseLong(IntiationId));
		BigDecimal TotalIntiationFeCost=(BigDecimal)query.getSingleResult();		
		if(TotalIntiationFeCost==null) {
			return 0.00;
		}
		return TotalIntiationFeCost.doubleValue();
	}

	@Override
	public Double TotalIntiationReCost(String IntiationId) throws Exception {

		Query query=manager.createNativeQuery(PROJECTINTITOTALRECOST);
		query.setParameter("initiationid", Long.parseLong(IntiationId));
		BigDecimal TotalIntiationReCost=(BigDecimal)query.getSingleResult();		
		if(TotalIntiationReCost==null) {
			return 0.00;
		}
		return TotalIntiationReCost.doubleValue();
	}

	@Override
	public List<Object[]> ProjectCost(Long IntiationId) throws Exception {

		Query query=manager.createNativeQuery(PROJECTCOSTDATA);
		query.setParameter("initiationid", IntiationId);
		List<Object[]> ProjectCost=(List<Object[]>)query.getResultList();		

		return ProjectCost;
	}



	@Override
	public List<Object[]> TccProjectList() throws Exception {

		Query query=manager.createNativeQuery(TCCPROJECTLIST);
		List<Object[]> TccProjectList=(List<Object[]>)query.getResultList();		

		return TccProjectList;
	}



	@Override
	public List<Object[]> ExpertList() throws Exception {

		Query query=manager.createNativeQuery(EXPERTLIST);
		List<Object[]> ExpertList=(List<Object[]>)query.getResultList();
		return ExpertList;
	}


	@Override
	public Long DivisionHeadId(String EmpId) throws Exception {
		try {
			Query query=manager.createNativeQuery(DIVISIONHEADID);
			query.setParameter("empid", Long.parseLong(EmpId));
			return (Long)query.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
		
	}

	@Override
	public Long RtmddoId() throws Exception {
		try {
			Query query=manager.createNativeQuery(RTMDDOID);
			return (Long)query.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	}

	@Override
	public Long TccChairpersonId(String Labcode) throws Exception {
		try {
			Query query=manager.createNativeQuery(TCCCHAIRPERSONID);
			query.setParameter("labcode", Labcode);
			return (Long)query.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	}

	@Override
	public Long CcmChairpersonId(String Labcode) throws Exception {
		try {
			Query query=manager.createNativeQuery(CCMCHAIRPERSONID);
			query.setParameter("labcode", Labcode);
	
			return (Long)query.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	}

	@Override
	public List<Object[]> ProjectMainList() throws Exception {
		Query query = manager.createNativeQuery(PROJECTMAINLIST);
		List<Object[]> ProjectMainList = (List<Object[]>) query.getResultList();
		return ProjectMainList;
	}
	

	@Override
	public Long ProjectMainClose(ProjectMain proType) throws Exception {
		Long count=0L;
		try {
			ProjectMain ExistingProjectMain = manager.find(ProjectMain.class, proType.getProjectMainId());
			if(ExistingProjectMain != null && ExistingProjectMain.getIsActive()==1) {
				ExistingProjectMain.setModifiedBy(proType.getModifiedBy());
				ExistingProjectMain.setModifiedDate(proType.getModifiedDate());
				ExistingProjectMain.setIsActive(0);
				
				count=1L;
			}
				return count;
		}catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}

	}
	@Override
	public Object[] ProjectMainEditData(String ProjectMainId) throws Exception {
		Query query = manager.createNativeQuery(PROJECTMAINEDITDATA);
		query.setParameter("promainid",Long.parseLong(ProjectMainId));
		Object[] ProjectMainEditData = (Object[]) query.getSingleResult();
		return ProjectMainEditData;
	}
	private static final String PROJECTMAINUPDATE="UPDATE project_main SET projecttypeid=:projecttypeid,"
			+ "projectcode=:projectcode,projectname=:projectname,projectdescription=:projectdescription, "
			+ "unitcode=:unitcode, sanctionno=:sanctionno, sanctiondate=:sanctiondate,"
			+ "sanctioncostre=:sanctioncostre, sanctioncostfe=:sanctioncostfe,totalsanctioncost=:totalsanctioncost,"
			+ "pdc=:pdc,projectdirector=:projectdirector,projsancauthority=:projsancauthority,boardreference=:boardreference,"
			+ "ismainwc=:ismainwc,workcenter=:workcenter,objective=:objective,deliverable=:deliverable,modifiedby=:modifiedby,"
			+ " modifieddate=:modifieddate, LabParticipating=:labparticipating, CategoryId=:categoryId, Scope=:scope ,"
			+ " application=:application , enduser=:enduser , ProjectShortName=:projectshortname, PlatformId=:PlatformId "
			+ "WHERE  projectmainid=:promainid  AND isactive='1' "; 
	@Override
	public Long ProjectMainEdit(ProjectMain proType) throws Exception {
		ProjectMain ExistingProjectMain= manager.find(ProjectMain.class, proType.getProjectMainId());
		if(ExistingProjectMain != null && ExistingProjectMain.getIsActive()==1) {
			ExistingProjectMain.setProjectTypeId(proType.getProjectTypeId());
			ExistingProjectMain.setProjectCode(proType.getProjectCode());
			ExistingProjectMain.setProjectName(proType.getProjectName());
			ExistingProjectMain.setProjectDescription(proType.getProjectDescription());
			ExistingProjectMain.setUnitCode(proType.getUnitCode());
			ExistingProjectMain.setSanctionNo(proType.getSanctionNo());
			ExistingProjectMain.setSanctionDate(proType.getSanctionDate());
			ExistingProjectMain.setSanctionCostRE(proType.getSanctionCostRE());
			ExistingProjectMain.setSanctionCostFE(proType.getSanctionCostFE());
			ExistingProjectMain.setTotalSanctionCost(proType.getTotalSanctionCost());
			ExistingProjectMain.setPDC(proType.getPDC());
			ExistingProjectMain.setProjectDirector(proType.getProjectDirector());
			ExistingProjectMain.setProjSancAuthority(proType.getProjSancAuthority());
			ExistingProjectMain.setBoardReference(proType.getBoardReference());
			ExistingProjectMain.setIsMainWC(proType.getIsMainWC());
			ExistingProjectMain.setWorkCenter(proType.getWorkCenter());
			ExistingProjectMain.setObjective(proType.getObjective());
			ExistingProjectMain.setDeliverable(proType.getDeliverable());
			ExistingProjectMain.setModifiedBy(proType.getModifiedBy());
			ExistingProjectMain.setModifiedDate(proType.getModifiedDate());
			ExistingProjectMain.setLabParticipating(proType.getLabParticipating());
			ExistingProjectMain.setCategoryId(proType.getCategoryId());
			ExistingProjectMain.setScope(proType.getScope());
			ExistingProjectMain.setApplication(proType.getApplication());
			ExistingProjectMain.setEndUser(proType.getEndUser());
			ExistingProjectMain.setProjectShortName(proType.getProjectShortName());
			ExistingProjectMain.setPlatformId(proType.getPlatformId());
			return 1L;
		}
		else {
			return 0L;
		}
		
	}


	@Override
	public List<Object[]> ProjectList() throws Exception {
		Query query = manager.createNativeQuery(PROJECTLIST1);
		List<Object[]> ProjectList = (List<Object[]>) query.getResultList();
		return ProjectList;
	}

	@Override
	public List<Object[]> ProjectTypeMainList() throws Exception {
		Query query = manager.createNativeQuery(PROJECTTYPEMAINLIST);
		List<Object[]> ProjectTypeList = (List<Object[]>) query.getResultList();
		return ProjectTypeList;
	}


	@Override
	public List<Object[]> ProjectTypeMainListNotAdded() throws Exception {
		Query query = manager.createNativeQuery(PROJECTTYPEMAINLISTNOTADDED);
		List<Object[]> ProjectMainList = (List<Object[]>) query.getResultList();
		return ProjectMainList;
	}

	@Override
	public List<Object[]> ProjectCategoryList() throws Exception {
		Query query = manager.createNativeQuery(PROJECTCATEGORY);
		List<Object[]> ProjectCategoryList = (List<Object[]>) query.getResultList();
		return ProjectCategoryList;
	}


	@Override
	public Long ProjectMasterAdd(ProjectMaster proType) throws Exception {
		manager.persist(proType);
		manager.flush();
		return proType.getProjectId();
	}

	@Override
	public Long ProjectEdit(ProjectMaster proType) throws Exception {

		ProjectMaster proj=pmrepo.findById(proType.getProjectId()).get();
		//	 proj.setIsMainWC(proType.getIsMainWC());
		//	 proj.setWorkCenter(proType.getWorkCenter());
		//   proj.setProjectCode(proType.getProjectCode());
		proj.setApplication(proType.getApplication());
		proj.setScope(proType.getScope());
		proj.setProjectShortName(proType.getProjectShortName());
		proj.setEndUser(proType.getEndUser());
		proj.setProjectName(proType.getProjectName());
		proj.setProjectType(proType.getProjectType());
		proj.setProjectDescription(proType.getProjectDescription());
		proj.setUnitCode(proType.getUnitCode());
		proj.setSanctionNo(proType.getSanctionNo());
		proj.setBoardReference(proType.getBoardReference());
		proj.setProjectMainId(proType.getProjectMainId());
		proj.setProjectDirector(proType.getProjectDirector());
		proj.setProjectCategory(proType.getProjectCategory());
		proj.setProjSancAuthority(proType.getProjSancAuthority());
		proj.setSanctionDate(proType.getSanctionDate());
		proj.setTotalSanctionCost(proType.getTotalSanctionCost());
		proj.setSanctionCostRE(proType.getSanctionCostRE());
		proj.setSanctionCostFE(proType.getSanctionCostFE());
		proj.setPDC(proType.getPDC());
		proj.setObjective(proType.getObjective());
		proj.setDeliverable(proType.getDeliverable());
		proj.setRevisionNo(proType.getRevisionNo());
		proj.setModifiedBy(proType.getModifiedBy());
		proj.setModifiedDate(proType.getModifiedDate());
		proj.setLabParticipating(proType.getLabParticipating());
		proj.setPlatformId(proType.getPlatformId()); 
		proj.setPlatform(proType.getPlatform()); 
		pmrepo.save(proj);

		return proj.getProjectId() ;
	}


	@Override
	public Long ProjectClose(ProjectMaster proType) throws Exception {
		ProjectMaster ExistingProjectMaster= manager.find(ProjectMaster.class, proType.getProjectId());
		if(ExistingProjectMaster!=null) {
			ExistingProjectMaster.setModifiedBy(proType.getModifiedBy());
			ExistingProjectMaster.setModifiedDate(proType.getModifiedDate());
			ExistingProjectMaster.setIsActive(0);
			return 1L;
		}
		else {
			return 0L;
		}

	}

	@Override
	public Object[] ProjectEditData1(String ProjectId) throws Exception {
		Query query = manager.createNativeQuery(PROJECTEDITDATA);
		query.setParameter("proid", Long.parseLong(ProjectId));
		Object[] ProjectEditData = (Object[]) query.getSingleResult();
		return ProjectEditData;
	}

	@Override
	public List<Object[]> getProjectList() throws Exception {
		Query query = manager
				.createNativeQuery(PROJECTITEMLIST11);
		List<Object[]> projectList = (List<Object[]>) query.getResultList();
		return projectList;
	}

	@Override
	public List<Object[]> ProjectAssignList(String EmpId) throws Exception {

		Query query=manager.createNativeQuery(PROJECTASSIGNLIST);
		query.setParameter("proid", Long.parseLong(EmpId));
		List<Object[]> ProjectAssignList=(List<Object[]>)query.getResultList();	
		return ProjectAssignList;
	}

	@Override
	public List<Object[]> UserList(String proId) throws Exception {

		Query query=manager.createNativeQuery(USERLIST);
		query.setParameter("projectid", Long.parseLong(proId));
		List<Object[]> OfficerList=(List<Object[]>)query.getResultList();
		return OfficerList;
	}

	@Override
	public Object[] ProjectData(String ProId) throws Exception {
		Query query=manager.createNativeQuery(PROJECTDATA);
		query.setParameter("proid", Long.parseLong(ProId));
		Object[] ProjectData=(Object[])query.getSingleResult();
		return ProjectData;
	}


	@Override
	public Long ProjectAssignAdd(ProjectAssign proAssign) throws Exception {
		manager.persist(proAssign);
		manager.flush();
		return proAssign.getProjectEmployeeId();
	}
	

	@Override
	public Long ProjectRevoke(ProjectAssign proAssign) throws Exception {
		ProjectAssign ExistingProjectAssign= manager.find(ProjectAssign.class, proAssign.getProjectEmployeeId());
		if(ExistingProjectAssign != null && ExistingProjectAssign.getIsActive()==1) {
			ExistingProjectAssign.setModifiedBy(proAssign.getModifiedBy());
			ExistingProjectAssign.setModifiedDate(proAssign.getModifiedDate());
			ExistingProjectAssign.setIsActive(0);
			return 1L;
		}
		else {
			return 0L;
		}
		
	}

	@Override
	public List<Object[]> ProjectApproveAdList(String EmpId) throws Exception {
		Query query=manager.createNativeQuery("CALL Project_Approval_Ad(:EmpId)");
		query.setParameter("EmpId", Long.parseLong(EmpId));
		List<Object[]> ProjectApproveAdList=(List<Object[]>)query.getResultList();	
		return ProjectApproveAdList;
	}

	@Override
	public Long AdId() throws Exception {

		Query query=manager.createNativeQuery(ADID);

		return (Long)query.getSingleResult();
	}

	@Override
	public List<Object[]> ApprovalStutusList(String AuthoId) throws Exception {
		Query query=manager.createNativeQuery(APPSTATUSLIST);
		query.setParameter("AuthoId", Long.parseLong(AuthoId));
		List<Object[]> StutusList=(List<Object[]>)query.getResultList();	
		return StutusList;
	}

	@Override
	public long ProjectDataSubmit(PfmsProjectData pfmsprojectdata) throws Exception {
		manager.persist(pfmsprojectdata);
		manager.flush();
		return pfmsprojectdata.getProjectDataId();
	}


	@Override
	public List<Object[]> ProjectStageDetailsList() throws Exception {
		Query query=manager.createNativeQuery(PROJECTSTAGEDETAILSLIST);
		List<Object[]> ProjectStageDetailsList=(List<Object[]>)query.getResultList();	
		return ProjectStageDetailsList;
	}


	@Override
	public Object[] ProjectDataDetails(String projectid) throws Exception {
		Query query=manager.createNativeQuery(PROJECTDATADETAILS);
		query.setParameter("projectid", Long.parseLong(projectid));
		Object[] ProjectStageDetails=null;
		try {
			ProjectStageDetails=(Object[])query.getSingleResult();
		}catch (Exception e) {
			logger.error(loggerdate +"Inside DAO ProjectDataDetails "+ e);
			return null;
		}

		return ProjectStageDetails;
	}



	@Override
	public long ProjectDataEditSubmit(PfmsProjectData pfmsprojectdata, String querystr) throws Exception {
		String PROJECTDATAEDITSUBMIT="UPDATE pfms_project_data SET "+querystr+" currentstageid=:currentstageid,RevisionNo=:revisionno,proclimit=:proclimit,ModifiedBy=:modifiedby,ModifiedDate=:modifieddate,LastPmrcDate=:pmrcdate,LastEBDate=:ebdate WHERE projectdataid=:projectdataid";
		Query query=manager.createNativeQuery(PROJECTDATAEDITSUBMIT);
		//query.setParameter("querystr", querystr);
		query.setParameter("currentstageid", pfmsprojectdata.getCurrentStageId());
		query.setParameter("projectdataid", pfmsprojectdata.getProjectDataId());
		query.setParameter("revisionno", pfmsprojectdata.getRevisionNo());
		query.setParameter("proclimit", pfmsprojectdata.getProcLimit());
		query.setParameter("pmrcdate", pfmsprojectdata.getLastPmrcDate());
		query.setParameter("ebdate", pfmsprojectdata.getLastEBDate());
		query.setParameter("modifiedby", pfmsprojectdata.getModifiedBy());
		query.setParameter("modifieddate", pfmsprojectdata.getModifiedDate());
		return query.executeUpdate();
	}


	@Override
	public Object[] ProjectDataSpecsFileData(String projectdataid) throws Exception
	{

		Query query=manager.createNativeQuery(PROJECTDATASPECSFILEDATA);
		query.setParameter("projectdataid", Long.parseLong(projectdataid));
		Object[] ProjectDataSpecsFileData=(Object[])query.getSingleResult();
		return ProjectDataSpecsFileData;

	}


	@Override
	public long ProjectRevDataSubmit(PfmsProjectDataRev pfmsprojectrevdata) throws Exception {
		manager.persist(pfmsprojectrevdata);
		manager.flush();
		return pfmsprojectrevdata.getProjectDataRevId();
	}


	@Override
	public List<Object[]> ProjectDataRevList(String projectid) throws Exception {
		Query query=manager.createNativeQuery(PROJECTDATAREVLIST);
		query.setParameter("projectid", Long.parseLong(projectid));
		List<Object[]> ProjectDataRevList=(List<Object[]>)query.getResultList();
		return ProjectDataRevList;
	}




	@Override
	public Object[] ProjectDataRevData(String projectdatarevid) throws Exception
	{
		Query query=manager.createNativeQuery(PROJECTDATAREVDATA);
		query.setParameter("projectdatarevid", Long.parseLong(projectdatarevid));
		Object[] ProjectDataRevData=null;
		try 
		{
			ProjectDataRevData=(Object[])query.getSingleResult();
		}catch (Exception e) {
			logger.error(loggerdate +"Inside DAO ProjectDataRevData "+ e);
			ProjectDataRevData=null;
		}
		return	 ProjectDataRevData; 
	}



	@Override
	public Object[] ProjectDataSpecsRevFileData(String projectdatarevid) throws Exception
	{

		Query query=manager.createNativeQuery(PROJECTDATASPECSREVFILEDATA);
		query.setParameter("projectdatarevid", Long.parseLong(projectdatarevid));
		Object[] ProjectDataSpecsRevFileData=(Object[])query.getSingleResult();
		return ProjectDataSpecsRevFileData;

	}

	@Override
	public List<Object[]> InitiatedProjectList() throws Exception {


		Query query=manager.createNativeQuery(INITIATEDPROJECT);
		List<Object[]> InitiatedProjectList=(List<Object[]>)query.getResultList();
		return InitiatedProjectList;
	}

	@Override
	public List<Object[]> InitiatedProjectDetails(String ProjectId) throws Exception {

		Query query=manager.createNativeQuery(INITIATEDPROJECTDETAILS);
		query.setParameter("initiationid", Long.parseLong(ProjectId));
		List<Object[]> InitiatedProjectDetails=(List<Object[]>)query.getResultList();
		return InitiatedProjectDetails;
	}

	@Override
	public List<Object[]> NodalLabList() throws Exception {

		Query query=manager.createNativeQuery(NODALLABLIST);
		List<Object[]> NodalLabList=(List<Object[]>)query.getResultList();
		return NodalLabList;
	}


	@Override
	public List<Object[]> SubProjectList(String InitiationId) throws Exception {

		Query query=manager.createNativeQuery(SUBPROJECTLIST);
		query.setParameter("initiationid", Long.parseLong(InitiationId));
		List<Object[]> SubProjectList=(List<Object[]>)query.getResultList();
		return SubProjectList;
	}

	@Override
	public String StatusDetails(String Status) throws Exception {
		Query query=manager.createNativeQuery(STATUSDETAILS);
		query.setParameter("status",Status);
		String StatusDetails=(String)query.getSingleResult();
		return StatusDetails;
	}


	@Override
	public Long EmpId(String InitiationId) throws Exception {
		try {
			Query query=manager.createNativeQuery(INTEMPID);
			query.setParameter("id", Long.parseLong(InitiationId));
			return (Long)query.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
		
	}

	@Override
	public List<Object[]> ProjectRiskDataList(String projectid,String LabCode) throws Exception 
	{			
		Query query=manager.createNativeQuery(PROJECTRISKDATALIST);
		query.setParameter("projectid", Long.parseLong(projectid));
		query.setParameter("LabCode", LabCode);
		List<Object[]>  ProjectRiskDataList=(List<Object[]>)query.getResultList();
		return ProjectRiskDataList;
	}

	@Override
	public Object[] ProjectRiskData(String actionassignid) throws Exception 
	{			
		Query query=manager.createNativeQuery(PROJECTRISKDATA);
		query.setParameter("actionassignid", Long.parseLong(actionassignid));
		Object[] ProjectRiskData=null;
		try {
			ProjectRiskData=(Object[])query.getSingleResult();
		}catch (Exception e) {
			logger.error(loggerdate +"Inside DAO ProjectRiskData "+ e);
			ProjectRiskData=null;
		}
		return ProjectRiskData;		
	}
	

	@Override
	public long CloseProjectRisk(PfmsRiskDto dto)throws Exception
	{
		String date=LocalDate.now().toString();
		ActionAssign ExistingActionAssign = manager.find(ActionAssign.class, dto.getActionMainId());
		PfmsRisk ExistingPfmsRisk = manager.find(PfmsRisk.class, dto.getRiskId());
		
		if(ExistingActionAssign != null && ExistingPfmsRisk != null) {
			ExistingActionAssign.setClosedDate(date);
			ExistingActionAssign.setActionStatus(dto.getStatus());
			ExistingActionAssign.setModifiedBy(dto.getModifiedBy());
			ExistingActionAssign.setModifiedDate(dto.getModifiedDate());
			
			ExistingPfmsRisk.setStatus(dto.getStatus());
			ExistingPfmsRisk.setStatusDate(dto.getStatusDate());
			ExistingPfmsRisk.setRemarks(dto.getRemarks());
			ExistingPfmsRisk.setModifiedBy(dto.getModifiedBy());
			ExistingPfmsRisk.setModifiedDate(dto.getModifiedDate());
			return 1L;
		}
		else {
			return 0L;
		}

	}

	@Override
	public long ProjectRiskDataSubmit(PfmsRisk model) throws Exception {
		manager.persist(model);
		manager.flush();
		return model.getRiskId();
	}


	@Override
	public Object[] ProjectRiskMatrixData(String actionmainid) throws Exception 
	{
		Query query=manager.createNativeQuery(PROJECTRISKMATRIXDATA);
		query.setParameter("actionmainid", Long.parseLong(actionmainid));
		Object[] ProjectRiskMatrixData=null;
		try {
			ProjectRiskMatrixData=(Object[])query.getSingleResult();
		}catch (Exception e) {
			logger.error(loggerdate +"Inside DAO ProjectRiskMatrixData "+ e);
			ProjectRiskMatrixData=null;
		}
		return ProjectRiskMatrixData;
	}

	@Override
	public int ProjectRiskDataEdit(PfmsRiskDto dto) throws Exception {
		
		PfmsRisk ExistingPfmsRisk = manager.find(PfmsRisk.class, dto.getRiskId());
		if(ExistingPfmsRisk != null) {
			ExistingPfmsRisk.setSeverity(Integer.parseInt(dto.getSeverity()));
			ExistingPfmsRisk.setProbability(Integer.parseInt(dto.getProbability()));
			ExistingPfmsRisk.setMitigationPlans(dto.getMitigationPlans());
			ExistingPfmsRisk.setRevisionNo(Long.parseLong(dto.getRevisionNo()));
			ExistingPfmsRisk.setModifiedBy(dto.getModifiedBy());
			ExistingPfmsRisk.setModifiedDate(dto.getModifiedDate());
			ExistingPfmsRisk.setRPN(Integer.parseInt(dto.getProbability()) * Integer.parseInt(dto.getSeverity()));
			ExistingPfmsRisk.setImpact(dto.getImpact());
			ExistingPfmsRisk.setCategory(dto.getCategory());
			ExistingPfmsRisk.setRiskTypeId(Integer.parseInt(dto.getRiskTypeId()));	
			return 1;
		}
		else {
			return 0;
		}
		
	}	

	@Override
	public long ProjectRiskDataRevSubmit(PfmsRiskRev model) throws Exception{
		manager.persist(model);
		manager.flush();
		return model.getRiskRevisionId();
	}


	@Override
	public List<Object[]> ProjectRiskMatrixRevList(String actionmainid) throws Exception {
		Query query=manager.createNativeQuery(PROJECTRISKMATRIXREVLIST);
		query.setParameter("actionmainid", Long.parseLong(actionmainid));
		List<Object[]>  ProjectRiskMatrixRevList=(List<Object[]>)query.getResultList();
		return ProjectRiskMatrixRevList;
	}	




	@Override
	public List<Object> RiskDataPresentList(String projectid,String LabCode) throws Exception {
		Query query=manager.createNativeQuery(RISKDATAPRESENTLIST);
		query.setParameter("projectid", Long.parseLong(projectid));
		//			query.setParameter("LabCode", LabCode);
		List<Object>  RiskDataPresentList=(List<Object>)query.getResultList();
		return RiskDataPresentList;
	}	


	@Override
	public Long ProjectInitiationAuthorityAdd(PfmsInitiationAuthority pfmsauthority,PfmsInitiationAuthorityFile pfmsinitiationauthorityfile) throws Exception {

		manager.persist(pfmsauthority);
		pfmsinitiationauthorityfile.setAuthorityId(pfmsauthority.getAuthorityId());
		manager.persist(pfmsinitiationauthorityfile);
		manager.flush();
		return pfmsinitiationauthorityfile.getInitiationAuthorityFileId();

	}

	@Override
	public PfmsInitiationAuthorityFile ProjectAuthorityDownload(String AuthorityFileId) throws Exception {


		PfmsInitiationAuthorityFile ProjectAuthorityDownload= manager.find(PfmsInitiationAuthorityFile.class,Long.parseLong(AuthorityFileId));

		return ProjectAuthorityDownload;
	}

	@Override
	public Long ProjectAuthorityUpdate(PfmsInitiationAuthority pfmsauthority) throws Exception {

		Query query=manager.createNativeQuery(AUTHORITYUPDATE);
		query.setParameter("authorityname",pfmsauthority.getAuthorityName() );
		query.setParameter("letterdate", pfmsauthority.getLetterDate());
		query.setParameter("letterno", pfmsauthority.getLetterNo());
		query.setParameter("modifiedby",pfmsauthority.getModifiedBy());
		query.setParameter("modifieddate", pfmsauthority.getModifiedDate());
		query.setParameter("initiationid", pfmsauthority.getInitiationId());
		int count= query.executeUpdate();

		return Long.valueOf(count);

	}
	private static final String AUTHORITYFILEUPDATE="UPDATE pfms_initiation_authority_file"
			+ " SET attachmentname=:attachmentname,file=:file WHERE initiationauthorityfileid=:initiationauthorityfileid ";

	@Override
	public Long AuthorityFileUpdate(PfmsInitiationAuthorityFile pfmsinitiationauthorityfile) throws Exception {
		PfmsInitiationAuthorityFile ExistingPfmsInitiationAuthorityFile = manager.find(PfmsInitiationAuthorityFile.class, pfmsinitiationauthorityfile.getInitiationAuthorityFileId());
		if(ExistingPfmsInitiationAuthorityFile !=null) {
			ExistingPfmsInitiationAuthorityFile.setAttachmentName(pfmsinitiationauthorityfile.getAttachmentName());
			ExistingPfmsInitiationAuthorityFile.setFile(pfmsinitiationauthorityfile.getFile());
			return 1L;
		}
		else {
			return 0L;
		}
		
	}


	@Override
	public List<Object[]> getProjectCatSecDetalis(Long projectId)throws Exception{
		Query query=manager.createNativeQuery(PROCATSECDETAILS);
		query.setParameter("projectmainid", projectId);
		return (List<Object[]>)query.getResultList();
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


	private static final String DORTMDADEMPDATA="SELECT pr.empid ,CONCAT(IFNULL(CONCAT(e.title,' '),''), e.emp_name) AS 'empname' ,ed.designation ,pr.type  FROM pfms_initiation_approver pr, employee e ,employee_desig ed WHERE pr.empid=e.emp_id AND e.desig_id=ed.desig_id AND pr.isactive='1' AND pr.LabCode=:Labcode ORDER BY FIELD (pr.type,'DO-RTMD','AD')";
	@Override
	public List<Object[]>  DoRtmdAdEmpData(String Labcode) throws Exception
	{
		Query query=manager.createNativeQuery(DORTMDADEMPDATA);
		query.setParameter("Labcode", Labcode)	;	
		return (List<Object[]>)query.getResultList();
	}


	@Override
	public Object[]  DirectorEmpData(String LabCode) throws Exception
	{
		Query query=manager.createNativeQuery(DIRECTOREMPDATA);
		query.setParameter("labcode", LabCode);	
		try {
			return (Object[])query.getSingleResult();
		}catch (NoResultException e) {
			return null;
		}
	}


	@Override
	public Object[]  EmpDivHeadData(String empid) throws Exception
	{
		Query query=manager.createNativeQuery(EMPDIVHEADDATA);
		query.setParameter("empid", Long.parseLong(empid));
		try {
			return (Object[])query.getSingleResult();
		}catch (NoResultException e) {
			logger.error(loggerdate +"Inside DAO EmpDivHeadData "+ e);
			return null;
		}

	}


	@Override
	public ProjectMain  ProjectMainObj(String projectmainid) throws Exception
	{
		ProjectMain model= manager.find(ProjectMain.class,Long.parseLong(projectmainid));
		return model;
	}


	@Override
	public List<Object[]>  InitCommDefault() throws Exception
	{
		Query query=manager.createNativeQuery(INITCOMMDEFAULT);
		return (List<Object[]> )query.getResultList();
	}

	@Override
	public long InitiationCommitteeAdd(CommitteeInitiation model) throws Exception
	{		
		manager.persist(model);
		manager.flush();
		return  model.getCommitteeInitiationId();
	}

	@Override
	public ProjectMaster ProjectMasterData(long projectid) throws Exception
	{		
		return manager.find(ProjectMaster.class, projectid);

	}


	@Override
	public ProjectMasterRev ProjectREVSubmit(ProjectMasterRev masterrev) throws Exception {
		manager.persist(masterrev);
		manager.flush();
		return masterrev;
	}


	@Override
	public List<Object[]> ProjectRevList(String projectid) throws Exception {
		Query query=manager.createNativeQuery(PROJECTREVLIST);
		query.setParameter("projectid",  Long.parseLong(projectid));
		return (List<Object[]>)query.getResultList();
	}

	@Override
	public long ProjectMasterAttachAdd(ProjectMasterAttach modal) throws Exception {
		manager.persist(modal);
		manager.flush();
		return modal.getProjectAttachId();
	}


	@Override
	public List<Object[]> ProjectMasterAttachList(String projectid) throws Exception {
		Query query=manager.createNativeQuery(PROJECTMASTERATTACHLIST);
		query.setParameter("projectid", Long.parseLong(projectid));
		return (List<Object[]>)query.getResultList();
	}


	@Override
	public Object[] ProjectMasterAttachData(String projectattachid) throws Exception {
		Query query=manager.createNativeQuery(PROJECTMASTERATTACHDATA);
		query.setParameter("projectattachid", Long.parseLong(projectattachid));
		return (Object[])query.getResultList().get(0);
	}




	@Override
	public int ProjectMasterAttachDelete(String projectattachid) throws Exception
	{
		Query query=manager.createNativeQuery(PROJECTMASTERATTACHDELETE);
		query.setParameter("projectattachid", Long.parseLong(projectattachid));
		return query.executeUpdate();
	}

	@Override
	public long TechnicalWorkDataAdd(ProjectTechnicalWorkData modal) throws Exception {
		modal = manager.merge(modal);
		manager.flush();
		return modal.getTechDataId();
	}

	@Override
	public long TechnicalWorkDataEdit(ProjectTechnicalWorkData modal,long Techdataid) throws Exception {
		ProjectTechnicalWorkData detached = manager.find(ProjectTechnicalWorkData.class, Techdataid);
		detached.setModifiedBy(modal.getCreatedBy());
		detached.setModifiedDate(modal.getCreatedDate());			
		detached.setIsActive(0);
		return detached.getTechDataId();
	}

	private static final String INITIATIONCHECKLIST ="SELECT cl.checklistid, cl.checklistno, cl.checklistSNo,  cl.checklistsubsno, cl.checklistitem ,(SELECT ischecked FROM pfms_initiation_checklist_data cd WHERE cl.checklistid = cd.checklistid AND cd.initiationid=:initiationid ) AS 'ischecked' FROM pfms_initiation_checklist_list cl  WHERE cl.isactive=1  ";

	@Override
	public List<Object[]> InitiationCheckList(String initiationid ) throws Exception 
	{
		Query query=manager.createNativeQuery(INITIATIONCHECKLIST);
		query.setParameter("initiationid", Long.parseLong(initiationid));
		return (List<Object[]>)query.getResultList();
	}


	@Override
	public long InitiationChecklistAdd( PfmsInitiationChecklistData cldata ) throws Exception 
	{
		manager.persist(cldata);
		manager.flush();
		return cldata.getChecklistDataId();
	}
	@Override
	public long sanctionDataAdd(PfmsInitiationSanctionData psd) throws Exception {

		manager.persist(psd);
		manager.flush();
		Integer x=psd.getIsChecked();
		if(x==0) {
			return psd.getStatementId();
		}
		return psd.getIsChecked();
	}

	@Override
	public PfmsInitiationChecklistData InitiationChecklistCheck( PfmsInitiationChecklistData cldata ) throws Exception
	{
		try {
			CriteriaBuilder cb =manager.getCriteriaBuilder();
			CriteriaQuery <PfmsInitiationChecklistData> cq = cb.createQuery(PfmsInitiationChecklistData.class);
			Root<PfmsInitiationChecklistData> rootentry = cq.from(PfmsInitiationChecklistData.class);
			CriteriaQuery<PfmsInitiationChecklistData> all = cq.select(rootentry).where(cb.and(cb.equal(rootentry.get("InitiationId"), cldata.getInitiationId() ),cb.equal(rootentry.get("ChecklistId"), cldata.getChecklistId() )));
			TypedQuery<PfmsInitiationChecklistData> query  = manager.createQuery(all);
			return query.getSingleResult();
		}catch (NoResultException e) {
			logger.error(loggerdate +"Inside DAO InitiationChecklistCheck "+ e);
			return null;
		}			
	}
	@Override
	public PfmsInitiationSanctionData sanctionDataUpdate(PfmsInitiationSanctionData psd) throws Exception {
		try {
			CriteriaBuilder cb =manager.getCriteriaBuilder();
			CriteriaQuery <PfmsInitiationSanctionData> cq = cb.createQuery(PfmsInitiationSanctionData.class);
			Root<PfmsInitiationSanctionData> rootentry = cq.from(PfmsInitiationSanctionData.class);
			CriteriaQuery<PfmsInitiationSanctionData> all = cq.select(rootentry).where(cb.and(cb.equal(rootentry.get("InitiationId"), psd.getInitiationId() ),cb.equal(rootentry.get("StatementId"), psd.getStatementId() )));
			TypedQuery<PfmsInitiationSanctionData> query  = manager.createQuery(all);
			return query.getSingleResult();
		}
		catch(NoResultException e) {
			logger.error(loggerdate +"Inside DAO sanctionDataUpdate "+ e);
			return null;
		}


	}


	@Override
	public long InitiationChecklistUpdate( PfmsInitiationChecklistData cldata ) throws Exception
	{
		manager.merge(cldata);
		manager.flush();
		return cldata.getChecklistDataId();
	}
	@Override
	public long sanctionDataUpdated(PfmsInitiationSanctionData pd) throws Exception {
		manager.merge(pd);
		manager.flush();			
		return pd.getIsChecked();
	}


	private static final String RISKTYPELIST = "SELECT risktypeid, risktype FROM pfms_risk_type";

	@Override
	public List<Object[]> RiskTypeList() throws Exception 
	{
		Query query=manager.createNativeQuery(RISKTYPELIST);
		return (List<Object[]>)query.getResultList();
	}		

	private static final String MILESTONEDATA ="FROM PfmsInitiationSchedule WHERE InitiationId=:InitiationId AND MilestoneNo=:MilestoneNo AND IsActive=1";
	@Override
	public PfmsInitiationSchedule MilestoneData(long InitiationId, int milestoneno) throws Exception {

		Query query=manager.createQuery(MILESTONEDATA);
		query.setParameter("InitiationId", InitiationId);	
		query.setParameter("MilestoneNo", milestoneno);	
		try {
			return (PfmsInitiationSchedule)query.getSingleResult();
		}catch (Exception e) {
			return null;
		}
	}
	private static final String REQQUIREMENTTYPELIST="SELECT a.reqtypeid,a.reqtypecode,a.reqtype,a.frnr FROM pfms_initiation_req_type a";
	@Override
	public List<Object[]> RequirementTypeList() throws Exception {
		Query query=manager.createNativeQuery(REQQUIREMENTTYPELIST);
		return (List<Object[]>)query.getResultList();
	}

	@Override
	public long ProjectRequirementAdd(PfmsInititationRequirement pir) throws Exception {
		manager.persist(pir);
		manager.flush();
		return pir.getInitiationReqId();
	}
	//Line Added	
	private static final String REQLIST="SELECT a.InitiationReqId, a.requirementid,a.reqtypeid,a.requirementbrief,a.requirementdesc,a.priority,a.needtype,a.remarks,a.category,a.constraints,a.linkedrequirements,a.linkedDocuments,a.linkedPara FROM pfms_initiation_req a WHERE ReqInitiationId=:ReqInitiationId AND isActive='1' ORDER BY reqCount";
	@Override
	public List<Object[]> RequirementList(String reqInitiationId) throws Exception {
		Query query=manager.createNativeQuery(REQLIST);
		query.setParameter("ReqInitiationId", Long.parseLong(reqInitiationId));
		List<Object[]> RequirementList=(List<Object[]> )query.getResultList();	
		return RequirementList;
	}



	private static final String REQDLT="";
	@Override
	public long ProjectRequirementDelete(long initiationReqId) throws Exception {
		return 0;
	}
	private static final String REQUIREMENTS="SELECT '0' as InitiationId,a.reqtypeid,a.RequirementBrief,a.RequirementDesc,a.RequirementId,a.priority,a.linkedrequirements,a.InitiationReqId,a.needtype,a.remarks,a.linkeddocuments,a.category,a.constraints,a.LinkedPara,a.Demonstration,a.Test,a.Analysis,a.Inspection,a.SpecialMethods,a.criticality,a.TestStage,a.LinkedSubSystem,a.Derivedtype  FROM pfms_initiation_req a WHERE InitiationReqId=:InitiationReqId AND isActive='1'";
	@Override
	public Object[] Requirement(long InitiationReqId) throws Exception {
		Query query=manager.createNativeQuery(REQUIREMENTS);
		query.setParameter("InitiationReqId", InitiationReqId);
		Object[] Requirement=(Object[])query.getSingleResult();	

		return Requirement;
	}
//	private static final String REQUPDATE="UPDATE pfms_initiation_req SET initiationid=:initiationid, requirementid=:requirementid, reqtypeid=:reqtypeid, requirementbrief=:requirementbrief ,RequirementDesc=:RequirementDesc,modifiedby=:modifiedby,modifieddate=:modifieddate,priority=:priority,linkedrequirements=:linkedrequirements,reqCount=:reqCount,NeedType=:needtype,remarks=:remarks,linkeddocuments=:linkeddocuments,constraints=:constraints,category=:category,linkedPara=:linkedPara WHERE initiationreqid=:initiationreqid AND isActive=1";
//	@Override
//	public long RequirementUpdate(PfmsInititationRequirement pir, String initiationReqId) throws Exception {
//		Query query=manager.createNativeQuery(REQUPDATE);
//
//		query.setParameter("initiationid", pir.getInitiationId());
//		query.setParameter("requirementid", pir.getRequirementId());
//		query.setParameter("reqtypeid", pir.getReqTypeId());
//		query.setParameter("requirementbrief", pir.getRequirementBrief());
//		query.setParameter("RequirementDesc", pir.getRequirementDesc());
//		query.setParameter("modifiedby", pir.getModifiedBy());
//		query.setParameter("modifieddate", pir.getModifiedDate());
//		query.setParameter("priority", pir.getPriority());
//		query.setParameter("linkedrequirements", pir.getLinkedRequirements());
//		query.setParameter("reqCount", pir.getReqCount());
//		query.setParameter("needtype", pir.getNeedType());
//		query.setParameter("remarks", pir.getRemarks());
//		query.setParameter("linkeddocuments",pir.getLinkedDocuments() );
//		query.setParameter("initiationreqid", initiationReqId);
//		query.setParameter("category", pir.getCategory());
//		query.setParameter("constraints", pir.getConstraints());
//		query.setParameter("linkedPara", pir.getLinkedPara());
//		query.executeUpdate();
//		return 1l;
//	}
	private static final String TDNUPDATE="UPDATE pfms_initiation_statement_data SET TDN=:TDN,ModifiedBy=:ModifiedBy, modifiedDate=:modifiedDate WHERE initiationid=:initiationid and statementid=:statementid";
	@Override
	public long projectTDNUpdate(PfmsInitiationSanctionData psd) throws Exception {


		Query query=manager.createNativeQuery(TDNUPDATE);
		query.setParameter("TDN", psd.getTDN());
		query.setParameter("ModifiedBy", psd.getModifiedBy()); 
		query.setParameter("modifiedDate", psd.getModifiedDate());
		query.setParameter("initiationid", psd.getInitiationId());
		query.setParameter("statementid", psd.getStatementId());

		query.executeUpdate();
		return 1l;
	}
	private static final String PGNAJUPDATE="UPDATE pfms_initiation_statement_data SET PGNAJ=:PGNAJ,ModifiedBy=:ModifiedBy, modifiedDate=:modifiedDate WHERE initiationid=:initiationid and statementid=:statementid";
	@Override
	public long ProjectPGNAJUpdate(PfmsInitiationSanctionData psd) throws Exception {

		Query query=manager.createNativeQuery(PGNAJUPDATE);
		query.setParameter("PGNAJ", psd.getPGNAJ());
		query.setParameter("ModifiedBy", psd.getModifiedBy()); 
		query.setParameter("modifiedDate", psd.getModifiedDate());
		query.setParameter("initiationid", psd.getInitiationId());
		query.setParameter("statementid", psd.getStatementId());

		query.executeUpdate();
		return 1l;
	}


	@Override
	public long projectInitiationUserUpdate(PfmsInitiation pf) throws Exception {
		
		PfmsInitiation ExistingPfmsInitiation= manager.find(PfmsInitiation.class, pf.getInitiationId());
		if(ExistingPfmsInitiation!=null) {
			ExistingPfmsInitiation.setUser(pf.getUser());
			ExistingPfmsInitiation.setModifiedBy(pf.getModifiedBy());
			ExistingPfmsInitiation.setModifiedDate(pf.getModifiedDate());
			return 1L;
		}
		else {
			return 0L;
		}
	
	}
	private static final String REQTYPECOUNT="SELECT IFNULL(MAX(ReqCount),0) AS 'MAX' FROM pfms_initiation_req WHERE initiationid=:intiationId AND projectid=:projectid AND isactive='1'";
	@Override
	public long numberOfReqTypeId(String intiationId,String projectId) throws Exception {

		Query query=manager.createNativeQuery(REQTYPECOUNT);
		query.setParameter("intiationId", intiationId);
		query.setParameter("projectid", projectId);
		return (Long)query.getSingleResult();
	}
	private final static String BUDGETHEADLIST="SELECT DISTINCT(a.budgetheadid), a. budgetheaddescription FROM budget_head a , budget_item_sanc b WHERE isproject='Y' AND b.projecttypeid=:projecttypeid AND a.budgetheadid =b.budgetheadid  ORDER BY budgetheaddescription ASC ";
	@Override
	public List<Object[]> BudgetHeadList(Long projecttypeid) throws Exception {
		Query query=manager.createNativeQuery(BUDGETHEADLIST);
		query.setParameter("projecttypeid", projecttypeid);

		List<Object[]> BudgetHeadList=(List<Object[]> )query.getResultList();	
		return BudgetHeadList;
	}
	private final static String REQCOUNTLIST="SELECT reqcount FROM pfms_initiation_req WHERE initiationid=:initiationId AND isactive=1";
	@Override
	public List<Integer> reqcountList(String initiationId) throws Exception {
		Query query=manager.createNativeQuery(REQCOUNTLIST);
		query.setParameter("initiationId", Long.parseLong(initiationId));
		List<Integer>reqcountList=(List<Integer>)query.getResultList();

		return reqcountList;
	}
	private  final static String REQDELT="DELETE FROM pfms_initiation_req WHERE initiationreqid=:initiationReqId";
	@Override
	public int deleteRequirement(String initiationReqId) throws Exception {
		Query query =manager.createNativeQuery(REQDELT);
		query.setParameter("initiationReqId", Long.parseLong(initiationReqId));

		return query.executeUpdate();
	}
	private final static String REQIREID="SELECT requirementid FROM pfms_initiation_req WHERE reqcount=:i AND initiationid=:initiationId AND isactive=1;";
	@Override

	public String getReqId(int i, String initiationId) throws Exception {
		Query query=manager.createNativeQuery(REQIREID);
		query.setParameter("i", i);
		query.setParameter("initiationId", Long.parseLong(initiationId));
		String getReqId=(String)query.getSingleResult();
		return getReqId;
	}
	private final static String REQIDUPDATE="UPDATE pfms_initiation_req SET requirementid=:s,reqcount=:last WHERE reqcount=:first AND initiationid=:initiationId AND isactive='1'";
	@Override
	public int updateReqId(int last, String s, int first, String initiationId) throws Exception {
		Query query =manager.createNativeQuery(REQIDUPDATE);
		query.setParameter("s", s);
		query.setParameter("last", last);
		query.setParameter("first", first);
		query.setParameter("initiationId", Long.parseLong(initiationId));

		return query.executeUpdate();
	}

	private final String reqtype="SELECT a.reqtypeid,a.reqtypecode,a.reqtype,a.frnr FROM pfms_initiation_req_type a WHERE reqtypeid=:r";
	@Override
	public Object[] reqType(String r) throws Exception {
		Query query =manager.createNativeQuery(reqtype);
		query.setParameter("r", r);
		Object[]reqType=(Object[])query.getSingleResult();
		return reqType;
	}
	/*
	 * @Override public long RequirementAttachmentAdd(PfmsRequirementAttachment pra)
	 * throws Exception { // TODO Auto-generated method stub manager.persist(pra);
	 * manager.flush(); return pra.getAttachmentId(); }
	 */

	private final String REQATTACHLIST="SELECT attachmentid,AttachmentsName FROM pfms_initiation_req_attach WHERE InitiationReqId=:inititationReqId";
	@Override
	public List<Object[]> RequirementAttachmentList(String inititationReqId) throws Exception {
		Query query=manager.createNativeQuery(REQATTACHLIST);
		query.setParameter("inititationReqId", Long.parseLong(inititationReqId));
		List<Object[]>requirementAttachmentList=(List<Object[]>)query.getResultList();
		return requirementAttachmentList;
	}


	private static final String REQATTACHDOWNLOAD="SELECT docId,stepid,FilePath,filename FROM pfms_initiation_file_upload WHERE documentid=:DocumentId AND versiondoc=:VersionDoc AND initiationid=:initiationid AND stepid=:stepid ";
	@Override
	public Object[] reqAttachDownload(String DocumentId,String VersionDoc,String initiationid, String stepid) throws Exception {
		logger.info(new java.util.Date() +"Inside DAO reqAttachDownload ");
		Query query =manager.createNativeQuery(REQATTACHDOWNLOAD);

		query.setParameter("DocumentId", Long.parseLong(DocumentId));
		query.setParameter("VersionDoc", VersionDoc);
		query.setParameter("initiationid", Long.parseLong(initiationid));
		query.setParameter("stepid", Long.parseLong(stepid));
		Object[]reqAttachDownload=(Object[])query.getSingleResult();
		return reqAttachDownload;
	}

	private static final String REQATTACHDEL="DELETE FROM pfms_initiation_req_attach WHERE attachmentid=:attachmentid";
	@Override
	public long requirementAttachmentDelete(String attachmentid) throws Exception {
		Query query =manager.createNativeQuery(REQATTACHDEL);
		query.setParameter("attachmentid", Long.parseLong(attachmentid));

		return query.executeUpdate();
	}
	private static final String INITIATIONSTEPS="SELECT stepid,stepname FROM pfms_initiation_steps";
	@Override
	public Object initiationSteps() throws Exception {
		Query query=manager.createNativeQuery(INITIATIONSTEPS);
		List<Object[]>InitiationSteps=(List<Object[]>)query.getResultList();
		return InitiationSteps;
	}

	@Override
	public long preProjectFileUpload(PreprojectFile pf) throws Exception {
		manager.persist(pf);
		manager.flush();


		return pf.getDocId();
	}
	private static final String PROJECTFILES="SELECT DocId,initiationid,stepid,fileName,DocumentName,FilePath,IFNULL(MAX(VersionDoc),0) AS 'MAXversiondoc',Description,DocumentId FROM pfms_initiation_file_upload WHERE initiationid=:initiationid AND stepid=:stepid GROUP BY DocumentId,DocId ";
	@Override
	public List<Object[]> getProjectFilese(String initiationid, String stepid) throws Exception {
		Query query=manager.createNativeQuery(PROJECTFILES);
		query.setParameter("initiationid", Long.parseLong(initiationid));
		query.setParameter("stepid", Long.parseLong(stepid));
		List<Object[]>projectfiles=(List<Object[]>)query.getResultList();
		return projectfiles;
	}


	private static final String FILECOUNT="SELECT IFNULL(MAX(DocumentId),0) AS 'MAX' FROM pfms_initiation_file_upload WHERE initiationid=:initiationid AND stepid=:stepid ";
	@Override
	public long filecount(String stepid, String initiationid) throws Exception {
		try {
			Query query =manager.createNativeQuery(FILECOUNT);
			query.setParameter("initiationid", Long.parseLong(initiationid));
			query.setParameter("stepid", Long.parseLong(stepid));
			return (Long)query.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
		
	}


	private static final String FILELIST="SELECT filename,filepath,versiondoc,createddate,docid,documentid,initiationid,stepid FROM pfms_initiation_file_upload WHERE initiationid=:inititationid AND stepid=:stepid AND documentid=:documentcount";
	@Override
	public List<Object[]> projectfilesList(String inititationid, String stepid, String documentcount)
			throws Exception {

		Query query=manager.createNativeQuery(FILELIST);
		query.setParameter("inititationid", Long.parseLong(inititationid));
		query.setParameter("stepid", Long.parseLong(stepid));
		query.setParameter("documentcount", Long.parseLong(documentcount));

		List<Object[]>projectfiles=(List<Object[]>)query.getResultList();
		return projectfiles;



	}
	private static final String PROJECTREQFILES="SELECT DocId,initiationid,stepid,fileName,DocumentName,FilePath,VersionDoc,Description,DocumentId,createddate FROM pfms_initiation_file_upload WHERE initiationid=:initiationid AND stepid=:stepid  ";
	@Override
	public List<Object[]> requirementFiles(String initiationid, int stepid) throws Exception {
		Query query=manager.createNativeQuery(PROJECTREQFILES);

		query.setParameter("initiationid", Long.parseLong(initiationid));
		query.setParameter("stepid",  stepid);
		List<Object[]>requirementFiles=(List<Object[]>)query.getResultList();
		return requirementFiles;
	}
	private static final String SANCDETAILS="SELECT sl.statementId,sl.Statement,(SELECT ischecked FROM pfms_initiation_statement_data sd WHERE sl.statementid=sd.statementid AND sd.initiationid=:initiationid)AS 'ischecked',(SELECT TDN FROM pfms_initiation_statement_data sd WHERE sl.statementid=sd.statementid AND sd.initiationid=:initiationid)AS TDN,(SELECT PGNAJ FROM pfms_initiation_statement_data sd WHERE sl.statementid=sd.statementid AND sd.initiationid=:initiationid ) AS 'PGNAJ'  FROM pfms_initiation_statement_list sl WHERE sl.isactive=1";
	@Override
	public List<Object[]> sanctionlistDetails(String initiationid) throws Exception {
		Query query =manager.createNativeQuery(SANCDETAILS);
		query.setParameter("initiationid", Long.parseLong(initiationid));
		List<Object[]>sanctionlistDetails=(List<Object[]>)query.getResultList();
		return sanctionlistDetails;
	}

	@Override
	public long addProjectPGNAJ(PfmsInitiationSanctionData psd) throws Exception {

		manager.persist(psd);
		manager.flush();
		return psd.getStatementDataId();
	}

	private  static final String FILEDETAILS=" SELECT FileName,CreatedDate,VersionDoc,docid,initiationid,stepid,DocumentId FROM pfms_initiation_file_upload WHERE initiationid=:inititationid AND stepid=:stepid ORDER BY docid DESC" ;
	@Override
	public List<Object[]> projectfiles(String inititationid, String stepid) throws Exception {
		Query query=manager.createNativeQuery(FILEDETAILS);
		query.setParameter("inititationid", Long.parseLong(inititationid));
		query.setParameter("stepid", Long.parseLong(stepid));

		List<Object[]>projectfiles=(List<Object[]>)query.getResultList();
		return projectfiles;
	}
	private static final String PROFILES="SELECT DocId,initiationid,stepid,fileName,DocumentName,FilePath,IFNULL(MAX(VersionDoc), 0) AS versiondoc,Description,DocumentId FROM pfms_initiation_file_upload WHERE initiationid=:initiationid AND stepid=:stepid AND Documentid=:documentid GROUP BY DocumentId ";
	@Override
	public Object[] projectfile(String initiationid, String stepid, String documentid) throws Exception {
		// TODO Auto-generated method stub
		Query query =manager.createNativeQuery(PROFILES);
		query.setParameter("initiationid", Long.parseLong(initiationid));
		query.setParameter("stepid",  Long.parseLong(stepid));
		query.setParameter("documentid", Long.parseLong(documentid));

		Object[]projectfile=null;
		try {
			projectfile=(Object[])query.getSingleResult();
		}catch(Exception e) {

		}
		return projectfile;
	}
	private static final String DEMANDLIST="SELECT demand_mode_id,demand_mode_name,mode_description FROM demand_mode";
	@Override
	public List<Object[]> DemandList() throws Exception {
		// TODO Auto-generated method stub

		Query query=manager.createNativeQuery(DEMANDLIST);
		List<Object[]>DemandList=(List<Object[]>)query.getResultList();
		return DemandList;
	}

	@Override
	public long PfmsProcurementPlanSubmit(PfmsProcurementPlan pp) throws Exception {
		// TODO Auto-generated method stubmanager.

		manager.persist(pp);

		return pp.getPlanId();
	}
	private static final String PROCUREMENTLIST="SELECT a.PlanId,a.InitiationID,a.Item,a.Purpose,a.Source,a.ModeName,a.Cost,a.Demand,a.Tender,a.OrderTime,a.payment,a.Approved,a.total FROM pfms_procurement_plan a WHERE initiationid=:initiationid";
	@Override
	public List<Object[]> ProcurementList(String initiationid) throws Exception {
		// TODO Auto-generated method stub

		Query query =manager.createNativeQuery(PROCUREMENTLIST);
		query.setParameter("initiationid", Long.parseLong(initiationid));
		List<Object[]>ProcurementList=null;
		try {
			ProcurementList=(List<Object[]>)query.getResultList();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return ProcurementList;
	}
	private static final String PROCUREMENTDETAILS="SELECT Item,Purpose,Source,ModeName,Cost,Demand,Tender,OrderTime,Payment,Approved FROM pfms_procurement_plan WHERE planid=:planid";
	@Override
	public Object[] PocurementPlanEditDetails(String planid) throws Exception {
		// TODO Auto-generated method stub
		Query query =manager.createNativeQuery(PROCUREMENTDETAILS);
		query.setParameter("planid",  Long.parseLong(planid));
		Object[]PocurementPlanEditDetails=(Object[])query.getSingleResult();
		return PocurementPlanEditDetails;
	}


	
	@Override
	public long ProjectProcurementEdit(PfmsProcurementPlan pp) throws Exception {
		
		PfmsProcurementPlan ExistingPfmsProcurementPlan= manager.find(PfmsProcurementPlan.class, pp.getPlanId());
		if(ExistingPfmsProcurementPlan != null) {
			ExistingPfmsProcurementPlan.setItem(pp.getItem());
			ExistingPfmsProcurementPlan.setPurpose(pp.getPurpose());
			ExistingPfmsProcurementPlan.setSource(pp.getSource());
			ExistingPfmsProcurementPlan.setModeName(pp.getModeName());
			ExistingPfmsProcurementPlan.setCost(pp.getCost());
			ExistingPfmsProcurementPlan.setDemand(pp.getDemand());
			ExistingPfmsProcurementPlan.setTender(pp.getTender());
			ExistingPfmsProcurementPlan.setOrderTime(pp.getOrderTime());
			ExistingPfmsProcurementPlan.setPayment(pp.getPayment());
			ExistingPfmsProcurementPlan.setTotal(pp.getTotal());
			ExistingPfmsProcurementPlan.setApproved(pp.getApproved());
			ExistingPfmsProcurementPlan.setModifiedBy(pp.getModifiedBy());
			ExistingPfmsProcurementPlan.setModifiedDate(pp.getModifiedDate());
			return 1L;
			}
		else {
			return 0L;
		}
		
	}
	private static final String TOTALCOST="SELECT SUM(cost) FROM pfms_procurement_plan WHERE payment BETWEEN :start AND :end AND InitiationId=:initiationid";
	@Override
	public String TotalPayOutMonth(String start, String end, String initiationid) throws Exception {
		// TODO Auto-generated method stub
		Query query=manager.createNativeQuery(TOTALCOST);
		query.setParameter("start", start);
		query.setParameter("end", end);
		query.setParameter("initiationid", Long.parseLong(initiationid));
		Object temp=query.getResultList().get(0);
		String total="0.00";
		if(temp!=null) {
			total=(temp.toString());
		}
		return total;
	}
	private static final String MACRODETAIL="SELECT a.detailid,a.initiationid,a.additionalrequirements,a.methodology,a.otherinformation,a.enclosures,a.PrototypesNo,a.deliverables,a.createdby,a.createddate,a.modifiedby,a.modifieddate,SanctionDate,a.TitleProgramme,a.prototypeDetails,a.PDRemarks,a.LabdirectorDetails,a.Highdevelopmentrisk,a.designIteration,a.subProjectDetails FROM pfms_initiation_macro_details a WHERE a.initiationid=:initiationid AND a.isactive=1";	@Override
	public Object[] projectMacroDetails(String initiationid) throws Exception {
		// TODO Auto-generated method stub
		Query query =manager.createNativeQuery(MACRODETAIL);
		query.setParameter("initiationid", Long.parseLong(initiationid));
		Object[]MACRODETAIL= {};
		try {
			MACRODETAIL=(Object[])query.getSingleResult();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return MACRODETAIL;
	}

	@Override
	public long InsertMacroDetails(PfmsInitiationMacroDetails pm) throws Exception {
		// TODO Auto-generated method stub
		manager.persist(pm);

		return (Long )pm.getDetailId();
	}
	private static final String METHODOLOGYUPDATE="UPDATE pfms_initiation_macro_details SET Methodology=:Methodology ,modifiedby=:modifiedby, modifieddate=:modifieddate WHERE initiationid=:initiationid AND isactive=1"; 
	@Override
	public long updateMactroDetailsMethodology(PfmsInitiationMacroDetails pm) throws Exception {
		// TODO Auto-generated method stub
		long count=0;
		Query query=manager.createNativeQuery(METHODOLOGYUPDATE);
		query.setParameter("Methodology", pm.getMethodology());
		query.setParameter("modifiedby", pm.getModifiedBy());
		query.setParameter("modifieddate", pm.getModifiedDate());
		query.setParameter("initiationid", pm.getInitiationId());
		count= query.executeUpdate();	
		return count;
	}	
	private static final String MACREQUPDATE="UPDATE pfms_initiation_macro_details SET AdditionalRequirements=:AdditionalRequirements ,modifiedby=:modifiedby, modifieddate=:modifieddate WHERE initiationid=:initiationid AND isactive=1"; 
	@Override
	public long updateMactroDetailsRequirements(PfmsInitiationMacroDetails pm) throws Exception {
		// TODO Auto-generated method stub

		long count=0;

		Query query=manager.createNativeQuery(MACREQUPDATE);
		query.setParameter("AdditionalRequirements", pm.getAdditionalRequirements());
		query.setParameter("modifiedby", pm.getModifiedBy());
		query.setParameter("modifieddate", pm.getModifiedDate());
		query.setParameter("initiationid", pm.getInitiationId());
		count= query.executeUpdate();	
		return count;
	}
	private static final String ENCLOSURESUPDATE="UPDATE pfms_initiation_macro_details SET Enclosures=:Enclosures ,modifiedby=:modifiedby, modifieddate=:modifieddate WHERE initiationid=:initiationid AND isactive=1";
	@Override
	public long UpdateProjectEnclosure(PfmsInitiationMacroDetails pm) throws Exception {
		// TODO Auto-generated method stub
		long count=0;

		Query query=manager.createNativeQuery(ENCLOSURESUPDATE);
		query.setParameter("Enclosures", pm.getEnclosures());
		query.setParameter("modifiedby", pm.getModifiedBy());
		query.setParameter("modifieddate", pm.getModifiedDate());
		query.setParameter("initiationid", pm.getInitiationId());
		count= query.executeUpdate();	
		return count;
	}
	private static final String OTHINFMUPDATE="UPDATE pfms_initiation_macro_details SET OtherInformation=:OtherInformation ,modifiedby=:modifiedby, modifieddate=:modifieddate WHERE initiationid=:initiationid AND isactive=1";
	@Override
	public long UpdateProjectOtherInformation(PfmsInitiationMacroDetails pm) throws Exception {
		// TODO Auto-generated method stub
		long count=0;

		Query query=manager.createNativeQuery(OTHINFMUPDATE);
		query.setParameter("OtherInformation", pm.getOtherInformation());
		query.setParameter("modifiedby", pm.getModifiedBy());
		query.setParameter("modifieddate", pm.getModifiedDate());
		query.setParameter("initiationid", pm.getInitiationId());
		count= query.executeUpdate();	
		return count;
	}
	private static final String DELIVERABLESUPDATE="UPDATE pfms_initiation_macro_details SET PrototypesNo=:PrototypesNo,deliverables=:deliverables ,modifiedby=:modifiedby, modifieddate=:modifieddate,SanctionDate=:SanctionDate,TitleProgramme=:TitleProgramme,prototypeDetails=:prototypeDetails,PDRemarks=:PDRemarks,LabdirectorDetails=:LabdirectorDetails,Highdevelopmentrisk=:Highdevelopmentrisk,subProjectDetails=:subProjectDetails,designIteration=:designIteration WHERE initiationid=:initiationid AND isactive=1";
	@Override
	public long ProposedprojectdeliverablesUpdate(PfmsInitiationMacroDetails pm) throws Exception {
		// TODO Auto-generated method stub
		long count=0;

		Query query=manager.createNativeQuery(DELIVERABLESUPDATE);
		query.setParameter("PrototypesNo", pm.getPrototypesNo());
		query.setParameter("deliverables", pm.getDeliverables());
		query.setParameter("modifiedby", pm.getModifiedBy());
		query.setParameter("modifieddate", pm.getModifiedDate());
		query.setParameter("initiationid", pm.getInitiationId());
		query.setParameter("SanctionDate", pm.getSanctionDate() );
		query.setParameter("TitleProgramme", pm.getTitleProgramme() );
		query.setParameter("prototypeDetails", pm.getPrototypeDetails() );
		query.setParameter("PDRemarks",pm.getPDRemarks() );
		query.setParameter("LabdirectorDetails",pm.getLabdirectorDetails() );
		query.setParameter("Highdevelopmentrisk",pm.getHighdevelopmentrisk() );
		query.setParameter("subProjectDetails",pm.getSubProjectDetails() );
		query.setParameter("designIteration",pm.getDesignIteration() );
		count= query.executeUpdate();	
		return count;
	}
	@Override
	public long ProjectMajorTrainingRequirementSubmit(ProjectMajorRequirements PMR) throws Exception {
		// TODO Auto-generated method stub
		manager.persist(PMR);
		manager.flush();
		return PMR.getTrainingId();
	}
	private static final String TRAINGLIST="SELECT trainingid,initiationid,Discipline,agency,personneltrained,duration,remarks,cost FROM pfms_initiation_soc_training WHERE initiationid=:initiationid AND isactive=1";
	@Override
	public List<Object[]> TrainingRequirementList(String initiationid) throws Exception {
		// TODO Auto-generated method stub
		Query query=manager.createNativeQuery(TRAINGLIST);
		query.setParameter("initiationid", Long.parseLong(initiationid));

		List<Object[]>TrainingRequirementList=(List<Object[]>)query.getResultList();

		return TrainingRequirementList;
	}
	private static final String TRANREQ="SELECT trainingid,initiationid,Discipline,agency,personneltrained,duration,cost,remarks FROM pfms_initiation_soc_training WHERE trainingid=:trainingid AND isactive=1";
	@Override
	public Object[] TraingRequirements(String trainingid) throws Exception {
		// TODO Auto-generated method stub
		Query query= manager.createNativeQuery(TRANREQ);
		query.setParameter("trainingid", Long.parseLong(trainingid));
		Object[]TraingRequirements=(Object[])query.getSingleResult();
		return TraingRequirements;
	}

	@Override
	public long ProjectMajorTrainingRequirementUpdate(ProjectMajorRequirements pmr) throws Exception {
		ProjectMajorRequirements ExistingProjectMajorRequirements = manager.find(ProjectMajorRequirements.class, pmr.getTrainingId());
		if(ExistingProjectMajorRequirements != null && ExistingProjectMajorRequirements.getIsActive()==1) {
			ExistingProjectMajorRequirements.setDiscipline(pmr.getDiscipline());
			ExistingProjectMajorRequirements.setAgency(pmr.getAgency());
			ExistingProjectMajorRequirements.setPersonneltrained(pmr.getPersonneltrained());
			ExistingProjectMajorRequirements.setDuration(pmr.getDuration());
			ExistingProjectMajorRequirements.setCost(pmr.getCost());
			ExistingProjectMajorRequirements.setRemarks(pmr.getRemarks());
			ExistingProjectMajorRequirements.setModifiedBy(pmr.getModifiedBy());
			ExistingProjectMajorRequirements.setModifiedDate(pmr.getModifiedDate());
			return 1L;
		}
		else {
			return 0L;
		}
	}
	@Override
	public long WorkPackageSubmit(ProjectMajorWorkPackages pw) throws Exception {
		// TODO Auto-generated method stub
		manager.persist(pw);
		manager.flush();
		return pw.getWorkId();
	}
	private static final String WORKLIST="SELECT workid,initiationid,govtagencies,workpackage,objective,scope,pdc,cost FROM pfms_initiation_soc_wp WHERE initiationid=:initiationid AND isactive=1";
	@Override
	public List<Object[]> WorkPackageList(String initiationid) throws Exception {
		// TODO Auto-generated method stub
		Query query=manager.createNativeQuery(WORKLIST);
		query.setParameter("initiationid", Long.parseLong(initiationid));
		List<Object[]>WorkPackageList=(List<Object[]>)query.getResultList();

		return WorkPackageList;
	}
	private static final String  WORKVALUE="SELECT workid,initiationid,govtagencies,workpackage,objective,scope,pdc,cost FROM pfms_initiation_soc_wp WHERE workid=:parameter AND isactive=1";
	@Override
	public Object[] WorkPackageValue(String parameter) throws Exception {
		// TODO Auto-generated method stub
		Query query=manager.createNativeQuery(WORKVALUE);
		query.setParameter("parameter", parameter);
		return (Object[])query.getSingleResult();
	}

	@Override
	public long WorkPackagesEdit(ProjectMajorWorkPackages pw) throws Exception {
		ProjectMajorWorkPackages ExistingProjectMajorWorkPackages= manager.find(ProjectMajorWorkPackages.class, pw.getWorkId());
		if(ExistingProjectMajorWorkPackages != null && ExistingProjectMajorWorkPackages.getIsActive()==1) {
			ExistingProjectMajorWorkPackages.setGovtAgencies(pw.getGovtAgencies());
			ExistingProjectMajorWorkPackages.setWorkPackage(pw.getWorkPackage());
			ExistingProjectMajorWorkPackages.setObjective(pw.getObjective());
			ExistingProjectMajorWorkPackages.setScope(pw.getScope());
			ExistingProjectMajorWorkPackages.setCost(pw.getCost());
			ExistingProjectMajorWorkPackages.setPDC(pw.getPDC());
			ExistingProjectMajorWorkPackages.setModifiedBy(pw.getModifiedBy());
			ExistingProjectMajorWorkPackages.setModifiedDate(pw.getModifiedDate());
			return 1L;
		}
		else {
			return 0L;
		}
		
	}
	@Override
	public long CarsDetailsAdd(ProjectMajorCars pmc) throws Exception {
		// TODO Auto-generated method stub
		manager.persist(pmc);
		manager.flush();
		return pmc.getCarsId();
	}
	private static final String CARSLIST="SELECT carsid,initiationid,institute,professor,AreaRd,cost,pdc,confidencelevel FROM pfms_initiation_soc_cars WHERE initiationid=:parameter AND isactive=1";
	@Override
	public List<Object[]> CarsList(String parameter) throws Exception {
		// TODO Auto-generated method stub
		Query query=manager.createNativeQuery(CARSLIST);
		query.setParameter("parameter", parameter);

		return (List<Object[]>)query.getResultList();
	}
	private static final String CARSVALUE="SELECT carsid,initiationid,institute,professor,AreaRd,cost,pdc,confidencelevel FROM pfms_initiation_soc_cars WHERE carsid=:parameter AND isactive=1";
	@Override
	public Object[] CarsValue(String parameter) throws Exception {
		Query query=manager.createNativeQuery(CARSVALUE);
		query.setParameter("parameter", parameter);
		return (Object[])query.getSingleResult();
	}
	private static final String CARSEDIT="UPDATE pfms_initiation_soc_cars SET institute=:institute,professor=:profe"
			+ "ssor,AreaRd=:AreaRd,cost=:cost,pdc=:pdc,confidencelevel=:confidencelevel,modifiedby=:modifiedby,"
			+ " modifieddate=:modifieddate WHERE carsid=:carsid AND isactive=1";
	@Override
	public long CarEdit(ProjectMajorCars pmc) throws Exception {
		ProjectMajorCars ExistingProjectMajorCars= manager.find(ProjectMajorCars.class, pmc.getCarsId());
		if(ExistingProjectMajorCars != null && ExistingProjectMajorCars.getIsActive()==1) {
			ExistingProjectMajorCars.setInstitute(pmc.getInstitute());
			ExistingProjectMajorCars.setProfessor(pmc.getProfessor());
			ExistingProjectMajorCars.setAreaRD(pmc.getAreaRD());
			ExistingProjectMajorCars.setCost(pmc.getCost());
			ExistingProjectMajorCars.setPDC(pmc.getPDC());
			ExistingProjectMajorCars.setConfidencelevel(pmc.getConfidencelevel());
			ExistingProjectMajorCars.setModifiedBy(pmc.getModifiedBy());
			ExistingProjectMajorCars.setModifiedDate(pmc.getModifiedDate());
			return 1L;
			}
		else {
			return 0L;
		}
	
	}

	@Override
	public long ConsultancySubmit(ProjectMajorConsultancy pmc) throws Exception {
		// TODO Auto-generated method stub
		manager.persist(pmc);
		manager.flush();
		return pmc.getConsultancyId();
	}
	private static final String CONSULTANCYLIST="SELECT a.consultancyid,a.initiationid,a.Discipline ,a.agency,a.person,a.cost,a.process FROM pfms_initiation_soc_consultancy a WHERE a.initiationid=:initiationid AND isactive=1";
	@Override
	public List<Object[]> ConsultancyList(String initiationid) throws Exception {
		// TODO Auto-generated method stub
		Query query =manager.createNativeQuery(CONSULTANCYLIST);
		query.setParameter("initiationid", Long.parseLong(initiationid));
		return (List<Object[]>)query.getResultList();
	}
	private static final String CONSULTANCYVALUE="SELECT a.consultancyid,a.initiationid,a.Discipline ,a.agency,a.person,a.cost,a.process FROM pfms_initiation_soc_consultancy a WHERE consultancyid=:parameter AND isactive=1";
	@Override
	public Object[] ConsultancyValue(String parameter) throws Exception {
		// TODO Auto-generated method stub
		Query query=manager.createNativeQuery(CONSULTANCYVALUE);
		query.setParameter("parameter", parameter);
		return (Object[])query.getSingleResult();
	}

	@Override
	public long ConsultancyEdit(ProjectMajorConsultancy pmc) throws Exception {
		ProjectMajorConsultancy ExistingProjectMajorConsultancy = manager.find(ProjectMajorConsultancy.class, pmc.getConsultancyId());
		if(ExistingProjectMajorConsultancy != null && ExistingProjectMajorConsultancy.getIsActive()==1) {
			ExistingProjectMajorConsultancy.setDiscipline(pmc.getDiscipline());
			ExistingProjectMajorConsultancy.setAgency(pmc.getAgency());
			ExistingProjectMajorConsultancy.setPerson(pmc.getPerson());
			ExistingProjectMajorConsultancy.setCost(pmc.getCost());
			ExistingProjectMajorConsultancy.setProcess(pmc.getProcess());
			ExistingProjectMajorConsultancy.setModifiedBy(pmc.getModifiedBy());
			ExistingProjectMajorConsultancy.setModifiedDate(pmc.getModifiedDate());
			return 1L;
		}
		else {
			return 0L;
		}

	}

	@Override
	public long ManpowerSubmit(ProjectMajorManPowers pm) throws Exception {
		// TODO Auto-generated method stub
		manager.persist(pm);
		manager.flush();
		return pm.getRequirementId();
	}
	private static final String MANPOWLIST="SELECT a.requirementid,a.initiationid,a.designation,a.discipline,a.numbers,a.period,a.remarks FROM pfms_initiation_soc_manpower a WHERE a.initiationid=:parameter AND isactive=1";
	@Override
	public List<Object[]> ManpowerList(String parameter) throws Exception {
		// TODO Auto-generated method stub
		Query query=manager.createNativeQuery(MANPOWLIST);
		query.setParameter("parameter", parameter);
		return (List<Object[]>)query.getResultList();
	}
	private static final String MANPOWVAL="SELECT a.requirementid,a.initiationid,a.designation,a.discipline,a.numbers,a.period,a.remarks FROM pfms_initiation_soc_manpower a WHERE a.requirementid=:requirementid AND isactive=1";
	@Override
	public Object[] ManpowerValue(String parameter) throws Exception {
		// TODO Auto-generated method stub
		Query query =manager.createNativeQuery(MANPOWVAL);
		query.setParameter("requirementid", parameter);
		return (Object[])query.getSingleResult();
	}
	
	@Override
	public long ManPowerEdit(ProjectMajorManPowers pm) throws Exception {
		ProjectMajorManPowers ExistingProjectMajorManPowers = manager.find(ProjectMajorManPowers.class, pm.getRequirementId());
		if(ExistingProjectMajorManPowers != null) {
			ExistingProjectMajorManPowers.setDesignation(pm.getDesignation());
			ExistingProjectMajorManPowers.setDiscipline(pm.getDiscipline());
			ExistingProjectMajorManPowers.setNumbers(pm.getNumbers());
			ExistingProjectMajorManPowers.setPeriod(pm.getPeriod());
			ExistingProjectMajorManPowers.setRemarks(pm.getRemarks());
			ExistingProjectMajorManPowers.setModifiedBy(pm.getModifiedBy());
			ExistingProjectMajorManPowers.setModifiedDate(pm.getModifiedDate());
			return 1L;
			}
		else {
			return 0L;
		}

	}
	private static final String MACRODETAILS2="SELECT DetailId,InitiationId,AdditionalInformation,Comments,Recommendations,AdditionalCapital,BuildingSpaceRequirement FROM pfms_initiation_macro_details_part2 WHERE initiationid=:initiationid AND isactive=1" ;	@Override
	public Object[] macroDetailsPartTwo(String initiationid) throws Exception {
		Query query =manager.createNativeQuery(MACRODETAILS2);
		query.setParameter("initiationid", Long.parseLong(initiationid));
		Object[]macroDetailsPartTwo= {};
		try {
			macroDetailsPartTwo=(Object[])query.getSingleResult();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return macroDetailsPartTwo;
	}
	@Override
	public long MacroDetailsPartTwoSubmit(PfmsInitiationMacroDetailsTwo pmd) throws Exception {
		// TODO Auto-generated method stub
		manager.persist(pmd);
		manager.flush();
		return pmd.getDetailId();
	}
	private static final String MACRODETAILSEDIT="UPDATE pfms_initiation_macro_details_part2 SET AdditionalInformation=:AdditionalInformation,Comments=:Comments,Recommendations=:Recommendations,AdditionalCapital=:AdditionalCapital,ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate,BuildingSpaceRequirement=:BuildingSpaceRequirement WHERE InitiationId=:InitiationId AND isactive='1'";
	@Override
	public long MacroDetailsPartTwoEdit(PfmsInitiationMacroDetailsTwo pmd) throws Exception {
		Query query=manager.createNativeQuery(MACRODETAILSEDIT);
		query.setParameter("AdditionalInformation", pmd.getAdditionalInformation());
		query.setParameter("Comments", pmd.getComments());
		query.setParameter("Recommendations", pmd.getRecommendations());
		query.setParameter("AdditionalCapital", pmd.getAdditionalCapital());
		query.setParameter("ModifiedBy", pmd.getModifiedBy());
		query.setParameter("ModifiedDate", pmd.getModifiedDate());
		query.setParameter("InitiationId", pmd.getInitiationId());
		query.setParameter("BuildingSpaceRequirement", pmd.getBuildingSpaceRequirement()  );
		return query.executeUpdate();
	}


	private static final String BRIEFLIST="SELECT socid,Initiationid,TRLanalysis,PeerReview,ActionPlan,TestingPlan,ResponsibilityMatrix,DevelopmentPartner,ProductionAgencies,CostsBenefit,ProjectManagement,PERT,Achievement,CriticalTech FROM pfms_initiation_soc_brief WHERE InitiationId=:initiationid AND isactive=1";
	@Override
	public Object[] BriefTechnicalAppreciation(String initiationid) throws Exception {
		// TODO Auto-generated method stub
		Query query=manager.createNativeQuery(BRIEFLIST);
		query.setParameter("initiationid", Long.parseLong(initiationid));

		Object[]BriefTechnicalAppreciation= {};
		try {
			BriefTechnicalAppreciation=(Object[])query.getSingleResult();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return BriefTechnicalAppreciation;
	}

	@Override
	public long BriefTechnicalAppreciationSubmit(ProjectMactroDetailsBrief pmb) throws Exception {
		// TODO Auto-generated method stub
		manager.persist(pmb);
		manager.flush();
		return pmb.getSocId();
	}


	private static final String BRIEFEDIT="UPDATE pfms_initiation_soc_brief SET TRLanalysis=:TRLanalysis,PeerReview=:PeerReview,ActionPlan=:ActionPlan,TestingPlan=:TestingPlan,ResponsibilityMatrix=:ResponsibilityMatrix,DevelopmentPartner=:DevelopmentPartner,ProductionAgencies=:ProductionAgencies,CostsBenefit=:CostsBenefit,ProjectManagement=:ProjectManagement,PERT=:PERT,Achievement=:Achievement,CriticalTech=:CriticalTech,ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate WHERE InitiationId=:InitiationId AND isactive=1";
	@Override
	public long BriefTechnicalAppreciationEdit(ProjectMactroDetailsBrief pmb) throws Exception {
		// TODO Auto-generated method stub

		Query query =manager.createNativeQuery(BRIEFEDIT);
		query.setParameter("TRLanalysis", pmb.getTRLanalysis());
		query.setParameter("PeerReview", pmb.getPeerReview());
		query.setParameter("ActionPlan", pmb.getActionPlan());
		query.setParameter("TestingPlan", pmb.getTestingPlan());
		query.setParameter("ResponsibilityMatrix", pmb.getResponsibilityMatrix());
		query.setParameter("DevelopmentPartner", pmb.getDevelopmentPartner());
		query.setParameter("ProductionAgencies", pmb.getProductionAgencies());
		query.setParameter("CostsBenefit", pmb.getCostsBenefit());
		query.setParameter("ProjectManagement", pmb.getProjectManagement());
		query.setParameter("PERT", pmb.getPERT());
		query.setParameter("Achievement",pmb.getAchievement());
		query.setParameter("CriticalTech", pmb.getCriticalTech());
		query.setParameter("ModifiedBy", pmb.getModifiedBy());
		query.setParameter("ModifiedDate", pmb.getModifiedDate());
		query.setParameter("InitiationId", pmb.getInitiationId());

		return query.executeUpdate();
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
	public long ProjectCapsiSubmit(ProjectMajorCapsi pmc) throws Exception {
		// TODO Auto-generated method stub
		manager.persist(pmc);
		manager.flush();
		return pmc.getCapsId();
	}
	private static final String CAPSLIST="SELECT capsid,initiationid,Station,Consultant,Areard,Cost,pdc,confidencelevel FROM pfms_initiation_soc_capsi WHERE initiationid=:initiationid AND isactive=1";
	@Override
	public List<Object[]> CapsiList(String initiationid) throws Exception {
		// TODO Auto-generated method stub
		Query query =manager.createNativeQuery(CAPSLIST);
		query.setParameter("initiationid", Long.parseLong(initiationid));
		return (List<Object[]>)query.getResultList();
	}
	private static final String CAPSVALUE="SELECT capsid,initiationid,Station,Consultant,Areard,Cost,pdc,confidencelevel FROM pfms_initiation_soc_capsi WHERE capsid=:parameter AND isactive=1";
	@Override
	public Object[] CapsiValue(String parameter) throws Exception {
		// TODO Auto-generated method stub
		Query query =manager.createNativeQuery(CAPSVALUE);
		query.setParameter("parameter", parameter);
		return (Object[])query.getSingleResult();
	}
	private static final String CAPSEDIT="UPDATE pfms_initiation_soc_capsi SET station=:station,consultant=:consultant,"
			+ "areard=:areard,cost=:cost,pdc=:pdc,confidencelevel=:confidencelevel,ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate "
			+ "WHERE capsid=:capsid";
	@Override
	public long CapsiEdt(ProjectMajorCapsi pmc) throws Exception {
		ProjectMajorCapsi ExistingProjectMajorCapsi = manager.find(ProjectMajorCapsi.class, pmc.getCapsId());
		if(ExistingProjectMajorCapsi != null) {
			ExistingProjectMajorCapsi.setStation(pmc.getStation());
			ExistingProjectMajorCapsi.setConsultant(pmc.getConsultant());
			ExistingProjectMajorCapsi.setAreaRD(pmc.getAreaRD());
			ExistingProjectMajorCapsi.setCost(pmc.getCost());
			ExistingProjectMajorCapsi.setPDC(pmc.getPDC());
			ExistingProjectMajorCapsi.setConfidencelevel(pmc.getConfidencelevel());
			ExistingProjectMajorCapsi.setModifiedBy(pmc.getModifiedBy());
			ExistingProjectMajorCapsi.setModifiedDate(pmc.getModifiedDate());
			return 1L;
		}
		else {
			return 0L;
		}
		
	}
	private static final String ALLLAB="SELECT labid ,labname,labcode  FROM cluster_lab WHERE labcode=:labcode";
	@Override
	public Object[] ALlLabList(String labcode) throws Exception {
		// TODO Auto-generated method stub
		Query query =manager.createNativeQuery(ALLLAB);
		query.setParameter("labcode", labcode);
		return (Object[])query.getSingleResult();
	}
	private static final String PROCOSTLIST="SELECT planid,InitiationCostId,initiationid,item,ModeName,source,cost,payment,Approved FROM pfms_procurement_plan WHERE initiationid=:initiationid AND InitiationCostId=:initiationCostId AND isactive=1";
	@Override
	public List<Object[]> ProcurementInitiationCostList(String initiationid, String initiationCostId) throws Exception {
		// TODO Auto-generated method stub
		Query query =manager.createNativeQuery(PROCOSTLIST);
		query.setParameter("initiationid", Long.parseLong(initiationid));
		query.setParameter("initiationCostId", Long.parseLong(initiationCostId));
		List<Object[]>ProcurementInitiationCostList=null;
		try{ProcurementInitiationCostList=query.getResultList();}
		catch(Exception e) {
			e.printStackTrace();
		}
		return ProcurementInitiationCostList;
	}

	private static final String ACHVEDIT="UPDATE pfms_initiation_soc_brief SET Achievement=:Achievement,ModifiedBy=:ModifiedBy WHERE InitiationId=:InitiationId";
	@Override
	public long BriefAchievementEdit(ProjectMactroDetailsBrief pmb) throws Exception {
		// TODO Auto-generated method stub
		Query query=manager.createNativeQuery(ACHVEDIT);
		query.setParameter("Achievement", pmb.getAchievement());
		query.setParameter("ModifiedBy", pmb.getModifiedBy());
		query.setParameter("InitiationId", pmb.getInitiationId());


		return query.executeUpdate();
	}
	private static final String TRLEDT="UPDATE pfms_initiation_soc_brief SET TRLanalysis=:TRLanalysis,ModifiedBy=:ModifiedBy WHERE InitiationId=:InitiationId";
	@Override
	public long BriefTRLanalysisEdit(ProjectMactroDetailsBrief pmb) throws Exception {
		// TODO Auto-generated method stub
		Query query=manager.createNativeQuery(TRLEDT);
		query.setParameter("TRLanalysis", pmb.getTRLanalysis());
		query.setParameter("ModifiedBy", pmb.getModifiedBy());
		query.setParameter("InitiationId", pmb.getInitiationId());
		return query.executeUpdate();
	}
	private static final String PEEREDIT="UPDATE pfms_initiation_soc_brief SET PeerReview=:PeerReview,ModifiedBy=:ModifiedBy WHERE InitiationId=:InitiationId";
	@Override
	public long BriefpeerEdit(ProjectMactroDetailsBrief pmb) throws Exception {
		// TODO Auto-generated method stub
		Query query=manager.createNativeQuery(PEEREDIT);
		query.setParameter("PeerReview", pmb.getPeerReview());
		query.setParameter("ModifiedBy", pmb.getModifiedBy());
		query.setParameter("InitiationId", pmb.getInitiationId());
		return query.executeUpdate();
	}
	private static final String ACTEDIT="UPDATE pfms_initiation_soc_brief SET ActionPlan=:ActionPlan,ModifiedBy=:ModifiedBy WHERE InitiationId=:InitiationId"; 
	@Override
	public long BriefActionEdit(ProjectMactroDetailsBrief pmb) throws Exception {
		// TODO Auto-generated method stub
		Query query=manager.createNativeQuery(ACTEDIT);
		query.setParameter("ActionPlan", pmb.getActionPlan());
		query.setParameter("ModifiedBy", pmb.getModifiedBy());
		query.setParameter("InitiationId", pmb.getInitiationId());
		return query.executeUpdate();
	}
	private static final String TESTEDIT="UPDATE pfms_initiation_soc_brief SET TestingPlan=:TestingPlan,ModifiedBy=:ModifiedBy WHERE InitiationId=:InitiationId";
	@Override
	public long BriefTestEdit(ProjectMactroDetailsBrief pmb) throws Exception {
		// TODO Auto-generated method stub
		Query query=manager.createNativeQuery(TESTEDIT);
		query.setParameter("TestingPlan", pmb.getTestingPlan());
		query.setParameter("ModifiedBy", pmb.getModifiedBy());
		query.setParameter("InitiationId", pmb.getInitiationId());
		return query.executeUpdate();
	}

	private static final String MATRIXEDIT="UPDATE pfms_initiation_soc_brief SET ResponsibilityMatrix=:ResponsibilityMatrix,ModifiedBy=:ModifiedBy WHERE InitiationId=:InitiationId";
	@Override
	public long BriefMatrixEdit(ProjectMactroDetailsBrief pmb) throws Exception {
		// TODO Auto-generated method stub
		Query query=manager.createNativeQuery(MATRIXEDIT);
		query.setParameter("ResponsibilityMatrix", pmb.getResponsibilityMatrix());
		query.setParameter("ModifiedBy", pmb.getModifiedBy());
		query.setParameter("InitiationId", pmb.getInitiationId());
		return query.executeUpdate();
	}
	private static final String DEVEDIT="UPDATE pfms_initiation_soc_brief SET DevelopmentPartner=:DevelopmentPartner,ModifiedBy=:ModifiedBy WHERE InitiationId=:InitiationId" ;
	@Override
	public long BriefDevEdit(ProjectMactroDetailsBrief pmb) throws Exception {
		// TODO Auto-generated method stub
		Query query=manager.createNativeQuery(DEVEDIT);
		query.setParameter("DevelopmentPartner", pmb.getDevelopmentPartner());
		query.setParameter("ModifiedBy", pmb.getModifiedBy());
		query.setParameter("InitiationId", pmb.getInitiationId());
		return query.executeUpdate();
	}
	private static final String PROEDIT="UPDATE pfms_initiation_soc_brief SET ProductionAgencies=:ProductionAgencies,ModifiedBy=:ModifiedBy WHERE InitiationId=:InitiationId";
	@Override
	public long BriefProductionAgenciesEdit(ProjectMactroDetailsBrief pmb) throws Exception {
		// TODO Auto-generated method stub
		Query query=manager.createNativeQuery(PROEDIT);
		query.setParameter("ProductionAgencies", pmb.getProductionAgencies());
		query.setParameter("ModifiedBy", pmb.getModifiedBy());
		query.setParameter("InitiationId", pmb.getInitiationId());
		return query.executeUpdate();
	}
	private static final String TECHDIT="UPDATE pfms_initiation_soc_brief SET CriticalTech=:CriticalTech,ModifiedBy=:ModifiedBy WHERE InitiationId=:InitiationId";
	@Override
	public long BriefCriticalTechsEdit(ProjectMactroDetailsBrief pmb) {
		// TODO Auto-generated method stub
		Query query=manager.createNativeQuery(TECHDIT);
		query.setParameter("CriticalTech", pmb.getCriticalTech());
		query.setParameter("ModifiedBy", pmb.getModifiedBy());
		query.setParameter("InitiationId", pmb.getInitiationId());
		return query.executeUpdate();
	}
	private static final String COSTBEDIT="UPDATE pfms_initiation_soc_brief SET CostsBenefit=:CostsBenefit,ModifiedBy=:ModifiedBy WHERE InitiationId=:InitiationId";
	@Override
	public long BriefCostsBenefitsEdit(ProjectMactroDetailsBrief pmb) throws Exception {
		// TODO Auto-generated method stub
		Query query=manager.createNativeQuery(COSTBEDIT);
		query.setParameter("CostsBenefit", pmb.getCostsBenefit());
		query.setParameter("ModifiedBy", pmb.getModifiedBy());
		query.setParameter("InitiationId", pmb.getInitiationId());
		return query.executeUpdate();
	}
	private static final String PROMANEDIT="UPDATE pfms_initiation_soc_brief SET ProjectManagement=:ProjectManagement,ModifiedBy=:ModifiedBy WHERE InitiationId=:InitiationId";
	@Override
	public long BriefProjectManagementEdit(ProjectMactroDetailsBrief pmb) throws Exception {
		// TODO Auto-generated method stub
		Query query=manager.createNativeQuery(PROMANEDIT);
		query.setParameter("ProjectManagement", pmb.getProjectManagement());
		query.setParameter("ModifiedBy", pmb.getModifiedBy());
		query.setParameter("InitiationId", pmb.getInitiationId());
		return query.executeUpdate();
	}
	private static final String PERTEDIT="UPDATE pfms_initiation_soc_brief SET PERT=:PERT,ModifiedBy=:ModifiedBy WHERE InitiationId=:InitiationId";
	@Override
	public long BriefPERTEdit(ProjectMactroDetailsBrief pmb) throws Exception {
		// TODO Auto-generated method stub
		Query query=manager.createNativeQuery(PERTEDIT);
		query.setParameter("PERT", pmb.getPERT());
		query.setParameter("ModifiedBy", pmb.getModifiedBy());
		query.setParameter("InitiationId", pmb.getInitiationId());
		return query.executeUpdate();
	}
	private static final String REQSTATUS="SELECT a.statusid,a.status,a.approvalId,a.RequirementNumber,a.version  FROM pfms_initiation_req_status a WHERE a.ReqInitiationId=:ReqInitiationId AND isactive=1";
	@Override
	public Object[] reqStatus(Long reqInitiationId) throws Exception {
		// TODO Auto-generated method stub
		Query query =manager.createNativeQuery(REQSTATUS);
		query.setParameter("ReqInitiationId", reqInitiationId);
		Object []reqStatus=null;
		try {
			reqStatus=(Object[])query.getSingleResult();	
		}catch (Exception e) {
			e.printStackTrace();
		}
		return reqStatus;
	}
	@Override
	public long ProjectRequirementAdd(PfmsInititationRequirement pir, PfmsReqStatus prs) throws Exception {
		// TODO Auto-generated method stub
		manager.persist(pir);
		manager.persist(prs);
		return pir.getInitiationReqId();
	}

	private static final String EMPID="SELECT EmpId FROM pfms_initiation WHERE InitiationId=:InitiationId";
	@Override
	public String getEmpId(String pdd) throws Exception {
		try {
			Query query =manager.createNativeQuery(EMPID);
			query.setParameter("InitiationId", Long.parseLong(pdd));
			return (Long) query.getSingleResult()+"";
		}catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
		
	}

	private static final String DOCFLWDATA="SELECT pr.empid ,CONCAT(IFNULL(CONCAT(e.title,' '),''), e.empname) AS 'empname' ,ed.designation ,pr.type  FROM pfms_initiation_approver pr, employee e ,employee_desig ed WHERE pr.empid=e.empid AND e.desigid=ed.desigid AND pr.isactive='1' AND pr.LabCode=:labcode AND pr.initiationid=:initiationid ORDER BY FIELD (pr.type,'Reviewer','Approver')";
	@Override
	public List<Object[]> DocumentApprovalFlowData(String labCode, String initiationid) throws Exception {
		Query query=manager.createNativeQuery(DOCFLWDATA);
		query.setParameter("labcode", labCode);
		query.setParameter("initiationid", Long.parseLong(initiationid));
		List<Object[]>DocumentApprovalFlowData=(List<Object[]>)query.getResultList();
		return DocumentApprovalFlowData;
	}

	private static final String MAXREQ="SELECT IFNULL(MAX(a.version),0) AS 'MAX' FROM pfms_initiation_req_status a WHERE ReqInitiationId=:ReqInitiationId AND isactive=1";
	@Override
	public String maxRequirementVersion(Long reqInitiationId) throws Exception {
		Query query =manager.createNativeQuery(MAXREQ);
		query.setParameter("ReqInitiationId", reqInitiationId);
		Object version=(Object)query.getSingleResult();
		try {
			if(version==null) {
				version="0";
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return version.toString();
	}
	private static final String REVIEWER="SELECT empid FROM pfms_initiation_approver pr WHERE initiationid=:initiationid AND pr.type='Reviewer' AND isactive='1'";
	@Override
	public String getInitiationReviewer(String initiationid) throws Exception {
		Query query = manager.createNativeQuery(REVIEWER);
		query.setParameter("initiationid", Long.parseLong(initiationid));
		Object reviewer=0;
		try {
			reviewer=	(Object)query.getSingleResult();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return reviewer.toString();
	}

//	private static final String REQAPPRVLIST=" (SELECT a.initiationid,a.status,b.remarks,d.projectshortname,d.projecttitle,e.empname,b.actiondate,a.requirementNumber FROM pfms_initiation_req_status a , pfms_initiation_req_approval b,pfms_initiation_approver c,pfms_initiation d,employee e WHERE a.initiationid=d.initiationid AND a.approvalId=b.reqapprovalid AND a.status IN('RFU') AND b.empid=e.empid AND a.initiationid=c.initiationid AND c.empid=:empid AND c.type IN ('Reviewer')AND a.isactive='1')UNION (SELECT a.initiationid,a.status,b.remarks,d.projectshortname,d.projecttitle,e.empname,b.actiondate,a.requirementNumber  FROM pfms_initiation_req_status a ,pfms_initiation_req_approval b, pfms_initiation_approver c ,pfms_initiation d,employee e WHERE a.initiationid=d.initiationid AND a.approvalId=b.reqapprovalid AND a.status IN('RFD') AND b.empid=e.empid AND a.initiationid=c.initiationid AND c.empid=:empid AND c.type IN ('Approver')AND a.isactive='1')";
//	@Override
//	public List<Object[]> RequirementApprovalList(String empId) throws Exception {
//		Query query =manager.createNativeQuery(REQAPPRVLIST);
//		query.setParameter("empid", empId);
//		return (List<Object[]>)query.getResultList();
//	}

	private static final String APPROVER="SELECT empid FROM pfms_initiation_approver pr WHERE initiationid=:initiationid AND pr.type='Approver' AND isactive='1'";
	@Override
	public String getInitiationApprover(String initiationid) throws Exception {
		Query query = manager.createNativeQuery(APPROVER);
		query.setParameter("initiationid",  Long.parseLong(initiationid));
		Object reviewer=(Object)query.getSingleResult();
		try {
			if(reviewer==null) {
				reviewer="1";
			}
		}catch(Exception e) {
			e.printStackTrace();
		}		
		return reviewer.toString();
	}

//	private static final String REQTRACK="SELECT b.reqapprovalid,b.empid,c.empname,b.actiondate,b.reqstatus,b.remarks,a.TransactionDetails,a.statuscolor,d.designation FROM pfms_initiation_req_transactionlist a , pfms_initiation_req_approval b , employee c,employee_desig d WHERE a.TransactionCode=b.ReqStatus AND b.ReqInitiationId=:ReqInitiationId AND b.empid=c.empid AND c.desigid=d.desigid ORDER BY reqapprovalid";
//	@Override
//	public List<Object[]> RequirementTrackingList(String reqInitiationId) throws Exception {
//		Query query =manager.createNativeQuery(REQTRACK);
//		query.setParameter("ReqInitiationId", reqInitiationId);
//		List<Object[]>TrackingList=(List<Object[]>)query.getResultList();
//		return TrackingList;
//
//	}
	
	private static final String OTHREQ="SELECT requirementid , ReqName FROM pfms_initiation_otherreq WHERE requirementid NOT IN(SELECT reqmainid FROM pfms_initiation_otherreq_details WHERE initiationid=:initiationid AND projectid =:projectid AND isactive=1)";
	@Override
	public List<Object[]> projecOtherRequirements(String initiationid,String projectid) throws Exception {
		Query query= manager.createNativeQuery(OTHREQ);
		query.setParameter("initiationid", initiationid);
		query.setParameter("projectid", projectid);
		return (List<Object[]>)query.getResultList();
	}
	private static final String OTHREQLIST="SELECT RequirementId,ReqInitiationId,ReqMainId,ReqParentId,RequirementName FROM pfms_initiation_otherreq_details WHERE ReqInitiationId=:ReqInitiationId AND ReqMainId=:ReqMainId ORDER BY RequirementId";
	@Override
	public List<Object[]> OtherRequirementList(Long reqInitiationId, Long reqMainId) throws Exception {
		Query query =manager.createNativeQuery(OTHREQLIST);
		query.setParameter("ReqInitiationId", reqInitiationId);
		query.setParameter("ReqMainId", reqMainId);
		return (List<Object[]>)query.getResultList();
	}
	@Override
	public long ProjectOtherRequirementAdd(ProjectOtherReqModel pm) throws Exception {
		manager.persist(pm);
		return pm.getRequirementId();
	}
	private static final String UPDREQNAME="UPDATE pfms_initiation_otherreq_details SET"
			+ " RequirementName=:RequirementName WHERE RequirementId=:RequirementId AND isactive='1'";
	@Override
	public long UpdateOtherRequirementName(ProjectOtherReqModel pm) throws Exception {
		ProjectOtherReqModel ExistingProjectOtherReqModel= manager.find(ProjectOtherReqModel.class, pm.getRequirementId());
		if(ExistingProjectOtherReqModel != null && ExistingProjectOtherReqModel.getIsActive()==1) {
			ExistingProjectOtherReqModel.setRequirementName(pm.getRequirementName());
			return 1L;
		}
		else {
			return 0L;
		}
		
	}
	private static final String UPDREQDETAILS="UPDATE pfms_initiation_otherreq_details SET "
			+ "RequirementDetails=:RequirementDetails WHERE RequirementId=:RequirementId AND isactive='1'";
	@Override
	public long UpdateOtherRequirementDetails(ProjectOtherReqModel pm) throws Exception {
		ProjectOtherReqModel ExistingProjectOtherReqModel = manager.find(ProjectOtherReqModel.class, pm.getRequirementId());
		if(ExistingProjectOtherReqModel != null && ExistingProjectOtherReqModel.getIsActive()==1) {
			ExistingProjectOtherReqModel.setRequirementDetails(pm.getRequirementDetails());
			return 1L;
		}
		else {
			return 0L;
		}
		
	}
	private static final String REQID="SELECT requirementid FROM pfms_initiation_otherreq_details  WHERE ReqInitiationId=:ReqInitiationId AND reqmainid=:reqmainid AND reqparentid=:reqparentid AND isactive='1'";
	@Override
	public String getRequirementId(Long reqInitiationId, Long reqMainId, int i) throws Exception {

		try {
			Query query=manager.createNativeQuery(REQID);
			query.setParameter("ReqInitiationId", reqInitiationId);
			query.setParameter("reqmainid", reqMainId);
			query.setParameter("reqparentid", i);
			return (Long)query.getSingleResult()+"";
		}catch(Exception e) {
			e.printStackTrace();
			return "0";
		}
	}
	private static final String OTHREQDATA="SELECT Requirementid,initiationid,reqmainid,reqparentid,requirementname,requirementdetails FROM pfms_initiation_otherreq_details WHERE requirementid=:requirementid AND isactive='1'";
	@Override
	public Object[] OtherSubRequirementsDetails(String requirementId) throws Exception {
		Query query =manager.createNativeQuery(OTHREQDATA);
		query.setParameter("requirementid", Long.parseLong(requirementId));
		return (Object[])query.getSingleResult();
	}
	private static final String OTHMAINREQLIST="SELECT ReqMainId,RequirementName,RequirementId FROM pfms_initiation_otherreq_details WHERE initiationid=:initiationid and projectid=:projectId AND reqparentid=0 AND isactive=1 ORDER BY ReqMainId ";
	@Override
	public List<Object[]> otherProjectRequirementList(String initiationid,String projectId) throws Exception {
		Query query =manager.createNativeQuery(OTHMAINREQLIST);
		query.setParameter("initiationid", initiationid);
		query.setParameter("projectId", projectId);
		return (List<Object[]>)query.getResultList();
	}
	private static final String ALLOTHREQ="SELECT RequirementId,ReqMainId,ReqParentId,RequirementName,RequirementDetails,InitiationId FROM pfms_initiation_otherreq_details WHERE InitiationId=:InitiationId AND isactive='1'";
	@Override
	public List<Object[]> getAllOtherReqByInitiationId(String initiationid) throws Exception {
		Query query =manager.createNativeQuery(ALLOTHREQ);
		query.setParameter("InitiationId", initiationid);
		return (List<Object[]>)query.getResultList();
	}
	private static final String LABDETAILS="select lab_code, lab_name,lab_address, lab_city,lab_logo,lab_pin from lab_master where lab_code=:labcode";
	@Override
	public Object[] LabListDetails(String labcode) throws Exception {
		Query query = manager.createNativeQuery(LABDETAILS);
		query.setParameter("labcode", labcode);
		return (Object[])query.getSingleResult();
	}
	private static final String REQINTRO="SELECT ReqInitiationId,Introduction,SystemBlockDiagram,SystemOverview,DocumentOverview,ApplicableStandards FROM pfms_initiation_req_intro WHERE ReqInitiationId=:ReqInitiationId AND isactive=1";
	@Override
	public Object[] RequirementIntro(String reqInitiationId) throws Exception {
		Query query=manager.createNativeQuery(REQINTRO);
//		query.setParameter("initiationid", initiationid);
//		System.out.println("inside dao"+ProjectId);
//		query.setParameter("ProjectId", ProjectId);
		query.setParameter("ReqInitiationId", Long.parseLong(reqInitiationId));
		Object[]ReqIntro=null;
		try {
			ReqIntro=(Object[])query.getSingleResult();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return ReqIntro;
	}

	@Override
	public long ReqIntroSubmit(PfmsInitiationReqIntro pr) throws Exception {
		manager.persist(pr);
		manager.flush();
		return pr.getIntroId();
	}
	//	@Override
	//	public long ReqIntroUpdate(PfmsInitiationReqIntro pr, String details) throws Exception {
	//		System.out.println(details);
	//		if(details.equalsIgnoreCase("Introduction")) {
	//			System.out.println("a");
	//		 String INTROUPDATE="UPDATE pfms_initiation_req_intro SET Introduction=:Introduction, ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate WHERE initiationid=:initiationid";
	//		 Query query=manager.createNativeQuery(INTROUPDATE);
	//		 query.setParameter("Introduction", pr.getIntroduction());
	//		 query.setParameter("ModifiedBy", pr.getModifiedBy());
	//		 query.setParameter("ModifiedDate", pr.getModifiedDate());
	//		 query.setParameter("initiationid", pr.getInitiationId());
	//		 return query.executeUpdate();
	//		 
	//		}else if(details.equalsIgnoreCase("Block Diagram")) {
	//		String BLOCKUPDTE="UPDATE pfms_initiation_req_intro SET SystemBlockDiagram=:SystemBlockDiagram, ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate WHERE initiationid=:initiationid";
	//		 Query query=manager.createNativeQuery(BLOCKUPDTE);
	//		 query.setParameter("SystemBlockDiagram", pr.getSystemBlockDiagram());
	//		 query.setParameter("ModifiedBy", pr.getModifiedBy());
	//		 query.setParameter("ModifiedDate", pr.getModifiedDate());
	//		 query.setParameter("initiationid", pr.getInitiationId());
	//		 return query.executeUpdate();	
	//		
	//		}else if(details.equalsIgnoreCase("System Overview")) {
	//		String SYSUPDATE="UPDATE pfms_initiation_req_intro SET SystemOverview=:SystemOverview, ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate WHERE initiationid=:initiationid";
	//		 Query query=manager.createNativeQuery(SYSUPDATE);
	//		 query.setParameter("SystemOverview", pr.getSystemOverview());
	//		 query.setParameter("ModifiedBy", pr.getModifiedBy());
	//		 query.setParameter("ModifiedDate", pr.getModifiedDate());
	//		 query.setParameter("initiationid", pr.getInitiationId());
	//		 return query.executeUpdate();
	//		
	//		}else if(details.equalsIgnoreCase("Document Overview")) {
	//		String DOCUPDATE="UPDATE pfms_initiation_req_intro SET DocumentOverview=:DocumentOverview, ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate WHERE initiationid=:initiationid";
	//		 Query query=manager.createNativeQuery(DOCUPDATE);
	//		 query.setParameter("DocumentOverview", pr.getDocumentOverview());
	//		 query.setParameter("ModifiedBy", pr.getModifiedBy());
	//		 query.setParameter("ModifiedDate", pr.getModifiedDate());
	//		 query.setParameter("initiationid", pr.getInitiationId());
	//		 return query.executeUpdate();
	//		
	//		}else if(details.equalsIgnoreCase("Applicable Standards")) {
	//		String APSUPDATE="UPDATE pfms_initiation_req_intro SET ApplicableStandards=:ApplicableStandards, ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate WHERE initiationid=:initiationid";	
	//		 Query query=manager.createNativeQuery(APSUPDATE);
	//		 query.setParameter("ApplicableStandards", pr.getApplicableStandards());
	//		 query.setParameter("ModifiedBy", pr.getModifiedBy());
	//		 query.setParameter("ModifiedDate", pr.getModifiedDate());
	//		 query.setParameter("initiationid", pr.getInitiationId());
	//		 return query.executeUpdate();
	//		}
	//		
	//		return 0;
	//	}


	@Override
	public long ReqIntroUpdate(PfmsInitiationReqIntro pr, String details) throws Exception {
		System.out.println(details);
		if(details.equalsIgnoreCase("Introduction")) {
			System.out.println("a");
			String INTROUPDATE="UPDATE pfms_initiation_req_intro SET Introduction=:Introduction,ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate WHERE ReqInitiationId=:ReqInitiationId";
			Query query=manager.createNativeQuery(INTROUPDATE);
			query.setParameter("Introduction", pr.getIntroduction());
			query.setParameter("ModifiedBy", pr.getModifiedBy());
			query.setParameter("ModifiedDate", pr.getModifiedDate());
//			query.setParameter("initiationid", pr.getInitiationId());
//			query.setParameter("projectId",pr.getProjectId());
			query.setParameter("ReqInitiationId",pr.getReqInitiationId());
			return query.executeUpdate();

		}else if(details.equalsIgnoreCase("Block Diagram")) {
			System.out.println("a");
			String BLOCKUPDTE="UPDATE pfms_initiation_req_intro SET SystemBlockDiagram=:SystemBlockDiagram, ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate WHERE ReqInitiationId=:ReqInitiationId";
			Query query=manager.createNativeQuery(BLOCKUPDTE);
			query.setParameter("SystemBlockDiagram", pr.getSystemBlockDiagram());
			query.setParameter("ModifiedBy", pr.getModifiedBy());
			query.setParameter("ModifiedDate", pr.getModifiedDate());
//			query.setParameter("initiationid", pr.getInitiationId());
//			query.setParameter("projectId",pr.getProjectId());
			query.setParameter("ReqInitiationId",pr.getReqInitiationId());
			return query.executeUpdate();	

		}else if(details.equalsIgnoreCase("System Overview")) {
			System.out.println("a");
			String SYSUPDATE="UPDATE pfms_initiation_req_intro SET SystemOverview=:SystemOverview, ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate WHERE ReqInitiationId=:ReqInitiationId";
			Query query=manager.createNativeQuery(SYSUPDATE);
			query.setParameter("SystemOverview", pr.getSystemOverview());
			query.setParameter("ModifiedBy", pr.getModifiedBy());
			query.setParameter("ModifiedDate", pr.getModifiedDate());
//			query.setParameter("initiationid", pr.getInitiationId());
//			query.setParameter("projectId",pr.getProjectId());
			query.setParameter("ReqInitiationId",pr.getReqInitiationId());
			return query.executeUpdate();

		}else if(details.equalsIgnoreCase("Document Overview")) {
			System.out.println("a");
			String DOCUPDATE="UPDATE pfms_initiation_req_intro SET DocumentOverview=:DocumentOverview, ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate WHERE ReqInitiationId=:ReqInitiationId";
			Query query=manager.createNativeQuery(DOCUPDATE);
			query.setParameter("DocumentOverview", pr.getDocumentOverview());
			query.setParameter("ModifiedBy", pr.getModifiedBy());
			query.setParameter("ModifiedDate", pr.getModifiedDate());
//			query.setParameter("initiationid", pr.getInitiationId());
//			query.setParameter("projectId",pr.getProjectId());
			query.setParameter("ReqInitiationId",pr.getReqInitiationId());
			return query.executeUpdate();

		}else if(details.equalsIgnoreCase("Applicable Standards")) {
			System.out.println("a");
			String APSUPDATE="UPDATE pfms_initiation_req_intro SET ApplicableStandards=:ApplicableStandards, ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate WHERE ReqInitiationId=:ReqInitiationId";	
			Query query=manager.createNativeQuery(APSUPDATE);
			query.setParameter("ApplicableStandards", pr.getApplicableStandards());
			query.setParameter("ModifiedBy", pr.getModifiedBy());
			query.setParameter("ModifiedDate", pr.getModifiedDate());
//			query.setParameter("initiationid", pr.getInitiationId());
//			query.setParameter("projectId",pr.getProjectId());
			query.setParameter("ReqInitiationId",pr.getReqInitiationId());
			return query.executeUpdate();
		}

		return 0;
	}
	private static final String PRGCOUNT="SELECT COUNT(a.ReqInitiationId) FROM pfms_initiation_req a,pfms_initiation_otherreq_details b ,pfms_initiation_req_intro c WHERE a.ReqInitiationId=b.ReqInitiationId AND a.ReqInitiationId=c.ReqInitiationId AND a.ReqInitiationId=:ReqInitiationId";
	@Override
	public Long ReqForwardProgress(String reqInitiationId) throws Exception {
		try {
			Query query =manager.createNativeQuery(PRGCOUNT);
			query.setParameter("ReqInitiationId", Long.parseLong(reqInitiationId));
			return (Long)query.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
		
	}
	
	@Override
	public long ProjectSqrSubmit(ProjectSqrFile psf) throws Exception {
		String Update="UPDATE pfms_initiation_sqr SET isactive='0' WHERE ReqInitiationId=:ReqInitiationId";
		Query query =manager.createNativeQuery(Update);
		query.setParameter("ReqInitiationId", psf.getReqInitiationId());
		query.executeUpdate();
		manager.persist(psf);
		manager.flush();
		return psf.getSqrId();
	}
	
	private static final String SQRFILES="SELECT a.User,a.refNo,a.ReqInitiationId,a.authority,a.version,a.createdDate,a.sqrNo,a.sqrid,a.PreviousSqrNo,a.MeetingReference,a.PriorityDevelopment,a.FilePath,a.FileName,a.Title,a.QRType FROM pfms_initiation_sqr a WHERE ReqInitiationId=:ReqInitiationId AND isactive=1";
	@Override
	public Object[] SqrFiles(String reqInitiationId) throws Exception {
		Query query =manager.createNativeQuery(SQRFILES);
		query.setParameter("ReqInitiationId", Long.parseLong(reqInitiationId));
		Object[]SQRFILES=null;
		try {
			SQRFILES=(Object[])query.getSingleResult();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return SQRFILES;
	}
	@Override
	public long RequirementParaSubmit(RequirementparaModel rpm) throws Exception {
		manager.persist(rpm);
		manager.flush();
		return rpm.getParaId();
	}
	private static final String PARADETAILS="SELECT paraid,sqrid,ReqInitiationId,parano,paradetails,SINo FROM pfms_initiation_sqr_para WHERE ReqInitiationId=:ReqInitiationId AND isactive=1 ORDER BY SINo";
	@Override
	public List<Object[]> ReParaDetails(String reqInitiationId) throws Exception {
		Query query = manager.createNativeQuery(PARADETAILS);
		query.setParameter("ReqInitiationId", Long.parseLong(reqInitiationId));
		List<Object[]>paraDetails=(List<Object[]>)query.getResultList();
		return paraDetails;
	}
	@Override
	public long RequirementParaEdit(RequirementparaModel rpm) throws Exception {
		
		if(rpm.getParaDetails()==null) {
			
			RequirementparaModel ExistingRequirementparaModel= manager.find(RequirementparaModel.class, rpm.getParaId());
			if(ExistingRequirementparaModel != null && ExistingRequirementparaModel.getIsActive()==1) {
				ExistingRequirementparaModel.setParaNo(rpm.getParaNo());
				return 1L;
			}
			else {
				return 0L;
			}
		}
		else
		{
			RequirementparaModel ExistingRequirementparaModel= manager.find(RequirementparaModel.class, rpm.getParaId());
			if(ExistingRequirementparaModel != null && ExistingRequirementparaModel.getIsActive()==1) {
				ExistingRequirementparaModel.setParaDetails(rpm.getParaDetails());
				return 1L;
			}
			else {
				return 0L;
			}
		}
	}


	@Override
	public Long insertRequirement(PfmsOtherReq pr) throws Exception {
		manager.persist(pr);
		return pr.getRequirementId();
	}
	@Override
	public Long insertReqType(ProjectRequirementType pt) throws Exception {
		manager.persist(pt);
		return pt.getReqTypeId();
	}

	private static final String VERIFICATIONLIST="select VerificationId,Provisions,ReqInitiationId,ProvisionsDetails from pfms_initiation_verification where ReqInitiationId=:ReqInitiationId and isactive='1'";
	@Override
	public List<Object[]> getVerificationList(String reqInitiationId) throws Exception {

		Query query=manager.createNativeQuery(VERIFICATIONLIST);
		query.setParameter("ReqInitiationId", Long.parseLong(reqInitiationId));
		List<Object[]> verificationList=(List<Object[]>)query.getResultList();
		return verificationList;
	}
	@Override
	public long insertRequirementVerification(RequirementVerification rv) throws Exception {
		manager.persist(rv);
		return rv.getVerificationId();
	}


	@Override
	public Long updateRequirementVerification(RequirementVerification rv) throws Exception {
		RequirementVerification ExistingRequirementVerification= manager.find(RequirementVerification.class, rv.getVerificationId());
		if(ExistingRequirementVerification != null && ExistingRequirementVerification.getIsActive()==1)
		{
			ExistingRequirementVerification.setProvisions(rv.getProvisions());
			return 1L;
		}
		else {
			return 0L;
		}
		
	}
	private static final String UPDATEVERDE="UPDATE pfms_initiation_verification SET ProvisionsDetails=:ProvisionsDetails "
			+ "WHERE VerificationId=:VerificationId AND isactive='1'";

	@Override
	public long updateRequirementVerificationDetails(RequirementVerification rv) throws Exception {
		RequirementVerification ExistingRequirementVerification = manager.find(RequirementVerification.class, rv.getVerificationId());
		if(ExistingRequirementVerification != null && ExistingRequirementVerification.getIsActive()==1) {
			ExistingRequirementVerification.setProvisionsDetails(rv.getProvisionsDetails());
			return 1L;
		}
		else {
			return 0L;
		}
		
	}


	//private static final String ABBREVIATIONS="SELECT AbbreviationsId,Abbreviations,Meaning FROM pfms_initiation_req_abbreviations WHERE InitiationId =:InitiationId ";
	private static final String ABBREVIATIONS="SELECT AbbreviationsId,Abbreviations,Meaning FROM pfms_initiation_req_abbreviations WHERE ReqInitiationId =:ReqInitiationId";		
	@Override
	public List<Object[]> getAbbreviationDetails(String reqInitiationId) throws Exception {
		Query query = manager.createNativeQuery(ABBREVIATIONS);
		query.setParameter("ReqInitiationId", Long.parseLong(reqInitiationId));
		return (List<Object[]>)query.getResultList();
	}

	@Override
	public long addAbbreviation(List<InitiationAbbreviations> iaList) throws Exception {
		// TODO Auto-generated method stub

		Long l=0l;

		for(InitiationAbbreviations ia:iaList) {
			manager.persist(ia);
			l++;
		}

		return l;
	}

	@Override
	public long addReqAppendix(PfmsInitiationAppendix pia) throws Exception {
		manager.persist(pia);
		manager.flush();
		return pia.getAppendixId();
	}
	private static final String APPENDIXLIST="SELECT AppendixId,AppendixName,AppendixDetails,InitiationId FROM pfms_initiation_req_appendix WHERE InitiationId=:InitiationId AND IsActive='1'";
	@Override
	public List<Object[]> AppendixList(String initiationid) throws Exception {

		Query query = manager.createNativeQuery(APPENDIXLIST);
		query.setParameter("InitiationId", Long.parseLong(initiationid));

		return (List<Object[]>)query.getResultList();
	}

	@Override
	public Long addReqAcronyms(List<RequirementAcronyms> raList) throws Exception {

		long count=0l;

		for(RequirementAcronyms ra:raList) {
			manager.persist(ra);
			count++;
		}
		return count;
	}

	//		private static final String ACRONYMLIST="SELECT AcronymsId , Acronyms , Definition , InitiationId FROM pfms_initiation_req_acronyms WHERE InitiationId = :InitiationId AND isActive='1'";
	//		@Override
	//		public List<Object[]> getAcronymsList(String initiationid) throws Exception {
	//			Query query = manager.createNativeQuery(ACRONYMLIST);
	//			query.setParameter("InitiationId", initiationid);
	//			return (List<Object[]>)query.getResultList();
	//		}
	//		
	private static final String ACRONYMLIST="SELECT AcronymsId , Acronyms , Definition , ReqInitiationId FROM pfms_initiation_req_acronyms WHERE ReqInitiationId = :ReqInitiationId  AND isActive='1'";
	@Override
	public List<Object[]> getAcronymsList(String reqInitiationId) throws Exception {
		Query query = manager.createNativeQuery(ACRONYMLIST);
		query.setParameter("ReqInitiationId", Long.parseLong(reqInitiationId));
		return (List<Object[]>)query.getResultList();
	}
	@Override
	public long addReqPerformanceParameters(List<RequirementPerformanceParameters> raList) throws Exception {

		Long count =0l;

		for(RequirementPerformanceParameters rpp : raList) {
			manager.persist(rpp);
			count++;
		}
		return count;
	}

	//		private static final String PERFORMANCELIST ="SELECT PerformanceId , KeyEffectiveness , KeyValues , InitiationId FROM pfms_initiation_req_performance WHERE InitiationId = :InitiationId AND isactive='1'";
	//		@Override
	//		public List<Object[]> getPerformanceList(String initiationid) throws Exception {
	//			
	//			Query query = manager.createNativeQuery(PERFORMANCELIST);
	//			
	//			query.setParameter("InitiationId", initiationid);
	//			
	//			return (List<Object[]>)query.getResultList();
	//		}
	private static final String PERFORMANCELIST ="SELECT PerformanceId , KeyEffectiveness , KeyValues , ReqInitiationId FROM pfms_initiation_req_performance WHERE ReqInitiationId = :ReqInitiationId AND  isactive='1'";
	@Override
	public List<Object[]> getPerformanceList(String reqInitiationId) throws Exception {

		Query query = manager.createNativeQuery(PERFORMANCELIST);
		query.setParameter("ReqInitiationId", Long.parseLong(reqInitiationId));
		return (List<Object[]>)query.getResultList();
	}
	@Override
	public long insertTestVerificationFile(ReqTestExcelFile re) throws Exception {
		manager.persist(re);
		manager.flush();
		return re.getTestVerificationId();
	}

	//		private static final String EXCELDATA="SELECT TestVerificationId,FilePath,FileName,InitiationId FROM pfms_initiation_req_testverification WHERE InitiationId =:InitiationId AND isactive=1";
	//		@Override
	//		public Object[] getVerificationExcelData(String initiationid) throws Exception {
	//			Query query = manager.createNativeQuery(EXCELDATA);
	//			query.setParameter("InitiationId", initiationid);	
	//			 Object[] result= (Object[])query.getSingleResult();
	//			return result;
	//		}


	private static final String EXCELDATA="SELECT TestVerificationId,FilePath,FileName,InitiationId FROM pfms_initiation_req_testverification WHERE InitiationId =:InitiationId AND ProjectId=:ProjectId AND isactive=1";
	@Override
	public Object[] getVerificationExcelData(String initiationid,String ProjectId) throws Exception {
		Query query = manager.createNativeQuery(EXCELDATA);
		query.setParameter("InitiationId", initiationid);	
		query.setParameter("ProjectId", ProjectId);
		Object[] result= (Object[])query.getSingleResult();
		return result;
	}



	//private static final String EMPLISTS=" SELECT a.empid,CONCAT(IFNULL(CONCAT(a.title,' '),''), a.empname) AS 'empname' ,b.designation FROM employee a,employee_desig b WHERE a.isactive='1' AND a.DesigId=b.DesigId AND a.LabCode=:LabCode AND empid NOT IN (SELECT empid FROM pfms_initiation_req_members WHERE InitiationId =:InitiationId AND isactive = 1)ORDER BY a.srno=0,a.srno";
	private static final String EMPLISTS=" SELECT a.emp_id,CONCAT(IFNULL(CONCAT(a.title,' '),''), a.emp_name) AS 'empname' ,b.designation FROM employee a,employee_desig b WHERE a.is_active='1' AND a.desig_id=b.desig_id AND a.lab_code=:LabCode AND a.emp_id NOT IN (SELECT empid FROM pfms_initiation_req_members WHERE ReqInitiationId =:ReqInitiationId AND isactive = 1)ORDER BY a.sr_no=0,a.sr_no";

	@Override
	public List<Object[]> EmployeeList(String labCode, String reqInitiationId) throws Exception {
		Query query = manager.createNativeQuery(EMPLISTS);

		query.setParameter("LabCode", labCode);
		query.setParameter("ReqInitiationId", Long.parseLong(reqInitiationId));
		
		return (List<Object[]>)query.getResultList();
	}

	@Override
	public long AddreqMembers(RequirementMembers r) throws Exception {

		manager.persist(r);
		manager.flush();

		return r.getReqMemeberId();
	}

	//private static final String REQMEMLIST = " SELECT a.empid,CONCAT(IFNULL(CONCAT(a.title,' '),''), a.empname) AS 'empname' ,b.designation,a.labcode,b.desigid FROM employee a,employee_desig b,pfms_initiation_req_members c WHERE a.isactive='1' AND a.DesigId=b.DesigId AND  a.empid = c.empid AND c.initiationid =:initiationid AND c.isactive =1 ORDER BY b.desigid ASC";
	private static final String REQMEMLIST = "SELECT a.emp_id,CONCAT(IFNULL(CONCAT(a.title,' '),''), a.emp_name) AS 'empname' ,b.designation,a.lab_code,b.desig_id,c.ReqMemeberId FROM employee a,employee_desig b,pfms_initiation_req_members c WHERE a.is_active='1' AND a.desig_id=b.desig_id AND  a.emp_id = c.empid AND c.ReqInitiationId =:ReqInitiationId AND c.isactive =1 ORDER BY a.sr_no ASC";

	@Override
	public List<Object[]> reqMemberList(String reqInitiationId) throws Exception {

		Query query = manager.createNativeQuery(REQMEMLIST);

		query.setParameter("ReqInitiationId", Long.parseLong(reqInitiationId));
		return (List<Object[]>)query.getResultList();
	}
	@Override
	public long addReqSummary(RequirementSummary rs) throws Exception {
		manager.persist(rs);
		return rs.getSummaryId();
	}

	//private static final String DOCSUM="SELECT a.AdditionalInformation,a.Abstract,a.Keywords,a.Distribution,a.reviewer,a.approver,(SELECT CONCAT(IFNULL(CONCAT(e.title,' '),''), e.empname)FROM employee e WHERE e.empid=a.approver ) AS 'Approver1',(SELECT CONCAT(IFNULL(CONCAT(e.title,' '),''), e.empname)FROM employee e WHERE e.empid=a.reviewer) AS 'Reviewer1',a.summaryid FROM pfms_initiation_req_summary a WHERE a.InitiationId =:InitiationId AND a.isactive='1'";
	private static final String DOCSUM="SELECT a.AdditionalInformation,a.Abstract,a.Keywords,a.Distribution,a.reviewer,a.approver,(SELECT CONCAT(CONCAT(IFNULL(CONCAT(e.title,' '),''), e.emp_name),', ', d.designation)FROM employee e,employee_desig d WHERE  e.desig_id=d.desig_id AND e.emp_id=a.approver ) AS 'Approver1',(SELECT CONCAT(CONCAT(IFNULL(CONCAT(e.title,' '),''), e.emp_name),', ', d.designation)FROM employee e,employee_desig d WHERE  e.desig_id=d.desig_id AND e.emp_id=a.reviewer ) AS 'Reviewer1',\r\n"
			+ "a.summaryid,a.preparedby,(SELECT CONCAT(CONCAT(IFNULL(CONCAT(e.title,' '),''), e.emp_name),', ', d.designation)FROM employee e,employee_desig d WHERE  e.desig_id=d.desig_id AND e.emp_id=a.PreparedBy)\r\n"
			+ " AS 'PreparedBy1',a.ReleaseDate FROM pfms_initiation_req_summary a WHERE a.ReqInitiationId =:ReqInitiationId AND a.isactive='1'";
	@Override
	public List<Object[]> getDocumentSummary(String reqInitiationId) throws Exception {

		Query query = manager.createNativeQuery(DOCSUM);
		query.setParameter("ReqInitiationId", Long.parseLong(reqInitiationId));
		List<Object[]>DocumentSummary=(List<Object[]>)query.getResultList();

		return DocumentSummary;
	}


	private static final String DOCSUMUPD="UPDATE pfms_initiation_req_summary SET AdditionalInformation=:AdditionalInformation,"
			+ "Abstract=:Abstract,Keywords=:Keywords,Distribution=:Distribution , reviewer=:reviewer,approver=:approver,"
			+ "ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate,PreparedBy=:PreparedBy,ReleaseDate=:ReleaseDate "
			+ "WHERE SummaryId=:SummaryId AND isactive='1'";

	

	@Override
	public long editreqSummary(RequirementSummary rs) throws Exception {
		RequirementSummary ExistingRequirementSummary= manager.find(RequirementSummary.class, rs.getSummaryId());
		if(ExistingRequirementSummary!=null && ExistingRequirementSummary.getIsActive()==1) {
			ExistingRequirementSummary.setAdditionalInformation(rs.getAdditionalInformation());
			ExistingRequirementSummary.setAbstract(rs.getAbstract());
			ExistingRequirementSummary.setKeywords(rs.getKeywords());
			ExistingRequirementSummary.setDistribution(rs.getDistribution());
			ExistingRequirementSummary.setReviewer(rs.getReviewer());
			ExistingRequirementSummary.setApprover(rs.getApprover());
			ExistingRequirementSummary.setModifiedBy(rs.getModifiedBy());
			ExistingRequirementSummary.setModifiedDate(rs.getModifiedDate());
			ExistingRequirementSummary.setPreparedBy(rs.getPreparedBy());
			ExistingRequirementSummary.setReleaseDate(rs.getReleaseDate());
			return 1L;
		}
		else {
			return 0L;
		}
		
	}
	private static final String DOCTEMPATTRIBUTES="SELECT a.HeaderFontSize,a.HeaderFontWeight,a.SubHeaderFontsize, a.SubHeaderFontweight,a.ParaFontSize,a.ParaFontWeight,a.MainTableWidth, a.subTableWidth,a.AttributId,a.SuperHeaderFontsize,a.SuperHeaderFontWeight,a.FontFamily,a.RestrictionOnUse FROM  pfms_doc_template_attributes a";
	@Override
	public Object[] DocTempAttributes() throws Exception {

		try {
			Query query=manager.createNativeQuery(DOCTEMPATTRIBUTES);
			List<Object[]> list =(List<Object[]>)query.getResultList();	
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
	private static final String PARADETAILSMAIN="SELECT paraid,sqrid,ReqInitiationId,parano,paradetails,SINo FROM pfms_initiation_sqr_para WHERE ReqInitiationId=:ReqInitiationId AND isactive=1 ORDER BY SINo";
	@Override
	public List<Object[]> ReParaDetailsMain(String reqInitiationId) throws Exception {
		Query query = manager.createNativeQuery(PARADETAILSMAIN);
		query.setParameter("ReqInitiationId", Long.parseLong(reqInitiationId));
		List<Object[]>paraDetails=(List<Object[]>)query.getResultList();
		return paraDetails;
	}

	private static final String VERIFICATIONLISTMAIN="select VerificationId,Provisions,ReqInitiationId,ProvisionsDetails from pfms_initiation_verification WHERE ReqInitiationId=:ReqInitiationId AND isactive='1'";
	@Override
	public List<Object[]> getVerificationListMain(String reqInitiationId) throws Exception {
		Query query=manager.createNativeQuery(VERIFICATIONLISTMAIN);
		query.setParameter("ReqInitiationId", Long.parseLong(reqInitiationId));
		List<Object[]> verificationList=(List<Object[]>)query.getResultList();
		return verificationList;
	}

	private static final String VPDETAILS="SELECT VerificationId,ReqInitiationId,Provisions,ProvisionsDetails FROM pfms_initiation_verification WHERE ReqInitiationId=:ReqInitiationId AND IsActive=1";
	@Override
	public List<Object[]> VPDetails(String reqInitiationId) throws Exception {
		Query query = manager.createNativeQuery(VPDETAILS);
//		query.setParameter("initiationid", initiationid);
//		query.setParameter("ProjectId", ProjectId);
		query.setParameter("ReqInitiationId", Long.parseLong(reqInitiationId));
		List<Object[]>paraDetails=(List<Object[]>)query.getResultList();
		return paraDetails;
	}
	private static final String EMPLISTS1="SELECT a.emp_id,CONCAT(IFNULL(CONCAT(a.title,' '),''), a.emp_name) AS 'empname' ,b.designation FROM employee a,employee_desig b WHERE a.is_active='1' AND a.Desig_Id=b.Desig_Id AND a.Lab_Code=:LabCode AND a.emp_id NOT IN (SELECT empid FROM pfms_doc_members WHERE TestPlanInitiationId =:TestPlanInitiationId AND SpecsInitiationId=:SpecsInitiationId AND isactive = 1) ORDER BY a.sr_no=0,a.sr_no";
	@Override
	public List<Object[]> EmployeeList1(String labCode, String testPlanInitiationId,String SpecsInitiationId) throws Exception {
		Query query = manager.createNativeQuery(EMPLISTS1);

		query.setParameter("LabCode", labCode);
		query.setParameter("TestPlanInitiationId", Long.parseLong(testPlanInitiationId));
		query.setParameter("SpecsInitiationId", Long.parseLong(SpecsInitiationId));


		return (List<Object[]>)query.getResultList();
	}

	private static final String GETPROJECTDETAILS= "SELECT a.project_id,a.project_code,a.project_short_name,a.project_name,a.division_id,a.sanction_date,a.pdc,a.objective,a.scope,a.board_reference,a.total_sanction_cost,a.project_description,b.category AS securityclassification FROM project_master a,pfms_category b WHERE a.is_active=1 AND a.project_category=b.categoryid AND a.lab_code=:LabCode AND a.project_id=:ProjectId";
	private static final String GETPREPROJECTDETAILS= "SELECT a.InitiationId,a.ProjectShortName AS 'ProjectCode',a.ProjectShortName,a.ProjectTitle,a.DivisionId,'Sanction Date','PDC','Objective','Scope','BoardReference',ProjectCost,'ProjectDescription',b.classification AS securityclassification FROM pfms_initiation a,pfms_security_classification b WHERE a.IsActive=1 AND a.ClassificationId=b.classification_id AND a.LabCode=:LabCode AND a.InitiationId=:ProjectId";
	@Override
	public Object[] getProjectDetails(String labcode,String projectId,String projectType) throws Exception {
		try {

			Query query = manager.createNativeQuery(projectType.equalsIgnoreCase("E")?GETPROJECTDETAILS:GETPREPROJECTDETAILS);
			query.setParameter("LabCode", labcode);
			query.setParameter("ProjectId", Long.parseLong(projectId));
			return (Object[])query.getSingleResult();

		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getProjectDetails "+e);
			return null;
		}
	}
	
	
	private static final String PRODETAILSDATA="SELECT a.initiationid,d.empname,e.divisioncode,a.projectprogramme,b.projecttype AS category ,c.classification AS securityclassification,\r\n"
			+ "a.projectshortname,a.projecttitle,a.projectcost,a.projectduration,a.isplanned,a.ismultilab,a.deliverable,a.labcount,a.fecost,a.recost,a.projecttitle AS 'initiatedproject',a.ismain,a.pcduration,a.pcremarks,a.indicativecost ,a.projecttypeid,a.User\r\n"
			+ "FROM pfms_initiation a,project_type b,pfms_security_classification c,employee d,division_master e\r\n"
			+ "WHERE a.initiationid=:initiationId \r\n"
			+ "AND a.projecttypeid=b.projecttypeid \r\n"
			+ "AND a.classificationid=c.classificationid \r\n"
			+ "AND a.empid=d.empid \r\n"
			+ "AND a.divisionid=e.division_id \r\n"
			+ "AND a.isactive='1'";
	
	@Override
	public List<Object[]> ProjectDetailesData(long initiationId) throws Exception {
		Query query = manager.createNativeQuery(PRODETAILSDATA);
		query.setParameter("initiationId", initiationId);
		
		List<Object[]>result = (List<Object[]>)query.getResultList();
		
		return result;
	}

	private static final String INITIATIONREQLIST = "SELECT a.SpecsInitiationId,a.ProjectId,a.InitiationId,a.ProductTreeMainId,a.InitiatedBy,a.InitiatedDate,b.emp_name,c.Designation,a.SpecsVersion,d.ReqStatusCode,d.ReqStatus,d.ReqStatusColor FROM pfms_specifications_initiation a,employee b,employee_desig c,pfms_req_approval_status d WHERE a.IsActive=1 AND a.InitiatedBy=b.emp_id AND b.desig_id=c.desig_id AND a.ReqStatusCode=d.ReqStatusCode AND a.ProjectId=:ProjectId AND a.ProductTreeMainId=:ProductTreeMainId AND a.InitiationId=:InitiationId ORDER BY a.SpecsInitiationId DESC";
	@Override
	public List<Object[]> initiationSpecList(String projectId, String mainId, String initiationId)throws Exception
	{
		try {
			Query query=manager.createNativeQuery(INITIATIONREQLIST);
			query.setParameter("ProjectId", Long.parseLong(projectId));
			query.setParameter("ProductTreeMainId",  Long.parseLong(mainId));
			query.setParameter("InitiationId",  Long.parseLong(initiationId));
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}

	}
	
	@Override
	public long addSpecificationContents(SpecificationContent sc) throws Exception {
		manager.persist(sc);
		manager.flush();
		return sc.getContentId();
	}
	
	
	private static final String SEPCCONTENTDETAILS=" SELECT ContentId,PointName,PointDetails FROM pfms_specifications_contents WHERE SpecsInitiationId=:SpecsInitiationId";
	@Override
	public List<Object[]> SpecContentsDetails(String specsInitiationId) throws Exception {
		Query query = manager.createNativeQuery(SEPCCONTENTDETAILS);
		query.setParameter("SpecsInitiationId", Long.parseLong(specsInitiationId));
		List<Object[]>SpecContentsDetails= new ArrayList<>();
		SpecContentsDetails= (List<Object[]>)query.getResultList();
		return SpecContentsDetails;
	}
	
	@Override
	public SpecificationContent getSpecificationContent(String contentid) throws Exception {
		try {

			return manager.find(SpecificationContent.class, Long.parseLong(contentid));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside DAO SpecificationContent " + e);
			return null;
		}
	}
	
	@Override
	public long addSpecification(Specification specs) throws Exception {
		manager.persist(specs);
		manager.flush();
		return  specs.getSpecsId();
	}
	
	@Override
	public Specification getSpecificationData(String specsId) throws Exception {
		try {

			return manager.find(Specification.class, Long.parseLong(specsId));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside DAO getSpecificationData " + e);
			return null;
		}
	}
	
	@Override
	public long addSpecificationIntro(SpecificationIntro s) throws Exception {
		manager.persist(s);
		manager.flush();
		return s.getIntroductionId();
	}
	private static final String SPECINTROLIST="SELECT IntroductionId,IntroName,IntroContent FROM pfms_specification_intro WHERE SpecsInitiationId=:SpecsInitiationId AND isactive='1'";
	@Override
	public List<Object[]> getSpecsIntro(String SpecsInitiationId) throws Exception {
		Query query = manager.createNativeQuery(SPECINTROLIST);
		query.setParameter("SpecsInitiationId", Long.parseLong(SpecsInitiationId));
		return (List<Object[]>)query.getResultList();
	}
	
	private static final String EDITSPECINTRO="UPDATE pfms_specification_intro SET IntroContent=:IntroContent ,"
			+ " ModifiedBy=:ModifiedBy , ModifiedDate=:ModifiedDate WHERE IntroductionId=:IntroductionId";
	@Override
	public long editSpecificationIntro(SpecificationIntro s) throws Exception {
		SpecificationIntro ExistingSpecificationIntro= manager.find(SpecificationIntro.class, s.getIntroductionId());
		if( ExistingSpecificationIntro != null) {
			ExistingSpecificationIntro.setIntroContent(s.getIntroContent());
			ExistingSpecificationIntro.setModifiedBy(s.getModifiedBy());
			ExistingSpecificationIntro.setModifiedDate(s.getModifiedDate());
			return 1L;
			}
		else {
			return 0L;
		}
	
	}
	
	@Override
	public long uploadProductTree(SpecifcationProductTree s) throws Exception {
		String sql = "UPDATE pfms_specification_producttree SET Isactive='0' WHERE SpecificationId=:SpecificationId";
		
		Query query = manager.createNativeQuery(sql);
		query.setParameter("SpecificationId", s.getSpecificationId());
		query.executeUpdate();
		
		manager.persist(s);
		manager.flush();
		return s.getProjectDataId();
	}
	private static final String PRODUCTTREE="SELECT a.ProjectDataId,a.FilesPath,a.comment,a.imageName FROM pfms_specification_producttree a WHERE a.SpecificationId=:SpecificationId AND isactive='1'";
	
	@Override
	public List<Object[]> SpecProducTreeDetails(String specsInitiationId) throws Exception {
		Query query = manager.createNativeQuery(PRODUCTTREE);
		query.setParameter("SpecificationId",  Long.parseLong(specsInitiationId));
		
		return (List<Object[]>)query.getResultList();
	}
	
	@Override
	public PfmsInitiation getPfmsInitiationById(String initiationId) throws Exception {
		try {
			return manager.find(PfmsInitiation.class, Long.parseLong(initiationId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public long ProjectScheduleEdit(PfmsInitiationSchedule pfmsinitiationschedule) throws Exception 
	{
		manager.merge(pfmsinitiationschedule);
		manager.flush();
		return pfmsinitiationschedule.getInitiationScheduleId();
	}
	
	private static final String PARAS="SELECT paraid,sqrid,ReqInitiationId,parano,paradetails FROM pfms_initiation_sqr_para WHERE Paraid=:paraid AND isactive=1 ";
	@Override
	public Object[] getParaDetails(String paraid) throws Exception {
		
		Query query = manager.createNativeQuery(PARAS);
		
		query.setParameter("paraid", Long.parseLong(paraid));
		
		return (Object[])query.getSingleResult();
	}
	@Override
	public long addInitiationMilestone(PfmsInitiationMilestone ms) throws Exception {
		try {
			manager.persist(ms);
			manager.flush();
		} catch (Exception e) {
		   e.printStackTrace();
		}
		return ms.getInitiationMilestoneId();
	}
	
	private static final String GETMILESTONEDATA="SELECT InitiationMilestoneId,InitiationId,PDRProbableDate,PDRActualDate,TIECProbableDate,TIECActualDate,CECProbableDate,CECActualDate,CCMProbableDate,CCMActualDate,DMCProbableDate,DMCActualDate,SanctionProbableDate,SanctionActualDate,SetBaseline,Revision FROM pfms_initiation_ms WHERE InitiationId=:initiationid AND IsActive='1'";
	@Override
	public List<Object[]> getInitiatedMilestoneDetails(String initiationid) throws Exception {
		try {
			
			Query query =  manager.createNativeQuery(GETMILESTONEDATA);
			query.setParameter("initiationid", Long.parseLong(initiationid));
			return (List<Object[]>)query.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}
	
	
	@Override
	public PfmsInitiationMilestone getInitiationMilestone(long initiationMilestoneId) throws Exception {
	     return manager.find(PfmsInitiationMilestone.class, initiationMilestoneId);
	}
	
	@Override
	public long editInitiationMilestone(PfmsInitiationMilestone ms) throws Exception {
	    try {
			manager.merge(ms);
			manager.flush();
		} catch (Exception e) {
		   e.printStackTrace();
		}
		return ms.getInitiationMilestoneId();  
	}
	
	@Override
	public long addInitiationMilestoneRev(PfmsInitiationMilestoneRev entityRev) throws Exception {
		try {
				manager.persist(entityRev);
				manager.flush();
			} catch (Exception e) {
			   e.printStackTrace();
			}
			return entityRev.getInitiationMileRevId();  
		}
	
	@Override
	public DocMembers  getDocMemberById(Long MemeberId) throws Exception
	{
		DocMembers dec = null;
		try {
			dec= manager.find(DocMembers.class, MemeberId);
		} catch (Exception e) {
			logger.error(new Date() + "Inside DAO  getDocMemberById "+e);
			e.printStackTrace();
		}
		return dec;
	}
	
	@Override
	public long editDocMember(DocMembers idm) throws Exception
	{
		try {
		    manager.merge(idm);
		    manager.flush();
			return idm.getMemeberId();
		}
		catch (Exception e) {
			logger.error(new Date()  + "Inside DAO editDocMember " + e);
			e.printStackTrace();
			return 0 ;
		}
	}
	
	String sql = "UPDATE pfms_initiation_req_members SET Isactive='0' WHERE ReqMemeberId=:reqMemberId";
	@Override
	public long UpdateInitiationReqMembers(long reqMemberId) throws Exception {
		
		RequirementMembers ExistingRequirementMembers = manager.find(RequirementMembers.class, reqMemberId);
		if(ExistingRequirementMembers != null) {
			ExistingRequirementMembers.setIsActive(0);
			return 1L;
		}
		else {
			return 0L;
		}
		
		
	}
	
	
	private static final String SYSSPECIFICLIST="SELECT a.SpecsMasterId , a.SpecificationName,a.Description,a.SpecsParameter,\r\n"
			+ "a.SpecsUnit,a.SpecsInitiationId,a.SpecValue,'EmpName' AS 'EMP',a.CreatedDate, \r\n"
			+ "a.ModifiedBy, a.ModifiedDate, a.IsActive,a.sid,a.mainid,a.ParentId,\r\n"
			+ "a.maximumValue,a.minimumValue,a.specCount,c.SpecTypeCode, a.SpecTypeId, c.SpecType \r\n"
			+ "FROM pfms_specification_master a, pfms_product_tree b, pfms_spec_types c \r\n"
			+ "WHERE a.mainid=b.SystemMainId AND b.MainId=:productTreeMainId AND C.SpecTypeId = a.SpecTypeId AND a.isactive=1 ORDER BY a.MainId,a.specCount";
	@Override
	public List<Object[]> getsystemSpecificationList(String productTreeMainId) throws Exception {

		Query query = manager.createNativeQuery(SYSSPECIFICLIST);
		query.setParameter("productTreeMainId", productTreeMainId);
		return (List<Object[]>)query.getResultList();
	}
	
	private static final String PLATFORMLIST="select PlatformId,PlatformName from pfms_platform_master where isactive='1' ORDER BY PlatformId"; 
	@Override
	public List<Object[]> PlatformList() {
		try {
			Query query=manager.createNativeQuery(PLATFORMLIST);
			return (List<Object[]>)query.getResultList();	
		}catch (Exception e) {
			e.printStackTrace();	
			return new ArrayList<>();
		}
		
	}
	
	private static final String PROJECTORGINIALDATA="SELECT a.project_id,a.project_name,a.project_code,a.unit_code,a.project_short_name,a.end_user,a.proj_sanc_authority,a.board_reference,e.title,e.salutation,e.emp_name,d.designation,a.sanction_no,a.sanction_date\r\n"
			+ ",a.pdc,a.lab_participating,a.total_sanction_cost,a.sanction_cost_re,a.sanction_cost_fe,a.application,c.classification,pt.project_type ,a.scope,a.objective,a.deliverable,a.project_main_id,pm.project_code AS id,a.project_description\r\n"
			+ "FROM project_master a\r\n"
			+ "LEFT JOIN employee e ON a.project_director = e.emp_id \r\n"
			+ "LEFT JOIN employee_desig d ON e.desig_id=d.desig_id \r\n"
			+ "LEFT JOIN pfms_security_classification  c ON a.project_category=c.classification_id \r\n"
			+ "LEFT JOIN project_type pt ON a.project_type_id=pt.project_type_id\r\n"
			+ "LEFT JOIN project_main pm ON pm.project_main_id=a.project_main_id\r\n"
			+ "WHERE a.project_id=:proid";

	@Override
	public Object[] ProjectOriginalData(String ProjectId) throws Exception {
		try {
			Query query = manager.createNativeQuery(PROJECTORGINIALDATA);
			query.setParameter("proid", Long.parseLong(ProjectId));
			return (Object[]) query.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public List<Object[]> ProjectReviseList(String ProjectId) throws Exception {
		try {
			Query query=manager.createNativeQuery("SELECT p.project_description,p.unit_code,p. project_code,p.board_reference,e.emp_name,e.title,e.salutation,d.designation ,p.project_director,p.proj_sanc_authority ,p.sanction_no,p.sanction_date,p.pdc, p.total_sanction_cost,p.sanction_cost_fe ,p.sanction_cost_re, p.lab_participating,p.application,p.project_category,p.scope,p.objective,p.deliverable,p.remarks,c.category,pt.project_type from  project_master_rev p LEFT join employee e on p.project_director = e.emp_id LEFT Join employee_desig d on e.desig_id=d.desig_id Left join pfms_category  c on p.project_category=c.categoryid Left join project_type pt on p.project_type=pt.project_type_id\r\n"
					+ "where project_id=:prjId");
			query.setParameter("prjId", Long.parseLong(ProjectId));
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
		
	}
	
	@Override
	public PlatformMaster getPlatformByPlatformId(long platformId) throws Exception {
		 return manager.find(PlatformMaster.class, platformId);
	}

	
	
	//29-04-2025
	@Override
	public PfmsInitiationApproval getPfmsInitiationApprovalById(String enoteId) throws Exception {
		
		return manager.find(PfmsInitiationApproval.class, Long.parseLong(enoteId));
	}
	
	@Override
	public long savePfmsInitiationApproval(PfmsInitiationApproval pe) throws Exception {
		
		manager.persist(pe);
		manager.flush();
		return pe.getEnoteId();
		
	}
	
	private final String INIAPPROVALDATA= "SELECT a.EnoteId , a.RefNo , a.RefDate , a.Subject , a.Comment ,\r\n"
			+ " a.InitiationId  ,a.Recommend1,a.Rec1_Role,a.Recommend2,a.Rec2_Role ,\r\n"
			+ "  a.Recommend3, a.Rec3_Role , a.ApprovingOfficer,a.Approving_Role,a.EnoteStatusCode,a.EnoteStatusCodeNext,\r\n"
			+ "  a.InitiatedBy,e.emp_name , d.designation , ds.EnoteStatus,ds.EnoteStatusColor,a.ApprovingOfficerLabCode \r\n"
			+ "  FROM pfms_initiation_approval a , employee e , employee_desig d , dak_enote_status ds WHERE   a.EnoteStatusCode=ds.EnoteStatusCode\r\n"
			+ " AND a.InitiationId=:InitiationId AND a.InitiatedBy = e.emp_id AND e.desig_id = d.desig_id AND a.isactive ='1'";
	
	@Override
	public Object[] InitiationApprovalData(String InitiationId) throws Exception {
		try {	
		Query query = manager.createNativeQuery(INIAPPROVALDATA);
		
		query.setParameter("InitiationId", InitiationId);
		return (Object[])query.getSingleResult();
		}
		catch (Exception e) {

			return null;
		}
	}
	
	
	@Override
	public long savePmsInitiationApprovalTransaction(PmsInitiationApprovalTransaction pt) throws Exception {
	
		manager.persist(pt);
		manager.flush();
		
		return pt.getEnoteTransId();
				
	}
	private static final String ENOTEPENDINGLIST="CALL Pms_initiationApproval_PendingList(:EmpId)";
	@Override
	public List<Object[]> initiationPendingList(long empId) throws Exception {
		logger.info(LocalDate.now() + "Inside initiationPendingList");
		try {
			Query query = manager.createNativeQuery(ENOTEPENDINGLIST);
		
			query.setParameter("EmpId", empId);
	
			 List<Object[]> eNotePendingList = (List<Object[]>) query.getResultList();
				return eNotePendingList;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LocalDate.now() + "Inside DaoImpl initiationPendingList", e);
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
			+ " FROM pfms_initiation_approval p WHERE p.EnoteId=:EnoteId";
	@Override
	public Object[] NewApprovalList(String enoteId) throws Exception {
		Query query = manager.createNativeQuery(NEWAPPROVALLIST);
		query.setParameter("EnoteId", enoteId);
				Object[]NewApprovalList = null;
		try {
			NewApprovalList=(Object[])query.getSingleResult();
			return NewApprovalList;
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		return null;
	}
	
	
	private static final String ENOTETRANSACTIONLIST="SELECT tra.EnoteTransId,emp.emp_id,emp.emp_name,des.designation,tra.ActionDate,tra.Remarks,sta.EnoteStatus,sta.EnoteStatusColor FROM pfms_initiation_approval_transaction tra,dak_enote_status sta,employee emp,employee_desig des,pfms_initiation_approval e WHERE e.EnoteId = tra.EnoteId AND tra.EnoteStatusCode = sta.EnoteStatusCode AND tra.ActionBy=emp.emp_id AND emp.desig_id = des.desig_id AND e.EnoteId=:enoteTrackId ORDER BY tra.ActionDate";
	@Override
	public List<Object[]> EnoteTransactionList(String enoteTrackId) throws Exception {
		logger.info(LocalDate.now() + "Inside EnoteTransactionList");
		try {
			Query query = manager.createNativeQuery(ENOTETRANSACTIONLIST);
			query.setParameter("enoteTrackId", enoteTrackId);
			 List<Object[]> EnoteTransactionList = (List<Object[]>) query.getResultList();
				return EnoteTransactionList;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LocalDate.now() + "Inside DaoImpl EnoteTransactionList", e);
			return null;
		}
	}
	
	private static final String APPRVLIST= "SELECT MAX(a.EnoteId) AS EnoteId,MAX(a.RefNo) AS RefNo,MAX(a.RefDate) AS RefDate,MAX(a.Subject) AS SUBJECT,MAX(a.Comment) AS COMMENT,MAX(a.InitiatedBy) AS InitiatedBy,MAX(c.ActionDate) AS ActionDate,\r\n"
			+ "					MAX(d.EnoteStatus) AS EnoteStatus,MAX(d.EnoteStatusColor) AS EnoteStatusColor,MAX(d.EnoteStatusCode) AS EnoteStatusCode,\r\n"
			+ "					MAX(p.projectShortName) AS ProjectShortName , MAX(CONCAT(IFNULL(CONCAT(b.title,' '),''), b.emp_name)) AS empname, MAX(ed.designation) AS designation,MAX(p.projecttitle) AS projecttitle,MAX(a.initiationid)\r\n"
			+ "					FROM pfms_initiation_approval a,employee b,pfms_initiation_approval_transaction c, pfms_initiation p ,dak_enote_status d , employee_desig ed\r\n"
			+ "					WHERE a.InitiatedBy=b.emp_id AND b.desig_id=ed.desig_id\r\n"
			+ "					AND a.EnoteStatusCode=d.EnoteStatusCode AND a.EnoteId=c.EnoteId AND a.InitiationId = p.InitiationId \r\n"
			+ "					AND c.EnoteStatusCode IN ('RC1','RC2','RC3','RC4','RC5','EXT','APR') AND c.ActionBy=:empId AND DATE(a.CreatedDate) \r\n"
			+ "					BETWEEN :fromDate AND :toDate  GROUP BY a.EnoteId";
			
	@Override
	public List<Object[]> initiationApprovalList(long empId, String fromDate, String toDate) throws Exception {

		Query query = manager.createNativeQuery(APPRVLIST);
		
		query.setParameter("empId", empId);
		query.setParameter("fromDate", fromDate);
		query.setParameter("toDate", toDate);
		return (List<Object[]>)query.getResultList();
	}
	
	
	private static final String PRINTDETAILS="SELECT tra.EnoteTransId,(SELECT empId FROM pfms_initiation_approval_transaction t ,\r\n"
			+ "			 employee e  WHERE e.empid = t.Actionby AND t.EnoteStatusCode =  sta.EnoteStatusCode AND\r\n"
			+ "			 t.EnoteId=par.EnoteId ORDER BY t.EnoteTransId DESC LIMIT 1) AS 'empid',\r\n"
			+ "			 (SELECT empname FROM pfms_initiation_approval_transaction t , employee e  WHERE e.empid = t.Actionby AND t.EnoteStatusCode =  sta.EnoteStatusCode AND t.EnoteId=par.EnoteId ORDER BY t.EnoteTransId DESC LIMIT 1) AS 'empname',\r\n"
			+ "			 (SELECT designation FROM pfms_initiation_approval_transaction t ,employee e,employee_desig des WHERE e.empid = t.Actionby AND e.desigid=des.desigid AND\r\n"
			+ "			  t.EnoteStatusCode =  sta.EnoteStatusCode AND t.EnoteId=par.EnoteId ORDER BY t.EnoteTransId DESC LIMIT 1) AS 'Designation', MAX(tra.ActionDate) AS ActionDate,(SELECT t.Remarks FROM pfms_initiation_approval_transaction t ,\r\n"
			+ "			 employee e  WHERE e.empid = t.Actionby AND t.EnoteStatusCode =  sta.EnoteStatusCode AND t.EnoteId=par.EnoteId ORDER BY t.EnoteTransId DESC LIMIT 1) AS 'Remarks',\r\n"
			+ "			 sta.EnoteStatus,sta.EnoteStatusColor,sta.EnoteStatusCode,par.InitiatedBy FROM \r\n"
			+ "			 pfms_initiation_approval_transaction tra,dak_enote_status sta,employee emp,pfms_initiation_approval par WHERE par.EnoteId=tra.EnoteId\r\n"
			+ "			 AND tra.EnoteStatusCode =sta.EnoteStatusCode AND sta.EnoteStatusCode IN ('FWD','RFD','RC1','RC2','RC3','RC4','RC5','APR','RR1','RR2','RR3','RAP') \r\n"
			+ "			 AND tra.Actionby=emp.EmpId AND par.EnoteId=:enoteid  GROUP BY tra.EnoteStatusCode,tra.EnoteTransId,sta.EnoteStatus,sta.EnoteStatusColor ORDER BY ActionDate ASC";
	
	@Override
	public List<Object[]> InitiationAprrovalPrintDetails(long enoteid) throws Exception {
		// TODO Auto-generated method stub
		Query query = manager.createNativeQuery(PRINTDETAILS);
		query.setParameter("enoteid", enoteid);
		return (List<Object[]>)query.getResultList();
	}

	private static final String PROJECTTEAMLISTBYLABCODE="SELECT a.emp_id, CONCAT(IFNULL(CONCAT(a.title,' '),(IFNULL(CONCAT(a.salutation, ' '), ''))), a.emp_name) AS 'EmpName', b.Designation FROM employee a LEFT JOIN employee_desig b ON a.desig_id=b.desig_id WHERE a.is_active=1 AND a.lab_code=:LabCode AND a.emp_id NOT IN (SELECT c.EmpId FROM project_employee c WHERE c.ProjectId=:ProjectId AND c.IsActive=1) AND a.emp_status NOT IN ('N') ORDER BY a.sr_no=0,a.sr_no"; 
	@Override
	public List<Object[]> getProjectTeamListByLabCode(String labCode, String projectId) {
		try {
			Query query = manager.createNativeQuery(PROJECTTEAMLISTBYLABCODE);
			query.setParameter("LabCode", labCode);
			query.setParameter("ProjectId", projectId);
			return (List<Object[]>)query.getResultList();	
		}catch (Exception e) {
			e.printStackTrace();	
			return new ArrayList<>();
		}
	}
	
	@Override
	public List<RoleMaster> getRoleMasterList() throws Exception {
		try {
			Query query = manager.createQuery("FROM RoleMaster");
			return (List<RoleMaster>)query.getResultList();	
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<RoleMaster>();
		}
	}
	
	@Override
	public ProjectAssign getProjectAssignById(String projectEmployeeId) throws Exception{
		try {
			return manager.find(ProjectAssign.class, Long.parseLong(projectEmployeeId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static final String GETPROJECTEMPLOYEEIDBYPROJECTID = "SELECT COALESCE((SELECT ProjectEmployeeId FROM project_employee WHERE ProjectId =:ProjectId AND RoleMasterId =:RoleMasterId LIMIT 1), 0) AS ProjectEmployeeId";
	@Override
	public Long getProjectEmployeeIdByProjectId(String projectId, String roleMasterId) throws Exception {
		try {
			Query query = manager.createNativeQuery(GETPROJECTEMPLOYEEIDBYPROJECTID);
			query.setParameter("ProjectId", projectId);
			query.setParameter("RoleMasterId", roleMasterId);
			return (Long)query.getSingleResult();	
		}catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	}
}

