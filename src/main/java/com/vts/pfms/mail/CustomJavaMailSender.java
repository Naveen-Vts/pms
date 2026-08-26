package com.vts.pfms.mail;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.vts.pfms.committee.service.CommitteeService;

@Controller
@EnableScheduling
public class CustomJavaMailSender {
	
	@Autowired
	@Lazy
	CommitteeService committeeService;
	
	@Autowired
	MailService mailService;
	
//	@Value("${Email}")
//	private String Email;
//
//	@Value("${password}")
//	private String mailPassword;

	@Value("${port}")
	private String port;

	@Value("${host}")
	private String host;
	
	
	private static final Logger logger=LogManager.getLogger(CustomJavaMailSender.class);
//    public HttpSession MyScheduledTasks(HttpSession httpSession) {
//        this.httpSession = httpSession;
//    }
	private final SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
	
	//@Scheduled(cron = "0 0 17 * * *")
	public void reportCurrentTime() throws Exception {
		asyncMethodforSendingMail("tommorrow");
		}
	
	//@Scheduled(cron = "0 0 7 * * *")
	public int reporttodayCurrentTime(String scheduleDate) throws Exception {
		return asyncMethodforSendingMail(scheduleDate);
		}
	@Async
	public int asyncMethodforSendingMail(String day) {
		logger.info(LocalDate.now().toString()+" insdide asyncMethodforSendingMail ");
		List<MeetingMailDto>meetingMailDtoData=new ArrayList<>();
		List<String>membertypes=Arrays.asList("CC","CS","PS","CI","P","I");
		String date=day;
		int mailCount=0;
//		if(day.equalsIgnoreCase("tommorrow")) {
//		date=LocalDate.now().plusDays(1).toString();
//		}else {
//			date=LocalDate.now().toString();
//		}
		System.out.println(date+"tommorrows Date");
		System.out.println(LocalDate.now().toString()+"Local tommorrows Date");
		try {
			List<Object[]>todayMeetings=mailService.getTodaysMeetings(date);
			List<Object[]>empAtendance= new ArrayList<>();
			if(todayMeetings.size()>0) {
			for(Object[]obj:todayMeetings) {
				empAtendance=committeeService.CommitteeAtendance(obj[0].toString(),"0").stream()
						.filter(e ->membertypes.contains(e[3].toString())).collect(Collectors.toList());
				for(Object[]obj1:empAtendance) {//Merge the data in MeetingMailDto
					MeetingMailDto m= new MeetingMailDto();
					m.setEmpid(obj1[0].toString());
					m.setEmpname(obj1[6].toString());
					m.setEmail(obj1[8].toString());
					m.setScheduleid(obj[0].toString());
					m.setProjectid(obj[1].toString());
					m.setInitiationId(obj[2].toString());
					m.setCommitteeShortName(obj[3].toString());
					m.setCommitteeName(obj[4].toString());
					m.setMeetingTime(obj[6].toString());
					m.setMeetingVenue(obj[5].toString());
					m.setProjectCode(obj[7].toString());
					m.setProjectname(obj[8].toString());
					m.setDronaEmail(obj1[13].toString());
					meetingMailDtoData.add(m);
				}
			}
			List<MeetingMailDto>meetingMailDtoSubData= new ArrayList<>();
			meetingMailDtoData.stream().forEach(System.out::println);
			
			
			while(meetingMailDtoData.size()!=0) {
			String empid=meetingMailDtoData.get(0).getEmpid();
			meetingMailDtoSubData=meetingMailDtoData.stream().filter( e -> e.getEmpid().equalsIgnoreCase(empid))
							  .collect(Collectors.toList());
			String message="Sir/Madam<br><p>&emsp;&emsp;This is to inform you that you have "+meetingMailDtoSubData.size()+" meetings scheduled "+day+".</p><table style=\"align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; max-width: 650px; font-size: 16px; border-collapse:collapse;\" >";
			for(MeetingMailDto m:meetingMailDtoSubData) {
				  message=message
						 +"<tr><th colspan=\"2\" style=\"text-align: left; font-weight: 700; width: 650px;border: 1px solid black; padding: 5px; padding-left: 15px\">Meeting Details </th></tr>"
						 +"<tr>"
						 + "<td style=\"border: 1px solid black; padding: 5px;text-align: left\"> Project : </td>"
						 + "<td style=\"border: 1px solid black; padding: 5px;text-align: left\"><b style=\"color:#0D47A1\">" + m.getProjectCode()+ "( "+m.getProjectname()+" )"  + "</b></td></tr>"
						 +"<tr>"
						 + "<td style=\"border: 1px solid black; padding: 5px;text-align: left\"> Meeting : </td>"
						 + "<td style=\"border: 1px solid black; padding: 5px;text-align: left\">" + m.getCommitteeShortName()  + "</td></tr>"
						 +"<tr><td style=\"border: 1px solid black; padding: 5px;text-align: left\"> Date :  </td>"
						 +"<td style=\"border: 1px solid black; padding: 5px;text-align: left\">" + LocalDate.now()+","+LocalDate.now().getDayOfWeek()+"</td></tr>"
						 +"<tr>"
						 + "<td style=\"border: 1px solid black; padding: 5px;text-align: left\"> Time : </td>"
						 + "<td style=\"border: 1px solid black; padding: 5px;text-align: left\">" + m.getMeetingTime()  + "</td></tr>"
						 +"<tr>"
						 + "<td style=\"border: 1px solid black; padding: 5px;text-align: left\"> Venue : </td>"
						 + "<td style=\"border: 1px solid black; padding: 5px;text-align: left\">" + m.getMeetingVenue()+ "</td></tr>";
					}
				  message=message
						 +"</table><p style=\"font-weight:bold;font-size:13px;\">[Note:This is an autogenerated email. Replies to this will not be attended to. Thank you.]</p>"
					     +"<p>Regards</p>"
					     +"<p>PMS Team"+"</p>";
				  String email=meetingMailDtoSubData.get(0).getEmail();
				  String DronaEmail=meetingMailDtoData.get(0).getDronaEmail();
				  String subject="Meeting Schedule On "+date;
				  int sendMail = sendMessage(email,subject,message);
				  
					meetingMailDtoData=meetingMailDtoData.stream().filter( e -> !e.getEmpid().equalsIgnoreCase(empid)).collect(Collectors.toList());
					mailCount=mailCount+sendMail;
					}
					System.out.println("No of mails Send"+mailCount);
					}else {
					return 111;
					}
					} catch (Exception e) {
					e.printStackTrace();
					}	
		return mailCount;
				}
	
	 	@Async
	    public CompletableFuture<Integer> sendScheduledEmailAsync1(String email, String subject, String msg, boolean isHtml) {
		 
	 		System.out.println("email "+email);
	        String typeOfHost = "L";
			MailConfigurationDto mailAuthentication;
			
			try {
				mailAuthentication = mailService.getMailConfigByTypeOfHost(typeOfHost);
			} catch (Exception e1) {
				e1.printStackTrace();
				return null;
			}

			
			  if (mailAuthentication == null) {
			      // Handle the case where mail configuration for the specified typeOfHost is not found
				  System.out.println("ERRROR -3 ");
			      return CompletableFuture.completedFuture(-3); // You can choose an appropriate error code
			  }else {

	      
			  JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

			    // Set mail configuration from the database
			    mailSender.setHost(mailAuthentication.getHost().toString());
			    mailSender.setPort(Integer.parseInt(mailAuthentication.getPort().toString()));
			    mailSender.setUsername(mailAuthentication.getUsername().toString());
			    mailSender.setPassword(mailAuthentication.getPassword().toString());

			    Properties properties = System.getProperties();
				// Setup mail server
				properties.setProperty("mail.smtp.host", mailSender.getHost());
				//properties.put("mail.smtp.starttls.enable", "true");
				// SSL Port
				properties.put("mail.smtp.port", mailSender.getPort());
				// enable authentication
				properties.put("mail.smtp.auth", "true");
				// SSL Factory
				properties.put("mail.smtp.socketFactory.class", "jakarta.net.ssl.SSLSocketFactory");
			    
				//properties.put("mail.smtp.starttls.enable", "true");

			    Session session = Session.getDefaultInstance(properties, new jakarta.mail.Authenticator() {
					// override the getPasswordAuthentication
					// method
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(mailSender.getUsername(), mailSender.getPassword());
					}
				});
			    int mailSendresult = 0;
			    try {
			    	MimeMessage message = new MimeMessage(session);
			    	message.setFrom(new InternetAddress(mailSender.getUsername()));
		            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
		            //message.addRecipient(Message.RecipientType.TO, new InternetAddress(DronaEmail));
					message.setSubject(subject);
					message.setText(msg);
					message.setContent(msg, "text/html");// this code is used to make the message in HTML formatting
					// Send message
					Transport.send(message);
					System.out.println("Message Sent");
					mailSendresult++;
			    } catch (AuthenticationFailedException e) {
			        // Handle authentication failure (wrong password) and complete the CompletableFuture with -2
			        e.printStackTrace();
			        System.out.println("ERORRRR -2");
			        return CompletableFuture.completedFuture(-2);
			    } catch (MessagingException e) {
			        // Handle other email sending exceptions and complete the CompletableFuture with -1
			        e.printStackTrace();
			        System.out.println("ERORRRR -1");
			        return CompletableFuture.completedFuture(-1);
			    }
			    return CompletableFuture.completedFuture(mailSendresult);
			  }
	    }
	 	
	 	@Async
	    public CompletableFuture<Integer> sendScheduledEmailAsync2(String DronaEmail, String subject, String msg, boolean isHtml) {
		 
	        String typeOfHost = "D";
			MailConfigurationDto mailAuthentication;
			
			try {
				mailAuthentication = mailService.getMailConfigByTypeOfHost(typeOfHost);
			} catch (Exception e1) {
				e1.printStackTrace();
				return null;
			}

			
			  if (mailAuthentication == null) {
			      // Handle the case where mail configuration for the specified typeOfHost is not found
				  System.out.println("ERRROR -3 ");
			      return CompletableFuture.completedFuture(-3); // You can choose an appropriate error code
			  }else {

	      
			  JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

			    // Set mail configuration from the database
			    mailSender.setHost(mailAuthentication.getHost().toString());
			    mailSender.setPort(Integer.parseInt(mailAuthentication.getPort().toString()));
			    mailSender.setUsername(mailAuthentication.getUsername().toString());
			    mailSender.setPassword(mailAuthentication.getPassword().toString());

			    Properties properties = System.getProperties();
				// Setup mail server
				properties.setProperty("mail.smtp.host", mailSender.getHost());
				//properties.put("mail.smtp.starttls.enable", "true");
				// SSL Port
				properties.put("mail.smtp.port", mailSender.getPort());
				// enable authentication
				properties.put("mail.smtp.auth", "true");
				// SSL Factory
				//properties.put("mail.smtp.socketFactory.class", "jakarta.net.ssl.SSLSocketFactory");
			    
				properties.put("mail.smtp.starttls.enable", "true");

			    Session session = Session.getDefaultInstance(properties, new jakarta.mail.Authenticator() {
					// override the getPasswordAuthentication
					// method
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(mailSender.getUsername(), mailSender.getPassword());
					}
				});
			    int mailSendresult = 0;
			    try {
			    	MimeMessage message = new MimeMessage(session);
			    	message.setFrom(new InternetAddress(mailSender.getUsername()));
		            //message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
		            message.addRecipient(Message.RecipientType.TO, new InternetAddress(DronaEmail));
					message.setSubject(subject);
					message.setText(msg);
					message.setContent(msg, "text/html");// this code is used to make the message in HTML formatting
					// Send message
					Transport.send(message);
					System.out.println("Message Sent");
					mailSendresult++;
			    } catch (AuthenticationFailedException e) {
			        // Handle authentication failure (wrong password) and complete the CompletableFuture with -2
			        e.printStackTrace();
			        System.out.println("ERORRRR -2");
			        return CompletableFuture.completedFuture(-2);
			    } catch (MessagingException e) {
			        // Handle other email sending exceptions and complete the CompletableFuture with -1
			        e.printStackTrace();
			        System.out.println("ERORRRR -1");
			        return CompletableFuture.completedFuture(-1);
			    }
			    return CompletableFuture.completedFuture(mailSendresult);
			  }
	    }
	 	
	 	
		public int sendMessage(String toEmail, String subject, String msg)  {
			
			String typeOfHost = "L";
			MailConfigurationDto mailAuthentication=null;
			try {
				mailAuthentication = mailService.getMailConfigByTypeOfHost(typeOfHost);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(mailAuthentication==null)return -1;
			// change accordingly
			String to = toEmail;
			// change accordingly
			String from = mailAuthentication.getUsername().toString();
			// or IP address
			String hostAddress = mailAuthentication.getHost().toString();
			// mail id from which mail will go
			final String username = mailAuthentication.getUsername().toString();
			// correct password for gmail id
			final String password = mailAuthentication.getPassword().toString();
			System.out.println("Sending Mail to" + toEmail);
			// Get the session object
			// Get system properties
			Properties properties = System.getProperties();
			// Setup mail server
			properties.setProperty("mail.smtp.host", host);
			//properties.put("mail.smtp.starttls.enable", "true");
			// SSL Port
			properties.put("mail.smtp.port", port);
			// enable authentication
			properties.put("mail.smtp.auth", "true");
			// SSL Factory
			properties.put("mail.smtp.socketFactory.class", "jakarta.net.ssl.SSLSocketFactory");
			// creating Session instance referenced to
			// Authenticator object to pass in
			// Session.getInstance argument
			Session session = Session.getDefaultInstance(properties, new jakarta.mail.Authenticator() {
				// override the getPasswordAuthentication
				// method
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});
			int mailSendresult = 0;
			// compose the message
			try {
				// jakarta.mail.internet.MimeMessage class is mostly
				// used for abstraction.
				MimeMessage message = new MimeMessage(session);
				// header field of the header.
				message.setFrom(new InternetAddress(from));
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
				message.setSubject(subject);
				message.setText(msg);
				message.setContent(msg, "text/html");// this code is used to make the message in HTML formatting
				// Send message
				Transport.send(message);
				System.out.println("Message Sent");
				mailSendresult++;
			} catch (MessagingException mex) {
				mex.printStackTrace();
			}
			return mailSendresult;
		}

		
public int sendMessage(String []Email, String subject, String msg)  {
	long startTime = System.currentTimeMillis();
			String typeOfHost = "L";
			MailConfigurationDto mailAuthentication=null;
			try {
				mailAuthentication = mailService.getMailConfigByTypeOfHost(typeOfHost);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// change accordingly
			String to = Email[0];
			// change accordingly
			String from = mailAuthentication.getUsername().toString();
			// or IP address
			String hostAddress = mailAuthentication.getHost().toString();
			// mail id from which mail will go
			final String username = mailAuthentication.getUsername().toString();
			// correct password for gmail id
			final String password = mailAuthentication.getPassword().toString();
			System.out.println("Sending Mail to" + Email[0]);
			// Get the session object
			// Get system properties
			Properties properties = System.getProperties();
			// Setup mail server
			properties.setProperty("mail.smtp.host", host);
			properties.put("mail.smtp.starttls.enable", "true");
			// SSL Port
			properties.put("mail.smtp.port", mailAuthentication.getPort());
			// enable authentication
			properties.put("mail.smtp.auth", "true");
			// SSL Factory
			properties.put("mail.smtp.socketFactory.class", "jakarta.net.ssl.SSLSocketFactory");
			// creating Session instance referenced to
			// Authenticator object to pass in
			// Session.getInstance argument
			Session session = Session.getDefaultInstance(properties, new jakarta.mail.Authenticator() {
				// override the getPasswordAuthentication
				// method
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(from, password);
				}
			});
			int mailSendresult = 0;
			// compose the message
			try {
				// jakarta.mail.internet.MimeMessage class is mostly
				// used for abstraction.
				MimeMessage message = new MimeMessage(session);
				// header field of the header.
				message.setFrom(new InternetAddress(from));
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
				for (String ccRecipient : Email) {
					message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccRecipient));
				}
				message.setSubject(subject);
				message.setText(msg);
				message.setContent(msg, "text/html");// this code is used to make the message in HTML formatting
				// Send message
				Transport.send(message);
				long endTime = System.currentTimeMillis();
				long elapsedTime = endTime - startTime;
				System.out.println("Elapsed time: " + elapsedTime + " milliseconds");
				mailSendresult++;
			} catch (MessagingException mex) {
				mex.printStackTrace();
			}
			return mailSendresult;
		}


public int sendMessage1(String[] toEmail, String subject, String msg)  {
	 String typeOfHost = "D";
	MailConfigurationDto mailAuthentication;
	try {
		mailAuthentication = mailService.getMailConfigByTypeOfHost(typeOfHost);
	} catch (Exception e1) {
		e1.printStackTrace();
		return (Integer) null;
	}

	
	  if (mailAuthentication == null) {
	      // Handle the case where mail configuration for the specified typeOfHost is not found
		  System.out.println("ERRROR -3 ERROR MOT CAUGHTTTTT");
	      return-3; // You can choose an appropriate error code
	  }else {

    		 System.out.println("URGENTDAKKKSEND @@@@@@@@@@@@@@@@@@@@@@@");
			 System.out.println("spring.mail.host "+mailAuthentication.getHost());
			 System.out.println("spring.mail.port "+mailAuthentication.getPort());
			 System.out.println("spring.mail.username "+mailAuthentication.getUsername());
			 System.out.println("spring.mail.password "+mailAuthentication.getPassword());
  
			  JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

			    // Set mail configuration from the database
			    mailSender.setHost(mailAuthentication.getHost().toString());
			    mailSender.setPort(Integer.parseInt(mailAuthentication.getPort().toString()));
			    mailSender.setUsername(mailAuthentication.getUsername().toString());
			    mailSender.setPassword(mailAuthentication.getPassword().toString());

			    Properties properties = System.getProperties();
				// Setup mail server
				properties.setProperty("mail.smtp.host", mailSender.getHost());
				//properties.put("mail.smtp.starttls.enable", "true");
				// SSL Port
				properties.put("mail.smtp.port", mailSender.getPort());
				// enable authentication
				properties.put("mail.smtp.auth", "true");
				// SSL Factory
				//properties.put("mail.smtp.socketFactory.class", "jakarta.net.ssl.SSLSocketFactory");
			    
				properties.put("mail.smtp.starttls.enable", "true");

			    Session session = Session.getDefaultInstance(properties, new jakarta.mail.Authenticator() {
					// override the getPasswordAuthentication
					// method
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(mailSender.getUsername(), mailSender.getPassword());
					}
				});
			    int mailSendresult = 0;
	
				try {
					
					MimeMessage message = new MimeMessage(session);
					
					message.setFrom(new InternetAddress(mailSender.getUsername()));
					for (String ccRecipient : toEmail) {
						message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccRecipient));
					}
					message.setSubject(subject);
					message.setText(msg);
					message.setContent(msg, "text/html");// this code is used to make the message in HTML formatting
					
					mailSender.send(message);
					System.out.println("Message Sent");
					mailSendresult++;
				} catch (MessagingException mex) {
					mex.printStackTrace();
				}
				return mailSendresult;
		 }
}

@RequestMapping(value = "sendMails.htm", method = RequestMethod.GET)
public @ResponseBody String sendMails(HttpServletRequest req, HttpServletResponse res, RedirectAttributes redir,HttpSession ses) throws Exception {
	
	String scheduleDate= req.getParameter("scheduledate");
	
	int mailSend = reporttodayCurrentTime(scheduleDate);
	System.out.println("mailSend"+mailSend);
	Gson json = new Gson();
	return json.toJson(mailSend);
}
}


