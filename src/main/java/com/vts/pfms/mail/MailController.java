package com.vts.pfms.mail;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.vts.pfms.mail.ReversibleEncryptionAlg;
import com.vts.pfms.utils.InputValidator;
import com.vts.pfms.admin.service.AdminService;
import com.vts.pfms.committee.service.CommitteeService;

@Controller
public class MailController {

	@Autowired
	CommitteeService committeeService;
	@Autowired
	MailService mailService;
	
	@Autowired 
	AdminService adminService;
	
	@Autowired
	ReversibleEncryptionAlg rea;
	
	private static final Logger logger=LogManager.getLogger(MailController.class);
	
//	@Autowired
//	CustomJavaMailSender customJavaMailSender;
	
	//private static SimpleDateFormat sqlDateFormat=new SimpleDateFormat("yyyy-MM-dd");
	//created on 27-11
	@RequestMapping(value = "ResetPasswordList.htm" ,method = {RequestMethod.GET,RequestMethod.POST})
	public String ResetPasswordList(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception {
		logger.info(new Date() + "ResetPasswordList.htm" + req.getUserPrincipal().getName());

		try {
			//List<Object[]>resetPasswordList=adminService.resetPasswordList();
			
			//req.setAttribute("resetPasswordList", resetPasswordList);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return "admin/ResetPasswordList";
	}
	
	//method for resetpassword confirmation
//	@RequestMapping(value="ResetPasswordConfirm.htm",method= {RequestMethod.POST})
//	public String ResetPasswordConfirm(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception{
//		logger.info(new Date() + "ResetPasswordConfirm.htm" + req.getUserPrincipal().getName());
//		String UserId=(String)ses.getAttribute("Username");
//		try {
//			String[] value=req.getParameter("empid").split("/");
//			String empid=value[0];
//			String username=value[1];
//			String email=value[2];
//			String loginid=value[3];
//			String resetid=value[4];
//		    Random random = new Random();
//		    int threeDigitRandom = random.nextInt(900) + 100;
//			String pw=email.substring(0,3).toUpperCase()+threeDigitRandom;
//		    System.out.println("threeDigitRandom"+pw);
//			
//			int count =(int)adminService.resetPassword(loginid,UserId,pw);
//			
//			if(count<1) {
//				System.out.println("Password Change unsuccesful");
//			}else {
//				redir.addAttribute("result", "Password is reset and a Email has been sent .");
//			}
//			
//			String message="Your password is reset to "+threeDigitRandom;
//			String subject="Reset Password";
//			// Send the email asynchronously within the loop
//           customJavaMailSender.sendScheduledEmailAsync(email, subject, message, true);
//           
//           int result=adminService.updateResetPWtable(resetid);
//		}
//		catch (Exception e) {
//			// TODO: handle exception
//		}
//		return "redirect:/ResetPasswordList.htm";	
//	}
	
	
	@RequestMapping(value="MailConfigurationList.htm")
	public String MailConfigurationList(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception{
		logger.info(new Date() +" Inside MailConfiguration.htm " );
		   try {
			   req.setAttribute("mailConfigurationList", adminService.MailConfigurationList());
		    }catch (Exception e) {
		    	
		    	 logger.error(new Date() +" Login Problem Occures When MailConfiguration.htm was clicked ", e);
		     }

		return "admin/mailConfiguration";
}
	
	
	@RequestMapping(value = "MailConfigurationAdd.htm")
	public String MailConfigurationAdd(HttpServletRequest req, HttpSession ses) throws Exception {
		logger.info(new Date() +" Inside MailConfigurationAdd.htm " );
		   try {
			   req.setAttribute("action", "Add");
	System.out.println("Mail Configurationnnnnnnn");
		    }catch (Exception e) {
		    	 logger.error(new Date() +" Login Problem Occures When MailConfiguration.htm was clicked ", e);
		     }
		return "admin/mailConfigurationAddEdit";

	}
	@RequestMapping(value = "MailConfigurationDelete.htm")
	public String MailConfigurationDelete(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
		logger.info(new Date() +" Inside MailConfigurationDelete.htm " );
		long result = 0;
		   try {
			   String MailConfigurationId = (String)req.getParameter("Lid");
			   System.out.println("MailConfigIdddd"+MailConfigurationId);
			   if(MailConfigurationId!=null) {
				   result = adminService.DeleteMailConfiguration(Long.parseLong(MailConfigurationId),req.getUserPrincipal().getName());
			   }else {
				   result = 0;
			   }
		    }catch (Exception e) {
		    	 logger.error(new Date() +" Login Problem Occures When MailConfigurationDelete.htm was clicked ", e);
		     }
		   
		   if(result >0) {
				redir.addAttribute("result", "Mail Configuration Delete Successful");
			}
			else {
				redir.addAttribute("resultfail", "Mail Configuration Delete Unsuccessful");
			}

		return "redirect:/MailConfigurationList.htm";

	}
	
	@RequestMapping(value = "MailConfigurationAddSubmit.htm", method = RequestMethod.POST)
	public String MailConfigurationAddSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
		logger.info(new Date() +" Inside MailConfigurationAddSubmit " );
		long result = 0;
		try {
			
			String Username = (String)req.getParameter("UserNameData");
			String Password = (String)req.getParameter("PasswordData");
			String HostType = (String)req.getParameter("HostTypeData");
			String port = (String)req.getParameter("Port");
			String Host = (String)req.getParameter("Host");
			//String MailType = (String)req.getParameter("MailTypeData");
			
			if(InputValidator.isContainsHTMLTags(Username) || InputValidator.isContainsHTMLTags(Password) || InputValidator.isContainsHTMLTags(Host) || InputValidator.isContainsHTMLTags(port)) {
				return  redirectWithError(redir,"MailConfigurationAdd.htm","Html Tags Should not be Entered");
			}
			result = adminService.AddMailConfiguration(Username,Password,HostType,req.getUserPrincipal().getName(),Host,port);
			
		}catch (Exception e) {
		 	 logger.error(new Date() +" Login Problem Occures When MailConfigurationAddSubmit.htm was clicked ", e);
		 	 result = 0;
	     }
		if(result >0) {	
			redir.addAttribute("result", "Mail Configuration Added Successfully");
		}
		else if(result ==-1){
			redir.addAttribute("resultfail", "TypeOfHost Already Exists ...!");
		}else {
			redir.addAttribute("resultfail", "Mail Configuration Add Unsuccessful");
		}

		return "redirect:/MailConfigurationList.htm";
	}
	
	
	@RequestMapping(value = "MailConfigurationEdit.htm")
	public String MailConfigurationEdit(HttpServletRequest req, HttpSession ses) throws Exception {
		logger.info(new Date() +" Inside MailConfigurationAdd.htm " );
		   try {
			   String MailConfigurationId = (String)req.getParameter("Lid");
			   req.setAttribute("action", "Edit");
			   req.setAttribute("mailConfigIdFrEdit", MailConfigurationId);
			   Object[] MailConfigurationEditObject = adminService.MailConfigurationEditList(Long.parseLong(MailConfigurationId));
			   if(MailConfigurationEditObject!=null && MailConfigurationEditObject[5]!=null) {
				   String pass=rea.decryptByAesAlg(MailConfigurationEditObject[5].toString());
				   req.setAttribute("pass", pass);
			   }
			   if(MailConfigurationId!=null && MailConfigurationEditObject!=null) {
				   req.setAttribute("mailConfigEditList", MailConfigurationEditObject);
			   }else {
				  return "static/Error";
			   }
	System.out.println("Mail Configurationnnnnnnn");
		    }catch (Exception e) {
		    	 logger.error(new Date() +" Login Problem Occures When MailConfiguration.htm was clicked ", e);
		     }
			return "admin/mailConfigurationAddEdit";

	}
	
	private String redirectWithError(RedirectAttributes redir, String redirecturl,String message) {
		redir.addAttribute("resultfail", message);
		return "redirect:/"+redirecturl;
	}
	
	@RequestMapping(value = "MailConfigurationEditSubmit.htm", method = RequestMethod.POST)
	public String MailConfigurationEditSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
		logger.info(new Date() +" Inside MailConfigurationEditSubmit " );
		long result = 0;
		try {
			String MailConfigurationId = (String)req.getParameter("MailConfigIdFrEditSubmit");
			String Username = (String)req.getParameter("UserNameData");
			String Password = (String)req.getParameter("PasswordData");
			String HostType = (String)req.getParameter("HostTypeData");
			String Host = (String)req.getParameter("Host");
			String Port = (String)req.getParameter("Port");
			if(InputValidator.isContainsHTMLTags(Username) || InputValidator.isContainsHTMLTags(Password) || InputValidator.isContainsHTMLTags(Host) || InputValidator.isContainsHTMLTags(Port)) {
				redir.addAttribute("Lid", MailConfigurationId);
				return  redirectWithError(redir,"MailConfigurationEdit.htm","Html Tags Should not be Entered");
			}
			if(MailConfigurationId!=null) {
			result = adminService.UpdateMailConfiguration(Long.parseLong(MailConfigurationId),Username,HostType,req.getUserPrincipal().getName(),Host,Port,Password);
			}else {
				result = 0;
			}
		}catch (Exception e) {
		 	 logger.error(new Date() +" Login Problem Occures When MailConfigurationEditSubmit.htm was clicked ", e);
		 	 result = 0;
	     }
		if(result >0) {
			redir.addAttribute("result", "Mail Configuration Updated Successfully");
		}
		else {
			redir.addAttribute("resultfail", "Mail Configuration Update Unsuccessful");
		}

		return "redirect:/MailConfigurationList.htm";
	}
}