package com.vts.pfms.committee.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.vts.pfms.FormatConverter;
import com.vts.pfms.committee.dao.CommitteeDao;
import com.vts.pfms.committee.dao.RODDao;
import com.vts.pfms.committee.dto.CommitteeScheduleDto;
import com.vts.pfms.committee.model.CommitteeSchedule;
import com.vts.pfms.committee.model.CommitteeScheduleAgenda;
import com.vts.pfms.committee.model.RODMaster;
import com.vts.pfms.mail.CustomJavaMailSender;

@Service
public class RODServiceImpl implements RODService{

	private static final Logger logger = LogManager.getLogger(RODServiceImpl.class);
	
	FormatConverter fc=new FormatConverter();
	private SimpleDateFormat sdf1= fc.getSqlDateAndTimeFormat();  //new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat sdf= fc.getRegularDateFormat();     //new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat sdf2=	fc.getDateMonthShortName();   //new SimpleDateFormat("dd-MMM-yyyy");
	private SimpleDateFormat sdf3=	fc.getSqlDateFormat();			//	new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	RODDao dao;

	@Autowired
	CommitteeDao committeedao;
	
	private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@Autowired 
	@Lazy
	CustomJavaMailSender cm;
	
	@Override
	public List<Object[]> rodProjectScheduleListAll(String projectId) throws Exception {
		
		return dao.rodProjectScheduleListAll(projectId);
	}

	@Override
	public Object[] getRODMasterDetails(String rodNameId) throws Exception {
		
		return dao.getRODMasterDetails(rodNameId);
	}

	@Override
	public List<Object[]> rodProjectScheduleListAll(String projectId, String rodNameId) throws Exception {
		
		return dao.rodProjectScheduleListAll(projectId, rodNameId);
	}

	@Override
	public List<Object[]> rodNamesList() throws Exception {
		
		return dao.rodNamesList();
	}

	@Override
	public Long addNewRODName(RODMaster master) throws Exception {
		
		return dao.addNewRODName(master);
	}
	
	@Override
	public long RODScheduleAddSubmit(CommitteeScheduleDto committeescheduledto)throws Exception
	{
		logger.info(new Date() +"Inside SERVICE RODScheduleAddSubmit ");
		CommitteeSchedule committeeschedule = new  CommitteeSchedule(); 
		
		committeeschedule.setRODNameId(Long.parseLong(committeescheduledto.getRodNameId()));
		committeeschedule.setLabCode(committeescheduledto.getLabCode());	
		committeeschedule.setCommitteeId(committeescheduledto.getCommitteeId());
		committeeschedule.setProjectId(Long.parseLong(committeescheduledto.getProjectId()));
		committeeschedule.setScheduleStartTime(committeescheduledto.getScheduleStartTime());
		committeeschedule.setScheduleDate(new java.sql.Date(sdf.parse(committeescheduledto.getScheduleDate()).getTime()));
		committeeschedule.setScheduleSub("N");		
		committeeschedule.setScheduleFlag(committeescheduledto.getScheduleFlag());
		committeeschedule.setConfidential(committeescheduledto.getConfidential());
		committeeschedule.setDivisionId(Long.parseLong(committeescheduledto.getDivisionId()));
		committeeschedule.setInitiationId(Long.parseLong(committeescheduledto.getInitiationId()));
		committeeschedule.setPresentationFrozen("N");
		committeeschedule.setCreatedBy(committeescheduledto.getCreatedBy());
		committeeschedule.setCreatedDate(sdf1.format(new Date()));
		committeeschedule.setIsActive(1);
		committeeschedule.setBriefingPaperFrozen("N");
		committeeschedule.setMinutesFrozen("N");
		committeeschedule.setBriefingStatus("INI");
		String RODName= dao.getRODMasterDetails(committeescheduledto.getRodNameId().toString())[2].toString();
		String LabName=committeedao.LabDetails(committeeschedule.getLabCode())[1].toString();
		Long SerialNo=committeedao.MeetingCount(new java.sql.Date(sdf.parse(committeescheduledto.getScheduleDate()).getTime()),committeescheduledto.getProjectId());
		String ProjectName=null;
		if(Long.parseLong(committeescheduledto.getProjectId())>0) 
		{
			ProjectName=committeedao.projectdetails(committeescheduledto.getProjectId())[4].toString();
		}
		else if(Long.parseLong(committeescheduledto.getDivisionId())>0) 
		{
			ProjectName="DIV";
		}
		else if(Long.parseLong(committeescheduledto.getInitiationId())>0) 
		{
			ProjectName=committeedao.Initiationdetails(committeescheduledto.getInitiationId())[1].toString();
		}
		else
		{
			ProjectName="GEN";
		}
		Date ScheduledDate= (new java.sql.Date(sdf.parse(committeescheduledto.getScheduleDate()).getTime()));
		committeeschedule.setMeetingId(LabName.trim()+"/"+ProjectName.trim()+"/"+RODName.trim()+"/"+sdf2.format(ScheduledDate).toString().toUpperCase().replace("-", "")+"/"+(SerialNo+1));
		return committeedao.CommitteeScheduleAddSubmit(committeeschedule);
	}

	@Override
	public Object[] RODScheduleEditData(String CommitteeScheduleId) throws Exception {
		
		return dao.RODScheduleEditData(CommitteeScheduleId);
	}
	
	@Override 
	public Long RODScheduleUpdate(CommitteeScheduleDto committeescheduledto) throws Exception {

		logger.info(new Date() +"Inside SERVICE CommitteeScheduleUpdate ");
		String meetingid = dao.RODScheduleData(committeescheduledto.getScheduleId().toString())[12].toString();
		CommitteeSchedule committeeschedule=new  CommitteeSchedule();
		committeeschedule.setScheduleDate(new java.sql.Date(sdf.parse(committeescheduledto.getScheduleDate()).getTime()));
		committeeschedule.setScheduleStartTime(committeescheduledto.getScheduleStartTime());
		committeeschedule.setScheduleId(committeescheduledto.getScheduleId());
		committeeschedule.setModifiedBy(committeescheduledto.getCreatedBy());
		committeeschedule.setModifiedDate(sdf1.format(new Date()));
		
		String[] strarr=meetingid.split("/");
		meetingid=strarr[0].toString().trim() +"/"+strarr[1].toString().trim() +"/"+strarr[2].toString().trim() +"/"+sdf2.format(committeeschedule.getScheduleDate()).toString().toUpperCase().replace("-", "")+"/"+strarr[4].toString().trim() ;
		committeeschedule.setMeetingId(meetingid);
		return committeedao.CommitteeScheduleUpdate(committeeschedule);
	}

	@Override
	public Object[] RODScheduleData(String CommitteeScheduleId) throws Exception {
		
		return dao.RODScheduleData(CommitteeScheduleId);
	}

	@Override
	public List<Object[]> RODMeetingSearchList(String MeetingId, String LabCode) throws Exception {
		
		return dao.RODMeetingSearchList(MeetingId, LabCode);
	}
	
	@Override
	public long RODPreviousAgendaAdd(String scheduleidto,String[] fromagendaids,String userid) throws Exception {
		logger.info(new Date() +"Inside SERVICE RODPreviousAgendaAdd ");
		long count=0;
		for(int i=0;i<fromagendaids.length;i++)
		{			
			Object[] scheduletodata = dao.RODScheduleEditData(scheduleidto);
			CommitteeScheduleAgenda scheduleagendafrom = committeedao.CommitteePreviousAgendaGet(fromagendaids[i]);
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

			List<Object[]> agendapriority=committeedao.CommitteeScheduleAgendaPriority(scheduleidto);
			
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
			
			
			count=committeedao.CommitteeAgendaSubmit(scheduleagendato);
		}		
		return count;
	}
	
	@Override
	public Object[] KickOffRODMeeting(HttpServletRequest req,RedirectAttributes redir) throws Exception
	{
		logger.info(new Date() +"Inside SERVICE KickOffRODMeeting ");
//		String CommitteeMainId=req.getParameter("committeemainid");
		String CommitteeScheduleId=req.getParameter("committeescheduleid");
		String Option=req.getParameter("sub");
//		SimpleDateFormat sdf1=new SimpleDateFormat("dd-MM-yyyy");
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		Object[] scheduledata=dao.RODScheduleData(CommitteeScheduleId);
		LocalDate scheduledate=LocalDate.parse(scheduledata[2].toString(),dtf);
	
//		if(LocalDate.now().isAfter(scheduledate))
//			{	
			CommitteeSchedule committeeschedule=new CommitteeSchedule(); 
			committeeschedule.setKickOffOtp(null);
			committeeschedule.setScheduleId(Long.parseLong(CommitteeScheduleId));
			committeeschedule.setScheduleFlag("MKV");
			String  ret= dao.UpdateOtp(committeeschedule);
			if (Integer.parseInt(ret)>0) {
			redir.addAttribute("result", "Meeting Kick Off Successful");
			} else {
			redir.addAttribute("result", "Meeting Kick Off UnSuccessful");
			}	
//			Object[] returnobj=new Object[2];
//			returnobj[0]=req;
//			returnobj[1]=redir;
//			return returnobj;
			
//		}
//		else if(!Option.equalsIgnoreCase("Validate")) 
//		{		
//			List<Object[]> Email=dao.ChaipersonEmailId(CommitteeMainId);
//			Random random = new Random(); 
//			int otp=random.nextInt(9000) + 1000;
//			CharSequence cs = String.valueOf(otp);
//			String Otp=encoder.encode(cs);
//			CommitteeSchedule committeeschedule=new CommitteeSchedule(); 
//			committeeschedule.setKickOffOtp(Otp);
//			committeeschedule.setScheduleId(Long.parseLong(CommitteeScheduleId));
//			committeeschedule.setScheduleFlag("MKO");
//			ArrayList<String> emails= new ArrayList<String>();
//			for(Object[] obj : Email)
//			{
//			if(obj[0]!=null) {
//			emails.add(obj[0].toString());
//			}
//			}
//			if(Email.get(0)[1].toString().equalsIgnoreCase("0")) 
//			{
//				Object[] RtmddoEmail=dao.RtmddoEmail();
//				if(RtmddoEmail!=null) {
//				emails.add(RtmddoEmail[0].toString());
//				}
//			}
//			
//				
//			String [] ToEmail = emails.toArray(new String[emails.size()]);
//			
//			if (ToEmail.length>0) 
//			{
//				int count=0;
//				String subject=req.getParameter("rodname") + " Meeting OTP ";
//				String message=String.valueOf(otp) + " is the OTP for Verification of Meeting ( " + req.getParameter("meetingid") +" ) which is Scheduled at " + sdf1.format(sdf.parse(req.getParameter("meetingdate"))) + "(" + req.getParameter("meetingtime") + "). Kindly Do Not Share the OTP with Anyone.";
//				for(String email:ToEmail) {
//				count = count +	cm.sendMessage(email, subject, message);
//				}
//			try
//			{
//					javaMailSender.send(msg);
//			}catch (Exception e) {
//				logger.error(new Date() +" Inside SERVICE KickOffMeeting ", e);
//				}
//			committeedao.UpdateOtp(committeeschedule);
//				redir.addAttribute("result", " OTP Sent to Successfully !! ");
//				redir.addFlashAttribute("otp", String.valueOf(otp));
//		}else
//			{
//				redir.addAttribute("resultfail", "Email-ids' Not Found");
//		}
//
//			dao.UpdateOtp(committeeschedule);
//		 }
//
//		 else {
//		 
//			 if(req.getParameter("otpvalue")!=null) {
//			 
//			 String OtpDb=committeedao.KickOffOtp(CommitteeScheduleId);
//
//		 		if(encoder.matches( req.getParameter("otpvalue"),OtpDb)) {
//		 			
//		 			CommitteeSchedule committeescheduledto=new CommitteeSchedule(); 
//				 	committeescheduledto.setKickOffOtp(OtpDb);
//				 	committeescheduledto.setScheduleId(Long.parseLong(CommitteeScheduleId));
//				 	committeescheduledto.setScheduleFlag("MKV");
//				 	String Validate=dao.UpdateOtp(committeescheduledto);
//				 	
//				 	if (Validate !=null) {
//						redir.addAttribute("result", " OTP Validated Successfully ");
//					} else {
//						redir.addAttribute("result", " OTP Not Matched");
//					}	
//				 	
//			 	}else {
//			 		redir.addAttribute("resultfail", " OTP Not Matched");
//			 	}
//			 }
//		 }
		 Object[] returnobj=new Object[2];
		 returnobj[0]=req;
		 returnobj[1]=redir;
		 return returnobj;
		 
	}
	@Override
	public List<Object[]> RODActionDetails(String rodNameId) throws Exception {
		
		return dao.RODActionDetails(rodNameId);
	}
	
	@Override
	public int RODMeetingNo(Object[] scheduledata) throws Exception 
	{
		logger.info(new Date() +"Inside SERVICE RODMeetingNo ");
		int count=0;
		if(scheduledata[21].toString().equalsIgnoreCase("P")) {
			String scheduledate = scheduledata[2].toString(); 
			String projectid = scheduledata[9].toString();
			String rodNameId = scheduledata[0].toString();
			count= dao.RODCommProScheduleList(projectid, rodNameId,scheduledate);
		}
		return count;
	}
	
	@Override
	public List<Object[]> industryPartnerRepListInvitations(String industryPartnerId, String scheduleid) throws Exception {
		
		return dao.industryPartnerRepListInvitations(industryPartnerId, scheduleid);
	}
}
