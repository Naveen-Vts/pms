<%@page import="java.util.Locale"%>
<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.fasterxml.jackson.databind.ObjectMapper"%>
<%@page import="java.util.Set"%>
<%@page import="com.vts.pfms.ccm.model.CCMAchievements"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="com.vts.pfms.committee.model.CommitteeSchedule"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="java.time.temporal.ChronoUnit"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Map"%>
<%@page import="com.vts.pfms.committee.model.Committee"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>

<spring:url value="/resources/js/excel.js" var="excel" />
<script src="${excel}"></script>

<spring:url value="/resources/ckeditor/ckeditor.js" var="ckeditor" />
<spring:url value="/resources/ckeditor/contents.css" var="contentCss" />
<script src="${ckeditor}"></script>

<spring:url value="/resources/css/slides-style.css" var="SlidesStyleCSS" />
<link href="${SlidesStyleCSS}" rel="stylesheet" />


<style type="text/css">
label {
	font-weight: 800;
	font-size: 16px;
	color: #07689f;
}

.tab-pane p{
	text-align: justify;
	
}

.tabpanes1 {
	min-height: 651px;
    max-height: 651px;
    overflow: auto;
    scrollbar-width: thin;
    scrollbar-height: thin;
  	scrollbar-color: #216583 #f8f9fa;
}

.tabpanes2 {
	min-height: 699px;
    max-height: 699px;
    overflow: auto;
    scrollbar-width: thin;
    scrollbar-height: thin;
  	scrollbar-color: #216583 #f8f9fa;
}

.card-body {
    padding-bottom: 50px; /* Add some padding to make sure content doesn't overlap with the buttons */
}

/* Chrome, Edge, and Safari */
.tabpanes1::-webkit-scrollbar, .tabpanes2::-webkit-scrollbar {
  width: 12px;
}

.tabpanes1::-webkit-scrollbar-track, .tabpanes2::-webkit-scrollbar-track {
  background: #f8f9fa;
  border-radius: 5px;
}

.tabpanes1::-webkit-scrollbar-thum, .tabpanes2::-webkit-scrollbar-thum {
  background-color: #216583;
  border-radius: 5px;
  border: 2px solid #f8f9fa;
}


.card-body{
	padding: 0rem !important;
}

.navigation_btn{
	margin: 1%;
}

 .b{
	background-color: #ebecf1;	
}
.a{
	background-color: #d6e0f0;
}

.nav-links{
	text-align: left;
}

body { 
   font-family : "Lato", Arial, sans-serif !important;
   overflow-x: hidden;
}

input,select,table,div,label,span {
	font-family : "Lato", Arial, sans-serif !important;
}

.previous{
	color: white !important;
}

.previous, .next{
	font-family: 'Montserrat', sans-serif;
    font-weight: 800 !important;
}

.next {
  padding: 4px 16px;
  font-weight: 800;
  background-color: #394989;
  border-color: #394989;
}

.center {
	text-align: center !important;
}

.right {
	text-align: right !important;
}

.left {
	text-align: left !important;
}

.fw-bold {
	font-weight: bold;
}
.select2-selection__rendered{
	text-align: left !important;
}

.agendaItemBtn > p {
	margin-bottom : 0;
}

.panel-bottom {
    bottom: 10px;
    right: 10px;
    text-align: right;
}

.ccmSideBar {
	min-height: 670px;
    max-height: 670px;
    overflow-y: auto;
    overflow-x: hidden;
    scrollbar-width: thin;
  	scrollbar-color: #4B70F5 #f8f9fa;
}

.ccmSideBarButton {
	margin-right: 0.25rem;
	margin-left: 0.25rem;
	margin-top: 0.25rem;
	width: 97%;
	border-radius: 0.75rem;
	text-align: left;
}

.panel-buttons {
	margin: 1%;
}

.btn-print {
	background-color: purple;
	border: none;
	color: white;
	font-weight: bold;
	text-decoration: none;
}

/* Custom colspan */
.col-custom-1-5 {
    width: calc(12.5%); /* This gives 1.5 out of 12 columns */
    flex: 0 0 calc(12.5%);
    max-width: calc(12.5%);
}

@media (max-width: 768px) {
    .col-custom-1-5 {
        width: 100%;
        flex: 0 0 100%;
        max-width: 100%;
    }
}

.col-custom-10-5 {
    width: calc(86.5%); 
    flex: 0 0 calc(86.5%);
    max-width: calc(86.5%);
    margin-left: 0.4rem;
}

@media (max-width: 768px) {
    .col-custom-10-5 {
        width: 100%;
        flex: 0 0 100%;
        max-width: 100%;
    }
}


.subtables {
	width: 100%;
	border-collapse: collapse;
	border: 1px solid black;
	/* margin-top: 10px; */
}


.custom-input {
	border-left: 0;
    border-top: 0;
    border-right: 0;
    width: 70%;
}

.modal-dialog-jump {
  animation: jumpIn 1.5s ease;
}

@keyframes jumpIn {
  0% {
    transform: scale(0.1);
    opacity: 0;
  }
  70% {
    transform: scale(1);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

/* Modal specific styles */
.modal-fixed-bottom-right {
    position: fixed;
    right: 25px;
    bottom: 30px;
    width: 300px;
    z-index: 1050; /* Ensure it's above other elements */
}

.modal-content {
    background-color: #f8f9fa;
    padding: 20px;
    border-radius: 5px;
}

    
.open-modal-button {
   right: 10px;
   background-color: #4B70F5;
   color: #fff;
   padding: 5px;
   border: none;
   border-radius: 20px;
   font-weight:bold;
   cursor: pointer;
   z-index: 1001; /* Make sure the button is above the modal */
}

.data-table {
	overflow-y: auto; 
	overflow-x: auto; 
}

.data-table thead {
    position: sticky;
	top: 0; /* Keeps the header at the top */
	z-index: 10; /* Ensure the header stays on top of the body */
	background-color: white; /* For visibility */
}
</style>


</head>
<body>

	<%
		String labcode = (String)session.getAttribute("labcode");
		String clusterid = (String)session.getAttribute("clusterid");
		
		String committeeId = (String)request.getAttribute("committeeId");
		String committeeIdDMC = (String)request.getAttribute("committeeIdDMC");
		String tabName = (String)request.getAttribute("tabName");
		String filesize = (String) request.getAttribute("filesize");
		String ccmScheduleId = (String) request.getAttribute("ccmScheduleId");
		
    	List<Object[]> clusterLabList = (List<Object[]>) request.getAttribute("clusterLabList");
    	Object[] clusterLabDetails = clusterLabList!=null && clusterLabList.size()>0 ?clusterLabList.stream().filter(e -> e[3].toString().equalsIgnoreCase("Y")).collect(Collectors.toList()).get(0) :null;
		String clusterLab = clusterLabDetails!=null && clusterLabDetails[2].toString().equalsIgnoreCase(labcode)?"Y":"N";
		
		List<Object[]> clusterLabListFilter = new ArrayList<>();
		
		int tabCount = 0;
		
		FormatConverter fc = new FormatConverter();
	%>

	<% 
    String ses = (String) request.getParameter("result");
    String ses1 = (String) request.getParameter("resultfail");
    if (ses1 != null) { %>
    <div align="center">
        <div class="alert alert-danger" role="alert">
            <%=StringEscapeUtils.escapeHtml4(ses1) %>
        </div>
    </div>
<% }if (ses != null) { %>
    <div align="center">
        <div class="alert alert-success" role="alert">
            <%=StringEscapeUtils.escapeHtml4(ses) %>
        </div>
    </div>
<% } %>
	
	<div class="container-fluid mb-3">
		<div class="card shadow-nohover">
 			<div class="card-header" style="background-color: transparent;height: 3rem;">
 				<div class="row">
 					<div class="col-md-3">
 						<h3 class="text-dark" style="font-weight: bold;">CCM Presentation
 							<a class="btn btn-info btn-sm shadow-nohover back mb-2" href="CCMModules.htm">
	 							<i class="fa fa-home" aria-hidden="true" style="font-size: 1rem;"></i> 
	 							CCM
 							</a> 
 						</h3>
 					</div>
 					<div class="col-md-7"></div>
 					<div class="col-md-2 right">
	 					<form action="#" method="post">
							<input type="hidden" name="ccmScheduleId" value="<%=ccmScheduleId %>">
							<input type="hidden" name="committeeId" value="<%=committeeId %>">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
				        									
    						<button type="submit" class="btn btn-sm btn-outline-success" formaction="CCMAgendaPresentation.htm" formmethod="post" formtarget="_blank" title="Agenda Presentation">
								<img alt="" src="view/images/presentation.png" style="width:19px !important">
							</button>
						</form>
 					</div>
 					
 				</div>
       		</div>
       		
       		<div class="card-body">
       			<div class="row ml-2">
       				<div class="col-custom-1-5 p-0 mt-2 mb-2">
       					<div class="card" style="border-color: #007bff;">
     						<div class="card-header center" style="background-color: transparent;border-color: #007bff;">
     							<h5 class="" style="font-weight: bold;color: #8b550c;">List of Topics</h5>
     						</div>
   							<div class="card-body ccmSideBar">
								<div class="row">
  									<div class="col-md-12">
  										<form action="CCMPresentation.htm" method="GET">
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
											<input type="hidden" name="committeeId" value="<%=committeeId%>">
     										<button type="submit" class="btn btn-outline-primary fw-bold ccmSideBarButton" name="tabName" value="ATR" data-toggle="tooltip" data-placement="top" title="Action Taken Report">
     											<i class="fa fa-caret-right" aria-hidden="true"></i>&nbsp; <%=++tabCount %>. ATR
     										</button>
     										<button type="submit" class="btn btn-outline-primary fw-bold ccmSideBarButton" name="tabName" value="DMC" data-toggle="tooltip" data-placement="top" title="DMC">
     											<i class="fa fa-caret-right" aria-hidden="true"></i>&nbsp; <%=++tabCount %>. DMC
     										</button>
     										<button type="submit" class="btn btn-outline-primary fw-bold ccmSideBarButton" name="tabName" value="EB Calendar" data-toggle="tooltip" data-placement="top" title="EB Calendar">
     											<i class="fa fa-caret-right" aria-hidden="true"></i>&nbsp; <%=++tabCount %>. EB Calendar 
     										</button>
     										<button type="submit" class="btn btn-outline-primary fw-bold ccmSideBarButton" name="tabName" value="PMRC Calendar" data-toggle="tooltip" data-placement="top" title="PMRC Calendar">
     											<i class="fa fa-caret-right" aria-hidden="true"></i>&nbsp; <%=++tabCount %>. PMRC Calendar 
     										</button>
     										<button type="submit" class="btn btn-outline-primary fw-bold ccmSideBarButton" name="tabName" value="ASP Status" data-toggle="tooltip" data-placement="top" title="ASP Status">
     											<i class="fa fa-caret-right" aria-hidden="true"></i>&nbsp; <%=++tabCount %>. ASP Status 
     										</button>
     										<button type="submit" class="btn btn-outline-primary fw-bold ccmSideBarButton" name="tabName" value="Closure Status" data-toggle="tooltip" data-placement="top" title="Closure Status">
     											<i class="fa fa-caret-right" aria-hidden="true"></i>&nbsp; <%=++tabCount %>. Closure Status
     										</button>
     										<button type="submit" class="btn btn-outline-primary fw-bold ccmSideBarButton" name="tabName" value="Cash Out Go Status" data-toggle="tooltip" data-placement="top" title="Cash Out Go Status">
     											<i class="fa fa-caret-right" aria-hidden="true"></i>&nbsp; <%=++tabCount %>. Cash Out Go Status
     										</button>
     										<button type="submit" class="btn btn-outline-primary fw-bold ccmSideBarButton" name="tabName" value="Test & Trials" data-toggle="tooltip" data-placement="top" title="Test & Trials">
     											<i class="fa fa-caret-right" aria-hidden="true"></i>&nbsp; <%=++tabCount %>. Test & Trials
     										</button>
     										<button type="submit" class="btn btn-outline-primary fw-bold ccmSideBarButton" name="tabName" value="Achievements" data-toggle="tooltip" data-placement="top" title="Achievements">
     											<i class="fa fa-caret-right" aria-hidden="true"></i>&nbsp; <%=++tabCount %>. Achievements
     										</button>
     										<button type="submit" class="btn btn-outline-primary fw-bold ccmSideBarButton" name="tabName" value="Others" data-toggle="tooltip" data-placement="top" title="Others">
     											<i class="fa fa-caret-right" aria-hidden="true"></i>&nbsp; <%=++tabCount %>. Others
     										</button>
	     								</form>		
  									</div>
   								</div>
   								
   							</div>
       					</div>
       				</div>
       				<div class="col-custom-10-5">
       					
						<!-- This is for Tab Panes -->
 						<div class="card mr-2 mb-2 mt-2" style="border-color: #007bff;">
 							<div class="tab-content text-center" style="">
 								<div class="tab-pane active" id="meetingschedule" role="tabpanel">
 								
 									<!-- ----------------------------------------------- ATR --------------------------------------------------- -->	
									<%if(tabName!=null && tabName.equalsIgnoreCase("ATR")) {
										
									    List<Object[]> ccmActions = (List<Object[]>) request.getAttribute("ccmActions");
									    
									    if(clusterLab.equalsIgnoreCase("N")){
									    	ccmActions = ccmActions.stream().filter(e -> e[19].toString().equalsIgnoreCase(labcode)).collect(Collectors.toList());
									    }
										
										List<String> latestScheduleMinutesIds = (List<String>) request.getAttribute("latestScheduleMinutesIds");
										List<String> currentScheduleMinutesIds = (List<String>) request.getAttribute("currentScheduleMinutesIds");
										
										Map<String, List<Object[]>> ccmActionsToListMap = ccmActions!=null && ccmActions.size()>0?ccmActions.stream()
																						  .filter(e -> latestScheduleMinutesIds.contains(e[18].toString()))
																						  .collect(Collectors.groupingBy(array -> array[0] + "", LinkedHashMap::new, Collectors.toList())) : new HashMap<>();
										
										Map<String, List<Object[]>> prevccmActionsToListMap = ccmActions!=null && ccmActions.size()>0?ccmActions.stream()
																							 .filter(e -> !e[9].toString().equalsIgnoreCase("C") && !latestScheduleMinutesIds.contains(e[18].toString()) && !currentScheduleMinutesIds.contains(e[18].toString()) )
																							 .collect(Collectors.groupingBy(array -> array[0] + "", LinkedHashMap::new, Collectors.toList())) : new HashMap<>();
										
										Committee committee = (Committee) request.getAttribute("committeeData");
										CommitteeSchedule ccmSchedule = (CommitteeSchedule) request.getAttribute("ccmSchedule");
										String seqDate = ccmSchedule!=null?ccmSchedule.getScheduleDate().toString().substring(2, 7):"";
										
										Map<Integer,String> mapCCM = (Map<Integer,String>)request.getAttribute("mapCCM");
									
										DecimalFormat df = new DecimalFormat("####################.##");
										SimpleDateFormat sdf = fc.getRegularDateFormat();
										SimpleDateFormat sdf1 = fc.getSqlDateFormat();
									    SimpleDateFormat inputFormat = new SimpleDateFormat("ddMMMyyyy",Locale.ENGLISH);
									    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");	
										String todayDate = outputFormat.format(new Date()).toString();		
									%>
 										<div class="container-fluid mt-3 tabpanes1">
 											<%-- <div class="card">
 												<div class="card-header" style="background-color: #007bff;color: white;">
 													<h4 class="atrHeading">
 														Action Taken Report of CCM(<%=seqDate %>)
 													</h4>
 													<h4 class="prevatrHeading" style="display: none;">
 														Pending Points from Prev CCM
 													</h4>
 												</div>
 											</div> --%>
	 										<table class="table table-bordered table-hover table-striped table-condensed data-table" id="atrTable">
												<thead style="background-color: #4B70F5; color: #ffff !important;">
													<tr style="background-color: #4C3BCF;border-radius: 1rem;">
														<th colspan="6" style="border-radius: 1rem;"> <h5>Action Taken Report of CCM(<%=seqDate!=null?StringEscapeUtils.escapeHtml4(seqDate):" - " %>)</h5></th>
													</tr>
													<tr>
														<th style="width: 5%;">SN</th>
														<th style="width: 10%;">ID</th>
														<th style="width: 30%;">Action Point</th>
														<th style="width: 20%;">Action by</th>
														<th style="width: 10%;">PDC</th>
														<th style="width: 25%;">Status</th>
													
													</tr>
												</thead>
	
												<tbody>
													<% if (ccmActionsToListMap!=null && ccmActionsToListMap.size() > 0) {
															int slno = 0;String key="";
															for (Map.Entry<String, List<Object[]>> map : ccmActionsToListMap.entrySet()) {
					                   							
					                   							List<Object[]> values = map.getValue();
					                   							int i=0;
					                   							for (Object[] obj : values) {
													%>
														<tr>
															<td style="text-align: center;"><%=++slno%></td>
															<td style="text-align: center;">
															
																<%if(obj[17]!=null && Long.parseLong(obj[17].toString())>0){ %>
																	<button type="button" class="btn btn-sm btn-primary" style="border-radius: 50px;font-weight: bold;background-color: #402E7A;" onclick="ActionDetails( <%=obj[17] %>);" data-toggle="tooltip" data-placement="top" title="Action Details" >
																		<%if(committee.getCommitteeShortName().trim().equalsIgnoreCase("CCM")){ %>
																		<%for (Map.Entry<Integer, String> entry : mapCCM.entrySet()) {
																			Date date = inputFormat.parse(obj[1].toString().split("/")[2]);
																			 String formattedDate = outputFormat.format(date);
																			 if(entry.getValue().equalsIgnoreCase(formattedDate)){
																				 key=entry.getKey().toString();
																			 } }}%>
																		
																		<%=committee.getCommitteeShortName()!=null?StringEscapeUtils.escapeHtml4(committee.getCommitteeShortName()).trim().toUpperCase():" - "%> - <%=key%> -(<%=seqDate!=null?StringEscapeUtils.escapeHtml4(seqDate):" - " %>)/<%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()).split("/")[3]:" - " %>
																	</button>
																<%}%> 
															<!--  -->
															</td>
															<%if(i==0) {%>
													    		<td rowspan="<%=values.size() %>" style="text-align: justify;vertical-align: middle;"><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></td>
			           										<%} %>
															<td style="text-align: justify;"><%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - "%>, <%=obj[12]!=null?StringEscapeUtils.escapeHtml4(obj[12].toString()): " - "%></td>
															<td style="text-align: center;">
																<%	String actionstatus = obj[9].toString();
																	int progress = obj[15]!=null ? Integer.parseInt(obj[15].toString()) : 0;
																	LocalDate pdcorg = LocalDate.parse(obj[3].toString());
																	LocalDate lastdate = obj[13]!=null ? LocalDate.parse(obj[13].toString()): null;
																	LocalDate today = LocalDate.now();
																	LocalDate endPdc = LocalDate.parse(obj[4].toString());
																%> 
												 				<%-- <% if(lastdate!=null && actionstatus.equalsIgnoreCase("C") ){%>
																		<%if(actionstatus.equals("C") && (pdcorg.isAfter(lastdate) || pdcorg.equals(lastdate))){%>
																		<span class="completed"><%= sdf.format(sdf1.parse(obj[13].toString()))%> </span>
																		<%}else if(actionstatus.equals("C") && pdcorg.isBefore(lastdate)){ %>	
																		<span class="completeddelay"><%= sdf.format(sdf1.parse(obj[13].toString()))%> </span>
																		<%} %>	
																	<%}else{ %>
																			-									
																	<%} %>
																<br> --%>
																<span <%if(endPdc.isAfter(today) || endPdc.isEqual(today)) {%>style="color:black;font-weight: bold;" <%} else{%> style="color:maroon ;font-weight:bolder;" <%} %>>
																<%=obj[4]!=null?sdf.format(sdf1.parse(obj[4].toString())):" - "%> 
																</span> 
																<%if(!pdcorg.equals(endPdc)) { %>
																<br> 
																<span <%if(pdcorg.isAfter(today) || pdcorg.isEqual(today)) {%>style="color:black;font-weight: bold;" <%} else{%> style="color:maroon ;font-weight:bolder;" <%} %>>
																<%= obj[3]!=null?sdf.format(sdf1.parse(obj[3].toString())):" - "%> 
																</span>	
																<%} %>
															</td>
															<!-- <td style="text-align: center;">
																<input type="text" class="custom-input" name="actionStatus" placeholder="Enter Status">
																&nbsp;&nbsp;
																<button type="submit" class="btn btn-sm edit" name="action" value="A" data-toggle="tooltip" data-placement="top" title="Update" onclick="return confirm('Are you sure to update?')" style="border-radius: 0.75rem;">
																	Update
																</button>
															</td> -->
															<td style="text-align: justify;">
																<% if (obj[16] != null) { %><%=StringEscapeUtils.escapeHtml4(obj[16].toString())%> <% } else{%>-<%} %>
															</td>
										
														</tr>
													<% ++i;}} } else{%>
														<tr>
															<td colspan="6" style="text-align: center;">No Data Available</td>
														</tr>
													<%} %>
												</tbody>
											</table>
											
	 										<table class="table table-bordered table-hover table-striped table-condensed data-table" id="prevatrTable" >
												<thead style="background-color: #4B70F5; color: #ffff !important;">
													<tr style="background-color: #4C3BCF;border-radius: 1rem;">
														<th colspan="6" style="border-radius: 1rem;"> <h5>Pending Points from Prev CCM</h5></th>
													</tr>
													<tr>
														<th style="width: 5%;">SN</th>
														<th style="width: 10%;">ID</th>
														<th style="width: 30%;">Action Point</th>
														<th style="width: 20%;">Action by</th>
														<th style="width: 10%;">PDC</th>
														<th style="width: 25%;">Status</th>
													</tr>
												</thead>
	
												<tbody>
													<% if (prevccmActionsToListMap!=null && prevccmActionsToListMap.size() > 0) {
															int slno = 0;String key="";
															for (Map.Entry<String, List<Object[]>> map : prevccmActionsToListMap.entrySet()) {
					                   							
					                   							List<Object[]> values = map.getValue();
					                   							int i=0;
					                   							for (Object[] obj : values) {
													%>
														<tr>
															<td style="text-align: center;"><%=++slno%></td>
															<td style="text-align: center;">
															
																<%if(obj[17]!=null && Long.parseLong(obj[17].toString())>0){ %>
																	<button type="button" class="btn btn-sm btn-primary" style="border-radius: 50px;font-weight: bold;background-color: #402E7A;" onclick="ActionDetails( <%=obj[17] %>);" data-toggle="tooltip" data-placement="top" title="Action Details" >
																		<%if(committee.getCommitteeShortName().trim().equalsIgnoreCase("CCM")){ %>
																		<%for (Map.Entry<Integer, String> entry : mapCCM.entrySet()) {
																			Date date = inputFormat.parse(obj[1].toString().split("/")[2]);
																			 String formattedDate = outputFormat.format(date);
																			 if(entry.getValue().equalsIgnoreCase(formattedDate)){
																				 key=entry.getKey().toString();
																			 } }}%>
																		
																		<%=committee.getCommitteeShortName()!=null?StringEscapeUtils.escapeHtml4(committee.getCommitteeShortName()).trim().toUpperCase():" - "%>-<%=key%>-(<%=(obj[20]!=null?StringEscapeUtils.escapeHtml4(obj[20].toString()).substring(2, 7):" - ")%>)/ <%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()).split("/")[3]:" - " %>
																	</button>
																<%}%> 
															<!--  -->
															</td>
															<%if(i==0) {%>
													    		<td rowspan="<%=values.size() %>" style="text-align: justify;vertical-align: middle;"><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></td>
			           										<%} %>
															<td style="text-align: justify;"><%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - "%>, <%=obj[12]!=null?StringEscapeUtils.escapeHtml4(obj[12].toString()): " - "%></td>
															<td style="text-align: center;">
																<%	String actionstatus = obj[9].toString();
																	int progress = obj[15]!=null ? Integer.parseInt(obj[15].toString()) : 0;
																	LocalDate pdcorg = LocalDate.parse(obj[3].toString());
																	LocalDate lastdate = obj[13]!=null ? LocalDate.parse(obj[13].toString()): null;
																	LocalDate today = LocalDate.now();
																	LocalDate endPdc = LocalDate.parse(obj[4].toString());
																%> 
												 				<%-- <% if(lastdate!=null && actionstatus.equalsIgnoreCase("C") ){%>
																		<%if(actionstatus.equals("C") && (pdcorg.isAfter(lastdate) || pdcorg.equals(lastdate))){%>
																		<span class="completed"><%= sdf.format(sdf1.parse(obj[13].toString()))%> </span>
																		<%}else if(actionstatus.equals("C") && pdcorg.isBefore(lastdate)){ %>	
																		<span class="completeddelay"><%= sdf.format(sdf1.parse(obj[13].toString()))%> </span>
																		<%} %>	
																	<%}else{ %>
																			-									
																	<%} %>
																<br> --%>
																<span <%if(endPdc.isAfter(today) || endPdc.isEqual(today)) {%>style="color:black;font-weight: bold;" <%} else{%> style="color:maroon ;font-weight:bolder;" <%} %>>
																<%= obj[4]!=null?sdf.format(sdf1.parse(obj[4].toString())):" - "%> 
																</span>  
																<%if(!pdcorg.equals(endPdc)) { %>
																<br> 
																<span <%if(pdcorg.isAfter(today) || pdcorg.isEqual(today)) {%>style="color:black;font-weight: bold;" <%} else{%> style="color:maroon ;font-weight:bolder;" <%} %>>
																<%= obj[3]!=null?sdf.format(sdf1.parse(obj[3].toString())):" - "%> 
																</span>	
																<%} %>
															</td>
															<!-- <td style="text-align: center;">
																<input type="text" class="custom-input" name="actionStatus" placeholder="Enter Status">
																&nbsp;&nbsp;
																<button type="submit" class="btn btn-sm edit" name="action" value="A" data-toggle="tooltip" data-placement="top" title="Update" onclick="return confirm('Are you sure to update?')" style="border-radius: 0.75rem;">
																	Update
																</button>
															</td> -->
															<td style="text-align: justify;">
																<% if (obj[16] != null) { %><%=StringEscapeUtils.escapeHtml4(obj[16].toString())%> <% } else{%>-<%} %>
															</td>
										
														</tr>
													<% i++; }} } else {%>
														<tr>
															<td colspan="6" style="text-align: center;">No Data Available</td>
														</tr>
													<%} %>
												</tbody>
											</table>
											
										</div>	
										<div class="center mt-2 mb-2">
											<button type="button" class="btn btn-sm btn-outline-secondary fw-bold atr-btn" style="border-radius: 0.75rem;">
												ATR of <%=seqDate!=null?StringEscapeUtils.escapeHtml4(seqDate):" - "  %> 
											</button>
											<button type="button" class="btn btn-sm btn-outline-info fw-bold pending-point-btn" style="border-radius: 0.75rem;">
												Pending Points 
											</button>
										</div>
									<!-- ----------------------------------------------- ATR End --------------------------------------------------- -->	
									
									<!-- ----------------------------------------------- DMC --------------------------------------------------- -->	
									<%} else if(tabName!=null && tabName.equalsIgnoreCase("DMC")) {
										
										String committeeMainId = (String)request.getAttribute("committeeMainId");
										List<Object[]> dmcActions = (List<Object[]>) request.getAttribute("dmcActions");
										
										if(clusterLab.equalsIgnoreCase("N")){
											dmcActions = dmcActions.stream().filter(e -> e[19].toString().equalsIgnoreCase(labcode)).collect(Collectors.toList());
									    }
										
										//List<String> latestScheduleMinutesIds = (List<String>) request.getAttribute("latestScheduleMinutesIds");
										
										//Map<String, List<Object[]>> dmcActionsToListMap = dmcActions!=null && dmcActions.size()>0? dmcActions.stream()
										//												.filter(e -> latestScheduleMinutesIds.contains(e[18].toString()))
										//												.collect(Collectors.groupingBy(array -> array[0] + "", LinkedHashMap::new, Collectors.toList())) : new HashMap<>();
										//
										Map<String, List<Object[]>> prevdmcActionsToListMap = dmcActions!=null && dmcActions.size()>0?dmcActions.stream()
																							  .filter(e -> !e[9].toString().equalsIgnoreCase("C"))
																							  .collect(Collectors.groupingBy(array -> array[0] + "", LinkedHashMap::new, Collectors.toList())) : new HashMap<>();
										Committee committee = (Committee) request.getAttribute("committeeData");
										CommitteeSchedule dmcSchedule = (CommitteeSchedule) request.getAttribute("dmcSchedule");
										String seqDate = dmcSchedule!=null?dmcSchedule.getScheduleDate().toString().substring(2, 7):"";
										
										Map<Integer,String> mapDMC = (Map<Integer,String>)request.getAttribute("mapDMC");
									
										DecimalFormat df = new DecimalFormat("####################.##");
										SimpleDateFormat sdf = fc.getRegularDateFormat();
										SimpleDateFormat sdf1 = fc.getSqlDateFormat();
									    SimpleDateFormat inputFormat = new SimpleDateFormat("ddMMMyyyy",Locale.ENGLISH);
									    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");	
										String todayDate = outputFormat.format(new Date()).toString();	
									%>
										<div class="container-fluid mt-3 tabpanes1">
											<table class="table table-bordered table-hover table-striped table-condensed data-table" >
												<thead style="background-color: #4B70F5; color: #ffff !important;">
													<tr style="background-color: #4C3BCF;border-radius: 1rem;">
														<th colspan="6" style="border-radius: 1rem;"> <h5>DMC Actions</h5></th>
													</tr>
													<tr>
														<th style="width: 5%;">SN</th>
														<th style="width: 10%;">ID</th>
														<th style="width: 30%;">Action Point</th>
														<th style="width: 20%;">Action by</th>
														<th style="width: 10%;">PDC</th>
														<th style="width: 25%;">Status</th>
													</tr>
												</thead>
												<tbody>
													<% if (prevdmcActionsToListMap!=null && prevdmcActionsToListMap.size() > 0) {
															int slno = 0;String key="";
															for (Map.Entry<String, List<Object[]>> map : prevdmcActionsToListMap.entrySet()) {
					                   							
					                   							List<Object[]> values = map.getValue();
					                   							int i=0;
					                   							for (Object[] obj : values) {
													%>
														<tr>
															<td style="text-align: center;"><%=++slno%></td>
															<td style="text-align: center;">
															
																<%if(obj[17]!=null && Long.parseLong(obj[17].toString())>0){ %>
																	<button type="button" class="btn btn-sm btn-primary" style="border-radius: 50px;font-weight: bold;background-color: #402E7A;" onclick="ActionDetails( <%=obj[17] %>);" data-toggle="tooltip" data-placement="top" title="Action Details" >
																		<%if(committee.getCommitteeShortName().trim().equalsIgnoreCase("DMC")){ %>
																		<%for (Map.Entry<Integer, String> entry : mapDMC.entrySet()) {
																			Date date = inputFormat.parse(obj[1].toString().split("/")[2]);
																			 String formattedDate = outputFormat.format(date);
																			 if(entry.getValue().equalsIgnoreCase(formattedDate)){
																				 key=entry.getKey().toString();
																			 } }}%>
																		
																		<%=committee.getCommitteeShortName()!=null?StringEscapeUtils.escapeHtml4(committee.getCommitteeShortName()).trim().toUpperCase():" - "%>-<%=key%>-(<%=(obj[20]!=null?StringEscapeUtils.escapeHtml4(obj[20].toString()).substring(2, 7):" - ")%>)/<%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()).split("/")[3]:" - " %>
																	</button>
																<%}%> 
															<!--  -->
															</td>
															<%if(i==0) {%>
													    		<td rowspan="<%=values.size() %>" style="text-align: justify;vertical-align: middle;"><%=obj[2] %></td>
			           										<%} %>
															<td style="text-align: justify;"><%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - "%>, <%=obj[12]!=null?StringEscapeUtils.escapeHtml4(obj[12].toString()): " - "%></td>
															<td style="text-align: center;">
																<%	String actionstatus = obj[9].toString();
																	int progress = obj[15]!=null ? Integer.parseInt(obj[15].toString()) : 0;
																	LocalDate pdcorg = LocalDate.parse(obj[3].toString());
																	LocalDate lastdate = obj[13]!=null ? LocalDate.parse(obj[13].toString()): null;
																	LocalDate today = LocalDate.now();
																	LocalDate endPdc = LocalDate.parse(obj[4].toString());
																%> 
												 				<%-- <% if(lastdate!=null && actionstatus.equalsIgnoreCase("C") ){%>
																		<%if(actionstatus.equals("C") && (pdcorg.isAfter(lastdate) || pdcorg.equals(lastdate))){%>
																		<span class="completed"><%= sdf.format(sdf1.parse(obj[13].toString()))%> </span>
																		<%}else if(actionstatus.equals("C") && pdcorg.isBefore(lastdate)){ %>	
																		<span class="completeddelay"><%= sdf.format(sdf1.parse(obj[13].toString()))%> </span>
																		<%} %>	
																	<%}else{ %>
																			-									
																	<%} %>
																<br> --%>
																<span <%if(endPdc.isAfter(today) || endPdc.isEqual(today)) {%>style="color:black;font-weight: bold;" <%} else{%> style="color:maroon ;font-weight:bolder;" <%} %>>
																<%=obj[4]!=null? sdf.format(sdf1.parse(obj[4].toString())):" - "%> 
																</span>  
																<%if(!pdcorg.equals(endPdc)) { %>
																<br> 
																<span <%if(pdcorg.isAfter(today) || pdcorg.isEqual(today)) {%>style="color:black;font-weight: bold;" <%} else{%> style="color:maroon ;font-weight:bolder;" <%} %>>
																<%=obj[3]!=null? sdf.format(sdf1.parse(obj[3].toString())):" - "%> 
																</span>	
																<%} %>
															</td>
															<!-- <td style="text-align: center;">
																<input type="text" class="custom-input" name="actionStatus" placeholder="Enter Status">
																&nbsp;&nbsp;
																<button type="submit" class="btn btn-sm edit" name="action" value="A" data-toggle="tooltip" data-placement="top" title="Update" onclick="return confirm('Are you sure to update?')" style="border-radius: 0.75rem;">
																	Update
																</button>
															</td> -->
															<td style="text-align: justify;">
																<% if (obj[16] != null) { %><%=StringEscapeUtils.escapeHtml4(obj[16].toString())%> <% } else{%>-<%} %>
															</td>
										
														</tr>
													<% i++; }} } else {%>
														<tr>
															<td colspan="6" style="text-align: center;">No Data Available</td>
														</tr>
													<%} %>
												</tbody>
											</table>	
										</div>	
										<div class="center mt-2 mb-2">
											<form action="#" id="dmcScheduleForm">
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"  />
												<input type="hidden" name="committeeMainId" value="<%=committeeMainId %>">
												<input type="hidden" name="committeeIdDMC" value="<%=committeeIdDMC %>">
												<input type="hidden" name="committeeId" value="<%=committeeIdDMC %>">
												<div class="row">
													<div class="col-md-4">
													</div>
													<div class="col-md-4 center">
														<%if(dmcSchedule!=null) {%> 
															<button type="submit" class="btn btn-sm fw-bold fs-custom btn-outline-info btn-minutes" value="Minutes" formaction="CommitteeScheduleMinutes.htm" >
																Minutes
															</button>
															<input type="hidden" name="committeescheduleid" value="<%=dmcSchedule.getScheduleId()%>">
															<input type="hidden" name="dmcFlag" value="Y">
														<%} %> 
													</div>
													
													
													<div class="col-md-4 right">
														<button type="submit" class="btn btn-sm submit" name="action" value="Add" id="dmcScheduleBtn" formaction="DCMScheduleDetailsSubmit.htm" style="display: none;"></button>
														<button type="button" class="open-modal-button mr-2" id="modalBtn" data-toggle="tooltip" title="Schedule New DMC">
															<img class="fa faa-pulse animated faa-medium" alt="" src="view/images/requirements.png" style="width:21px" >
														</button>
													</div>
													
												</div>
												
											</form>
											
										</div>	
										<!------------------------ DMC Schedule Modal -------------------------------------------->
										<div id="fixedModal" class="modal-fixed-bottom-right" style="display: none;">
										    <div class="modal-content">
										        <div class="modal-header" style="">
										            <h5 class="modal-title" style="">Schedule New DMC</h5>
										            <button type="button" class="close" id="closeModalBtn" aria-label="Close">
										                <span aria-hidden="true">&times;</span>
										            </button>
										        </div>
										        <div class="modal-body">
										            <input type="text" form="dmcScheduleForm" class="form-control" name="meetingDate" id="meetingDate" style="" readonly>
										        </div>
										        <div class="center">
										        	<button type="button" form="dmcScheduleForm" class="btn btn-sm fw-bold submit" onclick="validateDMCSchedule('<%=committeeIdDMC%>','<%=(String)request.getAttribute("committeeMainId")%>')">
														Submit
													</button>
									        	</div>
										    </div>
										</div>
										<!------------------------ DMC Schedule Modal End -------------------------------------------->
									<!-- ----------------------------------------------- DMC End --------------------------------------------------- -->
									
									<!-- ----------------------------------------------- EB Calendar --------------------------------------------------- -->	
									<%} else if(tabName!=null && tabName.equalsIgnoreCase("EB Calendar")) { 
										List<Object[]> ebCalendarData = (List<Object[]>) request.getAttribute("ebCalendarData");
										String previousMonth = (String) request.getAttribute("previousMonth");
										String currentMonth = (String) request.getAttribute("currentMonth");
										int year = (int) request.getAttribute("year");
									%>
										<div class="container-fluid mt-3 tabpanes1">
											<table class="table table-bordered table-hover table-striped table-condensed " style="width: 100%;" >
												<thead style="background-color: #4B70F5; color: #ffff !important;border-radius: 1rem;">
													<tr style="background-color: #4C3BCF;border-radius: 1rem;">
														<th colspan="6" style="border-radius: 1rem;"> <h5>EB Calendar</h5></th>
													</tr>
													<tr>
														<th>Lab</th>
														<th>EB Proposed - <%=previousMonth+" "+year %></th>
														<th>EB Held - <%=previousMonth+" "+year %></th>
														<th>EB Proposed - <%=currentMonth+" "+year %></th>
													</tr>
												</thead>
												<tbody>
													<%if(ebCalendarData!=null && ebCalendarData.size()>0) {
														for(Object[] obj : ebCalendarData) {
													%>
														<tr>
															<td style="width: 10%;"><%=obj[0]!=null?StringEscapeUtils.escapeHtml4(obj[0].toString()): " - " %></td>
															<td style="width: 30%;">
																<%if(obj[1]!=null) {
																	String[] split = obj[1].toString().split(", ");
																	StringBuilder result = new StringBuilder();
																	for (int i = 0; i < split.length; i++) {
																        String[] parts = split[i].split("/");
																        if (parts.length == 2) {
																            result.append("<span style=\"color: ").append(parts[1]).append("\">").append(parts[0]).append("</span>");
																            if (i < split.length - 1) {
																                result.append(",&emsp;");
																            }
																        }
																    }
																	 out.print(result);
																} else {
																    out.print("-");
																} %>
															</td>
															<td style="width: 30%;">
																<%if(obj[2]!=null) {
																	String[] split = obj[2].toString().split(", ");
																	StringBuilder result = new StringBuilder();
																	for (int i = 0; i < split.length; i++) {
																        String[] parts = split[i].split("/");
																        if (parts.length == 2) {
																            result.append("<span style=\"color: ").append(parts[1]).append("\">").append(parts[0]).append("</span>");
																            if (i < split.length - 1) {
																                result.append(",&emsp;");
																            }
																        }
																    }
																	 out.print(result);
																} else {
																    out.print("-");
																} %>
															</td>
															<td style="width: 30%;">
																<%if(obj[3]!=null) {%><%=StringEscapeUtils.escapeHtml4(obj[3].toString())%><%} %>
																
																<% if(obj[1]!=null) {
																	String[] split = obj[1].toString().split(", ");
																	StringBuilder result = new StringBuilder();
																	for (int i = 0; i < split.length; i++) {
																        String[] parts = split[i].split("/");
																        if (parts.length == 2 && parts[1].equalsIgnoreCase("red")) {
																            result.append("<span style=\"color: blue;").append("\">").append(parts[0]).append("</span>");
																            if (i < split.length - 1) {
																                result.append(",&emsp;");
																            }
																        }
																    }
																	 out.print((obj[3]!=null?",&emsp;":"")+result);
																} %>
																<%if(obj[3]==null && obj[1]==null) {%>-<%} %>
															</td>
														</tr>
													<%} }else {%>
														<tr>
															<td colspan="4" class="center">No Data Available</td>
														</tr>
													<%} %>
												</tbody>
											</table>
										</div>
										<div class="center mt-3 mb-2">
											<span style="color: #f502f5;">Conducted as per Schedule</span> |
											<span style="color: red;">Planned but Not Conducted</span> |
											<span style="color: blue;">Carry over From Prev Month</span>
										</div>				
									<!-- ----------------------------------------------- EB Calendar End --------------------------------------------------- -->	
									
									<!-- ----------------------------------------------- PMRC Calendar --------------------------------------------------- -->	
									<%} else if(tabName!=null && tabName.equalsIgnoreCase("PMRC Calendar")) {
										List<Object[]> pmrcCalendarData = (List<Object[]>) request.getAttribute("pmrcCalendarData");
										String previousMonth = (String) request.getAttribute("previousMonth");
										String currentMonth = (String) request.getAttribute("currentMonth");
										int year = (int) request.getAttribute("year");

									%>
										<div class="container-fluid mt-3 tabpanes1">
											<table class="table table-bordered table-hover table-striped table-condensed " style="width: 100%;" >
												<thead style="background-color: #4B70F5; color: #ffff !important;border-radius: 1rem;">
													<tr style="background-color: #4C3BCF;border-radius: 1rem;">
														<th colspan="6" style="border-radius: 1rem;"> <h5>PMRC Calendar</h5></th>
													</tr>
													<tr>
														<th>Lab</th>
														<th>PMRC Proposed - <%=previousMonth+" "+year %></th>
														<th>PMRC Held - <%=previousMonth+" "+year %></th>
														<th>PMRC Proposed - <%=currentMonth+" "+year %></th>
													</tr>
												</thead>
												<tbody>
													<%if(pmrcCalendarData!=null && pmrcCalendarData.size()>0) {
														for(Object[] obj : pmrcCalendarData) {
													%>
														<tr>
															<td style="width: 10%;"><%=obj[0]!=null?StringEscapeUtils.escapeHtml4(obj[0].toString()): " - " %></td>
															<td style="width: 30%;">
																<%if(obj[1]!=null) {
																	String[] split = obj[1].toString().split(", ");
																	StringBuilder result = new StringBuilder();
																	for (int i = 0; i < split.length; i++) {
																        String[] parts = split[i].split("/");
																        if (parts.length == 2) {
																            result.append("<span style=\"color: ").append(parts[1]).append("\">").append(parts[0]).append("</span>");
																            if (i < split.length - 1) {
																                result.append(",&emsp;");
																            }
																        }
																    }
																	 out.print(result);
																} else {
																    out.print("-");
																} %>
															</td>
															<td style="width: 30%;">
																<%if(obj[2]!=null) {
																	String[] split = obj[2].toString().split(", ");
																	StringBuilder result = new StringBuilder();
																	for (int i = 0; i < split.length; i++) {
																        String[] parts = split[i].split("/");
																        if (parts.length == 2) {
																            result.append("<span style=\"color: ").append(parts[1]).append("\">").append(parts[0]).append("</span>");
																            if (i < split.length - 1) {
																                result.append(",&emsp;");
																            }
																        }
																    }
																	 out.print(result);
																} else {
																    out.print("-");
																} %>
															</td>
															<td style="width: 30%;">
																<%if(obj[3]!=null) {%><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - " %><%} %>
																
																<% if(obj[1]!=null) {
																	String[] split = obj[1].toString().split(", ");
																	StringBuilder result = new StringBuilder();
																	for (int i = 0; i < split.length; i++) {
																        String[] parts = split[i].split("/");
																        if (parts.length == 2 && parts[1].equalsIgnoreCase("red")) {
																            result.append("<span style=\"color: blue;").append("\">").append(parts[0]).append("</span>");
																            if (i < split.length - 1) {
																                result.append(",&emsp;");
																            }
																        }
																    }
																	 out.print((obj[3]!=null?",&emsp;":"")+result);
																} %>
																<%if(obj[3]==null && obj[1]==null) {%>-<%} %>
															</td>
														</tr>
													<%} }else {%>
														<tr>
															<td colspan="4" class="center">No Data Available</td>
														</tr>
													<%} %>
												</tbody>
											</table>
										</div>
										<div class="center mt-3 mb-2">
											<span style="color: #f502f5;">Conducted as per Schedule</span> |
											<span style="color: red;">Planned but Not Conducted</span> |
											<span style="color: blue;">Carry over From Prev Month</span>
										</div>				
									<!-- ----------------------------------------------- PMRC Calendar End --------------------------------------------------- -->	
									
									<!-- ----------------------------------------------- ASP Status --------------------------------------------------- -->	
									<%} else if(tabName!=null && tabName.equalsIgnoreCase("ASP Status")) { 
										String labCode = (String)request.getAttribute("labCode");
										List<Object[]> aspStatusList = (List<Object[]>)request.getAttribute("aspList");
									%>
										<div class="container-fluid mt-3 tabpanes1">
											<table class="table table-bordered table-hover table-striped table-condensed " style="width: 100%;" >
												<thead style="background-color: #4B70F5; color: #ffff !important;border-radius: 1rem;">
													<tr style="background-color: #4C3BCF;border-radius: 1rem;">
														<th colspan="9" style="border-radius: 1rem;"> <h5>ASP Status</h5></th>
													</tr>
													<%if(clusterLab.equalsIgnoreCase("Y")) {%>
														<tr style="background-color: #ffff;">
															<td colspan="9" >
																<div style="display: inline-flex; align-items: flex-end;float: right;">
																	<form action="CCMPresentation.htm" method="get">
																		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"  />
												     					<input type="hidden" name="committeeId" value="<%=committeeId%>">
												     					<input type="hidden" name="tabName" value="<%=tabName%>">
												     					<div style="display: inline-flex; align-items: flex-end;">
																			<label>Lab : </label>
																			&nbsp;
																			<select class="form-control selectdee" id="labCodeASP" name="labCode" onchange="this.form.submit()" required style="width: 200px;">
																				<option value="0" disabled="disabled">---Select---</option>
																				<%if(clusterLabList!=null && clusterLabList.size()>0) {
																					for(Object[] obj : clusterLabList) {
																				%>
																					<option value="<%=obj[2]%>" <%if(labCode.equalsIgnoreCase(obj[2].toString())) {%>selected<%} %> ><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></option>
																				<%} }%>
																			</select>
																		</div>
																	</form>
																</div>
															</td>
														</tr>
													<%} %>
													<tr>
														<th rowspan="2" style="vertical-align: middle;">SN</th>
														<th rowspan="2" style="vertical-align: middle;">Project</th>
														<th colspan="6">Milestone Dates for</th>
														<th rowspan="2" style="vertical-align: middle;">Status</th>
													</tr>
													<tr>
														
														<th>PDR/PRC</th>
														<th>TiEC</th>
														<th>CEC</th>
														<th>CCM</th>
														<th>DMC</th>
														<th>Sanction</th>
													</tr>
												</thead>
												<tbody>
													<%if(aspStatusList!=null && aspStatusList.size()>0) { 
														int slno = 0;
														for(Object[] obj : aspStatusList) {
													%>
														<tr>
															<td class="center"><%=++slno %></td>
															<td class="left">
																<%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()):"-" %> <br>
																Cat&emsp;: <%=obj[5]!=null?StringEscapeUtils.escapeHtml4(obj[5].toString()):"-" %> <br>
																Cost&nbsp;&nbsp; : <%=obj[6]!=null?String.format("%.2f", Double.parseDouble(StringEscapeUtils.escapeHtml4(obj[6].toString()))/10000000):"-" %> (In Cr) <br>
																PDC&nbsp;&nbsp;&nbsp;: <%=obj[7]!=null?StringEscapeUtils.escapeHtml4(obj[7].toString()):"-" %> (In Months) <br>
																PDD&emsp;&nbsp;: <%=obj[8]!=null?StringEscapeUtils.escapeHtml4(obj[8].toString()):"-" %> <br>
															</td>
															<td class="center">
																<span style="color: blue;"><%=obj[9]!=null?fc.sdfTordf(StringEscapeUtils.escapeHtml4(obj[9].toString())):"-" %></span> <br>
																<span style="color: red;"><%=obj[10]!=null?fc.sdfTordf(StringEscapeUtils.escapeHtml4(obj[10].toString())):"-" %></span> <br>
																<span style="color: green;"><%=obj[11]!=null?fc.sdfTordf(StringEscapeUtils.escapeHtml4(obj[11].toString())):"-" %></span>
															</td>
															<td class="center">
																<span style="color: blue;"><%=obj[12]!=null?fc.sdfTordf(StringEscapeUtils.escapeHtml4(obj[12].toString())):"-" %></span> <br>
																<span style="color: red;"><%=obj[13]!=null?fc.sdfTordf(StringEscapeUtils.escapeHtml4(obj[13].toString())):"-" %></span> <br>
																<span style="color: green;"><%=obj[14]!=null?fc.sdfTordf(StringEscapeUtils.escapeHtml4(obj[14].toString())):"-" %></span>
															</td>
															<td class="center">
																<span style="color: blue;"><%=obj[15]!=null?fc.sdfTordf(StringEscapeUtils.escapeHtml4(obj[15].toString())):"-" %></span> <br>
																<span style="color: red;"><%=obj[16]!=null?fc.sdfTordf(StringEscapeUtils.escapeHtml4(obj[16].toString())):"-" %></span> <br>
																<span style="color: green;"><%=obj[17]!=null?fc.sdfTordf(StringEscapeUtils.escapeHtml4(obj[17].toString())):"-" %></span>
															</td>
															<td class="center">
																<span style="color: blue;"><%=obj[18]!=null?fc.sdfTordf(StringEscapeUtils.escapeHtml4(obj[18].toString())):"-" %></span> <br>
																<span style="color: red;"><%=obj[19]!=null?fc.sdfTordf(StringEscapeUtils.escapeHtml4(obj[19].toString())):"-" %></span> <br>
																<span style="color: green;"><%=obj[20]!=null?fc.sdfTordf(StringEscapeUtils.escapeHtml4(obj[20].toString())):"-" %></span>
															</td>
															<td class="center">
																<span style="color: blue;"><%=obj[21]!=null?fc.sdfTordf(StringEscapeUtils.escapeHtml4(obj[21].toString())):"-" %></span> <br>
																<span style="color: red;"><%=obj[22]!=null?fc.sdfTordf(StringEscapeUtils.escapeHtml4(obj[22].toString())):"-" %></span> <br>
																<span style="color: green;"><%=obj[23]!=null?fc.sdfTordf(StringEscapeUtils.escapeHtml4(obj[23].toString())):"-" %></span>
															</td>
															<td class="center">
																<span style="color: blue;"><%=obj[24]!=null?fc.sdfTordf(StringEscapeUtils.escapeHtml4(obj[24].toString())):"-" %></span> <br>
																<span style="color: red;"><%=obj[25]!=null?fc.sdfTordf(StringEscapeUtils.escapeHtml4(obj[25].toString())):"-" %></span> <br>
																<span style="color: green;"><%=obj[26]!=null?fc.sdfTordf(StringEscapeUtils.escapeHtml4(obj[26].toString())):"-" %></span>
															</td>
															<td>
																<form action="CCMASPStatusSubmit.htm" method="post" id="aspform_<%=slno%>">
																	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"  />
											     					<input type="hidden" name="committeeId" value="<%=committeeId%>">
											     					<input type="hidden" name="tabName" value="<%=tabName%>">
											     					<input type="hidden" name="ccmScheduleId" value="<%=ccmScheduleId%>">
											     					<input type="hidden" name="initiationId" value="<%=obj[1]%>">
																	<input type="text" name="milestoneStatus" maxlength="1000" placeholder="Enter Maximum 1000 characters" <%if(obj[27]!=null) {%>value="<%=StringEscapeUtils.escapeHtml4(obj[27].toString())%>"<%} %> style="border-left: 0;border-top: 0;border-right: 0;width: 85%;">
																	<button type="submit" class="btn btn-sm ml-2" name="ccmASPStatusId" value="<%=obj[28] %>" onclick="return confirm('Are you sure to Update?')" style="background: linear-gradient(135deg, #FDC830, #F37335);line-height: 1">
																		<i class="fa fa-edit" style="padding: 0px;color: #ffff;font-size: 1.2rem;" aria-hidden="true"></i>
																	</button>
																</form>
															</td>
														</tr>
													<%} } else{%>
														<tr>
															<td colspan="9" class="center">No Data Available</td>
														</tr>
													<%} %>	
												</tbody>
											</table>
										</div>
										
										<div class="center mt-3 mb-2">
											<span style="color: blue;">Probable Date</span> |
											<span style="color: red;">Revised Date</span> |
											<span style="color: green;">Actual Date</span>
										</div>			
									<!-- ----------------------------------------------- ASP Status End --------------------------------------------------- -->	
									
									<!-- ----------------------------------------------- Closure Status --------------------------------------------------- -->	
									<%} else if(tabName!=null && tabName.equalsIgnoreCase("Closure Status")) { 
										String labCode = (String)request.getAttribute("labCode");
										List<Object[]> closureStatusList = (List<Object[]>)request.getAttribute("closureStatusList");	
									%>
										<div class="container-fluid mt-3 tabpanes1">
											<table class="table table-bordered table-hover table-striped table-condensed data-table" style="width: 100%;" >
												<thead style="background-color: #4B70F5; color: #ffff !important;border-radius: 1rem;">
													<tr style="background-color: #4C3BCF;border-radius: 1rem;">
														<th colspan="8" style="border-radius: 1rem;"> <h5>Closure Status</h5></th>
													</tr>
													<%if(clusterLab.equalsIgnoreCase("Y")) {%>
														<tr style="background-color: #ffff;">
															<td colspan="8" >
																
																<div style="display: inline-flex; align-items: flex-end;float: right;">
																	<form action="CCMPresentation.htm" method="get">
																		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"  />
												     					<input type="hidden" name="committeeId" value="<%=committeeId%>">
												     					<input type="hidden" name="tabName" value="<%=tabName%>">
												     					<div style="display: inline-flex; align-items: flex-end;">
																			<label>Lab : </label>
																			&nbsp;
																			<select class="form-control selectdee" id="labCodeClosureStatus" name="labCode" onchange="this.form.submit()" required style="width: 200px;">
																				<option value="0" disabled="disabled">---Select---</option>
																				<%if(clusterLabList!=null && clusterLabList.size()>0) {
																					for(Object[] obj : clusterLabList) {
																				%>
																					<option value="<%=obj[2]%>" <%if(labCode.equalsIgnoreCase(obj[2].toString())) {%>selected<%} %> ><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></option>
																				<%} }%>
																			</select>
																		</div>
																	</form>
																</div>
															
															</td>
														</tr>
													<%} %>	
													<tr>
														<!-- <th>Lab</th> -->
														<th>Project</th>
														<th>DoS / PDC</th>
														<th>Recommendation</th>
														<th>TCR Status</th>
														<th>ACR Status</th>
														<th>Status of Activities</th>
														<th>Action</th>
													</tr>
												</thead>
												<tbody>
													<%if(closureStatusList!=null && closureStatusList.size()>0) { 
														int slno = 0;
														for(Object[] obj : closureStatusList) {
															++slno;
													%>
														<tr>
															<%-- <td><%=obj[1]!=null?obj[1]:"-" %></td> --%>
															<td>
																<%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()):"-" %> <br>
																Cat&emsp;: <%=obj[5]!=null?StringEscapeUtils.escapeHtml4(obj[5].toString()):"-" %> <br>
																Cost&nbsp;&nbsp; : <%=obj[6]!=null?String.format("%.2f", Double.parseDouble(StringEscapeUtils.escapeHtml4(obj[6].toString()))/10000000):"-" %> (In Cr) <br>
																PD&emsp;&nbsp;: <%=obj[7]!=null?StringEscapeUtils.escapeHtml4(obj[7].toString()):"-" %> 
															</td>
															<td class="center">
																<%=obj[8]!=null?fc.sdfTordf(StringEscapeUtils.escapeHtml4(obj[8].toString())):"-" %> / <br> <%=obj[9]!=null?fc.sdfTordf(StringEscapeUtils.escapeHtml4(obj[9].toString())):"-" %> 
															</td>
															<td><%=obj[10]!=null?StringEscapeUtils.escapeHtml4(obj[10].toString()):"-" %></td>
															<td><%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()):"-" %></td>
															<td><%=obj[12]!=null?StringEscapeUtils.escapeHtml4(obj[12].toString()):"-" %></td>
															<td><%=obj[13]!=null?StringEscapeUtils.escapeHtml4(obj[13].toString()):"-" %></td>
															<td class="center">
																<form action="#">
																	<input type="hidden" name="action" value="Edit">
																	<input type="hidden" name="tabName" value="<%=tabName%>">
																	<textarea id="recommendationEditData_<%=slno %>" style="display: none;"><%=obj[10]!=null?obj[10].toString(): "" %></textarea>
																	<textarea id="tcrStatusEditData_<%=slno %>" style="display: none;"><%=obj[11]!=null?obj[11].toString(): "" %></textarea>
																	<textarea id="acrStatusEditData_<%=slno %>" style="display: none;"><%=obj[12]!=null?obj[12].toString(): "" %></textarea>
																	<textarea id="activityStatusEditData_<%=slno %>" style="display: none;"><%=obj[13]!=null?obj[13].toString(): "" %></textarea>
																	<button type="button" class="btn btn-lg" formmethod="post" data-toggle="tooltip" data-placement="top" title="Edit Closure Status"
																	 onclick="openClosureStatusModalEdit('<%=obj[0]%>', '<%=obj[1]%>', '<%=obj[3]%>', '<%=slno%>')">
																		<i class="fa fa-pencil-square-o" aria-hidden="true"></i>
																	</button>
																	<button type="submit" class="btn btn-lg" name="ccmClosureId" value="<%=obj[0]!=null?StringEscapeUtils.escapeHtml4(obj[0].toString()): "" %>" formmethod="get" formaction="CCMClosureStatusDelete.htm" onclick="return confirm('Are you sure To Delete?')" data-toggle="tooltip" data-placement="top" title="Delete Closure Status"> 
																		<i class="fa fa-trash" aria-hidden="true"></i>
																	</button>
																</form>
															</td>
														</tr>
													<%} } else{%>
														<tr>
															<td colspan="7" class="center">No Data Available</td>
														</tr>
													<%} %>	
												</tbody>
											</table>
										</div>
										<div class="center mt-2 mb-2">
											<div class="row">
												<div class="col-md-4"></div>
												<div class="col-md-4 center">
													<button type="button" class="btn btn-sm fw-bold add" data-toggle="tooltip" data-target="modal" title="Add Closure Status" onclick="openClosureStatusModal()" >
														ADD CLOSURE STATUS
													</button>
												</div>
												<div class="col-md-4 right"></div>
											</div>
										</div>	
										
										<!-- ------------------------------------------------------------- Closure Status Modal (Add)  ------------------------------------------------------------------------- -->
										<div class="modal fade bd-example-modal-lg" id="closureStatusModal" tabindex="-1" role="dialog" aria-labelledby="closureStatusModal" aria-hidden="true" style="margin-top: 5%;">
											<div class="modal-dialog modal-lg" role="document" style="max-width: 1500px;">
												<div class="modal-content">
													<div class="modal-header bg-primary text-light">
											        	<h5 class="modal-title">Closure Status</h5>
												        <button type="button" class="close" style="text-shadow: none!important" data-dismiss="modal" aria-label="Close">
												          <span aria-hidden="true" style="color:red;">&times;</span>
												        </button>
											      	</div>
									     			<div class="modal-body">
									     				<form action="CCMClosureStatusSubmit.htm" method="post" id="closureStatusForm">
									     					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"  />
									     					<input type="hidden" name="scheduleId" value="<%=ccmScheduleId%>">
									     					<input type="hidden" name="committeeId" value="<%=committeeId%>">
									     					<input type="hidden" name="tabName" value="<%=tabName%>">
									     					
									     					<table class="table table-hover table-bordered" style="width: 100%;" id="closureStatusTable">
									     						<thead>
										     						<tr>
										     							<th style="width: 5%;">Lab <span class="mandatory">*</span></th>
										     							<th style="width: 10%;">Project <span class="mandatory">*</span></th>
										     							<th style="width: 20%;">Recommendation <span class="mandatory">*</span></th>
										     							<th style="width: 20%;">TCR Status <span class="mandatory">*</span></th>
										     							<th style="width: 20%;">ACR Status <span class="mandatory">*</span></th>
										     							<th style="width: 20%;">Status of Activities <span class="mandatory">*</span></th>
										     							<th style="width: 5%;"> 
																			<button type="button" class="btn btn-sm tr_clone_add_closure_status" name="add" data-toggle="tooltip" data-placement="top" title="Add New Closure Status"> <i class="btn fa fa-plus" style="color: green; padding: 0px  ;"></i></button>
																		</th>
										     						</tr>
									     						</thead>
									     						<tbody class="tbody_clone_closure_status">
									     							<tr class="tr_clone_closure_status">
									     								<td class="left">
									     									<select class="form-control selectitems labCodeClosure" id="labCodeClosure_1" name="labCode" required style="width: 100px;" onchange="getProjectList('1')">
																				<%if(clusterLab.equalsIgnoreCase("N")) { 
																					clusterLabList = clusterLabList.stream().filter(e -> e[2].toString().equalsIgnoreCase(labcode)).collect(Collectors.toList()) ;
																				}%>
																				<option value="" disabled selected>---Select---</option>
																				<%if(clusterLabList!=null && clusterLabList.size()>0) {
																					for(Object[] obj : clusterLabList) {
																				%>
																					<option value="<%=obj[2]%>" <%if(labcode.equalsIgnoreCase(obj[2].toString())) {%>selected <%} %> ><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></option>
																				<%} }%>
																			</select>
									     								</td>
									     								<td>
									     									<select class="form-control selectitems projectId" id="projectId_1" name="projectId" required style="width: 200px;">
										  										<option disabled="disabled" value="" selected="selected">--Select--</option>
										  										<!-- <option value="0">General</option> -->
										  									</select>
									     								</td>
									     								<td>
									     									<button type="button" class="form-control statusadd recommendationBtn" id="recommendationBtn_1" onclick="openEditor('Recommendations', 'recommendation', '1')" style="border: 1px solid #ced4da;height: 35px;width: 15rem;">
																				Enter Recommendations
																			</button>
																			<textarea class="form-control statusadd recommendation" name="recommendation" id="recommendation_1" style="display: none;"></textarea>
									     								</td>
									     								<td>
									     									<button type="button" class="form-control statusadd tcrStatusBtn" id="tcrStatusBtn_1" onclick="openEditor('TCR Status', 'tcrStatus', '1')" style="border: 1px solid #ced4da;height: 35px;width: 15rem;">
																				Enter TCR Status
																			</button>
																			<textarea class="form-control statusadd tcrStatus" name="tcrStatus" id="tcrStatus_1" style="display: none;"></textarea>
									     								</td>
									     								<td>
									     									<button type="button" class="form-control statusadd acrStatusBtn" id="acrStatusBtn_1" onclick="openEditor('ACR Status', 'acrStatus', '1')" style="border: 1px solid #ced4da;height: 35px;width: 15rem;">
																				Enter ACR Status
																			</button>
																			<textarea class="form-control statusadd acrStatus" name="acrStatus" id="acrStatus_1" style="display: none;"></textarea>
									     								</td>
									     								<td>
									     									<button type="button" class="form-control statusadd activityStatusBtn" id="activityStatusBtn_1" onclick="openEditor('Status of Activities', 'activityStatus', '1')" style="border: 1px solid #ced4da;height: 35px;width: 15rem;">
																				Enter Status of Activities
																			</button>
																			<textarea class="form-control statusadd activityStatus" name="activityStatus" id="activityStatus_1" style="display: none;"></textarea>
									     								</td>
									     								<td class="center">
																			<button type="button" class="btn btn-sm tr_clone_rem_closure_status" name="sub"  data-toggle="tooltip" data-placement="top" title="Remove Closure Status"> <i class="btn fa fa-minus" style="color: red;padding: 0px  ;"> </i></button>
																		</td>
									     							</tr>
									     						</tbody>
									     					</table>
										     				
											         		<div class="center mt-2">
											         			<button type="submit" name="action" value="Add" class="btn btn-sm submit btn-closurestatus" onclick="return confirm('Are you Sure to Submit?')">Submit</button>
											         		</div>
										         		</form>
										         	</div>
									    		</div>
									  		</div>
										</div>	
										<!-- ------------------------------------------------------------- Closure Status Modal (Add) End ------------------------------------------------------------------------- -->
										
										<!-- ------------------------------------------------------------- Closure Status Modal (Edit)  ------------------------------------------------------------------------- -->
										<div class="modal fade bd-example-modal-lg" id="closureStatusModalEdit" tabindex="-1" role="dialog" aria-labelledby="closureStatusModalEdit" aria-hidden="true" style="margin-top: 5%;">
											<div class="modal-dialog modal-lg" role="document" style="max-width: 1150px;">
												<div class="modal-content">
													<div class="modal-header bg-primary text-light">
											        	<h5 class="modal-title">Closure Status Edit - <span id="closureStatusEditHeading"></span> </h5>
												        <button type="button" class="close" style="text-shadow: none!important" data-dismiss="modal" aria-label="Close">
												          <span aria-hidden="true" style="color:red;">&times;</span>
												        </button>
											      	</div>
									     			<div class="modal-body">
									     				<form action="CCMClosureStatusEditSubmit.htm" method="post" id="closureStatusFormEdit">
									     					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"  />
									     					<input type="hidden" name="scheduleId" value="<%=ccmScheduleId%>">
									     					<input type="hidden" name="committeeId" value="<%=committeeId%>">
									     					<input type="hidden" name="tabName" value="<%=tabName%>">
									     					<input type="hidden" name="ccmClosureId" id="ccmClosureId">
									     					
									     					<div class="form-group">
									     						<div class="row">
										     						<div class="col-md-2 left"><label>Recommendation <span class="mandatory">*</span></label></div>
										     						<div class="col-md-4">
										     							<button type="button" class="form-control statusedit recommendationBtn" id="recommendationEditBtn_1" onclick="openEditor('Recommendations', 'recommendationEdit', '1')" style="border: 1px solid #ced4da;height: 35px;width: 15rem;">
																			Enter Recommendations
																		</button>
																		<textarea class="form-control statusedit recommendation" name="recommendation" id="recommendationEdit_1" style="display: none;"></textarea>
										     						</div>
										     						<div class="col-md-2 left"><label>TCR Status <span class="mandatory">*</span></label></div>
										     						<div class="col-md-4">
										     							<button type="button" class="form-control statusedit tcrStatusBtn" id="tcrStatusEditBtn_1" onclick="openEditor('TCR Status', 'tcrStatusEdit', '1')" style="border: 1px solid #ced4da;height: 35px;width: 15rem;">
																			Enter TCR Status
																		</button>
																		<textarea class="form-control statusedit tcrStatus" name="tcrStatus" id="tcrStatusEdit_1" style="display: none;"></textarea>
										     						</div>
										     					</div>
									     					</div>
									     					
									     					<div class="form-group">
										     					<div class="row">
										     						<div class="col-md-2 left"><label>ACR Status: <span class="mandatory">*</span></label></div>
										     						<div class="col-md-4">
										     							<button type="button" class="form-control statusedit acrStatusBtn" id="acrStatusEditBtn_1" onclick="openEditor('ACR Status', 'acrStatusEdit', '1')" style="border: 1px solid #ced4da;height: 35px;width: 15rem;">
																			Enter ACR Status
																		</button>
																		<textarea class="form-control statusedit acrStatus" name="acrStatus" id="acrStatusEdit_1" style="display: none;"></textarea>
										     						</div>
										     						<div class="col-md-2 left"><label>Status of Activities <span class="mandatory">*</span></label></div>
										     						<div class="col-md-4">
										     							<button type="button" class="form-control statusedit activityStatusBtn" id="activityStatusEditBtn_1" onclick="openEditor('Status of Activities', 'activityStatusEdit', '1')" style="border: 1px solid #ced4da;height: 35px;width: 15rem;">
																			Enter Activity Status
																		</button>
																		<textarea class="form-control statusedit activityStatus" name="activityStatus" id="activityStatusEdit_1" style="display: none;"></textarea>
										     						</div>
										     					</div>
						     								</div>	
										     				
											         		<div class="center mt-2">
											         			<button type="submit" name="action" value="Add" class="btn btn-sm submit btn-closurestatus" onclick="return confirm('Are you Sure to Submit?')">Submit</button>
											         		</div>
										         		</form>
										         	</div>
									    		</div>
									  		</div>
										</div>	
										<!-- ------------------------------------------------------------- Closure Status Modal (Edit) End ------------------------------------------------------------------------- -->
												
									<!-- ----------------------------------------------- Closure Status End --------------------------------------------------- -->	
									
									<!-- ----------------------------------------------- Cash Out Go Status --------------------------------------------------- -->	
									<%} else if(tabName!=null && tabName.equalsIgnoreCase("Cash Out Go Status")) { 
										String labCode = (String)request.getAttribute("labCode");
										int quarter = (int)request.getAttribute("quarter");
										List<Object[]> cashOutGoList = (List<Object[]>)request.getAttribute("cashOutGoList");
										
									%>
										<div class="container-fluid mt-3 tabpanes2">
											<table class="table table-bordered table-hover table-striped table-condensed data-table" style="width: 100%;" >
												<thead style="background-color: #4B70F5; color: #ffff !important;border-radius: 1rem;">
													<tr style="background-color: #4C3BCF;border-radius: 1rem;">
														<th colspan="<%=12-quarter %>" style="border-radius: 1rem;"> <h5>Cash Out Go Status</h5></th>
													</tr>
													<%-- <%if(clusterLab.equalsIgnoreCase("Y") || (cashOutGoList!=null && cashOutGoList.size()==0) || cashOutGoList==null) {%> --%>
														<tr style="background-color: #ffff;">
															<td colspan="<%=12-quarter %>" >
																<%-- <%if((cashOutGoList!=null && cashOutGoList.size()==0) || cashOutGoList==null) {%> --%>
																	<div style="display: inline-flex; align-items: flex-start;float: left;">
																		<form action="CCMCashOutGoStatusExcelUpload.htm" method="post" id="excelForm" enctype="multipart/form-data">
																			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"  />
													     					<input type="hidden" name="committeeId" value="<%=committeeId%>">
													     					<input type="hidden" name="tabName" value="<%=tabName%>">
													     					<input type="hidden" name="labCode" value="<%=labCode%>">
													     					<div style="display: inline-flex; align-items: flex-start;">
																				<label style="width: 5rem;margin-top: 0.5rem;">Upload : </label>
																				&nbsp;
																				 <input class="form-control" type="file" id="ccmExcelFile" name="filename" required="required"  accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">
																			</div>
																			&nbsp;&nbsp;
																			<button type="submit" class="btn btn-sm submit" onclick="return confirm('Are you sure to upload?')" data-toggle="tooltip" title="Upload">
																				<i class="fa fa-upload"></i>
																			</button>
																		</form>
																	</div>
																	<div style="display: inline-flex; align-items: flex-start;float: left;margin-left: 3rem;">
																		<form action="CCMCashOutGoStatusExcel.htm" method="post">
																			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
																			<input type="hidden" name="labCode" value="<%=labCode%>">
																			<div style="">
																				<button class="btn btn-sm" data-toggle="tooltip" type="submit" data-toggle="tooltip" data-placement="top"  title="Download Format" style="margin-top: 0.5rem;" >
																					<i class="fa fa-download fa-lg" aria-hidden="true"></i>&nbsp; Format
																				</button>
																			</div>	
																		</form>
																	</div>
																<%-- <%} %> --%>
																<%if(clusterLab.equalsIgnoreCase("Y")) {%>
																	<div style="display: inline-flex; align-items: flex-end;float: right;">
																		<form action="CCMPresentation.htm" method="get">
																			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"  />
													     					<input type="hidden" name="committeeId" value="<%=committeeId%>">
													     					<input type="hidden" name="tabName" value="<%=tabName%>">
													     					<div style="display: inline-flex; align-items: flex-end;">
																				<label>Lab : </label>
																				&nbsp;
																				<select class="form-control selectdee" id="labCodeCOG" name="labCode" onchange="this.form.submit()" required style="width: 200px;">
																					<option value="0" disabled="disabled">---Select---</option>
																					<%if(clusterLabList!=null && clusterLabList.size()>0) {
																						
																						for(Object[] obj : clusterLabList) {
																							if(obj[3]!=null && obj[3].toString().equalsIgnoreCase("Y")) continue;
																					%>
																						<option value="<%=obj[2]%>" <%if(labCode.equalsIgnoreCase(obj[2].toString())) {%>selected<%} %> ><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></option>
																					<%} }%>
																				</select>
																			</div>
																		</form>
																	</div>
																<%} %>	
															</td>
														</tr>
													<%-- <%} %>	 --%>
													<tr>
														<td colspan="<%=12-quarter %>" class="right" style="background-color: #ffff;color: black;"> <h5>In Lakhs</h5></td>
													</tr>
													<tr>
														<th>SN</th>
														<th>Project</th>
														<!-- <th>Budget Head</th> -->
														<th>Allotment</th>
														<th>Expenditure</th>
														<th>Balance</th>
														<%if(quarter<=1) {%>
															<th>COG Q1</th>
														<%} %>
														<%if(quarter<=2) {%>
															<th>COG Q2</th>
														<%} %>
														<%if(quarter<=3) {%>
															<th>COG Q3</th>
														<%} %>
														<%if(quarter<=4) {%>
															<th>COG Q4</th>
														<%} %>
														<th>Total COG</th>
														<th>Addl(-)/Surr(+)</th>
													</tr>
												</thead>
												<tbody>
													<%if(cashOutGoList!=null && cashOutGoList.size()>0) {
														int slno=0; 
														Double allotment = 0.00, expenditure = 0.00, balance = 0.00, cogQ1 = 0.00, cogQ2 = 0.00, cogQ3 = 0.00, cogQ4 = 0.00, cogTotal = 0.00, addl = 0.00,
																totoalAllotment = 0.00, totoalExpenditure = 0.00, totoalBalance = 0.00, totoalCOGQ1 = 0.00, totalCOGQ2 = 0.00, totalCOGQ3 = 0.00, totalCOGQ4 = 0.00, totalcogTotal = 0.00, totalAddl = 0.00;
														String budgetHead ="";
														Double lakh = 100000.00;
														String decimalPoint = "%.2f";
														for(Object[] obj : cashOutGoList) {
													%>
														<%if(!budgetHead.equalsIgnoreCase(obj[6].toString()) || slno==0 ) { %>
														 	<%if(slno!=0) {%>
															 	<tr>
																	<td class="right" colspan="2"><b>Total Amount (<%=StringEscapeUtils.escapeHtml4(budgetHead)%>) : </b> </td>
																	<td class="right"><%=String.format(decimalPoint, allotment/lakh) %></td>
																	<td class="right"><%=String.format(decimalPoint, expenditure/lakh) %></td>
																	<td class="right"><%=String.format(decimalPoint, balance/lakh) %></td>
																	<%if(quarter<=1) {%>
																		<td class="right"><%=String.format(decimalPoint, cogQ1/lakh) %></td>
																	<%} %>
																	<%if(quarter<=2) {%>
																		<td class="right"><%=String.format(decimalPoint, cogQ2/lakh) %></td>
																	<%} %>
																	<%if(quarter<=3) {%>
																		<td class="right"><%=String.format(decimalPoint, cogQ3/lakh) %></td>
																	<%} %>
																	<%if(quarter<=4) {%>
																		<td class="right"><%=String.format(decimalPoint, cogQ4/lakh) %></td>
																	<%} %>
																	<td class="right"><b><%=String.format(decimalPoint, cogTotal/lakh) %></b></td>
																	<td class="right"><b><%=String.format(decimalPoint, addl/lakh) %></b></td>
																</tr>	
														 	<%} %>
														 	<%budgetHead = obj[6]!=null?StringEscapeUtils.escapeHtml4(obj[6].toString()): ""; %>
															<tr>
																<td colspan="<%=12-quarter %>" class="center" style="background-color: aliceblue;"><b>Budget Head : <%=budgetHead %></b></td>
															</tr>
														<%
															totoalAllotment+=allotment;
															totoalExpenditure+=expenditure;
															totoalBalance+=balance;
															totoalCOGQ1+=cogQ1;
															totalCOGQ2+=cogQ2;
															totalCOGQ3+=cogQ3;
															totalCOGQ4+=cogQ4;
															totalcogTotal+=cogTotal;
															totalAddl+=addl;
														
															allotment = expenditure = balance = cogQ1 = cogQ2 = cogQ3 = cogQ4 = cogTotal = addl = 0.00;
														} 
															allotment+=Double.parseDouble(obj[7]!=null?obj[7].toString():"0.00");
															expenditure+=Double.parseDouble(obj[8]!=null?obj[8].toString():"0.00");
															balance+=Double.parseDouble(obj[9]!=null?obj[9].toString():"0.00");
															cogQ1+=Double.parseDouble(obj[10]!=null?obj[10].toString():"0.00");
															cogQ2+=Double.parseDouble(obj[11]!=null?obj[11].toString():"0.00");
															cogQ3+=Double.parseDouble(obj[12]!=null?obj[12].toString():"0.00");
															cogQ4+=Double.parseDouble(obj[13]!=null?obj[13].toString():"0.00");
															cogTotal+=Double.parseDouble(obj[16]!=null?obj[16].toString():"0.00");
															addl+=Double.parseDouble(obj[14]!=null?obj[14].toString():"0.00");
														%>
														<tr>
															<td class="center"><%=++slno %></td>
															<td ><%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - " %></td>
															<%-- <td><%=obj[6] %></td> --%>
															<td class="right"><%=String.format(decimalPoint, Double.parseDouble(obj[7]!=null?obj[7].toString():"0.00")/lakh) %></td>
															<td class="right"><%=String.format(decimalPoint, Double.parseDouble(obj[8]!=null?obj[8].toString():"0.00")/lakh) %></td>
															<td class="right"><%=String.format(decimalPoint, Double.parseDouble(obj[9]!=null?obj[9].toString():"0.00")/lakh) %></td>
															<%if(quarter<=1) {%>
																<td class="right"><%=String.format(decimalPoint, Double.parseDouble(obj[10]!=null?obj[10].toString():"0.00")/lakh) %></td>
															<%} %>
															<%if(quarter<=2) {%>
																<td class="right"><%=String.format(decimalPoint, Double.parseDouble(obj[11]!=null?obj[11].toString():"0.00")/lakh) %></td>
															<%} %>
															<%if(quarter<=3) {%>
																<td class="right"><%=String.format(decimalPoint, Double.parseDouble(obj[12]!=null?obj[12].toString():"0.00")/lakh) %></td>
															<%} %>
															<%if(quarter<=4) {%>
																<td class="right"><%=String.format(decimalPoint, Double.parseDouble(obj[13]!=null?obj[13].toString():"0.00")/lakh) %></td>
															<%} %>
															<td class="right"><b><%=String.format(decimalPoint, Double.parseDouble(obj[16]!=null?obj[16].toString():"0.00")/lakh) %></b></td>
															<td class="right"><b><%=String.format(decimalPoint, Double.parseDouble(obj[14]!=null?obj[14].toString():"0.00")/lakh) %></b></td>
														</tr>
														<%if(slno==cashOutGoList.size()) { %>
														 	<tr>
																<td class="right" colspan="2"> <b>Total Amount (<%=budgetHead %>) :</b> </td>
																<td class="right"><%=String.format(decimalPoint, allotment/lakh) %></td>
																<td class="right"><%=String.format(decimalPoint, expenditure/lakh) %></td>
																<td class="right"><%=String.format(decimalPoint, balance/lakh) %></td>
																<%if(quarter<=1) {%>
																	<td class="right"><%=String.format(decimalPoint, cogQ1/lakh) %></td>
																<%} %>
																<%if(quarter<=2) {%>
																	<td class="right"><%=String.format(decimalPoint, cogQ2/lakh) %></td>
																<%} %>
																<%if(quarter<=3) {%>
																	<td class="right"><%=String.format(decimalPoint, cogQ3/lakh) %></td>
																<%} %>
																<%if(quarter<=4) {%>
																	<td class="right"><%=String.format(decimalPoint, cogQ4/lakh) %></td>
																<%} %>
																<td class="right"><b><%=String.format(decimalPoint, cogTotal/lakh) %></b></td>
																<td class="right"><b><%=String.format(decimalPoint, addl/lakh) %></b></td>
															</tr>	
															<%
																totoalAllotment+=allotment;
																totoalExpenditure+=expenditure;
																totoalBalance+=balance;
																totoalCOGQ1+=cogQ1;
																totalCOGQ2+=cogQ2;
																totalCOGQ3+=cogQ3;
																totalCOGQ4+=cogQ4;
																totalcogTotal+=cogTotal;
																totalAddl+=addl;
															%>
															<tr>
																<td class="right" colspan="2"> <b>Grand Total Amount :</b> </td>
																<td class="right"><%=String.format(decimalPoint, totoalAllotment/lakh) %></td>
																<td class="right"><%=String.format(decimalPoint, totoalExpenditure/lakh) %></td>
																<td class="right"><%=String.format(decimalPoint, totoalBalance/lakh) %></td>
																<%if(quarter<=1) {%>
																	<td class="right"><%=String.format(decimalPoint, totoalCOGQ1/lakh) %></td>
																<%} %>
																<%if(quarter<=2) {%>
																	<td class="right"><%=String.format(decimalPoint, totalCOGQ2/lakh) %></td>
																<%} %>
																<%if(quarter<=3) {%>
																	<td class="right"><%=String.format(decimalPoint, totalCOGQ3/lakh) %></td>
																<%} %>
																<%if(quarter<=4) {%>
																	<td class="right"><%=String.format(decimalPoint, totalCOGQ4/lakh) %></td>
																<%} %>
																<td class="right"><b><%=String.format(decimalPoint, totalcogTotal/lakh) %></b></td>
																<td class="right"><b><%=String.format(decimalPoint, totalAddl/lakh) %></b></td>
															</tr>	
														<% } %>
													<%} } else{%>
														<tr>
															<td colspan="<%=12-quarter %>" class="center">No Data Available</td>
														</tr>
													<%} %>
												</tbody>
											</table>
										</div>
													
									<!-- ----------------------------------------------- Cash Out Go Status End --------------------------------------------------- -->	
									
									<!-- ----------------------------------------------- Test & Trials --------------------------------------------------- -->	
									<%} else if(tabName!=null && tabName.equalsIgnoreCase("Test & Trials")) { 
										List<CCMAchievements> ccmAchievementsList = (List<CCMAchievements>) request.getAttribute("ccmAchievementsList");
										
										if(clusterLabList!=null && clusterLabList.size()>0) {

											Set<String> achmntLabCodes = ccmAchievementsList.stream().map(CCMAchievements::getLabCode).collect(Collectors.toSet());

											// Filter the clusterLabList to exclude records already in ccmAchievementsList
											clusterLabListFilter = clusterLabList.stream().filter(e -> !achmntLabCodes.contains(e[2].toString())).collect(Collectors.toList());
										}
										
										if(clusterLab.equalsIgnoreCase("N")){
											ccmAchievementsList = ccmAchievementsList.stream().filter(e -> e.getLabCode().equalsIgnoreCase(labcode)).collect(Collectors.toList());
										}
									%>
										<div class="container-fluid mt-3 tabpanes1">
											<table class="table table-bordered table-hover table-striped table-condensed " style="width: 100%;" >
												<thead style="background-color: #4B70F5; color: #ffff !important;border-radius: 1rem;">
													<tr style="background-color: #4C3BCF;border-radius: 1rem;">
														<th colspan="6" style="border-radius: 1rem;"> <h5>Test & Trials</h5></th>
													</tr>
													<tr >
														<th style="width: 5%;">SN</th>
														<th style="width: 15%;">Lab</th>
														<th style="width: 70%;">Test & Trials</th>
														<th style="width: 10%;">Action</th>
													</tr>
												</thead>
												<tbody>
													<%if(ccmAchievementsList!=null && ccmAchievementsList.size()>0) {
														int slno=0;
														for(CCMAchievements achmnts : ccmAchievementsList) {
													%>
														<tr>
															<td class="center"><%=++slno %></td>
															<td><%=achmnts.getLabCode()!=null?achmnts.getLabCode(): " - " %></td>
															<td><%=achmnts.getAchievement()!=null?achmnts.getAchievement(): " - " %></td>
															<td class="center">
																<form action="#">
																	<input type="hidden" name="action" value="Edit">
																	<textarea class="achievement" name="achievement" id="achievement_<%=slno %>" style="display: none;"><%=achmnts.getAchievement()!=null?achmnts.getAchievement(): "" %></textarea>
																	<button type="button" class="btn btn-lg" formmethod="post" formaction="CCMAchievementSubmit.htm" data-toggle="tooltip" data-placement="top" title="Edit Achievement"
																	 onclick="openAchievementsModalEdit('<%=slno %>','<%=achmnts.getAchievementId() %>','<%=achmnts.getLabCode() %>', '<%=achmnts.getTopicType() %>','<%=achmnts.getImageName() %>','<%=achmnts.getAttachmentName() %>','<%=achmnts.getVideoName() %>')">
																		<i class="fa fa-pencil-square-o" aria-hidden="true"></i>
																	</button>
																	<button type="submit" class="btn btn-lg" name="achievementId" value="<%=achmnts.getAchievementId() %>" formmethod="get" formaction="CCMAchievementDelete.htm" onclick="return confirm('Are you sure To Delete this Achievement?')" data-toggle="tooltip" data-placement="top" title="Delete Achievement"> 
																		<i class="fa fa-trash" aria-hidden="true"></i>
																	</button>
																</form>
															</td>
														</tr>
													<%}} else{%>
														<tr>
															<td colspan="6" style="text-align: center;">No Data Available</td>
														</tr>
													<%} %>
												</tbody>
											</table>
										</div>
										
										<div class="center mt-2 mb-2">
											<div class="row">
												<div class="col-md-4"></div>
												<div class="col-md-4 center">
													<button type="button" class="btn btn-sm fw-bold add" data-toggle="tooltip" data-target="modal" title="Add Test & Trials" onclick="openAchievementsModal('T')"
														<%if(clusterLab.equalsIgnoreCase("N") && ccmAchievementsList.size()>0) {%>disabled<%} %> >
														ADD TEST & TRIALS
													</button>
												</div>
												<div class="col-md-4 right"></div>
											</div>
										</div>				
									<!-- ----------------------------------------------- Test & Trials End --------------------------------------------------- -->	
									
									<!-- ----------------------------------------------- Achievements --------------------------------------------------- -->		
									<%} else if(tabName!=null && tabName.equalsIgnoreCase("Achievements")) {
											List<CCMAchievements> ccmAchievementsList = (List<CCMAchievements>) request.getAttribute("ccmAchievementsList");
											
											if(clusterLabList!=null && clusterLabList.size()>0) {

												Set<String> achmntLabCodes = ccmAchievementsList.stream().map(CCMAchievements::getLabCode).collect(Collectors.toSet());

												// Filter the clusterLabList to exclude records already in ccmAchievementsList
												clusterLabListFilter = clusterLabList.stream().filter(e -> !achmntLabCodes.contains(e[2].toString())).collect(Collectors.toList());
											}
											
											if(clusterLab.equalsIgnoreCase("N")){
												ccmAchievementsList = ccmAchievementsList.stream().filter(e -> e.getLabCode().equalsIgnoreCase(labcode)).collect(Collectors.toList());
											}

									%>
										<div class="container-fluid mt-3 tabpanes1">
											<table class="table table-bordered table-hover table-striped table-condensed data-table" style="width: 100%;" >
												<thead style="background-color: #4B70F5; color: #ffff !important;border-radius: 1rem;">
													<tr style="background-color: #4C3BCF;border-radius: 1rem;">
														<th colspan="6" style="border-radius: 1rem;"> <h5>Achievements</h5></th>
													</tr>
													<tr >
														<th style="width: 5%;">SN</th>
														<th style="width: 15%;">Lab</th>
														<th style="width: 70%;">Achievement</th>
														<th style="width: 10%;">Action</th>
													</tr>
												</thead>
												<tbody>
													<%if(ccmAchievementsList!=null && ccmAchievementsList.size()>0) {
														int slno=0;
														for(CCMAchievements achmnts : ccmAchievementsList) {
													%>
														<tr>
															<td class="center"><%=++slno %></td>
															<td><%=achmnts.getLabCode()!=null?achmnts.getLabCode(): " - " %></td>
															<td><%=achmnts.getAchievement()!=null?achmnts.getAchievement(): " - " %></td>
															<td class="center">
																<form action="#">
																	<input type="hidden" name="action" value="Edit">
																	<textarea class="achievement" name="achievement" id="achievement_<%=slno %>" style="display: none;"><%=achmnts.getAchievement()!=null?achmnts.getAchievement(): "" %></textarea>
																	<button type="button" class="btn btn-lg" formmethod="post" formaction="CCMAchievementSubmit.htm" data-toggle="tooltip" data-placement="top" title="Edit Achievement"
																	 onclick="openAchievementsModalEdit('<%=slno %>','<%=achmnts.getAchievementId() %>','<%=achmnts.getLabCode() %>', '<%=achmnts.getTopicType() %>','<%=achmnts.getImageName() %>','<%=achmnts.getAttachmentName() %>','<%=achmnts.getVideoName() %>')">
																		<i class="fa fa-pencil-square-o" aria-hidden="true"></i>
																	</button>
																	<button type="submit" class="btn btn-lg" name="achievementId" value="<%=achmnts.getAchievementId() %>" formmethod="get" formaction="CCMAchievementDelete.htm" onclick="return confirm('Are you sure To Delete this Achievement?')" data-toggle="tooltip" data-placement="top" title="Delete Achievement"> 
																		<i class="fa fa-trash" aria-hidden="true"></i>
																	</button>
																</form>
															</td>
														</tr>
													<%}} else{%>
														<tr>
															<td colspan="6" style="text-align: center;">No Data Available</td>
														</tr>
													<%} %>
												</tbody>
											</table>
										</div>
										<div class="center mt-2 mb-2">
											<div class="row">
												<div class="col-md-4"></div>
												<div class="col-md-4 center">
													<button type="button" class="btn btn-sm fw-bold add" data-toggle="tooltip" data-target="modal" title="Add Achievement" onclick="openAchievementsModal('A')"
														<%if(clusterLab.equalsIgnoreCase("N") && ccmAchievementsList.size()>0) {%>disabled<%} %> >
														ADD ACHIEVEMENT
													</button>
												</div>
												<div class="col-md-4 right"></div>
											</div>
										</div>	
										
									<!-- ----------------------------------------------- Achievements End--------------------------------------------------- -->	
									
									<!-- ----------------------------------------------- Others --------------------------------------------------- -->	
									<%} else if(tabName!=null && tabName.equalsIgnoreCase("Others")) { 
										List<CCMAchievements> ccmAchievementsList = (List<CCMAchievements>) request.getAttribute("ccmAchievementsList");
										
										if(clusterLabList!=null && clusterLabList.size()>0) {

											Set<String> achmntLabCodes = ccmAchievementsList.stream().map(CCMAchievements::getLabCode).collect(Collectors.toSet());

											// Filter the clusterLabList to exclude records already in ccmAchievementsList
											clusterLabListFilter = clusterLabList.stream().filter(e -> !achmntLabCodes.contains(e[2].toString())).collect(Collectors.toList());
										}
										
										if(clusterLab.equalsIgnoreCase("N")){
											ccmAchievementsList = ccmAchievementsList.stream().filter(e -> e.getLabCode().equalsIgnoreCase(labcode)).collect(Collectors.toList());
										}
									%>
										<div class="container-fluid mt-3 tabpanes2">
											<table class="table table-bordered table-hover table-striped table-condensed " style="width: 100%;" >
												<thead style="background-color: #4B70F5; color: #ffff !important;border-radius: 1rem;">
													<tr style="background-color: #4C3BCF;border-radius: 1rem;">
														<th colspan="6" style="border-radius: 1rem;"> <h5>Others</h5></th>
													</tr>
													<tr >
														<th style="width: 5%;">SN</th>
														<th style="width: 15%;">Lab</th>
														<th style="width: 70%;">Details</th>
														<th style="width: 10%;">Action</th>
													</tr>
												</thead>
												<tbody>
													<%if(ccmAchievementsList!=null && ccmAchievementsList.size()>0) {
														int slno=0;
														for(CCMAchievements achmnts : ccmAchievementsList) {
													%>
														<tr>
															<td class="center"><%=++slno %></td>
															<td><%=achmnts.getLabCode()!=null?achmnts.getLabCode(): " - " %></td>
															<td><%=achmnts.getAchievement()!=null?achmnts.getAchievement(): " - " %></td>
															<td class="center">
																<form action="#">
																	<input type="hidden" name="action" value="Edit">
																	<textarea class="achievement" name="achievement" id="achievement_<%=slno %>" style="display: none;"><%=achmnts.getAchievement() !=null?achmnts.getAchievement(): ""%></textarea>
																	<button type="button" class="btn btn-lg" formmethod="post" formaction="CCMAchievementSubmit.htm" data-toggle="tooltip" data-placement="top" title="Edit Achievement"
																	 onclick="openAchievementsModalEdit('<%=slno %>','<%=achmnts.getAchievementId() %>','<%=achmnts.getLabCode() %>', '<%=achmnts.getTopicType() %>','<%=achmnts.getImageName() %>','<%=achmnts.getAttachmentName() %>','<%=achmnts.getVideoName() %>')">
																		<i class="fa fa-pencil-square-o" aria-hidden="true"></i>
																	</button>
																	<button type="submit" class="btn btn-lg" name="achievementId" value="<%=achmnts.getAchievementId() %>" formmethod="get" formaction="CCMAchievementDelete.htm" onclick="return confirm('Are you sure To Delete?')" data-toggle="tooltip" data-placement="top" title="Delete Topic"> 
																		<i class="fa fa-trash" aria-hidden="true"></i>
																	</button>
																</form>
															</td>
														</tr>
													<%}} else{%>
														<tr>
															<td colspan="6" style="text-align: center;">No Data Available</td>
														</tr>
													<%} %>
												</tbody>
											</table>
										</div>
										<div class="center mt-2 mb-2">
											<div class="row">
												<div class="col-md-4"></div>
												<div class="col-md-4 center">
													<button type="button" class="btn btn-sm fw-bold add" data-toggle="tooltip" data-target="modal" title="Add Achievement" onclick="openAchievementsModal('O')"
														<%if(clusterLab.equalsIgnoreCase("N") && ccmAchievementsList.size()>0) {%>disabled<%} %> >
														ADD OTHERS
													</button>
												</div>
												<div class="col-md-4 right"></div>
											</div>
										</div>				
									<!-- ----------------------------------------------- Others End --------------------------------------------------- -->
										
									<%} else {%>
										<div class="container-fluid mt-3 tabpanes2">
										</div>			
 									<%} %>
 								</div>
 							</div>
 						</div>
       				</div>
       			</div>
       		</div>		
       		
		</div>
	</div>		
	
	<!-- -------------------------------------------------------------- action modal ----------------------------------------------------- -->

	<div class=" modal bd-example-modal-lg" tabindex="-1" role="dialog" id="action_modal">
		<div class="modal-dialog modal-lg modal-dialog-jump" role="document" style="max-width: 950px;">
			<div class="modal-content">
				<div class="modal-header" style="background-color: #FFE0AD; ">
					<div class="row w-100"  >
						<div class="col-md-12" >
							<h5 class="modal-title" id="modal_action_no" style="font-weight:700; color: #A30808;">
								
							</h5>
						</div>
					</div>
					
					 <button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body" align="center">
					<form action="#" method="post" autocomplete="off"  >
						<table style="width: 100%;">
							<tr>
								<td style="width:20%;padding: 5px;border :0;font-weight: bold;"> Action Item :</td>
								<td class="tabledata" style="width:80%;padding: 5px;word-wrap:break-word;border :0;" colspan="3" id="modal_action_item"></td>
							</tr>
							<tr>
								<td style="padding: 5px;border :0;font-weight: bold;" >Assign Date :</td>
								<td style="padding: 5px;border :0;" id="modal_action_date"></td>
								<td style="padding: 5px;border :0;font-weight: bold;" >PDC :</td>
								<td style="padding: 5px;border :0;" id="modal_action_PDC"></td>
							</tr>
							<tr>
								<td style="padding: 5px;border :0;font-weight: bold;" >Assignor :</td>
								<td style="padding: 5px;border :0;" class="tabledata" id="modal_action_assignor"></td>
								<td style="padding: 5px;border :0;font-weight: bold;" >Assignee :</td>
								<td style="padding: 5px;border :0;" class="tabledata" id="modal_action_assignee"></td>
							</tr>
							<tr>
								<td style="padding: 5px;border :0;font-weight: bold;" >Final Progress :</td>
								<td style="padding: 5px;border :0;" id="modal_action_progress"></td>
								<td style="padding: 5px;border :0;font-weight: bold;" > Type :</td>
								<td style="padding: 5px;font-weight: bold;color:#A30808 ;border :0;" id="modal_action_type"></td>
							</tr>
							
						</table>
					</form>
					<hr>
					<form method="post" action="SubSubmit.htm" enctype="multipart/form-data" id="subsubmitform">
						<table class="table table-bordered table-hover table-striped table-condensed " id="" style="width: 100%">
							<thead> 
								<tr style="background-color: #055C9D; color: white;">
									<th style="text-align: center;width:5% !important;">SN</th>
									<th style="text-align: center;width:15% !important;">Progress Date</th>
									<th style="text-align: center;width:15% !important;"> Progress</th>
									<th style="width:60% !important;">Remarks</th>
									<th style="text-align: center;width:5% !important;">Download</th>
								</tr>
							</thead>
							<tbody id="modal_progress_table_body">
								
							</tbody>
						</table>
						
						<hr> 
						<table class="table table-bordered table-hover table-striped table-condensed " id="myTable20" style="margin-top: 15px;">
							<thead>  
								<tr  style="background-color: #055C9D; color: white;">
									<th>As On Date</th>
									<th>Remarks</th>
									<th>Progress %</th>
									<th>Attachment</th>
								</tr>							
								<tr id="Memberrow0">									
									<td width="10%"><input  class="form-control "  data-date-format="dd/mm/yyyy" name="AsOnDate" id="DateCompletion" required="required" readonly="readonly" ></td>
									<td width="20%"><input type="text" name="Remarks" class="form-control item_name" maxlength="500"  required="required" /></td>      
									<td width="10%"><input type="number" name="Progress" class="form-control item_name" max="100"  min="0"  required="required" /></td>								
					         		<td  width="25%"><input type="file" name="FileAttach" id="FileAttach"  class="form-control wrap" aria-describedby="inputGroup-sizing-sm" maxlength="255" onchange="Filevalidation('FileAttach');"  /></td>										
								</tr>
							</thead>
						</table>
						
						<div align="center">
							<input type="submit"  class="btn  btn-sm submit" id="myBtn" onclick="return formsubmit('subsubmitform');" value="SUBMIT"/>
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"  />
				     		<input type="hidden" name="ActionMainId" id="ActionMainId"  /> 
				     		<input type="hidden" name="ActionAssignId" id="ActionAssignId" /> 
				     		<input type="hidden" name="committeeId" value="<%=committeeId %>" /> 
				     		<input type="hidden" name="tabName" value="<%=tabName %>" /> 
				     		<input type="hidden" name="ccmActionFlag" value="Y" /> 
						</div>	
					</form>
				</div>
				
			</div>
		</div>
	</div>

	<!-- -------------------------------------------------------------- action modal end ----------------------------------------------------- -->
	
	<!-- -------------------------------------------------------------- Achievements Modal ----------------------------------------------------- -->
	<div class="modal fade bd-example-modal-lg" id="achievementsModal" tabindex="-1" role="dialog" aria-labelledby="achievementsModal" aria-hidden="true" style="margin-top: 5%;">
		<div class="modal-dialog modal-lg" role="document" style="max-width: 900px;">
			<div class="modal-content">
				<div class="modal-header bg-primary text-light">
		       		<h5 class="modal-title modal-heading"></h5>
			        <button type="button" class="close" style="text-shadow: none!important" data-dismiss="modal" aria-label="Close">
			          <span aria-hidden="true" style="color:red;">&times;</span>
			        </button>
		      	</div>
	    		<div class="modal-body">
	    			<form action="CCMAchievementSubmit.htm" method="post" id="achmntForm" enctype="multipart/form-data">
    					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"  />
    					<input type="hidden" name="scheduleId" value="<%=ccmScheduleId%>">
    					<input type="hidden" name="committeeId" value="<%=committeeId%>">
    					<input type="hidden" name="achievementId" id="achievementId">
    					<input type="hidden" name="topicType" id="topicType">
    					<input type="hidden" name="action" id="achmntsAction">
    					<%if(clusterLab.equalsIgnoreCase("Y")) {%>

    						<div class="row">
    							<div class="col-md-1">
    								<label class="mt-2">Lab: </label>
    							</div>
    							<div class="col-md-3 mb-2 labCodeSelectDiv">
									<select class="form-control selectdee" id="labCode" name="labCode" required style="width: 200px;">
										<option value="0">---Select---</option>
										<%if(clusterLabListFilter!=null && clusterLabListFilter.size()>0) {
											
											for(Object[] obj : clusterLabListFilter) {
												if(obj[3]!=null && obj[3].toString().equalsIgnoreCase("Y")) continue;
										%>
											<option value="<%=obj[2]%>" ><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></option>
										<%} }%>
									</select>
    							</div>
    							<div class="col-md-3 mt-2 labCodeSpanDiv left" style="display: none;">
    								<span id="showLabCode"></span>
    							</div>
    							<div class="col-md-8"></div>
    						</div>
	     					
	    				<%} else{%>
	    					<input type="hidden" id="labCode" name="labCode" value="<%=labcode%>">
	    				<%} %>	
	    				<textarea class="achievement" name="achievement" id="achievement" style="display: none;"></textarea>
		         		
		         		<div id="Editor" class="center"></div>
		         		
		         		<div class="row mt-2">
		 					<div class="col-md-4 left">
								<div class="form-group">
									<label><b> Image : </b></label>
									<button type="submit" class="btn btn-sm attachments" name="attachmentName" id="attachmentName1" value="image" formaction="CCMAchievementsAttachmentDownload.htm" formmethod="get" formnovalidate="formnovalidate" formtarget="_blank" data-toggle="tooltip" data-placement="top" title="Download">
	                      				<i class="fa fa-file-image-o" aria-hidden="true" style="color: #8b22cd;"></i>
	                      			</button>
									<input class="form-control form-control" type="file" name="attachment1" accept="image/png, image/jpeg" required="required" maxlength="3" style="font-size: 15px;">
								</div>
							</div>
							<div class="col-md-4 left">
								<div class="form-group">
									<label><b> Attachment : </b></label>
									<button type="submit" class="btn btn-sm attachments" name="attachmentName" id="attachmentName2" value="pdf" formaction="CCMAchievementsAttachmentDownload.htm" formmethod="get" formnovalidate="formnovalidate" formtarget="_blank" data-toggle="tooltip" data-placement="top" title="Download">
	                      				<i class="fa fa-download"></i>
	                      			</button>
									<input class="form-control form-control" type="file" name="attachment2" accept="application/pdf, application/vnd.ms-excel, .pptx" maxlength="3" style="font-size: 15px;">
								</div>
							</div>
							<div class="col-md-4 left">
								<div class="form-group">
									<label><b> Attachment video : </b></label>
									<button type="submit" class="btn btn-sm attachments" name="attachmentName" id="attachmentName3" value="video" formaction="CCMAchievementsAttachmentDownload.htm" formmethod="get" formnovalidate="formnovalidate" formtarget="_blank" data-toggle="tooltip" data-placement="top" title="Download">
	                      				<i class="fa fa-file-video-o" aria-hidden="true" style="color: #a92525;"></i>
	                      			</button>
									<input class="form-control form-control" type="file" name="attachment3" accept="video/*" maxlength="3" style="font-size: 15px;">
								</div>
							</div>
						</div>
		         		<div class="center mt-2">
		         			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"  />
		         			<button type="submit" class="btn btn-sm submit btn-achmnts" formmethod="post" onclick="submitAchmnts()">Submit</button>
		         		</div>
	         		</form>
	         	</div>
	   		</div>
	 	</div>
	</div>
	<!-- -------------------------------------------------------------- Achievements Modal End ----------------------------------------------------- -->
	
	<!-- -------------------------------------------------------------- CK Editor Modal ----------------------------------------------------- -->
	<div class="modal fade bd-example-modal-lg" id="ckEditorModal" tabindex="-1" role="dialog" aria-labelledby="ckEditorModal" aria-hidden="true" style="margin-top: 5%;">
		<div class="modal-dialog modal-lg" role="document" style="max-width: 900px;">
			<div class="modal-content">
				<div class="modal-header bg-primary text-light">
		        	<h5 class="modal-title" id="ckEditorModalHeading" style="text-transform: capitalize;"></h5>
			        <button type="button" class="close closeEditor" style="text-shadow: none!important" data-dismiss="modal" aria-label="Close">
			          <span aria-hidden="true" style="color:red;">&times;</span>
			        </button>
		      	</div>
     			<div class="modal-body">
	         		<div id="CKEditor" class="center"></div>
	         		<input type="hidden" id="topicId">
	         		<input type="hidden" id="topicBtnId">
	         		<div class="center mt-2">
	         			<button type="button" class="btn btn-primary closeEditor" data-dismiss="modal">Enter</button>
	         		</div>
	         		
	         	</div>
    		</div>
  		</div>
	</div>
	<!-- -------------------------------------------------------------- CK Editor Modal End ----------------------------------------------------- -->
	
<script type="text/javascript">
	$(document).ready(function(){
	
		const tab = document.querySelector('button[name="tabName"][value="<%=tabName%>"]');
		
		if(tab) {
			tab.style.backgroundColor = 'green';
			tab.style.color = 'white';
			tab.style.borderColor = 'green';
		}
		
		$('[data-toggle="tooltip"]').tooltip();
		
		$('.atr-btn').trigger('click');
	});

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
				
				$('#ActionAssignId').val(result[0]);
				$('#ActionMainId').val(result[3]);
				
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
					var progressBar ='<div class="progress" style="background-color:#cdd0cb !important;height: 1.5rem !important;">'; 
					progressBar += 		'<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >';
					progressBar +=		'Not Started'
					progressBar +=		'</div>'; 
					progressBar += '</div>'; 
				}
				else
				{
					var progressBar ='<div class="progress" style="background-color:#cdd0cb !important;height:1.5rem !important; ">'; 
					progressBar += 		'<div class="progress-bar progress-bar-striped" role="progressbar" style=" width: '+InProgress+'%;  " aria-valuemin="0" aria-valuemax="100" >';
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
						htmlStr += '<td class="tabledata" style="text-align: center;" >'+ (v+1) + '</td>';
						htmlStr += '<td class="tabledata" style="text-align: center;" >'+ moment(new Date(result[v][3]) ).format('DD-MM-YYYY') + '</td>';
						htmlStr += '<td class="tabledata" style="text-align: center;" >'+ result[v][2] + ' %</td>';
						htmlStr += '<td class="tabledata" >'+ result[v][4] + '</td>';
						if(result[v][5]=== null)
						{
						htmlStr += '<td class="tabledata" style="text-align: center;">-</td>';
						}
						else
						{
						htmlStr += '<td class="tabledata" style="text-align: center;"><button type="submit" class="btn btn-sm" name="ActionSubId" value="'+ result[v][5] + '" target="blank" formaction="ActionDataAttachDownload.htm" ><i class="fa fa-download"></i></button></td>';
						}
						htmlStr += '</tr>';
					}
				}
				else
				{
					htmlStr += '<tr>';
					htmlStr += '<td colspan="5" style="text-align: center;"> Progress Not Updated </td>';
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
	
	$('#DateCompletion').daterangepicker({
		"singleDatePicker" : true,
		"linkedCalendars" : false,
		"showCustomRangeLabel" : true,
		/* "minDate" :new Date(), */
		"startDate" : new Date(),

		"cancelClass" : "btn-default",
		showDropdowns : true,
		locale : {
			format : 'DD-MM-YYYY'
		}
	});
	
	var fsizeindi=0;
	  function Filevalidation (fileid) 
	    {
	        const fi = $('#'+fileid )[0].files[0].size;							 	
	        const file = Math.round((fi / 1024/1024));
	        if (file >= <%=filesize%>) 
	        {
	        	fsizeindi++;
	        	alert("File too Big, please select a file less than <%=filesize%> mb");
	        	document.getElementById("myBtn").disabled = true;
	        }else{
	        	fsizeindi=0;
	        	document.getElementById("myBtn").disabled = false;
	        }
	    }
	function formsubmit(formid)
	{
		if(fsizeindi>0)
		{
			alert("File too Big, please select a file less than 50mb");
			event.preventDefault();
			return false;
			
		}else
		{
			return confirm('Are You Sure To Submit?');
		}
	}
	
	
	// ATR Buttons handle
	// ATR Buttons handle
	$('.atr-btn').on('click', function () {
	
	    /* $('.atrHeading').show();
	    $('.prevatrHeading').hide(); */
	    
	    $('#prevatrTable').hide();
	    $('#atrTable').show();
	
	    // Toggle active class on the ATR button
	    $('.atr-btn').removeClass('btn-outline-secondary').addClass('btn-secondary');
	    
	    // Reset Pending Points button
	    $('.pending-point-btn').removeClass('btn-info').addClass('btn-outline-info');
	    
	});
	
	$('.pending-point-btn').on('click', function () {
		
		/* $('.atrHeading').hide();
	    $('.prevatrHeading').show(); */
	    
	    $('#atrTable').hide();
	    $('#prevatrTable').show();
	
	    // Toggle active class on the Pending Points button
	    $('.pending-point-btn').removeClass('btn-outline-info').addClass('btn-info');
	    
	    // Reset ATR button
	    $('.atr-btn').removeClass('btn-secondary').addClass('btn-outline-secondary');
	    
	});

	$('#meetingDate').daterangepicker({
	    "singleDatePicker": true,
	    "linkedCalendars": false,
	    "showCustomRangeLabel": true,
	    "startDate": new Date(),
	    "cancelClass": "btn-default",
	    "showDropdowns": true,
	    "drops": "up", // This will make the dropdown open above the input field
	    "locale": {
	        format: 'DD-MM-YYYY'
	    }
	});

	// Validate DMC Schedule
	function validateDMCSchedule(committeeId, committeeMainId) {
		if( committeeId==="0" && committeeMainId==="0"){
			if(confirm('DMC Committee not created yet \n Do you want to create New Committee?')){
				window.open('CommitteeList.htm', '_blank');
			}else{
				event.preventDefault();
			}
		}else if(committeeMainId=="0" && committeeId!="0"){
			if(confirm('DMC Committee not constituted yet \n Do you want to constitute Committee?')){
				window.open('CommitteeList.htm', '_blank');
			}else{
				event.preventDefault();
			}
		}else{
			if(confirm('Are you Sure to Add New Schedule?')){
				$('#dmcScheduleBtn').trigger('click');
			}else {
				event.preventDefault();
			}
		}
	}

	
	/* ---------------------CK Editor Config --------------------------------------------------------------------------------------------------- */

	var editor_config = {
			toolbar : [
					{
						name : 'clipboard',
						items : [ 'Undo', 'Redo' ]
					},
					{
						name : 'basicstyles',
						items : [ 'Bold', 'Italic', 'Underline', 'Strike',
								  'Subscript', 'Superscript' ]
					},

					{
						name : 'paragraph',
						items : [ 'NumberedList', 'BulletedList', '-',
								'Outdent', 'Indent']
					},

					{
						name : 'styles',
						items : [ 'Format', 'Font', 'FontSize' ]
					},
					{
						name : 'colors',
						items : [ 'TextColor', 'BGColor', 'CopyFormatting' ]
					},
					{
						name : 'align',
						items : [ 'JustifyLeft', 'JustifyCenter',
								'JustifyRight', 'JustifyBlock' ]
					}, {
						name : 'document',
						items : [ 'Source' ]
					} ],

			removeButtons : 'Underline,Strike,Subscript,Superscript,Anchor,Styles,Specialchar',

			customConfig : '',

			disallowedContent : 'img{width,height,float}',
			extraAllowedContent : 'img[width,height,align]',

			height : 200,

			contentsCss : [ CKEDITOR.basePath + 'mystyles.css' ],

			bodyClass : 'document-editor',

			format_tags : 'p;h1;h2;h3;pre',

			removeDialogTabs : 'image:advanced;link:advanced',

			stylesSet : [

			{
				name : 'Marker',
				element : 'span',
				attributes : {
					'class' : 'marker'
				}
			}, {
				name : 'Cited Work',
				element : 'cite'
			}, {
				name : 'Inline Quotation',
				element : 'q'
			},

			{
				name : 'Special Container',
				element : 'div',
				styles : {
					padding : '5px 10px',
					background : '#eee',
					border : '1px solid #ccc'
				}
			}, {
				name : 'Compact table',
				element : 'table',
				attributes : {
					cellpadding : '6',
					cellspacing : '0',
					border : '1',
					bordercolor : '#ccc'
				},
				styles : {
					'border-collapse' : 'collapse'
				}
			}, {
				name : 'Borderless Table',
				element : 'table',
				styles : {
					'border-style' : 'hidden',
					'background-color' : '#E6E6FA'
				}
			}, {
				name : 'Square Bulleted List',
				element : 'ul',
				styles : {
					'list-style-type' : 'square'
				}
			}, {
				filebrowserUploadUrl : '/path/to/upload-handler'
			}, ]
		};
	
		CKEDITOR.replace('Editor', editor_config);
		CKEDITOR.replace('CKEditor', editor_config);
	
	/* ---------------------CK Editor Config End --------------------------------------------------------------------------------------------------- */
	
	
	/* --------------------- Open Achievements Modal --------------------------------------------------------------------------------------------------- */
	
	function openAchievementsModal(topicType) {
		$('#achievementsModal').modal('show');
		
		$('.modal-heading').html(topicType==='A'?'Achievements': (topicType==='T'?'Test & Trials':'Others'));
		
		$('#achmntsAction').val('Add');
		$('#achievementId').val('0');
		$('#topicType').val(topicType);
		
		$('.labCodeSelectDiv').show();
		$('.labCodeSpanDiv').hide();
		$('.attachments').hide();
		
		CKEDITOR.instances['Editor'].setData('Enter Details');
	}
	
	function submitAchmnts(){
		var data = CKEDITOR.instances['Editor'].getData();
					
		$('#achievement').val(data);
		var $labCode = $('#labCode').val();
		
		if($labCode!="0") {
			if(window.confirm('Are you Sure to Submit?')) {
				$('#achmntForm').submit()
			}else{
				event.preventDefault();
			}
		}else{
			alert('Please Select Lab Name');
			event.preventDefault();
		}
		
	}
	
	function openAchievementsModalEdit(slno, achievementId, labCode, topicType, image, attachment, video) {
		
		$('#achievementsModal').modal('show');
		
		$('.modal-heading').html(topicType==='A'?'Achievements': (topicType==='T'?'Test & Trials':'Others'));
		
		$('#achievementId').val(achievementId);
		$('#achmntsAction').val('Edit');
		$('#showLabCode').html(labCode);
		$('#topicType').val(topicType);
		
		$('.labCodeSelectDiv').hide();
		$('.labCodeSpanDiv').show();
		
		if(image==null || image=="null" || image==""){
			$('#attachmentName1').hide();
		}else{
			$('#attachmentName1').show();
		}
		
		if(attachment==null || attachment=="null" || attachment==""){
			$('#attachmentName2').hide();		
		}
		else{
			$('#attachmentName2').show();
		}
		
		if(video==null || video=="null" || video==""){
			$('#attachmentName3').hide();
		}else{
			$('#attachmentName3').show();
		}
		
		var html = $('#achievement_'+slno).val();
		CKEDITOR.instances['Editor'].setData(html);
	}
	/* --------------------- Open Achievements Modal End --------------------------------------------------------------------------------------------------- */
	
	/* --------------------- Closure Status Modal --------------------------------------------------------------------------------------------------- */
	function openClosureStatusModal(){
		$('#closureStatusModal').modal('show');
		$('.btn-closurestatus').val('Add');
		$('#ccmClosureId').val('0');
		
	}
	
	function getProjectList(rowId) {
		
		var labCodeClosure = $('#labCodeClosure_'+rowId).val();
		$('#projectId_'+rowId).html('<option disabled="disabled" value="" selected="selected">--Select--</option>');
		
		$.ajax({
			Type:'GET',
			url:'GetProjectListByLabCode.htm',
			datatype:'json',
			data:{
				labCode : labCodeClosure,
			},
			success:function(result){
				var values = JSON.parse(result);
				for (var i = 0; i < values.length; i++) {
                    var data = values[i];
                    var optionValue = data[0];
                    var optionText = data[1] + "(" + data[2]+")"; 
                    var option = $("<option></option>").attr("value", optionValue).text(optionText);
                    $('#projectId_'+rowId).append(option); 
                }
				
				<%-- if(action=='Edit'){
					<% if(roadMap!=null) {%>
						$('#projectId').val('<%=roadMap.getProjectId()%>');
					<%}%>
					$('#projectId').select2();
				} --%>
				
			}
		});
	}
		
	<%if(tabName!=null && tabName.equalsIgnoreCase("Closure Status")) {  %>
		getProjectList('1');
	<%}%> 

	function openClosureStatusModalEdit(ccmclosureid, labcode, projectdet, slno){
		$('#closureStatusModalEdit').modal('show');
		$('#ccmClosureId').val(ccmclosureid);
		$('#recommendationEditBtn_1').html($('#recommendationEditData_'+slno).val());
		$('#recommendationEdit_1').val($('#recommendationEditData_'+slno).val());
		$('#tcrStatusEditBtn_1').html($('#tcrStatusEditData_'+slno).val());
		$('#tcrStatusEdit_1').val($('#tcrStatusEditData_'+slno).val());
		$('#acrStatusEditBtn_1').html($('#acrStatusEditData_'+slno).val());
		$('#acrStatusEdit_1').val($('#acrStatusEditData_'+slno).val());
		$('#activityStatusEditBtn_1').html($('#activityStatusEditData_'+slno).val());
		$('#activityStatusEdit_1').val($('#activityStatusEditData_'+slno).val());
		
		$('#closureStatusEditHeading').html(projectdet+' ('+labcode+')');
	}
	
	/* --------------------------------------- Closure Status Cloning Add  -----------------------------------------------  */
	 
	$('.selectitems').select2();
	
	var closureStatusCount = 1;
	
	$('#closureStatusTable').on('click','.tr_clone_add_closure_status', function(){
		$('.selectitems').select2("destroy");
		$tr = $('.tr_clone_closure_status').last();
		$clone = $tr.clone();
		$tr.after($clone);
		
		++closureStatusCount;
		
		$clone.find('.statusadd.recommendationBtn').attr('id', 'recommendationBtn_'+closureStatusCount).attr("onclick","openEditor('Recommendations', 'recommendation', "+closureStatusCount+')');  
		$clone.find('.statusadd.recommendation').attr('id', 'recommendation_'+closureStatusCount);
		$clone.find('.statusadd.tcrStatusBtn').attr('id', 'tcrStatusBtn_'+closureStatusCount).attr("onclick","openEditor('TCR Status', 'tcrStatus', "+closureStatusCount+')');
		$clone.find('.statusadd.tcrStatus').attr('id', 'tcrStatus_'+closureStatusCount);
		$clone.find('.statusadd.acrStatusBtn').attr('id', 'acrStatusBtn_'+closureStatusCount).attr("onclick","openEditor('ACR Status', 'acrStatus', "+closureStatusCount+')');
		$clone.find('.statusadd.acrStatus').attr('id', 'acrStatus_'+closureStatusCount);
		$clone.find('.statusadd.activityStatusBtn').attr('id', 'activityStatusBtn_'+closureStatusCount).attr("onclick","openEditor('Status of Activities', 'activityStatus', "+closureStatusCount+')');
		$clone.find('.statusadd.activityStatus').attr('id', 'activityStatus_'+closureStatusCount);
		$clone.find('.selectitems.labCodeClosure').attr('id', 'labCodeClosure_'+closureStatusCount).attr("onchange","getProjectList("+closureStatusCount+")");
		$clone.find('.selectitems.projectId').attr('id', 'projectId_'+closureStatusCount);
		
		$clone.find("input").val("");
		$clone.find("textarea").val("");
	    $clone.find(".statusadd.recommendationBtn").html("Enter Recommendations");
	    $clone.find(".statusadd.tcrStatusBtn").html("Enter TCR Status");
	    $clone.find(".statusadd.acrStatusBtn").html("Enter ACR Status");
	    $clone.find(".statusadd.activityStatusBtn").html("Enter Activity Status");
		$('.selectitems').select2();
	    $clone.find('.selectitems').select2('val', '');
	    
	    getProjectList(closureStatusCount);
	});
	
	/* --------------------------------------- Closure Status Cloning Add End -----------------------------------------------  */
	/* --------------------- Closure Status Cloning Removal ----------------------------------------- */
	$("#closureStatusTable").on('click', '.tr_clone_rem_closure_status', function() {
	    var cl = $('.tr_clone_closure_status').length;
	
	    if (cl > 1) {
	        var $tr = $(this).closest('.tr_clone_closure_status');
	        $tr.remove();
	    }
	}); 
	/* --------------------- Closure Status Cloning Removal End ----------------------------------------- */
	
	/* --------------------- Open Editor Modal ---------------------------------------------------------- */
	
	function openEditor(topicName, topicIdName, rowId) {
		
		$('#ckEditorModal').modal('show');
		
		var topicId = topicIdName+'_'+rowId;
		var topicBtnId = topicIdName+'Btn_'+rowId;
		
		$('#topicId').val(topicId);
		$('#topicBtnId').val(topicBtnId);
		$('#ckEditorModalHeading').html(topicName);
		
		var html = $('#'+topicBtnId).html();
		
		CKEDITOR.replace('CKEditor', editor_config);
		CKEDITOR.instances['CKEditor'].setData(html);
	}
	
	$('.closeEditor').click(function(){
		
		var topicId = $('#topicId').val();
		var topicBtnId = $('#topicBtnId').val();
		
		var data = CKEDITOR.instances['CKEditor'].getData();
				
		$('#'+topicId).val(data);
		$('#'+topicBtnId).html(data);

	});
	
	/* --------------------- Open Editor Modal End ---------------------------------------------------------- */
	
	/* --------------------- Closure Status Modal End --------------------------------------------------------------------------------------------------- */
	

	/* --------------------- COG Excel File Upload --------------------------------------------------------------------------------------------------- */
	<%if(tabName!=null && tabName.equalsIgnoreCase("Cash Out go Status")) {  %>
	
	var ccmExcelFile = document.getElementById('ccmExcelFile');

	ccmExcelFile.addEventListener('change', (event) => {
	
		var reader = new FileReader();
	    reader.readAsArrayBuffer(event.target.files[0]);
	
	    reader.onload = function (event){
	    
	    	var data = new Uint8Array(reader.result);
	    	
	    	var work_book = XLSX.read(data, {type:'array'});
	    	
	    	var sheet_name = work_book.SheetNames;
	    	
	    	var sheet_data = XLSX.utils.sheet_to_json(work_book.Sheets[sheet_name[0]],{header:1});
	    	
	
	    	var checkExcel = 0;
	    	table_output = '';
	    	var html="";
	    	var duplicate=[];
	    	
	    	var estimatedCost =[];
	    	var demandNo =[];
	    	var demandDate =[];
	    	if(sheet_data.length > 0){
	    		for(var row = 0; row < sheet_data.length ; row ++){
	    			if(row>0){
	    				table_output += '<tr>'
	    					duplicate.push(sheet_data[row][1])
	    			}
	    			var html="";
	    			
	   				for(var cell =0;cell<=10;cell++){
	   					if(row==0){
	   			 	 	if(cell==0 && "SN"!= sheet_data[row][cell]){checkExcel++;}
	   					if(cell==1 && "Project Code"!= sheet_data[row][cell]){checkExcel++;}
	   					if(cell==2 && "Budget Head"!= sheet_data[row][cell]){checkExcel++;}
	   					if(cell==3 && !sheet_data[row][cell].startsWith("Allotment")){checkExcel++;}
	   					if(cell==4 && !sheet_data[row][cell].startsWith("Expenditure")){checkExcel++;}
	   					if(cell==5 && !sheet_data[row][cell].startsWith("Balance")){checkExcel++;}
	   					if(cell==6 && !sheet_data[row][cell].startsWith("COG Q1")){checkExcel++;}
	   					if(cell==7 && !sheet_data[row][cell].startsWith("COG Q2")){checkExcel++;}
	   					if(cell==8 && !sheet_data[row][cell].startsWith("COG Q3")){checkExcel++;}
	   					if(cell==9 && !sheet_data[row][cell].startsWith("COG Q4")){checkExcel++;}
	   					if(cell==10 && !sheet_data[row][cell].startsWith("Addl(-)/Surr(+)")){checkExcel++;}
	   					}
	   					
	   					if(row>0){
	   						if(cell==0){
    	    					html=html+'<td colspan="1" style="text-align:center">'+sheet_data[row][cell]+'</td>';	
    	    				}else if(cell==1){
    	    					var projectCode = sheet_data[row][cell];
    	    					
    	    					if(projectCode===undefined ||  projectCode.length==0 ){
	    							alert("Project Code can not be blank");
	    							ccmExcelFile.value = '';
	    			    			return;
	    						}
    	    					
    	    					if((projectCode+"").length>50){
	    							alert("Project Code length should be of 50 Characters. Project Code length is too much for "+projectCode);
	    							ccmExcelFile.value = '';
	    			    			return;
	    						}
    	    					
    	    					html=html+'<td colspan="2" style="text-align:center">'+sheet_data[row][cell]+'</td>';
    	    				}else if(cell==2) {
								var budgetHead = sheet_data[row][cell];
    	    					
    	    					if(budgetHead===undefined ||  budgetHead.length==0 ){
	    							alert("Budget Head can not be blank");
	    							ccmExcelFile.value = '';
	    			    			return;
	    						}
    	    					
    	    					if((budgetHead+"").length>25){
	    							alert("Budget Head length should be of 25 Characters. Budget Head length is too much for "+budgetHead);
	    							ccmExcelFile.value = '';
	    			    			return;
	    						}
    	    					
    	    					html=html+'<td colspan="2" style="text-align:center">'+sheet_data[row][cell]+'</td>';
    	    				}
	   						
	   					}
	   					
	   					if(checkExcel>0){
	   		    			alert("Please Download the CCM Cash Out Go Status Excel format and upload it.");
	   		     			ccmExcelFile.value = '';
	   		  				return;
	   		    		}
	   		    	  	
	   				}
	   				
	    		}
	    		
	    		if(table_output.length<1){
   		    	  	alert("No Data available in this Excel Sheet!")
		    		ccmExcelFile.value = '';
		    	} 
	    	}
    	
    	}
	});
	
	<%}%>
	/* --------------------- COG Excel File Upload End --------------------------------------------------------------------------------------------------- */
	

	</script>	
	
	
	<script type="text/javascript">
		// Show modal on button click
		document.getElementById('modalBtn').addEventListener('click', function() {
		    document.getElementById('fixedModal').style.display = 'block';
		});
	
		// Close modal on close button click
		document.getElementById('closeModalBtn').addEventListener('click', function() {
		    document.getElementById('fixedModal').style.display = 'none';
		});
	
		document.addEventListener('DOMContentLoaded', function() {
			document.getElementById('fixedModal').style.display = 'block';
	    });
		
		$( document ).ready(function() {
	 	   setTimeout(() => { 
	 		  document.getElementById('fixedModal').style.display = 'none';
			}, 5000);
	 	});
	</script>
</body>
</html>