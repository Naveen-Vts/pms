package com.vts.pfms.login;


import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.vts.pfms.FormatConverter;
import com.vts.pfms.admin.service.AdminService;
import com.vts.pfms.cfg.JwtUtil;
import com.vts.pfms.committee.service.ActionService;
import com.vts.pfms.committee.service.CommitteeService;
import com.vts.pfms.header.model.ProjectDashBoardFavourite;
import com.vts.pfms.header.model.ProjectDashBoardFavouriteProjetcts;
import com.vts.pfms.header.service.HeaderService;
import com.vts.pfms.master.dto.ProjectSanctionDetailsMaster;
import com.vts.pfms.model.FinanceChanges;
import com.vts.pfms.model.IbasLabMaster;
import com.vts.pfms.model.Notice;
import com.vts.pfms.pfmsserv.feign.PFMSServeFeignClient;
import com.vts.pfms.service.RfpMainService;
import com.vts.pfms.utils.InputValidator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.Jws;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.MalformedJwtException;
//import io.jsonwebtoken.SignatureException;
//import io.jsonwebtoken.UnsupportedJwtException;

@JsonIgnoreProperties(ignoreUnknown = true)
@Controller
@EnableScheduling
public class LoginController {
	
	private static final Logger logger=LogManager.getLogger(LoginController.class);
	@Autowired
	RfpMainService rfpmainservice;
	
	@Autowired
	ActionService Actservice;
	
	@Autowired
	AdminService service;
	
	@Autowired
	LoginRepository Repository;
	
	@Autowired
	HeaderService headerservice;
	
	@Autowired
	CommitteeService comservice;

	@Autowired
	PFMSServeFeignClient PFMSServ;
	
	
	@Autowired
    private Environment env;

	private SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private  SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
	
//	@Autowired
//  private LoginDetailsServiceImpl loginService;
//
//  @Autowired
//  private SecurityServiceImpl securityService;
	
	private static final RestTemplate restTemplate = new RestTemplate();

	@Value("${centralapp}")
	private String centralapp;
	@Value("${server_uri}")
    private String uri;
	@Value("${ibas_uri}")
    private String ibasUri;
	@Value("${interval}")
	private String interval;
//	@Value("${ibasV3_uri}")
//    private String ibasV3Uri;

//	 @RequestMapping(value = "/login", method = RequestMethod.GET)
//	    public String login(Model model, String error, String logout,HttpServletRequest req,HttpSession ses,HttpServletResponse response) {
//	
//		 logger.info(new Date() +"Inside login ");
//	    	if (error != null) 
//	            model.addAttribute("error", "Your username and password is invalid.");
//
//	    	if (logout != null)
//	             model.addAttribute("message", "You have been logged out successfully.");
//	    	
//	    	try {
//	    	
//	    	}
//				catch(Exception e) {
//	    		e.printStackTrace();
//	    		logger.error(new Date() +" Inside login.htm "+ e);
//	    	}
//	        return "static/login";
//	    }

    
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(@RequestHeader(name = "User-Agent") String userAgent, Model model, 
			@RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "logout", required = false) String logout,
			HttpServletRequest req, HttpSession ses, HttpServletResponse response) {
		
		req.getSession().setAttribute("loginPage", "login");
		
		String browser = req.getHeader("user-agent");
		String[] br = browser.split("\\)");
		String version = "";
		userAgent=br[br.length-1];
		if (userAgent.contains("Chrome")) {
			browser = "Chrome";
			version = userAgent.substring(userAgent.indexOf("Chrome/") + 7);
		} else if (userAgent.contains("Firefox")) {
			browser = "Firefox";
			version = userAgent.substring(userAgent.indexOf("Firefox/") + 8);
		} else if (userAgent.contains("MSIE")) {
			browser = "Internet Explorer";
			version = userAgent.substring(userAgent.indexOf("MSIE") + 5);
		} else if (userAgent.contains("Trident")) {
			browser = "Internet Explorer";
			version = userAgent.substring(userAgent.indexOf("rv:") + 3);
		} else if (userAgent.contains("Edge")) {
			browser = "Edge";
			version = userAgent.substring(userAgent.indexOf("Edge/") + 5);
		} else if (userAgent.contains("Safari")) {
			browser = "Safari";
			version = userAgent.substring(userAgent.indexOf("Version/") + 8);
		}
		
		if (Integer.parseInt(version.substring(0, version.indexOf('.'))) < 110) {
			System.out.println(Integer.parseInt(version.substring(0, version.indexOf('.'))));
			req.setAttribute("browser", browser);
			req.setAttribute("version", "yes");
			req.setAttribute("versionint", Integer.parseInt(version.substring(0, version.indexOf('.'))));
		} else {
			System.out.println(Integer.parseInt(version.substring(0, version.indexOf('.'))));
			req.setAttribute("browser", browser);
			req.setAttribute("version", "no");
			req.setAttribute("versionint", Integer.parseInt(version.substring(0, version.indexOf('.'))));
		}

		System.out.println("Error-----"+error);
		
		logger.info(new Date() + "Inside login ");
		if (error != null)
			model.addAttribute("error", (error.equalsIgnoreCase("Invalid Captcha")||  error.equalsIgnoreCase("There have been several failed attempts. Please wait a while and try again later"))? error: "Your username and password is invalid.");

		if (logout != null)
			model.addAttribute("message", "You have been logged out successfully.");

		try {

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside login.htm " + e);
		}
		
		// License Expiration Validation 
 		boolean expstatus = true;
// 		try {
// 			Jws<Claims> claims = Jwts.parser().setSigningKey("vts-123").parseClaimsJws(env.getProperty("license"));
// 			expstatus = true;
// 		} catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
// 			expstatus = false;
// 		} catch (ExpiredJwtException ex) {
// 			expstatus = false;
// 		}
 		
 		// only generate once per session
 		String sessionKey = (String) req.getSession().getAttribute("LOGIN_AES_KEY");
 		String sessionIv  = (String) req.getSession().getAttribute("LOGIN_AES_IV");

 		if (sessionKey == null || sessionIv == null) {
 		    byte[] keyBytes = new byte[16];
 		    new SecureRandom().nextBytes(keyBytes);
 		    sessionKey = Base64.getEncoder().encodeToString(keyBytes);

 		    byte[] ivBytes = new byte[16];
 		    new SecureRandom().nextBytes(ivBytes);
 		    sessionIv = Base64.getEncoder().encodeToString(ivBytes);

 		    req.getSession().setAttribute("LOGIN_AES_KEY", sessionKey);
 		    req.getSession().setAttribute("LOGIN_AES_IV", sessionIv);

 		}

 		req.setAttribute("sessionKey", sessionKey);
 		req.setAttribute("sessionIv", sessionIv);

 		
// 		String captcha = org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric(5);
// 		req.getSession().setAttribute("LOGIN_CAPTCHA", captcha);
// 		req.setAttribute("captcha", captcha);
 		
// 		String captcha = org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric(5);
// 		System.out.println(captcha+"----captcha");
// 		
// 		
// 	    req.getSession().setAttribute("LOGIN_CAPTCHA", captcha);
// 	    req.setAttribute("captcha", generateCaptchaImage(captcha)); 

 		req.setAttribute("expstatus", expstatus);
		return "static/login";
	}
    
	
	private String generateCaptchaSvgBase64(String text) {
	    int width = 150, height = 50;
	    Random rnd = new Random();

	    StringBuilder sb = new StringBuilder();
	    sb.append("<?xml version='1.0' encoding='UTF-8'?>");
	    sb.append("<svg xmlns='http://www.w3.org/2000/svg' ")
	      .append("width='").append(width).append("' height='").append(height).append("' ")
	      .append("viewBox='0 0 ").append(width).append(" ").append(height).append("'>");

	    // background
	    sb.append("<rect width='100%' height='100%' fill='white'/>");

	    // random noise lines
	    for (int i = 0; i < 6; i++) {
	        int x1 = rnd.nextInt(width), y1 = rnd.nextInt(height);
	        int x2 = rnd.nextInt(width), y2 = rnd.nextInt(height);
	        sb.append("<line x1='").append(x1).append("' y1='").append(y1)
	          .append("' x2='").append(x2).append("' y2='").append(y2)
	          .append("' stroke='gray' stroke-width='1' stroke-opacity='0.6'/>");
	    }

	    // characters with small random rotation/offset
	    int len = text.length();
	    for (int i = 0; i < len; i++) {
	        char c = text.charAt(i);
	        int fx = 12 + i * (width - 24) / Math.max(1, len) + rnd.nextInt(7) - 3;
	        int fy = 30 + rnd.nextInt(9) - 4;
	        int rot = rnd.nextInt(30) - 15;
	        sb.append("<text x='").append(fx).append("' y='").append(fy)
	          .append("' font-family='sans-serif' font-size='28' ")
	          .append("transform='rotate(").append(rot).append(" ").append(fx).append(" ").append(fy).append(")'>")
	          .append(c)
	          .append("</text>");
	    }

	    // dots
	    for (int i = 0; i < 20; i++) {
	        int cx = rnd.nextInt(width), cy = rnd.nextInt(height);
	        sb.append("<circle cx='").append(cx).append("' cy='").append(cy).append("' r='1' fill='gray'/>");
	    }

	    sb.append("</svg>");

	    return Base64.getEncoder().encodeToString(sb.toString().getBytes(StandardCharsets.UTF_8));
	}

	@GetMapping("/refresh-captcha")
	@ResponseBody
	public Map<String, String> refreshCaptcha(HttpServletRequest req) {
	    String captcha = RandomStringUtils.randomAlphanumeric(5);
	    req.getSession().setAttribute("LOGIN_CAPTCHA", captcha);

	    String svgBase64 = generateCaptchaSvgBase64(captcha);
	    Map<String, String> result = new HashMap<>();
	    result.put("captcha", "data:image/svg+xml;base64," + svgBase64);
	    return result;
	}
	
	@RequestMapping(value = "/wr", method = RequestMethod.GET)
	public String loginWR(@RequestHeader(name = "User-Agent") String userAgent, Model model, 
			@RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "logout", required = false) String logout,
			HttpServletRequest req, HttpSession ses, HttpServletResponse response) {
		
		req.getSession().setAttribute("loginPage", "wr");
		
		String browser = req.getHeader("user-agent");
		String[] br = browser.split("\\)");
		String version = "";
		userAgent=br[br.length-1];
		if (userAgent.contains("Chrome")) {
			browser = "Chrome";
			version = userAgent.substring(userAgent.indexOf("Chrome/") + 7);
		} else if (userAgent.contains("Firefox")) {
			browser = "Firefox";
			version = userAgent.substring(userAgent.indexOf("Firefox/") + 8);
		} else if (userAgent.contains("MSIE")) {
			browser = "Internet Explorer";
			version = userAgent.substring(userAgent.indexOf("MSIE") + 5);
		} else if (userAgent.contains("Trident")) {
			browser = "Internet Explorer";
			version = userAgent.substring(userAgent.indexOf("rv:") + 3);
		} else if (userAgent.contains("Edge")) {
			browser = "Edge";
			version = userAgent.substring(userAgent.indexOf("Edge/") + 5);
		} else if (userAgent.contains("Safari")) {
			browser = "Safari";
			version = userAgent.substring(userAgent.indexOf("Version/") + 8);
		}
		
		if (Integer.parseInt(version.substring(0, version.indexOf('.'))) < 110) {
			System.out.println(Integer.parseInt(version.substring(0, version.indexOf('.'))));
			req.setAttribute("browser", browser);
			req.setAttribute("version", "yes");
			req.setAttribute("versionint", Integer.parseInt(version.substring(0, version.indexOf('.'))));
		} else {
			System.out.println(Integer.parseInt(version.substring(0, version.indexOf('.'))));
			req.setAttribute("browser", browser);
			req.setAttribute("version", "no");
			req.setAttribute("versionint", Integer.parseInt(version.substring(0, version.indexOf('.'))));
		}
		
		logger.info(new Date() + "Inside login ");
		if (error != null)
			model.addAttribute("error", "Your username and password is invalid.");
		
		if (logout != null)
			model.addAttribute("message", "You have been logged out successfully.");
		
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside login.htm " + e);
		}
		
		// License Expiration Validation 
		boolean expstatus = true;
//		try {
//			Jws<Claims> claims = Jwts.parser().setSigningKey("vts-123").parseClaimsJws(env.getProperty("license"));
//			expstatus = true;
//		} catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
//			expstatus = false;
//		} catch (ExpiredJwtException ex) {
//			expstatus = false;
//		}
		
		// only generate once per session
 		String sessionKey = (String) req.getSession().getAttribute("LOGIN_AES_KEY");
 		String sessionIv  = (String) req.getSession().getAttribute("LOGIN_AES_IV");

 		if (sessionKey == null || sessionIv == null) {
 		    byte[] keyBytes = new byte[16];
 		    new SecureRandom().nextBytes(keyBytes);
 		    sessionKey = Base64.getEncoder().encodeToString(keyBytes);

 		    byte[] ivBytes = new byte[16];
 		    new SecureRandom().nextBytes(ivBytes);
 		    sessionIv = Base64.getEncoder().encodeToString(ivBytes);

 		    req.getSession().setAttribute("LOGIN_AES_KEY", sessionKey);
 		    req.getSession().setAttribute("LOGIN_AES_IV", sessionIv);

 		}

 		req.setAttribute("sessionKey", sessionKey);
 		req.setAttribute("sessionIv", sessionIv);
		 		
		req.setAttribute("expstatus", expstatus);
		return "static/login-wr";
	}
	
    @RequestMapping(value = {"/TMDS"}, method = RequestMethod.GET)
    public String welcomeFromTMDS(Model model,HttpServletRequest req,HttpSession ses) throws Exception {
    	
    	return "Hii";
    }
	
    @RequestMapping(value = {"/", "/welcome"}, method = RequestMethod.GET)
    public String welcome(Model model,HttpServletRequest req,HttpSession ses) throws Exception {
    	
      logger.info(new Date() +" Login By "+req.getUserPrincipal().getName());
   
      try {
    	  // other session setup code
    	    String loginPage = (String) ses.getAttribute("loginPage");
		    long LoginId=Repository.findByUsername(req.getUserPrincipal().getName()).getLoginId();
		    Object[] empdetails = headerservice.EmployeeDetailes(String.valueOf(LoginId)).get(0);

		    ses.setAttribute("Username",req.getUserPrincipal().getName());
		    ses.setAttribute("LoginId",LoginId ); 	
		    ses.setAttribute("Division", Repository.findByUsername(req.getUserPrincipal().getName()).getDivisionId()); 	
		    ses.setAttribute("LoginType", Repository.findByUsername(req.getUserPrincipal().getName()).getLoginType()); 
		    ses.setAttribute("EmpId", Repository.findByUsername(req.getUserPrincipal().getName()).getEmpId()); 
		    ses.setAttribute("FormRole", Repository.findByUsername(req.getUserPrincipal().getName()).getFormRoleId());
		    ses.setAttribute("EmpName", empdetails[0]);
		    ses.setAttribute("EmpNo", empdetails[2].toString());
		    // ses.setAttribute("LoginAs", Repository.findByUsername(req.getUserPrincipal().getName()).getLoginType()); 
		    ses.setAttribute("ProjectId", "0");
		  	ses.setAttribute("DesgId", rfpmainservice.DesgId(Repository.findByUsername(req.getUserPrincipal().getName()).getEmpId().toString()));
		  	ses.setAttribute("LoginTypeName", headerservice.FormRoleName(Repository.findByUsername(req.getUserPrincipal().getName()).getLoginType()));
		  	ses.setAttribute("labid",headerservice.LabDetails(empdetails[3].toString())[0].toString());
		  	//ses.setAttribute("ProjectInitiationList", headerservice.ProjectIntiationList(Repository.findByUsername(req.getUserPrincipal().getName()).getEmpId().toString(),Repository.findByUsername(req.getUserPrincipal().getName()).getLoginType()).size());
		 	ses.setAttribute("DashBoardId", headerservice.getDashBoardId(Repository.findByUsername(req.getUserPrincipal().getName()).getEmpId(),Repository.findByUsername(req.getUserPrincipal().getName()).getLoginType())!=null && headerservice.getDashBoardId(Repository.findByUsername(req.getUserPrincipal().getName()).getEmpId(),Repository.findByUsername(req.getUserPrincipal().getName()).getLoginType()).size()>0 ? headerservice.getDashBoardId(Repository.findByUsername(req.getUserPrincipal().getName()).getEmpId(),Repository.findByUsername(req.getUserPrincipal().getName()).getLoginType()).get(0)[0].toString():"0");
		  	
		  	String LabCode = headerservice.getLabCode(Repository.findByUsername(req.getUserPrincipal().getName()).getUsername().toString()).trim();
		  	
		  	ses.setAttribute("labcode", LabCode);
		 	ses.setAttribute("clusterid", headerservice.LabDetails(empdetails[3].toString())[1].toString());
		
		 	String DGName = headerservice.LabMasterList(headerservice.LabDetails(empdetails[3].toString())[1].toString()).stream().filter(e-> "Y".equalsIgnoreCase(e[2].toString())).collect(Collectors.toList()).get(0)[1].toString();
		 	String IsDG = "No";
		    if(DGName.equalsIgnoreCase(LabCode))
		   	 IsDG = "Yes";
		    else
		   	 IsDG = "No";
		    ses.setAttribute("IsDG", IsDG);
		 
		 	req.setAttribute("loginTypeList", headerservice.loginTypeList(Repository.findByUsername(req.getUserPrincipal().getName()).getLoginType()));
//		    req.setAttribute("DashboardDemandCount", headerservice.DashboardDemandCount().get(0));
		
		    String empNo=rfpmainservice.getEmpNo(Repository.findByUsername(req.getUserPrincipal().getName()).getEmpId());
		    ses.setAttribute("empNo", empNo);
			ses.setAttribute("token",JwtUtil.generateToken(req.getUserPrincipal().getName()));
		    long pwdCount = headerservice.PasswordChangeHystoryCount(LoginId);
			if(pwdCount==0) 
			{
				return "redirect:/ForcePasswordChange.htm";
			}
			
		    if(loginPage.equalsIgnoreCase("login")) {
		    	return "redirect:/MainDashBoard.htm";
		    }else {
		    	return "redirect:/TimeSheetList.htm";
		    }
		    
		    
      }catch (Exception e) {
    	e.printStackTrace();
    	 logger.error(new Date() +" Login Problem Occures When Login By "+req.getUserPrincipal().getName(), e);
    	 return "static/Error";
     }
     
    }
    
    @RequestMapping(value = { "/welcome-react"}, method = RequestMethod.GET)
    public String welcomeFromReact(Model model,HttpServletRequest req,HttpSession ses) throws Exception {
    	
      logger.info(new Date() +" Login By "+req.getUserPrincipal().getName());
      String username= (String) ses.getAttribute("username");
      
      System.out.println("username-----123"+username);
      try {
    	  // other session setup code
    	    String loginPage = (String) ses.getAttribute("loginPage");
    	
		    long LoginId=Repository.findByUsername(username).getLoginId();
		    Object[] empdetails = headerservice.EmployeeDetailes(String.valueOf(LoginId)).get(0);

		    ses.setAttribute("Username",username);
		    ses.setAttribute("LoginId",LoginId ); 	
		    ses.setAttribute("Division", Repository.findByUsername(username).getDivisionId()); 	
		    ses.setAttribute("LoginType", Repository.findByUsername(username).getLoginType()); 
		    ses.setAttribute("EmpId", Repository.findByUsername(username).getEmpId()); 
		    ses.setAttribute("FormRole", Repository.findByUsername(username).getFormRoleId());
		    ses.setAttribute("EmpName", empdetails[0]);
		    ses.setAttribute("EmpNo", empdetails[2].toString());
		    // ses.setAttribute("LoginAs", Repository.findByUsername(req.getUserPrincipal().getName()).getLoginType()); 
		    ses.setAttribute("ProjectId", "0");
		  	ses.setAttribute("DesgId", rfpmainservice.DesgId(Repository.findByUsername(username).getEmpId().toString()));
		  	ses.setAttribute("LoginTypeName", headerservice.FormRoleName(Repository.findByUsername(username).getLoginType()));
		  	ses.setAttribute("labid",headerservice.LabDetails(empdetails[3].toString())[0].toString());
		  	//ses.setAttribute("ProjectInitiationList", headerservice.ProjectIntiationList(Repository.findByUsername(req.getUserPrincipal().getName()).getEmpId().toString(),Repository.findByUsername(req.getUserPrincipal().getName()).getLoginType()).size());
		 	ses.setAttribute("DashBoardId", headerservice.getDashBoardId(Repository.findByUsername(username).getEmpId(),Repository.findByUsername(username).getLoginType())!=null && headerservice.getDashBoardId(Repository.findByUsername(username).getEmpId(),Repository.findByUsername(username).getLoginType()).size()>0 ? headerservice.getDashBoardId(Repository.findByUsername(username).getEmpId(),Repository.findByUsername(username).getLoginType()).get(0)[0].toString():"0");
		  	
		  	String LabCode = headerservice.getLabCode(Repository.findByUsername(username).getUsername().toString()).trim();
		  	
		  	ses.setAttribute("labcode", LabCode);
		 	ses.setAttribute("clusterid", headerservice.LabDetails(empdetails[3].toString())[1].toString());
		
		 	
		 
		 	
		 	String DGName = headerservice.LabMasterList(headerservice.LabDetails(empdetails[3].toString())[1].toString()).stream().filter(e-> "Y".equalsIgnoreCase(e[2].toString())).collect(Collectors.toList()).get(0)[1].toString();
		 	String IsDG = "No";
		    if(DGName.equalsIgnoreCase(LabCode))
		   	 IsDG = "Yes";
		    else
		   	 IsDG = "No";
		    ses.setAttribute("IsDG", IsDG);
		 
		 	req.setAttribute("loginTypeList", headerservice.loginTypeList(Repository.findByUsername(username).getLoginType()));
//		    req.setAttribute("DashboardDemandCount", headerservice.DashboardDemandCount().get(0));
		
		    String empNo=rfpmainservice.getEmpNo(Repository.findByUsername(username).getEmpId());
		    ses.setAttribute("empNo", empNo);
		    
		    long pwdCount = headerservice.PasswordChangeHystoryCount(LoginId);
			if(pwdCount==0) 
			{
				return "redirect:/ForcePasswordChange.htm";
			}
			
		    if(loginPage.equalsIgnoreCase("login")) {
		    	return "redirect:/MainDashBoard.htm";
		    }else {
		    	return "redirect:/TimeSheetList.htm";
		    }
		    
		    
      }catch (Exception e) {
    	e.printStackTrace();
    	 logger.error(new Date() +" Login Problem Occures When Login By "+username, e);
    	 return "static/Error";
     }
     
    }
    
    @RequestMapping(value = "weeklyupdate.htm")
	public String weeklyupdate(HttpServletRequest req, HttpSession ses) {
		String procurement = req.getParameter("Procurement");
		String actionpoints = req.getParameter("actionpoints");
		String riskdetails = req.getParameter("riskdetails");
		String Meeting = req.getParameter("Meeting");
		String Mile = req.getParameter("Mile");
		int projectid = Integer.parseInt(req.getParameter("USubmit").toString());
		LocalDate currentDate = LocalDate.now();
		service.weeklyupdate(Integer.parseInt(ses.getAttribute("EmpId").toString()), ses.getAttribute("empNo").toString(),currentDate.toString(), procurement, actionpoints, riskdetails, Meeting, Mile, projectid);
		LocalDate today = LocalDate.now();

		return "redirect:/MainDashBoard.htm";
	}
	
	
	
	@RequestMapping(value = "GetUpdateDates.htm", method = RequestMethod.GET)
	public @ResponseBody String GetUpdateDates(HttpServletRequest req, HttpSession ses) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String empno = ((String) ses.getAttribute("empNo")).toString();
		String LabCode = (String) ses.getAttribute("labcode");
		List<List<Object[]>> projectwiseaction = new ArrayList<>();

		List<Object[]> projects = new ArrayList<>();
		projects = Actservice.getProjectByDirectorID(EmpId);

		int pid = Integer.parseInt(req.getParameter("ProjectId").toString());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		String iD = "";

		List<Object[]> riskdatalist = Actservice.getRiskDetails(LabCode, pid);
		List<Object[]> milestone = Actservice.getMilestoneDate(pid);
		List<Object[]> Meetingdate = Actservice.getMeetingDate(pid);
		List<Object[]> Procurementdate = Actservice.getProdurementDate(pid);
		projectwiseaction.add(Actservice.getmodifieddate(UserId, pid));

		List<Object[]> weeklyupdatedate = new ArrayList<>();
		weeklyupdatedate = Actservice.getRecentWeeklyUpdateDate(req.getParameter("ProjectId"));

		if (projectwiseaction.get(0).get(0)[0] != null) {

			LocalDate inputDate = LocalDate.parse(projectwiseaction.get(0).get(0)[0].toString().subSequence(0, 10),
					DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			iD = inputDate.format(formatter);
			projectwiseaction.get(0).get(0)[0] = iD;
		}

		if (weeklyupdatedate.size() > 0) {
			System.out.println("weekly update date is " + weeklyupdatedate.get(0)[0]);
			LocalDate inputDate = LocalDate.parse(weeklyupdatedate.get(0)[0].toString().subSequence(0, 10),
					DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			iD = inputDate.format(formatter);
			weeklyupdatedate.get(0)[0] = iD;
		}

		if (riskdatalist.size() > 0)
			if (riskdatalist.get(0)[0] != null) {
				LocalDate inputDate = LocalDate.parse(riskdatalist.get(0)[0].toString().subSequence(0, 10),
						DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				iD = inputDate.format(formatter);
				riskdatalist.get(0)[0] = iD;
			}

		if (milestone.size() > 0)
			if (milestone.get(0)[0] != null) {
				LocalDate inputDate = LocalDate.parse(milestone.get(0)[0].toString().subSequence(0, 10),
						DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				iD = inputDate.format(formatter);
				milestone.get(0)[0] = iD;
			}

		if (Meetingdate.size() > 0 && Meetingdate.get(0)[0]!=null) {
			LocalDate inputDate = LocalDate.parse(Meetingdate.get(0)[0].toString().subSequence(0, 10),
					DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			iD = inputDate.format(formatter);
			Meetingdate.get(0)[0] = iD;
		}
		if (Procurementdate.size() > 0)
			if (Procurementdate.get(0)[0] != null) {
				LocalDate inputDate = LocalDate.parse(Procurementdate.get(0)[0].toString().subSequence(0, 10),
						DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				iD = inputDate.format(formatter);
				System.out.println("procurement status date " + iD);
				Procurementdate.get(0)[0] = iD;
			}

		projectwiseaction.add(weeklyupdatedate);
		projectwiseaction.add(riskdatalist);
		projectwiseaction.add(milestone);
		projectwiseaction.add(Meetingdate);
		projectwiseaction.add(Procurementdate);
		System.out.println(Procurementdate.get(0)[0]);
		Gson convertedgson = new Gson();
		return convertedgson.toJson(projectwiseaction);
	}
	
//	@RequestMapping(value = "WeeklyUpdateReport.htm", method = {RequestMethod.GET,RequestMethod.POST})
//	public String WeeklyUpdateReport(HttpServletRequest req, HttpSession ses) throws Exception {
//		String LoginType = (String) ses.getAttribute("LoginType");
//		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
//		String LabCode = (String) ses.getAttribute("labcode");
//		List<Object[]> updatedProjectsList = new ArrayList<>();
//		
//		
//		List<Object[]> projects = new ArrayList<>();
//		List<Object[]> projectsReturns = new ArrayList<>();
//				projects = rfpmainservice.ProjectList(LoginType, EmpId, LabCode);
//		
//		req.setAttribute("projects", projects);
//		req.setAttribute("tableshow", "no");
//		System.out.println(req.getMethod());
//		if(req.getMethod().equals("POST")) {
//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//			System.out.println("in post");
//			String pid = String.valueOf(req.getParameter("getprojects").toString());
//			updatedProjectsList = Actservice.getAllRecentUpdateList(pid);
//			System.out.println(updatedProjectsList.size());
//			if(updatedProjectsList.size()>0)
//			req.setAttribute("tableshow", "yes");
//			else
//				req.setAttribute("resultfail", "Not Yet Updated");
//			for(int i=0;i<updatedProjectsList.size();i++)
//			{
//				updatedProjectsList.get(i)[1] = Actservice.getEmpnameById(Integer.parseInt((String.valueOf(updatedProjectsList.get(i)[1]))));
//				String strNew = String.valueOf(updatedProjectsList.get(i)[1]).replace("[", "");
//				strNew = String.valueOf(strNew).replace("]", "");
//				updatedProjectsList.get(i)[1] = strNew;
//				strNew =String.valueOf(Actservice.getProjectCodeById(Integer.parseInt(pid))).replace("[", "");
//				strNew = String.valueOf(strNew).replace("]", "");
//				req.setAttribute("projectname", strNew );
//				
//				
//				
//				
//				
//				strNew =String.valueOf(Actservice.getProjectShortNameById(Integer.parseInt(pid))).replace("[", "");
//				strNew = String.valueOf(strNew).replace("]", "");
//				req.setAttribute("shortname", strNew );
//				
//				LocalDate iD = LocalDate.parse(updatedProjectsList.get(i)[3].toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//				String inputDate = iD.format(formatter);
//				updatedProjectsList.get(i)[3] = inputDate;
//				
//				
//				iD = LocalDate.parse(updatedProjectsList.get(i)[10].toString().substring(0,10 ), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//				inputDate = iD.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
//				updatedProjectsList.get(i)[10] = updatedProjectsList.get(i)[10].toString()
//						.replace(updatedProjectsList.get(i)[10].toString().substring(0,10), inputDate.toString());
//				
//				
//			}
//			String selectedproj = "";
//			for(int i=0;i<projects.size();i++)
//			{
//				if(Integer.valueOf(pid) == Integer.valueOf(projects.get(i)[0].toString()) ) {
//					selectedproj=projects.get(i)[4].toString()+projects.get(i)[17];
//					projects.remove(i);}
//			}
//			
//			req.setAttribute("projects", projects);
//			req.setAttribute("selectedProject", selectedproj);
//			
//		
//			
//		}
//		req.setAttribute("tabledata", updatedProjectsList);
//		return "master/WeeklyUpdateReport";
//	}
	
	@RequestMapping(value = "WeeklyUpdateReport.htm", method = { RequestMethod.GET, RequestMethod.POST })
	public String WeeklyUpdateReport(HttpServletRequest req, HttpSession ses) throws Exception {
		String LoginType = (String) ses.getAttribute("LoginType");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String LabCode = (String) ses.getAttribute("labcode");
		List<Object[]> updatedProjectsList = new ArrayList<>();

		List<Object[]> projects = new ArrayList<>();
		List<Object[]> projectsReturns = new ArrayList<>();
		projects = rfpmainservice.ProjectList(LoginType, EmpId, LabCode);

		req.setAttribute("projects", projects);
		req.setAttribute("tableshow", "no");
	
		if (req.getMethod().equals("POST")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		
			String pid = req.getParameter("getprojects").toString();
			
			System.out.println("pid "+pid);
			
			updatedProjectsList = Actservice.getAllRecentUpdateList(pid);

			if (updatedProjectsList.size() > 0)
				req.setAttribute("tableshow", "yes");
			else
				req.setAttribute("resultfail", "Not Yet Updated");
			for (int i = 0; i < updatedProjectsList.size(); i++) {
				updatedProjectsList.get(i)[1] = Actservice
						.getEmpnameById(Integer.parseInt((String.valueOf(updatedProjectsList.get(i)[1]))));
				String strNew = String.valueOf(updatedProjectsList.get(i)[1]).replace("[", "");
				strNew = String.valueOf(strNew).replace("]", "");
				updatedProjectsList.get(i)[1] = strNew;
				strNew = String.valueOf(Actservice.getProjectCodeById(Integer.parseInt(pid))).replace("[", "");
				strNew = String.valueOf(strNew).replace("]", "");
				req.setAttribute("projectname", strNew);

				strNew = String.valueOf(Actservice.getProjectShortNameById(Integer.parseInt(pid))).replace("[", "");
				strNew = String.valueOf(strNew).replace("]", "");
				req.setAttribute("shortname", strNew);

				LocalDate iD = LocalDate.parse(updatedProjectsList.get(i)[3].toString(),
						DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				String inputDate = iD.format(formatter);
				updatedProjectsList.get(i)[3] = inputDate;

				iD = LocalDate.parse(updatedProjectsList.get(i)[10].toString().substring(0, 10),
						DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				inputDate = iD.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
				updatedProjectsList.get(i)[10] = updatedProjectsList.get(i)[10].toString()
						.replace(updatedProjectsList.get(i)[10].toString().substring(0, 10), inputDate.toString());

			}
			String selectedproj = "";
			for (int i = 0; i < projects.size(); i++) {
				if (Integer.valueOf(pid) == Integer.valueOf(projects.get(i)[0].toString())) {
					selectedproj = projects.get(i)[4].toString() + " ("+ projects.get(i)[17] +")";
					projects.remove(i);
				}
			}

			req.setAttribute("projects", projects);
			req.setAttribute("selectedProject", selectedproj);

		}
		req.setAttribute("tabledata", updatedProjectsList);
		return "master/WeeklyUpdateReport";
	}
	@RequestMapping(value = "GetUpdateReport.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody String GetUpdateReport(HttpServletRequest req, HttpSession ses) throws Exception {
		List<Object[]> projectwiseaction = new ArrayList<>();
		String pid = String.valueOf(req.getParameter("ProjectId").toString());
		System.out.println(pid);
		projectwiseaction.addAll(Actservice.getAllRecentUpdateList(pid));
		Gson convertedgson = new Gson();
		return convertedgson.toJson(projectwiseaction);
	}
	
    
    
    
    
    @RequestMapping(value = "MainDashBoard.htm", method = {RequestMethod.GET,RequestMethod.POST})
    public String MainDashBoard(HttpServletRequest req,HttpSession ses) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String LoginType = (String) ses.getAttribute("LoginType");
		String LoginId = String.valueOf(ses.getAttribute("LoginId"));
		String empNo = (String) ses.getAttribute("empNo");
		String LabCode = (String) ses.getAttribute("labcode");
		String ClusterId = (String) ses.getAttribute("clusterid");
		String statsUrl   = env.getProperty("stats_url");

		//check if it is project director or qioc
		if (LoginType.equals("Q") || LoginType.equals("P")) {

			List<Object[]> projects = new ArrayList<>();


			// project master, only get projects of THIS user
			//which we will display all in dropdown
			if(LoginType.equals("P"))
				projects = service.ProjectListPD(EmpId);
				else
				projects = service.ProjectListIC(EmpId);

			// returns dates that are less than 7 days old, we delete these projects in
			// 'Total Projects To Show' in dropdown
			List<Object[]> l = service.lastUpdate();
			for (Object[] objects : l) {
				System.out.println(objects[0]);
			}
			// if there is no existing data is updated, it will directly sent to dropdown,
			// with empty last update date
			if (l.size() == 0 && projects.size() > 0) {
				req.setAttribute("projectsOfEmp", projects);
				req.setAttribute("showmodal", "yes");
			} 
			// de-assigned projects || 
			else if (l.size() > 0 && projects.size() == 0) 
			{ 
				// else no
				req.setAttribute("showmodal", "no");
			}

			//some updates are done
			if (l.size() > 0) {
				// multi iter for removing already updated project status
				for (int i = 0; i < l.size(); i++) {
					for (int j = 0; j < projects.size(); j++)
						if (l.get(i)[1].toString().equals(projects.get(j)[0].toString()))
							projects.remove(j);
				}
				// if atleast one project has been not updated, then show
				if (projects.size() > 0) {
					req.setAttribute("showmodal", "yes");
					req.setAttribute("lastupdatedate", l);
					req.setAttribute("projectsOfEmp", projects);
				} else {
					// when all projects are already updated, and nothing yet to update
					req.setAttribute("showmodal", "no");
				}
			}
			//no projects & no updates
			if (projects.size() == 0)
			{
				req.setAttribute("showmodal", "no");
			}
		}
		//if not projector or qioc
		else {
			// as in front end it continusly checks for the showmodal,
			// null value will not allow rest of page to render, so no is sent
			req.setAttribute("showmodal", "no");
		}

		String ProjectId = "A";
		if (req.getParameter("projectid") != null) {
			ProjectId = req.getParameter("projectid");
		}
		String preferedDashBoard=req.getParameter("preferedDashBoard");
		
		logger.info(new Date() + "Inside MainDashBoard.htm ");
		try {
			req.setAttribute("statsUrl", statsUrl);
			System.out.println("statsUrl###########"+statsUrl);
			req.setAttribute("loginTypeList", headerservice.loginTypeList(LoginType));
//			req.setAttribute("DashboardDemandCount", headerservice.DashboardDemandCount().get(0));
			req.setAttribute("todayschedulelist", headerservice.TodaySchedulesList(EmpId, LocalDate.now().toString()));
            //req.setAttribute("todayactionlist", headerservice.TodayActionList(EmpId)); // CALL Pfms_Action_PDC(:empid)
			req.setAttribute("dashbordNotice", rfpmainservice.GetNotice(LabCode));
			req.setAttribute("noticeEligibility", rfpmainservice.GetNoticeEligibility(EmpId));
			req.setAttribute("logintype", LoginType);
			req.setAttribute("selfremindercount", rfpmainservice.SelfActionsList(EmpId).size());
			
			// req.setAttribute("NotiecList",rfpmainservice.getAllNotice());
           //req.setAttribute("budgetlist",rfpmainservice.ProjectBudgets());
			req.setAttribute("ibasUri", ibasUri);
			req.setAttribute("interval", interval);
//			req.setAttribute("ibasV3Uri", "http:8082/ibas");
//			req.setAttribute("ProjectInitiationList", headerservice.ProjectIntiationList(EmpId, LoginType).size());
			req.setAttribute("mytasklist", headerservice.MyTaskList(EmpId));
			req.setAttribute("approvallist", headerservice.ApprovalList(EmpId, LoginType));
			req.setAttribute("mytaskdetails",
			headerservice.MyTaskDetails(EmpId)); /* CALL `Dashboard_Mytask_Details` (:empid) */
			req.setAttribute("dashboardactionpdc", headerservice.DashboardActionPdc(EmpId, LoginType));
			req.setAttribute("QuickLinkList", headerservice.QuickLinksList(LoginType));
			
			//to find favourite DashBoard
			req.setAttribute("DashBoardsList", headerservice.getDashBoards(Long.parseLong(EmpId),LoginType));
		
			String DashBoardId = (String) ses.getAttribute("DashBoardId");
			List<Object[]>projecthealthdata = rfpmainservice.ProjectHealthData(LabCode);
			//Object[]projecthealthtotal = rfpmainservice.ProjectHealthTotalData(ProjectId, EmpId, LoginType, LabCode, "Y");
			//List<Object[]>DashboardFinance = rfpmainservice.DashboardFinance(LoginType, EmpId, LabCode, ClusterId);
			if(DashBoardId!=null && !DashBoardId.equalsIgnoreCase("0")) {
			ProjectDashBoardFavourite pd=headerservice.findProjectDashBoardFavourite(Long.parseLong(DashBoardId));
			
			List<String>DashBoardProjects= new ArrayList<>();
			// get the projects & add in temporaryList
			if(pd.getProjects()!=null) {
			for(ProjectDashBoardFavouriteProjetcts p:pd.getProjects()) {
				DashBoardProjects.add(p.getProjectId()+"");
			}
			}else {
				List<Object[]>projectLists = rfpmainservice.ProjectList(LoginType, EmpId, LabCode);
				for(Object[]obj:projectLists) {
					DashBoardProjects.add(obj[0].toString()+"");
				}
			}
			
			projecthealthdata = projecthealthdata.stream().filter(e->DashBoardProjects.contains(e[2].toString())).collect(Collectors.toList());
			//projecthealthtotal = headerservice.projecthealthtotalDashBoardwise(DashBoardId,LabCode);
			//DashboardFinance = headerservice.DashboardFinanceProjectWise(DashBoardId,LabCode);
			}
			req.setAttribute("projecthealthdata", projecthealthdata);
			//req.setAttribute("projecthealthtotal",projecthealthtotal);

			// req.setAttribute("clusterlablist", headerservice.LabList());
			// req.setAttribute("clusterlist", comservice.ClusterList());
			// req.setAttribute("CCMFinanceData",rfpmainservice.getCCMData(EmpId,LoginType,LabCode));
			req.setAttribute("DashboardFinanceCashOutGo",
					rfpmainservice.DashboardFinanceCashOutGo(LoginType, EmpId, LabCode, ClusterId));
			//req.setAttribute("DashboardFinance", DashboardFinance);

			String DGName = Optional.ofNullable(
					headerservice.LabMasterList(ClusterId).stream().filter(e -> "Y".equalsIgnoreCase(e[2].toString()))
							.collect(Collectors.toList()).get(0)[1].toString())
					.orElse("");
			
			System.out.println("DGName&&&&&&"+DGName);

			String IsDG = "No";
			System.out.println("DGName" + DGName + "LoginType" + LoginType);
			if (DGName.equalsIgnoreCase(LabCode) || LoginType.equalsIgnoreCase("K"))
				IsDG = "Yes";
			else
				IsDG = "No";
			req.setAttribute("IsDG", IsDG);

			List<Object[]> labdatalist = new ArrayList<Object[]>();
			List<Object[]> LabMasterList = new ArrayList<Object[]>();
			String InAll = "N";

			if (LoginType.equalsIgnoreCase("K")) {// For Secretary
				LabMasterList = headerservice.LabMasterList(ClusterId).stream()
						.filter(e -> "Y".equalsIgnoreCase(e[2].toString())).collect(Collectors.toList());
				InAll = "Y";
			} else {
				LabMasterList = headerservice.LabMasterList(ClusterId).stream()
						.filter(e -> "N".equalsIgnoreCase(e[2].toString())).collect(Collectors.toList());
			}

			//Only for DG view call this method
			if(IsDG.equalsIgnoreCase("YES")) {
			for (Object[] obj : LabMasterList) {
				labdatalist.add(rfpmainservice.ProjectHealthTotalData(ProjectId, EmpId, LoginType,
						obj[1].toString().trim(), InAll));
			}
			}
			req.setAttribute("projecthealthtotaldg", labdatalist);

			if (!LoginType.equalsIgnoreCase("U") || !LoginType.equalsIgnoreCase("K")) {

				// code for pfms service call to get data for project pie chart
				


				// req.setAttribute("AllSchedulesCount",
				// rfpmainservice.AllSchedulesCount(LoginType,LoginId));
				req.setAttribute("actionscount", rfpmainservice.AllActionsCount(LoginType, EmpId, LoginId, LabCode));
				req.setAttribute("ProjectList", rfpmainservice.ProjectList(LoginType, EmpId, LabCode));
				req.setAttribute("ProjectMeetingCount", rfpmainservice.ProjectMeetingCount(LoginType, EmpId, LabCode));
				req.setAttribute("ganttchartlist", rfpmainservice.GanttChartList());
				
				if(LabCode.equalsIgnoreCase("ADE")) {
				
				LocalDate currentDate = LocalDate.now();
				LocalDate firstDayOfYear = LocalDate.of(currentDate.getYear(), 1, 1);
				String fdate = firstDayOfYear.toString();
				LocalDate lastDayOfYear = LocalDate.of(currentDate.getYear(), 12, 31);
				String tdate=lastDayOfYear.toString();
				req.setAttribute("rfaPendingCountList", Actservice.GetRfaActionList(EmpId,"A","A","A", tdate, tdate));
				req.setAttribute("rfaForwardCount",Actservice.RfaForwardList(EmpId).size());
				req.setAttribute("rfaInspectionCount",Actservice.RfaInspectionList(EmpId).size());
				req.setAttribute("rfaInspectionAprCount",Actservice.RfaInspectionApprovalList(EmpId).size());
				
				}
				long start = System.currentTimeMillis();
				try {
					req.setAttribute("budgetlist", PFMSServ.getDetailsOfSupplyOrder(LoginType, empNo));
					System.out.println("Feign Call Success after " + (System.currentTimeMillis() - start) + "ms");
					}catch (Exception e) {
						System.out.println("Feign Call Failed after " + (System.currentTimeMillis() - start) + "ms");
						//e.printStackTrace();
						req.setAttribute("budgetlist",new ArrayList<>());
					}
			}

		} catch (Exception e) {

			logger.error(new Date() + " Inside MainDashboard.htm " + UserId, e);
			e.printStackTrace();
		}

		return "static/MainDashBoard";
	}
 
    
    @RequestMapping(value = "IndividualProjectDetails.htm", method = RequestMethod.GET)
	public @ResponseBody String IndividualProjectDetails(HttpServletRequest req, HttpSession ses) throws Exception {
		
    	
	 	Object[] IndividualProjectDetails=null;
	 	String UserId =(String)ses.getAttribute("Username");
	 	String EmpId= ((Long) ses.getAttribute("EmpId")).toString();
    	String LoginType=(String)ses.getAttribute("LoginType");
    	String LabCode=(String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside IndividualProjectDetails.htm "+UserId);		
		try {
			
			IndividualProjectDetails = rfpmainservice.ProjectHealthTotalData(req.getParameter("ProjectId"),EmpId,LoginType,LabCode,"Y");
		
		}
		catch (Exception e) {
			e.printStackTrace();
			 logger.error(new Date() +" Inside IndividualProjectDetails.htm "+UserId, e);
		}
	
		Gson convertedgson = new Gson();
		return convertedgson.toJson(IndividualProjectDetails);

	}
    
    @RequestMapping(value = "ChangesDataTotalCount.htm", method = RequestMethod.GET)
   	public @ResponseBody String ChangesDataTotalCount(HttpServletRequest req, HttpSession ses) throws Exception {
   		
   	 	Object[] ChangesDataTotalCount=null;
   	 	String UserId =(String)ses.getAttribute("Username");
   		logger.info(new Date() +"Inside ChangesDataTotalCount.htm "+UserId);		
   		try {
   			
   			ChangesDataTotalCount =  rfpmainservice.ChangesTotalCountData(req.getParameter("ProjectId"));
   		
   		}
   		catch (Exception e) {
   			e.printStackTrace();
   			 logger.error(new Date() +" Inside IndividualProjectDetails.htm "+UserId, e);
   		}
   	
   		Gson convertedgson = new Gson();
   		return convertedgson.toJson(ChangesDataTotalCount);

   	}
	

    @RequestMapping(value = {"/sessionExpired","/invalidSession"}, method = RequestMethod.GET)
    public String sessionExpired(Model model,HttpServletRequest req,HttpSession ses) {
    	logger.info(new Date() +"Inside sessionExpired ");
    	try {
      	  String LogId = ((Long) ses.getAttribute("LoginId")).toString();
      	  rfpmainservice.LoginStampingUpdate(LogId, "S");
      	}
      	catch (Exception e) {
      		
				e.printStackTrace();
			}
    	
        return "SessionExp";
    }
    
    @RequestMapping(value = {"/accessdenied"}, method = RequestMethod.GET)
    public String accessdenied(Model model,HttpServletRequest req,HttpSession ses) {
    	
        return "static/accessdeniederror";
    }

    
    
	/////////Rajat Changes//Notice
    @RequestMapping(value = {"IndividualNoticeList.htm"}, method = {RequestMethod.POST,RequestMethod.GET})
    public String addNotice(HttpServletRequest req,HttpSession ses) throws Exception {
    	
		/*
		 * String EmpId = ses.getAttribute("EmpId").toString(); logger.info(new Date()
		 * +"Inside Individual Notice List "+EmpId);
		 * 
		 * req.setAttribute("NotiecList",rfpmainservice.getIndividualNoticeList(EmpId));
		 * 
		 * return "admin/IndividualNoticeList";
		 */
    	
    	String UserId = (String) ses.getAttribute("Username");

		logger.info(new Date() +"Inside IndividualNoticeList.htm "+UserId);
		
		try {
			
			FormatConverter fc=new FormatConverter();
			Calendar c= Calendar.getInstance();
			c.add(Calendar.DATE, 30);
			Date d=c.getTime();
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String fdate=req.getParameter("fdate");
			String tdate=req.getParameter("tdate");
			
			if(fdate==null)
			{
				    fdate=fc.getRegularDateFormat().format(new Date());
					tdate=fc.getRegularDateFormat().format(d);
			}
			req.setAttribute("tdate",tdate);
			req.setAttribute("fdate",fdate);
			req.setAttribute("NotiecList",rfpmainservice.getIndividualNoticeList(EmpId,fdate,tdate));

			}
			catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside IndividualNoticeList.htm "+UserId, e);
			}
			return  "admin/IndividualNoticeList";

    }
    
    FormatConverter fc=new FormatConverter();
    
    @RequestMapping(value = {"NoticeAddSubmit.htm"}, method = RequestMethod.POST)
    public String NoticeAddSubmit(HttpServletRequest req,HttpSession ses, RedirectAttributes redir) throws Exception {
    	
    	String EmpId =  ses.getAttribute("EmpId").toString();
    	String UserId = (String) ses.getAttribute("Username");
    	String LabCode =(String) ses.getAttribute("labcode");
    	
    	logger.info(new Date() +"Inside Notice Add Submit "+ UserId);
    	if(InputValidator.isContainsHTMLTags(req.getParameter("noticeFiled"))) {
			return  redirectWithError(redir,"MainDashBoard.htm","NoticeFiled should not contain HTML elements !");
		}
    	Notice notice=new Notice();
    	notice.setNotice(req.getParameter("noticeFiled"));
    	notice.setNoticeBy(EmpId);
    	notice.setIsActive(1);
    	notice.setCreatedBy(UserId);
    	notice.setCreatedDate(this.fc.getSqlDateAndTimeFormat().format(new Date()));
    	notice.setFromDate(new java.sql.Date(sdf.parse(req.getParameter("fdate")).getTime()));
    	notice.setToDate(new java.sql.Date(sdf.parse(req.getParameter("tdate")).getTime()));
    	notice.setLabCode(LabCode);
    	
    	Long result=rfpmainservice.addNotice(notice);
    	

		if (result > 0) {
			redir.addAttribute("result", "Notice Added Successfully");
		} else {
			redir.addAttribute("resultfail", "Something went wrong");
	
		}
        	
      return "redirect:/MainDashBoard.htm";
    }
  


	@RequestMapping(value = {"SeeMoreNotice.htm"}, method = RequestMethod.GET)
    public String noticeSeeMore(HttpServletRequest req,HttpSession ses ) throws Exception {
    	
    	String EmpId =  ses.getAttribute("EmpId").toString();
    	logger.info(new Date() +"Notice See More Notice List "+EmpId);
        req.setAttribute("noticeEligibility", rfpmainservice.GetNoticeEligibility(EmpId));
    	 req.setAttribute("NotiecList",rfpmainservice.getAllNotice());
    	
      return "admin/SeeMoreNotice";
    }
    
    @RequestMapping(value = {"NoticeEditRevoke.htm"}, method = RequestMethod.POST)
    public String NoticeEditRevoke(HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception {
    	
    	String EmpId =  ses.getAttribute("EmpId").toString();
    	logger.info(new Date() +"NoticeEditRevoke.htm "+EmpId);
    	
    	String Option=req.getParameter("sub");
		String noticeId=req.getParameter("noticeId");
		
		if(Option.equalsIgnoreCase("edit")) {
			req.setAttribute("NoticeEditData",rfpmainservice.noticeEditData(noticeId).get(0));
			return "admin/NoticeEdit";
		}else {
            int count= rfpmainservice.noticeDelete(noticeId);
			
			if(count>0) {
				
				redir.addAttribute("result", "Notice Deleted Successfully ");
			}
			else {
				
				redir.addAttribute("resultfail","Notice Delete Unsuccessful");
			}
			
		}
    	
      return"redirect:/IndividualNoticeList.htm";
    }
    
    @RequestMapping(value = {"NoticeEditSubmit.htm"}, method = RequestMethod.POST)
     public String noticeEditSubmit(HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception {
    	String EmpId =  ses.getAttribute("EmpId").toString();
    	logger.info(new Date() +"NoticeEditSubmit.htm "+EmpId);
    	if(InputValidator.isContainsHTMLTags(req.getParameter("noticeFiled"))) {
    		redir.addAttribute("noticeId", req.getParameter("noticeId"));
			return  redirectWithError(redir,"IndividualNoticeList.htm","NoticeFiled should not contain HTML elements !");
		}
    	Notice notice=new Notice();
    	notice.setNoticeId(Long.parseLong(req.getParameter("noticeId")));
    	notice.setNotice(req.getParameter("noticeFiled"));
    	notice.setNoticeBy(EmpId);
     	notice.setFromDate(new java.sql.Date(sdf.parse(req.getParameter("fdate")).getTime()));
    	notice.setToDate(new java.sql.Date(sdf.parse(req.getParameter("tdate")).getTime()));
    	
    	
    	int count=rfpmainservice.editNotice(notice);
    	if(count>0) {
			
			redir.addAttribute("result", "Notice Edited Successfully ");
		}
		else {
			
			redir.addAttribute("resultfail","Notice Edit Unsuccessful");
		}
    	  return"redirect:/IndividualNoticeList.htm";
    }
    
    @RequestMapping (value="ProjectHealthUpdate.htm", method=RequestMethod.GET)
    public String ProjectHealthUpdate(HttpSession ses, RedirectAttributes redir)throws Exception{
    	
    	String EmpId =  ses.getAttribute("EmpId").toString();	
    	String UserId = (String) ses.getAttribute("Username");
    	String LoginType =(String) ses.getAttribute("LoginType");
    	
    	logger.info(new Date() +"ProjectHealthUpdate.htm "+EmpId);
    	long count=rfpmainservice.ProjectHealthUpdate(EmpId,UserId);
        if(count>0) {
			redir.addAttribute("Overall","Overall");
			
			if(LoginType.equalsIgnoreCase("X")) {
				redir.addAttribute("result", "Cluster Health Updated Successfully ");
			}else {
				redir.addAttribute("result", "Project Health Updated Successfully ");
			}
			
		}
		else 
		{
			
			redir.addAttribute("resultfail","Project Health Update Unsuccessful");
		}
    	  return "redirect:/MainDashBoard.htm";
    	
    }
    
    @Scheduled(cron ="0 1 9-19/3 * * ? ")
    public String ProjectHealthUpdateAuto()throws Exception{
    	
    	logger.info(new Date() +"ProjectHealthUpdateAuto.htm ");
    	rfpmainservice.ProjectHealthUpdate("0","auto-update");
        
    	return "";
    }
    
   
    @RequestMapping (value="ProjectHoaUpdate.htm", method=RequestMethod.GET)
    public String ProjectHoaUpdate(HttpSession ses, RedirectAttributes redir) throws Exception
    {
    	long start = System.currentTimeMillis();
		
    	String Empid= ses.getAttribute("EmpId").toString();
    	String UserId = (String) ses.getAttribute("Username");
    	String LabCode=(String)ses.getAttribute("labcode");
    	String ClusterId =(String)ses.getAttribute("clusterid");
    	logger.info(new Date() +"ProjectHoaUpdate.htm "+Empid);
    	List<CCMView> CCMViewData=new ArrayList<CCMView>();
    	long count= 0L;
    	long ibasserveron=0L;
    	try {
    	// Calling pms_serv to update hoa data from ibas to pms
//    	final String localUri1=uri+"/pfms_serv/tblprojectdata?labcode="+LabCode;
//    	final String localUri2=uri+"/pfms_serv/pfms-finance-changes?projectCode=A&interval=M";
//    	final String localUri3=uri+"/pfms_serv/pfms-finance-changes?projectCode=A&interval=W";
//    	final String localUri4=uri+"/pfms_serv/pfms-finance-changes?projectCode=A&interval=T";
//    	final String localUri5=uri+"/pfms_serv/labdetails";
//    	final String CCMDataURI=uri+"/pfms_serv/getCCMViewData";
    	
//    	String MonthlyData=null;
//    	String WeeklyData=null;
//    	String TodayData=null;
//    	String HoaJsonData=null;
//    	String LabData= null;
    
    	try {

			CCMViewData= PFMSServ.getCCMViewData(LabCode);
    	}
    	catch(HttpClientErrorException  | ResourceAccessException e) 
    	{
    		logger.error(new Date() +" Inside ProjectHoaUpdate.htm pfms_serv Not Responding.htm "+UserId, e);
    		e.printStackTrace();
    		ibasserveron = 1;
		}
    	catch(Exception e)
		{
    		logger.error(new Date() +" Inside ProjectHoaUpdate.htm "+UserId, e);
			e.printStackTrace();
		}

		List<ProjectHoa> projectDetails1=new ArrayList<>();
		List<FinanceChanges> FinanceDetailsMonthly=new ArrayList<>();
		List<FinanceChanges> FinanceDetailsWeekly=new ArrayList<>();
		List<FinanceChanges> FinanceDetailsToday=new ArrayList<>();
		List<IbasLabMaster> LabDetails=new ArrayList<>();
		

			try {

				projectDetails1 = PFMSServ.ProjectHoaData(LabCode);
				LabDetails = PFMSServ.LabDetails();
				count = rfpmainservice.ProjectHoaUpdate(projectDetails1,UserId,LabDetails);
				
			} catch (JsonProcessingException e) {
				
				e.printStackTrace();
			}

		
	
			try {
// Commented  because we are not showing any where
//				FinanceDetailsMonthly = PFMSServ.PfmsFinanceChanges("A", "M");
//				FinanceDetailsWeekly  =  PFMSServ.PfmsFinanceChanges("A", "W");
//				FinanceDetailsToday   =   PFMSServ.PfmsFinanceChanges("A", "T");
//				
//
//				rfpmainservice.ProjectFinanceChangesUpdate(FinanceDetailsMonthly,FinanceDetailsWeekly,FinanceDetailsToday,UserId,Empid);
//				
			} catch (Exception e) {

				logger.error(new Date() +" Inside ProjectHoaUpdate.htm "+UserId, e);
				e.printStackTrace();
			}
		
		
		if(CCMViewData!=null && CCMViewData.size()>0)
		{
			rfpmainservice.CCMViewDataUpdate(CCMViewData, LabCode, ClusterId, UserId, Empid);
		}
		
    	
    	if(count>0 ) {
			redir.addAttribute("Overall","Overall");
			redir.addAttribute("result", "Project HOA and CCM Details Updated Successfully ");
		}
    	else if(ibasserveron==1){
    		redir.addAttribute("resultfail","IBAS Server Not Responding");
    	}
		else {
			
			redir.addAttribute("resultfail","Project HOA and CCM Details Update Unsuccessful");
		  }
    	
    	}catch(Exception e) {
    		if(ibasserveron==1){
        		redir.addAttribute("resultfail","IBAS Server Not Responding");
        	}else {
    		redir.addAttribute("resultfail","Project HOA and CCM Details Update Unsuccessful");
        	}
    	}
    	System.out.println("Feign Call Success after " + (System.currentTimeMillis() - start) + "ms");
    	  return "redirect:/MainDashBoard.htm";
    }
    

    
   // @Scheduled(cron ="0 1 9-19/3 * * ? ")
    public String ProjectHoaUpdateAuto()throws Exception{
    	    	
    	logger.info(new Date() +"ProjectHoaUpdateAuto.htm ");
    	// Calling pms_serv to update hoa data from ibas to pms
    	final String localUri1=uri+"/pfms_serv/tblprojectdata";
    	final String localUri2=uri+"/pfms_serv/pfms-finance-changes?projectCode=A&interval=M";
    	final String localUri3=uri+"/pfms_serv/pfms-finance-changes?projectCode=A&interval=W";
    	final String localUri4=uri+"/pfms_serv/pfms-finance-changes?projectCode=A&interval=T";
    	final String localUri5=uri+"/pfms_serv/labdetails";
    	
    	String HoaJsonData=null;
    	String MonthlyData=null;
    	String WeeklyData=null;
    	String TodayData=null;
    	String LabData=null;
    
    	try {
    		
    		HttpHeaders headers = new HttpHeaders();
	 		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	 		//headers.set("labcode", LabCode);
    		HttpEntity<String> entity = new HttpEntity<String>(headers);
			ResponseEntity<String> response1=restTemplate.exchange(localUri1, HttpMethod.POST, entity, String.class);
			HoaJsonData=response1.getBody();
			
			ResponseEntity<String> monthlyresponse=restTemplate.exchange(localUri2, HttpMethod.POST, entity, String.class);
			ResponseEntity<String> weeklyresponse=restTemplate.exchange(localUri3, HttpMethod.POST, entity, String.class);
			ResponseEntity<String> todayresponse=restTemplate.exchange(localUri4, HttpMethod.POST, entity, String.class);
			ResponseEntity<String> labdata=restTemplate.exchange(localUri5, HttpMethod.POST, entity, String.class);
			MonthlyData=monthlyresponse.getBody();
			WeeklyData=weeklyresponse.getBody();
			TodayData=todayresponse.getBody();
			LabData=labdata.getBody();

    	}catch(Exception e)
		{
    		logger.error(new Date() +" Inside ProjectHoaUpdateAuto "+ e);
			e.printStackTrace();
			
		}

		ObjectMapper mao = new ObjectMapper().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		List<ProjectHoa> projectDetails1=null;
		List<FinanceChanges> FinanceDetailsMonthly=null;
		List<FinanceChanges> FinanceDetailsWeekly=null;
		List<FinanceChanges> FinanceDetailsToday=null;
		List<IbasLabMaster> LabDetails=null;
		if(HoaJsonData!=null) {
			try {

				projectDetails1 = mao.readValue(HoaJsonData, mao.getTypeFactory().constructCollectionType(List.class, ProjectHoa.class));
				LabDetails = mao.readValue(LabData, mao.getTypeFactory().constructCollectionType(List.class, IbasLabMaster.class));
				long count = rfpmainservice.ProjectHoaUpdate(projectDetails1,"auto-update",LabDetails);
				
			} catch (JsonProcessingException e) {

				logger.error(new Date() +" Inside ProjectHoaUpdateAuto "+ e);
				e.printStackTrace();
			}
		}
		
		if(MonthlyData!=null) {
			try {

				FinanceDetailsMonthly = mao.readValue(MonthlyData, mao.getTypeFactory().constructCollectionType(List.class, FinanceChanges.class));
				FinanceDetailsWeekly = mao.readValue(WeeklyData, mao.getTypeFactory().constructCollectionType(List.class, FinanceChanges.class));
				FinanceDetailsToday = mao.readValue(TodayData, mao.getTypeFactory().constructCollectionType(List.class, FinanceChanges.class));
				//we have to pass empid instead of 0
				rfpmainservice.ProjectFinanceChangesUpdate(FinanceDetailsMonthly,FinanceDetailsWeekly,FinanceDetailsToday,"auto-update","0");
				
			} catch (JsonProcessingException e) {

				logger.error(new Date() +" Inside ProjectHoaUpdateAuto "+ e);
				e.printStackTrace();
			}
		}
        
    	return "";
    }
    
    @RequestMapping(value="ChangesinProject.htm", method= {RequestMethod.GET,RequestMethod.POST})
    public String ChangesinProject (HttpSession ses,HttpServletRequest req) throws Exception
    {
    	logger.info(new Date() +"ChangesinProject.htm ");
    	try {
    	String Empid= ses.getAttribute("EmpId").toString();
    	String LoginType=(String)ses.getAttribute("LoginType");
    	String LabCode=(String)ses.getAttribute("labcode");
    	String ProjectId = "A";
    	String Interval = "T";
    	String ActiveTab = "all-tab_alltab";
    	String ClusterId = (String)ses.getAttribute("clusterid");
    	
    	 String DGName = headerservice.LabMasterList(ClusterId).stream().filter(e-> "Y".equalsIgnoreCase(e[2].toString())).collect(Collectors.toList()).get(0)[1].toString();
	     String IsDG = "No";
	     if(DGName.equalsIgnoreCase(LabCode)) {
	    	 IsDG = "Yes";
	     	 LabCode="A";
	     }
	     else {
	    	 IsDG = "No";
	     }
	     
    	
    	if(req.getParameter("interval")!=null) {
    		Interval=req.getParameter("interval");
    	}
    	if(req.getParameter("projectid")!=null) {
    		ProjectId=req.getParameter("projectid");
    	}
    	if(req.getParameter("activetab")!=null) {
    		ActiveTab=req.getParameter("activetab");
    	}
    	if(req.getParameter("labcode")!=null) {
    		LabCode = req.getParameter("labcode");
    	}
    	
    	List<Object[]> LabMasterList = headerservice.LabMasterList(ClusterId).stream().filter(e-> "N".equalsIgnoreCase(e[2].toString())).collect(Collectors.toList()) ;
    	List<Object[]> MeetingChangesData =rfpmainservice.MeetingChanges(ProjectId,Interval,LabCode);
    	List<Object[]> MilestoneChangesData =rfpmainservice.MilestoneChanges(ProjectId, Interval,LabCode);
    	List<Object[]> ActionChangesData =rfpmainservice.ActionChanges(ProjectId,Interval,LabCode);
    	List<Object[]> RiskChangesData =rfpmainservice.RiskChanges(ProjectId,Interval,LabCode) ;
    	String projectCode="A";
    	if(ProjectId!=null && !ProjectId.equalsIgnoreCase("A")) {
    		projectCode = rfpmainservice.ProjectData(ProjectId)[1].toString().trim();
    	}
	    	 
	    req.setAttribute("IsDG", IsDG);
    	req.setAttribute("ProjectList", rfpmainservice.ProjectList(LoginType,Empid,LabCode));
    	req.setAttribute("changestotalcount", rfpmainservice.ChangesTotalCountData(ProjectId));
    	req.setAttribute("meetingchangesdata", MeetingChangesData);
    	req.setAttribute("milestonechangesdata", MilestoneChangesData);
    	req.setAttribute("actionchangesdata", ActionChangesData );
    	req.setAttribute("riskchangesdata", RiskChangesData );
    	//Finance data from pfts_file is done and commented
    	//req.setAttribute("financedataparta", rfpmainservice.FinanceDataPartA(ProjectId,Interval));
    	req.setAttribute("interval", Interval);
    	req.setAttribute("projectid", ProjectId);
    	req.setAttribute("activetab", ActiveTab);
    	req.setAttribute("labmasterlist", LabMasterList);
    	req.setAttribute("labcode", LabCode);
 
    	final String localUri=uri+"/pfms_serv/pfms-finance-changes?projectCode="+projectCode+"&interval="+Interval;
    	List<FinanceChanges> FinanceDetails=null;
    	String FinanceChanges=null;
    	long count= 0L;
    	long ibasserveron=0L;
    	try {
    		
    		HttpHeaders headers = new HttpHeaders();
	 		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	 		headers.set("labcode", LabCode);
    		HttpEntity<String> entity = new HttpEntity<String>(headers);
			ResponseEntity<String> response1=restTemplate.exchange(localUri, HttpMethod.POST, entity, String.class);
			FinanceChanges=response1.getBody();
    	}
    	catch(HttpClientErrorException  | ResourceAccessException e) {
    		logger.error(new Date() +" Inside ChangesinProject.htm pfms_serv not Responding "+ e);
    		ibasserveron = 1;
		}
    	catch(Exception e)
		{
    		logger.error(new Date() +" Inside ChangesinProject.htm  "+ e);
			e.printStackTrace();
		}

		ObjectMapper mao = new ObjectMapper().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		
		if(FinanceChanges!=null) {
			try {
				FinanceDetails = mao.readValue(FinanceChanges, mao.getTypeFactory().constructCollectionType(List.class, FinanceChanges.class));
			} catch (JsonProcessingException e) {

				logger.error(new Date() +" Inside ChangesinProject.htm pfms_serv not Responding "+ e);
				e.printStackTrace();
			}
		}
		
		req.setAttribute("financechangesdata", FinanceDetails );
		
		
		List<Object[]> alldatalist = new ArrayList<Object[]>();

    	MeetingChangesData.stream().map(c->  new Object[] { "Meeting" ,c[12], c[8] , c[9], sdf.format(c[10]),c[13] } ).collect(Collectors.toList()).forEach( alldatalist :: add );
    	MilestoneChangesData.stream().map(c-> new Object[] {"Milestone" ,c[0] ,c[7] , c[8], sdf.format(c[9]),c[10] } ).collect(Collectors.toList()).forEach( alldatalist :: add );
    	ActionChangesData.stream().map(c-> new Object[] {"Action" ,c[2] , c[7] , c[9], sdf.format(c[8]),c[10] } ).collect(Collectors.toList()).forEach( alldatalist :: add );
    	RiskChangesData.stream().map(c-> new Object[] {"Risk" ,c[1] ,c[9] , c[10], sdf.format(c[8]),c[11] } ).collect(Collectors.toList()).forEach( alldatalist :: add );
    	
    	if(!IsDG.equalsIgnoreCase("Yes")) {
    	
    	if(FinanceDetails!=null) {
    	FinanceDetails.stream().map(c-> {
			try {
				return new Object[] {"Finance", c.getType(),c.getFirstName(),c.getDesignation(), sdf.format(sdf1.parse(c.getCreatedDate().toString())), "-" };
			} catch (ParseException e) {
				logger.error(new Date() +" Inside ChangesinProject.htm"+ e);
				e.printStackTrace();
			}
			return new Object[] {"Finance", c.getType(),c.getFirstName(),c.getDesignation(), "-" ,"-" };
		} ).forEach(alldatalist :: add);
    	}
    	
    	}
    	
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
    	alldatalist.sort((o1, o2) -> {
			//return (sdf.format(sdf.parse(o2[4].toString()))).compareTo(sdf.format(sdf.parse(o1[4].toString())));
			return (LocalDate.parse(o2[4].toString(), formatter)).compareTo(LocalDate.parse(o1[4].toString(), formatter));
		});
    
    	//alldatalist.stream().sorted(Comparator.comparing(c-> c. )).collect(Collectors.toList());
    	
    	req.setAttribute("alldatacombinedlist", alldatalist );

    	}
    	catch(Exception e) {
    		logger.error(new Date() +" Inside ChangesinProject.htm"+ e);
    		e.printStackTrace();
    	}

    	return "admin/ChangesRecord";
    }
    
    @RequestMapping(value="LabWiseProjectDetails.htm", method=RequestMethod.POST)
    public String LabWiseProjectDetails(HttpServletRequest req, HttpSession ses) throws Exception
    {
    	logger.info(new Date() +  "LabWiseProjectDetails.htm");
    	String LabCode = req.getParameter("labcode");
    	String EmpId =  ses.getAttribute("EmpId").toString();
    	String LoginType=(String)ses.getAttribute("LoginType");
    	String UserId = (String) ses.getAttribute("Username");
    	String ProjectId="A";
		if(req.getParameter("projectid")!=null) {
			ProjectId=req.getParameter("projectid");
		}    	
    	try {
    		String ClusterId = headerservice.LabDetails(LabCode)[1].toString();
    		req.setAttribute("projecthealthtotal",rfpmainservice.ProjectHealthTotalData("A",EmpId,"A",LabCode,"N"));
    		req.setAttribute("projecthealthdata",  rfpmainservice.ProjectHealthData(LabCode));
    		req.setAttribute("ProjectList", rfpmainservice.ProjectList("A",EmpId,LabCode));
    		req.setAttribute("logintype", LoginType);
    		req.setAttribute("DashboardFinanceCashOutGo",rfpmainservice.DashboardFinanceCashOutGo("A",EmpId,LabCode,ClusterId ));
    		
    		
    		req.setAttribute("DashboardFinance",rfpmainservice.DashboardFinance("A",EmpId,LabCode,ClusterId ));
    		List<Object[]> LabMasterList = headerservice.LabMasterList(ClusterId).stream().filter(e-> "N".equalsIgnoreCase(e[2].toString())).collect(Collectors.toList()) ;
    		List<Object[]> labdatalist = new ArrayList<Object[]>();
    		
		    for(Object[] obj : LabMasterList) 
		    {
		    	 labdatalist.add(rfpmainservice.ProjectHealthTotalData(ProjectId,EmpId,LoginType, obj[1].toString().trim() ,"N"));
		    }
		    req.setAttribute("projecthealthtotaldg", labdatalist);    		
    	}
    	catch(Exception e) {
    		logger.error(new Date() +" Inside LabWiseProjectDetails.htm"+UserId+ e);
    		e.printStackTrace();
    	}    	
    	return "static/LabProjectDetails";
    }
   
    
    @RequestMapping(value = "CashoutgoProject.htm", method = RequestMethod.GET)
   	public @ResponseBody String cashoutgoProject(HttpServletRequest req, HttpSession ses) throws Exception 
    {
    	List<Object[]> cashoutgo=null;
    	String LabCode=(String)ses.getAttribute("labcode");
    	String EmpId =  ses.getAttribute("EmpId").toString();
    	String LoginType=(String)ses.getAttribute("LoginType");
    	String UserId = (String) ses.getAttribute("Username");
    	String ClusterId =(String)ses.getAttribute("clusterid");
   		logger.info(new Date() +"Inside CashoutgoProject.htm "+UserId);		
   		try {
   			String ProjectCode = req.getParameter("ProjectCode");
   			
   			if(ProjectCode.equalsIgnoreCase("0")) {
   				cashoutgo = rfpmainservice.DashboardFinanceCashOutGo(LoginType,EmpId,LabCode,ClusterId );
   			}else
   			{
   				cashoutgo = rfpmainservice.DashboardProjectFinanceCashOutGo(ProjectCode);	
   			}
   		}
   		catch (Exception e) {
   			e.printStackTrace();
   			 logger.error(new Date() +" Inside CashoutgoProject.htm "+UserId, e);
   		}
   		Gson convertedgson = new Gson();
   		return convertedgson.toJson(cashoutgo);
   	}
    
    @RequestMapping(value = "ProjectAttribures.htm", method = RequestMethod.GET)
   	public @ResponseBody String ProjectAttribures(HttpServletRequest req, HttpSession ses) throws Exception 
    {
    	String UserId = (String) ses.getAttribute("Username");
   		logger.info(new Date() +"Inside CashoutgoProject.htm "+UserId);
   		Object[] Project = null;
   		
   		try {
   			String ProjectCode = req.getParameter("ProjectCode");
   			Project = rfpmainservice.ProjectAttributes(ProjectCode);
   		}
   		catch (Exception e) {
   			e.printStackTrace();
   			 logger.error(new Date() +" Inside ProjectAttribures.htm "+UserId, e);
   		}
   		Gson convertedgson = new Gson();
   		return convertedgson.toJson(Project);
   	}
    
    @GetMapping(value="/sessionExpiredHandler")
    public String handleSessionExpired(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);

        // Retrieve stored login page, defaulting to "/login"
        String originalLoginPage = (session != null && session.getAttribute("loginPage") != null)
                ? (String) session.getAttribute("loginPage")
                : "login"; 

        return "redirect:/" + originalLoginPage + "?sessionExpired";
    }
    
    private String redirectWithError(RedirectAttributes redir, String redirecturl, String message) {
		redir.addAttribute("resultfail", message);
		return "redirect:/"+redirecturl;
	}
}
