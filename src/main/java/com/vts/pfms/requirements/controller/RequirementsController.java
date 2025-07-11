package com.vts.pfms.requirements.controller;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.font.FontProvider;
import com.vts.pfms.CharArrayWriterResponse;
import com.vts.pfms.FormatConverter;
import com.vts.pfms.producttree.service.ProductTreeService;
import com.vts.pfms.project.controller.ProjectController;
import com.vts.pfms.project.dto.PfmsInitiationRequirementDto;
import com.vts.pfms.project.model.PfmsInititationRequirement;
import com.vts.pfms.project.service.ProjectService;
import com.vts.pfms.requirements.model.Abbreviations;
import com.vts.pfms.requirements.model.DocMembers;
import com.vts.pfms.requirements.model.DocumentFreeze;
import com.vts.pfms.requirements.model.PfmsReqTypes;
import com.vts.pfms.requirements.model.PfmsSystemSubIntroduction;
import com.vts.pfms.requirements.model.ReqDoc;
import com.vts.pfms.requirements.model.RequirementInitiation;
import com.vts.pfms.requirements.model.Specification;
import com.vts.pfms.requirements.model.SpecificationMaster;
import com.vts.pfms.requirements.model.SpecsInitiation;
import com.vts.pfms.requirements.model.TestAcceptance;
import com.vts.pfms.requirements.model.TestApproach;
import com.vts.pfms.requirements.model.TestDetails;
import com.vts.pfms.requirements.model.TestInstrument;
import com.vts.pfms.requirements.model.TestPlanInitiation;
import com.vts.pfms.requirements.model.TestPlanMaster;
import com.vts.pfms.requirements.model.TestPlanSummary;
import com.vts.pfms.requirements.model.TestSetUpAttachment;
import com.vts.pfms.requirements.model.TestSetupMaster;
import com.vts.pfms.requirements.model.TestTools;
import com.vts.pfms.requirements.model.VerificationData;
import com.vts.pfms.requirements.service.RequirementService;
import com.vts.pfms.utils.PMSLogoUtil;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@Controller
public class RequirementsController {
	@Autowired
	PMSLogoUtil LogoUtil;

	@Autowired
	ProjectService projectservice;

	@Autowired
	RequirementService service;

	@Autowired
	ProductTreeService productreeservice;
	
	@Value("${ApplicationFilesDrive}")
	String uploadpath;

	private static final Logger logger = LogManager.getLogger(ProjectController.class);

	FormatConverter fc = new FormatConverter();
	private SimpleDateFormat sdf2 = fc.getRegularDateFormat();/* new SimpleDateFormat("dd-MM-yyyy"); */
	private SimpleDateFormat sdf1 = fc.getSqlDateAndTimeFormat(); /* new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); */
	private SimpleDateFormat sdf3 = fc.getSqlDateFormat();

	@RequestMapping(value = "Requirements.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String projectRequirementsList(HttpServletRequest req, HttpSession ses, HttpServletResponse res,
			RedirectAttributes redir) throws Exception

	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String LoginType = (String) ses.getAttribute("LoginType");

		logger.info(new Date() + "Inside Requirements.htm" + UserId);
		try {

			String projectType = req.getParameter("projectType");
			projectType = projectType != null ? projectType : "M";
			String initiationId = "0";
			String projectId = "0";
			String productTreeMainId = "0";
			if (projectType.equalsIgnoreCase("M")) {

				projectId = req.getParameter("projectId");
				productTreeMainId = req.getParameter("productTreeMainId");
				List<Object[]> projectList = projectservice.LoginProjectDetailsList(EmpId, LoginType, LabCode);
				projectId = projectId != null ? projectId
						: (projectList.size() > 0 ? projectList.get(0)[0].toString() : "0");

				List<Object[]> productTreeList = service.productTreeListByProjectId(projectId);
				// productTreeMainId = productTreeMainId!=null?productTreeMainId:
				// (productTreeList.size()>0?productTreeList.get(0)[0].toString():"0");
				productTreeMainId = productTreeMainId != null ? productTreeMainId : "0";
				req.setAttribute("projectDetails", projectservice.getProjectDetails(LabCode, projectId, "E"));
				req.setAttribute("ProjectList", projectList);
				req.setAttribute("projectId", projectId);
				req.setAttribute("productTreeList", productTreeList);
				req.setAttribute("productTreeMainId", productTreeMainId);
				req.setAttribute("initiationReqList", service.initiationReqList(projectId, productTreeMainId, "0"));
			} else {
				initiationId = req.getParameter("initiationId");
				productTreeMainId = req.getParameter("productTreeMainId");
				productTreeMainId = productTreeMainId != null ? productTreeMainId : "0";
				List<Object[]> preProjectList = service.getPreProjectList(LoginType, LabCode, EmpId);
				initiationId = initiationId != null ? initiationId
						: (preProjectList.size() > 0 ? preProjectList.get(0)[0].toString() : "0");
				req.setAttribute("preProjectList", preProjectList);
				req.setAttribute("initiationId", initiationId);
				List<Object[]> productTreeList = service.productTreeListByInitiationId(initiationId);
				req.setAttribute("productTreeList", productTreeList);
				req.setAttribute("productTreeMainId", productTreeMainId);
				req.setAttribute("initiationReqList", service.initiationReqList("0", productTreeMainId, initiationId));
				req.setAttribute("projectDetails", projectservice.getProjectDetails(LabCode, initiationId, "P"));
			}

			req.setAttribute("projectType", projectType);
			req.setAttribute("requirementApprovalFlowData",
					service.getRequirementApprovalFlowData(initiationId, projectId, productTreeMainId));

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside Requirements.htm " + UserId, e);
			return "static/Error";
		}
		return "requirements/ProjectRequirementsList";
	}

	@RequestMapping(value = "SpecificationMasters.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String SpecificarionMaster(HttpServletRequest req, HttpServletResponse res, HttpSession ses,
			RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");

		logger.info(new Date() + "Inside SpecificationMasters.htm" + UserId);
		req.setAttribute("systemList", productreeservice.getAllSystemName());
		
		String sid = req.getParameter("sid");
		String mainid = req.getParameter("mainid");
		if(sid==null) {
			sid="1";
		}
		if(mainid==null) {
			mainid="0";
		}
		req.setAttribute("sid", sid);
		req.setAttribute("mainid", mainid);
	
		List<Object[]>proList=productreeservice.getSystemProductTreeList(sid);
//		if(proList!=null && proList.size()>0) {
//			proList=proList.stream().filter(e->e[2].toString().equalsIgnoreCase("1")).collect(Collectors.toList());
//		}
		req.setAttribute("proList", proList);
				try {
			String sidsub=sid;
			String mainsub=mainid;
			List<Object[]> specificationMasterList = service.SpecificationMasterList()
														.stream().filter(e->e[12].toString().equalsIgnoreCase(sidsub)
																&& e[13].toString().equalsIgnoreCase(mainsub))
																 .collect(Collectors.toList());
			req.setAttribute("specificarionMasterList", specificationMasterList);
			req.setAttribute("specificationTypesList", service.getSpecificationTypesList());
			req.setAttribute("specTypeId", req.getParameter("specTypeId"));
			
			return "requirements/SpecificationMasterList";
		} catch (Exception e) {

			e.printStackTrace();
			logger.error(new Date() + " Inside SpecificationMasters.htm " + UserId, e);
			return "static/Error";

		}
		

	}

	@RequestMapping(value="SpecificationMasterExcel.htm" ,method = {RequestMethod.POST,RequestMethod.GET})
	public void SpecificationMasterExcel( RedirectAttributes redir,HttpServletRequest req ,HttpServletResponse res ,HttpSession ses)throws Exception
	{
		String UserId=(String)ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() +"Inside ExcelUpload.htm "+UserId);
		String project= req.getParameter("project");
		try(XSSFWorkbook workbook = new XSSFWorkbook()){
			String action = req.getParameter("Action"); 
			String Type=req.getParameter("Type");
			String specTypeId=req.getParameter("specTypeId");
			
			if("GenerateExcel".equalsIgnoreCase(action)) {
				XSSFSheet sheet =  workbook.createSheet("Specification Master");
				XSSFRow row=sheet.createRow(0);

				CellStyle unlockedCellStyle = workbook.createCellStyle();
				unlockedCellStyle.setLocked(true); // Set the cell to be locked
				unlockedCellStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
				// Create the row
				unlockedCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND); // Set the fill pattern
				unlockedCellStyle.setBorderTop(BorderStyle.THIN);
				unlockedCellStyle.setBorderBottom(BorderStyle.THIN);
				unlockedCellStyle.setBorderLeft(BorderStyle.THIN);
				unlockedCellStyle.setBorderRight(BorderStyle.THIN);
				row = sheet.createRow(0); // Assuming this is the header row

				// Create and style cells
				Cell maincell0 = row.createCell(0);
				maincell0.setCellValue("SN");
				maincell0.setCellStyle(unlockedCellStyle); // Apply the style
				sheet.setColumnWidth(0, 5000);

				Cell maincell1 = row.createCell(1);
				maincell1.setCellValue("SpecificationCode");
				maincell1.setCellStyle(unlockedCellStyle); // Apply the style
				sheet.setColumnWidth(1, 5000);
				
				
				Cell maincell2 = row.createCell(2);
				maincell2.setCellValue("Parameter");
				maincell2.setCellStyle(unlockedCellStyle); // Apply the style
				sheet.setColumnWidth(2, 9000);
				
				Cell maincell7 = row.createCell(3);
				maincell7.setCellValue("Description");
				maincell7.setCellStyle(unlockedCellStyle); // Apply the style
				sheet.setColumnWidth(4, 9000);

				Cell maincellmin = row.createCell(4);
				maincellmin.setCellValue("Minimum Value");
				maincellmin.setCellStyle(unlockedCellStyle); // Apply the style
				sheet.setColumnWidth(3, 9000);
				
				Cell maincell3 = row.createCell(5);
				maincell3.setCellValue("Typical Value/Value");
				maincell3.setCellStyle(unlockedCellStyle); // Apply the style
				sheet.setColumnWidth(3, 9000);

				Cell maincellmax = row.createCell(6);
				maincellmax.setCellValue("Maximum Value");
				maincellmax.setCellStyle(unlockedCellStyle); // Apply the style
				sheet.setColumnWidth(3, 9000);
				
				Cell maincell4 = row.createCell(7);
				maincell4.setCellValue("Unit");
				maincell4.setCellStyle(unlockedCellStyle); // Apply the style
				sheet.setColumnWidth(4, 9000);
				
				Cell maincell8 = row.createCell(8);
				maincell8.setCellValue("Spec Type");
				maincell8.setCellStyle(unlockedCellStyle); // Apply the style
				sheet.setColumnWidth(4, 9000);
				
				
				Cell maincell9 = row.createCell(9);
				maincell9.setCellValue("Unit Required");
				maincell9.setCellStyle(unlockedCellStyle); // Apply the style
				sheet.setColumnWidth(4, 9000);
				
				Cell maincell10 = row.createCell(10);
				maincell10.setCellValue("Is Child");
				maincell10.setCellStyle(unlockedCellStyle); // Apply the style
				sheet.setColumnWidth(4, 9000);
				int r=0;

				List<Object[]> SpecificarionMasterList = service.SpecificationMasterList();
				
				if(SpecificarionMasterList!=null && SpecificarionMasterList.size()>0 ) {
					
					if(!Type.equalsIgnoreCase("A") ) {
						SpecificarionMasterList=SpecificarionMasterList
								.stream().filter(e -> e[19] != null && e[19].toString().equalsIgnoreCase(specTypeId)).collect(Collectors.toList());
					}
				for(Object[]obj:SpecificarionMasterList) {
					row = sheet.createRow(++r);
					// Determine the color based on a condition
					CellStyle style = workbook.createCellStyle();
					if (!obj[14].toString().equalsIgnoreCase("0")) { 
					    style.setFillForegroundColor(IndexedColors.WHITE.getIndex()); // Green for someCondition
					} else {
					    style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex()); // Orange otherwise
					}
					style.setFillPattern(FillPatternType.SOLID_FOREGROUND); // Set the fill pattern
					style.setBorderTop(BorderStyle.THIN);
					style.setBorderBottom(BorderStyle.THIN);
					style.setBorderLeft(BorderStyle.THIN);
					style.setBorderRight(BorderStyle.THIN);
					
					CellStyle centeredStyle = workbook.createCellStyle();
					centeredStyle.setAlignment(HorizontalAlignment.CENTER); // Center horizontally
					if (!obj[14].toString().equalsIgnoreCase("0")) { 
						centeredStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex()); // Green for someCondition
					} else {
						centeredStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex()); // Orange otherwise
					}
					
					centeredStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND); // Set the fill pattern
					centeredStyle.setBorderTop(BorderStyle.THIN);
					centeredStyle.setBorderBottom(BorderStyle.THIN);
					centeredStyle.setBorderLeft(BorderStyle.THIN);
					centeredStyle.setBorderRight(BorderStyle.THIN);
					// Apply the style to each cell in the row
					Cell cell0 = row.createCell(0);
					cell0.setCellValue(String.valueOf(r));
					cell0.setCellStyle(style);
					cell0.setCellStyle(centeredStyle);
					sheet.setColumnWidth(0, 3000);
					
					Cell cell1 = row.createCell(1);
					cell1.setCellValue(obj[5] != null ? obj[5].toString() : "-");
					cell1.setCellStyle(style);
					sheet.setColumnWidth(1, 7000);
					
					Cell cell2 = row.createCell(2);
					cell2.setCellValue(obj[3] != null ? obj[3].toString() : "-");
					cell2.setCellStyle(style);

					Cell cell3 = row.createCell(3);
					cell3.setCellValue(obj[2] != null ? obj[2].toString() : "-");
					cell3.setCellStyle(style);

					Cell cell4 = row.createCell(4);
					cell4.setCellValue(obj[16] != null ? obj[16].toString() : "-");
					cell4.setCellStyle(style);
					sheet.setColumnWidth(4, 10000);
					
					Cell cell5 = row.createCell(5);
					cell5.setCellValue(obj[6] != null ? obj[6].toString() : "-");
					cell5.setCellStyle(style);
					sheet.setColumnWidth(5, 10000);
					
					Cell cell6 = row.createCell(6);
					cell6.setCellValue(obj[15] != null ? obj[15].toString() : "-");
					cell6.setCellStyle(style);
					sheet.setColumnWidth(6, 10000);
					Cell cell7 = row.createCell(7);
					cell7.setCellValue(obj[4] != null && obj[4].toString().trim().length()>0 ? obj[4].toString() : "-");
					cell7.setCellStyle(style);
					sheet.setColumnWidth(7, 10000);
					
					Cell cell8 = row.createCell(8);
					cell8.setCellValue(obj[20] != null ? obj[20].toString() : "-");
					cell8.setCellStyle(style);
					sheet.setColumnWidth(8, 10000);
					
					Cell cell9 = row.createCell(9);
					cell9.setCellValue(obj[4]!=null && obj[4].toString().trim().length()>0?"YES":"NO"  );
					cell9.setCellStyle(style);
					sheet.setColumnWidth(9, 10000);
					
					
					Cell cell10 = row.createCell(10);
					cell10.setCellValue(obj[14]!=null && obj[14].toString().equals("0")?"NO":"YES"  );
					cell10.setCellStyle(style);
					sheet.setColumnWidth(9, 10000);
				}}
				res.setContentType("application/vnd.ms-excel");
				res.setHeader("Content-Disposition", "attachment; filename=SpecificationMaster.xls");	
				workbook.write(res.getOutputStream());
			}}
			catch (Exception e) {
				e.printStackTrace();
			}
		
	
		}
	@RequestMapping(value = "SpecificationMasterAdd.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String SpecificarionMasterAdd(HttpServletRequest req, HttpServletResponse res, HttpSession ses,
			RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");

		logger.info(new Date() + "Inside SpecificationMasterAdd.htm" + UserId);

		try {
			String SpecsMasterId = req.getParameter("Did");
			String action = req.getParameter("sub");
			
			SpecificationMaster sp = action.equalsIgnoreCase("edit") ?service.getSpecificationMasterById(Long.parseLong(SpecsMasterId)):new SpecificationMaster();
			
			req.setAttribute("SpecificationMaster", sp);
			req.setAttribute("systemList", productreeservice.getAllSystemName());
			req.setAttribute("specificationTypesList", service.getSpecificationTypesList());
			req.setAttribute("specTypeId", req.getParameter("specTypeId"));
			
			if(action.equalsIgnoreCase("add")) {
				
				return "requirements/SpecificationMasterAdd";
			}else {
				List<Object[]>subSpecificationListLevel1 = new ArrayList<>();
				List<Object[]>subSpecificationListLevel2 = new ArrayList<>();
				List<Object[]> SpecificationMasterList=service.SpecificationMasterList();
				
				for(Object[]obj:SpecificationMasterList) {
					if(obj[14].toString().equalsIgnoreCase(sp.getSpecsMasterId()+"")) {
						subSpecificationListLevel1.add(obj);
						subSpecificationListLevel2=SpecificationMasterList.stream()
													.filter(e->e[14].toString().equalsIgnoreCase(obj[0].toString()))
													.collect(Collectors.toList());
						subSpecificationListLevel1.addAll(subSpecificationListLevel2);
					}
				}
				
				req.setAttribute("subSpecificationList", subSpecificationListLevel1);

				return "requirements/SpecificationMasterAdd";
			}
			
		} catch (Exception e) {

			e.printStackTrace();
			logger.error(new Date() + " Inside SpecificationMasterAdd.htm " + UserId, e);
			return "static/Error";

		}

	}

	@RequestMapping(value = "ProjectRequirementDetails.htm")
	public String ProjectRequirementDetails(HttpServletRequest req, HttpSession ses, HttpServletResponse res,
			RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String LoginType = (String) ses.getAttribute("LoginType");

		logger.info(new Date() + "Inside ProjectRequirementDetails.htm" + UserId);
		try {
			String reqInitiationId = req.getParameter("reqInitiationId");
			String projectType = null;
			String projectId = null;
			String initiationId = null;
			String productTreeMainId = null;
			RequirementInitiation reqInitiation = null;
			if (!reqInitiationId.equals("0")) {
				reqInitiation = service.getRequirementInitiationById(reqInitiationId);
				projectId = reqInitiation.getProjectId().toString();
				initiationId = reqInitiation.getInitiationId().toString();
				productTreeMainId = reqInitiation.getProductTreeMainId().toString();
				projectType = projectId.equals("0") ? "I" : "M";
				String version = reqInitiation.getReqVersion().toString();
				req.setAttribute("DocumentVersion", version);
				reqInitiationId = service.getFirstVersionReqInitiationId(initiationId, projectId, productTreeMainId)
						+ "";
			} else {
				projectType = req.getParameter("projectType");
				projectId = req.getParameter("projectId");
				initiationId = req.getParameter("initiationId");
				productTreeMainId = req.getParameter("productTreeMainId");

				initiationId = initiationId != null && !initiationId.isEmpty() ? initiationId : "0";
				projectId = projectId != null && !projectId.isEmpty() ? projectId : "0";
				productTreeMainId = productTreeMainId != null && !productTreeMainId.isEmpty() ? productTreeMainId : "0";
			}

			projectType = projectType == null ? (projectId.equals("0") ? "I" : "M") : projectType;

			if (projectType != null && projectType.equalsIgnoreCase("I")) {

				redir.addAttribute("initiationId", initiationId);
				redir.addAttribute("reqInitiationId", reqInitiationId);
				redir.addAttribute("productTreeMainId", productTreeMainId);
				return "redirect:/ProjectOverAllRequirement.htm";
			}

			req.setAttribute("projectType", projectType);
			req.setAttribute("projectId", projectId);
			req.setAttribute("initiationId", initiationId);
			req.setAttribute("productTreeMainId", productTreeMainId);
			req.setAttribute("reqInitiationId", reqInitiationId);

			req.setAttribute("TotalEmployeeList", projectservice.EmployeeList(LabCode));
			req.setAttribute("DocumentSummary", projectservice.getDocumentSummary(reqInitiationId));
			req.setAttribute("LabList", projectservice.LabListDetails(LabCode));
			req.setAttribute("MemberList", projectservice.reqMemberList(reqInitiationId));
			req.setAttribute("EmployeeList", projectservice.EmployeeList(LabCode, reqInitiationId));
			req.setAttribute("AbbreviationDetails", projectservice.getAbbreviationDetails(reqInitiationId));
			req.setAttribute("ApplicableDocumentList", service.ApplicableDocumentList(reqInitiationId));
			req.setAttribute("ApplicableTotalDocumentList", service.ApplicableTotalDocumentList(reqInitiationId));

			req.setAttribute("projectDetails", projectservice.getProjectDetails(LabCode, projectId, "E"));
			req.setAttribute("reqInitiation", reqInitiation);

			if (!productTreeMainId.equalsIgnoreCase("0")) {
				Long firstReqInitiationId = service.getFirstVersionReqInitiationId(initiationId, projectId, "0");
				List<Object[]> RequirementMainList = service.RequirementList(firstReqInitiationId + "");
				req.setAttribute("RequirementList", RequirementMainList);
			}

			req.setAttribute("DocTempAttributes", projectservice.DocTempAttributes());
			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(LabCode));
			req.setAttribute("drdologo", LogoUtil.getDRDOLogoAsBase64String());
			List<Object[]> productTreeList = service.productTreeListByProjectId(projectId);
			req.setAttribute("productTreeList", productTreeList);
			req.setAttribute("VerificationMethodList", service.getVerificationMethodList());

			req.setAttribute("LabList", projectservice.LabListDetails(LabCode));
			req.setAttribute("result", req.getParameter("result"));
			req.setAttribute("resultfail", req.getParameter("resultfail"));
			return "requirements/ProjectRequirementDetails";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside ProjectRequirementDetails.htm " + UserId, e);
			return "static/Error";
		}
	}

	@RequestMapping(value = "RequirementAppendixMain.htm", method = { RequestMethod.POST, RequestMethod.GET }) // bharath
	public String RequirementAppendixMain(RedirectAttributes redir, HttpServletRequest req, HttpServletResponse res,
			HttpSession ses) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() + "Inside RequirementAppendix.htm " + UserId);
		try {
			req.setAttribute("projectId", req.getParameter("projectId"));// bharath
			req.setAttribute("initiationId", req.getParameter("initiationId"));
			req.setAttribute("productTreeMainId", req.getParameter("productTreeMainId"));
			req.setAttribute("reqInitiationId", req.getParameter("reqInitiationId"));
			req.setAttribute("AcronymsList", projectservice.AcronymsList(req.getParameter("reqInitiationId")));
			req.setAttribute("PerformanceList", projectservice.getPerformanceList(req.getParameter("reqInitiationId")));
			req.setAttribute("projectDetails",
					projectservice.getProjectDetails(LabCode, req.getParameter("projectId"), "E"));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside RequirementAppendix.htm " + UserId, e);
		}
		return "requirements/RequirementAppendices";
	}

	@RequestMapping(value = "ProjectRequiremntIntroductionMain.htm", method = { RequestMethod.GET, RequestMethod.POST })
	public String ProjectReqIntroductionMain(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() + "Inside ProjectRequiremntIntroductionMain.htm " + UserId);

		try {
			String initiationid = req.getParameter("initiationid");
			String project = req.getParameter("project");
			String projectId = req.getParameter("projectId");
			String productTreeMainId = req.getParameter("productTreeMainId");
			String reqInitiationId = req.getParameter("reqInitiationId");
			if (initiationid == null) {
				initiationid = "0";
			}
			if (projectId == null) {
				projectId = "0";
			}
			if (productTreeMainId == null) {
				productTreeMainId = "0";
			}
			req.setAttribute("projectId", projectId);
			req.setAttribute("initiationid", initiationid);
			req.setAttribute("project", project);
			req.setAttribute("reqInitiationId", reqInitiationId);
			req.setAttribute("productTreeMainId", productTreeMainId);
			req.setAttribute("attributes",
			req.getParameter("attributes") == null ? "Introduction" : req.getParameter("attributes"));
			req.setAttribute("projectDetails", projectservice.getProjectDetails(LabCode, projectId, "E"));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside ProjectRequiremntIntroduction.htm " + UserId, e);
		}

		return "requirements/RequirementIntro";
	}

	// bharath changes
	@RequestMapping(value = "RequirementParaMain.htm", method = { RequestMethod.GET, RequestMethod.POST })
	public String RequirementPara(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() + "Inside RequirementParaMain.htm " + UserId);
		try {
			String projectId = req.getParameter("projectId");
			String productTreeMainId = req.getParameter("productTreeMainId");
			String reqInitiationId = req.getParameter("reqInitiationId");
			req.setAttribute("projectId", projectId);
			req.setAttribute("productTreeMainId", productTreeMainId);
			req.setAttribute("reqInitiationId", reqInitiationId);
			req.setAttribute("ParaDetails", projectservice.ReqParaDetailsMain(reqInitiationId));
			req.setAttribute("SQRFile", projectservice.SqrFiles(reqInitiationId));
			String value = projectservice.ReqParaDetailsMain(reqInitiationId) != null && projectservice.ReqParaDetailsMain(reqInitiationId).size() > 0
							? projectservice.ReqParaDetailsMain(reqInitiationId).get(0)[0].toString()
							: "1";
			req.setAttribute("paracounts",
					req.getParameter("paracounts") == null ? value : req.getParameter("paracounts"));
			req.setAttribute("projectDetails", projectservice.getProjectDetails(LabCode, projectId, "E"));
			req.setAttribute("TotalSqr", service.getAllSqr(reqInitiationId));
		} catch (Exception e) {
			logger.error(new Date() + " Inside RequirementParaMain.htm " + UserId, e);
		}
		return "requirements/ProjectRequirementPara";
	}

	@RequestMapping(value = "OtherMainRequirement.htm", method = { RequestMethod.GET, RequestMethod.POST })
	public String OtherMainRequirement(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
			throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside OtherMainRequirement.htm " + UserId);
		try {
			String projectId = req.getParameter("projectId");
			req.setAttribute("projectId", projectId);
			String initiationid = req.getParameter("initiationid");
			if (initiationid == null) {
				initiationid = "0";
			}
			req.setAttribute("Otherrequirements", projectservice.projecOtherRequirements(initiationid, projectId)); // changed
																													// the
																													// code
																													// here
																													// pass
																													// the
																													// projectId
			req.setAttribute("RequirementList", projectservice.otherProjectRequirementList(initiationid, projectId)); // changed
																														// the
																														// code
																														// here
																														// pass
																														// the
																														// projectId
			req.setAttribute("MainId", req.getParameter("MainId") == null ? "1" : req.getParameter("MainId"));
		} catch (Exception e) {
			logger.error(new Date() + " Inside OtherMainRequirement.htm " + UserId, e);
		}
		return "requirements/OtherRequirements";
	}

	@RequestMapping(value = "RequirementVerifyMain.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String RequirementVerify(HttpServletRequest req, HttpSession ses, HttpServletResponse res,
			RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() + "Inside RequirementVerifyMain.htm" + UserId);
		try {
			String initiationId = req.getParameter("initiationId");
			String projectId = req.getParameter("projectId");
			String productTreeMainId = req.getParameter("productTreeMainId");
			String reqInitiationId = req.getParameter("reqInitiationId");
			if (initiationId == null)
				initiationId = "0";
			if (projectId == null)
				projectId = "0";
			if (productTreeMainId == null)
				productTreeMainId = "0";

			req.setAttribute("initiationid", initiationId);
			req.setAttribute("projectId", projectId);
			req.setAttribute("productTreeMainId", productTreeMainId);
			req.setAttribute("reqInitiationId", reqInitiationId);
			req.setAttribute("Verifications", projectservice.getVerificationListMain(reqInitiationId));
			req.setAttribute("paracounts",
					req.getParameter("paracounts") == null ? "1" : req.getParameter("paracounts"));
			// req.setAttribute("verificationId",req.getParameter("verificationId"));
			req.setAttribute("projectDetails", projectservice.getProjectDetails(LabCode, projectId, "E"));
		} catch (Exception e) {

		}
		return "requirements/MainProjectVerification";
	}

	@RequestMapping(value = "ProjectMainRequirement.htm", method = { RequestMethod.GET, RequestMethod.POST })
	public String ProjectMainRequirement(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside ProjectMainRequirement.htm " + UserId);
		try {
			String projectId = req.getParameter("projectId");
			String initiationid = req.getParameter("initiationid");
			String reqInitiationId = req.getParameter("reqInitiationId");
			if (initiationid == null) {
				initiationid = "0";
			}

			List<Object[]> requirementList = service.RequirementList(reqInitiationId);

			req.setAttribute("projectId", projectId);
			req.setAttribute("reqTypeList", projectservice.RequirementTypeList());
			req.setAttribute("ParaDetails", projectservice.ReqParaDetailsMain(projectId));
			req.setAttribute("RequirementList", requirementList);
			String InitiationReqId = req.getParameter("InitiationReqId");
			if (InitiationReqId == null) {
				if (requirementList != null && requirementList.size() > 0) {
					InitiationReqId = requirementList.get(0)[0].toString();
				}
			}
			req.setAttribute("InitiationReqId", InitiationReqId);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "requirements/ProjectRequirement";

	}

	@RequestMapping(value = "ProjectRequirementSubmit.htm", method = RequestMethod.POST)
	public String ProjectRequirementAddSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
			throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside ProjectRequirementSubmit.htm " + UserId);
		String LabCode = (String) ses.getAttribute("labcode");

		String option = req.getParameter("action");
		try {
			String r = req.getParameter("reqtype");
			String[] reqtype = r.split(" ");
			Long reqTypeId = Long.parseLong(reqtype[0]);

			String projectId = req.getParameter("projectId");
			String intiationId = req.getParameter("IntiationId");
			String reqInitiationId = req.getParameter("reqInitiationId");
			if (projectId == null) {
				projectId = "0";
			}
			if (intiationId == null) {
				intiationId = "0";
			}

			Long a = (Long) projectservice.numberOfReqTypeId(intiationId, projectId);
			String requirementId = "";
			if (a < 90L) {
				requirementId = reqtype[2] + reqtype[1] + ("000" + (a + 10));
			} else if (a < 990L) {
				requirementId = reqtype[2] + reqtype[1] + ("00" + (a + 10));
			} else {
				requirementId = reqtype[2] + reqtype[1] + ("0" + (a + 10));
			}

			String needType = req.getParameter("needtype");

			String RequirementDesc = req.getParameter("description");
			String RequirementBrief = req.getParameter("reqbrief");
			String linkedRequirements = "";
			if (req.getParameterValues("linkedRequirements") != null) {
				String[] linkedreq = req.getParameterValues("linkedRequirements");

				for (int i = 0; i < linkedreq.length; i++) {
					linkedRequirements = linkedRequirements + linkedreq[i];
					if (i != linkedreq.length - 1) {
						linkedRequirements = linkedRequirements + ",";
					}
				}
			}
			String linkedPara = "";
			if (req.getParameterValues("LinkedPara") != null) {
				String[] linkedParaArray = req.getParameterValues("LinkedPara");

				for (int i = 0; i < linkedParaArray.length; i++) {
					linkedPara = linkedPara + linkedParaArray[i];
					if (i != linkedParaArray.length - 1) {
						linkedPara = linkedPara + ",";
					}
				}
			}

			String linkedAttachements = "";
			if (req.getParameterValues("linkedAttachements") != null) {
				String[] linkedreq = req.getParameterValues("linkedAttachements");

				for (int i = 0; i < linkedreq.length; i++) {
					linkedAttachements = linkedAttachements + linkedreq[i];
					if (i != linkedreq.length - 1) {
						linkedAttachements = linkedAttachements + ",";
					}
				}
			}
			Long IntiationId = Long.parseLong(intiationId);
			PfmsInitiationRequirementDto prd = new PfmsInitiationRequirementDto();
			// prd.setInitiationId(IntiationId);
			prd.setReqTypeId(reqTypeId);
			prd.setRequirementId(requirementId);
			prd.setRequirementBrief(RequirementBrief);
			prd.setRequirementDesc(RequirementDesc);
			prd.setReqCount((a.intValue() + 10));
			prd.setPriority(req.getParameter("priority"));
			prd.setLinkedRequirements(linkedRequirements);
			prd.setLinkedPara(linkedPara);
			prd.setNeedType(needType);
			prd.setRemarks(req.getParameter("remarks"));
			prd.setCategory(req.getParameter("Category"));
			prd.setConstraints(req.getParameter("Constraints"));
			prd.setLinkedDocuments(linkedAttachements);
			// prd.setProjectId(Long.parseLong(projectId));
			prd.setReqInitiationId(Long.parseLong(reqInitiationId));
			long count = service.ProjectRequirementAdd(prd, UserId, LabCode);
			long InitiationReqId = count;
			if (count > 0) {
				redir.addAttribute("result", "Project Requirement Added Successfully");
			} else {
				redir.addAttribute("resultfail", "Project Requirement Add Unsuccessful");
			}
			redir.addAttribute("projectId", projectId);
			redir.addAttribute("InitiationReqId", InitiationReqId);
			return "redirect:/ProjectMainRequirement.htm";

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside ProjectRequirementSubmit.htm  " + UserId, e);
			return "static/Error";
		}
		/* return "redirect:/ProjectRequirement.htm"; */

	}

	@RequestMapping(value = "ProjectSpecifications.htm", method = { RequestMethod.GET, RequestMethod.POST })
	public String ProjectSpecifications(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) {
		String UserId = (String) ses.getAttribute("Username");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String LoginType = (String) ses.getAttribute("LoginType");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() + "Inside ProjectSpecifications.htm " + UserId);
		try {
			String projectType = req.getParameter("projectType");
			String projectId = req.getParameter("projectId");
			String initiationId = req.getParameter("initiationId");
			if (projectType == null) {
				projectType = "M";
			}
			req.setAttribute("projectType", projectType);
			List<Object[]> MainProjectList = projectservice.LoginProjectDetailsList(EmpId, LoginType, LabCode);// main
																												// Project
																												// List
			List<Object[]> InitiationProjectList = projectservice.ProjectIntiationList(EmpId, LoginType, LabCode); // initiationProject
			if (projectType.equalsIgnoreCase("M")) {
				req.setAttribute("MainProjectList", MainProjectList);
				if (projectId == null) {
					projectId = MainProjectList.get(0)[0].toString();
					initiationId = "0";
				}
			} else {
				req.setAttribute("InitiationProjectList", InitiationProjectList);
				if (initiationId == null) {
					initiationId = InitiationProjectList.get(0)[0].toString();
					projectId = "0";
				}
			}
			req.setAttribute("projectId", projectId);
			req.setAttribute("initiationId", initiationId);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "requirements/Specification";
	}

	@RequestMapping(value = "ProjectTestPlan.htm")
	public String projectTestPlanList(HttpServletRequest req, HttpSession ses, HttpServletResponse res,
			RedirectAttributes redir) throws Exception

	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String LoginType = (String) ses.getAttribute("LoginType");

		logger.info(new Date() + "Inside ProjectTestPlan.htm" + UserId);
		try {

			String projectType = req.getParameter("projectType");
			projectType = projectType != null ? projectType : "M";

			String initiationId = "0";
			String projectId = "0";
			String productTreeMainId = "0";
			if (projectType.equalsIgnoreCase("M")) {
				projectId = req.getParameter("projectId");
				productTreeMainId = req.getParameter("productTreeMainId");

				List<Object[]> projectList = projectservice.LoginProjectDetailsList(EmpId, LoginType, LabCode);
				projectId = projectId != null ? projectId
						: (projectList.size() > 0 ? projectList.get(0)[0].toString() : "0");

				List<Object[]> productTreeList = service.productTreeListByProjectId(projectId);
				// productTreeMainId = productTreeMainId!=null?productTreeMainId:
				// (productTreeList.size()>0?productTreeList.get(0)[0].toString():"0");
				productTreeMainId = productTreeMainId != null ? productTreeMainId : "0";
				req.setAttribute("projectDetails", projectservice.getProjectDetails(LabCode, projectId, "E"));
				req.setAttribute("ProjectList", projectList);
				req.setAttribute("projectId", projectId);
				req.setAttribute("productTreeList", productTreeList);
				req.setAttribute("productTreeMainId", productTreeMainId);
				req.setAttribute("initiationTestPlanList",
						service.initiationTestPlanList(projectId, productTreeMainId, "0"));
			} else {
				initiationId = req.getParameter("initiationId");
				productTreeMainId=req.getParameter("productTreeMainId")==null?"0":req.getParameter("productTreeMainId");
				req.setAttribute("productTreeMainId", productTreeMainId);
				List<Object[]> preProjectList = service.getPreProjectList(LoginType, LabCode, EmpId);
				initiationId = initiationId != null ? initiationId
						: (preProjectList.size() > 0 ? preProjectList.get(0)[0].toString() : "0");
				req.setAttribute("preProjectList", preProjectList);
				req.setAttribute("initiationId", initiationId);
				List<Object[]> productTreeList = service.productTreeListByInitiationId(initiationId);
				req.setAttribute("productTreeList", productTreeList);
				req.setAttribute("projectDetails", projectservice.getProjectDetails(LabCode, initiationId, "P"));
				req.setAttribute("initiationTestPlanList", service.initiationTestPlanList("0", productTreeMainId, initiationId));
			}

			req.setAttribute("projectType", projectType);

			req.setAttribute("testPlanApprovalFlowData",
					service.getTestPlanApprovalFlowData(initiationId, projectId, productTreeMainId));

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside ProjectTestPlan.htm " + UserId, e);
			return "static/Error";
		}
		return "requirements/ProjectTestPlanList";
	}

	@RequestMapping(value = "ProjectTestPlanDetails.htm")
	public String projectTestPlanDetails(HttpServletRequest req, HttpSession ses, HttpServletResponse res,
			RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String LoginType = (String) ses.getAttribute("LoginType");
		logger.info(new Date() + "Inside ProjectTestPlanDetails.htm" + UserId);
		try {
			String testPlanInitiationId = req.getParameter("testPlanInitiationId");
			String projectType = null;
			String projectId = null;
			String initiationId = null;
			String productTreeMainId = null;
			TestPlanInitiation testPlanInitiation = null;
			if (!testPlanInitiationId.equals("0")) {
				testPlanInitiation = service.getTestPlanInitiationById(testPlanInitiationId);
				projectId = testPlanInitiation.getProjectId().toString();
				initiationId = testPlanInitiation.getInitiationId().toString();
				productTreeMainId = testPlanInitiation.getProductTreeMainId().toString();
				projectType = projectId.equals("0") ? "I" : "M";

				testPlanInitiationId = service.getFirstVersionTestPlanInitiationId(initiationId, projectId,
						productTreeMainId) + "";
			} else {
				projectType = req.getParameter("projectType");
				projectId = req.getParameter("projectId");
				initiationId = req.getParameter("initiationId");
				productTreeMainId = req.getParameter("productTreeMainId");

				initiationId = initiationId != null && !initiationId.isEmpty() ? initiationId : "0";
				projectId = projectId != null && !projectId.isEmpty() ? projectId : "0";
				productTreeMainId = productTreeMainId != null && !productTreeMainId.isEmpty() ? productTreeMainId : "0";
			}

			projectType = projectType == null ? (projectId.equals("0") ? "I" : "M") : projectType;

			req.setAttribute("projectType", projectType);
			req.setAttribute("projectId", projectId);
			req.setAttribute("initiationId", initiationId);
			req.setAttribute("productTreeMainId", productTreeMainId);
			req.setAttribute("testPlanInitiationId", testPlanInitiationId);

			req.setAttribute("AbbreviationDetails", service.AbbreviationDetails(testPlanInitiationId, "0"));
			req.setAttribute("MemberList", service.DocMemberList(testPlanInitiationId, "0"));
			req.setAttribute("EmployeeList", projectservice.EmployeeList1(LabCode, testPlanInitiationId, "0"));
			req.setAttribute("TotalEmployeeList", projectservice.EmployeeList(LabCode));
			req.setAttribute("LabList", projectservice.LabListDetails(LabCode));
			req.setAttribute("DocumentSummary", service.getTestandSpecsDocumentSummary(testPlanInitiationId, "0"));
			req.setAttribute("TestContent", service.GetTestContentList(testPlanInitiationId));

			Object[] projectDetails = projectservice.getProjectDetails(LabCode,
					projectType.equalsIgnoreCase("M") ? projectId : initiationId,
					projectType.equalsIgnoreCase("M") ? "E" : "P");
			req.setAttribute("projectDetails", projectDetails);
			req.setAttribute("DocTempAttributes", projectservice.DocTempAttributes());
			req.setAttribute("projectShortName", projectDetails != null ? projectDetails[2] : "");
			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(LabCode));
			req.setAttribute("drdologo", LogoUtil.getDRDOLogoAsBase64String());
			req.setAttribute("version", testPlanInitiation != null ? testPlanInitiation.getTestPlanVersion() : "1.0");
			req.setAttribute("TestScopeIntro", service.TestScopeIntro(testPlanInitiationId));
			req.setAttribute("TestSuite", service.TestTypeList());
			req.setAttribute("TestDetailsList", service.TestDetailsList(testPlanInitiationId));
		List<TestSetupMaster>master = service.getTestSetupMaster();
			
			if(master!=null && master.size()>0) {
				master = master.stream().filter(e->e.getIsActive()==1).collect(Collectors.toList());
			}
			req.setAttribute("testSetupMasterMaster", master);
			String specsInitiationId = service.getFirstVersionSpecsInitiationId(initiationId, projectId,
					productTreeMainId) + "";
			req.setAttribute("specificationList", service.getSpecsList(specsInitiationId));
			req.setAttribute("StagesApplicable", service.StagesApplicable());
			req.setAttribute("isPdf", req.getParameter("isPdf"));

			List<Object[]> acceptanceTestingList = service.GetAcceptanceTestingList(testPlanInitiationId);

			if (acceptanceTestingList != null && !acceptanceTestingList.isEmpty()) {
				for (Object[] obj : acceptanceTestingList) {
					String fileName = (obj != null && obj[3] != null) ? obj[3].toString() : null;
					String filePath = (obj != null && obj[4] != null) ? obj[4].toString() : null;

					if (fileName != null && filePath != null && obj[1] != null && obj[2] != null) {
						Path fileFullPath = Paths.get(uploadpath, filePath, fileName);
						File my_file = fileFullPath.toFile();

						if (my_file.exists()) {
							try (FileInputStream fis = new FileInputStream(my_file)) {
								String htmlContent = convertExcelToHtml(fis);
								String category = obj[1].toString();

								switch (category) {
								case "Test Set UP":
									req.setAttribute("htmlContentTestSetUp", htmlContent);
									req.setAttribute("TestSetUp", obj[2].toString());
									break;
								case "Test Set Up Diagram":
									req.setAttribute("htmlContentTestSetUpDiagram", htmlContent);
									req.setAttribute("TestSetUpDiagram", obj[2].toString());
									break;
								case "Testing tools":
									req.setAttribute("htmlContentTestingtools", htmlContent);
									req.setAttribute("Testingtools", obj[2].toString());
									break;
								case "Test Verification":
									req.setAttribute("htmlContentTestVerification", htmlContent);
									req.setAttribute("TestVerification", obj[2].toString());
									break;
								case "Role & Responsibility":
									req.setAttribute("htmlContentRoleResponsibility", htmlContent);
									req.setAttribute("RoleResponsibility", obj[2].toString());
									break;
								default:
									logger.warn("Unrecognized category: {}", category);
								}
							} catch (IOException e) {
								logger.error("Error processing file: {}", my_file.getAbsolutePath(), e);
							}
						} else {
							logger.warn("File does not exist: {}", my_file.getAbsolutePath());
						}
					}
				}
			}

			req.setAttribute("AcceptanceTesting", acceptanceTestingList);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside ProjectTestPlanDetails.htm " + UserId, e);
			return "static/Error";
		}
		return "requirements/ProjectTestPlanDetails";
	}

	@RequestMapping(value = "AbbreviationExcelUploads.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String DivisionmasterExcelUpload(RedirectAttributes redir, HttpServletRequest req, HttpServletResponse res,
			HttpSession ses) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside ExcelUploads.htm " + UserId);
		try {
			String action = req.getParameter("Action");
			String initiationId = req.getParameter("initiationId");
			String projectId = req.getParameter("projectId");
			String productTreeMainId = req.getParameter("productTreeMainId");
			String testPlanInitiationId = req.getParameter("testPlanInitiationId");
			String SpecsInitiationId = req.getParameter("SpecsInitiationId");

			if (initiationId == null)
				initiationId = "0";
			if (projectId == null)
				projectId = "0";
			if (productTreeMainId == null)
				productTreeMainId = "0";

			if ("GenerateExcel".equalsIgnoreCase(action)) {
				XSSFWorkbook workbook = new XSSFWorkbook();
				XSSFSheet sheet = workbook.createSheet("Abbreviation Details");
				XSSFRow row = sheet.createRow(0);
				CellStyle unlockedCellStyle = workbook.createCellStyle();
				unlockedCellStyle.setLocked(true);

				row.createCell(0).setCellValue("SN");
				sheet.setColumnWidth(0, 5000);
				row.createCell(1).setCellValue("Abbreviation");
				sheet.setColumnWidth(1, 5000);
				row.createCell(2).setCellValue("Meaning");
				sheet.setColumnWidth(2, 5000);

				int r = 0;

				row = sheet.createRow(++r);
				row.createCell(0).setCellValue(String.valueOf(r));
				row.createCell(1).setCellValue("");
				row.createCell(2).setCellValue("");

				res.setContentType("application/vnd.ms-excel");
				res.setHeader("Content-Disposition", "attachment; filename=Abbreviation Details.xls");
				workbook.write(res.getOutputStream());
			} else if ("UploadExcel".equalsIgnoreCase(action)) {
				if (req.getContentType() != null && req.getContentType().startsWith("multipart/")) {
					Part filePart = req.getPart("filename");
					List<Abbreviations> iaList = new ArrayList<>();
					InputStream fileData = filePart.getInputStream();
					Workbook workbook = new XSSFWorkbook(fileData);
					Sheet sheet = workbook.getSheetAt(0);
					int rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();

					if (SpecsInitiationId == null) {
						if (testPlanInitiationId.equals("0")) {
							testPlanInitiationId = Long.toString(service.testPlanInitiationAddHandling(initiationId,
									projectId, productTreeMainId, EmpId, UserId, null, null));
						}
					}
					if (testPlanInitiationId == null) {
						if (SpecsInitiationId.equals("0")) {
							SpecsInitiationId = Long.toString(service.SpecificationInitiationAddHandling(initiationId,
									projectId, productTreeMainId, EmpId, UserId, null, null));
						}
					}

					for (int i = 1; i <= rowCount; i++) {

						int cellcount = sheet.getRow(i).getLastCellNum();
						Abbreviations iA = new Abbreviations();

						for (int j = 1; j < cellcount; j++) {

							if (sheet.getRow(i).getCell(j) != null) {
								if (j == 1) {
									switch (sheet.getRow(i).getCell(j).getCellType()) {
									case BLANK:
										break;
									case NUMERIC:
										iA.setAbbreviations(String
												.valueOf((long) sheet.getRow(i).getCell(j).getNumericCellValue()));
										break;
									case STRING:
										iA.setAbbreviations(sheet.getRow(i).getCell(j).getStringCellValue());
										break;
									}
								}

								if (j == 2) {
									switch (sheet.getRow(i).getCell(j).getCellType()) {
									case BLANK:
										break;
									case NUMERIC:
										iA.setMeaning(String
												.valueOf((long) sheet.getRow(i).getCell(j).getNumericCellValue()));
										break;
									case STRING:
										iA.setMeaning(sheet.getRow(i).getCell(j).getStringCellValue());
										break;
									}

								}
							}
						}

						// iA.setInitiationId(Long.parseLong(initiationId));
						// iA.setProjectId(Long.parseLong(projectId));//bharath
						iA.setTestPlanInitiationId(
								testPlanInitiationId == null ? 0L : Long.parseLong(testPlanInitiationId));
						iA.setSpecsInitiationId(SpecsInitiationId == null ? 0L : Long.parseLong(SpecsInitiationId));
						iA.setAbbreviationType("T");
						if (iA.getAbbreviations() != null && iA.getMeaning() != null) {
							iaList.add(iA);
						}

					}
					long Count = service.addAbbreviations(iaList);
					if (Count > 0) {
						redir.addAttribute("result", "Abbreviations Added Successfully");
					} else {
						redir.addAttribute("resultfail", "Abbreviations Add UnSuccessfully");// bharath
					}
					redir.addAttribute("initiationId", initiationId);
					redir.addAttribute("projectId", projectId);
					redir.addAttribute("productTreeMainId", productTreeMainId);
					if (SpecsInitiationId == null) {
						redir.addAttribute("testPlanInitiationId", testPlanInitiationId);
						return "redirect:/ProjectTestPlanDetails.htm";
					} else {
						redir.addAttribute("SpecsInitiationId", SpecsInitiationId);
						return "redirect:/ProjectSpecificationDetails.htm";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			redir.addAttribute("resultfail", " Adding Unsuccessfully");
			logger.error(new Date() + "Inside ExcelUpload.htm " + UserId, e);// bharath
		}
		return "static/Error";
	}
	@RequestMapping(value = "MemberSubmit.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String RequirementMemberSubmit(RedirectAttributes redir, HttpServletRequest req, HttpServletResponse res,
			HttpSession ses) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		String Logintype = (String) ses.getAttribute("LoginType");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside RequirementMemberSubmit.htm " + UserId);
		try {
			String initiationId = req.getParameter("initiationId");
			String projectId = req.getParameter("projectId");
			String productTreeMainId = req.getParameter("productTreeMainId");
			String testPlanInitiationId = req.getParameter("testPlanInitiationId");
			String SpecsInitiationId = req.getParameter("SpecsInitiationId");
			if (initiationId == null)
				initiationId = "0";
			if (projectId == null)
				projectId = "0";
			if (productTreeMainId == null)
				productTreeMainId = "0";

			if (SpecsInitiationId == null) {
				if (testPlanInitiationId.equals("0")) {
					testPlanInitiationId = Long.toString(service.testPlanInitiationAddHandling(initiationId, projectId,
							productTreeMainId, EmpId, UserId, null, null));
				}
			}
			if (testPlanInitiationId == null) {
				if (SpecsInitiationId.equals("0")) {
					SpecsInitiationId = Long.toString(service.SpecificationInitiationAddHandling(initiationId,
							projectId, productTreeMainId, EmpId, UserId, null, null));
				}
			}

			String[] Assignee = req.getParameterValues("Assignee");
			DocMembers rm = new DocMembers();
			// rm.setProjectId(Long.parseLong(projectId));
			// rm.setInitiationId(Long.parseLong(initiationId));
			rm.setTestPlanInitiationId(testPlanInitiationId == null ? 0L : Long.parseLong(testPlanInitiationId));
			rm.setCreatedBy(UserId);
			rm.setCreatedDate(sdf1.format(new Date()));
			rm.setEmps(Assignee);
			rm.setMemeberType("T");
			rm.setSpecsInitiationId(SpecsInitiationId == null ? 0L : Long.parseLong(SpecsInitiationId));
			long count = service.AddDocMembers(rm);
			if (count > 0) {
				redir.addAttribute("result", "Members Added Successfully for Document Distribution");
			} else {
				redir.addAttribute("resultfail", "Document Summary adding unsuccessful ");
			}

			redir.addAttribute("initiationId", initiationId);
			redir.addAttribute("projectId", projectId);
			redir.addAttribute("productTreeMainId", productTreeMainId);
			if (SpecsInitiationId == null) {
				redir.addAttribute("testPlanInitiationId", testPlanInitiationId);
				return "redirect:/ProjectTestPlanDetails.htm";
			} else {
				redir.addAttribute("SpecsInitiationId", SpecsInitiationId);
				return "redirect:/ProjectSpecificationDetails.htm";
			}
		} catch (Exception e) {
			e.printStackTrace();

			logger.info(new Date() + "Inside RequirementMemberSubmit.htm " + UserId);
		}
		return "static/Error";
	}

	@RequestMapping(value = "TestScope.htm", method = { RequestMethod.GET, RequestMethod.POST })
	public String ProjectReqIntroduction(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() + "Inside TestScope.htm " + UserId);

		try {
			String projectId = req.getParameter("projectId");
			String initiationId = req.getParameter("initiationId");
			req.setAttribute("initiationId", initiationId);
			req.setAttribute("projectId", projectId);
			req.setAttribute("productTreeMainId", req.getParameter("productTreeMainId"));
			req.setAttribute("testPlanInitiationId", req.getParameter("testPlanInitiationId"));

			req.setAttribute("attributes",
					req.getParameter("attributes") == null ? "Introduction" : req.getParameter("attributes"));
			req.setAttribute("projectDetails", projectservice.getProjectDetails(LabCode,
					!projectId.equals("0") ? projectId : initiationId, !projectId.equals("0") ? "E" : "P"));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside TestScope.htm " + UserId, e);
		}
		return "requirements/TestScope";
	}

	@RequestMapping(value = "TestScopeSubmit.htm", method = { RequestMethod.GET, RequestMethod.POST })
	public String RequiremnetIntroSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) {
		String UserId = (String) ses.getAttribute("Username");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside RequiremnetIntroSubmit.htm " + UserId);
		try {
			String initiationId = req.getParameter("initiationId");
			String projectId = req.getParameter("projectId");
			String productTreeMainId = req.getParameter("productTreeMainId");
			String testPlanInitiationId = req.getParameter("testPlanInitiationId");

			if (initiationId == null)
				initiationId = "0";
			if (projectId == null)
				projectId = "0";
			if (productTreeMainId == null)
				productTreeMainId = "0";

			if (testPlanInitiationId.equals("0")) {
				testPlanInitiationId = Long.toString(service.testPlanInitiationAddHandling(initiationId, projectId,
						productTreeMainId, EmpId, UserId, null, null));
			}

			String attributes = req.getParameter("attributes");
			String Details = req.getParameter("Details");
			Object[] TestScopeInto = service.TestScopeIntro(testPlanInitiationId);
			long count = 0l;
			if (TestScopeInto == null) {
				// this is for add
				count = service.TestScopeIntroSubmit(testPlanInitiationId, attributes, Details, UserId);
			} else {
				// this is for edit
				count = service.TestScopeUpdate(testPlanInitiationId, attributes, Details, UserId);

			}
			if (count > 0) {
				redir.addAttribute("result", attributes + " updated Successfully");
			} else {
				redir.addAttribute("resultfail", attributes + " update Unsuccessful");
			}
			redir.addAttribute("initiationId", initiationId);
			redir.addAttribute("projectId", projectId);
			redir.addAttribute("productTreeMainId", productTreeMainId);
			redir.addAttribute("testPlanInitiationId", testPlanInitiationId);
			redir.addAttribute("attributes", req.getParameter("attributes"));
			return "redirect:/TestScope.htm";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "static/Error";
	}

	@RequestMapping(value = "TestScopeIntroAjax.htm", method = { RequestMethod.GET })
	public @ResponseBody String RequirementIntroDetails(HttpServletRequest req, HttpSession ses) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside RequirementIntro.htm " + UserId);
		Object[] TestScopeIntroDetails = null;
		try {
			String testPlanInitiationId = req.getParameter("testPlanInitiationId");
			// String projectId=req.getParameter("projectId");
			// String ProjectType=req.getParameter("ProjectType");

			TestScopeIntroDetails = service.TestScopeIntro(testPlanInitiationId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside TestScopeIntroAjax.htm" + UserId, e);
		}
		Gson json = new Gson();
		return json.toJson(TestScopeIntroDetails);
	}

	@RequestMapping(value = "TestDocumentDownlod.htm")
	public String testDocumentDownlod(HttpServletRequest req, HttpSession ses, HttpServletResponse res,
			RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() + "Inside RequirementDocumentDownlod.htm " + UserId);
		try {
			Object[] DocTempAttributes = null;
			DocTempAttributes = projectservice.DocTempAttributes();
			req.setAttribute("DocTempAttributes", DocTempAttributes);

			String testPlanInitiationId = req.getParameter("testPlanInitiationId");

		

			TestPlanInitiation testPlanInitiation = service.getTestPlanInitiationById(testPlanInitiationId);
			if (testPlanInitiation != null) {
				testPlanInitiationId = service.getFirstVersionTestPlanInitiationId(
						testPlanInitiation.getInitiationId() + "", testPlanInitiation.getProjectId() + "",
						testPlanInitiation.getProductTreeMainId() + "") + "";
			}

			String filename = "ProjectRequirement";
			String path = req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path", path);
			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(LabCode));
			req.setAttribute("LabImage", LogoUtil.getLabImageAsBase64String(LabCode));
			req.setAttribute("LabList", projectservice.LabListDetails(LabCode));
			req.setAttribute("uploadpath", uploadpath);
			req.setAttribute("TestScopeIntro", service.TestScopeIntro(testPlanInitiationId));
			req.setAttribute("MemberList", service.DocMemberList(testPlanInitiationId, "0"));
			req.setAttribute("TestDocumentSummary", service.getTestandSpecsDocumentSummary(testPlanInitiationId, "0"));
			req.setAttribute("AbbreviationDetails", service.AbbreviationDetails(testPlanInitiationId, "0"));
			req.setAttribute("TestContent", service.GetTestContentList(testPlanInitiationId));
			req.setAttribute("AcceptanceTesting", service.GetAcceptanceTestingList(testPlanInitiationId));
			req.setAttribute("TestSuite", service.TestTypeList());
			req.setAttribute("TestDetailsList", service.TestDetailsList(testPlanInitiationId));
			req.setAttribute("TestTypeList", service.TestTypeList());
			req.setAttribute("StagesApplicable", service.StagesApplicable());

			File my_file = null;
			File my_file1 = null;
			File my_file2 = null;
			File my_file3 = null;
			File my_file4 = null;

			List<Object[]> list = service.GetAcceptanceTestingList(testPlanInitiationId);
			String TestSetUp = null;
			String TestSetUpDiagram = null;
			String Testingtools = null;
			String TestVerification = null;
			String RoleResponsibility = null;

			for (Object[] obj : list) {
				if (obj[1].toString().equalsIgnoreCase("Test Set UP")) {
					TestSetUp = obj[2].toString();
					my_file = new File(uploadpath + obj[4] + File.separator + obj[3]);
					if (my_file != null) {
						String htmlContent = convertExcelToHtml(new FileInputStream(my_file));
						req.setAttribute("htmlContent", htmlContent);
						req.setAttribute("TestSetUp", TestSetUp);
					}
				}
				if (obj[1].toString().equalsIgnoreCase("Test Set Up Diagram")) {

					TestSetUpDiagram = obj[2].toString();
					my_file1 = new File(uploadpath + obj[4] + File.separator + obj[3]);
					if (my_file1 != null) {
						String htmlContentTestSetUpDiagram = convertExcelToHtml(new FileInputStream(my_file1));
						req.setAttribute("htmlContentTestSetUpDiagram", htmlContentTestSetUpDiagram);
						req.setAttribute("TestSetUpDiagram", TestSetUpDiagram);

					}
				}
				if (obj[1].toString().equalsIgnoreCase("Testing tools")) {
					Testingtools = obj[2].toString();
					my_file2 = new File(uploadpath + obj[4] + File.separator + obj[3]);
					if (my_file2 != null) {
						String htmlContentTestingtools = convertExcelToHtml(new FileInputStream(my_file2));
						req.setAttribute("htmlContentTestingtools", htmlContentTestingtools);
						req.setAttribute("Testingtools", Testingtools);

					}
				}
				if (obj[1].toString().equalsIgnoreCase("Test Verification")) {
					TestVerification = obj[2].toString();
					my_file3 = new File(uploadpath + obj[4] + File.separator + obj[3]);
					if (my_file3 != null) {
						String htmlContentTestVerification = convertExcelToHtml(new FileInputStream(my_file3));
						req.setAttribute("htmlContentTestVerification", htmlContentTestVerification);
						req.setAttribute("TestVerification", TestVerification);
					}
				}
				if (obj[1].toString().equalsIgnoreCase("Role & Responsibility")) {
					RoleResponsibility = obj[2].toString();
					my_file4 = new File(uploadpath + obj[4] + File.separator + obj[3]);
					if (my_file4 != null) {
						String htmlContentRoleResponsibility = convertExcelToHtml(new FileInputStream(my_file4));
						req.setAttribute("htmlContentRoleResponsibility", htmlContentRoleResponsibility);
						req.setAttribute("RoleResponsibility", RoleResponsibility);
					
					}
				}

			}
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/print/TestPlanDownload.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside TestPlanDownload.jsp " + UserId, e);
		}
		return "print/TestPlanDownload";
	}

    public static String convertExcelToHtml(InputStream inputStream) throws Exception {
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        StringBuilder htmlContent = new StringBuilder(
            "<table class='table table-bordered htmlTable' border='1' style='border-collapse: collapse;'>");

        DataFormatter formatter = new DataFormatter();

        for (Row row : sheet) {
            if (isRowEmpty(row)) continue;

            htmlContent.append("<tr>");
            for (Cell cell : row) {
                String cellValue;
                String textAlign = "center"; // default alignment

                switch (cell.getCellType()) {
                    case STRING:
                        cellValue = cell.getStringCellValue();
                        textAlign = "center";
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            cellValue = formatter.formatCellValue(cell); // Date format
                            textAlign = "center";
                        } else {
                            double value = cell.getNumericCellValue();
                            cellValue = (value == Math.floor(value))
                                ? String.valueOf((long) value)
                                : String.valueOf(value);
                            textAlign = "right";
                        }
                        break;
                    case BOOLEAN:
                        cellValue = String.valueOf(cell.getBooleanCellValue());
                        textAlign = "center";
                        break;
                    case FORMULA:
                        cellValue = formatter.formatCellValue(cell, workbook.getCreationHelper().createFormulaEvaluator());
                        textAlign = "center";
                        break;
                    case BLANK:
                        cellValue = "";
                        textAlign = "center";
                        break;
                    default:
                        cellValue = cell.toString();
                        textAlign = "center";
                }

                String bgColorStyle = "background-color: white;";
                if (cell.getCellStyle() instanceof XSSFCellStyle) {
                    XSSFCellStyle style = (XSSFCellStyle) cell.getCellStyle();
                    XSSFColor color = style.getFillForegroundColorColor();
                    if (color != null) {
                        String hex = color.getARGBHex();
                        if (hex != null && hex.length() == 8) {
                            String rgb = "#" + hex.substring(2);
                            bgColorStyle = "background-color:" + rgb + ";";
                        }
                    }
                }

                htmlContent.append("<td style='border:1px solid black;")
                           .append(bgColorStyle)
                           .append("text-align:").append(textAlign).append(";'>")
                           .append(cellValue)
                           .append("</td>");
            }
            htmlContent.append("</tr>");
        }

        htmlContent.append("</table>");
        workbook.close();
        return htmlContent.toString();
    }

	public static String convertExcelToHtml1(InputStream inputStream) throws Exception {
		Workbook workbook = new XSSFWorkbook(inputStream);
		Sheet sheet = workbook.getSheetAt(0); // Assuming you are working with the first sheet

		StringBuilder htmlContent = new StringBuilder("<table class='table' border='1' style='border-collapse: collapse;'>");

		for (Row row : sheet) {
			// Skip rows with all cells blank
			if (isRowEmpty(row)) {
				continue;
			}

			htmlContent.append("<tr style='border:1px solid black;'>");
			for (Cell cell : row) {
				htmlContent.append("<td style='border:1px solid black;'>").append(cell.toString()).append("</td>");
			}
			htmlContent.append("</tr>");
		}

		htmlContent.append("</table>");

		workbook.close();

		return htmlContent.toString();
	}

	private static boolean isRowEmpty(Row row) {
		for (Cell cell : row) {
			if (cell.getCellType() != CellType.BLANK) {
				return false;
			}
		}
		return true;
	}

	@RequestMapping(value = "TestPlanSummaryAdd.htm", method = RequestMethod.POST)
	public String RequirementSummaryAdd(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
			throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		String Logintype = (String) ses.getAttribute("LoginType");

		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside TestPlanSummaryAdd.htm " + UserId);
		try {
			String initiationId = req.getParameter("initiationId");
			String projectId = req.getParameter("projectId");
			String productTreeMainId = req.getParameter("productTreeMainId");
			String testPlanInitiationId = req.getParameter("testPlanInitiationId");

			if (initiationId == null)
				initiationId = "0";
			if (projectId == null)
				projectId = "0";
			if (productTreeMainId == null)
				productTreeMainId = "0";

			if (testPlanInitiationId.equals("0")) {
				testPlanInitiationId = Long.toString(service.testPlanInitiationAddHandling(initiationId, projectId,
						productTreeMainId, EmpId, UserId, null, null));
			}

			String action = req.getParameter("action");

			TestPlanSummary rs = action != null && action.equalsIgnoreCase("Add") ? new TestPlanSummary()
					: service.getTestPlanSummaryById(req.getParameter("summaryId"));
			rs.setAbstract(req.getParameter("abstract"));
			rs.setAdditionalInformation(req.getParameter("information"));
			rs.setKeywords(req.getParameter("keywords"));
			rs.setDistribution(req.getParameter("distribution"));
			rs.setApprover(Long.parseLong(req.getParameter("approver")));
			;
			rs.setReviewer(req.getParameter("reviewer"));
			rs.setPreparedBy(req.getParameter("preparedBy"));
			rs.setTestPlanInitiationId(Long.parseLong(testPlanInitiationId));
			rs.setSpecsInitiationId(0L);
			rs.setReleaseDate(req.getParameter("pdc"));
			rs.setDocumentNo(req.getParameter("Document"));
			if (action.equalsIgnoreCase("Add")) {
				rs.setCreatedBy(UserId);
				rs.setCreatedDate(sdf1.format(new Date()));
				rs.setIsActive(1);
			} else if (action.equalsIgnoreCase("Edit")) {

				rs.setSummaryId(Long.parseLong(req.getParameter("summaryId")));
				rs.setModifiedBy(UserId);
				rs.setModifiedDate(sdf1.format(new Date()));
			}

			long count = service.addTestPlanSummary(rs);
			if (count > 0) {
				redir.addAttribute("result", "Document Summary " + action + "ed successfully ");
			} else {
				redir.addAttribute("resultfail", "Document Summary " + action + " unsuccessful ");
			}

			redir.addAttribute("initiationId", initiationId);
			redir.addAttribute("projectId", projectId);
			redir.addAttribute("productTreeMainId", productTreeMainId);
			redir.addAttribute("testPlanInitiationId", testPlanInitiationId);
			return "redirect:/ProjectTestPlanDetails.htm";

		} catch (Exception e) {
			logger.info(new Date() + "Inside TestPlanSummaryAdd.htm " + UserId);
			return "static/Error";
		}

	}

	@RequestMapping(value = "TestApprochAdd.htm", method = RequestMethod.POST)
	public String TestApprochAdd(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		String Logintype = (String) ses.getAttribute("LoginType");

		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside TestApprochAdd.htm " + UserId);
		try {
			String initiationId = req.getParameter("initiationId");
			String projectId = req.getParameter("projectId");
			String productTreeMainId = req.getParameter("productTreeMainId");
			String testPlanInitiationId = req.getParameter("testPlanInitiationId");

			if (initiationId == null)
				initiationId = "0";
			if (projectId == null)
				projectId = "0";
			if (productTreeMainId == null)
				productTreeMainId = "0";

			if (testPlanInitiationId.equals("0")) {
				testPlanInitiationId = Long.toString(service.testPlanInitiationAddHandling(initiationId, projectId,
						productTreeMainId, EmpId, UserId, null, null));
			}

			TestApproach rs = new TestApproach();
			// rs.setProjectId(Long.parseLong(projectId));
			// rs.setInitiationId(Long.parseLong(initiationId));
			rs.setTestPlanInitiationId(Long.parseLong(testPlanInitiationId));
			rs.setTestApproach(req.getParameter("TestApproach"));
			rs.setCreatedBy(UserId);
			rs.setCreatedDate(sdf1.format(new Date()));
			rs.setIsActive(1);
			String action = req.getParameter("btn");
			req.setAttribute("attributes",
					req.getParameter("attributes") == null ? "Introduction" : req.getParameter("attributes"));

			long count = 0l;
			if (action.equalsIgnoreCase("submit")) {
				count = service.addTestApproch(rs);
			}
			if (count > 0) {
				redir.addAttribute("result", "Test Approach added successfully ");
			} else {
				redir.addAttribute("resultfail", "Test Approach add unsuccessful ");
			}

			redir.addAttribute("initiationId", initiationId);
			redir.addAttribute("projectId", projectId);
			redir.addAttribute("productTreeMainId", productTreeMainId);
			redir.addAttribute("testPlanInitiationId", testPlanInitiationId);
			return "redirect:/ProjectTestPlanDetails.htm";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside Test ApproachAdd.htm " + UserId);
			return "static/Error";
		}
	}

	@RequestMapping(value = "TestDocContentSubmit.htm", method = { RequestMethod.GET, RequestMethod.POST })
	public String TestDocContentSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) {
		String UserId = (String) ses.getAttribute("Username");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside TestDocContentSubmit.htm " + UserId);
		try {
			String initiationId = req.getParameter("initiationId");
			String projectId = req.getParameter("projectId");
			String productTreeMainId = req.getParameter("productTreeMainId");
			String testPlanInitiationId = req.getParameter("testPlanInitiationId");

			if (initiationId == null)
				initiationId = "0";
			if (projectId == null)
				projectId = "0";
			if (productTreeMainId == null)
				productTreeMainId = "0";

			if (testPlanInitiationId.equals("0")) {
				testPlanInitiationId = Long.toString(service.testPlanInitiationAddHandling(initiationId, projectId,
						productTreeMainId, EmpId, UserId, null, null));
			}

			String attributes = req.getParameter("attributes");
			String Details = req.getParameter("Details");
			String action = req.getParameter("Action");
			String UpdateAction = req.getParameter("UpdateAction");

			long count = 0l;
			if ("add".equalsIgnoreCase(action)) {
				count = service.TestDocContentSubmit(testPlanInitiationId, attributes, Details, UserId);
			} else {
				// this is for edit
				count = service.TestDocContentUpdate(UpdateAction, Details, UserId);
			}
			if (count > 0) {
				redir.addAttribute("result", attributes + " Submitted  Successfully");
			} else {
				redir.addAttribute("resultfail", attributes + " Submit Unsuccessful");
			}

			redir.addAttribute("attributes", req.getParameter("attributes"));
			redir.addAttribute("initiationId", initiationId);
			redir.addAttribute("projectId", projectId);
			redir.addAttribute("productTreeMainId", productTreeMainId);
			redir.addAttribute("testPlanInitiationId", testPlanInitiationId);
			return "redirect:/ProjectTestPlanDetails.htm";

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside TestDocContentSubmit.htm " + UserId);
			return "static/Error";
		}

	}

	@RequestMapping(value = "AccceptanceTesting.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String RequirementAppendix(RedirectAttributes redir, HttpServletRequest req, HttpServletResponse res,
			HttpSession ses) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() + "Inside AccceptanceTesting.htm " + UserId);
		try {
			String initiationId = req.getParameter("initiationId");
			String projectId = req.getParameter("projectId");
			req.setAttribute("initiationId", initiationId);
			req.setAttribute("projectId", projectId);
			req.setAttribute("productTreeMainId", req.getParameter("productTreeMainId"));
			req.setAttribute("testPlanInitiationId", req.getParameter("testPlanInitiationId"));

			req.setAttribute("AcceptanceTesting",
					service.GetAcceptanceTestingList(req.getParameter("testPlanInitiationId")));
			req.setAttribute("projectDetails", projectservice.getProjectDetails(LabCode,
					!projectId.equals("0") ? projectId : initiationId, !projectId.equals("0") ? "E" : "P"));

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside AccceptanceTesting.htm " + UserId, e);
		}
		return "requirements/AcceptanceTesting";
	}

	@RequestMapping(value = "AcceptanceTestingUpload.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String TestVerificationUpload(HttpServletRequest req, HttpSession ses, RedirectAttributes redir,
			@RequestParam("filenameC") MultipartFile FileAttach) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		String Logintype = (String) ses.getAttribute("LoginType");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside AcceptanceTestingUpload.htm " + UserId);
		try {
			String initiationId = req.getParameter("initiationId");
			String projectId = req.getParameter("projectId");
			String productTreeMainId = req.getParameter("productTreeMainId");
			String testPlanInitiationId = req.getParameter("testPlanInitiationId");

			if (initiationId == null)
				initiationId = "0";
			if (projectId == null)
				projectId = "0";
			if (productTreeMainId == null)
				productTreeMainId = "0";

			if (testPlanInitiationId.equals("0")) {
				testPlanInitiationId = Long.toString(service.testPlanInitiationAddHandling(initiationId, projectId,
						productTreeMainId, EmpId, UserId, null, null));
			}

			String attributes = req.getParameter("attributes");
			String Details = req.getParameter("Details");
			String action = req.getParameter("Action");
			String UpdateActionid = req.getParameter("UpdateActionid");
			String filename = req.getParameter("filenameC");

			TestAcceptance re = new TestAcceptance();

			re.setAttributes(attributes);
			re.setAttributesDetailas(Details);
			// re.setInitiationId(Long.parseLong(initiationId));//bharath
			// re.setProjectId(Long.parseLong(projectId));
			re.setTestPlanInitiationId(Long.parseLong(testPlanInitiationId));
			re.setFile(FileAttach);

			long count = 0l;
			if ("add".equalsIgnoreCase(action)) {
				re.setCreatedBy(UserId);
				re.setCreatedDate(sdf1.format(new Date()));
				count = service.insertTestAcceptanceFile(re, LabCode);
			} else {
				count = service.TestAcceptancetUpdate(UpdateActionid, Details, UserId, FileAttach, LabCode);
			}
			if (count > 0) {
				redir.addAttribute("result", " Data & Document Uploaded Successfully");
			} else {
				redir.addAttribute("resultfail", "Data & Document Uploaded UnSuccessfully");
			}
			redir.addAttribute("initiationId", initiationId);
			redir.addAttribute("projectId", projectId);
			redir.addAttribute("productTreeMainId", productTreeMainId);
			redir.addAttribute("testPlanInitiationId", testPlanInitiationId);
			return "redirect:/AccceptanceTesting.htm";
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(new Date() + "Inside AcceptanceTestingUpload.htm.htm " + UserId);
			return "static/Error";
		}
	}

	@RequestMapping(value = "TestSetUp.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String AccronymsExcelUpload(RedirectAttributes redir, HttpServletRequest req, HttpServletResponse res,
			HttpSession ses) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() + "Inside ExcelUpload.htm " + UserId);

		try {
			String action = req.getParameter("Action");
			String initiationId = req.getParameter("initiationId");
			String projectId = req.getParameter("projectId");
			String ProjectType = req.getParameter("ProjectType");
			if ("GenerateExcel".equalsIgnoreCase(action)) {

				XSSFWorkbook workbook = new XSSFWorkbook();
				XSSFSheet sheet = workbook.createSheet("TestSetUp");
				XSSFRow row = sheet.createRow(0);

				CellStyle unlockedCellStyle = workbook.createCellStyle();
				unlockedCellStyle.setLocked(true);

				row.createCell(0).setCellValue("SN");
				sheet.setColumnWidth(0, 5000);
				row.createCell(1).setCellValue("Test Type");
				sheet.setColumnWidth(1, 5000);
				row.createCell(2).setCellValue("Test Setup Name");
				sheet.setColumnWidth(2, 5000);

				int r = 0;

				row = sheet.createRow(++r);
				row.createCell(0).setCellValue(String.valueOf(r));
				row.createCell(1).setCellValue("");
				row.createCell(2).setCellValue("");

				res.setContentType("application/vnd.ms-excel");
				res.setHeader("Content-Disposition", "attachment; filename=TestSetUp.xls");
				workbook.write(res.getOutputStream());

			}
			if ("GenerateExcelTestingTools".equalsIgnoreCase(action)) {

				XSSFWorkbook workbook = new XSSFWorkbook();
				XSSFSheet sheet = workbook.createSheet("TestingTools");
				XSSFRow row = sheet.createRow(0);

				CellStyle unlockedCellStyle = workbook.createCellStyle();
				unlockedCellStyle.setLocked(true);

				row.createCell(0).setCellValue("SN");
				sheet.setColumnWidth(0, 5000);
				row.createCell(1).setCellValue("Test Type");
				sheet.setColumnWidth(1, 5000);
				row.createCell(2).setCellValue("Test IDs");
				sheet.setColumnWidth(2, 5000);
				row.createCell(3).setCellValue("Test Tools");
				sheet.setColumnWidth(3, 5000);

				int r = 0;

				row = sheet.createRow(++r);
				row.createCell(0).setCellValue(String.valueOf(r));
				row.createCell(1).setCellValue("");
				row.createCell(2).setCellValue("");
				row.createCell(3).setCellValue("");
				res.setContentType("application/vnd.ms-excel");
				res.setHeader("Content-Disposition", "attachment; filename=TestingTools.xls");
				workbook.write(res.getOutputStream());

			}
			if ("GenerateExcelDiagram".equalsIgnoreCase(action)) {

				XSSFWorkbook workbook = new XSSFWorkbook();
				XSSFSheet sheet = workbook.createSheet("TestSetUpDiagram");
				XSSFRow row = sheet.createRow(0);

				CellStyle unlockedCellStyle = workbook.createCellStyle();
				unlockedCellStyle.setLocked(true);

				row.createCell(0).setCellValue("SN");
				sheet.setColumnWidth(0, 5000);
				row.createCell(1).setCellValue("Diagram");
				sheet.setColumnWidth(1, 5000);

				int r = 0;

				row = sheet.createRow(++r);
				row.createCell(0).setCellValue(String.valueOf(r));
				row.createCell(1).setCellValue("");
				

				res.setContentType("application/vnd.ms-excel");
				res.setHeader("Content-Disposition", "attachment; filename=TestSetUpDiagram.xls");
				workbook.write(res.getOutputStream());

			}
			if ("GenerateTestVerificationTable".equalsIgnoreCase(action)) {

				XSSFWorkbook workbook = new XSSFWorkbook();
				XSSFSheet sheet = workbook.createSheet("TestVerification");
				XSSFRow row = sheet.createRow(0);

				CellStyle unlockedCellStyle = workbook.createCellStyle();
				unlockedCellStyle.setLocked(true);

				row.createCell(0).setCellValue("SN");
				sheet.setColumnWidth(0, 5000);
				row.createCell(1).setCellValue("MOP Name");
				sheet.setColumnWidth(1, 5000);
				row.createCell(2).setCellValue("MOP ID");
				sheet.setColumnWidth(2, 5000);
				row.createCell(3).setCellValue("Sub Parameters");
				sheet.setColumnWidth(3, 5000);
				row.createCell(4).setCellValue("Applicability(Y/N)");
				sheet.setColumnWidth(4, 5000);
				row.createCell(5).setCellValue("Test Name");
				sheet.setColumnWidth(5, 5000);
				row.createCell(6).setCellValue("Test ID");
				sheet.setColumnWidth(6, 5000);
				row.createCell(7).setCellValue("Test Type");
				sheet.setColumnWidth(7, 5000);
				int r = 0;

				row = sheet.createRow(++r);
				row.createCell(0).setCellValue(String.valueOf(r));
				row.createCell(1).setCellValue("");
				row.createCell(2).setCellValue("");
				row.createCell(3).setCellValue("");
				row.createCell(4).setCellValue("");
				row.createCell(5).setCellValue("");
				row.createCell(6).setCellValue("");
				row.createCell(7).setCellValue("");
				res.setContentType("application/vnd.ms-excel");
				res.setHeader("Content-Disposition", "attachment; filename=TestVerification.xls");
				workbook.write(res.getOutputStream());

			}
			if ("GenerateTestRoleResponsibility".equalsIgnoreCase(action)) {

				XSSFWorkbook workbook = new XSSFWorkbook();
				XSSFSheet sheet = workbook.createSheet("RoleResponsibility");
				XSSFRow row = sheet.createRow(0);

				CellStyle unlockedCellStyle = workbook.createCellStyle();
				unlockedCellStyle.setLocked(true);

				row.createCell(0).setCellValue("SN");
				sheet.setColumnWidth(0, 5000);
				row.createCell(1).setCellValue("Test Name");
				sheet.setColumnWidth(1, 5000);
				row.createCell(2).setCellValue("Role & Responsibility/Test Id");
				sheet.setColumnWidth(2, 5000);
				row.createCell(3).setCellValue("Names of individual");
				sheet.setColumnWidth(3, 5000);
				int r = 0;
				row = sheet.createRow(++r);
				row.createCell(0).setCellValue(String.valueOf(r));
				row.createCell(1).setCellValue("");
				row.createCell(2).setCellValue("");
				row.createCell(3).setCellValue("");
				res.setContentType("application/vnd.ms-excel");
				res.setHeader("Content-Disposition", "attachment; filename=RoleResponsibility.xls");
				workbook.write(res.getOutputStream());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "static/Error";

	}

	@RequestMapping(value = { "TestSetupFileDownload.htm" })
	public void ProjectDataSystemSpecsFileDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res)
			throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside  TestSetupFileDownload" + UserId);
		try {
			String ftype = req.getParameter("filename");
			String Testid = req.getParameter("UpdateActionid");
			
			res.setContentType("Application/octet-stream");
			Object[] projectdatafiledata = service.AcceptanceTestingList(Testid);

			File my_file = null;

			my_file = new File(uploadpath + projectdatafiledata[4] + File.separator + projectdatafiledata[3]);
			res.setHeader("Content-disposition", "attachment; filename=" + projectdatafiledata[3].toString());
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
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside  TestSetupFileDownload" + UserId, e);
		}
	}

	@RequestMapping(value = "RequirementList.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String RequirementList(RedirectAttributes redir, HttpServletRequest req, HttpServletResponse res,
			HttpSession ses) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() + "Inside RequirementList.htm " + UserId);
		try {
			String initiationId = req.getParameter("initiationId");
			String projectId = req.getParameter("projectId");
			String productTreeMainId = req.getParameter("productTreeMainId");
			String reqInitiationId = req.getParameter("reqInitiationId");
			String project = req.getParameter("project");
			String InitiationReqId = req.getParameter("InitiationReqId");

			
			
			if (initiationId == null) {
				initiationId = "0";
			}
			if (projectId == null) {
				projectId = "0";
			}
			if (productTreeMainId == null) {
				productTreeMainId = "0";
			}
			
			
			
			
			req.setAttribute("initiationId", initiationId);
			req.setAttribute("projectId", projectId);
			req.setAttribute("productTreeMainId", productTreeMainId);
			req.setAttribute("reqInitiationId", reqInitiationId);
			req.setAttribute("project", project);
			List<Object[]> requirementTypeList = service.requirementTypeList(reqInitiationId);
			req.setAttribute("requirementTypeList", requirementTypeList);
			req.setAttribute("drdologo", LogoUtil.getDRDOLogoAsBase64String());
			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(LabCode));
			req.setAttribute("LabImage", LogoUtil.getLabImageAsBase64String(LabCode));
			req.setAttribute("LabList", projectservice.LabListDetails(LabCode));
			Object[] DocTempAttributes = null;
			DocTempAttributes = projectservice.DocTempAttributes();
			req.setAttribute("DocTempAttributes", DocTempAttributes);
			List<Object[]> RequirementList = service.RequirementList(reqInitiationId);

			req.setAttribute("RequirementList", RequirementList);

			if (InitiationReqId == null) {
				if (RequirementList != null && RequirementList.size() > 0) {
					InitiationReqId = RequirementList.get(0)[0].toString();
				} else {
					InitiationReqId = "0";
				}
			}

			req.setAttribute("InitiationReqId", InitiationReqId);
			req.setAttribute("subId", req.getParameter("subId"));
			req.setAttribute("VerificationMethodList", service.getVerificationMethodList());
			req.setAttribute("ProjectParaDetails", service.getProjectParaDetails(reqInitiationId));

			req.setAttribute("reqInitiation", service.getRequirementInitiationById(reqInitiationId));
			String type = projectId.equalsIgnoreCase("0") ? "P" : "E";
			
			req.setAttribute("projectDetails", projectservice.getProjectDetails(LabCode,
					type.equalsIgnoreCase("E") ? projectId : initiationId, type));
			List<Object[]> productTreeList = service.productTreeListByProjectId(projectId);
			req.setAttribute("productTreeList", productTreeList);
			
			if (!productTreeMainId.equalsIgnoreCase("0")) {
				Long firstReqInitiationId = service.getFirstVersionReqInitiationId(initiationId, projectId, "0");
				List<Object[]> RequirementMainList = service.RequirementList(firstReqInitiationId + "");
				final String productTreeMainIdfinal = productTreeMainId;
				List<Object[]> ReqSubSystemList = RequirementMainList!=null? RequirementMainList.stream()
						.filter(e->e[23]!=null&& Arrays.asList(e[23].toString().split(", ")).contains(productTreeMainIdfinal))
						.sorted(Comparator.comparing(e -> Integer.parseInt(e[14].toString())))
						.collect(Collectors.toList()):new ArrayList<>()  ;
				req.setAttribute("ReqSubSystemList", ReqSubSystemList);
				
				
				req.setAttribute("RequirementMainList", RequirementMainList);
			}
		}

		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside RequirementList.htm " + UserId, e);
		}
		return "requirements/RequirementList";
	}

	@RequestMapping(value = "RequirementAddList.htm", method = { RequestMethod.GET })
	public String RequirementAddList(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
			throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside RequirementAddList.htm ");

		try {
			String initiationId = req.getParameter("initiationId");
			String projectId = req.getParameter("projectId");
			String productTreeMainId = req.getParameter("productTreeMainId");
			String reqInitiationId = req.getParameter("reqInitiationId");
			String selectedValues = req.getParameter("selectedValues");

			if (productTreeMainId == null) {
				productTreeMainId = "0";
			}
			if (reqInitiationId.equals("0")) {
				reqInitiationId = Long.toString(service.requirementInitiationAddHandling(initiationId, projectId,
						productTreeMainId, EmpId, UserId, null, null));
			}
			long count = 0l;
			List<String> values = Arrays.asList(selectedValues.split(","));
			for (int i = 0; i < values.size(); i++) {
				PfmsInititationRequirement pir = new PfmsInititationRequirement();
				String[] valuesArray = values.get(i).split("/");
				// pir.setInitiationId(Long.parseLong(initiationId));
				// pir.setProjectId(Long.parseLong(projectId));
			
				pir.setCategory("N");
				pir.setNeedType("N");
				pir.setReqMainId(Long.parseLong(valuesArray[0]));
				pir.setRequirementBrief(valuesArray[1]);
				pir.setRequirementId(valuesArray[2]);
				pir.setReqTypeId(0l);
				pir.setLinkedDocuments("");
				pir.setLinkedPara("");
				pir.setLinkedRequirements("");
				pir.setCreatedBy(UserId);
				pir.setParentId(0l);
				pir.setReqInitiationId(Long.parseLong(reqInitiationId));
				count = service.addPfmsInititationRequirement(pir);
			}
			if (count > 0) {
				redir.addAttribute("result", "Requirement Details Added successfully");
			} else {
				redir.addAttribute("resultfail", "Requirement Details Added successfully");
			}

			redir.addAttribute("initiationId", initiationId);
			// redir.addAttribute("project", project);
			redir.addAttribute("projectId", projectId);
			// redir.addAttribute("InitiationReqId", InitiationReqId);
			redir.addAttribute("reqInitiationId", reqInitiationId);

			return "redirect:/RequirementList.htm";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside RequirementAddList.htm", e);
			return "static/Error";
		}

	}

	@RequestMapping(value = "RequirementEdit.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String RequirementEdit(RedirectAttributes redir, HttpServletRequest req, HttpServletResponse res,
			HttpSession ses) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() + "Inside RequirementEdit.htm " + UserId);
		try {
			String projectId = req.getParameter("projectId");
			String initiationid = req.getParameter("initiationid");
			String project = req.getParameter("project");
			String InitiationReqId = req.getParameter("InitiationReqId");
			String reqInitiationId = req.getParameter("reqInitiationId");

			String description = req.getParameter("description");
			String priority = req.getParameter("priority");
			String needtype = req.getParameter("needtype");

			PfmsInititationRequirement pir = new PfmsInititationRequirement();
			pir.setRequirementDesc(description);
			pir.setNeedType(needtype);
			pir.setPriority(priority);
			pir.setModifiedBy(UserId);
			pir.setInitiationReqId(Long.parseLong(InitiationReqId));
			pir.setModifiedDate(sdf1.format(new Date()));

			long count = 0;
			count = service.RequirementUpdate(pir);

			if (count > 0) {
				redir.addAttribute("result", "Requirement Details Added successfully");
			} else {
				redir.addAttribute("resultfail", "Requirement Details Added successfully");
			}

			redir.addAttribute("initiationId", initiationid);
			redir.addAttribute("project", project);
			redir.addAttribute("projectId", projectId);
			redir.addAttribute("InitiationReqId", InitiationReqId);
			redir.addAttribute("reqInitiationId", reqInitiationId);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside RequirementEdit.htm " + UserId, e);
		}
		return "redirect:/RequirementList.htm";
	}

	@RequestMapping(value = "RequirementMainJsonValue.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody String RequirementMainJsonValue(@RequestParam("ReqMainId") String ReqMainId) throws Exception {
		logger.info(new Date() + "Inside RequirementMainJsonValue.htm ");
		List<Object[]> ReqMainList = null;
		try {

			ReqMainList = service.getReqMainList(ReqMainId);

			if (ReqMainList.size() > 1) {
				ReqMainList.remove(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside RequirementMainJsonValue.htm", e);
		}
		Gson json = new Gson();
		return json.toJson(ReqMainList);
	}

	@RequestMapping(value = "RequirementSubAdd.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String RequirementSubAdd(RedirectAttributes redir, HttpServletRequest req, HttpServletResponse res,
			HttpSession ses) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside RequirementSubAdd.htm " + UserId);
		try {
			String projectId = req.getParameter("projectId");
			String initiationId = req.getParameter("initiationId");
			String project = req.getParameter("project");
			String InitiationReqId = req.getParameter("InitiationReqId");
			String productTreeMainId = req.getParameter("productTreeMainId");
			String reqInitiationId = req.getParameter("reqInitiationId");

			String description = req.getParameter("description");
			String priority = req.getParameter("priority");
			String needtype = req.getParameter("needtype");
			String derivedtype = req.getParameter("derivedtype");
			
			String reqType = req.getParameter("reqType");
			String[] reqTypes = reqType.split("/");

			String ReqMainId = reqTypes[0];
			List<Object[]> reqTypeList = service.getreqTypeList(ReqMainId, InitiationReqId);
			int length = 10;

			if (reqTypeList != null && reqTypeList.size() > 0) {
				Object[] tempList = reqTypeList.get(reqTypeList.size() - 1);

				if (tempList != null && tempList[1] != null) {

					String temp = tempList[1].toString();

					String[] tempArray = temp.split("_");

					String lengthValue = tempArray[tempArray.length - 1];

					length = Integer.parseInt(lengthValue) + 10;
				}

			}

			String Demonstration = null;

			if (req.getParameterValues("Demonstration") != null) {
				Demonstration = Arrays.asList(req.getParameterValues("Demonstration")).toString().replace("[", "")
						.replace("]", "");
			}

			String TestPlan = null;
			if (req.getParameterValues("TestPlan") != null) {
				TestPlan = Arrays.asList(req.getParameterValues("TestPlan")).toString().replace("[", "").replace("]",
						"");
			}

			String Analysis = null;
			if (req.getParameterValues("Analysis") != null) {
				Analysis = Arrays.asList(req.getParameterValues("Analysis")).toString().replace("[", "").replace("]",
						"");
			}

			String Inspection = null;
			if (req.getParameterValues("Inspection") != null) {
				Inspection = Arrays.asList(req.getParameterValues("Inspection")).toString().replace("[", "")
						.replace("]", "");
			}

			String specialMethods = null;

			if (req.getParameterValues("specialMethods") != null) {
				specialMethods = Arrays.asList(req.getParameterValues("specialMethods")).toString().replace("[", "")
						.replace("]", "");
			}

			String LinkedPara = null;

			if (req.getParameterValues("LinkedPara") != null) {
				LinkedPara = Arrays.asList(req.getParameterValues("LinkedPara")).toString().replace("[", "")
						.replace("]", "");
			}

			String LinkedSubSystem = null;

			if (req.getParameter("LinkedSub") != null) {
				LinkedSubSystem = Arrays.asList(req.getParameterValues("LinkedSub")).toString().replace("[", "")
						.replace("]", "");
			}

			String requirementId = "";
			if (length < 100) {
				requirementId = reqTypes[1] + ("_000" + length);
			} else if (length < 1000) {
				requirementId = reqTypes[1] + ("_00" + length);
			} else {
				requirementId = reqTypes[1] + ("_0" + length);
			}

			if (productTreeMainId == null) {
				productTreeMainId = "0";
			}

			if (reqInitiationId.equals("0")) {
				reqInitiationId = Long.toString(service.requirementInitiationAddHandling(initiationId, projectId,
						productTreeMainId, EmpId, UserId, null, null));
			}

			PfmsInititationRequirement pir = new PfmsInititationRequirement();
			pir.setRequirementDesc(description);
			pir.setNeedType(needtype);
			pir.setPriority(priority);
			pir.setRequirementBrief(reqTypes[2]);
			pir.setCreatedBy(UserId);
			pir.setRequirementId(requirementId);
			// pir.setInitiationId(Long.parseLong(initiationid));
			// pir.setProjectId(Long.parseLong(projectId));
			pir.setReqMainId(Long.parseLong(ReqMainId));
			pir.setParentId(Long.parseLong(InitiationReqId));
			pir.setDemonstration(Demonstration);
			pir.setTest(TestPlan);
			pir.setAnalysis(Analysis);
			pir.setInspection(Inspection);
			pir.setSpecialMethods(specialMethods);
			pir.setConstraints(req.getParameter("Constraints"));
			pir.setRemarks(req.getParameter("remarks"));
			pir.setLinkedPara(LinkedPara);
			pir.setCriticality(req.getParameter("criticality"));
			pir.setReqInitiationId(Long.parseLong(reqInitiationId));
			pir.setTestStage(req.getParameter("TestStage"));
			pir.setLinkedSubSystem(LinkedSubSystem);
			pir.setDerivedtype(derivedtype);
					long count = 0;
			count = service.addPfmsInititationRequirement(pir);

			if (count > 0) {
				redir.addAttribute("result", reqTypes[2] + " Added successfully");
			} else {
				redir.addAttribute("resultfail", reqTypes[2] + " Add unsuccessful");
			}

			redir.addAttribute("initiationId", initiationId);
			redir.addAttribute("project", project);
			redir.addAttribute("projectId", projectId);
			redir.addAttribute("productTreeMainId", productTreeMainId);
			redir.addAttribute("reqInitiationId", reqInitiationId);
			redir.addAttribute("InitiationReqId", InitiationReqId);
			redir.addAttribute("subId", count + "");

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside RequirementEdit.htm " + UserId, e);
		}
		return "redirect:/RequirementList.htm";
	}

	@RequestMapping(value = "RequirementUpdate.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String RequirementUpdate(RedirectAttributes redir, HttpServletRequest req, HttpServletResponse res,
			HttpSession ses) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() + "Inside RequirementUpdate.htm " + UserId);
		try {
			String projectId = req.getParameter("projectId");
			String initiationid = req.getParameter("initiationId");
			String project = req.getParameter("project");
			String InitiationReqId = req.getParameter("InitiationReqId");
			String MainInitiationReqId = req.getParameter("MainInitiationReqId");
			String description = req.getParameter("description");
			String priority = req.getParameter("priority");
			String needtype = req.getParameter("needtype");
			String reqInitiationId = req.getParameter("reqInitiationId");

			String reqType = req.getParameter("reqType");
			String[] reqTypes = reqType.split("/");
			String requirementId = "";

			String Demonstration = null;

			if (req.getParameterValues("Demonstration") != null) {
				Demonstration = Arrays.asList(req.getParameterValues("Demonstration")).toString().replace("[", "")
						.replace("]", "");
			}

			String TestPlan = null;
			if (req.getParameterValues("TestPlan") != null) {
				TestPlan = Arrays.asList(req.getParameterValues("TestPlan")).toString().replace("[", "").replace("]",
						"");
			}

			String Analysis = null;
			if (req.getParameterValues("Analysis") != null) {
				Analysis = Arrays.asList(req.getParameterValues("Analysis")).toString().replace("[", "").replace("]",
						"");
			}

			String Inspection = null;
			if (req.getParameterValues("Inspection") != null) {
				Inspection = Arrays.asList(req.getParameterValues("Inspection")).toString().replace("[", "")
						.replace("]", "");
			}

			String specialMethods = null;

			if (req.getParameterValues("specialMethods") != null) {
				specialMethods = Arrays.asList(req.getParameterValues("specialMethods")).toString().replace("[", "")
						.replace("]", "");
			}

			String LinkedPara = null;

			if (req.getParameterValues("LinkedPara") != null) {
				LinkedPara = Arrays.asList(req.getParameterValues("LinkedPara")).toString().replace("[", "")
						.replace("]", "");
			}

			String LinkedSubSystem = null;

			if (req.getParameter("LinkedSub") != null) {
				LinkedSubSystem = Arrays.asList(req.getParameterValues("LinkedSub")).toString().replace("[", "")
						.replace("]", "");
			}

			PfmsInititationRequirement pir = service.getPfmsInititationRequirementById(InitiationReqId);

			String ReqMainId = reqTypes[0];
			if (!ReqMainId.equalsIgnoreCase(pir.getReqMainId() + "")) {
				
				
				List<Object[]> reqTypeList = service.getreqTypeList(ReqMainId, MainInitiationReqId);
				int length = 10;

				if (reqTypeList != null && reqTypeList.size() > 0) {
					Object[] tempList = reqTypeList.get(reqTypeList.size() - 1);
					if (tempList != null && tempList[1] != null) {
						String temp = tempList[1].toString();
						String[] tempArray = temp.split("_");
						String lengthValue = tempArray[tempArray.length - 1];
						length = Integer.parseInt(lengthValue) + 10;
					}
				}
//			if (length < 9) {
//				requirementId = reqTypes[1]+ ("_000" + ( (length+1) * 10));
//			} else if (length < 99) {
//				requirementId = reqTypes[1]+ ("_00" + ( (length+1) * 10));
//			} else {
//				requirementId = reqTypes[1]+ ("_0" + ( (length+1) * 10));
//			}
//			
//			String requirementId="";
				if (length < 100) {
					requirementId = reqTypes[1] + ("_000" + length);
				} else if (length < 1000) {
					requirementId = reqTypes[1] + ("_00" + length);
				} else {
					requirementId = reqTypes[1] + ("_0" + length);
				}
				pir.setRequirementId(requirementId);
				pir.setReqMainId(Long.parseLong(ReqMainId));
				pir.setRequirementBrief(reqTypes[2]);
			}

			pir.setRequirementBrief(reqTypes[2]);
			pir.setRequirementDesc(description);
			pir.setNeedType(needtype);
			pir.setPriority(priority);
			pir.setModifiedBy(UserId);
			pir.setDemonstration(Demonstration);
			pir.setTest(TestPlan);
			pir.setAnalysis(Analysis);
			pir.setInspection(Inspection);
			pir.setSpecialMethods(specialMethods);
			pir.setConstraints(req.getParameter("Constraints"));
			pir.setRemarks(req.getParameter("remarks"));
			pir.setLinkedPara(LinkedPara);
			pir.setCriticality(req.getParameter("criticality"));
			pir.setReqInitiationId(Long.parseLong(reqInitiationId));
			pir.setTestStage(req.getParameter("TestStage"));
			pir.setLinkedSubSystem(LinkedSubSystem);
			pir.setDerivedtype(req.getParameter("derivedtype"));
			long count = 0;
			count = service.addOrUpdatePfmsInititationRequirement(pir);

			if (count > 0) {
				redir.addAttribute("result", "Requirements " + requirementId + " Updated successfully");
			} else {
				redir.addAttribute("resultfail", "Requirements Updated  unsuccessful");
			}

			redir.addAttribute("initiationId", initiationid);
			redir.addAttribute("project", project);
			redir.addAttribute("projectId", projectId);
			redir.addAttribute("InitiationReqId", MainInitiationReqId);
			redir.addAttribute("subId", InitiationReqId);
			redir.addAttribute("reqInitiationId", reqInitiationId);
			redir.addAttribute("productTreeMainId", req.getParameter("productTreeMainId"));

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside RequirementUpdate.htm " + UserId, e);
		}
		return "redirect:/RequirementList.htm";
	}

	// @RequestMapping(value ="AddDocs.htm",method =
	// {RequestMethod.POST,RequestMethod.GET})
	// public @ResponseBody String AddDocs(RedirectAttributes
	// redir,HttpServletRequest req ,HttpServletResponse res ,HttpSession ses)
	// throws Exception {
	// logger.info(new Date() +"Inside AddDocs.htm ");
	// List<Object[]>ReqMainList= null;
	// long count=0;
	// try {
	//
	// System.out.println("checkedValues"+req.getParameter("checkedValues"));
	// String chkValue=req.getParameter("checkedValues");
	// String []values=chkValue.split(",");
	//
	// String projectId = req.getParameter("projectId");
	// String initiationId= req.getParameter("initiationid");
	//
	// if(projectId==null)projectId="0";
	// if(initiationId==null)initiationId="0";
	//
	//
	//
	// List<ReqDoc>list= new ArrayList<>();
	//
	// for(int i=0;i<values.length;i++) {
	// ReqDoc rc= new ReqDoc();
	//
	// rc.setDocId(Long.parseLong(values[i]));
	// rc.setInitiationId(Long.parseLong(initiationId));
	// rc.setProjectId(Long.parseLong(projectId));
	// rc.setIsActive(1);
	// list.add(rc);
	// }
	//
	// count = service.addDocs(list);
	//
	//
	// }
	// catch(Exception e){
	// e.printStackTrace();
	// logger.error(new Date() +" Inside", e);
	// }
	// Gson json = new Gson();
	// return json.toJson(count);
	// }

	@RequestMapping(value = "AddRequirementDocs.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String addRequirementDocs(RedirectAttributes redir, HttpServletRequest req, HttpServletResponse res,
			HttpSession ses) throws Exception {
		logger.info(new Date() + "Inside AddRequirementDocs.htm ");
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		long count = 0;
		try {

			
			String chkValue = req.getParameter("checkedValues");
			String[] values = chkValue.split(",");

			String projectId = req.getParameter("projectId");
			String initiationId = req.getParameter("initiationid");
			String productTreeMainId = req.getParameter("productTreeMainId");
			String reqInitiationId = req.getParameter("reqInitiationId");

			if (projectId == null)
				projectId = "0";
			if (initiationId == null)
				initiationId = "0";
			if (productTreeMainId == null)
				productTreeMainId = "0";

			if (reqInitiationId.equals("0")) {
				reqInitiationId = Long.toString(service.requirementInitiationAddHandling(initiationId, projectId,
						productTreeMainId, EmpId, UserId, null, null));
			}

			List<ReqDoc> list = new ArrayList<>();

			for (int i = 0; i < values.length; i++) {
				ReqDoc rc = new ReqDoc();

				rc.setDocId(Long.parseLong(values[i]));
				// rc.setInitiationId(Long.parseLong(initiationId));
				// rc.setProjectId(Long.parseLong(projectId));
				rc.setIsActive(1);
				rc.setReqInitiationId(Long.parseLong(reqInitiationId));
				list.add(rc);
			}

			count = service.addDocs(list);

			if (count > 0) {
				redir.addAttribute("result", "Applicable Dcouments Linked successfully");
			} else {
				redir.addAttribute("resultfail", "Applicable Dcouments Link unsuccessful");
			}

			redir.addAttribute("initiationId", initiationId);
			redir.addAttribute("projectId", projectId);
			redir.addAttribute("reqInitiationId", reqInitiationId);
			if (!initiationId.equals("0")) {
				return "redirect:/ProjectOverAllRequirement.htm";
			} else {
				return "redirect:/ProjectRequirementDetails.htm";
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside", e);
			return "static/Error";
		}
	}

	@RequestMapping(value = "SQRDownload.htm", method = RequestMethod.GET)
	public void sqrDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside SQRDownload.htm " + UserId);

		try {

			String reqInitiationId = req.getParameter("reqInitiationId");

			Object[] sqrfile = projectservice.SqrFiles(reqInitiationId);
			

			File my_file = new File(
					uploadpath + File.separator + File.separator + sqrfile[11] + File.separator + sqrfile[12]);
			if (my_file.exists()) {
				res.setContentType("application/octet-stream");
				res.setHeader("Content-Disposition", "attachment; filename=\"" + sqrfile[12] + "\"");

				FileInputStream fis = new FileInputStream(my_file);
				ServletOutputStream os = res.getOutputStream();

				byte[] buffer = new byte[4096];
				int bytesRead = -1;
				while ((bytesRead = fis.read(buffer)) != -1) {
					os.write(buffer, 0, bytesRead);
				}

				fis.close();
				os.close();
			} else {
				// Handle file not found case
				res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " SQRDownload.htm " + req.getUserPrincipal().getName(), e);
		}
	}

	@RequestMapping(value = "ProjectRequirementTransStatus.htm", method = { RequestMethod.GET, RequestMethod.POST })
	public String projectRequirementTransStatus(HttpServletRequest req, HttpSession ses) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside ProjectRequirementTransStatus.htm" + UserId);
		try {
			String reqInitiationId = req.getParameter("reqInitiationId");
			String docType = req.getParameter("docType");
			if (reqInitiationId != null) {
				req.setAttribute("transactionList", service.projectDocTransList(reqInitiationId, docType));
				req.setAttribute("docInitiationId", reqInitiationId);
				req.setAttribute("docType", docType);
			}
			return "requirements/ProjectDocTransStatus";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside ProjectRequirementTransStatus.htm " + UserId, e);
			return "static/Error";
		}
	}

	@RequestMapping(value = "ProjectDocTransactionDownload.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public void projectDocTransactionDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res)
			throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		logger.info(new Date() + "Inside ProjectDocTransactionDownload.htm " + UserId);
		try {
			String docInitiationId = req.getParameter("docInitiationId");
			String docType = req.getParameter("docType");

			if (docInitiationId != null) {
				Object[] projectDetails = null;
				long initiationId = 0L;
				long projectId = 0L;

				if (docType != null && docType.equalsIgnoreCase("R")) {
					RequirementInitiation reqini = service.getRequirementInitiationById(docInitiationId);
					initiationId = reqini.getInitiationId();
					projectId = reqini.getProjectId();
				} else if (docType != null && docType.equalsIgnoreCase("S")) {

				} else if (docType != null && docType.equalsIgnoreCase("T")) {
					TestPlanInitiation testplan = service.getTestPlanInitiationById(docInitiationId);
					initiationId = testplan.getInitiationId();
					projectId = testplan.getProjectId();
				}

				projectDetails = projectservice.getProjectDetails(labcode,
						initiationId != 0 ? initiationId + "" : projectId + "", initiationId != 0 ? "P" : "E");
				req.setAttribute("projectDetails", projectDetails);
				req.setAttribute("transactionList", service.projectDocTransList(docInitiationId, "R"));
			}

			String filename = "Doc_Transaction";
			String path = req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path", path);
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/print/ProjectDocTransactionDownload.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			HtmlConverter.convertToPdf(html, new FileOutputStream(path + File.separator + filename + ".pdf"));
			PdfWriter pdfw = new PdfWriter(path + File.separator + "merged.pdf");
			PdfReader pdf1 = new PdfReader(path + File.separator + filename + ".pdf");
			PdfDocument pdfDocument = new PdfDocument(pdf1, pdfw);
			pdfDocument.close();
			pdf1.close();
			pdfw.close();

			res.setContentType("application/pdf");
			res.setHeader("Content-disposition", "inline;filename=" + filename + ".pdf");
			File f = new File(path + "/" + filename + ".pdf");

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

			Path pathOfFile2 = Paths.get(path + File.separator + filename + ".pdf");
			Files.delete(pathOfFile2);

		} catch (Exception e) {
			logger.error(new Date() + " Inside ProjectDocTransactionDownload.htm " + UserId, e);
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "ProjectRequirementApprovalSubmit.htm", method = { RequestMethod.GET, RequestMethod.POST })
	public String projectRequirementApprovalSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir,
			HttpServletResponse resp) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside ProjectRequirementApprovalSubmit.htm" + UserId);
		try {
			String reqInitiationId = req.getParameter("reqInitiationId");
			String action = req.getParameter("Action");
			String remarks = req.getParameter("remarks");

			RequirementInitiation reqInitiation = service.getRequirementInitiationById(reqInitiationId);
			String reqStatusCode = reqInitiation.getReqStatusCode();

		
		

			List<String> reqforwardstatus = Arrays.asList("RIN", "RRR", "RRA");

			long result = service.projectRequirementApprovalForward(reqInitiationId, action, remarks, EmpId, labcode,
					UserId);

			if (result != 0 && reqInitiation.getReqStatusCode().equalsIgnoreCase("RFA")
					&& reqInitiation.getReqStatusCodeNext().equalsIgnoreCase("RAM")) {
				// Pdf Freeze
				service.requirementPdfFreeze(req, resp, reqInitiationId, labcode);
			}

			if (action.equalsIgnoreCase("A")) {
				if (reqforwardstatus.contains(reqStatusCode)) {
					if (result != 0) {
						redir.addAttribute("result", "Requirment forwarded Successfully");
					} else {
						redir.addAttribute("resultfail", "Requirment forward Unsuccessful");
					}
					redir.addAttribute("projectType", req.getParameter("projectType"));
					redir.addAttribute("projectId", req.getParameter("projectId"));
					redir.addAttribute("initiationId", req.getParameter("initiationId"));
					redir.addAttribute("productTreeMainId", req.getParameter("productTreeMainId"));
					return "redirect:/Requirements.htm";
				} else if (reqStatusCode.equalsIgnoreCase("RFW")) {
					if (result != 0) {
						redir.addAttribute("result", "Requirment Recommended Successfully");
					} else {
						redir.addAttribute("resultfail", "Requirment Recommend Unsuccessful");
					}
					return "redirect:/DocumentApprovals.htm";
				} else if (reqStatusCode.equalsIgnoreCase("RFR")) {
					if (result != 0) {
						redir.addAttribute("result", "Requirment Approved Successfully");
					} else {
						redir.addAttribute("resultfail", "Requirment Approve Unsuccessful");
					}
					return "redirect:/DocumentApprovals.htm";
				}
			} else if (action.equalsIgnoreCase("R") || action.equalsIgnoreCase("D")) {
				if (result != 0) {
					redir.addAttribute("result", action.equalsIgnoreCase("R") ? "Requirment Returned Successfully"
							: "Requirment Disapproved Successfully");
				} else {
					redir.addAttribute("resultfail", action.equalsIgnoreCase("R") ? "Requirment Return Unsuccessful"
							: "Requirment Disapprove Unsuccessful");
				}
			}
			return "redirect:/DocumentApprovals.htm";

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside ProjectRequirementApprovalSubmit.htm " + UserId, e);
			return "static/Error";
		}
	}

	@RequestMapping(value = "DocumentApprovals.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String documentApprovals(HttpServletRequest req, HttpSession ses) throws Exception {
		String Username = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside DocumentApprovals.htm " + Username);
		try {

			String fromdate = req.getParameter("fromdate");
			String todate = req.getParameter("todate");

			LocalDate today = LocalDate.now();

			if (fromdate == null) {
				fromdate = today.withDayOfMonth(1).toString();
				todate = today.toString();

			} else {
				fromdate = fc.RegularToSqlDate(fromdate);
				todate = fc.RegularToSqlDate(todate);
			}

			req.setAttribute("fromdate", fromdate);
			req.setAttribute("todate", todate);
			req.setAttribute("tab", req.getParameter("tab"));

			req.setAttribute("reqPendingList", service.projectRequirementPendingList(EmpId, labcode));
			req.setAttribute("reqApprovedList", service.projectRequirementApprovedList(EmpId, fromdate, todate));
			req.setAttribute("testPlanPendingList", service.projectTestPlanPendingList(EmpId, labcode));
			req.setAttribute("testPlanApprovedList", service.projectTestPlanApprovedList(EmpId, fromdate, todate));
			req.setAttribute("specificationPendingList", service.projectSpecificationPendingList(EmpId, labcode));
			req.setAttribute("specificationApprovedList", service.projectSpecificationApprovedList(EmpId, fromdate, todate));
			
			// IGI / ICD / IRS / IDD Doc
			req.setAttribute("igiDocPendingList", service.igiDocumentPendingList(EmpId, labcode));
			req.setAttribute("igiDocApprovedList", service.igiDocumentApprovedList(EmpId, fromdate, todate));
			req.setAttribute("icdDocPendingList", service.icdDocumentPendingList(EmpId, labcode));
			req.setAttribute("icdDocApprovedList", service.icdDocumentApprovedList(EmpId, fromdate, todate));
			req.setAttribute("irsDocPendingList", service.irsDocumentPendingList(EmpId, labcode));
			req.setAttribute("irsDocApprovedList", service.irsDocumentApprovedList(EmpId, fromdate, todate));
			req.setAttribute("iddDocPendingList", service.iddDocumentPendingList(EmpId, labcode));
			req.setAttribute("iddDocApprovedList", service.iddDocumentApprovedList(EmpId, fromdate, todate));
			
			return "requirements/DocumentApprovals";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside DocumentApprovals.htm " + Username, e);
			return "static/Error";
		}
	}

	@RequestMapping(value = "TestDetails.htm", method = { RequestMethod.GET, RequestMethod.POST })
	public String TestDetails(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() + "Inside TestDetails " + UserId);
		try {
			String initiationId = req.getParameter("initiationId");
			String projectId = req.getParameter("projectId");
			String productTreeMainId = req.getParameter("productTreeMainId");
			String testPlanInitiationId = req.getParameter("testPlanInitiationId");

			req.setAttribute("TestTypeList", service.TestTypeList());
			req.setAttribute("StagesApplicable", service.StagesApplicable());
			req.setAttribute("VerificationMethodList", service.getVerificationMethodList());
			List<Object[]> TestDetailsList = service.TestDetailsList(testPlanInitiationId);
			
			if (req.getParameter("TestReqId") == null && TestDetailsList != null && TestDetailsList.size() > 0) {
				req.setAttribute("TestReqId", TestDetailsList.get(0)[0].toString());
			} else {
				req.setAttribute("TestReqId", req.getParameter("TestReqId"));
			}

			String specsInitiationId = service.getFirstVersionSpecsInitiationId(initiationId, projectId,
					productTreeMainId) + "";
				List<TestSetupMaster>master = service.getTestSetupMaster();
			
			if(master!=null && master.size()>0) {
				master = master.stream().filter(e->e.getIsActive()==1).collect(Collectors.toList());
			}
			req.setAttribute("testSetupMasterMaster", master);	
			req.setAttribute("specificationList", service.getSpecsList(specsInitiationId));
			req.setAttribute("TestDetailsList", service.TestDetailsList(testPlanInitiationId));
			req.setAttribute("initiationId", initiationId);
			req.setAttribute("projectId", projectId);
			req.setAttribute("productTreeMainId", productTreeMainId);
			req.setAttribute("testPlanInitiationId", testPlanInitiationId);
			req.setAttribute("click", req.getParameter("click"));
			req.setAttribute("testPlanMainList", service.getTestPlanMainList(testPlanInitiationId));
			req.setAttribute("projectDetails", projectservice.getProjectDetails(LabCode,
					!projectId.equals("0") ? projectId : initiationId, !projectId.equals("0") ? "E" : "P"));
//			List<Object[]>TestPlanMasterList = service.TestPlanMaster();
//			req.setAttribute("TestPlanMasterList", TestPlanMasterList);
			
			List<TestPlanMaster>tp= service.getAllTestPlans();
			req.setAttribute("TestPlanMasterList", tp);
			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(LabCode));
			req.setAttribute("LabList", projectservice.LabListDetails(LabCode));
			req.setAttribute("drdologo", LogoUtil.getDRDOLogoAsBase64String());
			req.setAttribute("DocTempAttributes", projectservice.DocTempAttributes());
			return "requirements/TestDetails";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "static/Error";
	}

	@RequestMapping(value = "TestDetailsAddSubmit.htm", method = RequestMethod.POST)
	public String TestDetailsAddSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
			throws Exception {


		String UserId = (String) ses.getAttribute("Username");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside TestDetailsAddSubmit.htm " + UserId);
		try {
			String initiationId = req.getParameter("initiationId");
			String projectId = req.getParameter("projectId");
			String productTreeMainId = req.getParameter("productTreeMainId");
			String testPlanInitiationId = req.getParameter("testPlanInitiationId");

			StringBuilder toolSB= new StringBuilder("");
			
			StringBuilder linkedSpecSb= new StringBuilder("");
			
			
		
			
			if (initiationId == null)
				initiationId = "0";
			if (projectId == null)
				projectId = "0";
			if (productTreeMainId == null)
				productTreeMainId = "0";

			if (testPlanInitiationId.equals("0")) {
				testPlanInitiationId = Long.toString(service.testPlanInitiationAddHandling(initiationId, projectId,
						productTreeMainId, EmpId, UserId, null, null));
			}

			String action = req.getParameter("action");
			String testId = req.getParameter("testId");

			TestDetails Td = action != null && action.equalsIgnoreCase("Edit") ? service.getTestPlanDetailsById(testId)
					: new TestDetails();

	
			
			String[] StageApplicable=req.getParameterValues("StageApplicable");
			
			String[] cycle = req.getParameterValues("rows");
			String[] cycleRows = req.getParameterValues("cycle");
			
		

			
			String stageSb= "";
			String cycleSb= "";
			String rowSb= "";
			if(StageApplicable!=null && StageApplicable.length>0) {
				stageSb=Arrays.stream(StageApplicable).collect(Collectors.joining(", "));
			}
			
			if ( Td.getStageApplicable()!=null ) {
				if(StageApplicable!=null && StageApplicable.length>0) {
					stageSb= stageSb+", "+Td.getStageApplicable();
				}else {
					stageSb = Td.getStageApplicable();
				}
			}
			
			
			if(cycleRows!=null && cycleRows.length>0) {
				
				rowSb = Arrays.stream(cycleRows).collect(Collectors.joining(", ") );
			}
			
			
			if (Td.getNumberofCycles()!=null && 
					!Td.getNumberofCycles().equalsIgnoreCase("0") ) 
			{
				if(StageApplicable!=null && StageApplicable.length>0) {
					rowSb=rowSb+", "+Td.getNumberofCycles();
				}else {
					rowSb = Td.getNumberofCycles();
				}
			}
			
			
			
			if(cycle!=null && cycle.length>0 ) {
				
				cycleSb = Arrays.stream(cycle).collect(Collectors.joining(", "));
						
			}
			
			
			if ( Td.getNumberofRows()!=null && !Td.getNumberofRows().equalsIgnoreCase("0")) {
				
				if(StageApplicable!=null && StageApplicable.length>0) {
					cycleSb = cycleSb+", "+Td.getNumberofRows();
				}else {
					cycleSb= Td.getNumberofRows();
				}
				
			}
			
			
			

			Td.setTestPlanInitiationId(Long.parseLong(testPlanInitiationId));
			Td.setName(req.getParameter("name"));

		

			String Methodology = "";
			if (req.getParameterValues("Methodology") != null) {
				String[] linkedreq = req.getParameterValues("Methodology");
				for (int i = 0; i < linkedreq.length; i++) {
					Methodology = Methodology + linkedreq[i];
					if (i != linkedreq.length - 1) {
						Methodology = Methodology + ",";
					}
				}
			}

			String ToolsSetup = "";
			if (req.getParameterValues("ToolsSetup") != null) {
				String[] linkedreq = req.getParameterValues("ToolsSetup");
				for (int i = 0; i < linkedreq.length; i++) {
					ToolsSetup = ToolsSetup + linkedreq[i];
					if (i != linkedreq.length - 1) {
						ToolsSetup = ToolsSetup + ",";
					}
				}
			}
			String specid = "";

			if (req.getParameterValues("SpecId") != null) {
				String[] SpecId = req.getParameterValues("SpecId");
				for (int i = 0; i < SpecId.length; i++) {
					specid = specid + SpecId[i];
					if (i != SpecId.length - 1) {
						specid = specid + ", ";
					}
				}
			}

			Td.setSpecificationId(specid);

			Td.setObjective(req.getParameter("Objective"));
			Td.setDescription(req.getParameter("Description"));
			Td.setPreConditions(req.getParameter("PreConditions"));
			Td.setPostConditions(req.getParameter("PostConditions"));
			Td.setConstraints(req.getParameter("Constraints"));
			Td.setSafetyRequirements(req.getParameter("SafetyReq"));
			Td.setMethodology(Methodology);
			Td.setToolsSetup(ToolsSetup);
			Td.setPersonnelResources(req.getParameter("PersonnelResources"));
			Td.setEstimatedTimeIteration(req.getParameter("EstimatedTimeIteration"));
			Td.setIterations(req.getParameter("Iterations"));
			Td.setSchedule(req.getParameter("Schedule"));
			Td.setPass_Fail_Criteria(req.getParameter("PassFailCriteria"));
			Td.setRemarks(req.getParameter("remarks"));
			Td.setTimetype(req.getParameter("timeType") );
			Td.setStageApplicable(stageSb.toString() );
			Td.setNumberofCycles(specid);
			Td.setNumberofCycles(rowSb.toString() );
			Td.setNumberofRows(cycleSb.toString() );
			
			Td.setIsActive(1);
			if (action != null && action.equalsIgnoreCase("Edit")) {
				Td.setModifiedBy(UserId);
				Td.setModifiedDate(sdf1.format(new Date()));
			} else {

				String Testtype = "TEST";
				Long a = (Long) service.numberOfTestTypeId(testPlanInitiationId);
				String TestDetailsId = "";
				if (a < 90L) {
			
					TestDetailsId = Testtype + ("000" + (a + 10));
				} else if (a < 990L) {
					TestDetailsId = Testtype + ("00" + (a + 10));
				} else {
					TestDetailsId = Testtype + ("0" + (a + 10));
				}
				Td.setTestDetailsId(TestDetailsId);
				Td.setTestCount((a.intValue() + 10));

				Td.setCreatedBy(UserId);
				Td.setCreatedDate(sdf1.format(new Date()));
			}
			long count = service.TestDetailsAdd(Td);

			if (count > 0) {
				redir.addAttribute("result", "Test  Details " + action + "ed Successfully");
			} else {
				redir.addAttribute("resultfail", "Test Details " + action + " Unsuccessful");
			}
			redir.addAttribute("initiationId", initiationId);
			redir.addAttribute("projectId", projectId);
			redir.addAttribute("productTreeMainId", productTreeMainId);
			redir.addAttribute("testPlanInitiationId", testPlanInitiationId);
			redir.addAttribute("TestReqId", count + "");
			return "redirect:/TestDetails.htm";
		}

		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside TestDetailsAddSubmit.htm  " + UserId, e);
			return "static/Error";
		}
	}

	@RequestMapping(value = "TestDetailsJson.htm", method = RequestMethod.GET)
	public @ResponseBody String TestDetailseJson(HttpSession ses, HttpServletRequest req) throws Exception {
		Gson json = new Gson();
		Object[] TestDetails = null;
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside TestDetailsJson.htm" + UserId);
		try {
			String testId = req.getParameter("testId");
			if (testId != null && testId != "") {
				List<Object[]> TestDetailsList = service.TestType(testId);
				if (TestDetailsList != null && TestDetailsList.size() > 0) {
					TestDetails = TestDetailsList.get(0);
				}
			}

		} catch (Exception e) {
			logger.error(new Date() + "Inside TestDetailsJson.htm" + UserId, e);
			e.printStackTrace();
			return null;
		}
		return json.toJson(TestDetails);
	}



	// adding requirement type
	@RequestMapping(value = "InsertTestType.htm", method = RequestMethod.GET)
	public @ResponseBody String insertReqType(HttpSession ses, HttpServletRequest req) throws Exception {
		Gson json = new Gson();
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside InsertTestType.htm" + UserId);
		long count = -1;
		try {
			String TestTypes = req.getParameter("TestTypes");
			String TestTools = req.getParameter("TestTools");
			String TestSetupName = req.getParameter("TestSetupName");
			// List<Object[]>TestSuiteList=service.TestTypeList();
			// long result = TestSuiteList.stream()
			// .filter(i -> i.length > 1 && i[1] != null &&
			// i[1].toString().equalsIgnoreCase(TestTypes))
			// .count();
			// if(result>0) {
			// System.out.println("result@@@@@@@"+result);
			// return json.toJson(count);
			// }

			int result = service.getDuplicateCountofTestType(TestTypes);

			if (result > 0) {
				return json.toJson(count);
			}

			TestTools pt = new TestTools();
			pt.setTestType(TestTypes);
			pt.setTestTools(TestTools);
			pt.setTestSetupName(TestSetupName);
			pt.setIsActive(1);
			count = service.insertTestType(pt);
		} catch (Exception e) {
			logger.error(new Date() + "Inside InsertTestType.htm" + UserId);
			e.printStackTrace();
		}
		return json.toJson(count);
	}

	//
	/* Test Plan Pdf */
	@RequestMapping(value = "TestPlanDownlodPdf.htm")
	public void TestPlanDocumentDownlodPdf(HttpServletRequest req, HttpSession ses, HttpServletResponse res,
			RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() + "Inside TestPlanDownlodPdf.htm " + UserId);
		try {
			Object[] DocTempAttributes = null;
			DocTempAttributes = projectservice.DocTempAttributes();
			req.setAttribute("DocTempAttributes", DocTempAttributes);
			String testPlanInitiationId = req.getParameter("testPlanInitiationId");

			TestPlanInitiation ini = service.getTestPlanInitiationById(testPlanInitiationId);

			String version = ini.getTestPlanVersion();
			if (ini != null) {
				testPlanInitiationId = service.getFirstVersionTestPlanInitiationId(ini.getInitiationId() + "",
						ini.getProjectId() + "", ini.getProductTreeMainId() + "") + "";
			} else {
				testPlanInitiationId = "0";
			}
			Object[] projectDetails = projectservice.getProjectDetails(LabCode,
					ini.getInitiationId() != 0 ? ini.getInitiationId() + "" : ini.getProjectId() + "",
					ini.getInitiationId() != 0 ? "P" : "E");
			req.setAttribute("projectShortName", projectDetails != null ? projectDetails[2] : "");
			req.setAttribute("projectDetails", projectDetails);
			String specsInitiationId = service.getFirstVersionSpecsInitiationId(ini.getInitiationId() + "",
					ini.getProjectId() + "", ini.getProductTreeMainId() + "") + "";

			req.setAttribute("specificationList", service.getSpecsList(specsInitiationId));
			String filename = "TestPlan";
			String path = req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path", path);
			req.setAttribute("version", version);
			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(LabCode));
			req.setAttribute("LabImage", LogoUtil.getLabImageAsBase64String(LabCode));
			req.setAttribute("LabList", projectservice.LabListDetails(LabCode));
			req.setAttribute("path", path);
			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(LabCode));
			req.setAttribute("LabImage", LogoUtil.getLabImageAsBase64String(LabCode));
			req.setAttribute("LabList", projectservice.LabListDetails(LabCode));
			req.setAttribute("uploadpath", uploadpath);
			req.setAttribute("TestScopeIntro", service.TestScopeIntro(testPlanInitiationId));
			req.setAttribute("MemberList", service.DocMemberList(testPlanInitiationId, "0"));
			req.setAttribute("DocumentSummary", service.getTestandSpecsDocumentSummary(testPlanInitiationId, "0"));
			req.setAttribute("AbbreviationDetails", service.AbbreviationDetails(testPlanInitiationId, "0"));
			req.setAttribute("TestContent", service.GetTestContentList(testPlanInitiationId));
			req.setAttribute("AcceptanceTesting", service.GetAcceptanceTestingList(testPlanInitiationId));
			req.setAttribute("TestSuite", service.TestTypeList());
			req.setAttribute("TestDetailsList", service.TestDetailsList(testPlanInitiationId));
			req.setAttribute("StagesApplicable", service.StagesApplicable());
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/requirements/TestPlanPDFDownload.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			ConverterProperties converterProperties = new ConverterProperties();
			FontProvider dfp = new DefaultFontProvider(true, true, true);
			converterProperties.setFontProvider(dfp);

			HtmlConverter.convertToPdf(html, new FileOutputStream(path + File.separator + filename + ".pdf"),
					converterProperties);
			PdfWriter pdfw = new PdfWriter(path + File.separator + "merged.pdf");
			PdfReader pdf1 = new PdfReader(path + File.separator + filename + ".pdf");
			PdfDocument pdfDocument = new PdfDocument(pdf1, pdfw);
			int totalPages = pdfDocument.getNumberOfPages();
			
			req.setAttribute("totalPages", totalPages);

			pdfDocument.close();
			pdf1.close();
			pdfw.close();

			CharArrayWriterResponse customResponse1 = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/requirements/TestPlanPDFDownload.jsp").forward(req, customResponse1);
			String html1 = customResponse1.getOutput();

			ConverterProperties converterProperties1 = new ConverterProperties();
			FontProvider dfp1 = new DefaultFontProvider(true, true, true);
			converterProperties1.setFontProvider(dfp1);
			HtmlConverter.convertToPdf(html1, new FileOutputStream(path + File.separator + filename + ".pdf"),
					converterProperties1);
			PdfWriter pdfw1 = new PdfWriter(path + File.separator + "merged.pdf");
			PdfReader pdf2 = new PdfReader(path + File.separator + filename + ".pdf");
			PdfDocument pdfDocument1 = new PdfDocument(pdf2, pdfw1);
			pdfDocument1.close();
			pdf2.close();
			pdfw1.close();

			res.setContentType("application/pdf");
			res.setHeader("Content-disposition", "inline;filename=" + filename + ".pdf");
			File f = new File(path + "/" + filename + ".pdf");

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
			Path pathOfFile2 = Paths.get(path + File.separator + filename + ".pdf");
			Files.delete(pathOfFile2);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside TestPalnDocumentPdfDownlod.htm " + UserId, e);
		}
	}

	@RequestMapping(value = "ProjectDocTransStatus.htm", method = { RequestMethod.GET, RequestMethod.POST })
	public String projectTestPlanTransStatus(HttpServletRequest req, HttpSession ses) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside ProjectDocTransStatus.htm" + UserId);
		try {
			String testPlanInitiationId = req.getParameter("testPlanInitiationId");
			String docType = req.getParameter("docType");
			if (testPlanInitiationId != null) {
				req.setAttribute("transactionList", service.projectDocTransList(testPlanInitiationId, docType));
				req.setAttribute("docInitiationId", testPlanInitiationId);
				req.setAttribute("docType", docType);
			}
			return "requirements/ProjectDocTransStatus";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside ProjectDocTransStatus.htm " + UserId, e);
			return "static/Error";
		}
	}

	@RequestMapping(value = "ProjectTestPlanApprovalSubmit.htm", method = { RequestMethod.GET, RequestMethod.POST })
	public String projectTestPlanApprovalSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir,
			HttpServletResponse resp) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside ProjectTestPlanApprovalSubmit.htm" + UserId);
		try {
			String testPlanInitiationId = req.getParameter("testPlanInitiationId");
			String action = req.getParameter("Action");
			String remarks = req.getParameter("remarks");

			TestPlanInitiation testplan = service.getTestPlanInitiationById(testPlanInitiationId);
			String reqStatusCode = testplan.getReqStatusCode();

			List<String> reqforwardstatus = Arrays.asList("RIN", "RRR", "RRA");

			long result = service.projectTestPlanApprovalForward(testPlanInitiationId, action, remarks, EmpId, labcode,
					UserId);

			if (result != 0 && testplan.getReqStatusCode().equalsIgnoreCase("RFA")
					&& testplan.getReqStatusCodeNext().equalsIgnoreCase("RAM")) {
				// Pdf Freeze
				service.testPlanPdfFreeze(req, resp, testPlanInitiationId, labcode);
			}

			if (action.equalsIgnoreCase("A")) {
				if (reqforwardstatus.contains(reqStatusCode)) {
					if (result != 0) {
						redir.addAttribute("result", "Test Plan forwarded Successfully");
					} else {
						redir.addAttribute("resultfail", "Test Plan forward Unsuccessful");
					}
					redir.addAttribute("projectType", req.getParameter("projectType"));
					redir.addAttribute("projectId", req.getParameter("projectId"));
					redir.addAttribute("initiationId", req.getParameter("initiationId"));
					redir.addAttribute("productTreeMainId", req.getParameter("productTreeMainId"));
					redir.addAttribute("testPlanInitiationId", testPlanInitiationId);
					return "redirect:/ProjectTestPlan.htm";
				} else if (reqStatusCode.equalsIgnoreCase("RFW")) {
					if (result != 0) {
						redir.addAttribute("result", "Test Plan Recommended Successfully");
					} else {
						redir.addAttribute("resultfail", "Test Plan Recommend Unsuccessful");
					}
					return "redirect:/DocumentApprovals.htm";
				} else if (reqStatusCode.equalsIgnoreCase("RFR")) {
					if (result != 0) {
						redir.addAttribute("result", "Test Plan Approved Successfully");
					} else {
						redir.addAttribute("resultfail", "Test Plan Approve Unsuccessful");
					}
					return "redirect:/DocumentApprovals.htm";
				}
			} else if (action.equalsIgnoreCase("R") || action.equalsIgnoreCase("D")) {
				if (result != 0) {
					redir.addAttribute("result", action.equalsIgnoreCase("R") ? "Test Plan Returned Successfully"
							: "Test Plan Disapproved Successfully");
				} else {
					redir.addAttribute("resultfail", action.equalsIgnoreCase("R") ? "Test Plan Return Unsuccessful"
							: "Test Plan Disapprove Unsuccessful");
				}
			}
			return "redirect:/DocumentApprovals.htm";

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside ProjectTestPlanApprovalSubmit.htm " + UserId, e);
			return "static/Error";
		}
	}

	@RequestMapping(value = "ProjectTestPlanAmendSubmit.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String projectTestPlanAmendSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
			throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside ProjectTestPlanAmendSubmit.htm" + UserId);
		try {
			String testPlanInitiationId = req.getParameter("testPlanInitiationId");
			String amendversion = req.getParameter("amendversion");
			String remarks = req.getParameter("remarks");

			TestPlanInitiation testplan = service.getTestPlanInitiationById(testPlanInitiationId);

			service.projectTestPlanApprovalForward(testPlanInitiationId, "A", remarks, EmpId, labcode, UserId);

			long result = service.testPlanInitiationAddHandling(testplan.getInitiationId() + "",
					testplan.getProjectId() + "", testplan.getProductTreeMainId() + "", EmpId, UserId, amendversion,
					remarks);

			if (result != 0) {
				redir.addAttribute("result", "Test Plan Amended Successfully");
			} else {
				redir.addAttribute("resultfail", "Test Plan Amend Unsuccessful");
			}
			redir.addAttribute("testPlanInitiationId", result);
			// redir.addAttribute("projectType", testplan.getInitiationId()!=0?"I":"M");
			// redir.addAttribute("initiationId", testplan.getInitiationId());
			// redir.addAttribute("projectId", testplan.getProjectId());
			// redir.addAttribute("productTreeMainId", testplan.getProductTreeMainId());

			return "redirect:/ProjectTestPlanDetails.htm";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside ProjectTestPlanAmendSubmit.htm " + UserId, e);
			return "static/Error";
		}
	}

	@RequestMapping(value = "TestPlanDownlodPdfFreeze.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public void testPlanDownlodPdfFreeze(HttpServletRequest req, HttpSession ses, HttpServletResponse res,
			HttpServletResponse resp) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		logger.info(new Date() + "Inside TestPlanDownlodPdfFreeze.htm " + UserId);
		try {
			String testPlanInitiationId = req.getParameter("testPlanInitiationId");

			DocumentFreeze freeze = service.getDocumentFreezeByDocIdandDocType(testPlanInitiationId, "T");

			if (freeze != null && freeze.getPdfFilePath() == null) {
				service.testPlanPdfFreeze(req, resp, testPlanInitiationId, labcode);
			}

			res.setContentType("application/pdf");
			res.setHeader("Content-disposition", "inline;filename= TestPlan.pdf");
			File f = new File(uploadpath + File.separator + freeze.getPdfFilePath());
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

		} catch (Exception e) {
			logger.error(new Date() + " Inside TestPlanDownlodPdfFreeze.htm " + UserId, e);
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "RequirementDocumentDownlodPdfFreeze.htm", method = { RequestMethod.POST,
			RequestMethod.GET })
	public void requirementDocumentDownlodPdfFreeze(HttpServletRequest req, HttpSession ses, HttpServletResponse res,
			HttpServletResponse resp) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		logger.info(new Date() + "Inside RequirementDocumentDownlodPdfFreeze.htm " + UserId);
		try {
			String reqInitiationId = req.getParameter("reqInitiationId");

			DocumentFreeze freeze = service.getDocumentFreezeByDocIdandDocType(reqInitiationId, "R");

			if (freeze != null && freeze.getPdfFilePath() == null) {
				service.requirementPdfFreeze(req, resp, reqInitiationId, labcode);
			}

			res.setContentType("application/pdf");
			res.setHeader("Content-disposition", "inline;filename= Requirement.pdf");
			File f = new File(uploadpath + File.separator + freeze.getPdfFilePath());
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

		} catch (Exception e) {
			logger.error(new Date() + " Inside RequirementDocumentDownlodPdfFreeze.htm " + UserId, e);
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "SpecificationDownlodPdfFreeze.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public void SpecificationDownlodPdfFreeze(HttpServletRequest req, HttpSession ses, HttpServletResponse res,
			HttpServletResponse resp) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		logger.info(new Date() + "Inside RequirementDocumentDownlodPdfFreeze.htm " + UserId);
		try {
			String SpecsInitiationId = req.getParameter("SpecsInitiationId");

			DocumentFreeze freeze = service.getDocumentFreezeByDocIdandDocType(SpecsInitiationId, "S");

			if (freeze != null && freeze.getPdfFilePath() == null) {
//				service.requirementPdfFreeze(req, resp, reqInitiationId, labcode);
				service.SpecsInitiationPdfFreeze(req, resp, SpecsInitiationId, labcode);
			}

			res.setContentType("application/pdf");
			res.setHeader("Content-disposition", "inline;filename= Specification.pdf");
			File f = new File(uploadpath + File.separator + freeze.getPdfFilePath());
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

		} catch (Exception e) {
			logger.error(new Date() + " Inside SpecificationDownlodPdfFreeze.htm " + UserId, e);
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "ProjectRequirementAmendSubmit.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String projectRequirementAmendSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
			throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside ProjectRequirementAmendSubmit.htm" + UserId);
		try {
			String reqInitiationId = req.getParameter("reqInitiationId");
			String amendversion = req.getParameter("amendversion");
			String remarks = req.getParameter("remarks");

			RequirementInitiation testplan = service.getRequirementInitiationById(reqInitiationId);

			service.projectRequirementApprovalForward(reqInitiationId, "A", remarks, EmpId, labcode, UserId);

			long result = service.requirementInitiationAddHandling(testplan.getInitiationId() + "",
					testplan.getProjectId() + "", testplan.getProductTreeMainId() + "", EmpId, UserId, amendversion,
					remarks);

			if (result != 0) {
				redir.addAttribute("result", "Requirement Amended Successfully");
			} else {
				redir.addAttribute("resultfail", "Requirement Amend Unsuccessful");
			}
			redir.addAttribute("reqInitiationId", result);
			// redir.addAttribute("projectType", testplan.getInitiationId()!=0?"I":"M");
			// redir.addAttribute("initiationId", testplan.getInitiationId());
			// redir.addAttribute("projectId", testplan.getProjectId());
			// redir.addAttribute("productTreeMainId", testplan.getProductTreeMainId());

			return "redirect:/ProjectRequirementDetails.htm";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside ProjectRequirementAmendSubmit.htm " + UserId, e);
			return "static/Error";
		}
	}

	@RequestMapping(value = "AddReqType.htm")
	public @ResponseBody String AddReqType(HttpServletRequest req, HttpSession ses, HttpServletResponse res,
			RedirectAttributes redir) throws Exception

	{
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside ProjectRequirementAmendSubmit.htm" + UserId);

		try {
			PfmsReqTypes pr = new PfmsReqTypes();

			pr.setReqCode(req.getParameter("ReqCode"));
			pr.setReqName(req.getParameter("ReqCodeName"));
			pr.setReqParentId(0l);

			long result = service.AddReqType(pr);

			Gson json = new Gson();

			return json.toJson(result);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return null;
	}

	@RequestMapping(value = "TraceabilityMatrix.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String TraceabilityMatrix(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
			throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside TraceabilityMatrix.htm" + UserId);
		try {
			String projectType = req.getParameter("projectType");
			String projectId = req.getParameter("projectId");
			String productTreeMainId = req.getParameter("productTreeMainId");

			String initiationId = req.getParameter("initiationId");

			String reqInitiationId = req.getParameter("reqInitiationId");
			RequirementInitiation reqini = service.getRequirementInitiationById(reqInitiationId);
			// Object[] projectDetails = service.getProjectDetails("LRDE",
			// reqini.getInitiationId()!=0?reqini.getInitiationId()+"":reqini.getProjectId()+"",
			// reqini.getInitiationId()!=0?"P":"E");

		
			req.setAttribute("ProjectParaDetails", service.getProjectParaDetails(reqInitiationId));
			req.setAttribute("RequirementList", service.RequirementList(reqInitiationId));
			req.setAttribute("projectType", projectType);
			req.setAttribute("projectId", projectId);
			req.setAttribute("initiationId", initiationId);
			req.setAttribute("productTreeMainId", productTreeMainId);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return "requirements/TraceabilityMatrix";
	}

	@RequestMapping(value = "SpecsTraceabilityMatrix.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String SpecsTraceabilityMatrix(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
			throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside SpecsTraceabilityMatrix.htm" + UserId);
		try {
			String projectType = req.getParameter("projectType");
			String projectId = req.getParameter("projectId");
			String productTreeMainId = req.getParameter("productTreeMainId");

			String initiationId = req.getParameter("initiationId");

			String SpecsInitiationId = req.getParameter("SpecsInitiationId");
			SpecsInitiation specsInitiation = service.getSpecsInitiationById(SpecsInitiationId);
			// Object[] projectDetails = service.getProjectDetails("LRDE",
			// reqini.getInitiationId()!=0?reqini.getInitiationId()+"":reqini.getProjectId()+"",
			// reqini.getInitiationId()!=0?"P":"E");

			req.setAttribute("specsList", service.getSpecsList(SpecsInitiationId));
			List<Object[]> initiationReqList = service.initiationReqList(specsInitiation.getProjectId() + "",
					specsInitiation.getProductTreeMainId() + "", specsInitiation.getInitiationId() + "");
			String reqInitiationId = null;
			List<Object[]> RequirementList = new ArrayList<>();
			if (initiationReqList != null && initiationReqList.size() > 0) {
				reqInitiationId = initiationReqList.get(0)[0].toString();
				RequirementList = service.RequirementList(reqInitiationId);
			}
			req.setAttribute("RequirementList", RequirementList);
			req.setAttribute("projectType", projectType);
			req.setAttribute("projectId", projectId);
			req.setAttribute("initiationId", initiationId);
			req.setAttribute("productTreeMainId", productTreeMainId);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return "requirements/SepecTraceabilityMatrix";
	}

	@RequestMapping(value = "deleteSqr.htm", method = { RequestMethod.GET })
	public @ResponseBody String deleteSqr(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
			throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside deleteSqr.htm" + UserId);
		long count = 0;
		try {
			String paraId = req.getParameter("paraId");
			count = service.deleteSqr(paraId);
		} catch (Exception e) {

		}
		Gson json = new Gson();
		return json.toJson(count);
	}

	@RequestMapping(value = "deleteInitiationReq.htm", method = { RequestMethod.GET })
	public @ResponseBody String deleteInitiationReq(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
			throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside deleteInitiationReq.htm" + UserId);
		long count = 0;
		try {
			String InitiationReqId = req.getParameter("InitiationReqId");
			count = service.deleteInitiationReq(InitiationReqId);
		} catch (Exception e) {

		}
		Gson json = new Gson();
		return json.toJson(count);
	}

	@RequestMapping(value = "deletetestPlan.htm", method = { RequestMethod.GET })
	public @ResponseBody String deletetestPlan(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
			throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside deleteInitiationReq.htm" + UserId);
		long count = 0;
		try {
			String TestId = req.getParameter("TestId");
			count = service.deleteTestPlan(TestId);
		} catch (Exception e) {
		}
		Gson json = new Gson();
		return json.toJson(count);
	}

	@RequestMapping(value = "UpdateSqrSerial.htm", method = { RequestMethod.GET })
	public @ResponseBody String UpdateSqrSerial(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
			throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside UpdateSqrSerial.htm" + UserId);
		long count = 0;
		try {
			String paraId = req.getParameter("paraid");
			String serialNo = req.getParameter("serialNo");

			String[] paraIds = paraId.split(",");
			String[] serialNoS = serialNo.split(",");

			for (int i = 0; i < paraIds.length; i++) {
				String para = paraIds[i];
				String sn = serialNoS[i];
				count = service.updateSerialParaNo(para, sn);
			}

		} catch (Exception e) {

		}
		Gson json = new Gson();
		return json.toJson(count);
	}

	@RequestMapping(value = "MainSpecificationAdd.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String MainSpecificationAdd(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
			throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside MainSpecificationAdd.htm" + UserId);
		try {
			String initiationId = req.getParameter("initiationId");
			String projectId = req.getParameter("projectId");
			String productTreeMainId = req.getParameter("productTreeMainId");
			String SpecsInitiationId = req.getParameter("SpecsInitiationId");

			String[] specificationCode = req.getParameterValues("specificationCode");
			String[] SpecificationName = req.getParameterValues("SpecificationName");
			long count = service.addSpecMaster(specificationCode, SpecificationName);
			redir.addAttribute("projectId", projectId);
			redir.addAttribute("initiationId", initiationId);
			redir.addAttribute("productTreeMainId", productTreeMainId);
			redir.addAttribute("SpecsInitiationId", SpecsInitiationId);
			redir.addAttribute("click", "c");
		} catch (Exception e) {
			// TODO: handle exception
		}

		return "redirect:/SpecificaionDetails.htm";
	}

	@RequestMapping(value = "MainTestPlanAdd.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String MainTestPlanAdd(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside MainTestPlanAdd.htm" + UserId);
		try {
			String initiationId = req.getParameter("initiationId");
			String projectId = req.getParameter("projectId");
			String productTreeMainId = req.getParameter("productTreeMainId");
			String testPlanInitiationId = req.getParameter("testPlanInitiationId");

			String[] testplanCode = req.getParameterValues("testplanCode");
			String[] testplanName = req.getParameterValues("testplanName");
			long count = service.addTestMaster(testplanCode, testplanName);
			redir.addAttribute("projectId", projectId);
			redir.addAttribute("initiationId", initiationId);
			redir.addAttribute("productTreeMainId", productTreeMainId);
			redir.addAttribute("testPlanInitiationId", testPlanInitiationId);
			redir.addAttribute("click", "c");
		} catch (Exception e) {
			// TODO: handle exception
		}

		return "redirect:/TestDetails.htm";
	}

	@RequestMapping(value = "SpecificationSelectSubmit.htm", method = { RequestMethod.POST })
	public String SpecificationSelectSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
			throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside MainSpecificationAdd.htm" + UserId);
		String initiationId = req.getParameter("initiationId");
		String projectId = req.getParameter("projectId");
		String productTreeMainId = req.getParameter("productTreeMainId");
		String SpecsInitiationId = req.getParameter("SpecsInitiationId");
		try {
			if (SpecsInitiationId.equals("0")) {
				SpecsInitiationId = Long.toString(service.SpecificationInitiationAddHandling(initiationId, projectId,
						productTreeMainId, EmpId, UserId, null, null));
			}

			String[] specValue = req.getParameterValues("specValue");
			long count = 0;
			for (int i = 0; i < specValue.length; i++) {
				Specification specs = new Specification();
				specs.setCreatedBy(UserId);
				specs.setCreatedDate(sdf1.format(new Date()));
				String[] s = specValue[i].split("/");
				specs.setMainId(Long.parseLong(s[0]));
				specs.setParentId(0l);
				specs.setSpecificationName(s[1]);
				specs.setSpecsInitiationId(Long.parseLong(SpecsInitiationId));
				specs.setIsActive(1);
				specs.setIsMasterData("N");
				count = projectservice.addSpecification(specs);
			}
			if (count > 0) {
				redir.addAttribute("result", "Specification  added successfully ");
			} else {
				redir.addAttribute("resultfail", "Specification add unsuccessful ");
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			redir.addAttribute("resultfail", "Specification add unsuccessful ");
		}
		redir.addAttribute("projectId", projectId);
		redir.addAttribute("initiationId", initiationId);
		redir.addAttribute("productTreeMainId", productTreeMainId);
		redir.addAttribute("SpecsInitiationId", SpecsInitiationId);
		return "redirect:/SpecificaionDetails.htm";
	}

	@RequestMapping(value = "TestPlanSelectSubmit.htm", method = RequestMethod.POST)
	public String TestPlanSelectSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)
			throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside TestPlanSelectSubmit.htm " + UserId);
		try {

			String initiationId = req.getParameter("initiationId");
			String projectId = req.getParameter("projectId");
			String productTreeMainId = req.getParameter("productTreeMainId");
			String testPlanInitiationId = req.getParameter("testPlanInitiationId");

			if (testPlanInitiationId.equals("0")) {
				testPlanInitiationId = Long.toString(service.testPlanInitiationAddHandling(initiationId, projectId,
						productTreeMainId, EmpId, UserId, null, null));
			}

			String[] testValue = req.getParameterValues("testValue");
			long count = 0l;
			for (int i = 0; i < testValue.length; i++) {
				TestDetails Td = new TestDetails();

				String[] sub = testValue[i].split("/");

				Td.setParentId(0l);
				Td.setMainId(Long.parseLong(sub[0]));
				Td.setTestDetailsId(sub[2] + "_" + sub[1]);
				Td.setName(sub[1]);
				Td.setIsActive(1);
				Td.setTestPlanInitiationId(Long.parseLong(testPlanInitiationId));
				count = service.TestDetailsAdd(Td);

			}

			if (count > 0) {
				redir.addAttribute("result", "Test  Details added Successfully");
			} else {
				redir.addAttribute("resultfail", "Test Details add Unsuccessful");
			}

			redir.addAttribute("projectId", projectId);
			redir.addAttribute("initiationId", initiationId);
			redir.addAttribute("productTreeMainId", productTreeMainId);
			redir.addAttribute("testPlanInitiationId", testPlanInitiationId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/TestDetails.htm";

	}

	@RequestMapping(value = "DeleteSpecificationMembers.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String DeleteSpecificationMembers(RedirectAttributes redir, HttpServletRequest req, HttpServletResponse res,
			HttpSession ses) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		String Logintype = (String) ses.getAttribute("LoginType");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside DeleteSpecificationMembers.htm " + UserId);
		try {

			String SpecsInitiationId = req.getParameter("SpecsInitiationId");
			String projectId = req.getParameter("projectId");
			String initiationId = req.getParameter("initiationId");
			String productTreeMainId = req.getParameter("productTreeMainId");
			String MemeberId = req.getParameter("MemeberId");

			DocMembers idm = projectservice.getDocMemberById(Long.parseLong(MemeberId));
			idm.setIsActive(0);
			long IgiMemeberIdAfterDelete = projectservice.editDocMember(idm);
			

			if (IgiMemeberIdAfterDelete > 0) {
				redir.addAttribute("result", "Members Deleted Successfully for Document Distribution");
			} else {
				redir.addAttribute("resultfail", "Member deleting unsuccessful ");
			}
			redir.addAttribute("initiationId", initiationId);
			redir.addAttribute("projectId", projectId);
			redir.addAttribute("productTreeMainId", productTreeMainId);
			redir.addAttribute("SpecsInitiationId", SpecsInitiationId);
			redir.addAttribute("projectType", req.getParameter("projectType"));

			return "redirect:/ProjectSpecificationDetails.htm";

		} catch (Exception e) {
			e.printStackTrace();

			logger.info(new Date() + "Inside DeleteSpecificationMembers.htm" + UserId);
		}
		return "static/Error";
	}

	@RequestMapping(value = "DeleteTestPlanMembers.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String DeleteTestPlanMembers(RedirectAttributes redir, HttpServletRequest req, HttpServletResponse res,
			HttpSession ses) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		String Logintype = (String) ses.getAttribute("LoginType");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() + "Inside DeleteTestPlanMembers.htm " + UserId);
		try {

			String testPlanInitiationId = req.getParameter("testPlanInitiationId");
			String projectId = req.getParameter("projectId");
			String initiationId = req.getParameter("initiationId");
			String productTreeMainId = req.getParameter("productTreeMainId");
			String MemeberId = req.getParameter("MemeberId");

			DocMembers idm = projectservice.getDocMemberById(Long.parseLong(MemeberId));
			idm.setIsActive(0);
			long IgiMemeberIdAfterDelete = projectservice.editDocMember(idm);
		

			if (IgiMemeberIdAfterDelete > 0) {
				redir.addAttribute("result", "Members Deleted Successfully for Document Distribution");
			} else {
				redir.addAttribute("resultfail", "Member deleting unsuccessful ");
			}
			redir.addAttribute("initiationId", initiationId);
			redir.addAttribute("projectId", projectId);
			redir.addAttribute("productTreeMainId", productTreeMainId);
			redir.addAttribute("testPlanInitiationId", testPlanInitiationId);
			redir.addAttribute("projectType", req.getParameter("projectType"));

			return "redirect:/ProjectTestPlanDetails.htm";

		} catch (Exception e) {
			e.printStackTrace();

			logger.info(new Date() + "Inside DeleteTestPlanMembers.htm" + UserId);
		}
		return "static/Error";
	}

	@RequestMapping(value = "RequirementVerifyMaster.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String RequirementVerifyMaster(HttpServletRequest req, HttpSession ses, HttpServletResponse res,
			RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside RequirementVerifyMaster.htm" + UserId);
		try {
			String initiationid = req.getParameter("initiationId");
			String ProjectId = req.getParameter("projectId");
			String projectType = req.getParameter("projectType");
			String verificationId = req.getParameter("verificationId");

		

			if (initiationid == null) {
				initiationid = "0";
			}
			if (ProjectId == null) {
				ProjectId = "0";
			}
			if (verificationId == null) {
				verificationId = "1";
			}

			req.setAttribute("verificationId", verificationId);
			req.setAttribute("initiationid", initiationid);
			req.setAttribute("projectId", ProjectId);
			req.setAttribute("project", req.getParameter("project"));
			req.setAttribute("reqInitiationId", req.getParameter("reqInitiationId"));
			req.setAttribute("projectType", projectType);
			req.setAttribute("VerifiyMasterList", service.getVerificationListMaster());
			req.setAttribute("verificationDataList", service.getverificationDataList(verificationId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "requirements/VerificationDocMaster";
	}

	@RequestMapping(value = "RequirementVerifyDataAdd.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String RequirementVerifyDataAdd(HttpServletRequest req, HttpSession ses, HttpServletResponse res,
			RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside RequirementVerifyDataAdd.htm" + UserId);
		try {
			String initiationid = req.getParameter("initiationId");
			String ProjectId = req.getParameter("projectId");
			String projectType = req.getParameter("projectType");
			String[] TestType = req.getParameterValues("TestType");
			String[] Purpose = req.getParameterValues("Purpose");
			String verificationId = req.getParameter("verificationId");

			if (initiationid == null) {
				initiationid = "0";
			}
			if (ProjectId == null) {
				ProjectId = "0";
			}

			List<VerificationData> verifyList = new ArrayList<VerificationData>();

			if (TestType != null && TestType.length > 0) {
				for (int i = 0; i < TestType.length; i++) {

					VerificationData verifiData = new VerificationData();
					verifiData.setVerificationMasterId(Long.parseLong(verificationId));

					verifiData.setTypeofTest(TestType[i]);
					verifiData.setPurpose(Purpose[i]);
					verifiData.setCreatedBy(UserId);
					verifiData.setCreatedDate(sdf1.format(new Date()));
					verifiData.setIsActive(1);
					verifyList.add(verifiData);
				}
			}

			long count = service.addVerificationData(verifyList);

			if (count > 0) {
				redir.addAttribute("result", "Verification Data Added Successfully");
			} else {
				redir.addAttribute("resultfail", "Verification Data Add Unsuccessful");
			}

			redir.addAttribute("verificationId", verificationId);
			redir.addAttribute("initiationId", initiationid);
			redir.addAttribute("projectId", ProjectId);
			redir.addAttribute("project", req.getParameter("project"));
			redir.addAttribute("reqInitiationId", req.getParameter("reqInitiationId"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/RequirementVerifyMaster.htm";
	}

	@RequestMapping(value = "RequirementVerifyDataEdit.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String RequirementVerifyDataEdit(HttpServletRequest req, HttpSession ses, HttpServletResponse res,
			RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside RequirementVerifyDataEdit.htm" + UserId);
		try {
			String initiationid = req.getParameter("initiationId");
			String ProjectId = req.getParameter("projectId");
			String projectType = req.getParameter("projectType");
			String verificationDataId = req.getParameter("verificationDataId");
			String TestType = req.getParameter("TestType" + verificationDataId);
			String Purpose = req.getParameter("Purpose" + verificationDataId);
			String verificationId = req.getParameter("verificationMasterId");

			if (initiationid == null) {
				initiationid = "0";
			}
			if (ProjectId == null) {
				ProjectId = "0";
			}

			VerificationData verifiData = new VerificationData();
			verifiData.setVerificationDataId(Long.parseLong(verificationDataId));
			verifiData.setVerificationMasterId(Long.parseLong(verificationId));
			verifiData.setTypeofTest(TestType);
			verifiData.setPurpose(Purpose);
			verifiData.setModifiedBy(UserId);
			verifiData.setModifiedDate(sdf1.format(new Date()));

			long count = service.verificationDataEdit(verifiData);

			if (count > 0) {
				redir.addAttribute("result", "Verification Data Edited Successfully");
			} else {
				redir.addAttribute("resultfail", "Verification Data Edit Unsuccessful");
			}

			redir.addAttribute("verificationId", verificationId);
			redir.addAttribute("initiationId", initiationid);
			redir.addAttribute("projectId", ProjectId);
			redir.addAttribute("project", req.getParameter("project"));
			redir.addAttribute("reqInitiationId", req.getParameter("reqInitiationId"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/RequirementVerifyMaster.htm";
	}
	
	@RequestMapping(value = "specificationMasterAddSubmit.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String specificationMasterAdd(HttpServletRequest req, HttpSession ses, HttpServletResponse res,
			RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside specificationMasterAddSubmit.htm" + UserId);
		try {
			String SpecsMasterId = req.getParameter("SpecsMasterId");
			String action = req.getParameter("action");
			SpecificationMaster sp =action.equalsIgnoreCase("update")? service.getSpecificationMasterById(Long.parseLong(SpecsMasterId)): new SpecificationMaster();

			String numberOfChild = req.getParameter("numberOfChild");

			List<Object[]>subSpecificationListLevel1 = new ArrayList<>();
			List<Object[]>subSpecificationListLevel2 = new ArrayList<>();
			List<Object[]> SpecificationMasterList=service.SpecificationMasterList();

			if(action.equalsIgnoreCase("update")) {
				for(Object[]obj:SpecificationMasterList) {
					if(obj[14].toString().equalsIgnoreCase(SpecsMasterId)) {
						subSpecificationListLevel1.add(obj);
						subSpecificationListLevel2=SpecificationMasterList.stream()
								.filter(e->e[14].toString().equalsIgnoreCase(obj[0].toString()))
								.collect(Collectors.toList());
						subSpecificationListLevel1.addAll(subSpecificationListLevel2);
					}
				}
				for(Object[]obj:subSpecificationListLevel1) {
					SpecificationMaster sp1 =service.getSpecificationMasterById(Long.parseLong(obj[0].toString()));
					sp1.setIsActive(0);
					service.specMasterAddSubmit(sp1);
				}
			}


			String specType = req.getParameter("SpecType");	
			String[] splitSpecType = specType!=null?specType.split("/"): null;
			Long specTypeId = splitSpecType!=null?Long.parseLong(splitSpecType[0]):0L;
			String specTypeCode = splitSpecType!=null?splitSpecType[1]:"-";

			String SpecsInitiationId = "";
			//sp.setSpecificationName(req.getParameter("SpecificationName"));
			sp.setDescription(req.getParameter("description"));
			sp.setSpecsParameter(req.getParameter("specParameter"));
			sp.setSpecsUnit(req.getParameter("specUnit"));
			sp.setSpecValue(req.getParameter("specValue"));
			sp.setMaximumValue(req.getParameter("maxValue"));
			sp.setMinimumValue(req.getParameter("minValue"));
			sp.setSpecTypeId(specTypeId);



			String[] subids = req.getParameter("subid").split("#");
			int subsytemspcCount=0;
			if(SpecsMasterId!=null) {
				sp.setModifiedBy(UserId);
				sp.setModifiedDate(LocalDate.now().toString());
			}else {
				sp.setCreatedBy(UserId);
				sp.setCreatedDate(LocalDate.now().toString());
				sp.setSid(Long.parseLong(req.getParameter("sid")));



				sp.setMainId(Long.parseLong(subids[0]));


				if(SpecificationMasterList!=null) {
					SpecificationMasterList=SpecificationMasterList.stream().filter(e->e[12].toString().equalsIgnoreCase(req.getParameter("sid")) && 
																					   e[13].toString().equalsIgnoreCase(subids[0]) &&
																					   e[14].toString().equalsIgnoreCase("0") &&
																					   Long.parseLong(e[19].toString())==(specTypeId))
																			.collect(Collectors.toList());
					if(SpecificationMasterList!=null && SpecificationMasterList.size()>0) {
						subsytemspcCount=SpecificationMasterList.size();
					}
				}

				String seqCount = String.format("%04d", subsytemspcCount + 1);

				sp.setSpecCount(subsytemspcCount+1);
				//SpecsInitiationId set it first
				SpecsInitiationId = subids[1]+"_"+specTypeCode+"_"+(seqCount);
				sp.setSpecsInitiationId(SpecsInitiationId);
				sp.setParentId(0L);
				sp.setIsActive(1);
			}
			long count = service.specMasterAddSubmit(sp);
			SpecsInitiationId=sp.getSpecsInitiationId();
			subsytemspcCount=sp.getSpecCount();
			if(numberOfChild!=null && !numberOfChild.isEmpty()) {
				int numberOfChilds = Integer.parseInt(numberOfChild);
				for(int i=1;i<=numberOfChilds;i++) {

					SpecificationMaster sp1 = new SpecificationMaster();

					sp1.setDescription(req.getParameter("description_"+i));
					sp1.setSpecValue(req.getParameter("specValue_"+i));
					sp1.setSpecsParameter(req.getParameter("specParameter_"+i));

					if(!req.getParameter("specUnit_"+i).isEmpty()) {
						sp1.setSpecsUnit(req.getParameter("specUnit_"+i));
					}

					if(!req.getParameter("maxValue_"+i).isEmpty()) {
						sp1.setMaximumValue(req.getParameter("maxValue_"+i));
					}
					if(!req.getParameter("minValue_"+i).isEmpty()) {
						sp1.setMinimumValue(req.getParameter("minValue_"+i));
					}
					sp1.setSid(Long.parseLong(req.getParameter("sid")));
					sp1.setMainId(Long.parseLong(subids[0]));
					sp1.setParentId(count);
					sp1.setSpecsInitiationId(SpecsInitiationId+"_"+i);
					sp1.setCreatedBy(UserId);
					sp1.setCreatedDate(LocalDate.now().toString());
					sp1.setIsActive(1);
					sp1.setSpecCount(subsytemspcCount);
					sp1.setSpecTypeId(specTypeId);
					long result = service.specMasterAddSubmit(sp1);

					String[] specParameters = req.getParameterValues(i+"_specParameter");
					String[]specUnits = req.getParameterValues(i+"_specUnit");
					String[]specValues = req.getParameterValues(i+"_specValue");
					String[] maxValues = req.getParameterValues(i+"_maxValue");
					String[]minValues = req.getParameterValues(i+"_minValue");
					String[]descriptions= req.getParameterValues(i+"_description");

					if(specParameters!=null) {
						for(int j=0;j<specParameters.length;j++) {

							SpecificationMaster sp2 = new SpecificationMaster();
							sp2.setDescription(descriptions[j]);
							sp2.setSpecsParameter(specParameters[j]);
							sp2.setSpecValue(specValues[j]);
							if(specUnits.length!=0) {
								sp2.setSpecsUnit(specUnits[j]);
							}
							if(maxValues.length!=0) {
								sp2.setMaximumValue(maxValues[j]);
							}
							if(minValues.length!=0) {
								sp2.setMinimumValue(minValues[j]);
							}

							sp2.setSid(Long.parseLong(req.getParameter("sid")));
							sp2.setMainId(Long.parseLong(subids[0]));
							sp2.setParentId(result);
							sp2.setSpecsInitiationId(SpecsInitiationId+"_"+i+"_"+(j+1));
							sp2.setCreatedBy(UserId);
							sp2.setCreatedDate(LocalDate.now().toString());
							sp2.setIsActive(1);
							sp2.setSpecCount(subsytemspcCount);
							sp2.setSpecTypeId(specTypeId);
							long result2 = service.specMasterAddSubmit(sp2);
						}
					}

				}
			}




			//			long count = service.specMasterAddSubmit(sp);
			if (count> 0) {
				if(action.equalsIgnoreCase("update")) {
					redir.addAttribute("result", "Specification Data Edited Successfully");
				}else {
					redir.addAttribute("result", "Specification Data Added Successfully");	
				}
			} else {
				redir.addAttribute("resultfail", "Specification Data Add Unsuccessful");
			}
			redir.addAttribute("specTypeId", specTypeId);
			redir.addAttribute("mainid", subids[0]);
		} catch (Exception e) {
			e.printStackTrace();
			return "static/Error";
		}

		return "redirect:/SpecificationMasters.htm";
	}
	
	@RequestMapping(value="getSpecificationMasterById.htm",method=RequestMethod.GET)
	public @ResponseBody String getSpecificationMasterById(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception {

		String SpecsMasterId = req.getParameter("SpecsMasterId");
		SpecificationMaster sp =service.getSpecificationMasterById(Long.parseLong(SpecsMasterId));
		
		Gson json = new Gson();
	
		return json.toJson(sp);
	}
	
	@RequestMapping(value = "TestPlanMaster.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String TestPlanMaster(HttpServletRequest req, HttpServletResponse res, HttpSession ses,
			RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String LoginType = (String) ses.getAttribute("LoginType");

		logger.info(new Date() + "Inside TestPlanMaster.htm" + UserId);
		List<Object[]> TestPlanMasterList = null;
		try {
			TestPlanMasterList = service.TestPlanMaster();
			req.setAttribute("TestPlanMasterList", TestPlanMasterList);
			req.setAttribute("StagesApplicable", service.StagesApplicable());
		
			List<TestSetupMaster>master = service.getTestSetupMaster();
			
			if(master!=null && master.size()>0) {
				master = master.stream().filter(e->e.getIsActive()==1).collect(Collectors.toList());
			}
			List<TestInstrument>instrumentList = service.getTestInstrument();
			req.setAttribute("testSetupMasterMaster", master);		
			req.setAttribute("specificarionMasterList", service.SpecificationMasterList());
		
			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(LabCode));
			req.setAttribute("LabList", projectservice.LabListDetails(LabCode));
			req.setAttribute("drdologo", LogoUtil.getDRDOLogoAsBase64String());
			req.setAttribute("DocTempAttributes", projectservice.DocTempAttributes());

		} catch (Exception e) {

			e.printStackTrace();
			logger.error(new Date() + " Inside TestPlanMaster.htm " + UserId, e);
			return "static/Error";

		}
		return "requirements/TestPlanMasterList";

	}
	
	@RequestMapping(value = "TestPlanMasterAdd.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String TestPlanMasterAdd( HttpServletRequest req, HttpServletResponse res, HttpSession ses,
			RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() + "Inside TestPlanMasterAdd.htm" + UserId);
		req.setAttribute("DocTempAttributes", projectservice.DocTempAttributes());
		try {
			String TestMasterId = req.getParameter("Did");
			String action = req.getParameter("sub");
			TestPlanMaster tp = TestMasterId!=null?service.getTestPlanById(Long.parseLong(TestMasterId)):new TestPlanMaster();

			// delete Test Plan Master
			if (TestMasterId != null  && action != null && "delete".equalsIgnoreCase(action)) {
				tp.setIsActive(0);
				long delete = service.testPlanMasterAdd(tp);

				if (delete != 0) redir.addAttribute("result", "Test Plan Master  Deleted Successfully");
				else redir.addAttribute("resultfail", "Test Plan Master  Delete Unsuccessful");

				return "redirect:/TestPlanMaster.htm";

			}
			
			req.setAttribute("TestPlanMaster", tp);
			
			if(tp!=null && tp.getToolsSetup()!=null) {
			String setUpId = tp.getToolsSetup().split(", ")[0];
			Long id = 0l;
			if(setUpId.length()>0 ) {
				id = Long.valueOf(setUpId);
			}
			
			TestSetupMaster ts = service.getTestSetupMasterById(id);
			if(ts!=null) {
				  
				File my_file = null;
				
				if(ts.getTdrsData()!=null) {
				Path path = Paths.get(uploadpath,LabCode,"Test SetUp", ts.getTdrsData());
				
				my_file = path.toFile(); 
			  
				if(my_file.exists()) {
			  try (FileInputStream fis = new FileInputStream(my_file)) {
					String htmlContent = convertExcelToHtml(fis);
					
					req.setAttribute("htmlContent", htmlContent);
			  }
			}}}}
			req.setAttribute("StagesApplicable", service.StagesApplicable());
			
			List<TestSetupMaster>master = service.getTestSetupMaster();
			
			if(master!=null && master.size()>0) {
				master = master.stream().filter(e->e.getIsActive()==1).collect(Collectors.toList());
			}
			req.setAttribute("testSetupMasterMaster", master);	
			List<TestInstrument>instrumentList = service.getTestInstrument();
				
			req.setAttribute("specificarionMasterList", service.SpecificationMasterList());
			
			return "requirements/TestMasterAdd";
		} catch (Exception e) {

			e.printStackTrace();
			logger.error(new Date() + " Inside TestPlanMasterAdd.htm " + UserId, e);
			return "static/Error";

		}

	}
	
	
	@RequestMapping(value = "TestMasterAddSubmit.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String TestMasterAddSubmit(HttpServletRequest req, HttpSession ses, HttpServletResponse res,
			RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside TestMasterAddSubmit.htm" + UserId);
		try {
			String TestMasterId = req.getParameter("TestMasterId");
			String action = req.getParameter("action");
			TestPlanMaster tp = action.equalsIgnoreCase("update") ?service.getTestPlanById(Long.parseLong(TestMasterId)):new TestPlanMaster();
			
			StringBuilder toolSB= new StringBuilder("");
			
			StringBuilder linkedSpecSb= new StringBuilder("");
			
			
			String[] ToolsSetup=req.getParameterValues("ToolsSetup");
			String[] linkedSpec = req.getParameterValues("linkedSpec");
			String[] StageApplicable=req.getParameterValues("StageApplicable");
			
			String[] cycle = req.getParameterValues("rows");
			String[] cycleRows = req.getParameterValues("cycle");
			
			
			String stageSb= "";
			String cycleSb= "";
			String rowSb= "";
			if(StageApplicable!=null && StageApplicable.length>0) {
				stageSb=Arrays.stream(StageApplicable).collect(Collectors.joining(", "));
			}
			
			if (action.equalsIgnoreCase("update") && tp.getStageApplicable()!=null ) {
				if(StageApplicable!=null && StageApplicable.length>0) {
					stageSb= stageSb+", "+tp.getStageApplicable();
				}else {
					stageSb = tp.getStageApplicable();
				}
			}
			
			
			if(cycleRows!=null && cycleRows.length>0) {
				
				rowSb = Arrays.stream(cycleRows).collect(Collectors.joining(", ") );
			}
			
			
			if (action.equalsIgnoreCase("update")
					&& tp.getNumberofCycles()!=null && 
					!tp.getNumberofCycles().equalsIgnoreCase("0") ) 
			{
				if(StageApplicable!=null && StageApplicable.length>0) {
					rowSb=rowSb+", "+tp.getNumberofCycles();
				}else {
					rowSb = tp.getNumberofCycles();
				}
			}
			
			
			
			if(cycle!=null && cycle.length>0 ) {
				
				cycleSb = Arrays.stream(cycle).collect(Collectors.joining(", "));
						
			}
			
			
			if (action.equalsIgnoreCase("update") && tp.getNumberofRows()!=null && !tp.getNumberofRows().equalsIgnoreCase("0")) {
				
				if(StageApplicable!=null && StageApplicable.length>0) {
					cycleSb = cycleSb+", "+tp.getNumberofRows();
				}else {
					cycleSb= tp.getNumberofRows();
				}
				
			}
				
			
			if(ToolsSetup!=null) {
				for(int i=0;i<ToolsSetup.length;i++) {
					toolSB.append(ToolsSetup[i]);
					if(i!=ToolsSetup.length-1) {toolSB.append(", ");}
				}}
			if(linkedSpec!=null) {
				for(int i=0;i<linkedSpec.length;i++) {
					linkedSpecSb.append(linkedSpec[i]);
					if(i!=linkedSpec.length-1) {linkedSpecSb.append(", ");}
				}}
			
			
			
			tp.setName(req.getParameter("name"));
			tp.setObjective(req.getParameter("Objective"));
			tp.setMethodology(req.getParameter("Methodology"));
			
			tp.setToolsSetup(toolSB.toString());
			
			
			tp.setConstraints(req.getParameter("Constraints"));
			tp.setEstimatedTimeIteration(req.getParameter("EstimatedTimeIteration"));
			tp.setIterations(req.getParameter("Iterations"));
			tp.setSchedule(req.getParameter("Schedule"));
			tp.setPass_Fail_Criteria(req.getParameter("PassFailCriteria"));
			
			tp.setPreConditions(req.getParameter("PreConditions"));
			tp.setPostConditions(req.getParameter("PostConditions"));
			tp.setSafetyRequirements(req.getParameter("SafetyReq"));
			tp.setDescription(req.getParameter("Description"));
			tp.setPersonnelResources(req.getParameter("PersonnelResources"));
			tp.setRemarks(req.getParameter("remarks"));;
			tp.setTimeType(req.getParameter("TimeType"));
			tp.setLinkedSpecids(linkedSpecSb.toString());
			tp.setStageApplicable(stageSb.toString());
			tp.setNumberofCycles(rowSb.toString() );
			tp.setNumberofRows(cycleSb.toString() );
			if(TestMasterId!=null) {
				tp.setModifiedBy(UserId);
				tp.setModifiedDate(LocalDate.now().toString());
				}else {
				tp.setCreatedBy(UserId);
				tp.setCreatedDate(LocalDate.now().toString());
				tp.setIsActive(1);
				}
			
			
			long count = service.testPlanMasterAdd(tp);
			if (count > 0) {
				if(action.equalsIgnoreCase("update")) {
				redir.addAttribute("result", "TestPlan Data Edited Successfully");
				}else {
					redir.addAttribute("result", "TestPlan Data Added Successfully");	
				}
			} else {
				redir.addAttribute("resultfail", "TestPlan Data Add Unsuccessful");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "redirect:/TestPlanMaster.htm";
		
	}
	
	@RequestMapping(value="AddTestDetailsFromMaster.htm",method=RequestMethod.GET)
	public @ResponseBody String AddTestDetailsFromMaster(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception {
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside AddTestDetailsFromMaster.htm" + UserId);
		long result=0;
		try {
			
			String MasterIds= req.getParameter("MasterIds");
			List<TestPlanMaster>tp= service.getAllTestPlans();
			String [] MasterId= MasterIds.split(",");
			
			String projectId= req.getParameter("projectId");
			String initiationId= req.getParameter("initiationId");
			String productTreeMainId= req.getParameter("productTreeMainId");
			String testPlanInitiationId= req.getParameter("testPlanInitiationId");
			String nextCount= req.getParameter("nextCount");
			int count= Integer.parseInt(nextCount);
			if (initiationId == null)
				initiationId = "0";
			if (projectId == null)
				projectId = "0";
			if (productTreeMainId == null)
				productTreeMainId = "0";

			String specsInitiationId = service.getFirstVersionSpecsInitiationId(initiationId, projectId,
					productTreeMainId) + "";
			
			List<Object[]>sepcList = service.getSpecsList(specsInitiationId);
			if (testPlanInitiationId==null ||testPlanInitiationId.equals("0")) {
				testPlanInitiationId = Long.toString(service.testPlanInitiationAddHandling(initiationId, projectId,
						productTreeMainId, EmpId, UserId, null, null));
			}
			List<Object[]> specificationMasterList = service.SpecificationMasterList();
			for(String id:MasterId) {
				Optional<TestPlanMaster> optionalTestPlanMaster = tp.stream()
				        .filter(e -> e.getTestMasterId().toString().equalsIgnoreCase(id))
				        .findFirst();
			TestPlanMaster testPlanMaster = optionalTestPlanMaster.orElse(null);
			if(testPlanMaster!=null) {
				
				List<String>specIds = Arrays.asList(testPlanMaster.getLinkedSpecids().split(", "));				
	
			
				
				List<String>specNames = specificationMasterList.stream()
										.filter(e->specIds.contains(e[0].toString() ) )
										.map(e->e[5].toString())
										.collect(Collectors.toList());
				
				
				
				List<String>specids = new ArrayList<>();
				
				if(specNames!=null && specNames.size()>0) {
					specids = sepcList.stream()	
								.filter(e->specNames.contains(e[1].toString()))
								.map(e->e[0].toString() )
								.collect(Collectors.toList() );		
					}
				System.out.println(specids.toString());
				
				
				String Testtype = "TEST";
				String TestDetailsId = "";
				if (count < 90) {
					count=count+10;
					TestDetailsId = Testtype + ("000" + (count));
				} else if (count < 990L) {
					count=count+10;
					TestDetailsId = Testtype + ("00" + (count));
				} else {
					count=count+10;
					TestDetailsId = Testtype + ("0" + (count));
				}
				
				TestDetails Td = new TestDetails();
				Td.setTestDetailsId(TestDetailsId);
				Td.setName(testPlanMaster.getName());
				Td.setDescription(testPlanMaster.getDescription());
				Td.setObjective(testPlanMaster.getObjective());
				Td.setConstraints(testPlanMaster.getConstraints());
				Td.setEstimatedTimeIteration(testPlanMaster.getEstimatedTimeIteration());
				Td.setTimetype(testPlanMaster.getTimeType());
				Td.setIterations(testPlanMaster.getIterations());
				Td.setTestCount(count);
				Td.setPass_Fail_Criteria(testPlanMaster.getPass_Fail_Criteria());
				Td.setStageApplicable(testPlanMaster.getStageApplicable());
				Td.setPreConditions(testPlanMaster.getPreConditions());
				Td.setPostConditions(testPlanMaster.getPostConditions());
				Td.setSafetyRequirements(testPlanMaster.getSafetyRequirements());
				Td.setPersonnelResources(testPlanMaster.getPersonnelResources());
				Td.setIsActive(1);
				Td.setRemarks(testPlanMaster.getRemarks());
				Td.setTestPlanInitiationId(Long.parseLong(testPlanInitiationId));
				Td.setSchedule(testPlanMaster.getSchedule());
				Td.setMethodology(testPlanMaster.getMethodology());	
				Td.setCreatedBy(UserId);
				Td.setCreatedDate(sdf1.format(new Date()));
				Td.setMainId(Long.parseLong(id));
				Td.setToolsSetup(testPlanMaster.getToolsSetup());
				
				Td.setNumberofRows(testPlanMaster.getNumberofRows());
				Td.setNumberofCycles( testPlanMaster.getNumberofCycles()  );			
				Td.setSpecificationId(specids.size()>0? specids.toString().replace("[", "").replace("]", "") :"");
				result= service.TestDetailsAdd(Td);
			}
			
			
			
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside AddTestDetailsFromMaster.htm " + UserId, e);
		}
	
		Gson json = new Gson();
	
		return json.toJson(result);
	}
	
	
	@RequestMapping(value="AddSpecDetailsFromMaster.htm",method=RequestMethod.GET)
	public @ResponseBody String AddSpecDetailsFromMaster(HttpServletRequest req, HttpSession ses, RedirectAttributes redir)throws Exception {
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside AddSpecDetailsFromMaster.htm" + UserId);
		long result=0;
		long result1=0;
		long result2=0;
		Gson json = new Gson();
		try {
			
			String MasterIds= req.getParameter("MasterIds");
			String [] MasterId= MasterIds.split(",");
			
			String projectId= req.getParameter("projectId");
			String initiationId= req.getParameter("initiationId");
			String productTreeMainId= req.getParameter("productTreeMainId");
			String SpecsInitiationId= req.getParameter("SpecsInitiationId");
			String Speccount= req.getParameter("count");
			
			if (initiationId == null)initiationId = "0";
			if (projectId == null)projectId = "0";
			if (productTreeMainId == null)productTreeMainId = "0";
			if(SpecsInitiationId.equals("0") ) {					
				SpecsInitiationId = Long.toString(service.SpecificationInitiationAddHandling(initiationId,projectId,productTreeMainId,EmpId,UserId,null,null));
			}
			List<SpecificationMaster>sp= service.getAllSpecPlans();
			
			int count=Speccount!=null ? Integer.parseInt(Speccount)+10:10;
			for(String id:MasterId) {
				
				Optional<SpecificationMaster>spMaster = sp.stream()
														   .filter(e->e.getSpecsMasterId().toString().equalsIgnoreCase(id)).findFirst();
				
				SpecificationMaster spm = spMaster.orElse(null);
				Specification s = new Specification();
				
				s.setDescription(spm.getDescription());
				s.setSpecsInitiationId(Long.parseLong(SpecsInitiationId));
				s.setLinkedRequirement("");
				s.setLinkedSubSystem("");
				s.setIsMasterData("Y");
				s.setIsActive(1);
				s.setSpecValue(spm.getSpecValue());
				s.setMaximumValue(spm.getMaximumValue());
				s.setMinimumValue(spm.getMinimumValue());
				s.setSpecsParameter(spm.getSpecsParameter());
				s.setSpecsUnit(spm.getSpecsUnit());
				s.setSpecsParameter(spm.getSpecsParameter());
				
				s.setSpecificationName(spm.getSpecsInitiationId());
				s.setMainId(0l);
				s.setParentId(0l);
				s.setCreatedBy(UserId);
				s.setCreatedDate(sdf1.format(new Date()));
				result=projectservice.addSpecification(s);
				
				List<SpecificationMaster>spLevel1=sp.stream()
				.filter(e->e.getParentId().toString().equalsIgnoreCase(id))
				.collect(Collectors.toList());
				
				
				for(SpecificationMaster s1:spLevel1) {
					Specification s1New = new Specification();
					s1New.setDescription(s1.getDescription());
					s1New.setSpecsInitiationId(Long.parseLong(SpecsInitiationId));
					s1New.setLinkedRequirement("");
					s1New.setLinkedSubSystem("");
					s1New.setIsMasterData("Y");
					s1New.setIsActive(1);
					s1New.setSpecValue(s1.getSpecValue());
					s1New.setMaximumValue(s1.getMaximumValue());
					s1New.setMinimumValue(s1.getMinimumValue());
					s1New.setSpecsParameter(s1.getSpecsParameter());
					s1New.setSpecsUnit(s1.getSpecsUnit());
					s1New.setSpecsParameter(s1.getSpecsParameter());
					
					s1New.setSpecificationName(s1.getSpecsInitiationId());
					s1New.setMainId(0l);
					s1New.setParentId(result);
					s1New.setCreatedBy(UserId);
					s1New.setCreatedDate(sdf1.format(new Date()));
					
					result1=projectservice.addSpecification(s1New);
					
					List<SpecificationMaster>spLevel2=sp.stream()
							.filter(e->e.getParentId().equals( s1.getSpecsMasterId()))
							.collect(Collectors.toList());
					
		
			
					for(SpecificationMaster s2:spLevel2) {
						Specification s2New = new Specification();
						s2New.setDescription(s2.getDescription());
						s2New.setSpecsInitiationId(Long.parseLong(SpecsInitiationId));
						s2New.setLinkedRequirement("");
						s2New.setLinkedSubSystem("");
						s2New.setIsMasterData("Y");
						s2New.setIsActive(1);
						s2New.setSpecValue(s2.getSpecValue());
						s2New.setMaximumValue(s2.getMaximumValue());
						s2New.setMinimumValue(s2.getMinimumValue());
						s2New.setSpecsParameter(s2.getSpecsParameter());
						s2New.setSpecsUnit(s2.getSpecsUnit());
						s2New.setSpecsParameter(s2.getSpecsParameter());
						
						s2New.setSpecificationName(s2.getSpecsInitiationId());
						s2New.setMainId(0l);
						s2New.setParentId(result1);
						s2New.setCreatedBy(UserId);
						s2New.setCreatedDate(sdf1.format(new Date()));
						result2=projectservice.addSpecification(s2New);
					}
				}
				
			}
			
		}
		catch (Exception e) {
			logger.error(new Date() + " Inside AddSpecDetailsFromMaster.htm " + UserId, e);
			return json.toJson(result);
		}
		return json.toJson(result);	
	}

	@GetMapping(value="SpecificationMasterDelete.htm" )
	public String specificationMasterDelete(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
		String userId = (String)ses.getAttribute("Username");
		logger.info(new Date()+ " Inside SpecificationMasterDelete.htm "+userId);
		try {
			String specsMasterId = req.getParameter("Did");
			
			int result = service.deleteSpecificationMasterById(specsMasterId);
			
			if (result > 0) {
				redir.addAttribute("result", "Specification(s) Deleted Successfully");
			} else {
				redir.addAttribute("resultfail", "Specification(s) Delete Unsuccessful");
			}
			redir.addAttribute("specTypeId", req.getParameter("specTypeId"));
			
			return "redirect:/SpecificationMasters.htm";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+ " Inside SpecificationMasterDelete.htm "+userId);
			return "static/Error";
		}
	}
	@GetMapping(value="TestSetUpMaster.htm" )
	public String TestSetUpMaster(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
		String userId = (String)ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		String Logintype= (String)ses.getAttribute("LoginType");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date()+ " Inside TestSetUpMaster.htm "+userId);
		try {
			List<TestSetupMaster>master = service.getTestSetupMaster();
			
			if(master!=null && master.size()>0) {
				master = master.stream().filter(e->e.getIsActive()==1).collect(Collectors.toList());
			}
			List<TestInstrument>instrumentList = service.getTestInstrument();
			req.setAttribute("testSetupMasterMaster", master);	

			req.setAttribute("instrumentList", instrumentList);
			
			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(LabCode));
			req.setAttribute("LabList", projectservice.LabListDetails(LabCode));
			req.setAttribute("drdologo", LogoUtil.getDRDOLogoAsBase64String());
			req.setAttribute("DocTempAttributes", projectservice.DocTempAttributes());

			return "requirements/TestSetUpMasterList";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+ " Inside TestSetUpMaster.htm "+userId);
			return "static/Error";
		}
	}
	@RequestMapping(value="TestSetMasterAdd.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String TestSetMasterAdd(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
		String userId = (String)ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		String Logintype= (String)ses.getAttribute("LoginType");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date()+ " Inside TestSetMasterAdd.htm "+userId);
		try {
			List<TestInstrument>instrumentList = service.getTestInstrument();
			req.setAttribute("instrumentList", instrumentList);	
			
				String setUpId = req.getParameter("Did");
				String action = req.getParameter("sub");
				
				if(action!=null && action.equalsIgnoreCase("add")) {	
					
				}else {
				
						if(setUpId!=null) {
			TestSetupMaster tp = service.getTestSetupMasterById(Long.valueOf(setUpId));
			
				// delete test Setup master list
				
			List<TestSetUpAttachment> listOfAttachments = service.getTestSetUpAttachment(setUpId);
			req.setAttribute("listOfAttachments", listOfAttachments);
			
		
			
			
			if(tp!=null) {
			  
				File my_file = null;
				
				if(tp.getTdrsData()!=null) {
				Path path = Paths.get(uploadpath,LabCode,"Test SetUp", tp.getTdrsData());
				
				my_file = path.toFile(); 
			  
				if(my_file.exists()) {
			  try (FileInputStream fis = new FileInputStream(my_file)) {
					String htmlContent = convertExcelToHtml(fis);
					
					req.setAttribute("htmlContent", htmlContent);
			  }
			}}}
			if (action != null && "delete".equalsIgnoreCase(action)) {
					tp.setIsActive(0);
					long delete = service.addTestSetupMaster(tp);
	
					if (delete != 0) redir.addAttribute("result", "SetUp Master  Deleted Successfully");
					else redir.addAttribute("resultfail", "SetUp Master  Delete Unsuccessful");
	
					return "redirect:/TestSetUpMaster.htm";
	
				}
			
			req.setAttribute("tp", tp);	
			}}
			
			List<TestSetupMaster>master = service.getTestSetupMaster();
			
			if(master!=null && master.size()>0) {
				master = master.stream().filter(e->e.getIsActive()==1).collect(Collectors.toList());
			}
			

			
			req.setAttribute("testSetupMasterMaster", master);	
			return "requirements/TestSetUpMasterAdd";
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+ " Inside TestSetMasterAdd.htm "+userId);
			return "static/Error";
		}
	}
	@RequestMapping(value="TestSetUpSubmit.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String TestSetUpSubmit(
			HttpServletRequest req,
			HttpSession ses,
			RedirectAttributes redir,
			@RequestParam("attach") MultipartFile [] attchments,
			@RequestParam("tdrs") MultipartFile tdrs
			) throws Exception {
		String userId = (String)ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		String Logintype= (String)ses.getAttribute("LoginType");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date()+ " Inside TestSetUpSubmit.htm "+userId);
		try {
			
			Random rand = new Random();
			int randomNumber = rand.nextInt(1000);
			
		String testSetUpId = req.getParameter("testSetUpId");
		String objective = req.getParameter("objective");
		String facility = req.getParameter("facility");
		String testSetup = req.getParameter("testSetup");
		String testProcedure = req.getParameter("testProcedure");
		
		String []testInstrument = req.getParameterValues("testInstrument");
		
		String id= req.getParameter("id");
		
	
		
		StringBuilder sb= new StringBuilder("");
		if(testInstrument!=null) {
				
			for(int i=0;i<testInstrument.length;i++) {
				sb.append(testInstrument[i]);
				if(i!=testInstrument.length-1) {
					sb.append(", ")	;			}
			}
			
		}
		
		TestSetupMaster tp =id==null? new TestSetupMaster():
			service.getTestSetupMasterById(Long.valueOf(id));
		
		tp.setTestSetUpId(testSetUpId);
		tp.setObjective(objective);
		tp.setFacilityRequired(facility);
		if(tdrs!= null && !tdrs.isEmpty()  ) {
		tp.setTdrsData(randomNumber+"_"+tdrs.getOriginalFilename() );
		}
		tp.setTestSetUp(testSetup);
		tp.setTestProcedure(testProcedure);	
		tp.setTestInstrument(sb.toString());
		tp.setTdrs(tdrs);
		tp.setLabCode(LabCode);	
		tp.setIsActive(1);
		if(id!=null) {
			tp.setSetupId(Long.valueOf(id));
			tp.setModifiedBy(userId)	;
			tp.setModifiedDate(LocalDateTime.now());
			
		}else {
			tp.setCreatedBy(userId);
			tp.setCreatedDate(LocalDateTime.now());
		}
		
		long count = service.addTestSetupMaster(tp);

		long result = service.saveTestSetUpAttachment(attchments,count,LabCode);
		
		if (count > 0) {
			if(id==null) {
			redir.addAttribute("result", "Test Setup Added Successfully");
			}else {
				redir.addAttribute("result", "Test Setup updated Successfully");	
			}
		} else {
			redir.addAttribute("resultfail", "Oops something went wrong !");
		}

		return "redirect:/TestSetUpMaster.htm";
		

		}catch (Exception e) {
			e.printStackTrace();
			redir.addAttribute("resultfail", "Oops something went wrong !");
			logger.error(new Date()+ " Inside TestSetUpSubmit.htm "+userId);
			return "static/Error";
		}
	}
	@RequestMapping(value="saveTestInstrumentName.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody String saveTestInstrumentName(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception {
		String userId = (String)ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		String Logintype= (String)ses.getAttribute("LoginType");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date()+ " Inside saveTestInstrumentName.htm "+userId);
		try {
			String InstrumentName = req.getParameter("InstrumentName");
			
		
			
			TestInstrument t = new TestInstrument();
			t.setInstrumentName(InstrumentName);

			long count = service.setTestInstrument(t);
			Gson json = new Gson();
			return json.toJson(count);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+ " Inside saveTestInstrumentName.htm "+userId);
			return "static/Error";
		}
	}
	
	
	
	
	@RequestMapping(value = { "TestSetupAttachmentDownload.htm" })
	public void TestSetupAttachmentDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res)
			throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() + "Inside  TestSetupAttachmentDownload" + UserId);
		try {
			String attachmentId = req.getParameter("attachmentId");
			String setupId = req.getParameter("id");
	
			List<TestSetUpAttachment> listOfAttachments = service.getTestSetUpAttachment(setupId);
			
			TestSetUpAttachment tp = listOfAttachments.stream()
				    .filter(a -> attachmentId.equals(a.getAttachmentId().toString() ))
				    .findFirst()
				    .orElse(null);
			
			
			  res.setContentType("Application/octet-stream"); 
			  
			  File my_file = null;
			  
			  Path path = Paths.get(uploadpath,LabCode,tp.getFilePath(),setupId+"_"+tp.getAttachmentFileName());
			  
			  my_file = path.toFile(); 
			  
			  res.setHeader("Content-disposition", "attachment; filename=" +tp.getAttachmentFileName() );
			  OutputStream out = res.getOutputStream();
			  
			  FileInputStream in = new FileInputStream(my_file);
			  byte[] buffer = new byte[4096]; 
			  int length;
			  while((length = in.read(buffer)) > 0)
			  { out.write(buffer, 0, length); }
			  in.close();
			  out.flush(); 
			  out.close();
			 
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside  TestSetupAttachmentDownload" + UserId, e);
		}
	}
	
	@RequestMapping(value = { "TestTdrsAttachmentDownload.htm" })
	public void TestTdrsAttachmentDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res)
			throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() + "Inside  TestTdrsAttachmentDownload" + UserId);
		try {
			
			String setupId = req.getParameter("id");
			
			
			TestSetupMaster tp = service.getTestSetupMasterById(Long.valueOf(setupId));
			
			
			res.setContentType("Application/octet-stream"); 
			
			File my_file = null;
			
			Path path = Paths.get(uploadpath,LabCode,"Test SetUp", tp.getTdrsData());
			
			my_file = path.toFile(); 
			
			res.setHeader("Content-disposition", "attachment; filename=" +tp.getTdrsData()    );
			OutputStream out = res.getOutputStream();
			
			FileInputStream in = new FileInputStream(my_file);
			byte[] buffer = new byte[4096]; 
			int length;
			while((length = in.read(buffer)) > 0)
			{ out.write(buffer, 0, length); }
			in.close();
			out.flush(); 
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside  TestTdrsAttachmentDownload" + UserId, e);
		}
	}

	@RequestMapping(value = "deleteTestSetUpAttachement.htm", method = { RequestMethod.GET })
	public @ResponseBody String deleteTestSetUpAttachement(HttpServletRequest req, HttpSession ses) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		Gson json = new Gson();
		try {
			
			String setUpId = req.getParameter("setUpId");
			String attachmentId = req.getParameter("attachmentId");

		
		
			
			List<TestSetUpAttachment> listOfAttachments = service.getTestSetUpAttachment(setUpId);
			
			TestSetUpAttachment tp = listOfAttachments.stream()
				    .filter(a -> attachmentId.equals(a.getAttachmentId().toString() ))
				    .findFirst()
				    .orElse(null);
			
			tp.setIsActive(0);
			
			service.saveTestSetUpAttachement(tp);
		}catch (Exception e) {
			json.toJson(0);
		}
		
		
		return json.toJson(1);
	}
	
	
	
	@RequestMapping(value = "ProductTreeIntroduction.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String ProductTreeIntroduction(HttpServletRequest req, HttpServletResponse res, HttpSession ses,
			RedirectAttributes redir) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		
		logger.info(new Date() + "Inside ProductTreeIntroduction.htm" + UserId);
		try {
			String Mainid = req.getParameter("Mainid");
			
			if(Mainid!=null) {
				req.setAttribute("Mainid", Mainid);
			}
			
			return "requirements/DocumentIntroduction";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside ProductTreeIntroduction.htm " + UserId, e);
			return "static/Error";
		}
	}
	
	
	@RequestMapping(value = "getSubsytemIntroduction.htm", method = { RequestMethod.GET })
	public @ResponseBody String getSubsytemIntroduction(HttpServletRequest req, HttpSession ses) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() + "Inside getSubsytemIntroduction.htm " + UserId);
		List<PfmsSystemSubIntroduction>  psi = new ArrayList<>();
		try {

			String MainId = req.getParameter("MainId");			
			
			psi = 
		service.getActiveSubIntroductionByMainId(Long.parseLong(MainId));
			
			
			System.out.println("MainId---->"+MainId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside getSubsytemIntroduction.htm" + UserId, e);
		}
		Gson json = new Gson();
		return json.toJson(psi);
	}

	@RequestMapping(value="submitSubIntroDuction.htm",method= {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody String ProjectAdditonalRequirementUpdate(HttpSession ses, HttpServletRequest req)
			throws Exception {

		Gson json = new Gson();
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date()
				+"Inside submitSubIntroDuction.htm"+UserId); 
		long count=0;
		String result="";
		try {
			String MainId = req.getParameter("MainId");
			String Details = req.getParameter("Details");
			String moduleName = req.getParameter("moduleName");
			String introductionId = req.getParameter("introductionId");
			
			
			
			PfmsSystemSubIntroduction ps = new PfmsSystemSubIntroduction();
			if( !introductionId.equalsIgnoreCase("0") ) {
				List<PfmsSystemSubIntroduction>  psi
				=service.getActiveSubIntroductionByMainId(Long.parseLong(MainId));
				
				ps = psi.stream().filter(e->e.getIntroductionId().toString().equalsIgnoreCase(introductionId) )
							.findFirst()
							.orElse(ps);				
				ps.setIntroductionId(Long.parseLong(introductionId));
			}
				
				
				ps.setMainId(Long.parseLong(MainId));
				ps.setIntroduction(moduleName);	
				ps.setDetails(Details);			
				ps.setCreatedBy(UserId);	
				ps.setCreatedDate(LocalDate.now().toString());
				ps.setIsActive(1);			
				
			
			
			count = service.savePfmsSystemSubIntroduction(ps);
		}
		catch(Exception e) {
			logger.error(new Date()+"Inside submitSubIntroDuction.htm"+UserId ,e);
			e.printStackTrace(); 
			return json.toJson(count);
		} 
		return json.toJson(count);
		
	}
	@RequestMapping(value="MilestoneIsActive.htm",method= {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody String MilestoneIsActive(HttpSession ses, HttpServletRequest req)
			throws Exception {
		
		Gson json = new Gson();
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date()
				+"Inside submitSubIntroDuction.htm"+UserId); 
		int count=0;
		String result="";
		try {
			String id = req.getParameter("id");		
			
			count =  service.setMilestoneInActive(id);
			
		}
		catch(Exception e) {
			logger.error(new Date()+"Inside MilestoneIsActive.htm"+UserId ,e);
			e.printStackTrace(); 
			return json.toJson(count);
		} 
		return json.toJson(count);
		
	}
	@RequestMapping(value="deleteSatagesForTestPlan.htm",method= {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody String deleteSatagesForTestPlan(HttpSession ses, HttpServletRequest req)
			throws Exception {
		
		Gson json = new Gson();
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date()
				+"Inside deleteSatagesForTestPlan.htm"+UserId); 
		long count=0;
		String result="";
		try {
			String id = req.getParameter("id");		
			TestPlanMaster tp = service.getTestPlanById(Long.parseLong(id));
			tp.setStageApplicable("");
			tp.setNumberofCycles("" );
			tp.setNumberofRows("" );
			tp.setModifiedBy(UserId);
			tp.setModifiedDate(LocalDate.now().toString());
		    count = service.testPlanMasterAdd(tp);
			
		}
		catch(Exception e) {
			logger.error(new Date()+"Inside deleteSatagesForTestPlan.htm"+UserId ,e);
			e.printStackTrace(); 
			return json.toJson(count);
		} 
		return json.toJson(count);
		
	}
	@RequestMapping(value="getTestPlanMasterList.htm",method= {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody String getTestPlanMasterList(HttpSession ses, HttpServletRequest req)
			throws Exception {
		
		Gson json = new Gson();
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date()
				+"Inside getTestPlanMasterList.htm"+UserId); 
		
		try {
			String testPlanInitiationId = req.getParameter("testPlanInitiationId");
			
			List<Object[]> TestDetailsList = service.TestDetailsList(testPlanInitiationId);
			return json.toJson(TestDetailsList);
		}
		catch(Exception e) {
			logger.error(new Date()+"Inside getTestPlanMasterList.htm"+UserId ,e);
			e.printStackTrace(); 
			return "";
		
			
		} 
	
		
	}


}
