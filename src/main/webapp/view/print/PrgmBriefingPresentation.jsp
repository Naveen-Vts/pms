<%@page import="java.util.Locale"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.time.temporal.ChronoUnit"%>
<%@page import="java.time.LocalDate"%>
<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="com.vts.pfms.model.LabMaster"%>
<%@page import="com.vts.pfms.committee.model.Committee"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="shortcut icon" type="image/png" href="view/images/drdologo.png">
<jsp:include page="../static/dependancy.jsp"></jsp:include>
<spring:url value="/resources/css/slides-style.css" var="SlidesStyleCSS" />
<link href="${SlidesStyleCSS}" rel="stylesheet" />
<title>Briefing Presentation</title>
<style type="text/css">
.main-row {
    cursor: pointer;
}
.main-row:hover {
    background-color: #e9ecef;
}
.arrow {
    display: inline-block;
    transition: transform 0.3s;
}
.arrow.rotate {
    transform: rotate(180deg);
}

.sublist-row {
    display: none;
    background: #fff;
}
.sublist-table th, .sublist-table td {
    font-size: 13px;
    vertical-align: middle;
}
</style>
</head>
<body style="background-color: #F9F2DF66;" class="slides-container" id="slides-container">

	<%
		FormatConverter fc = new FormatConverter();
		SimpleDateFormat inputFormat = new SimpleDateFormat("ddMMMyyyy",Locale.ENGLISH);
	    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");	
	    LocalDate today = LocalDate.now();
	    
		Committee committee = (Committee) request.getAttribute("committeeData");
		Object[] committeeMetingsCount =  (Object[]) request.getAttribute("committeeMetingsCount");
		LabMaster labInfo=(LabMaster)request.getAttribute("labInfo");
		Object[] nextMeetVenue =  (Object[]) request.getAttribute("nextMeetVenue");
		Object[] projectDetails =  (Object[]) request.getAttribute("projectDetails");
		
		
		List<Object[]> milestoneMainList = (List<Object[]>)request.getAttribute("milestoneMainList");
		Map<String, List<Object[]>> milestoneSubListMap = (Map<String, List<Object[]>>) request.getAttribute("milestoneSubListMap");
		
		Map<Long, List<Object[]>> milestoneProgressListMap = (Map<Long, List<Object[]>>) request.getAttribute("milestoneProgressListMap");

		List<Object[]> milestoneOpenActionList = (List<Object[]>)request.getAttribute("milestoneOpenActionList");
		
		Map<String, List<Object[]>> reviewMeetingListMap = (Map<String, List<Object[]>>) request.getAttribute("reviewMeetingListMap");
		List<Object[]> otherMeetingList = (List<Object[]>)request.getAttribute("otherMeetingList");
		
		Map<String, List<Object[]>> lastMeetingActionsListMap = (Map<String, List<Object[]>>) request.getAttribute("lastMeetingActionsListMap");
		Map<Integer,String> committeeWiseMap=(Map<Integer,String>)request.getAttribute("committeeWiseMap");
		
		String projectid = (String)request.getAttribute("projectid");
		String lablogo = (String)request.getAttribute("lablogo");
		String Drdologo = (String)request.getAttribute("Drdologo");
		String thankYouImg = (String)request.getAttribute("thankYouImg");
		
		String CommitteeCode = committee.getCommitteeShortName().trim();
		String MeetingNo = CommitteeCode+" #"+(Long.parseLong(committeeMetingsCount[1].toString())+1);
		String ProjectCode = projectDetails!=null?projectDetails[1].toString():"-";
		String mainpdc = projectDetails!=null? "PDC:"+fc.sdfTordf(projectDetails[6].toString()) :"-"; 
	%>
	<div id="presentation-slides" class="carousel slide " data-ride="carousel">

		<div class="carousel-inner" align="center">
			
			<!-- ---------------------------------------- P-0 Div ----------------------------------------------------- -->
			<div class="carousel-item active">
				<div class="content" align="center" style="height:93vh !important;padding-top: 15px;">
					<div class="firstpage"  > 
						<div align="center" ><h2 style="color: #145374 !important;font-family: 'Muli'!important">Presentation</h2></div>
						<div align="center" ><h3 style="color: #145374 !important">for</h3></div>
						<div align="center" >
							<h3 style="color: #4C9100 !important" ><%=CommitteeCode %> #<%=Long.parseLong(committeeMetingsCount[1].toString())+1 %> Meeting </h3>
				   		</div>
						<div align="center" >
							<h3 style="color: #4C9100 !important">
								<%= projectDetails[3] %> (<%=projectDetails[1] %>)
								<%-- <%if(projectattributeslist.size()>1) {
									for(int item=1;item<projectattributeslist.size();item++){ %> <br>
									<span style="font-size: 1rem;"><%= projectattributeslist.get(item)[1] %> (<%= projectattributeslist.get(item)[0] %>) (SUB)</span>
							 	<%}} %> --%>
							</h3>
						</div> 
						
						<table class="executive home-table" style="align: center; margin-left: auto;margin-right:auto;border:0px;  font-size: 16px;"  >
							<tr>			
								<th colspan="8" style="text-align: center; font-weight: 700;">
									<img class="logo" style="width:120px;height: 120px;x"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> > 
									<br>
								</th>
								</tr>
						</table>	
						<% if(nextMeetVenue!=null){ %>
							<table style="align: center;width: 650px;  "  >
								<tr style="margin-top: 10px">
									 <th  style="text-align: center;font-size: 18px;border:0px !important; "> <u>Meeting Id </u> </th></tr><tr>
									 <th  style="text-align: center;font-size: 18px;border:0px !important;  "> <%=nextMeetVenue[1] %> </th>				
								 </tr>
							</table>
							<table style="align: left; width: 650px; "  >
								<tr>
									 <th  style="text-align: center; width: 50%;font-size: 18px;border:0px !important; "> <u> Meeting Date </u></th>
									 <th  style="text-align: center;  width: 50%;font-size: 18px;border:0px !important;  "><u> Meeting Time </u></th>
								</tr>
								<tr>
									<td  style="text-align: center; width: 50%;font-size: 18px ;padding-top: 5px;border:0px !important;"> <b><%=fc.sdfTordf(nextMeetVenue[2].toString())%></b></td>
									<td  style="text-align: center; width: 50%;font-size: 18px ;padding-top: 5px;border:0px !important; "> <b><%=nextMeetVenue[3]/* starttime.format( DateTimeFormatter.ofPattern("hh:mm a") ) */%></b></td>
								</tr>
							</table>
							<table style="align: center; width: 650px;"  >
								<tr style="margin-top: 10px">
									<th  style="text-align: center;font-size: 18px;border:0px !important; "> <u>Meeting Venue</u> </th></tr><tr>
									<th  style="text-align: center;;font-size: 18px ;border:0px !important; "> <% if(nextMeetVenue[5]!=null){ %><%=nextMeetVenue[5] %> <%}else{ %> - <%} %></th>				
								 </tr>
							</table>
						<%}else{ %>
							<br><br><br><br><br><br><br><br><br>
						<%} %>
						
						<table class="executive home-table" style="align: center;margin-bottom:5px; margin-left: auto;margin-right:auto;border:0px;  font-size: 16px;"  >
							<% if(labInfo!=null){ %>
								<tr>
									<th colspan="8" style="text-align: center; font-weight: 700;font-size: 22px"><%if(labInfo.getLabName()!=null){ %><%=labInfo.getLabName()  %><%}else{ %>LAB NAME<%} %></th>
								</tr>
							<%}%>
							<tr>
								<th colspan="8" style="text-align: center; font-weight: 700;font-size:15px"><br>Government of India, Ministry of Defence</th>
							</tr>
							<tr>
								<th colspan="8" style="text-align: center; font-weight: 700;font-size:15px">Defence Research & Development Organization</th>
							</tr>
							<tr>
								<th colspan="8" style="text-align: center; font-weight: 700;font-size:15px"><%if(labInfo.getLabAddress() !=null){ %><%=labInfo.getLabAddress()  %> , <%=labInfo.getLabCity() %><%}else{ %>LAB ADDRESS<%} %> </th>
							</tr>
						</table>			
					</div>
				</div>
			</div>
			<!-- ---------------------------------------- P-0 End ----------------------------------------------------- -->
			
			<!-- ---------------------------------------- Milestone List ----------------------------------------------------- -->
			<div class="carousel-item ">
				<div class="content-header row ">
					<div class="col-md-1" >
						<img class="logo" style="width: 45px;margin-left: 5px;margin-top: -2px;"  <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					<div class="col-md-1" align="left" style="padding-top:5px;" ><b style="margin-left: -35px;"><%=ProjectCode%></b><h6 style="font-size: 0.8rem;margin-left: -43px;"><%=mainpdc %></h6>
					</div>
					<div class="col-md-8"><h3>1. Milestone List</h3></div>
					<div class="col-md-1" align="right"  style="padding-top:19px;" ><b style="margin-right: -35px;"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="logo" style="width: 45px;margin-left: 5px;margin-top: -2px;"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
				</div>
				<div class="content">
					<div class="row mt-2">
						<div class="col-md-12">
							<div id="milestoneContainer">
								<table class=" align-middle" style="width: 100%;">
							    	<thead class="center">
							    		<tr>
											<td colspan="10" style="border: 0">
												<p style="font-size: 12px; text-align: center">
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
							                <th width="5%"></th>
							                <th width="11%">Activity Id</th>
							                <th width="25%">Activity Name</th>
							                <th width="8%">Start Date</th>
							                <th width="8%">End Date</th>
							                <th width="8%">Status (DD)</th>
							                <th width="10%">Progress</th>
							                <th width="15%">Remarks</th>
							            </tr>
							        </thead>
        							<tbody>
								        <%
								            if (milestoneMainList != null) {
								                for (Object[] main : milestoneMainList) {
								                    String mainId = String.valueOf(main[0]);
								                    LocalDate startDateM = LocalDate.parse(main[6].toString());
													LocalDate endDateM = LocalDate.parse(main[7].toString());
													LocalDate orgEndDateM = LocalDate.parse(main[5].toString());
													int progressM = Integer.parseInt(main[8].toString());
													LocalDate completionDateM = main[19]!=null ? LocalDate.parse(main[19].toString()) : null;
								        %>
								            <!-- Main Row -->
								            <tr class="main-row" data-id="<%=mainId%>">
								                <td class="text-center">
								                    <span class="arrow">&#9660;</span>
								                </td>
								                <td class="center">M<%=main[2] %> </td>
					                            <td><%=main[3]!=null?StringEscapeUtils.escapeHtml4(main[3].toString()): " - "%></td>
		                            			<td class="center"><%=main[6]!=null?fc.sdfTordf(main[6].toString()):"-" %></td>
		                            			<td class="center"><%=main[7]!=null?fc.sdfTordf(main[7].toString()):"-" %></td>
		                            			<td style="text-align: center">	
													<%if(progressM==0){ %>
														<span class="assigned"> AA </span>
													<%} else if(progressM>0 && progressM<100 && (orgEndDateM.isAfter(today) || orgEndDateM.isEqual(today) )){ %>
														<span class="ongoing"> OG </span>
													<%} else if( progressM>0 && progressM<100 && (orgEndDateM.isBefore(today) )){ %>
														<span class="delay"> DO (<%=ChronoUnit.DAYS.between(orgEndDateM, LocalDate.now())%>)</span>
													<%} else if((completionDateM!=null && ( completionDateM.isBefore(orgEndDateM) ||  completionDateM.isEqual(orgEndDateM)))){ %>
														<span class="completed"> CO</span>
													<%} else if((completionDateM!=null && completionDateM.isAfter(orgEndDateM) )){ %>
														<span class="completeddelay">CD (<%=ChronoUnit.DAYS.between(orgEndDateM, completionDateM)%>)</span>
													<%}else if(completionDateM!=null && progressM==0 &&  ( endDateM.isAfter(today) ||  endDateM.isEqual(today)) ){ %>
														<span class="inactive">IA</span>
													<%}else{ %>
														<span class="assigned">AA</span>
													<%} %>
												</td>
		                            			<td class="center"><%=progressM %>%</td>
		                            			<td><%=main[18]!=null?main[18]:"-" %></td>
								            </tr>

								            <!-- Sublist Row -->
								            <tr class="sublist-row" id="sublist-<%=mainId%>">
								                <td colspan="8" class="p-0">
								                    <table class="table table-sm table-bordered sublist-table mb-0">
								                        <!-- <thead class="table-light">
								                            <tr>
									                            <th width="5%"></th>
									                            <th width="11%">Activity Id</th>
									                            <th width="25%">Activity Name</th>
									                            <th width="8%">Start Date</th>
																<th width="8%">End Date</th>	
																<th width="15%">First OIC</th>
																<th width="8%">Status (DD)</th>
																<th width="10%">Progress</th>
																<th width="15%">Remarks</th>	
									                        </tr>
								                        </thead> -->
								                        <tbody>
								                        <%
									                        List<Object[]> subMilestoneList = milestoneSubListMap != null ? milestoneSubListMap.get(mainId) : null;
									                        if (subMilestoneList != null) {
									                            for (Object[] sub : subMilestoneList) { 
									                            	LocalDate startDateS = LocalDate.parse(sub[6].toString());
																	LocalDate endDateS = LocalDate.parse(sub[7].toString());
																	LocalDate orgEndDateS = LocalDate.parse(sub[5].toString());
																	int progressS = Integer.parseInt(sub[8].toString());
																	LocalDate completionDateS = sub[17]!=null ? LocalDate.parse(sub[17].toString()) : null;
									                            %>
									                        <tr>
									                        	<td width="5%" class="center"></td>
															   	<td width="11%" class="center">M<%=sub[sub.length - 2]!=null?StringEscapeUtils.escapeHtml4(sub[sub.length-2].toString()): " - " %> <%=sub[sub.length - 3]!=null?StringEscapeUtils.escapeHtml4(sub[sub.length-3].toString()): " - " %></td>
									                            <td width="25%"><%=sub[3]!=null?StringEscapeUtils.escapeHtml4(sub[3].toString()): " - "%></td>
						                            			<td width="8%" class="center"><%=sub[6]!=null?fc.sdfTordf(sub[6].toString()):"-" %></td>
						                            			<td width="8%" class="center"><%=sub[7]!=null?fc.sdfTordf(sub[7].toString()):"-" %></td>
						                            			<td width="8%" style="text-align: center">	
																	<%if(progressS==0){ %>
																		<span class="assigned"> AA </span>
																	<%} else if(progressS>0 && progressS<100 && (orgEndDateS.isAfter(today) || orgEndDateS.isEqual(today) )){ %>
																		<span class="ongoing"> OG </span>
																	<%} else if( progressS>0 && progressS<100 && (orgEndDateS.isBefore(today) )){ %>
																		<span class="delay"> DO (<%=ChronoUnit.DAYS.between(orgEndDateS, LocalDate.now())%>)</span>
																	<%} else if((completionDateS!=null && ( completionDateS.isBefore(orgEndDateS) ||  completionDateS.isEqual(orgEndDateS)))){ %>
																		<span class="completed"> CO</span>
																	<%} else if((completionDateS!=null && completionDateS.isAfter(orgEndDateS) )){ %>
																		<span class="completeddelay">CD (<%=ChronoUnit.DAYS.between(orgEndDateS, completionDateS)%>)</span>
																	<%}else if(completionDateS!=null && progressS==0 &&  ( endDateS.isAfter(today) ||  endDateS.isEqual(today)) ){ %>
																		<span class="inactive">IA</span>
																	<%}else{ %>
																		<span class="assigned">AA</span>
																	<%} %>
																</td>
						                            			<td width="10%" class="center"><%=progressS %>%</td>
						                            			<td width="15%"><%=sub[15]!=null?sub[15]:"-" %></td>
									                        </tr>
									                    <%} } else { %>
									                        <tr><td colspan="8" class="text-muted text-center">No sub milestones</td></tr>
									                    <%} %>
								                        </tbody>
								                    </table>
								                </td>
								            </tr>
								        <%} } %>
									</tbody>
								</table>
						    </div>
						</div>
					</div>
				</div>
			</div>	
			<!-- ---------------------------------------- Milestone List End ----------------------------------------------------- -->
			
			<!-- ---------------------------------------- Milestones Achieved ----------------------------------------------------- -->
			<div class="carousel-item ">
				<div class="content-header row ">
					<div class="col-md-1" >
						<img class="logo" style="width: 45px;margin-left: 5px;margin-top: -2px;"  <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					<div class="col-md-1" align="left" style="padding-top:5px;" ><b style="margin-left: -35px;"><%=ProjectCode%></b><h6 style="font-size: 0.8rem;margin-left: -43px;"><%=mainpdc %></h6>
					</div>
					<div class="col-md-8"><h3>2. Milestones Achieved in Last One Week</h3></div>
					<div class="col-md-1" align="right"  style="padding-top:19px;" ><b style="margin-right: -35px;"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="logo" style="width: 45px;margin-left: 5px;margin-top: -2px;"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
				</div>
				<div class="content">
					<table class="subtables" style="align: left; margin-top: 10px; border-collapse: collapse;" style="width: 100%;">
						<thead>
							<tr>
								<th width="3%">SN</th>
                                <th width="3%">Main</th>
                                <th width="12%">Sub</th>
                                <th width="27%">Activity</th>
                                <th width="17%">Progress By</th>
                                <th width="8%">Progress Date</th>
                                <th width="10%">Progress</th>
                                <th width="20%">Remarks</th>
							</tr>
						</thead>
						<tbody>
							<%int slno = 0; int listsize = 0;
				            if(milestoneSubListMap!=null && milestoneSubListMap.size()>0) { 
				            	for(Map.Entry<String, List<Object[]>> entry : milestoneSubListMap.entrySet()) {
				            		List<Object[]> valueList =  entry.getValue();
				            		valueList = valueList.stream().filter(e -> Integer.parseInt(e[8].toString())>0 ).collect(Collectors.toList());
				            		for(Object[] obj : valueList) { 
					            		List<Object[]> porgressList = milestoneProgressListMap.get(Long.parseLong(obj[0].toString()));
					            		listsize += porgressList.size();
					            		for(Object[] prog : porgressList){
				            %>
                            		<tr>
                            			<td class="center"><%=++slno %></td>
                            			<td class="center">M<%=obj[obj.length - 2]!=null?StringEscapeUtils.escapeHtml4(obj[obj.length-2].toString()): " - " %></td>
                            			<td class="center"><%=obj[obj.length - 3]!=null?StringEscapeUtils.escapeHtml4(obj[obj.length-3].toString()): " - " %></td>
                            			<td><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - "%></td>
                            			<td><%=prog[8]!=null?StringEscapeUtils.escapeHtml4(prog[8].toString()): " - "%>, <%=prog[9]!=null?StringEscapeUtils.escapeHtml4(prog[9].toString()): " - "%></td>
                            			<td class="center"><%=prog[3]!=null?fc.sdfTordf(prog[3].toString()):"-" %></td>
                            			<td class="center"><%=prog[2]!=null?prog[2]:"0" %>%</td>
                            			<td><%=prog[6]!=null?StringEscapeUtils.escapeHtml4(prog[6].toString()):"-"%></td>
                            		</tr>
				            <%} }%>
				            <% } } else {%>
				            	<tr><td colspan="8" class="center">No Data Available</td></tr>
				            <%} %>
				            <%if(listsize==0) {%>
				            	<tr><td colspan="8" class="center">No Data Available</td></tr>
				            <%} %>
						</tbody>
					</table>
				</div>
			</div>	
			<!-- ---------------------------------------- Milestones Achieved End ----------------------------------------------------- -->
			
			<!-- ---------------------------------------- Milestone Open Actions ----------------------------------------------------- -->
			<div class="carousel-item ">
				<div class="content-header row ">
					<div class="col-md-1" >
						<img class="logo" style="width: 45px;margin-left: 5px;margin-top: -2px;"  <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					<div class="col-md-1" align="left" style="padding-top:5px;" ><b style="margin-left: -35px;"><%=ProjectCode%></b><h6 style="font-size: 0.8rem;margin-left: -43px;"><%=mainpdc %></h6>
					</div>
					<div class="col-md-8"><h3>3. Milestone Open Actions</h3></div>
					<div class="col-md-1" align="right"  style="padding-top:19px;" ><b style="margin-right: -35px;"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="logo" style="width: 45px;margin-left: 5px;margin-top: -2px;"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
				</div>
				<div class="content">
					<table  class="subtables" style="align: left; margin-top: 10px; border-collapse: collapse;" style="width: 100%;">
						<thead>
							<tr>
								<td colspan="7" style="border: 0">
									<p style="font-size: 12px; text-align: center">
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
								<th width="3%">SN</th>
								<th width="20%">ID</th>
								<th width="25%">Action Point</th>
								<th width="10%"><!-- ADC <br> -->PDC</th>
								<th width="15%">Responsibility</th>
								<th width="8%">Status(DD)</th>
								<th width="20%">Remarks</th>
							</tr>
						</thead>
						<tbody>
							<%if(milestoneOpenActionList!=null && milestoneOpenActionList.size()>0) { 
								int sn = 0;
								for(Object[] obj : milestoneOpenActionList) {
									String actionstatus = obj[10].toString();
									int progress = obj[12]!=null ? Integer.parseInt(obj[12].toString()) : 0;
									LocalDate pdcorg = LocalDate.parse(obj[4].toString());
									LocalDate lastdate = obj[23]!=null ? LocalDate.parse(obj[23].toString()): null;
									LocalDate endPdc=LocalDate.parse(obj[3].toString());
								
							%>
								<tr>
									<td class="center"><%=++sn %></td>
									<td class="center">
										<button type="button" class="btn btn-sm" style="border-radius: 50px;font-weight: bold" onclick="ActionDetails('<%=obj[0] %>');" data-toggle="tooltip" data-placement="top" title="Action Details" >
											<%=obj[2] %>
										</button>	
									</td>
									<td ><%=obj[16] %></td>
									<td class="center">
										<%-- <%=obj[11]!=null?fc.sdfTordf(obj[11].toString()):"-" %> <br> --%>
										<%=obj[3]!=null?fc.sdfTordf(obj[3].toString()):"-" %>
									</td>
									<td ><%=obj[21] %>, <%=obj[22] %></td>
									<td class="center">
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
									</td>
									<td><%=obj[9]!=null?obj[9]:"-" %></td>
								</tr>
							<%} } else {%>
								<tr><td colspan="7" class="center">No Data Available</td></tr>
							<%} %>
						</tbody>
					</table>
				</div>
			</div>	
			<!-- ---------------------------------------- Milestone Open Actions End ----------------------------------------------------- -->
			
			<!-- ---------------------------------------- Details of Technical/ User Reviews ----------------------------------------------------- -->
			<div class="carousel-item ">
				<div class="content-header row ">
					<div class="col-md-1" >
						<img class="logo" style="width: 45px;margin-left: 5px;margin-top: -2px;"  <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					<div class="col-md-1" align="left" style="padding-top:5px;" ><b style="margin-left: -35px;"><%=ProjectCode%></b><h6 style="font-size: 0.8rem;margin-left: -43px;"><%=mainpdc %></h6>
					</div>
					<div class="col-md-8"><h3>4. Details of Technical/ User Reviews</h3></div>
					<div class="col-md-1" align="right"  style="padding-top:19px;" ><b style="margin-right: -35px;"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="logo" style="width: 45px;margin-left: 5px;margin-top: -2px;"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
				</div>
				<div class="content">
					<div align="center">
						<div  class="mr-5 mt-2" style="float: right">
							<button type="button" class="btn btn-sm " onclick="showMeetingModal()" data-toggle="tooltip" data-placement="right" title="Other Meetings"><i class="fa fa-info-circle fa-lg " style="color: #145374" aria-hidden="true"></i></button>
						</div>
						<form action="CommitteeMinutesNewDownload.htm" method="get" target="_blank">
							<div class="row">
							<%for(Map.Entry<String, List<Object[]>> entry : reviewMeetingListMap.entrySet()) { 
								if(entry.getValue().size()>0) { %>
									<div class="col-md-3 mt-4">
										<table class="subtables" style="align: left; margin-top: 10px; margin-left: 25px; max-width: 350px; border-collapse: collapse; float: left;">
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
															<button class="btn btn-link" style="padding:0px;margin:0px;" name="committeescheduleid" value="<%=obj[0]%>"> <%=entry.getKey()%> #<%=++i %></button>
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
						</form>
					</div>
				</div>
			</div>	
			<!-- ---------------------------------------- Details of Technical/ User Reviews End ----------------------------------------------------- -->
			
			<!-- ---------------------------------------- Open Action Points ----------------------------------------------------- -->
			<% for(Map.Entry<String, List<Object[]>> actionMap : lastMeetingActionsListMap.entrySet()) {%>
				<div class="carousel-item ">
					<div class="content-header row ">
						<div class="col-md-1" >
							<img class="logo" style="width: 45px;margin-left: 5px;margin-top: -2px;"  <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
						</div>
						<div class="col-md-1" align="left" style="padding-top:5px;" ><b style="margin-left: -35px;"><%=ProjectCode%></b><h6 style="font-size: 0.8rem;margin-left: -43px;"><%=mainpdc %></h6>
						</div>
						<div class="col-md-8"><h3>5. Open Action Points - <%=actionMap.getKey() %></h3></div>
						<div class="col-md-1" align="right"  style="padding-top:19px;" ><b style="margin-right: -35px;"><%=MeetingNo %></b></div>
						<div class="col-md-1"><img class="logo" style="width: 45px;margin-left: 5px;margin-top: -2px;"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					</div>
					<div class="content">
						<table class="subtables" style="align: left; margin-top: 10px; border-collapse: collapse;">
							<thead>
								<tr>
									<td colspan="7" style="border: 0">
										<p style="font-size: 12px; text-align: center">
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
									<th style="width: 15px !important; text-align: center;">SN</th>
									<th style="width:30px;">ID</th>
									<th style="width: 280px;">Action Point</th>
									<th style="width: 95px;">ADC <br>PDC</th>
									<!-- <th style="width: 95px;">ADC</th> -->
									<th style="width: 205px;">Responsibility</th>
									<th style="width: 80px;">Status(DD)</th>
									<th style="width: 200px;">Remarks</th>
									<!-- <th style="width: 20px;">Info</th> -->
								</tr>
							</thead>
							<tbody>
								<%if (actionMap.getValue().size() > 0) {
								int i = 1;String key="";
								for (Object[] obj : actionMap.getValue()) {
								%>
								<tr>
									<td style="text-align: center;"><%=i%></td>
									<td style="text-align: center;">
										<%if(obj[17]!=null && Long.parseLong(obj[17].toString())>0){ %>
											<button type="button" class="btn btn-sm" style="border-radius: 50px;font-weight: bold" onclick="ActionDetails( <%=obj[17] %>);" data-toggle="tooltip" data-placement="top" title="Action Details" >
												<%
												Date date = inputFormat.parse(obj[1].toString().split("/")[3]);
												String formattedDate = outputFormat.format(date);
												for (Map.Entry<Integer, String> entry : committeeWiseMap.entrySet()) {
												 	if(entry.getValue().equalsIgnoreCase(formattedDate)){
														key=entry.getKey().toString();
												 	} }%>
										
												<%=actionMap.getKey().toUpperCase()+"-"+key+"/"+obj[1].toString().split("/")[4] %>
											</button>
										<%}%> 
									<!--  -->
									</td>
									<td style="text-align: justify;"> <%=obj[2]%> </td>
									<td style="text-align: center;">
										<%	String actionstatus = obj[9].toString();
											int progress = obj[15]!=null ? Integer.parseInt(obj[15].toString()) : 0;
											LocalDate pdcorg = LocalDate.parse(obj[3].toString());
											LocalDate lastdate = obj[13]!=null ? LocalDate.parse(obj[13].toString()): null;
											LocalDate endPdc=LocalDate.parse(obj[4].toString());
										%> 
						 				<% if(lastdate!=null && actionstatus.equalsIgnoreCase("C") ){%>
												<%if(actionstatus.equals("C") && (pdcorg.isAfter(lastdate) || pdcorg.equals(lastdate))){%>
												<span class="completed"><%=fc.sdfTordf(obj[13].toString())%> </span>
												<%}else if(actionstatus.equals("C") && pdcorg.isBefore(lastdate)){ %>	
												<span class="completeddelay"><%=fc.sdfTordf(obj[13].toString())%> </span>
												<%} %>	
											<%}else{ %>
													-									
											<%} %>
										<br>
										<span <%if(endPdc.isAfter(today) || endPdc.isEqual(today)) {%>style="color:black;font-weight: bold;" <%} else{%> style="color:maroon ;font-weight:bolder;" <%} %>>
										<%=fc.sdfTordf(obj[4].toString())%> 
										</span>
										<%if(!pdcorg.equals(endPdc)) { %>
										<br>
										<span <%if(pdcorg.isAfter(today) || pdcorg.isEqual(today)) {%>style="color:black;font-weight: bold;" <%} else{%> style="color:maroon ;font-weight:bolder;" <%} %>>
										<%= fc.sdfTordf(obj[3].toString())%> 
										</span>	
										<%} %>
									</td>
									<td><%=obj[11]%>, <%=obj[12] %></td>
									<td style="text-align: center;">
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
									<td style="text-align: justify;">
										<% if (obj[16] != null) { %><%=obj[16]%> <% } %>
									</td>
	
									
								</tr>
								<% i++; }
								} %>
							</tbody>

						</table>
					</div>
				</div>	
			<%} %>
			<!-- ---------------------------------------- Open Action Points End ----------------------------------------------------- -->
			
			<!-- ---------------------------------------- Thank you ----------------------------------------------------- -->
			<div class="carousel-item ">
				<div class="content" style="border: 0px solid black;padding-top: 50px;border-radius: 20px;position: relative;height: 93vh !important;">
					<!-- <div style=" position: absolute ;top: 40%;left: 34%;">
						<h1 style="font-size: 5rem;">Thank You !</h1>
					</div> -->
					<div class="content" >
						<%if(thankYouImg!=null ){ %>
							<img class="" style="width: 100%; height: 100%;"  src="data:image/*;base64,<%=thankYouImg%>"  > 
						<%}else{ %>
							<div style=" position: absolute ;top: 40%;left: 34%;">
								<h1 style="font-size: 5rem;">Thank You !</h1>
							</div>
						<%} %>
					</div>
				</div>
			</div>
			<!-- ---------------------------------------- Thank you End ----------------------------------------------------- -->
			
		</div>
		
		<a class="carousel-control-prev" href="#presentation-slides" role="button" data-slide="prev" style="width: 0%; padding-left: 20px;"> <span aria-hidden="true">
			<i class="fa fa-chevron-left fa-2x" style="color: #000000" aria-hidden="true"></i></span> <span class="sr-only">Previous</span>
		</a> <a class="carousel-control-next" href="#presentation-slides" role="button" data-slide="next" style="width: 0%; padding-right: 20px;"> <span aria-hidden="true">
			<i class="fa fa-chevron-right fa-2x" style="color: #000000" aria-hidden="true"></i></span> <span class="sr-only">Next</span>
		</a>
		
		<%int slideCount=0;%>
		<ol class="carousel-indicators">
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>"  class="carousel-indicator active" data-toggle="tooltip" data-placement="top" title="Start"><b><i class="fa fa-home" aria-hidden="true"></i></b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="Milestone List"><b>1</b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="Milestones Achieved in Last One Week"><b>2</b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="Milestone Open Actions"><b>3</b></li>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="Details of Technical/ User Reviews"><b>4</b></li>
			<%
			char a = 'a';
			for(Map.Entry<String, List<Object[]>> entry : lastMeetingActionsListMap.entrySet()) {%>
				<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="Open Action Points - <%=entry.getKey()%> "><b>5 (<%=a++ %>)</b></li>
			<%} %>
			<li data-target="#presentation-slides" data-slide-to="<%=slideCount++ %>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="Thank You"><b>End</b></li>
			<li data-slide-to="21" style="background-color:  #000000;width: 35px;margin-left: 20px;" class="carousel-indicator content_full_screen" data-toggle="tooltip" data-placement="top" title="Full Screen Mode"><b><i class="fa fa-expand fa-lg" aria-hidden="true"></i></b></li>
			<li data-slide-to="21" style="background-color:  #000000;width: 35px;margin-left: 20px;" class="carousel-indicator content_reg_screen" data-toggle="tooltip" data-placement="top" title="Exit Full Screen Mode"><b><i class="fa fa-compress fa-lg" aria-hidden="true"></i></b></li>
		</ol>
		
	</div>	
	
	<!-- -------------------------------------------- Other Meetings Modal -------------------------------------------------------- -->
	<div class="modal fade " id="meetingModal" tabindex="-1" aria-labelledby="exampleModalLabel1" aria-hidden="true">
		<div class="modal-dialog modal-xl modal-dialog-jump">
			<div class="modal-content">
				<div class="modal-header" id="ModalHeader" style="background: #055C9D ;color: white;">
			        <h5 class="modal-title" ><%= projectDetails[3] %> (<%=projectDetails[1] %>)</h5>
			        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
			          <span aria-hidden="true" class="text-light">&times;</span>
			        </button>
      			</div>
				<div class="modal-body">
					<div class="row mb-2">
						<div class="col-md-12">
							<%if(otherMeetingList!=null && otherMeetingList.size()>0) {%>
								<div align="left"><b>Other Meetings</b></div>
								<div align="left">
									<table class="subtables" style="align: left; margin-top: 10px; margin-left: 25px; max-width: 350px; border-collapse: collapse;">
										<thead>
											<tr> 
												<th style="width: 250px; ">Committee</th> 
												<th  style="width: 250px; "> Date Held</th>
											</tr>
										</thead>
										<tbody>
											<%for(Object[]obj:otherMeetingList) { %>
												<tr>
													<td>
														<a class="btn btn-link" style="padding:0px;margin:0px;" href="CommitteeMinutesViewAllDownload.htm?committeescheduleid=<%=obj[0]%>" target="blank"><%=obj[3]%></a>
													</td>												
													<td  style="text-align: center; " ><%= fc.sdfTordf(obj[1].toString())%></td>
												</tr>
											<%}%>
										</tbody>
									</table>
								</div>
							<%} %>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- -------------------------------------------- Other Meetings Modal End -------------------------------------------------------- -->
	
	<!-- -------------------------------------------------------------- action modal ----------------------------------------------------- -->

	<div class=" modal bd-example-modal-lg" tabindex="-1" role="dialog" id="action_modal">
		<div class="modal-dialog modal-lg modal-dialog-jump" role="document">
			<div class="modal-content">
				<div class="modal-header" style="background-color: #FFE0AD; ">
					<div class="row w-100"  >
						<div class="col-md-12" >
							<h5 class="modal-title" id="modal_action_no" style="font-weight:700; color: #A30808;"></h5>
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
						<form action="#" method="get">
						
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

	<!-- -------------------------------------------------------------- action modal ----------------------------------------------------- -->
									
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

$('.carousel').carousel({
	interval: false,
	keyboard: true,
});

$(function () {
	$('[data-toggle="tooltip"]').tooltip()
});
 
function showMeetingModal(){
	$('#meetingModal').modal('show');
}

$(document).ready(function(){
    $(".main-row").click(function(){
        let id = $(this).data("id");
        let subRow = $("#sublist-" + id);
        let arrow = $(this).find(".arrow");

        subRow.toggle();
        arrow.toggleClass("rotate");
    });
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
</body>
</html>