<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.vts.pfms.committee.model.ProgrammeMaster"%>
<%@page import="com.vts.pfms.cars.model.CARSInitiation"%>
<%@page import="org.apache.logging.log4j.core.pattern.IntegerPatternConverter"%>
<%@page import="java.sql.Date"%>
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
<spring:url value="/resources/css/committeeModule/committeeScheduleView.css" var="committeeScheduleView" />
<link href="${committeeScheduleView}" rel="stylesheet" />

<title>COMMITTEE SCHEDULE VIEW</title>

</head>
<body>

	<%
	String logintype = (String) request.getAttribute("logintype");
	int useraccess = Integer.parseInt(request.getAttribute("useraccess").toString());
	Object[] committeescheduleeditdata = (Object[]) request.getAttribute("committeescheduleeditdata");
	List<Object[]> committeeagendalist = (List<Object[]>) request.getAttribute("committeeagendalist");
	List<Object[]> employeelist = (List<Object[]>) request.getAttribute("employeelist");
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	List<Object[]> invitedlist = (List<Object[]>) request.getAttribute("committeeinvitedlist");
	List<Object[]> returndata = (List<Object[]>) request.getAttribute("ReturnData");
	List<Object[]> pfmscategorylist = (List<Object[]>) request.getAttribute("pfmscategorylist");
	String userview = (String) request.getAttribute("userview");
	String otp = (String) request.getAttribute("otp");
	int committeecons = (Integer) request.getAttribute("committeecons");
	List<Object[]> AgendaDocList = (List<Object[]>) request.getAttribute("AgendaDocList");

	String projectid = committeescheduleeditdata[9].toString();
	String divisionid = committeescheduleeditdata[16].toString();
	String initiationid = committeescheduleeditdata[17].toString();
	String carsInitiationId = committeescheduleeditdata[25].toString();
	String programmeId = committeescheduleeditdata[26] != null ? committeescheduleeditdata[26].toString() : "0";
	Object[] projectdetails = (Object[]) request.getAttribute("projectdetails");
	Object[] divisiondetails = (Object[]) request.getAttribute("divisiondetails");
	Object[] initiationdetails = (Object[]) request.getAttribute("initiationdetails");
	CARSInitiation carsInitiationDetails = (CARSInitiation) request.getAttribute("carsInitiationDetails");
	ProgrammeMaster prgmMasterDetails = (ProgrammeMaster) request.getAttribute("prgmMasterDetails");
	
	
	String labcode = (String) session.getAttribute("labcode");
	List<String> SplCommitteeCodes = (List<String>) request.getAttribute("SplCommitteeCodes");
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

			<div class="col-md-12 ">

				<div class="card shadow-nohover">
					<div class="card-header">
						<div class="row">
							<div class="col-md-8 ">
								<h4>
									<%=committeescheduleeditdata[8]!=null?StringEscapeUtils.escapeHtml4(committeescheduleeditdata[8].toString()): " - " %>
									Meeting
									<%if(Long.parseLong(projectid)>0){ %>
									(Project :
									<%=projectdetails[4]!=null?StringEscapeUtils.escapeHtml4(projectdetails[4].toString()): " - " %>)
									<%} %>
									<%if(Long.parseLong(divisionid)>0){ %>
									(Division :
									<%=divisiondetails[1]!=null?StringEscapeUtils.escapeHtml4(divisiondetails[1].toString()): " - "  %>)
									<%} %>
									<%if(Long.parseLong(initiationid)>0){ %>
									(Initiated Project :
									<%=initiationdetails[1]!=null?StringEscapeUtils.escapeHtml4(initiationdetails[1].toString()): " - "  %>)
									<%} %>
									<%if(Long.parseLong(carsInitiationId)>0){ %>
									(CARS :
									<%=carsInitiationDetails.getCARSNo()!=null?StringEscapeUtils.escapeHtml4(carsInitiationDetails.getCARSNo()): " - "  %>)
									<%} %>
									<%if(Long.parseLong(programmeId)>0){ %>
									(Programme :
									<%=prgmMasterDetails.getPrgmName()!=null?StringEscapeUtils.escapeHtml4(prgmMasterDetails.getPrgmName()): " - "  %>
									(<%=prgmMasterDetails.getPrgmCode()!=null?StringEscapeUtils.escapeHtml4(prgmMasterDetails.getPrgmCode()): " - "  %>)
									)
									<%} %>
								</h4>
							</div>
							<div class="col-md-4">
								<% if(userview!=null){%>
								<form action="MySchedules.htm" name="myfrm" method="post">
									<input type="submit" class="btn  btn-sm back float-right scheduleListStyles"
										value="SCHEDULE LIST">
									<input type="hidden" name="${_csrf.parameterName}"
										value="${_csrf.token}" />
								</form>

								<%}else if(Long.parseLong(projectid)>0 && userview==null ){%>
								<form action="ProjectBasedSchedule.htm" name="myfrm"
									method="post">
									<input type="submit" class="btn  btn-sm back float-right scheduleListStyles"
										value="SCHEDULE LIST"
										>
									<input type="hidden" name="committeeid"
										value="<%=committeescheduleeditdata[0]%>"> <input
										type="hidden" name="${_csrf.parameterName}"
										value="${_csrf.token}" /> <input type="hidden"
										name="projectid" value="<%=projectid %>">
								</form>
								<%}else if(Long.parseLong(projectid)==0 && Long.parseLong(divisionid)==0 && Long.parseLong(initiationid)==0 && Long.parseLong(carsInitiationId)==0 && Long.parseLong(programmeId)==0 && userview==null){%>
								<form action="CommitteeScheduleList.htm" name="myfrm"
									method="post">
									<input type="submit" class="btn  btn-sm back float-right scheduleListStyles"
										value="SCHEDULE LIST"
										>
									<input type="hidden" name="committeeid"
										value="<%=committeescheduleeditdata[0]%>"> <input
										type="hidden" name="${_csrf.parameterName}"
										value="${_csrf.token}" /> <input type="hidden"
										name="projectid" value="<%=projectid %>">
								</form>
								<%}else if( Long.parseLong(divisionid)>0){%>
								<form action="DivisionBasedSchedule.htm" name="myfrm"
									method="post">
									<input type="submit" class="btn  btn-sm back float-right scheduleListStyles"
										value="SCHEDULE LIST"
										>
									<input type="hidden" name="committeeid"
										value="<%=committeescheduleeditdata[0]%>"> <input
										type="hidden" name="${_csrf.parameterName}"
										value="${_csrf.token}" /> <input type="hidden"
										name="divisionid" value="<%=divisionid %>">
								</form>
								<%}else if( Long.parseLong(initiationid)>0){%>
								<form action="InitiationBasedSchedule.htm" name="myfrm"
									method="post">
									<input type="submit" class="btn  btn-sm back float-right scheduleListStyles"
										value="SCHEDULE LIST"
										>
									<input type="hidden" name="committeeid"
										value="<%=committeescheduleeditdata[0]%>"> <input
										type="hidden" name="${_csrf.parameterName}"
										value="${_csrf.token}" /> <input type="hidden"
										name="initiationid" value="<%=initiationid %>">
								</form>
								<%}else if( Long.parseLong(carsInitiationId)>0){%>
								<form action="CARSSchedule.htm" name="myfrm" method="post">
									<input type="submit" class="btn  btn-sm back float-right scheduleListStyles"
										value="SCHEDULE LIST"
										>
									<input type="hidden" name="committeeid"
										value="<%=committeescheduleeditdata[0]%>"> <input
										type="hidden" name="${_csrf.parameterName}"
										value="${_csrf.token}" /> <input type="hidden"
										name="carsInitiationId" value="<%=carsInitiationId %>">
								</form>
								<%}else if( Long.parseLong(programmeId)>0){%>
								<form action="PrgmSchedule.htm" name="myfrm" method="post">
									<input type="submit" class="btn  btn-sm back float-right scheduleListStyles"
										value="SCHEDULE LIST"
										>
									<input type="hidden" name="committeeid"
										value="<%=committeescheduleeditdata[0]%>"> <input
										type="hidden" name="${_csrf.parameterName}"
										value="${_csrf.token}" /> <input type="hidden"
										name="programmeId" value="<%=programmeId %>">
								</form>
								<%} %>
							</div>
						</div>
					</div>

					<div class="card-body">

						<%if(useraccess>=1 && Integer.parseInt(committeescheduleeditdata[10].toString())<5){%>
						<form action="CommitteeScheduleDelete.htm" method="post">
							<%} %>
							<h5 class="scheduleMeetingColor">
								Schedule (Meeting Id :
								<%=committeescheduleeditdata[11]%>)
								<%if(useraccess>=1 && Integer.parseInt(committeescheduleeditdata[10].toString())<5){%>
								<button class="fa fa-trash btn btn-danger bg-white border border-white" type="submit"
									onclick="return confirm('Are You Sure To Delete this Meeting?');"></button>
								<input type="hidden" name="${_csrf.parameterName}"
									value="${_csrf.token}" /> <input type="hidden"
									name="scheduleid" value="<%=committeescheduleeditdata[6]%>">
								<%} %>
							</h5>
							<%if(useraccess>=1 && Integer.parseInt(committeescheduleeditdata[10].toString())<5){%>
						</form>
						<%} %>


						<hr>

						<form action="CommitteeScheduleUpdate.htm" id="editform"
							method="post">

							<table class="mt-10">
								<tr>
									<td><label class="control-label">Meeting Date :
											&nbsp;</label></td>
									<td>
										<%LocalDate todaydate= LocalDate.now();
					   					 		 LocalDate scheduledate=Date.valueOf(committeescheduleeditdata[2].toString()).toLocalDate();				   		
					   							if(Integer.parseInt(committeescheduleeditdata[10].toString())<5)
					   							{ %> <input class="form-control"
										data-date-format="dd/mm/yyyy" id="readonlystartdate"
										name="committeedate" readonly="readonly" required="required"
										value=" <%= sdf.format(sdf1.parse( committeescheduleeditdata[2].toString()))%>">
										<%}else{ %> <input class="form-control"
										data-date-format="dd/mm/yyyy" id="startdate"
										name="committeedate" required="required" readonly="readonly"
										value=" <%= sdf.format(sdf1.parse( committeescheduleeditdata[2].toString()))%>">
										<%} %>

									</td>
									<td class="pl-20"><label
										class="control-label">Meeting Time : &nbsp;</label></td>
									<td><input class="form-control" type="text" id="starttime"
										name="committeetime" readonly="readonly" required="required"
										value="<%=committeescheduleeditdata[3]!=null?StringEscapeUtils.escapeHtml4(committeescheduleeditdata[3].toString()): "" %>"></td>

									<th class="pl-40">
										<%if(useraccess>=1){ %> <input type="hidden" name="scheduleid"
										value="<%=committeescheduleeditdata[6]%>"> <%					   		
						   							if(Integer.parseInt(committeescheduleeditdata[10].toString())<6 && (userview==null || userview.equalsIgnoreCase("CS") || userview.equalsIgnoreCase("CC") )) { %>
										<input type="submit" id="update" class="btn  btn-sm submit"
										value="SUBMIT" onclick="Add(myfrm)"> <%}else{ %> <script
											type="text/javascript">
						   									$("#startdate").prop('disabled', true);
						   									$("#starttime").prop('disabled', true);
						   									</script> <%} %> <input type="hidden"
										name="${_csrf.parameterName}" value="${_csrf.token}" /> <%} %>
									</th>
								</tr>
							</table>
						</form>
						<br>
						<hr>
						<form action="CommitteeVenueUpdate.htm" method="post"
							id="venuupdatefrm">
							<table class="mt-10 venueUpdateTblWidth">
								<tr>
									<td class="width-100px"><label class="control-label pr-10px">Venue : &nbsp;</label></td>
									<td><input class="form-control" type="text" name="venue"
										id="venue" required="required" placeholder="Enter the Venue"
										<%if(committeescheduleeditdata[12]!=null){ %>
										value="<%=StringEscapeUtils.escapeHtml4(committeescheduleeditdata[12].toString()) %>"
										<%} %>></td>



									<td class="pl-20"><label
										class="control-label">Category :&nbsp;</label></td>
									<td><select class="custom-select " id="isconfidential"
										required="required" name="isconfidential">
											<option disabled="true" selected value="">Choose...</option>
											<% for (Object[] obj : pfmscategorylist) {%>
											<option value="<%=obj[0]%>"
												<% if(committeescheduleeditdata[13].toString().equals(obj[0].toString())) {%>
												selected <%} %>><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "%></option>
											<%} %>
									</select></td>
								</tr>
								<% if( Long.parseLong(programmeId)==0){%>
								<tr>
									<td colspan="4"><label class="mb-n20">Briefing
											of the meeting : &nbsp;</label></td>
								<tr>
									<td colspan="4"><textarea class="form-control"
											name="decisions" id="decisions" cols="50" rows="5"
											placeholder="Decisions sought from Meeting" maxlength="1000"><%if(committeescheduleeditdata[18]!=null ){ %> <%=committeescheduleeditdata[18].toString() %> <%} %></textarea></td>
								</tr>
								<tr>
									<td><label class="mt-25px mb-n20">Reference
											: &nbsp;</label></td>
									<td colspan="3"><input class="form-control" type="text"
										name="reference" id="reference"
										placeholder="Reference for this Meeting"
										<%if(committeescheduleeditdata[14]!=null){ %>
										value="<%=StringEscapeUtils.escapeHtml4(committeescheduleeditdata[14].toString()) %>"
										<%} %>></td>
								</tr>
								<%} %>
								<tr>
									<td colspan="4" align="center">
										<%if(useraccess>=1){ %> <%if(Integer.parseInt(committeescheduleeditdata[10].toString())<6) { %>

										<input type="hidden" name="${_csrf.parameterName}"
										value="${_csrf.token}" /> <input type="hidden"
										name="scheduleid" value="<%=committeescheduleeditdata[6]%>">

										<button type="submit" class="btn  btn-sm edit" name="update"
											value="SUBMIT" onclick="return VenueUpdate('venuupdatefrm');">UPDATE</button>

										<% }else{ %> <script type="text/javascript">
							             					$('#venue').prop('readonly','readonly');
							             					$('#isconfidential').attr('disabled',true);
							             					$('#reference').prop('readonly','readonly');
							             					$('#decisions').prop('readonly','readonly');
							             				</script> <%} %> <%}else{ %> <script type="text/javascript">
						             				$('#venue').prop('readonly','readonly');
						             				$('#isconfidential').attr('disabled',true);
						             				$('#reference').prop('readonly','readonly');
						             				$('#decisions').prop('readonly','readonly');
						             			</script> <%} %>

									</td>
								</tr>
							</table>
						</form>
						<hr>
						<br>
						<h5 class="scheduleMeetingColor">Agenda</h5>
						<hr>
						<%if(!committeeagendalist.isEmpty()){ %>

						<table
							class="table table-bordered table-hover table-striped table-condensed ">
							<thead>
								<tr>
									<th>SN</th>
									<th>Agenda Item</th>
									<th>Reference</th>
									<%if( Long.parseLong(programmeId)>0){ %>
									<th>Group Name</th>
									<%} %>
									<th>Remarks</th>
									<th>Presenter</th>
									<th>Duration (Mins)</th>
									<th>Attachment</th>
								</tr>
							</thead>
							<%	int count=0;
							for(Object[] 	obj:committeeagendalist){ count++;%>

							<tbody>



								<tr>

									<td class="text-center"><%=count%></td>
									<td><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - "%></td>
									<td><%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - " %>
									</td>
									<%if( Long.parseLong(programmeId)>0){ %>
										<td><%=(obj[15]!=null && obj[15].toString().length()>0)?StringEscapeUtils.escapeHtml4(obj[15].toString()): " - " %></td>
									<%} %>
									<td><%=obj[6]!=null?StringEscapeUtils.escapeHtml4(obj[6].toString()): " - " %></td>
									<td><%=obj[10]!=null?StringEscapeUtils.escapeHtml4(obj[10].toString()): " - "%>,
										<%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - " %>
									</td>
									<td><%=obj[12]!=null?StringEscapeUtils.escapeHtml4(obj[12].toString()): " - " %></td>
									<td>

										<table>
											<%for(Object[] doc : AgendaDocList) { 
											if(obj[0].toString().equalsIgnoreCase(doc[1].toString())){%>
											<tr>
												<td><%= doc[3]!=null?StringEscapeUtils.escapeHtml4(doc[3].toString()): " - " %>
													<%= " <span class='text-muted'> Ver " %> <%= doc[4]!=null?StringEscapeUtils.escapeHtml4(doc[4].toString()): " - " %>
													. <%= doc[5]!=null?StringEscapeUtils.escapeHtml4(doc[5].toString()): " - " %>
													<%="</span>" %></td>
												<%if(Long.parseLong(initiationid) > 0){ %>
												<td class="agendaTdWidth"><a
													href="PrePRojectAgendaDocLinkDownload.htm?filerepid=<%=doc[2]%>"
													target="blank"><i class="fa fa-download text-primary"
														 aria-hidden="true"></i></a></td>
												<%}else{ %>
												<td class="agendaTdWidth"><a
													href="AgendaDocLinkDownload.htm?filerepid=<%=doc[2]%>"
													target="blank"><i class="fa fa-download text-primary"
														 aria-hidden="true"></i></a></td>
												<%} %>
											
											<tr>
												<%} }%>
											
										</table>

									</td>

								</tr>
							</tbody>
							<%} %>

						</table>

						<% }else if(Integer.parseInt(committeescheduleeditdata[10].toString())<5 ){ %>
						<br>
						<h6 align="center">Kindly Add Agenda ..!!</h6>

						<%} else{%>
						<br>
						<h6>No Agenda Defined ..!!</h6>
						<%} %>

						<div class="form-group mt-25px" align="center">
							<%	if(Integer.parseInt(committeescheduleeditdata[10].toString())<6 && (userview==null || userview.equalsIgnoreCase("CS") || userview.equalsIgnoreCase("CC") )) { %>
							<%if(!committeeagendalist.isEmpty()){%>
							<%if(useraccess>1){ %>
							<form <%if(programmeId.equals("0")) {%>
								action="CommitteeScheduleAgenda.htm" <%} else {%>
								action="PrgmScheduleAgenda.htm" <%} %> method="post">
								<input type="hidden" name="scheduleid"
									value="<%=committeescheduleeditdata[6] %>"> <input
									type="hidden" name="${_csrf.parameterName}"
									value="${_csrf.token}" />
								<button type="submit" class="btn  btn-sm  edit editPadding mr-p5"
									>EDIT</button>
							</form>
							<%} %>

							<%} else if(!committeescheduleeditdata[4].toString().equalsIgnoreCase("MAA") && useraccess>=1){%>
							<form <%if(programmeId.equals("0")) {%>
								action="CommitteeScheduleAgenda.htm" <%} else {%>
								action="PrgmScheduleAgenda.htm" <%} %> method="post">
								<input type="hidden" name="scheduleid"
									value="<%=committeescheduleeditdata[6] %>"> <input
									type="hidden" name="${_csrf.parameterName}"
									value="${_csrf.token}" />
								<button type="submit" class="btn  btn-sm  add editPadding"
									>ADD</button>
							</form>
							<%} %>
							<%}%>


						</div>
						<hr>
						<br>

						<%if( committeescheduleeditdata[4].toString().equalsIgnoreCase("MAR") || committeescheduleeditdata[4].toString().equalsIgnoreCase("MMR")  ){ %>
						<h5 class="scheduleMeetingColor">Reason For Returning</h5>
						<hr>
						<%for(Object[] obj : returndata){ %>

						<%if(committeescheduleeditdata[4].toString().equalsIgnoreCase(obj[2].toString())) {%>

						<b> &bull; <%=obj[0]!=null?StringEscapeUtils.escapeHtml4(obj[0].toString()): " - " %></b>

						<%} %>


						<%} %>
						<br>
						<br>
						<%} %>

						<%if(useraccess>=1){ %>
						<%if(committeecons==1  ){ %>
						<div>
							<h5 class="operationsColor">Operations</h5>

							<%if( committeescheduleeditdata[4].toString().equalsIgnoreCase("MMF") || committeescheduleeditdata[4].toString().equalsIgnoreCase("MMA") || committeescheduleeditdata[4].toString().equalsIgnoreCase("MMR")  ){ %>

							<form method="POST" action="CommitteeMinutesViewAllDownload.htm"
								name="myfrm" id="myfrm" class="float-right">

								<%if(committeescheduleeditdata[20]!=null){ %>
								<a class="btn  btn-sm view text-white attachedMinutesBgColor fs-12px"
									href="MinutesAttachDownload.htm?attachmentid=<%=committeescheduleeditdata[20] %>"
									target="_blank"
									>ATTACHED
									MINUTES </a>
								<%} %>
								<%if(Long.parseLong(projectid)>0){ %>
								<input type="submit" class="btn  btn-sm view text-white fs-12px committeeMinutesDownloadBgColor"
									value="MINUTES 2021"
									formaction="CommitteeMinutesNewDownload.htm"
									formtarget="_blank" formmethod="get"
									>
								<%} %>
								<button type="submit" class="btn btn-sm prints my-2 my-sm-0 fs-12px"
									formtarget="_blank" >VIEW
									MINUTES</button>
								<button type="submit" class="btn  btn-sm view text-white fs-12px committeeMinutesDownloadBgColor"
									formaction="MeetingTabularMinutesDownload.htm"
									formtarget="_blank"
									>TABULAR
									MINUTES</button>

								<input type="hidden" name="committeescheduleid"
									value="<%=committeescheduleeditdata[6] %>"> <input
									type="hidden" name="${_csrf.parameterName}"
									value="${_csrf.token}" />
							</form>

							<%} %>

						</div>

						<%}else{ %>
						<div class="row">

							<h5>
								<span class="ml-30px fs-20px">Note :
									&nbsp;</span><span class="text-danger"><%=committeescheduleeditdata[7]!=null?StringEscapeUtils.escapeHtml4(committeescheduleeditdata[7].toString()): " - " %>
									<%if(Integer.parseInt(projectid)>0){ %> For <%=projectdetails[4]!=null?StringEscapeUtils.escapeHtml4(projectdetails[4].toString()): " - "  %>
									<%} %> is Not Constituted </span>
							</h5>
						</div>
						<%} %>


						<hr class="lineMargin">

						<div class="form-block">

							<form action="CommitteeScheduleMinutes.htm" name="myfrm"
								id="myfrm" method="POST">


								<%if(committeescheduleeditdata[4].toString().equalsIgnoreCase("MAA") || committeescheduleeditdata[4].toString().equalsIgnoreCase("MKO") || committeescheduleeditdata[4].toString().equalsIgnoreCase("MKV") || committeescheduleeditdata[4].toString().equalsIgnoreCase("MMF") || committeescheduleeditdata[4].toString().equalsIgnoreCase("MMA") || committeescheduleeditdata[4].toString().equalsIgnoreCase("MMR") ){ %>
								<button type="button" class="btn  btn-sm submit bg-success agendaApprovedMargin"
									>
									<i class="fa fa-check agendaApprovedIconFs" aria-hidden="true"
										></i> Agenda Approved
								</button>
								<%} %>

								<%if(committeescheduleeditdata[4].toString().equalsIgnoreCase("MKV") || committeescheduleeditdata[4].toString().equalsIgnoreCase("MMF") || committeescheduleeditdata[4].toString().equalsIgnoreCase("MMA") || committeescheduleeditdata[4].toString().equalsIgnoreCase("MMR") ){ %>
								<button type="button" class="btn  btn-sm submit meetingKickedOffStyle">
									<i class="fa fa-check agendaApprovedIconFs" aria-hidden="true"></i> Meeting Kicked Off
								</button>
								<%} %>

								<%if(committeescheduleeditdata[4].toString().equalsIgnoreCase("MMA")){ %>
								<button type="button" class="btn  btn-sm submit minutesApprovedStyle">
									<i class="fa fa-check agendaApprovedIconFs" aria-hidden="true"></i> Minutes Approved
								</button>
								<%} %>






								<%if( committeeagendalist.size()>0  ){
														if(Integer.parseInt(committeescheduleeditdata[10].toString())<3 && invitedlist.size()>0)
															{%>
								<input type="submit" id="submit" class="btn  btn-sm back backBgColor"
									value="Participants" formaction="CommitteeInvitations.htm"/>
								<%}
															else if(invitedlist.size()>0 && Integer.parseInt(committeescheduleeditdata[10].toString())<8 ){ %>
								<input type="submit" id="submit" class="btn  btn-sm submit backBgColor backBgBorder"
									value="Participants" formaction="CommitteeInvitations.htm"/>
								<%}else if(committeescheduleeditdata[4].toString().equalsIgnoreCase("MSC") && committeecons==1 ) { %>
								<input type="submit" id="submit" class="btn  btn-sm submit backBgColor"
									value="Invite" onclick="invite()"
									formaction="CommitteeInvitations.htm"/>

								<%}
														} %>









								<%if(committeescheduleeditdata[4].toString().equalsIgnoreCase("MKV") ){ %>

								<input type="submit" id="submit" class="btn  btn-sm submit committeeAttendanceSubmitStyles"
									value="Attendance" formaction="CommitteeAttendance.htm"/>
								<input type="hidden" name="membertype" value="CC" />

								<%} %>

								<%if(committeescheduleeditdata[4].toString().equalsIgnoreCase("MKV") || committeescheduleeditdata[4].toString().equalsIgnoreCase("MMR") ){ %>

								<input type="submit" id="submit" class="btn  btn-sm submit minutesSubmitStyle"
									value="Minutes"/>


								<%} %>

								<%if(Integer.parseInt(committeescheduleeditdata[10].toString())>=7){ %>


								<input type="submit" id="submit" name="sub"
									class="btn  btn-sm view minutesApprovalSubmitStyles" value="Minutes Approval"
									formaction="MeetingMinutesApproval.htm"/>

								<%} %>


								<input type="hidden" name="committeescheduleid"
									value="<%=committeescheduleeditdata[6] %>"> <input
									type="hidden" name="${_csrf.parameterName}"
									value="${_csrf.token}" /> <input type="hidden"
									name="projectid" value="<%=projectid %>">

							</form>
							<form action="MeetingAgendaApproval.htm" name="myfrm1"
								id="myfrm1" method="post">
								<%if(committeeagendalist.size()>0 && invitedlist.size()>0 ){ %>

								<%if(committeescheduleeditdata[4].toString().equalsIgnoreCase("MSC") ) { %>
								<input type="submit" id="submit" name="sub"
									class="btn  btn-sm view minutesApprovalSubmitStyles" value="Agenda Approval"
									onclick="checkagenda()"/>
								<%} %>
								<%if(committeescheduleeditdata[4].toString().equalsIgnoreCase("MAF")){ %>
								<button type="button" class="btn  btn-sm view minutesApprovalSubmitStyles">
									<i class="fa fa-forward agendaApprovedIconFs" aria-hidden="true"></i> Agenda Forwarded
								</button>
								<%} %>
								<%if(committeescheduleeditdata[4].toString().equalsIgnoreCase("MAR")){ %>
								<input type="submit" id="submit" name="sub"
									class="btn  btn-sm view minutesApprovalSubmitStyles" value="Re-Submit Agenda Approval"
									onclick="checkagenda()"/>
								<%} %>

								<%} %>

								<input type="hidden" name="${_csrf.parameterName}"
									value="${_csrf.token}" /> <input type="hidden"
									name="committeescheduleid"
									value="<%=committeescheduleeditdata[6] %>">

							</form>

							<form action="KickOffMeeting.htm" name="myfrm" id="myfrm"
								method="post">

								<%if(committeescheduleeditdata[4].toString().equalsIgnoreCase("MAA") && !todaydate.isBefore(scheduledate) ){ %>

								<input type="submit" id="submit" name="sub"
									class="btn  btn-sm submit fa-input kickOffMeetingSubmitStyle"
									value=" &#xf017; &nbsp;&nbsp;Kick Off Meeting "
									onclick="return kickoff()"
									/>

								<%} %>

								<%if( committeescheduleeditdata[4].toString().equalsIgnoreCase("MKO") ){ %>

								<div class="otpDivStyle">
									<span class="font-weight-600">Enter OTP : </span> <input
										class="form-control otpInputStyle" type="password" id="otp" name="otpvalue"
										maxlength="4" <%if(otp!=null){ %>
										value="<%=StringEscapeUtils.escapeHtml4(otp) %>" <%} %>
										required="required" value="">
									<input type="submit" id="submit" name="sub"
										class="btn  btn-sm submit validateInputStyle" value="Validate" onclick=""/> 
										<input type="submit" id="submit"
										name="sub" class="btn  btn-sm view" value="Resend OTP"
										onclick="resendotp()"/>

								</div>

								<%} %>

								<input type="hidden" name="${_csrf.parameterName}"
									value="${_csrf.token}" /> <input type="hidden"
									name="committeescheduleid"
									value="<%=committeescheduleeditdata[6] %>"> <input
									type="hidden" name="committeemainid"
									value="<%=committeescheduleeditdata[1] %>"> <input
									type="hidden" name="committeeshortname"
									value="<%=committeescheduleeditdata[8] %>"> <input
									type="hidden" name="meetingdate"
									value="<%=committeescheduleeditdata[2] %>"> <input
									type="hidden" name="meetingtime"
									value="<%=committeescheduleeditdata[3] %>"> <input
									type="hidden" name="meetingid"
									value="<%=committeescheduleeditdata[11] %>"> <input
									type="hidden" name="projectid" value="<%=projectid %>">
								<input type="hidden" name="committeeid"
									value="<%=committeescheduleeditdata[0]%>">

							</form>

							<%} else{ %>
							<h5>
								<span class="fs-20px ml-30px">Note :
									&nbsp;</span> <span class="text-danger">-</span>
							</h5>

							<%} %>


							<hr class="lineMargin">

							<form action="#" method="post" id="myfrm" target="_blank">
								<%if(Integer.parseInt(committeescheduleeditdata[10].toString())>=5) { %>

								<button type="submit" class="btn btn-sm agendaPresentationSubmitStyle"
									formaction="AgendaPresentation.htm" formmethod="post"
									formtarget="_blank" title="Agenda Presentation">
									<img class="presentationImgWidth" alt="" src="view/images/presentation.png">
								</button>

								<input type="hidden" name="scheduleid"
									value="<%=committeescheduleeditdata[6] %>"> <input
									type="hidden" name="committeeid"
									value="<%=committeescheduleeditdata[0]%>"> <input
									type="hidden" name="projectid" value="<%=projectid %>">
								<input type="hidden" name="${_csrf.parameterName}"
									value="${_csrf.token}" />
								<%} %>
								<%if(!todaydate.isAfter(scheduledate)) {%>
								<% if(committeescheduleeditdata[23].toString().equalsIgnoreCase("Y")){%>
								<button type="submit" class="btn btn-sm back"
									formaction="MeetingBriefingPaper.htm" name="scheduleid"
									value="<%=committeescheduleeditdata[6] %>" formmethod="get"
									data-toggle="tooltip" data-placement="bottom"
									title="Briefing Paper">BRIEFING</button>
								<%}%>
								<%} %>
							</form>
						</div>

					</div>

				</div>
			</div>
		</div>
	</div>
</body>


<script type="text/javascript">
function char_count(str, letter) 
{
 var letter_Count = 0;
 for (var position = 0; position < str.length; position++) 
 {
    if (str.charAt(position) == letter) 
      {
      letter_Count += 1;
      }
  }
  return letter_Count;
}

 function VenueUpdate(frmid){
		var venue=$('#venue').val();
		var charcou=char_count(venue,' ');
		if(venue!='' && char_count(venue,' ')==venue.length  )
			{
				event.preventDefault();
				alert('Please Enter Valid Venue Name');
				return false;
			}
		else{
			var ret=confirm('Are You Sure To Save This venue?');
			return ret;
		}
	} 
	


	function kickoff(){
		<%if(committeescheduleeditdata[12]!=null){%>
		var venue='<%=StringEscapeUtils.escapeHtml4(committeescheduleeditdata[12].toString())%>';
		<%}else{%>
		var venue='';
		<%}%>
		if(venue=='' || char_count(venue,' ')==venue.length)
			{				
				event.preventDefault();
				alert('Please Update Proper Venue Details !');
			}else{
				
				var ret=confirm('Are you to sure to Kick off the Meeting ?');
				if (ret==false){
					event.preventDefault();
				}
			}
		}	
	
	function invite(){
		<%if(committeescheduleeditdata[12]!=null){%>
		var venue='<%=StringEscapeUtils.escapeHtml4(committeescheduleeditdata[12].toString())%>';
		<%}else{%>
		var venue='';
		<%}%>
		if(venue=='' || char_count(venue,' ')==venue.length)
			{				
				event.preventDefault();
				alert('Please Update Proper Venue Details !');
			}else{
				
				
				if (ret==false){
					event.preventDefault();
					return false;
				}
				else if(ret==true)
					{
					return true;
					}
			}		
	}	
</script>



<script>

$('#startdate').daterangepicker({
	"singleDatePicker" : true,
	"linkedCalendars" : false,
	"showCustomRangeLabel" : true,
	"cancelClass" : "btn-default",
	/* "minDate" :new Date(), */
	showDropdowns : true,
	locale : {
		format : 'DD-MM-YYYY'
	}
});

$('#readonlystartdate').daterangepicker({
	"singleDatePicker" : true,
	"linkedCalendars" : false,
	"showCustomRangeLabel" : true,
	/* "minDate":new Date(), */
	"cancelClass" : "btn-default",
	showDropdowns : true,
	locale : {
		format : 'DD-MM-YYYY'
	}
});

 
$(function() {
	   $('#starttime').daterangepicker({
	            timePicker : true,
	            singleDatePicker:true,
	            timePicker24Hour : true,
	            timePickerIncrement : 1,
	            timePickerSeconds : false,
	            locale : {
	                format : 'HH:mm'
	            }
	        }).on('show.daterangepicker', function(ev, picker) {
	            picker.container.find(".calendar-table").hide();
	   });
	})
 
	
function Add(myfrm){
	
	event.preventDefault();
	
	var date=$("#readonlystartdate").val();
	var time=$("#starttime").val();
	
	bootbox.confirm({ 
 		
	    size: "large",
		message: "<center></i>&nbsp;&nbsp;&nbsp;&nbsp;<b class='editbox'>Are You Sure To Update Schedule to "+date+" &nbsp;("+ time +") ?</b></center>",
	    buttons: {
	        confirm: {
	            label: 'Yes',
	            className: 'btn-success'
	        },
	        cancel: {
	            label: 'No',
	            className: 'btn-danger'
	        }
	    },
	    callback: function(result){ 
	 
	    	if(result){
	    	
	    		$("sub").value;
	         $("#editform").submit(); 
	    	}
	    	else{
	    		event.preventDefault();
	    	}
	    } 
	}) 
	
	
}	

function checkagenda(){
	
	<%if(committeeagendalist.size()==0){%>
	
		alert('Kindly Add Agenda Inorder to Forward ..!');
		event.preventDefault();
	
	<%}else{%>
	
	if(confirm('Are you sure to Forward for Agenda Approval')==false){
		 
		 event.preventDefault();
	 }
	<%}%>
	
	
	
}

function resendotp(){
	
	$('#otp').prop('required',false);
}
 
</script>
<br>
<br>
</html>
