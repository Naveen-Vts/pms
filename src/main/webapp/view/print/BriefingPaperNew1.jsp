<%@page import="com.vts.pfms.milestone.model.ProjectEconomicImpact"%>
<%@page import="com.vts.pfms.milestone.dto.ProjectUtilizationBriefingDto"%>
<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.nio.file.Paths"%>
<%@page import="java.nio.file.Path"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.time.LocalTime"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.net.Inet4Address"%>
<%@page import="java.time.temporal.ChronoUnit"%>
<%@page import="com.vts.pfms.committee.model.Committee"%>
<%@page import="java.time.LocalDate"%>
<%@page import="com.vts.pfms.print.model.TechImages"%>
<%@page import="java.io.File"%>
<%@page import="org.apache.commons.io.FileUtils"%>
<%@page import="java.util.Base64"%>
<%@page import="org.apache.commons.io.FilenameUtils"%>
<%@page import="com.vts.pfms.model.LabMaster"%>
<%@page import="com.vts.pfms.AESCryptor"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder"%>
<%@page import="java.net.InetAddress"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="java.text.Format"%>
<%@page import="java.util.Locale"%>
<%@page import="com.vts.pfms.master.dto.ProjectFinancialDetails"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="java.util.* , java.util.stream.Collectors"%> 
<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@page import="com.vts.pfms.model.TotalDemand" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>  

<% String wordFlag = (String)request.getAttribute("wordFlag"); %>
    
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">

<%if(wordFlag!=null && wordFlag.equalsIgnoreCase("Y")) { %>	 
	<script src="./webjars/jquery/3.4.0/jquery.min.js"></script>
	<spring:url value="/resources/js/FileSaver.min.js" var="FileSaver" />
	<script src="${FileSaver}"></script>
	<spring:url value="/resources/js/jquery.wordexport.js" var="wordexport" />
	<script src="${wordexport}"></script>
	<script src="${wordexport}"></script>
<%} %>


<title>Briefing Paper</title>


<%

Committee committee=(Committee)request.getAttribute("committeeData");
String isprint=(String)request.getAttribute("isprint"); 
List<Object[]> projectattributes = (List<Object[]> )request.getAttribute("projectattributes");
String ProjectCode="";
for(int i=0;i<projectattributes.size();i++){
	ProjectCode = ProjectCode +projectattributes.get(i)[0].toString()  ;
	if(i!=projectattributes.size()-1)ProjectCode=ProjectCode+"/";
}
List<TotalDemand> totalprocurementdetails = (List<TotalDemand>)request.getAttribute("TotalProcurementDetails");
String lablogo=(String)request.getAttribute("lablogo");
LabMaster labInfo=(LabMaster)request.getAttribute("labInfo");
String filePath=(String)request.getAttribute("filePath");
String projectLabCode=(String)request.getAttribute("projectLabCode");
SimpleDateFormat inputFormat = new SimpleDateFormat("ddMMMyyyy",Locale.ENGLISH);
SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");	
Object[] committeeMetingsCount =  (Object[]) request.getAttribute("committeeMetingsCount");
String CommitteeCode = committee.getCommitteeShortName().trim();
String No2 = CommitteeCode+(Long.parseLong(committeeMetingsCount[1].toString())+1);

List<Object[]> otherMeetingList = (List<Object[]>)request.getAttribute("otherMeetingList");

List<List<Object[]>> overallfinance = (List<List<Object[]>>)request.getAttribute("overallfinance");//b
String IsIbasConnected=(String)request.getAttribute("IsIbasConnected");
String thankYouImg = (String)request.getAttribute("thankYouImg");
%>

</head>

<%
DecimalFormat df=new DecimalFormat("####################.##");
FormatConverter fc=new FormatConverter(); 
SimpleDateFormat sdf=fc.getRegularDateFormat();
SimpleDateFormat sdf1=fc.getSqlDateFormat(); int addcount=0; 
NFormatConvertion nfc=new NFormatConvertion();
Format format = com.ibm.icu.text.NumberFormat.getCurrencyInstance(new Locale("en", "in"));

String projectid=(String)request.getAttribute("projectid");
List<Object[]> projectdatadetails = (List<Object[]> )request.getAttribute("projectdatadetails");
List<List<Object[]>> lastpmrcminsactlist = (List<List<Object[]>>)request.getAttribute("lastpmrcminsactlist");
List<List<Object[]>> lastpmrcactions = (List<List<Object[]>>)request.getAttribute("lastpmrcactions");

List<Object[]> TechWorkDataList=(List<Object[]>)request.getAttribute("TechWorkDataList");
List<List<Object[]>> milestones= (List<List<Object[]>>)request.getAttribute("milestones");
List<List<ProjectFinancialDetails>> projectFinancialDetails =(List<List<ProjectFinancialDetails>>)request.getAttribute("financialDetails");
List<List<Object[]>> ganttchartlist=(List<List<Object[]>>)request.getAttribute("ganttchartlist"); 
List<List<Object[]>> oldpmrcissueslist=(List<List<Object[]>>)request.getAttribute("oldpmrcissueslist");
List<List<Object[]>> riskmatirxdata = (List<List<Object[]>> )request.getAttribute("riskmatirxdata");
List<List<Object[]>> procurementOnDemand = (List<List<Object[]>>)request.getAttribute("procurementOnDemandlist");
List<List<Object[]>> procurementOnSanction = (List<List<Object[]>>)request.getAttribute("procurementOnSanctionlist");
List<List<Object[]>> actionplanthreemonths = (List<List<Object[]>>)request.getAttribute("actionplanthreemonths");
List<Object[]> ProjectDetail=(List<Object[]>)request.getAttribute("ProjectDetails");
List<String> projectidlist = (List<String>)request.getAttribute("projectidlist");
List<List<Object[]>> ProjectRevList = (List<List<Object[]>>)request.getAttribute("ProjectRevList");
List<List<Object[]>> ebandpmrccount = (List<List<Object[]>>)request.getAttribute("ebandpmrccount");

//List<List<Object[]>> ReviewMeetingList=(List<List<Object[]>>)request.getAttribute("ReviewMeetingList");
//List<List<Object[]>> ReviewMeetingListPMRC=(List<List<Object[]>>)request.getAttribute("ReviewMeetingListPMRC");
Map<String, List<Object[]>> reviewMeetingListMap = (Map<String, List<Object[]>>) request.getAttribute("reviewMeetingListMap");
List<Object[]> RiskTypes = (List<Object[]>)request.getAttribute("RiskTypes");
AESCryptor cryptor = new AESCryptor();
long ProjectCost = (long)request.getAttribute("ProjectCost");

List<List<TechImages>> TechImages = (List<List<TechImages>>)request.getAttribute("TechImages");

List<Object[]> MilestoneList=(List<Object[]>)request.getAttribute("MilestoneActivityList");
String levelid= (String) request.getAttribute("levelid");
List<List<Object[]>> MilestoneDetails6 = (List<List<Object[]>>)request.getAttribute("milestonedatalevel6");//c

String AppFilesPath= (String) request.getAttribute("AppFilesPath");
Object[] nextMeetVenue =  (Object[]) request.getAttribute("nextMeetVenue");

String text=(String)request.getAttribute("text");
List<Object[]> RecDecDetails = (List<Object[]>)request.getAttribute("recdecDetails");
//newly added on 13th sept
	Map<Integer,String> committeeWiseMap=(Map<Integer,String>)request.getAttribute("committeeWiseMap");
	//Map<Integer,String> mapEB=(Map<Integer,String>)request.getAttribute("mapEB");
	Map<Integer,String> treeMapLevOne =(Map<Integer,String>)request.getAttribute("treeMapLevOne");
	Map<Integer,String> treeMapLevTwo =(Map<Integer,String>)request.getAttribute("treeMapLevTwo");

Committee committeeData = (Committee) request.getAttribute("committeeData");
LocalDate before6months = LocalDate.now().minusDays(committeeData.getPeriodicDuration());
 
List<Object[]> envisagedDemandlist = (List<Object[]> )request.getAttribute("envisagedDemandlist");


List<List<Object[]>> sunsetmilestones= (List<List<Object[]>>)request.getAttribute("sunsetmilestones");
List<List<ProjectUtilizationBriefingDto>> manpowerDetails = (List<List<ProjectUtilizationBriefingDto>>)request.getAttribute("manpowerDetails"); 
List<List<ProjectUtilizationBriefingDto>> infrastructureDetails = (List<List<ProjectUtilizationBriefingDto>>)request.getAttribute("infrastructureDetails"); 
List<List<ProjectUtilizationBriefingDto>> trainingDetails = (List<List<ProjectUtilizationBriefingDto>>)request.getAttribute("trainingDetails"); 
List<List<ProjectEconomicImpact>> econmicImpactDetails = (List<List<ProjectEconomicImpact>>)request.getAttribute("econmicImpactDetails"); 

%>


<body id="export-container" <%if(text!=null && text.equalsIgnoreCase("p")) {%>style="background-color: rgba(245, 222, 179, 0.2);"<%} %>>

	<style type="text/css">
	
		<%if(wordFlag != null && wordFlag.equalsIgnoreCase("Y")) { %>	 
			/* Microsoft Word-specific CSS for Landscape & Margins */
			@page WordSection1 {
			    /* Width x Height (A4 Landscape: 841.95pt x 595.35pt | Letter: 11in x 8.5in) */
			    size: 841.95pt 595.35pt; 
			    mso-page-orientation: landscape;
			    margin: 0.5in 0.5in 0.5in 0.5in; /* Top, Right, Bottom, Left margins */
			    mso-header-margin: 0.5in;
			    mso-footer-margin: 0.5in;
			}
			
			div.WordSection1 {
			    page: WordSection1;
			}
			
			/* Keep tables from overflowing */
			table {
			    width: 100% !important;
			    table-layout: fixed;
			    word-wrap: break-word;
			    border-collapse: collapse !important;
			}
			
			.break {
			    clear: both;
			    page-break-before: always !important;
			    break-before: page !important;
			    mso-break-type: section-break; /* Forces Word to insert a true section/page break */
			}
			
			/* Force reduced padding and override any inline styles */
			th, td {
			    border: 1px solid black;
			    padding: 1px 2px !important; /* Reduces vertical padding to 1px and horizontal to 2px */
			    text-align: left;
			    overflow-wrap: break-word;
			    word-break: normal;
			}
			
			th {
			    text-align: center;
			}
			
			/* Specifically tighten custom sub-tables and attribute tables */
			.subtables th, .subtables td,
			.projectattributetable th, .projectattributetable td,
			.std, .sth {
			    padding: 1px 2px !important;
			}
		<% } %>
		
		<%if(wordFlag == null) { %>	 
			.break
			{
				page-break-after: always;
				margin: 25px 0px 25px 0px;
			} 
				
			@page {              
			          size: 1120px 790px; 
			          margin-top: 49px;
			          margin-left: 72px;
			          margin-right: 39px;
			          margin-buttom: 49px; 	
			          border: 1px solid black; 
			          padding-top: 15px;
			          
			          @bottom-left {          		
			        
			             content : "The information in this Document is proprietary of <%=labInfo.getLabCode()!=null?(labInfo.getLabCode()): " - " %> /DRDO , MOD Government of India. Unauthorized possession/use is violating the Government procedure which may be liable for prosecution. ";
			             margin-bottom: 30px;
			             margin-right: 5px;
			             font-size: 10px;
			          }
			             
			           @bottom-right {          		
			             content: "Page " counter(page) " of " counter(pages);
			             margin-bottom: 30px;
			             margin-right: 10px;
			          }
			           @top-right {
			             
			             content: "<%= projectattributes.get(0)[12]!=null?(projectattributes.get(0)[12].toString()): " - " %>";
			             margin-top: 30px;
			             margin-right: 50px;
			          }
			          
			          <%-- @top-left {
			          	margin-top: 30px;
			            margin-left: 10px;
			            content: url("data:image/*;base64,<%=lablogo%>");  
			          }   --%>     
			          
			            @top-left {
				          content: "Project: <%=ProjectCode!=null?(ProjectCode): " - "%>"; 
						  margin-top: 30px;
			              margin-left: 50px;
			             
			  			}   
			  			
			  			@top-center {
				         content: "<%=CommitteeCode!=null?(CommitteeCode): " - " %> #<%=Long.parseLong(committeeMetingsCount[1].toString())+1 %>"; 
						
						margin-top: 30px;
			             
			  			} 
			          
			 }
			 
			 th
			 {
			 	border: 1px solid black;
			 	text-align: center;
			 	padding: 5px;
				overflow-wrap: break-word;
			 }
			 
			 td
			 {
			 	border: 1px solid black;
			 	text-align: left;
			 	padding: 5px;
			 	overflow-wrap: break-word;
			 }
			
			th, td
			{
			
				word-break :normal;
			}
			
		 <%} %>
		 
		p{
		  text-align: justify;
		  text-justify: inter-word;
		}
		
		
		#pageborder {
		      position:fixed;
		      left: 0;
		      right: 0;
		      top: 0;
		      bottom: 0;
		      border: 2px solid black;
		}     
		
		 .border
		 {
		 	border: 1px solid black;
		 }
		 .textleft{
		 	text-align: left;
		 }
		 div
		 {
		  	width: 1000px;
		 }
		 
		 .textcenter{
		 	
		 	text-align: center;
		 }
		 
		 .sth
		 {
		 	   
		 	   border: 1px solid black;
		 }
		 
		 .std
		 {
		 	text-align: center;
		 	border: 1px solid black;
		 }
			 
		 #containers {
		    width: 100%;
		    height: 100%;
		    margin: 0;
		    padding: 0;
		}
		
		.anychart-credits {
		   display: none;
		}
		
		.flex-container 
		{
		  display: flex;
		  flex-direction: column;
		  min-height: 100vh;
		}
		
		img
		{
		
		<%if(isprint!=null && isprint.equals("1")){%>
			margin-left: -15px;
		<%}%>
		}
		 
		.pname
		{
			margin: 10px 0px 10px 20px;
		}
		 
		 .completed{
			color: green;
			font-weight: 700;
		}
		
		.briefactive{
			color: blue;
			font-weight: 700;
		}
		
		.inprogress{
			color: #F66B0E;
			font-weight: 700;
		}
		
		.assigned{
			color: brown;
			font-weight: 700;
		}
		
		.assigned{
			color: purple;
			font-weight: 700;
		}
		.notassign{
			color:#AB0072;
			font-weight: 700;
		}
		.ongoing{
			color: #F66B0E;
			font-weight: 700;
		}
		
		.completed{
			color: green;
			font-weight: 700;
		}
		
		.delay{
			color: maroon;
			font-weight: 700;
		}
		
		.completeddelay{
			color:#BABD42;
			font-weight: 700;
		}
		
		.inactive{
			color: red;
			font-weight: 700;
		}
		 
		.delaydays
		{
			color:#000000;
			font-weight: 700;
		}
		
		.firstpage th{
			border:none !important
		}
		 
		.executive{
			align-items: center;
		} 
		
		.sub-title{
			font-size : 20px !important;
			color: #145374 !important
		}
		
		.subtables{
			width: 970px !important;
		}
		
		.date-column{
			max-width:60px !important;
		}
		 
		.status-column{
			max-width:10px !important;
		} 
		
		.resp-column{
			max-width:80px !important;
		} 
		 
		.currency{
			color:#367E18 !important;
			font-style: italic;
		} 
		
		
		.subtables th{
			/* background-color: #001253 !important; 
			color: white !important;
			border-color: white; */
			color: #001253 !important;
			
		}
		 
		.mainsubtitle{
			font-size : 18px !important;
			color:#882042 !important;
		}
		 
		 
		.projectattributetable th{
			text-align: left !important;
		} 
	 
		td.no-border, th.no-border {
	        border: none !important;
	        border-style: none !important;
	        mso-border-alt: none !important;
	    }

		.bg-sunset{
			background-color: #ffbf006b!important;
		}

		.text-left{
			text-align: left!important;
		}
		.text-center{
			text-align: center!important;
		}
		.text-right{
			text-align: right!important;
		}
	</style>
	
	<div class="WordSection1">
	<div class="firstpage" id="firstpage" align="center" "> 
	
		<%if(text!=null && text.equalsIgnoreCase("p")) {%>
		<div align="center" ><h1 style="color: #145374 !important;font-family: 'Muli'!important">Presentation <br> for </h1></div>
		<%}else{ %>
		<div align="center" ><h1 style="color: #145374 !important;font-family: 'Muli'!important">Briefing Paper </h1></div>
		<%} %>
		<!-- <div align="center" ><h2 style="color: #145374 !important">for</h2></div> -->
		
			<div align="center" ><h2 <%if(text!=null && text.equalsIgnoreCase("p")) {%>style="color: #4C9100 !important;"<%}else{ %> style="color: #145374 !important" <%} %>><%=CommitteeCode!=null?(CommitteeCode): " - " %> #<%=Long.parseLong(committeeMetingsCount[1].toString())+1 %> Meeting </h2></div>
		
			<div align="center" ><h2 <%if(text!=null && text.equalsIgnoreCase("p")) {%>style="color: #4C9100 !important;"<%}else{ %> style="color: #145374 !important" <%} %>><%= projectattributes.get(0)[1]!=null?(projectattributes.get(0)[1].toString()): " - " %> (<%= projectattributes.get(0)[0]!=null?(projectattributes.get(0)[0].toString()): " - " %>)
			
			<%if(projectattributes.size()>1) {
						for(int item=1;item<projectattributes.size();item++){
						%>
						 <br>
						<span style="font-size: 1rem;"><%= projectattributes.get(item)[1]!=null?(projectattributes.get(item)[1].toString()): " - " %> (<%= projectattributes.get(item)[0]!=null?(projectattributes.get(item)[0].toString()): " - " %>) (SUB)</span>
						 <%}} %>
			</h2></div>
		
		
			<table class="executive" style="align: center;margin-left: auto;margin-right:auto;  font-size: 16px;"  >
				<tr>			
					<th colspan="8" style="text-align: center; font-weight: 700;">
					<img class="logo" style="width:120px;height: 120px;margin-bottom: 5px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> > 
					</th>
				</tr>
			</table>
		
						<% if(nextMeetVenue!=null){ %>
							<div class="executive" align="center">
							<table style="margin-left: auto;margin-right:auto; " >
								<tr >
									 <th  style="text-align: center; font-size: 20px;padding: 0px; "> <u>Meeting Id </u> </th></tr><tr>
									 <th  style="text-align: center;  font-size: 20px;padding: 0px;  "> <%=nextMeetVenue[1]!=null?(nextMeetVenue[1].toString()): " - " %> </th>				
								 </tr>
							</table>
							
							 <table style="margin-left: auto;margin-right:auto;width:900px; " >
								 <tr>
									 <th  style="text-align: center; width: 50%;font-size: 20px;padding: 0px; "> <u> Meeting Date </u></th>
									 <th  style="text-align: center;  width: 50%;font-size: 20px;padding: 0px; "><u> Meeting Time </u></th>
								 </tr>
								 <tr>
								 	<%-- <%LocalTime starttime = LocalTime.parse(LocalTime.parse(nextMeetVenue[3].toString(),DateTimeFormatter.ofPattern("HH:mm:ss")).format( DateTimeFormatter.ofPattern("HH:mm") ));   %> --%>
								    <td class="no-border" style="text-align: center; width: 50%; font-size: 20px; padding: 0px;">
								        <b><%=sdf.format(sdf1.parse(nextMeetVenue[2].toString()))%></b>
								    </td>
								    <td class="no-border" style="text-align: center; width: 50%; font-size: 20px; padding: 0px;">
								        <b><%=nextMeetVenue[3]!=null?(nextMeetVenue[3].toString()): " - "/* starttime.format( DateTimeFormatter.ofPattern("hh:mm a") ) */ %></b>
								    </td>
								</tr>
							 </table>
							 <table style=" margin-left: auto;margin-right:auto; " >
								<tr >
									 <th  style="text-align: center; font-size: 20px;padding: 0px "> <u>Meeting Venue</u> </th></tr><tr>
									 <th  style="text-align: center;  font-size: 20px;padding: 0px  "> <% if(nextMeetVenue[5]!=null){ %><%=(nextMeetVenue[5].toString()) %> <%}else{ %> - <%} %></th>				
								 </tr>
							</table>
							</div>
						<%}else{ %>
							<br><br><br><br><br><br><br><br><br>
						<%} %>
						
						<br><br>
		<table class="executive" style="align: center;margin-bottom:0px; margin-left: auto;margin-right:auto;  font-size: 16px;margin-top:0px;"  >
		<% if(labInfo!=null){ %>
			<tr>
				<th colspan="8" style="text-align: center; font-weight: 700;font-size: 22px;padding-bottom: 0px;"><%if(labInfo.getLabName()!=null){ %><%=(labInfo.getLabName())  %><%}else{ %>LAB NAME<%} %></th>
			</tr>
		<% } %>
		<tr>
			<th colspan="8" style="text-align: center; font-weight: 700;font-size:15px;padding-bottom: 0px;">Government of India, Ministry of Defence</th>
		</tr>
		<tr>
			<th colspan="8" style="text-align: center; font-weight: 700;font-size:15px;padding-bottom: 0px;">Defence Research & Development Organization</th>
		</tr>
		<tr>
			<th colspan="8" style="text-align: center; font-weight: 700;font-size:15px;padding-bottom: 0px;"><%if(labInfo.getLabAddress() !=null){ %><%=(labInfo.getLabAddress())  %> , <%=labInfo.getLabCity()!=null?(labInfo.getLabCity()): " - " %><%}else{ %>LAB ADDRESS<%} %></th>
		</tr>
		</table>			
		
	</div>
	
	
<%if(wordFlag == null) {%> <h1 class="break"></h1> <%} else {%> <br class="break"> <%} %>
<%char ch='a'; for(int z=0 ; z<projectidlist.size();z++) {   %>
	<%if(z>0){ %><%if(wordFlag == null) {%> <h1 class="break"></h1> <%} else {%> <br class="break"> <%} %> <%} %>
	
	<div  id="detailContainer" align="center" >
	
<!-- ------------------------------------heading commented------------------------------------------------- -->	
	
<!-- ------------------------------------project attributes------------------------------------------------- -->
			<div style="margin-left: 10px;" align="left"><b class="sub-title"> 
			
				<%-- <a class="sub-title" href="<%= HyperlinkPath+ "/ProjectSubmit.htm?ProjectId="+projectid + "&action=edit" %>" target="_top" rel="noopener noreferrer" >1. Project Attributes: </a> --%> 
				<span class="sub-title"  >1<%if(projectidlist.size()>1) {%>(<%=(char)(ch++)%>)<%} %> . Project Attributes: </span>
			</b>
			<b><%=ProjectDetail.get(z)[1]!=null?(ProjectDetail.get(z)[1].toString()): " - "%><% if (z > 0) { %>(SUB)<% } %>  </b>
			</div>
			<%if(projectattributes.get(z)!=null){ %>
			
			<table class="subtables projectattributetable" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px;   border-collapse:collapse;" >
										<tr>
											 <td style="width: 5px !important; padding: 5px; padding-left: 10px">(a)</td>
											 <th  style="width: 150px;padding: 5px; padding-left: 10px"><b>Project Title</b></th>
											 <td colspan="4" style=" width: 370px; padding: 5px; padding-left: 10px"> <%=projectattributes.get(z)[1]!=null?(projectattributes.get(z)[1].toString()): " - " %></td>
										</tr>
										<tr>
											 <td  style="padding: 5px; padding-left: 10px">(b)</td>
											 <th style="width: 150px;padding: 5px; padding-left: 10px"><b>Project Code </b></th>
											 <td colspan="4" style=" width: 370px; padding: 5px; padding-left: 10px"> <%=projectattributes.get(z)[0]!=null?(projectattributes.get(z)[0].toString()): " - "%> </td>
										</tr>
										<tr>
											 <td  style=" padding: 5px; padding-left: 10px">(c)</td>
											 <th  style="width: 150px;padding: 5px; padding-left: 10px"><b>Category</b></th>
											 <td colspan="4" style=" width: 370px; padding: 5px; padding-left: 10px"><%=projectattributes.get(z)[14]!=null?(projectattributes.get(z)[14].toString()): " - "%></td>
										</tr>
										<tr>
											 <td  style="padding: 5px; padding-left: 10px">(d)</td>
											 <th  style="width: 150px;padding: 5px; padding-left: 10px"><b>Date of Sanction</b></th>
											 <td colspan="4" style=" width: 370px; padding: 5px; padding-left: 10px"><%=sdf.format(sdf1.parse(projectattributes.get(z)[3].toString()))%></td>
										</tr>
										<tr>
											 <td  style="width: 20px; padding: 5px; padding-left: 10px">(e)</td>
											 <th  style="width: 150px;padding: 5px; padding-left: 10px"><b>Nodal and Participating Labs</b></th>
											 <td colspan="4" style=" width: 370px; padding: 5px; padding-left: 10px"><%if(projectattributes.get(z)[15]!=null){ %><%=(projectattributes.get(z)[15].toString())%><%} %></td>
										</tr>
										<tr>
											 <td  style=" padding: 5px; padding-left: 10px">(f)</td>
											 <th  style="width: 150px;padding: 5px; padding-left: 10px"><b>Objective</b></th>
											 <td colspan="4" style=" width: 370px; padding: 5px; padding-left: 10px;text-align: justify"> <%=projectattributes.get(z)[4]!=null?(projectattributes.get(z)[4].toString()): " - "%></td>
										</tr>
										<tr>
											 <td  style="padding: 5px; padding-left: 10px">(g)</td>
											 <th  style="width: 150px;padding: 5px; padding-left: 10px"><b>Deliverables</b></th>
											 <td colspan="4" style=" width: 370px; padding: 5px; padding-left: 10px"> <%=projectattributes.get(z)[5]!=null?(projectattributes.get(z)[5].toString()): " - "%></td>
										</tr>
										<tr>
											 <td rowspan="2" style="padding: 5px; padding-left: 10px">(h)</td>
											 <th rowspan="2" style="width: 150px;padding: 5px; padding-left: 10px"><b>PDC</b></th>
											 
											<th colspan="2" style="text-align: center !important"><!-- Original -->&nbsp;</th>					
											<%if( ProjectRevList.get(z).size()>0){ %>	
												<th colspan="2" style="text-align: center !important">Revised</th>																			
											<%}else{ %>													 
										 		<th colspan="2" ></th>	
										 	<%} %>
										</tr>
								 		<tr>
								 			<%if( ProjectRevList.get(z).size()>0 ){ %>								
										 		<td colspan="2" style="text-align: center;"><%= sdf.format(sdf1.parse(ProjectRevList.get(z).get(0)[12].toString()))%> </td>
										 		<td colspan="2" style="text-align: center;">
											 		<%if(LocalDate.parse(projectattributes.get(z)[6].toString()).isEqual(LocalDate.parse(ProjectRevList.get(z).get(0)[12].toString())) ){ %>
											 			-
											 		<%}else{ %>
											 			<%= sdf.format(sdf1.parse(projectattributes.get(z)[6].toString()))%>
											 		<%} %>
										 		
										 		</td>
											<%}else{ %>													 
										 		<td colspan="2" style="text-align: center;"><%= sdf.format(sdf1.parse(projectattributes.get(z)[6].toString()))%></td>
												<td colspan="2" ></td>
										 	<%} %>
										 		    
								 		</tr>
											 	
										<tr>
											<td rowspan="3" style="width: 30px; padding: 5px; padding-left: 10px">(i)</td>
											<th rowspan="3" style="padding-left: 10px"><b>Cost Breakup( &#8377; <span class="currency">Lakhs</span>)</b></th>
											
											<%if( ProjectRevList.get(z).size()>0 ){ %>
													<td style="width: 10% !important" >RE Cost</td>
													<td style="text-align: center;"><%=ProjectRevList.get(z).get(0)[17]!=null?(ProjectRevList.get(z).get(0)[17].toString()): " - " %></td> 
													<td colspan="2" style="text-align: center;"><%=projectattributes.get(z)[8]!=null?(projectattributes.get(z)[8].toString()): " - " %></td>
												</tr>
												
												
												<tr>
													<td style="width: 10% !important">FE Cost</td>		
													<td style="text-align: center;"><%=ProjectRevList.get(z).get(0)[16]!=null?(ProjectRevList.get(z).get(0)[16].toString()): " - " %></td>					
													<td colspan="2" style="text-align: center;"><%=projectattributes.get(z)[9]!=null?(projectattributes.get(z)[9].toString()): " - " %></td>
												</tr>
													
												<tr>	
													<td style="width: 10% !important">Total Cost</td>	
													<td style="text-align: center;"><%=ProjectRevList.get(z).get(0)[11]!=null?(ProjectRevList.get(z).get(0)[11].toString()): " - " %></td>
											 		<td colspan="2" style="text-align: center;"><%=projectattributes.get(z)[7]!=null?(projectattributes.get(z)[7].toString()): " - " %></td>
												</tr> 
														
											<%}else{ %>
													
													<td style="width: 10% !important">RE Cost</td>
													<td ><%=projectattributes.get(z)[8]!=null?(projectattributes.get(z)[8].toString()): " - " %></td>
													<td colspan="2" ></td>
												</tr>
											
												<tr>
													<td style="width: 10% !important">FE Cost</td>		
													<td ><%=projectattributes.get(z)[9]!=null?(projectattributes.get(z)[9].toString()): " - " %></td>					
													<td colspan="2"></td>
												</tr>
												
												<tr>	
													<td style="width: 10% !important" >Total Cost</td>	
													<td ><%=projectattributes.get(z)[7]!=null?(projectattributes.get(z)[7].toString()): " - " %></td>
													<td colspan="2"></td>			
												</tr> 
											<%} %>
												
																			 	
										<tr>
											<td  style="width: 20px; padding: 5px; padding-left: 10px">(j)</td>
											<th style="width: 150px;padding: 5px; padding-left: 10px"><b>No. of Meetings held</b> </th>
								 			<td colspan="4">
												<% if(ebandpmrccount!=null && ebandpmrccount.size()>0){
													List<Object[]> ebandpmrcsub = ebandpmrccount.get(z); 
													for(Object[] ebandpmrc: ebandpmrcsub) { %>
												 	<b><%=ebandpmrc[0]!=null?(ebandpmrc[0].toString()): " - " %> : </b>
													<span><%=ebandpmrc[1]!=null?(ebandpmrc[1].toString()): " - " %></span> &emsp;&emsp;
												<%} }%>
											</td>
										</tr>
										<tr>
											<td  style="width: 20px; padding: 5px; padding-left: 10px">(k)</td>
											<th  style="width: 210px;padding: 5px; padding-left: 10px"><b>Current Stage of Project</b></th>
											<td colspan="4" style=" width: 200px;color:blue; padding: 5px; padding-left: 10px ; <%if(projectdatadetails.get(z)!=null){ %> background-color: <%=projectdatadetails.get(z)[11] !=null?(projectdatadetails.get(z)[11].toString()): " - "%> ;   <%} %>" >
													 <span> <%if(projectdatadetails.get(z)!=null){ %><b><%=(projectdatadetails.get(z)[10].toString()) %> </b>  <%}else{ %>Data Not Found<%} %></span>
											</td> 
										</tr>	
			</table>
		
			<% }else{ %>
				<div align="center"  style="margin: 25px;" > Complete Project Data Not Found </div>
			<%} %>
		</div>
		<% } %>
<!-- ------------------------------------project attributes------------------------------------------------- -->
		<%if(wordFlag == null) {%> <h1 class="break"></h1> <%} else {%> <br class="break"> <%} %>
<!-- ------------------------------------system configuration and Specification------------------------------------------------- -->	
		<% for(int z=0 ; z<1;z++) {   %>
	<%if(z>0){ %><%if(wordFlag == null) {%> <h1 class="break"></h1> <%} else {%> <br class="break"> <%} %> <%} %>
		<div style="margin-left: 10px;" align="left" class="sub-title"><b>2. Schematic Configuration</b></div><br>
		<div align="left" style="margin-top: 5px;margin-left: 10px;"><b class="mainsubtitle">2 (a) System Configuration : </b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<%if(projectdatadetails.get(z)!=null && projectdatadetails.get(z)[3]!=null){ %>
				
				<%
				Path systemPath = Paths.get(filePath,projectLabCode,"ProjectData",projectdatadetails.get(z)[3].toString());
				File systemfile = systemPath.toFile();
				if(systemfile.exists()){ %>
					<%if(!FilenameUtils.getExtension(projectdatadetails.get(z)[3].toString()).equalsIgnoreCase("pdf") ){ %>
						<div align="center">
						<img class="logo" style="max-width:25cm;max-height:17cm;"  src="data:image/*;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(systemfile))%>" alt="confi" >
						</div>
					<% }else{ %>
						<b>  System Configuration Annexure </b>
					<% }%>
				
				<%}else{ %>
					<br>
					File Missing in File System
				<%} %>
			
			
			<%}else{ %>
			<div align="center">
			<br>
				<b> File Not Found</b>
				</div>
			<%} %>
	
	</div>
				<%if(projectdatadetails.get(z)!=null && projectdatadetails.get(z)[3]!=null){ %>
				
				<%
				Path systemPath1 = Paths.get(filePath,projectLabCode,"ProjectData",projectdatadetails.get(z)[3].toString());
				File systemfile1 = systemPath1.toFile();
				if(systemfile1.exists()){ %>
					<%if(!FilenameUtils.getExtension(projectdatadetails.get(z)[3].toString()).equalsIgnoreCase("pdf") ){ %>
							<%if(wordFlag == null) {%> <h1 class="break"></h1> <%} else {%> <br class="break"> <%} %>
					<% }else{ %>
					<% }}}%>
	
	

		
	
		<div align="left" style="margin-left: 15px;margin-top: 5px;"><b class="mainsubtitle">2 (b) System Specification : </b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	
		<%if(projectdatadetails.get(z)!=null && projectdatadetails.get(z)[4]!=null){ %>
				
				<%
				Path specificPath = Paths.get(filePath,projectLabCode,"ProjectData",projectdatadetails.get(z)[4].toString());
				File specificfile = specificPath.toFile();
				if(specificfile.exists()){ %>
					<%if(!FilenameUtils.getExtension(projectdatadetails.get(z)[4].toString()).equalsIgnoreCase("pdf") ){ %>
						<div align="center"><br>
							<img class="logo" style="max-width:25cm;max-height:17cm;"  src="data:image/*;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(specificfile))%>" alt="Speci" >
						</div> 
					<% }else{ %>
						<b> System Specification Annexure </b>
					<% }%>
				
				<%}else{ %>
					<div align="center">
					<br>
					File Missing in File System
					</div>
				<%} %>
			
			
			<%}else{ %>
				<div align="center">
				<br>
				<b> File Not Found</b>
				</div>
			<%} %>
				
		
		
	</div>	
	<!-- <%if(wordFlag == null) {%> <h1 class="break"></h1> <%} else {%> <br class="break"> <%} %> -->
					<%if(projectdatadetails.get(z)!=null && projectdatadetails.get(z)[4]!=null){ %>
				
				<%
				Path specificPath1 = Paths.get(filePath,projectLabCode,"ProjectData",projectdatadetails.get(z)[4].toString());
				File specificfile1 = specificPath1.toFile();
				if(specificfile1.exists()){ %>
					<%if(!FilenameUtils.getExtension(projectdatadetails.get(z)[4].toString()).equalsIgnoreCase("pdf") ){ %> <!-- changed -->
							<%if(wordFlag == null) {%> <h1 class="break"></h1> <%} else {%> <br class="break"> <%} %>
					<% }else{ %>
					<% }}}%>
<!-- --------------------------------------------- ----------------------------------------------- -->
		<div align="left" style="margin-left: 10px;margin-top: 5px;"><b class="mainsubtitle">3. Overall Product tree/WBS:</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

				<%if(projectdatadetails.get(z)!=null && projectdatadetails.get(z)[5]!=null){ %>
				
				<%
				Path productTreePath = Paths.get(filePath,projectLabCode,"ProjectData",projectdatadetails.get(z)[5].toString());
				File productTreeFile = productTreePath.toFile();
				if(productTreeFile.exists()){ %>
					<%if(!FilenameUtils.getExtension(projectdatadetails.get(z)[5].toString()).equalsIgnoreCase("pdf") ){ %>
						<div align="center"><br>
							<img class="logo" style="max-width:25cm;max-height:17cm;margin-bottom: 5px"  src="data:image/*;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(productTreeFile))%>" alt="Speci" >
						</div> 
					<% }else{ %>
						<b> Overall Product tree/WBS Annexure </b>
					<% }%>
				
				<%}else{ %>
					<div align="center">
					<br>
					File Missing in File System
					</div>
				<%} %>
			
			
			<%}else{ %>
			<div align="center">
			<br>
				<b> File Not Found</b>
			</div>
			<%} %>
			
		</div>	
		<%} %>


		<%if(wordFlag == null) {%> <h1 class="break"></h1> <%} else {%> <br class="break"> <%} %> 
<!-- ----------------------------------------------4.Details of work------------------------------------------------- -->			
		<% for(int z=0 ; z<1;z++) {   %>
		<div align="left" style="margin-left: 10px;"><b class="sub-title">4. Particulars of Meeting</b></div><br>
		<div align="left" style="margin-left: 15px;"><b class="mainsubtitle">(a) <%if(CommitteeCode.equalsIgnoreCase("PMRC") || CommitteeCode.equalsIgnoreCase("PMRB")){ %>
															   						Approval 
															   						<%}else { %>
															   						Ratification
															   						<%} %>  of <b>recommendations</b> of last <%=CommitteeCode!=null?(CommitteeCode).toUpperCase(): " - "%> Meeting (if any)</b></div>
		
		
			<table class="subtables" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px; border-collapse:collapse;" >
				<thead>
					<tr>
						<td colspan="6" style="border: 0">
							
						</td>									
					</tr>
										
					<tr>
						<th  style="width: 15px !important;text-align: center;">SN</th>
						<th  style="width: 100px !important;"> ID</th>
						<th  style="width: 415px !important;">Recommendation Point</th>
						<th  style="width: 150px !important;">ADC <br> PDC</th>
						<th  style="width: 250px !important;"> Responsibility</th>
				 		<th  style="width: 80px !important;">Status</th>
						<th  style="width: 250px !important; ">Remarks</th>
					</tr>
				</thead>
				<tbody>
					<%if(lastpmrcminsactlist.get(z).size()==0){ %>
						<tr><td colspan="7" style="text-align: center;" > Nil</td></tr>
					<%}
						else if(lastpmrcminsactlist.get(z).size()>0)
							{int i=1;String key2="";
								for(Object[] obj:lastpmrcminsactlist.get(z)){
									if(obj[3].toString().equalsIgnoreCase("R") && (obj[10]==null || !obj[10].toString().equals("C") || (obj[10].toString().equals("C") && obj[14]!=null && before6months.isBefore(LocalDate.parse(obj[14].toString()) ) ))      ){ %>
						<tr>
							<td  style="text-align: center;"><%=i %></td>
							<td>
							
									<%if(obj[21]!=null && Long.parseLong(obj[21].toString())>0){ %>
								
									
									<span style="font-size: 0.85rem;;">	<!-- <i class="fa fa-info-circle fa-lg " style="color: #145374" aria-hidden="true"></i> -->
								<%for (Map.Entry<Integer, String> entry : committeeWiseMap.entrySet()) {
									Date date = inputFormat.parse(obj[5].toString().split("/")[3]);
									 String formattedDate = outputFormat.format(date);
									 if(entry.getValue().equalsIgnoreCase(formattedDate)){
										 key2=entry.getKey().toString();
									 } }%>
								<%=committee.getCommitteeShortName().trim().toUpperCase()+"-"+key2+"/"+obj[5].toString().split("/")[4] %>

								</span>	
									

								<%}%>
							
							
							
							</td>
							<td  style="text-align: justify; "><%=obj[2]!=null?obj[2].toString(): " - " %></td>
					
							<td style="text-align: center;">
								<%if(obj[8]!= null && !LocalDate.parse(obj[8].toString()).equals(LocalDate.parse(obj[7].toString())) ){ %><span style="color:black;font-weight: bold;"><%=sdf.format(sdf1.parse(obj[8].toString()))%></span><br><%} %>	
								<%if(obj[7]!= null && !LocalDate.parse(obj[7].toString()).equals(LocalDate.parse(obj[6].toString())) ){ %><span style="color:black;font-weight: bold;"><%=sdf.format(sdf1.parse(obj[7].toString()))%></span><br><%} %>
								<%if(obj[6]!= null){ %><span><%=sdf.format(sdf1.parse(obj[6].toString()))%></span><br><%} %>
								</td>
							<td>
								<%if(obj[4]!= null){ %>  
									<%=obj[12]!=null?(obj[12].toString()): " - " %><%-- , <%=obj[13] %> --%>
								<%}else { %> <!-- <span class="notassign">NA</span>  --> <span class="">Not Assigned</span> <%} %> 
							</td>
							<td  class="text-center">
								<%if(obj[4]!= null){ %> 
									<%	String actionstatus = obj[10].toString();
										int progress = obj[18]!=null ? Integer.parseInt(obj[18].toString()) : 0;
										LocalDate pdcorg = LocalDate.parse(obj[6].toString());
										LocalDate lastdate = obj[14]!=null ? LocalDate.parse(obj[14].toString()): null;
										LocalDate today = LocalDate.now();
									%> 
									<% if(lastdate!=null && actionstatus.equalsIgnoreCase("C") ){%>
											<%if(actionstatus.equals("C") && (pdcorg.isAfter(lastdate) || pdcorg.equals(lastdate))){%>
												<span class="completed">CO</span>
											<%}else if(actionstatus.equals("C") && pdcorg.isBefore(lastdate)){ %>	
												<span class="completeddelay">CD (<%= ChronoUnit.DAYS.between(pdcorg, lastdate) %>) </span>
											<%} %>	
										<%}else{ %>
											<%if(actionstatus.equals("F")  && (pdcorg.isAfter(lastdate) || pdcorg.isEqual(lastdate) )){ %>
												<span class="ongoing">RC</span>												
											<%}else if(actionstatus.equals("F")  && pdcorg.isBefore(lastdate)) { %>
												<span class="delay">FD</span>
											<%}else if(actionstatus.equals("A") && progress==0){  %>
												<span class="assigned">
													AA <%if(pdcorg.isBefore(today)){ %> (<%= ChronoUnit.DAYS.between(pdcorg, today)  %>) <%} %>
												</span>
											<%} else if(pdcorg.isAfter(today) || pdcorg.isEqual(today)){  %>
												<span class="ongoing">OG</span>
											<%}else if(pdcorg.isBefore(today)){  %>
												<span class="delay">DO (<%= ChronoUnit.DAYS.between(pdcorg, today)  %>)  </span>
											<%} %>										
									<%} %>
								<%}else { %>
									<span class="notassign">NA</span>
								<%} %>
							</td>
						
				<td ><%if(obj[19]!=null){%><%=(obj[19].toString()) %><%} %></td>
					</tr>		
					<%i++;}
						}%>
					<%if(i==1){ %> <tr><td colspan="6" style="text-align: center;" > Nil</td></tr>	<%} %>
											
					<%} %>
				</tbody>
										
			</table>
			<%} %>
			
		<% for(int z=0 ; z<1;z++) {   %>
		<%if(wordFlag == null) {%> <h1 class="break"></h1> <%} else {%> <br class="break"> <%} %>
				 	<div align="left" style="margin-left: 15px;"><b class="mainsubtitle">(b) Last <%=CommitteeCode!=null?(CommitteeCode).toUpperCase(): " - "%>
														   						Meeting action points with Probable Date of completion (PDC), Actual Date of Completion (ADC) and status.</b>
					</div>
   							
					<table class="subtables" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px;   border-collapse:collapse;" >
						<thead>
							<tr>
								<td colspan="7" style="border: 0">
								</td>									
							</tr>
										
							<tr>
								<th  style="width: 15px !important;text-align: center;  ">SN</th>
								<th style="width: 60px;">ID</th>
								<th  style="width: 400px; ">Action Point</th>
								<th  style="width: 120px; ">ADC<br>PDC</th>
						<!-- 		<th  style="width: 80px; "> ADC</th> -->
								<th  style="width: 210px; "> Responsibility</th>
								<th  style="width: 80px; ">Status</th> 
								<th  style="width: 205px; ">Remarks</th>			
							</tr>
						</thead>
							
						<tbody>		
							<%if(lastpmrcactions.get(z).size()==0){ %>
								<tr><td colspan="7"  style="text-align: center;" > Nil</td></tr>
								<%}
								else if(lastpmrcactions.size()>0)
								{Map<String,List<Object[]>> list = lastpmrcactions.get(z)!=null ? lastpmrcactions.get(z).stream()
										.collect(Collectors.groupingBy(array -> array[0].toString(), LinkedHashMap::new,Collectors.toList())) : new HashMap<>();
							int i = 1;String key="";
							for(Map.Entry<String, List<Object[]>> map : list.entrySet()){
								int j=1;
								List<Object[]> values = map.getValue();
								int rowSpan = values.size();
							for (Object[] obj : values) { %>
								<tr>
									<td  style="text-align: center;"><%=i %></td>
									<td <%if(text!=null && text.equalsIgnoreCase("p")) {%>style="font-weight: bold;"<%} %>>	
								<!--newly added on 13th sept  -->	
								<span style="font-size: 12px;"><%
								
								String committeeCode = committee.getCommitteeShortName().trim().toUpperCase();
								String splCommittee = committeeCode;
								if("PMRC".equalsIgnoreCase(committeeCode)|| "PMRB".equalsIgnoreCase(committeeCode)){
									LocalDate scheduleDate = obj[20] != null ? LocalDate.parse(obj[20].toString()) : null;
									LocalDate PMRB_EFFECTIVE_DATE = LocalDate.of(2026, 4, 1);
									if(scheduleDate.isBefore(PMRB_EFFECTIVE_DATE)){
										splCommittee = "PMRC";
									}else {
										splCommittee = committeeCode;
									}
								}
								
								
								
								if(obj[17]!=null && Long.parseLong(obj[17].toString())>0){ %>
								<%for (Map.Entry<Integer, String> entry : committeeWiseMap.entrySet()) {
									Date date = inputFormat.parse(obj[1].toString().split("/")[3]);
									 String formattedDate = outputFormat.format(date);
									 if(entry.getValue().equalsIgnoreCase(formattedDate)){
										 key=entry.getKey().toString();
									 } }%>
								
 								<%=splCommittee+"-"+key+"/"+obj[1].toString().split("/")[4] %>
 								<%}%> </span>
								</td>
									<%if(j++==1){ %><td rowspan="<%=rowSpan%>" class="text-justify"> <%=obj[2].toString()%> </td> <%} %>
					
																	<td style="text-align: center;">
									<%	String actionstatus = obj[9].toString();
										int progress = obj[15]!=null ? Integer.parseInt(obj[15].toString()) : 0;
										LocalDate pdcorg = LocalDate.parse(obj[3].toString());
										LocalDate lastdate = obj[13]!=null ? LocalDate.parse(obj[13].toString()): null;
										LocalDate today = LocalDate.now();
										LocalDate endPdc=LocalDate.parse(obj[4].toString());
									%> 
					 				<% if(lastdate!=null && actionstatus.equalsIgnoreCase("C") ){%>
											<%if(actionstatus.equals("C") && (pdcorg.isAfter(lastdate) || pdcorg.equals(lastdate))){%>
											<span class="completed"><%= sdf.format(sdf1.parse(obj[13].toString()))%> </span>
											<%}else if(actionstatus.equals("C") && pdcorg.isBefore(lastdate)){ %>	
											<span class="completeddelay"><%= sdf.format(sdf1.parse(obj[13].toString()))%> </span>
											<%} %>	
										<%}else{ %>
												-									
										<%} %>
									<br>
									<span <%if(endPdc.isAfter(today) || endPdc.isEqual(today)) {%>style="color:black;font-weight: bold;" <%} else{%> style="color:maroon ;font-weight:bold;" <%} %>>
									<%= sdf.format(sdf1.parse(obj[4].toString()))%>
									</span>
										<%if(!pdcorg.equals(endPdc)) { %>
									<br>
									<span <%if(pdcorg.isAfter(today) || pdcorg.isEqual(today)) {%>style="color:black;font-weight: bold;" <%} else{%> style="color:maroon ;font-weight:bold;" <%} %>>
									<%= sdf.format(sdf1.parse(obj[3].toString()))%> 
									</span>	
									<%} %>
								</td>
									<td > <%=obj[11]!=null?(obj[11].toString()): " - " %><%-- , <%=obj[12] %> --%> </td>
									<td  class="text-center" > 
										<% if(lastdate!=null && actionstatus.equalsIgnoreCase("C") ){ %>
										<%if(actionstatus.equals("C") && (pdcorg.isAfter(lastdate) || pdcorg.equals(lastdate))){%>
										<span class="completed">CO</span>
										<%}else if(actionstatus.equals("C") && pdcorg.isBefore(lastdate)){ %>	
										<span class="completeddelay">CD (<%= ChronoUnit.DAYS.between(pdcorg, lastdate) %>) </span>
										<%} %>	
										<%}else{ %>
										<%if(actionstatus.equals("F")  && (pdcorg.isAfter(lastdate) || pdcorg.isEqual(lastdate) )){ %>
										<span class="ongoing">RC</span>												
										<%}else if(actionstatus.equals("F")  && pdcorg.isBefore(lastdate)) { %>
										<span class="delay">FD</span>
										<%}else if(actionstatus.equals("A") && progress==0){  %>
										<span class="assigned">AA <%if(pdcorg.isBefore(today)){ %> (<%= ChronoUnit.DAYS.between(pdcorg, today)  %>) <%} %></span>
									    <%} else if(pdcorg.isAfter(today) || pdcorg.isEqual(today)){  %>
										<span class="ongoing">OG</span>
										<%}else if(pdcorg.isBefore(today)){  %>
										<span class="delay">DO (<%= ChronoUnit.DAYS.between(pdcorg, today)  %>)  </span>
										<%} %>					
										<%} %>
										
						
									</td>	
									<td  style="text-align: justify ;"><%if(obj[16]!=null){%><%=(obj[16].toString()) %><%} %></td>			
								</tr>			
							<%i++;
							}}} %>
							</tbody>
									
						</table> 
								
					<%} %>	
					
						<% for(int z=0 ; z<1;z++) {   %>
					<%if(wordFlag == null) {%> <h1 class="break"></h1> <%} else {%> <br class="break"> <%} %>
						<div align="left" style="margin-left: 15px;"><b class="mainsubtitle">(c) Details of Technical/ User Reviews (if any).</b></div>
							<div >
							<%for(Map.Entry<String, List<Object[]>> entry : reviewMeetingListMap.entrySet()) { 
								if(entry.getValue().size()>0) {%>
									<div >
										<table class="subtables" style="align: left; margin-top: 10px; margin-left: 25px; max-width: 300px; border-collapse: collapse; float: left;">
											<thead>
												<tr>
													<th  style="width: 140px; ">Committee</th>
													<th  style="width: 140px; "> Date Held</th>
												</tr>
											</thead>
											<tbody>
												<%int i=0;
												for(Object[] obj : entry.getValue()){ %>
													<tr>
														<td >
															<%=obj[1]!=null?obj[1].toString(): " - "%> #<%=++i %>
														</td>												
														<td style="text-align: center; " ><%= fc.sdfTordf(obj[3].toString())%></td>
													</tr>				
												<%} %>
											</tbody>
										</table>
									</div>
								<%} %>
							<%} %>
							</div>	
						<div>
						<%if(otherMeetingList!=null && otherMeetingList.size()>0) { %>
						<div align="left"><b><%="Other Meetings" %></b></div>
						<div align="left"><table class="subtables" style="align: left; margin-top: 10px; margin-left: 25px; max-width: 350px; border-collapse: collapse;">
						<thead><tr> <th style="width: 140px; ">Committee</th> <th  style="width: 140px; "> Date Held</th></tr></thead>
						<%for(Object[]obj:otherMeetingList) {%>
						<tbody><tr><td><%=obj[3]!=null?(obj[3].toString()): " - "%></td>												
								<td  style="text-align: center; " ><%= sdf.format(sdf1.parse(obj[1].toString()))%></td>
								</tr>
									</tbody><%}%></table></div> <%} %>		</div>
			<%} %>
			
				<% for(int z=0 ; z<1;z++) {   %>
							 <%if(wordFlag == null) {%> <h1 class="break"></h1> <%} else {%> <br class="break"> <%} %>
<!-- -------------------------------------------------------------------------------------------- -->
		<div align="left" style="margin-left: 10px;">
		
			<%-- <a href="<%= HyperlinkPath+ "/MilestoneActivityList.htm?ProjectId="+projectid %>" target="_top" rel="noopener noreferrer" > --%>
				<b class="sub-title">5. Milestones achieved prior to this <%=CommitteeCode!=null?(CommitteeCode).toUpperCase(): " - " %> Meeting period.  </b>
			<!-- </a> -->
   		</div>

						
							<table  class="subtables" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px;  border-collapse:collapse;" >
								<thead>
									<tr>
										<td colspan="10" style="border: 0">
											<!-- <p style="font-size: 10px;text-align: center"> 
												 <span class="notassign">NA</span> : Not Assigned &nbsp;&nbsp;
												 <span class="assigned">AA</span> : Activity Assigned &nbsp;&nbsp; 
												 <span class="ongoing">OG</span> : On Going &nbsp;&nbsp; 
												 <span class="delay">DO</span> : Delay - On Going &nbsp;&nbsp; 
												 <span class="ongoing">RC</span> : Review & Close &nbsp;&nbsp;
												 <span class="delay">FD</span> : Forwarded With Delay &nbsp;&nbsp;
												 <span class="completed">CO</span> : Completed &nbsp;&nbsp; 
												 <span class="completeddelay">CD</span> : Completed with Delay &nbsp;&nbsp; 
												 <span class="inactive">IA</span> : InActive &nbsp;&nbsp;
												 <span class="delaydays">DD</span> : Delayed days &nbsp;&nbsp;
											 </p> -->
										</td>									
									</tr>
								
									<tr>
										<th  style="width: 20px; ">SN</th>
										<th  style="width: 30px; ">MS</th>
										<th  style="width: 60px; ">L</th>
										<th  style="width: 400px; ">System/ Subsystem/ Activities</th>
										<th style="width: 120px; " >Start Date</th>
										<th  style="width: 100px; "> ADC <br> PDC</th>
									<!-- 	<th  style="width: 150px; "> ADC</th> -->
										<th  style="width: 60px; "> Progress</th>
									<!-- 	<th  style="width: 50px; "> Status</th> -->
									 	<th  style="width: 260px; "> Remarks</th>
									</tr>
								</thead>
								<% if( milestones.get(z).size()>0){ 
									long count1=1;
									int milcountA=1;
									int milcountB=1;
									int milcountC=1;
									int milcountD=1;
									int milcountE=1;
									
									%>
									<%int serial=1;for(Object[] obj:milestones.get(z)){
										
										if(Integer.parseInt(obj[21].toString())<= Integer.parseInt(levelid)  
												){
										%>
										<tr>
											<td style="text-align: center"><%=serial%></td>
											<td>M<%=obj[0]!=null?(obj[0].toString()): " - " %></td>
											
											<td style="text-align: center">
												<%
												
												if(obj[21].toString().equals("0")) {%>
													<!-- L -->
												<%	milcountA=1;
													milcountB=1;
													milcountC=1;
													milcountD=1;
													milcountE=1;
												}else if(obj[21].toString().equals("1")) {
													// changed on 13th sept
													for(Map.Entry<Integer,String>entry:treeMapLevOne.entrySet()){
														if(entry.getKey().toString().equalsIgnoreCase(obj[2].toString())){%>
															<%=entry.getValue()!=null?(entry.getValue()): " - " %>
													<%}} 
													
													%>
													<%-- A-<%=milcountA %> --%>
												<% /* milcountA++;
													milcountB=1;
													milcountC=1;
													milcountD=1;
													milcountE=1; */
												}else if(obj[21].toString().equals("2")) {
													for(Map.Entry<Integer,String>entry:treeMapLevTwo.entrySet()){
														if(entry.getKey().toString().equalsIgnoreCase(obj[3].toString())){%>
															<%=entry.getValue()!=null?(entry.getValue()): " - " %>
													<%}}	
													%>
													<%-- B-<%=milcountB %> --%>
												<%/* milcountB+=1;
												milcountC=1;
												milcountD=1;
												milcountE=1; */
												}else if(obj[21].toString().equals("3")) { %>
													C-<%=milcountC %>
												<%milcountC+=1;
												milcountD=1;
												milcountE=1;
												}else if(obj[21].toString().equals("4")) { %>
													D-<%=milcountD %>
												<%
												milcountD+=1;
												milcountE=1;
												}else if(obj[21].toString().equals("5")) { %>
													E-<%=milcountE %>
												<%milcountE++;
												} %>
											</td>

											<td style="<%if(obj[21].toString().equals("0")) {%>font-weight: bold;<%}%>">
												<%if(obj[21].toString().equals("0")) {%>
													<%=obj[10]!=null?(obj[10].toString()): " - " %>
												<%}else if(obj[21].toString().equals("1")) { %>
													&nbsp;&nbsp;<%=obj[11]!=null?(obj[11].toString()): " - " %>
												<%}else if(obj[21].toString().equals("2")) { %>
													&nbsp;&nbsp;<%=obj[12]!=null?(obj[12].toString()): " - " %>
												<%}else if(obj[21].toString().equals("3")) { %>
													&nbsp;&nbsp;<%=obj[13]!=null?(obj[13].toString()): " - " %>
												<%}else if(obj[21].toString().equals("4")) { %>
													&nbsp;&nbsp;<%=obj[14]!=null?(obj[14].toString()): " - " %>
												<%}else if(obj[21].toString().equals("5")) { %>
													&nbsp;&nbsp;<%=obj[15]!=null?(obj[15].toString()): " - " %>
												<%} %>
											</td>
												<td style="text-align: center" ><%= obj[7]!=null? sdf.format(sdf1.parse(obj[7].toString())) : " - " %></td>
											<td style="text-align: center">
											<% 
												LocalDate StartDate = LocalDate.parse(obj[7].toString());
												LocalDate EndDate = LocalDate.parse(obj[8].toString());
												LocalDate OrgEndDate = LocalDate.parse(obj[9].toString());
												int Progess = Integer.parseInt(obj[17].toString());
												LocalDate CompletionDate =obj[24]!=null ? LocalDate.parse(obj[24].toString()) : null;
												LocalDate Today = LocalDate.now();
											%>
											<% if ((obj[19].toString().equalsIgnoreCase("3") || obj[19].toString().equalsIgnoreCase("5")) && obj[24] != null) { %>	
													<span 
														<%if(Progess==0){ %>
															class="assigned"
														<%} else if(Progess>0 && Progess<100 && (OrgEndDate.isAfter(Today) || OrgEndDate.isEqual(Today) )){ %>
															class="ongoing"
														<%} else if( Progess>0 && Progess<100 && (OrgEndDate.isBefore(Today) )){ %>
															class="delay"
														<%} else if((CompletionDate!=null && ( CompletionDate.isBefore(OrgEndDate) ||  CompletionDate.isEqual(OrgEndDate)))){ %>
															class="completed"
														<%} else if((CompletionDate!=null && CompletionDate.isAfter(OrgEndDate) )){ %>
															class="completeddelay"
														<%}else if(CompletionDate!=null && Progess==0 &&  ( EndDate.isAfter(Today) ||  EndDate.isEqual(Today)) ){ %>
															class="inactive"
														<%}else{ %>
															class="assigned"
														<%} %>
														> <%=sdf.format(sdf1.parse(obj[24].toString()))%> </span>
													
												 <% } else {  %> - <% } %>
											
											
											<br>
												<%if(! LocalDate.parse(obj[8].toString()).isEqual(LocalDate.parse(obj[9].toString())) ){ %> 
												<span style="color:black;font-weight: bold;">	<%= sdf.format(sdf1.parse(obj[8].toString()))%></span><br> 
												<%}%>
												<span <%if( LocalDate.parse(obj[8].toString()).isEqual(LocalDate.parse(obj[9].toString()))) {%>style="color:black;font-weight: bold;"<%} %>><%=sdf.format(sdf1.parse(obj[9].toString())) %></span>
											</td>


											<td style="text-align: center"><%=obj[17]!=null?(obj[17].toString()): " - "%>%</td>
						
											<td style="overflow-wrap: break-word !important; word-break: break-all !important; white-space: normal !important;"><%if(obj[23]!=null){%><%=obj[23]%><%} %></td>
										</tr>
									<%count1++;serial++;}} %>
								<%} else{ %>
								<tr><td colspan="9" style="text-align:center; "> Nil</td></tr>
								
								
								<%} %>
							</table>
				<%} %>
				
				<% for(int z=0 ; z<1;z++) {   %>
							 <%if(wordFlag == null) {%> <h1 class="break"></h1> <%} else {%> <br class="break"> <%} %>
<!-- -------------------------------------------------------------------------------------------- -->
		<div align="left" style="margin-left: 10px;">
		
			<%-- <a href="<%= HyperlinkPath+ "/MilestoneActivityList.htm?ProjectId="+projectid %>" target="_top" rel="noopener noreferrer" > --%>
				<b class="sub-title">6. Review of SunSet Milestones.  </b>
			<!-- </a> -->
   		</div>

						
							<table  class="subtables" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px;  border-collapse:collapse;" >
								<thead>
									<tr>
										<td colspan="10" style="border: 0">
											<!-- <p style="font-size: 10px;text-align: center"> 
												 <span class="notassign">NA</span> : Not Assigned &nbsp;&nbsp;
												 <span class="assigned">AA</span> : Activity Assigned &nbsp;&nbsp; 
												 <span class="ongoing">OG</span> : On Going &nbsp;&nbsp; 
												 <span class="delay">DO</span> : Delay - On Going &nbsp;&nbsp; 
												 <span class="ongoing">RC</span> : Review & Close &nbsp;&nbsp;
												 <span class="delay">FD</span> : Forwarded With Delay &nbsp;&nbsp;
												 <span class="completed">CO</span> : Completed &nbsp;&nbsp; 
												 <span class="completeddelay">CD</span> : Completed with Delay &nbsp;&nbsp; 
												 <span class="inactive">IA</span> : InActive &nbsp;&nbsp;
												 <span class="delaydays">DD</span> : Delayed days &nbsp;&nbsp;
											 </p> -->
										</td>									
									</tr>
								
									<tr>
										<th  style="width: 20px; ">SN</th>
										<th  style="width: 30px; ">MS</th>
										<th  style="width: 60px; ">L</th>
										<th  style="width: 400px; ">System/ Subsystem/ Activities</th>
										<th style="width: 120px; " >Start Date</th>
										<th  style="width: 100px; "> ADC <br> PDC</th>
									<!-- 	<th  style="width: 150px; "> ADC</th> -->
										<th  style="width: 60px; "> Progress</th>
									<!-- 	<th  style="width: 50px; "> Status</th> -->
									 	<th  style="width: 260px; "> Remarks</th>
									</tr>
								</thead>
								<% if( sunsetmilestones.get(z).size()>0){ 
									long count1=1;
									int milcountA=1;
									int milcountB=1;
									int milcountC=1;
									int milcountD=1;
									int milcountE=1;
									
									%>
									<%int serial=1;for(Object[] obj:sunsetmilestones.get(z)){
										
										if(Integer.parseInt(obj[21].toString())<= Integer.parseInt(levelid)  
												){
										%>
										<tr class="<% if("0".equals(obj[21].toString()) && "Y".equalsIgnoreCase(obj[31].toString())){ %> bg-sunset <%}%>">
											<td style="text-align: center"><%=serial%></td>
											<td>M<%=obj[0]!=null?(obj[0].toString()): " - " %></td>
											
											<td style="text-align: center">
												<%
												
												if(obj[21].toString().equals("0")) {%>
													<!-- L -->
												<%	milcountA=1;
													milcountB=1;
													milcountC=1;
													milcountD=1;
													milcountE=1;
												}else if(obj[21].toString().equals("1")) {
													// changed on 13th sept
													for(Map.Entry<Integer,String>entry:treeMapLevOne.entrySet()){
														if(entry.getKey().toString().equalsIgnoreCase(obj[2].toString())){%>
															<%=entry.getValue()!=null?(entry.getValue()): " - " %>
													<%}} 
													
													%>
													<%-- A-<%=milcountA %> --%>
												<% /* milcountA++;
													milcountB=1;
													milcountC=1;
													milcountD=1;
													milcountE=1; */
												}else if(obj[21].toString().equals("2")) {
													for(Map.Entry<Integer,String>entry:treeMapLevTwo.entrySet()){
														if(entry.getKey().toString().equalsIgnoreCase(obj[3].toString())){%>
															<%=entry.getValue()!=null?(entry.getValue()): " - " %>
													<%}}	
													%>
													<%-- B-<%=milcountB %> --%>
												<%/* milcountB+=1;
												milcountC=1;
												milcountD=1;
												milcountE=1; */
												}else if(obj[21].toString().equals("3")) { %>
													C-<%=milcountC %>
												<%milcountC+=1;
												milcountD=1;
												milcountE=1;
												}else if(obj[21].toString().equals("4")) { %>
													D-<%=milcountD %>
												<%
												milcountD+=1;
												milcountE=1;
												}else if(obj[21].toString().equals("5")) { %>
													E-<%=milcountE %>
												<%milcountE++;
												} %>
											</td>

											<td style="<%if(obj[21].toString().equals("0")) {%>font-weight: bold;<%}%>">
												<%if(obj[21].toString().equals("0")) {%>
													<%=obj[10]!=null?(obj[10].toString()): " - " %>
												<%}else if(obj[21].toString().equals("1")) { %>
													&nbsp;&nbsp;<%=obj[11]!=null?(obj[11].toString()): " - " %>
												<%}else if(obj[21].toString().equals("2")) { %>
													&nbsp;&nbsp;<%=obj[12]!=null?(obj[12].toString()): " - " %>
												<%}else if(obj[21].toString().equals("3")) { %>
													&nbsp;&nbsp;<%=obj[13]!=null?(obj[13].toString()): " - " %>
												<%}else if(obj[21].toString().equals("4")) { %>
													&nbsp;&nbsp;<%=obj[14]!=null?(obj[14].toString()): " - " %>
												<%}else if(obj[21].toString().equals("5")) { %>
													&nbsp;&nbsp;<%=obj[15]!=null?(obj[15].toString()): " - " %>
												<%} %>
											</td>
												<td style="text-align: center" ><%= obj[7]!=null? sdf.format(sdf1.parse(obj[7].toString())) : " - " %></td>
											<td style="text-align: center">
											<% 
												LocalDate StartDate = LocalDate.parse(obj[7].toString());
												LocalDate EndDate = LocalDate.parse(obj[8].toString());
												LocalDate OrgEndDate = LocalDate.parse(obj[9].toString());
												int Progess = Integer.parseInt(obj[17].toString());
												LocalDate CompletionDate =obj[24]!=null ? LocalDate.parse(obj[24].toString()) : null;
												LocalDate Today = LocalDate.now();
											%>
											<% if ((obj[19].toString().equalsIgnoreCase("3") || obj[19].toString().equalsIgnoreCase("5")) && obj[24] != null) { %>	
													<span 
														<%if(Progess==0){ %>
															class="assigned"
														<%} else if(Progess>0 && Progess<100 && (OrgEndDate.isAfter(Today) || OrgEndDate.isEqual(Today) )){ %>
															class="ongoing"
														<%} else if( Progess>0 && Progess<100 && (OrgEndDate.isBefore(Today) )){ %>
															class="delay"
														<%} else if((CompletionDate!=null && ( CompletionDate.isBefore(OrgEndDate) ||  CompletionDate.isEqual(OrgEndDate)))){ %>
															class="completed"
														<%} else if((CompletionDate!=null && CompletionDate.isAfter(OrgEndDate) )){ %>
															class="completeddelay"
														<%}else if(CompletionDate!=null && Progess==0 &&  ( EndDate.isAfter(Today) ||  EndDate.isEqual(Today)) ){ %>
															class="inactive"
														<%}else{ %>
															class="assigned"
														<%} %>
														> <%=sdf.format(sdf1.parse(obj[24].toString()))%> </span>
													
												 <% } else {  %> - <% } %>
											
											
											<br>
												<%if(! LocalDate.parse(obj[8].toString()).isEqual(LocalDate.parse(obj[9].toString())) ){ %> 
												<span style="color:black;font-weight: bold;">	<%= sdf.format(sdf1.parse(obj[8].toString()))%></span><br> 
												<%}%>
												<span <%if( LocalDate.parse(obj[8].toString()).isEqual(LocalDate.parse(obj[9].toString()))) {%>style="color:black;font-weight: bold;"<%} %>><%=sdf.format(sdf1.parse(obj[9].toString())) %></span>
											</td>


											<td style="text-align: center"><%=obj[17]!=null?(obj[17].toString()): " - "%>%</td>
						
											<td style="overflow-wrap: break-word !important; word-break: break-all !important; white-space: normal !important;"><%if(obj[23]!=null){%><%=obj[23]%><%} %></td>
										</tr>
									<%count1++;serial++;}} %>
								<%} else{ %>
								<tr><td colspan="9" style="text-align:center; "> Nil</td></tr>
								
								
								<%} %>
							</table>
				<%} %>
				
				
				
				
				
	<% for(int z=0 ; z<1;z++) {   %>
	
	
						 <%if(wordFlag == null) {%> <h1 class="break"></h1> <%} else {%> <br class="break"> <%} %>
<!-- ------------------------------------------------------------------------------------------------------------ -->

		<div align="left" style="margin-left: 10px;"><b class="sub-title">7. Details of work and current status of sub system with major milestones ( since last <%= CommitteeCode!=null?(CommitteeCode).toUpperCase():" - " %> meeting ) period </b></div> 
						
			
			<div align="left" style="margin-left: 15px;">
				<%-- <a href="<%= HyperlinkPath+ "/MilestoneActivityList.htm?ProjectId="+projectid %>" target="_top" rel="noopener noreferrer" > --%>
					<b class="mainsubtitle"><br>(a) Work carried out, Achievements, test result etc.</b>
				<!-- </a> -->	
			</div>	
						<!-- Tharun code Start (For Filtering Milestone based on levels) -->					

						<table class="subtables" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px;   border-collapse:collapse;" >
						
							<thead>
								<tr>
									<td colspan="9" style="border: 0">
								
									</td>									
								</tr>
							</thead>
							<tbody>
								<tr>
									<th  style="width: 20px; ">SN</th>
									<th  style="width: 30px; ">MS</th>
									<th  style="width: 30px; ">L</th>
									<th  style="width: 400px; ">System/ Subsystem/ Activities</th>
									<th style="width: 120px;'" >Start Date</th>
									<th  style="width: 100px; "> PDC</th>
									<th  style="width: 60px; "> Progress</th>
<!-- 									<th  style="width: 50px; "> Status</th>
 -->								 	<th  style="width: 320px; "> Remarks</th>

								<% if( MilestoneDetails6.get(z).size()>0){ 
									long count1=1;
									int milcountA=1;
									int milcountB=1;
									int milcountC=1;
									int milcountD=1;
									int milcountE=1;
								%>
									
									<% int serial=1; for(Object[] obj:MilestoneDetails6.get(z)){
										
										if(Integer.parseInt(obj[21].toString())<= Integer.parseInt(levelid) ){
										%>
										<tr>
											<td style="text-align: center"><%=serial%></td>
											<td style="text-align: center">M<%=obj[0]!=null?(obj[0].toString()): " - " %></td>
									
											<td style="text-align: center">
												<%
												
												if(obj[21].toString().equals("0")) {%>
													<!-- L -->
												<%	milcountA=1;
													milcountB=1;
													milcountC=1;
													milcountD=1;
													milcountE=1;
												}else if(obj[21].toString().equals("1")) { 
												//changed on 13th sept
													for(Map.Entry<Integer,String>entry:treeMapLevOne.entrySet()){
														if(entry.getKey().toString().equalsIgnoreCase(obj[2].toString())){%>
															<%=entry.getValue()!=null?(entry.getValue()): " - " %>
													<%}} 
												 
												%>
													<%-- A-<%=milcountA %> --%>
												<% /* milcountA++;
													milcountB=1;
													milcountC=1;
													milcountD=1;
													milcountE=1; */
												}else if(obj[21].toString().equals("2")) { 
													for(Map.Entry<Integer,String>entry:treeMapLevTwo.entrySet()){
														if(entry.getKey().toString().equalsIgnoreCase(obj[3].toString())){%>
															<%=entry.getValue()!=null?(entry.getValue()): " - " %>
													<%}}	
												%>
													<%-- B-<%=milcountB %> --%>
												<%/* milcountB+=1;
												milcountC=1;
												milcountD=1;
												milcountE=1; */
												}else if(obj[21].toString().equals("3")) { %>
													C-<%=milcountC %>
												<%milcountC+=1;
												milcountD=1;
												milcountE=1;
												}else if(obj[21].toString().equals("4")) { %>
													D-<%=milcountD %>
												<%
												milcountD+=1;
												milcountE=1;
												}else if(obj[21].toString().equals("5")) { %>
													E-<%=milcountE %>
												<%milcountE++;
												} %>
											</td>

											<td style="<%if(obj[21].toString().equals("0")) {%>font-weight: bold;<%}%>text-align: justify;">
												<%if(obj[21].toString().equals("0")) {%>
													<%=obj[10]!=null?(obj[10].toString()): " - " %>
												<%}else if(obj[21].toString().equals("1")) { %>
													&nbsp;&nbsp;<%=obj[11]!=null?(obj[11].toString()): " - " %>
												<%}else if(obj[21].toString().equals("2")) { %>
													&nbsp;&nbsp;<%=obj[12]!=null?(obj[12].toString()): " - " %>
												<%}else if(obj[21].toString().equals("3")) { %>
													&nbsp;&nbsp;<%=obj[13]!=null?(obj[13].toString()): " - " %>
												<%}else if(obj[21].toString().equals("4")) { %>
													&nbsp;&nbsp;<%=obj[14]!=null?(obj[14].toString()): " - " %>
												<%}else if(obj[21].toString().equals("5")) { %>
													&nbsp;&nbsp;<%=obj[15]!=null?(obj[15].toString()): " - " %>
												<%} %>
											</td>
											<td style="text-align: center" ><%= obj[7]!=null? sdf.format(sdf1.parse(obj[7].toString())) : " - " %></td>
											<td style="text-align: center;">
												<%if(! LocalDate.parse(obj[8].toString()).isEqual(LocalDate.parse(obj[9].toString())) ){ %> 
												<span style="color:black;font-weight: bold;"><%= sdf.format(sdf1.parse(obj[8].toString()))%></span>	<br> 
												<%}%>
												<span <%if( LocalDate.parse(obj[8].toString()).isEqual(LocalDate.parse(obj[9].toString())) ){ %>style="color:black;font-weight: bold;" <%} %>><%=sdf.format(sdf1.parse(obj[9].toString())) %></span>
											</td>
											<% 
												LocalDate StartDate = LocalDate.parse(obj[7].toString());
												LocalDate EndDate = LocalDate.parse(obj[8].toString());
												LocalDate OrgEndDate = LocalDate.parse(obj[9].toString());
												int Progess = Integer.parseInt(obj[17].toString());
												LocalDate CompletionDate =obj[24]!=null ? LocalDate.parse(obj[24].toString()) : null;
												LocalDate Today = LocalDate.now();
											%>
											<td style="text-align: center"><%=obj[17]!=null?(obj[17].toString()): " - " %>%</td>											
					
											<td style="overflow-wrap: break-word !important; word-break: break-all !important; white-space: normal !important;text-align: justify;"><%if(obj[23]!=null){%><%=(obj[23].toString())%><%} %></td>
									</tr>
									<%count1++; serial++;}} %>
								<% } else{ %>
									<tr><td colspan="9" style="text-align:center; "> Nil</td></tr>
								<%} %>
								</tbody>
								
							</table>
	<%} %>	
	
		<% for(int z=0 ; z<1;z++) {   %>
						<%if(wordFlag == null) {%> <h1 class="break"></h1> <%} else {%> <br class="break"> <%} %>
						 <div align="left" style="margin-left: 15px;"><b class="mainsubtitle">(b) TRL table with TRL at sanction stage and current stage indicating overall PRI.</b></div>
							<div>
						
								<% if(projectdatadetails.get(z)!=null && projectdatadetails.get(z)[6]!=null){ %>
				
									<%
									Path trlPath = Paths.get(filePath,projectLabCode,"ProjectData",projectdatadetails.get(z)[6].toString());
									File trlfile = trlPath.toFile();
									if(trlfile.exists()){ %>
										<%if(!FilenameUtils.getExtension(projectdatadetails.get(z)[6].toString()).equalsIgnoreCase("pdf") ){ %>
											<div align="center"><br>
												<img class="logo" style="max-width:23cm;max-height:15cm;margin-bottom: 5px"  src="data:image/*;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(trlfile))%>" alt="Speci" >
											</div> 
										<% }else{ %>
												<div align="center"><br>
											 	<b> TRL table with TRL at sanction stage Annexure </b>
												</div>
										<% }%>
									<%}else{ %>
										<div align="center">
											<br>
											File Missing in File System
										</div>
									<%} %>
								<%}else{ %>
								<div align="center"><br>
								<b> File Not Found</b>
								</div>
								<%} %>
						</div>
				<% if(projectdatadetails.get(z)!=null && projectdatadetails.get(z)[6]!=null){ %>
				
									<%
									Path trlPath1 = Paths.get(filePath,projectLabCode,"ProjectData",projectdatadetails.get(z)[6].toString());
									File trlfile1 = trlPath1.toFile();
									if(trlfile1.exists()){ %>
										<%if(!FilenameUtils.getExtension(projectdatadetails.get(z)[6].toString()).equalsIgnoreCase("pdf") ){ %>
										<% }else{ %>
											
										<% }}}%>
										
										
							<%if(wordFlag == null) {%> <h1 class="break"></h1> <%} else {%> <br class="break"> <%} %> 
			<div align="left" style="margin-left: 15px;"><b class="mainsubtitle">(c) Risk Matrix/Management Plan/Status. </b></div>
										
				<table class="subtables" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px;  border-collapse:collapse;" >
					<thead>	
							<tr>
								<td colspan="9" style="border: 0">
							
				   				</td>									
							</tr>
							<tr>
								<td colspan="10" style="border:0;text-align: right; "><b>RPN :</b> Risk Priority Number</td>
							</tr>
							<tr>
								<th style="width: 15px;text-align: center " rowspan="2">SN</th>
								<th style="width: 20px;" rowspan="2">ID</th>
								<th style="width: 430px; " colspan="3">Risk</th>
								<th style="width: 240px; " rowspan="1" > ADC<br>PDC</th>
							<!-- 	<th style="width: 100px; " rowspan="1"> ADC</th> -->
								<th style="width: 160px; " rowspan="1"> Responsibility</th>
							<!-- 	<th style="width: 50px; "  rowspan="1">Status</th> -->
								<th style="width: 200px; " colspan="2" rowspan="1">Remarks</th>	
							</tr>
							<tr>
								<th  style="text-align: center;width: 110px; " > Severity<br>(1-10)</th>
								<th  style="text-align: center;width: 110px;"> Probability<br>(1-10)</th>
								<th  style="text-align: center;width: 110px;"> RPN<br>(1-100)</th>
								<th  style="width:360px" colspan="2" > Mitigation Plans</th>
								<th  style="width:315px" colspan="2"> Impact</th>		
							</tr>
										
					</thead>
																		
					<tbody>
						<%if(riskmatirxdata.get(z).size()>0){
							int i=0;%> 
								<%for(Object[] obj : riskmatirxdata.get(z)){
								i++;%>
									<tr>
										<td style="text-align: center" rowspan="2"><%=i %></td>
																		<td style="text-align: center;" rowspan="2">
									<%if(obj[25]!=null && Long.parseLong(obj[25].toString())>0){
										String []tempArray=obj[13].toString().split("/");
										String tempRisk=tempArray[tempArray.length-1];
										%>
										<span>
										<%=tempRisk!=null?(tempRisk): " - " %>
										</span>
									<%}%>
								</td>
										
										
										<td style="text-align: justify;color: red; " colspan="3" >
											<%=obj[0] %> <span style="color: #3D60FF;font-weight: bold;"> - <%=obj[23]!=null?(obj[23].toString()): " - " %><%=obj[24]!=null?(obj[24].toString()): " - "%></span>
										</td>
										<td style="text-align: center" rowspan="1">
										<%	String actionstatus = obj[15].toString();
																	LocalDate pdcorg = LocalDate.parse(obj[9].toString());
																	LocalDate enddate = LocalDate.parse(obj[17].toString());
																	LocalDate lastdate = obj[20]!=null ? LocalDate.parse(obj[20].toString()): null;
																	LocalDate today = LocalDate.now();
																	int progress = obj[18]!=null ? Integer.parseInt(obj[18].toString()) : 0;
																%> 
																	<% if(lastdate!=null && actionstatus.equalsIgnoreCase("C") ){%>
																		<%if(actionstatus.equals("C") && (pdcorg.isAfter(lastdate) || pdcorg.equals(lastdate))){%>
																		<span class="completed"><%= sdf.format(sdf1.parse(obj[20].toString()))%> </span>
																		<%}else if(actionstatus.equals("C") && pdcorg.isBefore(lastdate)){ %>	
																		<span class="completeddelay"><%= sdf.format(sdf1.parse(obj[20].toString()))%> </span>
																		<%} %>	
																	<%}else{ %>
																		-									
																	<%} %>
																	<br>
<%-- 											<% if (obj[11] != null && !LocalDate.parse(obj[11].toString()).equals(LocalDate.parse(obj[10].toString())) ) { %><span style="font-size:0.9rem;"><%=sdf.format(sdf1.parse(obj[11].toString()))%></span><br> <% } %>
											<% if (obj[10] != null && !LocalDate.parse(obj[10].toString()).equals(LocalDate.parse(obj[9].toString())) ) { %><span style="font-size:0.9rem;"><%=sdf.format(sdf1.parse(obj[10].toString()))%></span><br><% } %> --%>
											<%if(!pdcorg.equals(enddate)) {%>
											<span style="font-size:0.9rem"><b><%=sdf.format(sdf1.parse(obj[17].toString()))%></b></span>
											<br>
											<%} %>
											<span style="font-size:0.9rem"><b><%=sdf.format(sdf1.parse(obj[9].toString()))%></b></span>
										</td>
										
									<!-- 	<td style="text-align: center" rowspan="1">
										
										</td> -->
													
										<td rowspan="1"  ><%=obj[7]!=null?(obj[7].toString()): " - " %><%-- ,&nbsp;<%=obj[8] %> --%></td>	
										<td style="text-align: justify" colspan="2" rowspan="1"><%if(obj[19]!=null){ %> <%=(obj[19].toString())%><%} %></td>
											
									</tr>	
									
									<tr>
										<td style="text-align: center;" ><%=obj[1]!=null?(obj[1].toString()): " - " %></td>
										<td style="text-align: center;" ><%=obj[2]!=null?(obj[2].toString()): " - " %></td>
										<td style="text-align: center;" >
											<%=obj[22]!=null?(obj[22].toString()): " - " %>
											<% int RPN =Integer.parseInt(obj[22].toString());
											if(RPN>=1 && RPN<=25){ %>(Low)
											<%}else if(RPN>=26 && RPN<=50){ %>(Medium)
											<%}else if(RPN>=51 && RPN<=75){ %>(High)
											<%}else if(RPN>=76){ %>(Very High)
											<%} %>
											
										</td>
										<td style="text-align: justify;" colspan="2" ><%=obj[3]!=null?(obj[3].toString()): " - " %></td>
										<td style="text-align: justify;" colspan="2" ><%=obj[21]!=null?(obj[21].toString()): " - " %></td>
									</tr>
												
									<%if(riskmatirxdata.get(z).size() > i){ %>
										<tr>
											<td colspan="9" style="color:transparent ;">.</td>
										</tr>
									<%} %>	
									<%}%>
								<%}else{%>
									<tr><td colspan="9"  style="text-align: center;">Nil </td></tr>
								<%} %>
							</tbody>		
						</table>
				
				
					<table class="subtables" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px;   border-collapse:collapse;width:600px !important;"  >
						<thead>
							<tr>
								<th style="width:10%">Risk Type</th>
								<th style="width:40%">Description</th>
								<th style="width:10%">Risk Type</th>
								<th style="width:40%">Description</th>
							</tr>
						</thead>
						<tbody>
							<% int riskcount=0;
							if(RiskTypes!=null && !RiskTypes.isEmpty()){
							for(Object[] risktype : RiskTypes ){ %>
							<tr>
								<td style="text-align: center;"><b>I<%=risktype[2]!=null?(risktype[2].toString()): " - " %></b></td>
								<td>Internal <%=risktype[1]!=null?(risktype[1].toString()): " - " %></td>
								<td style="text-align: center;"><b>E<%=risktype[2]!=null?(risktype[2].toString()): " - " %></b></td>
								<td>External <%=risktype[1]!=null?(risktype[1].toString()): " - " %></td>
								</tr>
							</tr>
							<%} }%>
						</tbody>
					</table>							
		<%} %>
		
		<%int chapter=1;int chapter2=1;
		for(int z=0 ; z<projectidlist.size();z++) {   %>
		<!-- ----------------------------------------------7a. Procurement Status------------------------------------------------- -->
						<%if(wordFlag == null) {%> <h1 class="break"></h1> <%} else {%> <br class="break"> <%} %>
						<div align="left" style="margin-left: 10px;"><b class="sub-title">8. Details of Procurement</b></div>
							<div align="left" style="margin-left: 15px;margin-top: 5px;"><b class="mainsubtitle">(a<%if(projectidlist.size()>1) {%><%="."+chapter++%><%} %>)Details of Procurement plan (Major Items) 			 </b>  <%if(projectidlist.size()>1) {%> (<b><%=ProjectDetail.get(z)[1]%><% if (z > 0) { %>(SUB)<% } %>  <%} %></b>)</div>
							<div align="right"> <span class="currency" style="font-weight: bold;width: 970px !important;" >(In &#8377; Lakhs)</span></div>
							
							<table class="subtables" style=" margin-left: 8px;margin-top:5px;font-size: 16px; border-collapse: collapse;border: 1px solid black" >
										<thead>
										<tr>
											<th colspan="11" style="text-align: right;"> <span class="currency" >(In &#8377; Lakhs)</span></th>
										</tr>
										 <tr>
										 	<th colspan="11" class="std">Demand Details ( > &#8377; <% if (projectdatadetails.get(0) != null && projectdatadetails.get(0)[13] != null) { %>
													<%=(projectdatadetails.get(0)[13].toString()).replaceAll("\\.\\d+$", "")%> ) <% } else { %> - )<% } %>
												
											</th>
										</tr>
										</thead>
										
										<tr>
											<th class="std" style="border: 1px solid black;width: 30px !important;">SN</th>
											<th class="std" style="border: 1px solid black;max-width:90px;">Demand No<br>Demand Date</th>
<!-- 											<th class="std" style="border: 1px solid black;max-width:90px; ">Demand Date</th>
 -->											<th class="std" colspan="4" style="border: 1px solid black;max-width: 150px;"> Nomenclature</th>
											<th class="std" style="border: 1px solid black;max-width:90px;"> Est. Cost</th>
											<th class="std" style="border: 1px solid black;max-width:80px; "> Status</th>
											<th class="std" colspan="3" style="border: 1px solid black;max-width:200px;">Remarks</th>
										</tr>
										    <% int k=0;
										    if(procurementOnDemand.get(z)!=null &&  procurementOnDemand.get(z).size()>0){
										    Double estcost=0.0;
										    Double socost=0.0;
										    for(Object[] obj : procurementOnDemand.get(z)){ 
										    	k++; %>
											<tr style="font-size: 14px;">
												<td class="std"  style=" border: 1px solid black;"><%=k%></td>
												<td class="std"  style=" border: 1px solid black;"><%=obj[1]!=null?(obj[1].toString()): " - "%><br><%=sdf.format(sdf1.parse(obj[3].toString()))%></td>
<%-- 												<td class="std"  style=" border: 1px solid black;"><%=sdf.format(sdf1.parse(obj[3].toString()))%></td>
 --%>												<td class="std" colspan="4" ><%=obj[8]!=null?(obj[8].toString()): " - "%></td>
												<td class="std" style=" text-align:right;"> <%=format.format(new BigDecimal(obj[5].toString())).substring(1)%></td>
												<td class="std"  style=" border: 1px solid black;"> <%=obj[10]!=null?(obj[10].toString()): " - "%> </td>
												<td class="std" colspan="3" style=" border: 1px solid black;"><%=obj[11]!=null?(obj[11].toString()): " - "%> </td>		
											</tr>		
											<%
											estcost += Double.parseDouble(obj[5].toString());
										    }%>
										    
										    <tr>
										    	<td class="std" colspan="8" style="text-align: right;"><b>Total</b></td>
										    	<td class="std" style="text-align: right;"><b><%=df.format(estcost)%></b></td>
										    	
										    	<td class="std" colspan="2" style="text-align: right;"></td>

										    </tr>
										    
										    
										    <% }else{%>											
												<tr><td colspan="11" style="border: 1px solid black;text-align: center;" class="std" >Nil </td></tr>
											<%} %>
											<!-- ********************************Future Demand Start *********************************** -->
											<tr>
											<th class="std" colspan="11" style="border: 1px solid black"><span class="mainsubtitle">Future Demand</span></th>
											</tr>
											<tr>
												 <th class="std" style="border: 1px solid black;width: 15px !important;text-align: center;">SN</th>
													 <th class="std"  colspan="4" style="border: 1px solid black;;width: 295px;"> Nomenclature</th>
													 <th class="std" style="border: 1px solid black;width: 80px;"> Est. Cost-Lakh &#8377;</th>
													 <th class="std" style="border: 1px solid black;max-width:50px; "> Status</th>
													 <th class="std" colspan="4" style="border: 1px solid black;max-width: 310px;">Remarks</th>
											</tr>
										
										    			    <% int a=0;
										    if(envisagedDemandlist!=null &&  envisagedDemandlist.size()>0){
										    Double estcost=0.0;
										    Double socost=0.0;
										    for(Object[] obj : envisagedDemandlist){ 
										    	a++; %>
											<tr style="font-size: 14px;">
												<td class="std"  style=" border: 1px solid black;"><%=a%></td>
												<td class="std" colspan="4" style="border: 1px solid black;" ><%=obj[3]!=null?(obj[3].toString()): " - "%></td>
												<td class="std" style="border: 1px solid black; text-align:right;"> <%=format.format(new BigDecimal(obj[2].toString())).substring(1)%></td>
												<td class="std"  style=" border: 1px solid black;"> <%=obj[6]!=null?(obj[6].toString()): " - "%> </td>
												<td class="std" colspan="4" style="border: 1px solid black;"><%=obj[4]!=null?(obj[4].toString()): " - "%> </td>		
											</tr>		
											<%
												estcost += Double.parseDouble(obj[2].toString());
										    }%>
										    
										    <tr>
										    	<td  class="std"colspan="7" style="border: 1px solid black;text-align: right;"><b>Total</b></td>
										    	<td class="std" style="border: 1px solid black;text-align: right;" colspan="4"><b><%=df.format(estcost)%></b></td>
										    </tr>
										    
										    
										    <% }else{%>											
												<tr><td colspan="11" style="border: 1px solid black;text-align: center;" class="std" >Nil </td></tr>
											<%} %>
											
									<!-- ********************************Future Demand End *********************************** -->
											
											 <tr >
											 
												<th  class="std"  colspan="11">Orders Placed ( > &#8377; <% if (projectdatadetails.get(0) != null && projectdatadetails.get(0)[13] != null) { %>
													<%=(projectdatadetails.get(0)[13].toString()).replaceAll("\\.\\d+$", "")%> ) <% } else { %> - )<% } %>
												</th>
											 </tr>
										
										  	 <tr>	
										  	 	 <th class="std" rowspan="1" style="border: 1px solid black;width: 30px !important;">SN</th>
										  	 	 <th class="std" style="border: 1px solid black;width:150px;">Demand No <br>Demand  Date</th>
										  	 	<!--  <th class="std" style="border: 1px solid black;" >Demand  Date</th> -->
												 <th class="std" colspan="2" style="border: 1px solid black;"> Nomenclature</th>
												 <th class="std"  style=" border: 1px solid black;width: 150px;">Supply Order No <br> SO Date </th>
												 <th class="std"  colspan="1" style="border: 1px solid black;width:100px">SO Cost-Lakh &#8377;</th>
												<!--  <th class="std" style="border: 1px solid black;max-width:90px;	">DP Date</th> -->
													 <th class="std" style="border: 1px solid black;width:100px;">DP Date<br> Rev DP</th>
												 <th class="std" colspan="2" style="border: 1px solid black;width: 200px;">Vendor Name</th>
												 <th class="std" style="border: 1px solid black;max-width:80px; "> Status</th>
												<th class="std" style="border: 1px solid black;width:200px; "> Remarks</th>
												</tr>
											
											
											<%if(procurementOnSanction.get(z)!=null && procurementOnSanction.get(z).size()>0){ 
												  int rowk=0;
										    	  Double estcost=0.0;
												  Double socost=0.0;
												  String demand="";
												  List<Object[]> list = new ArrayList<>();
												  for(Object[] obj:procurementOnSanction.get(z)){ 
													if(obj[2]!=null){
														if(!obj[1].toString().equalsIgnoreCase(demand)){
															rowk++;
											  	 		 	 list = procurementOnSanction.get(z).stream().filter(e-> e[0].toString().equalsIgnoreCase(obj[0].toString())).collect(Collectors.toList());
														}
													}
													  
											%>
					<tr style="font-size: 14px;">
					<td <%if(!obj[1].toString().equalsIgnoreCase(demand)){ %>  style="border: 1px solid black;border-bottom:none;"<%} else{ %> style="border: 1px solid black;border-bottom:none;border-top:none;"<%} %>>
					<%if(!obj[1].toString().equalsIgnoreCase(demand)){ %>
					<%=rowk %>
					<%} %>
					</td>
					<td <%if(!obj[1].toString().equalsIgnoreCase(demand)){ %> style="border: 1px solid black;border-bottom:none;"<%} else{ %> style="border: 1px solid black;border-bottom:none;border-top:none;"<%} %>>
					<%if(!obj[1].toString().equalsIgnoreCase(demand)){ %><%if(obj[1]!=null) {%> <%=(obj[1].toString())%><% }else{ %>-<%} %><br>
					<%=sdf.format(sdf1.parse(obj[3].toString()))%>
					<%} %>
					</td>
					<td colspan="2" <%if(!obj[1].toString().equalsIgnoreCase(demand)){ %> style="border: 1px solid black;border-bottom:none;"<%} else{ %> style="border: 1px solid black;border-bottom:none;border-top:none;"<%} %>>
					<%if(!obj[1].toString().equalsIgnoreCase(demand)){ %>
					<%=obj[8]!=null?(obj[8].toString()): " - "%>
					<%} %>
					</td>
				<td style="border: 1px solid black;text-align: center;">
				<% if(obj[2]!=null){%> <%=(obj[2].toString())%> <%}else{ %>-<%} %><br>
				<%if(obj[16]!=null){%> <%=sdf.format(sdf1.parse(obj[16].toString()))%> <%}else{ %> - <%} %>
				</td>
				<td style="border: 1px solid black;text-align: right"><%if(obj[6]!=null){%> <%=format.format(new BigDecimal(obj[6].toString())).substring(1)%> <%} else{ %> - <%} %></td>
				<td style="border: 1px solid black;">
				<%if(obj[4]!=null){%> <%=sdf.format(sdf1.parse(obj[4].toString()))%> <%}else{ %> - <%} %><br>
				<span style="text-align: center"><%if(obj[7]!=null){if(!obj[7].toString().equals("null")){%> <%=sdf.format(sdf1.parse(obj[7].toString()))%><%}}else{ %>-<%} %></span>	</td>
					<td colspan="2" style="border: 1px solid black;"><%=obj[12]!=null?(obj[12].toString()): " - " %> </td>
					<td <%if(!obj[1].toString().equalsIgnoreCase(demand)){ %> style="border: 1px solid black;border-bottom:none;"<%} else{ %> style="border: 1px solid black;border-bottom:none;border-top:none;"<%} %>>
						<%if(!obj[1].toString().equalsIgnoreCase(demand)){ %>
					<%=obj[10]!=null?(obj[10].toString()): " - "%>
					<%} %>
					
					</td>					
					<td <%if(!obj[1].toString().equalsIgnoreCase(demand)){ %> style="border: 1px solid black;border-bottom:none;"<%} else{ %> style="border: 1px solid black;border-bottom:none;border-top:none;"<%} %>>
						<%if(!obj[1].toString().equalsIgnoreCase(demand)){ %>
					<%=obj[11]!=null?(obj[11].toString()): " - "%>
					<%} %>
					
					</td>
					
					
					</tr>
											<%
											demand = obj[1].toString();
											Double value = 0.00;
								  	 		if(obj[6]!=null){
								  	 			value=Double.parseDouble(obj[6].toString());
								  	 		}
								  	 		
								  	 		estcost += Double.parseDouble(obj[5].toString());
								  	 		socost +=  value;
											}
											%>
											
												<tr>
										    	<td colspan="5" class="std" style="text-align: right;border: 1px solid black;"><b>Total</b></td>
										    	<td colspan="1" class="std" style="text-align: right;border: 1px solid black;"><b><%=df.format(socost)%></b></td>
										    	<td colspan="5" class="std" style="text-align: right;border: 1px solid black;"></td>
										   		 </tr>	
										 <% }else{%>
											
												<tr><td colspan="8" style="border: 1px solid black;" class="std"  style="text-align: center;">Nil </td></tr>
											<%} %>
									</table> 
							
									 
									 
									<div align="right"> <span class="currency" style="font-weight: bold;width: 970px !important;" >(In &#8377; Lakhs)</span></div>
									<table class="subtables" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px;  border-collapse:collapse;" >
										 <thead>
										 	
											 <tr >
												 <th colspan="5" ><b class="mainsubtitle">Total Summary of Procurement</b></th>
											 </tr>
										 </thead>
										 
										 <tbody>
										<tr >
												 <th>No. of Demand</th>
												 <th>Est. Cost</th>
										  	 	 <th>No. of Orders</th>
										  	 	 <th>SO Cost</th>
										  	 	 <th>Expenditure</th>
										</tr>
										 
										 <%if(totalprocurementdetails!=null && totalprocurementdetails.size()>0){ 
										 for(TotalDemand obj:totalprocurementdetails){
											 if(obj.getProjectId().equalsIgnoreCase(projectid)){
										 %>
										   <tr>
										      <td style="text-align: center;"><%=obj.getDemandCount()!=null?(obj.getDemandCount()): " - " %></td>
										      <td style="text-align: center;"><%=obj.getEstimatedCost()!=null?(obj.getEstimatedCost()): " - " %></td>
										      <td style="text-align: center;"><%=obj.getSupplyOrderCount()!=null?(obj.getSupplyOrderCount()): " - "%></td>
										      <td style="text-align: center;"><%=obj.getTotalOrderCost()!=null?(obj.getTotalOrderCost()): " - " %></td>
										      <td style="text-align: center;"><%=obj.getTotalExpenditure()!=null?(obj.getTotalExpenditure()): " - " %></td>
										   </tr>
										   <%}}}else{%>
										   <tr>
										      <td class="std" colspan="5" style="text-align: center;">IBAS Server Could Not Be Connected</td>
										   </tr>
										   <%} %>
										   </tbody>
									  </table>
								
<!-- ----------------------------------------------7b. Procurement Status------------------------------------------------- -->								
					  <%if(wordFlag == null) {%> <h1 class="break"></h1> <%} else {%> <br class="break"> <%} %>
								
								<div align="left" style="margin-left: 15px;"><b class="mainsubtitle">(b<%if(projectidlist.size()>1) {%><%="."+chapter2++%><%} %>) Procurement Status</b> 			<%if(projectidlist.size()>1) {%> (<b><%=ProjectDetail.get(z)[1]!=null?(ProjectDetail.get(z)[1].toString()): " - "%><% if (z > 0) { %>(SUB)<% } %>  <%} %></b>) </b></div>
								<div align="right" style="width:980px !important;"> <span class="currency" style="font-weight: bold;" >(In &#8377; Lakhs)</span></div>
								
								<table class="subtables" style="align: left; margin-top: 10px; margin-bottom: 0px; margin-left: 25px;width:980px !important;  border-collapse:collapse;" >
									<thead>
										<tr>
											<th colspan="29" ><span class="mainsubtitle">Procurement Status</span></th>
									 	</tr>
									</thead>
								</table>	 
							 <table class="subtables" style="align: left; margin-top: 00px; margin-bottom: 10px; margin-left: 25px;width:980px !important;  border-collapse:collapse;font-size: 12px;" >
									<thead>
									 	<tr>
											<th style="width: 30px;">SN</th>
											<th style="width: 280px;">Item Name</th>
											<th style="width: 130px;">Est/SO Cost<br><span class="currency" style="font-weight: bold;" >(In &#8377; Lakhs)</span></th>
											<th style="width: 20px;">0</th>
											<th style="width: 20px;">1</th>
											<th style="width: 20px;">2</th>
											<th style="width: 20px;">3</th>
											<th style="width: 20px;">4</th>
											<th style="width: 20px;">5</th>
											<th style="width: 20px;">6</th>
											<th style="width: 20px;">7</th>
											<th style="width: 20px;">8</th>
											<th style="width: 20px;">9</th>
											<th style="width: 20px;">10</th>
											<th style="width: 20px;">11</th>
											<th style="width: 20px;">12</th>
											<th style="width: 20px;">13</th>
											<th style="width: 20px;">14</th>
											<th style="width: 20px;">15</th>
											<th style="width: 20px;">16</th>
											<th style="width: 20px;">17</th>
											<th style="width: 20px;">18</th>
											<th style="width: 20px;">19</th>
											<th style="width: 20px;">20</th>
											<th style="width: 20px;">21</th>
											<th style="width: 20px;">22</th>
											<th style="width: 20px;">23</th>
											<th style="width: 20px;">24</th>
											<th style="width: 20px;">25</th>
									 	</tr>
									</thead>
									<tbody>
										<%	List<Object[]> procuremntsList = new ArrayList<>();
											
											if( procurementOnDemand.get(z)!=null ){  procuremntsList.addAll(procurementOnDemand.get(z)); }
											if( procurementOnSanction.get(z)!=null ){  procuremntsList.addAll(procurementOnSanction.get(z)); }
										%>
										<%int psn=0; for(Object[] proc : procuremntsList){psn++; %>
											<tr>
												<td style="text-align:center; "><%=psn %></td>
												<td>
													<%if(proc[8]!=null && proc[8].toString().length()>50){ %>
													<%=proc[8].toString().substring(0,50) %> ...
													<%}else{ %>
													<%=proc[8]!=null?(proc[8].toString()): " - "%>
													<%} %>
												</td>
												<td style="text-align: right;">
													<%if(proc[9].toString().equalsIgnoreCase("S")){ %>
														<%=proc[6]!=null?(proc[6].toString()): " - " %>
													<%}else{ %>
														<%=proc[5]!=null?(proc[5].toString()): " - " %>
													<%} %>
												</td>
												<td style="background-color: green;"></td>
												<% int filestatus = Integer.parseInt(proc[13].toString());
													int tempstatus = filestatus;
												%>
												<%for(int tdc=1;tdc<=25;tdc++){ %>
												
												<%if(filestatus>11){  filestatus--;  } %>
												<%if(filestatus>25){  filestatus--;  } %>
												
												
												<%if(tdc < (tempstatus)){ %>
													<td style="background-color: green;"></td>
												<%}else if(tdc == (tempstatus)){ %>
													<td style="background-color: green;text-align: center;color: white ">*</td>
												<%}else if(tdc >(tempstatus)){ %>
													<td style=""></td>
												<%} %>
												
											<%} %>
											</tr>
											<%}if(envisagedDemandlist!=null && envisagedDemandlist.size()>0){
										for(Object[] envi : envisagedDemandlist){psn++; %>
										<tr>
												<td style="text-align:center; "><%=psn %></td>
												<td><%=envi[3]!=null?(envi[3].toString()): " - " %></td>
												<td style="text-align: right;"><%=envi[2]!=null?(envi[2].toString()): " - " %></td>
												<td style="background-color: #F96E16;text-align: center; ">*</td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												</tr>
										<%}} %>
										
										<%if(psn ==0 && envisagedDemandlist!=null && envisagedDemandlist.size()==0  ){ %>
											<tr>
										      <td colspan="29" style="text-align: center;">Nil</td>
										   </tr>
										<%} %>
										
								 	</tbody>
								</table>
								
						<table class="subtables" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px;width:980px !important;  border-collapse:collapse;font-size: 12px;" >
									<tr>
										<td>0</td>
										<td>Demand to be Initiated</td>
										<td>7</td>
										<td>TCEC Approved</td>
										<td>14</td>
										<td>CDR</td>
										<td>21</td>
										<td>Inward Inspection Clearance</td>
									</tr>
									<tr>
							            <td>1</td>
										<td>Demand Initiated</td>
										<td>8</td>
										<td>TPC Approved</td>
										<td>15</td>
										<td>Acceptance of Critical BoM by Dev Partner</td>
										<td>22</td>
										<td>Payment Process</td>
									</tr>
									<tr>
										<td>2</td>
										<td>SPC Cleared</td>
									    <td>9</td>
										<td>Financial Sanction</td>
										<td>16</td>
										<td>Realization Completed</td>
										<td>23</td>
										<td>Partially Paid</td>
									</tr>
									<tr>
										<td>3</td>
										<td>Demand Approved</td>
									    <td>10</td>
										<td>Order Placement</td>
										<td>17</td>
										<td>FAT Completed</td>
										<td>24</td>
										<td>Payment Released</td>
									</tr>
									<tr>
										<td>4</td>
										<td>Tender Enquiry Floated</td>
										<td>11</td>
										<td>PDR</td>
										<td>18</td>
										<td>ATP/QTP Completed</td>
										<td>25</td>
										<td>Available for Integration</td>
									</tr>
									<tr>
										<td>5</td>
										<td>Receipt of Quotations</td>
										<td>12</td>
										<td>SO for Critical BoM by Dev Partner</td>
										<td>19</td>
										<td>Delivery at Stores</td>
										<td></td>
										<td></td>
									</tr>
									<tr>
										<td>6</td>
										<td>Tender Opening</td>
										<td>13</td>
										<td>DDR</td>
										<td>20	</td>
										<td>SAT / SoFT</td>
										<td></td>
										<td></td>
									</tr>
								</table>						  
               
									  
               
					
		<%} %>	
			<% char fch='a'; for(int z=0 ; z<projectidlist.size();z++) {   %>
					<!-- ----------------------------------------------9. Overall financial Status------------------------------------------------- -->
					<%if(wordFlag == null) {%> <h1 class="break"></h1> <%} else {%> <br class="break"> <%} %>	
		 
   					<div align="left" style="margin-left: 10px;"><b class="sub-title">9. <%if(projectidlist.size()>1) {%> (<%=(fch++) %>) <%} %>. Overall Financial Status </b> 			<b><%=ProjectDetail.get(z)[1]!=null?(ProjectDetail.get(z)[1].toString()): " - "%><% if (z > 0) { %>(SUB)<% } %>  </b></div><div align="right"><b><span class="currency" >(&#8377; <span>Crore</span>)</span></b></div>
						 
						  	<table  class="subtables" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px;  border-collapse:collapse;" >
						  	    <thead>
		                           <tr>
		                         	<th colspan="2" style="text-align: center ;width:200px !important;"><b>Head</b></td>
		                         	<th colspan="2" style="text-align: center;width:120px !important;"><b>Sanction</b></td>
			                        <th colspan="2" style="text-align: center;width:120px !important;"><b>Expenditure</b></td>
			                        <th colspan="2" style="text-align: center;width:120px !important;"><b>Out Commitment</b> </td>
		                           	<th colspan="2" style="text-align: center;width:120px !important;"><b>Balance</b></td>
			                        <th colspan="2" style="text-align: center;width:120px !important;"><b>DIPL</b></td>
		                          	<th colspan="2" style="text-align: center;width:120px !important;"><b>Notional Balance</b></td>
			                      </tr>
			                      <tr>
				                    <th style="width:30px !important;text-align: center;" >SN</th>
				                    <th   style="width:180px !important;" width="10">Head</th>
				                    <th>RE</th>
				                    <th>FE</th>
				                    <th>RE</th>
				                    <th>FE</th>
			            	        <th>RE</th>
			                    	<th>FE</th>
		                  		    <th>RE</th>
				                    <th>FE</th>
				                    <th>RE</th>
				                    <th>FE</th>
				                    <th>RE</th>
				                    <th>FE</th>
		                       	  </tr>
			                    </thead>
			                     <%
			                 	double totSanctionCost=0,totReSanctionCost=0,totFESanctionCost=0;
				                	double totExpenditure=0,totREExpenditure=0,totFEExpenditure=0;
				                 	double totCommitment=0,totRECommitment=0,totFECommitment=0,totalDIPL=0,totalREDIPL=0,totalFEDIPL=0;
					                double totBalance=0,totReBalance=0,totFeBalance=0,btotalRe=0,btotalFe=0;
			                     
			                     if(IsIbasConnected==null || IsIbasConnected.equalsIgnoreCase("Y")) {%>
			                    <tbody>
			                    <% 

				                int count=1;
			                        if(projectFinancialDetails!=null && projectFinancialDetails.size()>0 && projectFinancialDetails.get(z)!=null ){
			                      for(ProjectFinancialDetails projectFinancialDetail:projectFinancialDetails.get(z)){                       %>
			 
			                         <tr>
										<td align="center" style="max-width:50px !important;text-align: center;"><%=count++ %></td>
										<td ><b><%=projectFinancialDetail.getBudgetHeadDescription()!=null?(projectFinancialDetail.getBudgetHeadDescription()): " - "%></b></td>
										<td align="right" style="text-align: right;"><%=df.format(projectFinancialDetail.getReSanction()) %></td>
										<%totReSanctionCost+=(projectFinancialDetail.getReSanction());%>
										<td align="right" style="text-align: right;"><%=df.format(projectFinancialDetail.getFeSanction())%></td>
										<%totFESanctionCost+=(projectFinancialDetail.getFeSanction());%>
										<td align="right" style="text-align: right;"><%=df.format(projectFinancialDetail.getReExpenditure()) %></td>
										<%totREExpenditure+=(projectFinancialDetail.getReExpenditure());%>
									    <td align="right" style="text-align: right;"><%=df.format(projectFinancialDetail.getFeExpenditure())%></td>
										<%totFEExpenditure+=(projectFinancialDetail.getFeExpenditure());%>
									    <td align="right" style="text-align: right;"><%=df.format(projectFinancialDetail.getReOutCommitment())%></td>
										<%totRECommitment+=(projectFinancialDetail.getReOutCommitment());%>
									    <td align="right" style="text-align: right;"><%=df.format(projectFinancialDetail.getFeOutCommitment())%></td>
										<%totFECommitment+=(projectFinancialDetail.getFeOutCommitment());%>
										<td align="right"style="text-align: right;"><%=df.format(projectFinancialDetail.getReBalance()+projectFinancialDetail.getReDipl())%></td>
										<%btotalRe+=(projectFinancialDetail.getReBalance()+projectFinancialDetail.getReDipl());%>
										<td align="right"style="text-align: right;"><%=df.format(projectFinancialDetail.getFeBalance()+projectFinancialDetail.getFeDipl())%></td>
								       	<%btotalFe+=(projectFinancialDetail.getFeBalance()+projectFinancialDetail.getFeDipl());%>
										<td align="right" style="text-align: right;"><%=df.format(projectFinancialDetail.getReDipl())%></td>
										<%totalREDIPL+=(projectFinancialDetail.getReDipl());%>
										<td align="right" style="text-align: right;"><%=df.format(projectFinancialDetail.getFeDipl())%></td>
										<%totalFEDIPL+=(projectFinancialDetail.getFeDipl());%>
										<td align="right" style="text-align: right;"><%=df.format(projectFinancialDetail.getReBalance())%></td>
										<%totReBalance+=(projectFinancialDetail.getReBalance());%>
										<td align="right" style="text-align: right;"><%=df.format(projectFinancialDetail.getFeBalance())%></td>
										<%totFeBalance+=(projectFinancialDetail.getFeBalance());%>
									</tr>
			<%} }%>

					<tr>
						<td colspan="2"><b>Total</b></td>
						<td align="right" style="text-align: right;"><%=df.format(totReSanctionCost)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totFESanctionCost)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totREExpenditure)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totFEExpenditure)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totRECommitment)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totFECommitment)%></td>
						<td align="right" style="text-align: right;"><%=df.format(btotalRe)%></td>
						<td align="right" style="text-align: right;"><%=df.format(btotalFe)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totalREDIPL)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totalFEDIPL)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totReBalance)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totFeBalance)%></td>
					</tr>
					<tr>
						<td colspan="2"><b>GrandTotal</b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=df.format(totReSanctionCost+totFESanctionCost)%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=df.format(totREExpenditure+totFEExpenditure)%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=df.format(totRECommitment+totFECommitment)%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=df.format(btotalRe+btotalFe)%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=df.format(totalREDIPL+totalFEDIPL)%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=df.format(totReBalance+totFeBalance)%></b></td>
					</tr>
			     </tbody>
			     <%}else{ %>
			     <tbody id="tbody<%=ProjectDetail.get(z)[0].toString()%>">
			     <%int count=0;
			     if(overallfinance!=null && overallfinance.size()>0 && overallfinance.get(z)!=null && overallfinance.get(z).size()>0)  {
			    	for(Object[]obj:overallfinance.get(z)){ 
			    	 %>
			    	 <tr>
			   <td align="center" style="max-width:50px !important;text-align: center;"><%=++count %></td>
				<td style="text-align: justify ;"><b><%=obj[4]!=null?(obj[4].toString()): " - "%></b></td>
				<td style="text-align: right;"><%=obj[5]!=null?(obj[5].toString()): " - "%></td>
				<td style="text-align: right;"><%=obj[6]!=null?(obj[6].toString()): " - "%></td>
				<td style="text-align: right;"><%=obj[7]!=null?(obj[7].toString()): " - "%></td>
				<td style="text-align: right;"><%=obj[8]!=null?(obj[8].toString()): " - "%></td>
				<td style="text-align: right;"><%=obj[9]!=null?(obj[9].toString()): " - "%></td>
				<td style="text-align: right;"><%=obj[10]!=null?(obj[10].toString()): " - "%></td>
				<td style="text-align: right;"><%=obj[11]!=null?(obj[11].toString()): " - "%></td>
				<td style="text-align: right;"><%=obj[12]!=null?(obj[12].toString()): " - "%></td>
				<td style="text-align: right;"><%=obj[13]!=null?(obj[13].toString()): " - "%></td>
				<td style="text-align: right;"><%=obj[14]!=null?(obj[14].toString()): " - "%></td>
				<td style="text-align: right;"><%=obj[15]!=null?(obj[15].toString()): " - "%></td>
				<td style="text-align: right;"><%=obj[16]!=null?(obj[16].toString()): " - "%></td>
				</tr>
			     <%}%>
			    	 	<tr>
						<td colspan="2"><b>Total</b></td>
						<td align="right" style="text-align: right;"><%=overallfinance.get(z).get(0)[17]!=null?(overallfinance.get(z).get(0)[17].toString()): " - "%></td>
						<td align="right" style="text-align: right;"><%=overallfinance.get(z).get(0)[18]!=null?(overallfinance.get(z).get(0)[18].toString()): " - "%></td>
						<td align="right" style="text-align: right;"><%=overallfinance.get(z).get(0)[19]!=null?(overallfinance.get(z).get(0)[19].toString()): " - "%></td>
						<td align="right" style="text-align: right;"><%=overallfinance.get(z).get(0)[20]!=null?(overallfinance.get(z).get(0)[20].toString()): " - "%></td>
						<td align="right" style="text-align: right;"><%=overallfinance.get(z).get(0)[21]!=null?(overallfinance.get(z).get(0)[21].toString()): " - "%></td>
						<td align="right" style="text-align: right;"><%=overallfinance.get(z).get(0)[22]!=null?(overallfinance.get(z).get(0)[22].toString()): " - "%></td>
						<td align="right" style="text-align: right;"><%=overallfinance.get(z).get(0)[23]!=null?(overallfinance.get(z).get(0)[23].toString()): " - "%></td>
						<td align="right" style="text-align: right;"><%=overallfinance.get(z).get(0)[24]!=null?(overallfinance.get(z).get(0)[24].toString()): " - "%></td>
						<td align="right" style="text-align: right;"><%=overallfinance.get(z).get(0)[25]!=null?(overallfinance.get(z).get(0)[25].toString()): " - "%></td>
						<td align="right" style="text-align: right;"><%=overallfinance.get(z).get(0)[26]!=null?(overallfinance.get(z).get(0)[26].toString()): " - "%></td>
						<td align="right" style="text-align: right;"><%=overallfinance.get(z).get(0)[27]!=null?(overallfinance.get(z).get(0)[27].toString()): " - "%></td>
						<td align="right" style="text-align: right;"><%=overallfinance.get(z).get(0)[28]!=null?(overallfinance.get(z).get(0)[28].toString()): " - "%></td>
					</tr>
			     	<tr>
						<td colspan="2"><b>GrandTotal</b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=Double.parseDouble(overallfinance.get(z).get(0)[17].toString())  +Double.parseDouble(overallfinance.get(z).get(0)[18].toString())%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=Double.parseDouble(overallfinance.get(z).get(0)[19].toString())  +Double.parseDouble(overallfinance.get(z).get(0)[20].toString())%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=Double.parseDouble(overallfinance.get(z).get(0)[21].toString())  +Double.parseDouble(overallfinance.get(z).get(0)[22].toString())%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=Double.parseDouble(overallfinance.get(z).get(0)[23].toString())  +Double.parseDouble(overallfinance.get(z).get(0)[24].toString())%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=Double.parseDouble(overallfinance.get(z).get(0)[25].toString())  +Double.parseDouble(overallfinance.get(z).get(0)[26].toString())%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=Double.parseDouble(overallfinance.get(z).get(0)[27].toString())  +Double.parseDouble(overallfinance.get(z).get(0)[28].toString())%></b></td>				     
			     	</tr>
			     <%}else{%> 
			     	<tr>
						<td colspan="2"><b>Total</b></td>
						<td align="right" style="text-align: right;"><%=df.format(totReSanctionCost)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totFESanctionCost)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totREExpenditure)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totFEExpenditure)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totRECommitment)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totFECommitment)%></td>
						<td align="right" style="text-align: right;"><%=df.format(btotalRe)%></td>
						<td align="right" style="text-align: right;"><%=df.format(btotalFe)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totalREDIPL)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totalFEDIPL)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totReBalance)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totFeBalance)%></td>
					</tr>
					<tr>
						<td colspan="2"><b>GrandTotal</b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=df.format(totReSanctionCost+totFESanctionCost)%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=df.format(totREExpenditure+totFEExpenditure)%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=df.format(totRECommitment+totFECommitment)%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=df.format(btotalRe+btotalFe)%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=df.format(totalREDIPL+totalFEDIPL)%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=df.format(totReBalance+totFeBalance)%></b></td>
					</tr>
			     <% }%>
			     </tbody>
			     <% } %>
			</table>  
		
		<%} %>
			
	<% for(int z=0 ; z<1;z++) {   %>
			<%if(wordFlag == null) {%> <h1 class="break"></h1> <%} else {%> <br class="break"> <%} %>	
			
				<div align="left" style="margin-left: 10px;">
				<%-- <a href="<%= HyperlinkPath+ "/MilestoneActivityList.htm?ProjectId="+projectid %>" target="_top" rel="noopener noreferrer" > --%>
				<%if(CommitteeCode.equalsIgnoreCase("EB")){ %>
   							<b class="sub-title">10. Action Plan for Next Six Months - Technical Milestones with Financial Outlay : </b>  
				<%}else { %>
							<b class="sub-title">10. Action Plan for Next Three Months - Technical Milestones with Financial Outlay : </b> 
				<%} %>
				<!-- </a> -->
		
		</div>
		
			<!-- Tharun code after Level -->
		
				<table class="subtables" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px;   border-collapse:collapse;" >
				
				
						<thead>
							<tr>
								<td colspan="9" style="border: 0">
								
								</td>									
							</tr>
							
								<tr>
									<th style="width: 15px !important;text-align: center;">SN</th>
									<th style="width: 20px; ">MS</th>
									<th style="width: 20px; ">L</th>
									<th style="width: 325px;">Action Plan </th>	
									<th style="width: 120px;" >Start Date</th>
									<th style="width: 120px;" >PDC</th>	
								<%if(!session.getAttribute("labcode").toString().equalsIgnoreCase("ADE")) {%>
									<th style="width: 210px;">Responsibility </th>
									<% } %>
								<%if(!session.getAttribute("labcode").toString().equalsIgnoreCase("DLRL")) {%>
									<th style="width: 50px;">Progress </th>
									<% } %>
<!-- 					                <th style="width: 50px;padding-right: 5px !important ">Status</th>
 -->					                <th style="width: 230px;">Remarks</th>
								</tr>
							</thead>
							<tbody>
								<%if(actionplanthreemonths.get(z).size()>0){ 
									long count1=1;
									int countA=1;
									int countB=1;
									int countC=1;
									int countD=1;
									int countE=1;
									%>
									<%int serialno=1; for(Object[] obj:actionplanthreemonths.get(z)){
										
										if(Integer.parseInt(obj[26].toString())<= Integer.parseInt(levelid) ){
										/*  if(obj[26].toString().equals("0")||obj[26].toString().equals("1")){ */
										%>
										
										<tr>
											<td style="text-align: center;"><%=serialno %></td>
											<td style="text-align: center">M<%=obj[22]!=null?(obj[22].toString()): " - " %></td>
							
											<td style="text-align: center">
												<%
												
												if(obj[26].toString().equals("0")) {%>
													<!-- L -->
												<%countA=1;
													countB=1;
													countC=1;
													countD=1;
													countE=1;
												}else if(obj[26].toString().equals("1")) { 
													for(Map.Entry<Integer,String>entry:treeMapLevOne.entrySet()){
														if(entry.getKey().toString().equalsIgnoreCase(obj[2].toString())){%>
															<%=entry.getValue()!=null?(entry.getValue()): " - " %>
													<%}} 
												
												%>
													<%-- A-<%=countA %> --%>
												<% /* countA++;
												    countB=1;
												    countC=1;
													countD=1;
													countE=1; */
												}else if(obj[26].toString().equals("2")) {  
													for(Map.Entry<Integer,String>entry:treeMapLevTwo.entrySet()){
														if(entry.getKey().toString().equalsIgnoreCase(obj[3].toString())){%>
															<%=entry.getValue()!=null?(entry.getValue()): " - " %>
													<%}} 
												
												
												%>
												<%-- 	B-<%=countB %> --%>
												<%/* countB+=1;
												countC=1;
												countD=1;
												countE=1; */
												}else if(obj[26].toString().equals("3")) { %>
													C-<%=countC %>
												<%countC+=1;
												countD=1;
												countE=1;
												}else if(obj[26].toString().equals("4")) { %>
													D-<%=countD %>
												<%
												countD+=1;
												countE=1;
												}else if(obj[26].toString().equals("5")) { %>
													E-<%=countE %>
												<%countE++;
												} %>
											</td>
											
											<td style="<%if(obj[26].toString().equals("0")) {%>font-weight: bold;<%}%>;text-align:justify ">
												<%if(obj[26].toString().equals("0")) {%>
													<%=obj[9]!=null?(obj[9].toString()): " - " %>
												<%}else if(obj[26].toString().equals("1")) { %>
													&nbsp;&nbsp;<%=obj[10]!=null?(obj[10].toString()): " - " %>
												<%}else if(obj[26].toString().equals("2")) { %>
													&nbsp;&nbsp;<%=obj[11]!=null?(obj[11].toString()): " - " %>
												<%}else if(obj[26].toString().equals("3")) { %>
													&nbsp;&nbsp;<%=obj[12]!=null?(obj[12].toString()): " - " %>
												<%}else if(obj[26].toString().equals("4")) { %>
													&nbsp;&nbsp;<%=obj[13]!=null?(obj[13].toString()): " - " %>
												<%}else if(obj[26].toString().equals("5")) { %>
													&nbsp;&nbsp;<%=obj[14]!=null?(obj[14].toString()): " - " %>
												<%} %>
											</td>
											<td style="text-align:center" ><%= obj[7]!=null? sdf.format(sdf1.parse(obj[7].toString())) : " - " %></td>
											<td  style="text-align:center">
											
												<%if(! LocalDate.parse(obj[8].toString()).isEqual(LocalDate.parse(obj[29].toString())) ){ %> 
													<%=sdf.format(sdf1.parse(obj[8].toString())) %> 
												<%}%>
												<%=sdf.format(sdf1.parse(obj[29].toString())) %>
											
											</td>
																			<%if(!session.getAttribute("labcode").toString().equalsIgnoreCase("ADE")) {%>
											
											<td ><%=obj[24]!=null?(obj[24].toString()): " - " %><%-- (<%=obj[25] %>) --%></td>
											
											<%} %>
											<%if(!session.getAttribute("labcode").toString().equalsIgnoreCase("DLRL")) {%>
											<td style="text-align: center"><%=obj[16]!=null?(obj[16].toString()): " - " %>%</td>
											<%} %>		
											<% 
												LocalDate StartDate = LocalDate.parse(obj[7].toString());
												LocalDate EndDate = LocalDate.parse(obj[8].toString());
												LocalDate OrgEndDate = LocalDate.parse(obj[29].toString());
												int Progess = Integer.parseInt(obj[16].toString());
												LocalDate CompletionDate =obj[18]!=null ? LocalDate.parse(obj[18].toString()) : null;
												LocalDate Today = LocalDate.now();
											%>									
							
											<td >
												<%if(obj[28]!=null){ %>
												<%=(obj[28].toString()) %>
												<%} %>
											</td>
										</tr>
										
									<%count1++; serialno++;}} %>
								<%} else{ %>
								
								<tr><td colspan="9" style="text-align:center; "> Nil</td></tr>
								<%} %>
								</tbody>
							</table>
<% } %>
<!-- ----------------------------------------------10. Action plan for next three------------------------------------------------- -->

	<% for(int z=0 ; z<1;z++) {   %>
		<%if(wordFlag == null) {%> <h1 class="break"></h1> <%} else {%> <br class="break"> <%} %>
<!-- -------------------------------------------------------------------------------------------- -->
		<div align="left" style="margin-left: 10px;">
				<b class="sub-title">11. Valuation of Technologies.  </b>
   		</div>
   		<div align="left" style="margin-top: 5px;margin-left: 10px;"><b class="mainsubtitle">(a) ManPower Utilisation in days. </b>
   		
	   		<table  class="subtables" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px;  border-collapse:collapse;" > 
	   			<thead>
			        <tr>
			            <th rowspan="2" class="width60">
			                ManPower Utilisation in days
			            </th>
			            <th colspan="4" class="width30">
			                Man-days utilised
			            </th>
			            <th rowspan="2" class="width60">
			                (cummulative past years)
			            </th>
			            <th rowspan="2" class="width50">
			                (cummulative Till date)
			            </th>
			        </tr>
			        <tr>
				        <th class="width150">(1<sup>st</sup> Quarter)</th>
						<th class="width150">(2<sup>nd</sup> Quarter)</th>
						<th class="width150">(3<sup>rd</sup> Quarter)</th>
						<th class="width150">(4<sup>th</sup> Quarter)</th>
			        </tr>
			    </thead>
			    <% 
				if(manpowerDetails != null && manpowerDetails.get(z).size()>0) { 		
					for(ProjectUtilizationBriefingDto obj : manpowerDetails.get(z)){
						
						%>
						<tr>
						    <td><%= obj.getDesigCrade() != null ? obj.getDesigCrade() : "-" %></td>
						
						    <td class="text-center"><%= obj.getFirstQuarter() != null ? obj.getFirstQuarter() : "-" %></td>
						
						    <td class="text-center"><%= obj.getSecondQuarter() != null ? obj.getSecondQuarter() : "-" %></td>
						
						    <td class="text-center"><%= obj.getThirdQuarter() != null ? obj.getThirdQuarter() : "-" %></td>
						
						    <td class="text-center"><%= obj.getFourthQuarter() != null ? obj.getFourthQuarter() : "-" %></td>
						
						    <td class="text-center"><%= obj.getCummulativePastYears() != null ? obj.getCummulativePastYears() : "-" %></td>
						
						    <td class="text-center"><%= obj.getCummulativeTillDate() != null ? obj.getCummulativeTillDate() : "-" %></td>
						</tr>
					<%} 
					} else{ %>
					<tr>
						<td colspan="9" class="text-center"> Nil</td>
					</tr>
				<%} %>
	   		</table>   		
   		</div>
   		
   		
   		<div align="left" style="margin-top: 5px;margin-left: 10px;"><b class="mainsubtitle">(b) Utilization of Established Infrastructure/ Facilities of the Lab/Sister Lab. </b>
   		
	   		<table  class="subtables" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px;  border-collapse:collapse;" > 
				    <thead>
				        <tr>
				            <th colspan="2" class="width150">
				               	(1<sup>st</sup> Quarter)
				            </th>
				            <th colspan="2" class="width150">
				               	(2<sup>nd</sup> Quarter)
				            </th>
				            <th colspan="2" class="width150">
				            	(3<sup>rd</sup> Quarter)
				            </th>
				            <th colspan="2" class="width150">
				            	(4<sup>th</sup> Quarter)
				            </th>
				            <th rowspan="2" class="width60">
				                (cummulative past years)
				            </th>
				            <th rowspan="2" class="width60">
				                (cummulative Till date)
				            </th>
				        </tr>
				        <tr>
				            <th class="width150">Name of Infra/Facility</th>
				            <th class="width150">Days Utilized</th>
				            <th class="width150">Name of Infra/Facility</th>
				            <th class="width150">Days Utilized</th>
				            <th class="width150">Name of Infra/Facility</th>
				            <th class="width150">Days Utilized</th>
				            <th class="width150">Name of Infra/Facility</th>
				            <th class="width150">Days Utilized</th>
				        </tr>
				    </thead>
				    <% if(infrastructureDetails != null && infrastructureDetails.get(z).size()>0) { 									
						for(ProjectUtilizationBriefingDto obj:infrastructureDetails.get(z)){
							
							%>
							<tr>
														
							    <td class="text-left"><%= obj.getNameOfInfrastructure() != null ? obj.getNameOfInfrastructure() : "-" %></td>
							    
							    <td class="text-center"><%= obj.getFirstQuarter() != null ? obj.getFirstQuarter() : "-" %></td>
							
							    <td class="text-left"><%= obj.getNameOfInfrastructure() != null ? obj.getNameOfInfrastructure() : "-" %></td>
							    
							    <td class="text-center"><%= obj.getSecondQuarter() != null ? obj.getSecondQuarter() : "-" %></td>
							
							    <td class="text-left"><%= obj.getNameOfInfrastructure() != null ? obj.getNameOfInfrastructure() : "-" %></td>
							    
							    <td class="text-center"><%= obj.getThirdQuarter() != null ? obj.getThirdQuarter() : "-" %></td>
							
							    <td class="text-left"><%= obj.getNameOfInfrastructure() != null ? obj.getNameOfInfrastructure() : "-" %></td>
							    
							    <td class="text-center"><%= obj.getFourthQuarter() != null ? obj.getFourthQuarter() : "-" %></td>
							
							    <td class="text-center"><%= obj.getCummulativePastYears() != null ? obj.getCummulativePastYears() : "-" %></td>
							
							    <td class="text-center"><%= obj.getCummulativeTillDate() != null ? obj.getCummulativeTillDate() : "-" %></td>
							</tr>
						<%} 
						} else{ %>
						<tr>
							<td colspan="10"  style="text-align: center!important;" > Nil</td>
						</tr>
					<%} %>
	   		</table>
   		</div>
   		
   		<div align="left" style="margin-top: 5px;margin-left: 10px;"><b class="mainsubtitle">(c) Training. </b>
   		
	   		<table  class="subtables" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px;  border-collapse:collapse;" > 
	   		 	<thead>
				        <tr>
				            <th colspan="2" class="width150">
				               	(1<sup>st</sup> Quarter)
				            </th>
				            <th colspan="2" class="width150">
				               	(2<sup>nd</sup> Quarter)
				            </th>
				            <th colspan="2" class="width150">
				            	(3<sup>rd</sup> Quarter)
				            </th>
				            <th colspan="2" class="width150">
				            	(4<sup>th</sup> Quarter)
				            </th>
				            <th rowspan="2" class="width60">
				                (cummulative past years)
				            </th>
				            <th rowspan="2" class="width60">
				                (cummulative Till date)
				            </th>
				        </tr>
				        <tr>
				            <th class="width150">Name of Infra/Facility</th>
				            <th class="width150">Days Utilized</th>
				            <th class="width150">Name of Infra/Facility</th>
				            <th class="width150">Days Utilized</th>
				            <th class="width150">Name of Infra/Facility</th>
				            <th class="width150">Days Utilized</th>
				            <th class="width150">Name of Infra/Facility</th>
				            <th class="width150">Days Utilized</th>
				        </tr>
				    </thead>
				    <% if(trainingDetails != null && trainingDetails.get(z).size()>0) { 									
						for(ProjectUtilizationBriefingDto obj:trainingDetails.get(z)){
							
							%>
							<tr>
														
							    <td class="text-left"><%= obj.getNameOfTraining() != null ? obj.getNameOfTraining() : "-" %></td>
							    
							    <td class="text-center"><%= obj.getFirstQuarter() != null ? obj.getFirstQuarter() : "-" %></td>
							
							    <td class="text-left"><%= obj.getNameOfTraining() != null ? obj.getNameOfTraining() : "-" %></td>
							    
							    <td class="text-center"><%= obj.getSecondQuarter() != null ? obj.getSecondQuarter() : "-" %></td>
							
							    <td class="text-left"><%= obj.getNameOfTraining() != null ? obj.getNameOfTraining() : "-" %></td>
							    
							    <td class="text-center"><%= obj.getThirdQuarter() != null ? obj.getThirdQuarter() : "-" %></td>
							
							    <td class="text-left"><%= obj.getNameOfTraining() != null ? obj.getNameOfTraining() : "-" %></td>
							    
							    <td class="text-center"><%= obj.getFourthQuarter() != null ? obj.getFourthQuarter() : "-" %></td>
							
							    <td class="text-center"><%= obj.getCummulativePastYears() != null ? obj.getCummulativePastYears() : "-" %></td>
							
							    <td class="text-center"><%= obj.getCummulativeTillDate() != null ? obj.getCummulativeTillDate() : "-" %></td>
							</tr>
						<%} 
						} else{ %>
						<tr>
							<td colspan="10"  style="text-align: center!important;" > Nil</td>
						</tr>
					<%} %>
	   		</table>
   		</div>
						
	<%} %>
	<!-- --------------------------------------------------------------- 11. point end ---------------------------------------------------- -->
	
	<% for(int z=0 ; z<1;z++) {   %>
		<%if(wordFlag == null) {%> <h1 class="break"></h1> <%} else {%> <br class="break"> <%} %>
<!-- -------------------------------------------------------------------------------------------- -->
		<div align="left" style="margin-left: 10px;">
				<b class="sub-title">12. Economic Impact of project.  </b>
				<table  class="subtables" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px;  border-collapse:collapse;" > 
	   		 		<thead>
				        <tr>
				            <th class="width150">Sl. No.</th>
				            <th class="width150">Economic Impact</th>
				            <th class="width150">Details</th>
				        </tr>
				    </thead>
					    <tbody>
						<% if(econmicImpactDetails != null && econmicImpactDetails.get(z) != null && econmicImpactDetails.get(z).size() > 0) {
						                    for(ProjectEconomicImpact obj : econmicImpactDetails.get(z)) { %>
		                    <tr>
		                        <td class="text-center">a)</td>
		                        <td class="text-left"> Percentage Indigenous Content, Dependent Foreign Countries, Items Imported and Indigenization Efforts </td>
		                        <td class="economic-value">  <%= obj.getIndigenousContentAndIndigenization() != null && !obj.getIndigenousContentAndIndigenization().trim().isEmpty() ? obj.getIndigenousContentAndIndigenization() : "-" %> </td>
		                    </tr>
		                    <tr>
		                        <td class="text-center">b)</td>
		                        <td class="economic-title"> International Collaborations Executed </td>
		                        <td class="economic-value"> <%= obj.getInternationalCollaborationsExecuted() != null && !obj.getInternationalCollaborationsExecuted().trim().isEmpty() ? obj.getInternationalCollaborationsExecuted() : "-" %> </td>
		                    </tr>
		                    <tr>
		                        <td class="text-center">c)</td>
		                        <td class="economic-title"> Intellectual Property Rights Generated </td>
		                        <td class="economic-value"> <%= obj.getIntellectualPropertyRights() != null && !obj.getIntellectualPropertyRights().trim().isEmpty() ? obj.getIntellectualPropertyRights() : "-" %> </td>
		                    </tr>
		                    <tr>
		                        <td class="text-center">d)</td>
		                        <td class="economic-title"> Export Potential </td>
		                        <td class="economic-value"> <%= obj.getExportPotential() != null && !obj.getExportPotential().trim().isEmpty() ? obj.getExportPotential() : "-" %> </td>
		                    </tr>
		                    <tr>
		                        <td class="text-center">e)</td>
		                        <td class="economic-title"> Infrastructure created </td>
		                        <td class="economic-value"> <%= obj.getInfrastructureCreated() != null && !obj.getInfrastructureCreated().trim().isEmpty() ? obj.getInfrastructureCreated() : "-" %> </td>
		                    </tr>
						<%}}else{ %>
						<tr>
							<td colspan="3" style="text-align: center!important;" >NIL</td>
						</tr>
						<%} %>
                	</tbody>
				</table>
   		</div>
   	<%} %>

	<% for(int z=0 ; z<1;z++) {   %>
			<%if(wordFlag == null) {%> <h1 class="break"></h1> <%} else {%> <br class="break"> <%} %>
<!-- ----------------------------------------------13.GANTT chart---------------------------------------------------------- -->
			<div align="left" style="margin-left: 15px;">
					<b class="sub-title">13. PERT/GANTT chart of overall project schedule :
					<!--  [<span style="text-decoration: underline;">Original (as per Project sanction / Latest PDC extension) and Current</span>]: --> 
				</b>
			</div>
			
              <% 
              String fileName = String.format("grantt_%s_%s.png", projectidlist.get(z).toString(), No2);
              String fileName1 = String.format("grantt_%s_%s.pdf", projectidlist.get(z).toString(), No2);
              Path uploadPath = Paths.get(filePath,projectLabCode,"gantt",fileName);
              Path uploadPath1 = Paths.get(filePath,projectLabCode,"gantt",fileName1);
			  File file = uploadPath.toFile();
			  File file1 = uploadPath1.toFile();
              if(file.exists()){ %>
				<div style="font-weight: bold;" align="right" >
					<br>
					<span >
						<span style="margin:0px 0px 10px  10px;">Original : &ensp; <span style=" background-color: #046582;  padding: 0px 15px; border-radius: 3px;"></span></span>
						<span style="margin:0px 0px 10px  15px;">Ongoing : &ensp; <span style=" background-color: #81b214;  padding: 0px 15px;border-radius: 3px;"></span></span>
						<span style="margin:0px 0px 10px  15px;">Revised : &ensp; <span style=" background-color: #f25287; opacity: 0.5; padding: 0px 15px;border-radius: 3px;"></span></span>
					</span>
				</div>
				<div align="center"><br>
					<img class="logo" style="max-width:25cm;max-height:17cm;margin-bottom: 5px" src="data:image/*;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(file))%>" alt="confi" > 
				</div>
				
              <%} else if(file1.exists()){ %>
              
				<b>Grantt Chart Annexure</b>
				
		    <% }else{ %>
		    	<div align="center">
		    		<br>
		    		File Not Found
		    	</div>
			<%} %>

			<div>
	<%} %>
	
		<% for(int z=0 ; z<1;z++) {   %>
			<%if(wordFlag == null) {%> <h1 class="break"></h1> <%} else {%> <br class="break"> <%} %>
			<div align="left" style="margin-left: 10px;"><b class="sub-title">14. Issues:</b></div>
			
			<table class="subtables" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px;   border-collapse:collapse;" >
						<thead>
							<tr>
								<td colspan="7" style="border: 0">
								
								</td>									
							</tr>
							<tr>
								<th  style="width: 20px !important;text-align: center;">SN</th>
								<th  style="width: 20px !important;text-align: center;">ID</th>
								<th  style="width: 370px;">Issue Point</th>
								<th  style="width: 100px; "> ADC<br>PDC</th>
<!-- 								<th  style="width: 80px; "> ADC</th> -->
								<th  style="width: 210px; ">Responsibility</th>
<!-- 								<th  style="width: 50px; ">Status</th>	
 -->								<th  style="width: 270px; ">Remarks</th>		
							</tr>
						</thead>
						<tbody>				
										<%if(oldpmrcissueslist.get(z).size()==0){ %>
										<tr><td colspan="7" style="text-align: center;" > Nil</td></tr>
										<%}
										else if(oldpmrcissueslist.get(z).size()>0)
										  {int i=1;
										for(Object[] obj:oldpmrcissueslist.get(z)){ 
											if(!obj[9].toString().equals("C")  || (obj[9].toString().equals("C") && obj[13]!=null &&  before6months.isBefore(LocalDate.parse(obj[13].toString())) )){
										%>
											<tr>
												<td  style="text-align: center;"><%=i %></td>
							<td style="text-align: center;" >
									<%if(obj[18]!=null && Long.parseLong(obj[18].toString())>0){
										String []temp=obj[1].toString().split("/");
										String tempString=temp[temp.length-1];
										%>
									<span style="font-weight: bold">
										<%=tempString!=null?(tempString): " - "%>
										</span>
									<%}%>
								</td>
												<td  style="text-align: justify;"><%=obj[2]!=null?(obj[2].toString()): " - " %></td>
												<td   style="text-align: center;" >
												<%	String actionstatus = obj[9].toString();
															int progress = obj[16]!=null ? Integer.parseInt(obj[16].toString()) : 0;
															LocalDate pdcorg = LocalDate.parse(obj[3].toString());
															LocalDate endDate = LocalDate.parse(obj[4].toString());
															
															LocalDate lastdate = obj[13]!=null ? LocalDate.parse(obj[13].toString()): null;
															LocalDate today = LocalDate.now();
													%> 
													<% if(lastdate!=null && actionstatus.equalsIgnoreCase("C") ){%>
														<%if(actionstatus.equals("C") && (pdcorg.isAfter(lastdate) || pdcorg.equals(lastdate))){%>
														<span class="completed"><%= sdf.format(sdf1.parse(obj[13].toString()))%> </span>
														<%}else if(actionstatus.equals("C") && pdcorg.isBefore(lastdate)){ %>	
														<span class="completeddelay"><%= sdf.format(sdf1.parse(obj[13].toString()))%> </span>
														<%} %>	
													<%}else{ %>
															-									
													<%} %>
													<br>
													<%-- <% if (obj[6] != null && !LocalDate.parse(obj[6].toString()).equals(LocalDate.parse(obj[5].toString())) ) { %> <%=sdf.format(sdf1.parse(obj[6].toString()))%><br> <% } %> 
													<% if (obj[5] != null && !LocalDate.parse(obj[5].toString()).equals(LocalDate.parse(obj[3].toString())) ) { %> <%=sdf.format(sdf1.parse(obj[5].toString()))%><br> <% } %> --%>
													<%if(!pdcorg.equals(endDate)) {%>
													<%=sdf.format(sdf1.parse(obj[4].toString()))%><br>
													<%} %>
													<%=sdf.format(sdf1.parse(obj[3].toString()))%>
												</td>
											
												<td > <%=obj[11]!=null?(obj[11].toString()): " - " %></td>
				
												<td > <%if(obj[17]!=null){ %> <%=(obj[17].toString())%> <%} %> </td>			
											</tr>			
										<%i++;
										}} }%>
								</tbody>			
							</table>
			<%} %>
				
			<% for(int z=0 ; z<1;z++) {   %>
			<%if(wordFlag == null) {%> <h1 class="break"></h1> <%} else {%> <br class="break"> <%} %>	
			<div align="left" style="margin-left: 10px;"><b class="sub-title"> 
   							<%if(CommitteeCode.equalsIgnoreCase("EB")){ %>
   								15. Other Relevant Points (if any) and Technical Work Carried Out For Last Six Months
							<%}else { %>
								15. Other Relevant Points (if any) and Technical Work Carried Out For Last Three Months
							<%} %>
   						</b></div>
   						
						<table class="subtables" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px;  border-collapse:collapse;width:" >
							<tr>
								 <th  style="width: 80%; ">Other Relevant Points</th>
								 <!-- <th  style="width: 10%; ">Technical Work Carried</th> -->		
							</tr>
										
									<% if(TechWorkDataList.get(z)!=null){ %>
										<tr>
											<td style="text-align: justify;"><%=(TechWorkDataList.get(z)[2].toString()) %></td>
										</tr>
								<%}else{ %>
									<tr><td colspan="2" style="text-align: center ;">Nil </td></tr>
								<%} %>
									
						</table>
						
						<% if(TechImages.size()>0){
							List<TechImages>  TechImagesList= TechImages.get(z); 
							if(TechImagesList.size()>0){%>
						
						<%if(wordFlag == null) {%> <h1 class="break"></h1> <%} else {%> <br class="break"> <%} %>	
						<b class="mainsubtitle"> &nbsp;&nbsp;Technical Images</b> 
						
						<%} }%>
					<div>
										
					<br>
					</div>
							<% if(TechImages.size()>0){
							List<TechImages>  TechImagesList= TechImages.get(z); 
							if(TechImagesList.size()>0){
							for(TechImages imges:TechImagesList){ %>
							<div>
								<table>
									<tr>
										<td style="border:0; padding-left: 1.5rem;"> 
											<%
											Path techPath = Paths.get(filePath,projectLabCode,"TechImages",imges.getTechImagesId()+"_"+imges.getImageName());
											File tecfile = techPath.toFile();
											if(tecfile.exists()){ %>
											<img style="max-width:25cm;max-height:17cm;margin-bottom: 5px" src="data:image/*;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(tecfile))%>" > 											
											<%} %>
										</td>
										<td style="border:0;">  
										</td>
									</tr>
								</table>
							</div>
							<br>
							<%}}} %>
						
						
			<%} %>	
			<% for(int z=0 ; z<1;z++) {   %>
											<%if(wordFlag == null) {%> <h1 class="break"></h1> <%} else {%> <br class="break"> <%} %>		
<!-- -------------------------------------------------------------------------------------------------------------------------------------------------------- -->
						<div align="left" style="margin-left: 10px;"><b class="sub-title">16. Decision/Recommendations sought from <%=CommitteeCode.toUpperCase() %> Meeting :</b></div>
							
							<table class="subtables" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px;   border-collapse:collapse;" >
			
											<thead>
												<tr><th style="width: 5%;">SN</th><th style="width: 5%;">Type</th><th style="width: 85%;">Details</th></tr>
											</thead>
											<tbody>
												<%int i=0; if(RecDecDetails!=null && RecDecDetails.size()>0){ 
												for(Object[] obj :RecDecDetails){%>
												<tr>
													<td style="width: 5%; text-align: center;">  <%=++i%></td>
													<td style="width: 5%; text-align: center;"> <%=obj[2]!=null?(obj[2].toString()): " - "%></td>
													<td style="width: 85%;  word-wrap: break-word;"> <%=obj[3]!=null?(obj[3].toString()): " - "%></td>
													
												</tr>
												<%}}else{%><td colspan="3" style="text-align: center;"> No Data Available!</td><%}%>
					</tbody>
					</table>	
				<%} %>	
		<%if(wordFlag == null) {%> <h1 class="break"></h1><%} %> 

		<%if(wordFlag == null) {%>
			<!-- <div align="center" style="text-align: center; vertical-align: middle ;font-size:60px;font-weight: 600;margin: auto; position: relative;color: #145374 !important" >THANK YOU</div> -->
	       	<%if(thankYouImg!=null ){ %>
	       		<div class="content" >
	       			
					<img class="" style="width: 100%; height: 100%;"  src="data:image/*;base64,<%=thankYouImg%>" alt="Logo" > 
				</div>	
			<%}else{ %>
				 <div align="center" style="text-align: center; vertical-align: middle ;font-size:60px;font-weight: 600;margin-top: 250px; position: relative;color: #145374 !important" >THANK YOU</div>
			<%} %>
		<%} %> 
	</div>	
</body>
<%if(wordFlag!=null && wordFlag.equalsIgnoreCase("Y")) { %>	 
<script type="text/javascript">

	$(document).ready(function() {
		$("#export-container").wordExport("Briefing_Paper");
	});
</script>	
<%} %>
</html>

