package com.vts.pfms.pfts.controller;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.google.gson.Gson;
import com.vts.pfms.FormatConverter;
import com.vts.pfms.master.dto.DemandDetails;
import com.vts.pfms.pfmsserv.feign.PFMSServeFeignClient;
import com.vts.pfms.pfts.dto.DemandOrderDetails;
import com.vts.pfms.pfts.dto.PFTSFileDto;
import com.vts.pfms.pfts.model.PFTSFile;
import com.vts.pfms.pfts.model.PftsFileMilestone;
import com.vts.pfms.pfts.model.PftsFileMilestoneRev;
import com.vts.pfms.pfts.model.PftsFileOrder;
import com.vts.pfms.pfts.service.PFTSService;
import com.vts.pfms.utils.InputValidator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@Controller
public class PFTSController {

	@Autowired
	PFTSService service;
	
//	private static final RestTemplate restTemplate = new RestTemplate();
	
	@Value("${server_uri}")
    private String uri;
	
	@Autowired
	PFMSServeFeignClient serv;
	
	private static final Logger logger=LogManager.getLogger(PFTSController.class);
	FormatConverter fc = new FormatConverter();
	SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
	private  SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat sdf1= fc.getSqlDateAndTimeFormat();
	private SimpleDateFormat sdf2=fc.getRegularDateFormat();/*new SimpleDateFormat("dd-MM-yyyy");*/
	private SimpleDateFormat sdf3=fc.getSqlDateFormat();

	
	@RequestMapping(value = "ProcurementStatus.htm" ,method= {RequestMethod.GET,RequestMethod.POST})
	public String procurement(HttpServletRequest req, HttpSession ses,RedirectAttributes redir) throws Exception 
	{
		
		String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		String Logintype= (String)ses.getAttribute("LoginType");
		String projectId =req.getParameter("projectid");
		String projectCode = req.getParameter("projectCode");
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside ProcurementStatus.htm "+UserId);		
		try {
			List<Object[]> projectlist=service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
			
			if(projectlist.size()==0) 
		    {				
				redir.addFlashAttribute("resultfail", "No Project is Assigned to you.");
				return "redirect:/MainDashBoard.htm";
			}
			
			if(projectId==null) 
			{
				projectId=projectlist.get(0)[0].toString();
			}
			
			req.setAttribute("projectslist",projectlist );
			req.setAttribute("fileStatusList",service.getFileStatusList(projectId));
			req.setAttribute("pftsStageList", service.getpftsStageList());
			req.setAttribute("pftsMilestoneList", service.getpftsMilestoneList());
			req.setAttribute("projectId",projectId);			
			if(projectCode==null) {
				projectCode = service.ProjectData(projectId)[1].toString();
			}
			req.setAttribute("projectcode",projectCode);
		}catch (Exception e) 
		{			
			e.printStackTrace(); 
			logger.error(new Date() +" Inside ProcurementStatus.htm "+UserId, e); 
			return "static/Error";	
		}	
		
		return "pfts/FileStatus";
	}
	
	@RequestMapping(value="AddNewDemandFile.htm", method=RequestMethod.POST)
	public String addNewDemandFile(HttpServletRequest req, HttpSession ses) throws Exception
	{
		
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside AddNewDemandFile.htm "+UserId);		
		try {
			String projectId =req.getParameter("projectId");
			String projectcode = service.ProjectData(projectId)[1].toString();
			
			
			//final String localUri=uri+"/pfms_serv/newDemandsDetails?projectcode="+projectcode;
			List<DemandDetails> demandList=null;
			try {
				demandList = serv.DemandsDetails(projectcode);	
			}catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
//	 		HttpHeaders headers = new HttpHeaders();
//	 		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON)); 
//	 		headers.set("labcode", LabCode);
//	 		String jsonResult=null;
//			try {
//				HttpEntity<String> entity = new HttpEntity<String>(headers);
//				ResponseEntity<String> response=restTemplate.exchange(localUri, HttpMethod.POST, entity, String.class);
//				jsonResult=response.getBody();
//		
//			}catch(HttpClientErrorException e) {
//				req.setAttribute("errorMsg", "errorMsg");
//			}
//			ObjectMapper mapper = new ObjectMapper();
//			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//			mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
//			if(jsonResult!=null) {
//				try {
//				
//					demandList = mapper.readValue(jsonResult, new TypeReference<List<DemandDetails>>(){});
//				} catch (JsonProcessingException e) {
//	
//					e.printStackTrace();
//				}
//			}
			
		    List<DemandDetails> prevDemandFile=service.getprevDemandFile(projectId);
		    if(demandList!=null && prevDemandFile!=null) {
		    	demandList.removeAll(prevDemandFile);
		    }
	        req.setAttribute("demandList",demandList);
	        req.setAttribute("projectId",projectId);
	        req.setAttribute("projectCode", projectcode);
	        return "pfts/AddNewDemandFile";
		}catch (Exception e) 
		{			
			e.printStackTrace(); 
			logger.error(new Date() +" Inside AddNewDemandFile.htm "+UserId, e); 
			return "static/Error";	
		}	
		
	} 
	
	@RequestMapping(value="AddDemandFileSubmit.htm", method=RequestMethod.POST)
	public String addDemandFileSubmit(HttpServletRequest req, RedirectAttributes redir, HttpSession ses) throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside AddDemandFileSubmit.htm "+UserId);		
		try {
			String projectCode=req.getParameter("projectCode");
			Object[] projectData = service.ProjectDataByPrjCode(projectCode);
			String projectId=projectData[0].toString();
			PFTSFile pf=new PFTSFile();
			pf.setDemandNo(req.getParameter("DemandNo"));
			pf.setDemandType("I");
			pf.setProjectId(Long.parseLong(projectId));
			pf.setDemandDate(new java.sql.Date(sdf.parse(req.getParameter("demandDate")).getTime()));
			pf.setItemNomenclature(req.getParameter("ItemNomcl"));
			pf.setEstimatedCost(Double.parseDouble(req.getParameter("Estimtedcost")));
			pf.setPftsStatusId(1l);
			pf.setRemarks("Nil");
			pf.setIsActive(1);
			
//			List<DemandDetails> prevDemandFile=service.getprevDemandFile(projectId);
			
			Long result=service.addDemandfile(pf);
			
			if(result>0) {
				redir.addAttribute("result","Demand Added Successfully ");
			}else {
				redir.addAttribute("resultfail","Something went worng");
			}
			
			redir.addAttribute("projectid",projectId);
			redir.addAttribute("projectCode",projectCode);
			return  "redirect:/ProcurementStatus.htm";
		}catch (Exception e) 
		{			
			e.printStackTrace(); 
			logger.error(new Date() +" Inside AddDemandFileSubmit.htm "+UserId, e); 
			return "static/Error";	
		}
	}
   	
	
	@RequestMapping(value = "getStatusEvent.htm", method = RequestMethod.GET)
	public @ResponseBody String getStatusEvent(HttpServletRequest request, HttpSession ses) throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside getStatusEvent.htm "+UserId);		
		try {
				String fileid=request.getParameter("fileid");
				List<Object[]> statusList=service.getStatusList(fileid);
				Gson json = new Gson();
				return json.toJson(statusList);
		}catch (Exception e) 
		{			
			e.printStackTrace(); 
			logger.error(new Date() +" Inside getStatusEvent.htm "+UserId, e); 
			return null;
		}

	}
	
	@RequestMapping(value="upadteDemandFile.htm", method=RequestMethod.POST)
	public String upadteDemandFile(HttpServletRequest req, RedirectAttributes redir, HttpSession ses) throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");
		logger.info(new Date() +"Inside upadteDemandFile.htm "+UserId);		
		try {
			
             String statusId=req.getParameter("statusId");
             String projectId=req.getParameter("projectId");
             String eventDate=req.getParameter("eventDate");
             String fileId=req.getParameter("fileId");
             String remarks=req.getParameter("remarks");
             String demandNo=req.getParameter("demandNo");
             
             redir.addFlashAttribute("projectid",projectId);
             
             if(statusId.equals("10")) {
            	   	//final String localUri=uri+"/pfms_serv/newDemandsOrderDetails?demandNo="+demandNo;
    				List<DemandOrderDetails> demandOrderList=null;
//    		 		HttpHeaders headers = new HttpHeaders();
//    		 		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON)); 
//    		 		headers.set("labcode", LabCode);
//    		 		String jsonResult=null;
//    				try {
//    					HttpEntity<String> entity = new HttpEntity<String>(headers);
//    					ResponseEntity<String> response=restTemplate.exchange(localUri, HttpMethod.POST, entity, String.class);
//    					jsonResult=response.getBody();
//    			
//    				}catch(HttpClientErrorException e) {
//    					redir.addFlashAttribute("resultfail","Order not placed in IBAS");
//						return  "redirect:/ProcurementStatus.htm";
//    				}
//    				ObjectMapper mapper = new ObjectMapper();
//    				mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//    				mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
//    			
//    				if(jsonResult!=null && jsonResult.length()>2) {
//    					try {
//    					
//    						demandOrderList = mapper.readValue(jsonResult, new TypeReference<List<DemandOrderDetails>>(){});
//    					} catch (JsonProcessingException e) {
//    						redir.addAttribute("resultfail","Order not placed in IBAS");
//    						return  "redirect:/ProcurementStatus.htm";
//    					}
//    				}else {
//    					redir.addAttribute("resultfail","Order not placed in IBAS");
//						return  "redirect:/ProcurementStatus.htm";
//    				}
    				
    				try {
    					demandOrderList = serv.DemandsOrderDetails(demandNo);
    				}catch(Exception e) {
    					redir.addAttribute("resultfail","Order not placed in IBAS");
						return  "redirect:/ProcurementStatus.htm";
    				}
            	 service.updateCostOnDemand(demandOrderList,fileId,UserId);
             }
			int result =service.upadteDemandFile(fileId,statusId,eventDate,remarks);
			
			if(result>0) {
				redir.addAttribute("result","Demand Updated Successfully");
			}else {
				redir.addAttribute("resultfail","Something went worng");
			}
			
			
			return  "redirect:/ProcurementStatus.htm";
		}catch (Exception e) 
		{			
			e.printStackTrace(); 
			logger.error(new Date() +" Inside upadteDemandFile.htm "+UserId, e); 
			return "static/Error";	
		}
	}

	@RequestMapping(value="FileInActive.htm")
	public String FileInActive(HttpServletRequest req, RedirectAttributes redir, HttpSession ses) throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside FileInActive.htm "+UserId);		
		try {
             String projectId=req.getParameter("projectId");
             String fileId=req.getParameter("fileId");
			int result =service.FileInActive(fileId,UserId);
			
			if(result>0) {
				redir.addAttribute("result","File InActived successfully");
			}else {
				redir.addAttribute("resultfail","Something went worng");
			}
			redir.addFlashAttribute("projectid",projectId);
			
			return  "redirect:/ProcurementStatus.htm";
		}catch (Exception e) 
		{			
			e.printStackTrace(); 
			logger.error(new Date() +" Inside FileInActive.htm "+UserId, e); 
			return "static/Error";	
		}
	}
//	@RequestMapping(value = "FileSearch.htm", method = {RequestMethod.POST,RequestMethod.GET})
//	public String FileSearch(HttpServletRequest request, HttpSession ses,RedirectAttributes redir) throws Exception  
//	{
//		ModelAndView mv=new ModelAndView();
//		String UserId = (String) ses.getAttribute("Username");
//		logger.info(new Date() +"Inside FileSearch.htm "+UserId);		
//		try {
//			//String category=request.getParameter("Category");
//			String action=request.getParameter("action");			
//			String searchData=request.getParameter("SearchData");
//			
//			if(action!=null && searchData!=null && !searchData.equals("")) 
//			{
//				List<Object[]> demandSearchList=null;
//				
//				if(action.equalsIgnoreCase("DemandNo")) 
//				{
//					demandSearchList= service.getResultAsDemandNo(searchData.trim());
//				}
//				else if(action.equalsIgnoreCase("FileNo")) 
//				{
//					demandSearchList= service.getResultAsFileNo(searchData.trim());
//				}
//				else if(action.equalsIgnoreCase("Item"))
//				{
//					demandSearchList=service.getResultAsPerItemSearch(searchData.trim());
//				}	
//				
//				request.setAttribute("action", action);
//				request.setAttribute("searchData", searchData);
//				request.setAttribute("demandSearchList", demandSearchList);
//							
//			}
//			
//			return "pfts/FileSearch";
//		}
//		catch (Exception e) 
//		{			
//			e.printStackTrace(); 
//			logger.error(new Date() +" Inside FileSearch.htm "+UserId, e); 
//			return "static/Error";	
//		}
//	}
	@RequestMapping(value="FileOrderRetrive.htm", method=RequestMethod.POST)
	public String FileOrderRetrive(HttpServletRequest req, RedirectAttributes redir, HttpSession ses) throws Exception 
	{
		String UserId = (String) ses.getAttribute("Username");
		String LabCode = (String)ses.getAttribute("labcode");

		logger.info(new Date() +"Inside FileOrderRetrive.htm "+UserId);		
		try {
			
             String projectId=req.getParameter("projectId");
             String fileId=req.getParameter("fileId");
             String demandNo=req.getParameter("demandNo");
             redir.addFlashAttribute("projectid",projectId);
            	   	//final String localUri=uri+"/pfms_serv/newDemandsOrderDetails?demandNo="+demandNo;
    				List<DemandOrderDetails> demandOrderList=null;
//    		 		HttpHeaders headers = new HttpHeaders();
//    		 		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON)); 
//    		 		headers.set("labcode", LabCode);
//    		 		String jsonResult=null;
//    				try {
//    					HttpEntity<String> entity = new HttpEntity<String>(headers);
//    					ResponseEntity<String> response=restTemplate.exchange(localUri, HttpMethod.POST, entity, String.class);
//    					jsonResult=response.getBody();
//    			
//    				}catch(HttpClientErrorException e) {
//    					redir.addAttribute("resultfail","Order not placed in IBAS");
//						return  "redirect:/ProcurementStatus.htm";
//    				}
//    				ObjectMapper mapper = new ObjectMapper();
//    				mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//    				mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
//    				if(jsonResult!=null) {
//    					try {
//    					
//    						demandOrderList = mapper.readValue(jsonResult, new TypeReference<List<DemandOrderDetails>>(){});
//    					} catch (JsonProcessingException e) {
//    						redir.addAttribute("resultfail","Order not placed in IBAS");
//    						return  "redirect:/ProcurementStatus.htm";
//    					}
//    				}
    				
    				try {
    				demandOrderList = serv.DemandsOrderDetails(demandNo);
    				}catch (Exception e) {
    					e.printStackTrace();
    					redir.addAttribute("resultfail","Order not placed in IBAS");
						return  "redirect:/ProcurementStatus.htm";
					}
            	long result= service.updateCostOnDemand(demandOrderList,fileId,UserId);

            	if(result>0) {
     				redir.addAttribute("result","Demand Order Updated successfully");
     			}else {
     				redir.addAttribute("resultfail","Something went worng");
     			}
			
			
			return  "redirect:/ProcurementStatus.htm";
		}catch (Exception e) 
		{			
			e.printStackTrace(); 
			logger.error(new Date() +" Inside FileOrderRetrive.htm "+UserId, e); 
			return "static/Error";	
		}
	}
	

	@RequestMapping(value = "getFilePDCInfo.htm", method = RequestMethod.GET)
	public @ResponseBody String getFilePDCInfo(HttpServletRequest request, HttpSession ses) throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside getFilePDCInfo.htm "+UserId);		
		try {
			String fileid=request.getParameter("fileid");
			Object[] statusList=service.getFilePDCInfo(fileid);
			Gson json = new Gson();
			return json.toJson(statusList);
		}
		catch (Exception e) 
		{			
			e.printStackTrace(); 
			logger.error(new Date() +" Inside getFilePDCInfo.htm "+UserId, e); 
			return "";
		}

	}
	
	@RequestMapping(value = "UpdateFilePDCInfo.htm", method = RequestMethod.POST)
	public String UpdateFilePDCInfo(HttpServletRequest request, HttpSession ses,RedirectAttributes redir) throws Exception
	{
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside UpdateFilePDCInfo.htm "+UserId);		
		try {
			String fileid=request.getParameter("fileId");
			String PDCDate=request.getParameter("PDCDate");
			String IntegrationDate=request.getParameter("IntegrationDate");
			String projectId=request.getParameter("projectId");
			
			long result = service.UpdateFilePDCInfo(fileid, PDCDate, IntegrationDate, UserId);
			
			if(result>0) {
 				redir.addAttribute("result","Demand Order Updated successfully");
 			}else {
 				redir.addAttribute("resultfail","Something went worng");
 			}
			redir.addFlashAttribute("projectid",projectId);
			return "redirect:/ProcurementStatus.htm";
			
		}
		catch (Exception e) 
		{			
			e.printStackTrace(); 
			logger.error(new Date() +" Inside UpdateFilePDCInfo.htm "+UserId, e); 
			return "static/Error";
		}

	}
	
	// ********************************************************** Eniv Strat ***********************************************
			@RequestMapping(value = "envisagedAction.htm")
			public String envisagedAction(HttpServletRequest request, HttpSession ses) throws Exception
			{
				String UserId = (String) ses.getAttribute("Username");
				logger.info(new Date() +"Inside envisagedAction.htm "+UserId);		
				try {
					String projectId=request.getParameter("projectId");
					request.setAttribute("projectId", projectId);
					request.setAttribute("value", "Add");
				
					return "pfts/envisagedAction";
					
				}
				catch (Exception e) 
				{			
					e.printStackTrace(); 
					logger.error(new Date() +" Inside envisagedAction.htm "+UserId, e); 
					return "static/Error";
				}

			}
		
			@RequestMapping(value = "enviActionSubmit.htm", method=RequestMethod.POST)
			public String enviActionSubmit(HttpServletRequest request, HttpSession ses,RedirectAttributes redir) throws Exception
			{
				long result=0;
				long result1=0;
				String UserId = (String) ses.getAttribute("Username");
				int count=0;
				logger.info(new Date() +"Inside enviActionSubmit.htm "+UserId);		
				try {
					String projectId=request.getParameter("projectId");
					String itemNomenclature=request.getParameter("itemNomenclature");
					String estimatedCost=request.getParameter("estimatedCost");
					//String status=request.getParameter("status");
					String remarks=request.getParameter("remarks");
					String intiDate=request.getParameter("intiDate");
					String fileId=request.getParameter("fileId");
					
					if(!InputValidator.isValidCapitalsAndSmallsAndNumericAndSpace(itemNomenclature)) {
						
						return redirectWithError(redir, "ProcurementStatus.htm", "'Item Nomenclature' must contain only Alphabets and Numbers");
					}
					
					if(!InputValidator.isContainsNumberOnly(estimatedCost)) {
						
						return redirectWithError(redir, "ProcurementStatus.htm", "'Estimated Cost' must contain only Numbers");
					}
					
					if(InputValidator.isContainsHTMLTags(remarks)) {
						
						return redirectWithError(redir, "ProcurementStatus.htm", "'Remarks' should not contain HTML Tags.!");
					}
					 PFTSFile pf = new PFTSFile();
					 pf.setProjectId(Long.parseLong(projectId));
					 pf.setItemNomenclature(itemNomenclature);
					 pf.setEstimatedCost(Double.parseDouble(estimatedCost));
					 pf.setEnvisagedStatus("Demand to be Initiated");
					 pf.setRemarks(remarks);
					 pf.setPrbDateOfInti(new java.sql.Date(inputFormat.parse(intiDate).getTime()));
					 pf.setIsActive(1);
					 pf.setCreatedBy(UserId);
					 pf.setCreatedDate(sdf.format(new Date()));
					 pf.setEnvisagedFlag("Y");
					 if(!fileId.equalsIgnoreCase("null")) {
						 pf.setPftsFileId(Long.parseLong(fileId));
						 count=service.getpftsFieldId(fileId);
						 }
				
					if(count>0) {
						result1=service.updateEnvi(pf,UserId);
						if(result1>0) {
			 				redir.addAttribute("result","Demand Edited successfully");
			 			}else {
			 				redir.addAttribute("resultfail","Demand Edited unsuccessful");
			 			}
					}else {
					 result = service.addDemandfile(pf);
					
					if(result>0) {
		 				redir.addAttribute("result","Demand Added successfully");
		 			}else {
		 				redir.addAttribute("resultfail","Demand Added unsuccessful");
		 			}
					}
					redir.addFlashAttribute("projectid",projectId);
				
					return "redirect:/ProcurementStatus.htm";
					
				}
				catch (Exception e) 
				{			
					e.printStackTrace(); 
					logger.error(new Date() +" Inside enviActionSubmit.htm "+UserId, e); 
					return "static/Error";
				}

			}
			
			@RequestMapping(value = "getEnviEditData.htm", method = RequestMethod.GET)
			public @ResponseBody String getEnviEditData(HttpServletRequest request, HttpSession ses) throws Exception
			{
				String UserId = (String) ses.getAttribute("Username");
				logger.info(new Date() +"Inside getEnviEditData.htm "+UserId);		
				try {
					String PftsFileId=request.getParameter("PftsFileId");
					Object[] EnviData=service.getEnviData(PftsFileId);
					Gson json = new Gson();
					return json.toJson(EnviData);
				}
				catch (Exception e) 
				{			
					e.printStackTrace(); 
					logger.error(new Date() +" Inside getEnviEditData.htm "+UserId, e); 
					return "";
				}

			}
			
			@RequestMapping(value="enviEdit.htm", method = {RequestMethod.GET,RequestMethod.POST})
			public String enviEdit(HttpServletRequest req, HttpSession ses) throws Exception 
			{
				String UserId = (String) ses.getAttribute("Username");
				logger.info(new Date() +"Inside enviEdit.htm "+UserId);		
				try {
					req.setAttribute("itemN", req.getParameter("itemN"));
					req.setAttribute("estimatedCost", req.getParameter("estimatedCost"));
					req.setAttribute("PDOfInitiation", req.getParameter("PDOfInitiation"));
					req.setAttribute("status", req.getParameter("status"));
					req.setAttribute("remarks", req.getParameter("remarks"));
					req.setAttribute("fileId", req.getParameter("fileId"));
					req.setAttribute("projectId", req.getParameter("projectId"));
					req.setAttribute("value", "Edit");
					
					return  "pfts/envisagedAction";
				}catch (Exception e) 
				{			
					e.printStackTrace(); 
					logger.error(new Date() +" Inside enviEdit.htm "+UserId, e); 
					return "static/Error";	
				}
			}
			
	// ********************************************************** Eniv End ***********************************************
			
		@RequestMapping(value = "getFileViewList.htm", method = RequestMethod.GET)
		public @ResponseBody String getFileViewList(HttpServletRequest request, HttpSession ses) throws Exception
			{
				String UserId = (String) ses.getAttribute("Username");
				logger.info(new Date() +"Inside getFileViewList.htm "+UserId);		
				try {
						String procFileId=request.getParameter("fileId");
						Object[] fileViewList=service.getpftsFileViewList(procFileId);
						Gson json = new Gson();
						return json.toJson(fileViewList);
				}catch (Exception e) 
				{			
					e.printStackTrace(); 
					logger.error(new Date() +" Inside getFileViewList.htm "+UserId, e); 
					return null;
				}

			}
			
		@RequestMapping(value = "AddManualDemand.htm", method = {RequestMethod.GET, RequestMethod.POST})
		public String AddManualDemand(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
		{
			String UserId = (String) ses.getAttribute("Username");
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String Logintype= (String)ses.getAttribute("LoginType");
			String LabCode = (String)ses.getAttribute("labcode");
			logger.info(new Date() +"Inside AddManualDemand.htm"+UserId);
			
			try {
				
				String projectId = req.getParameter("projectId");
				
				List<Object[]> projectlist=service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
				req.setAttribute("projectslist",projectlist);
				req.setAttribute("projectId",projectId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "pfts/AddManualDemand";
		}
		
		@RequestMapping(value = "AddManualDemandSubmit.htm", method= {RequestMethod.GET,RequestMethod.POST})
		public String AddManualDemandSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
		{
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String Logintype= (String)ses.getAttribute("LoginType");
			String UserId = (String) ses.getAttribute("Username");
			String LabCode = (String)ses.getAttribute("labcode");
			
			logger.info(new Date() +"Inside AddManualDemand.htm"+UserId);
			
			try {
				
				String ProjectId = req.getParameter("ProjectId");
				String demandNo = req.getParameter("demandNo");
				String demanddate = req.getParameter("demanddate");
				String estimatedcost = req.getParameter("estimatedcost");
				String itemname = req.getParameter("itemname");
				String demandType = req.getParameter("demandType");
					
				List<Object[]> projectlist=service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
				
				if(!InputValidator.isValidCapitalsAndSmallsAndNumeric(demandNo)) {
					redir.addAttribute("projectId",ProjectId);
					return redirectWithError(redir, "AddManualDemand.htm", "'Demand Number' must contain only Alphabets and Numbers");
				}
				
				if(!InputValidator.isValidCapitalsAndSmallsAndNumeric(estimatedcost)) {
					redir.addAttribute("projectId",ProjectId);
					return redirectWithError(redir, "AddManualDemand.htm", "'Estimated Cost' must contain only Alphabets and Numbers");
				}
				
				if(InputValidator.isContainsHTMLTags(itemname)) {
					redir.addAttribute("projectId",ProjectId);
					return redirectWithError(redir, "AddManualDemand.htm", "'Item Name' should not contain HTML Tags");
				}
				
				
				
				 PFTSFile pf = new PFTSFile();
				 
				 pf.setDemandType(demandType);
				 pf.setProjectId(Long.parseLong(ProjectId));
				 pf.setDemandNo(demandNo);
				 pf.setDemandDate(new java.sql.Date(inputFormat.parse(demanddate).getTime()));
				 pf.setItemNomenclature(itemname);
				 pf.setEstimatedCost(Double.parseDouble(estimatedcost));
				 pf.setPftsStatusId(1l);
			     pf.setRemarks("Nil");
				 pf.setCreatedBy(UserId);
				 pf.setCreatedDate(sdf.format(new Date()));
				 pf.setIsActive(1);
				 
				 Long result=service.addDemandfile(pf);
					
					if(result>0) {
						redir.addAttribute("result","Demand Added Successfully ");
					}else {
						redir.addAttribute("resultfail","Something went worng");
					}
					
//					redir.addAttribute("projectslist",projectlist);
					redir.addAttribute("projectid",ProjectId);
//					redir.addAttribute("fileStatusList",service.getFileStatusList(ProjectId));
//					redir.addAttribute("pftsStageList", service.getpftsStageList());
//					return  "redirect:/ProcurementStatus.htm";
				
			} catch (Exception e) {
				e.printStackTrace(); 
				logger.error(new Date() +" Inside AddManualDemandSubmit.htm "+UserId, e);
				return "static/Error";	
			}
			return "redirect:/ProcurementStatus.htm";
		}
		
		@RequestMapping(value = "updateManualDemand.htm", method = {RequestMethod.GET,RequestMethod.POST})
		public String updateManualDemand(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
		{
			String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside updateManualDemand.htm "+UserId);
			
			try {
				
	             String fileId=req.getParameter("fileId");
	             req.setAttribute("fileViewList",service.getpftsFileViewList(fileId));
	             req.setAttribute("OrderLists", service.getOrderDetailsAjax(fileId));
	 		     req.setAttribute("pftsStageList", service.getpftsStageList());
				return  "pfts/UpdateManualDemand";
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.info(new Date() +"Inside updateManualDemand.htm "+UserId);
				return "static/Error";
			}
		}
		
		
		@RequestMapping(value = "updateManualDemandSubmit.htm", method = RequestMethod.POST)
		public String updateManualDemandSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
		{
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String Logintype= (String)ses.getAttribute("LoginType");
			String UserId = (String) ses.getAttribute("Username");
			String LabCode = (String)ses.getAttribute("labcode");
			
			logger.info(new Date() +"Inside updateManualDemandSubmit.htm "+UserId);
			
			try {
				
				List<Object[]> projectlist=service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
				
				 String statusId=req.getParameter("procstatus");
	             String projectId=req.getParameter("projectId");
	             String eventDate=req.getParameter("eventDate");
	             String fileId=req.getParameter("fileId");
	             String remarks=req.getParameter("procRemarks");
	             String flag=req.getParameter("flag");
	             
	             if(InputValidator.isContainsHTMLTags(remarks)) {
	            	 
	            	 redir.addAttribute("fileId", fileId);
	 				return redirectWithError(redir, "updateManualDemand.htm", "'Remarks' should not contain HTML tags");
	 			}
	        
	             long result=0l;
	             result =service.upadteDemandFile(fileId,statusId,eventDate,remarks);
	             
	             List<DemandOrderDetails> fileList = new ArrayList<DemandOrderDetails>();
	             
	             if(flag!=null &&   flag.equalsIgnoreCase("order")) {
	            	 
	                 String[] orderNo=req.getParameterValues("orderno");
		             String[] orderDate=req.getParameterValues("orderdate");
		             String[] orderCost=req.getParameterValues("ordercost");
		             String[] dpDate=req.getParameterValues("dpdate");
		             String[] itemFor=req.getParameterValues("itemfor");
		             String[] vendorName=req.getParameterValues("vendor");
	     			
		             for(int i=0;i<orderNo.length;i++) {
		            	 DemandOrderDetails pfts = new DemandOrderDetails();
		            	 pfts.setOrderNo(orderNo[i]);
		            	 pfts.setOrderDate(new java.sql.Date(inputFormat.parse(orderDate[i]).getTime()).toString());
		            	 pfts.setDpDate(new java.sql.Date(inputFormat.parse(dpDate[i]).getTime()).toString());
		            	 pfts.setOrderCost(Double.parseDouble(orderCost[i]));
		            	 pfts.setItemFor(itemFor[i]);
		            	 pfts.setVendorName(vendorName[i]);
		            	 pfts.setIsPresent("N");
		            	 fileList.add(pfts);
		             }
		             
		             result=service.updateCostOnDemand(fileList, fileId, UserId);
	             }
	             
	             if(result>0) {
	     				redir.addAttribute("result","Demand Updated Successfully");
	     			}else {
	     				redir.addAttribute("resultfail","Something went worng");
	     			}
	             
	             redir.addFlashAttribute("projectslist",projectlist);
				 redir.addAttribute("projectid",projectId);
				 redir.addAttribute("fileStatusList",service.getFileStatusList(projectId));
				 redir.addAttribute("pftsStageList", service.getpftsStageList());
				 return  "redirect:/ProcurementStatus.htm";
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.info(new Date() +"Inside updateManualDemandSubmit.htm "+UserId);
				return "static/Error";
			}
		}
		

		@RequestMapping(value = "getOrderDetailsAjax.htm", method = RequestMethod.GET)
		public @ResponseBody String getOrderDetailsAjax(HttpServletRequest request, HttpSession ses) throws Exception
		{
			String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside getOrderDetailsAjax.htm "+UserId);		
			try {
					String fileId=request.getParameter("fileId");
					List<Object[]> orderList=service.getOrderDetailsAjax(fileId);
					Gson json = new Gson();
					return json.toJson(orderList);
			}catch (Exception e) 
			{			
				e.printStackTrace(); 
				logger.error(new Date() +" Inside getOrderDetailsAjax.htm "+UserId, e); 
				return null;
			}

		}
		
		
		@RequestMapping(value = "updateManualOrderSubmit.htm", method = RequestMethod.POST)
		public String updateManualOrderSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
		{
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String Logintype= (String)ses.getAttribute("LoginType");
			String UserId = (String) ses.getAttribute("Username");
			String LabCode = (String)ses.getAttribute("labcode");
			
			logger.info(new Date() +"Inside updateManualOrderSubmit.htm "+UserId);
			
			try {
				
				List<Object[]> projectlist=service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
				
	                 String projectId=req.getParameter("projectId");
	                 String orderid=req.getParameter("orderid");
	                 String orderNo=req.getParameter("orderno");
		             String orderDate=req.getParameter("orderdate");
		             String orderCost=req.getParameter("ordercost");
		             String dpDate=req.getParameter("dpdate");
		             String itemFor=req.getParameter("itemfor");
		             String vendorName=req.getParameter("vendor");
	     			
		             if(Stream.of(vendorName,itemFor)
		            		 .anyMatch(field -> InputValidator.isContainsHTMLTags(field))) {
		            	 return redirectWithError(redir, "ProcurementStatus.htm", "Html Tags are not allowed");
		             }
		             
		             PftsFileOrder order = new PftsFileOrder();
		             order.setOrderNo(orderNo);
		             order.setOrderDate(new java.sql.Date(inputFormat.parse(orderDate).getTime()).toString());
		             order.setDpDate(new java.sql.Date(inputFormat.parse(dpDate).getTime()).toString());
		             order.setOrderCost(Double.parseDouble(orderCost));
		             order.setItemFor(itemFor);
		             order.setVendorName(vendorName);
		             order.setModifiedBy(UserId);
		             order.setModifiedDate(sdf.format(new Date()));
		             
		             long result=service.ManualOrderSubmit(order,orderid);
	             
	             if(result>0) {
	     				redir.addAttribute("result","Order Updated Successfully");
	     			}else {
	     				redir.addAttribute("resultfail","Order Update Unsuccessfull");
	     			}
	             
	             redir.addFlashAttribute("projectslist",projectlist);
				 redir.addAttribute("projectid",projectId);
				 redir.addAttribute("fileStatusList",service.getFileStatusList(projectId));
				 redir.addAttribute("pftsStageList", service.getpftsStageList());
				 return  "redirect:/ProcurementStatus.htm";
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.info(new Date() +"Inside updateManualOrderSubmit.htm "+UserId);
				return "static/Error";
			}
		}
		
		
		@RequestMapping(value = "manualDemandEditSubmit.htm", method= {RequestMethod.GET,RequestMethod.POST})
		public String manualDemandEditSubmit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
		{
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String Logintype= (String)ses.getAttribute("LoginType");
			String UserId = (String) ses.getAttribute("Username");
			String LabCode = (String)ses.getAttribute("labcode");
			
			logger.info(new Date() +"Inside manualDemandEditSubmit.htm "+UserId);
			
			try {
				String fileId=req.getParameter("procfileId");
				String ProjectId = req.getParameter("ProjectId");
				String demandNo = req.getParameter("demandno");
				String demanddate = req.getParameter("demanddate");
				String estimatedcost = req.getParameter("estimatedcost");
				String itemname = req.getParameter("itemname");
				
				if(InputValidator.isContainsHTMLTags(itemname)) {
					return redirectWithError(redir, "ProcurementStatus.htm", "'Item Name' should not contains HTML tags");
				}
				
				
				List<Object[]> projectlist=service.LoginProjectDetailsList(EmpId,Logintype,LabCode);
				
				  PFTSFileDto pftsDto = new PFTSFileDto();
				  pftsDto.setPftsFileId(Long.parseLong(fileId));
				  pftsDto.setDemandNo(demandNo);
				  pftsDto.setDemandDate(new java.sql.Date(inputFormat.parse(demanddate).getTime()));
				  pftsDto.setItemNomenclature(itemname);
				  pftsDto.setEstimatedCost(Double.parseDouble(estimatedcost));
				  pftsDto.setModifiedBy(UserId);
				  pftsDto.setModifiedDate(sdf.format(new Date()));
				
				 Long result=service.manualDemandEditSubmit(pftsDto);
					
					if(result>0) {
						redir.addAttribute("result","Demand Edited Successfully ");
					}else {
						redir.addAttribute("resultfail","Demand Edit Unsuccessfull");
					}
					
					redir.addFlashAttribute("projectslist",projectlist);
					redir.addAttribute("projectid",ProjectId);
					redir.addFlashAttribute("fileStatusList",service.getFileStatusList(ProjectId));
					redir.addFlashAttribute("pftsStageList", service.getpftsStageList());
					return  "redirect:/ProcurementStatus.htm";
				
			} catch (Exception e) {
				e.printStackTrace(); 
				logger.error(new Date() +" Inside manualDemandEditSubmit.htm "+UserId, e); 
				return "static/Error";	
			}
		}
		
		
		@RequestMapping(value = "checkManualDemandNo.htm", method = RequestMethod.GET)
		public @ResponseBody String checkManualDemandNo(HttpServletRequest req, HttpSession ses) throws Exception
			{
				String UserId = (String) ses.getAttribute("Username");
				logger.info(new Date() +"Inside checkManualDemandNo.htm "+UserId);		
				try {
						String projectId=req.getParameter("projectId");
						List<Object[]> demandNoList=service.getDemandNoList();
						Gson json = new Gson();
						return json.toJson(demandNoList);
				}catch (Exception e) 
				{			
					e.printStackTrace(); 
					logger.error(new Date() +" Inside checkManualDemandNo.htm "+UserId, e); 
					return null;
				}

			}
		@RequestMapping(value="ManualDemandExcel.htm" ,method = {RequestMethod.POST,RequestMethod.GET})
		public String ManualDemandExcel( RedirectAttributes redir,HttpServletRequest req ,HttpServletResponse res ,HttpSession ses)throws Exception
		{
			String UserId=(String)ses.getAttribute("Username");
			String LabCode =(String) ses.getAttribute("labcode");
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			logger.info(new Date() +"Inside ManualDemandExcel.htm "+UserId);
			try {
				XSSFWorkbook workbook = new XSSFWorkbook();
				XSSFSheet sheet = workbook.createSheet("Abbreviation Details");
				XSSFRow row = sheet.createRow(0);

				// Create a bold font style for headers
				Font headerFont = workbook.createFont();
				headerFont.setBold(true);

				// Create a cell style for headers
				CellStyle headerCellStyle = workbook.createCellStyle();
				headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
				headerCellStyle.setFont(headerFont);

				// Create a cell style for center alignment and text wrapping
				CellStyle centerWrapCellStyle = workbook.createCellStyle();
				centerWrapCellStyle.setAlignment(HorizontalAlignment.CENTER);
				centerWrapCellStyle.setWrapText(true);
				centerWrapCellStyle.setFont(headerFont);

				// Create and style cells
				Cell cell0 = row.createCell(0);
				cell0.setCellValue("SN");
				cell0.setCellStyle(headerCellStyle);
				sheet.setColumnWidth(0, 2000);

				Cell cell1 = row.createCell(1);
				cell1.setCellValue("Demand No");
				cell1.setCellStyle(headerCellStyle);
				sheet.setColumnWidth(1, 7000);

				   Cell cell2 = row.createCell(2, CellType.STRING);
				    cell2.setCellValue("Demand Date\n(DD-MM-YYYY)");
				    cell2.setCellStyle(centerWrapCellStyle);
				    sheet.setColumnWidth(2, 8000);

				Cell cell3 = row.createCell(3);
				cell3.setCellValue("Estimated cost \n(In Rs)");
				cell3.setCellStyle(headerCellStyle);
				cell3.setCellStyle(centerWrapCellStyle);
				sheet.setColumnWidth(3, 5000);

				Cell cell4 = row.createCell(4);
				cell4.setCellValue("Item Name");
				cell4.setCellStyle(headerCellStyle);
				sheet.setColumnWidth(4, 5000);

				res.setContentType("application/vnd.ms-excel");
				res.setHeader("Content-Disposition", "attachment; filename=Demands.xls");

				// Write the workbook to the response output stream
				workbook.write(res.getOutputStream());
				workbook.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		
//		 @RequestMapping(value="ManualDemandExcelSubmit.htm" ,method = {RequestMethod.POST,RequestMethod.GET})
//			public String ManualDemandExcelSubmit( RedirectAttributes redir,HttpServletRequest req ,HttpServletResponse res ,HttpSession ses)throws Exception
//			{
//			  
//				String UserId = (String) ses.getAttribute("Username");
//				String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
//				String Logintype= (String)ses.getAttribute("LoginType");
//				String LabCode = (String)ses.getAttribute("labcode");
//				String projectid =req.getParameter("ProjectId");
//			
//			
//				
//				try {
//					if (req.getContentType() != null && req.getContentType().startsWith("multipart/")) {
//						   Part filePart = req.getPart("filename");
//							List<ProjectOverallFinance>list = new ArrayList<>();
//							InputStream fileData = filePart.getInputStream();
//							
//						Long count =0l;
//							Workbook workbook = new XSSFWorkbook(fileData);
//							Sheet sheet  = workbook.getSheetAt(0);
//							int rowCount=sheet.getLastRowNum()-sheet.getFirstRowNum(); 
//						
//							for (int i=1;i<=rowCount;i++) {
//								PFTSFile pf = new PFTSFile();
//								int cellcount= sheet.getRow(i).getLastCellNum();
//								
//								 Row row = sheet.getRow(i);
//								 DecimalFormat df = new DecimalFormat("#");
//							
//								for(int j=1;j<cellcount;j++) {
//									Cell cell = row.getCell(j);
//									if(cell!=null) {
//										if(j==1) {
//											switch(sheet.getRow(i).getCell(j).getCellType()) {
//											case BLANK:
//												break;
//											case NUMERIC:
//												pf.setDemandNo(df.format(sheet.getRow(i).getCell(j).getNumericCellValue()));
//												break;
//											case STRING:
//												pf.setDemandNo(sheet.getRow(i).getCell(j).getStringCellValue());
//												break;	 
//											}
//										}
//										
//										if(j==2) {
//											System.out.println("sheet.getRow(i).getCell(j)"+sheet.getRow(i).getCell(j));
//											
//											switch(sheet.getRow(i).getCell(j).getCellType()) {
//											case BLANK:
//												break;
//											case NUMERIC:
//												 if (DateUtil.isCellDateFormatted(sheet.getRow(i).getCell(j))) {
//											            java.util.Date date = sheet.getRow(i).getCell(j).getDateCellValue();
//											            pf.setDemandDate(new java.sql.Date(date.getTime()));
//											        }
//												break;
//											case STRING:
//												pf.setDemandDate(new java.sql.Date(inputFormat.parse((sheet.getRow(i).getCell(j).getStringCellValue())).getTime()));
//												break;	 
//											
//											}
//										}
//										
//										if(j==3) {
//											switch(sheet.getRow(i).getCell(j).getCellType()) {
//											case BLANK:
//												break;
//											case NUMERIC:
//												pf.setEstimatedCost(sheet.getRow(i).getCell(j).getNumericCellValue());
//												break;
//											case STRING:
//												pf.setEstimatedCost(Double.parseDouble(sheet.getRow(i).getCell(j).getStringCellValue()));
//												break;	 
//											}
//										}
//										
//										if(j==4) {
//											switch(sheet.getRow(i).getCell(j).getCellType()) {
//											case BLANK:
//												break;
//											case NUMERIC:
//												break;
//											case STRING:
//												pf.setItemNomenclature(sheet.getRow(i).getCell(j).getStringCellValue());
//												break;	 
//											}
//										}
//									}
//								}
//								 pf.setDemandType("M");
//								 pf.setPftsStatusId(1l);
//							     pf.setRemarks("Nil");
//							     pf.setIsActive(1);
//								 pf.setCreatedBy(UserId);
//								 pf.setCreatedDate(sdf.format(new Date()));
//								 pf.setProjectId(Long.parseLong(projectid));
//								 
//								 if(pf.getDemandNo()!=null) {
//								  count=count+service.addDemandfile(pf);
//								 }
//							}
//							if(count>0) {
//								redir.addAttribute("result","Demands Added Successfully ");
//							}else {
//								redir.addAttribute("resultfail","Something went worng");
//							}
//					}
//				} catch (Exception e) {
//				e.printStackTrace();
//				}
//				redir.addAttribute("projectid", projectid);
//				return "redirect:/ProcurementStatus.htm";
//				
//			}
//		 
		
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

		
		
		
		
		@RequestMapping(value = "ManualDemandExcelSubmit.htm", method = {RequestMethod.POST, RequestMethod.GET})
		public String ManualDemandExcelSubmit(RedirectAttributes redir, HttpServletRequest req, HttpServletResponse res, HttpSession ses) throws Exception {

		    String UserId = (String) ses.getAttribute("Username");
		    String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
		    String Logintype = (String) ses.getAttribute("LoginType");
		    String LabCode = (String) ses.getAttribute("labcode");
		    String projectid = req.getParameter("ProjectId");

		    try {
		        if (req.getContentType() != null && req.getContentType().startsWith("multipart/")) {

		            Part filePart = req.getPart("filename");
		            String fileName = filePart.getSubmittedFileName();
		            String contentType = filePart.getContentType();

		            // ✅ Step 1: Validate Excel file (only xls or xlsx allowed)
		            if (!isValidExcelFile(fileName, contentType)) {
		                redir.addAttribute("resultfail", "Only Excel files (.xls, .xlsx) are allowed");
		                return "redirect:/ProcurementStatus.htm";
		            }

		            // ✅ Step 2: Read the Excel file
		            InputStream fileData = filePart.getInputStream();
		            Workbook workbook = null;

		            if (fileName.toLowerCase().endsWith(".xls")) {
		                workbook = new XSSFWorkbook(fileData); // old format
		            } else if (fileName.toLowerCase().endsWith(".xlsx")) {
		                workbook = new XSSFWorkbook(fileData); // new format
		            }

		            Sheet sheet = workbook.getSheetAt(0);
		            int rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();

		            Long count = 0L;

		            for (int i = 1; i <= rowCount; i++) {
		                PFTSFile pf = new PFTSFile();
		                Row row = sheet.getRow(i);
		                int cellcount = row.getLastCellNum();
		                DecimalFormat df = new DecimalFormat("#");

		                for (int j = 1; j < cellcount; j++) {
		                    Cell cell = row.getCell(j);
		                    if (cell != null) {
		                        if (j == 1) {
		                            switch (cell.getCellType()) {
		                                case BLANK: break;
		                                case NUMERIC:
		                                    pf.setDemandNo(df.format(cell.getNumericCellValue()));
		                                    break;
		                                case STRING:
		                                    pf.setDemandNo(cell.getStringCellValue());
		                                    break;
		                            }
		                        }

		                        if (j == 2) {
		                            switch (cell.getCellType()) {
		                                case BLANK: break;
		                                case NUMERIC:
		                                    if (DateUtil.isCellDateFormatted(cell)) {
		                                        java.util.Date date = cell.getDateCellValue();
		                                        pf.setDemandDate(new java.sql.Date(date.getTime()));
		                                    }
		                                    break;
		                                case STRING:
		                                    pf.setDemandDate(new java.sql.Date(inputFormat.parse(cell.getStringCellValue()).getTime()));
		                                    break;
		                            }
		                        }

		                        if (j == 3) {
		                            switch (cell.getCellType()) {
		                                case BLANK: break;
		                                case NUMERIC:
		                                    pf.setEstimatedCost(cell.getNumericCellValue());
		                                    break;
		                                case STRING:
		                                    pf.setEstimatedCost(Double.parseDouble(cell.getStringCellValue()));
		                                    break;
		                            }
		                        }

		                        if (j == 4) {
		                            switch (cell.getCellType()) {
		                                case BLANK: break;
		                                case STRING:
		                                    pf.setItemNomenclature(cell.getStringCellValue());
		                                    break;
		                            }
		                        }
		                    }
		                }

		                pf.setDemandType("M");
		                pf.setPftsStatusId(1L);
		                pf.setRemarks("Nil");
		                pf.setIsActive(1);
		                pf.setCreatedBy(UserId);
		                pf.setCreatedDate(sdf.format(new Date()));
		                pf.setProjectId(Long.parseLong(projectid));

		                if (pf.getDemandNo() != null) {
		                    count = count + service.addDemandfile(pf);
		                }
		            }

		            if (count > 0) {
		                redir.addAttribute("result", "Demands Added Successfully");
		            } else {
		                redir.addAttribute("resultfail", "Something went wrong");
		            }
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		        redir.addAttribute("resultfail", "Error processing file: " + e.getMessage());
		    }

		    redir.addAttribute("projectid", projectid);
		    return "redirect:/ProcurementStatus.htm";
		}

		
		
		
		
		@RequestMapping(value="addProcurementMilestone.htm" ,method = {RequestMethod.POST,RequestMethod.GET})
		public String addProcurementMilestone (RedirectAttributes redir,HttpServletRequest req ,HttpServletResponse res ,HttpSession ses) throws Exception {
		
			String UserId = (String) ses.getAttribute("Username");
			String [] statusId = req.getParameterValues("statusId");
			String pftsFileId = req.getParameter("pftsfile");
			String projectid = req.getParameter("project");
			String action = req.getParameter("action");
			String demandnumber = req.getParameter("demandNumber");
			String pftsMilestoneId = req.getParameter("pftsMilestoneId");
			String [] probableDate = req.getParameterValues("probableDate");
			
			try {
				long result=0;
				if(pftsFileId!=null) {
					if(action.equalsIgnoreCase("add")) {
						PftsFileMilestone entity = new PftsFileMilestone();
						entity.setPftsFileId(Long.parseLong(pftsFileId));
						entity.setRevision(0);
						entity.setSetBaseline("N");
						entity.setCreatedBy(UserId);
						entity.setCreatedDate(sdf1.format(new Date()));
						entity.setIsActive(1);
						for (int i = 0; i < statusId.length; i++) { 
							 int status = Integer.parseInt(statusId[i]);
						        String parsedDate =probableDate[i]!=null && !probableDate[i].isEmpty() ? sdf3.format(sdf2.parse(probableDate[i])) : null;
						        switch (status) {
						            case 1:
						                break;
						            case 2:
						                entity.setEPCDate(parsedDate);
						                break;
						            case 3:
						                entity.setTocDate(parsedDate);
						                break;
						            case 4:
						                entity.setOrderDate(parsedDate);
						                break;
						            case 5:
						                entity.setPDRDate(parsedDate);
						                break;
						            case 6:
						                entity.setCriticalDate(parsedDate);
						                break;
						            case 7:
						                entity.setDDRDate(parsedDate);
						                break;
						            case 8:
						            	entity.setCDRDate(parsedDate);
						            	break;
						            case 9:
						            	entity.setAcceptanceDate(parsedDate);
						            	break;
						            case 10:
						            	entity.setFATDate(parsedDate);
						            	break;
						            case 11:
						            	entity.setDeliveryDate(parsedDate);
						            	break;
						            case 12:
						            	entity.setSATDate(parsedDate);
						            	break;
						            case 13:
						            	entity.setIntegrationDate(parsedDate);
						            	break;
						            default:
						                throw new IllegalArgumentException("Invalid statusId: " + status);
						         }
						}
						result=service.addProcurementMilestone(entity);
						if(result>0) {
							redir.addAttribute("result","Milstone Added Successfully For Demand No. "+demandnumber);
						}else {
							redir.addAttribute("resultfail","Something went worng");
   					    }
					}
					else if (action.equalsIgnoreCase("edit")) {
							PftsFileMilestone entity =service.getEditMilestoneData(Long.parseLong(pftsMilestoneId));
							entity.setModifiedBy(UserId);
							entity.setModifiedDate(sdf1.format(new Date()));
							for (int i = 0; i < statusId.length; i++) { 
								 int status = Integer.parseInt(statusId[i]);
							        String parsedDate =probableDate[i]!=null && !probableDate[i].isEmpty() ? sdf3.format(sdf2.parse(probableDate[i])) : null;
							        switch (status) {
							            case 1:
							                break;
							            case 2:
							                entity.setEPCDate(parsedDate);
							                break;
							            case 3:
							                entity.setTocDate(parsedDate);
							                break;
							            case 4:
							                entity.setOrderDate(parsedDate);
							                break;
							            case 5:
							                entity.setPDRDate(parsedDate);
							                break;
							            case 6:
							                entity.setCriticalDate(parsedDate);
							                break;
							            case 7:
							                entity.setDDRDate(parsedDate);
							                break;
							            case 8:
							            	entity.setCDRDate(parsedDate);
							            	break;
							            case 9:
							            	entity.setAcceptanceDate(parsedDate);
							            	break;
							            case 10:
							            	entity.setFATDate(parsedDate);
							            	break;
							            case 11:
							            	entity.setDeliveryDate(parsedDate);
							            	break;
							            case 12:
							            	entity.setSATDate(parsedDate);
							            	break;
							            case 13:
							            	entity.setIntegrationDate(parsedDate);
							            	break;
							            default:
							                throw new IllegalArgumentException("Invalid statusId: " + status);
							         }
							}
							result=service.editProcurementMilestone(entity);
							
							if(result>0) {
								redir.addFlashAttribute("result","Milstone Edited Successfully For Demand No. "+demandnumber);
							}else {
								redir.addFlashAttribute("resultfail","Something went worng");
							}
					}else if(action.equalsIgnoreCase("baseline")) {
						PftsFileMilestone entity =service.getEditMilestoneData(Long.parseLong(pftsMilestoneId));
						entity.setSetBaseline("Y");
						entity.setModifiedBy(UserId);
						entity.setModifiedDate(sdf1.format(new Date()));
						for (int i = 0; i < statusId.length; i++) { 
							 int status = Integer.parseInt(statusId[i]);
						        String parsedDate =probableDate[i]!=null && !probableDate[i].isEmpty() ? sdf3.format(sdf2.parse(probableDate[i])) : null;
						        switch (status) {
						            case 1:
						                break;
						            case 2:
						                entity.setEPCDate(parsedDate);
						                break;
						            case 3:
						                entity.setTocDate(parsedDate);
						                break;
						            case 4:
						                entity.setOrderDate(parsedDate);
						                break;
						            case 5:
						                entity.setPDRDate(parsedDate);
						                break;
						            case 6:
						                entity.setCriticalDate(parsedDate);
						                break;
						            case 7:
						                entity.setDDRDate(parsedDate);
						                break;
						            case 8:
						            	entity.setCDRDate(parsedDate);
						            	break;
						            case 9:
						            	entity.setAcceptanceDate(parsedDate);
						            	break;
						            case 10:
						            	entity.setFATDate(parsedDate);
						            	break;
						            case 11:
						            	entity.setDeliveryDate(parsedDate);
						            	break;
						            case 12:
						            	entity.setSATDate(parsedDate);
						            	break;
						            case 13:
						            	entity.setIntegrationDate(parsedDate);
						            	break;
						            default:
						                throw new IllegalArgumentException("Invalid statusId: " + status);
						         }
						 }
							result=service.editProcurementMilestone(entity);
							if(result>0) {
								redir.addAttribute("result","Milstone Baseline Set Successfully For Demand No. "+demandnumber);
							}else {
								redir.addAttribute("resultfail","Something went worng");
							}
					}
					else if (action.equalsIgnoreCase("revise")) {
						PftsFileMilestone entity =service.getEditMilestoneData(Long.parseLong(pftsMilestoneId));
						
						PftsFileMilestoneRev rev = new PftsFileMilestoneRev();
						rev.setPftsMilestoneId(entity.getPftsMilestoneId());
						rev.setEPCDate(entity.getEPCDate());
						rev.setTocDate(entity.getTocDate());
						rev.setOrderDate(entity.getOrderDate());
						rev.setPDRDate(entity.getPDRDate());
						rev.setCriticalDate(entity.getCriticalDate());
						rev.setDDRDate(entity.getDDRDate());
						rev.setCDRDate(entity.getCDRDate());
						rev.setAcceptanceDate(entity.getAcceptanceDate());
						rev.setFATDate(entity.getFATDate());
						rev.setDeliveryDate(entity.getDeliveryDate());
						rev.setSATDate(entity.getSATDate());
						rev.setIntegrationDate(entity.getIntegrationDate());
						rev.setRevision(entity.getRevision());
						rev.setModifiedBy(UserId);
						rev.setModifiedDate(sdf1.format(new Date()));
						rev.setIsActive(entity.getIsActive());
						service.addProcurementMilestoneRev(rev);
						
						entity.setRevision(entity.getRevision()+1);
						entity.setModifiedBy(UserId);
						entity.setModifiedDate(sdf1.format(new Date()));
						for (int i = 0; i < statusId.length; i++) { 
							 int status = Integer.parseInt(statusId[i]);
						        String parsedDate =probableDate[i]!=null && !probableDate[i].isEmpty() ? sdf3.format(sdf2.parse(probableDate[i])) : null;
						        switch (status) {
						            case 1:
						                break;
						            case 2:
						                entity.setEPCDate(parsedDate);
						                break;
						            case 3:
						                entity.setTocDate(parsedDate);
						                break;
						            case 4:
						                entity.setOrderDate(parsedDate);
						                break;
						            case 5:
						                entity.setPDRDate(parsedDate);
						                break;
						            case 6:
						                entity.setCriticalDate(parsedDate);
						                break;
						            case 7:
						                entity.setDDRDate(parsedDate);
						                break;
						            case 8:
						            	entity.setCDRDate(parsedDate);
						            	break;
						            case 9:
						            	entity.setAcceptanceDate(parsedDate);
						            	break;
						            case 10:
						            	entity.setFATDate(parsedDate);
						            	break;
						            case 11:
						            	entity.setDeliveryDate(parsedDate);
						            	break;
						            case 12:
						            	entity.setSATDate(parsedDate);
						            	break;
						            case 13:
						            	entity.setIntegrationDate(parsedDate);
						            	break;
						            default:
						                throw new IllegalArgumentException("Invalid statusId: " + status);
						         }
						   }
						    result=service.editProcurementMilestone(entity);
							if(result>0) {
								redir.addAttribute("result","Milstone Revised Successfully For Demand No. "+demandnumber);
							}else {
								redir.addAttribute("resultfail","Something went worng");
							}
					  }
				}
			}catch (Exception e) {
				e.printStackTrace();
			} 
			
			redir.addAttribute("projectid", projectid);
			return "redirect:/ProcurementStatus.htm";
		}
		
		@RequestMapping(value = "pftsMilestoneView.htm" , method = RequestMethod.GET)
		public String pftsMilestoneView (RedirectAttributes redir,HttpServletRequest req ,HttpServletResponse res ,HttpSession ses) throws Exception {
			String UserId = (String) ses.getAttribute("Username");
			String PftsFileId = req.getParameter("PftsFileId");
			String ProjectId = req.getParameter("ProjectId");
			logger.info(new Date() +"Inside checkManualDemandNo.htm "+UserId);		
			try {
//				req.setAttribute("pftsMilestoneList", service.getpftsMilestoneList());
				req.setAttribute("pftsMileDemandList", service.getpftsMileDemandList(PftsFileId));
				req.setAttribute("pftsProjectDate", service.getpftsProjectDate(ProjectId));
				req.setAttribute("PftsFileId", PftsFileId);
				req.setAttribute("ProjectId", ProjectId);
				req.setAttribute("demandNumber", req.getParameter("demandNumber"));
				return"pfts/FileMilestoneView";
			}catch (Exception e) 
			{			
				e.printStackTrace(); 
				logger.error(new Date() +" Inside checkManualDemandNo.htm "+UserId, e); 
				return "static/Error";
			}

		}
		
		
		@RequestMapping(value = "getpftsActualDate.htm", method = RequestMethod.GET)
		public @ResponseBody String getpftsActualDate(HttpServletRequest req, HttpSession ses) throws Exception
		{
			String UserId = (String) ses.getAttribute("Username");
			String PftsFileId = req.getParameter("PftsFileId");
			logger.info(new Date() +"Inside getpftsActualDate.htm "+UserId);		
			try {
					Object[] pftsActualDate=service.getpftsActualDate(PftsFileId);
					Gson json = new Gson();
					return json.toJson(pftsActualDate);
			}catch (Exception e) 
			{			
				e.printStackTrace(); 
				logger.error(new Date() +" Inside getpftsActualDate.htm "+UserId, e); 
				return null;
			}
		}
		
		
		@RequestMapping(value = "filestatus.htm", method = RequestMethod.GET)
		public @ResponseBody String filestatus(HttpServletRequest request, HttpSession ses) throws Exception
		{
			String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside getStatusEvent.htm "+UserId);		
			try {
					List<Object[]> pftsMilestoneList=service.getpftsMilestoneList();
					Gson json = new Gson();
					return json.toJson(pftsMilestoneList);
			}catch (Exception e) 
			{			
				e.printStackTrace(); 
				logger.error(new Date() +" Inside fileopen.htm "+UserId, e); 
				return null;
			}
		}
		
		@RequestMapping(value = "getActualStatus.htm", method = RequestMethod.GET)
		public @ResponseBody String getActualStatus(HttpServletRequest request, HttpSession ses) throws Exception
		{
			String UserId = (String) ses.getAttribute("Username");
			logger.info(new Date() +"Inside getStatusEvent.htm "+UserId);		
			try {
				
				String projectId = request.getParameter("projectId");
				String demandId = request.getParameter("demandId");
				Object[] getActualStatus=service.getActualStatus(projectId,demandId);
				List<String[]> list = new ArrayList<String[]>();
			
				for(int i=0;i<getActualStatus.length;i++)
				{
					String[] array = new String[2];
					array[0]=(i + 1)+"";
					array[1]=getActualStatus[i]!=null ? getActualStatus[i].toString() : null;
				    list.add(array);
				}
				Gson json = new Gson();
				return json.toJson(list);
			}catch (Exception e) 
			{			
				e.printStackTrace(); 
				logger.error(new Date() +" Inside fileopen.htm "+UserId, e); 
				return null;
			}
		}
		
		@RequestMapping(value = "procurementMilestoneDetails.htm", method = RequestMethod.GET)
		public @ResponseBody String procurementMilestoneDetails(HttpServletRequest req, HttpSession ses) throws Exception {

			List<Object[]> procurementMilestoneDetails=null;
			String UserId =(String)ses.getAttribute("Username");
			logger.info(new Date() +"Inside procurementMilestoneDetails.htm "+UserId);		
			try {
				procurementMilestoneDetails = service.getprocurementMilestoneDetails(req.getParameter("pftsid"));
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside procurementMilestoneDetails.htm "+UserId, e);
			}
			
			Gson convertedgson = new Gson();
			if(procurementMilestoneDetails!=null && procurementMilestoneDetails.size()>0) {
				return convertedgson.toJson(procurementMilestoneDetails);
			}
			    return convertedgson.toJson("-1");
		}
		
		private String redirectWithError(RedirectAttributes redir,String redirURL, String message) {
		    redir.addAttribute("resultfail", message);
		    return "redirect:/"+redirURL;
		}
		
		
		@RequestMapping(value = "updateManualDemandRemarksEdit.htm", method = RequestMethod.POST)
		public String updateManualDemandRemarksEdit(HttpServletRequest req, HttpSession ses, RedirectAttributes redir) throws Exception
		{
			String EmpId = ((Long) ses.getAttribute("EmpId")).toString();
			String Logintype= (String)ses.getAttribute("LoginType");
			String UserId = (String) ses.getAttribute("Username");
			String LabCode = (String)ses.getAttribute("labcode");
			
			logger.info(new Date() +"Inside updateManualDemandSubmit.htm "+UserId);
			
			try {

	             String fileId=req.getParameter("fileId");
	             String remarks=req.getParameter("procRemarks");
	             
	             if(InputValidator.isContainsHTMLTags(remarks)) {
	            	 
	            	 redir.addAttribute("fileId", fileId);
	 				return redirectWithError(redir, "updateManualDemand.htm", "'Remarks' should not contain HTML tags");
	 			}
	        
	             long result=0l;
	             result =service.upadteDemandFileRemarks(fileId,remarks);
	             	             
	             if(result>0) {
	     				redir.addAttribute("result","Remarks Updated Successfully");
	     			}else {
	     				redir.addAttribute("resultfail","Something went worng");
	     			}
	             
				 redir.addAttribute("fileId",fileId);
				 
				 return  "redirect:/updateManualDemand.htm";
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.info(new Date() +"Inside updateManualDemandSubmit.htm "+UserId);
				return "static/Error";
			}
		}
		
}

