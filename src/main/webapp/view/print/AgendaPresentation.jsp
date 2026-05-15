<%@page import="com.vts.pfms.committee.model.ProgrammeMaster"%>
<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.LocalTime"%>
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
<link rel="shortcut icon" type="image/png"
	href="view/images/drdologo.png">
<jsp:include page="../static/dependancy.jsp"></jsp:include>
<spring:url value="/resources/css/slides-style.css" var="SlidesStyleCSS" />
<link href="${SlidesStyleCSS}" rel="stylesheet" />
<spring:url value="/resources/css/print/agendaPresentation.css" var="agendaPresentation" />     
<link href="${agendaPresentation}" rel="stylesheet" />
<title>PMS - Presentation</title>

<meta charset="ISO-8859-1">

</head>
<body class="agp-48">
	<%
		FormatConverter fc = new FormatConverter();
		SimpleDateFormat sdf = fc.getRegularDateFormat();
		SimpleDateFormat sdf1 = fc.getSqlDateFormat();
		List<String> SplCommitteeCodes=(List<String>) request.getAttribute("SplCommitteeCodes");
	
		Object[] projectattributes = (Object[]) request.getAttribute("projectattributes");
		Object[] committeeMetingsCount =  (Object[]) request.getAttribute("committeeMetingsCount");
		Object[] scheduledata =  (Object[]) request.getAttribute("scheduledata");
	
		List<Object[]> AgendaList =  (List<Object[]>) request.getAttribute("AgendaList");
		List<Object[]> AgendaDocList =  (List<Object[]>) request.getAttribute("AgendaDocList");
		
		LabMaster labInfo=(LabMaster)request.getAttribute("labInfo");
		String lablogo = (String)request.getAttribute("lablogo");
		String Drdologo = (String)request.getAttribute("Drdologo");
		Committee committeeData = (Committee) request.getAttribute("committeeData");
		
		String scheduleid = scheduledata[6].toString();
		String committeeid = scheduledata[0].toString();
		String CommitteeCode = committeeData.getCommitteeShortName().trim();
		String CommitteeName = committeeData.getCommitteeName().trim();
		String projectid = scheduledata[9].toString();
		String scheduletype = scheduledata[26]!=null?scheduledata[26].toString():"N";
		String initiationid=scheduledata[17].toString();
		
		
		String ProjectCode="General";
				if(projectattributes!=null){
					ProjectCode=projectattributes[0].toString();
				}
				
		String MeetingNo = CommitteeCode+" #"+(Long.parseLong(committeeMetingsCount[1].toString())+1);
		
		
		List<Object[]> decesions =  (List<Object[]>) request.getAttribute("decesions");
		List<Object[]> recommendationList =  (List<Object[]>) request.getAttribute("recommendationList");
		List<Object[]> ActionDetails =  (List<Object[]>) request.getAttribute("ActionDetails");
		List<Object[]> meetingsHeld =  (List<Object[]>) request.getAttribute("meetingsHeld");
		List<Object[]> OpenActionDetails = new ArrayList<>();
		List<Object[]> closeActionDetails = new ArrayList<>();
		
		if(ActionDetails!=null && ActionDetails.size()>0){
			OpenActionDetails = ActionDetails.stream().filter(e->!e[3].toString().equalsIgnoreCase("C")).collect(Collectors.toList());
			closeActionDetails = ActionDetails.stream().filter(e->e[3].toString().equalsIgnoreCase("C")).collect(Collectors.toList());
		}
		
		ProgrammeMaster programmeMaster = (ProgrammeMaster) request.getAttribute("programmeMaster");
		String programmeId = (String)request.getAttribute("programmeId");
		if(programmeMaster!=null) {
			ProjectCode = programmeMaster.getPrgmCode();
		}
		
		Map<Long, List<Object[]>> openActionsMap = OpenActionDetails.stream().collect(Collectors.groupingBy(row -> Long.parseLong(row[20].toString()) ));
		Map<Long, List<Object[]>> closeActionsMap = closeActionDetails.stream().collect(Collectors.groupingBy(row -> Long.parseLong(row[20].toString()) ));

	%>


	<div id="presentation-slides" class="carousel slide"
		data-ride="carousel">

		<div class="carousel-inner" align="center">

			<!-- ---------------------------------------- P-1  Div ----------------------------------------------------- -->
			<div class="carousel-item active">

				<div class="content agp-2" align="center"
					>

					<div class="firstpage">

						<div align="center">
							<h2 class="agp-1"
								>Presentation</h2>
						</div>
						<div align="center">
							<h3 class="agp-3">for</h3>
						</div>

						<div align="center">
							<h3 class="agp-4"><%=CommitteeName!=null?StringEscapeUtils.escapeHtml4(CommitteeName): " - " %>
								#<%=Long.parseLong(committeeMetingsCount[1].toString())+1 %>
								Meeting
							</h3>
						</div>

						<div align="center">
							<h3 class="agp-5">
								<%if(projectattributes!=null){%>
								<%= projectattributes[1]!=null?StringEscapeUtils.escapeHtml4(projectattributes[1].toString()): " - " %>
								(<%= projectattributes[0]!=null?StringEscapeUtils.escapeHtml4(projectattributes[0].toString()): " - "%>)
								<%}%>
							</h3>
						</div>

						<table class="executive home-table agp-5"
							>
							<tr>
								<th colspan="8" class="agp-7">
									<img class="logo agp-6" 
									<%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>"
									alt="Logo" <%}else{ %> alt="File Not Found" <%} %>> <br>
								</th>

							</tr>
						</table>

						<table class="agp-8">
							<tr class="mt-1">
								<th class="agp-9">
									<u>Meeting Id </u>
								</th>
							</tr>
							<tr>
								<th class="agp-10">
									<%=scheduledata[11]!=null?StringEscapeUtils.escapeHtml4(scheduledata[11].toString()): " - " %>
								</th>
							</tr>
						</table>
						<table class="agp-11">
							<tr>
								<th
									class="agp-12">
									<u> Meeting Date </u>
								</th>
								<th class="agp-13"
									><u>
										Meeting Time </u></th>
							</tr>
							<tr>
								<%-- <%LocalTime starttime = LocalTime.parse(LocalTime.parse(nextMeetVenue[3].toString(),DateTimeFormatter.ofPattern("HH:mm:ss")).format( DateTimeFormatter.ofPattern("HH:mm") ));   %> --%>
								<td
									class="agp-14">
									<b><%=sdf.format(sdf1.parse(StringEscapeUtils.escapeHtml4(scheduledata[2].toString())))%></b>
								</td>
								<td class="agp-14">
									<b><%=scheduledata[3]!=null?StringEscapeUtils.escapeHtml4(scheduledata[3].toString()): " - "/* starttime.format( DateTimeFormatter.ofPattern("hh:mm a") ) */%></b>
								</td>
							</tr>
						</table>
						<table class="agp-8">
							<tr class="mt-1">
								<th
									class="agp-10">
									<u>Meeting Venue</u>
								</th>
							</tr>
							<tr>
								<th
									class="agp-10">
									<% if(scheduledata[12]!=null){ %><%=StringEscapeUtils.escapeHtml4(scheduledata[12].toString()) %> <%}else{ %>
									- <%} %>
								</th>
							</tr>
						</table>

						<table class="executive home-table agp-15"
							s>
							<% if(labInfo!=null){ %>
							<tr>
								<th colspan="8"
									class="agp-16">
									<%if(labInfo.getLabName()!=null){ %><%=StringEscapeUtils.escapeHtml4(labInfo.getLabName())  %>
									<%}else{ %>LAB NAME<%} %>
								</th>
							</tr>
							<%}%>
							<tr>
								<th colspan="8"
									class="agp-17"><br>Government
									of India, Ministry of Defence</th>
							</tr>
							<tr>
								<th colspan="8"
									class="agp-17">Defence
									Research & Development Organization</th>
							</tr>
							<tr>
								<th colspan="8"
									class="agp-17">
									<%if(labInfo.getLabAddress() !=null){ %><%=StringEscapeUtils.escapeHtml4(labInfo.getLabAddress())  %>
									, <%=labInfo.getLabCity()!=null?StringEscapeUtils.escapeHtml4(labInfo.getLabCity()): " - " %>
									<%}else{ %>LAB ADDRESS<%} %>
								</th>
							</tr>
						</table>


					</div>

				</div>

			</div>
			<!-- ----------------------------------------  P-1  Div ----------------------------------------------------- -->


			<!-- ---------------------------------------- P-2  Div ----------------------------------------------------- -->

			<div class="carousel-item">

				<div class="content-header row ">

					<div class="col-md-1">
						<img class="agp-18"
							
							<%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>"
							alt="Logo" <%}else{ %> alt="File Not Found" <%} %>>
					</div>
					<div class="col-md-1 pt-2" align="left" >
						<b class="agp-19"><%=ProjectCode!=null?StringEscapeUtils.escapeHtml4(ProjectCode): " - " %></b>
					</div>
					<div class="col-md-8">
						<h3>Agenda</h3>
					</div>
					<div class="col-md-1 pt-2" align="right" >
						<b class="agp-19"><%=MeetingNo!=null?StringEscapeUtils.escapeHtml4(MeetingNo): " - " %></b>
					</div>
					<div class="col-md-1">
						<img class="agp-18"
							
							<%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>"
							alt="Logo" <%}else{ %> alt="File Not Found" <%} %>>
					</div>

				</div>

				<div class="content">




					<% if(Long.parseLong(projectid)>0  && SplCommitteeCodes.contains(CommitteeCode)){ %>
					<div class="row agp-20" >
						<form action="#" method="post" id="myfrm" target="_blank"
							class="agp-21">

							<% if(scheduledata[23].toString().equalsIgnoreCase("Y")){%>
							<input type="submit" class="btn btn-sm back"
								formaction="MeetingBriefingPaper.htm" value="Briefing"
								formmethod="get" data-toggle="tooltip" data-placement="bottom"
								title="Briefing Paper">
							<%}%>

							<button type="submit" class="btn btn-sm agp-22"
								
								formaction="BriefingPresentation.htm" formmethod="post"
								formtarget="_blank" data-toggle="tooltip"
								data-placement="bottom" title="Presentation">
								<img src="view/images/presentation.png"
									class="agp-23">
							</button>
							<input type="hidden" name="scheduleid" value="<%=scheduleid%>">
							<input type="hidden" name="committeeid" value="<%=committeeid%>">
							<input type="hidden" name="projectid" value="<%=projectid %>">
							<input type="hidden" name="${_csrf.parameterName}"
								value="${_csrf.token}" />
						</form>
					</div>
					<% } %>


					<table
						class="table table-bordered table-hover table-striped table-condensed agp-24"
						>
						<thead>
							<tr>
								<th class="width-5">SN</th>
								<th  class="width-22" >Agenda Item</th>
								<!-- 	<th style="width: 15%;">Reference</th>
			                       	<th style="width: 10%;">Remarks</th> -->
								<th class="width-24" >Presenter</th>
								<th class="width-14" >Duration</th>
								<th class="width-10" >Attachment</th>
								<%if(scheduletype.equalsIgnoreCase("P")) {%>
									<th class="width-10">Present</th>
								<%} %>
							</tr>
						</thead>
						<tbody>
							<%LocalTime starttime = LocalTime.parse(LocalTime.parse(scheduledata[3].toString(),DateTimeFormatter.ofPattern("HH:mm:ss")).format( DateTimeFormatter.ofPattern("HH:mm") ));  %>
							<%	int count=0;
								for(Object[] 	obj:AgendaList){ count++;%>
							<tr>

								<td class="text-center"><%=count%></td>
								<td><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - " %><%= obj[15]!=null && !obj[15].toString().isEmpty()? "  ("+ StringEscapeUtils.escapeHtml4(obj[15].toString())+")":"" %></td>
								<%-- <td><%=obj[4] %>  </td>									
									<td><%=obj[6] %></td> --%>
								<td><%=obj[10]!=null?StringEscapeUtils.escapeHtml4(obj[10].toString()): " - "%>,  <%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - " %></td>
								<td class="text-center"><%=starttime.format( DateTimeFormatter.ofPattern("hh:mm a") ) %>
									- <%=starttime.plusMinutes(Long.parseLong(obj[12].toString())).format( DateTimeFormatter.ofPattern("hh:mm a") )  %>
									<%starttime=starttime.plusMinutes(Long.parseLong(obj[12].toString()) /* + 1 */); %>

								</td>
								<td>
							 			<table>
											<%for(Object[] doc : AgendaDocList) { 
											if(obj[0].toString().equalsIgnoreCase(doc[1].toString())){%>
												<tr>
													<td><%=doc[3]!=null?StringEscapeUtils.escapeHtml4(doc[3].toString()): " - " %></td>
												    <%if(Long.parseLong(initiationid) > 0){ %>
														   <td class="agp-25" ><a href="PrePRojectAgendaDocLinkDownload.htm?filerepid=<%=doc[2]%>" target="blank"><i class="fa fa-download agp-26"  aria-hidden="true"></i></a></td>
													<%}else{ %>
															<td class="agp-25" ><a href="AgendaDocLinkDownload.htm?filerepid=<%=doc[2]%>" target="blank"><i class="fa fa-download agp-26" aria-hidden="true"></i></a></td>
													<%} %>
												<tr>													
											<%} }%>
										</table>
									</td> 
								<%if(scheduletype.equalsIgnoreCase("P")) {%>	
									<td>
										<form action="#" method="post" id="myfrm_<%=count %>" target="_blank"
											class="<%= (obj[5] != null && !obj[5].toString().equals("0") && !obj[5].toString().equals("-1")) ? "agp-21" : "agp-21 mr-5" %>">
				
											<% if(scheduledata[23].toString().equalsIgnoreCase("Y")){%>
												<input type="submit" class="btn btn-sm back" 
												formaction="MeetingBriefingPaper.htm" value="Briefing" formmethod="get" data-toggle="tooltip" 
												data-placement="bottom" title="Briefing Paper">
											<%}%>
											
											<%if(obj[5]!=null && !obj[5].toString().equalsIgnoreCase("0") && !obj[5].toString().equalsIgnoreCase("-1")){ %>
											<button type="submit" class="btn btn-sm agp-22"
												formaction="PrgmBriefingPresentation.htm" formmethod="post" formtarget="_blank" data-toggle="tooltip"
												data-placement="bottom" title="Presentation">
												<img src="view/images/presentation.png" class="agp-23">
											</button>
											<%}
											else if(obj[5]==null || (obj[5].toString().equalsIgnoreCase("0") || obj[5].toString().equalsIgnoreCase("-1"))){%>
											-
											<%} %>
											<input type="hidden" name="scheduleid" value="<%=scheduleid%>">
											<input type="hidden" name="committeeid" value="<%=committeeid%>">
											<input type="hidden" name="projectid" value="<%=obj[5] %>">
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										</form>
									</td>
								<%} %>
							</tr>

							<%} %>
						</tbody>
					</table>
				</div>

			</div>

			<div class="carousel-item">

			<div class="content-header row ">

					<div class="col-md-1">
						<img class="agp-18"
							
							<%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>"
							alt="Logo" <%}else{ %> alt="File Not Found" <%} %>>
					</div>
					<div class="col-md-1 pt-2" align="left" >
						<b class="agp-19"><%=ProjectCode!=null?StringEscapeUtils.escapeHtml4(ProjectCode): " - " %></b>
					</div>
					<div class="col-md-8">
						<h3>Agenda</h3>
					</div>
					<div class="col-md-1 pt-2" align="right" >
						<b class="agp-19"><%=MeetingNo!=null?StringEscapeUtils.escapeHtml4(MeetingNo): " - " %></b>
					</div>
					<div class="col-md-1">
						<img class="agp-18"
							
							<%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>"
							alt="Logo" <%}else{ %> alt="File Not Found" <%} %>>
					</div>

				</div>

				<div class="content">
					<div class="row">
					<div class="col-md-3"></div>
					<div class="col-md-3">
					<table class="subtables agp-27">
								<thead>
								<tr>
								<th class="agp-28">Committee</th>
								<th class="agp-28"> Date Held</th>
								</tr>
							</thead>
						<tbody>
						<% 
						int meetingCount=0;
						if( meetingsHeld !=null && meetingsHeld.size()>0) {
							int meetingsize = meetingsHeld.size();
							int limit = meetingsize>15 ? meetingsize / 2 + ( meetingsize % 2 == 0 ? 0 : 1 ) : meetingsize;	
						for(Object[]obj:meetingsHeld.stream().limit(limit).collect(Collectors.toList()) ){
						%>
						<tr>
						<td> 
						<%if(!obj[3].toString().equalsIgnoreCase("0")){ %>
						 <a target="blank" href="CommitteeMinutesNewDownload.htm?committeescheduleid=<%=obj[0].toString() %>"><%=obj[7]!=null?StringEscapeUtils.escapeHtml4(obj[7].toString()): " - "%> <%="#"+(++meetingCount) %> </a> 
						<%}else{ %>
				<a target="blank" href="CommitteeMinutesViewAllDownload.htm?committeescheduleid=<%=obj[0].toString() %>"><%=obj[7]!=null?StringEscapeUtils.escapeHtml4(obj[7].toString()): " - "%> <%="#"+(++meetingCount) %> </a> 
						
						<%} %>
						</td>
						<td class="text-center"> <%= sdf.format(sdf1.parse(StringEscapeUtils.escapeHtml4(obj[2].toString())))%> </td>
						</tr>
						<%} %>
						<%}else{ %>
						<tr>
						<td class="text-center" colspan="2"  >No Meetings Held !</td>
						</tr>
						<%} %>
						</tbody>


					</table>
					</div>
					<%  if( meetingsHeld !=null && meetingsHeld.size()>0){
						int meetingsize = meetingsHeld.size();
						int limit1 = meetingsize>15 ? meetingsHeld.size()/2 + (meetingsHeld.size()%2==0?0:1) : meetingsize;	
						if(meetingsHeld.stream().skip(limit1).collect(Collectors.toList()).size()>0) {%>
					<div class="col-md-3">
					<table class="subtables agp-27"
						>
								<thead>
								<tr>
								<th class="agp-28" >Committee</th>
								<th class="agp-28" > Date Held</th>
								</tr>
							</thead>
						<tbody>
						<%for(Object[]obj:meetingsHeld.stream().skip(limit1).collect(Collectors.toList()) ){
						%>	
						<tr>
						<td>
											<%
											if (!obj[3].toString().equalsIgnoreCase("0")) {
											%> <a
											target="blank"
											href="CommitteeMinutesNewDownload.htm?committeescheduleid=<%=obj[0].toString()%>"><%=obj[7]!=null?StringEscapeUtils.escapeHtml4(obj[7].toString()): " - " %> <%= "#" + (++meetingCount)%>
										</a> <%
											 } else {
											 %> <a target="blank"
											href="CommitteeMinutesViewAllDownload.htm?committeescheduleid=<%=obj[0].toString()%>"><%=obj[7]!=null?StringEscapeUtils.escapeHtml4(obj[7].toString()): " - "%> <%= "#" + (++meetingCount)%>
										</a> <%} %>
										</td>
						<td class="text-center"> <%= sdf.format(sdf1.parse(StringEscapeUtils.escapeHtml4(obj[2].toString())))%> </td>
						</tr>
						<%} %>
						
						</tbody>


					</table>
					</div>
					<%}} %>
					<div class="col-md-3"></div>
					</div>
				</div>

			</div>


			<div class="carousel-item">

				<div class="content-header row ">

					<div class="col-md-1">
						<img class="agp-18"
							
							<%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>"
							alt="Logo" <%}else{ %> alt="File Not Found" <%} %>>
					</div>
					<div class="col-md-1 pt-2" align="left" >
						<b class="agp-19"><%=ProjectCode!=null?StringEscapeUtils.escapeHtml4(ProjectCode): " - " %></b>
					</div>
					<div class="col-md-8">
						<h3>Agenda</h3>
					</div>
					<div class="col-md-1 pt-2" align="right" >
						<b class="agp-19"><%=MeetingNo!=null?StringEscapeUtils.escapeHtml4(MeetingNo): " - " %></b>
					</div>
					<div class="col-md-1">
						<img class="agp-18"
							
							<%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>"
							alt="Logo" <%}else{ %> alt="File Not Found" <%} %>>
					</div>

				</div>

				<div class="content">

					<table class="subtables agp-27"
						>
						<thead>
							<tr>
								<td colspan="10" >
									<p class="agp-29">
									
										<span class="assigned">AA</span> : Activity Assigned
										&nbsp;&nbsp; <span class="ongoing">OG</span> : On Going
										&nbsp;&nbsp; <span class="delay">DO</span> : Delay - On Going
										&nbsp;&nbsp; <span class="delay">FD</span> : Forwarded With
										Delay &nbsp;&nbsp;<span class="ongoing">RC</span> : Review & Close &nbsp;&nbsp; 
									</p>
								</td>

							</tr>
							<tr>
								<th class="std wid20" >SN</th>
								<th class="std wid60" >Action No</th>
								<th class="std wid300" >Action Point</th>
								<th class="std wid100" >PDC</th>
								<th class="std wid80" >Progress</th>
								<th class="std wid100" >Responsibility</th>
								<th class="std wid80" >Status<!-- (DD) --></th>
							</tr>
						</thead>

						<tbody>
							<%if(OpenActionDetails.size()>0) { %>
							<%int count1=0;String key="";;
								for(Object[]obj:OpenActionDetails){ %>
							<tr>
								<td class="std agp30" align="center"
									><%=++count1 %>.
								</td>
								<td class="std agp-31"
									>
							<button class="btn btn-sm agp-32"   onclick="ActionDetails( <%=obj[0] %>);" ><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %> </button>		
								</td>
								<td class="std agp-33" >
									<%=obj[5]!=null?obj[5].toString(): " - "%>
								</td>
								<td class="std tetx-center"><%=sdf.format(sdf1.parse(StringEscapeUtils.escapeHtml4(obj[10].toString()))) %></td>
								<td class="std .agp-34" ><%=obj[13]!=null?StringEscapeUtils.escapeHtml4(obj[13].toString()): " - " %></td>
								<td class="std .agp-34" ><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></td>
								<td class="std">
									<%if(obj[4]!= null){ %> <%	String actionstatus = obj[3].toString();
														int progress = obj[13]!=null ? Integer.parseInt(obj[13].toString()) : 0;
														LocalDate pdcorg = LocalDate.parse(obj[10].toString());
														LocalDate lastdate = obj[14]!=null ? LocalDate.parse(obj[14].toString()): null;
														LocalDate today = LocalDate.now();
													%> <%if(actionstatus.equals("F")  && (pdcorg.isAfter(lastdate) || pdcorg.isEqual(lastdate) )){ %>
									<span class="ongoing">RC</span> <%}else if(actionstatus.equals("F")  && pdcorg.isBefore(lastdate)) { %>
									<span class="delay">FD</span> <%}else if((pdcorg.isAfter(today) || pdcorg.isEqual(today)) && progress>0){  %>
									<span class="ongoing">OG</span> <%}else if(pdcorg.isBefore(today) && progress>0){  %>
									<span class="delay">DO <%-- (<%= ChronoUnit.DAYS.between(pdcorg, today)  %>)  --%>
								</span> <%}else if( progress==0) {%> <span class="assigned">AA</span> <%} %>
									<%}else { %> <span class="notassign">NA</span> <%} %>



								</td>
							</tr>
							<% }%>
							<%}else{ %>
							<tr>
								<td colspan="7" class="text-center">No Data Available</td>
							</tr>
							<%} %>
						</tbody>

					</table>

				</div>

			</div>

			<%if(programmeId!=null && !programmeId.isEmpty() && Long.parseLong(programmeId)>0 && !AgendaList.isEmpty()) {
				int sn = 0;
				for(Object[] agenda : AgendaList) { %>
				
					<div class="carousel-item">
						<div class="content-header row ">

					<div class="col-md-1">
						<img class="agp-18"
							
							<%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>"
							alt="Logo" <%}else{ %> alt="File Not Found" <%} %>>
					</div>
					<div class="col-md-1 pt-2" align="left" >
						<b class="agp-19"><%=ProjectCode!=null?StringEscapeUtils.escapeHtml4(ProjectCode): " - " %></b>
					</div>
					<div class="col-md-8">
						<h3>Agenda</h3>
					</div>
					<div class="col-md-1 pt-2" align="right" >
						<b class="agp-19"><%=MeetingNo!=null?StringEscapeUtils.escapeHtml4(MeetingNo): " - " %></b>
					</div>
					<div class="col-md-1">
						<img class="agp-18"
							
							<%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>"
							alt="Logo" <%}else{ %> alt="File Not Found" <%} %>>
					</div>

				</div>

						<div class="content">

							<table class="subtables agp-27" >
								<thead>
									<tr>
										<td colspan="10">
											<p class="agp-29">
												<span class="assigned">AA</span> : Activity Assigned
												&nbsp;&nbsp; <span class="ongoing">OG</span> : On Going
												&nbsp;&nbsp; <span class="delay">DO</span> : Delay - On Going
												&nbsp;&nbsp; <span class="delay">FD</span> : Forwarded With
												Delay &nbsp;&nbsp;<span class="ongoing">RC</span> : Review & Close &nbsp;&nbsp; 
											</p>
										</td>
									</tr>
									<tr>
												<th class="std wid20" >SN</th>
												<th class="std wid60" >Action No</th>
												<th class="std wid300" >Action Point</th>
												<th class="std wid100" >PDC</th>
												<th class="std wid80" >Progress</th>
												<th class="std wid100" >Responsibility</th>
												<th class="std wid80" >Status<!-- (DD) --></th>
									</tr>
								</thead>

								<tbody>
									<%
									List<Object[]> projectOpenAction = openActionsMap.get(Long.parseLong(agenda[5].toString()));
									
									if(projectOpenAction!=null && projectOpenAction.size()>0) { 
										int count1=0;
										for(Object[] obj : projectOpenAction){ %>
											<tr>
												<td class="std agp30" align="center"
													><%=++count1 %>.
												</td>
												<td class="std agp-31" >
													<button class="btn btn-sm agp-32"    onclick="ActionDetails( <%=obj[0] %>);" ><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %> </button>		
												</td>
												<td class="std agp-33" >
													<%=obj[5]!=null?obj[5].toString(): " - "%>
												</td>
												<td class="std agp-34" ><%=sdf.format(sdf1.parse(StringEscapeUtils.escapeHtml4(obj[10].toString()))) %></td>
												<td class="std agp-34" ><%=obj[13]!=null?StringEscapeUtils.escapeHtml4(obj[13].toString()): " - " %></td>
												<td class="std agp-34" ><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></td>
												<td class="std">
													<%if(obj[4]!= null){ %> <%	String actionstatus = obj[3].toString();
																		int progress = obj[13]!=null ? Integer.parseInt(obj[13].toString()) : 0;
																		LocalDate pdcorg = LocalDate.parse(obj[10].toString());
																		LocalDate lastdate = obj[14]!=null ? LocalDate.parse(obj[14].toString()): null;
																		LocalDate today = LocalDate.now();
																	%> <%if(actionstatus.equals("F")  && (pdcorg.isAfter(lastdate) || pdcorg.isEqual(lastdate) )){ %>
													<span class="ongoing">RC</span> <%}else if(actionstatus.equals("F")  && pdcorg.isBefore(lastdate)) { %>
													<span class="delay">FD</span> <%}else if((pdcorg.isAfter(today) || pdcorg.isEqual(today)) && progress>0){  %>
													<span class="ongoing">OG</span> <%}else if(pdcorg.isBefore(today) && progress>0){  %>
													<span class="delay">DO <%-- (<%= ChronoUnit.DAYS.between(pdcorg, today)  %>)  --%>
													</span> <%}else if( progress==0) {%> <span class="assigned">AA</span> <%} %>
													<%}else { %> <span class="notassign">NA</span> <%} %>
												</td>
											</tr>
										<% }%>
									<%}else{ %>
										<tr>
											<td colspan="7" class="text-center">No Data Available</td>
										</tr>
									<%} %>
								</tbody>
							</table>
						</div>
					</div>
					
					<div class="carousel-item">

						<div class="content-header row ">

					<div class="col-md-1">
						<img class="agp-18"
							
							<%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>"
							alt="Logo" <%}else{ %> alt="File Not Found" <%} %>>
					</div>
					<div class="col-md-1 pt-2" align="left" >
						<b class="agp-19"><%=ProjectCode!=null?StringEscapeUtils.escapeHtml4(ProjectCode): " - " %></b>
					</div>
					<div class="col-md-8">
						<h3>Agenda</h3>
					</div>
					<div class="col-md-1 pt-2" align="right" >
						<b class="agp-19"><%=MeetingNo!=null?StringEscapeUtils.escapeHtml4(MeetingNo): " - " %></b>
					</div>
					<div class="col-md-1">
						<img class="agp-18"
							
							<%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>"
							alt="Logo" <%}else{ %> alt="File Not Found" <%} %>>
					</div>

				</div>

						<div class="content">

							<table class="subtables agp-27" >
								<thead>
									<tr>
										<td colspan="10" style="">
										<p class="agp-29">
									
										<span class="assigned">AA</span> : Activity Assigned
										&nbsp;&nbsp; <span class="ongoing">OG</span> : On Going
										&nbsp;&nbsp; <span class="delay">DO</span> : Delay - On Going
										&nbsp;&nbsp; <span class="delay">FD</span> : Forwarded With
										Delay &nbsp;&nbsp;<span class="ongoing">RC</span> : Review & Close &nbsp;&nbsp; 
									</p>
										</td>
		
									</tr>
									<tr>
										<th class="std wid20" >SN</th>
								<th class="std wid60" >Action No</th>
								<th class="std wid300" >Action Point</th>
								<th class="std wid100" >PDC</th>
								<th class="std wid80" >Progress</th>
								<th class="std wid100" >Responsibility</th>
								<th class="std wid80" >Status<!-- (DD) --></th>
									</tr>
								</thead>

								<tbody>
									<%
									List<Object[]> projectCloseAction = closeActionsMap.get(Long.parseLong(agenda[5].toString()));
									
									if(projectCloseAction!=null && projectCloseAction.size()>0) { 
										int count1=0;
										for(Object[] obj : projectCloseAction){ %>
											<tr>
												<td class="std agp30" align="center"
													><%=++count1 %>.
												</td>
												<td class="std agp-31" >
													<button class="btn btn-sm agp-31"   onclick="ActionDetails( <%=obj[0] %>);" ><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %> </button>		
												</td>
												<td class="std agp-33" >
													<%=obj[5]!=null?obj[5].toString(): " - " %>
												</td>
												<td class="std"><%=sdf.format(sdf1.parse(obj[10].toString())) %></td>
												<td class="std agp-34"><%=obj[13]!=null?StringEscapeUtils.escapeHtml4(obj[13].toString()): " - " %></td>
												<td class="std agp-34" ><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></td>
												<td class="std">
													<%if(obj[4]!= null){ %> <%	String actionstatus = obj[3].toString();
																		int progress = obj[13]!=null ? Integer.parseInt(obj[13].toString()) : 0;
																		LocalDate pdcorg = LocalDate.parse(obj[10].toString());
																		LocalDate lastdate = obj[14]!=null ? LocalDate.parse(obj[14].toString()): null;
																		LocalDate today = LocalDate.now();
																	%> <% if(lastdate!=null && actionstatus.equalsIgnoreCase("C") ){%>
													<%if(actionstatus.equals("C") && (pdcorg.isAfter(lastdate) || pdcorg.equals(lastdate))){%>
													<span class="completed">CO</span> <%}else if(actionstatus.equals("C") && pdcorg.isBefore(lastdate)){ %>
													<span class="completeddelay">CD <%-- (<%= ChronoUnit.DAYS.between(pdcorg, lastdate) %>)  --%></span>
													<%} %> <%}else{ %> <%if(actionstatus.equals("F")  && (pdcorg.isAfter(lastdate) || pdcorg.isEqual(lastdate) )){ %>
													<span class="ongoing">RC</span> <%}else if(actionstatus.equals("F")  && pdcorg.isBefore(lastdate)) { %>
													<span class="delay">FD</span> <%}else if((pdcorg.isAfter(today) || pdcorg.isEqual(today)) && progress>0){  %>
													<span class="ongoing">OG</span> <%}else if(pdcorg.isBefore(today) && progress>0){  %>
													<span class="delay">DO <%-- (<%= ChronoUnit.DAYS.between(pdcorg, today)  %>)  --%>
													</span> <%}else if( progress==0) {%> <span class="assigned">AA</span> <%} %>
													<%} %> <%}else { %> <span class="notassign">NA</span> <%} %>

												</td>
											</tr>
										<% }%>
									<%}else{ %>
										<tr>
											<td colspan="7" class="text-center">No Data Available</td>
										</tr>
									<%} %>
								</tbody>

							</table>

						</div>

					</div>
					
				<% }%>
				
				<div class="carousel-item">
				<div class="content-header row ">

					<div class="col-md-1">
						<img class="agp-18"
							
							<%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>"
							alt="Logo" <%}else{ %> alt="File Not Found" <%} %>>
					</div>
					<div class="col-md-1 pt-2" align="left" >
						<b class="agp-19"><%=ProjectCode!=null?StringEscapeUtils.escapeHtml4(ProjectCode): " - " %></b>
					</div>
					<div class="col-md-8">
						<h3>Agenda</h3>
					</div>
					<div class="col-md-1 pt-2" align="right" >
						<b class="agp-19"><%=MeetingNo!=null?StringEscapeUtils.escapeHtml4(MeetingNo): " - " %></b>
					</div>
					<div class="col-md-1">
						<img class="agp-18"
							
							<%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>"
							alt="Logo" <%}else{ %> alt="File Not Found" <%} %>>
					</div>

				</div>

					<div class="content">

						<table class="subtables agp-27">
							<thead>
								<tr>
									<td colspan="10" >
										<p class="agp-29">
											<span class="assigned">AA</span> : Activity Assigned
											&nbsp;&nbsp; <span class="ongoing">OG</span> : On Going
											&nbsp;&nbsp; <span class="delay">DO</span> : Delay - On Going
											&nbsp;&nbsp; <span class="delay">FD</span> : Forwarded With
											Delay &nbsp;&nbsp;<span class="ongoing">RC</span> : Review & Close &nbsp;&nbsp; 
										</p>
									</td>
								</tr>
								<tr>
									<th class="std wid20" >SN</th>
								<th class="std wid60" >Action No</th>
								<th class="std wid300" >Action Point</th>
								<th class="std wid100" >PDC</th>
								<th class="std wid80" >Progress</th>
								<th class="std wid100" >Responsibility</th>
								<th class="std wid80" >Status<!-- (DD) --></th>
								</tr>
							</thead>

							<tbody>
								<%
								List<Object[]> projectOpenAction = openActionsMap.get(0L);
								
								if(projectOpenAction!=null && projectOpenAction.size()>0) { 
									int count1=0;
									for(Object[] obj : projectOpenAction){ %>
										<tr>
											<td class="std agp30" align="center"
												><%=++count1 %>.
											</td>
											<td class="std agp-31">
												<button class="btn btn-sm agp-32"  onclick="ActionDetails( <%=obj[0] %>);" ><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %> </button>		
											</td>
											<td class="std agp-33" >
												<%=obj[5]!=null?obj[5].toString(): " - "%>
											</td>
											<td class="std agp-34" ><%=sdf.format(sdf1.parse(StringEscapeUtils.escapeHtml4(obj[10].toString()))) %></td>
											<td class="std agp-34" ><%=obj[13]!=null?StringEscapeUtils.escapeHtml4(obj[13].toString()): " - " %></td>
											<td class="std agp-34" ><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></td>
											<td class="std">
												<%if(obj[4]!= null){ %> <%	String actionstatus = obj[3].toString();
																	int progress = obj[13]!=null ? Integer.parseInt(obj[13].toString()) : 0;
																	LocalDate pdcorg = LocalDate.parse(obj[10].toString());
																	LocalDate lastdate = obj[14]!=null ? LocalDate.parse(obj[14].toString()): null;
																	LocalDate today = LocalDate.now();
																%> <%if(actionstatus.equals("F")  && (pdcorg.isAfter(lastdate) || pdcorg.isEqual(lastdate) )){ %>
												<span class="ongoing">RC</span> <%}else if(actionstatus.equals("F")  && pdcorg.isBefore(lastdate)) { %>
												<span class="delay">FD</span> <%}else if((pdcorg.isAfter(today) || pdcorg.isEqual(today)) && progress>0){  %>
												<span class="ongoing">OG</span> <%}else if(pdcorg.isBefore(today) && progress>0){  %>
												<span class="delay">DO <%-- (<%= ChronoUnit.DAYS.between(pdcorg, today)  %>)  --%>
												</span> <%}else if( progress==0) {%> <span class="assigned">AA</span> <%} %>
												<%}else { %> <span class="notassign">NA</span> <%} %>
											</td>
										</tr>
									<% }%>
								<%}else{ %>
									<tr>
										<td colspan="7" class="text-center">No Data Available</td>
									</tr>
								<%} %>
							</tbody>
						</table>
					</div>
				</div>
				
				<div class="carousel-item">

						<div class="content-header row ">

					<div class="col-md-1">
						<img class="agp-18"
							
							<%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>"
							alt="Logo" <%}else{ %> alt="File Not Found" <%} %>>
					</div>
					<div class="col-md-1 pt-2" align="left" >
						<b class="agp-19"><%=ProjectCode!=null?StringEscapeUtils.escapeHtml4(ProjectCode): " - " %></b>
					</div>
					<div class="col-md-8">
						<h3>Agenda</h3>
					</div>
					<div class="col-md-1 pt-2" align="right" >
						<b class="agp-19"><%=MeetingNo!=null?StringEscapeUtils.escapeHtml4(MeetingNo): " - " %></b>
					</div>
					<div class="col-md-1">
						<img class="agp-18"
							
							<%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>"
							alt="Logo" <%}else{ %> alt="File Not Found" <%} %>>
					</div>

				</div>

					<div class="content">

						<table class="subtables agp-27" >
							<thead>
								<tr>
									<td colspan="10" >
										<p class="agp-29">
											<span class="completed">CO</span> :Completed &nbsp;&nbsp; <span
												class="completeddelay">CD</span> : Completed with Delay
											&nbsp;&nbsp;
										</p>
									</td>
	
								</tr>
								<tr>
									<th class="std wid20" >SN</th>
								<th class="std wid60" >Action No</th>
								<th class="std wid300" >Action Point</th>
								<th class="std wid100" >PDC</th>
								<th class="std wid80" >Progress</th>
								<th class="std wid100" >Responsibility</th>
								<th class="std wid80" >Status<!-- (DD) --></th>
								</tr>
							</thead>

							<tbody>
								<%
								List<Object[]> projectCloseAction = closeActionsMap.get(0L);
								
								if(projectCloseAction!=null && projectCloseAction.size()>0) { 
									int count1=0;
									for(Object[] obj : projectCloseAction){ %>
										<tr>
											<td class="std agp30" align="center"
												><%=++count1 %>.
											</td>
											<td class="std agp-31" >
												<button class="btn btn-sm agp-31"   onclick="ActionDetails( <%=obj[0] %>);" ><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %> </button>		
											</td>
											<td class="std agp-33" >
												<%=obj[5]!=null?obj[5].toString(): " - " %>
											</td>
											<td class="std"><%=sdf.format(sdf1.parse(obj[10].toString())) %></td>
											<td class="std agp-34" ><%=obj[13]!=null?StringEscapeUtils.escapeHtml4(obj[13].toString()): " - " %></td>
											<td class="std agp-34" ><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></td>
											<td class="std">
												<%if(obj[4]!= null){ %> <%	String actionstatus = obj[3].toString();
																	int progress = obj[13]!=null ? Integer.parseInt(obj[13].toString()) : 0;
																	LocalDate pdcorg = LocalDate.parse(obj[10].toString());
																	LocalDate lastdate = obj[14]!=null ? LocalDate.parse(obj[14].toString()): null;
																	LocalDate today = LocalDate.now();
																%> <% if(lastdate!=null && actionstatus.equalsIgnoreCase("C") ){%>
												<%if(actionstatus.equals("C") && (pdcorg.isAfter(lastdate) || pdcorg.equals(lastdate))){%>
												<span class="completed">CO</span> <%}else if(actionstatus.equals("C") && pdcorg.isBefore(lastdate)){ %>
												<span class="completeddelay">CD <%-- (<%= ChronoUnit.DAYS.between(pdcorg, lastdate) %>)  --%></span>
												<%} %> <%}else{ %> <%if(actionstatus.equals("F")  && (pdcorg.isAfter(lastdate) || pdcorg.isEqual(lastdate) )){ %>
												<span class="ongoing">RC</span> <%}else if(actionstatus.equals("F")  && pdcorg.isBefore(lastdate)) { %>
												<span class="delay">FD</span> <%}else if((pdcorg.isAfter(today) || pdcorg.isEqual(today)) && progress>0){  %>
												<span class="ongoing">OG</span> <%}else if(pdcorg.isBefore(today) && progress>0){  %>
												<span class="delay">DO <%-- (<%= ChronoUnit.DAYS.between(pdcorg, today)  %>)  --%>
												</span> <%}else if( progress==0) {%> <span class="assigned">AA</span> <%} %>
												<%} %> <%}else { %> <span class="notassign">NA</span> <%} %>

											</td>
										</tr>
									<% }%>
								<%}else{ %>
									<tr>
										<td colspan="7" class="text-center">No Data Available</td>
									</tr>
								<%} %>
							</tbody>

						</table>

					</div>

				</div>	
			<% }%>
			

			<div class="carousel-item">

			<div class="content-header row ">

					<div class="col-md-1">
						<img class="agp-18"
							
							<%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>"
							alt="Logo" <%}else{ %> alt="File Not Found" <%} %>>
					</div>
					<div class="col-md-1 pt-2" align="left" >
						<b class="agp-19"><%=ProjectCode!=null?StringEscapeUtils.escapeHtml4(ProjectCode): " - " %></b>
					</div>
					<div class="col-md-8">
						<h3>Agenda</h3>
					</div>
					<div class="col-md-1 pt-2" align="right" >
						<b class="agp-19"><%=MeetingNo!=null?StringEscapeUtils.escapeHtml4(MeetingNo): " - " %></b>
					</div>
					<div class="col-md-1">
						<img class="agp-18"
							
							<%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>"
							alt="Logo" <%}else{ %> alt="File Not Found" <%} %>>
					</div>

				</div>

				<div class="content">

					<table class="subtables agp-27"
						>
						<thead>
							<tr>
								<td colspan="10" >
									<p class="agp-29">
										<span class="completed">CO</span> :Completed &nbsp;&nbsp; <span
											class="completeddelay">CD</span> : Completed with Delay
										&nbsp;&nbsp;
									</p>
								</td>

							</tr>
							<tr>
							<th class="std wid20" >SN</th>
								<th class="std wid60" >Action No</th>
								<th class="std wid300" >Action Point</th>
								<th class="std wid100" >PDC</th>
								<th class="std wid80" >Progress</th>
								<th class="std wid100" >Responsibility</th>
								<th class="std wid80" >Status<!-- (DD) --></th>
							</tr>
						</thead>

						<tbody>
							<%if(closeActionDetails  .size()>0) { %>
							<%int count1=0;String key="";;
								for(Object[]obj:closeActionDetails){ %>
							<tr>
								<td class="std agp30" align="center"
									><%=++count1 %>.
								</td>
									<td class="std agp-31"
									>
							<button class="btn btn-sm agp-31"   onclick="ActionDetails( <%=obj[0] %>);" ><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %> </button>		
								</td>
								<td class="std agp-33" >
									<%=obj[5]!=null?obj[5].toString(): " - " %>
								</td>
								<td class="std"><%=sdf.format(sdf1.parse(obj[10].toString())) %></td>
								<td class="std agp-34" ><%=obj[13]!=null?StringEscapeUtils.escapeHtml4(obj[13].toString()): " - " %></td>
								<td class="std agp-34" ><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></td>
								<td class="std">
									<%if(obj[4]!= null){ %> <%	String actionstatus = obj[3].toString();
														int progress = obj[13]!=null ? Integer.parseInt(obj[13].toString()) : 0;
														LocalDate pdcorg = LocalDate.parse(obj[10].toString());
														LocalDate lastdate = obj[14]!=null ? LocalDate.parse(obj[14].toString()): null;
														LocalDate today = LocalDate.now();
													%> <% if(lastdate!=null && actionstatus.equalsIgnoreCase("C") ){%>
									<%if(actionstatus.equals("C") && (pdcorg.isAfter(lastdate) || pdcorg.equals(lastdate))){%>
									<span class="completed">CO</span> <%}else if(actionstatus.equals("C") && pdcorg.isBefore(lastdate)){ %>
									<span class="completeddelay">CD <%-- (<%= ChronoUnit.DAYS.between(pdcorg, lastdate) %>)  --%></span>
									<%} %> <%}else{ %> <%if(actionstatus.equals("F")  && (pdcorg.isAfter(lastdate) || pdcorg.isEqual(lastdate) )){ %>
									<span class="ongoing">RC</span> <%}else if(actionstatus.equals("F")  && pdcorg.isBefore(lastdate)) { %>
									<span class="delay">FD</span> <%}else if((pdcorg.isAfter(today) || pdcorg.isEqual(today)) && progress>0){  %>
									<span class="ongoing">OG</span> <%}else if(pdcorg.isBefore(today) && progress>0){  %>
									<span class="delay">DO <%-- (<%= ChronoUnit.DAYS.between(pdcorg, today)  %>)  --%>
								</span> <%}else if( progress==0) {%> <span class="assigned">AA</span> <%} %>
									<%} %> <%}else { %> <span class="notassign">NA</span> <%} %>



								</td>
							</tr>
							<% }%>
							<%}else{ %>
							<tr>
								<td colspan="7"class="text-center">No Data Available</td>
							</tr>
							<%} %>
						</tbody>

					</table>

				</div>

			</div>


			<div class="carousel-item">

				<div class="content-header row ">

					<div class="col-md-1">
						<img class="agp-18"
							
							<%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>"
							alt="Logo" <%}else{ %> alt="File Not Found" <%} %>>
					</div>
					<div class="col-md-1 pt-2" align="left" >
						<b class="agp-19"><%=ProjectCode!=null?StringEscapeUtils.escapeHtml4(ProjectCode): " - " %></b>
					</div>
					<div class="col-md-8">
						<h3>Agenda</h3>
					</div>
					<div class="col-md-1 pt-2" align="right" >
						<b class="agp-19"><%=MeetingNo!=null?StringEscapeUtils.escapeHtml4(MeetingNo): " - " %></b>
					</div>
					<div class="col-md-1">
						<img class="agp-18"
							
							<%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>"
							alt="Logo" <%}else{ %> alt="File Not Found" <%} %>>
					</div>

				</div>

				<div class="content">

					<table class="subtables agp-27"
						>
						<thead>
							<tr>
								<td colspan="10" >
									<p class="agp-29">
										<span class="completed">CO</span> :Completed &nbsp;&nbsp; <span
											class="completeddelay">CD</span> : Completed with Delay
										&nbsp;&nbsp; <span class="assigned">AA</span> : Activity
										Assigned &nbsp;&nbsp; <span class="ongoing">OG</span> : On
										Going &nbsp;&nbsp; <span class="delay">DO</span> : Delay - On
										Going &nbsp;&nbsp; <span class="delay">FD</span> : Forwarded
										With Delay &nbsp;&nbsp;<span class="ongoing">RC</span> : Review & Close &nbsp;&nbsp; 

									</p>
								</td>

							</tr>
							<tr>
								<th class="std wid20" >SN</th>
								<th class="std wid60" >Action No</th>
								<th class="std wid300" >Action Point</th>
								<th class="std wid100" >PDC</th>
								<th class="std wid80" >Progress</th>
								<th class="std wid100" >Responsibility</th>
								<th class="std wid80" >Status<!-- (DD) --></th>
							</tr>
						</thead>

						<tbody>
							<%if(ActionDetails  .size()>0) { %>
							<%int count1=0;String key="";;
								for(Object[]obj:ActionDetails){ %>
							<tr>
								<td class="std agp30" align="center"
									><%=++count1 %>.
								</td>
								<td class="std agp-31"
									>
							<button class="btn btn-sm agp-32"  onclick="ActionDetails( <%=obj[0] %>);" ><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %> </button>		
								</td>
								<td class="std agp-33" >
									<%=obj[5]!=null?obj[5].toString(): " - " %>
								</td>
								<td class="std"><%=sdf.format(sdf1.parse(obj[10].toString())) %></td>
								<td class="std agp-34"><%=obj[13]!=null?StringEscapeUtils.escapeHtml4(obj[13].toString()): " - " %></td>
								<td class="std agp-34" ><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></td>
								<td class="std">
									<%if(obj[4]!= null){ %> <%	String actionstatus = obj[3].toString();
														int progress = obj[13]!=null ? Integer.parseInt(obj[13].toString()) : 0;
														LocalDate pdcorg = LocalDate.parse(obj[10].toString());
														LocalDate lastdate = obj[14]!=null ? LocalDate.parse(obj[14].toString()): null;
														LocalDate today = LocalDate.now();
													%> <% if(lastdate!=null && actionstatus.equalsIgnoreCase("C") ){%>
									<%if(actionstatus.equals("C") && (pdcorg.isAfter(lastdate) || pdcorg.equals(lastdate))){%>
									<span class="completed">CO</span> <%}else if(actionstatus.equals("C") && pdcorg.isBefore(lastdate)){ %>
									<span class="completeddelay">CD <%-- (<%= ChronoUnit.DAYS.between(pdcorg, lastdate) %>)  --%></span>
									<%} %> <%}else{ %> <%if(actionstatus.equals("F")  && (pdcorg.isAfter(lastdate) || pdcorg.isEqual(lastdate) )){ %>
									<span class="ongoing">RC</span> <%}else if(actionstatus.equals("F")  && pdcorg.isBefore(lastdate)) { %>
									<span class="delay">FD</span> <%}else if((pdcorg.isAfter(today) || pdcorg.isEqual(today)) && progress>0){  %>
									<span class="ongoing">OG</span> <%}else if(pdcorg.isBefore(today) && progress>0){  %>
									<span class="delay">DO <%-- (<%= ChronoUnit.DAYS.between(pdcorg, today)  %>)  --%>
								</span> <%}else if( progress==0) {%> <span class="assigned">AA</span> <%} %>
									<%} %> <%}else { %> <span class="notassign">NA</span> <%} %>



								</td>
							</tr>
							<% }%>
							<%}else{ %>
							<tr>
								<td colspan="7" class="text-center">No Data Available</td>
							</tr>
							<%} %>
						</tbody>

					</table>






				</div>

			</div>
			
			
						<div class="carousel-item">

				<div class="content-header row ">

					<div class="col-md-1">
						<img class="agp-18"
							
							<%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>"
							alt="Logo" <%}else{ %> alt="File Not Found" <%} %>>
					</div>
					<div class="col-md-1 pt-2" align="left" >
						<b class="agp-19"><%=ProjectCode!=null?StringEscapeUtils.escapeHtml4(ProjectCode): " - " %></b>
					</div>
					<div class="col-md-8">
						<h3>Agenda</h3>
					</div>
					<div class="col-md-1 pt-2" align="right" >
						<b class="agp-19"><%=MeetingNo!=null?StringEscapeUtils.escapeHtml4(MeetingNo): " - " %></b>
					</div>
					<div class="col-md-1">
						<img class="agp-18"
							
							<%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>"
							alt="Logo" <%}else{ %> alt="File Not Found" <%} %>>
					</div>

				</div>

				<div class="content">

					<table class="subtables agp-27"
						>
						<thead>
							<tr>
								<td colspan="10">
									<p class="agp-29">
										<span class="completed">CO</span> :Completed &nbsp;&nbsp; <span
											class="completeddelay">CD</span> : Completed with Delay
										&nbsp;&nbsp; <span class="assigned">AA</span> : Activity
										Assigned &nbsp;&nbsp; <span class="ongoing">OG</span> : On
										Going &nbsp;&nbsp; <span class="delay">DO</span> : Delay - On
										Going &nbsp;&nbsp; <span class="delay">FD</span> : Forwarded
										With Delay &nbsp;&nbsp;<span class="ongoing">RC</span> : Review & Close &nbsp;&nbsp; 

									</p>
								</td>

							</tr>
							<tr>
								<th class="std wid20" >SN</th>
								<th class="std wid60" >Action No</th>
								<th class="std wid300" >Action Point</th>
								<th class="std wid100" >PDC</th>
								<th class="std wid80" >Progress</th>
								<th class="std wid100" >Responsibility</th>
								<th class="std wid80" >Status<!-- (DD) --></th>
							</tr>
						</thead>

						<tbody>
							<%if(recommendationList  .size()>0) { %>
							<%int count1=0;String key="";;
								for(Object[]obj:recommendationList){ %>
							<tr>
								<td class="std agp30" align="center"
									><%=++count1 %>.
								</td>
								<td class="std agp-31"
									>
							<button class="btn btn-sm agp-32"   onclick="ActionDetails( <%=obj[0] %>);" ><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %> </button>		
								</td>
								<td class="std agp-33" >
									<%=obj[5]!=null?obj[5].toString(): " - " %>
								</td>
								<td class="std"><%=sdf.format(sdf1.parse(obj[10].toString())) %></td>
								<td class="std agp-34" ><%=obj[13]!=null?StringEscapeUtils.escapeHtml4(obj[13].toString()): " - " %></td>
								<td class="std agp-34" ><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></td>
								<td class="std">
									<%if(obj[4]!= null){ %> <%	String actionstatus = obj[3].toString();
														int progress = obj[13]!=null ? Integer.parseInt(obj[13].toString()) : 0;
														LocalDate pdcorg = LocalDate.parse(obj[10].toString());
														LocalDate lastdate = obj[14]!=null ? LocalDate.parse(obj[14].toString()): null;
														LocalDate today = LocalDate.now();
													%> <% if(lastdate!=null && actionstatus.equalsIgnoreCase("C") ){%>
									<%if(actionstatus.equals("C") && (pdcorg.isAfter(lastdate) || pdcorg.equals(lastdate))){%>
									<span class="completed">CO</span> <%}else if(actionstatus.equals("C") && pdcorg.isBefore(lastdate)){ %>
									<span class="completeddelay">CD <%-- (<%= ChronoUnit.DAYS.between(pdcorg, lastdate) %>)  --%></span>
									<%} %> <%}else{ %> <%if(actionstatus.equals("F")  && (pdcorg.isAfter(lastdate) || pdcorg.isEqual(lastdate) )){ %>
									<span class="ongoing">RC</span> <%}else if(actionstatus.equals("F")  && pdcorg.isBefore(lastdate)) { %>
									<span class="delay">FD</span> <%}else if((pdcorg.isAfter(today) || pdcorg.isEqual(today)) && progress>0){  %>
									<span class="ongoing">OG</span> <%}else if(pdcorg.isBefore(today) && progress>0){  %>
									<span class="delay">DO <%-- (<%= ChronoUnit.DAYS.between(pdcorg, today)  %>)  --%>
								</span> <%}else if( progress==0) {%> <span class="assigned">AA</span> <%} %>
									<%} %> <%}else { %> <span class="notassign">NA</span> <%} %>



								</td>
							</tr>
							<% }%>
							<%}else{ %>
							<tr>
								<td colspan="7"class="text-center">No Data Available</td>
							</tr>
							<%} %>
						</tbody>

					</table>






				</div>

			</div>
			
			
					<div class="carousel-item">

			<div class="content-header row ">

					<div class="col-md-1">
						<img class="agp-18"
							
							<%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>"
							alt="Logo" <%}else{ %> alt="File Not Found" <%} %>>
					</div>
					<div class="col-md-1 pt-2" align="left" >
						<b class="agp-19"><%=ProjectCode!=null?StringEscapeUtils.escapeHtml4(ProjectCode): " - " %></b>
					</div>
					<div class="col-md-8">
						<h3>Agenda</h3>
					</div>
					<div class="col-md-1 pt-2" align="right" >
						<b class="agp-19"><%=MeetingNo!=null?StringEscapeUtils.escapeHtml4(MeetingNo): " - " %></b>
					</div>
					<div class="col-md-1">
						<img class="agp-18"
							
							<%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>"
							alt="Logo" <%}else{ %> alt="File Not Found" <%} %>>
					</div>

				</div>

				<div class="content">

					<table class="subtables agp-27"
				>
						<thead>
					<!-- 		<tr>
								<td colspan="10" style="border: 0">
									<p style="font-size: 12px; text-align: center">
										<span class="completed">CO</span> :Completed &nbsp;&nbsp; <span
											class="completeddelay">CD</span> : Completed with Delay
										&nbsp;&nbsp; <span class="assigned">AA</span> : Activity
										Assigned &nbsp;&nbsp; <span class="ongoing">OG</span> : On
										Going &nbsp;&nbsp; <span class="delay">DO</span> : Delay - On
										Going &nbsp;&nbsp; <span class="delay">FD</span> : Forwarded
										With Delay &nbsp;&nbsp;

									</p>
								</td>

							</tr> -->
							<tr>
								<th class="std wid20" >SN</th>
								<th class="std wid300" >Details</th>
							</tr>
						</thead>
						<tbody>
						<%if(decesions!=null &&  !decesions.isEmpty() ){
						int decCount = 0;
							for(Object[]obj:decesions){	
						%>
						<tr>
						<td class="text-center"><%=++decCount %>.</td>
						<td><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %></td>
						</tr>
						<%}}else{ %>
						<tr>
						<td colspan="2"> No Details Available ! </td>
						</tr>
						<%} %>
						</tbody>

			

					</table>






				</div>

			</div>
			<!-- ----------------------------------------  P-2  Div ----------------------------------------------------- -->


		</div>

		<a class="carousel-control-prev agp-35" href="#presentation-slides"
			role="button" data-slide="prev"
			> <span aria-hidden="true">
				<i class="fa fa-chevron-left fa-2x agp-36"
				aria-hidden="true"></i>
		</span> <span class="sr-only">Previous</span>
		</a> <a class="carousel-control-next agp-37" href="#presentation-slides"
			role="button" data-slide="next"
			> <span aria-hidden="true">
				<i class="fa fa-chevron-right fa-2x agp-36" 
				aria-hidden="true"></i>
		</span> <span class="sr-only">Next</span>
		</a>

		<%int slidecount = 0; %>
		<ol class="carousel-indicators">
			<li data-target="#presentation-slides" data-slide-to="0"
				class="carousel-indicator active" data-toggle="tooltip"
				data-placement="top" title="Start"><b><i class="fa fa-home"
					aria-hidden="true"></i></b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=++slidecount %>"
				class="carousel-indicator" data-toggle="tooltip"
				data-placement="top" title="Agenda"><b>1</b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=++slidecount %>"
				class="carousel-indicator" data-toggle="tooltip"
				data-placement="top" title="Previous Meetings"><b>2</b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=++slidecount %>"
				class="carousel-indicator" data-toggle="tooltip"
				data-placement="top" title="Open Action Points"><b>3</b></li>
			<%int sn = 0;
			if(programmeId!=null && !programmeId.isEmpty() && Long.parseLong(programmeId)>0 && !AgendaList.isEmpty()) {
				for(Object[] obj : AgendaList) {%>
					<li data-target="#presentation-slides" data-slide-to="<%=++slidecount %>"
					class="carousel-indicator" data-toggle="tooltip"
					data-placement="top" title="OPA <%=obj[7]%>"><b>3.<%=++sn %> (a)</b></li>
					
					<li data-target="#presentation-slides" data-slide-to="<%=++slidecount %>"
					class="carousel-indicator" data-toggle="tooltip"
					data-placement="top" title="CAP <%=obj[7]%>"><b>3.<%=sn %> (b)</b></li>
			<% }%>
			<li data-target="#presentation-slides" data-slide-to="<%=++slidecount %>"
			class="carousel-indicator" data-toggle="tooltip"
			data-placement="top" title="OPA Others"><b>3.<%=++sn %> (a)</b></li>
			
			<li data-target="#presentation-slides" data-slide-to="<%=++slidecount %>"
			class="carousel-indicator" data-toggle="tooltip"
			data-placement="top" title="CAP Others"><b>3.<%=sn %> (b)</b></li>
			
			<% }%>		
			<li data-target="#presentation-slides" data-slide-to="<%=++slidecount %>"
				class="carousel-indicator" data-toggle="tooltip"
				data-placement="top" title="Close Action Points"><b>4</b></li>
			<%-- <%
			if(programmeId!=null && !programmeId.isEmpty() && Long.parseLong(programmeId)>0 && !AgendaList.isEmpty()) {
				int sn = 0;
				for(Object[] obj : AgendaList) {%>
					<li data-target="#presentation-slides" data-slide-to="<%=++slidecount %>"
					class="carousel-indicator" data-toggle="tooltip"
					data-placement="top" title="CAP <%=obj[7]%>"><b>4.<%=++sn %></b></li>
			<%} }%> --%>
			<li data-target="#presentation-slides" data-slide-to="<%=++slidecount %>"
				class="carousel-indicator" data-toggle="tooltip"
				data-placement="top" title="All Action Points"><b>5</b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=++slidecount %>"
				class="carousel-indicator" data-toggle="tooltip"
				data-placement="top" title="All Recommendation Points"><b>6</b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=++slidecount %>"
				class="carousel-indicator" data-toggle="tooltip"
				data-placement="top" title="All Decesions"><b>7</b></li>
		</ol>
	</div>
	
	
	
	<!--  Action Modal   -->

	<div class=" modal bd-example-modal-lg" tabindex="-1" role="dialog" id="action_modal">
		<div class="modal-dialog modal-lg modal-dialog-jump" role="document">
			<div class="modal-content">
				<div class="modal-header agp-38" >
					<div class="row w-100"  >
						<div class="col-md-12" >
							<h5 class="modal-title agp-39" id="modal_action_no" ></h5>
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
								<td class="width-20 agp-40"> Action Item :</td>
								<td class="tabledata width-80 agp-41" scolspan="3" id="modal_action_item"></td>
							</tr>
							<tr>
								<td class="agp-42" >Assign Date :</td>
								<td class="agp-43" id="modal_action_date"></td>
								<td class="agp-42" >PDC :</td>
								<td class="agp-43" id="modal_action_PDC"></td>
							</tr>
							<tr>
								<td class="agp-42" >Assignor :</td>
								<td class="agp-43" class="tabledata" id="modal_action_assignor"></td>
								<td class="agp-42" >Assignee :</td>
								<td class="agp-43" class="tabledata" id="modal_action_assignee"></td>
							</tr>
							<tr>
								<td class="agp-42" >Final Progress :</td>
								<td class="agp-43" id="modal_action_progress"></td>
								<td class="agp-42" > Type :</td>
								<td class="agp-44" id="modal_action_type"></td>
							</tr>
							
						</table>
						</form>
						<hr>
						<form action="#" method="get">
						
						<table class="table table-bordered table-hover table-striped table-condensed width-100" id="" >
							<thead> 
								<tr class="agp-45">
									<th class="text-center width-5" >SN</th>
									<th class="text-center width-15" >Progress Date</th>
									<th class="text-center width-15" > Progress</th>
									<th class="width-60" ">Remarks</th>
									<th class="text-center width-5" >Download</th>
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
		<!--  -->
	<script type="text/javascript">

$('.carousel').carousel({
	  interval: false,
	  keyboard: true,
	})

$(function () {
$('[data-toggle="tooltip"]').tooltip()
})





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
					var progressBar ='<div class="progress agp-46">'; 
					progressBar += 		'<div class="progress-bar agp-47" role="progressbar"   >';
					progressBar +=		'Not Started'
					progressBar +=		'</div>'; 
					progressBar += '</div>'; 
				}
				else
				{
					var progressBar ='<div class="progress agp-46" ">'; 
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
						htmlStr += '<td class="tabledata text-center"   >'+ moment(new Date(result[v][3]) ).format('DD-MM-YYYY') + '</td>';
						htmlStr += '<td class="tabledata text-center"  >'+ result[v][2] + ' %</td>';
						htmlStr += '<td class="tabledata text-center" >'+ result[v][4] + '</td>';
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
</script>



</body>
</html>