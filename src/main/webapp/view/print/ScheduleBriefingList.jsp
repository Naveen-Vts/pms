<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.LocalTime"%>
<%@page import="java.time.format.TextStyle"%>
<%@page import="java.time.LocalDate"%>
<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"
	import="java.util.*,com.vts.*,java.text.SimpleDateFormat,java.io.ByteArrayOutputStream,java.io.ObjectOutputStream"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<style type="text/css">
label {
	font-weight: bold;
	font-size: 13px;
}

body {
	background-color: #f2edfa;
	overflow-x: hidden !important;
}

h6 {
	text-decoration: none !important;
}

.cc-rockmenu {
	color: fff;
	padding: 0px 5px;
	font-family: 'Lato', sans-serif;
}

.cc-rockmenu .rolling {
	display: inline-block;
	cursor: pointer;
	width: 34px;
	height: 30px;
	text-align: left;
	overflow: hidden;
	transition: all 0.3s ease-out;
	white-space: nowrap;
}

.cc-rockmenu .rolling:hover {
	width: 108px;
}

.cc-rockmenu .rolling .rolling_icon {
	float: left;
	z-index: 9;
	display: inline-block;
	width: 28px;
	height: 52px;
	box-sizing: border-box;
	margin: 0 5px 0 0;
}

.cc-rockmenu .rolling .rolling_icon:hover .rolling {
	width: 312px;
}

.cc-rockmenu .rolling i.fa {
	font-size: 20px;
	padding: 6px;
}

.cc-rockmenu .rolling span {
	display: block;
	font-weight: bold;
	padding: 2px 0;
	font-size: 14px;
	font-family: 'Muli', sans-serif;
}

.cc-rockmenu .rolling p {
	margin: 0;
}

.width {
	width: 270px !important;
}

a:hover {
	color: white;
}

#table thead tr th, #table tbody tr td{
	text-align: center;
}

.modal_table tr th, .modal_table tr td
{
	padding:5px;
}
.modal-dialog-jump {
  animation: jumpIn 0.8s ease;
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

.trdown {
	padding: 0px 10px 5px 10px;
	border-bottom-left-radius: 5px;
	border-bottom-right-radius: 5px;
	font-size: 14px;
	font-weight: 600;
}
.trup {
	padding: 5px 10px 0px 10px;
	border-top-left-radius: 5px;
	border-top-right-radius: 5px;
	font-size: 14px;
	font-weight: 600;
}
.rightArrow{
font-size: 2.20rem !important;
padding: 0px 5px;
}
#remarksTd1{
font-weight: bold;

color: #007bff;
}
#remarksDate{
color: black;
font-size: 13px;
}
.rtnHeader{
position: relative;
top: 6px;
}

.meetingIdRmks{
position: relative;
top: 6px;
}
</style>
</head>

<body>
	<%

  List<Object[]> BriefingScheduleList=(List<Object[]>)request.getAttribute("BriefingScheduleList");
  List<Object[]> BriefingScheduleFwdList=(List<Object[]>)request.getAttribute("BriefingScheduleFwdList");
  List<Object[]> BriefingScheduleFwdApprovedList=(List<Object[]>)request.getAttribute("BriefingScheduleFwdApprovedList");
  SimpleDateFormat rdf=new SimpleDateFormat("dd-MM-yyyy");
  SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
  SimpleDateFormat sdf1=new SimpleDateFormat("HH:mm:ss");
  String projectid=(String)request.getAttribute("projectid");
  String revProjectId=(String)request.getAttribute("revProjectId");
  String committeecode=(String)request.getAttribute("committeecode");
  String commiteeName=(String)request.getAttribute("commiteeName");
  String pendingClick=(String)request.getAttribute("pendingClick");
  String initiatedClick=(String)request.getAttribute("initiatedClick");
  Object[] directorDetails=(Object[])request.getAttribute("directorName");
  String EmpId=(String)request.getAttribute("EmpId");
  Object[] DHId=(Object[])request.getAttribute("DHId");
  Object[] GHId=(Object[])request.getAttribute("GHId");
  Object[] DoRtmdAdEmpData=(Object[])request.getAttribute("DoRtmdAdEmpData");
  List<Object[]> projectslist=(List<Object[]>)request.getAttribute("projectslist");
  List<Object[]> divisionHeadList=(List<Object[]>)request.getAttribute("divisionHeadList");
  
  List<String> frwStatus  = Arrays.asList("INI","REV","RDH","RGH","RPD","RBD");
	int count=0;
  for(Object[] obj : divisionHeadList){
		if(EmpId.equalsIgnoreCase(obj[0].toString())) {
			count++;
		}
	}

  
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

	<div class="container-fluid">
		<div class="row">
			<div class="col-md-12">
				<div class="card shadow-nohover">
					<div class="card-header row">
						<div class="col-md-6">
							<h4 class="">Schedule Briefing Paper History List</h4>
						</div>
						<div class="col-md-6" align="right" style="margin-top: -8px;">
							<form method="post" action="FroozenBriefingList.htm" >
								<table>
									<tr>
										<td>
											<b>Project : </b>
										</td>
										<td>
											<select class="form-control items selectdee" name="projectid" id="projectid" required="required" style="width:200px;" data-live-search="true" data-container="body" onchange="this.form.submit();">
												<%for(Object[] obj : projectslist){ 
											     String projectshortName=(obj[17]!=null)?" ( "+obj[17].toString()+" ) ":"";
												%>
													<option <%if(revProjectId!=null && revProjectId!="null" && revProjectId.equals(obj[0].toString())) { %>selected <%} else if(projectid!=null && projectid.equals(obj[0].toString())) { %>selected <%} %>value="<%=obj[0]%>" ><%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - "%> <%=projectshortName!=null?StringEscapeUtils.escapeHtml4(projectshortName): " - " %></option>
												<%} %>
											</select>
										</td>
										<td style="padding-left: 10px;">
											<b>Committee : </b>
										</td>
										<td>
											<select class="form-control items" name="committeecode" id="committeecode"  required="required" style="width:200px;" data-live-search="true" data-container="body" onchange="this.form.submit();" >
												<%if(commiteeName!=null ){
												if(commiteeName.equalsIgnoreCase("EB")){
													%>
													<option value="PMRB" >PMRB</option>
												<option selected value="EB" >EB</option>
												<%}}else{ %>
												<option <%if(committeecode!=null && committeecode.equalsIgnoreCase("PMRB")) { %>selected <%} %>value="PMRB" >PMRB</option>
												<option <%if(committeecode!=null && committeecode.equalsIgnoreCase("EB")) { %>selected <%} %>value="EB" >EB</option>
												<%} %>
											</select>
										</td>
									</tr>
								</table>
								<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" />
							</form>
						</div>
					</div>
					<div class="card-body">
					
					   <div align="center">	
  
	<div class="row w-100" style="margin-bottom: 10px;">
		<div class="col-12">
         <ul class="nav nav-pills mb-3" id="pills-tab" role="tablist" style="background-color: #E1E5E8;padding:0px;">
		  <li class="nav-item" style="width: 33.3%;"  >
		    <div class="nav-link active" style="text-align: center;" id="pills-mov-property-tab" data-toggle="pill" data-target="#pills-mov-property" role="tab" aria-controls="pills-mov-property" aria-selected="true">
			   <span>Initiated</span> 
				<span class="badge badge-danger badge-counter count-badge" style="margin-left: 0px;">
				   		 <%if(BriefingScheduleList.size()>99){ %>
				   			99+
				   		<%}else{ %>
				   			<%=BriefingScheduleList.size() %>
						<%} %>	 		   			
				  </span>  
		    </div>
		  </li>
		  <li class="nav-item"  style="width: 33.3%;">
		    <div class="nav-link " style="text-align: center;" id="pills-imm-property-tab" data-toggle="pill" data-target="#pills-imm-property" role="tab" aria-controls="pills-imm-property" aria-selected="false">
		    	 <span>Pending</span> 
		    	  <span class="badge badge-danger badge-counter count-badge" style="margin-left: 0px;">
				   	 <%if(BriefingScheduleFwdList.size()>99){ %>
				   			99+
				   		<%}else{ %>
				   			<%=BriefingScheduleFwdList.size() %>
						<%} %> 			   			
				  </span>  
		    </div>
		  </li>
		    <li class="nav-item"  style="width: 33.3%;">
		    <div class="nav-link " style="text-align: center;" id="pills-second-property-tab" data-toggle="pill" data-target="#pills-second-property" role="tab" aria-controls="pills-second-property" aria-selected="false">
		    	 <span>Approved</span> 
		    	  <span class="badge badge-danger badge-counter count-badge" style="margin-left: 0px;">
				   	 <%if(BriefingScheduleFwdApprovedList.size()>99){ %>
				   			99+
				   		<%}else{ %>
				   			<%=BriefingScheduleFwdApprovedList.size() %>
						<%} %>		   			
				  </span>  
		    </div>
		  </li>
		</ul>
	   </div>
	</div>
	</div>
					
					
						<div class="data-table-area mg-b-15">
						<div class="container-fluid">
						           <div class="tab-content" id="pills-tabContent">
            <div class="tab-pane fade show active" id="pills-mov-property" role="tabpanel" aria-labelledby="pills-mov-property-tab">
							<form method="get" id="briefingForm">
							
							
								<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
									<div class="sparkline13-list">

										<div class="sparkline13-graph">
											<div class="datatable-dashv1-list custom-datatable-overright">
											 	<div id="toolbar1">
													<select class="form-control dt-tb">
														<option value="">Export Basic</option>
														<option value="all">Export All</option>
														<option value="selected">Export Selected</option>
													</select>
												</div>
												<table id="table" data-toggle="table" data-pagination="true" data-search="true" data-show-columns="true" data-show-pagination-switch="true" data-show-refresh="true"
													data-key-events="true" data-show-toggle="true" data-resizable="true" data-cookie="true" data-cookie-id-table="saveId" data-show-export="true" data-click-to-select="true" data-toolbar="#toolbar1"
													>
													<thead >
														<tr class="new" style="background-color: #FFC436 !important;">
															<th style="width:30px;">Committee</th>
															<th style="width:130px;">Meeting Id</th>
															<th style="width:130px;">Schedule Date & Time</th>
															<th style="width:130px;">Meeting Status</th>
															<th style="width:130px;">Briefing Status</th>
															<th style="width:130px;">Action</th>
														</tr>
													</thead>
													<tbody>
														<%for(Object[] schedule : BriefingScheduleList){%>
															<tr>
																<td><%=schedule[2]!=null?StringEscapeUtils.escapeHtml4(schedule[2].toString()): " - " %></td>
																<td><b><%=schedule[9]!=null?StringEscapeUtils.escapeHtml4(schedule[9].toString()): " - " %></b></td>
																<td>
																	<%LocalTime starttime = LocalTime.parse(LocalTime.parse(schedule[5].toString(),DateTimeFormatter.ofPattern("HH:mm:ss")).format( DateTimeFormatter.ofPattern("HH:mm") ));   %>
																	<%=rdf.format(sdf.parse(schedule[4].toString()))  %> - <%=starttime.format( DateTimeFormatter.ofPattern("hh:mm a") ) %>
																</td>
																<td><%=schedule[6]!=null?StringEscapeUtils.escapeHtml4(schedule[6].toString()): " - " %></td>
																<td><button type="button" class="btn btn-sm" formaction="IntimationTransactionStatus.htm" value="" name="briefingStatus" id="briefingStatusBtn"  data-toggle="tooltip" data-placement="top" title="briefing Status" style=" color:<%if(schedule[15].toString().equalsIgnoreCase("APD")){ %>green<%}else{ %> purple<%} %>; font-weight: 600;" formtarget="_blank">
									    							&nbsp;<%=schedule[14]!=null?StringEscapeUtils.escapeHtml4(schedule[14].toString()): " - " %></button></td>
																<td>
																<% if(schedule[15].toString().equalsIgnoreCase("APD")){%>
														<button type="button" class="btn btn-sm " style="color:white;margin:5px; " 
																		onclick="showmodal('M', <%=schedule[0]%>,
																		'<%=schedule[9] %>', 
																		'<%=rdf.format(sdf.parse(schedule[4].toString()))  %> - <%=starttime.format( DateTimeFormatter.ofPattern("hh:mm a") ) %>',
																		'<%=schedule[11] %>',
																		'<%=schedule[3] %>','<%=schedule[7] %>','<%=schedule[8] %>','<%=schedule[12] %>'
																		)" data-toggle="tooltip" data-placement="top" title="Update">
																			<i class="fa fa-pencil-square-o fa-lg	" aria-hidden="true"></i>
																		</button>
																	 <%}if(schedule[7].toString().equalsIgnoreCase("Y")   ){
																		if(!schedule[15].toString().equalsIgnoreCase("APD")){%>
																		<button type="button" class="btn btn-sm " style="color:white;margin:5px; " 
																		onclick="showmodal('U', <%=schedule[0]%>,
																		'<%=schedule[9] %>', 
																		'<%=rdf.format(sdf.parse(schedule[4].toString()))  %> - <%=starttime.format( DateTimeFormatter.ofPattern("hh:mm a") ) %>',
																		'<%=schedule[11] %>',
																		'<%=schedule[3] %>','<%=schedule[7] %>','<%=schedule[8] %>','<%=schedule[12] %>'
																		)" data-toggle="tooltip" data-placement="top" title="Update">
																			<i class="fa fa-pencil-square-o fa-lg	" aria-hidden="true"></i>
																		</button>
																		
																		<% }if(schedule[7]!=null && schedule[7].toString().equalsIgnoreCase("Y")) {%>
																		<button class="btn btn-sm" style="margin:5px;" formaction="MeetingBriefingPaper.htm" name="scheduleid" value="<%=schedule[0]%>" formmethod="get" formtarget="_blank" data-toggle="tooltip" data-placement="top" title="View">
																			<i class="fa fa-eye" aria-hidden="true"></i>
																		</button>
																		<% }
																		if(schedule[12]!=null && schedule[12].toString().equalsIgnoreCase("Y")) {%>
																			<button type="submit" class="btn btn-sm" style="border: 0 ;border-radius: 3px;"  formmethod="GET" name="scheduleid" value="<%=schedule[0]%>" formaction="MeetingBriefingPresenttaion.htm" formtarget="_blank"  data-toggle="tooltip" data-placement="top" title="Presentation">
																			<img alt="" src="view/images/presentation.png" style="width:19px !important"><i class="fa fa-eye" aria-hidden="true" style="margin-left:6px;"></i>
																			</button>
																			<%} %>
																			
																			<%-- <%if(schedule[8]!=null && schedule[8].toString().equalsIgnoreCase("Y")) {%>
																	
																			<button type="submit" class="btn btn-sm" style="border: 0 ;border-radius: 3px;"  formmethod="GET" name="scheduleid" value="<%=schedule[0]%>" formaction="MeetingMom.htm" formtarget="_blank"  data-toggle="tooltip" data-placement="top" title="MOM">
																			<img alt="" src="view/images/requirement.png" style="width:19px !important"><i class="fa fa-eye" aria-hidden="true" style="margin-left:6px;"></i>
																			</button>
																			<%} %> --%>
																	<%}else{ %>
																		<button type="button" class="btn btn-sm " style="background-color:#0e49b5 ;color:white;margin:5px; " 
																		onclick="showmodal('A', <%=schedule[0]%>,
																		'<%=schedule[9] %>', 
																		'<%=rdf.format(sdf.parse(schedule[4].toString()))  %> - <%=starttime.format( DateTimeFormatter.ofPattern("hh:mm a") ) %>',
																		'<%=schedule[11] %>',
																		'<%=schedule[3] %>'
																		)" data-toggle="tooltip" data-placement="top" title="Add">
																			<i class="fa fa-plus-square" aria-hidden="true"></i>
																		</button>
																	<%} if(schedule[7].toString().equalsIgnoreCase("Y")  ){
																	if(count==0 
																			&& frwStatus.contains(schedule[15])
																			&& !EmpId.equalsIgnoreCase(DHId[0].toString())
																			&& !EmpId.equalsIgnoreCase(GHId[0].toString()) 
																			&& !EmpId.equalsIgnoreCase(DoRtmdAdEmpData[0].toString())
																			&& !EmpId.equalsIgnoreCase(directorDetails[0].toString())) { %>
																					<button class="editable-click" type="button"  style="background-color: transparent;" 
																				formaction="BriefingForward.htm" formmethod="POST" formnovalidate="formnovalidate"	name="BriefingFwd" value="UserFwd" onclick="return BriefingFwdd('<%=schedule[0] %>','<%=schedule[3] %>','<%=schedule[15] %>','<%=schedule[9] %>');"
																		data-toggle="tooltip" data-placement="top" id="BriefingFwdBtn" title="" data-original-title="FORWARD">
																	<div class="cc-rockmenu" >
																			<figure class="rolling_icon" >
																				<img src="view/images/forward1.png">
																			</figure>
																	</div>
																		</button>
												
																		
																		<%}} %>
																		
																		
																							<% if(schedule[16] !=null){
																							if(schedule[15].toString().equalsIgnoreCase("FWU") && EmpId.equalsIgnoreCase(schedule[16].toString()) ){%>

												
													<button type="button" class="btn btn-sm" style="border: 0 ;border-radius: 3px;"  formmethod="POST" name="scheduleid" value="<%=schedule[0]%>" formaction="BriefingActionReturn.htm"   data-toggle="tooltip" data-placement="top" title="REVOKE" onclick="return BriefingRevoke('<%=schedule[0] %>','<%=schedule[3] %>','<%=schedule[15] %>','<%=schedule[16] %>');" id="BriefingRevokeBtn">
													<img alt="" src="view/images/revoke.png" style="width:19px !important"></button>
												
												 <%}}if(Integer.valueOf(schedule[17].toString()) >0) {%>
												 
												 		<button title="REMARKS" class="editable-click" name="sub" type="button"style="background-color: transparent;"formaction="RemarksList.htm" formmethod="POST"formnovalidate="formnovalidate" name="briefingRmk" id="briefingRmk"onclick="return briefingRmks('<%=schedule[0]%>','<%=schedule[9] %>')">
														<i class="fa fa-comment" aria-hidden="true" style="color: #143F6B; font-size: 24px; position: relative; top: 5px;"></i></button>
													<%}%>
												
																</td>
															</tr>
														<%} %>
													</tbody>
												</table>
											</div>
										</div>
									</div>
								</div>
							
								<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" />
								<input type="hidden" name="projectidRtn" id="rvk_projectid" value="">
								<input type="hidden" name="projectid" id="fwd_projectid" value="">
								<input type="hidden" name="meetingIdNoFWd" id="meetingIdNoFWd" value="">
							<input type="hidden" name="committeecode" value=<%=committeecode %>>
								<input type="hidden" name="sheduleRtn" id="sheduleIdRvk" value="">
								<input type="hidden" name="sheduleIdFwd" id="sheduleIdFwd" value="">
								<input type="hidden" name="briefingStatus" id="briefingStatus" value="">
								<input type="hidden" name="userId" id="userId" value="">
								
								<input type="hidden" name="DHId" id="DHId" value="<%= DHId[0]%>">
								<input type="hidden" name="GHId" id="GHId" value="<%= GHId[0]%>">
								<input type="hidden" name="DOId" id="DOId" value="<%= DoRtmdAdEmpData[0]%>">
								<input type="hidden" name="DirectorId" id="DirectorId" value="<%= directorDetails[0]%>">
								
							</form>
							</div>
							
							<!---------------------------------------------- Pending List ----------------------------------------------------->
							  <div class="tab-pane fade show " id="pills-imm-property" role="tabpanel" aria-labelledby="pills-imm-property-tab">
							
							<form method="get" id="briefingForm">
							
							
								<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
									<div class="sparkline13-list">

										<div class="sparkline13-graph">
											<div class="datatable-dashv1-list custom-datatable-overright">
												<div id="toolbar2">
													<select class="form-control dt-tb">
														<option value="">Export Basic</option>
														<option value="all">Export All</option>
														<option value="selected">Export Selected</option>
													</select>
												</div> 
												<table id="table" data-toggle="table" data-pagination="true" data-search="true" data-show-columns="true" data-show-pagination-switch="true" data-show-refresh="true"
													data-key-events="true" data-show-toggle="true" data-resizable="true" data-cookie="true" data-cookie-id-table="saveId" data-show-export="true" data-click-to-select="true" data-toolbar="#toolbar2"
													>
													<thead>
														<tr>
															<th style="width:30px;">Committee</th>
															<th style="width:130px;">Meeting Id</th>
															<th style="width:130px;">Schedule Date & Time</th>
															<th style="width:130px;">Meeting Status</th>
															<th style="width:130px;">Briefing Status</th>
															<th style="width:130px;">Action</th>
														</tr>
													</thead>
													<tbody>
														<%if(BriefingScheduleFwdList!=null && BriefingScheduleFwdList.size()>0){
														for(Object[] schedule : BriefingScheduleFwdList){%>
															<tr>
																<td><%=schedule[2]!=null?StringEscapeUtils.escapeHtml4(schedule[2].toString()): " - " %></td>
																<td><b><%=schedule[9]!=null?StringEscapeUtils.escapeHtml4(schedule[9].toString()): " - " %></b></td>
																<td>
																	<%LocalTime starttime = LocalTime.parse(LocalTime.parse(schedule[5].toString(),DateTimeFormatter.ofPattern("HH:mm:ss")).format( DateTimeFormatter.ofPattern("HH:mm") ));   %>
																	<%=rdf.format(sdf.parse(schedule[4].toString()))  %> - <%=starttime.format( DateTimeFormatter.ofPattern("hh:mm a") ) %>
																</td>
																<td><%=schedule[6]!=null?StringEscapeUtils.escapeHtml4(schedule[6].toString()): " - " %></td>
																<td><button type="button" class="btn btn-sm" formaction="IntimationTransactionStatus.htm" value="" name="briefingStatus" id="briefingStatusBtn"  data-toggle="tooltip" data-placement="top" title="briefing Status" style=" color:<%if(schedule[15].toString().equalsIgnoreCase("APD")){ %>green<%}else{ %> purple<%} %>; font-weight: 600;" formtarget="_blank">
									    							&nbsp;<%=schedule[14]!=null?StringEscapeUtils.escapeHtml4(schedule[14].toString()): " - " %></button></td>
																<td>
																	<%if(schedule[7].toString().equalsIgnoreCase("Y")  ){ %>
																		<button type="button" class="btn btn-sm " style="color:white;margin:5px; " 
																		onclick="showmodal('U', <%=schedule[0]%>,
																		'<%=schedule[9] %>', 
																		'<%=rdf.format(sdf.parse(schedule[4].toString()))  %> - <%=starttime.format( DateTimeFormatter.ofPattern("hh:mm a") ) %>',
																		'<%=schedule[11] %>',
																		'<%=schedule[3] %>','<%=schedule[7] %>','<%=schedule[8] %>','<%=schedule[12] %>'
																		)" data-toggle="tooltip" data-placement="top" title="Update">
																			<i class="fa fa-pencil-square-o fa-lg	" aria-hidden="true"></i>
																		</button>
				
																		
																		<%if(schedule[7]!=null && schedule[7].toString().equalsIgnoreCase("Y")) {%>
																		<button class="btn btn-sm" style="margin:5px;" formaction="MeetingBriefingPaper.htm" name="scheduleid" value="<%=schedule[0]%>" formmethod="get" formtarget="_blank" data-toggle="tooltip" data-placement="top" title="View">
																			<i class="fa fa-eye" aria-hidden="true"></i>
																		</button>
																		<% }
																		if(schedule[12]!=null && schedule[12].toString().equalsIgnoreCase("Y")) {%>
																			<button type="submit" class="btn btn-sm" style="border: 0 ;border-radius: 3px;"  formmethod="GET" name="scheduleid" value="<%=schedule[0]%>" formaction="MeetingBriefingPresenttaion.htm" formtarget="_blank"  data-toggle="tooltip" data-placement="top" title="Presentation">
																			<img alt="" src="view/images/presentation.png" style="width:19px !important"><i class="fa fa-eye" aria-hidden="true" style="margin-left:6px;"></i>
																			</button>
																			<%} %>
																			
																		
																	<%}else{ %>
																		<button type="button" class="btn btn-sm " style="background-color:#0e49b5 ;color:white;margin:5px; " 
																		onclick="showmodal('A', <%=schedule[0]%>,
																		'<%=schedule[9] %>', 
																		'<%=rdf.format(sdf.parse(schedule[4].toString()))  %> - <%=starttime.format( DateTimeFormatter.ofPattern("hh:mm a") ) %>',
																		'<%=schedule[11] %>',
																		'<%=schedule[3] %>'
																		)" data-toggle="tooltip" data-placement="top" title="Add">
																			<i class="fa fa-plus-square" aria-hidden="true"></i>
																		</button>
																		<%}if(schedule[7].toString().equalsIgnoreCase("Y")  ){
																	if( schedule[15].toString().equalsIgnoreCase("FWU")) {%>
																		<button class="btn btn-primary btn-sm add" type="button" formaction="BriefingForward.htm" formmethod="POST" formnovalidate="formnovalidate"	name="BriefingFwd" value="DHRec" onclick="return BriefingFwdd('<%=schedule[0] %>','<%=schedule[3] %>','<%=schedule[15] %>','<%=schedule[9] %>');"
																		data-toggle="tooltip" data-placement="top" id="BriefingFwdBtn" title="" data-original-title="DH Rec">Recommend</button>
																		<%}else if( schedule[15].toString().equalsIgnoreCase("RED")) {%>
																		<button class="btn btn-primary btn-sm add" type="button" formaction="BriefingForward.htm" formmethod="POST" formnovalidate="formnovalidate"	name="BriefingFwd" value="DHRec" onclick="return BriefingFwdd('<%=schedule[0] %>','<%=schedule[3] %>','<%=schedule[15] %>','<%=schedule[9] %>');"
																		data-toggle="tooltip" data-placement="top" id="BriefingFwdBtn" title="" data-original-title="GH Rec">Recommend</button>
																		
																		<%}else if( schedule[15].toString().equalsIgnoreCase("REG")) {%>
																		<button class="btn btn-primary btn-sm add" type="button" formaction="BriefingForward.htm" formmethod="POST" formnovalidate="formnovalidate"	name="BriefingFwd" value="DHRec" onclick="return BriefingFwdd('<%=schedule[0] %>','<%=schedule[3] %>','<%=schedule[15] %>','<%=schedule[9] %>');"
																		 id="BriefingFwdBtn" title="" data-original-title="DO-P&C Rec">Recommend</button>
																		
																		<%}else if( schedule[15].toString().equalsIgnoreCase("REP")) {%>
																		<button class="btn btn-primary btn-sm add" type="button" formaction="BriefingForward.htm" formmethod="POST" formnovalidate="formnovalidate"	name="BriefingFwd" value="DHRec" onclick="return BriefingFwdd('<%=schedule[0] %>','<%=schedule[3] %>','<%=schedule[15] %>','<%=schedule[9] %>');"
																		 id="BriefingFwdBtn" title="" data-original-title="Director Approval">Approve</button>
																		
																		<%}}%>
											
												
												
											<button class="btn btn-danger btn-sm delete" type="button" formaction="BriefingActionReturn.htm" formmethod="POST" formnovalidate="formnovalidate"	name="BriefingRtn" value="return" onclick="return BriefingReturn('<%=schedule[0] %>','<%=schedule[3] %>','<%=schedule[15] %>','<%=schedule[9] %>');" id="BriefingReturnBtn" >Return</button>
																</td>
															</tr>
														<%} }%>
													</tbody>
												</table>
											</div>
										</div>
									</div>
								</div>
							
								<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" />
								<input type="hidden" name="projectid" id="fwd_projectid" value="">
								<input type="hidden" name="meetingIdNoFWd" id="meetingIdNoFWd" value="">
							<input type="hidden" name="committeecode" value=<%=committeecode %>>
								<input type="hidden" name="sheduleIdFwd" id="sheduleIdFwd" value="">
								<input type="hidden" name="briefingStatus" id="briefingStatus" value="">
								<input type="hidden" name="DHId" id="DHId" value="<%= DHId[0]%>">
								<input type="hidden" name="GHId" id="GHId" value="<%= GHId[0]%>">
								<input type="hidden" name="DOId" id="DOId" value="<%= DoRtmdAdEmpData[0]%>">
								<input type="hidden" name="DirectorId" id="DirectorId" value="<%= directorDetails[0]%>">
						<!-- 		<input type="hidden" name="sheduleRtn" id="sheduleIdRtn">
						 		<input type="hidden" name="projectidRtn" id="rtn_projectid">
						 		<input type="hidden" name="briefingStatus" id="briefingStatus"> -->
								
							</form>
							</div>
			
							<!---------------------------------------------- Approved List ----------------------------------------------------->

		<div class="tab-pane fade show " id="pills-second-property" role="tabpanel" aria-labelledby="pills-second-property-tab">
							
							<form method="get" id="briefingForm">
							<div class="container-fluid">
							
								<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
									<div class="sparkline13-list">

										<div class="sparkline13-graph">
											<div class="datatable-dashv1-list custom-datatable-overright">
												<div id="toolbar3">
													<select class="form-control dt-tb">
														<option value="">Export Basic</option>
														<option value="all">Export All</option>
														<option value="selected">Export Selected</option>
													</select>
												</div>
												<table id="table" data-toggle="table" data-pagination="true" data-search="true" data-show-columns="true" data-show-pagination-switch="true" data-show-refresh="true"
													data-key-events="true" data-show-toggle="true" data-resizable="true" data-cookie="true" data-cookie-id-table="saveId" data-show-export="true" data-click-to-select="true" data-toolbar="#toolbar3"
													>
													<thead>
														<tr>
															<th style="width:30px;">Committee</th>
															<th style="width:130px;">Meeting Id</th>
															<th style="width:130px;">Schedule Date & Time</th>
															<th style="width:130px;">Meeting Status</th>
															<th style="width:130px;">Briefing Status</th>
															<th style="width:130px;">Action</th>
														</tr>
													</thead>
													<tbody>
														<%if(BriefingScheduleFwdApprovedList!=null && BriefingScheduleFwdApprovedList.size()>0){
														for(Object[] schedule : BriefingScheduleFwdApprovedList){%>
															<tr>
																<td><%=schedule[2]!=null?StringEscapeUtils.escapeHtml4(schedule[2].toString()): " - " %></td>
																<td><b><%=schedule[9]!=null?StringEscapeUtils.escapeHtml4(schedule[9].toString()): " - " %></b></td>
																<td>
																	<%LocalTime starttime = LocalTime.parse(LocalTime.parse(schedule[5].toString(),DateTimeFormatter.ofPattern("HH:mm:ss")).format( DateTimeFormatter.ofPattern("HH:mm") ));   %>
																	<%=rdf.format(sdf.parse(schedule[4].toString()))  %> - <%=starttime.format( DateTimeFormatter.ofPattern("hh:mm a") ) %>
																</td>
																<td><%=schedule[6]!=null?StringEscapeUtils.escapeHtml4(schedule[6].toString()): " - " %></td>
																<td><button type="button" class="btn btn-sm" formaction="IntimationTransactionStatus.htm" value="" name="briefingStatus" id="briefingStatusBtn"  data-toggle="tooltip" data-placement="top" title="briefing Status" style=" color:<%if(schedule[15].toString().equalsIgnoreCase("APD")){ %>green<%}else{ %> purple<%} %>; font-weight: 600;" formtarget="_blank">
									    							&nbsp;<%=schedule[14]!=null?StringEscapeUtils.escapeHtml4(schedule[14].toString()): " - " %></button></td>
																<td>

																		<button class="btn btn-sm" style="margin:5px;" formaction="MeetingBriefingPaper.htm" name="scheduleid" value="<%=schedule[0]%>" formmethod="get" formtarget="_blank" data-toggle="tooltip" data-placement="top" title="View">
																			<i class="fa fa-eye" aria-hidden="true"></i>
																		</button>
																		<% 
																		if(schedule[12]!=null && schedule[12].toString().equalsIgnoreCase("Y")) {%>
																			<button type="submit" class="btn btn-sm" style="border: 0 ;border-radius: 3px;"  formmethod="GET" name="scheduleid" value="<%=schedule[0]%>" formaction="MeetingBriefingPresenttaion.htm" formtarget="_blank"  data-toggle="tooltip" data-placement="top" title="Presentation">
																			<img alt="" src="view/images/presentation.png" style="width:19px !important"><i class="fa fa-eye" aria-hidden="true" style="margin-left:6px;"></i>
																			</button>
																			<%} %>
																		
																
																</td>
															</tr>
														<%}} %>
													</tbody>
												</table>
											</div>
										</div>
									</div>
								</div>
							</div>
								<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" />
				
								
							</form>
							</div>
			
							
							
							</div>
							</div>
						</div>

					</div>

					<div class="row">
						<div class="col-md-12" style="text-align: center;">
							<b>Approval Flow</b>
						</div>
					</div>
					
							<div class="row"
						style="text-align: center; padding-top: 10px; padding-bottom: 15px;">
						<table align="center">
							<tr>
								<td class="trup" style="background: #B5EAEA;">Division Head
								</td>
								<td rowspan="2"><i class="fa fa-long-arrow-right rightArrow"
									aria-hidden="true"></i></td>
									
									<td class="trup" style="background: #FBC7F7;">Group Head
								</td>
								<td rowspan="2"><i class="fa fa-long-arrow-right rightArrow"
									aria-hidden="true"></i></td>

								<td class="trup" style="background: #C6B4CE;">DO-P&C 

								</td>
								<td rowspan="2"><i class="fa fa-long-arrow-right rightArrow"
									aria-hidden="true"></i></td>

								<td class="trup" style="background: #E8E46E;">DIRECTOR</td>
								

							</tr>

							<tr>
								<td class="trdown" style="background: #B5EAEA;">
									 Division Head of PDD

								</td>
									<td class="trdown" style="background: #FBC7F7;">
									 Group Head of PDD

								</td>
								<td class="trdown" style="background: #C6B4CE;">
							<%if(DoRtmdAdEmpData!=null && DoRtmdAdEmpData.length>0 ){ %><%=DoRtmdAdEmpData[1]!=null?StringEscapeUtils.escapeHtml4(DoRtmdAdEmpData[1].toString()): " - " %>,<%=DoRtmdAdEmpData[2]!=null?StringEscapeUtils.escapeHtml4(DoRtmdAdEmpData[2].toString()): " - " %><%} %>
									
								</td>
								<td class="trdown" style="background: #E8E46E;">
								<%if(directorDetails!=null && directorDetails.length>0 ){ %><%=directorDetails[1]!=null?StringEscapeUtils.escapeHtml4(directorDetails[1].toString()): " - " %><%} %>
					
								
								</td>
						
							</tr>
						</table>
					</div>

				</div>


			</div>

		</div>

	</div>

<!-- -------------------------------------------------------------- action modal ----------------------------------------------------- -->

	<div class=" modal bd-example-modal-lg" tabindex="-1" role="dialog" id="briefing_modal">
		<div class="modal-dialog modal-dialog-centered modal-dialog-jump modal-lg" role="document">
			<div class="modal-content">
				<div class="modal-header" style="background-color: #FFE0AD; ">
					<div class="row w-100"  >
						<div class="col-md-12" >
							<h5 class="modal-title" id="briefing_modal_header" style="font-weight:700; color: #A30808;"></h5>
						</div>
					</div>
					
					 <button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body" align="center">
					<form action="#" method="post" autocomplete="off" enctype="multipart/form-data"  >
						<table style="width: 100%;" class="modal_table">
							<tr>
								<th >Meeting Id :</th>
								<td id="modal_meetingid"></td>
								<th>Meeting Date & Time:</th>
								<td id="modal_datetime"></td>
							</tr>
							<tr>
								<th>Project :</th>
								<td id="modal_projectcode"></td>
								<th></th>
								<td></td>
							</tr>
							<tr>
								<th >Briefing Paper :</th>
								<td colspan="3" >
									<input type="file" accept=".pdf" id="briefingpaper" name="briefingpaper" class="form-control" required="required">
								</td>
							</tr>
							<tr><th> Briefing <br> Presentation : </th>
							<td colspan="3">
							<input type="file" accept=".pdf" id="briefingpresent" name="briefingpresent" class="form-control" required="required">
							</td>
							</tr>
							<tr style="display: none;" id="momTr">
							<th> MOM  :</th>
							<td colspan="3">
							<input type="file"  accept=".pdf" id="Momfile" name="Momfile" class="form-control"  >
							</td>
							</tr>
							<tr>
								<td style="text-align: center;" colspan="4">
									<button class="btn btn-sm submit" formaction="FrozenBriefingAdd.htm" formmethod="post"  onclick="return confirm('Are You Sure to Submit?');" id="modal_add_btn">Submit</button>
									<button class="btn btn-sm edit" formaction="FrozenBriefingUpdate.htm" formmethod="post" onclick="Update()" id="modal_update_btn">Update</button>
								</td>
							</tr>
						</table>
						<input type="hidden" name="projectid" id="modal_projectid" value="">
						<input type="hidden" name="committeecode" value=<%=committeecode %>>
						<input type="hidden" name="scheduleid" id="modal_scheduleid" value="">
						<input type="hidden" name="BriefingPaperFrozen" id="BriefingPaperFrozen">
						<input type="hidden" name="PresentationFrozen" id="PresentationFrozen">
						<input type="hidden" name="MinutesFrozen" id="MinutesFrozen">
						<input type="hidden" name="pendingEdit" id="pendingEdit">
						<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" />
					</form>
					
				</div>
				
			</div>
		</div>
	</div>

<!-- -------------------------------------------------------------- action modal ----------------------------------------------------- -->


		<!-- -- ******************************************************************return  Model End ***********************************************************************************-->
<form class="form-horizontal" role="form"
			action="#" method="POST" id="returnFrm" autocomplete="off">
			<div class="modal fade bd-example-modal-lg" id="briefingReturnmodal"
				tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel"
				aria-hidden="true">
				<div class="modal-dialog modal-lg">
					<div class="modal-content addreq" style="width: 130%; position: relative; right: 150px;" >
						<div class="modal-header" id="modalreqheader" style="background-color: #021B79">
							<h5 class="modal-title" id="exampleModalLabel" style="color: #fff;float:left;">Briefing Paper Return</h5>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<h6 class="modal-title rtnHeader" id="exampleModalLabel" style="color: #fff;">Meeting Id :- </h6>
							&nbsp;&nbsp;&nbsp;
							<h6 class="modal-title rtnHeader MeetingDetails " id="exampleModalLabel" style="color: #fff;"></h6>
							
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<h6 class="modal-title rtnHeader"  id="exampleModalLabel" style="color: #fff;">Schedule Date :- </h6>
							&nbsp;&nbsp;&nbsp;
							<h6 class="modal-title rtnHeader scheduleDate" id="exampleModalLabel" style="color: #fff;"></h6>
							
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close" style="color: white">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<div style="height: 340px; overflow: auto;">
							<div class="modal-body">
							<div class="row" style="" id="mainrow">
							<div class="col-md-12">
		                <div class="row">
		                  <div class="col-md-3" style="max-width: 10%">
		                      <label class="control-label returnLabel">Remarks</label>
		                      <span class="mandatory" style="color: #cd0a0a;">*</span>
		                    
		                  </div>
		                  <div class="col-md-10" style="max-width: 90%">
		                      <textarea class="form-control" rows="9" cols="30" placeholder="Max 500 Characters" name="replyMsg" id="replyMsg" maxlength="500"></textarea>
		                  </div>
		            </div>
		           
		             <br>
		            
		        <div class="form-group" align="center" >
					<span id="btnsub"><button type="button" id="returnSubBtn" class="btn btn-primary btn-sm submit" id="submit" formaction="BriefingActionReturn.htm"  value="SUBMIT" onclick="return returnSub()">SUBMIT</button></span>
				</div>

				<input type="hidden" name="${_csrf.parameterName}"		value="${_csrf.token}" /> 
 		<input type="hidden" name="sheduleRtn" id="sheduleIdRtn">
 		<input type="hidden" name="projectidRtn" id="rtn_projectid">
 		<input type="hidden" name="briefingStatus" id="briefingStatusRtn">
 		<input type="hidden" name="meetingIdRtn" id="meetingIdRtn">
 		<input type="hidden" name="commiteeName" id="commiteeName">
 		<!--  <input type="hidden" name="userId" id="userIdRtn">-->
 		<input type="hidden" name="committeecode" value=<%=committeecode %>>
        
	</div>
</div>
							</div>
						</div>
					</div>

				</div>
				</div>
		
		</form>
		
				<!-- -- ******************************************************************Remarks  Model Start ***********************************************************************************-->
<form class="form-horizontal" role="form"
			action="#" method="POST" id="returnFrm" autocomplete="off">
			<div class="modal fade bd-example-modal-lg" id="briefingRemarksmodal"
				tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel"
				aria-hidden="true">
				<div class="modal-dialog modal-lg">
					<div class="modal-content addreq" style="width: 100%; position: relative; " >
						<div class="modal-header" id="modalreqheader" style="background-color: #021B79">
							<h5 class="modal-title" id="exampleModalLabel" style="color: #fff">Briefing Paper Remarks</h5>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<h6 class="modal-title meetingIdRmks" id="exampleModalLabel" style="color: #fff">Meeting Id :-</h6>
						&nbsp;&nbsp;&nbsp;
						<h6 class="modal-title meetingIdRmks meetingIdRmk" id="exampleModalLabel" style="color: #fff"></h6>
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close" style="color: white">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<div style="height: 300px; overflow: auto;">
							<div class="modal-body">
							
		<div class="form-inline" >
		<table class=" table-hover table-striped remarksDetails " style="width: 100%;"  >
		<tbody id="remarksTb"></tbody>
							</table>

				<input type="hidden" name="${_csrf.parameterName}"		value="${_csrf.token}" /> 
 		<input type="hidden" name="rfa" id="rfaHidden">
 		<input type="hidden" name="RfaStatus" id="RfaStatusHidden">
        
	</div>
							</div>
						</div>
					</div>

				</div>
				</div>
		
		</form>
		<!-- -- ******************************************************************Remarks  Model End ***********************************************************************************-->
</body>
<script type="text/javascript">

$(function () {
	  $('[data-toggle="tooltip"]').tooltip();
	  var pendingClick='<%=pendingClick %>';
	  var initiatedClick='<%=initiatedClick %>';
	  if(pendingClick == "N"){
		  $('#pills-imm-property-tab').trigger('click');
	  }else if(initiatedClick == "N"){
		  $('#pills-mov-property-tab').trigger('click');
	  }
	
	  var committeName='<%= commiteeName%>';
	  if(committeName == "EB"){
		  $('#committeecode').val(committeName);
	  }
	  
	 
});


function showmodal(addupdate,scheduleid,meetingid,datetime,projectcode, projectid, a,b,c )
{
	$('#modal_scheduleid').val(scheduleid);
	$('#modal_projectid').val(projectid);
	
	$('#modal_meetingid').html(meetingid);
	$('#modal_datetime').html(datetime);
	$('#modal_projectcode').html(projectcode);
	
	if(addupdate === 'A'){
		$('#briefing_modal_header').html('Add Briefing Details');
		$('#modal_add_btn').show();
		$('#modal_update_btn').hide();
		var briefingpaper=document.getElementById('briefingpaper');
		briefingpaper.setAttribute("required","required");
		var briefingpresent=document.getElementById('briefingpresent');
		briefingpresent.setAttribute("required","required");
		var BriefingPaperFrozen=document.getElementById('BriefingPaperFrozen');
		BriefingPaperFrozen.value="";
		var PresentationFrozen=document.getElementById('PresentationFrozen');
		PresentationFrozen.value="";
		var MinutesFrozen=document.getElementById('MinutesFrozen');
		MinutesFrozen.value="";
		var momTr=document.getElementById('momTr');
		momTr.style.display = 'none';
	}else if(addupdate === 'U')
	{
		$('#briefing_modal_header').html('Update Existing Briefing Details');
		$('#modal_add_btn').hide();
		$('#modal_update_btn').show();	
		var briefingpaper=document.getElementById('briefingpaper');
		briefingpaper.removeAttribute("required");
		var briefingpresent=document.getElementById('briefingpresent');
		briefingpresent.removeAttribute("required");
		var Momfile=document.getElementById('Momfile');
		Momfile.removeAttribute("required");
		var BriefingPaperFrozen=document.getElementById('BriefingPaperFrozen');
		BriefingPaperFrozen.value=a;
		var MinutesFrozen=document.getElementById('MinutesFrozen');
		MinutesFrozen.value=b;
		var PresentationFrozen=document.getElementById('PresentationFrozen');
		PresentationFrozen.value=c;
		var momTr=document.getElementById('momTr');
		momTr.style.display = 'none';
		$('#pendingEdit').val("P");
		
	}else{
		$('#briefing_modal_header').html('Update Existing Briefing Details');
		$('#modal_add_btn').hide();
		$('#modal_update_btn').show();	
		var briefingpaper=document.getElementById('briefingpaper');
		briefingpaper.removeAttribute("required");
		var briefingpresent=document.getElementById('briefingpresent');
		briefingpresent.removeAttribute("required");
		var Momfile=document.getElementById('Momfile');
		Momfile.removeAttribute("required");
		var BriefingPaperFrozen=document.getElementById('BriefingPaperFrozen');
		BriefingPaperFrozen.value=a;
		var MinutesFrozen=document.getElementById('MinutesFrozen');
		MinutesFrozen.value=b;
		var PresentationFrozen=document.getElementById('PresentationFrozen');
		PresentationFrozen.value=c;
		var momTr=document.getElementById('momTr');
		momTr.style.display = '';
	}
	
	$('#briefing_modal').modal('toggle');
}

function Update(){
	var briefingpaper=$('#briefingpaper').val();
	var briefingpresent=$('#briefingpresent').val();
	var Momfile=$('#Momfile').val();
	if(briefingpresent.length==0 && briefingpaper.length==0 && Momfile.length==0){
		alert("please upload anyone file!");
		event.preventDefault();
		return false;
	}
	if(confirm("Are you sure , you want to update?")){
		return true;
	}else{
		event.preventDefault();
		return false;
	}
	
}

function BriefingFwdd(sheduleId,projectId,BriefingStatus,meetingId) {
	
	
	$('#sheduleIdFwd').val(sheduleId);
	$('#fwd_projectid').val(projectId);
	$('#briefingStatus').val(BriefingStatus);
	$('#meetingIdNoFWd').val(meetingId);
	if(BriefingStatus == "INI"){
		var confirmation = confirm('Are you sure to Forward the  Briefing Paper');
	}else if(BriefingStatus == "REP"){
	  var confirmation = confirm('Are you sure to Approve the  Briefing Paper');
	}else{
		 var confirmation = confirm('Are you sure to Recommend the  Briefing Paper');
	}
if(confirmation){

var form = document.getElementById("briefingForm");

if (form) {
var BriefingFwdBtn = document.getElementById("BriefingFwdBtn");
if (BriefingFwdBtn) {
var formactionValue = BriefingFwdBtn.getAttribute("formaction");

form.setAttribute("action", formactionValue);
form.submit();
}
}
else{
return false;
}
}
}

function BriefingRevoke(sheduleId,projectId,BriefingStatus,userId) {
	
	
	$('#sheduleIdRvk').val(sheduleId);
	$('#rvk_projectid').val(projectId);
	$('#briefingStatus').val(BriefingStatus);
	$('#userId').val(userId);
	  var confirmation = confirm('Are you sure to Revoke the  Briefing Paper');
if(confirmation){

var form = document.getElementById("briefingForm");

if (form) {
var BriefingRevokeBtn = document.getElementById("BriefingRevokeBtn");
if (BriefingRevokeBtn) {
var formactionValue = BriefingRevokeBtn.getAttribute("formaction");

form.setAttribute("action", formactionValue);
form.submit();
}
}
else{
return false;
}
}
}

function BriefingReturn(sheduleId,projectId,BriefingStatus,meetingId) {
	$('#briefingReturnmodal').modal('show');
	
	$('#sheduleIdRtn').val(sheduleId);
	$('#rtn_projectid').val(projectId);
	$('#briefingStatusRtn').val(BriefingStatus);
	$('#meetingIdRtn').val(meetingId);
	
	
	var meetingData = meetingId.split('/');
	
	console.log(meetingData[2]);
	$('#commiteeName').val(meetingData[2]);
	$.ajax({
		
		type:'GET',
		url:'getBriefingData.htm',
		datatype:'json',
		data:{
			sheduleId : sheduleId,
		},
		success:function(result){
			var ajaxresult = JSON.parse(result);
			  const date = new Date(ajaxresult[1]);
						   var formattedDate = date.toLocaleDateString('en-GB', {
                        day: '2-digit',
                        month: '2-digit',
                        year: 'numeric',
                        }).replace(/\//g, '-');
		
						   $('.scheduleDate').empty();
						   $('.MeetingDetails').empty();
						   $('.scheduleDate').append(formattedDate);
						   $('.MeetingDetails').append(ajaxresult[0]);
					
		}
		
	});

}

function returnSub() {
	var reply=$('#replyMsg').val();
	if(reply==null || reply=="" || reply=="null"){
		alert("Please enter Reply");
		document.getElementById("replyMsg").style.boxShadow = "rgb(239, 7, 7) 0px 0px 1px 1px";
		return false;
	}else{
	
	 var confirmation = confirm('Are You Sure To Return this Briefing Paper ?');
	  if(confirmation){
		  var form = document.getElementById("returnFrm");
		   
	       if (form) {
	        var returnSubBtn = document.getElementById("returnSubBtn");
	           if (returnSubBtn) {
	               var formactionValue = returnSubBtn.getAttribute("formaction");
	               
	                form.setAttribute("action", formactionValue);
	                 form.submit();
	             }
	        }
	
	  } else{
	  return false;
	  }
	}
}

$('#replyMsg').keyup(function (){
	  $('#replyMsg').css({'-webkit-box-shadow' : 'none', '-moz-box-shadow' : 'none','background-color' : 'none', 'box-shadow' : 'none'});
		  });
		  
		  
$('#pills-imm-property-tab').click(function () {
	$('#projectid').find('option').remove();
	$('#committeecode').find('option').remove();
	$("#projectid").append('<option>All</option>');
	$("#committeecode").append('<option>All</option>');
});

$('#pills-second-property-tab,#pills-mov-property-tab').click(function () {
	var projects = <%= new com.google.gson.Gson().toJson(projectslist) %>;
	var committeecode='<%=committeecode %>';
	var commiteeName='<%=commiteeName %>';
	var projectid='<%=projectid %>';
	var revProjectId='<%=revProjectId %>';
	$('#projectid').find('option').remove();
	$('#committeecode').find('option').remove();
if(commiteeName == "EB"){
	$("#committeecode").append('<option value="PMRB" ' + (commiteeName != null && commiteeName.toUpperCase() === "PMRB" ? 'selected' : '') + '>PMRB</option>');
	
	$("#committeecode").append('<option value="EB" ' + (commiteeName != null && commiteeName.toUpperCase() === "EB" ? 'selected' : '') + '>EB</option>');
}else{
	$("#committeecode").append('<option value="PMRB" ' + (committeecode != null && committeecode.toUpperCase() === "PMRB" ? 'selected' : '') + '>PMRB</option>');
	
	$("#committeecode").append('<option value="EB" ' + (committeecode != null && committeecode.toUpperCase() === "EB" ? 'selected' : '') + '>EB</option>');
}

	$('#projectid').empty();
	if(projects!=null  && projects.length>0){
		var projectOptions = '';
		
		  
        for (var z = 0; z < projects.length; z++) {
          var row = projects[z];
  if(revProjectId!=null &&revProjectId!="null" ){
	  projectOptions+='<option ' + ((revProjectId != null && revProjectId == row[0]) ? 'selected' : '') + ' value="'+row[0]+'" >'+row[4]+'</option>';
  }else{
	  projectOptions+='<option ' + ((projectid != null && projectid == row[0]) ? 'selected' : '') + ' value="'+row[0]+'" >'+row[4]+'</option>';
  }
         
          
        }
        $('#projectid').append(projectOptions);
        
	}
});

function briefingRmks(sheduleId,meetingId) {
	$('#briefingRemarksmodal').modal('show');
	$('.meetingIdRmk').empty();
	
	$('.meetingIdRmk').append(meetingId);
 	$.ajax({
        type: "GET",
        url: "getBriefingRemarks.htm",
        data: {
         sheduleId : sheduleId
        },
        dataType: 'json', 
        success: function(result) {
        	$("#remarksTb").empty();
        	if(result!=null && Array.isArray(result) && result.length>0){
        		
        		
        		  var ReplyAttachTbody = '';
		          for (var z = 0; z < result.length; z++) {
		            var row = result[z];
		            ReplyAttachTbody += '<tr>';
		            ReplyAttachTbody += '<td id="remarksTd1">'+row[0]+' &nbsp; <span id="remarksDate"> '+fDate(row[2])+'</span>';
		            ReplyAttachTbody += '</td>';
		            ReplyAttachTbody += '</tr>';
		            ReplyAttachTbody += '<tr>';
		            ReplyAttachTbody += '<td id="remarksTd2">  '+row[1]+'';
		            ReplyAttachTbody += '</td>';
		            ReplyAttachTbody += '</tr>';
		          }
		          $('#remarksTb').append(ReplyAttachTbody);
        }
        }
	}) 
}
function fDate(fdate) {
    var dateString = fdate;
	var date = new Date(dateString);
	var day = date.getDate().toString().padStart(2, '0'); 
	var month = (date.getMonth() + 1).toString().padStart(2, '0'); 
	var year = date.getFullYear();
	var hours = date.getHours().toString().padStart(2, '0');
	var minutes = date.getMinutes().toString().padStart(2, '0'); 

	var formattedDate = day+'-'+month+'-'+year+' '+hours+':'+minutes;
	return formattedDate;
}
$("textarea").on("keypress", function(e) {
    if (e.which === 32 && !this.value.length)
        e.preventDefault();
});

</script>
</html>