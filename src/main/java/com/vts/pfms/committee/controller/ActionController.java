package com.vts.pfms.committee.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.PdfMerger;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.font.FontProvider;
import com.vts.pfms.CharArrayWriterResponse;
import com.vts.pfms.FormatConverter;
import com.vts.pfms.committee.dao.ActionSelfDao;
import com.vts.pfms.committee.dto.ActionAssignDto;
import com.vts.pfms.committee.dto.ActionMainDto;
import com.vts.pfms.committee.dto.ActionSubDto;
import com.vts.pfms.committee.dto.CommitteeMinutesAttachmentDto;
import com.vts.pfms.committee.dto.MeetingExcelDto;
import com.vts.pfms.committee.dto.OldRfaUploadDto;
import com.vts.pfms.committee.dto.RfaActionDto;
import com.vts.pfms.committee.model.ActionAssign;
import com.vts.pfms.committee.model.ActionAttachment;
import com.vts.pfms.committee.model.ActionMain;
import com.vts.pfms.committee.model.RfaAssign;
import com.vts.pfms.committee.model.RfaInspection;
import com.vts.pfms.committee.service.ActionService;
import com.vts.pfms.committee.service.CommitteeService;
import com.vts.pfms.committee.service.RODService;
import com.vts.pfms.mail.MailConfigurationDto;
import com.vts.pfms.mail.MailService;
import com.vts.pfms.milestone.dto.MileEditDto;
import com.vts.pfms.requirements.service.RequirementService;
import com.vts.pfms.timesheet.service.TimeSheetService;
import com.vts.pfms.utils.InputValidator;
import com.vts.pfms.utils.PMSFileUtils;
import com.vts.pfms.utils.PMSLogoUtil;


@Controller
public class ActionController {
	
//	@Autowired
//	private Configuration configuration;
	
	@Value("${ApplicationFilesDrive}")
	String uploadpath;
	@Autowired
	Environment env;
	@Autowired
	ActionService service;
	
	@Autowired
	MailService mailService;
	
	@Autowired
	PMSFileUtils pmsFileUtils;
	
	@Value("${File_Size}")
	String file_size;
	
	@Autowired 
	CommitteeService committeservice;
	
	// Prudhvi - 13/03/2024
	@Autowired
	RODService rodservice;
	
	@Autowired
	PMSLogoUtil LogoUtil;
	
	@Autowired
	TimeSheetService timesheetservice;
	
	@Autowired
	RequirementService reqservice;
	
	FormatConverter fc=new FormatConverter();
	private SimpleDateFormat sdf2=fc.getRegularDateFormat();
	
	
	private  SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
	SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd");
	private static final Logger logger=LogManager.getLogger(ActionController.class);
	
	
	

private boolean isValidFileType(MultipartFile file) {
		
		
		
	    if (file == null || file.isEmpty()) {
	        return true; // nothing uploaded, so it's valid
	    }

	    String contentType = file.getContentType();
	    String originalFilename = file.getOriginalFilename();
	    
	  
	    if (contentType == null) {
	        return false;
	    }
	    
	 // Extract extension in lowercase
	    String extension = FilenameUtils.getExtension(originalFilename).toLowerCase();
	    
	 // Check mapping between MIME type and extension
	    switch (extension) {
	        case "pdf":
	            return contentType.equalsIgnoreCase("application/pdf");
	        case "jpeg":
	        case "jpg":
	            return contentType.equalsIgnoreCase("image/jpeg");
	        case "png":
	            return contentType.equalsIgnoreCase("image/png");
	        default:
	            return false;
	    }

//	    // Allow only images and PDF
//	 // Allowed MIME types
//	    boolean validMime = contentType.equalsIgnoreCase("application/pdf")
//	            || contentType.equalsIgnoreCase("image/jpeg")
//	            || contentType.equalsIgnoreCase("image/png");
//
//	    // Allowed extensions
//	    boolean validExtension = extension.equals("pdf")
//	            || extension.equals("jpeg")
//	            || extension.equals("jpg")
//	            || extension.equals("png");
//
//	    return validMime && validExtension;
	}

	
	
	
	@RequestMapping(value = "ActionLaunch.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String ActionLaunch(Model model, HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside ActionLaunch.htm "+UserId);		
		try {
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String Logintype= (String)ses.getAttribute("LoginType");
			String action=req.getParameter("Action");
			if(action!=null && "ReAssign".equalsIgnoreCase(action)) {
				String ActionAssignid = req.getParameter("ActionAssignid");
				String projectid = req.getParameter("ProjectId");
				Object[]  projectdata = service.GetProjectData(projectid);
				Object[]  actiondata = service.GetActionReAssignData(ActionAssignid);
				req.setAttribute("actiondata", actiondata);
				req.setAttribute("ProjectData", projectdata);
			}
			String onboard=req.getParameter("Onboarding");
			if(onboard==null) {
			Map md=model.asMap();
			onboard=(String)md.get("Onboard");
			}
			req.setAttribute("Onboarding", onboard);
			req.setAttribute("ProjectList", service.LoginProjectDetailsList(EmpId,Logintype,LabCode));  
			req.setAttribute("AssignedList", service.AssignedList(EmpId));
			req.setAttribute("EmployeeListModal", service.EmployeeList(LabCode));
			req.setAttribute("AllLabList", service.AllLabList());
			req.setAttribute("LabCode", LabCode);
			req.setAttribute("flag", req.getParameter("flag"));
			req.setAttribute("Empid", ((Long) ses.getAttribute("EmpId")).toString());
			req.setAttribute("projectid", req.getParameter("projectid"));
			req.setAttribute("committeeid", req.getParameter("committeeid"));
			req.setAttribute("meettingid", req.getParameter("meettingid"));
			return "action/ActionLaunch";
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside ActionLaunch.htm "+UserId, e);
			return "static/Error";
		}
		
	}

	@RequestMapping(value = "ActionStatus.htm" , method = RequestMethod.POST)
	public String ActionStatus (HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside ActionStatus.htm "+UserId);	
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside ActionStatus.htm "+UserId, e);
		}
		
		return "action/ActionStatus";
	}
	
	@RequestMapping(value = "ActionAssigneeEmpList.htm", method = RequestMethod.GET)
	public @ResponseBody String ActionAssigneeEmpList(HttpServletRequest req,HttpSession ses) throws Exception 
	{
		String UserId = (String)ses.getAttribute("Username");
		String clusterid =(String) ses.getAttribute("clusterid");
		logger.info(new Date() +" Inside ActionAssigneeEmpList.htm "+ UserId);
		
		List<Object[]> EmployeeList = new ArrayList<Object[]>();
		
		try {
			String CpLabCode = req.getParameter("LabCode");
			
			if(CpLabCode.trim().equalsIgnoreCase("@EXP")) 
			{
				EmployeeList = service.ClusterExpertsList();
			}
			else
			{
				String CpLabClusterId = service.LabInfoClusterLab(CpLabCode)[1].toString(); 
				
				if(Long.parseLong(clusterid) == Long.parseLong(CpLabClusterId)) 
				{
					EmployeeList = service.LabEmployeeList(CpLabCode.trim());
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside ActionAssigneeEmpList.htm "+UserId, e);
		}
		
		Gson json = new Gson();
		return json.toJson(EmployeeList);	
	}
	
	
	
	
	@RequestMapping(value = "ActionAssigneeEmployeeList.htm", method = RequestMethod.GET)
	public @ResponseBody String ActionAssigneeEmployeeList(HttpServletRequest req,HttpSession ses) throws Exception 
	{
		String UserId = (String)ses.getAttribute("Username");
		String clusterid =(String) ses.getAttribute("clusterid");
		logger.info(new Date() +" Inside ActionAssigneeEmployeeList.htm"+ UserId);
		
		List<Object[]> EmployeeList = new ArrayList<Object[]>();
		
		try {
			String CpLabCode = req.getParameter("LabCode");
			String mainid = req.getParameter("MainId");

			if(mainid!=null && mainid!="" && !"0".equalsIgnoreCase(mainid))
			{
				
				if(CpLabCode.trim().equalsIgnoreCase("@EXP")) 
				{
					EmployeeList = service.ClusterExpertsList();
					
				}else{
//					String CpLabClusterId = service.LabInfoClusterLab(CpLabCode)[1].toString(); 
//					if(true) 
//					{
						EmployeeList = service.LabEmployeeList(CpLabCode.trim());
//					}
				}
			}else {
				if(CpLabCode.trim().equalsIgnoreCase("@EXP") || CpLabCode.equalsIgnoreCase("Expert")) 
				{
					EmployeeList = service.ClusterExpertsList();
				}else{
//					String CpLabClusterId = service.LabInfoClusterLab(CpLabCode)[1].toString(); 
//					if(true) 
//					{
						EmployeeList = service.LabEmployeeList(CpLabCode.trim());
//					}
				}
				
			}

		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside ActionAssigneeEmployeeList.htm "+UserId, e);
		}
		
		Gson json = new Gson();
		return json.toJson(EmployeeList);	
	}
	
	@RequestMapping(value = "ActionDetailsAjax.htm", method = RequestMethod.GET)
	public @ResponseBody String ActionDetailsAjax(HttpServletRequest req, HttpSession ses) throws Exception {
		Gson json = new Gson();
		Object[] ActionDetails=null;
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside ActionDetailsAjax.htm "+UserId);		
		try {
			
			ActionDetails =   service.ActionDetailsAjax(req.getParameter("actionid"),req.getParameter("assignid") );
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside ActionDetailsAjax.htm "+UserId, e);
		}
		return json.toJson(ActionDetails);
	}
	
	
	
	@RequestMapping(value = "ActionSubmit.htm", method = RequestMethod.POST)
	public String ActionSubmit(HttpServletRequest req, HttpSession ses,
			RedirectAttributes redir, @RequestParam(value = "actionAttachment", required = false) MultipartFile file
)throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside ActionSubmit.htm "+UserId);	
		try {

			if(InputValidator.isContainsHTMLTags(req.getParameter("ActionItem"))) {
				//redir.addAttribute("resultfail", "Action Item should not contain HTML elements !");
				return  redirectWithError(redir,"ActionLaunch.htm","Action Item should not contain HTML elements !");
			}
			
			
			// 🔹 Validate file types
	        if (!isValidFileType(file) ) {

	            return redirectWithError(redir, "ActionLaunch.htm",
	                    "Invalid file type. Only PDF or Image files are allowed.");
	        }
			
			
			ActionMainDto mainDto=new ActionMainDto();
			
			mainDto.setMainId(req.getParameter("MainActionId"));
			mainDto.setActionItem(req.getParameter("ActionItem"));
			mainDto.setActionLinkId(req.getParameter("OldActionNoId"));
			mainDto.setProjectId(req.getParameter("ProjectId"));
			mainDto.setActionDate(req.getParameter("MainPDC"));
			mainDto.setScheduleMinutesId(req.getParameter("scheduleminutesid"));
			mainDto.setActionStatus("A");
			mainDto.setType(req.getParameter("Type"));
			mainDto.setPriority(req.getParameter("MainPriority"));
			mainDto.setCategory(req.getParameter("MainCategory"));
			
			if(req.getParameter("Atype")!=null && !req.getParameter("Atype").isEmpty() && req.getParameter("Atype")!=""  ) {
				mainDto.setActionType(req.getParameter("Atype"));
			}else {
				mainDto.setActionType("N");
			}
			mainDto.setLabName((String)ses.getAttribute("labcode"));
			mainDto.setActivityId("0");
			mainDto.setCreatedBy(UserId);
			mainDto.setMeetingDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			String actionlevel = req.getParameter("ActionLevel");
			
		
			
			if(actionlevel!=null && !actionlevel.isEmpty() &&  actionlevel !="") {
				long level = Long.parseLong(actionlevel)+1;
				mainDto.setActionLevel(level);
				
			}else {
				mainDto.setActionLevel(1L);
			}
			//changed on 06-11
			List<String> emp = new ArrayList<>();
			String [] assignee = req.getParameterValues("Assignee");
			for(String s : assignee) {
				emp.add(s);
			}
			mainDto.setActionParentId(req.getParameter("ActionParentid"));
			
			mainDto.setActionAttachment(file);
			ActionAssignDto assign = new ActionAssignDto();
			
			assign.setActionDate(req.getParameter("MainPDC"));
			assign.setAssigneeList(req.getParameterValues("Assignee"));
			assign.setAssignor((Long) ses.getAttribute("EmpId"));
			assign.setAssigneeLabCode(req.getParameter("AssigneeLabCode"));
			assign.setAssignorLabCode(LabCode);
			assign.setRevision(0);
//			assign.setActionFlag("N");		
			assign.setActionStatus("A");
			assign.setCreatedBy(UserId);
			assign.setIsActive(1);
			assign.setMeetingDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			assign.setPDCOrg(req.getParameter("MainPDC"));
			assign.setMultipleAssigneeList(emp);
			
		
		
			
			long count =service.ActionMainInsert(mainDto , assign);
			
		
			if (count > 0) {
				redir.addAttribute("result", "Action Added Successfully");
			} else {
				redir.addAttribute("resultfail", "Action Add Unsuccessful");
			}
		
			
			
			
		}catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside ActionSubmit.htm "+UserId, e);
		}
		return "redirect:/ActionLaunch.htm";
	}
	
	@RequestMapping(value = "AssigneeList.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String AssigneeList(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside AssigneeList.htm "+UserId);		
		try {
			
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();	
		List<Object[]>mainAssigneeList=service.AssigneeList(EmpId);
		List<Object[]>AssigneeListToday=new ArrayList<>();
		List<Object[]>remainingList=new ArrayList<>();
		List<Object[]>AssigneemainList=new ArrayList<>();
		if(mainAssigneeList.size()>0) {
			AssigneeListToday=mainAssigneeList.stream().filter(i -> i[4].toString().equalsIgnoreCase(LocalDate.now().toString())).collect(Collectors.toList());
			remainingList=mainAssigneeList.stream().filter(i -> !i[4].toString().equalsIgnoreCase(LocalDate.now().toString())).collect(Collectors.toList());
		}
		AssigneemainList.addAll(AssigneeListToday);
		AssigneemainList.addAll(remainingList);
		req.setAttribute("AssigneeList", mainAssigneeList);
		req.setAttribute("AssigneemainList", AssigneemainList);
		}catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside AssigneeList.htm "+UserId, e);
		}
		return "action/AssigneeList";
	}

	@RequestMapping(value = "ActionSubLaunch.htm", method = RequestMethod.POST)
	public String ActionSubLaunch(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside ActionSubLaunch.htm "+UserId);		
		try {		 
			
			String AssignerName=req.getParameter("Assigner");
			req.setAttribute("Assignee", service.AssigneeData(req.getParameter("ActionMainId") ,req.getParameter("ActionAssignid")).get(0));
			req.setAttribute("SubList", service.SubList(req.getParameter("ActionAssignid")));
			//req.setAttribute("LinkList", service.SubList(req.getParameter("ActionAssignid")));
			req.setAttribute("AssignerName", AssignerName);
			req.setAttribute("flag", req.getParameter("flag"));
			req.setAttribute("ActionPath", req.getParameter("ActionPath"));
			req.setAttribute("actiono", req.getParameter("ActionNo"));
			req.setAttribute("filesize",file_size);
			req.setAttribute("back", req.getParameter("back"));
			req.setAttribute("Empid", ((Long) ses.getAttribute("EmpId")).toString());
			req.setAttribute("projectid", req.getParameter("projectid"));
			req.setAttribute("committeeid", req.getParameter("committeeid"));
			req.setAttribute("meettingid", req.getParameter("meettingid"));
			req.setAttribute("fromDate", req.getParameter("fromDate"));
			req.setAttribute("toDate", req.getParameter("toDate"));
			req.setAttribute("empId", req.getParameter("empId"));
			req.setAttribute("type", req.getParameter("type"));
			req.setAttribute("status", req.getParameter("status"));
			
			req.setAttribute("AttachmentList", service.getActionMainAttachMent(req.getParameter("ActionMainId")));
			
			req.setAttribute("MeetingNumbr", req.getParameter("Meeting"));
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			logger.error(new Date() +" Inside ActionSubLaunch.htm "+UserId, e);
		}
		return "action/AssigneeUpdate";
	}
	
	@RequestMapping(value = "SubSubmit.htm", method = RequestMethod.POST)
	public String SubSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir,
			@RequestPart("FileAttach") MultipartFile FileAttach) throws Exception {
		
		String UserId  = (String) ses.getAttribute("Username");
		String labCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside SubSubmit.htm "+UserId);		
		try {
			
			String ccmActionFlag = req.getParameter("ccmActionFlag");
			
			
			
			// 🔹 Validate file types
	        if (!isValidFileType(FileAttach)) {

	        	if(ccmActionFlag!=null && ccmActionFlag.equalsIgnoreCase("Y")) {
	        		
	        		redir.addAttribute("committeeId", req.getParameter("committeeId"));
					redir.addAttribute("tabName", req.getParameter("tabName"));
					return redirectWithError(redir, "CCMPresentation.htm", "Invalid file type. Only PDF or Image files are allowed.");
	        	}else {
	        		
	        		redir.addFlashAttribute("ActionMainId", req.getParameter("ActionMainId"));
					redir.addFlashAttribute("ActionAssignId", req.getParameter("ActionAssignId"));
					redir.addFlashAttribute("Empid", ((Long) ses.getAttribute("EmpId")).toString());
					redir.addFlashAttribute("flag", req.getParameter("flag"));
					redir.addFlashAttribute("projectid", req.getParameter("projectid"));
	        		
	        		return redirectWithError(
	        			    redir,
	        			   "ActionSubLaunchRedirect.htm",
	        			    "Invalid file type. Only PDF or Image files are allowed."
	        			);

	        	}
	        }
			
			
			
			
			
			
			if(ccmActionFlag!=null && ccmActionFlag.equalsIgnoreCase("Y")) {
				redir.addAttribute("committeeId", req.getParameter("committeeId"));
				redir.addAttribute("tabName", req.getParameter("tabName"));
				
				return "redirect:/CCMPresentation.htm";
			}else {
				redir.addFlashAttribute("ActionMainId", req.getParameter("ActionMainId"));
				redir.addFlashAttribute("ActionAssignId", req.getParameter("ActionAssignId"));
				redir.addFlashAttribute("Empid", ((Long) ses.getAttribute("EmpId")).toString());
				redir.addFlashAttribute("flag", req.getParameter("flag"));
				redir.addFlashAttribute("projectid", req.getParameter("projectid"));
			}
			
			if(InputValidator.isContainsHTMLTags(req.getParameter("Remarks"))) {
				return redirectWithError(redir, "ActionSubLaunchRedirect.htm", "Remarks should not contain HTML Elements !");
			}
			
			
			ActionSubDto subDto=new ActionSubDto();
			subDto.setLabCode(labCode);
			subDto.setFileName(req.getParameter("FileName"));
			subDto.setFileNamePath(FileAttach.getOriginalFilename());
			subDto.setMultipartfile(FileAttach);
            subDto.setCreatedBy(UserId);
            subDto.setActionAssignId(req.getParameter("ActionAssignId"));
            subDto.setRemarks(req.getParameter("Remarks"));
            subDto.setProgress(req.getParameter("Progress"));
            subDto.setProgressDate(req.getParameter("AsOnDate"));
			Long count=service.ActionSubInsert(subDto);
            
			if (count > 0) {
				redir.addAttribute("result", "Action  Updated Successfully");
			} else {
				redir.addAttribute("resultfail", "Action Update Unsuccessful");
			}
			
		
			
		}
		catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside SubSubmit.htm "+UserId, e);
		}

		return "redirect:/ActionSubLaunchRedirect.htm";
	}
	
	@RequestMapping(value = "ActionSubLaunchRedirect.htm", method = RequestMethod.GET)
	public String ActionSubLaunchRedirect(Model model,HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside ActionSubLaunchRedirect.htm "+UserId);		
		try {
			  String MainId=null;
			  String AssignId=null;
			  String flag=null;
			  String projectid=null;
				 Map md = model.asMap();
				   
				    	
				    	MainId = (String) md.get("ActionMainId");
				    	AssignId = (String)md.get("ActionAssignId");
				    	flag = (String)md.get("flag");
				    	projectid = (String)md.get("projectid");
				    if(MainId==null|| AssignId==null) {
				    	 redir.addAttribute("resultfail", "Refresh Not Allowed");
				    	return "redirect:/AssigneeList.htm";
				    }
				
		    Object[] data=service.AssigneeData(MainId ,AssignId).get(0);
		    
		   
		     String AssignerName=data[1]+", "+data[2]; 		     
		     
		     
		     req.setAttribute("Assignee", data);
		     req.setAttribute("SubList", service.SubList(data[18].toString()));
		     req.setAttribute("AssignerName", AssignerName);
			 req.setAttribute("actiono",data[9].toString());
			 req.setAttribute("filesize",file_size);
			 req.setAttribute("flag", flag);
			 req.setAttribute("projectid", projectid);
			 List<Object[]> AssigneeDetails=service.AssigneeDetails(data[18].toString());
			 
			 if(AssigneeDetails.size()>0) 
			 {
				 req.setAttribute("AssigneeDetails", AssigneeDetails.get(0));
			 }else
			 {
				 req.setAttribute("AssigneeDetails", null);
			 }
		}
		catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside ActionSubLaunchRedirect.htm "+UserId, e);
		}
		return "action/AssigneeUpdate";
	}
	
	 @RequestMapping(value = "ActionAttachDownload.htm", method = RequestMethod.GET)
	 public void ActionAttachDownload(HttpServletRequest	 req, HttpSession ses, HttpServletResponse res) throws Exception 
	 {	 
		 String UserId = (String) ses.getAttribute("Username");
		 String labCode = (String)ses.getAttribute("labcode");
			logger.info(new Date() +"Inside ActionAttachDownload.htm "+UserId);		
			try { 
		 
				  ActionAttachment attachment=service.ActionAttachmentDownload(req.getParameter("ActionSubId" ));
				  Path pdfPath = Paths.get(uploadpath, labCode,"ActionData",attachment.getAttachName().toString());
				  File my_file=null;
//					my_file = new File(uploadpath+attachment.getAttachFilePath()+File.separator+attachment.getAttachName()); 
					my_file = pdfPath.toFile(); 
					res.setContentType("application/octet-stream");
					res.setHeader("Content-disposition","attachment; filename="+attachment.getAttachName().toString()); 
			        OutputStream out = res.getOutputStream();
			        FileInputStream in = new FileInputStream(my_file);
			        byte[] buffer = new byte[4096];
			        int length;
			        while ((length = in.read(buffer)) > 0){
			           out.write(buffer, 0, length);
			        }
			        in.close();
			        out.flush();
			        out.close();
			}
			catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside ActionAttachDownload.htm "+UserId, e);
			}
	 }
	 
	 @RequestMapping(value = "ActionDataAttachDownload.htm", method = RequestMethod.GET)
	 public void ActionDataAttachDownload(HttpServletRequest	 req, HttpSession ses, HttpServletResponse res) throws Exception 
	 {	 
		 String UserId = (String) ses.getAttribute("Username");
		 String labCode = (String)ses.getAttribute("labcode");
			logger.info(new Date() +"Inside ActionAttachDownload.htm "+UserId);		
			try { 
		 
					ActionAttachment attach=service.ActionAttachmentDownload(req.getParameter("ActionSubId" ));
					Path pdfPath = Paths.get(uploadpath,labCode,"ActionData",attach.getAttachName().toString());
					File my_file=null;
//					my_file = new File(uploadpath+attach.getAttachFilePath()+File.separator+attach.getAttachName()); 
					my_file = pdfPath.toFile(); 
					res.setContentType("Application/octet-stream");	
					res.setHeader("Content-disposition","attachment; filename="+attach.getAttachName().toString()); 
			        OutputStream out = res.getOutputStream();
			        FileInputStream in = new FileInputStream(my_file);
			        byte[] buffer = new byte[4096];
			        int length;
			        while ((length = in.read(buffer)) > 0){
			           out.write(buffer, 0, length);
			        }
			        in.close();
			        out.flush();
			        out.close();
 
			}
			catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside ActionAttachDownload.htm "+UserId, e);
			}
	 }
	 
	@RequestMapping(value = "ActionSubDelete.htm", method = RequestMethod.POST)
	public String TCCMemberDelete(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String labCode = (String)ses.getAttribute("labcode");
		String ActionSubId=req.getParameter("ActionSubId");
		String AttachMentId=req.getParameter("ActionAttachid");
		String Progress=null;
		String ProgressDate=null;
		String ProgressRemarks=null;
		
		logger.info(new Date() +"Inside ActionSubDelete.htm "+UserId);		
		try { 
			ActionAttachment attach = null;
			if(!AttachMentId.equalsIgnoreCase("null")) {
				attach=service.ActionAttachmentDownload(AttachMentId);	
				Path pdfPath = Paths.get(uploadpath,labCode,"ActionData",attach.getAttachName().toString());
				File my_file=pdfPath.toFile();
//				my_file = new File(uploadpath+attach.getAttachFilePath()+File.separator+attach.getAttachName()); 
				       
				if(my_file.exists()) {
					my_file.delete();
				}
			}
			int count = service.ActionSubDelete(ActionSubId, UserId);
			if (count > 0) {
				List<Object[]> Sublist=service.SubList(req.getParameter("ActionAssignId"));
				if(Sublist.size() > 0) {
					Progress=Sublist.get(Sublist.size()-1)[2].toString();
					ProgressDate=Sublist.get(Sublist.size()-1)[3].toString();
					ProgressRemarks=Sublist.get(Sublist.size()-1)[4].toString();
				}
				int result = service.ActionSubDeleteUpdate(req.getParameter("ActionAssignId"),Progress,ProgressDate,ProgressRemarks);
				redir.addAttribute("result", "Action Status Deleted Successfully");
			}
			else {
				redir.addAttribute("resultfail", "Action Status Delete Unsuccessful");
			}
			redir.addFlashAttribute("ActionMainId", req.getParameter("ActionMainId"));
			redir.addFlashAttribute("ActionAssignId",req.getParameter("ActionAssignId"));
			redir.addFlashAttribute("projectid",req.getParameter("projectid"));
			redir.addFlashAttribute("flag",req.getParameter("flag"));
			return "redirect:/ActionSubLaunchRedirect.htm";
		}catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside ActionSubDelete.htm "+UserId, e);
				return "static/Error";
		}
		}
	 
	 	@RequestMapping(value = "ActionForward.htm",method = {RequestMethod.GET, RequestMethod.POST})
		public String ActionForward(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
		 String UserId = (String) ses.getAttribute("Username");
		 String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		 String flag=req.getParameter("flag");
			logger.info(new Date() +"Inside ActionForward.htm "+UserId);		
			try {
				int count = service.ActionForward(req.getParameter("ActionMainId"),req.getParameter("ActionAssignId"), UserId);
	
				if (count > 0) {
					redir.addAttribute("result", "Action Forwarded Successfully");
				} else {
					redir.addAttribute("resultfail", "Action Forward Unsuccessful");
				}
				redir.addFlashAttribute("ActionAssignId", req.getParameter("ActionAssignId"));
				redir.addFlashAttribute("Type", req.getParameter("Type"));
			}
			catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside ActionForward.htm "+UserId, e);
			}
			
			if(!req.getParameter("projectid").equalsIgnoreCase("0") && !flag.equalsIgnoreCase("risk")) {
				redir.addAttribute("projectid", req.getParameter("projectid"));
				redir.addAttribute("committeeid", req.getParameter("committeeid"));
				redir.addAttribute("meettingid", req.getParameter("meettingid"));
				redir.addAttribute("EmpId", EmpId);
				return "redirect:/MeettingAction.htm";
			}else if(flag.equalsIgnoreCase("risk")){
				redir.addAttribute("projectid", req.getParameter("projectid"));
			     return "redirect:/ProjectRisk.htm";
			}else {
				 return "redirect:/AssigneeList.htm";
			}
		
		}
	 @RequestMapping(value = "ActionForwardList.htm", method = {RequestMethod.GET,RequestMethod.POST})
		public String ForwardList(Model model, HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
				throws Exception {
		 
		 	String UserId = (String) ses.getAttribute("Username");
		 	String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			logger.info(new Date() +"Inside ActionForwardList.htm "+UserId);		
			try { 
				
					String type = req.getParameter("Type");
					Map md = model.asMap();
					
					if(md.get("Type")!=null) {
						type = (String) md.get("Type");
					}
					if(type!=null && !type.equalsIgnoreCase("F")) {
						if(type.equalsIgnoreCase("A")) {
							req.setAttribute("ForwardList", service.ForwardList(EmpId));
						}else if (type.equalsIgnoreCase("NB")) {
							req.setAttribute("ForwardList", service.ForwardList(EmpId).stream().filter(flag-> flag[7].toString().equalsIgnoreCase("I") || flag[6].toString().equalsIgnoreCase("A") || flag[7].toString().equalsIgnoreCase("B")).collect(Collectors.toList()));
						}
						req.setAttribute("type", type);
					}else{
						req.setAttribute("ForwardList", service.ForwardList(EmpId).stream().filter(flag-> flag[6].toString().equalsIgnoreCase("F")).collect(Collectors.toList()));
						req.setAttribute("type", "F");
					}

			}
			catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside ActionForwardList.htm "+UserId, e);
			}
			return "action/ForwardList";
		}

	 @RequestMapping(value = "ForwardSub.htm", method = RequestMethod.POST)
		public String ForwardSub(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
				throws Exception {
		    String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside ForwardSub.htm "+UserId);		
			try { 
			String AssigneeName=req.getParameter("Assignee");
			req.setAttribute("actionno", req.getParameter("ActionNo"));
			req.setAttribute("Assignee", service.AssigneeData(req.getParameter("ActionMainId"),req.getParameter("ActionAssignId")).get(0));
			req.setAttribute("SubList", service.SubList(req.getParameter("ActionAssignId")));
			req.setAttribute("AssigneeName", AssigneeName);
			//req.setAttribute("LinkList", service.SubList(req.getParameter("ActionLinkId")));
			req.setAttribute("actionslist", service.ActionSubLevelsList(req.getParameter("ActionAssignId")));
			req.setAttribute("flag", req.getParameter("flag"));
			req.setAttribute("AttachmentList", service.getActionMainAttachMent(req.getParameter("ActionMainId"))); // 18-08

			}
			catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside ForwardSub.htm "+UserId, e);
			}
			return "action/ForwardSub";
		}
	
	 
	 @RequestMapping(value = "SendBackSubmit.htm", method = RequestMethod.POST)
		public String SendBackSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {

		 	String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside SendBackSubmit.htm "+UserId);	
			String back = req.getParameter("BACK");
			System.out.println("back"+back);
			long count=0;
			try { 
				
				if(InputValidator.isContainsHTMLTags(req.getParameter("Remarks"))) {
					return  redirectWithError(redir,"ActionForwardList.htm","Remarks should not contain HTML elements !");
				}
				 count =service.ActionSendBack(req.getParameter("ActionMainId"),req.getParameter("Remarks"), UserId,req.getParameter("ActionAssignId"));
	
				if(back!=null && "Issue".equalsIgnoreCase(back)) {
					if (count > 0) {
						redir.addAttribute("result", "Action Sent Back Successfully");
					} else {
						redir.addAttribute("resultfail", "Action SendBack Unsuccessful");
					}
					return "redirect:/ActionForwardList.htm";
				}else {
					if (count > 0) {
						redir.addAttribute("result", "Action Sent Back Successfully");
					} else {
						redir.addAttribute("resultfail", "Action SendBack Unsuccessful");
		
					}
					return "redirect:/ActionForwardList.htm";
				}

			}
			catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside SendBackSubmit.htm "+UserId, e);
					if(back!=null && "Issue".equalsIgnoreCase(back)) {
						redir.addAttribute("resultfail", "Issue SendBack Unsuccessful");
						return "redirect:/ActionForwardList.htm";
					}else {
						redir.addAttribute("resultfail", "Action SendBack Unsuccessful");
						return "redirect:/ActionForwardList.htm";
					}
					
			}
			

			

		}
	 
	 
	 	@RequestMapping(value = "CloseSubmit.htm", method = RequestMethod.POST)
		public String CloseSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {

		 	String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside CloseSubmit.htm "+UserId);		
			try { 
				
				String Remarks=req.getParameter("Remarks");
				if(InputValidator.isContainsHTMLTags(Remarks)) {
					redir.addAttribute("ActionMainId",req.getParameter("ActionMainId"));
        			redir.addAttribute("ActionAssignId",req.getParameter("ActionAssignId"));
        			redir.addAttribute("ActionPath",req.getParameter("ActionPath"));
					return redirectWithError(redir, "CloseAction.htm", "'Remarks' should not contain HTML Tags.!");
				}

				if(InputValidator.isContainsHTMLTags(req.getParameter("Remarks"))) {
					redir.addAttribute("ActionPath", req.getParameter("ActionPath"));
					redir.addAttribute("ActionAssignId", req.getParameter("ActionAssignId"));
					redir.addAttribute("ActionMainId", req.getParameter("ActionMainId"));
					redir.addAttribute("sub", req.getParameter("sub"));
					
					return redirectWithError(redir, "CloseAction.htm", "Remarks should not contain HTML elements!");
				}
				
				
				

				String levelcount = req.getParameter("LevelCount");
				long count = service.ActionClosed(req.getParameter("ActionMainId"),Remarks, UserId,req.getParameter("ActionAssignId") ,levelcount);
				
			  
			if (count > 0) {
				redir.addAttribute("result", "Action Closed Successfully");
			} else {
				redir.addAttribute("resultfail", "Action Closed Unsuccessful");

			}
			if ("C".equalsIgnoreCase(req.getParameter("sub"))) {
				return "redirect:/ActionLaunch.htm";
			}
			}catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside CloseSubmit.htm "+UserId, e);
			}
			if("F".equalsIgnoreCase(req.getParameter("sub"))) {
				redir.addAttribute("Type", "A");
			}

			return "redirect:/ActionForwardList.htm";
		}
	 
	 
	 	@RequestMapping(value = "ActionStatusList.htm")
		public String ActionStatusList(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	 	{
		 	String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside ActionStatusList.htm "+UserId);		
			try {
				 	FormatConverter fc=new FormatConverter();
					SimpleDateFormat sdf=fc.getRegularDateFormat();
					SimpleDateFormat sdf1=fc.getSqlDateFormat();
					
					String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
					String fdate=req.getParameter("fdate");
					String tdate=req.getParameter("tdate");
					if(fdate==null)
					{
						if(LocalDate.now().getMonthValue()<=3)
						{
							fdate=fc.getPreviousFinancialYearStartDateSqlFormat();
						}
						else if(LocalDate.now().getMonthValue()>=3)
						{
							fdate=fc.getFinancialYearStartDateSqlFormat();
						}
					}else
					{
						fdate=sdf1.format(sdf.parse(fdate));		    
					}
					
					if(tdate==null)
					{
						tdate=LocalDate.now().toString();
					}
					else 
					{
						tdate=sdf1.format(sdf.parse(tdate));				
					}

					req.setAttribute("tdate",tdate);
					req.setAttribute("fdate",fdate);
					req.setAttribute("StatusList", service.StatusList(EmpId,fdate,tdate));
			}catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside ActionStatusList.htm "+UserId, e);
			}
			return "action/ActionStatusList";
		}
	 
	@RequestMapping(value = "ActionList.htm", method = RequestMethod.GET)
	public String ActionList(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception 
	{
		 
		 	String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside ActionList.htm "+UserId);		
			try {
				String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
				req.setAttribute("ActionList", service.ActionList(EmpId));			
			}catch(Exception e){
				e.printStackTrace();
				logger.error(new Date() +" Inside ActionList.htm "+UserId, e);
		   }
			return "action/ActionList";
		}
	 
	 

		@RequestMapping(value="CommitteeAction.htm",method= {RequestMethod.GET,RequestMethod.POST})
		public String CommitteeAction(Model model,HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception{
			
		 	String UserId = (String) ses.getAttribute("Username");
		 	String LabCode = (String) ses.getAttribute("labcode");
			logger.info(new Date() +"Inside CommitteeAction.htm "+UserId);		
			try {
			
			String CommitteeScheduleId=null;
			String specname=null;
			String MinutesBack=null;
			String programmeId = req.getParameter("programmeId");
			if(req.getParameter("ScheduleId")!=null) {
				
				CommitteeScheduleId=req.getParameter("ScheduleId");
			}
			else {
				Map md=model.asMap();
				CommitteeScheduleId=(String)md.get("ScheduleId");
				MinutesBack=(String)md.get("minutesback");
			}
			if(CommitteeScheduleId==null) {
				return "redirect:/ActionList.htm";
			}
			
			Map md = model.asMap();
			specname = (String) md.get("specname");
			
			if(req.getParameter("minutesback")!=null) {
				MinutesBack=req.getParameter("minutesback");
			}
			// Prudhvi - 13/03/2024
			/* ------------------ start ----------------------- */
			Object[] committeescheduleeditdata = null;
			String rodflag = req.getParameter("rodflag");
			if(rodflag!=null && rodflag.equalsIgnoreCase("Y")) {
				req.setAttribute("rodflag", rodflag);
				committeescheduleeditdata = rodservice.RODScheduleEditData(CommitteeScheduleId);
			}else {
				committeescheduleeditdata = service.CommitteeScheduleEditData(CommitteeScheduleId);
			}
			/* ------------------ end ----------------------- */
			String projectid =committeescheduleeditdata[9].toString();
			
			List<Object[]> divisionList = service.divisionList();
		
			req.setAttribute("minutesback", MinutesBack);
			req.setAttribute("specname", specname);
			req.setAttribute("committeescheduleeditdata", committeescheduleeditdata);
			req.setAttribute("AllLabList", service.AllLabList());
			req.setAttribute("labcode", LabCode);
			req.setAttribute("EmpNameList", service.EmployeeList(LabCode));
			req.setAttribute("divisionList", divisionList);
			req.setAttribute("programmeId",programmeId);
			if(Long.parseLong(projectid)>0)
			{
				req.setAttribute("EmployeeList", service.ProjectEmpList(projectid));
			}else
			{
				req.setAttribute("EmployeeList", service.EmployeeList(LabCode));
			}
			req.setAttribute("committeescheduledata",service.CommitteeActionList(CommitteeScheduleId));
			
			String nonProject = req.getParameter("nonproject"); 
			if(nonProject!=null && nonProject.equalsIgnoreCase("Y")) {
				req.setAttribute("nonproject", nonProject);
			}else {
				req.setAttribute("nonproject", "N");
			}
			
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside CommitteeAction.htm "+UserId, e);
			}
			
			// CCM Handling
			String ccmFlag = req.getParameter("ccmFlag");
			if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {
				req.setAttribute("ccmScheduleId", req.getParameter("ScheduleId"));
				req.setAttribute("committeeMainId", req.getParameter("committeeMainId"));
				req.setAttribute("committeeId", req.getParameter("committeeId"));
				req.setAttribute("ccmFlag", ccmFlag);
			}
			
			// DMC Handling
			String dmcFlag = req.getParameter("dmcFlag");
			if(dmcFlag!=null && dmcFlag.equalsIgnoreCase("Y")) {
				req.setAttribute("committeeId", req.getParameter("committeeId"));
				req.setAttribute("dmcFlag", dmcFlag);
			}
			
			return "action/CommitteeScheduleActions";
		}
		
		
		@RequestMapping(value = "CommitteeActionSubmit.htm", method = RequestMethod.POST)
		public String CommitteeActionSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
				throws Exception {
			
			String UserId = (String) ses.getAttribute("Username");
			String LabCode = (String)ses.getAttribute("labcode");
//			String LabCode = "ADE";
			logger.info(new Date() +"Inside CommitteeActionSubmit.htm "+UserId);
			try {
				String multipleAssignee=req.getParameter("multipleAssignee");
				System.out.println("multipleAssignee"+multipleAssignee);
				String programmeId = req.getParameter("programmeId");
				
				
				//06-11
				List<Object[]> allEmployees = new ArrayList<>();
				if(multipleAssignee!=null) {
				allEmployees=service.getAllEmployees(multipleAssignee);
				}
				List<String>emp=new ArrayList<>();
				
				if(allEmployees.size()>0)
				{
					emp=allEmployees.stream().map(i -> i[0].toString()).collect(Collectors.toList());
				}
				
				String multipleDivisionAssignee = req.getParameter("multipleDivisionAssignee");
				List<Object[]> divisionEmployees = new ArrayList<Object[]>();
				if(multipleDivisionAssignee!=null) {
					divisionEmployees = service.getDivisionEmployeesList(multipleDivisionAssignee);
				}
				if(divisionEmployees.size()>0) {
					emp = divisionEmployees.stream().map(i -> i[0].toString()).collect(Collectors.toList());
				}
				
				String[]a=req.getParameterValues("Assignee");
				if(a!=null) {
					for(String s:a) {
						if(!emp.contains(s)) {
							emp.add(s);
						}
					}
					
				}
			String projectId  = req.getParameter("ProjectId");
			String nonproject = req.getParameter("nonproject");
				
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			
			ActionMainDto mainDto=new ActionMainDto();
			mainDto.setMainId(req.getParameter("MainActionId"));
			mainDto.setActionItem(req.getParameter("Item"));
			mainDto.setProjectId(projectId);
			mainDto.setActionLinkId(req.getParameter("OldActionNo"));
			mainDto.setScheduleMinutesId(req.getParameter("scheduleminutesid"));
			mainDto.setType(req.getParameter("Type"));
			mainDto.setPriority(req.getParameter("Priority"));
			mainDto.setCategory(req.getParameter("Category"));
			mainDto.setActionStatus("A");
			mainDto.setScheduleId(req.getParameter("ScheduleId"));
			// Prudhvi - 13/03/2024
			String rodflag = req.getParameter("rodflag");
			redir.addAttribute("rodflag", rodflag);
			mainDto.setActionType(rodflag!=null && rodflag.equalsIgnoreCase("Y")?"R":"S");
			
			mainDto.setActivityId("0");
			mainDto.setCreatedBy(UserId);
			mainDto.setMeetingDate(req.getParameter("meetingdate"));
			
			mainDto.setLabName(LabCode);
			String actionlevel = req.getParameter("ActionLevel");
			
			if(actionlevel !="" &&actionlevel!=null) {
				long level = Long.parseLong(actionlevel)+1;
				mainDto.setActionLevel(level);
				
			}else {
				mainDto.setActionLevel(1L);
			}
			mainDto.setActionParentId(req.getParameter("ActionParentid"));
			
		ActionAssignDto assign = new ActionAssignDto();
			
			assign.setActionDate(req.getParameter("meetingdate"));
			assign.setAssigneeList(req.getParameterValues("Assignee"));
			assign.setAssignor((Long) ses.getAttribute("EmpId"));
			assign.setAssigneeLabCode(req.getParameter("AssigneeLabCode"));
			assign.setAssignorLabCode(LabCode);
			assign.setRevision(0);
//			assign.setActionFlag("N");		
			assign.setActionStatus("A");
			assign.setCreatedBy(UserId);
			assign.setIsActive(1);
			assign.setPDCOrg(req.getParameter("DateCompletion"));
			assign.setMeetingDate(req.getParameter("meetingdate"));
			assign.setMultipleAssigneeList(emp);
			long count = 0;
			
//			if(LabCode.equalsIgnoreCase("ADE") && nonproject!=null && nonproject.equalsIgnoreCase("Y")) {
//				count = service.insertActionMainFornonProject(mainDto,assign);
//			}
//			else {
				count = service.ActionMainInsert(mainDto,assign);
//			}
			
			
			if (count > 0) {
				redir.addAttribute("result", "Action Assigned Successfully");
			} else {
				redir.addAttribute("resultfail", "Action Assign Unsuccessful");
			}
			redir.addAttribute("ScheduleId", req.getParameter("ScheduleId"));
			redir.addAttribute("specname", req.getParameter("specname"));
			redir.addAttribute("minutesback", req.getParameter("minutesback"));
			redir.addAttribute("committeescheduledata",service.CommitteeActionList(req.getParameter("ScheduleId")));
			redir.addAttribute("nonproject",nonproject);
			redir.addAttribute("programmeId",programmeId);
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside CommitteeActionSubmit.htm "+UserId, e);
			}
			
			// CCM Handling
			String ccmFlag = req.getParameter("ccmFlag");
			if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {
				redir.addAttribute("ccmScheduleId", req.getParameter("ScheduleId"));
				redir.addAttribute("committeeMainId", req.getParameter("committeeMainId"));
				redir.addAttribute("committeeId", req.getParameter("committeeId"));
				redir.addAttribute("ccmFlag", ccmFlag);
			}
			
			// DMC Handling
			String dmcFlag = req.getParameter("dmcFlag");
			if(dmcFlag!=null && dmcFlag.equalsIgnoreCase("Y")) {
				redir.addAttribute("committeeId", req.getParameter("committeeId"));
				redir.addAttribute("dmcFlag", dmcFlag);
			}
			
			return "redirect:/CommitteeAction.htm";
		}

		
		@RequestMapping(value = "ScheduleActionList.htm", method = RequestMethod.GET)
		public @ResponseBody String ItemDescriptionSearchLedger(HttpServletRequest req, HttpSession ses) throws Exception {
			Gson json = new Gson();
			List<Object[]> ItemDescriptionSearchLedger=new ArrayList<Object[]>();
			String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside ScheduleActionList.htm "+UserId);		
			try {
				ItemDescriptionSearchLedger = service.ScheduleActionList(req.getParameter("ScheduleMinutesId"));
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside ScheduleActionList.htm "+UserId, e);
			}
			
			return json.toJson(ItemDescriptionSearchLedger);

		}
		
		 @RequestMapping(value = "AgendaView.htm", method = RequestMethod.POST)
			public String AgendaView(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
			 
			String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside AgendaView.htm "+UserId);		
			try {
			
				req.setAttribute("Content",service.MeetingContent(req.getParameter("ActionMainId")).get(0) );
				
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside AgendaView.htm "+UserId, e);
			}
				return "action/AgendaView";

			}
		 
		 @RequestMapping(value = "ActionNoSearch.htm", method = RequestMethod.GET)
			public @ResponseBody String ActionNoSearch(HttpServletRequest req, HttpSession ses) throws Exception {

			 	
			 	List<Object[]> DisDesc = null;
			 	String UserId =(String)ses.getAttribute("Username");
				logger.info(new Date() +"Inside ActionNoSearch.htm "+UserId);		
				try {
					
					DisDesc = service.ActionNoSearch(req.getParameter("ActionSearch"));
				
				}
				catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside ActionNoSearch.htm "+UserId, e);
				}
				Gson json = new Gson();
				return json.toJson(DisDesc);

			}
		 
		 @RequestMapping(value = "ScheduleActionItem.htm", method = RequestMethod.GET)
			public @ResponseBody String ScheduleActionItem(HttpServletRequest req, HttpSession ses) throws Exception {
			
			 String UserId = (String) ses.getAttribute("Username");
				logger.info(new Date() +"Inside ScheduleActionItem.htm "+UserId);		
				Gson json = new Gson();			 
				String ItemDescriptionSearchLedger=null;
				try {
						ItemDescriptionSearchLedger =   service.ScheduleActionItem(req.getParameter("ScheduleMinutesId"));					
				}
				catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside ScheduleActionItem.htm "+UserId, e);
				}
				return json.toJson(ItemDescriptionSearchLedger);
			}
		 
		 
			@RequestMapping(value = "ActionReports.htm", method = RequestMethod.GET)
			public String ActionReports(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)	throws Exception 
			{
				String UserId =(String)ses.getAttribute("Username");
				String LabCode = (String)ses.getAttribute("labcode");
				logger.info(new Date() +"Inside ActionReports.htm "+UserId);		
				try {
					String Logintype= (String)ses.getAttribute("LoginType");
					String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
					
					req.setAttribute("Term", "N");
					req.setAttribute("Project", "A");
					req.setAttribute("Type", "A");
					req.setAttribute("ProjectList", service.LoginProjectDetailsList(EmpId, Logintype,LabCode));
					req.setAttribute("StatusList", service.ActionReports(EmpId,"N","A","A", LabCode));	
					//req.setAttribute("StatusList", service.ActionReportsNew(EmpId, "N", "A", "A", LabCode,Logintype));

				}
				catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside ActionReports.htm "+UserId, e);
				}

				return "action/ActionReports";
			}
			
//			@RequestMapping(value = "ActionReportSubmit.htm", method = RequestMethod.POST)
//			public String ActionReportSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
//					throws Exception {
//				String Logintype= (String)ses.getAttribute("LoginType");
//				String UserId =(String)ses.getAttribute("Username");
//				String LabCode = (String)ses.getAttribute("labcode");
//				logger.info(new Date() +"Inside ActionReportSubmit.htm "+UserId);		
//				try {
//				
//				String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
//				
//				String Project = "A";
//				if(req.getParameter("Project")!=null) {
//					Project = req.getParameter("Project");
//				}
//				String Type = "A";
//				if(req.getParameter("Type")!=null) {
//					Type = req.getParameter("Type");					
//				}
//				System.out.print("EmpId"+EmpId+"Term"+req.getParameter("Term")+"Project"+Project+"Type"+Type+"LabCode"+LabCode);
//				
//				
//				req.setAttribute("ProjectList",service.LoginProjectDetailsList(EmpId, Logintype,LabCode));
//				req.setAttribute("StatusList", service.ActionReports(EmpId,req.getParameter("Term"),Project,Type,LabCode));	
//				req.setAttribute("Term", req.getParameter("Term"));
//				req.setAttribute("Project",Project);
//				req.setAttribute("Type",Type);
//				}
//				catch (Exception e) {
//					e.printStackTrace();
//					logger.error(new Date() +" Inside ActionReportSubmit.htm "+UserId, e);
//				}			
//			
//				return "action/ActionReports";
//			}
//			
//			
			
			
			@RequestMapping(value = "ActionReportSubmit.htm", method = RequestMethod.POST)
			public String ActionReportSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
					throws Exception {
				String Logintype= (String)ses.getAttribute("LoginType");
				String UserId =(String)ses.getAttribute("Username");
				String LabCode = (String)ses.getAttribute("labcode");
				logger.info(new Date() +"Inside ActionReportSubmit.htm "+UserId);		
				try {
				
				String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
				
				String Project = "A";
				if(req.getParameter("Project")!=null) {
					Project = req.getParameter("Project");
				}
				String Type = "A";
				if(req.getParameter("Type")!=null) {
					Type = req.getParameter("Type");					
				}
				System.out.print("EmpId-"+EmpId+" Term- "+req.getParameter("Term")+" Project-"+Project+" Type-"+Type+" LabCode"+LabCode);
				String term=req.getParameter("Term");
				
				List<Object[]>StatusList=service.ActionReports(EmpId,req.getParameter("Term"),Project,Type,LabCode);
				
				//List<Object[]> StatusList = service.ActionReportsNew(EmpId, req.getParameter("Term"), Project, Type, LabCode,Logintype);

				if(term.equalsIgnoreCase("I") && StatusList.size()>0) {
					StatusList=StatusList.stream().filter(i -> Integer.parseInt(i[12].toString())>0).collect(Collectors.toList());
				}
				
				
				
				req.setAttribute("ProjectList",service.LoginProjectDetailsList(EmpId, Logintype,LabCode));
				req.setAttribute("StatusList", StatusList);	
				req.setAttribute("Term", req.getParameter("Term"));
				req.setAttribute("Project",Project);
				req.setAttribute("Type",Type);
				}
				catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside ActionReportSubmit.htm "+UserId, e);
				}			
			
				return "action/ActionReports";
			}
			@RequestMapping(value = "ActionSearch.htm", method = RequestMethod.GET)
			public String ActionSearch(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
					throws Exception {
				String UserId =(String)ses.getAttribute("Username");
				logger.info(new Date() +"Inside ActionSearch.htm "+UserId);		
				try {
					req.setAttribute("Position", "ASN");
				}
				catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside ActionSearch.htm "+UserId, e);
				}	
				
				return "action/ActionSearch";
			}
			
			@RequestMapping(value = "ActionSearchSubmit.htm", method = RequestMethod.POST)
			public String ActionSearchSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
					throws Exception {
				String UserId =(String)ses.getAttribute("Username");
				logger.info(new Date() +"Inside ActionSearchSubmit.htm "+UserId);		
				try {
				
					String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
								
					req.setAttribute("StatusList", service.ActionSearch(EmpId,req.getParameter("ActionNo"),req.getParameter("Position")));
					req.setAttribute("Position",req.getParameter("Position"));
				
				}
				catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside ActionSearchSubmit.htm "+UserId, e);
				}
				return "action/ActionSearch";
			}
			
			@RequestMapping(value = "ActionPDReports.htm", method = {RequestMethod.GET,RequestMethod.POST})
			public String ActionPDReports(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
					throws Exception {	
				String Logintype= (String)ses.getAttribute("LoginType");
				String UserId =(String)ses.getAttribute("Username");
				String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
				String LabCode = (String)ses.getAttribute("labcode");
				logger.info(new Date() +"Inside ActionPDReports.htm "+UserId);	
				List<Object[]>proList=service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
				try {
					String ProjectId=req.getParameter("ProjectId");
					if(ProjectId==null)
					{
						ProjectId=proList.get(0)[0].toString();
					}
						
					
					req.setAttribute("StatusList", service.LoginProjectDetailsList(EmpId,Logintype,LabCode));					
					req.setAttribute("ProjectId",ProjectId);
				}
				catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside ActionPDReports.htm "+UserId, e);
					return "static/Error";
				}
				return "action/ActionPDReports";
			}
			
			
			 @RequestMapping(value = "ActionCount.htm", method = RequestMethod.GET)
				public @ResponseBody String ActionCount(HttpServletRequest req, HttpSession ses) throws Exception {
				 Gson json = new Gson();
				 Object[] ItemDescriptionSearchLedger=null;
				 String UserId =(String)ses.getAttribute("Username");
					logger.info(new Date() +"Inside ActionCount.htm "+UserId);		
					try {
				 
					
					try {
					ItemDescriptionSearchLedger =   service.ActionCountList(req.getParameter("ProjectId")).get(0);
					}catch (Exception e) {
						
					}
					
					
					
					}
					catch (Exception e) {
						e.printStackTrace();
						logger.error(new Date() +" Inside ActionCount.htm "+UserId, e);
					}
					return json.toJson(ItemDescriptionSearchLedger);

				}
			 
				@RequestMapping(value = "ActionWiseReport.htm", method = RequestMethod.POST)
				public String ActionWiseReport(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
						throws Exception {
					String UserId =(String)ses.getAttribute("Username");
					logger.info(new Date() +"Inside ActionWiseReport.htm "+UserId);		
					try {
										
					req.setAttribute("StatusList", service.ActionWiseReports(req.getParameter("ActionType"),req.getParameter("ProjectId")));	
					req.setAttribute("ProjectId",req.getParameter("ProjectId"));
					req.setAttribute("ActionType",req.getParameter("ActionType"));
					}
					catch (Exception e) {
						e.printStackTrace();
						logger.error(new Date() +" Inside ActionWiseReport.htm "+UserId, e);
					}
				
					return "action/ActionWiseReports";
				} 
				
				
				@RequestMapping(value = "ActionPdcReport.htm", method = { RequestMethod.GET, RequestMethod.POST})
				public String ActionPdcReport(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
						throws Exception {
					String UserId =(String)ses.getAttribute("Username");
					String Logintype= (String)ses.getAttribute("LoginType");
					String LabCode = (String)ses.getAttribute("labcode");
					logger.info(new Date() +"Inside ActionPdcReport.htm "+UserId);		
					try {
					
					FormatConverter fc=new FormatConverter();
					Calendar c= Calendar.getInstance();
					c.add(Calendar.DATE, 30);
					Date d=c.getTime();
					String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
					String fdate=req.getParameter("fdate");
					String tdate=req.getParameter("tdate");
					String Emp=req.getParameter("EmpId");
					String Project=req.getParameter("Project");
					String Position=req.getParameter("Position");
					if(fdate==null)
					{
						    fdate=fc.getRegularDateFormat().format(new Date());
							tdate=fc.getRegularDateFormat().format(d);
							Emp="A";
							Project="A";Position="P";
						
					}
					req.setAttribute("tdate",tdate);
					req.setAttribute("fdate",fdate);
					
					req.setAttribute("ProjectList", service.LoginProjectDetailsList(EmpId,Logintype,LabCode));
					req.setAttribute("EmployeeList", service.EmployeeList(LabCode));
					req.setAttribute("Project",Project);
					req.setAttribute("Employee", Emp);
					req.setAttribute("Position",Position );
					req.setAttribute("StatusList", service.ActionPdcReports(Emp, Project, Position,fdate, tdate));
					
					}
					catch (Exception e) {
						e.printStackTrace();
						logger.error(new Date() +" Inside ActionPdcReport.htm "+UserId, e);
					}
					return "action/ActionPdcReport";
				}
				
				
				@RequestMapping(value = "ExtendPdc.htm", method = RequestMethod.POST)
				public String ExtendPdc(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception 
				{

//					 	String UserId = (String) ses.getAttribute("Username");
//						logger.info(new Date() +"Inside ExtendPdc.htm "+UserId);		
//						try { 
//
//							int count = service.ActionExtendPdc(req.getParameter("ActionMainId"),req.getParameter("ExtendPdc"), UserId ,req.getParameter("ActionAssignId"));
//	
//							if (count > 0) {
//								redir.addAttribute("result", "Action PDC Extended Successfully");
//							} else {
//								redir.addAttribute("resultfail", "Action PDC Extend Unsuccessful");
//	
//							}
//							String fwd=req.getParameter("froward");
//							if(fwd!=null && fwd.equalsIgnoreCase("Y")) {
//								return "redirect:/ActionForwardList.htm";
//							}
//						}
//						catch (Exception e) {
//								e.printStackTrace();
//								logger.error(new Date() +" Inside ExtendPdc.htm "+UserId, e);
//						}
//						return "redirect:/ActionLaunch.htm";
					
					String UserId = (String) ses.getAttribute("Username");
					logger.info(new Date() +"Inside ExtendPdc.htm "+UserId);		
					try { 

						int count = service.ActionExtendPdc(req.getParameter("ActionMainId"),req.getParameter("ExtendPdc"), UserId ,req.getParameter("ActionAssignId"));

						if (count > 0) {
							redir.addAttribute("result", "Action PDC Extended Successfully");
						} else {
							redir.addAttribute("resultfail", "Action PDC Extend Unsuccessful");

						}
						if(req.getParameter("flag")!=null && req.getParameter("flag").equalsIgnoreCase("A")) {
							redir.addAttribute("ActionMainId",req.getParameter("ActionMainId"));
		        			redir.addAttribute("ActionAssignId",req.getParameter("ActionAssignId"));
		        			redir.addAttribute("ActionPath",req.getParameter("ActionPath"));
		        			return "redirect:/CloseAction.htm";
						}
						
						
						String fwd=req.getParameter("froward");
						if(fwd!=null && fwd.equalsIgnoreCase("Y")) {
							return "redirect:/ActionForwardList.htm";
						}
					}
					catch (Exception e) {
							e.printStackTrace();
							logger.error(new Date() +" Inside ExtendPdc.htm "+UserId, e);
					}
					return "redirect:/ActionLaunch.htm";
				}
				 
					@RequestMapping(value = "CloseAction.htm", method = {RequestMethod.POST,RequestMethod.GET})
					public String CloseAction(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception 
					{
//						String UserId =(String)ses.getAttribute("Username");
//						logger.info(new Date() +"Inside CloseAction.htm "+UserId);		
//						try {
//						req.setAttribute("sub",req.getParameter("sub"));
//						req.setAttribute("ActionMainId",req.getParameter("ActionMainId"));
//						req.setAttribute("ActionAssignId",req.getParameter("ActionAssignId"));
//						req.setAttribute("Assignee", service.AssigneeData(req.getParameter("ActionMainId") , req.getParameter("ActionAssignId")).get(0));
//						req.setAttribute("actionslist", service.ActionSubLevelsList(req.getParameter("ActionAssignId")));
//						req.setAttribute("back", req.getParameter("back"));
//						}
//						catch (Exception e) {
//							e.printStackTrace();
//							logger.error(new Date() +" Inside CloseAction.htm "+UserId, e);
//						}
//					
//						return "action/CloseAction";
						String LabCode = (String) ses.getAttribute("labcode");
						String UserId =(String)ses.getAttribute("Username");
						logger.info(new Date() +"Inside CloseAction.htm "+UserId);	
						
						System.out.println(req.getParameter("ActionMainId")+ "--"+ req.getParameter("ActionAssignId"));
						try {
						req.setAttribute("sub",req.getParameter("sub"));
						req.setAttribute("ActionMainId",req.getParameter("ActionMainId"));
						req.setAttribute("ActionAssignId",req.getParameter("ActionAssignId"));
						req.setAttribute("Assignee", service.AssigneeData(req.getParameter("ActionMainId") , req.getParameter("ActionAssignId")).get(0));
						req.setAttribute("actionslist", service.ActionSubLevelsList(req.getParameter("ActionAssignId")));
						req.setAttribute("back", req.getParameter("back"));
						req.setAttribute("AllLabList", service.AllLabList());
						req.setAttribute("LabCode", LabCode);
						req.setAttribute("ActionPath", req.getParameter("ActionPath"));
						req.setAttribute("SubList", service.SubList(req.getParameter("ActionAssignId")));
						req.setAttribute("carsInitiationId", req.getParameter("carsInitiationId"));;
						req.setAttribute("carsSoCMilestoneId", req.getParameter("carsSoCMilestoneId"));
						}
						catch (Exception e) {
							e.printStackTrace();
							logger.error(new Date() +" Inside CloseAction.htm "+UserId, e);
						}
					
						return "action/CloseAction";
					} 	
		 
					@RequestMapping(value = "ActionSelfList.htm", method = {RequestMethod.GET,RequestMethod.POST})
					public String ActionSelf(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
					{	
						String UserId = (String) ses.getAttribute("Username");
						logger.info(new Date() +"Inside ActionSelfList.htm "+UserId);		
						try {
							String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
							req.setAttribute("AssignedList", service.ActionSelfList(EmpId));
						}catch(Exception e){
								e.printStackTrace();
								logger.error(new Date() +" Inside ActionSelfList.htm "+UserId, e);
						}
						return "action/ActionSelf";
					}	
					
					 @RequestMapping(value = "ActionDetails.htm", method = RequestMethod.POST)
						public String ActionDetails(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
								throws Exception {
						 String UserId = (String) ses.getAttribute("Username");
						 String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
							logger.info(new Date() +"Inside ActionDetails.htm "+UserId);		
							try { 
							String AssigneeName=req.getParameter("Assignee");
							
                            String status = req.getParameter("status");
                            
                            req.setAttribute("status", status);
							req.setAttribute("Assignee", service.SearchDetails(req.getParameter("ActionMainId"),req.getParameter("ActionAssignId")).get(0));
							req.setAttribute("SubList", service.SubList(req.getParameter("ActionAssignId")));
							req.setAttribute("AssigneeName", AssigneeName);
							req.setAttribute("LinkList", service.SubList(req.getParameter("ActionLinkId")));
							req.setAttribute("ActionNo", req.getParameter("ActionNo"));
							req.setAttribute("text", req.getParameter("text"));
							req.setAttribute("projectid", req.getParameter("projectid"));
							req.setAttribute("committeeid", req.getParameter("committeeid"));
							req.setAttribute("meettingid", req.getParameter("meettingid"));
							}
							catch (Exception e) {
									e.printStackTrace();
									logger.error(new Date() +" Inside ActionDetails.htm "+UserId, e);
							}
							return "action/ActionDetails";
						}
					 
					 @RequestMapping(value = "ActionWiseAllReport.htm", method = RequestMethod.POST)
						public String ActionWiseAllReport(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
								throws Exception {
							String UserId =(String)ses.getAttribute("Username");
							logger.info(new Date() +"Inside ActionWiseAllReport.htm "+UserId);		
							try {												
							String Logintype= (String)ses.getAttribute("LoginType");
							String ActionType=req.getParameter("ActionType");	
							String Type=req.getParameter("Type");	
							String ProjectId=req.getParameter("ProjectId");
							
							if(ActionType==null) {
								ActionType="NA";
							}
							if(Type==null) {
								Type="P";
							}
							if(ProjectId==null) {
								List<Object[]> projectlist = service.ProjectList();
								ProjectId=projectlist.get(0)[0].toString();
							}
	
						   List<Object[]> StatusList = service.ActionWiseAllReport(ProjectId);
						   
						   DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Adjust the pattern according to your date format
						   LocalDate currentDate = LocalDate.now();
						   // NA - New Action Lists
						   if(ActionType.equalsIgnoreCase("NA")) {
							   if(Type.equalsIgnoreCase("P")) {
								   StatusList = StatusList.stream().filter(e->!(e[10].toString().equalsIgnoreCase("C") || e[10].toString().equalsIgnoreCase("F"))
										         && e[11].toString().equalsIgnoreCase("N") && e[12].toString().equalsIgnoreCase("A")
										         && (e[14]!=null && Long.parseLong(e[14].toString())>0)).collect(Collectors.toList());
							   }else if(Type.equalsIgnoreCase("F")) {
								   StatusList = StatusList.stream().filter(e->e[10].toString().equalsIgnoreCase("F")
									         && e[11].toString().equalsIgnoreCase("N") && e[12].toString().equalsIgnoreCase("A")
									         && (e[14]!=null && Long.parseLong(e[14].toString())>0)).collect(Collectors.toList());
							   }else if(Type.equalsIgnoreCase("C")) {
								   StatusList = StatusList.stream().filter(e->e[10].toString().equalsIgnoreCase("C")
									         && e[11].toString().equalsIgnoreCase("N") && e[12].toString().equalsIgnoreCase("A")
									         && (e[14]!=null && Long.parseLong(e[14].toString())>0)).collect(Collectors.toList());
							   }else if(Type.equalsIgnoreCase("D")) {
								   StatusList = StatusList.stream().filter(e->!e[10].toString().equalsIgnoreCase("C")
									         && e[11].toString().equalsIgnoreCase("N") && e[12].toString().equalsIgnoreCase("A")
									         && LocalDate.parse(e[8].toString(), formatter).isBefore(currentDate)).collect(Collectors.toList());
							   }
							// MLA - Milestone Action Lists
						   }else if(ActionType.equalsIgnoreCase("MLA")) {
							   if(Type.equalsIgnoreCase("P")) {
								   StatusList = StatusList.stream().filter(e->!(e[10].toString().equalsIgnoreCase("C") || e[10].toString().equalsIgnoreCase("F"))
										         && (e[11].toString().equalsIgnoreCase("A") || e[11].toString().equalsIgnoreCase("B") || e[11].toString().equalsIgnoreCase("C")
										        	 || e[11].toString().equalsIgnoreCase("D") || e[11].toString().equalsIgnoreCase("E"))
										         && e[12].toString().equalsIgnoreCase("A") && (e[14]!=null && Long.parseLong(e[14].toString())>0)).collect(Collectors.toList());
							   }else if(Type.equalsIgnoreCase("F")) {
								   StatusList = StatusList.stream().filter(e->e[10].toString().equalsIgnoreCase("F")
									         && (e[11].toString().equalsIgnoreCase("A") || e[11].toString().equalsIgnoreCase("B") || e[11].toString().equalsIgnoreCase("C")
										        	 || e[11].toString().equalsIgnoreCase("D") || e[11].toString().equalsIgnoreCase("E"))
									         && e[12].toString().equalsIgnoreCase("A") && (e[14]!=null && Long.parseLong(e[14].toString())>0)).collect(Collectors.toList());
							   }else if(Type.equalsIgnoreCase("C")) {
								   StatusList = StatusList.stream().filter(e->e[10].toString().equalsIgnoreCase("C")
									         && (e[11].toString().equalsIgnoreCase("A") || e[11].toString().equalsIgnoreCase("B") || e[11].toString().equalsIgnoreCase("C")
										        	 || e[11].toString().equalsIgnoreCase("D") || e[11].toString().equalsIgnoreCase("E"))
									         && e[12].toString().equalsIgnoreCase("A") && (e[14]!=null && Long.parseLong(e[14].toString())>0)).collect(Collectors.toList());
							   }else if(Type.equalsIgnoreCase("D")) {
								   StatusList = StatusList.stream().filter(e->!e[10].toString().equalsIgnoreCase("C")
									         && (e[11].toString().equalsIgnoreCase("A") || e[11].toString().equalsIgnoreCase("B") || e[11].toString().equalsIgnoreCase("C")
										        	 || e[11].toString().equalsIgnoreCase("D") || e[11].toString().equalsIgnoreCase("E"))
									         && e[12].toString().equalsIgnoreCase("A") && LocalDate.parse(e[8].toString(), formatter).isBefore(currentDate)).collect(Collectors.toList());
							   }
							// MA - Meeting Action Lists
						   }else if(ActionType.equalsIgnoreCase("MA")) {
							   if(Type.equalsIgnoreCase("P")) {
								   StatusList = StatusList.stream().filter(e->!(e[10].toString().equalsIgnoreCase("C") || e[10].toString().equalsIgnoreCase("F"))
										         && e[11].toString().equalsIgnoreCase("S") && e[12].toString().equalsIgnoreCase("A")
										         && (e[14]!=null && Long.parseLong(e[14].toString())>0)).collect(Collectors.toList());
							   }else if(Type.equalsIgnoreCase("F")) {
								   StatusList = StatusList.stream().filter(e->e[10].toString().equalsIgnoreCase("F")
									         && e[11].toString().equalsIgnoreCase("S") && e[12].toString().equalsIgnoreCase("A")
									         && (e[14]!=null && Long.parseLong(e[14].toString())>0)).collect(Collectors.toList());
							   }else if(Type.equalsIgnoreCase("C")) {
								   StatusList = StatusList.stream().filter(e->e[10].toString().equalsIgnoreCase("C")
									         && e[11].toString().equalsIgnoreCase("S") && e[12].toString().equalsIgnoreCase("A")
									         && (e[14]!=null && Long.parseLong(e[14].toString())>0)).collect(Collectors.toList());
							   }else if(Type.equalsIgnoreCase("D")) {
								   StatusList = StatusList.stream().filter(e->!e[10].toString().equalsIgnoreCase("C")
									         && e[11].toString().equalsIgnoreCase("S") && e[12].toString().equalsIgnoreCase("A")
									         && LocalDate.parse(e[8].toString(), formatter).isBefore(currentDate)).collect(Collectors.toList());
							   }
							// RK - Risk Action Lists
						   }else if(ActionType.equalsIgnoreCase("RK")) {
							   if(Type.equalsIgnoreCase("P")) {
								   StatusList = StatusList.stream().filter(e->!(e[10].toString().equalsIgnoreCase("C") || e[10].toString().equalsIgnoreCase("F"))
										         && (e[11].toString().equalsIgnoreCase("N") || e[11].toString().equalsIgnoreCase("S")) && e[12].toString().equalsIgnoreCase("K")
										         && (e[14]!=null && Long.parseLong(e[14].toString())>0)).collect(Collectors.toList());
							   }else if(Type.equalsIgnoreCase("F")) {
								   StatusList = StatusList.stream().filter(e->e[10].toString().equalsIgnoreCase("F")
									         && (e[11].toString().equalsIgnoreCase("N") || e[11].toString().equalsIgnoreCase("S")) && e[12].toString().equalsIgnoreCase("K")
									         && (e[14]!=null && Long.parseLong(e[14].toString())>0)).collect(Collectors.toList());
							   }else if(Type.equalsIgnoreCase("C")) {
								   StatusList = StatusList.stream().filter(e->e[10].toString().equalsIgnoreCase("C")
									         && (e[11].toString().equalsIgnoreCase("N") || e[11].toString().equalsIgnoreCase("S")) && e[12].toString().equalsIgnoreCase("K")
									         && (e[14]!=null && Long.parseLong(e[14].toString())>0)).collect(Collectors.toList());
							   }else if(Type.equalsIgnoreCase("D")) {
								   StatusList = StatusList.stream().filter(e->!e[10].toString().equalsIgnoreCase("C")
									         && (e[11].toString().equalsIgnoreCase("N") || e[11].toString().equalsIgnoreCase("S")) && e[12].toString().equalsIgnoreCase("K")
									         && LocalDate.parse(e[8].toString(), formatter).isBefore(currentDate)).collect(Collectors.toList());
							   }
						   // IU- Issue Action Lists
						   }else if(ActionType.equalsIgnoreCase("IU")) {
							   if(Type.equalsIgnoreCase("P")) {
								   StatusList = StatusList.stream().filter(e->!(e[10].toString().equalsIgnoreCase("C") || e[10].toString().equalsIgnoreCase("F"))
										         && (e[11].toString().equalsIgnoreCase("N") || e[11].toString().equalsIgnoreCase("S")) && e[12].toString().equalsIgnoreCase("I")
										         && (e[14]!=null && Long.parseLong(e[14].toString())>0)).collect(Collectors.toList());
							   }else if(Type.equalsIgnoreCase("F")) {
								   StatusList = StatusList.stream().filter(e->e[10].toString().equalsIgnoreCase("F")
									         && (e[11].toString().equalsIgnoreCase("N") || e[11].toString().equalsIgnoreCase("S")) && e[12].toString().equalsIgnoreCase("I")
									         && (e[14]!=null && Long.parseLong(e[14].toString())>0)).collect(Collectors.toList());
							   }else if(Type.equalsIgnoreCase("C")) {
								   StatusList = StatusList.stream().filter(e->e[10].toString().equalsIgnoreCase("C")
									         && (e[11].toString().equalsIgnoreCase("N") || e[11].toString().equalsIgnoreCase("S")) && e[12].toString().equalsIgnoreCase("I")
									         && (e[14]!=null && Long.parseLong(e[14].toString())>0)).collect(Collectors.toList());
							   }else if(Type.equalsIgnoreCase("D")) {
								   StatusList = StatusList.stream().filter(e->!e[10].toString().equalsIgnoreCase("C")
									         && (e[11].toString().equalsIgnoreCase("N") || e[11].toString().equalsIgnoreCase("S")) && e[12].toString().equalsIgnoreCase("I")
									         && LocalDate.parse(e[8].toString(), formatter).isBefore(currentDate)).collect(Collectors.toList());
							   }
						   // RC - Recommendation Action Lists
						   }else if(ActionType.equalsIgnoreCase("RC")) {
							   if(Type.equalsIgnoreCase("P")) {
								   StatusList = StatusList.stream().filter(e->!(e[10].toString().equalsIgnoreCase("C") || e[10].toString().equalsIgnoreCase("F"))
										         && e[11].toString().equalsIgnoreCase("S") && e[12].toString().equalsIgnoreCase("R")
										         && (e[14]!=null && Long.parseLong(e[14].toString())>0)).collect(Collectors.toList());
							   }else if(Type.equalsIgnoreCase("F")) {
								   StatusList = StatusList.stream().filter(e->e[10].toString().equalsIgnoreCase("F")
									         && e[11].toString().equalsIgnoreCase("S") && e[12].toString().equalsIgnoreCase("R")
									         && (e[14]!=null && Long.parseLong(e[14].toString())>0)).collect(Collectors.toList());
							   }else if(Type.equalsIgnoreCase("C")) {
								   StatusList = StatusList.stream().filter(e->e[10].toString().equalsIgnoreCase("C")
									         && e[11].toString().equalsIgnoreCase("S") && e[12].toString().equalsIgnoreCase("R")
									         && (e[14]!=null && Long.parseLong(e[14].toString())>0)).collect(Collectors.toList());
							   }else if(Type.equalsIgnoreCase("D")) {
								   StatusList = StatusList.stream().filter(e->!e[10].toString().equalsIgnoreCase("C")
									         && e[11].toString().equalsIgnoreCase("S") && e[12].toString().equalsIgnoreCase("R")
									         && LocalDate.parse(e[8].toString(), formatter).isBefore(currentDate)).collect(Collectors.toList());
							   }
						   }

//							if(Logintype.equalsIgnoreCase("Y") || Logintype.equalsIgnoreCase("Z") || Logintype.equalsIgnoreCase("A") || Logintype.equalsIgnoreCase("C")|| 
//									Logintype.equalsIgnoreCase("I") || Logintype.equalsIgnoreCase("P") || Logintype.equalsIgnoreCase("D"))                             
//							{
//								req.setAttribute("StatusList",StatusList);
//							}
//							else if(Logintype.equalsIgnoreCase("P") )
//							{
//								String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
//								req.setAttribute("StatusList", service.ActionWiseAllReport(ActionType,EmpId,ProjectId));
//							}
							
							req.setAttribute("StatusList",StatusList);
							req.setAttribute("ProjectId",req.getParameter("ProjectId"));
							req.setAttribute("ActionType",req.getParameter("ActionType"));
							req.setAttribute("Type",req.getParameter("Type"));
							req.setAttribute("ProjectList", service.ProjectList());
							
							}
							catch (Exception e) {
								e.printStackTrace();
								logger.error(new Date() +" Inside ActionWiseAllReport.htm "+UserId, e);
							}						
							return "action/ActionWiseAllReports";
						} 
					 
					 
					 
	@RequestMapping(value = "ActionSelfReminderAdd.htm")
	public String ActionSelfAdd(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception {
		String UserId =(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside ActionSelfReminderAdd.htm "+UserId);		
		try {	
			FormatConverter fc=new FormatConverter();
			SimpleDateFormat sdf=fc.getRegularDateFormat();
			SimpleDateFormat sdf1=fc.getSqlDateFormat();
			
			String empid = ((Long) ses.getAttribute("EmpId")).toString();
			String fromdate = req.getParameter("fromdate");
			String todate = req.getParameter("todate");	
			
			if(fromdate==null)
			{				
				fromdate=LocalDate.now().minusDays(30).toString();
				todate=LocalDate.now().toString();
			}else
			{
				fromdate=sdf1.format(sdf.parse(fromdate));
				todate=sdf1.format(sdf.parse(todate));				
			}
			
			req.setAttribute("empid", empid);
			req.setAttribute("actionselflist", service.ActionSelfReminderList(empid,fromdate,todate));
						
			fromdate=sdf.format(sdf1.parse(fromdate));
			todate=sdf.format(sdf1.parse(todate));
			
			
			req.setAttribute("todate",todate);
			req.setAttribute("fromdate",fromdate);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside ActionSelfReminderAdd.htm "+UserId, e);
		}	
		return "action/ActionSelfAdd";
	}
	
	
	@RequestMapping(value = "ActionSelfReminderAddSubmit.htm")
	public String ActionSelfAddSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception {
		String UserId =(String)ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside ActionSelfReminderAddSubmit.htm "+UserId);		
		try {	
			String empid=req.getParameter("empid");
			String actiondate=req.getParameter("actiondate");
			String actiontime=req.getParameter("actiontime");
			String actiontype=req.getParameter("actiontype");
			String actionitem=req.getParameter("actionitem");
			ActionSelfDao actionselfdao=new ActionSelfDao();
			actionselfdao.setActionDate(actiondate);
			actionselfdao.setActionTime(actiontime);
			actionselfdao.setActionType(actiontype);
			actionselfdao.setEmpId(empid);
			actionselfdao.setActionItem(actionitem);
			actionselfdao.setCreatedBy(UserId);
			actionselfdao.setLabCode(LabCode);
			long count=0;
			count=service.ActionSelfReminderAddSubmit(actionselfdao);
			if (count > 0) {
				redir.addAttribute("result", "Reminder Added Successfully");
			} else {
				redir.addAttribute("resultfail", "Reminder Add Unsuccessful");
				
				return "redirect:/CommitteeList.htm";
			}			
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside ActionSelfReminderAddSubmit.htm "+UserId, e);
		}	
		return "redirect:/ActionSelfReminderAdd.htm";
	}
				
	@RequestMapping(value = "ActionSelfReminderDelete.htm")
	public String ActionSelfReminderDelete(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception {
		String UserId =(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside ActionSelfReminderDelete.htm "+UserId);		
		try {	
			int count=0;
			count=service.ActionSelfReminderDelete(req.getParameter("actionid"));
			if (count > 0) {
				redir.addAttribute("result", "Reminder Removed Successfully");
			} else {
				redir.addAttribute("resultfail", "Reminder Removal Unsuccessful");			
			}	
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside ActionSelfReminderDelete.htm "+UserId, e);
		}	
		return "redirect:/ActionSelfReminderAdd.htm";
	}
		
	@RequestMapping(value = "MilActionSubmit.htm", method = RequestMethod.POST)
	public String MilActionSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
			throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside MilActionSubmit.htm "+UserId);		
		try {
		
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			redir.addFlashAttribute("MilestoneActivityId", req.getParameter("MilestoneActivityId"));
			redir.addFlashAttribute("ActivityId", req.getParameter("ActivityId"));
			redir.addFlashAttribute("ProjectId", req.getParameter("ProjectId"));
			redir.addFlashAttribute("ActivityType", req.getParameter("ActivityType"));
			
			ActionMainDto mainDto=new ActionMainDto();
			mainDto.setLabName(LabCode);
			mainDto.setMainId(req.getParameter("MainActionId"));
			mainDto.setActionItem(req.getParameter("Item"));
			mainDto.setProjectId(req.getParameter("ProjectId"));
			mainDto.setActionLinkId(req.getParameter("OldActionNo"));
			mainDto.setActionDate(req.getParameter("DateCompletion"));
			mainDto.setScheduleMinutesId(req.getParameter("MilestoneActivityId"));
			mainDto.setActionType("A");
			mainDto.setActionStatus("A");
			mainDto.setCategory(req.getParameter("Category"));
			mainDto.setPriority(req.getParameter("Priority"));
			mainDto.setActivityId(req.getParameter("ActivityId"));
			mainDto.setType("A");
			mainDto.setCreatedBy(UserId);
			mainDto.setMeetingDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			String actionlevel = req.getParameter("ActionLevel");
			if(actionlevel!=null) {
				long level = Long.parseLong(actionlevel)+1;
				mainDto.setActionLevel(level);
				
			}else{
				mainDto.setActionLevel(1L);
			}
			//changed on 06-11
			List<String>emp=new ArrayList<>();
			String[] Assignee=req.getParameterValues("Assignee");
			for(String s:Assignee) {
				emp.add(s);
			}
			ActionAssignDto assign = new ActionAssignDto();
			
			assign.setActionDate(req.getParameter("DateCompletion"));
			assign.setAssigneeList(req.getParameterValues("Assignee"));
			assign.setAssignor((Long) ses.getAttribute("EmpId"));
			assign.setAssigneeLabCode(req.getParameter("AssigneeLabCode"));
			assign.setAssignorLabCode(LabCode);
			assign.setRevision(0);
//			assign.setActionFlag("N");		
			assign.setActionStatus("A");
			assign.setCreatedBy(UserId);
			assign.setPDCOrg(req.getParameter("DateCompletion"));
			assign.setMultipleAssigneeList(emp);
			long count =service.ActionMainInsert(mainDto , assign);
	
			if (count > 0) {
				redir.addAttribute("result", "Action Added Successfully ");
			} else {
				redir.addAttribute("resultfail", "Action Add Unsuccessful");
			}
		
		
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside MilActionSubmit.htm "+UserId, e);
		}
	
	
	return "redirect:/MA-UpdateRedirect.htm";
	}	
	
	
	@RequestMapping(value = "submitMessage.htm")
	public void submitMessage(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception {
		String UserId =(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside submitMessage.htm "+UserId);		
		try {	
			// Url that will be called to submit the message
			URL sendUrl = new URL("https://www.smsidea.co.in/smsstatuswithid.aspx");
			HttpURLConnection httpConnection = (HttpURLConnection) sendUrl
			.openConnection();
			// This method sets the method type to POST so that will be send as a POST
			httpConnection.setRequestMethod("POST");
			// This method is set as true wince we intend to send input to the server.
			httpConnection.setDoInput(true);
			// This method implies that we intend to receive data from server.
			httpConnection.setDoOutput(true);
			// Implies do not use cached data
			httpConnection.setUseCaches(false);
			// Data that will be sent over the stream to the server.
			DataOutputStream dataStreamToServer = new DataOutputStream(
			httpConnection.getOutputStream());
			dataStreamToServer.writeBytes("mobile="
			+ URLEncoder.encode("9343146866", "UTF-8") + "&pass="
			+ URLEncoder.encode("Vts@12345", "UTF-8") + "&senderid="
			+ URLEncoder.encode("TSTMSG", "UTF-8") + "&to="
			+ URLEncoder.encode("8763259755", "UTF-8") + "&msg="
			+ URLEncoder.encode("Hi , You have a meeting at 8pm", "UTF-8"));
			dataStreamToServer.flush();
			dataStreamToServer.close();
			// Here take the output value of the server.
			BufferedReader dataStreamFromUrl = new BufferedReader(
			new InputStreamReader(httpConnection.getInputStream()));
			String dataFromUrl = "", dataBuffer = "";
			// Writing information from the stream to the buffer
			while ((dataBuffer = dataStreamFromUrl.readLine()) != null) {
			dataFromUrl += dataBuffer;
			}
			/**
			* Now dataFromUrl variable contains the Response received from the
			* server so we can parse the response and process it accordingly.
			*/
			dataStreamFromUrl.close();	
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside submitMessage.htm "+UserId, e);
		}	
		
	}
	
	@RequestMapping(value = "AlertExcelFile.htm", method = RequestMethod.GET)
	public void AlertExcelFile(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception 
	{

		String Username = (String) ses.getAttribute("Username");
		
		logger.info(new Date() +"Inside  AlertExcelFile.htm "+Username);
			try {
				String name="NoData";
				String header="NoData";
			List<Object[]> bookData= service.getActionAlertList();
			if(bookData!=null && bookData.size()>0) {
			name="ActionMeetingAlertList"+new SimpleDateFormat("ddMMyyyy").format(new Date())+".csv";
			header="Action Alert List";
			}
			Workbook  wb=new XSSFWorkbook();  
	
	
			
			Sheet sheet=wb.createSheet("ActionAlertExcel"); 
		     int rowCount=0;
		     
		     
	
				
		
				  
				  	CellStyle heading = wb.createCellStyle();
				  	Font headingfont = wb.createFont();
				  	headingfont.setColor(IndexedColors.LIGHT_BLUE.getIndex());
				  	headingfont.setUnderline(HSSFFont.U_SINGLE);
				  	headingfont.setBold(true);
				  	headingfont.setFontHeightInPoints((short)15);
				  	heading.setFont(headingfont);
				  	heading.setAlignment(HorizontalAlignment.CENTER);
				  	
				  	CellStyle center = wb.createCellStyle();
				  	center.setAlignment(HorizontalAlignment.CENTER);
				  	
				  	CellStyle blue = wb.createCellStyle();
				  	Font bluefont = wb.createFont();
				  	bluefont.setColor(IndexedColors.BLUE.getIndex());
				  	blue.setFont(bluefont);
				  	blue.setAlignment(HorizontalAlignment.CENTER);
				  	blue.setBorderLeft(BorderStyle.THIN);
				  	blue.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				  	blue.setBorderRight(BorderStyle.THIN);
				  	blue.setRightBorderColor(IndexedColors.BLACK.getIndex());
				  	
				  	CellStyle red = wb.createCellStyle();
				  	Font redfont = wb.createFont();
				  	redfont.setColor(IndexedColors.RED.getIndex());
				  	red.setAlignment(HorizontalAlignment.CENTER);
				  	red.setFont(redfont);
				  	red.setBorderLeft(BorderStyle.THIN);
				  	red.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				  	red.setBorderRight(BorderStyle.THIN);
				  	red.setRightBorderColor(IndexedColors.BLACK.getIndex());
				  	
				  	CellStyle brown = wb.createCellStyle();
				  	Font brownfont = wb.createFont();
				  	brownfont.setColor(IndexedColors.BROWN.getIndex());
				  	brown.setFont(brownfont);
					CellStyle rhs = wb.createCellStyle();
				    rhs.setAlignment(HorizontalAlignment.RIGHT);
				    rhs.setBorderBottom(BorderStyle.THIN);
				    rhs.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				    rhs.setBorderLeft(BorderStyle.THIN);
				    rhs.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				    rhs.setBorderRight(BorderStyle.THIN);
				    rhs.setRightBorderColor(IndexedColors.BLACK.getIndex());
				    rhs.setBorderTop(BorderStyle.THIN);
				    rhs.setTopBorderColor(IndexedColors.BLACK.getIndex());
				  	CellStyle  wrapname	 = wb.createCellStyle();
				  	wrapname.setWrapText(true);
				  	wrapname.setBorderBottom(BorderStyle.THIN);
				  	wrapname.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				  	wrapname.setBorderLeft(BorderStyle.THIN);
				  	wrapname.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				  	wrapname.setBorderRight(BorderStyle.THIN);
				  	wrapname.setRightBorderColor(IndexedColors.BLACK.getIndex());
				  	wrapname.setBorderTop(BorderStyle.THIN);
				  	wrapname.setTopBorderColor(IndexedColors.BLACK.getIndex());
				  	String clr="33FFFF";
				    byte[] rgbB = Hex.decodeHex(clr); // get byte array from hex string
				    XSSFColor color = new XSSFColor(rgbB, null);
				    XSSFCellStyle aqua = (XSSFCellStyle)  wb.createCellStyle();
				  	aqua.setFillForegroundColor(color);
				  	aqua.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				  	aqua.setBorderBottom(BorderStyle.THIN);
				  	aqua.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				  	aqua.setBorderLeft(BorderStyle.THIN);
				  	aqua.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				  	aqua.setBorderRight(BorderStyle.THIN);
				  	aqua.setRightBorderColor(IndexedColors.BLACK.getIndex());
				  	aqua.setBorderTop(BorderStyle.THIN);
				  	aqua.setTopBorderColor(IndexedColors.BLACK.getIndex());
				  	
				  	CellStyle  wrap	 = wb.createCellStyle();
				  	wrap.setVerticalAlignment(VerticalAlignment.TOP);
				  	wrap.setAlignment(HorizontalAlignment.CENTER);
				  	wrap.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
				  	wrap.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				  	wrap.setBorderBottom(BorderStyle.THIN);
				  	wrap.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			        wrap.setBorderLeft(BorderStyle.THIN);
			        wrap.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			        wrap.setBorderRight(BorderStyle.THIN);
			        wrap.setRightBorderColor(IndexedColors.BLACK.getIndex());
			        wrap.setBorderTop(BorderStyle.THIN);
			        wrap.setTopBorderColor(IndexedColors.BLACK.getIndex());
				  	wrap.setWrapText(true);
				  	
				  	CellStyle  wrapcontent	 = wb.createCellStyle();
				  	wrapcontent.setVerticalAlignment(VerticalAlignment.TOP);
				  	wrapcontent.setWrapText(true);
	
				  	
				  	
				  	CellStyle  vertical	 = wb.createCellStyle();
				  	vertical.setBorderBottom(BorderStyle.THIN);
				  	vertical.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				  	vertical.setBorderLeft(BorderStyle.THIN);
				  	vertical.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				  	vertical.setBorderRight(BorderStyle.THIN);
				  	vertical.setRightBorderColor(IndexedColors.BLACK.getIndex());
				  	vertical.setBorderTop(BorderStyle.THIN);
				  	vertical.setTopBorderColor(IndexedColors.BLACK.getIndex());
				  	vertical.setVerticalAlignment(VerticalAlignment.TOP);
				  	
	//			  	Center and Border Style
				  	CellStyle  cb = wb.createCellStyle();
				  	cb.setBorderBottom(BorderStyle.THIN);
				  	cb.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				  	cb.setBorderLeft(BorderStyle.THIN);
				  	cb.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				  	cb.setBorderRight(BorderStyle.THIN);
				  	cb.setRightBorderColor(IndexedColors.BLACK.getIndex());
				  	cb.setBorderTop(BorderStyle.THIN);
				  	cb.setTopBorderColor(IndexedColors.BLACK.getIndex());
				  	cb.setAlignment(HorizontalAlignment.CENTER);
				  	cb.setVerticalAlignment(VerticalAlignment.TOP);
	
				  	
	//				Side Borders
				  	
				  	CellStyle  sb = wb.createCellStyle();
				  	sb.setBorderLeft(BorderStyle.THIN);
				  	sb.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				  	sb.setBorderRight(BorderStyle.THIN);
				  	sb.setRightBorderColor(IndexedColors.BLACK.getIndex());
				  	sb.setAlignment(HorizontalAlignment.CENTER);
				  	
				  	CellStyle  lsb = wb.createCellStyle();
				  	lsb.setBorderLeft(BorderStyle.THIN);
				  	lsb.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				  	lsb.setAlignment(HorizontalAlignment.CENTER);
				  	
				  	CellStyle  rsb = wb.createCellStyle();
				  	rsb.setBorderRight(BorderStyle.THIN);
				  	rsb.setRightBorderColor(IndexedColors.BLACK.getIndex());
				  	
					CellStyle  bsb = wb.createCellStyle();
					bsb.setBorderBottom(BorderStyle.THIN);
				  	bsb.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				  	bsb.setFont(redfont);
				  	
				  	CellStyle  top = wb.createCellStyle();
				  	top.setBorderTop(BorderStyle.THIN);
				  	top.setTopBorderColor(IndexedColors.BLACK.getIndex());
				  	top.setFont(redfont);
				  	top.setAlignment(HorizontalAlignment.CENTER);
				  	
				  	CellStyle  leftandside = wb.createCellStyle();
				  	leftandside.setBorderRight(BorderStyle.THIN);
				  	leftandside.setRightBorderColor(IndexedColors.BLACK.getIndex());
				  	leftandside.setBorderBottom(BorderStyle.THIN);
				  	leftandside.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				  	leftandside.setAlignment(HorizontalAlignment.CENTER);
				  	
				  	CellStyle  bnt = wb.createCellStyle();
				  	bnt.setBorderBottom(BorderStyle.THIN);
				  	bnt.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				  	bnt.setBorderTop(BorderStyle.THIN);
				  	bnt.setTopBorderColor(IndexedColors.BLACK.getIndex());
				  	
				  	CellStyle  bns = wb.createCellStyle();
				  	bns.setBorderBottom(BorderStyle.THIN);
				  	bns.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				  	bns.setBorderRight(BorderStyle.THIN);
				  	bns.setRightBorderColor(IndexedColors.BLACK.getIndex());
				  	bns.setBorderLeft(BorderStyle.THIN);
				  	bns.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				  	
				  	CellStyle  tns = wb.createCellStyle();
				  	tns.setBorderTop(BorderStyle.THIN);
				  	tns.setTopBorderColor(IndexedColors.BLACK.getIndex());
				  	tns.setBorderRight(BorderStyle.THIN);
				  	tns.setRightBorderColor(IndexedColors.BLACK.getIndex());
				  	tns.setBorderLeft(BorderStyle.THIN);
				  	tns.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				  	tns.setAlignment(HorizontalAlignment.CENTER);
				  	tns.setVerticalAlignment(VerticalAlignment.TOP);
				  	
				  	
	
	//				Content zero
				  	CellStyle  zero = wb.createCellStyle();
				  	zero.setFont(redfont);	
				  	zero.setBorderLeft(BorderStyle.THIN);
				  	zero.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				  	zero.setBorderRight(BorderStyle.THIN);
				  	zero.setRightBorderColor(IndexedColors.BLACK.getIndex());
				  	zero.setBorderTop(BorderStyle.THIN);
				  	zero.setTopBorderColor(IndexedColors.BLACK.getIndex());
				  	zero.setAlignment(HorizontalAlignment.RIGHT);
				  	zero.setVerticalAlignment(VerticalAlignment.TOP);
				    //Row cell = sheet.createRow(rowCount);
				    //cell.setHeightInPoints(80);
				          
			      
				  	 Row row10 = sheet.createRow(rowCount);
				        Cell r10cell1 = row10.createCell(0);
				        Cell r10cell2 = row10.createCell(1);
				        Cell r10cell3 = row10.createCell(2);
				        Cell r10cell4 = row10.createCell(3);
				        Cell r10cell5 = row10.createCell(4);
				        Cell r10cell6 = row10.createCell(5);
				        Cell r10cell7 = row10.createCell(6);
				        
				        r10cell1.setCellValue("MobileNo");
				        r10cell1.setCellStyle(wrapname);
				        r10cell2.setCellValue("Actions");
				        r10cell2.setCellStyle(wrapname);
	
	
	
				      
			            double total=0.00;
				        int count1=1;
	
				        if(!bookData.isEmpty()){
					        for(Object[] hlo :bookData) {
					        	
					        	 List<Object[]> Today=service.getActionToday(hlo[0].toString(), hlo[3].toString());
					 			   List<Object[]> Tommo=service.getActionTommo(hlo[0].toString(), hlo[3].toString());
					               String AiMsg="";
					               int tocount=1;
					               if(Today.size()>0) {
					            	   for(Object[] tod :Today) {
					            		   if(tocount>1) {
					            		   AiMsg=AiMsg.concat(", ");
					            	
					            		   }
					            		   String Pro=null;
					            		   if(!"0".equalsIgnoreCase(tod[1].toString())) {
					            			   Pro="P"+tod[1].toString();
					            		   }else {
					            			   Pro="G";
					            		   }
					            		   String[] str=tod[0].toString().split("/"); 
					            		   AiMsg=AiMsg.concat(Pro+"-"+str[str.length-1]);
	
					            		 tocount++; 
					            	   }
					            	  
					            	   
					               }else {
					            	   AiMsg=AiMsg.concat("T0");
					               }
					               
					               String AiMsgt="";
					               int tmcount=1;
					               if(Tommo.size()>0) {
					            	   for(Object[] tod :Tommo) {
					            		   if(tmcount>1) {
					            		   AiMsgt=AiMsgt.concat(", ");
	
					            		   }
					            		   String Pro=null;
					            		   if(!"0".equalsIgnoreCase(tod[1].toString())) {
					            			   Pro="P"+tod[1].toString();
					            		   }else {
					            			   Pro="G";
					            		   }
					            		   String[] str=tod[0].toString().split("/"); 
					            		   AiMsgt=AiMsgt.concat(Pro+"-"+str[str.length-1]);
					            		 tmcount++; 
					            	   }
					            	  
					            	   
					               }else {
					            	   AiMsgt=AiMsgt.concat("t0");
					               }
					              
					               if(hlo[2]!=null) {
					    		    Row row1 = sheet.createRow(++rowCount);
					 			
					 			    Cell cellsn1 = row1.createCell(0);
								    cellsn1.setCellValue(hlo[2].toString());
								    cellsn1.setCellStyle(wrapname);
					                 Cell cell11 = row1.createCell(1);
					                 cell11.setCellValue(hlo[3].toString()+"-"+hlo[4].toString()+"/C"+hlo[6].toString()+"/D"+hlo[7].toString()+"/P"+hlo[5].toString()+"/"+AiMsg+"/"+AiMsgt);
					                 cell11.setCellStyle(wrapname);  
					               
	
					               }
									
					            
									 count1++;}}
				        
				        
				        ++rowCount;
				   
				         sheet.autoSizeColumn(0);
					     sheet.setColumnWidth(1,8000);
					     sheet.setColumnWidth(2,25000);
					     sheet.setColumnWidth(3,5000);
					     sheet.setColumnWidth(4,8000);
					     sheet.setColumnWidth(5,8000);
					     sheet.setColumnWidth(6,5000);
		        
		        
		
	
		        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
		        wb.write(outByteStream);
		        byte [] outArray = outByteStream.toByteArray();
			
		        res.setContentType("application/ms-excel");
		        res.setHeader("Content-Disposition", "attachment; filename="+name);
		        
		        OutputStream outStream = res.getOutputStream();
		        outStream.write(outArray);
		        outStream.flush();
		        outStream.close();
			    wb.close();
			    outByteStream.close();
			 }
	        catch (Exception e) {
	        	e.printStackTrace();
			    logger.error(new Date() +"Inside AlertExcelFile.htm "+Username, e);
//			    return "static/Error";
		
	        }
	
	}
	
	
	@RequestMapping(value = "MeetingExcelFile.htm", method = RequestMethod.GET)
	public String MeetingExcelFile(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception {

		String Username = (String) ses.getAttribute("Username");
		
		logger.info(new Date() +"Inside  MeetingExcelFile.htm "+Username);
		try {
			
		List<Object[]> bookData= service.getMeetingAlertList();
	
	      List<MeetingExcelDto> dto=new ArrayList<MeetingExcelDto>();
            
	        int count1=1;

	        if(!bookData.isEmpty()){
		        for(Object[] hlo :bookData) {
		        	
		 			   List<Object[]> Today=service.getMeetingToday(hlo[0].toString());
		 			   List<Object[]> Tommo=service.getMeetingTommo(hlo[0].toString());
		               if(Today.size()>0) {
		            	   for(Object[] tod :Today) {
								/*
								 * if(tocount>1) { AiMsg=AiMsg.concat(","); TimeMsg=TimeMsg.concat(",");
								 * VenueMsg=VenueMsg.concat(","); }
								 */
		            		   String AiMsg="";
		            		   String Pro=null;
		            		   if(!"0".equalsIgnoreCase(tod[0].toString())) {
		            			   Pro="P"+tod[0].toString();
		            		   }else {
		            			   Pro="G";
		            		   }
		            		   String[] str=tod[1].toString().split("/"); 
		            		   AiMsg=AiMsg.concat(Pro+"-"+str[str.length-3]);
		            		   //TimeMsg=TimeMsg.concat(tod[3].toString());
		            		   //VenueMsg=VenueMsg.concat(tod[4].toString());
		            		   
		            		   
		            		   if(hlo[2]!=null) {
		   		    		    MeetingExcelDto Mdto=new MeetingExcelDto();
		   		 			
		   		 			    
		   					   Mdto.setMobileNo(hlo[2].toString());
		   		               Mdto.setMeetings(AiMsg+" "+tod[3].toString());
		   		               Mdto.setVenue(tod[4].toString());
                               dto.add(Mdto);
		   		               

		   		               }
		            	   }
		            	  
		            	   
		               }
		               
		               if(Tommo.size()>0) {
		            	   for(Object[] tod :Tommo) {
		            		   String AiMsgt="";
		            		   //if(tmcount>1) {
		            		   //AiMsgt=AiMsgt.concat(",");
		            		   //TimeMsgt=TimeMsgt.concat(",");
		            		   //VenueMsgt=VenueMsgt.concat(",");
		            		   //}
		            		   String Pro=null;
		            		   if(!"0".equalsIgnoreCase(tod[0].toString())) {
		            			   Pro="P"+tod[0].toString();
		            		   }else {
		            			   Pro="G";
		            		   }
		            		   String[] str=tod[1].toString().split("/"); 
		            		   AiMsgt=AiMsgt.concat(Pro+"-"+str[str.length-3]);
		            		   //TimeMsgt=TimeMsgt.concat(tod[3].toString());
		            		   //VenueMsgt=VenueMsgt.concat(tod[4].toString());
		            		   
		            		   if(hlo[2]!=null) {
		            			   MeetingExcelDto Mdto=new MeetingExcelDto();
				   		 			
			   		 			    
			   					   Mdto.setMobileNo(hlo[2].toString());
			   		               Mdto.setMeetings(AiMsgt+" "+tod[3].toString());
			   		               Mdto.setVenue(tod[4].toString());
	                               dto.add(Mdto);  
		   		              
		   		               }
		            		   
		            		   
		            	   }
		            	  
		            	   
		               }
		              
		            
						
		            
						 count1++;}}
	        
	        
	       req.setAttribute("MeetingList", dto);
	       

	

		 }
	        catch (Exception e) {
			    logger.error(new Date() +"Inside MeetingExcelFile.htm "+Username, e);
		        }
		
		return "action/MeetingExcel";
	
	}
	
	/*
	 * @RequestMapping(value = "MeetingExcelFile2.htm", method = RequestMethod.GET)
	 * public void MeetingExcelFile2(HttpServletRequest req, HttpSession ses,
	 * HttpServletResponse res) throws Exception {
	 * 
	 * String Username = (String) ses.getAttribute("Username");
	 * 
	 * logger.info(new Date() +"Inside  IccExcelSheet"+Username); try {
	 * WritableWorkbook myFirstWbook = null;
	 * 
	 * String EXCEL_FILE_LOCATION = "E:\\MyFirstExcel.xls"; WorkbookSettings ws =
	 * new WorkbookSettings(); ws.setEncoding("Cp1252"); myFirstWbook
	 * =jxl.Workbook.createWorkbook(new File(EXCEL_FILE_LOCATION),ws);
	 * 
	 * // create an Excel sheet WritableSheet excelSheet =
	 * myFirstWbook.createSheet("Sheet 1", 0);
	 * 
	 * // add something into the Excel sheet Label label = new Label(0, 0,
	 * "Test Count"); excelSheet.addCell(label);
	 * 
	 * jxl.write.Number number = new jxl.write.Number(0, 1, 1);
	 * excelSheet.addCell(number);
	 * 
	 * label = new Label(1, 0, "Result"); excelSheet.addCell(label);
	 * 
	 * label = new Label(1, 1, "Passed"); excelSheet.addCell(label);
	 * 
	 * number = new jxl.write.Number(0, 2, 2); excelSheet.addCell(number);
	 * 
	 * label = new Label(1, 2, "Passed 2"); excelSheet.addCell(label);
	 * 
	 * myFirstWbook.write(); myFirstWbook.close();
	 * 
	 * 
	 * } catch (Exception e) { logger.error(new Date()
	 * +"Inside IccExcelSheet"+Username, e); e.printStackTrace(); }
	 * 
	 * }
	 */
	
	
	@RequestMapping(value = "ProjectEmpListFetchAction.htm", method = RequestMethod.GET )
	public @ResponseBody String ProjectEmpListFetchAction(HttpServletRequest req,HttpSession ses) throws Exception 
	{		
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String Logintype= (String)ses.getAttribute("LoginType");
		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside ProjectEmpListFetchAction.htm "+ UserId);	
		String projectid=req.getParameter("projectid");
		List<Object[]> EmployeeList=null;
		if(projectid.equalsIgnoreCase("A"))
		{
			EmployeeList=service.AllEmpNameDesigList();
		}else if(Long.parseLong(projectid)>=0)
		{
			EmployeeList=service.EmployeeDropdown(EmpId,Logintype,projectid);
		}
		
		Gson json = new Gson();
		return json.toJson(EmployeeList);
	}
		
	
	
	
	
	@RequestMapping(value = "ActionEditSubmit.htm", method = RequestMethod.POST)
	public String ActionEditSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside ActionEditSubmit.htm "+UserId);	
		int count =0;
		try {
			if(InputValidator.isContainsHTMLTags(req.getParameter("actionitem"))) {
				//redir.addAttribute("resultfail", "Action Item should not contain HTML elements !");
				return  redirectWithError(redir,"ActionLaunch.htm","Action Item should not contain HTML elements !");
			}
			
			
			ActionMain main=new ActionMain();
			main.setActionMainId(Long.parseLong(req.getParameter("actionmainid")));
			main.setActionItem(req.getParameter("actionitem"));
			main.setModifiedBy(UserId);
			ActionAssign assign=new ActionAssign();
			
			if(req.getParameter("newPDC").length()>0) {
			assign.setPDCOrg(new java.sql.Date(sdf.parse(req.getParameter("newPDC")).getTime()));
			assign.setEndDate(new java.sql.Date(sdf.parse(req.getParameter("newPDC")).getTime()));
			}	
			assign.setAssigneeLabCode(req.getParameter("modelAssigneeLabCode"));
			assign.setAssignee(Long.parseLong(req.getParameter("Assignee")));
			assign.setActionAssignId(Long.parseLong(req.getParameter("actionassigneid")));
			assign.setModifiedBy(UserId);
			
			 count =service.ActionMainEdit(main);
			 count = service.ActionAssignEdit(assign);
			
			if(count>0) {
				//service.ActionExtendPdc(req.getParameter("actionmainid"),req.getParameter("newPDC"), UserId , req.getParameter("actionassigneid"));
			}
	
			if (count > 0) {
				redir.addAttribute("result", "Action Updated Successfully");
			} else {
				redir.addAttribute("resultfail", "Action Update Unsuccessful");
			}
			return "redirect:/ActionLaunch.htm";
		}
		catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside ActionEditSubmit.htm "+UserId, e);
				return "static/Error";
		}
		
	}
	
	
	@RequestMapping(value = "M-A-Update123.htm")
	public String MileActivityUpdate(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String Labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside M-A-Update123.htm "+UserId);
		
		try {
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
	        MileEditDto mainDto = new MileEditDto();
			mainDto.setMilestoneActivityId(req.getParameter("MilestoneActivityId"));
			mainDto.setActivityId(req.getParameter("ActivityId"));
			mainDto.setProjectId(req.getParameter("ProjectId"));
			mainDto.setActivityType(req.getParameter("ActivityType"));
			
//			req.setAttribute("StatusList", service.StatusList());
//			req.setAttribute("EmpList", service.ProjectEmpList(req.getParameter("ProjectId") ,Labcode));
//			req.setAttribute("EditData", service.MilestoneActivityEdit(mainDto).get(0));
//			req.setAttribute("EditMain", mainDto);
//			req.setAttribute("SubList", service.MilestoneActivitySub(mainDto));
//			if(req.getParameter("ActivityType").equals("M")) {
//				req.setAttribute("ActionList", service.ActionList("M",req.getParameter("MilestoneActivityId")));
//			}else
//			{
//				req.setAttribute("ActionList", service.ActionList("A",req.getParameter("ActivityId")));	
//			}
//			req.setAttribute("projectdetails",service.ProjectDetails(req.getParameter("ProjectId")).get(0));
//			req.setAttribute("AllLabsList", committeservice.AllLabList());
			
			
		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside M-A-Update123.htm "+UserId, e); 
			return "static/Error";
		}

		return "milestone/MileActivityUpdate";

	}
	
	
	@RequestMapping(value = "ActionTree.htm")
	public String ActionTree(HttpServletRequest req, HttpSession ses) throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside ActionTree.htm "+UserId);
		try {
			
			String ActionAssignId = req.getParameter("ActionAssignid"); 
			
			List<Object[]> actionslist = service.ActionSubLevelsList(ActionAssignId);
	
			req.setAttribute("actionslist", actionslist);
			return "action/ActionTree";
		}catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +" Inside ActionTree.htm "+UserId, e);		
			return "static/Error";
			
		}		

	}
	
	@RequestMapping(value = "ActionSubListAjax.htm", method = RequestMethod.GET)
	public @ResponseBody String ActionSubListAjax(HttpServletRequest req, HttpSession ses) throws Exception 
	{
		Gson json = new Gson();
		List<Object[]> ActionSubList=null;
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside ActionSubListAjax.htm "+UserId);		
		try {
			
			ActionSubList =   service.ActionSubList(req.getParameter("ActionAssignid"));
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside ActionSubListAjax.htm "+UserId, e);
			ActionSubList=new ArrayList<>();
		}
		return json.toJson(ActionSubList);
	}
	
	@RequestMapping(value = "ActionIssue.htm" ,method = {RequestMethod.POST,RequestMethod.GET})
	public String ActionIssue(Model model , HttpServletRequest req, HttpSession ses ,RedirectAttributes redir) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside ActionIssue.htm "+UserId);
		
		try {
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String action = req.getParameter("Action");
			
			List<Object[]> issuedatalist= new ArrayList<>();
			if(action!=null && "TA".equalsIgnoreCase(action)) {
				issuedatalist=service.GetIssueList( EmpId).stream().filter(flag-> flag[11].toString().equalsIgnoreCase(EmpId)).collect(Collectors.toList());
				action="TA";
			}else if(action!=null && "FA".equalsIgnoreCase(action)){
				issuedatalist=service.GetIssueList( EmpId).stream().filter(flag-> flag[10].toString().equalsIgnoreCase(EmpId)).collect(Collectors.toList());
				action="FA";
			}else if(action!=null && "F".equalsIgnoreCase(action)){
				issuedatalist=service.GetIssueList( EmpId).stream().filter(flag-> flag[9].toString().equalsIgnoreCase("F") && flag[11].toString().equalsIgnoreCase(EmpId)).collect(Collectors.toList());
				action="F";
			}else {
				issuedatalist=service.GetIssueList( EmpId);
				action="All";
			}
			req.setAttribute("action", action);
			req.setAttribute("issuedatalist", issuedatalist);
			
		return "Issue/Issuelist";
		}catch(Exception e){

			e.printStackTrace(); 
			logger.error(new Date() +" Inside ActionIssue.htm "+UserId, e);		
			return "ststic/Error";	
		}		
	}
	@RequestMapping(value = "ActionAssignDataAjax.htm", method = RequestMethod.GET)
	public @ResponseBody String ActionAssignDataAjax(HttpServletRequest req, HttpSession ses) throws Exception 
	{
		Gson json = new Gson();
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside ActionAssignDataAjax.htm "+UserId);	
		Object[] ActionSubList =null;
		try {
			
			ActionSubList =   service.ActionAssignDataAjax(req.getParameter("ActionAssignid"));
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside ActionAssignDataAjax.htm "+UserId, e);
		}
		return json.toJson(ActionSubList);
	}
	
	@RequestMapping(value = "IssueUpdate.htm" , method = {RequestMethod.POST,RequestMethod.GET})
	public String IssueUpdate(Model model , HttpServletRequest req, HttpSession ses ,RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside IssueUpdate.htm "+UserId);
		
		try{
			String actionmainid=req.getParameter("ActionMainId");
			String actionassignid=req.getParameter("ActionAssignid");
			if(actionassignid==null && actionmainid==null){
				Map md=model.asMap();
				actionmainid=(String)md.get("ActionMainId");
				actionassignid=(String)md.get("ActionAssignId");
			}
			
			req.setAttribute("Assignee", service.AssigneeData(actionmainid ,actionassignid).get(0));
			req.setAttribute("SubList", service.SubList(actionassignid));
			req.setAttribute("filesize",file_size);
			return "Issue/IssueUpdate";
		
		}catch(Exception e){
			e.printStackTrace();
			logger.error(new Date() +" Inside IssueUpdate.htm "+UserId, e);		
		}
		return "Issue/IssueUpdate";
	}
	
	@RequestMapping(value = "IssueSubSubmit.htm", method = RequestMethod.POST)
	public String IssueSubSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir,
			@RequestPart("FileAttach") MultipartFile FileAttach) throws Exception {
		
		String UserId  = (String) ses.getAttribute("Username");
		String labCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside IssueSubSubmit.htm "+UserId);		
		try {
			
			
			redir.addFlashAttribute("ActionMainId", req.getParameter("ActionMainId"));
			redir.addFlashAttribute("ActionAssignId", req.getParameter("ActionAssignId"));
			
				
			ActionSubDto subDto=new ActionSubDto();
				subDto.setLabCode(labCode);
				subDto.setFileName(req.getParameter("FileName"));
				subDto.setFileNamePath(FileAttach.getOriginalFilename());
				subDto.setMultipartfile(FileAttach);
	            subDto.setCreatedBy(UserId);
	            subDto.setActionAssignId(req.getParameter("ActionAssignId"));
	            subDto.setRemarks(req.getParameter("Remarks"));
	            subDto.setProgress(req.getParameter("Progress"));
	            subDto.setProgressDate(req.getParameter("AsOnDate"));
			Long count=service.IssueSubInsert(subDto);
            
			if (count > 0) {
				redir.addAttribute("result", "Issue  Updated Successfully");
			} else {
				redir.addAttribute("resultfail", "Issue Update Unsuccessful");
			}

		}
		catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside IssueSubSubmit.htm "+UserId, e);
		}

		return "redirect:/IssueUpdate.htm";
	}
	
	 @RequestMapping(value = "IssueForward.htm", method = RequestMethod.POST)
		public String IssueForward(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
		 String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside IssueForward.htm "+UserId);		
			try { 
			int count = service.ActionForward(req.getParameter("ActionMainId"),req.getParameter("ActionAssignId"), UserId);

			if (count > 0) {
				redir.addAttribute("result", "Issue Forwarded Successfully");
			} else {
				redir.addAttribute("resultfail", "Issue Forward Unsuccessful");

			}
			redir.addFlashAttribute("ActionAssignId", req.getParameter("ActionAssignId"));
			
			}
			catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside IssueForward.htm "+UserId, e);
			}
			return "redirect:/ActionIssue.htm";

		}
	 
	    @RequestMapping(value = "IssueForwardSub.htm", method = RequestMethod.POST)
		public String IssueForwardSub(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
				throws Exception {
		 String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside IssueForwardSub.htm "+UserId);		
			try { 
				req.setAttribute("Assignee", service.AssigneeData(req.getParameter("ActionMainId"),req.getParameter("ActionAssignId")).get(0));
				req.setAttribute("SubList", service.SubList(req.getParameter("ActionAssignId")));
			}
			catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside IssueForwardSub.htm "+UserId, e);
			}
			return "Issue/CloseOrSendBack";
		}
	 
	 	@RequestMapping(value = "Recommendation.htm" , method = {RequestMethod.GET , RequestMethod.POST })
	 	public String RecommendationList(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception
	 	{
	 		String UserId = (String) ses.getAttribute("Username");
	 		logger.info(new Date() +"Inside RecommendationList.htm "+UserId);	
	 		try {
	 			String Logintype= (String)ses.getAttribute("LoginType");
				String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
				String LabCode = (String)ses.getAttribute("labcode");
	 			List<Object[]> projectdetailslist=service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
	 			String projectid=req.getParameter("projectid");
				String committeeid=req.getParameter("committeeid");
				String recorDecision=req.getParameter("recOrDecision");
				if(projectdetailslist.size()==0) 
				{				
					redir.addAttribute("resultfail", "No Project is Assigned to you.");
					return "redirect:/MainDashBoard.htm";
				}
				
				if(committeeid==null && recorDecision==null)
				{
					committeeid="A";
					recorDecision="R";
				}		
				
				if(projectid==null || projectid.equals("null"))
				{
					projectid=projectdetailslist.get(0)[0].toString();
				}
				
				if(recorDecision!=null && recorDecision.equalsIgnoreCase("D")) {
					List<Object[]> recomendation = service.GetDecisionList(projectid,committeeid);
					req.setAttribute("recomendation", recomendation);
				}else if(recorDecision!=null && recorDecision.equalsIgnoreCase("R")) {
					List<Object[]> recomendation = service.GetRecomendationList(projectid,committeeid);
					req.setAttribute("recomendation", recomendation);
				}else if(recorDecision!=null && recorDecision.equalsIgnoreCase("S")) {
					List<Object[]> DecisionSought = service.GetRecDecSoughtList(projectid,committeeid,"D");
					
					Map<String,List<List<Object[]>>> actualdecisionsought=new HashMap<String,List<List<Object[]>>>(); 
					for(Object[] obj :DecisionSought){
						String keyval = obj[1].toString() +"//"+ obj[0].toString();
						
						List<List<Object[]>> list = new ArrayList<List<Object[]>>();
						list.add(service.getActualDecOrRecSought(obj[0].toString() , "D"));
						list.add(service.getDecOrRecSought(obj[0].toString() , "D"));
						actualdecisionsought.put(keyval, list);
					}
					req.setAttribute("actualRecDecought", actualdecisionsought);
					
				}else if(recorDecision!=null && recorDecision.equalsIgnoreCase("RS")) {
					List<Object[]> DecisionSought = service.GetRecDecSoughtList(projectid,committeeid,"R");
					Map<String,List<List<Object[]>>> actualdecisionsought=new HashMap<String,List<List<Object[]>>>(); 
					for(Object[] obj :DecisionSought){
						String keyval = obj[1].toString() +"//"+ obj[0].toString();
						List<List<Object[]>> list = new ArrayList<List<Object[]>>();
						list.add(service.getActualDecOrRecSought(obj[0].toString() , "R"));
						list.add(service.getDecOrRecSought(obj[0].toString() , "R"));
						actualdecisionsought.put(keyval, list);
					}
					req.setAttribute("actualRecDecought", actualdecisionsought);
					
				}
				List<Object[]> projapplicommitteelist=committeservice.ProjectApplicableCommitteeList(projectid);
				
				req.setAttribute("recOrDecision", recorDecision);
				req.setAttribute("projectid",projectid);
				req.setAttribute("committeeid",committeeid);
				req.setAttribute("projectlist", projectdetailslist);
				req.setAttribute("projapplicommitteelist",projapplicommitteelist);
			} catch(Exception e){
				e.printStackTrace();
				logger.error(new Date() +" Inside RecommendationList.htm "+UserId, e);
			}
	 		return "Issue/Recommendation";
	 	}
	 	
	 	
	 	@RequestMapping(value = "Decision.htm" , method = {RequestMethod.GET , RequestMethod.POST })
	 	public String DecisionList(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception
	 	{
	 		String UserId = (String) ses.getAttribute("Username");
	 		logger.info(new Date() +"Inside Decision.htm "+UserId);	
	 		try {
	 			String Logintype= (String)ses.getAttribute("LoginType");
				String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
				String LabCode = (String)ses.getAttribute("labcode");
	 			List<Object[]> projectdetailslist=service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
	 			String projectid=req.getParameter("projectid");
				String committeeid=req.getParameter("committeeid");
				String recorDecision=req.getParameter("recOrDecision");
				if(projectdetailslist.size()==0) 
				{				
					redir.addAttribute("resultfail", "No Project is Assigned to you.");
					return "redirect:/MainDashBoard.htm";
				}
				
				if(committeeid==null && recorDecision==null)
				{
					committeeid="A";
					recorDecision="D";
				}
				
				if(projectid==null || projectid.equals("null"))
				{
					projectid=projectdetailslist.get(0)[0].toString();
				}
				if(recorDecision!=null && recorDecision.equalsIgnoreCase("D")) {
					List<Object[]> decision = service.GetDecisionList(projectid,committeeid);
					req.setAttribute("recomendation", decision);
				}else if(recorDecision!=null && recorDecision.equalsIgnoreCase("R")) {
					List<Object[]> recomendation = service.GetRecomendationList(projectid,committeeid);
					req.setAttribute("recomendation", recomendation);
				}else if(recorDecision!=null && recorDecision.equalsIgnoreCase("S")) {
					List<Object[]> DecisionSought = service.GetRecDecSoughtList(projectid,committeeid,"D");
					Map<String,List<List<Object[]>>> actualdecisionsought=new HashMap<String,List<List<Object[]>>>(); 
					
					for(Object[] obj :DecisionSought){
						String keyval = obj[1].toString() +"//"+ obj[0].toString();
						List<List<Object[]>> list = new ArrayList<List<Object[]>>();
						list.add(service.getActualDecOrRecSought(obj[0].toString() , "D"));
						list.add(service.getDecOrRecSought(obj[0].toString() , "D"));
						actualdecisionsought.put(keyval, list);
					}
					req.setAttribute("actualRecDecought", actualdecisionsought);
				}else if(recorDecision!=null && recorDecision.equalsIgnoreCase("RS")) {
					List<Object[]> DecisionSought = service.GetRecDecSoughtList(projectid,committeeid,"R");
					Map<String,List<List<Object[]>>> actualdecisionsought=new HashMap<String,List<List<Object[]>>>(); 
					
					for(Object[] obj :DecisionSought){
						String keyval = obj[1].toString() +"//"+ obj[0].toString();
						List<List<Object[]>> list = new ArrayList<List<Object[]>>();
						list.add(service.getActualDecOrRecSought(obj[0].toString() , "R"));
						list.add(service.getDecOrRecSought(obj[0].toString() , "R"));
						actualdecisionsought.put(keyval, list);
					}
					req.setAttribute("actualRecDecought", actualdecisionsought);
				}
				List<Object[]> projapplicommitteelist=committeservice.ProjectApplicableCommitteeList(projectid);
				
				req.setAttribute("recOrDecision", recorDecision);
				req.setAttribute("projectid",projectid);
				req.setAttribute("committeeid",committeeid);
				req.setAttribute("projectlist", projectdetailslist);
				req.setAttribute("projapplicommitteelist",projapplicommitteelist);
			} catch(Exception e){
				e.printStackTrace();
				logger.error(new Date() +" Inside Decision.htm "+UserId, e);
			}
	 		return "Issue/Decision";
	 	}
	 	
	 	@RequestMapping(value = "ToDoReviews.htm" , method = RequestMethod.GET)
	 	public String ToDoList(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception
	 	{
	 		 String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside ToDoReviews.htm "+UserId);
	 		try {
	 			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
	 			String Logintype=(String)ses.getAttribute("LoginType");
	 			String LabCode=(String)ses.getAttribute("labcode");
				List<Object[]> actionlist = service.GetActionList(EmpId);
				req.setAttribute("actionassigneelist", actionlist.stream().filter(e->  e[11].toString().equalsIgnoreCase(EmpId)).collect(Collectors.toList()));
				req.setAttribute("actionassignorlist", actionlist.stream().filter(e->  e[12].toString().equalsIgnoreCase(EmpId)).collect(Collectors.toList()));
				req.setAttribute("ProjectList", service.LoginProjectDetailsList(EmpId,Logintype,LabCode)); 
				req.setAttribute("FavouriteList", service.GetFavouriteList(EmpId));
	 		}catch(Exception e){
				e.printStackTrace();
				logger.error(new Date() +" Inside ToDoReviews.htm "+UserId, e);
			}
	 		return "action/ToDoReview";
	 	}
	 	
	 	@RequestMapping(value = "ActionMonitoring.htm")
		public String ActionMonitoring(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception 
	 	{
			String UserId = (String) ses.getAttribute("Username");
			String Logintype= (String)ses.getAttribute("LoginType");
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String LabCode = (String)ses.getAttribute("labcode");
			logger.info(new Date() +"Inside ActionMonitoring.htm "+UserId);		
			try {
				List<Object[]> projectdetailslist=service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
				String projectid=req.getParameter("projectid");
				String status=req.getParameter("status");
				
				if(projectid==null && projectdetailslist.size()==0)
				{
					redir.addAttribute("resultfail", "Reminder Removal Unsuccessful");		
					return "MainDashBoard.htm";
				}
				if(projectid==null) 
				{
					projectid = projectdetailslist.get(0)[0].toString();
					status = "P";
				}
	 			
				req.setAttribute("projectid",projectid);
				req.setAttribute("status",status);
				req.setAttribute("ProjectsList",projectdetailslist);
				req.setAttribute("ActionsList",service.ActionMonitoring(projectid, status));
				return "action/ActionMonitoring";
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside ActionMonitoring.htm "+UserId, e);
				return "static/Error";
			}

		}
	 	
	 	@RequestMapping(value = "GetActionListForFavourite.htm", method = RequestMethod.GET)
		public @ResponseBody String ActionListForFavourite(HttpServletRequest req, HttpSession ses) throws Exception 
		{
			Gson json = new Gson();
			List<Object[]> list=null;
			String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside GetActionListForFavourite.htm "+UserId);		
			try {
				
				String fromdate = req.getParameter("Fromdate");
				String todate = req.getParameter("Todate");
				String projectid = req.getParameter("Projectid");
				String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
				
				list = service.GetActionListForFevorite(fromdate,todate,projectid,EmpId);
			}catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside GetActionListForFavourite.htm "+UserId, e);
				list=new ArrayList<>();
			}
			return json.toJson(list);
		}
	 	
	 	@RequestMapping(value = "AddFavouriteList.htm", method = { RequestMethod.GET , RequestMethod.POST})
		public  String AddFavouriteList(HttpServletRequest req, HttpSession ses , RedirectAttributes redir) throws Exception 
		{
	 		String UserId = (String) ses.getAttribute("Username");
	 		logger.info(new Date() +"Inside GetActionListForFavourite.htm "+UserId);		
			try {
				String[] asiignid = req.getParameterValues("favourite");
				
				Long EmpId = (Long) ses.getAttribute("EmpId");
				long count  = service.AddFavouriteList(asiignid , EmpId , UserId);
				if (count > 0) {
					redir.addAttribute("result", "Favourite List Add Successfully");
				} else {
					redir.addAttribute("resultfail", "Favourite List Add Unsuccessful");
				}
			}catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside AddFavouriteList.htm "+UserId, e);
			}
			return "redirect:/ToDoReviews.htm";
		}
	 	
	 	
	 	@RequestMapping(value = "ActionGraph.htm", method = {RequestMethod.GET, RequestMethod.POST})
	 	public String Actiongraph(HttpServletResponse res , HttpServletRequest req ,HttpSession ses , RedirectAttributes redir)throws Exception
	 	{
	 		String UserId = (String) ses.getAttribute("Username");
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String LabCode = (String) ses.getAttribute("labcode");
	 		logger.info(new Date() +"Inside ActionGraph.htm "+UserId);
	 		try {
	 			String projectid=req.getParameter("projectid");
				if(projectid==null)
				{
					projectid="A";
					
				}
				String Logintype= (String)ses.getAttribute("LoginType");
				
				
				if(Logintype.equals("A") || Logintype.equals("Z") || Logintype.equals("Y") || Logintype.equalsIgnoreCase("C")|| Logintype.equalsIgnoreCase("I"))
				{	//all projects for admin, associate director and director
					req.setAttribute("projectslist", service.allprojectdetailsList());
				}else if(Logintype.equals("P")){
					List<Object[]> projectlist= service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
					if(projectlist.size()==0) {
						
						redir.addAttribute("resultfail", "No Project is Assigned to you.");

						return "redirect:/MainDashBoard.htm";
					}
					req.setAttribute("projectslist",projectlist );
				}
				//req.setAttribute("ActionList", service.ActionReports(EmpId,"N",projectid,"A", LabCode));	
				req.setAttribute("ActionList", service.ActionReportsNew(EmpId, "N", projectid, "A", LabCode,Logintype));
				req.setAttribute("projectid",projectid);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside ActionGraph.htm "+UserId, e);
			}
	 		return "action/ActionGraph";
	 	}
	 	

	 	@RequestMapping(value = "RfaAction.htm", method = {RequestMethod.GET, RequestMethod.POST})
	 	public String RfaAction(HttpServletResponse res , HttpServletRequest req ,HttpSession ses , RedirectAttributes redir)throws Exception
	 	{
			String UserId =(String)ses.getAttribute("Username");
			String LoginType =(String)ses.getAttribute("LoginType");
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String LabCode =(String) ses.getAttribute("labcode");
			
	 		logger.info(new Date() +"Inside RfaAction.htm "+UserId);
			try {

				FormatConverter fc=new FormatConverter();
				SimpleDateFormat sdf=fc.getRegularDateFormat();
				SimpleDateFormat sdf1=fc.getSqlDateFormat();
				
				String fdate=req.getParameter("fdate");
				String tdate=req.getParameter("tdate");
				String Emp=req.getParameter("EmpId");
				String Status=req.getParameter("Status");
				String initiationId = "A";
				String projectId = "A";
				String projectType = req.getParameter("projectType");
				projectType = projectType != null ? projectType : "P";
				
				if (projectType.equalsIgnoreCase("P")) {
					projectId = req.getParameter("projectId");
					List<Object[]> projectList = service.LoginProjectDetailsList(EmpId, LoginType, LabCode);
					projectId = projectId != null ? projectId: "A";
					req.setAttribute("ProjectList", projectList);
					req.setAttribute("projectId",projectId);
				} else {
					initiationId = req.getParameter("initiationId");
					List<Object[]> preProjectList = reqservice.getPreProjectList(LoginType, LabCode, EmpId);
					initiationId = initiationId != null ? initiationId: "A";
					req.setAttribute("preProjectList", preProjectList);
					req.setAttribute("initiationId", initiationId);
				}
				
				 Gson json = new Gson();
				 LocalDate currentDate = LocalDate.now();
				if(fdate==null)
				{
					LocalDate firstDayOfYear = LocalDate.of(currentDate.getYear(), 1, 1);
				    fdate = firstDayOfYear.toString();
					Emp="A";
				}else
				{
					fdate=sdf1.format(sdf.parse(fdate));
				}		
				if(tdate==null)
				{
					LocalDate lastDayOfYear = LocalDate.of(currentDate.getYear(), 12, 31);
					tdate=lastDayOfYear.toString();
				}
				else 
				{	
					tdate=sdf1.format(sdf.parse(tdate));				
				}
				
				req.setAttribute("tdate",tdate);
				req.setAttribute("fdate",fdate);
				req.setAttribute("ModalEmpList",json.toJson(service.getRfaModalEmpList(LabCode)));
				req.setAttribute("ModalTDList", json.toJson( service.getRfaTDList(LabCode)));
				req.setAttribute("Employee", Emp);
				req.setAttribute("EmpId", EmpId);
				req.setAttribute("LoginType", LoginType);
				req.setAttribute("UserId", UserId);
				req.setAttribute("Status", Status);
				req.setAttribute("AssigneeEmplList", service.AssigneeEmpList());
				req.setAttribute("ExternalFromAssignEmployee", service.assigneeListForFromExternal());
				req.setAttribute("projectType", projectType);

				
				List<Object[]> RfaActionList = new ArrayList<>();
				
				if(LoginType.equalsIgnoreCase("A") || LoginType.equalsIgnoreCase("C") || LoginType.equalsIgnoreCase("I")) {
					RfaActionList = service.GetRfaActionList1(projectType,projectId,initiationId,fdate,tdate);    // Admin,TD,PGD can see all rfa list
				}else if(LoginType.equalsIgnoreCase("P")){
					RfaActionList = service.RfaProjectwiseList(EmpId,projectType,projectId,initiationId,fdate,tdate);  // project wise rfa list
				}else {
					RfaActionList = service.GetRfaActionList(EmpId,projectType,projectId,initiationId,fdate,tdate); // assignor can see only his rfa list
				}
				
				if(Status!=null && Status.equalsIgnoreCase("O")) {  // for rfa open list
					req.setAttribute("RfaActionList", RfaActionList.stream().filter(e-> !e[14].toString().equalsIgnoreCase("ARC") && !e[14].toString().equalsIgnoreCase("RFC")).collect(Collectors.toList()));
				}else if ( Status!=null && Status.equalsIgnoreCase("C")) {   // for rfa close list
					req.setAttribute("RfaActionList", RfaActionList.stream().filter(e-> e[14].toString().equalsIgnoreCase("ARC")).collect(Collectors.toList()));
				}else if ( Status!=null && Status.equalsIgnoreCase("CAN")) {   // for rfa cancel list
					req.setAttribute("RfaActionList", RfaActionList.stream().filter(e-> e[14].toString().equalsIgnoreCase("RFC")).collect(Collectors.toList()));
				}else {
					req.setAttribute("RfaActionList", RfaActionList);
				}
			
			    }catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside RfaAction.htm "+UserId, e);
				}
			
	 		return "action/RfaAction";
	 		
	 	}
		
		
		@RequestMapping(value = "RfaInspection.htm", method = RequestMethod.GET)
		public String RfaInspectionAction(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
			
		       String UserId = (String) ses.getAttribute("Username");
		       String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		       String LabCode =(String) ses.getAttribute("labcode");
		       String assignDetails = null;
		     
			logger.info(new Date() +"Inside RfaInspection.htm"+UserId);		
			try {
				List<Object[]> RfaInspectionList = service.RfaInspectionList(EmpId);
				List<Object[]> RfaForwardApprovedList = service.RfaForwardApprovedList(EmpId);
				
				for(int i=0;i<RfaInspectionList.size();i++) {
					
					Long rfaId = Long.parseLong(RfaInspectionList.get(i)[0].toString());
					assignDetails = service.getAssignDetails(EmpId,rfaId);
					
			     	}
				
				 Gson json = new Gson();
				
				req.setAttribute("RfaInspectionList", RfaInspectionList);
				req.setAttribute("RfaForwardApprovedList", RfaForwardApprovedList);
				req.setAttribute("ModalEmpList",json.toJson(service.getRfaModalEmpList(LabCode)));
				req.setAttribute("ModalTDList", json.toJson( service.getRfaTDList(LabCode)));
				req.setAttribute("EmpId", EmpId);
				req.setAttribute("rfaCount", assignDetails);
				req.setAttribute("AssigneeEmplList", service.AssigneeEmpList());
				
			}
			catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside RfaInspection.htm"+UserId, e);
			}
			return "action/RfaInspectionAction";

		}
		
		@RequestMapping(value = "RfaActionAdd.htm", method = {RequestMethod.GET, RequestMethod.POST})
	 	public String RfaActionAdd(HttpServletResponse res , HttpServletRequest req ,HttpSession ses , RedirectAttributes redir)throws Exception
	 	{
	 		String UserId = (String) ses.getAttribute("Username");
			String LabCode = (String) ses.getAttribute("labcode");
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String LoginType=(String)ses.getAttribute("LoginType");
			
	 		logger.info(new Date() +"Inside RfaActionAdd.htm "+UserId);
			try {
		
				String Option=req.getParameter("sub");
			    
			if(Option!=null && Option.equalsIgnoreCase("add")) {
				
				List<Object[]> ProjectList = service.LoginProjectDetailsList(EmpId, LoginType, LabCode);
				List<Object[]> preProjectList = reqservice.getPreProjectList(LoginType, LabCode, EmpId);
				List<Object[]> ProjectTypeList = service.ProjectTypeList();
				List<Object[]> PriorityList = service.PriorityList();
				List<Object[]> EmployeeList = service.EmployeeList(LabCode);
				List<Object[]> RfaNoTypeList = service.getRfaNoTypeList();
				
				req.setAttribute("ProjectList", ProjectList);		
				req.setAttribute("preProjectList", preProjectList);		
				req.setAttribute("ProjectTypeList", ProjectTypeList);
				req.setAttribute("PriorityList", PriorityList);
				req.setAttribute("EmployeeList", EmployeeList);
				req.setAttribute("RfaNoTypeList", RfaNoTypeList);
				req.setAttribute("EmpId", EmpId);
				req.setAttribute("projectType", req.getParameter("projectType"));
				req.setAttribute("projectId", req.getParameter("projectId"));
				req.setAttribute("initiationId", req.getParameter("initiationId"));
				List<Object[]>vendorList = service.getvendorList();
				req.setAttribute("vendorList", vendorList);
				return"action/RfaActionAdd";
			}
				
			req.setAttribute("", Option);
			}
			catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside RfaActionAdd.htm "+UserId, e);
				}
			return "redirect:/RfaActionAdd.htm";
	 		
	 	}
		
		@RequestMapping(value = "RfaActionEdit.htm", method = {RequestMethod.GET, RequestMethod.POST})
	 	public String RfaActionEdit(HttpServletResponse res , HttpServletRequest req ,HttpSession ses , RedirectAttributes redir)throws Exception
	 	{
			String UserId = (String) ses.getAttribute("Username");
			String LabCode = (String) ses.getAttribute("labcode");
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String LoginType=(String)ses.getAttribute("LoginType");
			
			logger.info(new Date() +"Inside RfaActionEdit.htm "+UserId);
			try {
				
				String rfaid=req.getParameter("Did");
				Object[] RfaActionEdit = service.RfaActionEdit(rfaid);
				Object[] rfaAttachDownload = service.RfaAttachmentDownload(rfaid);
				String rfaType = RfaActionEdit[15].toString();
				req.setAttribute("rfaAttachDownload", rfaAttachDownload);
				req.setAttribute("RfaAction", RfaActionEdit);
				req.setAttribute("ProjectList", service.LoginProjectDetailsList(EmpId, LoginType, LabCode));
				req.setAttribute("preProjectList", reqservice.getPreProjectList(LoginType, LabCode, EmpId));
				req.setAttribute("projectType", RfaActionEdit[21]!=null ? RfaActionEdit[21].toString() : "");
				req.setAttribute("ProjectTypeList",service.ProjectTypeList());
				req.setAttribute("PriorityList", service.PriorityList());
				req.setAttribute("RfaNoTypeList", service.getRfaNoTypeList());
				
				
		        List<Object[]> AssigneeList =service.AssigneeEmpList();
		        if(rfaType.equalsIgnoreCase("F")) {
		        	AssigneeList = service.assigneeListForFromExternal();
		        }
		        List<String> AssignEmp=new ArrayList<>();
		        String assigneeLab =LabCode;
		        for(Object[] obj :AssigneeList) {
		        	if(obj[0].toString().equalsIgnoreCase(rfaid)) {
		        		AssignEmp.add(obj[3].toString());
		        		assigneeLab=obj[4].toString();
		        	}
		        }
		        
		        
		        List<Object[]> employeeList = service.EmployeeList(LabCode);
		        System.out.println(assigneeLab+"======================="+LabCode);
		        if(!assigneeLab.equalsIgnoreCase(LabCode)) {
		        if(rfaType.equalsIgnoreCase("E")) {
			        List<Object[]> employeeList1 = service.EmployeeList(assigneeLab);
			        employeeList.addAll(employeeList1);
		        }
		        }
				req.setAttribute("EmployeeList", employeeList);
		        List<Object[]> RfaCCList =service.RfaCCList();
		        List<String> RfaCCEmp=new ArrayList<>();
		        
		        for(Object[] obj :RfaCCList) {
		        	if(obj[0].toString().equalsIgnoreCase(rfaid)) {
		        		RfaCCEmp.add(obj[3].toString()+"/"+obj[4].toString());
		        	}
		        }
		        Gson json = new Gson();
		        req.setAttribute("AssignEmp", json.toJson(AssignEmp));
		        req.setAttribute("RfaCCEmp", json.toJson(RfaCCEmp));
		    	List<Object[]>vendorList = service.getvendorList();
				req.setAttribute("vendorList", vendorList);
				req.setAttribute("assigneeLab", assigneeLab);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside RfaActionEdit.htm "+UserId, e);
			}
			return"action/RfaActionEdit";
			
	 	}
		
		@RequestMapping(value = "RfaAttachmentDownload.htm", method = {RequestMethod.GET, RequestMethod.POST})
	 	public void RfaAttachmentDownload(HttpServletResponse res , HttpServletRequest req ,HttpSession ses , RedirectAttributes redir)throws Exception
	 	{
			String UserId = (String) ses.getAttribute("Username");
			String LabCode = (String) ses.getAttribute("labcode");

			logger.info(new Date() +"Inside RfaAttachmentDownload.htm "+UserId);
			try {
				
				String rfaid=req.getParameter("rfaId");
				String type=req.getParameter("type");
				Object[] rfaAttachDownload = service.RfaAttachmentDownload(rfaid);
				
				File my_file=null;
				if(type.equalsIgnoreCase("ARD")) {
					Path filePath = Paths.get(uploadpath,LabCode,"RFAFiles",rfaAttachDownload[7].toString(),rfaAttachDownload[5].toString().replace('/', '_'),rfaAttachDownload[3].toString());
//					my_file = new File(uploadpath+ rfaAttachDownload[2]+File.separator+rfaAttachDownload[3]);
					my_file = filePath.toFile();
					res.setContentType("Application/octet-stream");	
			        res.setHeader("Content-disposition","attachment; filename="+rfaAttachDownload[3].toString()); 
					
				}else {
				  Path filePath1 = Paths.get(uploadpath,LabCode,"RFAFiles",rfaAttachDownload[7].toString(),rfaAttachDownload[5].toString().replace('/', '_'),rfaAttachDownload[4].toString());
				  my_file = filePath1.toFile();
//				   my_file = new File(uploadpath+ rfaAttachDownload[2]+File.separator+rfaAttachDownload[4]);
				   res.setContentType("Application/octet-stream");	
		           res.setHeader("Content-disposition","attachment; filename="+rfaAttachDownload[4].toString()); 
				}
		        OutputStream out = res.getOutputStream();
		        FileInputStream in = new FileInputStream(my_file);
		        byte[] buffer = new byte[4096];
		        int length;
		        while ((length = in.read(buffer)) > 0){
		           out.write(buffer, 0, length);
		        }
		        in.close();
		        out.flush();
		        out.close();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside RfaAttachmentDownload.htm "+UserId, e);
			}
			
	 	}
			
		//29-04-2025
		@RequestMapping(value = "RfaAEditSubmit.htm", method = {RequestMethod.GET, RequestMethod.POST})
	 	public String RfaAEditSubmit(HttpServletResponse res , HttpServletRequest req ,HttpSession ses , RedirectAttributes redir,
	 			@RequestPart("attachment") MultipartFile attachment)throws Exception
	 	{
	 		String UserId = (String) ses.getAttribute("Username");
	 		String LabCode = (String) ses.getAttribute("labcode");
	 		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
	 		logger.info(new Date() +"Inside RfaAEditSubmit.htm "+UserId);
			try {
				
				String projectCode=req.getParameter("projectCode");
				String rfano=req.getParameter("rfano");
				String rfadate=req.getParameter("rfadate");
				String priority=req.getParameter("priority");
				String projectType=req.getParameter("projectType");
				String projectId=req.getParameter("projectId");
				String statement=req.getParameter("statement");
				String description=req.getParameter("description");
				String reference=req.getParameter("reference");
				String rfaid=req.getParameter("rfaid");
				String [] assignee=req.getParameterValues("assignee");
				String [] CCEmpName=req.getParameterValues("CCEmpName");
				
	
				RfaActionDto rfa = new RfaActionDto();
				
				rfa.setRfaNo(rfano);
				rfa.setProjectCode(projectCode);
				rfa.setLabCode(LabCode);
				rfa.setRfaId(Long.parseLong(rfaid));
				rfa.setRfaDate(sdf.parse(rfadate));
				rfa.setPriorityId(Long.parseLong(priority));
				rfa.setStatement(statement);
				rfa.setDescription(description);
				rfa.setReference(reference);
				rfa.setModifiedBy(UserId);
				rfa.setMultipartfile(attachment);
				rfa.setAssignorAttachment(attachment.getOriginalFilename());
				rfa.setActionBy(Long.parseLong(EmpId));
				rfa.setVendorCode(req.getParameter("vendor")!=null?req.getParameter("vendor").split("/")[0]:LabCode);
				rfa.setBoxNo(req.getParameter("boxno"));
				if(req.getParameter("swdate")!=null) {
				rfa.setSWdate(sdf.parse(req.getParameter("swdate")));
				}
				rfa.setRigVersion(req.getParameter("RigVersion"));
				rfa.setFPGA(req.getParameter("FPGA"));		

				if(req.getParameter("rfaRaisedBy")!=null) {
					rfa.setRfaRaisedByName(req.getParameter("rfaRaisedBy"));
					}
				Long count=service.RfaEditSubmit(rfa,assignee,CCEmpName);
				if (count > 0) {
					redir.addAttribute("result", "RFA Edited Successfully");
				} else {
					redir.addAttribute("resultfail", "RFA Edited Unsuccessfully");
				}
				
				redir.addAttribute("projectType", projectType);
				if(projectType.equalsIgnoreCase("P")) {
					redir.addAttribute("projectId",projectId);
				}else {
					redir.addAttribute("initiationId", projectId);
				}
			    }catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside RfaAEditSubmit.htm "+UserId, e);
				}
	 		return "redirect:/RfaAction.htm";
	 	}
		
		//29-04-2025
		@RequestMapping(value = "RfaActionSubmit.htm", method = {RequestMethod.GET, RequestMethod.POST})
	 	public String RfaActionSubmit(HttpServletResponse res , HttpServletRequest req ,HttpSession ses , RedirectAttributes redir,
	 			@RequestPart("attachment") MultipartFile attachment)throws Exception
	 	{
	 		String UserId = (String) ses.getAttribute("Username");
			String LabCode = (String) ses.getAttribute("labcode");
			
	 		logger.info(new Date() +"Inside RfaActionSubmit.htm "+UserId);
			try {
				
				String rfadate=req.getParameter("rfadate");
				String EmpId=req.getParameter("EmpId");
				String projectid=req.getParameter("projectid");
				String projectType=req.getParameter("projectType");
				String priority=req.getParameter("priority");
				String statement=req.getParameter("statement");
				String description=req.getParameter("description");
				String reference=req.getParameter("reference");
				String [] assignee=req.getParameterValues("assignee");
			//	String rfano=req.getParameter("rfano");
				String rfanotype=req.getParameter("rfanotype");
				String [] CCEmpName=req.getParameterValues("CCEmpName");
				
				RfaActionDto rfa = new RfaActionDto();
				
				rfa.setRfaDate(sdf.parse(rfadate));
				rfa.setProjectType(projectType);
				rfa.setProjectId(Long.parseLong(projectid));
				rfa.setPriorityId(Long.parseLong(priority));
				rfa.setStatement(statement);
				rfa.setDescription(description);
				rfa.setReference(reference);
				rfa.setAssignorId(EmpId);
				rfa.setRfaTypeId(rfanotype);
				rfa.setMultipartfile(attachment);
				rfa.setAssignorAttachment(attachment.getOriginalFilename());
				rfa.setActionBy(Long.parseLong(EmpId));
				rfa.setTypeOfRfa(req.getParameter("type"));
				rfa.setVendorCode(req.getParameter("vendor")!=null?req.getParameter("vendor").split("/")[0]:"-");
				
				if(Arrays.asList("1","2","3","4").contains(rfanotype)){
				rfa.setBoxNo(req.getParameter("boxno"));
				rfa.setSWdate(sdf.parse(req.getParameter("swdate")));
				rfa.setRigVersion(req.getParameter("RigVersion"));
				rfa.setFPGA(req.getParameter("FPGA"));				
				}
				if(req.getParameter("rfaRaisedBy")!=null) {
				rfa.setRfaRaisedByName(req.getParameter("rfaRaisedBy"));
				}
				Long count=service.RfaActionSubmit(rfa,LabCode,UserId,assignee,CCEmpName);
				
				if (count > 0) {
					redir.addAttribute("result", "RFA Added Successfully");
				} else {
					redir.addAttribute("resultfail", "RFA Adding Unsuccessfull");
				}
				
		   	    redir.addAttribute("projectType", projectType);
			    if(projectType.equalsIgnoreCase("P")) {
			    	redir.addAttribute("projectId",projectid);
			    }else {
				    redir.addAttribute("initiationId", projectid);
			    }
				
			    }catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside RfaActionSubmit.htm "+UserId, e);
				}
	 		return "redirect:/RfaAction.htm";
	 		
	 	}
		
		
		@RequestMapping(value = "RfaActionPrint.htm", method = {RequestMethod.GET, RequestMethod.POST})
	 	public void RfaActionPrint(HttpServletResponse res , HttpServletRequest req ,HttpSession ses , RedirectAttributes redir)throws Exception
	 	{
			String UserId = (String) ses.getAttribute("Username");
			String LabCode =(String ) ses.getAttribute("labcode");
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String LoginType=(String)ses.getAttribute("LoginType");
			logger.info(new Date() +"Inside RfaActionPrint.htm "+UserId);
			
		try {
			
		        String rfaData=req.getParameter("rfaid");
		        String[] rfadetails = rfaData.split(",");
		        String rfaid=rfadetails[0];
//		        String printname=rfadetails[1]+"/"+rfadetails[2]+"/"+rfadetails[3]+"/"+rfadetails[4];
		        String printname=rfadetails[1];
		        String projectCode=rfadetails[2];
		        String result=printname.replace('/', '-');
			    Object[] RfaPrintData = service.RfaPrintData(rfaid);

			    String rfaType = RfaPrintData[22].toString();
			    List<Object[]> AssigneeList =service.AssigneeEmpList();
		        if(rfaType.equalsIgnoreCase("F")) {
		        	AssigneeList = service.assigneeListForFromExternal();
		        }
				req.setAttribute("RfaPrint", RfaPrintData);
				req.setAttribute("ProjectList", service.ProjectList());		
				req.setAttribute("ProjectTypeList",service.ProjectTypeList());
				req.setAttribute("PriorityList", service.PriorityList());
				req.setAttribute("EmployeeList", service.EmployeeList(LabCode));
			    req.setAttribute("LabDetails", service.RfaLabDetails(LabCode)); 
			    req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(LabCode)); 
			    req.setAttribute("AssigneeEmplList", AssigneeList);
			    req.setAttribute("RfaNoTypeList", service.getRfaNoTypeList());
			    
			    List<String> assignorCC =service.CCAssignorList(rfaid); 
	
			    req.setAttribute("CCTdeEmplList", assignorCC);
			    
			 Object[]attachmentData=service.RfaAttachmentDownload(rfaid);
			    
			String filename=result;
			
			String path=req.getServletContext().getRealPath("/view/temp");
		  	req.setAttribute("path",path);
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/action/RfaActionPrint.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();
			
//			pmsFileUtils.addWatermarktoPdf1(path + "/" + filename +  ".pdf", path + "/" + filename +  "1.pdf", "CANCEL");
			
			byte[] data = html.getBytes();
			InputStream fis1 = new ByteArrayInputStream(data);
			PdfDocument pdfDoc = new PdfDocument(new PdfWriter(path + "/" + filename +  ".pdf"));
			pdfDoc.setTagged();
			Document document = new Document(pdfDoc, PageSize.LEGAL);
			document.setMargins(50, 100, 150, 50);
			ConverterProperties converterProperties = new ConverterProperties();
			FontProvider dfp = new DefaultFontProvider(true, true, true);
			converterProperties.setFontProvider(dfp);
			HtmlConverter.convertToPdf(fis1, pdfDoc, converterProperties);
			//new
			PdfWriter pdfw=new PdfWriter(path +File.separator+ "mergedb.pdf");
	        PdfDocument pdfDocs = new PdfDocument(pdfw);
	        pdfw.setCompressionLevel(CompressionConstants.BEST_COMPRESSION);
	        PdfDocument pdfDocMain = new PdfDocument(new PdfReader(path+File.separator+filename+".pdf"),new PdfWriter(path+File.separator+filename+"Maintemp.pdf"));
	        Document docMain = new Document(pdfDocMain,PageSize.A4);
	        docMain.setMargins(50, 50, 50, 50);
	  
	        docMain.close();
	        pdfDocMain.close();
	        Path pathOfFileMain= Paths.get( path+File.separator+filename+".pdf");
	        //Files.delete(pathOfFileMain);	
	        
	        PdfReader pdf1=new PdfReader(path+File.separator+filename+"Maintemp.pdf");
	        PdfDocument pdfDocument = new PdfDocument(pdf1, pdfw);
	        PdfMerger merger = new PdfMerger(pdfDocument);
	        
	        
	        if(attachmentData!=null) {
	        	try {
	        		if(attachmentData[6]!=null) {
		        		
	        			if(FilenameUtils.getExtension(attachmentData[6].toString()).equalsIgnoreCase("pdf")) {
	        				Path pdfPath1 = Paths.get(uploadpath, LabCode,"RFAFiles",projectCode,printname.replace('/', '_'),attachmentData[6].toString());
	        			    PdfReader pdfReader1 = new PdfReader(pdfPath1.toString());
	        		        PdfDocument pdfDocument2 = new PdfDocument(pdfReader1,new PdfWriter(path+File.separator+filename+"temp.pdf"));
	        		        Document document5 = new Document(pdfDocument2,PageSize.A4);
	        		        document5.setMargins(50, 50, 50, 50);
	        		        Rectangle pageSize;
	        		        PdfCanvas canvas;
	        		        PdfPage page = pdfDocument2.getPage(1);
	        		            pageSize = page.getPageSize();
	        		            canvas = new PdfCanvas(page);
	        		            Rectangle recta=new Rectangle(10,pageSize.getHeight()-50,40,40);
	        		           // canvas.addImage(leftLogo, recta, false);
	        		            Rectangle recta2=new Rectangle(pageSize.getWidth()-50,pageSize.getHeight()-50,40,40);
	        		            //canvas.addImage(rightLogo, recta2, false);
	        		            canvas.beginText().setFontAndSize(
	        		                PdfFontFactory.createFont(StandardFonts.HELVETICA), 15)
	        		                .moveText(pageSize.getWidth() / 3, pageSize.getHeight() - 45)
	        		                 .showText("Closure Report (RFA)")
	        		                  .endText();

	        		   
	        		        document5.close();
	        		        pdfDocument2.close();
	        	        	PdfReader pdf2=new PdfReader(path+"/"+filename+"temp.pdf");
	        	        	 PdfDocument pdfDocument3 = new PdfDocument(pdf2);
	        		        merger.merge(pdfDocument3, 1, pdfDocument3.getNumberOfPages());
	        		        
	        		        pdfDocument3.close();
	        		        pdf2.close();
	        		        Path pathOfFile1= Paths.get( path+File.separator+filename+"temp.pdf");
	        		        Files.delete(pathOfFile1);	
	        			}
					} 
	        		if(attachmentData[3]!=null) {
		        		
	        			if(FilenameUtils.getExtension(attachmentData[3].toString()).equalsIgnoreCase("pdf")) {
	        				Path pdfPath = Paths.get(uploadpath, LabCode,"RFAFiles",projectCode,printname.replace('/', '_'),attachmentData[3].toString());
	        			    PdfReader pdfReader = new PdfReader(pdfPath.toString());
	        		        PdfDocument pdfDocument1 = new PdfDocument(pdfReader,new PdfWriter(path+File.separator+filename+"temp.pdf"));
	        		        Document document4 = new Document(pdfDocument1,PageSize.A4);
	        		        document4.setMargins(50, 50, 50, 50);
	        		        Rectangle pageSize;
	        		        PdfCanvas canvas;
	        		        int n = pdfDocument1.getNumberOfPages();
	        		
	        		            PdfPage page = pdfDocument1.getPage(1);
	        		            pageSize = page.getPageSize();
	        		            canvas = new PdfCanvas(page);
	        		            Rectangle recta=new Rectangle(10,pageSize.getHeight()-50,40,40);
	        		           // canvas.addImage(leftLogo, recta, false);
	        		            Rectangle recta2=new Rectangle(pageSize.getWidth()-50,pageSize.getHeight()-50,40,40);
	        		            //canvas.addImage(rightLogo, recta2, false);
	        		            canvas.beginText().setFontAndSize(
	        		                PdfFontFactory.createFont(StandardFonts.HELVETICA), 20)
	        		                .moveText(pageSize.getWidth() / 3  , pageSize.getHeight() - 25)
	        		                 .showText("RFA Attachment")
	        		                  .endText();

	        		   
	        		        document4.close();
	        		        pdfDocument1.close();
	        	        	PdfReader pdf4=new PdfReader(path+"/"+filename+"temp.pdf");
	        	        	PdfDocument pdfDocument4 = new PdfDocument(pdf4);
	        		        merger.merge(pdfDocument4, 1, pdfDocument4.getNumberOfPages());
	        		        
	        		        pdfDocument4.close();
	        		        pdf4.close();
	        		        Path pathOfFile= Paths.get( path+File.separator+filename+"temp.pdf");
	        		        Files.delete(pathOfFile);	
	        			}
					} 
	        		
	        		
	        	if(attachmentData[4]!=null) {
	        		
	        			if(FilenameUtils.getExtension(attachmentData[4].toString()).equalsIgnoreCase("pdf")) {
	        				Path pdfPath1 = Paths.get(uploadpath, LabCode,"RFAFiles",projectCode,printname.replace('/', '_'),attachmentData[4].toString());
	        			    PdfReader pdfReader1 = new PdfReader(pdfPath1.toString());
	        		        PdfDocument pdfDocument2 = new PdfDocument(pdfReader1,new PdfWriter(path+File.separator+filename+"temp.pdf"));
	        		        Document document5 = new Document(pdfDocument2,PageSize.A4);
	        		        document5.setMargins(50, 50, 50, 50);
	        		        Rectangle pageSize;
	        		        PdfCanvas canvas;
	        		        PdfPage page = pdfDocument2.getPage(1);
	        		            pageSize = page.getPageSize();
	        		            canvas = new PdfCanvas(page);
	        		            Rectangle recta=new Rectangle(10,pageSize.getHeight()-50,40,40);
	        		           // canvas.addImage(leftLogo, recta, false);
	        		            Rectangle recta2=new Rectangle(pageSize.getWidth()-50,pageSize.getHeight()-50,40,40);
	        		            //canvas.addImage(rightLogo, recta2, false);
	        		            canvas.beginText().setFontAndSize(
	        		                PdfFontFactory.createFont(StandardFonts.HELVETICA), 20)
	        		                .moveText(pageSize.getWidth() / 3, pageSize.getHeight() - 25)
	        		                 .showText("Closure Attachment")
	        		                  .endText();

	        		   
	        		        document5.close();
	        		        pdfDocument2.close();
	        	        	PdfReader pdf2=new PdfReader(path+"/"+filename+"temp.pdf");
	        	        	 PdfDocument pdfDocument3 = new PdfDocument(pdf2);
	        		        merger.merge(pdfDocument3, 1, pdfDocument3.getNumberOfPages());
	        		        
	        		        pdfDocument3.close();
	        		        pdf2.close();
	        		        Path pathOfFile1= Paths.get( path+File.separator+filename+"temp.pdf");
	        		        Files.delete(pathOfFile1);	
	        			}
					} 
	        	

	        	}
	        	
	        	catch (Exception e) {
					
				}
	        }
	        
	        pdfDocument.close();
	        merger.close();

	        pdf1.close();	       
	        pdfw.close();
	        res.setContentType("application/pdf");
	        res.setHeader("Content-disposition","inline;filename="+filename+".pdf");
	        
	        if(RfaPrintData[11]!=null && RfaPrintData[11].toString().equalsIgnoreCase("RFC")) {
	        pmsFileUtils.addWatermarktoPdf1(path +File.separator+ "mergedb.pdf", path +File.separator+ "mergedb1.pdf", "CANCEL");
	        }
	        
	        File f=new File(path +File.separator+ "mergedb.pdf");
			FileInputStream fis = new FileInputStream(f);
			DataOutputStream os = new DataOutputStream(res.getOutputStream());
			res.setHeader("Content-Length", String.valueOf(f.length()));
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fis.read(buffer)) >= 0) {
			os.write(buffer, 0, len);
			}
			os.close();
			fis.close();
			Path pathOfFile2 = Paths.get(path + "/" + filename  + ".pdf");
			//Files.delete(pathOfFile2);

 		
	 	}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside RfaActionPrint.htm "+UserId, e);
		}
		
	
	 }
		
		
		@RequestMapping(value = "RfaActionForward.htm", method = RequestMethod.POST)
		public String RfaActionForward(HttpServletRequest req,HttpServletResponse res, HttpSession ses, RedirectAttributes redir) throws Exception {
			
		       String UserId = (String) ses.getAttribute("Username");
		       String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		       String LabCode =(String ) ses.getAttribute("labcode");
			logger.info(new Date() +"Inside RfaActionForward.htm "+UserId);		
			try {

				String projectid=req.getParameter("projectid");
			    String rfa=req.getParameter("RFAID");
			    String status=req.getParameter("rfaoptionby");
			    String rfaEmpId=req.getParameter("rfaEmpModal");
			    String projectType=req.getParameter("projectType");
			    String projectId=req.getParameter("projectId");
			    

			  long count=service.RfaActionForward(status,projectid,UserId,rfa,EmpId,rfaEmpId);
			  
			  List<String> rfaMailSend = service.rfaMailSend(rfa);

		
			  if (count > 0) {
				  
				  if(status.equalsIgnoreCase("AV") || status.equalsIgnoreCase("AP")) {
					  
					    String rfaid=rfa;
				     
					    Object[] RfaPrintData = service.RfaPrintData(rfaid);
					  
						req.setAttribute("RfaPrint", RfaPrintData);
						req.setAttribute("ProjectList", service.ProjectList());		
						req.setAttribute("ProjectTypeList",service.ProjectTypeList());
						req.setAttribute("PriorityList", service.PriorityList());
						req.setAttribute("EmployeeList", service.EmployeeList(LabCode));
					    req.setAttribute("LabDetails", service.RfaLabDetails(LabCode)); 
					    req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(LabCode)); 
					    req.setAttribute("AssigneeEmplList", service.AssigneeEmpList());
					    req.setAttribute("RfaNoTypeList", service.getRfaNoTypeList());
					    
					    List<String> assignorCC =service.CCAssignorList(rfaid); 
					    req.setAttribute("CCTdeEmplList", assignorCC);
					    
					 Object[]attachmentData=service.RfaAttachmentDownload(rfaid);
		//			 List<String> rfaPrintData = attachmentData!=null ? Arrays.asList( attachmentData[2].toString().split("/")): new ArrayList<>(); 
					 String projectCode =  attachmentData!=null?attachmentData[7]+"":"";
					 String newRfaNo = attachmentData!=null? attachmentData[5].toString().replace("/","_"):"-";
					    
					String RFANo=RfaPrintData[3].toString();
					String filename="RFA Attachment ";
					
					String path=req.getServletContext().getRealPath("/view/temp");
				  	req.setAttribute("path",path);
					CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
					req.getRequestDispatcher("/view/action/RfaActionPrint.jsp").forward(req, customResponse);
					String html = customResponse.getOutput();
					
					byte[] data = html.getBytes();
					InputStream fis1 = new ByteArrayInputStream(data);
					PdfDocument pdfDoc = new PdfDocument(new PdfWriter(path + "/" + filename +  ".pdf"));
					pdfDoc.setTagged();
					Document document = new Document(pdfDoc, PageSize.LEGAL);
					document.setMargins(50, 100, 150, 50);
					ConverterProperties converterProperties = new ConverterProperties();
					FontProvider dfp = new DefaultFontProvider(true, true, true);
					converterProperties.setFontProvider(dfp);
					HtmlConverter.convertToPdf(fis1, pdfDoc, converterProperties);
					//new
					PdfWriter pdfw=new PdfWriter(path +File.separator+ "RFA.pdf");
			        PdfDocument pdfDocs = new PdfDocument(pdfw);
			        pdfw.setCompressionLevel(CompressionConstants.BEST_COMPRESSION);
			        PdfDocument pdfDocMain = new PdfDocument(new PdfReader(path+File.separator+filename+".pdf"),new PdfWriter(path+File.separator+filename+"Maintemp.pdf"));
			        Document docMain = new Document(pdfDocMain,PageSize.A4);
			        docMain.setMargins(50, 50, 50, 50);
			  
			        docMain.close();
			        pdfDocMain.close();
			        Path pathOfFileMain= Paths.get( path+File.separator+filename+".pdf");
			        //Files.delete(pathOfFileMain);	
			        
			        PdfReader pdf1=new PdfReader(path+File.separator+filename+"Maintemp.pdf");
			        PdfDocument pdfDocument = new PdfDocument(pdf1, pdfw);
			        PdfMerger merger = new PdfMerger(pdfDocument);
			        
			        if(attachmentData!=null) {
			        	try {
			        		
			        		if(attachmentData[3]!=null) {
				        		
			        			if(FilenameUtils.getExtension(attachmentData[3].toString()).equalsIgnoreCase("pdf")) {
			        				Path pdfPath = Paths.get(uploadpath, LabCode,"RFAFiles",projectCode,newRfaNo,attachmentData[3].toString());
			        			    PdfReader pdfReader = new PdfReader(pdfPath.toString());
			        		        PdfDocument pdfDocument1 = new PdfDocument(pdfReader,new PdfWriter(path+File.separator+filename+"temp.pdf"));
			        		        Document document4 = new Document(pdfDocument1,PageSize.A4);
			        		        document4.setMargins(50, 50, 50, 50);
			        		        Rectangle pageSize;
			        		        PdfCanvas canvas;
			        		        int n = pdfDocument1.getNumberOfPages();
			        		
			        		            PdfPage page = pdfDocument1.getPage(1);
			        		            pageSize = page.getPageSize();
			        		            canvas = new PdfCanvas(page);
			        		            Rectangle recta=new Rectangle(10,pageSize.getHeight()-50,40,40);
			        		           // canvas.addImage(leftLogo, recta, false);
			        		            Rectangle recta2=new Rectangle(pageSize.getWidth()-50,pageSize.getHeight()-50,40,40);
			        		            //canvas.addImage(rightLogo, recta2, false);
			        		            canvas.beginText().setFontAndSize(
			        		                PdfFontFactory.createFont(StandardFonts.HELVETICA), 20)
			        		                .moveText(pageSize.getWidth() / 3  , pageSize.getHeight() - 25)
			        		                 .showText("RFA Attachment")
			        		                  .endText();

			        		   
			        		        document4.close();
			        		        pdfDocument1.close();
			        	        	PdfReader pdf4=new PdfReader(path+"/"+filename+"temp.pdf");
			        	        	PdfDocument pdfDocument4 = new PdfDocument(pdf4);
			        		        merger.merge(pdfDocument4, 1, pdfDocument4.getNumberOfPages());
			        		        
			        		        pdfDocument4.close();
			        		        pdf4.close();
			        		        Path pathOfFile= Paths.get( path+File.separator+filename+"temp.pdf");
			        		        Files.delete(pathOfFile);	
			        			}
							} 
			        		
			        		
			        	if(attachmentData[4]!=null) {
			        		
			        			if(FilenameUtils.getExtension(attachmentData[4].toString()).equalsIgnoreCase("pdf")) {
			        				Path pdfPath1 = Paths.get(uploadpath, LabCode,"RFAFiles",projectCode,newRfaNo,attachmentData[4].toString());
			        			    PdfReader pdfReader1 = new PdfReader(pdfPath1.toString());
			        		        PdfDocument pdfDocument2 = new PdfDocument(pdfReader1,new PdfWriter(path+File.separator+filename+"temp.pdf"));
			        		        Document document5 = new Document(pdfDocument2,PageSize.A4);
			        		        document5.setMargins(50, 50, 50, 50);
			        		        Rectangle pageSize;
			        		        PdfCanvas canvas;
			        		        PdfPage page = pdfDocument2.getPage(1);
			        		            pageSize = page.getPageSize();
			        		            canvas = new PdfCanvas(page);
			        		            Rectangle recta=new Rectangle(10,pageSize.getHeight()-50,40,40);
			        		           // canvas.addImage(leftLogo, recta, false);
			        		            Rectangle recta2=new Rectangle(pageSize.getWidth()-50,pageSize.getHeight()-50,40,40);
			        		            //canvas.addImage(rightLogo, recta2, false);
			        		            canvas.beginText().setFontAndSize(
			        		                PdfFontFactory.createFont(StandardFonts.HELVETICA), 20)
			        		                .moveText(pageSize.getWidth() / 3, pageSize.getHeight() - 25)
			        		                 .showText("Closure Attachment")
			        		                  .endText();

			        		   
			        		        document5.close();
			        		        pdfDocument2.close();
			        	        	PdfReader pdf2=new PdfReader(path+"/"+filename+"temp.pdf");
			        	        	 PdfDocument pdfDocument3 = new PdfDocument(pdf2);
			        		        merger.merge(pdfDocument3, 1, pdfDocument3.getNumberOfPages());
			        		        
			        		        pdfDocument3.close();
			        		        pdf2.close();
			        		        Path pathOfFile1= Paths.get( path+File.separator+filename+"temp.pdf");
			        		        Files.delete(pathOfFile1);	
			        			}
							} 
			        	}
			        	
			        	catch (Exception e) {
							
						}
			        }
			        
			        pdfDocument.close();
			        merger.close();

			        pdf1.close();	       
			        pdfw.close();
			        res.setContentType("application/pdf");
			        res.setHeader("Content-disposition","inline;filename="+filename+".pdf"); 
			        File f=new File(path +File.separator+ "RFA.pdf");
					  
			    	String typeOfHost = "L";
					MailConfigurationDto mailAuthentication=null;
					try {
						mailAuthentication = mailService.getMailConfigByTypeOfHost(typeOfHost);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					String from = mailAuthentication.getUsername().toString();
					String hostAddress = mailAuthentication.getHost().toString();
					final String username = mailAuthentication.getUsername().toString();
					final String password = mailAuthentication.getPassword().toString(); 
					final String port = mailAuthentication.getPort().toString(); 
			
						Properties properties = System.getProperties(); 
						properties.setProperty("mail.smtp.host", hostAddress);
						properties.put("mail.smtp.ssl.enable", "true"); //TLS
						properties.put("mail.smtp.port", port); 
						properties.put("mail.smtp.auth", "true"); 
						properties.put("mail.smtp.socketFactory.class","jakarta.net.ssl.SSLSocketFactory");
						properties.put("mail.smtp.ssl.checkserveridentity", false);
						properties.put("mail.smtp.ssl.trust", hostAddress);
						
						Session session = Session.getDefaultInstance(properties,
							new jakarta.mail.Authenticator() {
								protected PasswordAuthentication getPasswordAuthentication() {
									return new PasswordAuthentication(from,password);
								}
							});	
						try {
							MimeMessage message = new MimeMessage(session);
							message.setFrom(new InternetAddress(from));
							message.addRecipient(Message.RecipientType.TO, new InternetAddress(from));
							for (String ccRecipient : rfaMailSend) {
								message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccRecipient));
							}
							message.setSubject("RFA "+RFANo);
							MimeBodyPart part1 = new MimeBodyPart();
							
							part1.setText("This is a System generated mail please Don't reply.");
							MimeBodyPart part2 = new MimeBodyPart();
							part2.attachFile(f);
							MimeMultipart mimeMultipart = new MimeMultipart();
							mimeMultipart.addBodyPart(part1);
							mimeMultipart.addBodyPart(part2);
							message.setContent(mimeMultipart);
							Transport.send(message);
					} catch (MessagingException mex) {
						mex.printStackTrace();
					} 	
						
				  }
				  if(status.equalsIgnoreCase("AF") || status.equalsIgnoreCase("AX")) {
					  redir.addAttribute("result", "RFA Forwarded Successfully");
					  redir.addAttribute("projectType", projectType);
					  if(projectType.equalsIgnoreCase("P")) {
						redir.addAttribute("projectId",projectId);
					  }else {
						redir.addAttribute("initiationId", projectId);
					  }
				  }else if( status.equalsIgnoreCase("AC")||status.equalsIgnoreCase("AV")){
					  redir.addAttribute("result", "RFA Forwarded Successfully");
					  return "redirect:/RfaActionForwardList.htm";
		          }else if(status.equalsIgnoreCase("ARC")){
		        	  redir.addAttribute("result", "RFA Close Successfully");
		        	  redir.addAttribute("projectType", projectType);
					  if(projectType.equalsIgnoreCase("P")) {
						redir.addAttribute("projectId",projectId);
					  }else {
						redir.addAttribute("initiationId", projectId);
					  }
		        	  return "redirect:/RfaAction.htm";
		          }else if(status.equalsIgnoreCase("AY") || status.equalsIgnoreCase("RFA")){
		        	  redir.addAttribute("result", "RFA Forwarded Successfully");
		        	  return "redirect:/RfaInspection.htm";
		          }else if(status.equalsIgnoreCase("AR")){
		        	  redir.addAttribute("result", "RFA Forwarded Successfully");
		        	  return "redirect:/RfaInspectionApproval.htm";
		          }else if(status.equalsIgnoreCase("AP")) {
		        	  redir.addAttribute("result", "RFA Approved Successfully");
		        	  return "redirect:/RfaInspectionApproval.htm";
		          }
				  else {
		        	  redir.addAttribute("result", "RFA Forward Unsuccessful");
		          }
			  }
			  	
			}
			catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside RfaActionForward.htm "+UserId, e);
			}
			
			return "redirect:/RfaAction.htm";

		}
	 

		@RequestMapping(value = "RfaActionForwardList.htm", method = RequestMethod.GET)
		public String RfaActionForwardList(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
			
		       String UserId = (String) ses.getAttribute("Username");
		       String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		       String LabCode =(String ) ses.getAttribute("labcode");
		       String assignDetails = null;
		     
			logger.info(new Date() +"Inside RfaActionForwardList.htm "+UserId);		
			try {
				List<Object[]> RfaForwardList = service.RfaForwardList(EmpId);
				List<Object[]> RfaForwardApprovedList = service.RfaForwardApprovedList(EmpId);
				
				for(int i=0;i<RfaForwardList.size();i++) {
					Long rfaId = Long.parseLong(RfaForwardList.get(i)[0].toString());
					
					 assignDetails = service.getAssignDetails(EmpId,rfaId);
					
				}
				req.setAttribute("RfaForwardList", RfaForwardList);
				req.setAttribute("ModalTDList", service.getRfaTDList(LabCode));
				req.setAttribute("RfaForwardApprovedList", RfaForwardApprovedList);
				req.setAttribute("EmpId", EmpId);
				req.setAttribute("rfaCount", assignDetails);
				
			}
			catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside RfaActionForwardList.htm "+UserId, e);
			}
			return "action/RfaForwardList";

		}
		
		@RequestMapping(value = "RfaInspectionApproval.htm", method = RequestMethod.GET)
		public String RfaInspectionApprovalList(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
			
		       String UserId = (String) ses.getAttribute("Username");
		       String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		       String LabCode =(String ) ses.getAttribute("labcode");
		       String assignDetails = null;
		     
			logger.info(new Date() +"Inside RfaInspectionApproval.htm "+UserId);		
			try {
				List<Object[]> RfaInspectionApprovalList = service.RfaInspectionApprovalList(EmpId);
				List<Object[]> RfaInspectionApprovedList = service.RfaInspectionApprovedList(EmpId);
				for(int i=0;i<RfaInspectionApprovalList.size();i++) {
					Long rfaId = Long.parseLong(RfaInspectionApprovalList.get(i)[0].toString());
					
					 assignDetails = service.getAssignDetails(EmpId,rfaId);
					//rfaList.add(assignDetails);  
					
				}
				req.setAttribute("RfaInspectionApprovalList", RfaInspectionApprovalList);
				req.setAttribute("RfaInspectionApprovedList", RfaInspectionApprovedList);
				req.setAttribute("ModalTDList", service.getRfaTDList(LabCode));
				req.setAttribute("EmpId", EmpId);
				req.setAttribute("rfaCount", assignDetails);
				
			}
			catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside RfaInspectionApproval.htm "+UserId, e);
			}
			return "action/RfaInspectionApproval";

		}
		@RequestMapping(value = "RfaAssignFormSubmit.htm", method = RequestMethod.POST)
		public String RfaModalSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir,
				@RequestPart("attachment") MultipartFile attachment) throws Exception {
			
		       String UserId = (String) ses.getAttribute("Username");
		       String LabCode =(String ) ses.getAttribute("labcode");
		       String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		     
			logger.info(new Date() +"Inside RfaAssignFormSubmit.htm "+UserId);		
			try {
				
				Long count=0l;
				String rfaid = req.getParameter("rfaId");
				String RfaNo = req.getParameter("RfaNo");
				String fdate = req.getParameter("fdate");
				String observation = req.getParameter("observation");
				String clarification = req.getParameter("clarification");
				String action = req.getParameter("Rfaaction");
				
				if(service.RfaAssignAjax(rfaid)==null) {
					
				RfaInspection inspection = new RfaInspection();
				
				inspection.setRfaId(Long.parseLong(rfaid));
				inspection.setRfaNo(RfaNo);
				inspection.setLabCode(LabCode);
				inspection.setObservation(observation);
				inspection.setCompletionDate(new java.sql.Date(sdf2.parse(fdate).getTime()));
				inspection.setClarification(clarification);
				inspection.setActionRequired(action);
				inspection.setEmpId(Long.parseLong(EmpId));
				inspection.setRfaStatus("AAA");
				inspection.setCreatedBy(UserId);
				inspection.setIsActive(1);
				
				RfaActionDto rfa = new RfaActionDto();
				rfa.setRfaId(Long.parseLong(rfaid));
				rfa.setLabCode(LabCode);
				rfa.setModifiedBy(UserId);
				rfa.setCreatedBy(UserId);
				rfa.setMultipartfile(attachment);
				rfa.setAssigneAttachment(attachment.getOriginalFilename());
				
				 count=service.RfaModalSubmit(inspection,rfa);

					if (count > 0) {
						redir.addAttribute("result", "RFA Inspection Added Successfully");
					} else {
						redir.addAttribute("resultfail", "RFA Inspection Add Unsuccessfully");
					}

				}else {
					RfaInspection inspection = new RfaInspection();
					
					inspection.setRfaId(Long.parseLong(rfaid));
					inspection.setRfaNo(RfaNo);
					inspection.setLabCode(LabCode);
					inspection.setObservation(observation);
					inspection.setCompletionDate(new java.sql.Date(sdf2.parse(fdate).getTime()));
					inspection.setClarification(clarification);
					inspection.setActionRequired(action);
					inspection.setEmpId(Long.parseLong(EmpId));
					inspection.setRfaStatus("AE");
					inspection.setModifiedBy(UserId);
					inspection.setIsActive(1);
					
					RfaActionDto rfa = new RfaActionDto();
					rfa.setRfaId(Long.parseLong(rfaid));
					rfa.setLabCode(LabCode);
					rfa.setModifiedBy(UserId);
					rfa.setCreatedBy(UserId);
					rfa.setMultipartfile(attachment);
					rfa.setAssigneAttachment(attachment.getOriginalFilename());

					count=service.RfaModalUpdate(inspection,rfa);

					
					if (count > 0) {
						redir.addAttribute("result", "RFA Inspection Updated Successfully");
					} else {
						redir.addAttribute("resultfail", "RFA Inspection Update Unsuccessfully");
					}
				}
			

				
			}
			catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside RfaAssignFormSubmit.htm "+UserId, e);
			}
			return "redirect:/RfaInspection.htm";

		}
	 
		@RequestMapping(value="RfaAssignAjax.htm",method=RequestMethod.GET)
	    public @ResponseBody String RfaAssignAjax(HttpSession ses, HttpServletRequest req) throws Exception {
			
			 Gson json = new Gson();
		   	 String UserId=(String)ses.getAttribute("Username");
		   	 
		   	logger.info(new Date() +"Inside RfaAssignAjax.htm"+UserId);
		   	
		   	Object[]RfaAssignAjax=null;
		   	try {
		   		String rfaId=req.getParameter("rfaId");
		   		
		   		RfaAssignAjax = service.RfaAssignAjax(rfaId);
		   	}
		   	catch (Exception e) {
		   		e.printStackTrace();
		   		logger.error(new Date() +"Inside RfaAssignAjax.htm"+UserId ,e);
		   	}
		   	
		return json.toJson(RfaAssignAjax);
		}

		
		@RequestMapping(value = "RfaActionReturnList.htm", method = RequestMethod.POST)
		public String RfaActionReturnList(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {	
		
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			 String UserId = (String) ses.getAttribute("Username");
			 logger.info(new Date() +"Inside RfaActionReturnList.htm "+UserId);	
			 
			 try {
				 
			 String status=req.getParameter("RfaStatus");
			 String assignee=req.getParameter("assignee");
			 String assignor=req.getParameter("assignor");
			 String rfa=req.getParameter("rfa");
			 String replyMsg=req.getParameter("replyMsg");
			 String projectType=req.getParameter("revokeProjType");
			 String projectId=req.getParameter("revokeProjId");
			
			 // for revoke
			 if(status.equalsIgnoreCase("AF") || status.equalsIgnoreCase("AX") ) {
				 if(assignor.equalsIgnoreCase(EmpId) ) {
				 status="REV";
				 }
			 }
			
			String revoke= req.getParameter("revoke"); 

//			    List<String>assigneeList=service.AssigneeEmpList().stream().filter(k->k[0].toString().equalsIgnoreCase(rfa))
//			    						.map(j->j[3].toString())
//			    						.collect(Collectors.toList());
			 
			    if((status.equalsIgnoreCase("RFA") || status.equalsIgnoreCase("AY")) &&  revoke!=null && revoke.equalsIgnoreCase("Y")) {
			    	status="REK";
				}
			 
			 List<String> ReturnStatus1  = Arrays.asList("AF","AX","AC");
			 List<String> ReturnStatus2  = Arrays.asList("RFA","AY","AR");
			 
			 long count=service.RfaReturnList(status,UserId,rfa,EmpId,assignee,assignor,replyMsg);
			 
			 if(status.equalsIgnoreCase("REV")) {
				 if(count>0) {
				     redir.addAttribute("result", "RFA Revoked Successfully");
				 }else {
					 redir.addAttribute("result", "RFA Revoked Unsuccessful");
				 }
				redir.addAttribute("projectType", projectType);
				if(projectType.equalsIgnoreCase("P")) {
					redir.addAttribute("projectId",projectId);
				}else {
					redir.addAttribute("initiationId", projectId);
				}
				 return "redirect:/RfaAction.htm";
			 }
		
			 if(count==4l) {
				 redir.addAttribute("result", "RFA Revoked Successfully"); 
				 return "redirect:/RfaInspection.htm";
			 }
			 
			 if(status.equalsIgnoreCase("RFC")) {
				 redir.addAttribute("result", "RFA Cancelled Successfully");
				 redir.addAttribute("projectType", projectType);
				 if(projectType.equalsIgnoreCase("P")) {
					redir.addAttribute("projectId",projectId);
				 }else {
					redir.addAttribute("initiationId", projectId);
				 }
				 return "redirect:/RfaAction.htm";
			 }
			 
			 if(count>0) {
				 redir.addAttribute("result", "RFA Returned Successfully");
			 
			 }else {
				 redir.addAttribute("result", "RFA Returned Unsuccessful");
			 }
			 
			 if(ReturnStatus1.contains(status)) {
					return "redirect:/RfaActionForwardList.htm";
					
			 }if(ReturnStatus2.contains(status)) {
					return "redirect:/RfaInspectionApproval.htm";
			 }
			 if(status.equalsIgnoreCase("AV")) {
				 return "redirect:/RfaInspection.htm";
			 }
			 
			 }catch (Exception e) {
				e.printStackTrace();
			} 
			 
			 return "redirect:/RfaActionForwardList.htm";
          }
		
		@RequestMapping(value="getRfaAddData.htm",method=RequestMethod.GET)
	    public @ResponseBody String getRfaAddData(HttpSession ses, HttpServletRequest req) throws Exception {
			
			 Gson json = new Gson();
		   	 String UserId=(String)ses.getAttribute("Username");
		   	 
		   	logger.info(new Date() +"Inside getRfaAddData.htm"+UserId);
		   	
		   	Object[]rfaAddData=null;
		   	try {
		   		String rfaId=req.getParameter("rfaId");
		   		
		   		rfaAddData = service.getRfaAddData(rfaId);
		   	}
		   	catch (Exception e) {
		   		e.printStackTrace();
		   		logger.error(new Date() +"Inside getRfaAddData.htm"+UserId ,e);
		   	}
		   	
		return json.toJson(rfaAddData);
		}
		

        @RequestMapping(value="getrfaRemarks.htm",method=RequestMethod.GET)
       public @ResponseBody String getrfaRemarks(HttpSession ses, HttpServletRequest req) throws Exception {
	
	           Gson json = new Gson();
   	          String UserId=(String)ses.getAttribute("Username");
   	 
          	logger.info(new Date() +"Inside getrfaRemarks.htm"+UserId);
   	
           List<Object[]> rfaRemarkData=null;
    	try {
   		     String rfaId=req.getParameter("rfaId");
   		     String status=req.getParameter("status");
   		
   		     rfaRemarkData = service.getrfaRemarks(rfaId,status);
   	        }
       catch (Exception e) {
   		       e.printStackTrace();
   		      logger.error(new Date() +"Inside getrfaRemarks.htm"+UserId ,e);
         	}
   	
            return json.toJson(rfaRemarkData);
         }


              @RequestMapping(value = "MeettingActionReports.htm", method = {RequestMethod.GET, RequestMethod.POST})
              public String MeettingActionReports(HttpServletRequest req,HttpSession ses, RedirectAttributes redir) throws Exception 
              {
            	  String UserId=(String)ses.getAttribute("Username");
          		String LabCode = (String)ses.getAttribute("labcode");
          		logger.info(new Date() +"Inside MeettingActionReports.htm "+UserId);
          		try {
          			String Logintype= (String)ses.getAttribute("LoginType");
          			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
          			String projectid=req.getParameter("projectid");
          			String committeeid=req.getParameter("committeeid");
          			String meettingid=req.getParameter("meettingid");
          			String status = req.getParameter("status");
          			
          			List<Object[]> projectdetailslist=service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
          			
          			if(projectdetailslist.size()==0) 
          			{				
          				redir.addAttribute("resultfail", "No Project is Assigned to you.");
          				return "redirect:/MainDashBoard.htm";
          			}
          			
          			if(committeeid==null)
          			{
          				committeeid="1";
          			}
          			
          		
          			if(projectid==null || projectid.equals("null"))
          			{
          				projectid=projectdetailslist.get(0)[0].toString();
          			}
					
          			String scheduleid=null;
          			List<Object[]> meetingcount=service.MeettingCount(committeeid,projectid);
          			
          			List<String>list=new ArrayList<String>();
          			if(meetingcount.size()>0) {
          				list=meetingcount.stream().map(i -> i[0].toString()).collect(Collectors.toList());
          			}
          			
          			if(meettingid==null) {
          				if(meetingcount.size()<1) {
          					scheduleid="0";
              			}else {
          				scheduleid=meetingcount.get(0)[0].toString();
              			}
          			}else {
          				if(list.contains(meettingid)) {
          				scheduleid=meettingid;
          				}else if(list.size()>0) {
          					scheduleid=list.get(0);
          				}else {
          					scheduleid="0";
          				}
          			}
      
          			List<Object[]> projapplicommitteelist=service.ProjectApplicableCommitteeList(projectid);
          		
          			List<Object[]> meetinglist=service.MeettingList(committeeid,projectid,scheduleid,status);
          			
          			if(status==null)req.setAttribute("status", "N");
          			else req.setAttribute("status", status);
          			req.setAttribute("scheduleid", scheduleid);
          			req.setAttribute("projectid",projectid);
          			req.setAttribute("committeeid",committeeid);
          			req.setAttribute("Projectdetails",projectdetailslist.get(0));
          			req.setAttribute("ProjectsList",projectdetailslist);
          			req.setAttribute("meetingcount", meetingcount);
          			req.setAttribute("meetinglist", meetinglist);
          			req.setAttribute("projapplicommitteelist",projapplicommitteelist);
          		}			
          		catch (Exception e) 
          		{
          			e.printStackTrace(); 
          			logger.error(new Date() +"Inside MeettingActionReports.htm "+UserId,e);
          		}
 
                  return "action/MeetingActionReports"; 
               }

      
              @RequestMapping(value = "MeettingAction.htm", method = {RequestMethod.GET, RequestMethod.POST})
              public String MeettingAction(HttpServletRequest req,HttpSession ses, RedirectAttributes redir) throws Exception 
              {
            	  String UserId=(String)ses.getAttribute("Username");
          		String LabCode = (String)ses.getAttribute("labcode");
          		logger.info(new Date() +"Inside MeettingAction.htm "+UserId);
          		try {
          			String Logintype= (String)ses.getAttribute("LoginType");
          			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
          			String projectid=req.getParameter("projectid");
          			String committeeid=req.getParameter("committeeid");
          			String meettingid=req.getParameter("meettingid");
          			List<Object[]> projectdetailslist=service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
          			if(projectdetailslist.size()==0) 
          			{				
          				redir.addAttribute("resultfail", "No Project is Assigned to you.");
          				return "redirect:/MainDashBoard.htm";
          			}
          			if(committeeid==null)
          			{
          				committeeid="1";
          			}
          			if(projectid==null || projectid.equals("null"))
          			{
          				projectid=projectdetailslist.get(0)[0].toString();
          			}
					
					/*
					 * String scheduleid=null; List<Object[]>
					 * meetingcount=service.MeettingCount(committeeid,projectid);
					 * 
					 * if(meettingid==null) { if(meetingcount.size()<1) { scheduleid="0"; }else {
					 * scheduleid=meetingcount.get(0)[0].toString(); } }else {
					 * scheduleid=meettingid; }
					 * 
					 */
          			
          			String scheduleid=null;
          			List<Object[]> meetingcount=service.MeettingCount(committeeid,projectid);
          			
          			List<String>list=new ArrayList<String>();
          			if(meetingcount.size()>0) {
          				list=meetingcount.stream().map(i -> i[0].toString()).collect(Collectors.toList());
          			}
          			
          			if(meettingid==null) {
          				if(meetingcount.size()<1) {
          					scheduleid="0";
              			}else {
          				scheduleid=meetingcount.get(0)[0].toString();
              			}
          			}else {
          				if(list.contains(meettingid)) {
          				scheduleid=meettingid;
          				}else {
          					scheduleid=list.get(0);
          				}
          			}
          			
          			List<Object[]> projapplicommitteelist=service.ProjectApplicableCommitteeList(projectid);
          		
          			List<Object[]> meetinglist=service.MeettingActionList(committeeid,projectid,scheduleid,EmpId);
          			
          			req.setAttribute("scheduleid", scheduleid);
          			req.setAttribute("projectid",projectid);
          			req.setAttribute("committeeid",committeeid);
          			req.setAttribute("Projectdetails",projectdetailslist.get(0));
          			req.setAttribute("ProjectsList",projectdetailslist);
          			req.setAttribute("meetingcount", meetingcount);
          			req.setAttribute("meetinglist", meetinglist);
          			req.setAttribute("projapplicommitteelist",projapplicommitteelist);
          		}			
          		catch (Exception e) 
          		{
          			e.printStackTrace(); 
          			logger.error(new Date() +"Inside MeettingAction.htm "+UserId,e);
          		}
 
                  return "action/MeetingAction"; 
               }

              @RequestMapping(value="getEmployees.htm",method=RequestMethod.GET)
      		public @ResponseBody String getEmployeesList(HttpServletRequest req,HttpSession ses) throws Exception {
      			String UserId=(String)ses.getAttribute("Username");
      			Gson json = new Gson();
      			List<Object[]>allEmployees=new ArrayList<>();
      			logger.info(new Date()+"Inside getEmployees.htm "+UserId);
      			try {
      				String flag=req.getParameter("flag");
      				allEmployees=service.getAllEmployees(flag); // to get All the employees either Gh,DH or PD
      			}
      			catch(Exception e) {
      				e.printStackTrace();
      			}
      			return json.toJson(allEmployees);
      		} 	     

          	@RequestMapping(value = "RfaTransStatus.htm" , method={RequestMethod.POST,RequestMethod.GET})
        	public String MobileNumberTransStatus(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception
        	{
        		String Username = (String) ses.getAttribute("Username");
        		logger.info(new Date() +"Inside RfaTransStatus.htm "+Username);
        		try {
        			String rfaTransId = req.getParameter("rfaTransId").trim();
        			req.setAttribute("RfaTransactionList", service.getRfaTransList(rfaTransId)) ;				
        			return "action/RfaTransactionStatus";
        		}catch (Exception e) {
        			e.printStackTrace();
        			logger.error(new Date() +" Inside RfaTransStatus.htm "+Username, e);
        			return "static/Error";
        		}
        	}
          	
        	@RequestMapping(value = "ActionReassign.htm" , method={RequestMethod.POST,RequestMethod.GET})
        	public String ActionReassign(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception
        	{
    			String LabCode = (String) ses.getAttribute("labcode");
				String UserId =(String)ses.getAttribute("Username");
        		logger.info(new Date() +"Inside ActionReassign.htm "+UserId);
        		try {
					String ActionAssignId= req.getParameter("ActionAssignId");
					String ActionMainId= req.getParameter("ActionMainId");
					
					String modelAssigneeLabCode=req.getParameter("modelAssigneeLabCode");
					String Assignee=req.getParameter("Assignee");
					ActionAssign assign=new ActionAssign();
					assign.setAssigneeLabCode(modelAssigneeLabCode);
					assign.setAssignee(Long.parseLong(Assignee));
					assign.setActionAssignId(Long.parseLong(ActionAssignId));
        			int count = service.ActionAssignEdit(assign);
        			if(count>0) {
        				redir.addAttribute("result","Action reassigned successfully");
        			}else {
        				redir.addAttribute("result","Action reassigned unsuccessful");
        			}
        			redir.addAttribute("ActionMainId",ActionMainId);
        			redir.addAttribute("ActionAssignId",ActionAssignId);
        			redir.addAttribute("ActionPath",req.getParameter("ActionPath"));
        			return "redirect:/CloseAction.htm";
        			
				} catch (Exception e) {
					e.printStackTrace();
				}
        		return null;
        	}
        	

        	
        	
          	//worked
        	 @PostMapping(value="ActionProgressAjaxSubmit.htm")
      		public @ResponseBody String ActionProgressAjaxSubmit(HttpServletRequest req,
      				HttpSession ses,  
      				@RequestParam(name = "file", required = false) MultipartFile file,
      				@RequestParam("progressDate") String progressDate
      				,@RequestParam("Progress") String Progress
      				,@RequestParam("remarks") String remarks,
      				@RequestParam("ActionAssignId") String ActionAssignId,
      				RedirectAttributes redir
      				) throws Exception {
          	String UserId  = (String) ses.getAttribute("Username");
      		String labCode = (String)ses.getAttribute("labcode");
      			Gson json = new Gson();
      			List<Object[]>allEmployees=new ArrayList<>();
      			logger.info(new Date()+"Inside ActionProgressAjaxSubmit.htm "+UserId);
      			try {
      				
      				if(InputValidator.isContainsHTMLTags(remarks)) {
      			 
      					return "error:Remarks should not contain HTML Tags!";
      				}
      				
      				
      				 // 🔹 Validate file type if present
      		        if (file != null && !file.isEmpty() && !isValidFileType(file)) {
      		            return "error:Invalid file type. Only PDF or Image files are allowed!";
      		        }
      				
      				
      				
      				
      				ActionSubDto subDto=new ActionSubDto();
      				subDto.setLabCode(labCode);
      				subDto.setFileName(req.getParameter("FileName"));
      				if(file!=null) {
      				subDto.setFileNamePath(file.getOriginalFilename());
          			subDto.setMultipartfile(file);
          			}
      	            subDto.setCreatedBy(UserId);
      	            subDto.setActionAssignId(req.getParameter("ActionAssignId"));
      	            subDto.setRemarks(remarks);
      	            subDto.setProgress(Progress);
      	            subDto.setProgressDate(progressDate);
      	        	Long count=service.ActionSubInsert(subDto);
      	        	System.out.println(count);
      				}
      			catch(Exception e) {
      				e.printStackTrace();
      			}
      				return "success:Action submitted successfully";
      		}

            
            
        	@RequestMapping(value = "ActionRemarksEdit.htm" , method={RequestMethod.POST,RequestMethod.GET})
        	public String ActionRemarksEdit(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception
        	{
				String UserId =(String)ses.getAttribute("Username");
				String progress=req.getParameter("progressVal");
				String progressRemarks=req.getParameter("progressRemarks");
				String ActionAssignId=req.getParameter("ActionAssignId");
				String ActionSubId=req.getParameter("ActionSubId");
				
        		logger.info(new Date() +"Inside ActionRemarksEdit.htm "+UserId);
        		try {
				
        			int count=service.actionSubRemarksEdit(ActionSubId,progress,progressRemarks,UserId);
        	    	if(req.getParameter("subaction").equalsIgnoreCase("1")) {
        			int result = service.ActionRemarksEdit(ActionAssignId,progress,progressRemarks,UserId);
        	    	}
        			if(count>0) {
        				redir.addAttribute("result","Action Remarks Updated successfully");
        			}else {
        				redir.addAttribute("result","Action Remarks Update unsuccessful");
        			}

        			redir.addFlashAttribute("ActionMainId", req.getParameter("ActionMainId"));
        			redir.addFlashAttribute("ActionAssignId",req.getParameter("ActionAssignId"));
        			redir.addFlashAttribute("projectid",req.getParameter("projectid"));
        			redir.addFlashAttribute("flag",req.getParameter("flag"));
        			return "redirect:/ActionSubLaunchRedirect.htm";
        			
				} catch (Exception e) {
					e.printStackTrace();
				}
        		return null;
        	}
        	
       
        	  @RequestMapping(value = "RfaActionReports.htm", method = {RequestMethod.GET, RequestMethod.POST})
              public String RfaActionReports(HttpServletRequest req,HttpSession ses, RedirectAttributes redir) throws Exception 
              {
              	String UserId = (String) ses.getAttribute("Username");
      			String LabCode = (String) ses.getAttribute("labcode");
      			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
      			String LoginType=(String)ses.getAttribute("LoginType");
      			
          		logger.info(new Date() +"Inside RfaActionReports.htm "+UserId);
          		try {
          			String rfaid=req.getParameter("rfaid");
          			String projectid= req.getParameter("projectid");
          			String projectType= req.getParameter("projectType");
          			String rfatypeid=req.getParameter("rfatypeid");
          			String rfatype = req.getParameter("rfatype");
          			String employeeId = req.getParameter("employeeId");
          			String rfaStatus = req.getParameter("rfaStatus");
          			String fdate = req.getParameter("fdate");
          			String tdate = req.getParameter("tdate");
          			rfatype = rfatype!=null ? rfatype : "I";
          			FormatConverter fc=new FormatConverter();
      				SimpleDateFormat sdf=fc.getRegularDateFormat();
      				SimpleDateFormat sdf1=fc.getSqlDateFormat();
          			
          			List<Object[]> ProjectList = service.LoginProjectDetailsList(EmpId, LoginType, LabCode);
          			List<Object[]> preProjectList = reqservice.getPreProjectList(LoginType, LabCode, EmpId);
          			List<Object[]> RfaNoTypeList = service.getRfaNoTypeList();
          			
          			if(projectType==null && projectid==null) {
          				projectType="P";
      					projectid=ProjectList.get(0)[0].toString();
      				}
          			if(projectType!=null && "I".equalsIgnoreCase(projectType)){
      					projectid=projectid!=null ? projectid : preProjectList.get(0)[0].toString();
      				}
          			if(projectType!=null && "P".equalsIgnoreCase(projectType)){
      					projectid= projectid!=null ? projectid : ProjectList.get(0)[0].toString();
          	        }
          			
      				if(rfatypeid==null) {
      					rfatypeid = "-"; 
      				}
      				 LocalDate currentDate = LocalDate.now();
      				if(fdate==null)
      				{		
  				        LocalDate firstDayOfYear = LocalDate.of(currentDate.getYear(), 1, 1);
  				        fdate = firstDayOfYear.toString();
      				}else
      				{		
      					fdate=sdf1.format(sdf.parse(fdate)); 
      				}		
      				if(tdate==null)
      				{
      					LocalDate lastDayOfYear = LocalDate.of(currentDate.getYear(), 12, 31);
      					tdate=lastDayOfYear.toString();
      				}
      				else 
      				{	
      					tdate=sdf1.format(sdf.parse(tdate));				
      				}

      				List<Object[]>RfaActionList = new ArrayList<Object[]>();
      				if(LabCode.equalsIgnoreCase("ADE")) {
      					RfaActionList = service.getRfaActionsOnRFAtype(projectType,projectid,rfatypeid,rfatype,employeeId,fdate,tdate,rfaStatus);
      				}else {
      					RfaActionList = service.rfaTotalActionList(projectType, projectid, rfatypeid, fdate, tdate);
      				}
      				
      				req.setAttribute("ProjectList", ProjectList);		
      				req.setAttribute("preProjectList", preProjectList);		
      				req.setAttribute("RfaNoTypeList", RfaNoTypeList);
      				req.setAttribute("rfaActionList", RfaActionList);
      				req.setAttribute("AssigneeEmplList", service.AssigneeEmpList());
      				req.setAttribute("projectid", projectid);
      				req.setAttribute("projectType", projectType);
      				req.setAttribute("rfatypeid", rfatypeid);
      				req.setAttribute("employeeList", service.EmployeeList(LabCode));
      				req.setAttribute("employeeId", employeeId);
      				req.setAttribute("rfaStatus", rfaStatus);
      				req.setAttribute("rfatype", rfatype);
      				req.setAttribute("fdate", fdate);
      				req.setAttribute("tdate", tdate);
          		}
          		catch (Exception e) 
          		{
          			e.printStackTrace(); 
          			logger.error(new Date() +"Inside RfaActionReports.htm "+UserId,e);
          		}

                  return "action/RfaActionReports"; 
               }

            
            @RequestMapping(value = "CommitteActionEdit.htm" , method={RequestMethod.POST,RequestMethod.GET})
        	public String CommitteActionEdit(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception
        	{
            	String UserId = (String) ses.getAttribute("Username");
        		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
        		String LabCode = (String) ses.getAttribute("labcode");
        		logger.info(new Date() +"Inside CommitteActionEdit.htm "+UserId);
        		try {
        			
        			FormatConverter fc=new FormatConverter();
					SimpleDateFormat sdf=fc.getRegularDateFormat();
					SimpleDateFormat sdf1=fc.getSqlDateFormat();
					
					String PDCDate=req.getParameter("PDCDate");
					String AssigneeId=req.getParameter("AssigneeId");
					String ActionAssignId=req.getParameter("ActionAssignId");
					String CommitteeScheduleId=req.getParameter("CommitteeScheduleId");
					String AssigneeLab=req.getParameter("AssigneeLab");
        			
        			ActionAssignDto actionAssign = new ActionAssignDto();
        			actionAssign.setActionAssignId(Long.parseLong(ActionAssignId));
        			actionAssign.setAssignee(Long.parseLong(AssigneeId));
        			actionAssign.setAssignorLabCode(LabCode);
        			actionAssign.setAssigneeLabCode(AssigneeLab);
        			actionAssign.setAssignor(Long.parseLong(EmpId));
        			actionAssign.setPDCOrg(sdf1.format(sdf.parse(PDCDate)));
        			actionAssign.setEndDate(sdf1.format(sdf.parse(PDCDate)));
        			actionAssign.setModifiedBy(UserId);
				
        			int count = service.CommitteActionEdit(actionAssign);
        			if(count>0) {
        				redir.addAttribute("result","Action Updated successfully");
        			}else {
        				redir.addAttribute("result","Action Update unsuccessful");
        			}

        			redir.addFlashAttribute("ScheduleId", CommitteeScheduleId);
        			redir.addFlashAttribute("minutesback", req.getParameter("minutesback"));
        			redir.addFlashAttribute("specname", req.getParameter("specnamevalue"));
        			redir.addAttribute("nonproject", req.getParameter("nonproject"));
        			
        			//rod Handling
        			String rodflag = req.getParameter("rodflag");
        			if(rodflag!=null && rodflag.equalsIgnoreCase("Y")) {
        				redir.addFlashAttribute("committeescheduledata",service.CommitteeActionList(CommitteeScheduleId));
        				redir.addFlashAttribute("committeescheduleeditdata", rodservice.RODScheduleEditData(CommitteeScheduleId));
        				redir.addFlashAttribute("AllLabList", service.AllLabList());
        				redir.addFlashAttribute("labcode", LabCode); 
        				redir.addFlashAttribute("EmpNameList", service.EmployeeList(LabCode));
        				redir.addFlashAttribute("rodflag", "Y");
        			}
        			// CCM Handling
        			String ccmFlag = req.getParameter("ccmFlag");
        			if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {
        				redir.addAttribute("ccmScheduleId", CommitteeScheduleId);
        				redir.addAttribute("committeeMainId", req.getParameter("committeeMainId"));
        				redir.addAttribute("committeeId", req.getParameter("committeeId"));
        				redir.addAttribute("ccmFlag", ccmFlag);
        			}
        			
        			// DMC Handling
        			String dmcFlag = req.getParameter("dmcFlag");
        			if(dmcFlag!=null && dmcFlag.equalsIgnoreCase("Y")) {
        				redir.addAttribute("committeeId", req.getParameter("committeeId"));
        				redir.addAttribute("dmcFlag", dmcFlag);
        			}
        			
        			return "redirect:/CommitteeAction.htm";
        			
				} catch (Exception e) {
					e.printStackTrace();
				}
        		return null;
        	}
            
            
            @RequestMapping(value = "RfaActionReportPdf.htm" , method={RequestMethod.POST})
        	public void RfaActionReportPdf(Model model,HttpServletRequest req,HttpServletResponse res ,HttpSession ses, RedirectAttributes redir)throws Exception
        	{
            	String UserId = (String) ses.getAttribute("Username");
        		String LabCode = (String) ses.getAttribute("labcode");
        		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
    			String LoginType=(String)ses.getAttribute("LoginType");
        		logger.info(new Date() +"Inside RfaActionReportPdf.htm "+UserId);
        		try {
        			
        			FormatConverter fc=new FormatConverter();
					SimpleDateFormat sdf=fc.getRegularDateFormat();
					SimpleDateFormat sdf1=fc.getSqlDateFormat();
        			
        			String projectid= req.getParameter("projectid");
        			String projectType= req.getParameter("projectType");
        			String rfatypeid=req.getParameter("rfatypeid");
        			String fdate = req.getParameter("reportFromdate");
        			String tdate = req.getParameter("reportTodate");
          			String rfatype = req.getParameter("rfatype");
          			String rfaStatus = req.getParameter("rfaStatus");
          			String employeeId = req.getParameter("employeeId");
          			rfatype = rfatype!=null ? rfatype : "I";
        			
        			List<Object[]> ProjectList = service.LoginProjectDetailsList(EmpId, LoginType, LabCode);
        			List<Object[]> preProjectList = reqservice.getPreProjectList(LoginType, LabCode, EmpId);
        			
        			List<Object[]> RfaActionReportList = new ArrayList<Object[]>();
      				if(LabCode.equalsIgnoreCase("ADE")) {
      					RfaActionReportList = service.getRfaActionsOnRFAtype(projectType,projectid,rfatypeid,rfatype,employeeId,fdate,tdate,rfaStatus);
      				}else {
      					RfaActionReportList = service.rfaTotalActionList(projectType, projectid, rfatypeid, fdate, tdate);
      				}
        			
        			req.setAttribute("RfaActionReportList", RfaActionReportList);
        			req.setAttribute("AssigneeEmplList", service.AssigneeEmpList());
        			req.setAttribute("ProjectList", ProjectList);
        			req.setAttribute("preProjectList", preProjectList);
        			req.setAttribute("projectType", projectType);
        			req.setAttribute("projectid", projectid);
    				req.setAttribute("rfatypeid", rfatypeid);
      				req.setAttribute("employeeId", employeeId);
      				req.setAttribute("rfatype", rfatype);
    				req.setAttribute("fdate", fdate);
    				req.setAttribute("tdate", tdate);
    				req.setAttribute("rfaStatus", rfaStatus);
    				req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(LabCode));
    				
        			
    			String filename="RFA Report From "+sdf.format(sdf1.parse(fdate))+" To "+sdf.format(sdf1.parse(tdate))+"";
    			String path=req.getServletContext().getRealPath("/view/temp");
    		  	req.setAttribute("path",path);
    			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
    			req.getRequestDispatcher("/view/action/RfaActionReportPdf.jsp").forward(req, customResponse);
    			String html = customResponse.getOutput();
		        HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf")) ; 
		        res.setContentType("application/pdf");
		        res.setHeader("Content-disposition","inline;filename="+filename+".pdf");
		        File f=new File(path +File.separator+ filename+".pdf");
		        FileInputStream fis = new FileInputStream(f);
		        DataOutputStream os = new DataOutputStream(res.getOutputStream());
		        res.setHeader("Content-Length",String.valueOf(f.length()));
		        byte[] buffer = new byte[1024];
		        int len = 0;
		        while ((len = fis.read(buffer)) >= 0) {
		            os.write(buffer, 0, len);
		        } 
		        os.close();
		        fis.close();
		        Path pathOfFile= Paths.get( path+File.separator+filename+".pdf"); 
		        Files.delete(pathOfFile);
    			
     		
    	 	}catch (Exception e) {
    			e.printStackTrace();
    			logger.error(new Date() +" Inside RfaActionReportPdf.htm "+UserId, e);
    		}
        		
        	}
            
            @RequestMapping(value = "OldRfaUpload.htm" , method={RequestMethod.POST,RequestMethod.GET})
        	public String OldRfaUpload(Model model,HttpServletRequest req,HttpServletResponse res ,HttpSession ses
        		)throws Exception
        	{
            	String UserId = (String) ses.getAttribute("Username");
        		String LabCode = (String) ses.getAttribute("labcode");
         		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
    			String LoginType=(String)ses.getAttribute("LoginType");
    			String projectId=(String)req.getParameter("projectId");
        		logger.info(new Date() +"Inside OldRfaUpload.htm "+UserId);
        		try {
        		List<Object[]> projectdetailslist=service.LoginProjectDetailsList(EmpId,LoginType,LabCode);
				 if(projectId==null) {
			        	try {
			        		Object[] pro=projectdetailslist.get(0);
			        		projectId=pro[0].toString();
			        	}catch (Exception e) {
							e.printStackTrace();
						}
			        }
        	    List<Object[]> oldRfaUploadList = service.getoldRfaUploadList(LabCode,projectId);
        	    req.setAttribute("oldRfaUploadList", oldRfaUploadList);
        		req.setAttribute("ProjectList", projectdetailslist);
        		req.setAttribute("projectId", projectId);
     		
    	 	}catch (Exception e) {
    			e.printStackTrace();
    			logger.error(new Date() +" Inside OldRfaUpload.htm "+UserId, e);
    		}
        		return "action/OldRfaUpload";
        	}
            
            
            @RequestMapping(value = "OldRfaUploadSubmit.htm" , method={RequestMethod.POST,RequestMethod.GET})
        	public String OldRfaUploadSubmit(Model model,HttpServletRequest req,HttpServletResponse res ,HttpSession ses, RedirectAttributes redir,
        		    @RequestParam(name = "rfafile", required = false) MultipartFile rfafile,
         			@RequestParam(name = "closurefile", required = false) MultipartFile closurefile,
     				@RequestParam("oldrfano") String oldrfano,
     				@RequestParam("projectId") String projectId,
     				@RequestParam("projecCode") String projecCode,
     				@RequestParam("rfadate") String rfadate)throws Exception
        	{
            	String UserId = (String) ses.getAttribute("Username");
        		String LabCode = (String) ses.getAttribute("labcode");
        		logger.info(new Date() +"Inside OldRfaUploadSubmit.htm "+UserId);
        		try {
        			        			
        			OldRfaUploadDto rfadto = new OldRfaUploadDto();
        			rfadto.setLabCode(LabCode);
        			rfadto.setProjectId(Long.parseLong(projectId));
        			rfadto.setProjecCode(projecCode);
        			rfadto.setRfaNo(oldrfano);
        			rfadto.setRfaDate(rfadate);
        			rfadto.setRfaFile(rfafile);
        			rfadto.setClosureFile(closurefile);
        			rfadto.setCreatedBy(UserId);
        			
        			long count = service.oldRfaUploadSubmit(rfadto);
        			if(count>0) {
        				redir.addAttribute("result","RFA Added successfully");
        			}else {
        				redir.addAttribute("result","RFA Add unsuccessful");
        			}
        			
             	    redir.addAttribute("projectId", projectId);
        			
    	 	}catch (Exception e) {
    			e.printStackTrace();
    			logger.error(new Date() +" Inside OldRfaUploadSubmit.htm "+UserId, e);
    		}
        		return "redirect:/OldRfaUpload.htm";
        	}
            
            @RequestMapping(value = {"OldRfaFileDownload.htm"})
        	public void OldRfaFileDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res)throws Exception 
        	{
        		String UserId = (String) ses.getAttribute("Username");
        		String LabCode = (String) ses.getAttribute("labcode");
        		logger.info(new Date() +"Inside OldRfaFileDownload.htm "+UserId);
        		try
        		{
        			String oldrfano=req.getParameter("rfano");
        			String projectCode=req.getParameter("projectCode");
        			String rfafile=req.getParameter("file1");
        			String closurefile=req.getParameter("file2");
        			String rfaNo = oldrfano.replaceAll("/", "_");
        			res.setContentType("application/pdf");
        			File my_file=null;
        			if(rfafile!=null) {
        				Path filepath1 = Paths.get(uploadpath,LabCode,"OldRFAFiles",projectCode,rfaNo,rfafile);
        				my_file = filepath1.toFile();
        				res.setHeader("Content-Disposition", "inline; filename=\""+rfafile +"\"");
        			}
        			if(closurefile!=null) {
        				Path filepath2 = Paths.get(uploadpath,LabCode,"OldRFAFiles",projectCode,rfaNo,closurefile);
        				my_file = filepath2.toFile();
        				res.setHeader("Content-Disposition", "inline; filename=\""+closurefile+"\"");
        			}
        			OutputStream out = res.getOutputStream();
        			FileInputStream in = new FileInputStream(my_file);
        			byte[] buffer = new byte[4096];
        			int length;
        			while ((length = in.read(buffer)) > 0){
        				out.write(buffer, 0, length);
        			}
        			in.close();
        			out.flush();
        			out.close();
        		}catch (Exception e) {
        			e.printStackTrace(); 
        			logger.error(new Date() +"Inside OldRfaFileDownload.htm "+UserId,e);
        		}
        	}
            
            
            @RequestMapping(value = "OldRfaUploadEditSubmit.htm" , method={RequestMethod.POST,RequestMethod.GET})
        	public String OldRfaUploadEditSubmit(Model model,HttpServletRequest req,HttpServletResponse res ,HttpSession ses, RedirectAttributes redir,
        		    @RequestParam(name = "rfafile", required = false) MultipartFile rfafile,
         			@RequestParam(name = "closurefile", required = false) MultipartFile closurefile,
     				@RequestParam("oldrfano") String oldrfano,
     				@RequestParam("rfaUploadId") String rfaUploadId,
     				@RequestParam("projectId") String projectId,
     				@RequestParam("projecCode") String projecCode,
     				@RequestParam("rfadate") String rfadate)throws Exception
        	{
            	String UserId = (String) ses.getAttribute("Username");
        		String LabCode = (String) ses.getAttribute("labcode");
        		logger.info(new Date() +"Inside OldRfaUploadEditSubmit.htm "+UserId);
        		try {
        			
        			OldRfaUploadDto rfadto = new OldRfaUploadDto();
        			rfadto.setRfaFileUploadId(Long.parseLong(rfaUploadId));
        			rfadto.setLabCode(LabCode);
        			rfadto.setProjecCode(projecCode);
        			rfadto.setRfaNo(oldrfano);
        			rfadto.setRfaDate(rfadate);
        			rfadto.setRfaFile(rfafile);
        			rfadto.setClosureFile(closurefile);
        			rfadto.setModifiedBy(UserId);
        			        			
        			long count = service.oldRfaUploadEditSubmit(rfadto);
        			if(count>0) {
        				redir.addAttribute("result","RFA Edited successfully");
        			}else {
        				redir.addAttribute("result","RFA Edit unsuccessful");
        			}
        			
             	    redir.addAttribute("projectId", projectId);
        			
    	 	}catch (Exception e) {
    			e.printStackTrace();
    			logger.error(new Date() +" Inside OldRfaUploadEditSubmit.htm "+UserId, e);
    		}
        		return "redirect:/OldRfaUpload.htm";
        	}
            
            //MeetingDateWiseReport.htm
            @RequestMapping(value = "MeetingDateWiseReport.htm" , method={RequestMethod.POST,RequestMethod.GET})
        	public String MeetingDateWiseReport(Model model,HttpServletRequest req,HttpServletResponse res ,HttpSession ses, RedirectAttributes redir)throws Exception
        	{
            	String UserId = (String) ses.getAttribute("Username");
        		String LabCode = (String) ses.getAttribute("labcode");
        		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
    			String LoginType=(String)ses.getAttribute("LoginType");
        		logger.info(new Date() +"Inside MeetingDateWiseReport.htm "+UserId);
        		try {
        			List<Object[]> projectdetailslist=service.LoginProjectDetailsList(EmpId,LoginType,LabCode);
        			req.setAttribute("ProjectsList",projectdetailslist);

        			String projectid=req.getParameter("projectid");
        			String fromDate=req.getParameter("fromDate");
        			String toDate=req.getParameter("toDate");        			
        			
        			Calendar cal = new GregorianCalendar(); 
       			cal.add(Calendar.DAY_OF_MONTH, -30); 
       			Date prevdate = cal.getTime();
        			if(fromDate== null || toDate==null) 
        			{
        				fromDate = new SimpleDateFormat("yyyy-MM-dd").format(prevdate);
        				//fromDate=DateTimeFormatUtil.getFinancialYearStartDateSqlFormat();
        				toDate  = LocalDate.now().toString();
        			}else
        			{
        				fromDate=sdf3.format(sdf.parse(fromDate));
        				toDate=sdf3.format(sdf.parse(toDate));
        			}
        			
        			if(projectid==null || projectid.equals("null"))
          			{
          				projectid=projectdetailslist.get(0)[0].toString();
          			}
        			
        			//meeting list
        			List<Object[]>getMeetingList=service.getMeetingList(Long.parseLong(projectid),fromDate,toDate);
        			
        			req.setAttribute("getMeetingList", getMeetingList);
        			
        			
        			req.setAttribute("projectid", projectid);
        			req.setAttribute("fromDate", fromDate);
        			req.setAttribute("toDate", toDate);
        			
        			
        			return "action/MeetingDateWiseReport"; 
        		}catch (Exception e) {
					e.printStackTrace();
				}
        		return null;
        		
        	
        	}
            
            //MeetingActionDetails.htm
            @RequestMapping(value = "MeetingActionDetails.htm" , method={RequestMethod.POST,RequestMethod.GET})
        	public String MeetingActionDetails(Model model,HttpServletRequest req,HttpServletResponse res ,HttpSession ses, RedirectAttributes redir)throws Exception
        	{
            	String UserId = (String) ses.getAttribute("Username");
        		String LabCode = (String) ses.getAttribute("labcode");
        		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
    			String LoginType=(String)ses.getAttribute("LoginType");
        		logger.info(new Date() +"Inside MeetingDateWiseReport.htm "+UserId);
        		try {
        			//MeetingId
        			String MeetingId = (String) req.getParameter("MeetingId");
        			String MeetingNumbr = (String) req.getParameter("Meeting");
        			//list of action items 
        			List<Object[]>actionList=service.getMeetingAction(Long.parseLong(MeetingId),LoginType,EmpId);
        			req.setAttribute("actionList", actionList);
        			req.setAttribute("FromDate", req.getParameter("FromDate"));
        			req.setAttribute("ToDate", req.getParameter("ToDate"));
        			req.setAttribute("MeetingNumbr", MeetingNumbr);
        			req.setAttribute("text", "R");
        			
        			req.setAttribute("flag", "M");
        			
        			req.setAttribute("meettingid", MeetingId);
        			req.setAttribute("LoginType", LoginType);
        			req.setAttribute("EmpId", EmpId);
        			req.setAttribute("MeetingNumbr", MeetingNumbr);
        			
        			return "action/ActionMeeting"; 
        		}catch (Exception e) {
					e.printStackTrace();
					return null;
				}
        		}
            
            
            @RequestMapping(value = "CommitteActionDelete.htm" , method={RequestMethod.POST,RequestMethod.GET})
        	public String CommitteActionDelete(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception
        	{
            	String UserId = (String) ses.getAttribute("Username");
        		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
        		String LabCode = (String) ses.getAttribute("labcode");
        		logger.info(new Date() +"Inside CommitteActionDelete.htm "+UserId);
        		try {
					
					String ActionAssignId=req.getParameter("actionAssignPkId");
					String CommitteeScheduleId=req.getParameter("committeeSchId");
        			
        			ActionAssignDto actionAssign = new ActionAssignDto();
        			actionAssign.setActionAssignId(Long.parseLong(ActionAssignId));
    
				
        			int count = service.CommitteActionDelete(actionAssign);
        			if(count>0) {
        				redir.addAttribute("result","Action Deleted Successfully");
        			}else {
        				redir.addAttribute("result","Action Deleted Unsuccessful");
        			}

        			redir.addFlashAttribute("ScheduleId", CommitteeScheduleId);
        			redir.addFlashAttribute("minutesback", req.getParameter("minutesback"));
        			redir.addFlashAttribute("specname", req.getParameter("specValueId"));
        			redir.addAttribute("nonproject", req.getParameter("nonproject"));
        			
        			//rod Handling
        			String rodflag = req.getParameter("rodflag");
        			if(rodflag!=null && rodflag.equalsIgnoreCase("Y")) {
        				redir.addFlashAttribute("committeescheduledata",service.CommitteeActionList(CommitteeScheduleId));
        				redir.addFlashAttribute("committeescheduleeditdata", rodservice.RODScheduleEditData(CommitteeScheduleId));
        				redir.addFlashAttribute("AllLabList", service.AllLabList());
        				redir.addFlashAttribute("labcode", LabCode); 
        				redir.addFlashAttribute("EmpNameList", service.EmployeeList(LabCode));
        				redir.addFlashAttribute("rodflag", "Y");
        			}
        			// CCM Handling
//        			String ccmFlag = req.getParameter("ccmFlag");
//        			if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {
//        				redir.addAttribute("ccmScheduleId", CommitteeScheduleId);
//        				redir.addAttribute("committeeMainId", req.getParameter("committeeMainId"));
//        				redir.addAttribute("committeeId", req.getParameter("committeeId"));
//        				redir.addAttribute("ccmFlag", ccmFlag);
//        			}
        			
        			// DMC Handling
//        			String dmcFlag = req.getParameter("dmcFlag");
//        			if(dmcFlag!=null && dmcFlag.equalsIgnoreCase("Y")) {
//        				redir.addAttribute("committeeId", req.getParameter("committeeId"));
//        				redir.addAttribute("dmcFlag", dmcFlag);
//        			}
        			
        			return "redirect:/CommitteeAction.htm";
        			
				} catch (Exception e) {
					e.printStackTrace();
				}
        		return null;
        	}
            
        	@RequestMapping(value = "RfaActionClose.htm", method = {RequestMethod.GET, RequestMethod.POST})
    	 	public String RfaActionClose( HttpServletRequest req ,HttpSession ses , RedirectAttributes redir,
    	 			@RequestPart(name ="attachment", required = false) MultipartFile attachment)throws Exception
    	 	{
    	 		String UserId = (String) ses.getAttribute("Username");
    			String LabCode = (String) ses.getAttribute("labcode");
    			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
    	 		logger.info(new Date() +"Inside RfaActionClose.htm "+UserId);
    			try {
    				String rfaid=req.getParameter("RFAID");
    				String status=req.getParameter("rfaoptionby");
    				RfaActionDto rfa = new RfaActionDto();
    				rfa.setMultipartfile(attachment);
    				rfa.setRfaId(Long.parseLong(rfaid));
    				rfa.setRfastatus(status);
    				rfa.setModifiedBy(UserId);
    				rfa.setActionBy(Long.parseLong(EmpId));
    				rfa.setLabCode(LabCode);
    				rfa.setRfaNo(req.getParameter("rfano"));
    				rfa.setProjectCode(req.getParameter("projectCode"));
    				
    				rfa.setReference(req.getParameter("remarks"));
    				Long count = service.rfaCloseForExternal(rfa);
    				if (count> 0) {
    					if(rfa.getRfastatus().equalsIgnoreCase("ARC")) {
    					redir.addAttribute("result", "RFA closed Successfully");
    					}else {
        				redir.addAttribute("result", "RFA returned for Rechecking");
    					}
    				} else {
    					redir.addAttribute("resultfail", "RFA close Unsuccessfull");
    				}
    			    }catch (Exception e) {
    					e.printStackTrace();
    					logger.error(new Date() +" Inside RfaActionSubmit.htm "+UserId, e);
    				}
    	 		return "redirect:/RfaAction.htm";
    	 		
    	 	}  
            
	@RequestMapping(value="ActionReport.htm", method= {RequestMethod.POST, RequestMethod.GET})    
	public String actionReport(HttpServletRequest req, HttpSession ses) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		String Logintype = (String)ses.getAttribute("LoginType");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
 		logger.info(new Date() +" Inside ActionReport.htm "+UserId);
		try {
			String empId = req.getParameter("empId");
			empId = empId==null?"A":empId;
			String projectId = req.getParameter("projectId");
			projectId = projectId==null?"A":projectId;
			String type = req.getParameter("type");
			type = type==null?"A":type;
			String status = req.getParameter("status");
			status = status==null?"I":status;
			String labCode = req.getParameter("labCode");
			labCode = labCode==null?LabCode:labCode;
			
			List<Object[]> projectList = service.LoginProjectDetailsList(EmpId, Logintype, LabCode);
			List<Object[]> roleWiseEmployeeList = timesheetservice.getRoleWiseEmployeeList(labCode, Logintype, EmpId);
			List<Object[]> allActionsList = service.ActionReports((empId!=null && empId.equalsIgnoreCase("E")?"A":empId), status, projectId , type, labCode);
			
			//List<Object[]> allActionsListFiltered = new ArrayList<Object[]>();
					
			Set<Object> empIds = roleWiseEmployeeList.stream().map(e -> e[0]).collect(Collectors.toSet());
			
			if(empId.equalsIgnoreCase("A") || empId.equalsIgnoreCase("E")) {
				if(empId.equalsIgnoreCase("A")) {
					allActionsList = allActionsList.stream().filter(e -> empIds.contains(e[16]) && !e[15].toString().equalsIgnoreCase("@EXP")).collect(Collectors.toList());
				}else {
					allActionsList = allActionsList.stream().filter(e -> e[15].toString().equalsIgnoreCase("@EXP")).collect(Collectors.toList());
				}
				
			}
			
			req.setAttribute("empId", empId);
			req.setAttribute("projectId", projectId);
			req.setAttribute("type", type);
			req.setAttribute("status", status);
			req.setAttribute("labCode", labCode);
			req.setAttribute("allLabList", committeservice.AllLabList());
			req.setAttribute("projectList", projectList);
			req.setAttribute("roleWiseEmployeeList", roleWiseEmployeeList);
			req.setAttribute("allActionsList", allActionsList);
			
			return "action/ActionReportNew";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside ActionReport.htm "+UserId, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="ActionProgressSubList.htm", method=RequestMethod.GET)
	public @ResponseBody String duplicateShortCodeCheck(HttpSession ses, HttpServletRequest req) throws Exception {
		
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside ActionProgressSubList.htm "+UserId);
		Gson json = new Gson();
		List<Object[]> subList = new ArrayList<Object[]>();
		try
		{	  
			subList = service.SubList(req.getParameter("actionAssignId"));
	          
		}catch (Exception e) {
			logger.error(new Date() +" Inside ActionProgressSubList.htm "+UserId ,e);
			e.printStackTrace(); 
		}
		  
		 return json.toJson(subList);    
	}
	@RequestMapping(value="MomAttachmentList.htm", method=RequestMethod.GET)
	public @ResponseBody String MomAttachmentList(HttpSession ses, HttpServletRequest req) throws Exception {
		
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside MomAttachmentList.htm "+UserId);
		Gson json = new Gson();
		List<Object[]> subList = new ArrayList<Object[]>();
		String CommitteeScheduleId = req.getParameter("CommitteeScheduleId");
		try
		{	  
			subList = committeservice.MinutesAttachmentList(CommitteeScheduleId);
			
		}catch (Exception e) {
			logger.error(new Date() +" Inside MomAttachmentList.htm "+UserId ,e);
			e.printStackTrace(); 
		}
		
		return json.toJson(subList);    
	}
	
	
	 @RequestMapping(value = "ActionMainAttachDownload.htm", method = RequestMethod.GET)
	 public void ActionMainAttachDownload(HttpServletRequest	 req, HttpSession ses, HttpServletResponse res) throws Exception 
	 {	 
		 String UserId = (String) ses.getAttribute("Username");
		 String labCode = (String)ses.getAttribute("labcode");
			logger.info(new Date() +"Inside ActionMainAttachDownload "+UserId);		
			try { 
		 
					Object[]getActionMainAttachMent=service.getActionMainAttachMent(req.getParameter("MainId" ));
					Path pdfPath = Paths.get(uploadpath,labCode,"ActionMain Attachment",getActionMainAttachMent[1].toString());
					File my_file=null;
//					my_file = new File(uploadpath+attach.getAttachFilePath()+File.separator+attach.getAttachName()); 
					my_file = pdfPath.toFile(); 
					res.setContentType("Application/octet-stream");	
					res.setHeader("Content-disposition","attachment; filename="+getActionMainAttachMent[1].toString()); 
			        OutputStream out = res.getOutputStream();
			        FileInputStream in = new FileInputStream(my_file);
			        byte[] buffer = new byte[4096];
			        int length;
			        while ((length = in.read(buffer)) > 0){
			           out.write(buffer, 0, length);
			        }
			        in.close();
			        out.flush();
			        out.close();

			}
			catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside ActionMainAttachDownload.htm "+UserId, e);
			}
	 }
	 
	 
	   @RequestMapping(value="momcopyadd.htm", method= {RequestMethod.POST, RequestMethod.GET})
		public @ResponseBody String MoMSignedCopySubmit(HttpServletRequest req,
				HttpSession ses, 
				@RequestParam(name = "file", required = false) MultipartFile file,
				@RequestParam("ScheduleId") String ScheduleId
			
				) throws Exception {
		   String UserId  = (String) ses.getAttribute("Username");
		   String labCode = (String)ses.getAttribute("labcode");
			Gson json = new Gson();
			Long count =0l; 
			try {
				CommitteeMinutesAttachmentDto dto= new CommitteeMinutesAttachmentDto();
				dto.setScheduleId(req.getParameter("ScheduleId"));
				dto.setMinutesAttachment(file);
				dto.setAttachmentName(file.getOriginalFilename());
				dto.setCreatedBy(UserId);
				dto.setMinutesAttachmentId(req.getParameter("attachmentid"));
				dto.setLabCode(labCode);
				count = committeservice.MinutesAttachmentAdd(dto);
				
				
			}catch (Exception e) {
				// TODO: handle exception
			}
			
			return json.toJson(count);
	   }

	   
	   @RequestMapping(value = "ActionReview.htm" , method={RequestMethod.POST,RequestMethod.GET})
   	public String ActionReview(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception
  	{
  		String Logintype= (String)ses.getAttribute("LoginType");
  		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
  		String labCode = (String)ses.getAttribute("labcode");
  		try {
  		List<Object[]>TotalActions= service.TotalActions();// get All the actions from List
  		List<Object[]>FilteredTotalActions= new ArrayList<>();
  		List<Object[]>projects= new ArrayList<>();
    		String assignorid=req.getParameter("assignorid")!=null ?req.getParameter("assignorid"):"0";
  		
  		if(Logintype.equalsIgnoreCase("A") || Logintype.equalsIgnoreCase("Z")) {
  			
  			if(assignorid.equalsIgnoreCase("0")) {
  			FilteredTotalActions = TotalActions;
  			}else {
  				FilteredTotalActions = TotalActions.stream().filter(e->e[15].toString().equalsIgnoreCase(assignorid)).collect(Collectors.toList());
  			}
  			projects=service.ProjectList(); // get All the projects as login is admin or director
  		}
  		
  		if(Logintype.equalsIgnoreCase("P")) {
  			projects=service.getProjects(EmpId);// get All projects of that project director
  			List<String>Projectids=new ArrayList<>();
  			if(!projects.isEmpty()) {
  				Projectids=projects.stream().map(i->i[0].toString()).collect(Collectors.toList());
  			}
  			List<String>temp=Projectids;
  			if(!temp.isEmpty()) {
  				FilteredTotalActions=TotalActions.stream().filter(i->temp.contains(i[14].toString())).collect(Collectors.toList());
  			}
  		}
  		
  		List<Object[]>	EmployeeList = service.LabEmployeeList(labCode);
  		
  		
  		
  		String projectid=req.getParameter("projectid");
  		req.setAttribute("projects", projects);
  		req.setAttribute("assignorid", assignorid);
  		req.setAttribute("EmployeeList", EmployeeList);
  		
  		if(projectid!=null && !projectid.equalsIgnoreCase("A")) {
  		FilteredTotalActions=FilteredTotalActions.stream().filter(i->i[14].toString().equalsIgnoreCase(projectid)).collect(Collectors.toList());//filtered the action items based on project
  		
  		req.setAttribute("projectid", projectid);
  		}else {
  		req.setAttribute("projectid", "A");
  		}
  		

  		req.setAttribute("ForwardList", FilteredTotalActions);
  		}
  		catch (Exception e) {
			e.printStackTrace();
			}
  		
  		return "action/ActionReview";
  	}
	   
	   	@RequestMapping(value = "ChangeAssignor.htm" , method={RequestMethod.POST,RequestMethod.GET})
		public String ChangeAssignor(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception
		{
			String LabCode = (String) ses.getAttribute("labcode");
			String UserId =(String)ses.getAttribute("Username");
			logger.info(new Date() +"Inside ChangeAssignor.htm "+UserId);
			try {
		
					int count=0;
					
					String projectid=req.getParameter("projectid");
					String AssignorId=req.getParameter("AssignorId");
					
					String assignId = req.getParameter("assignId");
					
					String []assignIds = assignId.split(",");
					
					
					for(int i=0;i<assignIds.length;i++) {
					ActionAssign assign=new ActionAssign();
					assign.setAssignor(Long.parseLong(AssignorId));
					assign.setActionAssignId(Long.parseLong(assignIds[i]));
					count = count+service.ActionAssignerEdit(assign);
					}
					
					
					if(count>0) {
	    				redir.addAttribute("result","Action assignor changed successfully");
	    			}else {
	    				redir.addAttribute("result","Action assignor change unsuccessful");
	    			}
				
				redir.addAttribute("projectid",projectid);
				redir.addAttribute("assignorid",AssignorId);
				redir.addAttribute("ActionPath",req.getParameter("ActionPath"));
				return "redirect:/ActionReview.htm";
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	
		private String redirectWithError(RedirectAttributes redir,String redirURL, String message) {
		    redir.addAttribute("resultfail", message);
		    return "redirect:/"+redirURL;
		}
		
		 @RequestMapping(value="getDivisionEmployees.htm",method=RequestMethod.GET)
   		public @ResponseBody String getDivisionEmployeesList(HttpServletRequest req,HttpSession ses) throws Exception {
   			String UserId=(String)ses.getAttribute("Username");
   			Gson json = new Gson();
   			List<Object[]> divisionEmployees=new ArrayList<>();
   			logger.info(new Date()+"Inside getDivisionEmployees.htm "+UserId);
   			try {
   				String divisionId=req.getParameter("divisionId");
   				divisionEmployees=service.getDivisionEmployeesList(divisionId); 
   			}
   			catch(Exception e) {
   				e.printStackTrace();
   			}
   			return json.toJson(divisionEmployees);
   		}
}
