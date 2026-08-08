<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.vts.pfms.master.model.IndustryPartner"%>
<%@page import="java.awt.Container"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="java.time.LocalDate"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"
	import="java.util.*,com.vts.*,java.text.SimpleDateFormat"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/committeeModule/ViewCommitteeInvitation.css" var="ViewCommitteeInvitation" />
<link href="${ViewCommitteeInvitation}" rel="stylesheet" />
<title>COMMITTEE INVITATION</title>
</head>
<body>

		
	<%
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");

	String labcode=(String)session.getAttribute("labcode");
	List<Object[]> ExpertList=(List<Object[]>) request.getAttribute("ExpertList");
	List<Object[]> EmployeeList=(List<Object[]>) request.getAttribute("EmployeeList");
	List<Object[]> committeeinvitedlist=(List<Object[]>)request.getAttribute("committeeinvitedlist");
	String committeescheduleid=(String)request.getAttribute("committeescheduleid");
	Object[] committeescheduledata=(Object[])request.getAttribute("committeescheduledata");
	List<Object[]> agendalist=(List<Object[]>) request.getAttribute("agendalist");
	List<Object[]> clusterlablist=(List<Object[]>) request.getAttribute("clusterlablist");
	List<Object[]> committeereplist=(List<Object[]>) request.getAttribute("committeereplist");
	int pscount=0;
	int CoChair=0;
	String labid=(String)request.getAttribute("labid");
	LocalDate scheduledate=LocalDate.parse(committeescheduledata[2].toString());
	LocalDate todaydate=LocalDate.now();	
	
	List<String> InvitedList = new ArrayList<String>();
	committeeinvitedlist.stream().map(obj -> InvitedList.add(obj[0].toString()+"_"+obj[11].toString())).collect(Collectors.toList());;
	
	// Prudhvi - 27/03/2024 
	/* --------- start -------------- */
	List<IndustryPartner> industryPartnerList = (List<IndustryPartner>)request.getAttribute("industryPartnerList");
	/* --------- end -------------- */
	//
	
	List<Object[]> agendaList=(List<Object[]>) request.getAttribute("agendaList");
	String ccmFlag = (String)request.getAttribute("ccmFlag");
	String committeeId = (String)request.getAttribute("committeeId");
	String committeemainid = (String)request.getAttribute("committeemainid");
	
	List<Object[]> DesignationList=(List<Object[]>)request.getAttribute("designationlist");
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
				
					<div class="card-header">
						<div class="row">
							<div class="col-md-3" >
					  			<h4><%=committeescheduledata[8]!=null?StringEscapeUtils.escapeHtml4(committeescheduledata[8].toString()): " - " %> Invitations</h4>
							 </div>
							 <div class="col-md-9 mt-3px" align="right">
					 			<h5 class="colorWhite">(Meeting Id : <%=committeescheduledata[12]!=null?StringEscapeUtils.escapeHtml4(committeescheduledata[12].toString()): " - " %>) &nbsp;&nbsp; - &nbsp;&nbsp; (Meeting Date & Time : <%= sdf.format(sdf1.parse(StringEscapeUtils.escapeHtml4( committeescheduledata[2].toString())))%>  &  <%=committeescheduledata[3]!=null?StringEscapeUtils.escapeHtml4(committeescheduledata[3].toString()): " - " %>)</h5>
							 </div>
					 	</div>
					</div>
				
					<div class="card-body">
					
<!-- -------------------------------------------------------------------------------------------------------------------------------------------------------------- -->					
					<div class="row">
						<div class="col-md-4">
							<table>
								<tr>
									<td><label class="control-label">Chairperson </label></td>
								</tr>
								<tr>																	
									<% for(Object[] committeeinvited:committeeinvitedlist )												
										{%>												
											<%if(committeeinvited[3].toString().equals("CC"))
											{ %>
												<td><%=committeeinvited[6]!=null?StringEscapeUtils.escapeHtml4(committeeinvited[6].toString()): " - "%>, <%= committeeinvited[7]!=null?StringEscapeUtils.escapeHtml4(committeeinvited[7].toString()): " - "%> (<%= committeeinvited[11]!=null?StringEscapeUtils.escapeHtml4(committeeinvited[11].toString()): " - "%>) </td>
												<%if( committeeinvited[9].toString().equalsIgnoreCase("Y")){ %>
												<td class="pl-10px">
											 	<i class="fa fa-check text-success" aria-hidden="true"></i> 
												</td>
												
												
												
											<%} 
											break;} 											
									}%>														
								</tr>
							</table>
														
						</div>
						<div class="col-md-4">
							<table>
								<tr>
									<td><label class="control-label">Member Secretary   </label></td>
								</tr>
								<tr>
									<% for(Object[] committeeinvited:committeeinvitedlist )												
										{%>												
											<%if(committeeinvited[3].toString().equals("CS"))
											{%>
												<td><%=committeeinvited[6]!=null?StringEscapeUtils.escapeHtml4(committeeinvited[6].toString()): " - "%>, <%= committeeinvited[7]!=null?StringEscapeUtils.escapeHtml4(committeeinvited[7].toString()): " - "%> (<%= committeeinvited[11]!=null?StringEscapeUtils.escapeHtml4(committeeinvited[11].toString()): " - "%>) </td>
												<%if( committeeinvited[9].toString().equalsIgnoreCase("Y")){ %>
											<td class="pl-10px">
											 	<i class="fa fa-check text-success" aria-hidden="true"></i> 
												</td>
												
											<%} 
											} 	
											if(committeeinvited[3].toString().equals("PS"))
											{
												pscount++;
											}
											else if( committeeinvited[3].toString().equals("CH"))
											{
												CoChair++;
											}
										}%>		
								</tr>
							</table>
													
						</div>
						<%if(pscount>0){ %>
						<div class="col-md-4">
							
							<table>
								<tr>
									<td><label class="control-label"> Member Secretary (Proxy)  </label></td>
								</tr>
								<tr>										
									<% for(Object[] committeeinvited:committeeinvitedlist )												
										{%>												
											<%if(committeeinvited[3].toString().equals("PS"))
											{%>
												<td><%=committeeinvited[6]!=null?StringEscapeUtils.escapeHtml4(committeeinvited[6].toString()): " - "%>, <%= committeeinvited[7]!=null?StringEscapeUtils.escapeHtml4(committeeinvited[7].toString()): " - "%> (<%= committeeinvited[11]!=null?StringEscapeUtils.escapeHtml4(committeeinvited[11].toString()): " - "%>) </td>
												<%if( committeeinvited[9].toString().equalsIgnoreCase("Y")){ %>
												<td class="pl-10px">
											 	<i class="fa fa-check text-success" aria-hidden="true"></i> 
												</td>
											<%} 
											break;} 											
									}%>											
								</tr>
							</table>
						</div>	
						<%} %>
						
						<%if(CoChair>0){ %>
						<div class="col-md-4">
							
							<table>
								<tr>
									<td><label class="control-label"> Co-Chairperson  </label></td>
								</tr>
								<tr>										
									<% for(Object[] committeeinvited:committeeinvitedlist )												
										{%>												
											<%if(committeeinvited[3].toString().equals("CH"))
											{%>
												<td><%=committeeinvited[6]!=null?StringEscapeUtils.escapeHtml4(committeeinvited[6].toString()): " - "%>, <%= committeeinvited[7]!=null?StringEscapeUtils.escapeHtml4(committeeinvited[7].toString()): " - "%> (<%= committeeinvited[11]!=null?StringEscapeUtils.escapeHtml4(committeeinvited[11].toString()): " - "%>) </td>
												<%if( committeeinvited[9].toString().equalsIgnoreCase("Y")){ %>
												<td class="pl-10px">
											 	<i class="fa fa-check text-success" aria-hidden="true"></i> 
												</td>
												
												
											<%} 
											break;} 											
									}%>											
								</tr>
							</table>
						</div>	
					<%} %>					
					</div>
					<br> 
					
					<div class="row">
								<div  class="col-md-4">
								
									<h5> Internal Members</h5> 
										<hr>									
									 <table border='0'>
			
										<tbody>
											<%	int memberscount = 1;
											for (Object[] obj : committeeinvitedlist ) {
												if(obj[3].toString().equals("CI")){								
											%>
											<tr>			
												<td class="tdclass"><%=memberscount%> )</td> <td> <%=obj[6]!=null?StringEscapeUtils.escapeHtml4(obj[6].toString()): " - "%>, <%=obj[7]!=null?StringEscapeUtils.escapeHtml4(obj[7].toString()): " - "%> (<%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - "%>)</td>
												<%if( obj[9].toString().equalsIgnoreCase("Y")){ %>
															<td class="pl-10px">
														 	<i class="fa fa-check text-success" aria-hidden="true"></i> 
															</td>
														<%} %>			
											</tr>
											<%	memberscount++; }}	%>		
											
										</tbody>
									</table>						
									<br>	
								
							</div>
					
					
						<div  class="col-md-4">
						
						<h5>External Members (Within DRDO)</h5>
							<hr>
						<%int 	externalcount=1;
						if(memberscount!=committeeinvitedlist.size()) %>
						 <table border='0'>
						 	<tbody>
								<% 
								for (Object[] obj : committeeinvitedlist ) {
									if(obj[3].toString().equals("CW")){  	%>
									<tr>			
										<td class="tdclass"><%=externalcount%> )</td> <td> <%=obj[6]!=null?StringEscapeUtils.escapeHtml4(obj[6].toString()): " - "%>, <%=obj[7]!=null?StringEscapeUtils.escapeHtml4(obj[7].toString()): " - "%> (<%=obj[11] !=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - "%>)</td>
									<%if( obj[9].toString().equalsIgnoreCase("Y")){ %>
										<td class="pl-10px">
									 	<i class="fa fa-check text-success" aria-hidden="true"></i> 
										</td>
									<%} %>
			
									</tr>
									
								<%	externalcount++; }}	%>		
											
							</tbody>					
						</table>						
						<br>	
					</div>
					
					
					<%if(committeeinvitedlist.size()>0){ %>
					
						<div  class="col-md-4">
						
						<h5>External Member (Outside DRDO)</h5>
							<hr>						
						 <table border='0'>

							<tbody>
								<%	 int outsidemember=1;
								for (Object[] obj : committeeinvitedlist ) {
									if(obj[3].toString().equals("CO")){  	%>
									<tr>			
										<td class="tdclass"><%=outsidemember%> )</td> <td> <%=obj[6]!=null?StringEscapeUtils.escapeHtml4(obj[6].toString()): " - "%>, <%=obj[7]!=null?StringEscapeUtils.escapeHtml4(obj[7].toString()): " - "%> (<%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - " %>)</td>
									<%if( obj[9].toString().equalsIgnoreCase("Y")){ %>
										<td class="pl-10px">
									 	<i class="fa fa-check text-success" aria-hidden="true"></i> 
										</td>
									<%} %>
			
									</tr>
								<%	outsidemember++; }}	%>		
											
							</tbody>	
						</table>						
						<br>	
						
					</div>
					<%} %>
										
					<!-- Prudhvi - 27/03/2024 start -->
					<%if(committeeinvitedlist.size()>0){ %>
					
						<div  class="col-md-4">
						
						<h5>Industry Partner</h5>
							<hr>						
						 <table border='0'>

							<tbody>
								<%	 int outsidemember=1;
								for (Object[] obj : committeeinvitedlist ) {
									if(obj[3].toString().equals("CIP")){  	%>
									<tr>			
										<td class="tdclass"><%=outsidemember%> )</td> <td> <%=obj[6]!=null?StringEscapeUtils.escapeHtml4(obj[6].toString()): " - "%>, <%=obj[7]!=null?StringEscapeUtils.escapeHtml4(obj[7].toString()): " - "%> (<%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - " %>)</td>
									<%if( obj[9].toString().equalsIgnoreCase("Y")){ %>
										<td class="pl-10px">
									 	<i class="fa fa-check text-success" aria-hidden="true" ></i> 
										</td>
									<%} %>
			
									</tr>
								<%	outsidemember++; }}	%>		
											
							</tbody>	
						</table>						
						<br>	
						
					</div>
					<%} %>
					<!-- Prudhvi - 27/03/2024 end -->
				</div>





<!-- -------------------------------------------------------------------------------------------------------------------------------------------------------------- -->
					
			
					<div  class="row">
						<div  class="col-md-6">
								<h5>Agenda Presenters</h5>
								<hr class="pb-10px">
					
							<table border='0'>
								<tr>
									<th><label class="control-label">SN &nbsp;</label></th>
									<th><label class="control-label">Agenda Item</label></th><th>&emsp; &emsp;</th>
									<th><label class="control-label">Presenter</label></th>
								</tr>
									<%
										if(agendalist!=null && agendalist.size()>0) {
										int count1 = 1;
										for (Object[] obj : agendalist) {
									%>
								<tr>
									<td>
										<label class="control-label"> <%=count1%> .</label> 
									</td>
									<td>
										<label class="control-label"><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - " %> </label>
									</td>
									<td>
										&emsp; :&emsp;
									</td>
									<td> 
										<%=obj[10]!=null?StringEscapeUtils.escapeHtml4(obj[10].toString()): " - "%>, <%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - "%> (<%=obj[14]!=null?StringEscapeUtils.escapeHtml4(obj[14].toString()): " - "%>)	
									</td>
									
									
										<td class="pl-10px">	
										<% int count=0;
 										for(int i=0;i<committeeinvitedlist.size();i++)
										{ 	 
 											count=0;
 											ArrayList<String> membertypes=new ArrayList<String>(Arrays.asList("CC","CS","PS","CH","CI","I","P"));									
											if( (membertypes.contains(committeeinvitedlist.get(i)[3].toString()) && committeeinvitedlist.get(i)[0].equals(obj[9])))
											{	
												
												if( committeeinvitedlist.get(i)[9].toString().equalsIgnoreCase("Y")){ %>
													
													 	<i class="fa fa-check text-success" aria-hidden="true"></i> 
													
												<%} 
												
											}
											for(String str : InvitedList)
											{
												if(str.equalsIgnoreCase(obj[9]+"_"+obj[14])){
													count++;
													break;
												}
											}
											
										}
										if(count==0){ %>
										
											<form  action="CommitteeInvitationCreate.htm" method="POST" name="myfrm1" id="myfrm1">
												<button type="submit" class="btn" onclick="return confirm('Are you sure To Add this Member to Invitation List?')" data-toggle="tooltip" data-placement="top" title="Member Not Added to Invitation List (Click Here to Add)"> <i class="fa fa-plus-square text-success margin-1px"aria-hidden="true"></i> </button>											
												<input type="hidden" name="internalmember" value="<%=obj[9]%>,P,<%=obj[13]%>">
												<input type="hidden" name="internallabcode" value="<%=obj[14] %>" />	
												<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid%>">																						
												<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
												<%if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {%>
													<input type="hidden" name="ccmFlag" value="<%=ccmFlag %>" />
												<%} %>
											</form>
										
										<%} %>	
										</td>										
									</tr>
								<%	count1++; }	%>
								<% }else if(agendaList!=null && agendaList.size()>0) {
										int count1 = 1;
										for (Object[] obj : agendaList) {
											if(obj[6]!=null && !obj[6].toString().equalsIgnoreCase("0")) {
									%>
								<tr>
									<td>
										<label class="control-label"> <%=count1%> .</label> 
									</td>
									<td>
										<label class="control-label"><%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - " %> </label>
									</td>
									<td>
										&emsp; :&emsp;
									</td>
									<td> 
										<%=obj[9]!=null?StringEscapeUtils.escapeHtml4(obj[9].toString()): " - " %> (<%=obj[5]!=null?StringEscapeUtils.escapeHtml4(obj[5].toString()): " - "%>) 	
									</td>
									
									
										<td class="pl-10px">	
										<% int count=0;
 										for(int i=0;i<committeeinvitedlist.size();i++)
										{ 	 
 											count=0;
 											ArrayList<String> membertypes=new ArrayList<String>(Arrays.asList("CC","CS","PS","CH","CI","I","P"));									
											if( (membertypes.contains(committeeinvitedlist.get(i)[3].toString()) && committeeinvitedlist.get(i)[0].equals(obj[6])))
											{	
												
												if( committeeinvitedlist.get(i)[9].toString().equalsIgnoreCase("Y")){ %>
													
													 	<i class="fa fa-check text-success" aria-hidden="true" ></i> 
													
												<%} 
												
											}
											for(String str : InvitedList)
											{
												if(str.equalsIgnoreCase(obj[6]+"_"+obj[5])){
													count++;
													break;
												}
											}
											
										}
										if(count==0){ %>
										
											<form  action="CommitteeInvitationCreate.htm" method="POST" name="myfrm1" id="myfrm1">
												<button type="submit" class="btn" onclick="return confirm('Are you sure To Add this Member to Invitation List?')" data-toggle="tooltip" data-placement="top" title="Member Not Added to Invitation List (Click Here to Add)"> <i class="fa fa-plus-square text-success margin-1px" aria-hidden="true"></i> </button>											
												<input type="hidden" name="internalmember" value="<%=obj[6]%>,P,<%=obj[10]%>">
												<input type="hidden" name="internallabcode" value="<%=obj[5] %>" />	
												<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid%>">																						
												<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
												<%if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {%>
													<input type="hidden" name="ccmFlag" value="<%=ccmFlag %>" />
												<%} %>
											</form>
										
										<%} %>	
										</td>										
									</tr>
								<%	count1++; } }	}%>
							</table>							
						</div>					
						<div class="col-md-2">	</div>
						<div class="col-md-4">
							<h5>Representatives</h5>
								<hr class="pb-10px">
					
							<table >
							<% int repcount=1;
							// Prudhvi 27/03/2024
							/* ------------------ start ----------------------- */
							ArrayList<String> membertypes=new ArrayList<String>(Arrays.asList("CC","CS","PS","CH","CI","CW","CO","P","I","W","E","CIP","IP","SPL"));
							/* ------------------ end ----------------------- */
							for(int i=0;i<committeeinvitedlist.size();i++)
							{								
								if(!membertypes.contains(committeeinvitedlist.get(i)[3].toString()))
								{
								String repCode = committeeinvitedlist.get(i)[3]!=null ? committeeinvitedlist.get(i)[3].toString() : "";
								boolean isCommitteRep = !repCode.endsWith("_NORMAL");
								
								if(!isCommitteRep){
									String[] reps = repCode.split("_");
									repCode = "REP_"+reps[0];
								}else{
									repCode = "REP_"+repCode;
								}
								%>		
										
										<tr>
										<td><%=repcount%> . <%=committeeinvitedlist.get(i)[6]!=null?StringEscapeUtils.escapeHtml4(committeeinvitedlist.get(i)[6].toString()): " - "%>, <%=committeeinvitedlist.get(i)[7]!=null?StringEscapeUtils.escapeHtml4(committeeinvitedlist.get(i)[7].toString()): " - "%> (<%=committeeinvitedlist.get(i)[11]!=null?StringEscapeUtils.escapeHtml4(committeeinvitedlist.get(i)[11].toString()): " - "%>) (<%= repCode %>)</td> 
										<td class="pl-30px">
										
										<%if(Long.parseLong(committeescheduledata[10].toString())<5 && Long.parseLong(committeescheduledata[10].toString())!=3 && !isCommitteRep ){ %>
											<form action="CommitteeInvitationDelete.htm" method="Post">
												<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
												<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid%>">
												<input type="hidden" name="committeeinvitationid" value="<%=committeeinvitedlist.get(i)[1]%>">
												<button type="submit" class="btn" onclick="return confirm('Are you sure To Remove this Member?')" > <i class="fa fa-trash" aria-hidden="true" ></i> </button>
											</form>		
										<% } %>										
										<td>										
											<form action="MeetingInvitationLetter.htm" method="Post" target="_blank">
												<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
												<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid%>">
												<input type="hidden" name="memberid" value="<%=committeeinvitedlist.get(i)[0]%>">
												<input type="hidden" name="invitationid" value="<%=committeeinvitedlist.get(i)[1]%>">
												<input type="hidden" name="membertype" value="<%=committeeinvitedlist.get(i)[3]%>">
												<button type="submit" class="btn"  > <i class="fa fa-eye" aria-hidden="true" ></i>  </button>
											</form>
										</td>
										<td>
											<form action="MeetingInvitationLetterDownload.htm" method="Post" target="_blank">
												<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
												<input type="hidden" name="invitationid" value="<%=committeeinvitedlist.get(i)[1]%>">
												<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid%>">
												<input type="hidden" name="memberid" value="<%=committeeinvitedlist.get(i)[0]%>">
												<input type="hidden" name="membertype" value="<%=committeeinvitedlist.get(i)[3]%>">
												<button type="submit" class="btn"  > <i class="fa fa-download" aria-hidden="true" ></i>  </button>
											</form>
										</td>
										</tr>				  
								<% repcount++;}
							}%>
							</table>
						
						
						</div>
					</div>	
					
					
					<div  class="row">
								
						<br>
		
						<!-- -----------------------Internal start------------------------------- -->
						
						<div class="col-md-4">
							
							<%	for(int i=0;i<committeeinvitedlist.size();i++)
									{
										if(committeeinvitedlist.get(i)[3].toString().equals("I"))
										{%>	
						
							<br>
								<label class="control-label">Internal</label>
								
							<%break;}} %>	
								
							<table border="0" >
								
								<%int intcount=1; 
									for(int i=0;i<committeeinvitedlist.size();i++)
									{
									
										if(committeeinvitedlist.get(i)[3].toString().equals("I"))
										{%>								
											<tr>

												<td>
													<%=intcount%> . <%=committeeinvitedlist.get(i)[6]!=null?StringEscapeUtils.escapeHtml4(committeeinvitedlist.get(i)[6].toString()): " - "%>, <%=committeeinvitedlist.get(i)[7]!=null?StringEscapeUtils.escapeHtml4(committeeinvitedlist.get(i)[7].toString()): " - "%>
												</td>
												<%if( committeeinvitedlist.get(i)[9].toString().equalsIgnoreCase("Y")){ %>
													<td class="pl-10px">
												 		<i class="fa fa-check text-success" aria-hidden="true"></i> 
													</td>
												<%} %>
												<td class="pl-30px">
												
													<%if(Long.parseLong(committeescheduledata[10].toString())<11 ){%>
														<form action="CommitteeInvitationDelete.htm" method="post">
															<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
															<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid%>">
															<input type="hidden" name="committeeinvitationid" value="<%=committeeinvitedlist.get(i)[1]%>">
															<button type="submit" class="btn" onclick="return confirm('Are you sure To Remove this Member?')" > <i class="fa fa-trash" aria-hidden="true" ></i> </button>
														</form>
													<%} %>
													
												</td>  
											
											</tr>	
														
										<% intcount++;
										}
									}%>						
							</table>
						
						</div>	
						
				<!-- -----------------------Internal end------------------------------- -->
				

				<!-- ----------------------External Within start------------------------------ -->
						
						<div class="col-md-4">
						
						<% 
							for(int i=0;i<committeeinvitedlist.size();i++)
							{
								if(committeeinvitedlist.get(i)[3].toString().equals("W"))
								{%>		
						
						<br>
						<label class="control-label">External (Within DRDO) </label>
						
						<%break;} } %>
						
						<table border="0">
						
						<% int extcountwithin=1; 
							for(int i=0;i<committeeinvitedlist.size();i++)
							{
								
								if(committeeinvitedlist.get(i)[3].toString().equals("W"))
								{%>		
										
										<tr>
										<td><%=extcountwithin%> . <%=committeeinvitedlist.get(i)[6]!=null?StringEscapeUtils.escapeHtml4(committeeinvitedlist.get(i)[6].toString()): " - "%> (<%=committeeinvitedlist.get(i)[7]!=null?StringEscapeUtils.escapeHtml4(committeeinvitedlist.get(i)[7].toString()): " - "%>) (<%=committeeinvitedlist.get(i)[11]!=null?StringEscapeUtils.escapeHtml4(committeeinvitedlist.get(i)[11].toString()): " - "%>)</td> 
										<td class="pl-30px">
										
										<%if(Long.parseLong(committeescheduledata[10].toString())<11 ){ %>
											<form action="CommitteeInvitationDelete.htm" method="Post">
												<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
												<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid%>">
												<input type="hidden" name="committeeinvitationid" value="<%=committeeinvitedlist.get(i)[1]%>">
												<button type="submit" class="btn" onclick="return confirm('Are you sure To Remove this Member?')" > <i class="fa fa-trash" aria-hidden="true" ></i> </button>
											</form>		
										<%} %>										
										<td>										
											<form action="MeetingInvitationLetter.htm" method="Post" target="_blank">
												<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
												<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid%>">
												<input type="hidden" name="memberid" value="<%=committeeinvitedlist.get(i)[0]%>">
												<input type="hidden" name="invitationid" value="<%=committeeinvitedlist.get(i)[1]%>">
												<input type="hidden" name="membertype" value="<%=committeeinvitedlist.get(i)[3]%>">
												<button type="submit" class="btn"  > <i class="fa fa-eye" aria-hidden="true" ></i>  </button>
											</form>
										</td>
										<td>
											<form action="MeetingInvitationLetterDownload.htm" method="Post" target="_blank">
												<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
												<input type="hidden" name="invitationid" value="<%=committeeinvitedlist.get(i)[1]%>">
												<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid%>">
												<input type="hidden" name="memberid" value="<%=committeeinvitedlist.get(i)[0]%>">
												<input type="hidden" name="membertype" value="<%=committeeinvitedlist.get(i)[3]%>">
												<button type="submit" class="btn"  > <i class="fa fa-download" aria-hidden="true" ></i>  </button>
											</form>
										</td>
										</tr>				  
								<% extcountwithin++;}
							}%>
					
						</table>
						
						</div>
					
				<!-- ----------------------External within end------------------------------ -->	
						
				
				<!-- ----------------------External start------------------------------ -->
						
						<div class="col-md-4">
						
						<%for(int i=0;i<committeeinvitedlist.size();i++)
							{
								if(committeeinvitedlist.get(i)[3].toString().equals("E"))
								{%>		
						<br>
						<label class="control-label">External (Outside DRDO) </label>
							
							<%break;} %>
							
						<%} %>
						
						<table border="0">
	
						<% int extcount=1; 
							for(int i=0;i<committeeinvitedlist.size();i++)
							{								
								if(committeeinvitedlist.get(i)[3].toString().equals("E"))
								{%>		
										
										<tr>
										<td><%=extcount%> . <%=committeeinvitedlist.get(i)[6]!=null?StringEscapeUtils.escapeHtml4(committeeinvitedlist.get(i)[6].toString()): " - "%> (<%=committeeinvitedlist.get(i)[7]!=null?StringEscapeUtils.escapeHtml4(committeeinvitedlist.get(i)[7].toString()): " - "%>)(<%=committeeinvitedlist.get(i)[11]!=null?StringEscapeUtils.escapeHtml4(committeeinvitedlist.get(i)[11].toString()): " - "%>)</td> 
										<td class="pl-30px">
										
										<%if(Long.parseLong(committeescheduledata[10].toString())<11 ){ %>
											<form action="CommitteeInvitationDelete.htm" method="Post">
												<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
												<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid%>">
												<input type="hidden" name="committeeinvitationid" value="<%=committeeinvitedlist.get(i)[1]%>">
												<button type="submit" class="btn" onclick="return confirm('Are you sure To Remove this Member?')" > <i class="fa fa-trash" aria-hidden="true" ></i> </button>
											</form>
										<%} %>
											
											
											
										<td>
										
											<form action="MeetingInvitationLetter.htm" method="Post" target="_blank">
												<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
												<input type="hidden" name="invitationid" value="<%=committeeinvitedlist.get(i)[1]%>">
												<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid%>">
												<input type="hidden" name="memberid" value="<%=committeeinvitedlist.get(i)[0]%>">
												<input type="hidden" name="membertype" value="<%=committeeinvitedlist.get(i)[3]%>">
												<button type="submit" class="btn"  > <i class="fa fa-eye" aria-hidden="true" ></i>  </button>
											</form>
										</td>
										<td>
											<form action="MeetingInvitationLetterDownload.htm" method="Post" target="_blank">
												<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
												<input type="hidden" name="invitationid" value="<%=committeeinvitedlist.get(i)[1]%>">
												<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid%>">
												<input type="hidden" name="memberid" value="<%=committeeinvitedlist.get(i)[0]%>">
												<input type="hidden" name="membertype" value="<%=committeeinvitedlist.get(i)[3]%>">
												<button type="submit" class="btn"  > <i class="fa fa-download" aria-hidden="true" ></i>  </button>
											</form>
										</td>
										</tr>				  
								<% extcount++;}
							}%>
					
						</table>
						</div>
				<!-- ----------------------External end------------------------------ -->
				
				<!--  special Member start  -->
				
								<div class="col-md-4">
						
						<%for(int i=0;i<committeeinvitedlist.size();i++)
							{
								if(committeeinvitedlist.get(i)[3].toString().equals("SPL"))
								{%>		
						<br>
						<label class="control-label">Special Members</label>
							
							<%break;} %>
							
						<%} %>
						
						<table border="0">
	
						<% int specialCount=1; 
							for(int i=0;i<committeeinvitedlist.size();i++)
							{								
								if(committeeinvitedlist.get(i)[3].toString().equals("SPL"))
								{%>		
										
										<tr>
										<td><%=extcount%> . <%=committeeinvitedlist.get(i)[6]!=null?StringEscapeUtils.escapeHtml4(committeeinvitedlist.get(i)[6].toString()): " - "%> (<%=committeeinvitedlist.get(i)[7]!=null?StringEscapeUtils.escapeHtml4(committeeinvitedlist.get(i)[7].toString()): " - "%>)(<%=committeeinvitedlist.get(i)[11]!=null?StringEscapeUtils.escapeHtml4(committeeinvitedlist.get(i)[11].toString()): " - "%>)</td> 
										<td class="pl-30px">
										
										<%if(Long.parseLong(committeescheduledata[10].toString())<11 ){ %>
											<form action="CommitteeInvitationDelete.htm" method="Post">
												<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
												<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid%>">
												<input type="hidden" name="committeeinvitationid" value="<%=committeeinvitedlist.get(i)[1]%>">
												<button type="submit" class="btn" onclick="return confirm('Are you sure To Remove this Member?')" > <i class="fa fa-trash" aria-hidden="true" ></i> </button>
											</form>
										<%} %>
											
											
											
										<td>
										
											<form action="MeetingInvitationLetter.htm" method="Post" target="_blank">
												<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
												<input type="hidden" name="invitationid" value="<%=committeeinvitedlist.get(i)[1]%>">
												<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid%>">
												<input type="hidden" name="memberid" value="<%=committeeinvitedlist.get(i)[0]%>">
												<input type="hidden" name="membertype" value="<%=committeeinvitedlist.get(i)[3]%>">
												<button type="submit" class="btn"  > <i class="fa fa-eye" aria-hidden="true" ></i>  </button>
											</form>
										</td>
										<td>
											<form action="MeetingInvitationLetterDownload.htm" method="Post" target="_blank">
												<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
												<input type="hidden" name="invitationid" value="<%=committeeinvitedlist.get(i)[1]%>">
												<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid%>">
												<input type="hidden" name="memberid" value="<%=committeeinvitedlist.get(i)[0]%>">
												<input type="hidden" name="membertype" value="<%=committeeinvitedlist.get(i)[3]%>">
												<button type="submit" class="btn"  > <i class="fa fa-download" aria-hidden="true" ></i>  </button>
											</form>
										</td>
										</tr>				  
								<% specialCount++;}
							}%>
					
						</table>
						</div>
				
				<!-- Special member end -->
								<!-- Prudhvi - 27/03/2024 start-->
				<%if(ccmFlag==null || (ccmFlag!=null && !ccmFlag.equalsIgnoreCase("Y"))) {%>					
				<!-- ---------------------- Industry Partner start------------------------------ -->
						
						<div class="col-md-4">
						
						<% 
						if(committeeinvitedlist!=null && committeeinvitedlist.size()>0){
							for(int i=0;i<committeeinvitedlist.size();i++)
							{
								if(committeeinvitedlist.get(i)[3].toString().equals("IP"))
								{%>		
						
						<br>
						<label class="control-label">Industry Partner </label>
						
						<%break;} } } %>
						
						<table border="0">
						
						<% 
						if(committeeinvitedlist!=null && committeeinvitedlist.size()>0){
							int indpartnercount=1; 
							for(int i=0;i<committeeinvitedlist.size();i++)
							{
								
								if(committeeinvitedlist.get(i)[3].toString().equals("IP"))
								{%>		
										
										<tr>
										<td><%=indpartnercount%> . <%=committeeinvitedlist.get(i)[6]!=null?StringEscapeUtils.escapeHtml4(committeeinvitedlist.get(i)[6].toString()): " - "%> (<%=committeeinvitedlist.get(i)[7]!=null?StringEscapeUtils.escapeHtml4(committeeinvitedlist.get(i)[7].toString()): " - "%>) (<%=committeeinvitedlist.get(i)[11]!=null?StringEscapeUtils.escapeHtml4(committeeinvitedlist.get(i)[11].toString()): " - "%>)</td> 
										<td class="pl-30px">
										
										<%if(Long.parseLong(committeescheduledata[10].toString())<11 ){ %>
											<form action="CommitteeInvitationDelete.htm" method="Post">
												<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
												<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid%>">
												<input type="hidden" name="committeeinvitationid" value="<%=committeeinvitedlist.get(i)[1]%>">
												<button type="submit" class="btn" onclick="return confirm('Are you sure To Remove this Member?')" > <i class="fa fa-trash" aria-hidden="true" ></i> </button>
											</form>		
										<%} %>										
										<td>										
											<form action="MeetingInvitationLetter.htm" method="Post" target="_blank">
												<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
												<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid%>">
												<input type="hidden" name="memberid" value="<%=committeeinvitedlist.get(i)[0]%>">
												<input type="hidden" name="invitationid" value="<%=committeeinvitedlist.get(i)[1]%>">
												<input type="hidden" name="membertype" value="<%=committeeinvitedlist.get(i)[3]%>">
												<button type="submit" class="btn"  > <i class="fa fa-eye" aria-hidden="true" ></i>  </button>
											</form>
										</td>
										<td>
											<form action="MeetingInvitationLetterDownload.htm" method="Post" target="_blank">
												<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
												<input type="hidden" name="invitationid" value="<%=committeeinvitedlist.get(i)[1]%>">
												<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid%>">
												<input type="hidden" name="memberid" value="<%=committeeinvitedlist.get(i)[0]%>">
												<input type="hidden" name="membertype" value="<%=committeeinvitedlist.get(i)[3]%>">
												<button type="submit" class="btn"  > <i class="fa fa-download" aria-hidden="true" ></i>  </button>
											</form>
										</td>
										</tr>				  
								<% indpartnercount++;}
							}}%>
					
						</table>
						
						</div>
					
				<!-- ---------------------- Industry Partner end------------------------------ -->		
				<%} %>
				<!-- Prudhvi - 27/03/2024 end-->
						
					</div>		
					<br>	
					<div  align="center">
	          			<table>
	          				<tr>
	          					<td>
						 		</td>
						 		<td>
						 			<%if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {%>
				          				<form method="post" action="CCMSchedule.htm" id="backfrm1" >
											<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
											<input type="hidden" name="ccmScheduleId" value="<%=committeescheduleid %>">
											<input type="hidden" name="committeeMainId" value="<%=committeemainid %>">
											<input type="hidden" name="committeeId" value="<%=committeeId %>">
											<button class="btn btn-info btn-sm  shadow-nohover back" onclick='$("#backform111").submit()'>Back</button>
										</form> 
	          				
	          						<%} else{%>
						            	<form method="post" action="CommitteeScheduleView.htm" id="backform111">
											<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
											<input type="hidden" name="scheduleid" value="<%=committeescheduleid %>">
											<button class="btn btn-info btn-sm  shadow-nohover back" onclick='$("#backform111").submit()'>Back</button>
										</form>
									<%} %>	
						 		</td>
						 		<td>			 		
						 		
								 	<%if( Integer.parseInt(committeescheduledata[10].toString())<7)
									{ %> 					 	
						            	<form method="post" action="SendInvitationLetter.htm" id="inviteform">
											<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
											<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid %>">
											<%if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {%>
												<input type="hidden" name="ccmFlag" value="<%=ccmFlag %>">
											<%} %>	
               								<button type="submit" onclick="inviteform()" class=" btn btn-sm btn-warning prints" form="inviteform"  ><i class="fa fa-paper-plane-o" aria-hidden="true"></i> SEND INVITATION</button>
										</form>
									<%} %>	  
											
							 		</td>
							 		<td>
								 		<button type="button" class="btn btn-sm add" id="addrep" onclick="showaddladd();">Add Additional Members</button>
								 		<button type="button" class="btn btn-sm add" id="addrep" onclick="showrepadd();">Add Representative</button>
							 		</td>
							 	</tr>
		            	</table>
	          		</div>
	          				
	          				
				<div class=row>
		       		<div class="col-md-12 addmemTitleIdDisplay" id="addmemtitleid">
		       			<h5 class="addh5Color">Add Additional Members</h5>
		       			<hr> 
		          	</div>
		          	<div class="col-md-12 addmemTitleIdDisplay" id="reptitleid">
		       			<h5 class="addh5Color">Add Representative Members</h5>
		       			<hr> 
		          	</div>
		          	
		     	</div>			
		
	<!-- --------------------------------internal add ----------------------------------------------- -->				
			<div  id="additionalmemadd"  > 
				<div class="row" id="repselect"  >						
					<div class="col-md-6">	 
						
						<table class="mt-10px w-100">
							<tr >			
									
								<td class="w-100">	
									<label>Representative Type</label>										
									<select class="form-control selectdee " name="reptype" id="reptype"  data-live-search="true" onchange="setreptype();" >
										<option selected value="0"  > Choose... </option>
										<%if(committeereplist.size()==0){ %> <option disabled> No results found </option> <%} %>
										<% for (Object[] obj : committeereplist) {%>					
											<option value="<%=obj[2]%>"> <%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - "%> </option>
										<%} %>
									</select>
								</td>
							</tr>
						</table>	
					</div>		
				</div>
	
					<form  action="CommitteeInvitationCreate.htm" method="POST" name="myfrm1" id="myfrm1">					
					<div class="row">	
								
						<div class="col-md-6">
							<table class="table  table-bordered table-hover table-striped table-condensed  info shadow-nohover mt-10px w-100" id="">
								<thead>  
									<tr id="" >
										<th> Internal Members</th>
									</tr>
								</thead>				
								<tr class="tr_clone">
									<td >								
										 <div class="input select external">
											 <select class="form-control selectdee" name="internalmember" id="internalmember"  data-live-search="true"   data-placeholder="Select Members" multiple required>
								               
								                 <% for (Object[] obj : EmployeeList) {%>
										       			<option value="<%=obj[0]%>,I,<%=obj[3]%>"><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "%>, <%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %> </option>
										    	<%} %>
											</select>
											<input type="hidden" name="internallabcode" value="<%=labcode %>" />
										</div>
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />	
	 									<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid %>">
	 									<input type="hidden" name="rep" id="rep1" value="0" />
										<%if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {%>
											<input type="hidden" name="ccmFlag" value="<%=ccmFlag %>" />
										<%} %>
									</td>
								</tr>
							</table>
						</div>
						
						<div class="col-md-6 align-self-center">
						
							<button class="btn btn-primary btn-sm add" name="submit" value="submit" type="submit"  onclick="return confirm('Are you Sure to Add these Members ?');">SUBMIT</button>
								
						</div>
					
				</div>
				</form>
	<!-- --------------------------------internal add ----------------------------------------------- -->
	
	<!-- --------------------------------External Members (Within DRDO)----------------------------------------------- -->
					<form  action="CommitteeInvitationCreate.htm" method="POST" name="myfrm1" id="myfrm1">
					<div class="row">	
						
						<div class="col-md-6">
							
							<table class="table  table-bordered table-hover table-striped table-condensed  info shadow-nohover mt-10px" id="table1">
								<thead>  
									<tr id="">
										<th colspan="2"> External Members (Within DRDO)
											<button class="btn bg-primary float-right colorWhite" type="button" id="externalAdd">ADD NEW </button> </th>
									</tr>
								</thead>
								<tr class="tr_clone1">
									<td class="width-30per">							
										<div class="input select">
											<select class="form-control selectdee" name="externallabid" tabindex="-1" id="LabCode" onchange="employeename()" required>
												<option disabled="true"  selected value="">Lab Name</option>
													<% for (Object[] obj : clusterlablist) {
													if(!labcode.equals(obj[3].toString())){%>
														<option value="<%=obj[3]%>"><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - "%></option>
													<%} 
													}%>
											</select>
										</div>
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />	
		 								<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid %>">
		 								<input type="hidden" name="rep" id="rep2" value="0" />
										<%if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {%>
											<input type="hidden" name="ccmFlag" value="<%=ccmFlag %>" />
										<%} %>
									</td>
									<td class="width-70per">
										<div class="input select ">
											<select class="form-control selectdee" name="externalmember" id="ExternalMemberLab" data-live-search="true"   data-placeholder="Select Members" multiple>
											</select>
										</div>
									</td>						
								</tr>
							</table>				
						</div>
						
						<div class="col-md-6 align-self-center">
							
								<button class="btn btn-primary btn-sm add" name="submit" value="submit" type="submit"  onclick="return confirm('Are you Sure to Add these Members ?');">SUBMIT</button>
									
						</div>
						
					</div>
					</form>
	<!-- --------------------------------External Members (Within DRDO)----------------------------------------------- -->
	<!-- --------------------------------External Members (Outside DRDO)----------------------------------------------- -->

					
					<form  action="CommitteeInvitationCreate.htm" method="POST" name="myfrm1" id="myfrm1">
					<div class="row">					
						
						<div class="col-md-6">
							
								<table class="table  table-bordered table-hover table-striped table-condensed  info shadow-nohove mt-10px w-100r" id="">
								<thead>  
									<tr id="">
										<th> External Members (Outside DRDO)
										<button class="btn bg-primary float-right colorWhite" type="button" id="expertAdd">ADD EXPERT </button> </th>
									</tr>
								</thead>
								<tr class="tr_clone2">
									<td >
										<div class="input select external">
											<select  class= "form-control selectdee" name="expertmember" id="expertmemberId"   data-live-search="true"   data-placeholder="Select Members" multiple required>
												<% for (Object[] obj : ExpertList) {%>
											       	<option value="<%=obj[0]%>,E,<%=obj[3]%>"><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "%>, <%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %> </option>
											    <%} %>
											</select>
											<input type="hidden" name="expertlabid" value="@EXP" />
										</div>
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />	
			 							<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid %>">
			 							<input type="hidden" name="rep" id="rep3" value="0" />	
			 							<%if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {%>
											<input type="hidden" name="ccmFlag" value="<%=ccmFlag %>" />
										<%} %>			
			 						</td>
								</tr>
							</table>
							
						</div>
						
						<div class="col-md-6 align-self-center">
								
							<button class="btn btn-primary btn-sm add" name="submit" value="submit" type="submit"  onclick="return confirm('Are you Sure to Add these Members ?');">SUBMIT</button>
										
						</div>
						
					</div>	
					</form>
	<!-- --------------------------------External Members (Outside DRDO)----------------------------------------------- -->
		<!-- Prudhvi - 27/03/2024 start-->
		
	<!-- --------------------------------Industry Partner----------------------------------------------- -->
	<%if(ccmFlag==null || (ccmFlag!=null && !ccmFlag.equalsIgnoreCase("Y"))) {%>	
					<form  action="CommitteeInvitationCreate.htm" method="POST" name="myfrm1" id="myfrm1">
					<div class="row">	
						
						<div class="col-md-6">
							
							<table class="table  table-bordered table-hover table-striped table-condensed  info shadow-nohover mt-10px" id="table1">
								<thead>  
									<tr id="">
										<th colspan="2"> Industry Partner</th>
									</tr>
								</thead>
								<tr class="tr_clone1">
									<td class="width-30per">							
										<div class="input select">
											<select class="form-control selectdee" name="industryPartnerId" tabindex="-1" id="industryPartnerId" onchange="industrypartnerrepname()" required>
												<option disabled="true"  selected value="">Industry Partner</option>
													<% for (IndustryPartner partner : industryPartnerList) {
													%>
														<option value="<%=partner.getIndustryPartnerId()%>"
														data-subtext="(<%=partner.getIndustryCity()+" - "+partner.getIndustryPinCode() %>)"
														><%=partner.getIndustryName()%> (<%=partner.getIndustryCity()!=null?StringEscapeUtils.escapeHtml4(partner.getIndustryCity()): " - "%> <%=" - "+partner.getIndustryPinCode()!=null?StringEscapeUtils.escapeHtml4(partner.getIndustryPinCode()): " - " %>)</option>
														
													<%}%>
													<option value="0">ADD NEW</option>
											</select>
											<input type="hidden" name="industrypartnerlabid" value="@IP" />
										</div>
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />	
		 								<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid %>">
		 								<input type="hidden" name="rep" id="rep4" value="0" />
		 								<%if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {%>
											<input type="hidden" name="ccmFlag" value="<%=ccmFlag %>" />
										<%} %>
									</td>
									<td class="width-70per">
										<div class="input select ">
											<select class="form-control selectdee" name="industryPartnerRep" id="industryPartnerRep" data-live-search="true"   data-placeholder="Select Members" multiple onchange ="addIndusRep()">
											</select>
										</div>
									</td>						
								</tr>
							</table>				
						</div>
						
						<div class="col-md-6 align-self-center">
							
								<button class="btn btn-primary btn-sm add" name="submit" value="submit" type="submit"  onclick="return confirm('Are you Sure to Add these Members ?');">SUBMIT</button>
									
						</div>
						
					</div>
					</form>
					<%} %>
	<!-- --------------------------------Industry Partner----------------------------------------------- -->
	<!-- Prudhvi - 27/03/2024 end-->
			<!--   Special Invitaion  -->
					<form  action="CommitteeInvitationCreate.htm" method="POST" name="myfrm1" id="myfrm1">
					<div class="row">	
						
						<div class="col-md-6">
							
							<table class="table  table-bordered table-hover table-striped table-condensed  info shadow-nohover mt-10px" id="deliverablesTable2">
								<thead>  
									<tr id="">
									  <th colspan="2"> Special Member</th>
									  <th >
									  <button type="button" class=" btn btn_add_deliverables2 "> <i class="btn btn-sm fa fa-plus text-success padding0pxStyle"></i></button>
									  </th>
									</tr>
								</thead>
								<tr class="tr_clone_deliverables2">
									<td class="width-30per">							
										<div class="input select">
											<select class="form-control specialselect specialLabCode w-100" name="specialLabCode" id="sepcialLab_1" tabindex="-1" onchange="specialname(this)" required>
												<option disabled="true"  selected value="">Lab Name</option>
													<% for (Object[] obj : clusterlablist) {%>
												<option value="<%=obj[3]%>"><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - "%></option>
													<%}%>
													<option value="@EXP">Expert</option>
											</select>
										</div>
										
									
									</td>
									<td class="width-70per">
										<div class="input select ">
											<select class="form-control specialselect SpecialMember w-100" name="SpecialMember" id="specialMembers_1" data-live-search="true"  data-placeholder="Select Members">
											</select>
										</div>
									</td>
									<td class="width-5per">
															<button type="button" class="btn btn_rem_deliverables2" > <i class="btn btn-sm fa fa-minus text-danger padding0pxStyle"></i></button>
														</td>						
								</tr>
							</table>				
						</div>
						
						<div class="col-md-6 align-self-center">
							
								<button class="btn btn-primary btn-sm add" name="submit" value="submit" type="submit"  onclick="return confirm('Are you Sure to Add these Members ?');">SUBMIT</button>
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />	
 								<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid %>">
 								<input type="hidden" name="rep" id="rep2" value="0" />
 								<%if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {%>
									<input type="hidden" name="ccmFlag" value="<%=ccmFlag %>" />
								<%} %>	
						</div>
						
					</div>
					</form>
			
			<!--  -->
			
				</div>	
			</div>		
					
				

				</div><!-- card-body end -->
	
			</div>
		
		</div>	
	</div>

<!-- externalAdd -->
		<div class="modal fade bd-example-modal-lg" id="externalAddModal"
			tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel"
			aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content modalContentWidth">
					<div class="modal-header">
						<h5 class="modal-title" id="exampleModalLabel">External Add
							Member</h5>
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
					</div>
					<div class="modal-body">

						<div class="row">

							<div class="col-md-4">
								
									<label>Lab Name:<span class="mandatory text-danger">*</span></label>
										
									
								<div class="form-group">
										
										<select
										class="form-control selectdee fs-5px" id="labModal" name="labModal"
										data-container="body" 
										required="required">
										<option value="" disabled="disabled" selected="selected"
											hidden="true">--Select--</option>
										<%
										for (Object[] obj : clusterlablist) {
										    if(!labcode.equals(obj[3].toString())){%>
										    <option value="<%=obj[3]%>"><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - "%></option>
										    <%}
										    }%>
									</select>
								</div>
								</div>
							


							<div class="col-md-4">
								<div class="form-group">
									<label>Rank/Salutation</label><br> <select
										class="form-control selectdee fs-5px" id="title" name="title"
										data-container="body" data-live-search="true"
										>
										<option value="" selected="selected" hidden="true">--Select--</option>
										<option value="Prof.">Prof.</option>
										<option value="Lt.">Lt.</option>
										<option value="Dr.">Dr.</option>

									</select>
								</div>
							</div>
							<div class="col-md-4">
								<div class="form-group">
									<label>Title</label><br> <select
										class="form-control selectdee fs-5px" id="salutation"
										name="salutation" data-container="body"
										data-live-search="true">
										<option value="" selected="selected" hidden="true">--Select--</option>
										<option value="Mr.">Mr.</option>
										<option value="Ms.">Ms.</option>
									</select>
								</div>
							</div>
						
						</div>

						<div class="row">
							<div class="col-md-4">
								<div class="form-group">
									<label>Employee No:<span class="mandatory text-danger"
										>*</span></label> <input
										class="form-control form-control fs-15px w-100 textTransform" type="text" id="EmpNo"
										name="EmpNo" required="required" maxlength="255"
										>
								</div>
							</div>
							<div class="col-md-4">
								<div class="form-group">
									<label>Employee Name:<span class="mandatory text-danger"
										>*</span></label> <input
										class="form-control form-control fs-15px w-100 textTransform" type="text" id="EmpName"
										name="EmpName" required="required" maxlength="255"
										>
								</div>
							</div>

							<div class="col-md-4">
								<div class="form-group">
									<label>Designation:<span class="mandatory text-danger"
										>*</span></label> <select
										class="form-control selectdee fs-5px" id="Designation"
										name="Designation" data-container="body"
										data-live-search="true" required="required"
										>
										<option value="" disabled="disabled" selected="selected"
											hidden="true">--Select--</option>
										 <%  for ( Object[]  obj :DesignationList) {%>
										<option value="<%=obj[0] %>">
											<%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></option>
										<%} %>
									</select>
								</div>
							</div>

						</div>
						
						<div class="mt-2" align="center">
						<button class="btn submit" onclick="empNoCheck()">SUBMIT</button>
						</div>

					</div>

				</div>
			</div>
		</div>
		
		<!--  Expert Addd -->
		
		<div class="modal fade bd-example-modal-lg" id="expertAddModal"
			tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel"
			aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content modalContentWidth">
					<div class="modal-header">
						<h5 class="modal-title" id="exampleModalLabel"> Add  Expert
							Member Details</h5>
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
					</div>
					<div class="modal-body">

						<div class="row">
	<div class="col-md-4">
								<div class="form-group">
									<label>Expert Name:<span class="mandatory text-danger"
										>*</span></label> <input
										class="form-control form-control fs-15px w-100 textTransform" type="text" id="ExtEmpName"
										name="EmpName" required="required" maxlength="255"
										>
								</div>
							</div>
							
							


							<div class="col-md-4">
								<div class="form-group">
									<label>Rank/Salutation</label><br> <select
										class="form-control selectdee fs-5px" id="Exttitle" name="title"
										data-container="body" data-live-search="true"
										>
										<option value="" selected="selected" hidden="true">--Select--</option>
										<option value="Prof.">Prof.</option>
										<option value="Lt.">Lt.</option>
										<option value="Dr.">Dr.</option>

									</select>
								</div>
							</div>
							<div class="col-md-4">
								<div class="form-group">
									<label>Title</label><br> <select
										class="form-control selectdee fs-5px" id="Extsalutation"
										name="salutation" data-container="body"
										data-live-search="true" >
										<option value="" selected="selected" hidden="true">--Select--</option>
										<option value="Mr.">Mr.</option>
										<option value="Ms.">Ms.</option>
									</select>
								</div>
							</div>
						
						</div>

						<div class="row">
							
						

							<div class="col-md-4">
								<div class="form-group">
									<label>Designation:<span class="mandatory text-danger"
										>*</span></label> <select
										class="form-control selectdee fs-5px" id="ExtDesignation"
										name="Designation" data-container="body"
										data-live-search="true" required="required"
										>
										<option value="" disabled="disabled" selected="selected"
											hidden="true">--Select--</option>
										 <%  for ( Object[]  obj :DesignationList) {%>
										<option value="<%=obj[0] %>">
											<%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></option>
										<%} %>
									</select>
								</div>
							</div>
							
							<div class="col-md-4">
									<div class="form-group">
										<label class="control-label">Organization</label><span class="mandatory text-danger">*</span>
										<input class="form-control" type="text" name="organization" id="organization" placeholder="Max 255 Characters" maxlength="255">
									</div>
								</div>

						</div>
						
						<div class="mt-2" align="center">
						<button class="btn submit" onclick="formCheck()">SUBMIT</button>
						</div>

					</div>

				</div>
			</div>
		</div>
	
	
	
	<!-- Industry partnerAdd  -->
		<div class="modal fade bd-example-modal-lg" id="industryPartnerModal"
			tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel"
			aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content modalContentWidth">
					<div class="modal-header">
						<h5 class="modal-title" id="exampleModalLabel"> Add  Industry 
							Partner Details</h5>
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
					</div>
					<div class="modal-body">

		

						<div class="row">						
							<div class="col-md-4">
								<div class="form-group">
									<label class="control-label">Industry Name</label><span class="mandatory text-danger">*</span>
									<input class="form-control" type="text" id="industryPartnerName2" name="industryPartnerName2" maxlength="255" placeholder="Enter Industry Partner" required="">		
								</div>
							</div>
							<div class="col-md-3">
								<div class="form-group">
									<label class="control-label"> Address</label><span class="mandatory text-danger">*</span>
									<input class="form-control" type="text" id="industryPartnerAddress2" name="industryPartnerAddress2" maxlength="1000" placeholder="Enter Street, village/ town" required="">	
								</div>
							</div>
							<div class="col-md-3">
								<div class="form-group">
									<label class="control-label"> City</label><span class="mandatory text-danger">*</span>
									<input class="form-control" type="text" id="industryPartnerCity2" name="industryPartnerCity2" maxlength="500" placeholder="Enter City" required="">	
								</div>
							</div>
							<div class="col-md-2">
								<div class="form-group">
									<label class="control-label"> Pin Code</label><span class="mandatory text-danger">*</span>
									<input class="form-control" type="text" id="industryPartnerPinCode2" name="industryPartnerPinCode2" maxlength="6" placeholder="Enter Pincode" oninput="this.value = this.value.replace(/[^0-9.]/g, '').replace(/(\..*?)\..*/g, '$1');" required="">	
								</div>
							
						</div>
						

					

						</div>
				
						
						
						
						<div class="mt-2" align="center">
						<button class="btn submit" onclick="industryPartenerAdd()">SUBMIT</button>
						</div>

					</div>

				</div>
			</div>
		</div>
	
		<div class="modal fade bd-example-modal-lg" id="industryPartnerEmpModal"
			tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel"
			aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content modalContentWidth">
					<div class="modal-header">
						<h5 class="modal-title" id="exampleModalLabel"> Add  Industry 
							Partner Employee Details</h5>
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
					</div>
					<div class="modal-body">

		

				
			
					<div class="row ml-1 mr-1" >
						<table class="w-100" id="repdetails">
										<thead class="text-center theadBgColor">
											<tr>
										    	<th class="thPaddingStyle5px">Name</th>
										    	<th class="thPaddingStyle5px">Designation</th>
										    	<th class="thPaddingStyle5px">Mobile No</th>
										    	<th class="thPaddingStyle5px">Email</th>
												
											</tr>
										</thead>
								 		<tbody>
									 		<tr class="tr_clone_repdetails">
												<td class="thClonePaddingStyle10px">
													<input class="form-control" type="text" id="repName" name="repName" maxlength="255" placeholder="Enter Rep Name" required="">
												</td>	
												<td class="thClonePaddingStyle10px">
													<input class="form-control" type="text" id="repDesignation" name="repDesignation" placeholder="Enter Rep Designation" maxlength="255" required="">
												</td>	
												<td class="thClonePaddingStyle10px">
													<input class="form-control" type="text" id="repMobileNo" maxlength="10" name="repMobileNo" placeholder="Enter Rep Mobile No" required="" oninput="this.value = this.value.replace(/[^0-9.]/g, '').replace(/(\..*?)\..*/g, '$1');">
												</td>
												<td class="thClonePaddingStyle10px">
													<input class="form-control" type="email" id="repEmail" name="repEmail" maxlength="255" placeholder="Enter Rep Email" required="">
												</td>
																					
											</tr>
										</tbody>
									</table>
						</div> 
						
						
						
						<div class="mt-2" align="center">
						<button class="btn submit" onclick="industryPartenerEmpAdd()">SUBMIT</button>
						</div>

					</div>

				</div>
			</div>
		</div>




	</div>
	
<script type="text/javascript">

	function FormNameEdit(id){
	
   		 $.ajax({

				type : "GET",
				url : "AttendanceToggle.htm",
				data : {
							invitationid : id
					   },
				datatype : 'json',
				success : function(result) {

				var result = JSON.parse(result);
		
				var values = Object.keys(result).map(function(e) {
			 				 return result[e]
			  
								});
					}
					   
				});

}
 
</script>


<script>

function submitforms(){
	
	var ret=onclick="return confirm('Are You Sure To Invite These Members ?');"
	if(ret){
		$("#myfrm1").submit();
		$("#myfrm2").submit();
	}else{
		event.preventDefault();
	}
	
}

function employeename(){

	$('#ExternalMemberLab').val("");
	
		var $LabCode = $('#LabCode').val();
	
		
				if($LabCode!=""){
		
							$
								.ajax({

								type : "GET",
								url : "ExternalEmployeeListInvitations.htm",
								data : {
											LabCode : $LabCode,
											scheduleid : '<%=committeescheduleid %>' 	
									   },
								datatype : 'json',
								success : function(result) {

								var result = JSON.parse(result);
						
								var values = Object.keys(result).map(function(e) {
							 				 return result[e]
							  
												});
						
						var s = '';
						s += '<option value="">'
							+"--Select--"+ '</option>';
						 for (i = 0; i < values.length; i++) {
							
							s += '<option value="'+values[i][0]+",W,"+values[i][4]+'">'
									+values[i][1].replaceAll("<","").replaceAll(">","") + ", " +values[i][3].replaceAll("<","").replaceAll(">","") 
									+ '</option>';
						} 
						 
						$('#ExternalMemberLab').html(s);
						
				
						
					}
				});

}
	}


function specialname(ele){

	
		var LabCode = $(ele).val();
		var id = ele.id.split("_")[1];
		console.log(id)
		
 	
				if(LabCode!=""){
					$('#specialMembers_'+id).html("");
							$
								.ajax({

								type : "GET",
								url : "SpecialEmployeeListInvitations.htm",
								data : {
											LabCode : LabCode,
											scheduleid : '<%=committeescheduleid %>' 	
									   },
								datatype : 'json',
								success : function(result) {

								var result = JSON.parse(result);
						
								var values = Object.keys(result).map(function(e) {
							 				 return result[e]
							  
												});
						
						var s = '';
						s += '<option value="">'
							+"--Select--"+ '</option>';
						 for (i = 0; i < values.length; i++) {
							
							s += '<option value="'+values[i][0]+",SPL,"+values[i][4]+'">'
									+values[i][1].replaceAll("<","").replaceAll(">","") + ", " +values[i][3].replaceAll("<","").replaceAll(">","") 
									+ '</option>';
						} 
						 
						$('#specialMembers_'+id).html(s);
						
				
						
					}
				});

} 
	}
	
$('.specialselect').select2();
var specialcount = 1;

$("#deliverablesTable2").on('click','.btn_add_deliverables2' ,function() {
		
	$('.specialselect').select2("destroy");
	
	var $tr = $('.tr_clone_deliverables2').last();
	var $clone = $tr.clone();
	$tr.after($clone);
	var cl=$('.tr_clone_deliverables2').length;
	
	++specialcount;
	
	$clone.find(".specialselect.specialLabCode").attr("id", 'sepcialLab_' + specialcount);
	$clone.find(".specialselect.SpecialMember").attr("id", 'specialMembers_' + specialcount);
	
	$('.specialselect').select2();
    $clone.find('.specialselect').select2('val', '');
    
	/* var newId="";
	$clone.find('select').each(function() {
		var $select = $(this);
		var oldId = $select.attr('id');
		var newId=oldId.split("_")[0]+"_"+cl
		$select.attr('id', newId);
	
		
	}); */
		
	
});
/* Cloning (Removing) the table body rows for Deliverables2 */
	$("#deliverablesTable2").on('click','.btn_rem_deliverables2' ,function() {
		
	var cl=$('.tr_clone_deliverables2').length;

	if(cl>1){
	
	   var $tr = $(this).closest('.tr_clone_deliverables2');
	  
	   var $clone = $tr.remove();
	   $tr.after($clone);
	   
	}
	   
	}); 
 
</script>
 <script type="text/javascript">
 
 		$(document).ready(function(){
 		
 			$('#additionalmemadd').hide();
 			
 		})
 		
 
 
 
					function showrepadd()
					{
						document.getElementById('addmemtitleid').style.display = 'none';
						document.getElementById('reptitleid').style.display = 'block';	
						

						$('#repselect').show();
						$('#additionalmemadd').show();
						
						$('html, body').animate({
					        scrollTop: $("#additionalmemadd").offset().top
					    }, 100);
					}
					
					function showaddladd()
					{
						document.getElementById('reptitleid').style.display = 'none';
						document.getElementById('addmemtitleid').style.display = 'block';
						
					 	

						$('#repselect').hide();
						$('#additionalmemadd').show();
						
						$("#reptype").val("0").change();
						
					
						
						$('html, body').animate({
					        scrollTop: $("#additionalmemadd").offset().top
					    }, 100);
						 
					}
					
		function setreptype()
		{
			reptype=$('#reptype').val();
			$('#rep1').val(reptype);
			$('#rep2').val(reptype);
			$('#rep3').val(reptype);
		}
			
		$(function () {
			  $('[data-toggle="tooltip"]').tooltip()
			})
</script>
<!-- Prudhvi 27/03/2024 start -->
<script type="text/javascript">
function addIndustryPartener(){
	$('#industryPartnerModal').modal('show');
}

function industrypartnerrepname(){

	$('#industryPartnerRep').val("");
	
		var $industryPartnerId = $('#industryPartnerId').val();
		if($industryPartnerId==="0"){
			addIndustryPartener();
			$('#industryPartnerRep').html('')
			return ;
		}
		
				if($industryPartnerId!=""){
		
							$
								.ajax({

								type : "GET",
								url : "IndustryPartnerRepListInvitations.htm",
								data : {
									industryPartnerId : $industryPartnerId,
											scheduleid : '<%=committeescheduleid %>' 	
									   },
								datatype : 'json',
								success : function(result) {

								var result = JSON.parse(result);
						
								var values = Object.keys(result).map(function(e) {
							 				 return result[e]
							  
												});
						
						var s = '';
						s += '<option value="">'
							+"--Select--"+ '</option>';
						 for (i = 0; i < values.length; i++) {
							
							s += '<option value="'+values[i][0]+",IP,"+values[i][4]+'">'
									+values[i][1].replaceAll("<","").replaceAll(">","") + ", " +values[i][3].replaceAll("<","").replaceAll(">","")
									+ '</option>';
						} 
						 s=s+'<option value="0">ADD NEW </option>'
						$('#industryPartnerRep').html(s);
						
				
						
					}
				});

}
	}
	
	
$("#externalAdd").click(function(){
	$('#externalAddModal').modal('show');
	});
	
	
	
	
function empNoCheck(frmid)
{
	var ExternalMember = $('#ExternalMemberLab').val();
	
	var labId=$('#labModal').val();
	
	console.log("labId ----"+labId);
	var EmpName=$('#EmpName').val().trim();
	var Designation=$('#Designation').val();

	var title=$('#title').val();
	var salutation=$('#salutation').val();
	var $empno=$('#EmpNo').val().trim();
	
	if(labId=== "" || $empno==="" ||EmpName==="" ||Designation===""  ) /* ExtNo===null || DronaEmail==="" || InternetEmail==="" || */ 
	{
		alert('Please Fill All Mandatory Fields.');
		
	}
	 else if((title==="" && salutation==="")||(title!=="" && salutation!=="")){
		alert('Please select either Title or Rank');
	} 
	else
	{
			$.ajax({
				
				type : "GET",
				url : "ExpEmpNoCheck.htm",
				data : {
					
					empno : $empno
					
				},
				datatype : 'json',
				success : function(result) {
					console.log(result);
					var count=0;
					
					if(Number(result) >= 1 ){
						
						alert('Employee No Already Exists');
						count++;
						return false;
					}
					if(count==0)
					{
						if(confirm('Are you Sure To Save ?'))
						{
							
							$.ajax({
								
								type:'get',
								url:'OfficerExternalAjaxAdd.htm',
								datatype:'json',
								data:{
									labId:labId,
									EmpName:EmpName,
									Designation:Designation,
									title:title,
									salutation:salutation,
									EmpNo:$empno
								},
								success:function(result){
									var ajaxresult = JSON.parse(result);
									
									if(Number(ajaxresult)>0){
										alert("Employee added successfully!")
										$('#labModal').val('');
										$('#EmpName').val('');
										$('#Designation').val('');
										$('#EmpNo').val('');
										$('#LabCode').val(labId).trigger('change');
										console.log(ExternalMember)
										$('#externalAddModal').modal('hide');
										ExternalMember.push(ajaxresult+"");
										console.log(ExternalMember)
										$('#ExternalMemberLab').val(ExternalMember).trigger('change');
									}else{
										alert("Something went wrong . Please add it from Master")
									}
								}
								
							})
							
						}
						else
						{
							return false;
						}
					}
				}
			});
		}
}



$("#expertAdd").click(function(){
	$('#expertAddModal').modal('show');
	});
	
	
function formCheck(frmid)
{
	var ExpertMemberIdshtm = $('#expertmemberId').html();
	
	var value = $('#expertmemberId').val();
	
	console.log(ExpertMemberIdshtm)
	console.log(value)
	

	var title=$('#Exttitle').val();
	var salutation=$('#Extsalutation').val();
	var expertname=$('#ExtEmpName').val();
	var selectDesig=$('#ExtDesignation').val();
	
	var organization=$('#organization').val();
	
	//console.log(title+salutation+expertname+selectDesig+mobile+email+organization);
	if(expertname===""||selectDesig===""||organization===""||selectDesig===null){
		alert('Please Fill All the Fields ');
	}
	
	else if((title==="" && salutation==="")||(title!=="" && salutation!=="")){
		window.alert('please select either Title or Rank');
		event.preventDefault();
		return false;
	}
 	else{
		if(window.confirm('Are you sure to save?')){
			
			$.ajax({
				type:'GET',
				url:'ExperAddAjax.htm',
				datatype:'json',
				data:{
					
					title:title,
					salutation:salutation,
					expertname:expertname,
					designationId:selectDesig,
					organization:organization,
				},
				success:function (result){
					var ajaxresult = JSON.parse(result);
					console.log(ajaxresult)
					var html="";
					for(var i=0;i<ajaxresult.length;i++){
						html = html+"<option value='"+ajaxresult[i][0]+"'> "+  ajaxresult[i][2].replaceAll("<","").replaceAll(">","")+ ", "+ajaxresult[i][3].replaceAll("<","").replaceAll(">","")+"</option>"
						value.push(ajaxresult[i][0]);
					}
					ExpertMemberIdshtm =ExpertMemberIdshtm+html
					$('#expertmemberId').html(ExpertMemberIdshtm)
					$('#expertmemberId').val(value).trigger('change');
					
					if(ajaxresult.length>0){
						$('#Exttitle').val('');
						$('#Extsalutation').val('');
						$('#ExtEmpName').val('');
						$('#ExtDesignation').val('');
						$('#organization').val('');
						$('#expertAddModal').modal('hide');
					}
				},
				error : function(error) {
					alert('Html Tags Not Allowed');
				}
			})
			
		}
		else{
			event.preventDefault();
			return false;
		}
	}
	
}



function industryPartenerAdd(){
	
	var industryPartnerId = $('#industryPartnerId').html();
	

	
	var industryPartnerName2 = 	  $('#industryPartnerName2').val().trim();
	
	var industryPartnerAddress2 = $('#industryPartnerAddress2').val().trim();
	
	var industryPartnerCity2 =    $('#industryPartnerCity2').val().trim();
	var industryPartnerPinCode2 = $('#industryPartnerPinCode2').val().trim();
	
	if(industryPartnerPinCode2===''||industryPartnerCity2===''||industryPartnerAddress2===''||industryPartnerName2===''){
		alert("Please fill all the fields")
		event.preventDefault();
		return false;
	}
	
	if(confirm('Are you sure to submit?')){
		
		$.ajax({
			type:'GET',
			url:'industryPartnerAdd.htm',
			datatype:'json',
			data:{
				industryPartnerName2:industryPartnerName2,
				industryPartnerAddress2:industryPartnerAddress2,
				industryPartnerCity2:industryPartnerCity2,
				industryPartnerPinCode2:industryPartnerPinCode2,
			},
			success: function(result) {
			    var ajaxresult = JSON.parse(result);

			    if (ajaxresult.IndustryPartnerId != null) {
			        alert('Industry Partner Added!');

			        // Create new option element
			        var newOption = $("<option>", {
			            value: ajaxresult.IndustryPartnerId,
			            text: ajaxresult.IndustryName.replaceAll("<","").replaceAll(">","")+"( " + ajaxresult.IndustryCity.replaceAll("<","").replaceAll(">","")  +" )"
			        });

			        // Remove ADD NEW temporarily
			        var $addNew = $('#industryPartnerId option[value="0"]').detach();

			        // Add new option
			        $('#industryPartnerId').append(newOption);

			        // Append ADD NEW back to end
			        $('#industryPartnerId').append($addNew);

			        // Set selected value to new partner
			        $('#industryPartnerId').val(ajaxresult.IndustryPartnerId);

			        // If using Select2, trigger update
			        $('#industryPartnerId').trigger('change');

			        // Clear modal inputs
			        $('#industryPartnerName2').val('');
			        $('#industryPartnerAddress2').val('');
			        $('#industryPartnerCity2').val('');
			        $('#industryPartnerPinCode2').val('');

			        // Hide modal
			        $('#industryPartnerModal').modal('hide');
			    }
			}
		})
		
	}else{
		event.preventDefault();
		return false;
	}
	
	

	
}


function addIndusRep(){
	var value = $('#industryPartnerRep').val();
	
	console.log(value)
	if(value.includes('0')){
		$('#industryPartnerEmpModal').modal('show');
	}
}


function industryPartenerEmpAdd(){
	
	var repName = $("#repName").val().trim();
	var repDesignation = $('#repDesignation').val().trim();
	var repEmail = $('#repEmail').val().trim();
	var repMobileNo = $('#repMobileNo').val().trim();
	
	var industryPartnerId = $('#industryPartnerId').val();
	
	if(repEmail==='' ||repMobileNo===''|| repDesignation==='' ||repName===''){
		alert("Please fill all the fields" +industryPartnerId)
		 event.preventDefault();
		 return false;
	}
	
	if(confirm('Are you sure to submit?')){
		$.ajax({
			type:'GET',
			url:'industryPartnerEmpAdd.htm',
			data:{
				repName:repName,
				repDesignation:repDesignation,
				repEmail:repEmail,
				repMobileNo:repMobileNo,
				industryPartnerId:industryPartnerId,
			},
			success:function (result){
				var ajaxresult = JSON.parse(result);
				if(Number(ajaxresult)>0){
					alert('Industry Partener added successfully !');
					$("#repName,#repDesignation,#repEmail,#repMobileNo").val('');
					$('#industryPartnerEmpModal').modal('hide');
					$('#industryPartnerId').val(industryPartnerId).trigger('change');
				}
			}
		})
		
	}else{
		 event.preventDefault();
		 return false;
	}
	
	
	
}
</script>
<!-- Prudhvi 27/03/2024 end -->
</body>
</html>