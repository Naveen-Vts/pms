package com.vts.pfms.committee.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.vts.pfms.FormatConverter;
import com.vts.pfms.admin.dao.AdminDao;
import com.vts.pfms.cars.dao.CARSDao;
import com.vts.pfms.committee.dao.CommitteeDao;
import com.vts.pfms.committee.dto.CommitteeConstitutionApprovalDto;
import com.vts.pfms.committee.dto.CommitteeDto;
import com.vts.pfms.committee.dto.CommitteeInvitationDto;
import com.vts.pfms.committee.dto.CommitteeMainDto;
import com.vts.pfms.committee.dto.CommitteeMembersDto;
import com.vts.pfms.committee.dto.CommitteeMembersEditDto;
import com.vts.pfms.committee.dto.CommitteeMinutesAttachmentDto;
import com.vts.pfms.committee.dto.CommitteeMinutesDetailsDto;
import com.vts.pfms.committee.dto.CommitteeScheduleAgendaDto;
import com.vts.pfms.committee.dto.CommitteeScheduleDto;
import com.vts.pfms.committee.dto.CommitteeSubScheduleDto;
import com.vts.pfms.committee.dto.EmpAccessCheckDto;
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
import com.vts.pfms.committee.model.PfmsEmpRoles;
import com.vts.pfms.committee.model.PfmsNotification;
import com.vts.pfms.committee.model.PmsEnote;
import com.vts.pfms.committee.model.PmsEnoteTransaction;
import com.vts.pfms.committee.model.ProgrammeMaster;
import com.vts.pfms.committee.model.ProgrammeProjects;
import com.vts.pfms.mail.CustomJavaMailSender;
import com.vts.pfms.master.dao.MasterDao;
import com.vts.pfms.master.dto.ProjectFinancialDetails;
import com.vts.pfms.milestone.model.FileRepUploadPreProject;
import com.vts.pfms.model.LabMaster;
import com.vts.pfms.print.model.CommitteeProjectBriefingFrozen;
import com.vts.pfms.print.model.MinutesFinanceList;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class CommitteeServiceImpl implements CommitteeService{

	@Autowired CommitteeDao dao;
	@Autowired AdminDao admindao;
	@Autowired CARSDao carsdao;
	@Autowired MasterDao masterDao;
	
//	@Autowired 	private JavaMailSender javaMailSender; 
	
	private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@Autowired
	@Lazy
	CustomJavaMailSender cm;
	
	FormatConverter fc=new FormatConverter();
	private SimpleDateFormat sdf1= fc.getSqlDateAndTimeFormat();  //new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private  SimpleDateFormat sdf= fc.getRegularDateFormat();     //new SimpleDateFormat("dd-MM-yyyy");
	private  SimpleDateFormat sdf2=	fc.getDateMonthShortName();   //new SimpleDateFormat("dd-MMM-yyyy");
	private SimpleDateFormat sdf3=	fc.getSqlDateFormat();			//	new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat sdf5 = new SimpleDateFormat("ddMMyyyy");
	private static final Logger logger=LogManager.getLogger(CommitteeServiceImpl.class);
	
	@Value("${ApplicationFilesDrive}")
	String uploadpath;
	
	@Override
	public long CommitteeAdd(CommitteeDto committeeDto) throws Exception
	{
		logger.info(new Date() +"Inside CommitteeAdd ");	
		long count=0;
		String name=committeeDto.getCommitteeName(); 
		String sname=committeeDto.getCommitteeShortName();
		List<Object[]> CommitteeNamesCheck=dao.CommitteeNamesCheck(name,sname,committeeDto.getIsGlobal(),committeeDto.getLabCode());
		
		if(CommitteeNamesCheck.get(0)[1]!=null || CommitteeNamesCheck.get(0)[0]!=null)
		{
			if(Long.parseLong(CommitteeNamesCheck.get(0)[1].toString())>0)
			{
				return -2;
			} 
			if(Long.parseLong(CommitteeNamesCheck.get(0)[0].toString())>0)
			{
				return -1;
			}			
		}
		
		Committee committeeModel=new Committee();
		
		committeeModel.setLabCode(committeeDto.getLabCode());
		committeeModel.setCommitteeName(name);
		committeeModel.setCommitteeShortName(sname);
		committeeModel.setCommitteeType(committeeDto.getCommitteeType());
		committeeModel.setProjectApplicable(committeeDto.getProjectApplicable());
		
		committeeModel.setTechNonTech(committeeDto.getTechNonTech());
		committeeModel.setPeriodicNon(committeeDto.getPeriodicNon());
		committeeModel.setPeriodicDuration(Integer.parseInt(committeeDto.getPeriodicDuration()));
		committeeModel.setGuidelines(committeeDto.getGuidelines());
		committeeModel.setDescription(committeeDto.getDescription());
		committeeModel.setTermsOfReference(committeeDto.getTermsOfReference());
		committeeModel.setIsGlobal(Long.parseLong(committeeDto.getIsGlobal()));
		committeeModel.setCreatedBy(committeeDto.getCreatedBy());
		committeeModel.setCreatedDate(sdf1.format(new Date()));
		committeeModel.setIsActive(1);
		committeeModel.setIsBriefing(committeeDto.getIsBriefing());
		count = dao.CommitteeNewAdd(committeeModel);
		
		return count;
		
	}
	
	@Override
	public Object[]CommitteeNamesCheck(String name,String sname, String projectid,String LabCode) throws Exception
	{
		logger.info(new Date() +"Inside CommitteeNamesCheck ");	
		return dao.CommitteeNamesCheck(name,sname,projectid,LabCode).get(0);
	}
		
		
	
	@Override
	public List<Object[]> CommitteeListActive(String isglobal,String Projectapplicable, String LabCode) throws Exception
	{
		logger.info(new Date() +"Inside CommitteeListActive ");	
		return dao.CommitteeListActive(isglobal,Projectapplicable,LabCode); 
	}
	
	@Override
	public Object[] CommitteeDetails(String committeeid) throws Exception
	{
		logger.info(new Date() +"Inside CommitteeDetails ");	
		return dao.CommitteeDetails(committeeid);
	}
	
	@Override
	public long CommitteeEditSubmit(CommitteeDto committeeDto) throws Exception
	{
		logger.info(new Date() +"Inside CommitteeEditSubmit ");	
		String name=committeeDto.getCommitteeName(); 
		String sname=committeeDto.getCommitteeShortName();
		
		
		long committeeid=committeeDto.getCommitteeId();
		String shortname=committeeDto.getCommitteeShortName();
		String fullname=committeeDto.getCommitteeName();
		List<Object[]> CommitteeList=(List<Object[]>)dao.CommitteeListActive(committeeDto.getIsGlobal(),committeeDto.getProjectApplicable(),committeeDto.getLabCode());
		for(int i=0;i<CommitteeList.size();i++)
		{
			if(Long.parseLong(CommitteeList.get(i)[0].toString())!=committeeid)
			{
				if(CommitteeList.get(i)[1].toString().equals(shortname))
				{
					return -1;
				}
				if(CommitteeList.get(i)[2].toString().equals(fullname))
				{
					return -2;
				}
			}
		}
		
		Committee committeemodel=new Committee();
		
		committeemodel.setCommitteeId(committeeid);
		committeemodel.setCommitteeName(name);
		committeemodel.setCommitteeShortName(sname);
		committeemodel.setCommitteeType(committeeDto.getCommitteeType());
		committeemodel.setProjectApplicable(committeeDto.getProjectApplicable());
		
		committeemodel.setTechNonTech(committeeDto.getTechNonTech());
		committeemodel.setPeriodicNon(committeeDto.getPeriodicNon());
		committeemodel.setPeriodicDuration(Integer.parseInt(committeeDto.getPeriodicDuration()));
		committeemodel.setGuidelines(committeeDto.getGuidelines());
		committeemodel.setDescription(committeeDto.getDescription());
		committeemodel.setTermsOfReference(committeeDto.getTermsOfReference());
		committeemodel.setIsGlobal(Long.parseLong(committeeDto.getIsGlobal()));
		committeemodel.setModifiedBy(committeeDto.getModifiedBy());
		committeemodel.setModifiedDate(sdf1.format(new Date()));
		committeemodel.setIsBriefing(committeeDto.getIsBriefing());
		return dao.CommitteeEditSubmit(committeemodel);	
	}	
	
	
	@Override
	public List<Object[]> EmployeeList(String LabCode) throws Exception {
		return dao.EmployeeList(LabCode);
	}
	
	@Override
	public Object[] CommitteeName(String CommitteeId) throws Exception {
		return dao.CommitteeName(CommitteeId);
	}


	@Override
	public long CommitteeDetailsSubmit(CommitteeMainDto committeemaindto) throws Exception 
	{
		logger.info(new Date() +"Inside CommitteeDetailsSubmit ");
		CommitteeMain committeemain= new CommitteeMain();
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate fromDate = LocalDate.parse(committeemaindto.getValidFrom(), formatter);
		
		LocalDate formationDate = LocalDate.parse(committeemaindto.getFormationDate(),formatter);
		LocalDate toDate=formationDate.plusYears(5).minusDays(1);
		
		committeemain.setValidFrom(java.sql.Date.valueOf(formationDate));
		committeemain.setValidTo(java.sql.Date.valueOf(toDate));
		committeemain.setCreatedBy(committeemaindto.getCreatedBy());
		committeemain.setCreatedDate(sdf1.format(new Date()));
		committeemain.setCommitteeId(Long.parseLong(committeemaindto.getCommitteeId()));
		committeemain.setProjectId(Long.parseLong(committeemaindto.getProjectId()));
		committeemain.setInitiationId(Long.parseLong(committeemaindto.getInitiationId()));
		committeemain.setDivisionId(Long.parseLong(committeemaindto.getDivisionId()));
		committeemain.setCARSInitiationId(Long.parseLong(committeemaindto.getCARSInitiationId()));
		committeemain.setProgrammeId(Long.parseLong(committeemaindto.getProgrammeId()));
		committeemain.setPreApproved(committeemaindto.getPreApproved());
		committeemain.setReferenceNo(committeemaindto.getReferenceNo());
		committeemain.setFormationDate(java.sql.Date.valueOf(formationDate));;
		if(committeemaindto.getPreApproved().equalsIgnoreCase("N")) {
			committeemain.setStatus("P");
			committeemain.setIsActive(0);
		}else if(committeemaindto.getPreApproved().equalsIgnoreCase("Y"))
		{
			committeemain.setStatus("A");
			committeemain.setIsActive(1);
		}
		if( committeemaindto.getPreApproved().equalsIgnoreCase("Y")) {
			long lastcommitteeid=dao.LastCommitteeId(committeemaindto.getCommitteeId(),committeemaindto.getProjectId(),committeemaindto.getDivisionId(),committeemaindto.getInitiationId(), committeemaindto.getCARSInitiationId(), committeemaindto.getProgrammeId());		
			if(lastcommitteeid!=0 )
			{
				CommitteeMain committeemain1= new CommitteeMain();
				
				committeemain1.setModifiedBy(committeemaindto.getCreatedBy());
				committeemain1.setModifiedDate(sdf1.format(new Date()));
				committeemain1.setValidTo(java.sql.Date.valueOf(fromDate.minusDays(1).toString()));
				committeemain1.setCommitteeMainId(lastcommitteeid);
					
				dao.UpdateCommitteemainValidto(committeemain1);
			}		
		}
		long mainid= dao.CommitteeDetailsSubmit(committeemain);		
		
		for(int i=1;i<=4;i++) 
		{
			CommitteeMember committeemember = new CommitteeMember(); 
			committeemember.setCommitteeMainId(mainid);
			if(i==1) 
			{
				committeemember.setEmpId(Long.parseLong(committeemaindto.getChairperson()));
				committeemember.setLabCode(committeemaindto.getCpLabCode());
				committeemember.setMemberType("CC");
			}
			else if(i==2)
				{
				if(committeemaindto.getCo_Chairperson().length()!=0 &&   Long.parseLong(committeemaindto.getCo_Chairperson())>0) 
				{
					committeemember.setEmpId(Long.parseLong(committeemaindto.getCo_Chairperson()));
					committeemember.setLabCode(committeemaindto.getCcplabocode());
					committeemember.setMemberType("CH");
				}
				else 
				{
					continue;
				}
			}
			else if(i==3)
			{
				committeemember.setEmpId(Long.parseLong(committeemaindto.getSecretary()));
				committeemember.setLabCode(committeemaindto.getMsLabCode());
				committeemember.setMemberType("CS");
			}
			else if(i==4)
			{
				if( Long.parseLong(committeemaindto.getProxySecretary())>0) 
				{
					committeemember.setEmpId(Long.parseLong(committeemaindto.getProxySecretary()) );
					committeemember.setLabCode(committeemaindto.getLabCode());
					committeemember.setMemberType("PS");
				}
				else 
				{
					continue;
				}
			}
			committeemember.setCreatedBy(committeemaindto.getCreatedBy());			
			committeemember.setCreatedDate(sdf1.format(new Date()));
			committeemember.setIsActive(1);
			dao.CommitteeMainMembersAdd(committeemember);
		}		
		
		String[] reps=committeemaindto.getReps();
		if(mainid>0 && reps!=null) {
					
			for(int i=0;i<reps.length;i++ ) 
			{
				CommitteeMemberRep repmems=new CommitteeMemberRep();
				repmems.setCommitteeMainId(mainid);
				repmems.setRepId(Integer.parseInt(reps[i]));
				repmems.setCreatedBy(committeemaindto.getCreatedBy());
				repmems.setCreatedDate(sdf1.format(new Date()));
				repmems.setIsActive(1);			
				dao.CommitteeRepMembersSubmit(repmems);
			}
		}
		
		if(committeemaindto.getPreApproved().equalsIgnoreCase("N"))
		{
			CommitteeConstitutionApproval approval=new CommitteeConstitutionApproval();
			approval.setCommitteeMainId(mainid);
			approval.setConstitutionStatus("CCR");
	//		approval.setRemarks(dto.getRemarks());
			approval.setActionBy(committeemaindto.getCreatedBy());
			approval.setActionDate(sdf1.format(new Date()));
			approval.setEmpid(Long.parseLong(committeemaindto.getCreatedByEmpid()));
			approval.setEmpLabid(Long.parseLong(committeemaindto.getCreatedByEmpidLabid()));
			approval.setConstitutedBy(Long.parseLong(committeemaindto.getCreatedByEmpid()));
			approval.setApprovalAuthority("0");
			dao.CommitteeConstitutionApprovalAdd(approval);
		}
		
		//Pms pe
		if(committeemaindto.getPreApproved().equalsIgnoreCase("N"))
		{
			PmsEnote pmsenote = new PmsEnote();
			pmsenote.setRefNo(committeemaindto.getReferenceNo());
			pmsenote.setRefDate(java.sql.Date.valueOf(formationDate));			
			pmsenote.setCreatedBy(committeemaindto.getCreatedBy());
			pmsenote.setCreatedDate(sdf1.format(new Date()));
			
			pmsenote.setCommitteeMainId(mainid);
			pmsenote.setScheduleId(0l);
			pmsenote.setEnoteFrom("C");
			pmsenote.setIsActive(1);
			pmsenote.setInitiatedBy(Long.parseLong(committeemaindto.getCreatedByEmpid()));
			pmsenote.setEnoteStatusCode("INI");
			pmsenote.setEnoteStatusCodeNext("INI");
			
			dao.addPmsEnote(pmsenote);
		}
		return mainid;
	}

	@Override
	public Long LastCommitteeId(String CommitteeId,String projectid,String divisionid,String initiationid, String carsInitiationId, String programmeId) throws Exception {
		return dao.LastCommitteeId(CommitteeId, projectid, divisionid,initiationid, carsInitiationId, programmeId);
	}
	
	@Override
	public List<Object[]> CommitteeMainList(String labcode) throws Exception {
		return dao.CommitteeMainList(labcode);
	}
	
	@Override
	public List<Object[]> CommitteeNonProjectList() throws Exception 
	{
		return dao.CommitteeNonProjectList();
	}


	
	@Override
	public List<Object[]> EmployeeListWithoutMembers(String committeemainid, String LabCode) throws Exception
	{
		logger.info(new Date() +"Inside SERVICE EmployeeListWithoutMembers ");
		List<Object[]> employeelist= dao.EmployeeListWithoutMembers(committeemainid,LabCode);
		return employeelist;
	}
	
	@Override
	public int CommitteeMemberDelete(String committeememberid, String modifiedby)throws Exception
	{
		logger.info(new Date() +"Inside SERVICE CommitteeMemberDelete ");
		CommitteeMember committeemember=new CommitteeMember();
		committeemember.setCommitteeMemberId(Long.parseLong(committeememberid));
		committeemember.setModifiedBy(modifiedby);
		committeemember.setModifiedDate(sdf1.format(new Date()));
		
		return dao.CommitteeMemberDelete(committeemember);
	}
	
	@Override
	public long CommitteeMainMembersAddSubmit(String committeemainid, String[] Member,String userid) throws Exception
	{
		logger.info(new Date() +"Inside SERVICE CommitteeMainMembersAddSubmit ");
		long count=0;
		for(int i=0;i< Member.length;i++)
		{
			CommitteeMember  committeemember=new CommitteeMember();
			committeemember.setCommitteeMainId(Long.parseLong(committeemainid));
			committeemember.setCreatedBy(userid);
			committeemember.setCreatedDate(sdf1.format(new Date()));
			committeemember.setEmpId(Long.parseLong(Member[i]));
			committeemember.setIsActive(1);
			count=dao.CommitteeMainMembersAdd(committeemember);
		}
		return count;
	}
	
	@Override
	public List<Object[]> EmployeeListNoMembers(String labid, String committeemainid) throws Exception
	{
		logger.info(new Date() +"Inside SERVICE EmployeeListNoMembers ");
		List<Object[]> employeelist= dao.ChairpersonEmployeeList( labid, committeemainid);	
		return employeelist;
	}

	@Override
	public long CommitteeScheduleAddSubmit(CommitteeScheduleDto committeescheduledto)throws Exception
	{
		try {

			logger.info(new Date() +"Inside SERVICE CommitteeScheduleAddSubmit ");
			CommitteeSchedule committeeschedule = new  CommitteeSchedule(); 
			
			committeeschedule.setLabCode(committeescheduledto.getLabCode());	
			committeeschedule.setCommitteeId(committeescheduledto.getCommitteeId());
			committeeschedule.setProjectId(committeescheduledto.getProjectId()!=null?Long.parseLong(committeescheduledto.getProjectId()):0L);
			committeeschedule.setCreatedBy(committeescheduledto.getCreatedBy());
			committeeschedule.setScheduleStartTime(committeescheduledto.getScheduleStartTime());
			committeeschedule.setScheduleDate(new java.sql.Date(sdf.parse(committeescheduledto.getScheduleDate()).getTime()));
			committeeschedule.setCreatedDate(sdf1.format(new Date()));
			committeeschedule.setScheduleSub("N");		
			committeeschedule.setIsActive(1);
			committeeschedule.setScheduleFlag(committeescheduledto.getScheduleFlag());
			committeeschedule.setConfidential(committeescheduledto.getConfidential());
			committeeschedule.setDivisionId(committeescheduledto.getDivisionId()!=null?Long.parseLong(committeescheduledto.getDivisionId()):0L);
			committeeschedule.setInitiationId(committeescheduledto.getInitiationId()!=null?Long.parseLong(committeescheduledto.getInitiationId()):0L);
			committeeschedule.setPresentationFrozen("N");
			committeeschedule.setBriefingPaperFrozen("N");
			committeeschedule.setMinutesFrozen("N");
			committeeschedule.setBriefingStatus("INI");
			
			// Added by Prudhvi on 21-10-2024
			committeeschedule.setCARSInitiationId(committeescheduledto.getCARSInitiationId()!=null?Long.parseLong(committeescheduledto.getCARSInitiationId()):0L);
			committeeschedule.setCommitteeMainId(committeescheduledto.getCommitteeMainId());
			
			committeeschedule.setProgrammeId(committeescheduledto.getProgrammeId()!=null?Long.parseLong(committeescheduledto.getProgrammeId()):0L);
			if(committeeschedule.getProgrammeId()!=0) {
				committeeschedule.setScheduleType("P");
			}
			
			String CommitteeName=dao.CommitteeName(committeescheduledto.getCommitteeId().toString())[2].toString();
			String LabName=dao.LabDetails(committeeschedule.getLabCode())[1].toString();
			Long SerialNo=dao.MeetingCount(new java.sql.Date(sdf.parse(committeescheduledto.getScheduleDate()).getTime()),committeescheduledto.getProjectId());
			String ProjectName=null;
			if(committeeschedule.getProjectId()>0) 
			{
				ProjectName=dao.projectdetails(committeescheduledto.getProjectId())[4].toString();
			}
			else if(committeeschedule.getDivisionId()>0) 
			{
				ProjectName="DIV";
			}
			else if(committeeschedule.getInitiationId()>0) 
			{
				ProjectName="INI";
			}
			else if(committeeschedule.getCARSInitiationId()>0)
			{
				ProjectName="CARS-"+committeeschedule.getCARSInitiationId();
				SerialNo=dao.carsMeetingCount(committeeschedule.getCARSInitiationId()+"");
			}else if(committeeschedule.getProgrammeId()>0) {
				ProjectName = "PRGM";
				SerialNo=dao.prgmMeetingCount(committeeschedule.getProgrammeId()+"");
			}
			else
			{
				ProjectName="GEN";
			}
			Date ScheduledDate= (new java.sql.Date(sdf.parse(committeescheduledto.getScheduleDate()).getTime()));
			if(ProjectName.equalsIgnoreCase("PRGM")) {
				committeeschedule.setMeetingId(LabName.trim()+"/7826/"+CommitteeName.trim()+"/Mtg/"+sdf5.format(ScheduledDate).toString());
			}
			else committeeschedule.setMeetingId(LabName.trim()+"/"+ProjectName.trim()+"/"+CommitteeName.trim()+"/"+sdf2.format(ScheduledDate).toString().toUpperCase().replace("-", "")+"/"+(SerialNo+1));			
			
			return dao.CommitteeScheduleAddSubmit(committeeschedule);
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		
	}
	
	@Override
	public List<Object[]> CommitteeScheduleListNonProject(String committeeid)throws Exception
	{
		return dao.CommitteeScheduleListNonProject(committeeid);
	}

	@Override
	public Object[] CommitteeScheduleEditData(String CommitteeScheduleId) throws Exception {

		return dao.CommitteeScheduleEditData(CommitteeScheduleId);
	}
	
	@Override
	public List<Object[]> AgendaReturnData(String CommitteeScheduleId) throws Exception {

		return dao.AgendaReturnData(CommitteeScheduleId);
	}

	@Override
	public List<Object[]> ProjectList(String LabCode) throws Exception {
		
		return dao.ProjectList(LabCode);
	}
	
	@Override
	public Long CommitteeAgendaSubmit(List<CommitteeScheduleAgendaDto> scheduleagendadtos) throws Exception {

		logger.info(new Date() +"Inside SERVICE CommitteeAgendaSubmit ");
		long ret=0;
		
		String committeescheduleid=scheduleagendadtos.get(0).getScheduleId();
		
		Iterator<CommitteeScheduleAgendaDto> iterator = scheduleagendadtos.iterator();
		
		while(iterator.hasNext()) 
		{
			CommitteeScheduleAgendaDto AgendaDto = iterator.next();
			CommitteeScheduleAgenda scheduleagenda=new CommitteeScheduleAgenda();
//			CommitteeSchedulesAttachment attachment = new CommitteeSchedulesAttachment();
			scheduleagenda.setPresentorLabCode(AgendaDto.getPresentorLabCode());
			scheduleagenda.setScheduleId(Long.parseLong(AgendaDto.getScheduleId()));
			scheduleagenda.setScheduleSubId(Long.parseLong(AgendaDto.getScheduleSubId()));
			scheduleagenda.setAgendaItem(AgendaDto.getAgendaItem());
			scheduleagenda.setPresenterId(Long.parseLong(AgendaDto.getPresenterId()));
			scheduleagenda.setDuration(Integer.parseInt(AgendaDto.getDuration()));
			scheduleagenda.setProjectId(Long.parseLong(AgendaDto.getProjectId()));
			scheduleagenda.setRemarks(AgendaDto.getRemarks());
			scheduleagenda.setCreatedBy(AgendaDto.getCreatedBy());
			scheduleagenda.setCreatedDate(sdf1.format(new Date()));
			scheduleagenda.setIsActive(Integer.parseInt(AgendaDto.getIsActive()));
			scheduleagenda.setGroupName(AgendaDto.getGroupName());
//			if(AgendaDto.getDocId()!=null) {
//				scheduleagenda.setDocId(Long.parseLong(AgendaDto.getDocId()));
//			}else
//			{
//				scheduleagenda.setDocId(0);
//			}			

			List<Object[]> agendapriority=dao.CommitteeScheduleAgendaPriority(committeescheduleid);
			
			
			if(agendapriority.size()==0)
			{
				scheduleagenda.setAgendaPriority(1);
			}
			else if((Integer)agendapriority.get(0)[2]==0)
			{
				scheduleagenda.setAgendaPriority(1);
			}else {
				scheduleagenda.setAgendaPriority(((Integer)agendapriority.get(0)[2])+1);	
			}
			
			ret= dao.CommitteeAgendaSubmit(scheduleagenda);
			
			if(AgendaDto.getDocLinkIds()!=null) 
			{
				List<String> docids = Arrays.asList(AgendaDto.getDocLinkIds());
				List<String> existingdocids = new ArrayList<String>();
				for(int j=0; j<docids.size();j++) {
					System.out.println(!existingdocids.contains(docids.get(j)));
					if(!existingdocids.contains(docids.get(j))) 
					{
						CommitteeScheduleAgendaDocs doc= new CommitteeScheduleAgendaDocs();
						doc.setAgendaId(ret);
						doc.setFileDocId(Long.parseLong(docids.get(j)));
						doc.setIsActive(1);
						doc.setCreatedBy(AgendaDto.getCreatedBy());
						doc.setCreatedDate(sdf1.format(new Date()));
						dao.AgendaDocLinkAdd(doc);
						existingdocids.add(String.valueOf(docids.get(j)));
					}
				}
			}
		
		}
		return ret;
	}


	@Override
	public Long CommitteeScheduleAgendaEdit(CommitteeScheduleAgendaDto scheduleagendadto,String attachmentid) throws Exception
	{
		logger.info(new Date() +"Inside SERVICE CommitteeScheduleAgendaEdit ");
		CommitteeScheduleAgenda scheduleagenda=new CommitteeScheduleAgenda();
//		CommitteeSchedulesAttachment attachment = new CommitteeSchedulesAttachment();
		
		scheduleagenda.setScheduleAgendaId(Long.parseLong(scheduleagendadto.getScheduleAgendaId()));
		scheduleagenda.setAgendaItem(scheduleagendadto.getAgendaItem());
		scheduleagenda.setProjectId(Long.parseLong(scheduleagendadto.getProjectId()));
		scheduleagenda.setRemarks(scheduleagendadto.getRemarks());
		scheduleagenda.setModifiedBy(scheduleagendadto.getModifiedBy());
		scheduleagenda.setModifiedDate(sdf1.format(new Date()));
		scheduleagenda.setPresentorLabCode(scheduleagendadto.getPresentorLabCode());
		scheduleagenda.setPresenterId(Long.parseLong(scheduleagendadto.getPresenterId()));
		scheduleagenda.setDuration(Integer.parseInt(scheduleagendadto.getDuration()));
		scheduleagenda.setGroupName(scheduleagendadto.getGroupName());
		
//		if(scheduleagendadto.getDocId()!=null) {
//			scheduleagenda.setDocId(Long.parseLong(scheduleagendadto.getDocId()));
//		}else
//		{
//			scheduleagenda.setDocId(0);
//		}		
		
		if(scheduleagendadto.getDocLinkIds()!=null) {
			List<String> docids = Arrays.asList(scheduleagendadto.getDocLinkIds());
			List<String> existingdocids = dao.AgendaAddedDocLinkIdList(scheduleagendadto.getScheduleAgendaId()) ;
			for(int j=0; j<docids.size();j++) {
				if(!existingdocids.contains(docids.get(j)) && !docids.get(j).equalsIgnoreCase("")) 
				{
					CommitteeScheduleAgendaDocs doc= new CommitteeScheduleAgendaDocs();
					doc.setAgendaId(Long.parseLong(scheduleagendadto.getScheduleAgendaId()));
					doc.setFileDocId(Long.parseLong(docids.get(j)));
					doc.setIsActive(1);
					doc.setCreatedBy(scheduleagendadto.getCreatedBy());
					doc.setCreatedDate(sdf1.format(new Date()));
					dao.AgendaDocLinkAdd(doc);
					existingdocids.add(docids.get(j));
				}
			}
		}
		
		
		long ret=0;
		ret=dao.CommitteeScheduleAgendaUpdate(scheduleagenda);
		
//		if(scheduleagendadto.getAgendaAttachment().length!=0 && attachmentid.equals("null"))
//		{
//			attachment.setScheduleAgendaId(Long.parseLong(scheduleagendadto.getScheduleAgendaId()));
//			attachment.setAgendaAttachment(scheduleagendadto.getAgendaAttachment());
//			attachment.setModifiedBy(scheduleagendadto.getCreatedBy());
//			attachment.setModifiedDate(sdf1.format(new Date()));
//			attachment.setAttachmentName(scheduleagendadto.getAttachmentName());
//			
//			ret=dao.CommitteeAgendaAttachment(attachment);
//		}
//		else if(scheduleagendadto.getAgendaAttachment().length!=0 && !attachmentid.equals("null")) 
//		{		
//			attachment.setAgendaAttachment(scheduleagendadto.getAgendaAttachment());
//			attachment.setAttachmentName(scheduleagendadto.getAttachmentName());
//			attachment.setModifiedBy(scheduleagendadto.getModifiedBy());
//			attachment.setModifiedDate(sdf1.format(new Date()));
//			attachment.setScheduleAttachmentId(Long.parseLong(attachmentid));
//			ret=0;
//			ret=dao.CommitteeScheduleAgendaAttachUpdate(attachment);
//		}
		
		return ret;
	}


	@Override
	public Long CommitteeAgendaPriorityUpdate(String[] agendaid,String[] priority) throws Exception
	{
		logger.info(new Date() +"Inside SERVICE CommitteeAgendaPriorityUpdate ");
			long ret=0;
			for(int i=0;i<agendaid.length;i++)
			{
				dao.CommitteeAgendaPriorityUpdate(agendaid[i],priority[i]);
			}
			return ret;
	
	}

	@Override
	public int CommitteeAgendaDelete(String committeescheduleagendaid, String attachmentid,String Modifiedby, String  scheduleid,String AgendaPriority) throws Exception
	{
		logger.info(new Date() +"Inside SERVICE CommitteeAgendaDelete ");
		List<Object[]> agendaslistafter=dao.CommitteeScheduleGetAgendasAfter(scheduleid,AgendaPriority);
		
		if(agendaslistafter.size()!=0) 
		{
			for(Object[] obj : agendaslistafter )
			{
				
				dao.CommitteeAgendaPriorityUpdate(obj[0].toString(),String.valueOf((Integer)obj[1]-1));
				
			}
		}
		
		
		dao.AgendaDocUnlink(committeescheduleagendaid, Modifiedby, sdf1.format(new Date()));
		
//		if(!attachmentid.equals("null")) 
//		{
//			dao.CommitteeAgendaAttachmentDelete(attachmentid);
//		}
		
		return dao.CommitteeAgendaDelete(committeescheduleagendaid,Modifiedby,sdf1.format(new Date()));
		
	}

//	@Override
//	public List<Object[]> CommitteeAgendaList1(String CommitteeScheduleId) throws Exception 
//	{
//		logger.info(new Date() +"Inside CommitteeAgendaList");
//		return dao.CommitteeAgendaList(CommitteeScheduleId);
//	}
	
	@Override
	public List<Object[]> AgendaList(String CommitteeScheduleId) throws Exception 
	{		
		return dao.AgendaList(CommitteeScheduleId);
	}

//	@Override
//	public CommitteeSchedulesAttachment CommitteeAgendaAttachDownload(String CommitteeAttachmentId) throws Exception {
//
//		logger.info(new Date() +"Inside CommitteeAgendaAttachDownload");
//		return dao.CommitteeAgendaAttachDownload(CommitteeAttachmentId);
//	}

	@Override 
	public Long CommitteeScheduleUpdate(CommitteeScheduleDto committeescheduledto) throws Exception {

		logger.info(new Date() +"Inside SERVICE CommitteeScheduleUpdate ");
		String meetingid = dao.CommitteeScheduleData(committeescheduledto.getScheduleId().toString())[12].toString();
		CommitteeSchedule committeeschedule=new  CommitteeSchedule();
		
		committeeschedule.setScheduleDate(new java.sql.Date(sdf.parse(committeescheduledto.getScheduleDate()).getTime()));
		committeeschedule.setScheduleStartTime(committeescheduledto.getScheduleStartTime());
		committeeschedule.setScheduleId(committeescheduledto.getScheduleId());
		committeeschedule.setModifiedBy(committeescheduledto.getCreatedBy());
		committeeschedule.setModifiedDate(sdf1.format(new Date()));
		
		String[] strarr=meetingid.split("/");
		meetingid=strarr[0].toString().trim() +"/"+strarr[1].toString().trim() +"/"+strarr[2].toString().trim() +"/"+sdf2.format(committeeschedule.getScheduleDate()).toString().toUpperCase().replace("-", "")+"/"+strarr[4].toString().trim() ;
		committeeschedule.setMeetingId(meetingid);
		
		CommitteeSchedule schedule = dao.getCommitteeScheduleById(committeescheduledto.getScheduleId());
		
		long count =dao.CommitteeScheduleUpdate(committeeschedule);
		
		sentScheduleChangeNotification(schedule,committeescheduledto);
		
		
		return count;
	}

	
	private void sentScheduleChangeNotification(CommitteeSchedule schedule, CommitteeScheduleDto committeescheduledto) throws Exception {
		StringBuilder message = new StringBuilder();

		
		String scheduleDate = schedule.getScheduleDate().toString();
		String newDate = committeescheduledto.getScheduleDate().split("-")[2] + "-"+ committeescheduledto.getScheduleDate().split("-")[1] + "-"+ committeescheduledto.getScheduleDate().split("-")[0];

		String oldTime = schedule.getScheduleStartTime().substring(0, 5);
		String newTime = committeescheduledto.getScheduleStartTime();

	

//// Start of the message
//		message.append("Dear Team,\n\n");
//
//		boolean changeFlag = false;
//
//		if (!scheduleDate.equalsIgnoreCase(newDate)) {
//			changeFlag = true;
//			message.append("This is to inform you that the **meeting date** has been rescheduled.\n\n")
//					.append("Project Code : ").append(projectCode).append("\n").append("Committee    : ")
//					.append(committee).append("\n").append("Previous Date: ").append(scheduleDate).append("\n")
//					.append("Updated Date : ").append(newDate).append("\n");
//		}
//
//		if (!oldTime.equals(newTime)) {
//			if (!changeFlag) {
//				message.append("Please note that the **meeting time** has been updated.\n\n").append("Project Code : ")
//						.append(projectCode).append("\n").append("Committee    : ").append(committee).append("\n")
//						.append("Meeting Date : ").append(newDate).append("\n");
//			}
//			changeFlag = true;
//
//			message.append("Previous Time: ").append(oldTime).append("\n").append("Updated Time : ").append(newTime)
//					.append("\n\n");
//		}
//
//		if (!changeFlag) {
//			message.append("This is to confirm that there have been **no changes** to the scheduled meeting.\n");
//		}
//
//		message.append("Kind regards,\nScheduling System");

		
		if (!scheduleDate.equalsIgnoreCase(newDate) || !oldTime.equals(newTime)) {
			String[] arr = schedule.getMeetingId().split("/");
			String projectCode = arr[1];
			String committee = arr[2];
			message.append("Please Note that ")
				.append(committee+ " Meeting for " + projectCode)
				.append(" on "+scheduleDate.split("-")[2] + "-"+ scheduleDate.split("-")[1] + "-"+ scheduleDate.split("-")[0]+" has been rescheduled on "+committeescheduledto.getScheduleDate()+ " at "+newTime);
	
			
		List<Object[]>list = 	dao.CommitteeAtendance(schedule.getScheduleId()+"");
		
		ArrayList<String> emails= new ArrayList<String>();
		ArrayList<String> emps= new ArrayList<String>();
	
		ArrayList<String> membertypes=new ArrayList<String>(Arrays.asList("CC","CS","PS","CI","I","P"));

		for(Object[] obj : list) 
		{	
			if(membertypes.contains(obj[3].toString())) {
				emps.add(obj[0].toString());
			}
		}
		
		for(String s:emps) {
			PfmsNotification notification=new PfmsNotification();
			notification.setEmpId(Long.parseLong(s) );
			notification.setNotificationMessage(message.toString() );
			notification.setNotificationUrl("CommitteeScheduleView.htm?scheduleid="+schedule.getScheduleId());
			notification.setNotificationby(0l);
			notification.setIsActive(1);
			notification.setCreatedBy(committeescheduledto.getCreatedBy());
			notification.setCreatedDate(sdf1.format(new Date()));
			notification.setNotificationDate(sdf1.format(new Date()));
			carsdao.addNotifications(notification);
		}
		
		
		System.out.println(message);
		}
	}

	@Override
	public List<Object[]> CommitteeMinutesSpecList(String CommitteeScheduleId) throws Exception {

		return dao.CommitteeMinutesSpecList(CommitteeScheduleId);
	}

	@Override
	public Long CommitteeMinutesInsert(CommitteeMinutesDetailsDto committeeminutesdetailsdto) throws Exception {

		logger.info(new Date() +"Inside SERVICE CommitteeMinutesInsert ");
		CommitteeScheduleMinutesDetails committeeminutesdetails = new CommitteeScheduleMinutesDetails();
		committeeminutesdetails.setScheduleId(Long.parseLong(committeeminutesdetailsdto.getScheduleId()));
		committeeminutesdetails.setScheduleSubId(Long.parseLong(committeeminutesdetailsdto.getScheduleSubId()));
		committeeminutesdetails.setMinutesId(Long.parseLong(committeeminutesdetailsdto.getMinutesId()));
		committeeminutesdetails.setMinutesSubId(Long.parseLong(committeeminutesdetailsdto.getMinutesSubId()));
		committeeminutesdetails.setMinutesSubOfSubId(Long.parseLong(committeeminutesdetailsdto.getMinutesSubOfSubId()));
		committeeminutesdetails.setDetails(committeeminutesdetailsdto.getDetails());
		committeeminutesdetails.setIDARCK(committeeminutesdetailsdto.getIDARCK());
		committeeminutesdetails.setCreatedBy(committeeminutesdetailsdto.getCreatedBy());
		committeeminutesdetails.setCreatedDate(sdf1.format(new Date()));
		committeeminutesdetails.setAgendaSubHead(committeeminutesdetailsdto.getAgendaSubHead());
		if(committeeminutesdetailsdto.getAircraftId()!=null) {
			committeeminutesdetails.setAirCraftId(Long.parseLong(committeeminutesdetailsdto.getAircraftId()));;
		}
		if(committeeminutesdetailsdto.getSubSystemId()!=null) {
			committeeminutesdetails.setSubSystemId(Long.parseLong(committeeminutesdetailsdto.getSubSystemId()));;
		}
		
		if(committeeminutesdetailsdto.getRemarks()=="" ) {
			
			committeeminutesdetails.setRemarks("Nil");
			
		}else {
			
			committeeminutesdetails.setRemarks(committeeminutesdetailsdto.getRemarks());
		}
		
		

		return dao.CommitteeMinutesInsert(committeeminutesdetails);
	}

	@Override
	public Object[] CommitteeMinutesSpecDesc(CommitteeMinutesDetailsDto committeeminutesdetailsdto) throws Exception {

		logger.info(new Date() +"Inside SERVICE CommitteeMinutesSpecDesc ");
		CommitteeScheduleMinutesDetails committeeminutesdetails = new CommitteeScheduleMinutesDetails();
		
		committeeminutesdetails.setMinutesId(Long.parseLong(committeeminutesdetailsdto.getMinutesId()));
		committeeminutesdetails.setMinutesSubId(Long.parseLong(committeeminutesdetailsdto.getMinutesSubId()));
		committeeminutesdetails.setMinutesSubOfSubId(Long.parseLong(committeeminutesdetailsdto.getMinutesSubOfSubId()));

		return dao.CommitteeMinutesSpecDesc(committeeminutesdetails);
	}
	
	@Override
	public Object[] CommitteeMinutesSpecEdit(CommitteeMinutesDetailsDto committeeminutesdetailsdto) throws Exception {

		logger.info(new Date() +"Inside SERVICE CommitteeMinutesSpecEdit ");
		CommitteeScheduleMinutesDetails committeeminutesdetails = new CommitteeScheduleMinutesDetails();
		committeeminutesdetails.setScheduleMinutesId(Long.parseLong(committeeminutesdetailsdto.getScheduleMinutesId()));
		
		return dao.CommitteeMinutesSpecEdit(committeeminutesdetails);
	}
	
	@Override
	public Object[] CommitteeMinutesSpecEditForAde(CommitteeMinutesDetailsDto committeeminutesdetailsdto) throws Exception {
		
		logger.info(new Date() +"Inside SERVICE CommitteeMinutesSpecEdit ");
		CommitteeScheduleMinutesDetails committeeminutesdetails = new CommitteeScheduleMinutesDetails();
		committeeminutesdetails.setScheduleMinutesId(Long.parseLong(committeeminutesdetailsdto.getScheduleMinutesId()));
		
		return dao.CommitteeMinutesSpecEditForAde(committeeminutesdetails);
	}
	
	@Override
	public Long CommitteeMinutesUpdate(CommitteeMinutesDetailsDto committeeminutesdetailsdto) throws Exception {

		logger.info(new Date() +"Inside SERVICE CommitteeMinutesUpdate ");
		CommitteeScheduleMinutesDetails committeeminutesdetails = new CommitteeScheduleMinutesDetails();
		committeeminutesdetails.setScheduleId(Long.parseLong(committeeminutesdetailsdto.getScheduleId()));
		committeeminutesdetails.setScheduleSubId(Long.parseLong(committeeminutesdetailsdto.getScheduleSubId()));
		committeeminutesdetails.setMinutesId(Long.parseLong(committeeminutesdetailsdto.getMinutesId()));
		committeeminutesdetails.setDetails(committeeminutesdetailsdto.getDetails());
		committeeminutesdetails.setIDARCK(committeeminutesdetailsdto.getIDARCK());
		committeeminutesdetails.setModifiedBy(committeeminutesdetailsdto.getModifiedBy());
		committeeminutesdetails.setModifiedDate(sdf1.format(new Date()));
		committeeminutesdetails.setScheduleMinutesId(Long.parseLong(committeeminutesdetailsdto.getScheduleMinutesId()));
		committeeminutesdetails.setRemarks(committeeminutesdetailsdto.getRemarks());
		
		if(committeeminutesdetailsdto.getAircraftId()!=null) {
			committeeminutesdetails.setAirCraftId(Long.parseLong(committeeminutesdetailsdto.getAircraftId()));;
		}
		if(committeeminutesdetailsdto.getSubSystemId()!=null) {
			committeeminutesdetails.setSubSystemId(Long.parseLong(committeeminutesdetailsdto.getSubSystemId()));;
		}
		

		return dao.CommitteeMinutesUpdate(committeeminutesdetails);
	}
	
	
	@Override
	public long CommitteeSubScheduleSubmit(CommitteeSubScheduleDto committeesubscheduledto) throws Exception 
	{
		logger.info(new Date() +"Inside SERVICE CommitteeSubScheduleSubmit ");
		CommitteeSubSchedule committeesubschedule=new CommitteeSubSchedule();
		committeesubschedule.setScheduleId(Long.parseLong(committeesubscheduledto.getScheduleId()));
		committeesubschedule.setScheduleDate(new java.sql.Date(sdf.parse(committeesubscheduledto.getScheduleDate()).getTime()));
		committeesubschedule.setScheduleStartTime(committeesubscheduledto.getScheduleStartTime());
		committeesubschedule.setCreatedBy(committeesubscheduledto.getCreatedBy());
		committeesubschedule.setCreatedDate(sdf1.format(new Date()));
		committeesubschedule.setScheduleFlag("N");
		committeesubschedule.setIsActive(1);		
		
		return dao.CommitteeSubScheduleSubmit(committeesubschedule);
	}
	
	@Override
	public List<Object[]> CommitteeSubScheduleList(String scheduleid) throws Exception
	{
		return dao.CommitteeSubScheduleList(scheduleid);
	}


	@Override
	public List<Object[]> CommitteeScheduleMinutes(String scheduleid) throws Exception
	{
		return dao.CommitteeScheduleMinutes(scheduleid);
	}



	@Override
	public List<Object[]> CommitteeMinutesSpecdetails()throws Exception
	{
		return dao.CommitteeMinutesSpecdetails();
	}

	@Override
	public List<Object[]> CommitteeMinutesSub()throws Exception
	{
		return dao.CommitteeMinutesSub();
	}

	@Override
	public List<Object[]> CommitteeAttendance(String CommitteeScheduleId) throws Exception {

		return dao.CommitteeAttendance(CommitteeScheduleId);
	}

	
	@Override
	public int MeetingAgendaApproval(String CommitteeScheduleId, String UserId, String EmpId,String Option ) throws Exception 
	{

		logger.info(new Date() +"Inside SERVICE MeetingAgendaApproval ");
		CommitteeMeetingApproval approval=new CommitteeMeetingApproval();
		CommitteeSchedule schedule= new CommitteeSchedule();
		
		
		List<PfmsNotification> notifications=new ArrayList<PfmsNotification>();
		String Status=null;
		if(Option.equalsIgnoreCase("Agenda Approval")) {
			Status="MAF";
		}else {
			Status="MAS";
		}
		
		approval.setScheduleId(Long.parseLong(CommitteeScheduleId));
		approval.setEmpId(Long.parseLong(EmpId));
		approval.setMeetingStatus(Status);
		approval.setActionBy(UserId);
		approval.setActionDate(sdf1.format(new Date()));
		
		schedule.setScheduleId(Long.parseLong(CommitteeScheduleId));
		schedule.setScheduleFlag("MAF");
		schedule.setModifiedBy(UserId);
		schedule.setModifiedDate(sdf1.format(new Date()));
		
		
		Object[] CommitteMainMembersData=dao.CommitteMainMembersData(CommitteeScheduleId,"CC");
		if(CommitteMainMembersData!=null && CommitteMainMembersData.length > 0)
		{
			PfmsNotification cpnotification= new PfmsNotification();
			cpnotification.setEmpId(Long.parseLong(CommitteMainMembersData[0].toString()));
			cpnotification.setNotificationby(Long.parseLong(EmpId));
			cpnotification.setNotificationDate(sdf1.format(new Date()));
			cpnotification.setNotificationMessage("Agenda Pending Approval For " + CommitteMainMembersData[2].toString() );
			cpnotification.setNotificationUrl("MeetingApprovalAgenda.htm");
			cpnotification.setCreatedBy(UserId);
			cpnotification.setCreatedDate(sdf1.format(new Date()));
			cpnotification.setIsActive(1);
			cpnotification.setScheduleId(Long.parseLong(CommitteeScheduleId));
			cpnotification.setStatus(Status);				
			notifications.add(cpnotification);
		}	
		CommitteMainMembersData=dao.CommitteMainMembersData(CommitteeScheduleId,"CS");
		if(CommitteMainMembersData!=null && CommitteMainMembersData.length>0)
		{
			PfmsNotification msnotification= new PfmsNotification();			
			msnotification.setEmpId(Long.parseLong(CommitteMainMembersData[0].toString()));
			msnotification.setNotificationby(Long.parseLong(EmpId));
			msnotification.setNotificationDate(sdf1.format(new Date()));
			msnotification.setNotificationMessage("Agenda Pending Approval For " + CommitteMainMembersData[2].toString() );
			msnotification.setNotificationUrl("MeetingApprovalAgenda.htm");
			msnotification.setCreatedBy(UserId);
			msnotification.setCreatedDate(sdf1.format(new Date()));
			msnotification.setIsActive(1);
			msnotification.setScheduleId(Long.parseLong(CommitteeScheduleId));
			msnotification.setStatus(Status);			
			notifications.add(msnotification);		
		}
		
		CommitteMainMembersData=dao.CommitteMainMembersData(CommitteeScheduleId,"PS");
		if(CommitteMainMembersData!=null && CommitteMainMembersData.length>0 )
		{
			PfmsNotification psnotification= new PfmsNotification();
			psnotification.setEmpId(Long.parseLong(CommitteMainMembersData[0].toString()));
			psnotification.setNotificationby(Long.parseLong(EmpId));
			psnotification.setNotificationDate(sdf1.format(new Date()));
			psnotification.setNotificationMessage("Agenda Pending Approval For " + CommitteMainMembersData[2].toString() );
			psnotification.setNotificationUrl("MeetingApprovalAgenda.htm");
			psnotification.setCreatedBy(UserId);
			psnotification.setCreatedDate(sdf1.format(new Date()));
			psnotification.setIsActive(1);
			psnotification.setScheduleId(Long.parseLong(CommitteeScheduleId));
			psnotification.setStatus(Status);			
			notifications.add(psnotification);		
		}
		
		return dao.MeetingAgendaApproval(approval,schedule,notifications);
	}

	@Override
	public List<Object[]> MeetingApprovalAgendaList(String EmpId) throws Exception 
	{
		return dao.MeetingApprovalAgendaList(EmpId);
	}
	

	@Override
	public int MeetingAgendaApprovalSubmit(String ScheduleId, String Remarks, String UserId, String EmpId,String Option) throws Exception {

		logger.info(new Date() +"Inside SERVICE MeetingAgendaApprovalSubmit ");
		CommitteeSchedule schedule= new CommitteeSchedule();
		CommitteeMeetingApproval approval = new CommitteeMeetingApproval();
		PfmsNotification notification=new PfmsNotification();
		
		schedule.setScheduleId(Long.parseLong(ScheduleId));
		schedule.setModifiedBy(UserId);
		schedule.setModifiedDate(sdf1.format(new Date()));
		
		approval.setScheduleId(Long.parseLong(ScheduleId));
		approval.setEmpId(Long.parseLong(EmpId));
		approval.setRemarks(Remarks);
		approval.setActionBy(UserId);
		approval.setActionDate(sdf1.format(new Date()));
		
		
		Object[] scheduleData =dao.CommitteeScheduleData(ScheduleId);
		
		Object[] NotificationData=dao.NotificationData(ScheduleId,EmpId,"MAF");
		if(NotificationData!=null) {
			notification.setEmpId(Long.parseLong(NotificationData[1].toString()));
		}else {
			notification.setEmpId(Long.parseLong("0"));
		}
		notification.setNotificationby(Long.parseLong(EmpId));
		notification.setNotificationDate(sdf1.format(new Date()));
		notification.setScheduleId(Long.parseLong(ScheduleId));
		notification.setCreatedBy(UserId);
		notification.setCreatedDate(sdf1.format(new Date()));
		notification.setIsActive(1);
		if(scheduleData[8].toString().equalsIgnoreCase("CCM")) {
			notification.setNotificationUrl("CCMSchedule.htm?ccmScheduleId="+ScheduleId+"&committeeMainId="+scheduleData[1].toString()+"&committeeId="+scheduleData[7].toString());
		}else {
			notification.setNotificationUrl("CommitteeScheduleView.htm?scheduleid="+ScheduleId);
		}
		
		if(Option.equalsIgnoreCase("approve")) {
			schedule.setScheduleFlag("MAA");
			approval.setMeetingStatus("MAA");
			notification.setNotificationMessage("Agenda Approved for " + scheduleData[8].toString());
			notification.setStatus("MAA");
		}
		if(Option.equalsIgnoreCase("return")) {
			schedule.setScheduleFlag("MAR");
			approval.setMeetingStatus("MAR");
			notification.setNotificationMessage("Agenda Returned for " + scheduleData[8].toString());
			notification.setStatus("MAR");
		}
		
		return dao.MeetingAgendaApprovalSubmit(schedule,approval,notification);
	}
	
	
	
	@Override
	public Object[] CommitteeScheduleData(String committeescheduleid) throws Exception
	{
		return dao.CommitteeScheduleData(committeescheduleid);
	}
	
	@Override
	public Object[] CommitteeScheduleDataPro(String committeescheduleid, String projectid) throws Exception
	{
		return dao.CommitteeScheduleDataPro(committeescheduleid, projectid);
	}


	@Override
	public List<Object[]> CommitteeAtendance(String committeescheduleid) throws Exception
	{
		return dao.CommitteeAtendance(committeescheduleid);
	}
	
	
	@Override
	public List<Object[]> EmployeeListNoInvitedMembers(String scheduleid,String LabCode) throws Exception
	{
		return dao.EmployeeListNoInvitedMembers(scheduleid,LabCode);
	}
	
	@Override
	public List<Object[]> ExternalMembersNotInvited(String scheduleid) throws Exception
	{
		return dao.ExternalMembersNotInvited(scheduleid);
	}
	
	@Override
	public List<Object[]> ExternalMembersNotAddedCommittee(String committeemainid) throws Exception
	{
		return dao.ExternalMembersNotAddedCommittee(committeemainid);		
	}
	
	@Override
	public Long CommitteeInvitationCreate(CommitteeInvitationDto committeeinvitationdto) throws Exception 
	{
		logger.info(new Date() +"Inside SERVICE CommitteeInvitationCreate ");
		long ret=0;
		long slno=1;
		Object[] maxslno=dao.InvitationMaxSerialNo(committeeinvitationdto.getCommitteeScheduleId());
		if(maxslno[1]!=null && Long.parseLong(maxslno[1].toString())>0) 
		{
			slno=Long.parseLong(maxslno[1].toString());
		}
	
		for(int i=0;i<committeeinvitationdto.getEmpIdList().size();i++) 
		{
			
			CommitteeInvitation committeeinvitation= new CommitteeInvitation();
			
			String MemberType[]=committeeinvitationdto.getEmpIdList().get(i).split(",");
			committeeinvitation.setCommitteeScheduleId(Long.parseLong(committeeinvitationdto.getCommitteeScheduleId()));
			
			committeeinvitation.setCreatedBy(committeeinvitationdto.getCreatedBy());
			committeeinvitation.setAttendance("P");
			committeeinvitation.setCreatedDate(sdf1.format(new Date()));
			committeeinvitation.setEmpId(Long.parseLong(MemberType[0]));
			if(committeeinvitationdto.getReptype()!= null && !committeeinvitationdto.getReptype().equals("0")) 
			{
				committeeinvitation.setMemberType(committeeinvitationdto.getReptype());
			}
			else 
			{
				committeeinvitation.setMemberType(MemberType[1]);
			}
			committeeinvitation.setDesigId(MemberType[2]);
			
			if(!committeeinvitationdto.getLabCodeList().isEmpty()) {
				committeeinvitation.setLabCode(committeeinvitationdto.getLabCodeList().get(i));
			}
			
			// Check weather this employee is already invited as committee member 
			if(!MemberType[1].equalsIgnoreCase("SPL") && dao.CommitteeInvitationCheck(committeeinvitation).size()>0)
			{
				continue;
			}
			else
			{
				if(committeeinvitationdto.getInviteFlag()!=null && committeeinvitationdto.getInviteFlag().equalsIgnoreCase("Y")) {
					if(MemberType[1].equalsIgnoreCase("CC")) {
						committeeinvitation.setSerialNo(1);
					}else if(MemberType[1].equalsIgnoreCase("CS")) {
						committeeinvitation.setSerialNo(committeeinvitationdto.getEmpIdList().size());
					}else {
						committeeinvitation.setSerialNo(MemberType.length>3 && Integer.parseInt(MemberType[3])>0?Integer.parseInt(MemberType[3]):++slno);
						
					}
				}else {
					committeeinvitation.setSerialNo(++slno);
					
				}

				
				ret=dao.CommitteeInvitationCreate(committeeinvitation);
			}
			
		}
		
		return ret; 
	}

	@Override
	public Long CommitteeInvitationDelete(String committeeinvitationid) throws Exception
	{
		logger.info(new Date() +"Inside SERVICE CommitteeInvitationDelete ");
		
		
			List<Object[]> serialnoafter=dao.CommitteeInvitationSerialNoAfter(committeeinvitationid);
			for(Object[] obj : serialnoafter)
			{
				dao.CommitteeInvitationSerialNoUpdate(obj[0].toString(), Long.parseLong(obj[1].toString())-1 );
			}
			
		long count=dao.CommitteeInvitationDelete(committeeinvitationid);
		return count;
	}


	@Override
	public Long CommitteeAttendanceToggle(String InvitationId) throws Exception {

		logger.info(new Date() +"Inside SERVICE CommitteeAttendanceToggle ");
		List<String> AttendanceList=dao.CommitteeAttendanceList(InvitationId);
		
		String Value=null;
		
		for(int i=0; i<AttendanceList.size();i++ ) {
			 Value=AttendanceList.get(i);	
		}
		
		long ret=dao.CommitteeAttendanceUpdate(InvitationId,Value);
			
		return ret;
	}



	@Override
	public Long ScheduleMinutesUnitUpdate(CommitteeMinutesDetailsDto detailsdto) throws Exception {
		
		logger.info(new Date() +"Inside SERVICE ScheduleMinutesUnitUpdate ");
		CommitteeScheduleMinutesDetails minutesdetails = new CommitteeScheduleMinutesDetails();
		minutesdetails.setScheduleId(Long.parseLong(detailsdto.getScheduleId()));
		minutesdetails.setScheduleSubId(1);
		minutesdetails.setMinutesId(Long.parseLong(detailsdto.getMinutesId()));
		minutesdetails.setMinutesSubId(Long.parseLong(detailsdto.getMinutesSubId()));
		minutesdetails.setMinutesSubOfSubId(Long.parseLong(detailsdto.getMinutesSubOfSubId()));
		minutesdetails.setCreatedBy(detailsdto.getCreatedBy());
		minutesdetails.setCreatedDate(sdf1.format(new Date()));
		
		return dao.ScheduleMinutesUnitUpdate(minutesdetails);
	}

	@Override
	public List<Object[]> MinutesUnitList(String CommitteeScheudleId) throws Exception {

		return dao.MinutesUnitList(CommitteeScheudleId);
	}
	
	@Override
	public List<Object[]> CommitteeAgendaPresenter(String scheduleid) throws Exception
	{
		return dao.CommitteeAgendaPresenter(scheduleid);
	}
	
	@Override
	public List<Object[]> PresenterRemovalEmpList(List<Object[]> Employeelist, List<Object[]> PresenterList) throws Exception
	{
		logger.info(new Date() +"Inside SERVICE PresenterRemovalEmpList ");
		for(int i=0;i<Employeelist.size();i++)
		{
			for(int j=0;j<PresenterList.size();j++)
			{
				if(Employeelist.get(i)[0].toString().equals(PresenterList.get(j)[0].toString()))
				{
					Employeelist.remove(i);						
				}
			}
		}
		return Employeelist;
	}

	
	
	
	@Override 
	public List<Object[]> LoginProjectDetailsList(String empid,String Logintype ,String LabCode) throws Exception
	{
		logger.info(new Date() +"Inside SERVICE LoginProjectDetailsList "); 
		List<Object[]> projectidlist=(ArrayList<Object[]>) dao.LoginProjectDetailsList(empid,Logintype,LabCode);  
		return projectidlist;
	}
	

	
	@Override
	public Object[] projectdetails(String projectid) throws Exception
	{
		return dao.projectdetails(projectid);
	}
	
	@Override
	public List<Object[]> ProjectScheduleListAll(String projectid) throws Exception
	{
		return dao.ProjectScheduleListAll(projectid);
	}
	@Override
	public List<Object[]> ProjectApplicableCommitteeList(String projectid)throws Exception
	{
		return dao.ProjectApplicableCommitteeList(projectid);
	}
	@Override
	public  int UpdateComitteeMainid(String committeemainid, String scheduleid ) throws Exception
	{
		return dao.UpdateComitteeMainid(committeemainid, scheduleid);
	}
	@Override
	public List<Object[]> ProjectCommitteeScheduleListAll(String projectid,String committeeid) throws Exception
	{
		return dao.ProjectCommitteeScheduleListAll(projectid, committeeid);
	}
	
	@Override
	public  List<Object[]> ChaipersonEmailId(String CommitteeMainId) throws Exception {

		return dao.ChaipersonEmailId(CommitteeMainId);
	}
	
	@Override
	public Object[] ProjectDirectorEmail(String ProjectId) throws Exception {

		return dao.ProjectDirectorEmail(ProjectId);
	}
	
	@Override
	public Object[] RtmddoEmail() throws Exception {

		return dao.RtmddoEmail();
	}

	@Override
	public String UpdateOtp(CommitteeScheduleDto committeescheduledto) throws Exception {

		logger.info(new Date() +"Inside SERVICE UpdateOtp ");
		CommitteeSchedule schedule=new CommitteeSchedule();
		schedule.setKickOffOtp(committeescheduledto.getKickOffOtp());
		schedule.setScheduleId(committeescheduledto.getScheduleId());
		schedule.setScheduleFlag(committeescheduledto.getScheduleFlag());
		
		return dao.UpdateOtp(schedule);

	}

	@Override
	public String KickOffOtp(String CommitteeScheduleId) throws Exception {

		return dao.KickOffOtp(CommitteeScheduleId);
	}

	

	@Override
	public List<Object[]> UserSchedulesList(String EmpId,String MeetingId) throws Exception {

		return dao.UserSchedulesList(EmpId,"%"+MeetingId+"%");
	}
	
	@Override
	public List<Object[]> MeetingSearchList(String MeetingId ,String LabCode) throws Exception {

		return dao.MeetingSearchList("%"+MeetingId+"%" ,LabCode);

	}

	@Override
	public long ProjectCommitteeAdd(String ProjectId, String[] Committee, String UserId) throws Exception {

		logger.info(new Date() +"Inside SERVICE ProjectCommitteeAdd ");
		long count=0;
		for(int i=0;i<Committee.length;i++) {
			Object[] committeedata=dao.CommitteeDetails(Committee[i]);
			CommitteeProject committeeproject=new CommitteeProject();
			committeeproject.setProjectId(Long.parseLong(ProjectId));
			committeeproject.setCommitteeId(Long.parseLong(Committee[i]));
			committeeproject.setCreatedBy(UserId);
			committeeproject.setCreatedDate(sdf1.format(new Date()));
			committeeproject.setDescription(committeedata[10].toString());
			committeeproject.setTermsOfReference(committeedata[11].toString());
			count=dao.ProjectCommitteeAdd(committeeproject);
		}
				
		return count;
	}
	
	
	@Override
	public long InitiationCommitteeAdd(String initiation, String[] Committee, String UserId) throws Exception {

		logger.info(new Date() +"Inside SERVICE InitiationCommitteeAdd ");
		long count=0;
		for(int i=0;i<Committee.length;i++) {
			Object[] committeedata=dao.CommitteeDetails(Committee[i]);
			CommitteeInitiation model=new CommitteeInitiation(); 
			model.setInitiationId(Long.parseLong(initiation));
			model.setCommitteeId(Long.parseLong(Committee[i]));
			model.setAutoSchedule("N");
			model.setCreatedBy(UserId);
			model.setCreatedDate(sdf1.format(new Date()));
			model.setDescription(committeedata[10].toString());
			model.setTermsOfReference(committeedata[11].toString());
			count=dao.InitiationCommitteeAdd(model);
		}
				
		return count;
	}


	@Override
	public List<Object[]> ProjectMasterList(String ProjectId) throws Exception {

		return dao.ProjectMasterList(ProjectId);
	}

	@Override
	public long ProjectCommitteeDelete( String[] CommitteeProject,String user) throws Exception {

		logger.info(new Date() +"Inside SERVICE ProjectCommitteeDelete ");
		long count=0;
		for(int i=0;i<CommitteeProject.length;i++) {
			CommitteeProject committeeproject=new CommitteeProject();
			committeeproject.setCommitteeProjectId(Long.parseLong(CommitteeProject[i]));		
			count=dao.ProjectCommitteeDelete(committeeproject);
		}				
		return count;
		
	}
	
	@Override
	public Object[] LabDetails(String LabCode)throws Exception
	{
		return dao.LabDetails(LabCode);
	}



	@Override
	public List<Object[]> ProjectCommitteesListNotAdded(String projectid,String LabCode) throws Exception {
		return dao.ProjectCommitteesListNotAdded(projectid, LabCode);		
	}



	@Override
	public List<Object[]> CommitteeAutoScheduleList(String ProjectId,String divisionid,String initiationid,String projectstatus) throws Exception {

		return dao.CommitteeAutoScheduleList(ProjectId,divisionid,initiationid,projectstatus);
	}
	
	@Override
	public List<Object[]> CommitteeAutoScheduleList(String ProjectId,String committeeid, String divisionid , String initiationid,String projectstatus) throws Exception 
	{
		return dao.CommitteeAutoScheduleList(ProjectId, committeeid,divisionid,initiationid,projectstatus);
	}
	
	@Override
	public Object[] CommitteeLastScheduleDate(String committeeid) throws Exception 
	{
		return dao.CommitteeLastScheduleDate(committeeid);
	}

	@Override
	public int CommitteeProjectUpdate(String ProjectId, String CommitteeId) throws Exception {

		return dao.CommitteeProjectUpdate(ProjectId,CommitteeId);
	}


	@Override
	public int UpdateMeetingVenue(CommitteeScheduleDto csdto) throws Exception
	{
		return dao.UpdateMeetingVenue(csdto);
	}

	@Override
	public long MinutesAttachmentAdd(CommitteeMinutesAttachmentDto dto) throws Exception 
	{
		logger.info(new Date() +"Inside SERVICE MinutesAttachmentAdd ");
		
		String LabCode= dto.getLabCode();
		Timestamp instant= Timestamp.from(Instant.now());
		String timestampstr = instant.toString().replace(" ","").replace(":", "").replace("-", "").replace(".","");
		
//		String Path = LabCode+"\\CommitteeMinutesAttachmentFile\\";
		Path filepath = Paths.get(uploadpath, LabCode, "CommitteeMinutesAttachmentFile");
		Path filepath1 = Paths.get(LabCode, "CommitteeMinutesAttachmentFile");
		CommitteeMinutesAttachment attachment= new CommitteeMinutesAttachment();
		attachment.setScheduleId(Long.parseLong(dto.getScheduleId()));
		attachment.setFilePath(filepath1.toString());
		attachment.setAttachmentName("minutesAttach"+timestampstr+"."+FilenameUtils.getExtension(dto.getMinutesAttachment().getOriginalFilename()));
		saveFile1(filepath, attachment.getAttachmentName(), dto.getMinutesAttachment());
		attachment.setCreatedBy(dto.getCreatedBy());
		attachment.setCreatedDate(sdf1.format(new Date()));		
		if(dto.getMinutesAttachmentId()!=null)
		{
			CommitteeMinutesAttachment attach = dao.MinutesAttachDownload(dto.getMinutesAttachmentId());
			Path filepath2 = Paths.get(uploadpath, LabCode, "CommitteeMinutesAttachmentFile", attach.getAttachmentName());
//			String filepath2 = uploadpath+attach.getFilePath()+attach.getAttachmentName();
			File file = filepath2.toFile();
			if(file.exists()) {	file.delete(); }
			dao.MinutesAttachmentDelete(dto.getMinutesAttachmentId());
		}		
		return dao.MinutesAttachmentAdd(attachment);		
	}
	
	public void saveFile(String uploadpath, String fileName, MultipartFile multipartFile) throws IOException 
	{
	   logger.info(new Date() +"Inside SERVICE saveFile ");
	   Path uploadPath = Paths.get(uploadpath);
	          
	   if (!Files.exists(uploadPath)) {
		   Files.createDirectories(uploadPath);
	   }
	        
	   try (InputStream inputStream = multipartFile.getInputStream()) {
		   Path filePath = uploadPath.resolve(fileName);
	       Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
	   } catch (IOException ioe) {       
		   throw new IOException("Could not save file: " + fileName, ioe);
	   }     
	}
	@Override
	public int MinutesAttachmentDelete(String  attachmentid) throws Exception
	{
		CommitteeMinutesAttachment attachment = dao.MinutesAttachDownload(attachmentid);
		String filedata =attachment.getFilePath().replaceAll("[/\\\\]", ",");
		String[] fileParts = filedata.split(",");
		Path filepath = Paths.get(uploadpath, fileParts[0], fileParts[1], attachment.getAttachmentName());
	//	String filepath = uploadpath+attachment.getFilePath()+attachment.getAttachmentName();
		File file = filepath.toFile();
		if(file.exists()) { file.delete(); }
		return dao.MinutesAttachmentDelete(attachmentid);
	}	
	@Override
	public List<Object[]> MinutesAttachmentList(String scheduleid ) throws Exception 
	{
		return dao.MinutesAttachmentList(scheduleid);
	}	
	@Override
	public CommitteeMinutesAttachment MinutesAttachDownload(String attachmentid) throws Exception 
	{
		return dao.MinutesAttachDownload(attachmentid);
	}	
	@Override
	public List<Object[]> PfmsCategoryList() throws Exception 
	{
		return dao.PfmsCategoryList();
	}
	
	@Override
	public int MeetingMinutesApproval(String CommitteeScheduleId, String UserId, String EmpId,String Option ) throws Exception 
	{
		logger.info(new Date() +"Inside SERVICE MeetingMinutesApproval ");
		CommitteeMeetingApproval approval=new CommitteeMeetingApproval();
		CommitteeSchedule schedule= new CommitteeSchedule();
	
		List<Object[]> AllActionAssignedCheck=dao.AllActionAssignedCheck(CommitteeScheduleId);
		for(Object[] obj:AllActionAssignedCheck)
		{
			if(obj[3]==null) {
				return -1;
			}
		}
		List<String>statuses=Arrays.asList("MMF","MMS","MMA");
		Object[] committeescheduleeditdata=dao.CommitteeScheduleEditData(CommitteeScheduleId);
		if(!statuses.contains(committeescheduleeditdata[4].toString())) {
		String Status=null;
		if(Option.equalsIgnoreCase("Minutes Approval")) {
			Status="MMF";
		}else {
			Status="MMS";
		}
		
//		approval.setScheduleId(Long.parseLong(CommitteeScheduleId));
//		approval.setEmpId(Long.parseLong(EmpId));
//		approval.setMeetingStatus(Status);
//		approval.setActionBy(UserId);
//		approval.setActionDate(sdf1.format(new Date()));
//		
//		schedule.setScheduleId(Long.parseLong(CommitteeScheduleId));
//		schedule.setScheduleFlag("MMF");
//		schedule.setModifiedBy(UserId);
//		schedule.setModifiedDate(sdf1.format(new Date()));
//		dao.MeetingMinutesApproval(approval,schedule);
		}

		Object[]CommitteMainEnoteList= dao.CommitteMainEnoteList("0",CommitteeScheduleId);
		if(CommitteMainEnoteList==null) {
		PmsEnote pe = new PmsEnote();
		pe.setRefNo(committeescheduleeditdata[11].toString());
		pe.setRefDate(java.sql.Date.valueOf(committeescheduleeditdata[2].toString()));
		pe.setEnoteStatusCode("INI");
		pe.setEnoteStatusCodeNext("INI");
		pe.setCommitteeMainId(0l);		pe.setSubject("Forwarding MoM");;
		pe.setScheduleId(Long.parseLong(CommitteeScheduleId));
		pe.setInitiatedBy(Long.parseLong(EmpId));
		pe.setCreatedBy(UserId);
		pe.setCreatedDate(sdf1.format(new Date()));
		pe.setEnoteFrom("S");
		pe.setIsActive(1);
		long enoteId =dao.addPmsEnote(pe);
		PmsEnoteTransaction transaction= PmsEnoteTransaction.builder()
				.EnoteId(enoteId)
				.EnoteStatusCode("INI")
				.Remarks("")
				.EnoteFrom("S")
				.ActionBy(Long.parseLong(EmpId))
				.ActionDate(sdf1.format(new Date()))
				.build();
		dao.addEnoteTrasaction(transaction);
		}
		return 2;
		
	}

	@Override
	public List<Object[]> MeetingApprovalMinutesList(String EmpId) throws Exception {

		return dao.MeetingApprovalMinutesList(EmpId);
	}


	@Override
	public int MeetingMinutesApprovalSubmit(String ScheduleId, String Remarks, String UserId, String EmpId,String Option) throws Exception {

		logger.info(new Date() +"Inside SERVICE MeetingMinutesApprovalSubmit ");
		CommitteeSchedule schedule= new CommitteeSchedule();
		CommitteeMeetingApproval approval = new CommitteeMeetingApproval();
		PfmsNotification notification=new PfmsNotification();
		
		schedule.setScheduleId(Long.parseLong(ScheduleId));
		schedule.setModifiedBy(UserId);
		schedule.setModifiedDate(sdf1.format(new Date()));
		
		approval.setScheduleId(Long.parseLong(ScheduleId));
		approval.setEmpId(Long.parseLong(EmpId));
		approval.setRemarks(Remarks);
		approval.setActionBy(UserId);
		approval.setActionDate(sdf1.format(new Date()));
		
		Object[] NotificationData=dao.NotificationData(ScheduleId,EmpId,"MMF");
		Object[] scheduleData = dao.CommitteeScheduleData(ScheduleId);
		
		if(NotificationData!=null) {
			notification.setEmpId(Long.parseLong(NotificationData[1].toString()));
		}else {
			notification.setEmpId(Long.parseLong("0"));
		}
		notification.setNotificationby(Long.parseLong(EmpId));
		notification.setNotificationDate(sdf1.format(new Date()));
		notification.setScheduleId(Long.parseLong(ScheduleId));
		notification.setCreatedBy(UserId);
		notification.setCreatedDate(sdf1.format(new Date()));
		notification.setIsActive(1);
		if(scheduleData[8].toString().equalsIgnoreCase("CCM")) {
			notification.setNotificationUrl("CCMSchedule.htm?ccmScheduleId="+ScheduleId+"&committeeMainId="+scheduleData[1].toString()+"&committeeId="+scheduleData[7].toString());
		}else {
			notification.setNotificationUrl("CommitteeScheduleView.htm?scheduleid="+ScheduleId);
		}
		try {
			if(Option.equalsIgnoreCase("approve")) {
				schedule.setScheduleFlag("MMA");
				approval.setMeetingStatus("MMA");
				notification.setNotificationMessage("Minutes Approved for " + scheduleData[8].toString());
				notification.setStatus("MMA");
			}
			if(Option.equalsIgnoreCase("return")) {
				schedule.setScheduleFlag("MMR");
				approval.setMeetingStatus("MMR");
				notification.setNotificationMessage("Minutes Returned for " + scheduleData[8].toString());
				notification.setStatus("MMR");
			}
		}catch (Exception e) {
			logger.error(new Date() +" Inside SERVICE MeetingMinutesApprovalSubmit ", e);
		}
		return dao.MeetingMinutesApprovalSubmit(schedule,approval,notification);
	}
	
	@Override
	public List<Object[]> CommitteeAllAttendance(String CommitteeScheduleId) throws Exception {
		 
		return dao.CommitteeAllAttendance(CommitteeScheduleId);
	}

	@Override
	public List<Object[]> MeetingReports(String EmpId, String Term, String ProjectId,String divisionid,String initiationid,String logintype,String LabCode) throws Exception {

		return dao.MeetingReports(EmpId,Term,ProjectId,divisionid,initiationid,logintype,LabCode);
	}

	@Override
	public List<Object[]> MeetingReportListAll(String fdate, String tdate, String ProjectId) throws Exception {
		
		return dao.MeetingReportListAll(fdate,tdate,ProjectId);
	}
	
	@Override
	public List<Object[]> MeetingReportListEmp(String fdate, String tdate, String ProjectId, String EmpId)	throws Exception {		
		return dao.MeetingReportListEmp(fdate,tdate,ProjectId,EmpId);
	}
	
	
	@Override
	public Object[] KickOffMeeting(HttpServletRequest req,RedirectAttributes redir) throws Exception
	{
		logger.info(new Date() +"Inside SERVICE KickOffMeeting ");
		String CommitteeMainId=req.getParameter("committeemainid");
		String CommitteeScheduleId=req.getParameter("committeescheduleid");
		String Option=req.getParameter("sub");
		SimpleDateFormat sdf1=new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		Object[] scheduledata=dao.CommitteeScheduleData(CommitteeScheduleId);
		LocalDate scheduledate=LocalDate.parse(scheduledata[2].toString(),dtf);
	
		if(LocalDate.now().isAfter(scheduledate))
			{	
			CommitteeSchedule committeeschedule=new CommitteeSchedule(); 
			committeeschedule.setKickOffOtp(null);
			committeeschedule.setScheduleId(Long.parseLong(CommitteeScheduleId));
			committeeschedule.setScheduleFlag("MKV");
			String  ret=dao.UpdateOtp(committeeschedule);
			if (Integer.parseInt(ret)>0) {
			redir.addAttribute("result", "Meeting Kick Off Successful");
			} else {
			redir.addAttribute("result", "Meeting Kick Off UnSuccessful");
			}	
			Object[] returnobj=new Object[2];
			returnobj[0]=req;
			returnobj[1]=redir;
			return returnobj;
			
		}else if(!Option.equalsIgnoreCase("Validate")) 
		{		
			List<Object[]> Email=dao.ChaipersonEmailId(CommitteeMainId);
			Random random = new Random(); 
			int otp=random.nextInt(9000) + 1000;
			CharSequence cs = String.valueOf(otp);
			String Otp=encoder.encode(cs);
			CommitteeSchedule committeeschedule=new CommitteeSchedule(); 
			committeeschedule.setKickOffOtp(Otp);
			committeeschedule.setScheduleId(Long.parseLong(CommitteeScheduleId));
			committeeschedule.setScheduleFlag("MKO");
			ArrayList<String> emails= new ArrayList<String>();
			for(Object[] obj : Email)
			{
			if(obj[0]!=null) {
			emails.add(obj[0].toString());
			}
			}
			if(Email.get(0)[1].toString().equalsIgnoreCase("0")) 
			{
				Object[] RtmddoEmail=dao.RtmddoEmail();
				if(RtmddoEmail!=null) {
				emails.add(RtmddoEmail[0].toString());
				}
			}
			
				
			String [] ToEmail = emails.toArray(new String[emails.size()]);
			
			if (ToEmail.length>0) 
			{
				int count=0;
				String subject=req.getParameter("committeeshortname") + " Meeting OTP ";
				String message=String.valueOf(otp) + " is the OTP for Verification of Meeting ( " + req.getParameter("meetingid") +" ) which is Scheduled at " + sdf1.format(sdf.parse(req.getParameter("meetingdate"))) + "(" + req.getParameter("meetingtime") + "). Kindly Do Not Share the OTP with Anyone.";
				for(String email:ToEmail) {
				count = count +	cm.sendMessage(email, subject, message);
				}
			try
			{
//					javaMailSender.send(msg);
			}catch (Exception e) {
				logger.error(new Date() +" Inside SERVICE KickOffMeeting ", e);
				}
			dao.UpdateOtp(committeeschedule);
				redir.addAttribute("result", " OTP Sent to Successfully !! ");
				redir.addFlashAttribute("otp", String.valueOf(otp));
		}else
			{
				redir.addAttribute("resultfail", "Email-ids' Not Found");
		}

		 }

		 else {
		 
			 if(req.getParameter("otpvalue")!=null) {
			 
			 String OtpDb=dao.KickOffOtp(CommitteeScheduleId);

		 		if(encoder.matches( req.getParameter("otpvalue"),OtpDb)) {
		 			
		 			CommitteeSchedule committeescheduledto=new CommitteeSchedule(); 
				 	committeescheduledto.setKickOffOtp(OtpDb);
				 	committeescheduledto.setScheduleId(Long.parseLong(CommitteeScheduleId));
				 	committeescheduledto.setScheduleFlag("MKV");
				 	String Validate=dao.UpdateOtp(committeescheduledto);
				 	
				 	if (Validate !=null) {
						redir.addAttribute("result", " OTP Validated Successfully ");
					} else {
						redir.addAttribute("result", " OTP Not Matched");
					}	
				 	
			 	}else {
			 		redir.addAttribute("resultfail", " OTP Not Matched");
			 	}
			 }
		 }
		 Object[] returnobj=new Object[2];
		 returnobj[0]=req;
		 returnobj[1]=redir;
		 return returnobj;
		 
	}
	
	@Override
	public int UpdateCommitteeInvitationEmailSent(String scheduleid)throws Exception
	{
		return dao.UpdateCommitteeInvitationEmailSent(scheduleid);
	}
	
//	@Override
//	public Object[] SendInvitationLetter(HttpServletRequest req,RedirectAttributes redir) throws Exception
//	{
//		logger.info(new Date() +"Inside SendInvitationLetter");
//		 List<Object[]> committeeinvitedlist=dao.CommitteeAtendance(req.getParameter("scheduleid"));
//		 Object[] scheduledata=dao.CommitteeScheduleEditData(req.getParameter("scheduleid"));
//		 SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
//		 ArrayList<String> emails= new ArrayList<String>();
//
//		 for(Object[] obj : committeeinvitedlist) {
//			 
//			 if(!obj[3].toString().equalsIgnoreCase("E") && obj[9].toString().equals("N") ) {				 
//				 emails.add(obj[8].toString());		
//			 }
//		 }
//		 
//		 
//		 String ProjectName="General Type";
//		 if(!scheduledata[9].toString().equalsIgnoreCase("0")) {
//			 
//			ProjectName= dao.projectdetails(scheduledata[9].toString())[1].toString();
//			 
//		 }
//		 
//		 String [] Email = emails.toArray(new String[emails.size()]);
//		 
//		 MimeMessage msg = javaMailSender.createMimeMessage();
//		 MimeMessageHelper helper = new MimeMessageHelper(msg, true);
//		 helper.setTo(Email);
//		 String Message="<p>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;This is to inform you that Meeting is Scheduled for the  <b>"+ scheduledata[7]  + "(" + scheduledata[8] + ")" +"</b> committee of <b>"+ ProjectName +"</b> and further details about the meeting is mentioned below. </p> <table style=\"align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; max-width: 650px; font-size: 16px; border-collapse:collapse;\" >"
//		 		+ "<tr><th colspan=\"2\" style=\"text-align: left; font-weight: 700; width: 650px;border: 1px solid black; padding: 5px; padding-left: 15px\">Meeting Details </th></tr>"
//		 		 + "<tr><td style=\"border: 1px solid black; padding: 5px;text-align: left\"> Date :  </td>"
//		 		 + "<td style=\"border: 1px solid black; padding: 5px;text-align: left\">" + sdf.format(scheduledata[2])  + "</td></tr>"
//		 		 
//		 		 +"<tr>"
//				 + "<td style=\"border: 1px solid black; padding: 5px;text-align: left\"> Time : </td>"
//				 + "<td style=\"border: 1px solid black; padding: 5px;text-align: left\">" + scheduledata[3]  + "</td></tr>"
//				 +"<tr>"
//				 +"<td style=\"border: 1px solid black; padding: 5px;text-align: left\"> Venue</td>"
//				 +"<td style=\"border: 1px solid black; padding: 5px;text-align: left\">"+ scheduledata[12] +"</td>"
//				 +"</tr>"				 
//				 ;
//				 
//		 
//		 helper.setSubject( scheduledata[8] + " " +" Committee Invitation Letter");
//		 helper.setText( Message , true);
//		 javaMailSender.send(msg); 
//			     
//		 if (Email.length>0) {
//			 dao.UpdateCommitteeInvitationEmailSent(req.getParameter("scheduleid"));
//			redir.addAttribute("result", " Committee Invitation Letter Sent Successfully !! ");
//		 } 
//	 redir.addFlashAttribute("committeescheduleid",req.getParameter("scheduleid"));
//	 Object[] returnobj=new Object[2];
//	 returnobj[0]=req;
//	 returnobj[1]=redir;
//	 return returnobj;	 
//	}
	
	@Override
	public List<Object[]> MinutesViewAllActionList(String scheduleid) throws Exception {
		return dao.MinutesViewAllActionList( scheduleid); 
	}
	
//	@Override
//	public List<Object[]> NonProjectCommitteesList() throws Exception {		
//		logger.info(new Date() +"Inside NonProjectCommitteesList");
//		return dao.NonProjectCommitteesList();
//	}

	@Override
	public List<Object[]> ProjectCommitteesList(String LabCode) throws Exception {
		return dao.ProjectCommitteesList( LabCode);
	}

	@Override
	public List<Object[]> ExpertList() throws Exception {
		return dao.ExpertList();
	}

	@Override
	public List<Object[]> AllLabList() throws Exception {
		return dao.AllLabList();
	}

	@Override
	public List<Object[]> ClusterLabs(String LabCode) throws Exception 
	{
		return dao.ClusterLabs(LabCode);
	}

	@Override
	public List<Object[]> ExternalEmployeeListFormation(String CpLabCode,String committeemainid) throws Exception {
		return dao.InternalEmployeeListFormation(CpLabCode,committeemainid);
	}
	
	@Override
	public List<Object[]> ClusterExpertsList(String committeemainid) throws Exception
	{
		return dao.ClusterExpertsList(committeemainid);
	}

	@Override
	public List<Object[]> ClusterExpertsListForCommitteeSchdule() throws Exception
	{
		return dao.ClusterExpertsListForCommitteeSchdule();
	}
	
	@Override
	public List<Object[]> ChairpersonEmployeeListFormation(String LabCode,String committeemainid) throws Exception {
		return dao.ChairpersonEmployeeList(LabCode,committeemainid);
	}
	
	@Override
	public List<Object[]> PreseneterForCommitteSchedule(String LabCode)throws Exception
	{
		return dao.PreseneterForCommitteSchedule(LabCode);
	}
	
	
	@Override
	public Object[] LabInfoClusterLab(String LabCode) throws Exception 
	{
		return dao.LabInfoClusterLab(LabCode);
	}
	
	@Override
	public List<Object[]> ExternalEmployeeListInvitations(String labcode,String scheduleid) throws Exception {
		return dao.ExternalEmployeeListInvitations(labcode,scheduleid);
	}


	@Override
	public long CommitteeMembersInsert(CommitteeMembersDto dto) throws Exception {
		
		logger.info(new Date() +"Inside SERVICE CommitteeMembersInsert ");
		long count=0;
		
		ArrayList<String> emplist=new ArrayList<String>();
		ArrayList<String> lablist=new ArrayList<String>();
		ArrayList<String> membertype=new ArrayList<String>();
		
		if (dto.getInternalMemberIds()!=null) 
		{

			for(int i=0;i<dto.getInternalMemberIds().length;i++) {
				
				lablist.add(dto.getInternalLabCode());
				membertype.add("CI");
			}
			
			emplist.addAll(Arrays.asList(dto.getInternalMemberIds()));
		}
		
		if (dto.getExternalMemberIds()!=null) 
		{

			for(int i=0;i<dto.getExternalMemberIds().length;i++) {
				
				lablist.add(dto.getExternalLabCode());
				membertype.add("CW");
			}
			
			emplist.addAll(Arrays.asList(dto.getExternalMemberIds()));
		}
		
		if (dto.getExpertMemberIds() != null) {
			
			for(int i=0;i<dto.getExpertMemberIds().length;i++) {
				
				lablist.add("@EXP");
				membertype.add("CO");
			}
			emplist.addAll(Arrays.asList(dto.getExpertMemberIds()));
		}

		// Prudhvi 27/03/2024
		/* ------------------ start ----------------------- */
		if (dto.getIndustrialPartnerRepIds() != null) {
			
			for(int i=0;i<dto.getIndustrialPartnerRepIds().length;i++) {
				
				lablist.add("@IP");
				membertype.add("CIP");
			}
			emplist.addAll(Arrays.asList(dto.getIndustrialPartnerRepIds()));
		}
		/* ------------------ end ----------------------- */
		
		for(int i=0;i< emplist.size();i++)
		{
			CommitteeMember  committeemember=new CommitteeMember();
			committeemember.setCommitteeMainId(Long.parseLong(dto.getCommitteeMainId()));			
			committeemember.setEmpId(Long.parseLong(emplist.get(i)));
			committeemember.setLabCode(lablist.get(i));
			committeemember.setMemberType(membertype.get(i));
			committeemember.setCreatedBy(dto.getCreatedBy());
			committeemember.setCreatedDate(sdf1.format(new Date()));
			committeemember.setIsActive(1);
			count=dao.CommitteeMainMembersAdd(committeemember);
		}		
		
		return count;
	}

	
	
	@Override
	public List<Object[]> CommitteeAllMembers(String committeemainid) throws Exception {
		return dao.CommitteeAllMembers(committeemainid);
	}
	
	@Override
	public Object[] ProjectBasedMeetingStatusCount(String projectid) throws Exception
	{
		return dao.ProjectBasedMeetingStatusCount(projectid);
	}
	
		
	@Override
	public List<Object[]> allprojectdetailsList() throws Exception {
		return dao.allprojectdetailsList();
	}
	
	@Override
	public List<Object[]> PfmsMeetingStatusWiseReport(String projectid,String statustype) throws Exception
	{
		return dao.PfmsMeetingStatusWiseReport(projectid, statustype);
	}
	
	@Override
	public List<Object[]> ProjectCommitteeFormationCheckList(String projectid) throws Exception
	{
		return dao.ProjectCommitteeFormationCheckList(projectid);
	}
	
	@Override
	public Object[] ProjectCommitteeDescriptionTOR(String projectid,String Committeeid) throws Exception
	{
		return dao.ProjectCommitteeDescriptionTOR(projectid, Committeeid);	
	}
	
	@Override
	public Object[] DivisionCommitteeDescriptionTOR(String divisionid,String Committeeid) throws Exception
	{	
		return dao.DivisionCommitteeDescriptionTOR(divisionid, Committeeid);	
	}

	@Override
	public int ProjectCommitteeDescriptionTOREdit( CommitteeProject  committeeproject ) throws Exception
	{	
		committeeproject.setModifiedDate(sdf1.format(new Date()));
		return dao.ProjectCommitteeDescriptionTOREdit(committeeproject);
	}
	
	
	@Override
	public int DivisionCommitteeDescriptionTOREdit(CommitteeDivision committeedivision) throws Exception
	{	
		committeedivision.setModifiedDate(sdf1.format(new Date()));
		return dao.DivisionCommitteeDescriptionTOREdit(committeedivision);
	}

	@Override
	public long CommitteePreviousAgendaAdd(String scheduleidto,String[] fromagendaids,String userid) throws Exception {
		logger.info(new Date() +"Inside SERVICE CommitteePreviousAgendaAdd ");
		long count=0;
		for(int i=0;i<fromagendaids.length;i++)
		{			
			Object[] scheduletodata = dao.CommitteeScheduleEditData(scheduleidto);
			CommitteeScheduleAgenda scheduleagendafrom=dao.CommitteePreviousAgendaGet(fromagendaids[i]);
			CommitteeScheduleAgenda scheduleagendato=new CommitteeScheduleAgenda();			
			scheduleagendato.setScheduleId(Long.parseLong(scheduleidto));
			scheduleagendato.setScheduleSubId(scheduleagendafrom.getScheduleSubId());
			scheduleagendato.setAgendaItem(scheduleagendafrom.getAgendaItem());
			scheduleagendato.setPresentorLabCode(scheduleagendafrom.getPresentorLabCode());
			scheduleagendato.setPresenterId(scheduleagendafrom.getPresenterId());
			scheduleagendato.setDuration(scheduleagendafrom.getDuration());
			scheduleagendato.setProjectId(Long.parseLong(scheduletodata[9].toString()));
			scheduleagendato.setRemarks(scheduleagendafrom.getRemarks());
			scheduleagendato.setCreatedBy(userid);
			scheduleagendato.setCreatedDate(sdf1.format(new Date()));
			
			scheduleagendato.setIsActive(1);

			List<Object[]> agendapriority=dao.CommitteeScheduleAgendaPriority(scheduleidto);
			
			if(agendapriority.size()==0)
			{
				scheduleagendato.setAgendaPriority(1);
			}
			else if((Integer)agendapriority.get(0)[2]==0)
			{
				scheduleagendato.setAgendaPriority(1);
			}else {
				scheduleagendato.setAgendaPriority(((Integer)agendapriority.get(0)[2])+1);	
			}
			
			
			count=dao.CommitteeAgendaSubmit(scheduleagendato);
		}		
		return count;
	}

	@Override
	public int ScheduleMinutesUnitUpdate(String UnitId, String Unit, String UserId) throws Exception {
		
		String dt=sdf3.format(new Date());
		return dao.ScheduleMinutesUnitUpdate(UnitId, Unit, UserId,dt);
	}
	
	@Override
	public List<Object[]> divisionList() throws Exception {
		return dao.divisionList();
		
	}
	
	
	@Override
	public List<Object[]> LoginDivisionList(String empid) throws Exception {
		return dao.LoginDivisionList(empid);
	}
	
	@Override
	public List<Object[]> CommitteedivisionAssigned(String divisionid) throws Exception
	{
		return dao.CommitteedivisionAssigned(divisionid);
		
	}
	
	@Override
	public List<Object[]> CommitteedivisionNotAssigned(String divisionid, String LabCode ) throws Exception
	{
		return dao.CommitteedivisionNotAssigned(divisionid,  LabCode );
	}
	
	@Override
	public long DivisionCommitteeAdd(String divisionid, String[] Committee, String UserId) throws Exception 
	{
		logger.info(new Date() +"Inside SERVICE DivisionCommitteeAdd ");
		long count=0;
		for(int i=0;i<Committee.length;i++) {
			Object[] committeedata=dao.CommitteeDetails(Committee[i]);
			CommitteeDivision committeeproject=new CommitteeDivision();
			committeeproject.setDivisionId(Long.parseLong(divisionid));
			committeeproject.setCommitteeId(Long.parseLong(Committee[i]));
			committeeproject.setCreatedBy(UserId);
			committeeproject.setCreatedDate(sdf1.format(new Date()));
			committeeproject.setDescription(committeedata[10].toString());
			committeeproject.setTermsOfReference(committeedata[11].toString());
			count=dao.DivisionCommitteeAdd(committeeproject);
		}
				
		return count;
	}
	
	
	@Override
	public List<Object[]> DivisionCommitteeFormationCheckList(String divisionid) throws Exception
	{
		return dao.DivisionCommitteeFormationCheckList(divisionid);
	}
	
	@Override
	public long DivisionCommitteeDelete( String[] CommitteeProject,String user) throws Exception {

		logger.info(new Date() +"Inside SERVICE DivisionCommitteeDelete ");
		long count=0;
		for(int i=0;i<CommitteeProject.length;i++) {
			CommitteeDivision committeedivision=new CommitteeDivision();
			committeedivision.setCommitteeDivisionId(Long.parseLong(CommitteeProject[i]));		
			count=dao.DivisionCommitteeDelete(committeedivision);
			
		}				
		return count;
		
	}
	
	@Override
	public  Object[] DivisionData(String divisionid) throws Exception
	{
		return dao.DivisionData(divisionid);
	}
	
	
	@Override
	public List<Object[]> DivisionScheduleListAll(String divisionid) throws Exception
	{
		return dao.DivisionScheduleListAll(divisionid);
	}
	
	@Override
	public List<Object[]> DivisionCommitteeScheduleList(String divisionid,String committeeid) throws Exception
	{
		return dao.DivisionCommitteeScheduleList(divisionid, committeeid);
	}
	
	
	@Override
	public List<Object[]> DivisionCommitteeMainList(String divisionid) throws Exception 
	{
		return dao.DivisionCommitteeMainList(divisionid);
	}
	
	
	@Override
	public List<Object[]> DivisionMasterList(String divisionid) throws Exception 
	{
		return dao.DivisionMasterList(divisionid);
	}
		
	@Override
	public List<Object[]> DivCommitteeAutoScheduleList(String divisionid) throws Exception 
	{		
		return dao.DivCommitteeAutoScheduleList(divisionid);
	}

	@Override
	public List<Object[]> CommitteeActionList(String EmpId) throws Exception {
		return dao.CommitteeActionList(EmpId);
	}
	
	
	@Override
	public int CommitteeDivisionUpdate(String divisionid, String CommitteeId) throws Exception 
	{
		return dao.CommitteeDivisionUpdate(divisionid, CommitteeId);
	}

	@Override
	public List<Object[]> MinutesOutcomeList() throws Exception {
		
		return dao.MinutesOutcomeList();
	}

		
	
	@Override
	public List<Object[]> InitiatedProjectDetailsList()throws Exception
	{
		return dao.InitiatedProjectDetailsList();
	}

	@Override
	public List<Object[]> InitiationMasterList(String initiationid) throws Exception {
		return dao.InitiationMasterList(initiationid);
	}
	
	@Override
	public List<Object[]> InitiationCommitteeFormationCheckList(String initiationid) throws Exception
	{
		return dao.InitiationCommitteeFormationCheckList(initiationid);
	}

	@Override
	public List<Object[]> InitiationCommitteesListNotAdded(String initiationid,String LabCode) throws Exception 
	{
		return dao.InitiationCommitteesListNotAdded(initiationid, LabCode);
	}
	
	@Override
	public Long InvitationSerialnoUpdate(String[] newslno,String[] invitationid) throws Exception
	{
		logger.info(new Date() +"Inside SERVICE InvitationSerialnoUpdate ");
			long ret=0;
			for(int i=0;i<invitationid.length;i++)
			{
				dao.InvitationSerialnoUpdate(invitationid[i],newslno[i]);
			}
			return ret;
	
	}
	
	@Override
	public List<Object[]> CommitteeRepList() throws Exception
	{
		return dao.CommitteeRepList();
	}
	
	@Override
	public List<Object[]> CommitteeMemberRepList(String committeemainid) throws Exception
	{
		return dao.CommitteeMemberRepList(committeemainid);
	}
	
	@Override
	public List<Object[]> CommitteeRepNotAddedList(String committeemainid) throws Exception
	{
		return dao.CommitteeRepNotAddedList(committeemainid);
	}
	
	@Override
	public long CommitteeRepMemberAdd(String[] repids, String committeemainid, String createdby ) throws Exception
	{
		logger.info(new Date() +"Inside SERVICE CommitteeRepMemberAdd ");
		long count=0;
		for(int i=0;i<repids.length;i++ ) 
		{
			CommitteeMemberRep repmems=new CommitteeMemberRep();
			repmems.setCommitteeMainId(Long.parseLong(committeemainid));
			repmems.setRepId(Integer.parseInt(repids[i]));
			repmems.setCreatedBy(createdby);
			repmems.setCreatedDate(sdf1.format(new Date()));
			repmems.setIsActive(1);			
			count=dao.CommitteeRepMembersSubmit(repmems);
		}
		
		return count;
	}
	
	@Override
	public int CommitteeMemberRepDelete(String memberrepid) throws Exception
	{
		return dao.CommitteeMemberRepDelete(memberrepid);
		
	}
	
	
	@Override
	public List<Object[]> CommitteeAllMembersList(String committeemainid) throws Exception
	{
		return dao.CommitteeAllMembersList(committeemainid);		
	}
	
	@Override
	public int CommitteeMemberUpdate(CommitteeMember model) throws Exception {
		logger.info(new Date() +"Inside SERVICE CommitteeMemberUpdate ");
		model.setModifiedDate(sdf1.format(new Date()));
		return dao.CommitteeMemberUpdate(model);		
	}
	
	@Override
	public int CommitteeMainMemberUpdate(CommitteeMembersEditDto dto,CommitteeMainDto cmd) throws Exception {	
		logger.info(new Date() +"Inside SERVICE CommitteeMainMemberUpdate ");
		
		int ret=0;
		// Update Chairperson
		CommitteeMainDto cmdd=new CommitteeMainDto();
		
		cmdd.setCommitteeMainId(dto.getCommitteemainid());
		
		cmdd.setReferenceNo(cmd.getReferenceNo());
		cmdd.setFormationDate(cmd.getFormationDate());
		int count=dao.ReformationDate(cmdd);
		
		
		CommitteeMember model=new CommitteeMember();
		model.setModifiedBy(dto.getModifiedBy());
		model.setModifiedDate(sdf1.format(new Date()));
		model.setCommitteeMemberId(Long.parseLong(dto.getChairpersonmemid()));
		model.setLabCode(dto.getCpLabCode());
		
		model.setEmpId(Long.parseLong(dto.getChairperson()));
		ret=dao.CommitteeMemberUpdate(model);		
				
		// Update or Remove or add  co-chairperson based on update
		model=new CommitteeMember();
		
		if(dto.getComemberid()!=null && dto.getCo_chairperson()!=null && Long.parseLong(dto.getCo_chairperson())==0)
		{
			model.setCommitteeMemberId(Long.parseLong(dto.getComemberid()));
			model.setModifiedBy(dto.getModifiedBy());
			model.setModifiedDate(sdf1.format(new Date()));
			dao.CommitteeMemberDelete(model);
		}
		else if(dto.getComemberid()!=null && Long.parseLong(dto.getCo_chairperson())>0)
		{
			model.setCommitteeMemberId(Long.parseLong(dto.getComemberid()));
			model.setLabCode(cmd.getCcplabocode());
			model.setEmpId(Long.parseLong(dto.getCo_chairperson()));
			model.setModifiedBy(dto.getModifiedBy());
			model.setModifiedDate(sdf1.format(new Date()));
			ret=dao.CommitteeMemberUpdate(model);
		}
		else if(dto.getComemberid()==null && dto.getCo_chairperson()!=null && !dto.getCo_chairperson().isEmpty() && Long.parseLong(dto.getCo_chairperson())>0)
		{
				CommitteeMember newmodel=new CommitteeMember();
				newmodel.setLabCode(cmd.getCcplabocode());
				newmodel.setEmpId(Long.parseLong(dto.getCo_chairperson()));
				newmodel.setMemberType("CH");
				newmodel.setCommitteeMainId(Long.parseLong(dto.getCommitteemainid()));
				newmodel.setCreatedBy(dto.getModifiedBy());
				newmodel.setCreatedDate(sdf1.format(new Date()));
				newmodel.setIsActive(1);
				dao.CommitteeMainMembersAdd(newmodel);
		}
			
		
		if(dto.getComemberid()!=null && Long.parseLong(dto.getCo_chairperson())==0)
		{
			model.setCommitteeMemberId(Long.parseLong(dto.getComemberid()));
			dao.CommitteeMemberDelete(model);
		}
		
		
		// Update Member Secretary
		model=new CommitteeMember();
		model.setModifiedBy(dto.getModifiedBy());
		model.setModifiedDate(sdf1.format(new Date()));
		model.setCommitteeMemberId(Long.parseLong(dto.getSecretarymemid()));
		model.setLabCode(dto.getMsLabCode());
		model.setEmpId(Long.parseLong(dto.getSecretary()));
		ret=dao.CommitteeMemberUpdate(model); 
		
		
		// Update, remove or add Proxy member Secretary based on update
		model=new CommitteeMember();
		
		if(dto.getProxysecretarymemid()!=null && Long.parseLong(dto.getProxysecretary())==0)
		{
			model.setCommitteeMemberId(Long.parseLong(dto.getProxysecretarymemid()));
			model.setModifiedBy(dto.getModifiedBy());
			model.setModifiedDate(sdf1.format(new Date()));
			dao.CommitteeMemberDelete(model);
		}
		else if(dto.getProxysecretarymemid()!=null && Long.parseLong(dto.getProxysecretary())>0)
		{
			model.setCommitteeMemberId(Long.parseLong(dto.getProxysecretarymemid()));
			model.setLabCode(dto.getSesLabCode());
			model.setModifiedBy(dto.getModifiedBy());
			model.setModifiedDate(sdf1.format(new Date()));
			model.setEmpId(Long.parseLong(dto.getProxysecretary()));
			
			ret=dao.CommitteeMemberUpdate(model);
		}
		else if(dto.getProxysecretarymemid()==null && Long.parseLong(dto.getProxysecretary())>0)
		{
				CommitteeMember newmodel=new CommitteeMember();
				newmodel.setLabCode(dto.getSesLabCode());
				newmodel.setEmpId(Long.parseLong(dto.getProxysecretary()));
				newmodel.setMemberType("PS");
				newmodel.setCommitteeMainId(Long.parseLong(dto.getCommitteemainid()));
				newmodel.setCreatedBy(dto.getModifiedBy());
				newmodel.setCreatedDate(sdf1.format(new Date()));
				newmodel.setIsActive(1);
				dao.CommitteeMainMembersAdd(newmodel);
		}
			
		
		
		
		return ret;
		
	}
	
	
	@Override
	public Object[] CommitteMainData(String committeemainid) throws Exception 
	{
		return dao.CommitteMainData(committeemainid);
	}
	
	@Override
	public long InitiationCommitteeDelete( String[] CommitteeProject,String user) throws Exception {

		logger.info(new Date() +"Inside SERVICE InitiationCommitteeDelete ");
		long count=0;
		for(int i=0;i<CommitteeProject.length;i++) {
			CommitteeInitiation model=new CommitteeInitiation();
			model.setCommitteeInitiationId(Long.parseLong(CommitteeProject[i]));		
			count=dao.InitiationCommitteeDelete(model);
		}				
		return count;
		
	}
	@Override
	public Object[] Initiationdetails(String initiationid) throws Exception
	{	
		return dao.Initiationdetails(initiationid);

	}
	
	@Override
	public Object[] InitiationCommitteeDescriptionTOR(String initiationid,String Committeeid) throws Exception
	{	
		return dao.InitiationCommitteeDescriptionTOR(initiationid, Committeeid);

	}
	
	@Override
	public int InitiationCommitteeDescriptionTOREdit(CommitteeInitiation committeeinitiation) throws Exception
	{	
		committeeinitiation.setModifiedDate(sdf1.format(new Date()));
		return dao.InitiationCommitteeDescriptionTOREdit(committeeinitiation);
	}
	
	@Override
	public List<Object[]> InitiaitionMasterList(String initiationid) throws Exception 
	{
		return dao.InitiaitionMasterList(initiationid);
	}
	@Override
	public int CommitteeInitiationUpdate(String initiationid, String CommitteeId) throws Exception 
	{
		return dao.CommitteeInitiationUpdate(initiationid,CommitteeId);
	}
	
	@Override
	public List<Object[]> InitiationCommitteeMainList(String initiationid) throws Exception 
	{
		return dao.InitiationCommitteeMainList(initiationid);
	}
	@Override
	public List<Object[]> InitiationScheduleListAll(String initiationid) throws Exception
	{
		return dao.InitiationScheduleListAll(initiationid);
	}
	
	@Override
	public List<Object[]> InitiationCommitteeScheduleList(String initiationid,String committeeid) throws Exception
	{
		return dao.InitiationCommitteeScheduleList(initiationid, committeeid);
	}
	@Override
	public Object[] ProposedCommitteeMainId(String committeemainid) throws Exception
	{
		return dao.ProposedCommitteeMainId(committeemainid);
	}
	
	@Override
	public Object[] GetProposedCommitteeMainId(String committeeid,String projectid,String divisionid,String initiationid) throws Exception
	{
		return dao.GetProposedCommitteeMainId(committeeid, projectid, divisionid, initiationid);		
	}
	
	@Override
	public Object[] CommitteeMainApprovalData(String committeemainid) throws Exception
	{
		return dao.CommitteeMainApprovalData(committeemainid);
	}
	

	@Override
	public List<Object[]> ApprovalStatusList(String committeemainid ) throws Exception
	{
		return dao.ApprovalStatusList(committeemainid);
	}
	
	@Override
	public long CommitteeMainApprove(CommitteeConstitutionApprovalDto dto ) throws Exception
	{
		logger.info(new Date() +"Inside SERVICE CommitteeMainApprove ");
		
		Object[] CommitteeMain = dao.CommitteeMainDetails(dto.getCommitteeMainId());
		
		int ret=0;		
		String operation=dto.getOperation();
		Object[] oldapprovaldata=dao.CommitteeMainApprovalData(dto.getCommitteeMainId());
		String constatusid=oldapprovaldata[5].toString();
		String approvalauth=oldapprovaldata[6].toString();
		CommitteeConstitutionApproval approval=new CommitteeConstitutionApproval();		
		
		if(dto.getRemarks()==null || dto.getRemarks().equals(""))
		{
			dto.setRemarks("NIL");
		}
		
		approval.setCommitteeMainId(Long.parseLong(dto.getCommitteeMainId()));	
		approval.setActionBy(dto.getActionBy());
		approval.setActionDate(sdf1.format(new Date()));
		approval.setRemarks(dto.getRemarks());
		//approval.setEmpid(Long.parseLong(dto.getEmpid()));
		//approval.setEmpLabid(Long.parseLong(dto.getEmpLabid()));
		approval.setApprovalAuthority(dto.getApprovalAuthority());
		Object[] notificationemployee = null;
		String notificationempid="";
		if(constatusid.equals("CCR") ) 
		{
			if(operation.equalsIgnoreCase("approve"))
			{
				constatusid="CFW";
				notificationemployee=dao.CommitteeMainApprovalDoData(dto.getCommitteeMainId());
				if(notificationemployee!=null) {
					notificationempid=	notificationemployee[0].toString();
				}
				
			}
		}
		else if(constatusid.equals("CFW")) 
		{
			if(operation.equalsIgnoreCase("approve"))
			{
				constatusid="RDO";
				notificationemployee=dao.DoRtmdAdEmpData();
				if(notificationemployee!=null) {
					notificationempid=	notificationemployee[0].toString();
				}
								
			}
			else if(operation.equalsIgnoreCase("return"))
			{
				constatusid="RTDO";	
				notificationemployee=dao.ComConstitutionEmpdetails(dto.getCommitteeMainId());
				if(notificationemployee!=null) {
					notificationempid=	notificationemployee[0].toString();
				}
			}
		}
		else if(constatusid.equals("RDO")) 
		{
			if(operation.equalsIgnoreCase("approve"))
			{
				constatusid="RDT";				
				notificationemployee=dao.DirectorEmpData(CommitteeMain[3].toString());
				if(notificationemployee!=null) {
					notificationempid=	notificationemployee[0].toString();
				}
			}
			else if(operation.equalsIgnoreCase("return"))
			{
				constatusid="RTR";
				notificationemployee=dao.CommitteeMainApprovalDoData(dto.getCommitteeMainId());
				if(notificationemployee!=null) {
					notificationempid=	notificationemployee[0].toString();
				}
			}
		}
		else if(constatusid.equals("RDT")) 
		{
			if(operation.equalsIgnoreCase("approve"))
			{
				constatusid="ADR";
				if(!approvalauth.equals(constatusid)) {
					constatusid="RDR";
				}
			}
			else if(operation.equalsIgnoreCase("return"))
			{
				constatusid="RTDR";
				notificationemployee=dao.DoRtmdAdEmpData();
				if(notificationemployee!=null) {
					notificationempid=	notificationemployee[0].toString();
				}
			}
		}
		else if(constatusid.equals("RDR")) 
		{
			if(operation.equalsIgnoreCase("approve"))
			{
				constatusid="ADG";
				if(!approvalauth.equals(constatusid)) {
					constatusid="RDG";
				}
			}
			else if(operation.equalsIgnoreCase("return"))
			{
				constatusid="RTDG";
			}
		}
		else if(constatusid.equals("RDG")) 
		{
			if(operation.equalsIgnoreCase("approve"))
			{
				constatusid="ASC";
				
			}
			else if(operation.equalsIgnoreCase("return"))
			{
				constatusid="RTSC";
			}
		}
		else if(constatusid.equals("RTSC")) 
		{
			if(operation.equalsIgnoreCase("approve"))
			{
				constatusid="RDG";
				
			}
			else if(operation.equalsIgnoreCase("return"))
			{
				constatusid="RTDG";
			}
		}
		
		else if(constatusid.equals("RTDG")) 
		{
			if(operation.equalsIgnoreCase("approve"))
			{
				constatusid="RDR";
				
			}
			else if(operation.equalsIgnoreCase("return"))
			{
				constatusid="RTDR";
				notificationemployee=dao.DoRtmdAdEmpData();
				if(notificationemployee!=null) {
					notificationempid=	notificationemployee[0].toString();
				}
			}
		}
		else if(constatusid.equals("RTDR")) 
		{
			if(operation.equalsIgnoreCase("approve"))
			{
				constatusid="RDT";
				/* dao.updatecommitteeapprovalauthority(approval); */
				notificationemployee=dao.DirectorEmpData(CommitteeMain[3].toString());
				if(notificationemployee!=null) {
					notificationempid=	notificationemployee[0].toString();
				}	
				
				
			}
			else if(operation.equalsIgnoreCase("return"))
			{
				constatusid="RTR";
				notificationemployee=dao.CommitteeMainApprovalDoData(dto.getCommitteeMainId());
				if(notificationemployee!=null) {
					notificationempid=	notificationemployee[0].toString();
				}	
			}
		}
		else if(constatusid.equals("RTR"))
		{
			if(operation.equalsIgnoreCase("approve"))
			{
				constatusid="RDO";
				notificationemployee=dao.DoRtmdAdEmpData();
				if(notificationemployee!=null) {
					notificationempid=	notificationemployee[0].toString();
				}			
			}
			else if(operation.equalsIgnoreCase("return"))
			{
				constatusid="RTDO";
				notificationempid=dao.ComConstitutionEmpdetails(dto.getCommitteeMainId())[0].toString();
			}
		}
		else if(constatusid.equals("RTDO")) 
		{
			if(operation.equalsIgnoreCase("approve"))
			{
				constatusid="CFW";
				notificationempid=dao.CommitteeMainApprovalDoData(dto.getCommitteeMainId())[0].toString();
				
			}			
		}		
			
		
		approval.setConstitutionStatus(constatusid);
		
		if((notificationempid==null || notificationempid.equals("") || Long.parseLong(notificationempid)==0 ) && !(approvalauth.equals(constatusid)) )
		{
			return -1;
		}
		if(constatusid.equalsIgnoreCase("RDT"))
		{
			dao.updatecommitteeapprovalauthority(approval);
		}
		CommitteeConstitutionHistory transaction=new CommitteeConstitutionHistory();
		transaction.setConstitutionApprovalId(Long.parseLong(oldapprovaldata[0].toString()));
		transaction.setCommitteeMainId(Long.parseLong(dto.getCommitteeMainId()));
		transaction.setRemarks(dto.getRemarks());
		transaction.setActionByLabid(Long.parseLong(dto.getEmpLabid()));
		transaction.setActionByEmpid(Long.parseLong(dto.getEmpid()));
		transaction.setActionDate(sdf1.format(new Date()));
		transaction.setConstitutionStatus(constatusid);
		dao.ConstitutionApprovalHistoryAdd(transaction);
		
		
		
		
		if(approvalauth.equals(constatusid))
		{
			Object[] committeemaindata=dao.CommitteMainData(dto.getCommitteeMainId());
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDate = LocalDate.parse(committeemaindata[6].toString());
			
			long lastcommitteeid=dao.LastCommitteeId(committeemaindata[1].toString(),committeemaindata[2].toString(),committeemaindata[3].toString(),committeemaindata[4].toString(),committeemaindata[13].toString(),committeemaindata[14].toString());	
			if(lastcommitteeid!=0)
			{
				CommitteeMain committeemain1= new CommitteeMain();
				committeemain1.setModifiedBy(dto.getActionBy());
				committeemain1.setModifiedDate(sdf1.format(new Date()));
				committeemain1.setValidTo(java.sql.Date.valueOf(fromDate.minusDays(1).toString()));
				committeemain1.setCommitteeMainId(lastcommitteeid);					
				dao.UpdateCommitteemainValidto(committeemain1);
			}
			dto.setActionDate(sdf1.format(new Date()));
			dao.NewCommitteeMainIsActiveUpdate(dto);
		}
		long schid=0;
		
		if(notificationempid!=null && !notificationempid.equals("")) 
		{
			PfmsNotification notification= new PfmsNotification();
			notification.setNotificationby(Long.parseLong(dto.getEmpid()));
			notification.setEmpId(Long.parseLong(notificationempid));
			Object[] ConStatusdetail=dao.CommitteeConStatusDetails(constatusid);
			notification.setNotificationMessage("Action Pending For Committee Constitution "+ConStatusdetail[0]);
			notification.setNotificationUrl("CommitteeMainApprovalList.htm");
			notification.setNotificationDate(sdf1.format(new Date()));
			notification.setCreatedBy(dto.getActionBy());
			notification.setCreatedDate(sdf1.format(new Date()));
			notification.setIsActive(1);
			notification.setScheduleId(schid);
			notification.setStatus("MAR");
			dao.MeetingMinutesApprovalNotification(notification);
		}
		ret=dao.CommitteeApprovalUpdate(approval);		
		return ret;
	}
	
	
	@Override
	public List<Object[]> ProposedCommitteList() throws Exception
	{
		return dao.ProposedCommitteList();
	}
	
	@Override
	public List<Object[]> ProposedCommitteesApprovalList(String loginid,String EmpId) throws Exception
	{
		logger.info(new Date() +"Inside SERVICE ProposedCommitteList ");
		
		List<Object[]> ProposedCommitteesApprovalList=new ArrayList<Object[]>(); 
		Object[] logindata=dao.LoginData(loginid);
		
		String logintype=logindata[4].toString();
		ProposedCommitteesApprovalList.addAll(dao.ProposedCommitteesApprovalList(logintype, EmpId));
		
		return ProposedCommitteesApprovalList;
	}
	
	@Override
	public List<Object[]>  ComConstitutionApprovalHistory(String committeemainid) throws Exception
	{
		return dao.ComConstitutionApprovalHistory(committeemainid);
	}
	
	
	@Override
	public List<Object[]>  ConstitutionApprovalFlow(String committeemainid) throws Exception
	{
		logger.info(new Date() +"Inside SERVICE ConstitutionApprovalFlow ");
		List<Object[]> list=new ArrayList<Object[]>();
		Object[] CommitteeMain = dao.CommitteeMainDetails(committeemainid);
		Object[] temp=dao.ComConstitutionEmpdetails(committeemainid);
		if(temp!=null) {
			list.add(temp);
		}
		temp=dao.CommitteeMainApprovalDoData(committeemainid);
		
		if(temp!=null) {
			list.add(temp);
		}
		list.add(dao.DoRtmdAdEmpData());
		
		temp=dao.DirectorEmpData(CommitteeMain[3].toString());
		if(temp!=null) {
			list.add(temp);
		}
		return list;
	}
	
	@Override
	public int  CommitteeMinutesDelete(String scheduleminutesid) throws Exception
	{
		return dao.CommitteeMinutesDelete(scheduleminutesid);
	}
	
	@Override
	public int  CommitteeScheduleDelete(CommitteeScheduleDto dto) throws Exception
	{
		logger.info(new Date() +"Inside SERVICE CommitteeScheduleDelete ");
		dto.setModifiedDate(sdf1.format(new Date()));
		dao.CommitteeScheduleAgendaDelete(dto);
		dao.CommitteeScheduleInvitationDelete(dto);
		return dao.CommitteeScheduleDelete(dto);
	}
	
	@Override
	public int  ScheduleCommitteeEmpCheck(EmpAccessCheckDto dto) throws Exception
	{
		logger.info(new Date() +"Inside SERVICE ScheduleCommitteeEmpCheck ");
		String logintype = dto.getLogintype();
		String scheduleid = dto.getScheduleid();
		String empid = dto.getEmpid();
		Object Committeemainid = dto.getCommitteemainid();
		int committeecons = dto.getCommitteecons();
		String confidential = dto.getConfidential();
		int useraccess=0;
		
		ArrayList<String> logintypes=new ArrayList<String>( Arrays.asList("A","Z","Y","E","H","C","I") );
		ArrayList<String> memtypes=new ArrayList<String>( Arrays.asList("CS","CC","PS") );
			if(logintypes.contains(logintype)) 
			{
				useraccess=2;
				return useraccess;
			}
			if( committeecons>0 )
			{
				List<Object[]> ScheduleCommEmpCheck = dao.ScheduleCommitteeEmpCheck(scheduleid, empid);
				if(ScheduleCommEmpCheck.size()>0)
				{
					useraccess=2;
					return useraccess;
				}
			}
			if( useraccess==0 )
			{
				List<Object[]> ScheduleCommEmpCheck = dao.ScheduleCommitteeEmpinvitedCheck(scheduleid, empid);
				
				if(ScheduleCommEmpCheck.size()>0)
				{
					if(confidential!=null && (Integer.parseInt(confidential)==1 || Integer.parseInt(confidential)==2)) 
					{
						if(memtypes.contains(ScheduleCommEmpCheck.get(0)[2].toString())) {
							useraccess=2;
							return useraccess;
						}
					}else
					{
						useraccess=2;
						return useraccess;
					}
				}
			}
			
			
		
		return useraccess;		
	}
	
	@Override
	public List<Object[]> EmpScheduleData(String empid,String scheduleid) throws Exception 
	{
		return dao.EmpScheduleData(empid, scheduleid);
	}
	
	@Override
	public List<Object[]> DefaultAgendaList(String committeeid,String LabCode) throws Exception 
	{
		return dao.DefaultAgendaList(committeeid,LabCode);
	}
	
	@Override
	public List<Object[]> ProcurementStatusList(String projectid)throws Exception{
		return dao.ProcurementStatusList(projectid);
	}
	private SimpleDateFormat sdf4=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");	
		String todayDate=outputFormat.format(new Date()).toString();
	@Override
	public List<Object[]> ActionPlanSixMonths(String projectid)throws Exception
	{
		
		logger.info(new Date()  +"Inside SERVICE ActionPlanThreeMonths ");
	
//			List<Object[]> main=dao.MilestoneActivityList(projectid);
//			List<Object[]> MilestoneActivityA=new ArrayList<Object[]>();
//			List<Object[]> MilestoneActivityB=new ArrayList<Object[]>();
//			List<Object[]> MilestoneActivityC=new ArrayList<Object[]>();
//			List<Object[]> MilestoneActivityD=new ArrayList<Object[]>();
//			List<Object[]> MilestoneActivityE=new ArrayList<Object[]>();
//			
//				for(Object[] objmain:main ) {
//					List<Object[]>  MilestoneActivityA1=dao.MilestoneActivityLevel(objmain[0].toString(),"1");
//					MilestoneActivityA.addAll(MilestoneActivityA1);
//					
//					for(Object[] obj:MilestoneActivityA1) {
//						List<Object[]>  MilestoneActivityB1=dao.MilestoneActivityLevel(obj[0].toString(),"2");
//						MilestoneActivityB.addAll(MilestoneActivityB1);
//						
//						for(Object[] obj1:MilestoneActivityB1) {
//							List<Object[]>  MilestoneActivityC1=dao.MilestoneActivityLevel(obj1[0].toString(),"3");
//							MilestoneActivityC.addAll(MilestoneActivityC1);
//							
//							for(Object[] obj2:MilestoneActivityC1) {
//								List<Object[]>  MilestoneActivityD1=dao.MilestoneActivityLevel(obj2[0].toString(),"4");
//								MilestoneActivityD.addAll( MilestoneActivityD1);
//								
//								for(Object[] obj3:MilestoneActivityD1) {
//									List<Object[]>  MilestoneActivityE1=dao.MilestoneActivityLevel(obj3[0].toString(),"5");
//									MilestoneActivityE.addAll( MilestoneActivityE1);
//								}
//							}
//						}
//					}
//				}
//				
//				List<Object[]> MilestoneActivityAll=new ArrayList<Object[]>();
//				MilestoneActivityAll.addAll(main);
//				MilestoneActivityAll.addAll(MilestoneActivityA);
//				MilestoneActivityAll.addAll(MilestoneActivityB);
//				MilestoneActivityAll.addAll(MilestoneActivityC);
//				MilestoneActivityAll.addAll(MilestoneActivityC);
//				MilestoneActivityAll.addAll(MilestoneActivityE);
		List<Object[]>mainList=dao.ActionPlanSixMonths(projectid);
		List<Object[]>subList=new ArrayList<>();
		List<Object[]>actionList= new ArrayList<>();
		if(mainList.size()!=0) {
			subList=mainList.stream().filter(i->i[33].toString().equalsIgnoreCase("Y")).collect(Collectors.toList());
		}
		LocalDate futureDay=LocalDate.parse(todayDate).plusDays(180);
		if(subList.size()!=0) {
		actionList=subList.stream().filter(i ->i[26].toString().equalsIgnoreCase("0")).collect(Collectors.toList());
		}
		return actionList;
		
		//	return dao.ActionPlanSixMonths(projectid);
		
	
	}
	
	@Override
	public Object[] ProjectDataDetails(String projectid) throws Exception 
	{
		return dao.ProjectDataDetails(projectid);
	}
	

	@Override 
	public List<Object[]> LastPMRCActions(long scheduleid,String committeeid,String proid,String isFrozen) throws Exception 
	{
		List<Object[]>lastPmrcActionList= new ArrayList<>();
		lastPmrcActionList=dao.LastPMRCActions(scheduleid, committeeid, proid, isFrozen);
		return dao.LastPMRCActions(scheduleid,committeeid,proid,isFrozen);
	}
	
	@Override
	public List<Object[]> CommitteeMinutesSpecNew()throws Exception
	{
		return dao.CommitteeMinutesSpecNew();
	}
	
	@Override 
	public List<Object[]> MilestoneSubsystems(String projectid) throws Exception {
		return dao.MilestoneSubsystems(projectid);
	}
	
	@Override 
	public List<Object[]> EmployeeScheduleReports(HttpServletRequest req,String empid,String rtype) throws Exception 
	{
		logger.info(new Date() +"Inside SERVICE EmployeeScheduleReports ");
		LocalDate fromdate=null;  
		LocalDate todate=null;
		String rtype1=rtype.toLowerCase();
		
			
		if(req.getParameter(rtype1+"fromdate")!=null && rtype.equalsIgnoreCase("D")) 
		{
			fromdate= todate =LocalDate.parse(sdf3.format(sdf.parse(req.getParameter((rtype1+"fromdate")))));
		}
		else if(req.getParameter(rtype1+"fromdate")!=null &&  rtype.equalsIgnoreCase("W"))
		{
			fromdate=LocalDate.parse(sdf3.format(sdf.parse(req.getParameter((rtype1+"fromdate")))));
			todate=LocalDate.parse(sdf3.format(sdf.parse(req.getParameter(rtype1+"todate"))));
		}
		else if(req.getParameter(rtype1+"fromdate")!=null &&  rtype.equalsIgnoreCase("M"))
		{
			
			String month = req.getParameter((rtype1+"fromdate")).split("-")[0];
			String year= req.getParameter((rtype1+"fromdate")).split("-")[1];
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
			fromdate = LocalDate.parse(month+" 01, "+year, formatter);
						
			todate = fromdate.plusMonths(1).withDayOfMonth(1).minusDays(1);
			
			ArrayList<LocalDate> monthdays = new ArrayList<LocalDate>();
			for(LocalDate fromdate1 = LocalDate.parse(fromdate.toString());fromdate1.isBefore(todate)|| fromdate1.isEqual(todate) ; fromdate1=fromdate1.plusDays(1)) 
			{
				monthdays.add(LocalDate.parse(fromdate1.toString()));
			}
			req.setAttribute("monthdays", monthdays);
									
		}
		else if(rtype.equalsIgnoreCase("D")) 
		{
			todate=fromdate=LocalDate.now();
		}
		else if(rtype.equalsIgnoreCase("W")) 
		{
			LocalDate today = LocalDate.now();			
		    // Go backward to get Monday
		    LocalDate monday = today;
		    while (monday.getDayOfWeek() != DayOfWeek.MONDAY)
		    {
		      monday = monday.minusDays(1);
		    }
		    // Go forward to get Sunday
		    LocalDate sunday = today;
		    while (sunday.getDayOfWeek() != DayOfWeek.SUNDAY)
		    {
		    	sunday = sunday.plusDays(1);
		    }
		    fromdate=monday.minusDays(1);
		    todate=sunday.minusDays(1);
		}
		else if(rtype.equalsIgnoreCase("M")) // month dates
		{
			fromdate = LocalDate.now().withDayOfMonth(1);
			todate = LocalDate.now().plusMonths(1).withDayOfMonth(1).minusDays(1);
			
			ArrayList<LocalDate> monthdays = new ArrayList<LocalDate>();
			for(LocalDate fromdate1 = LocalDate.parse(fromdate.toString());fromdate1.isBefore(todate)|| fromdate1.isEqual(todate) ; fromdate1=fromdate1.plusDays(1)) 
			{
				monthdays.add(LocalDate.parse(fromdate1.toString()));
			}
			req.setAttribute("monthdays", monthdays);
					
		}
		req.setAttribute("fromdate", fromdate);
		req.setAttribute("todate", todate);
		return dao.EmployeeScheduleReports(empid, fromdate.toString(), todate.toString());
	}
	
	@Override
	public List<Object[]> EmployeeDropdown(String empid,String logintype,String projectid)throws Exception
	{
		return dao.EmployeeDropdown(empid, logintype, projectid);
	}
	@Override
	public List<Object[]> FileRepMasterListAll(String projectid,String LabCode)throws Exception
	{
		return dao.FileRepMasterListAll(projectid,LabCode);
	}
	
	@Override
	public Object[] AgendaDocLinkDownload(String filerepid)throws Exception
	{
		return dao.AgendaDocLinkDownload(filerepid);
	}
	
	@Override
	public List<Object[]> AgendaLinkedDocList(String scheduleid) throws Exception {
		return dao.AgendaLinkedDocList(scheduleid);
	}
	
	@Override
	public int AgendaUnlinkDoc(CommitteeScheduleAgendaDocs agendadoc) throws Exception 
	{
		logger.info(new Date() +"Inside SERVICE AgendaUnlinkDoc ");
		agendadoc.setModifiedDate(sdf1.format(new Date()));
		
		if(dao.AgendaUnlinkDoc(agendadoc)!=null) {
			return 1;
		}else {
			return 0;
		}
		 
	}
	
	@Override
	public int PreDefAgendaEdit(CommitteeDefaultAgenda agenda) throws Exception 
	{
		agenda.setModifiedDate(sdf1.format(new Date()));
		return dao.PreDefAgendaEdit(agenda);
	}
	
	@Override
	public long PreDefAgendaAdd(CommitteeDefaultAgenda agenda) throws Exception 
	{
		agenda.setCreatedDate(sdf1.format(new Date()));
		return dao.PreDefAgendaAdd(agenda);
	}
	
	@Override
	public int PreDefAgendaDelete(String agendaid) throws Exception 
	{
		return dao.PreDefAgendaDelete(agendaid);
	}
	
	@Override
	public int MeetingNo(Object[] scheduledata) throws Exception 
	{
		logger.info(new Date() +"Inside SERVICE MeetingNo ");
		int count=0;
		if(scheduledata[21].toString().equalsIgnoreCase("P")) {
			String scheduledate = scheduledata[2].toString(); 
			String projectid = scheduledata[9].toString();
			String committeeid = scheduledata[0].toString();
			count= dao.CommProScheduleList(projectid, committeeid,scheduledate);
		}
		return count;
	}

	@Override
	public long insertMinutesFinance(MinutesFinanceList finance) throws Exception {
		finance.setCreatedDate(sdf1.format(new Date()));
		return dao.insertMinutesFinance(finance);
	}

	@Override
	public long getLastPmrcId(String projectid, String committeeid, String scheduleId) throws Exception {
		
		return dao.getLastPmrcId(projectid, committeeid, scheduleId);
	}

	@Override
	public int updateMinutesFrozen(String schduleid) throws Exception {
		
		return dao.updateMinutesFrozen(schduleid);
	}

	@Override
	public List<ProjectFinancialDetails> getMinutesFinance(String scheduleid) throws Exception {
		logger.info(new Date() +"Inside SERVICE getMinutesFinance ");
		List<ProjectFinancialDetails> finlist=new ArrayList<ProjectFinancialDetails>();
		for(MinutesFinanceList list:dao.getMinutesFinance(scheduleid)) {
			ProjectFinancialDetails finance=new ProjectFinancialDetails();
			finance.setBudgetHeadDescription(list.getBudgetHeadDescription());
			finance.setBudgetHeadId(list.getBudgetHeadId());
			finance.setFeBalance(list.getFeBalance());
			finance.setFeDipl(list.getFeDipl());
			finance.setFeExpenditure(list.getFeExpenditure());
			finance.setFeOutCommitment(list.getFeOutCommitment());
            finance.setFeSanction(list.getFeSanction());
            finance.setProjectId(list.getProjectId());
            finance.setReBalance(list.getReBalance());
            finance.setReDipl(list.getReDipl());
            finance.setReExpenditure(list.getFeExpenditure());
            finance.setReOutCommitment(list.getReOutCommitment());
	        finance.setReSanction(list.getReSanction());
	        finance.setTotalSanction(list.getTotalSanction());
            finlist.add(finance);
		}
		
		return finlist;
	}

	@Override
	public List<Object[]> ClusterList() throws Exception {
		
		return dao.ClusterList();
	}
	
	@Override
	public Object[] getDefaultAgendasCount(String committeeId, String LabCode) throws Exception
	{
		return dao.getDefaultAgendasCount(committeeId, LabCode);
	}
	
	@Override
	public LabMaster LabDetailes(String LabCode) throws Exception {
		return dao.LabDetailes(LabCode);
	}
	
	@Override
	public long FreezeDPFMMinutes(CommitteeMeetingDPFMFrozen dpfm) throws Exception 
	{
		String meedtingId = dpfm.getMeetingId().replaceAll("[&.:?|<>/]", "").replace("\\", "") ;
		String LabCode = dpfm.getLabCode();
//		String filepath = "\\"+LabCode.toUpperCase().trim()+"\\DPFM\\";
		Path freezepath = Paths.get(uploadpath, LabCode.toUpperCase().trim(), "DPFM");
		Path freezepath1 = Paths.get(LabCode.toUpperCase().trim(), "DPFM");
		int count=0;
		String filename = "DPFM-"+meedtingId;
		Path oldpath = freezepath.resolve(filename+".pdf");
		while(oldpath.toFile().exists())
		{
			filename = filename+" ("+ ++count+")";
		}
		File file = dpfm.getDpfmfile();
		saveFileFromFileObject(freezepath ,filename+".pdf" ,file );
		
		dpfm.setDPFMFileName(filename+".pdf");
		dpfm.setFrozenDPFMPath(freezepath1.toString());
		dpfm.setFreezeTime(sdf1.format(new Date()));
		return dao.FreezeDPFMMinutesAdd(dpfm);
	}
	
	public static int saveFile1(Path uploadPath, String fileName, MultipartFile multipartFile) throws IOException {
		logger.info(new Date() + "Inside SERVICE saveFile ");
		int result = 1;

		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}
		try (InputStream inputStream = multipartFile.getInputStream()) {
			Path filePath = uploadPath.resolve(fileName);
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ioe) {
			result = 0;
			throw new IOException("Could not save image file: " + fileName, ioe);
		} catch (Exception e) {
			result = 0;
			logger.error(new Date() + "Inside SERVICE saveFile " + e);
			e.printStackTrace();
		}
		return result;
	}
	
	public static int saveFileFromFileObject(Path uploadPath, String fileName, File file) throws IOException {
	    logger.info(new Date() + " Inside SERVICE saveFileFromFileObject ");
	    int result = 1;

	    // Check if the directory exists; if not, create it
	    if (!Files.exists(uploadPath)) {
	        Files.createDirectories(uploadPath);
	    }

	    try {
	        // Resolve the destination file path
	        Path filePath = uploadPath.resolve(fileName);
	        
	        // Copy the file from the source to the destination
	        Files.copy(file.toPath(), filePath, StandardCopyOption.REPLACE_EXISTING);
	        
	    } catch (IOException ioe) {
	        result = 0;
	        throw new IOException("Could not save file: " + fileName, ioe);
	    } catch (Exception e) {
	        result = 0;
	        logger.error(new Date() + " Inside SERVICE saveFileFromFileObject " + e);
	        e.printStackTrace();
	    }
	    
	    return result;
	}

	
	public void saveFile(String uploadpath, String fileName, File fileToSave) throws IOException 
	{
	   logger.info(new Date() +"Inside SERVICE saveFile ");
	   Path uploadPath = Paths.get(uploadpath);
	          
	   if (!Files.exists(uploadPath)) {
		   Files.createDirectories(uploadPath);
	   }
	        
	   try (InputStream inputStream = new FileInputStream(fileToSave)) {
		   Path filePath = uploadPath.resolve(fileName);
	       Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
	   } catch (IOException ioe) {       
		   throw new IOException("Could not save file: " + fileName, ioe);
	   }     
	}
	
	@Override
	public CommitteeMeetingDPFMFrozen getFrozenDPFMMinutes(String scheduleId)throws Exception
	{
		return dao.getFrozenDPFMMinutes(scheduleId);
	}
	
	
	
@Override
	public CommitteeProjectBriefingFrozen getFrozenCommitteeMOM(String committeescheduleid) throws Exception {
		
		return dao.getFrozenCommitteeMOM(committeescheduleid);
	}
	@Override
	public List<Object[]> totalProjectMilestones(String projectid) throws Exception {
		return dao.totalProjectMilestones(projectid);
	}
	
	@Override
	public long doMomFreezing(CommitteeProjectBriefingFrozen briefing) throws Exception {
	
		Object[] scheduledata = dao.CommitteeScheduleEditData(String.valueOf(briefing.getScheduleId()));
		String meedtingId = scheduledata[11].toString().replaceAll("[&.:?|<>/]", "").replace("\\", "") ;
		String LabCode = briefing.getLabCode();
		String filepath = "\\"+LabCode.toUpperCase().trim()+"\\Briefing\\";
		int count=0;
		String filename = "Briefing-"+meedtingId;
		while(new File(uploadpath+filepath+"\\"+filename+".pdf").exists())
		{
			filename = "Briefing-"+meedtingId;
			filename = filename+" ("+ ++count+")";
		}
	
		
		while(new File(uploadpath+filepath+"\\"+filename+".pdf").exists())
		{
			filename = "Briefing-"+meedtingId;
			filename = filename+" ("+ ++count+")";
		}
		File file = briefing.getMomFile();
		saveFile(uploadpath+filepath ,filename+".pdf" ,file );
		
		briefing.setMoM(filename+"-Mom"+".pdf");
		
		briefing.setFrozenBriefingPath(filepath);
		briefing.setFreezeTime(fc.getSqlDateAndTimeFormat().format(new Date()));
		return dao.FreezeBriefingAdd(briefing);
		
		
	}
	
	@Override
	public List<Object[]> getEnvisagedDemandList(String projectid) throws Exception {
		return dao.getEnvisagedDemandList(projectid);
	}
	@Override
	public int MomFreezingUpdate(String committeescheduleid) throws Exception {
		
		return dao.MomFreezingUpdate(committeescheduleid);
	}
	
	@Override
	public List<Object[]> getTodaysMeetings(String date) throws Exception {
		
		return dao.getTodaysMeetings(date);
	}
	
@Override
public List<Object[]> actionDetailsForNonProject(String committeeId, String scheduledate) throws Exception {
	// TODO Auto-generated method stub
	
	List<Object[]>actionDetails=dao.actionDetailsForNonProject(committeeId);
	return actionDetails;
}

@Override
public List<Object[]> CommitteeOthersList(String projectid, String divisionid, String initiationid,String projectstatus) throws Exception {
	
	return dao.CommitteeOthersList(projectid,divisionid,initiationid,projectstatus);
}

@Override
public Long UpdateMomAttach(Long scheduleId) throws Exception {
	// TODO Auto-generated method stub
	return dao.UpdateMomAttach(scheduleId);
}

	@Override
	public Long MomAttach(CommitteeMomAttachment cm,String LabCode) throws Exception {
		// TODO Auto-generated method stub
		
//	String Path = LabCode +"\\MinutesAttachment\\";
		Path filepath = Paths.get(uploadpath,LabCode, "MinutesAttachment");
		Path filepath1 = Paths.get(LabCode, "MinutesAttachment");
		
		if(!cm.getFile().isEmpty()) {
			cm.setAttachmentName(cm.getFile().getOriginalFilename());
			saveFile1(filepath,cm.getAttachmentName(),cm.getFile());
		}
		cm.setIsActive(1);
		cm.setFilePath(filepath1.toString());
		return dao.MomAttach(cm);
	}
	@Override
	public Object[] MomAttachmentFile(String committeescheduleid) throws Exception {
		return dao.MomAttachmentFile(committeescheduleid);
	}
	
	@Override
	public List<Object[]> MomReportList(String projectId, String committeeId) throws Exception {
		return dao.MomReportList(projectId,committeeId);
	}

	// Prudhvi 27/03/2024
	/* ------------------ start ----------------------- */
	@Override
	public List<Object[]> IndustryPartnerRepListInvitationsMainMembers(String industryPartnerId, String committeemainid) throws Exception {
		
		return dao.IndustryPartnerRepListInvitationsMainMembers(industryPartnerId, committeemainid);
	}
	/* ------------------ end ----------------------- */
	@Override
	public List<Object[]> ConstitutionApprovalFlowData(String committeemainid) throws Exception {
		return dao.ConstitutionApprovalFlowData(committeemainid);
	}
	
	@Override
	public int MemberSerialNoUpdate(String[] newslno, String[] memberId) throws Exception {
		
		int ret=0;
		for(int i=0;i<memberId.length;i++)
		{
			dao.MemberSerialNoUpdate(memberId[i],newslno[i]);
		}
		return ret;
	}
	@Override
	public long saveCommitteeLetter(CommitteeLetter committeeLetter) throws Exception {
		
		String LabCode= committeeLetter.getLabCode();
		Timestamp instant= Timestamp.from(Instant.now());
		String timestampstr = instant.toString().replace(" ","").replace(":", "").replace("-", "").replace(".","");
		
	//	String Path = LabCode+"\\CommitteeInvitationLetter\\";
		Path filepath = Paths.get(uploadpath, LabCode, "CommitteeInvitationLetter");
		Path filepath1 = Paths.get(LabCode, "CommitteeInvitationLetter");
		
		committeeLetter.setFilePath(filepath1.toString());
		committeeLetter.setAttachmentName(committeeLetter.getLetter().getOriginalFilename());
		committeeLetter.setCreatedBy(committeeLetter.getCreatedBy());
		committeeLetter.setCreatedDate(sdf1.format(new Date()));		
		committeeLetter.setIsActive(1);
		long count=0;
		if(!committeeLetter.getLetter().isEmpty()) {
			committeeLetter.setAttachmentName(committeeLetter.getLetter().getOriginalFilename());
			saveFile1(filepath,committeeLetter.getAttachmentName(),committeeLetter.getLetter());
			count=dao.saveCommitteeLetter(committeeLetter);
		}
		
		
		return count;
	}
	
	@Override
	public List<Object[]> getcommitteLetters(String commmitteeId, String projectId, String divisionId,
			String initiationId) throws Exception {
	
		return dao.getcommitteLetters(commmitteeId,projectId,divisionId,initiationId);
	}
	@Override
	public Object[] getcommitteeLetter(String letterId) throws Exception {
		return dao.getcommitteeLetter(letterId);
	}
	@Override
	public long UpdateCommitteLetter(String letterId) throws Exception {
		return dao.UpdateCommitteLetter(letterId);
	}
	
	//prakarsh
	@Override
	public List<Object[]> MeettingList(String projectId, String committeeId) throws Exception {
		
		return dao.MeettingList(projectId,committeeId);
	}

	@Override
	public List<Object[]> MeettingList(String projectid) {
		// TODO Auto-generated method stub
		return dao.MeettingList(projectid);
	}
	@Override
	public List<Object[]> SpecialEmployeeListInvitations(String labCode,String scheduleid) throws Exception {
		return dao.SpecialEmployeeListInvitations(labCode,scheduleid);
	}
	
	@Override
	public List<Object[]> allconstitutionapprovalflowData(String committeemainid) throws Exception {
		// TODO Auto-generated method stub
		return dao.allconstitutionapprovalflowData(committeemainid);
	}
	@Override
	public Object[] CommitteMainEnoteList(String committeemainid,String ScheduleId) throws Exception {
	
		return dao.CommitteMainEnoteList(committeemainid,ScheduleId);
	}
	@Override
	public PmsEnote getPmsEnote(String EnoteId) throws Exception {
		return dao.getPmsEnote(EnoteId);
	}
	@Override
	public long addPmsEnote(PmsEnote pe) throws Exception {
		return 	dao.addPmsEnote(pe);
		
	}
	
	@Override
	public long EnoteForward(PmsEnote pe, String remarks, Long empId, String flow,String Username) throws Exception {
		
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
			//upadte committee Main
			CommitteeConstitutionApprovalDto dto = new CommitteeConstitutionApprovalDto();
			dto.setCommitteeMainId(pe.getCommitteeMainId()+"");
			dto.setActionBy(Username);
			dto.setActionDate(sdf1.format(new Date()));
			dao.NewCommitteeMainIsActiveUpdate(dto);
		}
		else if(forwardstatus.contains(EnoteStatusCode)) {
			EnoteStatusCode="FWD";
			EnoteStatusCodeNext="RC1";
			pe.setEnoteStatusCode(EnoteStatusCode);
			pe.setEnoteStatusCodeNext(EnoteStatusCodeNext);
			if(pe.getInitiatedBy()==pe.getRecommend1()) {//If both the person is same
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
		}else {
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
		
		pe.setModifiedBy(Username);
		pe.setModifiedDate(sdf1.format(new Date()));
		
		dao.addPmsEnote(pe);
		if(pe.getEnoteFrom().equalsIgnoreCase("C")&&  EnoteStatusCodeNext.equalsIgnoreCase("APR") && !pe.getSessionLabCode().equalsIgnoreCase(pe.getApprovingOfficerLabCode())) {
			
			pe.setEnoteStatusCode("APR");
			pe.setEnoteStatusCodeNext("APR");
			pe.setModifiedBy(Username);
			pe.setModifiedDate(sdf1.format(new Date()));
			dao.addPmsEnote(pe);
			//update committe_Main
			CommitteeConstitutionApprovalDto dto = new CommitteeConstitutionApprovalDto();
			dto.setCommitteeMainId(pe.getCommitteeMainId()+"");
			dto.setActionBy(Username);
			dto.setActionDate(sdf1.format(new Date()));
			dao.NewCommitteeMainIsActiveUpdate(dto);;
		}
		
		
		PmsEnoteTransaction transaction= PmsEnoteTransaction.builder()
											.EnoteId(ENoteId)
											.EnoteStatusCode(EnoteStatusCode)
											.Remarks(remarks)
											.EnoteFrom(pe.getEnoteFrom())
											.ActionBy(empId)
											.ActionDate(sdf1.format(new Date()))
											.build();
		
		long result =dao.addEnoteTrasaction(transaction);
		
		String NotificationBy= admindao.EmployeeData(empId.toString())[3].toString();
		
		String msg="";
		String url ="";
		String revurl ="";
		String lastUrl ="";
		if(pe.getEnoteFrom().equalsIgnoreCase("C")) {
			msg="Committee with RefNo "+pe.getRefNo()+",";
			url ="CommitteeApprovalList.htm";
			revurl="CommitteeFlow.htm?committeemainid="+pe.getCommitteeMainId().toString();
			lastUrl="CommitteeMainMembers.htm?committeemainid="+pe.getCommitteeMainId();
		}else {
			Object[] scheduleData = dao.CommitteeScheduleData(pe.getScheduleId().toString());
			msg="MOM with RefNo "+pe.getRefNo()+",";
			url ="MoMApprovalList.htm";
			revurl="MeetingMinutesApproval.htm?committeescheduleid="+pe.getScheduleId().toString();
			if(scheduleData[8].toString().equalsIgnoreCase("CCM")) {
				lastUrl=("CCMSchedule.htm?ccmScheduleId="+pe.getScheduleId()+"&committeeMainId="+scheduleData[1].toString()+"&committeeId="+scheduleData[7].toString());
			}else {
				lastUrl="CommitteeScheduleView.htm?scheduleid="+pe.getScheduleId();
			}
			
		}
		
		PfmsNotification notification=new PfmsNotification();
		if(flow!=null && flow.equalsIgnoreCase("A")) {
			String  nextcode = pe.getEnoteStatusCodeNext();
			String cuurentCode = pe.getEnoteStatusCode();
			if(nextcode.equalsIgnoreCase("RC1")) {
				notification.setEmpId(pe.getRecommend1());
				notification.setNotificationMessage(msg+ " Forwarded by "+NotificationBy);
				notification.setNotificationUrl(url);
			}else if(nextcode.equalsIgnoreCase("RC2")) {
				notification.setEmpId(pe.getRecommend2());
				notification.setNotificationMessage(msg+ " Recommended by "+NotificationBy);
				notification.setNotificationUrl(url);
			}else if(nextcode.equalsIgnoreCase("RC3")) {
				notification.setEmpId(pe.getRecommend3());
				notification.setNotificationMessage(msg+ "Recommended by "+NotificationBy);
				notification.setNotificationUrl(url);
			}
			else if(cuurentCode.equalsIgnoreCase("APR") && nextcode.equalsIgnoreCase("APR")) {
				notification.setEmpId(pe.getInitiatedBy());
				notification.setNotificationMessage(msg+ " is Approved by "+NotificationBy);
				notification.setNotificationUrl(lastUrl);
				if(pe.getEnoteFrom().equalsIgnoreCase("S")) {
					CommitteeMeetingApproval approval=new CommitteeMeetingApproval();
					CommitteeSchedule schedule= new CommitteeSchedule();
					approval.setScheduleId(pe.getScheduleId());
					approval.setEmpId(empId);
					approval.setMeetingStatus("MMA");
					approval.setActionBy(Username);
					approval.setActionDate(sdf1.format(new Date()));
					
					schedule.setScheduleId(pe.getScheduleId());
					schedule.setScheduleFlag("MMA");
					schedule.setModifiedBy(Username);
					schedule.setModifiedDate(sdf1.format(new Date()));
					dao.MeetingMinutesApproval(approval,schedule);
				}
			}
			else if(nextcode.equalsIgnoreCase("APR")) {
				notification.setEmpId(pe.getApprovingOfficer());
				notification.setNotificationMessage(msg+ " Recommended by "+NotificationBy);
				notification.setNotificationUrl(url);
			}
			notification.setNotificationby(empId);
			notification.setIsActive(1);
			notification.setCreatedBy(Username);
			notification.setCreatedDate(sdf1.format(new Date()));
			notification.setNotificationDate(sdf1.format(new Date()));
			carsdao.addNotifications(notification);
		}else if(flow.equalsIgnoreCase("R")){
			notification.setEmpId(pe.getInitiatedBy());
			notification.setNotificationMessage(msg+ " Returned by "+NotificationBy);
			notification.setNotificationUrl(revurl);
			notification.setNotificationby(empId);
			notification.setIsActive(1);
			notification.setCreatedBy(Username);
			notification.setCreatedDate(sdf1.format(new Date()));
			notification.setNotificationDate(sdf1.format(new Date()));
			carsdao.addNotifications(notification);
			if(pe.getEnoteFrom().equalsIgnoreCase("S")) {
//				CommitteeMeetingApproval approval=new CommitteeMeetingApproval();
//				CommitteeSchedule schedule= new CommitteeSchedule();
//				approval.setScheduleId(pe.getScheduleId());
//				approval.setEmpId(empId);
//				approval.setMeetingStatus("MMR");
//				approval.setActionBy(Username);
//				approval.setActionDate(sdf1.format(new Date()));
//				
//				schedule.setScheduleId(pe.getScheduleId());
//				schedule.setScheduleFlag("MMR");
//				schedule.setModifiedBy(Username);
//				schedule.setModifiedDate(sdf1.format(new Date()));
//				dao.MeetingMinutesApproval(approval,schedule);
			}
		}
		
		
		return 1l;
	}
	
	@Override
	public List<Object[]> EnoteTransactionList(String enoteTrackId) throws Exception {
		return dao.EnoteTransactionList(enoteTrackId);
	}
	@Override
	public List<Object[]> eNotePendingList(long empId, String Type) throws Exception {
		return dao.eNotePendingList(empId,Type);
	}
	
	@Override
	public Object[] NewApprovalList(String enoteId) throws Exception {
		return dao.NewApprovalList(enoteId);
	}
	@Override
	public List<Object[]> eNoteApprovalList(long empId, String fromDate, String toDate) throws Exception {
		return dao.eNoteApprovalList(empId,fromDate,toDate);
	}
	@Override
	public List<Object[]> EnotePrintDetails(long enoteId,String type) throws Exception {
		// TODO Auto-generated method stub
		
		List<Object[]>epdetails =  dao.EnotePrintDetails(enoteId,type);
		
		List<String>status = Arrays.asList("FWD","RFD","RC1","RC2","RC3","RC4","RC5","APR");
		
		
		List<Object[]>newList= new ArrayList<>();
		
		for(String s:status) {
			
			List<Object[]>subList = epdetails.stream().filter(e->e[8].toString().equalsIgnoreCase(s)).collect(Collectors.toList());
			
			if(subList!=null && subList.size()>0) {
				
				newList.add(subList.get(subList.size()-1));
			}
			
		}
		
		return newList;
	}
	@Override
	public List<Object[]> getAgendaAttachId(String agendaid) throws Exception {
		return dao.getAgendaAttachId(agendaid);
	}
    @Override
    public long addAgendaLinkFile(CommitteeScheduleAgendaDocs docs) throws Exception {
    	return dao.addAgendaLinkFile(docs);
    }
    @Override
    public List<Object[]> MomeNoteApprovalList(long empId, String fromDate, String toDate) throws Exception {
    	return dao.MomeNoteApprovalList(empId,fromDate,toDate);
    }

	@Override
	public List<Object[]> carsScheduleList(String carsInitiationId) throws Exception {

		return dao.carsScheduleList(carsInitiationId);
	}
	
	@Override
	public void InvitationRoleoUpdate(String[] role, String[] empNo, String[] labCode, String userId,String []invitationid) throws Exception {

		List<PfmsEmpRoles>emps = masterDao.getAllEmpRoles();
		
		List<PfmsEmpRoles>subemps = new ArrayList<>();
		for (int i=0;i<empNo.length;i++) {
			  final int index = i;
			
			if (empNo[i] != null && labCode[i] != null) {
		        // Declare filteredList inside the loop
		        List<PfmsEmpRoles> filteredList = emps.stream()
		            .filter(e -> e.getEmpNo().equalsIgnoreCase(empNo[index]) 
		             && e.getOrganization().equalsIgnoreCase(labCode[index]))
		            .collect(Collectors.toList());
		        // Add the filtered results to subemps
		        subemps.addAll(filteredList);
		    }
			if(subemps.size()==0) {
				if(role[i]!=null && role[i].length()>0) {
				PfmsEmpRoles pf = new PfmsEmpRoles();
				pf.setEmpNo(empNo[i]);
				pf.setOrganization(labCode[i]);
				pf.setCreatedBy(userId);
				pf.setCreatedDate(LocalDate.now()+"");	
				pf.setEmpRole(role[i]);
				pf.setIsActive(1);
				masterDao.addPfmsEmpRoles(pf);
				}
			}
			if(role[i]!=null && role[i].length()>0) {
				dao.InvitationRoleoUpdate(role[i],invitationid[i]);
			}
		}
		
	}
	
	@Override
	public List<MeetingCheckDto> getMeetingCheckDto(String date, String committeemainid) throws Exception {
		
		return dao.getMeetingCheckDto(date,committeemainid );
	}
	
	@Override
	public List<MeetingCheckDto> getMeetingCheckDto(String empid, String labocode,String scheduleid) throws Exception {
		
		return dao.getMeetingCheckDto(empid,labocode,scheduleid);
	}
	
	@Override
	public List<Object[]> previousMeetingHeld(String committeeid) throws Exception {
		
		return dao.previousMeetingHeld(committeeid) ;
	}
	
	@Override
	public List<Object[]> getRecommendationsOfCommittee(String committeeid) throws Exception {
		
		return dao.getRecommendationsOfCommittee(committeeid);
	}
	
	@Override
	public List<Object[]> getDecisionsofCommittee(String committeeid) throws Exception {
		
		return dao.getDecisionsofCommittee(committeeid);
	}
	
	/* ********************************************* Programme AD ************************************************ */
	@Override
	public List<ProgrammeMaster> getProgrammeMasterList() throws Exception {
		
		return dao.getProgrammeMasterList();
	}
	
	@Override
	public Long getCommitteeMainIdByProgrammeId(String programmeId) throws Exception {
		
		return dao.getCommitteeMainIdByProgrammeId(programmeId);
	}
	
	@Override
	public List<Object[]> prgmScheduleList(String programmeId) throws Exception {
		
		return dao.prgmScheduleList(programmeId);
	}
	
	@Override
	public List<ProgrammeProjects> getProgrammeProjectsList(String programmeId) throws Exception {
		
		return dao.getProgrammeProjectsList(programmeId);
	}
	
	@Override
	public List<Object[]> prgmProjectList(String programmeId) throws Exception {
		
		return dao.prgmProjectList(programmeId);
	}
	
	@Override
	public ProgrammeMaster getProgrammeMasterById(String programmeId) throws Exception {
		
		return dao.getProgrammeMasterById(programmeId);
	}
	
	/* ********************************************* Programme AD End************************************************ */
	@Override
	public long sentMomDraft(CommitteScheduleMinutesDraft cmd) throws Exception {
		
		CommitteeSchedule schedule = dao.getCommitteeScheduleById(cmd.getScheduleId());
		
		 PfmsNotification notification = new PfmsNotification();
	        notification.setEmpId(cmd.getEmpid());
	        notification.setNotificationMessage("A MoM draft has been sent to you for review for Meeting ID "+schedule.getMeetingId());
	        notification.setNotificationUrl("CommitteeMomDraft.htm");
	        notification.setNotificationby(0L);
	        notification.setIsActive(1);
	        notification.setCreatedBy(cmd.getSentBy() );
	        notification.setCreatedDate(sdf1.format(new Date()));
	        notification.setNotificationDate(sdf1.format(new Date()));
	        carsdao.addNotifications(notification);
		
		
		return dao.sentMomDraft(cmd);
	}
	
	@Override
	public List<CommitteScheduleMinutesDraft> getCommitteScheduleMinutesDraftList()
			throws Exception {
		
		return dao.getCommitteScheduleMinutesDraftList();
	}
	
	@Override
	public List<Object[]> getdraftMomList(String empId, String pageSize) throws Exception {
		
		return dao.getdraftMomList(empId,pageSize);
	}
	
	@Override
	public long saveCommitteeSchedulesMomDraftRemarks(CommitteeSchedulesMomDraftRemarks cmd) throws Exception {
		
		return dao.saveCommitteeSchedulesMomDraftRemarks(cmd);
	}
	
	@Override
	public List<Object[]> getCommitteeSchedulesMomDraftRemarks(Long scheduleId) throws Exception {
		
		return dao.getCommitteeSchedulesMomDraftRemarks(scheduleId);
	}
	
	@Override
	public List<Object[]> preProjectlist(String labCode) throws Exception {
		
		return dao.preProjectlist(labCode);
	}
	
	@Override
	public FileRepUploadPreProject getPreProjectAgendaDocById(String filerepid) throws Exception {
		
		return dao.getPreProjectAgendaDocById(filerepid);
	}
	
	@Override
	public Object[] carsCommitteeDescriptionTOR(String carsInitiationId, String committeeId) throws Exception {
		
		return dao.carsCommitteeDescriptionTOR(carsInitiationId, committeeId);
	}
	
	@Override
	public CommitteeCARS getCommitteeCARSById(String comCARSInitiationId) throws Exception {
		
		return dao.getCommitteeCARSById(comCARSInitiationId);
	}
	
	@Override
	public Long addCommitteeCARS(CommitteeCARS committeeCARS) throws Exception {
		return dao.addCommitteeCARS(committeeCARS);
	}

//	---------------------------------- Naveen R 3/9/25 MOM Check ------------------------------------------
	
	@Override
	public List<Object[]> CommitteeScheduleMinutesforAction(String committeescheduleid) {
		return dao.CommitteeScheduleMinutesforAction(committeescheduleid);
	}

	@Override
	public Long addRepresentative(CommitteeRepresentative rep) {
		return dao.addRepresentative(rep);
	}

	@Override
	public CommitteeRepresentative getRepresentativeById(String repId) {
		return dao.getRepresentativeById(repId);
	}

	@Override
	public Long getRepNameDuplicateCount(String repName) {
		return dao.getRepNameDuplicateCount(repName);
	}

	@Override
	public Long getRepCodeDuplicateCount(String repCode) {
		return dao.getRepCodeDuplicateCount(repCode);
	}

	@Override
	public List<Object[]> getAircraftList(String committeeScheduleId) {
		return dao.getAircraftList(committeeScheduleId);
	}

	@Override
	public List<Object[]> getSubSystemList(String committeeScheduleId) {
		return dao.getSubSystemList(committeeScheduleId);
	}
	
	@Override
	public Long addAircraft(CommitteeAircraft aircraft) {
		return dao.addAircraft(aircraft);
	}

	@Override
	public CommitteeAircraft getAircraftById(String aircraftId) {
		return dao.getAircraftById(aircraftId);
	}

	@Override
	public Long addSubSystem(CommitteeSubSystem sub) {
		return dao.addSubSystem(sub);
	}

	@Override
	public CommitteeSubSystem getSubSystemById(String subsystemId) {
		return dao.getSubSystemById(subsystemId);
	}

	@Override
	public List<Object[]> committeeScheduleMinutesforActionForMomADE(String committeescheduleid) {
		return dao.committeeScheduleMinutesforActionForMomADE(committeescheduleid);
	}

	@Override
	public Object[] CommitteeScheduleEditDataforMom(String committeescheduleid) {
		return dao.CommitteeScheduleEditDataforMom(committeescheduleid);
	}

	@Override
	public List<Object[]> PrgmAgendaList(String scheduleid) {
		return dao.PrgmAgendaList(scheduleid);
	}
	
}