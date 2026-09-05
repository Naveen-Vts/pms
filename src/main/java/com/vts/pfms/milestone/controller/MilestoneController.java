package com.vts.pfms.milestone.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

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
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.google.gson.Gson;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.vts.pfms.FormatConverter;
import com.vts.pfms.Zipper;
import com.vts.pfms.committee.model.ActionAssign;
import com.vts.pfms.committee.service.ActionService;
import com.vts.pfms.committee.service.CommitteeService;
import com.vts.pfms.milestone.dto.FileDocAmendmentDto;
import com.vts.pfms.milestone.dto.FileProjectDocDto;
import com.vts.pfms.milestone.dto.FileUploadDto;
import com.vts.pfms.milestone.dto.MileEditDto;
import com.vts.pfms.milestone.dto.MilestoneActivityDto;
import com.vts.pfms.milestone.dto.ProjectEconomicImpactDto;
import com.vts.pfms.milestone.dto.ProjectInfrastructureUtilizationDto;
import com.vts.pfms.milestone.dto.ProjectManPowerUtilizationDto;
import com.vts.pfms.milestone.dto.ProjectTrainingUtilizationDto;
import com.vts.pfms.milestone.dto.RevisionItemDto;
import com.vts.pfms.milestone.dto.RevisionSummaryDto;
import com.vts.pfms.milestone.model.FileDocMaster;
import com.vts.pfms.milestone.model.FileRepMaster;
import com.vts.pfms.milestone.model.FileRepMasterPreProject;
import com.vts.pfms.milestone.model.FileRepNew;
import com.vts.pfms.milestone.model.FileRepUploadNew;
import com.vts.pfms.milestone.model.FileRepUploadPreProject;
import com.vts.pfms.milestone.model.MilestoneActivity;
import com.vts.pfms.milestone.model.MilestoneActivityBriefing;
import com.vts.pfms.milestone.model.MilestoneActivityLevel;
import com.vts.pfms.milestone.model.MilestoneActivityLevelRemarks;
import com.vts.pfms.milestone.model.MilestoneActivityPredecessor;
import com.vts.pfms.milestone.model.MilestoneActivitySub;
import com.vts.pfms.milestone.model.ProjectEconomicImpact;
import com.vts.pfms.milestone.model.ProjectEconomicImpactRev;
import com.vts.pfms.milestone.model.ProjectInfrastructureUtilization;
import com.vts.pfms.milestone.model.ProjectTrainingUtilization;
import com.vts.pfms.milestone.service.MilestoneService;
import com.vts.pfms.print.service.PrintService;
import com.vts.pfms.requirements.service.RequirementService;
import com.vts.pfms.timesheet.service.TimeSheetService;
import com.vts.pfms.utils.InputValidator;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class MilestoneController {

	FormatConverter fc = new FormatConverter();
	private SimpleDateFormat sdtf = fc.getSqlDateAndTimeFormat();
	
	@Autowired CommitteeService committeservice;

	@Autowired
	MilestoneService service;

	@Autowired
	PrintService printservice;
	
	@Autowired
	TimeSheetService timesheetservice;
	
	@Autowired
	ActionService actionservice;
	
	@Autowired
	RequirementService reqservice;
	
	private static final Logger logger=LogManager.getLogger(MilestoneController.class);
	@Value("${File_Size}")
	private String GlobalFileSize;

	@Value("${ApplicationFilesDrive}")
	private String FilePath;

	@RequestMapping(value = "MilestoneActivityList.htm")
	public String MilestoneActivityList(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{

		String UserId = (String) ses.getAttribute("Username");
		String Logintype= (String)ses.getAttribute("LoginType");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside MilestoneActivityList.htm "+UserId);		
		try {
			String ProjectId=req.getParameter("ProjectId");
			if(ProjectId==null)  {
				Map md=model.asMap();
				ProjectId=(String)md.get("ProjectId");
			}	
			List<Object[] > projlist= service.LoginProjectDetailsList(EmpId,Logintype,LabCode);

			if(projlist.size()==0) 
			{				
				redir.addAttribute("resultfail", "No Project is Assigned to you.");
				return "redirect:/MainDashBoard.htm";
			}



			if(ProjectId==null) {
				try {
					Object[] pro=projlist.get(0);
					ProjectId=pro[0].toString();
				}catch (Exception e) {

				}
			}
			req.setAttribute("EmployeeList", service.EmployeeList());

			List<Object[]> main=service.MilestoneActivityList(ProjectId);
			req.setAttribute("MilestoneActivityList",main );
			req.setAttribute("ProjectList",projlist);
			req.setAttribute("ProjectId", ProjectId);
			req.setAttribute("EmployeeList", service.EmployeeList());
			if(ProjectId!=null) {
				req.setAttribute("ProjectDetails", service.ProjectDetails(ProjectId).get(0));
				int MainCount=1;
				for(Object[] objmain:main ) {
					int countA=1;
					List<Object[]>  MilestoneActivityA=service.MilestoneActivityLevel(objmain[0].toString(),"1");
					req.setAttribute(MainCount+"MilestoneActivityA", MilestoneActivityA);
					for(Object[] obj:MilestoneActivityA) {
						List<Object[]>  MilestoneActivityB=service.MilestoneActivityLevel(obj[0].toString(),"2");
						req.setAttribute(MainCount+"MilestoneActivityB"+countA, MilestoneActivityB);
						int countB=1;
						for(Object[] obj1:MilestoneActivityB) {
							List<Object[]>  MilestoneActivityC=service.MilestoneActivityLevel(obj1[0].toString(),"3");
							req.setAttribute(MainCount+"MilestoneActivityC"+countA+countB, MilestoneActivityC);
							int countC=1;
							for(Object[] obj2:MilestoneActivityC) {
								List<Object[]>  MilestoneActivityD=service.MilestoneActivityLevel(obj2[0].toString(),"4");
								req.setAttribute(MainCount+"MilestoneActivityD"+countA+countB+countC, MilestoneActivityD);
								int countD=1;
								for(Object[] obj3:MilestoneActivityD) {
									List<Object[]>  MilestoneActivityE=service.MilestoneActivityLevel(obj3[0].toString(),"5");
									req.setAttribute(MainCount+"MilestoneActivityE"+countA+countB+countC+countD, MilestoneActivityE);
									countD++;
								}
								countC++;
							}
							countB++;
						}
						countA++;
					}
					MainCount++;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +" Inside MilestoneActivityList.htm "+UserId, e); 
			return "static/Error";

		}
		return "milestone/MilestoneActivityList";
	}

	@RequestMapping(value = "M-A-AssigneeList.htm")
	public String MilestoneActivityAssigneeList(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside M-A-AssigneeList.htm "+UserId);		
		try {
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String Logintype= (String)ses.getAttribute("LoginType");
			String ProjectId=req.getParameter("ProjectId");
			if(ProjectId==null)  {
				Map md=model.asMap();
				ProjectId=(String)md.get("ProjectId");
			}	
			List<Object[] > projlist=service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
			if(ProjectId==null) {
				try {
					Object[] pro=projlist.get(0);
					ProjectId=pro[0].toString();
				}catch (Exception e) {

				}
			}

			req.setAttribute("ProjectList", projlist);
			List<Object[]> totalAssignedMainList = new ArrayList<>();
			List<Object[]> totalAssignedSubList = new ArrayList<>();
			//Map<Long, Long> milestoneActivityIdsMap = new HashMap<>();
//			Map<Long, String]> milestoneActivityLevelsMap = new HashMap<>();
			Map<Long, Object[]> milestoneActivityLevelsMap = new HashMap<>();
			
			List<Object[]> main = service.MilestoneActivityAssigneeList(ProjectId, "A");
			
			if (main != null && !main.isEmpty()) {
				if("A".equalsIgnoreCase(Logintype) || "Z".equalsIgnoreCase(Logintype)) {
					totalAssignedMainList.addAll(main);
				}else if("P".equalsIgnoreCase(Logintype)) {
					totalAssignedMainList.addAll(main.stream()
							.filter(e ->  (e[11].toString().equalsIgnoreCase(EmpId) || e[17].toString().equalsIgnoreCase(EmpId))|| (e[18].toString().equalsIgnoreCase(EmpId)))
	                         .collect(Collectors.toList()));
				}else {
					totalAssignedMainList.addAll(main.stream()
	    					 .filter(e -> (e[11].toString().equalsIgnoreCase(EmpId) || e[17].toString().equalsIgnoreCase(EmpId)))
	                         .collect(Collectors.toList()));
				}
				
			}
			
			
			req.setAttribute("MilestoneActivityList",main );	
			req.setAttribute("ProjectId", ProjectId);
			
			if(ProjectId!=null) {
				req.setAttribute("ProjectDetails", service.ProjectDetails(ProjectId).get(0));
				
				//int MainCount=1;
				for(Object[] objmain : main ) {
					
					List<Object[]>  MilestoneActivityA = service.MilestoneActivityLevel(objmain[0].toString(),"1");
					if (MilestoneActivityA != null && !MilestoneActivityA.isEmpty()) {
						if(Arrays.asList("A", "Z").contains(Logintype)) {
							totalAssignedSubList.addAll(MilestoneActivityA);
						}else if("P".equalsIgnoreCase(Logintype) && (objmain[18].toString().equalsIgnoreCase(EmpId))) {
							totalAssignedSubList.addAll(MilestoneActivityA);
						}else {
							totalAssignedSubList.addAll(MilestoneActivityA.stream()
			    					 .filter(e -> (e[13].toString().equalsIgnoreCase(EmpId) || e[15].toString().equalsIgnoreCase(EmpId)))
			                         .collect(Collectors.toList()));
						}
						
					}
					//req.setAttribute(MainCount+"MilestoneActivityA", MilestoneActivityA);
					
					int countA=1;
					for(Object[] obj:MilestoneActivityA) {
						//milestoneActivityIdsMap.put(Long.parseLong(obj[0].toString()), Long.parseLong(objmain[0].toString()));
						//milestoneActivityLevelsMap.put(Long.parseLong(obj[0].toString()), "A"+countA);
						Object[] levels1 = new Object[3];
						levels1[0] = Long.parseLong(objmain[0].toString());
						levels1[1] = "A"+countA;
						levels1[2] = "M"+objmain[5];
						milestoneActivityLevelsMap.put(Long.parseLong(obj[0].toString()), levels1);
						List<Object[]>  MilestoneActivityB=service.MilestoneActivityLevel(obj[0].toString(),"2");
						if (MilestoneActivityB != null && !MilestoneActivityB.isEmpty()) {
							if(Arrays.asList("A", "Z").contains(Logintype)) {
								totalAssignedSubList.addAll(MilestoneActivityB);
							}else if("P".equalsIgnoreCase(Logintype) && (objmain[18].toString().equalsIgnoreCase(EmpId))) {
								totalAssignedSubList.addAll(MilestoneActivityB);
							}else {
								totalAssignedSubList.addAll(MilestoneActivityB.stream()
							    					 .filter(e -> (e[13].toString().equalsIgnoreCase(EmpId) || e[15].toString().equalsIgnoreCase(EmpId)))
							                         .collect(Collectors.toList()));
							}
						}
						//req.setAttribute(MainCount+"MilestoneActivityB"+countA, MilestoneActivityB);
						
						int countB=1;
						for(Object[] obj1:MilestoneActivityB) {
//							milestoneActivityIdsMap.put(Long.parseLong(obj1[0].toString()), Long.parseLong(objmain[0].toString()));
//							milestoneActivityLevelsMap.put(Long.parseLong(obj1[0].toString()), "A"+countA+"-B"+countB);
							Object[] levels2 = new Object[3];
							levels2[0] = Long.parseLong(objmain[0].toString());
							levels2[1] = "A"+countA+"-B"+countB;
							levels2[2] = "M"+objmain[5];
							milestoneActivityLevelsMap.put(Long.parseLong(obj1[0].toString()), levels2);
							List<Object[]>  MilestoneActivityC=service.MilestoneActivityLevel(obj1[0].toString(),"3");
							if (MilestoneActivityC != null && !MilestoneActivityC.isEmpty()) {
								if(Arrays.asList("A", "Z").contains(Logintype)) {
									totalAssignedSubList.addAll(MilestoneActivityC);
								}else if("P".equalsIgnoreCase(Logintype) && (objmain[18].toString().equalsIgnoreCase(EmpId))) {
									totalAssignedSubList.addAll(MilestoneActivityC);
								}else {
									totalAssignedSubList.addAll(MilestoneActivityC.stream()
								    					 .filter(e -> (e[13].toString().equalsIgnoreCase(EmpId) || e[15].toString().equalsIgnoreCase(EmpId)))
								                         .collect(Collectors.toList()));
								}
							}
							//req.setAttribute(MainCount+"MilestoneActivityC"+countA+countB, MilestoneActivityC);
							
							int countC=1;
							for(Object[] obj2:MilestoneActivityC) {
//								milestoneActivityIdsMap.put(Long.parseLong(obj2[0].toString()), Long.parseLong(objmain[0].toString()));
//								milestoneActivityLevelsMap.put(Long.parseLong(obj2[0].toString()), "A"+countA+"-B"+countB+"-C"+countC);
								Object[] levels3 = new Object[3];
								levels3[0] = Long.parseLong(objmain[0].toString());
								levels3[1] = "A"+countA+"-B"+countB+"-C"+countC;
								levels3[2] = "M"+objmain[5];
								milestoneActivityLevelsMap.put(Long.parseLong(obj2[0].toString()), levels3);
								List<Object[]>  MilestoneActivityD=service.MilestoneActivityLevel(obj2[0].toString(),"4");
								if (MilestoneActivityD != null && !MilestoneActivityD.isEmpty()) {
									if(Arrays.asList("A", "Z").contains(Logintype)) {
										totalAssignedSubList.addAll(MilestoneActivityD);
									}else if("P".equalsIgnoreCase(Logintype) && (objmain[18].toString().equalsIgnoreCase(EmpId))) {
										totalAssignedSubList.addAll(MilestoneActivityD);
									}else {
										totalAssignedSubList.addAll(MilestoneActivityD.stream()
									    					 .filter(e -> (e[13].toString().equalsIgnoreCase(EmpId) || e[15].toString().equalsIgnoreCase(EmpId)))
									                         .collect(Collectors.toList()));
									}
								}
								//req.setAttribute(MainCount+"MilestoneActivityD"+countA+countB+countC, MilestoneActivityD);
								
								int countD=1;
								for(Object[] obj3:MilestoneActivityD) {
//									milestoneActivityIdsMap.put(Long.parseLong(obj3[0].toString()), Long.parseLong(objmain[0].toString()));
//									milestoneActivityLevelsMap.put(Long.parseLong(obj3[0].toString()), "A"+countA+"-B"+countB+"-C"+countC+"-D"+countD);
									Object[] levels4 = new Object[3];
									levels4[0] = Long.parseLong(objmain[0].toString());
									levels4[1] = "A"+countA+"-B"+countB+"-C"+countC+"-D"+countD;
									levels4[2] = "M"+objmain[5];
									milestoneActivityLevelsMap.put(Long.parseLong(obj3[0].toString()), levels4);
									List<Object[]>  MilestoneActivityE=service.MilestoneActivityLevel(obj3[0].toString(),"5");
									if (MilestoneActivityE != null && !MilestoneActivityE.isEmpty()) {
										if(Arrays.asList("A", "Z").contains(Logintype)) {
											totalAssignedSubList.addAll(MilestoneActivityE);
										}else if("P".equalsIgnoreCase(Logintype) && (objmain[18].toString().equalsIgnoreCase(EmpId))) {
											totalAssignedSubList.addAll(MilestoneActivityE);
										}else {
											totalAssignedSubList.addAll(MilestoneActivityE.stream()
										    					 .filter(e -> (e[13].toString().equalsIgnoreCase(EmpId) || e[15].toString().equalsIgnoreCase(EmpId)))
										                         .collect(Collectors.toList()));
										}
									}
									int countE = 1;
									for(Object[] obj4:MilestoneActivityE) {
										Object[] levels5 = new Object[3];
										levels5[0] = Long.parseLong(objmain[0].toString());
										levels5[1] = "A"+countA+"-B"+countB+"-C"+countC+"-D"+countD+"-E"+countE;
										levels5[2] = "M"+objmain[5];
										milestoneActivityLevelsMap.put(Long.parseLong(obj4[0].toString()), levels5);
										countE++;
									}
									countD++;
								}
								countC++;
							}  
							
							countB++;
						}
						countA++;
					}
					//MainCount++;
				}

			}
			
			req.setAttribute("totalAssignedMainList", totalAssignedMainList);
			req.setAttribute("totalAssignedSubList", totalAssignedSubList);
			//req.setAttribute("milestoneActivityIdsMap", milestoneActivityIdsMap);
			req.setAttribute("milestoneActivityLevelsMap", milestoneActivityLevelsMap);
			
			return "milestone/MileAssigneeList";
		}catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside M-A-AssigneeList.htm "+UserId, e); 
			return "static/Error";

		}

	}

	@RequestMapping(value = "MilestoneActivityAdd.htm")
	public String MilestoneActivityAdd(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode =(String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside MilestoneActivityAdd.htm "+UserId);
		try {
			String ProjectId=req.getParameter("ProjectId");
			req.setAttribute("allLabList", committeservice.AllLabList());
			req.setAttribute("EmployeeList", service.ProjectEmpList(ProjectId , LabCode));
			req.setAttribute("ProjectDetails", service.ProjectDetails(ProjectId).get(0));
			req.setAttribute("ActivityTypeList", service.ActivityTypeList());
			req.setAttribute("ProjectId", ProjectId);
			req.setAttribute("projectDirector", req.getParameter("projectDirector"));
		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside MilestoneActivityAdd.htm "+UserId, e); 
			return "static/Error";
		}
		return "milestone/MilestoneActivityAdd";

	}


	@RequestMapping(value = "MilestoneActivityAddSubmit.htm", method = RequestMethod.POST)
	public String MilestoneActivityAddSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside MilestoneActivityAddSubmit.htm "+UserId);		
		try {


			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String activityName=req.getParameter("ActivityName");
			if(InputValidator.isContainsHTMLTags(activityName)) {
				redir.addAttribute("ProjectId", req.getParameter("ProjectId"));
				return redirectWithError(redir, "MilestoneActivityAdd.htm", "'Activity Name' should not contain HTML Tags.!");
			}

			MilestoneActivityDto mainDto=new MilestoneActivityDto();
			mainDto.setActivityType(req.getParameter("ActivityType"));
			mainDto.setProjectId(req.getParameter("ProjectId"));
			mainDto.setOicEmpId(req.getParameter("EmpId"));
			mainDto.setOicEmpId1(req.getParameter("EmpId1"));
			mainDto.setActivityName(activityName);
			mainDto.setMilestoneNo(req.getParameter("MilestoneNo"));
			mainDto.setStartDate(req.getParameter("ValidFrom"));
			mainDto.setEndDate(req.getParameter("ValidTo"));
			mainDto.setCreatedBy(UserId);
			long count =service.MilestoneActivityInsert(mainDto);

			if (count > 0) {
				redir.addAttribute("result", "Milestone Activity  Added Successfully");
			} else {
				redir.addAttribute("resultfail", "Milestone Activity Add Unsuccessful");
				return "redirect:/MilestoneActivityList.htm";
			}
			redir.addFlashAttribute("MilestoneActivityId", count+"");
			redir.addAttribute("ProjectId", req.getParameter("ProjectId"));
			redir.addAttribute("projectDirector", req.getParameter("projectDirector"));
		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside MilestoneActivityAddSubmit.htm "+UserId, e); 
			return "static/Error";
		}	
		return "redirect:/MA-DetailsRedirect.htm";
	}

	@RequestMapping(value = "MilestoneActivityDetails.htm")
	public String MilestoneActivityDetails(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode =(String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside MilestoneActivityDetails.htm "+UserId);		
		try {
			int countA=1;
			List<Object[]> list = service.MilestoneActivity(req.getParameter("MilestoneActivityId"));
			req.setAttribute("MilestoneActivity", list != null && !list.isEmpty() ? list.get(0) : new Object[100]);
			
	 
			List<Object[]>  MilestoneActivityA=service.MilestoneActivityLevel(req.getParameter("MilestoneActivityId"),"1");
			req.setAttribute("MilestoneActivityA", MilestoneActivityA);
			for(Object[] obj:MilestoneActivityA) {
				List<Object[]>  MilestoneActivityB=service.MilestoneActivityLevel(obj[0].toString(),"2");
				req.setAttribute("MilestoneActivityB"+countA, MilestoneActivityB);
				int countB=1;
				for(Object[] obj1:MilestoneActivityB) {
					List<Object[]>  MilestoneActivityC=service.MilestoneActivityLevel(obj1[0].toString(),"3");
					req.setAttribute("MilestoneActivityC"+countA+countB, MilestoneActivityC);

					int countC=1;
					for(Object[] obj2:MilestoneActivityC) {
						List<Object[]>  MilestoneActivityD=service.MilestoneActivityLevel(obj2[0].toString(),"4");
						req.setAttribute("MilestoneActivityD"+countA+countB+countC, MilestoneActivityD);
						int countD=1;
						for(Object[] obj3:MilestoneActivityD) {
							List<Object[]>  MilestoneActivityE=service.MilestoneActivityLevel(obj3[0].toString(),"5");
							req.setAttribute("MilestoneActivityE"+countA+countB+countC+countD, MilestoneActivityE);
							countD++;
						}
						countC++;
					}
					countB++;
				}
				countA++;
			}	
			req.setAttribute("ActivityTypeList", service.ActivityTypeList());
			String projectId=req.getParameter("ProjectId");
			req.setAttribute("EmployeeList", service.ProjectEmpList(projectId , LabCode));
			req.setAttribute("allLabList", committeservice.AllLabList());
			req.setAttribute("ProjectId", projectId);
			req.setAttribute("projectDirector", req.getParameter("projectDirector"));
			if("C".equalsIgnoreCase(req.getParameter("sub"))) {
				req.setAttribute("RevisionCount", service.MilestoneRevisionCount(req.getParameter("MilestoneActivityId")));
				return "milestone/MilestoneActivityPreview";
			}
			return "milestone/MilestoneActivityDetails";
		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside MilestoneActivityDetails.htm "+UserId, e); 
			return "static/Error";			
		}

	}

	@RequestMapping(value = "MilestoneActivitySubAdd.htm", method = RequestMethod.POST)
	public String MilestoneActivitySubAdd(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside MilestoneActivitySubAdd.htm "+UserId);		
		try {

			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String activityName=req.getParameter("ActivityName");
			if(InputValidator.isContainsHTMLTags(activityName)) {
				
				redir.addAttribute("ProjectId", req.getParameter("ProjectId"));
				redir.addAttribute("MilestoneActivityId", req.getParameter("MilestoneActivityId"));
				redir.addAttribute("formname", req.getParameter("formname"));
				return redirectWithError(redir, "MilestoneActivityDetails.htm", "'Activity Name' should not contain HTML Tags.!");
			}
			redir.addFlashAttribute("MilestoneActivityId", req.getParameter("MilestoneActivityId"));
			redir.addFlashAttribute("formname", req.getParameter("formname"));
			MilestoneActivityDto mainDto=new MilestoneActivityDto();
			mainDto.setActivityType(req.getParameter("ActivityType"));
			mainDto.setActivityName(activityName);
			mainDto.setActivityId(req.getParameter("ActivityId"));
			mainDto.setLevelId(req.getParameter("LevelId"));
			mainDto.setStartDate(req.getParameter("ValidFrom"));
			mainDto.setEndDate(req.getParameter("ValidTo"));
			mainDto.setWeightage(req.getParameter("Weightage"));
			mainDto.setOicEmpId(req.getParameter("EmpId"));
			mainDto.setOicEmpId1(req.getParameter("EmpId1"));
			mainDto.setCreatedBy(UserId);
			long count =service.MilestoneActivityLevelInsert(mainDto);

			if (count > 0) {
				redir.addAttribute("result", "Milestone Activity Sub Level Added Successfully");
			} else {
				redir.addAttribute("resultfail", "Milestone Activity Sub Level Add Unsuccessful");
			}
			redir.addAttribute("ProjectId", req.getParameter("ProjectId"));
			redir.addAttribute("projectDirector", req.getParameter("projectDirector"));
		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside MilestoneActivitySubAdd.htm "+UserId, e); 
			return "static/Error";
		}


		return "redirect:/MA-DetailsRedirect.htm";
	}

	@RequestMapping(value = "MA-DetailsRedirect.htm", method = RequestMethod.GET)
	public String MADetailsRedirect(Model model,HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside DetailsRedirect.htm "+UserId);		
		try {
			int countA=1;	
			String MainId=null;
			String FromName=null;
			Map md = model.asMap();
			for (Object modelKey : md.keySet()) {
				MainId = (String) md.get("MilestoneActivityId");
				FromName = (String) md.get("formname");   
			}
			if(MainId==null) {
				redir.addAttribute("resultfail", "Refresh Not Allowed");
				return "redirect:/MilestoneActivityList.htm";
			}
			req.setAttribute("MilestoneActivity", service.MilestoneActivity(MainId).get(0));
			List<Object[]>  MilestoneActivityA=service.MilestoneActivityLevel(MainId,"1");
			req.setAttribute("MilestoneActivityA", MilestoneActivityA);
			for(Object[] obj:MilestoneActivityA) {
				List<Object[]>  MilestoneActivityB=service.MilestoneActivityLevel(obj[0].toString(),"2");
				req.setAttribute("MilestoneActivityB"+countA, MilestoneActivityB);
				int countB=1;
				for(Object[] obj1:MilestoneActivityB) {
					List<Object[]>  MilestoneActivityC=service.MilestoneActivityLevel(obj1[0].toString(),"3");
					req.setAttribute("MilestoneActivityC"+countA+countB, MilestoneActivityC);

					int countC=1;
					for(Object[] obj2:MilestoneActivityC) {
						List<Object[]>  MilestoneActivityD=service.MilestoneActivityLevel(obj2[0].toString(),"4");
						req.setAttribute("MilestoneActivityD"+countA+countB+countC, MilestoneActivityD);
						int countD=1;
						for(Object[] obj3:MilestoneActivityD) {
							List<Object[]>  MilestoneActivityE=service.MilestoneActivityLevel(obj3[0].toString(),"5");
							req.setAttribute("MilestoneActivityE"+countA+countB+countC+countD, MilestoneActivityE);
							countD++;
						}
						countC++;
					}
					countB++;
				}
				countA++;
			}
			req.setAttribute("ActivityTypeList", service.ActivityTypeList());
			req.setAttribute("FromName",FromName);
			String projectId=req.getParameter("ProjectId");
			req.setAttribute("EmployeeList", service.ProjectEmpList(projectId , LabCode));
			req.setAttribute("allLabList", committeservice.AllLabList());
			req.setAttribute("ProjectId", projectId);
			req.setAttribute("projectDirector", req.getParameter("projectDirector"));
		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside DetailsRedirect.htm "+UserId, e); 
			return "static/Error";
		}

		return "milestone/MilestoneActivityDetails";
	}



	@RequestMapping(value = "M-A-Assign-OIC.htm", method = RequestMethod.POST)
	public String AssignToOic(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside M-A-Assign-OIC.htm "+UserId);		
		try {


			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			redir.addFlashAttribute("MilestoneActivityId", req.getParameter("MilestoneActivityId"));
			redir.addFlashAttribute("Sub", req.getParameter("sub"));
			MilestoneActivityDto mainDto=new MilestoneActivityDto();
			mainDto.setActivityId(req.getParameter("MilestoneActivityId"));
			mainDto.setOicEmpId(EmpId);
			mainDto.setStatusRemarks(req.getParameter("Remarks"));
			mainDto.setActivityType(req.getParameter("sub"));
			mainDto.setRevisionNo(req.getParameter("RevId"));
			mainDto.setCreatedBy(UserId);

			long count =service.MilestoneActivityAssign(mainDto);

			if (count == 1) {
				if("Assign".equalsIgnoreCase(req.getParameter("sub"))) {
					redir.addAttribute("result", "Milestone Activity Assigned Successfuly.");
				}else if("Accept".equalsIgnoreCase(req.getParameter("sub"))) {
					redir.addAttribute("result", "Milestone Activity Accepted And Base Line 0 Created   Successfuly.");	
				}else if("Back".equalsIgnoreCase(req.getParameter("sub"))) {
					redir.addAttribute("result", "Milestone Activity Sent Back  Successfuly.");	
				}
			}else if(count==2) {
				redir.addAttribute("resultfail", "Milestone Activity Sub Level Total Weightage less than 100%, Please Modify");
			}else if(count==3) {
				redir.addAttribute("resultfail", "Milestone Activity Sub Level Not Exist, Please Add One Sub Level");
			}else if(count==4) {
				redir.addAttribute("resultfail", "Milestone Activity Sub Level Total Weightage less than 100%, Please Send Back");
			}
			else {
				redir.addAttribute("resultfail", "Milestone Activity Assign Unsuccessful");
			}
			if("Assign".equalsIgnoreCase(req.getParameter("sub"))) {
				redir.addFlashAttribute("ProjectId",req.getParameter("projectid"));
				return "redirect:/MilestoneActivityList.htm";
			}
			redir.addFlashAttribute("ProjectId",req.getParameter("projectid"));
			return "redirect:/M-A-AssigneeList.htm";

		}catch (Exception e) {	
			e.printStackTrace(); 
			logger.error(new Date() +" Inside M-A-Assign-OIC.htm "+UserId, e); 
			return "static/Error";
		}


	}

	@RequestMapping(value = "M-A-Set-BaseLine.htm", method = RequestMethod.POST)
	public String BaseLine(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside M-A-Set-BaseLine.htm "+UserId);		
		try {


			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			redir.addFlashAttribute("MilestoneActivityId", req.getParameter("MilestoneActivityId"));
			MilestoneActivityDto mainDto=new MilestoneActivityDto();
			mainDto.setActivityId(req.getParameter("MilestoneActivityId"));
			mainDto.setRevisionNo(req.getParameter("RevId"));
			mainDto.setCreatedBy(UserId);
			long count =service.MilestoneActivityRevision(mainDto);

			if (count > 0) {
				redir.addAttribute("result", "Milestone Activity Base Line "+req.getParameter("RevId")+"  Successful.");
			} else {
				redir.addAttribute("resultfail", "Milestone Activity Revision Unsuccessful");
			}
			redir.addAttribute("projectDirector", req.getParameter("projectDirector"));
		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside M-A-Set-BaseLine.htm "+UserId, e); 
			return "static/Error";
		}


		return "redirect:/MA-PreviewRedirect.htm";
	}



	@RequestMapping(value = "MA-PreviewRedirect.htm", method = RequestMethod.GET)
	public String previewRedirect(Model model,HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside MA-PreviewRedirect.htm "+UserId);		
		try {
			int countA=1;	
			String MainId=null;
			Map md = model.asMap();
			for (Object modelKey : md.keySet()) {
				System.out.println(" =============================================="+md.get("MilestoneActivityId"));
				System.out.println(modelKey+"==============================================");
				MainId = (String) md.get(modelKey);

			}
			
			
			if(MainId==null) {
				redir.addAttribute("resultfail", "Refresh Not Allowed");
				return "redirect:/MilestoneActivityList.htm";
			}
			Object[] first = new Object[25];
			List<Object[]> list = service.MilestoneActivity(MainId);
			if(list != null && !list.isEmpty()) {
				first = list.get(0);
			}
			req.setAttribute("MilestoneActivity", first);
			List<Object[]>  MilestoneActivityA=service.MilestoneActivityLevel(MainId,"1");
			req.setAttribute("MilestoneActivityA", MilestoneActivityA);
			for(Object[] obj:MilestoneActivityA) {
				List<Object[]>  MilestoneActivityB=service.MilestoneActivityLevel(obj[0].toString(),"2");
				req.setAttribute("MilestoneActivityB"+countA, MilestoneActivityB);
				int countB=1;
				for(Object[] obj1:MilestoneActivityB) {
					List<Object[]>  MilestoneActivityC=service.MilestoneActivityLevel(obj1[0].toString(),"3");
					req.setAttribute("MilestoneActivityC"+countA+countB, MilestoneActivityC);

					int countC=1;
					for(Object[] obj2:MilestoneActivityC) {
						List<Object[]>  MilestoneActivityD=service.MilestoneActivityLevel(obj2[0].toString(),"4");
						req.setAttribute("MilestoneActivityD"+countA+countB+countC, MilestoneActivityD);
						int countD=1;
						for(Object[] obj3:MilestoneActivityD) {
							List<Object[]>  MilestoneActivityE=service.MilestoneActivityLevel(obj3[0].toString(),"5");
							req.setAttribute("MilestoneActivityE"+countA+countB+countC+countD, MilestoneActivityE);
							countD++;
						}
						countC++;
					}
					countB++;
				}
				countA++;
			}
			req.setAttribute("RevisionCount", service.MilestoneRevisionCount(MainId));
			req.setAttribute("ActivityTypeList", service.ActivityTypeList());
			req.setAttribute("EmployeeList", service.EmployeeList());
			req.setAttribute("allLabList", committeservice.AllLabList());
			req.setAttribute("projectDirector", req.getParameter("projectDirector"));
		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside MA-PreviewRedirect.htm "+UserId, e); 
			return "static/Error";
		}

		return "milestone/MilestoneActivityPreview";
	}

	@RequestMapping(value = "MilestoneActivityEdit.htm")
	public String MilestoneActivityEdit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");

		logger.info(new Date() +"Inside MilestoneActivityEdit.htm "+UserId);

		try {
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			MileEditDto mainDto=new MileEditDto();
			mainDto.setMilestoneActivityId(req.getParameter("MilestoneActivityId"));
			mainDto.setRevisionNo(req.getParameter("RevId"));
			mainDto.setActivityId(req.getParameter("ActivityId"));
			mainDto.setActivityType(req.getParameter("ActivityType"));


			req.setAttribute("EmployeeList", service.EmployeeList());
			req.setAttribute("ActivityTypeList", service.ActivityTypeList());
			req.setAttribute("EditData", service.MilestoneActivityEdit(mainDto).get(0));
			req.setAttribute("EditMain", mainDto);

		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside MilestoneActivityEdit.htm "+UserId, e); 
			return "static/Error";

		}

		return "milestone/MilestoneActivityEdit";

	}


	@RequestMapping(value = "MilestoneActivityEditSubmit.htm", method = RequestMethod.POST)
	public String MilestoneActivityEditSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside MilestoneActivityEditSubmit.htm "+UserId);		
		try {

			String chainId = req.getParameter("chainId");
			String targetRowId = req.getParameter("targetRowId");
			
			redir.addFlashAttribute("chainId",chainId);
			redir.addFlashAttribute("targetRowId",targetRowId);

			if(InputValidator.isContainsHTMLTags(req.getParameter("ActivityName"))) {
				redir.addAttribute("ProjectId", req.getParameter("ProjectId"));
				redir.addAttribute("MilestoneActivityId", req.getParameter("MilestoneActivityId"));
				redir.addAttribute("formname", req.getParameter("formname"));
				return  redirectWithError(redir,"MilestoneActivityDetails.htm","ActivityName should not contain HTML elements !");
			}
			
			System.out.println(req.getParameter("Weightage")+"------------");
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			redir.addFlashAttribute("MilestoneActivityId", req.getParameter("MilestoneActivityId"));
			MileEditDto mainDto=new MileEditDto();
			mainDto.setMilestoneActivityId(req.getParameter("MilestoneActivityId"));
			mainDto.setRevisionNo(req.getParameter("RevId"));
			mainDto.setActivityId(req.getParameter("ActivityId"));
			mainDto.setActivityType(req.getParameter("ActivityType"));
			mainDto.setActivityTypeId(req.getParameter("ActivityTypeId"));
			mainDto.setOicEmpId(req.getParameter("EmpId"));
			mainDto.setOicEmpId1(req.getParameter("EmpId1"));
			mainDto.setActivityName(req.getParameter("ActivityName"));
			mainDto.setStartDate(req.getParameter("ValidFrom"));
			mainDto.setEndDate(req.getParameter("ValidTo"));
			mainDto.setWeightage(req.getParameter("Weightage"));
			mainDto.setCreatedBy(UserId);
			
			String activityType = req.getParameter("ActivityType");
			
			System.out.println("Valid To -" +req.getParameter("ValidTo"));
			
			
			if(activityType!=null && !activityType.equalsIgnoreCase("M")) {
				
			  MilestoneActivityLevel level = service.getMilestoneActivityLevelById(req.getParameter("ActivityId"));
			 
			  System.out.println(level.getEndDate());
			  
			  
			  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			  LocalDate date = LocalDate.parse(req.getParameter("ValidTo"), formatter);
			  LocalDate date1 = LocalDate.parse(level.getEndDate().toString());
			  
			  long daysBetween = ChronoUnit.DAYS.between(date1, date); 
			  if(daysBetween!=0) {
				  updateTheLinkMilstoneTimeLine(req.getParameter("ActivityId"),daysBetween, UserId); 
			  }
			  
			}
			
			
	
			int count =service.MilestoneActivityUpdate(mainDto);

			if (count > 0) {
				redir.addAttribute("result", "Milestone Activity Updated  Successfuly.");
			} else {
				redir.addAttribute("resultfail", "Milestone Activity Update Unsuccessful");
			}
			
			redir.addAttribute("projectDirector",req.getParameter("projectDirector"));
			}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside MilestoneActivityEditSubmit.htm "+UserId, e); 
			return "static/Error";
		}


		return "redirect:/MA-PreviewRedirect.htm";
	}

	@Async
	private void updateTheLinkMilstoneTimeLine(String ActivityId,long daysBetween,String UserId) {
		
		try {
			List<String >list = new ArrayList<>();
			List<String>successorIds = getSuccessorIds(ActivityId,list);
			
			System.out.println(ActivityId+"----"+successorIds);
			
			//List<Object[]>successorList = service.getsuccessorList(ActivityId);
			
			
			if(successorIds!=null && successorIds.size()>0)
			{
				for(String s:successorIds) {
					MilestoneActivityLevel level = service.getMilestoneActivityLevelById(s);
					LocalDate endDate = LocalDate.parse(level.getEndDate().toString()).plusDays(daysBetween);
					LocalDate startDate = LocalDate.parse(level.getStartDate().toString()).plusDays(daysBetween);
					
					java.sql.Date sqlEndDate = java.sql.Date.valueOf(endDate);
					java.sql.Date sqlstartDate = java.sql.Date.valueOf(startDate);
					
					level.setEndDate(sqlEndDate);
					level.setStartDate(sqlstartDate);;
					
					level.setModifiedBy(UserId);					
					level.setModifiedDate(LocalDate.now().toString());
					
					service.MilestoneActivityLevelSave(level);
				}
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	@Async
	private List<String> getSuccessorIds(String activityId,List<String> list) {
		
		try {
			List<Object[]>successorList = service.getsuccessorList(activityId);
			
			if(successorList==null||successorList.size()==0) {
				if(!list.contains(activityId)) {
					list.add(activityId);
				}
			}else {
				for(Object[]obj:successorList) {
					list.add(obj[1].toString());
					getSuccessorIds(obj[1].toString(),list);
				}
			}
			
			return list;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	@RequestMapping(value = "MilestoneActivityCompare.htm")
	public String MilestoneActivityCompare(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");

		logger.info(new Date() +"Inside MilestoneActivityCompare.htm "+UserId);

		try {
			int rev=service.MilestoneRevisionCount(req.getParameter("MilestoneActivityId"));
			int rev2=1;
			if(rev>1) {
				rev2=rev-1;
			}
			
			String FirstNo = req.getParameter("FirstNo");
			
			String SecondNo = req.getParameter("SecondNo");
			
			System.out.println(FirstNo+"####"+SecondNo);
			
			if(FirstNo==null) {
				FirstNo= "1";	
			}
			if(SecondNo!=null) {
				rev2 = Integer.parseInt(SecondNo);
			}
			
			
			int countA=1;
			int count1A=1;
			List<Object[]> activityCompareMAin = service.ActivityCompareMAin(req.getParameter("MilestoneActivityId"),FirstNo,"1");
			req.setAttribute("MilestoneActivity", activityCompareMAin!=null && activityCompareMAin.size()>0? activityCompareMAin.get(0): null);
			List<Object[]>  MilestoneActivityA=service.ActivityLevelCompare(req.getParameter("MilestoneActivityId"),FirstNo,"1","1");
			req.setAttribute("MilestoneActivityA", MilestoneActivityA);
			for(Object[] obj:MilestoneActivityA) {
				List<Object[]>  MilestoneActivityB=service.ActivityLevelCompare(obj[0].toString(),FirstNo,"1","2");
				req.setAttribute("MilestoneActivityB"+countA, MilestoneActivityB);
				int countB=1;
				for(Object[] obj1:MilestoneActivityB) {
					List<Object[]>  MilestoneActivityC=service.ActivityLevelCompare(obj1[0].toString(),FirstNo,"1","3");
					req.setAttribute("MilestoneActivityC"+countA+countB, MilestoneActivityC);
					int countC=1;
					for(Object[] obj2:MilestoneActivityC) {
						List<Object[]>  MilestoneActivityD=service.ActivityLevelCompare(obj2[0].toString(),FirstNo,"1","4");
						req.setAttribute("MilestoneActivityD"+countA+countB+countC, MilestoneActivityD);
						int countD=1;
						for(Object[] obj3:MilestoneActivityD) {
							List<Object[]>  MilestoneActivityE=service.ActivityLevelCompare(obj3[0].toString(),FirstNo,"1","5");
							req.setAttribute("MilestoneActivityE"+countA+countB+countC+countD, MilestoneActivityE);
							countD++;
						}
						countC++;
					}
					countB++;
				}
				countA++;
			}	
			List<Object[]> list = service.ActivityCompareMAin(req.getParameter("MilestoneActivityId"),String.valueOf(rev2),"1");
			req.setAttribute("MilestoneActivity1", list != null && !list.isEmpty() ?list.get(0) : null );
			List<Object[]>  MilestoneActivity1A=service.ActivityLevelCompare(req.getParameter("MilestoneActivityId"),String.valueOf(rev2),"1","1");
			req.setAttribute("MilestoneActivity1A", MilestoneActivity1A);
			for(Object[] obj:MilestoneActivity1A) {
				List<Object[]>  MilestoneActivity1B=service.ActivityLevelCompare(obj[0].toString(),String.valueOf(rev2),"1","2");
				req.setAttribute("MilestoneActivity1B"+count1A, MilestoneActivity1B);
				int countB=1;
				for(Object[] obj1:MilestoneActivity1B) {
					List<Object[]>  MilestoneActivity1C=service.ActivityLevelCompare(obj1[0].toString(),String.valueOf(rev2),"1","3");
					req.setAttribute("MilestoneActivity1C"+count1A+countB, MilestoneActivity1C);
					int countC=1;
					for(Object[] obj2:MilestoneActivity1C) {
						List<Object[]>  MilestoneActivity1D=service.ActivityLevelCompare(obj2[0].toString(),String.valueOf(rev2),"1","4");
						req.setAttribute("MilestoneActivity1D"+count1A+countB+countC, MilestoneActivity1D);
						int countD=1;
						for(Object[] obj3:MilestoneActivity1D) {
							List<Object[]>  MilestoneActivity1E=service.ActivityLevelCompare(obj3[0].toString(),String.valueOf(rev2),"1","5");
							req.setAttribute("MilestoneActivity1E"+count1A+countB+countC+countD, MilestoneActivity1E);
							countD++;
						}
						countC++;
					}

					countB++;
				}
				count1A++;
			}	

			req.setAttribute("P", "0");		
			req.setAttribute("Count", service.MilestoneRevisionCount(req.getParameter("MilestoneActivityId")));	
		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside MilestoneActivityCompare.htm "+UserId, e); 
			return "static/Error";

		}

		return "milestone/MilestoneActivityCompare";

	}


	@RequestMapping(value = "MilestoneActivityCompareSubmit.htm")
	public String MilestoneActivityCompareSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");

		logger.info(new Date() +"Inside MilestoneActivityCompareSubmit.htm "+UserId);

		try {
			String rev=req.getParameter("FirstNo");
			String rev2=req.getParameter("SecondNo");
			int countA=1;
			int count1A=1;

			System.out.println(rev);
			System.out.println(rev2);

			req.setAttribute("MilestoneActivity", service.ActivityCompareMAin(req.getParameter("MilestoneActivityId"),rev,rev).get(0));
			List<Object[]>  MilestoneActivityA=service.ActivityLevelCompare(req.getParameter("MilestoneActivityId"),rev,rev,"1");
			req.setAttribute("MilestoneActivityA", MilestoneActivityA);
			for(Object[] obj:MilestoneActivityA) {
				List<Object[]>  MilestoneActivityB=service.ActivityLevelCompare(obj[0].toString(),rev,rev,"2");
				req.setAttribute("MilestoneActivityB"+countA, MilestoneActivityB);
				int countB=1;
				for(Object[] obj1:MilestoneActivityB) {
					List<Object[]>  MilestoneActivityC=service.ActivityLevelCompare(obj1[0].toString(),rev,rev,"3");
					req.setAttribute("MilestoneActivityC"+countA+countB, MilestoneActivityC);
					int countC=1;
					for(Object[] obj2:MilestoneActivityC) {
						List<Object[]>  MilestoneActivityD=service.ActivityLevelCompare(obj2[0].toString(),rev,rev,"4");
						req.setAttribute("MilestoneActivityD"+countA+countB+countC, MilestoneActivityD);
						int countD=1;
						for(Object[] obj3:MilestoneActivityD) {
							List<Object[]>  MilestoneActivityE=service.ActivityLevelCompare(obj3[0].toString(),rev,rev,"5");
							req.setAttribute("MilestoneActivityE"+countA+countB+countC+countD, MilestoneActivityE);
							countD++;
						}
						countC++;
					}
					countB++;
				}
				countA++;
			}	

			req.setAttribute("MilestoneActivity1", service.ActivityCompareMAin(req.getParameter("MilestoneActivityId"),rev2,rev).get(0));
			List<Object[]>  MilestoneActivity1A=service.ActivityLevelCompare(req.getParameter("MilestoneActivityId"),rev2,rev,"1");
			req.setAttribute("MilestoneActivity1A", MilestoneActivity1A);
			for(Object[] obj:MilestoneActivity1A) {
				List<Object[]>  MilestoneActivity1B=service.ActivityLevelCompare(obj[0].toString(),rev2,rev,"2");
				req.setAttribute("MilestoneActivity1B"+count1A, MilestoneActivity1B);
				int countB=1;
				for(Object[] obj1:MilestoneActivity1B) {
					List<Object[]>  MilestoneActivity1C=service.ActivityLevelCompare(obj1[0].toString(),rev2,rev,"3");
					req.setAttribute("MilestoneActivity1C"+count1A+countB, MilestoneActivity1C);
					int countC=1;
					for(Object[] obj2:MilestoneActivity1C) {
						List<Object[]>  MilestoneActivity1D=service.ActivityLevelCompare(obj2[0].toString(),rev2,rev,"4");
						req.setAttribute("MilestoneActivity1D"+count1A+countB+countC, MilestoneActivity1D);
						int countD=1;
						for(Object[] obj3:MilestoneActivity1D) {
							List<Object[]>  MilestoneActivity1E=service.ActivityLevelCompare(obj3[0].toString(),rev2,rev,"5");
							req.setAttribute("MilestoneActivity1E"+count1A+countB+countC+countD, MilestoneActivity1E);
							countD++;
						}
						countC++;
					}
					countB++;
				}
				count1A++;
			}	

			req.setAttribute("P", rev);	
			req.setAttribute("Count", service.MilestoneRevisionCount(req.getParameter("MilestoneActivityId")));	

		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside MilestoneActivityCompareSubmit.htm "+UserId, e); 
			return "static/Error";

		}

		return "milestone/MilestoneActivityCompare";

	}

	@RequestMapping(value = "MilestoneUpdate.htm")
	public String MilestoneUpdate(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");

		logger.info(new Date() +"Inside MilestoneUpdate.htm "+UserId);

		try {
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			req.setAttribute("MilestoneActivityList", service.MilestoneActivityEmpIdList(EmpId));


		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside MilestoneUpdate.htm "+UserId, e); 
			return "static/Error";

		}

		return "milestone/MilestoneOicList";

	}

	@RequestMapping(value = "MilestoneActivityUpdate.htm")
	public String MilestoneActivityUpdate(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");

		logger.info(new Date() +"Inside MilestoneActivityUpdate.htm "+UserId);

		try {
			int rev=service.MilestoneRevisionCount(req.getParameter("MilestoneActivityId"))-1;

			int countA=1; 
			req.setAttribute("MilestoneActivity", service.ActivityCompareMAin(req.getParameter("MilestoneActivityId"),String.valueOf(rev),String.valueOf(rev-1)).get(0));
			List<Object[]>  MilestoneActivityA=service.ActivityLevelCompare(req.getParameter("MilestoneActivityId"),String.valueOf(rev),String.valueOf(rev-1),"1");
			req.setAttribute("MilestoneActivityA", MilestoneActivityA);
			for(Object[] obj:MilestoneActivityA) {
				List<Object[]>  MilestoneActivityB=service.ActivityLevelCompare(obj[0].toString(),String.valueOf(rev),String.valueOf(rev-1),"2");
				req.setAttribute("MilestoneActivityB"+countA, MilestoneActivityB);
				int countB=1;
				for(Object[] obj1:MilestoneActivityB) {
					List<Object[]>  MilestoneActivityC=service.ActivityLevelCompare(obj1[0].toString(),String.valueOf(rev),String.valueOf(rev-1),"3");
					req.setAttribute(countA+"MilestoneActivityC"+countB, MilestoneActivityC);
					countB++;
				}
				countA++;
			}	

			req.setAttribute("projectid", req.getParameter("projectid") );
		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside MilestoneActivityUpdate.htm "+UserId, e); 
			return "static/Error";

		}

		return "milestone/MilestoneActivityUpdate";

	}

	@RequestMapping(value = "M-A-Update.htm")
	public String MileActivityUpdate(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String Labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside M-A-Update.htm "+UserId);

		try {
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			MileEditDto mainDto = new MileEditDto();
			mainDto.setMilestoneActivityId(req.getParameter("MilestoneActivityId"));
			mainDto.setActivityId(req.getParameter("ActivityId"));
			mainDto.setProjectId(req.getParameter("ProjectId"));
			mainDto.setActivityType(req.getParameter("ActivityType"));

			req.setAttribute("StatusList", service.StatusList());
			req.setAttribute("EmpList", service.ProjectEmpList(req.getParameter("ProjectId") ,Labcode));
			req.setAttribute("EditData", service.MilestoneActivityEdit(mainDto).get(0));
			req.setAttribute("EditMain", mainDto);
			req.setAttribute("SubList", service.MilestoneActivitySub(mainDto));
			if(req.getParameter("ActivityType").equals("M")) {
				req.setAttribute("ActionList", service.ActionList("M",req.getParameter("MilestoneActivityId")));
			}else
			{
				req.setAttribute("ActionList", service.ActionList("A",req.getParameter("ActivityId")));	
			}
			req.setAttribute("projectdetails",service.ProjectDetails(req.getParameter("ProjectId")).get(0));
			req.setAttribute("AllLabsList", committeservice.AllLabList());

			req.setAttribute("startdate", req.getParameter("startdate"));

		}catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside M-A-Update.htm "+UserId, e); 
			return "static/Error";
		}

		return "milestone/MileActivityUpdate";

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




	@RequestMapping(value = "M-A-UpdateSubmit.htm", method = RequestMethod.POST)   
	public String MileActivityUpdateSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir,
			@RequestPart("FileAttach") MultipartFile FileAttach)throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside M-A-UpdateSubmit.htm "+UserId);		
		try {

			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String remarks=req.getParameter("Remarks");
			

			
			if(InputValidator.isContainsHTMLTags(remarks)) {
				redir.addAttribute("MilestoneActivityId", req.getParameter("MilestoneActivityId"));
				redir.addAttribute("ActivityId", req.getParameter("ActivityId"));
				redir.addAttribute("ActivityType", req.getParameter("ActivityType"));
				redir.addAttribute("ProjectId", req.getParameter("ProjectId"));
				return redirectWithError(redir, "M-A-Update.htm", "'Remarks' should not contain HTML Tags.!");
			}
			
			  if (!isValidFileType(FileAttach)) {
				    redir.addAttribute("MilestoneActivityId", req.getParameter("MilestoneActivityId"));
					redir.addAttribute("ActivityId", req.getParameter("ActivityId"));
					redir.addAttribute("ActivityType", req.getParameter("ActivityType"));
					redir.addAttribute("ProjectId", req.getParameter("ProjectId"));
		            return redirectWithError(redir, "M-A-AssigneeList.htm",
		                    "Invalid file type. Only PDF or Image files are allowed!");
		        }
			
			
			redir.addFlashAttribute("MilestoneActivityId", req.getParameter("MilestoneActivityId"));
			redir.addFlashAttribute("ActivityId", req.getParameter("ActivityId"));
			redir.addFlashAttribute("ActivityType", req.getParameter("ActivityType"));
			redir.addFlashAttribute("ProjectId", req.getParameter("ProjectId"));
			MileEditDto mainDto=new MileEditDto();
			mainDto.setMilestoneActivityId(req.getParameter("MilestoneActivityId"));
			mainDto.setActivityId(req.getParameter("ActivityId"));
			mainDto.setActivityType(req.getParameter("ActivityType"));
			mainDto.setEndDate(req.getParameter("EndDate"));
			mainDto.setProgressStatus(req.getParameter("Progress"));
			mainDto.setDateOfCompletion(req.getParameter("DOC"));
			mainDto.setFileName(req.getParameter("FileName"));
			mainDto.setFileNamePath(FileAttach.getOriginalFilename());
			mainDto.setFilePath(FileAttach.getBytes());
			mainDto.setStatusRemarks(remarks);
			mainDto.setCreatedBy(UserId);
			mainDto.setProgressDate(req.getParameter("progressDate"));
			//int count =0;
			MilestoneActivityLevel Level = service.getMilestoneActivityLevelById(mainDto.getActivityId());
			
			//System.out.println("linkedLevel----"+Level.toString());
			int count =service.ActivityProgressUpdate(mainDto);
			if(Level.getIsMasterData().equalsIgnoreCase("Y")) {
				updateLinkedMilestoneProgress(Level.getLinkedMilestonId(), mainDto);
			}

			if (count > 0) {
				redir.addAttribute("result", "Milestone Activity  Updated  Successfuly.");
			} else {
				redir.addAttribute("resultfail", "Milestone Activity Update Unsuccessful");
			}

		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside M-A-UpdateSubmit.htm "+UserId, e); 
			return "static/Error";
		}



		return "redirect:/MA-UpdateRedirect.htm";
	}

	
	@Async
	private void updateLinkedMilestoneProgress(Long linkedLevel, MileEditDto mainDto) {
		MilestoneActivityLevel Level = service.getMilestoneActivityLevelById(linkedLevel+"");
		
		Level.setProgressStatus(Integer.parseInt(mainDto.getProgressStatus()));
		
		LocalDate endDate = LocalDate.parse(Level.getEndDate()+"");
		
		LocalDate progressDate = LocalDate.parse(mainDto.getProgressDate().split("-")[2]+"-"+mainDto.getProgressDate().split("-")[1]+"-"+mainDto.getProgressDate().split("-")[0]);
		
		if("100".equalsIgnoreCase(mainDto.getProgressStatus())) {
			//dto.setDateOfCompletion(progressdate);
			if(endDate.isAfter(progressDate)) {
			Level.setActivityStatusId(3);
			}else {
				Level.setActivityStatusId(5);
			}
			}
			else if("0".equalsIgnoreCase(mainDto.getProgressStatus())) {
				Level.setActivityStatusId(1);
			}else {
				if(endDate.isAfter(progressDate)) {
					Level.setActivityStatusId(2);
					}else {
						Level.setActivityStatusId(4);
					}
			}
		
			//long count1= service.MilestoneActivityLevelSave(Level);
			
			try {
				String mainId = service.getMainLevelId(Level.getActivityId());
				mainDto.setMilestoneActivityId(mainId);
				service.updateMilestoneLevelProgress(mainDto);
			} catch (Exception e) {
				
				e.printStackTrace();
			};
		
	}
	
	
	@RequestMapping(value = "MA-UpdateRedirect.htm", method = RequestMethod.GET)
	public String UpdateRedirect(Model model,HttpServletRequest req,HttpSession ses,RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode"); 
		logger.info(new Date() +"Inside MA-UpdateRedirect.htm"+UserId);		
		try {

			String MainId=null;
			String SubId=null;
			String Type=null;
			String Project=null;
			Map md = model.asMap();
			MainId = (String) md.get("MilestoneActivityId");
			SubId = (String) md.get("ActivityId");
			Type = (String) md.get("ActivityType");     
			Project = (String) md.get("ProjectId");   
			if(MainId==null) {
				redir.addAttribute("resultfail", "Refresh Not Allowed");
				return "redirect:/MilestoneUpdate.htm";
			}
			MileEditDto mainDto=new MileEditDto();
			mainDto.setMilestoneActivityId(MainId);
			mainDto.setActivityId(SubId);
			mainDto.setActivityType(Type);
			mainDto.setProjectId(Project);
			req.setAttribute("StatusList", service.StatusList());
			req.setAttribute("EmpList", service.ProjectEmpList(Project , LabCode));
			req.setAttribute("EditData", service.MilestoneActivityEdit(mainDto).get(0));
			req.setAttribute("EditMain", mainDto);
			req.setAttribute("SubList", service.MilestoneActivitySub(mainDto));
			req.setAttribute("ActionList", service.ActionList(Type,SubId));
			req.setAttribute("projectdetails",service.ProjectDetails(Project).get(0));
			req.setAttribute("AllLabsList", committeservice.AllLabList());
		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside MA-UpdateRedirect.htm "+UserId, e); 
			return "static/Error";

		}

		return "milestone/MileActivityUpdate";

	}

	@RequestMapping(value = "MilestoneReports.htm")
	public String MilestoneReports(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");

		logger.info(new Date() +"Inside MilestoneReports.htm "+UserId);

		try {
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			req.setAttribute("MilestoneList", service.MilestoneReportsList(EmpId));

		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside MilestoneReports.htm "+UserId, e); 
			return "static/Error";

		}

		return "milestone/MilestoneReports";

	}

	@RequestMapping(value = "MilestoneTotalWeightage.htm", method = RequestMethod.GET)
	public @ResponseBody String MilestoneTotalWeightage(HttpServletRequest req,HttpSession ses) throws Exception {

		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside MilestoneTotalWeightage.htm "+ UserId);		
		String MilestoneActivityId=req.getParameter("MilestoneActivityId");		
		int totalweightage = service.MilestoneTotalWeightage(MilestoneActivityId);
		Gson json = new Gson();
		return json.toJson(totalweightage);	
	}

	@RequestMapping(value = "ActivityAttachDownload.htm", method = RequestMethod.GET)
	public void ActivityAttachDownload(HttpServletRequest	 req, HttpSession ses, HttpServletResponse res) throws Exception 
	{	 
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside ActivityAttachDownload..htm "+UserId);		
		try { 

			MilestoneActivitySub attachment=service.ActivityAttachmentDownload(req.getParameter("ActivitySubId" ));

			res.setContentType("application/octet-stream");
			res.setHeader("Content-Disposition", String.format("inline; filename=\"" + attachment.getAttachName()));
			res.setContentLength((int)attachment.getAttachFile().length);

			InputStream inputStream = new ByteArrayInputStream(attachment.getAttachFile()); 
			OutputStream outputStream = res.getOutputStream(); 
			FileCopyUtils.copy(inputStream, outputStream);

			inputStream.close(); 
			outputStream.close();
		}
		catch (Exception e) {

			e.printStackTrace();  
			logger.error(new Date() +" Inside ActivityAttachDownload..htm "+UserId, e); 
		}

	}


	@RequestMapping(value = "WeightageSum.htm", method = RequestMethod.GET)
	public @ResponseBody String WeightageSum(HttpServletRequest req, HttpSession ses) throws Exception {
		Gson json = new Gson();
		int ActivitySum=0;
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside WeightageSum.htm "+UserId);		
		try {

			ActivitySum =service.WeightageSum(req.getParameter("Id"),req.getParameter("ActivityId"),req.getParameter("Type"),req.getParameter("LevelId"));

		}
		catch (Exception e) {

			e.printStackTrace();  
			logger.error(new Date() +" Inside WeightageSum.htm "+UserId, e); 
		}

		return json.toJson(ActivitySum);

	}

	@RequestMapping(value = "encryptFile.htm", method = RequestMethod.GET)
	public static void encryptFile(HttpSession ses) throws IOException, NoSuchPaddingException,
	NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
	BadPaddingException, IllegalBlockSizeException {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside encryptFile.htm "+UserId);	
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128);
		SecretKey key = keyGenerator.generateKey();
		String algorithm = "AES/CBC/PKCS5Padding";
		final byte[] bytes = new byte[16]; 
		new SecureRandom().nextBytes(bytes);
		IvParameterSpec iv =new IvParameterSpec(bytes);
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		FileInputStream inputStream = new FileInputStream("D:\\D_test.txt\\");
		FileOutputStream outputStream = new FileOutputStream("D:\\D_testenn.txt\\");
		byte[] buffer = new byte[64];
		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			byte[] output = cipher.update(buffer, 0, bytesRead);
			if (output != null) {
				outputStream.write(output);
			}
		}
		byte[] outputBytes = cipher.doFinal();
		if (outputBytes != null) {
			outputStream.write(outputBytes);
		}
		inputStream.close();
		outputStream.close();
	}
	@RequestMapping(value = "decryptFile.htm", method = RequestMethod.GET)
	public static void decryptFile2(HttpSession ses) throws  Exception{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside decryptFile.htm "+UserId);	
		String sourceFile = "D:\\D_testenn.txt\\";
		FileOutputStream fos = new FileOutputStream("D:\\compressed.zip\\");
		ZipOutputStream zipOut = new ZipOutputStream(fos);
		File fileToZip = new File(sourceFile);
		FileInputStream fis = new FileInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
		zipOut.putNextEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}
		zipOut.close();
		fis.close();
		fos.close();
		String fileZip1 = "D:\\compressed.zip\\";
		File destDir1 = new File("D:\\Test\\");
		byte[] buffer1 = new byte[1024];
		ZipInputStream zis1 = new ZipInputStream(new FileInputStream(fileZip1));
		ZipEntry zipEntry1 = zis1.getNextEntry();
		while (zipEntry1 != null) {
			File newFile = newFile(destDir1, zipEntry1);
			if (zipEntry1.isDirectory()) {
				if (!newFile.isDirectory() && !newFile.mkdirs()) {
					throw new IOException("Failed to create directory " + newFile);
				}
			} else {
				// fix for Windows-created archives
				File parent = newFile.getParentFile();
				if (!parent.isDirectory() && !parent.mkdirs()) {
					throw new IOException("Failed to create directory " + parent);
				}

				// write file content
				FileOutputStream fos1 = new FileOutputStream(newFile);
				int len;
				while ((len = zis1.read(buffer1)) > 0) {
					fos1.write(buffer1, 0, len);
				}
				fos1.close();
			}
			zipEntry1 = zis1.getNextEntry();
		}
		zis1.closeEntry();
		zis1.close();

	}

	public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName());

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
	}

	//@RequestMapping(value = "decryptFile.htm", method = RequestMethod.GET)
	public void decryptFile(HttpSession ses) throws  IOException, NoSuchPaddingException, NoSuchAlgorithmException,
	InvalidAlgorithmParameterException, InvalidKeyException,
	BadPaddingException, IllegalBlockSizeException {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside decryptFile.htm "+UserId);	
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128);
		SecretKey key = keyGenerator.generateKey();
		String algorithm = "AES/CBC/PKCS5Padding";
		final byte[] bytes = new byte[16];
		new SecureRandom().nextBytes(bytes);
		IvParameterSpec iv =new IvParameterSpec(bytes);
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.DECRYPT_MODE, key ,iv);
		FileInputStream inputStream = new FileInputStream("D:\\D_testenn.txt\\");
		FileOutputStream outputStream = new FileOutputStream("D:\\D_testde.txt\\");
		byte[] buffer = new byte[64];
		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			byte[] output = cipher.update(buffer, 0, bytesRead);
			if (output != null) {
				outputStream.write(output);

			}
		}
		byte[] outputBytes = cipher.doFinal(buffer, 0, bytesRead);
		if (outputBytes != null) {
			outputStream.write(outputBytes);
		}
		inputStream.close();
		outputStream.close();
	}


	@RequestMapping(value = "TestingFileRepo.htm")
	public String TestingFileRepo(HttpServletRequest req, HttpSession ses, RedirectAttributes redir, Model model)throws Exception 
	{
		String Logintype= (String)ses.getAttribute("LoginType");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside TestingFileRepo.htm "+UserId);
		try {
			String ProjectId=req.getParameter("projectid");
			Map md = model.asMap();
			if(ProjectId==null) {
				ProjectId= (String) md.get("projectid");
			}
			if(ProjectId==null){
				ProjectId=service.LoginProjectDetailsList(EmpId, Logintype,LabCode).get(0)[0].toString();
			}

			String MainSystemValue= (String) md.get("MainSystemValue");
			String s1= (String) md.get("s1");
			String s2= (String) md.get("s2");
			String s3= (String) md.get("s3");
			String s4= (String) md.get("s4");
			String sublevel= (String) md.get("sublevel");
			String doclev1= (String) md.get("doclev1");
			String doclev2= (String) md.get("doclev2");
			String doclev3= (String) md.get("doclev3");
			if(doclev1==null) {
				doclev1=doclev2=doclev3="0";
			}

			req.setAttribute("MainSystemValue", MainSystemValue);
			req.setAttribute("s1", s1);
			req.setAttribute("s2", s2);
			req.setAttribute("s3", s3);
			req.setAttribute("s4", s4);
			req.setAttribute("sublevel", sublevel);

			req.setAttribute("doclev1", doclev1);
			req.setAttribute("doclev2", doclev2);
			req.setAttribute("doclev3", doclev3);
			req.setAttribute("GlobalFileSize", GlobalFileSize);
			req.setAttribute("ProjectId", ProjectId);
			req.setAttribute("ProjectList", service.LoginProjectDetailsList(EmpId, Logintype,LabCode));
			req.setAttribute("filerepmasterlistall",service.FileRepMasterListAll(ProjectId,LabCode ));
		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside TestingFileRepo.htm "+UserId, e); 
			return "static/Error";
		}
		return "filerepo/FileRepoUpload";
	}

	//	 	@RequestMapping(value = "MainSystemList.htm", method = RequestMethod.GET)
	//		public @ResponseBody String MainSystemList(HttpServletRequest req, HttpSession ses) throws Exception {
	//
	//			String UserId = (String) ses.getAttribute("Username");
	//			List<Object[]> MainSystemList =null;
	//			logger.info(new Date() +"Inside MainSystemList.htm "+UserId);
	//			
	//			try {
	//				MainSystemList=service.MainSystem();
	//								
	//			}catch (Exception e) {
	//				
	//				e.printStackTrace();  logger.error(new Date() +" Inside MainSystemList.htm "+UserId, e); return "static/Error";
	//			}
	//			Gson json = new Gson();
	//			return json.toJson(MainSystemList);
	//
	//		}

	@RequestMapping(value = "SubSystemL1List.htm", method = RequestMethod.GET)
	public @ResponseBody String SubSystemL1List(HttpServletRequest req, HttpSession ses) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		List<Object[]> SubSystemL1List =null;
		logger.info(new Date() +"Inside SubSystemL1List.htm "+UserId);

		try {
			SubSystemL1List=service.MainSystemLevel(req.getParameter("MainSystem"));
		}catch (Exception e) {

			e.printStackTrace();  
			logger.error(new Date() +" Inside SubSystemL1List.htm "+UserId, e); 
		}
		Gson json = new Gson();
		return json.toJson(SubSystemL1List);
	}



	@RequestMapping(value = "DocumentStageList.htm", method = RequestMethod.GET)
	public @ResponseBody String DocumentStageList(HttpServletRequest req, HttpSession ses) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		List<Object[]> DocumentStageList =null;
		logger.info(new Date() +"Inside DocumentStageList.htm "+UserId);

		try {
			DocumentStageList=service.DocumentStageList(req.getParameter("documenttype"),req.getParameter("levelid"));
		}catch (Exception e) {

			e.printStackTrace();  
			logger.error(new Date() +" Inside DocumentStageList.htm "+UserId, e); 
		}
		Gson json = new Gson();
		return json.toJson(DocumentStageList);
	}


	@RequestMapping(value="FileSubAddNew.htm",method = RequestMethod.POST)
	public String FileSubAddNew(HttpServletRequest req, HttpSession ses, RedirectAttributes redir,@RequestPart("FileAttach") MultipartFile FileAttach)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside FileSubAddNew.htm "+UserId);
		try {

			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();				 
			String ProjectId=req.getParameter("projectidvalue");
			long release=Long.parseLong(req.getParameter("Rev"));
			long version=Long.parseLong(req.getParameter("Ver"));
			
			
			  if (!isValidFileType(FileAttach)) {
				  redir.addFlashAttribute("MainSystemValue", req.getParameter("mainsystemval"));
					redir.addFlashAttribute("s1", req.getParameter("s1"));
					redir.addFlashAttribute("s2", req.getParameter("s2"));
					redir.addFlashAttribute("s3", req.getParameter("s3"));
					redir.addFlashAttribute("s4", req.getParameter("s4"));
					redir.addFlashAttribute("sublevel", req.getParameter("sublevel"));
					redir.addFlashAttribute("projectid", req.getParameter("projectidvalue"));
					redir.addFlashAttribute("doclev1", req.getParameter("doclev1"));
					redir.addFlashAttribute("doclev2", req.getParameter("doclev2"));
					redir.addFlashAttribute("doclev3", req.getParameter("documenttitle"));
					
		            return redirectWithError(redir, "TestingFileRepo.htm",
		                    "Invalid file type. Only PDF or Image files are allowed!");
		        }
			

			if(req.getParameter("isnewver").equalsIgnoreCase("N")) 
			{
				release=release+1;
			}
			else if(req.getParameter("isnewver").equalsIgnoreCase("Y")) 
			{
				release=0;
				version=version+1;
			}
			FileRepNew fileRepo=new FileRepNew();
			fileRepo.setProjectId(Long.parseLong(ProjectId));
			fileRepo.setFileRepMasterId(Long.parseLong(req.getParameter("mainsystemval")));
			fileRepo.setCreatedBy(UserId);
			fileRepo.setDocumentId(Long.parseLong(req.getParameter("documenttitle")));
			fileRepo.setReleaseDoc(release);
			fileRepo.setVersionDoc(version);
			List<Object[]> VersionCheckList=new ArrayList<Object[]>();
			int sublevel=Integer.parseInt(req.getParameter("sublevel"));


			fileRepo.setSubL1(Long.parseLong(req.getParameter("s"+sublevel) != "" ? req.getParameter("s"+sublevel): "0" ));
			VersionCheckList=service.VersionCheckList(ProjectId,req.getParameter("s"+sublevel),req.getParameter("documenttitle"));


			FileUploadDto filedto=new FileUploadDto();

			if(VersionCheckList.size()>0) {
				filedto.setFileId(VersionCheckList.get(0)[3].toString());
			}else 
			{
				long result=service.FileSubInsertNew(fileRepo);
				filedto.setFileId(Long.toString(result));
			}

			filedto.setFileName(req.getParameter("FileName"));
			filedto.setProjectId(ProjectId);
			filedto.setIS(FileAttach.getInputStream());
			filedto.setFileNamePath(FileAttach.getOriginalFilename());
			filedto.setPathName(req.getParameter("Path"));
			filedto.setRel(String.valueOf(release));
			filedto.setVer(String.valueOf(version));
			filedto.setUserId(UserId);
			filedto.setDescription(req.getParameter("description"));
			filedto.setSubL1(String.valueOf(sublevel));
			filedto.setDocid(req.getParameter("documenttitle"));
			filedto.setLabCode(LabCode);
			long count=service.FileUploadNew(filedto);

			if (count > 0) {
				redir.addAttribute("result", "Document Added Successfully");
			} else {
				redir.addAttribute("resultfail", "Document Add Unsuccessful");
			}

			redir.addFlashAttribute("MainSystemValue", req.getParameter("mainsystemval"));
			redir.addFlashAttribute("s1", req.getParameter("s1"));
			redir.addFlashAttribute("s2", req.getParameter("s2"));
			redir.addFlashAttribute("s3", req.getParameter("s3"));
			redir.addFlashAttribute("s4", req.getParameter("s4"));
			redir.addFlashAttribute("sublevel", req.getParameter("sublevel"));
			redir.addFlashAttribute("projectid", req.getParameter("projectidvalue"));
			redir.addFlashAttribute("doclev1", req.getParameter("doclev1"));
			redir.addFlashAttribute("doclev2", req.getParameter("doclev2"));
			redir.addFlashAttribute("doclev3", req.getParameter("documenttitle"));
		}
		catch (Exception e) {

			e.printStackTrace();  
			logger.error(new Date() +" Inside FileSubAddNew.htm "+UserId, e); 
			return "static/Error";

		}

		return "redirect:/TestingFileRepo.htm";

	}

	@RequestMapping(value = "VersionCheckList.htm", method = RequestMethod.GET)
	public @ResponseBody String VersionCheckList(HttpServletRequest req, HttpSession ses) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		List<Object[]> VersionCheckList =null;
		logger.info(new Date() +"Inside VersionCheckList.htm "+UserId);

		try {
			VersionCheckList=service.VersionCheckList(req.getParameter("projectid"),req.getParameter("subsysteml1"),req.getParameter("documentName"));

		}catch (Exception e) {

			e.printStackTrace();  
			logger.error(new Date() +" Inside VersionCheckList.htm "+UserId, e);
		}
		Gson json = new Gson();
		return json.toJson(VersionCheckList);

	}







//	@RequestMapping(value = "MilestoneSchedulesList.htm")
//	public String MilestoneSchedulesList(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
//	{
//		String UserId = (String) ses.getAttribute("Username");
//		logger.info(new Date() +"Inside MilestoneSchedulesList.htm "+UserId);		
//		try {
//
//			List<Object[] > ProjectList= service.ProjectList();
//			String ProjectId=req.getParameter("ProjectId");
//
//			if(ProjectId==null) {
//				Object[] FirstProjectId=  ProjectList.get(0);
//				ProjectId= FirstProjectId[0].toString();
//
//			}
//
//			req.setAttribute("ProjectDetails", service.ProjectDetails(ProjectId).get(0));
//			req.setAttribute("MilestoneScheduleList", service.MilestoneScheduleList(ProjectId));
//			req.setAttribute("ProjectList", ProjectList);
//			req.setAttribute("ProjectId", ProjectId);
//
//		}catch (Exception e) {
//			e.printStackTrace(); 
//			logger.error(new Date() +" Inside MilestoneSchedulesList.htm "+UserId, e); 
//			return "static/Error";
//		}
//
//		return "milestone/MilestoneScheduleList";
//	}
//
//	@RequestMapping(value = "MilestoneScheduleAdd.htm")
//	public String MilestoneScheduleAdd(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
//	{
//		String UserId = (String) ses.getAttribute("Username");
//		logger.info(new Date() +"Inside MilestoneScheduleAdd.htm "+UserId);
//		try {
//			req.setAttribute("ActivityTypeList", service.ActivityTypeList());
//			req.setAttribute("ProjectDetails", service.ProjectDetails(req.getParameter("ProjectId")).get(0));
//			req.setAttribute("ProjectId", req.getParameter("ProjectId"));
//		}
//		catch (Exception e) {
//			e.printStackTrace();  
//			logger.error(new Date() +" Inside MilestoneScheduleAdd.htm "+UserId, e); 
//			return "static/Error";
//		}
//		return "milestone/MilestoneScheduleAdd";
//	}
//
//	@RequestMapping(value = "MilestoneScheduleAddSubmit.htm", method = RequestMethod.POST)
//	public String MilestoneScheduleAddSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
//	{			
//		String UserId = (String) ses.getAttribute("Username");
//		logger.info(new Date() +"Inside MilestoneScheduleAddSubmit.htm "+UserId);		
//		try {
//			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
//
//			MilestoneScheduleDto mainDto=new MilestoneScheduleDto();
//			mainDto.setProjectId(req.getParameter("ProjectId"));
//			mainDto.setMilestoneNo(req.getParameter("MilestoneNo"));
//			mainDto.setActivityType(req.getParameter("ActivityType"));
//			mainDto.setActivityName(req.getParameter("ActivityName"));
//			mainDto.setStartDate(req.getParameter("ValidFrom"));
//			mainDto.setEndDate(req.getParameter("ValidTo"));
//			mainDto.setCreatedBy(UserId);
//			long count =service.MilestoneScheduleInsert(mainDto);
//
//			if (count > 0)
//			{ 
//				redir.addAttribute("result", "Milestone Schedule Added Successfully");
//
//			} 
//			else {
//				redir.addAttribute("resultfail", "Milestone Schedule Add Unsuccessful");
//
//				return "redirect:/MilestoneSchedulesList.htm"; }
//
//		}
//
//		catch (Exception e) {
//			e.printStackTrace();  
//			logger.error(new Date() +" Inside MilestoneScheduleAddSubmit.htm "+UserId, e); 
//			return "static/Error";
//
//		}
//
//		return "redirect:/MilestoneSchedulesList.htm";
//
//	}

	@RequestMapping(value = "MilestoneExcelFile.htm", method = {RequestMethod.POST, RequestMethod.GET})
	public void MilestoneExcelFile(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception 
	{

		String Username = (String) ses.getAttribute("Username");

		logger.info(new Date() +"Inside  MilestoneExcelFile.htm "+Username);
		try {
			String name="NoData";
			String header="NoData";
			List<Object[]> bookData=service.MilestoneExcel(req.getParameter("ProjectId"));
			Object[] value=null;
			if(bookData!=null&&bookData.size()>0) {
				name="MilestoneExcelFile"+new SimpleDateFormat("ddMMyyyy").format(new Date())+".csv";
				header="Meeting Alert List";
			}

			Workbook  wb=new XSSFWorkbook();  



			Sheet sheet=wb.createSheet("MilestoneExcelFile"); 
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

			//				  	Center and Border Style
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


			//					Side Borders

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



			//					Content zero
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
			Cell r10cell8 = row10.createCell(7);
			Cell r10cell9 = row10.createCell(8);
			Cell r10cell10 = row10.createCell(9);


			r10cell1.setCellValue("Main Activity Id");
			r10cell1.setCellStyle(wrapname);
			r10cell2.setCellValue("Parent Id");
			r10cell2.setCellStyle(wrapname);
			r10cell3.setCellValue("Activity Name");
			r10cell3.setCellStyle(wrapname);
			r10cell4.setCellValue("Activity Type");
			r10cell4.setCellStyle(wrapname);	       
			r10cell5.setCellValue("Activity Status");
			r10cell5.setCellStyle(wrapname);
			r10cell6.setCellValue("Progress");
			r10cell6.setCellStyle(wrapname);
			r10cell7.setCellValue("Weightage");
			r10cell7.setCellStyle(wrapname);
			r10cell8.setCellValue("Startdate");
			r10cell8.setCellStyle(wrapname);
			r10cell9.setCellValue("PDC");
			r10cell9.setCellStyle(wrapname);
			r10cell10.setCellValue("OIC Name");
			r10cell10.setCellStyle(wrapname);

			if(!bookData.isEmpty()){     

				for(Object[] hlo :bookData) {			        	
					if(hlo[16].toString().equals("0")) {
						service.excelCellValuesSet(sheet, hlo, wrapname, ++rowCount);
						/*----------------------------------------------------------------------------------------*/	
						for(Object[] hlo1 :bookData) {			        	
							if(hlo1[16].toString().equals("1") && hlo1[1].toString().equals(hlo[0].toString())) {
								service.excelCellValuesSet(sheet, hlo1, wrapname, ++rowCount);
								/*----------------------------------------------------------------------------------------*/ 		
								for(Object[] hlo2 :bookData) {			        	
									if(hlo2[16].toString().equals("2") && hlo2[1].toString().equals(hlo1[0].toString())) {
										service.excelCellValuesSet(sheet, hlo2, wrapname, ++rowCount);
										/*----------------------------------------------------------------------------------------*/							        		
										for(Object[] hlo3 :bookData) {			        	
											if(hlo3[16].toString().equals("3") && hlo3[1].toString().equals(hlo2[0].toString())) {
												service.excelCellValuesSet(sheet, hlo3, wrapname, ++rowCount);
												/*----------------------------------------------------------------------------------------*/		
												for(Object[] hlo4 :bookData) {			        	
													if(hlo4[16].toString().equals("4") && hlo4[1].toString().equals(hlo3[0].toString())) {
														service.excelCellValuesSet(sheet, hlo4, wrapname, ++rowCount);
														/*----------------------------------------------------------------------------------------*/
														for(Object[] hlo5 :bookData) {			        	
															if(hlo5[16].toString().equals("5") && hlo5[1].toString().equals(hlo4[0].toString())) {
																service.excelCellValuesSet(sheet, hlo5, wrapname, ++rowCount);
															}
														}
														/*----------------------------------------------------------------------------------------*/
													}
												}
												/*----------------------------------------------------------------------------------------*/
											}
										}
										/*----------------------------------------------------------------------------------------*/							        		
									}
								}
								/*----------------------------------------------------------------------------------------*/	

							}
						}
						/*----------------------------------------------------------------------------------------*/						
					}
				}}




			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.setColumnWidth(2,5000);
			sheet.setColumnWidth(3,5000);
			sheet.setColumnWidth(4,5000);
			sheet.autoSizeColumn(5);
			sheet.autoSizeColumn(6);
			sheet.autoSizeColumn(7);
			sheet.autoSizeColumn(8);
			sheet.setColumnWidth(9,4000);


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
			logger.error(new Date() +"Inside MilestoneExcelFile.htm"+Username, e);

		}
	}	


	@RequestMapping(value = "FileRepMaster.htm")
	public String FileRepMaster(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String Logintype= (String)ses.getAttribute("LoginType");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside FileRepMaster.htm "+UserId);

		try {
			List<Object[] > projectslist= service.LoginProjectDetailsList(EmpId,Logintype,LabCode);				
			String projectid=req.getParameter("projectid");
			Map md = model.asMap();
			if(projectid==null) {		
				md = model.asMap();
				projectid = (String) md.get("projectid");   
			}
			if(projectid==null) {
				try {
					projectid=projectslist.get(0)[0].toString();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}


			String FromName=null;

			FromName = (String) md.get("formname");   
			req.setAttribute("FromName",FromName);
			req.setAttribute("projectslist", projectslist);
			req.setAttribute("projectid",projectid);		
			req.setAttribute("filerepmasterlistall",service.FileRepMasterListAll(projectid,LabCode));
		}		
		catch (Exception e) {

			e.printStackTrace();  
			logger.error(new Date() +" Inside FileRepMaster.htm "+UserId, e); 
			return "static/Error";
		}
		return "filerepo/FileRepMaster";

	}


	@RequestMapping(value="FileRepMasterSubAdd.htm",method = RequestMethod.POST)
	public String FileMasterSubAdd(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside FileMasterSubAdd.htm "+UserId);

		try {
			if(InputValidator.isContainsHTMLTags(req.getParameter("MasterSubName"))) {
				return  redirectWithError(redir,"FileRepMaster.htm","Sub Level Name should not contain HTML elements !");
			}
			FileRepMaster fileRepo=new FileRepMaster();
			fileRepo.setLabCode(LabCode);
			fileRepo.setProjectId(Long.parseLong(req.getParameter("projectid")));
			fileRepo.setLevelName(req.getParameter("MasterSubName").trim());
			fileRepo.setParentLevelId(Long.parseLong(req.getParameter("FileMasterId")));
			fileRepo.setLevelId(Long.parseLong(req.getParameter("LevelId"))+1);
			fileRepo.setCreatedBy(UserId);
			long result=service.FileRepMasterSubInsert(fileRepo);
			if (result > 0) {
				redir.addAttribute("result", "Sub File Added Successfully");
				redir.addAttribute("parentLevelId", req.getParameter("FileMasterId"));
				redir.addAttribute("type", "subLevel");
			} else {
				redir.addAttribute("resultfail", "Sub File Add Unsuccessful");
			}
			redir.addFlashAttribute("formname", req.getParameter("formname"));
			redir.addFlashAttribute("projectid", req.getParameter("projectid"));
		}
		catch (Exception e) {

			e.printStackTrace();  
			logger.error(new Date() +" Inside FileMasterSubAdd.htm "+UserId, e); 
			return "static/Error";

		}
		return "redirect:/FileRepMaster.htm";
	}

	@RequestMapping(value="FileRepMasterAdd.htm",method = RequestMethod.POST)
	public String FileRepMasterAdd(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside FileRepMasterAdd.htm "+UserId);

		try {
			
			if(InputValidator.isContainsHTMLTags(req.getParameter("MasterName"))) {
				return  redirectWithError(redir,"FileRepMaster.htm","Level Name should not contain HTML elements !");
			}
			FileRepMaster fileRepo=new FileRepMaster();
			fileRepo.setLabCode(LabCode);
			fileRepo.setLevelName(req.getParameter("MasterName").trim());
			fileRepo.setProjectId(Long.parseLong(req.getParameter("projectid")));
			fileRepo.setCreatedBy(UserId);
			long result=service.RepMasterInsert(fileRepo);
			if (result > 0) {
				redir.addAttribute("result", "Main File Added Successfully");
			} else {
				redir.addAttribute("resultfail", "Main File Add Unsuccessful");

			}
			redir.addFlashAttribute("formname", req.getParameter("formname"));
			redir.addFlashAttribute("projectid", req.getParameter("projectid"));
		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside FileRepMasterAdd.htm "+UserId, e); 
			return "static/Error";
		}
		return "redirect:/FileRepMaster.htm";
	}


	@RequestMapping(value = "ProjectEmpListFetch.htm", method = RequestMethod.GET)
	public @ResponseBody String ProjectEmpListFetch(HttpServletRequest req,HttpSession ses) throws Exception {

		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside ProjectEmpListFetch.htm "+ UserId);	
		String projectid = req.getParameter("projectid");
		String LabCode =(String) ses.getAttribute("labcode");

		String labCode = req.getParameter("labCode");
		List<Object[]> EmployeeList=null;
		if(Long.parseLong(projectid)>0)
		{
			EmployeeList=service.ProjectEmpList(projectid , labCode!=null && !labCode.isBlank()?labCode:LabCode);
		}else if(Long.parseLong(projectid)==0)
		{
			EmployeeList=service.AllEmpNameDesigList(labCode!=null && !labCode.isBlank()?labCode:LabCode);
		}
		Gson json = new Gson();
		return json.toJson(EmployeeList);	
	}

	@RequestMapping(value = "ProjectEmpListEdit.htm", method = RequestMethod.GET)
	public @ResponseBody String ProjectEmpListEdit(HttpServletRequest req,HttpSession ses) throws Exception {

		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside ProjectEmpListEdit.htm "+ UserId);	
		String projectid=req.getParameter("projectid");
		String labcode = (String) ses.getAttribute("labcode");
		String empid=req.getParameter("EmpId");
		List<Object[]> EmployeeList=null;
		if(Long.parseLong(projectid)>0)
		{
			EmployeeList=service.ProjectEmpListEdit(projectid,empid);
		}else if(Long.parseLong(projectid)==0)
		{
			EmployeeList=service.AllEmpNameDesigList(labcode);
		}

		Gson json = new Gson();
		return json.toJson(EmployeeList);	
	}



	@RequestMapping(value = "AllFilesList.htm", method = RequestMethod.GET)
	public @ResponseBody String AllFilesList(HttpServletRequest req,HttpSession ses) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside AllFilesList.htm "+UserId);
		List<Object[]> DocumentTitleList=new ArrayList<Object[]>();
		try {				
			String ProjectId=req.getParameter("projectid");
			String Sub=req.getParameter("subid");

			System.out.println("Sub   "+Sub);
			DocumentTitleList=service.DocumentTitleList(ProjectId,Sub,LabCode);
		}
		catch (Exception e) {

			e.printStackTrace();  
			logger.error(new Date() +" Inside AllFilesList.htm "+UserId, e); 
		}			
		Gson json = new Gson();
		return json.toJson(DocumentTitleList);	
	}

	@RequestMapping(value = "FileHistoryList.htm", method = RequestMethod.GET)
	public @ResponseBody String FileHistoryList(HttpServletRequest req,HttpSession ses) throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");

		logger.info(new Date() +"Inside FileHistoryList.htm "+UserId);
		List<Object[]> FileHistoryList=new ArrayList<Object[]>();
		try {				
			String filerepid=req.getParameter("filerepid");
			FileHistoryList=service.FileHistoryList(filerepid);
		}
		catch (Exception e) {

			e.printStackTrace();  
			logger.error(new Date() +" Inside FileHistoryList.htm "+UserId, e); 
		}			
		Gson json = new Gson();
		return json.toJson(FileHistoryList);	
	}

	@RequestMapping(value = "FileHistoryListAjax.htm", method = RequestMethod.GET)
	public @ResponseBody String FileHistoryListAjax(HttpServletRequest req,HttpSession ses) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");

		logger.info(new Date() +"Inside FileHistoryListAjax.htm "+UserId);
		List<Object[]> FileHistoryList=new ArrayList<Object[]>();
		try {				
			String ProjectId,MainSystem,SubLevelValue=null;

			ProjectId=req.getParameter("projectid");
			MainSystem=req.getParameter("mainsystemval");				
			SubLevelValue=req.getParameter("sublevel");				
			String Sub = req.getParameter("s"+SubLevelValue);													
			FileHistoryList=service.DocumentTitleList(ProjectId,Sub,LabCode);
		}
		catch (Exception e) 
		{				
			e.printStackTrace();  
			logger.error(new Date() +" Inside FileHistoryListAjax.htm "+UserId, e); 
		}			
		Gson json = new Gson();
		return json.toJson(FileHistoryList);	
	} 


	@RequestMapping(value = "FileDocMasterListAll.htm", method = RequestMethod.GET)
	public @ResponseBody String FileDocMasterListAll(HttpServletRequest req,HttpSession ses) throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside FileDocMasterListAll.htm "+UserId);
		List<Object[]> FileDocMasterListAll=new ArrayList<Object[]>();
		try 
		{	String projectid=req.getParameter("projectid");		
		FileDocMasterListAll= service.FileDocMasterListAll(projectid,LabCode);
		}
		catch (Exception e) 
		{
			e.printStackTrace();  
			logger.error(new Date() +" Inside FileDocMasterListAll.htm "+UserId, e); 
		}			
		Gson json = new Gson();
		return json.toJson(FileDocMasterListAll);	
	}


	@RequestMapping(value="ProjectDocumets.htm")
	public String ProjectDocumets(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String Logintype= (String)ses.getAttribute("LoginType");
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside ProjectDocumets.htm "+UserId);
		try {
			String projectid=req.getParameter("projectid");
			Map md = model.asMap();
			if(projectid==null) {
				projectid= (String) md.get("projectid");
			}
			if(projectid==null){
				projectid="0";
			}
			req.setAttribute("DocumentTypeList",service.DocumentTypeList(projectid, LabCode));
			req.setAttribute("projectid", projectid);
			req.setAttribute("projectslist", service.LoginProjectDetailsList(EmpId, Logintype ,LabCode));
			return "filerepo/ProjectDocument";
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +" Inside ProjectDocumets.htm "+UserId, e); 
			return "static/Error";
		}
	}

	@RequestMapping(value="ProjectDocumetsSubmit.htm", method = RequestMethod.POST )
	public String ProjectDocumetsSubmit(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() +"Inside ProjectDocumetsSubmit.htm "+UserId);
		try {
			String projectid=req.getParameter("projectid");
			String[] documentids=req.getParameterValues("documentid");				

			FileProjectDocDto dto=new FileProjectDocDto();
			dto.setProjectid(projectid);
			dto.setFileUploadMasterId(documentids);
			dto.setCreatedBy(UserId);
			long count = service.ProjectDocumetsAdd(dto);

			if (count > 0) {
				redir.addAttribute("result", "Adding File(s) to Project Successfully");
			} else {
				redir.addAttribute("resultfail", "Adding File(s) to Project Unsuccessful");

			}

			redir.addFlashAttribute("projectid", projectid);
			return "redirect:/ProjectDocumets.htm";
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +" Inside ProjectDocumetsSubmit.htm "+UserId, e); 
			return "static/Error";
		}
	}

	@RequestMapping(value="AddDocAmendment.htm",method = RequestMethod.POST)
	public String AddDocAmmendment(HttpServletRequest req, HttpSession ses, RedirectAttributes redir,@RequestPart("AmendFileAttach") MultipartFile FileAttach)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside AddDocAmendment.htm "+UserId);
		try {
			//		
			
			  if (!isValidFileType(FileAttach)) {
					redir.addFlashAttribute("MainSystemValue", req.getParameter("mainsystemval"));
					redir.addFlashAttribute("s1", req.getParameter("s1"));
					redir.addFlashAttribute("s2", req.getParameter("s2"));
					redir.addFlashAttribute("s3", req.getParameter("s3"));
					redir.addFlashAttribute("s4", req.getParameter("s4"));
					redir.addFlashAttribute("sublevel", req.getParameter("sublevel"));
					redir.addFlashAttribute("projectid", req.getParameter("projectidvalue"));
					redir.addFlashAttribute("doclev1", req.getParameter("doclev1"));
					redir.addFlashAttribute("doclev2", req.getParameter("doclev2"));
					redir.addFlashAttribute("doclev3", req.getParameter("documenttitle"));
					
		            return redirectWithError(redir, "TestingFileRepo.htm",
		                    "Invalid file type. Only PDF or Image files are allowed!");
		        }
			
			
			
			
			
			FileDocAmendmentDto amenddoc=new FileDocAmendmentDto(); 
			amenddoc.setFileRepUploadId(req.getParameter("uploaddocid"));
			amenddoc.setFileName(FileAttach.getOriginalFilename());
			amenddoc.setFilePass(req.getParameter("Path"));
			amenddoc.setCreatedBy(UserId);
			amenddoc.setDescription(req.getParameter("Amenddescription"));
			amenddoc.setProjectId(req.getParameter("projectidvalue"));
			amenddoc.setInStream(FileAttach.getInputStream());
			amenddoc.setLabCode(LabCode);
			long count=service.FileAmmendUploadNew(amenddoc);

			if (count > 0) {
				redir.addAttribute("result", "Document Added Successfully");
			} else {
				redir.addAttribute("resultfail", "Document Add Unsuccessful");
			}

			redir.addFlashAttribute("MainSystemValue", req.getParameter("mainsystemval"));
			redir.addFlashAttribute("s1", req.getParameter("s1"));
			redir.addFlashAttribute("s2", req.getParameter("s2"));
			redir.addFlashAttribute("s3", req.getParameter("s3"));
			redir.addFlashAttribute("s4", req.getParameter("s4"));
			redir.addFlashAttribute("sublevel", req.getParameter("sublevel"));
			redir.addFlashAttribute("projectid", req.getParameter("projectidvalue"));
			redir.addFlashAttribute("doclev1", req.getParameter("doclev1"));
			redir.addFlashAttribute("doclev2", req.getParameter("doclev2"));
			redir.addFlashAttribute("doclev3", req.getParameter("documenttitle"));

			return "redirect:/TestingFileRepo.htm";
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +" Inside AddDocAmendment.htm "+UserId, e); 
			return "static/Error";
		}
	}

	@RequestMapping(value = "AmendmentData.htm", method = RequestMethod.GET)
	public @ResponseBody String AmendmentData(HttpServletRequest req, HttpSession ses) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		List<Object[]> AmendmentData =null;
		logger.info(new Date() +"Inside AmendmentData.htm "+UserId);

		try {
			AmendmentData=service.DocumentAmendment(req.getParameter("DocUploadId"));
			if(AmendmentData.size()==0) {
				AmendmentData=null;
			}
		}catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside AmendmentData.htm "+UserId, e);
		}
		Gson json = new Gson();
		return json.toJson(AmendmentData);

	}

	@RequestMapping(value = "AmendmentFilesZipDownload.htm", method = RequestMethod.POST)
	public void AmendmentFilesZipDownload(Model model,HttpServletRequest req, HttpSession ses,HttpServletResponse res)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside AmendmentFilesZipDownload.htm "+UserId);
		try { 	

			List<String> amendlist=new ArrayList<String>();

			String Id=req.getParameter("Amendfileid");
			String path=req.getServletContext().getRealPath("/view/temp");
			Zipper zip=new Zipper();
			Object[] obj=service.FileDeatils(Id).get(0);                            	
			List<Object[]> amendmentsdata= service.DocumentAmendment(Id);
			for(Object[] amend:amendmentsdata) {
				zip.unpack(FilePath+amend[5].toString()+amend[2].toString()+amend[1].toString()+"-"+amend[4].toString()+".zip",path,amend[6].toString());
				amendlist.add(path+"/"+amend[7]);
			}

			amendlist.forEach(item->{
				if(item instanceof String){
				}
			});           

			res.setContentType("APPLICATION/OCTET-STREAM");
			res.setHeader("Content-Disposition","attachment; filename="+obj[3]+"-Amendments.zip");

			ArrayList<File> files = new ArrayList<File>();
			amendlist.forEach(item->{
				files.add(new File(item));

			});
			ServletOutputStream out = res.getOutputStream();
			ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(out));                

			for (File file : files) {

				zos.putNextEntry(new ZipEntry(file.getName()));

				FileInputStream fis = null;
				try {
					fis = new FileInputStream(file);

				} catch (FileNotFoundException fnfe) 
				{

					zos.write(("ERRORld not find file " + file.getName()).getBytes());
					zos.closeEntry();
					continue;
				}

				BufferedInputStream fif = new BufferedInputStream(fis);

				// Write the contents of the file
				int data = 0;
				while ((data = fif.read()) != -1) 
				{        				
					zos.write(data);
				}
				fif.close();
				zos.closeEntry();
			}

			zos.close();
			for(File file:files)
			{
				file.delete();
			}



		}
		catch (Exception e) {

			e.printStackTrace();  
			logger.error(new Date() +" Inside AmendmentFilesZipDownload.htm "+UserId, e); 

		}

	}


	@RequestMapping(value = "FileUnpack.htm", method = RequestMethod.POST)
	public void FileUnpack(Model model,HttpServletRequest req, HttpSession ses,HttpServletResponse res)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");

		logger.info(new Date() +"Inside FileUnpack.htm "+UserId);

		try {	
			String Id=req.getParameter("FileUploadId");
			Object[] obj=service.FileDeatils(Id).get(0);
			String path=req.getServletContext().getRealPath("/view/temp");
			Zipper zip=new Zipper();
			String tecdata = obj[2].toString().replaceAll("[/\\\\]", ",");
			String[] fileParts = tecdata.split(",");
			String zipName = String.format(obj[3].toString()+obj[7].toString()+"-"+obj[6].toString()+".zip");
			 Path techPath = null;
    		 if(fileParts.length == 4){
    		 	techPath = Paths.get(FilePath, fileParts[0],fileParts[1],fileParts[2],fileParts[3],zipName);
    		 }else{
    			techPath = Paths.get(FilePath, fileParts[0],fileParts[1],fileParts[2],fileParts[3],fileParts[4],zipName);
    		 }
			zip.unpack(techPath.toString(), path,obj[5].toString());

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
			os.close();
			fis.close();
			Path pathOfFile2= Paths.get(path+"/"+obj[4]); 
			Files.delete(pathOfFile2);

		}
		catch (Exception e) {

			e.printStackTrace();  
			logger.error(new Date() +" Inside FileUnpack.htm "+UserId, e); 

		}

	}


	@RequestMapping(value = "AmendFileUnpack.htm", method = RequestMethod.POST)
	public void AmendFileUnpack(Model model,HttpServletRequest req, HttpSession ses,HttpServletResponse res)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");

		logger.info(new Date() +"Inside AmendFileUnpack.htm "+UserId);

		try {	
			String Id=req.getParameter("amendfileid");
			Object[] obj	=service.DocumentAmendmentData(Id);
			String path=req.getServletContext().getRealPath("/view/temp");
			Zipper zip=new Zipper();
			zip.unpack(FilePath+obj[5].toString()+obj[2].toString()+obj[1].toString()+"-"+obj[4].toString()+".zip",path,obj[6].toString());
			res.setContentType("application/pdf");
			res.setHeader("Content-disposition","attachment;filename="+obj[7]); 
			File f=new File(path+"/"+obj[7]);
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

			Path pathOfFile2= Paths.get(path+"/"+obj[7]); 
			Files.delete(pathOfFile2);

		}
		catch (Exception e) {

			e.printStackTrace();  
			logger.error(new Date() +" Inside AmendFileUnpack.htm "+UserId, e); 

		}

	}


	@RequestMapping(value = "DocumentsZipDownload.htm", method = RequestMethod.POST)
	public void DocumentsZipDownload(Model model,HttpServletRequest req, HttpSession ses,HttpServletResponse res)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside DocumentsZipDownload.htm "+UserId);
		try { 	
			String repmasterid=req.getParameter("filerepmasterid");


			List<String> DocsList=new ArrayList<String>();				

			String path=req.getServletContext().getRealPath("/view/temp");

			Object[] repmasterdata=service.RepMasterData(repmasterid);   
			List<Object[]> DocsData= service.RepMasterAllDocLists(repmasterid);


			Zipper zip=new Zipper();

			for(Object[] Doc:DocsData) 
			{   Path uploadPath = Paths.get(FilePath, Doc[2].toString(),(Doc[3].toString()+Doc[7].toString()+"-"+Doc[6].toString()+".zip"));
			zip.unpack(uploadPath.toString(),path,Doc[5].toString());
			File OFile=new File(path+"/"+Doc[4]);
			File NFile=new File(path+"/"+Doc[13]+"-"+Doc[14]+"-"+Doc[4]);
			OFile.renameTo(NFile);
			DocsList.add(path+"/"+Doc[13]+"-"+Doc[14]+"-"+Doc[4]);
			}


			res.setContentType("APPLICATION/OCTET-STREAM");
			res.setHeader("Content-Disposition","attachment; filename="+repmasterdata[2]+"-Docs.zip");

			ArrayList<File> files = new ArrayList<File>();
			DocsList.forEach(item->{
				files.add(new File(item));
			});
			ServletOutputStream out = res.getOutputStream();
			ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(out));                

			for (File file : files) 
			{
				zos.putNextEntry(new ZipEntry(file.getName()));
				FileInputStream fis = null;
				try 
				{
					fis = new FileInputStream(file);
				}
				catch (FileNotFoundException fnfe) 
				{
					zos.write(("ERRORld not find file " + file.getName()).getBytes());
					zos.closeEntry();
					continue;
				}
				BufferedInputStream fif = new BufferedInputStream(fis);
				// Write the contents of the file
				int data = 0;
				while ((data = fif.read()) != -1) 
				{        				
					zos.write(data);
				}
				fif.close();
				zos.closeEntry();
			}

			zos.close();
			for(File file:files)
			{
				file.delete();
			}

		}
		catch (Exception e) {

			e.printStackTrace();  
			logger.error(new Date() +" Inside DocumentsZipDownload.htm "+UserId, e); 

		}

	}

	@RequestMapping(value = "ProjectModuleNameEdit.htm")
	public @ResponseBody ResponseEntity<String> ProjectModuleNameEdit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside ProjectModuleNameEdit.htm "+UserId);		
		try {

			String filerepmasterid = req.getParameter("filerepmasterid");
			String levelname = req.getParameter("levelname").trim();
			String levelType = req.getParameter("levelType");
			if(InputValidator.isContainsHTMLTags(levelname)) {
				return new ResponseEntity<String>("200",HttpStatus.EXPECTATION_FAILED);
			}
			int count = service.fileRepMasterEditSubmit(filerepmasterid, levelname, levelType);
			Gson json = new Gson();
			return ResponseEntity.ok(json.toJson(count));
		}
		catch (Exception e) 
		{
			e.printStackTrace();  
			logger.error(new Date() +" Inside ProjectModuleNameEdit.htm "+UserId, e); 
			Gson json = new Gson();
			return ResponseEntity.ok(json.toJson(0));
		}
	}


	@RequestMapping(value = "FileListInRepo.htm", method = RequestMethod.GET)
	public String FileListInRepo(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside FileListInRepo.htm "+UserId);		
		try {
			req.setAttribute("docmasterlist", service.fileDocMasterList(LabCode));
			return "filerepo/DocumentsList";
		}
		catch (Exception e) 
		{
			e.printStackTrace();  
			logger.error(new Date() +" Inside FileListInRepo.htm "+UserId, e); 
			return "static/Error";
		}
	}

	@RequestMapping(value = "FileLevelSubLevelAdd.htm", method = RequestMethod.POST)
	public String FileLevelSubLevelAdd(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside FileLevelSubLevelAdd.htm "+UserId);		
		try {
			FileDocMaster docmaster = new FileDocMaster();
			docmaster.setLabCode(LabCode);
			docmaster.setLevelId(Integer.parseInt(req.getParameter("levelid")));
			docmaster.setParentLevelId(Integer.parseInt(req.getParameter("parentlevel")));
			docmaster.setLevelName(req.getParameter("levelname").trim());
			docmaster.setCreatedBy(UserId);

			long count = service.FileDocMasterAdd(docmaster);

			if (count > 0) {
				redir.addAttribute("result", "File Level Added Successfully");
			} else {
				redir.addAttribute("resultfail", "File Level Add Unsuccessful");
			}

			return "redirect:/FileListInRepo.htm";
		}
		catch (Exception e) 
		{
			e.printStackTrace();  
			logger.error(new Date() +" Inside FileLevelSubLevelAdd.htm "+UserId, e); 
			return "static/Error";
		}
	}


	@RequestMapping(value = "FileLevelSublevelNameCheck.htm", method = RequestMethod.GET)
	public @ResponseBody String FileLevelSublevelNameCheck(Model model,HttpServletRequest req, HttpSession ses,HttpServletResponse res)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside FileLevelSublevelNameCheck.htm "+UserId);

		String levelname= req.getParameter("levelname");			
		List<FileDocMaster> FileLevelSublevelName = service.FileLevelSublevelNameCheck(levelname.trim(),LabCode);

		return String.valueOf(FileLevelSublevelName.size());
	}

	@RequestMapping(value = "FileNameCheck.htm", method = RequestMethod.GET)
	public @ResponseBody String FileNameCheck(Model model,HttpServletRequest req, HttpSession ses,HttpServletResponse res)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside FileNameCheck.htm "+UserId);

		String levelname= req.getParameter("levelname");
		String shortname= req.getParameter("shortname"); 
		String docid= req.getParameter("docid");
		String parentlevelid= req.getParameter("parentlevelid");

		Object[] FileLevelSublevelName = service.fileNameCheck(levelname.trim(), shortname.trim(), docid.trim(),parentlevelid,LabCode);

		Gson json = new Gson();
		return json.toJson(FileLevelSublevelName);
	}

	@RequestMapping(value = "FileNameAdd.htm", method = RequestMethod.POST)
	public String FileNameAdd(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside FileNameAdd.htm "+UserId);		
		try {
			FileDocMaster docmaster = new FileDocMaster();
			docmaster.setLabCode(LabCode);
			docmaster.setLevelId(3);
			docmaster.setParentLevelId(Integer.parseInt(req.getParameter("parentlevel")));
			docmaster.setLevelName(req.getParameter("levelname").trim());
			docmaster.setDocShortName(req.getParameter("docshortname").trim());
			docmaster.setDocId(Long.parseLong(req.getParameter("docid")));
			docmaster.setCreatedBy(UserId);

			long count = service.FileDocMasterAdd(docmaster);

			if (count > 0) {
				redir.addAttribute("result", "File Name Added Successfully");
			} else {
				redir.addAttribute("resultfail", "File Name Add Unsuccessful");
			}

			return "redirect:/FileListInRepo.htm";
		}
		catch (Exception e) 
		{
			e.printStackTrace();  
			logger.error(new Date() +" Inside FileNameAdd.htm "+UserId, e); 
			return "static/Error";
		}
	}

	@RequestMapping(value = "FileRepMasterListAllAjax.htm", method = RequestMethod.GET)
	public @ResponseBody String FileRepMasterListAllAjax(Model model,HttpServletRequest req, HttpSession ses,HttpServletResponse res)throws Exception 
	{
		try {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside FileRepMasterListAllAjax.htm "+UserId);

		String projectid= req.getParameter("projectid");	
		List<Object[]> FileLevelSublevelName = service.FileRepMasterListAll(projectid,LabCode);

		Gson json = new Gson();
		return json.toJson(FileLevelSublevelName);
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	@RequestMapping(value = "MileRemarkUpdate.htm", method = RequestMethod.POST)
	public String MileRemarkUpdate(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside MileRemarkUpdate.htm "+UserId);		
		try {
			if(InputValidator.isContainsHTMLTags(req.getParameter("Remarks"))) {
				return  redirectWithError(redir,"M-A-AssigneeList.htm","Remarks should not contain HTML elements !");
			}
			MilestoneActivityDto mainDto=new MilestoneActivityDto();
			mainDto.setActivityId(req.getParameter("MileId"));
			mainDto.setStatusRemarks(req.getParameter("Remarks"));
			String financialoutlay = req.getParameter("financialoutlay");
			mainDto.setFinancaOutlay(financialoutlay!=null && !financialoutlay.isEmpty()?Double.parseDouble(financialoutlay):0.00);
			mainDto.setCreatedBy(UserId);
			long count =service.MilestoneRemarkUpdate(mainDto);

			if (count > 0) {

				redir.addAttribute("result", "Milestone Remark Updated Successfuly.");

			} else {
				redir.addAttribute("resultfail", "Milestone Remark Update Unsuccessful");
			}

			redir.addFlashAttribute("ProjectId",req.getParameter("projectid"));
			return "redirect:/M-A-AssigneeList.htm";

		}catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +" Inside MileRemarkUpdate.htm "+UserId, e); 
			return "static/Error";
		}


	}


	@RequestMapping(value = "MainMilestoneDOCUpdate.htm", method = RequestMethod.POST)
	public String MainMilestoneDOCUpdate(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside MainMilestoneDOCUpdate.htm "+UserId);		
		try {
			String MainId = req.getParameter("MSMainid");
			String DateOfCompletion = req.getParameter("DateOfCompletion");
			long count =service.MainMilestoneDOCUpdate(MainId, DateOfCompletion, UserId) ;

			if (count > 0) {
				redir.addAttribute("result", "Milestone Completion Date Updated Successfuly.");
			} else {
				redir.addAttribute("resultfail", "Milestone Completion Date Update Unsuccessful");
			}
			redir.addFlashAttribute("ProjectId",req.getParameter("projectid"));
			return "redirect:/MilestoneActivityList.htm";

		}catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +" Inside MainMilestoneDOCUpdate.htm "+UserId, e); 
			return "static/Error";
		}
	}

	//prakarsh--------------------------------------------------------------------------------
	@RequestMapping(value = "IsActive.htm", method = RequestMethod.GET)
	public @ResponseBody  String SetIsActive(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside IsActive.htm "+UserId);	
		try {
			String project=req.getParameter("Project");
			String FileParentId=req.getParameter("FileParentId");


			String flag=req.getParameter("Flag");

			if(flag.equalsIgnoreCase("B")) {
				service.IsActive(project, FileParentId);
			}else if(flag.equalsIgnoreCase("A")) {
				int count=   service.IsFileInActive(project,FileParentId);
				service.IsActive(project, FileParentId);
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		Gson json = new Gson();
		return json.toJson(1);
	}


	//ajax call controller
	@RequestMapping(value = "IsActive1.htm", method = RequestMethod.GET)
	public @ResponseBody String DocumentLinkList(HttpServletRequest req,HttpSession ses) throws Exception
	{
		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside ExternalEmployeeListInvitations.htm"+ UserId);
		String project=req.getParameter("Project");
		String FileParentId=req.getParameter("FileParentId");

		List<Object[]> DocumentLinkList = new ArrayList<Object[]>();

		DocumentLinkList = service.FileRepUploadId(project,FileParentId);
		req.setAttribute("FileRepUploadId", DocumentLinkList);
		Gson json = new Gson();
		return json.toJson(DocumentLinkList);	
	}

	//document lsit edit controller
	@RequestMapping(value = "DocumentListNameEdit.htm", method = RequestMethod.GET)
	public String DocumentListNameEdit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside ProjectModuleNameEdit.htm "+UserId);		
		try {

			String filerepmasterid = req.getParameter("filerepmasterid");
			System.out.println("filerepmasterid--"+filerepmasterid);
			String levelname = req.getParameter("levelname").trim();
			System.out.println("levelname---"+levelname);
			int count = service.DocumentListNameEdit(filerepmasterid, levelname);

			if (count > 0) {
				redir.addAttribute("result", "Module Name Updated  Successfuly.");
			} else {
				redir.addAttribute("resultfail", "Module Name Update Unsuccessful");
			}

			redir.addFlashAttribute("formname", req.getParameter("formname"));
			redir.addFlashAttribute("projectid", req.getParameter("projectid"));
			return "redirect:/FileListInRepo.htm";
		}
		catch (Exception e) 
		{
			e.printStackTrace();  
			logger.error(new Date() +" Inside ProjectModuleNameEdit.htm "+UserId, e); 
			return "static/Error";
		}
	}

	//filedownlaod avaialable check controller
	@RequestMapping(value = "FileRepoSize.htm", method = RequestMethod.GET)
	public @ResponseBody String FileDownloadAvailable(HttpServletRequest req,HttpSession ses) throws Exception
	{
		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside FileDownloadAvailable.htm"+ UserId);
		String repmasterid=req.getParameter("FileRepMasterId");

		List<Object[]> DocsData= service.RepMasterAllDocLists(repmasterid);

		int DocsDatasize=DocsData.size();
		Gson json = new Gson();
		return json.toJson(DocsDatasize);	
	}	

	/* ************************************************** MS Project *************************************************************** */
	@RequestMapping(value = "MSProjectMilestone.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String MSProjectMilestone(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");
		String Logintype= (String)ses.getAttribute("LoginType");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside MSProjectMilestone.htm "+UserId);

		try {

			String ProjectId=req.getParameter("ProjectId");

			List<Object[] > projlist= service.LoginProjectDetailsList(EmpId,Logintype,LabCode);

			if(projlist.size()==0) 
			{				
				redir.addAttribute("resultfail", "No Project is Assigned to you.");
				return "redirect:/MainDashBoard.htm";
			}

			if(ProjectId==null) {
				try {
					Object[] pro=projlist.get(0);
					ProjectId=pro[0].toString();
				}catch (Exception e) {

				}
			}

			List<Object[]> mstaskList = service.getMsprojectTaskList(ProjectId);

			req.setAttribute("ProjectList",projlist);
			req.setAttribute("ProjectId", ProjectId);
			req.setAttribute("ProjectDetails", service.ProjectDetails(ProjectId).get(0));
			req.setAttribute("mstaskList",mstaskList);

			return "milestone/MSprojectMilestone";
		}catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +" Inside MSProjectMilestone.htm "+UserId, e); 
			return "static/Error";
		}
	}

	@RequestMapping(value = "MSprojectGanttChart.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String MSprojectGanttChart(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside MSprojectGanttChart.htm "+UserId);
		try {
			String ProjectId=req.getParameter("ProjectId");
			List<Object[]>mstaskList = service.getMsprojectTaskList(ProjectId);
			req.setAttribute("ProjectId", ProjectId);
			req.setAttribute("ProjectDetails", service.ProjectDetails(ProjectId).get(0));
			req.setAttribute("mstaskList",mstaskList);
			
			return "milestone/MsProjectGantChart";
		}catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +" Inside MSprojectGanttChart.htm "+UserId, e); 
			return "static/Error";
		}
	}

	@RequestMapping(value = "MSprojectCriticalPath.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String MSprojectCriticalPath(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{

		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside MSprojectCriticalPath.htm "+UserId);
		try {
			String ProjectId=req.getParameter("ProjectId");
			List<Object[]> mstaskList = service.getMsprojectTaskList(ProjectId);
			List<Object[]> msCriticalPathList = mstaskList!=null && mstaskList.size()>0?mstaskList.stream().filter(e -> Integer.parseInt(e[16].toString())==1).collect(Collectors.toList()):new ArrayList<>();
			req.setAttribute("ProjectId", ProjectId);
			req.setAttribute("ProjectDetails", service.ProjectDetails(ProjectId).get(0));
			req.setAttribute("msCriticalPathList", msCriticalPathList);
			
			return "milestone/MsProjectCriticalPath";
		}catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +" Inside MSprojectCriticalPath.htm "+UserId, e); 
			return "static/Error";
		}
	}

	@RequestMapping(value = "MSprojectProcurementList.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String msprojectProcurementList(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside MSprojectProcurementList.htm "+UserId);

		try {
			String ProjectId=req.getParameter("ProjectId");
			List<Object[]> msProjectList = service.getMsprojectTaskList(ProjectId);
			List<Object[]> msProcurementList = msProjectList!=null && msProjectList.size()>0?msProjectList.stream().filter(e -> Integer.parseInt(e[20].toString())==1).collect(Collectors.toList()):new ArrayList<>();
			req.setAttribute("ProjectId", ProjectId);
			req.setAttribute("projectDetails", service.ProjectDetails(ProjectId).get(0));
			req.setAttribute("msProcurementList", msProcurementList);
			
			return "milestone/MSprojectProcurementList";
		}catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +" Inside MSprojectProcurementList.htm "+UserId, e); 
			return "static/Error";
		}
	}
	
	@RequestMapping(value = "MSprojectProcurementStatus.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String msprojectProcurementStatus(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside MSprojectProcurementStatus.htm "+UserId);
		
		try {
			String projectId = req.getParameter("ProjectId");
			req.setAttribute("ProjectId", projectId);
			req.setAttribute("projectDetails", service.ProjectDetails(projectId).get(0));
			req.setAttribute("msProcurementStatusList", service.getMsprojectProcurementStatusList(projectId));
			req.setAttribute("procurementStatusList", printservice.ProcurementStatusList(projectId));
			
			return "milestone/MSprojectProcurementStatus";
		}catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +" Inside MSprojectProcurementStatus.htm "+UserId, e); 
			return "static/Error";
		}
	}

	@RequestMapping(value = "MSprojectProcurementGanttChart.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String msprojectProcurementGanttChart(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside MSprojectProcurementGanttChart.htm "+UserId);
		
		try {
			String ProjectId=req.getParameter("ProjectId");
			List<Object[]> mstaskList = service.getMsprojectTaskList(ProjectId);
			List<Object[]> msProcurementList = mstaskList!=null && mstaskList.size()>0?mstaskList.stream().filter(e -> Integer.parseInt(e[20].toString())==1).collect(Collectors.toList()):new ArrayList<>();
			req.setAttribute("ProjectId", ProjectId);
			req.setAttribute("projectDetails", service.ProjectDetails(ProjectId).get(0));
			req.setAttribute("msProcurementList", msProcurementList);
			
			return "milestone/MSprojectProcurementGanttChart";
		}catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +" Inside MSprojectProcurementGanttChart.htm "+UserId, e); 
			return "static/Error";
		}
	}
	
	@RequestMapping(value = "MSProjectSubLevelsList.htm", method = RequestMethod.GET)
	public @ResponseBody String msProjectSubLevelsList(HttpServletRequest req, HttpSession ses) throws Exception {
	    String UserId = (String) ses.getAttribute("Username");
	    logger.info(new Date() + " Inside MSProjectSubLevelsList.htm" + UserId);
	    Gson json = new Gson();

	    List<Map<String, Object>> responseList = new ArrayList<>();
	    try {
	        String projectId = req.getParameter("projectId");
	        String level = req.getParameter("level");
	        String parentId = req.getParameter("parentId");
	        String listfor = req.getParameter("listfor");

	        List<Object[]> mstaskList = service.getMsprojectTaskList(projectId);

	        if(listfor!=null && listfor.equalsIgnoreCase("P")) {
	        	mstaskList = mstaskList!=null && mstaskList.size()>0?mstaskList.stream().filter(e -> Integer.parseInt(e[20].toString())==1).collect(Collectors.toList()):new ArrayList<>();
	        }
	        // Current level tasks
	        List<Object[]> subTasks = mstaskList.stream().filter(e -> e[8].toString().equals(level) && e[7].toString().equals(parentId)).collect(Collectors.toList());

	        for (Object[] task : subTasks) {
	            String taskId = task[6].toString();
	            int nextLevel = Integer.parseInt(level) + 1;

	            boolean hasChild = mstaskList.stream()
	                .anyMatch(e -> e[8].toString().equals(String.valueOf(nextLevel)) && e[7].toString().equals(taskId));

	            Map<String, Object> map = new HashMap<>();
	            map.put("data", task);
	            map.put("hasChild", hasChild);

	            responseList.add(map);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return json.toJson(responseList);
	}

	/* ************************************************** MS Project End *************************************************************** */

	@PostMapping(value = "DocFileUpload.htm")
	public @ResponseBody ResponseEntity<String> DocFileUploadAjax(Model model,HttpServletRequest req, HttpSession ses,HttpServletResponse res,
			@RequestParam(name = "file", required = false) MultipartFile file,
			@RequestParam("fileRepId") String fileRepId,
			@RequestParam("projectid") String projectid,
			@RequestParam("mainId") String mainId,
			@RequestParam("subId") String subId,
			@RequestParam("docName") String docName,
			@RequestParam("version") String version,
			@RequestParam("release") String release,
			@RequestParam("fileType") String fileType )throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside DocFileUpload.htm "+UserId);
		try {
			
			  if (!isValidFileType(file)) {
				  return new ResponseEntity<String>(HttpStatus.EXPECTATION_FAILED);
		        }
			
			String agendaid = req.getParameter("agendaid");

			FileUploadDto upload = new FileUploadDto();
			upload.setFileId(fileRepId);
			upload.setFileType(fileType);
			upload.setFileRepMasterId(mainId);
			upload.setSubL1(subId);
			upload.setDocumentName(docName);
			upload.setIS(file.getInputStream());
			upload.setFileNamePath(file.getOriginalFilename());
			upload.setProjectId(projectid);
			upload.setVer(version);
			upload.setRel(release);
			upload.setUserId(UserId);
			upload.setLabCode(LabCode);
			upload.setAgendaId(agendaid);

			long result = service.DocFileUploadAjax(upload);

			
			return new ResponseEntity<String>("200",HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +" Inside DocFileUpload.htm "+UserId, e); 
			  return new ResponseEntity<String>(HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(value = "getAttachmentId.htm", method = RequestMethod.GET)
	public @ResponseBody String getAttachmentId(HttpServletRequest req,HttpSession ses) throws Exception {

		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside getAttachmentId.htm "+ UserId);		
		String projectid=req.getParameter("projectid");		
		List<Object[]> attach = service.getAttachmentId(projectid);
		Gson json = new Gson();
		if(attach!=null && attach.size()>0) {
			return json.toJson(attach.get(0));
		}
		return json.toJson("");
	}

	@RequestMapping(value = "submitCheckboxFile.htm", method = RequestMethod.GET)
	public @ResponseBody String submitCheckboxFile(HttpServletRequest req,HttpSession ses) throws Exception {

		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside submitCheckboxFile.htm "+ UserId);		
		String techDataId=req.getParameter("techDataId");	
		String attachid=req.getParameter("attachid");
		String projectid=req.getParameter("projectid");
		long result = service.submitCheckboxFile(UserId,techDataId,attachid,projectid);
		Gson json = new Gson();
		return json.toJson(result);	
	}


	@RequestMapping(value = "ProjectDocsList.htm", method = RequestMethod.GET)
	public @ResponseBody String ProjectDocsList(HttpServletRequest req,HttpSession ses)
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside ProjectDocsList.htm "+UserId);
		try {
			String projectid=req.getParameter("projectid");		
			List<Object[]> projectdoclist = service.FileDocMasterListAll(projectid,LabCode);
			Gson json = new Gson();
			if(projectdoclist!=null && projectdoclist.size()>0) {
				return json.toJson(projectdoclist);
			}
			return json.toJson("");
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +" Inside ProjectDocsList.htm "+UserId, e); 
		}
		return "";
	}

	
	@RequestMapping(value="MilestoneActivityMilNoUpdate.htm",method=RequestMethod.POST)
	public String milestoneActivityMilNoUpdate(HttpServletRequest req,HttpServletResponse res, RedirectAttributes redir, HttpSession ses)throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside MilestoneActivityMilNoUpdate.htm "+UserId);
		try
		{
			String[] newslno = req.getParameterValues("newslno");
			String[] milestoneActivityId = req.getParameterValues("milestoneActivityId");
			System.out.println("newslno: "+newslno);
			System.out.println("milestoneActivityId: "+milestoneActivityId);
			Set<String> s = new HashSet<String>(Arrays.asList(newslno));
			if (s.size() == newslno.length) 
			{
				service.mileStoneSerialNoUpdate(newslno, milestoneActivityId);
				redir.addAttribute("result", "Milestone Serial No Updated Successfully");
			} else {
				redir.addAttribute("resultfail", "Milestone Serial No Update UnSuccessfull");
			}	
			redir.addAttribute("ProjectId", req.getParameter("projectId"));
			return "redirect:/MilestoneActivityList.htm";
		}catch (Exception e){
			e.printStackTrace(); 
			logger.error(new Date() +"Inside MilestoneActivityMilNoUpdate.htm "+UserId,e);
			return "static/Error";
		}
	}
	
	@RequestMapping(value="MilestoneActivityProgressDetails.htm", method= {RequestMethod.GET})
	public @ResponseBody String milestoneActivityProgressDetails(HttpSession ses, HttpServletRequest req) throws Exception {
		
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside MilestoneActivityProgressDetails.htm "+UserId);
		Gson json = new Gson();
		List<Object[]> subList = new ArrayList<>();
		try
		{	 
			MileEditDto mainDto = new MileEditDto();
			mainDto.setMilestoneActivityId(req.getParameter("MilestoneActivityId"));
			mainDto.setActivityId(req.getParameter("ActivityId"));
			mainDto.setProjectId(req.getParameter("ProjectId"));
			mainDto.setActivityType(req.getParameter("ActivityType"));

			subList = service.MilestoneActivitySub(mainDto);

		}catch (Exception e) {
			logger.error(new Date() +"Inside MilestoneActivityProgressDetails.htm "+UserId ,e);
			e.printStackTrace(); 
		}
		  
		 return json.toJson(subList);    
	}
	private String redirectWithError(RedirectAttributes redir,String redirURL, String message) {
	    redir.addAttribute("resultfail", message);
	    return "redirect:/"+redirURL;
	}
	
	
	@RequestMapping(value = "ResourceGanttChart.htm", method= {RequestMethod.GET, RequestMethod.POST})
	public String ResourceGanntChart(HttpServletRequest req,HttpSession ses) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String EmpId = ((Long)ses.getAttribute("EmpId")).toString();
		String LoginType = (String)ses.getAttribute("LoginType");
		String labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside ResourceGanttChart.htm "+UserId);
		try {
			String tabNo = req.getParameter("tabNo");
			String activityType = req.getParameter("activityType");
			String empId = req.getParameter("empId");
			empId = empId == null?EmpId : empId;
			String finalEmpId = empId;
			
			List<Object[]> roleWiseEmployeeList = timesheetservice.getRoleWiseEmployeeList(labcode, LoginType, empId);
			List<Object[]> mainList = service.getAllMilestoneActivityList();
			List<Object[]> subList = service.getAllMilestoneActivityLevelList();
			
			List<Object[]> totalAssignedMainList = new ArrayList<>();
			List<Object[]> totalAssignedSubList = new ArrayList<>();
			
			if (mainList != null && !mainList.isEmpty()) {
				totalAssignedMainList.addAll(mainList.stream()
						.filter(e -> (e[10].toString().equalsIgnoreCase(finalEmpId) || e[11].toString().equalsIgnoreCase(finalEmpId)))
						.collect(Collectors.toList()));
				
				Map<String, List<Object[]>> groupedByParentIdAndLevel = subList.stream().collect(Collectors.groupingBy(e -> e[1].toString() + "_" + e[2].toString()));
				
				for(Object[] objmain : mainList ) {

					//List<Object[]> MilestoneActivityA = subList.stream().filter(e -> e[1].toString().equalsIgnoreCase(objmain[0].toString()) && Integer.parseInt(e[2].toString())==1).collect(Collectors.toList());
					List<Object[]> MilestoneActivityA = groupedByParentIdAndLevel.getOrDefault(objmain[0].toString() + "_1", Collections.emptyList());
					int countA = 1;
					for(Object[] obj:MilestoneActivityA) {
						
						if (obj[10].toString().equalsIgnoreCase(finalEmpId) || obj[11].toString().equalsIgnoreCase(finalEmpId)) {
					        Object[] newRow = Arrays.copyOf(obj, obj.length + 4);
					        newRow[obj.length] = objmain[0].toString();
					        newRow[obj.length + 1] = "A" + countA;
					        newRow[obj.length + 2] = objmain[2];
					        newRow[obj.length + 3] = objmain[1].toString();
					        totalAssignedSubList.add(newRow);
					    }
						
						//List<Object[]> MilestoneActivityB = subList.stream().filter(e -> e[1].toString().equalsIgnoreCase(obj[0].toString()) && Integer.parseInt(e[2].toString())==2).collect(Collectors.toList());
						List<Object[]> MilestoneActivityB = groupedByParentIdAndLevel.getOrDefault(obj[0].toString() + "_2", Collections.emptyList());
						int countB = 1;
						for(Object[] obj1:MilestoneActivityB) {
							
							if (obj1[10].toString().equalsIgnoreCase(finalEmpId) || obj1[11].toString().equalsIgnoreCase(finalEmpId)) {
						        Object[] newRow = Arrays.copyOf(obj1, obj1.length + 4);
						        newRow[obj1.length] = objmain[0].toString();
						        newRow[obj1.length + 1] = "A"+countA+"-B"+countB;
						        newRow[obj1.length + 2] = objmain[2];
						        newRow[obj1.length + 3] = objmain[1].toString();
						        totalAssignedSubList.add(newRow);
						    }
							
							//List<Object[]> MilestoneActivityC = subList.stream().filter(e -> e[1].toString().equalsIgnoreCase(obj1[0].toString()) && Integer.parseInt(e[2].toString())==3).collect(Collectors.toList());
							List<Object[]> MilestoneActivityC = groupedByParentIdAndLevel.getOrDefault(obj1[0].toString() + "_3", Collections.emptyList());
							int countC = 1;
							for(Object[] obj2:MilestoneActivityC) {

								if (obj2[10].toString().equalsIgnoreCase(finalEmpId) || obj2[11].toString().equalsIgnoreCase(finalEmpId)) {
							        Object[] newRow = Arrays.copyOf(obj2, obj2.length + 4);
							        newRow[obj2.length] = objmain[0].toString();
							        newRow[obj2.length + 1] = "A"+countA+"-B"+countB+"-C"+countC;
							        newRow[obj2.length + 2] = objmain[2];
							        newRow[obj2.length + 3] = objmain[1].toString();
							        totalAssignedSubList.add(newRow);
							    }
								
								//List<Object[]> MilestoneActivityD = subList.stream().filter(e -> e[1].toString().equalsIgnoreCase(obj2[0].toString()) && Integer.parseInt(e[2].toString())==4).collect(Collectors.toList());
								List<Object[]> MilestoneActivityD = groupedByParentIdAndLevel.getOrDefault(obj2[0].toString() + "_4", Collections.emptyList());
								int countD = 1;
								for(Object[] obj3:MilestoneActivityD) {
									
									if (obj3[10].toString().equalsIgnoreCase(finalEmpId) || obj3[11].toString().equalsIgnoreCase(finalEmpId)) {
								        Object[] newRow = Arrays.copyOf(obj3, obj3.length + 4);
								        newRow[obj3.length] = objmain[0].toString();
								        newRow[obj3.length + 1] = "A"+countA+"-B"+countB+"-C"+countC+"-D"+countD;
								        newRow[obj3.length + 2] = objmain[2];
								        newRow[obj3.length + 3] = objmain[1].toString();
								        totalAssignedSubList.add(newRow);
								    }
									
									//List<Object[]> MilestoneActivityE = subList.stream().filter(e -> e[1].toString().equalsIgnoreCase(obj3[0].toString()) && Integer.parseInt(e[2].toString())==5).collect(Collectors.toList());
									List<Object[]> MilestoneActivityE = groupedByParentIdAndLevel.getOrDefault(obj3[0].toString() + "_5", Collections.emptyList());
									int countE = 1;
									for(Object[] obj4:MilestoneActivityE) {
										
										if (obj4[10].toString().equalsIgnoreCase(finalEmpId) || obj4[11].toString().equalsIgnoreCase(finalEmpId)) {
									        Object[] newRow = Arrays.copyOf(obj4, obj4.length + 4);
									        newRow[obj4.length] = objmain[0].toString();
									        newRow[obj4.length + 1] = "A"+countA+"-B"+countB+"-C"+countC+"-D"+countD+"-E"+countE;
									        newRow[obj4.length + 2] = objmain[2];
									        newRow[obj4.length + 3] = objmain[1].toString();
									        totalAssignedSubList.add(newRow);
									    }
										countE++;
									}
									countD++;
								}
								countC++;
							}
							countB++;
						}
						countA++;
					}
				}
			}
			
			req.setAttribute("tabNo", tabNo!=null?tabNo:"1");
			req.setAttribute("activityType", activityType!=null?activityType:"A");
			req.setAttribute("empId", empId);
			req.setAttribute("roleWiseEmployeeList", roleWiseEmployeeList);
			req.setAttribute("totalAssignedMainList", totalAssignedMainList);
			req.setAttribute("totalAssignedSubList", totalAssignedSubList);
			req.setAttribute("actionAssigneeList", service.actionAssigneeList(empId));
			req.setAttribute("projectList", service.ProjectList());
			
			return "milestone/ResourceGanttChart";
		}catch (Exception e) {
			logger.error(new Date() +"Inside ResourceGanttChart.htm "+UserId ,e);
			e.printStackTrace(); 
			return "static/Error";
		}
	}

	@RequestMapping(value = "getOldFileDocNames.htm", method = RequestMethod.GET)
	public @ResponseBody String getOldFileDocNames(HttpServletRequest req,HttpSession ses) throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside getOldFileDocNames"+UserId);
		List<Object[]> fileDocNameList=null;
		Gson json = new Gson();
		try {				
			String projectId=req.getParameter("projectId");
			String fileId=req.getParameter("fileId");
			String fileType=req.getParameter("fileType");
			fileDocNameList=service.getOldFileDocNames(projectId,fileType,fileId);
		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside getOldFileDocNames"+UserId, e);
		}
		return json.toJson(fileDocNameList);	
	}
	
	@RequestMapping(value = "checkFolderNames.htm", method = RequestMethod.GET)
	public @ResponseBody String getFileRepMasterNames(HttpServletRequest req,HttpSession ses) throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside getFileRepMasterNames"+UserId);
		int count = 0;
		Gson json = new Gson();
		try {				
			
			String projectId=req.getParameter("projectId");
			String fileId=req.getParameter("fileId");
			String fileType=req.getParameter("fileType");
			String fileName=req.getParameter("fileName");
			count=service.getFileRepMasterNames(projectId,fileType,fileId,fileName);
		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside getFileRepMasterNames"+UserId, e);
		}
		return json.toJson(count);	
	}
	
	
	@RequestMapping(value = "getDocVersionList.htm", method = RequestMethod.GET)
	public @ResponseBody String getFileRepDocList(HttpServletRequest req,HttpSession ses) throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside getFileRepDocList"+UserId);
		List<Object[]> fileDocList= new ArrayList<>();
		Gson json = new Gson();
		try {				
			String fileRepId=req.getParameter("fileRepId");
			fileDocList=service.FileRepDocsList(fileRepId);
		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside getFileRepDocList"+UserId, e);
		}
		return json.toJson(fileDocList);	
	}
	
	@RequestMapping(value = "uploadFileData.htm", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> uploadFileData(HttpServletRequest req,HttpSession ses,
			@RequestParam(name = "fileAttach", required = false) MultipartFile file,
			@RequestParam("docName") String docName,
			@RequestParam("fileRepId") String fileRepId,
			@RequestParam("reportType") String reportType,
			@RequestParam("projectId") String projectId,
			@RequestParam("mainLevelId") String mainLevelId,
			@RequestParam("subLevelId") String subLevelId,
			@RequestParam("isnewversion") String isnewversion,
        	@RequestParam("fileType") String fileType,RedirectAttributes redir) throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside getOldFileDocNames"+UserId);
		long result = 0l;
		Gson json = new Gson();
		try {
			if(InputValidator.isContainsHTMLTags(docName)) {
				return new ResponseEntity<String>("200",HttpStatus.EXPECTATION_FAILED);	
			}
			
			
			 // 🔹 Check if file is missing
	        if (file == null || file.isEmpty()) {
	            return new ResponseEntity<String>(HttpStatus.EXPECTATION_FAILED);
	        }
	        
	        String originalFilename = file.getOriginalFilename();
	        
	     // Extract extension in lowercase
		    String extension = FilenameUtils.getExtension(originalFilename).toLowerCase();

	        // 🔹 Validate: Only PDF allowed
	        String contentType = file.getContentType();
	        if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")) {
	        	 return new ResponseEntity<String>(HttpStatus.EXPECTATION_FAILED);
	        }
	        
	        System.out.println("extension--"+extension);

	        if (!extension.equalsIgnoreCase("pdf")) {
	        	 return new ResponseEntity<String>(HttpStatus.EXPECTATION_FAILED);
	        }
	        String code = null;
	        if(labcode.equalsIgnoreCase("PGAD")) {
		        String reportTypeName = reportType!=null && reportType.length()>0 ? reportType : " - ";
		        
		        Object[] projectDetails = service.getprojectDetails(projectId);
		        String projectCode = projectDetails != null && projectDetails[3] != null
		                ? projectDetails[3].toString()
		                : "";
	
		        int num = 1;
		        String count = String.format("%04d", num);
		        int currYear = LocalDate.now().getYear();
	
		        Object[] fileRepUploadDetail = service.getFileRepUploadDetails(projectId);
	
		        if (fileRepUploadDetail != null && fileRepUploadDetail[3] != null) {
	
		            String lastCode = fileRepUploadDetail[3].toString();
		            String[] codes = lastCode.split("/");
	
		            if (codes.length >= 6) {
	
		                int lastUpdatedNum = Integer.parseInt(codes[4]);
		                int lastUpdatedYear = Integer.parseInt(codes[5]);
	
		                if (lastUpdatedYear == currYear) {
		                    count = String.format("%04d", lastUpdatedNum + 1);
		                } else {
		                    count = String.format("%04d", 1);
		                }
		            }
		        }
		        code = "DRDO/" + labcode + "/" + projectCode + "/" + reportTypeName + "/" + count + "/" + currYear;
	        } 

			FileUploadDto upload = new FileUploadDto();
			upload.setFileId(fileRepId);
			upload.setFileRepMasterId(mainLevelId);
			upload.setSubL1(subLevelId);
			upload.setDocumentName(docName);
			upload.setIS(file.getInputStream());
			upload.setFileNamePath(file.getOriginalFilename());
			upload.setProjectId(projectId);
			upload.setUserId(UserId);
			upload.setLabCode(labcode);
			upload.setIsNewVersion(isnewversion);
			upload.setReportType(code);
			
			result=service.uploadFileData(upload,fileType);
		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside getOldFileDocNames"+UserId, e);
		}

		return new ResponseEntity<String>("200", HttpStatus.OK);

	}
	
	@RequestMapping(value = "fileDownload.htm/{id}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Resource> downloadPdfFromPasswordZip(
			 @PathVariable("id") Long id,
		     @RequestParam("fileType") String fileType,
	        HttpServletRequest req,HttpSession session) throws Exception {

		String labCode = (String) session.getAttribute("labcode");
	    Optional<FileRepUploadNew> optionalFile = service.getFileById(id);
	    if (!optionalFile.isPresent()) {
	        return ResponseEntity.notFound().build();
	    }
	    
	    FileRepUploadNew fileEntity = optionalFile.get();
	    if (fileEntity.getFilePath() == null || fileEntity.getFileName() == null) {
	        return ResponseEntity.badRequest().build();
	    }

	    try {
	        Zipper zip = new Zipper();

	        // Build zip file path
	        String[] parts = fileEntity.getFilePath().replaceAll("[/\\\\]", ",").split(",");
	        String zipName = String.format("%s%s-%s.zip",
	                fileEntity.getFileNameUi(),
	                fileEntity.getVersionDoc(),
	                fileEntity.getReleaseDoc());

	        Path zipFilePath;
	        if ("mainLevel".equalsIgnoreCase(fileType)) {
	            zipFilePath = Paths.get(FilePath, parts[0], parts[1], parts[2], parts[3], zipName);
	        } else {
	            zipFilePath = Paths.get(FilePath, parts[0], parts[1], parts[2], parts[3], parts[4], zipName);
	        }

	        if (!Files.exists(zipFilePath)) {
	            return ResponseEntity.notFound().build();
	        }

	        // Extract zip to a temp folder
	        String tempDirPath = req.getServletContext().getRealPath("/view/temp/" + UUID.randomUUID());
	        File tempDir = new File(tempDirPath);
	        tempDir.mkdirs();

	        zip.unpack(zipFilePath.toString(), tempDirPath, fileEntity.getFilePass());

	        // Find the single PDF inside
	        File[] pdfFiles = tempDir.listFiles((d, name) -> name.toLowerCase().endsWith(".pdf"));
	        if (pdfFiles == null || pdfFiles.length == 0) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	        }

	        String code = "";
	        if(fileEntity.getReportType()!=null && fileEntity.getReportType().length()>0) {
	        	code = fileEntity.getReportType();
	        }
	        File pdfFile = pdfFiles[0];
	        File modifiedFile = pdfFile;
	        if(labCode.equalsIgnoreCase("PGAD")) {
	        	modifiedFile = addHeaderForPdf(pdfFile,code);
	        }
	        Resource resource = new UrlResource(modifiedFile.toURI());

	        return ResponseEntity.ok()
	                .contentType(MediaType.APPLICATION_PDF)
	                .header(HttpHeaders.CONTENT_DISPOSITION,
	                        "inline; filename=\"" + pdfFile.getName() + "\"")
	                .body(resource);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
	
	private File addHeaderForPdf(File inputPdf, String headerText) throws Exception {

	    String path = inputPdf.getParent();
	    String filename = inputPdf.getName().replace(".pdf", "");

	    PdfDocument pdfDocMain = new PdfDocument(
	            new PdfReader(inputPdf.getAbsolutePath()),
	            new PdfWriter(path + File.separator + filename + "_temp.pdf")
	    );

	    Document docMain = new Document(pdfDocMain, PageSize.A4);
	    docMain.setMargins(50, 50, 50, 50);

	    int pages = pdfDocMain.getNumberOfPages();

	    for (int i = 1; i <= pages; i++) {

	        PdfPage pageMain = pdfDocMain.getPage(i);
	        Rectangle pageSizeMain = pageMain.getPageSize();

	        Paragraph header = new Paragraph(headerText)
	                .setFontSize(10)
	                .setBold();
	                //.setTextAlignment(TextAlignment.CENTER);

	        float marginLeft = 36;   
	        float marginTop  = 15;   

	        float x = marginLeft;
	        float y = pageSizeMain.getHeight() - marginTop;

	        docMain.showTextAligned(
	                header,
	                x,
	                y,
	                i,  
	                TextAlignment.LEFT,
	                com.itextpdf.layout.properties.VerticalAlignment.TOP,
	                0f
	        );

	    }

	    docMain.close();
	    pdfDocMain.close();

	    return new File(path + File.separator + filename + "_temp.pdf");
	}


	@RequestMapping(value = "removeFileAttachment.htm", method = RequestMethod.POST)
	public @ResponseBody String removeFileAttachment(HttpServletRequest req, HttpSession ses,HttpServletResponse res,
			@RequestParam("techDataId") String techDataId,
			@RequestParam("techAttachId") String techAttachId,
			@RequestParam("projectId") String projectId,
			@RequestParam(value="relativePoints",required =false) String relativePoints)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside removeFileAttachment.htm "+UserId);
		try {

			long result = service.removeFileAttachment(projectId, techDataId, techAttachId, UserId, relativePoints);

			Gson json = new Gson();
			return json.toJson(result);

		} catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +" Inside removeFileAttachment.htm "+UserId, e); 
			return "static/Error";
		}
	}
	
	@RequestMapping(value = "getProjectMilestones.htm", method = RequestMethod.GET)
	public @ResponseBody String getProjectMilestones(HttpServletRequest req, HttpSession ses,HttpServletResponse res,
			@RequestParam("projectid") String projectid)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside removeFileAttachment.htm "+UserId);
		try {
			List<Object[]> main=service.MilestoneActivityList(projectid);
			
			
			
			List<Object[]> totalAssignedSubList = new ArrayList<>();
			Map<String , Object[]>map = new LinkedHashMap<>();
			if(projectid!=null) {int count=1;
				for(Object[] objmain : main ) {
					
					List<Object[]>  MilestoneActivityA = service.MilestoneActivityLevel(objmain[0].toString(),"1");
					int countA=1;
					for(Object[] obj:MilestoneActivityA) {
						totalAssignedSubList.add(obj);
						map.put("M"+count+"-A"+countA+"/"+obj[0], obj);
						List<Object[]>  MilestoneActivityB=service.MilestoneActivityLevel(obj[0].toString(),"2");
						int countb=1;
						for(Object[] obj1:MilestoneActivityB) {
							totalAssignedSubList.add(obj1);
							map.put("M"+count+"-A"+countA+"-B"+countb+"/"+obj1[0], obj1);
							List<Object[]>  MilestoneActivityC=service.MilestoneActivityLevel(obj1[0].toString(),"3");
							int countc=1;
							for(Object[] obj2:MilestoneActivityC) {
								totalAssignedSubList.add(obj2);
								map.put("M"+count+"-A"+countA+"-B"+countb+"-C"+countc+"/"+obj2[0], obj2);
								List<Object[]>  MilestoneActivityD=service.MilestoneActivityLevel(obj2[0].toString(),"4");
								int countD=1;
								for(Object[] obj3:MilestoneActivityD) {
									totalAssignedSubList.add(obj3);
									map.put("M"+count+"-A"+countA+"-B"+countb+"-C"+countc+"-D"+countD+"/"+obj3[0], obj3);
									List<Object[]>  MilestoneActivityE=service.MilestoneActivityLevel(obj3[0].toString(),"5");
									int countE=1;
									for(Object[] obj4:MilestoneActivityE) {
										totalAssignedSubList.add(obj4);
										map.put("M"+count+"-A"+countA+"-B"+countb+"-C"+countc+"-D"+countD+"-E"+countE+"/"+obj4[0], obj4);
										countE++;
									}
									countD++;
									}
								countc++;
								}
							countb++;
							}
						countA++;
						}
					count++;
					}
				}
			
			
			
			Gson json = new Gson();
			return json.toJson(map);
			
		} catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +" Inside getProjectMilestones.htm "+UserId, e); 
			return "static/Error";
		}
	}

	@RequestMapping(value = "updateMilestonSuperSeding.htm", method = RequestMethod.GET)
	public @ResponseBody String updateMilestonSuperSeding(HttpServletRequest req, HttpSession ses,HttpServletResponse res)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside removeFileAttachment.htm "+UserId);

		Gson json = new Gson();

		try {
			
			String milesMainId = req.getParameter("milesMainId");
			String mileIdLink = req.getParameter("mileIdLink");
			String isMasterData = req.getParameter("isMasterData");
			
			MilestoneActivityLevel level1= service.getMilestoneActivityLevelById(milesMainId);
			MilestoneActivityLevel level2= service.getMilestoneActivityLevelById(mileIdLink);

			System.out.println(milesMainId+"-----"+mileIdLink+"%%%%%%%%"+isMasterData);
			if (!level1.getLinkedMilestonId().toString().equalsIgnoreCase("0")  ) {
				MilestoneActivityLevel level3= service.getMilestoneActivityLevelById(level1.getLinkedMilestonId().toString());
				level3.setLinkedMilestonId(0l);
				level3.setIsMasterData("N");
				service.MilestoneActivityLevelSave(level3);;
			}
			
			level1.setLinkedMilestonId(Long.parseLong(mileIdLink));
			level1.setIsMasterData(isMasterData.equalsIgnoreCase("Y")?"Y":"L");
			level1.setProgressStatus(isMasterData.equalsIgnoreCase("Y")?level1.getProgressStatus():level2.getProgressStatus());			
			long count =service.MilestoneActivityLevelSave(level1);
			
			level2.setLinkedMilestonId(Long.parseLong(milesMainId));
			level2.setIsMasterData(isMasterData.equalsIgnoreCase("N")?"Y":"L");
			level2.setProgressStatus(isMasterData.equalsIgnoreCase("N")?level2.getProgressStatus():level1.getProgressStatus());			

		
			
			long count1= service.MilestoneActivityLevelSave(level2);
			
			if(isMasterData.equalsIgnoreCase("Y")) {
				updateMilestoneRemarks(milesMainId,mileIdLink,UserId);
			}else {
				updateMilestoneRemarks(mileIdLink,milesMainId,UserId);
			}
			
			return json.toJson(5);
			
		} catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +" Inside updateMilestonSuperSeding.htm "+UserId, e); 
			return json.toJson(0);
		}
	
	}
	
	
	// 18-08
	@Async
	private void updateMilestoneRemarks(String id1, String id2,String UserId) {
		MileEditDto mainDto = new MileEditDto();
		
		mainDto.setActivityId(id1);
		
		mainDto.setActivityType("A");
		
		try {
			List<Object[]>subList = service.MilestoneActivitySub(mainDto);
			
			for(Object[]obj:subList) {
				MilestoneActivitySub attach=new MilestoneActivitySub();
				
				attach.setActivityId(Long.parseLong(id2));
				attach.setRemarks(obj[3].toString());
				attach.setCreatedBy(UserId);
				attach.setCreatedDate(LocalDateTime.now().toString());
				attach.setProgress(Integer.parseInt(obj[1].toString()));
				attach.setAttachName(obj[4].toString());				
				java.sql.Date sqlDate = java.sql.Date.valueOf(obj[2].toString());
			
				attach.setProgressDate(sqlDate);
				attach.setIsActive(1);
			
				service.saveMilestoneSub(attach);
			}
			String mainId = service.getMainLevelId(Long.parseLong(id2));
			mainDto.setMilestoneActivityId(mainId);
//			mainDto.setFlag("Y");
			mainDto.setActivityId(id2);
			service.updateMilestoneLevelProgress(mainDto);
			
		} catch (Exception e) {

			e.printStackTrace();
		}
		
	
		
	}
	
	@RequestMapping(value = "MilestoneLinked.htm", method = RequestMethod.GET)
	public @ResponseBody String MilestoneLinked(HttpServletRequest req, HttpSession ses,HttpServletResponse res)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside MilestoneLinked.htm.htm "+UserId);
		
		Gson json = new Gson();
		
		try {
		
			String id = req.getParameter("id");
			//String flag = req.getParameter("flag");
			System.out.println("id "+id);
		
			String projectId = service.getProjectIdByMainLevelId(id);
			
//			if(flag!=null  && flag.equalsIgnoreCase("Y") ) {
//				return json.toJson(projectId);
//			}
			
			Object[]projectDetails =  service.ProjectDetails(projectId).get(0);
			List<Object[]> main=service.MilestoneActivityList(projectId);
			List<Object[]> totalAssignedSubList = new ArrayList<>();
			Map<String , String>map = new LinkedHashMap<>();
			if(projectId!=null) {int count=1;
				for(Object[] objmain : main ) {
					
					List<Object[]>  MilestoneActivityA = service.MilestoneActivityLevel(objmain[0].toString(),"1");
					int countA=1;
					for(Object[] obj:MilestoneActivityA) {
						totalAssignedSubList.add(obj);
						map.put(obj[0].toString(), "M"+count+"-A"+countA+"/"+obj[4]+"/"+projectDetails[1]+"/"+projectDetails[0]);
						List<Object[]>  MilestoneActivityB=service.MilestoneActivityLevel(obj[0].toString(),"2");
						int countb=1;
						for(Object[] obj1:MilestoneActivityB) {
							totalAssignedSubList.add(obj1);
							map.put(obj1[0].toString(),"M"+count+"-A"+countA+"-B"+countb+"/"+obj1[4]+"/"+projectDetails[1]+"/"+projectDetails[0]);
							List<Object[]>  MilestoneActivityC=service.MilestoneActivityLevel(obj1[0].toString(),"3");
							int countc=1;
							for(Object[] obj2:MilestoneActivityC) {
								totalAssignedSubList.add(obj2);
								map.put(obj2[0].toString(),"M"+count+"-A"+countA+"-B"+countb+"-C"+countc+"/"+obj2[4]+"/"+projectDetails[1]+"/"+projectDetails[0]);
								List<Object[]>  MilestoneActivityD=service.MilestoneActivityLevel(obj2[0].toString(),"4");
								int countD=1;
								for(Object[] obj3:MilestoneActivityD) {
									totalAssignedSubList.add(obj3);
									map.put(obj3[0].toString(),"M"+count+"-A"+countA+"-B"+countb+"-C"+countc+"-D"+countD+"/"+obj3[4]+"/"+projectDetails[1]+"/"+projectDetails[0]);
									List<Object[]>  MilestoneActivityE=service.MilestoneActivityLevel(obj3[0].toString(),"5");
									int countE=1;
									for(Object[] obj4:MilestoneActivityE) {
										totalAssignedSubList.add(obj4);
										map.put(obj4[0].toString(),"M"+count+"-A"+countA+"-B"+countb+"-C"+countc+"-D"+countD+"-E"+countE+"/"+obj4[4]+"/"+projectDetails[1]+"/"+projectDetails[0]);
										countE++;
									}
									countD++;
									}
								countc++;
								}
							countb++;
							}
						countA++;
						}
					count++;
					}
				}
			
			
			
			return json.toJson(map);
		} catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +" Inside MilestoneLinked.htm "+UserId, e); 
			return json.toJson(0);
		}
		
	}
	@RequestMapping(value="MilestoneActivityLoadingSubmit.htm", method= {RequestMethod.GET, RequestMethod.POST})
	public String milestoneActivityLoadingSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside MilestoneActivityLoadingSubmit.htm "+UserId);
		try {
			String milestoneActivityId = req.getParameter("milestoneActivityId");
			String milestoneType = req.getParameter("milestoneType");
			String loading = req.getParameter("loading");
			
			long result = 0;
			if(milestoneType.equalsIgnoreCase("M")) {
				MilestoneActivity activity = service.getMilestoneActivityById(milestoneActivityId);
				activity.setLoading(loading!=null?Integer.parseInt(loading):0);
				result = service.MilestoneActivitySave(activity);
			}else if(milestoneType.equalsIgnoreCase("S")) {
				MilestoneActivityLevel level = service.getMilestoneActivityLevelById(milestoneActivityId);
				level.setLoading(loading!=null?Integer.parseInt(loading):0);
				result = service.MilestoneActivityLevelSave(level);
			}
			
			if (result> 0) {
				redir.addAttribute("result", "Milestone Loading Submitted Successfully");
			} else {
				redir.addAttribute("resultfail", "Milestone Loading Submit Unsuccessful");
			}
			
			redir.addAttribute("empId", req.getParameter("empId"));
			redir.addAttribute("activityType", req.getParameter("activityType"));
			return "redirect:/ResourceGanttChart.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside MilestoneActivityLoadingSubmit.htm "+UserId, e); 
			return "static/Error";
		}
	}
	
	@RequestMapping(value="ActionActivityLoadingSubmit.htm", method= {RequestMethod.GET, RequestMethod.POST})
	public String actionActivityLoadingSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside ActionActivityLoadingSubmit.htm "+UserId);
		try {
			String actionAssignId = req.getParameter("actionAssignId");
			String loading = req.getParameter("loading");
			
			ActionAssign assign = actionservice.getActionAssign(actionAssignId);
			assign.setLoading(loading!=null?Integer.parseInt(loading):0);
			long result = service.ActionAssignInsert(assign);
			
			if (result> 0) {
				redir.addAttribute("result", "Action Loading Submitted Successfully");
			} else {
				redir.addAttribute("resultfail", "Action Loading Submit Unsuccessful");
			}
			
			redir.addAttribute("empId", req.getParameter("empId"));
			redir.addAttribute("activityType", req.getParameter("activityType"));
			return "redirect:/ResourceGanttChart.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside ActionActivityLoadingSubmit.htm "+UserId, e); 
			return "static/Error";
		}
	}

	@RequestMapping(value = "MilestoneActivityProgress.htm", method= {RequestMethod.GET, RequestMethod.POST})
	public String milestoneActivityProgress(HttpServletRequest req,HttpSession ses) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String EmpId = ((Long)ses.getAttribute("EmpId")).toString();
		String LoginType = (String)ses.getAttribute("LoginType");
		String labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside MilestoneActivityProgress.htm "+UserId);
		try {
			String oldProjectId = req.getParameter("oldProjectId");
			String projectId = req.getParameter("projectId");
			String fromDate = req.getParameter("fromDate");
			String toDate = req.getParameter("toDate");
			String sancDate = req.getParameter("sancDate");
			List<Object[]> projectList = service.LoginProjectDetailsList(EmpId, LoginType, labcode);
			Object[] projectData = null;
			if(projectList!=null && projectList.size()>0) {
				if(projectId==null) {
					projectData = projectList.get(0);
					projectId = projectData!=null ? projectData[0].toString(): "0";
				}else {
					Long projectid = Long.parseLong(projectId);
					projectData = projectList!=null && projectList.size()>0 ? projectList.stream().filter(e -> projectid == Long.parseLong(e[0].toString())).findFirst().orElse(null):null;
				}
			}
			
			LocalDate today=LocalDate.now();
			if(fromDate==null || (oldProjectId!=null && oldProjectId!=null && !oldProjectId.equalsIgnoreCase(projectId))) {
				fromDate = today.minusWeeks(1).toString();
				toDate = today.toString();
				sancDate = projectData!=null ? projectData[12].toString(): today.toString();
			}else{
				fromDate=fc.rdfTosdf(fromDate);
				toDate=fc.rdfTosdf(toDate);
			}
			
			String finalProjectId = projectId;
			List<Object[]> mainList = service.getAllMilestoneActivityList();
			List<Object[]> subList = service.getAllMilestoneActivityLevelList();
			List<Object[]> progressList = service.getMilestoneActivityProgressList();
			
			LocalDate fromDateL = LocalDate.parse(fromDate);
			LocalDate toDateL = LocalDate.parse(toDate);
			
			List<Object[]> totalAssignedMainList = new ArrayList<>();
			List<Object[]> totalAssignedSubList = new ArrayList<>();
			Map<Long, List<Object[]>> progressListMap = new HashMap<Long, List<Object[]>>();
			
			if (mainList != null && !mainList.isEmpty()) {
				mainList = mainList.stream().filter(e -> e[1].toString().equalsIgnoreCase(finalProjectId) ).collect(Collectors.toList());
				
				totalAssignedMainList.addAll(mainList.stream()
											.filter(e -> !LocalDate.parse(e[6].toString()).isBefore(fromDateL) && !LocalDate.parse(e[7].toString()).isAfter(toDateL) )
											.collect(Collectors.toList()));
				
				Map<String, List<Object[]>> groupedByParentIdAndLevel = subList.stream().collect(Collectors.groupingBy(e -> e[1].toString() + "_" + e[2].toString()));

				for(Object[] objmain : mainList ) {

					//List<Object[]> MilestoneActivityA = subList.stream().filter(e -> e[1].toString().equalsIgnoreCase(objmain[0].toString()) && Integer.parseInt(e[2].toString())==1).collect(Collectors.toList());
					List<Object[]> MilestoneActivityA = groupedByParentIdAndLevel.getOrDefault(objmain[0].toString()+"_1", Collections.emptyList());
					int countA = 1;
					for(Object[] obj:MilestoneActivityA) {
						
						progressListMap.put(Long.parseLong(obj[0].toString()), filteredProgressList(progressList, obj[0].toString(), fromDateL, toDateL));
						
						Object[] newRow1 = Arrays.copyOf(obj, obj.length + 4);
				        newRow1[obj.length] = objmain[0].toString();
				        newRow1[obj.length + 1] = "A" + countA;
				        newRow1[obj.length + 2] = objmain[2];
				        newRow1[obj.length + 3] = objmain[1].toString();
				        totalAssignedSubList.add(newRow1);
						
						//List<Object[]> MilestoneActivityB = subList.stream().filter(e -> e[1].toString().equalsIgnoreCase(obj[0].toString()) && Integer.parseInt(e[2].toString())==2).collect(Collectors.toList());
						List<Object[]> MilestoneActivityB = groupedByParentIdAndLevel.getOrDefault(obj[0].toString()+"_2", Collections.emptyList());
						int countB = 1;
						for(Object[] obj1:MilestoneActivityB) {
													
							progressListMap.put(Long.parseLong(obj1[0].toString()), filteredProgressList(progressList, obj1[0].toString(), fromDateL, toDateL));

					        Object[] newRow2 = Arrays.copyOf(obj1, obj1.length + 4);
					        newRow2[obj1.length] = objmain[0].toString();
					        newRow2[obj1.length + 1] = "A"+countA+"-B"+countB;
					        newRow2[obj1.length + 2] = objmain[2];
					        newRow2[obj1.length + 3] = objmain[1].toString();
					        totalAssignedSubList.add(newRow2);
							
							//List<Object[]> MilestoneActivityC = subList.stream().filter(e -> e[1].toString().equalsIgnoreCase(obj1[0].toString()) && Integer.parseInt(e[2].toString())==3).collect(Collectors.toList());
							List<Object[]> MilestoneActivityC = groupedByParentIdAndLevel.getOrDefault(obj1[0].toString()+"_3", Collections.emptyList());
							int countC = 1;
							for(Object[] obj2:MilestoneActivityC) {

								progressListMap.put(Long.parseLong(obj2[0].toString()), filteredProgressList(progressList, obj2[0].toString(), fromDateL, toDateL));

						        Object[] newRow3 = Arrays.copyOf(obj2, obj2.length + 4);
						        newRow3[obj2.length] = objmain[0].toString();
						        newRow3[obj2.length + 1] = "A"+countA+"-B"+countB+"-C"+countC;
						        newRow3[obj2.length + 2] = objmain[2];
						        newRow3[obj2.length + 3] = objmain[1].toString();
						        totalAssignedSubList.add(newRow3);
								
								//List<Object[]> MilestoneActivityD = subList.stream().filter(e -> e[1].toString().equalsIgnoreCase(obj2[0].toString()) && Integer.parseInt(e[2].toString())==4).collect(Collectors.toList());
								List<Object[]> MilestoneActivityD = groupedByParentIdAndLevel.getOrDefault(obj2[0].toString()+"_4", Collections.emptyList());
								int countD = 1;
								for(Object[] obj3:MilestoneActivityD) {

									progressListMap.put(Long.parseLong(obj3[0].toString()), filteredProgressList(progressList, obj3[0].toString(), fromDateL, toDateL));

							        Object[] newRow4 = Arrays.copyOf(obj3, obj3.length + 4);
							        newRow4[obj3.length] = objmain[0].toString();
							        newRow4[obj3.length + 1] = "A"+countA+"-B"+countB+"-C"+countC+"-D"+countD;
							        newRow4[obj3.length + 2] = objmain[2];
							        newRow4[obj3.length + 3] = objmain[1].toString();
							        totalAssignedSubList.add(newRow4);
									
									//List<Object[]> MilestoneActivityE = subList.stream().filter(e -> e[1].toString().equalsIgnoreCase(obj3[0].toString()) && Integer.parseInt(e[2].toString())==5).collect(Collectors.toList());
									List<Object[]> MilestoneActivityE = groupedByParentIdAndLevel.getOrDefault(obj3[0].toString()+"_5", Collections.emptyList());
									int countE = 1;
									for(Object[] obj4:MilestoneActivityE) {

										progressListMap.put(Long.parseLong(obj4[0].toString()), filteredProgressList(progressList, obj4[0].toString(), fromDateL, toDateL));

								        Object[] newRow5 = Arrays.copyOf(obj4, obj4.length + 4);
								        newRow5[obj4.length] = objmain[0].toString();
								        newRow5[obj4.length + 1] = "A"+countA+"-B"+countB+"-C"+countC+"-D"+countD+"-E"+countE;
								        newRow5[obj4.length + 2] = objmain[2];
								        newRow5[obj4.length + 3] = objmain[1].toString();
								        totalAssignedSubList.add(newRow5);
									        
										countE++;
									}
									countD++;
								}
								countC++;
							}
							countB++;
						}
						countA++;
					}
				}
			}
			
			req.setAttribute("projectId", projectId);
			req.setAttribute("fromDate", fromDate);
			req.setAttribute("toDate", toDate);
			req.setAttribute("sancDate", sancDate);
			req.setAttribute("totalAssignedMainList", totalAssignedMainList);
			req.setAttribute("totalAssignedSubList", totalAssignedSubList);
			req.setAttribute("progressListMap", progressListMap);
			req.setAttribute("projectList", projectList);
			
			return "milestone/MilestoneActivityProgress";
		}catch (Exception e) {
			logger.error(new Date() +"Inside MilestoneActivityProgress.htm "+UserId ,e);
			e.printStackTrace(); 
			return "static/Error";
		}
	}

	@RequestMapping(value = "delteSubMilestoneActivity.htm", method = RequestMethod.GET)
	public @ResponseBody String delteSubMilestoneActivity(HttpServletRequest req, HttpSession ses,HttpServletResponse res
			)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside removeFileAttachment.htm "+UserId);
		try {
			 String activityId = req.getParameter("activityId");
		
			//long result = service.deleteMilsetone(activityId);
			
		System.out.println(activityId+"----activityId");
		
		List<String>activityIds = new ArrayList<String>();
			
		if(activityId!=null) {
			activityIds = 	Arrays.asList(activityId.split(","));
		}
		int deleteCount = 0;
		for(String s:activityIds) {
			deleteCount = deleteCount+service.deleteMilsetone(s);
		}
		Gson json = new Gson();
		
		return json.toJson(deleteCount);
		} catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +" Inside removeFileAttachment.htm "+UserId, e); 
			return "static/Error";
		}
	
	}


	private List<Object[]> filteredProgressList(List<Object[]> progressList, String activityId, LocalDate from, LocalDate to) {
	    return progressList.stream()
	        .filter(e -> e[1].toString().equalsIgnoreCase(activityId)
	                && !LocalDate.parse(e[3].toString()).isBefore(from)
	                && !LocalDate.parse(e[3].toString()).isAfter(to))
	        .collect(Collectors.toList());
	}
	
	@RequestMapping(value = "PreProjectDocRep.htm")
	public String PreProjectDocRep(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LoginType = (String)ses.getAttribute("LoginType");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside PreProjectDocRep.htm "+UserId);

		try {
			List<Object[] > projectslist= reqservice.getPreProjectList(LoginType, LabCode, EmpId);				
			String initiationId=req.getParameter("initiationId");
			Map md = model.asMap();
			if(initiationId==null) {		
				md = model.asMap();
				initiationId = (String) md.get("initiationId");   
			}
			if(initiationId==null) {
				try {
					initiationId=projectslist.get(0)[0].toString();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
			req.setAttribute("projectslist", projectslist);
			req.setAttribute("initiationId",initiationId);		
		}		
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside PreProjectDocRep.htm "+UserId, e); 
			return "static/Error";
		}
		return "filerepo/PreProjectRepMaster";
	}
	
 	@RequestMapping(value = "preProjectMainFolderList.htm", method = RequestMethod.GET)
		public @ResponseBody String getPreProjectMainFolderList(HttpServletRequest req, HttpSession ses) throws Exception {

			String UserId = (String) ses.getAttribute("Username");
			String labcode = (String) ses.getAttribute("labcode");
			
			String initiationId=req.getParameter("initiationId");
			List<Object[]> folderList =null;
			logger.info(new Date() +"Inside MainSystemList.htm "+UserId);
			try {
				folderList=service.getPreProjectFolderList(initiationId,labcode);
			}catch (Exception e) {
				e.printStackTrace();  logger.error(new Date() +" Inside preProjectMainFolderList.htm "+UserId, e); return "static/Error";
			}
			Gson json = new Gson();
			return json.toJson(folderList);

		}
 	
 	@RequestMapping(value = "preProjectSubFolderList.htm", method = RequestMethod.GET)
 	public @ResponseBody String getPreProjectSubFolderList(HttpServletRequest req, HttpSession ses) throws Exception {
 		
 		String UserId = (String) ses.getAttribute("Username");
 		String labcode = (String) ses.getAttribute("labcode");
 		
 		String initiationId=req.getParameter("initiationId");
 		String mainLevelId=req.getParameter("mainLevelId");
 		List<Object[]> folderList =null;
 		logger.info(new Date() +"Inside MainSystemList.htm "+UserId);
 		try {
 			folderList=service.getPreProjectSubFolderList(initiationId,mainLevelId,labcode);
 		}catch (Exception e) {
 			e.printStackTrace();  logger.error(new Date() +" Inside preProjectMainFolderList.htm "+UserId, e); return "static/Error";
 		}
 		Gson json = new Gson();
 		return json.toJson(folderList);
 	}
 	
 	@RequestMapping(value = "checkPreProjectFolderNames.htm", method = RequestMethod.GET)
	public @ResponseBody String getPreProjectFileRepMasterNames(HttpServletRequest req,HttpSession ses) throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside getPreProjectFileRepMasterNames"+UserId);
		int count = 0;
		Gson json = new Gson();
		try {				
			
			String initiationId=req.getParameter("initiationId");
			String fileId=req.getParameter("fileId");
			String fileType=req.getParameter("fileType");
			String fileName=req.getParameter("fileName");
			count=service.getPreProjectFileRepMasterNames(initiationId,fileType,fileId,fileName);
		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside checkPreProjectFolderNames.htm"+UserId, e);
		}
		return json.toJson(count);	
	}
 	
	@RequestMapping(value="PreProjectFileRepAdd.htm",method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> addPreProjectFileRep(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside PreProjectFileRepAdd.htm "+UserId);
		long count = 0l;
		Gson json = new Gson();
		try {
			if(InputValidator.isContainsHTMLTags(req.getParameter("levelName"))) {
				return new ResponseEntity<String>("200", HttpStatus.EXPECTATION_FAILED);	
			}
			FileRepMasterPreProject fileRepo=new FileRepMasterPreProject();
			fileRepo.setLabCode(LabCode);
			fileRepo.setLevelName(req.getParameter("levelName").trim());
			fileRepo.setInitiationId(Long.parseLong(req.getParameter("initiationId")));
			fileRepo.setCreatedBy(UserId);
			count=service.preProjectRepMasterInsert(fileRepo);
		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside PreProjectFileRepAdd.htm "+UserId, e); 
		}
		return new ResponseEntity<String>("200", HttpStatus.CREATED);	
	}
	
	@RequestMapping(value="PreProjectFileRepMasterSubAdd.htm",method = RequestMethod.POST)
	public @ResponseBody String preProjectFileMasterSubAdd(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside PreProjectFileRepMasterSubAdd.htm "+UserId);
		long count = 0l;
		Gson json = new Gson();
		try {
			
			if(InputValidator.isContainsHTMLTags(req.getParameter("levelName"))) {
				return  redirectWithError(redir,"FileRepMaster.htm","Sub-Level Name should not contain HTML elements !");
			}
			FileRepMasterPreProject fileRepo=new FileRepMasterPreProject();
			fileRepo.setLabCode(LabCode);
			fileRepo.setInitiationId(Long.parseLong(req.getParameter("initiationId")));
			fileRepo.setLevelName(req.getParameter("levelName").trim());
			fileRepo.setParentLevelId(Long.parseLong(req.getParameter("parentLevelId")));
			fileRepo.setLevelId(Long.parseLong(req.getParameter("levelId"))+1);
			fileRepo.setCreatedBy(UserId);
			count=service.preProjectFileRepMasterSubInsert(fileRepo);
		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside PreProjectFileRepMasterSubAdd.htm "+UserId, e); 
		}
		return json.toJson(count);	
	}
	
	@RequestMapping(value = "PreProjectFolderNameEdit.htm")
	public @ResponseBody ResponseEntity<String> PreProjectFolderNameEdit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside PreProjectFolderNameEdit.htm "+UserId);		
		long count = 0l;
		Gson json = new Gson();
		try {
			String filerepmasterid = req.getParameter("filerepmasterid");
			String levelname = req.getParameter("levelname").trim();
			String levelType = req.getParameter("levelType");
			if(InputValidator.isContainsHTMLTags(levelname)) {
				return new ResponseEntity<String>("200",HttpStatus.EXPECTATION_FAILED);
			}
			count = service.preProjectfileEditSubmit(filerepmasterid, levelname, levelType);
		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside PreProjectFolderNameEdit.htm "+UserId, e); 
		}
		return ResponseEntity.ok(json.toJson(count));	
	}
	
	
	@RequestMapping(value = "getPreProjectOldFileNames.htm", method = RequestMethod.GET)
	public @ResponseBody String getPreProjectOldFileNames(HttpServletRequest req,HttpSession ses) throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside getPreProjectOldFileNames"+UserId);
		List<Object[]> fileDocNameList=null;
		Gson json = new Gson();
		try {				
			String initiationId=req.getParameter("initiationId");
			String fileId=req.getParameter("fileId");
			String fileType=req.getParameter("fileType");
			fileDocNameList=service.getPreProjectOldFileNames(initiationId,fileType,fileId);
		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside getPreProjectOldFileNames"+UserId, e);
		}
		return json.toJson(fileDocNameList);	
	}
	
	@RequestMapping(value = "uploadPreProjectFile.htm", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> uploadPreProjectFile(HttpServletRequest req,HttpSession ses,
			@RequestParam(name = "fileAttach", required = false) MultipartFile file,
			@RequestParam("docName") String docName,
			@RequestParam("fileRepId") String fileRepId,
			@RequestParam("initiationId") String initiationId,
			@RequestParam("mainLevelId") String mainLevelId,
			@RequestParam("subLevelId") String subLevelId,
			@RequestParam("isnewversion") String isnewversion,
        	@RequestParam("fileType") String fileType,RedirectAttributes redir) throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside uploadPreProjectFile"+UserId);
		long result = 0l;
		Gson json = new Gson();
		try {

			
		
			// 🔹 Validate file types
	        if (!isValidFileType(file) ) {

	        	return new ResponseEntity<String>("417",HttpStatus.EXPECTATION_FAILED);
	        }
			
			

			if(InputValidator.isContainsHTMLTags(req.getParameter("docName"))) {
				return new ResponseEntity<String>("417",HttpStatus.EXPECTATION_FAILED);
			}

			FileUploadDto upload = new FileUploadDto();
			upload.setFileId(fileRepId);
			upload.setFileRepMasterId(mainLevelId);
			upload.setSubL1(subLevelId);
			upload.setDocumentName(docName);
			upload.setIS(file.getInputStream());
			upload.setFileNamePath(file.getOriginalFilename());
			upload.setInitiationId(initiationId);
			upload.setUserId(UserId);
			upload.setLabCode(labcode);
			upload.setIsNewVersion(isnewversion);
			
			result=service.uploadPreProjectFile(upload,fileType);
		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside uploadPreProjectFile"+UserId, e);
		}
		return new ResponseEntity<String>("200",HttpStatus.CREATED);
	}
	
	
	@RequestMapping(value = "preProjectFileDownload.htm/{id}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Resource> downloadPreProjectFile(
			 @PathVariable("id") Long id,
		     @RequestParam("fileType") String fileType,
	        HttpServletRequest req) throws Exception {

	    Optional<FileRepUploadPreProject> optionalFile = service.getPreProjectFileById(id);
	    if (!optionalFile.isPresent()) {
	        return ResponseEntity.notFound().build();
	    }

	    FileRepUploadPreProject fileEntity = optionalFile.get();
	    if (fileEntity.getFilePath() == null || fileEntity.getFileName() == null) {
	        return ResponseEntity.badRequest().build();
	    }

	    try {
	        Zipper zip = new Zipper();

	        // Build zip file path
	        String[] parts = fileEntity.getFilePath().replaceAll("[/\\\\]", ",").split(",");
	        String zipName = String.format("%s%s-%s.zip",
	                fileEntity.getFileNameUi(),
	                fileEntity.getVersionDoc(),
	                fileEntity.getReleaseDoc());

	        Path zipFilePath;
	        if ("mainLevel".equalsIgnoreCase(fileType)) {
	            zipFilePath = Paths.get(FilePath, parts[0], parts[1], parts[2], parts[3], zipName);
	        } else {
	            zipFilePath = Paths.get(FilePath, parts[0], parts[1], parts[2], parts[3], parts[4], zipName);
	        }

	        if (!Files.exists(zipFilePath)) {
	            return ResponseEntity.notFound().build();
	        }

	        // Extract zip to a temp folder
	        String tempDirPath = req.getServletContext().getRealPath("/view/temp/" + UUID.randomUUID());
	        File tempDir = new File(tempDirPath);
	        tempDir.mkdirs();

	        zip.unpack(zipFilePath.toString(), tempDirPath, fileEntity.getFilePass());

	        // Find the single PDF inside
	        File[] pdfFiles = tempDir.listFiles((d, name) -> name.toLowerCase().endsWith(".pdf"));
	        if (pdfFiles == null || pdfFiles.length == 0) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	        }

	        File pdfFile = pdfFiles[0];
	        Resource resource = new UrlResource(pdfFile.toURI());

	        return ResponseEntity.ok()
	                .contentType(MediaType.APPLICATION_PDF)
	                .header(HttpHeaders.CONTENT_DISPOSITION,
	                        "inline; filename=\"" + pdfFile.getName() + "\"")
	                .body(resource);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
	
	
	@RequestMapping(value = "getPreProjectDocVersionList.htm", method = RequestMethod.GET)
	public @ResponseBody String getPreProjectDocVersionList(HttpServletRequest req,HttpSession ses) throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside getFileRepDocList"+UserId);
		List<Object[]> fileDocList= new ArrayList<>();
		Gson json = new Gson();
		try {				
			String fileRepId=req.getParameter("fileRepId");
			fileDocList=service.preProjectFileRepDocsList(fileRepId);
		}
		catch (Exception e) {
			e.printStackTrace();  
			logger.error(new Date() +" Inside getFileRepDocList"+UserId, e);
		}
		return json.toJson(fileDocList);	
	}
	
	@RequestMapping(value = "PreProjectFileRepMasterListAllAjax.htm", method = RequestMethod.GET)
	public @ResponseBody String PreProjectFileRepMasterListAllAjax(Model model,HttpServletRequest req, HttpSession ses,HttpServletResponse res)throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside PreProjectFileRepMasterListAllAjax.htm "+UserId);

		String initiationId= req.getParameter("initiationId");	
		List<Object[]> FileLevelSublevelName = service.getPreProjectFileRepMasterListAll(initiationId,LabCode);

		Gson json = new Gson();
		return json.toJson(FileLevelSublevelName);
	}
	
	@GetMapping("/pdf-viewer")
	public String pdfViewerPage() {
	    return "filerepo/pdfViewer"; 
	}
	
	@RequestMapping(value="submitMilestoneFeedBack.htm")
	public @ResponseBody String submitMilestoneFeedBack(HttpServletRequest req, HttpSession ses,HttpServletResponse res,RedirectAttributes redir)throws Exception 
	{
		String EmpId = ((Long)ses.getAttribute("EmpId")).toString();
		String UserId = (String) ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside removeFileAttachment.htm "+UserId);
		try {
			
			if(InputValidator.isContainsHTMLTags(req.getParameter("remarks"))) {
				return  redirectWithError(redir,"M-A-AssigneeList.htm","Comments should not contain HTML elements !");
			}
			String activityId = req.getParameter("activityId");
			String remarks = req.getParameter("remarks");
			//long result = service.deleteMilsetone(activityId);
			
			
			System.out.println("remarks--->"+remarks);
			System.out.println("activityId--->"+activityId);
			
			MilestoneActivityLevelRemarks cmd = new MilestoneActivityLevelRemarks();
			
			cmd.setEmpid(Long.parseLong(EmpId));
			cmd.setActivityId(Long.parseLong(activityId));
			cmd.setRemarks(remarks);
			cmd.setCreatedDate(sdtf.format(new Date()));
			cmd.setIsactive(1);	
			
			
			Gson json = new Gson();
			
			return json.toJson(service.saveMilestoneActivityLevelRemarks(cmd));
		} catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +" Inside submitMilestoneFeedBack.htm "+UserId, e); 
			return "static/Error";
		}
		
	}
	
	@RequestMapping(value = "getMilestoneDraftRemarks.htm", method = RequestMethod.GET)
	public @ResponseBody String getMilestoneDraftRemarks(HttpSession ses, HttpServletRequest req) throws Exception 
	{
		String UserId = (String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		String LoginType = (String)ses.getAttribute("LoginType");
		String EmpId = ((Long)ses.getAttribute("EmpId")).toString();
		List<Object[]>list = new ArrayList<Object[]>();
		Gson json = new Gson();
		try {
			
					
			Long activityId = Long.parseLong(req.getParameter("activityId") );			
			
		
			
	
			list = service.getMilestoneDraftRemarks(activityId);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return json.toJson(list);
	}
	
	
	@RequestMapping(value = "MilestoneActivityManage.htm")
	public String MilestoneActivityManage(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception 
	{
		
		String UserId = (String) ses.getAttribute("Username");
		String Logintype= (String)ses.getAttribute("LoginType");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside MilestoneActivityList.htm "+UserId);		
		try {
			String ProjectId=req.getParameter("ProjectId");
			if(ProjectId==null)  {
				Map md=model.asMap();
				ProjectId=(String)md.get("ProjectId");
			}	
			List<Object[] > projlist= service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
			
			if(projlist.size()==0) 
			{				
				redir.addAttribute("resultfail", "No Project is Assigned to you.");
				return "redirect:/MainDashBoard.htm";
			}
			
			
			
			if(ProjectId==null) {
				try {
					Object[] pro=projlist.get(0);
					ProjectId=pro[0].toString();
				}catch (Exception e) {
					
				}
			}
			req.setAttribute("EmployeeList", service.EmployeeList());
			
			List<Object[]> main=service.MilestoneActivityList(ProjectId);
			req.setAttribute("MilestoneActivityList",main );
			req.setAttribute("ProjectList",projlist);
			req.setAttribute("ProjectId", ProjectId);
			req.setAttribute("EmployeeList", service.EmployeeList());
			if(ProjectId!=null) {
				req.setAttribute("ProjectDetails", service.ProjectDetails(ProjectId).get(0));
				int MainCount=1;
				for(Object[] objmain:main ) {
					int countA=1;
					List<Object[]>  MilestoneActivityA=service.MilestoneActivityLevel(objmain[0].toString(),"1");
					req.setAttribute(MainCount+"MilestoneActivityA", MilestoneActivityA);
					for(Object[] obj:MilestoneActivityA) {
						List<Object[]>  MilestoneActivityB=service.MilestoneActivityLevel(obj[0].toString(),"2");
						req.setAttribute(MainCount+"MilestoneActivityB"+countA, MilestoneActivityB);
						int countB=1;
						for(Object[] obj1:MilestoneActivityB) {
							List<Object[]>  MilestoneActivityC=service.MilestoneActivityLevel(obj1[0].toString(),"3");
							req.setAttribute(MainCount+"MilestoneActivityC"+countA+countB, MilestoneActivityC);
							int countC=1;
							for(Object[] obj2:MilestoneActivityC) {
								List<Object[]>  MilestoneActivityD=service.MilestoneActivityLevel(obj2[0].toString(),"4");
								req.setAttribute(MainCount+"MilestoneActivityD"+countA+countB+countC, MilestoneActivityD);
								int countD=1;
								for(Object[] obj3:MilestoneActivityD) {
									List<Object[]>  MilestoneActivityE=service.MilestoneActivityLevel(obj3[0].toString(),"5");
									req.setAttribute(MainCount+"MilestoneActivityE"+countA+countB+countC+countD, MilestoneActivityE);
									countD++;
								}
								countC++;
							}
							countB++;
						}
						countA++;
					}
					MainCount++;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +" Inside MilestoneActivityList.htm "+UserId, e); 
			return "static/Error";
			
		}
		return "milestone/MilestoneActivityManage";
	}
	
	// Milestone predecessor
	@RequestMapping(value = "predecessorActivity.htm", method = RequestMethod.GET)
	public @ResponseBody String predecessorActivity(HttpSession ses, HttpServletRequest req) throws Exception 
	{
		String UserId = (String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		String LoginType = (String)ses.getAttribute("LoginType");
		String EmpId = ((Long)ses.getAttribute("EmpId")).toString();
		Long count =0l;
		Gson json = new Gson();
		try {


			String activityIds = req.getParameter("activityIds") ;			
			String successor = req.getParameter("successor") ;			


			int deleteResult = service.deleteMilestoneActivityPredecessor(successor);


			String []activityId = activityIds.split(",");			

			for(String s:activityId) {
				MilestoneActivityPredecessor mp = new MilestoneActivityPredecessor();
				mp.setSuccessorId(Long.parseLong(successor));
				mp.setPredecessorId(Long.parseLong(s));
				mp.setCreatedBy(UserId);				
				mp.setCreatedDate(LocalDate.now().toString());	

				count = service.saveMilestoneActivityPredecessor(mp);			
			}



		}catch (Exception e) {
			e.printStackTrace();
		}

		return json.toJson(count);
	}
	@RequestMapping(value = "predecessorList.htm", method = RequestMethod.GET)
	public @ResponseBody String predecessorList(HttpSession ses, HttpServletRequest req) throws Exception 
	{
		String UserId = (String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");
		String LoginType = (String)ses.getAttribute("LoginType");
		String EmpId = ((Long)ses.getAttribute("EmpId")).toString();
		List<Object[]>predecessorList = new ArrayList<>();
		Gson json = new Gson();
		try {



			String successor = req.getParameter("successor") ;			

			System.out.println("successor----"+successor);

			predecessorList = service.predecessorList(successor);

		}catch (Exception e) {
			e.printStackTrace();
		}

		return json.toJson(predecessorList!=null && !predecessorList.isEmpty()
				?predecessorList.stream().map(e->e[2].toString()).collect(Collectors.toList())
						:predecessorList
				);

	}
	
	// Naveen 05-03-2026

	@RequestMapping(value = "MilestoneActivityAddBriefingPaper.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String addMileStoneActivityData(HttpServletRequest req,HttpSession ses, RedirectAttributes redir) {
		String UserId = (String)ses.getAttribute("Username");
		String labcode = (String)ses.getAttribute("labcode");

		try {
			
			logger.info("Inside MilestoneActivityAddBriefingPaper.htm with userId "+UserId+ "on "+new Date());
			
			String projectId = (String) req.getParameter("projectid");
			String committeeid = (String) req.getParameter("committeeid");
			String MileStoneActivityBriefingId = (String) req.getParameter("MileStoneActivityBriefingId");
			String briefingPointId = (String) req.getParameter("briefingPointId");
			String points = (String) req.getParameter("mileStonePoints");
			String scheduleId = (String) req.getParameter("scheduleid");
			
			redir.addAttribute("projectid",projectId);
			redir.addAttribute("committeeid",committeeid);
						
			MilestoneActivityBriefing entity = new MilestoneActivityBriefing();
			if(MileStoneActivityBriefingId!=null && !MileStoneActivityBriefingId.isBlank() && Long.parseLong(MileStoneActivityBriefingId) > 0) {
				entity = service.getMilestoneActivityBriefing(MileStoneActivityBriefingId);
				if(entity!=null) {
					entity.setBriefingPointId(Long.parseLong(briefingPointId));
					entity.setScheduleId(Long.parseLong(scheduleId));
					entity.setPoints(points);
					entity.setModifiedBy(UserId);
					entity.setModifiedDate(LocalDate.now());
					entity.setIsActive(1);
					long count = service.saveMilestoneActivityBriefing(entity);
					if (count > 0) {
						redir.addAttribute("result", "Milestone Points Edited Successfuly.");
					} else {
						redir.addAttribute("resultfail", "Milestone Points Edit Unsuccessful");
					}
				}else {
					redir.addAttribute("resultfail", "Milestone Points Edit Unsuccessful");
				}
				return "redirect:/ProjectBriefingPaper.htm";
			}
			else {
				entity.setBriefingPointId(Long.parseLong(briefingPointId));
				entity.setScheduleId(Long.parseLong(scheduleId));
				entity.setPoints(points);
				entity.setCreatedBy(UserId);
				entity.setCreatedDate(LocalDate.now());
				entity.setIsActive(1);				
			}
			
			long count = service.saveMilestoneActivityBriefing(entity);
		
			
			if (count > 0) {
				redir.addAttribute("result", "Milestone Points Added Successfuly.");
			} else {
				redir.addAttribute("resultfail", "Milestone Points Add Unsuccessful");
			}
			
			return "redirect:/ProjectBriefingPaper.htm";			
		}catch (Exception e) {
			logger.error("Error in the MilestoneActivityAddBriefingPaper.htm error: {}",e.getMessage());
			e.printStackTrace();
			return "static/Error";
		}
	}
	

	@RequestMapping(value = "GetMilestoneBriefingData.htm", method = RequestMethod.GET)
	public @ResponseBody String Getrecdecdata(HttpServletRequest req) throws Exception {
		Object[] list = null;
		try {
			String milestoneid = req.getParameter("milestoneid");
			list = service.getMilestoneActivityBriefingById(milestoneid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Gson json = new Gson();
		return json.toJson(list);
	}
	
	@RequestMapping(value = "DeleteMilestoneActivityBriefing.htm",method = RequestMethod.GET)
	public String deleteMileStoneActivityBriefing(HttpServletRequest req, RedirectAttributes redir) {
		try {

			String MileStoneActivityBriefingId = req.getParameter("MileStoneActivityBriefingDeleteId");
			String projectId = (String) req.getParameter("projectid");
			String committeeid = (String) req.getParameter("committeeid");
			
			redir.addAttribute("projectid",projectId);
			redir.addAttribute("committeeid",committeeid);
			
			if(MileStoneActivityBriefingId!=null && !MileStoneActivityBriefingId.isBlank()) {
				MilestoneActivityBriefing entity = service.getMilestoneActivityBriefing(MileStoneActivityBriefingId);
				entity.setIsActive(0);
				
				long count = service.saveMilestoneActivityBriefing(entity);

				if (count > 0) {
					redir.addAttribute("result", "Milestone Points Deleted Successfuly.");
				} else {
					redir.addAttribute("resultfail", "Milestone Points Delete Unsuccessful");
				}							
				return "redirect:/ProjectBriefingPaper.htm";	
			}
			redir.addAttribute("resultfail", "Milestone Points Delete Unsuccessful");

			return "redirect:/ProjectBriefingPaper.htm";			
			
		}catch (Exception e) {
			e.printStackTrace();
			return "static/Error";
		}
	}
	
	@RequestMapping(value = "DeleteMainMilestone.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String deleteMainMilestone(HttpServletRequest req, RedirectAttributes redir) {
		try {
			String projectId = req.getParameter("projectId");
			String mainId = req.getParameter("mainid");
			
			redir.addAttribute("ProjectId",projectId);
			
			long count = service.deleteMainLevelMilsetone(mainId);
			if (count > 0) {
				redir.addAttribute("result", "Milestone Deleted Successfuly.");
			} else {
				redir.addAttribute("resultfail", "Milestone Delete Unsuccessful");
			}	

			return "redirect:/MilestoneActivityList.htm";
		}catch (Exception e) {
			e.printStackTrace();
			return "static/Error";
		}		
	}
	
	@RequestMapping(value = "SubLevelMilestoneDelete.htm", method = {RequestMethod.GET,RequestMethod.POST,RequestMethod.DELETE})
	public String deleteSubLevelMilestone(HttpServletRequest req,HttpSession ses,RedirectAttributes redir) {
		try {
			String MilestoneActivityId = req.getParameter("MilestoneActivityId");
			String subId = req.getParameter("subId");
			
			redir.addAttribute("MilestoneActivityId",MilestoneActivityId);
			
			long count = service.deleteSubLevelMilsetone(subId);
			if (count > 0) {
				redir.addAttribute("result", "Milestone Deleted Successfuly.");
			} else if(count == -1) {
				redir.addAttribute("resultfail", "Milestone deletion unsuccessful because the selected milestone or its sub-levels have progress.");
			} else {
				redir.addAttribute("resultfail", "Milestone Delete Unsuccessful");
			} 

			redir.addAttribute("sub","C");
			return "redirect:/MilestoneActivityDetails.htm";
		}catch (Exception e) {
			e.printStackTrace();
			return "static/Error";
		}		
	}
	
	@RequestMapping(value = "UpdateMilestoneSunSet.htm",method = {RequestMethod.POST})
	public String updateSunSet(HttpServletRequest req,RedirectAttributes redir) {
	    try {
	    	String milestoneActivityId = req.getParameter("milestoneActivityId");
	    	String levelId = req.getParameter("levelId");
	    	String isSunSet = req.getParameter("isSunSet");
	        long count = service.updateSunSet(milestoneActivityId,levelId,isSunSet);
	        // if(count > 0)
	        //else 
	        String projectId = req.getParameter("projectId");
			
			redir.addAttribute("ProjectId",projectId);
			
			if (count > 0) {

			    redir.addAttribute(
			        "result",
			        "N".equalsIgnoreCase(isSunSet)
			            ? "SunSet Milestone marked successfully."
			            : "SunSet Milestone designation removed successfully."
			    );

			} else {

			    redir.addAttribute(
			        "resultfail",
			        "N".equalsIgnoreCase(isSunSet)
			            ? "Failed to mark the milestone as a SunSet Milestone."
			            : "Failed to remove the SunSet Milestone designation."
			    );
			}	

	        return "redirect:/MilestoneActivityList.htm";
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "static/Error";
	    }
	}
	
	@RequestMapping(value = "ValuationOfTechnologies.htm" , method = {RequestMethod.GET, RequestMethod.POST})
	public String valuationOfTechnical(HttpServletRequest req,HttpSession ses,RedirectAttributes redir) {
		
		String UserId = (String) ses.getAttribute("Username");
		String Logintype= (String)ses.getAttribute("LoginType");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String LabCode = (String)ses.getAttribute("labcode");
		
		try {
	        Map<String, ?> flash = RequestContextUtils.getInputFlashMap(req);
			String projectId  = req.getParameter("projectId");
			String finYear = req.getParameter("finYear");
			String quarter = req.getParameter("quarter");
			String activeTab = req.getParameter("activeTab");
			
			 if (flash != null) {
	            if (projectId == null) projectId = (String) flash.get("projectId");
	            if (finYear == null) finYear = (String) flash.get("finYear");
	            if (quarter == null) quarter = (String) flash.get("quarter");
	            if (activeTab == null) activeTab = (String) flash.get("activeTab");
	        }
			List<Object[] > projlist= service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
	
			if(projlist == null || projlist.isEmpty()) {				
				redir.addAttribute("resultfail", "No Project is Assigned to you.");
				return "redirect:/MainDashBoard.htm";
			}
			
			if(projectId == null) {
				Object[] project = projlist.get(0);
				if(project != null && project.length > 0) {
					projectId  = project[0]!= null ? project[0].toString() : null;
					String pdc = project[9] != null ? project[9].toString() : null;
					String sanctionDate  = project[12] != null ? project[12].toString() : null;
					finYear = service.getFinancialYear(pdc,sanctionDate);
				}
			}
			
			Object[] projectDetails = service.getprojectDetails(projectId);
			
			List<String> finYearList = new ArrayList<String>();
			List<String> quarters = new ArrayList<String>();
			
			if(projectDetails != null) {
				String pdcDate = projectDetails[17] != null ? projectDetails[17].toString() : null;
				String sanctionDate  = projectDetails[13] != null ? projectDetails[13].toString() : null;
				
				finYearList = service.getFinancialYearBasedOnProject(pdcDate,sanctionDate);	
				quarters = service.getQuartersBasedOnProject(sanctionDate, pdcDate, finYear);
//				finYear = service.getFinancialYear(pdcDate,sanctionDate);
			}
			
			if (quarter == null) {

			    int month = LocalDate.now().getMonthValue();

			    if (month >= 4 && month <= 6) {
			        quarter = "Q1";
			    } else if (month >= 7 && month <= 9) {
			        quarter = "Q2";
			    } else if (month >= 10 && month <= 12) {
			        quarter = "Q3";
			    } else {
			        quarter = "Q4";
			    }
			}	
			
			Object[] manpowerData = service.getManPowerUtilizationCounts(projectId, finYear, quarter);
			List<Object[]> infrastructureItems = service.getInfrastructureItems(projectId, finYear, quarter);
			List<Object[]> trainingItems = service.getTrainingItems(projectId, finYear, quarter);

			
			String infraMode = service.getInfrastructureMode(Long.parseLong(projectId), finYear, quarter);
			req.setAttribute("infraMode", infraMode);

			String trainMode = service.getTrainingMode(Long.parseLong(projectId), finYear, quarter);
			req.setAttribute("trainMode", trainMode);

			if (activeTab == null) activeTab = "manpower";

			req.setAttribute("activeTab", activeTab);
			req.setAttribute("finYear", finYear);
			req.setAttribute("projectId", projectId);
			req.setAttribute("quarter", quarter);
			
			req.setAttribute("manpowerData", manpowerData);
			req.setAttribute("infrastructureItems", infrastructureItems);
			req.setAttribute("trainingItems", trainingItems);
			
			req.setAttribute("projectList", projlist);
			req.setAttribute("finYearList", finYearList);			
			req.setAttribute("quarters", quarters);			
			return "milestone/ManPowerUtilization";
		}catch (Exception e) {
			e.printStackTrace();
			return "static/Error";
		}
	}
	
	@RequestMapping(value = "SaveManpowerUtilization.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String saveManpowerUtilization(HttpServletRequest req,RedirectAttributes redir,HttpSession ses) {

		String UserId = (String) ses.getAttribute("Username");
    	String projectId = req.getParameter("projectId");
    	String finYear = req.getParameter("finYear");
    	String quarter = req.getParameter("quarter");
    	String sciCount = req.getParameter("sciCount");
    	String techCount = req.getParameter("techCount");
    	String admCount = req.getParameter("admCount");
    	String sciDaysCount = req.getParameter("sciDaysCount");
    	String techDaysCount = req.getParameter("techDaysCount");
    	String admDaysCount = req.getParameter("admDaysCount");

	    try {
    	
	    	ProjectManPowerUtilizationDto dto = new ProjectManPowerUtilizationDto();
	    	
	    	dto.setProjectId(projectId);
	    	dto.setFinancialYear(finYear);
	    	dto.setQuarter(quarter);
	    	
	    	dto.setScientistCount(sciCount);
	    	dto.setTechnicalCount(techCount);
	    	dto.setAdminCount(admCount);
	    	
	    	dto.setSciDaysCount(sciDaysCount);
	    	dto.setTechDaysCount(techDaysCount);
	    	dto.setAdminDaysCount(admDaysCount);
	    	
	    	dto.setCreatedDate(LocalDateTime.now());
	    	dto.setCreatedBy(UserId);
	    	
	    	
	    	long count = service.addManPowerUtilization(dto);
	        
	    	if(count > 0) redir.addAttribute("result", "Manpower data saved successfully");
	    	else redir.addAttribute("resultfail", "Failed to save manpower data");
	    } catch (Exception e) {
	    	redir.addAttribute("resultfail", "Failed to save manpower data");
	    	e.printStackTrace();
	    }   
	    
	    redir.addAttribute("activeTab", "manpower");
	    redir.addAttribute("projectId", projectId);
	    redir.addAttribute("finYear", finYear);
	    redir.addAttribute("quarter", quarter);
	    return "redirect:/ValuationOfTechnologies.htm";
	}
	
	@RequestMapping(value = "UpdateManPowerUtilization.htm", method = {RequestMethod.POST,RequestMethod.PUT})
	public String updateManPowerUtilization(HttpServletRequest req,HttpSession ses, RedirectAttributes redir) {
		String UserId = (String) ses.getAttribute("Username");
    	String projectId = req.getParameter("projectId");
    	String finYear = req.getParameter("finYear");
    	String quarter = req.getParameter("quarter");
    	String sciCount = req.getParameter("sciCount");
    	String techCount = req.getParameter("techCount");
    	String admCount = req.getParameter("admCount");
    	String sciDaysCount = req.getParameter("sciDaysCount");
    	String techDaysCount = req.getParameter("techDaysCount");
    	String admDaysCount = req.getParameter("admDaysCount");

	    try {
	    	ProjectManPowerUtilizationDto dto = new ProjectManPowerUtilizationDto();
	    	
	    	dto.setProjectId(projectId);
	    	dto.setFinancialYear(finYear);
	    	dto.setQuarter(quarter);
	    	
	    	dto.setScientistCount(sciCount);
	    	dto.setTechnicalCount(techCount);
	    	dto.setAdminCount(admCount);

	    	dto.setSciDaysCount(sciDaysCount);
	    	dto.setTechDaysCount(techDaysCount);
	    	dto.setAdminDaysCount(admDaysCount);
	    	
	    	dto.setCreatedDate(LocalDateTime.now());
	    	dto.setCreatedBy(UserId);
	    	
	    	dto.setAction(req.getParameter("mode"));
	    	long count = service.updateManPowerUtilization(dto);
	        
	    	String action = dto.getAction();
	    	
	    	String label = "";
	    	String errorLabel ="";
	    	
	    	if("EDIt".equalsIgnoreCase(action)) {
	    		label = "Edited";
	    		errorLabel = "Edit";
	    	}else {
	    		label = "Revised";
	    		errorLabel = "Revise";
	    	}
	    	
	    	if(count > 0) redir.addAttribute("result", "Manpower data " + label + " successfully");
	    	else redir.addAttribute("resultfail", "Failed to " + errorLabel + " manpower data");
	    } catch (Exception e) {
	    	redir.addAttribute("resultfail", "Failed to save manpower data");
	    	e.printStackTrace();
	    }
	    redir.addAttribute("projectId", projectId);
	    redir.addAttribute("finYear", finYear);
	    redir.addAttribute("quarter", quarter);

	    return "redirect:/ValuationOfTechnologies.htm";
	}
	
	
	@RequestMapping(value = "SaveInfrastructureUsed.htm", method = {RequestMethod.GET, RequestMethod.POST})
	public String saveInfrastructureUsed(@ModelAttribute ProjectInfrastructureUtilizationDto dto,@RequestParam("mode") String mode, HttpSession ses, RedirectAttributes redir) {
	    dto.setCreatedBy((String) ses.getAttribute("Username"));
	    dto.setCreatedDate(LocalDateTime.now());
        String label;
        String errorLabel;
	    try {
	        long count;
	    	if ("EDIT".equals(mode)) {
	    		label="Editing";
	    		errorLabel = "Edit";
	            count = service.editInfrastructureUtilization(dto);
	        } else if ("REVISE".equals(mode)) {
	        	label="Revision";
	    		errorLabel = "Revise";
	            count = service.reviseInfrastructureUtilization(dto);
	        } else {
	        	label ="Added";
	    		errorLabel = "Save";
	            count = service.saveInfrastructureUtilization(dto); // ADD
	        }

	        if( count > 0) {
	        	redir.addAttribute("result", "Infrastructure data "+label+" successful");
	        }else {
	        	redir.addAttribute("resultfail", "Failed to "+ errorLabel +" infrastructure data");
	        }
	    } catch (Exception e) {
	    	e.printStackTrace();
	        redir.addAttribute("resultfail", "Error Proccessing the Request!");
	    }
	    redir.addAttribute("activeTab", "infrastructure");
	    redir.addAttribute("projectId", dto.getProjectId());
	    redir.addAttribute("finYear", dto.getFinYear());
	    redir.addAttribute("quarter", dto.getQuarter());
	    return "redirect:/ValuationOfTechnologies.htm";
	}
	
	@RequestMapping(value = "SaveTrainingCost.htm", method = {RequestMethod.GET, RequestMethod.POST})
	public String saveTrainingCost(@ModelAttribute ProjectTrainingUtilizationDto dto, @RequestParam("mode") String mode, HttpSession ses, RedirectAttributes redir) {
	    dto.setCreatedBy((String) ses.getAttribute("Username"));
	    dto.setCreatedDate(LocalDateTime.now());
	    String label;
	    String errorLabel;
	    
	    try {
	        long count;
	        if ("EDIT".equals(mode)) {
	            label = "Editing";
	            errorLabel = "Edit";
	            count = service.editTrainingUtilization(dto);
	        } else if ("REVISE".equals(mode)) {
	            label = "Revision";
	            errorLabel = "Revise";
	            count = service.reviseTrainingUtilization(dto);
	        } else {
	            label = "Added";
	            errorLabel = "Save";
	            count = service.saveTrainingUtilization(dto); // ADD
	        }

	        if (count > 0) {
	            redir.addAttribute("result", "Training cost data " + label + " successful");
	        } else {
	            redir.addAttribute("resultfail", "Failed to " + errorLabel + " training cost data");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        redir.addAttribute("resultfail", "Error Processing the Request!");
	    }
	    
	    redir.addAttribute("activeTab", "training");
	    redir.addAttribute("projectId", dto.getProjectId());
	    redir.addAttribute("finYear", dto.getFinYear());
	    redir.addAttribute("quarter", dto.getQuarter());
	    return "redirect:/ValuationOfTechnologies.htm";
	}
	
	
	@RequestMapping(value = "EconomicImpact.htm" , method = {RequestMethod.GET, RequestMethod.POST})
	public String economicImpactOfProject(HttpServletRequest req,HttpSession ses,RedirectAttributes redir) {
		
		String UserId = (String) ses.getAttribute("Username");
		String Logintype= (String)ses.getAttribute("LoginType");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String LabCode = (String)ses.getAttribute("labcode");
		String projectId  = req.getParameter("projectId");
		
		try {
			
			
			List<Object[] > projlist= service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
	
			if(projlist == null || projlist.isEmpty()) {				
				redir.addAttribute("resultfail", "No Project is Assigned to you.");
				return "redirect:/MainDashBoard.htm";
			}
			
			if(projectId == null) {
				Object[] project = projlist.get(0);
				if(project != null && project.length > 0) {
					if(project[0]!= null) projectId  = project[0].toString();
				}
			}

			final String selectedProjectId = projectId;
			
			Object[] selectedProject = projlist.stream()
			        .filter(obj -> obj != null
			                && obj.length > 0
			                && obj[0] != null
			                && obj[0].toString().equalsIgnoreCase(selectedProjectId))
			        .findFirst()
			        .orElse(null);
			
			List<ProjectEconomicImpact> economicImpact = service.getEconomicImpact(Long.parseLong(projectId));

			req.setAttribute("projectId", projectId);
			req.setAttribute("projectDetails", selectedProject);
			
			req.setAttribute("projectList", projlist);
			req.setAttribute("economicImpact", economicImpact);
			return "milestone/EconomicImpact";
		}catch (Exception e) {
			e.printStackTrace();
			return "static/Error";
		}
	}
	
	@RequestMapping(value = "SaveEconomicImpact.htm", method = {RequestMethod.POST,RequestMethod.PUT})
	public String saveEconomicImpactOfProject(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) {

	    String UserId = (String) ses.getAttribute("Username");
	    String projectId = req.getParameter("projectId");
	    String label;
	    String errorLabel;
	    
	    try {

	    	ProjectEconomicImpactDto dto = new ProjectEconomicImpactDto();
	        dto.setProjectId(projectId);
	        dto.setEconomicImpactId(req.getParameter("economicImpactId"));
	        dto.setIndigenousContentAndIndigenization(req.getParameter("indigenousContentAndIndigenization"));
	        dto.setInternationalCollaborationsExecuted(req.getParameter("internationalCollaborationsExecuted"));
	        dto.setIntellectualPropertyRights(req.getParameter("intellectualPropertyRights"));
	        dto.setExportPotential(req.getParameter("exportPotential"));
	        dto.setInfrastructureCreated(req.getParameter("infrastructureCreated"));
	        dto.setCreatedBy(UserId);
	    	
	    	String status = req.getParameter("status");
	    	long count;
	        if ("EDIT".equals(status)) {
	            label = "Editing";
	            errorLabel = "Edit";
	            count = service.editEconomicImpact(dto);
	        } else if ("REVISE".equals(status)) {
	            label = "Revision";
	            errorLabel = "Revise";
	            count = service.reviseEconomicImpact(dto);
	        } else {
	            label = "Added";
	            errorLabel = "Save";
	            count = service.saveEconomicImpact(dto); // ADD
	        }

	        redir.addAttribute("projectId", projectId);
	        if (count > 0) {
	            redir.addAttribute("result", "Economic Impact " + label + " successful");
	        } else {
	            redir.addAttribute("resultfail", "Failed to " + errorLabel + " Economic Impact data");
	        }
	        return "redirect:/EconomicImpact.htm";
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "static/Error";
	    }
	}
	
	@RequestMapping(value = "GetRevisionList.htm", method = RequestMethod.GET)
	@ResponseBody
	public List<RevisionSummaryDto> getRevisionList(HttpServletRequest req) throws Exception {
		try {
			String projectId = req.getParameter("projectId");
			String finYear = req.getParameter("finYear");
			String quarter = req.getParameter("quarter");
			String type = req.getParameter("type");

		    Long resourceUtilizationId = service.findResourceUtilizationIdRaw(Long.parseLong(projectId), finYear, quarter);
		    if (resourceUtilizationId == null) return Collections.emptyList();
	
		    switch (type) {
		        case "INFRASTRUCTURE": return service.getInfrastructureRevisionList(resourceUtilizationId);
		        case "TRAINING":       return service.getTrainingRevisionList(resourceUtilizationId);
		        case "MANPOWER":       return service.getManpowerRevisionList(resourceUtilizationId);
		        default:               return Collections.emptyList();
		    }
		}catch(Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	@RequestMapping(value = "GetRevisionItems.htm", method = RequestMethod.GET)
	@ResponseBody
	public List<RevisionItemDto> getRevisionItems(HttpServletRequest req) throws Exception {

		try {
			String projectId = req.getParameter("projectId");
			String finYear = req.getParameter("finYear");
			String quarter = req.getParameter("quarter");
			String type = req.getParameter("type");
			String revisionNo = req.getParameter("revisionNo");
			
		    Long resourceUtilizationId = service.findResourceUtilizationIdRaw(Long.parseLong(projectId), finYear, quarter);
		    if (resourceUtilizationId == null) return Collections.emptyList();
	
		    switch (type) {
		        case "INFRASTRUCTURE": return service.getInfrastructureRevisionItems(resourceUtilizationId, revisionNo);
		        case "TRAINING":       return service.getTrainingRevisionItems(resourceUtilizationId, revisionNo);
		        case "MANPOWER":       return service.getManpowerRevisionItems(resourceUtilizationId, revisionNo);
		        default:               return Collections.emptyList();
		    }
		}catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	@RequestMapping(value = "EconomicImpactHistory.htm", method = RequestMethod.GET)
	public String economicImpactHistory(HttpServletRequest req,HttpSession ses) throws Exception {
	    String projectId = req.getParameter("projectId");
	    String revisionNoParam = req.getParameter("revisionNo");
	    Long projectIdLong = Long.parseLong(projectId);
	    
		String UserId = (String) ses.getAttribute("Username");
		String Logintype= (String)ses.getAttribute("LoginType");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String LabCode = (String)ses.getAttribute("labcode");
	    
		try {
			List<Object[] > projlist= service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
	
		    List<Object[]> revisionList = service.getEconomicImpactRevisionList(projectIdLong);
	
		    Long selectedRevisionNo = null;
		    if (revisionNoParam != null) {
		        selectedRevisionNo = Long.parseLong(revisionNoParam);
		    } else if (!revisionList.isEmpty()) {
		        selectedRevisionNo = ((Number) revisionList.get(0)[0]).longValue();
		    }
	
		    String indigenousContent = "", internationalCollaboration = "", intellectualProperty = "",
		           exportPotential = "", infrastructureCreated = "", revisedDate = "", revisedBy = "";
	
		    if (selectedRevisionNo != null) {
		        ProjectEconomicImpactRev rev = service.getEconomicImpactRevisionData(projectIdLong, selectedRevisionNo);
		        if (rev != null) {
		            indigenousContent = rev.getIndigenousContentAndIndigenization();
		            internationalCollaboration = rev.getInternationalCollaborationsExecuted();
		            intellectualProperty = rev.getIntellectualPropertyRights();
		            exportPotential = rev.getExportPotential();
		            infrastructureCreated = rev.getInfrastructureCreated();
		            revisedDate = rev.getRevisionDate() != null ? rev.getRevisionDate().toString() : "";
		            revisedBy = rev.getModifiedBy() != null ? rev.getModifiedBy() : rev.getCreatedBy();
		        }
		    }
	
		    Object[] projectDetails = service.getprojectDetails(projectId);
	
		    req.setAttribute("projectId", projectId);
		    req.setAttribute("projectDetails", projectDetails);
		    req.setAttribute("revisionList", revisionList);
		    req.setAttribute("selectedRevisionNo", selectedRevisionNo);
		    req.setAttribute("indigenousContent", indigenousContent);
		    req.setAttribute("internationalCollaboration", internationalCollaboration);
		    req.setAttribute("intellectualProperty", intellectualProperty);
		    req.setAttribute("exportPotential", exportPotential);
		    req.setAttribute("infrastructureCreated", infrastructureCreated);
		    req.setAttribute("revisedDate", revisedDate);
		    req.setAttribute("revisedBy", revisedBy);
			req.setAttribute("projectList", projlist);
	
		    return "milestone/EconomicImpactHistory";
	    }catch (Exception e) {
			e.printStackTrace();
			return "static/Error";
		}
	}
	
	@RequestMapping(value = "GetInfrastructureForCopy.htm", method = RequestMethod.GET)
	@ResponseBody
	public List<Map<String, Object>> getInfrastructureForCopy(
	        @RequestParam("projectId") String projectId,
	        @RequestParam("finYear") String finYear,
	        @RequestParam("sourceQuarter") String sourceQuarter) throws Exception {

	    Long resourceUtilizationId = service.findResourceUtilizationIdRaw(Long.parseLong(projectId), finYear, sourceQuarter);
	    if (resourceUtilizationId == null) return Collections.emptyList();

	    List<ProjectInfrastructureUtilization> active = service.getActiveInfrastructure(resourceUtilizationId);
	    List<Map<String, Object>> result = new ArrayList<>();
	    for (ProjectInfrastructureUtilization row : active) {
	        Map<String, Object> m = new HashMap<>();
	        m.put("infraName", row.getNameOfInfrastructure());
	        m.put("daysUtilized", row.getDaysUtilized());
	        result.add(m);
	    }
	    return result;
	}
	
	@RequestMapping(value = "GetTrainingForCopy.htm", method = RequestMethod.GET)
	@ResponseBody
	public List<Map<String, Object>> getTrainingForCopy(
	        @RequestParam("projectId") String projectId,
	        @RequestParam("finYear") String finYear,
	        @RequestParam("sourceQuarter") String sourceQuarter) throws Exception {

	    Long resourceUtilizationId = service.findResourceUtilizationIdRaw(Long.parseLong(projectId), finYear, sourceQuarter);
	    if (resourceUtilizationId == null) return Collections.emptyList();

	    List<ProjectTrainingUtilization> active = service.getActiveTraining(resourceUtilizationId);
	    List<Map<String, Object>> result = new ArrayList<>();
	    for (ProjectTrainingUtilization row : active) {
	        Map<String, Object> m = new HashMap<>();
	        m.put("trainingName", row.getNameOfTraining());
	        m.put("cost", row.getCost());
	        result.add(m);
	    }
	    return result;
	}

}


