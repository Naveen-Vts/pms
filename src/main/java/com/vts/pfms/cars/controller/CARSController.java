package com.vts.pfms.cars.controller;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.styledxmlparser.resolver.font.BasicFontProvider;
import com.vts.pfms.CharArrayWriterResponse;
import com.vts.pfms.FormatConverter;
import com.vts.pfms.cars.dto.CARSRSQRDetailsDTO;
import com.vts.pfms.cars.dto.CARSApprovalForwardDTO;
import com.vts.pfms.cars.dto.CARSContractDetailsDTO;
import com.vts.pfms.cars.model.CARSAnnualReport;
import com.vts.pfms.cars.model.CARSContract;
import com.vts.pfms.cars.model.CARSInitiation;
import com.vts.pfms.cars.model.CARSInitiationTrans;
import com.vts.pfms.cars.model.CARSOtherDocDetails;
import com.vts.pfms.cars.model.CARSRSQRDeliverables;
import com.vts.pfms.cars.model.CARSRSQRMajorRequirements;
import com.vts.pfms.cars.model.CARSSoC;
import com.vts.pfms.cars.model.CARSSoCMilestones;
import com.vts.pfms.cars.model.CARSSoCMilestonesProgress;
import com.vts.pfms.cars.service.CARSService;
import com.vts.pfms.ccm.service.CCMService;
import com.vts.pfms.committee.dto.ActionAssignDto;
import com.vts.pfms.committee.dto.ActionMainDto;
import com.vts.pfms.committee.model.ActionAssign;
import com.vts.pfms.committee.model.ActionMain;
import com.vts.pfms.committee.service.ActionService;
import com.vts.pfms.print.service.PrintService;
import com.vts.pfms.project.service.ProjectService;
import com.vts.pfms.utils.InputValidator;
import com.vts.pfms.utils.PMSLogoUtil;

@Controller
public class CARSController {

	private static final Logger logger = LogManager.getLogger(CARSController.class);
	
	FormatConverter fc = new FormatConverter();
	private SimpleDateFormat sdtf = fc.getSqlDateAndTimeFormat();
	private SimpleDateFormat sdf = fc.getSqlDateFormat();
	private SimpleDateFormat rdf = fc.getRegularDateFormat();
	
//	@Autowired
//	RestTemplate restTemplate;
	
	private final CARSService service;
	private final ProjectService projectservice;
	private final ActionService actionservice;
	private final CCMService ccmservice;
	private final PrintService printservice;
	
	private final PMSLogoUtil logoUtil ;
	
	@Value("${ApplicationFilesDrive}")
	private String labLogoPath;
	
	// constructor dependency injection
	public CARSController(CARSService service, ProjectService projectservice, PrintService printservice, CCMService ccmservice, ActionService actionservice,PMSLogoUtil logoUtil ) {
		this.service = service;
		this.projectservice = projectservice;
		this.actionservice = actionservice;
		this.ccmservice = ccmservice;
		this.printservice = printservice;
		this.logoUtil=logoUtil;
		
	}
	
	
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
	      
	        default:
	            return false;
	    }


	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public String getLabLogoAsBase64() throws IOException {

//		String path = LabLogoPath + "\\images\\lablogos\\lablogo.png";
		Path logoPath = Paths.get(labLogoPath,"images","lablogos","lablogo.png");
		try {
			return Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(logoPath.toFile()));
		} catch (FileNotFoundException e) {
			System.err.println("File Not Found at Path " + logoPath);
		}
		return "/print/.jsp";
	}
	
	public String getSecondLabLogoAsBase64() throws IOException {
		
//		String path = LabLogoPath + "\\images\\lablogos\\lablogo2.png";
		Path logoPath = Paths.get(labLogoPath,"images","lablogos","lablogo2.png");
		try {
			return Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(logoPath.toFile()));
		} catch (FileNotFoundException e) {
			System.err.println("File Not Found at Path " + logoPath);
		}
		return "/print/.jsp";
	}
	
//	@RequestMapping(value="CARSInitiationList.htm")
//	public String carsInitiationList(HttpServletRequest req, HttpSession ses) throws Exception{
//		String UserId = (String) ses.getAttribute("Username");
//		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
//		logger.info(new Date()+ " Inside CARSInitiationList.htm "+UserId);
//		try {
//			req.setAttribute("InitiationList", service.carsInitiationList(EmpId));
//			return "cars/InitiationList";
//		}catch (Exception e) {
//			e.printStackTrace();
//			logger.error(new Date()+" Inside CARSInitiationList.htm "+UserId,e);
//			return "static/Error";
//		}
//		
//	}
	
	@RequestMapping(value = "CARSInitiationList.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String carsInitiationList(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String UserId = (String) ses.getAttribute("Username");
		String LoginType = (String) ses.getAttribute("LoginType");
		logger.info(new Date() +"Inside CARSInitiationList.htm "+UserId);
		try {
			String pagination = req.getParameter("pagination");
			int pagin = Integer.parseInt(pagination!=null?pagination:"0");

			/* fetching actual data */
			List<Object[]> initiationList = service.carsInitiationList(LoginType, EmpId);

			// adding values to this List<Object[]>
			List<Object[]> arrayList = new ArrayList<>();

			/* search action starts */
			String search = req.getParameter("search");
			if(search!="" && search!=null) {
				req.setAttribute("searchFactor", search);
				for(Object[] obj: initiationList) {
					for(Object value:obj) {
						if(value instanceof String)
							if (((String)(value)).toLowerCase().contains(search.toLowerCase()) ) {
								arrayList.add(obj);
								continue;
							}
					}
				}
				if (arrayList.size()==0)req.setAttribute("resultfail", "search result is empty!");
			}
			else if(req.getParameter("clicked")!=null) {
				req.setAttribute("resultfail", "search is empty!");
			}

			/* search action ends */
			int p = initiationList.size()/6;
			int extra = initiationList.size()%6;
			if(arrayList.size()==0) arrayList=initiationList;

			/* pagination process starts */

			if(pagin>0 && pagin<(p+(extra>0?1:0)))
			{
				req.setAttribute("pagination", pagin);
				arrayList = arrayList.subList(pagin*6, ((pagin*6)+6)<arrayList.size()?((pagin*6)+6):arrayList.size());
			}
			else
			{
				arrayList = arrayList.subList(0, arrayList.size()>=6?6:arrayList.size());
				req.setAttribute("pagination", 0);
				pagin=0;
			}

			req.setAttribute("InitiationList", arrayList);
			req.setAttribute("maxpagin", p+(extra>0?1:0) );
			req.setAttribute("committeeId", String.valueOf(ccmservice.getCommitteeIdByCommitteeCode("CARS")));
			/* pagination process ends */

			return "cars/InitiationList";
		}
		catch (Exception e) {
			logger.error(new Date() +" Inside CARSInitiationList.htm "+UserId, e);
			e.printStackTrace();
			return "static/Error";
		} 
	}
	
	@RequestMapping(value="CARSInitiationDetails.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String carsInitiationDetails(HttpServletRequest req, HttpSession ses) throws Exception{
		String UserId = (String) ses.getAttribute("Username");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String Logintype= (String)ses.getAttribute("LoginType");
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date()+ " Inside CARSInitiationDetails.htm "+UserId);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			String TabId = req.getParameter("TabId");
			String isApproval = req.getParameter("isApproval");
			if(carsInitiationId==null) {
				String fromapprovals = req.getParameter("carsInitiationIdApprovals");
				if(fromapprovals!=null) {
					String[] split = fromapprovals.split("/");
					carsInitiationId = split[0];
					if(isApproval==null) {
						isApproval = split[1];
					}
					if(TabId==null) {
						TabId = split[2];
					}
				}
				
			}
			
			if(carsInitiationId!=null && carsInitiationId!="0") {
				long carsiniid = Long.parseLong(carsInitiationId);
				ses.setAttribute("carsid", carsiniid);
				CARSInitiation carsInitiation = service.getCARSInitiationById(carsiniid);
				req.setAttribute("CARSInitiationData", carsInitiation);
				req.setAttribute("RSQRDetails", service.carsRSQRDetails(carsInitiationId));
				req.setAttribute("RSQRMajorReqr", service.getCARSRSQRMajorReqrByCARSInitiationId(carsiniid));
				req.setAttribute("RSQRDeliverables", service.getCARSRSQRDeliverablesByCARSInitiationId(carsiniid));
				req.setAttribute("ProjectList", projectservice.LoginProjectDetailsList(carsInitiation.getEmpId()+"",Logintype,LabCode));
				req.setAttribute("EmpData", service.getEmpDetailsByEmpId(carsInitiation.getEmpId()+""));
				req.setAttribute("RSQRApprovalEmpData", service.carsTransApprovalData(carsInitiationId,"RF"));
				req.setAttribute("SoCApprovalEmpData", service.carsTransApprovalData(carsInitiationId,"SF"));
				req.setAttribute("CARSRSQRRemarksHistory", service.carsRemarksHistoryByType(carsInitiationId,"RF"));
				req.setAttribute("GDEmpIds", service.getEmpGDEmpId(carsInitiation.getEmpId()+""));
				req.setAttribute("PDEmpIds", service.getEmpPDEmpId(carsInitiation.getFundsFrom()));
				
				req.setAttribute("CARSSoCData", service.getCARSSoCByCARSInitiationId(carsiniid));
				req.setAttribute("CARSSoCRemarksHistory", service.carsRemarksHistoryByType(carsInitiationId,"SF"));
				req.setAttribute("CARSSoCMilestones", service.getCARSSoCMilestonesByCARSInitiationId(carsiniid));
				
				req.setAttribute("CARSContractData", service.getCARSContractByCARSInitiationId(carsiniid));
				req.setAttribute("CARSContractConsultants", service.getCARSContractConsultantsByCARSInitiationId(carsiniid));
				req.setAttribute("CARSContractEquipment", service.getCARSContractEquipmentByCARSInitiationId(carsiniid));
			}else {
				req.setAttribute("ProjectList", projectservice.LoginProjectDetailsList(EmpId,Logintype,LabCode));
				req.setAttribute("EmpData", service.getEmpDetailsByEmpId(EmpId));
			}
			
			req.setAttribute("carsInitiationId", carsInitiationId);
			
			req.setAttribute("TabId", TabId!=null?TabId:"1");
			
			String attributes = req.getParameter("attributes");
			req.setAttribute("attributes", req.getParameter("attributes")!=null?attributes:"Introduction");
			
			req.setAttribute("isApproval", isApproval);
			
			return "cars/InitiationDetails";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CARSInitiationDetails.htm "+UserId,e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="CARSInitiationAdd.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String carsInitiationAddSubmit(HttpServletRequest req, HttpSession ses,RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date()+ " Inside CARSInitiationAdd.htm "+UserId);
		try {
			
			String initiationTitle=req.getParameter("initiationTitle");
			String Amount=req.getParameter("amount");
			String Duration=req.getParameter("duration");
			String initiationAim=req.getParameter("initiationAim");
			String justification=req.getParameter("justification");
			String rspInstitute=req.getParameter("rspInstitute");
			String rspAddress=req.getParameter("rspAddress");
			String rspCity=req.getParameter("rspCity");
			String rspState=req.getParameter("rspState");
			String rspPinCode=req.getParameter("rspPinCode");
			String piName=req.getParameter("piName");
			String piDesig=req.getParameter("piDesig");
			String piDept=req.getParameter("piDept");
			String piMobileNo=req.getParameter("piMobileNo");
			String piEmail=req.getParameter("piEmail");
			String piFaxNo=req.getParameter("piFaxNo");
			
			if(InputValidator.isContainsHTMLTags(initiationTitle)) {
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'CARS Title' should not contain HTML Tags.!");
			}
			if(InputValidator.isContainsHTMLTags(Amount)) {
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Amount' must contain only numbers.!");
			}
			if(!InputValidator.isContainsNumberOnly(Duration)) {
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Duration' must contain only numbers.!");
			}
			if(InputValidator.isContainsHTMLTags(initiationAim)) {
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Aim' should not contain HTML Tags.!");
			}
			if(InputValidator.isContainsHTMLTags(justification)) {
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Justification' should not contain HTML Tags.!");
			}
			if(InputValidator.isContainsHTMLTags(rspInstitute)) {
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Name of Institute' should not contain HTML Tags.!");
			}
			if(InputValidator.isContainsHTMLTags(rspAddress)) {
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Address' should not contain HTML Tags.!");
			}
			if(!InputValidator.isValidNameWithCapitalsAndSmallLettersAndSpace(rspCity)) {
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'City' must contain only Alphabets.!");
			}
			if(!InputValidator.isContainsNumberOnly(rspPinCode)) {
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'PinCode' must contain only numbers.!");
			}
			if(InputValidator.isContainsHTMLTags(piName)) {
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Principal Investigator Details (Name)' should not contain HTML Tags.!");
			}
			if(!InputValidator.isValidNameWithCapitalsAndSmallLettersAndSpace(piDesig)) {
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Designation' must contain only Alphabets.!");
			}
			if(!InputValidator.isValidNameWithCapitalsAndSmallLettersAndSpace(piDept)) {
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Department' must contain only Alphabets.!");
			}
			if(!InputValidator.isContainsNumberOnly(piMobileNo)) {
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Mobile Number' must contain only Numbers.!");
			}
			if(!InputValidator.isValidEmail(piEmail)) {
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Email' should be in a Email Format.!");
			}
			if(InputValidator.isContainsHTMLTags(piFaxNo)) {
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Fax No' should not contain HTML Tags.!");
			}
			CARSInitiation initiation = CARSInitiation.builder()
					                   .EmpId(Long.parseLong(EmpId))
					                   .InitiationTitle(initiationTitle)
					                   .InitiationAim(initiationAim)
					                   .Justification(justification	)
					                   .FundsFrom(req.getParameter("fundsFrom"))
					                   .Amount(Amount)
					                   .Duration(Duration)
					                   .RSPInstitute(rspInstitute)
					                   .RSPAddress(rspAddress)
					                   .RSPCity(rspCity)
					                   .RSPState(rspState)
					                   .RSPPinCode(rspPinCode)
					                   .PITitle(req.getParameter("piTitle"))
					                   .PIName(piName)
					                   .PIDesig(piDesig)
					                   .PIDept(piDept)
					                   .PIMobileNo(piMobileNo)
					                   .PIEmail(piEmail)
					                   .PIFaxNo(piFaxNo)
					                   .EquipmentNeed("N")
					                   .DPCSoCForwardedBy(0)
					                   .CARSStatusCode("INI")
					                   .CARSStatusCodeNext("FWD")
					                   .DPCSoCStatus("N")
					                   .CreatedBy(UserId)
					                   .CreatedDate(sdtf.format(new Date()))
					                   .IsActive(1)
					                   .build();
			long result = service.addCARSInitiation(initiation,labcode);
			if(result!=0) {
				redir.addAttribute("result", "CARS Initiation Added Successfully");
			}else {
				redir.addAttribute("resultfail", "CARS Initiation Add UnSuccessful");
			}
			redir.addAttribute("carsInitiationId", result);
			redir.addAttribute("TabId","1");
			
			return "redirect:/CARSInitiationDetails.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CARSInitiationAdd.htm "+UserId,e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="CARSInitiationEdit.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String carsInitiationEditSubmit(HttpServletRequest req, HttpSession ses,RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date()+ " Inside CARSInitiationEdit.htm "+UserId);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			
			String initiationTitle=req.getParameter("initiationTitle");
			String Amount=req.getParameter("amount");
			String Duration=req.getParameter("duration");
			String initiationAim=req.getParameter("initiationAim");
			String justification=req.getParameter("justification");
			String rspInstitute=req.getParameter("rspInstitute");
			String rspAddress=req.getParameter("rspAddress");
			String rspCity=req.getParameter("rspCity");
			String rspState=req.getParameter("rspState");
			String rspPinCode=req.getParameter("rspPinCode");
			String piName=req.getParameter("piName");
			String piDesig=req.getParameter("piDesig");
			String piDept=req.getParameter("piDept");
			String piMobileNo=req.getParameter("piMobileNo");
			String piEmail=req.getParameter("piEmail");
			String piFaxNo=req.getParameter("piFaxNo");
			
			if(InputValidator.isContainsHTMLTags(initiationTitle)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","1");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'CARS Title' should not contain HTML Tags.!");
			}
			if(InputValidator.isContainsHTMLTags(Amount)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","1");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Amount' should not contain HTML Tags.!");
			}
			if(!InputValidator.isContainsNumberOnly(Duration)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","1");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Duration' must contain only numbers.!");
			}
			if(InputValidator.isContainsHTMLTags(initiationAim)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","1");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Aim' should not contain HTML Tags.!");
			}
			if(InputValidator.isContainsHTMLTags(justification)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","1");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Justification' should not contain HTML Tags.!");
			}
			if(InputValidator.isContainsHTMLTags(rspInstitute)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","1");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Name of Institute' should not contain HTML Tags.!");
			}
			if(InputValidator.isContainsHTMLTags(rspAddress)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","1");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Address' should not contain HTML Tags.!");
			}
			if(!InputValidator.isValidNameWithCapitalsAndSmallLettersAndSpace(rspCity)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","1");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'City' must contain only Alphabets.!");
			}
			if(!InputValidator.isContainsNumberOnly(rspPinCode)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","1");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'PinCode' must contain only numbers.!");
			}
			if(InputValidator.isContainsHTMLTags(piName)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","1");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Principal Investigator Details (Name)' should not contain HTML Tags.!");
			}
			if(!InputValidator.isValidNameWithCapitalsAndSmallLettersAndSpace(piDesig)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","1");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Designation' must contain only Alphabets.!");
			}
			if(!InputValidator.isValidNameWithCapitalsAndSmallLettersAndSpace(piDept)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","1");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Department' must contain only Alphabets.!");
			}
			if(!InputValidator.isContainsNumberOnly(piMobileNo)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","1");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Mobile Number' must contain only Numbers.!");
			}
			if(!InputValidator.isValidEmail(piEmail)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","1");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Email' should be in a Email Format.!");
			}
			if(InputValidator.isContainsHTMLTags(piFaxNo)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","1");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Fax No' should not contain HTML Tags.!");
			}
			
			CARSInitiation initiation = service.getCARSInitiationById(Long.parseLong(carsInitiationId));
			initiation.setInitiationTitle(initiationTitle);
			initiation.setInitiationAim(initiationAim);
			initiation.setJustification(justification);
			initiation.setFundsFrom(req.getParameter("fundsFrom"));
			initiation.setAmount(Amount);
			initiation.setDuration(Duration);
			initiation.setRSPInstitute(rspInstitute);
			initiation.setRSPAddress(rspAddress);
			initiation.setRSPCity(rspCity);
			initiation.setRSPState(req.getParameter("rspState"));
			initiation.setRSPPinCode(rspPinCode);
			initiation.setPITitle(req.getParameter("piTitle"));
			initiation.setPIName(piName);
			initiation.setPIDesig(piDesig);
			initiation.setPIDept(piDept);
			initiation.setPIMobileNo(piMobileNo);
			initiation.setPIEmail(piEmail);
			initiation.setPIFaxNo(piFaxNo);
			initiation.setModifiedBy(UserId);
			initiation.setModifiedDate(sdtf.format(new Date()));
			
			long result = service.editCARSInitiation(initiation);
			if(result!=0) {
				redir.addAttribute("result", "CARS Initiation Edited Successfully");
			}else {
				redir.addAttribute("resultfail", "CARS Initiation Edit UnSuccessfull");
			}
			redir.addAttribute("carsInitiationId", result);
			redir.addAttribute("TabId","1");
			
			return "redirect:/CARSInitiationDetails.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside CARSInitiationEdit.htm "+UserId,e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="RSQRDetailsSubmit.htm",method= {RequestMethod.GET,RequestMethod.POST})
	public String RequiremnetIntroSubmit(HttpServletRequest req,HttpSession ses, RedirectAttributes redir) {
		String UserId=(String)ses.getAttribute("Username");
		String labcode=(String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside RSQRDetailsSubmit.htm "+UserId);
		try {
		String carsInitiationId=req.getParameter("carsInitiationId");
		String attributes=req.getParameter("attributes");
		String Details=req.getParameter("Details");
		String tab=req.getParameter("tab");
		if(tab!=null && tab.equalsIgnoreCase("5")) {
			Details=req.getParameter("Details2");
		}
		Object[] carsRSQR = service.carsRSQRDetails(carsInitiationId);
		long result=0l;
		if(carsRSQR==null) {
			result=service.carsRSQRDetailsSubmit(carsInitiationId,attributes,Details,UserId,labcode);
		}else {
			result=service.carsRSQRDetailsUpdate(carsInitiationId,attributes,Details,UserId,labcode);
			if (result > 0) {
				redir.addAttribute("result", attributes+" updated Successfully");
			} else {
				redir.addAttribute("resultfail", attributes+" update Unsuccessful");
			}
			redir.addAttribute("carsInitiationId", carsInitiationId);
			redir.addAttribute("TabId",tab!=null?tab:"2");
			redir.addAttribute("attributes", attributes);
			return "redirect:/CARSInitiationDetails.htm";
		}
		if (result > 0) {
			redir.addAttribute("result", attributes+" added Successfully");
		} else {
			redir.addAttribute("resultfail", attributes+" add Unsuccessful");
		}
		redir.addAttribute("carsInitiationId", carsInitiationId);
		redir.addAttribute("TabId",tab!=null?tab:"2");
		redir.addAttribute("attributes", attributes);

		return "redirect:/CARSInitiationDetails.htm";
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside RSQRDetailsSubmit.htm "+UserId,e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="RSQRDetailsByAjax.htm",method = {RequestMethod.GET})
	public @ResponseBody String RequirementIntroDetails(HttpServletRequest req, HttpSession ses) throws Exception {
		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside RSQRDetailsByAjax.htm "+UserId);
		Object[] rsqrDetails=null;
		try {
			rsqrDetails = service.carsRSQRDetails(req.getParameter("carsinitiationid"));
		}
		catch(Exception e){
			e.printStackTrace();
			logger.error(new Date() +" Inside RSQRDetailsByAjax.htm"+UserId, e);
		}
		Gson json = new Gson();
		return json.toJson(rsqrDetails);
	}
	
	@RequestMapping(value="RSQRMajorReqrSubmit.htm",method= {RequestMethod.GET,RequestMethod.POST})
	public String rsqrMajorReqrDetailsSubmit(HttpServletRequest req,HttpSession ses, RedirectAttributes redir) throws Exception{
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside RSQRMajorReqrSubmit.htm "+UserId);
		try {
		String carsInitiationId = req.getParameter("carsInitiationId");
		String attributes=req.getParameter("attributes");
		long carsiniid = Long.parseLong(carsInitiationId);
		
		String tab=req.getParameter("tab");
		
		String[] ReqDescription=req.getParameterValues("reqDescription");
		String[] RelevantSpecs=req.getParameterValues("relevantSpecs");
		String[] ValidationMethod=req.getParameterValues("validationMethod");
		String[] Remarks=req.getParameterValues("remarks");
		
	
		if (containsHTMLTags(ReqDescription)) {
			redir.addAttribute("carsInitiationId", carsInitiationId);
			redir.addAttribute("attributes", attributes);
			redir.addAttribute("TabId",tab!=null?tab:"2");
		    return redirectWithError(redir, "CARSInitiationDetails.htm", "'Req Description' should not contain HTML Tags.!");
		}
		if (containsHTMLTags(RelevantSpecs)) {
			redir.addAttribute("carsInitiationId", carsInitiationId);
			redir.addAttribute("attributes", attributes);
			redir.addAttribute("TabId",tab!=null?tab:"2");
		    return redirectWithError(redir, "CARSInitiationDetails.htm", "'Relevant Specification' should not contain HTML Tags.!");
		}
		if (containsHTMLTags(ValidationMethod)) {
			redir.addAttribute("carsInitiationId", carsInitiationId);
			redir.addAttribute("attributes", attributes);
			redir.addAttribute("TabId",tab!=null?tab:"2");
		    return redirectWithError(redir, "CARSInitiationDetails.htm", "'Validation Method' should not contain HTML Tags.!");
		}
		if (containsHTMLTags(Remarks)) {
			redir.addAttribute("carsInitiationId", carsInitiationId);
			redir.addAttribute("attributes", attributes);
			redir.addAttribute("TabId",tab!=null?tab:"2");
		    return redirectWithError(redir, "CARSInitiationDetails.htm", "'Remarks' should not contain HTML Tags.!");
		}

		
		CARSRSQRDetailsDTO dto = new CARSRSQRDetailsDTO();
		dto.setCARSInitiationId(carsiniid);
		dto.setReqId(req.getParameterValues("reqId"));
		dto.setReqDescription(ReqDescription);
		dto.setRelevantSpecs(RelevantSpecs);
		dto.setValidationMethod(ValidationMethod);
		dto.setRemarks(Remarks);
		dto.setUserId(UserId);
		
		
		
		List<CARSRSQRMajorRequirements> majorReqr = service.getCARSRSQRMajorReqrByCARSInitiationId(carsiniid);
		long result = 0l;
		if(majorReqr!=null && majorReqr.size()==0) {
			result = service.carsRSQRMajorReqrDetailsSubmit(dto);
		}else {
			service.removeCARSRSQRMajorReqrDetails(carsiniid);
			result = service.carsRSQRMajorReqrDetailsSubmit(dto);
			if (result > 0) {
				redir.addAttribute("result", "RSQR Major Requirement Details updated Successfully");
			} else {
				redir.addAttribute("resultfail", "RSQR Major Requirement Details update Unsuccessful");
			}
			redir.addAttribute("carsInitiationId", carsInitiationId);
			redir.addAttribute("attributes", attributes);
			redir.addAttribute("TabId",tab!=null?tab:"2");
			return "redirect:/CARSInitiationDetails.htm";
		}
		if (result > 0) {
			redir.addAttribute("result", "RSQR Major Requirement Details added Successfully");
		} else {
			redir.addAttribute("resultfail", "RSQR Major Requirement Details add Unsuccessful");
		}
		redir.addAttribute("carsInitiationId", carsInitiationId);
		redir.addAttribute("attributes", attributes);
		redir.addAttribute("TabId",tab!=null?tab:"2");
		return "redirect:/CARSInitiationDetails.htm";
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside RSQRMajorReqrSubmit.htm "+UserId,e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="RSQRDeliverablesSubmit.htm",method= {RequestMethod.GET,RequestMethod.POST})
	public String rsqrDeliverableDetailsSubmit(HttpServletRequest req,HttpSession ses, RedirectAttributes redir) throws Exception{
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside RSQRDeliverablesSubmit.htm "+UserId);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			String attributes=req.getParameter("attributes");
			long carsiniid = Long.parseLong(carsInitiationId);

			String tab=req.getParameter("tab");
			
			String[] Description=req.getParameterValues("description");
			if (containsHTMLTags(Description)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("attributes", attributes);
				redir.addAttribute("TabId",tab!=null?tab:"2");
			    return redirectWithError(redir, "CARSInitiationDetails.htm", "'Description' should not contain HTML Tags.!");
			}
			
			CARSRSQRDetailsDTO dto = new CARSRSQRDetailsDTO();
			dto.setCARSInitiationId(carsiniid);
			dto.setDescription(Description);
			dto.setDeliverableType(req.getParameterValues("deliverableType"));
			dto.setUserId(UserId);
			
			List<CARSRSQRDeliverables> deliverables = service.getCARSRSQRDeliverablesByCARSInitiationId(carsiniid);
			long result = 0l;
			if(deliverables!=null && deliverables.size()==0) {
				result = service.carsRSQRDeliverableDetailsSubmit(dto);
			}else {
				service.removeCARSRSQRDeliverableDetails(carsiniid);
				result = service.carsRSQRDeliverableDetailsSubmit(dto);
				if (result > 0) {
					redir.addAttribute("result", "RSQR Deliverable Details updated Successfully");
				} else {
					redir.addAttribute("resultfail", "RSQR Deliverable Details update Unsuccessful");
				}
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("attributes", attributes);
				redir.addAttribute("TabId",tab!=null?tab:"2");
				return "redirect:/CARSInitiationDetails.htm";
			}
			if (result > 0) {
				redir.addAttribute("result", "RSQR Deliverable Details added Successfully");
			} else {
				redir.addAttribute("resultfail", "RSQR Deliverable Details add Unsuccessful");
			}
			redir.addAttribute("carsInitiationId", carsInitiationId);
			redir.addAttribute("attributes", attributes);
			redir.addAttribute("TabId",tab!=null?tab:"2");
			return "redirect:/CARSInitiationDetails.htm";
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside RSQRDeliverablesSubmit.htm "+UserId,e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="CARSRSQRDownloadBeforeFreeze.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public void carsRSQRDownloadBeforeFreeze(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside CARSRSQRDownloadBeforeFreeze.htm "+UserId);		
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			if(carsInitiationId!=null && carsInitiationId!="0") {
				long carsiniid = Long.parseLong(carsInitiationId);
				req.setAttribute("CARSInitiationData", service.getCARSInitiationById(carsiniid));
				req.setAttribute("RSQRMajorReqr", service.getCARSRSQRMajorReqrByCARSInitiationId(carsiniid));
				req.setAttribute("RSQRDeliverables", service.getCARSRSQRDeliverablesByCARSInitiationId(carsiniid));
				req.setAttribute("RSQRDetails", service.carsRSQRDetails(carsInitiationId));
				req.setAttribute("CARSSoCMilestones", service.getCARSSoCMilestonesByCARSInitiationId(carsiniid));
			}
			String filename="RSQR";	
			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/print/CARSRSQRDownload.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"));
			PdfWriter pdfw=new PdfWriter(path +File.separator+ "merged.pdf");
			PdfReader pdf1=new PdfReader(path+File.separator+filename+".pdf");
			PdfDocument pdfDocument = new PdfDocument(pdf1, pdfw);	
			pdfDocument.close();
			pdf1.close();	       
			pdfw.close();

			res.setContentType("application/pdf");
			res.setHeader("Content-disposition","inline;filename="+filename+".pdf"); 
			File f=new File(path+"/"+filename+".pdf");

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

			Path pathOfFile2= Paths.get( path+File.separator+filename+".pdf"); 
			Files.delete(pathOfFile2);		

		}
	    catch(Exception e) {	    		
    		logger.error(new Date() +" Inside CARSRSQRDownloadBeforeFreeze.htm "+UserId, e);
    		e.printStackTrace();
    	}		
	}
	
	@RequestMapping(value="RSQRApprovalSubmit.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String rsqrApprovalSubmit(HttpServletRequest req, HttpSession ses, HttpServletResponse res, RedirectAttributes redir) throws Exception{
		String UserId =(String)ses.getAttribute("Username");
		String labcode =(String)ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() +"Inside RSQRApprovalSubmit.htm "+UserId);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			String action = req.getParameter("Action");
			String remarks = req.getParameter("remarks");
			long carsini = Long.parseLong(carsInitiationId);
			
			CARSInitiation cars = service.getCARSInitiationById(carsini);
			String carsStatusCode = cars.getCARSStatusCode();
			
			if(InputValidator.isContainsHTMLTags(remarks)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				return redirectWithError(redir, "CARSRSQRApprovals.htm", "'Remarks' should not contain HTML Tags.!");
			}
			CARSApprovalForwardDTO dto = new CARSApprovalForwardDTO();
			dto.setCarsinitiationid(carsini);
			dto.setAction(action);
			dto.setEmpId(EmpId);
			dto.setRemarks(remarks);
			long result = service.rsqrApprovalForward(dto,req,res,labcode);
			
			if(action.equalsIgnoreCase("A")) {
				if(carsStatusCode.equalsIgnoreCase("INI") || carsStatusCode.equalsIgnoreCase("REV") || 
				   carsStatusCode.equalsIgnoreCase("RGD") || carsStatusCode.equalsIgnoreCase("RPD")) {
					
					if(result!=0) {
						redir.addAttribute("result","RSQR Approval form forwarded Successfully");
					}else {
						redir.addAttribute("resultfail","RSQR Approval form forward Unsuccessful");
					}
					return "redirect:/CARSInitiationList.htm";
				}else if(carsStatusCode.equalsIgnoreCase("FWD")) {
					if(result!=0) {
						redir.addAttribute("result","RSQR Approval form Approved Successfully");
					}else {
						redir.addAttribute("resultfail","RSQR Approval form Approve Unsuccessful");
					}
					return "redirect:/CARSRSQRApprovals.htm";
				}
			}
			else if(action.equalsIgnoreCase("R") || action.equalsIgnoreCase("D")) {
				if(result!=0) {
					redir.addAttribute("result",action.equalsIgnoreCase("R")?"RSQR Approval form Returned Successfully":"RSQR Approval form Disapproved Successfully");
				}else {
					redir.addAttribute("resultfail",action.equalsIgnoreCase("R")?"RSQR Approval form Return Unsuccessful":"RSQR Approval form Disapprove Unsuccessful");
				}
			}
			return "redirect:/CARSRSQRApprovals.htm";
			
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside RSQRApprovalSubmit.htm "+UserId,e);
			return "static/Error";
		}
	}

	@RequestMapping(value = "CARSTransStatus.htm" , method={RequestMethod.POST,RequestMethod.GET})
	public String carsTransStatus(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception
	{
		String Username = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside CARSTransStatus.htm "+Username);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			req.setAttribute("TransactionList", service.carsTransListByType(carsInitiationId,"A")) ;
			req.setAttribute("TransFlag", "A");
			req.setAttribute("carsInitiationId", carsInitiationId);
			return "cars/CARSTransactionStatus";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSTransStatus.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="CARSRSQRApprovals.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String carsApprovals(HttpServletRequest req,HttpSession ses) throws Exception{
		String Username = (String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() +"Inside CARSRSQRApprovals.htm "+Username);
		try {

			String fromdate = req.getParameter("fromdate");
			String todate = req.getParameter("todate");

			LocalDate today=LocalDate.now();

			if(fromdate==null) 
			{
				fromdate=today.withDayOfMonth(1).toString();
				todate = today.toString();

			}else
			{
				fromdate=fc.RegularToSqlDate(fromdate);
				todate=fc.RegularToSqlDate(todate);
			}

			req.setAttribute("fromdate", fromdate);
			req.setAttribute("todate", todate);
			req.setAttribute("tab", req.getParameter("tab"));

			req.setAttribute("PendingList", service.carsRSQRPendingList(EmpId));
			req.setAttribute("ApprovedList", service.carsRSQRApprovedList(EmpId,fromdate,todate,"B"));
			req.setAttribute("DPandCSoCPendingList", service.carsDPandCSoCPendingList(EmpId, labcode));
			req.setAttribute("DPandCSoCApprovedList", service.carsDPCSoCApprovedList(EmpId, fromdate, todate));
			req.setAttribute("CSPendingList", service.carsCSPendingList(EmpId, labcode));
			req.setAttribute("CSApprovedList", service.carsCSApprovedList(EmpId, fromdate, todate));
			req.setAttribute("MPPendingList", service.carsMPPendingList(EmpId, labcode));
			req.setAttribute("MPApprovedList", service.carsMPApprovedList(EmpId, fromdate, todate));
			
			return "cars/CARSApprovals";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSRSQRApprovals.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="CARSRSQRApprovalDownload.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public void carsRSQRApprovalDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside CARSRSQRApprovalDownload.htm "+UserId);		
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			if(carsInitiationId!=null && carsInitiationId!="0") {
				long carsiniid = Long.parseLong(carsInitiationId);
				CARSInitiation carsInitiation = service.getCARSInitiationById(carsiniid);
				req.setAttribute("CARSInitiationData", carsInitiation);
				req.setAttribute("EmpData", service.getEmpDetailsByEmpId(carsInitiation.getEmpId()+""));
				req.setAttribute("RSQRApprovalEmpData", service.carsTransApprovalData(carsInitiationId, "RF"));
				req.setAttribute("CARSRSQRRemarksHistory", service.carsRemarksHistoryByType(carsInitiationId,"RF"));
			}
			String filename="CARS-RSQR_Approval";	
			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/print/CARSRSQRApprovalDownload.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"));
			PdfWriter pdfw=new PdfWriter(path +File.separator+ "merged.pdf");
			PdfReader pdf1=new PdfReader(path+File.separator+filename+".pdf");
			PdfDocument pdfDocument = new PdfDocument(pdf1, pdfw);	
			pdfDocument.close();
			pdf1.close();	       
			pdfw.close();

			res.setContentType("application/pdf");
			res.setHeader("Content-disposition","inline;filename="+filename+".pdf"); 
			File f=new File(path+"/"+filename+".pdf");

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

			Path pathOfFile2= Paths.get( path+File.separator+filename+".pdf"); 
			Files.delete(pathOfFile2);		

		}
	    catch(Exception e) {	    		
    		logger.error(new Date() +" Inside CARSRSQRApprovalDownload.htm "+UserId, e);
    		e.printStackTrace();
    	}		
	}
	
	@RequestMapping(value="CARSInvForSoODownload.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public void carsInvForSoODownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception{
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CARSInvForSoODownload.htm "+UserId);		
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			if(carsInitiationId!=null && carsInitiationId!="0") {
				long carsiniid = Long.parseLong(carsInitiationId);
				CARSInitiation carsInitiation = service.getCARSInitiationById(carsiniid);
				req.setAttribute("CARSInitiationData", carsInitiation);
				req.setAttribute("EmpData", service.getEmpDetailsByEmpId(carsInitiation.getEmpId()+""));
				Object[] obj = service.getApprAuthorityDataByType(labcode, "DO-RTMD");
				req.setAttribute("DPandC", service.getEmpDetailsByEmpId(obj[0].toString()));
			}
			req.setAttribute("LabMasterData", service.getLabDetailsByLabCode(labcode));
			req.setAttribute("lablogo", getLabLogoAsBase64());
			String filename="Inv_for_SoO";	
			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			
			//Path for Hindi font
			String fontPath = req.getServletContext().getRealPath("/view/pfp/NotoSansDevanagari-Regular.ttf");
			req.setAttribute("fontPath", fontPath);
			//PdfFont hindiFont = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H, true);
			
			req.getRequestDispatcher("/view/print/CARSInvForSoODownload.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			// Hindi font converter
			ConverterProperties converterProperties = new ConverterProperties();
			FontProvider fontProvider = new BasicFontProvider();
			fontProvider.addFont(fontPath, PdfEncodings.IDENTITY_H);
			converterProperties.setFontProvider(fontProvider);
			
//			HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"),converterProperties);
//			
//			PdfWriter pdfw=new PdfWriter(path +File.separator+ "merged.pdf");
//			PdfReader pdf1=new PdfReader(path+File.separator+filename+".pdf");
//			PdfDocument pdfDocument = new PdfDocument(pdf1, pdfw);	
//			
//			pdfDocument.close();
//			pdf1.close();	       
//			pdfw.close();

			try (
					PdfWriter writer = new PdfWriter(path + File.separator + filename + ".pdf");
					PdfDocument pdfDoc = new PdfDocument(writer);
					Document document = HtmlConverter.convertToDocument(html, pdfDoc, converterProperties)) {
				
				 	// Create font using PdfDocument
			    	PdfFont hindiFont = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H, pdfDoc);
	                document.setFont(hindiFont);
	            }
			
			res.setContentType("application/pdf");
			res.setHeader("Content-disposition","inline;filename="+filename+".pdf"); 
			File f=new File(path+"/"+filename+".pdf");

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

			Path pathOfFile2= Paths.get( path+File.separator+filename+".pdf"); 
			Files.delete(pathOfFile2);		

		}
	    catch(Exception e) {	    		
    		logger.error(new Date() +" Inside CARSInvForSoODownload.htm "+UserId, e);
    		e.printStackTrace();
    	}		
	}
	
	@RequestMapping(value="CARSSoCAdd.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String carsSocAddSubmit(HttpServletRequest req,HttpSession ses,RedirectAttributes redir,
			@RequestPart(name="SoOUpload", required = false) MultipartFile SoOFile,
			@RequestPart(name="FRUpload", required = false) MultipartFile FRFile,
			@RequestPart(name="ExecutionPlan", required = false) MultipartFile ExecutionPlanFile) throws Exception{
		String Username = (String)ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() +"Inside CARSSoCAdd.htm "+Username);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			long carsiniid = Long.parseLong(carsInitiationId);
			
			String Alignment=req.getParameter("alignment");
			String timeReasonability=req.getParameter("timeReasonability");
			String costReasonability=req.getParameter("costReasonability");
			String rspSelection=req.getParameter("rspSelection");
			String socCriterion=req.getParameter("socCriterion");
			
			
			
			// 🔹 Validate file types
	        if (!isValidFileType(SoOFile) || !isValidFileType(FRFile)||!isValidFileType(ExecutionPlanFile)
	               ) {

	        	redir.addAttribute("carsInitiationId", carsInitiationId);
				
				redir.addAttribute("TabId","4");
				
	        	return redirectWithError(redir, "CARSInitiationDetails.htm",
	                    "Invalid file type. Only PDF  files are allowed.");
	        }
			
			if(InputValidator.isContainsHTMLTags(Alignment)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","4");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Alignment with' should not contain HTML Tags.!");
			}
			if(InputValidator.isContainsHTMLTags(timeReasonability)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","4");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Justification for time reasonability' should not contain HTML Tags.!");
			}
			if(InputValidator.isContainsHTMLTags(costReasonability)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","4");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Justification for cost reasonability' should not contain HTML Tags.!");
			}
			if(InputValidator.isContainsHTMLTags(rspSelection)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","4");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Justification for selection of RSP' should not contain HTML Tags.!");
			}
			if(InputValidator.isContainsHTMLTags(socCriterion)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","4");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Success / Acceptance Criterion' should not contain HTML Tags.!");
			}
			
			String rspOfferRef=req.getParameter("rspOfferRef");
			String kp1Details=req.getParameter("kp1Details");
			String kp2Details=req.getParameter("kp2Details");
			if(InputValidator.isContainsHTMLTags(rspOfferRef)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","4");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'RSP's Offer Ref' should not contain HTML Tags.!");
			}
			if(InputValidator.isContainsHTMLTags(kp1Details)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","4");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Key Professional-1 Details' should not contain HTML Tags.!");
			}
			if(InputValidator.isContainsHTMLTags(kp2Details)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","4");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Key Professional-2 Details' should not contain HTML Tags.!");
			}
			
			String[] consultantName=req.getParameterValues("consultantName");
			String[] consultantCompany=req.getParameterValues("consultantCompany");
			String[] equipmentDescription=req.getParameterValues("equipmentDescription");
			if (containsHTMLTags(consultantName)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","4");
			    return redirectWithError(redir, "CARSInitiationDetails.htm", "'Research Consultant Name(s)' should not contain HTML Tags.!");
			}
			if (containsHTMLTags(consultantCompany)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","4");
			    return redirectWithError(redir, "CARSInitiationDetails.htm", "'Research Consultant Company(s)' should not contain HTML Tags.!");
			}
			if (containsHTMLTags(equipmentDescription)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","4");
			    return redirectWithError(redir, "CARSInitiationDetails.htm", "'DRDO-owned Equipment (Description)' should not contain HTML Tags.!");
			}
			
			CARSSoC soc = new CARSSoC();
			soc.setCARSInitiationId(carsiniid);
			soc.setSoCAmount(req.getParameter("socAmount"));
			soc.setSoCDuration(req.getParameter("socDuration"));
			soc.setAlignment(Alignment);
			soc.setTimeReasonability(timeReasonability);
			soc.setCostReasonability(costReasonability);
			soc.setRSPSelection(rspSelection);
			soc.setSoCCriterion(socCriterion);
			soc.setCreatedBy(Username);
			soc.setCreatedDate(sdtf.format(new Date()));
			soc.setIsActive(1);
			
			long result = service.addCARSSoC(soc, SoOFile, FRFile, ExecutionPlanFile);
			
			String rspOfferDate = req.getParameter("rspOfferDate");
			CARSContract contract = new CARSContract();
			contract.setCARSInitiationId(carsiniid);
			contract.setRSPOfferRef(rspOfferRef);
			contract.setRSPOfferDate(rspOfferDate!=null?fc.RegularToSqlDate(rspOfferDate):null);
			contract.setKP1Details(kp1Details);
			contract.setKP2Details(kp2Details);
			contract.setExpndPersonnelCost(req.getParameter("expndPersonnelCost"));
			contract.setExpndEquipmentCost(req.getParameter("expndEquipmentCost"));
			contract.setExpndOthersCost(req.getParameter("expndOthersCost"));
			contract.setExpndGST(req.getParameter("expndGST"));
			contract.setExpndTotalCost(req.getParameter("expndTotalCost"));
			contract.setCreatedBy(Username);
			contract.setCreatedDate(sdtf.format(new Date()));
			contract.setIsActive(1);
			
			long contractResult = service.addCARSContractDetails(contract);
			
			CARSContractDetailsDTO dto = new CARSContractDetailsDTO();
			dto.setCARSInitiationId(carsiniid);
			dto.setConsultantName(consultantName);
			dto.setConsultantCompany(consultantCompany);
			dto.setEquipmentDescription(equipmentDescription);
			dto.setUserId(Username);
			
			long consultantsResult = service.addCARSContractConsultantsDetails(dto);
			
			long equipmentResult = service.addCARSContractEquipmentDetails(dto); 
			 
			if(result!=0 && contractResult!=0 && consultantsResult!=0 && equipmentResult!=0) {
				redir.addAttribute("result", "CARS SoC Details Added Successfully");
				
				// Transactions happend in the approval flow.
				CARSInitiationTrans transaction = CARSInitiationTrans.builder()
												  .CARSInitiationId(soc.getCARSInitiationId())
												  .MilestoneNo("0")
												  .CARSStatusCode("SIN")
												  .LabCode(labcode)
												  .ActionBy(EmpId)
												  .ActionDate(sdtf.format(new Date()))
												  .build();
				service.addCARSInitiationTransaction(transaction);
			}else {
				redir.addAttribute("resultfail", "CARS SoC Details Add UnSuccessful");
			}
			
			redir.addAttribute("carsInitiationId", carsInitiationId);
			redir.addAttribute("carsSoCId",result);
			redir.addAttribute("TabId","4");
			return "redirect:/CARSInitiationDetails.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSSoCAdd.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="CARSSoCEdit.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String carsSocEditSubmit(HttpServletRequest req,HttpSession ses,RedirectAttributes redir,
			@RequestPart(name="SoOUpload", required = false) MultipartFile SoOFile,
			@RequestPart(name="FRUpload", required = false) MultipartFile FRFile,
			@RequestPart(name="ExecutionPlan", required = false) MultipartFile ExecutionPlanFile) throws Exception{
		String Username = (String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CARSSoCEdit.htm "+Username);
		try {
		
			String carsInitiationId = req.getParameter("carsInitiationId");
			
			// 🔹 Validate file types
	        if (!isValidFileType(SoOFile) || !isValidFileType(FRFile)||!isValidFileType(ExecutionPlanFile)) {

	        	redir.addAttribute("carsInitiationId", carsInitiationId);
				
				redir.addAttribute("TabId","4");

	        	return redirectWithError(redir, "CARSInitiationDetails.htm", "Invalid file type. Only PDF  files are allowed.");
	        }
			
			String carsSocId = req.getParameter("carsSocId");
			long carsiniid = Long.parseLong(carsInitiationId);
			
			String Alignment=req.getParameter("alignment");
			String timeReasonability=req.getParameter("timeReasonability");
			String costReasonability=req.getParameter("costReasonability");
			String rspSelection=req.getParameter("rspSelection");
			String socCriterion=req.getParameter("socCriterion");
			if(InputValidator.isContainsHTMLTags(Alignment)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","4");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Alignment with' should not contain HTML Tags.!");
			}
			if(InputValidator.isContainsHTMLTags(timeReasonability)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","4");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Justification for time reasonability' should not contain HTML Tags.!");
			}
			if(InputValidator.isContainsHTMLTags(costReasonability)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","4");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Justification for cost reasonability' should not contain HTML Tags.!");
			}
			if(InputValidator.isContainsHTMLTags(rspSelection)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","4");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Justification for selection of RSP' should not contain HTML Tags.!");
			}
			if(InputValidator.isContainsHTMLTags(socCriterion)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","4");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Success / Acceptance Criterion' should not contain HTML Tags.!");
			}
			
			String rspOfferRef=req.getParameter("rspOfferRef");
			String kp1Details=req.getParameter("kp1Details");
			String kp2Details=req.getParameter("kp2Details");
			if(InputValidator.isContainsHTMLTags(rspOfferRef)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","4");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'RSP's Offer Ref' should not contain HTML Tags.!");
			}
			if(InputValidator.isContainsHTMLTags(kp1Details)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","4");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Key Professional-1 Details' should not contain HTML Tags.!");
			}
			if(InputValidator.isContainsHTMLTags(kp2Details)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","4");
				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Key Professional-2 Details' should not contain HTML Tags.!");
			}
			
			String[] consultantName=req.getParameterValues("consultantName");
			String[] consultantCompany=req.getParameterValues("consultantCompany");
			String[] equipmentDescription=req.getParameterValues("equipmentDescription");
			if (containsHTMLTags(consultantName)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","4");
			    return redirectWithError(redir, "CARSInitiationDetails.htm", "'Research Consultant -> Name(s)' should not contain HTML Tags.!");
			}
			if (containsHTMLTags(consultantCompany)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","4");
			    return redirectWithError(redir, "CARSInitiationDetails.htm", "'Research Consultant -> Institute / Company' should not contain HTML Tags.!");
			}
			if (containsHTMLTags(equipmentDescription)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","4");
			    return redirectWithError(redir, "CARSInitiationDetails.htm", "'DRDO-owned Equipment (Description)' should not contain HTML Tags.!");
			}
			
			CARSSoC soc = service.getCARSSoCById(Long.parseLong(carsSocId));
			soc.setSoCAmount(req.getParameter("socAmount"));
			soc.setSoCDuration(req.getParameter("socDuration"));
			soc.setAlignment(Alignment);
			soc.setTimeReasonability(timeReasonability);
			soc.setCostReasonability(costReasonability);
			soc.setRSPSelection(rspSelection);
			soc.setSoCCriterion(socCriterion);
			soc.setModifiedBy(Username);
			soc.setModifiedDate(sdtf.format(new Date()));
			
			long result = service.editCARSSoC(soc, SoOFile, FRFile, ExecutionPlanFile);
			
			String rspOfferDate = req.getParameter("rspOfferDate");
			CARSContract contract = service.getCARSContractByCARSInitiationId(carsiniid);
			contract.setRSPOfferRef(rspOfferRef);
			contract.setRSPOfferDate(rspOfferDate!=null?fc.RegularToSqlDate(rspOfferDate):null);
			contract.setKP1Details(kp1Details);
			contract.setKP2Details(kp2Details);
			contract.setExpndPersonnelCost(req.getParameter("expndPersonnelCost"));
			contract.setExpndEquipmentCost(req.getParameter("expndEquipmentCost"));
			contract.setExpndOthersCost(req.getParameter("expndOthersCost"));
			contract.setExpndGST(req.getParameter("expndGST"));
			contract.setExpndTotalCost(req.getParameter("expndTotalCost"));
			contract.setModifiedBy(Username);
			contract.setModifiedDate(sdtf.format(new Date()));
			
			long contractResult = service.editCARSContractDetails(contract);
			
			CARSContractDetailsDTO dto = new CARSContractDetailsDTO();
			dto.setCARSInitiationId(carsiniid);
			dto.setConsultantName(consultantName);
			dto.setConsultantCompany(consultantCompany);
			dto.setEquipmentDescription(equipmentDescription);
			dto.setUserId(Username);
			
			long consultantsResult = service.addCARSContractConsultantsDetails(dto);
			
			long equipmentResult = service.addCARSContractEquipmentDetails(dto); 
			 
			if(result!=0 && contractResult!=0 && consultantsResult!=0 && equipmentResult!=0) {
				redir.addAttribute("result", "CARS SoC Details Edit Successfully");
			}else {
				redir.addAttribute("resultfail", "CARS SoC Details Edit UnSuccessful");
			}
			
			redir.addAttribute("carsInitiationId", carsInitiationId);
			redir.addAttribute("carsSoCId",result);
			redir.addAttribute("TabId","4");
			return "redirect:/CARSInitiationDetails.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSSoCEdit.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value = {"CARSSoCFileDownload.htm"}, method = { RequestMethod.POST, RequestMethod.GET })
	public void carsSoCFileDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside CARSSoCFileDownload.htm "+UserId);
		try
		{
			String ftype=req.getParameter("filename");
			String carsSocId=req.getParameter("carsSocId");
			res.setContentType("Application/octet-stream");	
			CARSSoC soc = service.getCARSSoCById(Long.parseLong(carsSocId));
			Path carsPath = Paths.get(labLogoPath, "CARS", "SoC");
			File my_file=null;
			String file = ftype.equalsIgnoreCase("soofile")?soc.getSoOUpload():
				         (ftype.equalsIgnoreCase("frfile")?soc.getFRUpload(): 
				         (ftype.equalsIgnoreCase("momfile")?soc.getMoMUpload():soc.getExecutionPlan() ) );
			my_file = carsPath.resolve(file).toFile();
	        res.setHeader("Content-disposition","attachment; filename="+file); 
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
				logger.error(new Date() +"Inside CARSSoCFileDownload.htm "+UserId,e);
		}
	}
	
	@RequestMapping(value="CARSRSQRDownload.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public void carsRSQRDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside CARSRSQRDownload.htm "+UserId);		
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			
			Object[] carsRSQRDetails = service.carsRSQRDetails(carsInitiationId);
			String sqr = carsRSQRDetails[10].toString();
			String replacedSqr = sqr.replaceAll("[/\\\\]", ",");
			String[] result = replacedSqr.split(",");
			Path carsPath = Paths.get(labLogoPath, result[0].toString(), result[1].toString(), result[2].toString());
			
			if(carsRSQRDetails[10]==null || carsRSQRDetails[10].toString().isEmpty()) {
				service.carsRSQRFormFreeze(req, res, Long.parseLong(carsInitiationId));
			}
			
			res.setContentType("application/pdf");
			res.setHeader("Content-disposition", "inline;filename= RSQR.pdf");
			File f = carsPath.toFile();
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

		}
	    catch(Exception e) {	    		
    		logger.error(new Date() +" Inside CARSRSQRDownload.htm "+UserId, e);
    		e.printStackTrace();
    	}		
	}
	
	@RequestMapping(value="CARSFinalRSQRDownload.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public void carsFinalRSQRDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside CARSFinalRSQRDownload.htm "+UserId);		
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			if(carsInitiationId!=null && carsInitiationId!="0") {
				long carsiniid = Long.parseLong(carsInitiationId);
				req.setAttribute("CARSInitiationData", service.getCARSInitiationById(carsiniid));
				req.setAttribute("RSQRMajorReqr", service.getCARSRSQRMajorReqrByCARSInitiationId(carsiniid));
				req.setAttribute("RSQRDeliverables", service.getCARSRSQRDeliverablesByCARSInitiationId(carsiniid));
				req.setAttribute("RSQRDetails", service.carsRSQRDetails(carsInitiationId));
				req.setAttribute("CARSSoCMilestones", service.getCARSSoCMilestonesByCARSInitiationId(carsiniid));
				req.setAttribute("CARSSoCData", service.getCARSSoCByCARSInitiationId(carsiniid));
			}
			String filename="Final-RSQR";	
			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/print/CARSRSQRDownload.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"));
			PdfWriter pdfw=new PdfWriter(path +File.separator+ "merged.pdf");
			PdfReader pdf1=new PdfReader(path+File.separator+filename+".pdf");
			PdfDocument pdfDocument = new PdfDocument(pdf1, pdfw);	
			pdfDocument.close();
			pdf1.close();	       
			pdfw.close();

			res.setContentType("application/pdf");
			res.setHeader("Content-disposition","inline;filename="+filename+".pdf"); 
			File f=new File(path+"/"+filename+".pdf");

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

			Path pathOfFile2= Paths.get( path+File.separator+filename+".pdf"); 
			Files.delete(pathOfFile2);		

		}
	    catch(Exception e) {	    		
    		logger.error(new Date() +" Inside CARSFinalRSQRDownload.htm "+UserId, e);
    		e.printStackTrace();
    	}		
	}
	
	@RequestMapping(value="SoCApprovalSubmit.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String socApprovalSubmit(HttpServletRequest req, HttpSession ses, HttpServletResponse res, RedirectAttributes redir) throws Exception{
		String UserId=(String)ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() +"Inside SoCApprovalSubmit.htm "+UserId);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			String action = req.getParameter("Action");
			String remarks = req.getParameter("remarks");
			
			if (InputValidator.isContainsHTMLTags(remarks)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId","7");
			    return redirectWithError(redir, "CARSRSQRApprovals.htm", "'Remarks' should not contain HTML Tags.!");
			}
			long carsini = Long.parseLong(carsInitiationId);
			
			CARSInitiation cars = service.getCARSInitiationById(carsini);
			String statusCode = cars.getCARSStatusCode();
			
			CARSApprovalForwardDTO dto = new CARSApprovalForwardDTO();
			dto.setCarsinitiationid(carsini);
			dto.setAction(action);
			dto.setEmpId(EmpId);
			dto.setRemarks(remarks);
			dto.setLabcode(labcode);
			long result = service.socApprovalForward(dto);
			
			if(action.equalsIgnoreCase("A")) {
				if(statusCode.equalsIgnoreCase("AGD") || statusCode.equalsIgnoreCase("APD") || statusCode.equalsIgnoreCase("SRV") || 
						   statusCode.equalsIgnoreCase("SRG") || statusCode.equalsIgnoreCase("SRP")) {
					if(result!=0) {
						redir.addAttribute("result","SoC form forwarded Successfully");
					}else {
						redir.addAttribute("resultfail","SoC form forward Unsuccessful");
					}
					return "redirect:/CARSInitiationList.htm";
				}else if(statusCode.equalsIgnoreCase("SFU")) {
					if(result!=0) {
						redir.addAttribute("result","SoC form Recommended Successfully");
					}else {
						redir.addAttribute("resultfail","SoC form Recommend Unsuccessful");
					}
					return "redirect:/CARSRSQRApprovals.htm";
				}
			}
			else if(action.equalsIgnoreCase("R") || action.equalsIgnoreCase("D")) {
				if(result!=0) {
					redir.addAttribute("result",action.equalsIgnoreCase("R")?"SoC form Returned Successfully":"SoC form Disapproved Successfully");
				}else {
					redir.addAttribute("resultfail",action.equalsIgnoreCase("R")?"SoC form Return Unsuccessful":"SoC form Disapprove Unsuccessful");
				}
			}
			return "redirect:/CARSRSQRApprovals.htm";
			
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside SoCApprovalSubmit.htm "+UserId,e);
			return "static/Error";
		}
	}

	@RequestMapping(value = "CARSRSQRTransStatus.htm" , method={RequestMethod.POST,RequestMethod.GET})
	public String carsRSQRTransStatus(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception
	{
		String Username = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside carsRSQRTransStatus.htm "+Username);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			req.setAttribute("TransactionList", service.carsTransListByType(carsInitiationId,"R")) ;
			req.setAttribute("TransFlag", "R");
			req.setAttribute("carsInitiationId", carsInitiationId);
			return "cars/CARSTransactionStatus";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside carsRSQRTransStatus.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value = "CARSSoCTransStatus.htm" , method={RequestMethod.POST,RequestMethod.GET})
	public String carsSoCTransStatus(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception
	{
		String Username = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside CARSSoCTransStatus.htm "+Username);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			req.setAttribute("TransactionList", service.carsTransListByType(carsInitiationId,"S")) ;
			req.setAttribute("TransFlag", "S");
			req.setAttribute("carsInitiationId", carsInitiationId);
			return "cars/CARSTransactionStatus";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSSoCTransStatus.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="CARSSoCDownload.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public void carsSoCDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside CARSSoCDownload.htm "+UserId);		
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			if(carsInitiationId!=null && carsInitiationId!="0") {
				long carsiniid = Long.parseLong(carsInitiationId);
				CARSInitiation carsInitiation = service.getCARSInitiationById(carsiniid);
				req.setAttribute("CARSInitiationData", carsInitiation);
				req.setAttribute("RSQRDetails", service.carsRSQRDetails(carsInitiationId));
				req.setAttribute("EmpData", service.getEmpDetailsByEmpId(carsInitiation.getEmpId()+""));
				req.setAttribute("SoCApprovalEmpData", service.carsTransApprovalData(carsInitiationId, "SF"));
				req.setAttribute("CARSSoCData", service.getCARSSoCByCARSInitiationId(carsiniid));
				req.setAttribute("CARSSoCRemarksHistory", service.carsRemarksHistoryByType(carsInitiationId,"SF"));
				
				req.setAttribute("PDEmpIds", service.getEmpPDEmpId(carsInitiation.getFundsFrom()));
			}
			String filename="CARS-SoC";	
			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/print/CARSSoCDownload.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"));
			PdfWriter pdfw=new PdfWriter(path +File.separator+ "merged.pdf");
			PdfReader pdf1=new PdfReader(path+File.separator+filename+".pdf");
			PdfDocument pdfDocument = new PdfDocument(pdf1, pdfw);	
			pdfDocument.close();
			pdf1.close();	       
			pdfw.close();

			res.setContentType("application/pdf");
			res.setHeader("Content-disposition","inline;filename="+filename+".pdf"); 
			File f=new File(path+"/"+filename+".pdf");

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

			Path pathOfFile2= Paths.get( path+File.separator+filename+".pdf"); 
			Files.delete(pathOfFile2);		

		}
	    catch(Exception e) {	    		
    		logger.error(new Date() +" Inside CARSSoCDownload.htm "+UserId, e);
    		e.printStackTrace();
    	}		
	}
	
	@RequestMapping(value="CARSUserRevoke.htm", method= {RequestMethod.POST,RequestMethod.GET})
	public String carsUserRevoke(HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() +"Inside CARSUserRevoke.htm "+UserId);
		try {
			String carsUserRevoke = req.getParameter("carsUserRevoke");
			String[] split = carsUserRevoke.split("/");
			String carsInitiationId = split[0];
			String carsStatusCode = split[1];
            String status = carsStatusCode.equalsIgnoreCase("FWD")?"RSQR":"SoC";
            
			long count = service.carsUserRevoke(carsInitiationId, UserId, EmpId,carsStatusCode, labcode);
			
			if (count > 0) {
				redir.addAttribute("result", "CARS "+status+" Revoked Successfully");
			}
			else {
				redir.addAttribute("resultfail", "CARS "+status+" Revoke Unsuccessful");	
			}	

			return "redirect:/CARSInitiationList.htm";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSUserRevoke.htm "+UserId, e);
			return "static/Error";			
		}

	}
	
	@RequestMapping(value="CARSSoCMilestoneSubmit.htm", method= {RequestMethod.POST,RequestMethod.GET})
	public String carsSoCMitlestoneSubmit(HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside CARSSoCMilestoneSubmit.htm "+UserId);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			String attributes=req.getParameter("attributes");
			String tab=req.getParameter("tab");
			long carsiniid = Long.parseLong(carsInitiationId);
			
			String[] taskDescription=req.getParameterValues("taskDesc");
			String[] deliverables=req.getParameterValues("deliverables");
			String[] Months=req.getParameterValues("months");
			String[] remarks = req.getParameterValues("paymentTerms");
			
			
//			if (containsHTMLTags(taskDescription)) {
//				redir.addAttribute("carsInitiationId", carsInitiationId);
//				redir.addAttribute("attributes", attributes);
//				redir.addAttribute("TabId",tab!=null?tab:"2");
//			    return redirectWithError(redir, "CARSInitiationDetails.htm", "'Task Description' should not contain HTML Tags.!");
//			}
//			if (containsHTMLTags(remarks)) {
//				redir.addAttribute("carsInitiationId", carsInitiationId);
//				redir.addAttribute("attributes", attributes);
//				redir.addAttribute("TabId",tab!=null?tab:"2");
//				return redirectWithError(redir, "CARSInitiationDetails.htm", "'Remarks' should not contain HTML Tags.!");
//			}
//			if (containsHTMLTags(deliverables)) {
//				redir.addAttribute("carsInitiationId", carsInitiationId);
//				redir.addAttribute("attributes", attributes);
//				redir.addAttribute("TabId",tab!=null?tab:"2");
//			    return redirectWithError(redir, "CARSInitiationDetails.htm", "'Deliverables' should not contain HTML Tags.!");
//			}
//			if (containsHTMLTags(Months)) {
//				redir.addAttribute("carsInitiationId", carsInitiationId);
//				redir.addAttribute("attributes", attributes);
//				redir.addAttribute("TabId",tab!=null?tab:"2");
//			    return redirectWithError(redir, "CARSInitiationDetails.htm", "'Months' should not contain HTML Tags.!");
//			}
			

			CARSRSQRDetailsDTO dto = new CARSRSQRDetailsDTO();
			dto.setCARSInitiationId(carsiniid);
			dto.setMilestoneNo(req.getParameterValues("milestoneno"));
			dto.setTaskDesc(taskDescription);
			dto.setMonths(Months);
			dto.setDeliverables(deliverables);
			dto.setPaymentPercentage(req.getParameterValues("paymentPercentage"));
			dto.setPaymentTerms(req.getParameterValues("paymentTerms"));
			dto.setActualAmount(req.getParameterValues("actualAmount"));
			dto.setUserId(UserId);
			
			List<CARSSoCMilestones> milestones = service.getCARSSoCMilestonesByCARSInitiationId(carsiniid);
			long result = 0l;
			if(milestones!=null && milestones.size()==0) {
				result = service.carsSoCMilestonesDetailsSubmit(dto);
			}else {
				service.removeCARSSoCMilestonesDetails(carsiniid);
				result = service.carsSoCMilestonesDetailsSubmit(dto);
				if (result > 0) {
					redir.addAttribute("result", "SoC Milestone Details updated Successfully");
				} else {
					redir.addAttribute("resultfail", "SoC Milestone Details update Unsuccessful");
				}
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("TabId",tab!=null?tab:"2");
				redir.addAttribute("attributes", attributes);
				return "redirect:/CARSInitiationDetails.htm";
			}
			if (result > 0) {
				redir.addAttribute("result", "SoC Milestone Details added Successfully");
			} else {
				redir.addAttribute("resultfail", "SoC Milestone Details add Unsuccessful");
			}
			redir.addAttribute("carsInitiationId", carsInitiationId);
			redir.addAttribute("TabId",tab!=null?tab:"2");
			redir.addAttribute("attributes", attributes);
			return "redirect:/CARSInitiationDetails.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSSoCMilestoneSubmit.htm "+UserId, e);
			return "static/Error";			
		}
	}
	
	@RequestMapping(value="CARSSoCMilestonesDownload.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public void carsSoCMilestonesDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside CARSSoCMilestonesDownload.htm "+UserId);		
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			if(carsInitiationId!=null && carsInitiationId!="0") {
				long carsiniid = Long.parseLong(carsInitiationId);
				CARSInitiation carsInitiation = service.getCARSInitiationById(carsiniid);
				req.setAttribute("CARSInitiationData", carsInitiation);
				req.setAttribute("EmpData", service.getEmpDetailsByEmpId(carsInitiation.getEmpId()+""));
				req.setAttribute("CARSSoCMilestones", service.getCARSSoCMilestonesByCARSInitiationId(carsiniid));
				req.setAttribute("CARSSoCData", service.getCARSSoCByCARSInitiationId(carsiniid));
			}
			String filename="CARS-Milestones";	
			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/print/CARSSoCMilestonesDownload.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"));
			PdfWriter pdfw=new PdfWriter(path +File.separator+ "merged.pdf");
			PdfReader pdf1=new PdfReader(path+File.separator+filename+".pdf");
			PdfDocument pdfDocument = new PdfDocument(pdf1, pdfw);	
			pdfDocument.close();
			pdf1.close();	       
			pdfw.close();

			res.setContentType("application/pdf");
			res.setHeader("Content-disposition","inline;filename="+filename+".pdf"); 
			File f=new File(path+"/"+filename+".pdf");

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

			Path pathOfFile2= Paths.get( path+File.separator+filename+".pdf"); 
			Files.delete(pathOfFile2);		

		}
	    catch(Exception e) {	    		
    		logger.error(new Date() +" Inside CARSSoCMilestonesDownload.htm "+UserId, e);
    		e.printStackTrace();
    	}		
	}

	@RequestMapping(value="CARSRSQRApprovedList.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String carsRSQRApprovedList(HttpServletRequest req,HttpSession ses) throws Exception{
		String Username = (String)ses.getAttribute("Username");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() +"Inside CARSRSQRApprovedList.htm "+Username);
		try {
			String AllListTabId = req.getParameter("AllListTabId");

			LocalDate today= LocalDate.now();
			String fromdate = today.getYear()+"-01-01";
			String todate = today.getYear()+"-12-31";
			
			if((AllListTabId==null) || (AllListTabId!=null && AllListTabId.equalsIgnoreCase("1")) ) {
				String rsqrfromdate = req.getParameter("rsqrfromdate");
				String rsqrtodate = req.getParameter("rsqrtodate");

				if(rsqrfromdate == null) 
				{
					rsqrfromdate = today.getYear()+"-01-01";
					rsqrtodate = today.getYear()+"-12-31";
//					rsqrfromdate = today.withDayOfMonth(1).toString();
//					rsqrtodate = today.toString();

				}else
				{
					rsqrfromdate = fc.RegularToSqlDate(rsqrfromdate);
					rsqrtodate = fc.RegularToSqlDate(rsqrtodate);
				}

				req.setAttribute("rsqrfromdate", rsqrfromdate);
				req.setAttribute("rsqrtodate", rsqrtodate);
				req.setAttribute("ApprovedList", service.carsRSQRApprovedList(EmpId,rsqrfromdate,rsqrtodate,"A"));
				
				req.setAttribute("socfromdate", fromdate);
				req.setAttribute("soctodate", todate);
				req.setAttribute("MoMUploadedList", service.carsSoCMoMUploadedList(fromdate,todate));
				
				req.setAttribute("carsfromdate", fromdate);
				req.setAttribute("carstodate", todate);
				req.setAttribute("CARSFinalApprovedList", service.carsDPCSoCFinalApprovedList(fromdate,todate));
				
				req.setAttribute("AllListTabId", AllListTabId!=null?AllListTabId:"1");
			}
			if(AllListTabId!=null && AllListTabId.equalsIgnoreCase("2")) {
				String socfromdate = req.getParameter("socfromdate");
				String soctodate = req.getParameter("soctodate");

				if(socfromdate == null) 
				{
					socfromdate = today.getYear()+"-01-01";
					soctodate = today.getYear()+"-12-31";

				}else
				{
					socfromdate = fc.RegularToSqlDate(socfromdate);
					soctodate = fc.RegularToSqlDate(soctodate);
				}

				req.setAttribute("rsqrfromdate", fromdate);
				req.setAttribute("rsqrtodate", todate);
				req.setAttribute("ApprovedList", service.carsRSQRApprovedList(EmpId,fromdate,todate,"A"));
				
				req.setAttribute("socfromdate", socfromdate);
				req.setAttribute("soctodate", soctodate);
				req.setAttribute("MoMUploadedList", service.carsSoCMoMUploadedList(socfromdate,soctodate));
				
				req.setAttribute("carsfromdate", fromdate);
				req.setAttribute("carstodate", todate);
				req.setAttribute("CARSFinalApprovedList", service.carsDPCSoCFinalApprovedList(fromdate,todate));

				req.setAttribute("AllListTabId", AllListTabId!=null?AllListTabId:"2");

			}

			if(AllListTabId!=null && AllListTabId.equalsIgnoreCase("3")) {
				String carsfromdate = req.getParameter("carsfromdate");
				String carstodate = req.getParameter("carstodate");

				if(carsfromdate == null) 
				{
					carsfromdate = today.getYear()+"-01-01";
					carstodate = today.getYear()+"-12-31";

				}else
				{
					carsfromdate = fc.RegularToSqlDate(carsfromdate);
					carstodate = fc.RegularToSqlDate(carstodate);
				}

				req.setAttribute("rsqrfromdate", fromdate);
				req.setAttribute("rsqrtodate", todate);
				req.setAttribute("ApprovedList", service.carsRSQRApprovedList(EmpId,fromdate,todate,"A"));
				
				req.setAttribute("socfromdate", fromdate);
				req.setAttribute("soctodate", todate);
				req.setAttribute("MoMUploadedList", service.carsSoCMoMUploadedList(fromdate,todate));
				
				req.setAttribute("carsfromdate", carsfromdate);
				req.setAttribute("carstodate", carstodate);
				req.setAttribute("CARSFinalApprovedList", service.carsDPCSoCFinalApprovedList(carsfromdate,carstodate));
				
				req.setAttribute("AllListTabId", AllListTabId!=null?AllListTabId:"3");
			}

			
			return "cars/AllCARSList";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSRSQRApprovedList.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="InvForSoODateSubmit.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String invForSoODateSubmit(HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception{
		String Username = (String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside InvForSoODateSubmit.htm "+Username);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			String calendardate = req.getParameter("calendardate");
			int result = service.invForSoODateSubmit(carsInitiationId, fc.RegularToSqlDate(calendardate));
			if(result!=0) {
				redir.addAttribute("result","Inv for SoO Date Submitted Successfully");
			}else {
				redir.addAttribute("resultfail","Inv for SoO Date Submit Unsuccessful");
			}
			return "redirect:/CARSRSQRApprovedList.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside InvForSoODateSubmit.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="CARSDPCSoCDetails.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String carsDPCSoCDetails(HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception{
		String Username = (String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CARSDPCSoCDetails.htm "+Username);
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		try {
			
			String carsInitiationId = req.getParameter("carsInitiationId");
			String dpcTabId = req.getParameter("dpcTabId");
			String isApproval = req.getParameter("isApproval");
			if(carsInitiationId==null) {
				String fromapprovals = req.getParameter("carsInitiationIdSoCApprovals");
				if(fromapprovals!=null) {
					String[] split = fromapprovals.split("/");
					carsInitiationId = split[0];
					if(isApproval==null) {
						isApproval = split[1];
					}
					if(dpcTabId==null) {
						dpcTabId = split[2];
					}
				}
				
			}
			if(carsInitiationId!=null && carsInitiationId!="0") {
				long carsiniid = Long.parseLong(carsInitiationId);
				CARSInitiation carsInitiation = service.getCARSInitiationById(carsiniid);
				req.setAttribute("CARSInitiationData", carsInitiation);
				req.setAttribute("CARSSoCData", service.getCARSSoCByCARSInitiationId(carsiniid));
				req.setAttribute("CARSSoCMilestones", service.getCARSSoCMilestonesByCARSInitiationId(carsiniid));
				req.setAttribute("CARSDPCSoCRemarksHistory", service.carsRemarksHistoryByType(carsInitiationId,"DF"));
				req.setAttribute("DPCSoCApprovalEmpData", service.carsTransApprovalData(carsInitiationId, "DF"));
				req.setAttribute("PDEmpIds", service.getEmpPDEmpId(carsInitiation.getFundsFrom()));
				
				List<String> dpcsocexternalapprovestatus = Arrays.asList("SAI","ADG","SAJ","SAS");
				if(dpcsocexternalapprovestatus.contains(carsInitiation.getCARSStatusCodeNext())) {
					req.setAttribute("LabList", service.getLabList(labcode));
				}
			}
			req.setAttribute("EmpData", service.getEmpDetailsByEmpId(EmpId));
			req.setAttribute("dpcTabId", dpcTabId!=null?dpcTabId:"1");
			
			req.setAttribute("GHDPandC", service.getApprAuthorityDataByType(labcode, "GH-DP&C"));
			req.setAttribute("GDDPandC", service.getApprAuthorityDataByType(labcode, "DO-RTMD"));
			req.setAttribute("ChairmanRPB", service.getApprAuthorityDataByType(labcode, "Chairman RPB"));
			req.setAttribute("MMFDAG", service.getApprAuthorityDataByType(labcode, "MMFD AG"));
			req.setAttribute("GDDFandMM", service.getApprAuthorityDataByType(labcode, "GD DF&MM"));
			req.setAttribute("Director", service.getLabDirectorData(labcode));
			
			req.setAttribute("isApproval", isApproval);
			return "cars/DPCSoCDetails";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSDPCSoCDetails.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="CARSDPCSoCEdit.htm", method= {RequestMethod.POST,RequestMethod.GET})
	public String carsDPCSoCEditSubmit(HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception{
		String Username = (String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CARSDPCSoCEdit.htm "+Username);
		try{
			
			String carsInitiationId = req.getParameter("carsInitiationId");
			String carsSocId = req.getParameter("carsSocId");
			String Action = req.getParameter("Action");
			String dpcExpenditure=req.getParameter("dpcExpenditure");
			String dpcAdditinal=req.getParameter("dpcAdditional");
			
			if(InputValidator.isContainsHTMLTags(dpcExpenditure)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				return redirectWithError(redir, "CARSDPCSoCDetails.htm", "'Expenditure Head' should not contain HTML Tags.!");
			}
			if(InputValidator.isContainsHTMLTags(dpcAdditinal)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				return redirectWithError(redir, "CARSDPCSoCDetails.htm", "'Additional Points' should not contain HTML Tags.!");
			}
			CARSSoC soc = service.getCARSSoCById(Long.parseLong(carsSocId));
			soc.setDPCIntroduction(req.getParameter("dpcIntroduction"));
			soc.setDPCExpenditure(dpcExpenditure);
			soc.setDPCAdditional(dpcAdditinal);
			soc.setModifiedBy(Username);
			soc.setModifiedDate(sdtf.format(new Date()));
			
			long result = service.editCARSSoC(soc);
			if(result!=0) {
				redir.addAttribute("result", "CARS D-P&C SoC Details "+Action+" Successfully");
			}else {
				redir.addAttribute("resultfail", "CARS D-P&C SoC Details "+Action+" UnSuccessful");
			}
			
			redir.addAttribute("carsInitiationId", carsInitiationId);
			redir.addAttribute("carsSoCId",result);
			redir.addAttribute("dpcTabId","1");
			
			return "redirect:/CARSDPCSoCDetails.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSDPCSoCEdit.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="DPCSoCApprovalSubmit.htm", method= {RequestMethod.POST,RequestMethod.GET})
	public String dpcSoCApprovalSubmit(HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception{
		String Username = (String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() +"Inside DPCSoCApprovalSubmit.htm "+Username);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			String action = req.getParameter("Action");
			
			
			long carsini = Long.parseLong(carsInitiationId);
			
			CARSInitiation cars = service.getCARSInitiationById(carsini);
			String statusCode = cars.getCARSStatusCode();
			
			String Remarks=req.getParameter("remarks");
			if(InputValidator.isContainsHTMLTags(Remarks)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				return redirectWithError(redir, "CARSRSQRApprovals.htm", "'Remarks' should not contain HTML Tags.!");
			}
			
			CARSApprovalForwardDTO dto = new CARSApprovalForwardDTO();
			dto.setCarsinitiationid(carsini);
			dto.setAction(action);
			dto.setEmpId(EmpId);
			dto.setRemarks(Remarks);
			dto.setLabcode(labcode);
			dto.setApprovalSought(req.getParameter("approvalSought"));
			dto.setApproverLabCode(req.getParameter("LabCode"));
			dto.setApproverEmpId(req.getParameter("approverEmpId"));
			dto.setApprovalDate(req.getParameter("approvalDate"));
			long result = service.dpcSoCApprovalForward(dto);
			
			List<String> forwardstatus = Arrays.asList("SFG","SFP","SID","SGR","SPR","SRC","SRM","SRF","SRR","SRI","RDG","SRJ","SRS","SRD");
			List<String> approvestatus = Arrays.asList("SDF","SAD","SAI","ADG","SAJ","SAS");

			if(action.equalsIgnoreCase("A")) {
				if(forwardstatus.contains(statusCode)) {
					if(result!=0) {
						redir.addAttribute("result","SoC form forwarded Successfully");
					}else {
						redir.addAttribute("resultfail","SoC form forward Unsuccessful");
					}
				}else if(approvestatus.contains(statusCode)) {
					if(result!=0) {
						redir.addAttribute("result","SoC form Approved Successfully");
					}else {
						redir.addAttribute("resultfail","SoC form Approve Unsuccessful");
					}
					return "redirect:/CARSRSQRApprovals.htm";
				}else {
					if(result!=0) {
						redir.addAttribute("result","SoC form Recommended Successfully");
					}else {
						redir.addAttribute("resultfail","SoC form Recommend Unsuccessful");
					}
					return "redirect:/CARSRSQRApprovals.htm";
				}
			}
			else if(action.equalsIgnoreCase("R") || action.equalsIgnoreCase("D")) {
				if(result!=0) {
					redir.addAttribute("result",action.equalsIgnoreCase("R")?"SoC form Returned Successfully":"SoC form Disapproved Successfully");
				}else {
					redir.addAttribute("resultfail",action.equalsIgnoreCase("R")?"SoC form Return Unsuccessful":"SoC form Disapprove Unsuccessful");
				}
			}
			
			redir.addAttribute("AllListTabId", "2");
			return "redirect:/CARSRSQRApprovedList.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside DPCSoCApprovalSubmit.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="CARSTransactionDownload.htm")
	public void carsTransactionDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside CARSTransactionDownload.htm "+UserId);		
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			String TransFlag = req.getParameter("TransFlag");
			
			if(TransFlag!=null && TransFlag.equalsIgnoreCase("R")) {
				req.setAttribute("TransactionList", service.carsTransListByType(carsInitiationId, TransFlag)) ;
			}else if(TransFlag!=null && TransFlag.equalsIgnoreCase("S")) {
				req.setAttribute("TransactionList", service.carsTransListByType(carsInitiationId, TransFlag)) ;
			}else if(TransFlag!=null && TransFlag.equalsIgnoreCase("D")){
				req.setAttribute("TransactionList", service.carsTransListByType(carsInitiationId, TransFlag)) ;
			}else {
				req.setAttribute("TransactionList", service.carsTransListByType(carsInitiationId, TransFlag)) ;
			}
			req.setAttribute("CARSInitiationData", service.getCARSInitiationById(Long.parseLong(carsInitiationId)));
			
			String filename="CARS-Transaction";	
			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/print/CARSTransactionDownload.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"));
			PdfWriter pdfw=new PdfWriter(path +File.separator+ "merged.pdf");
			PdfReader pdf1=new PdfReader(path+File.separator+filename+".pdf");
			PdfDocument pdfDocument = new PdfDocument(pdf1, pdfw);	
			pdfDocument.close();
			pdf1.close();	       
			pdfw.close();

			res.setContentType("application/pdf");
			res.setHeader("Content-disposition","inline;filename="+filename+".pdf"); 
			File f=new File(path+"/"+filename+".pdf");

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

			Path pathOfFile2= Paths.get( path+File.separator+filename+".pdf"); 
			Files.delete(pathOfFile2);		

		}
	    catch(Exception e) {	    		
    		logger.error(new Date() +" Inside CARSTransactionDownload.htm "+UserId, e);
    		e.printStackTrace();
    	}		
	}
	
	@RequestMapping(value="CARSDPCSoCDownload.htm")
	public void carsDPCSoCDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside CARSTransactionDownload.htm "+UserId);		
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");

			if(carsInitiationId!=null && carsInitiationId!="0") {
				long carsiniid = Long.parseLong(carsInitiationId);
				CARSInitiation carsInitiation = service.getCARSInitiationById(carsiniid);
				req.setAttribute("CARSInitiationData", carsInitiation);
				req.setAttribute("CARSSoCData", service.getCARSSoCByCARSInitiationId(carsiniid));
				req.setAttribute("CARSSoCMilestones", service.getCARSSoCMilestonesByCARSInitiationId(carsiniid));
				req.setAttribute("DPCSoCApprovalEmpData", service.carsTransApprovalData(carsInitiationId, "DF"));
			}
			
			String filename="CARS-DPCSoC";	
			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/print/CARSDPCSoCDownload.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"));
			PdfWriter pdfw=new PdfWriter(path +File.separator+ "merged.pdf");
			PdfReader pdf1=new PdfReader(path+File.separator+filename+".pdf");
			PdfDocument pdfDocument = new PdfDocument(pdf1, pdfw);	
			pdfDocument.close();
			pdf1.close();	       
			pdfw.close();

			res.setContentType("application/pdf");
			res.setHeader("Content-disposition","inline;filename="+filename+".pdf"); 
			File f=new File(path+"/"+filename+".pdf");

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

			Path pathOfFile2= Paths.get( path+File.separator+filename+".pdf"); 
			Files.delete(pathOfFile2);		

		}
	    catch(Exception e) {	    		
    		logger.error(new Date() +" Inside CARSDPCSoCDownload.htm "+UserId, e);
    		e.printStackTrace();
    	}		
	}

	@RequestMapping(value="CARSSoCDPCRevoke.htm", method= {RequestMethod.POST,RequestMethod.GET})
	public String carsSoCDPCRevoke(HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() +"Inside CARSSoCDPCRevoke.htm "+UserId);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
		
            
			long count = service.carsSoCDPCRevoke(carsInitiationId, UserId, EmpId, labcode);
			
			if (count > 0) {
				redir.addAttribute("result", "CARS SoC Revoked Successfully");
			}
			else {
				redir.addAttribute("resultfail", "CARS SoC Revoke Unsuccessful");	
			}	

			redir.addAttribute("AllListTabId", "2");
			return "redirect:/CARSRSQRApprovedList.htm";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSSoCDPCRevoke.htm "+UserId, e);
			return "static/Error";			
		}

	}
	
	@RequestMapping(value = "CARSDPCSoCTransStatus.htm" , method={RequestMethod.POST,RequestMethod.GET})
	public String carsDPCSoCTransStatus(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception
	{
		String Username = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside CARSDPCSoCTransStatus.htm "+Username);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			req.setAttribute("TransactionList", service.carsTransListByType(carsInitiationId, "D")) ;
			req.setAttribute("TransFlag", "D");
			req.setAttribute("carsInitiationId", carsInitiationId);
			return "cars/CARSTransactionStatus";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSDPCSoCTransStatus.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="CARSSoCMoMUpload.htm")
	public String carsSocMoMUpload(HttpServletRequest req,HttpSession ses,RedirectAttributes redir,
			@RequestPart(name="MoMUpload", required = false) MultipartFile MoMFile) throws Exception{
		String Username = (String)ses.getAttribute("Username");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CARSSoCMoMUpload.htm "+Username);
		try {
			
			String carsInitiationId = req.getParameter("carsInitiationId");
			String carsSocId = req.getParameter("carsSocId");
			
			
			// 🔹 Validate file types
	        if (!isValidFileType(MoMFile)) {

	        	redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("carsSoCId",carsSocId);
				redir.addAttribute("TabId","8");
	        	return redirectWithError(redir, "CARSInitiationDetails.htm",
	                    "Invalid file type. Only PDF  files are allowed.");
	        }
			
			
			
			String MoMFlag = req.getParameter("MoMFlag");
		
			long result = service.carsSoCUploadMoM(MoMFile, labcode, carsInitiationId, EmpId, Username, MoMFlag);
			
			if(result!=0) {
				redir.addAttribute("result", "MoM Uploaded Successfully");
			}else {
				redir.addAttribute("resultfail", "MoM Upload UnSuccessful");
			}
			
			redir.addAttribute("carsInitiationId", carsInitiationId);
			redir.addAttribute("carsSoCId",carsSocId);
			redir.addAttribute("TabId","8");
			return "redirect:/CARSInitiationDetails.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSSoCMoMUpload.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="GetLabcodeEmpList.htm",method=RequestMethod.GET)
	public  @ResponseBody String  GetLabcodeEmpList(HttpServletRequest req, HttpSession ses)throws Exception{
		String Username=(String)ses.getAttribute("Username");
		logger.info(new Date() + "Inside GetLabcodeEmpList.htm"+Username);
		Gson json = new Gson();
		List<Object[]> GetLabcodeEmpList=null;
		try {
			String LabCode=(String)req.getParameter("LabCode");
			if(LabCode!=null && !LabCode.equalsIgnoreCase("@EXP")) {
				GetLabcodeEmpList=service.getEmployeeListByLabCode(LabCode);
			}else {
				GetLabcodeEmpList=service.ExpertEmployeeList();
			}
			
			} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside GetLabcodeEmpList.htm "+Username, e);
			}
		return json.toJson(GetLabcodeEmpList);

	}
	
	@RequestMapping(value = "SoOProFormWordDownload.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String ProFormaWordDownload(HttpServletRequest req, HttpServletResponse res, HttpSession ses,RedirectAttributes redir) throws Exception {
		String Username = (String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() + " Inside SoOProFormWordDownload.htm "+Username);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			if(carsInitiationId!=null) {
				long carsiniid = Long.parseLong(carsInitiationId);
				CARSInitiation carsInitiation = service.getCARSInitiationById(carsiniid);
				req.setAttribute("CARSInitiationData", carsInitiation);
				req.setAttribute("RSQRDetails", service.carsRSQRDetails(carsInitiationId));
			}
			req.setAttribute("LabMasterData", service.getLabDetailsByLabCode(labcode));
			return "cars/SoOProFormWordDownload";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside ProFormWordDownload.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="CARSDPCFinalReportDetails.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String carsDPCFinalReportDetails(HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception{
		String Username = (String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CARSDPCFinalReportDetails.htm "+Username);
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		try {
			
			String carsInitiationId = req.getParameter("carsInitiationId");
			
			if(carsInitiationId!=null && carsInitiationId!="0") {
				long carsiniid = Long.parseLong(carsInitiationId);
				CARSInitiation carsInitiation = service.getCARSInitiationById(carsiniid);
				req.setAttribute("CARSInitiationData", carsInitiation);
				req.setAttribute("CARSSoCData", service.getCARSSoCByCARSInitiationId(carsiniid));
				req.setAttribute("CARSSoCMilestones", service.getCARSSoCMilestonesByCARSInitiationId(carsiniid));
				req.setAttribute("CARSContractData", service.getCARSContractByCARSInitiationId(carsiniid));				
			}
			req.setAttribute("EmpData", service.getEmpDetailsByEmpId(EmpId));
			
			return "cars/DPCFinalReportDetails";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSDPCFinalReportDetails.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value = "CARSFinalFormWordDownload.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String carsFinalFormWordDownload(HttpServletRequest req, HttpServletResponse res, HttpSession ses,RedirectAttributes redir) throws Exception {
		String Username = (String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() + " Inside CARSFinalFormWordDownload.htm "+Username);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			if(carsInitiationId!=null) {
				long carsiniid = Long.parseLong(carsInitiationId);
				CARSInitiation carsInitiation = service.getCARSInitiationById(carsiniid);
				req.setAttribute("CARSInitiationData", carsInitiation);
				req.setAttribute("RSQRDetails", service.carsRSQRDetails(carsInitiationId));
				req.setAttribute("CARSSoCData", service.getCARSSoCByCARSInitiationId(carsiniid));
				req.setAttribute("CARSSoCMilestones", service.getCARSSoCMilestonesByCARSInitiationId(carsiniid));
				req.setAttribute("CARSContractData", service.getCARSContractByCARSInitiationId(carsiniid));
				req.setAttribute("CARSContractConsultants", service.getCARSContractConsultantsByCARSInitiationId(carsiniid));
				req.setAttribute("CARSContractEquipment", service.getCARSContractEquipmentByCARSInitiationId(carsiniid));
			}
			req.setAttribute("LabMasterData", service.getLabDetailsByLabCode(labcode));
			req.setAttribute("lablogo", getSecondLabLogoAsBase64());
			return "cars/CARSFinalFormWordDownload";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSFinalFormWordDownload.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="CARSFinalReportEditSubmit.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String carsFinalReportEditSubmit(HttpServletRequest req, HttpServletResponse res, HttpSession ses,RedirectAttributes redir) throws Exception {
		String Username = (String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() + " Inside CARSFinalReportEditSubmit.htm "+Username);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			String firstTime = req.getParameter("firstTime");
			String Remarks=req.getParameter("t0Remarks");
			
			if(InputValidator.isContainsHTMLTags(Remarks)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				return redirectWithError(redir, "CARSDPCFinalReportDetails.htm", "'T0 Remarks' should not contain HTML Tags.!");
			}
			CARSContract contract = service.getCARSContractByCARSInitiationId(Long.parseLong(carsInitiationId));
			String contractDate = req.getParameter("contractDate");
			String t0Date = req.getParameter("t0Date");
			contract.setContractDate(fc.RegularToSqlDate(contractDate));
			contract.setT0Date(fc.RegularToSqlDate(t0Date));
			contract.setT0Remarks(Remarks);
			long result = service.carsFinalReportEditSubmit(contract, firstTime, labcode);
			if (result > 0) {
				redir.addAttribute("result", "CARS Contract Details Submitted Successfully");
			}
			else {
				redir.addAttribute("resultfail", "CARS Contract Details Submit Unsuccessful");	
			}	
			redir.addAttribute("carsInitiationId",carsInitiationId);
			return "redirect:/CARSDPCFinalReportDetails.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSFinalReportEditSubmit.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="CARSFinalFormPdfDownload.htm")
	public void carsFinalFormPdfDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception{
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CARSFinalFormPdfDownload.htm "+UserId);		
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			if(carsInitiationId!=null) {
				long carsiniid = Long.parseLong(carsInitiationId);
				CARSInitiation carsInitiation = service.getCARSInitiationById(carsiniid);
				req.setAttribute("CARSInitiationData", carsInitiation);
				req.setAttribute("CARSSoCData", service.getCARSSoCByCARSInitiationId(carsiniid));
				req.setAttribute("CARSSoCMilestones", service.getCARSSoCMilestonesByCARSInitiationId(carsiniid));
				req.setAttribute("CARSContractData", service.getCARSContractByCARSInitiationId(carsiniid));
				req.setAttribute("CARSContractConsultants", service.getCARSContractConsultantsByCARSInitiationId(carsiniid));
				req.setAttribute("CARSContractEquipment", service.getCARSContractEquipmentByCARSInitiationId(carsiniid));
			}
			req.setAttribute("LabMasterData", service.getLabDetailsByLabCode(labcode));
			req.setAttribute("lablogo", getLabLogoAsBase64());
			req.setAttribute("pdfFlag", "Y");;
			String filename="CARS-03";	
			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/cars/CARSFinalFormWordDownload.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"));
			PdfWriter pdfw=new PdfWriter(path +File.separator+ "merged.pdf");
			PdfReader pdf1=new PdfReader(path+File.separator+filename+".pdf");
			PdfDocument pdfDocument = new PdfDocument(pdf1, pdfw);	
			pdfDocument.close();
			pdf1.close();	       
			pdfw.close();

			res.setContentType("application/pdf");
			res.setHeader("Content-disposition","inline;filename="+filename+".pdf"); 
			File f=new File(path+"/"+filename+".pdf");

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

			Path pathOfFile2= Paths.get( path+File.separator+filename+".pdf"); 
			Files.delete(pathOfFile2);		

		}
	    catch(Exception e) {	    		
    		logger.error(new Date() +" Inside CARSFinalFormPdfDownload.htm "+UserId, e);
    		e.printStackTrace();
    	}		
	}

	@RequestMapping(value="CARSOtherDocsList.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String carsOtherDocsList(HttpServletRequest req, HttpServletResponse res, HttpSession ses,RedirectAttributes redir) throws Exception {
		String Username = (String)ses.getAttribute("Username");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + " Inside carsOtherDocsList.htm "+Username);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			
			if(carsInitiationId!=null && carsInitiationId!="0") {
				long carsiniid = Long.parseLong(carsInitiationId);
				CARSInitiation carsInitiation = service.getCARSInitiationById(carsiniid);
				req.setAttribute("CARSInitiationData", carsInitiation);
				req.setAttribute("CARSSoCData", service.getCARSSoCByCARSInitiationId(carsiniid));
				req.setAttribute("CARSContractData", service.getCARSContractByCARSInitiationId(carsiniid));	
				req.setAttribute("CARSSoCMilestones", service.getCARSSoCMilestonesByCARSInitiationId(carsiniid));
				req.setAttribute("CARSOtherDocDetailsData", service.getCARSOtherDocDetailsByCARSInitiationId(carsiniid));
				req.setAttribute("CARSStatusDetails", service.carsStatusDetailsByCARSInitiationId(carsiniid));
				req.setAttribute("CARSMPStatusDetails", service.carsMPStatusDetailsByCARSInitiationId(carsiniid));
				req.setAttribute("PDEmpIds", service.getEmpPDEmpId(carsInitiation.getFundsFrom()));
			}
			req.setAttribute("EmpData", service.getEmpDetailsByEmpId(EmpId));
			
			return "cars/CARSOtherDocsList";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSOtherDocsList.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="CARSContractSignatureDetails.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String carsContractSignatureDetails(HttpServletRequest req, HttpServletResponse res, HttpSession ses,RedirectAttributes redir) throws Exception {
		String Username = (String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + " Inside CARSContractSignatureDetails.htm "+Username);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			String csDocsTabId = req.getParameter("csDocsTabId");
			String isApproval = req.getParameter("isApproval");
			if(carsInitiationId==null) {
				String fromapprovals = req.getParameter("carsInitiationIdCSDocApprovals");
				if(fromapprovals!=null) {
					String[] split = fromapprovals.split("/");
					carsInitiationId = split[0];
					if(isApproval==null) {
						isApproval = split[1];
					}
					if(csDocsTabId==null) {
						csDocsTabId = split[2];
					}
				}
				
			}
			
			if(carsInitiationId!=null && carsInitiationId!="0") {
				long carsiniid = Long.parseLong(carsInitiationId);
				CARSInitiation carsInitiation = service.getCARSInitiationById(carsiniid);
				req.setAttribute("CARSInitiationData", carsInitiation);
				req.setAttribute("CARSSoCData", service.getCARSSoCByCARSInitiationId(carsiniid));
				req.setAttribute("CARSContractData", service.getCARSContractByCARSInitiationId(carsiniid));	
				req.setAttribute("CARSOtherDocDetailsData", service.getCARSOtherDocDetailsByCARSInitiationId(carsiniid));
				req.setAttribute("CARSStatusDetails", service.carsStatusDetailsByCARSInitiationId(carsiniid));
				req.setAttribute("CARSOthersCSRemarksHistory", service.carsRemarksHistoryByType(carsInitiationId,"CF"));
				req.setAttribute("OthersCSApprovalEmpData", service.carsTransApprovalData(carsInitiationId, "CF"));
				req.setAttribute("CARSSoCData", service.getCARSSoCByCARSInitiationId(carsiniid));
				req.setAttribute("PDEmpIds", service.getEmpPDEmpId(carsInitiation.getFundsFrom()));
				
			}
			req.setAttribute("EmpData", service.getEmpDetailsByEmpId(EmpId));
			req.setAttribute("csDocsTabId", csDocsTabId!=null?csDocsTabId:"1");
			
			req.setAttribute("GHDPandC", service.getApprAuthorityDataByType(labcode, "GH-DP&C"));
			req.setAttribute("GDDPandC", service.getApprAuthorityDataByType(labcode, "DO-RTMD"));
			req.setAttribute("ADDPandC", service.getApprAuthorityDataByType(labcode, "AD-P&C"));
			req.setAttribute("Director", service.getLabDirectorData(labcode));
			
			req.setAttribute("isApproval", isApproval);
			
			return "cars/CARSContractSignatureDetails";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSContractSignatureDetails.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="CARSCSDocDetailsSubmit.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String carsCSDocDetailsSubmit(HttpServletRequest req,HttpSession ses,RedirectAttributes redir,
			@RequestPart(name="attatchFlagA", required = false) MultipartFile attatchFlagA,
			@RequestPart(name="attatchFlagB", required = false) MultipartFile attatchFlagB,
			@RequestPart(name="attatchFlagC", required = false) MultipartFile attatchFlagC) throws Exception{
		String Username = (String)ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + " Inside CARSCSDocDetailsSubmit.htm "+Username);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			String Action = req.getParameter("Action");
			String csOtherDocDate = req.getParameter("csOtherDocDate");
			
		
			// 🔹 Validate file types
	        if (!isValidFileType(attatchFlagA) || !isValidFileType(attatchFlagB)||!isValidFileType(attatchFlagC)) {

	        	redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("csDocsTabId","2");
	        	return redirectWithError(redir, "CARSContractSignatureDetails.htm",
	                    "Invalid file type. Only PDF  files are allowed.");
	        }
			
			
			long carsiniid = Long.parseLong(carsInitiationId);
			List<CARSOtherDocDetails> otherdocdetails = service.getCARSOtherDocDetailsByCARSInitiationId(carsiniid);
			List<CARSOtherDocDetails> csdetailslist = otherdocdetails.stream().filter(e-> "C".equalsIgnoreCase(e.getOtherDocType())).collect(Collectors.toList());
			CARSOtherDocDetails csdetails = csdetailslist!=null && csdetailslist.size()>0?csdetailslist.get(0):null;
			
			CARSOtherDocDetails doc = Action!=null && Action.equalsIgnoreCase("Add")?new CARSOtherDocDetails():csdetails;
			doc.setCARSInitiationId(carsiniid);
			doc.setOtherDocType("C");
			doc.setOtherDocDate(fc.RegularToSqlDate(csOtherDocDate));
			
			long result=0l;
			if(Action!=null && Action.equalsIgnoreCase("Add")) {
				doc.setForwardedBy(0);
				doc.setCreatedBy(Username);
				doc.setCreatedDate(sdtf.format(new Date()));
				doc.setIsActive(1);
				result=service.CARSCSDocDetailsSubmit(doc, attatchFlagA, attatchFlagB, attatchFlagC, EmpId, labcode);
			}else {
				doc.setModifiedBy(Username);
				doc.setModifiedDate(sdtf.format(new Date()));
				result=service.CARSCSDocDetailsUpdate(doc, attatchFlagA, attatchFlagB, attatchFlagC);
				if (result > 0) {
					redir.addAttribute("result", "Contract Signature Details Updated Successfully");
				} else {
					redir.addAttribute("resultfail", "Contract Signature Details Update Unsuccessful");
				}
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("csDocsTabId","2");
				return "redirect:/CARSContractSignatureDetails.htm";
			}
			if (result > 0) {
				redir.addAttribute("result", "Contract Signature Details Added Successfully");
			} else {
				redir.addAttribute("resultfail", "Contract Signature Details Add Unsuccessful");
			}
			redir.addAttribute("carsInitiationId", carsInitiationId);
			redir.addAttribute("csDocsTabId","2");
			
			return "redirect:/CARSContractSignatureDetails.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSCSDocDetailsSubmit.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value = "CARSCSTransStatus.htm" , method={RequestMethod.POST,RequestMethod.GET})
	public String carsCSTransStatus(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception
	{
		String Username = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside CARSCSTransStatus.htm "+Username);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			req.setAttribute("TransactionList", service.carsTransListByType(carsInitiationId,"C")) ;
			req.setAttribute("TransFlag", "C");
			req.setAttribute("carsInitiationId", carsInitiationId);
			return "cars/CARSTransactionStatus";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSCSTransStatus.htm "+Username, e);
			return "static/Error";
		}
	}

	@RequestMapping(value = {"CARSOtherDocAttachedFileDownload.htm"})
	public void carsOtherDocAttachedFileDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside CARSOtherDocAttachedFileDownload.htm "+UserId);
		try
		{
			String ftype=req.getParameter("filename");
			String otherDocDetailsId=req.getParameter("otherDocDetailsId");
			String otherDocType=req.getParameter("otherDocType");
			res.setContentType("Application/octet-stream");	
			
			CARSOtherDocDetails doc = service.getCARSOtherDocDetailsById(Long.parseLong(otherDocDetailsId));
			File my_file=null;
			String file = ftype.equalsIgnoreCase("flagAFile")?doc.getAttachFlagA():
			         	  (ftype.equalsIgnoreCase("flagBFile")?doc.getAttachFlagB():
			         	  (ftype.equalsIgnoreCase("flagCFile")?doc.getAttachFlagC(): doc.getUploadOtherDoc() ) );
			Path carsPath = Paths.get(labLogoPath, "CARS", "Other Docs");
//			my_file = new File(labLogoPath+"CARS\\Other Docs\\"+File.separator+file); 
			my_file = carsPath.resolve(file).toFile();
	        res.setHeader("Content-disposition","attachment; filename="+file); 
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
				logger.error(new Date() +"Inside CARSOtherDocAttachedFileDownload.htm "+UserId,e);
		}
	}
	
	@RequestMapping(value="OthersCSApprovalSubmit.htm")
	public String othersCSApprovalSubmit(HttpServletRequest req, HttpSession ses, HttpServletResponse res, RedirectAttributes redir) throws Exception{
		String UserId =(String)ses.getAttribute("Username");
		String labcode =(String)ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() +"Inside OthersCSApprovalSubmit.htm "+UserId);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			String otherDocDetailsId = req.getParameter("otherDocDetailsId");
			String action = req.getParameter("Action");
			String remarks = req.getParameter("remarks");
			long carsini = Long.parseLong(carsInitiationId);
			
			CARSInitiation cars = service.getCARSInitiationById(carsini);
			String carsStatusCode = cars.getCARSStatusCode();
			
			CARSApprovalForwardDTO dto = new CARSApprovalForwardDTO();
			dto.setCarsinitiationid(carsini);
			dto.setAction(action);
			dto.setEmpId(EmpId);
			dto.setRemarks(remarks);
			dto.setOtherDocDetailsId(otherDocDetailsId);
			long result = service.othersCSApprovalForward(dto,labcode);
			
			if(action.equalsIgnoreCase("A")) {
				if(carsStatusCode.equalsIgnoreCase("CIN") || carsStatusCode.equalsIgnoreCase("CRA") || 
				   carsStatusCode.equalsIgnoreCase("CRD") || carsStatusCode.equalsIgnoreCase("CRV")) {
					if(result!=0) {
						redir.addAttribute("result","Contract Signature form forwarded Successfully");
					}else {
						redir.addAttribute("resultfail","Contract Signature form forward Unsuccessful");
					}
					redir.addAttribute("carsInitiationId",carsInitiationId);
					return "redirect:/CARSOtherDocsList.htm";
				}else if(carsStatusCode.equalsIgnoreCase("CFA")) {
					if(result!=0) {
						redir.addAttribute("result","Contract Signature form Approved Successfully");
					}else {
						redir.addAttribute("resultfail","Contract Signature form Approve Unsuccessful");
					}
					
					return "redirect:/CARSRSQRApprovals.htm";
				}else {
					if(result!=0) {
						redir.addAttribute("result","Contract Signature form Recommended Successfully");
					}else {
						redir.addAttribute("resultfail","Contract Signature form Recommend Unsuccessful");
					}
					
					return "redirect:/CARSRSQRApprovals.htm";
				}
			}
			else if(action.equalsIgnoreCase("R") || action.equalsIgnoreCase("D")) {
				if(result!=0) {
					redir.addAttribute("result",action.equalsIgnoreCase("R")?"Contract Signature form Returned Successfully":"Contract Signature form Disapproved Successfully");
				}else {
					redir.addAttribute("resultfail",action.equalsIgnoreCase("R")?"Contract Signature form Return Unsuccessful":"Contract Signature form Disapprove Unsuccessful");
				}
			}
			return "redirect:/CARSRSQRApprovals.htm";
			
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside OthersCSApprovalSubmit.htm "+UserId,e);
			return "static/Error";
		}
	}

	@RequestMapping(value="CARSCSDocRevoke.htm", method= {RequestMethod.POST,RequestMethod.GET})
	public String carsCSDoCRevoke(HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() +"Inside CARSCSDocRevoke.htm "+UserId);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
		
			long count = service.carsCSDoCRevoke(carsInitiationId, UserId, EmpId, labcode);
			
			if (count > 0) {
				redir.addAttribute("result", "CARS Contract Signature form Revoked Successfully");
			}
			else {
				redir.addAttribute("resultfail", "CARS Contract Signature form Revoke Unsuccessful");	
			}	

			redir.addAttribute("carsInitiationId", carsInitiationId);
			return "redirect:/CARSOtherDocsList.htm";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSCSDocRevoke.htm "+UserId, e);
			return "static/Error";			
		}

	}

	@RequestMapping(value="CARSCSDownload.htm")
	public void carsCSDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside CARSCSDownload.htm "+UserId);		
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");

			if(carsInitiationId!=null && carsInitiationId!="0") {
				long carsiniid = Long.parseLong(carsInitiationId);
				CARSInitiation carsInitiation = service.getCARSInitiationById(carsiniid);
				req.setAttribute("CARSInitiationData", carsInitiation);
				req.setAttribute("CARSContractData", service.getCARSContractByCARSInitiationId(carsiniid));	
				req.setAttribute("CARSOtherDocDetailsData", service.getCARSOtherDocDetailsByCARSInitiationId(carsiniid));
				req.setAttribute("OthersCSApprovalEmpData", service.carsTransApprovalData(carsInitiationId, "CF"));
				req.setAttribute("PDEmpIds", service.getEmpPDEmpId(carsInitiation.getFundsFrom()));
			}
			
			String filename="CARS-Contract Signature";	
			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/print/CARSCSDownload.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"));
			PdfWriter pdfw=new PdfWriter(path +File.separator+ "merged.pdf");
			PdfReader pdf1=new PdfReader(path+File.separator+filename+".pdf");
			PdfDocument pdfDocument = new PdfDocument(pdf1, pdfw);	
			pdfDocument.close();
			pdf1.close();	       
			pdfw.close();

			res.setContentType("application/pdf");
			res.setHeader("Content-disposition","inline;filename="+filename+".pdf"); 
			File f=new File(path+"/"+filename+".pdf");

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

			Path pathOfFile2= Paths.get( path+File.separator+filename+".pdf"); 
			Files.delete(pathOfFile2);		

		}
	    catch(Exception e) {	    		
    		logger.error(new Date() +" Inside CARSCSDownload.htm "+UserId, e);
    		e.printStackTrace();
    	}		
	}
	
	@RequestMapping(value="CARSCSDocUpload.htm")
	public String carsCSDocUpload(HttpServletRequest req,HttpSession ses,RedirectAttributes redir,
			@RequestPart(name="uploadOtherDoc", required = false) MultipartFile uploadOtherDoc) throws Exception{
		String Username = (String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CARSCSDocUpload.htm "+Username);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			String otherDocDetailsId = req.getParameter("otherDocDetailsId");
		
			// 🔹 Validate file types
	        if (!isValidFileType(uploadOtherDoc)  ) {

	        	redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("csDocsTabId","3");
	        	return redirectWithError(redir, "CARSContractSignatureDetails.htm",
	                    "Invalid file type. Only PDF  files are allowed.");
	        }
			
			
			
			long result = service.carsCSDocUpload(uploadOtherDoc, otherDocDetailsId);
			
			if(result!=0) {
				redir.addAttribute("result", "CARS Contract Signature form Uploaded Successfully");
			}else {
				redir.addAttribute("resultfail", "CARS Contract Signature form Upload UnSuccessful");
			}
			
			redir.addAttribute("carsInitiationId", carsInitiationId);
			redir.addAttribute("csDocsTabId","3");
			return "redirect:/CARSContractSignatureDetails.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSCSDocUpload.htm "+Username, e);
			return "static/Error";
		}
	}

	@RequestMapping(value="CARSMilestonePaymentDetails.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String carsMilestonePaymentDetails(HttpServletRequest req, HttpServletResponse res, HttpSession ses,RedirectAttributes redir) throws Exception {
		String Username = (String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + " Inside CARSMilestonePaymentDetails.htm "+Username);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			String mpDocsTabId = req.getParameter("mpDocsTabId");
			String isApproval = req.getParameter("isApproval");
			String MilestoneNo = req.getParameter("MilestoneNo");
			if(carsInitiationId==null) {
				String fromapprovals = req.getParameter("carsInitiationIdMPDocApprovals");
				if(fromapprovals!=null) {
					String[] split = fromapprovals.split("/");
					carsInitiationId = split[0];
					if(isApproval==null) {
						isApproval = split[1];
					}
					if(mpDocsTabId==null) {
						mpDocsTabId = split[2];
					}
					if(MilestoneNo==null) {
						MilestoneNo = split[3];
					}
				}
				
			}
			
			if(carsInitiationId!=null && carsInitiationId!="0") {
				long carsiniid = Long.parseLong(carsInitiationId);
				CARSInitiation carsInitiation = service.getCARSInitiationById(carsiniid);
				req.setAttribute("CARSInitiationData", carsInitiation);
				req.setAttribute("CARSSoCData", service.getCARSSoCByCARSInitiationId(carsiniid));
				req.setAttribute("CARSContractData", service.getCARSContractByCARSInitiationId(carsiniid));	
				req.setAttribute("CARSSoCMilestones", service.getCARSSoCMilestonesByCARSInitiationId(carsiniid));
				req.setAttribute("CARSOtherDocDetailsData", service.getCARSOtherDocDetailsByCARSInitiationId(carsiniid));
				req.setAttribute("CARSStatusDetails", service.carsStatusDetailsByCARSInitiationId(carsiniid));
				req.setAttribute("CARSOthersMPRemarksHistory", service.carsRemarksHistoryByMilestoneNo(carsInitiationId,MilestoneNo));
				req.setAttribute("OthersMPApprovalEmpData", service.carsTransApprovalDataByMilestoneNo(carsInitiationId, MilestoneNo));
				req.setAttribute("PDEmpIds", service.getEmpPDEmpId(carsInitiation.getFundsFrom()));
				
			}
			req.setAttribute("EmpData", service.getEmpDetailsByEmpId(EmpId));
			req.setAttribute("mpDocsTabId", mpDocsTabId!=null?mpDocsTabId:"1");
			req.setAttribute("MilestoneNo", MilestoneNo);
			
			req.setAttribute("GHDPandC", service.getApprAuthorityDataByType(labcode, "GH-DP&C"));
			req.setAttribute("GDDPandC", service.getApprAuthorityDataByType(labcode, "DO-RTMD"));
			req.setAttribute("ADDPandC", service.getApprAuthorityDataByType(labcode, "AD-P&C"));
			req.setAttribute("Chairperson", service.getApprAuthorityDataByType(labcode, "Chairperson (CARS Committee)"));
			req.setAttribute("Director", service.getLabDirectorData(labcode));
			
			req.setAttribute("isApproval", isApproval);
			
			return "cars/CARSMilestonePaymentDetails";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSMilestonePaymentDetails.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="CARSMPDocDetailsSubmit.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String carsMPDocDetailsSubmit(HttpServletRequest req,HttpSession ses,RedirectAttributes redir,
			@RequestPart(name="attatchFlagA", required = false) MultipartFile attatchFlagA,
			@RequestPart(name="attatchFlagB", required = false) MultipartFile attatchFlagB,
			@RequestPart(name="attatchFlagC", required = false) MultipartFile attatchFlagC) throws Exception{
		String Username = (String)ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + " Inside CARSMPDocDetailsSubmit.htm "+Username);
		try {
		
			String carsInitiationId = req.getParameter("carsInitiationId");
			String Action = req.getParameter("Action");
			String mpOtherDocDate = req.getParameter("mpOtherDocDate");
			String invoiceDate = req.getParameter("invoiceDate");
			String MilestoneNo = req.getParameter("MilestoneNo");
			
			
			// 🔹 Validate file types
	        if (!isValidFileType(attatchFlagA) || !isValidFileType(attatchFlagB)||!isValidFileType(attatchFlagC) ) {

	        	redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("MilestoneNo",MilestoneNo);
				redir.addAttribute("mpDocsTabId","2");
				
	        	return redirectWithError(redir, "CARSMilestonePaymentDetails.htm","Invalid file type. Only PDF  files are allowed.");
	        }
			
			
			
			
			
			
			
			
	
			
			long carsiniid = Long.parseLong(carsInitiationId);
			List<CARSOtherDocDetails> otherdocdetails = service.getCARSOtherDocDetailsByCARSInitiationId(carsiniid);
			List<CARSOtherDocDetails> csdetailslist = otherdocdetails.stream().filter(e-> "M".equalsIgnoreCase(e.getOtherDocType()) && MilestoneNo.equalsIgnoreCase(e.getMilestoneNo())).collect(Collectors.toList());
			CARSOtherDocDetails csdetails = csdetailslist!=null && csdetailslist.size()>0?csdetailslist.get(0):null;
			
			CARSOtherDocDetails doc = Action!=null && Action.equalsIgnoreCase("Add")?new CARSOtherDocDetails():csdetails;
			doc.setCARSInitiationId(carsiniid);
			doc.setOtherDocType("M");
			doc.setOtherDocDate(fc.RegularToSqlDate(mpOtherDocDate));
			doc.setInvoiceNo(req.getParameter("invoiceNo"));
			doc.setInvoiceDate(fc.RegularToSqlDate(invoiceDate));
			doc.setMilestoneNo(MilestoneNo);
			
			long result=0l;
			if(Action!=null && Action.equalsIgnoreCase("Add")) {
				doc.setForwardedBy(0);
				doc.setOthersStatusCode("MIN");
				doc.setOthersStatusCodeNext("MFA");
				doc.setCreatedBy(Username);
				doc.setCreatedDate(sdtf.format(new Date()));
				doc.setIsActive(1);
				result=service.CARSMPDocDetailsSubmit(doc, attatchFlagA, attatchFlagB, attatchFlagC, EmpId, labcode);
			}else {
				doc.setModifiedBy(Username);
				doc.setModifiedDate(sdtf.format(new Date()));
				result=service.CARSMPDocDetailsUpdate(doc, attatchFlagA, attatchFlagB, attatchFlagC);
				if (result > 0) {
					redir.addAttribute("result", "Payment Details Updated Successfully");
				} else {
					redir.addAttribute("resultfail", "Payment Details Update Unsuccessful");
				}
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("MilestoneNo",MilestoneNo);
				redir.addAttribute("mpDocsTabId","2");
				return "redirect:/CARSMilestonePaymentDetails.htm";
			}
			if (result > 0) {
				redir.addAttribute("result", "Payment Details Added Successfully");
			} else {
				redir.addAttribute("resultfail", "Payment Details Add Unsuccessful");
			}
			redir.addAttribute("carsInitiationId", carsInitiationId);
			redir.addAttribute("MilestoneNo",MilestoneNo);
			redir.addAttribute("mpDocsTabId","2");
			
			return "redirect:/CARSMilestonePaymentDetails.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSMPDocDetailsSubmit.htm "+Username, e);
			return "static/Error";
		}
	}

	@RequestMapping(value="OthersMPApprovalSubmit.htm")
	public String othersMPApprovalSubmit(HttpServletRequest req, HttpSession ses, HttpServletResponse res, RedirectAttributes redir) throws Exception{
		String UserId =(String)ses.getAttribute("Username");
		String labcode =(String)ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() +"Inside OthersMPApprovalSubmit.htm "+UserId);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			String action = req.getParameter("Action");
			String remarks = req.getParameter("remarks");
			String otherDocDetailsId = req.getParameter("otherDocDetailsId");
			
			long carsini = Long.parseLong(carsInitiationId);
			
//			CARSInitiation cars = service.getCARSInitiationById(carsini);
//			String carsStatusCode = cars.getCARSStatusCode();
			
			CARSOtherDocDetails docDetails = service.getCARSOtherDocDetailsById(Long.parseLong(otherDocDetailsId));
			String statusCode = docDetails.getOthersStatusCode();
			
			CARSApprovalForwardDTO dto = new CARSApprovalForwardDTO();
			dto.setCarsinitiationid(carsini);
			dto.setAction(action);
			dto.setEmpId(EmpId);
			dto.setRemarks(remarks);
			dto.setOtherDocDetailsId(otherDocDetailsId);
			dto.setMilestoneNo(req.getParameter("MilestoneNo"));
			long result = service.othersMPApprovalForward(dto,labcode);
			
			if(action.equalsIgnoreCase("A")) {
				if(statusCode.equalsIgnoreCase("MIN") || statusCode.equalsIgnoreCase("MRA") || statusCode.equalsIgnoreCase("MRC") ||
				   statusCode.equalsIgnoreCase("MRD") || statusCode.equalsIgnoreCase("MRV")) {
					if(result!=0) {
						redir.addAttribute("result","Payment Approval form forwarded Successfully");
					}else {
						redir.addAttribute("resultfail","Payment Approval form forward Unsuccessful");
					}
					redir.addAttribute("carsInitiationId",carsInitiationId);
					return "redirect:/CARSOtherDocsList.htm";
				}else if(statusCode.equalsIgnoreCase("MFC")) {
					if(result!=0) {
						redir.addAttribute("result","Payment Approval form Approved Successfully");
					}else {
						redir.addAttribute("resultfail","Payment Approval form Approve Unsuccessful");
					}
					
					return "redirect:/CARSRSQRApprovals.htm";
				}else {
					if(result!=0) {
						redir.addAttribute("result","Payment Approval form Recommended Successfully");
					}else {
						redir.addAttribute("resultfail","Payment Approval form Recommend Unsuccessful");
					}
					
					return "redirect:/CARSRSQRApprovals.htm";
				}
			}
			else if(action.equalsIgnoreCase("R") || action.equalsIgnoreCase("D")) {
				if(result!=0) {
					redir.addAttribute("result",action.equalsIgnoreCase("R")?"Payment Approval form Returned Successfully":"Payment Approval form Disapproved Successfully");
				}else {
					redir.addAttribute("resultfail",action.equalsIgnoreCase("R")?"Payment Approval form Return Unsuccessful":"Payment Approval form Disapprove Unsuccessful");
				}
			}
			return "redirect:/CARSRSQRApprovals.htm";
			
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside OthersMPApprovalSubmit.htm "+UserId,e);
			return "static/Error";
		}
	}

	@RequestMapping(value="CARSMPDocRevoke.htm", method= {RequestMethod.POST,RequestMethod.GET})
	public String carsMPDocRevoke(HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() +"Inside CARSMPDocRevoke.htm "+UserId);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			String MilestoneNo = req.getParameter("MilestoneNo");
		
			long count = service.carsMPDoCRevoke(carsInitiationId, UserId, EmpId, MilestoneNo, labcode);
			
			if (count > 0) {
				redir.addAttribute("result", "CARS Payment Approval form Revoked Successfully");
			}
			else {
				redir.addAttribute("resultfail", "CARS Payment Approval form Revoke Unsuccessful");	
			}	

			redir.addAttribute("carsInitiationId", carsInitiationId);
			return "redirect:/CARSOtherDocsList.htm";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSMPDocRevoke.htm "+UserId, e);
			return "static/Error";			
		}

	}
	
	@RequestMapping(value="CARSMPDownload.htm")
	public void carsMPDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside CARSMPDownload.htm "+UserId);		
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			String MilestoneNo = req.getParameter("MilestoneNo");

			if(carsInitiationId==null) {
				String fromapprovals = req.getParameter("carsInitiationIdMPDocDownload");
				if(fromapprovals!=null) {
					String[] split = fromapprovals.split("/");
					carsInitiationId = split[0];
					
					if(MilestoneNo==null) {
						MilestoneNo = split[1];
					}
				}
				
			}
			
			if(carsInitiationId!=null && carsInitiationId!="0") {
				long carsiniid = Long.parseLong(carsInitiationId);
				CARSInitiation carsInitiation = service.getCARSInitiationById(carsiniid);
				req.setAttribute("CARSInitiationData", carsInitiation);
				req.setAttribute("CARSContractData", service.getCARSContractByCARSInitiationId(carsiniid));	
				req.setAttribute("CARSSoCMilestones", service.getCARSSoCMilestonesByCARSInitiationId(carsiniid));
				req.setAttribute("CARSOtherDocDetailsData", service.getCARSOtherDocDetailsByCARSInitiationId(carsiniid));
				req.setAttribute("OthersMPApprovalEmpData", service.carsTransApprovalDataByMilestoneNo(carsInitiationId, MilestoneNo));
				req.setAttribute("MilestoneNo", MilestoneNo);
			}
			
			String filename="CARS-Payment Approval";	
			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/print/CARSMPDownload.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"));
			PdfWriter pdfw=new PdfWriter(path +File.separator+ "merged.pdf");
			PdfReader pdf1=new PdfReader(path+File.separator+filename+".pdf");
			PdfDocument pdfDocument = new PdfDocument(pdf1, pdfw);	
			pdfDocument.close();
			pdf1.close();	       
			pdfw.close();

			res.setContentType("application/pdf");
			res.setHeader("Content-disposition","inline;filename="+filename+".pdf"); 
			File f=new File(path+"/"+filename+".pdf");

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

			Path pathOfFile2= Paths.get( path+File.separator+filename+".pdf"); 
			Files.delete(pathOfFile2);		

		}
	    catch(Exception e) {	    		
    		logger.error(new Date() +" Inside CARSMPDownload.htm "+UserId, e);
    		e.printStackTrace();
    	}		
	}

	@RequestMapping(value="CARSMPDocUpload.htm")
	public String carsMPDocUpload(HttpServletRequest req,HttpSession ses,RedirectAttributes redir,
			@RequestPart(name="uploadOtherDoc", required = false) MultipartFile uploadOtherDoc) throws Exception{
		String Username = (String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CARSMPDocUpload.htm "+Username);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			String otherDocDetailsId = req.getParameter("otherDocDetailsId");
			String MilestoneNo = req.getParameter("MilestoneNo");
		
		
			// 🔹 Validate file types
	        if (!isValidFileType(uploadOtherDoc)  ) {

				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("MilestoneNo", MilestoneNo);
				redir.addAttribute("mpDocsTabId","3");
	        	return redirectWithError(redir, "CARSMilestonePaymentDetails.htm",
	                    "Invalid file type. Only PDF  files are allowed.");
	        }
			
			long result = service.carsMPDocUpload(uploadOtherDoc, otherDocDetailsId);
			
			if(result!=0) {
				redir.addAttribute("result", "CARS Payment Approval form Uploaded Successfully");
			}else {
				redir.addAttribute("resultfail", "CARS Payment Approval form Upload UnSuccessful");
			}
			
			redir.addAttribute("carsInitiationId", carsInitiationId);
			redir.addAttribute("MilestoneNo", MilestoneNo);
			redir.addAttribute("mpDocsTabId","3");
			return "redirect:/CARSMilestonePaymentDetails.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSMPDocUpload.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="CARSPaymentDocDetailsSubmit.htm", method= {RequestMethod.POST,RequestMethod.GET})
	public String carsPaymentDocDetailsSubmit(HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception{
		String Username = (String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CARSPaymentDocDetailsSubmit.htm "+Username);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			String otherDocDetailsId = req.getParameter("otherDocDetailsId");
			String MilestoneNo = req.getParameter("MilestoneNo");
			String ptcOtherDocDate = req.getParameter("ptcOtherDocDate");
			
			long carsiniid = Long.parseLong(carsInitiationId);
			CARSOtherDocDetails ptcdetails = service.getCARSOtherDocDetailsById(Long.parseLong(otherDocDetailsId));
			CARSOtherDocDetails doc = otherDocDetailsId!=null && otherDocDetailsId.equalsIgnoreCase("0")?new CARSOtherDocDetails():ptcdetails;
			
			doc.setCARSInitiationId(carsiniid);
			doc.setOtherDocType("P");
			doc.setOtherDocDate(fc.RegularToSqlDate(ptcOtherDocDate));
			doc.setMilestoneNo(MilestoneNo);
			
			long result=0l;
			if(otherDocDetailsId!=null && otherDocDetailsId.equalsIgnoreCase("0")) {
				doc.setInitiationDate(sdf.format(new Date()));
				doc.setCreatedBy(Username);
				doc.setCreatedDate(sdtf.format(new Date()));
				doc.setIsActive(1);
				result=service.CARSPTCDocDetailsSubmit(doc);
			}else {
				doc.setModifiedBy(Username);
				doc.setModifiedDate(sdtf.format(new Date()));
				result=service.CARSPTCDocDetailsUpdate(doc);
				if (result > 0) {
					redir.addAttribute("result", "Payment Details Updated Successfully");
				} else {
					redir.addAttribute("resultfail", "Payment Details Update Unsuccessful");
				}
				redir.addAttribute("carsInitiationId", carsInitiationId);
				return "redirect:/CARSOtherDocsList.htm";
			}
			if (result > 0) {
				redir.addAttribute("result", "Payment Details Added Successfully");
			} else {
				redir.addAttribute("resultfail", "Payment Details Add Unsuccessful");
			}
			
			redir.addAttribute("carsInitiationId", carsInitiationId);
			return "redirect:/CARSOtherDocsList.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSPaymentDocDetailsSubmit.htm "+Username, e);
			return "static/Error";
		}
	}

	@RequestMapping(value="CARSMilestonePaymentLetterDownload.htm")
	public void carsMilestonePaymentLetterDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception{
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CARSMilestonePaymentLetterDownload.htm "+UserId);		
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			String MilestoneNo = req.getParameter("MilestoneNo");

			if(carsInitiationId!=null && carsInitiationId!="0") {
				long carsiniid = Long.parseLong(carsInitiationId);
				CARSInitiation carsInitiation = service.getCARSInitiationById(carsiniid);
				req.setAttribute("CARSInitiationData", carsInitiation);
				req.setAttribute("CARSContractData", service.getCARSContractByCARSInitiationId(carsiniid));	
				req.setAttribute("CARSSoCMilestones", service.getCARSSoCMilestonesByCARSInitiationId(carsiniid));
				req.setAttribute("CARSOtherDocDetailsData", service.getCARSOtherDocDetailsByCARSInitiationId(carsiniid));
				req.setAttribute("MilestoneNo", MilestoneNo);
				Object[] obj = service.getApprAuthorityDataByType(labcode, "DO-RTMD");
				req.setAttribute("DPandC", service.getEmpDetailsByEmpId(obj[0].toString()));
			}
			
			String filename="CARS-Payment towards"+MilestoneNo;	
			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/print/CARSMilestonePaymentLetterDownload.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"));
			PdfWriter pdfw=new PdfWriter(path +File.separator+ "merged.pdf");
			PdfReader pdf1=new PdfReader(path+File.separator+filename+".pdf");
			PdfDocument pdfDocument = new PdfDocument(pdf1, pdfw);	
			pdfDocument.close();
			pdf1.close();	       
			pdfw.close();

			res.setContentType("application/pdf");
			res.setHeader("Content-disposition","inline;filename="+filename+".pdf"); 
			File f=new File(path+"/"+filename+".pdf");

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

			Path pathOfFile2= Paths.get( path+File.separator+filename+".pdf"); 
			Files.delete(pathOfFile2);		

		}
	    catch(Exception e) {	    		
    		logger.error(new Date() +" Inside CARSMilestonePaymentLetterDownload.htm "+UserId, e);
    		e.printStackTrace();
    	}		
	}
	
	@RequestMapping(value="CARSFinalSoOLetter.htm")
	public void carsFinalSoOLetter(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception{
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CARSFinalSoOLetter.htm "+UserId);		
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");

			if(carsInitiationId!=null && carsInitiationId!="0") {
				long carsiniid = Long.parseLong(carsInitiationId);
				CARSInitiation carsInitiation = service.getCARSInitiationById(carsiniid);
				req.setAttribute("CARSInitiationData", carsInitiation);
				req.setAttribute("CARSContractData", service.getCARSContractByCARSInitiationId(carsiniid));	
				req.setAttribute("CARSOtherDocDetailsData", service.getCARSOtherDocDetailsByCARSInitiationId(carsiniid));
				Object[] obj = service.getApprAuthorityDataByType(labcode, "DO-RTMD");
				req.setAttribute("DPandC", service.getEmpDetailsByEmpId(obj[0].toString()));
			}
			req.setAttribute("LabMasterData", service.getLabDetailsByLabCode(labcode));
			req.setAttribute("lablogo", getLabLogoAsBase64());
			String filename="Final-SoO";	
			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			
			//Path for Hindi font
			String fontPath = req.getServletContext().getRealPath("/view/pfp/NotoSansDevanagari-Regular.ttf");
			req.setAttribute("fontPath", fontPath);
			
			req.getRequestDispatcher("/view/print/CARSFinalSoOLetter.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			// Hindi font converter
			ConverterProperties converterProperties = new ConverterProperties();
			FontProvider fontProvider = new FontProvider();
			fontProvider.addFont(fontPath, PdfEncodings.IDENTITY_H);
			converterProperties.setFontProvider(fontProvider);
			
			try (PdfWriter writer = new PdfWriter(path + File.separator + filename + ".pdf");
					PdfDocument pdfDoc = new PdfDocument(writer);
					Document document = HtmlConverter.convertToDocument(html, pdfDoc, converterProperties)) {
				
				 	// Create font using PdfDocument
			    	PdfFont hindiFont = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H, pdfDoc);
			    	
	                document.setFont(hindiFont);
	            }
			
			res.setContentType("application/pdf");
			res.setHeader("Content-disposition","inline;filename="+filename+".pdf"); 
			File f=new File(path+"/"+filename+".pdf");

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

			Path pathOfFile2= Paths.get( path+File.separator+filename+".pdf"); 
			Files.delete(pathOfFile2);	

		}
	    catch(Exception e) {	    		
    		logger.error(new Date() +" Inside CARSFinalSoOLetter.htm "+UserId, e);
    		e.printStackTrace();
    	}		
	}
	
	@RequestMapping(value="FinalSoODateSubmit.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String finalSoODateSubmit(HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception{
		String Username = (String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside FinalSoODateSubmit.htm "+Username);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			String calendardate = req.getParameter("calendardate");
			int result = service.finalSoODateSubmit(carsInitiationId, fc.RegularToSqlDate(calendardate));
			if(result!=0) {
				redir.addAttribute("result","Letter Date Submitted Successfully");
			}else {
				redir.addAttribute("resultfail","Letter Date Submit Unsuccessful");
			}
			
			redir.addAttribute("carsInitiationId", carsInitiationId);
			
			return "redirect:/CARSOtherDocsList.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside FinalSoODateSubmit.htm "+Username, e);
			return "static/Error";
		}
	}

	@RequestMapping(value="CARSMilestonesMonitor.htm")
	public String carsMilestonesMonitorList(HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception {
		String Username = (String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CARSMilestonesMonitor.htm "+Username);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			
			if(carsInitiationId!=null && carsInitiationId!="0") {
				long carsiniid = Long.parseLong(carsInitiationId);
				CARSInitiation carsInitiation = service.getCARSInitiationById(carsiniid);
				req.setAttribute("carsInitiationData", carsInitiation);
				req.setAttribute("carsSoCData", service.getCARSSoCByCARSInitiationId(carsiniid));
				req.setAttribute("carsContractData", service.getCARSContractByCARSInitiationId(carsiniid));	
				req.setAttribute("carsSoCMilestones", service.getCARSSoCMilestonesByCARSInitiationId(carsiniid));
				req.setAttribute("PDEmpIds", service.getEmpPDEmpId(carsInitiation.getFundsFrom()));
				List<Object[]> carsSoCMilestonesProgressList = service.getAllCARSSoCMilestonesProgressList();
				carsSoCMilestonesProgressList = carsSoCMilestonesProgressList!=null && carsSoCMilestonesProgressList.size()>0? carsSoCMilestonesProgressList.stream()
						.filter(e -> Long.parseLong(e[6].toString())==carsiniid).collect(Collectors.toList()) : new ArrayList<Object[]>();
				req.setAttribute("milestoneProgressList", carsSoCMilestonesProgressList);
			}
			
			return "cars/CARSMilestonesMonitor";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSMilestonesMonitor.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="CARSMilestonesMonitorDetails.htm")
	public String carsMilestonesMonitorDetails(HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception {
		String Username = (String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CARSMilestonesMonitor.htm "+Username);
		try {
			
			req.setAttribute("assignedList", service.assignedListByCARSSoCMilestoneId(req.getParameter("carsSoCMilestoneId")));
			req.setAttribute("carsInitiationId", req.getParameter("carsInitiationId"));
			req.setAttribute("carsSoCMilestoneId", req.getParameter("carsSoCMilestoneId"));
			req.setAttribute("presFlag", req.getParameter("presFlag"));
			return "cars/CARSMilestonesMonitorDetails";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSMilestonesMonitorDetails.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="CARSMilestonesMonitorDetailsSubmit.htm", method = RequestMethod.POST)
	public String CARSMilestonesMonitorDetailsSubmit(HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception {
		String Username = (String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CARSMilestonesMonitorDetailsSubmit.htm "+Username);
		try {
			String action = req.getParameter("Action");
			String carsInitiationId = req.getParameter("carsInitiationId");
			String carsSoCMilestoneId = req.getParameter("carsSoCMilestoneId");
			String actionMainId = req.getParameter("actionMainId");
			
			String actionName=req.getParameter("activityName");
			if(InputValidator.isContainsHTMLTags(actionName)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("carsSoCMilestoneId", carsSoCMilestoneId);
				return redirectWithError(redir, "CARSMilestonesMonitorDetails.htm", "'Action Name' should not contain HTML Tags.!");
			}
			
			CARSInitiation carsInitiation = service.getCARSInitiationById(Long.parseLong(carsInitiationId));
					
			ActionMainDto mainDto=new ActionMainDto();
			mainDto.setActionMainId(actionMainId);
			mainDto.setMainId("0");
			mainDto.setActionItem(actionName);
			mainDto.setActionLinkId("0");
			mainDto.setProjectId(carsInitiation.getFundsFrom());
			mainDto.setActionDate(req.getParameter("pdc"));
			mainDto.setScheduleMinutesId("0");
			mainDto.setCARSSoCMilestoneId(carsSoCMilestoneId);
			mainDto.setActionStatus("A");
			mainDto.setType("A");
			mainDto.setActionType("Z");
			mainDto.setPriority("L");
			mainDto.setCategory("O");
			mainDto.setLabName(labcode);
			mainDto.setActivityId("0");
			mainDto.setCreatedBy(Username);
			mainDto.setMeetingDate(sdf.format(new Date()));
			mainDto.setActionLevel(1L);
			mainDto.setActionParentId("0");
			
			List<String> emp = new ArrayList<>();
			String [] assignee = req.getParameterValues("Assignee");
			for(String s : assignee) {
				emp.add(s);
			}
			
			ActionAssignDto assign = new ActionAssignDto();
			
			assign.setActionDate(req.getParameter("pdc"));
			assign.setAssigneeList(req.getParameterValues("Assignee"));
			assign.setAssignor((Long) ses.getAttribute("EmpId"));
			assign.setAssigneeLabCode(labcode);
			assign.setAssignorLabCode(labcode);
			assign.setRevision(0);
//			assign.setActionFlag("N");		
			assign.setActionStatus("A");
			assign.setCreatedBy(Username);
			assign.setIsActive(1);
			assign.setMeetingDate(sdf.format(new Date()));
			assign.setPDCOrg(req.getParameter("pdc"));
			assign.setMultipleAssigneeList(emp);
			
			Long result = service.carsMilestoneActionMainInsert(mainDto , assign);
			if(result!=0) {
				redir.addAttribute("result","Milestone Action Details "+action+"ed Successfully");
			}else {
				redir.addAttribute("resultfail","Milestone Activity Details "+action+" Unsuccessful");
			}
			
			redir.addAttribute("carsInitiationId", carsInitiationId);
			redir.addAttribute("carsSoCMilestoneId", carsSoCMilestoneId);
			
			return "redirect:/CARSMilestonesMonitorDetails.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSMilestonesMonitorDetailsSubmit.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="CARSMilestonesMonitorDetailsEditSubmit.htm", method = RequestMethod.POST)
	public String CARSMilestonesMonitorDetailsEditSubmit(HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception {
		String Username = (String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside CARSMilestonesMonitorDetailsEditSubmit.htm "+Username);
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			String carsSoCMilestoneId = req.getParameter("carsSoCMilestoneId");
			
			String actionName=req.getParameter("activityName");
			if(InputValidator.isContainsHTMLTags(actionName)) {
				redir.addAttribute("carsInitiationId", carsInitiationId);
				redir.addAttribute("carsSoCMilestoneId", carsSoCMilestoneId);
				return redirectWithError(redir, "CARSMilestonesMonitorDetails.htm", "'Action Item' should not contain HTML Tags.!");
			}
			
			ActionMain main=new ActionMain();
			main.setActionMainId(Long.parseLong(req.getParameter("actionmainid")));
			main.setActionItem(actionName);
			main.setModifiedBy(Username);
			main.setModifiedDate(sdtf.format(new Date()));
			
			ActionAssign assign=new ActionAssign();
			
			String newPDC = req.getParameter("newPDC");
			if(newPDC!=null) {
				assign.setPDCOrg(java.sql.Date.valueOf(sdf.format(rdf.parse(newPDC))));
				assign.setEndDate(java.sql.Date.valueOf(sdf.format(rdf.parse(newPDC))));
			}	
			assign.setAssigneeLabCode(labcode);
			assign.setAssignee(Long.parseLong(req.getParameter("Assignee")));
			assign.setActionAssignId(Long.parseLong(req.getParameter("actionassigneid")));
			assign.setModifiedBy(Username);
			
			Long result = (long) actionservice.ActionMainEdit(main);
			actionservice.ActionAssignEdit(assign);
			
			if (result > 0) {
				redir.addAttribute("result", "Milestone Action Details Updated Successfully");
			} else {
				redir.addAttribute("resultfail", "Milestone Action Details Update Unsuccessful");
			}
			
			redir.addAttribute("carsInitiationId", carsInitiationId);
			redir.addAttribute("carsSoCMilestoneId", carsSoCMilestoneId);
			
			return "redirect:/CARSMilestonesMonitorDetails.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSMilestonesMonitorDetailsEditSubmit.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="CARSReport.htm", method = {RequestMethod.POST, RequestMethod.GET})
	public String carsReport(HttpServletRequest req, HttpSession ses) throws Exception {
		String Username = (String)ses.getAttribute("Username");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String LoginType = (String) ses.getAttribute("LoginType");
		logger.info(new Date() +" Inside CARSReport.htm "+Username);
		try {
			req.setAttribute("initiationList", service.carsInitiationList(LoginType, EmpId));
			return "cars/CARSReport";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSReport.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="CARSMilestonesProgressDetails.htm", method = {RequestMethod.POST, RequestMethod.GET})
	public String carsMilestonesProgressDetails(HttpServletRequest req, HttpSession ses) throws Exception {
		String Username = (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside CARSMilestonesProgressDetails.htm "+Username);
		try {
			req.setAttribute("carsInitiationId", req.getParameter("carsInitiationId"));
			req.setAttribute("carsSoCMilestoneId", req.getParameter("carsSoCMilestoneId"));
			req.setAttribute("carsSoCMilestones", service.getCARSSoCMilestonesById(req.getParameter("carsSoCMilestoneId")));
			req.setAttribute("carsSoCMilestonesProgressList", service.getCARSSoCMilestonesProgressListByCARSSoCMilestoneId(req.getParameter("carsSoCMilestoneId")));
			req.setAttribute("presFlag", req.getParameter("presFlag"));
			return "cars/CARSMilestonesProgressDetails";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSMilestonesProgressDetails.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="CARSMilestonesProgressDetailsSubmit.htm", method = {RequestMethod.POST, RequestMethod.GET})
	public String CARSMilestonesProgressDetailsSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
		String Username = (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside CARSMilestonesProgressDetailsSubmit.htm "+Username);
		try {
			String carsSoCMilestoneId = req.getParameter("carsSoCMilestoneId");
			CARSSoCMilestonesProgress milestoneProgress = CARSSoCMilestonesProgress.builder()
														  .CARSSoCMilestoneId(Long.parseLong(carsSoCMilestoneId))
														  .Progress(Integer.parseInt(req.getParameter("progress")))
														  .ProgressDate(fc.rdfTosdf(req.getParameter("progressDate")))
														  .Remarks(req.getParameter("remarks"))
														  .CreatedBy(Username)
														  .CreatedDate(sdtf.format(new Date()))
														  .IsActive(1)
														  .build();
			
			long result = service.addCARSSoCMilestonesProgress(milestoneProgress);
			
			if (result > 0) {
				redir.addAttribute("result", "Milestone Progress Details Updated Successfully");
			} else {
				redir.addAttribute("resultfail", "Milestone Progress Details Update Unsuccessful");
			}
			
			redir.addAttribute("carsInitiationId", req.getParameter("carsInitiationId"));
			redir.addAttribute("carsSoCMilestoneId", carsSoCMilestoneId);
			return "redirect:/CARSMilestonesProgressDetails.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSMilestonesProgressDetailsSubmit.htm "+Username, e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="CARSReportPresentation.htm", method = {RequestMethod.POST, RequestMethod.GET})
	public String carsReportPresentation(HttpServletRequest req, HttpSession ses) throws Exception {
		String Username = (String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		String LoginType = (String)ses.getAttribute("LoginType");
		String EmpId = ((Long)ses.getAttribute("EmpId")).toString();
		logger.info(new Date() +" Inside CARSReportPresentation.htm "+Username);
		try {
			req.setAttribute("labInfo", printservice.LabDetailes(labcode));
	    	req.setAttribute("lablogo", logoUtil.getLabLogoAsBase64String(labcode));
	    	req.setAttribute("initiationList", service.carsInitiationList(LoginType, EmpId));
	    	req.setAttribute("allCARSContractList", service.getAllCARSContractList());
	    	req.setAttribute("allCARSSoCMilestonesList", service.getAllCARSSoCMilestonesList());
	    	req.setAttribute("allMilestoneProgressList", service.getAllCARSSoCMilestonesProgressList());
	    	req.setAttribute("allCARSOtherDocDetailsList", service.getCARSOtherDocDetailsList());
			return "cars/CARSReportPresentation";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSReportPresentation.htm "+Username, e);
			return "static/Error";
		}
	}

	@RequestMapping(value="CARSPresentationDownload.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public void carsPresentationDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception{
		String Username = (String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		String LoginType = (String)ses.getAttribute("LoginType");
		String EmpId = ((Long)ses.getAttribute("EmpId")).toString();
		logger.info(new Date() +"Inside CARSPresentationDownload.htm "+Username);		
		try {
			req.setAttribute("labInfo", printservice.LabDetailes(labcode));
	    	req.setAttribute("lablogo", logoUtil.getLabLogoAsBase64String(labcode));
	    	req.setAttribute("initiationList", service.carsInitiationList(LoginType, EmpId));
	    	req.setAttribute("allCARSContractList", service.getAllCARSContractList());
	    	req.setAttribute("allCARSSoCMilestonesList", service.getAllCARSSoCMilestonesList());
	    	req.setAttribute("allMilestoneProgressList", service.getAllCARSSoCMilestonesProgressList());
	    	req.setAttribute("allCARSOtherDocDetailsList", service.getCARSOtherDocDetailsList());
	    	
			String filename="CARS_Presentation";	
			String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/print/CARSPresentationDownload.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"));
			PdfWriter pdfw=new PdfWriter(path +File.separator+ "merged.pdf");
			PdfReader pdf1=new PdfReader(path+File.separator+filename+".pdf");
			PdfDocument pdfDocument = new PdfDocument(pdf1, pdfw);	
			pdfDocument.close();
			pdf1.close();	       
			pdfw.close();

			res.setContentType("application/pdf");
			res.setHeader("Content-disposition","inline;filename="+filename+".pdf"); 
			File f=new File(path+"/"+filename+".pdf");

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

			Path pathOfFile2= Paths.get( path+File.separator+filename+".pdf"); 
			Files.delete(pathOfFile2);		

		}
	    catch(Exception e) {	    		
    		logger.error(new Date() +" Inside CARSRSQRDownloadBeforeFreeze.htm "+Username, e);
    		e.printStackTrace();
    	}		
	}
	
	@RequestMapping(value="CARSCurrentStatusSubmit.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String carsCurrentStatusSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
		String Username = (String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CARSCurrentStatusSubmit.htm "+Username);	
		try {
			String carsInitiationId = req.getParameter("carsInitiationId");
			String currentStatus = req.getParameter("currentStatus");
//			if(InputValidator.isContainsHTMLTags(currentStatus)) {
//				return  redirectWithError(redir,"CARSInitiationList.htm","Cars Status should not contain HTML elements !");
//			}
			int result = service.carsCurrentStatusUpdate(currentStatus, carsInitiationId);
			
			if (result > 0) {
				redir.addAttribute("result", "CARS Current Status Details Updated Successfully");
			} else {
				redir.addAttribute("resultfail", "CARS Current Status Details Update Unsuccessful");
			}
			return "redirect:/CARSInitiationList.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSCurrentStatusSubmit.htm "+Username, e);
			return "static/Error";
		}
	}

	@RequestMapping(value = "CARSAnnualLabReport.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String carsAnnualLabReport(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside CARSAnnualLabReport.htm "+UserId);
		try {
			String annualYear = req.getParameter("annualYear");
			if(annualYear==null) {
				annualYear = LocalDate.now().getYear()+"";
			}
			req.setAttribute("annualYear", annualYear);
			req.setAttribute("annualReportList", service.getCARSAnnualReportListByYear(annualYear));
			req.setAttribute("initiationList", service.carsInitiationList("A", EmpId));

			return "cars/CARSAnnualLabReport";
		}
		catch (Exception e) {
			logger.error(new Date() +" Inside CARSAnnualLabReport.htm "+UserId, e);
			e.printStackTrace();
			return "static/Error";
		} 
	}
	
	@RequestMapping(value="CARSAnnualReportSubmit.htm",method = {RequestMethod.GET})
	public @ResponseBody String CARSAnnualReportSubmit(HttpServletRequest req, HttpSession ses) throws Exception {
		String UserId = (String)ses.getAttribute("Username");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() +"Inside carsAnnualReportSubmit.htm "+UserId);
		List<Object[]> initiationList = new ArrayList<>();
		try {
			String annualYear = req.getParameter("annualYear");
			String carsInitiationIdList = req.getParameter("carsInitiationIdList");
			
			String[] carsInitiationIds = carsInitiationIdList.split(",");
			
			// Delete Existing Records
			service.deleteCARSAnnualReportRecordsByYear(annualYear);
			
			for(int i = 0; i < carsInitiationIds.length; i++) {
				CARSAnnualReport annualReport = new CARSAnnualReport();
				annualReport.setAnnualYear(Integer.parseInt(annualYear));
				annualReport.setCARSInitiationId(carsInitiationIds[i]!=null?Long.parseLong(carsInitiationIds[i]):0);
				annualReport.setCreatedBy(UserId);
				annualReport.setCreatedDate(sdtf.format(new Date()));
				annualReport.setIsActive(1);
				
				service.addCARSAnnualReport(annualReport);
			}
			
			initiationList = service.carsInitiationList("A", EmpId);
			
		}
		catch(Exception e){
			e.printStackTrace();
			logger.error(new Date() +" Inside CARSAnnualReportSubmit.htm"+UserId, e);
		}
		Gson json = new Gson();
		return json.toJson(initiationList);
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
	
}
