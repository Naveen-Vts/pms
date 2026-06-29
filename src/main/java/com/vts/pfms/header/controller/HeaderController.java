package com.vts.pfms.header.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.vts.pfms.header.model.ProjectDashBoardFavourite;
import com.vts.pfms.header.model.ProjectDashBoardFavouriteProjetcts;
import com.vts.pfms.header.service.HeaderService;
import com.vts.pfms.login.Login;
import com.vts.pfms.login.LoginRepository;
import com.vts.pfms.print.service.PrintService;
import com.vts.pfms.service.RfpMainService;
import com.vts.pfms.utils.InputValidator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
@Controller
public class HeaderController {

	@Autowired
	HeaderService service;
	
	@Autowired
	PrintService printservice;
	
	@Autowired
	RfpMainService rfpmainservice;
	
	@Autowired
	LoginRepository loginRepo;
	
	@Value("${ApplicationFilesDrive}")
	String uploadpath;
	
	private static final Logger logger=LogManager.getLogger(HeaderController.class);
	
	private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	private String redirectWithError(RedirectAttributes redir,String redirURL, String message) {
	    redir.addAttribute("resultfail", message);
	    return "redirect:/"+redirURL;
	}
	
	@RequestMapping(value = "HeaderModuleList.htm" , method = RequestMethod.GET)
	public @ResponseBody String HeaderModuleList(HttpServletRequest request ,HttpSession ses) throws Exception {
		
		Gson json = new Gson();
		List<Object[]> HeaderModuleList = null;
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside HeaderModuleList.htm "+UserId);		
		try {
			String LoginType = ((String) ses.getAttribute("LoginType"));
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String LabCode = (String) ses.getAttribute("labcode");
			String ClusterId =(String)ses.getAttribute("clusterid");
			
			String DGName = service.LabMasterList(ClusterId).stream().filter(e-> "Y".equalsIgnoreCase(e[2].toString())).collect(Collectors.toList()).get(0)[1].toString();

		     String IsDG = "No";
		     if(DGName.equalsIgnoreCase(LabCode))
		    	 IsDG = "Yes";
		     else
		    	 IsDG = "No";
		    	 
		    request.setAttribute("IsDG", "Yes");	
		    HeaderModuleList = service.FormModuleList(LoginType,LabCode);
		    request.setAttribute("ProjectInitiationList", service.ProjectIntiationList(EmpId,LoginType));
		    
		    
		}
		catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside HeaderModuleList.htm "+UserId, e);
		}
			return json.toJson(HeaderModuleList);
		
		
	}
	
	
	@RequestMapping(value = "LoginTypeChange.htm", method = RequestMethod.POST)
	public String LoginTypeChange(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
			throws Exception {
		
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside LoginTypeChange.htm "+UserId);		
		try {
			ses.setAttribute("LoginAs", req.getParameter("loginType"));
			String LoginType = (String) ses.getAttribute("LoginType");
			
			req.setAttribute("loginTypeList", service.loginTypeList(LoginType));
		
		}
		catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside LoginTypeChange.htm "+UserId, e);
		}
		
		  
	  	  return "static/MainDashBoard";

	  	  
	}
	
	
	@RequestMapping(value = "NotificationList.htm" , method = RequestMethod.GET)
	public @ResponseBody String NotificationList(HttpServletRequest request ,HttpSession ses) throws Exception {
			
		 List<Object[]> NotificationList =null;
		 Gson json = new Gson();
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside NotificationList.htm "+UserId);		
		try {
		
			String EmpId= ((Long) ses.getAttribute("EmpId")).toString();
			
		    NotificationList = service.NotificationList(EmpId);
		    
		    
		}
		catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside NotificationList.htm "+UserId, e);
		}
		    
		    
			return json.toJson(NotificationList);
		
		
	}
	
	
	
	@RequestMapping(value = "NotificationUpdate.htm" , method = RequestMethod.GET)
	public @ResponseBody String NotificationUpdate(HttpServletRequest request ,HttpSession ses) throws Exception {
		Gson json = new Gson();
		int count=0;
		
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside NotificationUpdate.htm "+UserId);		
		try {
			String NotificationId=request.getParameter("notificationid");
			String EmpId= ((Long) ses.getAttribute("EmpId")).toString();
			
		   count= service.NotificationUpdate(NotificationId);
		    
			
		}
		catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside NotificationUpdate.htm "+UserId, e);
		}
			return json.toJson(count);
			
		
	}
	
	
	@RequestMapping(value = "NotificationListView.htm", method = RequestMethod.GET)
	public String  NotificationListView(HttpServletRequest req,HttpSession ses) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside NotificationListView.htm "+UserId);		
		try {
		
		String EmpId= ((Long) ses.getAttribute("EmpId")).toString();		
		req.setAttribute("NotificationList", service.NotificationAllList(EmpId));
		
		}
		catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside NotificationListView.htm "+UserId, e);
		}

		return "admin/NotificationListView";

	}
	
	@RequestMapping(value = "EmpNameHeader.htm" , method = RequestMethod.GET)
	public @ResponseBody String EmpNameHeader(HttpServletRequest request ,HttpSession ses) throws Exception {
		Object[] EmpNameHeader=null;
		Gson json = new Gson();
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside EmpNameHeader.htm "+UserId);		
		try {
		
			String LoginId= ((Long) ses.getAttribute("LoginId")).toString();
				
			EmpNameHeader= service.EmployeeDetailes(LoginId).get(0);
		 
		}
		catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside EmpNameHeader.htm "+UserId, e);
		}
			return json.toJson(EmpNameHeader);
	}
	
	@RequestMapping(value = "DivisionNameHeader.htm" , method = RequestMethod.GET)
	public @ResponseBody String DivisionNameHeader(HttpServletRequest request ,HttpSession ses) throws Exception {
		
		String DivisionName=null;
		Gson json = new Gson();
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside DivisionNameHeader.htm "+UserId);		
		try {
		String Division= ((Long) ses.getAttribute("Division")).toString();
			
		 DivisionName= service.DivisionName(Division);
		}
		catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside DivisionNameHeader.htm "+UserId, e);
		}
		    
			return json.toJson(DivisionName);
	}
	
	@RequestMapping(value = "UserManualDoc.htm", method = RequestMethod.GET)
	public void UserManualDoc(HttpServletRequest req, HttpSession ses, HttpServletResponse res)
			throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside UserManualDoc.htm "+UserId);		
		try {
			Path filePath = Paths.get(uploadpath,"UserManual","PMS.pdf");
			String path = filePath.toString();
			
			res.setContentType("application/pdf");
			res.setHeader("Content-Disposition", String.format("inline; filename=User Manual-PFMS.pdf"));
	
			File my_file = new File(path);
	
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
				logger.error(new Date() +" Inside UserManualDoc.htm "+UserId, e);
		}
	}
	
	@RequestMapping(value = "WorkFlow.htm", method = RequestMethod.GET)
	public void WorkFlow(HttpServletRequest req, HttpSession ses, HttpServletResponse res)
			throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside WorkFlow.htm "+UserId);		
		try {

		//String path = req.getServletContext().getRealPath("/UserManual/" + "PFMS Work Flow.pdf");
			Path filePath = Paths.get(uploadpath,"WorkFlow","PMS.pdf");
			String path = filePath.toString();
			res.setContentType("application/pdf");
			res.setHeader("Content-Disposition", String.format("inline; filename=PFMS Work Flow.pdf"));

			File my_file = new File(path);
	
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
					logger.error(new Date() +" Inside WorkFlow.htm "+UserId, e);
			}
	}
	
	
	@RequestMapping(value = "MilestoneManual.htm", method = RequestMethod.GET)
	public void MilestoneManual(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside MilestoneManual.htm "+UserId);		
		try {

		String path = req.getServletContext().getRealPath("/UserManual/" + "MilestoneManual.pdf");

		res.setContentType("application/pdf");
		res.setHeader("Content-Disposition", String.format("inline; filename=MilestoneManual.pdf"));

		File my_file = new File(path);

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
				logger.error(new Date() +" Inside MilestoneManual.htm "+UserId, e);
		}
	}
	
	@RequestMapping(value = "LabReportChapter1.htm", method = RequestMethod.GET)
	public void LabReportChapter1(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode"); 
		logger.info(new Date() +"Inside LabReportChapter1.htm "+UserId);		
		try {
		Path chapter1path = Paths.get(ApplicationFilesDrive, LabCode,"LabReport","Chapter1.docx");
		System.out.println(chapter1path.toString());
		res.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		res.setHeader("Content-Disposition", String.format("inline; filename=Chapter1.docx"));
		File my_file = chapter1path.toFile();
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
				logger.error(new Date() +" Inside LabReportChapter1.htm "+UserId, e);
		}
	}
	@RequestMapping(value = "LabReportChapter2.htm", method = RequestMethod.GET)
	public void LabReportChapter2(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode"); 
		logger.info(new Date() +"Inside LabReportChapter2.htm "+UserId);		
		try {
		Path chapter1path = Paths.get(ApplicationFilesDrive, LabCode,"LabReport","Chapter2.docx");
		res.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		res.setHeader("Content-Disposition", String.format("inline; filename=Chapter2.docx"));
		File my_file = chapter1path.toFile();
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
				logger.error(new Date() +" Inside LabReportChapter2.htm "+UserId, e);
		}
	}
	
	@RequestMapping(value = "PDManual.htm", method = RequestMethod.GET)
	public void PDManual(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside MilestoneManual.htm "+UserId);		
		try {

		String path = req.getServletContext().getRealPath("/UserManual/" + "PD_Manual.pdf");

		res.setContentType("application/pdf");
		res.setHeader("Content-Disposition", String.format("inline; filename=PD_Manual.pdf"));

		File my_file = new File(path);

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
				logger.error(new Date() +" Inside PDManual.htm "+UserId, e);
		}
	}
	
	@RequestMapping(value = "PasswordChange.htm", method = {RequestMethod.GET, RequestMethod.POST})
	public String PasswordChange(HttpServletRequest req, HttpSession ses) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		String sessionKey = (String) ses.getAttribute("LOGIN_AES_KEY");
		String sessionIv = (String) ses.getAttribute("LOGIN_AES_IV");
		logger.info(new Date() +"Inside PasswordChange.htm "+UserId);	
		req.setAttribute("ForcePwd", "N");
		req.setAttribute("sessionKey", sessionKey);
 		req.setAttribute("sessionIv", sessionIv);
		return "admin/PasswordChange";
	}
	
	@RequestMapping(value = "PasswordChanges.htm", method = RequestMethod.POST)
	public String PasswordChangeSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside PasswordChanges.htm "+UserId);		
		try {
			long LoginId = (long) ses.getAttribute("LoginId");  
			long EmpId = (long) ses.getAttribute("EmpId");  
			String NewPassword =req.getParameter("NewPassword");
			String OldPassword =req.getParameter("OldPassword");
			
			if(Stream.of(NewPassword, OldPassword)
			        .anyMatch(field -> InputValidator.isContainsHTMLTags(field))) {
			    return redirectWithError(redir, "MainDashBoard.htm", "Password Should not contain HTML Tags");
			}
			
			if((NewPassword!=null && OldPassword!=null) && OldPassword.equalsIgnoreCase(NewPassword)) {
				redir.addAttribute("resultfail", "Your New Password Should Not Be Same as Your Old Password.");
	            return "redirect:/PasswordChanges.htm";
			}
			if (!isStrongPassword(NewPassword)) {
	            redir.addAttribute("resultfail", "New password does not meet security requirements.");
	            return "redirect:/PasswordChanges.htm";
	        }
			
			int count=service.PasswordChange(OldPassword, NewPassword, UserId, LoginId, EmpId);
		
			if (count > 0)  {
				redir.addAttribute("result", "Password Changed Successfully");
				return "redirect:/MainDashBoard.htm";
			} 
			else  {
				redir.addAttribute("resultfail", "Password Change Unsuccessful");
				return "redirect:/PasswordChange.htm";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside PasswordChanges.htm "+UserId, e);
			return "static/Error";
		}
		
	}
   
	private boolean isStrongPassword(String password) {
	    if (password == null) {
	        return false;
	    }
	    String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
	    Pattern regex = Pattern.compile(pattern);
	    Matcher matcher = regex.matcher(password);
	    return matcher.matches();
	}
	
	@RequestMapping (value="GanttChart.htm")
	public String GanttChart(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
		
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside GanttChart.htm "+UserId);		
		try {
		
			List<Object[] > ProjectList= service.ProjectList();
			String ProjectId=req.getParameter("ProjectId");
			
			if(ProjectId==null) {
				Object[] FirstProjectId=  ProjectList.get(0);
				ProjectId= FirstProjectId[0].toString();
				
			}
		
		req.setAttribute("ProjectDetails", service.ProjectDetails(ProjectId).get(0));	
		req.setAttribute("ProjectList", ProjectList);	
		req.setAttribute("ProjectId", ProjectId);	
		req.setAttribute("ganttchartlist", service.GanttChartList(ProjectId));
		
		return "admin/GanttChart";
		
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside GanttChart.htm "+UserId, e);
			return "static/Error";
		
		}
	}
	
	
	@RequestMapping (value="GanttChartSub.htm", method=RequestMethod.POST)
	public String GanttChartSub(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception 
	{
		
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside GanttChartSub.htm "+UserId);		
			try {
			
				List<Object[] > ProjectList= service.ProjectList();
				String ProjectId=req.getParameter("ProjectId");
				
				if(ProjectId==null) {
					Object[] FirstProjectId=  ProjectList.get(0);
					ProjectId= FirstProjectId[0].toString();
					
				}
						
				req.setAttribute("ProjectDetails", service.ProjectDetails(ProjectId).get(0));	
				req.setAttribute("ProjectList", ProjectList);	
				req.setAttribute("ProjectId", ProjectId);	
				
				if(ProjectId!=null) {
					List<Object[]> main=service.MilestoneActivityList(ProjectId);
					List<Object[]> MilestoneActivityA=new ArrayList<Object[]>();
					List<Object[]> MilestoneActivityB=new ArrayList<Object[]>();
					List<Object[]> MilestoneActivityC=new ArrayList<Object[]>();
					List<Object[]> MilestoneActivityD=new ArrayList<Object[]>();
					List<Object[]> MilestoneActivityE=new ArrayList<Object[]>();
					
						for(Object[] objmain:main ) {
							List<Object[]>  MilestoneActivityA1=service.MilestoneActivityLevel(objmain[0].toString(),"1");
							MilestoneActivityA.addAll(MilestoneActivityA1);
							
							for(Object[] obj:MilestoneActivityA1) {
								List<Object[]>  MilestoneActivityB1=service.MilestoneActivityLevel(obj[0].toString(),"2");
								MilestoneActivityB.addAll(MilestoneActivityB1);
								
								for(Object[] obj1:MilestoneActivityB1) {
									List<Object[]>  MilestoneActivityC1=service.MilestoneActivityLevel(obj1[0].toString(),"3");
									MilestoneActivityC.addAll(MilestoneActivityC1);
									
									for(Object[] obj2:MilestoneActivityC1) {
										List<Object[]>  MilestoneActivityD1=service.MilestoneActivityLevel(obj2[0].toString(),"4");
										MilestoneActivityD.addAll( MilestoneActivityD1);
										
										for(Object[] obj3:MilestoneActivityD1) {
											List<Object[]>  MilestoneActivityE1=service.MilestoneActivityLevel(obj3[0].toString(),"5");
											MilestoneActivityE.addAll( MilestoneActivityE1);
										}
									}
								}
							}
						}
					req.setAttribute("MilestoneActivityMain", main);
					req.setAttribute("MilestoneActivityE", MilestoneActivityE);
					req.setAttribute("MilestoneActivityD", MilestoneActivityD);
					req.setAttribute("MilestoneActivityC", MilestoneActivityC);
					req.setAttribute("MilestoneActivityB", MilestoneActivityB);
					req.setAttribute("MilestoneActivityA", MilestoneActivityA);
						
				}
				
				return "admin/GanttChartSub";
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside GanttChartSub.htm "+UserId, e);
				return "static/Error";
		}
		
	}
	
	
	@RequestMapping(value = "HeaderMenu.htm", method = RequestMethod.GET)
	public @ResponseBody String HeaderSchedulesList(HttpServletRequest req, HttpSession ses) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		List<Object[]> HeaderSchedulesList =null;
		String LabCode = (String) ses.getAttribute("labcode"); 
		logger.info(new Date() +"Inside HeaderSchedulesList.htm "+UserId);

		try {
			HeaderSchedulesList = service.HeaderSchedulesList(req.getParameter("logintype"),req.getParameter("formmoduleid"),LabCode);
					 
		}catch (Exception e) {
			logger.error(new Date() +" Inside HeaderSchedulesList.htm "+UserId, e);
			e.printStackTrace();
		}
		Gson json = new Gson();
		return json.toJson(HeaderSchedulesList);

	}
	
	@RequestMapping(value = "AboutPFM.htm", method = RequestMethod.GET)
	public void AboutPFM(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside AboutPFM.htm "+UserId);		
		try {

		String path = req.getServletContext().getRealPath("/UserManual/" + "aboutpms.pdf");

		res.setContentType("application/pdf");
		res.setHeader("Content-Disposition", String.format("inline; filename=AboutPMS.pdf"));

		File my_file = new File(path);

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
				logger.error(new Date() +" Inside AboutPFM.htm "+UserId, e);
		}
	}
	
	
	// new anilscode
	
	@RequestMapping(value = "getAllNoticationId.htm" , method = RequestMethod.GET)
	public @ResponseBody String getAllNoticationId(HttpServletRequest request ,HttpSession ses) throws Exception {
			
		 List<Object[]> notifationIdList =null;
		 Gson json = new Gson();
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside NotificationList.htm "+UserId);		
		try {
		
			String EmpId= ((Long) ses.getAttribute("EmpId")).toString();
			
			notifationIdList = service.getNotificationId(EmpId);
			if(notifationIdList!=null && notifationIdList.size()>0) {
			for(Object[] obj: notifationIdList) {
				service.NotificationUpdate(obj[0].toString());
			}
			}
		   
		    
		}
		catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside NotificationList.htm "+UserId, e);
		}
		    
		    
			return json.toJson(notifationIdList);
		
		
	}
	
	@RequestMapping(value = "SmartSearch.htm" , method = RequestMethod.GET)
	public @ResponseBody String SmartSearch(HttpServletRequest request ,HttpSession ses) throws Exception
	{
		Gson json = new Gson();
		List<Object[]> send = null;
		if (String.valueOf(request.getParameter("search")).length()>0)
		send = service.getFormNameByName(String.valueOf(request.getParameter("search")));
		return json.toJson(send);
	}
	
	@RequestMapping(value = "searchForRole.htm" , method = RequestMethod.GET)
	public @ResponseBody String searchForRole(HttpServletRequest request ,HttpSession ses) throws Exception
	{
		Gson json = new Gson();
		Boolean send = null;
		if (String.valueOf(request.getParameter("search")).length()>0)
		send = service.getRoleAccess(String.valueOf(request.getParameter("search")),ses.getAttribute("LoginType").toString());
		return json.toJson(send);
	}
	
	
	@RequestMapping(value= "DashboardFavAdd.htm" , method = RequestMethod.POST)
	public String DashboardFavAdd(HttpServletRequest req, HttpSession ses, HttpServletResponse res,RedirectAttributes redir) throws Exception {
		String EmpId= ((Long) ses.getAttribute("EmpId")).toString();
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode"); 
		String LoginType = ((String) ses.getAttribute("LoginType"));
		try {
			
			String projects = req.getParameter("projects");
			String FavName = req.getParameter("addFav");
			
			if(InputValidator.isContainsHTMLTags(FavName)) {
				redir.addAttribute("resultfail", "Favourite Name shold not contain html tag");
				return "redirect:/MainDashBoard.htm";
			}
			
			System.out.println("projects "+projects);
	
			
			long DashBoardId =service.addDashBoardFav(projects,FavName,EmpId,UserId,LoginType);
			
			
			ses.setAttribute("DashBoardId", DashBoardId+"");
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		 return "redirect:/MainDashBoard.htm";
	}
	
	
	
	@RequestMapping(value= "UpdateDashboardFav.htm" , method = RequestMethod.POST)
	public String UpdateDashboardFav(HttpServletRequest req, HttpSession ses, HttpServletResponse res,RedirectAttributes redir) throws Exception {
		String EmpId= ((Long) ses.getAttribute("EmpId")).toString();
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode"); 
		String LoginType = ((String) ses.getAttribute("LoginType"));
		try {
			String dashboardId = req.getParameter("dashboardId");
			if(dashboardId.equalsIgnoreCase("-1")) {
				service.isActiveDashBoard(EmpId,LoginType);
				ses.setAttribute("DashBoardId", "0");
				 return "redirect:/MainDashBoard.htm";
			}
			else {
				String projects = req.getParameter("favProjects");
				System.out.println(projects);
				service.isActiveDashBoard(EmpId,LoginType);
				service.updateDashBoard(dashboardId,projects,UserId);
				ses.setAttribute("DashBoardId", dashboardId);
				 return "redirect:/MainDashBoard.htm";
			}
			
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		 return "redirect:/MainDashBoard.htm";
	}
	@Value("${ApplicationFilesDrive}")
	private String ApplicationFilesDrive;
	@RequestMapping(value = "getDashBoardProjects.htm" , method = RequestMethod.GET)
	public @ResponseBody String getDashBoardProjects(HttpServletRequest request ,HttpSession ses) throws Exception
	{
		Gson json = new Gson();
		
		ProjectDashBoardFavourite pd=service.findProjectDashBoardFavourite(Long.parseLong(request.getParameter("DashBoardId")));
		
		List<String>projects = new ArrayList<>();
		
		if(pd!=null) {
			for(ProjectDashBoardFavouriteProjetcts p:pd.getProjects()) {
				projects.add(p.getProjectId()+"");
			}
		}
		
	
		return json.toJson(projects);
	}
	
	@RequestMapping(value = "storeSlideData.htm" , method = RequestMethod.GET)
	public @ResponseBody String storeSlideData(HttpServletRequest request ,HttpSession ses) throws Exception
	{
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String LoginType = (String) ses.getAttribute("LoginType");
		String LabCode = (String) ses.getAttribute("labcode");
		
		Gson json = new Gson();
		
		ProjectDashBoardFavourite pd=service.findProjectDashBoardFavourite(Long.parseLong(request.getParameter("DashBoardId")));
		
		List<String>projects = new ArrayList<>();
		
		if(pd!=null) {
			for(ProjectDashBoardFavouriteProjetcts p:pd.getProjects()) {
				projects.add(p.getProjectId()+"");
			}
		}else {
			List<Object[]>projectLists = rfpmainservice.ProjectList(LoginType, EmpId, LabCode);
			for(Object[]obj:projectLists) {
				projects.add(obj[0].toString()+"");
			}
		}
		List<Object[]> getAllProjectSlidedata = new ArrayList<>();

		List<Object[]> getAllProjectdata = new ArrayList<>();

		List<Object[]> getAllProjectSlidesdata = new ArrayList<>();

		if (projects != null && projects.size() > 0)

			for (String id : projects) {
								
				List<Object[]> getoneProjectSlidedata = printservice.GetAllProjectSildedata(id);  // freezing data
				Object[] projectslidedata = (Object[]) printservice.GetProjectSildedata(id);  //[7] id project id
				getAllProjectSlidesdata.add(projectslidedata);
				Object[] projectdata = (Object[]) printservice.GetProjectdata(id); //[0] is project id ------ all vals
				getAllProjectdata.add(projectdata);
				if (getoneProjectSlidedata.size() > 0) {
					for (Object[] objects : getoneProjectSlidedata) {
						getAllProjectSlidedata.add(objects);
					}
				}

			}

		Comparator<Object[]> dateComparator = new Comparator<Object[]>() {

			@Override

			public int compare(Object[] o1, Object[] o2) {

				Date date1 = (Date) o1[5];

				Date date2 = (Date) o2[5];

				return date1.compareTo(date2);

			}

		};
		try {

			if (getAllProjectdata.size() > 1) {
				Collections.sort(getAllProjectdata, dateComparator);
				Collections.sort(getAllProjectSlidedata, dateComparator);
			}
			Collections.reverse(getAllProjectdata);

			String labcode = ses.getAttribute("labcode").toString();

			ses.setAttribute("getAllProjectdata", getAllProjectdata);

			ses.setAttribute("labInfo", printservice.LabDetailes(labcode));


			ses.setAttribute("filepath", ApplicationFilesDrive);

			ses.setAttribute("getAllProjectSlidedata", getAllProjectSlidedata);
			ses.setAttribute("getAllProjectSlidesdata", getAllProjectSlidesdata);
			
			
			
		}catch (Exception e) {
			// TODO: handle exception
		}
	
		return json.toJson(projects);
	}
	
	@RequestMapping(value = "TimeSheetWorkFlowPdf.htm", method = RequestMethod.GET)
	public void timeSheetWorkFlowPdf(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside TimeSheetWorkFlowPdf.htm "+UserId);		
		try {
			
			String path = req.getServletContext().getRealPath("/UserManual/" + "WorkRegister.pdf");
			
			res.setContentType("application/pdf");
			res.setHeader("Content-Disposition", String.format("inline; filename=WorkRegister.pdf"));
			
			File my_file = new File(path);
			
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
			logger.error(new Date() +" Inside TimeSheetWorkFlowPdf.htm "+UserId, e);
		}
	}
	
	@RequestMapping(value = "ProductreeDownload.htm", method = RequestMethod.GET)
	public void ProductreeDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res)throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside ProductreeDownload.htm "+UserId);		
		try {
			String path = req.getServletContext().getRealPath("/UserManual/" + "ProductTree.xlsx");
	
			res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			res.setHeader("Content-Disposition", String.format("inline; filename=ProductTree.xlsx"));
	
			File my_file = new File(path);
	
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
		logger.error(new Date() +" Inside ProductreeDownload.htm "+UserId, e);
		}
	}
	
    @RequestMapping(value = "ForcePasswordChange.htm", method = RequestMethod.GET)
	public String ForcePasswordChange(HttpServletRequest req, HttpSession ses) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String sessionKey = (String) ses.getAttribute("LOGIN_AES_KEY");
		String sessionIv = (String) ses.getAttribute("LOGIN_AES_IV");
		logger.info(new Date() + "Inside ForcePasswordChange.htm "+UserId);
		
		req.setAttribute("ForcePwd", "Y");
		req.setAttribute("sessionKey", sessionKey);
 		req.setAttribute("sessionIv", sessionIv);
		return "admin/PasswordChange";
	}
    
    @RequestMapping(value = "NewPasswordChangeCheck.htm", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody String NewPasswordChangeCheck(HttpServletRequest req, HttpServletResponse response, HttpSession ses)throws Exception 
	{
		String Username = (String) ses.getAttribute("Username");
		long LoginId = (long) ses.getAttribute("LoginId");
		String LoginType = (String) ses.getAttribute("LoginType");
		logger.info(new Date() + "Inside NewPasswordChangeCheck.htm " + Username);

		int retVar = 0;
		try {
			String oldpass = req.getParameter("oldpass");
			boolean result = false;
			
			System.out.println("LoginId"+LoginId);
			System.out.println("oldpass"+oldpass);
			Optional<Login> optionalLogin = loginRepo.findById(LoginId);
			
			if (optionalLogin.isPresent()) {
			
			    Login login = optionalLogin.get();
			    String ExistingPassword = login.getPassword();
				System.out.println("oldpass"+ExistingPassword);
			    result = encoder.matches(oldpass, ExistingPassword);
			} 
			if(result) {
				retVar=1;
			}

		} catch (Exception e) {
			logger.error(new Date() + "Inside NewPasswordChangeCheck.htm " + Username,e);
			e.printStackTrace();
		}
		Gson json = new Gson();
		return json.toJson(retVar);
	}

}
