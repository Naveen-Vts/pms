package com.vts.pfms.project.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.vts.pfms.FormatConverter;
import com.vts.pfms.admin.dao.AdminDao;
import com.vts.pfms.cars.dao.CARSDao;
import com.vts.pfms.committee.model.PfmsNotification;
import com.vts.pfms.master.model.RoleMaster;
import com.vts.pfms.print.model.ProjectTechnicalWorkData;
import com.vts.pfms.project.dao.ProjectDao;
import com.vts.pfms.project.dto.PfmsInitiationAttachmentDto;
import com.vts.pfms.project.dto.PfmsInitiationAttachmentFileDto;
import com.vts.pfms.project.dto.PfmsInitiationAuthorityDto;
import com.vts.pfms.project.dto.PfmsInitiationAuthorityFileDto;
import com.vts.pfms.project.dto.PfmsInitiationCostDto;
import com.vts.pfms.project.dto.PfmsInitiationDetailDto;
import com.vts.pfms.project.dto.PfmsInitiationDto;
import com.vts.pfms.project.dto.PfmsInitiationRequirementDto;
import com.vts.pfms.project.dto.PfmsProjectDataDto;
import com.vts.pfms.project.dto.PfmsRiskDto;
import com.vts.pfms.project.dto.PreprojectFileDto;
import com.vts.pfms.project.dto.ProjectAssignDto;
import com.vts.pfms.project.dto.ProjectMajorCarsDto;
import com.vts.pfms.project.dto.ProjectMajorConsultancyDto;
import com.vts.pfms.project.dto.ProjectMajorManPowersDto;
import com.vts.pfms.project.dto.ProjectMajorRequirementsDto;
import com.vts.pfms.project.dto.ProjectMajorWorkPackagesDto;
import com.vts.pfms.project.dto.ProjectMasterAttachDto;
import com.vts.pfms.project.dto.ProjectOtherReqDto;
import com.vts.pfms.project.dto.ProjectScheduleDto;
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
import com.vts.pfms.requirements.dao.RequirementDao;
import com.vts.pfms.requirements.model.DocMembers;
import com.vts.pfms.requirements.model.SpecifcationProductTree;
import com.vts.pfms.requirements.model.Specification;
import com.vts.pfms.requirements.model.SpecificationContent;
import com.vts.pfms.requirements.model.SpecificationIntro;

@Service
public class ProjectServiceImpl implements ProjectService {

	@Value("${ApplicationFilesDrive}")
	String uploadpath;

	private static final Logger logger = LogManager.getLogger(ProjectServiceImpl.class);

	FormatConverter fc = new FormatConverter();
	private SimpleDateFormat sdf1 = fc.getSqlDateAndTimeFormat(); /* new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); */

	private SimpleDateFormat sdf = fc.getRegularDateFormat(); /* new SimpleDateFormat("dd-MM-yyyy"); */
	private SimpleDateFormat sdf2 = fc.getSqlDateFormat();
	private int year = Calendar.getInstance().get(Calendar.YEAR);
	private int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
	DecimalFormat df = new DecimalFormat("0.00");

	@Autowired
	ProjectDao dao;
	
	@Autowired
	RequirementDao reqDao;

	
	@Autowired AdminDao admindao;
	@Autowired CARSDao carsdao;
	
	@Override
	public List<Object[]> ProjectIntiationList(String Empid, String LoginType, String LabCode) throws Exception {

		return dao.ProjectIntiationList(Empid, LoginType, LabCode);
	}

	@Override
	public List<Object[]> ProjectTypeList() throws Exception {

		return dao.ProjectTypeList();
	}

	@Override
	public Long ProjectMainAdd(ProjectMain proType) throws Exception {

		return dao.ProjectMainAdd(proType);
	}

	@Override
	public List<Object[]> OfficerList() throws Exception {

		return dao.OfficerList();
	}

	@Override
	public Long ProjectMainEdit(ProjectMain proType) throws Exception {

		return dao.ProjectMainEdit(proType);
	}

	@Override
	public Object[] ProjectMainEditData(String ProjectMainId) throws Exception {

		return dao.ProjectMainEditData(ProjectMainId);
	}

	@Override
	public Long ProjectMainClose(ProjectMain proType) throws Exception {

		return dao.ProjectMainClose(proType);
	}

	@Override
	public List<Object[]> PfmsCategoryList() throws Exception {

		return dao.PfmsCategoryList();
	}

	@Override
	public List<Object[]> PfmsDeliverableList() throws Exception {

		return dao.PfmsDeliverableList();
	}

	@Override
	public List<Object[]> LabList(String IntiationId) throws Exception {

		return dao.LabList(IntiationId);
	}

	@Override
	public Long ProjectIntiationAdd(PfmsInitiationDto pfmsinitiationdto, String UserId, String EmpId, String EmpName)
			throws Exception {

		logger.info(new Date() + "Inside SERVICE ProjectIntiationAdd ");
		PfmsInitiation pfmsinitiation = new PfmsInitiation();
		PfmsNotification notification = new PfmsNotification();
		pfmsinitiation.setLabCode(pfmsinitiationdto.getLabCode());
		pfmsinitiation.setEmpId(Long.parseLong(pfmsinitiationdto.getEmpId()));
		pfmsinitiation.setDivisionId(Long.parseLong(pfmsinitiationdto.getDivisionId()));
		pfmsinitiation.setProjectProgramme(pfmsinitiationdto.getProjectProgramme());
		pfmsinitiation.setProjectTypeId(Long.parseLong(pfmsinitiationdto.getProjectTypeId()));
		//pfmsinitiation.setCategoryId(Long.parseLong(pfmsinitiationdto.getCategoryId()));
		pfmsinitiation.setClassificationId(Long.parseLong(pfmsinitiationdto.getClassificationId()));
		pfmsinitiation.setProjectShortName(pfmsinitiationdto.getProjectShortName());
		pfmsinitiation.setProjectTitle(pfmsinitiationdto.getProjectTitle());
		pfmsinitiation.setFeCost(Double.parseDouble(pfmsinitiationdto.getFeCost()) * 100000);
		pfmsinitiation.setReCost(Double.parseDouble(pfmsinitiationdto.getReCost()) * 100000);
		pfmsinitiation.setProjectCost(Double.parseDouble(pfmsinitiationdto.getFeCost()) * 100000
				+ Double.parseDouble(pfmsinitiationdto.getReCost()) * 100000);
		/*
		 * pfmsinitiation.setProjectDuration(Integer.parseInt(pfmsinitiationdto.
		 * getProjectDuration()));
		 */
		pfmsinitiation.setIsPlanned(pfmsinitiationdto.getIsPlanned());
		// pfmsinitiation.setIsMultiLab(pfmsinitiationdto.getIsMultiLab());
		 pfmsinitiation.setDeliverable(pfmsinitiationdto.getDeliverableId());

		if (pfmsinitiationdto.getIsMultiLab() != null && pfmsinitiationdto.getIsMultiLab().equals("Y")) {
			pfmsinitiation.setLabCount(0);
		}
		pfmsinitiation.setProjectStatus("PIN");
		pfmsinitiation.setCreatedBy(UserId);
		pfmsinitiation.setCreatedDate(sdf1.format(new Date()));
		pfmsinitiation.setIsActive(1);
		pfmsinitiation.setIsMain(pfmsinitiationdto.getIsMain());
		pfmsinitiation.setMainId(Long.parseLong(pfmsinitiationdto.getMainId()));
		pfmsinitiation.setNodalLab(Long.parseLong(pfmsinitiationdto.getNodalLab()));
		pfmsinitiation.setRemarks(pfmsinitiationdto.getRemarks());
		pfmsinitiation.setIndicativeCost(Double.parseDouble(pfmsinitiationdto.getIndicativeCost()));
		pfmsinitiation.setPCRemarks(pfmsinitiationdto.getPCRemarks());
		pfmsinitiation.setPCDuration(Long.parseLong(pfmsinitiationdto.getDuration()));
		pfmsinitiation.setStartDate(pfmsinitiationdto.getStartDate()!=null?fc.RegularToSqlDate(pfmsinitiationdto.getStartDate()):null);
		
		notification.setEmpId(Long.parseLong(pfmsinitiationdto.getEmpId()));
		notification.setNotificationby(Long.parseLong(EmpId));
		notification.setNotificationDate(sdf1.format(new Date()));
		notification.setNotificationMessage("Project Initiation Assigned By " + EmpName);
		notification.setNotificationUrl("ProjectIntiationList.htm");
		notification.setCreatedBy(UserId);
		notification.setCreatedDate(sdf1.format(new Date()));
		notification.setIsActive(1);
		notification.setScheduleId(Long.parseLong("0"));
		notification.setStatus("PST");

		return dao.ProjectIntiationAdd(pfmsinitiation, notification);
	}

	@Override
	public Long ProjectShortNameCount(String ProjectShortName) throws Exception {

		return dao.ProjectShortNameCount(ProjectShortName);
	}

	@Override
	public List<Object[]> ProjectDetailes(Long IntiationId) throws Exception {

		return dao.ProjectDetailes(IntiationId);
	}

	@Override
	public List<Object[]> ProjectDetailsPreview(Long IntiationId) throws Exception {

		return dao.ProjectDetailsPreview(IntiationId);
	}

//	@Override
//	public Long ProjectIntiationAdd(PfmsInitiationDetailDto pfmsinitiationdetaildto, String UserId) throws Exception {
//		
//		logger.info(new Date() +"Inside SERVICE ProjectIntiationAdd ");
//		PfmsInitiationDetail pfmsinitiationdetail=new PfmsInitiationDetail();
//		
//		pfmsinitiationdetail.setInitiationId(Long.parseLong(pfmsinitiationdetaildto.getInitiationId()));
//		pfmsinitiationdetail.setRequirements(pfmsinitiationdetaildto.getRequirements());
//		pfmsinitiationdetail.setObjective(pfmsinitiationdetaildto.getObjective());
//		pfmsinitiationdetail.setScope(pfmsinitiationdetaildto.getScope());
//		pfmsinitiationdetail.setMultiLabWorkShare(pfmsinitiationdetaildto.getMultiLabWorkShare());
//		pfmsinitiationdetail.setEarlierWork(pfmsinitiationdetaildto.getEarlierWork());
//		pfmsinitiationdetail.setCompentencyEstablished(pfmsinitiationdetaildto.getCompentencyEstablished());
//		pfmsinitiationdetail.setNeedOfProject(pfmsinitiationdetaildto.getNeedOfProject());
//		pfmsinitiationdetail.setTechnologyChallanges(pfmsinitiationdetaildto.getTechnologyChallanges());
//		pfmsinitiationdetail.setRiskMitiagation(pfmsinitiationdetaildto.getRiskMitiagation());
//		pfmsinitiationdetail.setProposal(pfmsinitiationdetaildto.getProposal());
//		pfmsinitiationdetail.setRealizationPlan(pfmsinitiationdetaildto.getRealizationPlan());
//		pfmsinitiationdetail.setCreatedBy(UserId);
//		pfmsinitiationdetail.setCreatedDate(sdf1.format(new Date()));
//		pfmsinitiationdetail.setIsActive(1);
//		
//		
//		
//		return dao.ProjectIntiationDetailAdd(pfmsinitiationdetail);
//	}
	@Override
	public Long ProjectIntiationAdd(PfmsInitiationDetailDto pfmsinitiationdetaildto, String UserId) throws Exception {

		logger.info(new Date() + "Inside SERVICE ProjectIntiationAdd ");
		PfmsInitiationDetail pfmsinitiationdetail = new PfmsInitiationDetail();

		pfmsinitiationdetail.setInitiationId(Long.parseLong(pfmsinitiationdetaildto.getInitiationId()));
		pfmsinitiationdetail.setRequirements(pfmsinitiationdetaildto.getRequirements());
		pfmsinitiationdetail.setObjective(pfmsinitiationdetaildto.getObjective());
		pfmsinitiationdetail.setScope(pfmsinitiationdetaildto.getScope());
		pfmsinitiationdetail.setMultiLabWorkShare(pfmsinitiationdetaildto.getMultiLabWorkShare());
		pfmsinitiationdetail.setEarlierWork(pfmsinitiationdetaildto.getEarlierWork());
		pfmsinitiationdetail.setCompentencyEstablished(pfmsinitiationdetaildto.getCompentencyEstablished());
		pfmsinitiationdetail.setNeedOfProject(pfmsinitiationdetaildto.getNeedOfProject());
		pfmsinitiationdetail.setTechnologyChallanges(pfmsinitiationdetaildto.getTechnologyChallanges());
		pfmsinitiationdetail.setRiskMitigation(pfmsinitiationdetaildto.getRiskMitigation());
		pfmsinitiationdetail.setProposal(pfmsinitiationdetaildto.getProposal());
		pfmsinitiationdetail.setRealizationPlan(pfmsinitiationdetaildto.getRealizationPlan());
		pfmsinitiationdetail.setReqBrief(pfmsinitiationdetaildto.getReqBrief());
		pfmsinitiationdetail.setObjBrief(pfmsinitiationdetaildto.getObjBrief());
		pfmsinitiationdetail.setScopeBrief(pfmsinitiationdetaildto.getScopeBrief());
		pfmsinitiationdetail.setMultiLabBrief(pfmsinitiationdetaildto.getMultiLabBrief());
		pfmsinitiationdetail.setEarlierWorkBrief(pfmsinitiationdetaildto.getEarlierWorkBrief());
		pfmsinitiationdetail.setCompentencyBrief(pfmsinitiationdetaildto.getCompentencyBrief());
		pfmsinitiationdetail.setNeedOfProjectBrief(pfmsinitiationdetaildto.getNeedOfProjectBrief());
		pfmsinitiationdetail.setTechnologyBrief(pfmsinitiationdetaildto.getTechnologyBrief());
		pfmsinitiationdetail.setRiskMitigationBrief(pfmsinitiationdetaildto.getRiskMitigationBrief());
		pfmsinitiationdetail.setProposalBrief(pfmsinitiationdetaildto.getProposalBrief());
		pfmsinitiationdetail.setRealizationBrief(pfmsinitiationdetaildto.getRealizationBrief());
		pfmsinitiationdetail.setWorldScenarioBrief(pfmsinitiationdetaildto.getWorldScenarioBrief());

		pfmsinitiationdetail.setCreatedBy(UserId);
		pfmsinitiationdetail.setCreatedDate(sdf1.format(new Date()));
		pfmsinitiationdetail.setIsActive(1);

		return dao.ProjectIntiationDetailAdd(pfmsinitiationdetail);
	}

	@Override
	public List<Object[]> BudgetItem(String BudegtId) throws Exception {

		return dao.BudgetItem(BudegtId);
	}

	@Override
	public List<Object[]> ProjectIntiationItemList(String InitiationId) throws Exception {

		return dao.ProjectIntiationItemList(InitiationId);
	}

	@Override
	public Long ProjectIntiationCostAdd(PfmsInitiationCostDto pfmsinitiationcostdto, String UserId,
			Object[] ProjectCost) throws Exception {

		logger.info(new Date() + "Inside SERVICE ProjectIntiationCostAdd ");
		PfmsInitiationCost pfmsinitiationcost = new PfmsInitiationCost();
		pfmsinitiationcost.setInitiationId(Long.parseLong(pfmsinitiationcostdto.getInitiationId()));
		pfmsinitiationcost.setBudgetHeadId(Long.parseLong(pfmsinitiationcostdto.getBudgetHeadId()));
	
		String Item = pfmsinitiationcostdto.getBudgetSancId();

		String[] temp = null;
		String ReFe = "";
		if (Item != null) {
			temp = Item.split("_");
			ReFe = temp[1];
		}

		pfmsinitiationcost.setBudgetSancId(Long.parseLong(temp[0]));
//		Double FeCost=Double.parseDouble(pfmsinitiationcostdto.getItemCost());
//		Double ReCost=Double.parseDouble(pfmsinitiationcostdto.getItemCost());
//		
//		if(ProjectCost[0]!=null) {
//			Double ProjectFeCost=Double.parseDouble(ProjectCost[0].toString());
//			FeCost=ProjectFeCost+Double.parseDouble(pfmsinitiationcostdto.getItemCost());
//		}
//		
//		if(ProjectCost[1]!=null) {			
//			Double ProjectReCost=Double.parseDouble(ProjectCost[1].toString());
//			ReCost=ProjectReCost+Double.parseDouble(pfmsinitiationcostdto.getItemCost());		
//		}
//		
		pfmsinitiationcost.setItemCost(Double.parseDouble(pfmsinitiationcostdto.getItemCost()));
		pfmsinitiationcost.setItemDetail(pfmsinitiationcostdto.getItemDetail());
		pfmsinitiationcost.setCreatedBy(UserId);
		pfmsinitiationcost.setCreatedDate(sdf1.format(new Date()));
		pfmsinitiationcost.setIsActive(1);

		PfmsInitiation pfmsinitiation = new PfmsInitiation();
		long count = dao.ProjectIntiationCostAdd(pfmsinitiationcost, ReFe, pfmsinitiation);

		if (count > 0) {
			double fecost = dao.PfmsInitiationRefeSum(pfmsinitiationcostdto.getInitiationId(), "FE");
			double recost = dao.PfmsInitiationRefeSum(pfmsinitiationcostdto.getInitiationId(), "RE");

			double totalcost = fecost + recost;

			pfmsinitiation.setProjectCost(totalcost);
			pfmsinitiation.setReCost(recost);
			pfmsinitiation.setFeCost(fecost);
			pfmsinitiation.setModifiedBy(UserId);
			pfmsinitiation.setModifiedDate(sdf1.format(new Date()));
			pfmsinitiation.setInitiationId(Long.parseLong(pfmsinitiationcostdto.getInitiationId()));
			dao.ProjectIntiationCostsUpdate(pfmsinitiation);

		}
		return count;
	}

	@Override
	public int ProjectLabAdd(String[] LabId, String InitiationId, String UserId) throws Exception {

		logger.info(new Date() + "Inside SERVICE ProjectLabAdd ");
		Object[] ProjectDetailes = dao.ProjectDetailes(Long.parseLong(InitiationId)).get(0);
		int lastlabcount = 0;

		if (ProjectDetailes[13] != null) {
			lastlabcount = Integer.parseInt(ProjectDetailes[13].toString());
		}

		List<PfmsInitiationLab> pfmsintiationlablist = new ArrayList<PfmsInitiationLab>();
		PfmsInitiation pfmsinitiation = new PfmsInitiation();
		for (String str : LabId) {
			PfmsInitiationLab pfmsintiationlab = new PfmsInitiationLab();
			pfmsintiationlab.setInitiationId(Long.parseLong(InitiationId));
			pfmsintiationlab.setLabId(Long.parseLong(str));
			pfmsintiationlab.setCreatedBy(UserId);
			pfmsintiationlab.setCreatedDate(sdf1.format(new Date()));
			pfmsintiationlab.setIsActive(1);
			pfmsintiationlablist.add(pfmsintiationlab);
		}
		pfmsinitiation.setInitiationId(Long.parseLong(InitiationId));
		pfmsinitiation.setLabCount(lastlabcount + LabId.length);
		pfmsinitiation.setModifiedBy(UserId);
		pfmsinitiation.setModifiedDate(sdf1.format(new Date()));

		return dao.ProjectLabAdd(pfmsintiationlablist, pfmsinitiation);
	}

	@Override
	public List<Object[]> ProjectIntiationLabList(String InitiationId) throws Exception {

		return dao.ProjectIntiationLabList(InitiationId);
	}

	@Override
	public List<Object[]> BudgetHead() throws Exception {

		return dao.BudgetHead();
	}

	@Override
	public Long ProjectScheduleAdd(String[] MilestoneActivity, String[] MilestoneMonth, String[] MilestoneRemark,
			String InitiationId, String UserId, Object[] ProjectDetailes, Integer TotalMonth) throws Exception {
		logger.info(new Date() + "Inside SERVICE ProjectScheduleAdd ");
		List<PfmsInitiationSchedule> pfmsinitiationschedulelist = new ArrayList<PfmsInitiationSchedule>();
		int count = dao.ProjectMileStoneNo(InitiationId) + 1;
		int count1 = 0;

		for (String str : MilestoneActivity) {
			PfmsInitiationSchedule pfmsinitiationschedule = new PfmsInitiationSchedule();
			pfmsinitiationschedule.setInitiationId(Long.parseLong(InitiationId));
			pfmsinitiationschedule.setMilestoneNo(count);
			pfmsinitiationschedule.setMilestoneActivity(str);
			pfmsinitiationschedule.setMilestoneMonth(Integer.parseInt(MilestoneMonth[count1]));
			pfmsinitiationschedule.setMilestoneRemark(MilestoneRemark[count1]);
			pfmsinitiationschedule.setCreatedBy(UserId);
			pfmsinitiationschedule.setCreatedDate(sdf1.format(new Date()));
			pfmsinitiationschedule.setIsActive(1);
			pfmsinitiationschedulelist.add(pfmsinitiationschedule);
			count++;
			count1++;
		}

		PfmsInitiation pfmsinitiation = new PfmsInitiation();
		pfmsinitiation.setProjectDuration(TotalMonth);

		return dao.ProjectScheduleAdd(pfmsinitiationschedulelist, pfmsinitiation);
	}
		
	//New Code
	@Override
	public Long ProjectScheduleAdd(String[] MilestoneActivity, String[] MilestoneMonth, int MilestoneTotalMonth,
			int Milestonestartedfrom, String[] MilestoneRemark, String InitiationId, String UserId,
			Object[] ProjectDetailes, Integer TotalMonth) throws Exception {
		logger.info(new Date() + "Inside SERVICE ProjectScheduleAdd ");
		List<PfmsInitiationSchedule> pfmsinitiationschedulelist = new ArrayList<PfmsInitiationSchedule>();
		int count = dao.ProjectMileStoneNo(InitiationId) + 1;
		int count1 = 0;

		PfmsInitiation initiation = dao.getPfmsInitiationById(InitiationId);
		for (String str : MilestoneActivity) {
			PfmsInitiationSchedule pfmsinitiationschedule = new PfmsInitiationSchedule();
			pfmsinitiationschedule.setInitiationId(Long.parseLong(InitiationId));
			pfmsinitiationschedule.setMilestoneNo(count);
			pfmsinitiationschedule.setMilestoneActivity(str);
			pfmsinitiationschedule.setMilestoneMonth(Integer.parseInt(MilestoneMonth[count1]));
			pfmsinitiationschedule.setMilestoneRemark(MilestoneRemark[count1]);
			pfmsinitiationschedule.setMilestoneTotalMonth(MilestoneTotalMonth); 
			pfmsinitiationschedule.setMilestonestartedfrom(Milestonestartedfrom);
			pfmsinitiationschedule.setStartDate(initiation.getStartDate());
			pfmsinitiationschedule.setEndDate(initiation.getStartDate()!=null?LocalDate.parse(initiation.getStartDate()).plusMonths(pfmsinitiationschedule.getMilestoneMonth()).toString():null);
			pfmsinitiationschedule.setCreatedBy(UserId);
			pfmsinitiationschedule.setCreatedDate(sdf1.format(new Date()));
			pfmsinitiationschedule.setIsActive(1);
			pfmsinitiationschedulelist.add(pfmsinitiationschedule);
			count++;
			count1++;
		}

		PfmsInitiation pfmsinitiation = new PfmsInitiation();
		pfmsinitiation.setProjectDuration(TotalMonth);

		return dao.ProjectScheduleAdd(pfmsinitiationschedulelist, pfmsinitiation);
	}
	

	@Override
	public List<Object[]> ProjectIntiationScheduleList(String InitiationId) throws Exception {

		return dao.ProjectIntiationScheduleList(InitiationId);
	}

	@Override
	public List<Object[]> ProjectScheduleTotalMonthList(String InitiationId) throws Exception {
	
		return dao.ProjectScheduleTotalMonthList(InitiationId);
	}
	
	@Override
	public List<Object[]> MileStonenoTotalMonths(String InitiationId, int msno) throws Exception {
		// TODO Auto-generated method stub
		return dao.MileStonenoTotalMonths(InitiationId, msno);
	}


	@Override
	public Object[] ProjectProgressCount(String InitiationId) throws Exception {

		return dao.ProjectProgressCount(InitiationId);
	}

	@Override
	public List<Object[]> ProjectIntiationDetailsList(String InitiationId) throws Exception {

		return dao.ProjectIntiationDetailsList(InitiationId);
	}

	@Override
	public List<Object[]> ProjectIntiationCostList(String InitiationId) throws Exception {

		return dao.ProjectIntiationCostList(InitiationId);
	}

	@Override
	public List<Object[]> ProjectEditData(String IntiationId) throws Exception {

		return dao.ProjectEditData(IntiationId);
	}

	@Override
	public int ProjectIntiationEdit(PfmsInitiationDto pfmsinitiationdto, String UserId) throws Exception {

		logger.info(new Date() + "Inside SERVICE ProjectIntiationEdit ");

		int ret = 0;

		PfmsInitiation pfmsinitiation = new PfmsInitiation();

		pfmsinitiation.setInitiationId(Long.parseLong(pfmsinitiationdto.getInitiationId()));

		pfmsinitiation.setProjectProgramme(pfmsinitiationdto.getProjectProgramme());
		pfmsinitiation.setProjectTypeId(Long.parseLong(pfmsinitiationdto.getProjectTypeId()));
		//pfmsinitiation.setCategoryId(Long.parseLong(pfmsinitiationdto.getCategoryId()));
		pfmsinitiation.setClassificationId(Long.parseLong(pfmsinitiationdto.getClassificationId()));
		pfmsinitiation.setNodalLab(Long.parseLong(pfmsinitiationdto.getNodalLab()));
		pfmsinitiation.setProjectTitle(pfmsinitiationdto.getProjectTitle());
		pfmsinitiation.setIsPlanned(pfmsinitiationdto.getIsPlanned());
		pfmsinitiation.setIsMultiLab(pfmsinitiationdto.getIsMultiLab());
		pfmsinitiation.setDeliverable(pfmsinitiationdto.getDeliverableId());
		if (pfmsinitiationdto.getIsMultiLab().equals("N")) {
			pfmsinitiation.setLabCount(0);
		}
		if (pfmsinitiationdto.getIsPlanned().equalsIgnoreCase("N")) {
			pfmsinitiation.setRemarks(pfmsinitiationdto.getRemarks());
		} else if (pfmsinitiationdto.getIsPlanned().equalsIgnoreCase("P")) {
			pfmsinitiation.setRemarks("");
		}

		pfmsinitiation.setModifiedBy(UserId);
		pfmsinitiation.setModifiedDate(sdf1.format(new Date()));
		pfmsinitiation.setIsActive(1);
		pfmsinitiation.setIsMain(pfmsinitiationdto.getIsMain());

		pfmsinitiation.setEmpId(Long.parseLong(pfmsinitiationdto.getEmpId()));
		pfmsinitiation.setPCDuration(Long.parseLong(pfmsinitiationdto.getDuration()));
		pfmsinitiation.setPCRemarks(pfmsinitiationdto.getPCRemarks());
		pfmsinitiation.setIndicativeCost(Double.parseDouble(pfmsinitiationdto.getIndicativeCost()));
		pfmsinitiation.setStartDate(pfmsinitiationdto.getStartDate()!=null?fc.RegularToSqlDate(pfmsinitiationdto.getStartDate()):null);
		
		ret = dao.ProjectIntiationEdit(pfmsinitiation);

		List<Object[]> SubProjectList = dao.SubProjectList(pfmsinitiationdto.getInitiationId());

		for (int i = 0; i < SubProjectList.size(); i++) {

			pfmsinitiation.setProjectProgramme(pfmsinitiationdto.getProjectProgramme());
			pfmsinitiation.setProjectTypeId(Long.parseLong(pfmsinitiationdto.getProjectTypeId()));
			//pfmsinitiation.setCategoryId(Long.parseLong(pfmsinitiationdto.getCategoryId()));
			pfmsinitiation.setClassificationId(Long.parseLong(pfmsinitiationdto.getClassificationId()));
			pfmsinitiation.setNodalLab(Long.parseLong(pfmsinitiationdto.getNodalLab()));
			pfmsinitiation.setModifiedDate(sdf1.format(new Date()));
			pfmsinitiation.setIsActive(1);
			pfmsinitiation.setInitiationId(Long.parseLong(SubProjectList.get(i)[0].toString()));
			pfmsinitiation.setEmpId(Long.parseLong(pfmsinitiationdto.getPDD()));
			pfmsinitiation.setPCDuration(Long.parseLong(pfmsinitiationdto.getDuration()));
			pfmsinitiation.setPCRemarks(pfmsinitiationdto.getPCRemarks());
			pfmsinitiation.setIndicativeCost(Double.parseDouble(pfmsinitiationdto.getIndicativeCost()));
			pfmsinitiation.setStartDate(pfmsinitiationdto.getStartDate()!=null?fc.RegularToSqlDate(pfmsinitiationdto.getStartDate()):null);
			
			ret = dao.ProjectIntiationEdit(pfmsinitiation);
		}

		return ret;
	}

	@Override
	public Double TotalIntiationCost(String IntiationId) throws Exception {

		return dao.TotalIntiationCost(IntiationId);
	}

	@Override
	public List<Object[]> ProjectCostEditData(String InitiationCostId) throws Exception {

		return dao.ProjectCostEditData(InitiationCostId);
	}

	@Override
	public int ProjectIntiationCostEdit(PfmsInitiationCostDto pfmsinitiationcostdto, String UserId, String InitiationId,
			String TotalCost) throws Exception {
		logger.info(new Date() + "Inside SERVICE ProjectIntiationCostEdit ");
		PfmsInitiationCost pfmsinitiationcost = new PfmsInitiationCost();
		pfmsinitiationcost.setInitiationCostId(Long.parseLong(pfmsinitiationcostdto.getInitiationCostId()));
		// pfmsinitiationcost.setBudgetHeadId(Long.parseLong(pfmsinitiationcostdto.getBudgetHeadId()));
		pfmsinitiationcost.setBudgetSancId(Long.parseLong(pfmsinitiationcostdto.getBudgetSancId()));
		pfmsinitiationcost.setItemCost(Double.parseDouble(pfmsinitiationcostdto.getItemCost()));
		pfmsinitiationcost.setItemDetail(pfmsinitiationcostdto.getItemDetail());
		pfmsinitiationcost.setModifiedBy(UserId);
		pfmsinitiationcost.setModifiedDate(sdf1.format(new Date()));
		pfmsinitiationcost.setIsActive(1);

		int count = dao.ProjectIntiationCostEdit(pfmsinitiationcost);

		double fecost = dao.PfmsInitiationRefeSum(InitiationId, "FE");
		double recost = dao.PfmsInitiationRefeSum(InitiationId, "RE");

		double totalcost = fecost + recost;

		if (count > 0) {

			PfmsInitiation pfmsinitiation = new PfmsInitiation();
			pfmsinitiation.setProjectCost(totalcost);
			pfmsinitiation.setReCost(recost);
			pfmsinitiation.setFeCost(fecost);
			pfmsinitiation.setModifiedBy(UserId);
			pfmsinitiation.setModifiedDate(sdf1.format(new Date()));
			pfmsinitiation.setInitiationId(Long.parseLong(InitiationId));
			dao.ProjectIntiationCostsUpdate(pfmsinitiation);

		}
		return count;
	}
	@Override
	public Object[] LabListDetails(String labCode) throws Exception {
		return dao.LabListDetails(labCode);
	}
	@Override
	public long ProjectScheduleEdit(ProjectScheduleDto projectscheduledto) throws Exception {

		logger.info(new Date() + "Inside SERVICE ProjectScheduleEdit ");
		
		PfmsInitiation pfmsinitiation = new PfmsInitiation();
		PfmsInitiationSchedule schedule = dao.getInitiationSchedule(projectscheduledto.getInitiationScheduleId());
		PfmsInitiation initiation = dao.getPfmsInitiationById(projectscheduledto.getInitiationId());
		int tempMonths= schedule.getMilestoneMonth();
		
		schedule.setMilestoneActivity(projectscheduledto.getMileStoneActivity());
		schedule.setMilestonestartedfrom(projectscheduledto.getMilestonestartedfrom());
		schedule.setMilestoneMonth(Integer.parseInt(projectscheduledto.getMileStoneMonth()));
		schedule.setStartDate(initiation.getStartDate());
		schedule.setFinancialOutlay(projectscheduledto.getFinancialOutlay()!=null?projectscheduledto.getFinancialOutlay():schedule.getFinancialOutlay()  );
		schedule.setEndDate(initiation.getStartDate()!=null?LocalDate.parse(initiation.getStartDate()).plusMonths(schedule.getMilestoneMonth()).toString():null);
		schedule.setMilestoneRemark(projectscheduledto.getMileStoneRemark());
		schedule.setModifiedBy(projectscheduledto.getModifiedBy());
		schedule.setModifiedDate(sdf1.format(new Date()));
		//long result = dao.ProjectScheduleEdit(schedule);

		
		if(tempMonths!=schedule.getMilestoneMonth()) 
		{
			int count = dao.InitiationClearTotalMonth(projectscheduledto.getInitiationId());
			List<PfmsInitiationSchedule> ScheduleList = dao.IntiationScheduleList(projectscheduledto.getInitiationId());
			
			
			for(PfmsInitiationSchedule milestone : ScheduleList)
			{
				if(milestone.getMilestonestartedfrom()>0)
				{
					
					PfmsInitiationSchedule parentmilestone = dao.MilestoneData(milestone.getInitiationId(),milestone.getMilestonestartedfrom());
					milestone.setMilestoneTotalMonth(milestone.getMilestoneMonth()+parentmilestone.getMilestoneTotalMonth());
					pfmsinitiation.setProjectDuration(dao.ProjectDurationMonth(projectscheduledto.getInitiationId()));
					pfmsinitiation.setInitiationId(Long.parseLong(projectscheduledto.getInitiationId()));
					
					System.out.println(schedule.getMilestoneActivity());
					
					dao.ProjectScheduleEdit(schedule,pfmsinitiation);
				}else
				{
					milestone.setMilestoneTotalMonth(milestone.getMilestoneMonth());
					pfmsinitiation.setProjectDuration(dao.ProjectDurationMonth(projectscheduledto.getInitiationId()));
					pfmsinitiation.setInitiationId(Long.parseLong(projectscheduledto.getInitiationId()));
					
					System.out.println(schedule.getMilestoneActivity()+"----------");
					dao.ProjectScheduleEdit(schedule,pfmsinitiation);
				}
			}
		}
		else {
			pfmsinitiation.setInitiationId(Long.parseLong(projectscheduledto.getInitiationId()));
			pfmsinitiation.setProjectDuration(dao.ProjectDurationMonth(projectscheduledto.getInitiationId()));
		}
		
		
		return 0L; 
	}
	
	@Override
	public int MilestoneTotalMonthUpdate(int newMilestoneTotalMonth, String IntiationId, String milestoneno)throws Exception {
		return dao.MilestoneTotalMonthUpdate(newMilestoneTotalMonth, IntiationId, milestoneno);
	}

	@Override
	public int ProjectScheduleDelete(String InitiationScheduleId, String UserId, Integer MilestoneScheduleMonth,
			String InitiationId) throws Exception {
		logger.info(new Date() + "Inside SERVICE ProjectScheduleDelete ");
		PfmsInitiationSchedule pfmsinitiationschedule = new PfmsInitiationSchedule();
		pfmsinitiationschedule.setInitiationScheduleId(Long.parseLong(InitiationScheduleId));
		pfmsinitiationschedule.setModifiedBy(UserId);
		pfmsinitiationschedule.setModifiedDate(sdf1.format(new Date()));

		PfmsInitiation pfmsinitiation = new PfmsInitiation();
		pfmsinitiation.setProjectDuration(MilestoneScheduleMonth);
		pfmsinitiation.setInitiationId(Long.parseLong(InitiationId));

		return dao.ProjectScheduleDelete(pfmsinitiationschedule, pfmsinitiation);
	}

//	@Override
//	public Long ProjectInitiationDetailsUpdate(PfmsInitiationDetailDto pfmsinitiationdetaildto, String UserId,String Details) throws Exception{
//		
//		logger.info(new Date() +"Inside SERVICE ProjectInitiationDetailsUpdate ");
//		PfmsInitiationDetail pfmsinitiationdetail=new PfmsInitiationDetail();
//		
//		if(Details.equalsIgnoreCase("requirement")) {
//			pfmsinitiationdetail.setRequirements(pfmsinitiationdetaildto.getRequirements());
//		}
//		if(Details.equalsIgnoreCase("objective")) {
//			pfmsinitiationdetail.setObjective(pfmsinitiationdetaildto.getObjective());
//		}
//		if(Details.equalsIgnoreCase("scope")) {
//			pfmsinitiationdetail.setScope(pfmsinitiationdetaildto.getScope());
//		}
//		if(Details.equalsIgnoreCase("multilab")) {
//			pfmsinitiationdetail.setMultiLabWorkShare(pfmsinitiationdetaildto.getMultiLabWorkShare());
//		}
//		if(Details.equalsIgnoreCase("earlierwork")) {
//			pfmsinitiationdetail.setEarlierWork(pfmsinitiationdetaildto.getEarlierWork());
//		}
//		if(Details.equalsIgnoreCase("competency")) {
//			pfmsinitiationdetail.setCompentencyEstablished(pfmsinitiationdetaildto.getCompentencyEstablished());
//		}
//		if(Details.equalsIgnoreCase("needofproject")) {
//			pfmsinitiationdetail.setNeedOfProject(pfmsinitiationdetaildto.getNeedOfProject());
//		}
//		if(Details.equalsIgnoreCase("technology")) {
//			pfmsinitiationdetail.setTechnologyChallanges(pfmsinitiationdetaildto.getTechnologyChallanges());
//		}
//		if(Details.equalsIgnoreCase("riskmitigation")) {
//			pfmsinitiationdetail.setRiskMitigation(pfmsinitiationdetaildto.getRiskMitigation());
//		}
//		if(Details.equalsIgnoreCase("proposal")) {
//			pfmsinitiationdetail.setProposal(pfmsinitiationdetaildto.getProposal());
//		}
//		if(Details.equalsIgnoreCase("realization")) {
//			pfmsinitiationdetail.setRealizationPlan(pfmsinitiationdetaildto.getRealizationPlan());
//		}
//		if(Details.equalsIgnoreCase("worldscenario")) {
//			pfmsinitiationdetail.setWorldScenario(pfmsinitiationdetaildto.getWorldScenario());
//		}
//		
//		
//		pfmsinitiationdetail.setInitiationId(Long.parseLong(pfmsinitiationdetaildto.getInitiationId()));
//		pfmsinitiationdetail.setModifiedBy(UserId);
//		pfmsinitiationdetail.setModifiedDate(sdf1.format(new Date()));
//		
//		
//		return dao.ProjectInitiationDetailsUpdate(pfmsinitiationdetail,Details);
//	}

	@Override
	public Long ProjectInitiationDetailsUpdate(PfmsInitiationDetailDto pfmsinitiationdetaildto, String UserId,
			String Details) throws Exception {

		logger.info(new Date() + "Inside SERVICE ProjectInitiationDetailsUpdate ");
		PfmsInitiationDetail pfmsinitiationdetail = new PfmsInitiationDetail();

		if (Details.equalsIgnoreCase("requirement")) {
			pfmsinitiationdetail.setRequirements(pfmsinitiationdetaildto.getRequirements());
			pfmsinitiationdetail.setReqBrief(pfmsinitiationdetaildto.getReqBrief());
		}
		if (Details.equalsIgnoreCase("objective")) {
			pfmsinitiationdetail.setObjective(pfmsinitiationdetaildto.getObjective());
			pfmsinitiationdetail.setObjBrief(pfmsinitiationdetaildto.getObjBrief());
		}
		if (Details.equalsIgnoreCase("scope")) {
			pfmsinitiationdetail.setScope(pfmsinitiationdetaildto.getScope());
			pfmsinitiationdetail.setScopeBrief(pfmsinitiationdetaildto.getScopeBrief());
		}
		if (Details.equalsIgnoreCase("multilab")) {
			pfmsinitiationdetail.setMultiLabWorkShare(pfmsinitiationdetaildto.getMultiLabWorkShare());
			pfmsinitiationdetail.setMultiLabBrief(pfmsinitiationdetaildto.getMultiLabBrief());
		}
		if (Details.equalsIgnoreCase("earlierwork")) {
			pfmsinitiationdetail.setEarlierWork(pfmsinitiationdetaildto.getEarlierWork());
			pfmsinitiationdetail.setEarlierWorkBrief(pfmsinitiationdetaildto.getEarlierWorkBrief());
		}
		if (Details.equalsIgnoreCase("competency")) {
			pfmsinitiationdetail.setCompentencyEstablished(pfmsinitiationdetaildto.getCompentencyEstablished());
			pfmsinitiationdetail.setCompentencyBrief(pfmsinitiationdetaildto.getCompentencyBrief());
		}
		if (Details.equalsIgnoreCase("needofproject")) {
			pfmsinitiationdetail.setNeedOfProject(pfmsinitiationdetaildto.getNeedOfProject());
			pfmsinitiationdetail.setNeedOfProjectBrief(pfmsinitiationdetaildto.getNeedOfProjectBrief());
		}
		if (Details.equalsIgnoreCase("technology")) {
			pfmsinitiationdetail.setTechnologyChallanges(pfmsinitiationdetaildto.getTechnologyChallanges());
			pfmsinitiationdetail.setTechnologyBrief(pfmsinitiationdetaildto.getTechnologyBrief());
		}
		if (Details.equalsIgnoreCase("riskmitigation")) {
			pfmsinitiationdetail.setRiskMitigation(pfmsinitiationdetaildto.getRiskMitigation());
			pfmsinitiationdetail.setRiskMitigationBrief(pfmsinitiationdetaildto.getRiskMitigationBrief());
		}
		if (Details.equalsIgnoreCase("proposal")) {
			pfmsinitiationdetail.setProposal(pfmsinitiationdetaildto.getProposal());
			pfmsinitiationdetail.setProposalBrief(pfmsinitiationdetaildto.getProposalBrief());
		}
		if (Details.equalsIgnoreCase("realization")) {
			pfmsinitiationdetail.setRealizationPlan(pfmsinitiationdetaildto.getRealizationPlan());
			pfmsinitiationdetail.setRealizationBrief(pfmsinitiationdetaildto.getRealizationBrief());
		}
		if (Details.equalsIgnoreCase("worldscenario")) {
			pfmsinitiationdetail.setWorldScenario(pfmsinitiationdetaildto.getWorldScenario());
			pfmsinitiationdetail.setWorldScenarioBrief(pfmsinitiationdetaildto.getWorldScenarioBrief());
		}

		pfmsinitiationdetail.setInitiationId(Long.parseLong(pfmsinitiationdetaildto.getInitiationId()));
		pfmsinitiationdetail.setModifiedBy(UserId);
		pfmsinitiationdetail.setModifiedDate(sdf1.format(new Date()));

		return dao.ProjectInitiationDetailsUpdate(pfmsinitiationdetail, Details);
	}

	@Override
	public Integer ProjectScheduleMonth(String InitiationId) throws Exception {

		return dao.ProjectScheduleMonth(InitiationId);
	}
	
	@Override/*L.A*/
	public Integer ProjectDurationMonth(String InitiationId) throws Exception {
		// TODO Auto-generated method stub
		return dao.ProjectDurationMonth(InitiationId);
	}
	@Override/*L.A*/
	public Integer MilestoneScheduleMonth(String initiationscheduleid, String IntiationId) throws Exception {
		// TODO Auto-generated method stub
		return dao.MilestoneScheduleMonth(initiationscheduleid,IntiationId);
	}

	
	@Override
	public int ProjectScheduleEditData(String InitiationScheduleId) throws Exception {

		return dao.ProjectScheduleEditData(InitiationScheduleId);
	}
	
	@Override
	public int mileStonemonthprevious(String IntiationId, String milestoneno) throws Exception {
		return dao.mileStonemonthprevious(IntiationId, milestoneno);
	}

	@Override
	public int milestonenototalmonth(String IntiationId, String milestoneno) throws Exception {
		// TODO Auto-generated method stub
		return dao.milestonenototalmonth(IntiationId, milestoneno);
	}


	@Override
	public Long ProjectInitiationAttachmentAdd(PfmsInitiationAttachmentDto pfmsinitiationattachmentdto,
			PfmsInitiationAttachmentFileDto pfmsinitiationattachmentfiledto, String UserId) throws Exception {

		logger.info(new Date() + "Inside SERVICE ProjectInitiationAttachmentAdd ");
		String LabCode = pfmsinitiationattachmentfiledto.getLabCode();
		Timestamp instant = Timestamp.from(Instant.now());
		String timestampstr = instant.toString().replace(" ", "").replace(":", "").replace("-", "").replace(".", "");
//		String Path = LabCode + "\\ProjectInitiation\\";
		
		Path path = Paths.get(LabCode, "ProjectInitiation");
		
		
		Path initiationPath = Paths.get(uploadpath,LabCode,"ProjectInitiation");

		PfmsInitiationAttachment pfmsinitiationattachment = new PfmsInitiationAttachment();
		pfmsinitiationattachment.setInitiationId(Long.parseLong(pfmsinitiationattachmentdto.getInitiationId()));
		pfmsinitiationattachment.setFileName(pfmsinitiationattachmentdto.getFileName());
		pfmsinitiationattachment.setFileNamePath(pfmsinitiationattachmentdto.getFileNamePath());
		pfmsinitiationattachment.setCreatedBy(UserId);
		pfmsinitiationattachment.setCreatedDate(sdf1.format(new Date()));
		pfmsinitiationattachment.setIsActive(1);

		PfmsInitiationAttachmentFile pfmsinitiationattachmentfile = new PfmsInitiationAttachmentFile();
		pfmsinitiationattachmentfile.setFilePath(path.toString());
		pfmsinitiationattachmentfile.setFileName("Initiation" + timestampstr + "."
				+ FilenameUtils.getExtension(pfmsinitiationattachmentfiledto.getFileAttach().getOriginalFilename()));
		saveFile1(initiationPath, pfmsinitiationattachmentfile.getFileName(),
				pfmsinitiationattachmentfiledto.getFileAttach());

		return dao.ProjectInitiationAttachmentAdd(pfmsinitiationattachment, pfmsinitiationattachmentfile);
	}


	@Override
	public List<Object[]> ProjectIntiationAttachment(String InitiationId) throws Exception {

		return dao.ProjectIntiationAttachment(InitiationId);
	}

	@Override
	public List<Object[]> AuthorityAttachment(String InitiationId) throws Exception {

		return dao.AuthorityAttachment(InitiationId);
	}

	

	@Override
	public PfmsInitiationAttachmentFile ProjectIntiationAttachmentFile(String InitiationAttachmentId) throws Exception {

		return dao.ProjectIntiationAttachmentFile(InitiationAttachmentId);
	}

	@Override
	public Object[] ProjectIntiationFileName(long InitiationAttachmentId) throws Exception {
		return dao.ProjectIntiationFileName(InitiationAttachmentId);
	}

	@Override
	public int ProjectInitiationAttachmentDelete(String InitiationAttachmentId, String UserId) throws Exception {

		logger.info(new Date() + "Inside SERVICE ProjectInitiationAttachmentDelete ");
		PfmsInitiationAttachment pfmsinitiationattachment = new PfmsInitiationAttachment();
		pfmsinitiationattachment.setInitiationAttachmentId(Long.parseLong(InitiationAttachmentId));

		pfmsinitiationattachment.setModifiedBy(UserId);
		pfmsinitiationattachment.setModifiedDate(sdf1.format(new Date()));
		return dao.ProjectInitiationAttachmentDelete(pfmsinitiationattachment);
	}

	@Override
	public int ProjectInitiationAttachmentUpdate(String InitiationAttachmentId, String FileName, String UserId)
			throws Exception {

		logger.info(new Date() + "Inside SERVICE ProjectInitiationAttachmentUpdate ");
		PfmsInitiationAttachment pfmsinitiationattachment = new PfmsInitiationAttachment();
		pfmsinitiationattachment.setInitiationAttachmentId(Long.parseLong(InitiationAttachmentId));
		pfmsinitiationattachment.setFileName(FileName);
		pfmsinitiationattachment.setModifiedBy(UserId);
		pfmsinitiationattachment.setModifiedDate(sdf1.format(new Date()));
		return dao.ProjectInitiationAttachmentUpdate(pfmsinitiationattachment);
	}

	@Override
	public String ProjectIntiationAttachmentFileName(String InitiationAttachmentId) throws Exception {

		return dao.ProjectIntiationAttachmentFileName(InitiationAttachmentId);
	}

	@Override
	public String ProjectIntiationAttachmentFileNamePath(String InitiationAttachmentId) throws Exception {

		return dao.ProjectIntiationAttachmentFileNamePath(InitiationAttachmentId);
	}

	@Override
	public int ProjectLabDelete(String initiationlabid, String InitiationId, String UserId) throws Exception {
		logger.info(new Date() + "Inside SERVICE ProjectLabDelete ");
		Object[] ProjectDetailes = dao.ProjectDetailes(Long.parseLong(InitiationId)).get(0);
		int lastlabcount = 0;

		if (ProjectDetailes[13] != null) {
			lastlabcount = Integer.parseInt(ProjectDetailes[13].toString());
		}

		PfmsInitiation pfmsinitiation = new PfmsInitiation();

		PfmsInitiationLab pfmsintiationlab = new PfmsInitiationLab();
		pfmsintiationlab.setInitiationLabId(Long.parseLong(initiationlabid));
		pfmsintiationlab.setLabId(Long.parseLong(initiationlabid));
		pfmsintiationlab.setModifiedBy(UserId);
		pfmsintiationlab.setModifiedDate(sdf1.format(new Date()));

		pfmsinitiation.setInitiationId(Long.parseLong(InitiationId));
		pfmsinitiation.setLabCount(lastlabcount - 1);
		pfmsinitiation.setModifiedBy(UserId);
		pfmsinitiation.setModifiedDate(sdf1.format(new Date()));

		return dao.ProjectLabdelete(pfmsintiationlab, pfmsinitiation);
	}

	@Override
	public int ProjectIntiationCostDelete(String initiationcostid, String UserId, String InitiationId)
			throws Exception {

		logger.info(new Date() + "Inside SERVICE ProjectIntiationCostDelete ");
		PfmsInitiationCost pfmsinitiationcost = new PfmsInitiationCost();
		pfmsinitiationcost.setInitiationCostId(Long.parseLong(initiationcostid));

		pfmsinitiationcost.setModifiedBy(UserId);
		pfmsinitiationcost.setModifiedDate(sdf1.format(new Date()));
		pfmsinitiationcost.setIsActive(1);
		int count = dao.ProjectIntiationCostDelete(pfmsinitiationcost);
		if (count > 0) {
			double fecost = dao.PfmsInitiationRefeSum(InitiationId, "FE");
			double recost = dao.PfmsInitiationRefeSum(InitiationId, "RE");

			double totalcost = fecost + recost;

			if (count > 0) {

				PfmsInitiation pfmsinitiation = new PfmsInitiation();
				pfmsinitiation.setProjectCost(totalcost);
				pfmsinitiation.setReCost(recost);
				pfmsinitiation.setFeCost(fecost);
				pfmsinitiation.setModifiedBy(UserId);
				pfmsinitiation.setModifiedDate(sdf1.format(new Date()));
				pfmsinitiation.setInitiationId(Long.parseLong(InitiationId));
				dao.ProjectIntiationCostsUpdate(pfmsinitiation);

			}
		}

		return count;
	}

	@Override
	public int ProjectIntiationStatusUpdate(String InitiationId, String UserId, String EmpId, String ProjectCode)
			throws Exception {

		logger.info(new Date() + "Inside SERVICE ProjectIntiationStatusUpdate ");
		PfmsInitiation pfmsinitiation = new PfmsInitiation();
		PfmsApproval pfmsapproval = new PfmsApproval();
		PfmsNotification notification = new PfmsNotification();
		Long DivisionHeadId = dao.DivisionHeadId(EmpId);
		pfmsinitiation.setInitiationId(Long.parseLong(InitiationId));
		pfmsinitiation.setProjectStatus("PST");
		pfmsinitiation.setModifiedBy(UserId);
		pfmsinitiation.setModifiedDate(sdf1.format(new Date()));
		
		pfmsapproval.setInitiationId(Long.parseLong(InitiationId));
		pfmsapproval.setProjectStatus("PST");
		pfmsapproval.setEmpId(DivisionHeadId.longValue());
		pfmsapproval.setActionBy(UserId);
		pfmsapproval.setActionDate(sdf1.format(new Date()));
		
		
		
		notification.setEmpId(DivisionHeadId.longValue());
		notification.setNotificationby(Long.parseLong(EmpId));
		notification.setNotificationDate(sdf1.format(new Date()));
		notification.setNotificationMessage("Pending Project Approval for " + ProjectCode + " from User");
		notification.setNotificationUrl("ProjectApprovalPd.htm");
		notification.setCreatedBy(UserId);
		notification.setCreatedDate(sdf1.format(new Date()));
		notification.setIsActive(1);
		notification.setScheduleId(Long.parseLong("0"));
		notification.setStatus("PST");
		return dao.ProjectIntiationStatusUpdate(pfmsinitiation, pfmsapproval, notification);
	}
	
	@Override
	public int ProjectRequirementStatusUpdate(String reqInitiationId, String projectcode, String userId, String Status,String Emp, String Remarks,String action)throws Exception {
		PfmsRequirementApproval approval=new PfmsRequirementApproval();
		PfmsReqStatus prs= new PfmsReqStatus();
		PfmsNotification notification = new PfmsNotification();
		// setting values when action is A or the details are forwarded
		if(action.equalsIgnoreCase("A")) {
		// setting values in requirement status table
//		prs.setInitiationId(Long.parseLong(reqInitiationId));
		prs.setReqInitiationId(Long.parseLong(reqInitiationId));		
		if(Status.equalsIgnoreCase("RIN")||Status.equalsIgnoreCase("RID")||Status.equalsIgnoreCase("RIP")) {
		prs.setStatus("RFU");
		}
		else if(Status.equalsIgnoreCase("RFU")) {
			prs.setStatus("RFD");
		}else {
			prs.setStatus("RFP");
		}
		prs.setModifiedBy(userId);
		prs.setModifiedDate(sdf1.format(new Date()));
		// setting value in initiation_approval table
		approval.setReqInitiationId(Long.parseLong(reqInitiationId));
		if(Status.equalsIgnoreCase("RIN")||Status.equalsIgnoreCase("RID")||Status.equalsIgnoreCase("RIP")) {
		approval.setReqStatus("RFU");
		}
		else if(Status.equalsIgnoreCase("RFU")) {
		approval.setReqStatus("RFD");		
		}else {
			// if project approver approves
			approval.setReqStatus("RFP");
		}
		approval.setRemarks(Remarks);
		//status is forwarded by admin or pdd
		if(Status.equalsIgnoreCase("RIN")||Status.equalsIgnoreCase("RID")||Status.equalsIgnoreCase("RIP")) {
			// Prudhvi pending 03-05-2024
//			approval.setEmpId((dao.getEmpId(initiationid))); // finding the pdd for specific project
		}else{
		//status is forwarded by reviewer or approver
		approval.setEmpId((Emp)); // Emp is reviewer or approver
		}
		approval.setActionBy(userId);
		approval.setActionDate(sdf1.format(new Date()));
		String Empid="";
		if(Status.equalsIgnoreCase("RIN")||Status.equalsIgnoreCase("RID")||Status.equalsIgnoreCase("RIP")) {
			// Prudhvi pending 03-05-2024
//			Empid=dao.getInitiationReviewer(initiationid); // finding the reviewer of specific project
			
			if(Empid.equalsIgnoreCase("0")) {
				return 0;
			}
		}
		else if(Status.equalsIgnoreCase("RFU")) {
			// Prudhvi pending 03-05-2024
//		Empid=dao.getInitiationApprover(initiationid); // finding the approver of specific project
		if(Empid.equalsIgnoreCase("1")) {
			return 0;
		}
		}else {
			// Prudhvi pending 03-05-2024
//			Empid=dao.getEmpId(initiationid);// for sending the notification to the the creator as the requirements are approved
		}
		notification.setEmpId(Long.parseLong(Empid));
		notification.setNotificationby(Long.parseLong(Emp));
		notification.setNotificationDate(sdf1.format(new Date()));
		if(!Status.equalsIgnoreCase("RFD")) {
		notification.setNotificationMessage("Pending Project Requirement Approval for " + projectcode + " from User");
		}else {
		notification.setNotificationMessage(" Project Requirement Approved for " + projectcode );
		}
		if(!Status.equalsIgnoreCase("RFD")) {
		notification.setNotificationUrl("RequirementApproval.htm");// if creator or reviewer forwarded
		}else {
		notification.setNotificationUrl("ProjectOverAllRequirement.htm"); // if approver forwarded then send notifiaction to creator
		}
		notification.setCreatedBy(userId);
		notification.setCreatedDate(sdf1.format(new Date()));
		notification.setIsActive(1);
		notification.setScheduleId(Long.parseLong("0"));
		// forwarded by user
		if(Status.equalsIgnoreCase("RIN")||Status.equalsIgnoreCase("RID")||Status.equalsIgnoreCase("RIP")) {
		notification.setStatus("RFU");
		}else if(Status.equalsIgnoreCase("RFU")) {
			// forwarded by reviewer
		notification.setStatus("RFD");	
		}else {
			notification.setStatus("RFP");	
		}
		}else {
			// when action is returned
			prs.setReqInitiationId(Long.parseLong(reqInitiationId));
			// when the document is returned by reviewer
			if(Status.equalsIgnoreCase("RFU")) {
			prs.setStatus("RID");
			}
			// when the document is returned by approver
			if(Status.equalsIgnoreCase("RFD")) {
			prs.setStatus("RIP");
			}
			prs.setModifiedBy(userId);
			prs.setModifiedDate(sdf1.format(new Date()));
			// setting values in approval table
			approval.setReqInitiationId(Long.parseLong(reqInitiationId));
			if(Status.equalsIgnoreCase("RFU")) {
			approval.setReqStatus("RID");		
			}
			if(Status.equalsIgnoreCase("RFD")) {
				approval.setReqStatus("RIP");		
				}
			approval.setRemarks(Remarks);
			approval.setEmpId((Emp));
			approval.setActionBy(userId);
			approval.setActionDate(sdf1.format(new Date()));
			String Empid="";
			if(Status.equalsIgnoreCase("RFU")||Status.equalsIgnoreCase("RFD")) {
				// returning to pdd of the project 
				// Prudhvi pending 03-05-2024
//				Empid=dao.getEmpId(initiationid);  // Getting the EmpId of pdd for that specific initiation id
			}
			notification.setEmpId(Long.parseLong(Empid));
			notification.setNotificationby(Long.parseLong(Emp));
			notification.setNotificationDate(sdf1.format(new Date()));
			notification.setNotificationMessage("Requirement Document for " + projectcode + " is returned");
			notification.setNotificationUrl("ProjectOverAllRequirement.htm");
			notification.setCreatedBy(userId);
			notification.setCreatedDate(sdf1.format(new Date()));
			notification.setIsActive(1);
			notification.setScheduleId(Long.parseLong("0"));
			if(Status.equalsIgnoreCase("RIN")||Status.equalsIgnoreCase("RID")||Status.equalsIgnoreCase("RIP")) {
			notification.setStatus("RFU");
			}else if(Status.equalsIgnoreCase("RFU")) {
			notification.setStatus("RID");	
			}else {
				notification.setStatus("RIP");
			}
		}
		
		return dao.ProjectRequirementStatusUpdate(prs,approval,notification);
	}
	@Override
	public Long ProjectForwardStatus(String InitiationId) throws Exception {

		return dao.ProjectForwardStatus(InitiationId);
	}

	@Override
	public List<Object[]> ProjectActionList(String ProjectAuthorityId) throws Exception {

		return dao.ProjectActionList(ProjectAuthorityId);
	}

	@Override
	public List<Object[]> ProjectApprovePdList(String EmpId) throws Exception {

		return dao.ProjectApprovePdList(EmpId);
	}

	@Override
	public int ProjectApprovePd(String InitiationId, String Remark, String UserId, String EmpId, String ProjectCode,
			String Status) throws Exception {
		logger.info(new Date() + "Inside SERVICE ProjectApprovePd ");
		PfmsInitiation pfmsinitiation = new PfmsInitiation();
		PfmsApproval pfmsapproval = new PfmsApproval();
		PfmsNotification notification = new PfmsNotification();

		pfmsinitiation.setInitiationId(Long.parseLong(InitiationId));
		pfmsinitiation.setProjectStatus(Status);
		pfmsinitiation.setModifiedBy(UserId);
		pfmsinitiation.setModifiedDate(sdf1.format(new Date()));

		pfmsapproval.setInitiationId(Long.parseLong(InitiationId));
		pfmsapproval.setProjectStatus(Status);
		pfmsapproval.setRemarks(Remark);
		pfmsapproval.setEmpId(Long.parseLong(EmpId));
		pfmsapproval.setActionBy(UserId);
		pfmsapproval.setActionDate(sdf1.format(new Date()));
		String massage = "";
		try {
			massage = dao.StatusDetails(Status);
		} catch (Exception e) {
			logger.error(new Date() + "Inside SERVICE ProjectApprovePd " + e);
		}
		Long Empid = dao.EmpId(InitiationId);
		Long RtmddoId = dao.RtmddoId();
		if (Status.equalsIgnoreCase("DOR")) {
			notification.setEmpId(RtmddoId.longValue());
			notification.setNotificationMessage("Pending Project " + massage + " for " + ProjectCode + " from DO");
			notification.setNotificationUrl("ProjectApprovalRtmddo.htm");
		} else if (Status.equalsIgnoreCase("DOI")) {
			notification.setEmpId(Empid.longValue());
			notification.setNotificationMessage("Pending Project " + massage + " for " + ProjectCode + " from DO");
			notification.setNotificationUrl("ProjectIntiationList.htm");
		}

		notification.setNotificationby(Long.parseLong(EmpId));
		notification.setNotificationDate(sdf1.format(new Date()));

		notification.setCreatedBy(UserId);
		notification.setCreatedDate(sdf1.format(new Date()));
		notification.setIsActive(1);
		notification.setScheduleId(Long.parseLong("0"));
		notification.setStatus(Status);

		return dao.ProjectApprove(pfmsinitiation, pfmsapproval, notification);
	}

	@Override
	public List<Object[]> EmployeeList(String LabCode) throws Exception {

		return dao.EmployeeList(LabCode);
	}

	@Override
	public List<Object[]> ProjectStatusList(String EmpId, String LoginType, String LabCode) throws Exception {

		return dao.ProjectStatusList(EmpId, LoginType, LabCode);
	}

	@Override
	public List<Object[]> ProjectApprovalTracking(String InitiationId) throws Exception {

		return dao.ProjectApprovalTracking(InitiationId);
	}

	@Override
	public List<Object[]> ProjectApproveRtmddoList(String EmpId) throws Exception {

		return dao.ProjectApproveRtmddoList(EmpId);
	}

	@Override
	public List<Object[]> ProjectApproveAdList(String EmpId) throws Exception {
		return dao.ProjectApproveAdList(EmpId);
	}
	
	@Override
	public int ProjectApproveAd(String InitiationId, String Remark, String UserId, String EmpId, String ProjectCode,
			String Status,String LabCode ) throws Exception {
		logger.info(new Date() + "Inside SERVICE ProjectApproveAd ");
		PfmsInitiation pfmsinitiation = new PfmsInitiation();
		PfmsApproval pfmsapproval = new PfmsApproval();
		PfmsNotification notification = new PfmsNotification();

		pfmsinitiation.setInitiationId(Long.parseLong(InitiationId));
		pfmsinitiation.setProjectStatus(Status);
		pfmsinitiation.setModifiedBy(UserId);
		pfmsinitiation.setModifiedDate(sdf1.format(new Date()));

		pfmsapproval.setInitiationId(Long.parseLong(InitiationId));
		pfmsapproval.setProjectStatus(Status);
		pfmsapproval.setRemarks(Remark);
		pfmsapproval.setEmpId(Long.parseLong(EmpId));
		pfmsapproval.setActionBy(UserId);
		pfmsapproval.setActionDate(sdf1.format(new Date()));

		String massage = "";
		try {
			massage = dao.StatusDetails(Status);
		} catch (Exception e) {
			logger.error(new Date() + "Inside SERVICE ProjectApproveAd " + e);
		}
		Long Empid = dao.EmpId(InitiationId);
		Long DivisionHeadId = dao.DivisionHeadId(Empid.toString());
		Long DORTMTDId = dao.RtmddoId();
		Long TccChairpersonId = dao.TccChairpersonId(LabCode);
		if(Status.equalsIgnoreCase("ADR")) {
			notification.setEmpId(TccChairpersonId.longValue());
			notification.setNotificationMessage("Pending Project " + massage + " for " + ProjectCode + " from AD");
			notification.setNotificationUrl("ProjectApprovalTcc.htm");
		}else if (Status.equalsIgnoreCase("ADI")) {
			notification.setEmpId(Empid.longValue());
			notification.setNotificationMessage("Pending Project " + massage + " for " + ProjectCode + " from AD");
			notification.setNotificationUrl("ProjectIntiationList.htm");
		}else if (Status.equalsIgnoreCase("ADO")) {
			notification.setEmpId(DivisionHeadId.longValue());
			notification.setNotificationMessage("Pending Project " + massage + " for " + ProjectCode + " from AD");
			notification.setNotificationUrl("ProjectApprovalPd.htm");
		}else if (Status.equalsIgnoreCase("ADT")) {
			notification.setEmpId(DORTMTDId.longValue());
			notification.setNotificationMessage("Pending Project " + massage + " for " + ProjectCode + " from AD");
			notification.setNotificationUrl("ProjectApprovalRtmddo.htm");
		}
		notification.setNotificationby(Long.parseLong(EmpId));
		notification.setNotificationDate(sdf1.format(new Date()));
		notification.setCreatedBy(UserId);
		notification.setCreatedDate(sdf1.format(new Date()));
		notification.setIsActive(1);
		notification.setScheduleId(Long.parseLong("0"));
		notification.setStatus(Status);

		return dao.ProjectApprove(pfmsinitiation, pfmsapproval, notification);
	}

	@Override
	public int ProjectApproveRtmddo(String InitiationId, String Remark, String UserId, String EmpId, String ProjectCode,
			String Status) throws Exception {
		logger.info(new Date() + "Inside SERVICE ProjectApproveRtmddo ");
		PfmsInitiation pfmsinitiation = new PfmsInitiation();
		PfmsApproval pfmsapproval = new PfmsApproval();
		PfmsNotification notification = new PfmsNotification();
		
		pfmsinitiation.setInitiationId(Long.parseLong(InitiationId));
		pfmsinitiation.setProjectStatus(Status);
		pfmsinitiation.setModifiedBy(UserId);
		pfmsinitiation.setModifiedDate(sdf1.format(new Date()));

		pfmsapproval.setInitiationId(Long.parseLong(InitiationId));
		pfmsapproval.setProjectStatus(Status);
		pfmsapproval.setRemarks(Remark);
		pfmsapproval.setEmpId(Long.parseLong(EmpId));
		pfmsapproval.setActionBy(UserId);
		pfmsapproval.setActionDate(sdf1.format(new Date()));
		String massage = "";
		try {
			massage = dao.StatusDetails(Status);
		} catch (Exception e) {
			logger.error(new Date() + "Inside SERVICE ProjectApproveRtmddo " + e);
		}
		Long Empid = dao.EmpId(InitiationId);
		Long DivisionHeadId = dao.DivisionHeadId(Empid.toString());
		Long TccChairpersonId = dao.AdId();
		if (Status.equalsIgnoreCase("RTR")) {
			notification.setEmpId(TccChairpersonId.longValue());
			notification.setNotificationMessage("Pending Project " + massage + " for " + ProjectCode + " from RTMDDO");
			notification.setNotificationUrl("ProjectApprovalAd.htm");
		} else if (Status.equalsIgnoreCase("RTI")) {
			notification.setEmpId(Empid.longValue());
			notification.setNotificationMessage("Pending Project " + massage + " for " + ProjectCode + " from RTMDDO");
			notification.setNotificationUrl("ProjectIntiationList.htm");
		} else if (Status.equalsIgnoreCase("RTD")) {
			notification.setEmpId(DivisionHeadId.longValue());
			notification.setNotificationMessage("Pending Project " + massage + " for " + ProjectCode + " from RTMDDO");
			notification.setNotificationUrl("ProjectApprovalPd.htm");
		}

		notification.setNotificationDate(sdf1.format(new Date()));

		notification.setCreatedBy(UserId);
		notification.setCreatedDate(sdf1.format(new Date()));
		notification.setIsActive(1);
		notification.setScheduleId(Long.parseLong("0"));
		notification.setStatus(Status);

		return dao.ProjectApprove(pfmsinitiation, pfmsapproval, notification);

	}

	@Override
	public List<Object[]> ProjectApproveTccList(String EmpId) throws Exception {

		return dao.ProjectApproveTccList(EmpId);
	}

	@Override
	public int ProjectApproveTcc(String InitiationId, String Remark, String UserId, String EmpId, String ProjectCode,
			String Status, String Labcode) throws Exception {

		logger.info(new Date() + "Inside SERVICE ProjectApproveTcc ");
		PfmsInitiation pfmsinitiation = new PfmsInitiation();
		PfmsApproval pfmsapproval = new PfmsApproval();
		PfmsNotification notification = new PfmsNotification();

		pfmsinitiation.setInitiationId(Long.parseLong(InitiationId));
		pfmsinitiation.setProjectStatus(Status);
		pfmsinitiation.setModifiedBy(UserId);
		pfmsinitiation.setModifiedDate(sdf1.format(new Date()));

		pfmsapproval.setInitiationId(Long.parseLong(InitiationId));
		pfmsapproval.setProjectStatus(Status);
		pfmsapproval.setRemarks(Remark);
		pfmsapproval.setEmpId(Long.parseLong(EmpId));
		pfmsapproval.setActionBy(UserId);
		pfmsapproval.setActionDate(sdf1.format(new Date()));

		String massage = "";
		try {
			massage = dao.StatusDetails(Status);
		} catch (Exception e) {
			logger.error(new Date() + "Inside SERVICE ProjectApproveTcc " + e);
		}
		Long Empid = dao.EmpId(InitiationId);
		Long DivisionHeadId = dao.DivisionHeadId(Empid.toString());
		Long TccChairpersonId = dao.AdId();
		Long CcmChairpersonId = dao.CcmChairpersonId(Labcode);
		Long DORTMTDId = dao.RtmddoId();
		if (Status.equalsIgnoreCase("PTA") || Status.equalsIgnoreCase("PDR")) {
			notification.setEmpId(CcmChairpersonId.longValue());
			notification.setNotificationMessage("Pending Project " + massage + " for " + ProjectCode + " from TCM");
			notification.setNotificationUrl("ProjectApprovalTcc.htm");
		} else if (Status.equalsIgnoreCase("PTI") || Status.equalsIgnoreCase("DRI")) {
			notification.setEmpId(Empid.longValue());
			notification.setNotificationMessage("Pending Project " + massage + " for " + ProjectCode + " from TCM");
			notification.setNotificationUrl("ProjectIntiationList.htm");
		} else if (Status.equalsIgnoreCase("PTD") || Status.equalsIgnoreCase("DRD")) {
			notification.setEmpId(DivisionHeadId.longValue());
			notification.setNotificationMessage("Pending Project " + massage + " for " + ProjectCode + " from TCM");
			notification.setNotificationUrl("ProjectApprovalPd.htm");
		} else if (Status.equalsIgnoreCase("PTR") || Status.equalsIgnoreCase("DRR")) {
			notification.setEmpId(DORTMTDId.longValue());
			notification.setNotificationMessage("Pending Project " + massage + " for " + ProjectCode + " from TCM");
			notification.setNotificationUrl("ProjectApprovalRtmddo.htm");
		} else if (Status.equalsIgnoreCase("PTY") || Status.equalsIgnoreCase("DAD")) {
			notification.setEmpId(TccChairpersonId.longValue());
			notification.setNotificationMessage("Pending Project " + massage + " for " + ProjectCode + " from TCM");
			notification.setNotificationUrl("ProjectApprovalAd.htm");
		}

		notification.setNotificationby(Long.parseLong(EmpId));
		notification.setNotificationDate(sdf1.format(new Date()));
		notification.setCreatedBy(UserId);
		notification.setCreatedDate(sdf1.format(new Date()));
		notification.setIsActive(1);
		notification.setScheduleId(Long.parseLong("0"));
		notification.setStatus(Status);

		return dao.ProjectApprove(pfmsinitiation, pfmsapproval, notification);

	}

	@Override
	public Double TotalIntiationFeCost(String IntiationId) throws Exception {

		return dao.TotalIntiationFeCost(IntiationId);
	}

	@Override
	public Double TotalIntiationReCost(String IntiationId) throws Exception {

		return dao.TotalIntiationReCost(IntiationId);
	}

	@Override
	public List<Object[]> ProjectCost(Long IntiationId) throws Exception {

		return dao.ProjectCost(IntiationId);
	}

	@Override
	public List<Object[]> TccProjectList() throws Exception {

		return dao.TccProjectList();
	}

	@Override
	public List<Object[]> ExpertList() throws Exception {

		return dao.ExpertList();
	}

	@Override
	public List<Object[]> ProjectMainList() throws Exception {

		return dao.ProjectMainList();
	}

	@Override
	public List<Object[]> ProjectList() throws Exception {

		return dao.ProjectList();
	}

	@Override
	public List<Object[]> ProjectTypeMainList() throws Exception {

		return dao.ProjectTypeMainList();
	}

	@Override
	public List<Object[]> ProjectCategoryList() throws Exception {

		return dao.ProjectCategoryList();
	}

	@Override
	public Long ProjectMasterAdd(ProjectMaster proType) throws Exception {

		return dao.ProjectMasterAdd(proType);
	}

	@Override
	public Long ProjectEdit(ProjectMaster proType) throws Exception {

		return dao.ProjectEdit(proType);
	}

	@Override
	public Long ProjectClose(ProjectMaster proType) throws Exception {

		return dao.ProjectClose(proType);
	}

	@Override
	public Object[] ProjectEditData1(String ProjectId) throws Exception {

		return dao.ProjectEditData1(ProjectId);
	}

	public List<Object[]> getProjectList() throws Exception {
		List<Object[]> projectList = dao.getProjectList();

		return projectList;
	}

	@Override
	public List<Object[]> ProjectAssignList(String EmpId) throws Exception {

		return dao.ProjectAssignList(EmpId);
	}

	@Override
	public List<Object[]> UserList(String proId) throws Exception {

		return dao.UserList(proId);
	}

	@Override
	public Object[] ProjectData(String ProId) throws Exception {

		return dao.ProjectData(ProId);
	}

	@Override
	public Long ProjectAssignAdd(ProjectAssignDto dto) throws Exception {
		logger.info(new Date() + "Inside SERVICE ProjectAssignAdd ");
		long count = 0;
		if(dto.getProjectEmployeeId()==0) {
			for (int i = 0; i < dto.getEmpId().length; i++) {
				ProjectAssign proAssign = new ProjectAssign();
				proAssign.setProjectId(Long.parseLong(dto.getProjectId()));
				proAssign.setRoleMasterId(Long.parseLong(dto.getRoleMasterId()));
				proAssign.setCreatedBy(dto.getCreatedBy());
				proAssign.setCreatedDate(sdf1.format(new Date()));
				proAssign.setEmpId(Long.parseLong(dto.getEmpId()[i]));
				proAssign.setIsActive(1);
	
				count = dao.ProjectAssignAdd(proAssign);
			}
		}else {
			ProjectAssign projectAssign = dao.getProjectAssignById(dto.getProjectEmployeeId()+"");
			projectAssign.setRoleMasterId(Long.parseLong(dto.getRoleMasterId()));
			if(dto.getEmpId()!=null && dto.getEmpId().length>0) {
				projectAssign.setEmpId(Long.parseLong(dto.getEmpId()[0]));
			}
			projectAssign.setModifiedBy(dto.getCreatedBy());
			projectAssign.setModifiedDate(sdf1.format(new Date()));
			
			count = dao.ProjectAssignAdd(projectAssign);
		}
		return count;
	}

	@Override
	public Long ProjectRevoke(ProjectAssign proAssign) throws Exception {
		return dao.ProjectRevoke(proAssign);
	}

	@Override
	public List<Object[]> ApprovalStutusList(String InitiationId) throws Exception {
		logger.info(new Date() + "Inside SERVICE ApprovalStutusList ");
		List<Object[]> list = new ArrayList<Object[]>();
		Object[] obj = dao.ProjectEditData(InitiationId).get(0);
		if (1000000 >= Double.parseDouble(obj[8].toString())) {
			list = dao.ApprovalStutusList("6");
		} else {
			list = dao.ApprovalStutusList("4");
		}
		return list;
	}

	public static void saveFile(String uploadpath, String fileName, MultipartFile multipartFile) throws IOException {
		logger.info(new Date() + "Inside SERVICE saveFile ");
		Path uploadPath = Paths.get(uploadpath);

		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		try (InputStream inputStream = multipartFile.getInputStream()) {
			Path filePath = uploadPath.resolve(fileName);
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ioe) {
			throw new IOException("Could not save image file: " + fileName, ioe);
		}
	}
	
	 public static int  saveFile1(Path uploadPath, String fileName, MultipartFile multipartFile) throws IOException 
	   {
		 logger.info(new Date()  +"Inside SERVICE saveFile ");
		    int result=1;
	        
	        if (!Files.exists(uploadPath)) {
	            Files.createDirectories(uploadPath);
	        }
	        try (InputStream inputStream = multipartFile.getInputStream()) {
	            Path filePath = uploadPath.resolve(fileName);
	            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
	        } catch (IOException ioe) {
	        	 result=0;
	            throw new IOException("Could not save image file: " + fileName, ioe);
	        }   catch (Exception e) {
	        	 result=0;
	        	 logger.error(new Date()  +"Inside SERVICE saveFile "+e);
	        	e.printStackTrace();
	        }
	        return result;
	   }

	@Override
	public List<Object[]> ProjectStageDetailsList() throws Exception {
		return dao.ProjectStageDetailsList();
	}

	@Override
	public Object[] ProjectDataDetails(String projectid) throws Exception {
		return dao.ProjectDataDetails(projectid);
	}

	@Override
	public long ProjectDataSubmit(PfmsProjectDataDto dto) throws Exception {
		logger.info(new Date() + "Inside SERVICE ProjectDataSubmit ");
		String LabCode = dto.getLabcode();
		Timestamp instant = Timestamp.from(Instant.now());
		String timestampstr = instant.toString().replace(" ", "").replace(":", "").replace("-", "").replace(".", "");

	//	String Path = LabCode + "\\ProjectData\\";
		
		Path filePath = Paths.get(uploadpath,LabCode,"ProjectData");
		Path filePath1 = Paths.get(LabCode,"ProjectData");

		PfmsProjectData model = new PfmsProjectData();
		model.setProjectId(Long.parseLong(dto.getProjectId()));
		model.setCurrentStageId(Integer.parseInt(dto.getCurrentStageId()));
		model.setFilesPath(filePath1.toString());

		if (!dto.getSystemConfigImg().isEmpty()) {
			model.setSystemConfigImgName("configimg" + timestampstr + "."
					+ FilenameUtils.getExtension(dto.getSystemConfigImg().getOriginalFilename()));
			saveFile1(filePath, model.getSystemConfigImgName(), dto.getSystemConfigImg());
		} else {
			model.setSystemConfigImgName(null);
		}
//	--------------------------------------------------------------		
		if (!dto.getSystemSpecsFile().isEmpty()) {
			model.setSystemSpecsFileName("specsfile" + timestampstr + "."
					+ FilenameUtils.getExtension(dto.getSystemSpecsFile().getOriginalFilename()));
			saveFile1(filePath, model.getSystemSpecsFileName(), dto.getSystemSpecsFile());
		} else {
			model.setSystemSpecsFileName(null);
		}
//	--------------------------------------------------------------		
		if (!dto.getProductTreeImg().isEmpty()) {
			model.setProductTreeImgName("producttree" + timestampstr + "."
					+ FilenameUtils.getExtension(dto.getProductTreeImg().getOriginalFilename()));
			saveFile1(filePath, model.getProductTreeImgName(), dto.getProductTreeImg());
		} else {
			model.setProductTreeImgName(null);
		}
//	--------------------------------------------------------------		
		if (!dto.getPEARLImg().isEmpty()) {
			model.setPEARLImgName("pearlimg" + timestampstr + "."
					+ FilenameUtils.getExtension(dto.getPEARLImg().getOriginalFilename()));
			saveFile1(filePath, model.getPEARLImgName(), dto.getPEARLImg());
		} else {
			model.setPEARLImgName(null);
		}
// ---------------------------------------------------------------

		model.setCreatedBy(dto.getCreatedBy());
		model.setCreatedDate(sdf1.format(new Date()));
		model.setLastPmrcDate(dto.getLastPmrcDate());
		model.setLastEBDate(dto.getLastEBDate());
		model.setRevisionNo(0);
		model.setProcLimit(Double.parseDouble(dto.getProcLimit()));
		// Convert Path to File
		  File theDir = filePath.toFile();  
	        if (!theDir.exists()) {
	            theDir.mkdirs();
	        }

		return dao.ProjectDataSubmit(model);
	}

	@Override
	public long preProjectFileupload(PreprojectFileDto pfd, MultipartFile fileAttach, String labCode,String UserId,Double version )
			throws Exception {
		logger.info(new Date() + "Inside SERVICE preProjectFileupload ");
		String Path = labCode + "\\ProjectRequirement\\";
		Path prePath = Paths.get(uploadpath,labCode,"ProjectRequirement");
		long count=0;
		PreprojectFile pf=new PreprojectFile();
		pf.setInitiationId(pfd.getInitiationId());
		pf.setStepId(pfd.getStepId());
		pf.setDocumentName(pfd.getDocumentName());
		if(!fileAttach.isEmpty()) {
			pf.setFileName(fileAttach.getOriginalFilename());
			saveFile1(prePath,pf.getFileName(),fileAttach);
		}
		pf.setFilePath(Path);
		pf.setVersionDoc(version);
		pf.setDescription(pfd.getDescription());
		pf.setDocumentId(pfd.getDocumentId());
		pf.setCreatedDate(sdf1.format(new Date()));
		pf.setCreatedBy(UserId);
		pf.setIsActive(1);
		return dao.preProjectFileUpload(pf);
	}
	
	@Override
	public long insertTestVerificationFile(ReqTestExcelFile re , String LabCode) throws Exception {
		
		//String Path = LabCode +"\\APPENDIX\\";
		Path testPath = Paths.get(uploadpath,LabCode,"APPENDIX");
		Path testPath1 = Paths.get(LabCode,"APPENDIX");
		if(!re.getFile().isEmpty()) {
			re.setFileName(re.getFile().getOriginalFilename());
			saveFile1(testPath,re.getFileName(),re.getFile());
		}
		re.setIsActive(1);
		re.setFilePath(testPath1.toString());
		return dao.insertTestVerificationFile(re);
	}
	
	
	@Override
	public long ProjectSqrSubmit(ProjectSqrFile psf, MultipartFile fileAttach, String userId,String LabCode) throws Exception {
		// TODO Auto-generated method stub
		logger.info(new Date() + "Inside SERVICE ProjectSqrSubmit ");
		String Path = LabCode + "\\ProjectRequirement\\";
		Path sqrPath = Paths.get(uploadpath,LabCode,"ProjectRequirement");
		Path sqrPath1 = Paths.get(LabCode,"ProjectRequirement");
		if(!fileAttach.isEmpty()) {
			psf.setFileName(fileAttach.getOriginalFilename());
			saveFile1(sqrPath,psf.getFileName(),fileAttach);
		}
		psf.setFilePath(sqrPath1.toString());
		psf.setCreatedDate(sdf1.format(new Date()));
		psf.setCreatedBy(userId);
		psf.setIsActive(1);
		return dao.ProjectSqrSubmit(psf);
	}
	
	@Override
	public long ProjectDataEditSubmit(PfmsProjectDataDto dto) throws Exception {
		logger.info(new Date() + "Inside SERVICE ProjectDataSubmit ");

		String LabCode = dto.getLabcode();

		Timestamp instant = Timestamp.from(Instant.now());
		String timestampstr = instant.toString().replace(" ", "").replace(":", "").replace("-", "").replace(".", "");
		String query = "";

//		String Path = LabCode + "\\ProjectData\\";
		
		Path filePath = Paths.get(uploadpath,LabCode,"ProjectData");
		Path filePath1 = Paths.get(LabCode,"ProjectData");

		PfmsProjectData model = new PfmsProjectData();
		model.setProjectId(Long.parseLong(dto.getProjectId()));
		model.setProjectDataId(Long.parseLong(dto.getProjectDataId()));
		model.setCurrentStageId(Integer.parseInt(dto.getCurrentStageId()));
		model.setFilesPath(filePath1.toString());
		if (!dto.getSystemConfigImg().isEmpty()) {
			model.setSystemConfigImgName("configimg" + timestampstr + "."
					+ FilenameUtils.getExtension(dto.getSystemConfigImg().getOriginalFilename()));
			saveFile1(filePath, model.getSystemConfigImgName(), dto.getSystemConfigImg());
			query = query + "SystemConfigImgName='" + model.getSystemConfigImgName() + "',";
		}
		if (!dto.getSystemSpecsFile().isEmpty()) {
			model.setSystemSpecsFileName("specsfile" + timestampstr + "."
					+ FilenameUtils.getExtension(dto.getSystemSpecsFile().getOriginalFilename()));
			saveFile1(filePath, model.getSystemSpecsFileName(), dto.getSystemSpecsFile());
			query = query + "SystemSpecsFileName='" + model.getSystemSpecsFileName() + "',";
		}
		if (!dto.getProductTreeImg().isEmpty()) {
			model.setProductTreeImgName("producttree" + timestampstr + "."
					+ FilenameUtils.getExtension(dto.getProductTreeImg().getOriginalFilename()));
			saveFile1(filePath, model.getProductTreeImgName(), dto.getProductTreeImg());
			query = query + "ProductTreeImgName='" + model.getProductTreeImgName() + "',";
		}
		if (!dto.getPEARLImg().isEmpty()) {
			model.setPEARLImgName("pearlimg" + timestampstr + "."
					+ FilenameUtils.getExtension(dto.getPEARLImg().getOriginalFilename()));
			saveFile1(filePath, model.getPEARLImgName(), dto.getPEARLImg());
			query = query + "PEARLImgName='" + model.getPEARLImgName() + "',";
		}
		model.setModifiedBy(dto.getModifiedBy());
		model.setModifiedDate(sdf1.format(new Date()));
		model.setRevisionNo(Long.parseLong(dto.getRevisionNo()));
		model.setProcLimit(Double.parseDouble(dto.getProcLimit()));
		model.setLastPmrcDate(dto.getLastPmrcDate());
		model.setLastEBDate(dto.getLastEBDate());

		return dao.ProjectDataEditSubmit(model, query);
	}

	@Override
	public Object[] ProjectDataSpecsFileData(String projectdataid) throws Exception {
		return dao.ProjectDataSpecsFileData(projectdataid);
	}

	@Override
	public long ProjectDataRevSubmit(PfmsProjectDataDto dto) throws Exception {
		logger.info(new Date() + "Inside SERVICE ProjectDataRevSubmit ");
		Object[] projectdatadetails = dao.ProjectDataDetails(dto.getProjectId());
		PfmsProjectDataRev model = new PfmsProjectDataRev();
		
		model.setProjectId(Long.parseLong(projectdatadetails[1].toString()));
		model.setFilesPath(projectdatadetails[2]+"");
		model.setSystemConfigImgName(projectdatadetails[3]+"");
		model.setSystemSpecsFileName(projectdatadetails[4]+"");
		model.setProductTreeImgName(projectdatadetails[5]+"");
		model.setPEARLImgName(projectdatadetails[6]+"");
		model.setCurrentStageId(Integer.parseInt(projectdatadetails[7]+""));
		model.setRevisionNo(Long.parseLong(projectdatadetails[8]+""));
		model.setRevisionDate(sdf1.format(new Date()));
		model.setCreatedBy(dto.getModifiedBy());
		model.setCreatedDate(sdf1.format(new Date()));

		return dao.ProjectRevDataSubmit(model);
	}

	@Override
	public List<Object[]> ProjectDataRevList(String projectid) throws Exception {
		return dao.ProjectDataRevList(projectid);
	}

	@Override
	public Object[] ProjectDataRevData(String projectdatarevid) throws Exception {
		return dao.ProjectDataRevData(projectdatarevid);
	}

	@Override
	public Object[] ProjectDataSpecsRevFileData(String projectdatarevid) throws Exception {
		return dao.ProjectDataSpecsRevFileData(projectdatarevid);
	}

	@Override
	public List<Object[]> InitiatedProjectList() throws Exception {

		return dao.InitiatedProjectList();
	}

	@Override
	public List<Object[]> InitiatedProjectDetails(String ProjectId) throws Exception {

		return dao.InitiatedProjectDetails(ProjectId);
	}

	@Override
	public List<Object[]> NodalLabList() throws Exception {

		return dao.NodalLabList();
	}

	@Override
	public List<Object[]> ProjectRiskDataList(String projectid, String LabCode) throws Exception {
		return dao.ProjectRiskDataList(projectid, LabCode);
	}

	@Override
	public Object[] ProjectRiskData(String actionassignid) throws Exception {
		return dao.ProjectRiskData(actionassignid);
	}
	@Override
	public long CloseProjectRisk(PfmsRiskDto dto)throws Exception
	{
		dto.setStatusDate(sdf1.format(new Date()));
		dto.setModifiedDate(sdf1.format(new Date()));
		
		return dao.CloseProjectRisk(dto);
	}

	@Override
	public long ProjectRiskDataSubmit(PfmsRiskDto dto) throws Exception {
		logger.info(new Date() + "Inside SERVICE ProjectRiskDataSubmit ");

		PfmsRisk model = new PfmsRisk();
		model.setLabCode(dto.getLabCode());
		model.setProjectId(Long.parseLong(dto.getProjectId()));
		model.setActionMainId(Long.parseLong(dto.getActionMainId()));
		model.setDescription(dto.getDescription());
		model.setSeverity(Integer.parseInt(dto.getSeverity()));
		model.setProbability(Integer.parseInt(dto.getProbability()));
		model.setRPN(model.getSeverity() * model.getProbability());
		model.setImpact(dto.getImpact());
		model.setMitigationPlans(dto.getMitigationPlans());
		model.setRevisionNo(Long.parseLong("0"));
		model.setCategory(dto.getCategory());
		model.setRiskTypeId(Integer.parseInt(dto.getRiskTypeId()));
		model.setStatus(dto.getStatus());
		model.setCreatedBy(dto.getCreatedBy());
		model.setCreatedDate(sdf1.format(new Date()));
		model.setIsActive(1);

		return dao.ProjectRiskDataSubmit(model);
	}

	@Override
	public Object[] ProjectRiskMatrixData(String actionmainid) throws Exception {
		logger.info(new Date() + "Inside ProjectRiskMatrixData ");
		return dao.ProjectRiskMatrixData(actionmainid);
	}

	@Override
	public long ProjectRiskDataEdit(PfmsRiskDto dto) throws Exception {
		logger.info(new Date() + "Inside ProjectRiskDataEdit");
		dto.setModifiedDate(sdf1.format(new Date()));
		return dao.ProjectRiskDataEdit(dto);
	}

	@Override
	public long ProjectRiskDataRevSubmit(PfmsRiskDto dto) throws Exception {
		logger.info(new Date() + "Inside SERVICE ProjectDataRevSubmit ");
		Object[] riskmatrixdata = dao.ProjectRiskMatrixData(dto.getActionMainId());
		PfmsRiskRev model = new PfmsRiskRev();

		model.setProjectId(Long.parseLong(riskmatrixdata[1].toString()));
		model.setActionMainId(Long.parseLong(riskmatrixdata[2].toString()));
		model.setDescription(riskmatrixdata[3].toString());
		model.setSeverity(Integer.parseInt(riskmatrixdata[4].toString()));
		model.setProbability(Integer.parseInt(riskmatrixdata[5].toString()));
		model.setRPN(Integer.parseInt(riskmatrixdata[9].toString()));

		model.setMitigationPlans(riskmatrixdata[6].toString());
		model.setImpact(riskmatrixdata[10].toString());
		model.setCategory(riskmatrixdata[11].toString());
		model.setRiskTypeId(Integer.parseInt(riskmatrixdata[12].toString()));

		model.setRevisionNo(Long.parseLong(riskmatrixdata[7].toString()));
		model.setRevisionDate(sdf1.format(new Date()));
		model.setCreatedBy(dto.getModifiedBy());
		model.setCreatedDate(sdf1.format(new Date()));
		model.setLabCode(riskmatrixdata[8].toString());

		return dao.ProjectRiskDataRevSubmit(model);
	}

	@Override
	public List<Object[]> ProjectRiskMatrixRevList(String actionmainid) throws Exception {
		return dao.ProjectRiskMatrixRevList(actionmainid);
	}

	@Override
	public List<Object> RiskDataPresentList(String projectid, String LabCode) throws Exception {
		return dao.RiskDataPresentList(projectid, LabCode);
	}

	@Override
	public Long ProjectInitiationAuthorityAdd(PfmsInitiationAuthorityDto pfmsinitiationauthoritydto, String UserId,
			PfmsInitiationAuthorityFileDto pfmsinitiationauthorityfiledto) throws Exception {

		logger.info(new Date() + "Inside SERVICE ProjectInitiationAuthorityAdd ");
		String LabCode = pfmsinitiationauthorityfiledto.getFilePath();
		Timestamp instant = Timestamp.from(Instant.now());
		String timestampstr = instant.toString().replace(" ", "").replace(":", "").replace("-", "").replace(".", "");

//		String Path = LabCode + "\\ProjectReference\\";
		Path path = Paths.get(LabCode, "ProjectReference");
		Path authPath = Paths.get(uploadpath,LabCode,"ProjectReference");
		
		PfmsInitiationAuthority pfmsauthority = new PfmsInitiationAuthority();
		pfmsauthority.setInitiationId(Long.parseLong(pfmsinitiationauthoritydto.getInitiationId()));
		pfmsauthority.setAuthorityName(Long.parseLong(pfmsinitiationauthoritydto.getAuthorityName()));
		pfmsauthority.setLetterDate(new java.sql.Date(sdf.parse(pfmsinitiationauthoritydto.getLetterDate()).getTime()));
		pfmsauthority.setLetterNo(pfmsinitiationauthoritydto.getLetterNo());
		pfmsauthority.setCreatedBy(UserId);
		pfmsauthority.setCreatedDate(sdf1.format(new Date()));

		PfmsInitiationAuthorityFile pfmsinitiationauthorityfile = new PfmsInitiationAuthorityFile();

		pfmsinitiationauthorityfile.setFile(path.toString());
		pfmsinitiationauthorityfile.setAttachmentName("Reference" + timestampstr + "."
				+ pfmsinitiationauthorityfiledto.getAttachFile().getOriginalFilename().split("\\.")[1]);
		saveFile1(authPath, pfmsinitiationauthorityfile.getAttachmentName(),
				pfmsinitiationauthorityfiledto.getAttachFile());

		return dao.ProjectInitiationAuthorityAdd(pfmsauthority, pfmsinitiationauthorityfile);
	}

	@Override
	public PfmsInitiationAuthorityFile ProjectAuthorityDownload(String AuthorityFileId) throws Exception {

		return dao.ProjectAuthorityDownload(AuthorityFileId);
	}

	@Override
	public Long ProjectAuthorityUpdate(PfmsInitiationAuthorityDto pfmsinitiationauthoritydto,
			PfmsInitiationAuthorityFileDto pfmsinitiationauthorityfiledto, String UserId) throws Exception {

		logger.info(new Date() + "Inside SERVICE ProjectInitiationAuthorityAdd ");
		Timestamp instant = Timestamp.from(Instant.now());
		String timestampstr = instant.toString().replace(" ", "").replace(":", "").replace("-", "").replace(".", "");

		Long ret = 0L;

		PfmsInitiationAuthority pfmsauthority = new PfmsInitiationAuthority();
		PfmsInitiationAuthorityFile pfmsinitiationauthorityfile = new PfmsInitiationAuthorityFile();

		pfmsauthority.setInitiationId(Long.parseLong(pfmsinitiationauthoritydto.getInitiationId()));
		pfmsauthority.setAuthorityName(Long.parseLong(pfmsinitiationauthoritydto.getAuthorityName()));
		pfmsauthority.setLetterDate(new java.sql.Date(sdf.parse(pfmsinitiationauthoritydto.getLetterDate()).getTime()));
		pfmsauthority.setLetterNo(pfmsinitiationauthoritydto.getLetterNo());
		pfmsauthority.setModifiedBy(UserId);
		pfmsauthority.setModifiedDate(sdf1.format(new Date()));

		ret = dao.ProjectAuthorityUpdate(pfmsauthority);

		if (!pfmsinitiationauthorityfiledto.getAttachFile().isEmpty()) {
			PfmsInitiationAuthorityFile attachment = dao
					.ProjectAuthorityDownload(pfmsinitiationauthorityfiledto.getInitiationAuthorityFileId());
			String file = uploadpath + attachment.getFile() + attachment.getAttachmentName();
			File f = new File(file);
			if (f.exists()) {
				f.delete();
			}
			String Path = pfmsinitiationauthorityfiledto.getFilePath() + "\\ProjectReference\\";
			Path authorityPath = Paths.get(uploadpath,pfmsinitiationauthorityfiledto.getFilePath(),"ProjectReference");
			pfmsinitiationauthorityfile.setAttachmentName("Reference" + timestampstr + "."
					+ FilenameUtils.getExtension(pfmsinitiationauthorityfiledto.getAttachFile().getOriginalFilename()));
			pfmsinitiationauthorityfile.setFile(Path);
			pfmsinitiationauthorityfile.setInitiationAuthorityFileId(
					Long.parseLong(pfmsinitiationauthorityfiledto.getInitiationAuthorityFileId()));
			saveFile1(authorityPath, pfmsinitiationauthorityfile.getAttachmentName(),
					pfmsinitiationauthorityfiledto.getAttachFile());

			ret = dao.AuthorityFileUpdate(pfmsinitiationauthorityfile);

		}

		return ret;
	}

	public List<Object[]> getProjectCatSecDetalis(String projectmainId) throws Exception {
		Long projectId = Long.parseLong(projectmainId);
		return dao.getProjectCatSecDetalis(projectId);
	}

	@Override
	public List<Object[]> LoginProjectDetailsList(String empid, String Logintype, String LabCode) throws Exception {
		return dao.LoginProjectDetailsList(empid, Logintype, LabCode);
	}

	@Override
	public List<Object[]> ProjectApprovalFlowEmpData(String empid, String LabCode) throws Exception {
		logger.info(new Date() + "Inside SERVICE ProjectApprovalFlowEmpData ");
		List<Object[]> list = new ArrayList<Object[]>();

		Object[] temp = dao.EmpDivHeadData(empid);
		if (temp != null) {
			list.add(temp);
		}

		list.addAll(dao.DoRtmdAdEmpData(LabCode));

		temp = dao.DirectorEmpData(LabCode);
		if (temp != null) {
			list.add(temp);
		}
		return list;
	}

	@Override
	public long ProjectMainToMaster(String projectmainid, String user, String LabCode) throws Exception {
		logger.info(new Date() + "Inside SERVICE ProjectMainToMaster ");

		ProjectMain main = dao.ProjectMainObj(projectmainid);
		ProjectMaster master = new ProjectMaster();
		master.setProjectMainId(main.getProjectMainId());
		master.setProjectCode(main.getProjectCode());
		// ProjectImmsCd
		master.setProjectShortName(main.getProjectShortName());
		master.setEndUser(main.getEndUser());
		master.setScope(main.getScope());
		master.setApplication(main.getApplication());
		master.setProjectName(main.getProjectName());
		master.setProjectDescription(main.getProjectDescription());
		master.setUnitCode(main.getUnitCode());
		master.setProjectType(main.getProjectTypeId());
		master.setProjectCategory(main.getCategoryId());
		master.setSanctionNo(main.getSanctionNo());
		master.setSanctionDate(main.getSanctionDate());
		master.setTotalSanctionCost(main.getTotalSanctionCost());
		master.setSanctionCostRE(main.getSanctionCostRE());
		master.setSanctionCostFE(main.getSanctionCostFE());
		master.setPDC(main.getPDC());
		master.setProjSancAuthority(main.getProjSancAuthority());
		master.setProjectDirector(main.getProjectDirector());
		master.setBoardReference(main.getBoardReference());
		master.setRevisionNo(main.getRevisionNo());
		master.setIsMainWC(1); // main.getIsMainWC()
		master.setWorkCenter(main.getWorkCenter());
		master.setObjective(main.getObjective());
		master.setDeliverable(main.getDeliverable());
		master.setIsActive(1);
		master.setCreatedBy(user);
		master.setCreatedDate(sdf1.format(new Date()));
		master.setLabCode(LabCode);
		master.setLabParticipating(main.getLabParticipating());
		master.setPlatformId(main.getPlatformId()); 	//srikant
		master.setIsCCS("N");
		
		Long result = dao.ProjectMasterAdd(master);

		ProjectAssignDto proAssigndto=new ProjectAssignDto();
		proAssigndto.setRoleMasterId("1");
		proAssigndto.setCreatedBy(user);
		proAssigndto.setProjectId(result.toString());;
		proAssigndto.setCreatedDate(sdf1.format(new Date()));
		proAssigndto.setEmpId(new String[]{master.getProjectDirector()+""});
		
		ProjectAssignAdd(proAssigndto);
	
		return result;
	}

	@Override
	public ProjectMasterRev ProjectMasterREVSubmit(String projectid, String userid, String remarks) throws Exception {
		logger.info(new Date() + "Inside SERVICE ProjectMasterREVSubmit ");
		ProjectMaster master = dao.ProjectMasterData(Long.parseLong(projectid));
		ProjectMasterRev rev = new ProjectMasterRev();

		rev.setProjectId(master.getProjectId());
		rev.setRevisionNo(master.getRevisionNo());
		rev.setProjectMainId(master.getProjectMainId());
		rev.setProjectCode(master.getProjectCode());
		rev.setProjectImmsCd(master.getProjectImmsCd());
		rev.setProjectName(master.getProjectName());
		rev.setProjectDescription(master.getProjectDescription());
		rev.setUnitCode(master.getUnitCode());
		rev.setProjectType(master.getProjectType());
		rev.setProjectCategory(master.getProjectCategory());
		rev.setSanctionNo(master.getSanctionNo());
		rev.setSanctionDate(master.getSanctionDate());
		rev.setTotalSanctionCost(master.getTotalSanctionCost());
		rev.setSanctionCostFE(master.getSanctionCostFE());
		rev.setSanctionCostRE(master.getSanctionCostRE());
		rev.setPDC(master.getPDC());
		rev.setProjectDirector(master.getProjectDirector());
		rev.setProjSancAuthority(master.getProjSancAuthority());
		rev.setBoardReference(master.getBoardReference());
		rev.setIsMainWC(master.getIsMainWC());
		rev.setWorkCenter(master.getWorkCenter());
		rev.setApplication(master.getApplication());
		rev.setLabParticipating(master.getLabParticipating());
        rev.setScope(master.getScope());
		rev.setObjective(master.getObjective());
		rev.setDeliverable(master.getDeliverable());
		rev.setCreatedBy(userid);
		rev.setCreatedDate(sdf1.format(new Date()));
		rev.setRemarks(remarks);
		rev.setPlatformId(master.getPlatformId()); //srikant
		rev.setPlatform(master.getPlatform()); // srikant

		return dao.ProjectREVSubmit(rev);
	}

	@Override
	public List<Object[]> ProjectTypeMainListNotAdded() throws Exception {
		return dao.ProjectTypeMainListNotAdded();
	}

	@Override
	public List<Object[]> ProjectRevList(String projectid) throws Exception {
		return dao.ProjectRevList(projectid);
	}

	@Override
	public long ProjectMasterAttachAdd(ProjectMasterAttachDto dto) throws Exception {
		logger.info(new Date() + "Inside SERVICE ProjectMastetAttachAdd ");

		String projectcode = dao.ProjectData(dto.getProjectId())[1].toString();
//		String path = dto.getLabCode() + "\\ProjectMasterFiles\\" + projectcode;
		Path path = Paths.get(dto.getLabCode(), "ProjectMasterFiles", projectcode);
		Path projectAttachPath = Paths.get(uploadpath,dto.getLabCode(),"ProjectMasterFiles",projectcode);

		//String FullPath = uploadpath + path;

		File filepath = projectAttachPath.toFile();
		long ret = 0;
		if (!filepath.exists()) {
			filepath.mkdirs();
		}

		for (int i = 0; i < dto.getFiles().length; i++) {
			if (!dto.getFiles()[i].isEmpty()) {
				ProjectMasterAttach modal = new ProjectMasterAttach();
				modal.setProjectId(Long.parseLong(dto.getProjectId()));
				modal.setFileName(dto.getFileName()[i]);
				modal.setOriginalFileName(dto.getFiles()[i].getOriginalFilename());

				modal.setCreatedBy(dto.getCreatedBy());
				modal.setCreatedDate(sdf1.format(new Date()));

				//String fullFilePath = FullPath + "\\" + modal.getOriginalFileName();
				Path fullFilePath =  Paths.get(projectAttachPath.toString(),modal.getOriginalFileName());

				File file =fullFilePath.toFile();
				int count = 0;
				while (true) {
				//	file = fullFilePath.toFile();
					if (file.exists()) {
						count++;
						/*
						 * fullFilePath = uploadpath + path + "\\" +
						 * FilenameUtils.getBaseName(modal.getOriginalFileName()) + "-" + count + "." +
						 * FilenameUtils.getExtension(modal.getOriginalFileName());
						 */
						 String newFileName = FilenameUtils.getBaseName(modal.getOriginalFileName()) + "-" + count + "."
			                        + FilenameUtils.getExtension(modal.getOriginalFileName());
			                fullFilePath = Paths.get(projectAttachPath.toString(), newFileName);
			                file = fullFilePath.toFile();
					} else {
						if (count > 0) {
							/*
							 * modal.setOriginalFileName(FilenameUtils.getBaseName(modal.getOriginalFileName
							 * ()) + "-" + count + "." +
							 * FilenameUtils.getExtension(modal.getOriginalFileName()));
							 */
							 String newFileName = FilenameUtils.getBaseName(modal.getOriginalFileName()) + "-" + count + "."
			                            + FilenameUtils.getExtension(modal.getOriginalFileName());
			                    modal.setOriginalFileName(newFileName);
						}
						break;
					}
				}

				modal.setPath(path.toString());

				saveFile1(projectAttachPath, modal.getOriginalFileName(), dto.getFiles()[i]);
				ret = dao.ProjectMasterAttachAdd(modal);

			}
		}
		return ret;
	}

	@Override
	public List<Object[]> ProjectMasterAttachList(String projectid) throws Exception {
		return dao.ProjectMasterAttachList(projectid);
	}

	@Override
	public Object[] ProjectMasterAttachData(String projectattachid) throws Exception {
		return dao.ProjectMasterAttachData(projectattachid);
	}

	@Override
	public int ProjectMasterAttachDelete(String projectattachid) throws Exception {
		logger.info(new Date() + "Inside SERVICE ProjectMasterAttachDelete ");
		Object[] attachdata = dao.ProjectMasterAttachData(projectattachid);
		boolean result = false;
		File file = new File(attachdata[2] + File.separator + attachdata[3]);
		System.out.println("esists1"+file.exists());
		if (file.exists()) {
			System.out.println("esists2");
			result = file.delete();
		}
	
			return dao.ProjectMasterAttachDelete(projectattachid);
	}

	@Override
	public long TechnicalWorkDataAdd(ProjectTechnicalWorkData modal) throws Exception {
		modal.setCreatedDate(sdf1.format(new Date()));
		modal.setIsActive(1);

		return dao.TechnicalWorkDataAdd(modal);
	}

	@Override
	public long TechnicalWorkDataEdit(ProjectTechnicalWorkData modal, String techdataid) throws Exception {
		return dao.TechnicalWorkDataEdit(modal, Long.parseLong(techdataid));
	}

	@Override
	public List<Object[]> InitiationCheckList(String initiationid) throws Exception {
		return dao.InitiationCheckList(initiationid);
	}

	@Override
	public long IntiationChecklistUpdate(PfmsInitiationChecklistData cldata) throws Exception {
		logger.info(new Date() + "Inside SERVICE IntiationChecklistUpdate ");
		PfmsInitiationChecklistData cldatacheck = dao.InitiationChecklistCheck(cldata);
		if (cldatacheck != null && cldatacheck.getChecklistDataId() > 0) {
			cldatacheck.setIsChecked(cldatacheck.getIsChecked() ^ 1);
			cldatacheck.setModifiedBy(cldata.getCreatedBy());
			cldatacheck.setModifiedDate(sdf1.format(new Date()));
			return dao.InitiationChecklistUpdate(cldatacheck);
		} else {
			cldata.setIsChecked(1);
			cldata.setCreatedDate(sdf1.format(new Date()));
			return dao.InitiationChecklistAdd(cldata);
		}

	}
	@Override
	public long sanctionDataUpdate(PfmsInitiationSanctionData psd) throws Exception {
		// TODO Auto-generated method stub
		
		String TDN=psd.getTDN();
		if(TDN==null) {
		PfmsInitiationSanctionData pd=dao.sanctionDataUpdate(psd);
		
		if(pd!=null && pd.getStatementDataId()>0) {
			pd.setIsChecked(pd.getIsChecked()^1);
			pd.setModifiedBy(psd.getCreatedBy());
			pd.setModifiedDate(psd.getCreatedDate());
			return dao.sanctionDataUpdated(pd);
		}else {
			psd.setIsChecked(1);
			psd.setCreatedDate(sdf1.format(new Date()));
			
			return dao.sanctionDataAdd(psd);
		}
		}
		else {
			
			psd.setCreatedDate(sdf1.format(new Date()));
			
			return dao.sanctionDataAdd(psd);
		}
		
	
	}

		@Override
		public List<Object[]> RiskTypeList() throws Exception 
		{
			return dao.RiskTypeList();
		}

		@Override
		public List<Object[]> RequirementTypeList() throws Exception {
			// TODO Auto-generated method stub
			return dao.RequirementTypeList();
		}

		@Override
		public long ProjectRequirementAdd(PfmsInitiationRequirementDto prd,String UserId, String LabCode, String shortname) throws Exception {
			// TODO Auto-generated method stu
			logger.info(new Date() + "Inside SERVICE ProjectRequirementAdd ");
			PfmsInititationRequirement pir=new PfmsInititationRequirement();
//			pir.setInitiationId(prd.getInitiationId());
			pir.setReqTypeId(prd.getReqTypeId());
			pir.setRequirementBrief(prd.getRequirementBrief());
			pir.setRequirementDesc(prd.getRequirementDesc());
			pir.setRequirementId(prd.getRequirementId());
			pir.setReqCount(prd.getReqCount());
			pir.setPriority(prd.getPriority());
			pir.setLinkedRequirements(prd.getLinkedRequirements());
			pir.setNeedType(prd.getNeedType());
			pir.setRemarks(prd.getRemarks());
			pir.setCategory(prd.getCategory());
			pir.setConstraints(prd.getConstraints());
			pir.setLinkedDocuments(prd.getLinkedDocuments());
			pir.setLinkedPara(prd.getLinkedPara());
			pir.setCreatedBy(UserId);
			pir.setCreatedDate(sdf1.format(new Date()));
			pir.setIsActive(1);
			pir.setReqInitiationId(prd.getReqInitiationId());
			Object[] reqStatus=dao.reqStatus(prd.getReqInitiationId());
			PfmsReqStatus prs= new PfmsReqStatus();
			if(reqStatus!=null) {
				return dao.ProjectRequirementAdd(pir);
			}else {
				String version=dao.maxRequirementVersion(prd.getReqInitiationId());
				prs.setRequirementNumber("REQ/"+shortname+"/"+(Integer.parseInt(version)+1));
				prs.setVersion(Integer.parseInt(version)+1+"");;
//				prs.setInitiationId(prd.getInitiationId());
				prs.setStatus("RIN");
				prs.setCreatedBy(UserId);
				prs.setApprovalId(0l);
				prs.setCreatedDate(sdf1.format(new Date()));
				prs.setIsActive(1);
				prs.setReqInitiationId(prd.getReqInitiationId());
				return dao.ProjectRequirementAdd(pir,prs);
			}
		}
		@Override
		public List<Object[]> RequirementList(String reqInitiationId) throws Exception {
			// TODO Auto-generated method stub
			return dao.RequirementList(reqInitiationId);
		}
		@Override
		public long ProjectRequirementDelete(long initiationReqId) throws Exception {
			// TODO Auto-generated method stub
			return dao.ProjectRequirementDelete(initiationReqId);
		}

		@Override
		public Object[] Requirement(long InitiationReqId) throws Exception {
			// TODO Auto-generated method stub
			return dao.Requirement(InitiationReqId);
		}

		@Override
		public long RequirementUpdate(PfmsInitiationRequirementDto prd, String userId, String initiationReqId) throws Exception {
			// TODO Auto-generated method stub
			logger.info(new Date() + "Inside SERVICE RequirementUpdate ");
			PfmsInititationRequirement pir= reqDao.getPfmsInititationRequirementById(prd.getReqInitiationId()+"");
//			pir.setInitiationId(prd.getInitiationId());
//			pir.setInitiationId(prd.getInitiationId());
			pir.setReqTypeId(prd.getReqTypeId());
			pir.setRequirementBrief(prd.getRequirementBrief());
			pir.setRequirementDesc(prd.getRequirementDesc());
			pir.setPriority(prd.getPriority());
			pir.setReqCount(prd.getReqCount());
			pir.setLinkedRequirements(prd.getLinkedRequirements());
			pir.setRequirementId(prd.getRequirementId());
			pir.setNeedType(prd.getNeedType());
			pir.setRemarks(prd.getRemarks());
			pir.setCategory(prd.getCategory());
			pir.setConstraints(prd.getConstraints());
			pir.setLinkedPara(prd.getLinkedPara());
			pir.setLinkedDocuments(prd.getLinkedDocuments());
			pir.setModifiedBy(userId);
			pir.setModifiedDate(sdf1.format(new Date()));
//			pir.setReqInitiationId(prd.getReqInitiationId());
			/* pir.setIsActive(1); */
			return reqDao.addOrUpdatePfmsInititationRequirement(pir);
		
		}
		
		@Override
		public long projectTDNUpdate(PfmsInitiationSanctionData psd) throws Exception {
			psd.setModifiedDate(sdf1.format(new Date()));
			return dao.projectTDNUpdate(psd);
		}
		
		
		@Override
		public long numberOfReqTypeId(String intiationId,String projectId) throws Exception {
		
			return dao.numberOfReqTypeId(intiationId,projectId);
		}

		@Override
		public List<Object[]> BudgetHeadList(Long projecttypeid) throws Exception {
			
			return dao.BudgetHeadList(projecttypeid);
		}

		@Override
		public List<Integer> reqcountList(String initiationId) throws Exception {
		
		return	dao.reqcountList(initiationId);
		}

		@Override
		public int deleteRequirement(String initiationReqId) throws Exception {
		
		return	dao.deleteRequirement(initiationReqId);
		}

		@Override
		public String getReqId(int i, String initiationId) throws Exception {
	
			return dao.getReqId(i,initiationId);
		}

		@Override
		public int updateReqId(int last, String s, int first, String initiationId) throws Exception {

			return dao.updateReqId(last,s,first,initiationId);
		}

		@Override
		public Object[] reqType(String r) throws Exception {
	
			return dao.reqType(r);
		}

		@Override
		public List<Object[]> RequirementAttachmentList(String inititationReqId) throws Exception {
			
			return dao.RequirementAttachmentList(inititationReqId);
		}

	
		@Override
		public Object[] reqAttachDownload(String DocumentId,String VersionDoc,String initiationid, String stepid) throws Exception {

			return dao.reqAttachDownload( DocumentId,VersionDoc,initiationid,stepid);
		}

		@Override
		public long requirementAttachmentDelete(String attachmentid) throws Exception {
			
			return dao.requirementAttachmentDelete(attachmentid);
		}
		@Override
		public Object inititionSteps() throws Exception {
	
			return dao.initiationSteps();
		}
		@Override
		public List<Object[]> getProjectFilese(String initiationid, String stepid) throws Exception {

			return dao.getProjectFilese(initiationid,stepid);
		}
		@Override
		public long filecount(String stepid, String initiationid) throws Exception {
			return dao.filecount(stepid,initiationid);
		}
		@Override
		public List<Object[]> projectfilesList(String inititationid, String stepid, String documentcount)throws Exception {
	
			return dao.projectfilesList(inititationid,stepid,documentcount);
		}

		@Override
		public List<Object[]> requirementFiles(String initiationid, int stepid) throws Exception {
			return dao.requirementFiles(initiationid,stepid);
		}

		@Override
		public List<Object[]> sanctionlistDetails(String initiationid) throws Exception {
			// TODO Auto-generated method stub
			return dao.sanctionlistDetails(initiationid);
		}

		@Override
		public long addProjectPGNAJ(PfmsInitiationSanctionData psd) throws Exception {

			return dao.addProjectPGNAJ(psd);
		}

		@Override
		public long ProjectPGNAJUpdate(PfmsInitiationSanctionData psd) throws Exception {

			return dao.ProjectPGNAJUpdate(psd);
		}

		/*
		 * @Override public long
		 * projectInitiationAdditionalRequirementUpdate(PfmsInitiation pf) throws
		 * Exception {
		 * 
		 * return dao.projectInitiationAdditionalRequirementUpdate(pf); }
		 * 
		 * @Override public long projectInitiationMethodologyUpdate(PfmsInitiation pf)
		 * throws Exception {
		 * 
		 * return dao.projectInitiationMethodologyUpdate(pf); }
		 */
		@Override
		public long projectInitiationUserUpdate(PfmsInitiation pf) throws Exception {

			return dao.projectInitiationUserUpdate(pf);
		}
		@Override
		public List<Object[]> projectfiles(String inititationid, String stepid) throws Exception {

			return dao.projectfiles(inititationid,stepid);
		}

		@Override
		public Object[] projectfile(String initiationid, String stepid, String documentid) throws Exception {
			// TODO Auto-generated method stub
			return dao.projectfile(initiationid,stepid,documentid);
		}
		@Override
		public List<Object[]> DemandList() throws Exception {
			// TODO Auto-generated method stub
			return dao.DemandList();
		}

		@Override
		public long PfmsProcurementPlanSubmit(PfmsProcurementPlan pp) throws Exception {
			// TODO Auto-generated method stub
			return dao.PfmsProcurementPlanSubmit(pp);
		}

		@Override
		public List<Object[]> ProcurementList(String initiationid) throws Exception {
			// TODO Auto-generated method stub
			return dao.ProcurementList(initiationid);
		}

		@Override
		public Object[] PocurementPlanEditDetails(String planid) throws Exception {
			// TODO Auto-generated method stub
			return dao.PocurementPlanEditDetails(planid);
		}

		@Override
		public long ProjectProcurementEdit(PfmsProcurementPlan pp) throws Exception {
			// TODO Auto-generated method stub
			return dao.ProjectProcurementEdit(pp);
		}
		@Override
		public String TotalPayOutMonth(String start, String end, String initiationid) throws Exception {
			// TODO Auto-generated method stub
			return dao.TotalPayOutMonth(start,end,initiationid);
		}

		@Override
		public Object[] projectMacroDetails(String initiationid) throws Exception {
			// TODO Auto-generated method stub
			return dao.projectMacroDetails(initiationid);
		}

		@Override
		public long InsertMacroDetails(PfmsInitiationMacroDetails pm) throws Exception {
			// TODO Auto-generated method stub
			return dao.InsertMacroDetails(pm);
		}

			@Override
			public long updateMactroDetailsMethodology(PfmsInitiationMacroDetails pm) throws Exception {
				// TODO Auto-generated method stub
				return dao.updateMactroDetailsMethodology(pm);
			}

			@Override
			public long updateMactroDetailsRequirements(PfmsInitiationMacroDetails pm) throws Exception {
				// TODO Auto-generated method stub
				return dao.updateMactroDetailsRequirements(pm);
			}

		@Override
		public long UpdateProjectEnclosure(PfmsInitiationMacroDetails pm) throws Exception {
			// TODO Auto-generated method stub
			return dao.UpdateProjectEnclosure(pm);
		}
		@Override
		public long UpdateProjectOtherInformation(PfmsInitiationMacroDetails pm) throws Exception {
			// TODO Auto-generated method stub
			return dao.UpdateProjectOtherInformation(pm);
		}
		@Override
		public long ProposedprojectdeliverablesUpdate(PfmsInitiationMacroDetails pm) throws Exception {
			// TODO Auto-generated method stub
			return dao.ProposedprojectdeliverablesUpdate(pm);
		}
		@Override
		public long ProjectMajorTrainingRequirementSubmit(ProjectMajorRequirementsDto pmr,String UserId) throws Exception {
			// TODO Auto-generated method stub
			ProjectMajorRequirements PMR=new ProjectMajorRequirements();
			PMR.setInitiationId(pmr.getInitiationId());
			PMR.setDiscipline(pmr.getDiscipline());
			PMR.setAgency(pmr.getAgency());
			PMR.setPersonneltrained(pmr.getPersonneltrained());
			PMR.setCost(pmr.getCost());
			PMR.setDuration(pmr.getDuration());
			PMR.setRemarks(pmr.getRemarks());
			PMR.setCreatedBy(UserId);
			PMR.setCreatedDate(sdf1.format(new Date()));
			PMR.setIsActive(1);
			
			long count=dao.ProjectMajorTrainingRequirementSubmit(PMR);
			
			
			return count;
		}
		@Override
		public List<Object[]> TrainingRequirementList(String initiationid) throws Exception {
			// TODO Auto-generated method stub
			return dao.TrainingRequirementList(initiationid);
		}
		@Override
		public Object[] TraingRequirements(String trainingid) throws Exception {
			// TODO Auto-generated method stub
			return dao.TraingRequirements(trainingid);
		}
		@Override
		public long ProjectMajorTrainingRequirementUpdate(ProjectMajorRequirementsDto pmr,String userid) throws Exception {
			// TODO Auto-generated method stub
			ProjectMajorRequirements PMR=new ProjectMajorRequirements();
			PMR.setDiscipline(pmr.getDiscipline());
			PMR.setAgency(pmr.getAgency());
			PMR.setPersonneltrained(pmr.getPersonneltrained());
			PMR.setCost(pmr.getCost());
			PMR.setDuration(pmr.getDuration());
			PMR.setRemarks(pmr.getRemarks());
			PMR.setModifiedBy(userid);
			PMR.setModifiedDate(sdf1.format(new Date()));
			PMR.setIsActive(1);
			PMR.setTrainingId(pmr.getTrainingId());
			return dao.ProjectMajorTrainingRequirementUpdate(PMR);
		}
		
		@Override
		public long WorkPackageSubmit(ProjectMajorWorkPackagesDto pwd, String userId) throws Exception {
			// TODO Auto-generated method stub
			
			ProjectMajorWorkPackages pw= new ProjectMajorWorkPackages();
			pw.setInitiationId(pwd.getInitiationId());
			pw.setGovtAgencies(pwd.getGovtAgencies());
			pw.setWorkPackage(pwd.getWorkPackage());
			pw.setObjective(pwd.getObjective());
			pw.setScope(pwd.getScope());
			pw.setCost(pwd.getCost());
			pw.setPDC(pwd.getPDC());
			pw.setCreatedBy(userId);
			pw.setCreatedDate(sdf1.format(new Date()));
			pw.setIsActive(1);
			
			return dao.WorkPackageSubmit(pw);
		}
		@Override
		public List<Object[]> WorkPackageList(String initiationid) throws Exception {
			// TODO Auto-generated method stub
			return dao.WorkPackageList(initiationid);
		}
		@Override
		public Object[] WorkPackageValue(String parameter) throws Exception {
			// TODO Auto-generated method stub
			return dao.WorkPackageValue(parameter);
		}
		@Override
		public long WorkPackagesEdit(ProjectMajorWorkPackagesDto pwd, String userId) throws Exception {
			// TODO Auto-generated method stub
			ProjectMajorWorkPackages pw= new ProjectMajorWorkPackages();
			pw.setGovtAgencies(pwd.getGovtAgencies());
			pw.setWorkPackage(pwd.getWorkPackage());
			pw.setObjective(pwd.getObjective());
			pw.setScope(pwd.getScope());
			pw.setCost(pwd.getCost());
			pw.setPDC(pwd.getPDC());
			pw.setWorkId(pwd.getWorkId());
			pw.setModifiedBy(userId);
			pw.setModifiedDate(sdf1.format(new Date()));
			return dao.WorkPackagesEdit(pw);
		}
		
		@Override
		public long CarsDetailsAdd(ProjectMajorCarsDto pcd, String userId) throws Exception {
			// TODO Auto-generated method stub
			ProjectMajorCars pmc=new ProjectMajorCars();
			pmc.setInitiationId(pcd.getInitiationId());
			pmc.setInstitute(pcd.getInstitute());
			pmc.setProfessor(pcd.getProfessor());
			pmc.setAreaRD(pcd.getAreaRD());
			pmc.setCost(pcd.getCost());
			pmc.setPDC(pcd.getPDC());
			pmc.setConfidencelevel(pcd.getConfidencelevel());
			pmc.setCreatedBy(userId);
			pmc.setCreatedDate(sdf1.format(new Date()));
			pmc.setIsActive(1);
			return dao.CarsDetailsAdd(pmc);
		}
		@Override
		public List<Object[]> CarsList(String parameter) throws Exception {
			// TODO Auto-generated method stub
			return dao.CarsList(parameter);
		}
		@Override
		public Object[] CarsValue(String parameter) throws Exception {
			// TODO Auto-generated method stub
			return dao.CarsValue(parameter);
		}
		
		@Override
		public long carsEdit(ProjectMajorCarsDto pcd, String userId) throws Exception {
			// TODO Auto-generated method stub
			ProjectMajorCars pmc=new ProjectMajorCars();
			pmc.setCarsId(pcd.getCarsId());
			pmc.setInstitute(pcd.getInstitute());
			pmc.setProfessor(pcd.getProfessor());
			pmc.setAreaRD(pcd.getAreaRD());
			pmc.setCost(pcd.getCost());
			pmc.setPDC(pcd.getPDC());
			pmc.setConfidencelevel(pcd.getConfidencelevel());
			pmc.setModifiedBy(userId);
			pmc.setModifiedDate(sdf1.format(new Date()));
			pmc.setIsActive(1);
			return dao.CarEdit(pmc);
		}
		
		@Override
		public long ConsultancySubmit(ProjectMajorConsultancyDto pcd, String userId) throws Exception {
			// TODO Auto-generated method stub
			ProjectMajorConsultancy pmc=new ProjectMajorConsultancy();
			pmc.setInitiationId(pcd.getInitiationId());
			pmc.setDiscipline(pcd.getDiscipline());
			pmc.setAgency(pcd.getAgency());
			pmc.setPerson(pcd.getPerson());
			pmc.setProcess(pcd.getProcess());
			pmc.setCost(pcd.getCost());
			pmc.setCreatedBy(userId);
			pmc.setCreatedDate(sdf1.format(new Date()));
			pmc.setIsActive(1);
			return dao.ConsultancySubmit(pmc);
		}
			@Override
			public List<Object[]> ConsultancyList(String initiationid) throws Exception {
				// TODO Auto-generated method stub
			return dao.ConsultancyList(initiationid);
			}
			@Override
			public Object[] ConsultancyValue(String parameter) throws Exception {
				// TODO Auto-generated method stub
				return dao.ConsultancyValue(parameter);
			}
			@Override
			public long ConsultancyEdit(ProjectMajorConsultancyDto pcd, String userId) throws Exception {
				// TODO Auto-generated method stub
				ProjectMajorConsultancy pmc=new ProjectMajorConsultancy();
				pmc.setConsultancyId(pcd.getConsultancyId());
				pmc.setDiscipline(pcd.getDiscipline());
				pmc.setAgency(pcd.getAgency());
				pmc.setPerson(pcd.getPerson());
				pmc.setProcess(pcd.getProcess());
				pmc.setCost(pcd.getCost());
				pmc.setModifiedBy(userId);
				pmc.setModifiedDate(sdf1.format(new Date()));
				pmc.setIsActive(1);
				return dao.ConsultancyEdit(pmc);
			}	
			@Override
			public long ManpowerSubmit(ProjectMajorManPowersDto pmd, String userId) throws Exception {
				// TODO Auto-generated method stub
				ProjectMajorManPowers pm= new ProjectMajorManPowers();
				pm.setInitiationId(pmd.getInitiationId());
				pm.setDesignation(pmd.getDesignation());
				pm.setDiscipline(pmd.getDiscipline());
				pm.setNumbers(pmd.getNumbers());
				pm.setPeriod(pmd.getPeriod());
				pm.setRemarks(pmd.getRemarks());
				pm.setCreatedBy(userId);
				pm.setCreatedDate(sdf1.format(new Date()));
				pm.setIsActive(1);
				return dao.ManpowerSubmit(pm);
			}
			@Override
			public List<Object[]> ManpowerList(String parameter) throws Exception {
				return dao.ManpowerList(parameter);
			}
			@Override
			public Object[] ManpowerValue(String parameter) throws Exception {
				return dao.ManpowerValue(parameter);
			}
			
			@Override
			public long ManpowerEdit(ProjectMajorManPowersDto pmd, String userId) throws Exception {
				ProjectMajorManPowers pm= new ProjectMajorManPowers();
				pm.setRequirementId(pmd.getRequirementId());
				pm.setDesignation(pmd.getDesignation());
				pm.setDiscipline(pmd.getDiscipline());
				pm.setNumbers(pmd.getNumbers());
				pm.setPeriod(pmd.getPeriod());
				pm.setRemarks(pmd.getRemarks());
				pm.setModifiedBy(userId);
				pm.setModifiedDate(sdf1.format(new Date()));
				pm.setIsActive(1);
				
				return dao.ManPowerEdit(pm);
			}
			@Override
			public Object[] macroDetailsPartTwo(String initiationid) throws Exception {
				return dao.macroDetailsPartTwo(initiationid);
			}
			@Override
			public long MacroDetailsPartTwoSubmit(PfmsInitiationMacroDetailsTwo pmd) throws Exception {
				// TODO Auto-generated method stub
				return dao.MacroDetailsPartTwoSubmit(pmd);
			}
			@Override
			public long MacroDetailsPartTwoEdit(PfmsInitiationMacroDetailsTwo pmd) throws Exception {
				// TODO Auto-generated method stub
				return dao.MacroDetailsPartTwoEdit(pmd);
			}
			@Override
			public Object[] BriefTechnicalAppreciation(String initiationid) throws Exception {
				// TODO Auto-generated method stub
				return dao.BriefTechnicalAppreciation(initiationid);
			}
			
			@Override
			public long BriefTechnicalAppreciationSubmit(ProjectMactroDetailsBrief pmb) throws Exception {
				// TODO Auto-generated method stub
				return dao.BriefTechnicalAppreciationSubmit(pmb);
			}
			
			@Override
			public long BriefTechnicalAppreciationEdit(ProjectMactroDetailsBrief pmb) throws Exception {
				// TODO Auto-generated method stub
				return dao.BriefTechnicalAppreciationEdit(pmb);
			}
			@Override
			public List<Object[]> GetCostBreakList(String initiationid, String projecttypeid) throws Exception {
				// TODO Auto-generated method stub
				return dao.GetCostBreakList(initiationid,projecttypeid);
			}
			@Override
			public long ProjectCapsiSubmit(ProjectMajorCapsi pmc) throws Exception {
				// TODO Auto-generated method stub
				return dao.ProjectCapsiSubmit(pmc);
			}
			@Override
			public List<Object[]> CapsiList(String initiationid) throws Exception {
				// TODO Auto-generated method stub
				return dao.CapsiList(initiationid);
			}
			@Override
			public Object[] CapsiValue(String parameter) throws Exception {
		
				return dao.CapsiValue(parameter);
			}
			@Override
			public long CapsiEdt(ProjectMajorCapsi pmc) throws Exception {
				// TODO Auto-generated method stub
				return dao.CapsiEdt(pmc);
			}
		@Override
		public Object[] AllLabList(String labCode) throws Exception {
			// TODO Auto-generated method stub
			return dao.ALlLabList(labCode);
		}
		@Override
		public List<Object[]> ProcurementInitiationCostList(String initiationid, String InitiationCostId) throws Exception {
			// TODO Auto-generated method stub
			return dao.ProcurementInitiationCostList(initiationid,InitiationCostId);
		}
		@Override
		public long BriefAchievementEdit(ProjectMactroDetailsBrief pmb) throws Exception {
			// TODO Auto-generated method stub
			return dao.BriefAchievementEdit(pmb);
		}
		@Override
		public long BriefTRLanalysisEdit(ProjectMactroDetailsBrief pmb) throws Exception {
			// TODO Auto-generated method stub
			return dao.BriefTRLanalysisEdit(pmb);
		}
		@Override
		public long BriefpeerEdit(ProjectMactroDetailsBrief pmb) throws Exception {
			// TODO Auto-generated method stub
			return dao.BriefpeerEdit(pmb);
		}
		
		@Override
		public long BriefActionEdit(ProjectMactroDetailsBrief pmb) throws Exception {
			// TODO Auto-generated method stub
			return dao.BriefActionEdit(pmb);
		}
		@Override
		public long BriefTestEdit(ProjectMactroDetailsBrief pmb) throws Exception {
			// TODO Auto-generated method stub
			return dao.BriefTestEdit(pmb);
		}
		@Override
		public long BriefMatrixEdit(ProjectMactroDetailsBrief pmb) throws Exception {
			// TODO Auto-generated method stub
			return dao.BriefMatrixEdit(pmb);
		}
		@Override
		public long BriefDevEdit(ProjectMactroDetailsBrief pmb) throws Exception {
			// TODO Auto-generated method stub
			return dao.BriefDevEdit(pmb);
		}
		@Override
		public long BriefProductionAgenciesEdit(ProjectMactroDetailsBrief pmb) throws Exception {
			// TODO Auto-generated method stub
			return dao.BriefProductionAgenciesEdit(pmb);
		}
		@Override
		public long BriefCriticalTechsEdit(ProjectMactroDetailsBrief pmb) throws Exception {
			// TODO Auto-generated method stub
			return dao.BriefCriticalTechsEdit(pmb);
		}
		@Override
		public long BriefCostsBenefitsEdit(ProjectMactroDetailsBrief pmb) throws Exception {

			return dao.BriefCostsBenefitsEdit(pmb);
		}
		@Override
		public long BriefProjectManagementEdit(ProjectMactroDetailsBrief pmb) throws Exception {

			return dao.BriefProjectManagementEdit(pmb);
		}
		@Override
		public long BriefPERTEdit(ProjectMactroDetailsBrief pmb) throws Exception {

			return dao.BriefPERTEdit(pmb);
		}
		@Override
		public Object[] reqStatus(Long reqInitiationId) throws Exception {

			return dao.reqStatus(reqInitiationId);
		}
		//for approval flow
	@Override
	public List<Object[]> DocumentApprovalFlowData(String labCode, String initiationid) throws Exception {

		return dao.DocumentApprovalFlowData(labCode,initiationid);
	}
//	@Override
//	public List<Object[]> RequirementApprovalList(String empId) throws Exception {
//		
//		return dao.RequirementApprovalList(empId);
//	}
	
//	@Override
//	public List<Object[]> RequirementTrackingList(String initiationid) throws Exception {
//		return dao.RequirementTrackingList(initiationid);
//	}
	
@Override
	public List<Object[]> projecOtherRequirements(String initiationid,String projectid) throws Exception {
		// TODO Auto-generated method stub
		return dao.projecOtherRequirements(initiationid,projectid);
	}
	@Override
	public long AddOtherRequirement(ProjectOtherReqDto pd, String subreqName) throws Exception {
		ProjectOtherReqModel pm= new ProjectOtherReqModel();
//		pm.setInitiationId(pd.getInitiationId());
		pm.setReqMainId(pd.getReqMainId());
		pm.setRequirementName(pd.getRequirementName());
		pm.setReqParentId(0l);
		pm.setIsActive(1);
//		pm.setProjectId(pd.getProjectId());
		pm.setReqInitiationId(pd.getReqInitiationId());
		List<Object[]>MainIdDetails=dao.OtherRequirementList(pd.getReqInitiationId(),pd.getReqMainId());    //changed and the method is used to get if any req with specific mainid is added or not
		System.out.println("MainId - "+pd.getReqMainId());
		System.out.println("MainIdDetails - "+MainIdDetails.size());
		
		long count=0l;
		long count1=0l;
		if(MainIdDetails.size()==0) {
			count1=dao.ProjectOtherRequirementAdd(pm);
			  if(count1>0) { 
				  ProjectOtherReqModel pms= new ProjectOtherReqModel();
//				  pms.setInitiationId(pd.getInitiationId());
				  pms.setReqMainId(pd.getReqMainId()); 
//				  pms.setProjectId(pd.getProjectId());
				  pms.setReqParentId(count1);
				  pms.setRequirementName(subreqName);
				  pms.setIsActive(1);
				  pms.setReqInitiationId(pd.getReqInitiationId());
				  count=dao.ProjectOtherRequirementAdd(pms);
			  }
		}else {
			pm.setRequirementName(subreqName);
			pm.setReqParentId(Long.parseLong(MainIdDetails.get(0)[0].toString()));
			pm.setIsActive(1);
			count=dao.ProjectOtherRequirementAdd(pm);
		}
		return count;
	}@Override
	public List<Object[]> OtherRequirementList(String reqInitiationId, String reqMainId) throws Exception {
		// TODO Auto-generated method stub
		return dao.OtherRequirementList(Long.parseLong(reqInitiationId),Long.parseLong(reqMainId));
	}
	@Override
	public long UpdateOtherRequirementName(ProjectOtherReqDto pd) throws Exception {
		
		ProjectOtherReqModel pm= new ProjectOtherReqModel();
		pm.setRequirementName(pd.getRequirementName());
		pm.setRequirementId(pd.getRequirementId());
		return dao.UpdateOtherRequirementName(pm);
	}
	
	@Override
	public long UpdateOtherRequirementDetails(ProjectOtherReqDto pd, String requirementId) throws Exception {
		// TODO Auto-generated method stub
		ProjectOtherReqModel pm= new ProjectOtherReqModel();
		long count=0;
		if(!requirementId.equalsIgnoreCase("0")) {
	
			pm.setRequirementDetails(pd.getRequirementDetails());
			pm.setRequirementId(Long.parseLong(requirementId));
			count =dao.UpdateOtherRequirementDetails(pm);
		}else {
			requirementId=dao.getRequirementId(pd.getReqInitiationId(),pd.getReqMainId(),0);
			if(Long.parseLong(requirementId)>0) {
				
				pm.setRequirementDetails(pd.getRequirementDetails());
				pm.setRequirementId(Long.parseLong(requirementId));
				count =dao.UpdateOtherRequirementDetails(pm);
			}else {
//				pm.setInitiationId((pd.getInitiationId()));
				pm.setReqMainId(pd.getReqMainId());
				pm.setReqParentId(pd.getReqParentId());
				pm.setRequirementName(pd.getRequirementName());
				pm.setRequirementDetails(pd.getRequirementDetails());
				pm.setReqInitiationId(pd.getReqInitiationId());			
				count = dao.ProjectOtherRequirementAdd(pm);
			}
		}
		return count;
	}
	@Override
	public Object[] OtherSubRequirementsDetails(ProjectOtherReqDto pd, String requirementId) throws Exception {
		// TODO Auto-generated method stub
		ProjectOtherReqModel pm= new ProjectOtherReqModel();
		Object[]OtherSubRequirementsDetails=null;
		if(!requirementId.equalsIgnoreCase("0")) {
			System.out.println(requirementId+"requirementId");
			 OtherSubRequirementsDetails=dao.OtherSubRequirementsDetails(requirementId);
		}
		return OtherSubRequirementsDetails;
	}
	@Override
	public long AddOtherRequirementDetails(String[] reqNames, String[] reqValue, String reqInitiationId) throws Exception {
		long count=0;
		for(int i=0;i<reqNames.length;i++) {
			ProjectOtherReqModel pm= new ProjectOtherReqModel();
//			pm.setInitiationId(Long.parseLong(initiationid));
			pm.setReqMainId(Long.parseLong(reqValue[i]));
			pm.setRequirementName(reqNames[i]);
			pm.setReqParentId(0l);
			pm.setIsActive(1);
//			pm.setProjectId(Long.parseLong(projectID));
			pm.setReqInitiationId(Long.parseLong(reqInitiationId));			
			count=dao.ProjectOtherRequirementAdd(pm);
		}
		return count;
	}
	@Override
	public List<Object[]> otherProjectRequirementList(String initiationid, String projectId) throws Exception {
		// To get main requirementList 
		return dao.otherProjectRequirementList(initiationid,projectId);
	}
	@Override
	public List<Object[]> getAllOtherrequirementsByInitiationId(String initiationid) throws Exception {
		return dao.getAllOtherReqByInitiationId(initiationid);
	}
//	@Override
//	public Object[] RequirementIntro(String initiationid) throws Exception {
//		// TODO Auto-generated method stub
//		return dao.RequirementIntro(initiationid);
//	}
//	@Override
//	public long ReqIntroSubmit(String initiationid, String attributes, String details, String UserId) throws Exception {
//		
//		PfmsInitiationReqIntro pr= new PfmsInitiationReqIntro();
//		if(attributes.equalsIgnoreCase("Introduction")) {
//			pr.setIntroduction(details);
//		}else if(attributes.equalsIgnoreCase("Block Diagram")) {
//			pr.setSystemBlockDiagram(details);
//		}else if(attributes.equalsIgnoreCase("System Overview")) {
//			pr.setSystemOverview(details);
//		}else if(attributes.equalsIgnoreCase("Document Overview")) {
//			pr.setDocumentOverview(details);
//		}else if(attributes.equalsIgnoreCase("Applicable Standards")) {
//			pr.setApplicableStandards(details);
//		}
//		pr.setInitiationId(Long.parseLong(initiationid));
//		pr.setCreatedBy(UserId);
//		pr.setCreatedDate(sdf1.format(new Date()));
//		pr.setIsActive(1);
//		return dao.ReqIntroSubmit(pr);
//	}
//	
//	
//	@Override
//	public long reqIntroUpdate(String initiationid, String attributes, String details, String userId) throws Exception {
//		PfmsInitiationReqIntro pr= new PfmsInitiationReqIntro();
//		if(attributes.equalsIgnoreCase("Introduction")) {
//			pr.setIntroduction(details);
//		}else if(attributes.equalsIgnoreCase("Block Diagram")) {
//			pr.setSystemBlockDiagram(details);
//		}else if(attributes.equalsIgnoreCase("System Overview")) {
//			pr.setSystemOverview(details);
//		}else if(attributes.equalsIgnoreCase("Document Overview")) {
//			pr.setDocumentOverview(details);
//		}else if(attributes.equalsIgnoreCase("Applicable Standards")) {
//			pr.setApplicableStandards(details);
//		}
//		pr.setInitiationId(Long.parseLong(initiationid));
//		pr.setModifiedBy(userId);
//		pr.setModifiedDate(sdf1.format(new Date()));
//		return dao.ReqIntroUpdate(pr,attributes);
//	}
	@Override
	public Object[] RequirementIntro(String reqInitiationId) throws Exception {
		// TODO Auto-generated method stub
		return dao.RequirementIntro(reqInitiationId);
	}
	@Override
	public long ReqIntroSubmit(String reqInitiationId, String attributes, String details, String UserId) throws Exception {
		
		PfmsInitiationReqIntro pr= new PfmsInitiationReqIntro();
		if(attributes.equalsIgnoreCase("Introduction")) {
			pr.setIntroduction(details);
		}else if(attributes.equalsIgnoreCase("Block Diagram")) {
			pr.setSystemBlockDiagram(details);
		}else if(attributes.equalsIgnoreCase("System Overview")) {
			pr.setSystemOverview(details);
		}else if(attributes.equalsIgnoreCase("Document Overview")) {
			pr.setDocumentOverview(details);
		}else if(attributes.equalsIgnoreCase("Applicable Standards")) {
			pr.setApplicableStandards(details);
		}
//		if(ProjectId==null) {
//			ProjectId="0";
//		}
//		if(initiationid==null) {
//			initiationid="0";
//		}
//		pr.setProjectId(Long.parseLong(ProjectId));
//		pr.setInitiationId(Long.parseLong(initiationid));
		pr.setReqInitiationId(Long.parseLong(reqInitiationId));		
		pr.setCreatedBy(UserId);
		pr.setCreatedDate(sdf1.format(new Date()));
		pr.setIsActive(1);
		return dao.ReqIntroSubmit(pr);
	}
	
	
	@Override
	public long reqIntroUpdate(String reqInitiationId, String attributes, String details, String userId) throws Exception {
		PfmsInitiationReqIntro pr= new PfmsInitiationReqIntro();
		if(attributes.equalsIgnoreCase("Introduction")) {
			pr.setIntroduction(details);
		}else if(attributes.equalsIgnoreCase("Block Diagram")) {
			pr.setSystemBlockDiagram(details);
		}else if(attributes.equalsIgnoreCase("System Overview")) {
			pr.setSystemOverview(details);
		}else if(attributes.equalsIgnoreCase("Document Overview")) {
			pr.setDocumentOverview(details);
		}else if(attributes.equalsIgnoreCase("Applicable Standards")) {
			pr.setApplicableStandards(details);
		}
//		if(ProjectId==null) {
//			ProjectId="0";
//		}
//		if(initiationid==null) {
//			initiationid="0";
//		}
//		pr.setProjectId(Long.parseLong(ProjectId));
//		pr.setInitiationId(Long.parseLong(initiationid));
		pr.setReqInitiationId(Long.parseLong(reqInitiationId));	
		pr.setModifiedBy(userId);
		pr.setModifiedDate(sdf1.format(new Date()));
		return dao.ReqIntroUpdate(pr,attributes);
	}
	
	@Override
	public Long ReqForwardProgress(String reqInitiationId) throws Exception {
		return dao.ReqForwardProgress(reqInitiationId);
	}
@Override
public Object[] SqrFiles(String reqInitiationId) throws Exception {
	
	return dao.SqrFiles(reqInitiationId);
}

@Override
public long RequirementParaSubmit(RequirementparaModel rpm) throws Exception {

	return dao.RequirementParaSubmit(rpm);
}
@Override
public List<Object[]> ReqParaDetails(String reqInitiationId) throws Exception {
	return dao.ReParaDetails(reqInitiationId);
}
@Override
public long RequirementParaEdit(RequirementparaModel rpm) throws Exception {
	return dao.RequirementParaEdit(rpm);	
}
@Override
public Long insertRequirement(PfmsOtherReq pr) throws Exception {
	return dao.insertRequirement(pr);
}
@Override
public Long insertReqType(ProjectRequirementType pt) throws Exception {
	return dao.insertReqType(pt);
}
@Override
public List<Object[]> getVerificationList(String reqInitiationId) throws Exception {
	return dao.getVerificationList(reqInitiationId);
}
@Override
public long insertRequirementVerification(RequirementVerification rv) throws Exception {
	return dao.insertRequirementVerification(rv);
}
	@Override
	public Long updateRequirementVerification(RequirementVerification rv) throws Exception {
		return dao.updateRequirementVerification(rv);
	}
@Override
public long updateRequirementVerificationDetails(RequirementVerification rv) throws Exception {
	return dao.updateRequirementVerificationDetails(rv);
}

@Override
public List<Object[]> getAbbreviationDetails(String reqInitiationId) throws Exception {
	return dao.getAbbreviationDetails(reqInitiationId);
}
@Override
public long addAbbreviation(List<InitiationAbbreviations> iaList) throws Exception {
	return dao.addAbbreviation(iaList);
}
@Override
public long addReqAppendix(PfmsInitiationAppendix pia) throws Exception {
	return dao.addReqAppendix(pia);
}
@Override
public List<Object[]> AppendixList(String initiationid) throws Exception {
	
	return dao.AppendixList(initiationid);
}
@Override
public long addReqAcronyms(List<RequirementAcronyms> raList) throws Exception {
	return dao.addReqAcronyms(raList);
}
@Override
public List<Object[]> AcronymsList(String reqInitiationId) throws Exception {
	return dao.getAcronymsList(reqInitiationId);
}
@Override
public long addReqPerformanceParameters(List<RequirementPerformanceParameters> raList) throws Exception {
	return dao.addReqPerformanceParameters(raList);
}
@Override
public List<Object[]> getPerformanceList(String reqInitiationId) throws Exception {
	return dao.getPerformanceList(reqInitiationId);
}
@Override
public Object[] getVerificationExcelData(String initiationid,String ProjectId) throws Exception {
	return dao.getVerificationExcelData(initiationid,ProjectId);
}
@Override
public List<Object[]> EmployeeList(String labCode, String reqInitiationId) throws Exception {
	return dao.EmployeeList(labCode,reqInitiationId);
}

@Override
public long AddreqMembers(RequirementMembers rm) throws Exception {
	
	int numberOfPersons= rm.getEmps().length; 
	
	String []assignee= rm.getEmps();
	long count=0;
	for(int i=0;i<numberOfPersons;i++) {
		RequirementMembers r = new RequirementMembers();
//		r.setProjectId(rm.getProjectId());
//		r.setInitiationId(rm.getInitiationId());
		r.setReqInitiationId(rm.getReqInitiationId());		
		r.setCreatedBy(rm.getCreatedBy());
		r.setCreatedDate(rm.getCreatedDate());
		r.setEmpId(Long.parseLong(assignee[i]));
		r.setIsActive(1);
		
		count=dao.AddreqMembers(r);
		;
	}
	
	return count;
}
		@Override
		public List<Object[]> reqMemberList(String reqInitiationId) throws Exception {
			
			return dao.reqMemberList(reqInitiationId);
		}
		@Override
		public long addReqSummary(RequirementSummary rs) throws Exception {
			return dao.addReqSummary(rs);
		}
		
		@Override
		public List<Object[]> getDocumentSummary(String reqInitiationId) throws Exception {
			return dao.getDocumentSummary(reqInitiationId);
		}
		@Override
		public long editreqSummary(RequirementSummary rs) throws Exception {
			return dao.editreqSummary(rs);
		}
		@Override
		public Object[] DocTempAttributes() throws Exception {
			 
			return dao.DocTempAttributes();
		}
		@Override
		public List<Object[]> ReqParaDetailsMain(String reqInitiationId) throws Exception {
			// TODO Auto-generated method stub
			return dao.ReParaDetailsMain(reqInitiationId);
		}
		@Override
		public List<Object[]> getVerificationListMain(String reqInitiationId) throws Exception {
			return dao.getVerificationListMain(reqInitiationId);
		}
		@Override
		public List<Object[]> VPDetails(String reqInitiationId) throws Exception {
			return dao.VPDetails(reqInitiationId);
		}
		@Override
		public List<Object[]> EmployeeList1(String labCode, String testPlanInitiationId,String SpecsInitiationId) throws Exception {
			return dao.EmployeeList1(labCode,testPlanInitiationId,SpecsInitiationId);
		}

		@Override
		public Object[] getProjectDetails(String labcode, String projectId, String projectType) throws Exception {
			
			return dao.getProjectDetails(labcode, projectId, projectType);
		}

		@Override
		public List<Object[]> ProjectDetailesData(long initiationId) throws Exception {
			return dao.ProjectDetailesData(initiationId);
		}
		@Override
		public List<Object[]> initiationSpecList(String projectId, String mainId, String initiationId) throws Exception {
			
			return dao.initiationSpecList(projectId, mainId, initiationId);
		}
		
		@Override
		public long addSpecificationContents(SpecificationContent sc) throws Exception {
			return dao.addSpecificationContents(sc);
		}
		
		@Override
		public List<Object[]> SpecContentsDetails(String specsInitiationId) throws Exception {
			
			return dao.SpecContentsDetails(specsInitiationId);
		}
		@Override
		public SpecificationContent getSpecificationContent(String contentid) throws Exception {
			return dao.getSpecificationContent(contentid);
		}
		
		@Override
		public long addSpecification(Specification specs) throws Exception {
			// TODO Auto-generated method stub
			return dao.addSpecification(specs);
		}
		
		@Override
		public Specification getSpecificationData(String specsId) throws Exception {
			return dao.getSpecificationData(specsId);
		}
		@Override
		public long addSpecificationIntro(SpecificationIntro s) throws Exception {
			return dao.addSpecificationIntro(s);
		}
		
		@Override
		public List<Object[]> getSpecsIntro(String specsInitiationId) throws Exception {
			return dao.getSpecsIntro(specsInitiationId);
		}
		
		@Override
		public long editSpecificationIntro(SpecificationIntro s) throws Exception {
			
			return dao.editSpecificationIntro(s);
		}
		
		@Override
		public long uploadProductTree(SpecifcationProductTree s, String LabCode) throws Exception {
			//String Path = LabCode + "\\SpecificationProducTree\\";
			Path path = Paths.get(LabCode, "SpecificationProducTree");
			Path treePath = Paths.get(uploadpath,LabCode,"SpecificationProducTree");
			long count=0l;
			if(!s.getFile().isEmpty()) {
				s.setFilesPath(path.toString());
				saveFile1(treePath, s.getFile().getOriginalFilename(), s.getFile());
				s.setIsactive(1);
				s.setImageName(s.getFile().getOriginalFilename());
				count=dao.uploadProductTree(s);
				}
			
			return count;
		}
		
		@Override
		public List<Object[]> SpecProducTreeDetails(String specsInitiationId) throws Exception {
			return dao.SpecProducTreeDetails(specsInitiationId);
		}

		@Override
		public long projectScheduleAllPeriodEditSubmit(String initiationId) throws Exception {
			try {
				PfmsInitiation initiation = dao.getPfmsInitiationById(initiationId);
				
				List<PfmsInitiationSchedule> scheduleList = dao.IntiationScheduleList(initiationId);
				for(PfmsInitiationSchedule schedule : scheduleList) {
					schedule.setStartDate(initiation.getStartDate());
					schedule.setEndDate(initiation.getStartDate()!=null?LocalDate.parse(initiation.getStartDate()).plusMonths(schedule.getMilestoneMonth()).toString():null);
					
					dao.ProjectScheduleEdit(schedule);
				}
				return 1L;
			}catch (Exception e) {
				e.printStackTrace();
				return 0L;
			}
			
		}
		@Override
		public Object[] getParaDetails(String paraid) throws Exception {
			return dao.getParaDetails(paraid);
		}
		
		@Override
		public long addInitiationMilestone(PfmsInitiationMilestone ms) throws Exception {
			return dao.addInitiationMilestone(ms);
		}
		
		@Override
		public List<Object[]> getInitiatedMilestoneDetails(String initiationid) throws Exception {
			return dao.getInitiatedMilestoneDetails(initiationid);
		}
		
		@Override
		public PfmsInitiationMilestone getInitiationMilestone(long initiationMilestoneId) throws Exception {
			return dao.getInitiationMilestone(initiationMilestoneId);
		}
		
		@Override
		public long editInitiationMilestone(PfmsInitiationMilestone ms) throws Exception {
			return dao.editInitiationMilestone(ms);
		}
		
		@Override
		public long addInitiationMilestoneRev(PfmsInitiationMilestoneRev entityRev) throws Exception {
			return dao.addInitiationMilestoneRev(entityRev);
		}
		
		@Override
		public DocMembers getDocMemberById(Long MemeberId) throws Exception
		{
			return dao.getDocMemberById(MemeberId);
		}
		
		@Override
		public long editDocMember(DocMembers idm) throws Exception
		{
			return dao.editDocMember(idm);
		}
		
		@Override
		public long UpdateInitiationReqMembers(long ReqMemberId) throws Exception {
			return dao.UpdateInitiationReqMembers(ReqMemberId);
		}
		
		@Override
		public List<Object[]> getsystemSpecificationList(String productTreeMainId) throws Exception {
		
			return dao.getsystemSpecificationList(productTreeMainId);
		}
		
		//srikant 
		@Override
		public List<Object[]> PlatformList() {
			return  dao.PlatformList();
		}
		
		@Override
		public List<Object[]> ProjectReviseList(String ProjectId) throws Exception{
			
			return dao.ProjectReviseList(ProjectId);
		}
		
		@Override
		public Object[] ProjectOriginalData(String ProjectId) throws Exception {

			return dao.ProjectOriginalData(ProjectId);
		}
		
		//srikant
		@Override
		public PlatformMaster getPlatformByPlatformId(long platformId) throws Exception {
			
			return dao.getPlatformByPlatformId(platformId);
		}
		//29-04-2025
		@Override
		public PfmsInitiationApproval getPfmsInitiationApprovalById(String enoteId) throws Exception {
			return dao.getPfmsInitiationApprovalById(enoteId);
		}
		
		@Override
		public long savePfmsInitiationApproval(PfmsInitiationApproval pe) throws Exception {
		
			return dao.savePfmsInitiationApproval(pe);
		}
		
		@Override
		public Object[] InitiationApprovalData(String initiationid) throws Exception {
			
			return dao.InitiationApprovalData(initiationid);
		}
		
		@Override
		public long InitiationForward(PfmsInitiationApproval pe, String remarks, Long empId, String flow, String username)
		throws Exception {
			try {
			Long ENoteId=pe.getEnoteId();
			String EnoteStatusCode = pe.getEnoteStatusCode();
			String EnoteStatusCodeNext = pe.getEnoteStatusCodeNext();
			Long NotifyEmpId=0l;
			List<String> forwardstatus = Arrays.asList("INI","RR1","RR2","RR3","RR4","RR5","RAP","REV");
			List<String> reforwardstatus = Arrays.asList("RR1","RR2","RR3","RR4","RR5","RAP");
			
			
			if(flow.equalsIgnoreCase("A")) {
				if(EnoteStatusCodeNext.equalsIgnoreCase("APR")) {
					EnoteStatusCode="APR";
					EnoteStatusCodeNext="APR";
					pe.setEnoteStatusCode(EnoteStatusCode);
					pe.setEnoteStatusCodeNext(EnoteStatusCodeNext);
				
				}else if(forwardstatus.contains(EnoteStatusCode)) {
					EnoteStatusCode="FWD";
					EnoteStatusCodeNext="RC1";
					pe.setEnoteStatusCode(EnoteStatusCode);
					pe.setEnoteStatusCodeNext(EnoteStatusCodeNext);
				}
				else if(EnoteStatusCode.equalsIgnoreCase("FWD")) {
					EnoteStatusCode="RC1";
					if(pe.getRecommend2()!=null) {
					EnoteStatusCodeNext="RC2";
					}else if(pe.getRecommend3()!=null) {
						EnoteStatusCodeNext="RC3";
					}else {
						EnoteStatusCodeNext="APR";
					}
					pe.setEnoteStatusCode(EnoteStatusCode);
					pe.setEnoteStatusCodeNext(EnoteStatusCodeNext);
				}
				else if(EnoteStatusCode.equalsIgnoreCase("RC1")) {
					EnoteStatusCode="RC2";
					if(pe.getRecommend3()!=null) {
						EnoteStatusCodeNext="RC3";
						}else  {
							EnoteStatusCodeNext="APR";
						}
					pe.setEnoteStatusCode(EnoteStatusCode);
					pe.setEnoteStatusCodeNext(EnoteStatusCodeNext);
				}
				else if(EnoteStatusCode.equalsIgnoreCase("RC2")) {
					EnoteStatusCode="RC3";
					EnoteStatusCodeNext="APR";
					pe.setEnoteStatusCode(EnoteStatusCode);
					pe.setEnoteStatusCodeNext(EnoteStatusCodeNext);	
				}
				
			}
			else if(flow.equalsIgnoreCase("REV")) {
				EnoteStatusCode="REV";
				EnoteStatusCodeNext="INI";
				pe.setEnoteStatusCode(EnoteStatusCode);
				pe.setEnoteStatusCodeNext(EnoteStatusCodeNext);
			}
			else {
				if(pe.getEnoteStatusCode().equalsIgnoreCase("FWD")) {
					EnoteStatusCode="RR1";
				}
				else if(pe.getEnoteStatusCode().equalsIgnoreCase("RC1")) {
					if(pe.getEnoteStatusCodeNext().equalsIgnoreCase("RC2")) {
					EnoteStatusCode="RR2";
					}else if(pe.getEnoteStatusCodeNext().equalsIgnoreCase("RC3")) {
						EnoteStatusCode="RR3";
					}else {
						EnoteStatusCode="RAP";	
					}
				}
				else if(pe.getEnoteStatusCode().equalsIgnoreCase("RC2")) {
					if(pe.getEnoteStatusCodeNext().equalsIgnoreCase("RC3")) {
						EnoteStatusCode="RR3";
						}else if(pe.getEnoteStatusCodeNext().equalsIgnoreCase("APR")) {
							EnoteStatusCode="RAP";
						}
				}
				else if(pe.getEnoteStatusCode().equalsIgnoreCase("RC3")) {
					EnoteStatusCode="RAP";
				}
				EnoteStatusCodeNext="INI";
				pe.setEnoteStatusCode(EnoteStatusCode);
				pe.setEnoteStatusCodeNext(EnoteStatusCodeNext);
			}
			
			
			pe.setModifiedBy(username);
			pe.setModifiedDate(sdf1.format(new Date()));
			
			dao.savePfmsInitiationApproval(pe);
			
			
			PmsInitiationApprovalTransaction pt = PmsInitiationApprovalTransaction.builder()
													.EnoteId(ENoteId)
													.EnoteStatusCode(EnoteStatusCode)
													.Remarks(remarks)
													.ActionBy(empId)
													.ActionDate(sdf1.format(new Date()))
													.build();
			
			long result=dao.savePmsInitiationApprovalTransaction(pt);
			
			String NotificationBy= admindao.EmployeeData(empId.toString())[3].toString();
			
			PfmsNotification notification=new PfmsNotification();
			if(flow!=null && flow.equalsIgnoreCase("A")) {
				String  nextcode = pe.getEnoteStatusCodeNext();
				String cuurentCode = pe.getEnoteStatusCode();
				if(nextcode.equalsIgnoreCase("RC1")) {
					notification.setIsActive(1);
					notification.setEmpId(pe.getRecommend1());
					notification.setNotificationMessage("Initiation Project Forwarded by "+NotificationBy +" for recommendation ! ");
					notification.setNotificationUrl("InitiationApprovalList.htm");
				}else if(nextcode.equalsIgnoreCase("RC2")) {
					notification.setIsActive(1);
					notification.setEmpId(pe.getRecommend2());
					notification.setNotificationMessage("Initiation Project Recommended by "+NotificationBy);
					notification.setNotificationUrl("InitiationApprovalList.htm");
				}else if(nextcode.equalsIgnoreCase("RC3")) {
					notification.setIsActive(1);
					notification.setEmpId(pe.getRecommend3());
					notification.setNotificationMessage("Initiation Project Recommended by "+NotificationBy+ " for Approval !");
					notification.setNotificationUrl("InitiationApprovalList.htm");
				}			
				else if(cuurentCode.equalsIgnoreCase("APR") && nextcode.equalsIgnoreCase("APR")) {
					notification.setIsActive(1);
					notification.setEmpId(pe.getInitiatedBy());
					notification.setNotificationMessage("Initiation Project is Approved by "+NotificationBy);
					notification.setNotificationUrl("ProjectIntiationList.htm");
				}
				carsdao.addNotifications(notification);
				}
			else if(flow.equalsIgnoreCase("R")){
				notification.setEmpId(pe.getInitiatedBy());
				notification.setNotificationMessage("Project Returned for checking by "+NotificationBy);
				notification.setNotificationUrl("ProjectIntiationList.htm");
				notification.setNotificationby(empId);
				notification.setIsActive(1);
				notification.setCreatedBy(username);
				notification.setCreatedDate(sdf1.format(new Date()));
				notification.setNotificationDate(sdf1.format(new Date()));
				carsdao.addNotifications(notification);
			}
			
			
			
			return result;
			
			
			}
			catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
			
		
		}
		
		
		@Override
		public List<Object[]> initiationPendingList(long empId) throws Exception {
			
			return dao.initiationPendingList(empId);
		}
		
		@Override
		public Object[] NewApprovalList(String enoteId) throws Exception {
			
			return dao.NewApprovalList(enoteId);
		}
		
		@Override
		public List<Object[]> EnoteTransactionList(String enoteTrackId) throws Exception {
			
			return dao.EnoteTransactionList(enoteTrackId);
		}
		
		@Override
		public List<Object[]> initiationApprovalList(long empId, String fromDate, String toDate) throws Exception {
			return dao.initiationApprovalList(empId,fromDate,toDate);
		}
		
		
		@Override
		public List<Object[]> InitiationAprrovalPrintDetails(long enoteid) throws Exception {
			List<Object[]>epdetails =  dao.InitiationAprrovalPrintDetails(enoteid);
			
//			List<String>status = Arrays.asList("FWD","RFD","RC1","RC2","RC3","RC4","RC5","APR");
//			
//			
//			List<Object[]>newList= new ArrayList<>();
//			
//			for(String s:status) {
//				
//				List<Object[]>subList = epdetails.stream().filter(e->e[8].toString().equalsIgnoreCase(s)).collect(Collectors.toList());
//				
//				if(subList!=null && subList.size()>0) {
//					
//					newList.add(subList.get(subList.size()-1));
//				}
//				
//			}
			
			return epdetails;
			
		}

	@Override
	public List<Object[]> getProjectTeamListByLabCode(String labCode, String projectId) {

		return dao.getProjectTeamListByLabCode(labCode, projectId);
	}

	@Override
	public List<RoleMaster> getRoleMasterList() throws Exception {
		
		return dao.getRoleMasterList();
	}
	
	@Override
	public ProjectAssign getProjectAssignById(String projectEmployeeId) throws Exception {
		
		return dao.getProjectAssignById(projectEmployeeId);
	}
	
	@Override
	public Long getProjectEmployeeIdByProjectId(String projectId, String roleMasterId) throws Exception {
		
		return dao.getProjectEmployeeIdByProjectId(projectId, roleMasterId);
	}
	
}
