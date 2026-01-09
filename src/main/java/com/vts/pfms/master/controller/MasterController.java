package com.vts.pfms.master.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.vts.pfms.FormatConverter;
import com.vts.pfms.admin.service.AdminService;
import com.vts.pfms.committee.model.PfmsEmpRoles;
import com.vts.pfms.committee.model.ProgrammeMaster;
import com.vts.pfms.committee.model.ProgrammeProjects;
import com.vts.pfms.committee.service.CommitteeService;
import com.vts.pfms.master.dto.DivisionEmployeeDto;
import com.vts.pfms.master.dto.LabMasterAdd;
import com.vts.pfms.master.dto.OfficerMasterAdd;
import com.vts.pfms.master.model.DivisionGroup;
import com.vts.pfms.master.model.DivisionTd;
import com.vts.pfms.master.model.HolidayMaster;
import com.vts.pfms.master.model.IndustryPartner;
import com.vts.pfms.master.model.IndustryPartnerRep;
import com.vts.pfms.master.model.MilestoneActivityType;
import com.vts.pfms.master.model.PfmsFeedback;
import com.vts.pfms.master.model.PfmsFeedbackAttach;
import com.vts.pfms.master.model.PfmsFeedbackTrans;
import com.vts.pfms.master.model.RoleMaster;
import com.vts.pfms.master.service.MasterService;
import com.vts.pfms.utils.InputValidator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@Controller
public class MasterController {

	@Autowired
	MasterService service;

	@Autowired
	AdminService adminservice;
	
	@Autowired
	CommitteeService committeeservice;
	
	@Value("${ApplicationFilesDrive}")
	String uploadpath;
	
	private String redirectWithError(RedirectAttributes redir,String redirURL, String message) {
	    redir.addAttribute("resultfail", message);
	    return "redirect:/"+redirURL;
	}
	
	private static final Logger logger=LogManager.getLogger(MasterController.class);
	private SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	FormatConverter fc = new FormatConverter();
	private SimpleDateFormat sdtf = fc.getSqlDateAndTimeFormat();
	private SimpleDateFormat sdf = fc.getSqlDateFormat();
	private SimpleDateFormat rdf = new SimpleDateFormat("dd-MM-yyyy");
	@RequestMapping(value="Officer.htm", method=RequestMethod.GET)
	public String OfficerList(Model model, HttpServletRequest req, HttpServletResponse res, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId= (String)ses.getAttribute("Username");
		String LabCode=(String)ses.getAttribute("labcode");
		logger.info(new Date() +" Inside Officer.htm "+UserId);
		String empType=req.getParameter("empType");
		String onboard=req.getParameter("Onboarding");
		if(onboard==null) {
			Map md=model.asMap();
			onboard=(String)md.get("Onboard");
		}
		req.setAttribute("Onboarding", onboard);
		req.setAttribute("OfficerList", service.OfficerList().stream().filter(e-> e[11]!=null).filter(e-> LabCode.equalsIgnoreCase(e[11].toString())).collect(Collectors.toList()));         
		req.setAttribute("AllOfficerList", service.OfficerList());         

		return "master/OfficerMasterList";
	}


	@RequestMapping(value="OfficerAdd.htm", method=RequestMethod.POST)
	public String OfficerAdd(HttpServletResponse res, HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{		
		String UserId= (String)ses.getAttribute("Username");
		String LabCode =(String)ses.getAttribute("labcode");
		logger.info(new Date() +" Inside OfficerAdd.htm "+UserId);

		try {
			req.setAttribute("DesignationList", service.DesignationList());
			req.setAttribute("OfficerDivisionList", adminservice.DivisionMasterList(LabCode));
			req.setAttribute("LabList", service.LabList());
			req.setAttribute("OfficerList", service.OfficerList());
			return "master/OfficerMasterAdd";

		}catch (Exception e){
			e.printStackTrace();
			logger.error(new Date() +" Inside OfficerAdd.htm "+UserId , e);
			return "static/Error";
		}
	}

	@RequestMapping(value = "EmpNoCheck.htm", method = RequestMethod.GET)
	public @ResponseBody String EmpNoCheck(HttpSession ses, HttpServletRequest req) throws Exception 
	{
		String UserId=(String)ses.getAttribute("Username");
		int len=0;
		logger.info(new Date() +"Inside EmpNoCheck.htm "+UserId);
		try
		{	 
			List<Object[]> DisDesc = null;
			String empno=req.getParameter("empno");					
			DisDesc= service.empNoCheckAjax(empno);
			len=DisDesc.size();
		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside EmpNoCheck.htm "+UserId,e);
		}

		return String.valueOf(len);

	}


	@RequestMapping(value = "ExpEmpNoCheck.htm", method = RequestMethod.GET)
	public @ResponseBody String ExpEmpNoCheck(HttpSession ses, HttpServletRequest req) throws Exception 
	{
		String UserId=(String)ses.getAttribute("Username");
		int len=0;
		logger.info(new Date() +"Inside ExpEmpNoCheck.htm "+UserId);
		try
		{	 
			List<Object[]> DisDesc = null;
			String empno=req.getParameter("empno");	
				if(empno.startsWith("<") ) {
					return String.valueOf(-1);
				}
			DisDesc= service.extEmpNoCheckAjax(empno);
			len=DisDesc.size();
			
			
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"Inside ExpEmpNoCheck.htm "+UserId,e);
		}

		return String.valueOf(len);

	}


	@RequestMapping(value="Officer.htm", method=RequestMethod.POST)
	public String OfficerListAddEdit(HttpServletResponse res, HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception{

		String UserId= (String)ses.getAttribute("Username");
		String LabCode =(String)ses.getAttribute("labcode");
		logger.info(new Date() +" Inside Officer.htm "+UserId);

		try {
			String Option=req.getParameter("sub");
			String OfficerId=req.getParameter("Did");		
			if(Option.equalsIgnoreCase("edit")) 
			{		
				req.setAttribute("OfficerEditData", service.OfficerEditData(OfficerId).get(0));
				req.setAttribute("DesignationList", service.DesignationList());
				req.setAttribute("OfficerDivisionList", adminservice.DivisionMasterList(LabCode));
				req.setAttribute("LabList", service.LabList());	
				req.setAttribute("OfficerList", service.OfficerList());
				return "master/OfficerMasterEdit";
			}
			else if(Option.equalsIgnoreCase("updateSeniority")) 
			{
				req.setAttribute("officersDetalis",service.getOfficerDetalis(OfficerId));
				return "master/UpdateSeniority";
			}
			else if(Option.equalsIgnoreCase("delete")) 
			{	
				int count= service.OfficerMasterDelete(OfficerId, UserId);
				if(count>0) {
					redir.addAttribute("result", "Officer Deleted Successfully ");
				}else {
					redir.addAttribute("resultfail","Officer Delete Unsuccessful");
				}	
			}

		}catch (Exception e){
			e.printStackTrace();
			logger.error(new Date() +" Inside Officer.htm "+UserId , e);
			return "static/Error";
		}

		return "redirect:/Officer.htm";
	}




	@RequestMapping (value="OfficerMasterAddSubmit.htm", method=RequestMethod.POST)
	public String OfficerAddSubmit (HttpSession ses, HttpServletRequest  req, HttpServletResponse res, RedirectAttributes redir) throws Exception{

		String UserId= (String)ses.getAttribute("Username");
		String LabCode =(String)ses.getAttribute("labcode");
		logger.info(new Date() +" Inside OfficerMasterAddSubmit.htm "+UserId);
		Integer labid= Integer.parseInt(ses.getAttribute("labid").toString());
		try {
			String EmpNo=req.getParameter("EmpNo");
			List<String> EmpNoCheck=service.EmpNoCheck();
			boolean check=EmpNoCheck.contains(EmpNo);			
			if(check) {
				redir.addAttribute("resultfail","Emp No Already Exists" );
				return "redirect:/Officer.htm";
			}			
			
			String empNo=req.getParameter("EmpNo");
			String empName=req.getParameter("EmpName");
			String extNo=req.getParameter("ExtNo");
			String email= req.getParameter("Email");
			String dEmail = req.getParameter("DronaEmail");
			String iEmail = req.getParameter("InternetEmail");
			String mobileNumber = req.getParameter("mobilenumber");
			
			if(!InputValidator.isValidCodeWithCapitalsAndNumeric(empNo) || !InputValidator.isValidCodeWithCapitalsAndNumeric(extNo)){
				return redirectWithError(redir,"Officer.htm","Number must contain only uppercase letters and numbers.");
			}		
			
			if(!InputValidator.isValidNameWithCapitalsAndSmallLettersAndSpace(empName)) {
				return redirectWithError(redir,"Officer.htm","Name can contain only letters and spaces.");
			}
			
			if(!InputValidator.isValidMobileNo(mobileNumber)) {
				return redirectWithError(redir,"Officer.htm","Please enter a valid 10-digit mobile number starting with 6-9.");	
			}
			
			if(!InputValidator.isValidEmail(email) || (dEmail!=null && !dEmail.isEmpty() && !InputValidator.isValidEmail(dEmail) ) || (iEmail!=null &&! iEmail.isEmpty() && !InputValidator.isValidEmail(iEmail) )) {
				return redirectWithError(redir,"Officer.htm","The email address you entered is not valid. Please try again.");
			}
			
			OfficerMasterAdd officermasteradd= new OfficerMasterAdd();
			officermasteradd.setLabId(req.getParameter("labId"));
			officermasteradd.setEmpNo(empNo.toUpperCase());
			officermasteradd.setTitle(req.getParameter("title"));
			officermasteradd.setSalutation(req.getParameter("salutation"));
			String name=empName;
			String words[]=name.split("\\s");  
			String capitalizeWord="";  
			for(String w:words){  
				String first=w.substring(0,1);  
				String afterfirst=w.substring(1);  
				capitalizeWord+=first.toUpperCase()+afterfirst+" ";  
			}	
			name = name.substring(0,1).toUpperCase() + name.substring(1);
			officermasteradd.setEmpName(capitalizeWord);
			officermasteradd.setDesignation(req.getParameter("Designation"));
			officermasteradd.setExtNo(extNo);
			officermasteradd.setEmail(email);
			officermasteradd.setDivision(req.getParameter("Division"));
			officermasteradd.setDronaEmail(dEmail);
			officermasteradd.setInternalEmail(iEmail);
			officermasteradd.setMobileNo(mobileNumber);
			officermasteradd.setSrNo("0");
			officermasteradd.setLabCode(LabCode);
			officermasteradd.setSuperiorOfficer(req.getParameter("superiorOfficer"));
			officermasteradd.setEmpStatus(req.getParameter("empStatus"));	//srikant

			long count=0;

			try {
				count= service.OfficerMasterInsert(officermasteradd, UserId);	
			}
			catch(Exception e){
				e.printStackTrace();
				return "redirect:/Officer.htm";
			}
			if(count>0) {
				redir.addAttribute("result", "Officer Added Successfully ");
			}else {
				redir.addAttribute("resultfail", "Officer Add Unsuccessful");
			}
		}
		catch (Exception e){			
			e.printStackTrace();
			logger.error(new Date() +" Inside OfficerMasterAddSubmit.htm "+UserId , e);
			return "static/Error";
		}
		return "redirect:/Officer.htm";
	}


	@RequestMapping (value="OfficerMasterEditSubmit.htm", method=RequestMethod.POST)
	public String OfficerMasterEditSubmit(HttpServletRequest req, HttpServletResponse res, HttpSession ses, RedirectAttributes redir) throws Exception{

		String UserId= (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside OfficerMasterEditSubmit.htm "+UserId);
		try {
			
			String empNo=req.getParameter("EmpNo");
			String empName=req.getParameter("EmpName");
			String extNo=req.getParameter("ExtNo");
			String email= req.getParameter("Email");
			String dEmail = req.getParameter("DronaEmail");
			String iEmail = req.getParameter("InternetEmail");
			String mobileNumber = req.getParameter("mobilenumber");
			
			if(!InputValidator.isValidCodeWithCapitalsAndNumeric(empNo) || !InputValidator.isValidCodeWithCapitalsAndNumeric(extNo)){
				return redirectWithError(redir,"Officer.htm","Number must contain only uppercase letters and numbers.");
			}		
			
			if(!InputValidator.isValidNameWithCapitalsAndSmallLettersAndSpace(empName)) {
				return redirectWithError(redir,"Officer.htm","Name can contain only letters and spaces.");
			}
			
			if(!InputValidator.isValidMobileNo(mobileNumber)) {
				return redirectWithError(redir,"Officer.htm","Please enter a valid 10-digit mobile number starting with 6-9.");	
			}
			
			if(!InputValidator.isValidEmail(email) || (dEmail != null && !dEmail.isEmpty() && !InputValidator.isValidEmail(dEmail) ) || (iEmail != null && !iEmail.isEmpty() && !InputValidator.isValidEmail(iEmail) )) {
				return redirectWithError(redir,"Officer.htm","The email address you entered is not valid. Please try again.");
			}

			OfficerMasterAdd officermasteradd= new OfficerMasterAdd();
			officermasteradd.setTitle(req.getParameter("title"));
			officermasteradd.setSalutation(req.getParameter("salutation"));
			officermasteradd.setLabId(req.getParameter("labId"));
			officermasteradd.setEmpNo(empNo);
			officermasteradd.setEmpName(empName);
			officermasteradd.setDesignation(req.getParameter("Designation"));
			officermasteradd.setExtNo(extNo);
			officermasteradd.setMobileNo(mobileNumber);
			officermasteradd.setEmail(email);
			officermasteradd.setDivision(req.getParameter("Division"));
			officermasteradd.setEmpId(req.getParameter("OfficerId"));
			officermasteradd.setDronaEmail(dEmail);
			officermasteradd.setInternalEmail(iEmail);
			officermasteradd.setSuperiorOfficer(req.getParameter("superiorOfficer"));
			officermasteradd.setEmpStatus(req.getParameter("empStatus"));	//srikant

			int count= service.OfficerMasterUpdate(officermasteradd, UserId);				

			if(count>0) {
				redir.addAttribute("result", "Officer Edited Successfully ");
			}
			else {			
				redir.addAttribute("resultfail", "Officer Edit Unsuccessful");
			}
		}
		catch (Exception e){

			e.printStackTrace();
			logger.error(new Date() +" Inside OfficerMasterEditSubmit.htm "+UserId , e);
			return "static/Error";
		}		
		return "redirect:/Officer.htm";
	}



	@RequestMapping(value="UpdateSenioritySubmit",method=RequestMethod.POST)
	public String updateSenoSubmit(HttpServletRequest req, HttpServletResponse res, HttpSession ses, RedirectAttributes redir)throws Exception {
		String UserId= (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside UpdateSenioritySubmit "+UserId);
		String empid= req.getParameter("empid");
		String newSeniorityNumber=req.getParameter("UpdatedSrNo");
		try{
		int result= service.updateSeniorityNumber(empid,newSeniorityNumber);
		if(result>0) {
			redir.addAttribute("result", "Officer Edited Successfully ");
		}
		else {
			redir.addAttribute("resultfail", "Officer Edit Unsuccessful");
		}
		}catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/Officer.htm";
	}

	@RequestMapping(value="OfficerExtList.htm", method=RequestMethod.GET)
	public String OfficerExtList(HttpServletRequest req, HttpServletResponse res, HttpSession ses, RedirectAttributes redir) throws Exception{

		String UserId= (String)ses.getAttribute("Username");
		String LabCode= (String)ses.getAttribute("labcode");
		logger.info(new Date() +" Inside OfficerExtList.htm "+UserId);
		try {

			req.setAttribute("OfficerList", service.ExternalOfficerList(LabCode));

		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside OfficerExtList.htm "+UserId , e);
			return "static/Error";
		}

		return "master/OfficerExtList";
	}

	@RequestMapping(value="OfficerExtAdd.htm", method=RequestMethod.POST)
	public String OfficerExtAdd(HttpServletResponse res, HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{		
		String UserId= (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside OfficerExtAdd.htm "+UserId);

		try {
			List<Object[]> OfficerDivisionList = service.OfficerDivisionList();
			req.setAttribute("DesignationList", service.DesignationList());
			req.setAttribute("OfficerDivisionList", OfficerDivisionList);
			req.setAttribute("LabList", service.LabList());

			return "master/OfficerExtAdd";

		}catch (Exception e){
			e.printStackTrace();
			logger.error(new Date() +" Inside OfficerExtAdd.htm "+UserId , e);
			return "static/Error";
		}
	}

	@RequestMapping (value="OfficerExtAddSubmit.htm", method=RequestMethod.POST)
	public String OfficerExtAddSubmit (HttpSession ses, HttpServletRequest  req, HttpServletResponse res, RedirectAttributes redir) throws Exception
	{
		String UserId= (String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +" Inside OfficerExtAddSubmit.htm "+UserId);
		try {
			String EmpNo=req.getParameter("EmpNo");
			List<String> EmpNoCheck=service.EmpExtNoCheck();
			boolean check=EmpNoCheck.contains(EmpNo);			
			if(check) {
				redir.addAttribute("resultfail","Emp No Already Exists" );
				return "redirect:/OfficerExtList.htm";
			}
			
			String empNo=req.getParameter("EmpNo");
			String empName=req.getParameter("EmpName");
			String extNo=req.getParameter("ExtNo");
			String email= req.getParameter("Email");
			String dEmail = req.getParameter("DronaEmail");
			String iEmail = req.getParameter("InternetEmail");
			String mobileNumber = req.getParameter("mobilenumber");
			
			if(!InputValidator.isValidCodeWithCapitalsAndNumeric(empNo) || !InputValidator.isValidCodeWithCapitalsAndNumeric(extNo)){
				return redirectWithError(redir,"OfficerExtList.htm","Number must contain only uppercase letters and numbers.");
			}		
			
			if(!InputValidator.isValidNameWithCapitalsAndSmallLettersAndSpace(empName)) {
				return redirectWithError(redir,"OfficerExtList.htm","Name can contain only letters and spaces.");
			}
			
			if(!InputValidator.isValidMobileNo(mobileNumber)) {
				return redirectWithError(redir,"OfficerExtList.htm","Please enter a valid 10-digit mobile number starting with 6-9.");	
			}
			
			if(!InputValidator.isValidEmail(email) || (dEmail!=null && !dEmail.isEmpty() && !InputValidator.isValidEmail(dEmail) ) || (iEmail!=null &&! iEmail.isEmpty() && !InputValidator.isValidEmail(iEmail) )) {
				return redirectWithError(redir,"OfficerExtList.htm","The email address you entered is not valid. Please try again.");
			}
			
			OfficerMasterAdd officermasteradd= new OfficerMasterAdd();
			officermasteradd.setLabId(req.getParameter("labId"));
			officermasteradd.setEmpNo(empNo.toUpperCase());
			String name=req.getParameter("EmpName");			
			String words[]=name.split("\\s");  
			String capitalizeWord="";  
			for(String w:words){  
				String first=w.substring(0,1);  
				String afterfirst=w.substring(1);  
				capitalizeWord+=first.toUpperCase()+afterfirst+" ";  
			}	
			name = name.substring(0,1).toUpperCase() + name.substring(1);
			officermasteradd.setTitle(req.getParameter("title"));
			officermasteradd.setSalutation(req.getParameter("salutation"));
			officermasteradd.setEmpName(capitalizeWord);
			officermasteradd.setDesignation(req.getParameter("Designation"));
			officermasteradd.setExtNo(extNo);
			officermasteradd.setEmail(email);
			officermasteradd.setDivision(req.getParameter("Division"));
			officermasteradd.setDronaEmail(dEmail);
			officermasteradd.setInternalEmail(iEmail);
			officermasteradd.setMobileNo(mobileNumber);
			officermasteradd.setSrNo("0");
			officermasteradd.setLabCode(labcode);
			long count=0;

			try {
				count= service.OfficerExtInsert(officermasteradd, UserId);	
			}
			catch(Exception e){
				e.printStackTrace();
				return "redirect:/OfficerExtList.htm";
			}
			if(count>0) {
				redir.addAttribute("result", "External Officer Added Successfully ");
			}else {
				redir.addAttribute("resultfail", "External Officer Add Unsuccessful");
			}
			return "redirect:/OfficerExtList.htm";
		}
		catch (Exception e){			
			e.printStackTrace();
			logger.error(new Date() +" Inside OfficerExtAddSubmit.htm "+UserId , e);
			return "static/Error";
		}

	}

	@RequestMapping(value="OfficerExtEdit.htm", method=RequestMethod.POST)
	public String OfficerExtEdit(HttpServletResponse res, HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{		
		String UserId= (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside OfficerExtEdit.htm "+UserId);

		try {
			String Option=req.getParameter("sub");
			String OfficerId=req.getParameter("Did");		
			if(Option.equalsIgnoreCase("edit")) 
			{		
				req.setAttribute("OfficerEditData", service.ExternalOfficerEditData(OfficerId).get(0));
				req.setAttribute("DesignationList", service.DesignationList());
				req.setAttribute("OfficerDivisionList", service.OfficerDivisionList());
				req.setAttribute("LabList", service.LabList());	
				return "master/OfficerExtEdit";
			}
			else if(Option.equalsIgnoreCase("delete")) 
			{	
				int count= service.OfficerExtDelete(OfficerId, UserId);
				if(count>0) {
					redir.addAttribute("result", "External Officer Deleted Successfully ");
				}else {
					redir.addAttribute("resultfail","External Officer Delete Unsuccessful");
				}	
			}

		}catch (Exception e){
			e.printStackTrace();
			logger.error(new Date() +" Inside OfficerExtEdit.htm "+UserId , e);
			return "static/Error";
		}

		return "redirect:/OfficerExtList.htm";
	}

	@RequestMapping (value="OfficerExtEditSubmit.htm", method=RequestMethod.POST)
	public String OfficerExtEditSubmit(HttpServletRequest req, HttpServletResponse res, HttpSession ses, RedirectAttributes redir) throws Exception{

		String UserId= (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside OfficerExtEditSubmit.htm "+UserId);
		try {
			String empNo=req.getParameter("EmpNo");
			String empName=req.getParameter("EmpName");
			String extNo=req.getParameter("ExtNo");
			String email= req.getParameter("Email");
			String dEmail = req.getParameter("DronaEmail");
			String iEmail = req.getParameter("InternetEmail");
			String mobileNumber = req.getParameter("mobilenumber");
			
			if(!InputValidator.isValidCodeWithCapitalsAndNumeric(empNo) || !InputValidator.isValidCodeWithCapitalsAndNumeric(extNo)){
				return redirectWithError(redir,"OfficerExtList.htm","Number must contain only uppercase letters and numbers.");
			}		
			
			if(!InputValidator.isValidNameWithCapitalsAndSmallLettersAndSpace(empName)) {
				return redirectWithError(redir,"OfficerExtList.htm","Name can contain only letters and spaces.");
			}
			
			if(!InputValidator.isValidMobileNo(mobileNumber)) {
				return redirectWithError(redir,"OfficerExtList.htm","Please enter a valid 10-digit mobile number starting with 6-9.");	
			}
			
			if(!InputValidator.isValidEmail(email) || (dEmail!=null && !dEmail.isEmpty() && !InputValidator.isValidEmail(dEmail) ) || (iEmail!=null &&! iEmail.isEmpty() && !InputValidator.isValidEmail(iEmail) )) {
				return redirectWithError(redir,"OfficerExtList.htm","The email address you entered is not valid. Please try again.");
			}
			
			OfficerMasterAdd officermasteradd= new OfficerMasterAdd();
			officermasteradd.setLabId(req.getParameter("labId"));
			officermasteradd.setEmpNo(empNo);
			officermasteradd.setEmpName(empName);
			officermasteradd.setDesignation(req.getParameter("Designation"));
			officermasteradd.setExtNo(extNo);
			officermasteradd.setMobileNo(mobileNumber);
			officermasteradd.setEmail(email);
			officermasteradd.setDivision(req.getParameter("Division"));
			officermasteradd.setEmpId(req.getParameter("OfficerId"));
			officermasteradd.setDronaEmail(dEmail);
			officermasteradd.setInternalEmail(iEmail);	
			officermasteradd.setTitle(req.getParameter("title"));
			officermasteradd.setSalutation(req.getParameter("salutation"));
			int count= service.OfficerExtUpdate(officermasteradd, UserId);							
			if(count>0) {
				redir.addAttribute("result", "External Officer Edited Successfully ");
			}
			else {			
				redir.addAttribute("resultfail", "External Officer Edit Unsuccessful");
			}
			return "redirect:/OfficerExtList.htm";
		}
		catch (Exception e){

			e.printStackTrace();
			logger.error(new Date() +" Inside OfficerExtEditSubmit.htm "+UserId , e);
			return "static/Error";
		}		

	}

	@RequestMapping(value="DivisionEmployee.htm",method= {RequestMethod.POST,RequestMethod.GET})
	public String DivisionEmployee(Model model,HttpServletRequest req, HttpServletResponse res, HttpSession ses, RedirectAttributes redir)throws Exception {

		String UserId= (String)ses.getAttribute("Username");
		String LabCode =(String)ses.getAttribute("labcode");
		logger.info(new Date() +" Inside DivisionEmployee.htm "+UserId);
		try {
			String divisionid=req.getParameter("divisionid");


			if(divisionid==null)
			{			
				Map md=model.asMap();
				divisionid=(String)md.get("divisionid");
			}

			List<Object[]>  divisionlist = adminservice.DivisionMasterList(LabCode);
			if(divisionlist==null || divisionlist.size()==0)
			{
				redir.addAttribute("resultfail", "No Divisions Found");
				return "redirect:/MainDashBoard.htm";
			}
			if(divisionid==null)
			{
				divisionid =divisionlist.get(0)[0].toString();
			}		
			req.setAttribute("divisiondata", service.DivisionData(divisionid));
			req.setAttribute("divisionlist", divisionlist);
			req.setAttribute("divisionemplist", service.DivisionEmpList(divisionid));	
			req.setAttribute("empoyeelist",service.DivisionNonEmpList(divisionid).stream().filter(e-> LabCode.equalsIgnoreCase(e[3].toString())).collect(Collectors.toList()));
			return "master/DivisionAssign";

		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside DivisionEmployee.htm "+UserId , e);
			return "static/Error";
		}
	}


	@RequestMapping(value="DivsionEmployeeRevoke.htm",method= {RequestMethod.POST,RequestMethod.GET})
	public String DivsionEmployeeRevoke(Model model,HttpServletRequest req, HttpServletResponse res, HttpSession ses, RedirectAttributes redir)throws Exception 
	{		
		String UserId= (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside DivsionEmployeeRevoke.htm "+UserId);
		try {
			String divisionid=req.getParameter("divisionid");
			String divisionempid=req.getParameter("divisionempid");
			DivisionEmployeeDto dto=new DivisionEmployeeDto();
			dto.setDivisionEmployeeId(divisionempid);
			dto.setModifiedBy(UserId);
			int ret=service.DivsionEmployeeRevoke(dto);

			if(ret>0) {
				redir.addAttribute("result", "Employee Revoke Successfully ");
			}
			else {
				redir.addAttribute("resultfail", "Employee Revoke Unsuccessful");
			}

			redir.addFlashAttribute("divisionid",divisionid);
			return "redirect:/DivisionEmployee.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside DivsionEmployeeRevoke.htm "+UserId , e);
			return "static/Error";
		}

	}


	@RequestMapping(value="DivisionAssignSubmit.htm",method= {RequestMethod.POST,RequestMethod.GET})
	public String DivisionAssignSubmit(Model model,HttpServletRequest req, HttpServletResponse res, HttpSession ses, RedirectAttributes redir)throws Exception 
	{		
		String UserId= (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside DivisionAssignSubmit.htm "+UserId);
		try {
			String divisionid=req.getParameter("divisionid");
			String employeeid[]=req.getParameterValues("employeeid");

			DivisionEmployeeDto dto=new DivisionEmployeeDto();
			dto.setDivisionId(divisionid);
			dto.setEmpId(employeeid);
			dto.setCreatedBy(UserId);
			long ret=service.DivisionAssignSubmit(dto);

			if(ret>0) {
				redir.addAttribute("result", "Employee Assigned Successfully ");
			}
			else {
				redir.addAttribute("resultfail", "Employee Assign Unsuccessful");
			}


			redir.addFlashAttribute("divisionid",divisionid);
			return "redirect:/DivisionEmployee.htm";

		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside DivisionAssignSubmit.htm "+UserId , e);
			return "static/Error";
		}		
	}


	@RequestMapping(value="MilestoneActivityTypes.htm",method= {RequestMethod.POST,RequestMethod.GET})
	public String MilestoneActivityTypes(Model model,HttpServletRequest req, HttpServletResponse res, HttpSession ses, RedirectAttributes redir)throws Exception 
	{		
		String UserId= (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside MilestoneActivityTypes.htm "+UserId);
		try {		


			req.setAttribute("activitylist", service.ActivityList());
			return "master/MilestoneActivityType";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside MilestoneActivityTypes.htm "+UserId , e);
			return "static/Error";
		}		
	}

	@RequestMapping(value = "AddActivityCheck.htm", method = RequestMethod.GET)
	public @ResponseBody String AddActivityCheck(HttpSession ses, HttpServletRequest req) throws Exception 
	{
		String UserId=(String)ses.getAttribute("Username");
		Object[] DisDesc = null;
		logger.info(new Date() +"Inside AddActivityCheck.htm "+UserId);
		try
		{	  
			DisDesc= service.ActivityNameCheck(req.getParameter("activityTypeId"), req.getParameter("activitytype"));
		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside AddActivityCheck.htm "+UserId,e);
		}
		Gson json = new Gson();
		return json.toJson(DisDesc); 

	}

	@RequestMapping(value="ActivityAddSubmit.htm",method= {RequestMethod.POST})
	public String ActivityAddSubmit(HttpServletRequest req, HttpServletResponse res, HttpSession ses, RedirectAttributes redir)throws Exception 
	{		
		String UserId= (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside ActivityAddSubmit.htm "+UserId);
		try {		
			String activityType = req.getParameter("activitytype");
			String activityCode = req.getParameter("activityCode");
			
			if(!InputValidator.isValidCapitalsAndSmallsAndNumericAndSpace(activityType)) {
				return redirectWithError(redir,"MilestoneActivityTypes.htm","Activity type must contain letters, numbers, and spaces only.");
			}
			if(!InputValidator.isValidCodeWithCapitalsAndNumeric(activityCode)){
				return redirectWithError(redir,"MilestoneActivityTypes.htm","Activity code must contain only uppercase letters and numbers.");
			}
			
			MilestoneActivityType model =new MilestoneActivityType();
			model.setActivityType(activityType);
			model.setActivityCode(activityCode);
			model.setIsTimeSheet(req.getParameter("isTimeSheet"));
			model.setCreatedBy(UserId);
			long count =service.ActivityAddSubmit(model);

			if(count>0) {
				redir.addAttribute("result", "Activity Type Added Successfully ");
			}
			else {
				redir.addAttribute("resultfail", "Activity Type Adding Unsuccessful");
			}

			return "redirect:/MilestoneActivityTypes.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside ActivityAddSubmit.htm "+UserId , e);
			return "static/Error";
		}		
	}

	@RequestMapping(value = "GroupMaster.htm", method = {RequestMethod.GET, RequestMethod.POST})
	public String GroupMaster(Model model, HttpServletRequest req, HttpServletResponse res, HttpSession ses, RedirectAttributes redir) throws Exception {

	    String UserId = (String) ses.getAttribute("Username");
	    String LabCode = (String) ses.getAttribute("labcode");

	    logger.info(new Date() + " Inside GroupMaster.htm " + UserId);

	    try {
	        String onboard = req.getParameter("Onboarding");
	        String Option = req.getParameter("sub");

	        if (onboard == null) {
	            Map<String, Object> md = model.asMap();
	            onboard = (String) md.get("Onboard");
	        }

	        if (Option == null) {
	            
	            req.setAttribute("groupslist", service.GroupsList(LabCode));
	            req.setAttribute("Onboarding", onboard);
	            return "master/GroupsList";
	        } 
	        else if (Option.equalsIgnoreCase("add")) {
	          
	            req.setAttribute("groupheadlist", service.GroupHeadList(LabCode));
	            req.setAttribute("tdaddlist", service.TDListAdd()
	                                            .stream()
	                                            .filter(e -> LabCode.equalsIgnoreCase(e[3].toString()))
	                                            .collect(Collectors.toList()));
	            return "master/GroupMasterAdd";
	        } 
	        else if (Option.equalsIgnoreCase("edit")) {
	            
	            String groupid = req.getParameter("groupid");
	            req.setAttribute("groupsdata", service.GroupsData(groupid));
	            req.setAttribute("groupheadlist", service.GroupHeadList(LabCode));
	            req.setAttribute("tdaddlist", service.TDListAdd()
	                                            .stream()
	                                            .filter(e -> LabCode.equalsIgnoreCase(e[3].toString()))
	                                            .collect(Collectors.toList()));
	            return "master/GroupMasterEdit";
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        logger.error(new Date() + " Inside GroupMaster.htm " + UserId, e);
	        redir.addAttribute("resultfail", "Technical Issue");
	    }

	    return "redirect:/GroupMaster.htm"; 
	}

	@RequestMapping(value = "GroupMasterAddSubmit.htm",method=RequestMethod.POST )
	public String GroupMasterAddSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside GroupMaster.htm "+UserId);	
		String LabCode= (String)ses.getAttribute("labcode");

		try {				
			String groupCode=req.getParameter("gCode");
			String groupName=req.getParameter("gName");
			String GroupHeadName=req.getParameter("ghempid");
			String tdid=req.getParameter("tdId");
			
			if(!InputValidator.isValidCapitalsAndSmallsAndNumeric(groupCode)){
				redir.addAttribute("sub", "add");
				return redirectWithError(redir,"GroupMaster.htm","'Group Code' must contain only Alphabets and Numbers.!");
			} 
			if(!InputValidator.isValidCapitalsAndSmallsAndNumericAndSpace(groupName)) {
				redir.addAttribute("sub", "add");
				return redirectWithError(redir,"GroupMaster.htm","'Group Name' must contain only Alphabets and Numbers.!");
			}
			
			DivisionGroup dgm=new DivisionGroup();
			dgm.setGroupCode(groupCode);
			dgm.setGroupName(groupName);
			dgm.setGroupHeadId(Long.parseLong(GroupHeadName));
			dgm.setCreatedBy(UserId);
			dgm.setLabCode(LabCode);
			dgm.setTDId(tdid);

			long count=service.GroupAddSubmit(dgm);
			if (count > 0) {
				redir.addAttribute("result", "Group Added Successfully");
			} else {
				redir.addAttribute("resultfail", "Group addition unsuccessful");
			}

			return "redirect:/GroupMaster.htm";
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside GroupMaster.htm "+UserId, e);
			return "static/Error";
		}
	}

	
	@RequestMapping(value = "GroupMasterEditSubmitCheck.htm", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> GroupMasterEditSubmitCheck(HttpServletRequest req, HttpSession ses) {
	    String userId = (String) ses.getAttribute("Username");
	    Map<String, Object> response = new HashMap<>();
	    String groupId = req.getParameter("groupid"); 
	    String groupCode = req.getParameter("groupcode"); 
	    Integer isActive = Integer.valueOf(req.getParameter("isActive"));
	    logger.info(new Date() + " Inside GroupMasterEditSubmitCheck.htm " + userId);
	    try {
	        List<Object[]> alertDivisionMaster = service.checkDivisionMasterId(groupId);
	        
	        if (alertDivisionMaster != null && !alertDivisionMaster.isEmpty()) {
	            for (Object[] obj : alertDivisionMaster) {
	                Integer isActivedb = (Integer) obj[1];
	                
	                if (isActivedb.equals(isActive)) {
	                    response.put("valid", true);
	                } else {
	                    response.put("valid", false);
	                    response.put("message", "Group Code- '" + groupCode + "' cannot be made inactive as it exists in Division Master ( Division Name- '"+obj[2]+"' )");
	                }
	            }
	        } else {
	            response.put("valid", true);
	        }
	    } catch (Exception e) {
	    	e.printStackTrace();
	        response.put("valid", false);
	        response.put("message", "Error validating status");
	        logger.error("Error in GroupMasterEditSubmitCheck", e);
	    }
	    
	    return response;
	}

	@RequestMapping (value="GroupMasterEditSubmit.htm" , method=RequestMethod.POST)
	public String GroupMasterEditSubmit(HttpServletRequest req, HttpServletResponse res, HttpSession ses, RedirectAttributes redir) throws Exception
	{

		String Userid = (String) ses.getAttribute("Username");

		logger.info(new Date() +" Inside GroupMasterEditSubmit.htm "+Userid );
		
		try {
		    String groupCode = req.getParameter("groupcode");
		    String groupName = req.getParameter("groupname");
		    String tdIdStr = req.getParameter("tdId");
		    String ghempidStr = req.getParameter("ghempid");
		    String groupidStr = req.getParameter("groupid");
		    String isActiveStr = req.getParameter("isActive");


		    if(!InputValidator.isValidCodeWithCapitalsAndNumeric(groupCode)){
		    	redir.addAttribute("sub", "edit");
		        redir.addAttribute("groupid", groupidStr);
		        return redirectWithError(redir,"GroupMaster.htm","'Group code' must contain only Alphabets and Numbers.!");
		    } 
		    if(!InputValidator.isValidCapitalsAndSmallsAndNumericAndSpace(groupName)) {
		    	redir.addAttribute("sub", "edit");
		        redir.addAttribute("groupid", groupidStr);
		        return redirectWithError(redir,"GroupMaster.htm","'Group name'  must contain only Alphabets and Numbers.!");
		    }

		    DivisionGroup model = new DivisionGroup();
		    model.setGroupCode(groupCode);
		    model.setGroupName(groupName);
		    model.setTDId(tdIdStr);  // Assuming TDID is a String
		    model.setGroupHeadId(Long.parseLong(ghempidStr));
		    model.setGroupId(Long.parseLong(groupidStr));                 
		    model.setIsActive(Integer.parseInt(isActiveStr));
		    model.setModifiedBy(Userid);    

		    int count = service.GroupMasterUpdate(model);

		    if(count > 0 ) {
		        redir.addAttribute("result", "Group Edited Successfully");
		    }
		    else {
		        redir.addAttribute("result", "Group Edit Unsuccessful");
		    }
		}
		catch(Exception e){
		    e.printStackTrace();
		    redir.addAttribute("resultfail", "Technical Issue");
		    logger.error(new Date() +" Inside GroupMasterEditSubmit.htm "+Userid , e);
		}


		return "redirect:/GroupMaster.htm";
	}



	@RequestMapping(value = "groupAddCheck.htm", method = RequestMethod.GET)
	public @ResponseBody String DivisionAddCheck(HttpSession ses, HttpServletRequest req) throws Exception 
	{
		String UserId=(String)ses.getAttribute("Username");
		Object[] DisDesc = null;
		logger.info(new Date() +"Inside groupAddCheck.htm "+UserId);
		try
		{	  
			String gCode=req.getParameter("gcode");
			DisDesc =service.GroupAddCheck(gCode);
		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside groupAddCheck.htm "+UserId,e);
		}
		Gson json = new Gson();
		return json.toJson(DisDesc); 
	}

	@RequestMapping (value= "LabDetails.htm", method= RequestMethod.GET)
	public String LabMasterList(HttpServletRequest req,HttpSession ses) throws Exception{

		String UserId = (String) ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +" Inside LabDetails.htm " +  UserId );			
		req.setAttribute("labmasterdata", service.LabMasterList().stream().filter(e-> LabCode.equalsIgnoreCase(e[1].toString())).collect(Collectors.toList()) );			
		return "master/LabDetails";
	}
	@RequestMapping (value="LabDetails.htm" , method= RequestMethod.POST)
	public String LabMasterAddEdit(HttpServletResponse res, HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception{

		String Option =req.getParameter("sub");
		String LabId= req.getParameter("Did");
		String Userid= (String) ses.getAttribute("Username");

		logger.info(new Date() +" Inside LabDetails.htm "+Userid);

		try {
			if(Option.equalsIgnoreCase("add")) {

				req.setAttribute("employeelist", service.EmployeeList());
				return "master/LabMasterAdd";
			}

			else if(Option.equalsIgnoreCase("edit")) {

				req.setAttribute("LabMasterEditData", service.LabMasterEditData(LabId).get(0));
				req.setAttribute("employeelist", service.EmployeeList());
				req.setAttribute("lablist", service.LabsList() );
				return "master/LabMasterEdit";
			}
		}
		catch(Exception e){

			redir.addAttribute("resultfail", "Technical Issue");
			logger.error(new Date() +" Inside LabDetails.htm "+Userid , e);

		}

		return "redirect:/LabDetails.htm";
	}


	@RequestMapping(value="LabMasterEditSubmit.htm" , method= RequestMethod.POST)
	public String LabMasterEdit (HttpServletResponse res, HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception 
	{		
		String Userid= (String) ses.getAttribute("Username");
		logger.info(new Date() +" Inside LabMasterEditSubmit.htm "+Userid);

		try {
			LabMasterAdd labmasteredit = new LabMasterAdd();

			labmasteredit.setLabCode(req.getParameter("LabCode").trim());
			labmasteredit.setLabName(req.getParameter("LabName"));
			labmasteredit.setLabUnitCode(req.getParameter("LabUnitCode"));
			labmasteredit.setLabAddress(req.getParameter("LabAddress"));
			labmasteredit.setLabCity(req.getParameter("LabCity"));
			labmasteredit.setLabPin(req.getParameter("LabPin"));
			labmasteredit.setLabMasterId(req.getParameter("LabMasterId"));
			labmasteredit.setLabTelephone(req.getParameter("LabTelephone"));
			labmasteredit.setLabFaxNo(req.getParameter("LabFaxNo"));
			labmasteredit.setLabEmail(req.getParameter("LabEmail"));
			labmasteredit.setLabAuthority(req.getParameter("LabAuthority"));
			labmasteredit.setLabAuthorityId(req.getParameter("LabAuthorityName"));
			labmasteredit.setLabRfpEmail(req.getParameter("LabRFPEmail"));
			String[] labid=req.getParameter("labid").split(",");
			labmasteredit.setLabId(labid[0]);
			labmasteredit.setClusterId(labid[1]);
			int count = service.LabMasterUpdate(labmasteredit,Userid);

			if(count > 0) {
				redir.addAttribute("result" , "Lab Details Edited Successfully ");
			}
			else {
				redir.addAttribute("resultfail", "Lab Details Edit Unsuccessful");
			}
		}
		catch (Exception e)
		{				
			e.printStackTrace();
			redir.addAttribute("resultfail", "Technical Issue");
			logger.error(new Date() +" Inside LabMasterEditSubmit.htm "+Userid , e);				
		}			
		return "redirect:/LabDetails.htm";
	}

	@RequestMapping(value = "FeedBack.htm", method = RequestMethod.GET)
	public String FeedBack(HttpServletRequest req, HttpSession ses) throws Exception {
		String Userid= (String) ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		String logintype=(String)ses.getAttribute("LoginType");
		logger.info(new Date() +" Inside FeedBack.htm "+Userid);
		req.setAttribute("fromdate", new FormatConverter().SqlToRegularDate( LocalDate.now().minusMonths(1).toString()));
		req.setAttribute("todate",new FormatConverter().SqlToRegularDate( LocalDate.now().toString()));
		if(logintype!=null && logintype.equalsIgnoreCase("A")) {
			List<Object[]> list = service.FeedbackListForUser(LabCode,"A");
			List<Object[]> Attch = service.GetfeedbackAttch(); 
			if(list!=null && list.size()>0) {
				req.setAttribute("FeedbackList", list);
				req.setAttribute("Attchment", Attch);
				req.setAttribute("feedbacktype", "A");
				return "master/FeedbackList";
			}else {
				req.setAttribute("FeedbackList", list);
				return "master/FeedBack";
			}
		}else {
			String empid =  String.valueOf((Long) ses.getAttribute("EmpId"));
			List<Object[]> list = service.FeedbackListForUser(LabCode , empid);
			List<Object[]> Attch = service.GetfeedbackAttchForUser( empid); 
			if(list!=null && list.size()>0) {
				req.setAttribute("FeedbackList", list);
				req.setAttribute("Attchment", Attch);
				req.setAttribute("feedbacktype", "A");
				return "master/FeedbackList";
			}else {
				req.setAttribute("FeedbackList", list);
				return "master/FeedBack";
			}
		}

	}

	@RequestMapping(value = "FeedbackAttachDownload.htm", method = RequestMethod.GET)
	public void FeedbackAttachDownload(HttpServletRequest	 req, HttpSession ses, HttpServletResponse res) throws Exception 
	{	 
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside ActionAttachDownload.htm "+UserId);		
		try { 

			res.setContentType("Application/octet-stream");	
			System.out.println(req.getParameter("attachid"));
			PfmsFeedbackAttach attach=service.FeedbackAttachmentDownload(req.getParameter("attachid"));

			File my_file=null;

			my_file = new File(uploadpath+attach.getPath()+File.separator+attach.getFileName()); 
			res.setHeader("Content-disposition","attachment; filename="+attach.getFileName().toString()); 
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


	@RequestMapping(value = "FeedBackPage.htm", method = RequestMethod.GET)
	public String FeedBackpage(HttpServletRequest req, HttpSession ses) throws Exception {
		String Userid= (String) ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		String logintype=(String)ses.getAttribute("LoginType");
		logger.info(new Date() +" Inside FeedBack.htm "+Userid);
		if(logintype!=null && logintype.equalsIgnoreCase("A")) {
			List<Object[]> list = service.FeedbackListForUser(LabCode,"A");
			req.setAttribute("FeedbackList", list);
		}else {
			String empid =  String.valueOf((Long) ses.getAttribute("EmpId"));
			List<Object[]> list = service.FeedbackListForUser(LabCode , empid);
			req.setAttribute("FeedbackList", list);
		}
		return "master/FeedBack";

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


	@RequestMapping(value = "FeedBackAdd.htm", method = RequestMethod.POST)
	public String FeedBackAdd(Model model,HttpServletRequest req, HttpSession ses,RedirectAttributes redir,
			@RequestPart("FileAttach") MultipartFile FileAttach)	throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +" Inside FeedBackAdd.htm "+UserId);
		try {
			Long EmpId = (Long) ses.getAttribute("EmpId");
			String Feedback=req.getParameter("Feedback");
			String feedbacktype=req.getParameter("feedbacktype");
			
			// 🔹 Validate file types
	        if (!isValidFileType(FileAttach) ) {

	            return redirectWithError(redir, "FeedBack.htm",
	                    "Invalid file type. Only PDF or Image files are allowed.");
	        }
			
			
			
			
			
			
			
			if(!FileAttach.isEmpty()){

			}
			
			
			
			if(Feedback ==null || Feedback.trim().equalsIgnoreCase("")) {			
				redir.addAttribute("resultfail", "Feedback Field is Empty, Please Enter Feedback");
				return "redirect:/FeedBack.htm";
			}
			if(feedbacktype==null) {
				redir.addAttribute("resultfail", "Please Select the FeedbackType");
				return "redirect:/FeedBack.htm";
			}
			
			
			
			
			
			PfmsFeedback feedback=new PfmsFeedback();
			feedback.setEmpId(EmpId);
			feedback.setStatus("O");
			feedback.setFeedbackType(feedbacktype);
			feedback.setFeedback(Feedback);
			feedback.setCreatedBy(UserId);
			feedback.setCreatedDate(sdf1.format(new Date()));
			feedback.setIsActive(1);

			Long Feedbackid=service.FeedbackInsert(feedback , FileAttach,LabCode);

			if (Feedbackid>0) {
				addPfmsFeedbackTrans(Feedbackid, Feedback, EmpId);
				redir.addAttribute("result", " Feedback Add Successful");
			} else {
				redir.addAttribute("resultfail", "Feedback Add UnSuccessful");
			}
		} catch (Exception e) {
			e.printStackTrace();
			redir.addAttribute("resultfail", "Feedback Add UnSuccessful");
		}

		return "redirect:/FeedBack.htm";
	}



	@RequestMapping(value = "FeedbackList.htm" , method= {RequestMethod.GET,RequestMethod.POST})
	public String FeedbackList(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +" Inside FeedbackList.htm "+UserId);

		try {
			String LabCode =(String) ses.getAttribute("labcode");
			String empid =  String.valueOf((Long) ses.getAttribute("EmpId"));
			String logintype=(String)ses.getAttribute("LoginType");
			String fromdate = req.getParameter("Fromdate");
			String todate = req.getParameter("Todate");
			String feedbacktype = req.getParameter("feedbacktype");
			if(feedbacktype==null) {
				feedbacktype="A";
			}
			if(logintype!=null && logintype.equalsIgnoreCase("A")) {
				List<Object[]> Attch = service.GetfeedbackAttch(); 
				req.setAttribute("FeedbackList", service.FeedbackList(fromdate,todate,"A" ,LabCode,feedbacktype));
				req.setAttribute("Attchment", Attch);
			}else {
				List<Object[]> Attch = service.GetfeedbackAttchForUser( empid); 
				req.setAttribute("FeedbackList", service.FeedbackList(fromdate,todate,empid,LabCode,feedbacktype));
				req.setAttribute("Attchment", Attch);
			}
			req.setAttribute("fromdate", fromdate);
			req.setAttribute("todate", todate);
			req.setAttribute("feedbacktype", feedbacktype);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +"Inside FeedbackList.htm "+UserId,e);
		}
		return "master/FeedbackList";
	}

	@RequestMapping(value = "FeedbackContent.htm", method = RequestMethod.GET)
	public @ResponseBody String FeedbackContent(HttpSession ses, HttpServletRequest req) throws Exception 
	{
		String UserId=(String)ses.getAttribute("Username");
		Object[] arr = null;
		logger.info(new Date() +"Inside FeedbackContent.htm "+UserId);
		try
		{	  
			String feedbackid=req.getParameter("feedbackid");					
			arr= service.FeedbackContent(feedbackid);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +"Inside FeedbackContent.htm "+UserId,e);
		}
		Gson json = new Gson();
		return json.toJson(arr); 

	}

	@RequestMapping( value = "FeedbackTransactionSubmit.htm" , method = RequestMethod.POST)
	public String feedbackTransactionSubmit(HttpSession ses, HttpServletRequest req ,RedirectAttributes redir)throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		Long EmpId = (Long) ses.getAttribute("EmpId");
		logger.info(new Date() +"Inside FeedbackTransactionSubmit.htm "+UserId);
		try {
			String feedbackid = req.getParameter("feedbackid");
			String remarks = req.getParameter("Remarks");
			String sub = req.getParameter("sub");
			
			PfmsFeedback feedback = service.getPfmsFeedbackById(feedbackid);
			feedback.setRemarks(remarks);
			if(sub.equalsIgnoreCase("Close")) {
				feedback.setStatus("C");
			}
			feedback.setModifiedBy(UserId);
			feedback.setModifiedDate(sdtf.format(new Date()));
			
			long result = service.addPfmsFeedback(feedback);
			
			if(result > 0) {
				addPfmsFeedbackTrans(Long.parseLong(feedbackid), remarks, EmpId);
				redir.addAttribute("result" , "Feedback Submitted Successfully ");
			}else {
				redir.addAttribute("resultfail", "Feedback Submit Unsuccessful");
			}
			return "redirect:/FeedBack.htm";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +"Inside FeedbackTransactionSubmit.htm "+UserId,e);
			return "static/Error";
		}
		
	}
	
	@RequestMapping( value = "CloseFeedBack.htm" , method = RequestMethod.POST)
	public String CloseFeedback(HttpSession ses, HttpServletRequest req ,RedirectAttributes redir)throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CloseFeedBack.htm "+UserId);
		try {
			String feedbackId = req.getParameter("feedbackId");
			//String remarks = req.getParameter("Remarks");
			
			PfmsFeedback feedback = service.getPfmsFeedbackById(feedbackId);
			//feedback.setRemarks(remarks);
			feedback.setStatus("C");
			feedback.setModifiedBy(UserId);
			feedback.setModifiedDate(sdtf.format(new Date()));
			
			long result = service.addPfmsFeedback(feedback);
			
			if(result > 0) {
				redir.addAttribute("result" , "Feedback Closed Successfully ");
			}else {
				redir.addAttribute("resultfail", "Feedback Close Unsuccessful");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +"Inside CloseFeedBack.htm "+UserId,e);
		}
		return "redirect:/FeedBack.htm";
	}

	@RequestMapping(value="TDMaster.htm", method={RequestMethod.GET, RequestMethod.POST})
	public String TDMaster(Model model, HttpServletRequest req, HttpServletResponse res, HttpSession ses, RedirectAttributes redir) throws Exception {

	    String UserId = (String) ses.getAttribute("Username");
	    String LabCode = (String) ses.getAttribute("labcode");
	    String Option = req.getParameter("sub"); 

	    logger.info(new Date() + " Inside TDMaster.htm " + UserId);

	    try {
	        if (Option == null) {
	            
	            model.addAttribute("TDList", service.TDList(LabCode));
	            return "master/TDList";
	        } else if ("add".equalsIgnoreCase(Option)) {
	           
	            model.addAttribute("tdheadlist", service.TDHeadList(LabCode));
	            return "master/TDMasterAdd";
	        } else if ("edit".equalsIgnoreCase(Option)) {
	           
	            String tdid = req.getParameter("tdid");
	            model.addAttribute("tdsdata", service.TDsData(tdid));
	            model.addAttribute("tdheadlist", service.TDHeadList(LabCode));
	            return "master/TDMasterEdit";
	        }
	    } catch (Exception e) {
	    	e.printStackTrace();
	        redir.addAttribute("resultfail", "Technical Issue");
	        logger.error(new Date() + " Inside TDMaster.htm " + UserId, e);
	    }

	    return "redirect:/TDMaster.htm";
	}

	@RequestMapping(value = "TDMasterAddSubmit.htm",method=RequestMethod.POST )
	public String TDMasterAddSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		String LabCode= (String)ses.getAttribute("labcode");

		logger.info(new Date() +"Inside TDMasterAddSubmit.htm "+UserId);	


		try {				
			String tdCode=req.getParameter("tCode");
			String tdName=req.getParameter("tName");
			String tdHeadName=req.getParameter("tdempid");
			
			if(!InputValidator.isValidCapitalsAndSmallsAndNumeric(tdCode)) {
				 redir.addAttribute("sub", "add");
				return redirectWithError(redir, "TDMaster.htm", "'TD Code' should contain Alphabets and Numbers.!");
			}
			if(!InputValidator.isValidCapitalsAndSmallsAndNumericAndSpace(tdName)) {
				redir.addAttribute("sub", "add");
				return redirectWithError(redir, "TDMaster.htm", "'TD Name' should contain Alphabets and Numbers.!");
			}

			DivisionTd dtd=new DivisionTd();
			dtd.setTdCode(tdCode);
			dtd.setTdName(tdName);
			dtd.setTdHeadId(Long.parseLong(tdHeadName));
			dtd.setCreatedBy(UserId);
			dtd.setLabCode(LabCode);

			long count=service.TDAddSubmit(dtd);
			if (count > 0) {
				redir.addAttribute("result", "TD Added Successfully");
			} else {
				redir.addAttribute("resultfail", "TD Adding Unsuccessfully");
			}

			return "redirect:/TDMaster.htm";
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside TDMasterAddSubmit.htm "+UserId, e);
			return "static/Error";
		}
	}


	@RequestMapping(value = "tdAddCheck.htm", method = RequestMethod.GET)
	public @ResponseBody String TDAddCheck(HttpSession ses, HttpServletRequest req) throws Exception 
	{
		String UserId=(String)ses.getAttribute("Username");
		Object[] TDCheck = null;
		logger.info(new Date() +"Inside tdAddCheck.htm "+UserId);
		try
		{	  
			String tCode=req.getParameter("tcode");
			TDCheck =service.TDAddCheck(tCode);
		}
		catch (Exception e) {
			e.printStackTrace(); logger.error(new Date() +"Inside tdAddCheck.htm "+UserId,e);
		}
		Gson json = new Gson();
		return json.toJson(TDCheck); 
	}
	
	@RequestMapping(value = "TDMasterEditSubmitCheck.htm", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> checkActiveStatus(HttpServletRequest req, HttpSession ses) {
		
	    String userId = (String) ses.getAttribute("Username");
	    Map<String, Object> response = new HashMap<>();
	    String tdCode = req.getParameter("tdcode");
	    Integer isActive = Integer.valueOf(req.getParameter("isActive"));
	    logger.info(new Date() + " Inside TDMasterEditSubmit.htm " + userId);
	    
	    try {
	        List<Object[]> alertGroupMaster = service.checkGroupMasterCode(tdCode);
	        
	        if (alertGroupMaster != null && !alertGroupMaster.isEmpty()) {
	            for (Object[] obj : alertGroupMaster) {
	                Integer isActivedb = (Integer) obj[1];
	                
	                if (isActivedb.equals(isActive)) {
	                    response.put("valid", true);
	                } else {
	                    response.put("valid", false);
	                    response.put("message", "TD Code- '" + tdCode + "' cannot be made inactive as it exists in Group Master (Group Name- '"+obj[2]+"' )");
	                }
	            }
	        } else {
	            response.put("valid", true);
	        }
	    } catch (Exception e) {
	        response.put("valid", false);
	        response.put("message", "Error validating status");
	        logger.error("Error in TDMasterEditSubmitCheck", e);
	    }
	    
	    return response;
	}


	@RequestMapping (value="TDMasterEditSubmit.htm" , method=RequestMethod.POST)
	public String TDMasterEditSubmit(HttpServletRequest req, HttpServletResponse res, HttpSession ses, RedirectAttributes redir) throws Exception
	{

		String Userid = (String) ses.getAttribute("Username");

		logger.info(new Date() +" Inside TDMasterEditSubmit.htm "+Userid );
		 String tdid = req.getParameter("tdid");
		try {
				String tdCode=req.getParameter("tdcode");
				String tdName=req.getParameter("tdname");
				if(!InputValidator.isValidCapitalsAndSmallsAndNumeric(tdCode)) {
					redir.addAttribute("tdid", tdid);
					redir.addAttribute("sub", "edit");
					return redirectWithError(redir, "TDMaster.htm", "'TD Code' should contain only Alphabets and Numbers.!");
				}
				if(!InputValidator.isValidCapitalsAndSmallsAndNumericAndSpace(tdName)) {
					redir.addAttribute("tdid", tdid);
					redir.addAttribute("sub", "edit");
					return redirectWithError(redir, "TDMaster.htm", "'TD Name' should contain only Alphabets and Numbers.!");
				}
			DivisionTd model= new DivisionTd();
			model.setTdCode(tdCode);
			model.setTdName(tdName);
			model.setTdHeadId(Long.parseLong(req.getParameter("tdempid")));
			model.setTdId(Long.parseLong(req.getParameter("tdid")));                 
			model.setIsActive(Integer.valueOf(req.getParameter("isActive")));
			model.setModifiedBy(Userid);    

			int count = service.TDMasterUpdate(model);

			if(count > 0 ) {
				redir.addAttribute("result", "TD Edited Successfully");
			}

			else {
				redir.addAttribute("result ", "TD Edit Unsuccessful");
			}

		}
		catch(Exception e){

			e.printStackTrace();
			redir.addAttribute("resultfail", "Technical Issue");
			logger.error(new Date() +" Inside TDMasterEditSubmit.htm "+Userid , e);
		}

		return "redirect:/TDMaster.htm";
	}


	@RequestMapping(value = "ActivityTypeEdit.htm", method = { RequestMethod.POST })
	public String ActivityTypeEdit(HttpServletRequest req,HttpServletResponse res, RedirectAttributes redir)throws Exception 
	{
		System.out.println("in ActivityTypeEdit");
		logger.info(new Date() +"Inside ActivityTypeEdit.htm ");
		try {
			boolean flag=req.getParameter("Delete")==null?false:true;
			
			String ActivityID = req.getParameter("ActivityID");
			int result = 0;
			if(flag && ActivityID!=null) {
				result = service.DeleteActivityType(ActivityID); // only Deletes, flag is true
			}else{	// else updates
				String toActivity = req.getParameter("toActivity");
				String activityCode = req.getParameter("activityCode");				
				if(!InputValidator.isValidCapitalsAndSmallsAndNumericAndSpace(toActivity)) {
					return redirectWithError(redir,"MilestoneActivityTypes.htm","Activity type must contain letters, numbers, and spaces only.");
				}
				if(!InputValidator.isValidCodeWithCapitalsAndNumeric(activityCode)){
					return redirectWithError(redir,"MilestoneActivityTypes.htm","Activity code must contain only uppercase letters and numbers.");
				}
				result = service.UpdateActivityType(toActivity, ActivityID, req.getParameter("isTimeSheet"), activityCode );
			}
			
			if (result > 0) {
				redir.addAttribute("result", flag?"Deleted Activity Type Succesfully":"Edited Activity Type Succesfully");
			} else {
				redir.addAttribute("resultfail", flag?"Deleted Activity Type Unsuccessful":"Edited Activity Type Unsuccessful");
			}
			
			return "redirect:/MilestoneActivityTypes.htm";		
		}catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +" Inside ActivityTypeEdit.htm ", e); 
			return "static/Error";
		}
	}


	@RequestMapping(value="IndustryPartner.htm")
	public String industryPartner(final HttpServletRequest req, final HttpSession ses, final RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside IndustryPartner.htm " + UserId);
		try {
			String action = req.getParameter("Action");
			if(action!=null && action.equalsIgnoreCase("Add")) {
				req.setAttribute("IndustryNamesList", service.getIndustryPartnerList());
				
				return "master/IndustryPartnerAddEdit";
				
			}else if(action!=null && action.equalsIgnoreCase("Edit")) {
				String industryPartnerRepId = req.getParameter("industryPartnerRepId");
				req.setAttribute("IndustryPartnerDetails", service.industryPartnerDetailsByIndustryPartnerRepId(industryPartnerRepId));
				req.setAttribute("IndustryNamesList", service.getIndustryPartnerList());
				
				return "master/IndustryPartnerAddEdit";
				
			}else {
				//req.setAttribute("IndustryPartnerList", service.industryPartnerList());
				
				String pagination = req.getParameter("pagination");
				String slno = req.getParameter("slno");
				int pagin = Integer.parseInt(pagination!=null?pagination:"0");
				int sln = Integer.parseInt(slno!=null?slno:"0");

				/* fetching actual data */
				List<Object[]> IndustryPartnerList = service.industryPartnerList();

				// adding values to this List<Object[]>
				List<Object[]> arrayList = new ArrayList<>();

				/* search action starts */
				String search = req.getParameter("search");
				if(search!="" && search!=null) {
					req.setAttribute("searchFactor", search);
					for(Object[] obj: IndustryPartnerList) {
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
				int p = IndustryPartnerList.size()/10;
				int extra = IndustryPartnerList.size()%10;
				if(arrayList.size()==0) arrayList=IndustryPartnerList;

				/* pagination process starts */
				System.out.println(sln);
				if(pagin>0 && pagin<(p+(extra>0?1:0)))
				{
					req.setAttribute("pagination", pagin);
					req.setAttribute("slno", sln);
					arrayList = arrayList.subList(pagin*10, ((pagin*10)+10)<arrayList.size()?((pagin*10)+10):arrayList.size());
				}
				else
				{
					arrayList = arrayList.subList(0, arrayList.size()>=10?10:arrayList.size());
					req.setAttribute("pagination", 0);
					req.setAttribute("slno", 0);
					pagin=0;
				}

				req.setAttribute("IndustryPartnerList", arrayList);
				req.setAttribute("maxpagin", p+(extra>0?1:0) );
				/* pagination process ends */
				
				return "master/IndustryPartnerList";
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside IndustryPartner.htm " + UserId, (Throwable) e);
			return "static/Error"; 
		}
		
	}

	@RequestMapping(value="IndustryPartnerSubmit.htm")
	public String industryPartnerSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside IndustryPartnerSubmit.htm " + UserId);
		try {
			
			
			if(InputValidator.isContainsHTMLTags(req.getParameter("ProgrammeName"))) {
				return  redirectWithError(redir,"ProgrammeMaster.htm","ProgrammeName should not contain HTML elements !");
			}
			String action = req.getParameter("Action");
			String industryPartnerId = req.getParameter("industryPartnerId");
			industryPartnerId = industryPartnerId!=null?industryPartnerId : req.getParameter("industryPartnerId2");
			
			boolean addnew = industryPartnerId!=null && industryPartnerId.equalsIgnoreCase("addNew")?true:false;
			IndustryPartner partner = addnew ? new IndustryPartner() : service.getIndustryPartnerById(industryPartnerId);
			if(addnew || action.equalsIgnoreCase("Edit")) {
				String industryPartnerName2 = req.getParameter("industryPartnerName2");
				String industryPartnerName = req.getParameter("industryPartnerName");
				
				if((industryPartnerName2!=null && !industryPartnerName2.isEmpty() && !InputValidator.isValidCapitalsAndSmallsAndNumericAndSpace(industryPartnerName2)) ||
				  (industryPartnerName!=null && !industryPartnerName.isEmpty() &&  !InputValidator.isValidCapitalsAndSmallsAndNumericAndSpace(industryPartnerName))	) {
					return redirectWithError(redir,"IndustryPartner.htm","Name can contain only letters, numbers and spaces.");
				}
				partner.setIndustryName(addnew?industryPartnerName2:industryPartnerName);
			}
			String industryPartnerAddress2 = req.getParameter("industryPartnerAddress2");
			String industryPartnerAddress = req.getParameter("industryPartnerAddress");
			String industryPartnerCity2 = req.getParameter("industryPartnerCity2");
			String industryPartnerCity = req.getParameter("industryPartnerCity");
			String industryPartnerPinCode2 = req.getParameter("industryPartnerPinCode2");
			String industryPartnerPinCode = req.getParameter("industryPartnerPinCode");
			
			if((industryPartnerAddress2!=null && !industryPartnerAddress2.isEmpty() && InputValidator.isContainsHTMLTags(industryPartnerAddress2)) ||
			   (industryPartnerAddress!=null && !industryPartnerAddress.isEmpty() && InputValidator.isContainsHTMLTags(industryPartnerAddress))	) {
						return redirectWithError(redir,"IndustryPartner.htm","HTML tags are not allowed in the address.");
			}
			if((industryPartnerCity2!=null && !industryPartnerCity2.isEmpty() && !InputValidator.isValidNameWithCapitalsAndSmallLettersAndSpace(industryPartnerCity2)) ||
			   (industryPartnerCity!=null && !industryPartnerCity.isEmpty() && !InputValidator.isValidNameWithCapitalsAndSmallLettersAndSpace(industryPartnerCity))	) {
						return redirectWithError(redir,"IndustryPartner.htm","The city name must contain only letters and spaces.");
			}
			if((industryPartnerPinCode2!=null && !industryPartnerPinCode2.isEmpty() && !InputValidator.isContainsNumberOnly(industryPartnerPinCode2)) ||
			   (industryPartnerPinCode!=null && !industryPartnerPinCode.isEmpty() &&   !InputValidator.isContainsNumberOnly(industryPartnerPinCode)) ) {
						return redirectWithError(redir,"IndustryPartner.htm","The PIN code must contain numbers only.");
			}
			
			partner.setIndustryAddress(addnew?industryPartnerAddress2:industryPartnerAddress);
			partner.setIndustryCity(addnew?industryPartnerCity2:industryPartnerCity);
			partner.setIndustryPinCode(addnew?industryPartnerPinCode2:industryPartnerPinCode);
			
			if(addnew) {
				partner.setCreatedBy(UserId);
				partner.setCreatedDate(sdf1.format(new Date()));
				partner.setIsActive(1);
				
			}else {
				partner.setModifiedBy(UserId);
				partner.setModifiedDate(sdf1.format(new Date()));
			}
			
			String industryPartnerRepId = req.getParameter("industryPartnerRepId");
			List<IndustryPartnerRep> partnerReplist = new ArrayList<>();
			
			String[] repName = req.getParameterValues("repName");
			String[] repDesignation = req.getParameterValues("repDesignation");
			String[] repMobileNo = req.getParameterValues("repMobileNo");
			String[] repEmail = req.getParameterValues("repEmail");
			
			for(int i=0;i<repName.length;i++) {
				IndustryPartnerRep partnerRep = action!=null && action.equalsIgnoreCase("Add")? new IndustryPartnerRep() : service.getIndustryPartnerRepById(industryPartnerRepId);
				
				if(!InputValidator.isValidNameWithCapitalsAndSmallLettersAndDotAndSpace(repName[i])) {
							return redirectWithError(redir,"IndustryPartner.htm","Name must contain only letters, dots, and spaces.");
				}
				if(!InputValidator.isValidCapitalsAndSmallsAndNumericAndSpace(repDesignation[i])) {
					return redirectWithError(redir,"IndustryPartner.htm","Designation can contain only letters, numbers and spaces.");
				}
				if(!InputValidator.isValidMobileNo(repMobileNo[i])) {
					return redirectWithError(redir,"IndustryPartner.htm","Please enter a valid 10-digit mobile number starting with 6-9.");
				}
				if(!InputValidator.isValidEmail(repEmail[i])) {
					return redirectWithError(redir,"IndustryPartner.htm","The email address you entered is not valid. Please try again.");
				}
				
				partnerRep.setRepName(repName[i]);
				partnerRep.setRepDesignation(repDesignation[i]);
				partnerRep.setRepMobileNo(repMobileNo[i]);
				partnerRep.setRepEmail(repEmail[i]);
				if(action!=null && action.equalsIgnoreCase("Add")) {
					partnerRep.setCreatedBy(UserId);
					partnerRep.setCreatedDate(sdf1.format(new Date()));
					partnerRep.setIsActive(1);
				}else {
					partnerRep.setModifiedBy(UserId);
					partnerRep.setModifiedDate(sdf1.format(new Date()));
				}
				partnerRep.setIndustryPartner(partner);
				partnerReplist.add(partnerRep);
			}
			
			partner.setRep(partnerReplist);
			
			long result = service.addIndustryPartner(partner);
			
			if(result!=0) {
				redir.addAttribute("result", "Industry Partner Details "+action+"ed Successfully");
			}else {
				redir.addAttribute("resultfail", "Industry Partner Details "+action+" UnSuccessful");
			}
			
			return "redirect:/IndustryPartner.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside IndustryPartnerSubmit.htm " + UserId, e);
			return "static/Error"; 
		}
	}
	
	@RequestMapping(value = "IndustryPartnerRepRevoke.htm")
	public String industryPartnerRepRevoke(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside IndustryPartnerRepRevoke.htm " + UserId);
		try {
			String industryPartnerRepId = req.getParameter("industryPartnerRepId");
			int result = service.revokeIndustryPartnerRep(industryPartnerRepId);
			
			if(result!=0) {
				redir.addAttribute("result", "Industry Partner Revoked Successfully");
			}else {
				redir.addAttribute("resultfail", "Industry Partner Revoke UnSuccessful");
			}
			
			return "redirect:/IndustryPartner.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside IndustryPartnerRepRevoke.htm " + UserId, e);
			return "static/Error"; 
		}
	}
	
	@RequestMapping(value = "IndustryPartnerRepDetails.htm", method = RequestMethod.GET)
	public @ResponseBody String industryPartnerRepDetails(HttpServletRequest req,HttpSession ses) throws Exception
	{
		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside IndustryPartnerRepDetails.htm"+ UserId);
		
		List<Object[]> industryPartnerRepDetails = new ArrayList<Object[]>();
		
		String industryPartnerId =req.getParameter("industryPartnerId");
		String industryPartnerRepId =req.getParameter("industryPartnerRepId");
	
		industryPartnerRepDetails = service.industryPartnerRepDetails(industryPartnerId, industryPartnerRepId!=null?industryPartnerRepId:"0");
	 
		Gson json = new Gson();
		return json.toJson(industryPartnerRepDetails);	
	}
	
	//prakarsh----Holiday Modules--------start------
		@RequestMapping(value="HolidayList.htm", method= {RequestMethod.GET,RequestMethod.POST})
		public String HolidayList(HttpServletRequest req,HttpSession ses) {
			
			try {
				
				String yr=req.getParameter("Year");
				 if (yr == null || yr.isEmpty()) {
			           
			            yr = getCurrentYearAsString();
			        }	
				List<Object[]>HolidayList=service.HolidayList(yr);
				
				req.setAttribute("HolidayList", HolidayList);
				req.setAttribute("yr", yr);
			return "master/HolidayList";
			}catch (Exception e) {
				e.printStackTrace();
				return "static/Error"; 
			}
		}
		
		private String getCurrentYearAsString() {
		    Calendar calendar = Calendar.getInstance();
		    int year = calendar.get(Calendar.YEAR);
		    return String.valueOf(year);
		}
		
		//delete functionality
		@RequestMapping(value = "HolidayDelete.htm", method = {RequestMethod.GET,RequestMethod.POST})
		public String HolidayDelete(HttpServletRequest req, HttpSession ses, HttpServletResponse res,RedirectAttributes redir) throws Exception {
			String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside HolidayAddSubmit.htm "+UserId);		
			try {
				
				HolidayMaster holiday= new HolidayMaster();
				
				String HolidayId=req.getParameter("HolidayId");
				
				holiday.setHolidayId(Long.parseLong(HolidayId));
				
				long save=service.HolidayDelete(holiday,UserId);
				if(save>0) {
					redir.addAttribute("result", "Holiday Details Delete Successfull");
				} else {
					redir.addAttribute("resultfail", "Holiday Details Delete Unsuccessful");
				}
				return "redirect:/HolidayList.htm";
					
			 }
		     catch (Exception e) {
		    	 e.printStackTrace();
				 logger.error(new Date() +" Inside HolidayEditSumit.htm "+UserId, e);
				 return "static/Error"; 

		       }
		}
		@RequestMapping(value = "HolidayAddEdit.htm", method = {RequestMethod.GET,RequestMethod.POST})
		public String HolidayAddEdit(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception {
			String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside HolidayAddEdit.htm "+UserId);		
			try {
				
				  String Action = (String) req.getParameter("Action");
				if("edit".equalsIgnoreCase(Action)) {
					HolidayMaster holiday= service.getHolidayData(Long.parseLong(req.getParameter("HolidayId")));
					req.setAttribute("Holidaydata", holiday);
					req.setAttribute("Action", Action);
					return "master/HolidayAddEdit";
	    	   	   
	    	   	      
	           }else{
	        	   req.setAttribute("Action", Action);
	    	   	 	  return "master/HolidayAddEdit";
	    	   	}
				
		    }
		     catch (Exception e) {
		    	 e.printStackTrace();
				 logger.error(new Date() +" Inside HolidayAddEdit.htm "+UserId, e);
		       }
		   return  "master/HolidayAddEdit";

		}
		
		@RequestMapping(value = "HolidayAddSumit.htm", method = {RequestMethod.GET,RequestMethod.POST})
		public String HolidayAddSubmit(HttpServletRequest req, HttpSession ses, HttpServletResponse res,RedirectAttributes redir) throws Exception {
			String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside HolidayAddSubmit.htm "+UserId);		
			try {
				if(InputValidator.isContainsHTMLTags(req.getParameter("HoliName"))) {
					return  redirectWithError(redir,"HolidayList.htm","Holiday Name should not contain HTML elements !");
				}
				HolidayMaster holiday= new HolidayMaster();

				holiday.setHolidayName(req.getParameter("HoliName"));
				holiday.setHolidayType(req.getParameter("HoliType"));
				holiday.setHolidayDate(fc.RegularToSqlDate(req.getParameter("HoliDate")));
			
				holiday.setCreatedBy(UserId);
				holiday.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
				holiday.setIsActive(1);
				
				long save=service.HolidayAddSubmit(holiday);
				if(save>0) {
					redir.addAttribute("result", "Holiday Details Add Successfull");
				} else {
					redir.addAttribute("resultfail", "Holiday Details Add Unsuccessful");
				}
				return "redirect:/HolidayList.htm";
					
			 }catch (Exception e) {
		    	 e.printStackTrace();
				 logger.error(new Date() +" Inside HolidayAddSubmit.htm "+UserId, e);
				 return "static/Error"; 
		       }
		}
		
		@RequestMapping(value = "HolidayEditSumit.htm", method = {RequestMethod.GET,RequestMethod.POST})
		public String HolidayEditSumit(HttpServletRequest req, HttpSession ses, HttpServletResponse res,RedirectAttributes redir) throws Exception {
			String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside HolidayEditSubmit.htm "+UserId);		
			try {
				if(InputValidator.isContainsHTMLTags(req.getParameter("HoliName"))) {
					return  redirectWithError(redir,"HolidayList.htm","Holiday Name should not contain HTML elements !");
				}
				HolidayMaster holiday= new HolidayMaster();
				
				String HolidayId=req.getParameter("HolidayId");
				holiday.setHolidayId(Long.parseLong(HolidayId));
				holiday.setHolidayName(req.getParameter("HoliName"));
				holiday.setHolidayType(req.getParameter("HoliType"));
				holiday.setHolidayDate(fc.RegularToSqlDate(req.getParameter("HoliDate")));
				holiday.setCreatedBy(UserId);
				holiday.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
				holiday.setIsActive(1);
				
				long save=service.HolidayEditSubmit(holiday,UserId);
				if(save>0) {
					redir.addAttribute("result", "Holiday Details Update Successfull");
				} else {
					redir.addAttribute("resultfail", "Holiday Details Update Unsuccessful");
				}
				return "redirect:/HolidayList.htm";
					
			 } catch (Exception e) {
		    	 e.printStackTrace();
				 logger.error(new Date() +" Inside HolidayEditSumit.htm "+UserId, e);
				 return "static/Error"; 

		       }
		}
		
		
		@RequestMapping(value = "LabPmsEmployee.htm", method = {RequestMethod.GET,RequestMethod.POST})
		public String ImmsPmsEmployee(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception {
			String UserId = (String) ses.getAttribute("Username");
			String LabCode =(String) ses.getAttribute("labcode");
			logger.info(new Date() +"Inside LabPmsEmployee.htm"+UserId);		
			try {
				List<Object[]> labPmsEmployeeList=service.labPmsEmployeeList(LabCode);
				req.setAttribute("labPmsEmployeeList", labPmsEmployeeList);
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside LabPmsEmployee.htm"+UserId, e);
			}
			return  "master/labpmsemployee";
			
		}
		
		@RequestMapping(value = "LabPmsEmployeeUpdate.htm", method = {RequestMethod.GET,RequestMethod.POST})
		public String ImmsPmsEmployeeUpdate(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
			String UserName = (String) ses.getAttribute("Username");
			String LabCode =(String) ses.getAttribute("labcode");
			logger.info(new Date() + "Inside LabPmsEmployeeUpdate.htm" + UserName);
			long status=0;
			try 
			{
				String[] LabPmsEmpId = req.getParameterValues("LabPmsEmpId");
				System.out.println("LabPmsEmpId*****"+LabPmsEmpId);
				if(LabPmsEmpId!=null)
				{
					status=service.LabPmsEmployeeUpdate(LabPmsEmpId,UserName,LabCode);
				}
				
				if(status>0) 
				{
					redir.addAttribute("status", "Employee Details Updates Succesfully.. &#128077;");
				}
				else
				{
					redir.addAttribute("failure", "Something Went Wrong..!");
				}
				
				return "redirect:/LabPmsEmployee.htm";
			} 
			catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + " Inside LabPmsEmployeeUpdate" + e);
				return "error";
			}
			
		}
		@RequestMapping(value = "EmployeeRoleList.htm", method = {RequestMethod.GET,RequestMethod.POST})
		public String EmployeeRoleList(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
			String UserName = (String) ses.getAttribute("Username");
			String LabCode =(String) ses.getAttribute("labcode");
			logger.info(new Date() + "Inside EmployeeRoleList.htm" + UserName);
			long status=0;
			try 
			{
				req.setAttribute("LabList", service.LabList());
				
				
				List<Object[]>empList = service.getEmployees();
				System.out.println(empList.size());
				String labcode = req.getParameter("labcode")!=null  ?req.getParameter("labcode"):LabCode;
				
				if(labcode.equalsIgnoreCase("@EXP")) {
					empList = empList.stream().filter(e->e[6].toString().equalsIgnoreCase("E")).collect(Collectors.toList());
						
				}else {
					
					empList = empList.stream().filter(e->e[2].toString().equalsIgnoreCase(labcode)).collect(Collectors.toList());
				
				}
				req.setAttribute("empList", empList);
				req.setAttribute("labcode", labcode);
				
				
				return  "master/EmployeeRoleList";
			} 
			catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + " Inside LabPmsEmployeeUpdate" + e);
				return "error";
			}
			
		}
		
		@RequestMapping(value = "EmployeeRoleAdd.htm", method = {RequestMethod.GET,RequestMethod.POST})
		public String EmployeeRoleAdd(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
			String UserName = (String) ses.getAttribute("Username");
			String LabCode =(String) ses.getAttribute("labcode");
			logger.info(new Date() + "Inside EmployeeRoleAdd.htm" + UserName);
			long status=0;
			try 
			{
				String labcode = req.getParameter("labcode")!=null  ?req.getParameter("labcode"):LabCode;
				redir.addAttribute("labcode", labcode);
			
				
				String roleid = req.getParameter("roleid");
				
				PfmsEmpRoles pf =!roleid.equalsIgnoreCase("0")?service.getPfmsEmpRolesById(roleid):new PfmsEmpRoles();
				
				pf.setEmpNo(req.getParameter("empno"));
				pf.setEmpRole(req.getParameter("role"));
				pf.setOrganization(req.getParameter("Organization"));
				if(roleid.equalsIgnoreCase("0")) {
				pf.setCreatedBy(UserName);
				pf.setCreatedDate(LocalDate.now().toString());
				pf.setIsActive(1);
				}else {
					pf.setRoleId(Long.parseLong(roleid));
					pf.setModifiedBy(UserName);
					pf.setModifiedDate(LocalDate.now().toString());
				}
				
				long count = service.addPfmsEmpRoles(pf);
				
				if(count>0) {
					if(roleid.equalsIgnoreCase("0")) {
						redir.addAttribute("result","Role added Successfully for Empno "+req.getParameter("empno"));
					}else {
						redir.addAttribute("result","Role updated Successfully for Empno "+req.getParameter("empno"));

					}
					
					
				}else {
					redir.addAttribute("resultfail", "Something Went Wrong..!");
				}
				
				
				
				
				return "redirect:/EmployeeRoleList.htm";
			} 
			catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + " Inside " + e);
				return "error";
			}
			
		}
		
	@RequestMapping(value="FeedbackTransaction.htm", method = {RequestMethod.POST, RequestMethod.GET})
	public String feedbackTransaction(HttpServletRequest req, HttpSession ses) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date()+ " Inside FeedbackTransaction.htm "+UserId);	
		try {
			String feedbackId = req.getParameter("feedbackId");
			req.setAttribute("transactionList", service.getFeedbackTransByFeedbackId(feedbackId));
			req.setAttribute("feedBackData", service.FeedbackContent(feedbackId));
			return "master/FeedbackTransaction";
		}catch (Exception e) {
			logger.error(new Date() +" Inside FeedbackTransaction.htm "+UserId, e);
			e.printStackTrace();
			return "static/Error";
		} 
	}
	
	public long addPfmsFeedbackTrans(Long feedbackId, String remarks, Long empId) {
		try {

			PfmsFeedbackTrans trans = new PfmsFeedbackTrans();
			trans.setFeedbackId(feedbackId);
			trans.setComments(remarks);
			trans.setActionBy(empId);
			trans.setActionDate(sdtf.format(new Date()));
			
			return service.addPfmsFeedbackTrans(trans);
			
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
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
	
	@RequestMapping(value = "industryPartnerAdd.htm" , method = {RequestMethod.GET})
	public @ResponseBody String industryPartnerAdd(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside industryPartnerAdd.htm " + UserId);
		Gson json = new Gson();
		IndustryPartner partner1 = new IndustryPartner();
		try {
			
			 if (hasHtmlTags(req)) {
		            return json.toJson(null); 
		        }
			
			String industryPartnerName2 = req.getParameter("industryPartnerName2");
			String industryPartnerAddress2 = req.getParameter("industryPartnerAddress2");
			String industryPartnerCity2 = req.getParameter("industryPartnerCity2");
			String industryPartnerPinCode2 = req.getParameter("industryPartnerPinCode2");
			
			IndustryPartner partner =  new IndustryPartner();
			partner.setIndustryName(industryPartnerName2);
			partner.setIndustryAddress(industryPartnerAddress2);
			partner.setIndustryCity(industryPartnerCity2);
			partner.setIndustryPinCode(industryPartnerPinCode2);
			partner.setCreatedBy(UserId);
			partner.setCreatedDate(LocalDate.now().toString());
			partner.setIsActive(1);
			
			long count = service.addIndustryPartner(partner);
			 partner1 = service.getIndustryPartnerById(count+"");
			return json.toJson(partner1);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside industryPartnerAdd.htm " + UserId, e);
			return json.toJson(partner1);
		}
	}
	
	
	
	@RequestMapping(value = "industryPartnerEmpAdd.htm" , method = {RequestMethod.GET})
	public @ResponseBody String industryPartnerEmpAdd(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside industryPartnerEmpAdd.htm " + UserId);
		Gson json = new Gson();
		try {
			
			 if (hasHtmlTags(req)) {
		            return json.toJson(null); 
		        }
			
			String repName = req.getParameter("repName");
			String repDesignation = req.getParameter("repDesignation");
			String repEmail = req.getParameter("repEmail");
			String repMobileNo = req.getParameter("repMobileNo");
			String industryPartnerId = req.getParameter("industryPartnerId");
			List<IndustryPartnerRep> partnerReplist = new ArrayList<>();
			IndustryPartnerRep partnerRep = new IndustryPartnerRep();
			IndustryPartner partner1 = service.getIndustryPartnerById((industryPartnerId));
			
			partnerRep.setIndustryPartner(partner1);
			partnerRep.setRepName(repName);
			partnerRep.setRepDesignation(repDesignation);
			partnerRep.setRepEmail(repEmail);
			partnerRep.setRepMobileNo(repMobileNo);
			partnerRep.setCreatedBy(UserId);
			partnerRep.setCreatedDate(LocalDateTime.now().toString());
			partnerRep.setIsActive(1);
			
			partnerReplist.add(partnerRep);
			partner1.setRep(partnerReplist);
			
			long result = service.addIndustryPartner(partner1);
			return json.toJson(1);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside industryPartnerEmpAdd.htm " + UserId, e);
			return json.toJson(0);
		}
		
	}
		
	
	@RequestMapping(value = "OfficerExternalAjaxAdd.htm", method = RequestMethod.GET)
	public @ResponseBody String OfficerExternalAjaxAdd(HttpSession ses, HttpServletRequest req,
	                                                  HttpServletResponse res, RedirectAttributes redir) throws Exception {
	    String UserId = (String) ses.getAttribute("Username");
	    String labcode = (String) ses.getAttribute("labcode");
	    logger.info(new Date() + " Inside OfficerExternalAjaxAdd.htm " + UserId);
	    Gson json = new Gson();

	    try {
	        // ✅ Step 1: Check all parameters for HTML tags
	        Enumeration<String> parameterNames = req.getParameterNames();
	        while (parameterNames.hasMoreElements()) {
	            String paramName = parameterNames.nextElement();
	            String paramValue = req.getParameter(paramName).trim();
	            System.out.println(paramName+"-----");
	            if (paramValue != null && paramValue.matches(".*<[^>]+>.*")) {
	                // Contains HTML tag → return -1
	                return json.toJson(-1);
	            }
	        }

	        // ✅ Step 2: Proceed if no HTML tags
	        String EmpNo = req.getParameter("EmpNo");

	        OfficerMasterAdd officermasteradd = new OfficerMasterAdd();
	        officermasteradd.setLabId(req.getParameter("labId"));
	        officermasteradd.setEmpNo(EmpNo.toUpperCase());

	        String name = req.getParameter("EmpName");
	        String words[] = name.split("\\s");
	        String capitalizeWord = "";
	        for (String w : words) {
	            String first = w.substring(0, 1);
	            String afterfirst = w.substring(1);
	            capitalizeWord += first.toUpperCase() + afterfirst + " ";
	        }

	        officermasteradd.setTitle(req.getParameter("title"));
	        officermasteradd.setSalutation(req.getParameter("salutation"));
	        officermasteradd.setEmpName(capitalizeWord.trim());
	        officermasteradd.setDesignation(req.getParameter("Designation"));
	        officermasteradd.setExtNo("-");
	        officermasteradd.setEmail("-");
	        officermasteradd.setDivision("0");
	        officermasteradd.setDronaEmail("-");
	        officermasteradd.setInternalEmail("-");
	        officermasteradd.setMobileNo("0123456789");
	        officermasteradd.setSrNo("0");
	        officermasteradd.setLabCode(labcode);

	        try {
	            long count = service.OfficerExtInsert(officermasteradd, UserId);
	            return json.toJson(count);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return json.toJson(0);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        logger.error(new Date() + " Inside OfficerExternalAjaxAdd.htm " + UserId, e);
	        return json.toJson(0);
	    }
	}
	
	/* **************************** Programme Master - Naveen R  - 16/07/2025 **************************************** */
	@RequestMapping(value = "ProgrammeMaster.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String getProgrameMaster(HttpServletRequest req, HttpServletResponse resp, HttpSession session,RedirectAttributes redir) throws Exception {
		String UserId = (String) session.getAttribute("Username");
		String labcode = (String) session.getAttribute("labcode");
		
		logger.info(new Date() + "Inside ProgrammeMaster.htm" + UserId);
		try {
			String action = req.getParameter("action");
			if(action!=null && "Add".equalsIgnoreCase(action)) {
				
				req.setAttribute("directorsList", service.OfficerList());
				req.setAttribute("projectsList", service.getProjectList(labcode));
				return "master/ProgramMasterAddEdit";
			}else if(action!=null && "Edit".equalsIgnoreCase(action)) {
				
				String ProgrammeId = req.getParameter("ProgrammeId");
				req.setAttribute("prgmMaster", committeeservice.getProgrammeMasterById(ProgrammeId));
				req.setAttribute("prgmprojectsList", committeeservice.getProgrammeProjectsList(ProgrammeId));
				req.setAttribute("directorsList", service.OfficerList());
				req.setAttribute("projectsList", service.getProjectList(labcode,ProgrammeId));
				
				return "master/ProgramMasterAddEdit";
			}else {
				req.setAttribute("programeMasterList", service.getProgramMasterList());
				return "master/ProgramMasterList";
			}
		}catch (Exception e) {
			logger.error(new Date() +" Inside ProgrammeMaster.htm "+UserId, e);
			e.printStackTrace();
			return "static/Error";
		} 
	}
	
	@RequestMapping(value = "ProgrammeMasterSubmit.htm",method = {RequestMethod.POST,RequestMethod.GET})
	public String getProgrammeMasterAddEditPage(HttpServletRequest req, HttpServletResponse resp, HttpSession session,RedirectAttributes redir) throws Exception {
		
		String UserId = (String) session.getAttribute("Username");
		logger.info(new Date() + "Inside ProgrammeMasterSubmit.htm" + UserId);
		try {
			
			if(InputValidator.isContainsHTMLTags(req.getParameter("ProgrammeCode"))) {
				return  redirectWithError(redir,"ProgrammeMaster.htm","ProgrammeCode should not contain HTML elements !");
			}
			if(InputValidator.isContainsHTMLTags(req.getParameter("ProgrammeName"))) {
				return  redirectWithError(redir,"ProgrammeMaster.htm","ProgrammeName should not contain HTML elements !");
			}
			
			String action = req.getParameter("action");	
			String programmeMasterId = req.getParameter("programmeMasterId");
			String programmedirector = req.getParameter("programmedirector");
			String sanctionDate = req.getParameter("sanctionDate");
			ProgrammeMaster master = programmeMasterId.equalsIgnoreCase("0")? new ProgrammeMaster(): committeeservice.getProgrammeMasterById(programmeMasterId);

			master.setPrgmCode(req.getParameter("ProgrammeCode").toUpperCase());
			master.setPrgmName(req.getParameter("ProgrammeName"));
			master.setPrgmDirector(programmedirector!=null? Long.parseLong(req.getParameter("programmedirector")):0);
			master.setSanctionedOn(sanctionDate!=null?fc.rdfTosdf(sanctionDate):null);

			if(programmeMasterId.equalsIgnoreCase("0")) {
				master.setCreatedBy(UserId);
				master.setCreatedDate(sdtf.format(new Date()));
				master.setIsActive(1);
			}else{
				master.setModifiedBy(UserId);
				master.setModifiedDate(sdtf.format(new Date()));
				
				service.removeProjectsLinked(programmeMasterId);
				
			}

			Long result = service.addProgrammeMaster(master);
			
			String[] prgmProjectsIds = req.getParameterValues("prgmprojectids");
			
			if(prgmProjectsIds!=null && prgmProjectsIds.length>0) {
				for(int i=0; i<prgmProjectsIds.length; i++) {

					if(prgmProjectsIds[i]!=null && !prgmProjectsIds[i].equalsIgnoreCase("0")) {

						ProgrammeProjects linked = new ProgrammeProjects();
						linked.setProgrammeId(result);
						linked.setProjectId(Long.parseLong(prgmProjectsIds[i]));
						linked.setCreatedBy(UserId);
						linked.setCreatedDate(sdtf.format(new Date()));
						linked.setIsActive(1);
						
						service.addProgrammeProjects(linked);
						
					}
				}
			} 
			

			if(result!=0) redir.addAttribute("result","Programme Master "+action +"ed Successfully");
			else redir.addAttribute("resultfail","Programme Master "+action +"ed UnSuccessfull");
			return "redirect:/ProgrammeMaster.htm";
		}catch (Exception e) {
			logger.error(new Date() +" Inside ProgrammeMasterSubmit.htm "+UserId, e);
			e.printStackTrace();
			return "static/Error";
		} 
	}
	
	@RequestMapping(value = "ProgrammeCodeCheck.htm", method = RequestMethod.GET)
	public @ResponseBody String programeCodeAddCheck(HttpSession ses, HttpServletRequest req) throws Exception 
	{
		String UserId=(String)ses.getAttribute("Username");
		Long prgmCodes = null;
		logger.info(new Date() +"Inside ProgrammeCodeCheck.htm "+UserId);
		try
		{	  
			String ProgrammeCode=req.getParameter("ProgrammeCode");
			String ProgrammeId=req.getParameter("ProgrammeId");
			prgmCodes =service.ProgramCodeCheck(ProgrammeCode,ProgrammeId);
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +"ProgrammeCodeCheck.htm "+UserId,e);
		}
		Gson json = new Gson();
		return json.toJson(prgmCodes); 
	}
	/* **************************** Programme Master - Naveen R - 16/07/2025 End **************************************** */

	@RequestMapping(value = "RoleMasterDetailsSubmit.htm", method = {RequestMethod.GET})
	public @ResponseBody ResponseEntity<String> roleMasterDetailsSubmit(HttpServletRequest req, HttpSession ses,RedirectAttributes redir) throws Exception{
		String Username=(String)ses.getAttribute("Username");
		logger.info(new Date() + " Inside RoleMasterDetailsSubmit.htm "+Username);
		Gson json = new Gson();
		Object[] data = new Object[3];
		try {
			String roleName = req.getParameter("roleName");
			String roleCode = req.getParameter("roleCode");
			
			if(InputValidator.isContainsHTMLTags(roleName)) {
				return new ResponseEntity<String>("200",HttpStatus.EXPECTATION_FAILED);
			}
			if(InputValidator.isContainsHTMLTags(roleCode)) {
				return new ResponseEntity<String>("200",HttpStatus.EXPECTATION_FAILED);
			}
			
			RoleMaster roleMaster = new RoleMaster();
			roleMaster.setRoleName(roleName);
			roleMaster.setRoleCode(roleCode);
			roleMaster.setCreatedBy(Username);
			roleMaster.setCreatedDate(sdtf.format(new Date()));
			roleMaster.setIsActive(1);
			
			Long roleMasterId = service.addRoleMaster(roleMaster);
			data[0] = roleMasterId;
			data[1] = roleName;
			data[2] = roleCode;
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside RoleMasterDetailsSubmit.htm "+Username, e);
		}
		return ResponseEntity.ok(json.toJson(data));
	}
	
	// 22/8/2025  Naveen R RoleName and RoleCode Duplicate Check start
	
	@RequestMapping(value = "RoleNameDuplicateCheck.htm", method = RequestMethod.GET)
	public @ResponseBody String roleNameDuplicateCheck(HttpSession ses,HttpServletRequest req) {
		String UserId = (String)ses.getAttribute("Username");
		String roleName = req.getParameter("roleName");
		Long count = null;
		logger.info(new Date() + " Inside RoleNameDuplicateCheck.htm "+ UserId );
		try {
			count = service.getRoleNameDuplicateCount(roleName);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside RoleNameDuplicateCheck.htm " + UserId);
		}
		Gson json = new Gson();
		return json.toJson(count);
	}
	
	@RequestMapping(value = "RoleCodeDuplicateCheck.htm", method = RequestMethod.GET)
	public @ResponseBody String roleCodeDuplicateCheck(HttpSession ses,HttpServletRequest req) {
		String UserId = (String)ses.getAttribute("Username");
		String roleCode =req.getParameter("roleCode");
		Long count = null;
		logger.info(new Date() + " Inside RoleCodeDuplicateCheck.htm "+ UserId );
		try {
			count = service.getRoleCodeDuplicateCount(roleCode);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside RoleCodeDuplicateCheck.htm " + UserId);
		}
		Gson json = new Gson();
		return json.toJson(count);
	}
	// 22/8/2025  Naveen R RoleName and RoleCode Duplicate Check End
	
}