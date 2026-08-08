package com.vts.pfms.committee.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import com.vts.pfms.CharArrayWriterResponse;
import com.vts.pfms.FormatConverter;
import com.vts.pfms.committee.dto.CommitteeScheduleDto;
import com.vts.pfms.committee.dto.EmpAccessCheckDto;
import com.vts.pfms.committee.model.RODMaster;
import com.vts.pfms.committee.service.CommitteeService;
import com.vts.pfms.committee.service.RODService;
import com.vts.pfms.mail.CustomJavaMailSender;
import com.vts.pfms.mail.MailService;
import com.vts.pfms.master.model.IndustryPartner;
import com.vts.pfms.master.service.MasterService;
import com.vts.pfms.utils.InputValidator;
import com.vts.pfms.utils.PMSLogoUtil;

@Controller
public class RODController {

	private static final Logger logger = LogManager.getLogger(RODController.class);

	FormatConverter fc = new FormatConverter();
	private SimpleDateFormat sdtf = fc.getSqlDateAndTimeFormat();
	private SimpleDateFormat sdf = fc.getSqlDateFormat();

	@Autowired 
	CommitteeService committeeservice;

	@Autowired
	MailService mailService;

	@Autowired
	RODService service;
	
	@Autowired
	MasterService masterservice;
	
	@Value("${File_Size}")
	String file_size;

	@Autowired
	@Lazy
	CustomJavaMailSender cm;
	
	@Autowired
	PMSLogoUtil LogoUtil;
	
	@RequestMapping(value="RecordofDiscussion.htm")
	public String RecordofDiscussion(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{		

		String UserId=(String)ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside RecordofDiscussion.htm "+UserId);
		try {
			String Logintype= (String)ses.getAttribute("LoginType");
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String projectId=req.getParameter("projectId");
			String initiationId=req.getParameter("initiationId");
			
			System.out.println("initiationId-----------"+initiationId);
			String rodNameId=req.getParameter("rodNameId");
			List<Object[]> InitiatedProjectDetailsList=committeeservice.InitiatedProjectDetailsList();
			
			String projectType=req.getParameter("projectType")!=null?req.getParameter("projectType"):"P";
			
			List<Object[]> projectdetailslist=committeeservice.LoginProjectDetailsList(EmpId,Logintype,LabCode);
 
			if(projectdetailslist.size()==0 && projectType.equalsIgnoreCase("P")) 
			{	
				projectType="N";
				//return "redirect:/MainDashBoard.htm";
				req.setAttribute("resultfail", "No project is Assigned to You!");
			}
			if(InitiatedProjectDetailsList.size()==0 && projectType.equalsIgnoreCase("I")) 
			{	
				//return "redirect:/MainDashBoard.htm";
				projectType="N";
				req.setAttribute("resultfail", "No project is Assigned to You!");
			}
			

			if(rodNameId==null)
			{
				rodNameId="all";
			}			

			if(projectType.equalsIgnoreCase("P")){
			if(projectId==null || projectId.equals("null"))
			{
				projectId=projectdetailslist.get(0)[0].toString();
			}
			initiationId="0";
			req.setAttribute("ProjectsList",projectdetailslist);
		}else if(projectType.equalsIgnoreCase("I")) {
			projectId="0";
			if(initiationId==null || initiationId.equals("null"))
			{
				initiationId=InitiatedProjectDetailsList.get(0)[0].toString();
			}
			req.setAttribute("ProjectsList",InitiatedProjectDetailsList);
		}else {
			projectId="0";
			initiationId="0";
		}
			
			
			if(rodNameId==null || rodNameId.equals("all"))
			{				
				req.setAttribute("RODProjectschedulelist", service.rodProjectScheduleListAll(projectId));
			} 
			else 
			{
				req.setAttribute("RODMasterDetails", service.getRODMasterDetails(rodNameId));
				req.setAttribute("RODProjectschedulelist", service.rodProjectScheduleListAll(projectId, rodNameId));				
			}

			List<Object[]> rodNameslist = service.rodNamesList();


			req.setAttribute("projectId",projectId);
			req.setAttribute("initiationId",initiationId);
			req.setAttribute("rodNameId",rodNameId);
			req.setAttribute("projectType",projectType!=null?projectType:"P");
			req.setAttribute("Projectdetails",projectdetailslist.get(0));
			
			req.setAttribute("RODNameslist",rodNameslist);

			return "rod/RODProjectSchedule";
		}			
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside RecordofDiscussion.htm "+UserId,e);
			return "static/Error";
		}

	}

	//	@RequestMapping(value="AddNewRODName.htm", method=RequestMethod.GET)
	//	public @ResponseBody String addNewRODName(HttpSession ses, HttpServletRequest req) throws Exception {
	//		String UserId=(String)ses.getAttribute("Username");
	//		
	//		logger.info(new Date() +"Inside AddNewRODName.htm "+UserId);
	//		Gson json = new Gson();
	//		
	//		long result = 0;
	//		try
	//		{	
	//			RODMaster master = new RODMaster();
	//			master.setRODName(req.getParameter("rodName"));
	//			master.setCreatedBy(UserId);
	//			master.setCreatedDate(sdtf.format(new Date()));
	//			master.setIsActive(1);
	//			
	//			result = service.addNewRODName(master);
	//			
	//		}catch (Exception e) {
	//			logger.error(new Date() +"Inside AddNewRODName.htm "+UserId ,e);
	//		    e.printStackTrace(); 
	//		}
	//		  
	//		return json.toJson(result);
	//	}

	@RequestMapping(value="AddNewRODName.htm", method=RequestMethod.POST)
	public String addNewRODName(HttpSession ses, HttpServletRequest req,RedirectAttributes redir) throws Exception {
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside AddNewRODName.htm "+UserId);

		try
		{	
			String projectId=req.getParameter("projectId");
			String rodName = req.getParameter("rodName");
			String rodShortName = req.getParameter("rodShortName");
			if(InputValidator.isContainsHTMLTags(rodName) || InputValidator.isContainsHTMLTags(rodShortName)) {
				redir.addAttribute("resultfail","Html Tags are not allowed");
				return "redirect:/RecordofDiscussion.htm";
			}

			RODMaster master = new RODMaster();
			master.setRODName(req.getParameter("rodName"));
			master.setRODShortName(req.getParameter("rodShortName"));
			master.setCreatedBy(UserId);
			master.setCreatedDate(sdtf.format(new Date()));
			master.setIsActive(1);

			long result = service.addNewRODName(master);
			if(result!=0) {
				redir.addAttribute("projectId", projectId);
				redir.addAttribute("rodNameId", result);
			}

			return "redirect:/RecordofDiscussion.htm";
		}catch (Exception e) {
			logger.error(new Date() +"Inside AddNewRODName.htm "+UserId ,e);
			e.printStackTrace(); 
			return "static/Error";
		}

	}

	@RequestMapping(value="RODScheduleAddSubmit.htm",method=RequestMethod.POST)
	public String RODScheduleAddSubmit(HttpServletRequest req,HttpServletResponse res, RedirectAttributes redir, HttpSession ses) throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside RODScheduleAddSubmit.htm "+UserId);
		try
		{
			String projectid=req.getParameter("projectId");
			String rodNameId=req.getParameter("rodNameId");

			CommitteeScheduleDto committeescheduledto = new CommitteeScheduleDto(); 
			committeescheduledto.setCommitteeId(Long.parseLong(req.getParameter("committeeId")));
			committeescheduledto.setScheduleDate(req.getParameter("startdate"));
			committeescheduledto.setScheduleStartTime(req.getParameter("starttime"));
			committeescheduledto.setCreatedBy(UserId);
			committeescheduledto.setScheduleFlag("MSC");			
			committeescheduledto.setProjectId(projectid);
			committeescheduledto.setDivisionId(req.getParameter("divisionId"));
			committeescheduledto.setInitiationId(req.getParameter("initiationId"));
			committeescheduledto.setRodNameId(rodNameId);
			committeescheduledto.setLabCode(labcode);

			if(projectid!=null && Long.parseLong(projectid)>0) 
			{
				Object[] projectdetails=committeeservice.projectdetails(projectid);				
				committeescheduledto.setConfidential(projectdetails[10].toString());
			}else {
				committeescheduledto.setConfidential("5");
			}


			long count=0;
			count=service.RODScheduleAddSubmit(committeescheduledto);
			redir.addAttribute("rodNameId",rodNameId);

			String rodName=req.getParameter("rodName");

			if(count>0)
			{
				redir.addAttribute("result", rodName + " Schedule Added Successfully !");
			}
			else 
			{
				redir.addAttribute("resultfail", "Schedule Adding Failed, Try Again");	
			}			
			redir.addAttribute("scheduleid", String.valueOf(count));
			return "redirect:/RODScheduleAgenda.htm";
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +"Inside RODScheduleAddSubmit.htm "+UserId,e);
			return "static/Error";
		}
	}

	@RequestMapping(value="RODScheduleAgenda.htm",method= {RequestMethod.GET,RequestMethod.POST})
	public String RODScheduleAgenda(Model model,HttpServletRequest req,HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode =ses.getAttribute("labcode").toString().trim();
		logger.info(new Date() +"Inside RODScheduleAgenda.htm "+UserId);
		try
		{
			String CommitteeScheduleId= null;			
			if (req.getParameter("scheduleid") != null) 
			{
				CommitteeScheduleId = (String) req.getParameter("scheduleid");
			} 
			else
			{
				Map md=model.asMap();
				CommitteeScheduleId=(String)md.get("scheduleid");
			}
			Object[] scheduledata= service.RODScheduleEditData(CommitteeScheduleId);
			List<Object[]> committeeagendalist = committeeservice.AgendaList(CommitteeScheduleId);

			String projectid = scheduledata[9].toString();
			req.setAttribute("AllLabList", committeeservice.AllLabList());
			req.setAttribute("scheduledata",scheduledata);
			req.setAttribute("projectlist", committeeservice.ProjectList(LabCode));
			req.setAttribute("preProjectlist", committeeservice.preProjectlist(LabCode));
			req.setAttribute("rodagendalist", committeeagendalist);
			req.setAttribute("labdata", committeeservice.LabDetails(scheduledata[24].toString()));			
			req.setAttribute("filesize",file_size);
			req.setAttribute("projectid",projectid);
			req.setAttribute("filerepmasterlistall",committeeservice.FileRepMasterListAll(projectid,LabCode));
			req.setAttribute("AgendaDocList",committeeservice.AgendaLinkedDocList(CommitteeScheduleId));

			req.setAttribute("LabCode",LabCode);
			req.setAttribute("LabEmpList",committeeservice.PreseneterForCommitteSchedule(LabCode.trim()));

			return "rod/RODScheduleAgenda";
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +"Inside RODScheduleAgenda.htm "+UserId,e);
			return "static/Error";
		}
	}

	@RequestMapping(value="RODScheduleView.htm",method= {RequestMethod.GET,RequestMethod.POST})
	public String RODScheduleView(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String Logintype= (String)ses.getAttribute("LoginType");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeScheduleView.htm "+UserId);
		try
		{
			String UserView=null;
			Map md=model.asMap();
			if(req.getParameter("userview")!=null) {
				UserView=(String)req.getParameter("userview");
			}
			String CommitteeScheduleId=null;

			if(req.getParameter("scheduleid")!=null) {
				CommitteeScheduleId=(String)req.getParameter("scheduleid");
			}
			else {
				CommitteeScheduleId=(String)md.get("scheduleid");
			}
			System.out.println(CommitteeScheduleId+"-----");
			int committeecons=1;
			Object[] rodscheduleeditdata=service.RODScheduleEditData(CommitteeScheduleId);
			String projectid= rodscheduleeditdata[9].toString();
			String initiationid= rodscheduleeditdata[17].toString();
			if(Integer.parseInt(projectid)>0)
			{
				req.setAttribute("projectdetails", committeeservice.projectdetails(projectid));
			}
			if(Integer.parseInt(initiationid)>0) {
				req.setAttribute("initiationdetails", committeeservice.Initiationdetails(initiationid));
			}

//			if(Logintype.equalsIgnoreCase("A") || Logintype.equalsIgnoreCase("Z") || Logintype.equalsIgnoreCase("C")|| Logintype.equalsIgnoreCase("I")) 
//			{
//				if(md.get("otp")!=null) {
//					req.setAttribute("otp", md.get("otp"));
//				}
//			}

			//			req.setAttribute("membertype", MemberType);
			req.setAttribute("userview",UserView);			
			req.setAttribute("ReturnData", committeeservice.AgendaReturnData(CommitteeScheduleId));
			req.setAttribute("rodscheduleeditdata", rodscheduleeditdata);
			req.setAttribute("rodagendalist", committeeservice.AgendaList(CommitteeScheduleId));
			req.setAttribute("rodinvitedlist", committeeservice.CommitteeAtendance(CommitteeScheduleId,"0"));	
			req.setAttribute("employeelist", committeeservice.EmployeeList(LabCode));
			req.setAttribute("pfmscategorylist", committeeservice.PfmsCategoryList());
			req.setAttribute("logintype", Logintype);
			req.setAttribute("committeecons", committeecons); 
			req.setAttribute("AgendaDocList",committeeservice.AgendaLinkedDocList(CommitteeScheduleId));

			int useraccess=committeeservice.ScheduleCommitteeEmpCheck
					(new EmpAccessCheckDto(Logintype,CommitteeScheduleId,EmpId,
							rodscheduleeditdata[1], committeecons,
							rodscheduleeditdata[13].toString()));
			req.setAttribute("useraccess", useraccess);
			return "rod/RODScheduleView";
		}
		catch (Exception e) 
		{
			e.printStackTrace(); 
			logger.error(new Date() +"Inside RODScheduleView.htm "+UserId,e);
			return "static/Error";
		}

	}

	@RequestMapping(value = "RODAgendasFromPreviousMeetingsAdd.htm")
	public String RODAgendasFromPreviousMeetingsAdd(HttpServletRequest req,HttpServletResponse res,HttpSession ses,RedirectAttributes redir)
	{
		String UserId =(String)ses.getAttribute("Username");
		String LabCode =(String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside RODAgendasFromPreviousMeetingsAdd.htm "+UserId);		
		try {
			String scheduleidto = req.getParameter("scheduleidto");
			String meetingid = req.getParameter("meetingid");
			String scheduleidfrom=req.getParameter("scheduleidfrom");
			String search = req.getParameter("search");

			if(search!=null)
			{
				req.setAttribute("meetingsearch", service.RODMeetingSearchList(search,LabCode));					
			}

			if(scheduleidfrom!=null)
			{
				req.setAttribute("rodscheduledata1",service.RODScheduleEditData(scheduleidfrom));
				req.setAttribute("agendalist",committeeservice.AgendaList(scheduleidfrom));
				req.setAttribute("meetingid",meetingid);
			}			

			req.setAttribute("scheduleidto",scheduleidto);
			req.setAttribute("rodscheduleeditdata",service.RODScheduleEditData(scheduleidto));

		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside RODAgendasFromPreviousMeetingsAdd.htm "+UserId, e);
		}
		return "rod/RODPreviousAgendasAdd";
	}

	@RequestMapping(value = "RODPreviousAgendaAddSubmit.htm")
	public String RODPreviousAgendaAddSubmit(HttpServletRequest req,HttpServletResponse res,HttpSession ses,RedirectAttributes redir)
	{
		String UserId =(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside RODPreviousAgendaAddSubmit.htm "+UserId);		
		try {
			String scheduleidto=req.getParameter("scheduleidto");
			//String scheduleidfrom=req.getParameter("scheduleidfrom");
			String[] fromagendaids=req.getParameterValues("fromagendaid");


			long count = service.RODPreviousAgendaAdd(scheduleidto, fromagendaids, UserId);
			if (count > 0) {
				redir.addAttribute("result", "Agenda From Previous Meeting Added Successfull");
			} else {
				redir.addAttribute("resultfail", "Agenda From Previous Meeting Add Unsuccessfull");
			}			
			redir.addAttribute("scheduleid",scheduleidto);				
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside RODPreviousAgendaAddSubmit.htm "+UserId, e);
		}
		return "redirect:/RODScheduleAgenda.htm";
	}
	
	@RequestMapping(value="RODScheduleDelete.htm")
	public String RODScheduleDelete(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteeScheduleDelete.htm "+UserId);
		try 
		{
			String scheduleid=req.getParameter("scheduleid");
			CommitteeScheduleDto dto=new CommitteeScheduleDto();
			dto.setScheduleId(Long.parseLong(scheduleid));
			dto.setModifiedBy(UserId);

			int count=committeeservice.CommitteeScheduleDelete(dto);

			if (count > 0) 
			{						
				redir.addAttribute("result", " Meeting Schedule Deleted  Successfully");					
			} 
			else 
			{
				redir.addAttribute("resultfail", " Meeting Delete Unsucessful");
			}		
			return "redirect:/RecordofDiscussion.htm" ;

		}catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside RODScheduleDelete.htm "+UserId,e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="RODScheduleUpdate.htm",method=RequestMethod.POST)
	public String RODScheduleUpdate(HttpServletRequest req, HttpSession ses,RedirectAttributes redir) throws Exception{
		
		String UserId=(String)ses.getAttribute("Username");
		
		logger.info(new Date() +"Inside RODScheduleUpdate.htm "+UserId);
		try
		{		
			CommitteeScheduleDto committeescheduledto=new CommitteeScheduleDto(); 
			committeescheduledto.setScheduleDate(req.getParameter("committeedate"));
			committeescheduledto.setScheduleStartTime(req.getParameter("committeetime"));
			committeescheduledto.setScheduleId(Long.parseLong(req.getParameter("scheduleid")));
			committeescheduledto.setCreatedBy(UserId);
			
			long count = service.RODScheduleUpdate(committeescheduledto);
			
			if (count > 0) {
				redir.addAttribute("result", "Record of Discussion Schedule Edited Successfully");
			} else {
				redir.addAttribute("resultfail", "Record of Discussion Schedule Edit Unsuccessful");
			}
	
			redir.addAttribute("scheduleid",req.getParameter("scheduleid"));
		}
		catch (Exception e) {
				e.printStackTrace(); 
				logger.error(new Date() +"Inside RODScheduleUpdate.htm "+    UserId,e);
		}
		
		return "redirect:/RODScheduleView.htm";
	}
	
	@RequestMapping(value="RODVenueUpdate.htm")
	public String RODVenueUpdate(HttpServletRequest req, HttpSession ses,RedirectAttributes redir) throws Exception{
		
		String UserId=(String)ses.getAttribute("Username");		
		logger.info(new Date() +"Inside RODVenueUpdate.htm "+UserId);
		try
		{
			CommitteeScheduleDto committeescheduledto=new CommitteeScheduleDto(); 
			committeescheduledto.setScheduleId(Long.parseLong(req.getParameter("scheduleid")));
			committeescheduledto.setMeetingVenue(req.getParameter("venue"));
			committeescheduledto.setConfidential("N");
			committeescheduledto.setReferrence(req.getParameter("reference"));
			committeescheduledto.setPMRCDecisions(req.getParameter("decisions"));		
			int count=0;
			count = committeeservice.UpdateMeetingVenue(committeescheduledto);
			
			if (count > 0) {
				redir.addAttribute("result", "Record of Discussion Schedule Edited Successfully");
			} else {
				redir.addAttribute("resultfail", "Record of Discussion Schedule Edit Unsuccessful");				
			}			
			redir.addAttribute("scheduleid",req.getParameter("scheduleid"));		
			redir.addAttribute("committeescheduleid",req.getParameter("scheduleid"));		
		}
		catch (Exception e) {
				e.printStackTrace(); 
				logger.error(new Date() +"Inside RODVenueUpdate.htm "+   UserId,e);
		} 
		return "redirect:/RODInvitations.htm";
	}
	
	@RequestMapping(value="RODInvitations.htm", method = {RequestMethod.POST,RequestMethod.GET})
	public String RODInvitations(Model model, HttpServletRequest req, HttpServletResponse res, RedirectAttributes redir,HttpSession ses) throws Exception 
	{
		String UserId=(String)ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside RODInvitations.htm "+UserId);
		try
		{
			String committeescheduleid=req.getParameter("committeescheduleid");						
			if (committeescheduleid == null) 
			{
				Map md = model.asMap();
				committeescheduleid = (String) md.get("committeescheduleid");
			}

			Object[] rodscheduledata = service.RODScheduleData(committeescheduleid );

//			String committeeid=committeescheduledata[7].toString();
//			String projectid=committeescheduledata[11].toString();
//			String divisionid=committeescheduledata[13].toString();
//			String initiationid=committeescheduledata[14].toString();

			List<Object[]> rodinvitedlist = committeeservice.CommitteeAtendance(committeescheduleid,"0");
			List<Object[]> EmployeeList = committeeservice.EmployeeListNoInvitedMembers(committeescheduleid,LabCode);
			List<Object[]> ExpertList = committeeservice.ExternalMembersNotInvited(committeescheduleid);
			List<IndustryPartner> industryPartnerList = masterservice.getIndustryPartnerList();
			if(rodinvitedlist.size()==0) 
			{	
//				String committeemainid="0";
//				if(Long.parseLong(initiationid)>0) {
//					committeemainid=String.valueOf(service.LastCommitteeId(committeeid, projectid, divisionid,initiationid));
//				}else {
//					committeemainid=String.valueOf(service.LastCommitteeId(committeeid, projectid, divisionid,"0"));
//				}
//
//
//				req.setAttribute("committeemainid",committeemainid);
//				req.setAttribute("committeeallmemberlist",service.CommitteeAllMembersList(committeemainid) );
				req.setAttribute("committeescheduleid", committeescheduleid);
				req.setAttribute("rodscheduledata",rodscheduledata );				
				req.setAttribute("agendalist",committeeservice.AgendaList(committeescheduleid) );
				req.setAttribute("labid", committeeservice.LabDetails(rodscheduledata[15].toString())[13].toString());
				req.setAttribute("EmployeeList", EmployeeList);
				req.setAttribute("ExpertList", ExpertList);
				req.setAttribute("clusterlablist", committeeservice.AllLabList());
				req.setAttribute("industryPartnerList", industryPartnerList);
				return "rod/ViewRODInvitation";
			}
			else
			{
				

				//req.setAttribute("committeereplist", committeeservice.CommitteeMemberRepList(committeescheduledata[1].toString()));
				req.setAttribute("rodinvitedlist", rodinvitedlist);
				req.setAttribute("EmployeeList", EmployeeList);
				req.setAttribute("ExpertList", ExpertList);
				req.setAttribute("committeescheduleid", committeescheduleid);
				req.setAttribute("rodscheduledata",rodscheduledata );
				req.setAttribute("agendalist",committeeservice.AgendaList(committeescheduleid) );
				req.setAttribute("clusterlablist", committeeservice.AllLabList());
				req.setAttribute("labid", committeeservice.LabDetails(rodscheduledata[15].toString())[13].toString());
				req.setAttribute("industryPartnerList", industryPartnerList);
				return "rod/ViewRODInvitation";
			}


		}
		catch (Exception e) {			
			e.printStackTrace();
			logger.error(new Date() +"Inside RODInvitations.htm "+UserId,e);
			return "static/Error";
		}

	}
	
	@RequestMapping(value="KickOffRODMeeting.htm",method=RequestMethod.POST)
	public String KickOffRODMeeting(HttpServletRequest req,HttpSession ses,RedirectAttributes redir, HttpServletResponse res) throws Exception{
		
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside KickOffRODMeeting.htm "+UserId);
		String CommitteeScheduleId=req.getParameter("committeescheduleid");
		
		 try {
			
			Object[] obj= service.KickOffRODMeeting(req, redir);
		    req=(HttpServletRequest)obj[0];
		    redir=(RedirectAttributes)obj[1];
		    
		 }catch (Exception e) {			
		 	e.printStackTrace(); 
		 	logger.error(new Date() +"Inside KickOffRODMeeting.htm "+UserId,e);
		 	
		 	
		}
		redir.addFlashAttribute("scheduleid",CommitteeScheduleId);
		return "redirect:/RODScheduleView.htm";
	}
	
	@RequestMapping(value = "RODAttendance.htm", method = {RequestMethod.POST,RequestMethod.GET})
	public String RODAttendance(Model model, HttpServletResponse res, HttpServletRequest req, HttpSession ses,
			RedirectAttributes redir) throws Exception 
	{
		String LabCode =(String) ses.getAttribute("labcode");
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteeAttendance.htm "+UserId);
		try
		{
			String committeescheduleid = null;
	
			if (req.getParameter("committeescheduleid") != null) {
				committeescheduleid = (String) req.getParameter("committeescheduleid");
			} else {
				Map md = model.asMap();
				committeescheduleid = (String) md.get("committeescheduleid");
			}
			
			Object[] rodscheduledata =service.RODScheduleData(committeescheduleid);
					
			List<Object[]> rodinvitedlist = committeeservice.CommitteeAtendance(committeescheduleid,"1");
			List<Object[]> EmployeeList = committeeservice.EmployeeListNoInvitedMembers(committeescheduleid,LabCode);
			List<Object[]> ExpertList = committeeservice.ExternalMembersNotInvited(committeescheduleid);
			
//			req.setAttribute("committeereplist", service.CommitteeMemberRepList(committeescheduledata[1].toString()));
			req.setAttribute("EmployeeList", EmployeeList);
			req.setAttribute("ExpertList", ExpertList);
			req.setAttribute("rodinvitedlist", rodinvitedlist);
			req.setAttribute("committeescheduleid", committeescheduleid);
			req.setAttribute("rodscheduledata",rodscheduledata );
			req.setAttribute("LabCode", LabCode);
			req.setAttribute("clusterlablist", committeeservice.AllLabList());
		}
		catch (Exception e) {
				e.printStackTrace(); logger.error(new Date() +"Inside RODAttendance.htm "+UserId,e);
		}		
		return "rod/RODAttendance";
	}
	
	@RequestMapping(value="RODScheduleMinutes.htm",method= {RequestMethod.GET,RequestMethod.POST})
	public String RODScheduleMinutes(Model model,HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside RODScheduleMinutes.htm "+UserId);
		try
		{
			String CommitteeScheduleId=null;
			String specname=null;
			String MemberType=null;
			String formname=null;
			String unit1=null;
			
			Map md = model.asMap();
			specname = (String) md.get("specname");
			formname=(String)md.get("formname");
			unit1=(String)md.get("unit1");
			
			
			if(req.getParameter("committeescheduleid")!=null) {
				CommitteeScheduleId=req.getParameter("committeescheduleid");
			}
			else {
				CommitteeScheduleId=(String)md.get("committeescheduleid");
			}
			if(CommitteeScheduleId==null) {
				return "redirect:/RODScheduleMinutes.htm";
			}
			
			if(req.getParameter("membertype")!=null) {
				MemberType=req.getParameter("membertype");
			}
			else {
				MemberType=(String)md.get("membertype");
			}
			
			
			req.setAttribute("unit1", unit1);
			req.setAttribute("formname", formname);
			req.setAttribute("membertype",MemberType);
			req.setAttribute("specname", specname);
			req.setAttribute("committscheduleid", CommitteeScheduleId);
			req.setAttribute("rodscheduleeditdata", service.RODScheduleEditData(CommitteeScheduleId));
			req.setAttribute("minutesspeclist", committeeservice.CommitteeMinutesSpecList(CommitteeScheduleId));
			req.setAttribute("rodagendalist", committeeservice.AgendaList(CommitteeScheduleId));
			req.setAttribute("minutesoutcomelist", committeeservice.MinutesOutcomeList());
			req.setAttribute("minutesattachmentlist",committeeservice.MinutesAttachmentList(CommitteeScheduleId));
			req.setAttribute("rodscheduledata",committeeservice.CommitteeActionList(CommitteeScheduleId));
			req.setAttribute("filesize",file_size);
			req.setAttribute("MomAttachment", committeeservice.MomAttachmentFile(CommitteeScheduleId));
			
		}
		catch (Exception e) {
				e.printStackTrace(); logger.error(new Date() +"Inside RODScheduleMinutes.htm "+
		UserId,e);
		}		
		return "rod/RODScheduleMinutes";
	}
	
	@RequestMapping(value="RODMinutesViewAllDownload.htm")
	public void RODMinutesViewAllDownload(HttpServletRequest req, HttpSession ses, RedirectAttributes redir,HttpServletResponse res) throws Exception
	{

		String UserId=(String)ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeMinutesViewAllDownload.htm "+UserId);
		try
		{
			String committeescheduleid = req.getParameter("committeescheduleid");			
			Object[] scheduleeditdata=service.RODScheduleEditData(committeescheduleid);
			String projectid= scheduleeditdata[9].toString();
			
			if(projectid!=null && Integer.parseInt(projectid)>0)
				
			{
				req.setAttribute("projectdetails", committeeservice.projectdetails(projectid));
			}
			String divisionid= scheduleeditdata[16].toString();
			if(divisionid!=null && Integer.parseInt(divisionid)>0)
			{
				req.setAttribute("divisiondetails", committeeservice.DivisionData(divisionid));
			}
			String initiationid= scheduleeditdata[17].toString();
			if(initiationid!=null && Integer.parseInt(initiationid)>0)
			{
				req.setAttribute("initiationdetails", committeeservice.Initiationdetails(initiationid));
			}
			
			List<Object[]> actionlist= committeeservice.MinutesViewAllActionList(committeescheduleid);
			HashMap< String, ArrayList<Object[]>> actionsdata=new LinkedHashMap<String, ArrayList<Object[]>>();
			
			for(Object obj[] : actionlist) {
					
					ArrayList<Object[]> values=new ArrayList<Object[]>(); 
					for(Object obj1[] : actionlist ) {
						if(obj1[0].equals(obj[0])) {
							values.add(obj1);
						}
					}
					if(!actionsdata.containsKey(obj[0].toString())) {
						actionsdata.put(obj[0].toString(), values);
					}
				} 
			
			req.setAttribute("committeeminutesspeclist",committeeservice.CommitteeScheduleMinutes(committeescheduleid) );
			req.setAttribute("committeescheduleeditdata", scheduleeditdata);
			req.setAttribute("CommitteeAgendaList", committeeservice.AgendaList(committeescheduleid));
			req.setAttribute("committeeminutes",committeeservice.CommitteeMinutesSpecdetails());
			req.setAttribute("committeeminutessub",committeeservice.CommitteeMinutesSub());
			req.setAttribute("committeeinvitedlist", committeeservice.CommitteeAtendance(committeescheduleid,"1"));

			req.setAttribute("actionlist",  actionsdata);
			req.setAttribute("labdetails", committeeservice.LabDetails(scheduleeditdata[24].toString()));
			req.setAttribute("isprint", "Y");	
			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(scheduleeditdata[24].toString()));
			req.setAttribute("labInfo", committeeservice.LabDetailes(LabCode));
			String rodNameId=scheduleeditdata[0].toString();
			String scheduledate=scheduleeditdata[2].toString();
					
			List<Object[]> ActionDetails=service.RODActionDetails(rodNameId);
			List<Object[]> actionSubDetails=new ArrayList();
			if(ActionDetails.size()>0) {
				actionSubDetails=ActionDetails.stream().filter(i -> LocalDate.parse(i[9].toString()).isBefore(LocalDate.parse(scheduledate))).collect(Collectors.toList());
			}
			Set<Integer>committeeCount=new TreeSet<>();
			for(Object[]obj:ActionDetails) {
				committeeCount.add(Integer.parseInt(obj[7].toString()));
			}
			int count=0;
			Map<Integer,Integer>committeeCountMap= new HashMap<>();
			for(Integer i:committeeCount) {
				committeeCountMap.put(++count, i);
			}
			
			req.setAttribute("committeeCountMap", committeeCountMap);
			req.setAttribute("ActionDetails", actionSubDetails);
			
			req.setAttribute("meetingcount",service.RODMeetingNo(scheduleeditdata));
			
			String filename=scheduleeditdata[11].toString().replace("/", "-");
			
			
			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);
			req.setAttribute("flagforView","A");
			
			
			
			
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/rod/RODMinutesViewAll.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();
			
			HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"));

			req.setAttribute("tableactionlist",  actionsdata);
			req.setAttribute("committeescheduleeditdata",  scheduleeditdata);
	        CharArrayWriterResponse customResponse1 = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/committee/ActionDetailsTable.jsp").forward(req, customResponse1);
			String html1 = customResponse1.getOutput();        
	        
	        HtmlConverter.convertToPdf(html1,new FileOutputStream(path+File.separator+filename+"1.pdf")); 
	         
	        
	        PdfWriter pdfw=new PdfWriter(path +File.separator+ "merged.pdf");
	        PdfReader pdf1=new PdfReader(path+File.separator+filename+".pdf");
	        PdfReader pdf2=new PdfReader(path+File.separator+filename+"1.pdf");
	        
	        PdfDocument pdfDocument = new PdfDocument(pdf1, pdfw);	       	        
	        PdfDocument pdfDocument2 = new PdfDocument(pdf2);
	        PdfMerger merger = new PdfMerger(pdfDocument);
	        
	        merger.merge(pdfDocument2, 1, pdfDocument2.getNumberOfPages());
            
            pdfDocument2.close();
	        pdfDocument.close();
	        merger.close();
	        pdf2.close();
	        pdf1.close();	       
	        pdfw.close();
	    
	        res.setContentType("application/pdf");
	        res.setHeader("Content-disposition","inline;filename="+filename+".pdf");
	        File f=new File(path +File.separator+ "merged.pdf");
	         
	        
	        OutputStream out = res.getOutputStream();
			FileInputStream in = new FileInputStream(f);
			byte[] buffer = new byte[4096];
			int length;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			in.close();
			out.flush();
			out.close();
	       
	       
	        Path pathOfFile2= Paths.get( path+File.separator+filename+"1.pdf"); 
	        Files.delete(pathOfFile2);		
	        pathOfFile2= Paths.get( path+File.separator+filename+".pdf"); 
	        Files.delete(pathOfFile2);	
	        pathOfFile2= Paths.get(path +File.separator+ "merged.pdf"); 
	        Files.delete(pathOfFile2);	
	        
		}catch(Exception e) {	    		
			logger.error(new Date() +" Inside RODMinutesViewAllDownload.htm "+UserId, e);
			e.printStackTrace();
		}	
	}
	
	@RequestMapping(value = "IndustryPartnerRepListInvitations.htm", method = RequestMethod.GET)
	public @ResponseBody String industryPartnerRepListInvitations(HttpServletRequest req,HttpSession ses) throws Exception
	{
		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside IndustryPartnerRepListInvitations.htm"+ UserId);
		
		List<Object[]> industryPartnerRepList = new ArrayList<Object[]>();
		
		String industryPartnerId =req.getParameter("industryPartnerId");
	
		industryPartnerRepList = service.industryPartnerRepListInvitations(industryPartnerId,req.getParameter("scheduleid"));
	 
		Gson json = new Gson();
		return json.toJson(industryPartnerRepList);	
	}
}
