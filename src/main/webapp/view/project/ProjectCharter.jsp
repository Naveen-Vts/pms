<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@page import="com.vts.pfms.FormatConverter"%>

<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/projectModule/projectCharter.css" var="ExternalCSS" />     
<link href="${ExternalCSS}" rel="stylesheet" />
<title>Insert title here</title>

</head>
<body>

<%
DecimalFormat df=new DecimalFormat("####################.##");
FormatConverter fc=new FormatConverter(); 
SimpleDateFormat sdf=fc.getRegularDateFormat();
SimpleDateFormat sdf1=fc.getSqlDateFormat(); 

List<Object[]> proList=(List<Object[]>)request.getAttribute("proList");

Object[] ProjectEditData=(Object[])request.getAttribute("ProjectEditData");
Object[] ProjectEditData1=(Object[])request.getAttribute("ProjectEditData1");
List<Object[]> ProjectAssignList=(List<Object[]>)request.getAttribute("ProjectAssignList");
List<Object[]> ProjectDetail=(List<Object[]>)request.getAttribute("ProjectDetails"); 
List<Object[]> riskdatalist=(List<Object[]>)request.getAttribute("riskdatalist");
List<String> projectidlist = (List<String>)request.getAttribute("projectidlist");
int ProjectAssignListsize=ProjectAssignList.size();
String Project=(String)request.getAttribute("ProjectId");
List<Object[]> MilestoneActivityList=(List<Object[]>)request.getAttribute("MilestoneActivityList");

%>

<div class="container-fluid">
		<div class="row" id="main">
			<div class="col-md-12">
				<div class="card shadow-nohover">
					<div class="row card-header">
			   			<div class="col-md-6 style1">
							<h3>Project Charter</h3>
						</div>	  
						<div class="col-md-6 style2">
							<form method="get" action="ProjectCharter.htm" id="projectchange" >
								<div class="row">
									<div class="col-md-4 right">
                            			<label class="control-label style3"><b>Project Name :</b></label>
                            		</div>
								 
									<div class="col-md-4 style4">
											
										<select class="form-control items selectdee style5" name="projectid"  required="required"data-live-search="true" data-container="body" onchange="this.form.submit();">
											<option disabled="true"  selected value="">Choose...</option>
											<%for(Object[] obj : proList){ 
												String projectshortName=(obj[17]!=null)?" ( "+obj[17].toString()+" ) ":""; %>
												<option  value="<%=obj[0] %>" <%if (Project.equalsIgnoreCase(obj[0].toString())) {%>
												selected="selected" <%}%>><%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - " %> <%=projectshortName!=null?StringEscapeUtils.escapeHtml4(projectshortName): " - "%></option>
											<%} %>
										</select>
									</div>
									
									<div class="col-md-2 left">
										<button  type="submit" class="btn btn-sm style6" formmethod="GET" formaction="ProjectCharterDownload.htm" formtarget="_blank">
											<i class="fa fa-lg fa-file-pdf-o" aria-hidden="true"></i>
										</button>
									</div>	
									
								</div>
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							</form>				
						</div>
				
						<div class="card-body style7">	
						
				 		
							 <details>					
							    <summary role="button" tabindex="0"><b>1. General Project Information </b>  </summary>
							
								<div class="content">
												
												<table class="subtables projectattributetable style8" >
											
										<tr>
											 <td class="style9">(a)</td>
											 <td class="style10"><b>Project Name</b></td>
											 <td colspan="4" class="style11"><%=ProjectEditData[1]!=null?StringEscapeUtils.escapeHtml4(ProjectEditData[1].toString()): " - " %></td>
											
										</tr>
										
										<tr>
											 <td  class="style12">(b)</td>
											 <td  class="style13"><b>Category</b></td>
											 <td colspan="4" class="style11"><%=ProjectEditData[14]!=null?StringEscapeUtils.escapeHtml4(ProjectEditData[14].toString()): " - " %></td>
										</tr>
										<tr>
											 <td class="style12">(c)</td>
											 <td  class="style10"><b>Date of Sanction</b></td>
											 <td colspan="4" class="style11"><%=sdf.format(sdf1.parse(ProjectEditData[3].toString()))%></td>
										</tr>
										<tr>
											 <td  class="style14">(d)</td>
											 <td  class="style13"><b>User</b></td>
											 <td colspan="4" class="style11"> <%if(ProjectEditData[16]!=null ){%> <%=StringEscapeUtils.escapeHtml4(ProjectEditData[16].toString()) %> <%} %></td>
										</tr>
										
									
										
									</table>
								
		
							</div>
						</details>
						
						
						 <details>					
							    <summary role="button" tabindex="0"><b>2.Project Team </b>  </summary>
								<div class="content">
								
								
									
					      <table class="subtables projectattributetable style8" >
					            <tr>
									<td class="style15">Role</td>
									<td class="style16"><b>Member</b></td>
									<td class="style17"><b> Lab</b></td>
									<td class="style18"><b> Telephone</b></td>
									<td class="style17"><b> Email</b></td>
								</tr>
										
										<% for(Object[]o:ProjectAssignList){%>
											<tr>
												<td class="style15"><%=o[12]!=null?StringEscapeUtils.escapeHtml4(o[12].toString()):"-" %></td>
												<td class="style19"><b><%=o[3]!=null?StringEscapeUtils.escapeHtml4(o[3].toString()): " - " %>, <%=o[4]!=null?StringEscapeUtils.escapeHtml4(o[4].toString()): " - " %></b></td>
												<td class="style20"><b><%=o[9]!=null?StringEscapeUtils.escapeHtml4(o[9].toString()):"-" %></b></td>
												<td class="style18"><b><%=o[6]!=null?StringEscapeUtils.escapeHtml4(o[6].toString()): " - " %> </b></td>
												<td class="style21"><b><%=o[7]!=null?StringEscapeUtils.escapeHtml4(o[7].toString()): " - " %> </b></td>
											</tr>
										<%} %>
									
									</table>
		
							</div>
						</details>
						
						
					<!-- StakeHolders -->
					<details>
   						<summary role="button" tabindex="0"><b>3. StakeHolders</b>     </summary>
   						
						  <div class="content">
						  <h6>No data avaiable</h6>
						  </div>
						</details>
						
						<details>
   						<summary role="button" tabindex="0"><b>4. Project Scope Statement</b>     </summary>
   						
						  <div class="content" >
						  
						    <table class="subtables projectattributetable style8" >
					               <tr>
											 <td class="style9">(a)</td>
											 <td class="style10"><b>Project Scope</b></td>
											 <td colspan="4" class="style11"><%=ProjectEditData[17]!=null?StringEscapeUtils.escapeHtml4(ProjectEditData[17].toString()): " - " %></td>
											
										</tr>
										
										<tr>
											 <td  class="style22">(b)</td>
											 <td  class="style13"><b>Objectives</b></td>
											 <td colspan="4" class="style11"><%=ProjectEditData[4]!=null?StringEscapeUtils.escapeHtml4(ProjectEditData[4].toString()): " - " %></td>
										</tr>
										
											<tr>
											 <td class="style22">(c)</td>
											 <td class="style13"><b>Deliverables</b></td>
											 <td colspan="4" class="style11"><%=ProjectEditData[5]!=null?StringEscapeUtils.escapeHtml4(ProjectEditData[5].toString()): " - " %></td>
										</tr>
						
		                      </table>
		                         <h5 class="style23"> Project Major MileStones</h5>
		                       <table class="subtables projectattributetable style8" >
										<tr>
											 <td class="style24">Mil-No</td>
											 <td class="style13"><b>Milestone Activity</b></td>
									
										</tr>
										<%if(MilestoneActivityList.size()>0){ %>
										<%for(Object[]o:MilestoneActivityList){ 
									
										%>
										<tr>
											 <td class="style24">Mil-<%=o[5]!=null?StringEscapeUtils.escapeHtml4(o[5].toString()): " - "%></td>
											 <td class="style13"><b><%=o[4]!=null?StringEscapeUtils.escapeHtml4(o[4].toString()): " - " %></b></td>
										
										</tr>
									
										<%}}else{ %>
										<td colspan="2" class="textcenter">No data found</td>
										<%} %>
										
										</table>
		                      
		                      <h5>Major Known Risk</h5>
		         
                   
                     
						
					
						    <table class="subtables projectattributetable style8" >
										<tr>
											 <td class="style9">Risk</td>
											 <td class="style13"><b>Risk Rating</b></td>
									
										</tr>
										<%if(riskdatalist.size()>0){ %>
									<%for(Object[]o:riskdatalist){ %>
										<tr>
											 <td class="style9"><%=o[1]!=null?StringEscapeUtils.escapeHtml4(o[1].toString()): " - " %></td>
											 <td class="style13"><b>Risk</b></td>
										</tr>
									<%}}else{ %>
									<td colspan="2" class="textcenter">No data found</td>
									<%} %>
									</div>
									</table>
						   <h6>Issues</h6>
						  <table class="subtables projectattributetable style8" >
										<tr>
											
											 <td class="style25"><b>Issues</b></td>
									
										</tr>
										
										<tr>
											 
											 <td class="style26"><b>No data Available</b></td>
									
										</tr>
										</table>
						   	</div>
						
						</details>
						
						<details>
   						<summary role="button" tabindex="0"><b>5. Communication Strategy (specify how the project manager will communicate to the Executive Sponsor, Project Team members and Stakeholders, e.g., frequency of status reports, frequency of Project Team meetings, etc.</b></summary>
   						
						  <div class="content" >
						  No data is available
						</div>
						</details>
						
						<details>
   						<summary role="button" tabindex="0"><b>	6.Sign-off</b>     </summary>
   						
						  <div class="content" >
						   No data is available
						  </div>
						  </details>
						  
						  <details>
   						<summary role="button" tabindex="0"><b>	7.Notes</b>     </summary>
   						
						  <div class="content" >
						   No data is available
						  </div>
						  </details>	
					
					</div>
					</div>
					</div>
					</div>
					</div>
					  
						
						  
						<script>
// JavaScript to open all details elements by default
document.addEventListener("DOMContentLoaded", function() {
  var detailsElements = document.querySelectorAll("details");
  detailsElements.forEach(function(details) {
    details.open = true;
  });
});
</script>
	
			
</body>
</html>