package com.vts.pfms.print.controller;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.utils.PdfMerger;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.properties.TextAlignment;
import com.vts.pfms.CharArrayWriterResponse;
import com.vts.pfms.FormatConverter;
import com.vts.pfms.Zipper;
import com.vts.pfms.cars.service.CARSService;
import com.vts.pfms.committee.model.Committee;
import com.vts.pfms.committee.service.CommitteeService;
import com.vts.pfms.header.service.HeaderService;
import com.vts.pfms.master.dto.ProjectFinancialDetails;
import com.vts.pfms.milestone.dto.MilestoneActivityLevelConfigurationDto;
import com.vts.pfms.milestone.dto.ProjectUtilizationBriefingDto;
import com.vts.pfms.milestone.model.ProjectEconomicImpact;
import com.vts.pfms.milestone.service.MilestoneService;
import com.vts.pfms.model.BriefingFinance;
import com.vts.pfms.model.BriefingHeading;
import com.vts.pfms.model.BriefingHeadingDetails;
import com.vts.pfms.model.TotalDemand;
import com.vts.pfms.pfmsserv.feign.PFMSServeFeignClient;
import com.vts.pfms.print.dto.PfmsBriefingFwdDto;
import com.vts.pfms.print.model.CommitteeProjectBriefingFrozen;
import com.vts.pfms.print.model.FavouriteSlidesModel;
import com.vts.pfms.print.model.InitiationSanction;
import com.vts.pfms.print.model.InitiationsanctionCopyAddr;
import com.vts.pfms.print.model.PfmsBriefingTransaction;
import com.vts.pfms.print.model.ProjectOverallFinance;
import com.vts.pfms.print.model.ProjectSlideFreeze;
import com.vts.pfms.print.model.ProjectSlides;
import com.vts.pfms.print.model.RecDecDetails;
import com.vts.pfms.print.model.TechImages;
import com.vts.pfms.print.service.PrintService;
import com.vts.pfms.project.dto.ProjectSlideDto;
import com.vts.pfms.project.service.ProjectService;
import com.vts.pfms.utils.InputValidator;
import com.vts.pfms.utils.PMSLogoUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@Controller
public class PrintController {

	@Autowired
	PrintService service;
	
	FormatConverter fc=new FormatConverter();
	
	private static final RestTemplate restTemplate = new RestTemplate();
	
	@Autowired
	MilestoneService milservice;
	
	@Autowired
	PFMSServeFeignClient PFMSserv;

	@Value("${server_uri}")
    private String uri;
	
	@Value("${ProjectCost}")
	private long ProjectCost;
	
	@Value("${ApplicationFilesDrive}")
	private String ApplicationFilesDrive;
	
	@Value("${IsIbasConnected}")
	private String IsIbasConnected;

	@Autowired
	Environment env;
	
	@Autowired
	PMSLogoUtil LogoUtil;
	
	@Autowired
	CARSService CarsService;
	
	
	@Autowired
	ProjectService prservice;
	
	
	@Autowired CommitteeService comservice;

	@Autowired
	HeaderService headservice;
	
	@Autowired
	PFMSServeFeignClient pfmsServ;
	
	private static final Logger logger=LogManager.getLogger(PrintController.class);

	private SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private  SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
	
	@RequestMapping(value="PfmsPrint.htm", method = RequestMethod.POST)
	public String PfmsPrint(HttpServletRequest req, HttpSession ses, RedirectAttributes redir,HttpServletResponse res) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
//		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside PfmsPrint.htm "+UserId);		
	    	try {
	    		String InitiationId=req.getParameter("IntiationId");
	    		Object[] PfmsInitiationList= service.PfmsInitiationList(InitiationId).get(0);
	    		String labcode=PfmsInitiationList[17].toString().toLowerCase();
	    		
	   		 	req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(labcode));                     
	    		req.setAttribute("LabList", service.LabList(labcode));
	    		req.setAttribute("PfmsInitiationList", PfmsInitiationList);
	    		req.setAttribute("DetailsList", service.ProjectIntiationDetailsList(InitiationId));
	    		req.setAttribute("CostDetailsList", service.CostDetailsList(InitiationId));
	    		req.setAttribute("ScheduleList", service.ProjectInitiationScheduleList(InitiationId));
			
	    	}				
	    	catch(Exception e) {	    		
	    		logger.error(new Date() +" Inside PfmsPrint.htm "+UserId, e);
	    		e.printStackTrace();
	    		return "static/Error";
		
	    	}		
	    return "print/PfmsPrint";
		
	}
	
	@RequestMapping(value = "CCMReport.htm")
	public void CCMReport(HttpServletRequest req , RedirectAttributes redir, HttpServletResponse res , HttpSession ses)throws Exception
	{
		try {
			req.setAttribute("DRDOLogo", LogoUtil.getDRDOLogoAsBase64String());
			
			String filename="CCMReport";		
	    	
	    	String path=req.getServletContext().getRealPath("/view/temp");
	    	req.setAttribute("path",path); 
	    	CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
	    	req.getRequestDispatcher("/view/print/CCMReport.jsp").forward(req, customResponse);
	    	String html = customResponse.getOutput();
	    	byte[] data = html.getBytes();
	    	InputStream fis1=new ByteArrayInputStream(data);
	    	PdfDocument pdfDoc = new PdfDocument(new PdfWriter(path+"/"+filename+".pdf"));	
	    	
	    	ConverterProperties converterProperties = new ConverterProperties();
	    	FontProvider dfp = new DefaultFontProvider(true, true, true);
	    	converterProperties.setFontProvider(dfp);
	        HtmlConverter.convertToPdf(fis1,pdfDoc,converterProperties);
            ImageData leftLogo = ImageDataFactory.create(env.getProperty("ApplicationFilesDrive")+"\\images\\lablogos\\drdo.png");
	        
	        PdfDocument pdfDocMain = new PdfDocument(new PdfReader(path+File.separator+filename+".pdf"),new PdfWriter(path+File.separator+filename+"Maintemp.pdf"));
	        Rectangle pageSizeMain;
	        
	        Document document = new Document(pdfDocMain); // Use Document for adding images
	        
	        int main = pdfDocMain.getNumberOfPages();
	        for (int i = 1; i <= main; i++) 
	        {
	            PdfPage pageMain = pdfDocMain.getPage(i);
	            pageSizeMain = pageMain.getPageSize();
	            
	            // Left Logo
        	    Image leftImage = new Image(leftLogo);
        	    leftImage.setFixedPosition(i, 54, pageSizeMain.getHeight() - 34);
        	    leftImage.scaleToFit(34, 33);
        	    
        	    // Add images to the document
        	    document.add(leftImage);
	        }
	        // Close document
        	document.close();
	        pdfDocMain.close();
	        Path pathOfFileMain= Paths.get( path+File.separator+filename+".pdf");
	        Files.delete(pathOfFileMain);	
	        
	        res.setContentType("application/pdf");
	        res.setHeader("Content-disposition","inline;filename="+filename+".pdf"); 
	        File f=new File(path+File.separator+filename+"Maintemp.pdf");
	        
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
		}
		catch (Exception e) {
			e.printStackTrace();
//			return "staic/Error";
		}
//		return "print/CCMReport";
	 }
	
	
	@RequestMapping(value="PfmsPrint2.htm", method = RequestMethod.POST)
	public String PfmsPrint2(HttpServletRequest req, HttpSession ses, RedirectAttributes redir,HttpServletResponse res)
			throws Exception {
		
		String UserId = (String) ses.getAttribute("Username");
		String LabCode =(String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside PfmsPrint2.htm "+UserId);		
	    try {
	    	String InitiationId=req.getParameter("IntiationId");		
	    		
	    	
	 		Object[] PfmsInitiationList= service.PfmsInitiationList(InitiationId).get(0);
    		String labcode=PfmsInitiationList[17].toString().toLowerCase();
    		
   		 	req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(labcode));                     
    		req.setAttribute("PfmsInitiationList", PfmsInitiationList);
    		
    	
    	
	    	req.setAttribute("DetailsList", service.ProjectIntiationDetailsList(InitiationId));
	    	req.setAttribute("CostDetailsList", service.CostDetailsList(InitiationId));
	    	req.setAttribute("ScheduleList", service.ProjectInitiationScheduleList(InitiationId));
	    	req.setAttribute("LabList", service.LabList(labcode));
	    	req.setAttribute("LabLogo", LogoUtil.getLabLogoAsBase64String(LabCode));
	    	
			
			 
	    	
	    	}
				
	    	catch(Exception e) {	    		
	    		logger.error(new Date() +" Inside PfmsPrint2.htm "+UserId, e);
	    		e.printStackTrace();
	    		return "static/Error";
	    	}	
		
		return "print/PfmsPrint2";
		
	}
	
	@RequestMapping(value="ExecutiveSummaryDownload.htm")
	public void ExecutiveSummaryDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res)	throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside ExecutiveSummaryDownload.htm "+UserId);		
	    try {
	    
	    	String InitiationId=req.getParameter("IntiationId");
	    	Object[] PfmsInitiationList= service.PfmsInitiationList(InitiationId).get(0);
	    	List<Object[]> costDetailsList = service.CostDetailsList(InitiationId);
	    	String labcode=PfmsInitiationList[17].toString();
   		 	req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(labcode)); 
   			
   		 	req.setAttribute("projecttypeid",PfmsInitiationList[19].toString() );
    		req.setAttribute("LabList", service.LabList(labcode));
    		req.setAttribute("PfmsInitiationList",PfmsInitiationList);
    		req.setAttribute("DetailsList", service.ProjectIntiationDetailsList(InitiationId));
			req.setAttribute("CostDetailsList", costDetailsList);
			req.setAttribute("CostDetailsListSummary", service.CostDetailsListSummary(InitiationId));
    		req.setAttribute("ScheduleList", service.ProjectInitiationScheduleList(InitiationId));
    		req.setAttribute("isprint", "1");
    	
			req.setAttribute("headofaccountsList",service.headofaccountsList( PfmsInitiationList[19].toString()) );
			
			
			
			
    		
    		costDetailsList.stream().filter(e-> Collections.frequency(costDetailsList, e[4])<1).collect(Collectors.toList());    		
    		
    		String filename="ExecutiveSummary";	
	    	String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/print/PfmsPrint.jsp").forward(req, customResponse);
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
    		logger.error(new Date() +" Inside ExecutiveSummaryDownload.htm "+UserId, e);
    		e.printStackTrace();
    	}		
	}
	
	@RequestMapping(value="ProjectProposal.htm", method = {RequestMethod.POST,RequestMethod.GET} )
	public String ProjectProposal(HttpServletRequest req, HttpSession ses, HttpServletResponse res)	throws Exception 
	{
	String UserId = (String) ses.getAttribute("Username");
		
		logger.info(new Date() +"Inside ProjectProposal.htm "+UserId);	
		
		try {
		String InitiationId=req.getParameter("IntiationId");
		Object[] PfmsInitiationList= service.PfmsInitiationList(InitiationId).get(0);
    	String LabCode =PfmsInitiationList[17].toString();
    	String projecttypeid =PfmsInitiationList[19].toString();
    	List<Object[]> CostBreak = service.GetCostBreakList(InitiationId,projecttypeid); 
    	req.setAttribute("costbreak", CostBreak);
    	req.setAttribute("lablogo",  LogoUtil.getLabLogoAsBase64String(LabCode));  
		req.setAttribute("PfmsInitiationList", PfmsInitiationList);
		req.setAttribute("DetailsList", service.ProjectIntiationDetailsList(InitiationId));
		req.setAttribute("CostDetailsList", service.CostDetailsList(InitiationId));
		req.setAttribute("ScheduleList", service.ProjectInitiationScheduleList(InitiationId));
		req.setAttribute("LabList", service.LabList(LabCode));
		req.setAttribute("RequirementList", service.RequirementList(InitiationId));
		req.setAttribute("InitiationId", InitiationId);
		}
		catch(Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside ProjectProposal.htm "+UserId, e);
		}
	
		return "print/ProjectProposal";
	}
	@RequestMapping(value="ProposalPresentationDownload.htm")
	public void PresentaionDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res)	throws Exception 
	{
	String UserId = (String) ses.getAttribute("Username");
		
		logger.info(new Date() +"Inside PresentaionDownload.htm "+UserId);		
	    try {
	    	String InitiationId=req.getParameter("IntiationId");
			Object[] PfmsInitiationList= service.PfmsInitiationList(InitiationId).get(0);
	    	String LabCode =PfmsInitiationList[17].toString();
	    	String projecttypeid =PfmsInitiationList[19].toString();
	    	List<Object[]> CostBreak = service.GetCostBreakList(InitiationId,projecttypeid); 
	    	req.setAttribute("costbreak", CostBreak);
	    	req.setAttribute("lablogo",  LogoUtil.getLabLogoAsBase64String(LabCode));  
			req.setAttribute("PfmsInitiationList", PfmsInitiationList);
			req.setAttribute("DetailsList", service.ProjectIntiationDetailsList(InitiationId));
			req.setAttribute("CostDetailsList", service.CostDetailsList(InitiationId));
			req.setAttribute("ScheduleList", service.ProjectInitiationScheduleList(InitiationId));
			req.setAttribute("LabList", service.LabList(LabCode));
//			req.setAttribute("RequirementList", service.RequirementList(InitiationId));
			req.setAttribute("InitiationId", InitiationId);
			
			String filename="ProjectProposalPresentation";
		  	String path=req.getServletContext().getRealPath("/view/temp");
		  	req.setAttribute("path",path);
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/print/PresentaionDownload.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			ConverterProperties converterProperties = new ConverterProperties();
	    	FontProvider dfp = new DefaultFontProvider(true, true, true);
	    	converterProperties.setFontProvider(dfp);

			HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"),converterProperties);
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
	    	logger.error(new Date() +" Inside PresentaionDownload.htm "+UserId, e);
    		e.printStackTrace();
			
	    }
	}
	
	
	@RequestMapping(value="ProjectProposalDownload.htm")
	public void ProjectProposalDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res)	throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		
		logger.info(new Date() +"Inside ProjectProposalDownload.htm "+UserId);		
	    try {
	    
	    	String InitiationId=req.getParameter("IntiationId");
	    	Object[] PfmsInitiationList= service.PfmsInitiationList(InitiationId).get(0);
	    	String LabCode =PfmsInitiationList[17].toString();
	    	String projecttypeid =PfmsInitiationList[19].toString();
	    	List<Object[]> CostBreak = service.GetCostBreakList(InitiationId,projecttypeid); 
	    	req.setAttribute("costbreak", CostBreak);
	    	req.setAttribute("lablogo",  LogoUtil.getLabLogoAsBase64String(LabCode));  
    		req.setAttribute("PfmsInitiationList", PfmsInitiationList);
    		req.setAttribute("DetailsList", service.ProjectIntiationDetailsList(InitiationId));
    		req.setAttribute("CostDetailsList", service.CostDetailsList(InitiationId));
    		req.setAttribute("ScheduleList", service.ProjectInitiationScheduleList(InitiationId));
    		req.setAttribute("LabList", service.LabList(LabCode));
    		//req.setAttribute("RequirementList", service.RequirementList(InitiationId));
    
    		String filename="ProjectProposal";	
	    	String path=req.getServletContext().getRealPath("/view/temp");
			req.setAttribute("path",path);
			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/print/PfmsPrint2.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			ConverterProperties converterProperties = new ConverterProperties();
	    	FontProvider dfp = new DefaultFontProvider(true, true, true);
	    	converterProperties.setFontProvider(dfp);

			HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"),converterProperties);
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

	        
//    		String filename="ProjectProposal";		
//	    	
//	    	String path=req.getServletContext().getRealPath("/view/temp");
//	    	req.setAttribute("path",path); 
//	    	CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
//	    	req.getRequestDispatcher("/view/print/PfmsPrint2.jsp").forward(req, customResponse);
//	    	String html = customResponse.getOutput();
//	    	byte[] data = html.getBytes();
//	    	InputStream fis1=new ByteArrayInputStream(data);
//	    	PdfDocument pdfDoc = new PdfDocument(new PdfWriter(path+"/"+filename+".pdf"));	
//	    	
//	    	Document document = new Document(pdfDoc, PageSize.A4);
//	    	//document.setMargins(50, 100, 150, 50);
//	    	document.setMargins(50, 50, 50, 50);
//	    	ConverterProperties converterProperties = new ConverterProperties();
//	    	FontProvider dfp = new DefaultFontProvider(true, true, true);
//	    	converterProperties.setFontProvider(dfp);
//	        HtmlConverter.convertToPdf(fis1,pdfDoc,converterProperties);
//            ImageData leftLogo = ImageDataFactory.create(env.getProperty("file_upload_path")+"\\logo\\drdo.png");
//            ImageData rightLogo = ImageDataFactory.create(env.getProperty("file_upload_path")+"\\logo\\lab.png");
//	        PdfWriter pdfw=new PdfWriter(path +File.separator+ "mergedb.pdf");
//	        
//	        
//	        PdfDocument pdfDocMain = new PdfDocument(new PdfReader(path+File.separator+filename+".pdf"),new PdfWriter(path+File.separator+filename+"Maintemp.pdf"));
//	        Document docMain = new Document(pdfDocMain,PageSize.A4);
//	        docMain.setMargins(50, 50, 50, 50);
//	        Rectangle pageSizeMain;
//	        PdfCanvas canvasMAin;
//	        int main = pdfDocMain.getNumberOfPages();
//	        for (int i = 1; i <= main; i++) {
//	            PdfPage pageMain = pdfDocMain.getPage(i);
//	            pageSizeMain = pageMain.getPageSize();
//	            canvasMAin = new PdfCanvas(pageMain);
//	            Rectangle rectaMain=new Rectangle(54,pageSizeMain.getHeight()-34,34,33);
//	            canvasMAin.addImage(leftLogo, rectaMain, false);
//	            Rectangle rectaMain2=new Rectangle(pageSizeMain.getWidth()-64,pageSizeMain.getHeight()-34,34,33);
//	            canvasMAin.addImage(rightLogo, rectaMain2, false);
//
//
//	        }
//	        
//	        res.setContentType("application/pdf");
//	        res.setHeader("Content-disposition","inline;filename="+filename+".pdf"); 
//	        
//	        File f=new File(path+"/"+filename+".pdf");
//	        FileInputStream fis = new FileInputStream(f);
//	        DataOutputStream os = new DataOutputStream(res.getOutputStream());
//	        res.setHeader("Content-Length",String.valueOf(f.length()));
//	        byte[] buffer = new byte[1024];
//	        int len = 0;
//	        while ((len = fis.read(buffer)) >= 0) {
//	            os.write(buffer, 0, len);
//	        } 
//	        os.close();
//	        fis.close();
//	        document.close();
//	        docMain.close();
//	        
//	        Path pathOfFile2= Paths.get(path +File.separator+ "mergedb.pdf"); 
//	        Files.delete(pathOfFile2);
//	        pathOfFile2= Paths.get( path+File.separator+filename+"Maintemp.pdf");
//	        Files.delete(pathOfFile2);	
//	        document.close();

	        
	    	
	    }
	    catch(Exception e) {	    		
    		logger.error(new Date() +" Inside ProjectProposalDownload.htm "+UserId, e);
    		e.printStackTrace();
			
	
    	}		
	}
	

	@RequestMapping(value = "ProjectBriefingDownload.htm")
	public void ProjectBriefingDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res)
	        throws Exception {
	    String UserId = (String) ses.getAttribute("Username");
	    String LabCode = (String) ses.getAttribute("labcode");

	    logger.info(new Date() + "Inside ProjectBriefingDownload.htm " + UserId);
	    try {
	        String projectid = req.getParameter("projectid");
	        String committeeid = req.getParameter("committeeid");

	        Committee committee = service.getCommitteeData(committeeid);

	        String projectLabCode = service.ProjectDetails(projectid).get(0)[5].toString();
	        String CommitteeCode = committee.getCommitteeShortName().trim();

	        if (LabCode.equalsIgnoreCase("ADE")) {
	            req.setAttribute("otherMeetingList", service.otherMeetingList(projectid));
	        }
	        List<Object[]> SpecialCommitteesList = service.SpecialCommitteesList(LabCode);
	        Map<String, List<Object[]>> reviewMeetingListMap = new HashMap<String, List<Object[]>>();
	        for (Object[] obj : SpecialCommitteesList) {
	            reviewMeetingListMap.put(obj[1] + "", service.ReviewMeetingList(projectid, obj[1] + ""));
	        }
	        req.setAttribute("reviewMeetingListMap", reviewMeetingListMap);

	        req.setAttribute("text", req.getParameter("text"));
	        req.setAttribute("IsIbasConnected", IsIbasConnected);
	        req.setAttribute("committeeData", committee);
	        req.setAttribute("projectid", projectid);
	        req.setAttribute("committeeid", committeeid);
	        req.setAttribute("ProjectCost", ProjectCost);
	        req.setAttribute("isprint", "0");
	        req.setAttribute("AppFilesPath", ApplicationFilesDrive);
	        req.setAttribute("projectLabCode", projectLabCode);
	        req.setAttribute("labInfo", service.LabDetailes(projectLabCode));
	        req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(projectLabCode));
	        req.setAttribute("thankYouImg", LogoUtil.getThankYouImageAsBase64String());
	        req.setAttribute("filePath", env.getProperty("ApplicationFilesDrive"));
	        req.setAttribute("ApplicationFilesDrive", env.getProperty("ApplicationFilesDrive"));
	        req.setAttribute("committeeMetingsCount",
	                service.ProjectCommitteeMeetingsCount(projectid, "0", "0", "0", "0", CommitteeCode));
	        Object[] nextmeetVenue = (Object[]) service.BriefingMeetingVenue(projectid, committeeid);
	        req.setAttribute("nextMeetVenue", nextmeetVenue);
	        Object[] lastmeetingVenue = service.getLastcreatedSchedule(projectid, committeeid);
	        req.setAttribute("lastmeetingVenue", lastmeetingVenue);
	        if (nextmeetVenue != null && nextmeetVenue[0] != null) {
	            req.setAttribute("recdecDetails", service.GetRecDecDetails(nextmeetVenue[0].toString()));
	        }
	        if (lastmeetingVenue != null && lastmeetingVenue[0] != null && LabCode.equalsIgnoreCase("PGAD")) {
	            List<Object[]> list = service.getMilestoneBriefingList(lastmeetingVenue[0].toString());
	            Map<String, List<Object[]>> milestoneBriefingMap = list != null
	                    ? list.stream().collect(Collectors.groupingBy(obj -> obj[1] != null ? obj[1].toString() : "UNKNOWN",
	                            LinkedHashMap::new, Collectors.toList()))
	                    : new LinkedHashMap<String, List<Object[]>>();
	            req.setAttribute("milestoneBriefingMap", milestoneBriefingMap);
	            req.setAttribute("recdecDetails", service.GetRecDecDetails(lastmeetingVenue[0].toString()));
	            req.setAttribute("briefingFinanceDetials", service.getBriefingFinanceDetails(lastmeetingVenue[0].toString()));
	        }
	        req.setAttribute("RiskTypes", service.RiskTypes());

	        Object[] mileStoneLevelId = service.MileStoneLevelId(projectid, committeeid);
	        req.setAttribute("levelid", mileStoneLevelId != null ? mileStoneLevelId[0].toString() : "2");

	        // Project Data
	        processProjectData(req, projectid, committeeid, uri, projectLabCode, UserId, IsIbasConnected,ses);

	        List<Object[]> projectdatadetails = (List<Object[]>) req.getAttribute("projectdatadetails");
	        List<List<Object[]>> ebandpmrccount = (List<List<Object[]>>) req.getAttribute("ebandpmrccount");
	        List<Object[]> TechWorkDataList = (List<Object[]>) req.getAttribute("TechWorkDataList");

	        // Milestone Data for Committee
	        milestoneLevelDataMap(req, reviewMeetingListMap, projectid, committee.getCommitteeShortName().trim());

	        String filename = "BriefingPaper";

	        String path = req.getServletContext().getRealPath("/view/temp");
	        req.setAttribute("path", path);
	        CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);

	        req.getRequestDispatcher("/view/print/BriefingPaperNew1.jsp").forward(req, customResponse);

	        String html = customResponse.getOutput();
	        byte[] data = html.getBytes();
	        InputStream fis1 = new ByteArrayInputStream(data);
	        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(path + "/" + filename + ".pdf"));
	        Document document = new Document(pdfDoc, PageSize.A4);
	        document.setMargins(50, 50, 50, 50);
	        ConverterProperties converterProperties = new ConverterProperties();
	        FontProvider dfp = new DefaultFontProvider(true, true, true);
	        converterProperties.setFontProvider(dfp);
	        HtmlConverter.convertToPdf(fis1, pdfDoc, converterProperties);
	        Path leftLogoPath = Paths.get(env.getProperty("ApplicationFilesDrive"), "images", "lablogos", "drdo.png");
	        req.setAttribute("thankYouImg", LogoUtil.getThankYouImageAsBase64String());
	        Path rightLogoPath = Paths.get(env.getProperty("ApplicationFilesDrive"), "images", "lablogos",
	                projectLabCode.toLowerCase() + ".png");
	        byte[] imageBytes = Files.readAllBytes(leftLogoPath);
	        byte[] imageBytes1 = Files.readAllBytes(rightLogoPath);
	        ImageData leftLogo = ImageDataFactory.create(imageBytes);
	        ImageData rightLogo = ImageDataFactory.create(imageBytes1);
	        PdfWriter pdfw = new PdfWriter(path + File.separator + "mergedb.pdf");
	        PdfDocument pdfDocs = new PdfDocument(pdfw);
	        pdfw.setCompressionLevel(CompressionConstants.BEST_COMPRESSION);
	        PdfDocument pdfDocMain = new PdfDocument(new PdfReader(path + File.separator + filename + ".pdf"),
	                new PdfWriter(path + File.separator + filename + "Maintemp.pdf"));
	        Document docMain = new Document(pdfDocMain, PageSize.A4);
	        docMain.setMargins(50, 50, 50, 50);

	        Rectangle pageSizeMain;

	        int main = pdfDocMain.getNumberOfPages();
	        for (int i = 1; i <= main; i++) {
	            PdfPage pageMain = pdfDocMain.getPage(i);
	            pageSizeMain = pageMain.getPageSize();

	            Image leftImage = new Image(leftLogo);
	            leftImage.setFixedPosition(i, 54, pageSizeMain.getHeight() - 34);
	            leftImage.scaleToFit(34, 33);

	            Image rightImage = new Image(rightLogo);
	            rightImage.setFixedPosition(i, pageSizeMain.getWidth() - 64, pageSizeMain.getHeight() - 34);
	            rightImage.scaleToFit(34, 33);

	            docMain.add(leftImage);
	            docMain.add(rightImage);
	        }
	        docMain.close();
	        pdfDocMain.close();

	        Path pathOfFileMain = Paths.get(path + File.separator + filename + ".pdf");
	        Files.delete(pathOfFileMain);

	        // ---- destDoc: the live document we will insert attachments INTO at the right spot ----
	        PdfReader pdf1 = new PdfReader(path + File.separator + filename + "Maintemp.pdf");
	        PdfDocument destDoc = new PdfDocument(pdf1, pdfw);

	        int z = 0;
	        for (Object[] objData : projectdatadetails) {
	        	
	            if (objData != null) {
	                try {
	                    String No2 = null;
	                    if (CommitteeCode.equalsIgnoreCase("PMRB")) {
	                        No2 = "P" + (Long.parseLong(ebandpmrccount.get(0).get(0)[1].toString()) + 1);
	                    } else if (CommitteeCode.equalsIgnoreCase("EB")) {
	                        No2 = "E" + (Long.parseLong(ebandpmrccount.get(0).get(1)[1].toString()) + 1);
	                    }
	                    String ganttFileName = String.format("grantt_%s_%s.pdf", objData[1].toString(), No2);
	                    Path ganttPath = Paths.get(env.getProperty("ApplicationFilesDrive"), projectLabCode, "gantt",
	                            ganttFileName);

	                    if (Files.exists(ganttPath)) {
	                        String anchor = objData[12] + " :-  Grantt Chart ";
	                        insertPdfAtAnchor(destDoc, anchor, ganttPath.toString(), path, filename,
	                                leftLogo, rightLogo, objData[12] + " :-  Grantt Chart ");
	                    }
	                } catch (Exception e) {
	                    logger.error(new Date() + " Inside ProjectBriefingDownload " + UserId, e);
	                    e.printStackTrace();
	                }
	            }
	            if (objData != null && objData[6] != null) {
	                Path pearlPath = Paths.get(env.getProperty("ApplicationFilesDrive"), projectLabCode, "ProjectData",
	                        objData[6].toString());
	                if (FilenameUtils.getExtension(objData[6].toString()).equalsIgnoreCase("pdf") && Files.exists(pearlPath)) {
	                    String anchor = "TRL table with TRL at sanction stage Annexure";
	                    insertPdfAtAnchor(destDoc, anchor, pearlPath.toString(), path, filename,
	                            leftLogo, rightLogo, objData[12] + " :-  TRL table with TRL at sanction stage Annexure ");
	                }
	            }

	            if (objData != null && objData[5] != null) {
	                Path treePath = Paths.get(env.getProperty("ApplicationFilesDrive"), projectLabCode, "ProjectData",
	                        objData[5].toString());
	                if (FilenameUtils.getExtension(objData[5].toString()).equalsIgnoreCase("pdf") && Files.exists(treePath)) {
	                    String anchor = "Overall Product tree/WBS Annexure";
	                    insertPdfAtAnchor(destDoc, anchor, treePath.toString(), path, filename,
	                            leftLogo, rightLogo, objData[12] + " :-  Overall Product tree/WBS Annexure: ");
	                }
	            }

	            if (objData != null && objData[4] != null) {
	                Path specPath = Paths.get(env.getProperty("ApplicationFilesDrive"), projectLabCode, "ProjectData",
	                        objData[4].toString());
	                if (FilenameUtils.getExtension(objData[4].toString()).equalsIgnoreCase("pdf") && Files.exists(specPath)) {
	                    String anchor = "System Specification Annexure";
	                    insertPdfAtAnchor(destDoc, anchor, specPath.toString(), path, filename,
	                            leftLogo, rightLogo, objData[12] + " :-  System Specification Annexure: ");
	                }
	            }

	            if (objData != null && objData[3] != null) {
	                Path sysPath = Paths.get(env.getProperty("ApplicationFilesDrive"), projectLabCode, "ProjectData",
	                        objData[3].toString());
	                if (FilenameUtils.getExtension(objData[3].toString()).equalsIgnoreCase("pdf") && Files.exists(sysPath)) {
	                    String anchor = "System Configuration Annexure";
	                    insertPdfAtAnchor(destDoc, anchor, sysPath.toString(), path, filename,
	                            leftLogo, rightLogo, objData[12] + " :-  System Configuration Annexure: ");
	                }
	            }


	            // Point 13 - Technical Details (zip)
	            try {
	                if (TechWorkDataList.get(z) != null
	                        && FilenameUtils.getExtension(TechWorkDataList.get(z)[8].toString()).equalsIgnoreCase("pdf")) {
	                    Zipper zip = new Zipper();
	                    String tecdata = TechWorkDataList.get(z)[6].toString().replaceAll("[/\\\\]", ",");
	                    String[] fileParts = tecdata.split(",");
	                    String zipName = String
	                            .format(TechWorkDataList.get(z)[7].toString() + TechWorkDataList.get(z)[11].toString()
	                                    + "-" + TechWorkDataList.get(z)[10].toString() + ".zip");
	                    Path techPath = null;
	                    if (fileParts.length == 4) {
	                        techPath = Paths.get(env.getProperty("ApplicationFilesDrive"), fileParts[0], fileParts[1],
	                                fileParts[2], fileParts[3], zipName);
	                    } else {
	                        techPath = Paths.get(env.getProperty("ApplicationFilesDrive"), fileParts[0], fileParts[1],
	                                fileParts[2], fileParts[3], fileParts[4], zipName);
	                    }
	                    zip.unpack(techPath.toString(), path, TechWorkDataList.get(z)[9].toString());

	                    String anchor = "Technical Work Carried";
	                    insertPdfAtAnchor(destDoc, anchor,
	                            path + File.separator + TechWorkDataList.get(z)[8].toString(), path, filename,
	                            leftLogo, rightLogo, objData[12] + " :- Technical Work Carried");

	                    Path unpackedFile = Paths.get(path + File.separator + TechWorkDataList.get(z)[8].toString());
	                    Files.delete(unpackedFile);
	                }
	            } catch (Exception e) {
	                logger.error(new Date() + " Inside ProjectBriefingDownload " + UserId, e);
	                e.printStackTrace();
	            }

	            z++;
	        }

	        destDoc.close();
	        pdf1.close();
	        pdfw.close();

	        res.setContentType("application/pdf");
	        res.setHeader("Content-disposition", "inline;filename=" + filename + ".pdf");
	        File f = new File(path + File.separator + "mergedb.pdf");
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

	        Path pathOfFile2 = Paths.get(path + File.separator + "mergedb.pdf");
	        Files.delete(pathOfFile2);
	        pathOfFile2 = Paths.get(path + File.separator + filename + "Maintemp.pdf");
	        Files.delete(pathOfFile2);
	        document.close();
	    } catch (Exception e) {
	        logger.error(new Date() + " Inside ProjectBriefingDownload.htm " + UserId, e);
	        e.printStackTrace();
	    }
	}

	/**
	 * Finds the page in destDoc containing anchorText, stamps logos + a label onto the
	 * source attachment PDF's pages, and inserts those pages right after the anchor page.
	 * Re-searches destDoc fresh each call, so insertion order across points doesn't matter.
	 */
	private void insertPdfAtAnchor(PdfDocument destDoc, String anchorText, String sourcePdfPath,
	        String tempDirPath, String filename, ImageData leftLogo, ImageData rightLogo, String stampLabel)
	        throws Exception {

	    int targetPage = -1;
	    int totalPages = destDoc.getNumberOfPages();
	    for (int p = 1; p <= totalPages; p++) {
	        String pageText = PdfTextExtractor.getTextFromPage(destDoc.getPage(p));
	        if (pageText.contains(anchorText)) {
	            targetPage = p;
	            break;
	        }
	    }

	    if (targetPage == -1) {
	        // Anchor not found - fall back to appending at the end so nothing is silently dropped
	        logger.error("Anchor not found, appending at end instead: " + anchorText);
	        targetPage = destDoc.getNumberOfPages();
	    }

	    // Stamp logos + label onto a temp copy of the source attachment first
	    String tempStampedPath = tempDirPath + File.separator + filename + "temp.pdf";
	    PdfDocument stampedDoc = new PdfDocument(new PdfReader(sourcePdfPath), new PdfWriter(tempStampedPath));
	    Document stampedDocWrapper = new Document(stampedDoc, PageSize.A4);
	    stampedDocWrapper.setMargins(50, 50, 50, 50);

	    int n = stampedDoc.getNumberOfPages();
	    for (int i = 1; i <= n; i++) {
	        PdfPage page = stampedDoc.getPage(i);
	        Rectangle pageSize = page.getPageSize();
	        PdfCanvas canvas = new PdfCanvas(page);

	        Image leftImage = new Image(leftLogo);
	        leftImage.setFixedPosition(i, 10, pageSize.getHeight() - 50);
	        leftImage.scaleToFit(40, 40);

	        Image rightImage = new Image(rightLogo);
	        rightImage.setFixedPosition(i, pageSize.getWidth() - 50, pageSize.getHeight() - 50);
	        rightImage.scaleToFit(40, 40);

	        stampedDocWrapper.add(leftImage);
	        stampedDocWrapper.add(rightImage);

	        canvas.beginText()
	                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 11)
	                .moveText(pageSize.getWidth() / 2 - 124, pageSize.getHeight() - 25)
	                .showText(stampLabel).endText();
	    }
	    stampedDocWrapper.close();
	    stampedDoc.close();

	    // Now insert the stamped pages into destDoc right after targetPage
	    PdfReader stampedReader = new PdfReader(tempStampedPath);
	    PdfDocument stampedForInsert = new PdfDocument(stampedReader);
	    stampedForInsert.copyPagesTo(1, stampedForInsert.getNumberOfPages(), destDoc, targetPage + 1);
	    stampedForInsert.close();
	    stampedReader.close();

	    Path tempFile = Paths.get(tempStampedPath);
	    Files.delete(tempFile);
	}

	@RequestMapping(value = "ProjectBriefingDownload123.htm")
	public void ProjectBriefingDownload123(HttpServletRequest req, HttpSession ses, HttpServletResponse res)
			throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside ProjectBriefingDownload.htm "+UserId);		
	    try {
	    	String projectid=req.getParameter("projectid");
	    	String committeeid= req.getParameter("committeeid");
	    	
	    	Committee committee = service.getCommitteeData(committeeid);
	    	
	    	String projectLabCode = service.ProjectDetails(projectid).get(0)[5].toString();
	    	String CommitteeCode = committee.getCommitteeShortName().trim();
	    	
	    	if(LabCode.equalsIgnoreCase("ADE")) {
	    		req.setAttribute("otherMeetingList", service.otherMeetingList(projectid));
	    	}
	    	List<Object[]> SpecialCommitteesList =  service.SpecialCommitteesList(LabCode);
	    	Map<String, List<Object[]>> reviewMeetingListMap = new HashMap<String, List<Object[]>>();
			for(Object[] obj : SpecialCommitteesList) {
				reviewMeetingListMap.put(obj[1]+"", service.ReviewMeetingList(projectid, obj[1]+""));
			}
	    	req.setAttribute("reviewMeetingListMap",reviewMeetingListMap);

	    	req.setAttribute("text", req.getParameter("text"));
	    	req.setAttribute("IsIbasConnected", IsIbasConnected);
	    	req.setAttribute("committeeData", committee);
    		req.setAttribute("projectid",projectid);
    		req.setAttribute("committeeid",committeeid);
    		req.setAttribute("ProjectCost",ProjectCost);
	    	req.setAttribute("isprint", "0");
	    	req.setAttribute("AppFilesPath",ApplicationFilesDrive);
	    	req.setAttribute("projectLabCode",projectLabCode);
	    	req.setAttribute("labInfo", service.LabDetailes(projectLabCode));
	    	req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(projectLabCode));  
	    	req.setAttribute("thankYouImg", LogoUtil.getThankYouImageAsBase64String());
            req.setAttribute("filePath", env.getProperty("ApplicationFilesDrive"));
    		req.setAttribute("ApplicationFilesDrive",env.getProperty("ApplicationFilesDrive"));
    		req.setAttribute("committeeMetingsCount", service.ProjectCommitteeMeetingsCount(projectid, "0", "0", "0", "0", CommitteeCode) );
    		Object[] nextmeetVenue = (Object[])service.BriefingMeetingVenue(projectid, committeeid);
	    	req.setAttribute("nextMeetVenue", nextmeetVenue);
	    	Object[] lastmeetingVenue = service.getLastcreatedSchedule(projectid, committeeid);
			req.setAttribute("lastmeetingVenue", lastmeetingVenue);
			if (nextmeetVenue != null && nextmeetVenue[0] != null) {
				req.setAttribute("recdecDetails", service.GetRecDecDetails(nextmeetVenue[0].toString()));
			}
			if(lastmeetingVenue!=null && lastmeetingVenue[0]!=null && LabCode.equalsIgnoreCase("PGAD")) {
				List<Object[]> list = service.getMilestoneBriefingList(lastmeetingVenue[0].toString());
				Map<String,List<Object[]>> milestoneBriefingMap = list!=null ? list.stream().collect(Collectors.groupingBy(obj -> obj[1]!=null?obj[1].toString():"UNKNOWN",LinkedHashMap::new,Collectors.toList())) : new LinkedHashMap<String, List<Object[]>>();
				req.setAttribute("milestoneBriefingMap", milestoneBriefingMap);
				req.setAttribute("recdecDetails", service.GetRecDecDetails(lastmeetingVenue[0].toString()));
			}
	    	req.setAttribute("RiskTypes", service.RiskTypes());
    		
    		Object[] mileStoneLevelId = service.MileStoneLevelId(projectid,committeeid);
			req.setAttribute("levelid", mileStoneLevelId!=null?mileStoneLevelId[0].toString():"2");
			
			// Project Data
			processProjectData(req, projectid, committeeid, uri, projectLabCode, UserId, IsIbasConnected,ses);


			List<Object[]> projectdatadetails = (List<Object[]>) req.getAttribute("projectdatadetails");
			List<List<Object[]>> ebandpmrccount = (List<List<Object[]>>) req.getAttribute("ebandpmrccount");
			List<Object[]> TechWorkDataList = (List<Object[]>) req.getAttribute("TechWorkDataList");

			// Milestone Data for Committee
			milestoneLevelDataMap(req, reviewMeetingListMap, projectid, committee.getCommitteeShortName().trim());
			
	    	String filename="BriefingPaper";		
	    	
	    	String path=req.getServletContext().getRealPath("/view/temp");
	    	req.setAttribute("path",path); 
	    	CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
	    	req.getRequestDispatcher("/view/print/BriefingPaperNew1.jsp").forward(req, customResponse);
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
	        Path leftLogoPath = Paths.get(env.getProperty("ApplicationFilesDrive"),"images","lablogos","drdo.png");
	        req.setAttribute("thankYouImg", LogoUtil.getThankYouImageAsBase64String());
	        Path rightLogoPath = Paths.get(env.getProperty("ApplicationFilesDrive"),"images","lablogos",projectLabCode.toLowerCase()+".png");
          //  ImageData leftLogo = ImageDataFactory.create(env.getProperty("ApplicationFilesDrive")+"\\images\\lablogos\\drdo.png");
           // ImageData rightLogo = ImageDataFactory.create(env.getProperty("ApplicationFilesDrive")+"\\images\\lablogos\\"+projectLabCode.toLowerCase()+".png");
	        byte[] imageBytes = Files.readAllBytes(leftLogoPath);
	        byte[] imageBytes1 = Files.readAllBytes(rightLogoPath);
	        ImageData leftLogo = ImageDataFactory.create(imageBytes);
            ImageData rightLogo = ImageDataFactory.create(imageBytes1);
	        PdfWriter pdfw=new PdfWriter(path +File.separator+ "mergedb.pdf");
	        //PdfWriter pdfWriter = new PdfWriter(path + File.separator + "mergedb.pdf");
	        PdfDocument pdfDocs = new PdfDocument(pdfw);
	        pdfw.setCompressionLevel(CompressionConstants.BEST_COMPRESSION);
	        pdfw.setSmartMode(true); // DLRL Changes
	        PdfDocument pdfDocMain = new PdfDocument(new PdfReader(path+File.separator+filename+".pdf"),new PdfWriter(path+File.separator+filename+"Maintemp.pdf"));
	        Document docMain = new Document(pdfDocMain,PageSize.A4);
	        docMain.setMargins(50, 50, 50, 50);
	        
	        Rectangle pageSizeMain;

	        int main = pdfDocMain.getNumberOfPages();
	        for (int i = 1; i <= main; i++) 
	        {
	            PdfPage pageMain = pdfDocMain.getPage(i);
	            pageSizeMain = pageMain.getPageSize();
	            
	            // Left Logo
        	    Image leftImage = new Image(leftLogo);
        	    leftImage.setFixedPosition(i, 54, pageSizeMain.getHeight() - 34);
        	    leftImage.scaleToFit(34, 33);
        	    
        	    // Right Logo
        	    Image rightImage = new Image(rightLogo);
        	    rightImage.setFixedPosition(i, pageSizeMain.getWidth() - 64, pageSizeMain.getHeight() - 34);
        	    rightImage.scaleToFit(34, 33);
        	    
        	    // Add images to the document
        	    docMain.add(leftImage);
        	    docMain.add(rightImage);
	        }
	        docMain.close();
	        pdfDocMain.close();
	        
	        Path pathOfFileMain= Paths.get( path+File.separator+filename+".pdf");
	        Files.delete(pathOfFileMain);	
	        
	        PdfReader pdf1=new PdfReader(path+File.separator+filename+"Maintemp.pdf");
	        PdfDocument pdfDocument = new PdfDocument(pdf1, pdfw);
	        PdfMerger merger = new PdfMerger(pdfDocument);
	        int z=0;
	        for(Object[] objData:projectdatadetails)
			{	
	        	if(objData!=null) {
	        	
	        	try {
	        		String No2=null;
	        		if(CommitteeCode.equalsIgnoreCase("PMRB")){ 
	        		No2="P"+(Long.parseLong(ebandpmrccount.get(0).get(0)[1].toString())+1);
	        		}else if(CommitteeCode.equalsIgnoreCase("EB")){
	        			No2="E"+(Long.parseLong(ebandpmrccount.get(0).get(1)[1].toString())+1);
	        		}
	        		String fileName = String.format("grantt_%s_%s.pdf", objData[1].toString(), No2);
	        		Path ganttPath = Paths.get(env.getProperty("ApplicationFilesDrive"), projectLabCode,"gantt",fileName);
	        		
		        	if(Files.exists(ganttPath)) {
		        		    PdfReader pdfReader = new PdfReader(ganttPath.toString());
		        		 	PdfDocument pdfDocument2 = new PdfDocument(pdfReader,new PdfWriter(path+File.separator+filename+"temp.pdf"));
					        Document document5 = new Document(pdfDocument2,PageSize.A4);
					        document5.setMargins(50, 50, 50, 50);
					        
					        Rectangle pageSize;
					        PdfCanvas canvas;
					        int n = pdfDocument2.getNumberOfPages();
					        for (int i = 1; i <= n; i++) {
					            PdfPage page = pdfDocument2.getPage(i);
					            pageSize = page.getPageSize();
					            canvas = new PdfCanvas(page);
					            
					            // Left Logo
				        	    Image leftImage = new Image(leftLogo);
				        	    leftImage.setFixedPosition(i, 10, pageSize.getHeight() - 50);
				        	    leftImage.scaleToFit(40, 40);
				        	    
				        	    // Right Logo
				        	    Image rightImage = new Image(rightLogo);
				        	    rightImage.setFixedPosition(i, pageSize.getWidth() - 50, pageSize.getHeight() - 50);
				        	    rightImage.scaleToFit(40, 40);
				        	    
				        	    // Add images to the document
				        	    document5.add(leftImage);
				        	    document5.add(rightImage);
				        	    
					            canvas.beginText().setFontAndSize(
					                    PdfFontFactory.createFont(StandardFonts.HELVETICA), 11)
					                    .moveText(pageSize.getWidth() / 2 - 124, pageSize.getHeight() - 25)
					                    .showText(objData[12]+" :-  Grantt Chart ")
					                    .endText();
					            

					        }
					        document5.close();
					        pdfDocument2.close();
				        	PdfReader pdf2=new PdfReader(path+"/"+filename+"temp.pdf");
				        	 PdfDocument pdfDocument3 = new PdfDocument(pdf2);
					        merger.merge(pdfDocument3, 1, pdfDocument3.getNumberOfPages());
					        
					        pdfDocument3.close();
					        pdf2.close();
					        Path pathOfFile= Paths.get( path+File.separator+filename+"temp.pdf");
					        Files.delete(pathOfFile);	
					        pathOfFile= Paths.get( path+File.separator+TechWorkDataList.get(z)[8].toString());
					        
						    }
		        	 
		        	}
	        	
	        		catch (Exception e) {
	        			logger.error(new Date() +" Inside ProjectBriefingDownload "+UserId, e);
						e.printStackTrace();
					}
	        	
	        	}
	        	
	        	
	        	if(objData!=null && objData[3]!=null) {
	        	   Path sysPath = Paths.get(env.getProperty("ApplicationFilesDrive"), projectLabCode,"ProjectData",objData[3].toString());
	        	if(FilenameUtils.getExtension(objData[3].toString()).equalsIgnoreCase("pdf") && Files.exists(sysPath)) {
	            PdfReader pdfReader = new PdfReader(sysPath.toString());
		        PdfDocument pdfDocument2 = new PdfDocument(pdfReader,new PdfWriter(path+File.separator+filename+"temp.pdf"));
		        Document document5 = new Document(pdfDocument2,PageSize.A4);
		        document5.setMargins(50, 50, 50, 50);
		        Rectangle pageSize;
		        PdfCanvas canvas;
		        int n = pdfDocument2.getNumberOfPages();
		        for (int i = 1; i <= n; i++) {
		            PdfPage page = pdfDocument2.getPage(i);
		            pageSize = page.getPageSize();
		            canvas = new PdfCanvas(page);
		            
		            // Left Logo
	        	    Image leftImage = new Image(leftLogo);
	        	    leftImage.setFixedPosition(i, 10, pageSize.getHeight() - 50);
	        	    leftImage.scaleToFit(40, 40);
	        	    
	        	    // Right Logo
	        	    Image rightImage = new Image(rightLogo);
	        	    rightImage.setFixedPosition(i, pageSize.getWidth() - 50, pageSize.getHeight() - 50);
	        	    rightImage.scaleToFit(40, 40);
	        	    
	        	    // Add images to the document
	        	    document5.add(leftImage);
	        	    document5.add(rightImage);
	        	    
		            canvas.beginText().setFontAndSize(
		                    PdfFontFactory.createFont(StandardFonts.HELVETICA), 11)
		                    .moveText(pageSize.getWidth() / 2 - 124, pageSize.getHeight() - 25)
		                    .showText(objData[12]+" :-  System Configuration Annexure: ")
		                    .endText();

		        }
		        document5.close();
		        pdfDocument2.close();
	        	PdfReader pdf2=new PdfReader(path+"/"+filename+"temp.pdf");
	        	 PdfDocument pdfDocument3 = new PdfDocument(pdf2);
		        merger.merge(pdfDocument3, 1, pdfDocument3.getNumberOfPages());
		        
		        pdfDocument3.close();
		        pdf2.close();
		        Path pathOfFile= Paths.get( path+File.separator+filename+"temp.pdf");
		        Files.delete(pathOfFile);	
			    }
	        	}
	        	if(objData!=null && objData[4]!=null) {
	        		Path specPath = Paths.get(env.getProperty("ApplicationFilesDrive"), projectLabCode,"ProjectData",objData[4].toString());
			    if(FilenameUtils.getExtension(objData[4].toString()).equalsIgnoreCase("pdf") && Files.exists(specPath)) {
			    	PdfReader pdfReader = new PdfReader(specPath.toString());
			    	PdfDocument pdfDocument2 = new PdfDocument(pdfReader,new PdfWriter(path+File.separator+filename+"temp.pdf"));
			        Document document5 = new Document(pdfDocument2,PageSize.A4);
			        document5.setMargins(50, 50, 50, 50);
			        Rectangle pageSize;
			        PdfCanvas canvas;
			        int n = pdfDocument2.getNumberOfPages();
			        for (int i = 1; i <= n; i++) {
			            PdfPage page = pdfDocument2.getPage(i);
			            pageSize = page.getPageSize();
			            canvas = new PdfCanvas(page);

			            // Left Logo
		        	    Image leftImage = new Image(leftLogo);
		        	    leftImage.setFixedPosition(i, 10, pageSize.getHeight() - 50);
		        	    leftImage.scaleToFit(40, 40);
		        	    
		        	    // Right Logo
		        	    Image rightImage = new Image(rightLogo);
		        	    rightImage.setFixedPosition(i, pageSize.getWidth() - 50, pageSize.getHeight() - 50);
		        	    rightImage.scaleToFit(40, 40);
		        	    
		        	    // Add images to the document
		        	    document5.add(leftImage);
		        	    document5.add(rightImage);
			            
			            canvas.beginText().setFontAndSize(
			                    PdfFontFactory.createFont(StandardFonts.HELVETICA),11)
			                    .moveText(pageSize.getWidth() / 2 - 130, pageSize.getHeight() - 25)
			                    .showText(objData[12]+" :-  System Specification Annexure: ")
			                    .endText();

			        }
			        document5.close();
			        pdfDocument2.close();
		        	PdfReader pdf2=new PdfReader(path+"/"+filename+"temp.pdf");
		        	 PdfDocument pdfDocument3 = new PdfDocument(pdf2);
			        merger.merge(pdfDocument3, 1, pdfDocument3.getNumberOfPages());
			        
			        pdfDocument3.close();
			        pdf2.close();
			        Path pathOfFile= Paths.get( path+File.separator+filename+"temp.pdf");
			        Files.delete(pathOfFile);	
			    }
	        	}
	        	if(objData!=null && objData[5]!=null) {
	        		Path treePath = Paths.get(env.getProperty("ApplicationFilesDrive"), projectLabCode,"ProjectData",objData[5].toString());
			    if(FilenameUtils.getExtension(objData[5].toString()).equalsIgnoreCase("pdf") && Files.exists(treePath)) {
			    	PdfReader pdfReader = new PdfReader(treePath.toString());
			    	PdfDocument pdfDocument2 = new PdfDocument(pdfReader,new PdfWriter(path+File.separator+filename+"temp.pdf"));
			        Document document5 = new Document(pdfDocument2,PageSize.A4);
			        document5.setMargins(50, 50, 50, 50);
			        Rectangle pageSize;
			        PdfCanvas canvas;
			        int n = pdfDocument2.getNumberOfPages();
			        for (int i = 1; i <= n; i++) {
			            PdfPage page = pdfDocument2.getPage(i);
			            pageSize = page.getPageSize();
			            canvas = new PdfCanvas(page);
			            
			            // Left Logo
		        	    Image leftImage = new Image(leftLogo);
		        	    leftImage.setFixedPosition(i, 10, pageSize.getHeight() - 50);
		        	    leftImage.scaleToFit(40, 40);
		        	    
		        	    // Right Logo
		        	    Image rightImage = new Image(rightLogo);
		        	    rightImage.setFixedPosition(i, pageSize.getWidth() - 50, pageSize.getHeight() - 50);
		        	    rightImage.scaleToFit(40, 40);
		        	    
		        	    // Add images to the document
		        	    document5.add(leftImage);
		        	    document5.add(rightImage);
		        	    
			            canvas.beginText().setFontAndSize(
			                    PdfFontFactory.createFont(StandardFonts.HELVETICA), 11)
			                    .moveText(pageSize.getWidth() / 2 - 140, pageSize.getHeight() - 25)
			                    .showText(objData[12]+" :-  Overall Product tree/WBS Annexure: ")
			                    .endText();

			        }
			        document5.close();
			        pdfDocument2.close();
		        	PdfReader pdf2=new PdfReader(path+"/"+filename+"temp.pdf");
		        	 PdfDocument pdfDocument3 = new PdfDocument(pdf2);
			        merger.merge(pdfDocument3, 1, pdfDocument3.getNumberOfPages());
			        
			        pdfDocument3.close();
			        pdf2.close();
			        Path pathOfFile= Paths.get( path+File.separator+filename+"temp.pdf");
			        Files.delete(pathOfFile);	
				    }
	        	}
	        	if(objData!=null&&objData[6]!=null) {
	        		Path pearlPath = Paths.get(env.getProperty("ApplicationFilesDrive"), projectLabCode,"ProjectData",objData[6].toString());
			    if(FilenameUtils.getExtension(objData[6].toString()).equalsIgnoreCase("pdf") && Files.exists(pearlPath)) {
			    	PdfReader pdfReader = new PdfReader(pearlPath.toString());
			    	PdfDocument pdfDocument2 = new PdfDocument(pdfReader,new PdfWriter(path+File.separator+filename+"temp.pdf"));
			        Document document5 = new Document(pdfDocument2,PageSize.A4);
			        document5.setMargins(50, 50, 50, 50);
			        Rectangle pageSize;
			        PdfCanvas canvas;
			        int n = pdfDocument2.getNumberOfPages();
			        for (int i = 1; i <= n; i++) {
			            PdfPage page = pdfDocument2.getPage(i);
			            pageSize = page.getPageSize();
			            canvas = new PdfCanvas(page);

			            // Left Logo
		        	    Image leftImage = new Image(leftLogo);
		        	    leftImage.setFixedPosition(i, 10, pageSize.getHeight() - 50);
		        	    leftImage.scaleToFit(40, 40);
		        	    
		        	    // Right Logo
		        	    Image rightImage = new Image(rightLogo);
		        	    rightImage.setFixedPosition(i, pageSize.getWidth() - 50, pageSize.getHeight() - 50);
		        	    rightImage.scaleToFit(40, 40);
		        	    
		        	    // Add images to the document
		        	    document5.add(leftImage);
		        	    document5.add(rightImage);
		        	    
			            canvas.beginText().setFontAndSize(
			                    PdfFontFactory.createFont(StandardFonts.HELVETICA), 11)
			                    .moveText(pageSize.getWidth() / 2 - 145, pageSize.getHeight() - 25)
			                    .showText(objData[12]+" :-  TRL table with TRL at sanction stage Annexure ")
			                    .endText();
			            

			        }
			        document5.close();
			        pdfDocument2.close();
		        	PdfReader pdf2=new PdfReader(path+"/"+filename+"temp.pdf");
		        	PdfDocument pdfDocument3 = new PdfDocument(pdf2);
			        merger.merge(pdfDocument3, 1, pdfDocument3.getNumberOfPages());
			        
			        pdfDocument3.close();
			        pdf2.close();
			        Path pathOfFile= Paths.get( path+File.separator+filename+"temp.pdf");
			        Files.delete(pathOfFile);	
			        
			        
				    }
	        	}
	        	
	        	
	        	//Point 13
	        	try {
	        	
	        	 if(TechWorkDataList.get(z) !=null &&FilenameUtils.getExtension(TechWorkDataList.get(z)[8].toString()).equalsIgnoreCase("pdf")) {
	        		 Zipper zip=new Zipper();
	        		 String tecdata = TechWorkDataList.get(z)[6].toString().replaceAll("[/\\\\]", ",");
	        		 String[] fileParts = tecdata.split(",");
	        		 String zipName = String.format(TechWorkDataList.get(z)[7].toString()+TechWorkDataList.get(z)[11].toString()+"-"+TechWorkDataList.get(z)[10].toString()+".zip");
	        		 Path techPath = null;
	        		 if(fileParts.length == 4){
	        		 	techPath = Paths.get(env.getProperty("ApplicationFilesDrive"), fileParts[0],fileParts[1],fileParts[2],fileParts[3],zipName);
	        		 }else{
	        			techPath = Paths.get(env.getProperty("ApplicationFilesDrive"), fileParts[0],fileParts[1],fileParts[2],fileParts[3],fileParts[4],zipName);
	        		 }
	                 zip.unpack(techPath.toString(),path,TechWorkDataList.get(z)[9].toString());
				    	PdfDocument pdfDocument2 = new PdfDocument(new PdfReader(path+File.separator+TechWorkDataList.get(z)[8].toString()),new PdfWriter(path+File.separator+filename+"temp.pdf"));
				        Document document5 = new Document(pdfDocument2,PageSize.A4);
				        document5.setMargins(50, 50, 50, 50);
				        Rectangle pageSize;
				        PdfCanvas canvas;
				        int n = pdfDocument2.getNumberOfPages();
				        for (int i = 1; i <= n; i++) {
				            PdfPage page = pdfDocument2.getPage(i);
				            pageSize = page.getPageSize();
				            canvas = new PdfCanvas(page);
				            
				            // Left Logo
			        	    Image leftImage = new Image(leftLogo);
			        	    leftImage.setFixedPosition(i, 10, pageSize.getHeight() - 50);
			        	    leftImage.scaleToFit(40, 40);
			        	    
			        	    // Right Logo
			        	    Image rightImage = new Image(rightLogo);
			        	    rightImage.setFixedPosition(i, pageSize.getWidth() - 50, pageSize.getHeight() - 50);
			        	    rightImage.scaleToFit(40, 40);
			        	    
			        	    // Add images to the document
			        	    document5.add(leftImage);
			        	    document5.add(rightImage);
				            canvas.beginText().setFontAndSize(
				                    PdfFontFactory.createFont(StandardFonts.HELVETICA), 11)
				                    .moveText(pageSize.getWidth() / 2 - 80, pageSize.getHeight() - 25)
				                    .showText(objData[12]+" :-  Technical Details ")
				                    .endText();
				            

				        }
				        document5.close();
				        pdfDocument2.close();
			        	PdfReader pdf2=new PdfReader(path+"/"+filename+"temp.pdf");
			        	PdfDocument pdfDocument3 = new PdfDocument(pdf2);
				        merger.merge(pdfDocument3, 1, pdfDocument3.getNumberOfPages());
				        
				        pdfDocument3.close();
				        pdf2.close();
				        Path pathOfFile= Paths.get( path+File.separator+filename+"temp.pdf");
				        Files.delete(pathOfFile);	
				        pathOfFile= Paths.get( path+File.separator+TechWorkDataList.get(z)[8].toString());
				        Files.delete(pathOfFile);	
				    }
	        	}catch (Exception e) {
	        		logger.error(new Date() +" Inside ProjectBriefingDownload "+UserId, e);
					e.printStackTrace();
				}
	        	
	        	z++;
			}


	        pdfDocument.close();
	        merger.close();

	        pdf1.close();	       
	        pdfw.close();
	        res.setContentType("application/pdf");
	        res.setHeader("Content-disposition","inline;filename="+filename+".pdf"); 
	        File f=new File(path +File.separator+ "mergedb.pdf");
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
	         
	            
	        Path pathOfFile2= Paths.get(path +File.separator+ "mergedb.pdf"); 
	        Files.delete(pathOfFile2);
	        pathOfFile2= Paths.get( path+File.separator+filename+"Maintemp.pdf");
	        Files.delete(pathOfFile2);
	        document.close();
	    }
	    catch(Exception e) {	    		
    		logger.error(new Date() +" Inside ProjectBriefingDownload.htm "+UserId, e);
    		e.printStackTrace();
			/* return "static/Error"; */
	
    	}		
	}
	
	
	public File BriefingPaperGeneratePDF(HttpServletRequest req, HttpServletResponse res, HttpSession ses,RedirectAttributes redir)
	{

		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");	  
    	String token=(String)ses.getAttribute("token");
    	String auToken = "Bearer "+token;
		logger.info(new Date() +"Inside ProjectBriefingDownload.htm "+UserId);		
	    try {
	    	String projectid=req.getParameter("projectid");
	    	String committeeid= req.getParameter("committeeid");
	    	String tempid=committeeid;
	  

	    	String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
	    	String Logintype= (String)ses.getAttribute("LoginType");
	    	Committee committee = service.getCommitteeData(committeeid);
	    	
	    	String projectLabCode = service.ProjectDetails(projectid).get(0)[5].toString();
	    	String CommitteeCode = committee.getCommitteeShortName().trim();
	    	
	    	List<Object[]> projectattributes = new ArrayList<Object[]>();
	    	List<List<Object[]>>  ebandpmrccount = new ArrayList<List<Object[]>>();
	    	List<List<Object[]>> milestonesubsystems = new ArrayList<List<Object[]>>();
	    	List<List<Object[]>> milestones  = new ArrayList<List<Object[]>>();
	    	List<List<Object[]>> lastpmrcactions = new ArrayList<List<Object[]>>();
	    	List<List<Object[]>> lastpmrcminsactlist = new ArrayList<List<Object[]>>();
	    	List<Object[]> ProjectDetails = new ArrayList<Object[]>();
	    	List<List<Object[]>> ganttchartlist = new ArrayList<List<Object[]>>();
	    	List<List<Object[]>> oldpmrcissueslist = new ArrayList<List<Object[]>>();
	    	List<List<Object[]>> riskmatirxdata = new ArrayList<List<Object[]>>();
	    	List<Object[]> lastpmrcdecisions = new ArrayList<Object[]>();
	    	List<List<Object[]>> actionplanthreemonths = new ArrayList<List<Object[]>>();
	    	List<List<Object[]>> ReviewMeetingListEB = new ArrayList<List<Object[]>>();
	    	List<List<Object[]>> ReviewMeetingListPMRC = new ArrayList<List<Object[]>>();
	    	List<Object[]> projectdatadetails  = new ArrayList<Object[]>();
	    	List<Object[]> TechWorkDataList =new ArrayList<Object[]>();
    		List<List<Object[]>> milestonesubsystemsnew = new ArrayList<List<Object[]>>();
	    	
	    	List<List<Object[]>> ProjectRevList =new ArrayList<List<Object[]>>();
	    	
	    	
	    	List<List<ProjectFinancialDetails>> financialDetails=new ArrayList<List<ProjectFinancialDetails>>();
	    	List<List<Object[]>> procurementOnDemandlist  = new ArrayList<List<Object[]>>();
    		List<List<Object[]>> procurementOnSanctionlist = new ArrayList<List<Object[]>>();
    		List<List<TechImages>> TechImages =new ArrayList<List<TechImages>>();
	    	List<String> Pmainlist = service.ProjectsubProjectIdList(projectid);
	    	for(String proid : Pmainlist) 
	    	{	    	
	    		Object[] projectattribute = service.ProjectAttributes(proid);
	    		
	    		TechImages.add(service.getTechList(proid));
	    		projectattributes.add(projectattribute);
	    		ebandpmrccount.add(service.EBAndPMRCCount(proid));
	    		milestonesubsystems.add(service.MilestoneSubsystems(proid));
	    		milestones.add(service.Milestones(proid,committeeid));
	    		lastpmrcactions.add(service.LastPMRCActions(proid,committeeid));
	    		lastpmrcminsactlist.add(service.LastPMRCActions1(proid,committeeid));
	    		ProjectDetails.add(service.ProjectDetails(proid).get(0));
	    		ganttchartlist.add(service.GanttChartList(proid));
	    		oldpmrcissueslist.add(service.OldPMRCIssuesList(proid));
	    		riskmatirxdata.add(service.RiskMatirxData(proid));
	    		lastpmrcdecisions.add(service.LastPMRCDecisions(committeeid,proid));
	    		actionplanthreemonths.add(service.ActionPlanSixMonths(proid,committeeid));
	    		projectdatadetails.add(service.ProjectDataDetails(proid));
	    		ReviewMeetingListEB.add(service.ReviewMeetingList(projectid, "EB"));
	    		ReviewMeetingListPMRC.add(service.ReviewMeetingList(projectid, "PMRB"));
	    		TechWorkDataList.add(service.TechWorkData(proid));
		    	milestonesubsystemsnew.add(service.BreifingMilestoneDetails(proid,committeeid));
	    		ProjectRevList.add(service.ProjectRevList(proid));
	     		// added on 30-10-2023
	    		int pmrccount=0;
	     		Map<Integer,String> mappmrc = new HashMap<>();
	    		Map<Integer,String> mapEB = new HashMap<>();
	     		for (Object []obj:ReviewMeetingListPMRC.get(0)) {
	     			mappmrc.put(++pmrccount,obj[3].toString());
	     		}
	    		int ebcount=0;
	    		for (Object []obj:ReviewMeetingListEB.get(0)) {
	    		mapEB.put(++ebcount,obj[3].toString());
	    		}
	    		req.setAttribute("mappmrc", mappmrc);
		    	req.setAttribute("mapEB", mapEB);
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
			 	 AllMilestones.add(Integer.parseInt(obj[0].toString())); // getting the milestones from list
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
		/* ----------------------------------------------------------------------------------------------------------	   */  		
//	    		 final String localUri=uri+"/pfms_serv/financialStatusBriefing?ProjectCode="+projectattribute[0]+"&rupess="+10000000;
//			 		HttpHeaders headers = new HttpHeaders();
//			 		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//			    	headers.set("labcode", LabCode);
			 		
//			 		String jsonResult=null;
					List<ProjectFinancialDetails> projectDetails=null;

					try {
//						HttpEntity<String> entity = new HttpEntity<String>(headers);
//						ResponseEntity<String> response=restTemplate.exchange(localUri, HttpMethod.POST, entity, String.class);
//						jsonResult=response.getBody();
						projectDetails = PFMSserv.financialStatusBriefing(auToken, projectattribute[0] != null ? projectattribute[0].toString() : "", "10000000");
					}catch(Exception e) {
						req.setAttribute("errorMsg", "errorMsg");
						req.setAttribute("financialDetails",List.of());
					}
//					ObjectMapper mapper = new ObjectMapper();
//					mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//					mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
					if(projectDetails!=null) {
						try {
							/*
							 * projectDetails = mapper.readValue(jsonResult,
							 * mapper.getTypeFactory().constructCollectionType(List.class,
							 * ProjectFinancialDetails.class));
							 */
//							projectDetails = mapper.readValue(jsonResult, new TypeReference<List<ProjectFinancialDetails>>(){});
							financialDetails.add(projectDetails);
							req.setAttribute("financialDetails",projectDetails);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
	    	
//					final String localUri2=uri+"/pfms_serv/getTotalDemand";
					List<TotalDemand> totaldemand=null;

//			 		String jsonResult2=null;
					try {
//						HttpEntity<String> entity = new HttpEntity<String>(headers);
//						ResponseEntity<String> response=restTemplate.exchange(localUri2, HttpMethod.POST, entity, String.class);
//						jsonResult2=response.getBody();		
						totaldemand = PFMSserv.getTotalDemand(auToken);
					}catch(Exception e) {
						req.setAttribute("errorMsg", "errorMsg");
						req.setAttribute("TotalProcurementDetails",List.of());
					}
//					ObjectMapper mapper2 = new ObjectMapper();
//					mapper2.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//					mapper2.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
					if(totaldemand!=null) {
						try {
							/*
							 * projectDetails = mapper.readValue(jsonResult,
							 * mapper.getTypeFactory().constructCollectionType(List.class,
							 * ProjectFinancialDetails.class));
							 */
//							totaldemand = mapper2.readValue(jsonResult2, new TypeReference<List<TotalDemand>>(){});
							req.setAttribute("TotalProcurementDetails",totaldemand);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
	 
	    	List<Object[]> procurementStatusList=(List<Object[]>)service.ProcurementStatusList(proid);
	    	List<Object[]> procurementOnDemand=null;
	    	List<Object[]> procurementOnSanction=null;
	    	

	    	if(procurementStatusList!=null)
	    	{
		    	Map<Object, List<Object[]>> map = procurementStatusList.stream().collect(Collectors.groupingBy(c -> c[9])); 
		    	Collection<?> keys = map.keySet();
		    	for(Object key:keys)
		    	{
		    		if(key.toString().equals("D")) {
		    			procurementOnDemand=map.get(key);
			    	}else if(key.toString().equals("S")) {
			    		procurementOnSanction=map.get(key);
			    	}
		    	 }
	    	}
//	    	procurementOnDemandlist.add(procurementStatusList);
//	    	procurementOnDemandlist.add(procurementOnDemand);
//	    	procurementOnSanctionlist.add(procurementOnSanction);
	    	
	    	procurementOnDemandlist.add(procurementOnDemand);
	    	procurementOnSanctionlist.add(procurementOnSanction);
	    	
	    	req.setAttribute("procurementOnDemand", procurementOnDemand);
	    	req.setAttribute("procurementOnSanction", procurementOnSanction);
	    }
/* ----------------------------------------------------------------------------------------------------------  */
	    	
	    	req.setAttribute("TechImages",TechImages);
	    	req.setAttribute("committeeData", committee);
	    	req.setAttribute("projectattributes",projectattributes);
    		req.setAttribute("ebandpmrccount", ebandpmrccount);	    		
    		req.setAttribute("milestonesubsystems", milestonesubsystems);
    		req.setAttribute("milestones", milestones);	  
    		req.setAttribute("lastpmrcactions", lastpmrcactions);
    		req.setAttribute("lastpmrcminsactlist", lastpmrcminsactlist);
    		req.setAttribute("ProjectDetails", ProjectDetails);
    		req.setAttribute("ganttchartlist", ganttchartlist );
    		req.setAttribute("oldpmrcissueslist",oldpmrcissueslist);	    		
    		req.setAttribute("riskmatirxdata",riskmatirxdata);	    		
    		req.setAttribute("lastpmrcdecisions" , lastpmrcdecisions);	    		
    		req.setAttribute("actionplanthreemonths" , actionplanthreemonths);  	
    		req.setAttribute("projectdatadetails",projectdatadetails);
    		
    		
    		req.setAttribute("ReviewMeetingList",ReviewMeetingListEB);
    		req.setAttribute("ReviewMeetingListPMRC",ReviewMeetingListPMRC);
    		
    		req.setAttribute("financialDetails",financialDetails);
    		req.setAttribute("procurementOnDemandlist",procurementOnDemandlist);
    		req.setAttribute("procurementOnSanctionlist",procurementOnSanctionlist);
    		
    		req.setAttribute("TechWorkDataList",TechWorkDataList);
    		req.setAttribute("ProjectRevList", ProjectRevList);
        	
    		req.setAttribute("projectidlist",Pmainlist);
    		req.setAttribute("projectid",projectid);
    		req.setAttribute("committeeid",tempid);
    		req.setAttribute("ProjectCost",ProjectCost);
	    	req.setAttribute("isprint", "0");
	    	req.setAttribute("AppFilesPath",ApplicationFilesDrive);
	    	req.setAttribute("projectLabCode",projectLabCode);
	    	req.setAttribute("labInfo", service.LabDetailes(projectLabCode));
	    	req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(projectLabCode));    
            req.setAttribute("filePath", env.getProperty("ApplicationFilesDrive"));
            req.setAttribute("milestonedatalevel6", milestonesubsystemsnew);
    		req.setAttribute("ApplicationFilesDrive",env.getProperty("ApplicationFilesDrive"));
    		req.setAttribute("committeeMetingsCount", service.ProjectCommitteeMeetingsCount(projectid, "0", "0", "0", "0", CommitteeCode) );
    		req.setAttribute("nextMeetVenue", service.BriefingMeetingVenue(projectid, committeeid));
    		
    		String LevelId= "2";
			
			if(service.MileStoneLevelId(projectid,committeeid) != null) {
				LevelId= service.MileStoneLevelId(projectid,committeeid)[0].toString();
			}
			  		
			req.setAttribute("levelid", LevelId);
					
		    		
		    		
			    	String filename="BriefingPaper";		
			    	
			    	String path=req.getServletContext().getRealPath("/view/temp");
			    	req.setAttribute("path",path); 
			    	CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			    	req.getRequestDispatcher("/view/print/BriefingPaperNew1.jsp").forward(req, customResponse);
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
		            ImageData leftLogo = ImageDataFactory.create(env.getProperty("ApplicationFilesDrive")+"\\images\\lablogos\\drdo.png");
		            ImageData rightLogo = ImageDataFactory.create(env.getProperty("ApplicationFilesDrive")+"\\images\\lablogos\\"+projectLabCode.toLowerCase()+".png");
			        PdfWriter pdfw=new PdfWriter(path +File.separator+ "mergedb.pdf");
			        
			        
			        PdfDocument pdfDocMain = new PdfDocument(new PdfReader(path+File.separator+filename+".pdf"),new PdfWriter(path+File.separator+filename+"Maintemp.pdf"));
			        Document docMain = new Document(pdfDocMain,PageSize.A4);
			        docMain.setMargins(50, 50, 50, 50);
			        Rectangle pageSizeMain;
			        PdfCanvas canvasMAin;
			        int main = pdfDocMain.getNumberOfPages();
			        for (int i = 1; i <= main; i++) {
			            PdfPage pageMain = pdfDocMain.getPage(i);
			            pageSizeMain = pageMain.getPageSize();
			            canvasMAin = new PdfCanvas(pageMain);

		        	    // Left Logo
		        	    Image leftImage = new Image(leftLogo);
		        	    leftImage.setFixedPosition(i, 54, pageSizeMain.getHeight() - 34);
		        	    leftImage.scaleToFit(34, 33);
		        	    
		        	    // Right Logo
		        	    Image rightImage = new Image(rightLogo);
		        	    rightImage.setFixedPosition(i, pageSizeMain.getWidth() - 64, pageSizeMain.getHeight() - 34);
		        	    rightImage.scaleToFit(34, 33);
		        	    
		        	    // Add images to the document
		        	    docMain.add(leftImage);
		        	    docMain.add(rightImage);
		
			        }
			        docMain.close();
			        pdfDocMain.close();
			        Path pathOfFileMain= Paths.get( path+File.separator+filename+".pdf");
			        Files.delete(pathOfFileMain);	
			        
			        
			        PdfReader pdf1=new PdfReader(path+File.separator+filename+"Maintemp.pdf");
			        PdfDocument pdfDocument = new PdfDocument(pdf1, pdfw);
			        PdfMerger merger = new PdfMerger(pdfDocument);
			        int z=0;
			        try {
			        	for(Object[] objData:projectdatadetails)
						{	
				        	if(objData!=null) {
				        	
				        	try {
				        		String No2=null;
				        		if(CommitteeCode.equalsIgnoreCase("PMRB")){ 
				        		No2="P"+(Long.parseLong(ebandpmrccount.get(0).get(0)[1].toString())+1);
				        		}else if(CommitteeCode.equalsIgnoreCase("EB")){
				        			No2="E"+(Long.parseLong(ebandpmrccount.get(0).get(1)[1].toString())+1);
				        		} 
					        	if(new File(env.getProperty("ApplicationFilesDrive")+"\\"+projectLabCode+"\\gantt\\grantt_"+objData[1]+"_"+No2+".pdf").exists()) {
					        		 	PdfDocument pdfDocument2 = new PdfDocument(new PdfReader(env.getProperty("ApplicationFilesDrive")+"\\"+projectLabCode+"\\gantt\\grantt_"+objData[1]+"_"+No2+".pdf"),new PdfWriter(path+File.separator+filename+"temp.pdf"));
								        Document document5 = new Document(pdfDocument2,PageSize.A4);
								        document5.setMargins(50, 50, 50, 50);
								        Rectangle pageSize;
								        PdfCanvas canvas;
								        int n = pdfDocument2.getNumberOfPages();
								        for (int i = 1; i <= n; i++) {
								            PdfPage page = pdfDocument2.getPage(i);
								            pageSize = page.getPageSize();
								            canvas = new PdfCanvas(page);

								            // Left Logo
							        	    Image leftImage = new Image(leftLogo);
							        	    leftImage.setFixedPosition(i, 10, pageSize.getHeight() - 50);
							        	    leftImage.scaleToFit(40, 40);
							        	    
							        	    // Right Logo
							        	    Image rightImage = new Image(rightLogo);
							        	    rightImage.setFixedPosition(i, pageSize.getWidth() - 50, pageSize.getHeight() - 50);
							        	    rightImage.scaleToFit(40, 40);
							        	    
							        	    // Add images to the document
							        	    document5.add(leftImage);
							        	    document5.add(rightImage);
							        	    
								            canvas.beginText().setFontAndSize(
								                    PdfFontFactory.createFont(StandardFonts.HELVETICA), 11)
								                    .moveText(pageSize.getWidth() / 2 - 124, pageSize.getHeight() - 25)
								                    .showText(objData[12]+" :-  Grantt Chart ")
								                    .endText();
								            
			
								        }
								        document5.close();
								        pdfDocument2.close();
							        	PdfReader pdf2=new PdfReader(path+"/"+filename+"temp.pdf");
							        	 PdfDocument pdfDocument3 = new PdfDocument(pdf2);
								        merger.merge(pdfDocument3, 1, pdfDocument3.getNumberOfPages());
								        
								        pdfDocument3.close();
								        pdf2.close();
								        Path pathOfFile= Paths.get( path+File.separator+filename+"temp.pdf");
								        Files.delete(pathOfFile);	
								        pathOfFile= Paths.get( path+File.separator+TechWorkDataList.get(z)[8].toString());
								        
									    }
					        	 
					        	}
				        	
				        		catch (Exception e) {
				        			logger.error(new Date() +" Inside ProjectBriefingDownload "+UserId, e);
									e.printStackTrace();
								}
				        	
				        	}
				        	
				        	
				        	if(objData!=null && objData[3]!=null) {
				        	if(FilenameUtils.getExtension(objData[3].toString()).equalsIgnoreCase("pdf")) {
					        PdfDocument pdfDocument2 = new PdfDocument(new PdfReader(env.getProperty("ApplicationFilesDrive")+objData[2]+"\\"+objData[3]),new PdfWriter(path+File.separator+filename+"temp.pdf"));
					        Document document5 = new Document(pdfDocument2,PageSize.A4);
					        document5.setMargins(50, 50, 50, 50);
					        Rectangle pageSize;
					        PdfCanvas canvas;
					        int n = pdfDocument2.getNumberOfPages();
					        for (int i = 1; i <= n; i++) {
					            PdfPage page = pdfDocument2.getPage(i);
					            pageSize = page.getPageSize();
					            canvas = new PdfCanvas(page);
					            
					            // Left Logo
				        	    Image leftImage = new Image(leftLogo);
				        	    leftImage.setFixedPosition(i, 10, pageSize.getHeight() - 50);
				        	    leftImage.scaleToFit(40, 40);
				        	    
				        	    // Right Logo
				        	    Image rightImage = new Image(rightLogo);
				        	    rightImage.setFixedPosition(i, pageSize.getWidth() - 50, pageSize.getHeight() - 50);
				        	    rightImage.scaleToFit(40, 40);
				        	    
				        	    // Add images to the document
				        	    document5.add(leftImage);
				        	    document5.add(rightImage);
				        	    
					            canvas.beginText().setFontAndSize(
					                    PdfFontFactory.createFont(StandardFonts.HELVETICA), 11)
					                    .moveText(pageSize.getWidth() / 2 - 124, pageSize.getHeight() - 25)
					                    .showText(objData[12]+" :-  System Configuration Annexure: ")
					                    .endText();
			
					        }
					        document5.close();
					        pdfDocument2.close();
				        	PdfReader pdf2=new PdfReader(path+"/"+filename+"temp.pdf");
				        	 PdfDocument pdfDocument3 = new PdfDocument(pdf2);
					        merger.merge(pdfDocument3, 1, pdfDocument3.getNumberOfPages());
					        
					        pdfDocument3.close();
					        pdf2.close();
					        Path pathOfFile= Paths.get( path+File.separator+filename+"temp.pdf");
					        Files.delete(pathOfFile);	
						    }
				        	}
				        	if(objData!=null && objData[4]!=null) {
						    if(FilenameUtils.getExtension(objData[4].toString()).equalsIgnoreCase("pdf")) {
						    	PdfDocument pdfDocument2 = new PdfDocument(new PdfReader(env.getProperty("ApplicationFilesDrive")+objData[2]+"\\"+objData[4]),new PdfWriter(path+File.separator+filename+"temp.pdf"));
						        Document document5 = new Document(pdfDocument2,PageSize.A4);
						        document5.setMargins(50, 50, 50, 50);
						        Rectangle pageSize;
						        PdfCanvas canvas;
						        int n = pdfDocument2.getNumberOfPages();
						        for (int i = 1; i <= n; i++) {
						            PdfPage page = pdfDocument2.getPage(i);
						            pageSize = page.getPageSize();
						            canvas = new PdfCanvas(page);

						            // Left Logo
					        	    Image leftImage = new Image(leftLogo);
					        	    leftImage.setFixedPosition(i, 10, pageSize.getHeight() - 50);
					        	    leftImage.scaleToFit(40, 40);
					        	    
					        	    // Right Logo
					        	    Image rightImage = new Image(rightLogo);
					        	    rightImage.setFixedPosition(i, pageSize.getWidth() - 50, pageSize.getHeight() - 50);
					        	    rightImage.scaleToFit(40, 40);
					        	    
					        	    // Add images to the document
					        	    document5.add(leftImage);
					        	    document5.add(rightImage);
					        	    
						            canvas.beginText().setFontAndSize(
						                    PdfFontFactory.createFont(StandardFonts.HELVETICA),11)
						                    .moveText(pageSize.getWidth() / 2 - 130, pageSize.getHeight() - 25)
						                    .showText(objData[12]+" :-  System Specification Annexure: ")
						                    .endText();
			
						        }
						        document5.close();
						        pdfDocument2.close();
					        	PdfReader pdf2=new PdfReader(path+"/"+filename+"temp.pdf");
					        	 PdfDocument pdfDocument3 = new PdfDocument(pdf2);
						        merger.merge(pdfDocument3, 1, pdfDocument3.getNumberOfPages());
						        
						        pdfDocument3.close();
						        pdf2.close();
						        Path pathOfFile= Paths.get( path+File.separator+filename+"temp.pdf");
						        Files.delete(pathOfFile);	
						    }
				        	}
				        	if(objData!=null && objData[5]!=null) {
						    if(FilenameUtils.getExtension(objData[5].toString()).equalsIgnoreCase("pdf")) {
						    	PdfDocument pdfDocument2 = new PdfDocument(new PdfReader(env.getProperty("ApplicationFilesDrive")+objData[2]+"\\"+objData[5]),new PdfWriter(path+File.separator+filename+"temp.pdf"));
						        Document document5 = new Document(pdfDocument2,PageSize.A4);
						        document5.setMargins(50, 50, 50, 50);
						        Rectangle pageSize;
						        PdfCanvas canvas;
						        int n = pdfDocument2.getNumberOfPages();
						        for (int i = 1; i <= n; i++) {
						            PdfPage page = pdfDocument2.getPage(i);
						            pageSize = page.getPageSize();
						            canvas = new PdfCanvas(page);

						            // Left Logo
					        	    Image leftImage = new Image(leftLogo);
					        	    leftImage.setFixedPosition(i, 10, pageSize.getHeight() - 50);
					        	    leftImage.scaleToFit(40, 40);
					        	    
					        	    // Right Logo
					        	    Image rightImage = new Image(rightLogo);
					        	    rightImage.setFixedPosition(i, pageSize.getWidth() - 50, pageSize.getHeight() - 50);
					        	    rightImage.scaleToFit(40, 40);
					        	    
					        	    // Add images to the document
					        	    document5.add(leftImage);
					        	    document5.add(rightImage);
					        	    
						            canvas.beginText().setFontAndSize(
						                    PdfFontFactory.createFont(StandardFonts.HELVETICA), 11)
						                    .moveText(pageSize.getWidth() / 2 - 140, pageSize.getHeight() - 25)
						                    .showText(objData[12]+" :-  Overall Product tree/WBS Annexure: ")
						                    .endText();
			
						        }
						        document5.close();
						        pdfDocument2.close();
					        	PdfReader pdf2=new PdfReader(path+"/"+filename+"temp.pdf");
					        	 PdfDocument pdfDocument3 = new PdfDocument(pdf2);
						        merger.merge(pdfDocument3, 1, pdfDocument3.getNumberOfPages());
						        
						        pdfDocument3.close();
						        pdf2.close();
						        Path pathOfFile= Paths.get( path+File.separator+filename+"temp.pdf");
						        Files.delete(pathOfFile);	
							    }
				        	}
				        	if(objData!=null&&objData[6]!=null) {
						    if(FilenameUtils.getExtension(objData[6].toString()).equalsIgnoreCase("pdf")) {
						    	PdfDocument pdfDocument2 = new PdfDocument(new PdfReader(env.getProperty("ApplicationFilesDrive")+objData[2]+"\\"+objData[6]),new PdfWriter(path+File.separator+filename+"temp.pdf"));
						        Document document5 = new Document(pdfDocument2,PageSize.A4);
						        document5.setMargins(50, 50, 50, 50);
						        Rectangle pageSize;
						        PdfCanvas canvas;
						        int n = pdfDocument2.getNumberOfPages();
						        for (int i = 1; i <= n; i++) {
						            PdfPage page = pdfDocument2.getPage(i);
						            pageSize = page.getPageSize();
						            canvas = new PdfCanvas(page);

						            // Left Logo
					        	    Image leftImage = new Image(leftLogo);
					        	    leftImage.setFixedPosition(i, 10, pageSize.getHeight() - 50);
					        	    leftImage.scaleToFit(40, 40);
					        	    
					        	    // Right Logo
					        	    Image rightImage = new Image(rightLogo);
					        	    rightImage.setFixedPosition(i, pageSize.getWidth() - 50, pageSize.getHeight() - 50);
					        	    rightImage.scaleToFit(40, 40);
					        	    
					        	    // Add images to the document
					        	    document5.add(leftImage);
					        	    document5.add(rightImage);
					        	    
						            canvas.beginText().setFontAndSize(
						                    PdfFontFactory.createFont(StandardFonts.HELVETICA), 11)
						                    .moveText(pageSize.getWidth() / 2 - 145, pageSize.getHeight() - 25)
						                    .showText(objData[12]+" :-  TRL table with TRL at sanction stage Annexure ")
						                    .endText();
						            
			
						        }
						        document5.close();
						        pdfDocument2.close();
					        	PdfReader pdf2=new PdfReader(path+"/"+filename+"temp.pdf");
					        	PdfDocument pdfDocument3 = new PdfDocument(pdf2);
						        merger.merge(pdfDocument3, 1, pdfDocument3.getNumberOfPages());
						        
						        pdfDocument3.close();
						        pdf2.close();
						        Path pathOfFile= Paths.get( path+File.separator+filename+"temp.pdf");
						        Files.delete(pathOfFile);	
							    }
				        	}
				        	
				        	//Point 13
				        	try {
				        	
				        	 if(FilenameUtils.getExtension(TechWorkDataList.get(z)[8].toString()).equalsIgnoreCase("pdf")) {
				        		 Zipper zip=new Zipper();
				        		 String tecdata = TechWorkDataList.get(z)[6].toString().replaceAll("[/\\\\]", ",");
				        		 String[] fileParts = tecdata.split(",");
				        		 String zipName = String.format(TechWorkDataList.get(z)[7].toString()+TechWorkDataList.get(z)[11].toString()+"-"+TechWorkDataList.get(z)[10].toString()+".zip");
				        		 Path techPath = null;
				        		 if(fileParts.length == 4){
				        		 	techPath = Paths.get(env.getProperty("ApplicationFilesDrive"), fileParts[0],fileParts[1],fileParts[2],fileParts[3],zipName);
				        		 }else{
				        			techPath = Paths.get(env.getProperty("ApplicationFilesDrive"), fileParts[0],fileParts[1],fileParts[2],fileParts[3],fileParts[4],zipName);
				        		 }
				                 zip.unpack(techPath.toString(),path,TechWorkDataList.get(z)[9].toString());
							    	PdfDocument pdfDocument2 = new PdfDocument(new PdfReader(path+File.separator+TechWorkDataList.get(z)[8].toString()),new PdfWriter(path+File.separator+filename+"temp.pdf"));
							        Document document5 = new Document(pdfDocument2,PageSize.A4);
							        document5.setMargins(50, 50, 50, 50);
							        Rectangle pageSize;
							        PdfCanvas canvas;
							        int n = pdfDocument2.getNumberOfPages();
							        for (int i = 1; i <= n; i++) {
							            PdfPage page = pdfDocument2.getPage(i);
							            pageSize = page.getPageSize();
							            canvas = new PdfCanvas(page);

							            // Left Logo
						        	    Image leftImage = new Image(leftLogo);
						        	    leftImage.setFixedPosition(i, 10, pageSize.getHeight() - 50);
						        	    leftImage.scaleToFit(40, 40);
						        	    
						        	    // Right Logo
						        	    Image rightImage = new Image(rightLogo);
						        	    rightImage.setFixedPosition(i, pageSize.getWidth() - 50, pageSize.getHeight() - 50);
						        	    rightImage.scaleToFit(40, 40);
						        	    
						        	    // Add images to the document
						        	    document5.add(leftImage);
						        	    document5.add(rightImage);
						        	    
							            canvas.beginText().setFontAndSize(
							                    PdfFontFactory.createFont(StandardFonts.HELVETICA), 11)
							                    .moveText(pageSize.getWidth() / 2 - 80, pageSize.getHeight() - 25)
							                    .showText(objData[12]+" :-  Technical Details ")
							                    .endText();
							            
			
							        }
							        document5.close();
							        pdfDocument2.close();
						        	PdfReader pdf2=new PdfReader(path+"/"+filename+"temp.pdf");
						        	PdfDocument pdfDocument3 = new PdfDocument(pdf2);
							        merger.merge(pdfDocument3, 1, pdfDocument3.getNumberOfPages());
							        
							        pdfDocument3.close();
							        pdf2.close();
							        Path pathOfFile= Paths.get( path+File.separator+filename+"temp.pdf");
							        Files.delete(pathOfFile);	
							        pathOfFile= Paths.get( path+File.separator+TechWorkDataList.get(z)[8].toString());
							        Files.delete(pathOfFile);	
								    }
				        	
				        	}catch (Exception e) {
				        		logger.error(new Date() +" Inside ProjectBriefingDownload "+UserId, e);
								e.printStackTrace();
								
							}
				        	
				        	z++;
						}
			        }catch (Exception e) {
			        	return new File(path+File.separator+filename+"Maintemp.pdf");
					}
		
			        pdfDocument.close();
			        merger.close();
			        document.close();
			        pdf1.close();	       
			        pdfw.close();
	        
			        return new File(path +File.separator+ "mergedb.pdf");
		    	
		    
	    }
	    catch(Exception e) {	    		
    		logger.error(new Date() +" Inside ProjectBriefingFreeze.htm "+UserId, e);
    		e.printStackTrace();
			return null;
	
    	}		
		
	
	}
	
	
	
	@PostMapping(value = "ProjectBriefingFreeze.htm")
	public String freezeBriefingPaper(HttpServletRequest req, HttpServletResponse res, HttpSession ses,RedirectAttributes redir)throws Exception
	{

		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		logger.info(new Date() +"Inside ProjectBriefingFreeze.htm "+UserId);		
	    try {
	    	String projectid=req.getParameter("projectid");
	    	String committeeid= req.getParameter("committeeid");
	    	String scheduleid= req.getParameter("committeescheduleid");
	    	long nextScheduleId=0;
	    	if(scheduleid==null) {
	    		nextScheduleId=service.getNextScheduleId(projectid, committeeid);
	    		if(nextScheduleId==0){
	    			redir.addAttribute("result", "No meeting is scheduled for this project");
	    		    redir.addFlashAttribute("projectid",projectid);
	    			redir.addFlashAttribute("committeeid",committeeid);
	    			return "redirect:/ProjectBriefingPaper.htm";
	    		}
	    	}else {
	    		nextScheduleId=Long.parseLong(scheduleid);
	    	}
		    if(nextScheduleId>0) 
		    {
		    	String freezeflag = service.getNextScheduleFrozen(nextScheduleId);
		    	if(freezeflag.equalsIgnoreCase("N")) 
		    	{
		    		
		    		String projectLabCode = service.ProjectDetails(projectid).get(0)[5].toString();
		    		File BPFile =  BriefingPaperGeneratePDF(req, res, ses, redir);
		    		
			        
	    			CommitteeProjectBriefingFrozen briefing = CommitteeProjectBriefingFrozen.builder()
		        											.ScheduleId(nextScheduleId)
		        											.FreezeByEmpId(Long.parseLong(EmpId))
		        											.BriefingFile(BPFile)
		        											.LabCode(projectLabCode)
		        											.IsActive(1)
		        											.build();
		       
	    			long count = service.FreezeBriefing(briefing);
	    			
	    			BPFile.delete();	
			        
	    			if(count>0)
	    			{
	    				int update=service.updateBriefingPaperFrozen(nextScheduleId,"Y","N","N");
	    				redir.addAttribute("result", "Briefing Paper Frozen Successfully");
	    			}
	    			else 
	    			{
	    				redir.addAttribute("resultfail", "Briefing Paper Freezing Failed");	
	    			}
	    			
		    	}
		    	else
		    	{
		    		redir.addAttribute("result", "Briefing Paper Already Frozen!");
		    	}
		    	
		    }
		    
		    redir.addFlashAttribute("projectid",projectid);
			redir.addFlashAttribute("committeeid",committeeid);
			return "redirect:/ProjectBriefingPaper.htm";
	    	
	    }
	    catch(Exception e) {	    		
    		logger.error(new Date() +" Inside ProjectBriefingFreeze.htm "+UserId, e);
    		e.printStackTrace();
			return "static/Error";
	
    	}		
		
	
	}
	

	@RequestMapping(value = "MeetingBriefingPaper.htm")
	public void MeetingBriefingPaper(HttpServletRequest req, HttpServletResponse res, HttpSession ses,RedirectAttributes redir)throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside ProjectBriefing.htm "+UserId);		
	    try { 
				String ScheduleId = req.getParameter("scheduleid");
				CommitteeProjectBriefingFrozen briefing = service.getFrozenProjectBriefing(ScheduleId);
				res.setContentType("application/pdf");
			    res.setHeader("Content-disposition","inline;filename="+"Briefing Paper"+".pdf"); 
			    Path filepath = Paths.get(env.getProperty("ApplicationFilesDrive"), LabCode, "Briefing", briefing.getBriefingFileName());
//			    File file=new File(env.getProperty("ApplicationFilesDrive") +briefing.getFrozenBriefingPath()+briefing.getBriefingFileName());
			    File file=filepath.toFile();
			    FileInputStream fis = new FileInputStream(file);
			    DataOutputStream os = new DataOutputStream(res.getOutputStream());
			    res.setHeader("Content-Length",String.valueOf(file.length()));
			    byte[] buffer = new byte[1024];
			    int len = 0;
			    while ((len = fis.read(buffer)) >= 0) {
			     os.write(buffer, 0, len);
			    } 
			    os.close();
			    fis.close();
	    }
	    catch(Exception e) {	    		
    		logger.error(new Date() +" Inside ProjectBriefingFreeze.htm "+UserId, e);
    		e.printStackTrace();
	
    	}		
	}
	
	@RequestMapping(value = "MeetingBriefingPresenttaion.htm")
	public void MeetingBriefingPresenttaion(HttpServletRequest req, HttpServletResponse res, HttpSession ses,RedirectAttributes redir)throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside MeetingBriefingPresenttaion.htm "+UserId);		
	    try { 
			String ScheduleId = req.getParameter("scheduleid");
			CommitteeProjectBriefingFrozen briefing = service.getFrozenProjectBriefing(ScheduleId);
				res.setContentType("application/pdf");
			    res.setHeader("Content-disposition","inline;filename="+"Briefing Paper"+".pdf"); 
			    Path filepath = Paths.get(env.getProperty("ApplicationFilesDrive"), LabCode, "Briefing", briefing.getPresentationName());
//			    File file=new File(env.getProperty("ApplicationFilesDrive") +briefing.getFrozenBriefingPath()+briefing.getPresentationName());
			    File file=filepath.toFile();
			    FileInputStream fis = new FileInputStream(file);
			    DataOutputStream os = new DataOutputStream(res.getOutputStream());
			    res.setHeader("Content-Length",String.valueOf(file.length()));
			    byte[] buffer = new byte[1024];
			    int len = 0;
			    while ((len = fis.read(buffer)) >= 0) {
			        os.write(buffer, 0, len);
			    } 
			    os.close();
			    fis.close();
	    }
	    catch(Exception e) {	    		
    		logger.error(new Date() +" Inside MeetingBriefingPresenttaion.htm "+UserId, e);
    		e.printStackTrace();
	
    	}		
	}
	
	@RequestMapping(value = "MeetingMom.htm")
	public void MeetingMom(HttpServletRequest req, HttpServletResponse res, HttpSession ses,RedirectAttributes redir)throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside MeetingMom.htm "+UserId);		
	    try { 
			String ScheduleId = req.getParameter("scheduleid");
			CommitteeProjectBriefingFrozen briefing = service.getFrozenProjectBriefing(ScheduleId);
				res.setContentType("application/pdf");
			    res.setHeader("Content-disposition","inline;filename="+"Briefing Paper"+".pdf"); 
			    Path filepath = Paths.get(env.getProperty("ApplicationFilesDrive"), LabCode, "Briefing", briefing.getMoM());
//			    File file=new File(env.getProperty("ApplicationFilesDrive") +briefing.getFrozenBriefingPath()+briefing.getMoM());
			    File file=filepath.toFile();
			    FileInputStream fis = new FileInputStream(file);
			    DataOutputStream os = new DataOutputStream(res.getOutputStream());
			    res.setHeader("Content-Length",String.valueOf(file.length()));
			    byte[] buffer = new byte[1024];
			    int len = 0;
			    while ((len = fis.read(buffer)) >= 0) {
			        os.write(buffer, 0, len);
			    } 
			    os.close();
			    fis.close();
	    }
	    catch(Exception e) {	    		
    		logger.error(new Date() +" Inside MeetingMom.htm "+UserId, e);
    		e.printStackTrace();
	
    	}		
	}
	
	
	@RequestMapping(value="ProjectBriefing.htm", method = RequestMethod.POST)
	public String ProjectBriefing(HttpServletRequest req, HttpSession ses, RedirectAttributes redir,HttpServletResponse res)	throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");	  
    	String token=(String)ses.getAttribute("token");
    	String auToken = "Bearer "+token;
		logger.info(new Date() +"Inside ProjectBriefing.htm "+UserId);		
	    try {    	
	    	
	    	String projectid=req.getParameter("projectid");
	    	String committeeid = req.getParameter("committeeid");
	    	if(committeeid==null  ) {
	    		committeeid="PMRB";
	    	}else if( Long.parseLong(committeeid)==0)
	    	{
	    		committeeid="PMRB";
	    	}

	    	String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
	    	String Logintype= (String)ses.getAttribute("LoginType");
	    	List<Object[]> projectslist =service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
	    	if(projectslist.size()==0) 
	        {				
				redir.addAttribute("resultfail", "No Project is Assigned to you.");
				return "redirect:/MainDashBoard.htm";
			}
	    	if(projectid==null) {
	    		projectid=projectslist.get(0)[0].toString();
	    	}
	    	
	    	Committee committee = service.getCommitteeData(committeeid);
	    	
	    	List<Object[]> projectattributes = new ArrayList<Object[]>();
	    	List<List<Object[]>>  ebandpmrccount = new ArrayList<List<Object[]>>();
	    	List<List<Object[]>> milestonesubsystems = new ArrayList<List<Object[]>>();
	    	List<List<Object[]>> milestones  = new ArrayList<List<Object[]>>();
	    	List<List<Object[]>> lastpmrcactions = new ArrayList<List<Object[]>>();
	    	List<List<Object[]>> lastpmrcminsactlist = new ArrayList<List<Object[]>>();
	    	List<Object[]> ProjectDetails = new ArrayList<Object[]>();
	    	List<List<Object[]>> ganttchartlist = new ArrayList<List<Object[]>>();
	    	List<List<Object[]>> oldpmrcissueslist = new ArrayList<List<Object[]>>();
	    	List<List<Object[]>> riskmatirxdata = new ArrayList<List<Object[]>>();
	    	List<Object[]> lastpmrcdecisions = new ArrayList<Object[]>();
	    	List<List<Object[]>> actionplanthreemonths = new ArrayList<List<Object[]>>();
	    	
    		List<List<Object[]>> ReviewMeetingList = new ArrayList<List<Object[]>>();
    		List<Object[]> TechWorkDataList =new ArrayList<Object[]>();
	    	List<Object[]> projectdatadetails  = new ArrayList<Object[]>();
    		
	    	List<List<Object[]>> ProjectRevList =new ArrayList<List<Object[]>>();
	    	List<List<Object[]>> milestonesubsystemsnew = new ArrayList<List<Object[]>>();
	    	
	    	List<List<ProjectFinancialDetails>> financialDetails=new ArrayList<List<ProjectFinancialDetails>>();
	    	List<List<Object[]>> procurementOnDemandlist  = new ArrayList<List<Object[]>>();
    		List<List<Object[]>> procurementOnSanctionlist = new ArrayList<List<Object[]>>();
	    	
    		String CommitteeCode = committee.getCommitteeShortName().trim();
	    	List<String> Pmainlist = service.ProjectsubProjectIdList(projectid);
	    	for(String proid : Pmainlist) 
	    	{	    	
	    		Object[] projectattribute = service.ProjectAttributes(proid);
	    		
	    		projectattributes.add(projectattribute);
	    		ebandpmrccount.add(service.EBAndPMRCCount(proid));
	    		milestonesubsystems.add(service.MilestoneSubsystems(proid));
	    		milestones.add(service.Milestones(proid,committeeid));
	    		lastpmrcactions.add(service.LastPMRCActions(proid,committeeid));
	    		lastpmrcminsactlist.add(service.LastPMRCActions1(proid,committeeid));
	    		ProjectDetails.add(service.ProjectDetails(proid).get(0));
	    		ganttchartlist.add(service.GanttChartList(proid));
	    		oldpmrcissueslist.add(service.OldPMRCIssuesList(proid));
	    		riskmatirxdata.add(service.RiskMatirxData(proid));
	    		lastpmrcdecisions.add(service.LastPMRCDecisions(committeeid,proid));
	    		actionplanthreemonths.add(service.ActionPlanSixMonths(proid,committeeid));
	    		projectdatadetails.add(service.ProjectDataDetails(proid));
	    		ReviewMeetingList.add(service.ReviewMeetingList(projectid, committeeid));
	    		TechWorkDataList.add(service.TechWorkData(proid));
	    		milestonesubsystemsnew.add(service.BreifingMilestoneDetails(proid,committeeid));
	    		
	    		ProjectRevList.add(service.ProjectRevList(proid));
	    		
				/* List<Object[]>totalMilestones=service.totalProjectMilestones(projectid); */
	    		//get all the milestones details based on projectid;
	    		
		/* ----------------------------------------------------------------------------------------------------------	   */  		
//	    		 	final String localUri=uri+"/pfms_serv/financialStatusBriefing?ProjectCode="+projectattribute[0]+"&rupess="+10000000;
//			 		HttpHeaders headers = new HttpHeaders();
//			 		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//			    	headers.set("labcode", LabCode);
//			 		String jsonResult=null;
	    			List<ProjectFinancialDetails> projectDetails=null;
					try{
//					HttpEntity<String> entity = new HttpEntity<String>(headers);
//					ResponseEntity<String> response=restTemplate.exchange(localUri, HttpMethod.POST, entity, String.class);
//					jsonResult=response.getBody();						
						projectDetails = PFMSserv.financialStatusBriefing(auToken,projectattribute[0] != null ? projectattribute[0].toString() : "", "10000000");
					}catch(Exception e) {
						req.setAttribute("errorMsg", "errorMsg");
						req.setAttribute("financialDetails",List.of());
					}
//					ObjectMapper mapper = new ObjectMapper();
//					mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//					mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));

					if(projectDetails!=null) {
						try {
//							projectDetails = mapper.readValue(jsonResult, new TypeReference<List<ProjectFinancialDetails>>(){});
							financialDetails.add(projectDetails);
							req.setAttribute("financialDetails",projectDetails);
						}catch (Exception e) {
							e.printStackTrace();
						}
					}
	    	List<Object[]> procurementStatusList=(List<Object[]>)service.ProcurementStatusList(proid);
	    	List<Object[]> procurementOnDemand=null;
	    	List<Object[]> procurementOnSanction=null;
	    	

	    	if(procurementStatusList!=null)
	    	{
		    	Map<Object, List<Object[]>> map = procurementStatusList.stream().collect(Collectors.groupingBy(c -> c[9])); 
		    	Collection<?> keys = map.keySet();
		    	for(Object key:keys)
		    	{
		    		if(key.toString().equals("D")) {
		    			procurementOnDemand=map.get(key);
			    	}else if(key.toString().equals("S")) {
			    		procurementOnSanction=map.get(key);
			    	}
		    	 }
	    	}
	    	
	    	procurementOnDemandlist.add(procurementOnDemand);
	    	procurementOnSanctionlist.add(procurementOnSanction);
	    	
	    	req.setAttribute("procurementOnDemand", procurementOnDemand);
	    	req.setAttribute("procurementOnSanction", procurementOnSanction);
	    }
/* ----------------------------------------------------------------------------------------------------------	   */
	    	req.setAttribute("projectslist", projectslist);
	    	
	    	
	    	
    		req.setAttribute("TechWorkDataList",TechWorkDataList);
	    	
	    	req.setAttribute("projectattributes",projectattributes);
    		req.setAttribute("ebandpmrccount", ebandpmrccount);	    		
    		req.setAttribute("milestonesubsystems", milestonesubsystems);
    		req.setAttribute("milestones", milestones);	  
    		req.setAttribute("lastpmrcactions", lastpmrcactions);
    		req.setAttribute("lastpmrcminsactlist", lastpmrcminsactlist);
    		req.setAttribute("ProjectDetails", ProjectDetails);
    		req.setAttribute("ganttchartlist", ganttchartlist );
    		req.setAttribute("oldpmrcissueslist",oldpmrcissueslist);	    		
    		req.setAttribute("riskmatirxdata",riskmatirxdata);	    		
    		req.setAttribute("lastpmrcdecisions" , lastpmrcdecisions);	    		
    		req.setAttribute("actionplanthreemonths" , actionplanthreemonths);  	
    		req.setAttribute("projectdatadetails",projectdatadetails);
    		req.setAttribute("ProjectRevList", ProjectRevList);
    		req.setAttribute("financialDetails",financialDetails);
    		req.setAttribute("procurementOnDemandlist",procurementOnDemandlist);
    		req.setAttribute("procurementOnSanctionlist",procurementOnSanctionlist);
    		req.setAttribute("ReviewMeetingList",ReviewMeetingList);
    		req.setAttribute("projectidlist",Pmainlist);
    		req.setAttribute("projectid",projectid);
    		req.setAttribute("committeeid",committeeid);
	    	req.setAttribute("ProjectCost",ProjectCost);
	    	req.setAttribute("isprint", "0");
	    	req.setAttribute("labInfo", service.LabDetailes(LabCode));
	    	req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(LabCode)); 
    		req.setAttribute("milestonedatalevel6", milestonesubsystemsnew);
    		req.setAttribute("nextMeetVenue", service.BriefingMeetingVenue(projectid, committeeid));
    		
    		
    		String LevelId= "2";
			
			if(service.MileStoneLevelId(projectid,committeeid) != null) {
				LevelId= service.MileStoneLevelId(projectid,committeeid)[0].toString();
			}
			  		
			req.setAttribute("levelid", LevelId);
	    	

	    	return "print/BriefingPaperNew1";
	    }
	    catch(Exception e) {	    		
    		logger.error(new Date() +" Inside ProjectBriefing.htm "+UserId, e);
    		e.printStackTrace();
    		return "static/Error";
	
    	}		
	}

//	public int setBriefingDataToResponse(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir,HttpServletResponse res) throws Exception
//	{
//		
//		try {
//		
//		String UserId = (String) ses.getAttribute("Username");
//		String LabCode = (String)ses.getAttribute("labcode");
//
//	    	String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
//	    	String Logintype= (String)ses.getAttribute("LoginType");
//	    	String projectid=req.getParameter("projectid");
//	    	String committeeid = req.getParameter("committeeid");
//	    	
//	    	if(projectid==null || committeeid==null) 
//	    	{
//	    		Map md = model.asMap();	    	
//	    		projectid = (String) md.get("projectid");
//	    		committeeid = (String) md.get("committeeid");
//	    	}
//	    	
//	    	List<Object[]> projectslist =service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
//	    	List<Object[] > projlist= service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
//	    	List<Object[]>SpecialCommitteesList =  service.SpecialCommitteesList(LabCode);
//	    	
//	    	if((projectslist.size()==0 && projectid==null) || projlist.size()==0) 
//	        {				
//				return 0;
//			}
//	    	
//	    	if(projectslist.size()==0 && projectid!=null) 
//	        {				
//				projectslist.addAll(service.ProjectDetails(projectid));
//			}
//	    	
//	    	Map md = model.asMap();
//	    	if(projectid==null) {
//	    	projectid = (String) md.get("projectid");
//	    	committeeid = (String) md.get("committeeid");
//	    	}
//	    	if(projectid==null) {
//	    		projectid=projectslist.get(0)[0].toString();
//	    	}
//	    	
//	    	String tempid=committeeid;
//	    	
//	    	
//	    	if(committeeid==null  ) 
//	    	{
//	    		
//	    		for(Object[] committee : SpecialCommitteesList) {
//	    			if(committee[1].toString().equalsIgnoreCase("PMRB")) {
//	    				
//	    				committeeid=committee[0].toString();
//	    	    		tempid=committee[0].toString();
//	    	    		
//	    	    		break;
//	    			}
//	    		}
//	    		
//	    	}
//	    	else if(Long.parseLong(committeeid)==0)
//	    	{
//	    		for(Object[] committee : SpecialCommitteesList) {
//	    			if(committee[1].toString().equalsIgnoreCase("PMRB")) 
//	    			{
//	    				committeeid=committee[0].toString();
//	    	    		break;
//	    			}
//	    		}
//	    	}
//	    	
//	    	if(LabCode.equalsIgnoreCase(LabCode)) {
//	    	List<Object[]>otherMeetingList = service.otherMeetingList(projectid);
//	    	req.setAttribute("otherMeetingList", otherMeetingList);
//	    	}
//	    	
//	    	Committee committee = service.getCommitteeData(committeeid);
//	    	String projectLabCode = service.ProjectDetails(projectid).get(0)[5].toString();
//	    	String CommitteeCode = committee.getCommitteeShortName().trim();
//	    	
//	    	List<Object[]> projectattributes = new ArrayList<Object[]>();
//	    	List<List<Object[]>>  ebandpmrccount = new ArrayList<List<Object[]>>();
//	    	List<List<Object[]>> milestonesubsystems = new ArrayList<List<Object[]>>();
//	    	List<List<Object[]>> milestonesubsystemsnew = new ArrayList<List<Object[]>>();
//	    	List<List<Object[]>> milestones  = new ArrayList<List<Object[]>>();
//	    	List<List<Object[]>> lastpmrcactions = new ArrayList<List<Object[]>>();
//	    	List<List<Object[]>> lastpmrcminsactlist = new ArrayList<List<Object[]>>();
//	    	List<Object[]> ProjectDetails = new ArrayList<Object[]>();
//	    	List<List<Object[]>> ganttchartlist = new ArrayList<List<Object[]>>();
//	    	List<List<Object[]>> oldpmrcissueslist = new ArrayList<List<Object[]>>();
//	    	List<List<Object[]>> riskmatirxdata = new ArrayList<List<Object[]>>();
//	    	List<Object[]> lastpmrcdecisions = new ArrayList<Object[]>();
//	    	List<List<Object[]>> actionplanthreemonths = new ArrayList<List<Object[]>>();
//	    	List<List<Object[]>> ReviewMeetingList = new ArrayList<List<Object[]>>();
//	    	List<List<Object[]>> ReviewMeetingListPMRC = new ArrayList<List<Object[]>>();
//	    	List<Object[]> projectdatadetails  = new ArrayList<Object[]>();
//    		
//	    	List<List<ProjectFinancialDetails>> financialDetails=new ArrayList<List<ProjectFinancialDetails>>();
//	    	List<List<Object[]>> procurementOnDemandlist  = new ArrayList<List<Object[]>>();
//    		List<List<Object[]>> procurementOnSanctionlist = new ArrayList<List<Object[]>>();
//	    	
//    		List<Object[]> pdffiles =new ArrayList<Object[]>();
//    		
//    		List<Object[]> TechWorkDataList =new ArrayList<Object[]>();
//    		
//    		List<List<Object[]>> ProjectRevList =new ArrayList<List<Object[]>>();
//    		List<List<TechImages>> TechImages =new ArrayList<List<TechImages>>();
//    		List<Object[]> LastMeetingDates =new ArrayList<Object[]>();
//    		//overall finance when ibas is not connected
//    		List<List<Object[]>> overallfinance =new ArrayList<List<Object[]>>();
//    		
//	    	List<String> Pmainlist = service.ProjectsubProjectIdList(projectid);
//	    	for(String proid : Pmainlist) 
//	    	{	   
//	    		
//	    		Object[] projectattribute = service.ProjectAttributes(proid);
//	    		projectattributes.add(projectattribute);
//	    		TechImages.add(service.getTechList(proid));
//	    		ebandpmrccount.add(service.EBAndPMRCCount(proid));
//	    		milestonesubsystems.add(service.MilestoneSubsystems(proid));
//				milestones.add(service.Milestones(proid,committeeid)); /* CALL Pfms_Milestone_Level_Prior(:projectid,committeeid) */
//				lastpmrcactions.add(service.LastPMRCActions(proid, committeeid)); /* CALL Last_PMRC_Actions_List(:projectid,:committeeid); */
//				lastpmrcminsactlist.add(service.LastPMRCActions1(proid,committeeid)); /* CALL last_pmrc_actions_list_bpaper(:projectid,:committeeid); */
//	    		ProjectDetails.add(service.ProjectDetails(proid).get(0));
//	    		ganttchartlist.add(service.GanttChartList(proid));
//				oldpmrcissueslist.add(service.OldPMRCIssuesList(proid));/* CALL Old_Issues_List(:projectid); */
//	    		riskmatirxdata.add(service.RiskMatirxData(proid));
//	    		lastpmrcdecisions.add(service.LastPMRCDecisions(committeeid,proid));
//				actionplanthreemonths.add(service.ActionPlanSixMonths(proid,committeeid)); /* CALL Pfms_Milestone_PDC_New(:projectid, :interval) */
//	    		TechWorkDataList.add(service.TechWorkData(proid)); 
//	    		ReviewMeetingList.add(service.ReviewMeetingList(projectid, "EB"));
//	    		ReviewMeetingListPMRC.add(service.ReviewMeetingList(projectid, "PMRB"));
//	    		ProjectRevList.add(service.ProjectRevList(proid));
//				milestonesubsystemsnew.add(service.BreifingMilestoneDetails(proid, committeeid)); /* */	    	 	
//	    		Object[] prodetails=service.ProjectDataDetails(proid);
//	    		LastMeetingDates.add(null);
//	    		projectdatadetails.add(prodetails);
//	    		
//	   
//	    		
//	    		String[] pdfs=new String[4];
//				if(prodetails!=null && prodetails.length>0)
//				{	
//				
//					if(prodetails[3]!=null) {
//						try {
//							//config
//							if(Files.exists(Paths.get(env.getProperty("ApplicationFilesDrive")+prodetails[2]+File.separator+prodetails[3]))) {
//								pdfs[0] = Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(new File(env.getProperty("ApplicationFilesDrive")+prodetails[2]+File.separator+prodetails[3])));
//							}
//						}catch ( FileNotFoundException  e) {
//							logger.error(new Date() +" Inside ProjectBriefingPaper "+UserId, e);
//							e.printStackTrace();
//							pdfs[0]=null;
//						}
//					}
//					if(prodetails[5]!=null){
//						try {
//							//producttree
//							if(Files.exists(Paths.get(env.getProperty("ApplicationFilesDrive")+prodetails[2]+File.separator+prodetails[5]))) {
//								pdfs[1]=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(new File(env.getProperty("ApplicationFilesDrive")+prodetails[2]+File.separator+prodetails[5])));
//							}
//						}catch (FileNotFoundException e) {
//							logger.error(new Date() +" Inside ProjectBriefingPaper "+UserId, e);
//							pdfs[1]=null;
//						}
//					}
//					if(prodetails[6]!=null) {
//						try {
//							
//	                    //pearlimg
//							if(Files.exists(Paths.get(env.getProperty("ApplicationFilesDrive")+prodetails[2]+File.separator+prodetails[6]))) {
//								pdfs[2]=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(new File(env.getProperty("ApplicationFilesDrive")+prodetails[2]+File.separator+prodetails[6])));
//							}
//						
//						}catch (FileNotFoundException e) {
//							logger.error(new Date() +" Inside ProjectBriefingPaper "+UserId, e);
//							pdfs[2]=null;
//						}
//					}
//					if(prodetails[4]!=null) {
//						try {
//							//Sysspecs
//							
//							if(Files.exists(Paths.get(env.getProperty("ApplicationFilesDrive")+prodetails[2]+File.separator+prodetails[4]))) {
//								pdfs[3]=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(new File(env.getProperty("ApplicationFilesDrive")+prodetails[2]+File.separator+prodetails[4])));
//						}
//						
//							}catch (FileNotFoundException e) {
//								logger.error(new Date() +" Inside ProjectBriefingPaper "+UserId, e);
//								e.printStackTrace();
//								pdfs[3]=null;
//							}
//					}
//						
//				}
//				pdffiles.add(pdfs);
//	    		
//				List<Object[]> envisagedDemandlist  = new ArrayList<Object[]>();
//				envisagedDemandlist=service.getEnvisagedDemandList(projectid);
//				req.setAttribute("envisagedDemandlist", envisagedDemandlist);
//	    	
//	    		
//		/* -------------------------------------------------------------------------------------------------------------- */  		
//	    		 final String localUri=uri+"/pfms_serv/financialStatusBriefing?ProjectCode="+projectattribute[0]+"&rupess="+10000000;
//			 		HttpHeaders headers = new HttpHeaders();
//			 		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//			    	headers.set("labcode", LabCode);
//			 		String jsonResult=null;
//					try {
//						HttpEntity<String> entity = new HttpEntity<String>(headers);
//						ResponseEntity<String> response=restTemplate.exchange(localUri, HttpMethod.POST, entity, String.class);
//						jsonResult=response.getBody();						
//					}catch(Exception e) {
//						req.setAttribute("errorMsg", "errorMsg");
//					}
//					ObjectMapper mapper = new ObjectMapper();
//					mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//					mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
//					List<ProjectFinancialDetails> projectDetails=null;
//					if(jsonResult!=null) {
//						try {
//							/*
//							 * projectDetails = mapper.readValue(jsonResult,
//							 * mapper.getTypeFactory().constructCollectionType(List.class,
//							 * ProjectFinancialDetails.class));
//							 */
//							projectDetails = mapper.readValue(jsonResult, new TypeReference<List<ProjectFinancialDetails>>(){});
//							financialDetails.add(projectDetails);
//							req.setAttribute("financialDetails",projectDetails);
//						} catch (JsonProcessingException e) {
//							e.printStackTrace();
//						}
//					}
//					
//					//make sure IsIbasConnected is no for the labs which does not have ibas.The value of IsIbasConnected is coming form applicationProperties
//					if(IsIbasConnected!=null && IsIbasConnected.equalsIgnoreCase("N")) {
//						overallfinance.add(service.getrOverallFinance(proid));
//				
//					}
//					
//					final String localUri2=uri+"/pfms_serv/getTotalDemand";
//
//			 		String jsonResult2=null;
//					try {
//						HttpEntity<String> entity = new HttpEntity<String>(headers);
//						ResponseEntity<String> response=restTemplate.exchange(localUri2, HttpMethod.POST, entity, String.class);
//						jsonResult2=response.getBody();						
//					}catch(Exception e) {
//						req.setAttribute("errorMsg", "errorMsg");
//					}
//					ObjectMapper mapper2 = new ObjectMapper();
//					mapper2.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//					mapper2.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
//					List<TotalDemand> totaldemand=null;
//					if(jsonResult2!=null) {
//						try {
//							/*
//							 * projectDetails = mapper.readValue(jsonResult,
//							 * mapper.getTypeFactory().constructCollectionType(List.class,
//							 * ProjectFinancialDetails.class));
//							 */
//							totaldemand = mapper2.readValue(jsonResult2, new TypeReference<List<TotalDemand>>(){});
//							req.setAttribute("TotalProcurementDetails",totaldemand);
//						} catch (JsonProcessingException e) {
//							e.printStackTrace();
//						}
//					}
//	 
//	    	List<Object[]> procurementStatusList=(List<Object[]>)service.ProcurementStatusList(proid);
//	    	List<Object[]> procurementOnDemand=null;
//	    	List<Object[]> procurementOnSanction=null;
//	    	
//
//	    	if(procurementStatusList!=null)
//	    	{
//		    	Map<Object, List<Object[]>> map = procurementStatusList.stream().collect(Collectors.groupingBy(c -> c[9])); 
//		    	Collection<?> keys = map.keySet();
//		    	for(Object key:keys)
//		    	{
//		    		if(key.toString().equals("D")) {
//		    			procurementOnDemand=map.get(key);
//			    	}else if(key.toString().equals("S")) {
//			    		procurementOnSanction=map.get(key);
//			    	}
//		    	 }
//	    	}
////	    	procurementOnDemandlist.add(procurementStatusList);
////	    	procurementOnDemandlist.add(procurementOnDemand);
////	    	procurementOnSanctionlist.add(procurementOnSanction);
//	    	
//	    	procurementOnDemandlist.add(procurementOnDemand);
//	    	procurementOnSanctionlist.add(procurementOnSanction);
//	    	
//	    	req.setAttribute("procurementOnDemand", procurementOnDemand);
//	    	req.setAttribute("procurementOnSanction", procurementOnSanction);
//	    }
///* -------------------------------------------------------------------------------------------------------------  */
//		Map<Integer,String> mappmrc = new HashMap<>();
// 		int pmrccount=0;
// 		System.out.println(ReviewMeetingListPMRC.size()+"------"+ReviewMeetingList.size());
// 		for (Object []obj:ReviewMeetingListPMRC.get(0)) {
// 			mappmrc.put(++pmrccount,obj[3].toString());
// 		}
//		int ebcount=0;
//		Map<Integer,String> mapEB = new HashMap<>();
//		for (Object []obj:ReviewMeetingList.get(0)) {
//		mapEB.put(++ebcount,obj[3].toString());
//		}
//	    
//		
//		
//		
//		// new code
//	 	List<Object[]>totalMileStones=service.totalProjectMilestones(projectid);//get all the milestones details based on projectid
//	 	List<Object[]>first=null;   //store the milestones with levelid 1
//	 	List<Object[]>second=null;	// store the milestones with levelid 2
//	 	List<Object[]>three= null; // store the milestones with levelid 3
//	 	Map<Integer,String> treeMapLevOne = new TreeMap<>();  // store the milestoneid with level id 1 and counts 
//	 	Map<Integer,String>treeMapLevTwo= new TreeMap<>(); // store the milestonidid with level id 2 and counts
//	 	Map<Integer,String>treeMapLevThree= new TreeMap<>();  // store the milestoneid with level id 3 and counts 
//	 	 TreeSet<Integer> AllMilestones = new TreeSet<>();   // store the number of milestone in sorted order
//	 	 if(!totalMileStones.isEmpty()) {
//	 	 for(Object[]obj:totalMileStones){
//	 	 AllMilestones.add(Integer.parseInt(obj[0].toString())); // getting the milestones from list
//	 	 }
//	 	
//	 	 for(Integer mile:AllMilestones) {
//	 	 int count=1;
//	 	 first=totalMileStones.stream().
// 			   filter(i->i[26].toString().equalsIgnoreCase("1") && i[22].toString().equalsIgnoreCase(mile+""))
// 				.map(objectArray -> new Object[]{objectArray[0], objectArray[2]})
// 				.collect(Collectors.toList());
//	 		for(Object[]obj:first) {
//	 		treeMapLevOne.put(Integer.parseInt(obj[1].toString()),"A"+(count++));// to get the first level
//	 		}
//	 	}
//	 	for (Map.Entry<Integer,String> entry : treeMapLevOne.entrySet()) {
//	 		int count=1;
//	 		second=totalMileStones.stream().
//	 			   filter(i->i[26].toString().equalsIgnoreCase("2") && i[2].toString().equalsIgnoreCase(entry.getKey()+""))
//	 			    .map(objectArray -> new Object[] {entry.getKey(),objectArray[3]})
//	 			   .collect(Collectors.toList());
//	 	for(Object[]obj:second) {
//	 		treeMapLevTwo.put(Integer.parseInt(obj[1].toString()),entry.getValue()+"-B"+(count++));
//	 	}
//	 	}
//	 	for(Map.Entry<Integer,String>entry: treeMapLevTwo.entrySet()) {
//	 		int count=1;
//	 		three=totalMileStones.stream().
//	 				filter(i->i[26].toString().equalsIgnoreCase("3") && i[3].toString().equalsIgnoreCase(entry.getKey()+""))
//	 				.map(objectArray -> new Object[] {entry.getKey(),objectArray[4]})
//	 				.collect(Collectors.toList());
//	 		for(Object[]obj:three) {
//				treeMapLevThree.put(Integer.parseInt(obj[1].toString()), "C"+(count++)); 
//	 			
//	 		}
//	 	}
//	 }
//	 	req.setAttribute("overallfinance", overallfinance);
//				 	 req.setAttribute("treeMapLevOne", treeMapLevOne);
//				 	 req.setAttribute("treeMapLevTwo", treeMapLevTwo);
//				 	 // new code end
//	    	req.setAttribute("mappmrc", mappmrc);
//	    	req.setAttribute("mapEB", mapEB);
//	    	req.setAttribute("projectslist", projectslist);
//	    	req.setAttribute("committeeData", committee);
//	    	
//	    	req.setAttribute("projectattributes",projectattributes);
//    		req.setAttribute("ebandpmrccount", ebandpmrccount);	    		
//    		req.setAttribute("milestonesubsystems", milestonesubsystems);
//    		req.setAttribute("milestones", milestones);	  
//    		req.setAttribute("lastpmrcactions", lastpmrcactions);
//    		req.setAttribute("lastpmrcminsactlist", lastpmrcminsactlist);
//    		req.setAttribute("ProjectDetails", ProjectDetails);
//    		req.setAttribute("ganttchartlist", ganttchartlist );
//    		req.setAttribute("oldpmrcissueslist",oldpmrcissueslist);	    		
//    		req.setAttribute("riskmatirxdata",riskmatirxdata);	    		
//    		req.setAttribute("lastpmrcdecisions" , lastpmrcdecisions);	    		
//    		req.setAttribute("actionplanthreemonths" , actionplanthreemonths);  	
//    		req.setAttribute("projectdatadetails",projectdatadetails);
//    		req.setAttribute("ReviewMeetingList",ReviewMeetingList);
//    		req.setAttribute("ProjectRevList", ProjectRevList);
//    		req.setAttribute("ReviewMeetingListPMRC",ReviewMeetingListPMRC);
//    		req.setAttribute("financialDetails",financialDetails);
//    		req.setAttribute("procurementOnDemandlist",procurementOnDemandlist);
//    		req.setAttribute("procurementOnSanctionlist",procurementOnSanctionlist);
//        	
//    		req.setAttribute("projectidlist",Pmainlist);
//    		req.setAttribute("projectid",projectid);
//    		req.setAttribute("committeeid",tempid);
//	    	
//    		req.setAttribute("pdffiles",pdffiles);
//    		req.setAttribute("TechWorkDataList",TechWorkDataList);
//    		req.setAttribute("ProjectCost",ProjectCost);
//    		
//    		req.setAttribute("milestoneactivitystatus", service.MilestoneActivityStatus());
//    		req.setAttribute("milestonedatalevel6", milestonesubsystemsnew);  
//    		req.setAttribute("TechImages", TechImages);   
//    		req.setAttribute("filePath", env.getProperty("ApplicationFilesDrive"));
//    		req.setAttribute("projectLabCode",projectLabCode);
//    		req.setAttribute("SpecialCommitteesList",SpecialCommitteesList);
//    		req.setAttribute("labInfo", service.LabDetailes(projectLabCode));
//	    	req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(projectLabCode));
//	    	req.setAttribute("Drdologo", LogoUtil.getDRDOLogoAsBase64String());
//	    	req.setAttribute("thankYouImg", LogoUtil.getThankYouImageAsBase64String());
//	    	req.setAttribute("committeeMetingsCount", service.ProjectCommitteeMeetingsCount(projectid, CommitteeCode) );
//	    	Object[] nextmeetVenue = (Object[])service.BriefingMeetingVenue(projectid, committeeid);
//	    	req.setAttribute("nextMeetVenue", nextmeetVenue);
//	    	if(nextmeetVenue!=null && nextmeetVenue[0]!=null) {
//	    		req.setAttribute("recdecDetails", service.GetRecDecDetails(nextmeetVenue[0].toString()));
//	    	}
//	    	req.setAttribute("RiskTypes", service.RiskTypes());
//	    	
//	    	
//	    	
//    		String LevelId= "2";
//			
//			if(service.MileStoneLevelId(projectid,committeeid) != null) {
//				LevelId= service.MileStoneLevelId(projectid,committeeid)[0].toString();
//			}
//			  		
//			req.setAttribute("levelid", LevelId);
//    		
//    		try {
//    	        if(projectid==null) {
//    	        	try {
//    	        		Object[] pro=projlist.get(0);
//    	        		projectid=pro[0].toString();
//    	        	}catch (Exception e) {
//    					
//    				}
//    	           
//    	        }
//    	        List<Object[]> main=milservice.MilestoneActivityList(projectid);
//    	            	        
//    			req.setAttribute("MilestoneActivityList",main );
//    			req.setAttribute("ProjectList",projlist);
//    			req.setAttribute("ProjectId", projectid);
//    			if(projectid!=null) {
//    				req.setAttribute("ProjectDetailsMil", milservice.ProjectDetails(projectid).get(0));
//    				int MainCount=1;
//    				for(Object[] objmain:main ) {
//    				 int countA=1;
//						List<Object[]> MilestoneActivityA = milservice.MilestoneActivityLevel(objmain[0].toString(),"1"); /* CALL Pfms_Milestone_Level_List(:id,:levelid) */
//    					req.setAttribute(MainCount+"MilestoneActivityA", MilestoneActivityA);
//    					for(Object[] obj:MilestoneActivityA) {
//    						List<Object[]>  MilestoneActivityB=milservice.MilestoneActivityLevel(obj[0].toString(),"2");
//    						req.setAttribute(MainCount+"MilestoneActivityB"+countA, MilestoneActivityB);
//    						int countB=1;
//    						for(Object[] obj1:MilestoneActivityB) {
//    							List<Object[]>  MilestoneActivityC=milservice.MilestoneActivityLevel(obj1[0].toString(),"3");
//    							req.setAttribute(MainCount+"MilestoneActivityC"+countA+countB, MilestoneActivityC);
//    							int countC=1;
//    							for(Object[] obj2:MilestoneActivityC) {
//    								List<Object[]>  MilestoneActivityD=milservice.MilestoneActivityLevel(obj2[0].toString(),"4");
//    								req.setAttribute(MainCount+"MilestoneActivityD"+countA+countB+countC, MilestoneActivityD);
//    								int countD=1;
//    								for(Object[] obj3:MilestoneActivityD) {
//    									List<Object[]>  MilestoneActivityE=milservice.MilestoneActivityLevel(obj3[0].toString(),"5");
//    									req.setAttribute(MainCount+"MilestoneActivityE"+countA+countB+countC+countD, MilestoneActivityE);
//    									countD++;
//    								}
//    								countC++;
//    							}
//    							countB++;
//    						}
//    						countA++;
//    					}
//    					MainCount++;
//    				}
//    			}
//
//    			return 1;
//    		}
//    		catch (Exception e) {
//    			e.printStackTrace(); 
//    			logger.error(new Date() +" Inside ProjectBriefingPaper.htm (Milestone ActivityLogic) "+UserId, e); 
//    			return 0;
//    		}
//    		
//			}	
//			catch (Exception e) {
//    			e.printStackTrace(); 
//    			logger.error(new Date() +" Inside ProjectBriefingPaper.htm (Milestone ActivityLogic) ", e); 
//    			return 0;
//    		}
//    		
//    		
//    		
//		
//	}
	
	@RequestMapping(value="ProjectBriefingPaper.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String ProjectBriefingPaper(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir,HttpServletResponse res)	throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String labCode = (String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside ProjectBriefingPaper.htm "+UserId);		
		try
		{
			int flag = setBriefingDataToResponseOpt(model, req, ses, redir, res);

			if(flag==0) 
			{				
				redir.addAttribute("resultfail", "No Project is Assigned to you.");
				return "redirect:/MainDashBoard.htm";
			}
			req.setAttribute("IsIbasConnected", IsIbasConnected);
			  if(labCode.equalsIgnoreCase("PGAD")) { 
				  return "print/ProjectBriefingPaperPgad"; 
			  }
			return "print/ProjectBriefingPaperNew";
		}
		catch(Exception e) {	    		
			logger.error(new Date() +" Inside ProjectBriefing.htm "+UserId, e);
			e.printStackTrace();
			return "static/Error";
		}		
	}
	
//	@RequestMapping(value="BriefingPresentation.htm", method = {RequestMethod.GET,RequestMethod.POST})
//	public String BriefingPresentation(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir,HttpServletResponse res)	throws Exception 
//	{
//		String UserId = (String) ses.getAttribute("Username");
//		logger.info(new Date() +"Inside BriefingPresentation.htm "+UserId);		
//		try
//		{
//			int flag = setBriefingDataToResponse(model, req, ses, redir, res);
//			int milFlag = setMilestoneDetailsToResponse(model, req, ses, redir, res);
//			if(flag==0) 
//			{		
//
//				redir.addAttribute("cc", "No Project is Assigned to you.");
//				return "redirect:/MainDashBoard.htm";
//			}  
//			req.setAttribute("IsIbasConnected", IsIbasConnected);
//			return "print/BriefingPresentation";
//		}
//		catch(Exception e) {	    		
//			logger.error(new Date() +" Inside BriefingPresentation.htm "+UserId, e);
//			e.printStackTrace();
//			return "static/Error";
//
//		}		
//	}
	
	/* ******************************* Briefing Paper Presentation New Changes ************************ */
	@RequestMapping(value="BriefingPresentation.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String BriefingPresentation(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir,HttpServletResponse res)	throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		logger.info(new Date() +"Inside BriefingPresentationOpt.htm "+UserId);		
		try
		{
			// Run both tasks in parallel
			CompletableFuture<Integer> flagFuture = CompletableFuture.supplyAsync(() ->
			setBriefingDataToResponseOpt(model, req, ses, redir, res)
					);

			CompletableFuture<Integer> milFlagFuture = CompletableFuture.supplyAsync(() ->
			setMilestoneDetailsToResponse(model, req, ses, redir, res)
					);

			// Wait for both to finish
			CompletableFuture.allOf(flagFuture, milFlagFuture).join();

			int flag = flagFuture.get();
			int milFlag = milFlagFuture.get();

			if(flag==0) 
			{		

				redir.addAttribute("cc", "No Project is Assigned to you.");
				return "redirect:/MainDashBoard.htm";
			}  
		
			if(labcode.equalsIgnoreCase("PGAD")) {
				return "print/BriefingPresentationpgad";
			}
			return "print/BriefingPresentation";
		}
		catch(Exception e) {	    		
			logger.error(new Date() +" Inside BriefingPresentationOpt.htm "+UserId, e);
			e.printStackTrace();
			return "static/Error";

		}		
	}
	
	private int setBriefingDataToResponseOpt(Model model, HttpServletRequest req, HttpSession ses, RedirectAttributes redir, HttpServletResponse res) {
		
		try {
			String UserId = (String) ses.getAttribute("Username");
			String LabCode = (String)ses.getAttribute("labcode");
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String Logintype= (String)ses.getAttribute("LoginType");
			
			String projectid=req.getParameter("projectid");
			String committeeid = req.getParameter("committeeid");

			List<Object[]> projectslist =service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
			List<Object[]> SpecialCommitteesList =  service.SpecialCommitteesList(LabCode);

			if (projectslist.isEmpty()) {
			    if (projectid == null) {
			        return 0;
			    } else {
			        projectslist.addAll(service.ProjectDetails(projectid));
			    }
			}

			if (projectid == null && !projectslist.isEmpty()) {
			    projectid = projectslist.get(0)[0].toString();
			}

			if(committeeid==null || (committeeid!=null && Long.parseLong(committeeid)==0)) {
				committeeid = SpecialCommitteesList!=null && SpecialCommitteesList.size()>0 ? SpecialCommitteesList.get(0)[0].toString(): "0";
			}
	
			Committee committee = service.getCommitteeData(committeeid);
			List<Object[]> projectDetails2 = service.ProjectDetails(projectid);
			String projectLabCode = projectDetails2.get(0)[5].toString();

			
	    	req.setAttribute("committeeData", committee);
			req.setAttribute("committeeMetingsCount", service.ProjectCommitteeMeetingsCount(projectid, "0", "0", "0", "0", committee.getCommitteeShortName().trim()) );
			
			processProjectData(req, projectid, committeeid, uri, projectLabCode, UserId, IsIbasConnected,ses);

			Map<String, List<Object[]>> reviewMeetingListMap = new HashMap<String, List<Object[]>>();
			for(Object[] obj : SpecialCommitteesList) {
				reviewMeetingListMap.put(obj[1]+"", service.ReviewMeetingList(projectid, obj[1]+""));
			}
			req.setAttribute("reviewMeetingListMap", reviewMeetingListMap);
			
			milestoneLevelDataMap(req, reviewMeetingListMap, projectid, committee.getCommitteeShortName().trim());
			
			Object[] nextmeetVenue = (Object[])service.BriefingMeetingVenue(projectid, committeeid);
	    	req.setAttribute("nextMeetVenue", nextmeetVenue);
	    	
	    	Object[] lastmeetingVenue = service.getLastcreatedSchedule(projectid, committeeid);
			req.setAttribute("lastmeetingVenue", lastmeetingVenue);

			req.setAttribute("projectslist", projectslist);
			req.setAttribute("SpecialCommitteesList", SpecialCommitteesList);
			req.setAttribute("otherMeetingList", service.otherMeetingList(projectid));
			if (nextmeetVenue != null && nextmeetVenue[0] != null) {				
				req.setAttribute("recdecDetails", service.GetRecDecDetails(nextmeetVenue[0].toString()));
			}
			if(lastmeetingVenue!=null && lastmeetingVenue[0]!=null && LabCode.equalsIgnoreCase("PGAD")) {
				List<Object[]> list = service.getMilestoneBriefingList(lastmeetingVenue[0].toString());
				Map<String,List<Object[]>> milestoneBriefingMap = list!=null ? list.stream().collect(Collectors.groupingBy(obj -> obj[1]!=null?obj[1].toString():"UNKNOWN",LinkedHashMap::new,Collectors.toList())) : new LinkedHashMap<String, List<Object[]>>();
				req.setAttribute("milestoneBriefingMap", milestoneBriefingMap);
				req.setAttribute("recdecDetails", service.GetRecDecDetails(lastmeetingVenue[0].toString()));
			}
	    	req.setAttribute("RiskTypes", service.RiskTypes());
	    	
	    	req.setAttribute("filePath", env.getProperty("ApplicationFilesDrive"));
    		req.setAttribute("projectLabCode",projectLabCode);
	    	req.setAttribute("labInfo", service.LabDetailes(projectLabCode));
	    	req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(projectLabCode));
	    	req.setAttribute("Drdologo", LogoUtil.getDRDOLogoAsBase64String());
	    	req.setAttribute("thankYouImg", LogoUtil.getThankYouImageAsBase64String());
    		req.setAttribute("projectid", projectid);
    		req.setAttribute("committeeid", committeeid);
    		req.setAttribute("ProjectCost",ProjectCost);
    		req.setAttribute("isCCS",projectDetails2.get(0)[6].toString());
    		req.setAttribute("IsIbasConnected", IsIbasConnected);
    		
    		milestoneLevelListforBP(req, projectslist, projectid, committeeid);
			return 1;
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	
	// add token from the session
	public int processProjectData(HttpServletRequest req, String projectid, String committeeid, String uri, String LabCode, String UserId, String IsIbasConnected,HttpSession ses) throws Exception{	  
		  
    	String token=(String)ses.getAttribute("token");
    	String auToken = "Bearer "+token;
    	
	    List<Object[]> projectattributes = new ArrayList<>();
	    List<List<Object[]>> ebandpmrccount = new ArrayList<>();
	    List<List<Object[]>> milestonesubsystemsnew = new ArrayList<>();
	    List<List<Object[]>> milestones = new ArrayList<>();
	    List<List<Object[]>> lastpmrcactions = new ArrayList<>();
	    List<List<Object[]>> lastpmrcminsactlist = new ArrayList<>();
	    List<Object[]> ProjectDetails = new ArrayList<>();
	    List<List<Object[]>> ganttchartlist = new ArrayList<>();
	    List<List<Object[]>> oldpmrcissueslist = new ArrayList<>();
	    List<List<Object[]>> riskmatirxdata = new ArrayList<>();
	    List<List<Object[]>> actionplanthreemonths = new ArrayList<>();
	    List<Object[]> projectdatadetails = new ArrayList<>();
	    List<List<ProjectFinancialDetails>> financialDetails = new ArrayList<>();
	    List<List<Object[]>> procurementOnDemandlist = new ArrayList<>();
	    List<List<Object[]>> procurementOnSanctionlist = new ArrayList<>();
	    List<Object[]> pdffiles = new ArrayList<>();
	    List<Object[]> TechWorkDataList = new ArrayList<>();
	    List<List<Object[]>> ProjectRevList = new ArrayList<>();
	    List<List<TechImages>> TechImages = new ArrayList<>();
	    List<List<Object[]>> overallfinance = new ArrayList<>();

		List<List<Object[]>> sunsetmilestones = new ArrayList<>();
		List<List<ProjectUtilizationBriefingDto>> manpower = new ArrayList<>();
		List<List<ProjectUtilizationBriefingDto>> infrastructure = new ArrayList<>();
		List<List<ProjectUtilizationBriefingDto>> training = new ArrayList<>();
		List<List<ProjectEconomicImpact>> econmicImpact  = new ArrayList<>();

	    try {

	    	List<String> Pmainlist = service.ProjectsubProjectIdList(projectid);

	    	for (String proid : Pmainlist) {
	    		Object[] projectattribute = service.ProjectAttributes(proid);
	    		projectattributes.add(projectattribute);
	    		TechImages.add(service.getTechList(proid));
	    		ebandpmrccount.add(service.EBAndPMRCCount(proid));
	    		milestones.add(service.Milestones(proid, committeeid));
	    		lastpmrcactions.add(service.LastPMRCActions(proid, committeeid));
	    		lastpmrcminsactlist.add(service.LastPMRCActions1(proid, committeeid));
	    		List<Object[]> projDetails = service.ProjectDetails(proid);
	    		ProjectDetails.add(!projDetails.isEmpty() ? projDetails.get(0) : new Object[0]);
	    		ganttchartlist.add(service.GanttChartList(proid));
	    		oldpmrcissueslist.add(service.OldPMRCIssuesList(proid));
	    		riskmatirxdata.add(service.RiskMatirxData(proid));
	    		actionplanthreemonths.add(service.ActionPlanSixMonths(proid, committeeid));
	    		TechWorkDataList.add(service.TechWorkData(proid));
	    		ProjectRevList.add(service.ProjectRevList(proid));
	    		milestonesubsystemsnew.add(service.BreifingMilestoneDetails(proid, committeeid));
	    		
	    		sunsetmilestones.add(service.SunSetMilestones(proid));
				manpower.add(milservice.getManPowerDetailsForBriefing(proid));
				infrastructure.add(milservice.getInfrastructureDetailsForBriefing(proid));
				training.add(milservice.getTrainingDetailsForBriefing(proid));
				econmicImpact.add(milservice.getEconomicImpactForBriefing(proid));

	    		Object[] prodetails = service.ProjectDataDetails(proid);
	    		projectdatadetails.add(prodetails);
	    		
	    		if(prodetails!=null) {
		    		String[] pdfs = new String[4];
		    		String basePath = env.getProperty("ApplicationFilesDrive") + prodetails[2] + File.separator;
		    		pdfs[0] = encodeFile(basePath, prodetails[3]);
		    		pdfs[1] = encodeFile(basePath, prodetails[5]);
		    		pdfs[2] = encodeFile(basePath, prodetails[6]);
		    		pdfs[3] = encodeFile(basePath, prodetails[4]);
		    		pdffiles.add(pdfs);
	    		}
	    		req.setAttribute("envisagedDemandlist", service.getEnvisagedDemandList(projectid));



//	    		List<ProjectFinancialDetails> projectDetails = fetchFromApi(
//	    				uri + "/pfms_serv/financialStatusBriefing?ProjectCode=" + projectattribute[0] + "&rupess=10000000",
//	    				LabCode,
//	    				new TypeReference<List<ProjectFinancialDetails>>() {}
//	    				);
	    		
//	    		financialDetails.add(projectDetails);
	    		try {
	    		financialDetails.add(pfmsServ.financialStatusBriefing(auToken,projectattribute[0]!=null?projectattribute[0].toString():"0", "10000000"));
	    		}catch(Exception e) {
	    			e.printStackTrace();
	    		}
	    		if ("N".equalsIgnoreCase(IsIbasConnected)) {
	    			overallfinance.add(service.getrOverallFinance(proid));
	    		}

//	    		List<TotalDemand> totaldemand = fetchFromApi(
//	    				uri + "/pfms_serv/getTotalDemand",
//	    				LabCode,
//	    				new TypeReference<List<TotalDemand>>() {}
//	    				);
	    		try {
	    		req.setAttribute("TotalProcurementDetails", pfmsServ.getTotalDemand(auToken));
	    		}catch (Exception e) {
	    			req.setAttribute("TotalProcurementDetails", new ArrayList<>());
				}
	    		List<Object[]> procurementStatusList = service.ProcurementStatusList(proid);
	    		if (procurementStatusList != null) {
	    			Map<String, List<Object[]>> map = procurementStatusList.stream()
	    					.filter(c -> c != null && c.length > 9)
	    					.collect(Collectors.groupingBy(c -> String.valueOf(c[9])));

	    			List<Object[]> onDemand = map.getOrDefault("D", new ArrayList<>());
	    			List<Object[]> onSanction = map.getOrDefault("S", new ArrayList<>());

	    			procurementOnDemandlist.add(onDemand);
	    			procurementOnSanctionlist.add(onSanction);

	    			req.setAttribute("procurementOnDemand", onDemand);
	    			req.setAttribute("procurementOnSanction", onSanction);
	    		}
	    	}

	    	req.setAttribute("projectattributes", projectattributes);
	    	req.setAttribute("ebandpmrccount", ebandpmrccount);
	    	req.setAttribute("milestonedatalevel6", milestonesubsystemsnew);
	    	req.setAttribute("milestones", milestones);	 
	    	req.setAttribute("lastpmrcactions", lastpmrcactions);
	    	req.setAttribute("lastpmrcminsactlist", lastpmrcminsactlist);
	    	req.setAttribute("ProjectDetails", ProjectDetails);
	    	req.setAttribute("ganttchartlist", ganttchartlist );
	    	req.setAttribute("oldpmrcissueslist",oldpmrcissueslist);
	    	req.setAttribute("riskmatirxdata",riskmatirxdata);	    		
	    	req.setAttribute("actionplanthreemonths" , actionplanthreemonths); 
	    	req.setAttribute("projectdatadetails",projectdatadetails);
	    	req.setAttribute("financialDetails",financialDetails);
	    	req.setAttribute("procurementOnDemandlist",procurementOnDemandlist);
	    	req.setAttribute("procurementOnSanctionlist",procurementOnSanctionlist);
	    	req.setAttribute("pdffiles",pdffiles);
	    	req.setAttribute("TechWorkDataList",TechWorkDataList);
	    	req.setAttribute("ProjectRevList", ProjectRevList);
	    	req.setAttribute("TechImages", TechImages);   
	    	req.setAttribute("overallfinance", overallfinance);
	    	req.setAttribute("projectidlist", Pmainlist);

			req.setAttribute("sunsetmilestones", sunsetmilestones);
			req.setAttribute("manpowerDetails", manpower);
			req.setAttribute("infrastructureDetails", infrastructure);
			req.setAttribute("trainingDetails", training);
			req.setAttribute("econmicImpactDetails", econmicImpact);
			
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	    
	}
	
	private String encodeFile(String basePath, Object filename) {
	    if (filename == null) return null;
	    try {
	        Path path = Paths.get(basePath + filename);
	        if (Files.exists(path)) {
	            return Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(path.toFile()));
	        }
	    } catch (Exception e) {
	        logger.error("Error encoding file: " + filename, e);
	    }
	    return null;
	}

	private <T> List<T> fetchFromApi(String url, String labCode, TypeReference<List<T>> typeRef) {
	    try {
	        HttpHeaders headers = new HttpHeaders();
	        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
	        headers.set("labcode", labCode);
	        HttpEntity<String> entity = new HttpEntity<>(headers);
	        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
	        String json = response.getBody();
	        if (json != null) {
	            ObjectMapper mapper = new ObjectMapper();
	            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	            mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
	            return mapper.readValue(json, typeRef);
	        }
	    } catch (Exception e) {
	        logger.error("Error fetching from API: " + url, e);
	    }
	    return Collections.emptyList();
	}
	
	private int milestoneLevelDataMap(HttpServletRequest req, Map<String, List<Object[]>> reviewMeetingListMap, String projectid, String committeCode) {
		try {
			Map<Integer, String> committeeWiseMap = new HashMap<>();

			List<Object[]> meetingList = reviewMeetingListMap.get(committeCode);

			IntStream.range(0, meetingList.size()).forEach(i -> committeeWiseMap.put(i + 1, String.valueOf(meetingList.get(i)[3])));

			// Milestone Mapping
			List<Object[]> totalMilestones = service.totalProjectMilestones(projectid);

			Map<Integer, String> levelOneMap = new TreeMap<>();
			Map<Integer, String> levelTwoMap = new TreeMap<>();
			Map<Integer, String> levelThreeMap = new TreeMap<>();

			if (!totalMilestones.isEmpty()) {

			    // Group milestones by Level (26th index) and Parent ID (indexes: 2 for Level 2, 3 for Level 3)
			    Map<String, List<Object[]>> levelGroup = totalMilestones.stream()
			            .collect(Collectors.groupingBy(row -> row[26].toString()));

			    Set<Integer> allMilestoneIds = totalMilestones.stream()
			            .map(row -> Integer.parseInt(row[0].toString()))
			            .collect(Collectors.toCollection(TreeSet::new));

			    // LEVEL 1
			    for (Integer milestoneId : allMilestoneIds) {
			        List<Object[]> levelOne = levelGroup.getOrDefault("1", Collections.emptyList()).stream()
			                .filter(row -> row[22].toString().equals(String.valueOf(milestoneId)))
			                .collect(Collectors.toList());

			        int count = 1;
			        for (Object[] row : levelOne) {
			            levelOneMap.put(Integer.parseInt(row[2].toString()), "A" + (count++));
			        }
			    }

			    // LEVEL 2
			    for (Map.Entry<Integer, String> entry : levelOneMap.entrySet()) {
			        int parentId = entry.getKey();
			        String parentLabel = entry.getValue();

			        List<Object[]> levelTwo = levelGroup.getOrDefault("2", Collections.emptyList()).stream()
			                .filter(row -> row[2].toString().equals(String.valueOf(parentId)))
			                .collect(Collectors.toList());

			        int count = 1;
			        for (Object[] row : levelTwo) {
			            levelTwoMap.put(Integer.parseInt(row[3].toString()), parentLabel + "-B" + (count++));
			        }
			    }

			    // LEVEL 3
			    for (Map.Entry<Integer, String> entry : levelTwoMap.entrySet()) {
			        int parentId = entry.getKey();

			        List<Object[]> levelThree = levelGroup.getOrDefault("3", Collections.emptyList()).stream()
			                .filter(row -> row[3].toString().equals(String.valueOf(parentId)))
			                .collect(Collectors.toList());

			        int count = 1;
			        for (Object[] row : levelThree) {
			            levelThreeMap.put(Integer.parseInt(row[4].toString()), "C" + (count++));
			        }
			    }
			}
			req.setAttribute("treeMapLevOne", levelOneMap);
			req.setAttribute("treeMapLevTwo", levelTwoMap);
			req.setAttribute("committeeWiseMap", committeeWiseMap);
			return 1;
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	private int milestoneLevelListforBP(HttpServletRequest req, List<Object[]> projectslist, String projectid, String committeeid) throws Exception {

		Object[] mileStoneLevelId = service.MileStoneLevelId(projectid,committeeid);  		
		req.setAttribute("levelid", mileStoneLevelId!=null?mileStoneLevelId[0].toString():"2");
		try {
	        if(projectid==null) {
	        	try {
	        		Object[] pro=projectslist.get(0);
	        		projectid=pro[0].toString();
	        	}catch (Exception e) {
					
				}
	           
	        }
	        List<Object[]> main=milservice.MilestoneActivityList(projectid);
	            	        
			req.setAttribute("MilestoneActivityList",main );
			req.setAttribute("ProjectList",projectslist);
			req.setAttribute("ProjectId", projectid);
			if(projectid!=null) {
				req.setAttribute("ProjectDetailsMil", milservice.ProjectDetails(projectid).get(0));
				int MainCount=1;
				for(Object[] objmain:main ) {
				 int countA=1;
					List<Object[]> MilestoneActivityA = milservice.MilestoneActivityLevel(objmain[0].toString(),"1"); /* CALL Pfms_Milestone_Level_List(:id,:levelid) */
					req.setAttribute(MainCount+"MilestoneActivityA", MilestoneActivityA);
					for(Object[] obj:MilestoneActivityA) {
						List<Object[]>  MilestoneActivityB=milservice.MilestoneActivityLevel(obj[0].toString(),"2");
						req.setAttribute(MainCount+"MilestoneActivityB"+countA, MilestoneActivityB);
						int countB=1;
						for(Object[] obj1:MilestoneActivityB) {
							List<Object[]>  MilestoneActivityC=milservice.MilestoneActivityLevel(obj1[0].toString(),"3");
							req.setAttribute(MainCount+"MilestoneActivityC"+countA+countB, MilestoneActivityC);
							int countC=1;
							for(Object[] obj2:MilestoneActivityC) {
								List<Object[]>  MilestoneActivityD=milservice.MilestoneActivityLevel(obj2[0].toString(),"4");
								req.setAttribute(MainCount+"MilestoneActivityD"+countA+countB+countC, MilestoneActivityD);
								int countD=1;
								for(Object[] obj3:MilestoneActivityD) {
									List<Object[]>  MilestoneActivityE=milservice.MilestoneActivityLevel(obj3[0].toString(),"5");
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

			return 1;
		}
		catch (Exception e) {
			e.printStackTrace(); 
			logger.error(new Date() +" Inside ProjectBriefingPaper.htm (Milestone ActivityLogic) "+ e); 
			return 0;
		}
	}
	
	/* ******************************* Briefing Paper Presentation New Changes ************************ */
	
	private int setMilestoneDetailsToResponse(Model model, HttpServletRequest req, HttpSession ses,
			RedirectAttributes redir, HttpServletResponse res) {

		try {
			String projectid=req.getParameter("projectid");





			System.out.println("setMilestoneDetailsToResponse --- "+projectid);

			if(projectid!=null) {
				List<Object[]> main=headservice.GanttChartList(projectid);
				List<Object[]> MilestoneActivityA0=new ArrayList<Object[]>();
				List<Object[]> MilestoneActivityB0=new ArrayList<Object[]>();
				List<Object[]> MilestoneActivityC0=new ArrayList<Object[]>();
				List<Object[]> MilestoneActivityD0=new ArrayList<Object[]>();
				List<Object[]> MilestoneActivityE0=new ArrayList<Object[]>();

				for(Object[] objmain:main ) {
					List<Object[]>  MilestoneActivityA1=headservice.MilestoneActivityLevel(objmain[0].toString(),"1");
					MilestoneActivityA0.addAll(MilestoneActivityA1);

					for(Object[] obj:MilestoneActivityA1) {
						List<Object[]>  MilestoneActivityB1=headservice.MilestoneActivityLevel(obj[0].toString(),"2");
						MilestoneActivityB0.addAll(MilestoneActivityB1);

						for(Object[] obj1:MilestoneActivityB1) {
							List<Object[]>  MilestoneActivityC1=headservice.MilestoneActivityLevel(obj1[0].toString(),"3");
							MilestoneActivityC0.addAll(MilestoneActivityC1);

							for(Object[] obj2:MilestoneActivityC1) {
								List<Object[]>  MilestoneActivityD1=headservice.MilestoneActivityLevel(obj2[0].toString(),"4");
								MilestoneActivityD0.addAll( MilestoneActivityD1);

								for(Object[] obj3:MilestoneActivityD1) {
									List<Object[]>  MilestoneActivityE1=headservice.MilestoneActivityLevel(obj3[0].toString(),"5");
									MilestoneActivityE0.addAll( MilestoneActivityE1);
								}
							}
						}
					}
				}
				req.setAttribute("MilestoneActivityMain0", main);
				req.setAttribute("MilestoneActivityE0", MilestoneActivityE0);
				req.setAttribute("MilestoneActivityD0", MilestoneActivityD0);
				req.setAttribute("MilestoneActivityC0", MilestoneActivityC0);
				req.setAttribute("MilestoneActivityB0", MilestoneActivityB0);
				req.setAttribute("MilestoneActivityA0", MilestoneActivityA0);

			}

		}catch (Exception e) {
			logger.error(new Date() +" Inside BriefingPresentation.htm ");
			e.printStackTrace();
			return 0;
		}
		return 1;
	}
	
	@RequestMapping(value="AnnualMeetingSchedules.htm", method = RequestMethod.GET)
	public String AnnualMeetingSchedules(HttpServletRequest req, HttpSession ses, RedirectAttributes redir,HttpServletResponse res)	throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside AnnualMeetingSchedules.htm "+UserId);		
	    try {   
	    	String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
	    	String Logintype= (String)ses.getAttribute("LoginType");
	    	String yeart=req.getParameter("year");
			if(yeart==null) {
			  yeart=fc.getCurrentYear();
			}
			req.setAttribute("year",yeart);
	    	List<Object[]> projectslist =service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
	    	req.setAttribute("projectslist",projectslist);
	    	return "print/AnnualMeetingSchedules";
	    }
	    catch(Exception e) {	    		
    		logger.error(new Date() +" Inside AnnualMeetingSchedules.htm "+UserId, e);
    		e.printStackTrace();
    		return "static/Error";
	
    	}		
	}
	
	@RequestMapping(value = "getMeetingSchedules.htm", method = RequestMethod.GET)
	public @ResponseBody String getWorkingHours(HttpServletRequest req, HttpServletResponse response, HttpSession ses) throws Exception 
	{
		List<Object[]> getWorkingHours =new ArrayList<Object[]>();
		try {
			getWorkingHours = service.getMeetingSchedules(req.getParameter("projectId"),req.getParameter("month"),req.getParameter("year"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Gson json = new Gson();
		return json.toJson(getWorkingHours);
	}
	

	
	
	
	@RequestMapping(value="ProjectDocs.htm" ,method = RequestMethod.GET)
	public String ProjectDocuments(HttpServletRequest req, HttpSession ses, RedirectAttributes redir,HttpServletResponse res)throws Exception
	{
		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside ProjectDocs.htm "+UserId);	
		try {
			
		} catch (Exception e) {
			logger.error(new Date() +" Inside ProjectDocs.htm "+UserId, e);
    		e.printStackTrace();
		}
		return "print/ProjectDocuments";
	}
	
	@RequestMapping(value="ProjectSanctionPreview.htm" ,method = { RequestMethod.POST , RequestMethod.GET})
	public String ProjectSanctionPreview(HttpServletRequest req , RedirectAttributes redir, HttpServletResponse res , HttpSession ses)throws Exception
	{
		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside ProjectSanctionPreview.htm "+UserId);	
		try {
			
			String action = req.getParameter("Action");
	
			String initiationid = req.getParameter("initiationid");
			
			String rdNo = req.getParameter("RddNo");
			String fromdept = req.getParameter("FromDepartment");
			String todept = req.getParameter("ToDepartment"); 
			String authority = req.getParameter("Authority");
			String startdate = req.getParameter("StartDate");
			String estimatefund = req.getParameter("EstimateCost");
			String uac = req.getParameter("USC");
			String videno =req.getParameter("videNo");
			String rddate = req.getParameter("RdDate");
			String fromdate = req.getParameter("fromDate");
			
			String[] copyaddr = req.getParameterValues("CopyAddr");
			

			if("AddEditSanction".equalsIgnoreCase(action)) {
				String initiationsanid = req.getParameter("initiationsanid");
				if(initiationsanid!=null && initiationsanid!="") {
					//edit the project sanction data
					InitiationSanction initiationsac = new InitiationSanction(); 
					initiationsac.setInitiationSanctionId(Long.parseLong(initiationsanid));
					initiationsac.setInitiationId(Long.parseLong(initiationid));
					initiationsac.setAuthorityId(Long.parseLong(authority));
					initiationsac.setFromDeptId(Long.parseLong(fromdept));
					initiationsac.setToDeptId(Long.parseLong(todept));
					initiationsac.setRdNo(rdNo);
					initiationsac.setStartDate(new java.sql.Date(sdf.parse(startdate).getTime()));
					initiationsac.setEstimateFund(Double.parseDouble(estimatefund));
					initiationsac.setUAC(uac);
					initiationsac.setVideNo(videno);
					initiationsac.setRdDate(new java.sql.Date(sdf.parse(rddate).getTime()));
					initiationsac.setFromDate(new java.sql.Date(sdf.parse(fromdate).getTime()));
					initiationsac.setModifiedBy(UserId);
					initiationsac.setModifiedDate(sdf1.format(new Date()));
				Long initiationsanctionid =	service.EditInitiationSanction(initiationsac);
					
				if(initiationsanctionid>0) {
					List<Object[]> copyaddresslist = (List<Object[]>)service.GetCopyAddressList(initiationid);
					for(Object[] obj:copyaddresslist) {
						service.DeleteCopyAddress(obj[4]+"");
					}
				for(int i=0;i<copyaddr.length;i++) {
					InitiationsanctionCopyAddr copyaddress = new InitiationsanctionCopyAddr();
					copyaddress.setInitiationId(Long.parseLong(initiationid));	
					copyaddress.setCopyaddrId(Long.parseLong(copyaddr[i]));
					copyaddress.setCreatedBy(UserId);
					copyaddress.setCreatedDate(sdf1.format(new Date()));
					service.AddCopyAddress(copyaddress);
				}	}
				if(initiationsanctionid>0) {
					redir.addAttribute("result", "Project Sanction Data Edit Successfully ");
					}else {
					    redir.addAttribute("resultfail","Project Sanction Data Edit Unsuccessful");
					}
				redir.addAttribute("projectinitiationid", initiationid);
				return "redirect:/ProjectSanctionPreview.htm";
				
				}else {
					//Add the project sanction data
					InitiationSanction initiationsac = new InitiationSanction(); 
					initiationsac.setInitiationId(Long.parseLong(initiationid));
					initiationsac.setAuthorityId(Long.parseLong(authority));
					initiationsac.setFromDeptId(Long.parseLong(fromdept));
					initiationsac.setToDeptId(Long.parseLong(todept));
					initiationsac.setRdNo(rdNo);
					initiationsac.setStartDate(new java.sql.Date(sdf.parse(startdate).getTime()));
					initiationsac.setEstimateFund(Double.parseDouble(estimatefund));
					initiationsac.setUAC(uac);
					initiationsac.setVideNo(videno);
					initiationsac.setRdDate(new java.sql.Date(sdf.parse(rddate).getTime()));
					initiationsac.setFromDate(new java.sql.Date(sdf.parse(fromdate).getTime()));
					initiationsac.setCreatedBy(UserId);
					initiationsac.setCreatedDate(sdf1.format(new Date()));
				Long initiationsanctionid =	service.AddInitiationSanction(initiationsac);
					
				
				if(initiationsanctionid>0) {
				for(int i=0;i<copyaddr.length;i++) {
					InitiationsanctionCopyAddr copyaddress = new InitiationsanctionCopyAddr();
					copyaddress.setInitiationId(Long.parseLong(initiationid));	
					copyaddress.setCopyaddrId(Long.parseLong(copyaddr[i]));
					copyaddress.setCreatedBy(UserId);
					copyaddress.setCreatedDate(sdf1.format(new Date()));
					service.AddCopyAddress(copyaddress);
				}	}
				if(initiationsanctionid>0) {
					redir.addAttribute("result", "Project Sanction Data Added Successfully ");
					}else {
					    redir.addAttribute("resultfail","Project Sanction Data Add Unsuccessful");
					}
				redir.addAttribute("projectinitiationid", initiationid);
				return "redirect:/ProjectSanctionPreview.htm";
				}
					
			
			}else if("EditSanction".equalsIgnoreCase(action)){
				
				String projectinitiationid = req.getParameter("initiationid");
				List<Object[]> projectinitiationlist = (List<Object[]>)service.GetProjectInitiationSanList();
				req.setAttribute("projectslist", projectinitiationlist);
				List<Object[]> authoritylist = (List<Object[]>)service.GetAuthorityList();
				req.setAttribute("authoritylist", authoritylist);
				List<Object[]> initiationcopy = (List<Object[]>)service.GetinitiationCopyAddr();
				req.setAttribute("initiationcopy", initiationcopy);
				List<Object[]> initiationdept = (List<Object[]>)service.GetinitiationDeptList();
				req.setAttribute("initiationdept", initiationdept);	
				Object[] projectdata = (Object[])service.GetProjectInitiationdata(projectinitiationid);
				List<Object[]> itemlist = (List<Object[]>) service.GetItemList(projectinitiationid);
				req.setAttribute("itemlist", itemlist );
				req.setAttribute("projectdata", projectdata);
				req.setAttribute("projectid", projectinitiationid);
				Object[] initiationSanctionlist = (Object[])service.GetInitiationSanctionData(projectinitiationid);
				req.setAttribute("initiationSanctionlist", initiationSanctionlist);
				List<Object[]> copyaddresslist = (List<Object[]>)service.GetCopyAddressList(projectinitiationid);
				req.setAttribute("copyaddresslist", copyaddresslist);
			
				req.setAttribute("editdata", "edit");
				
			return "print/ProgramSanction";
			}else{

			String projectid = req.getParameter("projectinitiationid");
			List<Object[]> projectinitiationlist = (List<Object[]>)service.GetProjectInitiationSanList();
			req.setAttribute("projectslist", projectinitiationlist);
			List<Object[]> authoritylist = (List<Object[]>)service.GetAuthorityList();
			req.setAttribute("authoritylist", authoritylist);
			List<Object[]> initiationcopy = (List<Object[]>)service.GetinitiationCopyAddr();
			req.setAttribute("initiationcopy", initiationcopy);
			List<Object[]> initiationdept = (List<Object[]>)service.GetinitiationDeptList();
			req.setAttribute("initiationdept", initiationdept);
			
			if(projectid!=null){
				Object[]projectdata = (Object[])service.GetProjectInitiationdata(projectid);
				List<Object[]> itemlist = (List<Object[]>) service.GetItemList(projectid);
				req.setAttribute("itemlist", itemlist );
				req.setAttribute("projectdata", projectdata);
				req.setAttribute("projectid", projectid);
				Object[] initiationSanctionlist = (Object[])service.GetInitiationSanctionData(projectid);
				req.setAttribute("initiationSanctionlist", initiationSanctionlist);
				List<Object[]> copyaddresslist = (List<Object[]>)service.GetCopyAddressList(projectid);
				req.setAttribute("copyaddresslist", copyaddresslist);
			}else {
				Object[] projectdata = (Object[])service.GetProjectInitiationdata(projectinitiationlist.get(0)[0].toString());
				List<Object[]> itemlist = (List<Object[]>) service.GetItemList(projectinitiationlist.get(0)[0].toString());
				req.setAttribute("itemlist", itemlist );
				req.setAttribute("projectdata", projectdata);
				req.setAttribute("projectid", projectinitiationlist.get(0)[0].toString());
				Object[] initiationSanctionlist = (Object[])service.GetInitiationSanctionData(projectinitiationlist.get(0)[0].toString());
				req.setAttribute("initiationSanctionlist", initiationSanctionlist);
				List<Object[]> copyaddresslist = (List<Object[]>)service.GetCopyAddressList(projectinitiationlist.get(0)[0].toString());
				req.setAttribute("copyaddresslist", copyaddresslist);
			}
		}
			
		} catch (Exception e) {
			logger.error(new Date() +" Inside ProjectSanctionPreview.htm "+UserId, e);
    		e.printStackTrace();
		}
		return "print/ProgramSanction";
	}
	
	@RequestMapping(value="PdcExtention.htm" ,method = { RequestMethod.GET , RequestMethod.POST})
	public String PdcExtentionPreview(HttpServletRequest req , HttpServletResponse res , HttpSession ses)throws Exception
	{
		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside PdcExtention.htm "+UserId);	
		try {
			List<Object[]> projectinitiationlist = (List<Object[]>)service.GetProjectInitiationSanList();
			req.setAttribute("projectslist", projectinitiationlist);
			
		} catch (Exception e) {
			logger.error(new Date() +" Inside PdcExtention.htm "+UserId, e);
    		e.printStackTrace();
		}
		return "print/PdcExtention";
	}
	
	@RequestMapping(value="ReallocationReport.htm" ,method = { RequestMethod.GET , RequestMethod.POST})
	public String ReallocationPreview(HttpServletRequest req , HttpServletResponse res , HttpSession ses)throws Exception
	{
		String UserId = (String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside ReallocationReport.htm "+UserId);	
		try {
			List<Object[]> projectinitiationlist = (List<Object[]>)service.GetProjectInitiationSanList();
			req.setAttribute("projectslist", projectinitiationlist);
			
		} catch (Exception e) {
			logger.error(new Date() +" Inside ReallocationReport.htm "+UserId, e);
    		e.printStackTrace();
		}
		return "print/ReallocationFunds";
	}
	
	public void freezeBriefingPaperAfterKickoff(HttpServletRequest req, HttpServletResponse res, HttpSession ses,RedirectAttributes redir)throws Exception{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");	  
    	String token=(String)ses.getAttribute("token");
    	String auToken = "Bearer "+token;
		logger.info(new Date() +"Inside freezeBriefingPaperAfterKickoff "+UserId);		
	    try {
	    	String projectid=req.getParameter("projectid");
	    	String committeeid= req.getParameter("committeeid");
	    	String tempid=committeeid;
	        long nextScheduleId=Long.parseLong(req.getParameter("committeescheduleid"));
	    	if(nextScheduleId>0) {
	    		if(service.getNextScheduleFrozen(nextScheduleId).equalsIgnoreCase("N")) {
	    		
	    	    	String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
	    	    	String Logintype= (String)ses.getAttribute("LoginType");
	    	    	Committee committee = service.getCommitteeData(committeeid);
	    	    	
	    	    	String projectLabCode = service.ProjectDetails(projectid).get(0)[5].toString();
	    	    	String CommitteeCode = committee.getCommitteeShortName().trim();
	    	    	
	    	    	List<Object[]> projectattributes = new ArrayList<Object[]>();
	    	    	List<List<Object[]>>  ebandpmrccount = new ArrayList<List<Object[]>>();
	    	    	List<List<Object[]>> milestonesubsystems = new ArrayList<List<Object[]>>();
	    	    	List<List<Object[]>> milestones  = new ArrayList<List<Object[]>>();
	    	    	List<List<Object[]>> lastpmrcactions = new ArrayList<List<Object[]>>();
	    	    	List<List<Object[]>> lastpmrcminsactlist = new ArrayList<List<Object[]>>();
	    	    	List<Object[]> ProjectDetails = new ArrayList<Object[]>();
	    	    	List<List<Object[]>> ganttchartlist = new ArrayList<List<Object[]>>();
	    	    	List<List<Object[]>> oldpmrcissueslist = new ArrayList<List<Object[]>>();
	    	    	List<List<Object[]>> riskmatirxdata = new ArrayList<List<Object[]>>();
	    	    	List<Object[]> lastpmrcdecisions = new ArrayList<Object[]>();
	    	    	List<List<Object[]>> actionplanthreemonths = new ArrayList<List<Object[]>>();
	    	    	List<List<Object[]>> ReviewMeetingListEB = new ArrayList<List<Object[]>>();
	    	    	List<List<Object[]>> ReviewMeetingListPMRC = new ArrayList<List<Object[]>>();
	    	    	List<Object[]> projectdatadetails  = new ArrayList<Object[]>();
	    	    	List<Object[]> TechWorkDataList =new ArrayList<Object[]>();
	        		List<List<Object[]>> milestonesubsystemsnew = new ArrayList<List<Object[]>>();
	    	    	
	    	    	List<List<Object[]>> ProjectRevList =new ArrayList<List<Object[]>>();
	    	    	
	    	    	List<List<ProjectFinancialDetails>> financialDetails=new ArrayList<List<ProjectFinancialDetails>>();
	    	    	List<List<Object[]>> procurementOnDemandlist  = new ArrayList<List<Object[]>>();
	        		List<List<Object[]>> procurementOnSanctionlist = new ArrayList<List<Object[]>>();
	        		List<List<TechImages>> TechImages =new ArrayList<List<TechImages>>();
	    	    	List<String> Pmainlist = service.ProjectsubProjectIdList(projectid);
	    	    	for(String proid : Pmainlist) 
	    	    	{	    	
	    	    		Object[] projectattribute = service.ProjectAttributes(proid);
	    	    		
	    	    		TechImages.add(service.getTechList(proid));
	    	    		projectattributes.add(projectattribute);
	    	    		ebandpmrccount.add(service.EBAndPMRCCount(proid));
	    	    		milestonesubsystems.add(service.MilestoneSubsystems(proid));
	    	    		milestones.add(service.Milestones(proid,committeeid));
	    	    		lastpmrcactions.add(service.LastPMRCActions(proid,committeeid));
	    	    		lastpmrcminsactlist.add(service.LastPMRCActions1(proid,committeeid));
	    	    		ProjectDetails.add(service.ProjectDetails(proid).get(0));
	    	    		ganttchartlist.add(service.GanttChartList(proid));
	    	    		oldpmrcissueslist.add(service.OldPMRCIssuesList(proid));
	    	    		riskmatirxdata.add(service.RiskMatirxData(proid));
	    	    		lastpmrcdecisions.add(service.LastPMRCDecisions(committeeid,proid));
	    	    		actionplanthreemonths.add(service.ActionPlanSixMonths(proid,committeeid));
	    	    		projectdatadetails.add(service.ProjectDataDetails(proid));
	    	    		ReviewMeetingListEB.add(service.ReviewMeetingList(projectid, "EB"));
	    	    		ReviewMeetingListPMRC.add(service.ReviewMeetingList(projectid, "PMRB"));
	    	    		TechWorkDataList.add(service.TechWorkData(proid));
	    		    	milestonesubsystemsnew.add(service.BreifingMilestoneDetails(proid,committeeid));
	    	    		ProjectRevList.add(service.ProjectRevList(proid));
	    	    	
	    		/* ----------------------------------------------------------------------------------------------------------	   */  		
//	    	    		 final String localUri=uri+"/pfms_serv/financialStatusBriefing?ProjectCode="+projectattribute[0]+"&rupess="+10000000;
//	    			 		HttpHeaders headers = new HttpHeaders();
//	    			 		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//	    			    	headers.set("labcode", LabCode);
	    			 		
//	    			 		String jsonResult=null;
	    	    			List<ProjectFinancialDetails> projectDetails=null;
	    					try {
//	    						HttpEntity<String> entity = new HttpEntity<String>(headers);
//	    						ResponseEntity<String> response=restTemplate.exchange(localUri, HttpMethod.POST, entity, String.class);
//	    						jsonResult=response.getBody();		
	    						projectDetails = PFMSserv.financialStatusBriefing(auToken ,projectattribute[0] != null ? projectattribute[0].toString() : "", "10000000");
	    					}catch(Exception e) {
	    						req.setAttribute("errorMsg", "errorMsg");
    							req.setAttribute("financialDetails",List.of());
	    					}
//	    					ObjectMapper mapper = new ObjectMapper();
//	    					mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//	    					mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
	    					if(projectDetails!=null) {
	    						try {
	    							/*
	    							 * projectDetails = mapper.readValue(jsonResult,
	    							 * mapper.getTypeFactory().constructCollectionType(List.class,
	    							 * ProjectFinancialDetails.class));
	    							 */
//	    							projectDetails = mapper.readValue(jsonResult, new TypeReference<List<ProjectFinancialDetails>>(){});
	    							financialDetails.add(projectDetails);
	    							req.setAttribute("financialDetails",projectDetails);
	    						} catch (Exception e) {
	    							e.printStackTrace();
	    						}
	    					}
	    	    	
//	    					final String localUri2=uri+"/pfms_serv/getTotalDemand";
	    					List<TotalDemand> totaldemand=null;

//	    			 		String jsonResult2=null;
	    					try {
//	    						HttpEntity<String> entity = new HttpEntity<String>(headers);
//	    						ResponseEntity<String> response=restTemplate.exchange(localUri2, HttpMethod.POST, entity, String.class);
//	    						jsonResult2=response.getBody();			
	    						totaldemand = PFMSserv.getTotalDemand(auToken);
	    					}catch(Exception e) {
	    						req.setAttribute("errorMsg", "errorMsg");
    							req.setAttribute("TotalProcurementDetails",List.of());
	    					}
//	    					ObjectMapper mapper2 = new ObjectMapper();
//	    					mapper2.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//	    					mapper2.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
	    					
	    					if(totaldemand!=null) {
	    						try {
	    							/*
	    							 * projectDetails = mapper.readValue(jsonResult,
	    							 * mapper.getTypeFactory().constructCollectionType(List.class,
	    							 * ProjectFinancialDetails.class));
	    							 */
//	    							totaldemand = mapper2.readValue(jsonResult2, new TypeReference<List<TotalDemand>>(){});
	    							req.setAttribute("TotalProcurementDetails",totaldemand);
	    						} catch (Exception e) {
	    							e.printStackTrace();
	    						}
	    					}
	    	 
	    	    	List<Object[]> procurementStatusList=(List<Object[]>)service.ProcurementStatusList(proid);
	    	    	List<Object[]> procurementOnDemand=null;
	    	    	List<Object[]> procurementOnSanction=null;
	    	    	

	    	    	if(procurementStatusList!=null)
	    	    	{
	    		    	Map<Object, List<Object[]>> map = procurementStatusList.stream().collect(Collectors.groupingBy(c -> c[9])); 
	    		    	Collection<?> keys = map.keySet();
	    		    	for(Object key:keys)
	    		    	{
	    		    		if(key.toString().equals("D")) {
	    		    			procurementOnDemand=map.get(key);
	    			    	}else if(key.toString().equals("S")) {
	    			    		procurementOnSanction=map.get(key);
	    			    	}
	    		    	 }
	    	    	}
//	    	    	procurementOnDemandlist.add(procurementStatusList);
//	    	    	procurementOnDemandlist.add(procurementOnDemand);
//	    	    	procurementOnSanctionlist.add(procurementOnSanction);
	    	    	
	    	    	procurementOnDemandlist.add(procurementOnDemand);
	    	    	procurementOnSanctionlist.add(procurementOnSanction);
	    	    	
	    	    	req.setAttribute("procurementOnDemand", procurementOnDemand);
	    	    	req.setAttribute("procurementOnSanction", procurementOnSanction);
	    	    }
	    /* ----------------------------------------------------------------------------------------------------------  */
	    	    	
	    	    	req.setAttribute("TechImages",TechImages);
	    	    	req.setAttribute("committeeData", committee);
	    	    	req.setAttribute("projectattributes",projectattributes);
	        		req.setAttribute("ebandpmrccount", ebandpmrccount);	    		
	        		req.setAttribute("milestonesubsystems", milestonesubsystems);
	        		req.setAttribute("milestones", milestones);	  
	        		req.setAttribute("lastpmrcactions", lastpmrcactions);
	        		req.setAttribute("lastpmrcminsactlist", lastpmrcminsactlist);
	        		req.setAttribute("ProjectDetails", ProjectDetails);
	        		req.setAttribute("ganttchartlist", ganttchartlist );
	        		req.setAttribute("oldpmrcissueslist",oldpmrcissueslist);	    		
	        		req.setAttribute("riskmatirxdata",riskmatirxdata);	    		
	        		req.setAttribute("lastpmrcdecisions" , lastpmrcdecisions);	    		
	        		req.setAttribute("actionplanthreemonths" , actionplanthreemonths);  	
	        		req.setAttribute("projectdatadetails",projectdatadetails);
	        		
	        		
	        		req.setAttribute("ReviewMeetingList",ReviewMeetingListEB);
	        		req.setAttribute("ReviewMeetingListPMRC",ReviewMeetingListPMRC);
	        		
	        		req.setAttribute("financialDetails",financialDetails);
	        		req.setAttribute("procurementOnDemandlist",procurementOnDemandlist);
	        		req.setAttribute("procurementOnSanctionlist",procurementOnSanctionlist);
	        		
	        		req.setAttribute("TechWorkDataList",TechWorkDataList);
	        		req.setAttribute("ProjectRevList", ProjectRevList);
	            	
	        		req.setAttribute("projectidlist",Pmainlist);
	        		req.setAttribute("projectid",projectid);
	        		req.setAttribute("committeeid",tempid);
	        		req.setAttribute("ProjectCost",ProjectCost);
	    	    	req.setAttribute("isprint", "0");
	    	    	req.setAttribute("AppFilesPath",ApplicationFilesDrive);
	    	    	req.setAttribute("projectLabCode",projectLabCode);
	    	    	req.setAttribute("labInfo", service.LabDetailes(projectLabCode));
	    	    	req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(projectLabCode));    
	                req.setAttribute("filePath", env.getProperty("ApplicationFilesDrive"));
	                req.setAttribute("milestonedatalevel6", milestonesubsystemsnew);
	        		req.setAttribute("ApplicationFilesDrive",env.getProperty("ApplicationFilesDrive"));
	        		req.setAttribute("committeeMetingsCount", service.ProjectCommitteeMeetingsCount(projectid, "0", "0", "0", "0", CommitteeCode) );
	        		req.setAttribute("nextMeetVenue", service.BriefingMeetingVenue(projectid, committeeid));
	        		
	        		String LevelId= "2";
	    			
	    			if(service.MileStoneLevelId(projectid,committeeid) != null) {
	    				LevelId= service.MileStoneLevelId(projectid,committeeid)[0].toString();
	    			}
	    			  		
	    			req.setAttribute("levelid", LevelId);
	    		}else {
	    			redir.addAttribute("resultfail", "Briefing Paper Already Frozen for  This Meeting.");
	    		}
	    	}else {
	    		redir.addAttribute("resultfail", "No  Meeting scheduled.");
	    	}
	        
	    }
	    catch(Exception e) {	
	    	redir.addAttribute("resultfail", "Briefing Paper Not Frozen for  This Meeting.");
    		logger.error(new Date() +" Inside freezeBriefingPaperAfterKickoff "+UserId, e);
    		e.printStackTrace();
			/* return "static/Error"; */
	
    	}		
		
	}
	
	@RequestMapping(value = "MilestoneActivityChange.htm", method = RequestMethod.GET)
	public @ResponseBody String MilestoneActivityChange(HttpServletRequest req, HttpServletResponse response, HttpSession ses) throws Exception 
	{
		String UserId=(String)ses.getAttribute("Username");
		logger.info(new Date() +" Inside freezeBriefingPaperAfterKickoff "+UserId);
		String projectid=req.getParameter("projectid");
    	String milestoneactivitystatusid = req.getParameter("milactivitystatusid");
    			
		List<List<Object[]>> milestoneschange =new ArrayList<List<Object[]>>();
		
		try {
		
			List<String> Pmainlist = service.ProjectsubProjectIdList(projectid);
	    	for(String proid : Pmainlist) 
	    	{	    	
	    		milestoneschange.add(service.MilestonesChange(proid,milestoneactivitystatusid));
	    	}
			
		} catch (Exception e) {
			logger.error(new Date() +" Inside freezeBriefingPaperAfterKickoff "+UserId,e);
			e.printStackTrace();
		}
		Gson json = new Gson();
		return json.toJson(milestoneschange);
	}
	
	@RequestMapping(value="MilestoneLevelUpdate.htm", method = RequestMethod.POST)
	public String MilestoneLevelUpdate(HttpServletRequest req, HttpServletResponse res, RedirectAttributes redir,HttpSession ses) throws Exception{
		
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside MilestoneLevelUpdate.htm "+UserId);	
		
		int count=0;
		String projectid=req.getParameter("projectid");
    	String committeeid= req.getParameter("committeeid");
		
		try {

			Object[] LevelDetails= service.MileStoneLevelId(projectid,committeeid); 
			
			MilestoneActivityLevelConfigurationDto dto = new MilestoneActivityLevelConfigurationDto();
			dto.setProjectId(projectid);
			dto.setCommitteeId(committeeid);
			if(LevelDetails!=null) {
			dto.setLevelConfigurationId(LevelDetails[1].toString());
			}
			dto.setLevelid(req.getParameter("milestonelevelid"));
			dto.setCreatedBy(UserId);			
			 count = service.MileStoneLevelUpdate(dto);
			
		}
		catch(Exception e) {	    		
    		logger.error(new Date() +" Inside MilestoneLevelUpdate.htm "+UserId, e);
    		e.printStackTrace();
	
    	}		
		
		if(count>0) {  
            redir.addAttribute("result", "Milestone Level Updated Successfully");
  		}else {
  			redir.addAttribute("resultfail", "Milestone Level Update Unsuccessful");
  		}

		redir.addFlashAttribute("projectid", projectid);
		redir.addFlashAttribute("committeeid", committeeid);
		//redir.addFlashAttribute("tabid", "6");
		
		return "redirect:/ProjectBriefingPaper.htm";
	}
	
	
	
	  	@PostMapping("/GanttChartUpload.htm") 
	    public String GanttChartUpload(@RequestParam("FileAttach") MultipartFile file,HttpServletRequest req,RedirectAttributes redir,HttpSession ses) {
		  String UserId = (String) ses.getAttribute("Username");
		  String Labcode = (String) ses.getAttribute("labcode");
			logger.info(new Date() +"Inside GanttChartUpload.htm "+UserId);	  
		  try {
          
			  if (file == null || file.isEmpty()) {
		            return redirectWithError(redir, "ProjectBriefingPaper.htm", "No file uploaded.");
		        }

		        // 🔹 Check Content-Type
		        String contentType = file.getContentType();
		        // 🔹 Check Extension
		        String originalFilename = file.getOriginalFilename();
		        String extension = (originalFilename != null) ? 
		                            originalFilename.substring(originalFilename.lastIndexOf(".") + 1) : "";

		        if (!"image/png".equalsIgnoreCase(contentType) || !"png".equalsIgnoreCase(extension)) {
		            redir.addFlashAttribute("projectid", req.getParameter("ProjectId"));
		            redir.addFlashAttribute("committeeid", req.getParameter("committeeid"));
		            return redirectWithError(redir, "ProjectBriefingPaper.htm", "Invalid file type. Only PNG files are allowed.");
		        }
  
            int result=service.saveGranttChart(file,req.getParameter("ChartName"),env.getProperty("ApplicationFilesDrive"),Labcode);
            
            
            
            
            if(result>0) {  
              redir.addAttribute("result", "Grantt Chart Saved");
    		}else {
    			redir.addAttribute("resultfail", "Grantt Chart Not Saved");
    		}

	        } catch (Exception e) {
	        	logger.error(new Date() +" Inside GanttChartUpload.htm "+UserId, e);
	        }
	        redir.addFlashAttribute("projectid", req.getParameter("ProjectId"));
			redir.addFlashAttribute("committeeid", req.getParameter("committeeid"));
	        return "redirect:/ProjectBriefingPaper.htm";
	    }
	  
	  
		
	    @PostMapping("/ProjectTechImages.htm") 
	    public String ProjectTechImages(@RequestParam("FileAttach") MultipartFile file,HttpSession ses,HttpServletRequest req,RedirectAttributes redir) {
	    	 String UserId = (String) ses.getAttribute("Username");
	    	 String LabCode= (String) ses.getAttribute("labcode");
				logger.info(new Date() +"Inside GanttChartUpload.htm "+UserId);
				
				
				String os = System.getProperty("os.name");
				
				System.out.println("osss  -----"+os);
	    	try {
	    		
	    		// 🔹 Validate file types
		        if (!isValidFileType(file)) {

		        	redir.addFlashAttribute("projectid", req.getParameter("ProjectId"));
					redir.addFlashAttribute("committeeid", req.getParameter("committeeid"));
		        	
		        	
		        	return redirectWithError(redir, "ProjectBriefingPaper.htm",
		                    "Invalid file type. Only  Image files are allowed.");
		        }
	    		
	    		
	    		
	    		
            int result=service.saveTechImages(file,req.getParameter("ProjectId"),env.getProperty("ApplicationFilesDrive"),req.getUserPrincipal().getName(),LabCode);
            if(result>0) {  
              redir.addAttribute("result", "Tech Image Saved");
    		}else {
    			redir.addAttribute("resultfail", "Tech Image Not Saved");
    		}

	        } catch (Exception e) {
	        	logger.error(new Date() +" Inside ProjectTechImages.htm "+UserId, e);
	        }
	        redir.addFlashAttribute("projectid", req.getParameter("ProjectId"));
			redir.addFlashAttribute("committeeid", req.getParameter("committeeid"));
	        return "redirect:/ProjectBriefingPaper.htm";
	    }
	    
	    @RequestMapping(value="ProjectImageDelete.htm",method = {RequestMethod.POST,RequestMethod.GET})
	    public String ProjectImageDelete(HttpServletRequest req, HttpServletResponse res, RedirectAttributes redir,HttpSession ses)throws Exception
	    {
	   	 String UserId = (String) ses.getAttribute("Username");
    	 String LabCode= (String) ses.getAttribute("labcode");
			logger.info(new Date() +"Inside ProjectImageDelete.htm "+UserId);
    	try {
    		String TechImagesId=req.getParameter("TechImagesId");
    		int count=service.ProjectImageDelete(TechImagesId);
    	    if(count>0) {  
                redir.addAttribute("result", "Tech Image removed successfully");
      		}else {
      			redir.addAttribute("resultfail", "Tech Image Not Deleted");
      		}
    		
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
	    	
    	redir.addFlashAttribute("projectid", req.getParameter("ProjectId"));
		redir.addFlashAttribute("committeeid", req.getParameter("committeeid"));
        return "redirect:/ProjectBriefingPaper.htm";
	    }
	    
	    @RequestMapping(value="DecesionRemove.htm",method = {RequestMethod.POST,RequestMethod.GET})
	    public String DecesionRemove(HttpServletRequest req, HttpServletResponse res, RedirectAttributes redir,HttpSession ses)throws Exception
	    {
	   	 String UserId = (String) ses.getAttribute("Username");
    	 String LabCode= (String) ses.getAttribute("labcode");
			logger.info(new Date() +"Inside DecesionRemove.htm "+UserId);
    	try {
    		String recdecId=req.getParameter("recdecId");
    		int count=service.ProjectDecRecDelete(recdecId);
    	    if(count>0) {  
                redir.addAttribute("result", "Data removed successfully");
      		}else {
      			redir.addAttribute("resultfail", "Data Not Deleted");
      		}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
	    	
    	redir.addFlashAttribute("projectid", req.getParameter("ProjectId"));
		redir.addFlashAttribute("committeeid", req.getParameter("committeeid"));
        return "redirect:/ProjectBriefingPaper.htm";
	    }
	    
	    
	    
	    
	    @RequestMapping(value ="FilterMilestone.htm" ,method = RequestMethod.POST)
	    public String FilterMilestone(HttpServletRequest req, HttpServletResponse res, RedirectAttributes redir,HttpSession ses)throws Exception
	    {
	    	
	    	String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside FilterMilestone.htm "+UserId);
		    try {
		    	String projectid = req.getParameter("projectidvalue");
		    	String committeeid = req.getParameter("committeidvalue");
		    	Committee committee = service.getCommitteeData(committeeid);
		    	String MilestoneActivity = req.getParameter("milestoneactivity");
		    	String CommitteeCode = committee.getCommitteeShortName().trim();
		    	String LevelId= "2";
		    	Object[] getlevelid = service.MileStoneLevelId(projectid,committeeid);
				if(getlevelid != null && getlevelid[0]!=null) {
				LevelId= getlevelid[0].toString();
				}
				List<String> Pmainlist = service.ProjectsubProjectIdList(projectid);
				List<List<Object[]>> MilestoneFilterlist = new ArrayList<List<Object[]>>();
		    	for(String  proid : Pmainlist) 
		    	{
		    	 MilestoneFilterlist.add(service.BreifingMilestoneDetails(proid,committeeid));
		    	}
		    	List<Object[]> main = new ArrayList<>();
     	        if(MilestoneActivity==null || "A".equalsIgnoreCase(MilestoneActivity)) {
     	        	 main=milservice.MilestoneActivityList(projectid);
     	        }else {
     	        	main=milservice.MilestoneActivityList(projectid).stream().filter(statusactivityid-> statusactivityid[14].toString().equalsIgnoreCase(MilestoneActivity)  ).collect(Collectors.toList());
     	        }
		    	if(projectid!=null) {
    			req.setAttribute("ProjectDetailsMil", milservice.ProjectDetails(projectid).get(0));
    			int MainCount=1;
    			for(Object[] objmain:main ) {
    			int countA=1;
    		    List<Object[]>  MilestoneActivityA = new ArrayList<>();
    			MilestoneActivityA=milservice.MilestoneActivityLevel(objmain[0].toString(),"1");
    			req.setAttribute(MainCount+"MilestoneActivityA", MilestoneActivityA);
    			for(Object[] obj:MilestoneActivityA) {
    			List<Object[]>  MilestoneActivityB = new ArrayList<>();
        		MilestoneActivityB=milservice.MilestoneActivityLevel(obj[0].toString(),"2");
    			req.setAttribute(MainCount+"MilestoneActivityB"+countA, MilestoneActivityB);
    			int countB=1;
    			for(Object[] obj1:MilestoneActivityB) {
    			List<Object[]>  MilestoneActivityC = new ArrayList<>();
            	MilestoneActivityC=milservice.MilestoneActivityLevel(obj1[0].toString(),"3");
    			req.setAttribute(MainCount+"MilestoneActivityC"+countA+countB, MilestoneActivityC);
    			int countC=1;
    			for(Object[] obj2:MilestoneActivityC){
    			List<Object[]>  MilestoneActivityD = new ArrayList<>();
                MilestoneActivityD=milservice.MilestoneActivityLevel(obj2[0].toString(),"4");
    			req.setAttribute(MainCount+"MilestoneActivityD"+countA+countB+countC, MilestoneActivityD);
    			int countD=1;
    			for(Object[] obj3:MilestoneActivityD) {
    			List<Object[]>  MilestoneActivityE = new ArrayList<>();
                MilestoneActivityE=milservice.MilestoneActivityLevel(obj3[0].toString(),"5");
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
		    	req.setAttribute("MilestoneActivityList",main );
    			req.setAttribute("ProjectId",projectid );
		    	req.setAttribute("milestoneactivitystatus", service.MilestoneActivityStatus());
		    	req.setAttribute("milestonefilterlist", MilestoneFilterlist);
		    	req.setAttribute("levelid", LevelId);
		    	req.setAttribute("CommitteeId", committeeid);
		    	req.setAttribute("MilestoneActivity", MilestoneActivity);
			} catch (Exception e) {
			logger.error(new Date() +" Inside FilterMilestone.htm "+UserId, e);
	    	e.printStackTrace();
			}	
		    return "milestone/MilestoneFilterList";
	    }
	    
	    
	    

	    @RequestMapping(value="AgendaPresentation.htm", method = {RequestMethod.GET,RequestMethod.POST})
		public String AgendaPresentation(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir,HttpServletResponse res)	throws Exception 
		{
			String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside AgendaPresentation.htm "+UserId);		
			try {
				String scheduleid = req.getParameter("scheduleid");
				String projectid = req.getParameter("projectid");
				String committeeid= req.getParameter("committeeid");

				Committee committee = service.getCommitteeData(committeeid);
				String projectLabCode = committee.getLabCode();
				String CommitteeCode = committee.getCommitteeShortName().trim();


				Object[] scheduledata=service.CommitteeScheduleEditData(scheduleid);

				Object[] scheduleeditdata= comservice.CommitteeScheduleEditData(scheduleid);

				String committeeId = scheduleeditdata!=null && scheduleeditdata[0]!=null? scheduleeditdata[0].toString():"0";
				String scheduledate = scheduleeditdata[2].toString();

				String initiationid = scheduleeditdata!=null && scheduleeditdata[17]!=null? scheduleeditdata[17].toString():"0";
				String divisionid = scheduleeditdata!=null && scheduleeditdata[16]!=null? scheduleeditdata[16].toString():"0";
				String carsInitiationId = scheduleeditdata!=null && scheduleeditdata[25]!=null? scheduleeditdata[25].toString():"0";
				String programmeId = scheduleeditdata!=null && scheduleeditdata[26]!=null? scheduleeditdata[26].toString():"0";
				
				List<Object[]>ActionDetails=comservice.actionDetailsForNonProject(committeeId,scheduledate);
				List<Object[]>actionSubDetails=new ArrayList();
				if(ActionDetails.size()>0) {
					actionSubDetails=ActionDetails.stream().filter(i -> LocalDate.parse(i[9].toString()).isBefore(LocalDate.parse(scheduledate))).collect(Collectors.toList());
					if(actionSubDetails.size()>0) {
						actionSubDetails=actionSubDetails.stream().filter(i -> i[15].toString().equalsIgnoreCase(projectid) && 
																			i[16].toString().equalsIgnoreCase(initiationid) && 
																			i[17].toString().equalsIgnoreCase(divisionid) &&
																			i[18].toString().equalsIgnoreCase(carsInitiationId) &&
																			i[19].toString().equalsIgnoreCase(programmeId)
																			).collect(Collectors.toList());
					}
				}

				List<Object[]>meetingsHeld = comservice.previousMeetingHeld(committeeid);
				if(meetingsHeld !=null && meetingsHeld.size()>0 ) {
					meetingsHeld=meetingsHeld.stream().filter(i -> LocalDate.parse(i[2].toString()).isBefore(LocalDate.parse(scheduledate))).collect(Collectors.toList());
					meetingsHeld = meetingsHeld.stream().filter(i -> i[3].toString().equalsIgnoreCase(projectid) && 
																	i[4].toString().equalsIgnoreCase(initiationid) && 
																	i[5].toString().equalsIgnoreCase(divisionid) &&	
																	i[8].toString().equalsIgnoreCase(carsInitiationId) &&	
																	i[9].toString().equalsIgnoreCase(programmeId)
																	).collect(Collectors.toList());
				}

				List<Object[]>recommendationList = comservice.getRecommendationsOfCommittee(committeeid) ;


				if(recommendationList.size()>0) {
					recommendationList=recommendationList.stream().filter(i -> LocalDate.parse(i[9].toString()).isBefore(LocalDate.parse(scheduledate))).collect(Collectors.toList());
					if(recommendationList.size()>0) {
						recommendationList=recommendationList.stream().filter(i -> i[15].toString().equalsIgnoreCase(projectid) && 
																					i[16].toString().equalsIgnoreCase(initiationid) && 
																					i[17].toString().equalsIgnoreCase(divisionid) &&
																					i[18].toString().equalsIgnoreCase(carsInitiationId) &&
																					i[19].toString().equalsIgnoreCase(programmeId) 
																					).collect(Collectors.toList());
					}
				}

				List<Object[]>decesions = comservice.getDecisionsofCommittee(committeeid);

				if(decesions.size()>0) {
					decesions=decesions.stream().filter(i -> LocalDate.parse(i[2].toString()).isBefore(LocalDate.parse(scheduledate))).collect(Collectors.toList());
					if(decesions.size()>0) {
						decesions=decesions.stream().filter(i -> i[3].toString().equalsIgnoreCase(projectid) &&
																i[4].toString().equalsIgnoreCase(initiationid) && 
																i[5].toString().equalsIgnoreCase(divisionid) &&
																i[6].toString().equalsIgnoreCase(carsInitiationId) &&
																i[7].toString().equalsIgnoreCase(programmeId)
																).collect(Collectors.toList());
					}
				}

				req.setAttribute("labInfo", service.LabDetailes(projectLabCode));
				req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(projectLabCode));
				req.setAttribute("Drdologo", LogoUtil.getDRDOLogoAsBase64String());
				req.setAttribute("committeeData", service.getCommitteeData(committeeid));
				req.setAttribute("projectattributes", service.ProjectAttributes(projectid));
				req.setAttribute("AgendaList", service.AgendaList(scheduleid));
				req.setAttribute("AgendaDocList",service.AgendaLinkedDocList(scheduleid));
				req.setAttribute("committeeMetingsCount", service.ProjectCommitteeMeetingsCount(projectid, divisionid, initiationid, carsInitiationId, programmeId, CommitteeCode) );
				req.setAttribute("scheduledata", scheduledata);
				List<Object[]> specialCommitteesList = service.SpecialCommitteesList(projectLabCode);
				req.setAttribute("SplCommitteeCodes", specialCommitteesList.stream().map(e -> e[1].toString()).collect(Collectors.toList()));
				req.setAttribute("recommendationList",recommendationList);

				req.setAttribute("projectid", projectid);

				req.setAttribute("ActionDetails", actionSubDetails);
				req.setAttribute("meetingsHeld", meetingsHeld);
				req.setAttribute("decesions", decesions);
				if(programmeId!=null && !programmeId.isEmpty()) {
					req.setAttribute("programmeMaster", comservice.getProgrammeMasterById(programmeId));
					req.setAttribute("programmeId", programmeId);
				}
				return "print/AgendaPresentation";
			}catch (Exception e) {
				e.printStackTrace(); 
				logger.error(new Date() +" Inside AgendaPresentation.htm  "+UserId, e); 
				return "static/Error";

			}
		}
	    
//		private String redirectWithError(RedirectAttributes redir,String redirURL, String message) {
//		    redir.addAttribute("resultfail", message);
//		    return "redirect:/"+redirURL;
//		}
		
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

//		    // Allow only images and PDF
//		 // Allowed MIME types
//		    boolean validMime = contentType.equalsIgnoreCase("application/pdf")
//		            || contentType.equalsIgnoreCase("image/jpeg")
//		            || contentType.equalsIgnoreCase("image/png");
	//
//		    // Allowed extensions
//		    boolean validExtension = extension.equals("pdf")
//		            || extension.equals("jpeg")
//		            || extension.equals("jpg")
//		            || extension.equals("png");
	//
//		    return validMime && validExtension;
		}

	    
	    
	    
	    @RequestMapping(value="FrozenBriefingAdd.htm", method = {RequestMethod.GET,RequestMethod.POST})
		public String FrozenBriefingAdd(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir,HttpServletResponse res,@RequestParam(name = "briefingpaper")MultipartFile BPaper ,@RequestParam(name="briefingpresent")MultipartFile pname,@RequestParam(name="Momfile")MultipartFile mom)	throws Exception 
		{
	    	String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
	    	String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside FrozenBriefingAdd.htm "+UserId);		
	    	try {
		    	String scheduleid = req.getParameter("scheduleid");
		    	String projectid=req.getParameter("projectid");
		    	String committeecode=req.getParameter("committeecode");
		    	
		    	String projectLabCode = service.ProjectDetails(projectid).get(0)[5].toString();
		    	
		    	// 🔹 Validate file types
		        if (!isValidFileType(BPaper) || !isValidFileType(pname)
		                || !isValidFileType(mom) ) {

		        	redir.addFlashAttribute("projectid",projectid);
			    	redir.addFlashAttribute("committeecode",committeecode);
		        	
		        	
		        	return redirectWithError(redir, "FroozenBriefingList.htm",
		                    "Invalid file type. Only PDF or Image files are allowed.");
		        }

		    	
		    	
		    	
		    	
		    	
		    	
		    	CommitteeProjectBriefingFrozen briefing = CommitteeProjectBriefingFrozen.builder()
						.ScheduleId(Long.parseLong(scheduleid))
						.FreezeByEmpId(Long.parseLong(EmpId))
						.BriefingFileMultipart(BPaper)
						.LabCode(projectLabCode)
						.PresentationNameMultipart(pname)
						.MomMultipart(mom)
						.IsActive(1)
						.build();
		    		
		    	long count = service.FreezeBriefingMultipart(briefing);
		    	String BriefingPaperFrozen="N";
		    	String PresentationFrozen= "N";
		    	String MinutesFrozen ="N";	
		    	if(!BPaper.isEmpty()) {
		    		BriefingPaperFrozen="Y";
		    	}
		    	if(!pname.isEmpty()) {
		    		PresentationFrozen="Y";
		    	}
		    	if(!mom.isEmpty()) {
		    		MinutesFrozen="Y";
		    	}
		    	
		    	System.out.println(BriefingPaperFrozen+"-----"+PresentationFrozen+"--------"+MinutesFrozen);
		    	
		    	if(count>0)
    			{
    				int update=service.updateBriefingPaperFrozen(Long.parseLong(scheduleid),BriefingPaperFrozen,PresentationFrozen,MinutesFrozen);
    				redir.addAttribute("result", "Documents Added Successfully");
    			}
    			else 
    			{
    				redir.addAttribute("resultfail", "Documents Adding Failed");	
    			}
		    	redir.addFlashAttribute("projectid",projectid);
		    	redir.addFlashAttribute("committeecode",committeecode);
		    	return "redirect:/FroozenBriefingList.htm";
	    	}catch (Exception e) {
    			e.printStackTrace(); 
    			logger.error(new Date() +" Inside FrozenBriefingAdd.htm  "+UserId, e); 
    			return "static/Error";
    			
    		}
		}
	    
	    
	    @RequestMapping(value="FrozenBriefingUpdate.htm", method = {RequestMethod.GET,RequestMethod.POST})
		public String FrozenBriefingUpdate(Model model,HttpServletRequest req, HttpSession ses, RedirectAttributes redir,HttpServletResponse res,@RequestParam(name = "briefingpaper")MultipartFile BPaper ,@RequestParam(name="briefingpresent")MultipartFile pname,@RequestParam(name="Momfile")MultipartFile mom)	throws Exception 
		{
	    	String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside FrozenBriefingUpdate.htm "+UserId);		
	    	try {
	    		
	    	  	String scheduleid = req.getParameter("scheduleid");
		    	String projectid=req.getParameter("projectid");
		    	String committeecode=req.getParameter("committeecode");
		    	String BriefingPaperFrozen=req.getParameter("BriefingPaperFrozen");
		    	String PresentationFrozen= req.getParameter("PresentationFrozen");
		    	String MinutesFrozen =req.getParameter("MinutesFrozen");
		    	String pendingEdit =req.getParameter("pendingEdit");
	    		// 🔹 Validate file types
		        if (!isValidFileType(BPaper) || !isValidFileType(pname)|| !isValidFileType(mom)
		               ) {

		        	redir.addFlashAttribute("projectid",projectid);
			    	redir.addFlashAttribute("committeecode",committeecode);
		        	return redirectWithError(redir, "FroozenBriefingList.htm",
		                    "Invalid file type. Only PDF and Image is Allowed  files are allowed.");
		        }
	    		
	    		
	    		
	    		
	    		
	    		
	    		
	    		
	    		
		  
		    	if(!BPaper.isEmpty() && BriefingPaperFrozen.equalsIgnoreCase("N")) {
		    		BriefingPaperFrozen="Y";
		    	}
		    	if(!pname.isEmpty() && PresentationFrozen.equalsIgnoreCase("N")) {
		    		PresentationFrozen="Y";
		    	}
		    	if(!mom.isEmpty() && MinutesFrozen.equalsIgnoreCase("N")) {
		    		MinutesFrozen="Y";
		    	}
		    
		    	long count = service.FreezeBriefingMultipartUpdate(scheduleid, BPaper,pname,mom);
		    	if(count>0)
    			{
		    		int update=service.updateBriefingPaperFrozen(Long.parseLong(scheduleid),BriefingPaperFrozen,PresentationFrozen,MinutesFrozen);
    				redir.addAttribute("result", "Documents updated Successfully");
    			}
    			else 
    			{
    				redir.addAttribute("resultfail", "Documents updating Failed");	
    			}
		    	if(pendingEdit.equalsIgnoreCase("P")) {
		    		redir.addAttribute("pendingClick", "N");
		    	}
		    	redir.addFlashAttribute("projectid",projectid);
		    	redir.addFlashAttribute("committeecode",committeecode);
		    	return "redirect:/FroozenBriefingList.htm";
	    	}catch (Exception e) {
    			e.printStackTrace(); 
    			logger.error(new Date() +" Inside FrozenBriefingUpdate.htm  "+UserId, e); 
    			return "static/Error";
    			
    		}
		}
	    
		
		@RequestMapping(value = "FroozenBriefingList.htm")
		public String FroozenBriefingList(Model model, HttpServletRequest req, HttpSession ses, HttpServletResponse res,RedirectAttributes redir) throws Exception 
		{
			String UserId = (String) ses.getAttribute("Username");
			String LabCode = (String) ses.getAttribute("labcode");
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
	    	String Logintype= (String)ses.getAttribute("LoginType");
			logger.info(new Date() +"Inside FroozenBriefingList.htm "+UserId);		
			try {
				
				String projectid= req.getParameter("projectid");
				String committeecode= req.getParameter("committeecode");
				String commiteeName= req.getParameter("commiteeName");
				String pendingClick= req.getParameter("pendingClick");
				String initiatedClick= req.getParameter("initiatedClick");
				String revProjectId= req.getParameter("revProjectId");
			
				
				List<Object[]> projectslist  =service.LoginProjectDetailsList(EmpId, Logintype, LabCode);
				List<Object[]> divisionHeadList  =service.getDivisionHeadList();
				
				
				if(projectslist.size()==0) 
				{
					redir.addAttribute("resultfail","Project is Not Assigned!" );
					return "redirect:/MainDashBoard.htm";
				}
				if(projectid==null || committeecode==null) 
		    	{
		    		Map md = model.asMap();	    	
		    		projectid = (String) md.get("projectid");
		    		committeecode = (String) md.get("committeecode");
		    	}
				if(projectid==null) {
					projectid  = projectslist.get(0)[0].toString();
					committeecode = "PMRB";
				}
				List<Object[]> BriefingScheduleList =new ArrayList<Object[]>();
				List<Object[]> BriefingScheduleFwdList =new ArrayList<Object[]>();
				List<Object[]> BriefingScheduleFwdApprovedList =new ArrayList<Object[]>();
				if(revProjectId!=null) {
					if(commiteeName!=null) {
					if(commiteeName.equalsIgnoreCase("EB")) {
						BriefingScheduleList=service.BriefingScheduleList(LabCode, commiteeName, revProjectId);
					}}else {
						BriefingScheduleList=service.BriefingScheduleList(LabCode, committeecode, revProjectId);
					}
					
				}else {
					if(commiteeName!=null) {
					if(commiteeName.equalsIgnoreCase("EB")) {
						BriefingScheduleList=service.BriefingScheduleList(LabCode, commiteeName, projectid);
					}}else {
						BriefingScheduleList=service.BriefingScheduleList(LabCode, committeecode, projectid);
					}
					
				
				}
				
				BriefingScheduleFwdList=service.BriefingScheduleFwdList(LabCode,EmpId);
				if(commiteeName!=null) {
				if(commiteeName.equalsIgnoreCase("EB")) {
					BriefingScheduleFwdApprovedList=service.BriefingScheduleFwdApprovedList(LabCode, commiteeName, projectid,EmpId);
				}}else {
					BriefingScheduleFwdApprovedList=service.BriefingScheduleFwdApprovedList(LabCode, committeecode, projectid,EmpId);
				}
				
				//String pDId=BriefingScheduleList.get(0)[13].toString();
				
				req.setAttribute("DoRtmdAdEmpData",service.DoRtmdAdEmpData(LabCode));
				req.setAttribute("directorName",service.getDirectorName(LabCode));
				req.setAttribute("projectid",projectid);
				req.setAttribute("committeecode",committeecode);
				req.setAttribute("commiteeName",commiteeName);
				req.setAttribute("projectslist", projectslist);
				req.setAttribute("BriefingScheduleList",BriefingScheduleList );
				req.setAttribute("BriefingScheduleFwdList",BriefingScheduleFwdList );
				req.setAttribute("BriefingScheduleFwdApprovedList",BriefingScheduleFwdApprovedList );
				req.setAttribute("divisionHeadList",divisionHeadList);
				req.setAttribute("DHId",service.getDHId(projectid));
				req.setAttribute("GHId",service.getGHId(projectid));
				req.setAttribute("pendingClick",pendingClick);
				req.setAttribute("initiatedClick",initiatedClick);
				req.setAttribute("revProjectId",revProjectId);
				req.setAttribute("EmpId",EmpId);
				return "print/ScheduleBriefingList";
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside FroozenBriefingList.htm "+UserId, e);
				return "static/error";
			}
		}
		
		
		@RequestMapping(value = "RecDecDetailsAdd.htm" ,method = RequestMethod.POST)
		public String RecDecAdd(HttpServletRequest req , HttpSession ses , RedirectAttributes redir)throws Exception
		{
			String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside RecDecDetailsAdd.htm "+UserId);	
			try {
				String committeeid = req.getParameter("committeeid");
				String projectid = req.getParameter("projectid");
				
				String recid  = req.getParameter("RedDecID");
				String recdec = req.getParameter("RecDecPoints");
				String type = req.getParameter("darc");
				String schedulid = req.getParameter("schedulid");
				String val="";
				if(type.equalsIgnoreCase("D")) {
					val="Decision";
				}else {
					val="Recommendations";
				}
				RecDecDetails rcdc = new RecDecDetails();
				
				if(recid!=null && recid!=""){
					rcdc.setRecDecId(Long.parseLong(recid));
				}
				rcdc.setType(type);
				rcdc.setPoint(recdec);
				rcdc.setScheduleId(Long.parseLong(schedulid));
				long result  =service.RedDecAdd(rcdc,UserId);
				if(recid!=null && recid!="") {
					if(result>0)
	    			{
	    				redir.addAttribute("result", val+" Updated Successfully");
	    			}else{
	    				redir.addAttribute("resultfail", val+" Update Failed");	
	    			}
				}else {
					if(result>0)
	    			{
	    				redir.addAttribute("result", val+" Added Successfully");
	    			}else{
	    				redir.addAttribute("resultfail", val+" Adding Failed");	
	    			}
				}
				redir.addFlashAttribute("projectid",projectid);
				redir.addFlashAttribute("committeeid",committeeid);
				return "redirect:/ProjectBriefingPaper.htm";
			}catch(Exception e){
				e.printStackTrace();
				logger.error(new Date() +" Inside RecDecDetailsAdd.htm "+UserId, e);
				return "static/error";
			}
		}
		
		@RequestMapping(value = "Getrecdecdata.htm" ,method = RequestMethod.GET)
		public @ResponseBody String Getrecdecdata(HttpServletRequest req)throws Exception
		{
			Object[] list=null;
			try {
				String recdecid = req.getParameter("recdesid");
				 list = service.GetRecDecData(recdecid);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Gson json = new Gson();
			return json.toJson(list);
		}
		
		@RequestMapping(value = "PfmsProjectSlides.htm" ,method = {RequestMethod.POST,RequestMethod.GET})
		public String ProjectSlides(Model model, RedirectAttributes redir , HttpServletRequest req , HttpSession ses)throws Exception
		{
			String UserId = (String) ses.getAttribute("Username");
			String labcode = (String) ses.getAttribute("labcode");
			logger.info(new Date() +"Inside PfmsProjectSlides.htm "+UserId);	
			try {
				String projectid = (String)req.getParameter("projectid");
				if(projectid==null) 
			    {
			    	Map md = model.asMap();	    	
			    	projectid = (String) md.get("projectid");
			    }

				Object[] projectdata = (Object[])service.GetProjectdata(projectid); // all vals
				req.setAttribute("projectdata", projectdata);
				Object[] projectslidedata = (Object[])service.GetProjectSildedata(projectid);
				if(projectdata==null){
					redir.addAttribute("resultfail", "Refresh Not Allowed!");
					return "redirect:/MainDashBoard.htm";
				}
				
				if(projectslidedata!=null) {
					if(projectslidedata[1]==null)projectslidedata[1]=1;
//					List<Object[]> getTodatfreezedSlidedata= service.GetTodayFreezedSlidedata(projectid);
					List<Object[]> getAllProjectSlidedata= service.GetAllProjectSildedata(projectid);
					List<Object[]> getFreezingData = service.GetFreezingHistory(projectid);
					if(getFreezingData.size()>0)
						for(int i=0;i<getFreezingData.size();i++){if(getFreezingData.get(i)[0]!=null)
						getFreezingData.get(i)[0]=CarsService.getEmpDetailsByEmpId(getFreezingData.get(i)[0].toString())[1];
						else {
							getFreezingData.get(i)[0]="-";
						}
					}
					req.setAttribute("FreezingData", getFreezingData);
					req.setAttribute("getAllProjectSlidedata", getAllProjectSlidedata);
//					req.setAttribute("freezedSlidedata", getTodatfreezedSlidedata);
					req.setAttribute("filepath", ApplicationFilesDrive);
					req.setAttribute("projectslidedata", projectslidedata);
					req.setAttribute("Drdologo", LogoUtil.getDRDOLogoAsBase64String());
					req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(labcode));
					
					return "print/ProjectSlideEditView";
				}
				return "print/ProjectSlide";
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside PfmsProjectSlides.htm "+UserId, e);
				return "static/error";
			}
		}
		
		@RequestMapping(value="AddProjectSlides.htm" , method = RequestMethod.POST)
		public String AddProjectSlideData(HttpServletRequest req , RedirectAttributes redir, HttpSession ses , @RequestParam(name="Attachment1", required = false)MultipartFile imageattch , @RequestParam(name="Attachment2", required = false)MultipartFile pdfattach, @RequestParam(name="Attachment3", required = false)MultipartFile VideoAttach)throws Exception
		{
			String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside AddProjectSlides.htm.htm "+UserId);	
			try {
				if(InputValidator.isContainsHTMLTags(req.getParameter("Brief"))) {
					redir.addAttribute("ProjectId", req.getParameter("ProjectId"));
					return  redirectWithError(redir,"PfmsProjectSlides.htm","Brief should not contain HTML elements !");
				}
				String LabCode = (String) ses.getAttribute("labcode");
				String projectid = req.getParameter("projectid");
				String status = req.getParameter("Status");
				String brief = req.getParameter("Brief");
				String slide = req.getParameter("silde");
				String wayforward = req.getParameter("wayForward");
				ProjectSlideDto slidedata = new ProjectSlideDto();
					slidedata.setProjectId(Long.parseLong(projectid));
					slidedata.setStatus(status);
					slidedata.setBrief(brief);
					slidedata.setSlide(slide);
					slidedata.setLabcode(LabCode);
					slidedata.setImageAttach(imageattch);
					slidedata.setPdfAttach(pdfattach);
					slidedata.setVideo(VideoAttach);
					slidedata.setWayForward(wayforward);
					slidedata.setIsActive(1);
					slidedata.setCreatedBy(UserId);
					long count  = service.AddProjectSlideData(slidedata );
					if(count>0)
	    			{
	    				redir.addAttribute("result", "Project Slide data Added Successfully");
	    			}else{
	    				redir.addAttribute("resultfail", "Project Slide data Adding Failed");	
	    			}
					redir.addFlashAttribute("projectid", projectid);	
				return "redirect:/PfmsProjectSlides.htm";
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside AddProjectSlides.htm.htm "+UserId, e);
				return "static/error";
			}
		}
		
		private boolean isValidImage(MultipartFile file) {
		    if (file == null || file.isEmpty()) return true;

		    String contentType = file.getContentType();
		    String extension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();

		    return (extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") )
		            && (contentType.equalsIgnoreCase("image/jpeg")
		                || contentType.equalsIgnoreCase("image/png")
		               );
		}

		private boolean isValidPdfOrExcel(MultipartFile file) {
		    if (file == null || file.isEmpty()) return true;

		    String contentType = file.getContentType();
		    String extension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();

		    return (extension.equals("pdf") || extension.equals("xls") || extension.equals("xlsx"))
		            && (contentType.equalsIgnoreCase("application/pdf")
		                || contentType.equalsIgnoreCase("application/vnd.ms-excel")
		                || contentType.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		}

		private boolean isValidVideo(MultipartFile file) {
		    if (file == null || file.isEmpty()) return true;

		    String contentType = file.getContentType();
		    String extension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();

		    return (extension.equals("mp4") || extension.equals("avi") || extension.equals("mkv"))
		            && contentType.startsWith("video/");
		}
		
		@RequestMapping(value="EditProjectSlides.htm" , method = RequestMethod.POST)
		public String EditProjectSlideData(HttpServletRequest req , RedirectAttributes redir , HttpSession ses , @RequestParam("Attachment1")MultipartFile imageattch , @RequestParam("Attachment2")MultipartFile pdfattach, @RequestParam("Attachment3")MultipartFile VideoFile)throws Exception
		{
			String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside EditProjectSlides.htm.htm "+UserId);	
			try {
				String projectid = (String)req.getParameter("ProjectId");
				if(InputValidator.isContainsHTMLTags(req.getParameter("Brief"))) {
					redir.addAttribute("projectid", projectid);
					return  redirectWithError(redir,"PfmsProjectSlides.htm","Brief should not contain HTML elements !");
				}
				
				
				String LabCode = (String) ses.getAttribute("labcode");
				String ProjectslideId = req.getParameter("ProjectslideId");
				String status = req.getParameter("Status");
				String brief = req.getParameter("Brief");
				String slide = req.getParameter("silde");
				String wayforward = req.getParameter("wayForward");
				

				  // File validation
		        if (!imageattch.isEmpty() && !isValidImage(imageattch)) {
		        	redir.addFlashAttribute("projectid", projectid);	
		            return redirectWithError(redir, "PfmsProjectSlides.htm", "Attachment1 must be an image file (jpg, png, jpeg).");
		        }

		        if (!pdfattach.isEmpty() && !isValidPdfOrExcel(pdfattach)) {
		        	redir.addFlashAttribute("projectid", projectid);	
		            return redirectWithError(redir, "PfmsProjectSlides.htm", "Attachment2 must be a PDF or Excel file.");
		        }

		        if (!VideoFile.isEmpty() && !isValidVideo(VideoFile)) {
		        	redir.addFlashAttribute("projectid", projectid);	
		            return redirectWithError(redir, "PfmsProjectSlides.htm", "Attachment3 must be a video file (mp4, avi, mkv).");
		        }
				
				ProjectSlideDto slidedata = new ProjectSlideDto();
				slidedata.setSlideId(Long.parseLong(ProjectslideId));
				slidedata.setStatus(status);
				slidedata.setBrief(brief);
				slidedata.setSlide(slide);
				slidedata.setLabcode(LabCode);
				slidedata.setImageAttach(imageattch);
				slidedata.setPdfAttach(pdfattach);
				slidedata.setVideo(VideoFile);
				slidedata.setModifiedBy(UserId);
				slidedata.setWayForward(wayforward);
				
					long count  = service.EditProjectSlideData(slidedata);
					if(count>0)
	    			{
	    				redir.addAttribute("result", "Project Slide data Updated Successfully");
	    			}else{
	    				redir.addAttribute("resultfail", "Project Slide data Update Failed");	
	    			}
				redir.addFlashAttribute("projectid", projectid);	
					return "redirect:/PfmsProjectSlides.htm";
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside EditProjectSlides.htm.htm "+UserId, e);
				return "static/error";
			}
		}
		
		 @RequestMapping(value = "SlideAttachDownload.htm", method = RequestMethod.GET)
		 public void SlideAttachDownload(HttpServletRequest	 req, HttpSession ses, HttpServletResponse res) throws Exception 
		 {	 
			     String UserId = (String) ses.getAttribute("Username");
			     String LabCode = (String) ses.getAttribute("labcode");
				logger.info(new Date() +"Inside SlideAttachDownload.htm "+UserId);		
				try { 
			 
						res.setContentType("Application/octet-stream");	
						ProjectSlides attach=service.SlideAttachmentDownload(req.getParameter("slideId" ));
						
						File my_file=null;
					    Path imagePath = Paths.get(ApplicationFilesDrive, LabCode, "ProjectSlide");
						my_file = new File(imagePath+File.separator+attach.getImageName()); 
				        res.setHeader("Content-disposition","attachment; filename="+attach.getImageName().toString());
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
						logger.error(new Date() +" Inside SlideAttachDownload.htm "+UserId, e);
				}
		 }
		 @RequestMapping(value = "SlidePdfAttachDownload.htm", method = RequestMethod.GET)
		 public void SlidePdfAttachDownload(HttpServletRequest	 req, HttpSession ses, HttpServletResponse res) throws Exception 
		 {	 
			 String UserId = (String) ses.getAttribute("Username");
			 String LabCode = (String) ses.getAttribute("labcode");
				logger.info(new Date() +"Inside SlidepdfAttachDownload.htm "+UserId);		
				try { 
			 
						res.setContentType("Application/octet-stream");	
						ProjectSlides attach=service.SlideAttachmentDownload(req.getParameter("slideId" ));
						
						File my_file=null;
						Path pdfPath = Paths.get(ApplicationFilesDrive, LabCode, "ProjectSlide");
						my_file = new File(pdfPath+File.separator+attach.getAttachmentName()); 
				        res.setHeader("Content-disposition","attachment; filename="+attach.getAttachmentName().toString()); 
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
						logger.error(new Date() +" Inside SlidepdfAttachDownload.htm "+UserId, e);
				}
		 }


		 @RequestMapping(value = "SlideVideoAttachDownload.htm", method = RequestMethod.GET)
		 public void SlideVideoAttachDownload(HttpServletRequest	 req, HttpSession ses, HttpServletResponse res) throws Exception 
		 {	 
			 String UserId = (String) ses.getAttribute("Username");
			 String LabCode = (String) ses.getAttribute("labcode");
			 logger.info(new Date() +"Inside SlidepdfAttachDownload.htm "+UserId);		
			 try { 
				 
				 res.setContentType("Application/octet-stream");	
				 ProjectSlides attach=service.SlideAttachmentDownload(req.getParameter("slideId" ));
				 
				 File my_file=null;
				 Path pdfPath = Paths.get(ApplicationFilesDrive, LabCode, "ProjectSlide");
				 my_file = new File(pdfPath+File.separator+attach.getVideoName()); 
				 res.setHeader("Content-disposition","attachment; filename="+attach.getVideoName().toString()); 
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
				 logger.error(new Date() +" Inside SlidepdfAttachDownload.htm "+UserId, e);
			 }
		 }
		 @RequestMapping(value = "SlidePdfOpenAttachDownload.htm", method = RequestMethod.GET)
		 public void SlidePdfOpenAttachDownload(HttpServletRequest	 req, HttpSession ses, HttpServletResponse res) throws Exception 
		 {
			 String UserId = (String) ses.getAttribute("Username");
			 String LabCode = (String) ses.getAttribute("labcode");
				logger.info(new Date() +"Inside SlidepdfAttachDownload.htm "+UserId);		
				try { 
					res.setContentType("application/pdf");	
					ProjectSlides attach=service.SlideAttachmentDownload(req.getParameter("slideId" ));
					
					File my_file=null;
					Path openPath = Paths.get(ApplicationFilesDrive, LabCode, "ProjectSlide");
					my_file = new File(openPath+File.separator+attach.getAttachmentName()); 
			        res.setContentType("application/pdf");
			        String filename = attach.getAttachmentName() != null ? attach.getAttachmentName().toString() : "name.pdf";
//					res.setHeader("Content-disposition", "inline; filename="+attach.getAttachmentName()!=null?attach.getAttachmentName().toString():"name"+".pdf"); 
			        res.setHeader("Content-disposition", "inline; filename=\"" + filename + "\"");			        
			        OutputStream out = res.getOutputStream();			        FileInputStream in = new FileInputStream(my_file);
			        byte[] buffer = new byte[4096];
			        int length;
			        while ((length = in.read(buffer)) > 0){
			           out.write(buffer, 0, length);
			        }
			        in.close();
			        out.close();
	 
				}
				catch (Exception e) {
						e.printStackTrace();
						logger.error(new Date() +" Inside SlidepdfAttachDownload.htm "+UserId, e);
				}
		 }

		 @RequestMapping(value = "SlideVideoOpenAttachDownload.htm", method = RequestMethod.GET)
		 public void SlideVideoOpenAttachDownload(HttpServletRequest	 req, HttpSession ses, HttpServletResponse res) throws Exception 
		 {
			 String UserId = (String) ses.getAttribute("Username");
			 String LabCode = (String) ses.getAttribute("labcode");
			 logger.info(new Date() +"Inside SlidepdfAttachDownload.htm "+UserId);		
			 try { 
				 res.setContentType("application/pdf");	
				 ProjectSlides attach=service.SlideAttachmentDownload(req.getParameter("slideId" ));
				 
				 File my_file=null;
				 Path videoPath = Paths.get(ApplicationFilesDrive, LabCode, "ProjectSlide");
				 my_file = new File(videoPath+File.separator+attach.getVideoName()); 
				 res.setContentType("video/mp4");
				 String filename = attach.getVideoName() != null ? attach.getVideoName().toString() : "name.pdf";
//				 res.setHeader("Content-disposition", "inline; filename="+attach.getAttachmentName()!=null?attach.getAttachmentName().toString():"name"+".pdf"); 
				 res.setHeader("Content-disposition", "inline; filename=\"" + filename + "\"");			        
				 OutputStream out = res.getOutputStream();
				 FileInputStream in = new FileInputStream(my_file);
				 byte[] buffer = new byte[4096];
				 int length;
				 while ((length = in.read(buffer)) > 0){
					 out.write(buffer, 0, length);
				 }
				 in.close();
				 out.close();
				 
			 }
			 catch (Exception e) {
				 e.printStackTrace();
				 logger.error(new Date() +" Inside SlidepdfAttachDownload.htm "+UserId, e);
			 }
		 }
		 
		 @RequestMapping(value = "GetSlidedata.htm" , method = RequestMethod.GET)
		 public @ResponseBody String GetSlidedata(HttpServletRequest req , HttpSession ses) throws Exception
		 {
			 String UserId = (String) ses.getAttribute("Username");
				logger.info(new Date() +"Inside GetSlidedata.htm "+UserId);		
				ProjectSlides attach=null;
				try {
					 attach=service.SlideAttachmentDownload(req.getParameter("slideid"));
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside SlideAttachDownload.htm "+UserId, e);	
				}
				Gson json = new Gson();
				return json.toJson(attach);
		 }
		 
		 @RequestMapping(value = "SlideFreezeSubmit.htm" , method = RequestMethod.POST)
		 public String ProjectSlideFreeze(HttpServletRequest req , RedirectAttributes redir, HttpServletResponse res , HttpSession ses)throws Exception
		 {
			 String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside SlideFreezeSubmit.htm "+UserId);
			 try {
				 if(InputValidator.isContainsHTMLTags(req.getParameter("review"))) {
						return  redirectWithError(redir,"MainDashBoard.htm","Html tags are not allowed !");
					}
				 String LabCode = (String) ses.getAttribute("labcode");
				 
				 Long EmpId = (Long) ses.getAttribute("EmpId");
				 String review = req.getParameter("review");
				 String reviewdate = req.getParameter("reviewdate");
				 String projectid = req.getParameter("ProjectId");
				 ProjectSlideFreeze freeze = new ProjectSlideFreeze();
					 freeze.setReviewby(review);
					 freeze.setReviewDate(new java.sql.Date(sdf.parse(reviewdate).getTime()));
					 freeze.setProjectId(Long.parseLong(projectid));
					 freeze.setEmpId(EmpId);
					 
					    Object[] projectdata = (Object[])service.GetProjectdata(projectid); // all columns
						Object[] projectslidedata = service.GetProjectSildedata(projectid);
						req.setAttribute("filepath", ApplicationFilesDrive);
						req.setAttribute("projectslidedata", projectslidedata);
						req.setAttribute("projectdata", projectdata);
						req.setAttribute("review", review);
						req.setAttribute("reviewdate", reviewdate);
						req.setAttribute("Drdologo", LogoUtil.getDRDOLogoAsBase64String());
						req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(LabCode));
					 
					    String filename="ProjectProposal";	
				    	String path=req.getServletContext().getRealPath("/view/temp");
						req.setAttribute("path",path);
						CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
						req.getRequestDispatcher("/view/print/ProjectSlideFreeze.jsp").forward(req, customResponse);
						String html = customResponse.getOutput();

						ConverterProperties converterProperties = new ConverterProperties();
				    	FontProvider dfp = new DefaultFontProvider(true, true, true);
				    	converterProperties.setFontProvider(dfp);
				    	
						HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"),converterProperties);

				        File f=new File(path+"/"+filename+".pdf");

						FileInputStream in = new FileInputStream(f);
						Timestamp instant = Timestamp.from(Instant.now());
						String timestampstr = instant.toString().replace(" ", "").replace(":", "").replace("-", "").replace(".", "");
				       String filesname="SlideFreeze"+timestampstr+".pdf";
//						String Path= ApplicationFilesDrive+LabCode+"\\FreezedProjectSlide\\";
						
				        Path uploadPath = Paths.get(ApplicationFilesDrive,LabCode,"FreezedProjectSlide");
				        Path uploadPath1 = Paths.get(LabCode,"FreezedProjectSlide");
				        if (!Files.exists(uploadPath)) {
				            Files.createDirectories(uploadPath);
				        }
						 Path filePath = uploadPath.resolve(filesname);
				         Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
						in.close();
						Path pathOfFile2= Paths.get( path+File.separator+filename+".pdf"); 
				        Files.delete(pathOfFile2);
				        
				        freeze.setAttachName(filesname);
				        freeze.setPath(uploadPath1.toString());
				        freeze.setCreatedBy(UserId);
				        long count  = service.AddFreezeData(freeze);
						if(count>0)
		    			{
		    				redir.addAttribute("result", "Project Slide Freezed Successfully");
		    			}else{
		    				redir.addAttribute("resultfail", "Project Slide Freezed Failed");	
		    			}
				        redir.addFlashAttribute("projectid", projectid);
			}catch (Exception e){
				e.printStackTrace();
				logger.error(new Date() +" Inside SlideFreezeSubmit.htm "+UserId, e);
			}
				
				return "redirect:/PfmsProjectSlides.htm";
		 }
		 
		 @RequestMapping(value = "ProjectSlide.htm" , method = {RequestMethod.GET, RequestMethod.POST})
		 public String ProjectSlide(Model model, HttpServletRequest req , RedirectAttributes redir, HttpServletResponse res , HttpSession ses)throws Exception
		 {
			 String UserId = (String) ses.getAttribute("Username");
				String LabCode = (String) ses.getAttribute("labcode");
				String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		    	String Logintype= (String)ses.getAttribute("LoginType");
			logger.info(new Date() +"Inside ProjectSlide.htm "+UserId);	
			 try {
				 String projectid= req.getParameter("projectid");
					
					List<Object[]> projectslist  =service.LoginProjectDetailsList(EmpId, Logintype, LabCode);
					
					if(projectslist.size()==0) 
					{
						redir.addAttribute("resultfail","Project is Not Assigned!" );
						return "redirect:/MainDashBoard.htm";
					}
					if(projectid==null) 
			    	{
			    		Map md = model.asMap();	    	
			    		projectid = (String) md.get("projectid");
			    	}
					if(projectid==null) {
						projectid  = projectslist.get(0)[0].toString();
					}
					req.setAttribute("projectid",projectid);
					req.setAttribute("projectslist", projectslist);
					req.setAttribute("ProjectSlideFreezedList", service.getProjectSlideList(projectid));
					
			} catch(Exception e){
				e.printStackTrace();
				logger.error(new Date() +" Inside ProjectSlide.htm "+UserId, e);
			}
			 return "print/ProjectSlideList";
		 }
		 
		 @RequestMapping(value = "freezedSlideAttachDownload.htm", method = RequestMethod.GET)
		 public void ProjectSlideAttachDownload(HttpServletRequest	 req, HttpSession ses, HttpServletResponse res) throws Exception 
		 {	 
			 String UserId = (String) ses.getAttribute("Username");
			 String LabCode = (String) ses.getAttribute("labcode");
				logger.info(new Date() +"Inside ProjectSlideAttachDownload.htm "+UserId);		
				try { 
			 
						res.setContentType("application/pdf");
						ProjectSlideFreeze attach=service.FreezedSlideAttachmentDownload(req.getParameter("freezedId" ));
						
						File my_file=null;
						Path freezePath = Paths.get(ApplicationFilesDrive, LabCode, "ProjectSlide");
						my_file = new File(freezePath+File.separator+attach.getAttachName()); 
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
						logger.error(new Date() +" Inside ProjectSlideAttachDownload.htm "+UserId, e);
				}
		 }
		 


		 @RequestMapping(value = "GetAllProjectSlide.htm" , method = RequestMethod.GET)
		 public String GetAllProjectSlide(HttpServletRequest req , RedirectAttributes redir, HttpServletResponse res , HttpSession ses)throws Exception
			{
			 long startTime = System.currentTimeMillis();
				String UserId = (String) ses.getAttribute("Username");

			logger.info(new Date() + "Inside GetAllProjectSlide.htm " + UserId);

			String[] IdsInput = req.getParameterValues("projectlist");

			List<Object[]> getAllProjectSlidedata = new ArrayList<>();

			List<Object[]> getAllProjectdata = new ArrayList<>();

				List<Object[]> getAllProjectSlidesdata = new ArrayList<>();

				if (IdsInput != null && IdsInput.length > 0)

					for (String id : IdsInput) {
									
						List<Object[]> getoneProjectSlidedata = service.GetAllProjectSildedata(id);  // freezing data
						Object[] projectslidedata = (Object[]) service.GetProjectSildedata(id);  //[7] id project id
						getAllProjectSlidesdata.add(projectslidedata);
						Object[] projectdata = (Object[]) service.GetProjectdata(id); //[0] is project id ------ all vals
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
				
			
				if (IdsInput != null && IdsInput.length == 0) {
					redir.addAttribute("resultfail", "could not open empty slideshow");
					return "redirect:/MainDashBoard.htm";
				}
				try {

					if (getAllProjectdata.size() > 1) {
					Collections.sort(getAllProjectdata, dateComparator);
						Collections.sort(getAllProjectSlidedata, dateComparator);
				}
					Collections.reverse(getAllProjectdata);

					String labcode = ses.getAttribute("labcode").toString();

					req.setAttribute("getAllProjectdata", getAllProjectdata);

				req.setAttribute("labInfo", service.LabDetailes(labcode));

					req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(labcode));

					req.setAttribute("Drdologo", LogoUtil.getDRDOLogoAsBase64String());

					req.setAttribute("filepath", ApplicationFilesDrive);

					req.setAttribute("getAllProjectSlidedata", getAllProjectSlidedata);
					req.setAttribute("getAllProjectSlidesdata", getAllProjectSlidesdata);

				} catch (Exception e) {

					e.printStackTrace();

					//logger.error(new Date() + " Inside GetAllProjectSlide.htm " + UserId, e);

				}
				 long end = System.currentTimeMillis();
				 System.out.println("Time Needed "+(end-startTime));
				
				return "print/ProjectSlideFreezedViewAll";
			}
	
		 
//		 @RequestMapping(value = "FreezedSlidesInpdf.htm")
//		 public void FreezedSlidesInpdf(HttpServletRequest req , RedirectAttributes redir, HttpServletResponse res , HttpSession ses)throws Exception
//		 {
//			 String UserId = (String) ses.getAttribute("Username");
//			logger.info(new Date() +"Inside GetAllProjectSlide.htm "+UserId);	
//			 try {
//					List<Object[]> getAllProjectSlidedata= service.GetAllProjectSildedata("All");
//					 String path=req.getServletContext().getRealPath("/view/temp");
//					
//					 PDFMergerUtility utility = new PDFMergerUtility();
//					 utility.setDestinationFileName(path +File.separator+ "merged.pdf");
//					 for(Object[] obj : getAllProjectSlidedata) 
//					 {
//						File file = new File(ApplicationFilesDrive+ obj[1].toString()+obj[2].toString());			 
//				        utility.addSource(file);
//					 }
//					 utility.mergeDocuments();
//					
//					
//				        res.setContentType("application/pdf");
//				        res.setHeader("Content-disposition","attachment; filename=FreezedProjectSlides.pdf");
//				        File f=new File(path +File.separator+ "merged.pdf");
//
//					        OutputStream out = res.getOutputStream();
//							FileInputStream in = new FileInputStream(f);
//							byte[] buffer = new byte[4096];
//							int length;
//							while ((length = in.read(buffer)) > 0) {
//								out.write(buffer, 0, length);
//							}
//							in.close();
//							out.close();
//					       
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		 } 
		 
		 @RequestMapping(value = "BriefingPointsUpdate.htm" ,method = RequestMethod.GET)
			public @ResponseBody String BriefingPointsUpdate(HttpServletRequest req)throws Exception
			{
				
				try {
					String point = req.getParameter("point");
					String activityid=req.getParameter("ActivityId");
					String status=req.getParameter("status");
					int count =service.BriefingPointsUpdate(point,activityid,status);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Gson json = new Gson();
				return null;
			}
		 
		 @RequestMapping(value = "BriefingForward.htm", method = {RequestMethod.GET,RequestMethod.POST})
		 public String BriefingForward(HttpServletRequest req , RedirectAttributes redir, HttpServletResponse res , HttpSession ses)throws Exception
		 {
			 String UserId = (String) ses.getAttribute("Username");
			 String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
				String projectid=req.getParameter("projectid");
		    	String committeecode=req.getParameter("committeecode");
		    	String DHId=req.getParameter("DHId");
		    	String GHId=req.getParameter("GHId");
		    	String DOId=req.getParameter("DOId");
		    	String DirectorId=req.getParameter("DirectorId");
		    	String meetingIdNoFWd=req.getParameter("meetingIdNoFWd");
		    	List<String> frwStatus  = Arrays.asList("INI","REV","RDH","RGH","RPD","RBD");
		    	//String option=req.getParameter("BriefingFwd");
			 long result1=0;
				logger.info(new Date() +"Inside BriefingForward.htm "+UserId);	
				 try {
			String scheduleId = req.getParameter("sheduleIdFwd");
			String briefingStatus = req.getParameter("briefingStatus");
			
				PfmsBriefingTransaction briefingTransaction = new PfmsBriefingTransaction();
				briefingTransaction.setScheduleId(Long.parseLong(scheduleId));
				briefingTransaction.setEmpId(Long.parseLong(EmpId));
				briefingTransaction.setActionBy(UserId);
				PfmsBriefingFwdDto briefingDto = new PfmsBriefingFwdDto();
				briefingDto.setBriefingTranc(briefingTransaction);
				briefingDto.setBriefingStatus(briefingStatus);
				briefingDto.setEmpId(EmpId);
				briefingDto.setProjectId(projectid);
				briefingDto.setScheduleId(scheduleId);
				briefingDto.setDhId(DHId);
				briefingDto.setGhId(GHId);
				briefingDto.setDoId(DOId);
				briefingDto.setDirectorId(DirectorId);
				briefingDto.setMeetingID(meetingIdNoFWd);
				
				 result1=service.insertBriefingTrans(briefingDto);
			
				 if(frwStatus.contains(briefingStatus)) {
			if(result1>0)
			{
				
				redir.addAttribute("result", "Briefing Paper Forwarded Successfully");
			}else{
				redir.addAttribute("resultfail", "Briefing Paper Forward unSuccessful");	
			}
		 }else if(briefingStatus.equalsIgnoreCase("REP")) {
		 if(result1>0)
			{
			 	redir.addFlashAttribute("pendingClick","N");			
				redir.addAttribute("result", "Briefing Paper Approved Successfully");
			}else{
				redir.addAttribute("resultfail", "Briefing Paper Approved unSuccessful");	
			}
			 }
		 else {
			 if(result1>0)
				{
				 	redir.addFlashAttribute("pendingClick","N");			
					redir.addAttribute("result", "Briefing Paper Recommended Successfully");
				}else{
					redir.addAttribute("resultfail", "Briefing Paper Recommended unSuccessful");	
				}
		 }
						       
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside BriefingForward.htm "+UserId, e);
				}
				 	redir.addFlashAttribute("projectid",projectid);
			    	redir.addFlashAttribute("committeecode",committeecode);
			    	
			    	return "redirect:/FroozenBriefingList.htm";
		 }
		 
		 @RequestMapping(value = "BriefingActionReturn.htm", method = {RequestMethod.GET,RequestMethod.POST})
		 public String BriefingActionReturn(HttpServletRequest req , RedirectAttributes redir, HttpServletResponse res , HttpSession ses)throws Exception
		 {
			 String UserId = (String) ses.getAttribute("Username");
			 String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
				String projectid=req.getParameter("projectidRtn");
		    	String committeecode=req.getParameter("committeecode");
		    	String commiteeName=req.getParameter("commiteeName");
		    	String userId=req.getParameter("userId");
		    	String replyMsg=req.getParameter("replyMsg");
		    	String meetingIdRtn=req.getParameter("meetingIdRtn");
			 long result1=0;
				logger.info(new Date() +"Inside BriefingForward.htm "+UserId);	
				 try {
			String scheduleId = req.getParameter("sheduleRtn");
			String briefingStatus = req.getParameter("briefingStatus");
				PfmsBriefingTransaction briefingTransaction = new PfmsBriefingTransaction();
				briefingTransaction.setScheduleId(Long.parseLong(scheduleId));
				briefingTransaction.setEmpId(Long.parseLong(EmpId));
				briefingTransaction.setActionBy(UserId);
				if(replyMsg!=null) {
				 	redir.addFlashAttribute("pendingClick","N");
					briefingTransaction.setRemarks(replyMsg.trim());
				}
				PfmsBriefingFwdDto briefingDto = new PfmsBriefingFwdDto();
				
				briefingDto.setBriefingTranc(briefingTransaction);
				briefingDto.setBriefingStatus(briefingStatus);
				briefingDto.setEmpId(EmpId);
				briefingDto.setProjectId(projectid);
				briefingDto.setUserId(userId);
				briefingDto.setScheduleId(scheduleId);
				briefingDto.setMeetingID(meetingIdRtn);
				briefingDto.setCommitteecode(commiteeName);

				 result1=service.briefingReturnAction(briefingDto);
			
				 if( !UserId.equalsIgnoreCase(EmpId)) {
			if(result1>0)
			{
				
				redir.addAttribute("result", "Briefing Paper Returned Successfully");
			}else{
				redir.addAttribute("resultfail", "Briefing Paper Returned unSuccessful");	
			}
		 }
	
						       
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(new Date() +" Inside BriefingForward.htm "+UserId, e);
				}
				 	redir.addFlashAttribute("projectid",projectid);
			    	redir.addFlashAttribute("committeecode",committeecode);
			    	redir.addFlashAttribute("commiteeName",commiteeName);
			   
			    	return "redirect:/FroozenBriefingList.htm";
		 }
		 
		 
		 
			@RequestMapping(value = "getBriefingData.htm", method = RequestMethod.GET)
			public @ResponseBody String getBriefingData(HttpServletRequest req, HttpServletResponse response, HttpSession ses) throws Exception 
			{
				String sheduleId = req.getParameter("sheduleId");
				Object[] getBriefingData =null;
				try {
					getBriefingData = service.getBriefingData(sheduleId);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Gson json = new Gson();
				return json.toJson(getBriefingData);
			}
			
			@RequestMapping(value="getBriefingRemarks.htm",method=RequestMethod.GET)
			public @ResponseBody String getBriefingRemarks(HttpSession ses, HttpServletRequest req) throws Exception {
				
				 Gson json = new Gson();
			   	 String UserId=(String)ses.getAttribute("Username");
			   	 
			   	logger.info(new Date() +"Inside getBriefingRemarks.htm"+UserId);
			   	
			  List<Object[]> rfaRemarkData=null;
			   	try {
			   		String sheduleId=req.getParameter("sheduleId");
			   		
			   		rfaRemarkData = service.getBriefingRemarks(sheduleId);
			   	}
			   	catch (Exception e) {
			   		e.printStackTrace();
			   		logger.error(new Date() +"Inside getBriefingRemarks.htm"+UserId ,e);
			   	}
			   	
			return json.toJson(rfaRemarkData);
			}

			@RequestMapping(value = "SlidefileShow.htm", method = RequestMethod.GET)

			public void SlideShow(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception

			{

				String UserId = (String) ses.getAttribute("Username");

				logger.info(new Date() + "Inside SlidefileShow.htm " + UserId);

				try {

					List<Object[]> getFreezingData = service.GetFreezingHistory(req.getParameter("projectid"));

					String freezingid = req.getParameter("freezeid").toString();

					int j = 0;

					for (int i = 0; i < getFreezingData.size(); i++)

					{
						j = i;

						if (getFreezingData.get(i)[5].toString().equals(freezingid))

							break;

					}

					String filename = (java.lang.String) getFreezingData.get(j)[4];

					res.setContentType("Application/octet-stream");

					File my_file = null;

					my_file = new File(ApplicationFilesDrive + getFreezingData.get(j)[3] + filename);

					res.setHeader("Content-disposition", "inline; filename=" + filename);

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

					logger.error(new Date() + " Inside SlideAttachDownload.htm " + UserId, e);

				}

			}
			
			
			 public String PrintProjectsOutline(Map<String,List<Object[]>> details, HttpServletRequest req , RedirectAttributes redir, HttpServletResponse res , HttpSession ses)throws Exception
			 {


				 String UserId = (String) ses.getAttribute("Username");
				logger.info(new Date() +"Inside SlideFreezeSubmit.htm "+UserId);
				String pathForPdf="";
				 try {
					 String LabCode = (String) ses.getAttribute("labcode");
					 req.setAttribute("getAllProjectdata", details.get("dataForOutline"));
					 req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(LabCode));
				    String filename="ProjectSlideCover";	
				    
			    	String path=req.getServletContext().getRealPath("/view/temp");

					req.setAttribute("path",path);

					CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);

					req.getRequestDispatcher("/view/print/ProjectOutlinePrint.jsp").forward(req, customResponse);

					String html = customResponse.getOutput();
					ConverterProperties converterProperties = new ConverterProperties();
			    	FontProvider dfp = new DefaultFontProvider(true, true, true);
			    	converterProperties.setFontProvider(dfp);

					HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"),converterProperties);
			        File f=new File(path+"/"+filename+".pdf");



					FileInputStream in = new FileInputStream(f);
					Timestamp instant = Timestamp.from(Instant.now());
					String timestampstr = instant.toString().replace(" ", "").replace(":", "").replace("-", "").replace(".", "");
			       String filesname="ProjectOutline.pdf";
					String Path= ApplicationFilesDrive+LabCode+"\\FreezedProjectSlide\\";

					

			        Path uploadPath = Paths.get(Path);
			        if (!Files.exists(uploadPath)) {
			            Files.createDirectories(uploadPath);
			        }
					 Path filePath = uploadPath.resolve(filesname);
			         Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
					in.close();
					Path pathOfFile2= Paths.get( path+File.separator+"ProjectOutline"+".pdf"); 
					if(Files.exists(pathOfFile2))
					{System.out.println("deleting the path now ");Files.delete(pathOfFile2);}

			        

			        pathForPdf = filePath.toString();

				}catch (Exception e){
					e.printStackTrace();
					logger.error(new Date() +" Inside PrintProjectsOutline.htm "+UserId, e);
				}
					return pathForPdf;

			 
			 }
			 public String PrintCoverSlide(Map<String,List<Object[]>> details, HttpServletRequest req , RedirectAttributes redir, HttpServletResponse res , HttpSession ses)throws Exception

			 {

				 String UserId = (String) ses.getAttribute("Username");
				logger.info(new Date() +"Inside SlideFreezeSubmit.htm "+UserId);
				String pathForPdf="";
				 try {
					 String LabCode = (String) ses.getAttribute("labcode");
					 req.setAttribute("labInfo", service.LabDetailes(LabCode));
					 req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(LabCode));
					 req.setAttribute("getAllProjectdata", details.get("getAllProjectdata"));
					 req.setAttribute("dataForOutline", details.get("dataForOutline"));
				    String filename="ProjectSlideCover";
			    	String path=req.getServletContext().getRealPath("/view/temp");
			    	
					req.setAttribute("path",path);

					CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);

					req.getRequestDispatcher("/view/print/CoverSlide.jsp").forward(req, customResponse);

					String html = customResponse.getOutput();
					ConverterProperties converterProperties = new ConverterProperties();
			    	FontProvider dfp = new DefaultFontProvider(true, true, true);
			    	converterProperties.setFontProvider(dfp);

					HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"),converterProperties);
			        File f=new File(path+"/"+filename+".pdf");



					FileInputStream in = new FileInputStream(f);
					Timestamp instant = Timestamp.from(Instant.now());
					String timestampstr = instant.toString().replace(" ", "").replace(":", "").replace("-", "").replace(".", "");
			       String filesname="coverslide.pdf";
					String Path= ApplicationFilesDrive+LabCode+"\\FreezedProjectSlide\\";

					

			        Path uploadPath = Paths.get(Path);
			        if (!Files.exists(uploadPath)) {
			            Files.createDirectories(uploadPath);
			        }
					 Path filePath = uploadPath.resolve(filesname);
			         Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
					in.close();
					Path pathOfFile2= Paths.get( path+File.separator+"coverslide"+".pdf"); 
					if(Files.exists(pathOfFile2))
					{System.out.println("deleting the path now ");Files.delete(pathOfFile2);}

			        

			        pathForPdf = filePath.toString();

				}catch (Exception e){
					e.printStackTrace();
					logger.error(new Date() +" PrintCoverSlide.htm "+UserId, e);
				}
					return pathForPdf;

			 }
//			 @RequestMapping(value = "DownloadSelectedSlides.htm/{ProjectIds}", method = RequestMethod.GET)
//			 public String SelectedFreezedSlidesInpdf(@PathVariable String[] ProjectIds,HttpServletRequest req , RedirectAttributes redir, HttpServletResponse res , HttpSession ses)throws Exception
//			{
//				String UserId = (String) ses.getAttribute("Username");
//				String pathtopdf = "";
//		    	 String LabCode= (String) ses.getAttribute("labcode");
//				String Path= ApplicationFilesDrive+LabCode+"\\FreezedProjectSlide\\";
//				
//		        Path uploadPath = Paths.get(Path);
//		        if (!Files.exists(uploadPath)) {
//		        	System.out.println("creating path for pdf, upload thank you slide for full slideshow");
//		            Files.createDirectories(uploadPath);
//		        }
//				logger.info(new Date() + "Inside DownloadSelectedSlides.htm " + UserId);
//				try {
//					List<Object[]> getAllProjectSlidedata = new ArrayList<>();
//					List<Object[]> getAllProjectdata = new ArrayList<>();
//					for (String id : ProjectIds) {
//						List<Object[]> getoneProjectSlidedata = service.GetAllProjectSildedata(id); // freezed
//						if (getoneProjectSlidedata.size() > 0){
//								Object[] projectdata = (Object[]) service.GetProjectdata(id); // all values
//								getAllProjectdata.add(projectdata);
//							for (Object[] objects : getoneProjectSlidedata) {
//								getAllProjectSlidedata.add(objects);
//							}
//						}
//					}
//					List<Object[]> dataForOutline = new ArrayList<>();
//					List<Object[]> FreedDataForCover = new ArrayList<>();
//					for (Object[] objects : getAllProjectdata) {
//						for (Object[] objects2 : getAllProjectSlidedata) {
//							File file = new File(ApplicationFilesDrive + objects2[1].toString() + objects2[2].toString());
//							if (file.exists()) {
//								if(objects2[3].toString().equals(objects[0].toString())) {
//									dataForOutline.add(objects);
//									FreedDataForCover.add(objects2);
//								}
//							}
//						
//							
//						}
//						
//					}
//					
//					
//					
//					Comparator<Object[]> dateComparator = new Comparator<Object[]>() {
//						@Override
//						public int compare(Object[] o1, Object[] o2) {
//							Date date1 = (Date) o1[5];
//							Date date2 = (Date) o2[5];
//							return date1.compareTo(date2);
//						}
//					};
//					
//					if (getAllProjectdata.size() > 1)
//						Collections.sort(getAllProjectdata, dateComparator);
//					//aligning list values with the PROJids of project pdf PROJids
//					
//					
//					getAllProjectSlidedata = FreedDataForCover;
//					
//					Map<String, List<Object[]>> details = new HashMap<>();
//					details.put("getAllProjectdata", FreedDataForCover);
//					details.put("dataForOutline", dataForOutline);
//					String path = req.getServletContext().getRealPath("/view/temp");
//					//static file thank you
//					String pathToThankYou = "";
//					String CoverSlide = "";
//					File coverslideFile = new File(Path + "coverslide.pdf");
//					if(coverslideFile.exists())
//						CoverSlide = Path + "coverslide.pdf";
//					else 
//						CoverSlide = PrintCoverSlide(details, req, redir, res, ses);
//					
//					
//					File projectOutline = new File(Path + "ProjectOutline.pdf");
//					String prjOutlineSlide="";
//					if(projectOutline.exists())prjOutlineSlide=projectOutline.getPath();
//					else prjOutlineSlide=PrintProjectsOutline(details, req, redir, res, ses);
//					
//					PDFMergerUtility utility = new PDFMergerUtility();
//					utility.setDestinationFileName(path + File.separator + "merged.pdf");
//					
//					File filec = new File(CoverSlide);
//					utility.addSource(filec);
//					File file0 = new File(prjOutlineSlide);
//					utility.addSource(file0);
//					
//					boolean flag = false;
////					Collections.reverse(getAllProjectSlidedata);
//					List<Object> mainProjectids =  dataForOutline!=null && dataForOutline.size()>0 ? (dataForOutline.stream().filter(e-> e[21]!=null && e[21].toString().equals("1")).map(objArray -> objArray[0]).collect(Collectors.toList())): new ArrayList<Object>();
//					List<Object> subProjectList =  dataForOutline!=null && dataForOutline.size()>0 ? (dataForOutline.stream().filter(e-> e[21]!=null && e[21].toString().equals("0")).map(objArray -> objArray[0]).collect(Collectors.toList())): new ArrayList<Object>();
//					
//					getAllProjectSlidedata.stream()
//				    .filter(obj -> new File(ApplicationFilesDrive + obj[1].toString() + obj[2].toString()).exists())
//				    .filter(obj -> mainProjectids.contains(obj[3]))
//				    .forEach(obj -> {
//				        File file = new File(ApplicationFilesDrive + obj[1].toString() + obj[2].toString());
//				        try {
//							utility.addSource(file);
//						} catch (FileNotFoundException e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						}
//				    });
//
//					// Process sub project ids
//					getAllProjectSlidedata.stream()
//					    .filter(obj -> new File(ApplicationFilesDrive + obj[1].toString() + obj[2].toString()).exists())
//					    .filter(obj -> subProjectList.contains(obj[3]))
//					    .forEach(obj -> {
//					        File file = new File(ApplicationFilesDrive + obj[1].toString() + obj[2].toString());
//					        try {
//								utility.addSource(file);
//							} catch (FileNotFoundException e1) {
//								// TODO Auto-generated catch block
//								e1.printStackTrace();
//							}
//					    });
//					
//					System.out.println("pathhhh----"+getAllProjectSlidedata.get(0)[1].toString());
//					
//					pathToThankYou =getAllProjectSlidedata.size()>0?getAllProjectSlidedata.get(0)[1].toString():"";
//					
//					pathToThankYou = ApplicationFilesDrive + pathToThankYou + "SlideFreezeTHANKYOU.pdf";
//					File file = new File(pathToThankYou);
//					if (pathToThankYou.equals("") || flag || !file.exists()) {
//						redir.addAttribute("resultfail", "Selected slide has not been Freezed");
//						if(!file.exists())System.out.println("=================  ERROR  =============\n=======================================\n"
//								+ "=======================================\n"
//								+ "==========  ADD THANKYOU SLIDE  ==========\n"
//								+ "=======================================\n=======================================\n"
//								+ "=======================================\n");
//						redir.addAttribute("result", null);
//						return "redirect:/MainDashBoard.htm";
//					}
//					
//					
//					utility.addSource(pathToThankYou);
//					utility.mergeDocuments();
//					res.setContentType("application/pdf");
//					res.setHeader("Content-disposition", "inline; filename=FreezedProjectSlides.pdf");
//					File f = new File(path + File.separator + "merged.pdf");
//					OutputStream out = res.getOutputStream();
//					FileInputStream in = new FileInputStream(f);
//					byte[] buffer = new byte[4096];
//					int length;
//					while ((length = in.read(buffer)) > 0) {
//						out.write(buffer, 0, length);
//					}
//					in.close();
//					out.close();
//					Files.delete(Paths.get(path + File.separator + "merged.pdf"));
//					file0.delete();
//					filec.delete();
//					pathtopdf = path + File.separator + "merged.pdf";
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				return "";
//			} 
			 
			 @RequestMapping(value = "DownloadSelectedSlides.htm/{ProjectIds}", method = RequestMethod.GET)
			 public void SelectedFreezedSlidesInpdf(@PathVariable String[] ProjectIds,HttpServletRequest req , RedirectAttributes redir, HttpServletResponse res , HttpSession ses)throws Exception
			 {
				 String UserId = (String) ses.getAttribute("Username");

					logger.info(new Date() + "Inside GetAllProjectSlide.htm " + UserId);

				

					List<Object[]> getAllProjectSlidedata = new ArrayList<>();

				List<Object[]> getAllProjectdata = new ArrayList<>();

					List<Object[]> getAllProjectSlidesdata = new ArrayList<>();

				if (ProjectIds != null && ProjectIds.length > 0)

						for (String id : ProjectIds) {
										
							List<Object[]> getoneProjectSlidedata = service.GetAllProjectSildedata(id);  // freezing data
							Object[] projectslidedata = (Object[]) service.GetProjectSildedata(id);  //[7] id project id
							getAllProjectSlidesdata.add(projectslidedata);
							Object[] projectdata = (Object[]) service.GetProjectdata(id); //[0] is project id ------ all vals
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

						req.setAttribute("getAllProjectdata", getAllProjectdata);

						req.setAttribute("labInfo", service.LabDetailes(labcode));

						req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(labcode));

						req.setAttribute("Drdologo", LogoUtil.getDRDOLogoAsBase64String());

						req.setAttribute("filepath", ApplicationFilesDrive);

						req.setAttribute("getAllProjectSlidedata", getAllProjectSlidedata);
						req.setAttribute("getAllProjectSlidesdata", getAllProjectSlidesdata);

					} catch (Exception e) {

						e.printStackTrace();

						logger.error(new Date() + " Inside GetAllProjectSlide.htm " + UserId, e);

					}
					
					String filename = "ProjectMasterSlide"+LocalDate.now().toString();
					String path = req.getServletContext().getRealPath("/view/temp");
					req.setAttribute("path", path);
					CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
					req.getRequestDispatcher("/view/print/ProjectSlidePDf.jsp").forward(req, customResponse);
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

			 } 
			 @RequestMapping(value = "saveFavSlides.htm", method = RequestMethod.POST)
			 public @ResponseBody String favoriteSlidesAddSubmit(HttpServletRequest req ,HttpSession ses, RedirectAttributes redir, HttpServletResponse res)throws Exception
			 {
				 try {
					
				
					 FavouriteSlidesModel FSM = new FavouriteSlidesModel();
					 String[] a = req.getParameterValues("projectlist");
					 
					 String Title = req.getParameter("name").toString();
					 String projectlist = "";
					 for (String string : a) {
						projectlist+=string+",";
					};
					projectlist = projectlist.substring(0, projectlist.length()-1);
					FSM.setCreatedBy(ses.getAttribute("Username").toString());
					FSM.setCreatedDate(new Date().toString());
					FSM.setProjectIds(projectlist);
					FSM.setFavouriteSlidesTitle(Title);
					FSM.setIsActive(1);
					Long saved = service.saveFavouriteSlides(FSM);
					redir.addAttribute("result", "Favourite Slides Added Succesfully");
					return "Success";
				} catch (Exception e) {
					// TODO: handle exception
					return "Failure";
				}
			 }
			 
			 @RequestMapping(value = "EditFavSlides.htm", method = RequestMethod.POST)
			 public @ResponseBody String favoriteSlidesEditSubmit(HttpServletRequest req ,HttpSession ses, RedirectAttributes redir, HttpServletResponse res)throws Exception
			 {
				 try {
					 FavouriteSlidesModel FSM = new FavouriteSlidesModel();
					 String Title = req.getParameter("name").toString();
					 String projectlist = "";
					projectlist = req.getParameter("projectlist").toString();
					FSM.setFavouriteSlidesId(Long.parseLong(req.getParameter("FavouritId").toString()));
					FSM.setCreatedDate(new Date().toString());
					FSM.setProjectIds(projectlist);
					FSM.setFavouriteSlidesTitle(Title);
					FSM.setModifiedBy(ses.getAttribute("Username").toString());
					FSM.setModifiedDate(new Date().toString());
					FSM.setIsActive(1);
					Long saved = service.EditFavouriteSlides(FSM);
					redir.addAttribute("result", "Favourite Slides Edited Succesfully");
					return "Success";
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("EditFavSlides.htm error");
					redir.addAttribute("result", "Favourite Slides Edit Failed");
					return "Failed";
				}
			 }
			 
			 @RequestMapping(value = "GetFavSlides.htm", method = RequestMethod.GET)
			 public @ResponseBody String favoriteSlidesGET(HttpServletRequest req ,HttpSession ses, RedirectAttributes redir, HttpServletResponse res)throws Exception
			 {
				 List<Object[]> favsSlideData = service.GETFavouriteSlides();
				Gson json = new Gson();
				return json.toJson(favsSlideData);
			 }
			 
			 
			 @RequestMapping(value="TechImagesEdit.htm",method = {RequestMethod.POST,RequestMethod.GET})
			    public String TechImagesEdit(@RequestParam("FileAttach") MultipartFile file,HttpSession ses,HttpServletRequest req,RedirectAttributes redir)throws Exception
			    {
			   	 String UserId = (String) ses.getAttribute("Username");
		    	 String LabCode= (String) ses.getAttribute("labcode");
					logger.info(new Date() +"Inside TechImagesEdit.htm "+UserId);
		    	try {
		    		String TechImageId=req.getParameter("TechImageId");
		    		// 🔹 Validate file types
			        if (!isValidFileType(file)) {

			        	redir.addFlashAttribute("projectid", req.getParameter("ProjectId"));
						redir.addFlashAttribute("committeeid", req.getParameter("committeeid"));
			        	
			        	
			        	return redirectWithError(redir, "ProjectBriefingPaper.htm",
			                    "Invalid file type. Only  Image files are allowed.");
			        }
		    		int count=service.TechImagesEdit(file,TechImageId,env.getProperty("ApplicationFilesDrive"),req.getUserPrincipal().getName(),LabCode);
		    	    if(count>0) {  
		                redir.addAttribute("result", "Tech Image Update Successfully");
		      		}else {
		      			redir.addAttribute("resultfail", "Tech Image Update Unsuccessful");
		      		}
		    		
		    	}catch(Exception e) {
		    		e.printStackTrace();
		    	}
			    	
		    	redir.addFlashAttribute("projectid", req.getParameter("ProjectId"));
				redir.addFlashAttribute("committeeid", req.getParameter("committeeid"));
		        return "redirect:/ProjectBriefingPaper.htm";
			    }
		
			 
			 @RequestMapping(value = "ProjectCharterDownload.htm", method = { RequestMethod.POST, RequestMethod.GET })
				public void ProjectCharterDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res)
						throws Exception {
					String Logintype = (String) ses.getAttribute("LoginType");
					String UserId = (String) ses.getAttribute("Username");
					String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
					String LabCode = (String) ses.getAttribute("labcode");
					logger.info(new Date() + "Inside ProjectCharterDownload.htm " + UserId);

					try {
			           
						List<Object[]> proList = service.LoginProjectDetailsList(EmpId, Logintype, LabCode);
				         req.setAttribute("proList", proList);
					    String projectid=req.getParameter("projectid");
					    
					    if(projectid==null || projectid.equals("null"))
						{
							projectid=proList.get(0)[0].toString();
						}
					
				  	req.setAttribute("ProjectId", projectid);
				  	req.setAttribute("ProjectEditData1", prservice.ProjectEditData1(projectid));   //this is for the project director
				 	req.setAttribute("ProjectAssignList", prservice.ProjectAssignList(projectid));

				  	Object[] projectattribute = service.ProjectAttributes(projectid);//this is for the project basic details
				  	req.setAttribute("ProjectEditData", projectattribute);
				  	
				  	//data for risk--
				  	List<Object[]> riskdatalist=prservice.ProjectRiskDataList(projectid, LabCode);
				  	req.setAttribute("riskdatalist", riskdatalist);
				  //milestones
					
				  	List<Object[]> main=milservice.MilestoneActivityList(projectid);
					req.setAttribute("MilestoneActivityList",main );

						String filename = "ProjectCharterPdf";
						String path = req.getServletContext().getRealPath("/view/temp");
						req.setAttribute("path", path);
						CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
						req.getRequestDispatcher("/view/print/ProjectCharterPdf.jsp").forward(req, customResponse);
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
						logger.error(new Date() +"ProjectSummaryPrint.htm" + UserId, e);
						e.printStackTrace();
					}

				}
			 
			 
			 @RequestMapping(value = "Projectvideo.htm", method = { RequestMethod.POST, RequestMethod.GET })
			 public String showVideo(HttpServletRequest req, HttpSession ses, HttpServletResponse res)
			 {
				 req.setAttribute("src", req.getAttribute("src"));
				 return "redirect:/ProjectVideo";
			 }
			 
			 @RequestMapping(value = "ProjectSlideImagePath.htm", method = RequestMethod.GET)
			 public @ResponseBody String ProjectSlideImagePath(@RequestParam String projectId,HttpServletResponse res)throws Exception
			 {
				 List<String> iframe=new ArrayList<>();
				 
				 List<String>proList = Arrays.asList(projectId.split(","));
				 List<String[]> getAllProjectSlidesdata = new ArrayList<String[]>();
				
				 for(String projectIds:proList) {
					 Object[] projectslidedata = (Object[]) service.GetProjectSildedata(projectIds);
					 if(projectslidedata!=null && projectslidedata[2]!=null && projectslidedata[3]!=null) {
						 String[] myArray = new String[5];
						 File my_file=null;
						 my_file = new File(ApplicationFilesDrive+projectslidedata[3]+File.separator+projectslidedata[2]); 
						 myArray[0]=projectIds;
						 myArray[1]=projectslidedata[1]+"";
						 //myArray[2]=FilenameUtils.getExtension(projectslidedata[2]+"");
						 //myArray[3]=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(my_file));
						myArray[4] = "SelectedSlidesImage.htm"+"/"+(projectIds);
//						 File imgFile = new File(ApplicationFilesDrive+projectslidedata[3]);
//				            res.setContentType("image/jpeg, image/jpg, image/png, image/gif");
//				            Files.copy(imgFile.toPath(), res.getOutputStream());
//				            res.getOutputStream().close();
						 getAllProjectSlidesdata.add(myArray);
					 }
				 }
				 
//			
				 Gson json = new Gson();
				 return json.toJson(getAllProjectSlidesdata);
			 }
			 
			 
			 @RequestMapping(value = "SelectedSlidesImage.htm/{projectIds}", method = RequestMethod.GET)
			    public void getImage(@PathVariable String projectIds ,HttpServletResponse response) throws Exception {
			       		
				 Object[] projectslidedata = (Object[]) service.GetProjectSildedata(projectIds);
			        	
			            File imgFile = new File(ApplicationFilesDrive+projectslidedata[3]+projectslidedata[2]);
			            response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
			            Files.copy(imgFile.toPath(), response.getOutputStream());
			            response.getOutputStream().close();
			        
			    }
			 @RequestMapping(value="OverallFinanceExcel.htm" ,method = {RequestMethod.POST,RequestMethod.GET})
				public String OverallFinanceExcel( RedirectAttributes redir,HttpServletRequest req ,HttpServletResponse res ,HttpSession ses)throws Exception
				{
				 try {
					 XSSFWorkbook workbook = new XSSFWorkbook();
				        XSSFSheet sheet = workbook.createSheet("PMS_Finance");

				        // Create cell styles
				        CellStyle headerStyle = workbook.createCellStyle();
				        headerStyle.setAlignment(HorizontalAlignment.CENTER);
				        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
				  
				        
				        // First row (merged cells)
				        XSSFRow row1 = sheet.createRow(0);

				        // Merge and set values for the first row
				        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1)); // Merge for "Head"
				        Cell cell = row1.createCell(0);
				        cell.setCellValue("Head");
				        cell.setCellStyle(headerStyle);

				        for (int i = 1; i <= 6; i++) {
				            int colIndex = (i - 1) * 2 + 2;
				            sheet.addMergedRegion(new CellRangeAddress(0, 0, colIndex, colIndex + 1));
				            cell = row1.createCell(colIndex);
				            cell.setCellValue(getColumnHeader(i) + "( in Cr )");
				            cell.setCellStyle(headerStyle);
				        }

				        // Second row (split cells)
				        XSSFRow row2 = sheet.createRow(1);
				        row2.createCell(0).setCellValue("SN");
				        row2.createCell(1).setCellValue("Head");

				        for (int i = 1; i <= 6; i++) {
				            int colIndex = (i - 1) * 2 + 2;
				            row2.createCell(colIndex).setCellValue("RE");
				            row2.createCell(colIndex + 1).setCellValue("FE");
				        }

				        // Adjust column widths
				        for (int i = 0; i <= 13; i++) {
				            sheet.setColumnWidth(i, 3000);
				        }

				    
				     

				        // Set the response properties
				        res.setContentType("application/vnd.ms-excel");
				        res.setHeader("Content-Disposition", "attachment; filename=PMS_Finance.xlsx");

				        // Write the workbook to the response output stream
				        workbook.write(res.getOutputStream());
				        workbook.close();


				} catch (Exception e) {
					// TODO: handle exception
				}
				 return null;
				}
			  private String getColumnHeader(int index) {
			        switch (index) {
			            case 1: return "Sanction";
			            case 2: return "Expenditure";
			            case 3: return "Out Commitment";
			            case 4: return "Balance";
			            case 5: return "DIPL";
			            case 6: return "Notional Balance";
			            default: return "";
			        }
			    }
			  
			  
			  
				private boolean isValidExcelFile(String fileName, String contentType) {
				    if (fileName == null || contentType == null) return false;

				    String lowerFile = fileName.toLowerCase();

				    if (lowerFile.endsWith(".xls") && contentType.equalsIgnoreCase("application/vnd.ms-excel")) {
				        return true;
				    }
				    if (lowerFile.endsWith(".xlsx") && contentType.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
				        return true;
				    }
				    return false;
				}
			  
			  
			  
			  
			  
			  
			  
			  @RequestMapping(value="OverAllFinaceSubmit.htm" ,method = {RequestMethod.POST,RequestMethod.GET})
				public String OverAllFinaceSubmit( RedirectAttributes redir,HttpServletRequest req ,HttpServletResponse res ,HttpSession ses)throws Exception
				{
				  
					String LabCode = (String) ses.getAttribute("labcode");
					String UserId = (String) ses.getAttribute("Username");
					String projectid =req.getParameter("projectid");
					String mainprojectid =req.getParameter("mainprojectid");
					String committeeid =req.getParameter("committeeid");
					Object[]projectDetails=service.ProjectDetails(mainprojectid).get(0);
					
					String projectCode=projectDetails[1].toString();
				   try {
					
					 
					   if (req.getContentType() != null && req.getContentType().startsWith("multipart/")) {
						  
						   
						   Part filePart = req.getPart("filename");
						   
						   String fileName = filePart.getSubmittedFileName();
				            String contentType = filePart.getContentType();
						   
						   if (!isValidExcelFile(fileName, contentType)) {
				                redir.addAttribute("resultfail", "Only Excel files (.xls, .xlsx) are allowed");
				                redir.addFlashAttribute("projectid",projectid);
				    			redir.addFlashAttribute("committeeid",committeeid);
				    			return "redirect:/ProjectBriefingPaper.htm";
				                
				            }
							List<ProjectOverallFinance>list = new ArrayList<>();
							InputStream fileData = filePart.getInputStream();
							
						
							Workbook workbook = new XSSFWorkbook(fileData);
							Sheet sheet  = workbook.getSheetAt(0);
							int rowCount=sheet.getLastRowNum()-sheet.getFirstRowNum(); 
							
			
							for (int i=2;i<=rowCount;i++) {
								ProjectOverallFinance pof = new ProjectOverallFinance();
								int cellcount= sheet.getRow(i).getLastCellNum();
								
								 Row row = sheet.getRow(i);
								 if (row == null) continue;
							
								for(int j=1;j<cellcount;j++) {
									 Cell cell = row.getCell(j);
						                if (cell == null) continue;
									if(j==1) {
										switch(sheet.getRow(i).getCell(j).getCellType()) {
										case BLANK:break;
										case NUMERIC:
											pof.setBudgetHead(String.valueOf((long)sheet.getRow(i).getCell(j).getNumericCellValue()));
											break;
										case STRING:
											pof.setBudgetHead(sheet.getRow(i).getCell(j).getStringCellValue());
											break;	 
										}
										
										
									}
							
									if(j==2) {
									
										switch(sheet.getRow(i).getCell(j).getCellType()) {
										case BLANK:
											pof.setSanctionCostRE(0.00);
											break;
										case NUMERIC:
											pof.setSanctionCostRE(Double.valueOf(sheet.getRow(i).getCell(j).getNumericCellValue()));
											break;
										case STRING:
											pof.setSanctionCostRE(Double.valueOf(sheet.getRow(i).getCell(j).getStringCellValue().length()>0?sheet.getRow(i).getCell(j).getStringCellValue():"0.00"));
											break;	 
										}
										
										
									}
									
									
									if(j==3) {
								
										switch(sheet.getRow(i).getCell(j).getCellType()) {
										case BLANK:
											pof.setSanctionCostFE(0.00);
											break;
										case NUMERIC:
											pof.setSanctionCostFE(Double.valueOf(sheet.getRow(i).getCell(j).getNumericCellValue()));
											break;
										case STRING:
											pof.setSanctionCostFE(Double.valueOf(sheet.getRow(i).getCell(j).getStringCellValue().length()>0?sheet.getRow(i).getCell(j).getStringCellValue():"0.00"));
											break;	 
										}
										
									}
									
									if(j==4) {
										switch(sheet.getRow(i).getCell(j).getCellType()) {
										case BLANK:
											pof.setExpenditureRE(0.00);
											break;
										case NUMERIC:
											pof.setExpenditureRE(Double.valueOf(sheet.getRow(i).getCell(j).getNumericCellValue()));
											break;
										case STRING:
											pof.setExpenditureRE(Double.valueOf(sheet.getRow(i).getCell(j).getStringCellValue().length()>0?sheet.getRow(i).getCell(j).getStringCellValue():"0.00"));
											break;	 
										}
									
									}
									
									if(j==5) {
										switch(sheet.getRow(i).getCell(j).getCellType()) {
										case BLANK:
											pof.setExpenditureFE(0.00);
											break;
										case NUMERIC:
											pof.setExpenditureFE(Double.valueOf(sheet.getRow(i).getCell(j).getNumericCellValue()));
											break;
										case STRING:
											pof.setExpenditureFE(Double.valueOf(sheet.getRow(i).getCell(j).getStringCellValue().length()>0?sheet.getRow(i).getCell(j).getStringCellValue():"0.00"));
											break;	 
										}
									
									}
									
									if(j==6) {
										switch(sheet.getRow(i).getCell(j).getCellType()) {
										case BLANK:
											pof.setOutCommitmentRE(0.00);
											break;
										case NUMERIC:
											pof.setOutCommitmentRE(Double.valueOf(sheet.getRow(i).getCell(j).getNumericCellValue()));
											break;
										case STRING:
											pof.setOutCommitmentRE(Double.valueOf(sheet.getRow(i).getCell(j).getStringCellValue().length()>0?sheet.getRow(i).getCell(j).getStringCellValue():"0.00"));
											break;	 
										}
						
									}
									if(j==7) {
										switch(sheet.getRow(i).getCell(j).getCellType()) {
										case BLANK:
											pof.setOutCommitmentFE(0.00);
											break;
										case NUMERIC:
											pof.setOutCommitmentFE(Double.valueOf(sheet.getRow(i).getCell(j).getNumericCellValue()));
											break;
										case STRING:
											pof.setOutCommitmentFE(Double.valueOf(sheet.getRow(i).getCell(j).getStringCellValue().length()>0?sheet.getRow(i).getCell(j).getStringCellValue():"0.00"));
											break;	 
										}
								
									}
									
									if(j==8) {
										switch(sheet.getRow(i).getCell(j).getCellType()) {
										case BLANK:
											pof.setBalanceRE(0.00);
											break;
										case NUMERIC:
											pof.setBalanceRE(Double.valueOf(sheet.getRow(i).getCell(j).getNumericCellValue()));
											break;
										case STRING:
											pof.setBalanceRE(Double.valueOf(sheet.getRow(i).getCell(j).getStringCellValue().length()>0?sheet.getRow(i).getCell(j).getStringCellValue():"0.00"));
											break;	 
										}
								
									}
									
									if(j==9) {
										switch(sheet.getRow(i).getCell(j).getCellType()) {
										case BLANK:
											pof.setBalanceFE(0.00);
											break;
										case NUMERIC:
											pof.setBalanceFE(Double.valueOf(sheet.getRow(i).getCell(j).getNumericCellValue()));
											break;
										case STRING:
											pof.setBalanceFE(Double.valueOf(sheet.getRow(i).getCell(j).getStringCellValue().length()>0?sheet.getRow(i).getCell(j).getStringCellValue():"0.00"));
											break;	 
										}
								
									}
									
									if(j==10) {
										switch(sheet.getRow(i).getCell(j).getCellType()) {
										case BLANK:
											pof.setDiplRE(0.00);
											break;
										case NUMERIC:
											pof.setDiplRE(Double.valueOf(sheet.getRow(i).getCell(j).getNumericCellValue()));
											break;
										case STRING:
											pof.setDiplRE(Double.valueOf(sheet.getRow(i).getCell(j).getStringCellValue().length()>0?sheet.getRow(i).getCell(j).getStringCellValue():"0.00"));
											break;	 
										}
									
									}
									if(j==11) {
										switch(sheet.getRow(i).getCell(j).getCellType()) {
										case BLANK:
											pof.setDiplFE(0.00);
											break;
										case NUMERIC:
											pof.setDiplFE(Double.valueOf(sheet.getRow(i).getCell(j).getNumericCellValue()));
											break;
										case STRING:
											pof.setDiplFE(Double.valueOf(sheet.getRow(i).getCell(j).getStringCellValue().length()>0?sheet.getRow(i).getCell(j).getStringCellValue():"0.00"));
											break;	 
										}
							
									}
									
									if(j==13) {
										switch(sheet.getRow(i).getCell(j).getCellType()) {
										case BLANK:
											pof.setNotaionalBalFE(0.00);
											break;
										case NUMERIC:
											pof.setNotaionalBalFE(Double.valueOf(sheet.getRow(i).getCell(j).getNumericCellValue()));
											break;
										case STRING:
											pof.setNotaionalBalFE(Double.valueOf(sheet.getRow(i).getCell(j).getStringCellValue().length()>0?sheet.getRow(i).getCell(j).getStringCellValue():"0.00"));
											break;	 
										}
								
									}
									
									if(j==12) {
										switch(sheet.getRow(i).getCell(j).getCellType()) {
										case BLANK:
											pof.setNotaionalBalRE(0.00);
											break;
										case NUMERIC:
											pof.setNotaionalBalRE(Double.valueOf(sheet.getRow(i).getCell(j).getNumericCellValue()));
											break;
										case STRING:
											pof.setNotaionalBalRE(Double.valueOf(sheet.getRow(i).getCell(j).getStringCellValue().length()>0?sheet.getRow(i).getCell(j).getStringCellValue():"0.00"));
											break;	 
										}
									}
								}
									
								pof.setProjectCode(projectCode);
								pof.setProjectId(Long.parseLong(mainprojectid));
								pof.setLabCode(LabCode);
								pof.setCreatedBy(UserId);
								pof.setCreatedDate(LocalDate.now().toString());
								pof.setIsActive(1);
								
								if(pof.getSanctionCostRE()==null) {
									pof.setSanctionCostRE(0.00);
								}
								if(pof.getSanctionCostFE()==null) {
									pof.setSanctionCostFE(0.00);
								}
								
								if(pof.getExpenditureRE()==null) {
									pof.setExpenditureRE(0.00);
								}
								
								if(pof.getExpenditureFE()==null) {
									pof.setExpenditureFE(0.00);
								}
								
								if(pof.getOutCommitmentRE()==null) {
									pof.setOutCommitmentRE(0.00);
								}
								
								if(pof.getOutCommitmentFE()==null) {
									pof.setOutCommitmentFE(0.00);
								}
								
								if(pof.getBalanceRE()==null) {
									pof.setBalanceRE(0.00);
								}
								
								if(pof.getBalanceFE()==null) {
									pof.setBalanceFE(0.00);
								}
								
								if(pof.getDiplRE()==null) {
									pof.setDiplRE(0.00);
								}
								
								if(pof.getDiplFE()==null) {
									pof.setDiplFE(0.00);
								}
								if(pof.getNotaionalBalRE()==null) {
									pof.setNotaionalBalRE(0.00);
								}
								
								if(pof.getNotaionalBalFE()==null) {
									pof.setNotaionalBalFE(0.00);
								}
								
								if(pof.getBudgetHead()!=null ) {
									list.add(pof);
								}
								
								System.out.println(list.size());
							}
							
							long count = service.addOverallFinace(list,mainprojectid);
							
							if(count>0) {
								redir.addAttribute("result","Overall Finance Data Added Successfully");
							}else {
								redir.addAttribute("resultfail","Overall Finance Data Add Unsuccessful");

							}
					
					   }
				   }catch (Exception e) {
					// TODO: handle exceptione.pr
					   e.printStackTrace();
				}
					redir.addFlashAttribute("projectid",projectid);
	    			redir.addFlashAttribute("committeeid",committeeid);
	    			return "redirect:/ProjectBriefingPaper.htm";
				}
			  
			  
			  @RequestMapping(value = "excelSheetWithFinanceData.htm", method = {RequestMethod.POST, RequestMethod.GET})
				public String excelSheetWithFinanceData(RedirectAttributes redir, HttpServletRequest req,
				                                        HttpServletResponse res, HttpSession ses) throws Exception {

				    String mainprojectid = req.getParameter("mainprojectid");
				    System.out.println("mainprojectid: " + mainprojectid);

				    List<Object[]> list = service.getrOverallFinance(mainprojectid);

				    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
				        XSSFSheet sheet = workbook.createSheet("PMS_Finance");

				        // Header cell style
				        CellStyle headerStyle = workbook.createCellStyle();
				        headerStyle.setAlignment(HorizontalAlignment.CENTER);
				        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
				      
				    

				        // === First Row (Merged Headers)
				        XSSFRow row1 = sheet.createRow(0);
				        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
				        Cell cell = row1.createCell(0);
				        cell.setCellValue("Head");
				        cell.setCellStyle(headerStyle);

				        for (int i = 1; i <= 6; i++) {
				            int colIndex = (i - 1) * 2 + 2;
				            sheet.addMergedRegion(new CellRangeAddress(0, 0, colIndex, colIndex + 1));
				            cell = row1.createCell(colIndex);
				            cell.setCellValue(getColumnHeader(i) + " ( in Cr )");
				            cell.setCellStyle(headerStyle);
				        }

				        // === Second Row (RE/FE Labels)
				        XSSFRow row2 = sheet.createRow(1);
				        row2.createCell(0).setCellValue("SN");
				        row2.createCell(1).setCellValue("Head");
				        for (int i = 1; i <= 6; i++) {
				            int colIndex = (i - 1) * 2 + 2;
				            row2.createCell(colIndex).setCellValue("RE");
				            row2.createCell(colIndex + 1).setCellValue("FE");
				        }

				        // Set column widths
				        for (int i = 0; i <= 13; i++) {
				            sheet.setColumnWidth(i, 4000);
				        }

				      
				        if (list != null && list.size() > 0) {
				            for (int i = 0; i < list.size(); i++) {
				                Object[] obj = list.get(i);
				                XSSFRow row = sheet.createRow(i + 2); // Start from 3rd row (index 2)

				                // SN
				                row.createCell(0).setCellValue(i + 1);

				                // Head from obj[4]
				                row.createCell(1).setCellValue(obj[4] != null ? obj[4].toString() : "");

				                // RE/FE values from obj[5] to obj[16]
				                int colIndex = 2;
				                for (int j = 5; j <= 16; j += 2) {
				                    String re = (obj[j] != null) ? obj[j].toString() : "";
				                    String fe = (j + 1 <= 16 && obj[j + 1] != null) ? obj[j + 1].toString() : "";

				                    row.createCell(colIndex).setCellValue(re);
				                    row.createCell(colIndex + 1).setCellValue(fe);
				                    colIndex += 2;
				                }
				            }
				        }

				        // === Set Response Headers
				        res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				        res.setHeader("Content-Disposition", "attachment; filename=PMS_Finance.xlsx");

				        workbook.write(res.getOutputStream());
				        res.getOutputStream().flush();

				    } catch (Exception e) {
				        e.printStackTrace(); // or use proper logging
				        throw new Exception("Error generating Excel file", e);
				    }

				    return null;
				}
	@RequestMapping(value="PrgmBriefingPresentation.htm", method= {RequestMethod.POST, RequestMethod.GET})
	public String prgmBriefingPresentation(HttpServletRequest req, HttpSession ses) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		String labcode = (String) ses.getAttribute("labcode");
		logger.info(new Date()+" Inside PrgmBriefingPresentation.htm "+UserId);
		try {
			String projectid=req.getParameter("projectid");
			String committeeid = req.getParameter("committeeid");
			
			Committee committee = service.getCommitteeData(committeeid);
			List<Object[]> projectDetails2 = service.ProjectDetails(projectid);
			String projectLabCode = projectDetails2.get(0)[5].toString();
			
	    	req.setAttribute("committeeData", committee);
			req.setAttribute("committeeMetingsCount", service.ProjectCommitteeMeetingsCount(projectid, "0", "0", "0", "0", committee.getCommitteeShortName().trim()) );
			
			Object[] nextmeetVenue = (Object[])service.BriefingMeetingVenue(projectid, committeeid);
	    	req.setAttribute("nextMeetVenue", nextmeetVenue);
	    	Object[] lastmeetingVenue = service.getLastcreatedSchedule(projectid, committeeid);
			req.setAttribute("lastmeetingVenue", lastmeetingVenue);

			req.setAttribute("nextMeetVenue", nextmeetVenue);
			if (nextmeetVenue != null && nextmeetVenue[0] != null) {
				req.setAttribute("recdecDetails", service.GetRecDecDetails(nextmeetVenue[0].toString()));
			}
			if(lastmeetingVenue!=null && lastmeetingVenue[0]!=null && labcode.equalsIgnoreCase("PGAD")) {
				List<Object[]> list = service.getMilestoneBriefingList(lastmeetingVenue[0].toString());
				Map<String,List<Object[]>> milestoneBriefingMap = list!=null ? list.stream().collect(Collectors.groupingBy(obj -> obj[1]!=null?obj[1].toString():"UNKNOWN",LinkedHashMap::new,Collectors.toList())) : new LinkedHashMap<String, List<Object[]>>();
				req.setAttribute("milestoneBriefingMap", milestoneBriefingMap);
				req.setAttribute("recdecDetails", service.GetRecDecDetails(lastmeetingVenue[0].toString()));
			}
	    	req.setAttribute("RiskTypes", service.RiskTypes());
	    	
	    	req.setAttribute("filePath", env.getProperty("ApplicationFilesDrive"));
    		req.setAttribute("projectLabCode", projectLabCode);
	    	req.setAttribute("labInfo", service.LabDetailes(projectLabCode));
	    	req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(projectLabCode));
	    	req.setAttribute("Drdologo", LogoUtil.getDRDOLogoAsBase64String());
	    	req.setAttribute("thankYouImg", LogoUtil.getThankYouImageAsBase64String());
    		req.setAttribute("projectid", projectid);
    		req.setAttribute("committeeid", committeeid);
    		req.setAttribute("ProjectCost", ProjectCost);
    		req.setAttribute("isCCS", projectDetails2.get(0)[6].toString());
    		req.setAttribute("IsIbasConnected", IsIbasConnected);
    		req.setAttribute("projectDetails", prservice.getProjectDetails(labcode, projectid, "E"));
    		
    		List<Object[]> SpecialCommitteesList =  service.SpecialCommitteesList(labcode);
	    	Map<String, List<Object[]>> reviewMeetingListMap = new HashMap<String, List<Object[]>>();
	    	Map<String, List<Object[]>> lastMeetingActionsListMap = new HashMap<String, List<Object[]>>();
	    	Map<Integer, String> committeeWiseMap = new HashMap<>();
			for(Object[] obj : SpecialCommitteesList) {
				reviewMeetingListMap.put(obj[1]+"", service.ReviewMeetingList(projectid, obj[1]+""));
				List<Object[]> lastMeetingActions = service.LastPMRCActions(projectid, obj[0]+"");
				if(lastMeetingActions!=null && lastMeetingActions.size()>0) {
					lastMeetingActionsListMap.put(obj[1]+"", lastMeetingActions);
					List<Object[]> meetingList = reviewMeetingListMap.get(obj[1]+"");
					IntStream.range(0, meetingList.size()).forEach(i -> committeeWiseMap.put(i + 1, String.valueOf(meetingList.get(i)[3])));
				}
			}
	    	req.setAttribute("reviewMeetingListMap",reviewMeetingListMap);
	    	req.setAttribute("otherMeetingList", service.otherMeetingList(projectid));
	    	req.setAttribute("lastMeetingActionsListMap",lastMeetingActionsListMap);
			req.setAttribute("committeeWiseMap", committeeWiseMap);
			
	    	List<Object[]> milestoneMainList = milservice.getAllMilestoneActivityList();
			List<Object[]> milestoneSubList = milservice.getAllMilestoneActivityLevelList();
			List<Object[]> progressList = milservice.getMilestoneActivityProgressList();
			
			getAllMilestoneList(req, milestoneMainList, milestoneSubList, progressList, projectid);
			
			req.setAttribute("milestoneOpenActionList", service.getMilestoneOpenActionListByProjectId(projectid));
			
			return "print/PrgmBriefingPresentation";
		}catch (Exception e) {
			e.printStackTrace();
			logger.info(new Date()+" Inside PrgmBriefingPresentation.htm "+UserId, e);
			return "static/Error";
		}
	}
	
	private long getAllMilestoneList(HttpServletRequest req, List<Object[]> milestoneMainList, List<Object[]> milestoneSubList, List<Object[]> progressList, String projectId) throws Exception {
		try {
			LocalDate todayL = LocalDate.now();
			LocalDate fromDateL = todayL.minusWeeks(1);
			
			Map<String, List<Object[]>> milestoneSubListMap = new HashMap<String, List<Object[]>>();
			Map<Long, List<Object[]>> milestoneProgressListMap = new HashMap<Long, List<Object[]>>();

			if (milestoneMainList != null && !milestoneMainList.isEmpty()) {
				milestoneMainList = milestoneMainList.stream().filter(e -> e[1].toString().equalsIgnoreCase(projectId) ).collect(Collectors.toList());

				Map<String, List<Object[]>> groupedByParentIdAndLevel = milestoneSubList.stream().collect(Collectors.groupingBy(e -> e[1].toString() + "_" + e[2].toString()));

				for(Object[] objmain : milestoneMainList ) {
					List<Object[]> totalAssignedSubList = new ArrayList<>();
					List<Object[]> MilestoneActivityA = groupedByParentIdAndLevel.getOrDefault(objmain[0].toString()+"_1", Collections.emptyList());
					int countA = 1;
					for(Object[] obj:MilestoneActivityA) {

						milestoneProgressListMap.put(Long.parseLong(obj[0].toString()), filteredProgressList(progressList, obj[0].toString(), fromDateL, todayL));

						Object[] newRow1 = Arrays.copyOf(obj, obj.length + 4);
						newRow1[obj.length] = objmain[0].toString();
						newRow1[obj.length + 1] = "A" + countA;
						newRow1[obj.length + 2] = objmain[2];
						newRow1[obj.length + 3] = objmain[1].toString();
						totalAssignedSubList.add(newRow1);

						List<Object[]> MilestoneActivityB = groupedByParentIdAndLevel.getOrDefault(obj[0].toString()+"_2", Collections.emptyList());
						int countB = 1;
						for(Object[] obj1:MilestoneActivityB) {

							milestoneProgressListMap.put(Long.parseLong(obj1[0].toString()), filteredProgressList(progressList, obj1[0].toString(), fromDateL, todayL));

							Object[] newRow2 = Arrays.copyOf(obj1, obj1.length + 4);
							newRow2[obj1.length] = objmain[0].toString();
							newRow2[obj1.length + 1] = "A"+countA+"-B"+countB;
							newRow2[obj1.length + 2] = objmain[2];
							newRow2[obj1.length + 3] = objmain[1].toString();
							totalAssignedSubList.add(newRow2);

							List<Object[]> MilestoneActivityC = groupedByParentIdAndLevel.getOrDefault(obj1[0].toString()+"_3", Collections.emptyList());
							int countC = 1;
							for(Object[] obj2:MilestoneActivityC) {

								milestoneProgressListMap.put(Long.parseLong(obj2[0].toString()), filteredProgressList(progressList, obj2[0].toString(), fromDateL, todayL));

								Object[] newRow3 = Arrays.copyOf(obj2, obj2.length + 4);
								newRow3[obj2.length] = objmain[0].toString();
								newRow3[obj2.length + 1] = "A"+countA+"-B"+countB+"-C"+countC;
								newRow3[obj2.length + 2] = objmain[2];
								newRow3[obj2.length + 3] = objmain[1].toString();
								totalAssignedSubList.add(newRow3);

								List<Object[]> MilestoneActivityD = groupedByParentIdAndLevel.getOrDefault(obj2[0].toString()+"_4", Collections.emptyList());
								int countD = 1;
								for(Object[] obj3:MilestoneActivityD) {

									milestoneProgressListMap.put(Long.parseLong(obj3[0].toString()), filteredProgressList(progressList, obj3[0].toString(), fromDateL, todayL));

									Object[] newRow4 = Arrays.copyOf(obj3, obj3.length + 4);
									newRow4[obj3.length] = objmain[0].toString();
									newRow4[obj3.length + 1] = "A"+countA+"-B"+countB+"-C"+countC+"-D"+countD;
									newRow4[obj3.length + 2] = objmain[2];
									newRow4[obj3.length + 3] = objmain[1].toString();
									totalAssignedSubList.add(newRow4);

									List<Object[]> MilestoneActivityE = groupedByParentIdAndLevel.getOrDefault(obj3[0].toString()+"_5", Collections.emptyList());
									int countE = 1;
									for(Object[] obj4:MilestoneActivityE) {
										
										milestoneProgressListMap.put(Long.parseLong(obj4[0].toString()), filteredProgressList(progressList, obj4[0].toString(), fromDateL, todayL));

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
					milestoneSubListMap.put(objmain[0].toString(), totalAssignedSubList);
				}
			}
			
			req.setAttribute("milestoneMainList", milestoneMainList);
			req.setAttribute("milestoneSubListMap", milestoneSubListMap);
			req.setAttribute("milestoneProgressListMap", milestoneProgressListMap);
			return 1;
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	private List<Object[]> filteredProgressList(List<Object[]> progressList, String activityId, LocalDate from, LocalDate to) {
	    return progressList.stream()
	        .filter(e -> e[1].toString().equalsIgnoreCase(activityId)
	                && !LocalDate.parse(e[3].toString()).isBefore(from)
	                && !LocalDate.parse(e[3].toString()).isAfter(to))
	        .collect(Collectors.toList());
	}
	
	private String redirectWithError(RedirectAttributes redir,String redirURL, String message) {
	    redir.addAttribute("resultfail", message);
	    return "redirect:/"+redirURL;
	}
	
	
	@RequestMapping(value = "techFilePreview.htm/{id}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Resource> downloadPdfFromPasswordZip(
			 @PathVariable("id") String id,
	        HttpServletRequest req) throws Exception {

	    Object[] filedata = service.TechWorkData(id);
	    if (filedata == null) {
	        return ResponseEntity.notFound().build();
	    }

	    try {
			String fileExt = FilenameUtils.getExtension(filedata[8].toString());
			String tecdata = filedata[6].toString().replaceAll("[/\\\\]", ",");
    		String[] fileParts = tecdata.split(",");
    		String zipName = String.format(filedata[7].toString()+filedata[11].toString()+"-"+filedata[10].toString()+".zip");
    		Path zipFilePath;
    		if(fileParts.length == 4){
    			zipFilePath = Paths.get(ApplicationFilesDrive, fileParts[0],fileParts[1],fileParts[2],fileParts[3],zipName);
    		}else{
    			zipFilePath = Paths.get(ApplicationFilesDrive, fileParts[0],fileParts[1],fileParts[2],fileParts[3],fileParts[4],zipName);
    		}
			
			 String path = req.getServletContext().getRealPath("/view/temp");
			Zipper zip = new Zipper();
	    	
	        if (!Files.exists(zipFilePath)) {
	            return ResponseEntity.notFound().build();
	        }
//	        // Extract zip to a temp folder
	        String tempDirPath = req.getServletContext().getRealPath("/view/temp/" + UUID.randomUUID());
	        File tempDir = new File(tempDirPath);
	        tempDir.mkdirs();
	        zip.unpack(zipFilePath.toString(), tempDirPath, filedata[9].toString());

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

	@RequestMapping(value = "LabAllProjectReport.htm" , method = RequestMethod.GET)
	public void LabAllProjectReport(HttpServletRequest req , RedirectAttributes redir, HttpServletResponse res , HttpSession ses)throws Exception
	{
		long startTime = System.currentTimeMillis();
		String UserId = (String) ses.getAttribute("Username");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String LabCode = (String) ses.getAttribute("labcode");
		String Logintype = (String) ses.getAttribute("LoginType");
		logger.info(new Date() + "Inside GetAllProjectSlide.htm " + UserId);
		try {
	
		List<Object[]> projectslist =service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
		
		List<String>projectIds = projectslist.stream().map(e->e[0].toString())
								.collect(Collectors.toList());
		
		List<Object[]> getAllProjectSlidedata = new ArrayList<>();

		List<Object[]> getAllProjectdata = new ArrayList<>();

		List<Object[]> getAllProjectSlidesdata = new ArrayList<>();

		if (projectIds != null && projectIds.size() > 0)

			for (String id : projectIds) {

				List<Object[]> getoneProjectSlidedata = service.GetAllProjectSildedata(id);  // freezing data
				Object[] projectslidedata = (Object[]) service.GetProjectSildedata(id);  //[7] id project id
				getAllProjectSlidesdata.add(projectslidedata);
				Object[] projectdata = (Object[]) service.GetProjectdata(id); //[0] is project id ------ all vals
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

		List<Object[]>projectPMRCMeetings = service.getProjectMeetings("PMRB");
		List<Object[]>ebPMRCMeetings = service.getProjectMeetings("EB");
		List<Object[]>projectSPRCMeetings = service.getProjectMeetings("SPRC");

		List<Object[]>projectClosureReport = service.getProjectClosureReport();
		
		List<Object[]>projectList = service.getprojectListProjectDirectorWise();

			if (getAllProjectdata.size() > 1) {
				Collections.sort(getAllProjectdata, dateComparator);
				Collections.sort(getAllProjectSlidedata, dateComparator);
			}
			Collections.reverse(getAllProjectdata);

			String labcode = ses.getAttribute("labcode").toString();

			req.setAttribute("getAllProjectdata", getAllProjectdata);
			req.setAttribute("projectPMRCMeetings", projectPMRCMeetings);
			req.setAttribute("ebMeetings", ebPMRCMeetings);
			req.setAttribute("projectSPRCMeetings", projectSPRCMeetings);
			req.setAttribute("projectList", projectList);
			req.setAttribute("projectClosureReport", projectClosureReport);

			req.setAttribute("labInfo", service.LabDetailes(labcode));

			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(labcode));

			req.setAttribute("Drdologo", LogoUtil.getDRDOLogoAsBase64String());

			req.setAttribute("filepath", ApplicationFilesDrive);

			req.setAttribute("getAllProjectSlidedata", getAllProjectSlidedata);
			req.setAttribute("getAllProjectSlidesdata", getAllProjectSlidesdata);

			CharArrayWriterResponse customResponse = new CharArrayWriterResponse(res);
			req.getRequestDispatcher("/view/print/LabAllProjectReport.jsp").forward(req, customResponse);
			String html = customResponse.getOutput();

			ConverterProperties converterProperties = new ConverterProperties();
			FontProvider dfp = new DefaultFontProvider(true, true, true);
			converterProperties.setFontProvider(dfp);
			String filename="ProjectPara";
			String path=req.getServletContext().getRealPath("/view/temp");
			HtmlConverter.convertToPdf(html,new FileOutputStream(path+File.separator+filename+".pdf"),converterProperties);
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
		
		} catch (Exception e) {

			e.printStackTrace();

			//logger.error(new Date() + " Inside GetAllProjectSlide.htm " + UserId, e);

		}
		long end = System.currentTimeMillis();
		System.out.println("Time Needed "+(end-startTime));

	
	}

	
	@RequestMapping(value = "LabAllProjectExcel.htm" , method = RequestMethod.GET)
	public String LabAllProjectExcel(HttpServletRequest req , RedirectAttributes redir, HttpServletResponse res , HttpSession ses)throws Exception
	{
		long startTime = System.currentTimeMillis();
		String UserId = (String) ses.getAttribute("Username");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String LabCode = (String) ses.getAttribute("labcode");
		String Logintype = (String) ses.getAttribute("LoginType");
		logger.info(new Date() + "Inside GetAllProjectSlide.htm " + UserId);
		try {
	
		List<Object[]> projectslist =service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
		
		List<String>projectIds = projectslist.stream().map(e->e[0].toString())
								.collect(Collectors.toList());
		
		List<Object[]> getAllProjectSlidedata = new ArrayList<>();

		List<Object[]> getAllProjectdata = new ArrayList<>();

		List<Object[]> getAllProjectSlidesdata = new ArrayList<>();

		if (projectIds != null && projectIds.size() > 0)

			for (String id : projectIds) {

				List<Object[]> getoneProjectSlidedata = service.GetAllProjectSildedata(id);  // freezing data
				Object[] projectslidedata = (Object[]) service.GetProjectSildedata(id);  //[7] id project id
				getAllProjectSlidesdata.add(projectslidedata);
				Object[] projectdata = (Object[]) service.GetProjectdata(id); //[0] is project id ------ all vals
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

		List<Object[]>projectPMRCMeetings = service.getProjectMeetings("PMRB");
		List<Object[]>ebPMRCMeetings = service.getProjectMeetings("EB");
		List<Object[]>projectSPRCMeetings = service.getProjectMeetings("SPRC");

		List<Object[]>projectClosureReport = service.getProjectClosureReport();
		
		List<Object[]>projectList = service.getprojectListProjectDirectorWise();

			if (getAllProjectdata.size() > 1) {
				Collections.sort(getAllProjectdata, dateComparator);
				Collections.sort(getAllProjectSlidedata, dateComparator);
			}
			Collections.reverse(getAllProjectdata);

			String labcode = ses.getAttribute("labcode").toString();

			req.setAttribute("getAllProjectdata", getAllProjectdata);
			req.setAttribute("projectPMRCMeetings", projectPMRCMeetings);
			req.setAttribute("ebMeetings", ebPMRCMeetings);
			req.setAttribute("projectSPRCMeetings", projectSPRCMeetings);
			req.setAttribute("projectList", projectList);
			req.setAttribute("projectClosureReport", projectClosureReport);

			req.setAttribute("labInfo", service.LabDetailes(labcode));

			req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(labcode));

			req.setAttribute("Drdologo", LogoUtil.getDRDOLogoAsBase64String());

			req.setAttribute("filepath", ApplicationFilesDrive);

			req.setAttribute("getAllProjectSlidedata", getAllProjectSlidedata);
			req.setAttribute("getAllProjectSlidesdata", getAllProjectSlidesdata);

		
		
		} catch (Exception e) {

			e.printStackTrace();

			//logger.error(new Date() + " Inside GetAllProjectSlide.htm " + UserId, e);

		}
		long end = System.currentTimeMillis();
		System.out.println("Time Needed "+(end-startTime));

		return "print/AllReportExcel";
	}
	
	@RequestMapping(value = "ProjectBriefingNewDownload.htm")
	public void downloadNewBriefingPaperPDF(HttpServletRequest req, HttpSession ses, HttpServletResponse res) {

	    String UserId = (String) ses.getAttribute("Username");
	    String LabCode = (String) ses.getAttribute("labcode");

	    logger.info(new Date() + "Inside ProjectBriefingNewDownload.htm " + UserId);

	    try {

	        String projectid = req.getParameter("projectid");
	        String committeeid = req.getParameter("committeeid");

	        Committee committee = service.getCommitteeData(committeeid);

	        String projectLabCode = service.ProjectDetails(projectid).get(0)[5].toString();
	        String CommitteeCode = committee.getCommitteeShortName().trim();

	        if (LabCode.equalsIgnoreCase("ADE")) {
	            req.setAttribute("otherMeetingList", service.otherMeetingList(projectid));
	        }

	        List<Object[]> SpecialCommitteesList = service.SpecialCommitteesList(LabCode);
	        Map<String, List<Object[]>> reviewMeetingListMap = new HashMap<>();

	        for (Object[] obj : SpecialCommitteesList) {
	            reviewMeetingListMap.put(obj[1] + "", service.ReviewMeetingList(projectid, obj[1] + ""));
	        }

	        req.setAttribute("reviewMeetingListMap", reviewMeetingListMap);

	        req.setAttribute("text", req.getParameter("text"));
	        req.setAttribute("IsIbasConnected", IsIbasConnected);
	        req.setAttribute("committeeData", committee);
	        req.setAttribute("projectid", projectid);
	        req.setAttribute("committeeid", committeeid);
	        req.setAttribute("ProjectCost", ProjectCost);
	        req.setAttribute("isprint", "0");
	        req.setAttribute("AppFilesPath", ApplicationFilesDrive);
	        req.setAttribute("projectLabCode", projectLabCode);
	        req.setAttribute("labInfo", service.LabDetailes(projectLabCode));
	        req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(projectLabCode));
	        req.setAttribute("thankYouImg", LogoUtil.getThankYouImageAsBase64String());
	        req.setAttribute("filePath", env.getProperty("ApplicationFilesDrive"));
	        req.setAttribute("ApplicationFilesDrive", env.getProperty("ApplicationFilesDrive"));

	        req.setAttribute("committeeMetingsCount",
	                service.ProjectCommitteeMeetingsCount(projectid, "0", "0", "0", "0", CommitteeCode));

	        Object[] nextmeetVenue = (Object[]) service.BriefingMeetingVenue(projectid, committeeid);
	        req.setAttribute("nextMeetVenue", nextmeetVenue);
			Object[] lastmeetingVenue = service.getLastcreatedSchedule(projectid, committeeid);
			req.setAttribute("lastmeetingVenue", lastmeetingVenue);


	        if (nextmeetVenue != null && nextmeetVenue[0] != null) {

	            req.setAttribute("recdecDetails", service.GetRecDecDetails(nextmeetVenue[0].toString()));

	        }
			if(lastmeetingVenue!=null && lastmeetingVenue[0]!=null && LabCode.equalsIgnoreCase("PGAD")) {
				List<Object[]> list = service.getMilestoneBriefingList(lastmeetingVenue[0].toString());
				Map<String,List<Object[]>> milestoneBriefingMap = list!=null ? list.stream().collect(Collectors.groupingBy(obj -> obj[1]!=null?obj[1].toString():"UNKNOWN",LinkedHashMap::new,Collectors.toList())) : new LinkedHashMap<String, List<Object[]>>();
				req.setAttribute("milestoneBriefingMap", milestoneBriefingMap);
				req.setAttribute("recdecDetails", service.GetRecDecDetails(lastmeetingVenue[0].toString()));
			}

	        req.setAttribute("RiskTypes", service.RiskTypes());

	        Object[] mileStoneLevelId = service.MileStoneLevelId(projectid, committeeid);
	        req.setAttribute("levelid", mileStoneLevelId != null ? mileStoneLevelId[0].toString() : "2");

	        processProjectData(req, projectid, committeeid, uri, projectLabCode, UserId, IsIbasConnected,ses);

	        List<Object[]> projectdatadetails = (List<Object[]>) req.getAttribute("projectdatadetails");
	        List<List<Object[]>> ebandpmrccount = (List<List<Object[]>>) req.getAttribute("ebandpmrccount");
	        List<Object[]> TechWorkDataList = (List<Object[]>) req.getAttribute("TechWorkDataList");

	        milestoneLevelDataMap(req, reviewMeetingListMap, projectid, committee.getCommitteeShortName().trim());

	        String filename = "BriefingPaper";

	        String path = req.getServletContext().getRealPath("/view/temp");
	        req.setAttribute("path", path);

	        CharArrayWriterResponse response1 = new CharArrayWriterResponse(res);
	        req.getRequestDispatcher("/view/print/BriefingPaperNewpgad.jsp").forward(req, response1);

	        String html1 = response1.getOutput();

	        String pdf1Path = path + File.separator + "briefing_" + UserId + ".pdf";

	        HtmlConverter.convertToPdf(html1, new FileOutputStream(pdf1Path));

	        CharArrayWriterResponse response2 = new CharArrayWriterResponse(res);
	        req.getRequestDispatcher("/view/print/ActionTablePgad.jsp").forward(req, response2);

	        String html2 = response2.getOutput();

	        String pdf2Path = path + File.separator + "actiontable_" + UserId + ".pdf";

	        HtmlConverter.convertToPdf(html2, new FileOutputStream(pdf2Path));

	        String mergedPath = path + File.separator + filename + "_" + UserId + ".pdf";

	        PdfDocument mergedPdf = new PdfDocument(new PdfWriter(mergedPath));
	        PdfMerger merger = new PdfMerger(mergedPdf);

	        PdfDocument pdf1 = new PdfDocument(new PdfReader(pdf1Path));
	        PdfDocument pdf2 = new PdfDocument(new PdfReader(pdf2Path));

	        merger.merge(pdf1, 1, pdf1.getNumberOfPages());
	        merger.merge(pdf2, 1, pdf2.getNumberOfPages());

	        pdf1.close();
	        pdf2.close();

	        int z = 0;

	        for (Object[] objData : projectdatadetails) {

	            try {

	                if (objData != null) {

	                    String No2 = null;

	                    if (CommitteeCode.equalsIgnoreCase("PMRB")) {
	                        No2 = "P" + (Long.parseLong(ebandpmrccount.get(0).get(0)[1].toString()) + 1);
	                    } 
	                    else if (CommitteeCode.equalsIgnoreCase("EB")) {
	                        No2 = "E" + (Long.parseLong(ebandpmrccount.get(0).get(1)[1].toString()) + 1);
	                    }

	                    String fileName = "grantt_" + objData[1].toString() + "_" + No2 + ".pdf";

	                    Path ganttPath = Paths.get(
	                            env.getProperty("ApplicationFilesDrive"),
	                            projectLabCode,
	                            "gantt",
	                            fileName
	                    );

	                    if (Files.exists(ganttPath)) {

	                        File headerPdf = addHeaderForPdf(
	                                ganttPath.toFile(),
	                                objData[12] + " :- Gantt Chart"
	                        );

	                        PdfDocument pdf = new PdfDocument(new PdfReader(headerPdf));
	                        merger.merge(pdf, 1, pdf.getNumberOfPages());
	                        pdf.close();

	                        headerPdf.delete();
	                    }
	                }


	                if (objData != null && objData[3] != null) {

	                    Path sysPath = Paths.get(
	                            env.getProperty("ApplicationFilesDrive"),
	                            projectLabCode,
	                            "ProjectData",
	                            objData[3].toString()
	                    );

	                    if (FilenameUtils.getExtension(objData[3].toString()).equalsIgnoreCase("pdf")
	                            && Files.exists(sysPath)) {

	                        File headerPdf = addHeaderForPdf(
	                                sysPath.toFile(),
	                                objData[12] + " :- System Configuration Annexure"
	                        );

	                        PdfDocument pdf = new PdfDocument(new PdfReader(headerPdf));
	                        merger.merge(pdf, 1, pdf.getNumberOfPages());
	                        pdf.close();

	                        headerPdf.delete();
	                    }
	                }



	                if (objData != null && objData[4] != null) {

	                    Path specPath = Paths.get(
	                            env.getProperty("ApplicationFilesDrive"),
	                            projectLabCode,
	                            "ProjectData",
	                            objData[4].toString()
	                    );

	                    if (FilenameUtils.getExtension(objData[4].toString()).equalsIgnoreCase("pdf")
	                            && Files.exists(specPath)) {

	                        File headerPdf = addHeaderForPdf(
	                                specPath.toFile(),
	                                objData[12] + " :- System Specification Annexure"
	                        );

	                        PdfDocument pdf = new PdfDocument(new PdfReader(headerPdf));
	                        merger.merge(pdf, 1, pdf.getNumberOfPages());
	                        pdf.close();

	                        headerPdf.delete();
	                    }
	                }


	                if (objData != null && objData[5] != null) {

	                    Path treePath = Paths.get(
	                            env.getProperty("ApplicationFilesDrive"),
	                            projectLabCode,
	                            "ProjectData",
	                            objData[5].toString()
	                    );

	                    if (FilenameUtils.getExtension(objData[5].toString()).equalsIgnoreCase("pdf")
	                            && Files.exists(treePath)) {

	                        File headerPdf = addHeaderForPdf(
	                                treePath.toFile(),
	                                objData[12] + " :- Overall Product tree/WBS Annexure"
	                        );

	                        PdfDocument pdf = new PdfDocument(new PdfReader(headerPdf));
	                        merger.merge(pdf, 1, pdf.getNumberOfPages());
	                        pdf.close();

	                        headerPdf.delete();
	                    }
	                }


	                if (objData != null && objData[6] != null) {

	                    Path trlPath = Paths.get(
	                            env.getProperty("ApplicationFilesDrive"),
	                            projectLabCode,
	                            "ProjectData",
	                            objData[6].toString()
	                    );

	                    if (FilenameUtils.getExtension(objData[6].toString()).equalsIgnoreCase("pdf")
	                            && Files.exists(trlPath)) {

	                        File headerPdf = addHeaderForPdf(
	                                trlPath.toFile(),
	                                objData[12] + " :- TRL table with TRL at sanction stage Annexure"
	                        );

	                        PdfDocument pdf = new PdfDocument(new PdfReader(headerPdf));
	                        merger.merge(pdf, 1, pdf.getNumberOfPages());
	                        pdf.close();

	                        headerPdf.delete();
	                    }
	                }


	                if (TechWorkDataList != null && z < TechWorkDataList.size() && TechWorkDataList.get(z) != null) {

	                    if (FilenameUtils.getExtension(TechWorkDataList.get(z)[8].toString()).equalsIgnoreCase("pdf")) {

	                        Zipper zip = new Zipper();

	                        String tecdata = TechWorkDataList.get(z)[6].toString().replaceAll("[/\\\\]", ",");

	                        String[] fileParts = tecdata.split(",");

	                        String zipName = TechWorkDataList.get(z)[7].toString()
	                                + TechWorkDataList.get(z)[11].toString()
	                                + "-" + TechWorkDataList.get(z)[10].toString() + ".zip";

	                        Path techPath;

	                        if (fileParts.length == 4) {

	                            techPath = Paths.get(env.getProperty("ApplicationFilesDrive"),
	                                    fileParts[0], fileParts[1], fileParts[2], fileParts[3], zipName);

	                        } else {

	                            techPath = Paths.get(env.getProperty("ApplicationFilesDrive"),
	                                    fileParts[0], fileParts[1], fileParts[2], fileParts[3], fileParts[4], zipName);
	                        }

	                        zip.unpack(techPath.toString(), path, TechWorkDataList.get(z)[9].toString());

	                        Path techPdfPath = Paths.get(path, TechWorkDataList.get(z)[8].toString());

	                        if (Files.exists(techPdfPath)) {

	                            File headerPdf = addHeaderForPdf(
	                                    techPdfPath.toFile(),
	                                    objData[12] + " :- Technical Details"
	                            );

	                            PdfDocument pdf = new PdfDocument(new PdfReader(headerPdf));
	                            merger.merge(pdf, 1, pdf.getNumberOfPages());
	                            pdf.close();

	                            headerPdf.delete();
	                            Files.deleteIfExists(techPdfPath);
	                        }
	                    }
	                }

	            } catch (Exception e) {

	                logger.error(new Date() + " Annexure Merge Error " + UserId, e);
	            }

	            z++;
	        }
	        mergedPdf.getDocumentInfo().setTitle("Project Briefing Paper");
	        mergedPdf.close();

	        res.setContentType("application/pdf");
	        res.setHeader("Content-disposition", "inline;filename=" + filename + ".pdf");

	        File f = new File(mergedPath);

	        try (OutputStream out = res.getOutputStream();
	             FileInputStream in = new FileInputStream(f)) {

	            byte[] buffer = new byte[4096];
	            int length;

	            while ((length = in.read(buffer)) > 0) {
	                out.write(buffer, 0, length);
	            }

	            out.flush();
	        }

	        Files.deleteIfExists(Paths.get(pdf1Path));
	        Files.deleteIfExists(Paths.get(pdf2Path));
	        Files.deleteIfExists(Paths.get(mergedPath));

	    } catch (Exception e) {

	        logger.error(new Date() + " Inside ProjectBriefingNewDownload.htm " + UserId, e);
	        e.printStackTrace();
	    }
	}
	
	private File addHeaderForPdf(File inputPdf, String headerText) throws Exception {
	
	    String path = inputPdf.getParent();
	    String filename = inputPdf.getName().replace(".pdf", "");
	
	    PdfDocument pdfDocMain = new PdfDocument(
	            new PdfReader(inputPdf.getAbsolutePath()),
	            new PdfWriter(path + File.separator + filename + "_temp.pdf")
	    );
	
	    Document docMain = new Document(pdfDocMain);
	
	    int pages = pdfDocMain.getNumberOfPages();
	
	    for (int i = 1; i <= pages; i++) {
	
	        PdfPage pageMain = pdfDocMain.getPage(i);
	        Rectangle pageSizeMain = pageMain.getPageSize();
	
	        Paragraph header = new Paragraph(headerText)
	                .setFontSize(10)
	                .setBold();
	
	        float marginLeft = 160;   
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
	


	@RequestMapping(value = "OverallFinanceExcelPgad.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String OverallFinanceExcelPgad(RedirectAttributes redir, HttpServletRequest req, HttpServletResponse res,
			HttpSession ses) throws Exception {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
	        XSSFSheet sheet = workbook.createSheet("PMS_Finance");

	        // ===== Header Style =====
	        CellStyle headerStyle = workbook.createCellStyle();
	        headerStyle.setAlignment(HorizontalAlignment.CENTER);
	        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

	        Font font = workbook.createFont();
	        font.setBold(true);
	        headerStyle.setFont(font);

	        // ===== Header Row =====
	        XSSFRow headerRow = sheet.createRow(0);

	        String[] headers = {
	                "Category Name",
	                "Allotment",
	                "Sanction",
	                "Balance",
	                "SO Value",
	                "Expenditure",
	                "INR",
	                "FE",
	        };

	        for (int i = 0; i < headers.length; i++) {
	            Cell cell = headerRow.createCell(i);
	            cell.setCellValue(headers[i]);
	            cell.setCellStyle(headerStyle);
	        }

	        // ===== Auto-size Columns =====
	        for (int i = 0; i < headers.length; i++) {
	            sheet.autoSizeColumn(i);
	            int currentWidth = sheet.getColumnWidth(i);
	            sheet.setColumnWidth(i, Math.max(currentWidth, 5000)); // minimum width
	        }

	        // ===== Response Setup =====
	        res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	        res.setHeader("Content-Disposition", "attachment; filename=PMS_Finance.xlsx");

	        workbook.write(res.getOutputStream());
	        workbook.close();

		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	@RequestMapping(value = "OverAllFinaceSubmitPgad.htm", method = { RequestMethod.POST, RequestMethod.GET })
	public String OverAllFinaceSubmitPgad(RedirectAttributes redir, HttpServletRequest req, HttpServletResponse res,
			HttpSession ses) throws Exception {

		String LabCode = (String) ses.getAttribute("labcode");
		String UserId = (String) ses.getAttribute("Username");
		String projectid = req.getParameter("projectid");
		String mainprojectid = req.getParameter("mainprojectid");
		String committeeid = req.getParameter("committeeid");
		String scheduleId = req.getParameter("scheduleId");
		Object[] projectDetails = service.ProjectDetails(mainprojectid).get(0);

		String projectCode = projectDetails[1].toString();
		try {

			if (req.getContentType() != null && req.getContentType().startsWith("multipart/")) {

				Part filePart = req.getPart("filename");

				String fileName = filePart.getSubmittedFileName();
				String contentType = filePart.getContentType();

				redir.addAttribute("projectid", projectid);
				redir.addAttribute("committeeid", committeeid);
				if (!isValidExcelFile(fileName, contentType)) {
					redir.addAttribute("resultfail", "Only Excel files (.xls, .xlsx) are allowed");
					return "redirect:/ProjectBriefingPaper.htm";

				}
				List<BriefingFinance> list = new ArrayList<>();
				InputStream fileData = filePart.getInputStream();

				Workbook workbook = new XSSFWorkbook(fileData);
				Sheet sheet = workbook.getSheetAt(0);
				int rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();

				for (int i = 2; i <= rowCount; i++) {
					BriefingFinance pof = new BriefingFinance();
					int cellcount = sheet.getRow(i).getLastCellNum();

					Row row = sheet.getRow(i);
					if (row == null)
						continue;

				
					System.out.println(list.size());
				}

				for (int i = 1; i <= rowCount; i++) {   // start from 1 if row 0 = header

				    Row row = sheet.getRow(i);
				    if (row == null) continue;

				    BriefingFinance pof = new BriefingFinance();

				    // 0 → Category Name
				    Cell cell0 = row.getCell(0);
				    if (cell0 != null) {
				        pof.setCategoryName(cell0.getStringCellValue().trim());
				    }

				    // 1 → Allotment
				    pof.setAllotment(getCellValueAsBigDecimal(row.getCell(1)));

				    // 2 → Sanction
				    pof.setSanction(getCellValueAsBigDecimal(row.getCell(2)));

				    // 3 → Balance
				    pof.setBalance(getCellValueAsBigDecimal(row.getCell(3)));

				    // 4 → SO Value
				    pof.setOutStanding(getCellValueAsBigDecimal(row.getCell(4)));

				    // 5 → Expenditure
				    pof.setExpenditure(getCellValueAsBigDecimal(row.getCell(5)));

				    // 6 → INR
				    pof.setInr(getCellValueAsBigDecimal(row.getCell(6)));

				    // 7 → FE
				    pof.setFe(getCellValueAsBigDecimal(row.getCell(7)));
				    
				    pof.setScheduleId(scheduleId!=null ? Long.parseLong(scheduleId) : 0L); // or setSchedule(entity)
				    pof.setIsActive(1);
				    pof.setCreatedBy(UserId);
				    pof.setCreatedDate(LocalDateTime.now());

				    list.add(pof);
				}
				
				long count = service.addOverallFinaceForPgad(list,scheduleId);

				if (count > 0) {
					redir.addAttribute("result", "Overall Finance Data Added Successfully");
				} else {
					redir.addAttribute("resultfail", "Overall Finance Data Add Unsuccessful");

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/ProjectBriefingPaper.htm";
	}
	/*
	 * private BigDecimal getCellValueAsBigDecimal(Cell cell) {
	 * 
	 * if (cell == null) return BigDecimal.ZERO;
	 * 
	 * switch (cell.getCellType()) {
	 * 
	 * case STRING: String str = cell.getStringCellValue(); if (str != null &&
	 * !str.trim().isEmpty()) { return new BigDecimal(str.replace(",", "").trim());
	 * } break;
	 * 
	 * case NUMERIC: return BigDecimal.valueOf(cell.getNumericCellValue());
	 * 
	 * default: return BigDecimal.ZERO; }
	 * 
	 * return BigDecimal.ZERO; }
	 */
	
	private BigDecimal getCellValueAsBigDecimal(Cell cell) {

	    if (cell == null) return BigDecimal.ZERO;

	    DataFormatter formatter = new DataFormatter();

	    String value = formatter.formatCellValue(cell);

	    if (value != null && !value.trim().isEmpty()) {
	        try {
	            return new BigDecimal(value.replace(",", "").trim());
	        } catch (NumberFormatException e) {
	            return BigDecimal.ZERO;
	        }
	    }

	    return BigDecimal.ZERO;
	}
	
	@RequestMapping(value = "BriefingPaperV2.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String getBriefingPaperV2(HttpServletRequest req, HttpSession ses) {
		try {
			
			setBriefingPaperV2Data(req,ses);
			
			return "print/ProjectBreifingPaperPgadV2";
		}catch (Exception e) {
			e.printStackTrace();
			return "static/error";
		}
	}
	
	public void setBriefingPaperV2Data(HttpServletRequest req, HttpSession ses) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String) ses.getAttribute("labcode");
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String Logintype = (String) ses.getAttribute("LoginType");
		String projectid = req.getParameter("projectid");
		String committeeid = req.getParameter("committeeid");
		String headingid = req.getParameter("headingid");

		List<Object[]> projectslist = service.LoginProjectDetailsList(EmpId, Logintype, LabCode);
		List<Object[]> SpecialCommitteesList = service.SpecialCommitteesList(LabCode);
		List<BriefingHeading> list = service.getBriefingHeading(projectid);

		if (projectslist.isEmpty()) {
			if (projectid == null) {
				return ;
			} else {
				projectslist.addAll(service.ProjectDetails(projectid));
			}
		}

		if (projectid == null && !projectslist.isEmpty()) {
			projectid = projectslist.get(0)[0].toString();
		}

		if (committeeid == null || (committeeid != null && Long.parseLong(committeeid) == 0)) {
			committeeid = SpecialCommitteesList != null && SpecialCommitteesList.size() > 0
					? SpecialCommitteesList.get(0)[0].toString()
					: "0";
		}
		

		List<Object[]> projectattributes = new ArrayList<Object[]>();
		List<List<Object[]>> ebandpmrccount = new ArrayList<List<Object[]>>();
		List<List<Object[]>> ProjectRevList = new ArrayList<List<Object[]>>();
		List<Object[]> projectdatadetails = new ArrayList<Object[]>();
		List<Object[]> ProjectDetails = new ArrayList<Object[]>();
		List<String> Pmainlist = service.ProjectsubProjectIdList(projectid);
		
		for (String proid : Pmainlist) {
			
			Object[] projectattribute = service.ProjectAttributes(proid);
			
			ProjectDetails.add(service.ProjectDetails(proid).get(0));
			ebandpmrccount.add(service.EBAndPMRCCount(proid));
			projectattributes.add(projectattribute);
			ProjectRevList.add(service.ProjectRevList(proid));
			projectdatadetails.add(service.ProjectDataDetails(proid));
		}

		String projectLabCode = service.ProjectDetails(projectid).get(0)[5].toString();

		req.setAttribute("projectLabCode", projectLabCode);
        req.setAttribute("filePath", env.getProperty("ApplicationFilesDrive"));
		req.setAttribute("projectattributes", projectattributes);
		req.setAttribute("ebandpmrccount", ebandpmrccount);
		req.setAttribute("ProjectRevList", ProjectRevList);
		req.setAttribute("projectdatadetails", projectdatadetails);
		req.setAttribute("ProjectDetails", ProjectDetails);
		req.setAttribute("projectidlist", Pmainlist);
		req.setAttribute("SpecialCommitteesList", SpecialCommitteesList);
		req.setAttribute("projectslist", projectslist);
		req.setAttribute("projectid", projectid);
		req.setAttribute("committeeid", committeeid);
		req.setAttribute("headingid", headingid);
		req.setAttribute("headingList", list);
		Object[] lastmeetingVenue = service.getLastcreatedSchedule(projectid, committeeid);
		if(lastmeetingVenue!=null) {
			String scheduleid = lastmeetingVenue[0]!=null ? lastmeetingVenue[0].toString() : null; 
			req.setAttribute("scheduleid", scheduleid); 
			req.setAttribute("lastmeetingVenue", lastmeetingVenue); 
		}
		
	}
	
	
	@RequestMapping(value = "BriefingHeadingSubmit.htm", method = {RequestMethod.GET, RequestMethod.POST})
	public String submitBriefingHeading(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) {
	    try {
	        String UserId = (String) ses.getAttribute("Username");
	        String LabCode = (String) ses.getAttribute("labcode");
	        String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
	        String Logintype = (String) ses.getAttribute("LoginType");

	        String projectid = req.getParameter("projectid");
	        String committeeid = req.getParameter("committeeid");
	        String action = req.getParameter("action");

	        String[] headings = req.getParameterValues("heading");
	        String[] headingIds = req.getParameterValues("headingId");

	        // seniority array
	        String[] seniorityArr = req.getParameterValues("seniority");

	        redir.addAttribute("projectid", projectid);
	        redir.addAttribute("committeeid", committeeid);

	        List<BriefingHeading> list1 = service.getBriefingHeading(projectid);

	        Map<Long, BriefingHeading> map = list1.stream()
	                .collect(Collectors.toMap(BriefingHeading::getHeadingId, h -> h));

	        Set<Long> receivedIds = new HashSet();
	        List<BriefingHeading> listOfHeadings = new ArrayList<>();

	        if (headings != null) {

	            for (int i = 0; i < headings.length; i++) {

	                String headingText = headings[i] != null ? headings[i].trim() : "";
	                if (headingText.isBlank()) continue;

	                //  GET SENIORITY (fallback to index)
	                Integer seniority = (seniorityArr != null && i < seniorityArr.length
	                        && seniorityArr[i] != null && !seniorityArr[i].isBlank())
	                        ? Integer.parseInt(seniorityArr[i])
	                        : (i + 1);

	                if (headingIds != null && i < headingIds.length
	                        && headingIds[i] != null && !headingIds[i].isBlank()) {

	                    Long id = Long.parseLong(headingIds[i]);
	                    receivedIds.add(id);

	                    BriefingHeading existing = map.get(id);

	                    if (existing != null) {
	                        existing.setModifiedBy(UserId);
	                        existing.setModifiedDate(LocalDateTime.now());
	                        existing.setIsActive(1);
	                        existing.setHeading(headingText);

	                        //  SET SENIORITY
	                        existing.setSeniority(seniority);

	                        listOfHeadings.add(existing);
	                    }

	                } else {

	                    BriefingHeading newHeading = new BriefingHeading();
	                    newHeading.setProjectId(Long.parseLong(projectid));
	                    newHeading.setHeading(headingText);
	                    newHeading.setCreatedBy(UserId);
	                    newHeading.setCreatedDate(LocalDateTime.now());
	                    newHeading.setIsActive(1);

	                    //  SET SENIORITY
	                    newHeading.setSeniority(seniority);

	                    listOfHeadings.add(newHeading);
	                }
	            }
	        }

	        //  HANDLE DELETED HEADINGS (soft delete)
	        for (BriefingHeading old : list1) {
	            if (!receivedIds.contains(old.getHeadingId())) {
	                old.setIsActive(0);
	                old.setModifiedBy(UserId);
	                old.setModifiedDate(LocalDateTime.now());
	                listOfHeadings.add(old);
	            }
	        }

	        //  SORT BEFORE SAVE
	        listOfHeadings.sort(Comparator.comparing(BriefingHeading::getSeniority));

	        // Reset proper sequence (no gaps)
		     for (int i = 0; i < listOfHeadings.size(); i++) {
		         listOfHeadings.get(i).setSeniority(i + 1);
		     }
	        long count = service.addHeadings(listOfHeadings, projectid);

	        if ("Add".equalsIgnoreCase(action)) {
	            if (count > 0) {
	                redir.addAttribute("result", "Heading Added Successfully");
	            } else {
	                redir.addAttribute("resultfail", "Heading Add Unsuccessful");
	            }
	        } else {
	            if (count > 0) {
	                redir.addAttribute("result", "Heading Edited Successfully");
	            } else {
	                redir.addAttribute("resultfail", "Heading Edit Unsuccessful");
	            }
	        }

	        return "redirect:/BriefingPaperV2.htm";

	    } catch (Exception e) {
	        e.printStackTrace();
	        return "static/error";
	    }
	}
	
	
//	@RequestMapping(value = "BriefingHeadingSubmit.htm", method = {RequestMethod.GET,RequestMethod.POST})
//	public String submitBriefingHeading(HttpServletRequest req, HttpSession ses,RedirectAttributes redir) {
//		try {
//			String UserId = (String) ses.getAttribute("Username");
//			String LabCode = (String) ses.getAttribute("labcode");
//			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
//			String Logintype = (String) ses.getAttribute("LoginType");
//
//			String projectid = req.getParameter("projectid");
//			String committeeid = req.getParameter("committeeid");
//			String action = req.getParameter("action");
//			String[] headings = req.getParameterValues("heading");
//			String[] headingIds = req.getParameterValues("headingId");
//			redir.addAttribute("projectid", projectid);
//			redir.addAttribute("committeeid", committeeid);
//
//			List<BriefingHeading> list1 = service.getBriefingHeading(projectid);
//			
//			Map<Long,BriefingHeading> map = list1.stream().collect(Collectors.toMap(BriefingHeading::getHeadingId, h -> h));
//			
//			Set<Long> receivedIds = new HashSet<>();
//			List<BriefingHeading> listOfHeadings = new ArrayList<BriefingHeading>();
//			
//			for(int i = 0 ; i < headings.length ; i++) {
//				String headingText = headings[i] != null ? headings[i].trim() : "";
//				if (headingText.isBlank()) continue;
//				if(headingIds!=null && i < headingIds.length &&  headingIds[i]!=null && !headingIds[i].isBlank()) {
//					Long id = Long.parseLong(headingIds[i]);
//		            receivedIds.add(id);
//		            
//		            BriefingHeading existing = map.get(id);
//		            if(existing!=null) {
//		            	existing.setModifiedBy(UserId);
//		            	existing.setModifiedDate(LocalDateTime.now());
//		            	existing.setIsActive(1);
//		            	existing.setHeading(headingText);
//		            	listOfHeadings.add(existing);
//		            }
//				}else {
//		            BriefingHeading newHeading = new BriefingHeading();
//		            newHeading.setProjectId(Long.parseLong(projectid));
//		            newHeading.setHeading(headingText);
//		            newHeading.setCreatedBy(UserId);
//		            newHeading.setCreatedDate(LocalDateTime.now());
//		            newHeading.setIsActive(1);
//	
//		            listOfHeadings.add(newHeading);
//				}
//			}
//			long count = service.addHeadings(listOfHeadings,projectid);
//			
//			if("Add".equalsIgnoreCase(action)) {
//				if (count > 0) {
//					redir.addAttribute("result", "Heading Added Successfully");
//				} else {
//					redir.addAttribute("resultfail", "Heading Add Unsuccessful");
//				}
//			}else {			
//				if (count > 0) {
//					redir.addAttribute("result", "Heading Edited Successfully");
//				} else {
//					redir.addAttribute("resultfail", "Heading Edit Unsuccessful");
//				}
//			}
//			return "redirect:/BriefingPaperV2.htm";
//		}catch (Exception e) {
//			e.printStackTrace();
//			return "static/error";			
//		}
//	}
	
//  public void saveOrUpdateHeadings(String[] headings, String[] headingIds, String projectid, Long userId) {
//
//	    Long projectId = Long.parseLong(projectid);
//
//	    // 👉 Step 1: Get existing headings from DB
//	    List<BriefingHeading> existingList = manager.createQuery(
//	            "FROM BriefingHeading WHERE projectId = :pid AND isActive = 1",
//	            BriefingHeading.class)
//	            .setParameter("pid", projectId)
//	            .getResultList();
//
//	    Map<Long, BriefingHeading> existingMap = existingList.stream()
//	            .collect(Collectors.toMap(BriefingHeading::getHeadingId, h -> h));
//
//	    Set<Long> receivedIds = new HashSet<>();
//
//	    // 👉 Step 2: Process incoming data
//	    for (int i = 0; i < headings.length; i++) {
//
//	        String headingText = headings[i] != null ? headings[i].trim() : "";
//
//	        if (headingText.isBlank()) continue;
//
//	        // ✅ UPDATE existing
//	        if (headingIds != null && i < headingIds.length &&
//	                headingIds[i] != null && !headingIds[i].isBlank()) {
//
//	            Long id = Long.parseLong(headingIds[i]);
//	            receivedIds.add(id);
//
//	            BriefingHeading existing = existingMap.get(id);
//
//	            if (existing != null) {
//	                existing.setHeading(headingText);
//	                existing.setModifiedBy(userId);
//	                existing.setModifiedDate(LocalDateTime.now());
//	                manager.merge(existing);
//	            }
//
//	        } else {
//	            // ✅ INSERT new
//	            BriefingHeading newHeading = new BriefingHeading();
//	            newHeading.setProjectId(projectId);
//	            newHeading.setHeading(headingText);
//	            newHeading.setCreatedBy(userId);
//	            newHeading.setCreatedDate(LocalDateTime.now());
//	            newHeading.setIsActive(1);
//
//	            manager.persist(newHeading);
//	        }
//	    }
//
//	    // 👉 Step 3: Deactivate removed headings ONLY
//	    for (BriefingHeading existing : existingList) {
//	        if (!receivedIds.contains(existing.getHeadingId())) {
//
//	            existing.setIsActive(0);
//	            existing.setModifiedBy(userId);
//	            existing.setModifiedDate(LocalDateTime.now());
//
//	            manager.merge(existing);
//	        }
//	    }
//	}
	
	@RequestMapping(value = "GetHeadingItems.htm",method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody String getHeadingDetails(HttpServletRequest req) {
		List<Object[]> getHeadingDetails = new ArrayList<Object[]>();
		try {
			String projectid = req.getParameter("projectid");
			String committeeid = req.getParameter("committeeid");
			String scheduleid = req.getParameter("scheduleid");
			String headingid = req.getParameter("headingid");
			getHeadingDetails = service.getHeadingDetails(headingid,projectid,scheduleid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Gson json = new Gson();
		return json.toJson(getHeadingDetails);
	}
	
	
	@RequestMapping(value = "DetailsAddEdit.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String addEditDetails(HttpServletRequest req) {
		try {
			String projectid = req.getParameter("projectid");
			String committeeid = req.getParameter("committeeid");
			String scheduleid = req.getParameter("scheduleid");
			String headingid = req.getParameter("headingid");
			String detailid = req.getParameter("detailsid");
						
			String headDetails = req.getParameter("headDetails");
			Object[] lastmeetingVenue = service.getLastcreatedSchedule(projectid, committeeid);
			
			
			if(detailid!=null && !detailid.isBlank())req.setAttribute("detailData", service.getDetailsById(detailid));
			
			req.setAttribute("headDetails", headDetails);
			req.setAttribute("lastmeetingVenue", lastmeetingVenue);
			req.setAttribute("committeeid", committeeid);
			req.setAttribute("projectid", projectid);
			req.setAttribute("scheduleid", scheduleid);
			req.setAttribute("headingid", headingid);
			

			return "print/ProjectBriefingHeadingdetailsAddEdit";
			
		}catch (Exception e) {
			e.printStackTrace();
			return "static/error";
		}
	}
	
	@RequestMapping(value = "DetailsAddEditSubmit.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String addEditDetails(HttpServletRequest req,RedirectAttributes redir,HttpSession ses) {
		try {
			String projectid = req.getParameter("projectid");
			String committeeid = req.getParameter("committeeid");
			String scheduleid = req.getParameter("scheduleid");
			String headingid = req.getParameter("headingid");
			String detailid = req.getParameter("detailsid");
			String content = req.getParameter("content");
			
			String UserId = (String) ses.getAttribute("Username");
			String LabCode = (String) ses.getAttribute("labcode");
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String Logintype = (String) ses.getAttribute("LoginType");
			
			redir.addAttribute("projectid",projectid);
			redir.addAttribute("committeeid",committeeid);
			redir.addAttribute("scheduleid",scheduleid);
			redir.addAttribute("headingid",headingid);
			

			if(detailid==null || "null".equalsIgnoreCase(detailid) || detailid.trim().isBlank()) {
				BriefingHeadingDetails dto = new BriefingHeadingDetails();
				dto.setHeadingId(headingid!=null ? Long.parseLong(headingid) : null);
				dto.setCreatedBy(UserId);
				dto.setCreatedDate(LocalDateTime.now());
				dto.setDetails(content);
				dto.setIsActive(1);
				dto.setScheduleId(scheduleid!=null ? Long.parseLong(scheduleid) : null);
				
				long count = service.addDetails(dto);
				
				if (count > 0) {
					redir.addAttribute("result", "Detail Added Successfully");
					redir.addAttribute("detailsid",count);
				} else {
					redir.addAttribute("resultfail", "Detail Edit Unsuccessful");
				}
				
				return "redirect:/BriefingPaperV2.htm";		
			}
			
			
			BriefingHeadingDetails dto = service.getDetailsById(detailid);
			dto.setModifiedBy(UserId);
			dto.setModifiedDate(LocalDateTime.now());
			dto.setDetails(content);
			dto.setIsActive(1);			

			long count = service.addDetails(dto);
			redir.addAttribute("detailsid",detailid);
			if (count > 0) {
				redir.addAttribute("result", "Detail Edited Successfully");
			} else {
				redir.addAttribute("resultfail", "Detail Edit Unsuccessful");
			}
			return "redirect:/BriefingPaperV2.htm";			
		}catch (Exception e) {
			e.printStackTrace();
			return "static/error";
		}
	}
	
	@RequestMapping(value = "DetailsDeleteSubmit.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String deleteDetails(HttpServletRequest req,HttpSession ses,RedirectAttributes redir) {
		try {

			String projectid = req.getParameter("projectid");
			String committeeid = req.getParameter("committeeid");
			String scheduleid = req.getParameter("scheduleid");
			String headingid = req.getParameter("headingid");
			String detailid = req.getParameter("detailsid");

			String UserId = (String) ses.getAttribute("Username");
			String LabCode = (String) ses.getAttribute("labcode");
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String Logintype = (String) ses.getAttribute("LoginType");
			
			redir.addAttribute("projectid",projectid);
			redir.addAttribute("committeeid",committeeid);
			redir.addAttribute("scheduleid",scheduleid);
			redir.addAttribute("headingid",headingid);
			
			BriefingHeadingDetails dto = service.getDetailsById(detailid);
			dto.setModifiedBy(UserId);
			dto.setModifiedDate(LocalDateTime.now());
			dto.setIsActive(0);			
			long count = service.addDetails(dto);
			if (count > 0) {
				redir.addAttribute("result", "Detail Deleted Successfully");
			} else {
				redir.addAttribute("resultfail", "Detail Delete Unsuccessful");
			}
			return "redirect:/BriefingPaperV2.htm";
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	@RequestMapping(value = "BriefingPaperV2Download.htm", method = {RequestMethod.POST,RequestMethod.GET})
	public void downloadNewBriefingPaperV2(HttpServletRequest req, HttpSession ses, HttpServletResponse res) {
		try{

			String UserId = (String) ses.getAttribute("Username");
		    String LabCode = (String) ses.getAttribute("labcode");

	        String projectid = req.getParameter("projectid");
	        String committeeid = req.getParameter("committeeid");

	        Committee committee = service.getCommitteeData(committeeid);
			
			setBriefingPaperV2Data(req,ses);


	        String projectLabCode = service.ProjectDetails(projectid).get(0)[5].toString();
	        String CommitteeCode = committee.getCommitteeShortName().trim();

	        List<Object[]> SpecialCommitteesList = service.SpecialCommitteesList(LabCode);
	        Map<String, List<Object[]>> reviewMeetingListMap = new HashMap<>();

	        for (Object[] obj : SpecialCommitteesList) {
	            reviewMeetingListMap.put(obj[1] + "", service.ReviewMeetingList(projectid, obj[1] + ""));
	        }

	        req.setAttribute("reviewMeetingListMap", reviewMeetingListMap);

	        
	        req.setAttribute("text", req.getParameter("text"));
	        req.setAttribute("IsIbasConnected", IsIbasConnected);
	        req.setAttribute("committeeData", committee);
	        req.setAttribute("projectid", projectid);
	        req.setAttribute("committeeid", committeeid);
	        req.setAttribute("ProjectCost", ProjectCost);
	        req.setAttribute("isprint", "0");
	        req.setAttribute("AppFilesPath", ApplicationFilesDrive);
	        req.setAttribute("projectLabCode", projectLabCode);
	        req.setAttribute("labInfo", service.LabDetailes(projectLabCode));
	        req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(projectLabCode));
	        req.setAttribute("thankYouImg", LogoUtil.getThankYouImageAsBase64String());
	        req.setAttribute("filePath", env.getProperty("ApplicationFilesDrive"));
	        req.setAttribute("ApplicationFilesDrive", env.getProperty("ApplicationFilesDrive"));

			Object[] lastmeetingVenue = service.getLastcreatedSchedule(projectid, committeeid);
			req.setAttribute("lastmeetingVenue", lastmeetingVenue);
			if(lastmeetingVenue!=null) {
		        List<Object[]> details = service.getAllHeadingDetails(projectid,lastmeetingVenue[0].toString());
		        if(details!=null ) {
		        	Map<String,List<Object[]>> headMap = details
		        			.stream()
		        			.collect(
		        					Collectors
		        					.groupingBy(
		        							row -> {
		        								return row[2]!=null ? row[2].toString() : "-1";
		        							},
		        							LinkedHashMap::new,
		        							Collectors.toList()
		        							));
			        req.setAttribute("headingDetails", headMap);
		        }
			}

	        req.setAttribute("committeeMetingsCount",
	                service.ProjectCommitteeMeetingsCount(projectid, "0", "0", "0", "0", CommitteeCode));



			List<Object[]> list = service.getMilestoneBriefingList(lastmeetingVenue[0].toString());
			Map<String,List<Object[]>> milestoneBriefingMap = list!=null ? list.stream().collect(Collectors.groupingBy(obj -> obj[1]!=null?obj[1].toString():"UNKNOWN",LinkedHashMap::new,Collectors.toList())) : new LinkedHashMap<String, List<Object[]>>();
			req.setAttribute("milestoneBriefingMap", milestoneBriefingMap);
			req.setAttribute("recdecDetails", service.GetRecDecDetails(lastmeetingVenue[0].toString()));
			req.setAttribute("briefingFinanceDetials", service.getBriefingFinanceDetails(lastmeetingVenue[0].toString()));
			

	        req.setAttribute("RiskTypes", service.RiskTypes());

	        Object[] mileStoneLevelId = service.MileStoneLevelId(projectid, committeeid);
	        req.setAttribute("levelid", mileStoneLevelId != null ? mileStoneLevelId[0].toString() : "2");

	        processProjectData(req, projectid, committeeid, uri, projectLabCode, UserId, IsIbasConnected,ses);

	        List<Object[]> projectdatadetails = (List<Object[]>) req.getAttribute("projectdatadetails");
	        List<List<Object[]>> ebandpmrccount = (List<List<Object[]>>) req.getAttribute("ebandpmrccount");
	        List<Object[]> TechWorkDataList = (List<Object[]>) req.getAttribute("TechWorkDataList");

	        milestoneLevelDataMap(req, reviewMeetingListMap, projectid, committee.getCommitteeShortName().trim());
	        
	        String filename = "BriefingPaper";

	        String path = req.getServletContext().getRealPath("/view/temp");
	        req.setAttribute("path", path);

	        CharArrayWriterResponse response1 = new CharArrayWriterResponse(res);
	        req.getRequestDispatcher("/view/print/BriefingPaperNewpgadV2.jsp").forward(req, response1);

	        String html1 = response1.getOutput();

	        String pdf1Path = path + File.separator + "briefing_" + UserId + ".pdf";

	        HtmlConverter.convertToPdf(html1, new FileOutputStream(pdf1Path));

	        CharArrayWriterResponse response2 = new CharArrayWriterResponse(res);
	        req.getRequestDispatcher("/view/print/ActionTablePgad.jsp").forward(req, response2);

	        String html2 = response2.getOutput();

	        String pdf2Path = path + File.separator + "actiontable_" + UserId + ".pdf";

	        HtmlConverter.convertToPdf(html2, new FileOutputStream(pdf2Path));

	        String mergedPath = path + File.separator + filename + "_" + UserId + ".pdf";

	        PdfDocument mergedPdf = new PdfDocument(new PdfWriter(mergedPath));
	        PdfMerger merger = new PdfMerger(mergedPdf);

	        PdfDocument pdf1 = new PdfDocument(new PdfReader(pdf1Path));
	        PdfDocument pdf2 = new PdfDocument(new PdfReader(pdf2Path));

	        merger.merge(pdf1, 1, pdf1.getNumberOfPages());
	        merger.merge(pdf2, 1, pdf2.getNumberOfPages());

	        pdf1.close();
	        pdf2.close();

	        int z = 0;

	        for (Object[] objData : projectdatadetails) {

	            try {

	                if (objData != null) {

	                    String No2 = null;

	                    if (CommitteeCode.equalsIgnoreCase("PMRB")) {
	                        No2 = "P" + (Long.parseLong(ebandpmrccount.get(0).get(0)[1].toString()) + 1);
	                    } 
	                    else if (CommitteeCode.equalsIgnoreCase("EB")) {
	                        No2 = "E" + (Long.parseLong(ebandpmrccount.get(0).get(1)[1].toString()) + 1);
	                    }

	                    String fileName = "grantt_" + objData[1].toString() + "_" + No2 + ".pdf";

	                    Path ganttPath = Paths.get(
	                            env.getProperty("ApplicationFilesDrive"),
	                            projectLabCode,
	                            "gantt",
	                            fileName
	                    );

	                    if (Files.exists(ganttPath)) {

	                        File headerPdf = addHeaderForPdf(
	                                ganttPath.toFile(),
	                                objData[12] + " :- Gantt Chart"
	                        );

	                        PdfDocument pdf = new PdfDocument(new PdfReader(headerPdf));
	                        merger.merge(pdf, 1, pdf.getNumberOfPages());
	                        pdf.close();

	                        headerPdf.delete();
	                    }
	                }


	                if (objData != null && objData[3] != null) {

	                    Path sysPath = Paths.get(
	                            env.getProperty("ApplicationFilesDrive"),
	                            projectLabCode,
	                            "ProjectData",
	                            objData[3].toString()
	                    );

	                    if (FilenameUtils.getExtension(objData[3].toString()).equalsIgnoreCase("pdf")
	                            && Files.exists(sysPath)) {

	                        File headerPdf = addHeaderForPdf(
	                                sysPath.toFile(),
	                                objData[12] + " :- System Configuration Annexure"
	                        );

	                        PdfDocument pdf = new PdfDocument(new PdfReader(headerPdf));
	                        merger.merge(pdf, 1, pdf.getNumberOfPages());
	                        pdf.close();

	                        headerPdf.delete();
	                    }
	                }



	                if (objData != null && objData[4] != null) {

	                    Path specPath = Paths.get(
	                            env.getProperty("ApplicationFilesDrive"),
	                            projectLabCode,
	                            "ProjectData",
	                            objData[4].toString()
	                    );

	                    if (FilenameUtils.getExtension(objData[4].toString()).equalsIgnoreCase("pdf")
	                            && Files.exists(specPath)) {

	                        File headerPdf = addHeaderForPdf(
	                                specPath.toFile(),
	                                objData[12] + " :- System Specification Annexure"
	                        );

	                        PdfDocument pdf = new PdfDocument(new PdfReader(headerPdf));
	                        merger.merge(pdf, 1, pdf.getNumberOfPages());
	                        pdf.close();

	                        headerPdf.delete();
	                    }
	                }


	                if (objData != null && objData[5] != null) {

	                    Path treePath = Paths.get(
	                            env.getProperty("ApplicationFilesDrive"),
	                            projectLabCode,
	                            "ProjectData",
	                            objData[5].toString()
	                    );

	                    if (FilenameUtils.getExtension(objData[5].toString()).equalsIgnoreCase("pdf")
	                            && Files.exists(treePath)) {

	                        File headerPdf = addHeaderForPdf(
	                                treePath.toFile(),
	                                objData[12] + " :- Overall Product tree/WBS Annexure"
	                        );

	                        PdfDocument pdf = new PdfDocument(new PdfReader(headerPdf));
	                        merger.merge(pdf, 1, pdf.getNumberOfPages());
	                        pdf.close();

	                        headerPdf.delete();
	                    }
	                }


	                if (objData != null && objData[6] != null) {

	                    Path trlPath = Paths.get(
	                            env.getProperty("ApplicationFilesDrive"),
	                            projectLabCode,
	                            "ProjectData",
	                            objData[6].toString()
	                    );

	                    if (FilenameUtils.getExtension(objData[6].toString()).equalsIgnoreCase("pdf")
	                            && Files.exists(trlPath)) {

	                        File headerPdf = addHeaderForPdf(
	                                trlPath.toFile(),
	                                objData[12] + " :- TRL table with TRL at sanction stage Annexure"
	                        );

	                        PdfDocument pdf = new PdfDocument(new PdfReader(headerPdf));
	                        merger.merge(pdf, 1, pdf.getNumberOfPages());
	                        pdf.close();

	                        headerPdf.delete();
	                    }
	                }


	                if (TechWorkDataList != null && z < TechWorkDataList.size() && TechWorkDataList.get(z) != null) {

	                    if (FilenameUtils.getExtension(TechWorkDataList.get(z)[8].toString()).equalsIgnoreCase("pdf")) {

	                        Zipper zip = new Zipper();

	                        String tecdata = TechWorkDataList.get(z)[6].toString().replaceAll("[/\\\\]", ",");

	                        String[] fileParts = tecdata.split(",");

	                        String zipName = TechWorkDataList.get(z)[7].toString()
	                                + TechWorkDataList.get(z)[11].toString()
	                                + "-" + TechWorkDataList.get(z)[10].toString() + ".zip";

	                        Path techPath;

	                        if (fileParts.length == 4) {

	                            techPath = Paths.get(env.getProperty("ApplicationFilesDrive"),
	                                    fileParts[0], fileParts[1], fileParts[2], fileParts[3], zipName);

	                        } else {

	                            techPath = Paths.get(env.getProperty("ApplicationFilesDrive"),
	                                    fileParts[0], fileParts[1], fileParts[2], fileParts[3], fileParts[4], zipName);
	                        }

	                        zip.unpack(techPath.toString(), path, TechWorkDataList.get(z)[9].toString());

	                        Path techPdfPath = Paths.get(path, TechWorkDataList.get(z)[8].toString());

	                        if (Files.exists(techPdfPath)) {

	                            File headerPdf = addHeaderForPdf(
	                                    techPdfPath.toFile(),
	                                    objData[12] + " :- Technical Details"
	                            );

	                            PdfDocument pdf = new PdfDocument(new PdfReader(headerPdf));
	                            merger.merge(pdf, 1, pdf.getNumberOfPages());
	                            pdf.close();

	                            headerPdf.delete();
	                            Files.deleteIfExists(techPdfPath);
	                        }
	                    }
	                }

	            } catch (Exception e) {

	                logger.error(new Date() + " Annexure Merge Error " + UserId, e);
	            }

	            z++;
	        }
	        mergedPdf.getDocumentInfo().setTitle("Project Briefing Paper");
	        mergedPdf.close();

	        res.setContentType("application/pdf");
	        res.setHeader("Content-disposition", "inline;filename=" + filename + ".pdf");

	        File f = new File(mergedPath);

	        try (OutputStream out = res.getOutputStream();
	             FileInputStream in = new FileInputStream(f)) {

	            byte[] buffer = new byte[4096];
	            int length;

	            while ((length = in.read(buffer)) > 0) {
	                out.write(buffer, 0, length);
	            }

	            out.flush();
	        }

	        Files.deleteIfExists(Paths.get(pdf1Path));
	        Files.deleteIfExists(Paths.get(pdf2Path));
	        Files.deleteIfExists(Paths.get(mergedPath));
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "CopyHeadingsFromLastMeeting.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String copyHeadingFromLastMeeting(HttpServletRequest req,HttpSession ses) {
		try {
			String UserId =(String)ses.getAttribute("Username");
			String LabCode =(String)ses.getAttribute("labcode");
			String scheduleid = req.getParameter("scheduleid");
			String meetingid = req.getParameter("meetingid");
			String scheduleidfrom=req.getParameter("scheduleidfrom");
			String projectid = req.getParameter("projectid");
			String committeeid = req.getParameter("committeeid");
			String search = req.getParameter("search");
			
			if(search!=null)
			{
				req.setAttribute("meetingsearch", comservice.MeetingSearchList(search,LabCode));					
			}

	
			if(scheduleidfrom!=null)
			{
				req.setAttribute("committeescheduledata1",comservice.CommitteeScheduleEditData(scheduleidfrom));
				req.setAttribute("headingList",service.getBriefingHeading(projectid));
				req.setAttribute("meetingid",meetingid);
				req.setAttribute("scheduleidfrom",scheduleidfrom);
			}			
	
			req.setAttribute("scheduleidto",scheduleid);
			req.setAttribute("committeescheduleeditdata",comservice.CommitteeScheduleEditData(scheduleid));
			req.setAttribute("projectid", projectid);
			req.setAttribute("committeeid", committeeid);
			return "print/BriefingHeadingCopy";
		}catch (Exception e) {
			e.printStackTrace();
			return "static/error";
		}
	}
	
	@RequestMapping(value = "CopyHeadingsList.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String CommitteePreviousAgendaAddSubmit(HttpServletRequest req,HttpServletResponse res,HttpSession ses,RedirectAttributes redir)
	{
		String UserId =(String)ses.getAttribute("Username");
		logger.info(new Date() +"Inside CopyHeadingsList.htm "+UserId);		
		try {
			String scheduleidto=req.getParameter("scheduleidto");
			String scheduleidfrom=req.getParameter("scheduleidfrom");
			String[] headingIds=req.getParameterValues("headingId");
			String projectid = req.getParameter("projectid");
			String committeeid = req.getParameter("committeeid");


			long count=service.previouseHeadingAdd(scheduleidto,scheduleidfrom, headingIds, UserId);
			if (count > 0) {
				redir.addAttribute("result", "Heading From Previous Meeting Added Successfull");
			} else {
				redir.addAttribute("resultfail", "Heading From Previous Meeting Add Unsuccessfull");
			}			
			redir.addAttribute("scheduleid",scheduleidto);		
			redir.addAttribute("projectid", projectid);
			redir.addAttribute("committeeid", committeeid);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" Inside CopyHeadingsList.htm "+UserId, e);
		}
		return "redirect:/BriefingPaperV2.htm";
	}
	
	@RequestMapping(value = "UpdateRemarksFromBriefingPaper.htm", method = RequestMethod.POST)
	@ResponseBody
	public String updateRemarks(HttpServletRequest req, HttpSession ses) {

	    String userId = (String) ses.getAttribute("Username");

	    try {

	        String id = req.getParameter("id");
	        String remarks = req.getParameter("remarks");
	        if(id == null || id.isBlank()) {
	        	id = "0";
	        }

	        // Validation
//	        if (InputValidator.isContainsHTMLTags(remarks)) {
//	            return "Invalid Remarks";
//	        }

	        // Call service
	        int count = service.updateRemarks(Long.parseLong(id), remarks, userId);
	        
	        if (count > 0) {
	            return "Update success";
	        } else {
	            return "Update failed";
	        }

	    } catch (Exception e) {
	        logger.error(new Date() + " Inside UpdateRemarks.htm " + userId, e);
	        e.printStackTrace();
	        return "error";
	    }
	}

	@RequestMapping(value="ProjectBriefingWordDownload.htm")
	public String ProjectBriefingWordDownload(HttpServletRequest req, HttpSession ses, HttpServletResponse res)	throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside ProjectBriefingWordDownload.htm "+UserId);		
	    try {
	    	String projectid=req.getParameter("projectid");
	    	String committeeid= req.getParameter("committeeid");
	    	
	    	Committee committee = service.getCommitteeData(committeeid);
	    	
	    	String projectLabCode = service.ProjectDetails(projectid).get(0)[5].toString();
	    	String CommitteeCode = committee.getCommitteeShortName().trim();
	    	
	    	if(LabCode.equalsIgnoreCase("ADE")) {
	    		req.setAttribute("otherMeetingList", service.otherMeetingList(projectid));
	    	}
	    	List<Object[]> SpecialCommitteesList =  service.SpecialCommitteesList(LabCode);
	    	Map<String, List<Object[]>> reviewMeetingListMap = new HashMap<String, List<Object[]>>();
			for(Object[] obj : SpecialCommitteesList) {
				reviewMeetingListMap.put(obj[1]+"", service.ReviewMeetingList(projectid, obj[1]+""));
			}
	    	req.setAttribute("reviewMeetingListMap",reviewMeetingListMap);

	    	req.setAttribute("text", req.getParameter("text"));
	    	req.setAttribute("IsIbasConnected", IsIbasConnected);
	    	req.setAttribute("committeeData", committee);
    		req.setAttribute("projectid",projectid);
    		req.setAttribute("committeeid",committeeid);
    		req.setAttribute("ProjectCost",ProjectCost);
	    	req.setAttribute("isprint", "0");
	    	req.setAttribute("AppFilesPath",ApplicationFilesDrive);
	    	req.setAttribute("projectLabCode",projectLabCode);
	    	req.setAttribute("labInfo", service.LabDetailes(projectLabCode));
	    	req.setAttribute("lablogo", LogoUtil.getLabLogoAsBase64String(projectLabCode));  
	    	req.setAttribute("thankYouImg", LogoUtil.getThankYouImageAsBase64String());
            req.setAttribute("filePath", env.getProperty("ApplicationFilesDrive"));
    		req.setAttribute("ApplicationFilesDrive",env.getProperty("ApplicationFilesDrive"));
    		req.setAttribute("committeeMetingsCount", service.ProjectCommitteeMeetingsCount(projectid, "0", "0", "0", "0", CommitteeCode) );
    		Object[] nextmeetVenue = (Object[])service.BriefingMeetingVenue(projectid, committeeid);
	    	req.setAttribute("nextMeetVenue", nextmeetVenue);
	    	Object[] lastmeetingVenue = service.getLastcreatedSchedule(projectid, committeeid);
			req.setAttribute("lastmeetingVenue", lastmeetingVenue);
			if (nextmeetVenue != null && nextmeetVenue[0] != null) {
				req.setAttribute("recdecDetails", service.GetRecDecDetails(nextmeetVenue[0].toString()));
			}
			if(lastmeetingVenue!=null && lastmeetingVenue[0]!=null && LabCode.equalsIgnoreCase("PGAD")) {
				List<Object[]> list = service.getMilestoneBriefingList(lastmeetingVenue[0].toString());
				Map<String,List<Object[]>> milestoneBriefingMap = list!=null ? list.stream().collect(Collectors.groupingBy(obj -> obj[1]!=null?obj[1].toString():"UNKNOWN",LinkedHashMap::new,Collectors.toList())) : new LinkedHashMap<String, List<Object[]>>();
				req.setAttribute("milestoneBriefingMap", milestoneBriefingMap);
				req.setAttribute("recdecDetails", service.GetRecDecDetails(lastmeetingVenue[0].toString()));
			}
	    	req.setAttribute("RiskTypes", service.RiskTypes());
    		
    		Object[] mileStoneLevelId = service.MileStoneLevelId(projectid,committeeid);
			req.setAttribute("levelid", mileStoneLevelId!=null?mileStoneLevelId[0].toString():"2");
			
			// Project Data
			processProjectData(req, projectid, committeeid, uri, projectLabCode, UserId, IsIbasConnected, ses);
			
			// Milestone Data for Committee
			milestoneLevelDataMap(req, reviewMeetingListMap, projectid, committee.getCommitteeShortName().trim());
			
			req.setAttribute("wordFlag", "Y");
			
			return "print/BriefingPaperNew1";
	    } catch(Exception e) {    		
    		logger.error(new Date() +" Inside ProjectBriefingWordDownload.htm "+UserId, e);
    		e.printStackTrace();
			return "static/Error"; 
	
    	}		
	}
}