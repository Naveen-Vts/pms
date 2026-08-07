package com.vts.pfms.committee.controller;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGrid;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGridCol;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.google.gson.Gson;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.CompressionConstants;
//import com.itextpdf.layout.Document;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
//import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.PdfMerger;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.font.FontProvider;
import com.vts.pfms.CharArrayWriterResponse;
import com.vts.pfms.FormatConverter;
import com.vts.pfms.Zipper;
import com.vts.pfms.admin.service.AdminService;
import com.vts.pfms.cars.service.CARSService;
import com.vts.pfms.ccm.service.CCMService;
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
import com.vts.pfms.committee.model.CommitteeDefaultAgenda;
import com.vts.pfms.committee.model.CommitteeDivision;
import com.vts.pfms.committee.model.CommitteeInitiation;
import com.vts.pfms.committee.model.CommitteeLetter;
import com.vts.pfms.committee.model.CommitteeMeetingDPFMFrozen;
import com.vts.pfms.committee.model.CommitteeMinutesAttachment;
import com.vts.pfms.committee.model.CommitteeMomAttachment;
import com.vts.pfms.committee.model.CommitteeProject;
import com.vts.pfms.committee.model.CommitteeRepresentative;
import com.vts.pfms.committee.model.CommitteeScheduleAgendaDocs;
import com.vts.pfms.committee.model.CommitteeSchedulesMomDraftRemarks;
import com.vts.pfms.committee.model.CommitteeSubSystem;
import com.vts.pfms.committee.model.PmsEnote;
import com.vts.pfms.committee.model.ProgrammeMaster;
import com.vts.pfms.committee.service.CommitteeService;
import com.vts.pfms.committee.service.RODService;
import com.vts.pfms.mail.CustomJavaMailSender;
import com.vts.pfms.mail.MailConfigurationDto;
import com.vts.pfms.mail.MailService;
import com.vts.pfms.master.dto.ProjectFinancialDetails;
import com.vts.pfms.master.model.Employee;
import com.vts.pfms.master.service.MasterService;
import com.vts.pfms.milestone.model.FileRepUploadPreProject;
import com.vts.pfms.model.TotalDemand;
import com.vts.pfms.print.controller.PrintController;
import com.vts.pfms.print.service.PrintService;
import com.vts.pfms.project.service.ProjectService;
import com.vts.pfms.utils.InputValidator;
import com.vts.pfms.utils.PMSFileUtils;
import com.vts.pfms.utils.PMSLogoUtil;

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

@Controller
public class CommitteeController {

	@Autowired CommitteeService service;

	@Autowired
	MailService mailService;

	@Autowired
	AdminService adminservice;
	
	@Autowired
	@Lazy
	CustomJavaMailSender cm;

//	@Value("${port}")
//	private String port;

//	@Autowired 
//	BCryptPasswordEncoder encoder;

	@Autowired
	PrintService printservice;
	
	
	private static final RestTemplate restTemplate = new RestTemplate();

	@Value("${server_uri}")
	private String uri;

	@Autowired
	Environment env;

	@Autowired
	RODService rodservice;

	// Prudhvi - 27/03/2024
	/* ------------------ start ----------------------- */
	@Autowired
	MasterService masterservice;
	/* ------------------ end ----------------------- */

	@Value("${ApplicationFilesDrive}")
	private String ApplicationFilesDrive;

	@Value("${ApplicationFilesDrive}")
	String uploadpath;

	@Value("${File_Size}")
	String file_size;

	@Autowired
	PrintController pt;

	@Autowired
	PMSFileUtils PMSFileUtils ;

	@Autowired
	PMSLogoUtil LogoUtil;

	@Autowired
	@Lazy
	CCMService ccmservice;

	@Autowired
	CARSService carsservice;
	
	@Autowired	
	ProjectService projectService;
	
	SimpleDateFormat inputFormat = new SimpleDateFormat("ddMMMyyyy",Locale.ENGLISH);
	SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
	FormatConverter fc=new FormatConverter(); 
	SimpleDateFormat sdf3=fc.getRegularDateFormat();
	SimpleDateFormat sdf=fc.getRegularDateFormatshort();
	SimpleDateFormat sdf1=fc.getSqlDateFormat(); int addcount=0; 
	Format  format = com.ibm.icu.text.NumberFormat.getCurrencyInstance(new Locale("en", "in"));
	private  SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat rdf=new SimpleDateFormat("dd-MM-yyyy");
	private static final Logger logger=LogManager.getLogger(CommitteeController.class);
	private SimpleDateFormat sdtf = fc.getSqlDateAndTimeFormat();
	
	@RequestMapping(value = "CommitteeAdd.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String CommitteeAddPage(HttpServletRequest req, HttpSession ses) throws Exception
	{	
		String UserId = (String) ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeAdd.htm "+UserId);
		String projectid=req.getParameter("projectid");
		String projectappliacble=req.getParameter("projectappliacble");

		if(Long.parseLong(projectid)>0)
		{
			req.setAttribute("projectdetails",service.projectdetails(projectid));
		}		
		req.setAttribute("projectid",projectid);
		req.setAttribute("projectappliacble",projectappliacble);
		req.setAttribute("projectslist",service.ProjectList(LabCode));		
		return "committee/CommitteeAdd";
	}




	@RequestMapping(value = "CommitteeAddSubmit.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String CommitteeAddSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeAddSubmit.htm "+UserId);		
		try {		
			String projectid=req.getParameter("projectid").trim();
			
			String committeeShortName=req.getParameter("committeeshortname").trim();
			String committeeName=req.getParameter("committeename").trim();
			String guideLines=req.getParameter("guidelines");
			
			if(!InputValidator.isValidCapitalsAndSmallsAndNumeric(committeeShortName)) {
				redir.addAttribute("projectid", projectid);
				redir.addAttribute("projectappliacble",req.getParameter("projectapplicable"));
				return redirectWithError(redir, "CommitteeAdd.htm", "'Committee Code' must contain Alphabets and Numbers.!");
			}
			if(!InputValidator.isValidCapitalsAndSmallsAndNumericAndSpace(committeeName)) {
				redir.addAttribute("projectid", projectid);
				redir.addAttribute("projectappliacble",req.getParameter("projectapplicable"));
				return redirectWithError(redir, "CommitteeAdd.htm", "'committee Name' must contain Alphabets and Numbers.!");
			}
			
			if( InputValidator.isContainsHTMLTags(guideLines)) {
				redir.addAttribute("projectid", projectid);
				redir.addAttribute("projectappliacble",req.getParameter("projectapplicable"));
				return redirectWithError(redir, "CommitteeAdd.htm", "'Guidelines' should not contain HTML Tags.!");
			}
			
			
			CommitteeDto committeeDto=new CommitteeDto();

			committeeDto.setCommitteeName(committeeName);
			committeeDto.setCommitteeShortName(committeeShortName);
			committeeDto.setCreatedBy(UserId);
			committeeDto.setCommitteeType(req.getParameter("committeetype"));
			committeeDto.setProjectApplicable(req.getParameter("projectapplicable"));
			committeeDto.setTechNonTech(req.getParameter("technontech"));
			committeeDto.setPeriodicNon(req.getParameter("periodic"));
			committeeDto.setDescription(req.getParameter("description").trim());
			committeeDto.setTermsOfReference(req.getParameter("TOR").trim());
			committeeDto.setIsGlobal(projectid);
			committeeDto.setReferenceNo(req.getParameter("refno"));
			committeeDto.setIsBriefing(req.getParameter("IsBriefing"));
			
			if(req.getParameter("periodic").equalsIgnoreCase("P"))
			{
				String PeriodicDuration=req.getParameter("periodicduration");
				if(!InputValidator.isContainsNumberOnly(PeriodicDuration)) {
					redir.addAttribute("committeemainid", req.getParameter("committeeid"));
					redir.addAttribute("committeeid", req.getParameter("committeeid"));
					return redirectWithError(redir, "CommitteeAdd.htm", "'Periodic Duration' must contain Numbers.!");
				}
				committeeDto.setPeriodicDuration(PeriodicDuration);
			}else
			{
				committeeDto.setPeriodicDuration("0");
			}

			committeeDto.setGuidelines(guideLines);
			committeeDto.setLabCode(LabCode);
			long count=0;

			count=service.CommitteeAdd(committeeDto);
			long count1=0;
			if(Long.parseLong(projectid)>0) {
				String ProjectId=req.getParameter("projectid");
				String[] Committee=new String[1];
				Committee[0]=String.valueOf(count);
				count1=service.ProjectCommitteeAdd(ProjectId,Committee,UserId);
			}
			if (count > 0) 
			{
				redir.addAttribute("result", "Committee Added Successfully");
				redir.addAttribute("projectappliacble",req.getParameter("projectapplicable"));
				redir.addAttribute("projectid", projectid);

				return "redirect:/CommitteeList.htm";					
			} 
			else if(count==-1)
			{
				redir.addAttribute("resultfail", "Committee short name already exists ");
			}
			else if(count==-2) 
			{
				redir.addAttribute("resultfail", "Committee "+committeeDto.getCommitteeName()+" already exists,  ");
			}
			else  
			{
				redir.addAttribute("resultfail", "Committee Adding Failed!! Tryagain");
			}
			redir.addAttribute("projectid", projectid);		

			return "redirect:/CommitteeAdd.htm";
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +" Inside CommitteeAddSubmit.htm "+UserId, e);
			return "static/Error";
		}
	}


	@RequestMapping(value = "CommitteeList.htm")
	public String CommitteeList(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");		
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeList.htm "+UserId);
		try {		
			String projectid=req.getParameter("projectid");
			if(projectid==null)
			{			
				Map md=model.asMap();
				projectid=(String)md.get("projectid");
			}
			if(projectid==null)
			{
				projectid="0";
			}			
			String projectappliacble=req.getParameter("projectappliacble");
			if(projectappliacble==null)
			{			
				Map md=model.asMap();
				projectappliacble=(String)md.get("projectappliacble");				
			}

			if(projectappliacble==null)
			{
				projectappliacble="N";
			}

			if(Long.parseLong(projectid)>0)
			{
				req.setAttribute("projectdetails",service.projectdetails(projectid));
			}
			List<Object[]> committeelist= service.CommitteeListActive(projectid,projectappliacble,LabCode);			
			req.setAttribute("projectappliacble",projectappliacble);
			req.setAttribute("projectslist",service.ProjectList(LabCode));
			req.setAttribute("projectid",projectid);
			req.setAttribute("committeelist",committeelist);
			return "committee/CommitteeList";
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +"Inside CommitteeList.htm "+UserId,e);
			return "static/Error";
		}
	}

	@RequestMapping(value="CommitteeEdit.htm")
	public String CommitteeEdit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");		
		logger.info(new Date() +"Inside CommitteeEdit.htm "+UserId);
		String LabCode =(String) ses.getAttribute("labcode");
		try {
			String committeeid=req.getParameter("committeeid");
			Object[] CommitteeDetails=service.CommitteeDetails(committeeid);			
			String projectid=CommitteeDetails[12].toString();
			if(Long.parseLong(projectid)>0)
			{
				req.setAttribute("projectdetails",service.projectdetails(projectid));
			}
			req.setAttribute("committeedetails",CommitteeDetails );	
			req.setAttribute("projectslist",service.ProjectList(LabCode));		
			return "committee/CommitteeEdit";
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside CommitteeEdit.htm "+UserId,e);
			return "static/Error";
		}		
	}

	@RequestMapping(value="CommitteeEditSubmit.htm", method= {RequestMethod.GET,RequestMethod.POST})
	public String CommitteeEditSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");		
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeEditSubmit.htm "+UserId);		
		try {	
			String committeeShortName=req.getParameter("committeeshortname").trim();
			String committeeName=req.getParameter("committeename").trim();
			String guideLines=req.getParameter("guidelines");
			String PeriodicDuration=req.getParameter("periodicduration");
			if(!InputValidator.isValidCapitalsAndSmallsAndNumeric(committeeShortName)) {
				redir.addAttribute("committeemainid", req.getParameter("committeeid"));
				redir.addAttribute("committeeid", req.getParameter("committeeid"));
				return redirectWithError(redir, "CommitteeEdit.htm", "'Committee Code' must contain Alphabets and Numbers.!");
			}
			if(!InputValidator.isValidCapitalsAndSmallsAndNumericAndSpace(committeeName)) {
				redir.addAttribute("committeemainid", req.getParameter("committeeid"));
				redir.addAttribute("committeeid", req.getParameter("committeeid"));
				return redirectWithError(redir, "CommitteeEdit.htm", "'committee Name' must contain Alphabets and Numbers.!");
			}
			if(InputValidator.isContainsHTMLTags(guideLines)) {
				redir.addAttribute("committeemainid", req.getParameter("committeeid"));
				redir.addAttribute("committeeid", req.getParameter("committeeid"));
				return redirectWithError(redir, "CommitteeEdit.htm", "'Guidelines' should not contain HTML Tags.!");
			}
			if(req.getParameter("periodic").equalsIgnoreCase("P") && !InputValidator.isContainsNumberOnly(PeriodicDuration)) {
				redir.addAttribute("committeemainid", req.getParameter("committeeid"));
				redir.addAttribute("committeeid", req.getParameter("committeeid"));
				return redirectWithError(redir, "CommitteeEdit.htm", "'Periodic Duration' must contain Numbers.!");
			}
			
			CommitteeDto committeeDto=new CommitteeDto();
			committeeDto.setCommitteeId(Long.parseLong(req.getParameter("committeeid")));
			committeeDto.setCommitteeName(committeeName);
			committeeDto.setCommitteeShortName(committeeShortName);
			committeeDto.setModifiedBy(UserId);
			committeeDto.setCommitteeType(req.getParameter("committeetype"));
			committeeDto.setProjectApplicable(req.getParameter("projectapplicable"));
			committeeDto.setTechNonTech(req.getParameter("technontech"));
			committeeDto.setPeriodicNon(req.getParameter("periodic"));
			committeeDto.setDescription(req.getParameter("description"));
			committeeDto.setTermsOfReference(req.getParameter("TOR"));
			committeeDto.setIsGlobal(req.getParameter("projectid"));
			committeeDto.setLabCode(LabCode);
			committeeDto.setIsBriefing(req.getParameter("IsBriefing"));
			if(req.getParameter("periodic").equalsIgnoreCase("P"))
			{
				committeeDto.setPeriodicDuration(PeriodicDuration);
			}
			else 
			{
				committeeDto.setPeriodicDuration("0");
			}				
			committeeDto.setGuidelines(guideLines);

			long count=0;
			count=service.CommitteeEditSubmit(committeeDto);				
			if (count > 0) 
			{
				redir.addAttribute("result", "Committee Updated Successfully");
				redir.addAttribute("projectappliacble",req.getParameter("projectapplicable"));
				redir.addAttribute("projectid", req.getParameter("projectid"));
				return "redirect:/CommitteeList.htm";
			} 
			else if(count==-1)
			{
				redir.addAttribute("resultfail", "Committee Short Name Already Exists ");
			}
			else if(count==-2)
			{
				redir.addAttribute("resultfail", "Committee "+committeeDto.getCommitteeName()+" Already Exists  ");
			}
			else 
			{
				redir.addAttribute("resultfail", "Committee Update Failed ");
			}

			redir.addAttribute("committeemainid", req.getParameter("committeeid"));
			redir.addAttribute("committeeid", req.getParameter("committeeid"));
		}
		catch (Exception e) 
		{
			e.printStackTrace(); 
			logger.error(new Date() +"Inside CommitteeEditSubmit.htm "+UserId,e);
		}
		return "redirect:/CommitteeEdit.htm";
	}	
	@RequestMapping(value = "CommitteeNamesCheck.htm", method = RequestMethod.GET)
	public @ResponseBody String CommitteeNamesCheck(HttpSession ses, HttpServletRequest req) throws Exception 
	{
		String UserId=(String)ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		Object[] DisDesc = null;
		logger.info(new Date() +"Inside CommitteeNamesCheck.htm "+UserId);
		try
		{	  
			String name=req.getParameter("fname");
			String sname=req.getParameter("sname");
			String isglobal=req.getParameter("isglobal");			
			DisDesc =service.CommitteeNamesCheck(name,sname,isglobal,LabCode);
		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside CommitteeNamesCheck.htm "+UserId,e);
		}
		Gson json = new Gson();
		return json.toJson(DisDesc); 

	}

	@RequestMapping(value = "CommitteeDetails.htm")
	public String CommitteDetails(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");

		logger.info(new Date() +"Inside CommitteeDetails.htm "+UserId);
		try
		{
			String CommitteeId=req.getParameter("committeeid");
			if(CommitteeId==null)  {
				Map md=model.asMap();
				CommitteeId=(String)md.get("committeeid");
			}

			String divisionid=req.getParameter("divisionid");
			if(divisionid==null)  {
				Map md=model.asMap();
				divisionid=(String)md.get("divisionid");
			}

			String projectid=req.getParameter("projectid");
			if(projectid==null)  {
				Map md=model.asMap();
				projectid=(String)md.get("projectid");
			}

			String initiationid=req.getParameter("initiationid");
			if(initiationid==null)  {
				Map md=model.asMap();
				initiationid=(String)md.get("initiationid");
			}	

			if(projectid==null && divisionid==null && CommitteeId==null )				
			{
				redir.addAttribute("resultfail", "Refresh Not Allowed ! Try again.");
				return "redirect:/MainDashBoard.htm";
			}

			List<Object[]> projectdetailslist=service.ProjectList(LabCode);
			if(Long.parseLong(projectid)>0 && (projectdetailslist.size()==0 || projectdetailslist==null) && !projectid.equals("0")) 
			{
				redir.addAttribute("resultfail", "No Project is Assigned To You.");
				return "redirect:/MainDashBoard.htm";
			}

			req.setAttribute("committeereplist", service.CommitteeRepList());
			req.setAttribute("committeedata", service.CommitteeName(CommitteeId));
			req.setAttribute("employeelist", service.EmployeeList(LabCode));
			req.setAttribute("projectlist", projectdetailslist);
			req.setAttribute("initiationid", initiationid);
			req.setAttribute("projectid", projectid);
			req.setAttribute("divisionid", divisionid);
			req.setAttribute("carsInitiationId", req.getParameter("carsInitiationId"));
			req.setAttribute("divisionslist",service.divisionList());			
			req.setAttribute("initiationdata", service.Initiationdetails(initiationid));
			req.setAttribute("AllLabsList", service.AllLabList());

			req.setAttribute("LabCode", LabCode);
			return "committee/CommitteeDetailsAdd";
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +"Inside CommitteeDetails.htm "+UserId,e);
			return "static/Error";
		}		
	}

	@RequestMapping(value="CommitteeDetailsSubmit.htm",method=RequestMethod.POST)
	public String CommitteeDetailsSubmit(HttpServletRequest req,HttpServletResponse res, RedirectAttributes redir, HttpSession ses )throws Exception
	{
		String Username=(String)ses.getAttribute("Username");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String LabId =(String)ses.getAttribute("labid");
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeDetailsSubmit.htm "+Username);
		try
		{
			String carsInitiationId = req.getParameter("carsInitiationId");
			carsInitiationId = carsInitiationId==null?"0":carsInitiationId;
			
			String referenceNo=req.getParameter("refNo");
			if(InputValidator.isContainsHTMLTags(referenceNo)) {
				redir.addAttribute("committeeid",req.getParameter("committeeid"));
				redir.addAttribute("projectid",req.getParameter("projectid"));
				redir.addAttribute("divisionid",req.getParameter("divisionid"));
				redir.addAttribute("initiationid",req.getParameter("initiationid"));
				redir.addAttribute("carsInitiationId", req.getParameter("carsInitiationId"));
				return redirectWithError(redir, "CommitteeDetails.htm", "'Reference No' should not contain HTML Tags.!");
			}

			CommitteeMainDto committeemaindto=new CommitteeMainDto();
			committeemaindto.setCommitteeId(req.getParameter("committeeid"));
			committeemaindto.setValidFrom(req.getParameter("Fromdate"));
			committeemaindto.setChairperson(req.getParameter("chairperson"));		
			committeemaindto.setSecretary(req.getParameter("Secretary"));
			committeemaindto.setProxySecretary(req.getParameter("proxysecretary"));
			committeemaindto.setCreatedBy(Username);
			committeemaindto.setCpLabCode(req.getParameter("CpLabCode"));
			committeemaindto.setDivisionId(req.getParameter("divisionid"));
			committeemaindto.setProjectId(req.getParameter("projectid"));
			committeemaindto.setInitiationId(req.getParameter("initiationid"));
			committeemaindto.setCARSInitiationId(carsInitiationId);
			committeemaindto.setProgrammeId("0");
			committeemaindto.setReps(req.getParameterValues("repids"));
			committeemaindto.setCreatedByEmpid(EmpId);	
			committeemaindto.setCreatedByEmpidLabid(LabId);
			committeemaindto.setPreApproved(req.getParameter("preApproved"));
			committeemaindto.setLabCode(LabCode);
			// new line//
			committeemaindto.setMsLabCode(req.getParameter("msLabCode"));
			committeemaindto.setCo_Chairperson(req.getParameter("cochairperson"));
			committeemaindto.setReferenceNo(referenceNo);
			committeemaindto.setFormationDate(req.getParameter("Formationdates"));;
			committeemaindto.setCcplabocode(req.getParameter("ccplabocode"));
			long mainid =service.CommitteeDetailsSubmit(committeemaindto);

			if (mainid > 0) {
				redir.addAttribute("result", "Committee Created Successfully");
			} else {
				redir.addAttribute("resultfail", "Committee Create Unsuccessful");
				if(Long.parseLong(req.getParameter("projectid"))>0) {
					return "redirect:/ProjectMaster.htm";
				}else if(Long.parseLong(req.getParameter("divisionid"))>0) {
					return "redirect:/DivisionCommitteeMaster.htm";
				}else if(Long.parseLong(req.getParameter("initiationid"))>0) {
					return "redirect:/InitiationCommitteeMaster.htm";
				}else if(Long.parseLong(carsInitiationId)>0) {
					return "redirect:/CARSInitiationList.htm";
				}else {
					return "redirect:/CommitteeList.htm";
				}

			}
			redir.addAttribute("committeeid",req.getParameter("committeeid"));
			redir.addAttribute("projectid",req.getParameter("projectid"));
			redir.addAttribute("divisionid",req.getParameter("divisionid"));
			redir.addAttribute("initiationid",req.getParameter("initiationid"));
			redir.addAttribute("carsInitiationId", req.getParameter("carsInitiationId"));

			redir.addFlashAttribute("committeemainid",String.valueOf(mainid));
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +"Inside CommitteeDetailsSubmit.htm "+Username,e);
		}
		return"redirect:/CommitteeMainMembers.htm"; 
	}

	@RequestMapping(value="CommitteeMainMembers.htm")
	public String CommitteeMainMembers(Model model,HttpServletRequest req,RedirectAttributes redir,HttpSession ses) throws Exception 
	{			
		String Username=(String)ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeMainMembers.htm "+Username);
		try
		{		
			String initiationid=req.getParameter("initiationid");
			String CommitteeId=req.getParameter("committeeid");
			String projectid=req.getParameter("projectid");	
			String divisionid=req.getParameter("divisionid");	
			String committeemainid=req.getParameter("committeemainid");		
			String carsInitiationId=req.getParameter("carsInitiationId");		

			Map md=model.asMap();

			committeemainid = committeemainid==null?(String)md.get("committeemainid"):committeemainid;
			carsInitiationId = carsInitiationId==null?"0":carsInitiationId;
			if(committeemainid==null)
			{
				if(CommitteeId==null) {
					CommitteeId=(String)md.get("committeeid");
				}			

				if(projectid==null) {
					projectid=(String)md.get("projectid");
				}	

				if(divisionid==null) {
					divisionid=(String)md.get("divisionid");
				}

				if(initiationid==null) {
					initiationid=(String)md.get("initiationid");
				}	

//				if(carsInitiationId==null) {
//					carsInitiationId=(String)md.get("carsInitiationId");
//				}					
				committeemainid=String.valueOf(service.LastCommitteeId(CommitteeId, projectid, divisionid, initiationid, carsInitiationId, "0"));
			}

		

			Object[] proposedcommitteeid = service.GetProposedCommitteeMainId(CommitteeId, projectid, divisionid, initiationid);

			if(committeemainid==null || committeemainid.equals("0"))
			{
				if(proposedcommitteeid!=null)
				{
					committeemainid=proposedcommitteeid[0].toString();
				}
			}
			Object[] committeedata=service.CommitteMainData(committeemainid);	

			if(committeedata==null )
			{		
				redir.addAttribute("resultfail", "No Active Committee Is Present, Constitute New Committee");
				redir.addFlashAttribute("committeeid", CommitteeId);			
				redir.addFlashAttribute("projectid", projectid);	
				redir.addFlashAttribute("divisionid", divisionid);	
				redir.addFlashAttribute("initiationid", initiationid);		
				redir.addFlashAttribute("carsInitiationId", carsInitiationId);		
				redir.addAttribute("noactive","true");
				return "redirect:/CommitteeDetails.htm";				
			}

			CommitteeId=committeedata[1].toString();
			projectid=committeedata[2].toString();
			divisionid=committeedata[3].toString();
			initiationid=committeedata[4].toString();
			carsInitiationId=committeedata[13].toString();

			committeemainid=committeedata[0].toString();	
			List<Object[]> employeelist=service.EmployeeListWithoutMembers(committeemainid,LabCode);
			List<Object[]> employeelist1=service.EmployeeListNoMembers(LabCode,committeemainid);					
			List<Object[]> ExpertList = service.ExternalMembersNotAddedCommittee(committeemainid); 
			Object[] proposedcommitteemainid =  service.ProposedCommitteeMainId(committeemainid);
			List<Object[]> committeemembersall= service.CommitteeAllMembersList(committeemainid);
			Object[] chairperson =null;
			for(int i=0;i<committeemembersall.size();i++)
			{	
				if(committeemembersall.get(i)[8].toString().equalsIgnoreCase("CC")){
					chairperson=committeemembersall.get(i);
				}
			}
			Object[]CommitteMainEnoteList = service.CommitteMainEnoteList(committeemainid,"0");
			// EnoteFlow List
			req.setAttribute("CommitteMainEnoteList", CommitteMainEnoteList);
			req.setAttribute("NewApprovalList", CommitteMainEnoteList!=null ? service.NewApprovalList(CommitteMainEnoteList[0].toString()):service.NewApprovalList("0"));
			req.setAttribute("committeemembersall",committeemembersall);
			req.setAttribute("committeerepnotaddedlist", service.CommitteeRepNotAddedList(committeemainid));
			req.setAttribute("committeeMemberreplist", service.CommitteeMemberRepList(committeemainid));
			req.setAttribute("employeelist", employeelist);
			req.setAttribute("employeelist1", employeelist1);	
			req.setAttribute("committeedata", committeedata);			
			req.setAttribute("expertlist", ExpertList);
			req.setAttribute("AllLabList", service.AllLabList());
			req.setAttribute("divisionid", divisionid);
			req.setAttribute("proposedcommitteemainid",proposedcommitteemainid );
			req.setAttribute("committeeapprovaldata",service.CommitteeMainApprovalData(committeemainid));
			req.setAttribute("constitutionapprovalflow",service.ConstitutionApprovalFlow(committeemainid));
			req.setAttribute("clusterlist", service.ClusterList());
			req.setAttribute("designationlist", adminservice.DesignationList());

			// Prudhvi - 27/03/2024
			/* ------------------ start ----------------------- */
			req.setAttribute("industryPartnerList",  masterservice.getIndustryPartnerList());
			/* ------------------ end ----------------------- */
			if( Long.parseLong(projectid)>0) {
				req.setAttribute("projectdata", service.projectdetails(projectid));
			}
			if( Long.parseLong(divisionid)>0) {
				req.setAttribute("divisiondata", service.DivisionData(divisionid) );
			}
			if(Long.parseLong(initiationid)>0) {
				req.setAttribute("initiationdata", service.Initiationdetails(initiationid) );
			}
			if(Long.parseLong(carsInitiationId)>0) {
				req.setAttribute("carsInitiationData", carsservice.getCARSInitiationById(Long.parseLong(carsInitiationId)) );
			}



			req.setAttribute("LabCode", LabCode);

			return "committee/CommitteeMembers";
		}
		catch (Exception e) {
			e.printStackTrace();			
			logger.error(new Date() +"Inside CommitteeMainMembers.htm "+Username ,e);
			return "static/Error";
		}
	}	

	@RequestMapping(value="CommitteeMainMembersSubmit.htm")
	public String CommitteeMainMembersSubmit(Model model,HttpServletRequest req,RedirectAttributes redir,HttpSession ses) throws Exception 
	{		
		String Username=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteeMainMembersSubmit.htm "+Username);
		try
		{	
			String ccmflag = req.getParameter("ccmflag");
			String prgmflag = req.getParameter("prgmflag");
			CommitteeMembersDto dto=new CommitteeMembersDto();
			dto.setCommitteeMainId(req.getParameter("committeemainid"));
			dto.setInternalMemberIds(req.getParameterValues("InternalMemberIds"));
			dto.setInternalLabCode(req.getParameter("InternalLabCode"));
			dto.setExternalMemberIds(req.getParameterValues("ExternalMemberIds"));
			dto.setExternalLabCode(req.getParameter("Ext_LabCode"));
			dto.setExpertMemberIds(req.getParameterValues("ExpertMemberIds"));
			// Prudhvi 27/03/2024
			/* ------------------ start ----------------------- */
			dto.setIndustrialPartnerRepIds(req.getParameterValues("industryPartnerRep"));
			dto.setIndustrialPartnerLabCode(req.getParameter("industrypartnerlabid"));
			/* ------------------ end ----------------------- */
			
			dto.setRepIds(req.getParameterValues("RepIds"));
			
			dto.setCreatedBy(Username);

			long count=service.CommitteeMembersInsert(dto);
			if (count > 0) {
				redir.addAttribute("result", "Committee Members Added Successfully");
			} else {
				redir.addAttribute("resultfail", "Committee Members Add Unsuccessful");	
			}			

			if(ccmflag!=null && ccmflag.equalsIgnoreCase("Y")) {
				redir.addAttribute("committeeMainId", req.getParameter("committeemainid"));
				redir.addAttribute("committeeId", req.getParameter("committeeId"));

				return "redirect:/CCMCommitteeConstitution.htm";
			}
			
			if(prgmflag!=null && prgmflag.equalsIgnoreCase("Y")) {
				redir.addAttribute("committeeMainId", req.getParameter("committeemainid"));
				redir.addAttribute("committeeId", req.getParameter("committeeId"));
				redir.addAttribute("programmeId", req.getParameter("programmeId"));
				
				return "redirect:/PrgmCommitteeConstitution.htm";
			}
			
			redir.addAttribute("committeemainid", req.getParameter("committeemainid"));
			return "redirect:/CommitteeMainMembers.htm";

		}
		catch (Exception e) {
			e.printStackTrace();			
			logger.error(new Date() +"Inside CommitteeMainMembersSubmit.htm "+Username ,e);
			return "static/Error";
		}
	}


	@RequestMapping(value="CommitteeRepMemberAdd.htm",method=RequestMethod.POST)
	public String CommitteeRepMemberAdd(HttpServletRequest req,HttpServletResponse res, RedirectAttributes redir, HttpSession ses )throws Exception
	{
		String Username=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteeRepMemberAdd.htm "+Username);
		try
		{
			String committeemainid=req.getParameter("committeemainid");
			String[] repids=req.getParameterValues("repids");

			service.CommitteeRepMemberAdd(repids,committeemainid,Username);

			String prgmflag = req.getParameter("prgmflag");
			if(prgmflag!=null && prgmflag.equalsIgnoreCase("Y")) {
				redir.addAttribute("committeeMainId", req.getParameter("committeemainid"));
				redir.addAttribute("committeeId", req.getParameter("committeeId"));
				redir.addAttribute("programmeId", req.getParameter("programmeId"));

				return "redirect:/PrgmCommitteeConstitution.htm";
			}
			
			redir.addFlashAttribute("committeemainid",committeemainid );
			return"redirect:/CommitteeMainMembers.htm";
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside CommitteeRepMemberAdd.htm "+Username,e);
			return "static/Error";

		}

	} 


	@RequestMapping(value="CommitteeFlow.htm",method= {RequestMethod.POST,RequestMethod.GET})
	public String CommitteeFlowApproval(HttpServletRequest req,HttpServletResponse res, RedirectAttributes redir, HttpSession ses )throws Exception
	{
		String Username=(String)ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeFlow.htm "+Username);
		try
		{
			String committeemainid=req.getParameter("committeemainid");

			Object[]CommitteMainEnoteList= service.CommitteMainEnoteList(committeemainid,"0");
			req.setAttribute("CommitteMainEnoteList", CommitteMainEnoteList);
			Object[] committeedata=service.CommitteMainData(committeemainid);	
			req.setAttribute("committeedata", committeedata);

			req.setAttribute("NewApprovalList", CommitteMainEnoteList!=null ? service.NewApprovalList(CommitteMainEnoteList[0].toString()):service.NewApprovalList("0"));

			String projectid=committeedata[2].toString();
			String divisionid=committeedata[3].toString();
			String initiationid=committeedata[4].toString();
			if( Long.parseLong(projectid)>0) {
				req.setAttribute("projectdata", service.projectdetails(projectid));
			}
			if( Long.parseLong(divisionid)>0) {
				req.setAttribute("divisiondata", service.DivisionData(divisionid) );
			}
			if(Long.parseLong(initiationid)>0) {
				req.setAttribute("initiationdata", service.Initiationdetails(initiationid) );
			}
			req.setAttribute("employeelist", service.EmployeeList(LabCode));
			req.setAttribute("committeemainid", committeemainid);
			req.setAttribute("AllLabList", service.AllLabList());
			return "committee/CommitteeFlowAdd";
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside CommitteeFlow.htm "+Username,e);
			return "static/Error";

		}

	} 

	@RequestMapping(value="CommitteeRecommendation.htm",method= {RequestMethod.POST,RequestMethod.GET})
	public String CommitteeRecommendation(HttpServletRequest req,HttpServletResponse res, RedirectAttributes redir, HttpSession ses )throws Exception
	{
		String Username=(String)ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeRecommendation.htm "+Username);

		try {
			String committeemainid=req.getParameter("committeemainid");
			String scheduleid=req.getParameter("scheduleid");
			String type=req.getParameter("type");
			req.setAttribute("CommitteMainEnoteList", service.CommitteMainEnoteList(committeemainid,scheduleid));
			Object[] committeedata=service.CommitteMainData(committeemainid);
			req.setAttribute("committeemainid", committeemainid);
			req.setAttribute("type", type);
			req.setAttribute("employeelist", service.EmployeeList(LabCode));
			req.setAttribute("NewApprovalList", service.NewApprovalList(service.CommitteMainEnoteList(committeemainid,scheduleid)[0].toString()));
		}catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside CommitteeRecommendation.htm "+Username,e);
			return "static/Error";
		}

		return "committee/CommitteeRecommendation";
	}
	@RequestMapping(value="CommitteeEnotePrint.htm",method= {RequestMethod.POST,RequestMethod.GET})
	public String CommitteeEnotePrint(HttpServletRequest req,HttpServletResponse res, RedirectAttributes redir, HttpSession ses )throws Exception
	{
		String Username=(String)ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeEnotePrint.htm "+Username);

		try {
			String committeemainid=req.getParameter("committeemainid");

			String scheduleid=req.getParameter("scheduleid");
			String type=req.getParameter("type");
			if(scheduleid==null) {
				scheduleid="0";
			}
			req.setAttribute("CommitteMainEnoteList", service.CommitteMainEnoteList(committeemainid,scheduleid));
			Object[] committeedata=service.CommitteMainData(committeemainid);
			req.setAttribute("committeemainid", committeemainid);
			req.setAttribute("scheduleid", scheduleid);
			req.setAttribute("type", type);
			String EnoteId=req.getParameter("EnoteId");
			List<Object[]> EnotePrintDetails=service.EnotePrintDetails(Long.parseLong(EnoteId),type);
			req.setAttribute("EnotePrintDetails", EnotePrintDetails);
			req.setAttribute("EnotePrintDetails", EnotePrintDetails);
			String filename="Committee_ENote_Print";
			req.setAttribute("NewApprovalList", service.NewApprovalList(service.CommitteMainEnoteList(committeemainid,scheduleid)[0].toString()));
			if(type.equalsIgnoreCase("S")) {
				filename="MOM_ENote_Print";
			}
			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path); 
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/committee/CommitteeEnotePrint.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();
			byte[] data = html.getBytes();
			InputStream fis1=new ByteArrayInputStream(data);
			PdfDocument pdfDoc = new PdfDocument(new PdfWriter(path+"/"+filename+".pdf"));	

			Document document = new Document(pdfDoc, PageSize.A4);
			//document.setMargins(50, 100, 150, 50);
			document.setMargins(50, 50, 50, 50);
			ConverterProperties converterProperties = new ConverterProperties();
			FontProvider dfp = new DefaultFontProvider(true, true, true);
			converterProperties.setFontProvider(dfp);
			HtmlConverter.convertToPdf(fis1,pdfDoc,converterProperties);
			res.setContentType("application/pdf");
			res.setHeader("Content-disposition","inline;filename="+filename+".pdf"); 
			File f=new File(path+"/"+filename+".pdf");
			FileInputStream fis = new FileInputStream(f);
			DataOutputStream os = new DataOutputStream(res.getOutputStream());
			res.setHeader("Content-Length",String.valueOf(f.length()));
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fis.read(buffer)) >= 0) {
				os.write(buffer, 0, len);
			} 
			fis.close();
			os.close();



			Path pathOfFile2= Paths.get(path+"/"+filename+".pdf"); 
			Files.delete(pathOfFile2);		

			document.close();

		}catch (Exception e) {
			// TODO: handle exception
		}

		return "committee/CommitteeEnotePrint";
	}


	@RequestMapping(value="EnoteForward.htm",method=RequestMethod.POST)
	public String CommitteeEnoteUpdate(HttpServletRequest req,HttpServletResponse res, RedirectAttributes redir, HttpSession ses )throws Exception
	{
		String Username=(String)ses.getAttribute("Username");
		Long EmpId = (Long)ses.getAttribute("EmpId");
		String labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside EnoteForward.htm "+Username);
		try
		{


			String action= req.getParameter("action");
			String flag = req.getParameter("flag");
			String flow =req.getParameter("flow");

			System.out.println("flow" +flow);

			long flagcount=0;
			PmsEnote pe = req.getParameter("EnoteId")!=null && req.getParameter("EnoteId").equalsIgnoreCase("0")? new PmsEnote():  service.getPmsEnote(req.getParameter("EnoteId"));

			if(flag!=null && flag.equalsIgnoreCase("UpdateForward")) {
				pe.setRefNo(req.getParameter("RefNo"));
				pe.setRefDate(pe.getRefDate()!=null ? pe.getRefDate(): java.sql.Date.valueOf(req.getParameter("RefDate")));
				pe.setSubject(req.getParameter("subject").trim());
				pe.setComment(req.getParameter("Comment").trim());
				pe.setCommitteeMainId(Long.parseLong(req.getParameter("committeemainid")));
				pe.setEnoteFrom("C");
				pe.setEnoteStatusCode(pe.getEnoteStatusCode()==null?"INI":pe.getEnoteStatusCode());
				pe.setEnoteStatusCodeNext(pe.getEnoteStatusCodeNext()==null ? "INI":pe.getEnoteStatusCodeNext());
				pe.setScheduleId(0l);
				pe.setInitiatedBy(Long.parseLong(req.getParameter("InitiatedBy")));
				if(req.getParameter("Recommend1")!=null) {
					pe.setRecommend1(Long.parseLong(req.getParameter("Recommend1")));
					pe.setRec1_Role(req.getParameter("Rec1_Role").toUpperCase());
				}
				if(req.getParameter("Recommend2").length()>0) {;
				pe.setRecommend2(Long.parseLong(req.getParameter("Recommend2")));
				pe.setRec2_Role(req.getParameter("Rec2_Role").toUpperCase());
				}else {
					pe.setRecommend2(null);
					pe.setRec2_Role(null);
				}
				if(req.getParameter("Recommend3").length()>0) {
					pe.setRecommend3(Long.parseLong(req.getParameter("Recommend3")));
					pe.setRec3_Role(req.getParameter("Rec3_Role").toUpperCase());
				}else {
					pe.setRecommend3(null);
					pe.setRec3_Role(null);
				}
				if(req.getParameter("ApprovingOfficer")!=null) {
					pe.setApprovingOfficer(Long.parseLong(req.getParameter("ApprovingOfficer")));;
				}
				pe.setApproving_Role(req.getParameter("Approving_Role").toUpperCase());
				pe.setApprovingOfficerLabCode(req.getParameter("ApprovingOfficerLabCode"));;
				if(pe.getCreatedBy()==null) {
					pe.setCreatedBy(Username);	
					pe.setCreatedDate(sdf1.format(new Date()));	
				}else {
					pe.setModifiedBy(Username);
					pe.setModifiedDate(sdf1.format(new Date()));
				}
				pe.setIsActive(1);
				flagcount = service.addPmsEnote(pe);
			}
			pe = service.getPmsEnote(req.getParameter("EnoteId"));

			if(action!=null && action.equalsIgnoreCase("UPDATE")) {

				if (flagcount > 0) {
					redir.addAttribute("result", "Data Saved Successfully");
				} else {
					redir.addAttribute("resultfail", "FlowData Add Unsuccessful");	
				}
				redir.addAttribute("initiationid", req.getParameter("initiationid"));
				redir.addAttribute("projectid", req.getParameter("projectid"));
				redir.addAttribute("divisionid", req.getParameter("divisionid"));
				redir.addAttribute("committeemainid", req.getParameter("committeemainid"));
				return"redirect:/CommitteeFlow.htm";
			}

			if(flow!=null && flow.equalsIgnoreCase("REV")) {

				if(!pe.getEnoteStatusCode().equalsIgnoreCase("FWD")) {
					redir.addAttribute("resultfail", "It can not be Revoked!");
					redir.addAttribute("initiationid", req.getParameter("initiationid"));
					redir.addAttribute("projectid", req.getParameter("projectid"));
					redir.addAttribute("divisionid", req.getParameter("divisionid"));
					redir.addAttribute("committeemainid", req.getParameter("committeemainid"));
					return"redirect:/CommitteeFlow.htm";
				}

			}

			String remarks= req.getParameter("Remarks");
			pe.setSessionLabCode(labcode);
			long result = service.EnoteForward(pe,remarks,EmpId,flow,Username);
			if(flow!=null && flow.equalsIgnoreCase("REV")) {
				if(result>0) {
					redir.addAttribute("result", "Committee Flow Revoked Successfully");	
				}else {
					redir.addAttribute("resultfail", "Committee Flow Revoke Unsuccessful");	
				}
				redir.addAttribute("initiationid", req.getParameter("initiationid"));
				redir.addAttribute("projectid", req.getParameter("projectid"));
				redir.addAttribute("divisionid", req.getParameter("divisionid"));
				redir.addAttribute("committeemainid", req.getParameter("committeemainid"));
				return"redirect:/CommitteeFlow.htm";

			}

			if(flow!=null && flow.equalsIgnoreCase("R")) {
				if(result>0) {
					redir.addAttribute("result", "Committee Flow Returned Successfully");	
				}else {
					redir.addAttribute("resultfail", "Committee Flow Return Unsuccessful");	
				}
				return"redirect:/CommitteeApprovalList.htm";
			}
			if(flow!=null && flow.equalsIgnoreCase("A")&& flag==null) {
				if(result>0) {
					redir.addAttribute("result", "Committee Flow Forwarded Successfully");	
				}else {
					redir.addAttribute("resultfail", "Committee Flow Forward Unsuccessful");	
				}
				return"redirect:/CommitteeApprovalList.htm";
			}


			if(flag!=null && flag.equalsIgnoreCase("UpdateForward")) {
				if(result>0) {
					redir.addAttribute("result", "Committee Flow Forwarded Successfully");	
				}else {
					redir.addAttribute("resultfail", "Committee Flow Forward Unsuccessful");	
				}
				redir.addAttribute("initiationid", req.getParameter("initiationid"));
				redir.addAttribute("projectid", req.getParameter("projectid"));
				redir.addAttribute("divisionid", req.getParameter("divisionid"));
				redir.addAttribute("committeemainid", req.getParameter("committeemainid"));
				return"redirect:/CommitteeFlow.htm";
			}



		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside EnoteForward.htm "+Username,e);
		}
		return"redirect:/CommitteeFlow.htm";
	} 



	//MoMEnote
	@RequestMapping(value="MomEnoteForward.htm",method= {RequestMethod.POST,RequestMethod.GET})
	public String MomEnoteForward(HttpServletRequest req,HttpServletResponse res, RedirectAttributes redir, HttpSession ses )throws Exception
	{
		String Username=(String)ses.getAttribute("Username");
		System.out.println("Username ---"+Username);
		Long EmpId = (Long)ses.getAttribute("EmpId");
		logger.info(new Date() +"Inside MomEnoteForward.htm "+Username);
		try
		{


			String action= req.getParameter("action");
			String flag = req.getParameter("flag");
			String flow =req.getParameter("flow");
			String labcode = (String)ses.getAttribute("labcode");
			System.out.println("flow" +flow);
			
			String Subject=req.getParameter("subject").trim();
			String Comment=req.getParameter("Comment").trim();
			String officer1Role=req.getParameter("Rec1_Role").toUpperCase();
			String officer2Role=req.getParameter("Rec2_Role").toUpperCase();
			String officer3Role=req.getParameter("Rec3_Role").toUpperCase();
			String approvingOfficerRole=req.getParameter("Approving_Role").toUpperCase();

			long flagcount=0;
			
	
			if(Subject!=null &&  InputValidator.isContainsHTMLTags(Subject)) {
				redir.addAttribute("committeescheduleid", req.getParameter("scheduleid"));
			    redir.addAttribute("committeeMainId", req.getParameter("committeeMainId"));
			    redir.addAttribute("committeeId", req.getParameter("committeeId"));
			    redir.addAttribute("ccmFlag", req.getParameter("ccmFlag"));
				return redirectWithError(redir, "MeetingMinutesApproval.htm", "'Subject' should not contain HTML Tags.!");
			}
			if(Comment!=null && InputValidator.isContainsHTMLTags(Comment)) {
				redir.addAttribute("committeescheduleid", req.getParameter("scheduleid"));
			    redir.addAttribute("committeeMainId", req.getParameter("committeeMainId"));
			    redir.addAttribute("committeeId", req.getParameter("committeeId"));
			    redir.addAttribute("ccmFlag", req.getParameter("ccmFlag"));
				return redirectWithError(redir, "MeetingMinutesApproval.htm", "'Comment' should not contain HTML Tags.!");
			}
			if(officer1Role!=null && !officer1Role.isEmpty()  && !InputValidator.isContainsDescriptionPattern(officer1Role)) {
				redir.addAttribute("committeescheduleid", req.getParameter("scheduleid"));
			    redir.addAttribute("committeeMainId", req.getParameter("committeeMainId"));
			    redir.addAttribute("committeeId", req.getParameter("committeeId"));
			    redir.addAttribute("ccmFlag", req.getParameter("ccmFlag"));
				return redirectWithError(redir, "MeetingMinutesApproval.htm", "'Officer1 Role' should contains Alphabets, Numbers or special characters.!");
			}
			if(officer2Role!=null && !officer2Role.isEmpty() && !InputValidator.isContainsDescriptionPattern(officer2Role)) {
				redir.addAttribute("committeescheduleid", req.getParameter("scheduleid"));
			    redir.addAttribute("committeeMainId", req.getParameter("committeeMainId"));
			    redir.addAttribute("committeeId", req.getParameter("committeeId"));
			    redir.addAttribute("ccmFlag", req.getParameter("ccmFlag"));
				return redirectWithError(redir, "MeetingMinutesApproval.htm", "'Officer2 Role' should contains Alphabets, Numbers or special characters.!");
			}
			if(officer3Role!=null && !officer3Role.isEmpty() &&  !InputValidator.isContainsDescriptionPattern(officer3Role)) {
				redir.addAttribute("committeescheduleid", req.getParameter("scheduleid"));
			    redir.addAttribute("committeeMainId", req.getParameter("committeeMainId"));
			    redir.addAttribute("committeeId", req.getParameter("committeeId"));
			    redir.addAttribute("ccmFlag", req.getParameter("ccmFlag"));
				return redirectWithError(redir, "MeetingMinutesApproval.htm", "'Officer3 Role' should contains Alphabets, Numbers or special characters.!");
			}
			if(approvingOfficerRole!=null && !InputValidator.isContainsDescriptionPattern(approvingOfficerRole)) {
				redir.addAttribute("committeescheduleid", req.getParameter("scheduleid"));
			    redir.addAttribute("committeeMainId", req.getParameter("committeeMainId"));
			    redir.addAttribute("committeeId", req.getParameter("committeeId"));
			    redir.addAttribute("ccmFlag", req.getParameter("ccmFlag"));
				return redirectWithError(redir, "MeetingMinutesApproval.htm", "'Approving Officer Role' should contains Alphabets, Numbers or special characters.!");
			}
			
			PmsEnote pe = req.getParameter("EnoteId")!=null && req.getParameter("EnoteId").equalsIgnoreCase("0")? new PmsEnote():  service.getPmsEnote(req.getParameter("EnoteId"));
			
			
//			if(Stream.of(Subject , Comment, officer1Role,officer2Role,officer3Role,approvingOfficerRole)
//					.anyMatch(field -> InputValidator.isContainsHTMLTags(field))) {
//				redir.addAttribute("committeescheduleid", req.getParameter("scheduleid"));
//			    redir.addAttribute("committeeMainId", req.getParameter("committeeMainId"));
//			    redir.addAttribute("committeeId", req.getParameter("committeeId"));
//			    redir.addAttribute("ccmFlag", req.getParameter("ccmFlag"));
//				return  redirectWithError(redir, "MeetingMinutesApproval.htm", "Html Tags Not Allowed");
//			}
			if(flag!=null && flag.equalsIgnoreCase("UpdateForward")) {
				
				
				pe.setRefNo(req.getParameter("RefNo"));
				pe.setRefDate(pe.getRefDate()!=null ? pe.getRefDate(): java.sql.Date.valueOf(req.getParameter("RefDate")));
				pe.setSubject(Subject);
				pe.setComment(Comment);
				pe.setCommitteeMainId(0l);
				pe.setEnoteFrom("S");
				pe.setEnoteStatusCode(pe.getEnoteStatusCode()==null?"INI":pe.getEnoteStatusCode());
				pe.setEnoteStatusCodeNext(pe.getEnoteStatusCodeNext()==null ? "INI":pe.getEnoteStatusCodeNext());
				pe.setScheduleId(Long.parseLong(req.getParameter("scheduleid")));
				pe.setInitiatedBy(Long.parseLong(req.getParameter("InitiatedBy")));
				if(req.getParameter("Recommend1")!=null) {
					pe.setRecommend1(Long.parseLong(req.getParameter("Recommend1")));
					pe.setRec1_Role(officer1Role);
				}
				if(req.getParameter("Recommend2").length()>0) {;
				pe.setRecommend2(Long.parseLong(req.getParameter("Recommend2")));
				pe.setRec2_Role(officer2Role);
				}else {
					pe.setRecommend2(null);
					pe.setRec2_Role(null);
				}
				if(req.getParameter("Recommend3").length()>0) {
					pe.setRecommend3(Long.parseLong(req.getParameter("Recommend3")));
					pe.setRec3_Role(officer3Role);
				}else {
					pe.setRecommend3(null);
					pe.setRec3_Role(null);
				}
				if(req.getParameter("ApprovingOfficer")!=null) {
					pe.setApprovingOfficer(Long.parseLong(req.getParameter("ApprovingOfficer")));;
					pe.setApprovingOfficerLabCode(labcode);
				}
				pe.setApproving_Role(approvingOfficerRole);
				if(pe.getCreatedBy()==null) {
					pe.setCreatedBy(Username);	
					pe.setCreatedDate(sdf1.format(new Date()));	
				}else {
					pe.setModifiedBy(Username);
					pe.setModifiedDate(sdf1.format(new Date()));
				}
				pe.setIsActive(1);
				flagcount = service.addPmsEnote(pe);
			}
			pe = service.getPmsEnote(req.getParameter("EnoteId"));

			if(action!=null && action.equalsIgnoreCase("UPDATE")) {

				if (flagcount > 0) {
					redir.addAttribute("result", "Data Saved Successfully");
				} else {
					redir.addAttribute("resultfail", "FlowData Add Unsuccessful");	
				}

				redir.addAttribute("committeescheduleid", req.getParameter("scheduleid"));
				String ccmFlag = req.getParameter("ccmFlag");
				if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {
					redir.addAttribute("ccmScheduleId", req.getParameter("scheduleid"));
					redir.addAttribute("committeeMainId", req.getParameter("committeeMainId"));
					redir.addAttribute("committeeId", req.getParameter("committeeId"));
					redir.addAttribute("ccmFlag", ccmFlag);
				}
				return"redirect:/MeetingMinutesApproval.htm";
			}

			if(flow!=null && flow.equalsIgnoreCase("REV")) {

				if(!pe.getEnoteStatusCode().equalsIgnoreCase("FWD")) {
					redir.addAttribute("resultfail", "It can not be Revoked!");
					redir.addAttribute("committeescheduleid", req.getParameter("scheduleid"));
					String ccmFlag = req.getParameter("ccmFlag");
					if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {
						redir.addAttribute("ccmScheduleId", req.getParameter("scheduleid"));
						redir.addAttribute("committeeMainId", req.getParameter("committeeMainId"));
						redir.addAttribute("committeeId", req.getParameter("committeeId"));
						redir.addAttribute("ccmFlag", ccmFlag);
					}
					return"redirect:/MeetingMinutesApproval.htm";
				}

			}

			String remarks= req.getParameter("Remarks");
			long result = service.EnoteForward(pe,remarks,EmpId,flow,Username);
			if(flow!=null && flow.equalsIgnoreCase("REV")) {
				if(result>0) {
					redir.addAttribute("result", "MoM Revoked Successfully");	
				}else {
					redir.addAttribute("resultfail", "MoM Revoke Unsuccessful");	
				}
				redir.addAttribute("committeescheduleid", req.getParameter("scheduleid"));
				String ccmFlag = req.getParameter("ccmFlag");
				if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {
					redir.addAttribute("ccmScheduleId", req.getParameter("scheduleid"));
					redir.addAttribute("committeeMainId", req.getParameter("committeeMainId"));
					redir.addAttribute("committeeId", req.getParameter("committeeId"));
					redir.addAttribute("ccmFlag", ccmFlag);
				}
				return"redirect:/MeetingMinutesApproval.htm";

			}

			if(flow!=null && flow.equalsIgnoreCase("R")) {
				if(result>0) {
					redir.addAttribute("result", "MoM Returned Successfully");	
				}else {
					redir.addAttribute("resultfail", "MoM Flow Return Unsuccessful");	
				}
				return"redirect:/MoMApprovalList.htm";
			}
			if(flow!=null && flow.equalsIgnoreCase("A")&& flag==null) {
				if(result>0) {
					redir.addAttribute("result", "MoM Forwarded Successfully");	
				}else {
					redir.addAttribute("resultfail", "MoM Forward Unsuccessful");	
				}
				return"redirect:/MoMApprovalList.htm";
			}


			if(flag!=null && flag.equalsIgnoreCase("UpdateForward")) {
				if(result>0) {
					redir.addAttribute("result", "MoM Forwarded Successfully");	
				}else {
					redir.addAttribute("resultfail", "MoM Forward Unsuccessful");	
				}
				redir.addAttribute("committeescheduleid", req.getParameter("scheduleid"));
				String ccmFlag = req.getParameter("ccmFlag");
				if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {
					redir.addAttribute("ccmScheduleId", req.getParameter("scheduleid"));
					redir.addAttribute("committeeMainId", req.getParameter("committeeMainId"));
					redir.addAttribute("committeeId", req.getParameter("committeeId"));
					redir.addAttribute("ccmFlag", ccmFlag);
				}
				return"redirect:/MeetingMinutesApproval.htm";
			}



		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside MomEnoteForward.htm "+Username,e);
		}
		return"redirect:/CommitteeFlow.htm";
	} 


	@RequestMapping(value = "EnoteStatusTrack.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String EnoteStatusTrack(HttpServletResponse resp, HttpServletRequest req, HttpSession ses) throws Exception {

		logger.info(new Date() +"EnoteStatusTrack.htm"+req.getUserPrincipal().getName());

		String EnoteTrackId=req.getParameter("EnoteTrackId").trim();

		req.setAttribute("EnoteTransactionList",service.EnoteTransactionList(EnoteTrackId));
		return "committee/EnoteTrack";

	}
	@RequestMapping(value = "CommitteeApprovalList.htm" , method = {RequestMethod.POST,RequestMethod.GET})
	public String EnoteApprovalList(HttpServletRequest req,HttpServletResponse resp,HttpSession ses) throws Exception {
		logger.info(new Date() +"CommitteeApprovalList.htm"+req.getUserPrincipal().getName());
		long EmpId=(Long)ses.getAttribute("EmpId");
		try {
			String fromDate=(String)req.getParameter("FromDate");
			String toDate=(String)req.getParameter("ToDate");

			String redirectedvalue=req.getParameter("redirectedvalue");
			if(redirectedvalue!=null) {
				req.setAttribute("redirectedvalueForward", redirectedvalue);
			}
			if(toDate == null) 
			{
				toDate=LocalDate.now().toString();
			}else
			{
				fromDate=sdf2.format(rdf.parse(fromDate));
			}

			if(fromDate==null) {
				fromDate=LocalDate.now().minusYears(1).toString();
			}else {
				toDate=sdf2.format(rdf.parse(toDate));
			}
			List<Object[]> eNotePendingList =service.eNotePendingList(EmpId,"C");
			List<Object[]> eNoteApprovalList=service.eNoteApprovalList(EmpId,fromDate,toDate);
			req.setAttribute("eNoteApprovalList", eNoteApprovalList);
			req.setAttribute("EnoteApprovalPendingList", eNotePendingList);
			req.setAttribute("frmDt", fromDate);
			req.setAttribute("toDt",   toDate);
			return "committee/CommitteeApprovalList";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@RequestMapping(value = "MoMApprovalList.htm" , method = {RequestMethod.POST,RequestMethod.GET})
	public String MoMApprovalList(HttpServletRequest req,HttpServletResponse resp,HttpSession ses) throws Exception {
		logger.info(new Date() +"MoMApprovalList.htm"+req.getUserPrincipal().getName());
		long EmpId=(Long)ses.getAttribute("EmpId");
		try {
			String fromDate=(String)req.getParameter("FromDate");
			String toDate=(String)req.getParameter("ToDate");

			String redirectedvalue=req.getParameter("redirectedvalue");
			if(redirectedvalue!=null) {
				req.setAttribute("redirectedvalueForward", redirectedvalue);
			}
			if(toDate == null) 
			{
				toDate=LocalDate.now().toString();
			}else
			{
				fromDate=sdf2.format(rdf.parse(fromDate));
			}

			if(fromDate==null) {
				fromDate=LocalDate.now().minusDays(30).toString();
			}else {
				toDate=sdf2.format(rdf.parse(toDate));
			}
			List<Object[]> eNotePendingList =service.eNotePendingList(EmpId,"S");
			List<Object[]> eNoteApprovalList=service.MomeNoteApprovalList(EmpId,fromDate,toDate);
			req.setAttribute("eNoteApprovalList", eNoteApprovalList);
			req.setAttribute("EnoteApprovalPendingList", eNotePendingList);
			req.setAttribute("frmDt", fromDate);
			req.setAttribute("toDt",   toDate);
			return "committee/MoMApprovalList";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@RequestMapping(value="CommitteeMemberDelete.htm",method=RequestMethod.POST)
	public String CommitteeMemberDelete(HttpServletRequest req,HttpServletResponse res, RedirectAttributes redir, HttpSession ses )throws Exception
	{
		String Username=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteeMemberDelete.htm "+Username);
		try
		{
			String ccmflag = req.getParameter("ccmflag");
			String prgmflag = req.getParameter("prgmflag");
			String committeememberid=req.getParameter("committeememberid");
			String committeemainid = req.getParameter("committeemainid");

			int count=0;
			count=service.CommitteeMemberDelete(committeememberid, Username);

			if (count > 0) {
				redir.addAttribute("result", "Committee Member Deleted Successfully");
			} else {
				redir.addAttribute("resultfail", "Committee Member Delete Unsuccessful");	
			}

			if(ccmflag!=null && ccmflag.equalsIgnoreCase("Y")) {
				redir.addAttribute("committeeMainId", committeemainid);
				redir.addAttribute("committeeId", req.getParameter("committeeId"));

				return "redirect:/CCMCommitteeConstitution.htm";
			}
			
			if(prgmflag!=null && prgmflag.equalsIgnoreCase("Y")) {
				redir.addAttribute("committeeMainId", committeemainid);
				redir.addAttribute("committeeId", req.getParameter("committeeId"));
				redir.addAttribute("programmeId", req.getParameter("programmeId"));

				return "redirect:/PrgmCommitteeConstitution.htm";
			}
			
			redir.addAttribute("committeemainid", committeemainid);
			return "redirect:/CommitteeMainMembers.htm";
			

		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside CommitteeMemberDelete.htm "+Username,e);
			return "static/Error";
		}

	} 

	@RequestMapping(value="CommitteeMainMembersAddSubmit.htm",method=RequestMethod.POST)
	public String CommitteeMainMembersAddSubmit(HttpServletRequest req,HttpServletResponse res, RedirectAttributes redir, HttpSession ses )throws Exception
	{

		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteeMainMembersAddSubmit.htm "+UserId);
		try
		{
			String committeemainid=req.getParameter("committeemainid");
			String committeeid=req.getParameter("committeeid");

			String[] Member=req.getParameterValues("Member");
			long count=0;
			count=service.CommitteeMainMembersAddSubmit( committeemainid,Member,UserId);

			if (count > 0) {
				redir.addAttribute("result", "Committee Members Added Successfully");
			} else {
				redir.addAttribute("resultfail", "Committee Member Adding Failed");	
			}


			redir.addFlashAttribute("projectid",req.getParameter("projectid"));	
			redir.addFlashAttribute("committeeid",committeeid);
			redir.addFlashAttribute("divisionid","0");

		} catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside CommitteeMainMembersAddSubmit.htm "+UserId,e);
		}
		return "redirect:/CommitteeMainMembers.htm";
	}

	@RequestMapping(value="MasterScheduleListSelect.htm",method=RequestMethod.GET)
	public String MasterScheduleListSelect(HttpServletRequest req,HttpServletResponse res, RedirectAttributes redir, HttpSession ses) throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside MasterScheduleListSelect.htm "+UserId);
		try
		{		

			List<Object[]> committeeactivelist=service.CommitteeMainList(labcode);
			if(committeeactivelist.size()==0)
			{
				redir.addAttribute("resultfail", "No Committee is Constituted, Constitute a Non-Project Committee ");
//				redir.addFlashAttribute("id","N");
				return "redirect:/MainDashBoard.htm";
			}			
			req.setAttribute("CommitteeList", committeeactivelist);
		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside MasterScheduleListSelect.htm "+UserId,e);
		}
		//return "committee/CommitteeScheduleSelect";
		return "redirect:/CommitteeScheduleList.htm";
	}

	@RequestMapping(value="CommitteeScheduleList.htm")
	public String CommitteeScheduleList(HttpServletRequest req,HttpServletResponse res, RedirectAttributes redir, HttpSession ses) throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeScheduleList.htm "+UserId);
		try
		{
			String committeeid=req.getParameter("committeeid");
			List<Object[]> committeeactivelist=service.CommitteeMainList(labcode);
			if(committeeid==null && committeeactivelist!=null && committeeactivelist.size()>0){
				committeeid = String.valueOf(committeeactivelist.get(0)[1]);
			} 
			req.setAttribute("initiationid","0");
			req.setAttribute("committeeid",committeeid);
			req.setAttribute("committeedetails",service.CommitteeDetails(committeeid));
			req.setAttribute("committeeschedulelist",service.CommitteeScheduleListNonProject(committeeid) );

			req.setAttribute("CommitteeList", committeeactivelist);
			return "committee/CommitteeScheduleSelect";
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside CommitteeScheduleList.htm "+UserId,e);
			return "static/Error";
		}		
	}


	@RequestMapping(value="CommitteeScheduleAddSubmit.htm",method=RequestMethod.POST)
	public String CommitteeScheduleAddSubmit(HttpServletRequest req,HttpServletResponse res, RedirectAttributes redir, HttpSession ses) throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeScheduleAddSubmit.htm "+UserId);
		try
		{
			String committeeid=req.getParameter("committeeid");			
			String projectid=req.getParameter("projectid");
			String divisionid=req.getParameter("divisionid");
			String initiationid=req.getParameter("initiationid");
			// Added by Prudhvi on 21-10-2024
			String carsInitiationId = req.getParameter("carsInitiationId");
			String committeeMainId = req.getParameter("committeeMainId");

			CommitteeScheduleDto committeescheduledto = new CommitteeScheduleDto(); 
			committeescheduledto.setCommitteeId(Long.parseLong(req.getParameter("committeeid")));
			committeescheduledto.setScheduleDate(req.getParameter("startdate"));
			committeescheduledto.setScheduleStartTime(req.getParameter("starttime"));
			committeescheduledto.setCreatedBy(UserId);
			committeescheduledto.setScheduleFlag("MSC");			
			committeescheduledto.setProjectId(projectid);
			committeescheduledto.setDivisionId(divisionid);
			committeescheduledto.setInitiationId(initiationid);
			committeescheduledto.setLabCode(labcode);
			
			// Added by Prudhvi on 21-10-2024
			committeescheduledto.setCARSInitiationId(carsInitiationId!=null?carsInitiationId:"0");
			committeescheduledto.setCommitteeMainId(committeeMainId!=null?Long.parseLong(committeeMainId):0);
			
			committeescheduledto.setProgrammeId(req.getParameter("programmeId"));
			
			if(projectid!=null && Long.parseLong(projectid)>0) 
			{
				Object[] projectdetails=service.projectdetails(projectid);				
				committeescheduledto.setConfidential(projectdetails[10].toString());
			}else {
				committeescheduledto.setConfidential("5");
			}


			long count = service.CommitteeScheduleAddSubmit(committeescheduledto);
			redir.addAttribute("committeeid",committeeid);

			String CommitteeName=req.getParameter("committeename");

			if(count>0)
			{
				redir.addAttribute("result", CommitteeName + " Schedule Added Successfully !");
			}
			else 
			{
				redir.addAttribute("resultfail", "Schedule Adding Failed, Try Again");	
			}			
			redir.addAttribute("scheduleid", String.valueOf(count));
			
			if(committeescheduledto.getProgrammeId()!=null && !committeescheduledto.getProgrammeId().equals("0")) {
				redir.addAttribute("programmeId", committeescheduledto.getProgrammeId());
				return "redirect:/PrgmScheduleAgenda.htm";
			}
			
			return "redirect:/CommitteeScheduleAgenda.htm";
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +"Inside CommitteeScheduleAddSubmit.htm "+UserId,e);
			return "static/Error";
		}
	}

	@RequestMapping(value="ScheduleDefAgendaAdd.htm",method= {RequestMethod.POST})
	public String ScheduleDefAgendaAdd(Model model,HttpServletRequest req,HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside ScheduleDefAgendaAdd.htm "+UserId);
		try
		{
			List<String> defagendaid=Arrays.asList(req.getParameterValues("defagendaid"));
			List<String> defagendaid1=Arrays.asList(req.getParameterValues("defagendaid1"));
			String AgendaItem[]=req.getParameterValues("agendaitem");
			String Duration[]=req.getParameterValues("duration");
			String ProjectId[]=req.getParameterValues("projectid");
			String Remarks[]= req.getParameterValues("remarks");
			String presenters[]=req.getParameterValues("presenterid");
			String PresLabCode[]=req.getParameterValues("PresLabCode");

			List<CommitteeScheduleAgendaDto> scheduleagendadtos=new ArrayList<CommitteeScheduleAgendaDto>();


			for(int i=0;i<defagendaid1.size();i++) 
			{
				if(defagendaid.contains(defagendaid1.get(i))) 
				{
					CommitteeScheduleAgendaDto scheduleagendadto = new CommitteeScheduleAgendaDto();
					scheduleagendadto.setScheduleId(req.getParameter("scheduleid"));
					scheduleagendadto.setScheduleSubId("1");
					scheduleagendadto.setAgendaItem(AgendaItem[i]);
					scheduleagendadto.setPresentorLabCode(PresLabCode[i]);
					scheduleagendadto.setPresenterId(presenters[i]);
					scheduleagendadto.setDuration(Duration[i]);
					scheduleagendadto.setProjectId(ProjectId[i]);
					scheduleagendadto.setRemarks(Remarks[i]);
					scheduleagendadto.setCreatedBy(UserId);
					scheduleagendadto.setIsActive("1");
					scheduleagendadto.setDocLinkIds(req.getParameterValues("attachid_"+defagendaid1.get(i)));
					scheduleagendadtos.add(scheduleagendadto);
				}
			}

			long count=service.CommitteeAgendaSubmit(scheduleagendadtos);

			if (count > 0) {
				redir.addAttribute("result", "Agendas Added Successfully");
			} else {
				redir.addAttribute("resultfail", "Agendas Add Unsuccessful");
			}


			redir.addFlashAttribute("scheduleid",req.getParameter("scheduleid"));
			return "redirect:/CommitteeScheduleAgenda.htm";
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +"Inside ScheduleDefAgendaAdd.htm "+UserId,e);
			return "static/Error";
		}
	}

	@RequestMapping(value="CommitteeScheduleAgenda.htm",method= {RequestMethod.GET,RequestMethod.POST})
	public String CommitteeScheduleAgenda(Model model,HttpServletRequest req,HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode =ses.getAttribute("labcode").toString().trim();
		logger.info(new Date() +"Inside CommitteeScheduleAgenda.htm "+UserId);
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
			Object[] scheduledata=service.CommitteeScheduleEditData(CommitteeScheduleId);
			List<Object[]> committeeagendalist = service.AgendaList(CommitteeScheduleId);

			String projectid = scheduledata[9].toString();
			req.setAttribute("AllLabList", service.AllLabList());
			req.setAttribute("scheduledata",scheduledata);
			req.setAttribute("projectlist", service.ProjectList(LabCode));
			req.setAttribute("preProjectlist", service.preProjectlist(LabCode));
			req.setAttribute("committeeagendalist", committeeagendalist);
			req.setAttribute("labdata", service.LabDetails(scheduledata[24].toString()));			
			req.setAttribute("filesize",file_size);
			req.setAttribute("projectid",projectid);
			req.setAttribute("filerepmasterlistall",service.FileRepMasterListAll(projectid,LabCode));
			req.setAttribute("AgendaDocList",service.AgendaLinkedDocList(CommitteeScheduleId));

			req.setAttribute("LabCode",LabCode);
			req.setAttribute("LabEmpList",service.PreseneterForCommitteSchedule(LabCode.trim()));
			//			Object[] DefaultAgendasCount = service.getDefaultAgendasCount(scheduledata[0].toString(), LabCode);
			//			if(Integer.parseInt(DefaultAgendasCount[0].toString())>0 && committeeagendalist.size()==0 && req.getParameter("skip")==null)
			//			{
			//				List<Object[]> defAgendaList = service.DefaultAgendaList(scheduledata[0].toString(),LabCode);
			//				req.setAttribute("defAgendaList",defAgendaList);
			//				
			//				return "committee/ScheduleDefaultAgendas";
			//			}
			return "committee/CommitteeScheduleAgenda";
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +"Inside CommitteeScheduleAgenda.htm "+UserId,e);
			return "static/Error";
		}
	}


	@RequestMapping(value = "CommitteeAgendaPresenterList.htm", method = RequestMethod.GET)
	public @ResponseBody String CommitteeAgendaPresenterList(HttpServletRequest req,HttpSession ses) throws Exception 
	{
		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside CommitteeAgendaPresenterList.htm"+ UserId);

		List<Object[]> EmployeeList = new ArrayList<Object[]>();

		try {
			String CsLabCode =req.getParameter("PresLabCode");
			if(CsLabCode.trim().equalsIgnoreCase("@EXP")) 
			{
				EmployeeList = service.ClusterExpertsListForCommitteeSchdule();
			}
			else
			{
				EmployeeList = service.PreseneterForCommitteSchedule(CsLabCode.trim());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CommitteeAgendaPresenterList.htm "+UserId, e);
		}

		Gson json = new Gson();
		return json.toJson(EmployeeList);	
	}



	@RequestMapping(value="AgendaUnlinkDoc.htm",method= RequestMethod.GET)
	public @ResponseBody String AgendaUnlinkDoc(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		int ret=0;
		logger.info(new Date() +"Inside AgendaUnlinkDoc.htm "+UserId);
		try
		{
			CommitteeScheduleAgendaDocs agendadoc = new CommitteeScheduleAgendaDocs();
			agendadoc.setAgendaDocid(Long.parseLong(req.getParameter("AttachDocid")));
			agendadoc.setModifiedBy(UserId);
			ret= service.AgendaUnlinkDoc(agendadoc);
		}catch (Exception e) 
		{
			e.printStackTrace(); 
			logger.error(new Date() +"Inside AgendaUnlinkDoc.htm "+UserId,e);

		}
		Gson json = new Gson();
		return json.toJson(ret); 

	}


	@RequestMapping(value="CommitteeScheduleView.htm",method= {RequestMethod.GET,RequestMethod.POST})
	public String CommitteeScheduleView(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception 
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
			//			String MemberType=null;
			//			if(req.getParameter("membertype")!=null) {
			//				MemberType=(String)req.getParameter("membertype");
			//			}
			String CommitteeScheduleId=null;

			if(req.getParameter("scheduleid")!=null) {
				CommitteeScheduleId=(String)req.getParameter("scheduleid");
			}
			else {
				CommitteeScheduleId=(String)md.get("scheduleid");
			}
			System.out.println(CommitteeScheduleId+"-----"+req.getParameter("scheduleid"));
			int committeecons=0;
			Object[] committeescheduleeditdata=service.CommitteeScheduleEditData(CommitteeScheduleId);
			String projectid= committeescheduleeditdata[9].toString();
			String committeeid=committeescheduleeditdata[0].toString();
			String divisionid=committeescheduleeditdata[16].toString();
			String initiationid=committeescheduleeditdata[17].toString();
			String carsInitiationId=committeescheduleeditdata[25].toString();
			String programmeId=committeescheduleeditdata[26]!=null?committeescheduleeditdata[26].toString():"0";
			
			List<Object[]> committeeagendalist =service.AgendaList(CommitteeScheduleId);

			Long committeemainid=null;
			if(Long.parseLong(initiationid)>0) {
				committeemainid=service.LastCommitteeId(committeeid, projectid, divisionid, initiationid, carsInitiationId, programmeId);
			}else
			{
				committeemainid=service.LastCommitteeId(committeeid, projectid, divisionid, initiationid, carsInitiationId, programmeId);
			}

			if(committeemainid==null || committeemainid==0)
			{
				committeecons=0;			
			}
			else
			{
				committeecons=1;				
			}
			if(Long.parseLong(projectid)>0)
			{
				req.setAttribute("projectdetails", service.projectdetails(projectid));
			}
			if(Long.parseLong(divisionid)>0)
			{
				req.setAttribute("divisiondetails", service.DivisionData(divisionid));
			}
			if(Long.parseLong(initiationid)>0)
			{
				req.setAttribute("initiationdetails", service.Initiationdetails(initiationid));
			}
			if(Long.parseLong(carsInitiationId)>0)
			{
				req.setAttribute("carsInitiationDetails", carsservice.getCARSInitiationById(Long.parseLong(carsInitiationId)));
			}
			if(Long.parseLong(programmeId)>0)
			{
				req.setAttribute("prgmMasterDetails", service.getProgrammeMasterById(programmeId));
				committeeagendalist = service.PrgmAgendaList(CommitteeScheduleId);
			}
			
			if(Logintype.equalsIgnoreCase("A") || Logintype.equalsIgnoreCase("Z") || Logintype.equalsIgnoreCase("C")|| Logintype.equalsIgnoreCase("I")) 
			{
				if(md.get("otp")!=null) {
					req.setAttribute("otp", md.get("otp"));
				}
			}

			//			req.setAttribute("membertype", MemberType);
			req.setAttribute("userview",UserView);			
			req.setAttribute("ReturnData", service.AgendaReturnData(CommitteeScheduleId));
			req.setAttribute("committeescheduleeditdata", committeescheduleeditdata);
			req.setAttribute("committeeagendalist", committeeagendalist);
			req.setAttribute("committeeinvitedlist", service.CommitteeAtendance(CommitteeScheduleId,"0"));	
			req.setAttribute("employeelist", service.EmployeeList(LabCode));
			req.setAttribute("pfmscategorylist", service.PfmsCategoryList());
			req.setAttribute("logintype", Logintype);
			req.setAttribute("committeecons", committeecons); 
			req.setAttribute("AgendaDocList",service.AgendaLinkedDocList(CommitteeScheduleId));
			List<Object[]> specialCommitteesList = printservice.SpecialCommitteesList(LabCode);
			req.setAttribute("SplCommitteeCodes", specialCommitteesList.stream().map(e -> e[1].toString()).collect(Collectors.toList()));

			int useraccess=service.ScheduleCommitteeEmpCheck
					(new EmpAccessCheckDto(Logintype,CommitteeScheduleId,EmpId,
							committeescheduleeditdata[1], committeecons,
							committeescheduleeditdata[13].toString()));
			req.setAttribute("useraccess", useraccess);
			return "committee/CommitteeScheduleView";
		}
		catch (Exception e) 
		{
			e.printStackTrace(); 
			logger.error(new Date() +"Inside CommitteeScheduleView.htm "+UserId,e);
			return "static/Error";
		}

	}


	@RequestMapping(value="CommitteeAgendaSubmit.htm",method=RequestMethod.POST)
	public String CommitteeAgendaSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception  //,@RequestPart("FileAttach") MultipartFile[] FileAttach
	{ 		
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteeAgendaSubmit.htm "+UserId);
		try
		{
			String statusflag = req.getParameter("redirpageflag");
			String prgmstatus = req.getParameter("prgmflag");
			String AgendaItem[]=req.getParameterValues("agendaitem");
			String Duration[]=req.getParameterValues("duration");
			String ProjectId[]=req.getParameterValues("projectid");
			String Remarks[]= req.getParameterValues("remarks");
			String presenters[]=req.getParameterValues("presenterid");
			String PresLabCode[]=req.getParameterValues("PresLabCode");
			String groupNames[]=req.getParameterValues("groupname");
			
			ArrayList<String[]> docids = new ArrayList<String[]>();
			for(int i=0 ; i<AgendaItem.length ;i++) {
				docids.add( req.getParameterValues("attachid_"+i));
			}
			
			if (AgendaItem!=null && containsHTMLTags(AgendaItem)) {
				redir.addAttribute("scheduleid",req.getParameter("scheduleid"));
				if(prgmstatus!=null && prgmstatus.equalsIgnoreCase("Y")) return redirectWithError(redir, "PrgmScheduleAgenda.htm", "'Agenda' should not contain HTML Tags.!");
			    if(statusflag!=null && statusflag.equalsIgnoreCase("ROD")) return redirectWithError(redir, "RODScheduleAgenda.htm", "'Agenda' should not contain HTML Tags.!");
				return redirectWithError(redir, "CommitteeScheduleAgenda.htm", "'Agenda' should not contain HTML Tags.!");
			} 
			if (Remarks!=null && containsHTMLTags(Remarks)) {
				redir.addAttribute("scheduleid",req.getParameter("scheduleid"));
				if(prgmstatus!=null && prgmstatus.equalsIgnoreCase("Y")) return redirectWithError(redir, "PrgmScheduleAgenda.htm", "'Remarks' should not contain HTML Tags.!");
			    if(statusflag!=null && statusflag.equalsIgnoreCase("ROD")) return redirectWithError(redir, "RODScheduleAgenda.htm", "'Remarks' should not contain HTML Tags.!");
				return redirectWithError(redir, "CommitteeScheduleAgenda.htm", "'Remarks' should not contain HTML Tags.!");
			}
			if (groupNames!=null && containsHTMLTags(groupNames)) {
				redir.addAttribute("scheduleid",req.getParameter("scheduleid"));
				if(prgmstatus!=null && prgmstatus.equalsIgnoreCase("Y")) return redirectWithError(redir, "PrgmScheduleAgenda.htm", "'GroupName' should not contain HTML Tags.!");
				if(statusflag!=null && statusflag.equalsIgnoreCase("ROD")) return redirectWithError(redir, "RODScheduleAgenda.htm", "'GroupName' should not contain HTML Tags.!");
				return redirectWithError(redir, "CommitteeScheduleAgenda.htm", "'GroupName' should not contain HTML Tags.!");
			}
			List<CommitteeScheduleAgendaDto> scheduleagendadtos=new ArrayList<CommitteeScheduleAgendaDto>();
			for(int i=0;i<AgendaItem.length;i++)
			{
				String presentorId = presenters[i] != null && presenters[i].toString().equalsIgnoreCase("-1") ? null : presenters[i].toString();
				CommitteeScheduleAgendaDto scheduleagendadto = new CommitteeScheduleAgendaDto();
				scheduleagendadto.setScheduleId(req.getParameter("scheduleid"));
				scheduleagendadto.setScheduleSubId("1");
				scheduleagendadto.setAgendaItem(AgendaItem[i]);
				scheduleagendadto.setPresentorLabCode(PresLabCode[i]);
				scheduleagendadto.setPresenterId(presentorId);
				scheduleagendadto.setDuration(Duration[i]);
				scheduleagendadto.setProjectId(ProjectId[i]);
				scheduleagendadto.setRemarks(Remarks[i]);
				scheduleagendadto.setCreatedBy(UserId);
				scheduleagendadto.setIsActive("1");
				//				scheduleagendadto.setAgendaAttachment(FileAttach[i].getBytes());
				//				scheduleagendadto.setAttachmentName(FileAttach[i].getOriginalFilename());
				scheduleagendadto.setDocLinkIds(docids.get(i));
				scheduleagendadtos.add(scheduleagendadto);
				if(groupNames!=null && groupNames[i]!=null) scheduleagendadto.setGroupName(groupNames[i]);
			}

			long count=service.CommitteeAgendaSubmit(scheduleagendadtos);

			if (count > 0) {
				redir.addAttribute("result", "Agenda Added Successfully");
			} else {
				redir.addAttribute("resultfail", "Agenda Add Unsuccessful");
			}
			System.out.println(req.getParameter("scheduleid")+"=============================");
			redir.addFlashAttribute("scheduleid", req.getParameter("scheduleid"));
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside CommitteeAgendaSubmit.htm "+UserId,e);
		}
		// Prudhvi - 04/03/2024
		/* ------------------ start ----------------------- */
		String redirpageflag = req.getParameter("redirpageflag");
		if(redirpageflag!=null && redirpageflag.equalsIgnoreCase("ROD")) {
			return"redirect:/RODScheduleView.htm";
		}
		
		/* ------------------ end ----------------------- */
		return"redirect:/CommitteeScheduleView.htm";
	}

	@RequestMapping(value="CommitteeScheduleAgendaEdit.htm",method=RequestMethod.POST)
	public String CommitteeScheduleAgendaEdit(HttpServletRequest req,HttpServletResponse res, RedirectAttributes redir, HttpSession ses )throws Exception  //,@RequestPart("FileAttach") MultipartFile FileAttach
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteeScheduleAgendaEdit.htm "+UserId);
		try
		{
			String statusflag = req.getParameter("redirpageflag");
			String prgmflag1 = req.getParameter("prgmflag");
			String agendaitem= req.getParameter("agendaitem");
			String projectid=req.getParameter("projectid");
			String remarks=req.getParameter("remarks");
			String presentorid=req.getParameter("presenterid");
			String duration=req.getParameter("duration");
			String PresLabCode=req.getParameter("PresLabCode");
			String groupName=req.getParameter("groupname");
			
			if (InputValidator.isContainsHTMLTags(agendaitem)) {
				redir.addAttribute("scheduleid",req.getParameter("scheduleid"));
				if(prgmflag1!=null && prgmflag1.equalsIgnoreCase("Y")) return redirectWithError(redir, "PrgmScheduleAgenda.htm", "'Agenda' should not contain HTML Tags.!");
			    if(statusflag!=null && statusflag.equalsIgnoreCase("ROD")) return redirectWithError(redir, "RODScheduleAgenda.htm", "'Agenda' should not contain HTML Tags.!");
				return redirectWithError(redir, "CommitteeScheduleAgenda.htm", "'Agenda' should not contain HTML Tags.!");
			} 
			if (InputValidator.isContainsHTMLTags(remarks)) {
				redir.addAttribute("scheduleid",req.getParameter("scheduleid"));
				if(prgmflag1!=null && prgmflag1.equalsIgnoreCase("Y")) return redirectWithError(redir, "PrgmScheduleAgenda.htm", "'Remarks' should not contain HTML Tags.!");
				if(statusflag!=null && statusflag.equalsIgnoreCase("ROD")) return redirectWithError(redir, "RODScheduleAgenda.htm", "'Remarks' should not contain HTML Tags.!");
				return redirectWithError(redir, "CommitteeScheduleAgenda.htm", "'Remarks' should not contain HTML Tags.!");
			}
			if (groupName!=null && InputValidator.isContainsHTMLTags(groupName)) {
				redir.addAttribute("scheduleid",req.getParameter("scheduleid"));
				if(prgmflag1!=null && prgmflag1.equalsIgnoreCase("Y")) return redirectWithError(redir, "PrgmScheduleAgenda.htm", "'GroupName' should not contain HTML Tags.!");
				if(statusflag!=null && statusflag.equalsIgnoreCase("ROD")) return redirectWithError(redir, "RODScheduleAgenda.htm", "'GroupName' should not contain HTML Tags.!");
				return redirectWithError(redir, "CommitteeScheduleAgenda.htm", "'GroupName' should not contain HTML Tags.!");
			}
			
//			System.out.println(groupName+"===================================");
			
			//			String docid=req.getParameter("editattachid");
			CommitteeScheduleAgendaDto scheduleagendadto = new CommitteeScheduleAgendaDto();
			scheduleagendadto.setPresentorLabCode(PresLabCode);
			scheduleagendadto.setScheduleAgendaId(req.getParameter("committeescheduleagendaid"));
			scheduleagendadto.setScheduleId(req.getParameter("scheduleid"));
			scheduleagendadto.setScheduleSubId("1");
			scheduleagendadto.setAgendaItem(agendaitem);
			scheduleagendadto.setPresenterId(presentorid);
			scheduleagendadto.setDuration(duration);
			scheduleagendadto.setProjectId(projectid);
			scheduleagendadto.setRemarks(remarks);
			scheduleagendadto.setModifiedBy(UserId);
			scheduleagendadto.setDocLinkIds( req.getParameterValues("attachid"));
			if(groupName!=null) scheduleagendadto.setGroupName(groupName);
			//			scheduleagendadto.setAgendaAttachment(FileAttach.getBytes());
			//			scheduleagendadto.setAttachmentName(FileAttach.getOriginalFilename());
			//			scheduleagendadto.setDocId(docid);
			String agendaattachmentid=req.getParameter("committeeattachmentid");



			long count = 0;
			count=	service.CommitteeScheduleAgendaEdit(scheduleagendadto,agendaattachmentid);

			if (count > 0) {
				redir.addAttribute("result", "Agenda Edited Successfully");
			} else {
				redir.addAttribute("resultfail", "Agenda Edit Unsuccessful");

			}

			redir.addAttribute("scheduleid", req.getParameter("scheduleid"));
			
			// Prudhvi - 04/03/2024
			/* ------------------ start ----------------------- */
			String redirpageflag = req.getParameter("redirpageflag");
			if(redirpageflag!=null && redirpageflag.equalsIgnoreCase("ROD")) {
				return"redirect:/RODScheduleView.htm";
			}
			/* ------------------ end ----------------------- */
			
			String prgmflag = req.getParameter("prgmflag");
			if(prgmflag!=null && prgmflag.equalsIgnoreCase("Y")) {
				return"redirect:/PrgmScheduleAgenda.htm";
			}
			return"redirect:/CommitteeScheduleAgenda.htm";
			
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside CommitteeScheduleAgendaEdit.htm "+UserId,e);
			return "static/Error";
		}
		
	}


	@RequestMapping(value="CommitteeAgendaPriorityUpdate.htm",method=RequestMethod.GET)
	public String CommitteeAgendaPriorityUpdate(HttpServletRequest req,HttpServletResponse res, RedirectAttributes redir, HttpSession ses)throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteeAgendaPriorityUpdate.htm "+UserId);
		try
		{
			String[] priority=req.getParameterValues("priority");
			String[] agendaid=req.getParameterValues("agendaid");

			Set<String> s = new HashSet<String>(Arrays.asList(priority));

			if (s.size() == priority.length) {
				service.CommitteeAgendaPriorityUpdate(agendaid, priority);
				redir.addAttribute("result", "Agenda Priority Updated Successfully");

			} else {
				redir.addAttribute("resultfail", "Agenda Priority Updated UnSuccessfull");
			}	
			
			redir.addAttribute("scheduleid", req.getParameter("scheduleid"));
			
			String prgmflag = req.getParameter("prgmflag");
			if(prgmflag!=null && prgmflag.equalsIgnoreCase("Y")) {
				return"redirect:/PrgmScheduleAgenda.htm";
			}
			
			return"redirect:/CommitteeScheduleAgenda.htm";
		}
		catch (Exception e) {

			e.printStackTrace();
			logger.error(new Date() +"Inside CommitteeAgendaPriorityUpdate.htm "+UserId,e);
			return "static/Error";
		}	

	}


	@RequestMapping(value = "CommitteeAgendaDelete.htm", method = RequestMethod.POST)
	public String TccAgendaDelete(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteeAgendaDelete.htm "+UserId);
		try
		{
			String Committeescheduleagendaid = req.getParameter("committeescheduleagendaid");
			String attachmentid = req.getParameter("committeeattachmentid");
			String scheduleid = req.getParameter("scheduleid");
			String AgendaPriority = req.getParameter("AgendaPriority");

			int count = service.CommitteeAgendaDelete(Committeescheduleagendaid, attachmentid, UserId, scheduleid, AgendaPriority);
			if (count > 0) {
				redir.addAttribute("result", " Agenda Deleted Successfully");
			} else {
				redir.addAttribute("resultfail", "Agenda Delete Unsuccessful");
			}

			redir.addAttribute("scheduleid", req.getParameter("scheduleid"));
			
			String prgmflag = req.getParameter("prgmflag");
			if(prgmflag!=null && prgmflag.equalsIgnoreCase("Y")) {
				return"redirect:/PrgmScheduleAgenda.htm";
			}
			
			return"redirect:/CommitteeScheduleAgenda.htm";
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside CommitteeAgendaDelete.htm "+UserId,e);
			return "static/Error";
		}
	}

	@RequestMapping(value="CommitteeMainEditSubmit.htm",method= {RequestMethod.GET,RequestMethod.POST})
	public String CommitteeMainEditSubmit(HttpServletRequest req,HttpServletResponse res, RedirectAttributes redir, HttpSession ses )throws Exception
	{
		String Username=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteeMainEditSubmit.htm "+Username);
		try
		{		
			
			String referenceNo=req.getParameter("Reference No.");
			if(InputValidator.isContainsHTMLTags(referenceNo)) {
				redir.addFlashAttribute("committeemainid",req.getParameter("committeemainid"));
				return redirectWithError(redir, "CommitteeMainMembers.htm", "'Reference No' should not contain HTML Tags.!");
			}
			CommitteeMembersEditDto dto=new CommitteeMembersEditDto();

			CommitteeMainDto cmd=new CommitteeMainDto(); // added by praksh
			dto.setChairperson(req.getParameter("chairperson"));
			dto.setSecretary(req.getParameter("Secretary"));
			dto.setProxysecretary(req.getParameter("proxysecretary"));
			dto.setSesLabCode((String) ses.getAttribute("labcode"));
			dto.setCpLabCode(req.getParameter("CpLabCode"));
			dto.setCommitteemainid(req.getParameter("committeemainid"));
			dto.setChairpersonmemid(req.getParameter("cpmemberid"));
			dto.setSecretarymemid(req.getParameter("msmemberid"));
			dto.setProxysecretarymemid(req.getParameter("psmemberid"));			
			dto.setModifiedBy(Username);		
			dto.setCo_chairperson(req.getParameter("co_chairperson"));
			dto.setComemberid(req.getParameter("comemberid"));
			dto.setMsLabCode(req.getParameter("msLabCode"));
			cmd.setFormationDate(!req.getParameter("Formationdates").isEmpty()?req.getParameter("Formationdates"):LocalDate.now().toString());
			cmd.setReferenceNo(referenceNo);
			cmd.setCcplabocode(req.getParameter("ccplabocode"));

			System.out.println("setCcplabocode---"+cmd.getCcplabocode());
			
			long count =service.CommitteeMainMemberUpdate(dto,cmd);			



			if (count > 0) {
				redir.addAttribute("result", "Committee Edited Successfully");
			} else {
				redir.addAttribute("resultfail", "Committee Edit Unsuccessful");

			}

			redir.addFlashAttribute("committeemainid",req.getParameter("committeemainid"));

		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside CommitteeMainEditSubmit.htm "+Username,e);
		}
		return "redirect:/CommitteeMainMembers.htm";
	}

	@RequestMapping(value="CommitteeScheduleUpdate.htm",method=RequestMethod.POST)
	public String CommitteeScheduleUpdate(HttpServletRequest req, HttpSession ses,RedirectAttributes redir) throws Exception{

		String UserId=(String)ses.getAttribute("Username");

		logger.info(new Date() +"Inside CommitteeScheduleUpdate.htm "+UserId);
		try
		{		
			CommitteeScheduleDto committeescheduledto=new CommitteeScheduleDto(); 
			committeescheduledto.setScheduleDate(req.getParameter("committeedate"));
			committeescheduledto.setScheduleStartTime(req.getParameter("committeetime"));
			committeescheduledto.setScheduleId(Long.parseLong(req.getParameter("scheduleid")));
			committeescheduledto.setCreatedBy(UserId);

			long count = service.CommitteeScheduleUpdate(committeescheduledto);

			if (count > 0) {
				redir.addAttribute("result", "Committee Schedule Edited Successfully");
			} else {
				redir.addAttribute("resultfail", "Committee Schedule Edit Unsuccessful");
			}

			redir.addFlashAttribute("scheduleid",req.getParameter("scheduleid"));
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside CommitteeScheduleUpdate.htm "+    UserId,e);
		}

		return "redirect:/CommitteeScheduleView.htm";
	}


	@RequestMapping(value="CommitteeVenueUpdate.htm")
	public String CommitteeVenueUpdate(HttpServletRequest req, HttpSession ses,RedirectAttributes redir) throws Exception{

		String UserId=(String)ses.getAttribute("Username");		
		logger.info(new Date() +"Inside CommitteeVenueUpdate.htm "+UserId);
		try
		{		
			String Venue=req.getParameter("venue");
			String Decisions=req.getParameter("decisions");
			String Reference=req.getParameter("reference");
			if(Venue!=null && InputValidator.isContainsHTMLTags(Venue)) {
				redir.addFlashAttribute("scheduleid",req.getParameter("scheduleid"));
				return redirectWithError(redir, "CommitteeScheduleView.htm", "'Venue' should not contain HTML Tags.!");
			}
			if(Decisions!=null && InputValidator.isContainsHTMLTags(Decisions)) {
				redir.addFlashAttribute("scheduleid",req.getParameter("scheduleid"));
				return redirectWithError(redir, "CommitteeScheduleView.htm", "'Decisions/Recommendations sought from Meeting' should not contain HTML Tags.!");
			}
			if(Reference!=null && InputValidator.isContainsHTMLTags(Reference)) {
				redir.addFlashAttribute("scheduleid",req.getParameter("scheduleid"));
				return redirectWithError(redir, "CommitteeScheduleView.htm", "'Reference' should not contain HTML Tags.!");
			}
			CommitteeScheduleDto committeescheduledto=new CommitteeScheduleDto(); 
			committeescheduledto.setScheduleId(Long.parseLong(req.getParameter("scheduleid")));
			committeescheduledto.setMeetingVenue(Venue);
			committeescheduledto.setConfidential(req.getParameter("isconfidential"));
			committeescheduledto.setReferrence(Reference);
			committeescheduledto.setPMRCDecisions(Decisions);		
			int count=0;
			count = service.UpdateMeetingVenue(committeescheduledto);

			if (count > 0) {
				redir.addAttribute("result", "Committee Schedule Edited Successfully");
			} else {
				redir.addAttribute("resultfail", "Committee Schedule Edit Unsuccessful");				
			}			
			redir.addFlashAttribute("scheduleid",req.getParameter("scheduleid"));		
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside CommitteeVenueUpdate.htm "+   UserId,e);
		} 
		return "redirect:/CommitteeScheduleView.htm";
	}

	@RequestMapping(value="CommitteeScheduleMinutes.htm",method= {RequestMethod.GET,RequestMethod.POST})
	public String CommitteeScheduleMinutes(Model model,HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		String labcode=(String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeScheduleMinutes.htm "+UserId);
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
				return "redirect:/CommitteeScheduleMinutes.htm";
			}

			if(req.getParameter("membertype")!=null) {
				MemberType=req.getParameter("membertype");
			}
			else {
				MemberType=(String)md.get("membertype");
			}

			if(labcode.equalsIgnoreCase("ADE")) {
				req.setAttribute("subsystemList", service.getSubSystemList(CommitteeScheduleId));
				req.setAttribute("aircraftList", service.getAircraftList(CommitteeScheduleId));
			}
			else {
				req.setAttribute("subsystemList", service.getSubSystemList(CommitteeScheduleId));
				req.setAttribute("aircraftList", service.getAircraftList(CommitteeScheduleId));
			}

			List<Object[]> specialCommitteesList = printservice.SpecialCommitteesList(labcode);
			req.setAttribute("SplCommitteeCodes", specialCommitteesList.stream().map(e -> e[1].toString()).collect(Collectors.toList()));
			req.setAttribute("unit1", unit1);
			req.setAttribute("formname", formname);
			req.setAttribute("membertype",MemberType);
			req.setAttribute("specname", specname);
			req.setAttribute("committscheduleid", CommitteeScheduleId);
			req.setAttribute("committeescheduleeditdata", service.CommitteeScheduleEditData(CommitteeScheduleId));
			req.setAttribute("minutesspeclist", service.CommitteeMinutesSpecList(CommitteeScheduleId));

			String ccmFlag = req.getParameter("ccmFlag");
			if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {
				req.setAttribute("agendaList", ccmservice.getCCMScheduleAgendaListByCCMScheduleId(CommitteeScheduleId));
			}else {
				req.setAttribute("committeeagendalist", service.AgendaList(CommitteeScheduleId));
			}

			req.setAttribute("minutesoutcomelist", service.MinutesOutcomeList());
			req.setAttribute("minutesattachmentlist",service.MinutesAttachmentList(CommitteeScheduleId));
			req.setAttribute("committeescheduledata",service.CommitteeActionList(CommitteeScheduleId));
			req.setAttribute("filesize",file_size);
			req.setAttribute("MomAttachment", service.MomAttachmentFile(CommitteeScheduleId));

			req.setAttribute("ccmFlag", ccmFlag);
			req.setAttribute("committeeMainId", req.getParameter("committeeMainId"));
			req.setAttribute("committeeId", req.getParameter("committeeId"));
			
			req.setAttribute("redirect", req.getParameter("redirect"));

			String dmcFlag = req.getParameter("dmcFlag");
			if(dmcFlag!=null && dmcFlag.equalsIgnoreCase("Y")) {
				req.setAttribute("dmcScheduleList", ccmservice.getScheduleListByScheduleType("D"));
			}
			req.setAttribute("dmcFlag", dmcFlag);
		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside CommitteeScheduleMinutes.htm "+
					UserId,e);
		}		
		return "committee/CommitteeScheduleMinutes";
	}

	@RequestMapping(value="CommitteeMinutesSubmit.htm", method=RequestMethod.POST)
	public String CommitteeMinutesSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{

		String UserId=(String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeMinutesSubmit.htm "+UserId);
		try
		{
			
			String status = req.getParameter("redirpageflag");
			String ActionName=req.getParameter("NoteText");
			String Remarks=req.getParameter("remarks");
			String minutesid = req.getParameter("minutesid");

			String aircraftid = req.getParameter("aircraftidedit");
			String subsystemid = req.getParameter("subsystemidedit");
			

//			if (!labcode.equalsIgnoreCase("PGAD") && (minutesid.equalsIgnoreCase("3") ||  minutesid.equalsIgnoreCase("5")) 
//					&& InputValidator.isContainsHTMLTags(req.getParameter("NoteText"))) {
//				redir.addAttribute("committeescheduleid", req.getParameter("scheduleid"));
//				redir.addAttribute("specname", req.getParameter("specname"));
//				redir.addAttribute("membertype",req.getParameter("membertype"));
//				redir.addAttribute("formname", req.getParameter("formname"));
//				redir.addAttribute("unit1",req.getParameter("unit1"));
//			    if(status!=null && status.equalsIgnoreCase("ROD"))	return  redirectWithError(redir,"RODScheduleMinutes.htm","HTML elements should not be entered !");
//			    return  redirectWithError(redir,"CommitteeScheduleMinutes.htm","HTML elements should not be entered !");
//			}

			if(InputValidator.isContainsHTMLTags(Remarks)) {
				redir.addAttribute("committeescheduleid", req.getParameter("scheduleid"));
				redir.addAttribute("specname", req.getParameter("specname"));
				redir.addAttribute("membertype",req.getParameter("membertype"));
				redir.addAttribute("formname", req.getParameter("formname"));
				redir.addAttribute("unit1",req.getParameter("unit1"));
				if(status!=null && status.equalsIgnoreCase("ROD"))	return  redirectWithError(redir,"RODScheduleMinutes.htm","HTML elements should not be entered !");
			    return  redirectWithError(redir,"CommitteeScheduleMinutes.htm","HTML elements should not be entered !");
			}


			redir.addFlashAttribute("committeescheduleid", req.getParameter("scheduleid"));
			redir.addFlashAttribute("specname", req.getParameter("specname"));
			redir.addFlashAttribute("membertype",req.getParameter("membertype"));
			redir.addFlashAttribute("formname", req.getParameter("formname"));
			redir.addFlashAttribute("unit1",req.getParameter("unit1"));
			
			

			CommitteeMinutesDetailsDto committeeminutesdetailsdto = new CommitteeMinutesDetailsDto();
			committeeminutesdetailsdto.setScheduleId(req.getParameter("scheduleid"));
			committeeminutesdetailsdto.setScheduleSubId(req.getParameter("schedulesubid"));
			committeeminutesdetailsdto.setMinutesId(req.getParameter("minutesid"));
			committeeminutesdetailsdto.setMinutesSubId(req.getParameter("scheduleagendaid"));
			committeeminutesdetailsdto.setMinutesSubOfSubId(req.getParameter("agendasubid"));
			committeeminutesdetailsdto.setMinutesUnitId(req.getParameter("minutesunitid"));
			committeeminutesdetailsdto.setStatusFlag(req.getParameter("statusflag"));
			committeeminutesdetailsdto.setDetails(ActionName);
			committeeminutesdetailsdto.setIDARCK(req.getParameter("darc"));
			committeeminutesdetailsdto.setCreatedBy(UserId);
			committeeminutesdetailsdto.setRemarks(Remarks);
			committeeminutesdetailsdto.setAgendaSubHead(req.getParameter("OutComeAirHead"));
			
			if(labcode.equalsIgnoreCase("ADE") && aircraftid!=null && !aircraftid.isEmpty() && !aircraftid.equalsIgnoreCase("0") ) {
				committeeminutesdetailsdto.setAircraftId(aircraftid);
			}
			if(labcode.equalsIgnoreCase("ADE") && subsystemid!=null  && !subsystemid.isEmpty() && !subsystemid.equalsIgnoreCase("0")) {
				committeeminutesdetailsdto.setSubSystemId(subsystemid);
			}
			
			long count = service.CommitteeMinutesInsert(committeeminutesdetailsdto);

			String SpecName = req.getParameter("specname");
			String CommitteeName= req.getParameter("committeename");
			String unit1 = req.getParameter("unit1");
			if(minutesid.equalsIgnoreCase("3") && unit1.length()<6) unit1+=req.getParameter("scheduleagendaid");

			if (count > 0) {
				redir.addFlashAttribute("result", CommitteeName + " Schedule Minutes (" + SpecName + ") Added Successfully");
				redir.addFlashAttribute("unit1",unit1);
				redir.addFlashAttribute("unit2",req.getParameter("unit2"));
				redir.addFlashAttribute("actionType","add");
				redir.addFlashAttribute("formname", req.getParameter("formname"));

			} else {
				redir.addAttribute("resultfail", " Schedule Minutes Add Unsuccessful");
			}

		
		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside CommitteeMinutesSubmit.htm "+ 
					UserId,e);
		}
		// Prudhvi - 06/03/2024
		/* ------------------ start ----------------------- */
		String redirpageflag = req.getParameter("redirpageflag");
		if(redirpageflag!=null && redirpageflag.equalsIgnoreCase("ROD")) {
			return"redirect:/RODScheduleMinutes.htm";
		}
		/* ------------------ end ----------------------- */

		// CCM Handling
		String ccmFlag = req.getParameter("ccmFlag");
		if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {
			redir.addAttribute("ccmScheduleId", req.getParameter("scheduleid"));
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

		return "redirect:/CommitteeScheduleMinutes.htm";
	}


	@RequestMapping(value = "CommitteeMinutesSpecAdd.htm", method = RequestMethod.GET)
	public @ResponseBody String CommitteeMinutesSpecAdd(HttpSession ses, HttpServletRequest req) throws Exception 
	{
		String UserId=(String)ses.getAttribute("Username");
		Object[] DisDesc = null;
		logger.info(new Date() +"Inside CommitteeMinutesSpecAdd.htm "+UserId);
		try
		{
			CommitteeMinutesDetailsDto minutesdetailsdto = new CommitteeMinutesDetailsDto();

			minutesdetailsdto.setMinutesId(req.getParameter("minutesid"));
			minutesdetailsdto.setMinutesSubId(req.getParameter("agendasubid"));
			minutesdetailsdto.setMinutesSubOfSubId(req.getParameter("scheduleagendaid"));


			DisDesc = service.CommitteeMinutesSpecDesc(minutesdetailsdto);
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside CommitteeMinutesSpecAdd.htm "+UserId,e);
		}
		Gson json = new Gson();
		return json.toJson(DisDesc); 
	}

	@RequestMapping(value = "CommitteeMinutesSpecEdit.htm", method = RequestMethod.GET)
	public @ResponseBody String CommitteeMinutesSpecEdit(HttpSession ses, HttpServletRequest req) throws Exception 
	{
		Object[] DisDesc = null;
		String UserId=(String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeMinutesSpecEdit.htm "+UserId);
		try
		{
			String Username = (String) ses.getAttribute("Username");
			CommitteeMinutesDetailsDto committeeminutesdetailsdto = new CommitteeMinutesDetailsDto();
			committeeminutesdetailsdto.setScheduleMinutesId(req.getParameter("scheduleminutesid"));
			if(labcode.equalsIgnoreCase("ADE")) DisDesc = service.CommitteeMinutesSpecEditForAde(committeeminutesdetailsdto);
			else DisDesc = service.CommitteeMinutesSpecEdit(committeeminutesdetailsdto);
		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside CommitteeMinutesSpecEdit.htm "+UserId,e);
		}
		Gson json = new Gson();
		return json.toJson(DisDesc);
	}

		@RequestMapping(value="CommitteeMinutesEditSubmit.htm", method=RequestMethod.POST)
		public String CommitteeMinutesEditSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
		{
			String UserId=(String)ses.getAttribute("Username");
			String labcode = (String)ses.getAttribute("labcode");
			logger.info(new Date() +"Inside CommitteeMinutesEditSubmit.htm "+UserId);
			try
			{
				String aircraftid = req.getParameter("aircraftidedit");
				String subsystemid = req.getParameter("subsystemidedit");
				String status = req.getParameter("redirpageflag");
				String minutesid = req.getParameter("minutesidedits");
//				if (!labcode.equalsIgnoreCase("PGAD") &&  (minutesid.equalsIgnoreCase("3") || minutesid.equalsIgnoreCase("5")) 
//						&& InputValidator.isContainsHTMLTags(req.getParameter("NoteText"))) {
//					redir.addAttribute("committeescheduleid", req.getParameter("scheduleid"));
//					redir.addAttribute("specname", req.getParameter("specname"));
//					redir.addAttribute("membertype",req.getParameter("membertype"));
//					redir.addAttribute("formname", req.getParameter("formname"));
//					redir.addAttribute("unit1",req.getParameter("unit1"));				
//					if(status!=null && status.equalsIgnoreCase("ROD"))	return  redirectWithError(redir,"RODScheduleMinutes.htm","HTML elements should not be entered !");
//					return  redirectWithError(redir,"CommitteeScheduleMinutes.htm","HTML elements should not be entered !");
//				}
				String ActionName=req.getParameter("NoteText");
				String Remarks=req.getParameter("remarks");
				
				if(InputValidator.isContainsHTMLTags(Remarks)) {
					redir.addAttribute("committeescheduleid", req.getParameter("scheduleid"));
					redir.addAttribute("specname", req.getParameter("specname"));
					redir.addAttribute("membertype",req.getParameter("membertype"));
					redir.addAttribute("formname", req.getParameter("formname"));
					redir.addAttribute("unit1",req.getParameter("unit1"));
					if(status!=null && status.equalsIgnoreCase("ROD"))	return  redirectWithError(redir,"RODScheduleMinutes.htm","HTML elements should not be entered !");
					return redirectWithError(redir, "CommitteeScheduleMinutes.htm", "'Remarks' should not contain HTML Tags.!");
				}
	
				CommitteeMinutesDetailsDto committeeminutesdetailsdto = new CommitteeMinutesDetailsDto();
				committeeminutesdetailsdto.setScheduleId(req.getParameter("scheduleidedit"));
				committeeminutesdetailsdto.setScheduleSubId(req.getParameter("schedulesubid"));
				committeeminutesdetailsdto.setMinutesId(req.getParameter("minutesidedits"));
				committeeminutesdetailsdto.setDetails(ActionName);
				committeeminutesdetailsdto.setIDARCK(req.getParameter("darc"));
				committeeminutesdetailsdto.setModifiedBy(UserId);
				committeeminutesdetailsdto.setScheduleMinutesId(req.getParameter("schedulminutesid"));
				committeeminutesdetailsdto.setRemarks(Remarks);
				
				if(labcode.equalsIgnoreCase("ADE") && aircraftid!=null && !aircraftid.isEmpty() && !aircraftid.equalsIgnoreCase("0") ) {
					committeeminutesdetailsdto.setAircraftId(aircraftid);
				}
				if(labcode.equalsIgnoreCase("ADE") && subsystemid!=null  && !subsystemid.isEmpty() && !subsystemid.equalsIgnoreCase("0")) {
					committeeminutesdetailsdto.setSubSystemId(subsystemid);
				}
				
					
				long count = service.CommitteeMinutesUpdate(committeeminutesdetailsdto);
	
				String SpecName = req.getParameter("specname");
				String CommitteeName= req.getParameter("committeename");
	
				if (count > 0) {
					redir.addFlashAttribute("result", CommitteeName + " Schedule Minutes (" + SpecName + ") Added Successfully");
					redir.addFlashAttribute("membertype",req.getParameter("membertype"));
				} else {
					redir.addFlashAttribute("resultfail", " Schedule Minutes Update Unsuccessful");
				}
	
				redir.addFlashAttribute("committeescheduleid", req.getParameter("scheduleid"));
				redir.addFlashAttribute("specname", req.getParameter("specname"));
				redir.addFlashAttribute("formname", req.getParameter("formname"));
				redir.addFlashAttribute("unit1",req.getParameter("unit1"));
				redir.addFlashAttribute("unit2",req.getParameter("unit2"));
				redir.addFlashAttribute("actionType","edit");
			}
			catch (Exception e) {
				e.printStackTrace(); logger.error(new Date() +"Inside CommitteeMinutesEditSubmit.htm "+UserId,e);
			}
			// Prudhvi - 06/03/2024
			/* ------------------ start ----------------------- */
			String redirpageflag = req.getParameter("redirpageflag");
			if(redirpageflag!=null && redirpageflag.equalsIgnoreCase("ROD")) {
				return"redirect:/RODScheduleMinutes.htm";
			}
			/* ------------------ end ----------------------- */
	
			// CCM Handling
			String ccmFlag = req.getParameter("ccmFlag");
			if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {
				redir.addAttribute("ccmScheduleId", req.getParameter("scheduleid"));
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
	
			redir.addAttribute("scheduleid", req.getParameter("scheduleid"));
	
			return "redirect:/CommitteeScheduleMinutes.htm";
	}

	@RequestMapping(value="CommitteeMinutesDeleteSubmit.htm", method=RequestMethod.POST)
	public String CommitteeMinutesDeleteSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception{


		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteeMinutesDeleteSubmit.htm "+UserId);
		try
		{

			CommitteeMinutesDetailsDto committeeminutesdetailsdto = new CommitteeMinutesDetailsDto();
			committeeminutesdetailsdto.setScheduleId(req.getParameter("scheduleidedit"));
			committeeminutesdetailsdto.setScheduleSubId(req.getParameter("schedulesubid"));
			committeeminutesdetailsdto.setMinutesId(req.getParameter("minutesidedits"));
			committeeminutesdetailsdto.setDetails(req.getParameter("NoteText"));
			committeeminutesdetailsdto.setIDARCK(req.getParameter("darc"));
			committeeminutesdetailsdto.setModifiedBy(UserId);
			committeeminutesdetailsdto.setScheduleMinutesId(req.getParameter("schedulminutesid"));
			committeeminutesdetailsdto.setRemarks(req.getParameter("remarks"));

			long count = service.CommitteeMinutesDelete(req.getParameter("schedulminutesid"));

			String SpecName = req.getParameter("specname");
			String CommitteeName= req.getParameter("committeename");

			if (count > 0) {
				redir.addFlashAttribute("result", CommitteeName + " Schedule Minutes (" + SpecName + ") Deleted Successfully");
				redir.addFlashAttribute("membertype",req.getParameter("membertype"));
				redir.addFlashAttribute("unit1",req.getParameter("unit1"));
				redir.addFlashAttribute("unit2",req.getParameter("unit2"));
				redir.addFlashAttribute("actionType","delete");

			} else {
				redir.addFlashAttribute("resultfail", " Schedule Minutes Delete Unsuccessful");
			}

			redir.addFlashAttribute("committeescheduleid", req.getParameter("scheduleid"));

		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside CommitteeMinutesDeleteSubmit.htm "+UserId,e);
		}
		// Prudhvi - 06/03/2024
		/* ------------------ start ----------------------- */
		String redirpageflag = req.getParameter("redirpageflag");
		if(redirpageflag!=null && redirpageflag.equalsIgnoreCase("ROD")) {
			return"redirect:/RODScheduleMinutes.htm";
		}
		/* ------------------ end ----------------------- */

		// CCM Handling
		String ccmFlag = req.getParameter("ccmFlag");
		if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {
			redir.addAttribute("ccmScheduleId", req.getParameter("scheduleid"));
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
		return "redirect:/CommitteeScheduleMinutes.htm";
	}



	@RequestMapping(value = "CommitteeSubScheduleSubmit.htm", method = RequestMethod.POST)
	public String CommitteeSubScheduleSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteeSubScheduleSubmit.htm "+UserId);
		try
		{

			String scheduleid=req.getParameter("scheduleid");

			CommitteeSubScheduleDto committeesubscheduledto= new CommitteeSubScheduleDto();
			committeesubscheduledto.setScheduleId(scheduleid);
			committeesubscheduledto.setScheduleDate(req.getParameter("subscheduledate"));
			committeesubscheduledto.setScheduleStartTime(req.getParameter("starttime"));
			committeesubscheduledto.setCreatedBy(UserId);
			long count=0;
			count=service.CommitteeSubScheduleSubmit(committeesubscheduledto);

			if (count > 0) {
				redir.addAttribute("result", " SubSchedule  Added Successfully");
			} else {
				redir.addAttribute("resultfail", "SubSchedule Adding Unsuccessful");
			}

			redir.addAttribute("scheduleid",scheduleid );
		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside CommitteeSubScheduleSubmit.htm "+UserId,e);
		}

		return "redirect:/CommitteeSubSchedule.htm";
	}


	@RequestMapping(value = "CommitteeSubSchedule.htm", method = RequestMethod.POST)
	public String CommitteeSubSchedule(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteeSubSchedule.htm "+UserId);
		try
		{
			req.setAttribute("scheduleid",req.getParameter("scheduleid") );

			req.setAttribute("subschedulelist", service.CommitteeSubScheduleList(req.getParameter("scheduleid")));
		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside CommitteeSubSchedule.htm "+UserId,e);
		}

		return "committee/CommitteeSubSchedule";
	}

	@RequestMapping(value="CommitteeInvitations.htm", method = {RequestMethod.POST,RequestMethod.GET})
	public String CommitteeInvitations(Model model, HttpServletRequest req, HttpServletResponse res, RedirectAttributes redir,HttpSession ses) throws Exception 
	{
		String UserId=(String)ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeInvitations.htm "+UserId);
		try
		{
			String committeescheduleid=req.getParameter("committeescheduleid");		
			String ccmFlag = req.getParameter("ccmFlag");		

			if (committeescheduleid == null) 
			{
				Map md = model.asMap();
				committeescheduleid = (String) md.get("committeescheduleid");
			}

			Object[] committeescheduledata =service.CommitteeScheduleData(committeescheduleid );

			String committeeid=committeescheduledata[7].toString();
			String projectid=committeescheduledata[11].toString();
			String divisionid=committeescheduledata[13].toString();
			String initiationid=committeescheduledata[14].toString();
			String carsInitiationId=committeescheduledata[16].toString();
			String programmeId=committeescheduledata[17].toString();
			
			List<Object[]> committeeinvitedlist = service.CommitteeAtendance(committeescheduleid,"0");
			// Prudhvi - 27/03/2024
			/* ------------------ start ----------------------- */
			req.setAttribute("industryPartnerList",  masterservice.getIndustryPartnerList());
			/* ------------------ end ----------------------- */	

			String committeemainid="0";
			if(Long.parseLong(initiationid)>0) {
				committeemainid=String.valueOf(service.LastCommitteeId(committeeid, projectid, divisionid, initiationid, carsInitiationId, programmeId));
			}else {
				committeemainid=String.valueOf(service.LastCommitteeId(committeeid, projectid, divisionid, "0", carsInitiationId, programmeId));
			}
			
			List<Object[]> repInvitationList = service.getCommitteeRepIsActiveList(committeescheduleid);

			List<Object[]> EmployeeList = service.EmployeeListNoInvitedMembers(committeescheduleid,LabCode);
			List<Object[]> ExpertList = service.ExternalMembersNotInvited(committeescheduleid);
			req.setAttribute("designationlist", adminservice.DesignationList());
			req.setAttribute("committeereplist", service.CommitteeMemberRepList(committeescheduledata[1].toString()));
			req.setAttribute("committeeinvitedlist", committeeinvitedlist);
			req.setAttribute("repInvitationList", repInvitationList);
			req.setAttribute("EmployeeList", EmployeeList);
			req.setAttribute("ExpertList", ExpertList);
			req.setAttribute("committeescheduleid", committeescheduleid);
			req.setAttribute("committeemainid",committeemainid);
			req.setAttribute("committeeId", committeeid);
			req.setAttribute("committeescheduledata",committeescheduledata);
			if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {
				req.setAttribute("agendaList", ccmservice.getCCMScheduleAgendaListByCCMScheduleId(committeescheduleid));
				req.setAttribute("ccmFlag", ccmFlag);
			}else {
				req.setAttribute("agendalist",service.AgendaList(committeescheduleid) );
			}
			req.setAttribute("clusterlablist", service.AllLabList());
			req.setAttribute("labid", service.LabDetails(committeescheduledata[15].toString())[13].toString());
			
			req.setAttribute("committeeallmemberlist",service.CommitteeAllMembersList(committeemainid) );
			if(committeeinvitedlist.size()==0) 
			{	

//				req.setAttribute("committeemainid",committeemainid);
//				req.setAttribute("committeeallmemberlist",service.CommitteeAllMembersList(committeemainid) );
//				req.setAttribute("committeescheduleid", committeescheduleid);
//				req.setAttribute("committeescheduledata",committeescheduledata );
//				req.setAttribute("committeeId", committeeid);
//				if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {
//					req.setAttribute("agendaList", ccmservice.getCCMScheduleAgendaListByCCMScheduleId(committeescheduleid));
//					req.setAttribute("ccmFlag", ccmFlag);
//				}else {
//					req.setAttribute("agendalist",service.AgendaList(committeescheduleid));
//				}
//				req.setAttribute("labid", service.LabDetails(committeescheduledata[15].toString())[13].toString());
				return "committee/CommitteeInvitation";
			}
			else
			{
				return "committee/ViewCommitteeInvitation";
			}



		}
		catch (Exception e) 
		{			
			e.printStackTrace();
			logger.error(new Date() +"Inside CommitteeInvitations.htm "+UserId,e);
			return "static/Error";
		}

	}


	@RequestMapping(value = "CommitteeInvitationCreate.htm", method = RequestMethod.POST)
	public String CommitteeInvitationCreate(HttpServletRequest req, HttpServletResponse res, RedirectAttributes redir,
			HttpSession ses) throws Exception {

		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteeInvitationCreate.htm "+UserId);
		try
		{
			String committeescheduleid = req.getParameter("committeescheduleid");
			String Committeeidmainid = req.getParameter("Committeeidmainid");
			String reptype=req.getParameter("rep");
			ArrayList<String> emplist = new ArrayList<String>();
			ArrayList<String> labCodelist = new ArrayList<String>();
			if(req.getParameter("chairperson") != null ) 
			{
				String empids[] =req.getParameterValues("empid");
				String Labcode[] =req.getParameterValues("Labcode");
				emplist = new ArrayList<String>(Arrays.asList(empids));
				labCodelist = new ArrayList<String>(Arrays.asList(Labcode));		
				service.UpdateComitteeMainid(Committeeidmainid, committeescheduleid);
			}
			else 
			{

				if(req.getParameterValues("internalmember")!=null && req.getParameterValues("internalmember").length!=0) {
					String members[] =req.getParameterValues("internalmember");
					String labcode =req.getParameter("internallabcode");
					emplist.addAll(Arrays.asList(members));
					System.out.println("emplist"+emplist);

					for(String member : members )
					{
						labCodelist.add(labcode);
					}
				}

				if(req.getParameterValues("externalmember")!=null && req.getParameterValues("externalmember").length!=0) {
					String members[] =req.getParameterValues("externalmember");
					String labid =req.getParameter("externallabid");
					emplist.addAll(Arrays.asList(members));
					for(String member : members )
					{
						labCodelist.add(labid);
					}
				}
				if(req.getParameterValues("expertmember")!=null && req.getParameterValues("expertmember").length!=0) {
					String members[] =req.getParameterValues("expertmember");
					String labid =req.getParameter("expertlabid");
					emplist.addAll(Arrays.asList(members));
					for(String member : members )
					{
						labCodelist.add(labid);
					}
				}

				// Prudhvi - 27/03/2024
				/* ------------------ start ----------------------- */

				if(req.getParameterValues("industryPartnerRep")!=null && req.getParameterValues("industryPartnerRep").length!=0) {
					String members[] =req.getParameterValues("industryPartnerRep");
					String labid =req.getParameter("industrypartnerlabid");
					emplist.addAll(Arrays.asList(members));
					for(String member : members )
					{
						labCodelist.add(labid);
					}
				}

				/* ------------------ end ----------------------- */
			}

			if(req.getParameterValues("SpecialMember")!=null && req.getParameter("SpecialMember").length()!=0) {
				String members[] =req.getParameterValues("SpecialMember");
				System.out.println(Arrays.asList(members).toString()+" memberList");
				emplist.addAll(Arrays.asList(members));
				String []labs=req.getParameterValues("specialLabCode");
				System.out.println(Arrays.asList(labs).toString()+" labs");
				labCodelist.addAll(Arrays.asList(labs));
			}

			CommitteeInvitationDto committeeinvitationdto = new CommitteeInvitationDto();

			committeeinvitationdto.setCommitteeScheduleId(committeescheduleid);
			committeeinvitationdto.setCreatedBy(UserId);
			committeeinvitationdto.setEmpIdList(emplist);
			committeeinvitationdto.setLabCodeList(labCodelist);
			committeeinvitationdto.setReptype(reptype);
			committeeinvitationdto.setInviteFlag(req.getParameter("inviteFlag"));
			committeeinvitationdto.setRevisionNo(0L);

			long count =  service.CommitteeInvitationCreate(committeeinvitationdto);

			if (count > 0) {				
				redir.addAttribute("result", " Member(s) Invited Successfully");
			} else {
				redir.addAttribute("resultfail", " Member(s) Invite Unsuccessful");
			}

			redir.addAttribute("committeescheduleid", committeescheduleid);
		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside CommitteeInvitationCreate.htm "+UserId,e);
		}
		// Prudhvi - 05/03/2024
		/* ------------------ start ----------------------- */
		String redirpageflag = req.getParameter("redirpageflag");
		if(redirpageflag!=null && redirpageflag.equalsIgnoreCase("ROD")) {
			return"redirect:/RODInvitations.htm";
		}
		/* ------------------ end ----------------------- */

		redir.addAttribute("ccmFlag", req.getParameter("ccmFlag"));
		return "redirect:/CommitteeInvitations.htm";
	}

	@RequestMapping(value="CommitteeMinutesViewAll.htm", method = {RequestMethod.POST,RequestMethod.GET})
	public String CommitteeMinutesViewAll(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeMinutesViewAll.htm "+UserId);
		try
		{		
			String committeescheduleid = req.getParameter("committeescheduleid");			
			Object[] scheduleeditdata=service.CommitteeScheduleEditData(committeescheduleid);
			String projectid= scheduleeditdata[9].toString();
			if(projectid!=null && Integer.parseInt(projectid)>0)

			{
				req.setAttribute("projectdetails", service.projectdetails(projectid));
			}
			String divisionid= scheduleeditdata[16].toString();
			if(divisionid!=null && Integer.parseInt(divisionid)>0)
			{
				req.setAttribute("divisiondetails", service.DivisionData(divisionid));
			}
			String initiationid= scheduleeditdata[17].toString();
			if(initiationid!=null && Integer.parseInt(initiationid)>0)
			{
				req.setAttribute("initiationdetails", service.Initiationdetails(initiationid));
			}
			List<Object[]> actionlist= service.MinutesViewAllActionList(committeescheduleid);
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

			req.setAttribute("committeeminutesspeclist",service.CommitteeScheduleMinutes(committeescheduleid) );
			req.setAttribute("committeescheduleeditdata", scheduleeditdata);
			req.setAttribute("CommitteeAgendaList", service.AgendaList(committeescheduleid));
			req.setAttribute("committeeminutes",service.CommitteeMinutesSpecdetails());
			req.setAttribute("committeeminutessub",service.CommitteeMinutesSub());
			req.setAttribute("committeeinvitedlist", service.CommitteeAtendance(committeescheduleid,"1"));			
			req.setAttribute("actionlist",actionsdata);
			req.setAttribute("labdetails", service.LabDetails(scheduleeditdata[24].toString()));
			req.setAttribute("isprint", "N");	    
			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(scheduleeditdata[24].toString()));
			req.setAttribute("meetingcount",service.MeetingNo(scheduleeditdata));
			req.setAttribute("labInfo", service.LabDetailes(LabCode));
		}
		catch (Exception e) 
		{
			e.printStackTrace(); 
			logger.error(new Date() +"Inside CommitteeMinutesViewAll.htm "+UserId,e);
		}				
		return "committee/CommitteeMinutesViewAll";
	}

	@RequestMapping(value="MeetingAgendaApproval.htm", method=RequestMethod.POST)
	public String MeetingAgendaApproval(HttpServletRequest req, HttpSession ses,RedirectAttributes redir) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside MeetingAgendaApproval.htm "+UserId);
		String Option = req.getParameter("sub");
		try
		{		
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();			
			String CommitteeScheduleId=req.getParameter("committeescheduleid");			
			int count = service.MeetingAgendaApproval(CommitteeScheduleId, UserId, EmpId,Option);	
			if (count > 0) {
				redir.addAttribute("result", "Meeting Agenda Forwarded Successfully");
			} else {
				redir.addAttribute("resultfail", "Meeting Agenda Forward Unsuccessful");
			}

			String ccmFlag = req.getParameter("ccmFlag");
			if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {
				redir.addAttribute("ccmScheduleId", CommitteeScheduleId);
				redir.addAttribute("committeeMainId", req.getParameter("committeeMainId"));
				redir.addAttribute("committeeId", req.getParameter("committeeId"));
				return "redirect:/CCMSchedule.htm";
			}else {
				redir.addAttribute("scheduleid", CommitteeScheduleId);
				return "redirect:/CommitteeScheduleView.htm";
			}

		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside MeetingAgendaApproval.htm "+UserId,e);
			return "static/Error";
		}		
	}

	@RequestMapping(value = "MeetingApprovalAgenda.htm")
	public String MeetingApprovalAgenda(HttpServletRequest req, RedirectAttributes redir, HttpSession ses)	throws Exception 
	{
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside MeetingApprovalAgenda.htm "+UserId);
		try
		{

			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();

			req.setAttribute("MeetingApprovalAgList", service.MeetingApprovalAgendaList(EmpId));
			req.setAttribute("MeetingApprovalMinutes", service.MeetingApprovalMinutesList(EmpId));

		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside MeetingApprovalAgenda.htm "+UserId,e);
		}

		return "committee/ComMeetingApprovalAgList";
	}

	@RequestMapping(value = "MeetingApprovalAgendaDetails.htm", method = {RequestMethod.POST, RequestMethod.GET})
	public String MeetingApprovalAgendaDetails(HttpServletRequest req, RedirectAttributes redir, HttpSession ses)	throws Exception {

		String UserId=(String)ses.getAttribute("Username");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside MeetingApprovalAgendaDetails.htm "+UserId);
		try
		{		
			String ScheduleId=req.getParameter("scheduleid");
			Object[] ScheduletData =service.CommitteeScheduleEditData(ScheduleId);

			String committeename = req.getParameter("committeename");
			req.setAttribute("empscheduledata", service.EmpScheduleData(EmpId, ScheduleId));			
			req.setAttribute("committeename", committeename);		
			if(committeename!=null && committeename.equalsIgnoreCase("CCM")) {
				req.setAttribute("ccmScheduleData", ccmservice.getCCMScheduleById(ScheduleId));
				req.setAttribute("agendaList",  ccmservice.getCCMScheduleAgendaListByCCMScheduleId(ScheduleId));
			}else {
				req.setAttribute("agendalist",  service.AgendaList(ScheduleId));
			}

			req.setAttribute("scheduledata",ScheduletData);			
			req.setAttribute("committeedata", service.CommitteeMainList(labcode) );			
			req.setAttribute("invitedlist",service.CommitteeAtendance(ScheduleId,"0"));
			req.setAttribute("AgendaDocList",service.AgendaLinkedDocList(ScheduleId));
		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside MeetingApprovalAgendaDetails.htm "+UserId,e);
		}

		return "committee/ComMeetingApprovalAgDetails";
	}

	@RequestMapping(value="MeetingAgendaApprovalSubmit.htm", method=RequestMethod.POST)
	public String MeetingAgendaApprovalSubmit(HttpServletRequest req,RedirectAttributes redir, HttpSession ses) throws Exception{

		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside MeetingAgendaApprovalSubmit.htm "+UserId);
		try
		{
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();

			String ScheduleId=req.getParameter("scheduleid");
			String Option=req.getParameter("sub");
			String Remarks=req.getParameter("Remark");

			if(InputValidator.isContainsHTMLTags(Remarks)) {
				
				return redirectWithError(redir, "MeetingApprovalAgenda.htm", "'Remarks' should not contain HTML Tags.!");


			}

			if(Option.equalsIgnoreCase("back")) {

				return "redirect:/MeetingApprovalAgenda.htm";
			}
			else {

				int count = service.MeetingAgendaApprovalSubmit(ScheduleId, Remarks, UserId, EmpId,Option);

				if (count > 0) {

					if(Option.equalsIgnoreCase("approve")) {

						redir.addAttribute("result", "Meeting Approved Successfully");
					}

					if(Option.equalsIgnoreCase("return")) {

						redir.addAttribute("result", "Meeting Returned Successfully");
					}

				} 

				else {

					redir.addAttribute("resultfail", "Meeting Forward Unsuccessful");
				}

			}
		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside MeetingAgendaApprovalSubmit.htm "+UserId,e);
		}

		return "redirect:/MeetingApprovalAgenda.htm";
	}




	@RequestMapping(value = "CommitteeInvitationDelete.htm", method = RequestMethod.POST)
	public String CommitteeInvitationDelete(HttpServletRequest req, HttpServletResponse res, RedirectAttributes redir,
			HttpSession ses) throws Exception {
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteeInvitationDelete.htm "+UserId);
		try
		{


			String committeeinvitationid = req.getParameter("committeeinvitationid");
			String committeescheduleid = req.getParameter("committeescheduleid");

			long count = 0;
			count = service.CommitteeInvitationDelete(committeeinvitationid);

			if (count > 0) {
				redir.addAttribute("result", "Member Removed Successfully");
			} else {
				redir.addAttribute("resultfail", "Member Removal Unsuccessful");
			}

			redir.addAttribute("committeescheduleid", committeescheduleid);
		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside CommitteeInvitationDelete.htm "+UserId,e);
		}
		// Prudhvi - 05/03/2024
		/* ------------------ start ----------------------- */
		String redirpageflag = req.getParameter("redirpageflag");
		if(redirpageflag!=null && redirpageflag.equalsIgnoreCase("ROD")) {
			return"redirect:/RODInvitations.htm";
		}
		/* ------------------ end ----------------------- */
		return "redirect:/CommitteeInvitations.htm";
	}

	@RequestMapping(value = "CommitteeAttendance.htm", method = {RequestMethod.POST,RequestMethod.GET})
	public String CommitteeAttendance(Model model, HttpServletResponse res, HttpServletRequest req, HttpSession ses,
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

			Object[] committeescheduledata =service.CommitteeScheduleData(committeescheduleid);

			List<Object[]> committeeinvitedlist = service.CommitteeAtendance(committeescheduleid,"1");
			List<Object[]> EmployeeList = service.EmployeeListNoInvitedMembers(committeescheduleid,LabCode);
			List<Object[]> ExpertList = service.ExternalMembersNotInvited(committeescheduleid);

			req.setAttribute("committeereplist", service.CommitteeMemberRepList(committeescheduledata[1].toString()));
			req.setAttribute("EmployeeList", EmployeeList);
			req.setAttribute("ExpertList", ExpertList);
			req.setAttribute("committeeinvitedlist", committeeinvitedlist);
			req.setAttribute("committeescheduleid", committeescheduleid);
			req.setAttribute("committeescheduledata",committeescheduledata );
			req.setAttribute("LabCode", LabCode);
			req.setAttribute("clusterlablist", service.AllLabList());
			req.setAttribute("ccmFlag", req.getParameter("ccmFlag"));
			req.setAttribute("committeeMainId", req.getParameter("committeeMainId"));
			req.setAttribute("committeeId", req.getParameter("committeeId"));
		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside CommitteeAttendance.htm "+UserId,e);
		}		
		return "committee/CommitteeAttendance";
	}

	@RequestMapping(value = "CommitteeAttendanceSubmit.htm", method = RequestMethod.POST)
	public String CommitteeAttendanceSubmit(HttpServletRequest req, HttpServletResponse res, RedirectAttributes redir,
			HttpSession ses) throws Exception {
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteeAttendanceSubmit.htm "+UserId);
		try
		{
			String committeescheduleid = req.getParameter("committeescheduleid");
			String Committeeidmainid = req.getParameter("Committeeidmainid");

			String InternalMember[] = req.getParameterValues("internalmember");
			String ExternalMember[] = req.getParameterValues("externalmember");

			String LabId[] =req.getParameterValues("LabId");
			String LabId1[] =req.getParameterValues("LabId1");
			String InternalLabId[] =req.getParameterValues("InternalLabId");
			String ExternalMemberLab[] =req.getParameterValues("ExternalMemberLab");
			String reptype=req.getParameter("rep");


			if (InternalMember != null) {
				Set<String> s1 = new HashSet<String>(Arrays.asList(InternalMember));
				if (s1.size() < InternalMember.length) {

					redir.addAttribute("resultfail","Cannot Invite Same Members Multiple times, Check Internal Members Selection");
					redir.addAttribute("committeescheduleid", committeescheduleid);
					return "redirect:/CommitteeInvitations.htm";
				}
			}

			if (ExternalMember != null) {
				Set<String> s2 = new HashSet<String>(Arrays.asList(ExternalMember));
				if (s2.size() < ExternalMember.length) {

					redir.addAttribute("resultfail",
							"Cannot Invite Same Members Multiple times, Check External Members Selection");
					redir.addAttribute("committeescheduleid", committeescheduleid);
					return "redirect:/CommitteeInvitations.htm";
				}
			}

			ArrayList<String> emplist = new ArrayList<String>();
			ArrayList<String> lablist = new ArrayList<String>();

			if (req.getParameter("chairperson") != null) 
			{	

				String Member[] = req.getParameterValues("member");				
				String externalmemberlab[]=req.getParameterValues("externalmemberlab");
				String expertmember[]=req.getParameterValues("expertmember");

				String internallabid1[] =req.getParameterValues("internallabid");
				String expertlabid[]=req.getParameterValues("expertlabid");
				String externallabid[] =req.getParameterValues("externallabid");

				emplist.add(req.getParameter("chairperson")+",CC");	

				emplist.add(req.getParameter("secretary")+",CS");

				if(Long.parseLong(req.getParameter("proxysecretary"))>0)
				{// proxy secretary
					emplist.add(req.getParameter("proxysecretary")+",PS");
				}
				if(Member!=null && Member.length>0) 
				{
					emplist.addAll(Arrays.asList(Member));
				}				
				lablist.addAll(Arrays.asList(internallabid1));

				if(externalmemberlab!=null && externalmemberlab.length>0)
				{
					emplist.addAll(Arrays.asList(externalmemberlab));
					lablist.addAll(Arrays.asList(externallabid));
				}

				if(expertmember!=null && expertmember.length>0)
				{
					emplist.addAll(Arrays.asList(expertmember));
					lablist.addAll(Arrays.asList(expertlabid));
				}			
			}else {



				if (InternalMember != null) {

					for(int i=0;i<InternalMember.length;i++) {
						lablist.addAll(Arrays.asList(InternalLabId));
					}

					emplist.addAll(Arrays.asList(InternalMember));
				}
				if (ExternalMember != null) {

					for(int i=0;i<ExternalMember.length;i++) {
						lablist.addAll(Arrays.asList(LabId1));
					}

					emplist.addAll(Arrays.asList(ExternalMember));
				}

				if (ExternalMemberLab != null) {

					for(int i=0;i<ExternalMemberLab.length;i++) {
						lablist.addAll(Arrays.asList(LabId));
					}

					emplist.addAll(Arrays.asList(ExternalMemberLab));

				}
			}

			CommitteeInvitationDto committeeinvitationdto = new CommitteeInvitationDto();

			committeeinvitationdto.setCommitteeScheduleId(committeescheduleid);
			committeeinvitationdto.setCreatedBy(UserId);
			committeeinvitationdto.setEmpIdList(emplist);
			//committeeinvitationdto.setCommitteeScheduleId(req.getParameter("scheduleid"));
			committeeinvitationdto.setLabCodeList(lablist);
			committeeinvitationdto.setReptype(reptype);
			committeeinvitationdto.setRevisionNo(0L);

			if(req.getParameter("chairperson") != null )
			{
				service.UpdateComitteeMainid(Committeeidmainid, committeescheduleid);
			}
			long count =  service.CommitteeInvitationCreate(committeeinvitationdto);

			if (count > 0) {				

				redir.addAttribute("result", " Member(s) Invited Successfully");
			} else {
				redir.addAttribute("resultfail", " Member(s) Invite Unsuccessful");
			}

			redir.addAttribute("committeescheduleid", committeescheduleid);

		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside CommitteeAttendanceSubmit.htm "+UserId,e);
		}
		return "redirect:/CommitteeAttendance.htm";
	}


	@RequestMapping(value = "CommitteeAttendanceToggle.htm", method = RequestMethod.GET)
	public @ResponseBody String AttendanceToggle(HttpSession ses, HttpServletRequest req, RedirectAttributes redir)
			throws Exception 
	{
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteeAttendanceToggle.htm "+UserId);
		try
		{
			String Invitationid = req.getParameter("invitationid");
			service.CommitteeAttendanceToggle(Invitationid);
		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside CommitteeAttendanceToggle.htm "+UserId,e);
		}

		return "committee/CommitteeAttendance";
	}




	@RequestMapping(value="ProjectBasedSchedule.htm")
	public String ProjectBasedSchedule(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{		

		String UserId=(String)ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside ProjectBasedSchedule.htm "+UserId);
		try {
			String Logintype= (String)ses.getAttribute("LoginType");
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String projectid=req.getParameter("projectid");
			String committeeid=req.getParameter("committeeid");

			List<Object[]> projectdetailslist=service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
		
			if(projectdetailslist.size()==0) 
			{				
				redir.addAttribute("resultfail", "No Project is Assigned to you.");
				return "redirect:/MainDashBoard.htm";
			}

			if(committeeid==null)
			{
				committeeid="all";
			}			

			if(projectdetailslist.size()==0) {

				redir.addAttribute("resultfail", "No Project is Assigned to you.");

				return "redirect:/MainDashBoard.htm";
			}
			if(projectid==null || projectid.equals("null"))
			{
				projectid=projectdetailslist.get(0)[0].toString();
			}
			if(committeeid==null || committeeid.equals("all"))
			{				
				req.setAttribute("Projectschedulelist", service.ProjectScheduleListAll(projectid));
			} 
			else 
			{
				req.setAttribute("committeedetails",service.CommitteeDetails(committeeid));
				req.setAttribute("Projectschedulelist",service.ProjectCommitteeScheduleListAll(projectid, committeeid));				
			}

			List<Object[]> projapplicommitteelist=service.ProjectApplicableCommitteeList(projectid);

			
			System.out.println(committeeid);
		
			System.out.println(projectid);
			
			String committeemainid=String.valueOf(service.LastCommitteeId(committeeid, projectid, "0", "0", "0", "0"));

			req.setAttribute("projectid",projectid);
			req.setAttribute("committeeid",committeeid);
			req.setAttribute("committeemainid",committeemainid);
			req.setAttribute("Projectdetails",projectdetailslist.get(0));
			req.setAttribute("ProjectsList",projectdetailslist);
			req.setAttribute("projapplicommitteelist",projapplicommitteelist);
		}			
		catch (Exception e) 
		{
			e.printStackTrace(); 
			logger.error(new Date() +"Inside ProjectBasedSchedule.htm "+UserId,e);
		}
		return "committee/CommitteeProjectSchedule";
	}




	@RequestMapping(value="KickOffMeeting.htm",method=RequestMethod.POST)
	public String KickOffMeeting(HttpServletRequest req,HttpSession ses,RedirectAttributes redir, HttpServletResponse res) throws Exception{

		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside KickOffMeeting.htm "+UserId);
		String CommitteeScheduleId=req.getParameter("committeescheduleid");

		try {

			//String ret = pt.freezeBriefingPaper(req, res, ses, redir);
			Object[] obj= service.KickOffMeeting(req, redir);
			req=(HttpServletRequest)obj[0];
			redir=(RedirectAttributes)obj[1];

			//		    if(ret.equalsIgnoreCase("static/Error"))
			//		    {
			//		    	redir.addAttribute("resultfail", "Briefing Paper Freezing Failed !!");
			//		    }

		}catch (Exception e) {			
			e.printStackTrace(); 
			logger.error(new Date() +"Inside KickOffMeeting.htm "+UserId,e);


		}
		String ccmFlag = req.getParameter("ccmFlag");
		if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {
			redir.addAttribute("ccmScheduleId", CommitteeScheduleId);
			redir.addAttribute("committeeMainId", req.getParameter("committeeMainId"));
			redir.addAttribute("committeeId", req.getParameter("committeeId"));
			return "redirect:/CCMSchedule.htm";
		}else {
			redir.addAttribute("scheduleid", CommitteeScheduleId);
			return "redirect:/CommitteeScheduleView.htm";
		} 
	}


	@RequestMapping(value="MySchedules.htm")
	public String MySchedules(HttpServletRequest req,RedirectAttributes redir,HttpSession ses) throws Exception{
		String UserId = (String) ses.getAttribute("Username");

		logger.info(new Date() +"Inside MySchedules.htm "+UserId);
		try
		{

			String EmpId= ((Long) ses.getAttribute("EmpId")).toString();

			req.setAttribute("userschedulelist", service.UserSchedulesList(EmpId,"%%"));
		}catch(Exception e) {	    		
			logger.error(new Date() +" Inside MySchedules.htm "+UserId, e);
			e.printStackTrace();
		}	
		return "committee/UserSchedules";

	}

	@RequestMapping(value="CommitteeUserScheduleView.htm",method=RequestMethod.GET)
	public String CommitteeUserScheduleView(HttpServletRequest req,RedirectAttributes redir,HttpSession ses, HttpServletResponse res) throws Exception{

		String UserId = (String) ses.getAttribute("Username");

		logger.info(new Date() +"Inside CommitteeUserScheduleView.htm "+UserId);
		try
		{
			String EmpId= ((Long) ses.getAttribute("EmpId")).toString();
			String ScheduleId=req.getParameter("scheduleid");

			Object[] MemberType=service.UserSchedulesList(EmpId,"%%").get(0);

			redir.addAttribute("scheduleid", ScheduleId);
			redir.addAttribute("userview", MemberType[4].toString());
			redir.addAttribute("membertype",req.getParameter("membertype"));

		}catch(Exception e) {	    		
			logger.error(new Date() +" Inside CommitteeUserScheduleView.htm "+UserId, e);
			e.printStackTrace();	
		}	

		return "redirect:/CommitteeScheduleView.htm";

	}


	@RequestMapping(value="CommitteeMinutesViewAllDownload.htm")
	public void CommitteeMinutesViewAllDownload(HttpServletRequest req, HttpSession ses, RedirectAttributes redir,HttpServletResponse res) throws Exception
	{

		String UserId=(String)ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeMinutesViewAllDownload.htm "+UserId);
		try
		{
			String committeescheduleid = req.getParameter("committeescheduleid");			
			Object[] scheduleeditdata=service.CommitteeScheduleEditData(committeescheduleid);
			String projectid= scheduleeditdata[9].toString();

			if(projectid!=null && Integer.parseInt(projectid)>0)

			{
				req.setAttribute("projectdetails", service.projectdetails(projectid));
			}
			String divisionid= scheduleeditdata[16].toString();
			if(divisionid!=null && Integer.parseInt(divisionid)>0)
			{
				req.setAttribute("divisiondetails", service.DivisionData(divisionid));
			}
			String initiationid= scheduleeditdata[17].toString();
			if(initiationid!=null && Integer.parseInt(initiationid)>0)
			{
				req.setAttribute("initiationdetails", service.Initiationdetails(initiationid));
			}
			String carsInitiationId= scheduleeditdata[25].toString();
			if(Long.parseLong(carsInitiationId)>0)
			{
				req.setAttribute("carsInitiationDetails", carsservice.getCARSInitiationById(Long.parseLong(carsInitiationId)));
			}
			String programmeId= scheduleeditdata[26].toString();
			if(Long.parseLong(programmeId)>0)
			{
				req.setAttribute("prgmMasterDetails", service.getProgrammeMasterById(programmeId));
			}
			List<String>list = Arrays.asList("A","I","K");
			
			
			List<Object[]> actionlist= service.MinutesViewAllActionList(committeescheduleid);
			actionlist=actionlist.stream().filter(e->list.contains(e[2].toString())).collect(Collectors.toList());
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

			req.setAttribute("committeeminutesspeclist",service.CommitteeScheduleMinutes(committeescheduleid) );
			req.setAttribute("committeescheduleeditdata", scheduleeditdata);
			req.setAttribute("CommitteeAgendaList", service.AgendaList(committeescheduleid));
			req.setAttribute("committeeminutes",service.CommitteeMinutesSpecdetails());
			req.setAttribute("committeeminutessub",service.CommitteeMinutesSub());
			req.setAttribute("committeeinvitedlist", service.CommitteeAtendance(committeescheduleid,"1"));

			req.setAttribute("actionlist",  actionsdata);
			req.setAttribute("labdetails", service.LabDetails(scheduleeditdata[24].toString()));
			req.setAttribute("isprint", "Y");	
			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(scheduleeditdata[24].toString()));
			req.setAttribute("labInfo", service.LabDetailes(LabCode));
			String committeeId=scheduleeditdata[0].toString();
			String scheduledate=scheduleeditdata[2].toString();


			List<Object[]>ActionDetails=service.actionDetailsForNonProject(committeeId,scheduledate);
			List<Object[]>actionSubDetails=new ArrayList();
			if(ActionDetails.size()>0) {
				actionSubDetails=ActionDetails.stream().filter(i -> LocalDate.parse(i[9].toString()).isBefore(LocalDate.parse(scheduledate))).collect(Collectors.toList());
				if(actionSubDetails.size()>0) {
					actionSubDetails=actionSubDetails.stream().filter(i -> i[15].toString().equalsIgnoreCase(projectid) && i[16].toString().equalsIgnoreCase(initiationid) 
																		&& i[17].toString().equalsIgnoreCase(divisionid) && i[18].toString().equalsIgnoreCase(carsInitiationId)
																		&& i[19].toString().equalsIgnoreCase(programmeId))
																		.collect(Collectors.toList());
				}
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
			req.setAttribute("flagforView", "M");
			req.setAttribute("meetingcount",service.MeetingNo(scheduleeditdata));

			String filename=scheduleeditdata[11].toString().replace("/", "-");


			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);

			req.setAttribute("ccmFlag", req.getParameter("ccmFlag"));			

			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/committee/CommitteeMinutesViewAll.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"));

			req.setAttribute("tableactionlist",  actionsdata);
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
			logger.error(new Date() +" Inside CommitteeMinutesViewAllDownload.htm "+UserId, e);
			e.printStackTrace();
		}	
	}


	@RequestMapping(value="ProjectMaster.htm")
	public String ProjectCommitteeMaster( Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside ProjectMaster.htm "+UserId);
		try {
			String LoginType=(String)ses.getAttribute("LoginType");
			String projectid=req.getParameter("projectid");
			if(projectid==null)  {
				Map md=model.asMap();
				projectid=(String)md.get("projectid");
			}
			String loginid= ses.getAttribute("LoginId").toString();
			List<Object[]> projectdetailslist=null;

			projectdetailslist=service.LoginProjectDetailsList(EmpId,LoginType,LabCode);


			if(projectdetailslist.size()==0) {				
				redir.addAttribute("resultfail", "No Project is Assigned to you.");
				return "redirect:/MainDashBoard.htm";
			}
			if(projectid==null || projectid.equals("null"))
			{
				projectid=projectdetailslist.get(0)[0].toString();
			}
			List<Object[]> projectmasterlist=service.ProjectMasterList(projectid);
			req.setAttribute("ProjectCommitteeFormationCheckList", service.ProjectCommitteeFormationCheckList(projectid));
			req.setAttribute("projectid",projectid);
			req.setAttribute("initiationid","0");	
			req.setAttribute("divisionid","0");	
			req.setAttribute("Projectdetails",projectdetailslist.get(0));
			req.setAttribute("ProjectsList",projectdetailslist);
			req.setAttribute("projectmasterlist",projectmasterlist);
			req.setAttribute("committeelist", service.ProjectCommitteesListNotAdded(projectid, LabCode));
		}
		catch (Exception e) 
		{
			e.printStackTrace(); 
			logger.error(new Date() +"Inside ProjectMaster.htm "+UserId,e);
		}
		return "committee/CommitteeProjectMaster";
	}

	@RequestMapping(value="ProjectCommitteeAdd.htm",method = RequestMethod.POST)
	public String ProjectCommitteeAdd(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception{

		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside ProjectCommitteeAdd.htm "+UserId);
		try {

			String ProjectId=req.getParameter("projectid");
			String[] Committee=req.getParameterValues("committeeid");

			long count=service.ProjectCommitteeAdd(ProjectId,Committee,UserId);

			if (count > 0) {
				redir.addAttribute("result", " Committee Added Successfully");
				redir.addAttribute("projectid",ProjectId);
			} else {
				redir.addAttribute("result", " Committee Add Unsucessful");
			}

		}

		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside ProjectCommitteeAdd.htm "+UserId,e);
		}

		return "redirect:/ProjectMaster.htm";
	}


	@RequestMapping(value="ProjectCommitteeDelete.htm",method = RequestMethod.POST)
	public String ProjectCommitteeDelete(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception{

		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside ProjectCommitteeDelete.htm "+UserId);
		try {
			String ProjectId=req.getParameter("projectid");
			if(req.getParameter("sub")!=null) {

				String Option=req.getParameter("sub");

				if(!Option.equalsIgnoreCase("remove")) 
				{
					redir.addFlashAttribute("initiationid","0");
					redir.addFlashAttribute("projectid",ProjectId);
					redir.addFlashAttribute("divisionid","0");
					redir.addFlashAttribute("committeeid",req.getParameter("sub"));
					return "redirect:/CommitteeMainMembers.htm";
				}
				else {
					String[] CommitteeProject=req.getParameterValues("committeeprojectid");	

					long count=service.ProjectCommitteeDelete(CommitteeProject,UserId);					
					if (count > 0) {						
						redir.addAttribute("result", " Committee Removed Successfully");
						redir.addFlashAttribute("projectid",ProjectId);
					} else {
						redir.addAttribute("result", " Committee Remove Unsucessful");
					}					
				}		

			}			
			return "redirect:/ProjectMaster.htm";
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside ProjectCommitteeDelete.htm "+UserId,e);
			return "static/Error";
		}

	}


	@RequestMapping(value="CommitteeAutoSchedule.htm",method = { RequestMethod.POST,RequestMethod.GET})
	public String CommitteeAutoSchedule(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception{

		String UserId = (String) ses.getAttribute("Username");
		String LabCode =(String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeAutoSchedule.htm "+UserId);
		try
		{	
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String LoginType=(String)ses.getAttribute("LoginType");

			List<Object[]> projectdetailslist=null;
			projectdetailslist=service.LoginProjectDetailsList(EmpId,LoginType,LabCode);

			if(projectdetailslist.size()==0) {				
				redir.addAttribute("resultfail", "No Project is Assigned to you.");
				return "redirect:/MainDashBoard.htm";
			}

			String projectid=null;
			String projectname=null;
			String initiationid=null;
			String divisionid=null;
			String projectstatus=null;
			if(req.getParameter("projectid")!=null) {
				projectid=req.getParameter("projectid");
				projectname=req.getParameter("projectname");
				initiationid=req.getParameter("initiationid");
				divisionid=req.getParameter("divisionid");	
				projectstatus=req.getParameter("projectstatus");
			}else if(projectid==null || projectid.equals("null")) {
				projectid=projectdetailslist.get(0)[0].toString();
				projectname=projectdetailslist.get(0)[1].toString();
				initiationid="0";
				divisionid="0";
				projectstatus="C";


			}


			List<Object[]> projectmasterlist = null;
			if(Long.parseLong(projectid)>0) 
			{
				projectmasterlist = service.ProjectMasterList(projectid);
			}
			req.setAttribute("projectid",projectid );
			req.setAttribute("projectmasterlist",projectmasterlist );
			req.setAttribute("projectname",projectname );
			req.setAttribute("divisionid",divisionid );
			req.setAttribute("initiationid",initiationid );

			for(Object[] obj : projectmasterlist ) {
				if(obj[6].toString().equalsIgnoreCase("N")) {		
					return "committee/CommitteeAutoSchedule";
				}
			}		
			List<Object[]> CommitteeAutoScheduleList= service.CommitteeAutoScheduleList(projectid,divisionid,initiationid,projectstatus);
			if(CommitteeAutoScheduleList.size()>0) {
				redir.addAttribute("projectname",projectname);
				redir.addAttribute("projectid",projectid);
				redir.addAttribute("divisionid",divisionid);
				redir.addAttribute("initiationid",initiationid);
				redir.addAttribute("projectstatus",projectstatus);
				return "redirect:/CommitteeAutoScheduleList.htm";
			}		
		}catch(Exception e) {	    		
			logger.error(new Date() +" Inside CommitteeAutoSchedule.htm "+UserId, e);
			e.printStackTrace();
		}	
		return "committee/CommitteeAutoSchedule";
	}

	@RequestMapping(value="CommitteeAutoScheduleSubmit.htm",method = RequestMethod.POST)
	public String CommitteeAutoScheduleSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception{
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeAutoScheduleSubmit.htm "+UserId);
		try
		{

			long count=0;
			String projectid=req.getParameter("projectid");
			String divisionid=req.getParameter("divisionid");
			String initiationid=req.getParameter("initiationid");
			Object[] projectdetails=service.projectdetails(projectid);
			String PDC=service.projectdetails(projectid)[9].toString();
			List<Object[]> ProjectMasterList= service.ProjectMasterList(projectid);	
			LocalDate EndDatePDC=LocalDate.parse(PDC);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");	
			if (EndDatePDC.isBefore(LocalDate.now()) ) {
				redir.addAttribute("resultfail", "PDC of Project is Expired");
				redir.addFlashAttribute("projectid", projectid);
				return "redirect:/ProjectMaster.htm";
			}
			for(int i=0;i<ProjectMasterList.size();i++) 
			{
				if(ProjectMasterList.get(i)[6].toString().equalsIgnoreCase("N"))
				{
					String CommitteeId=ProjectMasterList.get(i)[2].toString();
					String PeriodicDuration=service.CommitteeName(CommitteeId)[4].toString();

					if(!PeriodicDuration.equalsIgnoreCase("0")) 
					{					
						LocalDate StartDate=LocalDate.parse(req.getParameter("startdate"), formatter);
						LocalDate EndDate=LocalDate.parse(PDC);

						while(StartDate.isBefore(EndDate) || StartDate.isEqual(EndDate))
						{	  	
							CommitteeScheduleDto committeescheduledto=new CommitteeScheduleDto(); 
							committeescheduledto.setCommitteeId(Long.parseLong(CommitteeId));
							committeescheduledto.setScheduleDate(req.getParameter("startdate"));
							committeescheduledto.setScheduleStartTime(req.getParameter("starttime"));
							committeescheduledto.setCreatedBy(UserId);
							committeescheduledto.setScheduleFlag("MSC");
							committeescheduledto.setProjectId(projectid);
							committeescheduledto.setConfidential(projectdetails[10].toString());
							committeescheduledto.setDivisionId(divisionid);
							committeescheduledto.setInitiationId(initiationid);
							String formattedString2 = StartDate.format(formatter);
							committeescheduledto.setScheduleDate(formattedString2);
							committeescheduledto.setLabCode(labcode);

							count=service.CommitteeScheduleAddSubmit(committeescheduledto);
							service.CommitteeProjectUpdate(projectid,CommitteeId);

							StartDate=StartDate.plusDays(Long.parseLong(PeriodicDuration));

						}
					}
					else if(PeriodicDuration.equalsIgnoreCase("0")) {

						CommitteeScheduleDto committeescheduledto=new CommitteeScheduleDto(); 
						committeescheduledto.setCommitteeId(Long.parseLong(CommitteeId));
						committeescheduledto.setScheduleDate(req.getParameter("startdate"));
						committeescheduledto.setScheduleStartTime(req.getParameter("starttime"));
						committeescheduledto.setCreatedBy(UserId);
						committeescheduledto.setScheduleFlag("MSC");
						committeescheduledto.setProjectId(projectid);
						committeescheduledto.setScheduleDate(req.getParameter("startdate"));
						committeescheduledto.setConfidential(projectdetails[10].toString());
						committeescheduledto.setDivisionId(divisionid);
						committeescheduledto.setInitiationId(initiationid);
						committeescheduledto.setLabCode(labcode);
						count=service.CommitteeScheduleAddSubmit(committeescheduledto);
						service.CommitteeProjectUpdate(projectid,CommitteeId);
					}

					if (count > 0) {
						redir.addAttribute("result", "Committee Auto-Scheduled Successfully");
					} else {
						redir.addAttribute("resultfail", " Committee Auto-Schedule Unsucessful");
					}			
				}
			}

			redir.addAttribute("projectid", projectid);
			redir.addAttribute("divisionid", divisionid);
			redir.addAttribute("initiationid", initiationid);
			redir.addAttribute("projectname",service.projectdetails(projectid)[1].toString());
		}catch(Exception e) {	    		
			logger.error(new Date() +" Inside CommitteeAutoScheduleSubmit.htm "+UserId, e);
			e.printStackTrace();
		}	
		return "redirect:/CommitteeAutoScheduleList.htm";
	}


	@RequestMapping(value="CommitteeAutoScheduleList.htm")
	public String CommitteeAutoScheduleList( Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeAutoScheduleList.htm "+UserId);
		try
		{
			List<Object[]> CommitteeAutoScheduleList=null;
			String committeeid=req.getParameter("committeeid");
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String LoginType=(String)ses.getAttribute("LoginType");
			List<Object[]> projectdetailslist = null; 
			projectdetailslist= service.LoginProjectDetailsList(EmpId, LoginType,LabCode);
			if(projectdetailslist.size()==0) {				
				redir.addAttribute("resultfail", "No Project is Assigned to you.");
				return "redirect:/MainDashBoard.htm";
			}
			if(committeeid==null)
			{
				Map md=model.asMap();
				committeeid=(String)md.get("committeeid");
			}
			String divisionid=req.getParameter("divisionid");
			if(divisionid==null)  {

				Map md=model.asMap();
				divisionid=(String)md.get("divisionid");
			}		

			String initiationid=req.getParameter("initiationid");
			if(initiationid==null)  
			{
				Map md=model.asMap();
				initiationid=(String)md.get("initiationid");
			}

			String projectid=req.getParameter("projectid");
			if(projectid==null)  {

				Map md=model.asMap();
				projectid=(String)md.get("projectid");
			}
			if(projectid==null) {
				projectid=projectdetailslist.get(0)[0].toString();
				divisionid="0";
				initiationid="0";
			}


			String projectstatus=req.getParameter("projectstatus");
			if(projectstatus==null)  
			{					
				Map md=model.asMap();
				projectstatus=(String)md.get("projectstatus");
			}	

			if(projectstatus==null) 
			{
				projectstatus="A";
			}


			if(committeeid==null || committeeid.equals("all") )
			{
				committeeid="all";
				CommitteeAutoScheduleList= service.CommitteeAutoScheduleList(projectid,divisionid,initiationid,projectstatus);	
			}
			else if(committeeid.equals("others")) {

				CommitteeAutoScheduleList=service.CommitteeOthersList(projectid,divisionid,initiationid,projectstatus);
			}
			else
			{
				CommitteeAutoScheduleList= service.CommitteeAutoScheduleList(projectid, committeeid,divisionid,initiationid,projectstatus);
			}
			if(Long.parseLong(projectid)>0) 
			{
				req.setAttribute("committeelist",service.ProjectApplicableCommitteeList(projectid));
				req.setAttribute("ProjectName", service.projectdetails(projectid)[4].toString());
			}
			else if(Long.parseLong(divisionid)>0)
			{	
				req.setAttribute("committeelist",service.DivisionCommitteeMainList(divisionid));
				req.setAttribute("ProjectName", service.DivisionData(divisionid)[2].toString());
			}
			else if(Long.parseLong(initiationid)>0)
			{	
				req.setAttribute("committeelist",service.InitiationCommitteeMainList(initiationid)); 
				req.setAttribute("ProjectName", service.Initiationdetails(initiationid)[2].toString());
			}


			req.setAttribute("committeeid",committeeid);
			req.setAttribute("projectstatus",projectstatus);
			req.setAttribute("divisionid",divisionid);
			req.setAttribute("projectid",projectid);
			req.setAttribute("initiationid",initiationid);
			req.setAttribute("ProjectsList", projectdetailslist);
			req.setAttribute("CommitteeAutoScheduleList", CommitteeAutoScheduleList);
			
			return "committee/CommitteeAutoScheduleList";
		}catch(Exception e) {	    		
			logger.error(new Date() +" Inside CommitteeAutoScheduleList.htm "+UserId, e);
			e.printStackTrace();
			return "static/Error";
		}	
	}


	@RequestMapping(value="CommitteeAutoScheduleEdit.htm")
	public String CommitteeAutoScheduleEdit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteeAutoScheduleEdit.htm "+UserId);
		try
		{
			String scheduleid=req.getParameter("scheduleid");		
			Object[] scheduledata=service.CommitteeScheduleEditData(scheduleid);
			String projectid=scheduledata[9].toString();

			req.setAttribute("projectname",req.getParameter("projectname"));
			req.setAttribute("scheduledata",scheduledata);
			req.setAttribute("projectid",projectid);
			req.setAttribute("projectstatus",req.getParameter("projectstatus"));

		}catch(Exception e) {	    		
			logger.error(new Date() +" Inside CommitteeAutoScheduleEdit.htm "+UserId, e);
			e.printStackTrace();
		}	
		return "committee/CommitteeAutoScheduleEdit";
	}

	@RequestMapping(value="CommitteeAutoScheduleUpdate.htm")
	public String CommitteeAutoScheduleUpdate(HttpServletRequest req, HttpSession ses,RedirectAttributes redir) throws Exception
	{		
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteeAutoScheduleUpdate.htm "+UserId);
		try
		{				
			CommitteeScheduleDto committeescheduledto=new CommitteeScheduleDto(); 
			committeescheduledto.setScheduleDate(req.getParameter("startdate"));
			committeescheduledto.setScheduleStartTime(req.getParameter("starttime"));
			committeescheduledto.setScheduleId(Long.parseLong(req.getParameter("scheduleid")));
			committeescheduledto.setModifiedBy(UserId);

			long count = service.CommitteeScheduleUpdate(committeescheduledto);

			if (count > 0) {
				redir.addAttribute("result", "Committee Schedule Edited Successfully");
			} else {
				redir.addAttribute("resultfail", "Committee Schedule Edit Unsuccessful");

			}	
			String committeeid=req.getParameter("committeeid");
			String projectid=req.getParameter("projectid");
			String divisionid=req.getParameter("divisionid");
			String initiationid=req.getParameter("initiationid");

			redir.addFlashAttribute("committeeid",committeeid);
			redir.addFlashAttribute("projectid",projectid);
			redir.addFlashAttribute("divisionid",divisionid);
			redir.addFlashAttribute("initiationid",initiationid);

			if((projectid!=null && Long.parseLong(projectid)>0) ||(divisionid!=null && Long.parseLong(divisionid)>0) || (initiationid!=null && Long.parseLong(initiationid)>0) )
			{	
				redir.addFlashAttribute("committeeid",committeeid);
				return "redirect:/CommitteeAutoScheduleList.htm"; 
			}

		}catch(Exception e) {	    		
			logger.error(new Date() +" Inside CommitteeAutoScheduleUpdate.htm "+UserId, e);
			e.printStackTrace();
		}	
		return "redirect:/NonProjectCommitteeAutoSchedule.htm";

	}

private boolean isValidFileType(MultipartFile file) {
		
		
		
	    String contentType = file.getContentType();
	    String originalFilename = file.getOriginalFilename();
		
	    if (file == null || file.isEmpty()) {
	        return true; // nothing uploaded, so it's valid
	    }

	    
	    
	  
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

	
	
	
	
	
	@RequestMapping(value="MinutesAttachment.htm",method=RequestMethod.POST)
	public String MinutesAttachment(HttpServletRequest req, HttpSession ses, RedirectAttributes redir,@RequestPart("FileAttach") MultipartFile FileAttach) throws Exception{

		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside MinutesAttachment.htm "+UserId);
		try
		{			
		
			// 🔹 Validate file types
	        if (!isValidFileType(FileAttach) ) {

	        	redir.addFlashAttribute("committeescheduleid", req.getParameter("ScheduleId"));
	    		redir.addFlashAttribute("specname", "Introduction");
	    		redir.addFlashAttribute("unit1","unit1");
	    		// Prudhvi - 06/03/2024
	    		/* ------------------ start ----------------------- */
	    		String redirpageflag = req.getParameter("redirpageflag");
	    		if(redirpageflag!=null && redirpageflag.equalsIgnoreCase("ROD")) {
	    			
	    			return redirectWithError(redir, "RODScheduleMinutes.htm",
		                    "Invalid file type. Only PDF or Image files are allowed.");
	    			
	    		}
	    		/* ------------------ end ----------------------- */

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
   
	    		
	        	
	        	return redirectWithError(redir, "CommitteeScheduleMinutes.htm",
	                    "Invalid file type. Only PDF or Image files are allowed.");
	    		
	        }
			
			
			CommitteeMinutesAttachmentDto dto= new CommitteeMinutesAttachmentDto();
			dto.setScheduleId(req.getParameter("ScheduleId"));
			dto.setMinutesAttachment(FileAttach);
			dto.setAttachmentName(FileAttach.getOriginalFilename());
			dto.setCreatedBy(UserId);
			dto.setMinutesAttachmentId(req.getParameter("attachmentid"));
			dto.setLabCode(LabCode);
			Long count = service.MinutesAttachmentAdd(dto);

			if (count > 0) {
				redir.addAttribute("result", "Minutes Attachment Added Successfully");
			} else {
				redir.addAttribute("resultfail", "Minutes Attachment Add Unsuccessful");
			}

		}

		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside MinutesAttachment.htm "+UserId,e);
		}

		redir.addFlashAttribute("committeescheduleid", req.getParameter("ScheduleId"));
		redir.addFlashAttribute("specname", "Introduction");
		redir.addFlashAttribute("unit1","unit1");
		// Prudhvi - 06/03/2024
		/* ------------------ start ----------------------- */
		String redirpageflag = req.getParameter("redirpageflag");
		if(redirpageflag!=null && redirpageflag.equalsIgnoreCase("ROD")) {
			return"redirect:/RODScheduleMinutes.htm";
		}
		/* ------------------ end ----------------------- */

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

		return "redirect:/CommitteeScheduleMinutes.htm";	
	}
	@RequestMapping(value="MomAttachment.htm",method=RequestMethod.POST)
	public String MomAttachment(HttpServletRequest req, HttpSession ses, RedirectAttributes redir,@RequestPart("MOMFile") MultipartFile FileAttach) throws Exception{

		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside MomAttachment.htm "+UserId);
		try
		{			
			
			// 🔹 Validate file types
	        if (!isValidFileType(FileAttach) ) {

//	     
	        	
	        	redir.addFlashAttribute("committeescheduleid", req.getParameter("ScheduleId"));
	    		redir.addFlashAttribute("specname", "Introduction");
	    		redir.addFlashAttribute("unit1","unit1");
	    		// Prudhvi - 06/03/2024
	    		/* ------------------ start ----------------------- */
	    		String redirpageflag = req.getParameter("redirpageflag");
	    		if(redirpageflag!=null && redirpageflag.equalsIgnoreCase("ROD")) {
	    			
	    			
	    			 return redirectWithError(redir, "RODScheduleMinutes.htm",
 	                    "Invalid file type. Only PDF or Image files are allowed.");
	    		}
	    		/* ------------------ end ----------------------- */
	    		
	    		 return redirectWithError(redir, "CommitteeScheduleMinutes.htm",
	 	                    "Invalid file type. Only PDF or Image files are allowed.");
	        	
	        }
			
			
			
			CommitteeMomAttachment cm = new CommitteeMomAttachment();

			cm.setScheduleId(Long.parseLong(req.getParameter("ScheduleId")));
			cm.setFile(FileAttach);
			cm.setCreatedBy(UserId);
			cm.setCreatedDate(sdf1.format(new Date()));

			long result=service.UpdateMomAttach(cm.getScheduleId());

			Long count = service.MomAttach(cm,LabCode);

			if (count > 0) {
				redir.addAttribute("result", "Mom Attachment Added Successfully");
			} else {
				redir.addAttribute("resultfail", "Mom Attachment Add Unsuccessful");
			}

		}

		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside MomAttachment.htm "+UserId,e);
		}

		redir.addFlashAttribute("committeescheduleid", req.getParameter("ScheduleId"));
		redir.addFlashAttribute("specname", "Introduction");
		redir.addFlashAttribute("unit1","unit1");
		// Prudhvi - 06/03/2024
		/* ------------------ start ----------------------- */
		String redirpageflag = req.getParameter("redirpageflag");
		if(redirpageflag!=null && redirpageflag.equalsIgnoreCase("ROD")) {
			return"redirect:/RODScheduleMinutes.htm";
		}
		/* ------------------ end ----------------------- */
		return "redirect:/CommitteeScheduleMinutes.htm";	
	}

	@RequestMapping(value="MinutesAttachmentDelete.htm",method=RequestMethod.POST)
	public String MinutesAttachmentDelete(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{	
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside MinutesAttachmentDelete.htm "+UserId);
		try
		{			
			String attachid=req.getParameter("attachmentid");			
			int count = service.MinutesAttachmentDelete(attachid);
			if (count > 0) {
				redir.addAttribute("result", "Minutes Attachment Deleted Successfully");
			} else {
				redir.addAttribute("resultfail", "Minutes Attachment Delete Unsuccessful");
			}		
		}

		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside MinutesAttachmentDelete.htm "+UserId,e);
		}

		redir.addFlashAttribute("committeescheduleid", req.getParameter("ScheduleId"));
		redir.addFlashAttribute("specname", "Introduction");
		redir.addFlashAttribute("unit1","unit1");
		// Prudhvi - 06/03/2024
		/* ------------------ start ----------------------- */
		String redirpageflag = req.getParameter("redirpageflag");
		if(redirpageflag!=null && redirpageflag.equalsIgnoreCase("ROD")) {
			return"redirect:/RODScheduleMinutes.htm";
		}
		/* ------------------ end ----------------------- */

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

		return "redirect:/CommitteeScheduleMinutes.htm";	
	}


	@RequestMapping(value = "MinutesAttachDownload.htm", method = RequestMethod.GET)
	public void MinutesAttachDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res)
			throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside MinutesAttachDownload.htm "+UserId);		
		try { 
			res.setContentType("Application/octet-stream");	
			CommitteeMinutesAttachment attachment = service.MinutesAttachDownload(req.getParameter("attachmentid"));
			String filedata =attachment.getFilePath().replaceAll("[/\\\\]", ",");
			String[] fileParts = filedata.split(",");
			Path filepath = Paths.get(uploadpath, fileParts[0], fileParts[1], attachment.getAttachmentName());
			File my_file=filepath.toFile();
			//		my_file = new File(uploadpath+attachment.getFilePath()+File.separator+attachment.getAttachmentName()); 
			res.setHeader("Content-disposition","inline; filename="+attachment.getAttachmentName().toString()); 
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
			e.printStackTrace(); logger.error(new Date() +"Inside MinutesAttachDownload.htm"+UserId,e);
		}
	}


	@RequestMapping(value = "MinutesAttachDownloadprotected.htm", method = RequestMethod.GET)
	public void MinutesAttachDownloadprotected(HttpServletRequest req, HttpSession ses, HttpServletResponse res)
			throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside MinutesAttachDownloadprotected.htm "+UserId);		
		try {
			String attachmentId = req.getParameter("attachmentid");
			String userId = (String) req.getSession().getAttribute("Username");
			String EmpName = (String) req.getSession().getAttribute("EmpName");
			String EmpNo = (String) req.getSession().getAttribute("EmpNo");

			String LabCode = (String) ses.getAttribute("labcode");

			CommitteeMinutesAttachment attachment = service.MinutesAttachDownload(attachmentId);

			// String inputFilePath = uploadpath + attachment.getFilePath() + File.separator + attachment.getAttachmentName();
			String filedata =attachment.getFilePath().replaceAll("[/\\\\]", ",");
			String[] fileParts = filedata.split(",");
			Path filepath = Paths.get(uploadpath, fileParts[0], fileParts[1], attachment.getAttachmentName());

			//			            String encryptedOutputPath = uploadpath + attachment.getFilePath() + File.separator +"Encrypted_" + attachment.getAttachmentName();
			Path filepath2 = Paths.get(uploadpath, fileParts[0], fileParts[1], ("Encrypted_" +attachment.getAttachmentName()));

			String password = LabCode+"123";

			PdfReader pdfReader = new PdfReader(filepath.toString());

			pdfReader.setUnethicalReading(true);

			PdfWriter pdfWriter = new PdfWriter(filepath2.toString(),
					new WriterProperties().setStandardEncryption(password.getBytes(),
							password.getBytes(),
							EncryptionConstants.ALLOW_PRINTING, EncryptionConstants.ENCRYPTION_AES_128));


			PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter);

			PMSFileUtils.addWatermarktoPdf(pdfDocument, EmpName , EmpNo );

			pdfDocument.close();
			pdfWriter.close();
			pdfReader.close();

			//			           File pdfFile = new File(encryptedOutputPath);
			File pdfFile = filepath2.toFile();
			res.setContentType("application/pdf");
			res.setHeader("Content-disposition", "inline; filename=" + attachment.getAttachmentName());

			try (FileInputStream in = new FileInputStream(pdfFile);
					OutputStream out = res.getOutputStream()) {

				byte[] buffer = new byte[4096];
				int length;
				while ((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}

			} finally {
				pdfFile.delete();
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Inside MinutesAttachDownloadprotected.htm", e);
		}
	}


	@RequestMapping(value="MeetingMinutesApproval.htm", method=   {RequestMethod.POST,RequestMethod.GET})
	public String MeetingMinutesApproval(HttpServletRequest req, HttpSession ses,RedirectAttributes redir) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		String Option=req.getParameter("sub");

		logger.info(new Date() +"Inside MeetingMinutesApproval.htm "+UserId);
		try
		{
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String CommitteeScheduleId=req.getParameter("committeescheduleid");
			String LabCode =(String)ses.getAttribute("labcode");

			req.setAttribute("ccmFlag", req.getParameter("ccmFlag"));
			req.setAttribute("committeeMainId", req.getParameter("committeeMainId"));
			req.setAttribute("committeeId", req.getParameter("committeeId"));

			if(Option!=null) {
				int count = service.MeetingMinutesApproval(CommitteeScheduleId, UserId, EmpId,Option);
				if (count == -1) {
					redir.addAttribute("resultfail", "Please Assign All Actions To Forward Minutes For Approval");
					redir.addAttribute("scheduleid", CommitteeScheduleId);
					return "redirect:/CommitteeScheduleView.htm";
				}

				else if (count > 0) {
					Object[]CommitteMainEnoteList= service.CommitteMainEnoteList("0",CommitteeScheduleId);
					req.setAttribute("NewApprovalList", CommitteMainEnoteList!=null ? service.NewApprovalList(CommitteMainEnoteList[0].toString()):service.NewApprovalList("0"));
					req.setAttribute("scheduleid", CommitteeScheduleId);
					req.setAttribute("employeelist", service.EmployeeList(LabCode));
					req.setAttribute("CommitteMainEnoteList", CommitteMainEnoteList);
					return "committee/MoMFlowAdd";
				}
			}
			Object[]CommitteMainEnoteList= service.CommitteMainEnoteList("0",CommitteeScheduleId);
			req.setAttribute("NewApprovalList", CommitteMainEnoteList!=null ? service.NewApprovalList(CommitteMainEnoteList[0].toString()):service.NewApprovalList("0"));
			req.setAttribute("scheduleid", CommitteeScheduleId);
			req.setAttribute("employeelist", service.EmployeeList(LabCode));
			req.setAttribute("CommitteMainEnoteList", CommitteMainEnoteList);

		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"MeetingMinutesApproval.htm "+UserId,e);
		}			

		return "committee/MoMFlowAdd";
	}

	//	@RequestMapping(value = "MeetingApprovalMinutes.htm", method = RequestMethod.GET)
	//	public String MeetingApprovalMinutes(HttpServletRequest req, RedirectAttributes redir, HttpSession ses)	throws Exception 
	//	{
	//		String UserId=(String)ses.getAttribute("Username");
	//		logger.info(new Date() +"Inside MeetingApprovalMinutes.htm "+UserId);
	//		try
	//		{
	//		
	//			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
	//	
	//			req.setAttribute("MeetingApprovalMinutes", service.MeetingApprovalMinutesList(EmpId));
	//		}
	//		catch (Exception e) {
	//				e.printStackTrace(); logger.error(new Date() +"Inside MeetingApprovalAgenda.htm "+UserId,e);
	//		}
	//
	//		return "committee/ComMeetingApprovalMinList";
	//	}
	//	
	@RequestMapping(value = "MeetingApprovalMinutesDetails.htm", method = RequestMethod.POST)
	public String MeetingApprovalMinutesDetails(HttpServletRequest req, RedirectAttributes redir, HttpSession ses)	throws Exception {

		String UserId=(String)ses.getAttribute("Username");

		logger.info(new Date() +"Inside MeetingApprovalMinutesDetails.htm "+UserId);
		try
		{	
			String committeescheduleid=req.getParameter("scheduleid");

			Object[] committeescheduleeditdata=service.CommitteeScheduleEditData(committeescheduleid);
			String projectid= committeescheduleeditdata[9].toString();
			if(projectid!=null && Integer.parseInt(projectid)>0)

			{
				req.setAttribute("projectdetails", service.projectdetails(projectid));
			}
			String divisionid= committeescheduleeditdata[16].toString();
			if(divisionid!=null && Integer.parseInt(divisionid)>0)
			{
				req.setAttribute("divisiondetails", service.DivisionData(divisionid));
			}
			String initiationid= committeescheduleeditdata[17].toString();
			if(initiationid!=null && Integer.parseInt(initiationid)>0)
			{
				req.setAttribute("initiationdetails", service.Initiationdetails(initiationid));
			}
			List<Object[]> actionlist= service.MinutesViewAllActionList(committeescheduleid);
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

			req.setAttribute("committeeminutesspeclist",service.CommitteeScheduleMinutes(committeescheduleid) );
			req.setAttribute("committeescheduleeditdata", committeescheduleeditdata);
			req.setAttribute("CommitteeAgendaList", service.AgendaList(committeescheduleid));
			req.setAttribute("committeeminutes",service.CommitteeMinutesSpecdetails());
			req.setAttribute("committeeminutessub",service.CommitteeMinutesSub());
			req.setAttribute("committeeinvitedlist", service.CommitteeAtendance(committeescheduleid,"1"));			
			req.setAttribute("actionlist",actionsdata);
			req.setAttribute("labdetails", service.LabDetails(committeescheduleeditdata[24].toString()));
			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(committeescheduleeditdata[24].toString()));


			return "committee/ComMeetingApprovalMinDetails";
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside MeetingApprovalMinutesDetails.htm "+UserId,e);
			return "static/Error";
		}


	}

	@RequestMapping(value="MeetingMinutesApprovalSubmit.htm", method=RequestMethod.POST)
	public String MeetingMinutesApprovalSubmit(HttpServletRequest req,RedirectAttributes redir,HttpServletResponse res, HttpSession ses) throws Exception{

		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside MeetingMinutesApprovalSubmit.htm "+UserId);
		try
		{
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();

			String ScheduleId=req.getParameter("committeescheduleid");
			String Option=req.getParameter("sub");
			String Remarks=req.getParameter("Remark");

			if(Option.equalsIgnoreCase("approve")) {
				getMinutesFrozen(req, ses, res, redir);
			}
			int count = service.MeetingMinutesApprovalSubmit(ScheduleId, Remarks, UserId, EmpId,Option);		
			if (count > 0) {					
				if(Option.equalsIgnoreCase("approve")) {						
					redir.addAttribute("result", "Meeting Minutes Approved Successfully");
				}					
				if(Option.equalsIgnoreCase("return")) {						
					redir.addAttribute("result", "Meeting Minutes Returned Successfully");
				}			
			}else {					
				redir.addAttribute("resultfail", "Meeting Minutes Forward Unsuccessful");
			}

		}catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside MeetingMinutesApprovalSubmit.htm "+UserId,e);
		}

		return "redirect:/MeetingApprovalAgenda.htm";
	}


	@RequestMapping(value = "MeetingSearch.htm")
	public String ActionSearch(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception {
		String UserId=(String)ses.getAttribute("Username");
		String LabCode =(String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside MeetingSearch.htm "+UserId);
		try
		{
			String EmpId= ((Long) ses.getAttribute("EmpId")).toString();
			String LoginType=(String)ses.getAttribute("LoginType");

			if(req.getParameter("search")!=null) {

				if(LoginType.equalsIgnoreCase("P")|| LoginType.equalsIgnoreCase("Y") || LoginType.equalsIgnoreCase("Z") || LoginType.equalsIgnoreCase("A")  || LoginType.equalsIgnoreCase("C")|| LoginType.equalsIgnoreCase("I")) 
				{
					req.setAttribute("meetingsearch", service.MeetingSearchList(req.getParameter("search") ,LabCode));
				}
				else {
					req.setAttribute("meetingsearch", service.UserSchedulesList(EmpId,req.getParameter("search")));
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside MeetingSearch.htm "+UserId,e);
		}

		return "committee/MeetingSearch";
	}

	@RequestMapping(value="CommitteeConstitutionLetter.htm")   
	public String CommitteeConstitutionLetter(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteeConstitutionLetter.htm "+UserId);
		String labcode= (String) ses.getAttribute("labcode");
		try
		{

			String committeemainid=req.getParameter("committeemainid");	

			Object[] committeemaindata= service.CommitteMainData(committeemainid);
			String committeeid=committeemaindata[1].toString();
			String projectid=committeemaindata[2].toString() ;
			String divisionid=committeemaindata[3].toString() ;
			String initiationid=committeemaindata[4].toString() ;
			String labCode=committeemaindata[10].toString();
			if(Long.parseLong(projectid)>0)
			{	
				req.setAttribute("projectdata", service.projectdetails(projectid));
				req.setAttribute("committeedescription", service.ProjectCommitteeDescriptionTOR(projectid,committeeid));
			}
			if(Long.parseLong(divisionid)>0)
			{	
				req.setAttribute("divisiondata", service.DivisionData(divisionid));
				req.setAttribute("committeedescription", service.DivisionCommitteeDescriptionTOR(divisionid,committeeid));
			}	
			if(Long.parseLong(initiationid)>0)
			{	
				req.setAttribute("initiationdata", service.Initiationdetails(initiationid));
				req.setAttribute("committeedescription", service.InitiationCommitteeDescriptionTOR(initiationid,committeeid));
			}	

			Object[] committeeedata=service.CommitteeDetails(committeeid);		
			List<Object[]> committeeallmemberslist=service.CommitteeAllMembers(committeemainid);

			req.setAttribute("committeeallmemberslist",committeeallmemberslist );
			req.setAttribute("committeemaindata", committeemaindata);
			req.setAttribute("committeeedata",committeeedata);
			req.setAttribute("projectid",projectid);		
			req.setAttribute("labdetails", service.LabDetails(labCode));
			req.setAttribute("email","N");
			req.setAttribute("constitutionapprovalflow",service.ConstitutionApprovalFlowData (committeemainid));


			Object[]CommitteMainEnoteList= service.CommitteMainEnoteList(committeemainid,"0");
			req.setAttribute("CommitteMainEnoteList", CommitteMainEnoteList);
			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(committeeedata[13].toString()));
			req.setAttribute("projectAssignList", projectService.ProjectAssignList(projectid));
			req.setAttribute("isLetter", "Y");

			req.setAttribute("constitutionapprovalflowData", service.allconstitutionapprovalflowData(committeemainid));
			if(labcode.equalsIgnoreCase("PGAD")) {
				return "committee/CommitteeConstitutionLetterpgad" ;
			}
			return "committee/CommitteeConstitutionLetter" ;
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside CommitteeConstitutionLetter.htm "+UserId,e);
			return "static/Error";
		}
	}



	@RequestMapping(value="CommitteeConstitutionLetterDownload.htm")   
	public void CommitteeConstitutionLetterDownload(HttpServletRequest req, HttpSession ses, RedirectAttributes redir,HttpServletResponse res) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteeConstitutionLetterDownload.htm "+UserId);
		String labcode = (String) ses.getAttribute("labcode");
		try
		{
			String committeemainid=req.getParameter("committeemainid");	
			String flag=req.getParameter("flag");
			System.out.println("flag -"+flag);
			Object[] committeemaindata= service.CommitteMainData(committeemainid);
			String committeeid=committeemaindata[1].toString();
			String projectid=committeemaindata[2].toString() ;
			String divisionid=committeemaindata[3].toString() ;
			String initiationid=committeemaindata[4].toString() ;
			if(Long.parseLong(projectid)>0)
			{	
				req.setAttribute("projectdata", service.projectdetails(projectid));
				req.setAttribute("committeedescription", service.ProjectCommitteeDescriptionTOR(projectid,committeeid));
			}
			if(Long.parseLong(divisionid)>0)
			{	
				req.setAttribute("divisiondata", service.DivisionData(divisionid));
				req.setAttribute("committeedescription", service.DivisionCommitteeDescriptionTOR(divisionid,committeeid));
			}	
			if(Long.parseLong(initiationid)>0)
			{	
				req.setAttribute("initiationdata", service.Initiationdetails(initiationid));
				req.setAttribute("committeedescription", service.InitiationCommitteeDescriptionTOR(initiationid,committeeid));
			}	

			Object[] committeeedata=service.CommitteeDetails(committeeid);		
			List<Object[]> committeeallmemberslist=service.CommitteeAllMembers(committeemainid);
			req.setAttribute("constitutionapprovalflowData", service.allconstitutionapprovalflowData(committeemainid));
			req.setAttribute("committeeallmemberslist",committeeallmemberslist );
			req.setAttribute("committeemaindata", committeemaindata);
			req.setAttribute("committeeedata",committeeedata);
			req.setAttribute("projectid",projectid);		
			req.setAttribute("labdetails", service.LabDetails(committeeedata[13].toString()));
			req.setAttribute("email","N");
			req.setAttribute("flag", "Y");
			req.setAttribute("projectAssignList", projectService.ProjectAssignList(projectid));
			req.setAttribute("constitutionapprovalflow",service.ConstitutionApprovalFlowData (committeemainid));
			String filename=committeeedata[1]+" Formation Letter"+"("+  (committeemaindata[12]!=null?committeemaindata[12].toString():committeemaindata[5].toString())+ ")";		
			Object[]CommitteMainEnoteList= service.CommitteMainEnoteList(committeemainid,"0");
			req.setAttribute("CommitteMainEnoteList", CommitteMainEnoteList);
			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(committeeedata[13].toString()));

			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path); 
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			if(labcode.equalsIgnoreCase("PGAD")) req.getRequestDispatcher("/view/committee/CommitteeConstitutionLetterpgad.jsp").forward(req, customResponse);
			else req.getRequestDispatcher("/view/committee/CommitteeConstitutionLetter.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();
			byte[] data = html.getBytes();
			InputStream fis1=new ByteArrayInputStream(data);
			PdfDocument pdfDoc = new PdfDocument(new PdfWriter(path+"/"+filename+".pdf"));	

			Document document = new Document(pdfDoc, PageSize.A4);
			//document.setMargins(50, 100, 150, 50);
			document.setMargins(50, 50, 50, 50);
			ConverterProperties converterProperties = new ConverterProperties();
			FontProvider dfp = new DefaultFontProvider(true, true, true);
			converterProperties.setFontProvider(dfp);
			HtmlConverter.convertToPdf(fis1,pdfDoc,converterProperties);
			document.close();
			res.setContentType("application/pdf");
			if(flag!=null && flag.equalsIgnoreCase("P")) {
				res.setHeader("Content-disposition","attchment;filename="+filename+".pdf"); 
			}else {
				res.setHeader("Content-disposition","inline;filename="+filename+".pdf");
			}
			File f=new File(path+"/"+filename+".pdf");
			FileInputStream fis = new FileInputStream(f);
			DataOutputStream os = new DataOutputStream(res.getOutputStream());
			res.setHeader("Content-Length",String.valueOf(f.length()));
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fis.read(buffer)) >= 0) {
				os.write(buffer, 0, len);
			} 
			fis.close();
			os.close();



			Path pathOfFile2= Paths.get(path+"/"+filename+".pdf"); 
			Files.delete(pathOfFile2);		

		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside CommitteeConstitutionLetterDownload.htm "+UserId,e);
		}


	}

	@RequestMapping(value="MeetingInvitationLetterDownload.htm")
	public void MeetingInvitationLetterDownload(HttpServletRequest req, HttpSession ses, RedirectAttributes redir,HttpServletResponse res) throws Exception
	{

		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside MeetingInvitationLetterDownload.htm "+UserId);
		try
		{			
			String scheduleid=req.getParameter("committeescheduleid");		
			String invitationid=req.getParameter("invitationid");				
			// Prudhvi - 05/03/2024
			/* ------------------ start ----------------------- */
			Object[] scheduledata = null;
			String rodflag = req.getParameter("rodflag");
			if(rodflag!=null && rodflag.equalsIgnoreCase("Y")) {
				req.setAttribute("rodflag", rodflag);
				scheduledata = rodservice.RODScheduleEditData(scheduleid);
			}else {
				scheduledata = service.CommitteeScheduleEditData(scheduleid);
			}
			/* ------------------ end ----------------------- */
			String projectid=scheduledata[9].toString();

			if(projectid==null || Long.parseLong(projectid)==0)
			{					
				projectid="0";
			}
			else if(Long.parseLong(projectid)>0)
			{						
				req.setAttribute("projectdata", service.projectdetails(projectid));
			}			

			List<Object[]> invitedlist=service.CommitteeAtendance(scheduleid,"0");

			Object[] tomemberdata=null;
			for(int i=0;i<invitedlist.size();i++) {

				if(invitedlist.get(i)[1].toString().equalsIgnoreCase(invitationid))
				{
					tomemberdata=invitedlist.get(i);
					break;
				}			
			}		
			req.setAttribute("scheduledata", scheduledata);
			req.setAttribute("labdetails", service.LabDetails(scheduledata[24].toString()));
			req.setAttribute("tomember",tomemberdata );
			req.setAttribute("projectid",projectid);		

			String filename=tomemberdata[6]+" ("+tomemberdata[9]+")";


			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path); 
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/committee/MeetingInvitationLetterView.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();
			byte[] data = html.getBytes();
			InputStream fis1=new ByteArrayInputStream(data);
			PdfDocument pdfDoc = new PdfDocument(new PdfWriter(path+"/"+filename+".pdf"));	

			Document document = new Document(pdfDoc, PageSize.A4);
			//document.setMargins(50, 100, 150, 50);
			document.setMargins(50, 50, 50, 50);
			ConverterProperties converterProperties = new ConverterProperties();
			FontProvider dfp = new DefaultFontProvider(true, true, true);
			converterProperties.setFontProvider(dfp);
			HtmlConverter.convertToPdf(fis1,pdfDoc,converterProperties);
			res.setContentType("application/pdf");
			res.setHeader("Content-disposition","attachment;filename="+filename+".pdf"); 
			File f=new File(path+"/"+filename+".pdf");
			FileInputStream fis = new FileInputStream(f);
			DataOutputStream os = new DataOutputStream(res.getOutputStream());
			res.setHeader("Content-Length",String.valueOf(f.length()));
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fis.read(buffer)) >= 0) {
				os.write(buffer, 0, len);
			} 
			fis.close();
			os.close();



			Path pathOfFile2= Paths.get(path+"/"+filename+".pdf"); 
			Files.delete(pathOfFile2);		

			document.close();


		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside MeetingInvitationLetterDownload.htm "+UserId,e);
		}
	}




	@RequestMapping(value="MeetingInvitationLetter.htm")
	public String MeetingInvitationLetter(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside MeetingInvitationLetter.htm "+UserId);
		try
		{	

			String scheduleid=req.getParameter("committeescheduleid");

			String invitationid=req.getParameter("invitationid");

			// Prudhvi - 05/03/2024
			/* ------------------ start ----------------------- */
			Object[] scheduledata = null;
			String rodflag = req.getParameter("rodflag");
			if(rodflag!=null && rodflag.equalsIgnoreCase("Y")) {
				req.setAttribute("rodflag", rodflag);
				scheduledata = rodservice.RODScheduleEditData(scheduleid);
			}else {
				scheduledata = service.CommitteeScheduleEditData(scheduleid);
			}
			/* ------------------ end ----------------------- */
			String projectid=scheduledata[9].toString();


			if(projectid==null || Long.parseLong(projectid)==0)
			{

				projectid="0";
			}
			else if(Long.parseLong(projectid)>0)
			{			
				req.setAttribute("projectdata", service.projectdetails(projectid));
			}



			List<Object[]> invitedlist=service.CommitteeAtendance(scheduleid,"0");

			Object[] tomemberdata=null;
			for(int i=0;i<invitedlist.size();i++) {


				if(invitedlist.get(i)[1].toString().equalsIgnoreCase(invitationid))
				{
					tomemberdata=invitedlist.get(i);
					break;
				}			
			}		
			req.setAttribute("scheduledata", scheduledata);
			req.setAttribute("labdetails", service.LabDetails(scheduledata[24].toString()));
			req.setAttribute("tomember",tomemberdata );
			req.setAttribute("projectid",projectid);



		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside MeetingInvitationLetter.htm "+UserId,e);
		}
		return "committee/MeetingInvitationLetterView" ;
	}




	@RequestMapping(value="MeetingTabularMinutes.htm")
	public String MeetingTabularMinutes(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{

		String UserId=(String)ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside MeetingTabularMinutes.htm "+UserId);
		try
		{		
			String committeescheduleid = req.getParameter("committeescheduleid");
			Object[] committeescheduleeditdata=service.CommitteeScheduleEditData(committeescheduleid);

			String projectid= committeescheduleeditdata[9].toString();
			if(projectid!=null && Integer.parseInt(projectid)>0)
			{
				req.setAttribute("projectdetails", service.projectdetails(projectid));
			}
			String divisionid= committeescheduleeditdata[16].toString();
			if(divisionid!=null && Integer.parseInt(divisionid)>0)
			{
				req.setAttribute("divisiondetails", service.DivisionData(divisionid));
			}
			String initiationid= committeescheduleeditdata[17].toString();
			if(initiationid!=null && Integer.parseInt(initiationid)>0)
			{
				req.setAttribute("initiationdetails", service.Initiationdetails(initiationid));
			}

			List<Object[]> actionlist= service.MinutesViewAllActionList(committeescheduleid);
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

			req.setAttribute("actionsdata",actionsdata );
			req.setAttribute("committeeminutesspeclist",service.CommitteeScheduleMinutes(committeescheduleid) );
			req.setAttribute("committeescheduleeditdata", committeescheduleeditdata);
			req.setAttribute("committeeminutes",service.CommitteeMinutesSpecdetails());
			req.setAttribute("committeeinvitedlist", service.CommitteeAtendance(committeescheduleid,"1"));
			req.setAttribute("projectid", projectid);				
			req.setAttribute("actionlist", service.MinutesViewAllActionList(committeescheduleid));
			req.setAttribute("labdetails", service.LabDetails(committeescheduleeditdata[24].toString()));
			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(committeescheduleeditdata[24].toString()));
			req.setAttribute("isprint", "N");
			req.setAttribute("meetingcount",service.MeetingNo(committeescheduleeditdata));
			req.setAttribute("labInfo", service.LabDetailes(LabCode));
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +"Inside MeetingTabularMinutes.htm "+UserId,e);
		}		
		return "committee/MeetingTabularMinutes";
	}


	@RequestMapping(value="MeetingTabularMinutesDownload.htm")
	public void MeetingTabularMinutesDownload(HttpServletRequest req, HttpSession ses, RedirectAttributes redir,HttpServletResponse res) throws Exception
	{

		String UserId=(String)ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside MeetingTabularMinutesDownload.htm "+UserId);
		try
		{
			String committeescheduleid = req.getParameter("committeescheduleid");
			Object[] committeescheduleeditdata=service.CommitteeScheduleEditData(committeescheduleid);
			String projectid= committeescheduleeditdata[9].toString();
			if(projectid!=null && Integer.parseInt(projectid)>0)
			{
				req.setAttribute("projectdetails", service.projectdetails(projectid));
			}
			String divisionid= committeescheduleeditdata[16].toString();
			if(divisionid!=null && Integer.parseInt(divisionid)>0)
			{
				req.setAttribute("divisiondetails", service.DivisionData(divisionid));
			}
			String initiationid= committeescheduleeditdata[17].toString();
			if(initiationid!=null && Integer.parseInt(initiationid)>0)
			{
				req.setAttribute("initiationdetails", service.Initiationdetails(initiationid));
			}

			List<Object[]> actionlist= service.MinutesViewAllActionList(committeescheduleid);
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

			req.setAttribute("actionsdata",actionsdata );

			List<Object[]> repList = service.getCommitteeMainRepList(committeescheduleeditdata[1]!=null?committeescheduleeditdata[1].toString():"0");
			List<String> repCodes = new ArrayList<>();
			
			if(repList != null && !repList.isEmpty()){
				repCodes = repList.stream().map(obj -> obj[1]!=null ? obj[1].toString() : null).filter(Objects::nonNull).collect(Collectors.toList());
			}

			req.setAttribute("repCodes", repCodes);
			req.setAttribute("committeeminutesspeclist",service.CommitteeScheduleMinutes(committeescheduleid) );
			req.setAttribute("committeescheduleeditdata", committeescheduleeditdata);
			//req.setAttribute("CommitteeAgendaList", service.CommitteeAgendaList(committeescheduleid));
			req.setAttribute("committeeminutes",service.CommitteeMinutesSpecdetails());
			//req.setAttribute("committeeminutessub",service.CommitteeMinutesSub());
			req.setAttribute("committeeinvitedlist", service.CommitteeAtendance(committeescheduleid,"1"));
			req.setAttribute("projectid", projectid);				
			req.setAttribute("actionlist", service.MinutesViewAllActionList(committeescheduleid));
			req.setAttribute("labdetails", service.LabDetails(committeescheduleeditdata[24].toString()));
			req.setAttribute("isprint", "Y");
			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(committeescheduleeditdata[24].toString()));
			req.setAttribute("meetingcount",service.MeetingNo(committeescheduleeditdata));
			req.setAttribute("labInfo", service.LabDetailes(LabCode));
			req.setAttribute("ActionPlanSixMonths", service.ActionPlanSixMonths(projectid));
			String filename=committeescheduleeditdata[11].toString().replace("/", "-");

			req.setAttribute("flagforView", "M");			
			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);

			req.setAttribute("ccmFlag", req.getParameter("ccmFlag"));


			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/committee/MeetingTabularMinutes.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"));
			req.setAttribute("tableactionlist",  actionsdata);
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


		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside MeetingTabularMinutesDownload.htm "+UserId,e);
		}
	}

	// commented on 07-12-2023
	//	@RequestMapping(value="EmailMeetingInvitationLetter.htm",method=RequestMethod.POST)
	//	public String EmailMeetingInvitationLetter(HttpServletRequest req,HttpSession ses,RedirectAttributes redir,HttpServletResponse res) throws Exception{
	//		
	//		String UserId=(String)ses.getAttribute("Username");
	//		logger.info(new Date() +"Inside EmailMeetingInvitationLetter.htm "+UserId);		
	//		 try {			 
	//			 	String scheduleid=req.getParameter("committeescheduleid");
	////			 	String memberid=req.getParameter("memberid");
	//				Object[] scheduledata=service.CommitteeScheduleEditData(scheduleid);
	//				String projectid=scheduledata[9].toString();
	////				String committeeid=scheduledata[0].toString();				
	//				if(projectid==null || Long.parseLong(projectid)==0)
	//				{					
	//					projectid="0";
	//				}
	//				else if(Long.parseLong(projectid)>0)
	//				{						
	//					req.setAttribute("projectdata", service.projectdetails(projectid));
	//				}			
	//				List<Object[]> invitedlist=service.CommitteeAllAttendance(scheduleid);				
	//				req.setAttribute("scheduledata", scheduledata);
	//				req.setAttribute("labdetails", service.LabDetails(scheduledata[24].toString()));
	//				req.setAttribute("tomember",invitedlist.get(0) );
	//				req.setAttribute("projectid",projectid);
	//				
	//				//String path=req.getServletContext().getRealPath("/view/temp");
	//				//req.setAttribute("path",path);
	//				CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
	//				req.getRequestDispatcher("/view/committee/MeetingInvitationLetterView.jsp").forward(req, customResponse);
	//				String msgstring = customResponse.getOutput();
	//						 
	//				 MimeMessage msg = javaMailSender.createMimeMessage();
	//				 MimeMessageHelper helper = new MimeMessageHelper(msg, true);
	//				 helper.setTo("dinesh.vedts@gmail.com");
	//				 helper.setCc("bandarudinesh1997@gmail.com");
	//				 helper.setSubject("Meeting Invitation");
	//				 helper.setText(msgstring);
	//				 javaMailSender.send(msg);
	//					 
	//		        
	//		 }		 
	//		 catch (Exception e) {			
	//		 	e.printStackTrace(); logger.error(new Date() +"Inside EmailMeetingInvitationLetter.htm "+UserId,e);
	//		}
	//			
	//		return "redirect:/CommitteeScheduleView.htm";
	//	}
	//	
	@RequestMapping(value = "MeetingReports.htm", method = {RequestMethod.POST, RequestMethod.GET})
	public String MeetingReports(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
			throws Exception {
		String UserId=(String)ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside MeetingReports.htm "+UserId);		
		try 
		{
			String LoginType =(String)ses.getAttribute("LoginType");
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();

			String projectid=req.getParameter("projectid");
			String divisionid=req.getParameter("divisionid");
			String initiationid=req.getParameter("initiationid");
			String term=req.getParameter("term");
			String typeid=req.getParameter("typeid");
			if(term==null)
			{
				term="T";
			}

			if(typeid==null || typeid.equals("A") )
			{
				projectid="A";divisionid="A";initiationid="A";typeid="A";
			}

			req.setAttribute("initiationlist",service.InitiatedProjectDetailsList());
			req.setAttribute("divisionlist",service.LoginDivisionList(EmpId)); 
			req.setAttribute("ProjectList", service.LoginProjectDetailsList(EmpId,LoginType,LabCode));
			req.setAttribute("Term", term);
			req.setAttribute("typeid", typeid);
			req.setAttribute("projectid",projectid );
			req.setAttribute("divisionid",divisionid );
			req.setAttribute("initiationid",initiationid );
			req.setAttribute("MeetingReports", service.MeetingReports(EmpId,term,projectid,divisionid,initiationid, LoginType,LabCode));
		}		 
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside MeetingReports.htm "+UserId,e);
		}
		return "committee/MeetingReports";
	}

	//	@RequestMapping(value = "MeetingReportSubmit.htm", method = RequestMethod.POST)
	//	public String MeetingReportSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception 
	//	{
	//		String UserId=(String)ses.getAttribute("Username");
	//		String LoginType =(String)ses.getAttribute("LoginType");
	//		logger.info(new Date() +"Inside MeetingReportSubmit.htm "+UserId);		
	//		try {
	//			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();		
	//			String loginid= ses.getAttribute("LoginId").toString();
	//			
	//			req.setAttribute("initiationlist",service.InitiatedProjectDetailsList());
	//			req.setAttribute("divisionlist",service.LoginDivisionList(EmpId)); 
	//			req.setAttribute("ProjectList", service.LoginProjectDetailsList(loginid));
	//			req.setAttribute("MeetingReports", service.MeetingReports(EmpId,req.getParameter("Term"),req.getParameter("projectid"),LoginType ));
	//			req.setAttribute("Term", req.getParameter("Term"));
	//			req.setAttribute("Project", req.getParameter("projectid"));
	//		}		 
	//		catch (Exception e) {			
	//		 	e.printStackTrace();
	//		 	logger.error(new Date() +"Inside MeetingReportSubmit.htm "+UserId,e);
	//		}
	//		return "committee/MeetingReports";
	//	}
	//commented for testing




	//	@RequestMapping(value="SendFormationLetter.htm",method=RequestMethod.POST)
	//	public String SendFormationLetter1(HttpServletRequest req,HttpSession ses,RedirectAttributes redir,HttpServletResponse res) throws Exception
	//	{
	//		String UserId=(String)ses.getAttribute("Username");
	//		System.out.println("Hiiiii");
	//		logger.info(new Date() +"Inside SendFormationLetter.htm "+UserId);
	//		try
	//		{
	//			String committeemainid=req.getParameter("committeemainid");	
	//			Object[] committeemaindata= service.CommitteMainData(committeemainid);
	//			String committeeid=committeemaindata[1].toString();
	//			String projectid=committeemaindata[2].toString() ;
	//			String divisionid=committeemaindata[3].toString() ;
	//			String initiationid=committeemaindata[4].toString() ;
	//			if(Long.parseLong(projectid)>0)
	//			{	
	//				req.setAttribute("projectdata", service.projectdetails(projectid));
	//				req.setAttribute("committeedescription", service.ProjectCommitteeDescriptionTOR(projectid,committeeid));
	//			}
	//			if(Long.parseLong(divisionid)>0)
	//			{	
	//				req.setAttribute("divisiondata", service.DivisionData(divisionid));
	//				req.setAttribute("committeedescription", service.DivisionCommitteeDescriptionTOR(divisionid,committeeid));
	//			}	
	//			if(Long.parseLong(initiationid)>0)
	//			{	
	//				req.setAttribute("initiationdata", service.Initiationdetails(initiationid));
	//				req.setAttribute("committeedescription", service.InitiationCommitteeDescriptionTOR(initiationid,committeeid));
	//			}	
	//			
	//			Object[] committeeedata=service.CommitteeDetails(committeeid);		
	//			List<Object[]> committeeallmemberslist=service.CommitteeAllMembers(committeemainid);
	//			
	//			req.setAttribute("committeeallmemberslist",committeeallmemberslist );
	//			req.setAttribute("committeemaindata", committeemaindata);
	//			req.setAttribute("committeeedata",committeeedata);
	//			req.setAttribute("projectid",projectid);
	//			req.setAttribute("labdetails", service.LabDetails(committeeedata[13].toString()));
	//			req.setAttribute("email","Y");
	//			
	//			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
	//			req.getRequestDispatcher("/view/committee/CommitteeConstitutionLetter.jsp").forward(req, customResponse);
	//			String Message= customResponse.getOutput();
	//			
	//			
	//			MimeMessage msg = javaMailSender.createMimeMessage();
	//			MimeMessageHelper helper = new MimeMessageHelper(msg, true);
	//			
	//			ArrayList<String> emails= new ArrayList<String>();			
	//			
	//			for(Object[] obj : committeeallmemberslist ) 
	//			{				
	//				if(obj[6]!=null && obj[8].toString().equals("CC") || obj[8].toString().equals("CS") || obj[8].toString().equals("CI") || obj[8].toString().equals("CW")) 
	//				{				 
	//					emails.add(obj[6].toString());				 
	//				}
	//			}
	//			String [] Email = emails.toArray(new String[emails.size()]);
	//			for(String s:Email) {
	//				System.out.println();
	//			}
	//			helper.setTo(Email);	
	//			helper.setSubject( committeemaindata[7] + " " +" Committee Formation Letter");
	//			helper.setText( Message , true);
	//			 
	//			if(Email!=null && Email.length>0) {
	//				javaMailSender.send(msg); 
	//			}
	//			if (Email.length>0) 
	//			{
	//				redir.addAttribute("result", " Committee Formation Letter Sent Successfully !! ");
	//			}
	//		
	//		redir.addFlashAttribute("committeemainid",committeemaindata[0].toString());
	//		
	//		}
	//		catch (Exception e) 
	//		{
	//			e.printStackTrace(); 
	//			logger.error(new Date() +"Inside SendFormationLetter.htm "+UserId,e);
	//		}
	//		return "redirect:/CommitteeMainMembers.htm";
	//	}

	// new method for committee formation
	@RequestMapping(value="SendFormationLetter.htm",method=RequestMethod.POST)
	public String SendFormationLetter1(HttpServletRequest req,HttpSession ses,RedirectAttributes redir,HttpServletResponse res) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside SendFormationLetter.htm "+UserId);
		try
		{
			String committeemainid=req.getParameter("committeemainid");	
			Object[] committeemaindata= service.CommitteMainData(committeemainid);
			String committeeid=committeemaindata[1].toString();
			String projectid=committeemaindata[2].toString() ;
			String divisionid=committeemaindata[3].toString() ;
			String initiationid=committeemaindata[4].toString() ;
			if(Long.parseLong(projectid)>0)
			{	
				req.setAttribute("projectdata", service.projectdetails(projectid));
				req.setAttribute("committeedescription", service.ProjectCommitteeDescriptionTOR(projectid,committeeid));
			}
			if(Long.parseLong(divisionid)>0)
			{	
				req.setAttribute("divisiondata", service.DivisionData(divisionid));
				req.setAttribute("committeedescription", service.DivisionCommitteeDescriptionTOR(divisionid,committeeid));
			}	
			if(Long.parseLong(initiationid)>0)
			{	
				req.setAttribute("initiationdata", service.Initiationdetails(initiationid));
				req.setAttribute("committeedescription", service.InitiationCommitteeDescriptionTOR(initiationid,committeeid));
			}	

			Object[] committeeedata=service.CommitteeDetails(committeeid);		
			List<Object[]> committeeallmemberslist=service.CommitteeAllMembers(committeemainid);

			req.setAttribute("committeeallmemberslist",committeeallmemberslist );
			req.setAttribute("committeemaindata", committeemaindata);
			req.setAttribute("committeeedata",committeeedata);
			req.setAttribute("projectid",projectid);
			req.setAttribute("labdetails", service.LabDetails(committeeedata[13].toString()));
			req.setAttribute("email","Y");
			req.setAttribute("constitutionapprovalflow",service.ConstitutionApprovalFlowData (committeemainid));

			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/committee/CommitteeConstitutionLetter.jsp").forward(req, customResponse);
			String Message= customResponse.getOutput();




			ArrayList<String> emails= new ArrayList<String>();	
			ArrayList<String> dronaemails= new ArrayList<String>();	
			for(Object[] obj : committeeallmemberslist ) 
			{				
				if(obj[6]!=null && (obj[8].toString().equals("CC") || obj[8].toString().equals("CS") || obj[8].toString().equals("CI") || obj[8].toString().equals("CW") || obj[8].toString().equals("CH"))) 
				{				 
					emails.add(obj[6].toString());				 
				}

				if(obj[10]!=null && (obj[8].toString().equals("CC") || obj[8].toString().equals("CS") || obj[8].toString().equals("CI") || obj[8].toString().equals("CW") || obj[8].toString().equals("CH"))) 
				{				 
					dronaemails.add(obj[10].toString());				 
				}

			}
			String [] Email = emails.toArray(new String[emails.size()]);
			String [] DronaEmail = dronaemails.toArray(new String[dronaemails.size()]);
			String subject=committeemaindata[8] + " " +" Committee Formation Letter";
			int count=0;
			//			if(Email!=null && Email.length>0){
			//			for(String email:Email) {
			//			count= count +	cm.sendMessage(email, subject, Message);
			//			}
			//			}
			if(Email.length>0) {
				System.out.println("SAy HIiiii");
				count = cm.sendMessage(Email,subject,Message);
			}
			if(DronaEmail.length>0) {
				//long result = cm.sendMessage1(DronaEmail,subject,Message);
			}
			if (count>0) 
			{
				redir.addAttribute("result", " Committee Formation Letter Sent Successfully !! ");
			}
			redir.addFlashAttribute("committeemainid",committeemaindata[0].toString());
		}
		catch (Exception e) 
		{
			e.printStackTrace(); 
			logger.error(new Date() +"Inside SendFormationLetter.htm "+UserId,e);
		}
		return "redirect:/CommitteeMainMembers.htm";
	}




	//	@RequestMapping(value="SendInvitationLetter.htm",method=RequestMethod.POST)
	//	public String SendInvitationLetter(HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception
	//	{		
	//		String UserId=(String)ses.getAttribute("Username");
	//		logger.info(new Date() +"Inside SendInvitationLetter.htm "+UserId);
	//		
	//		try {
	//			String committeescheduleid=req.getParameter("committeescheduleid");
	//			 
	//			List<Object[]> committeeinvitedlist=service.CommitteeAtendance(committeescheduleid);
	//			
	//			
	//			
	//			Object[] scheduledata=service.CommitteeScheduleEditData(committeescheduleid);
	//			SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
	//			ArrayList<String> emails= new ArrayList<String>();
	//			ArrayList<String> membertypes=new ArrayList<String>(Arrays.asList("CC","CS","PS","CI","I","P"));
	//
	//			for(Object[] obj : committeeinvitedlist) 
	//			{	 
	//				if(membertypes.contains(obj[3].toString()) && obj[9].toString().equals("N")) 
	//				{
	//					if(obj[8]!=null) {
	//						emails.add(obj[8].toString());
	//					}
	//				}
	//			}
	//			
	//			String LabCode =(String) ses.getAttribute("labcode");
	//			String ProjectName="General Type";
	//			if(!scheduledata[9].toString().equalsIgnoreCase("0")) 
	//			{
	//				ProjectName= service.projectdetails(scheduledata[9].toString())[1].toString();
	//			}
	//			 
	//			String [] Email = emails.toArray(new String[emails.size()]);
	//			 
	//			MimeMessage msg = javaMailSender.createMimeMessage();
	//			MimeMessageHelper helper = new MimeMessageHelper(msg, true);
	//			helper.setTo(Email);
	//			String Message="Sir/Madam<br><p>&emsp;&emsp;&emsp;&emsp;&emsp;This is to inform you that Meeting is Scheduled for the  <b>"+ scheduledata[7]  + "(" + scheduledata[8] + ")" +"</b> committee of <b>"+ ProjectName +"</b> and further details about the meeting is mentioned below. </p> <table style=\"align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; max-width: 650px; font-size: 16px; border-collapse:collapse;\" >"
	//			 		+ "<tr><th colspan=\"2\" style=\"text-align: left; font-weight: 700; width: 650px;border: 1px solid black; padding: 5px; padding-left: 15px\">Meeting Details </th></tr>"
	//			 		 + "<tr><td style=\"border: 1px solid black; padding: 5px;text-align: left\"> Date :  </td>"
	//			 		 + "<td style=\"border: 1px solid black; padding: 5px;text-align: left\">" + sdf.format(scheduledata[2])+","+LocalDate.parse(scheduledata[2].toString()).getDayOfWeek()+"</td></tr>"
	//			 		 
	//			 		 +"<tr>"
	//					 + "<td style=\"border: 1px solid black; padding: 5px;text-align: left\"> Time : </td>"
	//					 + "<td style=\"border: 1px solid black; padding: 5px;text-align: left\">" + scheduledata[3]  + "</td></tr>"
	//					 +"<tr>"
	//					 +"<td style=\"border: 1px solid black; padding: 5px;text-align: left\"> Venue</td>"
	//					 +"<td style=\"border: 1px solid black; padding: 5px;text-align: left\">"+ scheduledata[12] +"</td>"
	//					 +"</tr>"
	//					 +"<p style=\"font-weight:bold;font-size:13px;\">[Note:This is an autogenerated e-mail.Reply to this will not be attended please.]</p>"
	//					 +"<p>Regards</p>"
	//					 +"<p>"+LabCode+ "- PMS Team"+"</p>"
	//					 ;
	//					 
	//			if (Email.length>0) {
	//				
	//				try{
	//					helper.setSubject( scheduledata[8] + " " +" Committee Invitation Letter");
	//					helper.setText( Message , true);
	//					javaMailSender.send(msg); 
	//					service.UpdateCommitteeInvitationEmailSent(committeescheduleid);
	//					redir.addAttribute("result", " Committee Invitation Letter Sent Successfully !! ");
	//				}catch (MailAuthenticationException e) {
	//					redir.addAttribute("resultfail", " Host Email Authentication Failed, Unable to Send Invitations !!");
	//				}
	//				
	//			} 
	//			 
	//			 redir.addFlashAttribute("committeescheduleid",committeescheduleid);
	//			 
	//			
	//		 }
	//		 catch (Exception e) {
	//				
	//			 	e.printStackTrace(); 
	//			 	logger.error(new Date() +"Inside SendInvitationLetter.htm "+UserId,e);
	//			}			 
	//		 return "redirect:/CommitteeInvitations.htm";
	//		 }
	//	
	// new method sending email 

	@RequestMapping(value="SendInvitationLetter.htm",method=RequestMethod.POST)
	public String SendInvitationLetter(HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception
	{		
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside SendInvitationLetter.htm "+UserId);
		String committeescheduleid=req.getParameter("committeescheduleid");
		int count=0;
		try {


			List<Object[]> committeeinvitedlist=service.CommitteeAtendance(committeescheduleid,"0");

			// Prudhvi - 05/03/2024
			/* ------------------ start ----------------------- */
			Object[] scheduledata = null;
			String rodflag = req.getParameter("rodflag");
			String ccmFlag = req.getParameter("ccmFlag");
			String rodmessagehandle = "";
			if(rodflag!=null && rodflag.equalsIgnoreCase("Y")) {
				scheduledata = rodservice.RODScheduleEditData(committeescheduleid);
				rodmessagehandle = "Record of Discussion of";
			}else {
				scheduledata = service.CommitteeScheduleEditData(committeescheduleid);
				rodmessagehandle = "(" + scheduledata[8] + ")" +"</b> committee of <b>";
			}
			/* ------------------ end ----------------------- */

			SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
			ArrayList<String> emails= new ArrayList<String>();
			ArrayList<String> dronaemails= new ArrayList<String>();
			ArrayList<String> membertypes=new ArrayList<String>(Arrays.asList("CC","CS","PS","CI","I","P", "CH"));

			for(Object[] obj : committeeinvitedlist) 
			{	 
				if(membertypes.contains(obj[3].toString()) && obj[9].toString().equals("N")) 
				{
					if(obj[8]!=null) {
						emails.add(obj[8].toString());
					}

					if(obj[13]!=null) {
						dronaemails.add(obj[13].toString());
					}
				}
			}

			String LabCode =(String) ses.getAttribute("labcode");
			String ProjectName="General Type";
			if(!scheduledata[9].toString().equalsIgnoreCase("0")) 
			{
				ProjectName= service.projectdetails(scheduledata[9].toString())[1].toString();
			}

			if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {
				ProjectName = "";
			}

			String [] Email = emails.toArray(new String[emails.size()]);
			String [] DronaEmail = dronaemails.toArray(new String[dronaemails.size()]);
			String Message="Sir/Madam<br><p>&emsp;&emsp;&emsp;&emsp;&emsp;This is to inform you that Meeting is Scheduled for the  <b>"+ scheduledata[7]  + rodmessagehandle + ProjectName +"</b> and further details about the meeting is mentioned below. </p> <table style=\"align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; max-width: 650px; font-size: 16px; border-collapse:collapse;\" >"
					+ "<tr><th colspan=\"2\" style=\"text-align: left; font-weight: 700; width: 650px;border: 1px solid black; padding: 5px; padding-left: 15px\">Meeting Details </th></tr>"
					+ "<tr><td style=\"border: 1px solid black; padding: 5px;text-align: left\"> Date :  </td>"
					+ "<td style=\"border: 1px solid black; padding: 5px;text-align: left\">" + sdf.format(scheduledata[2])+","+LocalDate.parse(scheduledata[2].toString()).getDayOfWeek()+"</td></tr>"

			 		 +"<tr>"
			 		 + "<td style=\"border: 1px solid black; padding: 5px;text-align: left\"> Time : </td>"
			 		 + "<td style=\"border: 1px solid black; padding: 5px;text-align: left\">" + scheduledata[3]  + "</td></tr>"
			 		 +"<tr>"
			 		 +"<td style=\"border: 1px solid black; padding: 5px;text-align: left\"> Venue</td>"
			 		 +"<td style=\"border: 1px solid black; padding: 5px;text-align: left\">"+ scheduledata[12] +"</td>"
			 		 +"</tr>"
			 		 +"<p style=\"font-weight:bold;font-size:13px;\">[Note:This is an autogenerated e-mail.Reply to this will not be attended please.]</p>"
			 		 +"<p>Regards</p>"
			 		 +"<p>"+LabCode+ "- PMS Team"+"</p>"
			 		 ;

			if (Email.length>0) {

				try{

					String subject=scheduledata[8] + " " +" Committee Invitation Letter";
					count=cm.sendMessage(Email, subject, Message);
					if(DronaEmail.length>0) {
						//long result=cm.sendMessage1(DronaEmail, subject, Message);
					}
					//service.UpdateCommitteeInvitationEmailSent(committeescheduleid);
					if(count>0) {
						redir.addAttribute("result", " Committee Invitation Letter Sent Successfully !! ");
					}

				}catch (MailAuthenticationException e) {
					System.out.println("committeescheduleid ---klsdjfkso "+committeescheduleid);
					e.printStackTrace();
					redir.addAttribute("committeescheduleid",committeescheduleid);
					redir.addAttribute("resultfail", " Host Email Authentication Failed, Unable to Send Invitations !!");
				}

			} 
			
			


		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside SendInvitationLetter.htm "+UserId,e);
		}
		
		if(count>0) {
			redir.addAttribute("result", " Committee Invitation Letter Sent Successfully !! ");
		}else {
			redir.addAttribute("resultfail", " Host Email Authentication Failed, Unable to Send Invitations !!");
		}
		redir.addAttribute("committeescheduleid",committeescheduleid);

		// Prudhvi - 05/03/2024
		/* ------------------ start ----------------------- */
		String rodflag = req.getParameter("rodflag");
		if(rodflag!=null && rodflag.equalsIgnoreCase("Y")) {
			return "redirect:/RODInvitations.htm";
		}
		/* ------------------ end ----------------------- */

		redir.addAttribute("ccmFlag", req.getParameter("ccmFlag"));
		return "redirect:/CommitteeInvitations.htm";
	}

	@RequestMapping(value = "TotalMeetingReport.htm", method = {RequestMethod.GET })
	public String TotalMeetingReport(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception 
	{
		String UserId =(String)ses.getAttribute("Username");
		String LoginType =(String)ses.getAttribute("LoginType");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String LabCode =(String) ses.getAttribute("labcode");
		String projectid = req.getParameter("projectid");
		String committeeid=req.getParameter("committeeid");
		logger.info(new Date() +"Inside TotalMeetingReport.htm "+UserId);

		try {


			String loginid= ses.getAttribute("LoginId").toString();
			List<Object[]> projectdetailslist = service.LoginProjectDetailsList(EmpId, LoginType, LabCode);
			req.setAttribute("ProjectList", projectdetailslist);
			if(committeeid==null)
			{
				committeeid="A";
			}

			if(projectid==null || projectid.equals("null"))
			{
				projectid=projectdetailslist.get(0)[0].toString();

			}

			if(committeeid==null || committeeid.equals("A"))
			{		
				req.setAttribute("meetinglistAll", service.MeettingList( projectid));
			} 
			else {

				List<Object[]> meetinglist = service.MeettingList(committeeid, projectid);
				req.setAttribute("meetinglist", meetinglist);
				List<String>list=new ArrayList<String>();
				if(meetinglist.size()>0) {
					list=meetinglist.stream().map(i -> i[0].toString()).collect(Collectors.toList());
				}
			}
			List<Object[]> projapplicommitteelist = service.ProjectApplicableCommitteeList(projectid);
			req.setAttribute("projapplicommitteelist", projapplicommitteelist);
			
			req.setAttribute("projectid", projectid);
			req.setAttribute("committeeid", committeeid);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside TotalMeetingReport.htm "+UserId, e);
		}
		return "committee/TotalMeetingReport";
	}	


	//	@RequestMapping(value = "TotalMeetingReport.htm", method = {RequestMethod.GET })
	//	public String TotalMeetingReport(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception 
	//	{
	//		String UserId =(String)ses.getAttribute("Username");
	//		String LoginType =(String)ses.getAttribute("LoginType");
	//		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
	//		String LabCode =(String) ses.getAttribute("labcode");
	//		logger.info(new Date() +"Inside TotalMeetingReport.htm "+UserId);
	//		try {
	//		
	//		FormatConverter fc=new FormatConverter();
	//		SimpleDateFormat sdf=fc.getRegularDateFormat();
	//		SimpleDateFormat sdf1=fc.getSqlDateFormat();
	//		
	//		String fdate=req.getParameter("fdate");
	//		String tdate=req.getParameter("tdate");
	//		String Emp=req.getParameter("EmpId");
	//		String Project=req.getParameter("Project");
	//		if(fdate==null)
	//		{
	//			fdate=LocalDate.now().toString();
	//			Emp="A";
	//			Project="";
	//		}else
	//		{
	//			fdate=sdf1.format(sdf.parse(fdate));
	//		}		
	//		if(tdate==null)
	//		{
	//			tdate=LocalDate.now().plusMonths(1).toString();
	//		}
	//		else 
	//		{	
	//			tdate=sdf1.format(sdf.parse(tdate));				
	//		}
	//		req.setAttribute("tdate",tdate);
	//		req.setAttribute("fdate",fdate);
	//		
	//		String loginid= ses.getAttribute("LoginId").toString();
	//		req.setAttribute("ProjectList", service.LoginProjectDetailsList(EmpId,LoginType,LabCode));
	//		req.setAttribute("EmployeeList", service.EmployeeList(LabCode));
	//		req.setAttribute("Project",Project);
	//		req.setAttribute("Employee", Emp);
	//		req.setAttribute("EmpId", EmpId);
	//		req.setAttribute("LoginType", LoginType);
	//		List<Object[]> TotalMeetingReportListAll= service.MeetingReportListAll(fdate,tdate,Project);
	//		req.setAttribute("TotalMeetingReportListAll", TotalMeetingReportListAll );
	//		}
	//		catch (Exception e) {
	//			e.printStackTrace();
	//			logger.error(new Date() +" Inside TotalMeetingReport.htm "+UserId, e);
	//		}
	//		return "committee/TotalMeetingReport";
	//	}
	//	
	//	@RequestMapping(value = "TotalMeetingReport.htm", method = { RequestMethod.POST})
	//	public String TotalMeetingReportSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
	//			throws Exception {
	//		String UserId =(String)ses.getAttribute("Username");
	//		String LoginType =(String)ses.getAttribute("LoginType");
	//		String LabCode =(String) ses.getAttribute("labcode");
	//		logger.info(new Date() +"Inside TotalMeetingReport.htm "+UserId);		
	//		try {
	//		
	//		FormatConverter fc=new FormatConverter();
	//		SimpleDateFormat sdf=fc.getRegularDateFormat();
	//		SimpleDateFormat sdf1=fc.getSqlDateFormat();
	//		
	//		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
	//		String fdate=req.getParameter("fdate");
	//		String tdate=req.getParameter("tdate");
	//		String Emp=req.getParameter("EmpId");
	//		String Project=req.getParameter("Project");
	//		
	//		fdate=sdf1.format(sdf.parse(fdate));
	//
	//		tdate=sdf1.format(sdf.parse(tdate));				
	//	
	//		req.setAttribute("tdate",tdate);
	//		req.setAttribute("fdate",fdate);
	//		
	//		String loginid= ses.getAttribute("LoginId").toString();
	//		req.setAttribute("ProjectList", service.LoginProjectDetailsList(loginid,LoginType,LabCode));
	//		req.setAttribute("EmployeeList", service.EmployeeList(LabCode));
	//		req.setAttribute("Project",Project);
	//		req.setAttribute("Employee", Emp);
	//		req.setAttribute("EmpId", EmpId);
	//		req.setAttribute("LoginType", LoginType);
	//		
	//		List<Object[]> TotalMeetingReportListAll= service.MeetingReportListAll(fdate,tdate,Project);
	//		req.setAttribute("TotalMeetingReportListAll", TotalMeetingReportListAll );
	//		
	//		if(!Emp.toString().equalsIgnoreCase("A")) {
	//			
	//			TotalMeetingReportListAll= service.MeetingReportListEmp(fdate,tdate,Project,Emp);
	//			
	//			req.setAttribute("TotalMeetingReportListAll", TotalMeetingReportListAll );
	//		}
	//
	//		}
	//		catch (Exception e) {
	//			e.printStackTrace();
	//			logger.error(new Date() +" Inside TotalMeetingReport.htm "+UserId, e);
	//		}
	//		return "committee/TotalMeetingReport";
	//	}


	@RequestMapping(value = "ExternalEmployeeListFormation.htm", method = RequestMethod.GET)
	public @ResponseBody String ExternalEmployeeListFormation(HttpServletRequest req,HttpSession ses) throws Exception {

		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside ExternalEmployeeListFormation.htm"+ UserId);

		List<Object[]> ExternalEmployeeList= new ArrayList<Object[]>();

		ExternalEmployeeList = service.ExternalEmployeeListFormation(req.getParameter("CpLabCode"),req.getParameter("committeemainid"));

		Gson json = new Gson();
		return json.toJson(ExternalEmployeeList);	
	}

	@RequestMapping(value = "ChairpersonEmployeeListFormation.htm", method = RequestMethod.GET)
	public @ResponseBody String ChairpersonEmployeeListFormation(HttpServletRequest req,HttpSession ses) throws Exception 
	{
		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside ChairpersonEmployeeListFormation.htm"+ UserId);

		List<Object[]> EmployeeList = new ArrayList<Object[]>();

		try {
			String CpLabCode =req.getParameter("CpLabCode");
			String committeemainid = req.getParameter("committeemainid");
			if(CpLabCode.trim().equalsIgnoreCase("@EXP")) 
			{
				EmployeeList = service.ClusterExpertsList(committeemainid);
			}
			else
			{
				EmployeeList = service.ChairpersonEmployeeListFormation(CpLabCode.trim(),committeemainid);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside ChairpersonEmployeeListFormation.htm "+UserId, e);
		}

		Gson json = new Gson();
		return json.toJson(EmployeeList);	
	}



	@RequestMapping(value = "ExternalEmployeeListInvitations.htm", method = RequestMethod.GET)
	public @ResponseBody String ExternalEmployeeListInvitations(HttpServletRequest req,HttpSession ses) throws Exception
	{
		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside ExternalEmployeeListInvitations.htm"+ UserId);

		List<Object[]> ExternalEmployeeList = new ArrayList<Object[]>();

		String LabCode =req.getParameter("LabCode");

		ExternalEmployeeList = service.ExternalEmployeeListInvitations(LabCode,req.getParameter("scheduleid"));

		Gson json = new Gson();
		return json.toJson(ExternalEmployeeList);	
	}

	@RequestMapping(value = "SpecialEmployeeListInvitations.htm", method = RequestMethod.GET)
	public @ResponseBody String SpecialEmployeeListInvitations(HttpServletRequest req,HttpSession ses) throws Exception
	{
		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside SpecialEmployeeListInvitations.htm"+ UserId);

		List<Object[]> SpecialEmployeeListInvitations = new ArrayList<Object[]>();

		String LabCode =req.getParameter("LabCode");

		SpecialEmployeeListInvitations = service.SpecialEmployeeListInvitations(LabCode,req.getParameter("scheduleid"));

		Gson json = new Gson();
		return json.toJson(SpecialEmployeeListInvitations);	
	}

	@RequestMapping(value = "MeetingsStatusReports.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String MeetingsStatusReports(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
			throws Exception {	

		String UserId =(String)ses.getAttribute("Username");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside MeetingsStatusReports.htm "+UserId);		
		try {
			String projectid=req.getParameter("projectid");
			if(projectid==null)
			{
				projectid="0";

			}
			String loginid= ses.getAttribute("LoginId").toString();
			String Logintype= (String)ses.getAttribute("LoginType");

			if(Logintype.equals("A") || Logintype.equals("Z") || Logintype.equals("Y") || Logintype.equalsIgnoreCase("C")|| Logintype.equalsIgnoreCase("I"))
			{	//all projects for admin, associate director and director
				req.setAttribute("projectslist", service.allprojectdetailsList());
			} 
			else if(Logintype.equals("P"))
			{
				List<Object[]> projectlist= service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
				if(projectlist.size()==0) {

					redir.addAttribute("resultfail", "No Project is Assigned to you.");

					return "redirect:/MainDashBoard.htm";
				}
				req.setAttribute("projectslist",projectlist );
			}
			req.setAttribute("projectid",projectid);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside MeetingsStatusReports.htm "+UserId, e);
		}
		return "committee/MeetingsStatusReports";
	}

	@RequestMapping(value = "MeetingsCount.htm", method = RequestMethod.GET)
	public @ResponseBody String MeetingsCount(HttpServletRequest req, HttpSession ses) throws Exception {
		Gson json = new Gson();
		Object[] ItemDescriptionSearchLedger=null;
		String UserId =(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside MeetingsCount.htm "+UserId);		
		try {		

			ItemDescriptionSearchLedger =   service.ProjectBasedMeetingStatusCount(req.getParameter("projectid"));

		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside MeetingsCount.htm "+UserId, e);
		}
		return json.toJson(ItemDescriptionSearchLedger);

	}

	@RequestMapping(value = "MeetingStatusWiseReport.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String MeetingStatusWiseReport(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{
		String UserId =(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside MeetingStatusWiseReport.htm "+UserId);		
		try {
			String projectid=req.getParameter("projectid");
			String statustype=req.getParameter("statustype");
			List<Object[]> meetingstatuslist= service.PfmsMeetingStatusWiseReport(projectid,statustype);
			req.setAttribute("meetingstatuslist",meetingstatuslist);
			req.setAttribute("projectid",projectid);
			req.setAttribute("statustype",statustype);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside MeetingStatusWiseReport.htm "+UserId, e);
		}

		return "committee/MeetingsStatusWiseList";
	}


	@RequestMapping(value = "ProjectCommitteeDescriptionTOREdit.htm")
	public String ProjectCommitteeDescriptionTOREdit(Model model,HttpServletRequest req,HttpServletResponse res,HttpSession ses)
	{
		String UserId =(String)ses.getAttribute("Username");
		Object[] ProjectCommitteeDescriptionTOR=null;
		logger.info(new Date() +"Inside ProjectCommitteeDescriptionTOREdit.htm "+UserId);		
		try {

			String committeemainid=req.getParameter("committeemainid");
			if(committeemainid==null) {
				Map md=model.asMap();
				committeemainid=(String)md.get("committeemainid");
			}	
			Object[] committeemaindata= service.CommitteMainData(committeemainid);
			String committeeid=committeemaindata[1].toString();
			String projectid=committeemaindata[2].toString() ;
			String divisionid=committeemaindata[3].toString() ;
			String initiationid=committeemaindata[4].toString() ;
			String carsInitiationId=committeemaindata[13]!=null?committeemaindata[13].toString(): "0" ;

			if(projectid!=null && Long.parseLong(projectid)>0)
			{
				ProjectCommitteeDescriptionTOR=service.ProjectCommitteeDescriptionTOR(projectid, committeeid);
			}
			else if(divisionid!=null && Long.parseLong(divisionid)>0)
			{
				ProjectCommitteeDescriptionTOR=service.DivisionCommitteeDescriptionTOR(divisionid, committeeid);
			}else if(Long.parseLong(initiationid)>0)
			{					
				ProjectCommitteeDescriptionTOR = service.InitiationCommitteeDescriptionTOR(initiationid,committeeid);
			}else if(Long.parseLong(carsInitiationId)>0)
			{
				ProjectCommitteeDescriptionTOR = service.carsCommitteeDescriptionTOR(carsInitiationId, committeeid);
			}
			req.setAttribute("committeeprojectdata", ProjectCommitteeDescriptionTOR);
			req.setAttribute("committeemaindata", committeemaindata);
			
			return "committee/CommitteeProjectDescriptionTOREdit";
		
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside ProjectCommitteeDescriptionTOREdit.htm "+UserId, e);
			return "static/Error";
		}
	}


	@RequestMapping(value = "ProjectCommitteeDescriptionTOREditSubmit.htm")
	public String ProjectCommitteeDescriptionTOREditSubmit(HttpServletRequest req,HttpServletResponse res,HttpSession ses,RedirectAttributes redir)
	{
		String UserId =(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside ProjectCommitteeDescriptionTOREditSubmit.htm "+UserId);		
		try {
			String committeemainid=req.getParameter("committeemainid");
			Object[] committeemaindata= service.CommitteMainData(committeemainid);
			String committeeid=committeemaindata[1].toString();
			String projectid=committeemaindata[2].toString() ;
			String divisionid=committeemaindata[3].toString() ;
			String initiationid=committeemaindata[4].toString() ;
			String carsInitiationId=committeemaindata[13]!=null?committeemaindata[13].toString(): "0" ;
			long count=0;
			if(Long.parseLong(projectid)>0)
			{
				CommitteeProject  committeeproject = new CommitteeProject();
				committeeproject.setCommitteeProjectId(Long.parseLong(req.getParameter("committeeprojectid")));
				committeeproject.setDescription(req.getParameter("description"));
				committeeproject.setTermsOfReference(req.getParameter("TOR"));
				committeeproject.setModifiedBy(UserId);			

				count= service.ProjectCommitteeDescriptionTOREdit(committeeproject);
			}else if(Long.parseLong(divisionid)>0)
			{
				CommitteeDivision committeedivision = new CommitteeDivision(); 
				committeedivision.setCommitteeDivisionId(Long.parseLong(req.getParameter("committeeprojectid")));
				committeedivision.setDescription(req.getParameter("description"));
				committeedivision.setTermsOfReference(req.getParameter("TOR"));
				committeedivision.setModifiedBy(UserId);
				count= service.DivisionCommitteeDescriptionTOREdit(committeedivision);
			}else if(Long.parseLong(initiationid)>0)
			{
				CommitteeInitiation committeeinitiation = new CommitteeInitiation(); 
				committeeinitiation.setCommitteeInitiationId(Long.parseLong(req.getParameter("committeeprojectid")));
				committeeinitiation.setDescription(req.getParameter("description"));
				committeeinitiation.setTermsOfReference(req.getParameter("TOR"));
				committeeinitiation.setModifiedBy(UserId);
				count= service.InitiationCommitteeDescriptionTOREdit(committeeinitiation);
			}else if(Long.parseLong(carsInitiationId)>0)
			{
				String committeeprojectid = req.getParameter("committeeprojectid");
				Long comCARSInitiationId = committeeprojectid!=null?Long.parseLong(committeeprojectid): 0;
				CommitteeCARS committeeCARS = comCARSInitiationId>0? service.getCommitteeCARSById(comCARSInitiationId+"") : new CommitteeCARS(); 
				if(comCARSInitiationId==0) {
					committeeCARS.setCARSInitiationId(Long.parseLong(carsInitiationId));
					committeeCARS.setCommitteeId(Long.parseLong(committeeid));
					committeeCARS.setAutoSchedule("N");
					committeeCARS.setCreatedBy(UserId);
					committeeCARS.setCreatedDate(LocalDate.now().toString());
				}else {
					committeeCARS.setModifiedBy(UserId);
					committeeCARS.setModifiedDate(LocalDate.now().toString());
				}
				
				committeeCARS.setDescription(req.getParameter("description"));
				committeeCARS.setTermsOfReference(req.getParameter("TOR"));
				
				count= service.addCommitteeCARS(committeeCARS);
			}

			if (count > 0) {
				redir.addAttribute("result", "Description and Terms of Reference Edited Successfully");
			} else {
				redir.addAttribute("resultfail", "Description and Terms of Reference Edit unSuccessfull");
			}
			redir.addAttribute("committeemainid",committeemainid);
			
			return "redirect:/ProjectCommitteeDescriptionTOREdit.htm";
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside ProjectCommitteeDescriptionTOREditSubmit.htm "+UserId, e);
			return "static/Error";
		}
		
	}

	@RequestMapping(value = "AgendasFromPreviousMeetingsAdd.htm")
	public String AgendasFromPreviousMeetingsAdd(HttpServletRequest req,HttpServletResponse res,HttpSession ses,RedirectAttributes redir)
	{
		String UserId =(String)ses.getAttribute("Username");
		String LabCode =(String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside AgendasFromPreviousMeetingsAdd.htm "+UserId);		
		try {
			String scheduleidto = req.getParameter("scheduleidto");
			String meetingid = req.getParameter("meetingid");
			String scheduleidfrom=req.getParameter("scheduleidfrom");
			String search = req.getParameter("search");

			if(search!=null)
			{
				req.setAttribute("meetingsearch", service.MeetingSearchList(search,LabCode));					
			}

			if(scheduleidfrom!=null)
			{
				req.setAttribute("committeescheduledata1",service.CommitteeScheduleEditData(scheduleidfrom));
				req.setAttribute("agendalist",service.AgendaList(scheduleidfrom));
				req.setAttribute("meetingid",meetingid);
			}			

			req.setAttribute("scheduleidto",scheduleidto);
			req.setAttribute("committeescheduleeditdata",service.CommitteeScheduleEditData(scheduleidto));

		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside AgendasFromPreviousMeetingsAdd.htm "+UserId, e);
		}
		return "committee/CommitteePreviousAgendasAdd";
	}



	@RequestMapping(value = "CommitteePreviousAgendaAddSubmit.htm")
	public String CommitteePreviousAgendaAddSubmit(HttpServletRequest req,HttpServletResponse res,HttpSession ses,RedirectAttributes redir)
	{
		String UserId =(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteePreviousAgendaAddSubmit.htm "+UserId);		
		try {
			String scheduleidto=req.getParameter("scheduleidto");
			//String scheduleidfrom=req.getParameter("scheduleidfrom");
			String[] fromagendaids=req.getParameterValues("fromagendaid");


			long count=service.CommitteePreviousAgendaAdd(scheduleidto, fromagendaids, UserId);
			if (count > 0) {
				redir.addAttribute("result", "Agenda From Previous Meeting Added Successfull");
			} else {
				redir.addAttribute("resultfail", "Agenda From Previous Meeting Add Unsuccessfull");
			}			
			redir.addFlashAttribute("scheduleid",scheduleidto);				
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CommitteePreviousAgendaAddSubmit.htm "+UserId, e);
		}
		return "redirect:/CommitteeScheduleAgenda.htm";
	}


	@RequestMapping(value = "NonProjectCommitteeAutoSchedule.htm")
	public String NonProjectCommitteeAutoSchedule(Model model,HttpServletRequest req,HttpServletResponse res,HttpSession ses,RedirectAttributes redir)
	{	
		String UserId =(String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode"); 
		logger.info(new Date() +"Inside NonProjectCommitteeAutoSchedule.htm "+UserId);		
		try {
			String fromdate=req.getParameter("fromdate");			
			String committeeid = req.getParameter("committeeid");
			String Dashboard="nondashboard";

			if(committeeid==null) {
				Map md=model.asMap();
				committeeid=(String)md.get("committeeid");
			}	


			Object[] committeedata= service.CommitteeDetails(committeeid);
			/*------------------------------------------------------------------------------------------*/		
			if(fromdate!=null)
			{
				String todate=req.getParameter("todate");
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
				LocalDate StartDate=LocalDate.parse(fromdate, formatter);
				LocalDate EndDate=LocalDate.parse(todate, formatter);
				long count=0;

				while(StartDate.isBefore(EndDate) )
				{	  	
					CommitteeScheduleDto committeescheduledto=new CommitteeScheduleDto(); 
					committeescheduledto.setCommitteeId(Long.parseLong(committeeid));
					committeescheduledto.setScheduleDate(req.getParameter("startdate"));
					committeescheduledto.setScheduleStartTime(req.getParameter("starttime"));
					committeescheduledto.setCreatedBy(UserId);
					committeescheduledto.setScheduleFlag("MSC");
					committeescheduledto.setProjectId("0");
					committeescheduledto.setConfidential("5");
					committeescheduledto.setDivisionId("0");
					committeescheduledto.setInitiationId("0");
					committeescheduledto.setLabCode(labcode);
					committeescheduledto.setCARSInitiationId("0");
					String formatteddate = StartDate.format(formatter);
					committeescheduledto.setScheduleDate(formatteddate);

					count=service.CommitteeScheduleAddSubmit(committeescheduledto);

					StartDate=StartDate.plusDays(Long.parseLong(committeedata[8].toString()));

					if(committeedata[7].toString().equalsIgnoreCase("N"))
					{
						break;	
					}						
				}
				if (count > 0) {
					req.setAttribute("result", "Auto Scheduling Successfully ");
				} else {
					req.setAttribute("resultfail", "Auto Scheduling Unsuccessful ");
				}	
			}
			/*------------------------------------------------------------------------------------------*/	
			if(req.getParameter("dashboardlink")!=null) {

				Dashboard=req.getParameter("dashboardlink");
			}

			req.setAttribute("committeeid", committeeid);
			req.setAttribute("committeelist", service.CommitteeMainList(labcode));
			req.setAttribute("dashboard", Dashboard);
			req.setAttribute("startdate",service.CommitteeLastScheduleDate(committeeid));
			req.setAttribute("CommitteeAutoScheduleList", service.CommitteeAutoScheduleList("0", "0","0","0"));
			req.setAttribute("committeedata", committeedata);

		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside NonProjectCommitteeAutoSchedule.htm "+UserId, e);
		}
		return "committee/CommitteeAutoScheduleNP";
	}

	@RequestMapping(value="DivisionCommitteeMaster.htm")
	public String DivisionCommitteeMaster( Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode"); 
		logger.info(new Date() +"Inside DivisionCommitteeMaster.htm "+UserId);
		try {
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();		
			String divisionid=req.getParameter("divisionid");
			if(divisionid==null)  {
				Map md=model.asMap();
				divisionid=(String)md.get("divisionid");
			}	

			List<Object[]> divisionlist=service.LoginDivisionList(EmpId);
			if(divisionlist.size()==0) {				
				redir.addAttribute("resultfail", " No Division is Assigned to You");
				return "redirect:/MainDashBoard.htm";
			}

			if(divisionid==null || divisionid.equals("null"))
			{
				divisionid=divisionlist.get(0)[0].toString();
			}			
			List<Object[]> CommitteedivisionAssigned=service.CommitteedivisionAssigned(divisionid);

			req.setAttribute("CommitteeFormationCheckList", service.DivisionCommitteeFormationCheckList(divisionid));
			req.setAttribute("divisionid",divisionid);
			req.setAttribute("initiationid","0");
			req.setAttribute("projectid","0");
			req.setAttribute("divisionlist",divisionlist);
			req.setAttribute("CommitteedivisionAssigned",CommitteedivisionAssigned);
			req.setAttribute("CommitteedivisionNotAssigned", service.CommitteedivisionNotAssigned(divisionid, LabCode ));
		}

		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside DivisionCommitteeMaster.htm "+UserId,e);
			return "ststic/Error";
		}

		return "committee/CommitteeDivisionMaster";
	}

	@RequestMapping(value="DivisionCommitteeAdd.htm",method = RequestMethod.POST)
	public String DivisionCommitteeAdd(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception{

		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside DivisionCommitteeAdd.htm "+UserId);
		try {

			String divisionid=req.getParameter("divisionid");
			String[] Committee=req.getParameterValues("committeeid");

			long count=service.DivisionCommitteeAdd(divisionid,Committee,UserId);

			if (count > 0) {
				redir.addAttribute("result", " Committee Added Successfully");
				redir.addAttribute("divisionid",divisionid);
			} else {
				redir.addAttribute("result", " Committee Add Unsucessful");
			}

		}

		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside DivisionCommitteeAdd.htm "+UserId,e);
		}

		return "redirect:/DivisionCommitteeMaster.htm";
	}


	@RequestMapping(value="DivisionCommitteeDelete.htm",method = RequestMethod.POST)
	public String DivisionCommitteeDelete(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception{

		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside DivisionCommitteeDelete.htm "+UserId);
		try {
			String divisionid=req.getParameter("divisionid");
			if(req.getParameter("sub")!=null) {

				String Option=req.getParameter("sub");

				if(!Option.equalsIgnoreCase("remove")) 
				{
					redir.addFlashAttribute("initiationid",req.getParameter("initiationid"));
					redir.addFlashAttribute("divisionid",divisionid);
					redir.addFlashAttribute("projectid","0");
					redir.addFlashAttribute("committeeid",req.getParameter("sub"));
					return "redirect:/CommitteeMainMembers.htm";

				}
				else 
				{
					String[] committeedivisionids=req.getParameterValues("committeedivisionid");	

					long count=service.DivisionCommitteeDelete(committeedivisionids,UserId);					
					if (count > 0) {						
						redir.addAttribute("result", " Committee Removed Successfully");
						redir.addFlashAttribute("divisionid",divisionid);
					} else {
						redir.addAttribute("result", " Committee Removal Unsucessful");
					}					
				}				
			}						
		}

		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside DivisionCommitteeDelete.htm "+UserId,e);
		}
		return "redirect:/DivisionCommitteeMaster.htm";
	}




	@RequestMapping(value="DivisionBasedSchedule.htm")
	public String DivisionBasedSchedule(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{		
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside DivisionBasedSchedule.htm "+UserId);
		try {
			String divisionid=req.getParameter("divisionid");
			String committeeid=req.getParameter("committeeid");
			List<Object[]> divisiondetailslist=service.LoginDivisionList(EmpId);

			if(committeeid==null)
			{
				committeeid="all";
			}			

			if(divisiondetailslist.size()==0) {

				redir.addAttribute("resultfail", "No Division is Available .");

				return "redirect:/MainDashBoard.htm";
			}
			if(divisionid==null || divisionid.equals("null"))
			{
				divisionid=divisiondetailslist.get(0)[0].toString();
			}

			if(committeeid==null || committeeid.equals("all"))
			{				
				req.setAttribute("divisionchedulelist", service.DivisionScheduleListAll(divisionid));
			} 
			else 
			{
				req.setAttribute("committeedetails",service.CommitteeDetails(committeeid));
				req.setAttribute("divisionchedulelist",service.DivisionCommitteeScheduleList(divisionid, committeeid));				
			}

			List<Object[]> projapplicommitteelist=service.DivisionCommitteeMainList(divisionid);

			req.setAttribute("initiationid","0");
			req.setAttribute("divisionid",divisionid);
			req.setAttribute("committeeid",committeeid);
			req.setAttribute("Projectdetails",divisiondetailslist.get(0));
			req.setAttribute("divisionslist",divisiondetailslist);
			req.setAttribute("projapplicommitteelist",projapplicommitteelist);
		}

		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside DivisionBasedSchedule.htm "+UserId,e);
		}
		return "committee/CommitteeDivisionSchedule";
	}


	@RequestMapping(value="DivCommitteeAutoSchedule.htm",method = RequestMethod.POST)
	public String DivCommitteeAutoSchedule(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside DivCommitteeAutoSchedule.htm "+UserId);
		try
		{			
			String projectid=req.getParameter("projectid");
			String divisionid=req.getParameter("divisionid");
			String initiationid=req.getParameter("initiationid");



			List<Object[]> masterlist = null;
			if(Long.parseLong(divisionid)>0)
			{
				masterlist = service.DivisionMasterList(divisionid);
			}

			if(Long.parseLong(initiationid)>0)
			{
				masterlist = service.InitiaitionMasterList(initiationid);
			}

			req.setAttribute("divisionmasterlist",masterlist );

			req.setAttribute("divisiondata",service.DivisionData(divisionid) );
			req.setAttribute("initiationdata",service.Initiationdetails(initiationid) );
			req.setAttribute("projectid",projectid );
			req.setAttribute("divisionid",divisionid );
			req.setAttribute("initiationid",initiationid );
			for(Object[] obj : masterlist ) {
				if(obj[6].toString().equalsIgnoreCase("N")) 
				{							
					return "committee/DivCommitteeAutoSchedule";
				}
			}

			redir.addAttribute("projectid",projectid);
			redir.addAttribute("divisionid",divisionid);
			redir.addAttribute("initiationid",initiationid);
			return "redirect:/CommitteeAutoScheduleList.htm";

		}catch(Exception e) {	    		
			logger.error(new Date() +" Inside DivCommitteeAutoSchedule.htm" +UserId, e);
			e.printStackTrace();
			return "static/Error";
		}	
	}


	@RequestMapping(value="DivCommitteeAutoScheduleSubmit.htm",method = RequestMethod.POST)
	public String DivCommitteeAutoScheduleSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception{
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode"); 
		logger.info(new Date() +"Inside DivCommitteeAutoScheduleSubmit.htm "+UserId);
		try
		{		
			long count=0;
			long count1=0;
			String projectid=req.getParameter("projectid");
			String divisionid=req.getParameter("divisionid");
			String initiationid=req.getParameter("initiationid");

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate StartDate1=LocalDate.parse(req.getParameter("startdate"), formatter);
			LocalDate EndDate=LocalDate.parse(req.getParameter("enddate"),formatter);
			List<Object[]> masterlist= new ArrayList<Object[]>();
			if(Long.parseLong(divisionid)>0) {
				masterlist= service.DivisionMasterList(divisionid);
			}else if(Long.parseLong(initiationid)>0)
			{
				masterlist= service.InitiationMasterList(initiationid);
			}
			for(int i=0;i<masterlist.size();i++) {
				LocalDate StartDate=StartDate1;
				if(masterlist.get(i)[6].toString().equalsIgnoreCase("N")) {
					String CommitteeId=masterlist.get(i)[2].toString();
					String PeriodicDuration=masterlist.get(i)[5].toString();
					if(Long.parseLong(PeriodicDuration)>0) 
					{					
						while (StartDate.isBefore(EndDate) ) /* || StartDate.isBefore(EndDate) */
						{
							CommitteeScheduleDto committeescheduledto=new CommitteeScheduleDto(); 
							committeescheduledto.setCommitteeId(Long.parseLong(CommitteeId));
							committeescheduledto.setScheduleDate(req.getParameter("startdate"));
							committeescheduledto.setScheduleStartTime(req.getParameter("starttime"));
							committeescheduledto.setCreatedBy(UserId);
							committeescheduledto.setScheduleFlag("MSC");
							committeescheduledto.setProjectId(projectid);
							committeescheduledto.setConfidential("5");
							committeescheduledto.setDivisionId(divisionid);
							committeescheduledto.setInitiationId(initiationid);
							String formattedString2 = StartDate.format(formatter);
							committeescheduledto.setScheduleDate(formattedString2);
							committeescheduledto.setLabCode(labcode);
							count=service.CommitteeScheduleAddSubmit(committeescheduledto);

							if(Long.parseLong(divisionid)>0) {
								count1=service.CommitteeDivisionUpdate(divisionid,CommitteeId);
							}else if(Long.parseLong(initiationid)>0)
							{
								count1=service.CommitteeInitiationUpdate(initiationid,CommitteeId);
							}							
							StartDate=StartDate.plusDays(Long.parseLong(PeriodicDuration));
						}
					}

					if(Long.parseLong(PeriodicDuration)==0) {

						CommitteeScheduleDto committeescheduledto=new CommitteeScheduleDto(); 
						committeescheduledto.setCommitteeId(Long.parseLong(CommitteeId));
						committeescheduledto.setScheduleDate(req.getParameter("startdate"));
						committeescheduledto.setScheduleStartTime(req.getParameter("starttime"));
						committeescheduledto.setCreatedBy(UserId);
						committeescheduledto.setScheduleFlag("MSC");
						committeescheduledto.setProjectId(projectid);
						committeescheduledto.setScheduleDate(req.getParameter("startdate"));
						committeescheduledto.setConfidential("5");
						committeescheduledto.setDivisionId(divisionid);
						committeescheduledto.setInitiationId(initiationid);
						committeescheduledto.setLabCode(labcode);
						count=service.CommitteeScheduleAddSubmit(committeescheduledto);
						if(Long.parseLong(divisionid)>0) {
							count1=service.CommitteeDivisionUpdate(divisionid,CommitteeId);
						}else if(Long.parseLong(initiationid)>0)
						{
							count1=service.CommitteeInitiationUpdate(initiationid,CommitteeId);
						}
					}

					if (count > 0) {
						redir.addAttribute("result", "Committee Auto-Scheduled Successfully");
					} else {
						redir.addAttribute("result", " Committee Auto-Schedule Unsucessful");
					}			
				}		
			}


			redir.addAttribute("initiationid",initiationid);
			redir.addAttribute("projectid", projectid);
			redir.addAttribute("divisionid", divisionid);
			return "redirect:/CommitteeAutoScheduleList.htm";
		}catch(Exception e) {	    		
			logger.error(new Date() +" Inside DivCommitteeAutoScheduleSubmit.htm "+UserId, e);
			e.printStackTrace();
			return "static/Error";
		}	

	}



	@RequestMapping(value="InitiationCommitteeMaster.htm")
	public String InitiationCommitteeMaster( Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode"); 
		logger.info(new Date() +"Inside InitiationCommitteeMaster.htm "+UserId);
		try {
			String initiationid=req.getParameter("initiationid");

			List<Object[]> projectdetailslist=service.InitiatedProjectDetailsList();
			if(projectdetailslist.size()==0) {		
				redir.addAttribute("resultfail", "No Project is Initiated.");
				return "redirect:/MainDashBoard.htm";
			}
			if(initiationid==null || initiationid.equals("null"))
			{
				initiationid=projectdetailslist.get(0)[0].toString();
			}	
			System.out.println(initiationid+"-----");
			List<Object[]> projectmasterlist=service.InitiationMasterList(initiationid);
			req.setAttribute("ProjectCommitteeFormationCheckList", service.InitiationCommitteeFormationCheckList(initiationid));
			req.setAttribute("initiationid",initiationid);
			req.setAttribute("projectid","0");
			req.setAttribute("divisionid","0");
			req.setAttribute("Projectdetails",projectdetailslist.get(0));
			req.setAttribute("ProjectsList",projectdetailslist);
			req.setAttribute("projectmasterlist",projectmasterlist);
			req.setAttribute("committeelist", service.InitiationCommitteesListNotAdded(initiationid,LabCode));
			return "committee/CommitteeInitiationMaster";
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside InitiationCommitteeMaster.htm "+UserId,e);
			return "static/Error";
		}
	}

	@RequestMapping(value="InvitationSerialNoUpdate.htm",method=RequestMethod.POST)
	public String InvitationSerialNoUpdate(HttpServletRequest req,HttpServletResponse res, RedirectAttributes redir, HttpSession ses)throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside InvitationSerialNoUpdate.htm "+UserId);
		try
		{
			String committeescheduleid = req.getParameter("scheduleid");

			String[] newslno=req.getParameterValues("newslno");
			String[] invitationid=req.getParameterValues("invitationid");
			
			String [] Role = req.getParameterValues("Role");
			String [] EmpNo = req.getParameterValues("EmpNo");
			String [] LabCode = req.getParameterValues("LabCode");
			
			if (containsHTMLTags(Role)) {
				redir.addAttribute("committeescheduleid", committeescheduleid);
			    return redirectWithError(redir, "CommitteeAttendance.htm", "'Role' should not contain HTML Tags.!");
			}
			
			
			Set<String> s = new HashSet<String>(Arrays.asList(newslno));

			if (s.size() == newslno.length) 
			{
				service.InvitationSerialnoUpdate(newslno, invitationid);
				redir.addAttribute("result", "Serial No Updated Successfully");

			} else {
				redir.addAttribute("resultfail", "Agenda Priority Updated UnSuccessfull");

			}	
			
			if(EmpNo!=null)
			service.InvitationRoleoUpdate(Role,EmpNo,LabCode,UserId,invitationid);
			
			redir.addAttribute("committeescheduleid", committeescheduleid);
			// Prudhvi - 06/03/2024
			/* ------------------ start ----------------------- */
			String redirpageflag = req.getParameter("redirpageflag");
			if(redirpageflag!=null && redirpageflag.equalsIgnoreCase("ROD")) {
				return"redirect:/RODAttendance.htm";
			}
			/* ------------------ end ----------------------- */

			// CCM Handling
			String ccmFlag = req.getParameter("ccmFlag");
			if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {
				redir.addAttribute("committeeMainId", req.getParameter("committeeMainId"));
				redir.addAttribute("committeeId", req.getParameter("committeeId"));
				redir.addAttribute("ccmFlag", ccmFlag);			
			}

			return "redirect:/CommitteeAttendance.htm";
		}
		catch (Exception e)
		{
			e.printStackTrace(); 
			logger.error(new Date() +"Inside InvitationSerialNoUpdate.htm "+UserId,e);
			return "static/Error";
		}
	}
	@RequestMapping(value="MemberSerialNoUpdate.htm",method=RequestMethod.POST)
	public String MemberSerialNoUpdate(HttpServletRequest req,HttpServletResponse res, RedirectAttributes redir, HttpSession ses)throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside InvitationSerialNoUpdate.htm "+UserId);
		try
		{
			String ccmflag = req.getParameter("ccmflag");
			String prgmflag = req.getParameter("prgmflag");
			String committeemainid = req.getParameter("committeemainid");

			String[] newslno=req.getParameterValues("newslno");
			String[] memberId=req.getParameterValues("memberId");

			Set<String> s = new HashSet<String>(Arrays.asList(newslno));

			if (s.size() == newslno.length) 
			{
				service.MemberSerialNoUpdate(newslno, memberId);
				redir.addAttribute("result", "Serial No Updated Successfully");

			} else {
				redir.addAttribute("resultfail", "Agenda Priority Updated UnSuccessfull");

			}	

			if(ccmflag!=null && ccmflag.equalsIgnoreCase("Y")) {
				redir.addAttribute("committeeMainId", committeemainid);
				redir.addAttribute("committeeId", req.getParameter("committeeId"));

				return "redirect:/CCMCommitteeConstitution.htm";
			}
			
			if(prgmflag!=null && prgmflag.equalsIgnoreCase("Y")) {
				redir.addAttribute("committeeMainId", committeemainid);
				redir.addAttribute("committeeId", req.getParameter("committeeId"));
				redir.addAttribute("programmeId", req.getParameter("programmeId"));
				
				return "redirect:/PrgmCommitteeConstitution.htm";
			}
			
			redir.addAttribute("committeemainid", committeemainid);
			return "redirect:/CommitteeMainMembers.htm";

		}
		catch (Exception e)
		{
			e.printStackTrace(); 
			logger.error(new Date() +"Inside InvitationSerialNoUpdate.htm "+UserId,e);
			return "static/Error";
		}
	}




	@RequestMapping(value = "CommitteeMemberRepDelete.htm", method = RequestMethod.GET)
	public @ResponseBody String CommitteeMemberRepDelete(HttpSession ses, HttpServletRequest req) throws Exception 
	{
		String UserId=(String)ses.getAttribute("Username");
		int ret= 0;
		logger.info(new Date() +"Inside CommitteeMemberRepDelete.htm "+UserId);
		try
		{	  
			String memberrepid=req.getParameter("memrepid");
			ret=service.CommitteeMemberRepDelete(memberrepid);
		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside CommitteeMemberRepDelete.htm "+UserId,e);
		}
		Gson json = new Gson();
		return json.toJson(ret); 

	}

	@RequestMapping(value = "CommitteeRepNotAddedList.htm", method = RequestMethod.GET)
	public @ResponseBody String CommitteeRepNotAddedList(HttpServletRequest req,HttpSession ses) throws Exception {

		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside CommitteeRepNotAddedList.htm"+ UserId);		
		List<Object[]> CommitteeRepNotAddedList = service.CommitteeRepNotAddedList(req.getParameter("committeemainid"));
		Gson json = new Gson();
		return json.toJson(CommitteeRepNotAddedList);	
	}

	@RequestMapping(value="InitiationCommitteeAdd.htm",method = RequestMethod.POST)
	public String InitiationCommitteeAdd(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception{

		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside InitiationCommitteeAdd.htm "+UserId);
		try {

			String ProjectId=req.getParameter("projectid");
			String initiationid=req.getParameter("initiationid");
			String[] Committee=req.getParameterValues("committeeid");

			long count=service.InitiationCommitteeAdd(initiationid,Committee,UserId);

			if (count > 0) {
				redir.addAttribute("result", " Committee Added Successfully");					
			} else {
				redir.addAttribute("result", " Committee Add Unsucessful");
			}							
			redir.addAttribute("projectid",ProjectId);
			redir.addAttribute("initiationid",initiationid);
			return "redirect:/InitiationCommitteeMaster.htm";
		}catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside InitiationCommitteeAdd.htm "+UserId,e);
			return "static/Error";
		}
	}

	@RequestMapping(value="InitiationCommitteeDelete.htm",method = RequestMethod.POST)
	public String InitiationCommitteeDelete(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception{

		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside InitiationCommitteeDelete.htm "+UserId);
		try {
			String initiationid=req.getParameter("initiationid");
			if(req.getParameter("sub")!=null) {

				String Option=req.getParameter("sub");					
				if(!Option.equalsIgnoreCase("remove")) 
				{
					redir.addFlashAttribute("projectid","0");
					redir.addFlashAttribute("divisionid","0");
					redir.addFlashAttribute("initiationid",initiationid);						
					redir.addFlashAttribute("committeeid",req.getParameter("sub"));
					return "redirect:/CommitteeMainMembers.htm";
				}
				else {
					String[] CommitteeProject=req.getParameterValues("committeeprojectid");	

					long count=service.InitiationCommitteeDelete(CommitteeProject,UserId);					
					if (count > 0) {						
						redir.addAttribute("result", " Committee Removed Successfully");

					} else {
						redir.addAttribute("result", " Committee Remove Unsucessful");
					}					
				}
			}
			System.out.println(initiationid+"************");
			redir.addAttribute("projectid","0");
			redir.addAttribute("initiationid",initiationid);
			return "redirect:/InitiationCommitteeMaster.htm";
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside InitiationCommitteeDelete.htm "+UserId,e);
			return "static/Error";
		}

	}

	@RequestMapping(value="InitiationBasedSchedule.htm")
	public String InitiationBasedSchedule(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{		

		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside InitiationBasedSchedule.htm "+UserId);
		try {				
			String initiationid=req.getParameter("initiationid");
			String committeeid=req.getParameter("committeeid");
			List<Object[]> InitiatedProjectDetailsList=service.InitiatedProjectDetailsList();

			if(committeeid==null)
			{
				committeeid="all";
			}			

			if(InitiatedProjectDetailsList.size()==0) {

				redir.addAttribute("resultfail", "No Project Initiation Available .");

				return "redirect:/MainDashBoard.htm";
			}
			if(initiationid==null || initiationid.equals("null"))
			{
				initiationid=InitiatedProjectDetailsList.get(0)[0].toString();
			}
			if(committeeid==null || committeeid.equals("all"))
			{				
				req.setAttribute("divisionchedulelist", service.InitiationScheduleListAll(initiationid));
			} 
			else 
			{
				req.setAttribute("committeedetails",service.CommitteeDetails(committeeid));
				req.setAttribute("divisionchedulelist",service.InitiationCommitteeScheduleList(initiationid, committeeid));				
			}

			List<Object[]> initiationcommitteemainlist=service.InitiationCommitteeMainList(initiationid);				
			req.setAttribute("initiationid",initiationid );
			req.setAttribute("divisionid","0");
			req.setAttribute("committeeid",committeeid);
			req.setAttribute("Projectdetails",InitiatedProjectDetailsList.get(0));
			req.setAttribute("divisionslist",InitiatedProjectDetailsList);
			req.setAttribute("projapplicommitteelist",initiationcommitteemainlist);
		}catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside InitiationBasedSchedule.htm "+UserId,e);
			return "static/Error";
		}
		return "committee/CommitteeInitiationSchedule";
	}

	@RequestMapping(value="CommitteeMainApproval.htm")
	public String CommitteeMainApproval(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{	
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteeMainApproval.htm "+UserId);
		try 
		{
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String LabId =(String)ses.getAttribute("labid"); 
			String redirect=req.getParameter("redirect");
			String operation=req.getParameter("operation");
			String committeemainid=req.getParameter("committeemainid");
			String remark=req.getParameter("remarks");
			String approvalauthority=req.getParameter("approvalauthority");
			long ret=0;
			CommitteeConstitutionApprovalDto approvaldto = new CommitteeConstitutionApprovalDto(); 
			approvaldto.setCommitteeMainId(committeemainid);
			approvaldto.setRemarks(remark);
			approvaldto.setActionBy(UserId);
			approvaldto.setEmpid(EmpId);
			approvaldto.setEmpLabid(LabId);
			approvaldto.setApprovalAuthority(approvalauthority);
			approvaldto.setConstitutionStatus(req.getParameter("newconstitutionstatus"));
			approvaldto.setOperation(operation);

			ret=service.CommitteeMainApprove(approvaldto);
			if(ret==-1)
			{
				redir.addAttribute("resultfail", "Approval Authority Data Not Found");			
				redir.addFlashAttribute("committeemainid",committeemainid);
				return "redirect:/ComConstitutionApprovalDetails.htm";
			}

			if (ret > 0) {						
				redir.addAttribute("result", " Committee "+operation+" Successfully");					
			} else {
				redir.addAttribute("resultfail", " Committee "+operation+" Unsucessful");
			}		
			if(redirect!=null && redirect.equals("1"))
			{
				if (ret > 0) 
				{						
					redir.addAttribute("result", " Committee Forwarded Successfully");					
				} else {
					redir.addAttribute("resultfail", " Committee Forwarding Unsucessful");
				}		
				redir.addFlashAttribute("committeemainid",committeemainid);
				return "redirect:/CommitteeMainMembers.htm";					
			}

			return "redirect:/CommitteeMainApprovalList.htm";
		}			
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside CommitteeMainApproval.htm "+UserId,e);
			return "static/Error";
		}
	}

	@RequestMapping(value="CommitteeMainApprovalList.htm")
	public String CommitteeMainApprovalList(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{	
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteeMainApprovalList.htm "+UserId);
		try 
		{
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String LabId =(String)ses.getAttribute("labid"); 
			String loginid= ses.getAttribute("LoginId").toString();
			req.setAttribute("approvallist", service.ProposedCommitteesApprovalList(loginid,EmpId));
			return "committee/ComConstitutionApprovalList";
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside CommitteeMainApprovalList.htm "+UserId,e);
			return "static/Error";
		}
	}



	@RequestMapping(value="ComConstitutionApprovalDetails.htm")
	public String ComConstitutionApprovalDetails(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside ComConstitutionApprovalDetails.htm "+UserId);
		try 
		{
			String committeemainid=req.getParameter("committeemainid");		
			if(committeemainid==null) 
			{
				Map md=model.asMap();
				committeemainid=(String)md.get("committeemainid");
			}	
			String initiationid=null;
			String CommitteeId=null;
			String projectid=null;
			String divisionid=null;
			Object[] committeedata=service.CommitteMainData(committeemainid);	


			CommitteeId=committeedata[1].toString();
			projectid=committeedata[2].toString();
			divisionid=committeedata[3].toString();
			initiationid=committeedata[4].toString();

			committeemainid=committeedata[0].toString();	
			Object[] proposedcommitteemainid =  service.ProposedCommitteeMainId(committeemainid);

			req.setAttribute("committeemembersall",service.CommitteeAllMembersList(committeemainid));
			req.setAttribute("committeeMemberreplist", service.CommitteeMemberRepList(committeemainid));
			req.setAttribute("committeedata", committeedata);			
			req.setAttribute("divisionid", divisionid);
			req.setAttribute("proposedcommitteemainid",proposedcommitteemainid );
			req.setAttribute("committeeapprovaldata",service.CommitteeMainApprovalData(committeemainid));
			req.setAttribute("approvalstatuslist",service.ApprovalStatusList(committeemainid) );
			req.setAttribute("constitutionapprovalflow", service.ConstitutionApprovalFlow(committeemainid));

			if( Long.parseLong(projectid)>0) {
				req.setAttribute("projectdata", service.projectdetails(projectid));
			}
			if( Long.parseLong(divisionid)>0) {
				req.setAttribute("divisiondata", service.DivisionData(divisionid) );
			}
			if(Long.parseLong(initiationid)>0) {
				req.setAttribute("initiationdata", service.Initiationdetails(initiationid) );
			}

			return "committee/ComConstitutionApprovalDetails";
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside ComConstitutionApprovalDetails.htm "+UserId,e);
			return "static/Error";
		}
	}

	@RequestMapping(value="ComConstitutionApprovalHistory.htm")
	public String ComConstitutionApprovalHistory(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside ComConstitutionApprovalHistory.htm "+UserId);
		try 
		{
			String committeemainid=req.getParameter("committeemainid");		
			if(committeemainid==null) {
				Map md=model.asMap();
				committeemainid=(String)md.get("committeemainid");
			}	

			Object[] committeedata=service.CommitteMainData(committeemainid);	
			String CommitteeId=committeedata[1].toString();
			String projectid=committeedata[2].toString();
			String divisionid=committeedata[3].toString();
			String initiationid=committeedata[4].toString();



			if( Long.parseLong(projectid)>0) {
				req.setAttribute("projectdata", service.projectdetails(projectid));
			}
			if( Long.parseLong(divisionid)>0) {
				req.setAttribute("divisiondata", service.DivisionData(divisionid) );
			}
			if(Long.parseLong(initiationid)>0) {
				req.setAttribute("initiationdata", service.Initiationdetails(initiationid) );
			}
			req.setAttribute("committeedata", committeedata);	
			req.setAttribute("historydata",service.ComConstitutionApprovalHistory(committeemainid) );
			return "committee/ComConApprovalTracking";					
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside ComConstitutionApprovalHistory.htm "+UserId,e);
			return "static/Error";
		}
	}


	@RequestMapping(value="CommitteeScheduleDelete.htm")
	public String CommitteeScheduleDelete(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteeScheduleDelete.htm "+UserId);
		try 
		{
			String scheduleid=req.getParameter("scheduleid");
			CommitteeScheduleDto dto=new CommitteeScheduleDto();
			dto.setScheduleId(Long.parseLong(scheduleid));
			dto.setModifiedBy(UserId);
			Object[] scheduledata=service.CommitteeScheduleEditData(scheduleid);
			String projectid= scheduledata[9].toString();
			String divisionid= scheduledata[16].toString();
			String initiationid= scheduledata[17].toString();
			String carsInitiationId= scheduledata[25].toString();
			String programmeId= scheduledata[26].toString();

			int count=service.CommitteeScheduleDelete(dto);

			if (count > 0) 
			{						
				redir.addAttribute("result", " Meeting Schedule Deleted  Successfully");					
			} 
			else 
			{
				redir.addAttribute("resultfail", " Meeting Delete Unsucessful");
			}		

			if(Long.parseLong(projectid)>0 ) {
				return "redirect:/ProjectBasedSchedule.htm" ;
			}else if( Long.parseLong(divisionid)>0) {
				return "redirect:/DivisionBasedSchedule.htm" ;
			}else if( Long.parseLong(initiationid)>0) {
				return "redirect:/InitiationBasedSchedule.htm" ;
			}else if(Long.parseLong(carsInitiationId)>0) {
				return "redirect:/CARSSchedule.htm";
			}else if(Long.parseLong(programmeId)>0) {
				return "redirect:/PrgmSchedule.htm";
			}else  {
				return "redirect:/CommitteeScheduleList.htm" ;
			}

		}catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside CommitteeScheduleDelete.htm "+UserId,e);
			return "static/Error";
		}
	}

	@RequestMapping(value="CommitteeMinutesNewDownload.htm", method = {RequestMethod.POST,RequestMethod.GET})
	public void CommitteeMinutesNewDownload(HttpServletRequest req,HttpServletResponse res, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		String EmpNo=(String)ses.getAttribute("EmpNo");
		String EmpName=(String)ses.getAttribute("EmpName");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeMinutesNewDownload.htm "+UserId);
		try
		{		
			String committeescheduleid = req.getParameter("committeescheduleid");			
			String PROTECTED_MINUTES = req.getParameter("PROTECTED MINUTES");

			Object[] committeescheduleeditdata=service.CommitteeScheduleEditData(committeescheduleid);
			String projectid= committeescheduleeditdata[9].toString();
			String committeeid=committeescheduleeditdata[0].toString();
			if(Long.parseLong(projectid) >0 && committeescheduleeditdata[22].toString().equals("Y")) 
			{
				CommitteeMeetingDPFMFrozen dpfm = service.getFrozenDPFMMinutes(committeescheduleid);
				//CommitteeProjectBriefingFrozen CPF=service.getFrozenCommitteeMOM(committeescheduleid);

				res.setContentType("application/pdf"); res.setHeader("Content-Disposition",
						"inline; name="+ dpfm.getDPFMFileName()+".pdf; filename"+dpfm.getDPFMFileName()); 
				Path filepath = Paths.get(uploadpath, LabCode, "DPFM", dpfm.getDPFMFileName());
				//						 File f=new File(uploadpath+dpfm.getFrozenDPFMPath()+dpfm.getDPFMFileName());
				File f=filepath.toFile();

				OutputStream out = res.getOutputStream(); 
				FileInputStream in = new FileInputStream(f); 
				byte[] buffer = new byte[4096]; 
				int length; 
				while((length = in.read(buffer)) > 0) 
				{ 
					out.write(buffer, 0, length);
				}
				in.close(); 
				out.close();
			}
			else 
			{

				Object[] projectdetails = null;

				if(projectid!=null && Integer.parseInt(projectid)>0)
				{
					projectdetails = service.projectdetails(projectid);
					req.setAttribute("projectdetails", projectdetails);
				}
				String divisionid= committeescheduleeditdata[16].toString();
				if(divisionid!=null && Integer.parseInt(divisionid)>0)
				{
					req.setAttribute("divisiondetails", service.DivisionData(divisionid));
				}
				String initiationid= committeescheduleeditdata[17].toString();
				if(initiationid!=null && Integer.parseInt(initiationid)>0)
				{
					req.setAttribute("initiationdetails", service.Initiationdetails(initiationid));
				}

				Committee committee = printservice.getCommitteeData(committeeid);
				
				List<Object[]> repList = service.getCommitteeMainRepList(committeescheduleeditdata[1]!=null?committeescheduleeditdata[1].toString():"0");
				List<String> repCodes = new ArrayList<>();
				
				if(repList != null && !repList.isEmpty()){
					repCodes = repList.stream().map(obj -> obj[1]!=null ? obj[1].toString() : null).filter(Objects::nonNull).collect(Collectors.toList());
				}

				req.setAttribute("repCodes", repCodes);
				

				HashMap< String, ArrayList<Object[]>> actionsdata=new LinkedHashMap<String, ArrayList<Object[]>>();
				long lastid=service.getLastPmrcId(projectid, committeeid, committeescheduleid);

				//****************************envi download start**********************************
				List<Object[]> envisagedDemandlist  = new ArrayList<Object[]>();
				envisagedDemandlist=service.getEnvisagedDemandList(projectid);
				req.setAttribute("envisagedDemandlist", envisagedDemandlist);

				req.setAttribute("committeeminutesspeclist",service.CommitteeScheduleMinutes(committeescheduleid) );
				req.setAttribute("committeescheduleeditdata", committeescheduleeditdata);
				req.setAttribute("committeeminutes",service.CommitteeMinutesSpecNew());
				req.setAttribute("committeeinvitedlist", service.CommitteeAtendance(committeescheduleid,"1"));			
				req.setAttribute("labdetails", service.LabDetails(committeescheduleeditdata[24].toString()));
				req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(committeescheduleeditdata[24].toString()));
				req.setAttribute("meetingcount",service.MeetingNo(committeescheduleeditdata));
				//req.setAttribute("milestonedatalevel6", printservice.BreifingMilestoneDetails(projectid,committeeid));

				// this method will show the milestones based on each meeting date
				req.setAttribute("milestonedatalevel6", printservice.BreifingMilestoneDetails(projectid,committeeid,committeescheduleeditdata[2].toString()));

				//				req.setAttribute("committeeminutessub",service.CommitteeMinutesSub());
				//				req.setAttribute("CommitteeAgendaList", service.CommitteeAgendaList(committeescheduleid));
				String LevelId= "2";
				Object[] MileStoneLevelId = printservice.MileStoneLevelId(projectid,committeeid);
				if( MileStoneLevelId!= null) {
					LevelId= MileStoneLevelId[0].toString();
				}
				req.setAttribute("levelid", LevelId);
				req.setAttribute("labInfo", service.LabDetailes(LabCode));
				/*---------------------------------------------------------------------------------------------------------------*/
				//				if(Long.parseLong(projectid) >0 && committeescheduleeditdata[22].toString().equals("N") ) {

				final String localUri=uri+"/pfms_serv/financialStatusBriefing?ProjectCode="+(projectdetails!=null?projectdetails[4].toString():"")+"&rupess="+10000000;
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				headers.set("labcode", LabCode);
				String jsonResult=null;
				try {
					HttpEntity<String> entity = new HttpEntity<String>(headers);
					ResponseEntity<String> response=restTemplate.exchange(localUri, HttpMethod.POST, entity, String.class);
					jsonResult=response.getBody();						
				}catch(Exception e) {
					req.setAttribute("errorMsg", "errorMsg");
				}
				ObjectMapper mapper = new ObjectMapper();
				mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
				List<ProjectFinancialDetails> projectDetails=null;
				if(jsonResult!=null) {
					try {
						projectDetails = mapper.readValue(jsonResult, new TypeReference<List<ProjectFinancialDetails>>(){});
						req.setAttribute("financialDetails",projectDetails);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				}

				final String localUri2=uri+"/pfms_serv/getTotalDemand";

				String jsonResult2=null;
				try {
					HttpEntity<String> entity = new HttpEntity<String>(headers);
					ResponseEntity<String> response=restTemplate.exchange(localUri2, HttpMethod.POST, entity, String.class);
					jsonResult2=response.getBody();						
				}catch(Exception e) {
					req.setAttribute("errorMsg", "errorMsg");
				}
				ObjectMapper mapper2 = new ObjectMapper();
				mapper2.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				mapper2.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
				List<TotalDemand> totaldemand=null;
				if(jsonResult2!=null) {
					try {
						totaldemand = mapper2.readValue(jsonResult2, new TypeReference<List<TotalDemand>>(){});
						req.setAttribute("TotalProcurementDetails",totaldemand);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				}

				List<Object[]> procurementStatusList=(List<Object[]>)service.ProcurementStatusList(projectid);
				List<Object[]> procurementOnDemand=null;
				List<Object[]> procurementOnSanction=null;
				
			    List<List<Object[]>> overallfinance = new ArrayList<>();
			    List<String> Pmainlist = printservice.ProjectsubProjectIdList(projectid);
			    List<Object[]> ProjectDetails = new ArrayList<>();
			    
		    	for(String proId: Pmainlist) {
		    		List<Object[]> projDetails = printservice.ProjectDetails(proId);
		    		ProjectDetails.add(!projDetails.isEmpty() ? projDetails.get(0) : new Object[0]);
		    		overallfinance.add(printservice.getrOverallFinance(proId));
		    	}
		    	req.setAttribute("projectidlist", Pmainlist);
		    	req.setAttribute("overallfinance", overallfinance);
		    	req.setAttribute("ProjectDetails", ProjectDetails);

				if(procurementStatusList!=null){
					Map<Object, List<Object[]>> map = procurementStatusList.stream().collect(Collectors.groupingBy(c -> c[9])); 
					Collection<?> keys = map.keySet();
					for(Object key:keys){
						if(key.toString().equals("D")) {
							procurementOnDemand=map.get(key);
						}else if(key.toString().equals("S")) {
							procurementOnSanction=map.get(key);
						}
					}
				}
				List<Object[]> actionlist= service.MinutesViewAllActionList(committeescheduleid);

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

				//req.setAttribute("lastpmrcactions", printservice.LastPMRCActions(projectid,committeeid));
				req.setAttribute("lastpmrcactions", printservice.LastPMRCActions(projectid,committeeid,committeescheduleeditdata[2].toString()));

				//							

				req.setAttribute("actionlist",actionsdata);
				req.setAttribute("procurementOnDemand", procurementOnDemand);
				req.setAttribute("procurementOnSanction", procurementOnSanction);
				req.setAttribute("ActionPlanSixMonths", service.ActionPlanSixMonths(projectid));
				req.setAttribute("projectdatadetails", service.ProjectDataDetails(projectid));
				// new code
				List<Object[]>totalMileStones=service.totalProjectMilestones(projectid);//get all the milestones details based on projectid
				List<Object[]>first=null;   //store the milestones with levelid 1
				List<Object[]>second=null;	// store the milestones with levelid 2
				List<Object[]>three= null; // store the milestones with levelid 3
				Map<Integer,String> treeMapLevOne = new TreeMap<>();  // store the milestoneid with level id 1 and counts 
				Map<Integer,String>treeMapLevTwo= new TreeMap<>(); // store the milestonidid with level id 2 and counts
				Map<Integer,String>treeMapLevThree= new TreeMap<>();  // store the milestoneid with level id 3 and counts 
				TreeSet<Integer> AllMilestones = new TreeSet<>();   // store the number of milestone in sorted order
				if(!totalMileStones.isEmpty()) {
					for(Object[]obj:totalMileStones){
						AllMilestones.add(Integer.parseInt(obj[22].toString())); // getting the milestones from list
					}
					for(Integer mile:AllMilestones) {
						int count=1;
						first=totalMileStones.stream().
								filter(i->i[26].toString().equalsIgnoreCase("1") && i[22].toString().equalsIgnoreCase(mile+""))
								.map(objectArray -> new Object[]{objectArray[0], objectArray[2]})
								.collect(Collectors.toList());
						for(Object[]obj:first) {
							treeMapLevOne.put(Integer.parseInt(obj[1].toString()),"A"+(count++));// to get the first level
						}
					}
					for (Map.Entry<Integer,String> entry : treeMapLevOne.entrySet()) {
						int count=1;
						second=totalMileStones.stream().
								filter(i->i[26].toString().equalsIgnoreCase("2") && i[2].toString().equalsIgnoreCase(entry.getKey()+""))
								.map(objectArray -> new Object[] {entry.getKey(),objectArray[3]})
								.collect(Collectors.toList());
						for(Object[]obj:second) {
							treeMapLevTwo.put(Integer.parseInt(obj[1].toString()),entry.getValue()+"-B"+(count++));
						}
					}
					for(Map.Entry<Integer,String>entry: treeMapLevTwo.entrySet()) {
						int count=1;
						three=totalMileStones.stream().
								filter(i->i[26].toString().equalsIgnoreCase("3") && i[3].toString().equalsIgnoreCase(entry.getKey()+""))
								.map(objectArray -> new Object[] {entry.getKey(),objectArray[4]})
								.collect(Collectors.toList());
						for(Object[]obj:three) {
							treeMapLevThree.put(Integer.parseInt(obj[1].toString()), "C"+(count++)); 

						}
					}
				}
				req.setAttribute("treeMapLevOne", treeMapLevOne);
				req.setAttribute("treeMapLevTwo", treeMapLevTwo);
				// new code end
				//code on 06-10-2023				 	 
				List<List<Object[]>> ReviewMeetingList = new ArrayList<List<Object[]>>();
				List<List<Object[]>> ReviewMeetingListPMRC = new ArrayList<List<Object[]>>();
				ReviewMeetingList.add(printservice.ReviewMeetingList(projectid, "EB"));
				ReviewMeetingListPMRC.add(printservice.ReviewMeetingList(projectid, "PMRC")); 
				Map<Integer,String> mappmrc = new HashMap<>();
				Map<Integer,String> mapEB = new HashMap<>();
				int pmrccount=0;
				for (Object []obj:ReviewMeetingListPMRC.get(0)) {
					mappmrc.put(++pmrccount,obj[3].toString());
				}

				int ebcount=0;

				for (Object []obj:ReviewMeetingList.get(0)) {
					mapEB.put(++ebcount,obj[3].toString());
				}
				req.setAttribute("mappmrc", mappmrc);
				req.setAttribute("mapEB", mapEB);

				Object[]momAttachment=service.MomAttachmentFile(committeescheduleid);
				req.setAttribute("tableactionlist",  actionsdata);
				//
				String filename=committeescheduleeditdata[11].toString().replace("/", "-");
				String path=req.getServletContext().getRealPath("/view/temp");
				req.setAttribute("path",path);
				CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
				req.getRequestDispatcher("/view/committee/CommitteeMinutesNew.jsp").forward(req, customResponse);
				String html = customResponse.getOutput();


				byte[]data=html.getBytes();
				InputStream fis1 = new ByteArrayInputStream(data);
				PdfDocument pdfDoc = new PdfDocument(new PdfWriter(path + "/" + filename +  ".pdf"));
				pdfDoc.setTagged(); 
				Document document = new Document(pdfDoc, PageSize.LEGAL);
				document.setMargins(50, 100, 150, 50);
				ConverterProperties converterProperties = new ConverterProperties();
				FontProvider dfp = new DefaultFontProvider(true, true, true);
				converterProperties.setFontProvider(dfp);
				HtmlConverter.convertToPdf(fis1, pdfDoc, converterProperties);

				PdfWriter pdfw=null;

				if(PROTECTED_MINUTES == null) {
					pdfw=new PdfWriter(path +File.separator+ "mergedb.pdf");
				}else {

					String password = LabCode+"123";
					pdfw = new PdfWriter(path +File.separator+ "mergedb.pdf",
							new WriterProperties().setStandardEncryption(password.getBytes(), password.getBytes(),
									EncryptionConstants.ALLOW_PRINTING, EncryptionConstants.ENCRYPTION_AES_128));
				}
				PdfDocument pdfDocs = new PdfDocument(pdfw);
				pdfw.setCompressionLevel(CompressionConstants.BEST_COMPRESSION);
				PdfDocument pdfDocMain = new PdfDocument(new PdfReader(path+File.separator+filename+".pdf"),new PdfWriter(path+File.separator+filename+"Maintemp.pdf"));
				Document docMain = new Document(pdfDocMain,PageSize.A4);
				docMain.setMargins(50, 50, 50, 50);

				docMain.close();
				pdfDocMain.close();
				Path pathOfFileMain= Paths.get( path+File.separator+filename+".pdf");


				PdfReader pdf1=new PdfReader(path+File.separator+filename+"Maintemp.pdf");
				PdfDocument pdfDocument = new PdfDocument(pdf1, pdfw);
				PdfMerger merger = new PdfMerger(pdfDocument);

				if(momAttachment!=null) {
					if(momAttachment[2]!=null) {
						if(FilenameUtils.getExtension(momAttachment[2].toString()).equalsIgnoreCase("pdf")) {
							PdfDocument pdfDocument1 = new PdfDocument(new PdfReader(env.getProperty("ApplicationFilesDrive")+momAttachment[1]+"\\"+momAttachment[2]),new PdfWriter(path+File.separator+filename+"temp.pdf"));
							Document document4 = new Document(pdfDocument1,PageSize.A4);
							document4.setMargins(50, 50, 50, 50);
							Rectangle pageSize;
							PdfCanvas canvas;
							int n = pdfDocument1.getNumberOfPages();

							for(int i=1;i<=n;i++) {
								PdfPage page = pdfDocument1.getPage(i);
								pageSize = page.getPageSize();
								canvas = new PdfCanvas(page);
								Rectangle recta=new Rectangle(10,pageSize.getHeight()-50,40,40); // Left side position
								Rectangle recta2=new Rectangle(pageSize.getWidth()-50,pageSize.getHeight()-50,40,40); // Right side position


								// Add content on the left side
								String[] no=committeescheduleeditdata[11].toString().split("/");


								// Add content on the right side

								canvas.beginText()
								.setFontAndSize(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN), 10)
								.moveText(pageSize.getWidth()-200 , pageSize.getHeight() - 10)
								.showText("Attachment to MoM - "+no[2]+"/"+(service.MeetingNo(committeescheduleeditdata)>0?service.MeetingNo(committeescheduleeditdata):"")+"/"+projectdetails[4].toString())
								.endText();

							}
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
				}
				CharArrayWriterResponse customResponse1 = new CharArrayWriterResponse(res);
				req.getRequestDispatcher("/view/committee/ActionDetailsTable.jsp").forward(req, customResponse1);
				String html1 = customResponse1.getOutput();        
				HtmlConverter.convertToPdf(html1,new FileOutputStream(path+File.separator+filename+"1.pdf")); 

				PdfMerger merger1 = new PdfMerger(pdfDocument);
				PdfReader pdf2=new PdfReader(path+File.separator+filename+"1.pdf");
				PdfDocument pdfDocument2 = new PdfDocument(pdf2);
				merger1.merge(pdfDocument2, 1, pdfDocument2.getNumberOfPages());

				pdfDocument.close();
				merger.close();
				pdf2.close();
				pdf1.close();
				merger1.close();
				pdfw.close();
				res.setContentType("application/pdf");
				res.setHeader("Content-disposition","inline;filename="+filename+".pdf"); 
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

			}
		}
		catch (Exception e) 
		{
			e.printStackTrace(); 
			logger.error(new Date() +"Inside CommitteeMinutesNewDownload.htm "+UserId,e);
		}


	}

	@RequestMapping(value="CommitteeMinutesNewProtected.htm", method = {RequestMethod.POST,RequestMethod.GET})
	public void CommitteeMinutesNewProtected(HttpServletRequest req,HttpServletResponse res, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		String EmpNo=(String)ses.getAttribute("EmpNo");
		String EmpName=(String)ses.getAttribute("EmpName");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeMinutesNewProtected.htm "+UserId);
		try
		{		
			String committeescheduleid = req.getParameter("committeescheduleid");			
			String PROTECTED_MINUTES = req.getParameter("PROTECTED MINUTES");

			Object[] committeescheduleeditdata=service.CommitteeScheduleEditData(committeescheduleid);
			String projectid= committeescheduleeditdata[9].toString();
			String committeeid=committeescheduleeditdata[0].toString();
			if(Long.parseLong(projectid) >0 && committeescheduleeditdata[22].toString().equals("Y")) 
			{

			}
			else 
			{

				Object[] projectdetails = null;

				if(projectid!=null && Integer.parseInt(projectid)>0)
				{
					projectdetails = service.projectdetails(projectid);
					req.setAttribute("projectdetails", projectdetails);
				}
				String divisionid= committeescheduleeditdata[16].toString();
				if(divisionid!=null && Integer.parseInt(divisionid)>0)
				{
					req.setAttribute("divisiondetails", service.DivisionData(divisionid));
				}
				String initiationid= committeescheduleeditdata[17].toString();
				if(initiationid!=null && Integer.parseInt(initiationid)>0)
				{
					req.setAttribute("initiationdetails", service.Initiationdetails(initiationid));
				}

				Committee committee = printservice.getCommitteeData(committeeid);


				List<Object[]> repList = service.getCommitteeMainRepList(committeescheduleeditdata[1]!=null?committeescheduleeditdata[1].toString():"0");
				List<String> repCodes = new ArrayList<>();
				
				if(repList != null && !repList.isEmpty()){
					repCodes = repList.stream().map(obj -> obj[1]!=null ? obj[1].toString() : null).filter(Objects::nonNull).collect(Collectors.toList());
				}

				req.setAttribute("repCodes", repCodes);
				HashMap< String, ArrayList<Object[]>> actionsdata=new LinkedHashMap<String, ArrayList<Object[]>>();
				long lastid=service.getLastPmrcId(projectid, committeeid, committeescheduleid);

				//****************************envi download start**********************************
				List<Object[]> envisagedDemandlist  = new ArrayList<Object[]>();
				envisagedDemandlist=service.getEnvisagedDemandList(projectid);
				req.setAttribute("envisagedDemandlist", envisagedDemandlist);

				req.setAttribute("committeeminutesspeclist",service.CommitteeScheduleMinutes(committeescheduleid) );
				req.setAttribute("committeescheduleeditdata", committeescheduleeditdata);
				req.setAttribute("committeeminutes",service.CommitteeMinutesSpecNew());
				req.setAttribute("committeeinvitedlist", service.CommitteeAtendance(committeescheduleid,"1"));			
				req.setAttribute("labdetails", service.LabDetails(committeescheduleeditdata[24].toString()));
				req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(committeescheduleeditdata[24].toString()));
				req.setAttribute("meetingcount",service.MeetingNo(committeescheduleeditdata));
				//req.setAttribute("milestonedatalevel6", printservice.BreifingMilestoneDetails(projectid,committeeid));

				// this method will show the milestones based on each meeting date
				req.setAttribute("milestonedatalevel6", printservice.BreifingMilestoneDetails(projectid,committeeid,committeescheduleeditdata[2].toString()));

				//				req.setAttribute("committeeminutessub",service.CommitteeMinutesSub());
				//				req.setAttribute("CommitteeAgendaList", service.CommitteeAgendaList(committeescheduleid));
				String LevelId= "2";
				Object[] MileStoneLevelId = printservice.MileStoneLevelId(projectid,committeeid);
				if( MileStoneLevelId!= null) {
					LevelId= MileStoneLevelId[0].toString();
				}
				req.setAttribute("levelid", LevelId);
				req.setAttribute("labInfo", service.LabDetailes(LabCode));
				/*---------------------------------------------------------------------------------------------------------------*/
				//				if(Long.parseLong(projectid) >0 && committeescheduleeditdata[22].toString().equals("N") ) {

				final String localUri=uri+"/pfms_serv/financialStatusBriefing?ProjectCode="+projectdetails[4].toString()+"&rupess="+10000000;
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				headers.set("labcode", LabCode);
				String jsonResult=null;
				try {
					HttpEntity<String> entity = new HttpEntity<String>(headers);
					ResponseEntity<String> response=restTemplate.exchange(localUri, HttpMethod.POST, entity, String.class);
					jsonResult=response.getBody();						
				}catch(Exception e) {
					req.setAttribute("errorMsg", "errorMsg");
				}
				ObjectMapper mapper = new ObjectMapper();
				mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
				List<ProjectFinancialDetails> projectDetails=null;
				if(jsonResult!=null) {
					try {
						projectDetails = mapper.readValue(jsonResult, new TypeReference<List<ProjectFinancialDetails>>(){});
						req.setAttribute("financialDetails",projectDetails);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				}

				final String localUri2=uri+"/pfms_serv/getTotalDemand";

				String jsonResult2=null;
				try {
					HttpEntity<String> entity = new HttpEntity<String>(headers);
					ResponseEntity<String> response=restTemplate.exchange(localUri2, HttpMethod.POST, entity, String.class);
					jsonResult2=response.getBody();						
				}catch(Exception e) {
					req.setAttribute("errorMsg", "errorMsg");
				}
				ObjectMapper mapper2 = new ObjectMapper();
				mapper2.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				mapper2.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
				List<TotalDemand> totaldemand=null;
				if(jsonResult2!=null) {
					try {
						totaldemand = mapper2.readValue(jsonResult2, new TypeReference<List<TotalDemand>>(){});
						req.setAttribute("TotalProcurementDetails",totaldemand);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				}

				List<Object[]> procurementStatusList=(List<Object[]>)service.ProcurementStatusList(projectid);
				List<Object[]> procurementOnDemand=null;
				List<Object[]> procurementOnSanction=null;


				if(procurementStatusList!=null){
					Map<Object, List<Object[]>> map = procurementStatusList.stream().collect(Collectors.groupingBy(c -> c[9])); 
					Collection<?> keys = map.keySet();
					for(Object key:keys){
						if(key.toString().equals("D")) {
							procurementOnDemand=map.get(key);
						}else if(key.toString().equals("S")) {
							procurementOnSanction=map.get(key);
						}
					}
				}
				List<Object[]> actionlist= service.MinutesViewAllActionList(committeescheduleid);

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

				//req.setAttribute("lastpmrcactions", printservice.LastPMRCActions(projectid,committeeid));
				req.setAttribute("lastpmrcactions", printservice.LastPMRCActions(projectid,committeeid,committeescheduleeditdata[2].toString()));

				//							

				req.setAttribute("actionlist",actionsdata);
				req.setAttribute("procurementOnDemand", procurementOnDemand);
				req.setAttribute("procurementOnSanction", procurementOnSanction);
				req.setAttribute("ActionPlanSixMonths", service.ActionPlanSixMonths(projectid));
				req.setAttribute("projectdatadetails", service.ProjectDataDetails(projectid));
				// new code
				List<Object[]>totalMileStones=service.totalProjectMilestones(projectid);//get all the milestones details based on projectid
				List<Object[]>first=null;   //store the milestones with levelid 1
				List<Object[]>second=null;	// store the milestones with levelid 2
				List<Object[]>three= null; // store the milestones with levelid 3
				Map<Integer,String> treeMapLevOne = new TreeMap<>();  // store the milestoneid with level id 1 and counts 
				Map<Integer,String>treeMapLevTwo= new TreeMap<>(); // store the milestonidid with level id 2 and counts
				Map<Integer,String>treeMapLevThree= new TreeMap<>();  // store the milestoneid with level id 3 and counts 
				TreeSet<Integer> AllMilestones = new TreeSet<>();   // store the number of milestone in sorted order
				if(!totalMileStones.isEmpty()) {
					for(Object[]obj:totalMileStones){
						AllMilestones.add(Integer.parseInt(obj[22].toString())); // getting the milestones from list
					}
					for(Integer mile:AllMilestones) {
						int count=1;
						first=totalMileStones.stream().
								filter(i->i[26].toString().equalsIgnoreCase("1") && i[22].toString().equalsIgnoreCase(mile+""))
								.map(objectArray -> new Object[]{objectArray[0], objectArray[2]})
								.collect(Collectors.toList());
						for(Object[]obj:first) {
							treeMapLevOne.put(Integer.parseInt(obj[1].toString()),"A"+(count++));// to get the first level
						}
					}
					for (Map.Entry<Integer,String> entry : treeMapLevOne.entrySet()) {
						int count=1;
						second=totalMileStones.stream().
								filter(i->i[26].toString().equalsIgnoreCase("2") && i[2].toString().equalsIgnoreCase(entry.getKey()+""))
								.map(objectArray -> new Object[] {entry.getKey(),objectArray[3]})
								.collect(Collectors.toList());
						for(Object[]obj:second) {
							treeMapLevTwo.put(Integer.parseInt(obj[1].toString()),entry.getValue()+"-B"+(count++));
						}
					}
					for(Map.Entry<Integer,String>entry: treeMapLevTwo.entrySet()) {
						int count=1;
						three=totalMileStones.stream().
								filter(i->i[26].toString().equalsIgnoreCase("3") && i[3].toString().equalsIgnoreCase(entry.getKey()+""))
								.map(objectArray -> new Object[] {entry.getKey(),objectArray[4]})
								.collect(Collectors.toList());
						for(Object[]obj:three) {
							treeMapLevThree.put(Integer.parseInt(obj[1].toString()), "C"+(count++)); 

						}
					}
				}
				req.setAttribute("treeMapLevOne", treeMapLevOne);
				req.setAttribute("treeMapLevTwo", treeMapLevTwo);
				// new code end
				//code on 06-10-2023				 	 
				List<List<Object[]>> ReviewMeetingList = new ArrayList<List<Object[]>>();
				List<List<Object[]>> ReviewMeetingListPMRC = new ArrayList<List<Object[]>>();
				ReviewMeetingList.add(printservice.ReviewMeetingList(projectid, "EB"));
				ReviewMeetingListPMRC.add(printservice.ReviewMeetingList(projectid, "PMRC")); 
				Map<Integer,String> mappmrc = new HashMap<>();
				Map<Integer,String> mapEB = new HashMap<>();
				int pmrccount=0;
				for (Object []obj:ReviewMeetingListPMRC.get(0)) {
					mappmrc.put(++pmrccount,obj[3].toString());
				}

				int ebcount=0;

				for (Object []obj:ReviewMeetingList.get(0)) {
					mapEB.put(++ebcount,obj[3].toString());
				}
				req.setAttribute("mappmrc", mappmrc);
				req.setAttribute("mapEB", mapEB);

				Object[]momAttachment=service.MomAttachmentFile(committeescheduleid);


				req.setAttribute("tableactionlist",  actionsdata);
				//
				String filename=committeescheduleeditdata[11].toString().replace("/", "-");
				String path=req.getServletContext().getRealPath("/view/temp");
				req.setAttribute("path",path);
				CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
				req.getRequestDispatcher("/view/committee/CommitteeMinutesNew.jsp").forward(req, customResponse);
				String html = customResponse.getOutput();


				byte[]data=html.getBytes();
				InputStream fis1 = new ByteArrayInputStream(data);
				PdfDocument pdfDoc = new PdfDocument(new PdfWriter(path + "/" + filename +  ".pdf"));
				pdfDoc.setTagged(); 
				Document document = new Document(pdfDoc, PageSize.LEGAL);
				document.setMargins(50, 100, 150, 50);
				ConverterProperties converterProperties = new ConverterProperties();
				FontProvider dfp = new DefaultFontProvider(true, true, true);
				converterProperties.setFontProvider(dfp);
				HtmlConverter.convertToPdf(fis1, pdfDoc, converterProperties);

				String password = LabCode+"123";

				PdfWriter pdfw= new PdfWriter(path +File.separator+ "mergedb.pdf",
						new WriterProperties().setStandardEncryption(password.getBytes(), password.getBytes(),
								EncryptionConstants.ALLOW_PRINTING, EncryptionConstants.ENCRYPTION_AES_128));

				PdfDocument pdfDocs = new PdfDocument(pdfw);
				PdfDocument pdfDocMain = new PdfDocument(new PdfReader(path+File.separator+filename+".pdf"),new PdfWriter(path+File.separator+filename+"Maintemp.pdf"));
				Document docMain = new Document(pdfDocMain,PageSize.A4);
				docMain.setMargins(50, 50, 50, 50);

				docMain.close();
				pdfDocMain.close();
				Path pathOfFileMain= Paths.get( path+File.separator+filename+".pdf");


				PdfReader pdf1=new PdfReader(path+File.separator+filename+"Maintemp.pdf");
				PdfDocument pdfDocument = new PdfDocument(pdf1, pdfw);
				PdfMerger merger = new PdfMerger(pdfDocument);

				//								        if(momAttachment!=null) {
				//								        	if(momAttachment[2]!=null) {
				//								        		if(FilenameUtils.getExtension(momAttachment[2].toString()).equalsIgnoreCase("pdf")) {
				//							        		        PdfDocument pdfDocument1 = new PdfDocument(new PdfReader(env.getProperty("ApplicationFilesDrive")+momAttachment[1]+"\\"+momAttachment[2]),new PdfWriter(path+File.separator+filename+"temp.pdf"));
				//							        		        Document document4 = new Document(pdfDocument1,PageSize.A4);
				//							        		        document4.setMargins(50, 50, 50, 50);
				//							        		        Rectangle pageSize;
				//							        		        PdfCanvas canvas;
				//							        		        int n = pdfDocument1.getNumberOfPages();
				//							        		
				//							        		        for(int i=1;i<=n;i++) {
				//							        		            PdfPage page = pdfDocument1.getPage(i);
				//							        		            pageSize = page.getPageSize();
				//							        		            canvas = new PdfCanvas(page);
				//							        		            Rectangle recta=new Rectangle(10,pageSize.getHeight()-50,40,40); // Left side position
				//							        		            Rectangle recta2=new Rectangle(pageSize.getWidth()-50,pageSize.getHeight()-50,40,40); // Right side position
				//
				//							        		            
				//							        		            // Add content on the left side
				//							        		            String[] no=committeescheduleeditdata[11].toString().split("/");
				//							        		        
				//
				//							        		            // Add content on the right side
				//							        		            
				//							        		            canvas.beginText()
				//							        		                .setFontAndSize(PdfFontFactory.createFont(FontConstants.TIMES_ROMAN), 10)
				//							        		                .moveText(pageSize.getWidth()-200 , pageSize.getHeight() - 10)
				//							        		                .showText("Attachment to MoM - "+no[2]+"/"+(service.MeetingNo(committeescheduleeditdata)>0?service.MeetingNo(committeescheduleeditdata):"")+"/"+projectdetails[4].toString())
				//							        		                .endText();
				//
				//							        		        }
				//							        		        document4.close();
				//							        		        pdfDocument1.close();
				//							        	        	PdfReader pdf4=new PdfReader(path+"/"+filename+"temp.pdf");
				//							        	        	PdfDocument pdfDocument4 = new PdfDocument(pdf4);
				//							        		        merger.merge(pdfDocument4, 1, pdfDocument4.getNumberOfPages());
				//							        		        
				//							        		        pdfDocument4.close();
				//							        		        pdf4.close();
				//							        		        Path pathOfFile= Paths.get( path+File.separator+filename+"temp.pdf");
				//							        		        Files.delete(pathOfFile);	
				//								        	}
				//								        	}
				//								        }
				//								        


				pdfDocument.close();
				merger.close();

				pdf1.close();	       
				pdfw.close();
				res.setContentType("application/pdf");
				res.setHeader("Content-disposition","inline;filename="+filename+".pdf"); 
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

			}
		}
		catch (Exception e) 
		{
			e.printStackTrace(); 
			logger.error(new Date() +"Inside CommitteeMinutesNewProtected.htm "+UserId,e);
		}


	}






	@RequestMapping(value="EmployeeScheduleReports.htm", method = {RequestMethod.POST,RequestMethod.GET})
	public String EmployeeScheduleReports(Model model,HttpServletRequest req,HttpServletResponse res, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		String Logintype= (String)ses.getAttribute("LoginType");
		logger.info(new Date() +"Inside EmployeeScheduleReports.htm "+UserId);
		try
		{
			String rtype=req.getParameter("rtype");
			String empid=req.getParameter("empid");
			if(empid==null)
			{
				empid = ((Long) ses.getAttribute("EmpId")).toString();
				rtype = "D";
			}				
			List<Object[]> employeeScheduleList=service.EmployeeScheduleReports(req,empid, rtype);
			List<Object[]> employeeList=service.EmployeeDropdown(empid, Logintype, "0");

			req.setAttribute("rtype", rtype);
			req.setAttribute("empid", empid);
			req.setAttribute("employeeList", employeeList);
			req.setAttribute("employeeScheduleList", employeeScheduleList);
			return "committee/EmployeeReports";
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside EmployeeScheduleReports.htm "+UserId,e);
			return "static/Error";
		}


	}

	@RequestMapping(value="EmployeeScheduleReportDownload.htm", method = {RequestMethod.POST,RequestMethod.GET})
	public void EmployeeScheduleReportDownload(Model model,HttpServletRequest req,HttpServletResponse res, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside EmployeeScheduleReportDownload.htm "+UserId);
		try
		{
			String rtype=req.getParameter("rtype");
			String empid=req.getParameter("empid");

			if(empid==null)
			{
				empid = ((Long) ses.getAttribute("EmpId")).toString();
				rtype = "D";
			}				
			List<Object[]> employeeScheduleList=service.EmployeeScheduleReports(req,empid, rtype);
			List<Object[]> employeeList=service.EmployeeList(LabCode);


			req.setAttribute("rtype", rtype);
			req.setAttribute("empid", empid);
			req.setAttribute("employeeList", employeeList);
			req.setAttribute("employeeScheduleList", employeeScheduleList);


			String filename="employeereport "+empid;
			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);




			CharArrayWriterResponse customResponse1 = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/committee/EmployeeReportDownload.jsp").forward(req, customResponse1);
			String html1 = customResponse1.getOutput();        

			HtmlConverter.convertToPdf(html1,new FileOutputStream(path+File.separator+filename+".pdf")); 

			res.setContentType("application/pdf");
			res.setHeader("Content-disposition","attachment;filename="+filename+".pdf");
			File f=new File(path +File.separator+ filename+".pdf");


			FileInputStream fis = new FileInputStream(f);
			DataOutputStream os = new DataOutputStream(res.getOutputStream());
			res.setHeader("Content-Length",String.valueOf(f.length()));
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fis.read(buffer)) >= 0) {
				os.write(buffer, 0, len);
			} 
			fis.close();
			os.close();



			Path pathOfFile= Paths.get( path+File.separator+filename+".pdf"); 
			Files.delete(pathOfFile);		


		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside EmployeeScheduleReportDownload.htm "+UserId,e);
		}


	}

	@RequestMapping(value = "AgendaDocLinkDownload.htm", method = RequestMethod.GET)
	public void AgendaDocLinkDownload(Model model,HttpServletRequest req, HttpSession ses,HttpServletResponse res)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");

		logger.info(new Date() +"Inside AgendaDocLinkDownload.htm "+UserId);

		try {	
			String filerepid=req.getParameter("filerepid");
			Object[] obj=service.AgendaDocLinkDownload(filerepid);
			Path uploadPath = Paths.get(ApplicationFilesDrive, obj[2].toString(),(obj[3].toString()+obj[7].toString()+"-"+obj[6].toString()+".zip"));
			String path=req.getServletContext().getRealPath("/view/temp");
			Zipper zip=new Zipper();
			zip.unpack(uploadPath.toString(),path,obj[5].toString());
			res.setContentType("application/pdf");
			res.setHeader("Content-disposition","attachment;filename="+obj[4]); 
			File f=new File(path+"/"+obj[4]);
			FileInputStream fis = new FileInputStream(f);
			DataOutputStream os = new DataOutputStream(res.getOutputStream());
			res.setHeader("Content-Length",String.valueOf(f.length()));
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fis.read(buffer)) >= 0) {
				os.write(buffer, 0, len);
			} 
			fis.close();
			os.close();

			Path pathOfFile2= Paths.get(path+"/"+obj[4]); 
			Files.delete(pathOfFile2);

		}
		catch (Exception e) {

			e.printStackTrace();  
			logger.error(new Date() +" Inside AgendaDocLinkDownload.htm "+UserId, e); 

		}

	}


	@RequestMapping(value = "PreDefinedAgendas.htm")
	public String PreDefinedAgendas(Model model,HttpServletRequest req, HttpSession ses,HttpServletResponse res)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside PreDefinedAgendas.htm "+UserId);			
		try {	
			String committeeid=req.getParameter("committeeid");
			if(committeeid==null)
			{			
				Map md=model.asMap();
				committeeid=(String)md.get("committeeid");
			}

			List<Object[]> committeeslist = service.ProjectCommitteesList(LabCode);

			if(committeeslist.size()>0 &&  committeeid==null)
			{
				committeeid=committeeslist.get(0)[0].toString();
			}
			if(committeeid==null)
			{
				committeeid="0";
			}

			List<Object[]> DefAgendas =  service.DefaultAgendaList(committeeid,LabCode);

			req.setAttribute("DefAgendas", DefAgendas);
			req.setAttribute("committeeid", committeeid);
			req.setAttribute("committeeslist",committeeslist);

			return "committee/PreDefAgendas";
		}
		catch (Exception e) {

			e.printStackTrace();  
			logger.error(new Date() +" Inside PreDefinedAgendas.htm "+UserId, e); 
			return "static/Error";
		}

	}


	@RequestMapping(value = "PreDefinedAgendaEdit.htm", method = RequestMethod.POST)
	public String PreDefinedAgendaEdit(Model model,HttpServletRequest req, HttpSession ses,HttpServletResponse res,RedirectAttributes redir)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside PreDefinedAgendaEdit.htm "+UserId);			
		try {	
			String committeeid=req.getParameter("committeeid");
			String agendaitem=req.getParameter("agendaitem");
			String remarks=req.getParameter("remarks");
			String duration=req.getParameter("duration");
			String agendaid=req.getParameter("agendaid");

			CommitteeDefaultAgenda agenda = new CommitteeDefaultAgenda();

			agenda.setDefaultAgendaId(Long.parseLong(agendaid));
			agenda.setDuration(Integer.parseInt(duration));
			agenda.setAgendaItem(agendaitem);
			agenda.setRemarks(remarks);
			agenda.setModifiedBy(UserId);

			int count = service.PreDefAgendaEdit(agenda);

			if(count > 0) 
			{						
				redir.addAttribute("result", " Agenda Updated Successfully");					
			} 
			else 
			{
				redir.addAttribute("resultfail", " Agenda Update Unsucessful");
			}		

			redir.addFlashAttribute("committeeid", committeeid);
			return "redirect:/PreDefinedAgendas.htm";
		}
		catch (Exception e) {

			e.printStackTrace();  
			logger.error(new Date() +" Inside PreDefinedAgendaEdit.htm "+UserId, e); 
			return "static/Error";
		}

	}

	@RequestMapping(value = "PreDefinedAgendaAdd.htm", method = RequestMethod.POST)
	public String PreDefinedAgendaAdd(Model model,HttpServletRequest req, HttpSession ses,HttpServletResponse res,RedirectAttributes redir)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside PreDefinedAgendaAdd.htm "+UserId);			
		try {	
			String committeeid=req.getParameter("committeeid");
			String agendaitem=req.getParameter("agendaitem");
			String remarks=req.getParameter("remarks");
			String duration=req.getParameter("duration");

			CommitteeDefaultAgenda agenda = new CommitteeDefaultAgenda();

			agenda.setLabCode(LabCode);
			agenda.setDuration(Integer.parseInt(duration));
			agenda.setAgendaItem(agendaitem);
			agenda.setRemarks(remarks);
			agenda.setCommitteeId(Long.parseLong(committeeid));
			agenda.setCreatedBy(UserId);

			long count = service.PreDefAgendaAdd(agenda);

			if(count > 0) 
			{						
				redir.addAttribute("result", " Agenda Added Successfully");					
			} 
			else 
			{
				redir.addAttribute("resultfail", " Agenda Adding Unsucessful");
			}		

			redir.addFlashAttribute("committeeid", committeeid);
			return "redirect:/PreDefinedAgendas.htm";
		}
		catch (Exception e) {				
			e.printStackTrace();  
			logger.error(new Date() +" Inside PreDefinedAgendaAdd.htm "+UserId, e); 
			return "static/Error";
		}

	}


















	@RequestMapping(value = "PreDefinedAgendaDelete.htm", method = RequestMethod.POST)
	public String PreDefinedAgendaDelete(Model model,HttpServletRequest req, HttpSession ses,HttpServletResponse res,RedirectAttributes redir)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside PreDefinedAgendaDelete.htm "+UserId);			
		try {	
			String committeeid=req.getParameter("committeeid");
			String agendaid=req.getParameter("agendaid");

			int count = service.PreDefAgendaDelete(agendaid);

			if(count > 0) 
			{						
				redir.addAttribute("result", " Agenda Deleted Successfully");					
			} 
			else 
			{
				redir.addAttribute("resultfail", " Agenda Delete Unsucessful");
			}		

			redir.addFlashAttribute("committeeid", committeeid);
			return "redirect:/PreDefinedAgendas.htm";
		}
		catch (Exception e) {

			e.printStackTrace();  
			logger.error(new Date() +" Inside PreDefinedAgendaDelete.htm "+UserId, e); 
			return "static/Error";
		}

	}


	@RequestMapping(value = "getMinutesFrozen.htm")
	public String getMinutesFrozen(HttpServletRequest req, HttpSession ses,HttpServletResponse res,RedirectAttributes redir) throws Exception 
	{	
		String UserId = (String) ses.getAttribute("Username");
		long EmpId = (Long) ses.getAttribute("EmpId");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside getMinutesFrozen.htm "+UserId);		
		try { 

			String committeescheduleid = req.getParameter("committeescheduleid");			
			Object[] committeescheduleeditdata=service.CommitteeScheduleEditData(committeescheduleid);
			String projectid= committeescheduleeditdata[9].toString();
			String committeeid=committeescheduleeditdata[0].toString();

			String IsFrozen=req.getParameter("IsFrozen");
			if(IsFrozen.equalsIgnoreCase("Y")) {

				int count=	service.MomFreezingUpdate(committeescheduleid);
				if(count > 0) 
				{				

					redir.addAttribute("result", "DPFM unfreezed Successfully");	
					redir.addFlashAttribute("committeescheduleid", committeescheduleid);
					return "redirect:/CommitteeScheduleMinutes.htm";
				} 
			}

			Object[] projectdetails = null;

			if(projectid!=null && Integer.parseInt(projectid)>0)
			{
				projectdetails = service.projectdetails(projectid);
				req.setAttribute("projectdetails", projectdetails);
			}
			String divisionid= committeescheduleeditdata[16].toString();
			if(divisionid!=null && Integer.parseInt(divisionid)>0)
			{
				req.setAttribute("divisiondetails", service.DivisionData(divisionid));
			}
			String initiationid= committeescheduleeditdata[17].toString();
			if(initiationid!=null && Integer.parseInt(initiationid)>0)
			{
				req.setAttribute("initiationdetails", service.Initiationdetails(initiationid));
			}

			Committee committee = printservice.getCommitteeData(committeeid);

			HashMap< String, ArrayList<Object[]>> actionsdata=new LinkedHashMap<String, ArrayList<Object[]>>();
			long lastid=service.getLastPmrcId(projectid, committeeid, committeescheduleid);

			req.setAttribute("committeeminutesspeclist",service.CommitteeScheduleMinutes(committeescheduleid) );
			req.setAttribute("committeescheduleeditdata", committeescheduleeditdata);
			req.setAttribute("committeeminutes",service.CommitteeMinutesSpecNew());
			req.setAttribute("committeeinvitedlist", service.CommitteeAtendance(committeescheduleid,"1"));			
			req.setAttribute("labdetails", service.LabDetails(committeescheduleeditdata[24].toString()));
			req.setAttribute("isprint", "Y");	
			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(committeescheduleeditdata[24].toString()));
			req.setAttribute("meetingcount",service.MeetingNo(committeescheduleeditdata));
			//req.setAttribute("milestonedatalevel6", printservice.BreifingMilestoneDetails(projectid,committeeid));

			req.setAttribute("lastpmrcactions", printservice.LastPMRCActions(projectid,committeeid,committeescheduleeditdata[2].toString()));
			req.setAttribute("milestonedatalevel6", printservice.BreifingMilestoneDetails(projectid,committeeid,committeescheduleeditdata[2].toString()));
			//				req.setAttribute("committeeminutessub",service.CommitteeMinutesSub());
			//				req.setAttribute("CommitteeAgendaList", service.CommitteeAgendaList(committeescheduleid));
			String LevelId= "2";
			Object[] MileStoneLevelId = printservice.MileStoneLevelId(projectid,committeeid);
			if( MileStoneLevelId!= null) {
				LevelId= MileStoneLevelId[0].toString();
			}
			req.setAttribute("levelid", LevelId);
			req.setAttribute("labInfo", service.LabDetailes(LabCode));
			/*---------------------------------------------------------------------------------------------------------------*/
			//				if(Long.parseLong(projectid) >0 && committeescheduleeditdata[22].toString().equals("N") ) {

			final String localUri=uri+"/pfms_serv/financialStatusBriefing?ProjectCode="+projectdetails[4].toString()+"&rupess="+10000000;
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.set("labcode", LabCode);
			String jsonResult=null;
			try {
				HttpEntity<String> entity = new HttpEntity<String>(headers);
				ResponseEntity<String> response=restTemplate.exchange(localUri, HttpMethod.POST, entity, String.class);
				jsonResult=response.getBody();						
			}catch(Exception e) {
				req.setAttribute("errorMsg", "errorMsg");
			}
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
			List<ProjectFinancialDetails> projectDetails=null;
			if(jsonResult!=null) {
				try {
					projectDetails = mapper.readValue(jsonResult, new TypeReference<List<ProjectFinancialDetails>>(){});
					req.setAttribute("financialDetails",projectDetails);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}

			final String localUri2=uri+"/pfms_serv/getTotalDemand";

			String jsonResult2=null;
			try {
				HttpEntity<String> entity = new HttpEntity<String>(headers);
				ResponseEntity<String> response=restTemplate.exchange(localUri2, HttpMethod.POST, entity, String.class);
				jsonResult2=response.getBody();						
			}catch(Exception e) {
				req.setAttribute("errorMsg", "errorMsg");
			}
			ObjectMapper mapper2 = new ObjectMapper();
			mapper2.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			mapper2.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
			List<TotalDemand> totaldemand=null;
			if(jsonResult2!=null) {
				try {
					totaldemand = mapper2.readValue(jsonResult2, new TypeReference<List<TotalDemand>>(){});
					req.setAttribute("TotalProcurementDetails",totaldemand);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}

			List<Object[]> procurementStatusList=(List<Object[]>)service.ProcurementStatusList(projectid);
			List<Object[]> procurementOnDemand=null;
			List<Object[]> procurementOnSanction=null;


			if(procurementStatusList!=null){
				Map<Object, List<Object[]>> map = procurementStatusList.stream().collect(Collectors.groupingBy(c -> c[9])); 
				Collection<?> keys = map.keySet();
				for(Object key:keys){
					if(key.toString().equals("D")) {
						procurementOnDemand=map.get(key);
					}else if(key.toString().equals("S")) {
						procurementOnSanction=map.get(key);
					}
				}
			}
			List<Object[]> actionlist= service.MinutesViewAllActionList(committeescheduleid);

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

			//req.setAttribute("lastpmrcactions", service.LastPMRCActions(lastid,committeeid,projectid,committeescheduleeditdata[22]+""));
			req.setAttribute("actionlist",actionsdata);
			req.setAttribute("procurementOnDemand", procurementOnDemand);
			req.setAttribute("procurementOnSanction", procurementOnSanction);
			req.setAttribute("ActionPlanSixMonths", service.ActionPlanSixMonths(projectid));



			//new code added
			List<Object[]>totalMileStones=service.totalProjectMilestones(projectid);
			List<Object[]>first=null;
			List<Object[]>second=null;
			Map<Integer,String> treeMapLevOne = new TreeMap<>();
			Map<Integer,String>treeMapLevTwo= new TreeMap<>();
			TreeSet<Integer> AllMilestones = new TreeSet<>();
			if(!totalMileStones.isEmpty()) {
				for(Object[]obj:totalMileStones) {
					AllMilestones.add(Integer.parseInt(obj[22].toString()));
				}
				for(Integer mile:AllMilestones) {
					int count=1;
					first=totalMileStones.stream().
							filter(i->i[26].toString().equalsIgnoreCase("1") && i[22].toString().equalsIgnoreCase(mile+""))
							.map(objectArray -> new Object[]{objectArray[0], objectArray[2]})
							.collect(Collectors.toList());
					for(Object[]obj:first) {
						treeMapLevOne.put(Integer.parseInt(obj[1].toString()),"A"+(count++));// to get the first level
					}
				}
				for (Map.Entry<Integer,String> entry : treeMapLevOne.entrySet()) {
					int count=1;
					second=totalMileStones.stream().
							filter(i->i[26].toString().equalsIgnoreCase("2") && i[2].toString().equalsIgnoreCase(entry.getKey()+""))
							.map(objectArray -> new Object[] {entry.getKey(),objectArray[3]})
							.collect(Collectors.toList());
					for(Object[]obj:second) {
						treeMapLevTwo.put(Integer.parseInt(obj[1].toString()),entry.getValue()+"-B"+(count++));
					}
				}
			}
			req.setAttribute("treeMapLevOne", treeMapLevOne);
			req.setAttribute("treeMapLevTwo", treeMapLevTwo);

			// new code added end
			//					 	req.setAttribute("milestonesubsystems", service.MilestoneSubsystems(projectid));
			//				}
			//				else if(committeescheduleeditdata[22].toString().equals("Y") ){
			//					List<Object[]> procurementStatusList=(List<Object[]>)service.getMinutesProcure(committeescheduleid);
			//				 	List<Object[]> procurementOnDemand=null;
			//				 	List<Object[]> procurementOnSanction=null;
			//		 						
			//				 	 if(procurementStatusList!=null){
			//				 		Map<Object, List<Object[]>> map = procurementStatusList.stream().collect(Collectors.groupingBy(c -> c[9])); 
			//				 		Collection<?> keys = map.keySet();
			//				 		for(Object key:keys){
			//				 		    if(key.toString().equals("D")) {
			//				 		    	procurementOnDemand=map.get(key);
			//				 		    }else if(key.toString().equals("S")) {
			//				 		    	procurementOnSanction=map.get(key);
			//				 		    }
			//				 		 }
			//				 	}
			//				 	 
			//				 	req.setAttribute("lastpmrcactions", service.LastPMRCActions(Long.parseLong(committeescheduleid),committeeid,projectid,committeescheduleeditdata[22]+""));
			//				 	req.setAttribute("financialDetails",service.getMinutesFinance(committeescheduleid));
			////					req.setAttribute("milestonesubsystems", service.getMinutesSubMile(committeescheduleid));
			//					req.setAttribute("ActionPlanSixMonths", service.getMinutesMile(committeescheduleid));
			//					req.setAttribute("procurementOnDemand", procurementOnDemand);
			//				 	req.setAttribute("procurementOnSanction", procurementOnSanction);
			//				 	List<Object[]> actionlist= service.getMinutesAction(committeescheduleid);
			//					
			//					
			//					for(Object obj[] : actionlist) {
			//							
			//							ArrayList<Object[]> values=new ArrayList<Object[]>(); 
			//							for(Object obj1[] : actionlist ) {
			//								if(obj1[0].equals(obj[0])) {
			//									values.add(obj1);
			//								}
			//						}
			//							if(!actionsdata.containsKey(obj[0].toString())) {
			//								actionsdata.put(obj[0].toString(), values);
			//							}
			//					} 
			//					req.setAttribute("actionlist",actionsdata);
			//				}
			/*---------------------------------------------------------------------------------------------------------------*/			
			List<List<Object[]>> ReviewMeetingList = new ArrayList<List<Object[]>>();
			List<List<Object[]>> ReviewMeetingListPMRC = new ArrayList<List<Object[]>>();
			ReviewMeetingList.add(printservice.ReviewMeetingList(projectid, "EB"));
			ReviewMeetingListPMRC.add(printservice.ReviewMeetingList(projectid, "PMRC")); 
			Map<Integer,String> mappmrc = new HashMap<>();
			int pmrccount=0;


			int ebcount=0;
			Map<Integer,String> mapEB = new HashMap<>();
			for (Object []obj:ReviewMeetingList.get(0)) {
				mapEB.put(++ebcount,obj[3].toString());
			}
			req.setAttribute("mappmrc", mappmrc);
			req.setAttribute("mapEB", mapEB);
			req.setAttribute("tableactionlist",  actionsdata);

			String filename=committeescheduleeditdata[11].toString().replace("/", "-");
			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);

			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/committee/CommitteeMinutesNew.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"));
			req.setAttribute("tableactionlist",  actionsdata);
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

			File dpfm_file=new File(path +File.separator+ "merged.pdf");

			// new code by sankha on 12-10-2023
			/*
			 * CommitteeProjectBriefingFrozen briefing =
			 * CommitteeProjectBriefingFrozen.builder()
			 * .ScheduleId(Long.parseLong(committeescheduleid)) .FreezeByEmpId(EmpId)
			 * .momFile(dpfm_file) .LabCode(LabCode) .IsActive(1) .build();
			 * 
			 * long result=service.doMomFreezing(briefing);
			 */


			CommitteeMeetingDPFMFrozen dpfm = CommitteeMeetingDPFMFrozen.builder()
					.ScheduleId(Long.parseLong(committeescheduleid)) .FreezeByEmpId(EmpId)
					.dpfmfile(dpfm_file) .MeetingId(committeescheduleeditdata[11].toString())
					.LabCode(LabCode) .IsActive(1) .build(); 
			long count  =service.FreezeDPFMMinutes(dpfm);
			if(count > 0) 
			{				
				service.updateMinutesFrozen(committeescheduleid);
				redir.addAttribute("result", "DPFM Frozen Successfully");					
			} 
			else 
			{
				redir.addAttribute("resultfail", "DPFM Freezing Unsucessful");
			}		
			Path pathOfFile2= Paths.get( path+File.separator+filename+"1.pdf"); 
			Files.delete(pathOfFile2);		
			pathOfFile2= Paths.get( path+File.separator+filename+".pdf"); 
			Files.delete(pathOfFile2);	
			pathOfFile2= Paths.get(path +File.separator+ "merged.pdf"); 
			Files.delete(pathOfFile2);

			redir.addFlashAttribute("committeescheduleid", committeescheduleid);
			return "redirect:/CommitteeScheduleMinutes.htm"; 
		}
		catch(Exception e) {	    		
			logger.error(new Date() +" Inside getMinutesFrozen.htm "+UserId, e);
			e.printStackTrace();
			return "static/Error";
		}

	}


	//		@RequestMapping(value = "getMinutesFrozen.htm", method = RequestMethod.POST)
	//		public String getMinutesFrozen(HttpServletRequest req, HttpSession ses,RedirectAttributes redir) throws Exception 
	//		{	
	//			String UserId = (String) ses.getAttribute("Username");
	//			logger.info(new Date() +"Inside getMinutesFrozen.htm "+UserId);		
	//		    try { 
	//		    	long result=0;
	//		    	int ibasserviceon = 0;
	//		    	String committeescheduleid = req.getParameter("committeescheduleid");
	//		    	Object[] committeescheduleeditdata=service.CommitteeScheduleEditData(committeescheduleid);
	//				String projectid= committeescheduleeditdata[9].toString();
	//				String committeeid=committeescheduleeditdata[0].toString();
	//		    	if(Long.parseLong(projectid) >0 ) {
	//					
	//		    			Object[] projectdetails = service.projectdetails(projectid);
	//		    			
	//		    		 	final String localUri=uri+"/pfms_serv/financialStatusBriefing?ProjectCode="+projectdetails[4].toString()+"&rupess="+10000000;
	//				 		HttpHeaders headers = new HttpHeaders();
	//				 		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	//				    	 
	//				 		String jsonResult=null;
	//						try {
	//							HttpEntity<String> entity = new HttpEntity<String>(headers);
	//							ResponseEntity<String> response=restTemplate.exchange(localUri, HttpMethod.POST, entity, String.class);
	//							jsonResult=response.getBody();						
	//						}
	//						catch(HttpClientErrorException  | ResourceAccessException e) 
	//						{
	//							e.printStackTrace();
	//							ibasserviceon++;
	//							redir.addAttribute("errorMsg", "Not Freezed as IBAS Server is not Responding !!");
	//
	//						}
	//						catch(Exception e) {
	//							
	//							e.printStackTrace();
	//							req.setAttribute("errorMsg", "errorMsg");
	//						}
	//						ObjectMapper mapper = new ObjectMapper();
	//						mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	//						mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
	//						List<ProjectFinancialDetails> projectDetails=null;
	//						if(jsonResult!=null) {
	//							try {
	//								/*
	//								 * projectDetails = mapper.readValue(jsonResult,
	//								 * mapper.getTypeFactory().constructCollectionType(List.class,
	//								 * ProjectFinancialDetails.class));
	//								 */
	//								projectDetails = mapper.readValue(jsonResult, new TypeReference<List<ProjectFinancialDetails>>(){});
	//							} catch (JsonProcessingException e) {
	//								e.printStackTrace();
	//							}
	//						}
	//				
	//						
	//				if(ibasserviceon==0) {
	//					
	//				if(projectDetails!=null) {
	//
	//				for (ProjectFinancialDetails finance:projectDetails) {
	//					MinutesFinanceList fin=new MinutesFinanceList();
	//					fin.setBudgetHeadDescription(finance.getBudgetHeadDescription());
	//					fin.setCommiteeScheduleId(Long.parseLong(committeescheduleid));
	//					fin.setBudgetHeadId(finance.getBudgetHeadId());
	//					fin.setTotalSanction(finance.getTotalSanction());
	//					fin.setReSanction(finance.getReSanction());
	//					fin.setFeSanction(finance.getFeSanction());
	//					fin.setReExpenditure(finance.getReExpenditure());
	//					fin.setFeExpenditure(finance.getFeExpenditure());
	//					fin.setReOutCommitment(finance.getReOutCommitment());
	//					fin.setFeOutCommitment(finance.getFeOutCommitment());
	//					fin.setReDipl(finance.getReDipl());
	//					fin.setFeDipl(finance.getFeDipl());
	//					fin.setReBalance(finance.getReBalance());
	//					fin.setFeBalance(finance.getFeBalance());
	//					fin.setProjectId(finance.getProjectId());
	//					
	//					
	//					fin.setCreatedBy(UserId);		
	//					result=service.insertMinutesFinance(fin);
	//				}	
	//				
	//				}
	//		    	
	//		    	List<Object[]> procurementStatusList=(List<Object[]>)service.ProcurementStatusList(projectid);
	//		    	for(Object[] obj:procurementStatusList) 
	//		    	{
	//			    	MinutesProcurementList procure=new MinutesProcurementList();
	//			    	procure.setDemandDate(obj[3].toString());
	//			    	procure.setCommiteeScheduleId(Long.parseLong(committeescheduleid));
	//			    	procure.setDemandNo(obj[1].toString());
	//			    	procure.setDpDate(obj[4]!=null? obj[4].toString() : null );
	//			    	procure.setOrderNo(obj[2]!=null? obj[2].toString() : null );
	//			    	procure.setEstimatedCost(Double.parseDouble(obj[5].toString()));
	//			    	procure.setItemNomenclature(obj[8].toString());
	//			    	procure.setOrderCost(Double.parseDouble(obj[6]!=null?obj[6].toString():"0.00"));
	//			    	procure.setPftsFileId(Long.parseLong(obj[0].toString()));
	//			    	procure.setPftsStageName(obj[10].toString());
	//			    	procure.setPftsStatus(obj[9].toString());
	//			    	procure.setRemarks(obj[11]!=null? obj[11].toString() : null );
	//			    	procure.setVendorName(obj[12]!=null? obj[12].toString() : null );  
	//			    	procure.setPftsStatusId(Long.parseLong(obj[13].toString()));
	//			    	procure.setRevisedDp(obj[7]!=null? obj[7].toString() : null );
	//			    	procure.setCreatedBy(UserId);	
	//			    	result = service.insertMinutesProcurement(procure);
	//		    	}
	//		    	
	//		    	long lastid=service.getLastPmrcId(projectid, committeeid, committeescheduleid);	
	//		    	List<Object[]> actionlist= service.MinutesViewAllActionList(committeescheduleid);
	//		    	if(actionlist.size()>0) {
	//		    	for(Object[] obj:actionlist) {
	//		    		MinutesActionList action=new MinutesActionList();
	//
	//		    		if(obj[9]!=null) 
	//		    		{
	//				    	action.setActionDate(obj[4]!=null? obj[4].toString() : null );
	//				    	action.setActionFlag(obj[10]!=null? obj[10].toString() : null );
	//				    	action.setActionItem(obj[8]!=null? obj[8].toString() : null );
	//			    		action.setActionNo(obj[3]!=null? obj[3].toString() : null );
	//			    		action.setActionStatus(obj[9]!=null? obj[9].toString() : null );
	//			    		action.setAssignee(Long.parseLong(obj[7]+""));
	//			    		action.setAssigneeDesig(obj[14]!=null? obj[14].toString() : null );
	//			    		action.setAssigneeName(obj[13]!=null? obj[13].toString() : null );
	//			    		action.setAssignerDesig(obj[12]!=null? obj[12].toString() : null );
	//			    		action.setAssignor(Long.parseLong(obj[6]+""));
	//			    		action.setAssignorName(obj[11]!=null? obj[11].toString() : null);
	//			    		action.setEndDate(obj[5]!=null? obj[5].toString() : null);
	//		    		
	//		    		}
	//		    		action.setDetails(obj[1]!=null? obj[1].toString() : null);
	//		    		action.setIdrck(obj[2].toString());
	//		    		action.setSchduleMinutesId(Long.parseLong(obj[0].toString()));
	//		    		action.setCommiteeScheduleId(Long.parseLong(committeescheduleid));
	//		    		action.setCreatedBy(UserId);	
	//		    		result=service.insertMinutesAction(action);
	//		    	 }	
	//		    	}
	//		    	List<Object[]> Milelist= service.MilestoneSubsystems(projectid);
	//		    	for(Object[] obj:Milelist) {
	//		    		MinutesSubMile submile=new MinutesSubMile();
	//		    		submile.setActivityName(obj[2]!=null? obj[2].toString() : null);
	//		    		submile.setActivityShort(obj[6]!=null? obj[6].toString() : null);
	//		    		submile.setActivityStatusId(Long.parseLong(obj[5].toString()));
	//		    		submile.setCommitteeScheduleId(Long.parseLong(committeescheduleid));
	//		    		submile.setCreatedBy(UserId);
	//		    		submile.setEndDate(obj[4]!=null? obj[4].toString() : null);
	//		    		submile.setActivityId(Long.parseLong(obj[0].toString()));
	//		    		submile.setMilestoneNo(obj[8]!=null? obj[8].toString() : null);
	//		    		submile.setOrgEndDate(obj[3]!=null? obj[3].toString() : null);
	//		    		submile.setParentActivityId(Long.parseLong(obj[1].toString()));
	//		    		submile.setProgress(obj[7]!=null?Integer.parseInt(obj[7].toString()):0);
	//		    		submile.setStatusRemarks(obj[9]!=null? obj[9].toString() : null);
	//		    		result=service.insertMinutesSubMile(submile);
	//		    	}
	//		    	List<Object[]> actionsixlist= service.ActionPlanSixMonths(projectid);
	//		    	for(Object[] obj:actionsixlist) {
	//		    		MinutesMileActivity mile=MinutesMileActivity.builder()
	//		    		.MinutesMileId(null)
	//		    		.CommitteeScheduleId(Long.parseLong(committeescheduleid))
	//		    		.MilestoneNo(Integer.parseInt(obj[0]+""))
	//    				.MainId(Long.parseLong(obj[1]+""))
	//    				.AId(Long.parseLong(obj[2]+""))
	//    				.BId(Long.parseLong(obj[3]+""))
	//    				.CId(Long.parseLong(obj[4]+""))
	//    				.DId(Long.parseLong(obj[5]+""))
	//    				.EId(Long.parseLong(obj[6]+""))
	//    				.StartDate(obj[7]!=null? obj[7].toString() : null)
	//    				.EndDate(obj[8]!=null? obj[8].toString() : null)
	//    				.MileStoneMain(obj[9]!=null? obj[9].toString() : null)
	//    				.MileStoneA(obj[10]!=null? obj[10].toString() : null)
	//    				.MileStoneB(obj[11]!=null? obj[11].toString() : null)
	//    				.MileStoneC(obj[12]!=null? obj[12].toString() : null)
	//    				.MileStoneD(obj[13]!=null? obj[13].toString() : null)
	//    				.MileStoneE(obj[14]!=null? obj[14].toString() : null)
	//    				.ActivityType(obj[15]!=null? obj[15].toString() : null)
	//    				.ProgressStatus(Integer.parseInt(obj[16]+""))
	//    				.Weightage(Integer.parseInt(obj[17]+""))
	//    				.DateOfCompletion(obj[18]!=null ? obj[18].toString() : null)
	//    				.Activitystatus(obj[19]+"")
	//    				.ActivitystatusId(Integer.parseInt(obj[20]+""))
	//    				.RevisionNo(Integer.parseInt(obj[21]+""))
	//    				.OicEmpId(Long.parseLong(obj[23]+""))
	//    				.EmpName(obj[24]!=null? obj[24].toString() : null)
	//    				.Designation(obj[25]!=null? obj[25].toString() : null)
	//    				.LevelId(Integer.parseInt(obj[26]!=null? obj[26].toString() : null))
	//    				.ActivityShort(obj[27]!=null? obj[27].toString() : null)
	//    				.StatusRemarks( obj[28]!=null ? obj[28].toString() : null)
	//    				.CreatedBy(UserId)
	//    				.build();
	//		    		
	//		    		result=service.insertMinutesMileActivity(mile);		
	//		    	}
	//		    	
	//		    	List<Object[]> lastpmrcactionlist= service.LastPMRCActions(lastid,committeeid,projectid,committeescheduleeditdata[22]+"");
	//		    	for(Object[] obj:lastpmrcactionlist) {
	//		    		MinutesLastPmrc lastpmrc=new MinutesLastPmrc();
	//		    		lastpmrc.setActionFlag(obj[16]!=null? obj[16].toString() : null);
	//		    		lastpmrc.setActionNo(obj[5]!=null? obj[5].toString() : null);
	//		    		lastpmrc.setActionStatus(obj[10]!=null? obj[10].toString() : null);
	//		    		lastpmrc.setAssignee(obj[11]!=null?Long.parseLong(obj[11]+""):0);
	//		    		lastpmrc.setAssigneeDesig(obj[13]!=null? obj[13].toString() : null);
	//		    		lastpmrc.setAssigneeName(obj[12]!=null? obj[12].toString() : null);
	//		    		lastpmrc.setCommiteeScheduleId(Long.parseLong(committeescheduleid));
	//		    		lastpmrc.setDetails(obj[2]!=null? obj[2].toString() : null);
	//		    		if(obj[17]!=null) {
	//		    		lastpmrc.setEndDate(obj[17].toString());
	//		    		}
	//		    		lastpmrc.setIdrck(obj[3].toString());
	//		    		lastpmrc.setMinutesId(obj[0]!=null?Long.parseLong(obj[0]+""):0);
	//		    		lastpmrc.setActionMainId(obj[4]!=null? obj[4].toString() : null);
	//		    		if(obj[6]!=null) {
	//		    		lastpmrc.setPdcOrg(obj[6]!=null? obj[6].toString() : null);
	//		    		}
	//		    		if(obj[7]!=null) {
	//		    		lastpmrc.setPDC1(obj[7]!=null? obj[7].toString() : null);
	//		    		}
	//		    		if(obj[8]!=null) {
	//		    		lastpmrc.setPDC2(obj[8]!=null? obj[8].toString() : null);
	//		    		}
	//		    		lastpmrc.setRevision(obj[9]!=null?Integer.parseInt(obj[9].toString()):0);
	//		    		if(obj[14]!=null) {
	//		    		lastpmrc.setLastDate(obj[14]!=null? obj[14].toString() : null);
	//		    		}
	//		    		lastpmrc.setProgress(obj[18]!=null?Integer.parseInt(obj[18].toString()):0);
	//		    		lastpmrc.setCreatedBy(UserId);	
	//		    		result=service.insertMinutesLastPmrc(lastpmrc);
	//		    		
	//		    	}
	//		    	if(result>0) {
	//		    			int count=service.updateMinutesFrozen(committeescheduleid);
	//		    		
	//		    		}
	//		    	
	//				}
	//		    	
	//		    	}
	//		    	
	//		    	redir.addFlashAttribute("committeescheduleid", committeescheduleid);
	//				redir.addFlashAttribute("specname", req.getParameter("specname"));
	//				redir.addFlashAttribute("membertype",req.getParameter("membertype"));
	//				redir.addFlashAttribute("formname", req.getParameter("formname"));
	//				redir.addFlashAttribute("unit1",req.getParameter("unit1"));
	//		    	return "redirect:/CommitteeScheduleMinutes.htm";
	//		    }
	//		    catch(Exception e) {	    		
	//	    		logger.error(new Date() +" Inside getMinutesFrozen.htm "+UserId, e);
	//	    		e.printStackTrace();
	//	    		return "static/Error";
	//		
	//	    	}
	//			
	//		}


	@PostMapping(value = "/FrozenProjectBriefingPaper.htm")
	public void FrozenProjectBriefingPaper(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside FrozenProjectBriefingPaper.htm "+UserId);		
		try {

			Path filepath = Paths.get(ApplicationFilesDrive, LabCode, "Frozen", 
					"BriefingPaper_P" + req.getParameter("projectid") + 
					"_C" + req.getParameter("committeeid") + 
					"_S" + req.getParameter("scheduleid") + ".pdf");
			String path = ApplicationFilesDrive+LabCode+"\\Frozen\\BriefingPaper_P"+req.getParameter("projectid")+"_C"+req.getParameter("committeeid")+"_S"+req.getParameter("scheduleid")+".pdf";

			res.setContentType("application/pdf");
			res.setHeader("Content-Disposition", String.format("inline; filename=BriefingPaper.pdf"));

			File my_file = filepath.toFile();

			OutputStream out = res.getOutputStream();
			FileInputStream in = new FileInputStream(my_file);
			byte[] buffer = new byte[4096];
			int length;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			in.close();
			out.flush();
			out.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside FrozenProjectBriefingPaper.htm "+UserId, e);
		}
	}


	@RequestMapping(value="CommitteeMinutesNewDfm.htm", method = {RequestMethod.POST,RequestMethod.GET})
	public void CommitteeMinutesNewDfm(HttpServletRequest req,HttpServletResponse res, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeMinutesNewDfm.htm"+UserId);
		try
		{		
			String committeescheduleid = req.getParameter("committeescheduleid");			    
			Object[] committeescheduleeditdata=service.CommitteeScheduleEditData(committeescheduleid);
			String projectid= committeescheduleeditdata[9].toString();
			String committeeid=committeescheduleeditdata[0].toString();

			if(Long.parseLong(projectid) >0 && committeescheduleeditdata[22].toString().equals("Y") ) 
			{
				CommitteeMeetingDPFMFrozen dpfm = service.getFrozenDPFMMinutes(committeescheduleid);
				res.setContentType("application/pdf");
				res.setHeader("Content-Disposition", "inline; name="+ dpfm.getDPFMFileName()+".pdf; filename"+ dpfm.getDPFMFileName());
				Path filepath = Paths.get(uploadpath, LabCode, "DPFM", dpfm.getDPFMFileName());
				//  File f=new File(uploadpath+dpfm.getFrozenDPFMPath() +dpfm.getDPFMFileName());
				File f=filepath.toFile();

				OutputStream out = res.getOutputStream();
				FileInputStream in = new FileInputStream(f);
				byte[] buffer = new byte[4096];
				int length;
				while ((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}
				in.close();
				out.close();

			}
			else 
			{

				Object[] projectdetails = null;

				if(projectid!=null && Integer.parseInt(projectid)>0)
				{
					projectdetails = service.projectdetails(projectid);
					req.setAttribute("projectdetails", projectdetails);
				}
				String divisionid= committeescheduleeditdata[16].toString();
				if(divisionid!=null && Integer.parseInt(divisionid)>0)
				{
					req.setAttribute("divisiondetails", service.DivisionData(divisionid));
				}
				String initiationid= committeescheduleeditdata[17].toString();
				if(initiationid!=null && Integer.parseInt(initiationid)>0)
				{
					req.setAttribute("initiationdetails", service.Initiationdetails(initiationid));
				}

				Committee committee = printservice.getCommitteeData(committeeid);

				List<Object[]> repList = service.getCommitteeMainRepList(committeescheduleeditdata[1]!=null?committeescheduleeditdata[1].toString():"0");
				List<String> repCodes = new ArrayList<>();
				
				if(repList != null && !repList.isEmpty()){
					repCodes = repList.stream().map(obj -> obj[1]!=null ? obj[1].toString() : null).filter(Objects::nonNull).collect(Collectors.toList());
				}

				req.setAttribute("repCodes", repCodes);
				HashMap< String, ArrayList<Object[]>> actionsdata=new LinkedHashMap<String, ArrayList<Object[]>>();
				long lastid=service.getLastPmrcId(projectid, committeeid, committeescheduleid);

				req.setAttribute("committeeminutesspeclist",service.CommitteeScheduleMinutes(committeescheduleid) );
				req.setAttribute("committeescheduleeditdata", committeescheduleeditdata);
				req.setAttribute("committeeminutes",service.CommitteeMinutesSpecNew());
				req.setAttribute("committeeinvitedlist", service.CommitteeAtendance(committeescheduleid,"1"));			
				req.setAttribute("labdetails", service.LabDetails(committeescheduleeditdata[24].toString()));
				req.setAttribute("isprint", "Y");	
				req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(committeescheduleeditdata[24].toString()));
				req.setAttribute("meetingcount",service.MeetingNo(committeescheduleeditdata));
				req.setAttribute("milestonedatalevel6", printservice.BreifingMilestoneDetails(projectid,committee.getCommitteeShortName().trim()));

				//				req.setAttribute("committeeminutessub",service.CommitteeMinutesSub());
				//				req.setAttribute("CommitteeAgendaList", service.CommitteeAgendaList(committeescheduleid));
				String LevelId= "2";
				Object[] MileStoneLevelId = printservice.MileStoneLevelId(projectid,committeeid);
				if( MileStoneLevelId!= null) {
					LevelId= MileStoneLevelId[0].toString();
				}
				req.setAttribute("levelid", LevelId);
				req.setAttribute("labInfo", service.LabDetailes(LabCode));
				/*---------------------------------------------------------------------------------------------------------------*/
				//				if(Long.parseLong(projectid) >0 && committeescheduleeditdata[22].toString().equals("N") ) {

				final String localUri=uri+"/pfms_serv/financialStatusBriefing?ProjectCode="+projectdetails[4].toString()+"&rupess="+10000000;
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				headers.set("labcode", LabCode);
				String jsonResult=null;
				try {
					HttpEntity<String> entity = new HttpEntity<String>(headers);
					ResponseEntity<String> response=restTemplate.exchange(localUri, HttpMethod.POST, entity, String.class);
					jsonResult=response.getBody();						
				}catch(Exception e) {
					req.setAttribute("errorMsg", "errorMsg");
				}
				ObjectMapper mapper = new ObjectMapper();
				mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
				List<ProjectFinancialDetails> projectDetails=null;
				if(jsonResult!=null) {
					try {
						projectDetails = mapper.readValue(jsonResult, new TypeReference<List<ProjectFinancialDetails>>(){});
						req.setAttribute("financialDetails",projectDetails);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				}

				final String localUri2=uri+"/pfms_serv/getTotalDemand";

				String jsonResult2=null;
				try {
					HttpEntity<String> entity = new HttpEntity<String>(headers);
					ResponseEntity<String> response=restTemplate.exchange(localUri2, HttpMethod.POST, entity, String.class);
					jsonResult2=response.getBody();						
				}catch(Exception e) {
					req.setAttribute("errorMsg", "errorMsg");
				}
				ObjectMapper mapper2 = new ObjectMapper();
				mapper2.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				mapper2.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
				List<TotalDemand> totaldemand=null;
				if(jsonResult2!=null) {
					try {
						totaldemand = mapper2.readValue(jsonResult2, new TypeReference<List<TotalDemand>>(){});
						req.setAttribute("TotalProcurementDetails",totaldemand);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				}

				List<Object[]> procurementStatusList=(List<Object[]>)service.ProcurementStatusList(projectid);
				List<Object[]> procurementOnDemand=null;
				List<Object[]> procurementOnSanction=null;


				if(procurementStatusList!=null){
					Map<Object, List<Object[]>> map = procurementStatusList.stream().collect(Collectors.groupingBy(c -> c[9])); 
					Collection<?> keys = map.keySet();
					for(Object key:keys){
						if(key.toString().equals("D")) {
							procurementOnDemand=map.get(key);
						}else if(key.toString().equals("S")) {
							procurementOnSanction=map.get(key);
						}
					}
				}
				List<Object[]> actionlist= service.MinutesViewAllActionList(committeescheduleid);

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

				req.setAttribute("lastpmrcactions", service.LastPMRCActions(lastid,committeeid,projectid,committeescheduleeditdata[22]+""));
				req.setAttribute("actionlist",actionsdata);
				req.setAttribute("procurementOnDemand", procurementOnDemand);
				req.setAttribute("procurementOnSanction", procurementOnSanction);
				req.setAttribute("ActionPlanSixMonths", service.ActionPlanSixMonths(projectid));
				//					 	req.setAttribute("milestonesubsystems", service.MilestoneSubsystems(projectid));
				//				}

				//				else if(committeescheduleeditdata[22].toString().equals("Y") ){
				//					List<Object[]> procurementStatusList=(List<Object[]>)service.getMinutesProcure(committeescheduleid);
				//				 	List<Object[]> procurementOnDemand=null;
				//				 	List<Object[]> procurementOnSanction=null;
				//		 						
				//				 	 if(procurementStatusList!=null){
				//				 		Map<Object, List<Object[]>> map = procurementStatusList.stream().collect(Collectors.groupingBy(c -> c[9])); 
				//				 		Collection<?> keys = map.keySet();
				//				 		for(Object key:keys){
				//				 		    if(key.toString().equals("D")) {
				//				 		    	procurementOnDemand=map.get(key);
				//				 		    }else if(key.toString().equals("S")) {
				//				 		    	procurementOnSanction=map.get(key);
				//				 		    }
				//				 		 }
				//				 	}
				//				 	 
				//				 	req.setAttribute("lastpmrcactions", service.LastPMRCActions(Long.parseLong(committeescheduleid),committeeid,projectid,committeescheduleeditdata[22]+""));
				//				 	req.setAttribute("financialDetails",service.getMinutesFinance(committeescheduleid));
				////					req.setAttribute("milestonesubsystems", service.getMinutesSubMile(committeescheduleid));
				//					req.setAttribute("ActionPlanSixMonths", service.getMinutesMile(committeescheduleid));
				//					req.setAttribute("procurementOnDemand", procurementOnDemand);
				//				 	req.setAttribute("procurementOnSanction", procurementOnSanction);
				//				 	List<Object[]> actionlist= service.getMinutesAction(committeescheduleid);
				//					
				//					
				//					for(Object obj[] : actionlist) {
				//							
				//							ArrayList<Object[]> values=new ArrayList<Object[]>(); 
				//							for(Object obj1[] : actionlist ) {
				//								if(obj1[0].equals(obj[0])) {
				//									values.add(obj1);
				//								}
				//						}
				//							if(!actionsdata.containsKey(obj[0].toString())) {
				//								actionsdata.put(obj[0].toString(), values);
				//							}
				//					} 
				//					req.setAttribute("actionlist",actionsdata);
				//				}
				/*---------------------------------------------------------------------------------------------------------------*/			
				String filename=committeescheduleeditdata[11].toString().replace("/", "-");


				String path=req.getServletContext().getRealPath("/view/temp");
				req.setAttribute("path",path);

				CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
				req.getRequestDispatcher("/view/committee/CommitteeMinutesNewDfm.jsp").forward(req, customResponse);
				String html = customResponse.getOutput();

				HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"));
				req.setAttribute("tableactionlist",  actionsdata);
				CharArrayWriterResponse customResponse1 = new CharArrayWriterResponse(res);
				req.getRequestDispatcher("/view/committee/ActionDetailsTable1.jsp").forward(req, customResponse1);
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
				res.setHeader("Content-Disposition", "inline; name="+ filename+".pdf; filename"+ filename+".pdf");
				File f=new File(path +File.separator+ "merged.pdf");


				OutputStream out = res.getOutputStream();
				FileInputStream in = new FileInputStream(f);
				byte[] buffer = new byte[4096];
				int length;
				while ((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}
				in.close();
				out.close();

				Path pathOfFile2= Paths.get( path+File.separator+filename+"1.pdf"); 
				Files.delete(pathOfFile2);		
				pathOfFile2= Paths.get( path+File.separator+filename+".pdf"); 
				Files.delete(pathOfFile2);	
				pathOfFile2= Paths.get(path +File.separator+ "merged.pdf"); 
				Files.delete(pathOfFile2);

			}
		}
		catch (Exception e) 
		{
			e.printStackTrace(); 
			logger.error(new Date() +"Inside CommitteeMinutesNewDfm.htm "+UserId,e);
		}


	}

	//anil's code for minutes word file




	private static void createParagraph(XWPFDocument doc, String[] texts, String alignment, boolean[] bolds, int[] fontSizes, int spacingAfter) {
		XWPFParagraph paragraph = doc.createParagraph();
		if (texts.length != bolds.length || texts.length != fontSizes.length) {
			throw new IllegalArgumentException("Lengths of texts, bolds, and fontSizes arrays must match.");
		}

		if (texts.length > 0) {
			for (int i = 0; i < texts.length; i++) {
				XWPFRun run = paragraph.createRun();
				run.setText(texts[i]+"      ");
				run.setFontSize(fontSizes[i]);
				run.setBold(bolds[i]);
			}
		}

		if ("CENTER".equals(alignment)) {
			paragraph.setAlignment(ParagraphAlignment.CENTER);
		} else if ("LEFT".equals(alignment)) {
			paragraph.setAlignment(ParagraphAlignment.LEFT);
		}

		// Set the spacing after the paragraph
		paragraph.setSpacingAfter(spacingAfter);
	}

	private static void createParagraph(XWPFDocument doc, String text, String alignment, boolean isBold, int fontSize, int spacingAfter) {
		XWPFParagraph paragraph = doc.createParagraph();
		XWPFRun run = paragraph.createRun();
		run.setText(text);
		run.setFontSize(fontSize);
		if (isBold) {
			run.setBold(true);
		}
		if ("CENTER".equals(alignment)) {
			paragraph.setAlignment(ParagraphAlignment.CENTER);
		} else if ("LEFT".equals(alignment)) {
			paragraph.setAlignment(ParagraphAlignment.LEFT);
		} else if ("RIGHT".equals(alignment)) {
			paragraph.setAlignment(ParagraphAlignment.RIGHT);
		}

		// Set the spacing after the paragraph
		paragraph.setSpacingAfter(spacingAfter);
	}


	private static int setTableRowData(XWPFTable table, int row, int length, String[] cellTexts) {
		XWPFTableRow tableRow = table.getRow(row);
		int rowIndex;

		if (tableRow == null) {
			// If the row doesn't exist, create a new row
			tableRow = table.createRow();
			rowIndex = table.getRows().indexOf(tableRow);
		} else {
			rowIndex = row;
		}

		for (int i = 0; i < length; i++) {
			XWPFTableCell cell = tableRow.getCell(i);

			if (cell == null) {
				// If the cell doesn't exist, create a new cell
				cell = tableRow.createCell();
			}

			cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

			XWPFParagraph paragraphAT = cell.getParagraphs().get(0);
			XWPFRun runAT = null;

			if (paragraphAT.getRuns().isEmpty()) {
				runAT = paragraphAT.createRun();
			} else {
				runAT = paragraphAT.getRuns().get(0);
			}

			if (cellTexts[i] != null) {
				runAT.setText(cellTexts[i]);
				runAT.setBold(true);
			}

			paragraphAT.setAlignment(ParagraphAlignment.CENTER);
			runAT.setBold(true);
		}

		return rowIndex;
	}


	private static void mergrColums(XWPFTable table,int numCellsToMerge,int row) {
		XWPFTableCell cell = table.getRow(row).getCell(0);
		for (int i = 1; i < numCellsToMerge; i++) {
			cell.getCTTc().addNewTcPr().addNewGridSpan().setVal(BigInteger.valueOf(numCellsToMerge));
		}

		// Get the row and cells in the same row for the next columns
		XWPFTableRow sameRow = table.getRow(table.getNumberOfRows() - 1);
		for (int i = 1; i < numCellsToMerge; i++) {
			XWPFTableCell nextCell = sameRow.getCell(1);
			sameRow.getCtRow().removeTc(sameRow.getTableCells().indexOf(nextCell));
		}
	}

	private static void setTableWidth(XWPFTable table,int totalTableWidth,int[] percentageColumnWidths) {
		// Get the table properties and set the width
		CTTblPr tblPr = table.getCTTbl().getTblPr();
		if (tblPr == null) {
			tblPr = table.getCTTbl().addNewTblPr();
		}
		CTTblWidth tblWidth = tblPr.getTblW();
		if (tblWidth == null) {
			tblWidth = tblPr.addNewTblW();
		}
		tblWidth.setType(STTblWidth.PCT);
		tblWidth.setW(BigInteger.valueOf(totalTableWidth));

		// Set the column widths
		CTTblGrid tblGrid = table.getCTTbl().addNewTblGrid();
		for (int width : percentageColumnWidths) {
			CTTblGridCol tblGridCol = tblGrid.addNewGridCol();
			tblGridCol.setW(BigInteger.valueOf((width * totalTableWidth) / 100));
		}
	}

	private static void setTableHeader(XWPFTableRow thirdRow) {
		// Get the merged cell (the first cell in the first row)
		XWPFTableCell mergedCell = thirdRow.getCell(0);
		mergedCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

		// Set cell color (if needed)
		mergedCell.setColor("auto");

		// Get the paragraph within the merged cell
		XWPFParagraph paragraphTable = mergedCell.getParagraphs().get(0);

		String formattedText = "NA : Not Assigned  AA : Activity Assigned  OG : On Going  DO : Delay - On Going  RC : Review & Close  FD : Forwarded With Delay  CO : Completed  CD : Completed with Delay  IA : InActive  DD : Delayed days";

		String[] wordAndFormat = formattedText.split("\\s+");

		for (int i = 0; i < wordAndFormat.length; i++) {
			String text = wordAndFormat[i];
			XWPFRun run3 = paragraphTable.createRun();
			run3.setFontSize(9); 
			// Check if the text is a word (not a formatting code)
			if (!text.matches(".*:[\\s\\w]*")) {
				// Apply red color to specific words
				if (text.equals("NA") || text.equals("AA") || text.equals("OG") ||
						text.equals("DO") || text.equals("RC") || text.equals("FD") ||
						text.equals("CO") || text.equals("CD") || text.equals("IA") ||
						text.equals("DD")) {
					run3.setColor("FF0000"); // Red color
				} else {
					run3.setColor("000000"); // Black color (default)
				}
			}
			// Add a space after "NA : Not Assigned" except for the last item
			if ((text.equals("Assigned") && i < wordAndFormat.length - 1) || text.equals("Going") || text.equals("Close") || text.equals("ForwardedWithDelay") || text.equals("InActive") || text.equals("Delayeddays") || text.equals("Completed")) {
				text += "  ";
			}
			run3.setText(text);

		}

		// Set the alignment of the paragraph to center
		paragraphTable.setAlignment(ParagraphAlignment.CENTER);

	}
	private static void mergeColums(XWPFTable table,int rowNo,int startIdx,int endIdx ) {
		XWPFTableRow futureRow = table.getRow(rowNo);
		XWPFTableCell lastCell5 = futureRow.getCell(startIdx);
		//  XWPFTableCell lastCell2 = seventhRow.getCell(6);

		// Merge the last two cells
		CTTcPr tcPr3 = lastCell5.getCTTc().addNewTcPr();
		tcPr3.addNewGridSpan().setVal(BigInteger.valueOf(2));

		futureRow.getCtRow().removeTc(endIdx);
	}	
	private static void mergeRows(XWPFTable table,int rowNo1,int rowNo2,int startIdx,int endIdx) {
		XWPFTableCell firstCellFirstRow = table.getRow(rowNo1).getCell(startIdx);
		XWPFTableCell firstCellSecondRow = table.getRow(rowNo2).getCell(endIdx);

		// Merge the cells by setting the vMerge property
		CTTcPr tcPrFirstCellFirstRow = firstCellFirstRow.getCTTc().isSetTcPr() ? firstCellFirstRow.getCTTc().getTcPr() : firstCellFirstRow.getCTTc().addNewTcPr();
		CTTcPr tcPrFirstCellSecondRow = firstCellSecondRow.getCTTc().isSetTcPr() ? firstCellSecondRow.getCTTc().getTcPr() : firstCellSecondRow.getCTTc().addNewTcPr();

		CTVMerge vMerge = tcPrFirstCellFirstRow.isSetVMerge() ? tcPrFirstCellFirstRow.getVMerge() : tcPrFirstCellFirstRow.addNewVMerge();
		vMerge.setVal(STMerge.RESTART);

		vMerge = tcPrFirstCellSecondRow.isSetVMerge() ? tcPrFirstCellSecondRow.getVMerge() : tcPrFirstCellSecondRow.addNewVMerge();
		vMerge.setVal(STMerge.CONTINUE);
	}



	private static void createCell2(XWPFTableRow row, String value, int cell) {
		XWPFTableCell cell2 = row.getCell(cell);
		if (value != null) {
			String[] lines = value.split("\n");
			for (int i = 0; i < lines.length; i++) {
				if (i > 0) {
					cell2.addParagraph().createRun().addBreak();
				}
				cell2.addParagraph().createRun().setText(lines[i]);
			}

		} else {
			cell2.setText("-");
		}
		cell2.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
	}
	private static void createCell(XWPFTableRow row, String value, int cell) {
		XWPFTableCell cell2 = row.getCell(cell);
		if (value != null) {
			cell2.setText(value);
		} else {
			cell2.setText("-");
		}
		cell2.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
	}






	@RequestMapping(value = "CommitteeMinutesNewWordDownload.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String CommitteeMinutesNewWordDownload(HttpServletRequest req, HttpServletResponse res, HttpSession ses,
			RedirectAttributes redir) throws Exception {
		long startTime = System.currentTimeMillis();
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() + "Inside CommitteeMinutesNewWordDownload.htm " + UserId);


		try {
			String committeescheduleid = req.getParameter("committeescheduleid");			
			Object[] committeescheduleeditdata=service.CommitteeScheduleEditData(committeescheduleid);
			String projectid= committeescheduleeditdata[9].toString();
			String committeeid=committeescheduleeditdata[0].toString();
			Object[] projectdetails = null;
			if(projectid!=null && Integer.parseInt(projectid)>0)
			{
				projectdetails = service.projectdetails(projectid);
				req.setAttribute("projectdetails", projectdetails);
			}
			String divisionid= committeescheduleeditdata[16].toString();
			if(divisionid!=null && Integer.parseInt(divisionid)>0)
			{
				req.setAttribute("divisiondetails", service.DivisionData(divisionid));
			}
			String initiationid= committeescheduleeditdata[17].toString();
			if(initiationid!=null && Integer.parseInt(initiationid)>0)
			{
				req.setAttribute("initiationdetails", service.Initiationdetails(initiationid));
			}

			Committee committee = printservice.getCommitteeData(committeeid);

			List<Object[]> repList = service.getCommitteeMainRepList(committeescheduleeditdata[1]!=null?committeescheduleeditdata[1].toString():"0");
			List<String> repCodes = new ArrayList<>();
			
			if(repList != null && !repList.isEmpty()){
				repCodes = repList.stream().map(obj -> obj[1]!=null ? obj[1].toString() : null).filter(Objects::nonNull).collect(Collectors.toList());
			}

			req.setAttribute("repCodes", repCodes);
			HashMap< String, ArrayList<Object[]>> actionsdata=new LinkedHashMap<String, ArrayList<Object[]>>();
			long lastid=service.getLastPmrcId(projectid, committeeid, committeescheduleid);

			//****************************envi download start**********************************
			List<Object[]> envisagedDemandlist  = new ArrayList<Object[]>();
			envisagedDemandlist=service.getEnvisagedDemandList(projectid);
			req.setAttribute("envisagedDemandlist", envisagedDemandlist);

			req.setAttribute("committeeminutesspeclist",service.CommitteeScheduleMinutes(committeescheduleid) );
			req.setAttribute("committeescheduleeditdata", committeescheduleeditdata);
			req.setAttribute("committeeminutes",service.CommitteeMinutesSpecNew());
			req.setAttribute("committeeinvitedlist", service.CommitteeAtendance(committeescheduleid,"1"));			
			req.setAttribute("labdetails", service.LabDetails(committeescheduleeditdata[24].toString()));
			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(committeescheduleeditdata[24].toString()));
			req.setAttribute("meetingcount",service.MeetingNo(committeescheduleeditdata));
			//	req.setAttribute("milestonedatalevel6", printservice.BreifingMilestoneDetails(projectid,committee.getCommitteeShortName().trim()));
			//					req.setAttribute("milestonedatalevel6", printservice.BreifingMilestoneDetails(projectid,committeeid));
			req.setAttribute("milestonedatalevel6", printservice.BreifingMilestoneDetails(projectid,committeeid,committeescheduleeditdata[2].toString()));
			//					req.setAttribute("committeeminutessub",service.CommitteeMinutesSub());
			//					req.setAttribute("CommitteeAgendaList", service.CommitteeAgendaList(committeescheduleid));
			String LevelId= "2";
			Object[] MileStoneLevelId = printservice.MileStoneLevelId(projectid,committeeid);
			if( MileStoneLevelId!= null) {
				LevelId= MileStoneLevelId[0].toString();
			}
			req.setAttribute("levelid", LevelId);
			req.setAttribute("labInfo", service.LabDetailes(LabCode));
			/*---------------------------------------------------------------------------------------------------------------*/
			//					if(Long.parseLong(projectid) >0 && committeescheduleeditdata[22].toString().equals("N") ) {

			final String localUri=uri+"/pfms_serv/financialStatusBriefing?ProjectCode="+projectdetails[4].toString()+"&rupess="+10000000;
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.set("labcode", LabCode);
			String jsonResult=null;
			try {
				HttpEntity<String> entity = new HttpEntity<String>(headers);
				ResponseEntity<String> response=restTemplate.exchange(localUri, HttpMethod.POST, entity, String.class);
				jsonResult=response.getBody();						
			}catch(Exception e) {
				req.setAttribute("errorMsg", "errorMsg");
			}
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
			List<ProjectFinancialDetails> projectDetails=null;
			if(jsonResult!=null) {
				try {
					projectDetails = mapper.readValue(jsonResult, new TypeReference<List<ProjectFinancialDetails>>(){});
					req.setAttribute("financialDetails",projectDetails);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}

			final String localUri2=uri+"/pfms_serv/getTotalDemand";

			String jsonResult2=null;
			try {
				HttpEntity<String> entity = new HttpEntity<String>(headers);
				ResponseEntity<String> response=restTemplate.exchange(localUri2, HttpMethod.POST, entity, String.class);
				jsonResult2=response.getBody();						
			}catch(Exception e) {
				req.setAttribute("errorMsg", "errorMsg");
			}
			ObjectMapper mapper2 = new ObjectMapper();
			mapper2.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			mapper2.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
			List<TotalDemand> totaldemand=null;
			if(jsonResult2!=null) {
				try {
					totaldemand = mapper2.readValue(jsonResult2, new TypeReference<List<TotalDemand>>(){});
					req.setAttribute("TotalProcurementDetails",totaldemand);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}

			List<Object[]> procurementStatusList=(List<Object[]>)service.ProcurementStatusList(projectid);
			List<Object[]> procurementOnDemand=null;
			List<Object[]> procurementOnSanction=null;


			if(procurementStatusList!=null){
				Map<Object, List<Object[]>> map = procurementStatusList.stream().collect(Collectors.groupingBy(c -> c[9])); 
				Collection<?> keys = map.keySet();
				for(Object key:keys){
					if(key.toString().equals("D")) {
						procurementOnDemand=map.get(key);
					}else if(key.toString().equals("S")) {
						procurementOnSanction=map.get(key);
					}
				}
			}
			List<Object[]> actionlist= service.MinutesViewAllActionList(committeescheduleid);

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

			//req.setAttribute("lastpmrcactions", service.LastPMRCActions(lastid,committeeid,projectid,committeescheduleeditdata[22]+""));
			req.setAttribute("lastpmrcactions", printservice.LastPMRCActions(projectid,committeeid));
			//						req.setAttribute("actionlist",actionsdata);
			req.setAttribute("procurementOnDemand", procurementOnDemand);
			req.setAttribute("procurementOnSanction", procurementOnSanction);
			req.setAttribute("ActionPlanSixMonths", service.ActionPlanSixMonths(projectid));
			req.setAttribute("projectdatadetails", service.ProjectDataDetails(projectid));
			// new code
			List<Object[]>totalMileStones=service.totalProjectMilestones(projectid);//get all the milestones details based on projectid
			List<Object[]>first=null;   //store the milestones with levelid 1
			List<Object[]>second=null;	// store the milestones with levelid 2
			List<Object[]>three= null; // store the milestones with levelid 3
			Map<Integer,String> treeMapLevOne = new TreeMap<>();  // store the milestoneid with level id 1 and counts 
			Map<Integer,String>treeMapLevTwo= new TreeMap<>(); // store the milestonidid with level id 2 and counts
			Map<Integer,String>treeMapLevThree= new TreeMap<>();  // store the milestoneid with level id 3 and counts 
			TreeSet<Integer> AllMilestones = new TreeSet<>();   // store the number of milestone in sorted order
			if(!totalMileStones.isEmpty()) {
				for(Object[]obj:totalMileStones){
					AllMilestones.add(Integer.parseInt(obj[22].toString())); // getting the milestones from list
				}
				for(Integer mile:AllMilestones) {
					int count=1;
					first=totalMileStones.stream().
							filter(i->i[26].toString().equalsIgnoreCase("1") && i[22].toString().equalsIgnoreCase(mile+""))
							.map(objectArray -> new Object[]{objectArray[0], objectArray[2]})
							.collect(Collectors.toList());
					for(Object[]obj:first) {
						treeMapLevOne.put(Integer.parseInt(obj[1].toString()),"A"+(count++));// to get the first level
					}
				}
				for (Map.Entry<Integer,String> entry : treeMapLevOne.entrySet()) {
					int count=1;
					second=totalMileStones.stream().
							filter(i->i[26].toString().equalsIgnoreCase("2") && i[2].toString().equalsIgnoreCase(entry.getKey()+""))
							.map(objectArray -> new Object[] {entry.getKey(),objectArray[3]})
							.collect(Collectors.toList());
					for(Object[]obj:second) {
						treeMapLevTwo.put(Integer.parseInt(obj[1].toString()),entry.getValue()+"-B"+(count++));
					}
				}
				for(Map.Entry<Integer,String>entry: treeMapLevTwo.entrySet()) {
					int count=1;
					three=totalMileStones.stream().
							filter(i->i[26].toString().equalsIgnoreCase("3") && i[3].toString().equalsIgnoreCase(entry.getKey()+""))
							.map(objectArray -> new Object[] {entry.getKey(),objectArray[4]})
							.collect(Collectors.toList());
					for(Object[]obj:three) {
						treeMapLevThree.put(Integer.parseInt(obj[1].toString()), "C"+(count++)); 
					}
				}
			}
			req.setAttribute("treeMapLevOne", treeMapLevOne);
			req.setAttribute("treeMapLevTwo", treeMapLevTwo);
			// new code end
			//code on 06-10-2023				 	 
			List<List<Object[]>> ReviewMeetingList = new ArrayList<List<Object[]>>();
			List<List<Object[]>> ReviewMeetingListPMRC = new ArrayList<List<Object[]>>();
			ReviewMeetingList.add(printservice.ReviewMeetingList(projectid, "EB"));
			ReviewMeetingListPMRC.add(printservice.ReviewMeetingList(projectid, "PMRC")); 
			Map<Integer,String> mappmrc = new HashMap<>();
			Map<Integer,String> mapEB = new HashMap<>();
			int pmrccount=0;
			for (Object []obj:ReviewMeetingListPMRC.get(0)) {
				mappmrc.put(++pmrccount,obj[3].toString());
			}

			int ebcount=0;

			for (Object []obj:ReviewMeetingList.get(0)) {
				mapEB.put(++ebcount,obj[3].toString());
			}
			req.setAttribute("mappmrc", mappmrc);
			req.setAttribute("mapEB", mapEB);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside CommitteeMinutesNewWordDownload.htm " + UserId, e);
		}
		return "committee/CommitteeMinutesNewDoc";
	}






	@RequestMapping(value="SendMinutes.htm", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody String SendMinutes(HttpServletRequest req,HttpServletResponse res, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		String EmpNo=(String)ses.getAttribute("EmpNo");
		String EmpName=(String)ses.getAttribute("EmpName");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside SendMinutes.htm "+UserId);
		int mailcount=0;

		try
		{		
			String committeescheduleid = req.getParameter("committeescheduleid");			
			String PROTECTED_MINUTES = req.getParameter("PROTECTED MINUTES");

			Object[] committeescheduleeditdata=service.CommitteeScheduleEditData(committeescheduleid);
			String projectid= committeescheduleeditdata[9].toString();
			String committeeid=committeescheduleeditdata[0].toString();
			if(Long.parseLong(projectid) >0 && committeescheduleeditdata[22].toString().equals("Y")) 
			{}
			else 
			{

				Object[] projectdetails = null;

				if(projectid!=null && Integer.parseInt(projectid)>0)
				{
					projectdetails = service.projectdetails(projectid);
					req.setAttribute("projectdetails", projectdetails);
				}
				String divisionid= committeescheduleeditdata[16].toString();
				if(divisionid!=null && Integer.parseInt(divisionid)>0)
				{
					req.setAttribute("divisiondetails", service.DivisionData(divisionid));
				}
				String initiationid= committeescheduleeditdata[17].toString();
				if(initiationid!=null && Integer.parseInt(initiationid)>0)
				{
					req.setAttribute("initiationdetails", service.Initiationdetails(initiationid));
				}

				Committee committee = printservice.getCommitteeData(committeeid);

				List<Object[]> repList = service.getCommitteeMainRepList(committeescheduleeditdata[1]!=null?committeescheduleeditdata[1].toString():"0");
				List<String> repCodes = new ArrayList<>();
				
				if(repList != null && !repList.isEmpty()){
					repCodes = repList.stream().map(obj -> obj[1]!=null ? obj[1].toString() : null).filter(Objects::nonNull).collect(Collectors.toList());
				}

				req.setAttribute("repCodes", repCodes);
				HashMap< String, ArrayList<Object[]>> actionsdata=new LinkedHashMap<String, ArrayList<Object[]>>();
				long lastid=service.getLastPmrcId(projectid, committeeid, committeescheduleid);

				//****************************envi download start**********************************
				List<Object[]> envisagedDemandlist  = new ArrayList<Object[]>();
				envisagedDemandlist=service.getEnvisagedDemandList(projectid);
				req.setAttribute("envisagedDemandlist", envisagedDemandlist);

				req.setAttribute("committeeminutesspeclist",service.CommitteeScheduleMinutes(committeescheduleid) );
				req.setAttribute("committeescheduleeditdata", committeescheduleeditdata);
				req.setAttribute("committeeminutes",service.CommitteeMinutesSpecNew());
				req.setAttribute("committeeinvitedlist", service.CommitteeAtendance(committeescheduleid,"1"));			
				req.setAttribute("labdetails", service.LabDetails(committeescheduleeditdata[24].toString()));
				req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(committeescheduleeditdata[24].toString()));
				req.setAttribute("meetingcount",service.MeetingNo(committeescheduleeditdata));
				req.setAttribute("milestonedatalevel6", printservice.BreifingMilestoneDetails(projectid,committeeid));

				// this method will show the milestones based on each meeting date
				//req.setAttribute("milestonedatalevel6", printservice.BreifingMilestoneDetails(projectid,committeeid,committeescheduleeditdata[2].toString()));

				//				req.setAttribute("committeeminutessub",service.CommitteeMinutesSub());
				//				req.setAttribute("CommitteeAgendaList", service.CommitteeAgendaList(committeescheduleid));
				String LevelId= "2";
				Object[] MileStoneLevelId = printservice.MileStoneLevelId(projectid,committeeid);
				if( MileStoneLevelId!= null) {
					LevelId= MileStoneLevelId[0].toString();
				}
				req.setAttribute("levelid", LevelId);
				req.setAttribute("labInfo", service.LabDetailes(LabCode));
				/*---------------------------------------------------------------------------------------------------------------*/
				//				if(Long.parseLong(projectid) >0 && committeescheduleeditdata[22].toString().equals("N") ) {

				final String localUri=uri+"/pfms_serv/financialStatusBriefing?ProjectCode="+projectdetails[4].toString()+"&rupess="+10000000;
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				headers.set("labcode", LabCode);
				String jsonResult=null;
				try {
					HttpEntity<String> entity = new HttpEntity<String>(headers);
					ResponseEntity<String> response=restTemplate.exchange(localUri, HttpMethod.POST, entity, String.class);
					jsonResult=response.getBody();						
				}catch(Exception e) {
					req.setAttribute("errorMsg", "errorMsg");
				}
				ObjectMapper mapper = new ObjectMapper();
				mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
				List<ProjectFinancialDetails> projectDetails=null;
				if(jsonResult!=null) {
					try {
						projectDetails = mapper.readValue(jsonResult, new TypeReference<List<ProjectFinancialDetails>>(){});
						req.setAttribute("financialDetails",projectDetails);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				}

				final String localUri2=uri+"/pfms_serv/getTotalDemand";

				String jsonResult2=null;
				try {
					HttpEntity<String> entity = new HttpEntity<String>(headers);
					ResponseEntity<String> response=restTemplate.exchange(localUri2, HttpMethod.POST, entity, String.class);
					jsonResult2=response.getBody();						
				}catch(Exception e) {
					req.setAttribute("errorMsg", "errorMsg");
				}
				ObjectMapper mapper2 = new ObjectMapper();
				mapper2.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				mapper2.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
				List<TotalDemand> totaldemand=null;
				if(jsonResult2!=null) {
					try {
						totaldemand = mapper2.readValue(jsonResult2, new TypeReference<List<TotalDemand>>(){});
						req.setAttribute("TotalProcurementDetails",totaldemand);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				}

				List<Object[]> procurementStatusList=(List<Object[]>)service.ProcurementStatusList(projectid);
				List<Object[]> procurementOnDemand=null;
				List<Object[]> procurementOnSanction=null;


				if(procurementStatusList!=null){
					Map<Object, List<Object[]>> map = procurementStatusList.stream().collect(Collectors.groupingBy(c -> c[9])); 
					Collection<?> keys = map.keySet();
					for(Object key:keys){
						if(key.toString().equals("D")) {
							procurementOnDemand=map.get(key);
						}else if(key.toString().equals("S")) {
							procurementOnSanction=map.get(key);
						}
					}
				}
				List<Object[]> actionlist= service.MinutesViewAllActionList(committeescheduleid);

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

				req.setAttribute("lastpmrcactions", printservice.LastPMRCActions(projectid,committeeid));
				//						req.setAttribute("actionlist",actionsdata);
				req.setAttribute("procurementOnDemand", procurementOnDemand);
				req.setAttribute("procurementOnSanction", procurementOnSanction);
				req.setAttribute("ActionPlanSixMonths", service.ActionPlanSixMonths(projectid));
				req.setAttribute("projectdatadetails", service.ProjectDataDetails(projectid));
				// new code
				List<Object[]>totalMileStones=service.totalProjectMilestones(projectid);//get all the milestones details based on projectid
				List<Object[]>first=null;   //store the milestones with levelid 1
				List<Object[]>second=null;	// store the milestones with levelid 2
				List<Object[]>three= null; // store the milestones with levelid 3
				Map<Integer,String> treeMapLevOne = new TreeMap<>();  // store the milestoneid with level id 1 and counts 
				Map<Integer,String>treeMapLevTwo= new TreeMap<>(); // store the milestonidid with level id 2 and counts
				Map<Integer,String>treeMapLevThree= new TreeMap<>();  // store the milestoneid with level id 3 and counts 
				TreeSet<Integer> AllMilestones = new TreeSet<>();   // store the number of milestone in sorted order
				if(!totalMileStones.isEmpty()) {
					for(Object[]obj:totalMileStones){
						AllMilestones.add(Integer.parseInt(obj[22].toString())); // getting the milestones from list
					}
					for(Integer mile:AllMilestones) {
						int count=1;
						first=totalMileStones.stream().
								filter(i->i[26].toString().equalsIgnoreCase("1") && i[22].toString().equalsIgnoreCase(mile+""))
								.map(objectArray -> new Object[]{objectArray[0], objectArray[2]})
								.collect(Collectors.toList());
						for(Object[]obj:first) {
							treeMapLevOne.put(Integer.parseInt(obj[1].toString()),"A"+(count++));// to get the first level
						}
					}
					for (Map.Entry<Integer,String> entry : treeMapLevOne.entrySet()) {
						int count=1;
						second=totalMileStones.stream().
								filter(i->i[26].toString().equalsIgnoreCase("2") && i[2].toString().equalsIgnoreCase(entry.getKey()+""))
								.map(objectArray -> new Object[] {entry.getKey(),objectArray[3]})
								.collect(Collectors.toList());
						for(Object[]obj:second) {
							treeMapLevTwo.put(Integer.parseInt(obj[1].toString()),entry.getValue()+"-B"+(count++));
						}
					}
					for(Map.Entry<Integer,String>entry: treeMapLevTwo.entrySet()) {
						int count=1;
						three=totalMileStones.stream().
								filter(i->i[26].toString().equalsIgnoreCase("3") && i[3].toString().equalsIgnoreCase(entry.getKey()+""))
								.map(objectArray -> new Object[] {entry.getKey(),objectArray[4]})
								.collect(Collectors.toList());
						for(Object[]obj:three) {
							treeMapLevThree.put(Integer.parseInt(obj[1].toString()), "C"+(count++)); 

						}
					}
				}
				req.setAttribute("treeMapLevOne", treeMapLevOne);
				req.setAttribute("treeMapLevTwo", treeMapLevTwo);
				// new code end
				//code on 06-10-2023				 	 
				List<List<Object[]>> ReviewMeetingList = new ArrayList<List<Object[]>>();
				List<List<Object[]>> ReviewMeetingListPMRC = new ArrayList<List<Object[]>>();
				ReviewMeetingList.add(printservice.ReviewMeetingList(projectid, "EB"));
				ReviewMeetingListPMRC.add(printservice.ReviewMeetingList(projectid, "PMRC")); 
				Map<Integer,String> mappmrc = new HashMap<>();
				Map<Integer,String> mapEB = new HashMap<>();
				int pmrccount=0;
				for (Object []obj:ReviewMeetingListPMRC.get(0)) {
					mappmrc.put(++pmrccount,obj[3].toString());
				}

				int ebcount=0;

				for (Object []obj:ReviewMeetingList.get(0)) {
					mapEB.put(++ebcount,obj[3].toString());
				}
				req.setAttribute("mappmrc", mappmrc);
				req.setAttribute("mapEB", mapEB);
				Object[]momAttachment=service.MomAttachmentFile(committeescheduleid);
				req.setAttribute("tableactionlist",  actionsdata);
				//
				String filename=committeescheduleeditdata[11].toString().replace("/", "-");
				String path=req.getServletContext().getRealPath("/view/temp");
				req.setAttribute("path",path);
				CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
				req.getRequestDispatcher("/view/committee/CommitteeMinutesNew.jsp").forward(req, customResponse);
				String html = customResponse.getOutput();


				byte[]data=html.getBytes();
				InputStream fis1 = new ByteArrayInputStream(data);
				PdfDocument pdfDoc = new PdfDocument(new PdfWriter(path + "/" + filename +  ".pdf"));
				pdfDoc.setTagged(); 
				Document document = new Document(pdfDoc, PageSize.LEGAL);
				document.setMargins(50, 100, 150, 50);
				ConverterProperties converterProperties = new ConverterProperties();
				FontProvider dfp = new DefaultFontProvider(true, true, true);
				converterProperties.setFontProvider(dfp);
				HtmlConverter.convertToPdf(fis1, pdfDoc, converterProperties);

				PdfWriter pdfw=null;

				if(PROTECTED_MINUTES == null) {
					pdfw=new PdfWriter(path +File.separator+ "MoM.pdf");
				}else {

					String password = LabCode+"123";

					pdfw = new PdfWriter(path +File.separator+ "MoM.pdf",
							new WriterProperties().setStandardEncryption(password.getBytes(), password.getBytes(),
									EncryptionConstants.ALLOW_PRINTING, EncryptionConstants.ENCRYPTION_AES_128));
				}
				PdfDocument pdfDocs = new PdfDocument(pdfw);
				pdfw.setCompressionLevel(CompressionConstants.BEST_COMPRESSION);
				PdfDocument pdfDocMain = new PdfDocument(new PdfReader(path+File.separator+filename+".pdf"),new PdfWriter(path+File.separator+filename+"Maintemp.pdf"));
				Document docMain = new Document(pdfDocMain,PageSize.A4);
				docMain.setMargins(50, 50, 50, 50);

				docMain.close();
				pdfDocMain.close();
				Path pathOfFileMain= Paths.get( path+File.separator+filename+".pdf");


				PdfReader pdf1=new PdfReader(path+File.separator+filename+"Maintemp.pdf");
				PdfDocument pdfDocument = new PdfDocument(pdf1, pdfw);
				PdfMerger merger = new PdfMerger(pdfDocument);

				if(momAttachment!=null) {
					if(momAttachment[2]!=null) {
						if(FilenameUtils.getExtension(momAttachment[2].toString()).equalsIgnoreCase("pdf")) {
							PdfDocument pdfDocument1 = new PdfDocument(new PdfReader(env.getProperty("ApplicationFilesDrive")+momAttachment[1]+"\\"+momAttachment[2]),new PdfWriter(path+File.separator+filename+"temp.pdf"));
							Document document4 = new Document(pdfDocument1,PageSize.A4);
							document4.setMargins(50, 50, 50, 50);
							Rectangle pageSize;
							PdfCanvas canvas;
							int n = pdfDocument1.getNumberOfPages();

							for(int i=1;i<=n;i++) {
								PdfPage page = pdfDocument1.getPage(i);
								pageSize = page.getPageSize();
								canvas = new PdfCanvas(page);
								Rectangle recta=new Rectangle(10,pageSize.getHeight()-50,40,40); // Left side position
								Rectangle recta2=new Rectangle(pageSize.getWidth()-50,pageSize.getHeight()-50,40,40); // Right side position


								// Add content on the left side
								String[] no=committeescheduleeditdata[11].toString().split("/");


								// Add content on the right side

								canvas.beginText()
								.setFontAndSize(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN), 10)
								.moveText(pageSize.getWidth()-200 , pageSize.getHeight() - 10)
								.showText("Attachment to MoM - "+no[2]+"/"+(service.MeetingNo(committeescheduleeditdata)>0?service.MeetingNo(committeescheduleeditdata):"")+"/"+projectdetails[4].toString())
								.endText();

							}
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
				}



				pdfDocument.close();
				merger.close();

				pdf1.close();	       
				pdfw.close();
				res.setContentType("application/pdf");
				res.setHeader("Content-disposition","inline;filename="+filename+".pdf"); 
				File f=new File(path +File.separator+ "MoM.pdf");




				ArrayList<String> emails= new ArrayList<String>();
				ArrayList<String> membertypes=new ArrayList<String>(Arrays.asList("CC","CS","PS","CI","I","P","CH"));

				List<Object[]>committeeinvitedlist=service.CommitteeAtendance(committeescheduleid,"1");

				String MainEmail="";

				for(Object[] obj : committeeinvitedlist) 
				{	 
					if(membertypes.contains(obj[3].toString())   && obj[4].toString().equalsIgnoreCase("P")) 
					{
						if(obj[8]!=null) {
							emails.add(obj[8].toString());
						}
					}

					if(obj[3].toString().equalsIgnoreCase("CC")) {
						MainEmail=obj[8].toString();
					}
				}
				if(emails.contains(MainEmail)) {
					emails.remove(MainEmail);
				}


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
				System.out.println("TLSEmail Start");
				long startTime = System.currentTimeMillis();

				Properties properties = System.getProperties(); 
				if(LabCode.equalsIgnoreCase("ADE")) {
					properties.setProperty("mail.smtp.host", hostAddress);
					properties.put("mail.smtp.ssl.enable", "true"); //TLS
					properties.put("mail.smtp.port", mailAuthentication.getPort()); 
					properties.put("mail.smtp.auth", "true"); 
					properties.put("mail.smtp.socketFactory.class","jakarta.net.ssl.SSLSocketFactory");
					properties.put("mail.smtp.ssl.checkserveridentity", false);
					properties.put("mail.smtp.ssl.trust", hostAddress);
				}else {
				properties.setProperty("mail.smtp.host", hostAddress);
				//properties.put("mail.smtp.starttls.enable", "true"); //TLS
				properties.put("mail.smtp.port", mailAuthentication.getPort()); 
				properties.put("mail.smtp.auth", "true"); 
				properties.put("mail.smtp.socketFactory.class","jakarta.net.ssl.SSLSocketFactory"); 
				} 
				Session session = Session.getDefaultInstance(properties,
						new jakarta.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(from,password);
					}
				});	
				try {
					MimeMessage message = new MimeMessage(session);
					message.setFrom(new InternetAddress(from));
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(MainEmail));
					message.setSubject("Mom attachement for "+committeescheduleeditdata[11].toString());
					MimeBodyPart part1 = new MimeBodyPart();
					for (String ccRecipient : emails) {
						message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccRecipient));
					}
					part1.setText("This is a System generated mail please Don't reply.");
					MimeBodyPart part2 = new MimeBodyPart();
					part2.attachFile(f);
					MimeMultipart mimeMultipart = new MimeMultipart();
					mimeMultipart.addBodyPart(part1);
					mimeMultipart.addBodyPart(part2);
					message.setContent(mimeMultipart);
					Transport.send(message);
					mailcount++;
					long endTime = System.currentTimeMillis();
					long elapsedTime = endTime - startTime;
					System.out.println("Elapsed time: " + elapsedTime + " milliseconds");

				} catch (MessagingException mex) {
					mex.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 



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

				Gson json = new Gson();
				return json.toJson(mailcount);

			}
		}
		catch (Exception e) 
		{
			e.printStackTrace(); 
			logger.error(new Date() +"Inside SendMinutes.htm "+UserId,e);
		}

		return null;
	}

	@RequestMapping(value="SendNonProjectMinutes.htm", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody String SendNonProjectMinutes(HttpServletRequest req,HttpServletResponse res, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside SendNonProjectMinutes.htm "+UserId);
		int mailcount=0;
		try
		{
			String committeescheduleid = req.getParameter("committeescheduleid");	

			System.out.println("committeescheduleid"+committeescheduleid);
			Object[] scheduleeditdata=service.CommitteeScheduleEditData(committeescheduleid);
			String projectid= scheduleeditdata[9].toString();

			if(projectid!=null && Integer.parseInt(projectid)>0)

			{
				req.setAttribute("projectdetails", service.projectdetails(projectid));
			}
			String divisionid= scheduleeditdata[16].toString();
			if(divisionid!=null && Integer.parseInt(divisionid)>0)
			{
				req.setAttribute("divisiondetails", service.DivisionData(divisionid));
			}
			String initiationid= scheduleeditdata[17].toString();
			if(initiationid!=null && Integer.parseInt(initiationid)>0)
			{
				req.setAttribute("initiationdetails", service.Initiationdetails(initiationid));
			}

			List<Object[]> actionlist= service.MinutesViewAllActionList(committeescheduleid);
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

			List<Object[]> repList = service.getCommitteeMainRepList(scheduleeditdata[1]!=null?scheduleeditdata[1].toString():"0");
			List<String> repCodes = new ArrayList<>();
			
			if(repList != null && !repList.isEmpty()){
				repCodes = repList.stream().map(obj -> obj[1]!=null ? obj[1].toString() : null).filter(Objects::nonNull).collect(Collectors.toList());
			}

			req.setAttribute("repCodes", repCodes);
			req.setAttribute("committeeminutesspeclist",service.CommitteeScheduleMinutes(committeescheduleid) );
			req.setAttribute("committeescheduleeditdata", scheduleeditdata);
			req.setAttribute("CommitteeAgendaList", service.AgendaList(committeescheduleid));
			req.setAttribute("committeeminutes",service.CommitteeMinutesSpecdetails());
			req.setAttribute("committeeminutessub",service.CommitteeMinutesSub());
			req.setAttribute("committeeinvitedlist", service.CommitteeAtendance(committeescheduleid,"1"));

			req.setAttribute("actionlist",  actionsdata);
			req.setAttribute("labdetails", service.LabDetails(scheduleeditdata[24].toString()));
			req.setAttribute("isprint", "Y");	
			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(scheduleeditdata[24].toString()));
			req.setAttribute("labInfo", service.LabDetailes(LabCode));
			String committeeId=scheduleeditdata[0].toString();
			String scheduledate=scheduleeditdata[2].toString();

			List<Object[]>ActionDetails=service.actionDetailsForNonProject(committeeId,scheduledate);
			List<Object[]>actionSubDetails=new ArrayList();
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
			req.setAttribute("meetingcount",service.MeetingNo(scheduleeditdata));
			String filename=scheduleeditdata[11].toString().replace("/", "-");
			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);

			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/committee/CommitteeMinutesViewAll.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();
			HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"));
			req.setAttribute("tableactionlist",  actionsdata);
			CharArrayWriterResponse customResponse1 = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/committee/ActionDetailsTable.jsp").forward(req, customResponse1);
			String html1 = customResponse1.getOutput();        
			HtmlConverter.convertToPdf(html1,new FileOutputStream(path+File.separator+filename+"1.pdf")); 
			PdfWriter pdfw=new PdfWriter(path +File.separator+ "Mom.pdf");
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
			File f=new File(path +File.separator+ "Mom.pdf");
			ArrayList<String> emails= new ArrayList<String>();
			ArrayList<String> membertypes=new ArrayList<String>(Arrays.asList("CC","CS","PS","CI","I","P","CH"));
			List<Object[]>committeeinvitedlist=service.CommitteeAtendance(committeescheduleid,"1");
			String MainEmail="";
			for(Object[] obj : committeeinvitedlist) 
			{	 
				if(membertypes.contains(obj[3].toString()) && obj[4].toString().equalsIgnoreCase("P")) 
				{
					if(obj[8]!=null) {
						emails.add(obj[8].toString());
					}
				}

				if( obj[8]!=null && obj[3].toString().equalsIgnoreCase("CC")) {
					MainEmail=obj[8].toString();
				}
			}
			if(emails.contains(MainEmail)) {
				emails.remove(MainEmail);
			}

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
			System.out.println("TLSEmail Start");
			long startTime = System.currentTimeMillis();

			Properties properties = System.getProperties(); 	if(LabCode.equalsIgnoreCase("ADE")) {
				properties.setProperty("mail.smtp.host", hostAddress);
				properties.put("mail.smtp.ssl.enable", "true"); //TLS
				properties.put("mail.smtp.port", mailAuthentication.getPort()); 
				properties.put("mail.smtp.auth", "true"); 
				properties.put("mail.smtp.socketFactory.class","jakarta.net.ssl.SSLSocketFactory");
				properties.put("mail.smtp.ssl.checkserveridentity", false);
				properties.put("mail.smtp.ssl.trust", hostAddress);
			}else {
				properties.setProperty("mail.smtp.host", hostAddress);
				properties.put("mail.smtp.starttls.enable", "true"); //TLS
				properties.put("mail.smtp.port", mailAuthentication.getPort()); 
				properties.put("mail.smtp.auth", "true"); 
				properties.put("mail.smtp.socketFactory.class","jakarta.net.ssl.SSLSocketFactory"); 
			}
			
			Session session = Session.getDefaultInstance(properties,
					new jakarta.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(from,password);
				}
			});	
			try {
				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(from));
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(MainEmail));
				message.setSubject("Mom attachement for "+scheduleeditdata[11].toString());
				MimeBodyPart part1 = new MimeBodyPart();
				for (String ccRecipient : emails) {
					message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccRecipient));
				}
				part1.setText("This is a System generated mail please Don't reply.");
				MimeBodyPart part2 = new MimeBodyPart();
				part2.attachFile(f);
				MimeMultipart mimeMultipart = new MimeMultipart();
				mimeMultipart.addBodyPart(part1);
				mimeMultipart.addBodyPart(part2);
				message.setContent(mimeMultipart);
				Transport.send(message);
				mailcount++;
				long endTime = System.currentTimeMillis();
				long elapsedTime = endTime - startTime;
				System.out.println("Elapsed time: " + elapsedTime + " milliseconds");
				System.out.println("Message Sent");

			} catch (MessagingException mex) {
				mex.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			pathOfFile2= Paths.get(path +File.separator+ "MoM.pdf"); 
			Files.delete(pathOfFile2);


			Gson json= new Gson();
			return json.toJson(mailcount);


		}catch(Exception e) {	    		
			logger.error(new Date() +" Inside "+UserId, e);
			e.printStackTrace();
		}		
		return null;	
	}




	@RequestMapping(value = "MomReportList.htm")
	public String MomReportList(HttpServletRequest req, HttpSession ses) throws Exception
	{	
		String UserId = (String) ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		String Logintype= (String)ses.getAttribute("LoginType");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() +"Inside CommitteeAdd.htm "+UserId);
		try {
			String projectId=req.getParameter("projectid");
			String committeeId=req.getParameter("committeeid");

			List<Object[]> projectList=service.LoginProjectDetailsList(EmpId,Logintype,LabCode);

			if(committeeId==null)
			{
				committeeId="1";
			}


			if(projectId==null || projectId.equals("null"))
			{
				projectId=projectList.get(0)[0].toString();
			}

			List<Object[]> projectCommitteList=service.ProjectApplicableCommitteeList(projectId);

			List<Object[]> MomReportList=service.MomReportList(projectId,committeeId);
			req.setAttribute("MomReportList", MomReportList);
			req.setAttribute("projectId", projectId);
			req.setAttribute("committeeId", committeeId);
			req.setAttribute("projectList", projectList);
			req.setAttribute("projectCommitteList", projectCommitteList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "committee/MomReportList";
	}


	// Prudhvi 27/03/2024
	/* ------------------ start ----------------------- */
	@RequestMapping(value = "IndustryPartnerRepListInvitationsMainMembers.htm", method = RequestMethod.GET)
	public @ResponseBody String industryPartnerRepListInvitationsMainMembers(HttpServletRequest req,HttpSession ses) throws Exception
	{
		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside IndustryPartnerRepListInvitationsMainMembers.htm"+ UserId);

		List<Object[]> industryPartnerRepList = new ArrayList<Object[]>();

		industryPartnerRepList = service.IndustryPartnerRepListInvitationsMainMembers(req.getParameter("industryPartnerId"), req.getParameter("committeemainid"));

		Gson json = new Gson();
		return json.toJson(industryPartnerRepList);	
	}
	/* ------------------ end ----------------------- */
	@RequestMapping(value = "findCommitteeMainId.htm", method = RequestMethod.GET)
	public @ResponseBody String findCommitteeMainId(HttpServletRequest req,HttpSession ses) throws Exception
	{
		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside findCommitteeMainId.htm"+ UserId);


		List<Object[]>committeLetters=new ArrayList<>();
		try {
			String letterId=req.getParameter("letterId");

			if(letterId!=null) {
				long count=service.UpdateCommitteLetter(letterId);
				if(count==1) {
					Gson json = new Gson();
					return json.toJson(true);
				}

			}else {
				committeLetters = service.getcommitteLetters(req.getParameter("committeeId"),req.getParameter("projectId"),req.getParameter("divisionId"), req.getParameter("initiationId"));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}


		Gson json = new Gson();
		return json.toJson(committeLetters);	
	}

	@RequestMapping(value="CommitteeLettersUpload.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String CommitteeLettersUpload(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir,HttpServletResponse res,@RequestParam(name = "pdf-file")MultipartFile letter)	throws Exception 
	{
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String UserId = (String) ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeLettersUpload.htm "+UserId);

		try {
			String projectid = req.getParameter("projectid");
			String initiationid = req.getParameter("initiationid");
			String divisionid = req.getParameter("divisionid");
			String committeeid = req.getParameter("committeeid");

		
			// 🔹 Validate file types
	        if (!isValidFileType(letter) ) {
	        	
	        	redir.addFlashAttribute("projectid",projectid);
				redir.addFlashAttribute("divisionid",divisionid);
				redir.addFlashAttribute("initiationid",initiationid);	
	        	

	            return redirectWithError(redir, "ProjectMaster.htm",
	                    "Invalid file type. Only PDF or Image files are allowed.");
	        }
			
			
			
			CommitteeLetter committeeLetter=CommitteeLetter.builder()
					.ProjectId(Long.parseLong(projectid))
					.InitiationId(Long.parseLong(initiationid))
					.DivisionId(Long.parseLong(divisionid))
					.CommitteId(Long.parseLong(committeeid))
					.LabCode(LabCode)
					.letter(letter)
					.CreatedBy(UserId)
					.build();

			long count = service.saveCommitteeLetter(committeeLetter);
			if (count > 0) {
				redir.addAttribute("result", "Committee Invitation Letter added Successfully");
			} else {
				redir.addAttribute("resultfail", "Committee Invitation Letter Add Unsuccessful");
			}

			redir.addFlashAttribute("projectid",projectid);
			redir.addFlashAttribute("divisionid",divisionid);
			redir.addFlashAttribute("initiationid",initiationid);	

		}catch (Exception e) {
			e.printStackTrace();
		} 

		return "redirect:/ProjectMaster.htm";
	}

	@RequestMapping(value = "UploadedCommitteLetterDownload.htm")
	public void UploadedCommitteLetterDownload(HttpServletRequest req, HttpSession ses,
			RedirectAttributes redir, HttpServletResponse res) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside UploadedCommitteLetterDownload.htm " + UserId);
		try {
			String letterId= req.getParameter("letterid");
			Object[]committeeLetter=service.getcommitteeLetter(letterId);
			String freezedata =committeeLetter[1].toString().replaceAll("[/\\\\]", ",");
			String[] fileParts = freezedata.split(",");
			Path filepath = Paths.get(uploadpath, fileParts[0], fileParts[1],committeeLetter[2].toString());
			File my_file=filepath.toFile();
			//my_file=new File(uploadpath+committeeLetter[1]+File.separator+committeeLetter[2]);
			res.setContentType("Application/pdf");	
			res.setHeader("Content-disposition","inline; filename="+committeeLetter[2].toString()); 
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
			logger.error(new Date() + "Inside UploadedCommitteLetterDownload.htm " + UserId, e);
		}

	}

	@RequestMapping(value = "getAgendaAttachId.htm", method = RequestMethod.GET)
	public @ResponseBody String getAgendaAttachId(HttpServletRequest req,HttpSession ses) throws Exception {

		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside getAgendaAttachId.htm "+ UserId);		
		String agendaid=req.getParameter("agendaid");		
		List<Object[]> attach = service.getAgendaAttachId(agendaid);
		Gson json = new Gson();
		if(attach!=null && attach.size()>0) {
			return json.toJson(attach);
		}
		return json.toJson("");
	}


	@RequestMapping(value = "addAgendaLinkFile.htm", method = RequestMethod.GET)
	public @ResponseBody String addAgendaLinkFile(HttpServletRequest req,HttpSession ses) throws Exception {

		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside addAgendaLinkFile.htm "+ UserId);	

		String agendaId=req.getParameter("agendaId");		
		String attachId=req.getParameter("attachId");

		CommitteeScheduleAgendaDocs docs = new CommitteeScheduleAgendaDocs();
		docs.setAgendaId(Long.parseLong(agendaId));
		docs.setFileDocId(Long.parseLong(attachId));
		docs.setCreatedBy(UserId);
		docs.setCreatedDate(sdf1.format(new Date()));
		docs.setIsActive(1);
		long attach = service.addAgendaLinkFile(docs);

		Gson json = new Gson();
		return json.toJson(attach);
	}
	
	@RequestMapping(value="CARSSchedule.htm", method = {RequestMethod.GET, RequestMethod.POST})
	public String carsScheduleMeeting(HttpServletRequest req,HttpSession ses) throws Exception {
		String UserId = (String)ses.getAttribute("Username");
		String LoginType = (String)ses.getAttribute("LoginType");
		String EmpId = ((Long)ses.getAttribute("EmpId")).toString();
		logger.info(new Date() +" Inside CARSSchedule.htm "+ UserId);	
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			
			List<Object[]> carsInitiationList = carsservice.carsInitiationList(LoginType, EmpId);
			if(carsInitiationId==null) {
				carsInitiationId = carsInitiationList!=null && carsInitiationList.size()>0?carsInitiationList.get(0)[0].toString():"0";
			}
			req.setAttribute("carsInitiationList", carsInitiationList);
			req.setAttribute("carsInitiationId", carsInitiationId);
			req.setAttribute("committeeschedulelist", service.carsScheduleList(carsInitiationId));
			req.setAttribute("committeeId", String.valueOf(ccmservice.getCommitteeIdByCommitteeCode("CARS")));;
			return "committee/CARSSchedule";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CARSSchedule.htm "+ UserId, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value = "checkMeetingOnParitcularDay.htm", method = RequestMethod.GET)
	public @ResponseBody String checkMeetingOnParitcularDay(HttpSession ses, HttpServletRequest req) throws Exception 
	{
		List<MeetingCheckDto>list = new ArrayList<>();
		try {
			String committeemainid = req.getParameter("committeemainid");
		
			String date = req.getParameter("date");
			LocalDate todayDate = LocalDate.now();
			LocalDate dateparameter = LocalDate.parse(date);

			if(!dateparameter.isBefore(todayDate)){	
			list = service.getMeetingCheckDto(date,committeemainid );
			}
		}catch (Exception e) {
		  e.printStackTrace();
		}
		Gson json = new Gson();
		return json.toJson(list);
	}
	@RequestMapping(value = "checkMeetingEmpWise.htm", method = RequestMethod.GET)
	public @ResponseBody String checkMeetingEmpWise(HttpSession ses, HttpServletRequest req) throws Exception 
	{
		List<MeetingCheckDto>list = new ArrayList<>();
		try {
			String empid = req.getParameter("empid");
			String labocode = req.getParameter("labocode");
			String scheduleid = req.getParameter("scheduleid");
			
		
			
			list = service.getMeetingCheckDto(empid,labocode,scheduleid);
		}catch (Exception e) {
			e.printStackTrace();
		}
		Gson json = new Gson();
		return json.toJson(list);
	}	
	
	/* ********************************************* Programme AD ************************************************ */
	@RequestMapping(value="PrgmCommitteeConstitution.htm", method = {RequestMethod.POST, RequestMethod.GET})
	public String prgmCommitteeConstitution(HttpServletRequest req,HttpSession ses, RedirectAttributes redir) throws Exception {
		String UserId = (String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +" Inside PrgmCommitteeConstitution.htm "+UserId);
		try {
			String committeeMainId = req.getParameter("committeeMainId");
			String committeeId = req.getParameter("committeeId");
			String programmeId = req.getParameter("programmeId");
			
			List<ProgrammeMaster> programmeList = service.getProgrammeMasterList();
			if(programmeId==null) {
				programmeId = programmeList!=null && programmeList.size()>0?programmeList.get(0).getProgrammeId()+"":"0";
			}
			
			if(committeeId==null) {
				committeeId = String.valueOf(ccmservice.getCommitteeIdByCommitteeCode("PMG"));
				if(committeeId.equals("0")) {
					redir.addAttribute("resultfail","The PMG Committee has not yet been created");
					return "redirect:/CommitteeList.htm";
				}
			}

			if(committeeMainId==null) {
				committeeMainId = String.valueOf(service.getCommitteeMainIdByProgrammeId(programmeId));
			}
			
			req.setAttribute("allLabList", service.AllLabList());
			req.setAttribute("programmeList", programmeList);
			req.setAttribute("employeeList", service.EmployeeListWithoutMembers(committeeMainId,labcode));
			req.setAttribute("employeeList1", service.EmployeeListNoMembers(labcode,committeeMainId));
			req.setAttribute("expertList", service.ExternalMembersNotAddedCommittee(committeeMainId));
			req.setAttribute("committeeMembersAll", service.CommitteeAllMembersList(committeeMainId));
			req.setAttribute("committeedata", service.CommitteMainData(committeeMainId));
			req.setAttribute("industryPartnerList", masterservice.getIndustryPartnerList());
			req.setAttribute("committeerepnotaddedlist", service.CommitteeRepNotAddedList(committeeMainId));
			req.setAttribute("committeeMemberreplist", service.CommitteeMemberRepList(committeeMainId));
			req.setAttribute("committeeMainId", committeeMainId);
			req.setAttribute("committeeId", committeeId);
			req.setAttribute("programmeId", programmeId);
			req.setAttribute("designationlist", adminservice.DesignationList());
			
			return "committee/PrgmCommitteeConstitution";
		}catch (Exception e) {
			logger.error(new Date() +" Inside PrgmCommitteeConstitution.htm "+UserId, e);
			e.printStackTrace();
			return "static/Error";
		}
	}
	
	@RequestMapping(value="PrgmCommitteeSubmit.htm", method= {RequestMethod.GET, RequestMethod.POST})
	public String prgmCommitteeSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
		String UserId = (String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +" Inside PrgmCommitteeSubmit.htm "+UserId);
		try {
			String action = req.getParameter("action");
			String committeeId = req.getParameter("committeeId");
			
			CommitteeMembersEditDto dto=new CommitteeMembersEditDto();
			
			dto.setChairperson(req.getParameter("chairperson"));
			dto.setSecretary(req.getParameter("Secretary"));
			dto.setProxysecretary(req.getParameter("proxysecretary"));
			dto.setSesLabCode(labcode);
			dto.setCpLabCode(req.getParameter("CpLabCode"));
			dto.setCommitteemainid(req.getParameter("committeemainid"));
			dto.setChairpersonmemid(req.getParameter("cpmemberid"));
			dto.setSecretarymemid(req.getParameter("msmemberid"));
			dto.setProxysecretarymemid(req.getParameter("psmemberid"));			
			dto.setModifiedBy(UserId);		
			dto.setCo_chairperson(req.getParameter("co_chairperson"));
			dto.setComemberid(req.getParameter("comemberid"));
			dto.setMsLabCode(req.getParameter("msLabCode"));
			dto.setCommitteeId(committeeId);
			dto.setProgrammeId(req.getParameter("programmeId"));
			//dto.setReferenceNo(req.getParameter("ReferenceNo"));
			//dto.setFormationDate(req.getParameter("Formationdates"));
			long result = ccmservice.ccmCommitteeMainMembersSubmit(dto, action);
			
			if(result!=0) {
				redir.addAttribute("result", "Committee Main Members "+action+"ed Successfully");
			}else {
				redir.addAttribute("resultfail", "Committee Main Members "+action+" UnSuccessful");
			}
			
			redir.addAttribute("committeeMainId", result);
			redir.addAttribute("committeeId", committeeId);
			redir.addAttribute("programmeId", req.getParameter("programmeId"));
			return "redirect:/PrgmCommitteeConstitution.htm";
			
		}catch (Exception e) {
			logger.error(new Date() +" Inside PrgmCommitteeSubmit.htm "+UserId, e);
			e.printStackTrace();
			return "static/Error";
		}
	}
	
	@RequestMapping(value="PrgmSchedule.htm", method= {RequestMethod.GET, RequestMethod.POST})
	public String prgmSchedule(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside PrgmSchedule.htm "+UserId);
		try {

			String committeeId = req.getParameter("committeeId");
			String committeeMainId = req.getParameter("committeeMainId");
			String programmeId = req.getParameter("programmeId");
			
			List<ProgrammeMaster> programmeList = service.getProgrammeMasterList();
			if(programmeId==null) {
				programmeId = programmeList!=null && programmeList.size()>0?programmeList.get(0).getProgrammeId()+"":"0";
			}
			
			if(committeeId==null) {
				committeeId = String.valueOf(ccmservice.getCommitteeIdByCommitteeCode("PMG"));
				if(committeeId.equals("0")) {
					redir.addAttribute("resultfail","The PMG Committee has not yet been created");
					return "redirect:/CommitteeList.htm";
				}
			}
			
			if(committeeMainId==null) {
				committeeMainId = String.valueOf(service.getCommitteeMainIdByProgrammeId(programmeId));
				if(committeeMainId.equals("0")) {
					redir.addAttribute("programmeId", programmeId);
					redir.addAttribute("committeeId", committeeId);
					redir.addAttribute("resultfail","The PMG Committee has not yet been constituted");
					return "redirect:/PrgmCommitteeConstitution.htm";
				}
			}
			
			req.setAttribute("programmeList", programmeList);
			req.setAttribute("programmeId", programmeId);
			req.setAttribute("committeeschedulelist", service.prgmScheduleList(programmeId));
			req.setAttribute("committeeId", committeeId);
			req.setAttribute("committeeMainId", committeeMainId);
		
			return "committee/PrgmSchedule";
		}catch (Exception e) {
			logger.error(new Date() +" Inside PrgmSchedule.htm "+UserId, e);
			e.printStackTrace();
			return "static/Error";
		}
	}
	
	@RequestMapping(value="PrgmScheduleAgenda.htm", method= {RequestMethod.GET, RequestMethod.POST})
	public String prgmScheduleAgenda(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside PrgmScheduleAgenda.htm "+UserId);
		try {
			String scheduleid = req.getParameter("scheduleid");
			
			Object[] scheduledata = service.CommitteeScheduleEditData(scheduleid);
			req.setAttribute("scheduledata", scheduledata);
			req.setAttribute("prgmProjectList", service.prgmProjectList(scheduledata!=null?scheduledata[26].toString():"0"));
			req.setAttribute("committeeAgendaList", service.PrgmAgendaList(scheduleid));
			req.setAttribute("agendaDocList",service.AgendaLinkedDocList(scheduleid));
			req.setAttribute("filesize",file_size);
			req.setAttribute("labEmpList",service.PreseneterForCommitteSchedule("A"));
			req.setAttribute("allLabList", service.AllLabList());

			return "committee/PrgmScheduleAgenda";
		}catch (Exception e) {
			logger.error(new Date() +" Inside PrgmScheduleAgenda.htm "+UserId, e);
			e.printStackTrace();
			return "static/Error";
		}
	}
	/* ********************************************* Programme AD End ************************************************ */
	
	@RequestMapping(value = "sendDraftMom.htm", method = RequestMethod.GET)
	public @ResponseBody String sendDraftMom(HttpSession ses, HttpServletRequest req) throws Exception 
	{
		String UserId = (String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		String LoginType = (String)ses.getAttribute("LoginType");
		String EmpId = ((Long)ses.getAttribute("EmpId")).toString();
		long count = 0;
		Gson json = new Gson();
		try {
	
			String committeescheduleid = req.getParameter("committeescheduleid");
			
			List<CommitteScheduleMinutesDraft> draftList = service.getCommitteScheduleMinutesDraftList();
			draftList = draftList.stream().filter(e->e.getScheduleId().toString().equalsIgnoreCase(committeescheduleid))
							.collect(Collectors.toList());					
			if(draftList.size()>0 ) {
				return json.toJson(-1);
			}
			
			
			List<Object[]> committeeinvitedlist = service.CommitteeAtendance(committeescheduleid,"1");
			
			
			
			committeeinvitedlist = committeeinvitedlist.stream().filter(e->e[11].toString().equalsIgnoreCase(labcode) && !e[3].toString().equalsIgnoreCase("CS")  )
									.collect(Collectors.toList()) ;
			
			
			
			
			
			for(Object[]obj:committeeinvitedlist) {
				CommitteScheduleMinutesDraft cmd = new CommitteScheduleMinutesDraft();
				
				cmd.setScheduleId(Long.parseLong(committeescheduleid) );	
				cmd.setSentDate(LocalDate.now());
				cmd.setSentBy(UserId);	
				cmd.setEmpid(Long.parseLong(obj[0].toString()));
				
				count = service.sentMomDraft(cmd);
			}
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}

		return json.toJson(count);
	}
	@RequestMapping(value = "CommitteeMomDraft.htm", method = RequestMethod.GET)
	public  String CommitteeMomDraft(HttpSession ses, HttpServletRequest req
			,RedirectAttributes redir
	) throws Exception 
	{
		String UserId = (String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		String LoginType = (String)ses.getAttribute("LoginType");
		String EmpId = ((Long)ses.getAttribute("EmpId")).toString();
	
		try {
			
			String pageSize = req.getParameter("pageSize");			
			
			pageSize = pageSize!=null?pageSize:"5";
			
			try {
			    long page = Long.parseLong(pageSize);
			} catch (NumberFormatException e) {
			    req.setAttribute("errorInPageSize", "Invalid page size: Please enter a valid number.");
			    pageSize = "0";
			}
			List<Object[]>draftMomList = service.getdraftMomList(EmpId,pageSize);
			req.setAttribute("draftMomList", draftMomList);
			req.setAttribute("pageSize", pageSize);
		}catch (Exception e) {
			e.printStackTrace();
			 	return "static/Error";
		}
		
		return "committee/committeeDraftList";
	}
	
	@RequestMapping(value = "submitMomRemarks.htm", method = RequestMethod.GET)
	public @ResponseBody String submitMomRemarks(HttpSession ses, HttpServletRequest req) throws Exception 
	{
		String UserId = (String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		String LoginType = (String)ses.getAttribute("LoginType");
		String EmpId = ((Long)ses.getAttribute("EmpId")).toString();
		long count = 0;
		Gson json = new Gson();
		try {
			
			String remarks = req.getParameter("remarks");			
			Long scheduleId = Long.parseLong(req.getParameter("scheduleId") );			
			
			CommitteeSchedulesMomDraftRemarks cmd = new CommitteeSchedulesMomDraftRemarks();
			
			System.out.println(LocalDateTime.now().toString());
			
			cmd.setEmpid(Long.parseLong(EmpId));
			cmd.setScheduleId(scheduleId);
			cmd.setRemarks(remarks);
			cmd.setCreatedDate(sdtf.format(new Date()));
			cmd.setIsactive(1);		
			
			 count = service.saveCommitteeSchedulesMomDraftRemarks(cmd);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return json.toJson(count);
	}
	@RequestMapping(value = "getMomDraftRemarks.htm", method = RequestMethod.GET)
	public @ResponseBody String getMomDraftRemarks(HttpSession ses, HttpServletRequest req) throws Exception 
	{
		String UserId = (String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		String LoginType = (String)ses.getAttribute("LoginType");
		String EmpId = ((Long)ses.getAttribute("EmpId")).toString();
		List<Object[]>list = new ArrayList<Object[]>();
		Gson json = new Gson();
		try {
			
					
			Long scheduleId = Long.parseLong(req.getParameter("scheduleId") );			
			
		
			
	
			list = service.getCommitteeSchedulesMomDraftRemarks(scheduleId);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return json.toJson(list);
	}

	
	
	private String redirectWithError(RedirectAttributes redir,String redirURL, String message) {
	    redir.addAttribute("resultfail", message);
	    return "redirect:/"+redirURL;
	}
	private boolean containsHTMLTags(String[] inputs) {
		    
		    for (String input : inputs) {
		        if (InputValidator.isContainsHTMLTags(input)) {
		            return true;
		        }
		    }
		    return false;
	}
	
	
	@RequestMapping(value = "PrePRojectAgendaDocLinkDownload.htm", method = RequestMethod.GET)
	public void PrePRojectAgendaDocLinkDownload(Model model,HttpServletRequest req, HttpSession ses,HttpServletResponse res)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");

		logger.info(new Date() +"Inside PrePRojectAgendaDocLinkDownload.htm "+UserId);

		try {	
			String filerepid=req.getParameter("filerepid");
			FileRepUploadPreProject obj=service.getPreProjectAgendaDocById(filerepid);
			Path uploadPath = Paths.get(ApplicationFilesDrive, obj.getFilePath(),(obj.getFileNameUi()+obj.getVersionDoc()+"-"+obj.getReleaseDoc()+".zip"));
			String path=req.getServletContext().getRealPath("/view/temp");
			Zipper zip=new Zipper();
			zip.unpack(uploadPath.toString(),path,obj.getFilePass());
			res.setContentType("application/pdf");
			res.setHeader("Content-disposition","attachment;filename="+obj.getFileName()); 
			File f=new File(path+"/"+obj.getFileName());
			FileInputStream fis = new FileInputStream(f);
			DataOutputStream os = new DataOutputStream(res.getOutputStream());
			res.setHeader("Content-Length",String.valueOf(f.length()));
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fis.read(buffer)) >= 0) {
				os.write(buffer, 0, len);
			} 
			fis.close();
			os.close();

			Path pathOfFile2= Paths.get(path+"/"+obj.getFileName()); 
			Files.delete(pathOfFile2);

		}
		catch (Exception e) {

			e.printStackTrace();  
			logger.error(new Date() +" Inside PrePRojectAgendaDocLinkDownload.htm "+UserId, e); 

		}

	}
	
//	------------------------------------ Naveen R 3/9/25 MOM TabularMinutesDownload.htm -------------------------------------------
	@RequestMapping(value = "MOMTabularMinutesDownload.htm")
	public void getMomCheck(HttpServletRequest req, HttpSession ses, RedirectAttributes redir,HttpServletResponse res) throws Exception
	{

		String UserId=(String)ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside MOMTabularMinutesDownload.htm "+UserId);
		try
		{
			String committeescheduleid = req.getParameter("committeescheduleid");
			Object[] committeescheduleeditdata=service.CommitteeScheduleEditDataforMom(committeescheduleid);
			String projectid= committeescheduleeditdata[9].toString();
			if(projectid!=null && Integer.parseInt(projectid)>0)
			{
				req.setAttribute("projectdetails", service.projectdetails(projectid));
			}
			String divisionid= committeescheduleeditdata[16].toString();
			if(divisionid!=null && Integer.parseInt(divisionid)>0)
			{
				req.setAttribute("divisiondetails", service.DivisionData(divisionid));
			}
			String initiationid= committeescheduleeditdata[17].toString();
			if(initiationid!=null && Integer.parseInt(initiationid)>0)
			{
				req.setAttribute("initiationdetails", service.Initiationdetails(initiationid));
			}

			List<Object[]> actionlist= service.MinutesViewAllActionList(committeescheduleid);
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

			req.setAttribute("actionsdata",actionsdata );

			req.setAttribute("committeeminutesspeclist",service.CommitteeScheduleMinutesforAction(committeescheduleid) );
			req.setAttribute("committeescheduleeditdata", committeescheduleeditdata);
			req.setAttribute("committeeminutes",service.CommitteeMinutesSpecdetails());
			List<Object[]> invitedlists= service.CommitteeAtendance(committeescheduleid,"1");
			
			Map<Object,List<Object[]>> invitedlist = invitedlists.stream().filter(row->row[4].toString().equalsIgnoreCase("P")).collect(Collectors.groupingBy(row -> row[3]));
			List<Map.Entry<Object, List<Object[]>>> entries = new ArrayList<>(invitedlist.entrySet());
			
			req.setAttribute("committeeinvitedlist", invitedlists);
			req.setAttribute("projectid", projectid);				
			req.setAttribute("actionlist", service.MinutesViewAllActionList(committeescheduleid));
			req.setAttribute("labdetails", service.LabDetails(committeescheduleeditdata[24].toString()));
			req.setAttribute("isprint", "Y");
			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(committeescheduleeditdata[24].toString()));
			req.setAttribute("meetingcount",service.MeetingNo(committeescheduleeditdata));
			req.setAttribute("labInfo", service.LabDetailes(LabCode));
			req.setAttribute("ActionPlanSixMonths", service.ActionPlanSixMonths(projectid));
			String filename=committeescheduleeditdata[11].toString().replace("/", "-");

			req.setAttribute("flagforView", "M");			
			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);

			req.setAttribute("ccmFlag", req.getParameter("ccmFlag"));

			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/committee/MoMTabularMinutes.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"));
			req.setAttribute("tableactionlist",  actionsdata);

			PdfReader pdf1=new PdfReader(path+File.separator+filename+".pdf");

			PdfDocument pdfDocument = new PdfDocument(pdf1);

			pdfDocument.close();
			pdf1.close();	       

			res.setContentType("application/pdf");
			res.setHeader("Content-disposition","inline;filename="+filename+".pdf");

			File f = new File(path + File.separator + filename + ".pdf");
			try (OutputStream out = res.getOutputStream();
			     FileInputStream in = new FileInputStream(f)) {

			    byte[] buffer = new byte[4096];
			    int length;
			    while ((length = in.read(buffer)) > 0) {
			        out.write(buffer, 0, length);
			    }
			    out.flush(); 
			}

			Files.deleteIfExists(Paths.get(path + File.separator + filename + ".pdf"));

		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside MOMTabularMinutesDownload.htm "+UserId,e);
		}
	}
	
	public static boolean hasHtmlTags(HttpServletRequest req) {
        Enumeration<String> parameterNames = req.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = req.getParameter(paramName);
            if (paramValue != null && paramValue.trim().matches(".*<[^>]+>.*")) {
                return true;
            }
        }
        return false;
	}
	
//	------------------------------------ Naveen R 10/10/25 Representative Add -------------------------------------------	
	
	@RequestMapping(value = "representativeAdd.htm" , method = {RequestMethod.GET})
	public @ResponseBody String representativeAdd(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside representativeAdd.htm " + UserId);
		Gson json = new Gson();
		CommitteeRepresentative rep1 = new CommitteeRepresentative();
		try {
			
			 if (hasHtmlTags(req)) {
		            return json.toJson(null); 
		        }
			
			String repCode = req.getParameter("repCode");
			String repName = req.getParameter("repName");
			
			CommitteeRepresentative rep = new CommitteeRepresentative();
			rep.setRepCode(repCode);
			rep.setRepName(repName);
			rep.setCreatedBy(UserId);
			rep.setCreatedDate(LocalDate.now().toString());
			
			long count = service.addRepresentative(rep);
			rep1 = service.getRepresentativeById(count+"");
			return json.toJson(rep1);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside representativeAdd.htm " + UserId, e);
			return json.toJson(rep1);
		}
	}
	
	@RequestMapping(value = "RepNameDuplicateCheck.htm", method = RequestMethod.GET)
	public @ResponseBody String repNameDuplicateCheck(HttpSession ses,HttpServletRequest req) {
		String UserId = (String)ses.getAttribute("Username");
		String repName = req.getParameter("repName");
		Long count = null;
		logger.info(new Date() + " Inside RepNameDuplicateCheck.htm "+ UserId );
		try {
			count = service.getRepNameDuplicateCount(repName);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside RepNameDuplicateCheck.htm " + UserId);
		}
		Gson json = new Gson();
		return json.toJson(count);
	}
	
	@RequestMapping(value = "RepCodeDuplicateCheck.htm", method = RequestMethod.GET)
	public @ResponseBody String repCodeDuplicateCheck(HttpSession ses,HttpServletRequest req) {
		String UserId = (String)ses.getAttribute("Username");
		String repCode =req.getParameter("repCode");
		Long count = null;
		logger.info(new Date() + " Inside RepCodeDuplicateCheck.htm "+ UserId );
		try {
			count = service.getRepCodeDuplicateCount(repCode);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside RepCodeDuplicateCheck.htm " + UserId);
		}
		Gson json = new Gson();
		return json.toJson(count);
	}
	
	@RequestMapping(value = "MinutesOfMeetingTabularMinutesDownload.htm")
	public void minutesOfMeetingTabularMinutesDownload(HttpServletRequest req, HttpSession ses, RedirectAttributes redir,HttpServletResponse res,Model model) throws Exception
	{

		String UserId=(String)ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside MinutesOfMeetingTabularMinutesDownload.htm "+UserId);
		try
		{
			String UserView=null;
			Map md=model.asMap();
			if(req.getParameter("userview")!=null) {
				UserView=(String)req.getParameter("userview");
			}
			
			String committeescheduleid = req.getParameter("committeescheduleid");
			Object[] committeescheduleeditdata=service.CommitteeScheduleEditData(committeescheduleid);
			String projectid= committeescheduleeditdata[9].toString();
			if(projectid!=null && Integer.parseInt(projectid)>0)
			{
				req.setAttribute("projectdetails", service.projectdetails(projectid));
			}
			String divisionid= committeescheduleeditdata[16].toString();
			if(divisionid!=null && Integer.parseInt(divisionid)>0)
			{
				req.setAttribute("divisiondetails", service.DivisionData(divisionid));
			}
			String initiationid= committeescheduleeditdata[17].toString();
			if(initiationid!=null && Integer.parseInt(initiationid)>0)
			{
				req.setAttribute("initiationdetails", service.Initiationdetails(initiationid));
			}

			List<Object[]> actionlist= service.MinutesViewAllActionList(committeescheduleid);
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

			List<Object[]> repList = service.getCommitteeMainRepList(committeescheduleeditdata[1]!=null?committeescheduleeditdata[1].toString():"0");
			List<String> repCodes = new ArrayList<>();
			
			if(repList != null && !repList.isEmpty()){
				repCodes = repList.stream().map(obj -> obj[1]!=null ? obj[1].toString() : null).filter(Objects::nonNull).collect(Collectors.toList());
			}

			req.setAttribute("repCodes", repCodes);
			req.setAttribute("actionsdata",actionsdata );
			req.setAttribute("userview",UserView);
			req.setAttribute("committeeminutesspeclist",service.committeeScheduleMinutesforActionForMomADE(committeescheduleid));
			req.setAttribute("committeescheduleeditdata", committeescheduleeditdata);
			req.setAttribute("committeeminutes",service.CommitteeMinutesSpecdetails());
			req.setAttribute("committeeinvitedlist", service.CommitteeAtendance(committeescheduleid,"1"));
			req.setAttribute("projectid", projectid);				
			req.setAttribute("actionlist", service.MinutesViewAllActionList(committeescheduleid));
			req.setAttribute("labdetails", service.LabDetails(committeescheduleeditdata[24].toString()));
			req.setAttribute("isprint", "Y");
			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(committeescheduleeditdata[24].toString()));
			req.setAttribute("meetingcount",service.MeetingNo(committeescheduleeditdata));
			req.setAttribute("labInfo", service.LabDetailes(LabCode));
			req.setAttribute("ActionPlanSixMonths", service.ActionPlanSixMonths(projectid));
			String filename=committeescheduleeditdata[11].toString().replace("/", "-");

			req.setAttribute("flagforView", "M");			
			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);

			req.setAttribute("ccmFlag", req.getParameter("ccmFlag"));
 
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/committee/MinutesOfMeetingTabularMinutes.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"));

			req.setAttribute("tableactionlist",  actionsdata);
			CharArrayWriterResponse customResponse1 = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/committee/MinutesOfMeetingActionPointsTable.jsp").forward(req, customResponse1);
			String html1 = customResponse1.getOutput();        

			HtmlConverter.convertToPdf(html1,new FileOutputStream(path+File.separator+filename+"1.pdf")); 

			req.setAttribute("committeeinvitedlist", service.CommitteeAtendance(committeescheduleid,"1"));
			CharArrayWriterResponse customResponse2 = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/committee/MinutesOfMeetingAttendence.jsp").forward(req, customResponse2);
			String html2 = customResponse2.getOutput();
			
			HtmlConverter.convertToPdf(html2, new FileOutputStream(path + File.separator + filename + "2.pdf"));

			
			PdfWriter pdfw=new PdfWriter(path +File.separator+ "merged.pdf");
			PdfReader pdf1=new PdfReader(path+File.separator+filename+".pdf");
			PdfReader pdf2=new PdfReader(path+File.separator+filename+"1.pdf");
			PdfReader pdf3=new PdfReader(path+File.separator+filename+"2.pdf");

			PdfDocument pdfDocument = new PdfDocument(pdf1, pdfw);	       	        
			PdfDocument pdfDocument2 = new PdfDocument(pdf2);
			PdfDocument pdfDocument3 = new PdfDocument(pdf3);
			PdfMerger merger = new PdfMerger(pdfDocument);

			merger.merge(pdfDocument2, 1, pdfDocument2.getNumberOfPages());
			merger.merge(pdfDocument3, 1, pdfDocument3.getNumberOfPages());
			
			pdfDocument3.close();
			pdfDocument2.close();
			pdfDocument.close();
			merger.close();
			pdf3.close();
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
			pathOfFile2 = Paths.get(path + File.separator + filename + "2.pdf"); 
			Files.delete(pathOfFile2);
			pathOfFile2= Paths.get( path+File.separator+filename+".pdf"); 
			Files.delete(pathOfFile2);	
			pathOfFile2= Paths.get(path +File.separator+ "merged.pdf"); 
			Files.delete(pathOfFile2);	


		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside MinutesOfMeetingTabularMinutesDownload.htm "+UserId,e);
		}
	}
	
	@RequestMapping(value = "aircraftAdd.htm" , method = {RequestMethod.GET})
	public @ResponseBody String aircraftAdd(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside aircraftAdd.htm " + UserId);
		Gson json = new Gson();
		CommitteeAircraft aircraft1 = new CommitteeAircraft();
		try {
			
			 if (hasHtmlTags(req)) {
		            return json.toJson(null); 
		        }
			
			String aircraftname = req.getParameter("aircraft");
			
			CommitteeAircraft aircraft = new CommitteeAircraft();
			aircraft.setAircraft(aircraftname);
			aircraft.setCreatedBy(UserId);
			aircraft.setCreatedDate(LocalDate.now().toString());
			
			long count = service.addAircraft(aircraft);
			aircraft1 = service.getAircraftById(count+"");
			return json.toJson(aircraft1);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside aircraftAdd.htm " + UserId, e);
			return json.toJson(aircraft1);
		}
	}
	
	@RequestMapping(value = "subsystemAdd.htm" , method = {RequestMethod.GET})
	public @ResponseBody String subsystemAdd(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside subsystemAdd.htm " + UserId);
		Gson json = new Gson();
		CommitteeSubSystem sub1 = new CommitteeSubSystem();
		try {
			
			 if (hasHtmlTags(req)) {
		            return json.toJson(null); 
		        }
			
			String subsystem = req.getParameter("subsystem");
			
			CommitteeSubSystem sub = new CommitteeSubSystem();
			sub.setSubSystem(subsystem);
			sub.setCreatedBy(UserId);
			sub.setCreatedDate(LocalDate.now().toString());
			
			long count = service.addSubSystem(sub);
			sub1 = service.getSubSystemById(count+"");
			return json.toJson(sub1);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside subsystemAdd.htm " + UserId, e);
			return json.toJson(sub1);
		}
	}
	
// **************************************** Naveen R 1/12/2025 *******************************************************
	
	@RequestMapping(value = "SendProgrammeMinutesOfMeeting.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody String sendProgrammeMinutesOfMeeting(HttpServletRequest req,HttpServletResponse res, HttpSession ses, RedirectAttributes redir) throws Exception {
		String UserId=(String)ses.getAttribute("Username");
		String EmpNo=(String)ses.getAttribute("EmpNo");
		String EmpName=(String)ses.getAttribute("EmpName");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside SendProgrammeMinutesOfMeeting.htm "+UserId);
		int mailcount=0;
		try {
			String committeescheduleid = req.getParameter("committeescheduleid");	

			Object[] committeescheduleeditdata = service.CommitteeScheduleEditDataforMom(committeescheduleid);
			String projectid= committeescheduleeditdata[9].toString();
			if(projectid!=null && Integer.parseInt(projectid)>0)
			{
				req.setAttribute("projectdetails", service.projectdetails(projectid));
			}
			String divisionid= committeescheduleeditdata[16].toString();
			if(divisionid!=null && Integer.parseInt(divisionid)>0)
			{
				req.setAttribute("divisiondetails", service.DivisionData(divisionid));
			}
			String initiationid= committeescheduleeditdata[17].toString();
			if(initiationid!=null && Integer.parseInt(initiationid)>0)
			{
				req.setAttribute("initiationdetails", service.Initiationdetails(initiationid));
			}

			List<Object[]> actionlist= service.MinutesViewAllActionList(committeescheduleid);
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

			req.setAttribute("actionsdata",actionsdata );

			req.setAttribute("committeeminutesspeclist",service.CommitteeScheduleMinutesforAction(committeescheduleid) );
			req.setAttribute("committeescheduleeditdata", committeescheduleeditdata);
			req.setAttribute("committeeminutes",service.CommitteeMinutesSpecdetails());
			List<Object[]> invitedlists= service.CommitteeAtendance(committeescheduleid,"1");
			
			Map<Object,List<Object[]>> invitedlist = invitedlists.stream().filter(row->row[4].toString().equalsIgnoreCase("P")).collect(Collectors.groupingBy(row -> row[3]));
			List<Map.Entry<Object, List<Object[]>>> entries = new ArrayList<>(invitedlist.entrySet());
			
			req.setAttribute("committeeinvitedlist", invitedlists);
			req.setAttribute("projectid", projectid);
			req.setAttribute("actionlist", service.MinutesViewAllActionList(committeescheduleid));
			req.setAttribute("labdetails", service.LabDetails(committeescheduleeditdata[24].toString()));
			req.setAttribute("isprint", "Y");
			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(committeescheduleeditdata[24].toString()));
			req.setAttribute("meetingcount",service.MeetingNo(committeescheduleeditdata));
			req.setAttribute("labInfo", service.LabDetailes(LabCode));
			req.setAttribute("ActionPlanSixMonths", service.ActionPlanSixMonths(projectid));
			String filename=committeescheduleeditdata[11].toString().replace("/", "-");

			req.setAttribute("flagforView", "M");
			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);

			req.setAttribute("ccmFlag", req.getParameter("ccmFlag"));

			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/committee/MoMTabularMinutes.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"));
			req.setAttribute("tableactionlist",  actionsdata);

			PdfReader pdf1=new PdfReader(path+File.separator+filename+".pdf");

			PdfDocument pdfDocument = new PdfDocument(pdf1);

			pdfDocument.close();
			pdf1.close();	       


			String typeOfHost = "L";
			MailConfigurationDto mailAuthentication=null;
			try {
				mailAuthentication = mailService.getMailConfigByTypeOfHost(typeOfHost);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Exception in getting mail configuration for Programme Mom "+e.getMessage());
				System.out.println(e.getMessage());
			}	
			
			File pdfFile = new File(path + File.separator + filename + ".pdf"); 


			List<String> ccEmails = new ArrayList<>();
			String mainEmail = "";
			ArrayList<String> membertypes = new ArrayList<>(Arrays.asList("CC","CS","PS","CI","I","P","CH"));
			List<Object[]> committeeInvitedList = service.CommitteeAtendance(committeescheduleid,"1");

			for(Object[] obj : committeeInvitedList) {
			    if(membertypes.contains(obj[3].toString()) && obj[4].toString().equalsIgnoreCase("P") && obj[8] != null) {
			        ccEmails.add(obj[8].toString());
			    }
			    if(obj[3].toString().equalsIgnoreCase("CC")) {
			        mainEmail = obj[8].toString();
			    }
			}
			ccEmails.remove(mainEmail); 


			final String username = mailAuthentication.getUsername().toString();
			final String password = mailAuthentication.getPassword().toString(); 
			
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");     
			props.put("mail.smtp.host", mailAuthentication.getHost());
			props.put("mail.smtp.port", mailAuthentication.getPort());
			props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
 
			
			Session session = Session.getDefaultInstance(props,
					new jakarta.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username,password);
				}
			});	

			try {
			    MimeMessage message = new MimeMessage(session);
			    message.setFrom(new InternetAddress(username));
//			    mainEmail="naveen.vedts@gmail.com";
			    message.setRecipient(Message.RecipientType.TO, new InternetAddress(mainEmail));
			    message.setSubject("MoM Attachment for " + committeescheduleeditdata[11].toString());


			    for (String cc : ccEmails) {
			        message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
			    }

			    MimeBodyPart textPart = new MimeBodyPart();
			    textPart.setContent("<div style='font-family:Arial; font-size:14px; color:#333;'>"
				    		+"<p>Dear Sir/Madam,</p>"
							+"<p>Please find attached the PMG Minutes of Meeting held on "+sdf3.format(committeescheduleeditdata[2])+". </p>"
							+"<p><i>This is a system generated email. Please do not reply.</i></p>"+
						"</div>","text/html; charset=UTF-8");


			    MimeBodyPart attachmentPart = new MimeBodyPart();
			    attachmentPart.attachFile(pdfFile);


			    MimeMultipart multipart = new MimeMultipart();
			    multipart.addBodyPart(textPart);
			    multipart.addBodyPart(attachmentPart);

			    message.setContent(multipart);


			    Transport.send(message);
			    logger.info("Mail sent successfully to " + mainEmail);

			} catch (Exception e) {
			    logger.error("Error sending email: " + e.getMessage(), e);
			    e.printStackTrace();
				System.out.println(e.getMessage());
			}


			FileInputStream fis = new FileInputStream(pdfFile);
			DataOutputStream os = new DataOutputStream(res.getOutputStream());
			res.setHeader("Content-Length", String.valueOf(pdfFile.length()));
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fis.read(buffer)) >= 0) {
				os.write(buffer, 0, len);
			}

			os.close();
			fis.close();
			Path pathOfFile2 = Paths.get(path + "/" + filename  + ".pdf");

			Gson json = new Gson();
			return json.toJson(mailcount);
			
		}catch (Exception e) {
			logger.error("Exception in sending the mail for programme MOM "+e.getMessage());
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	
//	Naveen R 28/02/2026
	
	@RequestMapping(value="CommitteeMinutesNewMOMDownload.htm", method = {RequestMethod.POST,RequestMethod.GET})
	public void CommitteeMinutesNewMOMDownload(HttpServletRequest req,HttpServletResponse res, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		String EmpNo=(String)ses.getAttribute("EmpNo");
		String EmpName=(String)ses.getAttribute("EmpName");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeMinutesNewMOMDownload.htm "+UserId);
		try
		{		
			String committeescheduleid = req.getParameter("committeescheduleid");			
			String PROTECTED_MINUTES = req.getParameter("PROTECTED MINUTES");

			Object[] committeescheduleeditdata=service.CommitteeScheduleEditData(committeescheduleid);
			String projectid= committeescheduleeditdata[9].toString();
			String committeeid=committeescheduleeditdata[0].toString();

			List<Object[]> list = printservice.getMilestoneBriefingList(committeescheduleid);

			Map<String,List<Object[]>> milestoneBriefingMap = list!=null ? list.stream().collect(Collectors.groupingBy(obj -> obj[1]!=null?obj[1].toString():"UNKNOWN",LinkedHashMap::new,Collectors.toList())) : new LinkedHashMap<String, List<Object[]>>();
			req.setAttribute("milestoneBriefingMap", milestoneBriefingMap);
			if(Long.parseLong(projectid) >0 && committeescheduleeditdata[22].toString().equals("Y")) 
			{
				CommitteeMeetingDPFMFrozen dpfm = service.getFrozenDPFMMinutes(committeescheduleid);
				//CommitteeProjectBriefingFrozen CPF=service.getFrozenCommitteeMOM(committeescheduleid);
				
				
				res.setContentType("application/pdf"); res.setHeader("Content-Disposition",
						"inline; name="+ dpfm.getDPFMFileName()+".pdf; filename"+dpfm.getDPFMFileName()); 
				Path filepath = Paths.get(uploadpath, LabCode, "DPFM", dpfm.getDPFMFileName());
				//						 File f=new File(uploadpath+dpfm.getFrozenDPFMPath()+dpfm.getDPFMFileName());
				File f=filepath.toFile();

				OutputStream out = res.getOutputStream(); 
				FileInputStream in = new FileInputStream(f); 
				byte[] buffer = new byte[4096]; 
				int length; 
				while((length = in.read(buffer)) > 0) 
				{ 
					out.write(buffer, 0, length);
				}
				in.close(); 
				out.close();
			}
			else 
			{

				Object[] projectdetails = null;

				if(projectid!=null && Integer.parseInt(projectid)>0)
				{
					projectdetails = service.projectdetails(projectid);
					req.setAttribute("projectdetails", projectdetails);
				}
				String divisionid= committeescheduleeditdata[16].toString();
				if(divisionid!=null && Integer.parseInt(divisionid)>0)
				{
					req.setAttribute("divisiondetails", service.DivisionData(divisionid));
				}
				String initiationid= committeescheduleeditdata[17].toString();
				if(initiationid!=null && Integer.parseInt(initiationid)>0)
				{
					req.setAttribute("initiationdetails", service.Initiationdetails(initiationid));
				}

				Committee committee = printservice.getCommitteeData(committeeid);

				HashMap< String, ArrayList<Object[]>> actionsdata=new LinkedHashMap<String, ArrayList<Object[]>>();
				long lastid=service.getLastPmrcId(projectid, committeeid, committeescheduleid);

				//****************************envi download start**********************************
				List<Object[]> envisagedDemandlist  = new ArrayList<Object[]>();
				envisagedDemandlist=service.getEnvisagedDemandList(projectid);
				req.setAttribute("envisagedDemandlist", envisagedDemandlist);

				req.setAttribute("committeeminutesspeclist",service.CommitteeScheduleMinutesMom(committeescheduleid) );
				req.setAttribute("committeescheduleeditdata", committeescheduleeditdata);
				req.setAttribute("committeeminutes",service.CommitteeMinutesSpecNew());
				req.setAttribute("committeeinvitedlist", service.CommitteeAtendance(committeescheduleid,"1"));			
				req.setAttribute("labdetails", service.LabDetails(committeescheduleeditdata[24].toString()));
				req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(committeescheduleeditdata[24].toString()));
				req.setAttribute("meetingcount",service.MeetingNo(committeescheduleeditdata));
				//req.setAttribute("milestonedatalevel6", printservice.BreifingMilestoneDetails(projectid,committeeid));

				// this method will show the milestones based on each meeting date
				req.setAttribute("milestonedatalevel6", printservice.BreifingMilestoneDetails(projectid,committeeid,committeescheduleeditdata[2].toString()));

				//				req.setAttribute("committeeminutessub",service.CommitteeMinutesSub());
				//				req.setAttribute("CommitteeAgendaList", service.CommitteeAgendaList(committeescheduleid));
				String LevelId= "2";
				Object[] MileStoneLevelId = printservice.MileStoneLevelId(projectid,committeeid);
				if( MileStoneLevelId!= null) {
					LevelId= MileStoneLevelId[0].toString();
				}
				req.setAttribute("levelid", LevelId);
				req.setAttribute("labInfo", service.LabDetailes(LabCode));
				/*---------------------------------------------------------------------------------------------------------------*/
				//				if(Long.parseLong(projectid) >0 && committeescheduleeditdata[22].toString().equals("N") ) {

				final String localUri=uri+"/pfms_serv/financialStatusBriefing?ProjectCode="+(projectdetails!=null?projectdetails[4].toString():"")+"&rupess="+10000000;
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				headers.set("labcode", LabCode);
				String jsonResult=null;
				try {
					HttpEntity<String> entity = new HttpEntity<String>(headers);
					ResponseEntity<String> response=restTemplate.exchange(localUri, HttpMethod.POST, entity, String.class);
					jsonResult=response.getBody();						
				}catch(Exception e) {
					req.setAttribute("errorMsg", "errorMsg");
				}
				ObjectMapper mapper = new ObjectMapper();
				mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
				List<ProjectFinancialDetails> projectDetails=null;
				if(jsonResult!=null) {
					try {
						projectDetails = mapper.readValue(jsonResult, new TypeReference<List<ProjectFinancialDetails>>(){});
						req.setAttribute("financialDetails",projectDetails);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				}

				final String localUri2=uri+"/pfms_serv/getTotalDemand";

				String jsonResult2=null;
				try {
					HttpEntity<String> entity = new HttpEntity<String>(headers);
					ResponseEntity<String> response=restTemplate.exchange(localUri2, HttpMethod.POST, entity, String.class);
					jsonResult2=response.getBody();						
				}catch(Exception e) {
					req.setAttribute("errorMsg", "errorMsg");
				}
				ObjectMapper mapper2 = new ObjectMapper();
				mapper2.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				mapper2.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
				List<TotalDemand> totaldemand=null;
				if(jsonResult2!=null) {
					try {
						totaldemand = mapper2.readValue(jsonResult2, new TypeReference<List<TotalDemand>>(){});
						req.setAttribute("TotalProcurementDetails",totaldemand);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				}

				List<Object[]> procurementStatusList=(List<Object[]>)service.ProcurementStatusList(projectid);
				List<Object[]> procurementOnDemand=null;
				List<Object[]> procurementOnSanction=null;
				
			    List<List<Object[]>> overallfinance = new ArrayList<>();
			    List<String> Pmainlist = printservice.ProjectsubProjectIdList(projectid);
			    List<Object[]> ProjectDetails = new ArrayList<>();
			    
		    	for(String proId: Pmainlist) {
		    		List<Object[]> projDetails = printservice.ProjectDetails(proId);
		    		ProjectDetails.add(!projDetails.isEmpty() ? projDetails.get(0) : new Object[0]);
		    		overallfinance.add(printservice.getrOverallFinance(proId));
		    	}
		    	req.setAttribute("projectidlist", Pmainlist);
		    	req.setAttribute("overallfinance", overallfinance);
		    	req.setAttribute("ProjectDetails", ProjectDetails);

				if(procurementStatusList!=null){
					Map<Object, List<Object[]>> map = procurementStatusList.stream().collect(Collectors.groupingBy(c -> c[9])); 
					Collection<?> keys = map.keySet();
					for(Object key:keys){
						if(key.toString().equals("D")) {
							procurementOnDemand=map.get(key);
						}else if(key.toString().equals("S")) {
							procurementOnSanction=map.get(key);
						}
					}
				}
				List<Object[]> actionlist= service.MinutesViewAllActionList(committeescheduleid);

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

				//req.setAttribute("lastpmrcactions", printservice.LastPMRCActions(projectid,committeeid));
				req.setAttribute("lastpmrcactions", printservice.LastPMRCActions(projectid,committeeid,committeescheduleeditdata[2].toString()));

				//							

				req.setAttribute("actionlist",actionsdata);
				req.setAttribute("procurementOnDemand", procurementOnDemand);
				req.setAttribute("procurementOnSanction", procurementOnSanction);
				req.setAttribute("ActionPlanSixMonths", service.ActionPlanSixMonths(projectid));
				req.setAttribute("projectdatadetails", service.ProjectDataDetails(projectid));
				// new code
				List<Object[]>totalMileStones=service.totalProjectMilestones(projectid);//get all the milestones details based on projectid
				List<Object[]>first=null;   //store the milestones with levelid 1
				List<Object[]>second=null;	// store the milestones with levelid 2
				List<Object[]>three= null; // store the milestones with levelid 3
				Map<Integer,String> treeMapLevOne = new TreeMap<>();  // store the milestoneid with level id 1 and counts 
				Map<Integer,String>treeMapLevTwo= new TreeMap<>(); // store the milestonidid with level id 2 and counts
				Map<Integer,String>treeMapLevThree= new TreeMap<>();  // store the milestoneid with level id 3 and counts 
				TreeSet<Integer> AllMilestones = new TreeSet<>();   // store the number of milestone in sorted order
				if(!totalMileStones.isEmpty()) {
					for(Object[]obj:totalMileStones){
						AllMilestones.add(Integer.parseInt(obj[22].toString())); // getting the milestones from list
					}
					for(Integer mile:AllMilestones) {
						int count=1;
						first=totalMileStones.stream().
								filter(i->i[26].toString().equalsIgnoreCase("1") && i[22].toString().equalsIgnoreCase(mile+""))
								.map(objectArray -> new Object[]{objectArray[0], objectArray[2]})
								.collect(Collectors.toList());
						for(Object[]obj:first) {
							treeMapLevOne.put(Integer.parseInt(obj[1].toString()),"A"+(count++));// to get the first level
						}
					}
					for (Map.Entry<Integer,String> entry : treeMapLevOne.entrySet()) {
						int count=1;
						second=totalMileStones.stream().
								filter(i->i[26].toString().equalsIgnoreCase("2") && i[2].toString().equalsIgnoreCase(entry.getKey()+""))
								.map(objectArray -> new Object[] {entry.getKey(),objectArray[3]})
								.collect(Collectors.toList());
						for(Object[]obj:second) {
							treeMapLevTwo.put(Integer.parseInt(obj[1].toString()),entry.getValue()+"-B"+(count++));
						}
					}
					for(Map.Entry<Integer,String>entry: treeMapLevTwo.entrySet()) {
						int count=1;
						three=totalMileStones.stream().
								filter(i->i[26].toString().equalsIgnoreCase("3") && i[3].toString().equalsIgnoreCase(entry.getKey()+""))
								.map(objectArray -> new Object[] {entry.getKey(),objectArray[4]})
								.collect(Collectors.toList());
						for(Object[]obj:three) {
							treeMapLevThree.put(Integer.parseInt(obj[1].toString()), "C"+(count++)); 

						}
					}
				}
				req.setAttribute("treeMapLevOne", treeMapLevOne);
				req.setAttribute("treeMapLevTwo", treeMapLevTwo);
				// new code end
				//code on 06-10-2023				 	 
				List<List<Object[]>> ReviewMeetingList = new ArrayList<List<Object[]>>();
				List<List<Object[]>> ReviewMeetingListPMRC = new ArrayList<List<Object[]>>();
				ReviewMeetingList.add(printservice.ReviewMeetingList(projectid, "EB"));
				ReviewMeetingListPMRC.add(printservice.ReviewMeetingList(projectid, "PMRC")); 
				Map<Integer,String> mappmrc = new HashMap<>();
				Map<Integer,String> mapEB = new HashMap<>();
				int pmrccount=0;
				for (Object []obj:ReviewMeetingListPMRC.get(0)) {
					mappmrc.put(++pmrccount,obj[3].toString());
				}

				int ebcount=0;

				for (Object []obj:ReviewMeetingList.get(0)) {
					mapEB.put(++ebcount,obj[3].toString());
				}
				req.setAttribute("mappmrc", mappmrc);
				req.setAttribute("mapEB", mapEB);

				Object[]momAttachment=service.MomAttachmentFile(committeescheduleid);
				req.setAttribute("tableactionlist",  actionsdata);
				//
				String filename=committeescheduleeditdata[11].toString().replace("/", "-");
				String path=req.getServletContext().getRealPath("/view/temp");
				req.setAttribute("path",path);
				CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
				req.getRequestDispatcher("/view/committee/CommitteeMinutesNewMOM.jsp").forward(req, customResponse);
				String html = customResponse.getOutput();

				HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"));
				req.setAttribute("tableactionlist",  actionsdata);

				PdfReader pdf1=new PdfReader(path+File.separator+filename+".pdf");

				PdfDocument pdfDocument = new PdfDocument(pdf1);

				pdfDocument.close();
				pdf1.close();	       

				res.setContentType("application/pdf");
				res.setHeader("Content-disposition","inline;filename="+filename+".pdf");

				File f = new File(path + File.separator + filename + ".pdf");
				try (OutputStream out = res.getOutputStream();
				     FileInputStream in = new FileInputStream(f)) {

				    byte[] buffer = new byte[4096];
				    int length;
				    while ((length = in.read(buffer)) > 0) {
				        out.write(buffer, 0, length);
				    }
				    out.flush(); 
				}

				Files.deleteIfExists(Paths.get(path + File.separator + filename + ".pdf"));

			}
		}
		catch (Exception e) 
		{
			e.printStackTrace(); 
			logger.error(new Date() +"Inside CommitteeMinutesNewMOMDownload.htm "+UserId,e);
		}
	}
	
	@RequestMapping(value = "CoverLetter.htm",method = {RequestMethod.GET,RequestMethod.POST})
	public void downloadCoverLetter(HttpServletRequest req,HttpSession ses,HttpServletResponse res) {
		String UserId=(String)ses.getAttribute("Username");
		String EmpNo=(String)ses.getAttribute("EmpNo");
		String EmpName=(String)ses.getAttribute("EmpName");
		String LabCode =(String) ses.getAttribute("labcode");
		try {
		
			logger.info("Inside CoverLetter.htm with "+ UserId+" on "+new Date());
						
			String committeeId = (String) req.getParameter("committeeid");
			String projectId = (String) req.getParameter("projectid");	
			
			List<Object[]> projectMeetingList = service.getMeetingCountList(committeeId);
			
			Committee committee = printservice.getCommitteeData(committeeId);
			
			if(projectId != null && !projectId.isBlank() && committeeId !=null && !committeeId.isBlank()) {
				Object[] lastmeetingCreated = printservice.getLastcreatedSchedule(projectId, committeeId);
				req.setAttribute("lastmeetingCreated", lastmeetingCreated);
			}
			
			req.setAttribute("projectMeetingList", projectMeetingList);
			req.setAttribute("committee", committee);
			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(LabCode));
			

			String filename = "CoverLetter";
			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/committee/CoverLetter.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"));

			PdfReader pdf1=new PdfReader(path+File.separator+filename+".pdf");

			PdfDocument pdfDocument = new PdfDocument(pdf1);

			pdfDocument.close();
			pdf1.close();
			

			res.setContentType("application/pdf");
			res.setHeader("Content-disposition","inline;filename="+filename+".pdf");

			File f = new File(path + File.separator + filename + ".pdf");
			try (OutputStream out = res.getOutputStream();
			     FileInputStream in = new FileInputStream(f)) {

			    byte[] buffer = new byte[4096];
			    int length;
			    while ((length = in.read(buffer)) > 0) {
			        out.write(buffer, 0, length);
			    }
			    out.flush(); 
			}

			Files.deleteIfExists(Paths.get(path + File.separator + filename + ".pdf"));
			

		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +"Inside CoverLetter.htm "+UserId,e);
		}
		
	}
	

	@RequestMapping(value = "MeetingTabularMinutesNewDownload.htm")
	public void minutesOfMeetingNewDownload(HttpServletRequest req, HttpSession ses, RedirectAttributes redir,HttpServletResponse res,Model model) throws Exception
	{

		String UserId=(String)ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside MeetingTabularMinutesNewDownload.htm "+UserId);
		try
		{
			String committeescheduleid = req.getParameter("committeescheduleid");
			Object[] committeescheduleeditdata=service.CommitteeScheduleEditDataforMom(committeescheduleid);
			String projectid= committeescheduleeditdata[9].toString();
			if(projectid!=null && Integer.parseInt(projectid)>0)
			{
				req.setAttribute("projectdetails", service.projectdetails(projectid));
			}
			String divisionid= committeescheduleeditdata[16].toString();
			if(divisionid!=null && Integer.parseInt(divisionid)>0)
			{
				req.setAttribute("divisiondetails", service.DivisionData(divisionid));
			}
			String initiationid= committeescheduleeditdata[17].toString();
			if(initiationid!=null && Integer.parseInt(initiationid)>0)
			{
				req.setAttribute("initiationdetails", service.Initiationdetails(initiationid));
			}

			List<Object[]> actionlist= service.MinutesViewAllActionList(committeescheduleid);
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

			req.setAttribute("actionsdata",actionsdata );

			req.setAttribute("committeeminutesspeclist",service.CommitteeScheduleMinutesforAction(committeescheduleid) );
			req.setAttribute("committeescheduleeditdata", committeescheduleeditdata);
			req.setAttribute("committeeminutes",service.CommitteeMinutesSpecdetails());
			List<Object[]> invitedlists= service.CommitteeAtendance(committeescheduleid,"1");
			
			Map<Object,List<Object[]>> invitedlist = invitedlists.stream().filter(row->row[4].toString().equalsIgnoreCase("P")).collect(Collectors.groupingBy(row -> row[3]));
			List<Map.Entry<Object, List<Object[]>>> entries = new ArrayList<>(invitedlist.entrySet());
			
			req.setAttribute("committeeinvitedlist", invitedlists);
			req.setAttribute("projectid", projectid);				
			req.setAttribute("actionlist", service.MinutesViewAllActionList(committeescheduleid));
			req.setAttribute("labdetails", service.LabDetails(committeescheduleeditdata[24].toString()));
			req.setAttribute("isprint", "Y");
			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(committeescheduleeditdata[24].toString()));
			req.setAttribute("meetingcount",service.MeetingNo(committeescheduleeditdata));
			req.setAttribute("labInfo", service.LabDetailes(LabCode));
			req.setAttribute("isGeneral", "Y");
			req.setAttribute("ActionPlanSixMonths", service.ActionPlanSixMonths(projectid));
			String filename=committeescheduleeditdata[11].toString().replace("/", "-");

			req.setAttribute("flagforView", "M");			
			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);

			req.setAttribute("ccmFlag", req.getParameter("ccmFlag"));

			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/committee/MoMTabularMinutes.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"));
			req.setAttribute("tableactionlist",  actionsdata);

			PdfReader pdf1=new PdfReader(path+File.separator+filename+".pdf");

			PdfDocument pdfDocument = new PdfDocument(pdf1);

			pdfDocument.close();
			pdf1.close();	       

			res.setContentType("application/pdf");
			res.setHeader("Content-disposition","inline;filename="+filename+".pdf");

			File f = new File(path + File.separator + filename + ".pdf");
			try (OutputStream out = res.getOutputStream();
			     FileInputStream in = new FileInputStream(f)) {

			    byte[] buffer = new byte[4096];
			    int length;
			    while ((length = in.read(buffer)) > 0) {
			        out.write(buffer, 0, length);
			    }
			    out.flush(); 
			}

			Files.deleteIfExists(Paths.get(path + File.separator + filename + ".pdf"));

		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside MeetingTabularMinutesNewDownload.htm "+UserId,e);
		}
	}

	@RequestMapping(value="ProjectMOMV2Download.htm", method = {RequestMethod.POST,RequestMethod.GET})
	public void CommitteeMinutesNewMOMV2Download(HttpServletRequest req,HttpServletResponse res, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		String EmpNo=(String)ses.getAttribute("EmpNo");
		String EmpName=(String)ses.getAttribute("EmpName");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CommitteeMinutesNewMOMDownload.htm "+UserId);
		try
		{		
			String committeescheduleid = req.getParameter("scheduleid");	
			

			Object[] committeescheduleeditdata=service.CommitteeScheduleEditData(committeescheduleid);
			String projectid= committeescheduleeditdata[9].toString();
			String committeeid=committeescheduleeditdata[0].toString();

			List<Object[]> list = printservice.getMilestoneBriefingList(committeescheduleid);
			
			Map<String,List<Object[]>> milestoneBriefingMap = list!=null ? list.stream().collect(Collectors.groupingBy(obj -> obj[1]!=null?obj[1].toString():"UNKNOWN",LinkedHashMap::new,Collectors.toList())) : new LinkedHashMap<String, List<Object[]>>();
			req.setAttribute("milestoneBriefingMap", milestoneBriefingMap);
			if(Long.parseLong(projectid) >0 && committeescheduleeditdata[22].toString().equals("Y")) 
			{
				CommitteeMeetingDPFMFrozen dpfm = service.getFrozenDPFMMinutes(committeescheduleid);
				//CommitteeProjectBriefingFrozen CPF=service.getFrozenCommitteeMOM(committeescheduleid);
				
				
				res.setContentType("application/pdf"); res.setHeader("Content-Disposition",
						"inline; name="+ dpfm.getDPFMFileName()+".pdf; filename"+dpfm.getDPFMFileName()); 
				Path filepath = Paths.get(uploadpath, LabCode, "DPFM", dpfm.getDPFMFileName());
				//						 File f=new File(uploadpath+dpfm.getFrozenDPFMPath()+dpfm.getDPFMFileName());
				File f=filepath.toFile();

				OutputStream out = res.getOutputStream(); 
				FileInputStream in = new FileInputStream(f); 
				byte[] buffer = new byte[4096]; 
				int length; 
				while((length = in.read(buffer)) > 0) 
				{ 
					out.write(buffer, 0, length);
				}
				in.close(); 
				out.close();
			}
			else 
			{

				Object[] projectdetails = null;

				if(projectid!=null && Integer.parseInt(projectid)>0)
				{
					projectdetails = service.projectdetails(projectid);
					req.setAttribute("projectdetails", projectdetails);
				}
				String divisionid= committeescheduleeditdata[16].toString();
				if(divisionid!=null && Integer.parseInt(divisionid)>0)
				{
					req.setAttribute("divisiondetails", service.DivisionData(divisionid));
				}
				String initiationid= committeescheduleeditdata[17].toString();
				if(initiationid!=null && Integer.parseInt(initiationid)>0)
				{
					req.setAttribute("initiationdetails", service.Initiationdetails(initiationid));
				}

				Committee committee = printservice.getCommitteeData(committeeid);

				HashMap< String, ArrayList<Object[]>> actionsdata=new LinkedHashMap<String, ArrayList<Object[]>>();
				long lastid=service.getLastPmrcId(projectid, committeeid, committeescheduleid);

				//****************************envi download start**********************************
				List<Object[]> envisagedDemandlist  = new ArrayList<Object[]>();
				envisagedDemandlist=service.getEnvisagedDemandList(projectid);
				req.setAttribute("envisagedDemandlist", envisagedDemandlist);

				req.setAttribute("committeeminutesspeclist",service.CommitteeScheduleMinutes(committeescheduleid) );
				req.setAttribute("committeescheduleeditdata", committeescheduleeditdata);
				req.setAttribute("committeeminutes",service.CommitteeMinutesSpecNew());
				req.setAttribute("committeeinvitedlist", service.CommitteeAtendance(committeescheduleid,"1"));			
				req.setAttribute("labdetails", service.LabDetails(committeescheduleeditdata[24].toString()));
				req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(committeescheduleeditdata[24].toString()));
				req.setAttribute("meetingcount",service.MeetingNo(committeescheduleeditdata));
				//req.setAttribute("milestonedatalevel6", printservice.BreifingMilestoneDetails(projectid,committeeid));

				// this method will show the milestones based on each meeting date
				req.setAttribute("milestonedatalevel6", printservice.BreifingMilestoneDetails(projectid,committeeid,committeescheduleeditdata[2].toString()));

				//				req.setAttribute("committeeminutessub",service.CommitteeMinutesSub());
				//				req.setAttribute("CommitteeAgendaList", service.CommitteeAgendaList(committeescheduleid));
				String LevelId= "2";
				Object[] MileStoneLevelId = printservice.MileStoneLevelId(projectid,committeeid);
				if( MileStoneLevelId!= null) {
					LevelId= MileStoneLevelId[0].toString();
				}
				req.setAttribute("levelid", LevelId);
				req.setAttribute("labInfo", service.LabDetailes(LabCode));
				/*---------------------------------------------------------------------------------------------------------------*/
				//				if(Long.parseLong(projectid) >0 && committeescheduleeditdata[22].toString().equals("N") ) {

				final String localUri=uri+"/pfms_serv/financialStatusBriefing?ProjectCode="+(projectdetails!=null?projectdetails[4].toString():"")+"&rupess="+10000000;
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				headers.set("labcode", LabCode);
				String jsonResult=null;
				try {
					HttpEntity<String> entity = new HttpEntity<String>(headers);
					ResponseEntity<String> response=restTemplate.exchange(localUri, HttpMethod.POST, entity, String.class);
					jsonResult=response.getBody();						
				}catch(Exception e) {
					req.setAttribute("errorMsg", "errorMsg");
				}
				ObjectMapper mapper = new ObjectMapper();
				mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
				List<ProjectFinancialDetails> projectDetails=null;
				if(jsonResult!=null) {
					try {
						projectDetails = mapper.readValue(jsonResult, new TypeReference<List<ProjectFinancialDetails>>(){});
						req.setAttribute("financialDetails",projectDetails);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				}

				final String localUri2=uri+"/pfms_serv/getTotalDemand";

				String jsonResult2=null;
				try {
					HttpEntity<String> entity = new HttpEntity<String>(headers);
					ResponseEntity<String> response=restTemplate.exchange(localUri2, HttpMethod.POST, entity, String.class);
					jsonResult2=response.getBody();						
				}catch(Exception e) {
					req.setAttribute("errorMsg", "errorMsg");
				}
				ObjectMapper mapper2 = new ObjectMapper();
				mapper2.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				mapper2.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
				List<TotalDemand> totaldemand=null;
				if(jsonResult2!=null) {
					try {
						totaldemand = mapper2.readValue(jsonResult2, new TypeReference<List<TotalDemand>>(){});
						req.setAttribute("TotalProcurementDetails",totaldemand);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				}

				List<Object[]> procurementStatusList=(List<Object[]>)service.ProcurementStatusList(projectid);
				List<Object[]> procurementOnDemand=null;
				List<Object[]> procurementOnSanction=null;
				
			    List<List<Object[]>> overallfinance = new ArrayList<>();
			    List<String> Pmainlist = printservice.ProjectsubProjectIdList(projectid);
			    List<Object[]> ProjectDetails = new ArrayList<>();
			    
		    	for(String proId: Pmainlist) {
		    		List<Object[]> projDetails = printservice.ProjectDetails(proId);
		    		ProjectDetails.add(!projDetails.isEmpty() ? projDetails.get(0) : new Object[0]);
		    		overallfinance.add(printservice.getrOverallFinance(proId));
		    	}
		    	req.setAttribute("projectidlist", Pmainlist);
		    	req.setAttribute("overallfinance", overallfinance);
		    	req.setAttribute("ProjectDetails", ProjectDetails);

				if(procurementStatusList!=null){
					Map<Object, List<Object[]>> map = procurementStatusList.stream().collect(Collectors.groupingBy(c -> c[9])); 
					Collection<?> keys = map.keySet();
					for(Object key:keys){
						if(key.toString().equals("D")) {
							procurementOnDemand=map.get(key);
						}else if(key.toString().equals("S")) {
							procurementOnSanction=map.get(key);
						}
					}
				}
				List<Object[]> actionlist= service.MinutesViewAllActionList(committeescheduleid);

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

				//req.setAttribute("lastpmrcactions", printservice.LastPMRCActions(projectid,committeeid));
				req.setAttribute("lastpmrcactions", printservice.LastPMRCActions(projectid,committeeid,committeescheduleeditdata[2].toString()));

				//							

				req.setAttribute("actionlist",actionsdata);
				req.setAttribute("procurementOnDemand", procurementOnDemand);
				req.setAttribute("procurementOnSanction", procurementOnSanction);
				req.setAttribute("ActionPlanSixMonths", service.ActionPlanSixMonths(projectid));
				req.setAttribute("projectdatadetails", service.ProjectDataDetails(projectid));
				// new code
				List<Object[]>totalMileStones=service.totalProjectMilestones(projectid);//get all the milestones details based on projectid
				List<Object[]>first=null;   //store the milestones with levelid 1
				List<Object[]>second=null;	// store the milestones with levelid 2
				List<Object[]>three= null; // store the milestones with levelid 3
				Map<Integer,String> treeMapLevOne = new TreeMap<>();  // store the milestoneid with level id 1 and counts 
				Map<Integer,String>treeMapLevTwo= new TreeMap<>(); // store the milestonidid with level id 2 and counts
				Map<Integer,String>treeMapLevThree= new TreeMap<>();  // store the milestoneid with level id 3 and counts 
				TreeSet<Integer> AllMilestones = new TreeSet<>();   // store the number of milestone in sorted order
				if(!totalMileStones.isEmpty()) {
					for(Object[]obj:totalMileStones){
						AllMilestones.add(Integer.parseInt(obj[22].toString())); // getting the milestones from list
					}
					for(Integer mile:AllMilestones) {
						int count=1;
						first=totalMileStones.stream().
								filter(i->i[26].toString().equalsIgnoreCase("1") && i[22].toString().equalsIgnoreCase(mile+""))
								.map(objectArray -> new Object[]{objectArray[0], objectArray[2]})
								.collect(Collectors.toList());
						for(Object[]obj:first) {
							treeMapLevOne.put(Integer.parseInt(obj[1].toString()),"A"+(count++));// to get the first level
						}
					}
					for (Map.Entry<Integer,String> entry : treeMapLevOne.entrySet()) {
						int count=1;
						second=totalMileStones.stream().
								filter(i->i[26].toString().equalsIgnoreCase("2") && i[2].toString().equalsIgnoreCase(entry.getKey()+""))
								.map(objectArray -> new Object[] {entry.getKey(),objectArray[3]})
								.collect(Collectors.toList());
						for(Object[]obj:second) {
							treeMapLevTwo.put(Integer.parseInt(obj[1].toString()),entry.getValue()+"-B"+(count++));
						}
					}
					for(Map.Entry<Integer,String>entry: treeMapLevTwo.entrySet()) {
						int count=1;
						three=totalMileStones.stream().
								filter(i->i[26].toString().equalsIgnoreCase("3") && i[3].toString().equalsIgnoreCase(entry.getKey()+""))
								.map(objectArray -> new Object[] {entry.getKey(),objectArray[4]})
								.collect(Collectors.toList());
						for(Object[]obj:three) {
							treeMapLevThree.put(Integer.parseInt(obj[1].toString()), "C"+(count++)); 

						}
					}
				}
				req.setAttribute("treeMapLevOne", treeMapLevOne);
				req.setAttribute("treeMapLevTwo", treeMapLevTwo);
				// new code end
				//code on 06-10-2023				 	 
				List<List<Object[]>> ReviewMeetingList = new ArrayList<List<Object[]>>();
				List<List<Object[]>> ReviewMeetingListPMRC = new ArrayList<List<Object[]>>();
				ReviewMeetingList.add(printservice.ReviewMeetingList(projectid, "EB"));
				ReviewMeetingListPMRC.add(printservice.ReviewMeetingList(projectid, "PMRC")); 
				Map<Integer,String> mappmrc = new HashMap<>();
				Map<Integer,String> mapEB = new HashMap<>();
				int pmrccount=0;
				for (Object []obj:ReviewMeetingListPMRC.get(0)) {
					mappmrc.put(++pmrccount,obj[3].toString());
				}

				int ebcount=0;

				for (Object []obj:ReviewMeetingList.get(0)) {
					mapEB.put(++ebcount,obj[3].toString());
				}
				req.setAttribute("mappmrc", mappmrc);
				req.setAttribute("mapEB", mapEB);

				Object[]momAttachment=service.MomAttachmentFile(committeescheduleid);
				req.setAttribute("tableactionlist",  actionsdata);
				//
				String filename=committeescheduleeditdata[11].toString().replace("/", "-");
				String path=req.getServletContext().getRealPath("/view/temp");
				req.setAttribute("path",path);
				CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
				req.getRequestDispatcher("/view/committee/CommitteeMinutesNewMOMV2.jsp").forward(req, customResponse);
				String html = customResponse.getOutput();

				HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"));
				req.setAttribute("tableactionlist",  actionsdata);

				PdfReader pdf1=new PdfReader(path+File.separator+filename+".pdf");

				PdfDocument pdfDocument = new PdfDocument(pdf1);

				pdfDocument.close();
				pdf1.close();	       

				res.setContentType("application/pdf");
				res.setHeader("Content-disposition","inline;filename="+filename+".pdf");

				File f = new File(path + File.separator + filename + ".pdf");
				try (OutputStream out = res.getOutputStream();
				     FileInputStream in = new FileInputStream(f)) {

				    byte[] buffer = new byte[4096];
				    int length;
				    while ((length = in.read(buffer)) > 0) {
				        out.write(buffer, 0, length);
				    }
				    out.flush(); 
				}

				Files.deleteIfExists(Paths.get(path + File.separator + filename + ".pdf"));

			}
		}
		catch (Exception e) 
		{
			e.printStackTrace(); 
			logger.error(new Date() +"Inside CommitteeMinutesNewMOMDownload.htm "+UserId,e);
		}
	}
	
	@RequestMapping(value = "EligibleRepEmployees.htm", method = {RequestMethod.GET})
	@ResponseBody
	public String getEligibleRepEmployees(
	        @RequestParam("repType") String repType,
	        @RequestParam("scheduleid") String scheduleId,
	        @RequestParam("currentInvitationId") String currentInvitationId) {

	    try {
	        List<Object[]> allEmployees = masterservice.EmployeeList();

	        JSONArray jsonArray = new JSONArray();
	        for (Object[] emp : allEmployees) {
	            String empNo = emp[0] != null ? emp[0].toString() : "";

	            JSONObject obj = new JSONObject();
	            obj.put("id", empNo);
	            obj.put("name", emp[1]);
	            obj.put("labCode", emp[2]);
	            obj.put("designationId", emp[3]);
	            obj.put("scheduleId", scheduleId);
	            jsonArray.put(obj);
	        }
	        return jsonArray.toString();

	    } catch (Exception e) {
	        e.printStackTrace();
	        return new JSONArray().toString();
	    }
	}
	
	@RequestMapping(value = "ChangeRepresentative.htm", method = {RequestMethod.POST})
	public String changeRepresentative(HttpServletRequest req,HttpSession ses,RedirectAttributes redir) {
	    try {
			String UserId=(String)ses.getAttribute("Username");
	        
	    	String invitationId = req.getParameter("invitationId");
	    	String parentInvitationId = req.getParameter("parentInvitationId");
	    	String newEmpNo = req.getParameter("newEmpNo");
	    	String newLabCode = req.getParameter("newLabCode");
	    	String designationId = req.getParameter("designationId");

	    	redir.addAttribute("committeescheduleid",req.getParameter("committeescheduleid"));
	    	
	    	CommitteeInvitationDto dto = new CommitteeInvitationDto();
	    	
	    	dto.setDesignationId(designationId);
	    	dto.setEmpId(newEmpNo);
	    	dto.setInvitationId(invitationId);
	    	dto.setEmpLabCode(newLabCode);
	    	dto.setParentInvitationId(parentInvitationId);
	    	dto.setCreatedBy(UserId);

	    	long count = service.changeRepresentative(dto);
	    	if(count > 0) {
	    		redir.addAttribute("result","Rep Change Successful");
	    	}else {
	    		redir.addAttribute("resultFail","Rep Change Unsuccessful");
	    	}
	    	return "redirect:/CommitteeAttendance.htm";
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    return "static/Error";
	}
	
	@RequestMapping(value = "AddRepresentative.htm", method = {RequestMethod.POST})
	public String addRepresentative(HttpServletRequest req,HttpSession ses,RedirectAttributes redir) {
	    try {
			String UserId=(String)ses.getAttribute("Username");
	        
	    	String repCode = req.getParameter("repCode");
	    	String parentInvitationId = req.getParameter("parentInvitationId");
	    	String newEmpNo = req.getParameter("newEmpNo");
	    	String newLabCode = req.getParameter("newLabCode");
	    	String designationId = req.getParameter("designationId");
	    	String scheduleId  = req.getParameter("committeescheduleid");

	    	redir.addAttribute("committeescheduleid",scheduleId);
	    	
	    	CommitteeInvitationDto dto = new CommitteeInvitationDto();
	    	
	    	dto.setDesignationId(designationId);
	    	dto.setEmpId(newEmpNo);
	    	dto.setRepCode(repCode);
	    	dto.setEmpLabCode(newLabCode);
	    	dto.setCommitteeScheduleId(scheduleId);
	    	dto.setParentInvitationId(parentInvitationId);
	    	dto.setCreatedBy(UserId);

	    	long count = service.addRepresentative(dto);
	    	if(count > 0) {
	    		redir.addAttribute("result","Rep Added Successfully");
	    	}else {
	    		redir.addAttribute("resultFail","Rep Add Unsuccessful");
	    	}
	    	return "redirect:/CommitteeInvitations.htm";
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    return "static/Error";
	}
	
	//DLRL
	@RequestMapping(value = "CommitteeOnlineAttendanceToggle.htm", method = RequestMethod.GET)
	public @ResponseBody String CommitteeOnlineAttendanceToggle(HttpSession ses, HttpServletRequest req, RedirectAttributes redir)
			throws Exception 
	{
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CommitteeAttendanceToggle.htm "+UserId);
		try
		{
			String Invitationid = req.getParameter("invitationid");
			String isOnlineAttendenc = req.getParameter("isOnlineAttendenc");
			
			service.CommitteeOnlineAttendanceToggle(Invitationid,isOnlineAttendenc);
		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside CommitteeAttendanceToggle.htm "+UserId,e);
		}
		
		return "0";
	}
}
