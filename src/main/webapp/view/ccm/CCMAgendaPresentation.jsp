<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.fasterxml.jackson.databind.ObjectMapper"%>
<%@page import="com.vts.pfms.ccm.model.CCMAchievements"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.vts.pfms.committee.model.Committee"%>
<%@page import="com.vts.pfms.ccm.model.CCMPresentationSlides"%>
<%@page import="com.vts.pfms.committee.model.CommitteeSchedule"%>
<%@page import="com.vts.pfms.model.LabMaster"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.LocalTime"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="com.vts.pfms.FormatConverter"%>
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

<title>CCM Presentation</title>

<meta charset="ISO-8859-1">

<style type="text/css">
.custom-checkbox {
	transform : scale(1.5);
}
label {
	font-weight: 800;
	font-size: 16px;
}
body { 
   font-family : "Lato", Arial, sans-serif !important;
   overflow-x: hidden;
}

input,select,table,div,label,span,button {
	font-family : "Lato", Arial, sans-serif !important;
	font-size: 1.2rem !important;
}

.content-header {
	background-color: darkblue !important;
}

.logo {
	width: 80px !important;
	margin-left: 5px;
}

.slideNames {
	margin-top: 1rem;
	margin-right: 3rem;
	font-size: 3rem;
}

.refNoHeading {
	margin-top: 1.5rem;
	font-size: 1.5rem !important;
}
.firstpagefontfamily  {
	font-family: 'Muli' !important;
}

.data-table td{
	padding: 10px !important;
}
.data-table tbody{
	font-size: 1.2rem !important;
}
.data-table th{
	font-size: 1.5rem !important;
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
	font-weight: bold !important;
}
</style>

<style type="text/css">
.highcharts-figure,	.highcharts-data-table table {
	min-width: 310px;
	max-width: 800px;
}

.highcharts-data-table table {
    font-family: Verdana, sans-serif;
    border-collapse: collapse;
    border: 1px solid #ebebeb;
    margin: 10px auto;
    text-align: center;
    width: 100%;
    max-width: 500px;
}

.highcharts-data-table caption {
    padding: 1em 0;
    font-size: 1.2em;
    color: #555;
}

.highcharts-data-table th {
    font-weight: 600;
    padding: 0.5em;
}

.highcharts-data-table td,
.highcharts-data-table th,
.highcharts-data-table caption {
    padding: 0.5em;
}

.highcharts-data-table thead tr,
.highcharts-data-table tr:nth-child(even) {
    background: #f8f8f8;
}

.highcharts-data-table tr:hover {
    background: #f1f7ff;
}
</style>

</head>
<body style="background-color: #e7f9ff !important;" class="slides-container" id="slides-container">
	<%
		FormatConverter fc = new FormatConverter();
		SimpleDateFormat sdf = fc.getRegularDateFormat();
		SimpleDateFormat sdf1 = fc.getSqlDateFormat();
		SimpleDateFormat inputFormat = new SimpleDateFormat("ddMMMyyyy",Locale.ENGLISH);
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");	
		    
		DecimalFormat df = new DecimalFormat("####################.##");
	   
		String todayDate = outputFormat.format(new Date()).toString();	
		
		CommitteeSchedule ccmSchedule = (CommitteeSchedule) request.getAttribute("ccmScheduleData");
		CCMPresentationSlides presentationSlide = (CCMPresentationSlides) request.getAttribute("ccmPresentationSlides");
		Long ccmPresSlideId = presentationSlide!=null?presentationSlide.getCCMPresSlideId():0L;
		List<String> slideNames = (List<String>)request.getAttribute("slideNames");
		String slideShow = presentationSlide!=null?presentationSlide.getFreezeStatus(): "N";
		
		List<Object[]> agendaList =  (List<Object[]>) request.getAttribute("agendaList");
		
		LabMaster labInfo = (LabMaster)request.getAttribute("labInfo");
		String lablogo = (String)request.getAttribute("lablogo");
		String drdologo = (String)request.getAttribute("drdologo");
		String clusterLabs = (String)request.getAttribute("clusterLabs");
		String thankYouImg = (String)request.getAttribute("thankYouImg");
		String ccmCommitteeId = (String)request.getAttribute("ccmCommitteeId");
		Long ccmMeetingCount = (Long)request.getAttribute("ccmMeetingCount");
		
		//String previewFlag = (String)request.getParameter("previewFlag");
		//slideShow = previewFlag!=null?previewFlag:slideShow;
	
		 //ArrayList<String> cogLabList = new ArrayList<>();
		 ArrayList<String> csLabList = new ArrayList<>();
		 ArrayList<String> aspLabList = new ArrayList<>();
		 
		 Map<String, Map<String, List<Map<String, Object>>>> cogListForStackGraph = (Map<String, Map<String, List<Map<String, Object>>>>)request.getAttribute("cogListForStackGraph");
		// Convert cogList to JSON format and pass it to the JSP
		 String jsonCogListForStackGraph = new Gson().toJson(cogListForStackGraph);
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
	
	<div id="presentation-slides" class="carousel slide" data-ride="carousel">
		<div class="carousel-inner" align="center">
			<!-- ---------------------------------------- P-1  Div ----------------------------------------------------- -->
			<div class="carousel-item active">
				<div class="content" style="height:93vh !important;">
					<table class="table table-condensed " style="width:100%;height:90vh !important;">
						<tr>
							<td style="width: 50%;border: none;vertical-align: middle;text-align: center;">
								<img class="" style="width: 45rem;" <%if(clusterLabs!=null ){ %> src="data:image/*;base64,<%=clusterLabs%>" alt="Logo"<%}else{ %> alt="Image Not Found" <%} %> > 
							</td>
							<td style="width: 50%;border: none;">
								<div class="mt-2 center">
									<h2 class="firstpagefontfamily" style="color: #145374 !important;">Presentation</h2>
									<h3 class="firstpagefontfamily" style="color: #145374 !important;">for</h3>
									<h2 class="firstpagefontfamily" style="color: #145374 !important;" >Cluster Council Meeting - <%=ccmMeetingCount!=null?StringEscapeUtils.escapeHtml4(ccmMeetingCount.toString()): " - " %> </h2>
								</div>
								<div class="mt-4 center">
									<img class="frontpagelogo" style="width:120px;height: 120px;" <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="Image Not Found" <%} %> > 
								</div>
								<div class="mt-4 center">
									<h4 class="firstpagefontfamily">Meeting ID</h4>
									<h4 class="firstpagefontfamily"><%if(ccmSchedule!=null && ccmSchedule.getMeetingId()!=null) {%> <%=StringEscapeUtils.escapeHtml4(ccmSchedule.getMeetingId())%> <%} else{%>-<%} %></h4>
								</div>
								<div class="mt-4 center" style="width: 100%;display: flex;">
									<div style="width: 50%;">
										<h4 class="firstpagefontfamily">Meeting Date</h4>
										<h4 class="firstpagefontfamily"><%if(ccmSchedule!=null && ccmSchedule.getScheduleDate()!=null) {%> <%=fc.sdfTordf(StringEscapeUtils.escapeHtml4(ccmSchedule.getScheduleDate().toString())) %> <%} else{%>-<%} %></h4>
									</div>
									<div style="width: 50%;">
										<h4 class="firstpagefontfamily">Meeting Time</h4>
										<h4 class="firstpagefontfamily"><%if(ccmSchedule!=null && ccmSchedule.getScheduleStartTime()!=null) {%> <%=StringEscapeUtils.escapeHtml4(ccmSchedule.getScheduleStartTime()) %> <%} else{%>-<%} %></h4>
									</div>
								</div>
								<div class="mt-4 center">
									<h4 class="firstpagefontfamily">Meeting Venue</h4>
									<h4 class="firstpagefontfamily"><%if(ccmSchedule!=null && ccmSchedule.getMeetingVenue()!=null) {%> <%=StringEscapeUtils.escapeHtml4(ccmSchedule.getMeetingVenue()) %> <%} else{%>-<%} %></h4>
								</div>
								<div class="mt-5 center">
									<% if(labInfo!=null){ %>
										<h4 class="firstpagefontfamily"><%if(labInfo.getLabName()!=null){ %><%=StringEscapeUtils.escapeHtml4(labInfo.getLabName())  %><%}else{ %>LAB NAME<%} %></h4>
									<%} %>
									<h4 class="firstpagefontfamily mt-5">Government of India, Ministry of Defence</h4>
									<h4 class="firstpagefontfamily">Defence Research & Development Organization</h4>
									<h4 class="firstpagefontfamily"><%if(labInfo.getLabAddress() !=null){ %><%=StringEscapeUtils.escapeHtml4(labInfo.getLabAddress())  %> , <%=StringEscapeUtils.escapeHtml4(labInfo.getLabCity()) %><%}else{ %>LAB ADDRESS<%} %></h4>
								</div>
							</td>
						</tr>	
					</table>
				</div>
			</div>
			<!-- ----------------------------------------  P-1  Div ----------------------------------------------------- -->
			
			
			<!-- ---------------------------------------- P-2  Div ----------------------------------------------------- -->
			
			<div class="carousel-item">

				<div class="content-header row ">
					
					<div class="col-md-1" >
						<img class="logo" <%if(drdologo!=null ){ %> src="data:image/*;base64,<%=drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					<div class="col-md-2" align="left" style="display: inherit;" >
						<b class="refNoHeading"><%=ccmSchedule.getMeetingId()!=null?StringEscapeUtils.escapeHtml4(ccmSchedule.getMeetingId()): " - " %></b>
					</div>
					<div class="col-md-7">
						<h3 class="slideNames">Agenda</h3>
					</div>
					<div class="col-md-1" align="right"  style="padding-top:19px;" >
						<b style="margin-right: -35px;"><%="" %></b>
					</div>
					<div class="col-md-1">
						<img class="logo" <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					
				</div>
				
				<div class="content" >

					<div class="mt-2 mb-2" style="float: right;">
						<button type="button" class="btn btn-sm submit"data-toggle="tooltip" data-placement="bottom" title="ATR Topics" onclick="openpresentationTopicsModal()" >
							<i class="fa fa-th-list" aria-hidden="true"></i> Topics
						</button>
					</div>
							
								
		         	<table class="table table-bordered table-hover table-condensed data-table" style="margin-top:10px;width:100% ">
		     	      	<thead style="background-color: #4B70F5; color: #ffff !important;">
		            		<tr>
		            			<th>Expand</th>
		                    	<th style="width: 5%;">SN</th>
		                       	<th style="width: 22%;">Agenda Item</th> 
		                       	<th style="width: 24%;">Presenter</th>
		                       	<th style="width: 14%;">Duration</th>
		                       	<th style="width: 14%;">File</th>
		                    </tr>
		              	</thead> 
			            <tbody>
		              		<%//LocalTime starttime = LocalTime.parse(LocalTime.parse(scheduledata[3].toString(),DateTimeFormatter.ofPattern("HH:mm:ss")).format( DateTimeFormatter.ofPattern("HH:mm") ));  %>
		              		
			    			<%
								if(agendaList!=null && agendaList.size()>0) {
									LocalTime starttime = LocalTime.parse(ccmSchedule.getScheduleStartTime());
									int  count=0;
								  	for(Object[] level1: agendaList){
										if(level1[2].toString().equalsIgnoreCase("0")) {
											List<Object[]> agendaList2 = agendaList.stream().filter(e -> level1[0].toString().equalsIgnoreCase(e[2].toString())).collect(Collectors.toList());
											++count;
							%>
								<tr>
									<td class="center" style="width: 3%;">
										<span class="clickable" data-toggle="collapse" id="row<%=count %>" data-target=".row<%=count %>">
											<button type="button" class="btn btn-sm btn-success" id="btn<%=count %>" onclick="ChangeButton('<%=count %>')" data-toggle="tooltip" data-placement="top" title="Expand" <%if(agendaList2.size()<1) {%>disabled<%} %> >
												<i class="fa fa-plus"  id="fa<%=count%>"></i> 
											</button>
										</span>
									</td>
									<td class="center" style="width: 5%;"><%=count %></td>
									
									<td style="width: 35%;"><%=level1[4]!=null?StringEscapeUtils.escapeHtml4(level1[4].toString()): " - " %></td>
									
									<td style="width: 25%;">
										<%if(level1[6]!=null && !level1[6].toString().equalsIgnoreCase("0")) {%>
											<%=level1[9]!=null?StringEscapeUtils.escapeHtml4(level1[9].toString()): " - "  %>
										<%} else {%>
											-
										<%} %>
									</td>
									
									<td class="center" style="width: 10%;">
										<%=starttime.format( DateTimeFormatter.ofPattern("hh:mm a") ) %> - <%=starttime.plusMinutes(Long.parseLong(level1[7].toString())).format( DateTimeFormatter.ofPattern("hh:mm a") )  %>
									</td>
									
									<td class="center">
										<%if(level1[8]!=null && !level1[8].toString().isEmpty()) {%>
											<a class="btn btn-sm" href="CCMScheduleAgendaFileDownload.htm?scheduleAgendaId=<%=level1[0] %>&count=<%=count %>&subCount=0" target="_blank">
												Annex-<%=level1[3]!=null?StringEscapeUtils.escapeHtml4(level1[3].toString()): " - " %>
				               				</a>
										<%} else{%>	
											-
										<%} %>
									</td>
									
								</tr>
		
								<%
								
								if(agendaList2.size()>0) {
									LocalTime substarttime = starttime ;
								%>
				
									<tr class="collapse row<%=count %>" id="rowcollapse<%=count%>" >
										<td colspan="1"></td>
										<td colspan="8">
											<table style="width:100%;" class="table table-bordered table-hover table-condensed  subagendatable" id="subagendatable">
												<tbody>
													<%	int countA=0;
														for(Object[] level2: agendaList2){
																++countA;
													%>
														<tr>
															<%-- <td class="center"><%=level2[3] %></td> --%>
															<td class="center" style="width: 5%;"><%=count+"."+countA %></td>
															<td style="width: 38%;"><%=level2[4]!=null?StringEscapeUtils.escapeHtml4(level2[4].toString()): " - "  %></td>
															
															<td style="width: 27%;">
																<%if(level2[6]!=null && !level2[6].toString().equalsIgnoreCase("0")) {%>
																	<%=level2[9]!=null?StringEscapeUtils.escapeHtml4(level2[9].toString()): " - "  %>
																<%} else {%>
																	-
																<%} %>
															</td>
															
															<td class="center" style="width: 15.25%;">
																<%=substarttime.format( DateTimeFormatter.ofPattern("hh:mm a") ) %> - <%=substarttime.plusMinutes(Long.parseLong(level2[7].toString())).format( DateTimeFormatter.ofPattern("hh:mm a") )  %>
																<%substarttime = substarttime.plusMinutes(Long.parseLong(level2[7].toString())); %>
															</td>
															
															<td class="center" style="width: 14.75%;">
																<%if(level2[8]!=null && !level2[8].toString().isEmpty()) {%>
																	<a class="btn btn-sm" href="CCMScheduleAgendaFileDownload.htm?scheduleAgendaId=<%=level2[0] %>&count=<%=count %>&subCount=<%=countA %>" target="_blank">
																		Annex-<%=count %>-<%=countA %>
										               				</a>
																<%} else{%>	
																	-
																<%} %>
															</td>
														</tr>
														
													<%} %>
												</tbody>
											</table>
										</td>	
									</tr>	
								<%} %>
								<%starttime=starttime.plusMinutes(Long.parseLong(level1[7].toString())); %>
							<%} } }%>
						</tbody>
					</table>
				</div>
				
			</div>
	
			<!-- ----------------------------------------  P-2  Div ----------------------------------------------------- -->
			
			<!-- ---------------------------------------- Presentation of Selected Slides ---------------------------------------------------  -->
			
			<%if(slideNames.contains("ATR")) {
				List<Object[]> ccmActions = (List<Object[]>) request.getAttribute("ccmActions");
			    
			    //if(clusterLab.equalsIgnoreCase("N")){
			    //	ccmActions = ccmActions.stream().filter(e -> e[19].toString().equalsIgnoreCase(labcode)).collect(Collectors.toList());
			    //}
				
				List<String> ccmLatestScheduleMinutesIds = (List<String>)request.getAttribute("ccmLatestScheduleMinutesIds");
				List<String> ccmPreviousScheduleMinutesIds = (List<String>)request.getAttribute("ccmPreviousScheduleMinutesIds");
				
				Map<String, List<Object[]>> ccmActionsToListMap = ccmActions!=null && ccmActions.size()>0?ccmActions.stream()
																  .filter(e -> ccmLatestScheduleMinutesIds.contains(e[18].toString()))
																  .collect(Collectors.groupingBy(array -> array[0] + "", LinkedHashMap::new, Collectors.toList())) : new HashMap<>();
				
				Map<String, List<Object[]>> prevccmActionsToListMap = ccmActions!=null && ccmActions.size()>0?ccmActions.stream()
																	 .filter(e -> !e[9].toString().equalsIgnoreCase("C") && ccmPreviousScheduleMinutesIds.contains(e[18].toString()))
																	 .collect(Collectors.groupingBy(array -> array[0] + "", LinkedHashMap::new, Collectors.toList())) : new HashMap<>();
				
				Committee committee = (Committee) request.getAttribute("ccmCommitteeData");
				CommitteeSchedule atrScheduleData = (CommitteeSchedule) request.getAttribute("atrScheduleData");
				String seqDate = atrScheduleData!=null?atrScheduleData.getScheduleDate().toString().substring(2, 7):"";
				
				Map<Integer,String> mapCCM = (Map<Integer,String>)request.getAttribute("mapCCM");
			
			%>
				<!-- ---------------------------------------- ATR Slide -------------------------------------------------------  -->
				<div class="carousel-item">
	
					<div class="content-header row ">
						
						<div class="col-md-1" >
							<img class="logo" <%if(drdologo!=null ){ %> src="data:image/*;base64,<%=drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						<div class="col-md-2" align="left" style="display: inherit;">
							<b class="refNoHeading"><%=ccmSchedule.getMeetingId()!=null?StringEscapeUtils.escapeHtml4(ccmSchedule.getMeetingId()): " - "  %></b>
						</div>
						<div class="col-md-7">
							<h3 class="slideNames">Action Taken Report of CCM(<%=seqDate!=null?StringEscapeUtils.escapeHtml4(seqDate):" - "  %>)</h3>
						</div>
						<div class="col-md-1" align="right"  style="padding-top:19px;" >
							<b style="margin-right: -35px;"><%="" %></b>
						</div>
						<div class="col-md-1">
							<img class="logo" <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						
					</div>
					
					<div class="content" >
						<div class="container-fluid mt-3 tabpanes1">
 											
							<table class="table table-bordered table-hover table-striped table-condensed data-table" id="atrTable">
								<thead style="background-color: #4B70F5; color: #ffff !important;">
									<%-- <tr style="background-color: #4C3BCF;border-radius: 1rem;">
										<th colspan="6" style="border-radius: 1rem;"> <h5>Action Taken Report of CCM(<%=seqDate %>)</h5></th>
									</tr> --%>
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
														
														<%=committee.getCommitteeShortName()!=null?StringEscapeUtils.escapeHtml4(committee.getCommitteeShortName()).trim().toUpperCase():" - "%>-<%=key%>-(<%=seqDate!=null?StringEscapeUtils.escapeHtml4(seqDate):" - " %>)/<%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()).split("/")[3]:" - " %>
													</button>
													<input type="hidden" id="thisYearNo" value="<%=key%>">
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
								 				
												<span <%if(endPdc.isAfter(today) || endPdc.isEqual(today)) {%>style="color:black;font-weight: bold;" <%} else{%> style="color:maroon ;font-weight:bolder;" <%} %>>
												<%=obj[4]!=null? sdf.format(sdf1.parse(obj[4].toString())):" - "%> 
												</span> 
												<%if(!pdcorg.equals(endPdc)) { %>
												<br> 
												<span <%if(pdcorg.isAfter(today) || pdcorg.isEqual(today)) {%>style="color:black;font-weight: bold;" <%} else{%> style="color:maroon ;font-weight:bolder;" <%} %>>
												<%= obj[3]!=null?sdf.format(sdf1.parse(obj[3].toString())):" - "%> 
												</span>	
												<%} %>
											</td>
											
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
							
									
						</div>	
						
					</div>
				</div>	
				<!-- ---------------------------------------- ATR Slide End ---------------------------------------------------  -->
				
				<!-- ---------------------------------------- Pending Points from Previous CCM Slide -------------------------------------------------------  -->
				<div class="carousel-item">
	
					<div class="content-header row ">
						
						<div class="col-md-1" >
							<img class="logo" <%if(drdologo!=null ){ %> src="data:image/*;base64,<%=drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						<div class="col-md-2" align="left" style="display: inherit;">
							<b class="refNoHeading"><%=ccmSchedule.getMeetingId()!=null?StringEscapeUtils.escapeHtml4(ccmSchedule.getMeetingId()): " - " %></b>
						</div>
						<div class="col-md-7">
							<h3 class="slideNames">Pending Points from Prev CCM</h3>
						</div>
						<div class="col-md-1" align="right"  style="padding-top:19px;" >
							<b style="margin-right: -35px;"><%="" %></b>
						</div>
						<div class="col-md-1">
							<img class="logo" <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						
					</div>
					
					<div class="content" >
						<div class="container-fluid mt-3 tabpanes1">
 											
							
							<table class="table table-bordered table-hover table-striped table-condensed data-table" id="prevatrTable" >
								<thead style="background-color: #4B70F5; color: #ffff !important;">
									<!-- <tr style="background-color: #4C3BCF;border-radius: 1rem;">
										<th colspan="6" style="border-radius: 1rem;"> <h5>Pending Points from Prev CCM</h5></th>
									</tr> -->
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
														
														<%=committee.getCommitteeShortName()!=null?StringEscapeUtils.escapeHtml4(committee.getCommitteeShortName()).trim().toUpperCase():" - "%>-<%=key%>-(<%=(obj[20]!=null?StringEscapeUtils.escapeHtml4(obj[20].toString()).substring(2, 7):" - ")%>)/<%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()).split("/")[3]:" - " %>
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
						
					</div>
				</div>	
				<!-- ---------------------------------------- Pending Points from Previous CCM Slide End ---------------------------------------------------  -->
			<%} %>
			<!-- ---------------------------------------- DMC Slide -------------------------------------------------------  -->
			<%if(slideNames.contains("DMC")) {
				String committeeMainId = (String)request.getAttribute("committeeMainId");
				List<Object[]> dmcActions = (List<Object[]>) request.getAttribute("dmcActions");
				
				//if(clusterLab.equalsIgnoreCase("N")){
				//	dmcActions = dmcActions.stream().filter(e -> e[19].toString().equalsIgnoreCase(labcode)).collect(Collectors.toList());
			    //}
				
				//List<String> dmcLatestScheduleMinutesIds = (List<String>) request.getAttribute("dmcLatestScheduleMinutesIds");
				
				//Map<String, List<Object[]>> dmcActionsToListMap = dmcActions!=null && dmcActions.size()>0? dmcActions.stream()
				//												.filter(e -> latestScheduleMinutesIds.contains(e[18].toString()))
				//												.collect(Collectors.groupingBy(array -> array[0] + "", LinkedHashMap::new, Collectors.toList())) : new HashMap<>();
				//
				Map<String, List<Object[]>> prevdmcActionsToListMap = dmcActions!=null && dmcActions.size()>0?dmcActions.stream()
																	  .filter(e -> !e[9].toString().equalsIgnoreCase("C"))
																	  .collect(Collectors.groupingBy(array -> array[0] + "", LinkedHashMap::new, Collectors.toList())) : new HashMap<>();
				Committee committee = (Committee) request.getAttribute("dmcCommitteeData");
				CommitteeSchedule dmcSchedule = (CommitteeSchedule) request.getAttribute("dmcSchedule");
				String seqDate = dmcSchedule!=null?dmcSchedule.getScheduleDate().toString().substring(2, 7):"";
				
				Map<Integer,String> mapDMC = (Map<Integer,String>)request.getAttribute("mapDMC");
				
			%>
				<div class="carousel-item">
	
					<div class="content-header row ">
						
						<div class="col-md-1" >
							<img class="logo" <%if(drdologo!=null ){ %> src="data:image/*;base64,<%=drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						<div class="col-md-2" align="left" style="display: inherit;">
							<b class="refNoHeading"><%=ccmSchedule.getMeetingId()!=null?StringEscapeUtils.escapeHtml4(ccmSchedule.getMeetingId()): " - " %></b>
						</div>
						<div class="col-md-7">
							<h3 class="slideNames">DMC Actions</h3>
						</div>
						<div class="col-md-1" align="right"  style="padding-top:19px;" >
							<b style="margin-right: -35px;"><%="" %></b>
						</div>
						<div class="col-md-1">
							<img class="logo" <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						
					</div>
					
					<div class="content" >
						<div class="container-fluid mt-3 tabpanes1">
							<table class="table table-bordered table-hover table-striped table-condensed data-table" >
								<thead style="background-color: #4B70F5; color: #ffff !important;">
									<!-- <tr style="background-color: #4C3BCF;border-radius: 1rem;">
										<th colspan="6" style="border-radius: 1rem;"> <h5>DMC Approval</h5></th>
									</tr> -->
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
												<span <%if(endPdc.isAfter(today) || endPdc.isEqual(today)) {%>style="color:black;font-weight: bold;" <%} else{%> style="color:maroon ;font-weight:bolder;" <%} %>>
												<%=obj[4]!=null? sdf.format(sdf1.parse(obj[4].toString())):" - "%> 
												</span>  
												<%if(!pdcorg.equals(endPdc)) { %>
												<br> 
												<span <%if(pdcorg.isAfter(today) || pdcorg.isEqual(today)) {%>style="color:black;font-weight: bold;" <%} else{%> style="color:maroon ;font-weight:bolder;" <%} %>>
												<%= obj[3]!=null?sdf.format(sdf1.parse(obj[3].toString())):" - "%> 
												</span>	
												<%} %>
											</td>
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
					</div>
				</div>		
			<%} %>
			<!-- ---------------------------------------- DMC Slide End ---------------------------------------------------  -->
			<!-- ---------------------------------------- EB Calendar Slide ---------------------------------------------------  -->
			<%if(slideNames.contains("EB Calendar")) { 
				List<Object[]> ebCalendarData = (List<Object[]>) request.getAttribute("ebCalendarData");
				String previousMonth = (String) request.getAttribute("previousMonth");
				String currentMonth = (String) request.getAttribute("currentMonth");
				int year = (int) request.getAttribute("year");
			%>
				<div class="carousel-item">
	
					<div class="content-header row ">
						
						<div class="col-md-1" >
							<img class="logo" <%if(drdologo!=null ){ %> src="data:image/*;base64,<%=drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						<div class="col-md-2" align="left" style="display: inherit;">
							<b class="refNoHeading"><%=ccmSchedule.getMeetingId()!=null?StringEscapeUtils.escapeHtml4(ccmSchedule.getMeetingId()): " - " %></b>
						</div>
						<div class="col-md-7">
							<h3 class="slideNames">EB Calendar</h3>
						</div>
						<div class="col-md-1" align="right"  style="padding-top:19px;" >
							<b style="margin-right: -35px;"><%="" %></b>
						</div>
						<div class="col-md-1">
							<img class="logo" <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						
					</div>
					
					<div class="content" >
						<div class="container-fluid mt-3 tabpanes1">
							<table class="table table-bordered table-hover table-striped table-condensed data-table" style="width: 100%;" >
								<thead style="background-color: #4B70F5; color: #ffff !important;border-radius: 1rem;">
									<!-- <tr style="background-color: #4C3BCF;border-radius: 1rem;">
										<th colspan="6" style="border-radius: 1rem;"> <h5>EB Calendar</h5></th>
									</tr> -->
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
												<%if(obj[3]!=null) {%><%=StringEscapeUtils.escapeHtml4(obj[3].toString()) %><%} %>
												
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
					</div>
				</div>
			<%} %>
			<!-- ---------------------------------------- EB Calendar Slide End ---------------------------------------------------  -->
			<!-- ---------------------------------------- PMRC Calendar Slide ---------------------------------------------------  -->
			<%if(slideNames.contains("PMRC Calendar")) { 
				List<Object[]> pmrcCalendarData = (List<Object[]>) request.getAttribute("pmrcCalendarData");
				String previousMonth = (String) request.getAttribute("previousMonth");
				String currentMonth = (String) request.getAttribute("currentMonth");
				int year = (int) request.getAttribute("year");
			%>
				<div class="carousel-item">
	
					<div class="content-header row ">
						
						<div class="col-md-1" >
							<img class="logo" <%if(drdologo!=null ){ %> src="data:image/*;base64,<%=drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						<div class="col-md-2" align="left" style="display: inherit;">
							<b class="refNoHeading"><%=ccmSchedule.getMeetingId()!=null?StringEscapeUtils.escapeHtml4(ccmSchedule.getMeetingId()): " - " %></b>
						</div>
						<div class="col-md-7">
							<h3 class="slideNames">PMRC Calendar</h3>
						</div>
						<div class="col-md-1" align="right"  style="padding-top:19px;" >
							<b style="margin-right: -35px;"><%="" %></b>
						</div>
						<div class="col-md-1">
							<img class="logo" <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						
					</div>
					
					<div class="content" >
						<div class="container-fluid mt-3 tabpanes1">
							<table class="table table-bordered table-hover table-striped table-condensed data-table" style="width: 100%;" >
								<thead style="background-color: #4B70F5; color: #ffff !important;border-radius: 1rem;">
									<!-- <tr style="background-color: #4C3BCF;border-radius: 1rem;">
										<th colspan="6" style="border-radius: 1rem;"> <h5>PMRC Calendar</h5></th>
									</tr> -->
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
												<%if(obj[3]!=null) {%><%=StringEscapeUtils.escapeHtml4(obj[3].toString()) %><%} %>
												
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
					</div>
				</div>
			<%} %>
			<!-- ---------------------------------------- PMRC Calendar Slide End ---------------------------------------------------  -->
			<!-- ---------------------------------------- ASP Status Slide ---------------------------------------------------  -->
			<%if(slideNames.contains("ASP Status")) { 
				HashMap<String, List<Object[]> > aspList = (HashMap<String, List<Object[]> >) request.getAttribute("aspList");
				
				for (Map.Entry<String, List<Object[]>> entry : aspList.entrySet()) {
				    List<Object[]> aspStatusList = entry.getValue(); 
				    aspLabList.add(entry.getKey());
			%>
				<div class="carousel-item">
	
					<div class="content-header row ">
						
						<div class="col-md-1" >
							<img class="logo" <%if(drdologo!=null ){ %> src="data:image/*;base64,<%=drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						<div class="col-md-2" align="left" style="display: inherit;">
							<b class="refNoHeading"><%=ccmSchedule.getMeetingId()!=null?StringEscapeUtils.escapeHtml4(ccmSchedule.getMeetingId()): " - " %></b>
						</div>
						<div class="col-md-7">
							<h3 class="slideNames">ASP Status - <%=entry.getKey()!=null?StringEscapeUtils.escapeHtml4(entry.getKey()): " - " %></h3>
						</div>
						<div class="col-md-1" align="right"  style="padding-top:19px;" >
							<b style="margin-right: -35px;"><%="" %></b>
						</div>
						<div class="col-md-1">
							<img class="logo" <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						
					</div>
					
					<div class="content" >
						<div class="container-fluid mt-3 tabpanes2">
							<table class="table table-bordered table-hover table-striped table-condensed data-table" style="width: 100%;" >
								<thead style="background-color: #4B70F5; color: #ffff !important;border-radius: 1rem;">
									<!-- <tr style="background-color: #4C3BCF;border-radius: 1rem;">
										<th colspan="9" style="border-radius: 1rem;"> <h5>ASP Status</h5></th>
									</tr> -->
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
											<td><%=obj[27]!=null?StringEscapeUtils.escapeHtml4(obj[27].toString()):"-" %></td>
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
					</div>
				</div>
			<%} }%>
			<!-- ---------------------------------------- ASP Status Slide End ---------------------------------------------------  -->
			<!-- ---------------------------------------- Closure Status Slide ---------------------------------------------------  -->
			<%if(slideNames.contains("Closure Status")) { 
				HashMap<String, List<Object[]> > csList = (HashMap<String, List<Object[]> >) request.getAttribute("closureStatusList");
				
				for (Map.Entry<String, List<Object[]>> entry : csList.entrySet()) {
				    List<Object[]> closureStatusList = entry.getValue(); 
				    csLabList.add(entry.getKey());
			%>
				<div class="carousel-item">
	
					<div class="content-header row ">
						
						<div class="col-md-1" >
							<img class="logo" <%if(drdologo!=null ){ %> src="data:image/*;base64,<%=drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						<div class="col-md-2" align="left" style="display: inherit;">
							<b class="refNoHeading"><%=ccmSchedule.getMeetingId()!=null?StringEscapeUtils.escapeHtml4(ccmSchedule.getMeetingId()): " - " %></b>
						</div>
						<div class="col-md-7">
							<h3 class="slideNames">Closure Status - <%=entry.getKey()!=null?StringEscapeUtils.escapeHtml4(entry.getKey()): " - " %></h3>
						</div>
						<div class="col-md-1" align="right"  style="padding-top:19px;" >
							<b style="margin-right: -35px;"><%="" %></b>
						</div>
						<div class="col-md-1">
							<img class="logo" <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						
					</div>
					
					<div class="content" >
						<div class="container-fluid mt-3 tabpanes1">
							<table class="table table-bordered table-hover table-striped table-condensed data-table" style="width: 100%;" >
								<thead style="background-color: #4B70F5; color: #ffff !important;border-radius: 1rem;">
									<!-- <tr style="background-color: #4C3BCF;border-radius: 1rem;">
										<th colspan="7" style="border-radius: 1rem;"> <h5>Closure Status</h5></th>
									</tr> -->
									<tr>
										<!-- <th>Lab</th> -->
										<th>Project</th>
										<th>DoS / PDC</th>
										<th>Recommendation</th>
										<th>TCR Status</th>
										<th>ACR Status</th>
										<th>Status</th>
									</tr>
								</thead>
								<tbody>
									<%if(closureStatusList!=null && closureStatusList.size()>0) { 
										for(Object[] obj : closureStatusList) {
									%>
										<tr>
											<%-- <td><%=obj[1]!=null?obj[1]:"-" %></td> --%>
											<td>
												<%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()):"-" %> <br>
												Cat&emsp;: <%=obj[5]!=null?StringEscapeUtils.escapeHtml4(obj[5].toString()):"-" %> <br>
												Cost&nbsp;&nbsp; : <%=obj[6]!=null?String.format("%.2f", Double.parseDouble(StringEscapeUtils.escapeHtml4(obj[6].toString()))/10000000):"-" %> (In Cr) <br>
												PD&emsp;&nbsp;: <%=obj[7]!=null?obj[7]:"-" %> 
											</td>
											<td class="center">
												<%=obj[8]!=null?fc.sdfTordf(StringEscapeUtils.escapeHtml4(obj[8].toString())):"-" %> / <br> <%=obj[9]!=null?fc.sdfTordf(StringEscapeUtils.escapeHtml4(obj[9].toString())):"-" %> 
											</td>
											<td><%=obj[10]!=null?StringEscapeUtils.escapeHtml4(obj[10].toString()):"-" %></td>
											<td><%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()):"-" %></td>
											<td><%=obj[12]!=null?StringEscapeUtils.escapeHtml4(obj[12].toString()):"-" %></td>
											<td><%=obj[13]!=null?StringEscapeUtils.escapeHtml4(obj[13].toString()):"-" %></td>
										</tr>
									<%} } else{%>
										<tr>
											<td colspan="7" class="center">No Data Available</td>
										</tr>
									<%} %>	
								</tbody>
							</table>
						</div>
					</div>
				</div>
			<%} }%>
			<!-- ---------------------------------------- Closure Status Slide End ---------------------------------------------------  -->
			<!-- ---------------------------------------- Cash Out Go Status Slide ---------------------------------------------------  -->
			<%if(slideNames.contains("Cash Out Go Status")) { 
				LinkedHashMap<String, List<Object[]> > cogList = (LinkedHashMap<String, List<Object[]> >) request.getAttribute("cashOutGoList");
				int quarter = (int)request.getAttribute("quarter");
				
			%>
				<div class="carousel-item">
	
					<div class="content-header row ">
						
						<div class="col-md-1" >
							<img class="logo" <%if(drdologo!=null ){ %> src="data:image/*;base64,<%=drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						<div class="col-md-2" align="left" style="display: inherit;">
							<b class="refNoHeading"><%=ccmSchedule.getMeetingId()!=null?StringEscapeUtils.escapeHtml4(ccmSchedule.getMeetingId()): " - " %></b>
						</div>
						<div class="col-md-7">
							<h3 class="slideNames">Cash Out Go Status </h3>
						</div>
						<div class="col-md-1" align="right"  style="padding-top:19px;" >
							<b style="margin-right: -35px;"><%="" %></b>
						</div>
						<div class="col-md-1">
							<img class="logo" <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						
					</div>
					
					<div class="content" >
						<div class="container-fluid mt-3 tabpanes2">
							<table class="table table-bordered table-hover table-striped table-condensed data-table" style="width: 100%;" >
								<thead style="background-color: #4B70F5; color: #ffff !important;">
									<tr>
										<td colspan="<%=12-quarter %>" class="right" style="background-color: #ffff;color: black;"> <h5>In Lakhs</h5></td>
									</tr>
									<%-- <tr style="background-color: #4C3BCF;border-radius: 1rem;">
										<th colspan="<%=12-quarter %>" style="border-radius: 1rem;"> <h5>Cash Out Go Status</h5></th>
									</tr> --%>
									
									<tr>
										<th>SN</th>
										<!-- <th>Project</th> -->
										<th>Budget Head</th>
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
									<%
									//Double totalAllotmentGrand = 0.00, totalExpenditureGrand = 0.00, totalBalanceGrand = 0.00, totalCOGQ1Grand = 0.00, totalCOGQ2Grand = 0.00, totalCOGQ3Grand = 0.00, totalCOGQ4Grand = 0.00, totalcogTotalGrand = 0.00, totalAddlGrand = 0.00;
									for (Map.Entry<String, List<Object[]>> entry : cogList.entrySet()) {
									    List<Object[]> cashOutGoList = entry.getValue(); 
									%>
									<tr data-toggle="modal" data-target="#cog<%=entry.getKey() %>Modal" style="cursor: pointer;">
										<td colspan="<%=12-quarter %>" class="center" style="background-color: aliceblue;"><b><%=entry.getKey()!=null?StringEscapeUtils.escapeHtml4(entry.getKey()): " - "%></b></td>
									</tr>
									<%
									
									if(cashOutGoList!=null && cashOutGoList.size()>0) {
										int slno=0; 
										Double allotment = 0.00, expenditure = 0.00, balance = 0.00, cogQ1 = 0.00, cogQ2 = 0.00, cogQ3 = 0.00, cogQ4 = 0.00, cogTotal = 0.00, addl = 0.00,
												totalAllotment = 0.00, totalExpenditure = 0.00, totalBalance = 0.00, totalCOGQ1 = 0.00, totalCOGQ2 = 0.00, totalCOGQ3 = 0.00, totalCOGQ4 = 0.00, totalcogTotal = 0.00, totalAddl = 0.00;
										String budgetHead ="";
										Double lakh = 100000.00;
										String decimalPoint = "%.2f";
										for(Object[] obj : cashOutGoList) {
									%>
										<%if(!budgetHead.equalsIgnoreCase(obj[6]!=null?obj[6].toString():"") || slno==0 ) { %>
										 	<%if(slno!=0) {%>
											 	<tr data-toggle="modal" data-target="#cog<%=entry.getKey() %>Modal" style="cursor: pointer;">
													<td class="right" colspan="2"><b><%=budgetHead!=null?StringEscapeUtils.escapeHtml4(budgetHead): " - "  %> : </b> </td>
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
										 	<%budgetHead = obj[6]!=null?StringEscapeUtils.escapeHtml4(obj[6].toString()):""; %>
											<%-- <tr>
												<td colspan="<%=12-quarter %>" class="center" style="background-color: aliceblue;"><b>Budget Head : <%=budgetHead %></b></td>
											</tr> --%>
										<%
											totalAllotment+=allotment;
											totalExpenditure+=expenditure;
											totalBalance+=balance;
											totalCOGQ1+=cogQ1;
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
											++slno;
										%>
										<%-- <tr>
											<td class="center"><%=slno %></td>
											<td ><%=obj[4] %></td>
											<td><%=obj[6] %></td>
											<td class="right"><%=String.format(decimalPoint, Double.parseDouble(obj[7]!=null?obj[7].toString():"0.00")) %></td>
											<td class="right"><%=String.format(decimalPoint, Double.parseDouble(obj[8]!=null?obj[8].toString():"0.00")) %></td>
											<td class="right"><%=String.format(decimalPoint, Double.parseDouble(obj[9]!=null?obj[9].toString():"0.00")) %></td>
											<%if(quarter<=1) {%>
												<td class="right"><%=String.format(decimalPoint, Double.parseDouble(obj[10]!=null?obj[10].toString():"0.00")) %></td>
											<%} %>
											<%if(quarter<=2) {%>
												<td class="right"><%=String.format(decimalPoint, Double.parseDouble(obj[11]!=null?obj[11].toString():"0.00")) %></td>
											<%} %>
											<%if(quarter<=3) {%>
												<td class="right"><%=String.format(decimalPoint, Double.parseDouble(obj[12]!=null?obj[12].toString():"0.00")) %></td>
											<%} %>
											<%if(quarter<=4) {%>
												<td class="right"><%=String.format(decimalPoint, Double.parseDouble(obj[13]!=null?obj[13].toString():"0.00")) %></td>
											<%} %>
											<td class="right"><b><%=String.format(decimalPoint, Double.parseDouble(obj[16]!=null?obj[16].toString():"0.00")) %></b></td>
											<td class="right"><b><%=String.format(decimalPoint, Double.parseDouble(obj[14]!=null?obj[14].toString():"0.00")) %></b></td>
										</tr> --%>
										<%if(slno==cashOutGoList.size()) { %>
										 	<tr data-toggle="modal" data-target="#cog<%=entry.getKey() %>Modal" style="cursor: pointer;">
												<td class="right" colspan="2"> <b><%=budgetHead!=null?StringEscapeUtils.escapeHtml4(budgetHead): " - "  %> :</b> </td>
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
												totalAllotment+=allotment;
												totalExpenditure+=expenditure;
												totalBalance+=balance;
												totalCOGQ1+=cogQ1;
												totalCOGQ2+=cogQ2;
												totalCOGQ3+=cogQ3;
												totalCOGQ4+=cogQ4;
												totalcogTotal+=cogTotal;
												totalAddl+=addl;
											%>
											<tr data-toggle="modal" data-target="#cog<%=entry.getKey() %>Modal" style="cursor: pointer;">
												<td class="right" colspan="2"> <b> Total Amount :</b> </td>
												<td class="right"><%=String.format(decimalPoint, totalAllotment/lakh) %></td>
												<td class="right"><%=String.format(decimalPoint, totalExpenditure/lakh) %></td>
												<td class="right"><%=String.format(decimalPoint, totalBalance/lakh) %></td>
												<%if(quarter<=1) {%>
													<td class="right"><%=String.format(decimalPoint, totalCOGQ1/lakh) %></td>
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
									<%} %>
								</tbody>	
							</table>
						</div>		
					</div>
				</div>	
				
				<%
				for (Map.Entry<String, List<Object[]>> entry : cogList.entrySet()) {
				    List<Object[]> cashOutGoList = entry.getValue(); 
				    //cogLabList.add(entry.getKey());
				%>
				<div class="modal fade bd-example-modal-lg" id="cog<%=entry.getKey() %>Modal" tabindex="-1" role="dialog" aria-labelledby="cog<%=entry.getKey() %>Modal" aria-hidden="true" style="margin-top: 5%;">
					<div class="modal-dialog modal-lg" role="document" style="max-width: 1600px;">
						<div class="modal-content">
							<div class="modal-header text-light" style="background-color: darkblue;">
					        	<h5 class="modal-title " style="font-size: 2rem;">Cash Out Go Status - <%=entry.getKey() %> </h5>
						        <button type="button" class="close" style="text-shadow: none!important" data-dismiss="modal" aria-label="Close">
						          <span aria-hidden="true" style="color: #f02828;">&times;</span>
						        </button>
					      	</div>
			     			<div class="modal-body">
			     				<div class="container-fluid mt-3 tabpanes2">
			     					<table class="table table-bordered table-hover table-striped table-condensed data-table" style="width: 100%;" >
										<thead style="background-color: #4B70F5; color: #ffff !important;border-radius: 1rem;">
											<tr>
												<td colspan="<%=12-quarter %>" class="right" style="background-color: #ffff;color: black;"> <h5>In Lakhs</h5></td>
											</tr>
											<%-- <tr style="background-color: #4C3BCF;border-radius: 1rem;">
												<th colspan="<%=12-quarter %>" style="border-radius: 1rem;"> <h5>Cash Out Go Status - <%=entry.getKey() %></h5></th>
											</tr> --%>
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
												<%if(!budgetHead.equalsIgnoreCase(obj[6]!=null?obj[6].toString():"") || slno==0 ) { %>
												 	<%if(slno!=0) {%>
													 	<tr>
															<td class="right" colspan="2"><b>Total Amount (<%=budgetHead %>) : </b> </td>
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
												 	<%budgetHead = obj[6]!=null?StringEscapeUtils.escapeHtml4(obj[6].toString()):""; %>
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
													<td ><%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()):" - " %></td>
													<%-- <td><%=obj[6] %></td> --%>
													<td class="right"><%=String.format(decimalPoint, Double.parseDouble(obj[7]!=null?StringEscapeUtils.escapeHtml4(obj[7].toString()):"0.00")/lakh) %></td>
													<td class="right"><%=String.format(decimalPoint, Double.parseDouble(obj[8]!=null?StringEscapeUtils.escapeHtml4(obj[8].toString()):"0.00")/lakh) %></td>
													<td class="right"><%=String.format(decimalPoint, Double.parseDouble(obj[9]!=null?StringEscapeUtils.escapeHtml4(obj[9].toString()):"0.00")/lakh) %></td>
													<%if(quarter<=1) {%>
														<td class="right"><%=String.format(decimalPoint, Double.parseDouble(obj[10]!=null?StringEscapeUtils.escapeHtml4(obj[10].toString()):"0.00")/lakh) %></td>
													<%} %>
													<%if(quarter<=2) {%>
														<td class="right"><%=String.format(decimalPoint, Double.parseDouble(obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()):"0.00")/lakh) %></td>
													<%} %>
													<%if(quarter<=3) {%>
														<td class="right"><%=String.format(decimalPoint, Double.parseDouble(obj[12]!=null?StringEscapeUtils.escapeHtml4(obj[12].toString()):"0.00")/lakh) %></td>
													<%} %>
													<%if(quarter<=4) {%>
														<td class="right"><%=String.format(decimalPoint, Double.parseDouble(obj[13]!=null?StringEscapeUtils.escapeHtml4(obj[13].toString()):"0.00")/lakh) %></td>
													<%} %>
													<td class="right"><b><%=String.format(decimalPoint, Double.parseDouble(obj[16]!=null?StringEscapeUtils.escapeHtml4(obj[16].toString()):"0.00")/lakh) %></b></td>
													<td class="right"><b><%=String.format(decimalPoint, Double.parseDouble(obj[14]!=null?StringEscapeUtils.escapeHtml4(obj[14].toString()):"0.00")/lakh) %></b></td>
												</tr>
												<%if(slno==cashOutGoList.size()) { %>
												 	<tr>
														<td class="right" colspan="2"> <b>Total Amount (<%=budgetHead!=null?StringEscapeUtils.escapeHtml4(budgetHead): " - " %>) :</b> </td>
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
			     			</div>
			     		</div>
			     	</div>
			     </div>
			     <%} %>
			     
				<div class="carousel-item">
		
					<div class="content-header row ">
						
						<div class="col-md-1" >
							<img class="logo" <%if(drdologo!=null ){ %> src="data:image/*;base64,<%=drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						<div class="col-md-2" align="left" style="display: inherit;">
							<b class="refNoHeading"><%=ccmSchedule.getMeetingId()!=null?StringEscapeUtils.escapeHtml4(ccmSchedule.getMeetingId()): " - " %></b>
						</div>
						<div class="col-md-7">
							<h3 class="slideNames">Cash Out Go Status - Graph </h3>
						</div>
						<div class="col-md-1" align="right"  style="padding-top:19px;" >
							<b style="margin-right: -35px;"><%="" %></b>
						</div>
						<div class="col-md-1">
							<img class="logo" <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						
					</div>
					
					<div class="content" >
						<div class="container-fluid mt-3 tabpanes2">
						
							<!-- ------------------------- Stacked, Clustered bar graph Analytics ------------------------------  -->
							<div class="row mt-2 mb-5">
								<div class="col-md-12">
									<div id="stackedgraphcontainer" style="display:block;" ></div>
								</div>
							</div>
							<!-- ------------------------- Stacked, Clustered bar graph Analytics End ------------------------------  -->
						</div>
					</div>
				</div>
				
				<%-- <%
				for (Map.Entry<String, List<Object[]>> entry : cogList.entrySet()) {
				    List<Object[]> cashOutGoList = entry.getValue(); 
				    //cogLabList.add(entry.getKey());
				%>
				<div class="carousel-item">
	
					<div class="content-header row ">
						
						<div class="col-md-1" >
							<img class="logo" <%if(drdologo!=null ){ %> src="data:image/*;base64,<%=drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						<div class="col-md-2" align="left" style="display: inherit;">
							<b class="refNoHeading"><%=ccmSchedule.getMeetingId() %></b>
						</div>
						<div class="col-md-7">
							<h3 class="slideNames">Cash Out Go Status - <%=entry.getKey() %> </h3>
						</div>
						<div class="col-md-1" align="right"  style="padding-top:19px;" >
							<b style="margin-right: -35px;"><%="" %></b>
						</div>
						<div class="col-md-1">
							<img class="logo" <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						
					</div>
					
					<div class="content" >
						<div class="container-fluid mt-3 tabpanes2">
							<table class="table table-bordered table-hover table-striped table-condensed data-table" style="width: 100%;" >
								<thead style="background-color: #4B70F5; color: #ffff !important;border-radius: 1rem;">
									<tr>
										<td colspan="<%=12-quarter %>" class="right" style="background-color: #ffff;color: black;"> <h5>In Lakhs</h5></td>
									</tr>
									<tr style="background-color: #4C3BCF;border-radius: 1rem;">
										<th colspan="<%=12-quarter %>" style="border-radius: 1rem;"> <h5>Cash Out Go Status</h5></th>
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
													<td class="right" colspan="2"><b>Total Amount (<%=budgetHead %>) : </b> </td>
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
										 	<%budgetHead = obj[6].toString(); %>
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
											<td ><%=obj[4] %></td>
											<td><%=obj[6] %></td>
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
					</div>
				</div>
			<%} }%> --%>
			<%} %>
			<!-- ---------------------------------------- Cash Out Go Status Slide End ---------------------------------------------------  -->
			<!-- ---------------------------------------- Test & Trials Slide ---------------------------------------------------  -->
			<%if(slideNames.contains("Test & Trials")) { 
				List<CCMAchievements> ccmTestAndTrialsList = (List<CCMAchievements>) request.getAttribute("ccmTestAndTrialsList");
			%>
				<div class="carousel-item">
	
					<div class="content-header row ">
						
						<div class="col-md-1" >
							<img class="logo" <%if(drdologo!=null ){ %> src="data:image/*;base64,<%=drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						<div class="col-md-2" align="left" style="display: inherit;">
							<b class="refNoHeading"><%=ccmSchedule.getMeetingId()!=null?StringEscapeUtils.escapeHtml4(ccmSchedule.getMeetingId()): " - " %></b>
						</div>
						<div class="col-md-7">
							<h3 class="slideNames">Test & Trials</h3>
						</div>
						<div class="col-md-1" align="right"  style="padding-top:19px;" >
							<b style="margin-right: -35px;"><%="" %></b>
						</div>
						<div class="col-md-1">
							<img class="logo" <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						
					</div>
					
					<div class="content" >
						<div class="container-fluid mt-3 tabpanes1">
							<table class="table table-bordered table-hover table-striped table-condensed data-table" style="width: 100%;" >
								<thead style="background-color: #4B70F5; color: #ffff !important;border-radius: 1rem;">
									<!-- <tr style="background-color: #4C3BCF;border-radius: 1rem;">
										<th colspan="6" style="border-radius: 1rem;"> <h5>Test & Trials</h5></th>
									</tr> -->
									<tr >
										<th style="width: 5%;">SN</th>
										<th style="width: 15%;">Lab</th>
										<th style="width: 70%;">Test & Trials</th>
										<th style="width: 10%;">Action</th>
									</tr>
								</thead>
								<tbody>
									<%if(ccmTestAndTrialsList!=null && ccmTestAndTrialsList.size()>0) {
										int slno=0;
										for(CCMAchievements achmnts : ccmTestAndTrialsList) {
									%>
										<tr>
											<td class="center"><%=++slno %></td>
											<td><%=achmnts.getLabCode()!=null?StringEscapeUtils.escapeHtml4(achmnts.getLabCode()): " - "  %></td>
											<td><%=achmnts.getAchievement()!=null?StringEscapeUtils.escapeHtml4(achmnts.getAchievement()): " - "  %></td>
											<td class="center">
												<form action="#">
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
													<input type="hidden" name="achievementId" value="<%=achmnts.getAchievementId() %>">
													<%if(achmnts.getImageName()!=null) {%>
														<button type="submit" class="btn btn-sm attachments" name="attachmentName" value="image" formaction="CCMAchievementsAttachmentDownload.htm" formmethod="get" formnovalidate="formnovalidate" formtarget="_blank" data-toggle="tooltip" data-placement="top" title="Download">
						                      				<i class="fa fa-file-image-o fa-lg fa-2x" aria-hidden="true" style="color: #8b22cd;"></i>
						                      			</button>
					                      			<%} %>
													<%if(achmnts.getAttachmentName()!=null) {%>
														<button type="submit" class="btn btn-sm attachments" name="attachmentName" value="pdf" formaction="CCMAchievementsAttachmentDownload.htm" formmethod="get" formnovalidate="formnovalidate" formtarget="_blank" data-toggle="tooltip" data-placement="top" title="Download">
						                      				<i class="fa fa-file fa-lg fa-2x" style="color: green;"></i>
						                      			</button>
					                      			<%} %>
													<%if(achmnts.getVideoName()!=null) {%>
														<button type="submit" class="btn btn-sm attachments" name="attachmentName" value="video" formaction="CCMAchievementsAttachmentDownload.htm" formmethod="get" formnovalidate="formnovalidate" formtarget="_blank" data-toggle="tooltip" data-placement="top" title="Download">
						                      				<i class="fa fa-file-video-o fa-lg fa-2x" aria-hidden="true" style="color: #a92525;"></i>
						                      			</button>
					                      			<%} %>
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
					</div>
				</div>
			<%} %>
			<!-- ---------------------------------------- Test & Trials Slide End ---------------------------------------------------  -->
			<!-- ---------------------------------------- Achievements Slide ---------------------------------------------------  -->
			<%if(slideNames.contains("Achievements")) { 
				List<CCMAchievements> ccmAchievementsList = (List<CCMAchievements>) request.getAttribute("ccmAchievementsList");
			%>
				<div class="carousel-item">
	
					<div class="content-header row ">
						
						<div class="col-md-1" >
							<img class="logo" <%if(drdologo!=null ){ %> src="data:image/*;base64,<%=drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						<div class="col-md-2" align="left" style="display: inherit;">
							<b class="refNoHeading"><%=ccmSchedule.getMeetingId()!=null?StringEscapeUtils.escapeHtml4(ccmSchedule.getMeetingId()): " - "  %></b>
						</div>
						<div class="col-md-7">
							<h3 class="slideNames">Achievements</h3>
						</div>
						<div class="col-md-1" align="right"  style="padding-top:19px;" >
							<b style="margin-right: -35px;"><%="" %></b>
						</div>
						<div class="col-md-1">
							<img class="logo" <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						
					</div>
					
					<div class="content" >
						<div class="container-fluid mt-3 tabpanes1">
							<table class="table table-bordered table-hover table-striped table-condensed data-table" style="width: 100%;" >
								<thead style="background-color: #4B70F5; color: #ffff !important;border-radius: 1rem;">
									<!-- <tr style="background-color: #4C3BCF;border-radius: 1rem;">
										<th colspan="6" style="border-radius: 1rem;"> <h5>Achievements</h5></th>
									</tr> -->
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
											<td><%=achmnts.getLabCode()!=null?StringEscapeUtils.escapeHtml4(achmnts.getLabCode()): " - "  %></td>
											<td><%=achmnts.getAchievement()!=null?StringEscapeUtils.escapeHtml4(achmnts.getAchievement()): " - "  %></td>
											<td class="center">
												<form action="#">
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
													<input type="hidden" name="achievementId" value="<%=achmnts.getAchievementId() %>">
													<%if(achmnts.getImageName()!=null) {%>
														<button type="submit" class="btn btn-sm attachments" name="attachmentName" value="image" formaction="CCMAchievementsAttachmentDownload.htm" formmethod="get" formnovalidate="formnovalidate" formtarget="_blank" data-toggle="tooltip" data-placement="top" title="Download">
						                      				<i class="fa fa-file-image-o fa-lg fa-2x" aria-hidden="true" style="color: #8b22cd;"></i>
						                      			</button>
					                      			<%} %>
													<%if(achmnts.getAttachmentName()!=null) {%>
														<button type="submit" class="btn btn-sm attachments" name="attachmentName" value="pdf" formaction="CCMAchievementsAttachmentDownload.htm" formmethod="get" formnovalidate="formnovalidate" formtarget="_blank" data-toggle="tooltip" data-placement="top" title="Download">
						                      				<i class="fa fa-file fa-lg fa-2x" style="color: green;"></i>
						                      			</button>
					                      			<%} %>
													<%if(achmnts.getVideoName()!=null) {%>
														<button type="submit" class="btn btn-sm attachments" name="attachmentName" value="video" formaction="CCMAchievementsAttachmentDownload.htm" formmethod="get" formnovalidate="formnovalidate" formtarget="_blank" data-toggle="tooltip" data-placement="top" title="Download">
						                      				<i class="fa fa-file-video-o fa-lg fa-2x" aria-hidden="true" style="color: #a92525;"></i>
						                      			</button>
					                      			<%} %>
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
					</div>
				</div>
			<%} %>
			<!-- ---------------------------------------- Achievements Slide End ---------------------------------------------------  -->
			<!-- ---------------------------------------- Others Slide ---------------------------------------------------  -->
			<%if(slideNames.contains("Others")) { 
				List<CCMAchievements> ccmOtherssList = (List<CCMAchievements>) request.getAttribute("ccmOtherssList");
			%>
				<div class="carousel-item">
	
					<div class="content-header row ">
						
						<div class="col-md-1" >
							<img class="logo" <%if(drdologo!=null ){ %> src="data:image/*;base64,<%=drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						<div class="col-md-2" align="left" style="display: inherit;">
							<b class="refNoHeading"><%=ccmSchedule.getMeetingId()!=null?StringEscapeUtils.escapeHtml4(ccmSchedule.getMeetingId()): " - "  %></b>
						</div>
						<div class="col-md-7">
							<h3 class="slideNames">Others</h3>
						</div>
						<div class="col-md-1" align="right"  style="padding-top:19px;" >
							<b style="margin-right: -35px;"><%="" %></b>
						</div>
						<div class="col-md-1">
							<img class="logo" <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						
					</div>
					
					<div class="content" >
						<div class="container-fluid mt-3 tabpanes1">
							<table class="table table-bordered table-hover table-striped table-condensed data-table" style="width: 100%;" >
								<thead style="background-color: #4B70F5; color: #ffff !important;border-radius: 1rem;">
									<!-- <tr style="background-color: #4C3BCF;border-radius: 1rem;">
										<th colspan="6" style="border-radius: 1rem;"> <h5>Achievements</h5></th>
									</tr> -->
									<tr >
										<th style="width: 5%;">SN</th>
										<th style="width: 15%;">Lab</th>
										<th style="width: 70%;">Details</th>
										<th style="width: 10%;">Action</th>
									</tr>
								</thead>
								<tbody>
									<%if(ccmOtherssList!=null && ccmOtherssList.size()>0) {
										int slno=0;
										for(CCMAchievements achmnts : ccmOtherssList) {
									%>
										<tr>
											<td class="center"><%=++slno %></td>
											<td><%=achmnts.getLabCode()!=null?StringEscapeUtils.escapeHtml4(achmnts.getLabCode()): " - "  %></td>
											<td><%=achmnts.getAchievement()!=null?StringEscapeUtils.escapeHtml4(achmnts.getAchievement()): " - "  %></td>
											<td class="center">
												<form action="#">
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
													<input type="hidden" name="achievementId" value="<%=achmnts.getAchievementId() %>">
													<%if(achmnts.getImageName()!=null) {%>
														<button type="submit" class="btn btn-sm attachments" name="attachmentName" value="image" formaction="CCMAchievementsAttachmentDownload.htm" formmethod="get" formnovalidate="formnovalidate" formtarget="_blank" data-toggle="tooltip" data-placement="top" title="Download">
						                      				<i class="fa fa-file-image-o fa-lg fa-2x" aria-hidden="true" style="color: #8b22cd;"></i>
						                      			</button>
					                      			<%} %>
													<%if(achmnts.getAttachmentName()!=null) {%>
														<button type="submit" class="btn btn-sm attachments" name="attachmentName" value="pdf" formaction="CCMAchievementsAttachmentDownload.htm" formmethod="get" formnovalidate="formnovalidate" formtarget="_blank" data-toggle="tooltip" data-placement="top" title="Download">
						                      				<i class="fa fa-file fa-lg fa-2x" style="color: green;"></i>
						                      			</button>
					                      			<%} %>
													<%if(achmnts.getVideoName()!=null) {%>
														<button type="submit" class="btn btn-sm attachments" name="attachmentName" value="video" formaction="CCMAchievementsAttachmentDownload.htm" formmethod="get" formnovalidate="formnovalidate" formtarget="_blank" data-toggle="tooltip" data-placement="top" title="Download">
						                      				<i class="fa fa-file-video-o fa-lg fa-2x" aria-hidden="true" style="color: #a92525;"></i>
						                      			</button>
					                      			<%} %>
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
					</div>
				</div>
			<%} %>
			<!-- ---------------------------------------- Others Slide End ---------------------------------------------------  -->
			
			<!-- ---------------------------------------- Thank you Slide End ---------------------------------------------------  -->
			<div class="carousel-item">
	
				<div class="content-header row ">
					
					<div class="col-md-1" >
						<img class="logo" <%if(drdologo!=null ){ %> src="data:image/*;base64,<%=drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					<div class="col-md-2" align="left" style="display: inherit;">
						<b class="refNoHeading"><%=ccmSchedule.getMeetingId()!=null?StringEscapeUtils.escapeHtml4(ccmSchedule.getMeetingId()): " - "  %></b>
					</div>
					<div class="col-md-7">
						<h3 class="slideNames">Thank You</h3>
					</div>
					<div class="col-md-1" align="right"  style="padding-top:19px;" >
						<b style="margin-right: -35px;"><%="" %></b>
					</div>
					<div class="col-md-1">
						<img class="logo" <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					
				</div>
				
				<div class="content" >
					<img class="" style="width: 100%; height: 100%;" <%if(thankYouImg!=null ){ %> src="data:image/*;base64,<%=thankYouImg%>" alt="Logo"<%}else{ %> alt="Image Not Found" <%} %> > 
				</div>
			</div>
			<!-- ---------------------------------------- Thank you Slide End ---------------------------------------------------  -->
			
			<!-- ---------------------------------------- Presentation of Selected Slides End ------------------------------------------------------------------------------------  -->
			
		</div>

		
		<a class="carousel-control-prev" href="#presentation-slides" role="button" data-slide="prev" style="width: 0%; padding-left: 20px;"> <span aria-hidden="true">
			<i class="fa fa-chevron-left fa-2x" style="color: #000000" aria-hidden="true"></i></span> <span class="sr-only">Previous</span>
		</a> <a class="carousel-control-next" href="#presentation-slides" role="button" data-slide="next" style="width: 0%; padding-right: 20px;"> <span aria-hidden="true">
			<i class="fa fa-chevron-right fa-2x" style="color: #000000" aria-hidden="true"></i></span> <span class="sr-only">Next</span>
		</a>

		<ol class="carousel-indicators mt-2">
			<li data-target="#presentation-slides" data-slide-to="0" class="carousel-indicator active" data-toggle="tooltip" data-placement="top" title="Start"><b><i class="fa fa-home" aria-hidden="true"></i></b></li>
			<li data-target="#presentation-slides" data-slide-to="1" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="Agenda"><b>1</b></li>
			<% int count = 1;
				int count2 = 1;
			if(slideNames.size()>0) {
				
				for(String slideName : slideNames) {%>
				
				<%if(slideName.equalsIgnoreCase("ATR")) {%>
					<li data-target="#presentation-slides" data-slide-to="<%=++count %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="<%=slideName %>"><b><%=++count2 %></b></li>
					<li data-target="#presentation-slides" data-slide-to="<%=++count %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="Pending Points"><b><%=++count2 %></b></li>
				<%} else if(slideName.equalsIgnoreCase("ASP Status")) {%>
					<%
					++count2;
					char a = 'a';
					for(int i=0;i<aspLabList.size();i++) {%>
						<li data-target="#presentation-slides" data-slide-to="<%=++count %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="<%=slideName+" - "+aspLabList.get(i) %>"><b><%=count2 %> (<%=a++ %>)</b></li>
					<%} %>
				<%} else if(slideName.equalsIgnoreCase("Closure Status")) {%>
					<%
					++count2;
					char a = 'a';
					for(int i=0;i<csLabList.size();i++) {%>
						<li data-target="#presentation-slides" data-slide-to="<%=++count %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="<%=slideName+" - "+csLabList.get(i) %>"><b><%=count2 %> (<%=a++ %>)</b></li>
					<%} %>
				<%} else if(slideName.equalsIgnoreCase("Cash Out Go Status")) {
					++count2;
				%>
					<li data-target="#presentation-slides" data-slide-to="<%=++count %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="<%=slideName %>"><b><%=count2 %></b></li>
					<%-- <%
					char a = 'a';
					for(int i=0;i<cogLabList.size();i++) {%>
						<li data-target="#presentation-slides" data-slide-to="<%=++count %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="<%=slideName+" - "+cogLabList.get(i) %>"><b><%=count2 %> (<%=a++ %>)</b></li>
					<%} %> --%>
					<%
					//char a = 'a';
					//for(int i=0;i<cogLabList.size();i++) {%>
						<li data-target="#presentation-slides" data-slide-to="<%=++count %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="<%=slideName+" Graph " %>"><b><%=count2 %> (a)</b></li>
					<%//} %>
				<%} else{%>
					<li data-target="#presentation-slides" data-slide-to="<%=++count %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="<%=slideName %>"><b><%=++count2 %></b></li>
				<%} %>
			<%} }%>
			<li data-target="#presentation-slides" data-slide-to="<%=++count %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="End"><b>End</b></li>
			<li data-slide-to="<%=count-1 %>" style="background-color:  #000000;width: 35px;margin-left: 20px;" class="carousel-indicator content_full_screen" data-toggle="tooltip" data-placement="top" title="Full Screen Mode"><b><i class="fa fa-expand fa-lg" aria-hidden="true"></i></b></li>
			<li data-slide-to="<%=count-1 %>" style="background-color:  #000000;width: 35px;margin-left: 20px;" class="carousel-indicator content_reg_screen" data-toggle="tooltip" data-placement="top" title="Exit Full Screen Mode"><b><i class="fa fa-compress fa-lg" aria-hidden="true"></i></b></li>
			<li style="background-color:  white;width: 55px;margin-left: 20px;">
				<a target="_blank" href="CCMPresentationDownload.htm?ccmScheduleId=<%=ccmSchedule.getScheduleId() %>&committeeId=<%=ccmCommitteeId %>" data-toggle="tooltip" title="Download CCM Presentation" >
					<i class="fa fa-download" style="color: green;font-size: 1.2rem;padding: 0.1rem;" aria-hidden="true"></i>
				</a>	
			</li>
		</ol>
	</div>

	<!-- ---------------------------------------------------------------- Presentation Topics Modal ---------------------------------------------------------------------------------------------- -->
	<div class="modal fade bd-example-modal-lg" id="presentationTopicsModal" tabindex="-1" role="dialog" aria-labelledby="presentationTopicsModal" aria-hidden="true" style="margin-top: 5%;">
		<div class="modal-dialog modal-lg" role="document" style="max-width: 900px;">
			<div class="modal-content">
				<div class="modal-header bg-primary text-light">
		        	<h5 class="modal-title">Topics</h5>
			        <button type="button" class="close" style="text-shadow: none!important" data-dismiss="modal" aria-label="Close">
			          <span aria-hidden="true" style="color:red;">&times;</span>
			        </button>
		      	</div>
     			<div class="modal-body">
     				<form action="CCMPresentationSlidesSubmit.htm" method="post" id="presentationTopicsForm">
     					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"  />
     					<input type="hidden" name="ccmScheduleId" value="<%=ccmSchedule.getScheduleId()%>">
		         		<input type="hidden" name="ccmPresSlideId" value="<%=ccmPresSlideId%>">
		         		<input type="hidden" name="committeeId" value="<%=ccmCommitteeId%>">
		         		<div class="form-group">
			         		<div class="row ml-3 mr-3">
			         			<div class="col-md-4">
			         				<input type="checkbox" class=" custom-checkbox selectall" <%if(presentationSlide==null || (slideNames!=null && slideNames.size()==10)) {%>checked<%} %> >
			         				&nbsp;&nbsp;&nbsp;<label>Select All</label>
			         			</div>
			         			<div class="col-md-4">
			         				
			         			</div>
			         			<div class="col-md-4">
			         				
			         			</div>
			         		</div>
			         	</div>	
		         		
		         		<hr>
		         		
		         		<div class="form-group">
			         		<div class="row ml-3 mr-3">
			         			<div class="col-md-4">
			         				<input type="checkbox" class=" custom-checkbox slides" name="topicName" value="ATR" <%if(slideNames.contains("ATR")) {%>checked="checked"<%} %> >
			         				&nbsp;&nbsp;&nbsp;<label>Action Taken Report</label>
			         			</div>
			         			<div class="col-md-4">
			         				<input type="checkbox" class=" custom-checkbox slides" name="topicName" value="DMC" <%if(slideNames.contains("DMC")) {%>checked="checked"<%} %> >
			         				&nbsp;&nbsp;&nbsp;<label>DMC</label>
			         			</div>
			         			<div class="col-md-4">
			         				<input type="checkbox" class=" custom-checkbox slides" name="topicName" value="EB Calendar" <%if(slideNames.contains("EB Calendar")) {%>checked="checked"<%} %> >
			         				&nbsp;&nbsp;&nbsp;<label>EB Calendar</label>
			         			</div>
			         		</div>
			         	</div>	
			         	
		         		<div class="form-group">
			         		<div class="row ml-3 mr-3">
			         			<div class="col-md-4">
			         				<input type="checkbox" class=" custom-checkbox slides" name="topicName" value="PMRC Calendar" <%if(slideNames.contains("PMRC Calendar")) {%>checked="checked"<%} %> >
			         				&nbsp;&nbsp;&nbsp;<label>PMRC Calendar</label>
			         			</div>
			         			<div class="col-md-4">
			         				<input type="checkbox" class=" custom-checkbox slides" name="topicName" value="ASP Status" <%if(slideNames.contains("ASP Status")) {%>checked="checked"<%} %> >
			         				&nbsp;&nbsp;&nbsp;<label>ASP Status</label>
			         			</div>
			         			<div class="col-md-4">
			         				<input type="checkbox" class=" custom-checkbox slides" name="topicName" value="Closure Status" <%if(slideNames.contains("Closure Status")) {%>checked="checked"<%} %> >
			         				&nbsp;&nbsp;&nbsp;<label>Closure Status</label>
			         			</div>
			         		</div>
			         	</div>	
			         	
		         		<div class="form-group">
			         		<div class="row ml-3 mr-3">
			         			<div class="col-md-4">
			         				<input type="checkbox" class=" custom-checkbox slides" name="topicName" value="Cash Out Go Status" <%if(slideNames.contains("Cash Out Go Status")) {%>checked="checked"<%} %> >
			         				&nbsp;&nbsp;&nbsp;<label>Cash Out Go Status</label>
			         			</div>
			         			<div class="col-md-4">
			         				<input type="checkbox" class=" custom-checkbox slides" name="topicName" value="Test & Trials" <%if(slideNames.contains("Test & Trials")) {%>checked="checked"<%} %> >
			         				&nbsp;&nbsp;&nbsp;<label>Test & Trials</label>
			         			</div>
			         			<div class="col-md-4">
			         				<input type="checkbox" class=" custom-checkbox slides" name="topicName" value="Achievements" <%if(slideNames.contains("Achievements")) {%>checked="checked"<%} %> >
			         				&nbsp;&nbsp;&nbsp;<label>Achievements</label>
			         			</div>
			         		</div>
			         	</div>	
			         	
		         		<div class="form-group">
			         		<div class="row ml-3 mr-3">
			         			<div class="col-md-4">
			         				<input type="checkbox" class=" custom-checkbox slides" name="topicName" value="Others" <%if(slideNames.contains("Others")) {%>checked="checked"<%} %> >
			         				&nbsp;&nbsp;&nbsp;<label>Others</label>
			         			</div>
			         			<div class="col-md-4">
			         				
			         			</div>
			         			<div class="col-md-4">
			         				
			         			</div>
			         		</div>
			         	</div>	
			         	
		         		<div class="center mt-2">
		         			<button type="submit" name="action" value="Save" class="btn btn-sm submit btn-presentationTopics" onclick="return checkSlidesSelection()">Save</button>
		         			<button type="submit" name="action" value="Save & Freeze" class="btn btn-sm add btn-presentationTopics" onclick="return checkSlidesSelection()">Save & Freeze</button>
		         		</div>
	         		</form>
	         	</div>
    		</div>
  		</div>
	</div>
	<!-- ---------------------------------------------------------------- Presentation Topics Modal End ---------------------------------------------------------------------------------------------- -->
	
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
						
					</form>
				</div>
				
			</div>
		</div>
	</div>

	<!-- -------------------------------------------------------------- action modal end ----------------------------------------------------- -->
	
<script type="text/javascript">

window.setTimeout(function() {
    $(".alert").fadeTo(500, 0).slideUp(500, function(){
        $(this).remove(); 
    });
}, 4000);

$('.carousel').carousel({
	  interval: false,
	  keyboard: true,
	})

$(function () {
	$('[data-toggle="tooltip"]').tooltip()
})

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

/* --------------------- Expand Button Handle for Agenda List--------------------------- */
	function ChangeButton(id) {
		  
		if($("#btn"+id ).hasClass( "btn btn-sm btn-success" ).toString()=='true'){
		$( "#btn"+id ).removeClass( "btn btn-sm btn-success" ).addClass( "btn btn-sm btn-danger" );
		$( "#fa"+id ).removeClass( "fa fa-plus" ).addClass( "fa fa-minus" );
		$( ".row"+id).show();
	    }else{
		$( "#btn"+id ).removeClass( "btn btn-sm btn-danger" ).addClass( "btn btn-sm btn-success" );
		$( "#fa"+id ).removeClass( "fa fa-minus" ).addClass( "fa fa-plus" );
		$( ".row"+id).hide();
	    }
	}	
/* --------------------- Expand Button Handle for Agenda List End --------------------------- */

/* -----------------------  Presentation Modal ---------------------------------------------------------------------------------- */

function openpresentationTopicsModal() {
	$('#presentationTopicsModal').modal('show');
}

/* -------------------- Check Box Handling ---------------------------------- */

<%if(presentationSlide==null) {%>
$( document ).ready(function() {
	var isChecked = $('.selectall').prop('checked');
	$('.slides').prop('checked', isChecked);
});
<%} %>
$('.selectall').on('click', function(){
	var isChecked = $(this).prop('checked');
	$('.slides').prop('checked', isChecked);
});

$(".slides").click(function() {
    
       var allChecked = true;
       $('.slides').each(function() {
           if (!$(this).prop('checked')) {
        	   allChecked = false;
           }
       });
       
       if(allChecked)  $('.selectall').prop('checked', true);
       else $('.selectall').prop('checked', false);
   
});

/* -------------------- Check Box Handling End ---------------------------------- */

/* -------------------- Validations ---------------------------------- */

function checkSlidesSelection(){
	var slidesLength = $('.slides:checked').length;

	if(slidesLength<=0){
		alert('Please Select atleast one Topic');
		event.preventDefault();
		return false;
	}else {
		return confirm('Are you Sure to Preview?');
	}
}
 /* -------------------- Validations End ---------------------------------- */

/* ----------------------- Presentation Modal End ---------------------------------------------------------------------------------- */

</script>

<!-- -------------------------------------------------------------- Handling of Selected Project Slides ------------------------------------------- -->
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
</script>
<!-- -------------------------------------------------------------- Handling of Selected Project Slides End------------------------------------------- -->



<script type="text/javascript">
 

/* ----------------------------- Stacked, Clustered bar graph Analytics End (In Bar Graph) (Project) ------------------------------- */

// Fetch the dynamic chart data from the backend
const chartData = JSON.parse('<%=jsonCogListForStackGraph%>');

console.log(chartData); // Log the fetched data for debugging

// Create the series data dynamically
const seriesData = [];
const categories = []; // Lab categories

// Define the desired order of categories
const categoryOrder = ['Revenue', 'Capital', 'Miscellaneous'];

// Iterate over the chartData to build Highcharts series structure
for (const lab in chartData) {
    // Add lab name to categories array for the X-axis
    categories.push(lab);

    // Iterate through the predefined category order
    categoryOrder.forEach(category => {
        // Check if the category exists in the current lab's data
        if (chartData[lab][category]) {
            chartData[lab][category].forEach((projectData) => {
                // Create a series for this project under the corresponding category
                let series = seriesData.find(s => s.name === projectData.project && s.stack === category);
                
                // If series doesn't exist, create it
                if (!series) {
                    series = {
                        name: projectData.project,
                        data: [],
                        stack: category // Set the stack to the current category
                    };
                    seriesData.push(series);
                }

                // Fill the data array with amounts for each lab
                const labIndex = categories.indexOf(lab);
                series.data[labIndex] = (series.data[labIndex] || 0) + (projectData.amount || 0); // Assign amount or 0 if null
            });
        }
    });
}

// Fill missing data points with 0 to ensure correct stacking
seriesData.forEach(series => {
    for (let i = 0; i < categories.length; i++) {
        if (typeof series.data[i] === 'undefined') {
            series.data[i] = 0;
        }
    }
});

//Initialize the Highcharts chart
Highcharts.chart('stackedgraphcontainer', {
    chart: {
        type: 'column'
    },
    title: {
        text: 'Stacked, Clustered Cash Out Go Status',
       	style: {
            fontSize: '20px',
            fontWeight: 'bold',
        },
    },
    xAxis: {
        categories: categories,
        labels: {
            style: {
                fontSize: '14px',
                fontWeight: 'bold',
                color: 'black',
            }
        },
    },
    yAxis: {
        min: 0,
        title: {
            text: 'Actual Cost (Rs)',
            style: {
                fontSize: '14px',
                fontWeight: 'bold',
                color: 'black',
            },
        },
        labels: {
            enabled: false // Disable the y-axis labels
        },
        
    },
    legend: {
        enabled: false // This will hide the project names at the bottom
    },
    plotOptions: {
        series: {
            stacking: 'normal'
        },style: {
            fontSize: '14px',
            fontWeight: 'bold'
        },
    },
    series: seriesData,
    tooltip: {
        formatter: function () {
            return '<b>' + this.x + '</b><br/>' +  // Lab name (category)
                   'Project: ' + this.series.name + '<br/>' +  // Project name
                   'Amount: ' + this.y + '<br/>' +  // Amount (y-value)
                   'Budget Head: ' + this.series.stackKey.split(',')[1] + '<br/>' + // Stack category
                   'Total: ' + this.point.stackTotal;  // Total stacked value
        }
    },
});


/* ----------------------------- Stacked, Clustered bar graph Analytics End ------------------------------- */
</script>
</body>
</html>