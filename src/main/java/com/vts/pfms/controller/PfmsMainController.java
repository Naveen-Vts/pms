package com.vts.pfms.controller;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vts.pfms.committee.controller.ActionController;
import com.vts.pfms.service.RfpMainService;

@Controller
public class PfmsMainController {
	
	private  SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
	private  SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat rdf=new SimpleDateFormat("dd-MM-yyyy");
	@Autowired
	RfpMainService service;

	private static final Logger logger=LogManager.getLogger(PfmsMainController.class);
	@RequestMapping(value = "AdminDashBoard.htm", method = RequestMethod.GET)
	public String AdminDashBoard(HttpServletRequest req, HttpSession ses) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside AdminDashBoard.htm "+UserId);		
		String LogId = ((Long) ses.getAttribute("LoginId")).toString();
		req.setAttribute("DashBoardFormUrlList", service.DashBoardFormUrlList("1", LogId));

		return "admin/AdminDashBoard";
	}

	@RequestMapping(value = "MasterDashBoard.htm", method = RequestMethod.GET)
	public String MasterDashBoard(HttpServletRequest req, HttpSession ses) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside MasterDashBoard.htm "+UserId);	
		String LogId = ((Long) ses.getAttribute("LoginId")).toString();
		req.setAttribute("DashBoardFormUrlList", service.DashBoardFormUrlList("2", LogId));
		return "master/MasterDashBoard";
	}

	@RequestMapping(value = "ProjectDashBoard.htm", method = RequestMethod.GET)
	public String ProjectDashBoard(HttpServletRequest req, HttpSession ses) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside ProjectDashBoard.htm "+UserId);	
		String LogId = ((Long) ses.getAttribute("LoginId")).toString();
		req.setAttribute("DashBoardFormUrlList", service.DashBoardFormUrlList("3", LogId));
		return "project/ProjectDashBoard";
	}

	@RequestMapping(value = "BudgetDashBoard.htm", method = RequestMethod.GET)
	public String BudgetDashBoard(HttpServletRequest req, HttpSession ses) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside BudgetDashBoard.htm "+UserId);	
		String LogId = ((Long) ses.getAttribute("LoginId")).toString();
		req.setAttribute("DashBoardFormUrlList", service.DashBoardFormUrlList("4", LogId));
		return "budget/BudgetDashBoard";
	}

	@RequestMapping(value = "DemandDashBoard.htm", method = RequestMethod.GET)
	public String DemandDashBoard(HttpServletRequest req, HttpSession ses) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside DemandDashBoard.htm "+UserId);	
		String LogId = ((Long) ses.getAttribute("LoginId")).toString();
		req.setAttribute("DashBoardFormUrlList", service.DashBoardFormUrlList("5", LogId));
		
		return "demand/DemandDashBoard";
	}

	
	
	@RequestMapping(value = "ApprovalDashBoard.htm", method = RequestMethod.GET)
	public String ApprovalDashBoard(HttpServletRequest req, HttpSession ses) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside ApprovalDashBoard.htm "+UserId);	
		String LogId = ((Long) ses.getAttribute("LoginId")).toString();
		req.setAttribute("DashBoardFormUrlList", service.DashBoardFormUrlList("6", LogId));
		return "approval/ApprovalDashBoard";
	}

	
	@RequestMapping(value = "RfpDashBoard.htm", method = RequestMethod.GET)
	public String RfpDashBoard(HttpServletRequest req, HttpSession ses) throws Exception {
		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside RfpDashBoard.htm "+UserId);	
		String LogId = ((Long) ses.getAttribute("LoginId")).toString();
		req.setAttribute("DashBoardFormUrlList", service.DashBoardFormUrlList("7", LogId));
		return "rfp/RfpDashBoard";
	}
	
	
	@RequestMapping(value = "LoginPage/PurchaseMangementDoc2020.htm", method = RequestMethod.GET)
	public void PurchaseMangementDoc2020(HttpServletRequest req, HttpSession ses, HttpServletResponse res)
			throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside LoginPage/PurchaseMangementDoc2020.htm "+UserId);	
		String path = req.getServletContext().getRealPath("/PurchaseManuals/" + "PM_2020.pdf");

		res.setContentType("application/pdf");
		res.setHeader("Content-Disposition", String.format("inline; filename=PM_2020.pdf"));

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
	
	
	@RequestMapping(value = "LoginPage/PurchaseMangementDoc.htm", method = RequestMethod.GET)
	public void PurchaseMangementDoc(HttpServletRequest req, HttpSession ses, HttpServletResponse res)
			throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside LoginPage/PurchaseMangementDoc.htm "+UserId);	
		String path = req.getServletContext().getRealPath("/PurchaseManuals/" + "PM_2016.pdf");

		res.setContentType("application/pdf");
		res.setHeader("Content-Disposition", String.format("inline; filename=PM_2016.pdf"));

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


	@RequestMapping(value = "LoginPage/MmForms2020.htm", method = RequestMethod.GET)
	public void MmForms2020DownLoad(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside LoginPage/MmForms2020.htm "+UserId);	
		String path = req.getServletContext().getRealPath("/PurchaseManuals/" + "MM_FORMS_2020.pdf");

		res.setContentType("application/pdf");
		res.setHeader("Content-Disposition", String.format("inline; filename=MM_FORMS_2020.pdf"));

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
	
	
	
	
	
	
	@RequestMapping(value = "LoginPage/DemandInstruction.htm", method = RequestMethod.GET)
	public void DemandInstructionDownLoad(HttpServletRequest req, HttpSession ses, HttpServletResponse res) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside LoginPage/DemandInstruction.htm "+UserId);	
		String path = req.getServletContext().getRealPath("/PurchaseManuals/" + "DemandInstruction.pdf");

		res.setContentType("application/pdf");
		res.setHeader("Content-Disposition", String.format("inline; filename=DemandInstruction.pdf"));

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
	
	
	
	@RequestMapping(value = "LoginPage/DelegationOfPower.htm", method = RequestMethod.GET)
	public String DelegationOfPower(HttpServletRequest req, HttpSession ses) throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside LoginPage/DelegationOfPower.htm "+UserId);	
		return "static/DelegationOfPower";
	}
	
	@RequestMapping(value = "LoginPage/PPFMDoc2016.htm", method = RequestMethod.GET)
	public void PPFM2016(HttpServletRequest req, HttpSession ses, HttpServletResponse res)
			throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside LoginPage/PPFMDoc2016.htm "+UserId);	
		String path = req.getServletContext().getRealPath("/PurchaseManuals/" + "PPFM-2016.pdf");

		res.setContentType("application/pdf");
		res.setHeader("Content-Disposition", String.format("inline; filename=PPFM-2016.pdf"));

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
	
	@RequestMapping(value = "LoginPage/DPFMDoc2021.htm", method = RequestMethod.GET)
	public void PPFM2021(HttpServletRequest req, HttpSession ses, HttpServletResponse res)
			throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside LoginPage/DPFMDoc2021.htm "+UserId);	
		String path = req.getServletContext().getRealPath("/PurchaseManuals/" + "DPFM_2021_DIR.pdf");

		res.setContentType("application/pdf");
		res.setHeader("Content-Disposition", String.format("inline; filename=DPFM_2021_DIR.pdf"));

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
	
	
	@RequestMapping(value = "LoginPage/DPFMDoc2021Handbook.htm", method = RequestMethod.GET)
	public void DPFMDoc2021Handbook(HttpServletRequest req, HttpSession ses, HttpServletResponse res)
			throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside LoginPage/DPFMDoc2021Handbook.htm "+UserId);	
		String path = req.getServletContext().getRealPath("/PurchaseManuals/" + "DPFM_2021_HB.pdf");

		res.setContentType("application/pdf");
		res.setHeader("Content-Disposition", String.format("inline; filename=DPFM_2021_HB.pdf"));

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
	
	@RequestMapping(value = "LoginPage/DPFMDoc2026.htm", method = RequestMethod.GET)
	public void DPFMDoc2026Handbook(HttpServletRequest req, HttpSession ses, HttpServletResponse res)
			throws Exception {

		String UserId = (String) ses.getAttribute("Username");
		logger.info(new Date() +"Inside LoginPage/DPFMDoc2026.htm "+UserId);	
		String path = req.getServletContext().getRealPath("/PurchaseManuals/" + "DPFM-2026.pdf");

		res.setContentType("application/pdf");
		res.setHeader("Content-Disposition", String.format("inline; filename=DPFM-2026.pdf"));

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


	
	@RequestMapping(value = "SmsReportList.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String SmsReport(HttpServletRequest req, HttpSession ses) throws Exception {
		    String UserId = (String) ses.getAttribute("Username");
		    logger.info(new Date() +"SmsReportList.htm"+req.getUserPrincipal().getName());
		    try {
					String fromDate=(String)req.getParameter("FromDate");
					String toDate=(String)req.getParameter("ToDate");
					if(fromDate==null || toDate == null) 
					{
						fromDate = LocalDate.now().toString();
						toDate  = LocalDate.now().toString();
					}else
					{
						fromDate=sdf2.format(rdf.parse(fromDate));
						toDate=sdf2.format(rdf.parse(toDate));
					}
					req.setAttribute("SmsReportList", service.SmsReportList(fromDate,toDate));
					req.setAttribute("frmDt", fromDate);
					req.setAttribute("toDt",   toDate);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside SmsReportList.htm "+UserId, e);
		   }
		
		return "action/SmsReportList";

	}
    
	
	@RequestMapping(value = "SmsActionReportExcel.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public void SmsReportExcel(HttpServletRequest req,HttpServletResponse resp,HttpSession ses) throws Exception{
		String UserId = (String) ses.getAttribute("Username");
		int RowNo=0;
		logger.info(new Date() + " Inside SmsActionReportExcel.htm " + UserId);
		try {
			String fromDate=(String)req.getParameter("FromDate");
			String toDate=(String)req.getParameter("ToDate");
			
			if(fromDate!=null && toDate!=null) {
				fromDate=sdf2.format(rdf.parse(fromDate));
				toDate=sdf2.format(rdf.parse(toDate));
			}
			List<Object[]> SmsReportList=service.SmsReportList(fromDate,toDate);
			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("Download Excel");
			sheet.setColumnWidth(0, 1000);
			sheet.setColumnWidth(1, 7000);
			sheet.setColumnWidth(2, 4000);
			sheet.setColumnWidth(3, 8000);
			sheet.setColumnWidth(4, 3000);
			sheet.setColumnWidth(5, 4000);
			sheet.setColumnWidth(6, 4000);
			sheet.setColumnWidth(7, 4000);
			sheet.setColumnWidth(8, 5000);
			sheet.setColumnWidth(9, 4000);
			sheet.setColumnWidth(10, 4000);
			sheet.setColumnWidth(11, 4000);
			sheet.setColumnWidth(12, 4000);
			sheet.setColumnWidth(13, 5000);
			
			XSSFFont font = ((XSSFWorkbook) workbook).createFont();
			font.setFontName("Times New Roman");
			font.setFontHeightInPoints((short) 10);
			font.setBold(true);
			// style for table header i.e Subtitle or Report Title
						CellStyle t_header_style = workbook.createCellStyle();
						t_header_style.setLocked(true);
						t_header_style.setFont(font);
						t_header_style.setWrapText(true);
						t_header_style.setAlignment(HorizontalAlignment.CENTER);
						t_header_style.setVerticalAlignment(VerticalAlignment.CENTER);
						// style for table cells
						CellStyle t_body_style = workbook.createCellStyle();
						t_body_style.setWrapText(true);
						
						CellStyle styleRight = workbook.createCellStyle();
						styleRight.setAlignment(HorizontalAlignment.RIGHT);
						
						CellStyle styleLeft = workbook.createCellStyle();
						styleLeft.setAlignment(HorizontalAlignment.LEFT);
						
						CellStyle styleCenter = workbook.createCellStyle();
						styleCenter.setAlignment(HorizontalAlignment.CENTER);

						// style for file header
						CellStyle file_header_Style = workbook.createCellStyle();
						file_header_Style.setLocked(true);
						file_header_Style.setFont(font);
						file_header_Style.setWrapText(true);
						file_header_Style.setAlignment(HorizontalAlignment.CENTER);
						file_header_Style.setVerticalAlignment(VerticalAlignment.CENTER);
						
						
						String Excel="SMS Action Report Excel List ";
						Row file_header_row = sheet.createRow(RowNo++);
						sheet.addMergedRegion(new CellRangeAddress(0, 0,0, 13));   // Merging Header Cells 
						Cell cell= file_header_row.createCell(0);
						cell.setCellValue(""+Excel+ " "+"( "+sdf.format(sdf2.parse(fromDate))+" to "+sdf.format(sdf2.parse(toDate)) +")");
						file_header_row.setHeightInPoints((2*sheet.getDefaultRowHeightInPoints()));
						cell.setCellStyle(file_header_Style);
						// File header Row 2
						
						Row t_header_row = sheet.createRow(RowNo++);
						cell= t_header_row.createCell(0); 
						cell.setCellValue("SN"); 
						cell.setCellStyle(t_header_style);
						
						cell= t_header_row.createCell(1); 
						cell.setCellValue("Employee"); 
						cell.setCellStyle(t_header_style);
						
						cell= t_header_row.createCell(2); 
						cell.setCellValue("Mobile No"); 
						cell.setCellStyle(t_header_style);
						
						cell= t_header_row.createCell(3); 
						cell.setCellValue("Message"); 
						cell.setCellStyle(t_header_style);
						
						cell= t_header_row.createCell(4); 
						cell.setCellValue("ActionItemP"); 
						cell.setCellStyle(t_header_style);

						cell= t_header_row.createCell(5); 
						cell.setCellValue("ActionItemTP"); 
						cell.setCellStyle(t_header_style);
						
						cell= t_header_row.createCell(6); 
						cell.setCellValue("ActionItemDP"); 
						cell.setCellStyle(t_header_style);
						
						cell= t_header_row.createCell(7); 
						cell.setCellValue("MilestoneActionP"); 
						cell.setCellStyle(t_header_style);
						
						cell= t_header_row.createCell(8); 
						cell.setCellValue("MilestoneActionTP"); 
						cell.setCellStyle(t_header_style);
						
						cell= t_header_row.createCell(9); 
						cell.setCellValue("MilestoneActionDP"); 
						cell.setCellStyle(t_header_style);
						
						cell= t_header_row.createCell(10); 
						cell.setCellValue("MeetingActionP"); 
						cell.setCellStyle(t_header_style);
						
						cell= t_header_row.createCell(11); 
						cell.setCellValue("MeetingActionTP"); 
						cell.setCellStyle(t_header_style);
						
						cell= t_header_row.createCell(12); 
						cell.setCellValue("MeetingActionDP"); 
						cell.setCellStyle(t_header_style);
						
						cell= t_header_row.createCell(13); 
						cell.setCellValue("Sms SentDate"); 
						cell.setCellStyle(t_header_style);
						
				long slno=1;
				if(SmsReportList!=null && SmsReportList.size()>0){
					for(Object[] obj:SmsReportList){
			     	Row t_body_row=sheet.createRow(RowNo);
				
			    	 t_body_row=sheet.createRow(RowNo++);
					
					 cell= t_body_row.createCell(0); 
					 cell.setCellValue(slno); 
					 cell.setCellStyle(t_body_style);
					 cell.setCellStyle(styleCenter);
					 
					 
					 cell= t_body_row.createCell(1); 
					 if(obj[0]!=null && obj[1]!=null) {
					 cell.setCellValue(obj[0].toString()+", "+obj[1].toString()); 
					 }else {
						 cell.setCellValue("-");
					 }
					 cell.setCellStyle(t_body_style);
					 cell.setCellStyle(styleLeft);
					 
				     cell= t_body_row.createCell(2); 
				     if(obj[11]!=null) {
					 cell.setCellValue(obj[11].toString());
				     }else {
				     cell.setCellValue("-");
				     }
					 cell.setCellStyle(t_body_style);
					 cell.setCellStyle(styleRight);
					 
					 cell= t_body_row.createCell(3);
					 if(obj[12]!=null) {
					 cell.setCellValue(obj[12].toString());
					 }else {
					 cell.setCellValue("-");
					 }
					 cell.setCellStyle(t_body_style);
					 cell.setCellStyle(styleRight);
					 
					 cell= t_body_row.createCell(4); 
					 if(obj[2]!=null) {
					 cell.setCellValue(obj[2].toString()); 
					 }else {
					 cell.setCellValue("-");
					 }
					 cell.setCellStyle(t_body_style);
					 cell.setCellStyle(styleRight);
					 
					 cell= t_body_row.createCell(5); 
					 if(obj[3]!=null) {
					 cell.setCellValue(obj[3].toString()); 
					 }else {
					 cell.setCellValue("-");	 
					 }
					 cell.setCellStyle(t_body_style);
					 cell.setCellStyle(styleRight);
					 
					 cell= t_body_row.createCell(6); 
					 if(obj[4]!=null) {
					 cell.setCellValue(obj[4].toString()); 
					 }else {
					 cell.setCellValue("-");
					 }
					 cell.setCellStyle(t_body_style);
					 cell.setCellStyle(styleRight);
					 
					 cell= t_body_row.createCell(7); 
					 if(obj[5]!=null) {
					 cell.setCellValue(obj[5].toString()); 
					 }else {
					 cell.setCellValue("-");
					 }
					 cell.setCellStyle(t_body_style);
					 cell.setCellStyle(styleRight);
					 
					 cell= t_body_row.createCell(8); 
					 if(obj[6]!=null) {
					 cell.setCellValue(obj[6].toString()); 
					 }else {
					 cell.setCellValue("-");
					 }
					 cell.setCellStyle(t_body_style);
					 cell.setCellStyle(styleRight);
					 
					 cell= t_body_row.createCell(9); 
					 if(obj[7]!=null) {
					 cell.setCellValue(obj[7].toString()); 
					 }else {
					 cell.setCellValue("-");
					 }
					 cell.setCellStyle(t_body_style);
					 cell.setCellStyle(styleRight);
					 
					 cell= t_body_row.createCell(10); 
					 if(obj[8]!=null) {
					 cell.setCellValue(obj[8].toString()); 
					 }else {
					 cell.setCellValue("-");
					 }
					 cell.setCellStyle(t_body_style);
					 cell.setCellStyle(styleRight);
					 
					 cell= t_body_row.createCell(11); 
					 if(obj[9]!=null) {
					 cell.setCellValue(obj[9].toString()); 
					 }else {
					 cell.setCellValue("-");
					 }
					 cell.setCellStyle(t_body_style);
					 cell.setCellStyle(styleRight);
					 
					 cell= t_body_row.createCell(12); 
					 if(obj[10]!=null) {
					 cell.setCellValue(obj[10].toString()); 
					 }else {
					 cell.setCellValue("-");
					 }
					 cell.setCellStyle(t_body_style);
					 cell.setCellStyle(styleRight);
					 
					 cell= t_body_row.createCell(13); 
					 if(obj[13]!=null) {
						 cell.setCellValue(sdf.format(obj[13])); 
				         }else {
				         cell.setCellValue("NA"); 
				         }
					 cell.setCellStyle(t_body_style);
					 cell.setCellStyle(styleCenter);
					 
					 slno++;
					}
					}
					
	
				String path = req.getServletContext().getRealPath("/view/temp");
				String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";
				
				FileOutputStream outputStream = new FileOutputStream(fileLocation);
				workbook.write(outputStream);
				workbook.close();

				String filename="SMS Action Report Excel ";
				resp.setContentType("application/octet-stream");
				resp.setHeader("Content-disposition", "attachment; filename="+filename+".xlsx");
				File f=new File(fileLocation);
				FileInputStream fis = new FileInputStream(f);
			    DataOutputStream os = new DataOutputStream(resp.getOutputStream());
		        resp.setHeader("Content-Length",String.valueOf(f.length()));
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = fis.read(buffer)) >= 0)
				{
				    os.write(buffer, 0, len);
				} 
				os.close();
				fis.close();
		
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "PMSSmsReportListExcel.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public void PMSSmsReportListExcel(HttpServletRequest req,HttpServletResponse resp,HttpSession ses) throws Exception{
		String UserId = (String) ses.getAttribute("Username");
		int RowNo=0;
		logger.info(new Date() + " Inside PMSSmsReportListExcel.htm " + UserId);
		try {
			String fromDate=(String)req.getParameter("FromDate");
			String toDate=(String)req.getParameter("ToDate");
			
			if(fromDate!=null && toDate!=null) {
				fromDate=sdf2.format(rdf.parse(fromDate));
				toDate=sdf2.format(rdf.parse(toDate));
			}
			List<Object[]> SmsReportList=service.SmsReportList(fromDate,toDate);
			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("Download Excel");
			sheet.setColumnWidth(0, 1000);
			sheet.setColumnWidth(1, 5000);
			sheet.setColumnWidth(2, 8000);
			
			XSSFFont font = ((XSSFWorkbook) workbook).createFont();
			font.setFontName("Times New Roman");
			font.setFontHeightInPoints((short) 10);
			font.setBold(true);
			// style for table header i.e Subtitle or Report Title
						CellStyle t_header_style = workbook.createCellStyle();
						t_header_style.setLocked(true);
						t_header_style.setFont(font);
						t_header_style.setWrapText(true);
						t_header_style.setAlignment(HorizontalAlignment.CENTER);
						t_header_style.setVerticalAlignment(VerticalAlignment.CENTER);
						// style for table cells
						CellStyle t_body_style = workbook.createCellStyle();
						t_body_style.setWrapText(true);
						
						CellStyle styleRight = workbook.createCellStyle();
						styleRight.setAlignment(HorizontalAlignment.RIGHT);
						
						CellStyle styleLeft = workbook.createCellStyle();
						styleLeft.setAlignment(HorizontalAlignment.LEFT);
						
						CellStyle styleCenter = workbook.createCellStyle();
						styleCenter.setAlignment(HorizontalAlignment.CENTER);

						// style for file header
						CellStyle file_header_Style = workbook.createCellStyle();
						file_header_Style.setLocked(true);
						file_header_Style.setFont(font);
						file_header_Style.setWrapText(true);
						file_header_Style.setAlignment(HorizontalAlignment.CENTER);
						file_header_Style.setVerticalAlignment(VerticalAlignment.CENTER);
						
						
						String Excel="SMS Action Report";
						Row file_header_row = sheet.createRow(RowNo++);
						sheet.addMergedRegion(new CellRangeAddress(0, 0,0, 2));   // Merging Header Cells 
						Cell cell= file_header_row.createCell(0);
						cell.setCellValue(""+Excel+ " "+"( "+sdf.format(sdf2.parse(fromDate))+" to "+sdf.format(sdf2.parse(toDate)) +")");
						file_header_row.setHeightInPoints((2*sheet.getDefaultRowHeightInPoints()));
						cell.setCellStyle(file_header_Style);
						// File header Row 2
						
						Row t_header_row = sheet.createRow(RowNo++);
						cell= t_header_row.createCell(0); 
						cell.setCellValue("SN"); 
						cell.setCellStyle(t_header_style);
						
						cell= t_header_row.createCell(1); 
						cell.setCellValue("Mobile No"); 
						cell.setCellStyle(t_header_style);
						
						cell= t_header_row.createCell(2); 
						cell.setCellValue("Message"); 
						cell.setCellStyle(t_header_style);
						
						
				long slno=1;
				if(SmsReportList!=null && SmsReportList.size()>0){
					for(Object[] obj:SmsReportList){
			     	Row t_body_row=sheet.createRow(RowNo);
				
			    	 t_body_row=sheet.createRow(RowNo++);
					
					 cell= t_body_row.createCell(0); 
					 cell.setCellValue(slno); 
					 cell.setCellStyle(t_body_style);
					 cell.setCellStyle(styleCenter);
					 
				     cell= t_body_row.createCell(1); 
				     if(obj[11]!=null) {
					 cell.setCellValue(obj[11].toString());
				     }else {
				     cell.setCellValue("-");
				     }
					 cell.setCellStyle(t_body_style);
					 cell.setCellStyle(styleRight);
					 
					 cell= t_body_row.createCell(2);
					 if(obj[12]!=null) {
					 cell.setCellValue(obj[12].toString());
					 }else {
					 cell.setCellValue("-");
					 }
					 cell.setCellStyle(t_body_style);
					 cell.setCellStyle(styleRight);
					 
					 slno++;
					}
					}
					
	
				String path = req.getServletContext().getRealPath("/view/temp");
				String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";
				
				FileOutputStream outputStream = new FileOutputStream(fileLocation);
				workbook.write(outputStream);
				workbook.close();

				String filename="SMS Action Report";
				resp.setContentType("application/octet-stream");
				resp.setHeader("Content-disposition", "attachment; filename="+filename+".xlsx");
				File f=new File(fileLocation);
				FileInputStream fis = new FileInputStream(f);
			    DataOutputStream os = new DataOutputStream(resp.getOutputStream());
		        resp.setHeader("Content-Length",String.valueOf(f.length()));
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = fis.read(buffer)) >= 0)
				{
				    os.write(buffer, 0, len);
				} 
				os.close();
				fis.close();
		
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@RequestMapping(value = "SmsCommitteReportList.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public String SmsCommitteReportList(HttpServletRequest req, HttpSession ses) throws Exception {
		    String UserId = (String) ses.getAttribute("Username");
		    logger.info(new Date() +"SmsCommitteReportList.htm"+req.getUserPrincipal().getName());
		    try {
					String fromDate=(String)req.getParameter("FromDate");
					String toDate=(String)req.getParameter("ToDate");
					if(fromDate==null || toDate == null) 
					{
						fromDate = LocalDate.now().toString();
						toDate  = LocalDate.now().toString();
					}else
					{
						fromDate=sdf2.format(rdf.parse(fromDate));
						toDate=sdf2.format(rdf.parse(toDate));
					}
					req.setAttribute("SmsCommitteReportList", service.SmsCommitteReportList(fromDate,toDate));
					req.setAttribute("frmDt", fromDate);
					req.setAttribute("toDt",   toDate);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" Inside SmsCommitteReportList.htm "+UserId, e);
		   }
		
		return "action/SmsCommitteReportList";
	}
	
	
	@RequestMapping(value = "SmsCommitteReportExcel.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public void SmsCommitteReportExcel(HttpServletRequest req,HttpServletResponse resp,HttpSession ses) throws Exception{
		String UserId = (String) ses.getAttribute("Username");
		int RowNo=0;
		logger.info(new Date() + " Inside SmsCommitteReportExcel.htm " + UserId);
		try {
			String fromDate=(String)req.getParameter("FromDate");
			String toDate=(String)req.getParameter("ToDate");
			
			if(fromDate!=null && toDate!=null) {
				fromDate=sdf2.format(rdf.parse(fromDate));
				toDate=sdf2.format(rdf.parse(toDate));
			}
			List<Object[]> SmsCommitteReportList=service.SmsCommitteReportList(fromDate,toDate);
			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("Download Excel");
			sheet.setColumnWidth(0, 1000);
			sheet.setColumnWidth(1, 7000);
			sheet.setColumnWidth(2, 4000);
			sheet.setColumnWidth(3, 8000);
			sheet.setColumnWidth(4, 3000);
			
			XSSFFont font = ((XSSFWorkbook) workbook).createFont();
			font.setFontName("Times New Roman");
			font.setFontHeightInPoints((short) 10);
			font.setBold(true);
			// style for table header i.e Subtitle or Report Title
						CellStyle t_header_style = workbook.createCellStyle();
						t_header_style.setLocked(true);
						t_header_style.setFont(font);
						t_header_style.setWrapText(true);
						t_header_style.setAlignment(HorizontalAlignment.CENTER);
						t_header_style.setVerticalAlignment(VerticalAlignment.CENTER);
						// style for table cells
						CellStyle t_body_style = workbook.createCellStyle();
						t_body_style.setWrapText(true);
						
						CellStyle styleRight = workbook.createCellStyle();
						styleRight.setAlignment(HorizontalAlignment.RIGHT);
						
						CellStyle styleLeft = workbook.createCellStyle();
						styleLeft.setAlignment(HorizontalAlignment.LEFT);
						
						CellStyle styleCenter = workbook.createCellStyle();
						styleCenter.setAlignment(HorizontalAlignment.CENTER);

						// style for file header
						CellStyle file_header_Style = workbook.createCellStyle();
						file_header_Style.setLocked(true);
						file_header_Style.setFont(font);
						file_header_Style.setWrapText(true);
						file_header_Style.setAlignment(HorizontalAlignment.CENTER);
						file_header_Style.setVerticalAlignment(VerticalAlignment.CENTER);
						
						
						String Excel="SMS Committe Report Excel List ";
						Row file_header_row = sheet.createRow(RowNo++);
						sheet.addMergedRegion(new CellRangeAddress(0, 0,0, 4));   // Merging Header Cells 
						Cell cell= file_header_row.createCell(0);
						cell.setCellValue(""+Excel+ " "+"( "+sdf.format(sdf2.parse(fromDate))+" to "+sdf.format(sdf2.parse(toDate)) +")");
						file_header_row.setHeightInPoints((2*sheet.getDefaultRowHeightInPoints()));
						cell.setCellStyle(file_header_Style);
						// File header Row 2
						
						Row t_header_row = sheet.createRow(RowNo++);
						cell= t_header_row.createCell(0); 
						cell.setCellValue("SN"); 
						cell.setCellStyle(t_header_style);
						
						cell= t_header_row.createCell(1); 
						cell.setCellValue("Employee"); 
						cell.setCellStyle(t_header_style);
						
						cell= t_header_row.createCell(2); 
						cell.setCellValue("Mobile No"); 
						cell.setCellStyle(t_header_style);
						
						cell= t_header_row.createCell(3); 
						cell.setCellValue("Message"); 
						cell.setCellStyle(t_header_style);
						
						cell= t_header_row.createCell(4); 
						cell.setCellValue("Sms SentDate"); 
						cell.setCellStyle(t_header_style);
						
				long slno=1;
				if(SmsCommitteReportList!=null && SmsCommitteReportList.size()>0){
					for(Object[] obj:SmsCommitteReportList){
			     	Row t_body_row=sheet.createRow(RowNo);
				
			    	 t_body_row=sheet.createRow(RowNo++);
					
					 cell= t_body_row.createCell(0); 
					 cell.setCellValue(slno); 
					 cell.setCellStyle(t_body_style);
					 cell.setCellStyle(styleCenter);
					 
					 
					 cell= t_body_row.createCell(1); 
					 if(obj[0]!=null && obj[1]!=null) {
					 cell.setCellValue(obj[0].toString()+", "+obj[1].toString()); 
					 }else {
						 cell.setCellValue("-");
					 }
					 cell.setCellStyle(t_body_style);
					 cell.setCellStyle(styleLeft);
					 
				     cell= t_body_row.createCell(2); 
				     if(obj[2]!=null) {
					 cell.setCellValue(obj[2].toString());
				     }else {
				     cell.setCellValue("-");
				     }
					 cell.setCellStyle(t_body_style);
					 cell.setCellStyle(styleRight);
					 
					 cell= t_body_row.createCell(3);
					 if(obj[3]!=null) {
					 cell.setCellValue(obj[3].toString());
					 }else {
					 cell.setCellValue("-");
					 }
					 cell.setCellStyle(t_body_style);
					 cell.setCellStyle(styleRight);
					 
					 cell= t_body_row.createCell(4); 
					 if(obj[13]!=null) {
						 cell.setCellValue(sdf.format(obj[4])); 
				         }else {
				         cell.setCellValue("NA"); 
				         }
					 cell.setCellStyle(t_body_style);
					 cell.setCellStyle(styleCenter);
					 
					 slno++;
					}
					}
					
	
				String path = req.getServletContext().getRealPath("/view/temp");
				String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";
				
				FileOutputStream outputStream = new FileOutputStream(fileLocation);
				workbook.write(outputStream);
				workbook.close();

				String filename="SMS Committe Report Excel ";
				resp.setContentType("application/octet-stream");
				resp.setHeader("Content-disposition", "attachment; filename="+filename+".xlsx");
				File f=new File(fileLocation);
				FileInputStream fis = new FileInputStream(f);
			    DataOutputStream os = new DataOutputStream(resp.getOutputStream());
		        resp.setHeader("Content-Length",String.valueOf(f.length()));
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = fis.read(buffer)) >= 0)
				{
				    os.write(buffer, 0, len);
				} 
				os.close();
				fis.close();
		
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "PMSSmsCommitteReportListExcel.htm", method = {RequestMethod.GET,RequestMethod.POST})
	public void PMSSmsCommitteReportListExcel(HttpServletRequest req,HttpServletResponse resp,HttpSession ses) throws Exception{
		String UserId = (String) ses.getAttribute("Username");
		int RowNo=0;
		logger.info(new Date() + " Inside PMSSmsCommitteReportListExcel.htm " + UserId);
		try {
			String fromDate=(String)req.getParameter("FromDate");
			String toDate=(String)req.getParameter("ToDate");
			
			if(fromDate!=null && toDate!=null) {
				fromDate=sdf2.format(rdf.parse(fromDate));
				toDate=sdf2.format(rdf.parse(toDate));
			}
			List<Object[]> SmsCommitteReportList=service.SmsCommitteReportList(fromDate,toDate);
			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("Download Excel");
			sheet.setColumnWidth(0, 1000);
			sheet.setColumnWidth(1, 4000);
			sheet.setColumnWidth(2, 8000);
			
			XSSFFont font = ((XSSFWorkbook) workbook).createFont();
			font.setFontName("Times New Roman");
			font.setFontHeightInPoints((short) 10);
			font.setBold(true);
			// style for table header i.e Subtitle or Report Title
						CellStyle t_header_style = workbook.createCellStyle();
						t_header_style.setLocked(true);
						t_header_style.setFont(font);
						t_header_style.setWrapText(true);
						t_header_style.setAlignment(HorizontalAlignment.CENTER);
						t_header_style.setVerticalAlignment(VerticalAlignment.CENTER);
						// style for table cells
						CellStyle t_body_style = workbook.createCellStyle();
						t_body_style.setWrapText(true);
						
						CellStyle styleRight = workbook.createCellStyle();
						styleRight.setAlignment(HorizontalAlignment.RIGHT);
						
						CellStyle styleLeft = workbook.createCellStyle();
						styleLeft.setAlignment(HorizontalAlignment.LEFT);
						
						CellStyle styleCenter = workbook.createCellStyle();
						styleCenter.setAlignment(HorizontalAlignment.CENTER);

						// style for file header
						CellStyle file_header_Style = workbook.createCellStyle();
						file_header_Style.setLocked(true);
						file_header_Style.setFont(font);
						file_header_Style.setWrapText(true);
						file_header_Style.setAlignment(HorizontalAlignment.CENTER);
						file_header_Style.setVerticalAlignment(VerticalAlignment.CENTER);
						
						
						String Excel="SMS Committe Report";
						Row file_header_row = sheet.createRow(RowNo++);
						sheet.addMergedRegion(new CellRangeAddress(0, 0,0, 2));   // Merging Header Cells 
						Cell cell= file_header_row.createCell(0);
						cell.setCellValue(""+Excel+ " "+"( "+sdf.format(sdf2.parse(fromDate))+" to "+sdf.format(sdf2.parse(toDate)) +")");
						file_header_row.setHeightInPoints((2*sheet.getDefaultRowHeightInPoints()));
						cell.setCellStyle(file_header_Style);
						// File header Row 2
						
						Row t_header_row = sheet.createRow(RowNo++);
						cell= t_header_row.createCell(0); 
						cell.setCellValue("SN"); 
						cell.setCellStyle(t_header_style);
						
						cell= t_header_row.createCell(1); 
						cell.setCellValue("Mobile No"); 
						cell.setCellStyle(t_header_style);
						
						cell= t_header_row.createCell(2); 
						cell.setCellValue("Message"); 
						cell.setCellStyle(t_header_style);
						
						
				long slno=1;
				if(SmsCommitteReportList!=null && SmsCommitteReportList.size()>0){
					for(Object[] obj:SmsCommitteReportList){
			     	Row t_body_row=sheet.createRow(RowNo);
				
			    	 t_body_row=sheet.createRow(RowNo++);
					
					 cell= t_body_row.createCell(0); 
					 cell.setCellValue(slno); 
					 cell.setCellStyle(t_body_style);
					 cell.setCellStyle(styleCenter);
					 
				     cell= t_body_row.createCell(1); 
				     if(obj[2]!=null) {
					 cell.setCellValue(obj[2].toString());
				     }else {
				     cell.setCellValue("-");
				     }
					 cell.setCellStyle(t_body_style);
					 cell.setCellStyle(styleRight);
					 
					 cell= t_body_row.createCell(2);
					 if(obj[3]!=null) {
					 cell.setCellValue(obj[3].toString());
					 }else {
					 cell.setCellValue("-");
					 }
					 cell.setCellStyle(t_body_style);
					 cell.setCellStyle(styleRight);
					 slno++;
					}
					}
					
	
				String path = req.getServletContext().getRealPath("/view/temp");
				String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";
				
				FileOutputStream outputStream = new FileOutputStream(fileLocation);
				workbook.write(outputStream);
				workbook.close();

				String filename="SMS Committe Report";
				resp.setContentType("application/octet-stream");
				resp.setHeader("Content-disposition", "attachment; filename="+filename+".xlsx");
				File f=new File(fileLocation);
				FileInputStream fis = new FileInputStream(f);
			    DataOutputStream os = new DataOutputStream(resp.getOutputStream());
		        resp.setHeader("Content-Length",String.valueOf(f.length()));
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = fis.read(buffer)) >= 0)
				{
				    os.write(buffer, 0, len);
				} 
				os.close();
				fis.close();
		
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
