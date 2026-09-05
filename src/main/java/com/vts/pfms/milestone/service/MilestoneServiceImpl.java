package com.vts.pfms.milestone.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.vts.pfms.FormatConverter;
import com.vts.pfms.Zipper;
import com.vts.pfms.committee.dao.ActionDao;
import com.vts.pfms.committee.dao.CommitteeDao;
import com.vts.pfms.committee.model.ActionAssign;
import com.vts.pfms.committee.model.CommitteeScheduleAgendaDocs;
import com.vts.pfms.committee.model.PfmsNotification;
import com.vts.pfms.milestone.dao.MilestoneDao;
import com.vts.pfms.milestone.dto.FileDocAmendmentDto;
import com.vts.pfms.milestone.dto.FileProjectDocDto;
import com.vts.pfms.milestone.dto.FileUploadDto;
import com.vts.pfms.milestone.dto.InfrastructureItemDto;
import com.vts.pfms.milestone.dto.MileEditDto;
import com.vts.pfms.milestone.dto.MilestoneActivityDto;
import com.vts.pfms.milestone.dto.MilestoneScheduleDto;
import com.vts.pfms.milestone.dto.ProjectEconomicImpactDto;
import com.vts.pfms.milestone.dto.ProjectInfrastructureUtilizationDto;
import com.vts.pfms.milestone.dto.ProjectManPowerUtilizationDto;
import com.vts.pfms.milestone.dto.ProjectTrainingUtilizationDto;
import com.vts.pfms.milestone.dto.ProjectUtilizationBriefingDto;
import com.vts.pfms.milestone.dto.RevisionItemDto;
import com.vts.pfms.milestone.dto.RevisionSummaryDto;
import com.vts.pfms.milestone.dto.TrainingItemDto;
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
import com.vts.pfms.milestone.model.MilestoneActivityRev;
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
import com.vts.pfms.project.dao.ProjectDao;

import jakarta.transaction.Transactional;

@Service
public class MilestoneServiceImpl implements MilestoneService {

	@Autowired
	MilestoneDao dao;
	
	@Autowired
	ProjectDao projectDao;
	
	@Autowired
	CommitteeDao committeDao;
	
	@Autowired
	ActionDao actionDao;
	
	FormatConverter fc=new FormatConverter();
	private static final Logger logger=LogManager.getLogger(MilestoneServiceImpl.class);
	
	@Value("${ApplicationFilesDrive}")
    private String FilePath;
	
	private  SimpleDateFormat rdf=fc.getRegularDateFormat();
	private  SimpleDateFormat sdf=fc.getSqlDateFormat();
	private  SimpleDateFormat sdtf=fc.getSqlDateAndTimeFormat();
	private  SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

	
	@Override
	public List<Object[]> MilestoneActivityList(String ProjectId) throws Exception {
		
		return dao.MilestoneActivityList(ProjectId);
	}

	@Override
	public List<Object[]> ProjectList() throws Exception {
		
		return dao.ProjectList();
	}

	@Override
	public List<Object[]> EmployeeList() throws Exception 
	{		
		return dao.EmployeeList();
	}

	@Override
	public long MilestoneActivityInsert(MilestoneActivityDto dto) throws Exception {
		logger.info(new Date() +"Inside SERVICE MilestoneActivityInsert ");
		MilestoneActivity Milestone=new MilestoneActivity();
		Milestone.setProjectId(Long.parseLong(dto.getProjectId()));
		Milestone.setActivityType(Long.parseLong(dto.getActivityType()));
		Milestone.setActivityName(dto.getActivityName());
		Milestone.setActivityStatusId(0);
		Milestone.setMilestoneNo(dao.MilestoneCount(dto.getProjectId())+1);
		Milestone.setStartDate(new java.sql.Date(fc.getRegularDateFormat().parse(dto.getStartDate()).getTime()));
		Milestone.setEndDate(new java.sql.Date(fc.getRegularDateFormat().parse(dto.getEndDate()).getTime()));
		Milestone.setOrgStartDate(new java.sql.Date(fc.getRegularDateFormat().parse(dto.getStartDate()).getTime()));
		Milestone.setOrgEndDate(new java.sql.Date(fc.getRegularDateFormat().parse(dto.getEndDate()).getTime()));
		Milestone.setOicEmpId(Long.parseLong(dto.getOicEmpId()));
		Milestone.setOicEmpId1(Long.parseLong(dto.getOicEmpId1()));
		Milestone.setCreatedBy(dto.getCreatedBy());
		Milestone.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
		Milestone.setProgressStatus(0);
		Milestone.setWeightage(0);
		Milestone.setIsActive(1);
		Milestone.setLoading(0);
		return dao.MilestoneActivity(Milestone);
	}

	@Override
	public List<Object[]> MilestoneActivity(String MilestoneActivityId) throws Exception {
		
		return dao.MilestoneActivity(MilestoneActivityId);
	}

	@Override
	public long MilestoneActivityLevelInsert(MilestoneActivityDto dto) throws Exception {
		logger.info(new Date() +"Inside SERVICE MilestoneActivityLevelInsert ");
		MilestoneActivityLevel Milestone=new MilestoneActivityLevel();
		Milestone.setParentActivityId(Long.parseLong(dto.getActivityId()));
		Milestone.setActivityLevelId(Long.parseLong(dto.getLevelId()));
		Milestone.setActivityName(dto.getActivityName());
		Milestone.setActivityType(Long.parseLong(dto.getActivityType()));
		Milestone.setActivityStatusId(0);
		Milestone.setStartDate(new java.sql.Date(fc.getRegularDateFormat().parse(dto.getStartDate()).getTime()));
		Milestone.setEndDate(new java.sql.Date(fc.getRegularDateFormat().parse(dto.getEndDate()).getTime()));
		Milestone.setOrgStartDate(new java.sql.Date(fc.getRegularDateFormat().parse(dto.getStartDate()).getTime()));
		Milestone.setOrgEndDate(new java.sql.Date(fc.getRegularDateFormat().parse(dto.getEndDate()).getTime()));
		Milestone.setOicEmpId(Long.parseLong(dto.getOicEmpId()));
		Milestone.setOicEmpId1(Long.parseLong(dto.getOicEmpId1()));
		Milestone.setRevision(Long.parseLong("0"));
		Milestone.setWeightage(0);
		Milestone.setCreatedBy(dto.getCreatedBy());
		Milestone.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
		Milestone.setProgressStatus(0);
		Milestone.setWeightage(0);
		Milestone.setIsActive(1);
		Milestone.setPoint5("Y");
		Milestone.setPoint6("Y");
		Milestone.setPoint9("Y");
		Milestone.setLoading(0);
		Milestone.setIsMasterData("N");
		Milestone.setLinkedMilestonId(0l);
		return dao.MilestoneActivityLevelInsert(Milestone);
	}
	

	@Override
	public List<Object[]> MilestoneActivityLevel(String MilestoneActivityId,String LevelId) throws Exception {
		
		return dao.MilestoneActivityLevel(MilestoneActivityId,LevelId);
	}



	@Override
	public int MilestoneRevisionCount(String MileActivityId) throws Exception {
		int count=0;
		logger.info(new Date() +"Inside SERVICE MilestoneRevisionCount ");
		try
		{
			count=dao.MilestoneRevisionCount(MileActivityId)+1;
		}catch (Exception e) {
			logger.error(new Date() +"Inside  MilestoneRevisionCount ",e);
			count=0;
		}
		return count;
	}

	@Override
	public long MilestoneActivityRevision(MilestoneActivityDto dto) throws Exception {
		long count=1;
		logger.info(new Date() +"Inside SERVICE MilestoneActivityRevision ");
		MilestoneActivity getMA=dao.MileActivityDetails(Long.parseLong(dto.getActivityId()));
		MilestoneActivityRev MaRev=new MilestoneActivityRev();
		MaRev.setActivityName(getMA.getActivityName());
		MaRev.setActivityStatusId(getMA.getActivityStatusId());
		MaRev.setMilestoneActivityId(getMA.getMilestoneActivityId());
		MaRev.setStartDate(getMA.getStartDate());
		MaRev.setEndDate(getMA.getEndDate());
		MaRev.setOicEmpId(getMA.getOicEmpId());
		MaRev.setOicEmpId1(getMA.getOicEmpId1());
		MaRev.setProgressStatus(getMA.getProgressStatus());
		MaRev.setWeightage(getMA.getWeightage());
		MaRev.setRevisionNo(Integer.parseInt(dto.getRevisionNo()));
		MaRev.setStatusRemarks(getMA.getStatusRemarks());
		MaRev.setCreatedBy(getMA.getCreatedBy());
		MaRev.setCreatedDate(LocalDateTime.now().toString());
		MaRev.setIsActive(1);
		long rev=dao.MilestoneActivityRev(MaRev);
		if(rev>0) {
    	List<MilestoneActivityLevel> listA=dao.ActivityLevelList(Long.parseLong(dto.getActivityId()),count);
		for(MilestoneActivityLevel MileA:listA) {
		MilestoneActivitySubRev SubA=new MilestoneActivitySubRev();
		SubA.setActivityName(MileA.getActivityName());
		SubA.setActivityId(MileA.getActivityId());
		SubA.setOicEmpId(MileA.getOicEmpId());
		SubA.setOicEmpId1(MileA.getOicEmpId1());
		SubA.setActivityType(MileA.getActivityType());
		SubA.setActivityStatusId(MileA.getActivityStatusId());
		SubA.setProgressStatus(MileA.getProgressStatus());
		SubA.setWeightage(MileA.getWeightage());
		SubA.setRevision(Long.parseLong(dto.getRevisionNo()));
		SubA.setStartDate(MileA.getStartDate());
		SubA.setEndDate(MileA.getEndDate());
		SubA.setCreatedBy(MileA.getCreatedBy());
		SubA.setCreatedDate(LocalDateTime.now().toString());
		SubA.setIsActive(1);
		dao.MilestoneActivitySubRev(SubA);
		List<MilestoneActivityLevel> listB=dao.ActivityLevelList(MileA.getActivityId(),count+1);
		for(MilestoneActivityLevel MileB:listB) {
		MilestoneActivitySubRev SubB=new MilestoneActivitySubRev();
		SubB.setActivityName(MileB.getActivityName());
		SubB.setActivityId(MileB.getActivityId());
		SubB.setOicEmpId(MileB.getOicEmpId());
		SubB.setOicEmpId1(MileB.getOicEmpId1());
		SubB.setActivityType(MileB.getActivityType());
		SubB.setActivityStatusId(MileB.getActivityStatusId());
		SubB.setProgressStatus(MileB.getProgressStatus());
		SubB.setWeightage(MileB.getWeightage());
		SubB.setRevision(Long.parseLong(dto.getRevisionNo()));
		SubB.setStartDate(MileB.getStartDate());
		SubB.setEndDate(MileB.getEndDate());
		SubB.setCreatedBy(MileB.getCreatedBy());
		SubB.setCreatedDate(LocalDateTime.now().toString());
		SubB.setIsActive(1);
		dao.MilestoneActivitySubRev(SubB);
		List<MilestoneActivityLevel> listC=dao.ActivityLevelList(MileB.getActivityId(),count+2);
		for(MilestoneActivityLevel MileC:listC) {
		MilestoneActivitySubRev SubC=new MilestoneActivitySubRev();
		SubC.setActivityName(MileC.getActivityName());
		SubC.setActivityId(MileC.getActivityId());
		SubC.setOicEmpId(MileC.getOicEmpId());
		SubC.setOicEmpId1(MileC.getOicEmpId1());
		SubC.setActivityType(MileC.getActivityType());
		SubC.setActivityStatusId(MileC.getActivityStatusId());
		SubC.setProgressStatus(MileC.getProgressStatus());
		SubC.setWeightage(MileC.getWeightage());
		SubC.setRevision(Long.parseLong(dto.getRevisionNo()));
		SubC.setStartDate(MileC.getStartDate());
		SubC.setEndDate(MileC.getEndDate());
		SubC.setCreatedBy(MileC.getCreatedBy());
		SubC.setCreatedDate(LocalDateTime.now().toString());
		SubC.setIsActive(1);
		dao.MilestoneActivitySubRev(SubC);
		List<MilestoneActivityLevel> listD=dao.ActivityLevelList(MileC.getActivityId(),count+3);
		for(MilestoneActivityLevel MileD:listD) {
		MilestoneActivitySubRev SubD=new MilestoneActivitySubRev();
		SubD.setActivityName(MileD.getActivityName());
		SubD.setActivityId(MileD.getActivityId());
		SubD.setOicEmpId(MileD.getOicEmpId());
		SubD.setOicEmpId1(MileD.getOicEmpId1());
		SubD.setActivityType(MileD.getActivityType());
		SubD.setActivityStatusId(MileD.getActivityStatusId());
		SubD.setProgressStatus(MileD.getProgressStatus());
		SubD.setWeightage(MileD.getWeightage());
		SubD.setRevision(Long.parseLong(dto.getRevisionNo()));
		SubD.setStartDate(MileD.getStartDate());
		SubD.setEndDate(MileD.getEndDate());
		SubD.setCreatedBy(MileD.getCreatedBy());
		SubD.setCreatedDate(LocalDateTime.now().toString());
		SubD.setIsActive(1);
		dao.MilestoneActivitySubRev(SubD);
		List<MilestoneActivityLevel> listE=dao.ActivityLevelList(MileD.getActivityId(),count+4);
		for(MilestoneActivityLevel MileE:listE) {
		MilestoneActivitySubRev SubE=new MilestoneActivitySubRev();
		SubE.setActivityName(MileE.getActivityName());
		SubE.setActivityId(MileE.getActivityId());
		SubE.setOicEmpId(MileE.getOicEmpId());
		SubE.setOicEmpId1(MileE.getOicEmpId1());
		SubE.setActivityType(MileE.getActivityType());
		SubE.setActivityStatusId(MileE.getActivityStatusId());
		SubE.setProgressStatus(MileE.getProgressStatus());
		SubE.setWeightage(MileE.getWeightage());
		SubE.setRevision(Long.parseLong(dto.getRevisionNo()));
		SubE.setStartDate(MileE.getStartDate());
		SubE.setEndDate(MileE.getEndDate());
		SubE.setCreatedBy(MileE.getCreatedBy());
		SubE.setCreatedDate(LocalDateTime.now().toString());
		SubE.setIsActive(1);
		dao.MilestoneActivitySubRev(SubE);
		
		    dao.RevLevelUpdate(MileE.getActivityId().toString(), dto.getRevisionNo());
		    }
		    dao.RevLevelUpdate(MileD.getActivityId().toString(), dto.getRevisionNo());
		    }
			dao.RevLevelUpdate(MileC.getActivityId().toString(), dto.getRevisionNo());
			}
			dao.RevLevelUpdate(MileB.getActivityId().toString(), dto.getRevisionNo());
			}
			dao.RevLevelUpdate(MileA.getActivityId().toString(), dto.getRevisionNo());
			}
			dao.RevMainUpdate(dto.getActivityId(), dto.getRevisionNo());
     	}
		
		
		
		if(Integer.parseInt(dto.getRevisionNo())>=1) {
		System.out.println(dto.getActivityId()+"-----dto.getActivityId()");
		MileEditDto dto1 = new MileEditDto();
		dto1.setMilestoneActivityId(dto.getActivityId());
		
		updateMilestoneLevelProgress(dto1);
		}
		
		
		
		
		return rev;
	}

	@Async
	@Override
	public void updateMilestoneLevelProgress(MileEditDto dto)  {
		
		System.out.println("Inside ABCD--------------------------------");
		try {
			System.out.println(dto.getMilestoneActivityId()+"-----dto.getActivityId()");
		List<Object[]> BaselineMain=dao.BaseLineMain(dto.getMilestoneActivityId());
		double TotalA=0.00;
		for(Object[] objMain:BaselineMain) {
			List<Object[]> BaselineA=dao.BaseLineLevel(objMain[0].toString(),"1");
			for(Object[] objA:BaselineA) {
				List<Object[]> BaselineB=dao.BaseLineLevel(objA[0].toString(),"2");
				for(Object[] objB:BaselineB) {
					List<Object[]> BaselineC=dao.BaseLineLevel(objB[0].toString(),"3");
					
						for(Object[] objC:BaselineC) {
								List<Object[]> BaselineD=dao.BaseLineLevel(objC[0].toString(),"4");
								for(Object[] objD:BaselineD) {
									List<Object[]> BaselineE=dao.BaseLineLevel(objD[0].toString(),"5");
									if(BaselineE.size()>0) {
										double ProgressD=0.00;
										for(Object[] objE:BaselineE){
										ProgressD+=(Double.parseDouble(objE[3].toString())/100)*Double.parseDouble(objE[2].toString());
										}
										// status for D
										String StatusD=objD[5].toString();
										
		                                 // dao D update
										dao.ProgressLevel(objD[0].toString(), StatusD,(int)Math.round(ProgressD),dto);
										
									}
									
									}
								
							
							
						}	
					
					}
					
					
				
				}
			
		
		}
		//C
		for(Object[] objMain:BaselineMain) {
			List<Object[]> BaselineA=dao.BaseLineLevel(dto.getMilestoneActivityId(),"1");
			for(Object[] objA:BaselineA) {
				List<Object[]> BaselineB=dao.BaseLineLevel(objA[0].toString(),"2");
				for(Object[] objB:BaselineB) {
					List<Object[]> BaselineC=dao.BaseLineLevel(objB[0].toString(),"3");
					for(Object[] objC:BaselineC) {
						List<Object[]> BaselineD=dao.BaseLineLevel(objC[0].toString(),"4");
				if(BaselineD.size()>0) {
					double ProgressC=0.00;
				for(Object[] objD:BaselineD) {
					
						
						double ED=(Double.parseDouble(objD[3].toString())/100)*Double.parseDouble(objD[2].toString());
						ProgressC+=ED;
						
						
					
					}
				
				// status for C
				String StatusC=objC[5].toString();
				
				      dao.ProgressLevel(objC[0].toString(), StatusC,(int)Math.round(ProgressC),dto);
				      
                     // dao C update
				}
					}
				}
				}
			
		
		}
		//B
		for(Object[] objMain:BaselineMain) {
			List<Object[]> BaselineA=dao.BaseLineLevel(dto.getMilestoneActivityId(),"1");
			for(Object[] objA:BaselineA) {
				List<Object[]> BaselineB=dao.BaseLineLevel(objA[0].toString(),"2");
				for(Object[] objB:BaselineB) {
					List<Object[]> BaselineC=dao.BaseLineLevel(objB[0].toString(),"3");
				if(BaselineC.size()>0) {
					double ProgressB=0.00;
				for(Object[] objC:BaselineC) {
					
						
						double EC=(Double.parseDouble(objC[3].toString())/100)*Double.parseDouble(objC[2].toString());
						ProgressB+=EC;
						
						
					
					}
				
				// status for B
				String StatusB=objB[5].toString();
				
				dao.ProgressLevel(objB[0].toString(), StatusB,(int)Math.round(ProgressB),dto);
				      
                     // dao B update
				}
				
				}
				}
			
		
		}
		//A
		for(Object[] objMain:BaselineMain) {
			List<Object[]> BaselineA=dao.BaseLineLevel(dto.getMilestoneActivityId(),"1");
			for(Object[] objA:BaselineA) {
				List<Object[]> BaselineB=dao.BaseLineLevel(objA[0].toString(),"2");
				if(BaselineB.size()>0) {
					double ProgressA=0.00;
				for(Object[] objB:BaselineB) {
					
						double EB=(Double.parseDouble(objB[3].toString())/100)*Double.parseDouble(objB[2].toString());
						
						ProgressA+=EB;
						
						
					
					}
				
				// status for A
				String StatusA=objA[5].toString();
				
				      dao.ProgressLevel(objA[0].toString(), StatusA,(int)Math.round(ProgressA),dto);
				      
                     // dao A update
				}
				
				
				}
			
		
		}
		for(Object[] objMain:BaselineMain) {
			List<Object[]> BaselineA=dao.BaseLineLevel(dto.getMilestoneActivityId(),"1");
			for(Object[] objA:BaselineA) {
					TotalA+=(Double.parseDouble(objA[3].toString())/100)*Double.parseDouble(objA[2].toString());
				}
			
			// status for Main
			
			String StatusMain=objMain[5].toString();
			
			String DateOfCompletion = objMain[6]!=null?objMain[6].toString():null;
		
			dao.ProgressMain(dto.getMilestoneActivityId(), StatusMain,(int)Math.round(TotalA), DateOfCompletion,dto);
			// dao upadate main
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}

	@Override
	public List<Object[]> MilestoneActivityEdit(MileEditDto dto) throws Exception {
		logger.info(new Date() +"Inside  MilestoneActivityEdit ");
		List<Object[]> MileEditData=new ArrayList<Object[]>();
		if("M".equalsIgnoreCase(dto.getActivityType())) {
			MileEditData=dao.MilestoneActivityData(dto.getMilestoneActivityId());
		}
        if(!"M".equalsIgnoreCase(dto.getActivityType())) {
        	MileEditData=dao.ActivityLevelData(dto.getActivityId());
		}
        
		return MileEditData;
	}

	@Override
	public List<Object[]> ActivityTypeList() throws Exception {
		
		return dao.ActivityTypeList();
	}

	@Override
	public int MilestoneActivityUpdate(MileEditDto dto) throws Exception {
		try {


			logger.info(new Date() +"Inside  MilestoneActivityUpdate ");
			int result=0;
			dto.setCreatedDate(fc.getSqlDateFormat().format(new Date()));
			Date fdate = fc.getRegularDateFormat().parse(dto.getStartDate());
			Date tdate = fc.getRegularDateFormat().parse(dto.getEndDate());
			dto.setEndDate(fc.getSqlDateFormat().format(tdate));
			dto.setStartDate(fc.getSqlDateFormat().format(fdate));


			System.out.println("dto.getRevisionNo()--"+dto.getRevisionNo());
			System.out.println("dto.getActivityType()--"+dto.getActivityType());

			if("0".equalsIgnoreCase(dto.getRevisionNo())&&"M".equalsIgnoreCase(dto.getActivityType())) {
				result=dao.MilestoneActivityMainUpdate(dto);
			}
			if("0".equalsIgnoreCase(dto.getRevisionNo())&&!"M".equalsIgnoreCase(dto.getActivityType())) {
				result=dao.ActivityLevelFullEdit(dto);
			}

			if(!"0".equalsIgnoreCase(dto.getRevisionNo())&&"M".equalsIgnoreCase(dto.getActivityType())) {

				MilestoneActivity mileActivity = dao.MileActivityDetails(Long.parseLong(dto.getActivityId()));

				java.sql.Date startDate = new java.sql.Date(fdate.getTime());
				java.sql.Date endDate = new java.sql.Date(tdate.getTime());

				mileActivity.setStartDate(startDate);
				mileActivity.setEndDate(endDate);
				if(Integer.parseInt(dto.getRevisionNo())<3) {
					mileActivity.setOrgStartDate(startDate);
					mileActivity.setOrgEndDate(endDate);
				}
				mileActivity.setWeightage(Integer.parseInt(dto.getWeightage()));
				mileActivity.setModifiedBy(dto.getCreatedBy());
				mileActivity.setModifiedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
				mileActivity.setActivityName(dto.getActivityName());
				mileActivity.setOicEmpId(dto.getOicEmpId()!=null?Long.parseLong(dto.getOicEmpId()):0L);
				mileActivity.setOicEmpId1(dto.getOicEmpId1()!=null?Long.parseLong(dto.getOicEmpId1()):0L);
				dao.MilestoneActivity(mileActivity);
				result = 1;
			}
			if(!"0".equalsIgnoreCase(dto.getRevisionNo())&&!"M".equalsIgnoreCase(dto.getActivityType())) {
				MilestoneActivityLevel activityLevel = dao.getActivityLevelListById(dto.getActivityId());

				java.sql.Date startDate = new java.sql.Date(fdate.getTime());
				java.sql.Date endDate = new java.sql.Date(tdate.getTime());

				activityLevel.setStartDate(startDate);
				activityLevel.setEndDate(endDate);
				if(Integer.parseInt(dto.getRevisionNo())<3) {
					activityLevel.setOrgStartDate(startDate);
					activityLevel.setOrgEndDate(endDate);
				}
				activityLevel.setWeightage(Integer.parseInt(dto.getWeightage()));
				activityLevel.setModifiedBy(dto.getCreatedBy());
				activityLevel.setModifiedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
				activityLevel.setActivityName(dto.getActivityName());
				activityLevel.setOicEmpId(dto.getOicEmpId()!=null?Long.parseLong(dto.getOicEmpId()):0L);
				activityLevel.setOicEmpId1(dto.getOicEmpId1()!=null?Long.parseLong(dto.getOicEmpId1()):0L);
				
				dao.MilestoneActivityLevelInsert(activityLevel);
				result = 1;
			}

			return result;
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}	
	}

	@Override
	public List<Object[]> ActivityCompareMAin(String ActivityId, String Rev,String Rev1) throws Exception {
		
		return dao.ActivityCompareMAin(ActivityId, Rev,Rev1);
	}

	

	@Override
	public List<Object[]> MilestoneActivityEmpIdList(String EmpId) throws Exception {
		
		return dao.MilestoneActivityEmpIdList(EmpId);
	}

	@Override
	public List<Object[]> StatusList() throws Exception {
	
		return dao.StatusList();
	}
	private  SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
	@Override
	public int ActivityProgressUpdate(MileEditDto dto) throws Exception {
		logger.info(new Date() +"Inside  ActivityProgressUpdate ");
		try {
		int result=0;
		
		String progressdate= new java.sql.Date(dateFormat.parse(dto.getProgressDate()).getTime())+"";
		

		Date enddate = fc.getSqlDateFormat().parse(dto.getEndDate());
		Date progressDate=fc.getSqlDateFormat().parse(progressdate);
		
		LocalDate today = LocalDate.now();
		LocalDate endDateLocalDate = enddate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate progressLocalDate = progressDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		
		dto.setCreatedDate(fc.getSqlDateFormat().format(new Date()));
		if("100".equalsIgnoreCase(dto.getProgressStatus())) {
			dto.setDateOfCompletion(progressdate);
			if(endDateLocalDate.isEqual(progressLocalDate) || endDateLocalDate.isAfter(progressLocalDate)) {
				dto.setActivityStatusId("3");	
			}else {
				dto.setActivityStatusId("5");
			}
		}
		else if("0".equalsIgnoreCase(dto.getProgressStatus())) {
			dto.setActivityStatusId("1");
		}else {
			if(enddate.after(progressDate)) {
				dto.setActivityStatusId("2");
				}else {
					dto.setActivityStatusId("4");
				}
			}
		if("M".equalsIgnoreCase(dto.getActivityType())) {
			result=dao.ActivityProgressMainUpdate(dto);
		}

		  if(!"M".equalsIgnoreCase(dto.getActivityType())) {
			  result=dao.ActivityProgressUpdateLevel(dto);
			}

	        if(result>0) {
	        	String dt=fc.getRegularDateFormat().format(new Date());
	        	MilestoneActivitySub attach=new MilestoneActivitySub();
	        	attach.setActivityId(Long.parseLong(dto.getActivityId()));
	        	attach.setProgress(Integer.parseInt(dto.getProgressStatus()));
	        	attach.setProgressDate(new java.sql.Date(dateFormat.parse(dto.getProgressDate()).getTime()));
				attach.setAttachName(dto.getFileNamePath());
				attach.setAttachFile(dto.getFilePath());
				attach.setCreatedBy(dto.getCreatedBy());
				attach.setRemarks(dto.getStatusRemarks());
				attach.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
				attach.setIsActive(1);
				dao.MilestoneActivitySubInsert(attach);
				List<Object[]> BaselineMain=dao.BaseLineMain(dto.getMilestoneActivityId());
				double TotalA=0.00;
				for(Object[] objMain:BaselineMain) {
					List<Object[]> BaselineA=dao.BaseLineLevel(objMain[0].toString(),"1");
					for(Object[] objA:BaselineA) {
						List<Object[]> BaselineB=dao.BaseLineLevel(objA[0].toString(),"2");
						for(Object[] objB:BaselineB) {
							List<Object[]> BaselineC=dao.BaseLineLevel(objB[0].toString(),"3");
							
								for(Object[] objC:BaselineC) {
										List<Object[]> BaselineD=dao.BaseLineLevel(objC[0].toString(),"4");
										for(Object[] objD:BaselineD) {
											List<Object[]> BaselineE=dao.BaseLineLevel(objD[0].toString(),"5");
											if(BaselineE.size()>0) {
												double ProgressD=0.00;
												for(Object[] objE:BaselineE){
												ProgressD+=(Double.parseDouble(objE[3].toString())/100)*Double.parseDouble(objE[2].toString());
												}
												// status for D
												String StatusD="1";
												Date enddateD = fc.getSqlDateFormat().parse(dto.getEndDate());
												if(Math.round(ProgressD)>=100) {
													ProgressD=100.00;
													if(enddateD.after(progressDate)) { // taking progressdate in place of new Date()
														StatusD="3";
													}else {
														StatusD="5";
													}
													}
													else if(Math.round(ProgressD)==0) {
														StatusD="1";
													}else {
														if(enddateD.after(progressDate)) { // taking progressdate in place of new Date()
															StatusD="2";
															}else {
																StatusD="4";
															}
														}
				                                 // dao D update
												dao.ProgressLevel(objD[0].toString(), StatusD,(int)Math.round(ProgressD),dto);
												
											}
											
											}
										
									
									
								}	
							
							}
							
							
						
						}
					
				
				}
				//C
				for(Object[] objMain:BaselineMain) {
					List<Object[]> BaselineA=dao.BaseLineLevel(dto.getMilestoneActivityId(),"1");
					for(Object[] objA:BaselineA) {
						List<Object[]> BaselineB=dao.BaseLineLevel(objA[0].toString(),"2");
						for(Object[] objB:BaselineB) {
							List<Object[]> BaselineC=dao.BaseLineLevel(objB[0].toString(),"3");
							for(Object[] objC:BaselineC) {
								List<Object[]> BaselineD=dao.BaseLineLevel(objC[0].toString(),"4");
						if(BaselineD.size()>0) {
							double ProgressC=0.00;
						for(Object[] objD:BaselineD) {
							
								
								double ED=(Double.parseDouble(objD[3].toString())/100)*Double.parseDouble(objD[2].toString());
								ProgressC+=ED;
								
								
							
							}
						
						// status for C
						String StatusC="1";
						Date enddateC= fc.getSqlDateFormat().parse(dto.getEndDate());
						if(Math.round(ProgressC)>=100) {
							ProgressC=100.00;
							if(enddateC.after(progressDate)) { // taking progressdate in place of new Date()
								 StatusC="3";
							}else {
								 StatusC="5";
							}
							}
							else if(Math.round(ProgressC)==0) {
								 StatusC="1";
							}else {
								if(enddateC.after(progressDate)) { // taking progressdate in place of new Date()
									 StatusC="2";
									}else {
										 StatusC="4";
									}
								}
						      dao.ProgressLevel(objC[0].toString(), StatusC,(int)Math.round(ProgressC),dto);
						      
                             // dao C update
						}
							}
						}
						}
					
				
				}
				//B
				for(Object[] objMain:BaselineMain) {
					List<Object[]> BaselineA=dao.BaseLineLevel(dto.getMilestoneActivityId(),"1");
					for(Object[] objA:BaselineA) {
						List<Object[]> BaselineB=dao.BaseLineLevel(objA[0].toString(),"2");
						for(Object[] objB:BaselineB) {
							List<Object[]> BaselineC=dao.BaseLineLevel(objB[0].toString(),"3");
						if(BaselineC.size()>0) {
							double ProgressB=0.00;
						for(Object[] objC:BaselineC) {
							
								
								double EC=(Double.parseDouble(objC[3].toString())/100)*Double.parseDouble(objC[2].toString());
								ProgressB+=EC;
								
								
							
							}
						
						// status for B
						String StatusB="1";
						Date enddateB = fc.getSqlDateFormat().parse(dto.getEndDate());
						if(Math.round(ProgressB)>=100) {
							ProgressB=100.00;
							if(enddateB.after(progressDate)) {// taking progressdate in place of new Date()
								 StatusB="3";
							}else {
								 StatusB="5";
							}
							}
							else if(Math.round(ProgressB)==0) {
								 StatusB="1";
							}else {
								if(enddateB.after(progressDate)) { // taking progressdate in place of new Date()
									 StatusB="2";
									}else {
										 StatusB="4";
									}
								}
						      dao.ProgressLevel(objB[0].toString(), StatusB,(int)Math.round(ProgressB),dto);
						      
                             // dao B update
						}
						
						}
						}
					
				
				}
				//A
				for(Object[] objMain:BaselineMain) {
					List<Object[]> BaselineA=dao.BaseLineLevel(dto.getMilestoneActivityId(),"1");
					for(Object[] objA:BaselineA) {
						List<Object[]> BaselineB=dao.BaseLineLevel(objA[0].toString(),"2");
						if(BaselineB.size()>0) {
							double ProgressA=0.00;
						for(Object[] objB:BaselineB) {
							
								double EB=(Double.parseDouble(objB[3].toString())/100)*Double.parseDouble(objB[2].toString());
								
								ProgressA+=EB;
								
								
							
							}
						
						// status for A
						String StatusA="1";
						Date enddateA = fc.getSqlDateFormat().parse(dto.getEndDate());
						if(Math.round(ProgressA)>=100) { 
							ProgressA=100.00;
							if(enddateA.after(progressDate)) {// taking progressdate in place of new Date()
								 StatusA="3";
							}else {
								 StatusA="5";
							}
							}
							else if(Math.round(ProgressA)==0) {
								 StatusA="1";
							}else {
								if(enddateA.after(progressDate)) {  // taking progressdate in place of new Date()
									 StatusA="2";
									}else {
										 StatusA="4";
									}
								}
						      dao.ProgressLevel(objA[0].toString(), StatusA,(int)Math.round(ProgressA),dto);
						      
                             // dao A update
						}
						
						
						}
					
				
				}
				for(Object[] objMain:BaselineMain) {
					List<Object[]> BaselineA=dao.BaseLineLevel(dto.getMilestoneActivityId(),"1");
					for(Object[] objA:BaselineA) {
							TotalA+=(Double.parseDouble(objA[3].toString())/100)*Double.parseDouble(objA[2].toString());
						}
					
					// status for Main
					
					String StatusMain="1";
					Date enddateMain = fc.getSqlDateFormat().parse(objMain[1].toString());
					if(Math.round(TotalA)>=100) {
						TotalA=100.00;
						if(enddateMain.after(progressDate)) { // taking progressdate in place of new Date()
							StatusMain="3";
						}else {
							StatusMain="5";
						}
						}
						else if(Math.round(TotalA)==0) {
							StatusMain="1";
						}else {
							if(enddateMain.after(progressDate)) {
								StatusMain="2";
								}else {
									StatusMain="4";
								}
							}
					int mainProgress = (int)Math.round(TotalA);
					String DateOfCompletion = null;
					if(mainProgress == 100) {
						 DateOfCompletion = LocalDate.now().toString();
					}
					dao.ProgressMain(dto.getMilestoneActivityId(), StatusMain,(int)Math.round(TotalA), DateOfCompletion,dto);
					// dao upadate main
				}
			}else {
				result=0;
			}   
		return result;
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public List<Object[]> MilestoneReportsList(String ProjectId) throws Exception {
		return dao.MilestoneReportsList(ProjectId);
	}
	
	@Override
	public List<Object[]> MilestoneActivitySub(MileEditDto dto) throws Exception {
		List<Object[]> MileSubata=new ArrayList<Object[]>();
		if(dto.getActivityType().equals("M")) {
			//MileSubata=dao.MilestoneActivitySub(dto.getActivityId(),"M");
		}else {
			MileSubata=dao.MilestoneActivitySub(dto.getActivityId(),"A");
		}
		return MileSubata;
	}

	@Override
	public com.vts.pfms.milestone.model.MilestoneActivitySub ActivityAttachmentDownload(String ActivitySubId)
			throws Exception {
		
		return dao.ActivityAttachmentDownload(Long.parseLong(ActivitySubId));
	}


	@Override
	public List<Object[]> ProjectDetails(String ProjectId) throws Exception {
		
		return dao.ProjectDetails(ProjectId);
	}

	@Override
	public List<Object[]> MilestoneActivityAssigneeList(String ProjectId, String EmpId) throws Exception {
		
		return dao.MilestoneActivityAssigneeList(ProjectId, EmpId);
	}

	@Override
	public List<Object[]> ProjectAssigneeList(String EmpId) throws Exception {
		
		return dao.ProjectAssigneeList(EmpId);
	}

	@Override
	public int MilestoneActivityAssign(MilestoneActivityDto dto) throws Exception {
		logger.info(new Date() +"Inside SERVICE MilestoneActivityAssign ");
		int result=0;
		dto.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
		 String dt=fc.getSqlDateFormat().format(new Date());
		 
		if(dto.getActivityType().equalsIgnoreCase("Accept")) {
			int tot=MileActivityAssignCheck(dto);
		  result=dao.MilestoneActivityAccept(dto, dt);	
			  
			  long count=1;
			  MilestoneActivity getMA=dao.MileActivityDetails(Long.parseLong(dto.getActivityId()));
				MilestoneActivityRev MaRev=new MilestoneActivityRev();
				MaRev.setActivityName(getMA.getActivityName());
				MaRev.setActivityStatusId(getMA.getActivityStatusId());
				MaRev.setMilestoneActivityId(getMA.getMilestoneActivityId());
				MaRev.setStartDate(getMA.getStartDate());
				MaRev.setEndDate(getMA.getEndDate());
				MaRev.setOicEmpId(getMA.getOicEmpId());
				MaRev.setOicEmpId1(getMA.getOicEmpId1());
				MaRev.setProgressStatus(getMA.getProgressStatus());
				MaRev.setWeightage(getMA.getWeightage());
				MaRev.setRevisionNo(1);
				MaRev.setStatusRemarks(getMA.getStatusRemarks());
				MaRev.setCreatedBy(getMA.getCreatedBy());
				MaRev.setCreatedDate(getMA.getCreatedDate());
				MaRev.setIsActive(1);
				long rev=dao.MilestoneActivityRev(MaRev);
				if(rev>0) {
					
					List<MilestoneActivityLevel> listA=dao.ActivityLevelList(Long.parseLong(dto.getActivityId()),count);
					for(MilestoneActivityLevel MileA:listA) {
					MilestoneActivitySubRev SubA=new MilestoneActivitySubRev();
					SubA.setActivityName(MileA.getActivityName());
					SubA.setActivityId(MileA.getActivityId());
					SubA.setOicEmpId(MileA.getOicEmpId());
					SubA.setOicEmpId1(MileA.getOicEmpId1());
					SubA.setActivityType(MileA.getActivityType());
					SubA.setActivityStatusId(MileA.getActivityStatusId());
					SubA.setProgressStatus(MileA.getProgressStatus());
					SubA.setWeightage(MileA.getWeightage());
					SubA.setRevision(count);
					SubA.setStartDate(MileA.getStartDate());
					SubA.setEndDate(MileA.getEndDate());
					SubA.setCreatedBy(MileA.getCreatedBy());
					SubA.setCreatedDate(MileA.getCreatedDate());
					SubA.setIsActive(1);
					dao.MilestoneActivitySubRev(SubA);
					List<MilestoneActivityLevel> listB=dao.ActivityLevelList(MileA.getActivityId(),count+1);
					for(MilestoneActivityLevel MileB:listB) {
					MilestoneActivitySubRev SubB=new MilestoneActivitySubRev();
					SubB.setActivityName(MileB.getActivityName());
					SubB.setActivityId(MileB.getActivityId());
					SubB.setOicEmpId(MileB.getOicEmpId());
					SubB.setOicEmpId1(MileB.getOicEmpId1());
					SubB.setActivityType(MileB.getActivityType());
					SubB.setActivityStatusId(MileB.getActivityStatusId());
					SubB.setProgressStatus(MileB.getProgressStatus());
					SubB.setWeightage(MileB.getWeightage());
					SubB.setRevision(count);
					SubB.setStartDate(MileB.getStartDate());
					SubB.setEndDate(MileB.getEndDate());
					SubB.setCreatedBy(MileB.getCreatedBy());
					SubB.setCreatedDate(MileB.getCreatedDate());
					SubB.setIsActive(1);
					dao.MilestoneActivitySubRev(SubB);
					List<MilestoneActivityLevel> listC=dao.ActivityLevelList(MileB.getActivityId(),count+2);
					for(MilestoneActivityLevel MileC:listC) {
					MilestoneActivitySubRev SubC=new MilestoneActivitySubRev();
					SubC.setActivityName(MileC.getActivityName());
					SubC.setActivityId(MileC.getActivityId());
					SubC.setOicEmpId(MileC.getOicEmpId());
					SubC.setOicEmpId1(MileC.getOicEmpId1());
					SubC.setActivityType(MileC.getActivityType());
					SubC.setActivityStatusId(MileC.getActivityStatusId());
					SubC.setProgressStatus(MileC.getProgressStatus());
					SubC.setWeightage(MileC.getWeightage());
					SubC.setRevision(count);
					SubC.setStartDate(MileC.getStartDate());
					SubC.setEndDate(MileC.getEndDate());
					SubC.setCreatedBy(MileC.getCreatedBy());
					SubC.setCreatedDate(MileC.getCreatedDate());
					SubC.setIsActive(1);
					dao.MilestoneActivitySubRev(SubC);
					List<MilestoneActivityLevel> listD=dao.ActivityLevelList(MileC.getActivityId(),count+3);
					for(MilestoneActivityLevel MileD:listD) {
					MilestoneActivitySubRev SubD=new MilestoneActivitySubRev();
					SubD.setActivityName(MileD.getActivityName());
					SubD.setActivityId(MileD.getActivityId());
					SubD.setOicEmpId(MileD.getOicEmpId());
					SubD.setOicEmpId1(MileD.getOicEmpId1());
					SubD.setActivityType(MileD.getActivityType());
					SubD.setActivityStatusId(MileD.getActivityStatusId());
					SubD.setProgressStatus(MileD.getProgressStatus());
					SubD.setWeightage(MileD.getWeightage());
					SubD.setRevision(count);
					SubD.setStartDate(MileD.getStartDate());
					SubD.setEndDate(MileD.getEndDate());
					SubD.setCreatedBy(MileD.getCreatedBy());
					SubD.setCreatedDate(MileD.getCreatedDate());
					SubD.setIsActive(1);
					dao.MilestoneActivitySubRev(SubD);
					List<MilestoneActivityLevel> listE=dao.ActivityLevelList(MileD.getActivityId(),count+4);
					for(MilestoneActivityLevel MileE:listE) {
					MilestoneActivitySubRev SubE=new MilestoneActivitySubRev();
					SubE.setActivityName(MileE.getActivityName());
					SubE.setActivityId(MileE.getActivityId());
					SubE.setOicEmpId(MileE.getOicEmpId());
					SubE.setOicEmpId1(MileE.getOicEmpId1());
					SubE.setActivityType(MileE.getActivityType());
					SubE.setActivityStatusId(MileE.getActivityStatusId());
					SubE.setProgressStatus(MileE.getProgressStatus());
					SubE.setWeightage(MileE.getWeightage());
					SubE.setRevision(count);
					SubE.setStartDate(MileE.getStartDate());
					SubE.setEndDate(MileE.getEndDate());
					SubE.setCreatedBy(MileE.getCreatedBy());
					SubE.setCreatedDate(MileE.getCreatedDate());
					SubE.setIsActive(1);
					dao.MilestoneActivitySubRev(SubE);
					
					dao.RevLevelUpdate(MileE.getActivityId().toString(), "1");
				    }
				    dao.RevLevelUpdate(MileD.getActivityId().toString(),"1");
				    }
					dao.RevLevelUpdate(MileC.getActivityId().toString(),"1");
					}
					dao.RevLevelUpdate(MileB.getActivityId().toString(),"1");
					}
					dao.RevLevelUpdate(MileA.getActivityId().toString(), "1");
					}
					dao.RevMainUpdate(dto.getActivityId(), "1");
				}
				ActivityTransaction Trans=new ActivityTransaction();
				Trans.setMilestoneActivityId(Long.parseLong(dto.getActivityId()));
				Trans.setSentBy(Long.parseLong(dto.getOicEmpId()));
				Trans.setStatus("Y");
				Trans.setActionDate(new java.sql.Date(fc.getSqlDateFormat().parse(dt).getTime()));
				Trans.setCreatedBy(dto.getCreatedBy());
				Trans.setCreatedDate(dto.getCreatedDate());
				Trans.setIsActive(1);
				dao.ActivityTransactionInsert(Trans);
				
		}else if(dto.getActivityType().equalsIgnoreCase("Back")) 
		{
			result=dao.MilestoneActivityBack(dto);
			ActivityTransaction Trans=new ActivityTransaction();
			Trans.setMilestoneActivityId(Long.parseLong(dto.getActivityId()));
			Trans.setSentBy(Long.parseLong(dto.getOicEmpId()));
			Trans.setStatus("B");
			Trans.setRemarks(dto.getStatusRemarks());
			Trans.setActionDate(new java.sql.Date(fc.getSqlDateFormat().parse(dt).getTime()));
			Trans.setCreatedBy(dto.getCreatedBy());
			Trans.setCreatedDate(dto.getCreatedDate());
			Trans.setIsActive(1);
			dao.ActivityTransactionInsert(Trans);
			
			
		}else {
			int tot=MileActivityAssignCheck(dto);
			if(tot==0) {
			result=dao.MilestoneActivityAssign(dto);
			ActivityTransaction Trans=new ActivityTransaction();
			Trans.setMilestoneActivityId(Long.parseLong(dto.getActivityId()));
			Trans.setSentBy(Long.parseLong(dto.getOicEmpId()));
			Trans.setStatus("A");
			Trans.setActionDate(new java.sql.Date(fc.getSqlDateFormat().parse(dt).getTime()));
			Trans.setCreatedBy(dto.getCreatedBy());
			Trans.setCreatedDate(dto.getCreatedDate());
			Trans.setIsActive(1);
			dao.ActivityTransactionInsert(Trans);
			if(result>0) {
				MilestoneActivity activity = dao.MileActivityDetails(Long.parseLong(dto.getActivityId()));
				
				PfmsNotification OIC1notification =new PfmsNotification();
				OIC1notification.setEmpId(activity.getOicEmpId());
				OIC1notification.setNotificationMessage("Action Pending On Milestone Assigned ");
				OIC1notification.setNotificationUrl("M-A-AssigneeList.htm?ProjectId="+activity.getProjectId());
				OIC1notification.setNotificationDate(fc.getSqlDateAndTimeFormat().format(new Date()));
				OIC1notification.setNotificationby(Long.parseLong(dto.getOicEmpId()));
				OIC1notification.setIsActive(1);
				OIC1notification.setScheduleId(0L);
				OIC1notification.setStatus("MAR");
				OIC1notification.setCreatedBy(dto.getCreatedBy());
				OIC1notification.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
				
				dao.PfmsNotificationAdd(OIC1notification);
				
				PfmsNotification OIC2notification =new PfmsNotification();
				OIC2notification.setEmpId(activity.getOicEmpId1());
				OIC2notification.setNotificationMessage("Action Pending On Milestone Assigned ");
				OIC2notification.setNotificationUrl("M-A-AssigneeList.htm?ProjectId="+activity.getProjectId());
				OIC2notification.setNotificationDate(fc.getSqlDateAndTimeFormat().format(new Date()));
				OIC2notification.setNotificationby(Long.parseLong(dto.getOicEmpId()));
				OIC2notification.setIsActive(1);
				OIC2notification.setScheduleId(0L);
				OIC2notification.setStatus("MAR");
				OIC2notification.setCreatedBy(dto.getCreatedBy());
				OIC2notification.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
				
				dao.PfmsNotificationAdd(OIC2notification);
			}
			
			}else {
				result=tot;
			}
		}
		return result;
	}

	@Override
	public List<Object[]> ActionList(String actiontype, String activityid) throws Exception {
		
		return dao.ActionList(actiontype, activityid);
	}

	@Override
	public int WeightageSum(String Id, String ActivityId, String ActivityType,String LevelId) throws Exception {
		int result=0;
		logger.info(new Date() +"Inside SERVICE WeightageSum ");
		if("M".equalsIgnoreCase(ActivityType)) {
			result=dao.ActivityMainSum(Id, ActivityId);
		}
		if(!"M".equalsIgnoreCase(ActivityType)) {
			result=dao.ActivityLevelSum(Id, ActivityId,LevelId);
		}
		
		return result;
	}


	@Override
	public List<Object[]> FileDeatils(String FileId) throws Exception {
		return dao.FileDeatils(FileId);
	}


	@Override
	public List<Object[]> MilestoneScheduleList(String ProjectId) throws Exception {

		return dao.MilestoneScheduleList(ProjectId);
	}

	@Override
	public long MilestoneScheduleInsert(MilestoneScheduleDto maindto) throws Exception {

		logger.info(new Date() +"Inside SERVICE MilestoneScheduleInsert ");
		MilestoneSchedule Milestone=new MilestoneSchedule();
		
		try {
			
			Milestone.setProjectId(Long.parseLong(maindto.getProjectId()));
			Milestone.setMilestoneNo(dao.MilestoneScheduleCount(maindto.getProjectId())+1);
			Milestone.setActivityType(Long.parseLong(maindto.getActivityType()));
			Milestone.setActivityName(maindto.getActivityName());
			Milestone.setOrgStartDate(new java.sql.Date(fc.getRegularDateFormat().parse(maindto.getStartDate()).getTime()));
			Milestone.setOrgEndDate(new java.sql.Date(fc.getRegularDateFormat().parse(maindto.getEndDate()).getTime()));
			Milestone.setRevisionNo(0);
			Milestone.setCreatedBy(maindto.getCreatedBy());
			Milestone.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
			Milestone.setActivityStatusId(0);
			Milestone.setProgressStatus(0);
			Milestone.setWeightage(0);
			Milestone.setIsActive(1);
			
		}
		catch(Exception e) {
			logger.error(new Date() +"Inside  MilestoneScheduleInsert ",e);
			e.printStackTrace();
			
		}
		
		
		return dao.MilestoneScheduleInsert(Milestone);
	}

	@Override
	public List<Object[]> MilestoneExcel(String ProjectId) throws Exception {
	
		logger.info(new Date() +"Inside SERVICE MilestoneExcel ");
		List<Object[]> main=dao.MilestoneActivityListNew(ProjectId);
		List<Object[]> MilestoneActivityA=new ArrayList<Object[]>();
		List<Object[]> MilestoneActivityB=new ArrayList<Object[]>();
		List<Object[]> MilestoneActivityC=new ArrayList<Object[]>();
		List<Object[]> MilestoneActivityD=new ArrayList<Object[]>();
		List<Object[]> MilestoneActivityE=new ArrayList<Object[]>();
		
			for(Object[] objmain:main ) {
				List<Object[]>  MilestoneActivityA1=dao.MilestoneActivityLevelExcel(objmain[0].toString(),"1");
				MilestoneActivityA.addAll(MilestoneActivityA1);
				
				for(Object[] obj:MilestoneActivityA1) {
					List<Object[]>  MilestoneActivityB1=dao.MilestoneActivityLevelExcel(obj[0].toString(),"2");
					MilestoneActivityB.addAll(MilestoneActivityB1);
					
					for(Object[] obj1:MilestoneActivityB1) {
						List<Object[]>  MilestoneActivityC1=dao.MilestoneActivityLevelExcel(obj1[0].toString(),"3");
						MilestoneActivityC.addAll(MilestoneActivityC1);
						
						for(Object[] obj2:MilestoneActivityC1) {
							List<Object[]>  MilestoneActivityD1=dao.MilestoneActivityLevelExcel(obj2[0].toString(),"4");
							MilestoneActivityD.addAll( MilestoneActivityD1);
							
							for(Object[] obj3:MilestoneActivityD1) {
								List<Object[]>  MilestoneActivityE1=dao.MilestoneActivityLevelExcel(obj3[0].toString(),"5");
								MilestoneActivityE.addAll( MilestoneActivityE1);
							}
						}
					}
				}
			}
		
			List<Object[]> milestoneactivityall = new ArrayList<Object[]>();
			
			milestoneactivityall.addAll(main);
			milestoneactivityall.addAll(MilestoneActivityA);
			milestoneactivityall.addAll(MilestoneActivityB);
			milestoneactivityall.addAll(MilestoneActivityC);
			milestoneactivityall.addAll(MilestoneActivityD);
			milestoneactivityall.addAll(MilestoneActivityE);
			
			
			
			
			
			return milestoneactivityall; //dao.MilestoneExcel(ProjectId);
	}

	@Override
	public List<Object[]> MainSystem(String projectid) throws Exception {
		
		return dao.MainSystem(projectid);
	}

	@Override
	public long RepMasterInsert(com.vts.pfms.milestone.model.FileRepMaster fileRepo) throws Exception {
		logger.info(new Date() +"Inside SERVICE RepMasterInsert ");
		long pi=0;
		fileRepo.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
		fileRepo.setIsActive(1);
		fileRepo.setParentLevelId(pi);
		fileRepo.setLevelId(pi+1);
		return dao.RepMasterInsert(fileRepo);
	}

	@Override
	public List<Object[]> MainSystemLevel(String ParentId) throws Exception {	
		return dao.MainSystemLevel(ParentId);
	}

	@Override
	public long FileRepMasterSubInsert(FileRepMaster fileRepo) throws Exception {
		fileRepo.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
		fileRepo.setIsActive(1);
		return dao.RepMasterInsert(fileRepo);
	}
	
	@Override 
	public List<Object[]> LoginProjectDetailsList(String empid,String Logintype ,String LabCode) throws Exception
	{
		List<Object[]> projectidlist=(ArrayList<Object[]>) dao.LoginProjectDetailsList(empid,Logintype,LabCode);  
		return projectidlist;
	}
	
	

	@Override
	public List<Object[]> ProjectEmpList(String projectid,String Labcode)throws Exception
	{
		return dao.ProjectEmpList(projectid , Labcode);
	}
	
	@Override
	public List<Object[]> AllEmpNameDesigList( String labcode)throws Exception
	{
		return dao.AllEmpNameDesigList(labcode);
	}

	@Override
	public List<Object[]> ActivityLevelCompare(String ActivityId, String Rev, String Rev1, String LevelId)throws Exception {		
		return dao.ActivityLevelCompare(ActivityId, Rev, Rev1, LevelId);
	}

	@Override
	public List<Object[]> ProjectEmpListEdit(String projectid, String id) throws Exception {
		
		return dao.ProjectEmpListEdit(projectid, id);
	}
	
	@Override
	public List<Object[]> DocumentTypeList(String ProjectId,String LabCode) throws Exception 
	{		
		return dao.DocumentTypeList(ProjectId,LabCode);
	}
	
	@Override
	public List<Object[]> DocumentTitleList(String ProjectId,String Sub,String LabCode) throws Exception {
		
		return dao.DocumentTitleList(ProjectId,Sub,LabCode);
	}
	
	@Override
	public List<Object[]> DocumentStageList(String documenttype,String levelid) throws Exception {
		
		return dao.DocumentStageList(documenttype,levelid);
	}
	
	@Override
	public long FileSubInsertNew(FileRepNew fileRepo) throws Exception {
	
		fileRepo.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
		fileRepo.setIsActive(1);
		return dao.FileSubInsertNew(fileRepo);
	}
	


	@Override
	public List<Object[]> VersionCheckList(String ProjectId, String SubsystemL1,String documenttitle) throws Exception {
		return dao.VersionCheckList(ProjectId,SubsystemL1,documenttitle);
	}

	
	@Override
	public List<Object[]> FileHistoryList(String filerepid) throws Exception 
	{
		return dao.FileHistoryList(filerepid);
	}
	
	@Override
	public List<Object[]> FileRepMasterListAll(String projectid,String LabCode )throws Exception
	{
		return dao.FileRepMasterListAll( projectid, LabCode );
	}
	
	@Override
	public List<Object[]> FileDocMasterListAll(String projectid,String LabCode)throws Exception
	{
		return dao.FileDocMasterListAll(projectid, LabCode);
	}
	
	@Override
	public long ProjectDocumetsAdd(FileProjectDocDto dto)throws Exception
	{
		logger.info(new Date() +"Inside  ProjectDocumetsAdd ");
		long count=0;
		for(String FileUploadMasterId:dto.getFileUploadMasterId()) 
		{
			FileProjectDoc model=new FileProjectDoc();
			model.setProjectid(Long.parseLong(dto.getProjectid()));
			model.setCreatedBy(dto.getCreatedBy());
			model.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
			model.setIsActive(1);		
			model.setFileUploadMasterId(Long.parseLong(FileUploadMasterId.split("_")[0]));
			model.setParentLevelid(Long.parseLong(FileUploadMasterId.split("_")[1]));
			count=dao.ProjectDocumetsAdd(model);
		}
		return count;
	}
	
	@Override
	public List<Object[]> DocumentAmendment(String FileRepUploadId) throws Exception {
		return dao.DocumentAmendment(FileRepUploadId);
	}
	
	
	
	@Override
	public long FileUploadNew(FileUploadDto dto) throws Exception {
		logger.info(new Date() +"Inside SERVICE FileUploadNew ");
		long count=0;
	
		 try {
				String[] Dir=dto.getPathName().split("/");
				
				Path docPath1=Paths.get(dto.getLabCode(),"docrepo",("P"+dto.getProjectId()),Dir[0],Dir[1]);
				Path docPath2=Paths.get(FilePath,dto.getLabCode(),"docrepo",("P"+dto.getProjectId()),Dir[0],Dir[1]);
				
//				String FullDir=dto.getLabCode()+"\\docrepo\\P"+dto.getProjectId()+"\\";
//				String actialFullPath = FilePath+FullDir;
//				
//				for (int i = 0; i < Dir.length; i++) {
//					actialFullPath=actialFullPath.concat(Dir[i]+"\\");
//					FullDir =FullDir.concat(Dir[i]+"\\");
//					File theDir = new File(actialFullPath);
//					 if (!theDir.exists()){
//					     theDir.mkdirs();
//					 }
//				}
			
				Zipper zip=new Zipper();

				String Pass=dao.FilePass(dto.getUserId());
				long version=Long.parseLong(dto.getVer());
				long release=Long.parseLong(dto.getRel());
				 
				FileRepUploadNew upload=new FileRepUploadNew();
				upload.setFileName(dto.getFileNamePath());
				upload.setFilePass(Pass);
				upload.setReleaseDoc(release);
				upload.setVersionDoc(version);
				upload.setFileRepId(Long.parseLong(dto.getFileId()));
				upload.setFilePath(docPath1.toString());
				upload.setFileNameUi(dto.getFileName());
				upload.setDescription(dto.getDescription());
				upload.setCreatedBy(dto.getUserId());
				upload.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
				upload.setIsActive(1);
				 
				count=dao.FileUploadInsertNew(upload);
				 
				Long Rev=Long.parseLong(dto.getRel());
				Long Ver=Long.parseLong(dto.getVer());
				if(count>0) {
					
				if (!Files.exists(docPath2)) {
					Files.createDirectories(docPath2);
				}
		        zip.pack(dto.getFileNamePath(),dto.getIS(),docPath2.toString(),dto.getFileName()+Ver+"-"+Rev,Pass);
		        
		        count=dao.FileRepRevUpdate(dto.getFileId(),release,version);
	         
	        
			 }
			 
		 }catch (Exception e) {
			 logger.error(new Date() +"Inside SERVICE FileUploadNew "+ e);
			 e.printStackTrace();
		   count=0;
		}

		return count;
	}
	
	@Override
	public long FileAmmendUploadNew(FileDocAmendmentDto dto) throws Exception 
	{
		logger.info(new Date() +"Inside SERVICE FileAmmendUploadNew ");
		long count=0;
		try 
		{
			String FullDir=dto.getLabCode()+"\\docrepo\\P"+dto.getProjectId()+"\\Amendments\\";
			
			
			String actialFullPath = FilePath+FullDir;
			
			File theDir = new File(actialFullPath);
			if (!theDir.exists())
			{
			    theDir.mkdirs();
			}
				
			Zipper zip=new Zipper();
			
			List<Object[]> amendmentslist = dao.DocumentAmendment(dto.getFileRepUploadId());
			
			FileDocAmendment model=new FileDocAmendment();
			model.setFileName("Amendment");
			model.setFileRepUploadId(Long.parseLong(dto.getFileRepUploadId()));
			model.setDescription(dto.getDescription());
			model.setFilePass(dao.FilePass(dto.getCreatedBy()));
			model.setFilePath(FullDir);
			model.setAmendmentName(dto.getFileName());
			if(amendmentslist!=null && amendmentslist.size()>0) {
				model.setAmendVersion(Integer.parseInt(amendmentslist.get(0)[4].toString())+1);
			}else
			{
				model.setAmendVersion(1);
			}
			model.setCreatedBy(dto.getCreatedBy());
			model.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
			count =dao.DocumetAmmendAdd(model);
			
	
	
			if(count>0) {
				zip.pack(dto.getFileName(),dto.getInStream(),actialFullPath,model.getFileName()+dto.getFileRepUploadId()+"-"+model.getAmendVersion(),model.getFilePass());
			}

	 }catch (Exception e) {
		 logger.error(new Date() +"Inside SERVICE FileAmmendUploadNew "+e);
		 e.printStackTrace();
		 count=0;
	 }
		return count;
	}
	
	
	@Override
	public Object[] DocumentAmendmentData(String docammendmentid) throws Exception {
		return dao.DocumentAmendmentData(docammendmentid);
	}
	@Override
	public Object[] RepMasterData(String filerepmasterid) throws Exception {
		return dao.RepMasterData(filerepmasterid);
	}
	
	@Override
	public List<Object[]> RepMasterAllDocLists(String filerepmasterid) throws Exception {
		return dao.RepMasterAllDocLists(filerepmasterid);
	}
	
	@Override
	public List<Object[]> MainSystem1(String filerepmasterid) throws Exception
 	{
		return dao.MainSystem1(filerepmasterid);
 	}
	
	@Override
	public int fileRepMasterEditSubmit(String filerepmasterid,String levelname, String levelType) throws Exception
 	{
		String fileType = levelType.trim().toLowerCase();
		Optional<FileRepMaster> request = dao.getFileRepMasterById(Long.parseLong(filerepmasterid));
		if(request.isPresent()) {
			FileRepMaster repMaster = request.get();
			  switch (fileType) {
	          case "mainlevel":
	  	          Path filepath1 = Paths.get(FilePath, repMaster.getLabCode().toString(), "docrepo", "P"+repMaster.getProjectId().toString(), repMaster.getLevelName());
	  	          if (Files.exists(filepath1) || Files.isDirectory(filepath1)) {
	  	            renameMainFolder(levelType, repMaster.getLabCode(), repMaster.getProjectId(), repMaster.getFileRepMasterId(), 0l, levelname.trim(), repMaster.getLevelName().trim());
	  	          }
	  	          break;
	          case "sublevel":
	        	  Long parentId = repMaster.getParentLevelId();
	        	  FileRepMaster master = dao.getFileRepMasterById(parentId).get();
	  	          Path filepath2 = Paths.get(FilePath, repMaster.getLabCode().toString(), "docrepo", "P"+repMaster.getProjectId().toString(), master.getLevelName(), repMaster.getLevelName());
	  	          if (Files.exists(filepath2) || Files.isDirectory(filepath2)) {
	  	             renameSubFolder(levelType, repMaster.getLabCode(), repMaster.getProjectId(), parentId, repMaster.getFileRepMasterId(), master.getLevelName().trim(), levelname.trim(), repMaster.getLevelName().trim());
	  	          }
	  	          break;
	          }
		}
		return dao.fileRepMasterEditSubmit(filerepmasterid, levelname);
 	}
	
    private void renameMainFolder(String levelType, String labCode, Long projectId, Long mainRepMasterId, Long subRepMasterId, String newName, String oldName) throws Exception {
        Path oldPath = Paths.get(FilePath, labCode, "docrepo", "P"+projectId, oldName);
        Path newPath = Paths.get(FilePath, labCode, "docrepo", "P"+projectId, newName);

        if (!Files.exists(oldPath) || !Files.isDirectory(oldPath)) {
            throw new IllegalArgumentException("Main folder not found: " + oldPath);
        }
        
        // Prevent moving into itself
 		if (newPath.startsWith(oldPath)) {
 		    throw new IllegalArgumentException("Cannot move a folder into its subfolder: " + newPath);
 		}
     		
 		try {
     		// Attempt to rename (move) the folder
     		   Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
     	} catch (FileSystemException e) {
     		// Likely caused by file lock
     		throw new IOException("Failed to move folder. It may be open or locked by another process: " + e.getMessage(), e);
 		}

        List<Object[]> affectedFiles = dao.findByFilePathStartingWith(levelType,projectId,mainRepMasterId,subRepMasterId,oldName);
        for (Object[] file : affectedFiles) {
        	Long uploadId = ((Number) file[0]).longValue(); 
            String oldPathStr = file[4].toString();
            if (oldPathStr != null) {
                // Split the path by slash or backslash
                String[] segments = oldPathStr.split("[/\\\\]");
                StringBuilder uploadPath = new StringBuilder();

                for (int i = 0; i < segments.length; i++) {
                    if (segments[i].equals(oldName)) {
                        segments[i] = newName; // Replace only exact match
                    }
                    uploadPath.append(segments[i]);
                    if (i < segments.length - 1) {
                    	uploadPath.append(File.separator);
                    }
                }

                dao.updateFileRepUploadById(uploadId, uploadPath.toString());
            }
        }
    }

    private void renameSubFolder(String levelType, String labCode, Long projectId, Long mainRepMasterId, Long subRepMasterId, String mainLevelName, String newName, String oldName) throws Exception {
        Path oldPath = Paths.get(FilePath, labCode, "docrepo", "P"+projectId, mainLevelName, oldName);
        Path newPath = Paths.get(FilePath, labCode, "docrepo", "P"+projectId, mainLevelName, newName);

        if (!Files.exists(oldPath) || !Files.isDirectory(oldPath)) {
            throw new IllegalArgumentException("Subfolder not found: " + oldPath);
        }

        // Prevent moving into itself
   		if (newPath.startsWith(oldPath)) {
   		    throw new IllegalArgumentException("Cannot move a folder into its subfolder: " + newPath);
   		}
       		
   		try {
       		// Attempt to rename (move) the folder
       		   Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
       	} catch (FileSystemException e) {
       		// Likely caused by file lock
       		throw new IOException("Failed to move folder. It may be open or locked by another process: " + e.getMessage(), e);
   		}

        List<Object[]> affectedFiles = dao.findByFilePathStartingWith(levelType,projectId,mainRepMasterId,subRepMasterId,oldName);
        for (Object[] file : affectedFiles) {
        	Long uploadId = ((Number) file[0]).longValue(); 
            String oldPathStr = file[4].toString();
            if (oldPathStr != null) {
                // Split the path by slash or backslash
                String[] segments = oldPathStr.split("[/\\\\]");
                StringBuilder uploadPath = new StringBuilder();

                for (int i = 0; i < segments.length; i++) {
                    if (segments[i].equals(oldName)) {
                        segments[i] = newName; // Replace only exact match
                    }
                    uploadPath.append(segments[i]);
                    if (i < segments.length - 1) {
                    	uploadPath.append(File.separator);
                    }
                }

                dao.updateFileRepUploadById(uploadId, uploadPath.toString());
            }
        }
    }
	
	@Override
	public List<FileDocMaster> fileDocMasterList(String LabCode) throws Exception
 	{
		return dao.fileDocMasterList(LabCode);
 	}
	
	@Override
	public long FileDocMasterAdd(FileDocMaster docmaster) throws Exception
	{
		docmaster.setIsActive(1);
		docmaster.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
		return dao.FileDocMasterAdd(docmaster);
	}
	@Override
	public List<FileDocMaster> FileLevelSublevelNameCheck(String levelname,String LabCode) throws Exception
 	{
		return dao.FileLevelSublevelNameCheck(levelname, LabCode);
 	}
	
	@Override
	public Object[] fileNameCheck(String levelname, String shortname, String docid, String parentlevelid,String LabCode ) throws Exception
 	{
		return dao.fileNameCheck(levelname, shortname, docid,parentlevelid,LabCode);
 	}
	
	@Override
	public void excelCellValuesSet(Sheet sheet,Object[] hlo, CellStyle  wrapname,int rowcount) throws Exception
 	{
		logger.info(new Date() +"Inside SERVICE excelCellValuesSet ");
		SimpleDateFormat sdf=fc.getRegularDateFormat();
		SimpleDateFormat sdf1=fc.getSqlDateFormat();
		Row row1 = sheet.createRow(rowcount);
			
		Cell cell1 = row1.createCell(0);
			
		Cell cell2 = row1.createCell(1);
		
			switch (hlo[16].toString())
			{
				case  "0":
				{
					cell1.setCellValue("M-"+hlo[0].toString());
					break;
				}
				case  "1":
				{
					cell1.setCellValue("M-A-"+hlo[0].toString());
					cell2.setCellValue("M-"+hlo[1].toString());
					break;
				}
				case  "2":
				{
					cell1.setCellValue("M-B-"+hlo[0].toString());
					cell2.setCellValue("M-A-"+hlo[1].toString());
					break;
				}
				case  "3":
				{
					cell1.setCellValue("M-C-"+hlo[0].toString());
					cell2.setCellValue("M-B-"+hlo[1].toString());
					break;
				}
				case  "4":
				{
					cell1.setCellValue("M-D-"+hlo[0].toString());
					cell2.setCellValue("M-C-"+hlo[1].toString());
					break;
				}
				case  "5":
				{
					cell1.setCellValue("M-E-"+hlo[0].toString());
					cell2.setCellValue("M-D-"+hlo[1].toString());
					break;
				}
			}
		
		cell1.setCellStyle(wrapname);
		cell2.setCellStyle(wrapname);
		
		
		
		Cell cell3 = row1.createCell(2);
		cell3.setCellValue(hlo[4].toString());
		cell3.setCellStyle(wrapname);

		Cell cell4 = row1.createCell(3);
		cell4.setCellValue(hlo[12].toString());
		cell4.setCellStyle(wrapname);

		Cell cell5 = row1.createCell(4);
		cell5.setCellValue(hlo[8].toString());
		cell5.setCellStyle(wrapname);

		Cell cell6 = row1.createCell(5);
		cell6.setCellValue(hlo[5].toString());
		cell6.setCellStyle(wrapname);

		Cell cell7 = row1.createCell(6);
		cell7.setCellValue(hlo[6].toString());
		cell7.setCellStyle(wrapname);
		
		Cell cell8 = row1.createCell(7);
		cell8.setCellValue(sdf.format(sdf1.parse( hlo[2].toString())));
		cell8.setCellStyle(wrapname);
		
		Cell cell9 = row1.createCell(8);
		cell9.setCellValue(sdf.format(sdf1.parse( hlo[3].toString())));
		cell9.setCellStyle(wrapname);
		
		Cell cell10 = row1.createCell(9);
		cell10.setCellValue( hlo[14].toString());
		cell10.setCellStyle(wrapname);
 	}

	@Override
	public int MilestoneRemarkUpdate(MilestoneActivityDto dto) throws Exception {

		return dao.MilestoneRemarkUpdate(dto);
	}

	@Override
	public int MileActivityAssignCheck(MilestoneActivityDto dto) throws Exception {
		
		logger.info(new Date() +"Inside SERVICE MileActivityAssignCheck ");
		int Total=0;
			List<Object[]> BaselineA=dao.WeightageLevel(dto.getActivityId(),"1");
			if(BaselineA.size()>0){
				int TotalA=0;
			for(Object[] objA:BaselineA) {
				
				List<Object[]> BaselineB=dao.WeightageLevel(objA[0].toString(),"2");
				if(BaselineB.size()>0){
					int TotalB=0;
				for(Object[] objB:BaselineB) {
					
					List<Object[]> BaselineC=dao.WeightageLevel(objB[0].toString(),"3");
					if(BaselineC.size()>0){
						int TotalC=0;
						for(Object[] objC:BaselineC) {
								List<Object[]> BaselineD=dao.WeightageLevel(objC[0].toString(),"4");
								if(BaselineD.size()>0){
									int TotalD=0;
								for(Object[] objD:BaselineD) {
									List<Object[]> BaselineE=dao.WeightageLevel(objD[0].toString(),"5");
									if(BaselineE.size()>0){
										int TotalE=0;
									if(BaselineE.size()>0) {
										for(Object[] objE:BaselineE) {
										TotalE+=Integer.parseInt(objE[3].toString());
										}
									}
								    if(TotalE<100) {
								    	return Total=2;
								    }
								}
									TotalD+=Integer.parseInt(objD[3].toString());
								}
							    if(TotalD<100) {
							    	return Total=2;
							    }
						}
							
							
								TotalC+=Integer.parseInt(objC[3].toString());
						}
					    if(TotalC<100) {
					    	return Total=2;
					    }	
				}
						TotalB+=Integer.parseInt(objB[3].toString());
				}
			    if(TotalB<100) {
			      return	Total=2;
			    }
					
				}	
				TotalA+=Integer.parseInt(objA[3].toString());
				}
			    if(TotalA<100) {
			      return	Total=2;
			    }
			}else {
				Total=3;
			}

		return Total;
	}
	
	
	
	@Override
	public long MainMilestoneDOCUpdate(String MainId,String DateOfCompletion, String UserId) throws Exception
	{
		MilestoneActivity mainms = dao.MileActivityDetails(Long.parseLong(MainId));
		mainms.setDateOfCompletion(fc.RegularToSqlDate(DateOfCompletion));
		mainms.setModifiedBy(UserId);
		mainms.setModifiedDate(sdtf.format(new Date()));
		
		return dao.MileActivityDetailsUpdtae(mainms);
	}
	
	//PRAKARSH 
	
	  @Override
	  public void IsActive(String project, String FileParentId) {
	
        ///List<Object[]>FileRepUploadId=dao.FileRepUploadId(project);
      int  fileUploadMasterId=Integer.parseInt(FileParentId.split("_")[0]); //int
   	int parentLevelid=Integer.parseInt(FileParentId.split("_")[1]); //
   	
   	  dao.isActive(project,fileUploadMasterId,parentLevelid);

	  }

	@Override
	public List<Object[]> FileRepUploadId(String project,String FileParentId) {
		int  documentID=Integer.parseInt(FileParentId.split("_")[0]);
       List<Object[]>FileRepUploadId=dao.FileRepUploadId(project,documentID);
       
		  return FileRepUploadId;
	}
	//change file isActive to zero---
	 public int IsFileInActive(String project, String FileParentId) {
		 int  documentID=Integer.parseInt(FileParentId.split("_")[0]);
	   int count=	dao.IsFileInActive(project,documentID);
	   return count;
		 
	 }

	@Override
	public int DocumentListNameEdit(String filerepmasterid, String levelname) {
		
		return dao.DocumentListNameEdit(filerepmasterid,levelname);
	}
	@Override
	public List<Object[]> getMsprojectTaskList(String projectId) throws Exception {
		return dao.getMsprojectTaskList(projectId);
	}
	
	@Override
	public long DocFileUploadAjax(FileUploadDto uploadDto) throws Exception {
		
		logger.info(new Date() +"Inside SERVICE DocFileUploadAjax ");
		long count=0;
		long count1=0;
	
		 try {
			 AtomicReference<Path> fullPathRef = new AtomicReference<>();

			 FileRepUploadNew upload = new FileRepUploadNew();
			 String fileType = uploadDto.getFileType();
			 String labCode = uploadDto.getLabCode();
			 String projectPath = "P" + uploadDto.getProjectId();

			 if ("mainLevel".equalsIgnoreCase(fileType)) {
			     dao.getFileRepMasterById(Long.parseLong(uploadDto.getFileRepMasterId()))
			        .ifPresent(repMaster -> {
			            String levelName = repMaster.getLevelName();

			            Path fullPath = Paths.get(FilePath, labCode, "docrepo", projectPath, levelName);
			            Path relativePath = Paths.get(labCode, "docrepo", projectPath, levelName);

			            fullPathRef.set(fullPath);

			            upload.setFilePath(relativePath.toString());
			        });

			 } else if ("subLevel".equalsIgnoreCase(fileType)) {
			     Optional<FileRepMaster> subLevelOpt = dao.getFileRepMasterById(Long.parseLong(uploadDto.getSubL1()));
			     
			     if (subLevelOpt.isPresent()) {
			         FileRepMaster subLevel = subLevelOpt.get();
			         Long parentId = subLevel.getParentLevelId();
			         
			         Optional<FileRepMaster> parentLevelOpt = dao.getFileRepMasterById(parentId);
			         
			         if (parentLevelOpt.isPresent()) {
			             FileRepMaster parentLevel = parentLevelOpt.get();
			             
			             Path fullPath = Paths.get(FilePath, labCode, "docrepo", projectPath,
			                                       parentLevel.getLevelName(), subLevel.getLevelName());
			             
			             Path relativePath = Paths.get(labCode, "docrepo", projectPath,
			                                           parentLevel.getLevelName(), subLevel.getLevelName());

			             fullPathRef.set(fullPath);

			             upload.setFilePath(relativePath.toString());
			         }
			     }
			 }
				
				
				Zipper zip=new Zipper();
				String Pass=dao.FilePass(uploadDto.getUserId());
				long version=Long.parseLong(uploadDto.getVer());
				long release=Long.parseLong(uploadDto.getRel());
				if(version>=1) {
		             release +=1;
					}
				if(version==0) {
					version +=1;
				}
				
	            FileRepNew rep = new FileRepNew();
	            List<Object[]> fileRepUpdate = dao.getFileRepData(uploadDto.getProjectId(),uploadDto.getFileRepMasterId(),uploadDto.getSubL1(),uploadDto.getDocumentName().trim());
	            if(fileRepUpdate!=null && fileRepUpdate.size()>0) {
	            	rep.setFileRepId(Long.parseLong(fileRepUpdate.get(0)[0].toString()));
	 	            rep.setVersionDoc(version);
	 	            rep.setReleaseDoc(release);
	 	            rep.setCreatedBy(uploadDto.getUserId());
	 	            rep.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
	 	            dao.FileRepUpdate(rep);
	 	            count1=Long.parseLong(fileRepUpdate.get(0)[0].toString());
	            }else {
		            rep.setProjectId(Long.parseLong(uploadDto.getProjectId()));
		            rep.setFileRepMasterId(Long.parseLong(uploadDto.getFileRepMasterId()));
		            rep.setSubL1(Long.parseLong(uploadDto.getSubL1()));
		            rep.setVersionDoc(version);
		            rep.setReleaseDoc(release);
		            rep.setDocumentId(0l);
		            rep.setDocumentName(uploadDto.getDocumentName());
		            rep.setCreatedBy(uploadDto.getUserId());
		            rep.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
		            rep.setIsActive(1);
		            count1=dao.FileSubInsertNew(rep);
	            }
	            
				upload.setFileName(uploadDto.getFileNamePath());
				upload.setFilePass(Pass);
				upload.setReleaseDoc(release);
				upload.setVersionDoc(version);
				upload.setFileRepId(count1);
				upload.setFileNameUi(uploadDto.getDocumentName());
				upload.setCreatedBy(uploadDto.getUserId());
				upload.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
				upload.setIsActive(1);
				count=dao.FileUploadInsertNew(upload);
				
				Long Rev=upload.getReleaseDoc();
				Long Ver=upload.getVersionDoc();
				
				if(fullPathRef.get() != null && count>0) {
					if (!Files.exists(fullPathRef.get())) {
						Files.createDirectories(fullPathRef.get());
					}
					zip.pack(uploadDto.getFileNamePath(),uploadDto.getIS(),fullPathRef.get().toString(),uploadDto.getDocumentName()+Ver+"-"+Rev,Pass);
					
		        long count2=dao.FileRepRevUpdate(uploadDto.getFileId(),upload.getReleaseDoc(),version);
		        
		        List<Object[]> count3 = dao.getAttachmentId(uploadDto.getProjectId());
		        Long techDataId = 0L;
		        Long attachId = 0L;
		        if(count3!=null && count3.size()>0) {
		        	techDataId = count3.get(0)[0]!=null ? Long.parseLong(count3.get(0)[0].toString()) : 0L ;
			        attachId = count3.get(0)[1]!=null ? Long.parseLong(count3.get(0)[1].toString()) : 0L;
		        }
		        ProjectTechnicalWorkData tdata = techDataId!=0 ? dao.getProjectTechnicalWorkDataById(techDataId) : new ProjectTechnicalWorkData();
		        if(uploadDto.getAgendaId()==null) {
		        	tdata.setAttachmentId(count);
		        	tdata.setIsActive(1);
		        	if(techDataId!=0 && attachId>=0) {
		        		tdata.setModifiedBy(uploadDto.getUserId());
		        		tdata.setModifiedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
		        	} else {
			        	tdata.setProjectId(Long.parseLong(uploadDto.getProjectId()));
			        	tdata.setRelatedPoints(" ");
			        	tdata.setCreatedBy(uploadDto.getUserId());
			        	tdata.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
			        }
		        	projectDao.TechnicalWorkDataAdd(tdata);
		        }else {
		          if(! uploadDto.getAgendaId().equalsIgnoreCase("0")) {
		      	  CommitteeScheduleAgendaDocs docs = new CommitteeScheduleAgendaDocs();
				  docs.setAgendaId(Long.parseLong(uploadDto.getAgendaId()));
				  docs.setFileDocId(count); 
				  docs.setCreatedBy(uploadDto.getUserId());
				  docs.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
				  docs.setIsActive(1); 
				  committeDao.addAgendaLinkFile(docs); 
		          }
		        }
				
			 }
			 
		 }catch (Exception e) {
			 logger.error(new Date() +"Inside SERVICE DocFileUploadAjax "+ e);
			 e.printStackTrace();
		     count=0;
		}
		return count;
	}
	
	@Override
	public List<Object[]> getAttachmentId(String projectid) throws Exception {
		return dao.getAttachmentId(projectid);
	}
	
	@Override
	public long submitCheckboxFile(String userId, String techDataId, String attachid, String projectid) throws Exception {
		
		ProjectTechnicalWorkData modal = techDataId!=null ? dao.getProjectTechnicalWorkDataById(Long.parseLong(techDataId)) : new ProjectTechnicalWorkData();
		modal.setAttachmentId(Long.parseLong(attachid));
		modal.setIsActive(1);
		if(techDataId!=null) {
			modal.setModifiedBy(userId);
			modal.setModifiedDate(sdtf.format(new Date()));
		}else {
			modal.setProjectId(Long.parseLong(projectid));
			modal.setRelatedPoints("");
			modal.setCreatedBy(userId);
			modal.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
		}
		projectDao.TechnicalWorkDataAdd(modal);
		return 1;
	}

	@Override
	public List<Object[]> getMsprojectProcurementStatusList(String projectId) throws Exception {
		
		return dao.getMsprojectProcurementStatusList(projectId);
	}

	@Override
	public int mileStoneSerialNoUpdate(String[] newslno, String[] milestoneActivityId) {
		int ret=0;
		try {
			
			for(int i=0;i<milestoneActivityId.length;i++) {
				ret+=dao.mileStoneSerialNoUpdate(newslno[i], milestoneActivityId[i]);
			}
			return ret;
		
		}catch (Exception e) {
			e.printStackTrace();
			return ret;
		}
	}
	
	@Override
	public List<Object[]> getAllMilestoneActivityList() throws Exception {
		
		return dao.getAllMilestoneActivityList();
	}
	
	@Override
	public List<Object[]> getAllMilestoneActivityLevelList() throws Exception {

		return dao.getAllMilestoneActivityLevelList();
	}
	
	@Override
	public List<Object[]> getOldFileDocNames(String projectId,String fileType,String fileId) throws Exception {
		
		return dao.getOldFileDocNames(projectId,fileType,fileId);
	}
	
	@Override
	public long uploadFileData(FileUploadDto upload, String fileType) throws Exception {
		try {
			
			FileRepNew fileRepNew = new FileRepNew();
			FileRepUploadNew uploadNew = new FileRepUploadNew();
			
			Zipper zip=new Zipper();
			String Pass=dao.FilePass(upload.getUserId());
			String reportType = upload.getReportType();
			
			if(fileType.equalsIgnoreCase("mainLevel")) {
				Object[] repData = dao.RepMasterData(upload.getFileRepMasterId());
				
				Path docPath1=Paths.get(upload.getLabCode(),"docrepo",("P"+upload.getProjectId()),repData[2].toString());
				Path docPath2=Paths.get(FilePath,upload.getLabCode(),"docrepo",("P"+upload.getProjectId()),repData[2].toString());
				
				if(upload.getFileId()!=null && Long.parseLong(upload.getFileId())>0) {
					
					FileRepNew fileRep = dao.getFileRepById(Long.parseLong(upload.getFileId()));
					
					long version = upload.getIsNewVersion().toString().equalsIgnoreCase("Y") ? fileRep.getVersionDoc() + 1 : fileRep.getVersionDoc();
					long release = upload.getIsNewVersion().toString().equalsIgnoreCase("Y") ? 0l : fileRep.getReleaseDoc() + 1;
					
					fileRepNew.setFileRepId(fileRep.getFileRepId());
					fileRepNew.setVersionDoc(version);
					fileRepNew.setReleaseDoc(release);
					fileRepNew.setCreatedBy(upload.getUserId());
					fileRepNew.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
					dao.FileRepUpdate(fileRepNew);
					
		            uploadNew.setFileName(upload.getFileNamePath());
		            uploadNew.setFilePass(Pass);
		            uploadNew.setVersionDoc(version);
		            uploadNew.setReleaseDoc(release);
		            uploadNew.setFileRepId(fileRep.getFileRepId());
		            uploadNew.setFilePath(docPath1.toString());
		            uploadNew.setFileNameUi(upload.getDocumentName());
		            uploadNew.setCreatedBy(upload.getUserId());
		            uploadNew.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
		            uploadNew.setIsActive(1);
		            uploadNew.setReportType(reportType);
					long count = dao.FileUploadInsertNew(uploadNew);
					
					 if(count>0) {
						if (!Files.exists(docPath2)) {
							Files.createDirectories(docPath2);
						}
					   zip.pack(upload.getFileNamePath(),upload.getIS(),docPath2.toString(),upload.getDocumentName()+version+"-"+release,Pass);
				     }
					
				}else {
					fileRepNew.setProjectId(Long.parseLong(upload.getProjectId()));
					fileRepNew.setFileRepMasterId(Long.parseLong(upload.getFileRepMasterId()));
					fileRepNew.setSubL1(Long.parseLong(upload.getSubL1()));
					fileRepNew.setVersionDoc(Long.parseLong("1"));
					fileRepNew.setReleaseDoc(Long.parseLong("0"));
					fileRepNew.setDocumentId(0l);
					fileRepNew.setDocumentName(upload.getDocumentName());
					fileRepNew.setCreatedBy(upload.getUserId());
					fileRepNew.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
					fileRepNew.setIsActive(1);
		            long count = dao.FileSubInsertNew(fileRepNew);
		            
		            uploadNew.setFileName(upload.getFileNamePath());
		            uploadNew.setFilePass(Pass);
		            uploadNew.setVersionDoc(Long.parseLong("1"));
		            uploadNew.setReleaseDoc(Long.parseLong("0"));
		            uploadNew.setFileRepId(count);
		            uploadNew.setFilePath(docPath1.toString());
		            uploadNew.setFileNameUi(upload.getDocumentName());
		            uploadNew.setCreatedBy(upload.getUserId());
		            uploadNew.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
		            uploadNew.setIsActive(1);
		            uploadNew.setReportType(reportType);
					long count1 = dao.FileUploadInsertNew(uploadNew);
					
					 if(count1>0) {
						if (!Files.exists(docPath2)) {
							Files.createDirectories(docPath2);
						}
					   zip.pack(upload.getFileNamePath(),upload.getIS(),docPath2.toString(),upload.getDocumentName()+"1-0",Pass);
				     }
			    }
			}else {
				Object[] repSubData = dao.RepMasterData(upload.getSubL1());

				Path docPath3=Paths.get(upload.getLabCode(),"docrepo",("P"+upload.getProjectId()),repSubData[5].toString(),repSubData[2].toString());
				Path docPath4=Paths.get(FilePath,upload.getLabCode(),"docrepo",("P"+upload.getProjectId()),repSubData[5].toString(),repSubData[2].toString());
                
				 if(upload.getFileId()!=null && Long.parseLong(upload.getFileId())>0) {
					
					FileRepNew fileRep = dao.getFileRepById(Long.parseLong(upload.getFileId()));
					
					long version = upload.getIsNewVersion().toString().equalsIgnoreCase("Y") ? fileRep.getVersionDoc() + 1 : fileRep.getVersionDoc();
					long release = upload.getIsNewVersion().toString().equalsIgnoreCase("Y") ? 0l : fileRep.getReleaseDoc() + 1;
					
					fileRepNew.setFileRepId(fileRep.getFileRepId());
					fileRepNew.setVersionDoc(version);
					fileRepNew.setReleaseDoc(release);
					fileRepNew.setCreatedBy(upload.getUserId());
					fileRepNew.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
					dao.FileRepUpdate(fileRepNew);
					
		            uploadNew.setFileName(upload.getFileNamePath());
		            uploadNew.setFilePass(Pass);
		            uploadNew.setVersionDoc(version);
		            uploadNew.setReleaseDoc(release);
		            uploadNew.setFileRepId(fileRep.getFileRepId());
		            uploadNew.setFilePath(docPath3.toString());
		            uploadNew.setFileNameUi(upload.getDocumentName());
		            uploadNew.setCreatedBy(upload.getUserId());
		            uploadNew.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
		            uploadNew.setIsActive(1);
		            uploadNew.setReportType(reportType);
					long count = dao.FileUploadInsertNew(uploadNew);
					
					 if(count>0) {
						if (!Files.exists(docPath4)) {
							Files.createDirectories(docPath4);
						}
					   zip.pack(upload.getFileNamePath(),upload.getIS(),docPath4.toString(),upload.getDocumentName()+version+"-"+release,Pass);
				     }
					
				}else {
					fileRepNew.setProjectId(Long.parseLong(upload.getProjectId()));
					fileRepNew.setFileRepMasterId(Long.parseLong(upload.getFileRepMasterId()));
					fileRepNew.setSubL1(Long.parseLong(upload.getSubL1()));
					fileRepNew.setVersionDoc(Long.parseLong("1"));
					fileRepNew.setReleaseDoc(Long.parseLong("0"));
					fileRepNew.setDocumentId(0l);
					fileRepNew.setDocumentName(upload.getDocumentName());
					fileRepNew.setCreatedBy(upload.getUserId());
					fileRepNew.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
					fileRepNew.setIsActive(1);
		            long count = dao.FileSubInsertNew(fileRepNew);
		            
		            uploadNew.setFileName(upload.getFileNamePath());
		            uploadNew.setFilePass(Pass);
		            uploadNew.setVersionDoc(Long.parseLong("1"));
		            uploadNew.setReleaseDoc(Long.parseLong("0"));
		            uploadNew.setFileRepId(count);
		            uploadNew.setFilePath(docPath3.toString());
		            uploadNew.setFileNameUi(upload.getDocumentName());
		            uploadNew.setCreatedBy(upload.getUserId());
		            uploadNew.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
		            uploadNew.setIsActive(1);
		            uploadNew.setReportType(reportType);
					long count1 = dao.FileUploadInsertNew(uploadNew);
					
					 if(count1>0) {
						if (!Files.exists(docPath4)) {
							Files.createDirectories(docPath4);
						}
					   zip.pack(upload.getFileNamePath(),upload.getIS(),docPath4.toString(),upload.getDocumentName()+"1-0",Pass);
				     }
			    }
			}
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	@Override
	public List<Object[]> FileRepDocsList(String fileRepId) throws Exception {
		return dao.FileRepDocsList(fileRepId);
	}
	
	@Override
	public Optional<FileRepUploadNew> getFileById(Long id) throws Exception {
		return dao.getFileById(id);
	}
	
	@Override
	public int getFileRepMasterNames(String projectId, String fileType, String fileId, String fileName)
			throws Exception {
		return dao.getFileRepMasterNames(projectId,fileType,fileId,fileName);
	}
	
	@Override
	public long removeFileAttachment(String projectId, String techDataId, String techAttachId, String userId, String relativePoints) throws Exception {
		try {
			// Unlink the Document
			ProjectTechnicalWorkData workData = dao.getProjectTechnicalWorkDataById(Long.parseLong(techDataId));
			workData.setModifiedBy(userId);
			workData.setModifiedDate(sdtf.format(new Date()));
			workData.setAttachmentId(0L);
			workData.setIsActive(1);
			return projectDao.TechnicalWorkDataAdd(workData);
			
			// Add new row of data if Relative Points points are not empty
//			if(relativePoints!=null && !relativePoints.isEmpty()) {
//				ProjectTechnicalWorkData workData2 = new ProjectTechnicalWorkData();
//				workData2.setProjectId(Long.parseLong(projectId));
//				workData2.setRelatedPoints(relativePoints);
//				workData2.setAttachmentId(0L);
//				workData2.setCreatedBy(userId);
//				workData2.setCreatedDate(sdtf.format(new Date()));
//				workData2.setIsActive(1);
//				projectDao.TechnicalWorkDataAdd(workData2);
//			}
//			return 1;
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	
	@Override
	public MilestoneActivityLevel getMilestoneActivityLevelById(String id) {
		
		return dao.getMilestoneActivityLevelById(id);
	}
	
	@Override
	public long MilestoneActivityLevelSave(MilestoneActivityLevel level1) {
		try {
			dao.MilestoneActivityLevelInsert(level1);
			return 1;
		}catch (Exception e) {
				
			logger.error("Error inside MilestoneActivityLevelSave ",e.getMessage() );
			return 0;
		}
	}
	
	@Override
	public String getMainLevelId(Long getActivityId) throws Exception {
		
		return dao.getMainLevelId(getActivityId);
	}
	
	@Override
	public String getProjectIdByMainLevelId(String id) throws Exception {
		return dao.getProjectIdByMainLevelId(id);
	}

	@Override
	public MilestoneActivity getMilestoneActivityById(String id) {

		return dao.getMilestoneActivityById(id);
	}
	
	@Override
	public long MilestoneActivitySave(MilestoneActivity activity) throws Exception {

		return dao.MileActivityDetailsUpdtae(activity);
	}
	
	@Override
	public List<Object[]> actionAssigneeList(String EmpId) throws Exception {

		return dao.actionAssigneeList(EmpId);
	}
	
	@Override
	public long ActionAssignInsert(ActionAssign assign) throws Exception {

		return actionDao.ActionAssignInsert(assign);
	}

	@Override
	public int MilestoneTotalWeightage(String MilestoneActivityId) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}
	

	
	@Override
	public int deleteMilsetone(String activityId) throws Exception {
		try {
	
		return dao.deleteMilsetone(activityId);
		
		}catch (Exception e) {
			e.getMessage();
		}
		return 0;
	}

	@Override
	public List<Object[]> getMilestoneActivityProgressList() throws Exception {
		
		return dao.getMilestoneActivityProgressList();
	}
	
	
	@Override
	public List<Object[]> getPreProjectFolderList(String initiationId, String labcode) throws Exception {
		
		return dao.getPreProjectFolderList(initiationId,labcode);
	}
	
	@Override
	public List<Object[]> getPreProjectSubFolderList(String initiationId, String mainLevelId, String labcode)
			throws Exception {
		return dao.getPreProjectSubFolderList(initiationId,mainLevelId,labcode);
	}
	
	@Override
	public int getPreProjectFileRepMasterNames(String initiationId, String fileType, String fileId, String fileName) throws Exception{
		return dao.getPreProjectFileRepMasterNames(initiationId,fileType,fileId,fileName);
	}
	
	@Override
	public long preProjectRepMasterInsert(FileRepMasterPreProject fileRepo) throws Exception {
		long pi=0;
		fileRepo.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
		fileRepo.setIsActive(1);
		fileRepo.setParentLevelId(pi);
		fileRepo.setLevelId(pi+1);
		return dao.preProjectRepMasterInsert(fileRepo);
	}
	
	@Override
	public long preProjectFileRepMasterSubInsert(FileRepMasterPreProject fileRepo) throws Exception {
		fileRepo.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
		fileRepo.setIsActive(1);
		return dao.preProjectFileRepMasterSubInsert(fileRepo);
	}
	
	@Override
	public long preProjectfileEditSubmit(String filerepmasterid, String levelname, String levelType) throws Exception {
		String fileType = levelType.trim().toLowerCase();
		Optional<FileRepMasterPreProject> request = dao.getPreProjectFileRepById(Long.parseLong(filerepmasterid));
		if(request.isPresent()) {
			FileRepMasterPreProject repMaster = request.get();
			  switch (fileType) {
	          case "mainLevel":
	  	          Path filepath1 = Paths.get(FilePath, repMaster.getLabCode().toString(), "preProjectRepo", "P"+repMaster.getInitiationId().toString(), repMaster.getLevelName());
	  	          if (Files.exists(filepath1) || Files.isDirectory(filepath1)) {
	  	        	preProjectRenameMainFolder(levelType, repMaster.getLabCode(), repMaster.getInitiationId(), repMaster.getFileRepMasterId(), 0l, levelname.trim(), repMaster.getLevelName().trim());
	  	          }
	  	          break;
	          case "subLevel":
	        	  Long parentId = repMaster.getParentLevelId();
	        	  FileRepMasterPreProject master = dao.getPreProjectMainFileById(parentId).get();
	  	          Path filepath2 = Paths.get(FilePath, repMaster.getLabCode().toString(), "preProjectRepo", "P"+repMaster.getInitiationId().toString(), master.getLevelName(), repMaster.getLevelName());
	  	          if (Files.exists(filepath2) || Files.isDirectory(filepath2)) {
	  	        	preProjectRenameSubFolder(levelType, repMaster.getLabCode(), repMaster.getInitiationId(), parentId, repMaster.getFileRepMasterId(), master.getLevelName().trim(), levelname.trim(), repMaster.getLevelName().trim());
	  	          }
	  	          break;
	          }
		}
		return dao.preProjectfileEditSubmit(filerepmasterid, levelname);
	}
	
    private void preProjectRenameMainFolder(String levelType, String labCode, Long initoationId, Long mainRepMasterId, Long subRepMasterId, String newName, String oldName) throws Exception {
        Path oldPath = Paths.get(FilePath, labCode, "preProjectRepo", "P"+initoationId, oldName);
        Path newPath = Paths.get(FilePath, labCode, "preProjectRepo", "P"+initoationId, newName);

        if (!Files.exists(oldPath) || !Files.isDirectory(oldPath)) {
            throw new IllegalArgumentException("Main folder not found: " + oldPath);
        }
        
        // Prevent moving into itself
 		if (newPath.startsWith(oldPath)) {
 		    throw new IllegalArgumentException("Cannot move a folder into its subfolder: " + newPath);
 		}
     		
 		try {
     		// Attempt to rename (move) the folder
     		   Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
     	} catch (FileSystemException e) {
     		// Likely caused by file lock
     		throw new IOException("Failed to move folder. It may be open or locked by another process: " + e.getMessage(), e);
 		}

        List<Object[]> affectedFiles = dao.findByPreProjectFilePath(levelType,initoationId,mainRepMasterId,subRepMasterId,oldName);
        for (Object[] file : affectedFiles) {
        	Long uploadId = ((Number) file[0]).longValue(); 
            String oldPathStr = file[4].toString();
            if (oldPathStr != null) {
                // Split the path by slash or backslash
                String[] segments = oldPathStr.split("[/\\\\]");
                StringBuilder uploadPath = new StringBuilder();

                for (int i = 0; i < segments.length; i++) {
                    if (segments[i].equals(oldName)) {
                        segments[i] = newName; // Replace only exact match
                    }
                    uploadPath.append(segments[i]);
                    if (i < segments.length - 1) {
                    	uploadPath.append(File.separator);
                    }
                }

                dao.updatePreProjectFileById(uploadId, uploadPath.toString());
            }
        }
    }

    private void preProjectRenameSubFolder(String levelType, String labCode, Long initoationId, Long mainRepMasterId, Long subRepMasterId, String mainLevelName, String newName, String oldName) throws Exception {
        Path oldPath = Paths.get(FilePath, labCode, "preProjectRepo", "P"+initoationId, mainLevelName, oldName);
        Path newPath = Paths.get(FilePath, labCode, "preProjectRepo", "P"+initoationId, mainLevelName, newName);

        if (!Files.exists(oldPath) || !Files.isDirectory(oldPath)) {
            throw new IllegalArgumentException("Subfolder not found: " + oldPath);
        }

        // Prevent moving into itself
   		if (newPath.startsWith(oldPath)) {
   		    throw new IllegalArgumentException("Cannot move a folder into its subfolder: " + newPath);
   		}
       		
   		try {
       		// Attempt to rename (move) the folder
       		   Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
       	} catch (FileSystemException e) {
       		// Likely caused by file lock
       		throw new IOException("Failed to move folder. It may be open or locked by another process: " + e.getMessage(), e);
   		}

        List<Object[]> affectedFiles = dao.findByPreProjectFilePath(levelType,initoationId,mainRepMasterId,subRepMasterId,oldName);
        for (Object[] file : affectedFiles) {
        	Long uploadId = ((Number) file[0]).longValue(); 
            String oldPathStr = file[4].toString();
            if (oldPathStr != null) {
                // Split the path by slash or backslash
                String[] segments = oldPathStr.split("[/\\\\]");
                StringBuilder uploadPath = new StringBuilder();

                for (int i = 0; i < segments.length; i++) {
                    if (segments[i].equals(oldName)) {
                        segments[i] = newName; // Replace only exact match
                    }
                    uploadPath.append(segments[i]);
                    if (i < segments.length - 1) {
                    	uploadPath.append(File.separator);
                    }
                }

                dao.updatePreProjectFileById(uploadId, uploadPath.toString());
            }
        }
    }
    
    @Override
    public List<Object[]> getPreProjectOldFileNames(String initiationId, String fileType, String fileId)
    		throws Exception {
    	return dao.getPreProjectOldFileNames(initiationId, fileType, fileId);
    }
    
    @Override
    public long uploadPreProjectFile(FileUploadDto upload, String fileType) throws Exception {
      try {
    	    FileRepNewPreProject fileRepNew = new FileRepNewPreProject();
			FileRepUploadPreProject uploadNew = new FileRepUploadPreProject();
			
			Zipper zip=new Zipper();
			String Pass=dao.FilePass(upload.getUserId());
			
			if(fileType.equalsIgnoreCase("mainLevel")) {
				Object[] repData = dao.preProjectRepMaster(upload.getFileRepMasterId());
				
				Path docPath1=Paths.get(upload.getLabCode(),"preProjectRepo",("P"+upload.getInitiationId()),repData[2].toString());
				Path docPath2=Paths.get(FilePath,upload.getLabCode(),"preProjectRepo",("P"+upload.getInitiationId()),repData[2].toString());
				
				if(upload.getFileId()!=null && Long.parseLong(upload.getFileId())>0) {
					
					FileRepNewPreProject fileRep = dao.getPreProjectFileById(Long.parseLong(upload.getFileId()));
					
					long version = upload.getIsNewVersion().toString().equalsIgnoreCase("Y") ? fileRep.getVersionDoc() + 1 : fileRep.getVersionDoc();
					long release = upload.getIsNewVersion().toString().equalsIgnoreCase("Y") ? 0l : fileRep.getReleaseDoc() + 1;
					
					fileRepNew.setFileRepId(fileRep.getFileRepId());
					fileRepNew.setVersionDoc(version);
					fileRepNew.setReleaseDoc(release);
					fileRepNew.setCreatedBy(upload.getUserId());
					fileRepNew.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
					dao.prepRojectFileUpdate(fileRepNew);
					
		            uploadNew.setFileName(upload.getFileNamePath());
		            uploadNew.setFilePass(Pass);
		            uploadNew.setVersionDoc(version);
		            uploadNew.setReleaseDoc(release);
		            uploadNew.setFileRepId(fileRep.getFileRepId());
		            uploadNew.setFilePath(docPath1.toString());
		            uploadNew.setFileNameUi(upload.getDocumentName());
		            uploadNew.setCreatedBy(upload.getUserId());
		            uploadNew.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
		            uploadNew.setIsActive(1);
					long count = dao.preProjectFileUploadInsert(uploadNew);
					
					 if(count>0) {
						if (!Files.exists(docPath2)) {
							Files.createDirectories(docPath2);
						}
					   zip.pack(upload.getFileNamePath(),upload.getIS(),docPath2.toString(),upload.getDocumentName()+version+"-"+release,Pass);
				     }
					
				}else {
					fileRepNew.setInitiationId(Long.parseLong(upload.getInitiationId()));
					fileRepNew.setFileRepMasterId(Long.parseLong(upload.getFileRepMasterId()));
					fileRepNew.setSubL1(Long.parseLong(upload.getSubL1()));
					fileRepNew.setVersionDoc(Long.parseLong("1"));
					fileRepNew.setReleaseDoc(Long.parseLong("0"));
					fileRepNew.setDocumentName(upload.getDocumentName());
					fileRepNew.setCreatedBy(upload.getUserId());
					fileRepNew.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
					fileRepNew.setIsActive(1);
		            long count = dao.preProjectFileSubInsert(fileRepNew);
		            
		            uploadNew.setFileName(upload.getFileNamePath());
		            uploadNew.setFilePass(Pass);
		            uploadNew.setVersionDoc(Long.parseLong("1"));
		            uploadNew.setReleaseDoc(Long.parseLong("0"));
		            uploadNew.setFileRepId(count);
		            uploadNew.setFilePath(docPath1.toString());
		            uploadNew.setFileNameUi(upload.getDocumentName());
		            uploadNew.setCreatedBy(upload.getUserId());
		            uploadNew.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
		            uploadNew.setIsActive(1);
					long count1 = dao.preProjectFileUploadInsert(uploadNew);
					
					 if(count1>0) {
						if (!Files.exists(docPath2)) {
							Files.createDirectories(docPath2);
						}
					   zip.pack(upload.getFileNamePath(),upload.getIS(),docPath2.toString(),upload.getDocumentName()+"1-0",Pass);
				     }
			    }
			}else {
				Object[] repSubData = dao.preProjectRepMaster(upload.getSubL1());

				Path docPath3=Paths.get(upload.getLabCode(),"preProjectRepo",("P"+upload.getInitiationId()),repSubData[5].toString(),repSubData[2].toString());
				Path docPath4=Paths.get(FilePath,upload.getLabCode(),"preProjectRepo",("P"+upload.getInitiationId()),repSubData[5].toString(),repSubData[2].toString());
                
				 if(upload.getFileId()!=null && Long.parseLong(upload.getFileId())>0) {
					
					FileRepNew fileRep = dao.getFileRepById(Long.parseLong(upload.getFileId()));
					
					long version = upload.getIsNewVersion().toString().equalsIgnoreCase("Y") ? fileRep.getVersionDoc() + 1 : fileRep.getVersionDoc();
					long release = upload.getIsNewVersion().toString().equalsIgnoreCase("Y") ? 0l : fileRep.getReleaseDoc() + 1;
					
					fileRepNew.setFileRepId(fileRep.getFileRepId());
					fileRepNew.setVersionDoc(version);
					fileRepNew.setReleaseDoc(release);
					fileRepNew.setCreatedBy(upload.getUserId());
					fileRepNew.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
					dao.prepRojectFileUpdate(fileRepNew);
					
		            uploadNew.setFileName(upload.getFileNamePath());
		            uploadNew.setFilePass(Pass);
		            uploadNew.setVersionDoc(version);
		            uploadNew.setReleaseDoc(release);
		            uploadNew.setFileRepId(fileRep.getFileRepId());
		            uploadNew.setFilePath(docPath3.toString());
		            uploadNew.setFileNameUi(upload.getDocumentName());
		            uploadNew.setCreatedBy(upload.getUserId());
		            uploadNew.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
		            uploadNew.setIsActive(1);
					long count = dao.preProjectFileUploadInsert(uploadNew);
					
					 if(count>0) {
						if (!Files.exists(docPath4)) {
							Files.createDirectories(docPath4);
						}
					   zip.pack(upload.getFileNamePath(),upload.getIS(),docPath4.toString(),upload.getDocumentName()+version+"-"+release,Pass);
				     }
					
				}else {
					fileRepNew.setInitiationId(Long.parseLong(upload.getInitiationId()));
					fileRepNew.setFileRepMasterId(Long.parseLong(upload.getFileRepMasterId()));
					fileRepNew.setSubL1(Long.parseLong(upload.getSubL1()));
					fileRepNew.setVersionDoc(Long.parseLong("1"));
					fileRepNew.setReleaseDoc(Long.parseLong("0"));
					fileRepNew.setDocumentName(upload.getDocumentName());
					fileRepNew.setCreatedBy(upload.getUserId());
					fileRepNew.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
					fileRepNew.setIsActive(1);
		            long count = dao.preProjectFileSubInsert(fileRepNew);
		            
		            uploadNew.setFileName(upload.getFileNamePath());
		            uploadNew.setFilePass(Pass);
		            uploadNew.setVersionDoc(Long.parseLong("1"));
		            uploadNew.setReleaseDoc(Long.parseLong("0"));
		            uploadNew.setFileRepId(count);
		            uploadNew.setFilePath(docPath3.toString());
		            uploadNew.setFileNameUi(upload.getDocumentName());
		            uploadNew.setCreatedBy(upload.getUserId());
		            uploadNew.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
		            uploadNew.setIsActive(1);
					long count1 = dao.preProjectFileUploadInsert(uploadNew);
					
					 if(count1>0) {
						if (!Files.exists(docPath4)) {
							Files.createDirectories(docPath4);
						}
					   zip.pack(upload.getFileNamePath(),upload.getIS(),docPath4.toString(),upload.getDocumentName()+"1-0",Pass);
				     }
			    }
			}
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
    }
    
    @Override
    public Optional<FileRepUploadPreProject> getPreProjectFileById(Long id) throws Exception {
    	return dao.getPreProjectUploadFileById(id);
    }
    
    
    @Override
    public List<Object[]> preProjectFileRepDocsList(String fileRepId) throws Exception {
    	return dao.preProjectFileRepoDocsList(fileRepId);
    }
    
    @Override
    public List<Object[]> getPreProjectFileRepMasterListAll(String initiationId, String labCode) throws Exception {
    	return dao.getPreProjectFileRepMasterListAll(initiationId,labCode);
    }

	@Override
	public int saveMilestoneActivityLevelRemarks(MilestoneActivityLevelRemarks cmd) throws Exception {
		return dao.saveMilestoneActivityLevelRemarks(cmd);
	}

	
	@Override
	public List<Object[]> getMilestoneDraftRemarks(Long activityId) throws Exception {
	
		return dao.getMilestoneDraftRemarks(activityId);
	}

	@Override
	public Long saveMilestoneActivityPredecessor(MilestoneActivityPredecessor mp) throws Exception {
		
		return dao.saveMilestoneActivityPredecessor(mp);
	}
	
	@Override
	public List<Object[]> predecessorList(String successor) throws Exception {
		
		return dao.predecessorList(successor);
	}
	
	@Override
	public List<Object[]> getsuccessorList(String activityId) throws Exception {
		return dao.getsuccessorList(activityId);
	}
	
	@Override
	public int deleteMilestoneActivityPredecessor(String successor) throws Exception {
		
		return dao.deleteMilestoneActivityPredecessor(successor);
	}
	
	@Override
	public long  saveMilestoneSub(MilestoneActivitySub attach) throws Exception {
		return dao.MilestoneActivitySubInsert(attach);
	}

	@Override
	public Object[] getprojectDetails(String projectId) throws Exception {
		return dao.getprojectDetails(projectId);
	}

	@Override
	public Object[] getFileRepUploadDetails(String projectId) throws Exception {
		return dao.getFileRepUploadDetails(projectId);
	}

	// Naveen 05-03-2026
	@Override
	public long saveMilestoneActivityBriefing(MilestoneActivityBriefing entity) throws Exception  {
		return dao.saveMilestoneActivityBriefing(entity);
	}

	@Override
	public MilestoneActivityBriefing getMilestoneActivityBriefing(String mileStoneActivityBriefingId) throws Exception  {
		return dao.getMilestoneActivityBriefing(mileStoneActivityBriefingId);
	}

	@Override
	public Object[] getMilestoneActivityBriefingById(String milestoneid) throws Exception  {
		return dao.getMilestoneActivityBriefingById(milestoneid);
	}

	@Override
	public long deleteMainLevelMilsetone(String mainId) throws Exception {
		return dao.deleteMainLevelMilsetone(mainId);
	}

	@Override
	public long deleteSubLevelMilsetone(String subId) throws Exception {
		return dao.deleteSubLevelMilsetone(subId);
	}

	@Override
	public long updateSunSet(String milestoneActivityId, String levelId, String isSunSet) throws Exception {
		Long id = milestoneActivityId != null && !milestoneActivityId.isBlank() ? Long.parseLong(milestoneActivityId) : 0L;
//		if("1".equals(levelId)) {
			return dao.updateMainMileStoneSunSet(id, "Y".equalsIgnoreCase(isSunSet) ? "N" : "Y");
//		}
//		return dao.updateSubMileStoneSunSet(id,"Y".equalsIgnoreCase(isSunSet) ? "N" : "Y");
	}
	
	@Override
	public String getFinancialYear(String pdcDate, String projectEndDate) throws Exception {

	    LocalDate date;

	    if (pdcDate != null && !pdcDate.isEmpty() && projectEndDate != null && !projectEndDate.isEmpty()) {

	        date = LocalDate.parse(projectEndDate);

	    } else {
	        date = LocalDate.now();
	    }

	    int year = date.getYear();

	    if (date.getMonthValue() >= 4) {
	        return year + "-" + (year + 1);
	    } else {
	        return (year - 1) + "-" + year;
	    }
	}
	
	@Override
	public List<String> getFinancialYearBasedOnProject(String pdcDate,String sanctionDate) throws Exception {

	    LocalDate sanction = LocalDate.parse(sanctionDate);
	    LocalDate pdc = LocalDate.parse(pdcDate);

	    List<String> financialYears = new ArrayList<String>();

	    int startYear;

	    if (sanction.getMonthValue() >= 4) {
	        startYear = sanction.getYear();
	    } else {
	        startYear = sanction.getYear() - 1;
	    }

	    int endYear;

	    if (pdc.getMonthValue() >= 4) {
	        endYear = pdc.getYear();
	    } else {
	        endYear = pdc.getYear() - 1;
	    }

	    for (int year = startYear; year <= endYear; year++) {
	        financialYears.add(year + "-" + (year + 1));
	    }

	    return financialYears;
	}
	
	@Override
	public Object[] getManPowerUtilizationCounts(String projectId, String finYear, String quarter) throws Exception {
		return dao.getManPowerUtilizationCounts(projectId,finYear,quarter);
	}
	
	@Override
	public long addManPowerUtilization(ProjectManPowerUtilizationDto dto) throws Exception {

	    long projectId = Long.parseLong(dto.getProjectId());

	    long sciCount  = toCount(dto.getScientistCount());
	    long techCount = toCount(dto.getTechnicalCount());
	    long admCount  = toCount(dto.getAdminCount());
	    	    
	    long sciDaysCount  = toCount(dto.getSciDaysCount());
	    long techDaysCount = toCount(dto.getTechDaysCount());
	    long admDyasCount  = toCount(dto.getAdminDaysCount());

	    String[] cadreCodes = { "DRDS", "DTDC", "Admin & Allied" };
	    long[] counts = { sciCount, techCount, admCount };
	    long[] daysCount = {sciDaysCount, techDaysCount, admDyasCount};
	    long count = 0;
	    
	    ProjectResourceUtilization resource = dao.findOrCreateResourceUtilization(projectId, dto.getFinancialYear(), dto.getQuarter(), dto.getCreatedBy());

	    for (int i = 0; i < 3; i++) {
	        ProjectManPowerUtilization entity = new ProjectManPowerUtilization();

	        entity.setResourceUtilizationId(resource.getResourceUtilizationId());
	        entity.setDesigCadre(cadreCodes[i]);
	        entity.setManPowerCount(counts[i]);
	        entity.setManPowerDays(daysCount[i]);
	        entity.setRevisionNo(0L);
	        entity.setCreatedBy(dto.getCreatedBy());
	        entity.setCreatedDate(dto.getCreatedDate());
	        entity.setIsActive(1);

	        count += dao.addManPowerUtilization(entity);
	    }

	    return count;
	}

	private long toCount(String raw) {
	    return (raw != null && !raw.isBlank()) ? Long.parseLong(raw) : 0L;
	}
		
	@Override
	public long updateManPowerUtilization(ProjectManPowerUtilizationDto dto) throws Exception {

	    long projectId = Long.parseLong(dto.getProjectId());

	    long sciCount  = toCount(dto.getScientistCount());
	    long techCount = toCount(dto.getTechnicalCount());
	    long admCount  = toCount(dto.getAdminCount());
	    
		long sciDaysCount  = toCount(dto.getSciDaysCount());
		long techDaysCount = toCount(dto.getTechDaysCount());
		long admDyasCount  = toCount(dto.getAdminDaysCount());

		String action = dto.getAction();
	    
	    ProjectResourceUtilization resource = dao.findResourceUtilizationId(projectId, dto.getFinancialYear(), dto.getQuarter());
	    
	    if (resource == null || resource.getResourceUtilizationId() == null) {
	        throw new IllegalStateException("Nothing to edit — this quarter has never been saved.");
	    }	    

	    List<ProjectManPowerUtilization> existingList = dao.getManPowerUtilization(resource.getResourceUtilizationId());

	    if (existingList == null || existingList.isEmpty()) {
	        throw new Exception("Manpower utilization record not found");
	    }

	    long currentRevision = existingList.get(0).getRevisionNo();
	    
	    
	    long newRevision = currentRevision + 1;
	    
	    long count = 0;
	    
	    if("EDIT".equalsIgnoreCase(action)) {

		    if (currentRevision > 0) {
		        throw new IllegalStateException("This quarter has already been revised — use Revise instead of Edit.");
		    }
		    
	    	for (ProjectManPowerUtilization existing : existingList) {

	    	    String cadre = existing.getDesigCadre();

	    	    if ("DRDS".equalsIgnoreCase(cadre)) {

	    	        existing.setManPowerCount(sciCount);
	    	        existing.setManPowerDays(sciDaysCount);

	    	    } else if ("DTDC".equalsIgnoreCase(cadre)) {

	    	        existing.setManPowerCount(techCount);
	    	        existing.setManPowerDays(techDaysCount);

	    	    } else if ("Admin & Allied".equalsIgnoreCase(cadre)) {

	    	        existing.setManPowerCount(admCount);
	    	        existing.setManPowerDays(admDyasCount);
	    	    }
	    	    
	    	    existing.setModifiedBy(dto.getCreatedBy());
	    	    existing.setModifiedDate(LocalDateTime.now());
	    	    
	    	    
	    	    count += dao.addManPowerUtilization(existing);
	    	}
	    }else if("REVISE".equalsIgnoreCase(action)){
	
		    for (ProjectManPowerUtilization oldEntity : existingList) {
		        ProjectManPowerUtilizationRev revision =   new ProjectManPowerUtilizationRev();
	
		        revision.setResourceUtilizationId(oldEntity.getResourceUtilizationId());
		        revision.setManPowerCount(oldEntity.getManPowerCount());
		        revision.setDesigCadre(oldEntity.getDesigCadre());
		        revision.setManPowerDays(oldEntity.getManPowerDays());
		        
		        revision.setCreatedBy(oldEntity.getCreatedBy());
		        revision.setCreatedDate(oldEntity.getCreatedDate());
		        
		        revision.setRevisionDate(LocalDateTime.now());
		        revision.setRevisionNo(oldEntity.getRevisionNo());
		        
		        revision.setIsActive(oldEntity.getIsActive());
		        revision.setModifiedBy(oldEntity.getModifiedBy());
		        revision.setModifiedDate(oldEntity.getModifiedDate());
		        
		        dao.addManPowerUtilizationRevision(revision);
		    }

		    for (ProjectManPowerUtilization entity : existingList) {

	    	    String cadre = entity.getDesigCadre();

	    	    if ("DRDS".equalsIgnoreCase(cadre)) {

	    	    	entity.setManPowerCount(sciCount);
	    	    	entity.setManPowerDays(sciDaysCount);

	    	    } else if ("DTDC".equalsIgnoreCase(cadre)) {

	    	    	entity.setManPowerCount(techCount);
	    	    	entity.setManPowerDays(techDaysCount);

	    	    } else if ("Admin & Allied".equalsIgnoreCase(cadre)) {

	    	    	entity.setManPowerCount(admCount);
	    	    	entity.setManPowerDays(admDyasCount);
	    	    }

		        entity.setRevisionNo(newRevision);
		        entity.setModifiedBy(dto.getCreatedBy());
		        entity.setModifiedDate(LocalDateTime.now());
	    	    
	    	    
	    	    count += dao.addManPowerUtilization(entity);
	    	}
	    }
	    return count;
	}
	
	@Override
	public List<String> getQuartersBasedOnProject(String sanctionDate,String pdcDate,String finYear) {

	    List<String> quarterList = new ArrayList<String>();

	    LocalDate sanction = null;
	    LocalDate pdc = null;

	    if (sanctionDate != null && !sanctionDate.trim().isEmpty()) {
	        sanction = LocalDate.parse(sanctionDate);
	    }

	    if (pdcDate != null && !pdcDate.trim().isEmpty()) {
	        pdc = LocalDate.parse(pdcDate);
	    }

	    String[] years = finYear.split("-");

	    int startYear = Integer.parseInt(years[0]);

	    LocalDate fyStart = LocalDate.of(startYear, 4, 1);
	    LocalDate fyEnd = LocalDate.of(startYear + 1, 3, 31);

	    // Q1: Apr-Jun
	    LocalDate q1Start = LocalDate.of(startYear, 4, 1);
	    LocalDate q1End = LocalDate.of(startYear, 6, 30);

	    // Q2: Jul-Sep
	    LocalDate q2Start = LocalDate.of(startYear, 7, 1);
	    LocalDate q2End = LocalDate.of(startYear, 9, 30);

	    // Q3: Oct-Dec
	    LocalDate q3Start = LocalDate.of(startYear, 10, 1);
	    LocalDate q3End = LocalDate.of(startYear, 12, 31);

	    // Q4: Jan-Mar
	    LocalDate q4Start = LocalDate.of(startYear + 1, 1, 1);
	    LocalDate q4End = LocalDate.of(startYear + 1, 3, 31);



	    // Check each quarter
	    if (isQuarterAllowed(q1Start, q1End, sanction, pdc, finYear)) {
	        quarterList.add("Q1");
	    }

	    if (isQuarterAllowed(q2Start, q2End, sanction, pdc, finYear)) {
	        quarterList.add("Q2");
	    }

	    if (isQuarterAllowed(q3Start, q3End, sanction, pdc, finYear)) {
	        quarterList.add("Q3");
	    }

	    if (isQuarterAllowed(q4Start, q4End, sanction, pdc, finYear)) {
	        quarterList.add("Q4");
	    }

	    return quarterList;
	}
	
	private boolean isQuarterAllowed(LocalDate quarterStart,LocalDate quarterEnd,LocalDate sanction,LocalDate pdc,String finYear) {

	    LocalDate today = LocalDate.now();
	    
	    if (sanction != null && quarterEnd.isBefore(sanction)) {
	        return false;
	    }

	    if (pdc != null && quarterStart.isAfter(pdc)) {
	        return false;
	    }

	    LocalDate currentDate = LocalDate.now();

	    int currentYear = currentDate.getYear();

	    int currentStartYear = currentDate.getMonthValue() >= 4 ? currentYear : currentYear - 1;

	    int selectedStartYear = Integer.parseInt(finYear.substring(0, 4));

	    if (selectedStartYear == currentStartYear && quarterStart.isAfter(today)) {
	        return false;
	    }

	    return true;
	}
	
	
	@Override
	public long saveInfrastructureUtilization(ProjectInfrastructureUtilizationDto dto) throws Exception {
	    long projectId = Long.parseLong(dto.getProjectId());
	    long count = 0;
	    try {
	    	
	    	List<InfrastructureItemDto> items = dto.getItems();
	    	
	    	if(items == null || items.isEmpty()) {
	    		return 0;
	    	}
	    	
	    	ProjectResourceUtilization resource = dao.findOrCreateResourceUtilization(projectId, dto.getFinYear(), dto.getQuarter(), dto.getCreatedBy());
	    	
	    	for(InfrastructureItemDto item: items) {
	    		ProjectInfrastructureUtilization entity = new ProjectInfrastructureUtilization();
	    		
	    		entity.setNameOfInfrastructure(item.getInfraName());
	    		entity.setDaysUtilized(item.getDaysUtilized() != null && !item.getDaysUtilized().isBlank() ? Long.parseLong(item.getDaysUtilized()) : null);
	    		entity.setResourceUtilizationId(resource.getResourceUtilizationId());
	    		entity.setRevisionNo(0L);
	    		entity.setCreatedBy(dto.getCreatedBy());
	    		entity.setCreatedDate(LocalDateTime.now());
	    		entity.setIsActive(1);
	    		
	    		count += dao.saveProjectInfrastructureUtilization(entity);
	    	}

	    }catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	    return count;
	}
	
	
	@Override
	public List<Object[]> getInfrastructureItems(String projectId, String finYear, String quarter) throws Exception {
		return dao.getInfrastructureItems(projectId,finYear,quarter);
	}
	
	@Override
	public String getInfrastructureMode(Long projectId, String finYear, String quarter) throws Exception {
		ProjectResourceUtilization resource = dao.findResourceUtilizationId(projectId, finYear, quarter);
	    if (resource == null || resource.getResourceUtilizationId() == null) return "ADD";

	    List<ProjectInfrastructureUtilization> active = dao.getActiveInfrastructure(resource.getResourceUtilizationId());
	    if (active.isEmpty()) return "ADD";

	    return (active.get(0).getRevisionNo() == 0) ? "EDIT" : "REVISE";
	}

	@Override
	public long editInfrastructureUtilization(ProjectInfrastructureUtilizationDto dto) throws Exception {
	    long projectId = Long.parseLong(dto.getProjectId());
	    
	    List<InfrastructureItemDto> items = dto.getItems();
	    if (items == null || items.isEmpty()) return 0;

	    ProjectResourceUtilization resource = dao.findResourceUtilizationId(projectId, dto.getFinYear(), dto.getQuarter());
	    
	    if (resource == null || resource.getResourceUtilizationId() == null) {
	        throw new IllegalStateException("Nothing to edit — this quarter has never been saved.");
	    }
	    

	    List<ProjectInfrastructureUtilization> active = dao.getActiveInfrastructure(resource.getResourceUtilizationId());

	    if ( active !=null && !active.isEmpty() && active.get(0).getRevisionNo() > 0) {
	        throw new IllegalStateException("This quarter has already been revised — use Revise instead of Edit.");
	    }
	    
	    Map<Long, ProjectInfrastructureUtilization> existingById = new HashMap<>();
	    for (ProjectInfrastructureUtilization row : active) {
	        existingById.put(row.getInfrastructureUtilizationId(), row);
	    }
	    
	    Set<Long> submittedIds = new HashSet<>();
	    long count = 0;
	    
	    for (InfrastructureItemDto item : items) {
	        if (item.getInfraName() == null || item.getInfraName().isBlank()) continue;

	        Long itemId = (item.getInfraUtilizationId() != null && !item.getInfraUtilizationId().isBlank())
	                ? Long.parseLong(item.getInfraUtilizationId()) : null;
	        Long days = (item.getDaysUtilized() != null && !item.getDaysUtilized().isBlank())
	                ? Long.parseLong(item.getDaysUtilized()) : null;

	        if (itemId != null && existingById.containsKey(itemId)) {
	            ProjectInfrastructureUtilization existing = existingById.get(itemId);
	            existing.setNameOfInfrastructure(item.getInfraName().trim());
	            existing.setDaysUtilized(days);
	            dao.updateProjectInfrastructureUtilization(existing);
	            submittedIds.add(itemId);
	            count++;
	        } else {
	            ProjectInfrastructureUtilization entity = new ProjectInfrastructureUtilization();
	            entity.setNameOfInfrastructure(item.getInfraName().trim());
	            entity.setDaysUtilized(days);
	            entity.setResourceUtilizationId(resource.getResourceUtilizationId());
	            entity.setRevisionNo(0L);
	            entity.setCreatedBy(dto.getCreatedBy());
	            entity.setCreatedDate(LocalDateTime.now());
	            entity.setIsActive(1);
	            count += dao.saveProjectInfrastructureUtilization(entity);
	        }
	    }

	    for (ProjectInfrastructureUtilization row : active) {
	        if (!submittedIds.contains(row.getInfrastructureUtilizationId())) {
	            dao.deleteProjectInfrastructureUtilization(row);
	        }
	    }

	    return count;
	}

	@Override
	public long reviseInfrastructureUtilization(ProjectInfrastructureUtilizationDto dto) throws Exception {
	    long projectId = Long.parseLong(dto.getProjectId());
	    
	    List<InfrastructureItemDto> items = dto.getItems();
	    if (items == null || items.isEmpty()) return 0;

	    ProjectResourceUtilization resource = dao.findResourceUtilizationId(projectId, dto.getFinYear(), dto.getQuarter());
	    
	    if (resource == null || resource.getResourceUtilizationId() == null) {
	        throw new IllegalStateException("Nothing to revise — this quarter has never been saved.");
	    }

	    List<ProjectInfrastructureUtilization> active = dao.getActiveInfrastructure(resource.getResourceUtilizationId());
	   
	    long nextRevision =  active.isEmpty() ? 1L : active.get(0).getRevisionNo() + 1;;

	    for (ProjectInfrastructureUtilization row : active) {
	    	ProjectInfrastructureUtilizationRev rev = new ProjectInfrastructureUtilizationRev();
	    	
	    	rev.setNameOfInfrastructure(row.getNameOfInfrastructure());
	    	rev.setDaysUtilized(row.getDaysUtilized());
	    	rev.setResourceUtilizationId(row.getResourceUtilizationId());
	    	rev.setRevisionNo(row.getRevisionNo());
	    	rev.setRevisionDate(LocalDateTime.now());

	    	rev.setCreatedBy(dto.getCreatedBy());
	    	rev.setCreatedDate(LocalDateTime.now());
	    	rev.setIsActive(row.getIsActive());
	    	
	    	dao.saveProjectInfrastructureUtilizationRev(rev);
	        dao.deleteProjectInfrastructureUtilization(row);
	    }

	    long count = 0;
	    for (InfrastructureItemDto item : items) {
	        if (item.getInfraName() == null || item.getInfraName().isBlank()) continue;

	        ProjectInfrastructureUtilization entity = new ProjectInfrastructureUtilization();
	        entity.setNameOfInfrastructure(item.getInfraName());
	        entity.setDaysUtilized(item.getDaysUtilized() != null && !item.getDaysUtilized().isBlank() ? Long.parseLong(item.getDaysUtilized()) : null);
	        entity.setResourceUtilizationId(resource.getResourceUtilizationId());
	        entity.setRevisionNo(nextRevision);
	        entity.setCreatedBy(dto.getCreatedBy());
	        entity.setCreatedDate(LocalDateTime.now());
	        entity.setIsActive(1);

	        count += dao.saveProjectInfrastructureUtilization(entity);
	    }
	    
	    return count;
	}

	@Override
	public long saveTrainingUtilization(ProjectTrainingUtilizationDto dto) throws Exception {
	    long projectId = Long.parseLong(dto.getProjectId());
	    long count = 0;
	    try {
	        List<TrainingItemDto> items = dto.getItems();
	        
	        if (items == null || items.isEmpty()) {
	            return 0;
	        }
	        
	        ProjectResourceUtilization resource = dao.findOrCreateResourceUtilization(projectId, dto.getFinYear(), dto.getQuarter(), dto.getCreatedBy());
	        
	        for (TrainingItemDto item : items) {
	            if (item.getTrainingName() == null || item.getTrainingName().isBlank()) continue;

	            ProjectTrainingUtilization entity = new ProjectTrainingUtilization();
	            
	            entity.setNameOfTraining(item.getTrainingName().trim());
	            entity.setCost(item.getCost() != null && !item.getCost().isBlank() ? new BigDecimal(item.getCost()) : BigDecimal.ZERO);
	            entity.setResourceUtilizationId(resource.getResourceUtilizationId());
	            entity.setRevisionNo(0L);
	            entity.setCreatedBy(dto.getCreatedBy());
	            entity.setCreatedDate(LocalDateTime.now()); 
	            entity.setIsActive(1);
	            
	            count += dao.saveProjectTrainingUtilization(entity);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return 0;
	    }
	    return count;
	}

	@Override
	public List<Object[]> getTrainingItems(String projectId, String finYear, String quarter) throws Exception {
	    return dao.getTrainingItems(projectId, finYear, quarter);
	}

	@Override
	public String getTrainingMode(Long projectId, String finYear, String quarter) throws Exception {
	    ProjectResourceUtilization resource = dao.findResourceUtilizationId(projectId, finYear, quarter);
	    if (resource == null || resource.getResourceUtilizationId() == null) return "ADD";

	    List<ProjectTrainingUtilization> active = dao.getActiveTraining(resource.getResourceUtilizationId());
	    if (active.isEmpty()) return "ADD";

	    return (active.get(0).getRevisionNo() == 0) ? "EDIT" : "REVISE";
	}

	@Override
	public long editTrainingUtilization(ProjectTrainingUtilizationDto dto) throws Exception {
	    long projectId = Long.parseLong(dto.getProjectId());
	    
	    List<TrainingItemDto> items = dto.getItems();
	    if (items == null || items.isEmpty()) return 0;

	    ProjectResourceUtilization resource = dao.findResourceUtilizationId(projectId, dto.getFinYear(), dto.getQuarter());
	    
	    if (resource == null || resource.getResourceUtilizationId() == null) {
	        throw new IllegalStateException("Nothing to edit — this quarter has never been saved.");
	    }
	    
	    List<ProjectTrainingUtilization> active = dao.getActiveTraining(resource.getResourceUtilizationId());

	    if (active != null && !active.isEmpty() && active.get(0).getRevisionNo() > 0) {
	        throw new IllegalStateException("This quarter has already been revised — use Revise instead of Edit.");
	    }
	    
	    Map<Long, ProjectTrainingUtilization> existingById = new HashMap<>();
	    for (ProjectTrainingUtilization row : active) {
	        existingById.put(row.getTrainingUtilizationId(), row);
	    }
	    
	    Set<Long> submittedIds = new HashSet<>();
	    long count = 0;
	    
	    for (TrainingItemDto item : items) {
	        if (item.getTrainingName() == null || item.getTrainingName().isBlank()) continue;

	        Long itemId = (item.getTrainingUtilizationId() != null && !item.getTrainingUtilizationId().isBlank())
	                ? Long.parseLong(item.getTrainingUtilizationId()) : null;
	        BigDecimal cost = (item.getCost() != null && !item.getCost().isBlank())
	                ? new BigDecimal(item.getCost()) : BigDecimal.ZERO;

	        if (itemId != null && existingById.containsKey(itemId)) {
	            ProjectTrainingUtilization existing = existingById.get(itemId);
	            existing.setNameOfTraining(item.getTrainingName().trim());
	            existing.setCost(cost);
	            existing.setModifiedBy(dto.getCreatedBy());
	            existing.setModifiedDate(LocalDateTime.now());
	            dao.updateProjectTrainingUtilization(existing);
	            submittedIds.add(itemId);
	            count++;
	        } else {
	            ProjectTrainingUtilization entity = new ProjectTrainingUtilization();
	            entity.setNameOfTraining(item.getTrainingName().trim());
	            entity.setCost(cost);
	            entity.setResourceUtilizationId(resource.getResourceUtilizationId());
	            entity.setRevisionNo(0L);
	            entity.setCreatedBy(dto.getCreatedBy());
	            entity.setCreatedDate(LocalDateTime.now());
	            entity.setIsActive(1);
	            count += dao.saveProjectTrainingUtilization(entity);
	        }
	    }

	    // Delete missing rows
	    for (ProjectTrainingUtilization row : active) {
	        if (!submittedIds.contains(row.getTrainingUtilizationId())) {
	            dao.deleteProjectTrainingUtilization(row);
	        }
	    }

	    return count;
	}

	@Override
	public long reviseTrainingUtilization(ProjectTrainingUtilizationDto dto) throws Exception {
	    long projectId = Long.parseLong(dto.getProjectId());
	    
	    List<TrainingItemDto> items = dto.getItems();
	    if (items == null || items.isEmpty()) return 0;

	    ProjectResourceUtilization resource = dao.findResourceUtilizationId(projectId, dto.getFinYear(), dto.getQuarter());
	    
	    if (resource == null || resource.getResourceUtilizationId() == null) {
	        throw new IllegalStateException("Nothing to revise — this quarter has never been saved.");
	    }

	    List<ProjectTrainingUtilization> active = dao.getActiveTraining(resource.getResourceUtilizationId());
	   
	    long nextRevision = active.isEmpty() ? 1L : active.get(0).getRevisionNo() + 1;

	    for (ProjectTrainingUtilization row : active) {
	        ProjectTrainingUtilizationRev rev = new ProjectTrainingUtilizationRev();
	        
	        rev.setNameOfTraining(row.getNameOfTraining());
	        rev.setCost(row.getCost());
	        rev.setResourceUtilizationId(row.getResourceUtilizationId());
	        rev.setRevisionNo(row.getRevisionNo());
	        rev.setRevisionDate(new Date());
	        rev.setCreatedBy(dto.getCreatedBy());
	        rev.setCreatedDate(LocalDateTime.now());
	        rev.setIsActive(row.getIsActive());
	        
	        dao.saveProjectTrainingUtilizationRev(rev);
	        dao.deleteProjectTrainingUtilization(row);
	    }

	    long count = 0;
	    for (TrainingItemDto item : items) {
	        if (item.getTrainingName() == null || item.getTrainingName().isBlank()) continue;

	        ProjectTrainingUtilization entity = new ProjectTrainingUtilization();
	        entity.setNameOfTraining(item.getTrainingName().trim());
	        entity.setCost(item.getCost() != null && !item.getCost().isBlank() ? new BigDecimal(item.getCost()) : BigDecimal.ZERO);
	        entity.setResourceUtilizationId(resource.getResourceUtilizationId());
	        entity.setRevisionNo(nextRevision);
	        entity.setCreatedBy(dto.getCreatedBy());
	        entity.setCreatedDate(LocalDateTime.now());
	        entity.setIsActive(1);

	        count += dao.saveProjectTrainingUtilization(entity);
	    }
	    
	    return count;
	}
	
	@Override
	public List<ProjectEconomicImpact> getEconomicImpact(Long projectId) throws Exception{
		return dao.getEconomicImpact(projectId);
	}

	@Override
	@Transactional
	public long saveEconomicImpact(ProjectEconomicImpactDto dto) throws Exception {
		try{
			ProjectEconomicImpact impact = new ProjectEconomicImpact();
			
			impact.setProjectId(Long.parseLong(dto.getProjectId()));
			
			impact.setIndigenousContentAndIndigenization(dto.getIndigenousContentAndIndigenization());
			impact.setInternationalCollaborationsExecuted(dto.getInternationalCollaborationsExecuted());
			impact.setIntellectualPropertyRights(dto.getIntellectualPropertyRights());
			impact.setExportPotential(dto.getExportPotential());
			impact.setInfrastructureCreated(dto.getInfrastructureCreated());
			
			impact.setCreatedBy(dto.getCreatedBy());
			impact.setCreatedDate(LocalDateTime.now());
			impact.setRevisionNo(0L);
			impact.setIsActive(1);
			
			return dao.saveProjectEconomicImpact(impact);
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public long editEconomicImpact(ProjectEconomicImpactDto dto) throws Exception {
		try {
			ProjectEconomicImpact impact = dao.getEconomiImpactById(Long.parseLong(dto.getEconomicImpactId()));
			
			impact.setIndigenousContentAndIndigenization(dto.getIndigenousContentAndIndigenization());
			impact.setInternationalCollaborationsExecuted(dto.getInternationalCollaborationsExecuted());
			impact.setIntellectualPropertyRights(dto.getIntellectualPropertyRights());
			impact.setExportPotential(dto.getExportPotential());
			impact.setInfrastructureCreated(dto.getInfrastructureCreated());
			
			impact.setModifiedBy(dto.getCreatedBy());
			impact.setModifiedDate(LocalDateTime.now());
			
			return dao.saveProjectEconomicImpact(impact);
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	@Transactional
	public long reviseEconomicImpact(ProjectEconomicImpactDto dto) throws Exception {
		try {
			ProjectEconomicImpact impact = dao.getEconomiImpactById(Long.parseLong(dto.getEconomicImpactId()));
			Long revisionNo = impact.getRevisionNo() + 1;
			
			ProjectEconomicImpactRev rev = new ProjectEconomicImpactRev();

			rev.setProjectId(impact.getProjectId());
			
			rev.setIndigenousContentAndIndigenization(impact.getIndigenousContentAndIndigenization());
			rev.setInternationalCollaborationsExecuted(impact.getInternationalCollaborationsExecuted());
			rev.setIntellectualPropertyRights(impact.getIntellectualPropertyRights());
			rev.setExportPotential(impact.getExportPotential());
			rev.setInfrastructureCreated(impact.getInfrastructureCreated());
			
			rev.setRevisionNo(impact.getRevisionNo());
			rev.setRevisionDate(LocalDateTime.now());
			
			rev.setCreatedBy(dto.getCreatedBy());
			rev.setCreatedDate(LocalDateTime.now());			
			
			rev.setIsActive(impact.getIsActive());
			
			dao.saveProjectEconomicImpactRev(rev);
			
			impact.setIndigenousContentAndIndigenization(dto.getIndigenousContentAndIndigenization());
			impact.setInternationalCollaborationsExecuted(dto.getInternationalCollaborationsExecuted());
			impact.setIntellectualPropertyRights(dto.getIntellectualPropertyRights());
			impact.setExportPotential(dto.getExportPotential());
			impact.setInfrastructureCreated(dto.getInfrastructureCreated());
			
			impact.setModifiedBy(dto.getCreatedBy());
			impact.setModifiedDate(LocalDateTime.now());
			
			impact.setRevisionNo(revisionNo);
			
			return dao.saveProjectEconomicImpact(impact);
			
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	@Override
	public Long findResourceUtilizationIdRaw(long projectId, String finYear, String quarter) throws Exception {
		ProjectResourceUtilization resource = dao.findResourceUtilizationId(projectId, finYear, quarter);
	    
	    if (resource == null || resource.getResourceUtilizationId() == null) {
	       return null;
	    }
	    
	    return resource.getResourceUtilizationId();

	}

	@Override
	public List<RevisionSummaryDto> getInfrastructureRevisionList(Long resourceUtilizationId) throws Exception {
		try {
			List<RevisionSummaryDto> summaryDtos = new ArrayList<RevisionSummaryDto>();
			List<Object[]> revisionCounts = dao.getInfrastructureRevisionList(resourceUtilizationId);
			for(Object[] obj: revisionCounts) {
				RevisionSummaryDto dto = new RevisionSummaryDto();
				
				dto.setRevisedBy(obj[2] != null ? obj[2].toString() : null);
				dto.setRevisionNo(obj[1]!=null ? Long.parseLong(obj[1].toString()) : null);
				dto.setRevisedDate(obj[3] != null ? obj[3].toString() : null);
				
				summaryDtos.add(dto);
			}
			return summaryDtos;
		}catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	public List<RevisionSummaryDto> getTrainingRevisionList(Long resourceUtilizationId) throws Exception {
		try {
			List<RevisionSummaryDto> summaryDtos = new ArrayList<RevisionSummaryDto>();
			List<Object[]> revisionCounts = dao.getTrainingRevisionList(resourceUtilizationId);
			for(Object[] obj: revisionCounts) {
				RevisionSummaryDto dto = new RevisionSummaryDto();
				
				dto.setRevisedBy(obj[2] != null ? obj[2].toString() : null);
				dto.setRevisionNo(obj[1]!=null ? Long.parseLong(obj[1].toString()) : null);
				dto.setRevisedDate(obj[3] != null ? obj[3].toString() : null);
				
				summaryDtos.add(dto);
			}
			return summaryDtos;
		}catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	public List<RevisionSummaryDto> getManpowerRevisionList(Long resourceUtilizationId) throws Exception {
		try {
			List<RevisionSummaryDto> summaryDtos = new ArrayList<RevisionSummaryDto>();
			List<Object[]> revisionCounts = dao.getManpowerRevisionList(resourceUtilizationId);
			for(Object[] obj: revisionCounts) {
				RevisionSummaryDto dto = new RevisionSummaryDto();
				
				dto.setRevisedBy(obj[2] != null ? obj[2].toString() : null);
				dto.setRevisionNo(obj[1]!=null ? Long.parseLong(obj[1].toString()) : null);
				dto.setRevisedDate(obj[3] != null ? obj[3].toString() : null);
				
				summaryDtos.add(dto);
			}
			return summaryDtos;
		}catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	public List<RevisionItemDto> getInfrastructureRevisionItems(Long resourceUtilizationId, String revisionNo) throws Exception {
		try {
			List<RevisionItemDto> list = new ArrayList<RevisionItemDto>();
			List<Object[]> revisionList = dao.getInfrastructureRevisionItemsList(resourceUtilizationId, Long.parseLong(revisionNo));
			
			for(Object[] obj: revisionList) {
				RevisionItemDto dto = new RevisionItemDto();

				dto.setMainId(obj[0] != null ? obj[0].toString() : null);
				dto.setResourceUtilizationId(obj[1] != null ? obj[1].toString() : null);
				dto.setItemName(obj[2] !=null ? obj[2].toString() : null);
				dto.setItemValue(obj[3] != null ? obj[3].toString() : null);
				dto.setRevisionNo(obj[4]!=null ? obj[4].toString() : null);
				
				list.add(dto);
			}
			return list;
		}catch (Exception e) {
			e.printStackTrace();
			return List.of();
		}
	}

	@Override
	public List<RevisionItemDto> getTrainingRevisionItems(Long resourceUtilizationId, String revisionNo) throws Exception {
		try {
			List<RevisionItemDto> list = new ArrayList<RevisionItemDto>();
			List<Object[]> revisionList = dao.getTrainingRevisionItemsList(resourceUtilizationId, Long.parseLong(revisionNo));
			
			for(Object[] obj: revisionList) {
				RevisionItemDto dto = new RevisionItemDto();

				dto.setMainId(obj[0] != null ? obj[0].toString() : null);
				dto.setResourceUtilizationId(obj[1] != null ? obj[1].toString() : null);
				dto.setItemName(obj[2] !=null ? obj[2].toString() : null);
				dto.setItemValue(obj[3] != null ? obj[3].toString() : null);
				dto.setRevisionNo(obj[4]!=null ? obj[4].toString() : null);
				
				list.add(dto);
			}
			return list;
		}catch (Exception e) {
			e.printStackTrace();
			return List.of();
		}
	}

	@Override
	public List<RevisionItemDto> getManpowerRevisionItems(Long resourceUtilizationId, String revisionNo) throws Exception {
	    try {
	        List<RevisionItemDto> list = new ArrayList<>();

	        Object[] row = dao.getManpowerRevisionRow(resourceUtilizationId, Long.parseLong(revisionNo));
	        if (row == null) return list;

	        long sciCount  = row[1] != null ? ((Number) row[1]).longValue() : 0;
	        long techCount = row[2] != null ? ((Number) row[2]).longValue() : 0;
	        long admCount  = row[3] != null ? ((Number) row[3]).longValue() : 0;
	        long sciDays   = row[4] != null ? ((Number) row[4]).longValue() : 0;
	        long techDays  = row[5] != null ? ((Number) row[5]).longValue() : 0;
	        long admDays   = row[6] != null ? ((Number) row[6]).longValue() : 0;

	        list.add(buildManpowerRow(resourceUtilizationId,revisionNo,"Scientist", sciCount, sciDays));
	        list.add(buildManpowerRow(resourceUtilizationId,revisionNo,"Technical", techCount, techDays));
	        list.add(buildManpowerRow(resourceUtilizationId,revisionNo,"Admin & Allied", admCount, admDays));

	        return list;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return List.of();
	    }
	}

	private RevisionItemDto buildManpowerRow( Long resUtilId, String revNo,String cadreName, long count, long days) {
	    RevisionItemDto dto = new RevisionItemDto();
	    dto.setResourceUtilizationId(resUtilId.toString());
	    dto.setRevisionNo(revNo);
	    dto.setItemName(cadreName);
	    dto.setItemValue(String.valueOf(count));
	    dto.setItemDays(String.valueOf(days));
	    dto.setItemTotal(String.valueOf(count * days));
	    return dto;
	}
	
	@Override
	public List<Object[]> getEconomicImpactRevisionList(Long projectId) throws Exception {
	    return dao.getEconomicImpactRevisionList(projectId);
	}

	@Override
	public ProjectEconomicImpactRev getEconomicImpactRevisionData(Long projectId, Long revisionNo) throws Exception {
	    return dao.getEconomicImpactRevByProjectAndRevision(projectId, revisionNo);
	}
	
	private String getCurrentFinancialYear() {

	    LocalDate today = LocalDate.now();

	    int startYear = today.getMonthValue() >= 4
	            ? today.getYear()
	            : today.getYear() - 1;

	    return startYear + "-" + String.valueOf(startYear + 1);
	}
	
	@Override
	public List<ProjectUtilizationBriefingDto> getManPowerDetailsForBriefing(String projectIdValue) {

		Long projectId= Long.parseLong(projectIdValue);
	    List<ProjectUtilizationBriefingDto> list = new ArrayList<>();

	    try {
	        ProjectUtilizationBriefingDto scientist = buildManPowerBriefingDataDto("Scientist");
	        ProjectUtilizationBriefingDto technical = buildManPowerBriefingDataDto("Technical");
	        ProjectUtilizationBriefingDto adminAndAllied = buildManPowerBriefingDataDto("Admin & Allied");
	        
	        list.add(scientist);
	        list.add(technical);
	        list.add(adminAndAllied);
	        
	    	String financialYear = getCurrentFinancialYear();
	    	
	        List<Object[]> resources = dao.getProjectResourceUtilizationByProjectId(projectId,financialYear.trim());

	        if (resources == null || resources.isEmpty()) {
	            return list;
	        }
	        

	        Object[] totalCounts = dao.getManPowerTotalCounts(projectId);
	
		    
		    Long totalSciDaysCount = totalCounts[1] != null ? ((Number) totalCounts[1]).longValue() : 0L;
		    Long totalTechnicalDaysCount = totalCounts[2] != null ? ((Number) totalCounts[2]).longValue() : 0L;
		    Long totalAdmDaysCount = totalCounts[3] != null ? ((Number) totalCounts[3]).longValue() : 0L;
		    
		    scientist.setCummulativeTillDate(totalSciDaysCount);
		    technical.setCummulativeTillDate(totalTechnicalDaysCount);
		    adminAndAllied.setCummulativeTillDate(totalAdmDaysCount);
		
	        Long currentYearSciCount = 0L;
	        Long currentYearTechCount = 0L;
	        Long currentYearAdmCount = 0L;
	        
	        for (Object[] obj : resources) {
	        	
	        	String quarter = obj[3] != null ? obj[3].toString() : null;
	        	
	        	Long totalSci = obj[10] != null ? ((Number) obj[10]).longValue() : 0L;
	        	
	    		Long totalTech = obj[11] != null ? ((Number) obj[11]).longValue() : 0L;
	    		
	    		Long totalAdm = obj[12] != null ? ((Number) obj[12]).longValue() : 0L;
	    		
	    		currentYearSciCount+= totalSci;
	    		currentYearTechCount+= totalTech;
	    		currentYearAdmCount+= totalAdm;
	    		
	        	if("Q1".equalsIgnoreCase(quarter)) {
	        		
	        		scientist.setFirstQuarter(totalSci);
	        		technical.setFirstQuarter(totalTech);
	        		adminAndAllied.setFirstQuarter(totalAdm);
	        		
	        	}else if("Q2".equalsIgnoreCase(quarter)) {

	        		scientist.setSecondQuarter(totalSci);
	        		technical.setSecondQuarter(totalTech);
	        		adminAndAllied.setSecondQuarter(totalAdm);
	        		
	        	}else if("Q3".equalsIgnoreCase(quarter)) {

	        		scientist.setThirdQuarter(totalSci);
	        		technical.setThirdQuarter(totalTech);
	        		adminAndAllied.setThirdQuarter(totalAdm);
	        		
	        	}else if("Q4".equalsIgnoreCase(quarter)) {

	        		scientist.setFourthQuarter(totalSci);
	        		technical.setFourthQuarter(totalTech);
	        		adminAndAllied.setFourthQuarter(totalAdm);
	        		
	        	}
	        }
	        
	        scientist.setCummulativePastYears(totalSciDaysCount - currentYearSciCount);
	        technical.setCummulativePastYears(totalTechnicalDaysCount - currentYearTechCount);
	        adminAndAllied.setCummulativePastYears(totalAdmDaysCount - currentYearAdmCount);
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return list;
	}
	
	private ProjectUtilizationBriefingDto buildManPowerBriefingDataDto(String desigCrade) {
		ProjectUtilizationBriefingDto dto = new ProjectUtilizationBriefingDto();
		
		dto.setDesigCrade(desigCrade);
		
		
		return dto;
	}

	@Override
	public List<ProjectUtilizationBriefingDto> getInfrastructureDetailsForBriefing(String proid) throws Exception {
		try {
			Long projectId = Long.parseLong(proid);
	        
	    	String financialYear = getCurrentFinancialYear();
			List<ProjectUtilizationBriefingDto> list = new ArrayList<>();
			List<Object[]> infrastructures = dao.getInfrastructures(projectId,financialYear);
			
			if(infrastructures == null || infrastructures.isEmpty()) return List.of();
			
			for (Object[] row : infrastructures) {
			    ProjectUtilizationBriefingDto dto = new ProjectUtilizationBriefingDto();

			    dto.setNameOfInfrastructure(row[0] != null ? row[0].toString() : null);

			    dto.setCummulativePastYears(row[1] != null ? ((Number) row[1]).longValue() : 0L);
			    dto.setCummulativeTillDate(row[2] != null ? ((Number) row[2]).longValue() : 0L);
			    
			    dto.setFirstQuarter(row[3] != null ? ((Number) row[3]).longValue() : 0L);
			    dto.setSecondQuarter(row[4] != null ? ((Number) row[4]).longValue() : 0L);
			    dto.setThirdQuarter(row[5] != null ? ((Number) row[5]).longValue() : 0L);
			    dto.setFourthQuarter(row[6] != null ? ((Number) row[6]).longValue() : 0L);

			    dto.setProjectId(projectId);
			    dto.setFinancialYear(financialYear);

			    list.add(dto);
			}
			
			
			return list;
		}catch (Exception e) {
			e.printStackTrace();
			return List.of();
		}
	}

	@Override
	public List<ProjectUtilizationBriefingDto> getTrainingDetailsForBriefing(String proid) throws Exception {
		
		try {
			Long projectId = Long.parseLong(proid);
	        
	    	String financialYear = getCurrentFinancialYear();
			List<ProjectUtilizationBriefingDto> list = new ArrayList<>();
			
			List<Object[]> trainings = dao.getTrainings(projectId,financialYear);
			
			if(trainings == null || trainings.isEmpty()) return List.of();
			
			for (Object[] row : trainings) {
			    ProjectUtilizationBriefingDto dto = new ProjectUtilizationBriefingDto();

			    dto.setNameOfTraining(row[0] != null ? row[0].toString() : null);

			    dto.setCummulativePastYears(row[1] != null ? ((Number) row[1]).longValue() : 0L);
			    dto.setCummulativeTillDate(row[2] != null ? ((Number) row[2]).longValue() : 0L);
			    
			    dto.setFirstQuarter(row[3] != null ? ((Number) row[3]).longValue() : 0L);
			    dto.setSecondQuarter(row[4] != null ? ((Number) row[4]).longValue() : 0L);
			    dto.setThirdQuarter(row[5] != null ? ((Number) row[5]).longValue() : 0L);
			    dto.setFourthQuarter(row[6] != null ? ((Number) row[6]).longValue() : 0L);

			    dto.setProjectId(projectId);
			    dto.setFinancialYear(financialYear);

			    list.add(dto);
			}
			return list;
		}catch (Exception e) {
			e.printStackTrace();
			return List.of();
		}
	}

	@Override
	public List<ProjectInfrastructureUtilization> getActiveInfrastructure(Long resourceUtilizationId) throws Exception {
		return dao.getActiveInfrastructure(resourceUtilizationId);
	}

	@Override
	public List<ProjectTrainingUtilization> getActiveTraining(Long resourceUtilizationId) throws Exception {
		return dao.getActiveTraining(resourceUtilizationId);
	}

	@Override
	public List<ProjectEconomicImpact> getEconomicImpactForBriefing(String proid) {
		try {
			return dao.getEconomicImpact(Long.parseLong(proid));
		}catch (Exception e) {
			e.printStackTrace();
			return List.of();
		}
	}
	
}