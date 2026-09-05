<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.nio.file.Paths"%>
<%@page import="java.nio.file.Path"%>
<%@page import="java.time.DayOfWeek"%>
<%@page import="java.util.stream.Collector"%>
<%@page import="java.math.MathContext"%>
<%@page import="com.vts.pfms.model.TotalDemand"%>
<%@page import="java.time.temporal.ChronoUnit"%>
<%@page import="com.vts.pfms.committee.model.Committee"%>
<%@page import="org.apache.commons.io.FileUtils"%>
<%@page import="com.vts.pfms.print.model.TechImages"%>
<%@page import="com.vts.pfms.model.LabMaster"%>
<%@page import="com.vts.pfms.AESCryptor"%>
<%@page import="org.apache.commons.io.FilenameUtils"%>
<%@page import="java.io.File"%>
<%@page import="java.math.BigInteger"%>
<%@page import="java.text.Format"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="com.vts.pfms.master.dto.ProjectFinancialDetails"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.math.BigDecimal"%> 
<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat,java.time.LocalDate"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/ckeditor/ckeditor.js" var="ckeditor" />
<script src="${ckeditor}"></script>
<spring:url value="/resources/js/excel.js" var="excel" />
<script src="${excel}"></script>
<spring:url value="/resources/ckeditor/contents.css" var="contentCss" />
<link href="${contentCss}" rel="stylesheet" />
<spring:url value="/resources/css/sweetalert2.min.css" var="sweetalertCss" />
<spring:url value="/resources/js/sweetalert2.min.js" var="sweetalertJs" />
<link href="${sweetalertCss}" rel="stylesheet" />
<script src="${sweetalertJs}"></script>
<spring:url value="/resources/css/print/projectBriefingPaperNew.css" var="projectBriefingPaperNew" />
<link href="${projectBriefingPaperNew}" rel="stylesheet" />
<spring:url value="/resources/css/action/actionCommon.css" var="actionCommon" />
<link href="${actionCommon}" rel="stylesheet" />
<title>Briefing </title>



<meta charset="ISO-8859-1">
</head>
<body >
<%
DecimalFormat df=new DecimalFormat("####################.##");
FormatConverter fc=new FormatConverter(); 
SimpleDateFormat sdf=fc.getRegularDateFormat();
SimpleDateFormat sdf1=fc.getSqlDateFormat();

int addcount=0; 
NFormatConvertion nfc=new NFormatConvertion();
Format format = com.ibm.icu.text.NumberFormat.getCurrencyInstance(new Locale("en", "in"));
String filePath=(String)request.getAttribute("filePath");
String projectLabCode=(String)request.getAttribute("projectLabCode");
List<Object[]> projectslist=(List<Object[]>)request.getAttribute("projectslist");
String projectid=(String)request.getAttribute("projectid");
String committeeid=(String)request.getAttribute("committeeid");
Committee committee=(Committee)request.getAttribute("committeeData");

List<Object[]> projectattributeslist = (List<Object[]> )request.getAttribute("projectattributes");
List<List<Object[]>> ebandpmrccount = (List<List<Object[]>> )request.getAttribute("ebandpmrccount");
List<List<Object[]>> milestones= (List<List<Object[]>>)request.getAttribute("milestones");
List<List<Object[]>> lastpmrcactions = (List<List<Object[]>>)request.getAttribute("lastpmrcactions");
List<List<Object[]>> lastpmrcminsactlist = (List<List<Object[]>>)request.getAttribute("lastpmrcminsactlist");
List<List<Object[]>> ganttchartlist=(List<List<Object[]>>)request.getAttribute("ganttchartlist");
List<Object[]> projectdatadetails = (List<Object[]> )request.getAttribute("projectdatadetails");
List<List<Object[]>> oldpmrcissueslist=(List<List<Object[]>>)request.getAttribute("oldpmrcissueslist");

List<List<ProjectFinancialDetails>> projectFinancialDetails = (List<List<ProjectFinancialDetails>>)request.getAttribute("financialDetails");
List<List<Object[]>> procurementOnDemand = (List<List<Object[]>>)request.getAttribute("procurementOnDemandlist");
List<List<Object[]>> procurementOnSanction = (List<List<Object[]>>)request.getAttribute("procurementOnSanctionlist");
List<List<Object[]>> riskmatirxdata = (List<List<Object[]>>)request.getAttribute("riskmatirxdata");
List<List<Object[]>> actionplanthreemonths = (List<List<Object[]>>)request.getAttribute("actionplanthreemonths");
List<Object[]> TechWorkDataList=(List<Object[]>)request.getAttribute("TechWorkDataList");
List<Object[]> ProjectDetail=(List<Object[]>)request.getAttribute("ProjectDetails"); 
List<String> projectidlist = (List<String>)request.getAttribute("projectidlist");
List<Object[]> pdffiles=(List<Object[]>)request.getAttribute("pdffiles");
List<Object[]> milestoneactivitystatus =(List<Object[]>)request.getAttribute("milestoneactivitystatus");
List<Object[]> MilestoneList=(List<Object[]>)request.getAttribute("MilestoneActivityList");
String ProjectId=(String)request.getAttribute("projectid");
List<TotalDemand> totalprocurementdetails = (List<TotalDemand>)request.getAttribute("TotalProcurementDetails");
//List<List<Object[]>> ReviewMeetingList=(List<List<Object[]>>)request.getAttribute("ReviewMeetingList");
//List<List<Object[]>> ReviewMeetingListPMRC=(List<List<Object[]>>)request.getAttribute("ReviewMeetingListPMRC");
Map<String, List<Object[]>> reviewMeetingListMap = (Map<String, List<Object[]>>) request.getAttribute("reviewMeetingListMap");
List<List<Object[]>> ProjectRevList = (List<List<Object[]>>)request.getAttribute("ProjectRevList");
List<List<Object[]>> MilestoneDetails6 = (List<List<Object[]>>)request.getAttribute("milestonedatalevel6");//b
List<List<TechImages>> TechImages = (List<List<TechImages>>)request.getAttribute("TechImages");

List<Object[]> SpecialCommitteesList = (List<Object[]>)request.getAttribute("SpecialCommitteesList");

List<List<Object[]>> sunsetmilestones = (List<List<Object[]>>)request.getAttribute("sunsetmilestones");

long ProjectCost = (long)request.getAttribute("ProjectCost"); 
String levelid= (String) request.getAttribute("levelid");
LocalDate before6months = LocalDate.now().minusDays(committee.getPeriodicDuration());
String No2=null;
SimpleDateFormat sdfg=new SimpleDateFormat("yyyy");

if(ebandpmrccount!=null && ebandpmrccount.size()>0){
	List<Object[]> ebandpmrcsub = ebandpmrccount.get(0);
	Object[] comcount = ebandpmrcsub.stream().filter(e -> e[0].toString().equalsIgnoreCase(committee.getCommitteeShortName())).findFirst().orElse(null);
	No2=committee.getCommitteeShortName()+(Long.parseLong(comcount!=null? comcount[1].toString() : "0")+1);
	
}


Object[] nextMeetVenue =  (Object[]) request.getAttribute("nextMeetVenue");
List<Object[]> RecDecDetails = (List<Object[]>)request.getAttribute("recdecDetails");

List<Object[]> RiskTypes = (List<Object[]>)request.getAttribute("RiskTypes");
Map<Integer,String> treeMapLevOne =(Map<Integer,String>)request.getAttribute("treeMapLevOne");
Map<Integer,String> treeMapLevTwo =(Map<Integer,String>)request.getAttribute("treeMapLevTwo");

List<Object[]> envisagedDemandlist = (List<Object[]>)request.getAttribute("envisagedDemandlist");
SimpleDateFormat inputFormat = new SimpleDateFormat("ddMMMyyyy", Locale.ENGLISH);
SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
Map<Integer,String> committeeWiseMap=(Map<Integer,String>)request.getAttribute("committeeWiseMap");
//Map<Integer,String> mapEB=(Map<Integer,String>)request.getAttribute("mapEB");

List<Object[]> otherMeetingList = (List<Object[]>)request.getAttribute("otherMeetingList");
List<List<Object[]>> overallfinance = (List<List<Object[]>>)request.getAttribute("overallfinance");//b
String thankYouImg = (String)request.getAttribute("thankYouImg");
String IsIbasConnected=(String)request.getAttribute("IsIbasConnected");
String isCCS = (String)request.getAttribute("isCCS");
String labcode = (String) session.getAttribute("labcode");
%>
	<% String ses = (String) request.getParameter("result"); 
       String ses1 = (String) request.getParameter("resultfail");
       if (ses1 != null) { %>
        <div align="center">
            <div class="alert alert-danger" role="alert">
                <%= ses1 %>
            </div>
        </div>
    <% } if (ses != null) { %>
        <div align="center">
            <div class="alert alert-success" role="alert">
                <%= ses %>
            </div>
        </div>
 	<% } %>
	<div id="spinner" class="spinner display-none"><img id="img-spinner" class="img-height" src="view/images/spinner1.gif" alt="Loading"/></div>
 
	<div class="container-fluid">
		<div class="row" id="main">
			<div class="col-md-12">
				<div class="card shadow-nohover">
					<div class="row card-header" >
			   			<div class="col-md-4 marging-top8"  >
							<h3>Project Briefing Paper</h3>
						</div>							
						<div class="col-md-8 justify-content-end margin-top-17 float-right" >
						<form method="post" action="ProjectBriefingPaper.htm" id="projectchange">
							<table >
								<tr>
									<td  class="border-0 padding-top13px"><h6>Project </h6></td>
									<td  class="border-0">
										
										<select class="form-control items width200" name="projectid"  required="required"  data-live-search="true" data-container="body" onchange="submitForm('projectchange');">
											<%for(Object[] obj : projectslist){ 
												String projectshortName=(obj[17]!=null)?" ("+obj[17].toString().trim()+") ":"";
											%>
												<option value=<%=obj[0]%> <%if(projectid!=null && projectid.equals(obj[0].toString())) { %>selected <%} %> ><%=obj[4] +projectshortName%></option>
											<%} %>
										</select>
									</td>
									<td  class="border-0 padding-top13px"><h6>Committee</h6></td>
									<td  class="border-0 ">
										<select class="form-control items width200" name="committeeid"  required="required"  data-live-search="true" data-container="body" onchange="submitForm('projectchange');">
											<%
											for(Object[] comm : SpecialCommitteesList){ %>

												<option <%if(committeeid.equalsIgnoreCase(comm[0].toString())){ %>selected<%} %> value="<%=comm[0] %>" ><%=comm[1] %></option>
											<%} %>
										</select>
									</td>
									
									<td class="border-0"> 
										<button  type="submit" class="btn btn-sm border-radius3 border-0"  formmethod="GET" formaction="ProjectBriefingDownload.htm" formtarget="_blank"
										 data-toggle="tooltip" data-placement="top" title="Briefing Paper pdf" >
											<i class="fa fa-download fa-lg" aria-hidden="true"></i>
										</button>
									</td>
									<td class="border-0"> 
										<button type="submit" class="btn btn-sm border-radius3 border-0"  formmethod="GET" formaction="ProjectBriefingWordDownload.htm" formtarget="_blank"
										 data-toggle="tooltip" data-placement="top" title="Briefing Paper Word" >
											<img alt="" src="view/images/worddoc.png" class="width19">
										</button>
									</td>
									<td class="border-0 "> 
										<button  type="submit" class="btn btn-sm border-radius3 border-0" formmethod="POST" formaction="ProjectBriefingFreeze.htm" onclick="return confirm('Are You Sure To Freeze Briefing Paper for Next Scheduled Meeting ?')" title="Freeze" 
										data-toggle="tooltip" data-placement="top">
											<i class="fa fa-certificate fa-lg text-danger"  aria-hidden="true"></i>
										</button>
									</td>
									<td class="border-0 "> 
										<button  type="submit" class="btn btn-sm border-radius3 border-0 " formmethod="POST" formaction="BriefingPresentation.htm" formtarget="_blank" title="Presentation" 
										data-toggle="tooltip" data-placement="top" >
											<img alt="" src="view/images/presentation.png" class="width19">
										</button>
									</td>
										<td class="border-0 "> 
										<button  type="submit" class="btn btn-sm border-radius3 border-0"  name="text" value="p" formmethod="GET" formaction="ProjectBriefingDownload.htm" formtarget="_blank"
										data-toggle="tooltip" data-placement="top" title="Presentation pdf">
											<img alt="" src="view/images/presentation.png" class="width19"><i class="fa fa-download marging-left6" aria-hidden="true" ></i>
										</button>
									</td>
									<td class="border-0 "><button  type="button" class="btn btn-sm back btn-black"  data-toggle="modal" data-target="#LevelModal"  >Mil Level (<%=levelid %>)</button></td>
								</tr>
							</table>
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								<input type="hidden" name="projectid" value="<%=projectid%>"/>
								<input type="hidden" name="committeeid" value="<%=committeeid%>"/>	
							</form>				
						</div>
					 </div>
					 
	
						<div class="card-body">	
						
				 		
							 <details>					
							    <summary role="button" class="no-arrow" tabindex="0"><b>1. Project Attributes </b>  </summary>
								<%-- <div class="content">
									<% 
									for(int z=0;z<projectidlist.size();z++)
									{
										List<Object[]>  revlist= ProjectRevList.get(z); 
										Object[] projectattributes =projectattributeslist.get(z);	%>  
										<%if(projectattributes!=null){ %>
										
										<div>
											<form action="ProjectSubmit.htm" method="post" target="_blank">
												<b>Project : <%=ProjectDetail.get(z)[1] %> 	<%if(z!=0){ %>(SUB)<%} %>	</b>
												<button type="submit" name="action" value="edit"  class="btn btn-sm edit padding-3px" > <i class="fa fa-pencil-square-o fa-lg pencil-icon"  aria-hidden="true"></i> </button>
												<input type="hidden" name="ProjectId" value="<%=ProjectDetail.get(z)[0] %>">
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											</form>
										</div>	
										
										
									<table class="subtables projectattributetable"  >
										<tr>
											 <td class="cst-td">(a)</td>
											 <td class="cst-td1"><b>Project Title</b></td>
											 <td colspan="4" class="cst-td2"> <%=projectattributes[1] %></td>
										</tr>
										<tr>
											 <td  class="td-cp">(b)</td>
											 <td class="cst-td1"><b>Project Code</b></td>
											 <td colspan="4" class="cst-td2"> <%=projectattributes[0]%> </td>
										</tr>
										<tr>
											 <td  class=" td-cp">(c)</td>
											 <td  class="cst-td1"><b>Category</b></td>
											 <td colspan="4" class="cst-td2"><%=projectattributes[14]%></td>
										</tr>
										<tr>
											 <td  class="td-cp">(d)</td>
											 <td  class="cst-td1"><b>Date of Sanction</b></td>
											 <td colspan="4" class="cst-td2"><%=sdf.format(sdf1.parse(projectattributes[3].toString()))%></td>
										</tr>
										<tr>
											 <td  class="width20-padding5">(e)</td>
											 <td  class="width150-padding5"><b>Nodal and Participating Labs</b></td>
											 <td colspan="4" class="cst-td2"><%if(projectattributes[15]!=null){ %><%=projectattributes[15]%><%} %></td>
										</tr>
										<tr>
											 <td  class="td-cp">(f)</td>
											 <td  class="width150-padding5"><b>Objective</b></td>
											 <td colspan="4" class="cst-td2 text-justify"> <%=projectattributes[4]%></td>
										</tr>
										<tr>
											 <td  class="td-cp">(g)</td>
											 <td  class="width150-padding5"><b>Deliverables</b></td>
											 <td colspan="4" class=" cst-td2"> <%=projectattributes[5]%></td>
										</tr>
										<tr>
											 <td rowspan="2" class="td-cp">(h)</td>
											 <td rowspan="2" class="width150-padding5"><b>PDC</b></td>
											 
											<td colspan="2" class="textaligncenter">&nbsp;</td>					
											<%if( ProjectRevList.get(z).size()>0){ %>	
												<td colspan="2" class="textaligncenter">Revised</td>																			
											<%}else{ %>													 
										 		<td colspan="2" ></td>	
										 	<%} %>
										</tr>
								 		<tr>
								 			<%if( ProjectRevList.get(z).size()>0 ){ %>								
										 		<td colspan="2" class="text-center"><%= sdf.format(sdf1.parse(ProjectRevList.get(z).get(0)[12].toString()))%> </td>
										 		<td colspan="2" class="text-center">
											 		<%if(LocalDate.parse(projectattributes[6].toString()).isEqual(LocalDate.parse(ProjectRevList.get(z).get(0)[12].toString())) ){ %>
											 			-
											 		<%}else{ %>
											 			<%= sdf.format(sdf1.parse(projectattributes[6].toString()))%>
											 		<%} %>
										 		
										 		</td>
											<%}else{ %>													 
										 		<td colspan="2" class="textaligncenter"><%= sdf.format(sdf1.parse(projectattributes[6].toString()))%></td>
												<td colspan="2" ></td>
										 	<%} %>
										 		    
								 		</tr>
											 	
										<tr>
											<td rowspan="3" class="td-i">(i)</td>
											<td rowspan="3" class="padding-left10"><b>Cost Breakup( &#8377; <span class="currency">Lakhs</span>)</b></td>
											
											<%if( ProjectRevList.get(z).size()>0 ){ %>
													<td class="width10" >RE Cost</td>
													<td class="text-center"><%=ProjectRevList.get(z).get(0)[17] %></td> 
													<td colspan="2" class="text-center"><%=projectattributes[8] %></td>
												</tr>
												
												
												<tr>
													<td class="width10">FE Cost</td>		
													<td class="text-center"><%=ProjectRevList.get(z).get(0)[16] %></td>					
													<td colspan="2" class="text-center"><%=projectattributes[9] %></td>
												</tr>
													
												<tr>	
													<td class="width10">Total Cost</td>	
													<td class="text-center"><%=ProjectRevList.get(z).get(0)[11] %></td>
											 		<td colspan="2" class="text-center"><%=projectattributes[7] %></td>
												</tr> 
														
											<%}else{ %>
													
													<td class="width10">RE Cost</td>
													<td ><%=projectattributes[8] %></td>
													<td colspan="2" ></td>
												</tr>
											
												<tr>
													<td class="width10">FE Cost</td>		
													<td ><%=projectattributes[9] %></td>					
													<td colspan="2"></td>
												</tr>
												
												<tr>	
													<td class="width10" >Total Cost</td>	
													<td ><%=projectattributes[7] %></td>
													<td colspan="2"></td>			
												</tr> 
											<%} %>
												
																			 	
										<tr>
											<td  class="td-j">(j)</td>
											<td class="width150-padding5"><b>No. of Meetings held</b> </td>
											<td colspan="4">
												<% if(ebandpmrccount!=null && ebandpmrccount.size()>0){
													List<Object[]> ebandpmrcsub = ebandpmrccount.get(z); 
													for(Object[] ebandpmrc: ebandpmrcsub) { %>
												 	<b><%=ebandpmrc[0] %> : </b>
													<span><%=ebandpmrc[1] %></span> &emsp;&emsp;
												<%} }%>
											</td>
										</tr>
										<tr>
											<td  class="td-j">(k)</td>
											<td  class="td-k"><b>Current Stage of Project</b></td>
										  	<%
											   String colorCode = projectdatadetails.get(z)!=null ? (String) projectdatadetails.get(z)[11] : "#77D970";
											   String className = "C" + colorCode.replace("#", "").toUpperCase();
											%>
											<td colspan="4" 
		  									 class="ctm-td <%=className%>"> <%= projectdatadetails.get(z) != null ? "<b class="+4+">" + projectdatadetails.get(z)[10] + "</b>" : "Data Not Found" %>
										</td>

										</tr>	
									</table>
		
										<%}else{ %>
											<div align="center" class="margin25"> Complete Project Data Not Found </div>
										<%} %>
									<% } %>
							</div> --%>
						</details>

<!--  ---------------------------------------------------------------------------------------------------------------------------------------------  -->		
				 
						<details>
	   						<summary role="button" class="no-arrow" tabindex="0"><b>2. Schematic Configuration</b>   </summary>
	   						<%-- <div class="content">
	   						<%for(int z=0;z<1;z++){ %>
	   						<div align="left" class="margin-left15">
	   							
								<%if(ProjectDetail.size()>1){ %>
										<div>
											<form action="ProjectData.htm" method="post" target="_blank">
												<b>Project : <%=ProjectDetail.get(z)[1] %> 	<%if(z!=0){ %>(SUB)<%} %>	</b>
												<button type="submit" name="action" value="edit"  class="btn btn-sm edit padding-3px" > <i class="fa fa-pencil-square-o fa-lg pencil-icon"  aria-hidden="true"></i> </button>
												<input type="hidden" name="projectid" value="<%=ProjectDetail.get(z)[0] %>">
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											</form>
										</div>	
								<%} %>
	   							<table >
									<tr>
										<td class="border-0"> 
										
										
											<%if(projectdatadetails.get(z)!=null && projectdatadetails.get(z)[3]!=null){ %>
												<form action="ProjectDataSystemSpecsFileDownload.htm"  method="post" target="_blank" >	
													2 (a) System Configuration. &nbsp; <span class="anchorlink" onclick="$('#config<%=ProjectDetail.get(z)[0] %>').toggle();"  ><b>As on File Attached</b></span>
													<button  type="submit" class="btn btn-sm "  ><i class="fa fa-download fa-lg" ></i></button>
													<input type="hidden" name="projectdataid" value="<%=projectdatadetails.get(z)[0]%>"/>
													<input type="hidden" name="filename" value="sysconfig"/>
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
												</form>	
												
												<%=projectdatadetails.get(z)[3].toString() %>
												<%
												Path systemPath = Paths.get(filePath,projectLabCode,"ProjectData",projectdatadetails.get(z)[3].toString());
												
												File systemfile = systemPath.toFile();
												if(systemfile.exists()){
												if(FilenameUtils.getExtension(projectdatadetails.get(z)[3].toString()).equalsIgnoreCase("pdf")){ %>
													<iframe	width="1200" height="600" src="data:application/pdf;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(systemfile))%>"  id="config<%=ProjectDetail.get(z)[0] %>" class="display-none" > </iframe>
												<%}else{ %>
													<img class="img-maxwidth" src="data:image/<%=FilenameUtils.getExtension(projectdatadetails.get(z)[3].toString()) %>;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(systemfile))%>"  id="config<%=ProjectDetail.get(z)[0] %>"  > 											
												<%} %>
                                              <%} %>
											<%}else{ %>
												2 (a) System Configuration. &nbsp; File Not Found
											<%} %>
										
										
										</td>
											
									</tr>
								</table>
							
							</div>
							<div align="left" class="margin-left15">
							<table >
								<tr>
									<td class="border-0"> 
										<%if(projectdatadetails.get(z)!=null && projectdatadetails.get(z)[4]!=null){ %>
											<form action="ProjectDataSystemSpecsFileDownload.htm"  method="post" target="_blank" >	
															
												2 (b) System Specifications. &nbsp; <span class="anchorlink" onclick="$('#sysspecs<%=ProjectDetail.get(z)[0] %>').toggle();" ><b>As on File Attached</b></span>
												<button  type="submit" class="btn btn-sm "  ><i class="fa fa-download fa-lg" ></i></button>
												<input type="hidden" name="projectdataid" value="<%=projectdatadetails.get(z)[0]%>"/>
												<input type="hidden" name="filename" value="sysspecs"/>
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											</form>
											<%
											Path specificPath = Paths.get(filePath,projectLabCode,"ProjectData",projectdatadetails.get(z)[4].toString());
											File specificfile = specificPath.toFile();
											if(specificfile.exists()){
											if(FilenameUtils.getExtension(projectdatadetails.get(z)[4].toString()).equalsIgnoreCase("pdf")){ %>
												<iframe	width="1200" height="600" src="data:application/pdf;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(specificfile))%>"  id="sysspecs<%=ProjectDetail.get(z)[0] %>" class="display-none" > </iframe>
											<%}else{ %>
												<img class="img-maxwidth" src="data:image/<%=FilenameUtils.getExtension(projectdatadetails.get(z)[4].toString()) %>;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(specificfile))%>"  id="sysspecs<%=ProjectDetail.get(z)[0] %>"  > 											
											<%} %>
										   <%} %>
										<%}else{ %>
											2 (b) System Specifications. &nbsp; File Not Found
										<%} %>
									
									
									
									</td>
									<td class="border-0">  
									
									</td>
								</tr>
							</table>
							</div>
							<%} %>
							</div> --%>
						</details>
				
<!--  ---------------------------------------------------------------------------------------------------------------------------------------------  -->
				 			
						<details>
	   						<summary role="button" class="no-arrow" tabindex="0"><b>3. Overall Product tree/WBS</b> </summary>
							
							<%-- <%for(int z=0;z<1;z++){ %>
							<div>
								<%if(ProjectDetail.size()>1){ %>
									<div class="margin-left">
										<b>Project : <%=ProjectDetail.get(z)[1] %> 	<%if(z!=0){ %>(SUB)<%} %>	</b>
									</div>	
								<%} %>
								<table>
									<tr>
										<td class="border-0 padding-left-rem "> 
											<%if(projectdatadetails.get(z)!=null && projectdatadetails.get(z)[5]!=null){ %>
											
												<form action="ProjectDataSystemSpecsFileDownload.htm"  method="post" target="_blank" >	
													Overall Product tree/WBS &nbsp; :  &nbsp;<span class="anchorlink" onclick="$('#protree<%=ProjectDetail.get(z)[0] %>').toggle();" ><b>As on File Attached</b></span>	
													<button  type="submit" class="btn btn-sm "  ><i class="fa fa-download fa-lg" ></i></button>
													<input type="hidden" name="projectdataid" value="<%=projectdatadetails.get(z)[0]%>"/>
													<input type="hidden" name="filename" value="protree"/>
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
												</form>	
												
												
												<%
												Path productTreePath = Paths.get(filePath,projectLabCode,"ProjectData",projectdatadetails.get(z)[5].toString());
												File productTreeFile = productTreePath.toFile();
												if(productTreeFile.exists()){
												if(FilenameUtils.getExtension(projectdatadetails.get(z)[5].toString()).equalsIgnoreCase("pdf")){ %>
													<iframe	width="1200" height="600" src="data:application/pdf;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(productTreeFile))%>"  id="protree<%=ProjectDetail.get(z)[0] %>" class="display-none" > </iframe>
												<%}else{ %>
													<img class="img-maxwidth" src="data:image/<%=FilenameUtils.getExtension(projectdatadetails.get(z)[5].toString()) %>;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(productTreeFile))%>"  id="protree<%=ProjectDetail.get(z)[0] %>"  > 											
												<%} %>
											  <%} %>
											<%}else{ %>
												Overall Product tree/WBS &nbsp; File Not Found
											<%} %>
										
										</td>
										<td class="border-0">  
											
										</td>
									</tr>
								</table>
							</div>
							<%} %> --%>
						</details>
  				
<!--  ---------------------------------------------------------------------------------------------------------------------------------------------  -->		
				
						<details>
   						<summary role="button" class="no-arrow" tabindex="0"><b>4. Particulars of Meeting </b> </summary>
   						
   						<%-- <div class="content">
   							<%for(int z=0;z<1;z++){ %>
   								<h1 class="break"></h1>
   								
   								  	<%if(ProjectDetail.size()>1){ %>
										<div>
											<b>Project : <%=ProjectDetail.get(z)[1] %> 	<%if(z!=0){ %>(SUB)<%} %>	</b>
										</div>	
									<%} %>	
								   <div align="left" class="margin-left15">(a) <%if(committee.getCommitteeShortName().trim().equalsIgnoreCase("PMRC") || committee.getCommitteeShortName().trim().equalsIgnoreCase("PMRB")){ %>
															   						Approval 
															   						<%}else { %>
															   						Ratification
															   						<%} %>  of <b>recommendations</b> of last PMRB / <%=committee.getCommitteeShortName().trim().toUpperCase() %> Meeting (if any)</div>
															   						
							
			<table class="subtables table-subtables" >
				<thead>
					<tr>
						<td colspan="6" class="border-0">
							<p class="font-size10 text-center"> 
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
							</p>
						</td>									
					</tr>
										
					<tr>
						<th  class="width15 text-center">SN</th>
						<th  class="width100"> ID</th>
						<th  class="width315">Recommendation Point</th>
						<th  class="width100">ADC<br> PDC</th>
						<th  class="width210"> Responsibility</th>
						<th  class="width80">Status(DD)</th>
						<th  class="width250">Remarks</th>
					</tr>
				</thead>
				<tbody>
					<%if(lastpmrcminsactlist.get(z).size()==0){ %>
						<tr><td colspan="7" class="text-center" > Nil</td></tr>
					<%}
						else if(lastpmrcminsactlist.get(z).size()>0)
							{int i=1;String key2="";
								for(Object[] obj:lastpmrcminsactlist.get(z)){
									// only recommendations and the if recommendation is completed or closed then only those actions which are completed after last meeting
									if( obj[3].toString().equalsIgnoreCase("R") && (obj[10]==null || !obj[10].toString().equals("C") || (obj[10].toString().equals("C") && obj[14]!=null && before6months.isBefore(LocalDate.parse(obj[14].toString()) ) )) ){ %>
						<tr>
							<td class="text-center"><%=i %></td>
							<td>
							
							
									<%if(obj[21]!=null && Long.parseLong(obj[21].toString())>0){ %>
								
									
			 						<span class="font-weight-bold">	
								<%for (Map.Entry<Integer, String> entry : committeeWiseMap.entrySet()) {
									Date date = inputFormat.parse(obj[5].toString().split("/")[3]);
									 String formattedDate = outputFormat.format(date);
									 if(entry.getValue().equalsIgnoreCase(formattedDate)){
										 key2=entry.getKey().toString();
									 } }
									
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
									 
									 %>
								
								<%=splCommittee.trim().toUpperCase()+"-"+key2+"/"+obj[5].toString().split("/")[4] %>
								
								
								</span>	
									
									
								<%}%>
							</td>
							

							<td class="text-justify "><%=  obj[2].toString() %></td>


							<td class="text-center">
								<%if(obj[8]!= null && !LocalDate.parse(obj[8].toString()).equals(LocalDate.parse(obj[7].toString())) ){ %><span class="pencil-icon font-weight-bold"><%=sdf.format(sdf1.parse(obj[8].toString()))%></span><br><%} %>	
								<%if(obj[7]!= null && !LocalDate.parse(obj[7].toString()).equals(LocalDate.parse(obj[6].toString())) ){ %><span class="pencil-icon font-weight-bold"><%=sdf.format(sdf1.parse(obj[7].toString()))%></span><br><%} %>
								<%if(obj[6]!= null){ %><span><%=sdf.format(sdf1.parse(obj[6].toString()))%></span><br><%} %>
								</td>
							<td>
								<%if(obj[4]!= null){ %>  
									<%=obj[12] %>, <%=obj[13] %>
								<%}else { %><span class="">Not Assigned</span> <%} %> 
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
							<td ><%if(obj[19]!=null){%><%= (obj[19].toString()) %><%} %></td>
						</tr>		
					<%i++;}
						}%>
					<%if(i==1){ %> <tr><td colspan="6" class="text-center" > Nil</td></tr>	<%} %>
											
					<%} %>
				</tbody>
										
			</table>
				
							
							
		 <%if((Double.parseDouble(projectattributeslist.get(0)[7].toString())*100000)>1){ %>
								  
		  	<div align="left" class="margin-left15">(b) Last <%=committee.getCommitteeShortName().trim().toUpperCase() %>
															   						Meeting action points with Probable Date of completion (PDC), Actual Date of Completion (ADC) and current status.</div>
					
					<table class="subtables table-subtables" >
						<thead>
							<tr>
								<td colspan="7" class="border-0">
									<p class="font-size10 text-center"> 
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
									</p>
								</td>									
							</tr>
										
							<tr>
								<th  class="width15 text-center">SN</th>
								<th  class="width120 text-center">ID</th>
								<th  class="width320">Action Point</th>
								<th  class="width150">ADC<br>PDC</th>
								<th  class="width210"> Responsibility</th>
								<th  class="width80">Status(DD)</th>
								<th  class="width205">Remarks</th>			
							</tr>
						</thead>
							
						<tbody>		
							<%if(lastpmrcactions.get(z).size()==0){ %>
								<tr><td colspan="7"  class="text-center" > Nil</td></tr>
								<%}
								else if(lastpmrcactions.size()>0)
								{
									Map<String,List<Object[]>> list = lastpmrcactions.get(z)!=null ? lastpmrcactions.get(z).stream()
										.collect(Collectors.groupingBy(array -> array[0].toString(), LinkedHashMap::new,Collectors.toList())) : new HashMap<>();
							int i = 1;String key="";
							for(Map.Entry<String, List<Object[]>> map : list.entrySet()){
								int j=1;
								List<Object[]> values = map.getValue();
								int rowSpan = values.size();
							for (Object[] obj : values) { %>
								<tr>
									<td  class="text-center"><%=i %></td>
									<td>
										<%
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
								<span class="font-weight-bold">
								<%for (Map.Entry<Integer, String> entry : committeeWiseMap.entrySet()) {
									String actionNo = obj[1].toString();
									Date date = inputFormat.parse(obj[1].toString().split("/")[3]);
									 String formattedDate = outputFormat.format(date);
									 if(entry.getValue().equalsIgnoreCase(formattedDate)){
										 key=entry.getKey().toString();
									 } }%>
								
								<%=splCommittee+"-"+key+"/"+obj[1].toString().split("/")[4] %>
								</span> 
								<%}%>
									</td>
									<%if(j++==1){ %><td rowspan="<%=rowSpan%>" class="text-justify"> <%=obj[2].toString()%> </td> <%} %>
									<td class="text-center">
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
									<span <%if(endPdc.isAfter(today) || endPdc.isEqual(today)) {%>class="pencil-icon font-weight-bold" <%} else{%> class="color-maroon font-weight-bold" <%} %>>
									<%=sdf.format(sdf1.parse(endPdc.toString())) %>
									</span>	
									<%if(!pdcorg.equals(endPdc)) {%>
									<br>
									<span <%if(pdcorg.isAfter(today) || pdcorg.isEqual(today)) {%>class="pencil-icon font-weight-bold" <%} else{%> class="color-maroon font-weight-bold" <%} %>>
									<%=sdf.format(sdf1.parse(pdcorg.toString())) %>
									</span>	
									<%} %>
								</td>	
									<td> 
										<%=obj[11] %>, <%=obj[12] %> 
										</td>
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
									<td class="text-justify"><%if(obj[16]!=null){%><%= (obj[16].toString()) %><%} %></td>			
								</tr>			
							<%i++;
							}}} %>
							</tbody>
									
						</table> 
								
					<%} %>
								
			<div align="left" class="margin-left15">(c) Details of Technical/ User Reviews (if any).</div>
						
							
						<div align="center" class="width1100">

							<form action="CommitteeMinutesNewDownload.htm" method="get" target="_blank">
							<div class="row">
							<%for(Map.Entry<String, List<Object[]>> entry : reviewMeetingListMap.entrySet()) { 
								if(entry.getValue().size()>0) { %>
									<div class="col-md-4 mt-2">
										<table class="subtables table-subtables-copy">
											<thead>
												<tr>
													<th  class="width140">Committee</th>
													<th  class="width140"> Date Held</th>
												</tr>
											</thead>
											<tbody>
												<%int i=0;
												for(Object[] obj : entry.getValue()){ %>
													<tr>
														<td >
															<button class="btn btn-link p-0 m-0"  name="committeescheduleid" value="<%=obj[0]%>"> <%=obj[1]%> #<%=++i %></button>
														</td>												
														<td class="text-center" ><%= fc.sdfTordf(obj[3].toString())%></td>
													</tr>				
												<%} %>
											</tbody>
										</table>
									</div>
								<%} %>	
							<%} %>
							</div>
						</form>
						
									<%if(otherMeetingList!=null && otherMeetingList.size()>0) {
					int count=0;
				%>
				<div align="left" class="mb-2 ml-4"><b><%="Other Meetings" %></b></div>
						<div align="left" class="mb-2"><table class="subtables tbl-sub" >
						<thead><tr> <th class="width140">Committee</th> <th  class="width140"> Date Held</th></tr></thead>
				<%for(Object[]obj:otherMeetingList) {%>
				
											<tbody>
									<tr><td><button class="btn btn-link p-0 m-0"  name="committeescheduleid" value="<%=obj[0]%>"><%=obj[3]%> </button>
														</td>												
														<td  class="text-center" ><%= sdf.format(sdf1.parse(obj[1].toString()))%></td>
													</tr>
									</tbody>
				
				<%}%></table></div> <%} %>
						</div>
															
					<%} %> --%>
					 
				</details>
				
<!--  ---------------------------------------------------------------------------------------------------------------------------------------------  -->				
				 
						<details>
   						<summary role="button" class="no-arrow" tabindex="0"><b>5. Milestones achieved prior to this <%=committee.getCommitteeShortName().trim().toUpperCase() %> period.</b>  </summary>
							<%-- <div class="content">
				
								<%for(int z=0;z<1;z++){ %>
									<%if(ProjectDetail.size()>1){ %>
										<div>
											<b>Project : <%=ProjectDetail.get(z)[1] %> 	<%if(z!=0){ %>(SUB)<%} %>	</b>
										</div>	
									<%} %>	
				
							<table  class="subtables table-subtables" >
								<thead>
								<tr>
									<td colspan="10" class="border-0">
										<p class="font-size10 text-center"> 
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
										 </p>
									</td>									
								</tr>
								
									<tr>
										<th  class="width20">SN</th>
										<th  class="width30">MS</th>
										<th  class="width60">L</th>
										<th  class="width350">System/ Subsystem/ Activities</th>
										<th class="width120" >Start Date</th>
										<th  class="width120">ADC<br> PDC</th>
										<th  class="width60"> Progress</th>
										<th  class="width50"> Status(DD)</th>
									 	<th  class="width26"> Remarks</th>
									 	<th  class="max-width30"> Info </th>
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
											
											if(Integer.parseInt(obj[21].toString())<= Integer.parseInt(levelid)){
											%>
											<tr class="<% if("0".equals(obj[21].toString()) && "Y".equalsIgnoreCase(obj[31].toString())){ %> bg-sunset <%}%>">
												<td class="text-center"><%=serial%></td>
												<td>M<%=obj[0] %></td>
												
												<td class="text-center">
													<%
													
													if(obj[21].toString().equals("0")) {%>
														<!-- L -->
													<%	milcountA=1;
														milcountB=1;
														milcountC=1;
														milcountD=1;
														milcountE=1;
													}else if(obj[21].toString().equals("1")) {
														for(Map.Entry<Integer,String>entry:treeMapLevOne.entrySet()){
															if(entry.getKey().toString().equalsIgnoreCase(obj[2].toString())){%>
																<%=entry.getValue() %>
														<%}} 
														%>
													<%
													}else if(obj[21].toString().equals("2")) {
														for(Map.Entry<Integer,String>entry:treeMapLevTwo.entrySet()){
															if(entry.getKey().toString().equalsIgnoreCase(obj[3].toString())){%>
																<%=entry.getValue() %>
														<%}}	
														%>
													<%
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
	
												<td class="<%if(obj[21].toString().equals("0")) {%>font-weight-bold<%}%>">
													<%if(obj[21].toString().equals("0")) {%>
														<%=(obj[10].toString()) %>
													<%}else if(obj[21].toString().equals("1")) { %>
														&nbsp;&nbsp;<%=(obj[11].toString()) %>
													<%}else if(obj[21].toString().equals("2")) { %>
														&nbsp;&nbsp;<%=(obj[12].toString()) %>
													<%}else if(obj[21].toString().equals("3")) { %>
														&nbsp;&nbsp;<%=(obj[13].toString()) %>
													<%}else if(obj[21].toString().equals("4")) { %>
														&nbsp;&nbsp;<%=(obj[14].toString()) %>
													<%}else if(obj[21].toString().equals("5")) { %>
														&nbsp;&nbsp;<%=(obj[15].toString()) %>
													<%} %>
												</td>
												<% 
													LocalDate StartDate = LocalDate.parse(obj[7].toString());
													LocalDate EndDate = LocalDate.parse(obj[8].toString());
													LocalDate OrgEndDate = LocalDate.parse(obj[9].toString());
													int Progess = Integer.parseInt(obj[17].toString());
													LocalDate CompletionDate =obj[24]!=null ? LocalDate.parse(obj[24].toString()) : null;
													
													LocalDate Today = LocalDate.now();
													
												%>
												<td class="text-center" ><%= obj[7]!=null? sdf.format(sdf1.parse(obj[7].toString())) : " - " %></td>
												<td class="text-center">
												<!-- ADC  -->
												
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
														<%= sdf.format(sdf1.parse(obj[8].toString()))%><br> 
													<%}%>
													<%=sdf.format(sdf1.parse(obj[9].toString())) %>
												</td>
												

												<td class="text-center"><%=obj[17] %>%</td>											

												
												<td class="text-center">	
								
														<%if(Progess==0){ %>
															<span class="assigned"> AA </span>
														<%} else if(Progess>0 && Progess<100 && (OrgEndDate.isAfter(Today) || OrgEndDate.isEqual(Today) )){ %>
															<span class="ongoing"> OG </span>
														<%} else if( Progess>0 && Progess<100 && (OrgEndDate.isBefore(Today) )){ %>
															<span class="delay"> DO (<%=ChronoUnit.DAYS.between(OrgEndDate, LocalDate.now())%>)</span>
														<%} else if((CompletionDate!=null && ( CompletionDate.isBefore(OrgEndDate) ||  CompletionDate.isEqual(OrgEndDate)))){ %>
															<span class="completed"> CO</span>
														<%} else if((CompletionDate!=null && CompletionDate.isAfter(OrgEndDate) )){ %>
															<span class="completeddelay">CD (<%=ChronoUnit.DAYS.between(OrgEndDate, CompletionDate)%>)</span>
														<%}else if(CompletionDate!=null && Progess==0 &&  ( EndDate.isAfter(Today) ||  EndDate.isEqual(Today)) ){ %>
															<span class="inactive">IA</span>
														<%}else{ %>
															<span class="assigned">AA</span>
														<%} %>
													
												</td>
												<td class="overflowWrap"><%if(obj[23]!=null){%><%=(obj[23].toString()) %><%} %></td>

	                                            <td >
													<a  data-toggle="modal" data-target="#exampleModal1" data-id="milestonemodal<%=obj[0] %>" class="milestonemodal m-modal" data-whatever="@mdo" >
														<i class="fa fa-info-circle circle-font"  aria-hidden="true"></i> 
													</a>
												</td>
											</tr>
										<%count1++;serial++;}} %>
									<%} else{ %>
									<tr><td colspan="10" class="text-center" > Nil</td></tr>
									
									
									<%} %>
							</table>
			
			
								<div id="milestoneactivitychange" ></div>
								
							<%} %>
						</div> --%>
				</details>
<!--  -------------------------------------------------- Review of SunSet Milestone   -------------------------------------------------------------------------------------------  -->
 				
 				<details>
 				<summary role="button" class="no-arrow" tabindex="0" id="leveltab"><b>6. Review of Sunset milestone.</b>  </summary>
 					<%-- <div class="content">
				
								<%for(int z=0;z<1;z++){ %>
									<%if(ProjectDetail.size()>1){ %>
										<div>
											<b>Project : <%=ProjectDetail.get(z)[1] %> 	<%if(z!=0){ %>(SUB)<%} %>	</b>
										</div>	
									<%} %>	
				
							<table  class="subtables table-subtables" >
								<thead>
								<tr>
									<td colspan="10" class="border-0">
										<p class="font-size10 text-center"> 
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
										 </p>
									</td>									
								</tr>
								
									<tr>
										<th  class="width20">SN</th>
										<th  class="width30">MS</th>
										<th  class="width60">L</th>
										<th  class="width350">System/ Subsystem/ Activities</th>
										<th class="width120" >Start Date</th>
										<th  class="width120">ADC<br> PDC</th>
										<th  class="width60"> Progress</th>
										<th  class="width50"> Status(DD)</th>
									 	<th  class="width26"> Remarks</th>
									 	<th  class="max-width30"> Info </th>
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
											
											if(Integer.parseInt(obj[21].toString())<= Integer.parseInt(levelid)){
											%>
											<tr class="<% if( "0".equals(obj[21].toString()) && "Y".equalsIgnoreCase(obj[31].toString())){ %> bg-sunset <%}%>">
												<td class="text-center"><%=serial%></td>
												<td>M<%=obj[0] %></td>
												
												<td class="text-center">
													<%
													
													if(obj[21].toString().equals("0")) {%>
														<!-- L -->
													<%	milcountA=1;
														milcountB=1;
														milcountC=1;
														milcountD=1;
														milcountE=1;
													}else if(obj[21].toString().equals("1")) {
														for(Map.Entry<Integer,String>entry:treeMapLevOne.entrySet()){
															if(entry.getKey().toString().equalsIgnoreCase(obj[2].toString())){%>
																<%=entry.getValue() %>
														<%}} 
														%>
													<%
													}else if(obj[21].toString().equals("2")) {
														for(Map.Entry<Integer,String>entry:treeMapLevTwo.entrySet()){
															if(entry.getKey().toString().equalsIgnoreCase(obj[3].toString())){%>
																<%=entry.getValue() %>
														<%}}	
														%>
													<%
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
	
												<td class="<%if(obj[21].toString().equals("0")) {%>font-weight-bold<%}%>">
													<%if(obj[21].toString().equals("0")) {%>
														<%=StringEscapeUtils.escapeHtml4(obj[10].toString()) %>
													<%}else if(obj[21].toString().equals("1")) { %>
														&nbsp;&nbsp;<%=StringEscapeUtils.escapeHtml4(obj[11].toString()) %>
													<%}else if(obj[21].toString().equals("2")) { %>
														&nbsp;&nbsp;<%=StringEscapeUtils.escapeHtml4(obj[12].toString()) %>
													<%}else if(obj[21].toString().equals("3")) { %>
														&nbsp;&nbsp;<%=StringEscapeUtils.escapeHtml4(obj[13].toString()) %>
													<%}else if(obj[21].toString().equals("4")) { %>
														&nbsp;&nbsp;<%=StringEscapeUtils.escapeHtml4(obj[14].toString()) %>
													<%}else if(obj[21].toString().equals("5")) { %>
														&nbsp;&nbsp;<%=StringEscapeUtils.escapeHtml4(obj[15].toString()) %>
													<%} %>
												</td>
												<% 
													LocalDate StartDate = LocalDate.parse(obj[7].toString());
													LocalDate EndDate = LocalDate.parse(obj[8].toString());
													LocalDate OrgEndDate = LocalDate.parse(obj[9].toString());
													int Progess = Integer.parseInt(obj[17].toString());
													LocalDate CompletionDate =obj[24]!=null ? LocalDate.parse(obj[24].toString()) : null;
													
													LocalDate Today = LocalDate.now();
													
												%>
												<td class="text-center" ><%= obj[7]!=null? sdf.format(sdf1.parse(obj[7].toString())) : " - " %></td>
												<td class="text-center">
												<!-- ADC  -->
												
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
														<%= sdf.format(sdf1.parse(obj[8].toString()))%><br> 
													<%}%>
													<%=sdf.format(sdf1.parse(obj[9].toString())) %>
												</td>
												

												<td class="text-center"><%=obj[17] %>%</td>											

												
												<td class="text-center">	
								
														<%if(Progess==0){ %>
															<span class="assigned"> AA </span>
														<%} else if(Progess>0 && Progess<100 && (OrgEndDate.isAfter(Today) || OrgEndDate.isEqual(Today) )){ %>
															<span class="ongoing"> OG </span>
														<%} else if( Progess>0 && Progess<100 && (OrgEndDate.isBefore(Today) )){ %>
															<span class="delay"> DO (<%=ChronoUnit.DAYS.between(OrgEndDate, LocalDate.now())%>)</span>
														<%} else if((CompletionDate!=null && ( CompletionDate.isBefore(OrgEndDate) ||  CompletionDate.isEqual(OrgEndDate)))){ %>
															<span class="completed"> CO</span>
														<%} else if((CompletionDate!=null && CompletionDate.isAfter(OrgEndDate) )){ %>
															<span class="completeddelay">CD (<%=ChronoUnit.DAYS.between(OrgEndDate, CompletionDate)%>)</span>
														<%}else if(CompletionDate!=null && Progess==0 &&  ( EndDate.isAfter(Today) ||  EndDate.isEqual(Today)) ){ %>
															<span class="inactive">IA</span>
														<%}else{ %>
															<span class="assigned">AA</span>
														<%} %>
													
												</td>
												<td class="overflowWrap"><%if(obj[23]!=null){%><%=StringEscapeUtils.escapeHtml4(obj[23].toString()) %><%} %></td>
	                                            <td >
													<a  data-toggle="modal" data-target="#exampleModal1" data-id="milestonemodal<%=obj[0] %>" class="milestonemodal m-modal" data-whatever="@mdo" >
														<i class="fa fa-info-circle circle-font"  aria-hidden="true"></i> 
													</a>
												</td>
											</tr>
										<%count1++;serial++;}} %>
									<%} else{ %>
									<tr><td colspan="10" class="text-center" > Nil</td></tr>
									
									
									<%} %>
							</table>
			
			
								<div id="milestoneactivitychange" ></div>
								
							<%} %>
						</div> --%>
 				
 				</details>
 				
<!--  ---------------------------------------------------------------------------------------------------------------------------------------------  -->
				 	
						<details>
   						<summary role="button" class="no-arrow" tabindex="0" id="leveltab"><b>7. Details of work and current status of sub system with major milestones (since last <%=committee.getCommitteeShortName().trim().toUpperCase()%>)</b>  </summary>
						<%-- <div class="content">
							
							<%for(int z=0;z<1;z++){ %>
								<%if(ProjectDetail.size()>1){ %>
									<div>
										<b>Project : <%=ProjectDetail.get(z)[1] %> 	<%if(z!=0){ %>(SUB)<%} %>	</b>
									</div>	
								<%} %>	
								<div align="left" class="margin-left15">(a) Work carried out, Achievements, test result etc.
									   <%if(z==0){ %>
										<form action="FilterMilestone.htm" method="POST">  
											<button class="btn btn-sm back filter-st"  formtarget="blank"> Filter</button> 
											<input type="hidden" name="projectidvalue" <%if( projectid!=null ){%> value="<%=projectid%>" <%}%>>
											<input type="hidden" name="committeidvalue" <%if(committeeid!=null){%> value="<%=committeeid %>" <%}%>>
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										</form>
										<%}%> 
								</div>
								<div align="left" class="margin-left20"><b>Present Status:</b>
								
								</div>
								
								
								
			<!-- Tharun code Start (For Filtering Milestone based on levels) -->		
			
			
						<table  class="subtables table-subtables" >
							<thead>
							<tr>
								<td colspan="10" class="border-0">
									<p class="font-size10 text-center"> 
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
									 </p>
								</td>									
							</tr>
							
							<tr>
								<th  class="width20">SN</th>
								<th  class="width30">MS</th>
								<th  class="width60">L</th>
								<th  class="width350">System/ Subsystem/ Activities</th>
								<th class="width120" >Start Date</th>
								<th  class="width150"> PDC</th>
								<th  class="width60"> Progress</th>
								<th  class="width50"> Status(DD)</th>
							 	<th  class="width260"> Remarks</th>
							</tr>
							</thead>
								<% if( MilestoneDetails6.get(z).size()>0) { 
									long count1=1;
									int milcountA=1;
									int milcountB=1;
									int milcountC=1;
									int milcountD=1;
									int milcountE=1;
									
									%>
									<%int serial=1;for(Object[] obj:MilestoneDetails6.get(z)){
										
										if(Integer.parseInt(obj[21].toString())<= Integer.parseInt(levelid)){
										%>
										<tr>
											<td class="text-center"><%=serial%></td>
											<td>M<%=obj[0] %></td>
											
											<td class="text-center">
												<%
												
												if(obj[21].toString().equals("0")) {%>
													<!-- L -->
												<%	milcountA=1;
													milcountB=1;
													milcountC=1;
													milcountD=1;
													milcountE=1;
												}else if(obj[21].toString().equals("1")) {
													for(Map.Entry<Integer,String>entry:treeMapLevOne.entrySet()){
														if(entry.getKey().toString().equalsIgnoreCase(obj[2].toString())){%>
															<%=entry.getValue() %>
													<%}}	
													
													%>
												<% 
												}else if(obj[21].toString().equals("2")) { 
													for(Map.Entry<Integer,String>entry:treeMapLevTwo.entrySet()){
														if(entry.getKey().toString().equalsIgnoreCase(obj[3].toString())){%>
															<%=entry.getValue() %>
													<%}}	
												%>
												<%
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
											<td class="<%if(obj[21].toString().equals("0")) {%>font-weight-bold<%}%>">
													<%if(obj[21].toString().equals("0")) {%>
														<%=(obj[10].toString()) %>
													<%}else if(obj[21].toString().equals("1")) { %>
														&nbsp;&nbsp;<%=(obj[11].toString()) %>
													<%}else if(obj[21].toString().equals("2")) { %>
														&nbsp;&nbsp;<%=(obj[12].toString()) %>
													<%}else if(obj[21].toString().equals("3")) { %>
														&nbsp;&nbsp;<%=(obj[13].toString()) %>
													<%}else if(obj[21].toString().equals("4")) { %>
														&nbsp;&nbsp;<%=(obj[14].toString()) %>
													<%}else if(obj[21].toString().equals("5")) { %>
														&nbsp;&nbsp;<%=(obj[15].toString()) %>
													<%} %>
												</td>
												<td class="text-center" ><%= obj[7]!=null? sdf.format(sdf1.parse(obj[7].toString())) : " - " %></td>
											<td class="text-align-center">
												<%if(! LocalDate.parse(obj[8].toString()).isEqual(LocalDate.parse(obj[9].toString())) ){ %> 
													<%= sdf.format(sdf1.parse(obj[8].toString()))%><br> 
												<%}%>
												<%=sdf.format(sdf1.parse(obj[9].toString())) %>
											</td>
											<% 
												LocalDate StartDate = LocalDate.parse(obj[7].toString());
												LocalDate EndDate = LocalDate.parse(obj[8].toString());
												LocalDate OrgEndDate = LocalDate.parse(obj[9].toString());
												int Progess = Integer.parseInt(obj[17].toString());
												LocalDate CompletionDate =obj[24]!=null ? LocalDate.parse(obj[24].toString()) : null;
												LocalDate Today = LocalDate.now();
											%>
											<td class="text-center"><%=obj[17] %>%</td>											
											<td class="text-center">

												
												
												<%if(Progess==0){ %>
													<span class="assigned"> AA </span>
												<%} else if(Progess>0 && Progess<100 && (OrgEndDate.isAfter(Today) || OrgEndDate.isEqual(Today) )){ %>
													<span class="ongoing"> OG </span>
												<%} else if( Progess>0 && Progess<100 && (OrgEndDate.isBefore(Today) )){ %>
													<span class="delay"> DO (<%=ChronoUnit.DAYS.between(OrgEndDate, LocalDate.now())%>)</span>
												<%} else if((CompletionDate!=null && ( CompletionDate.isBefore(OrgEndDate) ||  CompletionDate.isEqual(OrgEndDate)))){ %>
													<span class="completed"> CO</span>
												<%} else if((CompletionDate!=null && CompletionDate.isAfter(OrgEndDate) )){ %>
													<span class="completeddelay">CD (<%=ChronoUnit.DAYS.between(OrgEndDate, CompletionDate)%>)</span>
												<%}else if(CompletionDate!=null && Progess==0 &&  ( EndDate.isAfter(Today) ||  EndDate.isEqual(Today)) ){ %>
													<span class="inactive">IA</span>
												<%}else{ %>
													<span class="assigned">AA</span>
												<%} %>
												
											</td>
											<td class="overflowWrap"><%if(obj[23]!=null){%><%=(obj[23].toString()) %><%} %></td>
										</tr>
									<%count1++;serial++;}} %>
								<%} else{ %>
										<tr><td colspan="9" class="text-center"> Nil</td></tr>
								
								
								<%} %>
							</table>

								<!--  Commenting Old Data End-->
							
								<div align="left" class="margin-left15">(b) TRL table with TRL at sanction stage and current stage indicating overall PRI.</div>
									
								<div>
									<table  >
										<tr><td class="border-0"></td>
											<td class="border-0">  
											<%if(projectdatadetails.get(z)!=null && projectdatadetails.get(z)[6]!=null ){ %>
												<form action="ProjectDataSystemSpecsFileDownload.htm"  method="post" target="_blank" >	
													<span class="anchorlink" onclick="$('#pearl<%=ProjectDetail.get(z)[0] %>').toggle();" ><b>As on File Attached</b></span>
													<button  type="submit" class="btn btn-sm "  ><i class="fa fa-download fa-lg" ></i></button>
													<input type="hidden" name="projectdataid" value="<%=projectdatadetails.get(z)[0]%>"/>
													<input type="hidden" name="filename" value="pearl"/>
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
												</form>	
												
												<%
												Path trlPath = Paths.get(filePath,projectLabCode,"ProjectData",projectdatadetails.get(z)[6].toString());
												File trlfile = trlPath.toFile();
												if(trlfile.exists()){
												if(FilenameUtils.getExtension(projectdatadetails.get(z)[6].toString()).equalsIgnoreCase("pdf")){ %>
													<iframe	width="1200" height="600" src="data:application/pdf;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(trlfile))%>"  id="pearl<%=ProjectDetail.get(z)[0] %>" class="display-none" > </iframe>
												<%}else{
													%>
													<img class="img-maxwidth" src="data:image/<%=FilenameUtils.getExtension(projectdatadetails.get(z)[6].toString()) %>;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(trlfile))%>"  id="pearl<%=ProjectDetail.get(z)[0] %>"  >											
												  <%} %>
												<%} %>
											<% }else{ %>
												File Not Found
											<%} %>
										</td>
										</tr>
									</table>
								</div>
								<div align="left" class="margin-left15">(c) Risk Matrix/Management Plan/Status. </div>
									
									<table class="subtables table-subtables">
										<thead>	
												<tr>
													<td colspan="9" class="border-0">
														<p class="font-size10 text-center"> 
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
														</p>
									   				</td>									
												</tr>
												<tr>
													<td colspan="9" class="border-0 text-right"><b>RPN :</b>Risk Priority Number</td>
												</tr>
												<tr>
													<th class="width15 text-center " rowspan="2">SN</th>
													<th class="width330 " colspan="3">
														Risk
														<a data-toggle="modal" class="fa faa-pulse animated m-modal" data-target="#RiskTypesModal" data-whatever="@mdo" ><i class="fa fa-info-circle circle-font"  aria-hidden="true"></i> </a>
													</th>
													<th class="width100" rowspan="1" > ADC <br>PDC</th>
													<th class="width160" rowspan="1"> Responsibility</th>
													<th class="width50"  rowspan="1">Status(DD)</th>
													<th class="width215" rowspan="1">Remarks</th>	
												</tr>
												<tr>
													<th  class="text-center width110 " > Severity<br>(1-10)</th>
													<th  class="text-center width110"> Probability<br>(1-10)</th>
													<th  class="text-center width110"> RPN<br>(1-100)</th>
													<th  class="width210" colspan="3" > Mitigation Plans</th>
													<th  class="width315" colspan="2"> Impact</th>		
												</tr>
															
										</thead>
																							
										<tbody>
											<%if(riskmatirxdata.get(z).size()>0){
												int i=0;%> 
													<%for(Object[] obj : riskmatirxdata.get(z)){
													i++;%>
														<tr>
															<td class="text-center" rowspan="2"><%=i %></td>
															<td class="text-justify text-danger" colspan="3" >
																<%=obj[0] %> <span class="color-c font-weight-bold"> - <%=obj[23] %><%=obj[24]%></span>
															</td>
															<td class="text-center" rowspan="1">
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
															
															
																<%if(!pdcorg.equals(enddate)) {%>
																<%=sdf.format(sdf1.parse(obj[17].toString()))%>
																<%} %>
																
																<%=sdf.format(sdf1.parse(obj[9].toString()))%>
															</td>
															

																		
															<td rowspan="1"  ><%=obj[7] %>, <%=obj[8] %></td>	
															<td class="text-center" rowspan="1">
																	
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
																		<span class="assigned">
																			AA <%if(pdcorg.isBefore(today)){ %> (<%= ChronoUnit.DAYS.between(pdcorg, today)  %>) <%} %>
																		</span>
																	<%} else if(pdcorg.isAfter(today) || pdcorg.isEqual(today)){  %>
																		<span class="ongoing">OG</span>
																	<%}else if(pdcorg.isBefore(today)){  %>
																		<span class="delay">DO (<%= ChronoUnit.DAYS.between(pdcorg, today)  %>)  </span>
																	<%} %>					
																									
																<%} %>
																
																			
															</td>
															<td class="text-justify" rowspan="1"><%if(obj[19]!=null){ %> <%= (obj[19].toString()) %><%} %></td>
																
														</tr>	
														
																		
														<tr>
															<td class="text-center" ><%=obj[1]!=null?(obj[1].toString()):"" %></td>
															<td class="text-center" > <%=obj[2]!=null?(obj[2].toString()):"" %></td>
															<td class="text-center">
																<%=obj[22]%>
																<% int RPN =Integer.parseInt(obj[22].toString());
																		if(RPN>=1 && RPN<=25){ %>(Low)
																		<%}else if(RPN>=26 && RPN<=50){ %>(Medium)
																		<%}else if(RPN>=51 && RPN<=75){ %>(High)
																		<%}else if(RPN>=76){ %>(Very High)
																		<%} %>
															</td>
															<td class="text-justify" colspan="3" ><%=obj[3]!=null?(obj[3].toString()):"" %></td>
															<td class="text-justify" colspan="2" ><%=obj[21]!=null?(obj[21].toString()):"" %></td>
														</tr>
																	
														<%if(riskmatirxdata.get(z).size() > i){ %>
															<tr>
																<td colspan="9" class="color-transparent">.</td>
															</tr>
														<%} %>	
														<%}%>
													<%}else{%>
														<tr><td colspan="9"  class="text-center">Nil </td></tr>
													<%} %>
												</tbody>		
											</table>
 	 
									
								
								<% } %>
							</div>
							
									 --%>
						</details>
				
<!--  ---------------------------------------------------------------------------------------------------------------------------------------------  -->
				 
						<details>
   						<summary role="button" class="no-arrow" tabindex="0"><b>8. Details of Procurement</b>  </summary>
						<%-- <div class="content">
							<%for(int z=0;z<projectidlist.size();z++){ %>
								<%if(ProjectDetail.size()>1){ %>
									<div>
										<b>Project : <%=ProjectDetail.get(z)[1] %> 	<%if(z!=0){ %>(SUB)<%} %>	</b>
									</div>	
								<%} %>
								
								<div align="left" class="margin-left15"><b>(a) Details of Procurement Plan (Major Items)</b></div>
								<div align="right"> <span class="currency font-weight-bold"  >(In &#8377; Lakhs)</span></div>
			
								
								<table class="subtables tbl-sub-copy"  >
										<thead>
										<tr>
											<th colspan="11" class="text-right"> <span class="currency" >(In &#8377; Lakhs)</span></th>
										</tr>
										 <tr>
										 	<th colspan="11" class="std">Demand Details ( > &#8377; <% if (projectdatadetails.get(0) != null && projectdatadetails.get(0)[13] != null) { %>
													<%=projectdatadetails.get(0)[13].toString().replaceAll("\\.\\d+$", "")%> ) <% } else { %> - )<% } %>
												
											</th>
										</tr>
										</thead>
										
										<tr>
											<th class="std th-std" >SN</th>
											<th class="std th-std1" >Demand No<br>Demand Date</th>
 											<th class="std th-std2" colspan="4" > Nomenclature</th>
											<th class="std th-std1"> Est. Cost</th>
											<th class="std th-std3" > Status</th>
											<th class="std th-std4" colspan="3">Remarks</th>
										</tr>
										    <% int k=0;
										    if(procurementOnDemand.get(z)!=null &&  procurementOnDemand.get(z).size()>0){
										    Double estcost=0.0;
										    Double socost=0.0;
										    for(Object[] obj : procurementOnDemand.get(z)){ 
										    	k++; %>
											<tr>
												<td class="std border1px"  ><%=k%></td>
												<td class="std border1px" ><%= (obj[1].toString()) %><br><%=sdf.format(sdf1.parse(obj[3].toString()))%></td>
												<td class="std" colspan="4" ><%=obj[8]%></td>
												<td class="std text-right" > <%=format.format(new BigDecimal(obj[5].toString())).substring(1)%></td>
												<td class="std border1px" > <%=obj[10]%> </td>
												<td class="std border1px" colspan="3" ><%=obj[11]!=null?(obj[11].toString()):"" %> </td>		
											</tr>		
											<%
											estcost += Double.parseDouble(obj[5].toString());
										    }%>
										    
										    <tr>
										    	<td class="std text-right" colspan="8" ><b>Total</b></td>
										    	<td class="std text-right"><b><%=df.format(estcost)%></b></td>
										    	
										    	<td class="std text-right" colspan="2"></td>

										    </tr>
										    
										    
										    <% }else{%>											
												<tr><td colspan="11" class="std border1px text-center" >Nil </td></tr>
											<%} %>
											<!-- ********************************Future Demand Start *********************************** -->
											<tr>
											<th class="std border1px" colspan="11" ><span class="mainsubtitle">Future Demand</span></th>
											</tr>
											<tr>
												 <th class="std border1px width15 text-center">SN</th>
													 <th class="std width295 border1px"  colspan="4"> Nomenclature</th>
													 <th class="std border1px width80" > Est. Cost-Lakh &#8377;</th>
													 <th class="std border1px max-width50"> Status</th>
													 <th class="std border1px max-width310" colspan="4" >Remarks</th>
											</tr>
										
										    			    <% int a=0;
										    if(envisagedDemandlist!=null &&  envisagedDemandlist.size()>0){
										    Double estcost=0.0;
										    Double socost=0.0;
										    for(Object[] obj : envisagedDemandlist){ 
										    	a++; %>
											<tr>
												<td class="std border1px"  ><%=a%></td>
												<td class="std border1px" colspan="4" ><%=obj[3]%></td>
												<td class="std border1px text-right" > <%=format.format(new BigDecimal(obj[2].toString())).substring(1)%></td>
												<td class="std border1px"  > <%=obj[6]%> </td>
												<td class="std border1px" colspan="4" ><%= obj[4]!=null?(obj[4].toString()):"-" %> </td>		
											</tr>		
											<%
												estcost += Double.parseDouble(obj[2].toString());
										    }%>
										    
										    <tr>
										    	<td  class="std border1px text-right"colspan="7" ><b>Total</b></td>
										    	<td class="std border1px text-right" colspan="4"><b><%=df.format(estcost)%></b></td>
										    </tr>
										    
										    
										    <% }else{%>											
												<tr><td colspan="11"  class="std border1px text-center" >Nil </td></tr>
											<%} %>
											
									<!-- ********************************Future Demand End *********************************** -->
											
											 <tr >
											 
												<th  class="std"  colspan="11">Orders Placed ( > &#8377; <% if (projectdatadetails.get(0) != null && projectdatadetails.get(0)[13] != null) { %>
													<%=projectdatadetails.get(0)[13].toString().replaceAll("\\.\\d+$", "")%> ) <% } else { %> - )<% } %>
												</th>
											 </tr>
										
										  	 <tr>	
										  	 	 <th class="std width30 border1px" rowspan="1" >SN</th>
										  	 	 <th class="std border1px width150" >Demand No <br>Demand  Date</th>
												 <th class="std border1px" colspan="2" > Nomenclature</th>
												 <th class="std border1px width150"  >Supply Order No <br> SO Date </th>
												 <th class="std border1px width100"  colspan="1" >SO Cost-Lakh &#8377;</th>
													 <th class="std border1px width100" >DP Date<br> Rev DP</th>
												 <th class="std border1px width200" colspan="2" >Vendor Name</th>
												 <th class="std border1px max-width80 " > Status</th>
												<th class="std border1px width200" > Remarks</th>
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
					<tr>
					<td <%if(!obj[1].toString().equalsIgnoreCase(demand)){ %>  class="border-bottom-none"<%} else{ %> class="border-bottomtop-none"<%} %>>
					<%if(!obj[1].toString().equalsIgnoreCase(demand)){ %>
					<%=rowk %>
					<%} %>
					</td>
					<td <%if(!obj[1].toString().equalsIgnoreCase(demand)){ %> class="border-bottom-none"<%} else{ %> class="border-bottomtop-none"<%} %>>
					<%if(!obj[1].toString().equalsIgnoreCase(demand)){ %><%if(obj[1]!=null) {%> <%=(obj[1].toString())%><% }else{ %>-<%} %><br>
					<%=sdf.format(sdf1.parse(obj[3].toString()))%>
					<%} %>
					</td>
					<td colspan="2" <%if(!obj[1].toString().equalsIgnoreCase(demand)){ %> class="border-bottom-none"<%} else{ %> class="border-bottomtop-none"<%} %>>
					<%if(!obj[1].toString().equalsIgnoreCase(demand)){ %>
					<%=obj[8]%>
					<%} %>
					</td>
				<td class="border1px text-center"><% if(obj[2]!=null){%> <%=obj[2]%> <%}else{ %>-<%} %><br>
					<%if(obj[16]!=null){%> <%=sdf.format(sdf1.parse(obj[16].toString()))%> <%}else{ %> - <%} %>
				</td>
				<td class="border1px text-right"><%if(obj[6]!=null){%> <%=format.format(new BigDecimal(obj[6].toString())).substring(1)%> <%} else{ %> - <%} %></td>
				<td class="border1px">
				<%if(obj[4]!=null){%> <%=sdf.format(sdf1.parse(obj[4].toString()))%> <%}else{ %> - <%} %><br>
				<span class="text-center"><%if(obj[7]!=null){if(!obj[7].toString().equals("null")){%> <%=sdf.format(sdf1.parse(obj[7].toString()))%><%}}else{ %>-<%} %></span>	</td>
					<td colspan="2" class="border1px"><%=obj[12] %> </td>
					<td <%if(!obj[1].toString().equalsIgnoreCase(demand)){ %> class="border-bottom-none"<%} else{ %> class="border-bottomtop-none"<%} %>>
						<%if(!obj[1].toString().equalsIgnoreCase(demand)){ %>
					<%=obj[10]%>
					<%} %>
					
					</td>					
					<td <%if(!obj[1].toString().equalsIgnoreCase(demand)){ %> class="border-bottom-none"<%} else{ %> class="border-bottomtop-none"<%} %>>
						<%if(!obj[1].toString().equalsIgnoreCase(demand)){ %>
					<%=(obj[11].toString())%>
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
										    	<td colspan="5" class="std" class="text-right border1px"><b>Total</b></td>
										    	<td colspan="1" class="std" class="text-right border1px"><b><%=df.format(socost)%></b></td>
										    	<td colspan="5" class="std" class="text-right border1px"></td>
										   		 </tr>	
										 <% }else{%>
											
												<tr><td colspan="8" class="std border1px text-center" >Nil </td></tr>
											<%} %>
									</table> 
								<div align="right" class="width980"> <span class="currency font-weight-bold" >(In &#8377; Lakhs)</span></div>
								<table class="subtables tb-sub"  >
										 <thead>
											 <tr >
												 <th colspan="8" ><span class="mainsubtitle">Total Summary of Procurement</span></th>
											 </tr>
										 </thead>
										 
										 <tbody>
										<tr >
												 <th>No. of Demand</th>
												 <th>Est. Cost</th>
										  	 	 <th>No. of Orders</th>
										  	 	 <th>SO Cost </th>
										  	 	 <th>Expenditure</th>
										</tr>
										 
										 <%if(totalprocurementdetails!=null && totalprocurementdetails.size()>0){ 
										 for(TotalDemand obj:totalprocurementdetails){
											 if(obj.getProjectId().equalsIgnoreCase(projectid)){
										 %>
										   <tr>
										      <td class="text-center"><%=obj.getDemandCount() %></td>
										      <td class="text-center"><%=obj.getEstimatedCost() %></td>
										      <td class="text-center"><%=obj.getSupplyOrderCount()%></td>
										      <td class="text-center"><%=obj.getTotalOrderCost() %></td>
										      <td class="text-center"><%=obj.getTotalExpenditure() %></td>
										   </tr>
										   <%}}}else{%>
										   <tr>
										      <td class="std text-center" colspan="5" >IBAS Server Could Not Be Connected</td>
										   </tr>
										   <%} %>
										   </tbody>
									  </table>
									  
									  
								<div align="left" class="margin-left"><b>(b) Procurement Status</b></div>
								<div align="right" class="width980"> <span class="currency font-weight-bold">(In &#8377; Lakhs)</span></div>
							 	
							 	<table class="subtables tb-sub font-size12" >
									<thead>
										<tr>
											<th colspan="29" ><span class="mainsubtitle">Procurement Status</span></th>
									 	</tr>
									 	<tr>
											<th class="width30">SN</th>
											<th class="width250">Item Name</th>
											<th class="width130">Est/SO Cost<br><span class="currency font-weight-bold" >(In &#8377; Lakhs)</span></th>
											<th class="width20">0</th>
											<th class="width20">1</th>
											<th class="width20">2</th>
											<th class="width20">3</th>
											<th class="width20">4</th>
											<th class="width20">5</th>
											<th class="width20">6</th>
											<th class="width20">7</th>
											<th class="width20">8</th>
											<th class="width20">9</th>
											<th class="width20">10</th>
											<th class="width20">11</th>
											<th class="width20">12</th>
											<th class="width20">13</th>
											<th class="width20">14</th>
											<th class="width20">15</th>
											<th class="width20">16</th>
											<th class="width20">17</th>
											<th class="width20">18</th>
											<th class="width20">19</th>
											<th class="width20">20</th>
											<th class="width20">21</th>
											<th class="width20">22</th>
											<th class="width20">23</th>
											<th class="width20">24</th>
											<th class="width20">25</th>
									 	</tr>
									</thead>
									<tbody>
										<%	List<Object[]> procuremntsList = new ArrayList<>();
											
											if( procurementOnDemand.get(z)!=null ){  procuremntsList.addAll(procurementOnDemand.get(z)); }
											if( procurementOnSanction.get(z)!=null ){  procuremntsList.addAll(procurementOnSanction.get(z)); }
										%>
										<%int psn=0; for(Object[] proc : procuremntsList){psn++; %>
											<tr>
												<td class="text-center"><%=psn %></td>
												<td><%=proc[8] %></td>
												<td class="text-right">
													<%if(proc[9].toString().equalsIgnoreCase("S")){ %>
														<%=proc[6] %>
													<%}else{ %>
														<%=proc[5] %>
													<%} %>
												</td>
												<td class="bg-success"></td>
												<% int filestatus = Integer.parseInt(proc[13].toString()); 
													int tempstatus = filestatus;
													%>
												<%for(int tdc=1;tdc<=25;tdc++){ %>
													
													<%if(filestatus>11){  filestatus--;  } %>
													<%if(filestatus>25){  filestatus--;  } %>
													
													
													<%if(tdc < (tempstatus)){ %>
														<td class="bg-success"></td>
													<%}else if(tdc == (tempstatus)){ %>
														<td class="bg-success text-center text-white ">*</td>
													<%}else if(tdc >(tempstatus)){ %>
														<td ></td>
													<%} %>
													
												<%} %>
											</tr>
										<%}if(envisagedDemandlist!=null && envisagedDemandlist.size()>0){
										for(Object[] envi : envisagedDemandlist){psn++; %>
										<tr>
												<td class="text-center"><%=psn %></td>
												<td><%=envi[3] %></td>
												<td class="text-right"><%=envi[2] %></td>
												<td class=" bgColor text-center">*</td>
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
										<%if(psn ==0 && envisagedDemandlist.size()==0 ){ %>
											<tr>
										      <td colspan="29" class="text-center">Nil</td>
										   </tr>
										<%} %>
										
										
								 	</tbody>
								</table>
								<table class="subtables tb-sub font-size12" >
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
									  
               				<% } %>
							</div> --%>
						</details>
				
<!--  ---------------------------------------------------------------------------------------------------------------------------------------------  -->						
				 	
						<details>
   						<summary role="button" tabindex="0"><b>9. Overall Financial Status  <i class="text-underline">(&#8377; Lakhs)</i> </b> </summary>
   						
											  	<div class="content">
						  	<%for(int z=0;z<projectidlist.size();z++){ 
		                		double totSanctionCost=0,totReSanctionCost=0,totFESanctionCost=0;
			                	double totExpenditure=0,totREExpenditure=0,totFEExpenditure=0;
			                 	double totCommitment=0,totRECommitment=0,totFECommitment=0,totalDIPL=0,totalREDIPL=0,totalFEDIPL=0;
				                double totBalance=0,totReBalance=0,totFeBalance=0,btotalRe=0,btotalFe=0;
						  	%>
						  	<%if(ProjectDetail.size()>1){ %>
								<div>
									<b>Project : <%=ProjectDetail.get(z)[1] %> 	<%if(z!=0){ %>(SUB)<%} %>	</b>
								</div>	
							<%} %>	
						  	
						  	<br>				 
						  	<table  class="subtables width1100" >
						  	    <thead>
		                           <tr>
		                         	<th colspan="2" class="text-center width200">Head</th>
		                         	<th colspan="2" class="text-center width120">Sanction</th>
			                        <th colspan="2" class="text-center width120">Expenditure</th>
			                        <th colspan="2" class="text-center width120">Out Commitment </th>
		                           	<th colspan="2" class="text-center width120">Balance</th>
			                        <th colspan="2" class="text-center width120">DIPL</th>
		                          	<th colspan="2" class="text-center width120">Notional Balance</th>
		                          	<%if(IsIbasConnected!=null &&  IsIbasConnected.equalsIgnoreCase("N")) {%>
		                          	<th colspan="1" class="text-center border-0">
		                          	<button data-toggle="tooltip" onclick ="showModal(<%=projectid %>,'<%=ProjectDetail.get(z)[0] %>','<%=ProjectDetail.get(z)[1] %>')" class="btn btn-sm cursor-pointer"   type="button"  data-toggle="tooltip" data-placement="right"  title="Upload Overall Finance"  ><i class="fa fa-file-excel-o bg-success" aria-hidden="true" ></i>&nbsp;Excel Upload</button>
		                       	<jsp:include page="../print/OverallExcelUpload.jsp"></jsp:include> 
		                          	</th> <%} %>
			                      </tr>
			                      <tr>
				                    <th class="width30 text-center" >SN</th>
				                    <th   class="width180">Head</th>
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
			                    <%if(IsIbasConnected==null || IsIbasConnected.equalsIgnoreCase("Y")) {%>
			                    <tbody>
			                    <% 

				                int count=1;
			                        if(projectFinancialDetails!=null && projectFinancialDetails.size()>0 && projectFinancialDetails.get(z)!=null ){
			                      for(ProjectFinancialDetails projectFinancialDetail:projectFinancialDetails.get(z)){                       %>
			 
			                         <tr>
										<td align="center" class="max-width50 text-center"><%=count++ %></td>
										<td ><b><%=projectFinancialDetail.getBudgetHeadDescription()%></b></td>
										<td align="right" class="text-right"><%=df.format(projectFinancialDetail.getReSanction()) %></td>
										<%totReSanctionCost+=(projectFinancialDetail.getReSanction());%>
										<td align="right" class="text-right"><%=df.format(projectFinancialDetail.getFeSanction())%></td>
										<%totFESanctionCost+=(projectFinancialDetail.getFeSanction());%>
										<td align="right" class="text-right"><%=df.format(projectFinancialDetail.getReExpenditure()) %></td>
										<%totREExpenditure+=(projectFinancialDetail.getReExpenditure());%>
									    <td align="right" class="text-right"><%=df.format(projectFinancialDetail.getFeExpenditure())%></td>
										<%totFEExpenditure+=(projectFinancialDetail.getFeExpenditure());%>
									    <td align="right" class="text-right"><%=df.format(projectFinancialDetail.getReOutCommitment())%></td>
										<%totRECommitment+=(projectFinancialDetail.getReOutCommitment());%>
									    <td align="right" class="text-right"><%=df.format(projectFinancialDetail.getFeOutCommitment())%></td>
										<%totFECommitment+=(projectFinancialDetail.getFeOutCommitment());%>
										<td align="right" class="text-right"><%=df.format(projectFinancialDetail.getReBalance()+projectFinancialDetail.getReDipl())%></td>
										<%btotalRe+=(projectFinancialDetail.getReBalance()+projectFinancialDetail.getReDipl());%>
										<td align="right" class="text-right"><%=df.format(projectFinancialDetail.getFeBalance()+projectFinancialDetail.getFeDipl())%></td>
								       	<%btotalFe+=(projectFinancialDetail.getFeBalance()+projectFinancialDetail.getFeDipl());%>
										<td align="right" class="text-right"><%=df.format(projectFinancialDetail.getReDipl())%></td>
										<%totalREDIPL+=(projectFinancialDetail.getReDipl());%>
										<td align="right" class="text-right"><%=df.format(projectFinancialDetail.getFeDipl())%></td>
										<%totalFEDIPL+=(projectFinancialDetail.getFeDipl());%>
										<td align="right" class="text-right"><%=df.format(projectFinancialDetail.getReBalance())%></td>
										<%totReBalance+=(projectFinancialDetail.getReBalance());%>
										<td align="right" class="text-right"><%=df.format(projectFinancialDetail.getFeBalance())%></td>
										<%totFeBalance+=(projectFinancialDetail.getFeBalance());%>
									</tr>
			<%} }%>

					<tr>
						<td colspan="2"><b>Total</b></td>
						<td align="right" class="text-right"><%=df.format(totReSanctionCost)%></td>
						<td align="right" class="text-right"><%=df.format(totFESanctionCost)%></td>
						<td align="right" class="text-right"><%=df.format(totREExpenditure)%></td>
						<td align="right" class="text-right"><%=df.format(totFEExpenditure)%></td>
						<td align="right" class="text-right"><%=df.format(totRECommitment)%></td>
						<td align="right" class="text-right"><%=df.format(totFECommitment)%></td>
						<td align="right" class="text-right"><%=df.format(btotalRe)%></td>
						<td align="right" class="text-right"><%=df.format(btotalFe)%></td>
						<td align="right" class="text-right"><%=df.format(totalREDIPL)%></td>
						<td align="right" class="text-right"><%=df.format(totalFEDIPL)%></td>
						<td align="right" class="text-right"><%=df.format(totReBalance)%></td>
						<td align="right" class="text-right"><%=df.format(totFeBalance)%></td>
					</tr>
					<tr>
						<td colspan="2"><b>GrandTotal</b></td>
						<td colspan="2" align="right" class="text-right"><b><%=df.format(totReSanctionCost+totFESanctionCost)%></b></td>
						<td colspan="2" align="right" class="text-right"><b><%=df.format(totREExpenditure+totFEExpenditure)%></b></td>
						<td colspan="2" align="right" class="text-right"><b><%=df.format(totRECommitment+totFECommitment)%></b></td>
						<td colspan="2" align="right" class="text-right"><b><%=df.format(btotalRe+btotalFe)%></b></td>
						<td colspan="2" align="right" class="text-right"><b><%=df.format(totalREDIPL+totalFEDIPL)%></b></td>
						<td colspan="2" align="right" class="text-right"><b><%=df.format(totReBalance+totFeBalance)%></b></td>
					</tr>
			                         
			                         
			                         
			                    
			                 
			     </tbody>
			     <%}else{ %>
			     <tbody id="tbody<%=ProjectDetail.get(z)[0].toString()%>">
			     <%int count=0;
			     if(overallfinance!=null && overallfinance.size()>0 && overallfinance.get(z)!=null && overallfinance.get(z).size()>0)  {
			    	for(Object[]obj:overallfinance.get(z)){ 
			    	 %>
			    	 <tr>
			   <td align="center" class="max-width50 text-center"><%=++count %></td>
				<td class="text-justify"><b><%=obj[4].toString()%></b></td>
				<td class="text-right"><%=obj[5].toString()%></td>
				<td class="text-right"><%=obj[6].toString()%></td>
				<td class="text-right"><%=obj[7].toString()%></td>
				<td class="text-right"><%=obj[8].toString()%></td>
				<td class="text-right"><%=obj[9].toString()%></td>
				<td class="text-right"><%=obj[10].toString()%></td>
				<td class="text-right"><%=obj[11].toString()%></td>
				<td class="text-right"><%=obj[12].toString()%></td>
				<td class="text-right"><%=obj[13].toString()%></td>
				<td class="text-right"><%=obj[14].toString()%></td>
				<td class="text-right"><%=obj[15].toString()%></td>
				<td class="text-right"><%=obj[16].toString()%></td>
				</tr>
			     <%}%>
			    	 	<tr>
						<td colspan="2"><b>Total</b></td>
						<td align="right" class="text-right"><%=overallfinance.get(z).get(0)[17].toString()%></td>
						<td align="right" class="text-right"><%=overallfinance.get(z).get(0)[18].toString()%></td>
						<td align="right" class="text-right"><%=overallfinance.get(z).get(0)[19].toString()%></td>
						<td align="right" class="text-right"><%=overallfinance.get(z).get(0)[20].toString()%></td>
						<td align="right" class="text-right"><%=overallfinance.get(z).get(0)[21].toString()%></td>
						<td align="right" class="text-right"><%=overallfinance.get(z).get(0)[22].toString()%></td>
						<td align="right" class="text-right"><%=overallfinance.get(z).get(0)[23].toString()%></td>
						<td align="right" class="text-right"><%=overallfinance.get(z).get(0)[24].toString()%></td>
						<td align="right" class="text-right"><%=overallfinance.get(z).get(0)[25].toString()%></td>
						<td align="right" class="text-right"><%=overallfinance.get(z).get(0)[26].toString()%></td>
						<td align="right" class="text-right"><%=overallfinance.get(z).get(0)[27].toString()%></td>
						<td align="right" class="text-right"><%=overallfinance.get(z).get(0)[28].toString()%></td>
					</tr>
			     	<tr>
						<td colspan="2"><b>GrandTotal</b></td>
						<td colspan="2" align="right" class="text-right"><b><%=Double.parseDouble(overallfinance.get(z).get(0)[17].toString())  +Double.parseDouble(overallfinance.get(z).get(0)[18].toString())%></b></td>
						<td colspan="2" align="right" class="text-right"><b><%=Double.parseDouble(overallfinance.get(z).get(0)[19].toString())  +Double.parseDouble(overallfinance.get(z).get(0)[20].toString())%></b></td>
						<td colspan="2" align="right" class="text-right"><b><%=Double.parseDouble(overallfinance.get(z).get(0)[21].toString())  +Double.parseDouble(overallfinance.get(z).get(0)[22].toString())%></b></td>
						<td colspan="2" align="right" class="text-right"><b><%=Double.parseDouble(overallfinance.get(z).get(0)[23].toString())  +Double.parseDouble(overallfinance.get(z).get(0)[24].toString())%></b></td>
						<td colspan="2" align="right" class="text-right"><b><%=Double.parseDouble(overallfinance.get(z).get(0)[25].toString())  +Double.parseDouble(overallfinance.get(z).get(0)[26].toString())%></b></td>
						<td colspan="2" align="right" class="text-right"><b><%=Double.parseDouble(overallfinance.get(z).get(0)[27].toString())  +Double.parseDouble(overallfinance.get(z).get(0)[28].toString())%></b></td>				     
			     	</tr>
			     <%}else{%> 
			     	<tr>
						<td colspan="2"><b>Total</b></td>
						<td align="right" class="text-right"><%=df.format(totReSanctionCost)%></td>
						<td align="right" class="text-right"><%=df.format(totFESanctionCost)%></td>
						<td align="right" class="text-right"><%=df.format(totREExpenditure)%></td>
						<td align="right" class="text-right"><%=df.format(totFEExpenditure)%></td>
						<td align="right" class="text-right"><%=df.format(totRECommitment)%></td>
						<td align="right" class="text-right"><%=df.format(totFECommitment)%></td>
						<td align="right" class="text-right"><%=df.format(btotalRe)%></td>
						<td align="right" class="text-right"><%=df.format(btotalFe)%></td>
						<td align="right" class="text-right"><%=df.format(totalREDIPL)%></td>
						<td align="right" class="text-right"><%=df.format(totalFEDIPL)%></td>
						<td align="right" class="text-right"><%=df.format(totReBalance)%></td>
						<td align="right" class="text-right"><%=df.format(totFeBalance)%></td>
					</tr>
					<tr>
						<td colspan="2"><b>GrandTotal</b></td>
						<td colspan="2" align="right" class="text-right"><b><%=df.format(totReSanctionCost+totFESanctionCost)%></b></td>
						<td colspan="2" align="right" class="text-right"><b><%=df.format(totREExpenditure+totFEExpenditure)%></b></td>
						<td colspan="2" align="right" class="text-right"><b><%=df.format(totRECommitment+totFECommitment)%></b></td>
						<td colspan="2" align="right" class="text-right"><b><%=df.format(btotalRe+btotalFe)%></b></td>
						<td colspan="2" align="right" class="text-right"><b><%=df.format(totalREDIPL+totalFEDIPL)%></b></td>
						<td colspan="2" align="right" class="text-right"><b><%=df.format(totReBalance+totFeBalance)%></b></td>
					</tr>
			     <% }%>
			     </tbody>
			     <% } %>
			</table>  	
  
  
							<%} %>
							
							</div> 	
						
						</details>
	
<!--  ---------------------------------------------------------------------------------------------------------------------------------------------  -->
						
					<details>
						<%if(committee.getCommitteeShortName().trim().equalsIgnoreCase("EB")){ %>
   							<summary role="button" class="no-arrow" tabindex="0"><b>10. Action Plan for Next Six months </b>    </summary>
						<%}else { %>
							<summary role="button" class="no-arrow" tabindex="0"><b>10. Action Plan for Next Three months </b>    </summary>
						<%} %>
						
						<%-- <div class="content">
						<%for(int z=0;z<1;z++){ %>
							<%if(ProjectDetail.size()>1){ %>
								<div>
									<b>Project : <%=ProjectDetail.get(z)[1] %> 	<%if(z!=0){ %>(SUB)<%} %>	</b>
								</div>	
							<%} %>
					
					
				<table class="subtables table-subtables">
				
				
						<thead>
							<tr>
								<td colspan="9" class="border-0">
									<p class="font-size10 text-center"> 
									<span class="notassign">NA</span> : Not Assigned &nbsp;
									<span class="assigned">AA</span> : Activity Assigned &nbsp;
									<span class="ongoing">OG</span> : On Going &nbsp;
									<span class="delay">DO</span> : Delay - On Going &nbsp;
									<span class="ongoing">RC</span> : Review & Close &nbsp;
									<span class="delay">FD</span> : Forwarded With Delay &nbsp;
									<span class="completed">CO</span> : Completed &nbsp;
									<span class="completeddelay">CD</span> : Completed with Delay &nbsp;
									<span class="inactive">IA</span> : InActive &nbsp;
									<span class="delaydays">DD</span> : Delayed days &nbsp;&nbsp; 
									 </p>
								</td>									
							</tr>
							
							<tr>
									<th class="width15 text-center">SN</th>
									<th class="width20">MS</th>
									<th class="width50">L</th>
									<th class="width275">Action Plan </th>	
									<th class="width120" >Start Date</th>
									<th class="width110" >PDC</th>	
									
									<%if(!session.getAttribute("labcode").toString().equalsIgnoreCase("ADE")) {%>
									<th class="width210">Responsibility </th>
									<%} %>
									<%if(!session.getAttribute("labcode").toString().equalsIgnoreCase("DLRL")) {%>
									<th class="width50">Progress </th>
									<%} %>
					                <th class="width50 padding-right5">Status(DD)</th>
					                <th class="width220">Remarks</th>
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
										%>
										
										<tr>
											<td class="text-center"><%=serialno %></td>
											<td class="text-center">M<%=obj[22] %></td>
							
											<td class="text-center">
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
												<%=entry.getValue() %>
										<%}} 
									 %>
												<% 
													}else if(obj[26].toString().equals("2")) {
													for(Map.Entry<Integer,String>entry:treeMapLevTwo.entrySet()){
														if(entry.getKey().toString().equalsIgnoreCase(obj[3].toString())){%>
															<%=entry.getValue() %>
													<%}} 
													%>
												<%
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
											
											<td class="<%if(obj[26].toString().equals("0")) {%>font-weight-bold<%}%> text-justify ">
												<%if(obj[26].toString().equals("0")) {%>
													<%= (obj[9].toString()) %>
												<%}else if(obj[26].toString().equals("1")) { %>
													&nbsp;&nbsp;<%= (obj[10].toString()) %>
												<%}else if(obj[26].toString().equals("2")) { %>
													&nbsp;&nbsp;<%=(obj[11].toString()) %>
												<%}else if(obj[26].toString().equals("3")) { %>
													&nbsp;&nbsp;<%= (obj[12].toString()) %>
												<%}else if(obj[26].toString().equals("4")) { %>
													&nbsp;&nbsp;<%=(obj[13].toString()) %>
												<%}else if(obj[26].toString().equals("5")) { %>
													&nbsp;&nbsp;<%=(obj[14].toString()) %>
												<%} %>
											</td>
											<td class="text-center" ><%= obj[7]!=null? sdf.format(sdf1.parse(obj[7].toString())) : " - " %></td>
											<td  class="text-center">
												
												<%if(! LocalDate.parse(obj[8].toString()).isEqual(LocalDate.parse(obj[29].toString())) ){ %> 
													<%=sdf.format(sdf1.parse(obj[8].toString())) %> 
												<%}%>
												<%=sdf.format(sdf1.parse(obj[29].toString())) %>
												
											
											</td>
											<%if(!session.getAttribute("labcode").toString().equalsIgnoreCase("ADE")) {%>											
											<td ><%=obj[24] %>, <%=obj[25] %></td>
											<%} %>
											<%if(!session.getAttribute("labcode").toString().equalsIgnoreCase("DLRL")) {%>
											<td class="text-center"><%=obj[16] %>%</td>	
											<%} %>
											<% 
												LocalDate StartDate = LocalDate.parse(obj[7].toString());
												LocalDate EndDate = LocalDate.parse(obj[8].toString());
												LocalDate OrgEndDate = LocalDate.parse(obj[29].toString());
												int Progess = Integer.parseInt(obj[16].toString());
												LocalDate CompletionDate =obj[18]!=null ? LocalDate.parse(obj[18].toString()) : null;
												LocalDate Today = LocalDate.now();
												
											%>										
											<td  class="text-center">
											
											<%if(Progess==0){ %>
												<span class="assigned"> AA </span>
											<%} else if(Progess>0 && Progess<100 && (OrgEndDate.isAfter(Today) || OrgEndDate.isEqual(Today) )){ %>
												<span class="ongoing"> OG </span>
											<%} else if( Progess>0 && Progess<100 && (OrgEndDate.isBefore(Today) )){ %>
												<span class="delay"> DO (<%=ChronoUnit.DAYS.between(OrgEndDate, LocalDate.now())%>)</span>
											<%} else if((CompletionDate!=null && ( CompletionDate.isBefore(OrgEndDate) ||  CompletionDate.isEqual(OrgEndDate)))){ %>
												<span class="completed"> CO</span>
											<%} else if((CompletionDate!=null && CompletionDate.isAfter(OrgEndDate) )){ %>
												<span class="completeddelay">CD (<%=ChronoUnit.DAYS.between(OrgEndDate, CompletionDate)%>)</span>
											<%}else if(CompletionDate!=null && Progess==0 &&  ( EndDate.isAfter(Today) ||  EndDate.isEqual(Today)) ){ %>
												<span class="inactive">IA</span>
											<%}else{ %>
												<span class="assigned">AA</span>
											<%} %>
											
											</td>


											<td>
											    // Commented Code 
											    
											    <span id="remarksSpan_<%=obj[34]%>">
											        <%= obj[28] != null ? StringEscapeUtils.escapeHtml4(obj[28].toString()) : "-" %>
											    <!-- </span> -->
											
											    <input
											        type="text"
											        id="remarksInput_<%=obj[34]%>"
											        class="form-control"
											        value="<%= obj[28] != null ? StringEscapeUtils.escapeHtml4(obj[28].toString()) : "" %>"
											        style="display:none; width:200px;">
											
											    <% if("DLRL".equalsIgnoreCase(labcode) && obj[34] != null && !"0".equalsIgnoreCase(obj[34].toString())){ %>
											        <button
											            type="button"
											            class="btn btn-sm edit"
											            onclick="editRemarks('<%=obj[34]%>')">
											            <i class="fa fa-edit"></i>
											        </button>
											
											        <button
											            type="button"
											            id="saveBtn_<%=obj[34]%>"
											            class="btn btn-sm btn-success"
											            style="display:none;"
											            onclick="saveRemarks('<%=obj[34]%>')">
											            Save
											        </button>
											    <% } %>
											</td>
										</tr>
										
									<%count1++; serialno++;}} %>
								<%} else{ %>
								
								<tr><td colspan="9" class="text-center"> Nil</td></tr>
								
								<%} %>
								
								</tbody>
								
							</table>
		
		
						<%} %>
						</div> --%>
					
					</details>

<!--  ---------------------------------------------------------------------------------------------------------------------------------------------  -->						

					<%-- <jsp:include page="briefingPaper/ValuationOfTechnologyBp.jsp" />  --%>
					
					<details >
   						<summary role="button" class="no-arrow" tabindex="0"><b> 11. Valuation of Technologies</b>    </summary>
   					</details>
					
   					

<!--  ---------------------------------------------------------------------------------------------------------------------------------------------  -->						

					<details >
   						<summary role="button" class="no-arrow" tabindex="0"><b> 12. Economic Impact of Project</b>    </summary>
   						
   					</details>


<!--  ---------------------------------------------------------------------------------------------------------------------------------------------  -->						
						
					<details >
   						<summary role="button" tabindex="0"><b> 13. GANTT chart of overall project schedule [<span class="text-underline">Original </span>(as per Project sanction / Latest PDC extension) and <span class="text-underline">Current</span>]</b>    </summary>
   						
						    <div class="content">
							    <%for(int z=0;z<1;z++){ %>
							    <div>
							    	<%if(ProjectDetail.size()>1){ %>
										<div>
											<b>Project : <%=ProjectDetail.get(z)[1] %> 	<%if(z!=0){ %>(SUB)<%} %>	</b>
										</div>	
									<%} %>	
								    <div class="row">
								    	<div class="col-md-9 ">
											<form method="post" class="form-post" enctype="multipart/form-data" >
												<label>Note : Upload PNG image only.</label>
												<input type="file" name="FileAttach" id="FileAttach"  required="required"  accept="image/png"/>
												<input type="hidden" name="ChartName"  value="grantt_<%=projectidlist.get(z)%>_<%=No2%>"> 
												<button type="submit" class="btn btn-sm back margin-right" formaction="GanttChartUpload.htm"  >Upload</button>
												<button type="submit" formtarget="_blank" class="btn btn-sm back ganttChartSub" formaction="GanttChartSub.htm" formnovalidate="formnovalidate" >Sub Level</button>
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
												<input type="hidden" name="ProjectId" id="ProjectId" value="<%=projectidlist.get(z)%>"> 
												<input type="hidden" name="committeeid" value="<%=committeeid%>">
											</form>
										</div>
										<div class="col-md-3 margin-top10 float-right">
											<label>Interval : &nbsp;&nbsp;&nbsp; </label>
											<select class="form-control selectdee width150" name="interval_<%=projectidlist.get(z)%>" id="interval_<%=projectidlist.get(z)%>" required="required"  data-live-search="true" >
				                                <option value="quarter"> Quarterly </option>
				                                <option value="half" >Half-Yearly</option>
				                                <option value="year" >Yearly</option>
				                                <option value="month"> Monthly </option>
											</select>
										</div>
									</div>
	<!-- -----------------------------------------------gantt chart js ------------------------------------------------------------------------------------------------------------------------------- -->						    		
									
										
											<div class="row margin-top10 font-weight-bold"    >
												<div class="col-md-4"></div>
												<div class="col-md-4"></div>
												<div class="col-md-4">
													<div class="font-weight-bold" >
														<span class="span1">Original :&ensp; <span class="spa1"></span></span>
														<span class="span2">Ongoing :&ensp; <span class="spa2"></span></span>
														<span class="span2">Revised :&ensp; <span class="spa3"></span></span>
													</div>
												</div>
											</div>
										
									<div class="row">
										<div class="col-md-12 float-right" align="center">
										   	<div class="flex-container containers" id="containers_<%=projectidlist.get(z)%>"  ></div>
										</div>		
									</div>		
								</div>
							<%} %>							
						</div>
					
					</details>
<!--  ---------------------------------------------------------------------------------------------------------------------------------------------  -->
 						
					<details>
   						<summary role="button" class="no-arrow" tabindex="0"><b>14. Issues</b></summary>
   						
						 <%--   <div class="content">
						   			<%for(int z=0;z<1;z++){ %>		
						   			
						   			<%if(ProjectDetail.size()>1){ %>
										<div>
											<b>Project : <%=ProjectDetail.get(z)[1] %> 	<%if(z!=0){ %>(SUB)<%} %>	</b>
										</div>	
									<%} %>	
										   		 
									
			<table class="subtables table-subtables" >
						<thead>
							<tr>
								<td colspan="7" class="border-0">
									<p class="font-size10 text-center"> 
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
									</p>
								</td>									
							</tr>
							<tr>
								<th  class="width20 text-center">SN</th>
								<th  class="width50 text-center">ID</th>
								<th  class="width300">Issue Point</th>
								<th  class="width120 "> ADC <br> PDC</th>
								<th  class="width210 ">Responsibility</th>
								<th  class="width50 " >Status(DD)</th>	
								<th  class="width230">Remarks</th>		
							</tr>
						</thead>
						<tbody>				
										<%if(oldpmrcissueslist.get(z).size()==0){ %>
										<tr><td colspan="7" class="text-center" > Nil</td></tr>
										<%}
										else if(oldpmrcissueslist.get(z).size()>0)
										{
											int i=1;
										for(Object[] obj:oldpmrcissueslist.get(z)){
											if(!obj[9].toString().equals("C")  || (obj[9].toString().equals("C") && obj[13]!=null && before6months.isBefore(LocalDate.parse(obj[13].toString())) )){
											%>
											<tr>
												<td  class="text-center"><%=i %></td>
																<td class="text-center" >
									<%if(obj[18]!=null && Long.parseLong(obj[18].toString())>0){
										String []temp=obj[1].toString().split("/");
										String tempString=temp[temp.length-1];
										%>
										<span class="font-weight-bold">
										<%=tempString %>
										</span>
									<%}%>
								</td>
												<td  class="text-justify"><%=obj[2]!=null?(obj[2].toString()):"" %></td>
												<td  class="text-center" >
													<span >		<%	String actionstatus = obj[9].toString();
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
												
												<br></span>
													
													<%if(!pdcorg.equals(endDate)){ %>
													<%=sdf.format(sdf1.parse(obj[4].toString()))%><br>
													<%} %>
													<%=sdf.format(sdf1.parse(obj[3].toString()))%>
												</td>

											

												<td > <%=obj[11] %>, <%=obj[12] %></td>

												<td  class="text-center"> 
													<%if(obj[4]!= null){ %> 
														
														<% if(lastdate!=null && actionstatus.equalsIgnoreCase("C") ){%>
																<%if(actionstatus.equals("C") && (pdcorg.isAfter(lastdate) || pdcorg.equals(lastdate))){%>
																	<span class="completed">CO</span>
																<%}else if(actionstatus.equals("C") && pdcorg.isBefore(lastdate)){ %>	
																	<span class="delay">CD (<%= ChronoUnit.DAYS.between(pdcorg, today)  %>)  </span>
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
														-
													<%} %>
												</td>	
												<td > <%if(obj[17]!=null){ %> <%=(obj[17].toString()) %> <%} %> </td>			
											</tr>			
										<%i++;
										}}} %>
								</tbody>			
							</table>
	
								<%} %>
						   </div>	 --%>
						   
					</details>

<!--  ---------------------------------------------------------------------------------------------------------------------------------------------  -->						
					<details>
   						<summary role="button" tabindex="0"><b> 15. Other Relevant Points (if any) 
   							<%if(committee.getCommitteeShortName().trim().equalsIgnoreCase("EB")){ %>
   								and Technical Work Carried Out For Last Six Months
							<%}else { %>
								and Technical Work Carried Out For Last Three Months
							<%} %>
   							</b>
   						</summary>
   						
						  <div class="content">
						  	<%for(int z=0;z<1;z++){ %>
						  	<%if(z!=0){ break;} %>
								<div>
									<b>Project : <%=ProjectDetail.get(z)[1] %> 		</b>
								</div>	
								
								<div class="card-body width-100" >
								
									<form action="TechnicalWorkDataAdd.htm" method="post">
										<div class="row"align="center" >
											<div class="row width-100 div-margin">
												<div class="col-12">
													<textarea class="form-control" name="RelevantPoints" id="ckeditor" rows="5" cols="50" maxlength="5"><%if(TechWorkDataList.get(z)!=null){ %> <%=TechWorkDataList.get(z)[2] %> <%}%></textarea>
												</div>	
											</div>
										</div>
										<div class="row marging-top15" align="center" >
										<div class="col-3 div-col" ><label class="div-label">Technical Work Carried (Attachment)</label></div>
											<div class="col-9 text-left">
												<div class="row">
												  <div class="col-2 margin-rem" >
													<span id="attachname_<%=projectidlist.get(z)%>" ></span>
													<%if(TechWorkDataList.get(z)==null){ %>
														<input type="hidden" name="TechDataId" value="0">
														<input type="hidden" class="hidden" name="attachid" id="attachid_<%=projectidlist.get(z)%>" value="0">
													<%}else{ %>
														<input type="hidden" name="TechDataId" value="<%=TechWorkDataList.get(z)[0]%>">
														<%if(TechWorkDataList.get(z)[3]!=null && Long.parseLong(TechWorkDataList.get(z)[3].toString())>0){ %>
														<button type="button" class="btn" title="Download Document"  onclick="FileDownload1('<%=TechWorkDataList.get(z)[3]%>');"  ><i class="fa fa-download" aria-hidden="true"> </i></button>
														<button type="button" class="btn btn-danger btnfileattachment"  title="Unlink Document" onclick="removeFileAttch('<%=projectidlist.get(z)%>','<%=TechWorkDataList.get(z)[0]%>','<%=TechWorkDataList.get(z)[3]%>') ;" ><i class="fa fa-chain-broken" aria-hidden="true"></i></button>
														<input type="hidden" class="hidden" name="attachid" id="attachid_<%=projectidlist.get(z)%>" value="<%=TechWorkDataList.get(z)[3]%>">
														<%} else{%>
														<input type="hidden" class="hidden" name="attachid" id="attachid_<%=projectidlist.get(z)%>" value="0">
														<%} %>
													<%} %>
													<button type="button" class="btn btn-primary btnfileattachment"  title="Link Document" onclick="openMainModal(<%=projectidlist.get(z)%>) ;" ><i class="fa fa-link" aria-hidden="true"></i></button>
													<input type="hidden" name="projectid" value="<%=projectidlist.get(z)%>">
													<input type="hidden" name="committeeid" value="<%=committeeid%>">
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                                    </div>
                                                    <div class="col-2"></div>
		                                             <div class="col-3">
														<button type="submit" class="btn btn-sm submit" name="submit" value="submit" onclick="return confirm('Are You Sure To Submit ?');">SUBMIT</button>
														<input type="hidden" name="fileRepID" id="fileRepID" value="">
													</div>
											  </div>
											</div>
										</div>
									</form>								
								
								<b>Technical Images</b> 
								<div class="row">
									<form action="ProjectTechImages.htm" method="post" class="float-left" enctype="multipart/form-data" >
										<input type="file" name="FileAttach" id="FileAttach" required="required"  accept="image/jpeg"/> 
										<button type="submit" class="btn btn-sm back"  onclick="return confirm('Are you sure to submit this?')">Upload</button>
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
										<input type="hidden" name="committeeid" value="<%=committeeid%>">
										<input type="hidden" name="ProjectId"  value="<%=projectidlist.get(z)%>"> 
									</form>
											
								</div>
							<% if(TechImages.size()>0){
							List<TechImages>  TechImagesList= TechImages.get(z); 
							if(TechImagesList.size()>0){
							int i=0;
							for(TechImages imges:TechImagesList){ %>
							<div class="row">
							<table>
							<tr>
							<td class="border-0 padding-leftrem"> 
							<% 
							Path uploadPath = Paths.get(filePath,projectLabCode,"TechImages",imges.getTechImagesId()+"_"+imges.getImageName());
							File file = uploadPath.toFile();
							if(file.exists()){ %>
								<img class="img-styl" src="data:image/*;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(file))%>" > 											
									<%} %>
									<button class="btn btn-sm bg-transparent" id="TechImagesId1" value="" onclick="openEditDiv(<%=imges.getTechImagesId()%>)" ><i class="fa fa-pencil-square-o" aria-hidden="true"></i></button>
									<form action="#" class="d-inline">
									<button class="btn btn-danger" name="TechImagesId" value="<%=imges.getTechImagesId()%>" formaction="ProjectImageDelete.htm" formmethod="POST" onclick="return confirm('Are you sure, you want to remove this?')"><i class="fa fa-trash text-white" aria-hidden="true"  ></i></button>
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
									<input type="hidden" name="committeeid" value="<%=committeeid%>">
									<input type="hidden" name="ProjectId"  value="<%=projectidlist.get(z)%>"> 
									</form>
									<form action="TechImagesEdit.htm" method="post" class="d-inline" enctype="multipart/form-data">
									<label for="FileAttach" id="filelabel<%=imges.getTechImagesId()%>" class=" margin-left20 display-none">
										<input type="file" name="FileAttach" id="FileAttach" required="required"  accept="image/jpeg"/> 
										<button type="submit" class="btn btn-sm back"  onclick="return confirm('Are you sure, you want to edit this?')">Upload</button>
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
										<input type="hidden" name="committeeid" value="<%=committeeid%>">
										<input type="hidden" name="ProjectId"  value="<%=projectidlist.get(z)%>"> 
										<input type="hidden" name="TechImageId"  value="<%=imges.getTechImagesId()%>"> 
								    </label>
								    </form>										
									</td>

									</tr>
								</table>
							</div>
							<br>
							<%i++;}}} %>
						
								
								</div>
								
						  <%} %>
						  </div>
					
					
					</details>


<!--  ---------------------------------------------------------------------------------------------------------------------------------------------  -->
 
					<details>
   						<summary role="button" tabindex="0"><b>16. Decision/Recommendations sought from <%=committee.getCommitteeShortName().trim().toUpperCase() %></b>     </summary>
   						
						  <div class="content">
						  
						  <%if(nextMeetVenue!=null && nextMeetVenue[0]!=null){%>
						  
						  	<form action="RecDecDetailsAdd.htm" method="post" id="recdecdetails">
								<div class="row margin-top10" >
									<div class="col-md-4"> 
										<table class="table table-bordered table-hover table-striped table-condensed ">
											<thead>
												<tr><th class="width-5">SN</th><th class="width-80">Type</th><th class="width-5">Action</th></tr>
											</thead>
											<tbody>
											<%int i=0; if(RecDecDetails!=null && RecDecDetails.size()>0){ 
												for(Object[] obj :RecDecDetails){
												String pointdata= "";
												if(obj[3].toString().length()>30){
													pointdata=obj[3].toString();
												}else{
													pointdata=obj[3].toString();
												}
												%>
												<tr>
													<td class="width-5 text-center"> <%=++i%></td>
													<td class="width-80 text-break">
													<b class="color145374"><%=obj[2]%> :-</b>
													  <%if(pointdata.length()>30){%> <%=pointdata.substring(0,30)%>  <span onclick="RecDecmodal('<%=obj[0]%>')" class="color1176ab"><b> ...View More </b></span> <%}else{%> <%=pointdata%><%}%>
													  </td>
													<td class="text-center width-5"> 
													<button class="btn btn-warning btn-sm" type="button" onclick="RecDecEdit('<%=obj[0]%>' )" value="EDIT"  > <i class="fa fa-pencil-square-o color100f0e"  aria-hidden="true"></i></button>
												
													<button class="btn btn-sm btn-danger" type="button" onclick="RecDecremove('<%=obj[0].toString() %>')" ><i class="fa fa-trash text-white" aria-hidden="true" ></i></button>
																									
													</td>
												</tr>
												<%}}else{%><td colspan="3" class="text-center"> No Data Available!</td><%}%>
											</tbody>
										</table>
										<div align="center">
											<button type="button" class="btn btn-info btn-sm add" onclick="RecDecEdit('0')"> ADD</button>
										</div>
									</div>
									<div class="col-md-8"> 
									<div class="card" >
										<div class="card-header height40" >
											<div align="center" id="drcdiv"  >
			  									<div class="form-check form-check-inline">
												  <input class="form-check-input" type="radio" name="darc" id="decision" value="D" required="required">
												  <label class="form-check-label" for="decision"><b> Decision </b></label>
												</div>
												<div class="form-check form-check-inline">
												  <input class="form-check-input" type="radio" name="darc" id="recommendation" value="R" required="required">
												  <label class="form-check-label" for="recommendation"> <b>Recommendation </b></label>
												</div>
			  								</div>
										</div>
										 <div class="card-body">
										    <textarea class="form-control" name="RecDecPoints" id="ckeditor1" rows="5" cols="20" maxlength="5"  required="required"></textarea>
											<div align="center">
												<input type="hidden" name="RedDecID" id="recdecid">
												<input type="hidden" name="schedulid" value="<%=nextMeetVenue[0]%>">
												<button type="button"  class="btn btn-primary btn-sm add margin-top10"  onclick="return checkData('recdecdetails')">Submit </button>
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
												<input type="hidden" name="projectid" value="<%=projectid%>"/>
												<input type="hidden" name="committeeid" value="<%=committeeid%>"/>	
											</div>
										</div>
								    </div>
									</div>
								</div>	
								</form>
								<%}else{%>
										 <h5>Meeting is Not Scheduled!</h5>
								<%}%>
						  	<br><br><br><br><br>
						  </div>	
						   
					</details>						
			 					<form action="DecesionRemove.htm" id="remvfrm" class="display-none">
				<input type="hidden" name="recdecId" id="recdecId">
				<input type="hidden" name="committeeid" value="<%=committeeid%>">
						<input type="hidden" name="ProjectId"  value="<%=projectidlist.get(0)%>"> 
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				</form>	

<!--   --------------------------------------------------------------------------------------------------------------------------------------------- --> 
					<details>
   						<summary role="button" tabindex="0"><b>Note</b></summary>
						  <div class="content">
							
								1) Agenda mentioned in Chapter 6 on Project Monitoring and Review be referred while making briefing papers. 
								
								<br>	
								2) Action plan and Impact on Overall PDC (if any), as mentioned at S. No. 9 should 
								   mandatorily form part of AB/ PMB/ EB minutes which should be released within 10 days 
								   of meeting.  If the minutes of meeting to be vetted by outside offices cut off dates should 
								   be given beyond which minutes would be assumed to be approved. 
								
								<br>
								3) Format for Apex Board, Programme Management Board or Executive Board may be 
								   chosen based on the review being scheduled for a project.
								<br>
								4) Detailed technical discussions on each sub systems to be deliberated and recorded 
								   during PMB/ EB / PMRB. Ratification points from the higher review committee to be 
								   clearly mentioned in the minutes. 
								<br>
								5) For PDC extension cases, the defendable reason why PDC could not be adhered & 
								   remedial steps to be taken to avoid further PDC extension may also be presented as per 
								   the table given below & recorded in minutes. 
						  </div>	
					</details>
				
		</div>
	</div>
			</div>
		</div>
	</div>


<!--------------------------------------------------- Milestone Model -----------------------------------------------  -->

<div class="modal fade " id="exampleModal1" tabindex="-1" aria-labelledby="exampleModalLabel1" aria-hidden="true">
		<div class="modal-dialog modal-xl">
			<div class="modal-content">
					      
				<div class="modal-body">
					
					
					
   <div class="container-fluid">
		<div class="row">
			<div class="col-md-12">
				<div class="card shadow-nohover" >
				<div class="row card-header">
			     <div class="col-md-10">
					<h5 ><%if(ProjectId!=null){
						Object[] ProjectDetail123=(Object[])request.getAttribute("ProjectDetailsMil");
						%>
						<%=ProjectDetail123[2] %> ( <%=ProjectDetail123[1] %> ) 
					<%} %>
					</h5>
					</div>
					
					 </div>
					<div class="card-body">
					
                                              <div class="table-responsive"> 
												<table class="table  table-hover table-bordered">
													<thead>

														<tr>
															<th>Expand</th>
															<th class="text-left max-width15">Mil-No</th>
															<th class="text-left max-width200">Milestone Activity</th>
															<th >Start Date</th>
															<th >End Date</th>	
															<th  class="text-left max-width200">First OIC </th>
															<th  class="text-center max-width50">Weightage</th>	
															<th  class="text-center max-width80">Progress</th>												

														</tr>
													</thead>
													<tbody>
													
														<%int  count=1;
														
														 	if(MilestoneList!=null&&MilestoneList.size()>0){
											
															for(Object[] obj: MilestoneList){ %>
														<tr class="milestonemodalwhole" id="milestonemodal<%=obj[5] %>"  >
															<td class="center width2">
																<span class="clickable" data-toggle="collapse" id="row<%=count %>" data-target=".row<%=count %>">
																	<button class="btn btn-sm btn-success" id="btn<%=count %>"  onclick="ChangeButton('<%=count %>')">
																		<i class="fa fa-plus"  id="fa<%=count%>"></i>
																	 </button>
																</span>
															</td>
															<td class="text-left width-7"> Mil-<%=obj[5]%></td>
															<td class="td-wrap"><%=(obj[4].toString()) %></td>
															
															<td  class="width8"><%=sdf.format(obj[2])%></td>
															<td  class="width8"><%=sdf.format(obj[3])%></td>
															<td  class="width15P"><%=obj[6]%></td>
															<td  class="width9p" align="center"><%=obj[13]%></td>	
															<td>
															<%if(!obj[12].toString().equalsIgnoreCase("0")){ %>
															<div class="progress div-progress" >
															<div class="progress-bar progress-bar-striped width-<%=obj[12] %>
															<%if(obj[14].toString().equalsIgnoreCase("2")){ %>
															 bg-success
															<%} else if(obj[14].toString().equalsIgnoreCase("3")){ %>
															  bg-info
															<%} else if(obj[14].toString().equalsIgnoreCase("4")){ %>
															  bg-danger
															<%} else if(obj[14].toString().equalsIgnoreCase("5")){ %>
															  bg-warning
															<%}  %>
															" role="progressbar"  aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
															<%=obj[12] %>
															</div> 
															</div> <%}else{ %>
															<div class="progress div-progress" >
															<div class="progress-bar progressbar" role="progressbar" >
															Not Started
															</div>
															</div> <%} %>
															</td>
																
		
														</tr>
														 <tr class="collapse font-weight-bold row<%=count %>">
                                                         <td></td>
                                                         <td>Sub</td>
                                                         <td>Activity</td>
                                                         <td>Start Date</td>
                                                         <td>End Date</td>
                                                         <td>Date Of Completion</td>
                                                         <td>Sub Weightage</td>
                                                         <td>Sub Progress</td>
                                                         <td></td>
                                                         </tr>
                                                         <% int countA=1;
                                                            List<Object[]> MilestoneA=(List<Object[]>)request.getAttribute(count+"MilestoneActivityA");
														 	if(MilestoneA!=null&&MilestoneA.size()>0){
															for(Object[] objA: MilestoneA){
	                                                            List<Object[]> MilestoneB=(List<Object[]>)request.getAttribute(count+"MilestoneActivityB"+countA);
	
																%>
														<tr class="collapse row<%=count %>">
															<td  class="center width2"> </td>
															<td class="text-left width-5"> A-<%=countA%></td>
															<td class="td-wrap"><%=(objA[4].toString()) %></td>
															
															<td class="width-30px"><%=sdf.format(objA[2])%></td>
															<td class="width8"><%=sdf.format(objA[3])%></td>
															
															
															<td class="width-30px"><%if(objA[9].toString().equalsIgnoreCase("3")||objA[9].toString().equalsIgnoreCase("5")){ %>
														     <%if(objA[7]!=null){ %>   <%=sdf.format(objA[7]) %> <%}else{ %><%=objA[8] %> <%} %>
														         <%}else{ %>
														         <%=objA[8] %>
															 <%} %></td>
															 <td align="center"><%=objA[6] %></td>
															<td>
															<%if(!objA[5].toString().equalsIgnoreCase("0")){ %>
															<div class="progress div-progress">
															<div class="progress-bar progress-bar-striped width-<%=objA[5] %>
															<%if(objA[9].toString().equalsIgnoreCase("2")){ %>
															 bg-success
															<%} else if(objA[9].toString().equalsIgnoreCase("3")){ %>
															  bg-info
															<%} else if(objA[9].toString().equalsIgnoreCase("4")){ %>
															  bg-danger
															<%} else if(objA[9].toString().equalsIgnoreCase("5")){ %>
															  bg-warning
															<%}  %>
															" role="progressbar" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
															<%=objA[5] %>
															</div> 
															</div> <%}else{ %>
															<div class="progress div-progress" >
															<div class="progress-bar progressbar" role="progressbar" >
															Not Started
															</div>
															</div> <%} %>
															</td>						
													
                                                         <td></td>
                                                         </tr>
                                                         <% int countB=1;
														 	if(MilestoneB!=null&&MilestoneB.size()>0){
															for(Object[] objB: MilestoneB){
	                                                            List<Object[]> MilestoneC=(List<Object[]>)request.getAttribute(count+"MilestoneActivityC"+countA+countB);
	
																%>
														<tr class="collapse row<%=count %>">
															<td  class="center width2"> </td>
															<td class="text-left width-5"> &nbsp;&nbsp;&nbsp;B-<%=countB%></td>
															<td class="td-wrap"><%=(objB[4].toString()) %></td>
															
															<td class="width-30px"><%=sdf.format(objB[2])%></td>
															<td class="width8 "><%=sdf.format(objB[3])%></td>
															
															<td class="width-30px"><%if(objB[9].toString().equalsIgnoreCase("3")||objB[9].toString().equalsIgnoreCase("5")){ %>
														      <%if(objB[7]!=null){ %>   <%=sdf.format(objB[7]) %> <%}else{ %><%=objB[8] %> <%} %>
														         <%}else{ %>
														         <%=objB[8] %>
															 <%} %></td>
															  <td align="center"><%=objB[6] %></td>
															<td>
															<%if(!objB[5].toString().equalsIgnoreCase("0")){ %>
															<div class="progress div-progress" >
															<div class="progress-bar progress-bar-striped width-<%=objB[5] %>
															<%if(objB[9].toString().equalsIgnoreCase("2")){ %>
															 bg-success
															<%} else if(objB[9].toString().equalsIgnoreCase("3")){ %>
															  bg-info
															<%} else if(objB[9].toString().equalsIgnoreCase("4")){ %>
															  bg-danger
															<%} else if(objB[9].toString().equalsIgnoreCase("5")){ %>
															  bg-warning
															<%}  %>
															" role="progressbar"  aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
															<%=objB[5] %>
															</div> 
															</div> <%}else{ %>
															<div class="progress div-progress" >
															<div class="progress-bar progressbar" role="progressbar" >
															Not Started
															</div>
															</div> <%} %>
															</td>
															
														 													
                                                         <td></td>
                                                         </tr>
                                                         <% int countC=1;
														 	if(MilestoneC!=null&&MilestoneC.size()>0){
															for(Object[] objC: MilestoneC){
													         List<Object[]> MilestoneD=(List<Object[]>)request.getAttribute(count+"MilestoneActivityD"+countA+countB+countC);
																%>
														<tr class="collapse row<%=count %>">
															<td  class="center width2"> </td>
															<td class="text-left width-5"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;C-<%=countC%></td>
															<td class="td-wrap"><%=(objC[4].toString()) %></td>
															
															<td class="width-30px"><%=sdf.format(objC[2])%></td>
															<td class="width8"><%=sdf.format(objC[3])%></td>
															
															<td class="width-30px"><%if(objC[9].toString().equalsIgnoreCase("3")||objC[9].toString().equalsIgnoreCase("5")){ %>
														     <%if(objC[7]!=null){ %>   <%=sdf.format(objC[7]) %> <%}else{ %><%=objC[8] %> <%} %>
														         <%}else{ %>
														         <%=objC[8] %>
															 <%} %></td>	
															  <td align="center"><%=objC[6] %></td>
															<td>
															<%if(!objC[5].toString().equalsIgnoreCase("0")){ %>
															<div class="progress div-progress" >
															<div class="progress-bar progress-bar-striped width-<%=objC[5] %>
															<%if(objC[9].toString().equalsIgnoreCase("2")){ %>
															 bg-success
															<%} else if(objC[9].toString().equalsIgnoreCase("3")){ %>
															  bg-info
															<%} else if(objC[9].toString().equalsIgnoreCase("4")){ %>
															  bg-danger
															<%} else if(objC[9].toString().equalsIgnoreCase("5")){ %>
															  bg-warning
															<%}  %>
															" role="progressbar"  aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
															<%=objC[5] %>
															</div> 
															</div> <%}else{ %>
															<div class="progress div-progress" >
															<div class="progress-bar progressbar" role="progressbar" >
															Not Started
															</div>
															</div> <%} %>
															</td>
															
														
                                                         <td></td>
                                                         </tr>
                                                         <% int countD=1;
														 	if(MilestoneD!=null&&MilestoneD.size()>0){
															for(Object[] objD: MilestoneD){
	                                                            List<Object[]> MilestoneE=(List<Object[]>)request.getAttribute(count+"MilestoneActivityE"+countA+countB+countC+countD);
	
																%>
														<tr class="collapse row<%=count %>">
															<td  class="center width2"> </td>
															<td class="text-left width-5"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;D-<%=countD%></td>
															<td class="td-wrap"><%=(objD[4].toString()) %></td>
															
															<td class="width-30px"><%=sdf.format(objB[2])%></td>
															<td class="width8"><%=sdf.format(objB[3])%></td>
															
															<td class="width-30px"><%if(objD[9].toString().equalsIgnoreCase("3")||objD[9].toString().equalsIgnoreCase("5")){ %>
														      <%if(objD[7]!=null){ %>   <%=sdf.format(objD[7]) %> <%}else{ %><%=objD[8] %> <%} %>
														         <%}else{ %>
														         <%=objD[8] %>
															 <%} %></td>
															  <td align="center"><%=objD[6] %></td>
															<td>
															<%if(!objD[5].toString().equalsIgnoreCase("0")){ %>
															<div class="progress div-progress" >
															<div class="progress-bar progress-bar-striped width-<%=objD[5] %>
															<%if(objD[9].toString().equalsIgnoreCase("2")){ %>
															 bg-success
															<%} else if(objD[9].toString().equalsIgnoreCase("3")){ %>
															  bg-info
															<%} else if(objD[9].toString().equalsIgnoreCase("4")){ %>
															  bg-danger
															<%} else if(objD[9].toString().equalsIgnoreCase("5")){ %>
															  bg-warning
															<%}  %>
															" role="progressbar"  aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
															<%=objD[5] %>
															</div> 
															</div> <%}else{ %>
															<div class="progress div-progress" >
															<div class="progress-bar progressbar" role="progressbar" >
															Not Started
															</div>
															</div> <%} %>
															</td>
															
														 													
                                                         <td></td>
                                                         </tr>
                                                         <% int countE=1;
														 	if(MilestoneE!=null&&MilestoneE.size()>0){
															for(Object[] objE: MilestoneE){ %>
														<tr class="collapse row<%=count %>">
															<td  class="center width2"> </td>
															<td class="text-left width-5"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;E-<%=countE%></td>
															<td class="td-wrap"><%=(objE[4].toString()) %></td>
															
															<td class="width-30px"><%=sdf.format(objE[2])%></td>
															<td class="width8"><%=sdf.format(objE[3])%></td>
															
															<td class="width-30px"><%if(objE[9].toString().equalsIgnoreCase("3")||objE[9].toString().equalsIgnoreCase("5")){ %>
														     <%if(objE[7]!=null){ %>   <%=sdf.format(objE[7]) %> <%}else{ %><%=objE[8] %> <%} %>
														         <%}else{ %>
														         <%=objE[8] %>
															 <%} %></td>	
															  <td align="center"><%=objE[6] %></td>
															<td>
															<%if(!objE[5].toString().equalsIgnoreCase("0")){ %>
															<div class="progress div-progress" >
															<div class="progress-bar progress-bar-striped width-<%=objE[5] %>
															<%if(objC[9].toString().equalsIgnoreCase("2")){ %>
															 bg-success
															<%} else if(objE[9].toString().equalsIgnoreCase("3")){ %>
															  bg-info
															<%} else if(objE[9].toString().equalsIgnoreCase("4")){ %>
															  bg-danger
															<%} else if(objE[9].toString().equalsIgnoreCase("5")){ %>
															  bg-warning
															<%}  %>
															" role="progressbar" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
															<%=objE[5] %>
															</div> 
															</div> <%}else{ %>
															<div class="progress div-progress" >
															<div class="progress-bar progressbar" role="progressbar"  >
															Not Started
															</div>
															</div> <%} %>
															</td>
															
														
                                                         <td></td>
                                                         </tr>
												<% countE++;} }%>
												<% countD++;} }%>
												<% countC++;} }%>
												<% countB++;} }%>
												<% countA++;} }else{%>
												<tr class="collapse row<%=count %>">
													<td colspan="9"  class="center text-center">No Sub List Found</td>
												</tr>
												<%} %>
												<% count++; } }else{%>
												<tr >
													<td colspan="9"  class="center text-center">No List Found</td>
												</tr>
												<%} %>
												</tbody>
												</table>
												</div>
											</div>
						</div>
					</div>
				</div>
			</div>	
	</div>
			</div>
		</div>
	</div>

	<div class="modal fade" id="LevelModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
	  <div class="modal-dialog modal-lg" role="document">
	    <div class="modal-content">
	      <div class="modal-heade md-bg" >
	        <h5 class="modal-title title-styl" id="exampleModalLabel" >Set Milestone Level</h5>
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
	          <span aria-hidden="true">&times;</span>
	        </button>
	      </div>
	      <div class="modal-body">
	      	<div class="row">
	      		<div class="col-md-4">
	      			<h6><b>Project : </b>
	      			<%for(int z=0;z<projectidlist.size();z++){ %>
	      				<%if(z==0){ %><%=ProjectDetail.get(z)[1] %> <%} %> 
	      			<%} %>
	      			</h6>
	      		</div>
	      		<div class="col-md-3">
	      			<h6>
	      				<b>Committee :</b> 
	      				<%=committee.getCommitteeShortName().trim().toUpperCase() %>
	      			</h6>
	      		</div>
	      		<div class="col-md-1"><b>Level</b></div>
	      		<div class="col-md-4">
	      			<select class="form-control" name="LevelValue"  required="required" data-live-search="true" data-container="body" id="levelvalue">
						<option <%if(levelid.equalsIgnoreCase("1")) {%> selected <%} %>   value="1">Level A</option>
						<option <%if(levelid.equalsIgnoreCase("2")) {%> selected <%} %> value="2">Level B</option>
						<option <%if(levelid.equalsIgnoreCase("3")) {%> selected <%} %> value="3">Level C</option>
						<option <%if(levelid.equalsIgnoreCase("4")) {%> selected <%} %> value="4">Level D</option>
						<option <%if(levelid.equalsIgnoreCase("5")) {%> selected <%} %> value="5">Level E</option>
					</select>	
	      		</div>
	      		
	      	</div>
	      </div>
	      <div class="modal-footer text-danger" >
	        Note : Upto Selected Milestone Levels Are Showing in Breifing Paper 5, 6 and 9 points.
	      </div>
	    </div>
	  </div>
	</div>
	

	<div class="modal fade" id="recdecmodel" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
			<div class="modal-dialog modal-dialog-centered md-fade1" role="document">
				<div class="modal-content min-height1" >
				    <div class="modal-header div-bg" >
				    	<h4 class="modal-title h4-color" id="model-card-header" > <span id="val1"></span> </h4>
				        <button type="button" class="close" data-dismiss="modal" aria-label="Close" >
				          <span aria-hidden="true">&times;</span>
				        </button>
				    </div>
				     <div class="modal-body">
		  	      		<div class="row">
							<div class="col-md-12" > 
									<span id="recdecdata"></span>
		  	      		    </div>
		  	      		</div>
  	      				</div>
				</div>
			</div>
		</div>


<form method="POST" action="FileUnpack.htm"  id="downloadform" target="_blank"> 
	<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
	<input type="hidden" name="FileUploadId" id="FileUploadId" value="" />
</form>
<form method="get" action="AgendaDocLinkDownload.htm"  id="downloadform1" target="_blank"> 
	<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
	<input type="hidden" name="filerepid" id="filerepid" value="" />
</form>
<form method="POST" action="MilestoneLevelUpdate.htm"  id="milestonelevelform" > 
	<input type="hidden" name="projectid" id="projectid">
	<input type="hidden" name="committeeid" id="committeeid" >
	<input type="hidden" name="milestonelevelid" id="milestonelevelid" />
	<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
</form>	

<!--  -----------------------------------------------Tech data attachment js ---------------------------------------------- -->

<!-- -------------------------------------------- Risk Types Modal  -------------------------------------------------------- -->

		<div class="modal fade" id="RiskTypesModal" tabindex="-1" role="dialog" aria-labelledby="RiskTypesModalCenterTitle" aria-hidden="true">
			<div class="modal-dialog modal-dialog-centered max-width60 " >
		
				<div class="modal-content" >
					   
				    <div class="modal-header div-bg">
				      
				    	<h4 class="modal-title h4-color" >Risk Types</h4>
	
				        <button type="button" class="close" data-dismiss="modal" aria-label="Close" >
				          <span aria-hidden="true">&times;</span>
				        </button>
				        				        
				    </div>
					<div class="modal-body md-body"  >
							
							<div class="card-body cd-body" >
			
								<div class="row" align="center">
									<div class="table-responsive"> 
										<table class="table table-bordered table-hover table-striped table-condensed width-70">
											<thead>
												<tr>
													<th class="width-10">SN</th>
													<th class="width-20">Risk Type</th>
													<th class="width-70">Description</th>
												</tr>
											</thead>
											<tbody>
												<% int riskcount=0;
												for(Object[] risktype : RiskTypes ){ %>
												<tr>
													<td class="text-center"><%=++riskcount %></td>
													<td class="text-center"><b>I<%=risktype[2] %></b></td>
													<td>Internal <%=(risktype[1].toString()) %></td>
												</tr>
												<%} %>
												<%for(Object[] risktype : RiskTypes ){ %>
												<tr>
													<td class="text-center"><%=++riskcount %></td>
													<td class="text-center"><b>E<%=risktype[2] %></b></td>
													<td>External <%=risktype[1] %></td>
												</tr>
												<%} %>
											</tbody>
										</table>
									</div>	
											
				             	</div>					
							</div>
					</div>
				</div>
			</div> 
		</div>
<!-- --------------------------------------------  Risk Types Modal End  -------------------------------------------------------- -->

<!-- File Repo Modal -->
<div class="modal fade" id="pdfModal" tabindex="-1" role="dialog" aria-labelledby="pdfModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content shadow-lg border-0">
      <div class="modal-header bg-primary text-white">
        <h5 class="modal-title" id="modalTitleId"><i class="fa fa-folder-open"></i></h5>
        <button type="button" class="close text-white" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <!-- Folder Tree List -->
         <ul class="list-group folder-tree" id="folderTree"></ul>
      </div>
	  <div class="modal-footer">
		 <div class="font-weight500 text-danger">Note - Please upload PDF files only and PDF size should be smaller than 10mb.</div>
	  </div>
    </div>
  </div>
</div>





<script>
var projectId='<%=projectid%>';
var techDataId="";
var attachmentId="";
var projectName="";
$( document ).ready(function() {
	$.ajax({
	    url: 'getAttachmentId.htm',
	    type: 'GET',
	    data: {projectid: projectId},
	    success: function(response) {
	    	var result= JSON.parse(response);
	    	attachmentId=result[1];
	    	techDataId=result[0];
	    },
	  })
});
function openMainModal(projectid) {
    $.ajax({
        type: "GET",
        url: "FileRepMasterListAllAjax.htm",
        data: { projectid: projectid },
        success: function(result) {
            var data = JSON.parse(result);
            var folderMap = {};
            var html = '';
            if (data.length > 0) {
                projectName = data[0][4];
           }

            // First pass: build main folders
            for (var i = 0; i < data.length; i++) {
                var mainId = data[i][0];
                var parentId = data[i][1];
                var name = data[i][3];

                if (parentId === 0) { // Main Folder
                    folderMap[mainId] = { name: name, subfolders: [] };
                }
            }

            // Second pass: attach subfolders
            for (var j = 0; j < data.length; j++) {
                var subId = data[j][0];
                var subParentId = data[j][1];
                var subName = data[j][3];

                if (subParentId !== 0 && folderMap[subParentId]) { // Subfolder
                    folderMap[subParentId].subfolders.push({ id: subId, name: subName });
                }
            }

            // Generate HTML
            if (folderMap && Object.keys(folderMap).length > 0) {
                for (var mainId in folderMap) {
                    if (folderMap.hasOwnProperty(mainId)) {
                        html += '<li class="list-group-item folder-item" data-id="' + mainId + '" onclick="toggleFolder(this, ' + mainId + ', '+ projectid +', \'mainLevel\')">';
                        html += '<i class="fa fa-folder folder-icon text-warning"></i> ' + folderMap[mainId].name;
                        html += '<ul class="list-group subfolder display-none">';

                        var subfolders = folderMap[mainId].subfolders;
                        for (var k = 0; k < subfolders.length; k++) {
                            var sub = subfolders[k];
                            html += '<li class="list-group-item folder-item" data-id="' + sub.id + '" onclick="toggleFolder(this, ' + sub.id + ', '+ projectid +', \'subLevel\')">';
                            html += '<i class="fa fa-folder folder-icon text-warning"></i> ' + sub.name;
                            html += '<ul class="list-group subfolder display-none" id="subfolder-files-' + sub.id + '" ></ul>';
                            html += '</li>';
                        }

                        html += '<div class="display-none" id="mainfolder-files-' + mainId + '" ></div>';
                        html += '</ul></li>';
                    }
                }
            }else {
                html += '<div>No Data Available.</div></br>';
                html += '<div>Please go to <span class="sapncolor">Document Repository Module &rarr; Document Rep Master</span>, create a folder, and upload pdfs.</div></br>';
            }

            $('.folder-tree').html(html);
            $('#pdfModal').modal('show');
            if (projectName !== undefined && projectName.trim() !== '') {
                $('#modalTitleId').text('PDF Files Explorer for ' + projectName);
            }else{
            	$('#modalTitleId').text('PDF Files Explorer');
            }
        }
    });
}

function toggleFolder(element, folderId, projecId,  type) {
	
    if ($(event.target).closest('.file-item').length > 0 || $(event.target).hasClass('pdf-check')) {
        return;
    }
    event.stopPropagation(); // Prevent parent toggling

    var $elem = $(element);
    var $icon = $elem.children('.folder-icon');
    var $subfolder = $elem.children('ul.subfolder');

    if ($subfolder.is(':visible')) {
        $subfolder.slideUp(200);
        $elem.removeClass('open');
        $icon.removeClass('fa-folder-open').addClass('fa-folder');
    } else {
        $subfolder.slideDown(200);
        $elem.addClass('open');
        $icon.removeClass('fa-folder').addClass('fa-folder-open');

        // Load files if not loaded yet
        var fileContainerId = '';
        if (type === 'mainLevel') {
            fileContainerId = '#mainfolder-files-' + folderId;
        } else {
            fileContainerId = '#subfolder-files-' + folderId;
        }

        if ($(fileContainerId).is(':empty')) {
            loadFolderFiles(folderId, projecId,  type);
        }
    }
}

function loadFolderFiles(folderId, projecId, type) {
    $.ajax({
        type: "GET",
        url: "getOldFileDocNames.htm",
        data : {
   			projectId : projecId,
   			fileId : folderId,
   			fileType : type,
	    },
        success: function(result) {
            var data = JSON.parse(result);
            var html = '';

            for (var i = 0; i < data.length; i++) {
                var fileName = data[i][6];
                html += '<li class="list-group-item file-item">';
                html += '<input type="checkbox" class="pdf-check mr-2" id="checkId'+data[i][0]+'" value="' + data[i][7] + '" onclick="singleSelect(this)"';
                if(data[i][7] != 0 && attachmentId === data[i][7]) {
                    html += ' checked disabled';
                }
                html += '/>';
                html += '<i class="fa fa-file-pdf-o text-danger"></i> ' + fileName;
                html += '<span class="text-muted font-size13" > Ver '+data[i][4]+'.'+data[i][5]+'</span>';
                html += '<i class="fa fa-download i-downlaod"  onclick="fileDownload(' + data[i][7] + ', \'' + type + '\')"></i>';
                html += '<i class="fa fa-upload i-upload" aria-hidden="true"  onclick="fileUpload(\''+data[i][0]+'\')"></i></button><br/>';
                html += '<label for="fileInput" class="lb-input" id="uploadlabel'+data[i][0]+'" >'
                html += '<input type="file" name="docFileInput" id="fileInput'+data[i][0]+'" required="required"  accept="application/pdf"/> '
                html += '<button type="button" class="btn btn-sm back" onclick="fileSubmit(\''+type+'\',\''+data[i][0]+'\',\''+data[i][2]+'\',\''+data[i][3]+'\',\''+data[i][4]+'\',\''+data[i][5]+'\',\''+data[i][6]+'\')">Upload</button>'
                html += '</label>'
                html += '</li>';
            }

            if (type === 'mainLevel') {
                $('#mainfolder-files-' + folderId).html(html).show();
            } else {
                $('#subfolder-files-' + folderId).html(html).show();
            }
        }
    });
}

// Allow only one checkbox to be selected at a time
function singleSelect(checkbox) {
	console.log(checkbox.id);
    $('.pdf-check').not(checkbox).not(':disabled').prop('checked', false);
}

function fileDownload(fileId, fileType) {
    $.ajax({
        url: 'fileDownload.htm/' + fileId + '?fileType=' + encodeURIComponent(fileType),
        type: 'GET',
        xhrFields: {
            responseType: 'blob'
        },
        success: function (data, status, xhr) {
        	  const blob = new Blob([data], { type: 'application/pdf' });
              const blobUrl = URL.createObjectURL(blob);
              const viewerUrl = '<%=request.getContextPath()%>/view/filerepo/pdfViewer.jsp?url=' + encodeURIComponent(blobUrl);
              window.open(viewerUrl, '_blank');
              setTimeout(() => URL.revokeObjectURL(blobUrl), 5000);
        },
        error: function (xhr, status, error) {
		     Swal.fire({
			        icon: 'error',
			        title: 'Error',
			        text: 'Failed to download/open file.',
			 });
        }
    });
}

// Event delegation for dynamically added checkboxes
$(document).on('change', '.pdf-check', function() {
  // Logic to allow only one checkbox to be checked at a time within the list
  $('.pdf-check').not(this).not(':disabled').prop('checked', false);
  // Send AJAX request with the selected checkbox value
  var selectedValue = $(this).val();
  var projectid = <%=projectid%>;
	  if(selectedValue){
	     Swal.fire({
	            title: 'Are you sure to linking?',
	            icon: 'question',
	            showCancelButton: true,
	            confirmButtonColor: 'green',
	            cancelButtonColor: '#d33',
	            confirmButtonText: 'Yes'
	        }).then((result) => {
	            if (result.isConfirmed) {
				  $.ajax({
					    url: 'submitCheckboxFile.htm', 
					    type: 'GET',
					    data: { attachid: selectedValue,techDataId: techDataId,projectid: projectid},
					    success: function(response) {
					      Swal.fire({
					        icon: 'success',
					        title: 'Success',
					        text: 'Document linked successfully!',
					        allowOutsideClick :false
					      });
					      $('#pdfModal').hide();
					      $('.swal2-confirm').click(function (){
					           location.reload();
					       	})
					    },
					    error: function() {
					      Swal.fire({
					        icon: 'error',
					        title: 'Error',
					        text: 'An error occurred while submitting the checkbox selection.',
					      });
					    }
				});
	          }else{
	        	  $('.pdf-check').not(':disabled').prop('checked', false);
	          }
	       });
	  }else{
		  Swal.fire({
		        icon: 'error',
		        title: 'Error',
		        text: 'An error occurred while submitting the checkbox selection.',
		  }); 
	  }
});

$('#levelvalue').on('change', function(){
	$('#milestonelevelid').val($(this).find(":selected").val());
	$('#projectid').val(<%=projectid%>);
	$('#committeeid').val(<%=committeeid%>);
	$('#milestonelevelform').submit();
})

$( document).on("click", ".milestonemodal", function () {
	var milId = $(this).data('id');
    $('.milestonemodalwhole').hide();
    $('.collapse').removeClass('show'); 
    $('#row'+milId.charAt(milId.length-1)).click();
 	$('#'+milId).show();
});

function milactivitychange(val){
	if(val.value=='A'){
		$('#milestonechangetableajax').hide();
		$('#milestoneactivitychangetable').show();
	}else{
	var Proid = <%=projectid%>;	
	$('#milestoneactivitychangetable').hide();
	
	 $.ajax({
		type : "GET",
		url : "MilestoneActivityChange.htm",
		data : {
			projectid : Proid,
			milactivitystatusid : val.value,			
		},
		datatype: 'json',
		success : function(result)
			{
				var result= JSON.parse(result);
				var values= Object.keys(result).map(function(e){
					return result[e];
				})	
							
				var s = "<table id='milestonechangetableajax' class='tb-sub'><tr><th  class='width30'>SN</th><th  class='max-width40'>MS No.</th><th  class='max-width230'>Milestones </th><th  class='max-width80'> Original PDC </th><th  class='max-width80'> Revised PDC</th>"
							+ "<th  class='max-width50'>Progress</th><th  class='max-width70'> Status</th><th  class='max-width70'> Remarks</th></tr>";
				
							if(values[0].length==0){
								s+= "<tr><td colspan=8' class='text-center' > Nil</td></tr>";
							}else{
								for(var i=0;i<values[0].length;i++){
									 if(parseInt(values[0][i][12])>0){ 
										s+= "<tr><td  class='max-width30'>" +parseInt(i+1)+ "</td><td  class='max-width40'>M"+values[0][i][2]+"</td><td  class='max-width230'>"+values[0][i][3]+"</td><td  class='max-width80' >"+formatDate(values[0][i][5])+" </td><td  class='max-width80'>"+formatDate(values[0][i][7])+"</td>"
										+"<td  class='max-width50'>"+values[0][i][12]+"</td><td  class='max-width70'>"+values[0][i][11]+"	</td><td  class='max-width70'>"+values[0][i][13]+"</td></tr>";
									 } 
								}
							}
						s+="</table>"	
				$('#milestoneactivitychange').html(s);

			}
	}) 		
	
	}
}
function milactivitychange6(val){
	if(val.value=='A'){
		$('#milestonechangetableajax6').hide();
		$('#milestoneactivitychangetable6').show();
	}else{
	var Proid = <%=projectid%>;	
	$('#milestoneactivitychangetable6').hide();
	
	 $.ajax({
		type : "GET",
		url : "MilestoneActivityChange.htm",
		data : {
			projectid : Proid,
			milactivitystatusid : val.value,			
		},
		datatype: 'json',
		success : function(result)
			{
				var result= JSON.parse(result);
				var values= Object.keys(result).map(function(e){
					return result[e];
				})	
				var s = "<table id='milestonechangetableajax6' class='tb-sub'><tr><th  class='width30'>SN</th><th  class='max-width40'>MS No.</th><th  class='max-width230'>Milestones </th><th  class='max-width80'> Original PDC </th><th  class='max-width80'> Revised PDC</th>"
							+ "<th  class='max-width50 '>Progress</th><th  class='max-width70'> Status</th><th  class='max-width70'> Remarks</th></tr>";
							if(values[0].length==0){
								s+= "<tr><td colspan=8' class='text-center' > Nil</td></tr>";
							}else{
								for(var i=0;i<values[0].length;i++){
									 if(parseInt(values[0][i][12])>0){ 
										
										s+= "<tr><td  class='max-width30'>" +parseInt(i+1)+ "</td><td  class='max-width40'>M"+values[0][i][2]+"</td><td  class='max-width230'>"+values[0][i][3]+"</td><td  class='max-width80' >"+formatDate(values[0][i][5])+" </td><td  class='max-width80'>"+formatDate(values[0][i][7])+"</td>"
										+"<td  class='max-width50'>"+values[0][i][12]+"</td><td  class='max-width70'>"+values[0][i][11]+"	</td><td  class='max-width70'>"+values[0][i][13]+"</td></tr>";

									 } 
								}
							}
						s+="</table>"	
				$('#milestoneactivitychange').html(s);

			}
		
	}) 		
	
	}
	
}

	 function formatDate(date) {
		    var d = new Date(date),
		        month = '' + (d.getMonth() + 1),
		        day = '' + d.getDate(),
		        year = d.getFullYear();

		    if (month.length < 2) 
		        month = '0' + month;
		    if (day.length < 2) 
		        day = '0' + day;

		    return [day, month, year].join('-');
		}

</script>

<script type="text/javascript">
function FileDownload(fileid1)
{
	$('#FileUploadId').val(fileid1);
	$('#downloadform').submit();
}
function FileDownload1(fileid1)
{
	$('#filerepid').val(fileid1);
	$('#downloadform1').submit();
}
</script>

<!--  -----------------------------------------------agenda attachment js ---------------------------------------------- -->
			
	<% for(int z=0;z<projectidlist.size();z++){ %>
		<script>
								    	  
									function chartprint_<%=projectidlist.get(z)%>(type,interval){ 
								    	  var data = [
 											<%for(Object[] obj : ganttchartlist.get(z)){%>
								    		  {
								    		    id: "<%=obj[3]%>",
								    		    name: "<%=(obj[2].toString()) %>",
								    		    <%if(!obj[9].toString().equalsIgnoreCase("0") && !obj[9].toString().equalsIgnoreCase("1")){ %>
								    		    baselineStart: "<%=obj[6]%>",
								    		    baselineEnd: "<%=obj[7]%>",
								    		    baseline: {fill: "#f25287 0.5", stroke: "0.5 #dd2c00"},
								    		    actualStart: "<%=obj[4]%>",
								    		    actualEnd: "<%=obj[5]%>",
								    		    actual: {fill: "#29465B", stroke: "0.8 #29465B"},
								    		    baselineProgressValue: "<%= Math.round((int)obj[8])%>%",
								    		    progress: {fill: "#81b214 0.0", stroke: "0.0 #150e56"},
								    		    progressValue: "<%= Math.round((int)obj[8])%>%",
								    		    rowHeight: "55",
								    		    <%}else{%>
								    		    baselineStart: "<%=obj[4]%>",
								    		    baselineEnd: "<%=obj[5]%>",
								    		    baseline: {fill: "#29465B", stroke: "0.8 #29465B"},
								    		    baselineProgressValue: "<%= Math.round((int)obj[8])%>%",
								    		    progress: {fill: "#81b214 0.0", stroke: "0.0 #150e56"},
								    		    progressValue: "<%= Math.round((int)obj[8])%>%",
								    		    rowHeight: "55",
								    		    <%}%>
								    		  },
								    		  
								    		  <%}%>
								    	
								    		  ];
								    		 
								    		// create a data tree
								    		var treeData = anychart.data.tree(data, "as-tree");
								    		// create a chart
								    		var chart = anychart.ganttProject();
								    		// set the data
								    		chart.data(treeData);   
								        	// set the container id
								        	chart.container("containers_<%=projectidlist.get(z)%>");  
								        	// initiate drawing the chart
								        	chart.draw();    
								        	// fit elements to the width of the timeline
								        	chart.fitAll();
								        
								        	 chart.getTimeline().tooltip().useHtml(true);   
								        	 
								        	  var timeline = chart.getTimeline();
											   // configure labels of elements
											   timeline.elements().labels().fontWeight(600);
											   timeline.elements().labels().fontSize("14px");
											   timeline.elements().labels().fontColor("#FF6F00");
								        	 
										        chart.getTimeline().tooltip().format(
									        		 function() {
									        		        var actualStart = this.getData("actualStart") ? this.getData("actualStart") : this.getData("baselineStart");
									        		        var actualEnd = this.getData("actualEnd") ? this.getData("actualEnd") : this.getData("baselineEnd");
									        		        var reDate=this.getData("actualStart") ;
									        		   
									        		        var html="";
									        		        if(reDate===undefined){
									        		        	html="";
									        		        	html= "<span class='span-1'> Actual : " +
									        		               anychart.format.dateTime(actualStart, 'dd MMM yyyy') + " - " +
									        		               anychart.format.dateTime(actualEnd, 'dd MMM yyyy') + "</span><br>" +
									        		               "Progress: " + this.getData("baselineProgressValue") + "<br>"
									        		        }else{
									        		        	html="";
									        		        html="<span class='span-1'> Actual : " +
									        		               anychart.format.dateTime(actualStart, 'dd MMM yyyy') + " - " +
									        		               anychart.format.dateTime(actualEnd, 'dd MMM yyyy') + "</span><br>" +
									        		               "<span class='span-1'> Revised : " +
									        		               anychart.format.dateTime(this.getData("baselineStart"), 'dd MMM yyyy') + " - " +
									        		               anychart.format.dateTime(this.getData("baselineEnd"), 'dd MMM yyyy') + "</span><br>" +
									        		               "Progress: " + this.getData("baselineProgressValue") + "<br>"
									        		        }
									        		        
									        		        return html;
									        		    }
										        		
										        ); 
								       
								        /* Title */
								        
								        var title = chart.title();
										title.enabled(true);
										title.text("<%=ProjectDetail.get(z)[2] %> ( <%=ProjectDetail.get(z)[1] %> ) Gantt Chart");
										title.fontColor("#64b5f6");
										title.fontSize(18);
										title.fontWeight(600);
										title.padding(5);
										<%-- <%} %> --%>
								        chart.rowHoverFill("#8fd6e1 0.3");
								        chart.rowSelectedFill("#8fd6e1 0.3");
								        chart.rowStroke("0.5 #64b5f6");
								        chart.columnStroke("0.5 #64b5f6");
								        chart.defaultRowHeight(35);
								     	chart.headerHeight(90);
								     	
								     	/* Hiding the middle column */
								     	chart.splitterPosition("17.4%");
								     	
								     	var dataGrid = chart.dataGrid();
								     	dataGrid.rowEvenFill("gray 0.3");
								     	dataGrid.rowOddFill("gray 0.1");
								     	dataGrid.rowHoverFill("#ffd54f 0.3");
								     	dataGrid.rowSelectedFill("#ffd54f 0.3");
								     	dataGrid.columnStroke("2 #64b5f6");
								     	dataGrid.headerFill("#64b5f6 0.2");
								     
								     	/* Title */
								     	var column_1 = chart.dataGrid().column(0);
								    	column_1.labels().fontWeight(600);
								     	column_1.labels().useHtml(true);
								     	column_1.labels().fontColor("#055C9D");
								     	
								     	var column_2 = chart.dataGrid().column(1);
								     	column_2.title().text("Milestone");
								     	column_2.title().fontColor("#145374");
								     	column_2.title().fontWeight(600);
								     	
										chart.dataGrid().column(0).width(25);
								     	
								     	chart.dataGrid().tooltip().useHtml(true);    
								        
								     	if(interval==="year"){
								     		/* Yearly */
									     	chart.getTimeline().scale().zoomLevels([["year"]]);
									     	var header = chart.getTimeline().header();
									     	header.level(2).format("{%value}-{%endValue}");
									     	header.level(1).format("{%value}-{%endValue}"); 
								     	}
								     	if(interval==="half"){
								     		/* Half-yearly */
									     	chart.getTimeline().scale().zoomLevels([["semester", "year"]]);
									     	var header = chart.getTimeline().header();
									     	header.level(2).format("{%value}-{%endValue}");
									     	var header = chart.getTimeline().header();
									     	header.level(0).format(function() {
								     			var duration = '';
								     			if(this.value=='Q1')
								     				duration='H1';
								     			if(this.value=='Q3')
								     				duration='H2'
								     		  return duration;
								     		});
								     	}
								     	if(interval==="quarter"){
								     		/* Quarterly */
									     	chart.getTimeline().scale().zoomLevels([["quarter", "semester","year"]]);
									     	var header = chart.getTimeline().header();
									     	header.level(1).format(function() {
								     			var duration = '';
								     			if(this.value=='Q1')
								     				duration='H1';
								     			if(this.value=='Q3')
								     				duration='H2'
								     		  return duration;
								     		});
								     	}
								     	if(interval==="month"){
								     		/* Monthly */
									     	chart.getTimeline().scale().zoomLevels([["month", "quarter","year"]]);
								     	}
								     	else if(interval===""){
								     		/* Quarterly */
									     	chart.getTimeline().scale().zoomLevels([["quarter", "semester","year"]]);
									     	var header = chart.getTimeline().header();
									     	header.level(1).format(function() {
								     			var duration = '';
								     			if(this.value=='Q1')
								     				duration='H1';
								     			if(this.value=='Q3')
								     				duration='H2'
								     		  return duration;
								     		});
								     	}
								     	/* chart.getTimeline().scale().fiscalYearStartMonth(4); */
								     	/* Header */
								     	var header = chart.getTimeline().header();
								     	header.level(0).fill("#64b5f6 0.2");
								     	header.level(0).stroke("#64b5f6");
								     	header.level(0).fontColor("#145374");
								     	header.level(0).fontWeight(600);
								     	/* Marker */
								     	var marker_1 = chart.getTimeline().lineMarker(0);
								     	marker_1.value("current");
								     	marker_1.stroke("2 #dd2c00");
								     	/* Progress */
								     	var timeline = chart.getTimeline();
								     	timeline.tasks().labels().useHtml(true);
								     	timeline.tasks().labels().format(function() {
								     	  if (this.progress == 1) {
								     	    return "<span class='ret-span'></span>";
								     	  } else {
								     	    return "<span class='ret-span2'></span>";
								     	  }
								     	});
								     	
								    // calculate height
								     	var traverser = treeData.getTraverser();
								        var itemSum = 0;
								        var rowHeight = chart.defaultRowHeight();
								        while (traverser.advance()){
								           if (traverser.get('rowHeight')) {
								          itemSum += traverser.get('rowHeight');
								        } else {
								        	itemSum += rowHeight;
								        }
								        if (chart.rowStroke().thickness != null) {
								        	itemSum += chart.rowStroke().thickness;
								        } else {
								          itemSum += 1;
								        }
								        }
								        itemSum += chart.headerHeight();
								        var menu = chart.contextMenu();
								          
									} 
		 $( document ).ready(function(){
	    	  chartprint_<%=projectidlist.get(z)%>('type','');
	      })
	      function ChartPrint_<%=projectidlist.get(z)%>(){
		   		console.log("#interval_<%=projectidlist.get(z) %>");
	    	  var interval_<%=projectidlist.get(z) %> = $("#interval_<%=projectidlist.get(z) %>").val();
	    	  $('#containers_<%=projectidlist.get(z) %>').empty();
	    	  chartprint_<%=projectidlist.get(z)%>('print',interval_<%=projectidlist.get(z) %>);
	     }
				$('#interval_<%=projectidlist.get(z) %>').on('change',function(){
					$('#containers_<%=projectidlist.get(z) %>').empty();
					var interval_<%=projectidlist.get(z) %> = $("#interval_<%=projectidlist.get(z) %>").val()
					chartprint_<%=projectidlist.get(z)%>('type',interval_<%=projectidlist.get(z) %>);
					
				})
		</script>
	<% } %>
<script type="text/javascript">
function submitForm(frmid)
{ 
	        $('body').css("filter", "blur(0.8px)");
	        $('#main').hide();
	        $('#spinner').show();
	document.getElementById(frmid).submit(); 
} 
</script>
<script type="text/javascript">
$('.edititemsdd').select2();
$('.items').select2();
$("table").on('click','.tr_clone_addbtn' ,function() {
   $('.items').select2("destroy");        
   var $tr = $('.tr_clone').last('.tr_clone');
   var $clone = $tr.clone();
   $tr.after($clone);
   $('.items').select2();
   $clone.find('.items' ).select2('val', '');    
   $clone.find("input").val("").end();
   /* $clone.find("input:number").val("").end();
   	  $clone.find("input:file").val("").end() 
   */  
});
</script>
<script type="text/javascript">
var editor_config = {
	maxlength: '4000',
	toolbar: [{
	          name: 'clipboard',
	          items: ['PasteFromWord', '-', 'Undo', 'Redo']
	        },
	        {
	          name: 'basicstyles',
	          items: ['Bold', 'Italic', 'Underline', 'Strike', 'RemoveFormat', 'Subscript', 'Superscript']
	        },
	        {
	          name: 'links',
	          items: ['Link', 'Unlink']
	        },
	        {
	          name: 'paragraph',
	          items: ['NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote']
	        },
	        {
	          name: 'insert',
	          items: ['Image', 'Table']
	        },
	        {
	          name: 'editing',
	          items: ['Scayt']
	        },
	        '/',
	        {
	          name: 'styles',
	          items: ['Format', 'Font', 'FontSize']
	        },
	        {
	          name: 'colors',
	          items: ['TextColor', 'BGColor', 'CopyFormatting']
	        },
	        {
	          name: 'align',
	          items: ['JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock']
	        },
	      
	      ],
	    removeButtons: 'Underline,Strike,Subscript,Superscript,Anchor,Styles,Specialchar',
		customConfig: '',
		disallowedContent: 'img{width,height,float}',
		extraAllowedContent: 'img[width,height,align]',
		height: 300,
		contentsCss: [CKEDITOR.basePath +'mystyles.css' ],
		bodyClass: 'document-editor',
		format_tags: 'p;h1;h2;h3;pre',
		removeDialogTabs: 'image:advanced;link:advanced',
		stylesSet: [
			{ name: 'Marker', element: 'span', attributes: { 'class': 'marker' } },
			{ name: 'Cited Work', element: 'cite' },
			{ name: 'Inline Quotation', element: 'q' },
			{
				name: 'Special Container',
				element: 'div',
				styles: {
					padding: '5px 10px',
					background: '#eee',
					border: '1px solid #ccc'
				}
			},
			{
				name: 'Compact table',
				element: 'table',
				attributes: {
					cellpadding: '5',
					cellspacing: '0',
					border: '1',
					bordercolor: '#ccc'
				},
				styles: {
					'border-collapse': 'collapse'
				}
			},
			{ name: 'Borderless Table', element: 'table', styles: { 'border-style': 'hidden', 'background-color': '#E6E6FA' } },
			{ name: 'Square Bulleted List', element: 'ul', styles: { 'list-style-type': 'square' } },

]
		
	} ;
	
CKEDITOR.replace('ckeditor', editor_config );
CKEDITOR.replace('ckeditor1', editor_config );

$(document).ready(function() {
	var locked=0;
	   var editAbstract=CKEDITOR.instances.ckeditor;
	   editAbstract.on("key",function(e) {      
	      var maxLength=e.editor.config.maxlength;
	      e.editor.document.on("keyup",function() {KeyUp(e.editor,maxLength,"letterCount",e);});
	      e.editor.document.on("paste",function() {KeyUp(e.editor,maxLength,"letterCount",e);});
	      e.editor.document.on("blur",function() {KeyUp(e.editor,maxLength,"letterCount",e);});
	   },editAbstract.element.$);
	   //function to handle the count check
	   function KeyUp(editorID,maxLimit,infoID,editor) 
	   {
		   var text=editor.editor.getData().replace(/<("[^"]*"|'[^']*'|[^'">])*>/gi, '').replace(/^\s+|\s+$/g, '');
		   $("#"+infoID).text(text.length);
		   if( text.length  >= maxLimit )
		   {
		      if ( !locked )
		      {
		    	 // Record the last legal content.
		         editAbstract.fire('saveSnapshot'), 
		         locked = 1;			                      
		         editor.cancel();			         
		      }
		      else if( text.length > maxLimit ){ // Rollback the illegal one.
		    	 alert('Cannot Insert content longer than '+maxLimit+' Characters');
		         editAbstract.execCommand( 'undo' );			         
		      }
		      else{
		    	  locked = 0;
		      }
		   }
	   }   
	});

function RecDecEdit(recdescid ){
	
	 if(recdescid=='0'){
		     CKEDITOR.instances['ckeditor1'].setData("");
			 $("#recdecid").val("");
			 $('#decision').prop('checked',false);
			 $('#recommendation').prop('checked',false);
	 }else{
			$.ajax({
				type : "GET",
				url : "Getrecdecdata.htm",
				data : {
					recdesid : recdescid,
				},
				datatype : 'json',
				success : function(result){
					var result = JSON.parse(result);
					var type = result[2];
					
					if(type=='D'){
						 $('#decision').prop('checked',true);
					}else if(type=='R'){
						 $('#recommendation').prop('checked',true);
					}else{
						 $('#decision').prop('checked',false);
						 $('#recommendation').prop('checked',false);
					}
					$("#recdecid").val(result[0]);
					CKEDITOR.instances['ckeditor1'].setData(result[3]);
				}
			});
	 }
}

function RecDecmodal(recdescid)
{
	$.ajax({
			type : "GET",
			url : "Getrecdecdata.htm",
			data : {
				recdesid : recdescid,
			},
			datatype : 'json',
			success : function(result) {
				var result = JSON.parse(result);
				var type = result[2];
				if(type=='D'){
					$("#val1").html("Decision");
				}else if(type=='R'){
					$("#val1").html("Recommendation");
				}
				$("#recdecdata").html(result[3]);
				$('#recdecmodel').modal('toggle');
			}
	});
	
}
function checkData(formid)
{
	var recdec = CKEDITOR.instances['ckeditor1'].getData(); 
	var fields = $("input[name='darc']").serializeArray();
	if(recdec!='' && fields!=0){
		if( recdec.length>999){
			alert("Data is Too long !");
			return false;
		}else{
			if(confirm("Are you sure to submit!")){
				document.getElementById(formid).submit();
				return true;
			}
		}
	}else{
		alert("Fill all the details!");
		return false;
	}
	
}

function RecDecremove(a){
	$('#recdecId').val(a);
	if(confirm("Are you sure,you want to remove?")){
	$('#remvfrm').submit();
	}else{
		event.preventDefault();
		return false;
	}
}

$('.btn[data-toggle="tooltip"]').tooltip({
    animated: 'fade',
    placement: 'top',
    html : true,
    boundary: 'window'
});

function openEditDiv(a){
    var label = document.getElementById("filelabel"+a);
    if (label.style.display === "none") {
        label.style.display = "inline-block";
    } else {
        label.style.display = "none";
    }
}

function fileUpload(Id){
	 var label = document.getElementById("uploadlabel"+Id);
    if (label.style.display === "none") {
        label.style.display = "inline-block";
    } else {
        label.style.display = "none";
    }
}
</script>
<script>
function fileSubmit(type,repid,mainId,subId,version,release,docName) {
    event.preventDefault();
    var fileInput =  $("#fileInput"+repid)[0].files[0];
    
	 if (fileInput === undefined) {
	       Swal.fire({
	            icon: 'error',
	            title: 'Oops...',
	            text: 'Please select a file to upload!',
	            allowOutsideClick :false
	       });
	       return;
	  }
	// Check if the file is a PDF
	   var fileType = fileInput.type;
	   if (fileType !== 'application/pdf') {
	       Swal.fire({
	           icon: 'error',
	           title: 'Invalid File Type',
	           text: 'Please select a PDF file!',
	           allowOutsideClick: false
	       });
	       return;
	   }
	   // Check if the file size is less than 10MB (10 * 1024 * 1024 bytes)
	   var fileSize = fileInput.size;
	   if (fileSize > 10 * 1024 * 1024) {
	       Swal.fire({
	           icon: 'error',
	           title: 'File Too Large',
	           text: 'Please select a file smaller than 10MB!',
	           allowOutsideClick: false
	       });
	       return;
	   }
	   
       Swal.fire({
            title: 'Are you sure to upload?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: 'green',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes'
        }).then((result) => {
            if (result.isConfirmed) {
		        var projectid = <%= projectid %>;
		        var formData = new FormData();
		        formData.append("file", $("#fileInput"+repid)[0].files[0]);
		        formData.append("fileType", type);
		        formData.append("fileRepId", repid);
		        formData.append("projectid", projectid);
		        formData.append("mainId", mainId);
		        formData.append("subId", subId);
		        formData.append("docName", docName);
		        formData.append("version", version);
		        formData.append("release", release);
		        formData.append("${_csrf.parameterName}", "${_csrf.token}");
		        // Use AJAX to submit the form data
		        $.ajax({
		            url: 'DocFileUpload.htm',
		            type: 'POST',
		            data: formData,
		            contentType: false,
		            processData: false,
		            success: function(response) {
		                if (response === "200") {
		                    Swal.fire({
		                        title: "Success",
		                        text: "File Uploaded Successfully",
		                        icon: "success",
		                        allowOutsideClick: false
		                    });
		                    $('#pdfModal').hide();
		                    $('.swal2-confirm').click(function () {
		                        location.reload();
		                    });
		                } else {
		                    Swal.fire({
		                        icon: 'error',
		                        title: 'Error',
		                        text: 'Unexpected response: ' + response
		                    });
		                }
		            },
		            error: function(xhr, status, error) {
		            	  Swal.fire({
		                      icon: 'error',
		                      title: 'Error',
		                      text: 'An error occurred while uploading the file'
		                  });
		                  console.log(xhr.responseText);
		             }
		        });
        }
    });
}

function removeFileAttch(projectId,techDataId,techAttachId) {
    Swal.fire({
        title: 'Are you sure to remove attachment?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: 'green',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes'
    }).then((result) => {
        if (result.isConfirmed) {
        	 $.ajax({
		            url: 'removeFileAttachment.htm',
		            type: 'POST',
		            datatype: 'json',
		            data: {
		            	 techDataId : techDataId,
		            	 techAttachId : techAttachId,
		            	 projectId : projectId,
		            	 ${_csrf.parameterName} : "${_csrf.token}",
		            },
		            success: function(response) {
		            	  Swal.fire({
				    	       	title: "Success",
				                text: "Attachment Removed Successfully",
				                icon: "success",
				                allowOutsideClick :false
				         		});
		            	  $('.swal2-confirm').click(function (){
		      	                location.reload();
		      	        	})
		            },
		            error: function(xhr, status, error) {
		            	  Swal.fire({
		                      icon: 'error',
		                      title: 'Error',
		                      text: 'An error occurred while removing the file'
		                  });
		                  console.log(xhr.responseText);
		             }
		        });
        }
    });
}

function editRemarks(id) {

    var span = document.getElementById("remarksSpan_" + id);
    var input = document.getElementById("remarksInput_" + id);
    var saveBtn = document.getElementById("saveBtn_" + id);

    if (input.style.display === "none" || input.style.display === "") {
        // Show input
        span.style.display = "none";
        input.style.display = "inline-block";
        saveBtn.style.display = "inline-block";
    } else {
        // Hide input
        span.style.display = "inline-block";
        input.style.display = "none";
        saveBtn.style.display = "none";
    }
}

function saveRemarks(id) {

    var remarks = document.getElementById("remarksInput_" + id).value;
    
    if(!remarks){
		alert("Please Fill the Remarks !");
		return;
    }

    $.ajax({
        url: "UpdateRemarksFromBriefingPaper.htm",
        type: "POST",
        data: {
            id: id,
            remarks: remarks,
            "${_csrf.parameterName}": "${_csrf.token}"
        },
        success: function(response) {
			console.log(response);
            //	location.reload();
        }
    });
}
</script>
</body>