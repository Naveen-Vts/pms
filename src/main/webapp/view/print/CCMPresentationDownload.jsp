<%@page import="java.util.Locale"%>
<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
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
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>CCM Presentation</title>

<%
	CommitteeSchedule ccmSchedule = (CommitteeSchedule) request.getAttribute("ccmScheduleData");
	Long ccmMeetingCount = (Long)request.getAttribute("ccmMeetingCount");
%>
<style type="text/css">
#pageborder {
      position:fixed;
      left: 0;
      right: 0;
      top: 0;
      bottom: 0;
      border: 2px solid black;
}  
@page {             
      size: 1220px 850px; 
      margin-top: 50px;
      margin-left: 40px;
      margin-right: 40px;
      margin-buttom: 50px; 	
      border: 1px solid black;
      
      @bottom-left {          		
	      content : "The information in this Document is proprietary of DRDO , MOD Government of India. Unauthorized possession/use is violating the Government procedure which may be liable for prosecution. ";
	      margin-bottom: 30px;
	      margin-right: 5px;
	      font-size: 10px;
      }
      @bottom-right {          		
	      content: "Page " counter(page) " of " counter(pages);
	      margin-bottom: 30px;
	      margin-right: 10px;
      }
      @top-left {          		
	      content: "<%=ccmSchedule.getMeetingId()!=null?ccmSchedule.getMeetingId(): " - "%>";
	      margin-left: 80px;
	      margin-top: 30px;
      }
      @top-center {          		
	      content: "CCM #<%=ccmMeetingCount %>";
	       margin-top: 30px;
      }
      @top-right {          		
	      content: "Restricted";
	      margin-top: 30px;
	      margin-right: 50px;
      }
          
          
 }
 .border
 {
 	border: 1px solid black;
 }


p{
  text-align: justify;
  text-justify: inter-word;
}

.break
{
	page-break-after: always;
	margin: 25px 0px 25px 0px;
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
.firstpagefontfamily  {
	font-family: 'Muli' !important;
}

.data-table{
	margin-left : 10px;
	margin-right : 10px;
	margin-top : 10px;
	border-collapse : collapse;
	border : 1px solid black;
	width : 100%;
}  

.data-table tbody{
	font-size: 1rem;
}

.data-table th{
	text-align : center;
	font-size: 1.1rem;
	padding: 7px;
}
.data-table td{
	padding: 5px;
}

.data-table td,th{
	border : 1px solid black;
	padding : 7px;
}

.subagendatable{
	border-collapse : collapse;
	border : 1px solid black;
	width : 100%;
}  

.subagendatable th{
	text-align : center;
}

.subagendatable td,th{
	border : 1px solid black;
}

.heading {
	font-size: 1.5rem !important;
} 

input,select,table,div,label,span,button {
	font-family : "Lato", Arial, sans-serif !important;
	font-size: 1rem ;
} 
</style>

</head>
<body>
	<%
		FormatConverter fc = new FormatConverter();
		SimpleDateFormat sdf = fc.getRegularDateFormat();
		SimpleDateFormat sdf1 = fc.getSqlDateFormat();
		SimpleDateFormat inputFormat = new SimpleDateFormat("ddMMMyyyy",Locale.ENGLISH);
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");	
		    
		DecimalFormat df = new DecimalFormat("####################.##");
	   
		String todayDate = outputFormat.format(new Date()).toString();	
		
		CCMPresentationSlides presentationSlide = (CCMPresentationSlides) request.getAttribute("ccmPresentationSlides");
		Long ccmPresSlideId = presentationSlide!=null?presentationSlide.getCCMPresSlideId():0L;
		List<String> slideNames = presentationSlide!=null? Arrays.asList(presentationSlide.getSlideName().split(",")): new ArrayList<String>();
		String slideShow = presentationSlide!=null?presentationSlide.getFreezeStatus(): "N";
		
		List<Object[]> agendaList =  (List<Object[]>) request.getAttribute("agendaList");
		
		LabMaster labInfo = (LabMaster)request.getAttribute("labInfo");
		String lablogo = (String)request.getAttribute("lablogo");
		String drdologo = (String)request.getAttribute("drdologo");
		String clusterLabs = (String)request.getAttribute("clusterLabs");
		String thankYouImg = (String)request.getAttribute("thankYouImg");
		String ccmCommitteeId = (String)request.getAttribute("ccmCommitteeId");
		
	%>
	<div>
		
 		<!-- ---------------------------------------- Page-1 --------------------------------------- -->
		<table class="" style="width:100%;">
			<tr>
				<td style="width: 50%;border: none;vertical-align: middle;text-align: center;">
					<img class="" style="width: 500px;height: 500px;" <%if(clusterLabs!=null ){ %> src="data:image/*;base64,<%=clusterLabs%>" alt="Logo"<%}else{ %> alt="Image Not Found" <%} %> >
				</td>
				<td style="width: 50%;border: none;vertical-align: middle;text-align: center;">
					<div class="center" style="line-height: 0.5;margin-top: 50px;">
						<h2 class="firstpagefontfamily" style="color: #145374 !important;">Presentation</h2>
						<h3 class="firstpagefontfamily" style="color: #145374 !important;">for</h3>
						<h2 class="firstpagefontfamily" style="color: #145374 !important;" >Cluster Council Meeting - <%=ccmMeetingCount %> </h2>
					</div>
					<div class="center">
						<img class="frontpagelogo" style="width: 80px;height: 80px;" <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="Image Not Found" <%} %> > 
					</div>
					<div class="center">
						<h4 class="firstpagefontfamily">Meeting ID</h4>
						<h4 class="firstpagefontfamily"><%if(ccmSchedule!=null && ccmSchedule.getMeetingId()!=null) {%> <%=ccmSchedule.getMeetingId() %> <%} else{%>-<%} %></h4>
					</div>
					<div class="center" style="width: 100%;display: inline">
						<div style="width: 50%;float: left;">
							<h4 class="firstpagefontfamily">Meeting Date</h4>
							<h4 class="firstpagefontfamily"><%if(ccmSchedule!=null && ccmSchedule.getScheduleDate()!=null) {%> <%=fc.sdfTordf(ccmSchedule.getScheduleDate().toString()) %> <%} else{%>-<%} %></h4>
						</div>
						<div style="width: 50%;float: right;">
							<h4 class="firstpagefontfamily">Meeting Time</h4>
							<h4 class="firstpagefontfamily"><%if(ccmSchedule!=null && ccmSchedule.getScheduleStartTime()!=null) {%> <%=ccmSchedule.getScheduleStartTime() %> <%} else{%>-<%} %></h4>
						</div>
					</div>
					<div class="center">
						<h4 class="firstpagefontfamily">Meeting Venue</h4>
						<h4 class="firstpagefontfamily"><%if(ccmSchedule!=null && ccmSchedule.getMeetingVenue()!=null) {%> <%=ccmSchedule.getMeetingVenue() %> <%} else{%>-<%} %></h4>
					</div>
					<div class="center" style="margin-top: 50px;">
						<% if(labInfo!=null){ %>
							<h4 class="firstpagefontfamily"><%if(labInfo.getLabName()!=null){ %><%=labInfo.getLabName()  %><%}else{ %>LAB NAME<%} %></h4>
						<%} %>
						<h4 class="firstpagefontfamily" style="margin-top: 50px;">Government of India, Ministry of Defence</h4>
						<h4 class="firstpagefontfamily">Defence Research & Development Organization</h4>
						<h4 class="firstpagefontfamily"><%if(labInfo.getLabAddress() !=null){ %><%=labInfo.getLabAddress()  %> , <%=labInfo.getLabCity()!=null?labInfo.getLabCity(): " - " %><%}else{ %>LAB ADDRESS<%} %></h4>
					</div>
				</td>
			</tr>	
		</table>
 		<!-- ---------------------------------------- Page-1 End --------------------------------------- -->
 		<!-- ---------------------------------------- Page-2 --------------------------------------- -->
		<h1 class="break"></h1>
		
		<table class="agendatable data-table">
			<thead>  
				<tr style="">
					<th colspan="5" class="heading">Agenda</th>
				</tr>
				<tr>
					<th>SN</th>
					<th>Agenda Item</th>
					<!-- <th>Lab</th> -->
					<th>Presenter</th>
					<th>Duration </th>
					<th>File</th> 
				</tr>
			</thead>
			<tbody>
				<%
					if(agendaList!=null && agendaList.size()>0) {
						LocalTime starttime = LocalTime.parse(ccmSchedule.getScheduleStartTime());
						int  count=0;
					  	for(Object[] level1: agendaList){
							if(level1[2].toString().equalsIgnoreCase("0")) {
								++count;
				%>
					<tr>
						<td class="center" style="width: 5%;"><%=count %></td>
						
						<td style="width: 35%;"><%=level1[4]!=null?level1[4].toString(): " - " %></td>
						
						<%-- <td class="center" style="width: 10%;">
							<%if(level1[5]!=null && !level1[5].toString().equalsIgnoreCase("0")) {%>
								<%=level1[5] %>
							<%} else {%>
								-
							<%} %>
						</td> --%>
							
						<td style="width: 25%;">
							<%if(level1[6]!=null && !level1[6].toString().equalsIgnoreCase("0")) {%>
								<%=level1[9]!=null?level1[9].toString(): " - " %>
							<%} else {%>
								-
							<%} %>
						</td>
						
						<td class="center" style="width: 20%;">
							<%=starttime.format( DateTimeFormatter.ofPattern("hh:mm a") ) %> - <%=starttime.plusMinutes(Long.parseLong(level1[7].toString())).format( DateTimeFormatter.ofPattern("hh:mm a") )  %>
						</td>
						
						<td class="center" style="width: 13%;">
							<%if(level1[8]!=null && !level1[8].toString().isEmpty()) {%>
								<a class="btn btn-sm" href="CCMScheduleAgendaFileDownload.htm?scheduleAgendaId=<%=level1[0] %>&count=<%=count %>&subCount=0" target="_blank">
									Annex-<%=level1[3]!=null?level1[3].toString(): " - " %>
	               				</a>
							<%} else{%>	
								-
							<%} %>
						</td>
					</tr>
					
					<%
					List<Object[]> agendaList2 = agendaList.stream().filter(e -> level1[0].toString().equalsIgnoreCase(e[2].toString())).collect(Collectors.toList());
					
					if(agendaList2.size()>0) {
						LocalTime substarttime = starttime ;
					%>
					
						<tr>
							<td colspan="1"></td>
							<td colspan="8">
								<table style="width:100%;" class="subagendatable" id="subagendatable">
									<tbody>
										<%	int countA=0;
											for(Object[] level2: agendaList2){
													++countA;
										%>
											<tr>
												<%-- <td class="center"><%=level2[3] %></td> --%>
												
												<td style="width: 37.3%;"><%=level2[4]!=null?level2[4].toString(): " - " %></td>
												
												<%-- <td class="center" style="width: 11%;">
													<%if(level2[5]!=null && !level2[5].toString().equalsIgnoreCase("0")) {%>
														<%=level2[5] %>
													<%} else {%>
														-
													<%} %>
												</td> --%>
													
												<td style="width: 27.7%;">
													<%if(level2[6]!=null && !level2[6].toString().equalsIgnoreCase("0")) {%>
														<%=level2[9]!=null?level2[9].toString(): " - " %>
													<%} else {%>
														-
													<%} %>
												</td>
												
												<td class="center" style="width: 22%;">
													<%=substarttime.format( DateTimeFormatter.ofPattern("hh:mm a") ) %> - <%=substarttime.plusMinutes(Long.parseLong(level2[7].toString())).format( DateTimeFormatter.ofPattern("hh:mm a") )  %>
													<%substarttime = substarttime.plusMinutes(Long.parseLong(level2[7].toString())); %>
												</td>
												
												<td class="center" style="width: 13%;">
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
		<!-- ---------------------------------------- Page-2 End --------------------------------------- -->

		<!-- ---------------------------------------- Selected Pages --------------------------------------- -->
		
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
				<!-- ---------------------------------------- ATR Page -------------------------------------------------------  -->
				<h1 class="break"></h1>	
								
				<table class="data-table">
					<thead>
						<tr>
							<th colspan="6" class="heading">Action Taken Report of CCM(<%=seqDate !=null?seqDate: " - "%>)</th>
						</tr>
						<tr>
							<th style="width: 5%;">SN</th>
							<th style="width: 15%;">ID</th>
							<th style="width: 30%;">Action Point</th>
							<th style="width: 20%;">Action by</th>
							<th style="width: 10%;">PDC</th>
							<th style="width: 20%;">Status</th>
						
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
										<%if(committee.getCommitteeShortName().trim().equalsIgnoreCase("CCM")){ %>
										<%for (Map.Entry<Integer, String> entry : mapCCM.entrySet()) {
											Date date = inputFormat.parse(obj[1].toString().split("/")[2]);
											 String formattedDate = outputFormat.format(date);
											 if(entry.getValue().equalsIgnoreCase(formattedDate)){
												 key=entry.getKey().toString();
											 } }}%>
										
										<%=committee.getCommitteeShortName()!=null?committee.getCommitteeShortName().trim().toUpperCase():"-"%> <%="-"%> <%=key!=null?key: " - "%> <%="-("+(seqDate!=null?seqDate: " - ")+")/"%> <%=obj[1]!=null?obj[1].toString().split("/")[3]:" - " %>
									<%}%> 
								<!--  -->
								</td>
								<%if(i==0) {%>
						    		<td rowspan="<%=values.size() %>" style="text-align: justify;vertical-align: middle;"><%=obj[2]!=null?obj[2].toString(): " - " %></td>
       										<%} %>
								<td style="text-align: justify;"><%=obj[11]!=null?obj[11].toString(): " - "%> <%=", "%> <%=obj[12]!=null?obj[12].toString(): " - "%></td>
								<td style="text-align: center;">
									<%	String actionstatus = obj[9].toString();
										int progress = obj[15]!=null ? Integer.parseInt(obj[15].toString()) : 0;
										LocalDate pdcorg = LocalDate.parse(obj[3].toString());
										LocalDate lastdate = obj[13]!=null ? LocalDate.parse(obj[13].toString()): null;
										LocalDate today = LocalDate.now();
										LocalDate endPdc = LocalDate.parse(obj[4].toString());
									%> 
					 				
									<span <%if(endPdc.isAfter(today) || endPdc.isEqual(today)) {%>style="color:black;font-weight: bold;" <%} else{%> style="color:maroon ;font-weight:bolder;" <%} %>>
									<%= sdf.format(sdf1.parse(obj[4].toString()))%> 
									</span> 
									<%if(!pdcorg.equals(endPdc)) { %>
									<br> 
									<span <%if(pdcorg.isAfter(today) || pdcorg.isEqual(today)) {%>style="color:black;font-weight: bold;" <%} else{%> style="color:maroon ;font-weight:bolder;" <%} %>>
									<%= sdf.format(sdf1.parse(obj[3].toString()))%> 
									</span>	
									<%} %>
								</td>
								
								<td style="text-align: justify;">
									<% if (obj[16] != null) { %><%=obj[16].toString()%> <% } else{%>-<%} %>
								</td>
			
							</tr>
						<% ++i;}} } else{%>
							<tr>
								<td colspan="6" style="text-align: center;">No Data Available</td>
							</tr>
						<%} %>
					</tbody>
				</table>
							
									
						
				<!-- ---------------------------------------- ATR Page End ---------------------------------------------------  -->
				
				<!-- ---------------------------------------- Pending Points from Previous CCM Page -------------------------------------------------------  -->
				<h1 class="break"></h1>			
							
				<table class="data-table" >
					<thead>
						<tr>
							<th colspan="6" class="heading">Pending Points from Prev CCM</th>
						</tr>
						<tr>
							<th style="width: 5%;">SN</th>
							<th style="width: 15%;">ID</th>
							<th style="width: 30%;">Action Point</th>
							<th style="width: 20%;">Action by</th>
							<th style="width: 10%;">PDC</th>
							<th style="width: 20%;">Status</th>
						
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
										
										<%if(committee.getCommitteeShortName().trim().equalsIgnoreCase("CCM")){ %>
										<%for (Map.Entry<Integer, String> entry : mapCCM.entrySet()) {
											Date date = inputFormat.parse(obj[1].toString().split("/")[2]);
											 String formattedDate = outputFormat.format(date);
											 if(entry.getValue().equalsIgnoreCase(formattedDate)){
												 key=entry.getKey().toString();
											 } }}%>
										
										<%=committee.getCommitteeShortName()!=null?committee.getCommitteeShortName().trim().toUpperCase():" - "%> <%="-"%> <%=key!=null?key: " - "%> <%="-("+(obj[20]!=null?obj[20].toString().substring(2, 7):" - ")+")/"%> <%=obj[1]!=null?obj[1].toString().split("/")[3]:" - " %>
									<%}%> 
								<!--  -->
								</td>
								<%if(i==0) {%>
						    		<td rowspan="<%=values.size() %>" style="text-align: justify;vertical-align: middle;"><%=obj[2]!=null?obj[2].toString(): " - " %></td>
       										<%} %>
								<td style="text-align: justify;"><%=obj[11]!=null?obj[11].toString(): " - "%> <%=", "%> <%=obj[12]!=null?obj[12].toString(): " - "%></td>
								<td style="text-align: center;">
									<%	String actionstatus = obj[9].toString();
										int progress = obj[15]!=null ? Integer.parseInt(obj[15].toString()) : 0;
										LocalDate pdcorg = LocalDate.parse(obj[3].toString());
										LocalDate lastdate = obj[13]!=null ? LocalDate.parse(obj[13].toString()): null;
										LocalDate today = LocalDate.now();
										LocalDate endPdc = LocalDate.parse(obj[4].toString());
									%> 
					 				
									<span <%if(endPdc.isAfter(today) || endPdc.isEqual(today)) {%>style="color:black;font-weight: bold;" <%} else{%> style="color:maroon ;font-weight:bolder;" <%} %>>
									<%= sdf.format(sdf1.parse(obj[4].toString()))%> 
									</span>  
									<%if(!pdcorg.equals(endPdc)) { %>
									<br> 
									<span <%if(pdcorg.isAfter(today) || pdcorg.isEqual(today)) {%>style="color:black;font-weight: bold;" <%} else{%> style="color:maroon ;font-weight:bolder;" <%} %>>
									<%= sdf.format(sdf1.parse(obj[3].toString()))%> 
									</span>	
									<%} %>
								</td>
								
								<td style="text-align: justify;">
									<% if (obj[16] != null) { %><%=obj[16].toString()%> <% } else{%>-<%} %>
								</td>
			
							</tr>
						<% i++; }} } else {%>
							<tr>
								<td colspan="6" style="text-align: center;">No Data Available</td>
							</tr>
						<%} %>
					</tbody>
				</table>
					
				<!-- ---------------------------------------- Pending Points from Previous CCM Page End ---------------------------------------------------  -->
			<%} %>
			
			<!-- ---------------------------------------- DMC Page -------------------------------------------------------  -->
			<%if(slideNames.contains("DMC")) {
				String committeeMainId = (String)request.getAttribute("committeeMainId");
				List<Object[]> dmcActions = (List<Object[]>) request.getAttribute("dmcActions");
				
				Map<String, List<Object[]>> prevdmcActionsToListMap = dmcActions!=null && dmcActions.size()>0?dmcActions.stream()
																	  .filter(e -> !e[9].toString().equalsIgnoreCase("C"))
																	  .collect(Collectors.groupingBy(array -> array[0] + "", LinkedHashMap::new, Collectors.toList())) : new HashMap<>();
				Committee committee = (Committee) request.getAttribute("dmcCommitteeData");
				CommitteeSchedule dmcSchedule = (CommitteeSchedule) request.getAttribute("dmcSchedule");
				String seqDate = dmcSchedule!=null?dmcSchedule.getScheduleDate().toString().substring(2, 7):"";
				
				Map<Integer,String> mapDMC = (Map<Integer,String>)request.getAttribute("mapDMC");
				
			%>
				<h1 class="break"></h1>		
				
				<table class="data-table" >
					<thead>
						<tr>
							<th colspan="6" class="heading">DMC Actions</th>
						</tr>
						<tr >
							<th style="width: 5%;">SN</th>
							<th style="width: 15%;">ID</th>
							<th style="width: 30%;">Action Point</th>
							<th style="width: 20%;">Action by</th>
							<th style="width: 10%;">PDC</th>
							<th style="width: 20%;">Status</th>
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
										<%if(committee.getCommitteeShortName().trim().equalsIgnoreCase("DMC")){ %>
										<%for (Map.Entry<Integer, String> entry : mapDMC.entrySet()) {
											Date date = inputFormat.parse(obj[1].toString().split("/")[2]);
											 String formattedDate = outputFormat.format(date);
											 if(entry.getValue().equalsIgnoreCase(formattedDate)){
												 key=entry.getKey().toString();
											 } }}%>
										
										<%=committee.getCommitteeShortName()!=null?committee.getCommitteeShortName().trim().toUpperCase():" - "%> <%="-"%> <%=key!=null?key: " - "%> <%="-("+(obj[20]!=null?obj[20].toString().substring(2, 7):" - ")+")/"%> <%=obj[1]!=null?obj[1].toString().split("/")[3]:" - " %>
									<%}%> 
								<!--  -->
								</td>
								<%if(i==0) {%>
						    		<td rowspan="<%=values.size() %>" style="text-align: justify;vertical-align: middle;"><%=obj[2]!=null?obj[2].toString(): " - " %></td>
       										<%} %>
								<td style="text-align: justify;"><%=obj[11]!=null?obj[11].toString(): " - "%> <%=", "%> <%=obj[12]!=null?obj[12].toString(): " - "%></td>
								<td style="text-align: center;">
									<%	String actionstatus = obj[9].toString();
										int progress = obj[15]!=null ? Integer.parseInt(obj[15].toString()) : 0;
										LocalDate pdcorg = LocalDate.parse(obj[3].toString());
										LocalDate lastdate = obj[13]!=null ? LocalDate.parse(obj[13].toString()): null;
										LocalDate today = LocalDate.now();
										LocalDate endPdc = LocalDate.parse(obj[4].toString());
									%> 
									<span <%if(endPdc.isAfter(today) || endPdc.isEqual(today)) {%>style="color:black;font-weight: bold;" <%} else{%> style="color:maroon ;font-weight:bolder;" <%} %>>
									<%= sdf.format(sdf1.parse(obj[4].toString()))%> 
									</span>  
									<%if(!pdcorg.equals(endPdc)) { %>
									<br> 
									<span <%if(pdcorg.isAfter(today) || pdcorg.isEqual(today)) {%>style="color:black;font-weight: bold;" <%} else{%> style="color:maroon ;font-weight:bolder;" <%} %>>
									<%= sdf.format(sdf1.parse(obj[3].toString()))%> 
									</span>	
									<%} %>
								</td>
								<td style="text-align: justify;">
									<% if (obj[16] != null) { %><%=obj[16].toString()%> <% } else{%>-<%} %>
								</td>
			
							</tr>
						<% i++; }} } else {%>
							<tr>
								<td colspan="6" style="text-align: center;">No Data Available</td>
							</tr>
						<%} %>
					</tbody>
				</table>	
						
			<%} %>
			<!-- ---------------------------------------- DMC Page End ---------------------------------------------------  -->
			
			<!-- ---------------------------------------- EB Calendar Page ---------------------------------------------------  -->
			<%if(slideNames.contains("EB Calendar")) { 
				List<Object[]> ebCalendarData = (List<Object[]>) request.getAttribute("ebCalendarData");
				String previousMonth = (String) request.getAttribute("previousMonth");
				String currentMonth = (String) request.getAttribute("currentMonth");
				int year = (int) request.getAttribute("year");
			%>
				<h1 class="break"></h1>
				
				<table class="data-table">
					<thead >
						<tr>
							<th colspan="6" class="heading"> EB Calendar</th>
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
								<td style="width: 10%;"><%=obj[0]!=null?obj[0].toString(): " - " %></td>
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
									<%if(obj[3]!=null) {%><%=obj[3].toString() %><%} %>
									
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
										 out.print((obj[3]!=null?",&emsp;":"")+result.toString());
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
						
				<div class="center" style="margin-top: 10px;">
					<span style="color: #f502f5;">Conducted as per Schedule</span> |
					<span style="color: red;">Planned but Not Conducted</span> |
					<span style="color: blue;">Carry over From Prev Month</span>
				</div>				
					
			<%} %>
			<!-- ---------------------------------------- EB Calendar Page End ---------------------------------------------------  -->
			
			<!-- ---------------------------------------- PMRC Calendar Page ---------------------------------------------------  -->
			<%if(slideNames.contains("PMRC Calendar")) { 
				List<Object[]> pmrcCalendarData = (List<Object[]>) request.getAttribute("pmrcCalendarData");
				String previousMonth = (String) request.getAttribute("previousMonth");
				String currentMonth = (String) request.getAttribute("currentMonth");
				int year = (int) request.getAttribute("year");
			%>
				<h1 class="break"></h1>
				
				<table class="data-table">
					<thead>
						<tr>
							<th colspan="6" class="heading">PMRC Calendar</th>
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
								<td style="width: 10%;"><%=obj[0]!=null?obj[0].toString(): " - " %></td>
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
									<%if(obj[3]!=null) {%><%=obj[3].toString()%><%} %>
									
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
										 out.print((obj[3]!=null?",&emsp;":"")+result.toString());
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
						
				<div class="center" style="margin-top: 10px;">
					<span style="color: #f502f5;">Conducted as per Schedule</span> |
					<span style="color: red;">Planned but Not Conducted</span> |
					<span style="color: blue;">Carry over From Prev Month</span>
				</div>				
					
			<%} %>
			<!-- ---------------------------------------- PMRC Calendar Page End ---------------------------------------------------  -->
			
			<!-- ---------------------------------------- ASP Status Page ---------------------------------------------------  -->
			<%if(slideNames.contains("ASP Status")) { 
				HashMap<String, List<Object[]> > aspList = (HashMap<String, List<Object[]> >) request.getAttribute("aspList");
				
				for (Map.Entry<String, List<Object[]>> entry : aspList.entrySet()) {
				    List<Object[]> aspStatusList = entry.getValue(); 
			%>
				<h1 class="break"></h1>
				
				<table class="data-table">
					<thead>
						<tr>
							<th colspan="9" class="heading">ASP Status - <%=entry.getKey() %></th>
						</tr>
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
								<td class="center" style="width: 3%;"><%=++slno %></td>
								<td class="left" style="width: 23%;">
									<%=obj[3]!=null?obj[3].toString():"-" %> <br>
									Cat&emsp;: <%=obj[5]!=null?obj[5].toString():"-" %> <br>
									Cost&nbsp;&nbsp; : <%=obj[6]!=null?String.format("%.2f", Double.parseDouble(obj[6].toString())/10000000):"-" %> (In Cr) <br>
									PDC&nbsp;&nbsp;&nbsp;: <%=obj[7]!=null?obj[7].toString():"-" %> (In Months) <br>
									PDD&emsp;&nbsp;: <%=obj[8]!=null?obj[8].toString():"-" %> <br>
								</td>
								<td class="center" style="width: 9%;">
									<span style="color: blue;"><%=obj[9]!=null?fc.sdfTordf(obj[9].toString()):"-" %></span> <br>
									<span style="color: red;"><%=obj[10]!=null?fc.sdfTordf(obj[10].toString()):"-" %></span> <br>
									<span style="color: green;"><%=obj[11]!=null?fc.sdfTordf(obj[11].toString()):"-" %></span>
								</td>
								<td class="center" style="width: 9%;">
									<span style="color: blue;"><%=obj[12]!=null?fc.sdfTordf(obj[12].toString()):"-" %></span> <br>
									<span style="color: red;"><%=obj[13]!=null?fc.sdfTordf(obj[13].toString()):"-" %></span> <br>
									<span style="color: green;"><%=obj[14]!=null?fc.sdfTordf(obj[14].toString()):"-" %></span>
								</td>
								<td class="center" style="width: 9%;">
									<span style="color: blue;"><%=obj[15]!=null?fc.sdfTordf(obj[15].toString()):"-" %></span> <br>
									<span style="color: red;"><%=obj[16]!=null?fc.sdfTordf(obj[16].toString()):"-" %></span> <br>
									<span style="color: green;"><%=obj[17]!=null?fc.sdfTordf(obj[17].toString()):"-" %></span>
								</td>
								<td class="center" style="width: 9%;">
									<span style="color: blue;"><%=obj[18]!=null?fc.sdfTordf(obj[18].toString()):"-" %></span> <br>
									<span style="color: red;"><%=obj[19]!=null?fc.sdfTordf(obj[19].toString()):"-" %></span> <br>
									<span style="color: green;"><%=obj[20]!=null?fc.sdfTordf(obj[20].toString()):"-" %></span>
								</td>
								<td class="center" style="width: 9%;">
									<span style="color: blue;"><%=obj[21]!=null?fc.sdfTordf(obj[21].toString()):"-" %></span> <br>
									<span style="color: red;"><%=obj[22]!=null?fc.sdfTordf(obj[22].toString()):"-" %></span> <br>
									<span style="color: green;"><%=obj[23]!=null?fc.sdfTordf(obj[23].toString()):"-" %></span>
								</td>
								<td class="center" style="width: 9%;">
									<span style="color: blue;"><%=obj[24]!=null?fc.sdfTordf(obj[24].toString()):"-" %></span> <br>
									<span style="color: red;"><%=obj[25]!=null?fc.sdfTordf(obj[25].toString()):"-" %></span> <br>
									<span style="color: green;"><%=obj[26]!=null?fc.sdfTordf(obj[26].toString()):"-" %></span>
								</td>
								<td style="width: 20%;"><%=obj[27]!=null?obj[27].toString():"-" %></td>
							</tr>
						<%} } else{%>
							<tr>
								<td colspan="9" class="center">No Data Available</td>
							</tr>
						<%} %>	
					</tbody>
				</table>
						
				<div class="center" style="margin-top: 10px;">
					<span style="color: blue;">Probable Date</span> |
					<span style="color: red;">Revised Date</span> |
					<span style="color: green;">Actual Date</span>
				</div>		
					
			<%} }%>
			<!-- ---------------------------------------- ASP Status Page End ---------------------------------------------------  -->
			<!-- ---------------------------------------- Closure Status Page ---------------------------------------------------  -->
			<%if(slideNames.contains("Closure Status")) { 
				HashMap<String, List<Object[]> > csList = (HashMap<String, List<Object[]> >) request.getAttribute("closureStatusList");
				
				for (Map.Entry<String, List<Object[]>> entry : csList.entrySet()) {
				    List<Object[]> closureStatusList = entry.getValue(); 
			%>
				<h1 class="break"></h1>
				
				<table class="data-table">
					<thead>
						<tr>
							<th colspan="7" class="heading">Closure Status - <%=entry.getKey()!=null?entry.getKey(): " - " %></th>
						</tr>
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
								<td style="width: 25%;">
									<%=obj[3]!=null?obj[3].toString():"-" %> <br>
									Cat&emsp;: <%=obj[5]!=null?obj[5].toString():"-" %> <br>
									Cost&nbsp;&nbsp; : <%=obj[6]!=null?String.format("%.2f", Double.parseDouble(obj[6].toString())/10000000):"-" %> (In Cr) <br>
									PD&emsp;&nbsp;: <%=obj[7]!=null?obj[7].toString():"-" %> 
								</td>
								<td class="center" style="width: 10%;">
									<%=obj[8]!=null?fc.sdfTordf(obj[8].toString()):"-" %> / <br> <%=obj[9]!=null?fc.sdfTordf(obj[9].toString()):"-" %> 
								</td>
								<td style="width: 15%;"><%=obj[10]!=null?obj[10].toString():"-" %></td>
								<td style="width: 15%;"><%=obj[11]!=null?obj[11].toString():"-" %></td>
								<td style="width: 15%;"><%=obj[12]!=null?obj[12].toString():"-" %></td>
								<td style="width: 20%;"><%=obj[13]!=null?obj[13].toString():"-" %></td>
							</tr>
						<%} } else{%>
							<tr>
								<td colspan="7" class="center">No Data Available</td>
							</tr>
						<%} %>	
					</tbody>
				</table>
						
			<%} }%>
			<!-- ---------------------------------------- Closure Status Page End ---------------------------------------------------  -->
			
			<!-- ---------------------------------------- Cash Out Go Status Page ---------------------------------------------------  -->
			<%if(slideNames.contains("Cash Out Go Status")) { 
				LinkedHashMap<String, List<Object[]> > cogList = (LinkedHashMap<String, List<Object[]> >) request.getAttribute("cashOutGoList");
				int quarter = (int)request.getAttribute("quarter");
				
			%>
				<h1 class="break"></h1>
				
				<table class="data-table" >
					<thead>
						
						<tr>
							<th colspan="<%=12-quarter %>" class="heading">Cash Out Go Status</th>
						</tr>
						<tr>
							<th colspan="<%=12-quarter %>" class="right" style="font-size: 1.1rem !important;">In Lakhs</th>
						</tr>
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
						<tr>
							<td colspan="<%=12-quarter %>" class="center" style="background-color: aliceblue;"><b><%=entry.getKey()!=null?entry.getKey(): " - " %></b></td>
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
							<%if(!budgetHead.equalsIgnoreCase(obj[6].toString()) || slno==0 ) { %>
							 	<%if(slno!=0) {%>
								 	<tr>
										<td class="right" colspan="2"><b><%=budgetHead %> : </b> </td>
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
							 	<tr>
									<td class="right" colspan="2"> <b><%=budgetHead!=null?budgetHead: " - " %> :</b> </td>
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
								<tr>
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
				<%

				for (Map.Entry<String, List<Object[]>> entry : cogList.entrySet()) {
				    List<Object[]> cashOutGoList = entry.getValue(); 
				%>			
				<h1 class="break"></h1>
				
				<table class="data-table">
					<thead>
						<tr>
							<th colspan="<%=12-quarter %>" class="heading">Cash Out Go Status - <%=entry.getKey()!=null?entry.getKey(): " - " %></th>
						</tr>
						<tr>
							<th colspan="<%=12-quarter %>" class="right" style="font-size: 1.1rem !important;">In Lakhs</th>
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
									totalAllotment = 0.00, totalExpenditure = 0.00, totalBalance = 0.00, totalCOGQ1 = 0.00, totalCOGQ2 = 0.00, totalCOGQ3 = 0.00, totalCOGQ4 = 0.00, totalcogTotal = 0.00, totalAddl = 0.00;
							String budgetHead ="";
							Double lakh = 100000.00;
							String decimalPoint = "%.2f";
							for(Object[] obj : cashOutGoList) {
						%>
							<%if(!budgetHead.equalsIgnoreCase(obj[6].toString()) || slno==0 ) { %>
							 	<%if(slno!=0) {%>
								 	<tr>
										<td class="right" colspan="2"><b>Total Amount (<%=budgetHead!=null?budgetHead: " - " %>) : </b> </td>
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
									<td colspan="<%=12-quarter %>" class="center" style="background-color: aliceblue;"><b>Budget Head : <%=budgetHead!=null?budgetHead: " - " %></b></td>
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
								<td ><%=obj[4]!=null?obj[4].toString(): " - " %></td>
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
									<td class="right" colspan="2"> <b>Total Amount (<%=budgetHead!=null?budgetHead: " - " %>) :</b> </td>
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
								<tr>
									<td class="right" colspan="2"> <b>Grand Total Amount :</b> </td>
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
					</tbody>
				</table>
			<%} }%>
			<!-- ---------------------------------------- Cash Out Go Status Page End ---------------------------------------------------  -->
			
			<!-- ---------------------------------------- Test & Trials Page ---------------------------------------------------  -->
			<%if(slideNames.contains("Test & Trials")) { 
				List<CCMAchievements> ccmTestAndTrialsList = (List<CCMAchievements>) request.getAttribute("ccmTestAndTrialsList");
			%>
				<h1 class="break"></h1>
				
				<table class="data-table">
					<thead>
						<tr>
							<th colspan="6" class="heading" >Test & Trials</th>
						</tr>
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
								<td><%=achmnts.getLabCode()!=null?achmnts.getLabCode(): " - " %></td>
								<td><%=achmnts.getAchievement()!=null?achmnts.getAchievement(): " - " %></td>
								<td class="center">
									<%if(achmnts.getImageName()!=null) {%>
										<a class="btn btn-sm" href="CCMAchievementsAttachmentDownload.htm?achievementId=<%=achmnts.getAchievementId() %>&attachmentName=image" target="_blank">
											Image
								        </a>
								        <br>
	                      			<%} %>
									<%if(achmnts.getAttachmentName()!=null) {%>
		                      			<a class="btn btn-sm" href="CCMAchievementsAttachmentDownload.htm?achievementId=<%=achmnts.getAchievementId() %>&attachmentName=pdf" target="_blank">
											Attach
								    	</a>
								    	<br>
	                      			<%} %>
									<%if(achmnts.getVideoName()!=null) {%>
		                      			<a class="btn btn-sm" href="CCMAchievementsAttachmentDownload.htm?achievementId=<%=achmnts.getAchievementId() %>&attachmentName=video" target="_blank">
											Video
								        </a>
	                      			<%} %>
								</td>
							</tr>
						<%}} else{%>
							<tr>
								<td colspan="6" style="text-align: center;">No Data Available</td>
							</tr>
						<%} %>
					</tbody>
				</table>
			<%} %>
			<!-- ---------------------------------------- Test & Trials Page End ---------------------------------------------------  -->
			<!-- ---------------------------------------- Achievements Page ---------------------------------------------------  -->
			<%if(slideNames.contains("Achievements")) { 
				List<CCMAchievements> ccmAchievementsList = (List<CCMAchievements>) request.getAttribute("ccmAchievementsList");
			%>
				<h1 class="break"></h1>
				
				<table class="data-table">
					<thead>
						<tr>
							<th colspan="6" class="heading">Achievements</th>
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
									<%if(achmnts.getImageName()!=null) {%>
										<a class="btn btn-sm" href="CCMAchievementsAttachmentDownload.htm?achievementId=<%=achmnts.getAchievementId() %>&attachmentName=image" target="_blank">
											Image
								        </a>
								        <br>
	                      			<%} %>
									<%if(achmnts.getAttachmentName()!=null) {%>
		                      			<a class="btn btn-sm" href="CCMAchievementsAttachmentDownload.htm?achievementId=<%=achmnts.getAchievementId() %>&attachmentName=pdf" target="_blank">
											Attach
								    	</a>
								    	<br>
	                      			<%} %>
									<%if(achmnts.getVideoName()!=null) {%>
		                      			<a class="btn btn-sm" href="CCMAchievementsAttachmentDownload.htm?achievementId=<%=achmnts.getAchievementId() %>&attachmentName=video" target="_blank">
											Video
								        </a>
	                      			<%} %>
								</td>
							</tr>
						<%}} else{%>
							<tr>
								<td colspan="6" style="text-align: center;">No Data Available</td>
							</tr>
						<%} %>
					</tbody>
				</table>
						
			<%} %>
			<!-- ---------------------------------------- Achievements Page End ---------------------------------------------------  -->
			<!-- ---------------------------------------- Others Page ---------------------------------------------------  -->
			<%if(slideNames.contains("Others")) { 
				List<CCMAchievements> ccmOtherssList = (List<CCMAchievements>) request.getAttribute("ccmOtherssList");
			%>
				<h1 class="break"></h1>
				
				<table class="data-table">
					<thead>
						<tr>
							<th colspan="6" class="heading">Achievements</th>
						</tr>
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
								<td><%=achmnts.getLabCode()!=null?achmnts.getLabCode(): " - " %></td>
								<td><%=achmnts.getAchievement()!=null?achmnts.getAchievement(): " - " %></td>
								<td class="center">
									<%if(achmnts.getImageName()!=null) {%>
										<a class="btn btn-sm" href="CCMAchievementsAttachmentDownload.htm?achievementId=<%=achmnts.getAchievementId() %>&attachmentName=image" target="_blank">
											Image
								        </a>
								        <br>
	                      			<%} %>
									<%if(achmnts.getAttachmentName()!=null) {%>
		                      			<a class="btn btn-sm" href="CCMAchievementsAttachmentDownload.htm?achievementId=<%=achmnts.getAchievementId() %>&attachmentName=pdf" target="_blank">
											Attach
								    	</a>
								    	<br>
	                      			<%} %>
									<%if(achmnts.getVideoName()!=null) {%>
		                      			<a class="btn btn-sm" href="CCMAchievementsAttachmentDownload.htm?achievementId=<%=achmnts.getAchievementId() %>&attachmentName=video" target="_blank">
											Video
								        </a>
	                      			<%} %>
								</td>
							</tr>
						<%}} else{%>
							<tr>
								<td colspan="6" style="text-align: center;">No Data Available</td>
							</tr>
						<%} %>
					</tbody>
				</table>
			<%} %>
			<!-- ---------------------------------------- Others Page End ---------------------------------------------------  -->
			
		<!-- ---------------------------------------- Selected Pages End------------------------------------ -->
		
		<!-- ---------------------------------------- Last Page --------------------------------------- -->
		<h1 class="break"></h1>
		<div style="margin: 60px 10px 10px 10px;">
			<img class="" style="width: 100%; height: 100%;" <%if(thankYouImg!=null ){ %> src="data:image/*;base64,<%=thankYouImg%>" alt="Logo"<%}else{ %> alt="Image Not Found" <%} %> > 
		</div>
		<!-- ---------------------------------------- Last Page --------------------------------------- -->
			
		
	</div><!-- main div -->
	
	
</body>
</html>