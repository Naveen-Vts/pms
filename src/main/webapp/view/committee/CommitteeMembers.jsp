<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.vts.pfms.cars.model.CARSInitiation"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="com.vts.pfms.master.model.IndustryPartner"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>

<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/committeeModule/committeeMembers.css" var="committeeMembers" />
<link href="${committeeMembers}" rel="stylesheet" />
<title> COMMITTEE MEMBERS </title>
</head>

<body>

<%
Object[] projectdata=(Object[])request.getAttribute("projectdata"); 
Object[] committeedata=(Object[])request.getAttribute("committeedata");
Object[] divisiondata=(Object[])request.getAttribute("divisiondata"); 
CARSInitiation carsInitiationData=(CARSInitiation)request.getAttribute("carsInitiationData"); 
Object[] proposedcommitteemainid=(Object[])request.getAttribute("proposedcommitteemainid"); 
List<Object[]> EmployeeList=(List<Object[]>)request.getAttribute("employeelist");
List<Object[]> EmployeeList1=(List<Object[]>)request.getAttribute("employeelist1");

List<Object[]> clusterlist=(List<Object[]>) request.getAttribute("clusterlist");
List<Object[]> AllLabList=(List<Object[]>) request.getAttribute("AllLabList");
List<Object[]> expertlist=(List<Object[]>) request.getAttribute("expertlist");

Object[] initiationdata=(Object[])request.getAttribute("initiationdata");
Object[] approvaldata=(Object[])request.getAttribute("committeeapprovaldata");
List<Object[]> DesignationList=(List<Object[]>)request.getAttribute("designationlist");

List<Object[]> committeemembersall=(List<Object[]>)request.getAttribute("committeemembersall");
Object[] chairperson=null;
Object[] co_chairperson=null;
Object[] secretary =null;
Object[] proxysecretary=null;

String initiationid=committeedata[4].toString();
String divisionid=committeedata[3].toString();
String projectid=committeedata[2].toString();
String committeeid=committeedata[1].toString();
String committeemainid=committeedata[0].toString();
String carsInitiationId=committeedata[13].toString();
String status=committeedata[9].toString();
String proposedmainid=null;
if(proposedcommitteemainid!=null)
{
	proposedmainid=proposedcommitteemainid[0].toString();
}
List<Object[]> committeerepnotaddedlist=(List<Object[]>)request.getAttribute("committeerepnotaddedlist");
List<Object[]> committeeMemberreplist=(List<Object[]>)request.getAttribute("committeeMemberreplist");

String LabCode = (String)request.getAttribute("LabCode");

String LoginType =(String) session.getAttribute("LoginType");

Object[]CommitteMainEnoteList = (Object[])request.getAttribute("CommitteMainEnoteList");
%>



<%
for(int i=0;i<committeemembersall.size();i++)
{	
	if(committeemembersall.get(i)[8].toString().equalsIgnoreCase("CC")){
		chairperson = committeemembersall.get(i);
		committeemembersall.remove(i);
		break;
	}
}
for(int i=0;i<committeemembersall.size();i++)
{
	if(committeemembersall.size()>0 && committeemembersall.get(i)[8].toString().equalsIgnoreCase("CS")){
		secretary = committeemembersall.get(i);
		committeemembersall.remove(i);
		break;
	}
}
for(int i=0;i<committeemembersall.size();i++)
{
	if(committeemembersall.size()>0 && committeemembersall.get(i)[8].toString().equalsIgnoreCase("PS")){
		proxysecretary = committeemembersall.get(i);
		committeemembersall.remove(i);
		break;
	}
}
for(int i=0;i<committeemembersall.size();i++)
{
	if(committeemembersall.size()>0 && committeemembersall.get(i)[8].toString().equalsIgnoreCase("CH")){
		co_chairperson = committeemembersall.get(i);
		committeemembersall.remove(i);
		break;
	}
}
String CpLabCode = chairperson[9].toString();


//Prudhvi - 27/03/2024 
/* --------- start -------------- */
List<IndustryPartner> industryPartnerList = (List<IndustryPartner>)request.getAttribute("industryPartnerList");
/* --------- end -------------- */
List<Object[]> constitutionapprovalflow=(List<Object[]>)request.getAttribute("constitutionapprovalflow");
List<String>loginTypes = Arrays.asList("A","P");

String logintype = (String)session.getAttribute("LoginType");
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

    <br />
   
<div class="container-fluid mt-n2">
	<div class="row">
		<div class="col-md-12">	
		   
			<div class="card shadow-nohover">	
						
					<div class="card-header">						
						<div class="row">										
							<div class="col-md-12"><h3 class="headerName"><%=committeedata[8]!=null?StringEscapeUtils.escapeHtml4(committeedata[8].toString()): " - " %>
							
								<p class="float-right">
									
									<%if(Long.parseLong(projectid)>0){ %> 
										Project: <%=projectdata[4]!=null?StringEscapeUtils.escapeHtml4(projectdata[4].toString()): " - "  %>
									<%}else if (Long.parseLong(divisionid)>0){ %>  
										Division: <%=divisiondata[1]!=null?StringEscapeUtils.escapeHtml4(divisiondata[1].toString()): " - "  %> 
									<%}else if(Long.parseLong(initiationid)>0){ %>
										Pre-Project: <%=initiationdata[1]!=null?StringEscapeUtils.escapeHtml4(initiationdata[1].toString()): " - " %> 
									<%}else if(Long.parseLong(initiationid)>0){ %>
										Pre-Project: <%=initiationdata[1]!=null?StringEscapeUtils.escapeHtml4(initiationdata[1].toString()): " - " %> 
									<%}else if(Long.parseLong(carsInitiationId)>0){ %>
										CARS No: <%=carsInitiationData.getCARSNo()!=null?StringEscapeUtils.escapeHtml4(carsInitiationData.getCARSNo()): " - "  %> 
									<%}else{ %>Non-Project<%} %>
									
								</p>
							</h3>
							</div>	
																	
						
						</div>
					</div>
					
<!-- ---------------------------------------------------------------------committee main members ---------------------------------------------- -->
					<div class="card-body">	
						  <form action="CommitteeMainEditSubmit.htm" method="post" id="committeeeditfrm">				
							 <div class="row">							
								<div class="col-md-8 mt-p5">									 
					                    	<label class="control-label chairPerson">Chairperson<span class="mandatory text-danger">*</span></label>
					                    	<table class="w-100">
					                        <tr >
												<td class="w-25 border-0">
													 <div class="input select" id="cplab-col">
														<select class="form-control selectdee labNameWidth" name="CpLabCode" tabindex="-1" required="required" id="CpLabCode" onchange="chairpersonfetch('1')">
															<option disabled="disabled"  selected value="">Lab Name</option>
														    <% for (Object[] obj : AllLabList) {%>
															    <option <%if(chairperson[9].toString().equals(obj[3].toString())){ %>selected <%} %>value="<%=obj[3]%>"><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - "%></option>
														    <%} %>
														    <option <%if(chairperson[9].toString().equalsIgnoreCase("@EXP")){ %>selected <%} %>value="@EXP">Expert</option>
														</select>
																
													</div>
												</td>										
												<td class="border-0">
												<div class="input select">
														<select class="form-control selectdee" name="chairperson" id="chairperson" data-live-search="true" required="required"   data-placeholder="Select Chairperson" >
												             
														</select>	
														<input type="hidden" name="cpmemberid" value="<%=chairperson[0]%>"> 										
												</div>														
											</td>						
										</tr>
										</table>
								</div>
							</div> 
							<div class="row">
			
											<div class="col-md-8 mt-p5">									 
					                    	<label class="control-label chairPerson">Member Secretary<span class="mandatory text-danger">*</span></label>
					                    	<table class="w-100">
					                        <tr >
												<td class="w-25 border-0">
													 <div class="input select" id="cplab-col">
														<select class="form-control selectdee labNameWidth" name="msLabCode" tabindex="-1" required="required" id="mSLabCode" onchange="msfetch('1')">
															<option disabled="disabled"  selected value="">Lab Name</option>
														    <% for (Object[] obj : AllLabList) {%>
															    <option <%if(secretary!=null&& secretary[9].toString().equals(obj[3].toString())){ %>selected <%} %>value="<%=obj[3]%>"><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - "%></option>
														    <%} %>
														    <option <%if(secretary!=null && secretary[9].toString().equalsIgnoreCase("@EXP")){ %>selected <%} %>value="@EXP">Expert</option>
														</select>
																
													</div>
												</td>										
												<td class="border-0">
												<div class="input select">
														<select class="form-control selectdee" name="Secretary" id="secretary" data-live-search="true" required="required"   data-placeholder="Select Member secretary" >
												             
														</select>	
														<%if(secretary!=null){ %>
				  						<input type="hidden" name="msmemberid" value="<%=secretary[0]%>">
				  						<%} %>										
												</div>														
											</td>						
										</tr>
										</table>
								</div>
			
									<div class="col-md-3">
									<div class="form-group">
										<label class="control-label">Member Secretary (Proxy)</label>
										<select class="form-control selectdee mt-n5" id="proxysecretary" required="required" name="proxysecretary">
				    						<option value="0"  selected >None</option>
				    						<% for (Object[] obj : EmployeeList1) {%>
												<option value="<%=obj[0]%>" <%if(proxysecretary!=null && proxysecretary[5].toString().equals(obj[0].toString())){ %>selected<%} %> ><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "%>, <%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - " %></option>
											<%} %>
				  						</select>
				  						<%if(proxysecretary!=null){ %>
				  						<input type="hidden" name="psmemberid" value="<%=proxysecretary[0]%>">
				  						<%}%> 
									</div>
								</div>
								
												<div class="col-md-8 mt-p5">									 
					                   <label class="control-label"> Co-Chairperson </label>
					                    	<table class="w-100">
					                        <tr >
												<td class="w-25 border-0">
													 <div class="input select" id="cplab-col">
														<select class="form-control selectdee labNameWidth" name="ccplabocode" tabindex="-1" id="ccplabocode" onchange="ccchairpersonfetch('1')">
															<option disabled="disabled"  selected value="">SELECT</option>
														    <% for (Object[] obj : AllLabList) {%>
															    <option <%if(co_chairperson!=null &&   co_chairperson[9].toString().equals(obj[3].toString())){ %>selected <%} %>value="<%=obj[3]%>"><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - "%></option>
														    <%} %>
														    <option <%if(co_chairperson!=null && co_chairperson[9].toString().equalsIgnoreCase("@EXP")){ %>selected <%} %>value="@EXP">Expert</option>
														</select>
																
													</div>
												</td>										
												<td class="border-0">
												<div class="input select">
														<select class="form-control selectdee" name="co_chairperson" id="co_chairperson" data-live-search="true"    data-placeholder="Select co-chairPerson" >
												             
														</select>	
												<%if(co_chairperson!=null){ %>
				  						<input type="hidden" name="comemberid" value="<%=co_chairperson[0]%>">
				  						<%} %>									
												</div>														
											</td>						
										</tr>
										</table>
								</div>
		
							</div>
						
									<!-- prakarsh -->
							<div class="row">
							
							<div class="col-md-3">
							<div class="form-group">
							<label class="control-label">Reference No.</label>
							
												
							<input type="text" class="form-control"   name="Reference No." value="<%= committeedata[11] != null ? StringEscapeUtils.escapeHtml4(committeedata[11].toString()) : "--" %>" >
							
							</div>
							</div>
						
							<div class="col-md-4">
				         	<div class="form-group">
				            	<label class="control-label" >Formation Date</label>
				  				<input type="date" class="form-control"  data-date-format="dd/mm/yyyy" id="Formationdate" name="Formationdates"  value="<%= committeedata[12] != null ? StringEscapeUtils.escapeHtml4(committeedata[12].toString()) : '-' %>"  >
				        	</div>
				        </div>
							
							</div> 
							<div class="row">			
								<div class="col-md-12" align="center">
					              	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />                  	
									<input type="hidden" name="committeemainid" value="<%=committeemainid%>"> 
									<%if(status.equals("A") || (status.equals("P") && (approvaldata[5].toString().equals("RTDO") || approvaldata[5].toString().equals("CCR"))) ){ %>
				                	<button class=" btn btn-primary btn-sm submit" type="submit"  onclick="Add('committeeeditfrm')" >SUBMIT</button>
				                	<%} %>
				              </div> 
			              </div>
						</form>
				<br>
<!-- 	------------------------------------------------------------------------------- internal and Expert Members----------------------------------------------------------
 -->			
 	<%
 				List<Object[]>tempcommitteemembersall=new ArrayList<>();
 				List<String>memberTypes=Arrays.asList("CC","CS","PS","CH");
 				if(committeemembersall.size()>0) 
 				{
 					tempcommitteemembersall=committeemembersall.stream()
 												.filter(i->!memberTypes.contains(i[8].toString())).collect(Collectors.toList());
 					tempcommitteemembersall=tempcommitteemembersall.stream()
 													.sorted(Comparator.comparingInt(e -> Integer.parseInt(e[11].toString()))).collect(Collectors.toList());
 				}
 				%>
 
 <%if(tempcommitteemembersall.size()>0 ){ %>
 					<hr class="hr-pt hr-pb">
 				<%} %>
 				 <%if(tempcommitteemembersall.size()>0 ){ %>
 		<div class="row">
 		<div class="col-md-2"></div>
 			<div class="col-md-7">
 	<form action="MemberSerialNoUpdate.htm" method="POST">
 		<table  class="table table-bordered table-hover table-striped table-condensed ">
			            	<thead>
			               		<tr>
			               			
			               			<th class="text-center srNo"> Sl No.</th>
			               			<th >Participants</th>			                    	
			                    	<th>Member Type</th>
			                   <%if(loginTypes.contains(logintype)) {%> 	<th>Action</th>  <%} %>
			                    </tr>
			              	</thead>
			              	<tbody>
			              	<%int count=0;
			              	for(Object[]obj:tempcommitteemembersall){%>
			              	<tr>
			              	<td class="d-flex justify-content-center align-items-center">
			            <input type="number" class="form-control w-50" name="newslno" value="<%=obj[11] %>" min="1" max="<%=tempcommitteemembersall.size()%>"> 
			              	<input type="hidden" name="memberId" value="<%=obj[0].toString() %>">
			              	</td>
			              	<td><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %>,<%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - " %></td>
			              	<td> 
										<%  if(obj[8].toString().equalsIgnoreCase("CC")) {		 %>Chairperson<%}
											else if(obj[8].toString().equalsIgnoreCase("CS") ){	 %> Member Secretary<%}
											else if(obj[8].toString().equalsIgnoreCase("CH") ){	 %> Co-Chairperson<%}
											else if(obj[8].toString().equalsIgnoreCase("PS") ) { %>Member Secretary (Proxy) <%}
											else if(obj[8].toString().equalsIgnoreCase("CI")){   %> Internal<%}
											else if(obj[8].toString().equalsIgnoreCase("CW")){	 %> External(<%=obj[12]!=null?StringEscapeUtils.escapeHtml4(obj[12].toString()): " - " %>)<%}
											else if(obj[8].toString().equalsIgnoreCase("CO")){	 %> External(<%=obj[12]!=null?StringEscapeUtils.escapeHtml4(obj[12].toString()): " - "%>)<%}
											else if(obj[8].toString().equalsIgnoreCase("P") ){	 %>Presenter <%}
											else if(obj[8].toString().equalsIgnoreCase("I")){	 %> Addl. Internal<%}
											else if(obj[8].toString().equalsIgnoreCase("W") ){	 %> Addl. External(<%=obj[12]!=null?StringEscapeUtils.escapeHtml4(obj[12].toString()): " - " %>)<%}
											else if(obj[8].toString().equalsIgnoreCase("E") )    {%> Addl. External(<%=obj[12]!=null?StringEscapeUtils.escapeHtml4(obj[12].toString()): " - " %>)<%}
										    // Prudhvi - 27/03/2024 start
											else if(obj[8].toString().equalsIgnoreCase("CIP") )    {%> Industry Partner(<%=obj[12]!=null?StringEscapeUtils.escapeHtml4(obj[12].toString()): " - " %>)<%}
											else if(obj[8].toString().equalsIgnoreCase("IP") )    {%> Addl. Industry Partner(<%=obj[12]!=null?StringEscapeUtils.escapeHtml4(obj[12].toString()): " - " %>)<%}
										%>
										
									</td>
												                   <%if(loginTypes.contains(logintype)) {%> 
									<td>

														<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
												        <input type="hidden" name="committeemainid" value="<%=committeemainid%>">
												      <%--   <input type="hidden" name="committeememberid" value="<%=obj[0] %>" /> --%> 
												        <%if(status.equals("A") || (status.equals("P") && (approvaldata[5].toString().equals("RTDO") || approvaldata[5].toString().equals("CCR"))) ){ %>
														<button class="fa fa-trash btn btn-danger bg-white border border-white" type="submit"
															formaction="CommitteeMemberDelete.htm" formmethod="POST" name="committeememberid" value="<%=obj[0] %>"
														  onclick="return confirm('Are You Sure To Delete this Member?');" ></button>
														<%} %>
									
									
									</td>
									<%} %>
			              	</tr>
			              	<%}%>
			              	
			              	<tr>
			              	<td colspan=1 class="d-flex justify-content-center align-items-center">
			              	<input type="hidden" name="committeemainid" value="<%=committeemainid%>">
			              	<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
			              	<button class="btn btn-sm edit" onclick="return slnocheck('serialnoupdate');">UPDATE</button>
			              	</td>
			              	<td colspan=3></td>
			              	</tr>
			              	</tbody>
			              	</table>
			              	</form>
			              	</div>
			              	
 		</div>	
 				<%} %>
 				
						
	<!-- ---------------------------------------------------------------------committee main members ---------------------------------------------- -->

		       <hr>
		       <br>
		       <%if(status.equals("A")){ %>
		     	  <div class="row" >	
		     	  		
							<table align="center">
								<tr>
 									<td>				
 										<button class="btn btn-primary btn-sm back" type="button"  onclick="submitForm('backfrm');">BACK</button> 						
 									</td>
		 							<td>
										<form  method="post" action="CommitteeConstitutionLetter.htm" target="_blank" >
											<button  type="submit" class="btn btn-sm preview"  >LETTER</button>
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />     
											<input type="hidden" name="committeemainid" value="<%=committeemainid%>">
										</form>
									</td>
									<td>
										<form  method="post" action="CommitteeConstitutionLetterDownload.htm" target="_blank" >
											<button  type="submit"  class="btn btn-sm edit"  ><i class="fa fa-download fs-90"></i></button>
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />    												
											<input type="hidden" name="committeemainid" value="<%=committeemainid%>">
										</form>
									</td>
									<td>
						               	<form  action="SendFormationLetter.htm" method="post" >  						             
						                	<button type="submit" onclick="inviteform()" class=" btn btn-sm prints"  ><i class="fa fa-paper-plane-o" aria-hidden="true"></i>&nbsp; EMAIL</button>
						              		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />  
						              		<input type="hidden" name="committeemainid" value="<%=committeemainid%>">
						              	 </form>
				             		</td>	
				             		<td>
				             		<%if( proposedmainid==null ){
				             			if(LabCode.equalsIgnoreCase("LRDE") && (LoginType.equalsIgnoreCase("A") || LoginType.equalsIgnoreCase("P"))){%>             		
										<form  method="post" action="CommitteeDetails.htm">
											<button  type="submit" class="btn btn-sm add">CONSTITUTE NEW COMMITTEE</button>
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />     
											<input type="hidden" name="committeeid" value="<%=committeeid%>">
											<input type="hidden" name="projectid" value="<%=projectid %>" >	
											<input type="hidden" name="divisionid" value="<%=divisionid%>"> 		
											<input type="hidden" name="initiationid" value="<%=initiationid%>"> 
											<input type="hidden" name="carsInitiationId" value="<%=carsInitiationId %>"> 											
										</form>
											
											
									<%}}else if(!proposedmainid.equals(committeemainid)  ){ 
									if(LabCode.equalsIgnoreCase("LRDE") && (LoginType.equalsIgnoreCase("A") || LoginType.equalsIgnoreCase("P"))) {%>
										<form  method="post" action="CommitteeMainMembers.htm">
											<button  type="submit" class="btn btn-sm add">PROPOSED COMMITTEE</button>
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />     
											<input type="hidden" name="committeemainid" value="<%=proposedcommitteemainid[0]%>"> 											
										</form>
					             		
				             		<%} }%>
				             		</td>
			             		
										
								<%if( Long.parseLong(divisionid)>0 || Long.parseLong(projectid)>0 || Long.parseLong(initiationid)>0 || Long.parseLong(carsInitiationId)>0){ %>
								 	<td>
										<form  method="post" action="ProjectCommitteeDescriptionTOREdit.htm">
											<button  type="submit" class="btn btn-sm edit">DESCRIPTION</button>
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />     
											<input type="hidden" name="committeemainid" value="<%=committeemainid%>">		
											<input type="hidden" name="operation" value="approve">								
										</form>
									</td>								
								<%} %>
								<%if(CommitteMainEnoteList!=null ) {%>
								<td>
									<form  method="post" action="CommitteeFlow.htm">
												<button  type="submit" class="btn btn-sm submit approvalHistoryButton"> Approval History </button>
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />     
												<input type="hidden" name="committeemainid" value="<%=committeemainid%>">
												<input type="hidden" name="projectid" value="<%=projectid %>" >	
												<input type="hidden" name="divisionid" value="<%=divisionid%>"> 		
												<input type="hidden" name="initiationid" value="<%=initiationid%>"> 
												<input type="hidden" name="carsInitiationId" value="<%=carsInitiationId %>"> 
								</form>	
								</td>
								<%} %>
								</tr>
							</table>
						</div>	
					<%} %>
						
								
							
					<% if(status.equals("P") ){ %>
							<%if(Long.parseLong(approvaldata[8].toString())>0){ %>
								<%-- <h4 align="center"><%=approvaldata[7] %></h4> --%>
							<%} %>
						<div class="row" >	
						
							<table align="center" class="mb-2">
							
								<tr>
									
			 						<td>				
			 							<button class="btn btn-primary btn-sm back" type="button"  onclick="submitForm('backfrm');">BACK</button> 						
			 						</td>	
			 						<td>
									<%
									if(status.equals("P") ) { %>
									<form  method="post" action="CommitteeFlow.htm">
												<button  type="submit" class="btn btn-sm submit">Preview</button>
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />     
												<input type="hidden" name="committeemainid" value="<%=proposedcommitteemainid[0]%>">
												<input type="hidden" name="projectid" value="<%=projectid %>" >	
												<input type="hidden" name="divisionid" value="<%=divisionid%>"> 		
												<input type="hidden" name="initiationid" value="<%=initiationid%>"> 
												<input type="hidden" name="carsInitiationId" value="<%=carsInitiationId %>"> 
										</form>	
										</td>	
											<td>
											<%if(Long.parseLong(divisionid)>0 || Long.parseLong(projectid)>0 ||Long.parseLong(initiationid)>0){ %>
											<form  method="post" action="ProjectCommitteeDescriptionTOREdit.htm">
											<button  type="submit" class="btn btn-sm edit">DESCRIPTION</button>
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />     
											<input type="hidden" name="committeemainid" value="<%=committeemainid%>">		
											<input type="hidden" name="operation" value="approve">								
											</form>
											<%} %>
										</td>
										<%} %>	
										
									
									<td>
									<form  method="post" action="CommitteeConstitutionLetterDownload.htm" target="_blank" >
											<button  type="submit"  class="btn btn-sm edit"  ><i class="fa fa-download fs-90"></i></button>
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />    												
											<input type="hidden" name="committeemainid" value="<%=committeemainid%>">
										</form>
									</td>
								</tr>
							</table>	
							
								</div> 
							
						<%Object[]NewApprovalList = (Object[])request.getAttribute("NewApprovalList"); %>
						
									<%if(NewApprovalList!=null ){ %>
							 	<div class="row text-center">
				                <table  align="center" >
				                	<tr>
				                	
				                		<td class="trup checkPngConst">
				                		&nbsp;<%if(Arrays.asList("FWD","RC1","RC2","RC3","APR").contains(CommitteMainEnoteList[15].toString())) {%>
				                		<img src="view/images/check.png"><br>
				                		<%} %>
				                			Constituted By 
				                			<br>
				                			<%=NewApprovalList[0]!=null?StringEscapeUtils.escapeHtml4(NewApprovalList[0].toString()): " - " %>
				                		</td>
				                		<%if(NewApprovalList!=null && NewApprovalList[2]!=null){ %>
				                		<td rowspan="2">
				                			 <b>----------&gt;</b>
				                		</td >
				                		
				                		<td class="trup checkPngRec1">
				                			&nbsp;<%if(Arrays.asList("RC1","RC2","RC3","APR").contains(CommitteMainEnoteList[15].toString())) {%>
				                		<img src="view/images/check.png"><br>
				                		<%} %>
				                			Recommended Officer 1
				                		
				                			<br>
				                			<%=NewApprovalList[1]!=null?StringEscapeUtils.escapeHtml4(NewApprovalList[1].toString()): " - "  %>
				                		</td>
				                		
				                		<%} %>
				                		<%if(NewApprovalList!=null && NewApprovalList[4]!=null){ %>
				                		<td rowspan="2">
				                			 <b>----------&gt;</b>
				                		</td>
				                		
				                		<td class="trup checkPngRec2">
				                			&nbsp;<%if(Arrays.asList("RC2","RC3","APR").contains(CommitteMainEnoteList[15].toString())) {%>
				                		<img src="view/images/check.png"><br>
				                		<%} %>
				                		
				                		Recommended Officer 2
				                	
				                		<br>
				                			<%=NewApprovalList[3]!=null?StringEscapeUtils.escapeHtml4(NewApprovalList[3].toString()): " - "  %>
				                		</td>
				                		<%} %>
				                		<%if(NewApprovalList!=null && NewApprovalList[6]!=null){ %>
				                		<td rowspan="2">
				                			 <b>----------&gt;</b>
				                		</td>
				                		
				                		<td class="trup checkPngRec3">
				                			&nbsp;<%if(Arrays.asList("RC3","APR").contains(CommitteMainEnoteList[15].toString())) {%>
				                		<img src="view/images/check.png">	<br>
				                		<%} %>
				                	
				                			Recommended Officer 3
				                		
				                			<br>
				                			<%=NewApprovalList[5]!=null?StringEscapeUtils.escapeHtml4(NewApprovalList[5].toString()): " - "  %>
				                		</td>
				                		<%} %>
				                		<%if(NewApprovalList!=null && NewApprovalList[8]!=null){ %>
				                		<td rowspan="2">
				                			 <b>----------&gt;</b>
				                		</td>
				                		
				                		<td class="trup checkPngAppr">
				                		&nbsp;<%if(Arrays.asList("APR").contains(CommitteMainEnoteList[15].toString())) {%>
				                		<img src="view/images/check.png"><br>
				                		<%} %>
				                		
				                			Approving Officer
				                			<br>
				                			<%=NewApprovalList[7]!=null?StringEscapeUtils.escapeHtml4(NewApprovalList[7].toString()): " - "  %>
				                		</td>
				                		<%} %>
				                			                		
				                	</tr>	
				                	
				                	</table>
				                	</div>
					<%} %>			 
						<%} %>
																
		 			
	 				
		      
<!-- ------------------------------------- add new members ---------------------------------------------------------------- -->

						<div class="row">
							<div class="col-md-6 ml-3">
								<label class="chairPerson" for="repids"> Add More Members</label>
								<hr>
<!-- -------------------------------------- Internal members -------------------------------------------- -->
								<form action="CommitteeMainMembersSubmit.htm" method="post" name="editfrm" id="editfrm" >				
										
										<div class="row">				
											<div class="col-md-9">
												<table class="table  table-bordered table-hover table-striped table-condensed  info shadow-nohover w-100 mt-10" id="">
													<thead>  
														<tr>
															<th> Internal Members</th>
														</tr>
													</thead>								
													<tbody>
														<tr class="tr_clone">
															<td>
																<div class="input select">
																	<select class="form-control selectdee internalMembers" name="InternalMemberIds" data-live-search="true" required  data-placeholder="Select Members" multiple>
													                <%for(Object[] obj:EmployeeList){ %>																							
																		<option value="<%=obj[0]%>"><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "%>, <%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - "%></option>																				
																	<%} %>
																	</select>
																<input type="hidden" name="InternalLabCode" value="<%=LabCode%>"> 	
																</div>
															</td>
														</tr>																
													</tbody>
												</table>
											</div>
											
										<div class="col-md-2 align-self-center">	
										<%if(status.equals("A") || (status.equals("P") && (approvaldata[5].toString().equals("RTDO") || approvaldata[5].toString().equals("CCR"))) ){ %>				
											<button class="btn  btn-sm submit" name="submit" type="submit" onclick="return confirm('Are you Sure to Add this Member(s)');"  >SUBMIT</button>
											<%} %>							
										</div>									
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										<input type="hidden" name="committeemainid" value="<%=committeemainid%>"> 
									</div>
							</form>
	<!-- --------------------------------------Internal members -------------------------------------------- -->
	
	
	<!-- --------------------------------------External MembersWithin DRDO -------------------------------------------- -->
							<form action="CommitteeMainMembersSubmit.htm" method="post" name="editfrm" id="editfrm" >							
								<div class="row">				
									<div class="col-md-9">
										<table class="table  table-bordered table-hover table-striped table-condensed  info shadow-nohover w-100 mt-10" id="">
											<thead>  
												<tr >
													<th colspan="2" >External Members (Within DRDO) 
													<button class="btn bg-primary float-right addExpertButtonColor" type="button" id="externalAdd">ADD NEW </button> </th>
													
												</tr>
											</thead>								
											<tbody>
												<tr class="tr_clone1">
													<td class="trCloneWidth">
														 <div class="input select">
															 <select class="form-control selectdee" name="Ext_LabCode" tabindex="-1" required  id="Ext_LabCode" onchange="employeename()">
																<option disabled="true"  selected value="">Lab Name</option>
																    <% for (Object[] obj : AllLabList) {
																    if(!LabCode.equals(obj[3].toString())){%>
																    <option value="<%=obj[3]%>"><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - "%></option>
																    <%}
																    }%>
															</select>
															
														</div>
													</td>										
													<td class="externalMemberIdWidth">
														<div class="input select">
															<select class="form-control selectdee" name="ExternalMemberIds" id="ExternalMember" data-live-search="true"   data-placeholder="Select Members" multiple>

															</select>															
														</div>														
													</td>						
												</tr>
											</tbody>
										</table>
									</div>
									<div class="col-md-2 align-self-center">					
										<%if(status.equals("A") || (status.equals("P") && (approvaldata[5].toString().equals("RTDO") || approvaldata[5].toString().equals("CCR"))) ){ %>
										<button class="btn  btn-sm submit" name="submit" value="add" type="submit" onclick="return confirm('Are you Sure to Add this Member(s)');" >SUBMIT</button>
										<%} %>							
									</div>
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									<input type="hidden" name="committeemainid" value="<%=committeemainid%>"> 
								</div>
							</form>
				<!-- --------------------------------------External Members Within DRDO -------------------------------------------- -->
				<!-- --------------------------------------External Members Outside DRDO -------------------------------------------- -->
							<form action="CommitteeMainMembersSubmit.htm" method="post"  >				
								<div class="row">				
									<div class="col-md-9">
										<table class="table  table-bordered table-hover table-striped table-condensed  info shadow-nohover w-100 mt-10" id="">
											<thead>  
												<tr>
													<th>Expert Member (Outside DRDO)
													<button class="btn bg-primary float-right addExpertButtonColor" type="button" id="expertAdd">ADD EXPERT </button> </th>
											
												</tr>
											</thead>								
											<tbody>
												<tr class="tr_clone2">
													<td >
														<div class="input select ">
															<select class="selectdee expertMemberIdWidth" name="ExpertMemberIds" id="ExpertMemberIds"   data-live-search="true" data-placeholder="Select Members" required multiple>
												            	<%for(Object[] obj:expertlist){ %>																									
																	<option value="<%=obj[0]%>"><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "%>, <%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - "%></option>	
																													
																<%} %>
																					
															</select>
														
														</div>
													</tr>
												</table>
									</div>
									<div class="col-md-2 align-self-center">					
										<%if(status.equals("A") || (status.equals("P") && (approvaldata[5].toString().equals("RTDO") || approvaldata[5].toString().equals("CCR"))) ){ %>
										<button class="btn  btn-sm submit" name="submit" value="add" type="submit" onclick="return confirm('Are you Sure to Add this Member(s)');" >SUBMIT</button>
										<%} %>							
									</div>	
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />											
									<input type="hidden" name="committeemainid" value="<%=committeemainid%>"> 
							</div>
						</form>
				<!-- --------------------------------------External Members Outside DRDO -------------------------------------------- -->
				<!-- Prudhvi - 27/03/2024 start -->
				<!-- -------------------------------------- Industry Partner -------------------------------------------- -->
							<form action="CommitteeMainMembersSubmit.htm" method="post"  >				
								<div class="row">				
									<div class="col-md-9">
										<table class="table  table-bordered table-hover table-striped table-condensed  info shadow-nohover mt-10" id="table1">
								<thead>  
									<tr id="">
										<th colspan="2"> Industry Partner</th>
									</tr>
								</thead>
								<tr class="tr_clone1">
									<td class="trCloneWidth">							
										<div class="input select">
											<select class="form-control selectdee" name="industryPartnerId" tabindex="-1"   id="industryPartnerId" onchange="industrypartnerrepname()" required>
												<option disabled="true"  selected value="">Industry Partner</option>
													<% for (IndustryPartner partner : industryPartnerList) {
													%>
														<option value="<%=partner.getIndustryPartnerId()%>"
														data-subtext="(<%=partner.getIndustryCity()+" - "+partner.getIndustryPinCode() %>)"
														><%=partner.getIndustryName()%> (<%=partner.getIndustryCity()+" - "+partner.getIndustryPinCode() %>)</option>
														
													<%}%>
													<option value="0">ADD NEW</option>
											</select>
											<input type="hidden" name="industrypartnerlabid" value="@IP" />
										</div>
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />	
		 								<input type="hidden" name="rep" id="rep4" value="0" />
									</td>
									<td class="externalMemberIdWidth">
										<div class="input select ">
											<select class="form-control selectdee" name="industryPartnerRep" id="industryPartnerRep" data-live-search="true"   data-placeholder="Select Members" multiple onchange ="addIndusRep()">
											</select>
										</div>
									</td>						
								</tr>
							</table>
									</div>
									<div class="col-md-2 align-self-center">					
										<%if(status.equals("A") || (status.equals("P") && (approvaldata[5].toString().equals("RTDO") || approvaldata[5].toString().equals("CCR"))) ){ %>
										<button class="btn  btn-sm submit" name="submit" value="add" type="submit" onclick="return confirm('Are you Sure to Add this Member(s)');" >SUBMIT</button>
										<%} %>							
									</div>	
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />											
									<input type="hidden" name="committeemainid" value="<%=committeemainid%>"> 
							</div>
						</form>
				<!-- -------------------------------------- Industry Partner -------------------------------------------- -->
				<!-- Prudhvi - 27/03/2024 end -->
				</div>
				
							<div class="col-md-5">
								<div class="row">	
									<div  class="col-md-12 mt-t20 mt-l20">
										<form action="CommitteeRepMemberAdd.htm" method="post">
											<table class="w-100">	
												<tr>
													<td colspan="2">
										           		<label class="chairPerson" for="repids"> Add Representatives </label>
										           	</td>
									           	<tr>
										           	<td class="width-60">
											  			<select class="form-control selectdee" id="repids" name="repids" onchange="handleRepChange()" data-placeholder="Select Rep Types" multiple="multiple" >
															<option  disabled="disabled" value="0">Choose...</option>
															<option value="0">ADD NEW</option>
															<%	for (Object[] obj  : committeerepnotaddedlist) {%>
														     	<option value="<%=obj[0]%>" ><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - "%>  </option>
															<% } %>
														</select>
													</td>
													<td class="width-20"> 		  					
														<%if(status.equals("A") || (status.equals("P") && (approvaldata[5].toString().equals("RTDO") || approvaldata[5].toString().equals("CCR"))) ){ %>
										     	  		<button class="btn  btn-sm submit" type="submit"  onclick="return handleRepClick()" >SUBMIT</button>
										     	  		<%} %>
										     	   </td>
									     	   </tr>							
											</table>
										
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />											
											<input type="hidden" name="committeemainid" value="<%=committeemainid%>"> 
										</form>						
									</div>
								</div>
								<div class="row">	
									<div  class="col-md-12 mt-t20 mt-l20">
											<h5 class="representativeColor">Representatives</h5>
											<hr>						
											<table border='0'>
					
												<tbody>
													<%if(committeeMemberreplist.size()>0){
														int count = 1;
														for (Object[] obj : committeeMemberreplist) {
													%>
													<tr id="repmem<%=obj[0]!=null?StringEscapeUtils.escapeHtml4(obj[0].toString()): " - " %>">
														<td><sp> <%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - "%> </sp></td>
														<td>
															<%if(status.equals("A") || (status.equals("P") && (approvaldata[5].toString().equals("RTDO") || approvaldata[5].toString().equals("CCR"))) ){ %>
															<button class="fa fa-trash btn btn-danger bg-white border border-white" type="button" onclick="memberrepdelete('<%=obj[0] %>');" ></button>
															<%} %>
														</td>
													</tr>
													<%	count++; 	} } %>
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
				
<!-- --------------------------------------External Members Outside DRDO -------------------------------------------- -->
				
					<%if(Long.parseLong(projectid)>0){ %>
						<form method="post" action="ProjectMaster.htm" name="backfrm" id="backfrm">
							<input type="hidden" name="projectid" value="<%=projectid%>"/>							
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						</form>
					<%}else if(Long.parseLong(divisionid)==0 && Long.parseLong(projectid)==0 && Long.parseLong(initiationid)==0 && Long.parseLong(carsInitiationId)==0 ){ %>
						<form method="post" action="CommitteeList.htm" name="backfrm" id="backfrm">
							<input type="hidden" name="projectid" value="<%=projectid%>"/> 							
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />								
							<input type="hidden" name="projectappliacble" value="N">		
						</form>
					<%}else if(Long.parseLong(divisionid)>0 ){ %>
	       				<form method="post" action="DivisionCommitteeMaster.htm" name="backfrm" id="backfrm">
							<input type="hidden" name="divisionid" value="<%=divisionid %>">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />									
						</form>		
		      		<%}else if(Long.parseLong(initiationid)>0 ){ %>
		      			<form method="post" action="InitiationCommitteeMaster.htm" name="backfrm" id="backfrm">
							<input type="hidden" name="initiationid" value="<%=initiationid %>">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />									
						</form>	      		
		      		<%}else if(Long.parseLong(carsInitiationId)>0 ){ %>
		      			<form method="post" action="CARSInitiationList.htm" name="backfrm" id="backfrm">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />									
						</form>	      		
		      		<% } %>




		<div class="modal fade bd-example-modal-lg" id="externalAddModal"
			tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel"
			aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content modalwidth">
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
										for (Object[] obj : AllLabList) {
										    if(!LabCode.equals(obj[3].toString())){%>
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
										class="form-control form-control fs-15px w-100 employeeNoText" type="text" id="EmpNo"
										name="EmpNo" required="required" maxlength="255">
								</div>
							</div>
							<div class="col-md-4">
								<div class="form-group">
									<label>Employee Name:<span class="mandatory text-danger"
										>*</span></label> <input
										class="form-control form-control fs-15px w-100 employeeNameText" type="text" id="EmpName"
										name="EmpName" required="required" maxlength="255">
								</div>
							</div>

							<div class="col-md-4">
								<div class="form-group">
									<label>Designation:<span class="mandatory text-danger"
										>*</span></label> <select
										class="form-control selectdee fs-5px" id="Designation"
										name="Designation" data-container="body"
										data-live-search="true" required="required">
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

		<div class="modal fade bd-example-modal-lg" id="expertAddModal"
			tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel"
			aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content modalwidth">
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
									<label>Expert Name:<span class="mandatory text-danger">*</span></label> <input
										class="form-control form-control fs-15px w-100 employeeNameText" type="text" id="ExtEmpName"
										name="EmpName" required="required" maxlength="255">
								</div>
							</div>
							
							


							<div class="col-md-4">
								<div class="form-group">
									<label>Rank/Salutation</label><br> <select
										class="form-control selectdee fs-5px" id="Exttitle" name="title"
										data-container="body" data-live-search="true">
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
		<div class="modal fade bd-example-modal-lg" id="industryPartnerModal"
			tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel"
			aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content modalwidth">
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
				<div class="modal-content modalwidth">
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
										<thead class="text-white text-center repDetailsTHeadBgColor">
											<tr>
										    	<th class="thPadding">Name</th>
										    	<th class="thPadding">Designation</th>
										    	<th class="thPadding">Mobile No</th>
										    	<th class="thPadding">Email</th>
												
											</tr>
										</thead>
								 		<tbody>
									 		<tr class="tr_clone_repdetails">
												<td class="trClonePadding">
													<input class="form-control" type="text" id="repName" name="repName" maxlength="255" placeholder="Enter Rep Name" required="">
												</td>	
												<td class="trClonePadding">
													<input class="form-control" type="text" id="repDesignation" name="repDesignation" placeholder="Enter Rep Designation" maxlength="255" required="">
												</td>	
												<td class="trClonePadding">
													<input class="form-control" type="text" id="repMobileNo" maxlength="10" name="repMobileNo" placeholder="Enter Rep Mobile No" required="" oninput="this.value = this.value.replace(/[^0-9.]/g, '').replace(/(\..*?)\..*/g, '$1');">
												</td>
												<td class="trClonePadding">
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
		<div class="modal fade bd-example-modal-lg" id="representativeAddModal"
			tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel"
			aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content modalwidth">
					<div class="modal-header">
						<h5 class="modal-title" id="exampleModalLabel"> Add  Representative </h5>
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
					</div>
					<div class="modal-body">
	
			
					<div class="row">
							<div class="col-md-2"></div>						
							<div class="col-md-4">
								<div class="form-group">
									<label class="control-label">Rep Code</label><span class="mandatory">*</span>
									<input class="form-control" type="text" id="repCode" name="repCode" maxlength="255" placeholder="Enter Rep Code" required>		
								</div>
							</div>
							<div class="col-md-4">
								<div class="form-group">
									<label class="control-label"> Rep Name</label><span class="mandatory">*</span>
									<input class="form-control" type="text" id="repName1" name="repName1" maxlength="1000" placeholder="Enter Rep Name" required>	
								</div>
							</div>
							
						</div>
						
						<div class="mt-2" align="center">
						<button class="btn submit" onclick="reperesentativeAdd()">SUBMIT</button>
						</div>

					</div>

				</div>
			</div>
		</div>



	</div>

<script type="text/javascript">


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



function employeename(){

	$('#ExternalMember').val("");
	
		var $CpLabCode  = $('#Ext_LabCode').val()
		
			if($CpLabCode !=""){
				$.ajax({

					type : "GET",
					url : "ExternalEmployeeListFormation.htm",
					data : {
						CpLabCode : $CpLabCode ,
						committeemainid : '<%=committeemainid%>'
					   },
					datatype : 'json',
					success : function(result) {
						var result = JSON.parse(result);
						
						var values = Object.keys(result).map(function(e) {
		 					return result[e]
							  
						});
						console.log(values.length);
						var s = '';
						s += '<option value="">--Select--</option>';
						for (i = 0; i < values.length; i++) 
						{
							s += '<option value="'+values[i][0]+'">' +values[i][1].trim().replaceAll("<","").replaceAll(">","")+ ", " +values[i][3].replaceAll("<","").replaceAll(">","")+""  + '</option>';
						} 
						 
						$('#ExternalMember').html(s);
						
					}
				});

}
	}
	
	

</script>



<script type="text/javascript">
function submitForm1(myform)
{ 
 	myconfirm('Are You Sure To Add This members ?',myform);
 	event.preventDefault();
}
function submitForm(frmid)
{ 
  document.getElementById(frmid).submit(); 
} 
</script>



<script type="text/javascript">



function memberrepdelete(memrepid){			
	
	if(confirm('Are You Sure To Delete ?')){
				$.ajax({

						type : "GET",
						url : "CommitteeMemberRepDelete.htm",
						data : {
								memrepid : memrepid
								},
						datatype : 'json',
						success : function(result) {
						var result = JSON.parse(result);
						
							if(result===1)
							{
								$('#repmem'+memrepid).remove();
								replacerepdd();
							}else if(result===0)
							{
								alert('Delete Operation Failed, Try Again !');
							}
						}
					});
				}
			}
function handleRepClick() {
    var repSelect = $('#repids').val(); 

    if (!repSelect || repSelect.length === 0) {
        alert("Please Select Representative");
        return false;
    }

    if (repSelect.includes("0")) {
        alert("Please Remove Add New Option");
        return false;
    }

    return confirm('Are you sure to Add this Representative(s)?');
}


function replacerepdd(){
				
			$.ajax({		
				type : "GET",
				url : "CommitteeRepNotAddedList.htm",
				data : {
						committeemainid : '<%=committeemainid%>'
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
						
					s += '<option value="'+values[i][0]+'">'
						+values[i][2].replaceAll("<","").replaceAll(">","") + " (" +values[i][1].replaceAll("<","").replaceAll(">","")+")" 
						+ '</option>';
				} 
					 
				$('#repids').html(s);
						
		}
	});
}

</script>


<script type="text/javascript">

 $(document).ready(function(){	
	 
	chairpersonfetch('0');
	msfetch('0');
	ccchairpersonfetch('0')
}); 

	
		function chairpersonfetch(hint){
			$('#chairperson').val("");
				var $CpLabCode = $('#CpLabCode').val();
						if($CpLabCode !=""){
				
						$.ajax({		
							type : "GET",
							url : "ChairpersonEmployeeListFormation.htm",
							data : {
								CpLabCode : $CpLabCode,
								committeemainid : '<%= committeemainid %>',
								   },
							datatype : 'json',
							success : function(result) {
		
							var result = JSON.parse(result);
								
							var values = Object.keys(result).map(function(e) {
										 return result[e]
									  
							});
								
					var s = '';
						s += '<option value="">'+"--Select--"+ '</option>';
								 for (i = 0; i < values.length; i++) {									
									s += '<option value="'+values[i][0]+'">'
											+values[i][1].replaceAll("<","").replaceAll(">","") + ", " +values[i][3].replaceAll("<","").replaceAll(">","")
											+ '</option>';
								} 
								 
								$('#chairperson').html(s);
								if(hint=='0' && $CpLabCode =='<%=chairperson[9]%>'){
									$('#chairperson').val('<%=chairperson[5]%>');
								}
															
							}
						});
		
		}
	}
		
		function msfetch(hint){
			$('#secretary').val("");
			var $CpLabCode = $('#mSLabCode').val();
					if($CpLabCode !=""){
			
					$.ajax({		
						type : "GET",
						url : "ChairpersonEmployeeListFormation.htm",
						data : {
							CpLabCode : $CpLabCode,
							committeemainid : '<%= committeemainid %>',
							   },
						datatype : 'json',
						success : function(result) {
	
						var result = JSON.parse(result);
							
						var values = Object.keys(result).map(function(e) {
									 return result[e]
								  
						});
							
				var s = '';
					s += '<option value="">'+"--Select--"+ '</option>';
							 for (i = 0; i < values.length; i++) {									
								s += '<option value="'+values[i][0]+'">'
										+values[i][1].replaceAll("<","").replaceAll(">","")+", " +  values[i][3].replaceAll("<","").replaceAll(">","")
										+ '</option>';
							} 
							 <%if(secretary!=null){ %>
							$('#secretary').html(s);
							if(hint=='0' && $CpLabCode =='<%=secretary[9]%>'){
								$('#secretary').val('<%=secretary[5]%>');
							}
								<%}%>						
						}
					});
	
	}
		}
		function ccchairpersonfetch(hint){
			$('#co_chairperson').val("");
			var $CpLabCode = $('#ccplabocode').val();
					if($CpLabCode !=""){
			
					$.ajax({		
						type : "GET",
						url : "ChairpersonEmployeeListFormation.htm",
						data : {
							CpLabCode : $CpLabCode,
							committeemainid : '<%= committeemainid %>',
							   },
						datatype : 'json',
						success : function(result) {
	
						var result = JSON.parse(result);
							
						var values = Object.keys(result).map(function(e) {
									 return result[e]
								  
						});
							
				var s = '';
					s += '<option value="">'+"--Select--"+ '</option>';
							 for (i = 0; i < values.length; i++) {									
								s += '<option value="'+values[i][0]+'">'
										+values[i][1].replaceAll("<","").replaceAll(">","")+", " +  values[i][3].replaceAll("<","").replaceAll(">","")
										+ '</option>';
							} 
							 
							$('#co_chairperson').html(s);
							<%if(co_chairperson!=null){%>
							if(hint=='0' && $CpLabCode =='<%=co_chairperson[9]%>'){
								$('#co_chairperson').val('<%=co_chairperson[5]%>');
							}
							<%}%>							
						}
					});
	
	}
		}	
</script>

<script>


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


function Add(myfrm){

    
    var fieldvalues = $("select[name='Member']").map(function(){return $(this).val();}).get();
    
    var $chairperson = $("#chairperson").val();
    var $cplabCode = $('#CpLabCode').val();
    var $LabCode = '<%=LabCode%>';
    var $mSLabCode=$('#mSLabCode').val();
    
    var $cochairperson = $("#co_chairperson").val();
    var $secretary = $("#secretary").val();
    var $proxysecretary=$("#proxysecretary").val();
    
    if($mSLabCode===$cplabCode){
		if($chairperson==$secretary){
			 alert("Chairperson and Member Secretary Should Not Be The Same Person ");	   
			 event.preventDefault();
				return false;
		}
    }
    
    if( $LabCode === $cplabCode){
		if($chairperson==$secretary){
			 alert("Chairperson and Member Secretary Should Not Be The Same Person ");	   
			 event.preventDefault();
				return false;
		}
		
		if($cochairperson == $chairperson)
		{
			alert("Chairperson and Co-Chairperson Should Not Be The Same Person ");	   
			 event.preventDefault();
				return false;
		}
		
		if($proxysecretary==$chairperson)
		{
			alert("Chairperson and Proxy Member Secretary Should Not Be The Same Person ");	   
			 event.preventDefault();
				return false;
		}
	}
    
    if($cochairperson==$secretary)
	{
		alert("Member Secretary and Co-Chairperson Should Not Be The Same Person ");	   
		 event.preventDefault();
			return false;
	}
    if($proxysecretary==$secretary)
	{
		alert("Member Secretary and Proxy Member Secretary Should Not Be The Same Person ");	   
		 event.preventDefault();
			return false;
	}
	
	if($cochairperson!=='0' &&  $proxysecretary!=='0' && $cochairperson==$proxysecretary )
	{
		alert("Co-Chairperson and Proxy Member Secretary Should Not Be The Same Person ");	   
		 event.preventDefault();
			return false;
	}
    
	
	
	
    
    for (var i = 0; i < fieldvalues.length; i++) {
    	
    	if($chairperson==fieldvalues[i]){
    		 alert("Chairperson Should Not Be A Member ");	   
    		 event.preventDefault();
    			return false;
    	}
    	
    	if($secretary==fieldvalues[i]){
   		 alert("Member Secretary Should Not Be A Member ");	   
   		 event.preventDefault();
   			return false;
 	  	}
    	
    	if($proxysecretary==fieldvalues[i]){
      		 alert("Proxy Member Secretary Should Not Be A Member ");	   
      		 event.preventDefault();
      			return false;
    	  	}
    	
   } 
    
	if(confirm('Are you Sure to Update this Committee?')){
		return true;
	}else{
		event.preventDefault();
	}

  return true;
}

 

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
								url : "IndustryPartnerRepListInvitationsMainMembers.htm",
								data : {
									industryPartnerId : $industryPartnerId,
									committeemainid : '<%=committeemainid %>' 	
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
							
							s += '<option value="'+values[i][0]+'">'
									+values[i][1].replaceAll("<","").replaceAll(">","") + " (" +values[i][3].replaceAll("<","").replaceAll(">","")+")" 
									+ '</option>';
						} 
						 s=s+'<option value="0">ADD NEW </option>'
						$('#industryPartnerRep').html(s);
						
				
						
					}
				});

}
	}
	
function slnocheck(formid) {
	
	 var arr = document.getElementsByName("newslno");

	var arr1 = [];
	for (var i=0;i<arr.length;i++){
		arr1.push(arr[i].value);
	}		 
	 
    let result = false;
  
    const s = new Set(arr1);
    
    if(arr.length !== s.size){
       result = true;
    }
    if(result) {
   	event.preventDefault();
       alert('Serial No contains duplicate Values');
       return false;
    } else {
   	 return confirm('Are You Sure to Update?');
    }
  }
$( document ).ready(function() {
	var formationDate=new Date(<%=committeedata[12]%>)
	console.log("formationDate --- "+formationDate)
	console.log("hiii")
});

$("#externalAdd").click(function(){
	$('#externalAddModal').modal('show');
	});
$("#expertAdd").click(function(){
	$('#expertAddModal').modal('show');
	});



function empNoCheck(frmid)
{
	var ExternalMember = $('#ExternalMember').val();
	
	var labId=$('#labModal').val();
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
										$('#Ext_LabCode').val(labId).trigger('change');
										console.log(ExternalMember)
										$('#externalAddModal').modal('hide');
										ExternalMember.push(ajaxresult+"");
										$('#ExternalMember').val(ExternalMember).trigger('change');
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



function formCheck(frmid)
{
	var ExpertMemberIdshtm = $('#ExpertMemberIds').html();
	
	var value = $('#ExpertMemberIds').val();
	
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
					$('#ExpertMemberIds').html(ExpertMemberIdshtm)
					$('#ExpertMemberIds').val(value).trigger('change');
					
					if(ajaxresult.length>0){
						$('#Exttitle').val('');
						$('#Extsalutation').val('');
						$('#ExtEmpName').val('');
						$('#ExtDesignation').val('');
						$('#organization').val('');
						$('#expertAddModal').modal('hide');
					}
				}
			})
			
		}
		else{
			event.preventDefault();
			return false;
		}
	}
	
}

$(document).on('shown.bs.modal', function () {
	  $(this).find('.selectdee').each(function () {
	    if (!$(this).hasClass('select2-hidden-accessible')) {
	      $(this).select2({
	        dropdownParent: $(this).closest('.modal'),
	        width: '100%'
	      });
	    }
	  });
	});

<!-- Prudhvi 27/03/2024 end -->

function handleRepChange() {
	var repvalue = $('#repids').val();
	
	/* console.log(repvalue) */
	if(repvalue[0] === '0' || repvalue[0] === 0){
		$('#representativeAddModal').modal('show');
	}
}

function reperesentativeAdd() {
	
	const repCode = $('#repCode').val();
	const repName = $('#repName1').val();
	
	if(repCode===''||repName===''){
		alert("Please fill all the fields")
		event.preventDefault();
		return false;
	}
	
	if(window.confirm("Are you sure to Submit?")){
		$.ajax({
			type:'GET',
			url:'representativeAdd.htm',
			datatype:'json',
			data:{
				repName:repName,
				repCode:repCode,
			},
			success: function(result) {
			    var ajaxresult = JSON.parse(result);

			    if (ajaxresult.RepId != null) {
			        alert('Representative Added Successfully!');

			        // Create new option element
			        var newOption = $("<option>", {
			            value: ajaxresult.RepId,
			            text: ajaxresult.RepName.replaceAll("<","").replaceAll(">","")
			        });

			        // Remove ADD NEW temporarily
			        var $addNew = $('#repids option[value="0"]').detach();

			        // Add new option
			        $('#repids').append(newOption);

			        // Append ADD NEW back to end
			        $('#repids').append($addNew);

			        // Set selected value to new Rep
			        $('#repids').val(ajaxresult.RepId);

			        // If using Select2, trigger update
			        $('#repids').trigger('change');

			        // Clear modal inputs
			        $('#repCode').val('');
			        $('#repName1').val('');

			        // Hide modal
			        $('#representativeAddModal').modal('hide');
			    }
			}
		})
		
	}else{
		event.preventDefault();
		return false;
	}
	
}
function handleInputChangerepName(value) {
    if (value.length >=2) {
        $.ajax({
            type: "GET",
            url: "RepNameDuplicateCheck.htm",
            data: {
				repName:value,
            },
            datatype: 'json',
            success: function(result) {
                var ajaxresult = JSON.parse(result); 
                //Check if the Programme code already exists
                if (ajaxresult >= 1) {
                	 $('#repName1').val('');
                    alert('Representative Name Already Exists');
                }
               
            },
            error: function() {
                alert('An error occurred while checking the Representative Name.');
            }
        });
    }
}

function handleInputChangerepCode(value) {
    if (value.length >=2) {
        $.ajax({
            type: "GET",
            url: "RepCodeDuplicateCheck.htm",
            data: {
                repCode: value,
            },
            datatype: 'json',
            success: function(result) {
                var ajaxresult = JSON.parse(result); 
                //Check if the Rep code already exists
                if (ajaxresult >= 1) {
                	 $('#repCode').val('');
                    alert('Representative Code Already Exists');
                }
               
            },
            error: function() {
                alert('An error occurred while checking the Representative code.');
            }
        });
    }
}
		

</script>
	
</body>

</html>