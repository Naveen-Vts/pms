<%@page import="com.vts.pfms.milestone.model.ProjectEconomicImpact"%>
<%@page import="com.vts.pfms.milestone.dto.ProjectUtilizationBriefingDto"%>
<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.nio.file.Paths"%>
<%@page import="java.nio.file.Path"%>
<%@page import="java.time.LocalTime"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.net.Inet4Address"%>
<%@page import="com.vts.pfms.Zipper"%>
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
	pageEncoding="ISO-8859-1"
	import="java.util.*,com.vts.*,java.text.SimpleDateFormat,java.time.LocalDate"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link rel="shortcut icon" type="image/png" href="view/images/drdologo.png">
<jsp:include page="../static/dependancy.jsp"></jsp:include>
<spring:url value="/resources/css/slides-style.css" var="SlidesStyleCSS" />
<link href="${SlidesStyleCSS}" rel="stylesheet" />
<spring:url value="/resources/css/print/briefingPresentation.css" var="briefingPresentation" />     
<link href="${briefingPresentation}" rel="stylesheet" />
<title>Briefing Presentation</title>
</head>
<body  class="slides-container bpbg" id="slides-container">
	<%
	DecimalFormat df = new DecimalFormat("####################.##");
	FormatConverter fc = new FormatConverter();
	SimpleDateFormat sdf = fc.getRegularDateFormat();
	SimpleDateFormat sdf1 = fc.getSqlDateFormat();
    SimpleDateFormat inputFormat = new SimpleDateFormat("ddMMMyyyy",Locale.ENGLISH);
    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");	
	String todayDate=outputFormat.format(new Date()).toString();
	int addcount = 0;
	NFormatConvertion nfc = new NFormatConvertion();
	Format format = com.ibm.icu.text.NumberFormat.getCurrencyInstance(new Locale("en", "in"));
	String filePath = (String) request.getAttribute("filePath");
	String projectLabCode = (String) request.getAttribute("projectLabCode");
	List<Object[]> projectslist = (List<Object[]>) request.getAttribute("projectslist");
	String projectid = (String) request.getAttribute("projectid");
	Committee committee = (Committee) request.getAttribute("committeeData");
	String committeeid = (String) request.getAttribute("committeeid");
	List<Object[]> projectattributeslist = (List<Object[]>) request.getAttribute("projectattributes");
	List<List<Object[]>> ebandpmrccount = (List<List<Object[]>>) request.getAttribute("ebandpmrccount");
	List<List<Object[]>> milestones = (List<List<Object[]>>) request.getAttribute("milestones");
	List<List<Object[]>> lastpmrcactions = (List<List<Object[]>>) request.getAttribute("lastpmrcactions");
	List<List<Object[]>> lastpmrcminsactlist = (List<List<Object[]>>) request.getAttribute("lastpmrcminsactlist");
	List<List<Object[]>> ganttchartlist = (List<List<Object[]>>) request.getAttribute("ganttchartlist");
	List<Object[]> projectdatadetails = (List<Object[]>) request.getAttribute("projectdatadetails");
	List<List<Object[]>> oldpmrcissueslist = (List<List<Object[]>>) request.getAttribute("oldpmrcissueslist");

	List<List<ProjectFinancialDetails>> projectFinancialDetails = (List<List<ProjectFinancialDetails>>) request.getAttribute("financialDetails");
	List<List<Object[]>> procurementOnDemand = (List<List<Object[]>>) request.getAttribute("procurementOnDemandlist");
	List<List<Object[]>> procurementOnSanction = (List<List<Object[]>>) request.getAttribute("procurementOnSanctionlist");
	List<List<Object[]>> riskmatirxdata = (List<List<Object[]>>) request.getAttribute("riskmatirxdata");
	List<List<Object[]>> actionplanthreemonths = (List<List<Object[]>>) request.getAttribute("actionplanthreemonths");
	List<Object[]> TechWorkDataList = (List<Object[]>) request.getAttribute("TechWorkDataList");
	List<Object[]> ProjectDetail = (List<Object[]>) request.getAttribute("ProjectDetails");
	List<String> projectidlist = (List<String>) request.getAttribute("projectidlist");
	List<Object[]> pdffiles = (List<Object[]>) request.getAttribute("pdffiles");
	List<Object[]> milestoneactivitystatus = (List<Object[]>) request.getAttribute("milestoneactivitystatus");
	List<Object[]> MilestoneList = (List<Object[]>) request.getAttribute("MilestoneActivityList");
	String ProjectId = (String) request.getAttribute("projectid");
	List<TotalDemand> totalprocurementdetails = (List<TotalDemand>) request.getAttribute("TotalProcurementDetails");
	//List<List<Object[]>> ReviewMeetingList = (List<List<Object[]>>) request.getAttribute("ReviewMeetingList");
	//List<List<Object[]>> ReviewMeetingListPMRC = (List<List<Object[]>>) request.getAttribute("ReviewMeetingListPMRC");
	Map<String, List<Object[]>> reviewMeetingListMap = (Map<String, List<Object[]>>) request.getAttribute("reviewMeetingListMap");
	List<List<Object[]>> overallfinance = (List<List<Object[]>>)request.getAttribute("overallfinance");//b
	String IsIbasConnected=(String)request.getAttribute("IsIbasConnected");
	List<List<Object[]>> ProjectRevList = (List<List<Object[]>>) request.getAttribute("ProjectRevList");
	List<List<Object[]>> MilestoneDetails6 = (List<List<Object[]>>) request.getAttribute("milestonedatalevel6");//a
	List<List<TechImages>> TechImages = (List<List<TechImages>>) request.getAttribute("TechImages");
	Object[] committeeMetingsCount =  (Object[]) request.getAttribute("committeeMetingsCount");
	Object[] nextMeetVenue =  (Object[]) request.getAttribute("nextMeetVenue");
	List<Object[]> RecDecDetails = (List<Object[]>)request.getAttribute("recdecDetails");
	List<Object[]> otherMeetingList = (List<Object[]>)request.getAttribute("otherMeetingList");
	LabMaster labInfo=(LabMaster)request.getAttribute("labInfo");
	String lablogo = (String)request.getAttribute("lablogo");
	String Drdologo = (String)request.getAttribute("Drdologo");
	String pdc="";String mainpdc="";
	long ProjectCost = (long) request.getAttribute("ProjectCost");
	String levelid = (String) request.getAttribute("levelid");
	Committee committeeData = (Committee) request.getAttribute("committeeData");
	String CommitteeCode = committeeData.getCommitteeShortName().trim();
	
	Map<Integer,String> committeeWiseMap=(Map<Integer,String>)request.getAttribute("committeeWiseMap");
	//Map<Integer,String> mapEB=(Map<Integer,String>)request.getAttribute("mapEB");

	
	String ProjectCode="";
		for(int i=0;i<projectattributeslist.size();i++){
			ProjectCode = ProjectCode +projectattributeslist.get(i)[0].toString()  ;
			if(i!=projectattributeslist.size()-1)ProjectCode=ProjectCode+"/";
		}
	
	String MeetingNo = CommitteeCode+" #"+(Long.parseLong(committeeMetingsCount[1].toString())+1);
	LocalDate before6months = LocalDate.now().minusDays(committeeData.getPeriodicDuration());

	String thankYouImg = (String)request.getAttribute("thankYouImg");
	List<Object[]> RiskTypes = (List<Object[]>)request.getAttribute("RiskTypes");
	Map<Integer,String> treeMapLevOne =(Map<Integer,String>)request.getAttribute("treeMapLevOne");
	Map<Integer,String> treeMapLevTwo =(Map<Integer,String>)request.getAttribute("treeMapLevTwo");
	List<Object[]> envisagedDemandlist = (List<Object[]>)request.getAttribute("envisagedDemandlist");
	

	List<List<Object[]>> sunsetmilestones = (List<List<Object[]>>) request.getAttribute("sunsetmilestones");

	%>
	
		<%
String scheme = request.getScheme();
String serverName = request.getServerName();
int serverPort = request.getServerPort();
String contextPath = request.getContextPath();

String baseUrl = scheme + "://" + serverName
                 + (serverPort != 80 && serverPort != 443 ? ":" + serverPort : "")
                 + contextPath;


%>
	<div id="presentation-slides" class="carousel slide " data-ride="carousel">

		<div class="carousel-inner" align="center">
			
			<!-- ---------------------------------------- P-0  Div ----------------------------------------------------- -->
			<div class="carousel-item active">
				<div class="content bp-1" align="center" >
					<div class="firstpage"  > 
						<div align="center" ><h2 class="bp-2">Presentation</h2></div>
						<div align="center" ><h3 class="bp-3">for</h3></div>
						<div align="center" >
							<h3 class="bp-4" ><%=CommitteeCode %> #<%=Long.parseLong(committeeMetingsCount[1].toString())+1 %> Meeting </h3>
				   		</div>
						<div align="center" >
							<h3 class="bp-4">
								<%= projectattributeslist.get(0)[1] %> (<%= projectattributeslist.get(0)[0] %>)
								<%if(projectattributeslist.size()>1) {
									for(int item=1;item<projectattributeslist.size();item++){ %> <br>
									<span class="bp-5"><%= projectattributeslist.get(item)[1] %> (<%= projectattributeslist.get(item)[0] %>) (SUB)</span>
							 	<%}} %>
							</h3>
						</div>
						
						<table class="executive home-table bp-6"  >
							<tr>			
								<th colspan="8" class="bp-7">
									<img class="bp-8"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> > 
									<br>
								</th>
								</tr>
						</table>	
						<% if(nextMeetVenue!=null){ %>
							<table class="bp-9"  >
								<tr class="mt-1">
									 <th  class="bp-10"> <u>Meeting Id </u> </th></tr><tr>
									 <th  class="bp-10"> <%=nextMeetVenue[1] %> </th>				
								 </tr>
							</table>
							<table class="bp-9" >
								<tr>
									 <th  class="bp-11"> <u> Meeting Date </u></th>
									 <th  class="bp-11"><u> Meeting Time </u></th>
								</tr>
								<tr>
									<td  class="bp-12"> <b><%=fc.sdfTordf(nextMeetVenue[2].toString())%></b></td>
									<td  class="bp-12"> <b><%=nextMeetVenue[3]/* starttime.format( DateTimeFormatter.ofPattern("hh:mm a") ) */%></b></td>
								</tr>
							</table>
							<table class="bp-13"  >
								<tr class="">
									<th  class="bp-10"> <u>Meeting Venue</u> </th></tr><tr>
									<th  class="bp-10"> <% if(nextMeetVenue[5]!=null){ %><%=nextMeetVenue[5] %> <%}else{ %> - <%} %></th>				
								 </tr>
							</table>
						<%}else{ %>
							<br><br><br><br><br><br><br><br><br>
						<%} %>
						
						<table class="executive home-table bp-15"   >
							<% if(labInfo!=null){ %>
								<tr>
									<th colspan="8" class="bp-16"><%if(labInfo.getLabName()!=null){ %><%=labInfo.getLabName()  %><%}else{ %>LAB NAME<%} %></th>
								</tr>
							<%}%>
							<tr>
								<th colspan="8" class="bp-17"><br>Government of India, Ministry of Defence</th>
							</tr>
							<tr>
								<th colspan="8" class="bp-17">Defence Research & Development Organization</th>
							</tr>
							<tr>
								<th colspan="8" class="bp-17"><%if(labInfo.getLabAddress() !=null){ %><%=labInfo.getLabAddress()  %> , <%=labInfo.getLabCity() %><%}else{ %>LAB ADDRESS<%} %> </th>
							</tr>
						</table>			
					</div>
				</div>
			</div>
			<!-- ---------------------------------------- P-0  Div End----------------------------------------------------- -->
			<!-- ---------------------------------------- P-1  Div ----------------------------------------------------- -->
			<%char ch='a';for (int z = 0; z < projectidlist.size(); z++) {mainpdc="PDC:"+sdf.format(sdf1.parse(projectattributeslist.get(0)[6].toString()));
			List<Object[]> revlist = ProjectRevList.get(z);Object[] projectattributes = projectattributeslist.get(z);pdc=pdc+"(PDC:"+sdf.format(sdf1.parse(projectattributes[6].toString()))+")<br>";%>
			<%if (projectattributes != null) {%>
			<div class="carousel-item ">
				<div class="content-header row ">
					<div class="col-md-1" ><img class="bp-18"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					<div class="col-md-1 bp-19" align="left"  ><b class="bp-20"><%=ProjectCode %></b>
					<h6 class="bp-21"><%=pdc %></h6>
					</div>
					<div class="col-md-8"><h3>1<%if(projectidlist.size()>1) {%>(<%=(char)(ch++)%>)<%} %> . Project Attributes </h3></div>
					<div class="col-md-1 bp-22" align="right"  ><b class="bp-20"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="bp-18"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
				</div>
				<div class="content" align="center">
				
					<b class="bp-23">Project : <%=ProjectDetail.get(z)[1]%><% if (z != 0) { %>(SUB)<% } %></b>
					<hr class="bp-24">
					<table class="subtables bp-25" >
						<tr>
							<td class="bp-26"
								>(a)</td>
							<td class="bp-27"><b>Project
									Title</b></td>
							<td colspan="4" 
								class="bp-28">
								<%=projectattributes[1]%><span class="bp-29">PDC -(<%=sdf.format(sdf1.parse(projectattributes[6].toString()))%>)</span></td>
						</tr>
						<tr>
							<td class="bp-30">(b)</td>
							<td class="bp-31"><b>Project
									Code</b></td>
							<td colspan="4" class="prjattr bp-32"
								><%=projectattributes[0]%>
							</td>
						</tr>
						<tr>
							<td class="bp-30">(c)</td>
							<td class="bp-31"><b>Category</b></td>
							<td colspan="4" class="prjattr bp-32"><%=projectattributes[14]%></td>
						</tr>
						<tr>
							<td class="bp-30">(d)</td>
							<td class="bp-31"><b>Date of Sanction</b></td>
							<td colspan="4" class="prjattr bp-32" ><%=sdf.format(sdf1.parse(projectattributes[3].toString()))%></td>
						</tr>
						<tr>
							<td class="bp-33">(e)</td>
							<td class="bp-31"><b>Nodal
									and Participating Labs</b></td>
							<td colspan="4" class="prjattr bp-32"
								>
								<%if (projectattributes[15] != null) {%><%=projectattributes[15]%><%}%>
							</td>
						</tr>
						<tr>
							<td class="bp-30">(f)</td>
							<td class="bp-31"><b>Objective</b></td>
							<td colspan="4"  class="prjattr bp-32 bp-34"
								>
								<%=projectattributes[4]%></td>
						</tr>
						<tr>
							<td class="bp-30">(g)</td>
							<td class="bp-31"><b>Deliverables</b></td>
							<td colspan="4" class="prjattr bp-32"
								><%=projectattributes[5]%></td>
						</tr>
						<tr>
							<td rowspan="2" class="bp-30">(h)</td>
							<td rowspan="2"  class="bp-31"
								"><b>PDC</b></td>

							<td colspan="2" class="text-center"><!-- Original -->&nbsp;</td>
							<%if (ProjectRevList.get(z).size() > 0) {%>
							<td colspan="2" class="text-center" >Revised</td>
							<%} else {%><td colspan="2"></td><%}%>
						</tr>
						<tr>
							<%
							if (ProjectRevList.get(z).size() > 0) {
							%>
							<td colspan="2"  class="prjattr text-center"><%=sdf.format(sdf1.parse(ProjectRevList.get(z).get(0)[12].toString()))%>
							</td>
							<td colspan="2"  class="prjattr text-center">
								<% if (LocalDate.parse(projectattributes[6].toString()) .isEqual(LocalDate.parse(ProjectRevList.get(z).get(0)[12].toString()))) { %>
									- 
								<% } else { %><%=sdf.format(sdf1.parse(projectattributes[6].toString()))%>
								<% } %>
							</td>
							<%
							} else {
							%>
							<td colspan="2"  class="prjattr text-center"><%=sdf.format(sdf1.parse(projectattributes[6].toString()))%></td>
							<td colspan="2"></td>
							<%
							}
							%>
						</tr>
						<tr>
							<td rowspan="3"
								class="bp-35">(i)</td>
							<td rowspan="3" class="pl-1"><b>Cost
									Breakup( In &#8377; <span class="currency">Lakhs</span>)
							</b></td>

							<%
							if (ProjectRevList.get(z).size() > 0) {
							%>
							<td class="bp-36">RE Cost</td>
							<td  class="prjattr text-center"><%=ProjectRevList.get(z).get(0)[17]%></td>
							<td colspan="2" class="prjattr text-center" ><%=projectattributes[8]%></td>
						</tr>
						<tr>
							<td class="bp-36">FE Cost</td>
							<td  class="prjattr text-center"><%=ProjectRevList.get(z).get(0)[16]%></td>
							<td colspan="2"  class="prjattr text-center"><%=projectattributes[9]%></td>
						</tr>
						<tr>
							<td class="bp-36" >Total Cost</td>
							<td  class="prjattr text-center"><%=ProjectRevList.get(z).get(0)[11]%></td>
							<td colspan="2"  class="prjattr text-center"><%=projectattributes[7]%></td>
						</tr>
						<%
						} else {
						%>
						<td class="bp-36" >RE Cost</td>
						<td class="prjattr "><%=projectattributes[8]%></td>
						<td colspan="2" class="prjattr"></td>
						</tr>
						<tr>
							<td class="bp-36"">FE Cost</td>
							<td class="prjattr"><%=projectattributes[9]%></td>
							<td colspan="2"></td>
						</tr>
						<tr>
							<td class="bp-36">Total Cost</td>
							<td class="prjattr"><%=projectattributes[7]%></td>
							<td colspan="2"></td>
						</tr>
						<%
						}
						%>
						<tr>
							<td class="bp-33">(j)</td>
							<td class="bp-31"><b>No.of Meetings held</b></td>
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
							<td class="bp-33">(k)</td>
							<td class="bp-37"><b>Current
									Stage of Project</b></td>
															  	<%
											   String colorCode = projectdatadetails.get(z)!=null ? (String) projectdatadetails.get(z)[11] : "#77D970";
											   String className = "C" + colorCode.replace("#", "").toUpperCase();
											%>
											<td colspan="4" 
		  									 class="ctm-td <%=className%>"> <%= projectdatadetails.get(z) != null ? "<b class="+4+">" + projectdatadetails.get(z)[10] + "</b>" : "Data Not Found" %>
										</td>

						</tr>
					</table>
				</div></div>
					<%} else {%>
					<div align="center" class="bp-39">Complete ProjectData Not Found</div>
					<%}%><%}%>
			<!-- ----------------------------------------  P-1  Div ----------------------------------------------------- -->
			<!-- ---------------------------------------- P-2a Div ----------------------------------------------------- -->
			<div class="carousel-item ">
						
					<div class="content-header row ">
					<div class="col-md-1" ><img class="bp-18"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					<div class="col-md-1 bp-19" align="left"  ><b class="bp-20"><%=ProjectCode %></b>
					<h6 class="bp-21"><%=pdc %></h6>
					</div>
					<div class="col-md-8"><h3>2. Schematic Configuration - (a) System Configuration</h3></div>
					<div class="col-md-1 bp-22" align="right"  ><b class="bp-20"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="bp-18"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					</div>
				
				<div class="content">
					<%for (int z = 0; z < 1; z++) { %>
					<div align="left" class="bp-40">
						<div align="center">
							<table class="width-100">
								<%if (projectdatadetails.get(z) != null && projectdatadetails.get(z)[3] != null) {%>
								<tr>
									<td class="bp-41">
										<form action="#" method="post" target="_blank">
											<b><%=ProjectDetail.get(z)[1]%><% if (z != 0) { %>(SUB<% } %> : </b>
											<span class="mainsubtitle"></span><span class="anchorlink bp-42" onclick="$('#config<%=ProjectDetail.get(z)[0]%>').toggle();" ><b>As on File Attached</b></span>
											<button type="submit" class="btn btn-sm " formaction="ProjectDataSystemSpecsFileDownload.htm" formmethod="post" formtarget="_blank">
												<i class="fa fa-download fa-lg"></i>
											</button>
											<input type="hidden" name="projectdataid" value="<%=projectdatadetails.get(z)[0]%>" /> 
											<input type="hidden" name="filename" value="sysconfig" /> 
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										</form>
									</td>
								</tr>
								<tr>
								
									<td class="bp-45">
										<%
										Path systemPath = Paths.get(filePath,projectLabCode,"ProjectData",projectdatadetails.get(z)[3].toString());
										File systemfile = systemPath.toFile();
										if(systemfile.exists()){
										if (FilenameUtils.getExtension(projectdatadetails.get(z)[3].toString()).equalsIgnoreCase("pdf")) {%>
<%-- 										<iframe
											src="data:application/pdf;base64,<%=pdffiles.get(z)[0]%>#view=FitV" class="bp-43"
											id="config<%=ProjectDetail.get(z)[0]%>"> </iframe> --%>
											<button class="btn btn-lg btn-link" onclick="showModal('sysconfig','<%=projectdatadetails.get(z)[0]%>')" id="sysconfigbtn"></button>
											<div class="col-md-12" id="sysconfigModal">
											
											</div>
											 <%} else {
											 %>
										<img data-enlargable
										class="bp-44"
										src="data:image/<%=FilenameUtils.getExtension(projectdatadetails.get(z)[3].toString())%>;base64,<%=pdffiles.get(z)[0]%>"
										id="config<%=ProjectDetail.get(z)[0]%>"> <%
										 }}
										 %>
									</td>
								</tr>
								<%
								} else {
								%>
								<tr>
									<td class="bp-41"><b><%=ProjectDetail.get(z)[1]%><% if (z != 0) { %>(SUB<% } %> : </b><span class="mainsubtitle">System Configuration File Not Found</span></td>
								</tr>
								<%}%>
							</table>
						</div>
					</div>
					<%}%>
				</div>
			</div>
			<!-- ----------------------------------------  P-2a Div ----------------------------------------------------- -->
			<!-- ---------------------------------------- P-2b   Div ----------------------------------------------------- -->
			<div class="carousel-item ">
					<div class="content-header row ">
					<div class="col-md-1" ><img class="bp-18"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					<div class="col-md-1 bp-19" align="left"  ><b class="bp-20"><%=ProjectCode %></b>
					<h6 class="bp-21"><%=pdc %></h6>
					</div>
					<div class="col-md-8"><h3>2. Schematic Configuration - (b) System Specifications</h3></div>
					<div class="col-md-1 bp-22" align="right"  ><b class="bp-20"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="bp-18"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					</div>
				<div class="content">
					<%for (int z = 0; z < 1; z++){ %>
					<div align="left" class="ml-1">
						<div align="center">
							<table class="width-100">
								<%if (projectdatadetails.get(z) != null && projectdatadetails.get(z)[4] != null) {%>
								<tr>
									<td class="bp-41">
										<form action="#" method="post" target="_blank">
											<b><%=ProjectDetail.get(z)[1]%> <% if (z != 0) { %>(SUB<% } %> : </b>
											<span class="mainsubtitle"></span><span class="anchorlink bp-42"
												onclick="$('#sysspecs<%=ProjectDetail.get(z)[0]%>').toggle();"><b>As on File Attached</b></span>
											<button type="submit" class="btn btn-sm "
												formaction="ProjectDataSystemSpecsFileDownload.htm"
												formmethod="post" formtarget="_blank">
												<i class="fa fa-download fa-lg"></i>
											</button>
											<input type="hidden" name="projectdataid"
												value="<%=projectdatadetails.get(z)[0]%>" /> <input
												type="hidden" name="filename" value="sysspecs" /> <input
												type="hidden" name="${_csrf.parameterName}"
												value="${_csrf.token}" />
										</form>
									</td>
								</tr>
								<tr>
									<td class=" border-0 text-left">
										<%if (FilenameUtils.getExtension(projectdatadetails.get(z)[4].toString()).equalsIgnoreCase("pdf")) {%>
									<%-- 	<iframe 
											src="data:application/pdf;base64,<%=pdffiles.get(z)[3]%>#view=FitV" class="bp-43"
											id="sysspecs<%=ProjectDetail.get(z)[0]%>" > </iframe>  --%>
											<button class="btn btn-lg btn-link" onclick="showModal('sysspecs','<%=projectdatadetails.get(z)[0]%>')" id="sysspecsbtn">
											</button>	
											<div class="col-md-12" id="sysspecsModal">
											
											</div>
											<%
										 } else {
										 %>
										<img data-enlargable
										class="bp-44"
										src="data:image/<%=FilenameUtils.getExtension(projectdatadetails.get(z)[4].toString())%>;base64,<%=pdffiles.get(z)[3]%>"
										id="sysspecs<%=ProjectDetail.get(z)[0]%>"> <%
										 }
										 %>
									</td>
								</tr>
								<%
								} else {
								%>
								<tr>
									<td class="border-0 text-left"><b><%=ProjectDetail.get(z)[1]%><% if (z != 0) { %>(SUB<% } %> : </b><span class="mainsubtitle"> System Specifications File Not Found</span></td>
								</tr>
								<%}%>
							</table>
						</div>
					</div>
					<%}%>
				</div>
			</div>
			<!-- ---------------------------------------- P-2b   Div ----------------------------------------------------- -->
			<!-- ---------------------------------------- P-3  Overall Product tree/WBS Div ----------------------------------------------------- -->
			<div class="carousel-item ">

					<div class="content-header row ">
					<div class="col-md-1" ><img class="bp-18"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					<div class="col-md-1 bp-19" align="left"  ><b class="bp-20"><%=ProjectCode %></b>
					<h6 class="bp-21"><%=pdc %></h6>
					</div>
					<div class="col-md-8">
					<h3>3. Overall Product Tree/WBS</h3>
					</div>
					<div class="col-md-1 bp-22" align="right"  ><b class="bp-20"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="bp-18"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					</div>
				<div class="content">
					<% for (int z = 0; z < 1; z++) { %>
					<% if (ProjectDetail.size() > 1) { %>
					<div class="ml-1">
						<b>Project : <%=ProjectDetail.get(z)[1]%> <% if (z != 0) {  %>(SUB)<% }  %> </b>
					</div>
					<% } %>
					<table class="width-100">
						<%
						if (projectdatadetails.get(z) != null && projectdatadetails.get(z)[5] != null) {
						%>
						<tr>
							<td class="bp-46">
								<form action="ProjectDataSystemSpecsFileDownload.htm"
									method="post" target="_blank">
									<span class="anchorlink bp-42"
										onclick="$('#protree<%=ProjectDetail.get(z)[0]%>').toggle();"
										><b>As on
											File Attached</b></span>
									<button type="submit" class="btn btn-sm ">
										<i class="fa fa-download fa-lg"></i>
									</button>
									<input type="hidden" name="projectdataid"
										value="<%=projectdatadetails.get(z)[0]%>" /> <input
										type="hidden" name="filename" value="protree" /> <input
										type="hidden" name="${_csrf.parameterName}"
										value="${_csrf.token}" />
								</form>
							</td>
						</tr>
						<tr>
							<td class="bp-47">
								<%
								if (FilenameUtils.getExtension(projectdatadetails.get(z)[5].toString()).equalsIgnoreCase("pdf")) {
								%>
							<%-- 	<iframe class="bp-43"
									src="data:application/pdf;base64,<%=pdffiles.get(z)[1]%>#view=FitV" 
									id="protree<%=ProjectDetail.get(z)[0]%>"> </iframe> --%>
								<button class="btn btn-lg btn-link" onclick="showModal('protree','<%=projectdatadetails.get(z)[0]%>')" id="protreebtn"></button>		
										<div class="col-md-12" id="protreeModal">
											
											</div>
									 <%
								 } else {
								 %>
								<img data-enlargable class="bp-44"
								src="data:image/<%=FilenameUtils.getExtension(projectdatadetails.get(z)[5].toString())%>;base64,<%=pdffiles.get(z)[1]%>"
								id="protree<%=ProjectDetail.get(z)[0]%>"> <% } %>
							</td>

						</tr>
						<% } else { %>
						<tr>
							<td class="bp-46">
								<span class="mainsubtitle">Overall Product tree/WBS File
									Not Found</span>
							</td>
						</tr>
						<% } %>
					</table>
					<% } %>
				</div>
			</div>
			<!-- ----------------------------------------  Overall Product tree/WBS Div ----------------------------------------------------- -->
			<!-- ---------------------------------------- P-4a  Particulars of Meeting Div ----------------------------------------------------- -->
			<div class="carousel-item ">
					<div class="content-header row ">
					<div class="col-md-1" ><img class="bp-18"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					<div class="col-md-1 bp-19" align="left"  ><b class="bp-20"><%=ProjectCode %></b>
					<h6 class="bp-21"><%=pdc %></h6>
					</div>
					<div class="col-md-8">
						<h3>
							4 (a) <% if (committee.getCommitteeShortName().trim().equalsIgnoreCase("PMRC") || committee.getCommitteeShortName().trim().equalsIgnoreCase("PMRB")) { %> Approval
							<% } else { %> Ratification <% } %> of <b>Recommendations</b> of last <%=committee.getCommitteeShortName().trim().toUpperCase()%> Meeting
						</h3>
					</div>
					<div class="col-md-1 bp-22" align="right"  ><b class="bp-20"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="bp-18"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					</div>
					
				<div class="content">
					<% for (int z = 0; z < 1; z++) { %>
					<% if (ProjectDetail.size() > 1) { %>
					<div>
						<b>Project : <%=ProjectDetail.get(z)[1]%> <% if (z != 0) {  %>(SUB)<% }  %> </b>
					</div>
					<% } %>
					<!-- CALL last_pmrc_actions_list_bpaper(:projectid,:committeeid); -->
					<table class="subtables bp-48" >
						<thead>
							<tr>
								<td colspan="7" class="border-0">
									<p class="bp-49">
										<span class="notassign">NA</span> : Not Assigned &nbsp;&nbsp;
										<span class="assigned">AA</span> : Activity Assigned &nbsp;&nbsp; 
										<span class="ongoing">OG</span> : On Going &nbsp;&nbsp; 
										<span class="delay">DO</span> : Delay - On Going &nbsp;&nbsp; 
										<span class="ongoing">RC</span> : Review & Close &nbsp;&nbsp; 
										<span class="delay">FD</span> : Forwarded With Delay &nbsp;&nbsp; 
										<span class="completed">CO</span> :Completed &nbsp;&nbsp; 
										<span class="completeddelay">CD</span> : Completed with Delay &nbsp;&nbsp; 
										<span class="inactive">IA</span> : InActive &nbsp;&nbsp; 
										<span class="delaydays">DD</span> : Delayed days &nbsp;&nbsp;
									</p>
								</td>
							</tr>
							<tr>
								<th class="width15">SN</th>
								<th class="width20">ID</th>
								<th class="width315">Recommendation Point</th>
								<th class="width100">PDC</th>
								<th class="width200">Responsibility</th>
								<th class="width70">Status(DD)</th>
								<th class="width250">Remarks</th>
							</tr>
						</thead>
						<tbody>
							<% if (lastpmrcminsactlist.get(z).size() == 0) { %>
							<tr>
								<td colspan="7" class="text-center">Nil</td>
							</tr>
							<%
							} else if (lastpmrcminsactlist.get(z).size() > 0) {
							int i = 1;String key2="";
							for (Object[] obj : lastpmrcminsactlist.get(z)) {
								if (obj[3].toString().equalsIgnoreCase("R")&& (obj[10]==null || !obj[10].toString().equals("C") || (obj[10].toString().equals("C") && obj[14]!=null &&  before6months.isBefore(LocalDate.parse(obj[14].toString()) ) ))      )
								{ %>
							<tr>
								<td class="text-center"><%=i%></td>
															<td class="text-center">
								<%if(obj[21]!=null && Long.parseLong(obj[21].toString())>0){ %>
									<button type="button" class="btn btn-sm font-weight-bold"  onclick="ActionDetails( <%=obj[21] %>);" data-toggle="tooltip" data-placement="bottom" title="Action Details"  >
								<%for (Map.Entry<Integer, String> entry : committeeWiseMap.entrySet()) {
									Date date = inputFormat.parse(obj[5].toString().split("/")[3]);
									 String formattedDate = outputFormat.format(date);
									 if(entry.getValue().equalsIgnoreCase(formattedDate)){
										 key2=entry.getKey().toString();
									 } }%>
								<%=committee.getCommitteeShortName().trim().toUpperCase()+"-"+key2+"/"+obj[5].toString().split("/")[4] %>
									</button>
								<%}%>
							</td>

								<td class="text-justify"> <%=obj[2]!=null?obj[2].toString():"" %> </td>
								<td class="text-center">
								<%if(obj[8]!= null && !LocalDate.parse(obj[8].toString()).equals(LocalDate.parse(obj[7].toString())) ){ %><span class="text-dark font-weight-bold"><%=sdf.format(sdf1.parse(obj[8].toString()))%></span><br><%} %>	
								<%if(obj[7]!= null && !LocalDate.parse(obj[7].toString()).equals(LocalDate.parse(obj[6].toString())) ){ %><span class="text-dark font-weight-bold"><%=sdf.format(sdf1.parse(obj[7].toString()))%></span><br><%} %>
								<%if(obj[6]!= null){ %><span><%=sdf.format(sdf1.parse(obj[6].toString()))%></span><br><%} %>
								</td>
								<td>
									<% if (obj[4] != null) { %>
										<%=obj[12]%> <%-- , <%=obj[13] %> --%> 
									<% } else { %>
										Not Assigned 
									<%  } %>
								</td>
								<td class="text-center">
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
									<%}else { %><span class="notassign">NA</span><%} %>
							</td>
							<td><% if (obj[19] != null) { %><%= (obj[19].toString())%> <% } %></td>
						</tr>
						<% i++; }
						} %>
							<% if (i == 1) { %>
							<tr>
								<td colspan="7" class="text-center">Nil</td>
							</tr>
							<% } %><% } %>
					</tbody>
				</table>
				<% } %>
				</div>
			</div>
			<!-- ----------------------------------------  P-4a Div ----------------------------------------------------- -->
			<!-- ---------------------------------------- P-4b Div ----------------------------------------------------- -->
			<div class="carousel-item ">
			
				
					<div class="content-header row ">
					<div class="col-md-1" ><img class="bp-18"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					<div class="col-md-1 bp-19" align="left"  ><b class="bp-20"><%=ProjectCode %></b>
					<h6 class="bp-21"><%=pdc %></h6>
					</div>
					<div class="col-md-8">
						<h5>
							4 (b) Last <%=committee.getCommitteeShortName().trim().toUpperCase()%> Meeting Action Points with Probable Date of Completion (PDC), Actual Date of Completion (ADC) and Status
						</h5>
					</div>
					<div class="col-md-1 bp-22" align="right"  ><b class="bp-20"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="bp-18"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					</div>
				
				<div class="content">
					<% for (int z = 0; z < 1; z++) { %>
					<% if (ProjectDetail.size() > 1) { %>
					<div><b>Project : <%=ProjectDetail.get(z)[1]%> <% if (z != 0) {  %>(SUB)<% }  %> </b></div>
					<% } %>
					<%if ((Double.parseDouble(projectattributeslist.get(0)[7].toString()) * 100000) > 0) {%>
					<table class="subtables bp-48" >
						<thead>
							<tr>
								<td colspan="7" class="border-0">
									<p class="bp-49">
										<span class="notassign">NA</span> : Not Assigned &nbsp;&nbsp;
										<span class="assigned">AA</span> : Activity Assigned &nbsp;&nbsp; 
										<span class="ongoing">OG</span> : On Going &nbsp;&nbsp; 
										<span class="delay">DO</span> : Delay - On Going &nbsp;&nbsp; 
										<span class="ongoing">RC</span> : Review & Close &nbsp;&nbsp; 
										<span class="delay">FD</span> : Forwarded With Delay &nbsp;&nbsp; 
										<span class="completed">CO</span> :Completed &nbsp;&nbsp; 
										<span class="completeddelay">CD</span> : Completed with Delay &nbsp;&nbsp; 
										<span class="inactive">IA</span> : InActive &nbsp;&nbsp; 
										<span class="delaydays">DD</span> : Delayed days &nbsp;&nbsp;
									</p>
								</td>
							</tr>
							<tr>
								<th class="width15">SN</th>
								<th class="width30">ID</th>
								<th class="width280">Action Point</th>
								<th class="width95">ADC <br>PDC</th>
								<!-- <th style="width: 95px;">ADC</th> -->
								<th class="width205">Responsibility</th>
								<th class="width80">Status(DD)</th>
								<th class="width200">Remarks</th>
								<!-- <th style="width: 20px;">Info</th> -->
							</tr>
						</thead>
						<tbody>
							<% if (lastpmrcactions.get(z).size() == 0) { %>
							<tr>
								<td colspan="7" class="text-center">Nil</td>
							</tr>
							<% } else if (lastpmrcactions.size() > 0) {
								Map<String,List<Object[]>> list = lastpmrcactions.get(z)!=null ? lastpmrcactions.get(z).stream()
										.collect(Collectors.groupingBy(array -> array[0].toString(), LinkedHashMap::new,Collectors.toList())) : new HashMap<>();
							int i = 1;String key="";
							for(Map.Entry<String, List<Object[]>> map : list.entrySet()){
								int j=1;
								List<Object[]> values = map.getValue();
								int rowSpan = values.size();
							for (Object[] obj : values) {
							%>
							<tr>
								<td class="text-center"><%=i%></td>
								<td class="text-center">
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
								<button type="button" class="btn btn-sm font-weight-bold"  onclick="ActionDetails( <%=obj[17] %>);" data-toggle="tooltip" data-placement="top" title="Action Details" >
								<%for (Map.Entry<Integer, String> entry : committeeWiseMap.entrySet()) {
									Date date = inputFormat.parse(obj[1].toString().split("/")[3]);
									 String formattedDate = outputFormat.format(date);
									 if(entry.getValue().equalsIgnoreCase(formattedDate)){
										 key=entry.getKey().toString();
									 } }%>
								
								<%=splCommittee+"-"+key+"/"+obj[1].toString().split("/")[4] %>
								</button>
								<%}%> 
								<!--  -->
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
									<span <%if(endPdc.isAfter(today) || endPdc.isEqual(today)) {%>class="text-dark font-weight-bold" <%} else{%> class="bp-50" <%} %>>
									<%= sdf.format(sdf1.parse(obj[4].toString()))%> 
									</span>
									<%if(!pdcorg.equals(endPdc)) { %>
									<br>
									<span <%if(pdcorg.isAfter(today) || pdcorg.isEqual(today)) {%>class="text-dark font-weight-bold"<%} else{%> class="bp-50" <%} %>>
									<%= sdf.format(sdf1.parse(obj[3].toString()))%> 
									</span>	
									<%} %>
								</td>
								<td><%=obj[11]%><%-- , <%=obj[12] %> --%></td>
								<td class="text-center">
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
								<td class="text-justify">
									<% if (obj[16] != null) { %><%= (obj[16].toString()) %> <% } %>
								</td>

								
							</tr>
							<% i++; }}
							} %>
						</tbody>

					</table>

					<% } %>
					<% } %>				
				</div>

			</div>

			<!-- ----------------------------------------  P-4b Div ----------------------------------------------------- -->

			<!-- ---------------------------------------- P-4c  Div ----------------------------------------------------- -->

			<div class="carousel-item ">
					<div class="content-header row ">
					<div class="col-md-1" ><img class="bp-18"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					<div class="col-md-1 bp-19" align="left"  ><b class="bp-20"><%=ProjectCode %></b>
					<h6 class="bp-21"><%=pdc %></h6>
					</div>
					<div class="col-md-8">
						<h3>4 (c) Details of Technical/ User Reviews</h3>
					</div>
					<div class="col-md-1 bp-22" align="right"  ><b class="bp-20"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="bp-18"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					</div>
					
				<div class="content">
					<div align="center">
						<div  class="mr-5 mt-2 bp-51" >
							<button type="button" class="btn btn-sm " onclick="showMeetingModal()" data-toggle="tooltip" data-placement="right" title="Other Meetings"><i class="fa fa-info-circle fa-lg bp-3"  aria-hidden="true"></i></button>
						</div>
						<form action="CommitteeMinutesNewDownload.htm" method="get" target="_blank">
							<div class="row">
							<%for(Map.Entry<String, List<Object[]>> entry : reviewMeetingListMap.entrySet()) { 
								if(entry.getValue().size()>0) { %>
									<div class="col-md-3 mt-4">
										<table class="subtables bp-52" >
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
					</div>								
				</div></div>
			<!-- ----------------------------------------   P-4c Div ----------------------------------------------------- -->
			<!-- ---------------------------------------- P-5  Milestones achieved prior Div ----------------------------------------------------- -->
			<div class="carousel-item ">
					<div class="content-header row ">
					<div class="col-md-1" ><img class="bp-18"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					<div class="col-md-1 bp-19" align="left"  ><b class="bp-20"><%=ProjectCode %></b>
					<h6 class="bp-21"><%=pdc %></h6>
					</div>
					<div class="col-md-8">
						<h3> 5. Milestones Achieved Prior to this <%=CommitteeCode%> Period</h3>
					</div>
					<div class="col-md-1 bp-22" align="right"  ><b class="bp-20"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="bp-18"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					</div>
				
				
				<div class="content">

					<% for (int z = 0; z < 1; z++) { %>
					<% if (ProjectDetail.size() > 1) { %>
					<div>
						<b>Project : <%=ProjectDetail.get(z)[1]%> <% if (z != 0) {  %>(SUB)<% }  %> </b>
					</div>
					<%
					}
					%>
					<form action="MilestoneActivityList.htm" target="_blank" class="bp-51">
						<button class="btn btn-sm bp-53" data-toggle="tooltip" data-placement="bottom" title="Milestone Data">
							<i class="fa fa-info-circle bp-54"  aria-hidden="true"></i>
						</button>
						<input type="hidden" name="ProjectId" value="<%=projectid%>">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					</form>

					<!-- CALL Pfms_Milestone_Level_Prior(:projectid) -->
					<table class="subtables bp-55" >
						<thead>
							<tr>
								<td colspan="10" class="border-0">
									<p class="bp-49">
										<span class="notassign">NA</span> : Not Assigned &nbsp;&nbsp;
										<span class="assigned">AA</span> : Activity Assigned &nbsp;&nbsp; 
										<span class="ongoing">OG</span> : On Going &nbsp;&nbsp; 
										<span class="delay">DO</span> : Delay - On Going &nbsp;&nbsp; 
										<span class="ongoing">RC</span> : Review & Close &nbsp;&nbsp; 
										<span class="delay">FD</span> : Forwarded With Delay &nbsp;&nbsp; 
										<span class="completed">CO</span> :Completed &nbsp;&nbsp; 
										<span class="completeddelay">CD</span> : Completed with Delay &nbsp;&nbsp; 
										<span class="inactive">IA</span> : InActive &nbsp;&nbsp; 
										<span class="delaydays">DD</span> : Delayed days &nbsp;&nbsp;
									</p>
								</td>
							</tr>

							<tr>
								<th class="width20">SN</th>
								<th class="width20">MS</th>
								<th class="width70">L</th>
								<th class="width350">System/ Subsystem/ Activities</th>
								<th class="width100" >Start Date</th>
								<th class="width100">ADC<br>PDC</th>
								<!-- <th style="width: 150px;">ADC</th> -->
								<th class="width70">Progress</th>
								<th class="width70">Status(DD)</th>
								<th class="width350">Remarks</th>
								<th class="width30">Info</th>
							</tr>
						</thead>
						<%
						if (milestones.get(z).size() > 0) {
							long count1 = 1;
							int milcountA = 1;
							int milcountB = 1;
							int milcountC = 1;
							int milcountD = 1;
							int milcountE = 1;
						%>
						<%
						int serial = 1;int milestonecount=0;// to remember milestonecount
						for (Object[] obj : milestones.get(z)) {
						if(obj[21].toString().equals("1")){
						}
					if(Integer.parseInt(obj[21].toString())<= Integer.parseInt(levelid)  ){
							%>
						<tr>
							<td class="text-center"><%=serial%></td>
							<td>M<%=obj[0]%></td>

							<td class="text-center">
								<%
								if (obj[21].toString().equals("0")) {
								%> <!-- L --> <%
								 milcountA = 1;
								 milcountB = 1;
								 milcountC = 1;
								 milcountD = 1;
								 milcountE = 1;
								 } else if (obj[21].toString().equals("1")) {
										for(Map.Entry<Integer,String>entry:treeMapLevOne.entrySet()){
											if(entry.getKey().toString().equalsIgnoreCase(obj[2].toString())){%>
												<%=entry.getValue() %>
										<%}} 
									 
								 %>
								<%
					
								} else if (obj[21].toString().equals("2")) {
									for(Map.Entry<Integer,String>entry:treeMapLevTwo.entrySet()){
										if(entry.getKey().toString().equalsIgnoreCase(obj[3].toString())){%>
											<%=entry.getValue() %>
									<%}}	
									
								%> <%-- B-<%=milcountB%> --%>
								<%
						
								} else if (obj[21].toString().equals("3")) {
								%> C-<%=milcountC%>
								<%
								milcountC += 1;
								milcountD = 1;
								milcountE = 1;
								} else if (obj[21].toString().equals("4")) {
								%> D-<%=milcountD%>
								<%
								milcountD += 1;
								milcountE = 1;
								} else if (obj[21].toString().equals("5")) {
								%> E-<%=milcountE%>
								<%
								milcountE++;
								}
								%>
							</td>

							<td
								class="<%if (obj[21].toString().equals("0")) {%>font-weight-bold<%}%>">
								<%
								if (obj[21].toString().equals("0")) {
								%> <%=( obj[10].toString() )%> <%
									 } else if (obj[21].toString().equals("1")) {
									 %>
																	&nbsp;&nbsp;<%=( obj[11].toString() )%> <%
									 } else if (obj[21].toString().equals("2")) {
									 %>
																	&nbsp;&nbsp;<%= (obj[12].toString()) %> <%
									 } else if (obj[21].toString().equals("3")) {
									 %>
																	&nbsp;&nbsp;<%= (obj[13].toString())%> <%
									 } else if (obj[21].toString().equals("4")) {
									 %>
																	&nbsp;&nbsp;<%= (obj[14].toString())%> <% } else if (obj[21].toString().equals("5")) { %>
																	&nbsp;&nbsp;<%=(obj[15].toString())%> <% } %>
							</td>
							<td class="text-center" ><%= obj[7]!=null? sdf.format(sdf1.parse(obj[7].toString())) : " - " %></td>
							<td class="text-center">

								
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
									
								 <% } else {  %> <span>- </span><% } %>
								<br>
								<% if (!LocalDate.parse(obj[8].toString()).isEqual(LocalDate.parse(obj[9].toString()))) {
									boolean incident=true;
									%>
								<span class="font-weight-bold"><%=sdf.format(sdf1.parse(obj[8].toString()))%></span><br> 
								<%}%> 
								<span  class="font-weight-bold"><%=sdf.format(sdf1.parse(obj[9].toString()))%></span>
								
							</td>
					
							<td class="text-center"><%=obj[17]%>%</td>
							
							
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
							
							<td class="bp-56">
								<% if (obj[23] != null) { %><%= (obj[23].toString()) %> <% } %>
							</td>
							<td class="text-center">
							<%if (obj[21].toString().equals("0")) {%>
								<a data-toggle="modal" data-target="#exampleModal1" data-id="milestonemodal<%=obj[0]%>" class="milestonemodal" data-whatever="@mdo" class="bp-57"> 
									<i class="fa fa-info-circle fa-lg bp-3" aria-hidden="true"></i>
								</a>
								<%} %>
							</td>
						</tr>
						<%
						count1++;serial++;}}
						%>
						<% } else { %>
						<tr><td colspan="10" class="text-center">Nil</td></tr>
						<% } %>
					</table>
					<div id="milestoneactivitychange"></div>
					<% } %>
				</div>
			</div>
			<!-- ----------------------------------------  Milestones achieved prior Div ----------------------------------------------------- -->
			<!-- ----------------------------------------  SunSet Milestones Div ----------------------------------------------------- -->
			
			<div class="carousel-item ">
					<div class="content-header row ">
					<div class="col-md-1" ><img class="bp-18"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					<div class="col-md-1 bp-19" align="left"  ><b class="bp-20"><%=ProjectCode %></b>
					<h6 class="bp-21"><%=pdc %></h6>
					</div>
					<div class="col-md-8">
						<h3> 6. Review of SunSetMilestones</h3>
					</div>
					<div class="col-md-1 bp-22" align="right"  ><b class="bp-20"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="bp-18"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					</div>
				
				
				<div class="content">

					<% for (int z = 0; z < 1; z++) { %>
					<% if (ProjectDetail.size() > 1) { %>
					<div>
						<b>Project : <%=ProjectDetail.get(z)[1]%> <% if (z != 0) {  %>(SUB)<% }  %> </b>
					</div>
					<%
					}
					%>
					<form action="MilestoneActivityList.htm" target="_blank" class="bp-51">
						<button class="btn btn-sm bp-53" data-toggle="tooltip" data-placement="bottom" title="Milestone Data">
							<i class="fa fa-info-circle bp-54"  aria-hidden="true"></i>
						</button>
						<input type="hidden" name="ProjectId" value="<%=projectid%>">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					</form>

					<!-- CALL Pfms_Milestone_Level_Prior(:projectid) -->
					<table class="subtables bp-55" >
						<thead>
							<tr>
								<td colspan="10" class="border-0">
									<p class="bp-49">
										<span class="notassign">NA</span> : Not Assigned &nbsp;&nbsp;
										<span class="assigned">AA</span> : Activity Assigned &nbsp;&nbsp; 
										<span class="ongoing">OG</span> : On Going &nbsp;&nbsp; 
										<span class="delay">DO</span> : Delay - On Going &nbsp;&nbsp; 
										<span class="ongoing">RC</span> : Review & Close &nbsp;&nbsp; 
										<span class="delay">FD</span> : Forwarded With Delay &nbsp;&nbsp; 
										<span class="completed">CO</span> :Completed &nbsp;&nbsp; 
										<span class="completeddelay">CD</span> : Completed with Delay &nbsp;&nbsp; 
										<span class="inactive">IA</span> : InActive &nbsp;&nbsp; 
										<span class="delaydays">DD</span> : Delayed days &nbsp;&nbsp;
									</p>
								</td>
							</tr>

							<tr>
								<th class="width20">SN</th>
								<th class="width20">MS</th>
								<th class="width70">L</th>
								<th class="width350">System/ Subsystem/ Activities</th>
								<th class="width100" >Start Date</th>
								<th class="width100">ADC<br>PDC</th>
								<!-- <th style="width: 150px;">ADC</th> -->
								<th class="width70">Progress</th>
								<th class="width70">Status(DD)</th>
								<th class="width350">Remarks</th>
								<th class="width30">Info</th>
							</tr>
						</thead>
						<%
						if (sunsetmilestones.get(z).size() > 0) {
							long count1 = 1;
							int milcountA = 1;
							int milcountB = 1;
							int milcountC = 1;
							int milcountD = 1;
							int milcountE = 1;
						%>
						<%
						int serial = 1;int milestonecount=0;// to remember milestonecount
						for (Object[] obj : sunsetmilestones.get(z)) {
						if(obj[21].toString().equals("1")){
						}
					if(Integer.parseInt(obj[21].toString())<= Integer.parseInt(levelid)  ){
							%>
						<tr class="<% if("0".equals(obj[21].toString()) && "Y".equalsIgnoreCase(obj[31].toString())){ %> bg-sunset <%}%>">
							<td class="text-center"><%=serial%></td>
							<td>M<%=obj[0]%></td>

							<td class="text-center">
								<%
								if (obj[21].toString().equals("0")) {
								%> <!-- L --> <%
								 milcountA = 1;
								 milcountB = 1;
								 milcountC = 1;
								 milcountD = 1;
								 milcountE = 1;
								 } else if (obj[21].toString().equals("1")) {
										for(Map.Entry<Integer,String>entry:treeMapLevOne.entrySet()){
											if(entry.getKey().toString().equalsIgnoreCase(obj[2].toString())){%>
												<%=entry.getValue() %>
										<%}} 
									 
								 %>
								<%
					
								} else if (obj[21].toString().equals("2")) {
									for(Map.Entry<Integer,String>entry:treeMapLevTwo.entrySet()){
										if(entry.getKey().toString().equalsIgnoreCase(obj[3].toString())){%>
											<%=entry.getValue() %>
									<%}}	
									
								%> <%-- B-<%=milcountB%> --%>
								<%
						
								} else if (obj[21].toString().equals("3")) {
								%> C-<%=milcountC%>
								<%
								milcountC += 1;
								milcountD = 1;
								milcountE = 1;
								} else if (obj[21].toString().equals("4")) {
								%> D-<%=milcountD%>
								<%
								milcountD += 1;
								milcountE = 1;
								} else if (obj[21].toString().equals("5")) {
								%> E-<%=milcountE%>
								<%
								milcountE++;
								}
								%>
							</td>

							<td
								class="<%if (obj[21].toString().equals("0")) {%>font-weight-bold<%}%>">
								<%
								if (obj[21].toString().equals("0")) {
								%> <%=StringEscapeUtils.escapeHtml4( obj[10].toString() )%> <%
									 } else if (obj[21].toString().equals("1")) {
									 %>
																	&nbsp;&nbsp;<%=StringEscapeUtils.escapeHtml4( obj[11].toString() )%> <%
									 } else if (obj[21].toString().equals("2")) {
									 %>
																	&nbsp;&nbsp;<%= StringEscapeUtils.escapeHtml4(obj[12].toString()) %> <%
									 } else if (obj[21].toString().equals("3")) {
									 %>
																	&nbsp;&nbsp;<%= StringEscapeUtils.escapeHtml4(obj[13].toString())%> <%
									 } else if (obj[21].toString().equals("4")) {
									 %>
																	&nbsp;&nbsp;<%= StringEscapeUtils.escapeHtml4(obj[14].toString())%> <% } else if (obj[21].toString().equals("5")) { %>
																	&nbsp;&nbsp;<%=StringEscapeUtils.escapeHtml4(obj[15].toString())%> <% } %>
							</td>
							<td class="text-center" ><%= obj[7]!=null? sdf.format(sdf1.parse(obj[7].toString())) : " - " %></td>
							<td class="text-center">

								
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
									
								 <% } else {  %> <span>- </span><% } %>
								<br>
								<% if (!LocalDate.parse(obj[8].toString()).isEqual(LocalDate.parse(obj[9].toString()))) {
									boolean incident=true;
									%>
								<span class="font-weight-bold"><%=sdf.format(sdf1.parse(obj[8].toString()))%></span><br> 
								<%}%> 
								<span  class="font-weight-bold"><%=sdf.format(sdf1.parse(obj[9].toString()))%></span>
								
							</td>
					
							<td class="text-center"><%=obj[17]%>%</td>
							
							
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
							
							<td class="bp-56">
								<% if (obj[23] != null) { %><%= StringEscapeUtils.escapeHtml4(obj[23].toString()) %> <% } %>
							</td>
							<td class="text-center">
							<%if (obj[21].toString().equals("0")) {%>
								<a data-toggle="modal" data-target="#exampleModal1" data-id="milestonemodal<%=obj[0]%>" class="milestonemodal" data-whatever="@mdo" class="bp-57"> 
									<i class="fa fa-info-circle fa-lg bp-3" aria-hidden="true"></i>
								</a>
								<%} %>
							</td>
						</tr>
						<%
						count1++;serial++;}}
						%>
						<% } else { %>
						<tr><td colspan="10" class="text-center">Nil</td></tr>
						<% } %>
					</table>
					<div id="milestoneactivitychange"></div>
					<% } %>
				</div>
			</div>
			
			<!-- ----------------------------------------  SunSet Milestones Div ----------------------------------------------------- -->			
			
			
			<!-- ---------------------------------------- P-7a Div ----------------------------------------------------- -->
			<div class="carousel-item ">
					<div class="content-header row ">
					<div class="col-md-1" ><img class="bp-18"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					<div class="col-md-1 bp-19" align="left"  ><b class="bp-20"><%=ProjectCode %></b>
					<h6 class="bp-21"><%=pdc %></h6>
					</div>
					<div class="col-md-8">
						<h3>7 (a) Work Carried Out, Achievements, Test Result etc.</h3>
					</div>
					<div class="col-md-1 bp-22" align="right"  ><b class="bp-20"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="bp-18"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					</div>
				<div class="content">

					<% for (int z = 0; z < 1; z++) { %>
					<% if (ProjectDetail.size() > 1) { %>
					<div>
						<b>Project : <%=ProjectDetail.get(z)[1]%> <% if (z != 0) {  %>(SUB)<% }  %> </b>
					</div>
					<% } %>
					<div align="left" class="ml-1">
						<% if (z == 0) { %>
						<form action="FilterMilestone.htm" method="POST">
							<button class="btn btn-sm back bp-58"  formtarget="blank">Filter</button>
							<input type="hidden" name="projectidvalue"  value="<%=projectid%>">
							<input type="hidden" name="committeidvalue" value="<%=committeeid%>" >
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						</form>
						<% } %>
					</div>
					<table class="subtables bp-55" >
						<thead>
							<tr>
								<td colspan="10" class="border-0">
									<p class="bp-49">
										<span class="notassign">NA</span> : Not Assigned &nbsp;&nbsp;
										<span class="assigned">AA</span> : Activity Assigned &nbsp;&nbsp; 
										<span class="ongoing">OG</span> : On Going &nbsp;&nbsp; 
										<span class="delay">DO</span> : Delay - On Going &nbsp;&nbsp; 
										<span class="ongoing">RC</span> : Review & Close &nbsp;&nbsp; 
										<span class="delay">FD</span> : Forwarded With Delay &nbsp;&nbsp; 
										<span class="completed">CO</span> :Completed &nbsp;&nbsp; 
										<span class="completeddelay">CD</span> : Completed with Delay &nbsp;&nbsp; 
										<span class="inactive">IA</span> : InActive &nbsp;&nbsp; 
										<span class="delaydays">DD</span> : Delayed days &nbsp;&nbsp;
									</p>
								</td>
							</tr>
							<tr>
								<th class="width20">SN</th>
								<th class="width30">MS</th>
								<th class="width70">L</th>
								<th class="width450">System/ Subsystem/ Activities</th>
								<th class="width100" >Start Date</th>
								<th class="width150">PDC</th>
								<th class="width60">Progress</th>
								<th class="width50">Status(DD)</th>
								<th class="width310">Remarks</th>
								<th class="width30">Info</th>
							</tr>
						</thead>
						<%
						if (MilestoneDetails6.get(z).size() > 0) {
							long count1 = 1;
							int milcountA = 1;
							int milcountB = 1;
							int milcountC = 1;
							int milcountD = 1;
							int milcountE = 1;
						%>
						<%
						int serial = 1;
						for (Object[] obj : MilestoneDetails6.get(z)) {
						if (Integer.parseInt(obj[21].toString()) <= Integer.parseInt(levelid)) {
						%>
						<tr>
							<td class="text-center"><%=serial%></td>
							<td>M<%=obj[0]%></td>
							<td class="text-center">
								<%
								if (obj[21].toString().equals("0")) {
								%> <!-- L --> <%
								 milcountA = 1;
								 milcountB = 1;
								 milcountC = 1;
								 milcountD = 1;
								 milcountE = 1;
								 } else if (obj[21].toString().equals("1")) {
								 for(Map.Entry<Integer,String>entry:treeMapLevOne.entrySet()){
								if(entry.getKey().toString().equalsIgnoreCase(obj[2].toString())){%>
								<%=entry.getValue() %>
										<%}}
								 %> <%-- A-<%=milcountA%> --%>
								<%
								/* milcountA++;
								milcountB = 1;
								milcountC = 1;
								milcountD = 1;
								milcountE = 1; */
								} else if (obj[21].toString().equals("2")) {
									for(Map.Entry<Integer,String>entry:treeMapLevTwo.entrySet()){
										if(entry.getKey().toString().equalsIgnoreCase(obj[3].toString())){%>
											<%=entry.getValue() %>
									<%}}
								%><%--  B-<%=milcountB%> --%>
								<%
								/* milcountB += 1;
								milcountC = 1;
								milcountD = 1;
								milcountE = 1; */
								} else if (obj[21].toString().equals("3")) {
								%> C-<%=milcountC%>
								<%
								milcountC += 1;
								milcountD = 1;
								milcountE = 1;
								} else if (obj[21].toString().equals("4")) {
								%> D-<%=milcountD%>
								<%
								milcountD += 1;
								milcountE = 1;
								} else if (obj[21].toString().equals("5")) {
								%> E-<%=milcountE%>
								<%
								milcountE++;
								}
								%>
							</td>

							<td class="<%if(obj[21].toString().equals("0")) {%>font-weight-bold<%}%> text-justify">
									<%if(obj[21].toString().equals("0")) {%>
										<%=( obj[10].toString() ) %>
									<%}else if(obj[21].toString().equals("1")) { %>
										&nbsp;&nbsp;<%= (obj[11].toString()) %>
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
							<% 
								LocalDate StartDate = LocalDate.parse(obj[7].toString());
								LocalDate EndDate = LocalDate.parse(obj[8].toString());
								LocalDate OrgEndDate = LocalDate.parse(obj[9].toString());
								int Progess = Integer.parseInt(obj[17].toString());
								LocalDate CompletionDate =obj[24]!=null ? LocalDate.parse(obj[24].toString()) : null;
								LocalDate Today = LocalDate.now();
							%>
							<td class="text-center">
								<% if (!LocalDate.parse(obj[8].toString()).isEqual(LocalDate.parse(obj[9].toString()))) { %>
									<span <%if(LocalDate.parse(obj[8].toString()).isBefore(LocalDate.parse(todayDate))) {%>class="bp-50"<%}else{%>class="font-weight-bold" <%} %>>
									<%=sdf.format(sdf1.parse(obj[8].toString()))%></span><br> 
								<% } %> 
							<span <%if( LocalDate.parse(obj[8].toString()).isEqual(LocalDate.parse(obj[9].toString())) && LocalDate.parse(obj[9].toString()).isBefore(LocalDate.parse(todayDate))) {%> class="bp-50"<%}else{ %>class="font-weight-bold"<%} %>><%=sdf.format(sdf1.parse(obj[9].toString()))%></span>
							</td>
							<td class="text-center"><%=obj[17]%>%</td>
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
							<td class="bp-59">
								<% if (obj[23] != null) {
								%><%=(obj[23].toString())%>
								<%}%>
							</td>
							<td class="text-center">
							<%
								if (obj[21].toString().equals("0")) {%>
								<a data-toggle="modal" data-target="#exampleModal1" data-id="milestonemodal<%=obj[0]%>" class="milestonemodal" data-whatever="@mdo" class="bp-57"> 
									<i class="fa fa-info-circle fa-lg bp-3"  aria-hidden="true"></i>
								</a>
								<%} %>
							</td>
						</tr>
						<% count1++;
						serial++;
						}
						} %>
						<% } else { %>
						<tr>
							<td colspan="10" class="text-center">Nil</td>
						</tr>


						<%}%>
					</table>

					<%}%>
				</div>

			</div>

			<!-- ----------------------------------------   P-7a Div ----------------------------------------------------- -->

			<!-- ---------------------------------------- P-7b Div ----------------------------------------------------- -->

			<div class="carousel-item ">
					<div class="content-header row ">
					<div class="col-md-1" ><img class="bp-18"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					<div class="col-md-1 bp-19" align="left"  ><b class="bp-20"><%=ProjectCode %></b>
					<h6 class="bp-21"><%=pdc %></h6>
					</div>
					<div class="col-md-8">
					<h4>7 (b) TRL Table with TRL at Sanction stage and Current stage Indicating Overall PRI</h4>
					</div>
					<div class="col-md-1 bp-22" align="right"  ><b class="bp-20"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="bp-18"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					</div>

				<div class="content">
	
					<% for (int z = 0; z < 1; z++) { %>
						<% if (ProjectDetail.size() > 1) { %>
							<div>
								<b>Project : <%=ProjectDetail.get(z)[1]%> <% if (z != 0) {  %>(SUB)<% }  %> </b>
							</div>
						<% } %>

					<!-- <div align="left" style="margin-left: 15px;"><span class="mainsubtitle">(b) TRL table with TRL at sanction stage and current stage indicating overall PRI.</span></div> -->

					<div>
						<table class="width-100">
							<% if (projectdatadetails.get(z) != null && projectdatadetails.get(z)[6] != null) { %>
							<tr>
								<td class="border-0">
									<form action="ProjectDataSystemSpecsFileDownload.htm" method="post" target="_blank">
										<span class="anchorlink bp-42" onclick="$('#pearl<%=ProjectDetail.get(z)[0]%>').toggle();">
											<b>As on File Attached</b>
										</span>
										<button type="submit" class="btn btn-sm "> <i class="fa fa-download fa-lg"></i> </button>
										<input type="hidden" name="projectdataid" value="<%=projectdatadetails.get(z)[0]%>" /> 
										<input type="hidden" name="filename" value="pearl" /> 
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									</form>
								</td>
							</tr>
							<tr>
									<td class="border-0 text-center" >
									<%  if (FilenameUtils.getExtension(projectdatadetails.get(z)[6].toString()).equalsIgnoreCase("pdf")) {  %>
										
											<button class="btn btn-lg btn-link" onclick="showModal('pearl','<%=projectdatadetails.get(z)[0]%>')" id="pearlbtn"></button>		
										<div class="col-md-12" id="pearlModal">
											
											</div>
										<%-- <iframe  src="data:application/pdf;base64,<%=pdffiles.get(z)[2]%>#view=FitV" class="bp-43" id="pearl<%=ProjectDetail.get(z)[0]%>"> </iframe>  --%>
									
									<% } else { %>
										<img data-enlargable class="bp-61" src="data:image/<%=FilenameUtils.getExtension(projectdatadetails.get(z)[6].toString())%>;base64,<%=pdffiles.get(z)[2]%>"id="pearl<%=ProjectDetail.get(z)[0]%>"> 
									<% } %>
								</td>
							</tr>

							<% } else { %>
								<tr>
									<td class="border-0">File Not Found</td>
								</tr>
							<% } %>

						</table>
					</div>

					<% } %>
				</div>

			</div>

			<!-- ----------------------------------------   P-7b Div ----------------------------------------------------- -->

			<!-- ---------------------------------------- P-7c Div ----------------------------------------------------- -->

			<div class="carousel-item ">
					<div class="content-header row ">
					<div class="col-md-1" ><img class="bp-18"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					<div class="col-md-1 bp-19" align="left"  ><b class="bp-20"><%=ProjectCode %></b>
					<h6 class="bp-21"><%=pdc %></h6>
					</div>
					<div class="col-md-8">
					<h3>7 (c) Risk Matrix/Management Plan/Status</h3>
					</div>
					<div class="col-md-1 bp-22" align="right"  ><b class="bp-20"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="bp-18"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					</div>
				<div class="content">

					<% for (int z = 0; z < 1; z++) { %>
					<% if (ProjectDetail.size() > 1) { %>
					<div>
						<b>Project : <%=ProjectDetail.get(z)[1]%> <% if (z != 0) {  %>(SUB)<% }  %> </b>
					</div>
					<% } %>

					<!-- <div align="left" style="margin-left: 15px;"><span class="mainsubtitle">(c) Risk Matrix/Management Plan/Status.</span> </div> -->

					<table class="subtables bp-55" >
						<thead>

							<tr>
								<td colspan="10" class="border-0">
									<p class="bp-49">
									<span class="notassign">NA</span> : Not Assigned &nbsp;&nbsp;
									<span class="assigned">AA</span> : Activity Assigned &nbsp;&nbsp; 
									<span class="ongoing">OG</span> : On Going &nbsp;&nbsp; 
									<span class="delay">DO</span> : Delay - On Going &nbsp;&nbsp; 
									<span class="ongoing">RC</span> : Review & Close &nbsp;&nbsp; 
									<span class="delay">FD</span> : Forwarded With Delay &nbsp;&nbsp; 
									<span class="completed">CO</span> :Completed &nbsp;&nbsp; 
									<span class="completeddelay">CD</span> : Completed with Delay &nbsp;&nbsp; 
									<span class="inactive">IA</span> : InActive &nbsp;&nbsp; 
									<span class="delaydays">DD</span> : Delayed days &nbsp;&nbsp;
									</p>
								</td>
							</tr>
							<tr>
								<td colspan="10" class="border-0 text-right"><b>RPN :</b> Risk Priority Number</td>
							</tr>
							<tr>
								<th class="width15" rowspan="2">SN</th>
								<th class="width20" rowspan="2">ID</th>
								<th class="width425" colspan="3">Risk
									<a data-toggle="modal" class="fa faa-pulse animated bp-78" data-target="#RiskTypesModal" data-whatever="@mdo"><i class="fa fa-info-circle f13r"  aria-hidden="true"></i> </a>
								</th>
								<th class="width100" rowspan="1">ADC<br>PDC</th>
								<!-- <th style="width: 100px;" rowspan="1">ADC</th> -->
								<th class="width160" rowspan="1">Responsibility</th>
								<th class="width50" rowspan="1">Status(DD)</th>
								<th class="width200" rowspan="1">Remarks</th>
								
							</tr>
							<tr>
								<th class="text-center width110">Severity<br>(1-10)</th>
								<th class="text-center width110">Probability<br>(1-10)</th>
								<th class="text-center width110">RPN<br>(1-100)</th>
								<th class="width200" colspan="3">Mitigation Plans</th>
								<th class="width200" colspan="2">Impact</th>
							</tr>

						</thead>

						<tbody>
							<% 	if (riskmatirxdata.get(z).size() > 0) { 
								int i = 0; %>
							<% 	for (Object[] obj : riskmatirxdata.get(z)) {
								i++; %>
							<tr>
								<td class="text-center" rowspan="2"><%=i%></td>
								
																<td class="text-center" rowspan="2">
									<%if(obj[25]!=null && Long.parseLong(obj[25].toString())>0){
										String []tempArray=obj[13].toString().split("/");
										String tempRisk=tempArray[tempArray.length-1];
										%>
										<button type="button" class="btn btn-sm font-weight-bold"  onclick="ActionDetails( <%=obj[25] %>);" data-toggle="tooltip" data-placement="bottom" title="Action Details"  >
											<!-- <i class="fa fa-info-circle fa-lg " style="color: #145374" aria-hidden="true"></i> -->
										<%=tempRisk %>
										</button>
									<%}%>
								</td>
								<td class="text-justify" colspan="3">
									<%=obj[0]%>  <span class="cl-1 font-weight-bold"> - <%=obj[23] %><%=obj[24]%></span>
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
									
									<%if(!enddate.equals(pdcorg)){ %>
									<%=sdf.format(sdf1.parse(obj[17].toString()))%>
									<br>
									<%} %>
									<%=sdf.format(sdf1.parse(obj[9].toString()))%>
								</td>
								<td rowspan="1"><%=obj[7]%><%-- ,&nbsp;<%=obj[8] %> --%></td>
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
								<td class="text-justify" rowspan="1">
									<% if (obj[19] != null) { %> <%=(obj[19].toString())%> <% } %>
								</td>
							</tr>
							<tr>
								<td class="text-center"><%=obj[1]%></td>
								<td class="text-center"><%=obj[2]%></td>
								<td class="text-center">
									<%=obj[22]%>
									<% int RPN =Integer.parseInt(obj[22].toString());
											if(RPN>=1 && RPN<=25){ %>(Low)
											<%}else if(RPN>=26 && RPN<=50){ %>(Medium)
											<%}else if(RPN>=51 && RPN<=75){ %>(High)
											<%}else if(RPN>=76){ %>(Very High)
											<%} %>
								</td>
								<td class="text-justify" colspan="3"><%=obj[3]!=null?(obj[3].toString()):"" %></td>
								<td class="text-justify" colspan="2"><%=obj[21]!=null?(obj[21].toString()):"" %></td>
							</tr>

							<% if (riskmatirxdata.get(z).size() > i) { %>
							<tr>
								<td colspan="10" class="cl-2">.</td>
							</tr>
							<% } %>

							<% } %>

							<% } else { %>
							<tr>
								<td colspan="10" class="text-center">Nil</td>
							</tr>
							<% } %>
						</tbody>
					</table>

					<% } %>
				</div>
			</div>
			<!-- ----------------------------------------   P-7c Div ----------------------------------------------------- -->

			<!-- ---------------------------------------- P-8a  Details of Procurement Plan Div ----------------------------------------------------- -->
<% int chapter=1;int chapter2=1;
for (int z = 0; z < projectidlist.size(); z++){  %>
			<div class="carousel-item ">

					<div class="content-header row ">
					<div class="col-md-1" ><img class="bp-18"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					<div class="col-md-1 bp-19" align="left"  ><b class="bp-20"><%=ProjectCode %></b>
					<h6 class="bp-21"><%=pdc %></h6>
					</div>
					<div class="col-md-8">
						<h3>8 (a<%if(projectidlist.size()>1) {%><%="."+chapter++%><%} %>) Details of Procurement Plan (Major Items) (<%=ProjectDetail.get(z)[1]%>)</h3>
					</div>
					<div class="col-md-1 bp-22" align="right"  ><b class="bp-20"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="bp-18"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					</div>
				<div class="content">
					
					<% if (ProjectDetail.size() > 1) { %>
					<div>
						<b>Project : <%=ProjectDetail.get(z)[1]%> <% if (z != 0) {  %>(SUB)<% }  %> </b>
					</div>
					<% } %>
					
									<table class="subtables bp-62"  >
										<thead>
										<tr>
											<th colspan="11" class="text-center"> <span class="currency" >(In &#8377; Lakhs)</span></th>
										</tr>
										 <tr>
										 	<th colspan="11" class="std">Demand Details ( > &#8377; <% if (projectdatadetails.get(0) != null && projectdatadetails.get(0)[13] != null) { %>
													<%=projectdatadetails.get(0)[13].toString().replaceAll("\\.\\d+$", "")%> ) <% } else { %> - )<% } %>
												
											</th>
										</tr>
										</thead>
										
										<tr>
											<th class="std bp-63" >SN</th>
											<th class="std bp-64" >Demand No<br>Demand Date</th>
<!-- 											<th class="std" style="border: 1px solid black;max-width:90px; ">Demand Date</th>
 -->											<th class="std bp-65" colspan="4" > Nomenclature</th>
										 	<th class="std bp-64" > Est. Cost</th>
											<th class="std bp-64" > Status</th>
											<th class="std bp-65" colspan="3" s>Remarks</th>
										</tr>
										    <% int k=0;
										    if(procurementOnDemand.get(z)!=null &&  procurementOnDemand.get(z).size()>0){
										    Double estcost=0.0;
										    Double socost=0.0;
										    for(Object[] obj : procurementOnDemand.get(z)){ 
										    	k++; %>
											<tr>
												<td class="std border border-dark"  ><%=k%></td>
												<td class="std border border-dark"  ><%=obj[1]%><br><%=sdf.format(sdf1.parse(obj[3].toString()))%></td>
<%-- 												<td class="std"  style=" border: 1px solid black;"><%=sdf.format(sdf1.parse(obj[3].toString()))%></td>
 --%>												<td class="std" colspan="4" ><%=obj[8]%></td>
												<td class="std text-right" s> <%=format.format(new BigDecimal(obj[5].toString())).substring(1)%></td>
												<td class="std border border-dark"  > <%=obj[10]%> </td>
												<td class="std border border-dark" colspan="3" ><%=obj[11]%> </td>		
											</tr>		
											<%
											estcost += Double.parseDouble(obj[5].toString());
										    }%>
										    
										    <tr>
										    	<td class="std" colspan="8" ><b>Total</b></td>
										    	<td class="std text-center" ><b><%=df.format(estcost)%></b></td>
										    	
										    	<td class="std text-right" colspan="2"></td>

										    </tr>
										    
										    
										    <% }else{%>											
												<tr><td colspan="11"  class="std border border-dark text-center"  >Nil </td></tr>
											<%} %>
											<!-- ********************************Future Demand Start *********************************** -->
											<tr>
											<th class="std" colspan="11" class="border border-dark"><span class="mainsubtitle">Future Demand</span></th>
											</tr>
											<tr>
												 <th class="std border border-dark width15" >SN</th>
													 <th class="std border border-dark width295"  colspan="4" > Nomenclature</th>
													 <th class="std  border border-dark width80" > Est. Cost-Lakh &#8377;</th>
													 <th class="std border border-dark widthM50" > Status</th>
													 <th class="std border border-dark widthM310" colspan="4" >Remarks</th>
											</tr>
										
										    			    <% int a=0;
										    if(envisagedDemandlist!=null &&  envisagedDemandlist.size()>0){
										    Double estcost=0.0;
										    Double socost=0.0;
										    for(Object[] obj : envisagedDemandlist){ 
										    	a++; %>
											<tr>
												<td class="std border border-dark"  ><%=a%></td>
												<td class="std border border-dark" colspan="4"  ><%=obj[3]%></td>
												<td class="std border border-dark text-right" "> <%=format.format(new BigDecimal(obj[2].toString())).substring(1)%></td>
												<td class="std border border-dark"  > <%=obj[6]%> </td>
												<td class="std border border-dark" colspan="4"><%=obj[4]%> </td>		
											</tr>		
											<%
												estcost += Double.parseDouble(obj[2].toString());
										    }%>
										    
										    <tr>
										    	<td  class="std border border-dark text-right"colspan="7" ><b>Total</b></td>
										    	<td class="std border border-dark text-right"  colspan="4"><b><%=df.format(estcost)%></b></td>
										    </tr>
										    
										    
										    <% }else{%>											
												<tr><td colspan="11" class="std border border-dark text-right" >Nil </td></tr>
											<%} %>
											
									<!-- ********************************Future Demand End *********************************** -->
											
											 <tr >
											 
												<th  class="std"  colspan="11">Orders Placed ( > &#8377; <% if (projectdatadetails.get(0) != null && projectdatadetails.get(0)[13] != null) { %>
													<%=projectdatadetails.get(0)[13].toString().replaceAll("\\.\\d+$", "")%> ) <% } else { %> - )<% } %>
												</th>
											 </tr>
										
										  	 <tr>	
										  	 	 <th class="std border border-dark width30" rowspan="1" ">SN</th>
										  	 	 <th class="std border border-dark width150" >Demand No <br>Demand  Date</th>
										  	 	<!--  <th class="std" style="border: 1px solid black;" >Demand  Date</th> -->
												 <th class="std border border-dark" colspan="2" > Nomenclature</th>
												 <th class="std border border-dark width150"  >Supply Order No <br> SO Date </th>
												 <th class="std width100 border border-dark"  colspan="1" >SO Cost-Lakh &#8377;</th>
												<!--  <th class="std" style="border: 1px solid black;max-width:90px;	">DP Date</th> -->
													 <th class="std width100 border border-dark" >DP Date<br> Rev DP</th>
												 <th class="std  width200 border border-dark" colspan="2" >Vendor Name</th>
												 <th class="std width80 border border-dark" > Status</th>
												<th class="std width200 border border-dark "> Remarks</th>
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
					<td <%if(!obj[1].toString().equalsIgnoreCase(demand)){ %>  class="bp-66"<%} else{ %> class="bp-67"<%} %>>
					<%if(!obj[1].toString().equalsIgnoreCase(demand)){ %>
					<%=rowk %>
					<%} %>
					</td>
					<td <%if(!obj[1].toString().equalsIgnoreCase(demand)){ %> class="bp-66"<%} else{ %> class="bp-67"<%} %>>
					<%if(!obj[1].toString().equalsIgnoreCase(demand)){ %><%if(obj[1]!=null) {%> <%=obj[1].toString()%><% }else{ %>-<%} %><br>
					<%=sdf.format(sdf1.parse(obj[3].toString()))%>
					<%} %>
					</td>
					<td colspan="2" <%if(!obj[1].toString().equalsIgnoreCase(demand)){ %> class="bp-66"<%} else{ %> class="bp-67"<%} %>>
					<%if(!obj[1].toString().equalsIgnoreCase(demand)){ %>
					<%=obj[8]%>
					<%} %>
					</td>
				<td class="border border-dark text-center">
				<% if(obj[2]!=null){%> <%=(obj[2].toString())%> <%}else{ %>-<%} %><br>
					<%if(obj[16]!=null){%> <%=sdf.format(sdf1.parse(obj[16].toString()))%> <%}else{ %> - <%} %>
				</td>
				<td class="border border-dark text-right"><%if(obj[6]!=null){%> <%=format.format(new BigDecimal(obj[6].toString())).substring(1)%> <%} else{ %> - <%} %></td>
				<td class="border border-dark">
				<%if(obj[4]!=null){%> <%=sdf.format(sdf1.parse(obj[4].toString()))%> <%}else{ %> - <%} %><br>
				<span class="text-center"><%if(obj[7]!=null){if(!obj[7].toString().equals("null")){%> <%=sdf.format(sdf1.parse(obj[7].toString()))%><%}}else{ %>-<%} %></span>	</td>
					<td colspan="2" class="border border-dark"><%=obj[12] %> </td>
					<td <%if(!obj[1].toString().equalsIgnoreCase(demand)){ %>class="bp-66"<%} else{ %> class="bp-67"<%} %>>
						<%if(!obj[1].toString().equalsIgnoreCase(demand)){ %>
					<%=obj[10]%>
					<%} %>
					
					</td>					
					<td <%if(!obj[1].toString().equalsIgnoreCase(demand)){ %> class="bp-66"<%} else{ %> class="bp-67"<%} %>>
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
										    	<td colspan="5" class="std border border-dark text-right" ><b>Total</b></td>
										    	<td colspan="1" class="std border border-dark text-right" ><b><%=df.format(socost)%></b></td>
										    	<td colspan="5" class="std border border-dark text-right" ></td>
										   		 </tr>	
										 <% }else{%>
											
												<tr><td colspan="8"  class="std border border-dark text-right text-center">Nil </td></tr>
											<%} %>
									</table> 
					<table class="subtables bp-55 mb-1" >
						<thead>
							<tr>
								<th colspan="5" class="text-right border-0"><span class="currency">(In &#8377; Lakhs)</span></th>
							</tr>
							<tr>
								<th colspan="5"><span class="mainsubtitle">Total Summary of Procurement</span></th>
							</tr>
						</thead>

						<tbody>
							<tr>
								<th>No. of Demand</th>
								<th>Est. Cost</th>
								<th>No. of Orders</th>
								<th>SO Cost</th>
								<th>Expenditure</th>
							</tr>

							<% if (totalprocurementdetails != null && totalprocurementdetails.size() > 0) {
								for (TotalDemand obj : totalprocurementdetails) {
									if (obj.getProjectId().equalsIgnoreCase(projectidlist.get(z))) { %>
							<tr>
								<td class="text-center"><%=obj.getDemandCount()%></td>
								<td class="text-center"><%=obj.getEstimatedCost()%></td>
								<td class="text-center"><%=obj.getSupplyOrderCount()%></td>
								<td class="text-center"><%=obj.getTotalOrderCost()%></td>
								<td class="text-center"><%=obj.getTotalExpenditure()%></td>
							</tr>
							<% } } } else { %>
							<tr>
								<td class="std text-center" colspan="5" >IBAS
									Server Could Not Be Connected</td>
							</tr>
							<% } %>
						</tbody>
					</table>
			
				
				</div>

			</div>


			<!-- ----------------------------------------  Details of Procurement Plan Div ----------------------------------------------------- -->
			
			<!-- ---------------------------------------- P-8b  Procurement status Report Div ----------------------------------------------------- -->

 <%--for (int z = 0; z < projectidlist.size(); z++) { %> --%>
			<div class="carousel-item ">

	
				
					<div class="content-header row ">
					<div class="col-md-1" ><img class="bp-18"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					<div class="col-md-1 bp-19" align="left"  ><b class="bp-20"><%=ProjectCode %></b>
					<h6 class="bp-21"><%=pdc %></h6>
					</div>
					<div class="col-md-8">
						<h3>8 (b<%if(projectidlist.size()>1) {%><%="."+(chapter2++)%><%} %>) Procurement Status <%if(projectidlist.size()>1) {%> (<%=ProjectDetail.get(z)[1]%> ) <%} %></h3>
					</div>
					<div class="col-md-1 bp-22" align="right"  ><b class="bp-20"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="bp-18"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					</div>

				<div class="content">
					
					<%
					if (ProjectDetail.size() > 1) {
					%>
					<div>
						<b>Project : <%=ProjectDetail.get(z)[1]%> <% if (z != 0) {  %>(SUB)<% }  %> </b>
					</div>
					<% } %>

			
							 		<table  class="bp-68" >
									<thead>
										<tr>
											<th colspan="29" class="text-right border-0"><span class="currency">(In &#8377; Lakhs)</span></th>
										</tr>
										<tr>
											<th colspan="29" ><span class="mainsubtitle">Procurement Status</span></th>
									 	</tr>
									 	<tr>
											<th class="width40">SN</th>
											<th class="width280">Item Name</th>
											<th class="width155">Est/SO Cost <br><span class="currency font-weight-bold"  >(In &#8377; Lakhs)</span></th>
										<%for(int i=0;i<=25;i++) {%>
										<th class="width35"><%=i %></th>
										<%} %>
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
												<td>
													<%if(proc[8].toString().length()>60){ %>
													<%=(proc[8].toString().substring(0,60)) %> ...
													<%}else{ %>
													<%=(proc[8].toString() )%>
													<%} %>
												</td>
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
														<td class="bg-success text-light">*</td>
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
												<td class="bp-69">*</td>
												<td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>
												</tr>
										<%}} %>

										
										<%if(psn ==0 && envisagedDemandlist.size()==0 ){ %>
											<tr>
										      <td colspan="29" class="text-center">Nil</td>
										   </tr>
										<%} %>
										
								 	</tbody>
								</table>
								<table class="subtables bp-70"  >
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
					</div></div><% } %>
			<!-- ---------------------------------------- Procurement status Report Div ----------------------------------------------------- -->

			<!-- ---------------------------------------- P-9  Overall Financial Status Div ----------------------------------------------------- -->
	<% char fch='a'; for (int z = 0; z < projectidlist.size(); z++) {%>
			<div class="carousel-item ">

					<div class="content-header row ">
					<div class="col-md-1" ><img class="bp-18"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					<div class="col-md-1 bp-19" align="left"  ><b class="bp-20"><%=ProjectCode %></b>
					<h6 class="bp-21"><%=pdc %></h6>
					</div>
					<div class="col-md-8">
					<h3>9<%if(projectidlist.size()>1) {%> (<%=(fch++) %>) <%} %>.  Overall Financial Status</h3>
					</div>
					<div class="col-md-1 bp-22" align="right"  ><b class="bp-20"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="bp-18"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					</div>
				<div class="content">
					<%
						double totSanctionCost=0,totReSanctionCost=0,totFESanctionCost=0;
	                	double totExpenditure=0,totREExpenditure=0,totFEExpenditure=0;
	                 	double totCommitment=0,totRECommitment=0,totFECommitment=0,totalDIPL=0,totalREDIPL=0,totalFEDIPL=0;
		                double totBalance=0,totReBalance=0,totFeBalance=0,btotalRe=0,btotalFe=0;
						%>
					<% if (ProjectDetail.size() > 1) { %>
					<div>
						<b>Project : <%=ProjectDetail.get(z)[1]%> <% if (z != 0) {  %>(SUB)<% }  %> </b>
					</div>
					<% } %>

					<br>
					<table class="subtables" >
						<thead>
							<tr>
								<td colspan="14" class="text-right border-0"><span
									class="currency"><b>(In &#8377; Crore)</b></span></td>
							</tr>
							<tr>
								<th colspan="2" class="bp-71">Head </th>
								<th colspan="2" class="bp-72">Sanction </th>
								<th colspan="2" class="bp-72">Expenditure </th>
								<th colspan="2" class="bp-72">Out Commitment </th>
								<th colspan="2" class="bp-72">Balance </th>
								<th colspan="2" class="bp-72">DIPL </th>
								<th colspan="2" class="bp-72">Notional Balance </th>
							</tr>
							<tr>
								<th class="width30 text-center">SN</th>
								<th class="width180" width="10">Head</th>
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
			          <%if(IsIbasConnected==null || IsIbasConnected.equalsIgnoreCase("Y")) { %>
			                    <tbody>
			                    <% 

				                int count=1;
			                        if(projectFinancialDetails!=null && projectFinancialDetails.size()>0 && projectFinancialDetails.get(z)!=null ){
			                      for(ProjectFinancialDetails projectFinancialDetail:projectFinancialDetails.get(z)){                       %>
			 
			                         <tr>
										<td align="center" class="bp-73"><%=count++ %></td>
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
			   <td 	align="center"class="bp-74"><%=++count %></td>
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
					</table></div></div>
			<% } %>
			<!-- ----------------------------------------  Overall Financial Status Div ----------------------------------------------------- -->

			<!-- ---------------------------------------- P-10  Action Plan for Div ----------------------------------------------------- -->

			<div class="carousel-item ">
					<div class="content-header row ">
					<div class="col-md-1" ><img class="bp-18"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					<div class="col-md-1 bp-19" align="left"  ><b class="bp-20"><%=ProjectCode %></b>
					<h6 class="bp-21"><%=pdc %></h6>
					</div>
					<div class="col-md-8">
					<h3>
							<% if (CommitteeCode.equalsIgnoreCase("EB")) { %>
								10. Action Plan for Next Six Months
							<% } else { %>
								10. Action Plan for Next Three Months
							<% } %>
						</h3>
					</div>
					<div class="col-md-1 bp-22" align="right"  ><b class="bp-20"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="bp-18"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					</div>
				

				<div class="content">
					<% for (int z = 0; z < 1; z++) { %>
					<% if (ProjectDetail.size() > 1) { %>
					<div>
						<b>Project : <%=ProjectDetail.get(z)[1]%> <% if (z != 0) {  %>(SUB)<% }  %> </b>
					</div>
					<% } %>

					<!-- CALL Pfms_Milestone_PDC_New(:projectid, :interval) -->
					<table class="subtables bp-55" >
						<thead>
							<tr>
								<td colspan="10" class="border-0">
									<p class="bp-49">
										<span class="notassign">NA</span> : Not Assigned &nbsp;&nbsp;
										<span class="assigned">AA</span> : Activity Assigned &nbsp;&nbsp; 
										<span class="ongoing">OG</span> : On Going &nbsp;&nbsp; 
										<span class="delay">DO</span> : Delay - On Going &nbsp;&nbsp; 
										<span class="ongoing">RC</span> : Review & Close &nbsp;&nbsp; 
										<span class="delay">FD</span> : Forwarded With Delay &nbsp;&nbsp; 
										<!-- <span class="completed">CO</span> :Completed &nbsp;&nbsp; 
										<span class="completeddelay">CD</span> : Completed with Delay &nbsp;&nbsp;  -->
										<span class="inactive">IA</span> : InActive &nbsp;&nbsp; 
										<span class="delaydays">DD</span> : Delayed days &nbsp;&nbsp;
									</p>
								</td>
							</tr>

							<tr>
								<th class="width15">SN</th>
								<th class="width20">MS</th>
								<th class="width50">L</th>
								<th class="width265">Action Plan</th>
								<th class="width100" >Start Date</th>
								<th class="width110">PDC</th>
								<%if(!session.getAttribute("labcode").toString().equalsIgnoreCase("ADE")) {%>
									<th class="width200">Responsibility </th>
									<%} %>
								<th class="width50">Progress</th>
								<th class="width50 pr-1">Status(DD)</th>
								<th class="width220">Remarks</th>
								<th class="width30">Info</th>
							</tr>
						</thead>
						<tbody>
							<%
							if (actionplanthreemonths.get(z).size() > 0) {
								long count1 = 1;
								int countA = 1;
								int countB = 1;
								int countC = 1;
								int countD = 1;
								int countE = 1;

								int serialno = 1;
								for (Object[] obj : actionplanthreemonths.get(z)) {

									if (Integer.parseInt(obj[26].toString()) <= Integer.parseInt(levelid)) {
								/*  if(obj[26].toString().equals("0")||obj[26].toString().equals("1")){ */
							%>

							<tr>
								<td class="text-center"><%=serialno%></td>
								<td class="text-center">M<%=obj[22]%></td>
								<td class="text-center">
									<%
									if (obj[26].toString().equals("0")) {
									%> <!-- L --> <%
									 countA = 1;
									 countB = 1;
									 countC = 1;
									 countD = 1;
									 countE = 1;
									 } else if (obj[26].toString().equals("1")) {
											for(Map.Entry<Integer,String>entry:treeMapLevOne.entrySet()){
												if(entry.getKey().toString().equalsIgnoreCase(obj[2].toString())){%>
													<%=entry.getValue() %>
											<%}}  
									 %> 
									 
									 
									 <%-- A-<%=countA%>  --%><%
									/*  countA++;
									 countB = 1;
									 countC = 1;
									 countD = 1;
									 countE = 1; */
									 } else if (obj[26].toString().equals("2")) {
										 
										 for(Map.Entry<Integer,String>entry:treeMapLevTwo.entrySet()){
												if(entry.getKey().toString().equalsIgnoreCase(obj[3].toString())){%>
													<%=entry.getValue() %>
											<%}}  
									 %> <%-- B-<%=countB%> --%> <%
								/* 	 countB += 1;
									 countC = 1;
									 countD = 1;
									 countE = 1; */
									 } else if (obj[26].toString().equals("3")) {
									 %> C-<%=countC%> <%
									 countC += 1;
									 countD = 1;
									 countE = 1;
									 } else if (obj[26].toString().equals("4")) {
									 %> D-<%=countD%> <%
									 countD += 1;
									 countE = 1;
									 } else if (obj[26].toString().equals("5")) {
									 %> E-<%=countE%> <%
									 countE++;
									 }
									 %>
								</td>

								<td class="<%if (obj[26].toString().equals("0")) { %>font-weight-bold <%}%> text-justify ">
									<% if (obj[26].toString().equals("0")) { %> <%= (obj[9].toString())%> 
									<%}else if (obj[26].toString().equals("1")) { %>&nbsp;&nbsp;<%= (obj[10].toString())%> 
									<%}else if (obj[26].toString().equals("2")) { %>&nbsp;&nbsp;<%= (obj[11].toString())%> 
									<%}else if (obj[26].toString().equals("3")) { %>&nbsp;&nbsp;<%= (obj[12].toString())%> 
									<%}else if (obj[26].toString().equals("4")) { %>&nbsp;&nbsp;<%= (obj[13].toString())%> 
									<%}else if (obj[26].toString().equals("5")) { %>&nbsp;&nbsp;<%= (obj[14].toString())%>
									<%}%>
								</td>
								<td class="text-center" ><%= obj[7]!=null? sdf.format(sdf1.parse(obj[7].toString())) : " - " %></td>
								<td class="text-center">
									<%
									if (!LocalDate.parse(obj[8].toString()).isEqual(LocalDate.parse(obj[29].toString())) ) {%>
									<span <%if(LocalDate.parse(obj[8].toString()).isBefore(LocalDate.parse(todayDate))) {%> class="bp-50"<%}else{ %>class="font-weight-bold"<%} %>><%=sdf.format(sdf1.parse(obj[8].toString()))%></span><br> 
									<%}%> 
								<span <% if (LocalDate.parse(obj[8].toString()).isEqual(LocalDate.parse(obj[29].toString()))&& LocalDate.parse(obj[29].toString()).isBefore(LocalDate.parse(todayDate))){%>class="bp-50"<%}else if(LocalDate.parse(obj[8].toString()).isEqual(LocalDate.parse(obj[29].toString()))) {%>class="font-weight-bold"<%} %>><%=sdf.format(sdf1.parse(obj[29].toString()))%></span>
								</td>
																<%if(!session.getAttribute("labcode").toString().equalsIgnoreCase("ADE")) {%>
								
								<td><%=obj[24]%><%-- (<%=obj[25] %>) --%></td>
								
								<%} %>
								<td class="text-center""><%=obj[16]%>%</td>
								
								<% 
									LocalDate StartDate = LocalDate.parse(obj[7].toString());
									LocalDate EndDate = LocalDate.parse(obj[8].toString());
									LocalDate OrgEndDate = LocalDate.parse(obj[29].toString());
									int Progess = Integer.parseInt(obj[16].toString());
									LocalDate CompletionDate =obj[18]!=null ? LocalDate.parse(obj[18].toString()) : null;
									LocalDate Today = LocalDate.now();
								%>
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
								<td>
									<% if (obj[28] != null) { %> <%=(obj[28].toString())%> <% } %>
								</td>
								<td class="text-center">
						
									<a data-toggle="modal" data-target="#exampleModal1" data-id="milestonemodal<%=obj[0]%>" class="milestonemodal" data-whatever="@mdo" class="bp-57"> 
										<i class="fa fa-info-circle fa-lg bp-3"  aria-hidden="true"></i>
									</a>
		
								</td>
							</tr>
							<% count1++;
							serialno++; }
								} %>
							<% } else { %>
								<tr>
									<td colspan="10" class="text-center">Nil</td>
								</tr>
							<% } %>
						</tbody>
					</table>
					<% } %>
				</div>
			</div>
			<!-- ---------------------------------------- Action Plan for Div ----------------------------------------------------- -->
			
			
			<!-- ----------------------------------------11. Valuation of Technology  Div ----------------------------------------------------- -->
			
			
			<jsp:include page="briefingPaper/BriefingPresentationTwo.jsp" />
			
			<!-- ----------------------------------------12. Economic Impact  Div ----------------------------------------------------- -->
			
			<!-- ---------------------------------------- P-13  GANTT chart of overall project Div ----------------------------------------------------- -->

			<div class="carousel-item ">

	
				
					<div class="content-header row ">
					<div class="col-md-1" ><img class="bp-18"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					<div class="col-md-1 bp-19" align="left"  ><b class="bp-20"><%=ProjectCode %></b>
					<h6 class="bp-21"><%=pdc %></h6>
					</div>
					<div class="col-md-8">
					<h3>13. GANTT Chart of Overall Project Schedule</h3>
					</div>
					<div class="col-md-1 bp-22" align="right"  ><b class="bp-20"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="bp-18"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					</div>


				<div class="content">
				<jsp:include page="BpGrantChart.jsp" />
				</div>
			</div>
			<!-- ---------------------------------------- GANTT chart of overall project Div ----------------------------------------------------- -->
			<!-- ---------------------------------------- P-14 Issues Div ----------------------------------------------------- -->
			<div class="carousel-item ">
					<div class="content-header row ">
					<div class="col-md-1" ><img class="bp-18"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					<div class="col-md-1 bp-19" align="left"  ><b class="bp-20"><%=ProjectCode %></b>
					<h6 class="bp-21"><%=pdc %></h6>
					</div>
					<div class="col-md-8">
						<h3>14. Issues</h3>
					</div>
					<div class="col-md-1 bp-22" align="right"  ><b class="bp-20"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="bp-18"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					</div>

				<div class="content">
					<% for (int z = 0; z < 1; z++) { %>

					<% if (ProjectDetail.size() > 1) { %>
					<div>
						<b>Project : <%=ProjectDetail.get(z)[1]%> <% if (z != 0) {  %>(SUB)<% }  %> </b>
					</div>
					<% } %>
					<!-- CALL Old_Issues_List(:projectid); -->
					<table class="subtables bp-55" >
						<thead>
							<tr>
								<td colspan="7" class="border=0">
									<p class="bp-49">
										<span class="notassign">NA</span> : Not Assigned &nbsp;&nbsp;
										<span class="assigned">AA</span> : Activity Assigned &nbsp;&nbsp; 
										<span class="ongoing">OG</span> : On Going &nbsp;&nbsp; 
										<span class="delay">DO</span> : Delay - On Going &nbsp;&nbsp; 
										<span class="ongoing">RC</span> : Review & Close &nbsp;&nbsp; 
										<span class="delay">FD</span> : Forwarded With Delay &nbsp;&nbsp; 
										<span class="completed">CO</span> :Completed &nbsp;&nbsp; 
										<span class="completeddelay">CD</span> : Completed with Delay &nbsp;&nbsp; 
										<span class="inactive">IA</span> : InActive &nbsp;&nbsp; 
										<span class="delaydays">DD</span> : Delayed days &nbsp;&nbsp;
									</p>
								</td>
							</tr>
							<tr>
								<th class="width20">SN</th>
								<th class="width20">ID</th>
								<th class="width350">Issue Point</th>
								<th class="width100">ADC <br> PDC</th>
								<!-- <th style="width: 100px;">ADC</th> -->
								<th class="width200">Responsibility</th>
								<th class="width50">Status(DD)</th>
								<th class="width220">Remarks</th>
							</tr>
						</thead>
						<tbody>
							<% if (oldpmrcissueslist.get(z).size() == 0) { %>
							<tr>
								<td colspan="7" class="text-center">Nil</td>
							</tr>
							<% } else if (oldpmrcissueslist.get(z).size() > 0) {
							int i = 1;
							for (Object[] obj : oldpmrcissueslist.get(z)) {
								if(!obj[9].toString().equals("C")  || (obj[9].toString().equals("C") && obj[13]!=null && before6months.isBefore(LocalDate.parse(obj[13].toString())) )){ %>
							<tr>
								<td class="text-center"><%=i%></td>
									<td class="text-center" >
									<%if(obj[18]!=null && Long.parseLong(obj[18].toString())>0){
										String []temp=obj[1].toString().split("/");
										String tempString=temp[temp.length-1];
										%>
										<button type="button" class="btn btn-sm font-weight-bold"  onclick="ActionDetails( <%=obj[18] %>);" data-toggle="tooltip" data-placement="bottom" title="Action Details" >
										<%=tempString %>
										</button>
									<%}%>
								</td>
								<td class="text-justify"> <%=(obj[2].toString())%> </td>
								<td class="text-justify">
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
									<%if(!pdcorg.equals(endDate)) {%>
									<%=sdf.format(sdf1.parse(obj[4].toString()))%><br>
									<%} %>
									<%=sdf.format(sdf1.parse(obj[3].toString()))%>
								</td>
						<!-- 		<td style="text-align: center;">

								</td> -->
								<td><%=obj[11]%><%-- <%=obj[12] %> --%></td>
								<td class="text-center">
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
								<td>
									<% if (obj[17] != null) { %> <%=(obj[17].toString() )%> <% } %>
								</td>
							
							</tr>
							<% i++; }}
							} %>
						</tbody>
					</table>
					<% } %>
				</div>

			</div>

			<!-- ---------------------------------------- P-15a  Other Relevant Points Div ----------------------------------------------------- -->

			<div class="carousel-item ">
				   <div class="content-header row ">
					<div class="col-md-1" ><img class="bp-18"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					<div class="col-md-1 bp-19" align="left"  ><b class="bp-20"><%=ProjectCode %></b>
					<h6 class="bp-21"><%=pdc %></h6>
					</div>
					<div class="col-md-8">
						<h3>15 (a) Other Relevant Points</h3>
					</div>
					<div class="col-md-1 bp-22" align="right"  ><b class="bp-20"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="bp-18"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					</div>
				
				<div class="content">
					<% for (int z = 0; z < 1; z++) { %>
					<div align="left">
						<b>Project : <%=ProjectDetail.get(z)[1]%> <% if (z != 0) {  %>(SUB)<% }  %> </b>
					</div>
					<div class="card-body width-100" >
						<form action="TechnicalWorkDataAdd.htm" method="post">
							<div class="row" align="left">
								<div class="row width-100 ml-1 mt-1" >
									<div class="col-12">
										<div>
							<% if (TechWorkDataList.get(z) != null) { %> <%=TechWorkDataList.get(z)[2]%> <%}%>
						</div></div></div></div>
						</form>
					</div>
					<% } %>
				</div>

			</div>


			<!-- ----------------------------------------p-15a Other Relevant Points Div ----------------------------------------------------- -->

			<!-- ---------------------------------------- P-15b Technical Work Carried Div ----------------------------------------------------- -->

			<div class="carousel-item ">

 					 <div class="content-header row ">
					<div class="col-md-1" ><img class="bp-18"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					<div class="col-md-1 bp-19" align="left"  ><b class="bp-20"><%=ProjectCode %></b>
					<h6 class="bp-21"><%=pdc %></h6>
					</div>
					<div class="col-md-8">
						<h3>
							<% if (CommitteeCode.equalsIgnoreCase("EB")) { %>
								15 (b) Technical Work Carried Out For Last Six Months
							<% } else { %>
								15 (b) Technical Work Carried Out For Last Three Months
							<% } %>
						</h3>
					</div>
					<div class="col-md-1 bp-22" align="right"  ><b class="bp-20"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="bp-18"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					</div>

				<div class="content">

					<% ArrayList<String> FileExtList = new ArrayList<String>( Arrays.asList("jpg", "jpeg", "png", "pdf", "JPG", "JPEG", "PNG", "PDF"));
					
					for (int z = 0; z < 1; z++) {
					%>
					<div class="card-body width-100" >

						<div class="row" align="center" >
							<div class="col-6 text-left">

								<form action="TechnicalWorkDataAdd.htm" method="post">
									<% if (TechWorkDataList.get(z) != null) { %>
									<input type="hidden" name="TechDataId" value="<%=TechWorkDataList.get(z)[0]%>"> <b>Project : <%=ProjectDetail.get(z)[1]%> <% if (z != 0) {  %>(SUB)<% }  %> </b> <span class="mainsubtitle">Technical Work Carried (Attachment)</span>
									<% if (TechWorkDataList.get(z)[3] != null && Long.parseLong(TechWorkDataList.get(z)[3].toString()) > 0) { %>
									<button type="button" class="btn" title="Download Document"
										onclick="FileDownload1('<%=TechWorkDataList.get(z)[3]%>');">
										<i class="fa fa-download" aria-hidden="true"> </i>
									</button>
									<input type="hidden" class="hidden" name="attachid"
										id="attachid_<%=projectidlist.get(z)%>"
										value="<%=TechWorkDataList.get(z)[3]%>">
									<% } else { %>
									<input type="hidden" class="hidden" name="attachid"
										id="attachid_<%=projectidlist.get(z)%>" value="0">
									<%}%><% } %>
								</form>
							</div>
						</div>
						<br>
						<div class="row">
							<div class="col-md-12 bp-76" >
									
								<% 
								try{
								if (TechWorkDataList.get(z) != null && TechWorkDataList.get(z)[3] != null && Long.parseLong(TechWorkDataList.get(z)[3].toString()) > 0) { %>
								<% Object[] TechWork = TechWorkDataList.get(z);
								String fileExt = FilenameUtils.getExtension(TechWork[8].toString());
								
								if (fileExt.equalsIgnoreCase("pdf") ) { %>
								<iframe 
								  src="<%=baseUrl%>/techFilePreview.htm/<%=projectid %>"
								  style="width:100%;height:70vh;"
								  id="pearl<%=ProjectDetail.get(z)[0]%>">
								</iframe>
							
								<%}}}
								catch(Exception e){
									
								}
								
								%>
							</div>
							</div>
							</div>
					<% } %>
				</div></div>
			<!-- ---------------------------------------- p-15b Technical Work Carried Div ----------------------------------------------------- -->

			<!-- ---------------------------------------- P-15c  Technical Images Div ----------------------------------------------------- -->
			<div class="carousel-item ">
					<div class="content-header row ">
					<div class="col-md-1" ><img class="bp-18"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					<div class="col-md-1 bp-19" align="left"  ><b class="bp-20"><%=ProjectCode %></b>
					<h6 class="bp-21"><%=pdc %></h6>
					</div>
					<div class="col-md-8">
							<h3>15 (c) Technical Images</h3>
					</div>
					<div class="col-md-1 bp-22" align="right"  ><b class="bp-20"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="bp-18"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					</div>
				<div class="content">
					<% for (int z = 0; z < 1; z++) { %>
					<div align="left"> <b class="bp-79">Project : <%=ProjectDetail.get(z)[1]%><% if (z != 0) { %>(SUB<% } %></b> </div>
					<div align="center">
						<span class="mainsubtitle">Technical Images</span>
						<hr>
						<% if (TechImages.size() > 0) {
							List<TechImages> TechImagesList = TechImages.get(z);
							if (TechImagesList.size() > 0) {
								for (TechImages imges : TechImagesList) { %>
								<% Path imagePath = Paths.get(filePath,projectLabCode,"TechImages",(imges.getTechImagesId() + "_" + imges.getImageName()));
								if (imagePath.toFile().exists()) { %>
								<img data-enlargable class="bp-80" src="data:image/*;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(imagePath.toFile()))%>">
								<hr>
								<%}%><%}}}%>
					</div>
					<%}%>
				</div>
			</div>

		<!-- ---------------------------------------- p-15c Technical Images Div ----------------------------------------------------- -->
		
			<!-- ---------------------------------------- Issues Div ----------------------------------------------------- -->
			<!-- ---------------------------------------- P-16 Decision/Recommendations sought Div ----------------------------------------------------- -->
			<div class="carousel-item ">
	
					<div class="content-header row ">
					<div class="col-md-1" ><img class="bp-18"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					<div class="col-md-1 bp-19" align="left"  ><b class="bp-20"><%=ProjectCode %></b>
					<h6 class="bp-21"><%=pdc %></h6>
					</div>
					<div class="col-md-8">
						<h3> 16. Decision/Recommendations Sought from <%=CommitteeCode%> </h3>
					</div>
					<div class="col-md-1 bp-22" align="right"  ><b class="bp-20"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="bp-18"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					</div>
				
				<div class="content align-items-center" align="left">
					<table class="subtables bp-48 width-100" >
											<thead>
												<tr><th class="width-5">SN</th><th class="width-5">Type</th><th class="width-85">Details</th></tr>
											</thead>
											<tbody>
												<%int i=0; if(RecDecDetails!=null && RecDecDetails.size()>0){ 
												for(Object[] obj :RecDecDetails){%>
												<tr>
													<td class="width-5 text-center"> <%=++i%></td>
													<td class="width-5 text-center"> <%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()):""%></td>
													<td class="width-85 bp-75">  <%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()):""%></td>
												</tr>
												<%}}else{%><td colspan="3" class="text-center"> No Data Available!</td><%}%>
											</tbody>
					</table>
				</div>
			</div>
			<!-- ---------------------------------------- Decision/Recommendations sought Div ----------------------------------------------------- -->

			
		<!-- ---------------------------------------- P-17  Thank you Div ----------------------------------------------------- -->


			<div class="carousel-item ">
				<div class="content bp-81">
					<!-- <div style=" position: absolute ;top: 40%;left: 34%;">
						<h1 style="font-size: 5rem;">Thank You !</h1>
					</div> -->
					<div class="content" >
					<%if(thankYouImg!=null ){ %>
					<img class="bp-82"   src="data:image/*;base64,<%=thankYouImg%>"  > 
						<%}else{ %>
							<div class="bp-83">
						<h1 class="bp-92">Thank You !</h1>
					</div>
				<%} %>
				</div>
				</div>
			</div>
		<!-- ---------------------------------------- P-17  Thank you Div ----------------------------------------------------- -->
		</div>
		<a class="carousel-control-prev bp-84" href="#presentation-slides" role="button" data-slide="prev" > <span aria-hidden="true">
			<i class="fa fa-chevron-left fa-2x bp-86"  aria-hidden="true"></i></span> <span class="sr-only">Previous</span>
		</a> <a class="carousel-control-next bp-85" href="#presentation-slides" role="button" data-slide="next" > <span aria-hidden="true">
			<i class="fa fa-chevron-right fa-2x bp-86"  aria-hidden="true"></i></span> <span class="sr-only">Next</span>
		</a>
		<%int slideCount=0;%>
		<ol class="carousel-indicators">
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>"  class="carousel-indicator active" data-toggle="tooltip" data-placement="top" title="Start"><b><i class="fa fa-home" aria-hidden="true"></i></b></li>
			<%char ch1='a';for (int z = 0; z < projectidlist.size(); z++) {%>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>"  class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="1. Project Attributes (<%=ProjectDetail.get(z)[1]%>)"><b>1 <%if(projectidlist.size()>1) {%>(<%=(char)(ch1++)%>)<%} %></b></li>
			<%} %>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="2 (a) System Configuration"><b>2 (a) </b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="2 (b) System Specifications"><b>2 (b) </b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="3. Overall Product tree/WBS"><b>3</b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title='4 (a) <%if (committee.getCommitteeShortName().trim().equalsIgnoreCase("PMRC") || committee.getCommitteeShortName().trim().equalsIgnoreCase("PMRB")) {%> Approval <%} else {%> Ratification <%}%>  of recommendations of last <%=committee.getCommitteeShortName().trim().toUpperCase()%> Meeting'><b>4(a)</b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="4 (b) Last <%=committee.getCommitteeShortName().trim().toUpperCase()%> Meeting action points with Probable Date of completion (PDC), Actual Date of Completion (ADC) and status"><b>4(b)</b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="4 (c) Details of Technical/ User Reviews"><b>4 (c)</b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="5. Milestones achieved prior to this Meeting"><b>5</b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="6. Review of SunSet MileStones"><b>6</b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="7 (a) Work carried out, Achievements, test result etc"><b>7(a)</b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="7 (b) TRL table with TRL at sanction stage and current stage indicating overall PRI"><b>7(b)</b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="7 (c) Risk Matrix/Management Plan/Status."><b>7 (c)</b></li>
			<%int chapter7a=1 ; int chapter7b=1;for (int z = 0; z < projectidlist.size(); z++) {%>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="8 (a<%if(projectidlist.size()>1) {%><%="."+chapter7a%><%} %>) Details of Procurement Plan (<%=ProjectDetail.get(z)[1]%>)"><b>8(a<%if(projectidlist.size()>1) {%><%="."+chapter7a++%><%} %>)</b></li>
			
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="8 (b<%if(projectidlist.size()>1) {%><%="."+chapter7b%><%} %>) Procurement Status (<%=ProjectDetail.get(z)[1]%>)"><b>8(b<%if(projectidlist.size()>1) {%><%="."+chapter7b++%><%} %>)</b></li>
			<%} %>
				<%char ch6='a';for (int z = 0; z < projectidlist.size(); z++) {%>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="9. (<%=(char)(ch6) %>)  Overall Financial Status <%if(projectidlist.size()>1) {%>(<%=ProjectDetail.get(z)[1]%>) <%}%>"><b>9<%if(projectidlist.size()>1) {%>(<%=(char)(ch6++)%>)<%} %></b></li>
			<%} %>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="10. Action Plan"><b>10</b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="11. Valuation Of Technologies"><b>11</b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="12. Economic Impact of Project"><b>12</b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="13. GANTT chart of overall project schedule"><b>13</b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="14. Issues"><b>14</b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="15 (a) Other Relevant Points"><b>15 (a)</b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="15 (b) Technical Work Carried out"><b>15 (b)</b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="15 (c) Technical Images"><b>15 (c)</b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="16. Decision/Recommendations"><b>16</b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="Thank You"><b>End</b></li>
			<li data-slide-to="21"  class="carousel-indicator content_full_screen bp-87" data-toggle="tooltip" data-placement="top" title="Full Screen Mode"><b><i class="fa fa-expand fa-lg" aria-hidden="true"></i></b></li>
			<li data-slide-to="21"  class="carousel-indicator content_reg_screen bp-87" data-toggle="tooltip" data-placement="top" title="Exit Full Screen Mode"><b><i class="fa fa-compress fa-lg" aria-hidden="true"></i></b></li>
		</ol>
	</div>
	<!-- ------------------------------------------------- MODALS -------------------------------------------------- -->

	<div class="modal fade " id="exampleModal1" tabindex="-1" aria-labelledby="exampleModalLabel1" aria-hidden="true">
		<div class="modal-dialog modal-xl modal-dialog-jump">
			<div class="modal-content">
				<div class="modal-body">
					<div class="container-fluid">
						<div class="row">
							<div class="col-md-12">
								<div class="card shadow-nohover" >
									<div class="row card-header">
										<div class="col-md-10">
											<h5>
												<%
												if (ProjectId != null) {
													Object[] ProjectDetail123 = (Object[]) request.getAttribute("ProjectDetailsMil");
												%>
												<%=ProjectDetail123[2]%>
												(<%=ProjectDetail123[1]%>)<%}%>
											</h5>
										</div>
									</div>
									<div class="card-body">
										<div class="table-responsive">
											<table class="table  table-hover table-bordered">
												<thead>
													<tr>
														<th>Expand</th>
														<th class="bp-88">Mil-No</th>
														<th class="bp-89">Milestone Activity</th>
														<th>Start Date</th>
														<th>End Date</th>
														<th class="bp-89">First OIC</th>
														<th class="bp-90">Weightage</th>
														<th class="bp-91">Progress</th>

													</tr>
												</thead>
												<tbody>
													<%
													int count = 1;
													if (MilestoneList != null && MilestoneList.size() > 0) {
														for (Object[] obj : MilestoneList) {
													%>
													<tr class="milestonemodalwhole"
														id="milestonemodal<%=obj[5]%>">
														<td class="width-2 text-center"><span
															class="clickable" data-toggle="collapse"
															id="row<%=count%>" data-target=".row<%=count%>">
																<button class="btn btn-sm btn-success"
																	id="btn<%=count%>"
																	onclick="ChangeButton('<%=count%>')">
																	<i class="fa fa-plus" id="fa<%=count%>"></i>
																</button>
														</span></td>
														<td class="text-center width-7">Mil-<%=obj[5]%></td>
														<%-- <td class="width-30px"><%=obj[1]%></td> --%>
														<td class="bp-93"
															><%=(obj[4].toString())%></td>

														<td class="width-8"><%=sdf.format(obj[2])%></td>
														<td class="width-8"><%=sdf.format(obj[3])%></td>
														<td class="width-15"><%=obj[6]%></td>
														<td class="width-9" align="center"><%=obj[13]%></td>
														<td>
															<%
															if (!obj[12].toString().equalsIgnoreCase("0")) {
															%>
															<div class="progress bp-94">
																<div
																	class="progress-bar progress-bar-striped
															<%if (obj[14].toString().equalsIgnoreCase("2")) {%>
															 bg-success
															<%} else if (obj[14].toString().equalsIgnoreCase("3")) {%>
															  bg-info
															<%} else if (obj[14].toString().equalsIgnoreCase("4")) {%>
															  bg-danger
															<%} else if (obj[14].toString().equalsIgnoreCase("5")) {%>
															  bg-warning
															<%}%> width-<%=obj[12] %>
															"
																	role="progressbar" 
																	aria-valuenow="25" aria-valuemin="0"
																	aria-valuemax="100">
																	<%=obj[12]%>
																</div>
															</div> <%
															 } else {
															 %>
															<div class="progress bp-94"
																>
																<div class="progress-bar bp-95" role="progressbar"
																	>
																	Not Started</div>
															</div> <%
															 }
															 %>
														</td>
													</tr>
													<tr class="collapse row<%=count%> font-weight-bold"
														>
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
													<%
													int countA = 1;
													List<Object[]> MilestoneA = (List<Object[]>) request.getAttribute(count + "MilestoneActivityA");
													if (MilestoneA != null && MilestoneA.size() > 0) {
														for (Object[] objA : MilestoneA) {
															List<Object[]> MilestoneB = (List<Object[]>) request.getAttribute(count + "MilestoneActivityB" + countA);
													%>
													<tr class="collapse row<%=count%>">
														<td class="width-2" class="center"></td>
														<td class="width-5"">A-<%=countA%></td>
														<%-- <td class="width-30px"><%=obj[1]%></td> --%>
														<td class="bp-93"
															><%=(objA[4].toString())%></td>

														<td class="width-30px"><%=sdf.format(objA[2])%></td>
														<td class="width-8"><%=sdf.format(objA[3])%></td>


														<td class="width-30px">
															<%
															if (objA[9].toString().equalsIgnoreCase("3") || objA[9].toString().equalsIgnoreCase("5")) {
															%>
															<%
															if (objA[7] != null) {
															%> <%=sdf.format(objA[7])%> <%
															 } else {
															 %><%=objA[8]%>
															<%
															}
															%> <%
															 } else {
															 %> <%=objA[8]%> <%
															 }
															 %>
														</td>
														<td align="center"><%=objA[6]%></td>
														<td>
															<%
															if (!objA[5].toString().equalsIgnoreCase("0")) {
															%>
															<div class="progress bp-94"
																>
																<div
																	class="progress-bar progress-bar-striped
															<%if (objA[9].toString().equalsIgnoreCase("2")) {%>
															 bg-success
															<%} else if (objA[9].toString().equalsIgnoreCase("3")) {%>
															  bg-info
															<%} else if (objA[9].toString().equalsIgnoreCase("4")) {%>
															  bg-danger
															<%} else if (objA[9].toString().equalsIgnoreCase("5")) {%>
															  bg-warning
															<%}%> width-<%=objA[5] %>
															"
																	role="progressbar" 
																	aria-valuenow="25" aria-valuemin="0"
																	aria-valuemax="100">
																	<%=objA[5]%>
																</div>
															</div> <%
															 } else {
															 %>
															<div class="progress bp-94"
															>
																<div class="progress-bar bp-95" role="progressbar"
																	>
																	Not Started</div>
															</div> <%
															 }
															 %>
														</td>
														<td>
														</td>
													</tr>
													<%
													int countB = 1;
													if (MilestoneB != null && MilestoneB.size() > 0) {
														for (Object[] objB : MilestoneB) {
															List<Object[]> MilestoneC = (List<Object[]>) request.getAttribute(count + "MilestoneActivityC" + countA + countB);
													%>
													<tr class="collapse row<%=count%>">
														<td  class="center width-2"></td>
														<td class="width-2">
															&nbsp;&nbsp;&nbsp;B-<%=countB%></td>
														<%-- <td class="width-30px"><%=obj[1]%></td> --%>
														<td class="bp-93"
															><%=(objB[4].toString())%></td>

														<td class="width-30px"><%=sdf.format(objB[2])%></td>
														<td class="width-8"><%=sdf.format(objB[3])%></td>

														<td class="width-30px">
															<%
															if (objB[9].toString().equalsIgnoreCase("3") || objB[9].toString().equalsIgnoreCase("5")) {
															%>
															<%
															if (objB[7] != null) {
															%> <%=sdf.format(objB[7])%> <%
															 } else {
															 %><%=objB[8]%>
															<%
															}
															%> <%
															 } else {
															 %> <%=objB[8]%> <%
															 }
															 %>
														</td>
														<td align="center"><%=objB[6]%></td>
														<td>
															<%
															if (!objB[5].toString().equalsIgnoreCase("0")) {
															%>
															<div class="progress bp-94">
																<div
																	class="progress-bar progress-bar-striped
															<%if (objB[9].toString().equalsIgnoreCase("2")) {%>
															 bg-success
															<%} else if (objB[9].toString().equalsIgnoreCase("3")) {%>
															  bg-info
															<%} else if (objB[9].toString().equalsIgnoreCase("4")) {%>
															  bg-danger
															<%} else if (objB[9].toString().equalsIgnoreCase("5")) {%>
															  bg-warning
															<%}%> width-<%=objB[5] %>
															"
																	role="progressbar" 
																	aria-valuenow="25" aria-valuemin="0"
																	aria-valuemax="100">
																	<%=objB[5]%>
																</div>
															</div> <%
															 } else {
															 %>
															<div class="progress bp-94"
																>
																<div class="progress-bar bp-95" role="progressbar"
																	>
																	Not Started</div>
															</div> <%
															 }
															 %>
														</td>
														<td>
														</td>
													</tr>
													<%
													int countC = 1;
													if (MilestoneC != null && MilestoneC.size() > 0) {
														for (Object[] objC : MilestoneC) {
															List<Object[]> MilestoneD = (List<Object[]>) request.getAttribute(count + "MilestoneActivityD" + countA + countB + countC);
													%>
													<tr class="collapse row<%=count%>">
														<td  class="center width-2"></td>
														<td class="width-5">
															&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;C-<%=countC%></td>
														<%-- <td class="width-30px"><%=obj[1]%></td> --%>
														<td class="bp-93"
															><%=(objC[4].toString())%></td>

														<td class="width-30px"><%=sdf.format(objC[2])%></td>
														<td class="width-8"><%=sdf.format(objC[3])%></td>

														<td class="width-30px">
															<%
															if (objC[9].toString().equalsIgnoreCase("3") || objC[9].toString().equalsIgnoreCase("5")) {
															%>
															<%
															if (objC[7] != null) {
															%> <%=sdf.format(objC[7])%> <%
															 } else {
															 %><%=objC[8]%>
															<%
															}
															%> <%
															 } else {
															 %> <%=objC[8]%> <%
															 }
															 %>
														</td>
														<td align="center"><%=objC[6]%></td>
														<td>
															<%
															if (!objC[5].toString().equalsIgnoreCase("0")) {
															%>
															<div class="progress bp-94"
																>
																<div
																	class="progress-bar progress-bar-striped
															<%if (objC[9].toString().equalsIgnoreCase("2")) {%>
															 bg-success
															<%} else if (objC[9].toString().equalsIgnoreCase("3")) {%>
															  bg-info
															<%} else if (objC[9].toString().equalsIgnoreCase("4")) {%>
															  bg-danger
															<%} else if (objC[9].toString().equalsIgnoreCase("5")) {%>
															  bg-warning
															<%}%> width-<%=objC[5]%>
															"
																	role="progressbar" 
																	aria-valuenow="25" aria-valuemin="0"
																	aria-valuemax="100">
																	<%=objC[5]%>
																</div>
															</div> <%
															 } else {
															 %>
															<div class="progress bp-94"
																>
																<div class="progress-bar bp-95" role="progressbar"
																	>
																	Not Started</div>
															</div> <%
 }
 %>
														</td>
														<td></td>
													</tr>
													<%
													int countD = 1;
													if (MilestoneD != null && MilestoneD.size() > 0) {
														for (Object[] objD : MilestoneD) {
															List<Object[]> MilestoneE = (List<Object[]>) request.getAttribute(count + "MilestoneActivityE" + countA + countB + countC + countD);
													%>
													<tr class="collapse row<%=count%>">
														<td class="center width-2"></td>
														<td class="width-5">
															&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;D-<%=countD%></td>
														<%-- <td class="width-30px"><%=obj[1]%></td> --%>
														<td class="bp-93"
															>(objD[4].toString())</td>

														<td class="width-30px"><%=sdf.format(objB[2])%></td>
														<td class="width-8"><%=sdf.format(objB[3])%></td>

														<td class="width-30px">
														<%
														if (objD[9].toString().equalsIgnoreCase("3") || objD[9].toString().equalsIgnoreCase("5")) {
														%>
														<%
														if (objD[7] != null) {
														%> <%=sdf.format(objD[7])%> <%
														 } else {
														 %><%=objD[8]%>
														<%
														}
														%> <%
														 } else {
														 %> <%=objD[8]%> <%
														 }
														 %>
														</td>
														<td align="center"><%=objD[6]%></td>
														<td>
															<%
															if (!objD[5].toString().equalsIgnoreCase("0")) {
															%>
															<div class="progress bp-94"
																>
																<div
																	class="progress-bar progress-bar-striped
															<%if (objD[9].toString().equalsIgnoreCase("2")) {%>
															 bg-success
															<%} else if (objD[9].toString().equalsIgnoreCase("3")) {%>
															  bg-info
															<%} else if (objD[9].toString().equalsIgnoreCase("4")) {%>
															  bg-danger
															<%} else if (objD[9].toString().equalsIgnoreCase("5")) {%>
															  bg-warning
															<%}%> width-<%=objD[5] %>
															"
																	role="progressbar" 
																	aria-valuenow="25" aria-valuemin="0"
																	aria-valuemax="100">
																	<%=objD[5]%>
																</div>
															</div> <%
															 } else {
															 %>
															<div class="progress bp-94"
																>
																<div class="progress-bar bp-95" role="progressbar"
																	>
																	Not Started</div>
															</div> <%
															 }
															 %>
														</td>


														<td></td>
													</tr>
													<%
													int countE = 1;
													if (MilestoneE != null && MilestoneE.size() > 0) {
														for (Object[] objE : MilestoneE) {
													%>
													<tr class="collapse row<%=count%>">
														<td  class="center width-2"></td>
														<td class="width-5">
															&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;E-<%=countE%></td>
														<%-- <td class="width-30px"><%=obj[1]%></td> --%>
														<td class="bp-93"
															><%=(objE[4].toString())%></td>

														<td class="width-30px"><%=sdf.format(objE[2])%></td>
														<td class="width-8"><%=sdf.format(objE[3])%></td>

														<td class="width-30px">
															<%
															if (objE[9].toString().equalsIgnoreCase("3") || objE[9].toString().equalsIgnoreCase("5")) {
															%>
															<%
															if (objE[7] != null) {
															%> <%=sdf.format(objE[7])%> <%
															} else {
															 %><%=objE[8]%>
															<%
															}
															%> <%
															} else {
															%> <%=objE[8]%> <%
															}
															%>
														</td>
														<td align="center"><%=objE[6]%></td>
														<td>
															<%
															if (!objE[5].toString().equalsIgnoreCase("0")) {
															%>
															<div class="progress bp-93"
																>
																<div
																	class="progress-bar progress-bar-striped
															<%if (objC[9].toString().equalsIgnoreCase("2")) {%>
															 bg-success
															<%} else if (objE[9].toString().equalsIgnoreCase("3")) {%>
															  bg-info
															<%} else if (objE[9].toString().equalsIgnoreCase("4")) {%>
															  bg-danger
															<%} else if (objE[9].toString().equalsIgnoreCase("5")) {%>
															  bg-warning
															<%}%> width-<%=objE[5] %>
															"
																	role="progressbar" 
																	aria-valuenow="25" aria-valuemin="0"
																	aria-valuemax="100">
																	<%=objE[5]%>
																</div>
															</div> <%
														 } else {
														 %>
															<div class="progress bp-93"
																>
																<div class="progress-bar bp-94" role="progressbar"
																	>
																	Not Started</div>
															</div> <%
														 }
														 %>
														</td>
														<td>
														</td>
													</tr>
													<%
													countE++;
													}
													}
													%><%countD++;}}%><%countC++;}}%><%countB++;}}	%><%countA++;}} else {%>
													<tr class="collapse row<%=count%>">
														<td colspan="9"  class="center text-center">No
															Sub List Found</td>
													</tr>
													<%}%><%count++;}} else {%>
													<tr><td colspan="9"  class="center">No List Found</td></tr><%}%>
												</tbody>
											</table></div></div></div></div></div></div></div></div></div></div>


	<div class="modal fade" id="LevelModal" tabindex="-1" role="dialog"
		aria-labelledby="exampleModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg" role="document">
			<div class="modal-content">
				<div class="modal-header bg-2" >
					<h5 class="modal-title bp-54" id="exampleModalLabel"
						>Set Milestone
						Level</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<div class="row">
						<div class="col-md-4">
							<h6>
								<b>Project : </b>
								<% for (int z = 0; z < projectidlist.size(); z++) { %>
									<% if (z == 0) { %><%=ProjectDetail.get(z)[1]%> <% } %>
								<% } %>
							</h6>
						</div>
						<div class="col-md-3">
							<h6>
								<b>Committee :</b> <%=CommitteeCode%>
							</h6>
						</div>
						<div class="col-md-1">
							<b>Level</b>
						</div>
						<div class="col-md-4">
							<select class="form-control" name="LevelValue"
								required="required" data-live-search="true"
								data-container="body" id="levelvalue">
								<option <%if (levelid.equalsIgnoreCase("1")) {%> selected <%}%>
									value="1">Level A</option>
								<option <%if (levelid.equalsIgnoreCase("2")) {%> selected <%}%>
									value="2">Level B</option>
								<option <%if (levelid.equalsIgnoreCase("3")) {%> selected <%}%>
									value="3">Level C</option>
								<option <%if (levelid.equalsIgnoreCase("4")) {%> selected <%}%>
									value="4">Level D</option>
								<option <%if (levelid.equalsIgnoreCase("5")) {%> selected <%}%>
									value="5">Level E</option>
							</select>
						</div>

					</div>
				</div>
				<div class="modal-footer"></div>
			</div>
		</div>
	</div>



	<!-- ------------------------------------------------- MODALS -------------------------------------------------- -->


	<form method="POST" action="FileUnpack.htm" id="downloadform" target="_blank">
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
		<input type="hidden" name="FileUploadId" id="FileUploadId" value="" />
	</form>


	<form method="get" action="AgendaDocLinkDownload.htm" id="downloadform1" target="_blank">
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
		<input type="hidden" name="filerepid" id="filerepid" value="" />
	</form>

	<form method="POST" action="MilestoneLevelUpdate.htm" id="milestonelevelform">

		<input type="hidden" name="projectid" id="projectid"> 
		<input type="hidden" name="committeeid" id="committeeid"> 
		<input type="hidden" name="milestonelevelid" id="milestonelevelid" /> 
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	</form>

<!-- -------------------------------------------------------------- action modal ----------------------------------------------------- -->

	<div class=" modal bd-example-modal-lg" tabindex="-1" role="dialog" id="action_modal">
		<div class="modal-dialog modal-lg modal-dialog-jump" role="document">
			<div class="modal-content">
				<div class="modal-header bp-96" >
					<div class="row w-100"  >
						<div class="col-md-12" >
							<h5 class="modal-title bp-97" id="modal_action_no" ></h5>
						</div>
					</div>
					
					 <button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body" align="center">
					<form action="#" method="post" autocomplete="off"  >
						<table class="width-100">
							<tr>
								<td class="bp-98"> Action Item :</td>
								<td class="tabledata bp-99"  colspan="3" id="modal_action_item"></td>
							</tr>
							<tr>
								<td class="p-1 border-0 font-weight-bold"  >Assign Date :</td>
								<td class="p-1 border-0 font-weight-bold"  id="modal_action_date"></td>
								<td class="p-1 border-0 font-weight-bold"  >PDC :</td>
								<td class="p-1 border-0 font-weight-bold"  id="modal_action_PDC"></td>
							</tr>
							<tr>
								<td class="p-1 border-0 font-weight-bold" >Assignor :</td>
								<td  class="tabledata p-1 border-0 font-weight-bold" id="modal_action_assignor"></td>
								<td class="p-1 border-0 font-weight-bold" >Assignee :</td>
								<td class="tabledata p-1 border-0 font-weight-bold" id="modal_action_assignee"></td>
							</tr>
							<tr>
								<td class="p-1 border-0 font-weight-bold" >Final Progress :</td>
								<td class="p-1 border-0 font-weight-bold" id="modal_action_progress"></td>
								<td class="p-1 border-0 font-weight-bold" > Type :</td>
								<td class="bp-100" id="modal_action_type"></td>
							</tr>
							
						</table>
						</form>
						<hr>
						<form action="#" method="get">
						
						<table class="table table-bordered table-hover table-striped table-condensed  width-100" id="" >
							<thead> 
								<tr class="bp-101">
									<th class="text-center width-15">SN</th>
									<th class="text-center width-15">Progress Date</th>
									<th class="text-center width-15"> Progress</th>
									<th class="text-center width-60">Remarks</th>
									<th class="text-center width-5">Download</th>
								</tr>
							</thead>
							<tbody id="modal_progress_table_body">
								
							</tbody>
						</table>
						</form>
					
				</div>
				
			</div>
		</div>
	</div>

<!-- -------------------------------------------------------------- action modal ----------------------------------------------------- -->
<!-- -------------------------------------------- Risk Types Modal  -------------------------------------------------------- -->

		<div class="modal fade" id="RiskTypesModal" tabindex="-1" role="dialog" aria-labelledby="RiskTypesModalCenterTitle" aria-hidden="true">
			<div class="modal-dialog modal-dialog-centered modal-dialog-jump bp-102"  >
		
				<div class="modal-content" >
					   
				    <div class="modal-header" >
				      
				    	<h4 class="modal-title bp-3"  >Risk Types</h4>
	
				        <button type="button" class="close" data-dismiss="modal" aria-label="Close" >
				          <span aria-hidden="true">&times;</span>
				        </button>
				        				        
				    </div>
					<div class="modal-body p-1"  >
							
							<div class="card-body bp-103" >
			
								<div class="row" align="center">
									<div class="table-responsive"> 
										<table class="table table-bordered table-hover table-striped table-condensed width-70" >
											<thead>
												<tr>
													<th class="width-10" >SN</th>
													<th class="width-20" >Risk Type</th>
													<th class="width-70" >Description</th>
												</tr>
											</thead>
											<tbody>
												<% int riskcount=0;
												for(Object[] risktype : RiskTypes ){ %>
												<tr>
													<td class="text-center"><%=++riskcount %></td>
													<td class="text-center"><b>I<%=risktype[2]!=null?(risktype[2].toString()):"" %></b></td>
													<td>Internal <%=risktype[1]!=null?(risktype[1].toString()):"" %></td>
												</tr>
												<%} %>
												<%for(Object[] risktype : RiskTypes ){ %>
												<tr>
													<td class="text-center"><%=++riskcount %></td>
													<td class="text-center"><b>E<%=risktype[2]!=null?(risktype[2].toString()):"" %></b></td>
													<td>Internal <%=risktype[1]!=null?(risktype[1].toString()):"" %></td>
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
		
<!-- --------------------------------------------  Risk Types Modal   -------------------------------------------------------- -->
	<div class="modal fade " id="meetingModal" tabindex="-1" aria-labelledby="exampleModalLabel1" aria-hidden="true">
		<div class="modal-dialog modal-xl modal-dialog-jump">
			<div class="modal-content">
				<div class="modal-body">
					<div class="container-fluid">
						<div class="row">
							<div class="col-md-12">
								<div class="card shadow-nohover" >
									<div class="row card-header">
										<div class="col-md-10">
											<h5>
												<%
												if (ProjectId != null) {
													Object[] ProjectDetail123 = (Object[]) request.getAttribute("ProjectDetailsMil");
												%>
												<%=ProjectDetail123[2]%>
												(
												<%=ProjectDetail123[1]%>
												)
												<%
												}
												%>
											</h5>
										</div>
									</div>
									<div class="card-body">
						<%if(otherMeetingList!=null && otherMeetingList.size()>0) {%>
						<div align="left"><b><%="Other Meetings" %></b></div>
						<div align="left"><table class="subtables bp-52" >
						<thead><tr> <th class="width220">Committee</th> <th  class="width220"> Date Held</th></tr></thead>
						<%for(Object[]obj:otherMeetingList) {%>
						<tbody><tr><td><a class="btn btn-link p-0 m-0"  href="CommitteeMinutesViewAllDownload.htm?committeescheduleid=<%=obj[0]%>" target="blank"><%=obj[3]%></a>
								</td>												
								<td  class="text-center" ><%= sdf.format(sdf1.parse(obj[1].toString()))%></td>
								</tr>
									</tbody><%}%></table></div> <%} %>
									</div></div></div></div></div></div></div></div></div>

<!-- <div class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true" id="longdivmodal"  data-backdrop="static">
  <div class="modal-dialog modal-lg">
    
    <div class="modal-content mt-2 mb-2 style9" id="modalcontent" style="width:180%;margin-left:-35%">
     
    </div>
  </div>
</div> -->
<script type="text/javascript">

$('.content_reg_screen').hide();
$('.content_full_screen, .content_reg_screen').on('click', function(e){
	  
	  if (document.fullscreenElement) {
	    	document.exitFullscreen();
	  } else {
		  $('.slides-container').get(0).requestFullscreen();
	  }
	});

$('.content_full_screen').on('click', function(e){ contentFullScreen() });

$('.content_reg_screen').on('click', function(e){ contentRegScreen() });

function contentFullScreen()
{
	$('.content_full_screen').hide();
	$('.content_reg_screen').show();
	openFullscreen();
}

function contentRegScreen()
{
	$('.content_reg_screen').hide();
	$('.content_full_screen').show();
	closeFullscreen();
}
/* Get the documentElement (<html>) to display the page in fullscreen */
var elem = document.documentElement;

/* View in fullscreen */
function openFullscreen() {
  if (elem.requestFullscreen) {
    elem.requestFullscreen();
  } else if (elem.webkitRequestFullscreen) { /* Safari */
    elem.webkitRequestFullscreen();
  } else if (elem.msRequestFullscreen) { /* IE11 */
    elem.msRequestFullscreen();
  }
}

/* Close fullscreen */
function closeFullscreen() {
  if (document.exitFullscreen) {
    document.exitFullscreen();
  } else if (document.webkitExitFullscreen) { /* Safari */
    document.webkitExitFullscreen();
  } else if (document.msExitFullscreen) { /* IE11 */
    document.msExitFullscreen();
  }
}
</script>
<script type="text/javascript">
$('.carousel').carousel({
	  interval: false,
	  keyboard: true,
	})

$(function () {
  $('[data-toggle="tooltip"]').tooltip()
})

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
   $('#row'+milId.substring(14)).click(); 
 	$('#'+milId).show();
});
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
							
				var s = "<table id='milestonechangetableajax6' class='bp-104'><tr><th  class='width30'>SN</th><th  class='bp-105'>MS No.</th><th  class='bp-106'>Milestones </th><th  class='bp-91'> Original PDC </th><th  class='bp-91'> Revised PDC</th>"
							+ "<th  class='bp-107'>Progress</th><th  class='bp-108'> Status</th><th  class='bp-108'> Remarks</th></tr>";
				
							if(values[0].length==0){
								
								s+= "<tr><td colspan=8' class='text center' > Nil</td></tr>";
								
							}else{
								
								for(var i=0;i<values[0].length;i++){
									 if(parseInt(values[0][i][12])>0){ 
										s+= "<tr><td  class='bp-107'>" +parseInt(i+1)+ "</td><td  class='bp-105'>M"+values[0][i][2]+"</td><td  class='bp-106'>"+values[0][i][3]+"</td><td  class='bp-107' >"+formatDate(values[0][i][5])+" </td><td  class='bp-108'>"+formatDate(values[0][i][7])+"</td>"
										+"<td  class='bp-107'>"+values[0][i][12]+"</td><td  class='bp-108'>"+values[0][i][11]+"	</td><td  class='bp-108'>"+values[0][i][13]+"</td></tr>";
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
	<script type="text/javascript">
function setattchidvalue(attachid, attchName)
{
	var $projectid=$('#AttachProjectId').val();
	$('#attachid_'+$projectid).val(attachid);
	$('#attachname_'+$projectid).html(attchName);
	$('#exampleModalCenter1').modal('hide');
	$('#attachmentmodal').modal('hide');
}
</script>

<script type="text/javascript">
function ActionDetails(InAssignId)
{
		$("#modal_progress_table").DataTable().destroy();
		
		$.ajax({		
			type : "GET",
			url : "ActionAssignDataAjax.htm",
			data : {
				ActionAssignid : InAssignId
			},
			datatype : 'json',
			success : function(result) {
				var result = JSON.parse(result);
				
				$('#modal_action_item').html(result[1]);
				$('#modal_action_no').html(result[2]);
				$('#modal_action_date').html(moment(new Date(result[5]) ).format('DD-MM-YYYY'));
				$('#modal_action_PDC').html(moment(new Date(result[6]) ).format('DD-MM-YYYY'));
				$('#modal_action_assignor').html(result[8]);
				$('#modal_action_assignee').html(result[9]);
				
				var InActionType = result[10];
				var ActionType = 'Action';
				
				if(InActionType==='A')
				{
					ActionType = 'Action';
				}
				else if(InActionType==='I')
				{
					ActionType = 'Issue';
				}
				else if(InActionType==='D')
				{
					ActionType = 'Decision';
				}
				else if(InActionType==='R')
				{
					ActionType = 'Recommendation';
				}
				else if(InActionType==='C')
				{
					ActionType = 'Comment';
				}
				else if(InActionType==='K')
				{
					ActionType = 'Risk';
				}
				
				$('#modal_action_type').html(ActionType);
				
				var InProgress = '0'
				if(result[4]!=null){
					InProgress=result[4]+'';
				}
				
				if(InProgress.trim() === '0')
				{
					var progressBar ='<div class="progress bp-94" >'; 
					progressBar += 		'<div class="progress-bar bp-95" role="progressbar"   >';
					progressBar +=		'Not Started'
					progressBar +=		'</div>'; 
					progressBar += '</div>'; 
				}
				else
				{
					var progressBar ='<div class="progress bp-94" >'; 
					progressBar += 		'<div class="progress-bar progress-bar-striped width-'+InProgress+'" role="progressbar"  aria-valuemin="0" aria-valuemax="100" >';
					progressBar +=		InProgress
					progressBar +=		'</div>'; 
					progressBar += '</div>'; 
				}
				$('#modal_action_progress').html(progressBar);
			}
		});
		$.ajax({		
			type : "GET",
			url : "ActionSubListAjax.htm",
			data : {
				ActionAssignid : InAssignId
			},
			datatype : 'json',
			success :  function(result) {
				var result = JSON.parse(result);
				
				
				var htmlStr='';
				if(result.length> 0){
					for(var v=0;v<result.length;v++)
						{
						htmlStr += '<tr>';
						htmlStr += '<td class="tabledata text-center"  >'+ (v+1) + '</td>';
						htmlStr += '<td class="tabledata text-center"  >'+ moment(new Date(result[v][3]) ).format('DD-MM-YYYY') + '</td>';
						htmlStr += '<td class="tabledata text-center"  >'+ result[v][2] + ' %</td>';
						htmlStr += '<td class="tabledata" >'+ result[v][4] + '</td>';
						if(result[v][5]=== null)
						{
						htmlStr += '<td class="tabledata text-center" >-</td>';
						}
						else
						{
						htmlStr += '<td class="tabledata text-center" ><button type="submit" class="btn btn-sm" name="ActionSubId" value="'+ result[v][5] + '" target="blank" formaction="ActionDataAttachDownload.htm" ><i class="fa fa-download"></i></button></td>';
						}
						htmlStr += '</tr>';
					}
				}
				else
				{
					htmlStr += '<tr>';
					htmlStr += '<td colspan="5" class="text-center"> Progress Not Updated </td>';
					htmlStr += '</tr>';
				}
				setModalDataTable();
				$('#modal_progress_table_body').html(htmlStr);
				
				
				$('#action_modal').modal('toggle');
			}
		});
	}
	setModalDataTable();
	function setModalDataTable()
	{
		$("#modal_progress_table").DataTable({
			"lengthMenu": [ 5, 10,25, 50, 75, 100 ],
			"pagingType": "simple",
			"pageLength": 5
		});
	}
	
	function ListOfMilestones(){
		var totalMilestones=[];
		var totalCompletedMilestones=[];
		var totalProgressedMilestones=[];
		}
	function showMeetingModal(){
		$('#meetingModal').modal('show');
	}
	function showModal(a,b){
		   
		//$('#longdivmodal').modal('show');
		$.ajax({
		    type: 'GET',
		    url: 'ProjectDataAjax.htm',
		    xhrFields: {
		        responseType: 'blob' // Crucial: tells jQuery to handle binary data
		    },
		    data: { filename: a, projectdataid: b },
		    success: function(blob, status, xhr) {
		        // Get the extension we sent from the header
		        var extension = xhr.getResponseHeader('File-Extension').toLowerCase();
		        
		        // Create a temporary URL for the browser to use
		        var fileUrl = URL.createObjectURL(blob);
		        
		        var htmlContent = '<button type="button" class="close style10" data-dismiss="modal">&times;</button>';
		        
		        if (extension === "pdf") {
		            htmlContent += '<iframe width="100%" height="600" src="' + fileUrl + '"></iframe>';
		        } else {
		            htmlContent += '<img class="style11" src="' + fileUrl + '">';
		        }

		        $('#modalcontent').html(htmlContent);
		        $('#'+a+'Modal').html(htmlContent);
		        //$('#longdivmodal').modal('show');
		        
		        // Clean up memory after the modal is closed
		        $('#longdivmodal').on('hidden.bs.modal', function () {
		            URL.revokeObjectURL(fileUrl);
		        });
		    }
		});
	}
	
	   
    $( document ).ready(function(){
  	  
		$('#pearlbtn').click();
		$('#protreebtn').click();
		$('#sysspecsbtn').click();
		$('#sysconfigbtn').click();
    })
    
	
</script>
</body>
</html>