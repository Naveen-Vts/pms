<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.time.temporal.ChronoUnit"%>
<%@page import="java.time.LocalDate"%>
<%@page import="com.vts.pfms.model.LabMaster"%>
<%@page import="com.vts.pfms.projectclosure.model.ProjectClosure"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.vts.pfms.master.dto.ProjectFinancialDetails"%>
<%@page import="com.vts.pfms.project.model.ProjectMasterRev"%>
<%@page import="com.vts.pfms.IndianRupeeFormat"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="com.vts.pfms.projectclosure.model.ProjectClosureACPAchievements"%>
<%@page import="com.vts.pfms.projectclosure.model.ProjectClosureACPTrialResults"%>
<%@page import="com.vts.pfms.projectclosure.model.ProjectClosureACPConsultancies"%>
<%@page import="com.vts.pfms.projectclosure.model.ProjectClosureACPProjects"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="com.vts.pfms.projectclosure.model.ProjectClosureACP"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.List"%>
<%@page import="com.vts.pfms.project.model.ProjectMaster"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/projectdetails.css" var="projetdetailscss" />
<spring:url value="/resources/ckeditor/ckeditor.js" var="ckeditor" />
<spring:url value="/resources/ckeditor/contents.css" var="contentCss" />
<script src="${ckeditor}"></script>
<link href="${contentCss}" rel="stylesheet" />
<link href="${projetdetailscss}" rel="stylesheet" />
<spring:url value="/resources/css/projectModule/projectClosureACPDetails.css" var="ExternalCSS" />     
<link href="${ExternalCSS}" rel="stylesheet" />
</head>
<body>
<%
ProjectMaster projectMaster = (ProjectMaster)request.getAttribute("ProjectDetails");
ProjectClosure closure = (ProjectClosure)request.getAttribute("ProjectClosureDetails");
ProjectClosureACP acp = (ProjectClosureACP)request.getAttribute("ProjectClosureACPData");
LabMaster labMaster = (LabMaster)request.getAttribute("labMasterData");
List<ProjectMasterRev> projectMasterRevList = (List<ProjectMasterRev>)request.getAttribute("projectMasterRevList");

Object[] potherdetails = (Object[])request.getAttribute("ProjectOriginalRevDetails");
Object[] expndDetails = (Object[])request.getAttribute("ProjectExpenditureDetails");
List<ProjectClosureACPProjects> linkedprojectsdata = (List<ProjectClosureACPProjects>)request.getAttribute("ACPProjectsData");
List<ProjectClosureACPConsultancies> consultancies = (List<ProjectClosureACPConsultancies>)request.getAttribute("ACPConsultanciesData");
List<ProjectClosureACPTrialResults> trialresults = (List<ProjectClosureACPTrialResults>)request.getAttribute("ACPTrialResultsData");
List<ProjectClosureACPAchievements> achievements = (List<ProjectClosureACPAchievements>)request.getAttribute("ACPAchivementsData");

List<ProjectClosureACPProjects> subprojects = linkedprojectsdata!=null && linkedprojectsdata.size()>0 ? linkedprojectsdata.stream().filter(e -> e.getACPProjectType().equalsIgnoreCase("S")).collect(Collectors.toList()): new ArrayList<>();
List<ProjectClosureACPProjects> carscapsiprojects = linkedprojectsdata!=null && linkedprojectsdata.size()>0 ? linkedprojectsdata.stream().filter(e -> !e.getACPProjectType().equalsIgnoreCase("S")).collect(Collectors.toList()): new ArrayList<>();
/* List<ProjectClosureACPProjects> carsprojects = linkedprojectsdata!=null && linkedprojectsdata.size()>0 ? linkedprojectsdata.stream().filter(e -> e.getACPProjectType().equalsIgnoreCase("R")).collect(Collectors.toList()): new ArrayList<>();
List<ProjectClosureACPProjects> capsiprojects = linkedprojectsdata!=null && linkedprojectsdata.size()>0 ? linkedprojectsdata.stream().filter(e -> e.getACPProjectType().equalsIgnoreCase("P")).collect(Collectors.toList()): new ArrayList<>(); */

List<Object[]> acpApprovalEmpData = (List<Object[]>)request.getAttribute("ACPApprovalEmpData");
List<Object[]> acpRemarksHistory = (List<Object[]>)request.getAttribute("ACPRemarksHistory");

List<Object[]> labList = (List<Object[]>)request.getAttribute("LabList");

String acpTabId = (String)request.getAttribute("acpTabId");
String closureId = (String)request.getAttribute("closureId");
String isApproval = (String)request.getAttribute("isApproval");
String Details=(String)request.getAttribute("details");

List<String> acpforward = Arrays.asList("AIN","ARG","ARA","ARP","ARL","ARD","ARO","ARN","ARC","ARV");
List<String> acpapprove = Arrays.asList("AAP","AAL","AAD","AAO","AAN","AAC");

Object[] Director = (Object[])request.getAttribute("Director");
Object[] GDDPandC = (Object[])request.getAttribute("GDDPandC");
Object[] LAO = (Object[])request.getAttribute("LAO");
Object[] AD = (Object[])request.getAttribute("AD");
Object[] GD = (Object[])request.getAttribute("GDDetails");
Object[] PDData = (Object[])request.getAttribute("PDData");
Object[] emp = (Object[])request.getAttribute("EmpData");

String labcode = (String)session.getAttribute("labcode");

FormatConverter fc = new FormatConverter();
SimpleDateFormat sdtf = fc.getSqlDateAndTimeFormat();
SimpleDateFormat sdf = fc.getSqlDateFormat();
SimpleDateFormat rdf = fc.getRegularDateFormat();

DecimalFormat df = new DecimalFormat("#.####");
df.setMinimumFractionDigits(4); 

String statuscode = closure!=null?closure.getClosureStatusCode():null;

int isMain = projectMaster.getIsMainWC();
Double sanctionCost = projectMaster.getTotalSanctionCost();

List<String> projectidlist = (List<String>) request.getAttribute("projectidlist");
List<List<ProjectFinancialDetails>> projectFinancialDetails = (List<List<ProjectFinancialDetails>>) request.getAttribute("financialDetails");
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
       		<div class="card slider">
       			<!-- This is for Slider Headers -->
         		<div class="card-header slider_header style1">
             		<h4 class="category">Administrative Closure - <%if(projectMaster!=null) {%><%=projectMaster.getProjectShortName()!=null?StringEscapeUtils.escapeHtml4(projectMaster.getProjectShortName()): " - "%> (<%=projectMaster.getProjectCode()!=null?StringEscapeUtils.escapeHtml4(projectMaster.getProjectCode()): " - "%>) <%} %>

             			<a class="btn btn-info btn-sm  shadow-nohover back style2"
             				<%if(isApproval!=null && isApproval.equalsIgnoreCase("Y") ) {%>
               					href="ProjectClosureApprovals.htm"
               				<%} else if(isApproval!=null &&  isApproval.equalsIgnoreCase("N") ) {%>
               					href="ProjectClosureApprovals.htm?val=app"
               				<%} else{%> 
               					href="ProjectClosureList.htm"
               				<%} %>
             		
             			 >Back</a>
             		</h4>
             		<hr class="style3">
             		<ul class="nav nav-tabs justify-content-center style4" role="tablist">

            			<li class="nav-item" id="nav-acpdetails">
             				<%if(acpTabId!=null && acpTabId.equalsIgnoreCase("1")){ %> 
             		    		<a class="nav-link active " data-toggle="tab" href="#acpdetails" id="nav" role="tab">
             				<%}else{ %>
              			 		<a class="nav-link  " data-toggle="tab" href="#acpdetails" role="tab">
               				<%} %>  
               					
                	         	Closure Details
              			 		</a>
            			</li>

            			<li class="nav-item" id="nav-acpforward">
            	     		<%if(acpTabId!=null && acpTabId.equalsIgnoreCase("2")){ %>
              					<a class="nav-link active" data-toggle="tab" href="#acpforward" id="nav"role="tab" >
              				<%}else{ %>
              					<a class="nav-link" data-toggle="tab" href="#acpforward" role="tab" >
               				<%} %>
                  				Closure Forward
              					</a>
            			</li>
            			
              		</ul>
         		</div>
         		
         		<!-- This is for Tab Panes -->
         		<div class="card">
         			<div class="tab-content text-center style5">
         				<!-- *********** Administrative Closure Details ***********      --> 
               			<%if(acpTabId!=null && acpTabId.equalsIgnoreCase("1")){ %> 
         					<div class="tab-pane active" id="acpdetails" role="tabpanel">
         				<%}else{ %>
              				<div class="tab-pane " id="acpdetails" role="tabpanel">
               			<%} %>
               					<div class="container-fluid">
		        					<div class="row">
		            					<div class="col-md-12 details_container">
						        			<div class="tab-vertical">
						        				<!-- Side tabs headings  -->
						        				<ul class="nav nav-tabs" id="myTab3" role="tablist">
						            
						            				<%-- <li class="nav-item"> 
							                			<a class="nav-link <%if(Details!=null&&Details.equalsIgnoreCase("expenditure")){ %> active <%} %> <%if(Details==null){ %> active <%} %>" id="expenditure-vertical-tab" data-toggle="tab" href="#expenditure-vertical" role="tab" aria-controls="home" aria-selected="true">
							                				Expenditure &emsp; <img src="view/images/check.png" align="right">
							                			</a>
						                			</li> --%>
						                			
						                			<%-- <li class="nav-item">
						            					<a class="nav-link <%if(Details!=null&&Details.equalsIgnoreCase("aimobjectives")){ %> active <%} %>  <%if(Details==null){ %> active <%} %>" id="aimobjectives-vertical-tab" data-toggle="tab" href="#aimobjectives-vertical" role="tab" aria-controls="home" aria-selected="false">
						            						Aim & Objectives &emsp;<%if(acp!=null && acp.getACPAim()!=null && acp.getACPObjectives()!=null) {%> <img src="view/images/check.png" align="right"> <%} %>
						            					</a> 
													</li> --%>
													
													<%-- <li class="nav-item">
						            					<a class="nav-link <%if(Details!=null&&Details.equalsIgnoreCase("prototypes")){ %> active <%} %>  " id="prototypes-vertical-tab" data-toggle="tab" href="#prototypes-vertical" role="tab" aria-controls="contact" aria-selected="false">
						            						No of Prototypes <img src="view/images/check.png" align="right">
						            					</a> 
													</li> --%>
													
													<li class="nav-item">
						            					<a class="nav-link <%if((Details!=null&&Details.equalsIgnoreCase("subprojects")) || Details==null){ %> active <%} %>  " id="subprojects-vertical-tab" data-toggle="tab" href="#subprojects-vertical" role="tab" aria-controls="home" aria-selected="true">
						            						List of Sub-Projects &emsp;<%if(subprojects!=null && subprojects.size()>0) {%> <img src="view/images/check.png" align="right"> <%} %>
						            					</a> 
													</li>
													
													<li class="nav-item">
						            					<a class="nav-link <%if(Details!=null&&Details.equalsIgnoreCase("carscapsi")){ %> active <%} %>  " id="carscapsi-vertical-tab" data-toggle="tab" href="#carscapsi-vertical" role="tab" aria-controls="contact" aria-selected="false">
						            						List of CARS / CAPSI &emsp;<%if(carscapsiprojects!=null && carscapsiprojects.size()>0 ) {%> <img src="view/images/check.png" align="right"> <%} %>
						            					</a> 
													</li>
													
													<li class="nav-item">
						            					<a class="nav-link <%if(Details!=null&&Details.equalsIgnoreCase("consultancies")){ %> active <%} %>  " id="consultancies-vertical-tab" data-toggle="tab" href="#consultancies-vertical" role="tab" aria-controls="contact" aria-selected="false">
						            						List of Consultancies &emsp;<%if(consultancies!=null && consultancies.size()>0) {%> <img src="view/images/check.png" align="right"> <%} %>
						            					</a> 
													</li>
													
													<li class="nav-item">
						            					<a class="nav-link <%if(Details!=null&&Details.equalsIgnoreCase("facilitiescreated")){ %> active <%} %>  " id="facilitiescreated-vertical-tab" data-toggle="tab" href="#facilitiescreated-vertical" role="tab" aria-controls="contact" aria-selected="false">
						            						Facilities Created &emsp;<%if(acp!=null && acp.getFacilitiesCreated()!=null) {%> <img src="view/images/check.png" align="right"> <%} %>
						            					</a> 
													</li>
													
													<li class="nav-item">
						            					<a class="nav-link <%if(Details!=null&&Details.equalsIgnoreCase("trialresults")){ %> active <%} %>  " id="trialresults-vertical-tab" data-toggle="tab" href="#trialresults-vertical" role="tab" aria-controls="contact" aria-selected="false">
						            						Trial Results &emsp;<%if(trialresults!=null && trialresults.size()>0) {%> <img src="view/images/check.png" align="right"> <%} %>
						            					</a> 
													</li>
													
													<li class="nav-item">
						            					<a class="nav-link <%if(Details!=null&&Details.equalsIgnoreCase("achievements")){ %> active <%} %>  " id="achievements-vertical-tab" data-toggle="tab" href="#achievements-vertical" role="tab" aria-controls="contact" aria-selected="false">
						            						Achievements &emsp;<%if(achievements!=null && achievements.size()>0) {%> <img src="view/images/check.png" align="right"> <%} %>
						            					</a> 
													</li>
													
													<li class="nav-item">
						            					<a class="nav-link <%if(Details!=null&&Details.equalsIgnoreCase("recommendations")){ %> active <%} %>  " id="recommendations-vertical-tab" data-toggle="tab" href="#recommendations-vertical" role="tab" aria-controls="contact" aria-selected="false">
						            						Recommendations &emsp;<%if(acp!=null && acp.getMonitoringCommittee()!=null) {%> <img src="view/images/check.png" align="right"> <%} %>
						            					</a> 
													</li>
													
													<li class="nav-item">
						            					<a class="nav-link <%if(Details!=null&&Details.equalsIgnoreCase("others")){ %> active <%} %>  " id="others-vertical-tab" data-toggle="tab" href="#others-vertical" role="tab" aria-controls="contact" aria-selected="false">
						            						Others &emsp;<%if(acp!=null && acp.getTechReportNo()!=null){ %> <img src="view/images/check.png" align="right"> <%} %>
						            					</a> 
													</li>
						                		</ul>
						                		
						                		<!-- Side tab Details -->
						                		<div class="tab-content" id="myTabContent3">
						                			
						                			<!-- Sub-Projects Side bar Details -->
						                			<div class="tab-pane fade <%if((Details!=null&&Details.equalsIgnoreCase("subprojects")) || Details==null){ %> show active <%} %> " id="subprojects-vertical" role="tabpanel" aria-labelledby="subprojects-vertical-tab">
						                				
						                				<!-- Sub-Projects showing as Doc -->
						                				<div id="subprojectsdiv">
						                					<div class="row">
							                            		<div class="col-md-10" align="left">
							                            			<h4 class="style6"><b>List of Sub-Projects</b></h4>
							                            		</div>
			                            	
								                   				<div class="col-md-2 style7">
																	<button class="share-button style29" onclick="AllowEdit('subprojects')" id="" value="EDIT" >
														  				<span><i class="fa fa-pencil" aria-hidden="true"></i></span>
														  				<input type="submit"  class="edit-btn" name="sub" value="EDIT">
																	</button>
																</div>       			  	
				                            				</div>
				                            				
				                            				<hr>
				                            				
				                            				<table id="projectdatatable" class="style8" >
																<thead class="style9">
																	<tr>
																		<th class="style10">SN</th>
																    	<th class="style11">Projects Name</th>
																    	<th class="style12">Project No</th>
																    	<th class="style13">Agency</th>
																    	<th class="style12">Cost in Cr (&#8377;)</th>
																    	<th class="style11">Status</th>
																    	<th>Achievement</th>
																	</tr>
																</thead>
																<tbody>
																	<%if(subprojects!=null && subprojects.size()>0) {
																		int subprojectslno = 0;
																		for(ProjectClosureACPProjects sub :subprojects) {%>
																		<tr>
																			<td class="style14"><%=++subprojectslno %></td>
																			<td class="style11"><%=sub.getACPProjectName()!=null?StringEscapeUtils.escapeHtml4(sub.getACPProjectName()): " - " %> </td>
																			<td class="style15"><%=sub.getACPProjectNo()!=null?StringEscapeUtils.escapeHtml4(sub.getACPProjectNo()): " - " %> </td>
																			<td class="style13"><%=sub.getProjectAgency()!=null?StringEscapeUtils.escapeHtml4(sub.getProjectAgency()): " - " %> </td>
																			<td class="style16"><%=df.format(Double.parseDouble(sub.getProjectCost())/10000000) %> </td>
																			<td class="style11"><%=sub.getProjectStatus()!=null?StringEscapeUtils.escapeHtml4(sub.getProjectStatus()): " - " %> </td>
																			<td><%=sub.getProjectAchivements()!=null?StringEscapeUtils.escapeHtml4(sub.getProjectAchivements()): " - " %> </td>
																		</tr>
																	<%} }%>	
																</tbody>
															</table>		                
															
						                				</div>
						                				
						                				<!-- Sub-Projects Add / Edit -->
						                				<form action="ProjectClosureACPProjectDetailsSubmit.htm" method="POST" name="myform2" id="myform2">
						                					<div class="row">
							                					<div class="col-md-2" align="left">
								                            		<h4 class="style17">
								                            			<b>List of Sub-Projects : </b>
								                            		</h4>
								                            	</div>
								                            	<div class="col-md-2">
								                            		<div class="style18">
								                            			<input type="checkbox" class="form-control" id="subprojectscheck" checked>
								                            		</div>
								                            	</div>
								                            </div>
						                					<table class="style8" id="subprojectstable">
																<thead class="style19">
																	<tr>
																    	<th class="style20">Projects Name</th>
																    	<th class="style20">Project No</th>
																    	<th class="style20">Agency</th>
																    	<th class="style20">Cost (&#8377;)</th>
																    	<th class="style20">Status</th>
																    	<th class="style20">Achievement</th>
																		<td class="style10">
																			<button type="button" class=" btn btn_add_subprojects "> <i class="btn btn-sm fa fa-plus style21"></i></button>
																		</td>
																	</tr>
																</thead>
																<tbody>
																	<%if(subprojects!=null && subprojects.size()>0) {
																		for(ProjectClosureACPProjects sub :subprojects) {%>
																		<tr class="tr_clone_subprojects">
																			<td class="style22" >
																				<input type="text" class="form-control item" name="acpProjectName" value="<%if(sub.getACPProjectName()!=null) {%><%=StringEscapeUtils.escapeHtml4(sub.getACPProjectName()) %><%} %>" placeholder="Enter Sub-Project Name" maxlength="1000" required="required" >
																			</td>	
																			<td class="style22">
																				<input type="text" class="form-control item" name="acpProjectNo" value="<%if(sub.getACPProjectNo()!=null) {%><%=StringEscapeUtils.escapeHtml4(sub.getACPProjectNo()) %><%} %>" placeholder="Enter Sub-Project No." maxlength="200" required="required" >
																			</td>	
																			<td class="style22">
																				<input type="text" class="form-control item" name="projectAgency" value="<%if(sub.getProjectAgency()!=null) {%><%=StringEscapeUtils.escapeHtml4(sub.getProjectAgency()) %><%} %>" placeholder="Enter Agency Details" maxlength="1000" required="required" >
																			</td>	
																			<td class="style22">
																				<input type="text" class="form-control item decimal-format" name="projectCost" value="<%if(sub.getProjectCost()!=null) {%><%=StringEscapeUtils.escapeHtml4(sub.getProjectCost()) %><%} %>" placeholder="Enter Project Cost" maxlength="15" required="required" step="0.01" >
																			</td>
																			<td class="style22">
																				<input type="text" class="form-control item" name="projectStatus" value="<%if(sub.getProjectStatus()!=null) {%><%=StringEscapeUtils.escapeHtml4(sub.getProjectStatus()) %><%} %>" placeholder="Enter Project Status" maxlength="500" required="required" >
																			</td>
																			<td class="style22">
																				<input type="text" class="form-control item" name="projectAchivements" value="<%if(sub.getProjectAchivements()!=null) {%><%=StringEscapeUtils.escapeHtml4(sub.getProjectAchivements()) %><%} %>" placeholder="Enter Achievement Details" maxlength="2000" required="required" >
																			</td>
																			<td class="style10">
																				<button type="button" class=" btn btn_rem_subprojects" > <i class="btn btn-sm fa fa-minus style23"></i></button>
																			</td>									
																		</tr>
																	<%} } else {%>
																		<tr class="tr_clone_subprojects">
																			<td class="style22" >
																				<input type="text" class="form-control item" name="acpProjectName" maxlength="1000" placeholder="Enter Sub-Project Name" required="required" >
																			</td>	
																			<td class="style22">
																				<input type="text" class="form-control item" name="acpProjectNo" maxlength="200" placeholder="Enter Sub-Project No." required="required" >
																			</td>	
																			<td class="style22">
																				<input type="text" class="form-control item" name="projectAgency" maxlength="1000" placeholder="Enter Agency Details" required="required" >
																			</td>	
																			<td class="style22">
																				<input type="text" class="form-control item decimal-format" name="projectCost" maxlength="15" placeholder="Enter Project Cost" required="required" step="0.01" >
																			</td>
																			<td class="style22">
																				<input type="text" class="form-control item" name="projectStatus" maxlength="500" placeholder="Enter Project Status" required="required" >
																			</td>
																			<td class="style22">
																				<input type="text" class="form-control item" name="projectAchivements" maxlength="2000" placeholder="Enter Achievement Details" required="required" >
																			</td>
																			<td class="style10">
																				<button type="button" class=" btn btn_rem_subprojects " > <i class="btn btn-sm fa fa-minus style23"></i></button>
																			</td>	
																		</tr>
																	<%} %>
																</tbody>
															</table>
															<div align="center" class="style24">
																
																<%if(subprojects!=null && subprojects.size()>0) {%>
																	<button type="submit" class="btn btn-sm btn-warning edit mt-2 btn-acp btn-subproject" name="Action" value="Edit" onclick="return confirm('Are you sure to update?')">UPDATE</button>
																	
																	<button type="button" class="btn btn-sm style25" onclick="CloseEdit('subprojects')"
																	 formnovalidate="formnovalidate" data-toggle="tooltip" data-placement="top" title="Close">
																		<i class="fa fa-times fa-lg style26" aria-hidden="true"></i>
																	</button>
																	
																<%} else{%> 
																	<button type="submit" class="btn btn-sm submit btn-acp btn-subproject" name="Action" value="Add" onclick="return confirm('Are you sure to submit?')">SUBMIT</button>
																<%} %>
																<input type="hidden" name="closureId" value="<%=closureId%>">
																<input type="hidden" name="details" value="subprojects" />
																<input type="hidden" name="acpProjectTypeFlag" value="S" />
																<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
																
															</div>
						                				</form>
						                			</div>
						                			
						                			<!-- CARS / CAPSI Side bar Details -->
						                			<div class="tab-pane fade <%if(Details!=null&&Details.equalsIgnoreCase("carscapsi")){ %> show active <%} %> " id="carscapsi-vertical" role="tabpanel" aria-labelledby="carscapsi-vertical-tab">
						                				
						                				<!-- CARS / CAPSI showing as Doc -->
						                				<div id="carscapsidiv">
						                					<div class="row">
							                            		<div class="col-md-10" align="left">
							                            			<h4 class="style27"><b> List of CARS / CAPSI</b></h4>
							                            		</div>
			                            	
								                   				<div class="col-md-2 style28">
																	<button class="share-button style29" onclick="AllowEdit('carscapsi')" id="" value="EDIT" >
														  				<span><i class="fa fa-pencil" aria-hidden="true"></i></span>
														  				<input type="submit"  class="edit-btn" name="sub" value="EDIT">
																	</button>
																</div>       			  	
				                            				</div>
				                            				
				                            				<hr>
				                            				
				                            				<table id="projectdatatable" class="style30" >
																<thead class="style31">
																	<tr>
																		<th class="style10">SN</th>
																    	<th class="style10">CARS / CAPSI</th>
																    	<th class="style11">Name</th>
																    	<th class="style12">No</th>
																    	<th class="style13">Agency</th>
																    	<th class="style12">Cost in Cr (&#8377;)</th>
																    	<th class="style13">Status</th>
																    	<th>Achievement</th>
																	</tr>
																</thead>
																<tbody>
																	<%if(carscapsiprojects !=null && carscapsiprojects.size()>0) {
																		int carscapsiprojectslno = 0;
																		for(ProjectClosureACPProjects carscapsi :carscapsiprojects) {%>
																		<tr>
																			<td class="style14"><%=++carscapsiprojectslno %></td>
																			<td class="style10">
																				<%if(carscapsi.getACPProjectType()!=null && carscapsi.getACPProjectType().equalsIgnoreCase("R")) {%>
																					CARS
																				<%} else{%>
																					CAPSI
																				<%} %>
																			</td>
																			<td class="style11"><%=carscapsi.getACPProjectName()!=null?StringEscapeUtils.escapeHtml4(carscapsi.getACPProjectName()): " - " %> </td>
																			<td class="style15"><%=carscapsi.getACPProjectNo()!=null?StringEscapeUtils.escapeHtml4(carscapsi.getACPProjectNo()): " - " %> </td>
																			<td class="style13"><%=carscapsi.getProjectAgency()!=null?StringEscapeUtils.escapeHtml4(carscapsi.getProjectAgency()): " - " %> </td>
																			<td class="style16"><%=df.format(Double.parseDouble(carscapsi.getProjectCost()) / 10000000) %> </td>
																			<td class="style11"><%=carscapsi.getProjectStatus()!=null?StringEscapeUtils.escapeHtml4(carscapsi.getProjectStatus()): " - " %> </td>
																			<td><%=carscapsi.getProjectAchivements()!=null?StringEscapeUtils.escapeHtml4(carscapsi.getProjectAchivements()): " - " %> </td>
																		</tr>
																	<%} }%>	
																</tbody>
															</table>		                
															
						                				</div>
						                				
						                				<!-- CARS / CAPSI Add / Edit -->
						                				<form action="ProjectClosureACPProjectDetailsSubmit.htm" method="POST" name="myform3" id="myform3">
						                					<div class="row">
							                					<div class="col-md-2" align="left">
								                            		<h4 class="style27">
								                            			<b>List of CARS / CAPSI : </b>
								                            		</h4>
								                            	</div>
								                            	<div class="col-md-2">
								                            		<div class="style18">
								                            			<input type="checkbox" class="form-control" id="carscapsicheck" checked>
								                            		</div>
								                            	</div>
								                            </div>
						                					<table class="style8" id="carscapsitable">
																<thead class="style9">
																	<tr>
																    	<th class="style32">CARS / CAPSI</th>
																    	<th class="style32">Name</th>
																    	<th class="style32">No</th>
																    	<th class="style32">Agency</th>
																    	<th class="style32">Cost (&#8377;)</th>
																    	<th class="style32">Status</th>
																    	<th class="style32">Achievement</th>
																		<td class="style10">
																			<button type="button" class=" btn btn_add_carscapsi "> <i class="btn btn-sm fa fa-plus style21"></i></button>
																		</td>
																	</tr>
																</thead>
																<tbody>
																	<%if(carscapsiprojects!=null && carscapsiprojects.size()>0) {
																		for(ProjectClosureACPProjects carscapsi :carscapsiprojects) {%>
																		<tr class="tr_clone_carscapsi">
																			<td class="style33">
																				<select class="form-control" name="acpProjectType" required>
																					<option value="R" <%if(carscapsi.getACPProjectType()!=null && carscapsi.getACPProjectType().equalsIgnoreCase("R")) {%>selected<%} %> >CARS</option>
																					<option value="P" <%if(carscapsi.getACPProjectType()!=null && carscapsi.getACPProjectType().equalsIgnoreCase("P")) {%>selected<%} %> >CAPSI</option>
																				</select>
																			</td>
																			<td class="style33" >
																				<input type="text" class="form-control item" name="acpProjectName" value="<%if(carscapsi.getACPProjectName()!=null) {%><%=StringEscapeUtils.escapeHtml4(carscapsi.getACPProjectName()) %><%} %>" placeholder="Enter Project Name" maxlength="1000" required="required" >
																			</td>	
																			<td class="style33">
																				<input type="text" class="form-control item" name="acpProjectNo" value="<%if(carscapsi.getACPProjectNo()!=null) {%><%=StringEscapeUtils.escapeHtml4(carscapsi.getACPProjectNo()) %><%} %>" placeholder="Enter Project No." maxlength="200" required="required" >
																			</td>	
																			<td class="style33">
																				<input type="text" class="form-control item" name="projectAgency" value="<%if(carscapsi.getProjectAgency()!=null) {%><%=StringEscapeUtils.escapeHtml4(carscapsi.getProjectAgency()) %><%} %>" placeholder="Enter Agency Details" maxlength="1000" required="required" >
																			</td>	
																			<td class="style33">
																				<input type="number" class="form-control item decimal-format" name="projectCost" value="<%if(carscapsi.getProjectCost()!=null) {%><%=StringEscapeUtils.escapeHtml4(carscapsi.getProjectCost()) %><%} %>" placeholder="Enter Project Cost" maxlength="15" required="required" step="0.01" >
																			</td>
																			<td class="style33">
																				<input type="text" class="form-control item" name="projectStatus" value="<%if(carscapsi.getProjectStatus()!=null) {%><%=StringEscapeUtils.escapeHtml4(carscapsi.getProjectStatus()) %><%} %>" placeholder="Enter Project Status" maxlength="500" required="required" >
																			</td>
																			<td class="style33">
																				<input type="text" class="form-control item" name="projectAchivements" value="<%if(carscapsi.getProjectAchivements()!=null) {%><%=StringEscapeUtils.escapeHtml4(carscapsi.getProjectAchivements()) %><%} %>" placeholder="Enter Achievement Details" maxlength="2000" required="required" >
																			</td>
																			<td class="style10">
																				<button type="button" class=" btn btn_rem_carscapsi" > <i class="btn btn-sm fa fa-minus style23"></i></button>
																			</td>									
																		</tr>
																	<%} } else {%>
																		<tr class="tr_clone_carscapsi">
																			<td class="style33">
																				<select class="form-control" name="acpProjectType" required>
																					<option value="R" >CARS</option>
																					<option value="P" >CAPSI</option>
																				</select>
																			</td>
																			<td class="style33" >
																				<input type="text" class="form-control item" name="acpProjectName" maxlength="1000" placeholder="Enter Project Name" required="required" >
																			</td>	
																			<td class="style33">
																				<input type="text" class="form-control item" name="acpProjectNo" maxlength="200" placeholder="Enter Project No." required="required" >
																			</td>	
																			<td class="style33">
																				<input type="text" class="form-control item" name="projectAgency" maxlength="1000" placeholder="Enter Agency Details" required="required" >
																			</td>	
																			<td class="style33">
																				<input type="number" class="form-control item decimal-format" name="projectCost" maxlength="15" placeholder="Enter Project Cost" required="required" step="0.01" >
																			</td>
																			<td class="style33">
																				<input type="text" class="form-control item" name="projectStatus" maxlength="500" placeholder="Enter Project Status" required="required" >
																			</td>
																			<td class="style33">
																				<input type="text" class="form-control item" name="projectAchivements" maxlength="2000" placeholder="Enter Achievement Details" required="required" >
																			</td>
																			<td class="style10">
																				<button type="button" class=" btn btn_rem_carscapsi " > <i class="btn btn-sm fa fa-minus style23"></i></button>
																			</td>	
																		</tr>
																	<%} %>
																</tbody>
															</table>
															<div align="center" class="style24">
																
																<%if(carscapsiprojects!=null && carscapsiprojects.size()>0) {%>
																	<button type="submit" class="btn btn-sm btn-warning edit mt-2 btn-acp btn-carscapsi" name="Action" value="Edit" onclick="return confirm('Are you sure to update?')">UPDATE</button>
																	
																	<button type="button" class="btn btn-sm style25" onclick="CloseEdit('carscapsi')"
																	 formnovalidate="formnovalidate" data-toggle="tooltip" data-placement="top" title="Close">
																		<i class="fa fa-times fa-lg style26" aria-hidden="true"></i>
																	</button>
																	
																<%} else{%> 
																	<button type="submit" class="btn btn-sm submit btn-acp btn-carscapsi" name="Action" value="Add" onclick="return confirm('Are you sure to submit?')">SUBMIT</button>
																<%} %>
																<input type="hidden" name="closureId" value="<%=closureId%>">
																<input type="hidden" name="details" value="carscapsi" />
																<input type="hidden" name="acpProjectTypeFlag" value="O" />
																<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
																
															</div>
						                				</form>
						                			</div>
						                			
						                			<!-- Consultancies Side bar Details -->
						                			<div class="tab-pane fade <%if(Details!=null&&Details.equalsIgnoreCase("consultancies")){ %> show active <%} %> " id="consultancies-vertical" role="tabpanel" aria-labelledby="consultancies-vertical-tab">
						                				
						                				<!-- Consultancies showing as Doc -->
						                				<div id="consultanciesdiv">
						                					<div class="row">
							                            		<div class="col-md-10" align="left">
							                            			<h4 class="style27"><b> List of Consultancies</b></h4>
							                            		</div>
			                            	
								                   				<div class="col-md-2" class="style28">
																	<button class="share-button style29" onclick="AllowEdit('consultancies')" id="" value="EDIT" >
														  				<span><i class="fa fa-pencil" aria-hidden="true"></i></span>
														  				<input type="submit"  class="edit-btn" name="sub" value="EDIT">
																	</button>
																</div>       			  	
				                            				</div>
				                            				
				                            				<hr>
				                            				
				                            				<table id="projectdatatable" class="style30" >
																<thead class="style31">
																	<tr>
																		<th class="style10">SN</th>
																    	<th class="style34">Aim</th>
																    	<th class="style35">Agency</th>
																    	<th class="style11">Amount in Cr(&#8377;)</th>
																    	<th class="style12">Date</th>
																	</tr>
																</thead>
																<tbody>
																	<%if(consultancies !=null && consultancies.size()>0) {
																		int consultancieslno = 0;
																		for(ProjectClosureACPConsultancies consultancy :consultancies) {%>
																		<tr>
																			<td class="style14"><%=++consultancieslno %></td>
																			<td class="style34"><%=consultancy.getConsultancyAim()!=null?StringEscapeUtils.escapeHtml4(consultancy.getConsultancyAim()): " - " %> </td>
																			<td class="style35"><%=consultancy.getConsultancyAgency()!=null?StringEscapeUtils.escapeHtml4(consultancy.getConsultancyAgency()): " - " %> </td>
																			<td class="style36"><%=df.format(Double.parseDouble(consultancy.getConsultancyCost()) / 10000000) %> </td>
																			<td class="style15"><%=fc.SqlToRegularDate(consultancy.getConsultancyDate()) %> </td>
																		</tr>
																	<%} }%>	
																</tbody>
															</table>		                
															
						                				</div>
						                				
						                				<!-- Consultancies Add / Edit -->
						                				<form action="ProjectClosureACPConsultancyDetailsSubmit.htm" method="POST" name="myform4" id="myform4">
						                					<div class="row">
							                					<div class="col-md-2" align="left">
								                            		<h4 class="style27">
								                            			<b>List of Consultancies : </b>
								                            		</h4>
								                            	</div>
								                            	<div class="col-md-2">
								                            		<div class="style37">
								                            			<input type="checkbox" class="form-control" id="consultanciescheck" checked>
								                            		</div>
								                            	</div>
								                            </div>
						                					<table class="style30" id="consultanciestable">
																<thead class="style31">
																	<tr>
																    	<th class="style38">Aim</th>
																    	<th class="style39">Agency</th>
																    	<th class="style40">Amount (&#8377;)</th>
																    	<th class="style41">Date</th>
																		<td class="style10">
																			<button type="button" class=" btn btn_add_consultancies "> <i class="btn btn-sm fa fa-plus style21"></i></button>
																		</td>
																	</tr>
																</thead>
																<tbody>
																	<%if(consultancies !=null && consultancies.size()>0) {
																		for(ProjectClosureACPConsultancies consultancy :consultancies) {%>
																		<tr class="tr_clone_consultancies">
																			<td class="style42" >
																				<input type="text" class="form-control item" name="consultancyAim" value="<%if(consultancy.getConsultancyAim()!=null) {%><%=StringEscapeUtils.escapeHtml4(consultancy.getConsultancyAim()) %><%} %>" placeholder="Enter Consultancy Aim" maxlength="3000" required="required" >
																			</td>	
																			<td class="style43">
																				<input type="text" class="form-control item" name="consultancyAgency" value="<%if(consultancy.getConsultancyAgency()!=null) {%><%=StringEscapeUtils.escapeHtml4(consultancy.getConsultancyAgency()) %><%} %>" placeholder="Enter Consultancy Agency" maxlength="2000" required="required" >
																			</td>	
																			<td class="style44">
																				<input type="number" class="form-control item decimal-format" name="consultancyCost" value="<%if(consultancy.getConsultancyCost()!=null) {%><%=StringEscapeUtils.escapeHtml4(consultancy.getConsultancyCost()) %><%} %>" placeholder="Enter Consultancy Amount" maxlength="15" required="required" step="0.01" >
																			</td>
																			<td class="style45">
																				<input type="text" class="form-control item" name="consultancyDate" id="consultancyDate" value="<%if(consultancy.getConsultancyDate()!=null) {%><%=StringEscapeUtils.escapeHtml4(consultancy.getConsultancyDate()) %><%} %>" required="required" >
																			</td>	
																			<td class="style10">
																				<button type="button" class=" btn btn_rem_consultancies" > <i class="btn btn-sm fa fa-minus style23"></i></button>
																			</td>									
																		</tr>
																	<%} } else {%>
																		<tr class="tr_clone_consultancies">
																			<td class="style42" >
																				<input type="text" class="form-control item" name="consultancyAim" placeholder="Enter Consultancy Aim" maxlength="3000" required="required" >
																			</td>	
																			<td class="style43">
																				<input type="text" class="form-control item" name="consultancyAgency" placeholder="Enter Consultancy Agency" maxlength="2000" required="required" >
																			</td>	
																			<td class="style44">
																				<input type="number" class="form-control item decimal-format" name="consultancyCost" placeholder="Enter Consultancy Amount" maxlength="15" required="required" step="0.01" >
																			</td>
																			<td class="style45">
																				<input type="text" class="form-control item style46" name="consultancyDate" id="consultancyDate" readonly="readonly" required="required" >
																			</td>
																			<td class="style10">
																				<button type="button" class=" btn btn_rem_consultancies " > <i class="btn btn-sm fa fa-minus style23"></i></button>
																			</td>	
																		</tr>
																	<%} %>
																</tbody>
															</table>
															<div align="center" class="style24">
																
																<%if(consultancies !=null && consultancies.size()>0) {%>
																	<button type="submit" class="btn btn-sm btn-warning edit mt-2 btn-acp btn-consultancies" name="Action" value="Edit" onclick="return confirm('Are you sure to update?')">UPDATE</button>
																	
																	<button type="button" class="btn btn-sm style25" onclick="CloseEdit('consultancies')"
																	 formnovalidate="formnovalidate" data-toggle="tooltip" data-placement="top" title="Close">
																		<i class="fa fa-times fa-lg style47" aria-hidden="true"></i>
																	</button>
																	
																<%} else{%> 
																	<button type="submit" class="btn btn-sm submit btn-acp btn-consultancies" name="Action" value="Add" onclick="return confirm('Are you sure to submit?')">SUBMIT</button>
																<%} %>
																<input type="hidden" name="closureId" value="<%=closureId%>">
																<input type="hidden" name="details" value="consultancies" />
																<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
																
															</div>
						                				</form>
						                			</div>
						                			
						                			<!-- Facilities Created Side bar Details -->
						                			<div class="tab-pane fade <%if(Details!=null&&Details.equalsIgnoreCase("facilitiescreated")){ %> show active <%} %> " id="facilitiescreated-vertical" role="tabpanel" aria-labelledby="facilitiescreated-vertical-tab">
						                				
						                				<!-- Facilities Created showing as Doc -->
						                				<div id="facilitiescreateddiv">
						                					<div class="row">
							                            		<div class="col-md-10" align="left">
							                            			<h4 class="style27"><b>Details of Facilities Created</b></h4>
							                            		</div>
			                            	
								                   				<div class="col-md-2 style28">
																	<button class="share-button style29" onclick="AllowEdit('facilitiescreated')" id="" value="EDIT" >
														  				<span><i class="fa fa-pencil" aria-hidden="true"></i></span>
														  				<input type="submit"  class="edit-btn" name="sub" value="EDIT">
																	</button>
																</div>       			  	
				                            				</div>
															
															<hr>
															
															<div class="row">
																<div class="col-md-12" align="left">
																	<label class="style48">
																		<b class="style49">Facilities Created</b>
																	</label>
																</div>
															</div>
															
															<div class="row">
				                            					<div class="col-md-12" align="left">
 																	<p><%if(acp!=null && acp.getFacilitiesCreated()!=null) {%><%=StringEscapeUtils.escapeHtml4(acp.getFacilitiesCreated()) %> <%} %></p>
 																</div>
 															</div>				                
															
						                				</div>
						                				
						                				<!-- Facilities Created Add / Edit -->
					                					<form action="ProjectClosureACPDetailsSubmit.htm" method="POST" name="myform5" id="myform5" >	
															<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" />
															<input type="hidden" name="closureId" value="<%=closureId %>" />
															<div class="row"> 
																<div class="col-md-12"  >
																	<div class="card" >
																		
																		<div class="card-body">
																	    	
																	 		<div class="row style50" >
																	 			<label class="control-label">Details of Facilities Created: </label><span class="mandatory">*</span>
																				<div class="col-md-12 style51"  align="left" >
																					<div id="facilitiesnote" class="center"> </div>
																					<input type="hidden" id="facilitiescreatedhidden" value="<%if(acp!=null && acp.getFacilitiesCreated()!=null) {%><%=acp.getFacilitiesCreated() %><%}%>">
																				</div>
																	  			<textarea name="facilitiesCreated" class="style52"></textarea>
																				<input type="hidden" name="details" value="facilitiescreated"> 
																	 			
																	 		</div>  
																	 		<div class="row"  class="style53">
																	 			<div class="col-md-12">
																	 				<span class="style47">Note:-  </span><b class="style54">Editor works like Ms-Word/Ms-Excel, Need to use shortcuts key ( E.g.-Next line in table cells- Shift+Enter ) </b>
																	 			</div>
																	 		</div>	
																	 		
																	 	</div>
																	 		
																	 	<div class="form-group" align="center" >
																	 		<%if(acp!=null && acp.getFacilitiesCreated()!=null) {%>
																				<button type="submit" class="btn btn-sm btn-warning btn-sm edit btn-acp" name="Action" value="Edit" onclick="return confirm('Are you sure to Update?');" >UPDATE</button>
																				
																				<button type="button" class="btn btn-sm style55" onclick="CloseEdit('facilitiescreated')"
																				 formnovalidate="formnovalidate" data-toggle="tooltip" data-placement="top" title="Close">
																					<i class="fa fa-times fa-lg style47" aria-hidden="true"></i>
																				</button>
																					
																			<%} else{%>
																				<button type="submit" class="btn btn-sm btn-primary btn-sm submit btn-acp" name="Action" value="Add" onclick="return confirm('Are you sure to Submit?');" >SUBMIT</button>
																			<%} %>
																		</div>
																	</div>
																		
																</div>
															</div>
														</form>
														
						                			</div>
						                			
						                			<!-- Trial Results Side bar Details -->
						                			<div class="tab-pane fade <%if(Details!=null&&Details.equalsIgnoreCase("trialresults")){ %> show active <%} %> " id="trialresults-vertical" role="tabpanel" aria-labelledby="trialresults-vertical-tab">
						                				
						                				<!-- Trial Results showing as Doc -->
						                				<div id="trialresultsdiv">
						                					<div class="row">
							                            		<div class="col-md-10" align="left">
							                            			<h4 class="style27"><b>Trial Results</b></h4>
							                            		</div>
			                            	
								                   				<div class="col-md-2 style28">
																	<button class="share-button style29" onclick="AllowEdit('trialresults')" id="" value="EDIT" >
														  				<span><i class="fa fa-pencil" aria-hidden="true"></i></span>
														  				<input type="submit"  class="edit-btn" name="sub" value="EDIT">
																	</button>
																</div>       			  	
				                            				</div>
				                            				
				                            				<hr>
				                            				
				                            				<div class="row">
				                            					<div class="col-md-12" align="left">
 																	<label class="style56">
 																		<b class="style49">Brief</b>
 																	</label>
 																</div>
				                            				</div>
				                            				<div class="row">
				                            					<div class="col-md-12" align="left">
 																	<p>
 																		<%if(acp!=null && acp.getTrialResults()!=null) {%>
 																			<%=StringEscapeUtils.escapeHtml4(acp.getTrialResults()).replaceAll("\n", "<br>") %> 
 																		<%} %>
 																	</p>
 																</div>
 															</div>
															
															<hr>
															
															<div class="row">
				                            					<div class="col-md-12" align="left">
 																	<label class="style56">
 																		<b class="style49">Attachments</b>
 																	</label>
 																</div>
				                            				</div>
															<table id="projectdatatable" class="style30" >
																<thead class="style9">
																	<tr>
																		<th class="style10">SN</th>
																    	<th class="style57">Description</th>
																    	<th class="style11">Action</th>
																	</tr>
																</thead>
																<tbody>
																	<%if(trialresults!=null && trialresults.size()>0) {
																		int trialresultsslno = 0;
																		for(ProjectClosureACPTrialResults results :trialresults) {%>
																		<tr>
																			<td class="style14"><%=++trialresultsslno %></td>
																			
																			<td class="style57"><%=results.getDescription()!=null?StringEscapeUtils.escapeHtml4(results.getDescription()): " - " %> </td>
																			<td class="style58">
																				<%if(results.getAttachment()!=null && !results.getAttachment().isEmpty()) {%>
																					<button type="submit" form="myform6" class="btn btn-sm style59"  id="attachedfile" name="attachmentfile" formmethod="post" formnovalidate="formnovalidate"
                            					 		  				 			 value="<%=results.getTrialResultsId() %>" formaction="ProjectClosureACPTrialResultsFileDownload.htm" formtarget="_blank" data-toggle="tooltip" data-placement="top" title="Attatchment Download">
                            					 										<i class="fa fa-download fa-lg"></i>
                            					 									</button>
																				<%} %>
																			</td>
																		</tr>
																	<%} }%>	
																</tbody>
															</table>		                
															
						                				</div>
						                				
						                				<!-- Trial Results Add / Edit -->
					                					<form action="ProjectClosureACPTrialResultsSubmit.htm" method="POST" name="myform6" id="myform6" enctype="multipart/form-data">	
															<input type="hidden" name="closureId" value="<%=closureId%>">
																<input type="hidden" name="details" value="trialresults" />
																<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
															<div class="row"> 
																<div class="col-md-12"  >
																	<div class="card" >
																		<div class="card-body">
																			<div class="row style60">
																	        	<div class="col-md-12">
																	        		<div class="form-group">
																	                	<label class="control-label">Brief:</label><span class="mandatory">*</span>
																	                    <textarea class="form-control form-control style61" name="trialResults" maxlength="5000" rows="3" cols="65" 
					                              		  								 placeholder="Enter maximum 5000 charecters" required><%if(acp!=null && acp.getTrialResults()!=null){ %><%=acp.getTrialResults()!=null?acp.getTrialResults(): " - " %><%} %></textarea> 
																	                </div>
																	            </div>
											       	 						</div>
																		</div>
																		<div class="card-body">
																	    	<div class="row style50">
																	 			<label class="control-label">Attachments: </label>
																	 		</div>
																	 		
																	 		<table class="style62" id="trialresultstable">
																				<thead class="style31">
																					<tr>
																				    	<th class="style38">Description</th>
																				    	<th class="style39">Attachment</th>
																				    	<%if(acp!=null && acp.getTrialResults()!=null) {%>
																				    	<th class="style41">Action</th>
																						<%} %>
																						<td class="style10">
																							<button type="button" class=" btn btn_add_trialresults "> <i class="btn btn-sm fa fa-plus style21"></i></button>
																						</td>
																					</tr>
																				</thead>
																				<tbody>
																					<%if(trialresults !=null && trialresults.size()>0) {
																						for(ProjectClosureACPTrialResults results :trialresults) {%>
																						<tr class="tr_clone_trialresults">
																							<td class="style42" >
																								<input type="text" class="form-control item" name="description" value="<%if(results.getDescription()!=null) {%><%=StringEscapeUtils.escapeHtml4(results.getDescription()) %><%} %>" placeholder="Enter maximum 2000 charecters" maxlength="2000" >
																							</td>	
																							<td class="style43">
																								<input type="file" class="form-control item" name="attachment" accept=".pdf">
																								<input type="hidden" name="attatchmentname" value="<%if(results.getAttachment()!=null && !results.getAttachment().isEmpty()) {%><%=results.getAttachment() %><%} %>">
																							</td>
																							<td class="style44" id="actiontd">
																								<%if(results.getAttachment()!=null && !results.getAttachment().isEmpty()) {%>
																									<button type="submit" class="btn btn-sm style59" id="attachedfile" name="attachmentfile" formmethod="post" formnovalidate="formnovalidate"
                            					 		  				 							 value="<%=results.getTrialResultsId() %>" formaction="ProjectClosureACPTrialResultsFileDownload.htm" formtarget="_blank" data-toggle="tooltip" data-placement="top" title="Attatchment Download">
                            					 														<i class="fa fa-download fa-lg"></i>
                            					 													</button>
																								<%} %>
																							</td>	
																							<td class="style10">
																								<button type="button" class=" btn btn_rem_trialresults " > <i class="btn btn-sm fa fa-minus style23"></i></button>
																							</td>									
																						</tr>
																					<%} } else {%>
																						<tr class="tr_clone_trialresults">
																							<td class="style42" >
																								<input type="text" class="form-control item" name="description" placeholder="Enter maximum 2000 charecters" maxlength="2000" >
																							</td>	
																							<td class="style43">
																								<input type="file" class="form-control item" name="attachment" accept=".pdf" >
																							</td>	
																							<td class="style10">
																								<button type="button" class=" btn btn_rem_trialresults " > <i class="btn btn-sm fa fa-minus style23"></i></button>
																							</td>	
																						</tr>
																					<%} %>
																				</tbody>
																			</table>	
																	 		
																	 	</div>
																	 		
																	 	<div class="form-group" align="center" >
																	 		<%if(acp!=null && acp.getTrialResults()!=null) {%>
																				<button type="submit" class="btn btn-sm btn-warning btn-sm edit btn-acp" name="Action" value="Edit" onclick="return confirm('Are you sure to Update?');" >UPDATE</button>
																				
																				<button type="button" class="btn btn-sm style55" onclick="CloseEdit('trialresults')"
																				 formnovalidate="formnovalidate" data-toggle="tooltip" data-placement="top" title="Close">
																					<i class="fa fa-times fa-lg style47" aria-hidden="true"></i>
																				</button>
																					
																			<%} else{%>
																				<button type="submit" class="btn btn-sm btn-primary btn-sm submit btn-acp" name="Action" value="Add" onclick="return confirm('Are you sure to Submit?');" >SUBMIT</button>
																			<%} %>
																		</div>
																	</div>
																		
																</div>
															</div>
														</form>
						                				
						                			</div>
						                			
						                			<!-- Achievements Side bar Details -->
						                			<div class="tab-pane fade <%if(Details!=null&&Details.equalsIgnoreCase("achievements")){ %> show active <%} %> " id="achievements-vertical" role="tabpanel" aria-labelledby="achievements-vertical-tab">
						                				
						                				<!-- Achievements showing as Doc -->
						                				<div id="achievementsdiv">
						                					<div class="row">
							                            		<div class="col-md-10" align="left">
							                            			<h4 class="style27"><b>Achievements (based on Aim & Objectives)</b></h4>
							                            		</div>
			                            	
								                   				<div class="col-md-2 style28">
																	<button class="share-button style29" onclick="AllowEdit('achievements')" id="" value="EDIT" >
														  				<span><i class="fa fa-pencil" aria-hidden="true"></i></span>
														  				<input type="submit"  class="edit-btn" name="sub" value="EDIT">
																	</button>
																</div>       			  	
				                            				</div>
				                            				
				                            				<hr>
				                            				
															<div class="row">
				                            					<div class="col-md-12" align="left">
 																	<label class="style63">
 																		<b class="style49">Achievements</b>
 																	</label>
 																</div>
				                            				</div>
															<table id="projectdatatable" class="style30" >
																<thead class="style31">
																	<tr>
																		<th class="style10">SN</th>
																    	<th class="style34">Targets as Envisaged</th>
																    	<th class="style34">Targets as Achieved</th>
																    	<th class="style64">Remarks</th>
																	</tr>
																</thead>
																<tbody>
																	<%if(achievements!=null && achievements.size()>0) {
																		int achievementsslno = 0;
																		for(ProjectClosureACPAchievements achieve :achievements) {%>
																		<tr>
																			<td class="style14"><%=++achievementsslno %></td>
																			<td class="style34"><%=achieve.getEnvisaged()!=null?StringEscapeUtils.escapeHtml4(achieve.getEnvisaged()): " - " %> </td>
																			<td class="style34"><%=achieve.getAchieved()!=null?StringEscapeUtils.escapeHtml4(achieve.getAchieved()): " - " %> </td>
																			<td class="style43"><%=achieve.getRemarks()!=null?StringEscapeUtils.escapeHtml4(achieve.getRemarks()): " - " %> </td>
																		</tr>
																	<%} }%>	
																</tbody>
															</table>		                
															
						                				</div>
						                				
						                				<!-- Achievements Add / Edit -->
						                				<form action="ProjectClosureACPAchievementDetailsSubmit.htm" method="POST" name="myform7" id="myform7">
						                					<table class="style30" id="achievementstable">
																<thead class="style31">
																	<tr>
																    	<th class="style65">Targets as Envisaged</th>
																    	<th class="style65">Targets as Achieved</th>
																    	<th class="style39">Remarks</th>
																		<td class="style10">
																			<button type="button" class=" btn btn_add_achievements "> <i class="btn btn-sm fa fa-plus style21"></i></button>
																		</td>
																	</tr>
																</thead>
																<tbody>
																	<%if(achievements !=null && achievements.size()>0) {
																		for(ProjectClosureACPAchievements achieve :achievements) {%>
																		<tr class="tr_clone_achievements">
																			<td class="style42" >
																				<input type="text" class="form-control item" name="envisaged" value="<%if(achieve.getEnvisaged()!=null) {%><%=StringEscapeUtils.escapeHtml4(achieve.getEnvisaged()) %><%} %>" placeholder="Enter maximum 5000 characters" maxlength="5000" required="required" >
																			</td>	
																			<td class="style43">
																				<input type="text" class="form-control item" name="achieved" value="<%if(achieve.getAchieved()!=null) {%><%=StringEscapeUtils.escapeHtml4(achieve.getAchieved()) %><%} %>" placeholder="Enter maximum 5000 characters" maxlength="5000" required="required" >
																			</td>	
																			<td class="style45">
																				<input type="text" class="form-control item" name="remarks" value="<%if(achieve.getRemarks()!=null) {%><%=StringEscapeUtils.escapeHtml4(achieve.getRemarks()) %><%} %>" placeholder="Enter maximum 2000 characters" >
																			</td>	
																			<td class="style10">
																				<button type="button" class=" btn btn_rem_achievements" > <i class="btn btn-sm fa fa-minus style23"></i></button>
																			</td>									
																		</tr>
																	<%} } else {%>
																		<tr class="tr_clone_achievements">
																			<td class="style42" >
																				<input type="text" class="form-control item" name="envisaged" placeholder="Enter maximum 5000 characters" maxlength="5000" required="required" >
																			</td>	
																			<td class="style43">
																				<input type="text" class="form-control item" name="achieved" placeholder="Enter maximum 5000 characters" maxlength="5000" required="required" >
																			</td>	
																			<td class="style45">
																				<input type="text" class="form-control item" name="remarks" placeholder="Enter maximum 2000 characters">
																			</td>
																			<td class="style10">
																				<button type="button" class=" btn btn_rem_achievements " > <i class="btn btn-sm fa fa-minus style23" ></i></button>
																			</td>	
																		</tr>
																	<%} %>
																</tbody>
															</table>
															<div align="center" class="style24">
																
																<%if(achievements !=null && achievements.size()>0) {%>
																	<button type="submit" class="btn btn-sm btn-warning edit mt-2 btn-acp" name="Action" value="Edit" onclick="return confirm('Are you sure to update?')">UPDATE</button>
																	
																	<button type="button" class="btn btn-sm style25" onclick="CloseEdit('achievements')"
																	 formnovalidate="formnovalidate" data-toggle="tooltip" data-placement="top" title="Close">
																		<i class="fa fa-times fa-lg style47" aria-hidden="true" ></i>
																	</button>
																	
																<%} else{%> 
																	<button type="submit" class="btn btn-sm submit btn-acp" name="Action" value="Add" onclick="return confirm('Are you sure to submit?')">SUBMIT</button>
																<%} %>
																<input type="hidden" name="closureId" value="<%=closureId%>">
																<input type="hidden" name="details" value="achievements" />
																<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
																
															</div>
						                				</form>
						                				
						                			</div>	
						                			
						                			<!-- Recommendations Side bar Details -->
						                			<div class="tab-pane fade <%if(Details!=null&&Details.equalsIgnoreCase("recommendations")){ %> show active <%} %> " id="recommendations-vertical" role="tabpanel" aria-labelledby="recommendations-vertical-tab">
						                				
						                				<!-- Recommendations showing as Doc -->
						                				<div id="recommendationsdiv">
						                					<div class="row">
							                            		<div class="col-md-10" align="left">
							                            			<h4 class="style27"><b>Recommendation of highest Monitoring Committee</b></h4>
							                            		</div>
			                            	
								                   				<div class="col-md-2 style28">
																	<button class="share-button style29" onclick="AllowEdit('recommendations')" id="" value="EDIT" >
														  				<span><i class="fa fa-pencil" aria-hidden="true"></i></span>
														  				<input type="submit"  class="edit-btn" name="sub" value="EDIT">
																	</button>
																</div>       			  	
				                            				</div>
				                            				
				                            				<hr>
				                            				
															<div class="row">
				                            					<div class="col-md-2" align="left">
 																	<label class="style63">
 																		<b class="style49">Minutes of Meeting</b>
 																	</label>
 																	<form action="#">
 																		<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" />
																		<input type="hidden" name="closureId" value="<%=closureId %>" />
 																		<%if(acp!=null && acp.getMonitoringCommitteeAttach()!=null){ %>
				                            					 			<button type="submit" class="btn btn-sm style59" name="filename" formmethod="post" formnovalidate="formnovalidate"
				                            					 		  	 value="monitoringcommitteefile" formaction="ProjectClosureACPFileDownload.htm" formtarget="_blank" data-toggle="tooltip" data-placement="top" title="Monitoring Committee Recommendations Download">
				                            					 				<i class="fa fa-download fa-lg"></i>
				                            					 			</button>
		                            					 				<%} %>
 																	</form>
 																</div>
				                            				</div>
				                            				
				                            				<hr>
				                            				
															<div class="row">
				                            					<div class="col-md-12" align="left">
 																	<label class="style63">
 																		<b class="style49">Recommendations</b>
 																	</label>
 																</div>
				                            				</div>
															<div class="row">
				                            					<div class="col-md-12" align="left">
 																	<p><%if(acp!=null && acp.getMonitoringCommittee()!=null) {%><%=StringEscapeUtils.escapeHtml4(acp.getMonitoringCommittee()) %> <%} %></p>
 																</div>
 															</div>
 															
						                				</div>
						                				
						                				<!-- Recommendations Add / Edit -->
					                					<form action="ProjectClosureACPDetailsSubmit.htm" method="POST" name="myform8" id="myform8" enctype="multipart/form-data" >	
															<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" />
															<input type="hidden" name="closureId" value="<%=closureId %>" />
															<div class="row"> 
																<div class="col-md-12"  >
																	<div class="card" >
																		
																		<div class="card-body">
																			<div class="row mt-2 style50" >
																				<div class="col-md-4">
																					<label class="control-label">Minutes of Meeting: </label><span class="mandatory">*</span>
																					<%if(acp!=null && acp.getMonitoringCommitteeAttach()!=null){ %>
				                            					 						<button type="submit" class="btn btn-sm style59" name="filename" formmethod="post" formnovalidate="formnovalidate"
				                            					 		  				 	value="monitoringcommitteefile" formaction="ProjectClosureACPFileDownload.htm" formtarget="_blank" data-toggle="tooltip" data-placement="top" title="Monitoring Committee Recommendations Download">
				                            					 							<i class="fa fa-download fa-lg"></i>
				                            					 						</button>
		                            					 							<%} %>
		                              		      									<input type="file" class="form-control modals" name="monitoringCommitteeAttach" <%if(acp==null || (acp!=null && acp.getMonitoringCommitteeAttach()==null)) {%>required<%} %> accept=".pdf">
																				</div>
																	 		</div>
																		</div>
																		<br>
																		<div class="card-body">
																	    	
																	 		<div class="row style50">
																	 			<label class="control-label style66">Recommendation of highest Monitoring Committee: </label><span class="mandatory">*</span>
																				<div class="col-md-12 style51"  align="left" >
																					<div id="recommendationsnote" class="center"> </div>
																					<input type="hidden" id="recommendationshidden" value="<%if(acp!=null && acp.getMonitoringCommittee()!=null) {%><%=acp.getMonitoringCommittee() %><%}%>">
																				</div>
																	  			<textarea name="monitoringCommittee" class="style52"></textarea>
																				<input type="hidden" name="details" value="recommendations"> 
																	 			
																	 		</div>  
																	 		<div class="row style53">
																	 			<div class="col-md-12">
																	 				<span class="style47">Note:-  </span><b class="style54">Editor works like Ms-Word/Ms-Excel, Need to use shortcuts key ( E.g.-Next line in table cells- Shift+Enter ) </b>
																	 			</div>
																	 		</div>	
																	 		
																	 	</div>
																	 		
																	 	<div class="form-group" align="center" >
																	 		<%if(acp!=null && acp.getMonitoringCommittee()!=null) {%>
																				<button type="submit" class="btn btn-sm btn-warning btn-sm edit btn-acp" name="Action" value="Edit" onclick="return confirm('Are you sure to Update?');" >UPDATE</button>
																				
																				<button type="button" class="btn btn-sm style55" onclick="CloseEdit('recommendations')"
																				 formnovalidate="formnovalidate" data-toggle="tooltip" data-placement="top" title="Close">
																					<i class="fa fa-times fa-lg style47" aria-hidden="true"></i>
																				</button>
																					
																			<%} else{%>
																				<button type="submit" class="btn btn-sm btn-primary btn-sm submit btn-acp" name="Action" value="Add" onclick="return confirm('Are you sure to Submit?');" >SUBMIT</button>
																			<%} %>
																		</div>
																	</div>
																		
																</div>
															</div>
														</form>
						                				
						                			</div>
						                			
						                			<!-- Others Side bar Details -->
						                			<div class="tab-pane fade <%if(Details!=null&&Details.equalsIgnoreCase("others")){ %> show active <%} %> " id="others-vertical" role="tabpanel" aria-labelledby="others-vertical-tab">
						                    			
						                    			<!-- Others showing as Doc -->
														<div id="othersdiv">
						                					<div class="row">
							                            		<div class="col-md-10" align="left">
							                            			<h4 class="style27"><b>Other Details</b></h4>
							                            		</div>
			                            	
								                   				<div class="col-md-2 style28">
																	<button class="share-button style29" onclick="AllowEdit('others')" id="" value="EDIT" >
														  				<span><i class="fa fa-pencil" aria-hidden="true"></i></span>
														  				<input type="submit"  class="edit-btn" name="sub" value="EDIT">
																	</button>
																</div>       			  	
				                            				</div>
				                            				<hr>
				                            				
				                            				<div class="row">
				                            					<div class="col-md-4" align="left">
				                            						<label class="style63">
				                            							<b class="style49">Prototype Details</b>
				                            						</label>
				                            					</div>
				                            				</div>
				                            				
				                            				<div class="row">
				                            					<div class="col-md-12" align="left">
				                            						No of Prototypes (type approved/qualified) deliverables as brought out in Govt. Letter: &emsp;<%if(acp!=null && acp.getPrototyes()!=0) {%><%=acp.getPrototyes() %> <%} else{%>0<%} %>
				                            					</div>
				                            				</div>
				                            				
				                            				<hr>
				                            				
															<div class="row">
				                            					<div class="col-md-12" align="left">
 																	<label class="style63">
 																		<b class="style49">Technical Report Details</b>
 																	</label>
 																</div>
				                            				</div>
															<div class="row">
				                            					<div class="col-md-12" align="left">
 																	Technical Report No.: &emsp;<%if(acp!=null && acp.getTechReportNo()!=null) {%><%=StringEscapeUtils.escapeHtml4(acp.getTechReportNo()) %><%} else{%>-<%} %>
 																</div>
 															</div>
				                            				
				                            				<hr>
				                            				
															<div class="row">
				                            					<div class="col-md-4" align="left">
 																	<label class="style63">
 																		<b class="style49">Certificate from Lab MMG/Store Section</b>
 																	</label>
 																	<form action="#">
 																		<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" />
																		<input type="hidden" name="closureId" value="<%=closureId %>" />
 																		<%if(acp!=null && acp.getCertificateFromLab()!=null){ %>
				                            					 			<button type="submit" class="btn btn-sm style59" name="filename" formmethod="post" formnovalidate="formnovalidate"
				                            					 		  	 value="certificatefromlabfile" formaction="ProjectClosureACPFileDownload.htm" formtarget="_blank" data-toggle="tooltip" data-placement="top" title="Certificate from Lab MMG/Store Section Download">
				                            					 				<i class="fa fa-download fa-lg"></i>
				                            					 			</button>
		                            					 				<%} %>
 																	</form>
 																</div>
				                            				</div>
				                            				
						                				</div>
						                    			
						                    			<!-- Others Add / Edit -->
						                    			<form action="ProjectClosureACPDetailsSubmit.htm" method="POST" name="myform9" id="myform9" enctype="multipart/form-data">
															<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
												    		<input type="hidden" name="closureId" value="<%=closureId%>">
												    		<input type="hidden" name="details" value="others">
												    		<div class="row style60">
													            <div class="col-md-2">
													            	<div class="form-group">
													                	<label class="control-label">No of Prototypes:</label><span class="mandatory">*</span>
													                    <input  class="form-control form-control" type="number" name="noofprototypes" placeholder="Enter No of Prototypes" maxlength="10"
													                     value="<%if(acp!=null && acp.getPrototyes()!=0) {%><%=acp.getPrototyes() %><%} %>" required> 
													                </div>
													            </div>
														            
													            <div class="col-md-2">
													            	<div class="form-group">
													                	<label class="control-label">Tech Report No:</label><span class="mandatory">*</span>
													                    <input  class="form-control form-control" type="text" name="techReportNo" placeholder="Enter Technical Report No" maxlength="100"
													                     value="<%if(acp!=null && acp.getTechReportNo()!=null) {%><%=StringEscapeUtils.escapeHtml4(acp.getTechReportNo()) %><%} %>" required> 
													                </div>
													            </div>
													            
													            <div class="col-md-4">
													            	<div class="form-group">
											                			<label class="control-label">Certificate from Lab MMG / Store Section:</label><span class="mandatory">*</span>
													                	<%if(acp!=null && acp.getCertificateFromLab()!=null){ %>
	                            					 						<button type="submit" class="btn btn-sm style59" name="filename" formmethod="post" formnovalidate="formnovalidate"
	                            					 		  				 	value="labcertificatefile" formaction="ProjectClosureACPFileDownload.htm" formtarget="_blank" data-toggle="tooltip" data-placement="top" title="Certifciate from Lab Download">
	                            					 							<i class="fa fa-download fa-lg"></i>
	                            					 						</button>
	                            					 					<%} %>
                             		      								<input type="file" class="form-control modals" name="labCertificateAttach" <%if(acp==null || (acp!=null && acp.getCertificateFromLab()==null)) {%>required<%} %> accept=".pdf">
											                		</div>
													            </div>
													            
												    		</div>
											    			<div align="center">
											    				<%if(acp!=null && acp.getTechReportNo()!=null){ %>
																	<button type="submit" class="btn btn-sm btn-warning edit btn-acp" name="Action" value="Edit" onclick="return confirm('Are you sure to update?')" >UPDATE</button>
																	
																	<button type="button" class="btn btn-sm style55" onclick="CloseEdit('others')"
																	 formnovalidate="formnovalidate" data-toggle="tooltip" data-placement="top" title="Close">
																		<i class="fa fa-times fa-lg style47" aria-hidden="true"></i>
																	</button>
																<%}else{ %>
																	<button type="submit" class="btn btn-sm btn-success submit btn-acp" name="Action" value="Add" onclick="return confirm('Are you sure to submit?')" >SUBMIT</button>
																<%} %>
											    			</div>
												    	</form>
                                			
													</div>
						                		</div>
						        			</div>
						        		</div>
						        	</div>
						        </div>
						        <div class="style67">
               						<div></div>
		               				<div class="navigation_btn style68">
		               					<%if( (achievements!=null && achievements.size()>0) && (acp!=null && acp.getFacilitiesCreated()!=null && acp.getMonitoringCommittee()!=null && acp.getTechReportNo()!=null) ){ %>
				               				<form action="#">
				               					<button type="submit" class="btn btn-sm style69" formaction="ProjectClosureACPDownload.htm" formtarget="_blank" formmethod="GET" data-toggle="tooltip" data-placement="top" title="Administrative closure Download" >
				               						Print Administrative Closure
				               					</button>
				               					<input type="hidden" name="closureId" value="<%=closureId%>">
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
				               				</form>
			               				<%} %>
									</div>
		               				<div class="navigation_btn style70" >
            							<a class="btn btn-info btn-sm  shadow-nohover back style71" href="ProjectClosureList.htm">Back</a>
										<button class="btn btn-info btn-sm next">Next</button>
									</div>
               					</div>			
               					
               			<%if(acpTabId!=null && acpTabId.equalsIgnoreCase("1")){ %> 
         					</div>
         				<%}else{ %>
              				</div>
               			<%} %>
               			
               			<!-- *********** Administrative Closure Forward ***********      --> 
               			<%if(acpTabId!=null && acpTabId.equalsIgnoreCase("2")){ %> 
         					<div class="tab-pane active" id="acpforward" role="tabpanel">
         				<%}else{ %>
              				<div class="tab-pane " id="acpforward" role="tabpanel">
               			<%} %>
               					<%if( (achievements!=null && achievements.size()>0) && (acp!=null && acp.getFacilitiesCreated()!=null && acp.getMonitoringCommittee()!=null && acp.getTechReportNo()!=null) ) {%>
               					
               						<div class="col-md-8 mt-2">
               							<div class="card style72  <%if(isApproval==null) {%>style73<%} else{%>style74<%} %>" >
               								<div class="card-body mt-2 ml-4">
               									<form action="#">
               										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                   									<input type="hidden" name="closureId" value="<%=closureId%>">
                   									<div class="mt-2" align="center">
			               								<h5 class="style75">
				               								AUDIT OF STATEMENT OF ACCOUNTS (EXPENDITURE) AND ADMINISTRATIVE CLOSURE OF  <%=projectMaster.getProjectShortName()!=null?StringEscapeUtils.escapeHtml4(projectMaster.getProjectShortName()): " - "%> (<%= projectMaster.getProjectCode()!=null?StringEscapeUtils.escapeHtml4(projectMaster.getProjectCode()): " - " %>)
			               								 </h5>
			               								 <h5 class="style76">Part - I</h5>
			               							</div>
			               							<%int slno=0; %>
    												<table id="tabledata">
												    	<tr>
												    		<td class="style77"><%=++slno %>.</td>
												    		<td class="style78">Name of Lab</td>
												    		<td>: <%=labMaster.getLabName()!=null?StringEscapeUtils.escapeHtml4(labMaster.getLabName()): " - "%> (<%= labMaster.getLabCode()!=null?StringEscapeUtils.escapeHtml4(labMaster.getLabCode()): " - " %>), <%= labMaster.getLabAddress()!=null?StringEscapeUtils.escapeHtml4(labMaster.getLabAddress()): " - "%> </td>
												    	</tr>
												    	<tr>
												    		<td class="style77"><%=++slno %>.</td>
												    		<td class="style78">Title of the Project</td>
												    		<td>: <%=projectMaster.getProjectName()!=null?StringEscapeUtils.escapeHtml4(projectMaster.getProjectName()): " - " %> </td>
												    	</tr>
												    	<tr>
												    		<td class="style77"><%=++slno %>.</td>
												    		<td class="style78">Date of Sanction</td>
												    		<td>: <%if(projectMaster!=null && projectMaster.getSanctionDate()!=null) {%><%=fc.SqlToRegularDate(projectMaster.getSanctionDate().toString()) %><%} else{%><%} %></td>
												    	</tr>
												    	<tr>
												    		<td class="style77"><%=++slno %>.</td>
												    		<td class="style78">Category of Project</td>
												    		<td>: <%if(potherdetails!=null && potherdetails[0]!=null) {%><%=StringEscapeUtils.escapeHtml4(potherdetails[0].toString()) %><%} %> </td>
												    	</tr>
												    	<tr>
												    		<td class="style77"><%=++slno %>.</td>
												    		<td colspan="1" class="style78">Cost in Cr (original & revised)</td>
												    		<td>:</td>
												    	</tr>
												    	<tr>
												    		<td class="style77"></td>
												    		<td colspan="">
												    			<table class="style30" id="projectdatatablep" >
																	<thead class="style68">
																		<tr>
																	    	<th class="style11">Cost (&#8377;)</th>
																	    	<th class="style79">Original</th>
																	    	<th class="style79">Revised</th>
																		</tr>
																	</thead>
																	<tbody>
																		<tr>
																			<td>RE</td>
																			<td class="style70">
																				<%if(potherdetails!=null && potherdetails[1]!=null) {%>
																					<%=df.format(Double.parseDouble(potherdetails[1].toString())/10000000) %>
																				<%} else{%>
																					--
																				<%} %>
																			</td>
																			<td class="style70">
																				<%if(potherdetails!=null && potherdetails[4]!=null) {%>
																					<%=df.format(Double.parseDouble(potherdetails[4].toString())/10000000) %>
																				<%} else{%>
																					--
																				<%} %>
																			</td>
																		</tr>
																		<tr>
																			<td>FE</td>
																			<td class="style70">
																				<%if(potherdetails!=null && potherdetails[2]!=null) {%>
																					<%=df.format(Double.parseDouble(potherdetails[2].toString())/10000000) %>
																				<%} else{%>
																					--
																				<%} %>
																			</td>
																			<td class="style70">
																				<%if(potherdetails!=null && potherdetails[5]!=null) {%>
																					<%=df.format(Double.parseDouble(potherdetails[5].toString())/10000000) %>
																				<%} else{%>
																					--
																				<%} %>
																			</td>
																		</tr>
																		<tr>
																			<td>Total (FE)</td>
																			<td class="style70">
																				<%if(potherdetails!=null && potherdetails[3]!=null) {%>
																					<%=df.format(Double.parseDouble(potherdetails[3].toString())/10000000) %>
																				<%} else{%>
																					--
																				<%} %>
																			</td>
																			<td class="style70">
																				<%if(potherdetails!=null && potherdetails[6]!=null) {%>
																					<%=df.format(Double.parseDouble(potherdetails[6].toString())/10000000) %>
																				<%} else{%>
																					--
																				<%} %>
																			</td>
																		</tr>
																	</tbody>
																</table>
												    		</td>
												    		<td></td>
												    	</tr>
												    	<tr>
												    		<td class="style77"><%=++slno %>.</td>
												    		<td class="style78">PDC of the Project</td>
												    		<td>:</td>
												    	</tr>
												    	<tr>
												    		<td class="style77"></td>
												    		<td class="style79">
												    			<table id="projectdatatablep" class="style30" >
																	<thead class="style86">
																		<tr>
																	    	<th class="style35">Original</th>
																	    	<th class="style35">Revised</th>
																	    	<th class="style79">No of Revisions</th>
																		</tr>
																	</thead>
																	<tbody>
																		<%
																		String projectPDC = projectMaster.getPDC()+"";
																		LocalDate date1 = LocalDate.parse(projectPDC);
																		int rowspan = projectMasterRevList!=null && projectMasterRevList.size()>0?projectMasterRevList.size():1;
																		
																		%>
																		<%for(int i=0; i<rowspan; i++) {%>
																			<tr>
																				<%if(i==0) { %>
																					<td rowspan="<%=rowspan %>" class="style80">
																						<%if(potherdetails!=null && potherdetails[7]!=null) { 
																							projectPDC = potherdetails[7].toString(); %>
																							<%=fc.SqlToRegularDate(potherdetails[7].toString()) %>
																						<%} else{%>
																							--
																						<%} %>
																					</td>
																				<%} %>
																				
																				<td class="style81">
																					<%if(projectMasterRevList!=null && projectMasterRevList.size()>0) {
																						ProjectMasterRev rev = projectMasterRevList.get(i);
																					%>
																						<%=fc.SqlToRegularDate(rev.getPDC().toString())%>
																						
																						<%
																						LocalDate date2 = LocalDate.parse(rev.getPDC().toString());
																						// Ensure date1 is before date2
																				        if (date1.isAfter(date2)) {
																				            LocalDate temp = date1;
																				            date1 = date2;
																				            date2 = temp;
																				        }
																						
																						%>
																						
																						&nbsp;(<%=ChronoUnit.MONTHS.between(date1, date2) %> Months)
																						
																					<%} else{%>
																						--
																					<%} %>
																				</td>
																				
																				<%if(i==0) { %>
																					<td rowspan="<%=rowspan %>" class="style80">
																						<%if(potherdetails!=null && potherdetails[9]!=null) {%>
																							<%=StringEscapeUtils.escapeHtml4(potherdetails[9].toString()) %>
																						<%} %>
																					</td>
																				<%} %>
																			</tr>
																		<%} %>
																	</tbody>
																</table>
												    		</td>
												    		<td></td>
												    	</tr>
												    	<tr>
												    		<td class="style77"><%=++slno %>.</td>
												    		<td class="style78">Expenditure ( as on <%-- <%if(acp.getExpndAsOn()!=null) {%><%=fc.SqlToRegularDate(acp.getExpndAsOn()) %> <%} else{%>-<%} %> --%> )</td>
												    		<td>
												    			: Total(<span class="style82">&#x20B9;</span>)
												    				<span class="style83">
												    					<%-- <%if(acp.getTotalExpnd()!=null) {%><%=IndianRupeeFormat.getRupeeFormat(Double.parseDouble(acp.getTotalExpnd())) %> <%} %> --%>
												    					<%if(expndDetails!=null && expndDetails[0]!=null) {%>
												    						<%=df.format(Double.parseDouble(expndDetails[0].toString())/10000000 ) %> 
												    					<%} %>
												    				</span> Cr
												    			 (FE<span class="style83">
												    					<%-- <%if(acp.getTotalExpndFE()!=null) {%><%=IndianRupeeFormat.getRupeeFormat(Double.parseDouble(acp.getTotalExpndFE())) %> <%} %> --%>
												    					<%if(expndDetails!=null && expndDetails[1]!=null) {%>
												    						<%=df.format(Double.parseDouble(expndDetails[1].toString())/10000000 ) %> 
												    					<%} %>
												    				</span>) Cr	
												    		</td>
												    	</tr>
												    	<tr>
												    		<td class="style77"><%=++slno %>.</td>
												    		<td class="style78">Aim & Objectives</td>
												    		<td>: <%if(projectMaster.getObjective()!=null) {%><%=StringEscapeUtils.escapeHtml4(projectMaster.getObjective()) %><%} %> </td>
												    	</tr>
												    	<tr>
												    		<td class="style77"><%=++slno %>.</td>
												    		<td class="style78">No of Prototypes (type approved/qualified) deliverables as brought out in Govt. Letter</td>
												    		<td>: <%=acp.getPrototyes() %> </td>
												    	</tr>
												    	<tr>
												    		<td class="style77"><%=++slno %>.</td>
												    		<td class="style78">List of Sub-Projects</td>
												    		<td>: <%if( (subprojects==null) || (subprojects!=null && subprojects.size()==0)) { %>Nil<%} %></td>
												    	</tr>
												    	<tr>
												    		<td colspan="3">
												    			<%if(subprojects!=null && subprojects.size()>0) {%>
													    			<table id="projectdatatablep" class="style84" >
																		<thead class="style68">
																			<tr>
																				<th class="style10">SN</th>
																		    	<th class="style13">Projects Name</th>
																		    	<th class="style12">Project No</th>
																		    	<th class="style13">Agency</th>
																		    	<th class="style12">Cost in Cr (&#8377;)</th>
																		    	<th class="style11">Status</th>
																		    	<th>Achievement</th>
																			</tr>
																		</thead>
																		<tbody>
																			<%  int subprojectslno = 0;
																				for(ProjectClosureACPProjects sub :subprojects) {%>
																				<tr>
																					<td class="style14"><%=++subprojectslno %></td>
																					<td class="style13"><%=sub.getACPProjectName()!=null?StringEscapeUtils.escapeHtml4(sub.getACPProjectName()): " - " %> </td>
																					<td class="style15"><%=sub.getACPProjectNo()!=null?StringEscapeUtils.escapeHtml4(sub.getACPProjectNo()): " - " %> </td>
																					<td class="style13"><%=sub.getProjectAgency()!=null?StringEscapeUtils.escapeHtml4(sub.getProjectAgency()): " - " %> </td>
																					<td class="style16"><%=df.format(Double.parseDouble(sub.getProjectCost())/10000000) %> </td>
																					<td class="style11"><%=sub.getProjectStatus()!=null?StringEscapeUtils.escapeHtml4(sub.getProjectStatus()): " - " %> </td>
																					<td><%=sub.getProjectAchivements()!=null?StringEscapeUtils.escapeHtml4(sub.getProjectAchivements()): " - " %> </td>
																				</tr>
																			<%}%>	
																		</tbody>
																	</table>
																<%} %>
												    		</td>
												    	</tr>
												    	<tr>
												    		<td class="style77"><%=++slno %>.</td>
												    		<td class="style78">List of CARS / CAPSI</td>
												    		<td>: <%if( (carscapsiprojects==null) || (carscapsiprojects !=null && carscapsiprojects.size()==0)) { %>Nil<%} %></td>
												    	</tr>
												    	<tr>
												    		<td colspan="3">
												    			<%if(carscapsiprojects!=null && carscapsiprojects.size()>0) { %>
													    			<table id="projectdatatablep" class="style85" >
																		<thead class="style86">
																			<tr>
																				<th class="style10">SN</th>
																		    	<th class="style10">CARS / CAPSI</th>
																		    	<th class="style13">Name</th>
																		    	<th class="style12">No</th>
																		    	<th class="style13">Agency</th>
																		    	<th class="style12">Cost in Cr (&#8377;)</th>
																		    	<th class="style13">Status</th>
																		    	<th class="style13">Achievement</th>
																			</tr>
																		</thead>
																		<tbody>
																			<%  int carscapsiprojectslno = 0;
																				for(ProjectClosureACPProjects carscapsi :carscapsiprojects) {%>
																				<tr>
																					<td class="style14"><%=++carscapsiprojectslno %></td>
																					<td class="style10">
																						<%if(carscapsi.getACPProjectType()!=null && carscapsi.getACPProjectType().equalsIgnoreCase("R")) {%>
																							CARS
																						<%} else{%>
																							CAPSI
																						<%} %>
																					</td>
																					<td class="style13"><%=carscapsi.getACPProjectName()!=null?StringEscapeUtils.escapeHtml4(carscapsi.getACPProjectName()): " - " %> </td>
																					<td class="style15"><%=carscapsi.getACPProjectNo() !=null?StringEscapeUtils.escapeHtml4(carscapsi.getACPProjectNo()): " - "%> </td>
																					<td class=""><%=carscapsi.getProjectAgency()!=null?StringEscapeUtils.escapeHtml4(carscapsi.getProjectAgency()): " - " %> </td>
																					<td class="style16"><%=df.format(Double.parseDouble(carscapsi.getProjectCost())/10000000) %> </td>
																					<td class="style11"><%=carscapsi.getProjectStatus()!=null?StringEscapeUtils.escapeHtml4(carscapsi.getProjectStatus()): " - "%> </td>
																					<td class="style13"><%=carscapsi.getProjectAchivements()!=null?StringEscapeUtils.escapeHtml4(carscapsi.getProjectAchivements()): " - " %> </td>
																				</tr>
																			<%}%>	
																		</tbody>
																	</table>
																<%} %>
												    		</td>
												    	</tr>
												    	<tr>
												    		<td class="style77"><%=++slno %>.</td>
												    		<td class="style78">List of Consultancies</td>
												    		<td>: <%if( (consultancies==null) || (consultancies!=null && consultancies.size()==0)) { %>Nil<%} %></td>
												    	</tr>
												    	<tr>
												    		<td colspan="3">
												    			<%if( consultancies!=null && consultancies.size()>0) { %>
													    			<table id="projectdatatablep" class="style85" >
																		<thead class="style86">
																			<tr>
																				<th class="style10">SN</th>
																		    	<th class="style34">Aim</th>
																		    	<th class="style64">Agency</th>
																		    	<th class="style11">Amount in Cr (&#8377;)</th>
																		    	<th class="style12">Date</th>
																			</tr>
																		</thead>
																		<tbody>
																			<%  int consultancieslno = 0;
																				for(ProjectClosureACPConsultancies consultancy :consultancies) {%>
																				<tr>
																					<td class="style14"><%=++consultancieslno %></td>
																					<td class="style34"><%=consultancy.getConsultancyAim()!=null?StringEscapeUtils.escapeHtml4(consultancy.getConsultancyAim()): " - " %> </td>
																					<td class="style64"><%=consultancy.getConsultancyAgency()!=null?StringEscapeUtils.escapeHtml4(consultancy.getConsultancyAgency()): " - " %> </td>
																					<td class="style36"><%=df.format(Double.parseDouble(consultancy.getConsultancyCost())/10000000) %> </td>
																					<td class="style15"><%=fc.SqlToRegularDate(consultancy.getConsultancyDate()) %> </td>
																				</tr>
																			<%} %>	
																		</tbody>
																	</table>
																<%} %>	
												    		</td>
												    	</tr>
												    	<tr>
												    		<td class="style77"><%=++slno %>.</td>
												    		<td class="style78">Details of Facilities created (as proposed in the programme) </td>
												    		<td>: <span><%if(acp.getFacilitiesCreated()!=null) {%><%=StringEscapeUtils.escapeHtml4(acp.getFacilitiesCreated()) %><%} %></span></td>
												    	</tr>
												    	<%-- <tr>
												    		<td colspan="3">
												    			<span><%if(acp.getFacilitiesCreated()!=null) {%><%=acp.getFacilitiesCreated() %><%} %></span>
												    		</td>
												    	</tr> --%>
												    	<tr>
												    		<td class="style77"><%=++slno %>.</td>
												    		<td class="style78">Trial Results </td>
												    		<td>: </td>
												    	</tr>
												    	<tr>
												    		<td colspan="1"></td>
												    		<td colspan="2">
												    			<span><%if(acp.getTrialResults()!=null) {%><%=StringEscapeUtils.escapeHtml4(acp.getTrialResults()) %><%} %></span>
												    		</td>
												    	</tr>
												    	<tr>
												    		<td colspan="3">
												    			<table id="projectdatatablep" class="style85" >
																	<thead class="style86">
																		<tr>
																			<th class="style10">SN</th>
																	    	<th class="style87">Description</th>
																	    	<th class="style11">Action</th>
																		</tr>
																	</thead>
																	<tbody>
																		<%if(trialresults!=null && trialresults.size()>0) {
																			int trialresultsslno = 0;
																			for(ProjectClosureACPTrialResults results :trialresults) {%>
																			<tr>
																				<td class="style14"><%=++trialresultsslno %></td>
																				
																				<td class="style87"><%=results.getDescription()!=null?StringEscapeUtils.escapeHtml4(results.getDescription()): " - " %> </td>
																				<td class="style58">
																					<%if(results.getAttachment()!=null && !results.getAttachment().isEmpty()) {%>
																						<button type="submit" form="myform6" class="btn btn-sm style59" id="attachedfile" name="attachmentfile" formmethod="post" formnovalidate="formnovalidate"
	                            					 		  				 			 value="<%=results.getTrialResultsId() %>" formaction="ProjectClosureACPTrialResultsFileDownload.htm" formtarget="_blank" data-toggle="tooltip" data-placement="top" title="Attatchment Download">
	                            					 										<i class="fa fa-download fa-lg"></i>
	                            					 									</button>
																					<%} %>
																				</td>
																			</tr>
																		<%} }%>	
																	</tbody>
																</table>
												    		</td>
												    	</tr>
												    	<tr>
												    		<td class="style77"><%=++slno %>.</td>
												    		<td class="style78">Achievements</td>
												    		<td>: </td>
												    	</tr>
												    	<tr>
												    		<td colspan="3">
												    			<table id="projectdatatablep" class="style85" >
																	<thead class="style86">
																		<tr>
																			<th class="style10">SN</th>
																	    	<th class="style88">Targets as Envisaged</th>
																	    	<th class="style88">Targets as Achieved</th>
																	    	<th class="style64">Remarks</th>
																		</tr>
																	</thead>
																	<tbody>
																		<%if(achievements!=null && achievements.size()>0) {
																			int achievementsslno = 0;
																			for(ProjectClosureACPAchievements achieve :achievements) {%>
																			<tr>
																				<td class="style14"><%=++achievementsslno %></td>
																				<td class="style88"><%=achieve.getEnvisaged()!=null?StringEscapeUtils.escapeHtml4(achieve.getEnvisaged()): " - " %> </td>
																				<td class="style88"><%=achieve.getAchieved()!=null?StringEscapeUtils.escapeHtml4(achieve.getAchieved()): " - "%> </td>
																				<td class="style64"><%=achieve.getRemarks()!=null?StringEscapeUtils.escapeHtml4(achieve.getRemarks()): " - " %> </td>
																			</tr>
																		<%} }%>	
																	</tbody>
																</table>
												    		</td>
												    	</tr>
												    	<tr>
												    		<td class="style77"><%=++slno %>.</td>
												    		<td colspan="2" class="style78">Recommendation of highest Monitoring Committee Meeting for Administrative Closure of the Project</td>
												    	</tr>
												    	<tr>
												    		<td colspan="1"></td>
												    		<td colspan="2">
												    			<span>
												    				<%if(acp.getMonitoringCommittee()!=null) {%><%=acp.getMonitoringCommittee() %><%} %>
												    				<%if(acp!=null && acp.getMonitoringCommitteeAttach()!=null){ %>
					                            						<button type="submit" class="btn btn-sm style59"  name="filename" formmethod="post" formnovalidate="formnovalidate"
					                            					 	 value="monitoringcommitteefile" formaction="ProjectClosureACPFileDownload.htm" formtarget="_blank" data-toggle="tooltip" data-placement="top" title="Monitoring Committee Recommendations Download">
					                            					 		<i class="fa fa-download"></i>
					                            					 	</button>
		                            					 			<%} %>
												    			</span>
												    		</td>
												    	</tr>
												    	<tr>
												    		<td class="style77"><%=++slno %>.</td>
												    		<td colspan="2" class="style78">Certificate from Lab MMG/Store Section stating no outstanding commitment, no live supply order or contracts & warranty is enclosed. List of payments, to be made due to contractual obligation be enclosed. </td>
												    	</tr>
												    	<tr>
												    		<td colspan="1"></td>
												    		<td colspan="2">
												    			<%if(acp!=null && acp.getCertificateFromLab()!=null){ %>
	                            					 				<button type="submit" class="btn btn-sm style59" name="filename" formmethod="post" formnovalidate="formnovalidate"
	                            					 		  		 value="labcertificatefile" formaction="ProjectClosureACPFileDownload.htm" formtarget="_blank" data-toggle="tooltip" data-placement="top" title="Certifciate from Lab Download">
	                            					 					<i class="fa fa-download"></i>
	                            					 				</button>
	                            					 			<%} %>
												    		</td>
												    	</tr>
												    	<tr>
												    		<td class="style77"><%=++slno %>.</td>
												    		<td  colspan="2" class="style89">Certified that objectives set for the project have been met as per Technical Report No. <span class="style93"><%if(acp.getTechReportNo()!=null) {%><%= StringEscapeUtils.escapeHtml4(acp.getTechReportNo()) %><%} %></span></td>
												    	</tr>
												    </table>
												    
												    <br>
			               		   					
			               		   					<!-- Signature and timestamp of PD -->
			               		   					
													<div class="style90">
		               								 	<div class="style61">Project Director</div>
						               					<%for(Object[] apprInfo : acpApprovalEmpData){ %>
						   			   					<%if(apprInfo[8].toString().equalsIgnoreCase("AFW")){ %>
						   								<label class="style91"><%=apprInfo[2]!=null?StringEscapeUtils.escapeHtml4(apprInfo[2].toString()): " - "%></label>,<!-- <br> -->
						   								<label class="style92"><%=apprInfo[3]!=null?StringEscapeUtils.escapeHtml4(apprInfo[3].toString()): " - "%></label><br>
						   								<label class="style82">[Forwarded On:&nbsp; <%=fc.SqlToRegularDate(StringEscapeUtils.escapeHtml4(apprInfo[4].toString()).substring(0, 10))  +" "+apprInfo[4]!=null?StringEscapeUtils.escapeHtml4(apprInfo[4].toString()).substring(11,19):" - " %>]</label>
						   			    				<%break;}} %>  
							            			</div>
							            			
							            			<!-- Signature and timestamp of Recommending officers -->
							            			<%for(Object[] apprInfo : acpApprovalEmpData) {%>
							            			 	<div class="style94">
							            			 		<%if(apprInfo[8].toString().equalsIgnoreCase("AAG")){ %>
							            			 			<div class="style61"> Signature of GD</div>
								   								<label class="style91"><%=apprInfo[2]!=null?StringEscapeUtils.escapeHtml4(apprInfo[2].toString()): " - "%></label>,<!-- <br> -->
								   								<label class="style92"><%=apprInfo[3]!=null?StringEscapeUtils.escapeHtml4(apprInfo[3].toString()): " - "%></label><br>
								   								<label class="style82">[Recommended On:&nbsp; <%=fc.SqlToRegularDate(StringEscapeUtils.escapeHtml4(apprInfo[4].toString()).substring(0, 10))  +" "+apprInfo[4]!=null?StringEscapeUtils.escapeHtml4(apprInfo[4].toString()).substring(11,19):" - " %>]</label>
							   			    				<%} else if(apprInfo[8].toString().equalsIgnoreCase("AAA")) {%> 
							   			    					<div class="style61"> Signature of AD</div>
							   			    					<label class="style91"><%=apprInfo[2]!=null?StringEscapeUtils.escapeHtml4(apprInfo[2].toString()): " - "%></label>,<!-- <br> -->
								   								<label class="style92"><%=apprInfo[3]!=null?StringEscapeUtils.escapeHtml4(apprInfo[3].toString()): " - "%></label><br>
								   								<label class="style82">[Recommended On:&nbsp; <%=fc.SqlToRegularDate(StringEscapeUtils.escapeHtml4(apprInfo[4].toString()).substring(0, 10))  +" "+apprInfo[4]!=null?StringEscapeUtils.escapeHtml4(apprInfo[4].toString()).substring(11,19):" - " %>]</label>
							   			    				<%} else if(apprInfo[8].toString().equalsIgnoreCase("AAP")) {%> 
							   			    					<div class="style61"> Signature of GD-DP&C</div>
							   			    					<label class="style91"><%=apprInfo[2]%></label>,<!-- <br> -->
								   								<label class="style92"><%=apprInfo[3]%></label><br>
								   								<label class="style82">[Recommended On:&nbsp; <%=fc.SqlToRegularDate(StringEscapeUtils.escapeHtml4(apprInfo[4].toString()).substring(0, 10))  +" "+apprInfo[4].toString().substring(11,19) %>]</label>
							   			    				
							   			    				<%} %>
							            			 	</div>	
							            			 <%} %>
							            			
							            			<br>
							            			<hr class="style95">
							            			<br>
							            			
							            			<div class="" align="center">
			               								 <h5 class="style76">Part - II</h5>
			               								 <h5 class="style76">
				               								Statement of Accounts (Expenditure)
			               								 </h5>
			               							</div>
			               							
			               							<table id="tabledata">
												    	<tr>
												    		<td class="style77"><%=++slno %>.</td>
												    		<td colspan="2">
												    			It is certified that the project
												    			<%if(projectMaster!=null) {%>
												    				"<%if(projectMaster.getProjectName()!=null) {%><%=StringEscapeUtils.escapeHtml4(projectMaster.getProjectName()) %><%} %>
												    				(<%if(projectMaster.getProjectShortName()!=null) {%><%=StringEscapeUtils.escapeHtml4(projectMaster.getProjectShortName()) %>" <%} %>)
												    			
												    			 No. <%if(projectMaster.getSanctionNo()!=null) {%><%=StringEscapeUtils.escapeHtml4(projectMaster.getSanctionNo()) %> <%} %>
												    			 has incurred the expenditure of 
												    			 <%-- <%if(acp.getTotalExpnd()!=null) {%><%=IndianRupeeFormat.getRupeeFormat(Double.parseDouble(acp.getTotalExpnd())) %><%} %> --%>
												    			 <%if(expndDetails!=null && expndDetails[0]!=null) {%><%=df.format(Double.parseDouble(expndDetails[0].toString())/10000000 ) %> <%} %> Cr
												    			 including FE
												    			 <%-- <%if(acp.getTotalExpndFE()!=null) {%><%=IndianRupeeFormat.getRupeeFormat(Double.parseDouble(acp.getTotalExpndFE())) %><%} %> --%>
												    			 <%if(expndDetails!=null && expndDetails[1]!=null) {%><%=df.format(Double.parseDouble(expndDetails[1].toString())/10000000 ) %> <%} %> Cr
																 against the sanctioned cost of
																 <%if(projectMaster.getTotalSanctionCost()!=null) {%><%=df.format(projectMaster.getTotalSanctionCost()/10000000) %><%} %> Cr
																 including FE
																 <%if(projectMaster.getSanctionCostFE()!=null) {%><%=df.format(projectMaster.getSanctionCostFE()/10000000) %><%} %> Cr
																 as per the enclosed Audited Statement of Expenditure.
																<%} %>
																<br><br>
																<span class="style96">All the stores/equipment undertaken in the project has been accounted for.</span>
												    			
												    		</td>
												    	</tr>
			               							</table>
			               							<br>
			               		   					
			               		   					<!-- Signature and timestamp of Lab Accounts Officer -->
			               		   					
													<div class="style90">
		               								 	<div class="style61">
		               								 		(Lab Accounts Officer or equivalent) <br><br>
		               								 		<span>(Signature with name and office seal)</span>
		               								 	</div>
						               					<%for(Object[] apprInfo : acpApprovalEmpData){ %>
						   			   					<%if(apprInfo[8].toString().equalsIgnoreCase("AAL")){ %>
						   								<label class="style91"><%=apprInfo[2]!=null?StringEscapeUtils.escapeHtml4(apprInfo[2].toString()): " - "%></label>,<!-- <br> -->
						   								<label class="style92"><%=apprInfo[3]!=null?StringEscapeUtils.escapeHtml4(apprInfo[3].toString()): " - "%></label><br>
						   								<label class="style82">[Approved On:&nbsp; <%=fc.SqlToRegularDate(StringEscapeUtils.escapeHtml4(apprInfo[4].toString()).substring(0, 10))  +" "+apprInfo[4]!=null?StringEscapeUtils.escapeHtml4(apprInfo[4].toString()).substring(11,19):" - " %>]</label>
						   			    				<%break;}} %>  
							            			</div>
							            			
			               							<br>
							            			<hr class="style95">
							            			<br>
							            			
							            			<div class="" align="center">
			               								 <h5 class="style76">Part - III</h5>
			               								 <h5 class="style76">
				               								The Statement of Accounts (Expenditure) of the project has been audited and reconciled.
			               								 </h5>
			               							</div>
			               							
			               							<br><br><br>
			               							<div class="" align="center">
			               								<span>Audit Authorities</span> <br>
			               								<span>(Local Audit Officer / CDA R&D)</span> <br>
			               								<span>(Signature with name and office seal)</span>
			               							</div>
			               							
			               							<br>
							            			<hr class="style95">
							            			<br>
							            			
							            			<!-- Signature and timestamp of Approving officers -->
							            			<%for(Object[] apprInfo : acpApprovalEmpData) {%>
							            			 	<div class="style97">
							            			 		<%if(apprInfo[8].toString().equalsIgnoreCase("AAD")){ %>
							            			 			<div class="style61"> Signature of Director</div>
								   								<label class="style91"><%=apprInfo[2]!=null?StringEscapeUtils.escapeHtml4(apprInfo[2].toString()): " - "%></label>,<!-- <br> -->
								   								<label class="style92"><%=apprInfo[3]!=null?StringEscapeUtils.escapeHtml4(apprInfo[3].toString()): " - "%></label><br>
								   								<label class="style82">[Approved On:&nbsp; <%=fc.SqlToRegularDate(StringEscapeUtils.escapeHtml4(apprInfo[4].toString()).substring(0, 10))  +" "+apprInfo[4]!=null?StringEscapeUtils.escapeHtml4(apprInfo[4].toString()).substring(11,19):" - " %>]</label>
							   			    				<%} else if(apprInfo[8].toString().equalsIgnoreCase("AAO")) {%> 
							   			    					<div class="style61"> Signature of O/o DG (ECS)</div>
							   			    					<label class="style91"><%=apprInfo[2]!=null?StringEscapeUtils.escapeHtml4(apprInfo[2].toString()): " - "%></label>,<!-- <br> -->
								   								<label class="style92"><%=apprInfo[3]!=null?StringEscapeUtils.escapeHtml4(apprInfo[3].toString()): " - "%></label><br>
								   								<label class="style82">[Approved On:&nbsp; <%=fc.SqlToRegularDate(StringEscapeUtils.escapeHtml4(apprInfo[4].toString()).substring(0, 10)) %>]</label>
								   							<%} else if(apprInfo[8].toString().equalsIgnoreCase("AAN")) {%> 
							   			    					<div class="style61"> Signature of DG (Nodal)</div>
							   			    					<label class="style91"><%=apprInfo[2]!=null?StringEscapeUtils.escapeHtml4(apprInfo[2].toString()): " - "%></label>,<!-- <br> -->
								   								<label class="style92"><%=apprInfo[3]!=null?StringEscapeUtils.escapeHtml4(apprInfo[3].toString()): " - "%></label><br>
								   								<label class="style82">[Approved On:&nbsp; <%=fc.SqlToRegularDate(StringEscapeUtils.escapeHtml4(apprInfo[4].toString()).substring(0, 10)) %>]</label>	
							   			    				<%} else if(apprInfo[8].toString().equalsIgnoreCase("AAC")) {%> 
							   			    					<div class="style61"> Signature of CFA</div>
							   			    					<label class="style91"><%=apprInfo[2]!=null?StringEscapeUtils.escapeHtml4(apprInfo[2].toString()): " - "%></label>,<!-- <br> -->
								   								<label class="style92"><%=apprInfo[3]!=null?StringEscapeUtils.escapeHtml4(apprInfo[3].toString()): " - "%></label><br>
								   								<label class="style82">[Approved On:&nbsp; <%=fc.SqlToRegularDate(StringEscapeUtils.escapeHtml4(apprInfo[4].toString()).substring(0, 10)) %>]</label>
							   			    				
							   			    				<%} %>
							            			 	</div>	
							            			 <%} %>
							            			 
							            			 <br>
							            			 
							            			 <table id="tabledata">
												    	<tr>
												    		<td class="style77"><%=++slno %>.</td>
												    		<td colspan="2" class="style89">
												    			Expenditure Status in Cr
												    		</td>
												    	</tr>
												    	<tr>
												    		<td colspan="3">
												    			<table id="projectdatatablep" class="style85" >
																	<thead class="style86">
																		<tr>
																			<th colspan="2" class="style98">Head </th>
																			<th colspan="3" class="style99">Sanction </th>
																			<th colspan="3" class="style99">Expenditure </th>
																			<th colspan="3" class="style99">O/s Commitments </th>
																			<th colspan="3" class="style99">Balance </th>
																		</tr>
																		<tr>
																			<th class="style100">SN</th>
																			<th class="style101" width="10">Head</th>
																			<th>IC</th>
																			<th>FE</th>
																			<th>Total</th>
																			<th>IC</th>
																			<th>FE</th>
																			<th>Total</th>
																			<th>IC</th>
																			<th>FE</th>
																			<th>Total</th>
																			<th>IC</th>
																			<th>FE</th>
																			<th>Total</th>
																		</tr>
																	</thead>
																	<tbody>
																		<%
																		double totSanctionCost = 0, totReSanctionCostCap = 0, totFESanctionCostCap = 0, totReFESantionCostCap = 0, totReSanctionCostRev = 0, totFESanctionCostRev = 0, totReFESantionCostRev = 0;
																		double totExpenditure = 0, totREExpenditureCap = 0, totFEExpenditureCap = 0, totReFEExpenditureCap = 0, totREExpenditureRev = 0, totFEExpenditureRev = 0, totReFEExpenditureRev = 0 ;
																		double totCommitment = 0, totRECommitmentCap = 0, totFECommitmentCap = 0, totReFECommitmentCap = 0, totRECommitmentRev = 0, totFECommitmentRev= 0, totReFECommitmentRev = 0;
																		double totBalance = 0, btotalReCap = 0, btotalFeCap = 0, totReFeBalanceCap = 0, btotalReRev = 0, btotalFeRev = 0, totReFeBalanceRev = 0;
																		
																		int count = 1;
																		if (projectFinancialDetails != null && projectFinancialDetails.size() > 0 && projectFinancialDetails.get(0) != null) {
																			List <ProjectFinancialDetails> revenue = projectFinancialDetails.get(0).stream().filter(e -> e.getBudgetHeadId()==1).collect(Collectors.toList());
																			List <ProjectFinancialDetails> capital = projectFinancialDetails.get(0).stream().filter(e -> e.getBudgetHeadId()==2).collect(Collectors.toList());
																		%>
																		
																		<!-- Revenue -->
																		<%if(revenue!=null && capital.size()>0) {
																			int i=0;
																			for (ProjectFinancialDetails projectFinancialDetail : revenue) {
																		%>
																		<tr>
																			<td align="center" class="style102"><%=count++%></td>
																			<td><b><%=projectFinancialDetail.getBudgetHeadDescription()!=null?StringEscapeUtils.escapeHtml4(projectFinancialDetail.getBudgetHeadDescription()):" - "%></b></td>
																			<!-- Sanction Cost -->
																			<!-- IC -->
																			<td align="right" class="style70"><%=df.format(projectFinancialDetail.getReSanction())%></td>
																			<% totReSanctionCostRev += (projectFinancialDetail.getReSanction()); %>
																			<!-- FE -->
																			<td align="right" class="style70"><%=df.format(projectFinancialDetail.getFeSanction())%></td>
																			<% totFESanctionCostRev += (projectFinancialDetail.getFeSanction()); %>
																			<!-- Total -->
																			<td align="right" class="style70"><%=df.format(projectFinancialDetail.getReSanction() + projectFinancialDetail.getFeSanction()) %> </td>
																			<% totReFESantionCostRev +=projectFinancialDetail.getReSanction() + projectFinancialDetail.getFeSanction();%>
																			
																			<!-- Expenditure Cost -->
																			<!-- IC -->
																			<td align="right" class="style70"><%=df.format(projectFinancialDetail.getReExpenditure())%></td>
																			<% totREExpenditureRev += (projectFinancialDetail.getReExpenditure()); %>
																			<!-- FE -->
																			<td align="right" class="style70"><%=df.format(projectFinancialDetail.getFeExpenditure())%></td>
																			<% totFEExpenditureRev += (projectFinancialDetail.getFeExpenditure()); %>
																			<!-- Total -->
																			<td align="right" class="style70"><%=df.format(projectFinancialDetail.getReExpenditure() + projectFinancialDetail.getFeExpenditure())%></td>
																			<% totReFEExpenditureRev += (projectFinancialDetail.getReExpenditure() + projectFinancialDetail.getFeExpenditure()); %>
																			
																			<!-- O/s Commitments Cost -->
																			<!-- IC -->
																			<td align="right" class="style70"><%=df.format(projectFinancialDetail.getReOutCommitment())%></td>
																			<% totRECommitmentRev += (projectFinancialDetail.getReOutCommitment()); %>
																			<!-- FE -->
																			<td align="right" class="style70"><%=df.format(projectFinancialDetail.getFeOutCommitment() )%></td>
																			<% totFECommitmentRev += (projectFinancialDetail.getFeOutCommitment() ); %>
																			<!-- Total -->
																			<td align="right" class="style70"><%=df.format(projectFinancialDetail.getReOutCommitment() + projectFinancialDetail.getFeOutCommitment())%></td>
																			<% totReFECommitmentRev += (projectFinancialDetail.getReOutCommitment() + projectFinancialDetail.getFeOutCommitment()); %>
																			
																			<!-- Balance Cost -->
																			<!-- IC -->
																			<td align="right" class="style70"><%=df.format(projectFinancialDetail.getReBalance() + projectFinancialDetail.getReDipl())%></td>
																			<% btotalReRev += (projectFinancialDetail.getReBalance() + projectFinancialDetail.getReDipl()); %>
																			<!-- FE -->
																			<td align="right" class="style70"><%=df.format(projectFinancialDetail.getFeBalance() + projectFinancialDetail.getFeDipl())%></td>
																			<% btotalFeRev += (projectFinancialDetail.getFeBalance() + projectFinancialDetail.getFeDipl()); %>
																			<!-- Total -->
																			<td align="right" class="style70"><%=df.format((projectFinancialDetail.getReBalance() + projectFinancialDetail.getReDipl()) + (projectFinancialDetail.getFeBalance() + projectFinancialDetail.getFeDipl()) )%></td>
																			<% totReFeBalanceRev += ((projectFinancialDetail.getReBalance() + projectFinancialDetail.getReDipl()) + (projectFinancialDetail.getFeBalance() + projectFinancialDetail.getFeDipl()) ); %>
																		</tr>
																		<%}} %>
																		</tbody>
																		<tbody>
																			<tr>
																				<td colspan="2" class="style70"><b>Total (Revenue)</b></td>
																				<!-- Sanction Cost -->
																				<td align="right" class="style70"><%=df.format(totReSanctionCostRev)%></td>
																				<td align="right" class="style70"><%=df.format(totFESanctionCostRev)%></td>
																				<td align="right" class="style70"><%=df.format(totReFESantionCostRev)%></td>
																				<!-- Expenditure Cost -->
																				<td align="right" class="style70"><%=df.format(totREExpenditureRev)%></td>
																				<td align="right" class="style70"><%=df.format(totFEExpenditureRev)%></td>
																				<td align="right" class="style70"><%=df.format(totReFEExpenditureRev)%></td>
																				<!-- O/s Commitments Cost -->
																				<td align="right" class="style70"><%=df.format(totRECommitmentRev)%></td>
																				<td align="right" class="style70"><%=df.format(totFECommitmentRev)%></td>
																				<td align="right" class="style70"><%=df.format(totReFECommitmentRev)%></td>
																				<!-- Balance Cost -->
																				<td align="right" class="style70"><%=df.format(btotalReRev)%></td>
																				<td align="right" class="style70"><%=df.format(btotalFeRev)%></td>
																				<td align="right" class="style70"><%=df.format(totReFeBalanceRev)%></td>
																				
																			</tr>

																	</tbody>
																	<tbody>
																		<!-- Capital -->
																		<%if(capital!=null && capital.size()>0) {
																			int j=0;
																			for (ProjectFinancialDetails projectFinancialDetail : capital) {
																		%>
																		
																		<tr>
																			<td align="center" class="style102"><%=count++%></td>
																			<td><b><%=projectFinancialDetail.getBudgetHeadDescription()!=null?StringEscapeUtils.escapeHtml4(projectFinancialDetail.getBudgetHeadDescription()):" - "%></b></td>
																			<!-- Sanction Cost -->
																			<!-- IC -->
																			<td align="right" class="style70"><%=df.format(projectFinancialDetail.getReSanction())%></td>
																			<% totReSanctionCostCap += (projectFinancialDetail.getReSanction()); %>
																			<!-- FE -->
																			<td align="right" class="style70"><%=df.format(projectFinancialDetail.getFeSanction())%></td>
																			<% totFESanctionCostCap += (projectFinancialDetail.getFeSanction()); %>
																			<!-- Total -->
																			<td align="right" class="style70"><%=df.format(projectFinancialDetail.getReSanction() + projectFinancialDetail.getFeSanction()) %> </td>
																			<% totReFESantionCostCap +=projectFinancialDetail.getReSanction() + projectFinancialDetail.getFeSanction();%>
																			
																			<!-- Expenditure Cost -->
																			<!-- IC -->
																			<td align="right" class="style70"><%=df.format(projectFinancialDetail.getReExpenditure())%></td>
																			<% totREExpenditureCap += (projectFinancialDetail.getReExpenditure()); %>
																			<!-- FE -->
																			<td align="right" class="style70"><%=df.format(projectFinancialDetail.getFeExpenditure())%></td>
																			<% totFEExpenditureCap += (projectFinancialDetail.getFeExpenditure()); %>
																			<!-- Total -->
																			<td align="right" class="style70"><%=df.format(projectFinancialDetail.getReExpenditure() + projectFinancialDetail.getFeExpenditure())%></td>
																			<% totReFEExpenditureCap += (projectFinancialDetail.getReExpenditure() + projectFinancialDetail.getFeExpenditure()); %>
																			
																			<!-- O/s Commitments Cost -->
																			<!-- IC -->
																			<td align="right" class="style70"><%=df.format(projectFinancialDetail.getReOutCommitment())%></td>
																			<% totRECommitmentCap += (projectFinancialDetail.getReOutCommitment()); %>
																			<!-- FE -->
																			<td align="right" class="style70"><%=df.format(projectFinancialDetail.getFeOutCommitment() )%></td>
																			<% totFECommitmentCap += (projectFinancialDetail.getFeOutCommitment() ); %>
																			<!-- Total -->
																			<td align="right" class="style70"><%=df.format(projectFinancialDetail.getReOutCommitment() + projectFinancialDetail.getFeOutCommitment())%></td>
																			<% totReFECommitmentCap += (projectFinancialDetail.getReOutCommitment() + projectFinancialDetail.getFeOutCommitment()); %>
																			
																			<!-- Balance Cost -->
																			<!-- IC -->
																			<td align="right" class="style70"><%=df.format(projectFinancialDetail.getReBalance() + projectFinancialDetail.getReDipl())%></td>
																			<% btotalReCap += (projectFinancialDetail.getReBalance() + projectFinancialDetail.getReDipl()); %>
																			<!-- FE -->
																			<td align="right" class="style70"><%=df.format(projectFinancialDetail.getFeBalance() + projectFinancialDetail.getFeDipl())%></td>
																			<% btotalFeCap += (projectFinancialDetail.getFeBalance() + projectFinancialDetail.getFeDipl()); %>
																			<!-- Total -->
																			<td align="right" class="style70"><%=df.format((projectFinancialDetail.getReBalance() + projectFinancialDetail.getReDipl()) + (projectFinancialDetail.getFeBalance() + projectFinancialDetail.getFeDipl()) )%></td>
																			<% totReFeBalanceCap += ((projectFinancialDetail.getReBalance() + projectFinancialDetail.getReDipl()) + (projectFinancialDetail.getFeBalance() + projectFinancialDetail.getFeDipl()) ); %>
																		</tr>
																		<%}} %>
																		
																		<%}%>
																	</tbody>
																	<tbody>
																		<tr>
																			<td colspan="2" class="style70"><b>Total (Capital)</b></td>
																			<!-- Sanction Cost -->
																			<td align="right" class="style70"><%=df.format(totReSanctionCostCap)%></td>
																			<td align="right" class="style70"><%=df.format(totFESanctionCostCap)%></td>
																			<td align="right" class="style70"><%=df.format(totReFESantionCostCap)%></td>
																			<!-- Expenditure Cost -->
																			<td align="right" class="style70"><%=df.format(totREExpenditureCap)%></td>
																			<td align="right" class="style70"><%=df.format(totFEExpenditureCap)%></td>
																			<td align="right" class="style70"><%=df.format(totReFEExpenditureCap)%></td>
																			<!-- O/s Commitments Cost -->
																			<td align="right" class="style70"><%=df.format(totRECommitmentCap)%></td>
																			<td align="right" class="style70"><%=df.format(totFECommitmentCap)%></td>
																			<td align="right" class="style70"><%=df.format(totReFECommitmentCap)%></td>
																			<!-- Balance Cost -->
																			<td align="right" class="style70"><%=df.format(btotalReCap)%></td>
																			<td align="right" class="style70"><%=df.format(btotalFeCap)%></td>
																			<td align="right" class="style70"><%=df.format(totReFeBalanceCap)%></td>
																		</tr>
																		<tr>
																			<td colspan="2" class="style70"><b>GrandTotal (Rev + Cap) </b></td>
																			<!-- Sanction Cost -->
																			<td align="right" class="style70"><%=df.format(totReSanctionCostRev + totReSanctionCostCap)%></td>
																			<td align="right" class="style70"><%=df.format(totFESanctionCostRev + totFESanctionCostCap)%></td>
																			<td align="right" class="style70"><%=df.format(totReFESantionCostRev + totReFESantionCostCap)%></td>
																			<!-- Expenditure Cost -->
																			<td align="right" class="style70"><%=df.format(totREExpenditureRev + totREExpenditureCap)%></td>
																			<td align="right" class="style70"><%=df.format(totFEExpenditureRev + totFEExpenditureCap)%></td>
																			<td align="right" class="style70"><%=df.format(totReFEExpenditureRev + totReFEExpenditureCap)%></td>
																			<!-- O/s Commitments Cost -->
																			<td align="right" class="style70"><%=df.format(totRECommitmentRev + totRECommitmentCap)%></td>
																			<td align="right" class="style70"><%=df.format(totFECommitmentRev + totFECommitmentCap)%></td>
																			<td align="right" class="style70"><%=df.format(totReFECommitmentRev + totReFECommitmentCap)%></td>
																			<!-- Balance Cost -->
																			<td align="right" class="style70"><%=df.format(btotalReRev + btotalReCap)%></td>
																			<td align="right" class="style70"><%=df.format(btotalFeRev + btotalFeCap)%></td>
																			<td align="right" class="style70"><%=df.format(totReFeBalanceRev + totReFeBalanceCap)%></td>
																		</tr>

																	</tbody>
																</table>
																<%-- <%}} %> --%>
												    		</td>
												    	</tr>
			               							</table>
			               							
			               							
			               							
			               							<!-- Remarks History -->
							            			 <div class="row mt-2 style103">
														<%if(acpRemarksHistory.size()>0){ %>
															<div class="col-md-8 style104" align="left">
																<%if(acpRemarksHistory.size()>0){ %>
																	<table class="style105">
																		<tr>
																			<td class="style106">
																			<h6 class="style83">Remarks :</h6> 
																			</td>											
																		</tr>
																		<%for(Object[] obj : acpRemarksHistory){%>
																		<tr>
																			<td class="style107">
																				<%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - "%>&nbsp; :
																				<span class="style108">	<%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %></span>
																			</td>
																		</tr>
																		<%} %>
																	</table>
																<%} %>
															</div>
														<%} %>
					   								</div>
			               							
			               							<div class="row mt-2 mb-4">
														<div class="col-md-12" align="center">
															<%if(statuscode!=null && acpforward.contains(statuscode)) {%>
																<div class="ml-2" align="left">
									   								<b >Remarks :</b><br>
									   								<textarea rows="3" cols="65" name="remarks" id="remarksarea"></textarea>
								         						</div>
																<button type="submit" class="btn btn-sm submit" name="Action" formaction="ProjectClosureACPApprovalSubmit.htm" value="A" onclick="return confirm('Are you Sure to Submit ?');">Forward</button>
															<%} %>
															<%if(isApproval!=null && isApproval.equalsIgnoreCase("Y")) {%>
																<%if(statuscode!=null && ( statuscode.contains("AAD")  || statuscode.contains("AAO") || statuscode.contains("AAN") ) ) {%>
																	<div class="row externalapproval">
																		<div class="col-md-3">
																			<label class="control-label">Lab</label><span class="mandatory">*</span>
																			<select class="form-control selectdee" id="LabCode" name="LabCode" onchange="LabcodeSubmit()" data-live-search="true"  required="required">
		        																<option disabled="disabled" value="" selected="selected"> Select</option>
																					<%if (labList != null && labList.size() > 0) {
																						for (Object[] obj : labList) {
																					%>
																						<option value=<%=obj[2].toString()%>><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - "%></option>
																					<%}}%>
																					<option value="@EXP">Expert</option>
																			</select>
																		</div>
																		<div class="col-md-3">
																			<label class="control-label">Approval Officer</label><span class="mandatory">*</span>
				  															<select class="form-control selectdee" id="approverEmpId" name="approverEmpId" data-live-search="true" required>
				  																<option disabled="disabled" value="" selected="selected"> Select</option>
				  															</select>
																		</div>
																		<div class="col-md-2">
																			<label class="control-label" >Approval Date</label><span class="mandatory">*</span>
			          														<input type="text" class="form-control" id="approvalDate" name="approvalDate">
																		</div>
																		<div class="col-md-4"></div>
																		
																	</div>
																<%} %>
																
																<div class="ml-2" align="left">
									   								<b >Remarks :</b><br>
									   								<textarea rows="3" cols="65" name="remarks" id="remarksarea"></textarea>
								         						</div>
								         						<%if(statuscode!=null && acpapprove.contains(statuscode)) {%>
								         						<button type="submit" class="btn btn-sm btn-success style89" id="finalSubmission" formaction="ProjectClosureACPApprovalSubmit.htm" name="Action" value="A" onclick="return confirm('Are You Sure To Approve?');">
										    						Approve	
									      						</button>
									      						
									      						<button type="submit" class="btn btn-sm btn-danger style109" id="finalSubmission" formaction="ProjectClosureACPApprovalSubmit.htm" name="Action" value="R" onclick="return validateTextBox();">
										 							Return
																</button>
								         						<%} else{%>
																<button type="submit" class="btn btn-sm btn-success style89" id="finalSubmission" formaction="ProjectClosureACPApprovalSubmit.htm" name="Action" value="A" onclick="return confirm('Are You Sure To Recommend?');">
										    						Recommend	
									      						</button>
									      						
									      						<button type="submit" class="btn btn-sm btn-danger style109" id="finalSubmission" formaction="ProjectClosureACPApprovalSubmit.htm" name="Action" value="R" onclick="return validateTextBox();">
										 							Return
																</button>
															<%} }%>
														</div>
                   									</div>
                   									
                   								</form>
                   							</div>
                   						</div>
                   					</div>
               					<%} else{%>
			               			<div class="mt-4 style110">
			               				<h4 class="style111">Please fill Administrative Closure Details..!</h4>
			               			</div>
               					<%} %>
               					
               					<div class="style67">
	               					<div></div>
		               				<div>
		               					<%if(isApproval==null) {%>
		               					<div class="row"  >
				 		  					<div class="col-md-12 style68"><b>Approval Flow For Administrative Closure Approval</b></div>
				 	    				</div>
				 	    				<div class="row style112" >
			              					<table align="center"  >
			               						<tr>
			               							<td class="trup style113">
			                							PD -  <%if(PDData!=null) {%><%=PDData[2]!=null?StringEscapeUtils.escapeHtml4(PDData[2].toString()): " - " %> <%} else{%>GD<%} %>
			                						</td>
			                		
		                        					<td rowspan="2">
			                							<i class="fa fa-long-arrow-right " aria-hidden="true"></i>
			                						</td>
			                						<td class="trup style114">
			                							GD - <%if(GD!=null) {%><%=GD[1]!=null?StringEscapeUtils.escapeHtml4(GD[1].toString()): " - " %> <%} else{%>GD<%} %>
			                	    				</td>
			                	    				
			                	    				<td rowspan="2">
			                							<i class="fa fa-long-arrow-right " aria-hidden="true"></i>
			                						</td>
			                						<td class="trup style115">
			                							AD - <%if(AD!=null) {%><%=AD[1]!=null?StringEscapeUtils.escapeHtml4(AD[1].toString()): " - " %> <%} else{%>AD<%} %>
			                	    				</td>
			                	    				
			                	    				<td rowspan="2">
			                							<i class="fa fa-long-arrow-right " aria-hidden="true"></i>
			                						</td>
			                						<td class="trup style116">
			                							GD-DP&C - <%if(GDDPandC!=null) {%><%=GDDPandC[1]!=null?StringEscapeUtils.escapeHtml4(GDDPandC[1].toString()): " - " %> <%} else{%>GD-DP&C<%} %>
			                	    				</td>
			                	    				
			                	    				<td rowspan="2">
			                							<i class="fa fa-long-arrow-right " aria-hidden="true"></i>
			                						</td>
			                						<td class="trup style117">
			                							Lab Accounting Officer - <%if(LAO!=null) {%><%=LAO[1]!=null?StringEscapeUtils.escapeHtml4(LAO[1].toString()): " - " %> <%} else{%>LAO<%} %>
			                	    				</td>
			                	    				
			                	    				<td rowspan="2">
			                							<i class="fa fa-long-arrow-right " aria-hidden="true"></i>
			                						</td>
			                						<td class="trup style118">
			                							Director - <%if(Director!=null) {%><%=Director[1]!=null?StringEscapeUtils.escapeHtml4(Director[1].toString()): " - " %> <%} else{%>Director<%} %>
			                	    				</td>
			                	    				
			                	    				<%if(isMain!=0 && sanctionCost<=10000000) {
			                							
			                						}else if(isMain!=0 && (sanctionCost>10000000 && sanctionCost<=750000000)) {
			                							
			                						}else {%>
			                	    				<td rowspan="2">
			                							<i class="fa fa-long-arrow-right " aria-hidden="true"></i>
			                						</td>
			                						
			                						<td class="trup style119">
			                							O/o DG(ECS) - O/o DG(ECS)
			                	    				</td>
			                	    				<%} %>
			                	    				
			                	    				<%if(isMain==0 && sanctionCost>750000000) {%>
			                	    				<td rowspan="2">
			                							<i class="fa fa-long-arrow-right " aria-hidden="true"></i>
			                						</td>
			                						
			                						<td class="trup style120">
			                							 DG(Nodal) - DG(Nodal)
			                	    				</td>
			                	    				<%} %>
			                	    				
			                	    				<%if(!(isMain!=0 && sanctionCost<=10000000)) {%>
			                	    				<td rowspan="2">
			                							<i class="fa fa-long-arrow-right " aria-hidden="true"></i>
			                						</td>
			                						
			                						<td class="trup style121">
			                							CFA - CFA
			                	    				</td>
			                	    				<%} %>
			               						</tr> 	
			               	    			</table>			             
					 					</div>
					 					<%} %>
		               				</div>
		               				<div class="navigation_btn style70">
		            					<a class="btn btn-info btn-sm  shadow-nohover previous" >Previous</a>
										<button class="btn btn-info btn-sm next">Next</button>
									</div>
               					</div>
               			<%if(acpTabId!=null && acpTabId.equalsIgnoreCase("2")){ %> 
         					</div>
         				<%}else{ %>
              				</div>
               			<%} %>
               			
               		</div>
               	</div>
         	</div>
         </div>
	</div>
</div>

<script type="text/javascript">
// Date Picker for Expediture as on
$('#expndAsOn').daterangepicker({
	"singleDatePicker" : true,
	"linkedCalendars" : false,
	"showCustomRangeLabel" : true,
	/* "minDate" :datearray,   */
	 /* "startDate" : new Date(), */
	 "maxDate" : new Date(), 
	"cancelClass" : "btn-default",
	showDropdowns : true,
	locale : {
		format : 'DD-MM-YYYY'
	}
});	

// Date Picker for Consultancy Date
$('#consultancyDate').daterangepicker({
	"singleDatePicker" : true,
	"linkedCalendars" : false,
	"showCustomRangeLabel" : true,
	/* "minDate" :datearray,   */
	 /* "startDate" : new Date(), */
	 "maxDate" : new Date(), 
	"cancelClass" : "btn-default",
	showDropdowns : true,
	locale : {
		format : 'DD-MM-YYYY'
	}
});	
</script>

<script type="text/javascript">

 function bootstrapTabControl(){
	  var i, items = $('.nav-link'), pane = $('.tab-pane');
	  // next
	  $('.next').on('click', function(){
	      for(i = 0; i < items.length; i++){
	          if($(items[i]).hasClass('active') == true){
	              break;
	          }
	      }
	      if(i < items.length - 1){
	          $(items[i+1]).trigger('click');
	      }

	  });
	  // Prev
	  $('.previous').on('click', function(){
	      for(i = 0; i < items.length; i++){
	          if($(items[i]).hasClass('active') == true){
	              break;
	          }
	      }
	      if(i != 0){
	          $(items[i-1]).trigger('click');
	      }
	  });
	}
	bootstrapTabControl(); 

</script>


<script type="text/javascript">
/* button disabling for ACP Approval */
<%if((acp!=null && acpforward.contains(closure.getClosureStatusCode())) || acp==null) {%>
$('.btn-acp').prop('disabled',false);
<%} else{%>
$('.btn-acp').prop('disabled',true);
<%} %>

/* tabs hiding for ACP approval */
<%if(isApproval!=null && (isApproval.equalsIgnoreCase("Y") || isApproval.equalsIgnoreCase("N"))) {%>
   $('.navigation_btn').hide();
   $('#nav-acpdetails').hide();
<%} %>
</script>

<!-- Remarks Handling -->
<script type="text/javascript">
function validateTextBox() {
    if (document.getElementById("remarksarea").value.trim() != "") {
    	return confirm('Are You Sure To Return?');
    	
    } else {
        alert("Please enter Remarks to Return");
        return false;
    }
}
function disapprove() {
    if (document.getElementById("remarksarea").value.trim() != "") {
    	return confirm('Are You Sure To Disappove?');
    	
    } else {
        alert("Please enter Remarks to Disapprove");
        return false;
    }
}
</script>

<script type="text/javascript">
function LabcodeSubmit() {
	   var LabCode = document.getElementById("LabCode").value;
	   $('#approverEmpId').empty();
	   $.ajax({
	       type: "GET",
	       url: "GetLabcodeEmpList.htm",
	       data: {
	       	LabCode: LabCode
	       },
	       dataType: 'json',
	       success: function(result) {
	    	   if (result != null && LabCode!='@EXP') {
	                for (var i = 0; i < result.length; i++) {
	                    var data = result[i];
	                    var optionValue = data[0];
	                    var optionText = data[1].trim() + ", " + data[3]; 
	                    var option = $("<option></option>").attr("value", optionValue).text(optionText);
	                    $('#approverEmpId').append(option); 
	                }
	                $('#approverEmpId').select2();
	                }else{
	                	for (var i = 0; i < result.length; i++) {
	                        var data = result[i];
	                        var optionValue = data[0];
	                        var optionText = data[1].trim() + ", " + data[3]; 
	                        var option = $("<option></option>").attr("value", optionValue).text(optionText);
	                        $('#approverEmpId').append(option); 
	                    }
	                    $('#approverEmpId').select2();
	                }
	               /* 	for (var i = 0; i < result.length; i++) {
	                       var data = result[i];
	                       var optionValue = data[0];
	                       var optionText = data[1].trim() + ", " + data[3]; 
	                       var option = $("<option></option>").attr("value", optionValue).text(optionText);
	                       $('#approverEmpId').append(option); 
	                   }
	                   $('#approverEmpId').selectpicker('refresh'); */
	               
	           }
	   });
	}
	
$('#approvalDate').daterangepicker({
	"singleDatePicker" : true,
	"linkedCalendars" : false,
	"showCustomRangeLabel" : true,
	/* "minDate" :datearray,   */
	 /* "startDate" : new Date(), */
	 "maxDate" : new Date(), 
	"cancelClass" : "btn-default",
	showDropdowns : true,
	locale : {
		format : 'DD-MM-YYYY'
	}
});	
</script>

<script type="text/javascript">

/* ----------------- CKEDITOR Configuration ------------------------------------ */
var editor_config = {
		toolbar : [
				{
					name : 'clipboard',
					items : [ 'PasteFromWord', '-', 'Undo', 'Redo' ]
				},
				{
					name : 'basicstyles',
					items : [ 'Bold', 'Italic', 'Underline', 'Strike',
							'RemoveFormat', 'Subscript', 'Superscript' ]
				},
				{
					name : 'links',
					items : [ 'Link', 'Unlink' ]
				},
				{
					name : 'paragraph',
					items : [ 'NumberedList', 'BulletedList', '-',
							'Outdent', 'Indent', '-', 'Blockquote' ]
				},
				{
					name : 'insert',
					items : [ 'Image', 'Table' ]
				},
				{
					name : 'editing',
					items : [ 'Scayt' ]
				},
				'/',

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
					items : [ 'Print', 'PageBreak', 'Source' ]
				} ],

		removeButtons : 'Underline,Strike,Subscript,Superscript,Anchor,Styles,Specialchar',

		customConfig : '',

		disallowedContent : 'img{width,height,float}',
		extraAllowedContent : 'img[width,height,align]',

		height : 300,

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
	

/* -------------------------- Form Submission with CKEDITOR Data -------------------------------------------- */

/* $('#myform1').submit(function() {

	  var data =CKEDITOR.instances['objectivesnote'].getData();
	  $('textarea[name=acpObjectives]').val(data);

}); */

$('#myform5').submit(function() {

	  var data =CKEDITOR.instances['facilitiesnote'].getData();
	  $('textarea[name=facilitiesCreated]').val(data);

});

$('#myform8').submit(function() {

	  var data =CKEDITOR.instances['recommendationsnote'].getData();
	  $('textarea[name=monitoringCommittee]').val(data);

});

/* ------------------------ Using CKEDITOR Configuration ------------------------ */
/* CKEDITOR.replace('objectivesnote', editor_config); */
CKEDITOR.replace('facilitiesnote', editor_config);
CKEDITOR.replace('recommendationsnote', editor_config);	
</script>

<script type="text/javascript">


$( document ).ready(function() {
	
	/* ----------------------- Aim & Objectives ------------------------ */ 
	<%-- <%if(acp!=null && acp.getACPAim()!=null && acp.getACPObjectives()!=null) {%>
		$('#myform1').hide();
		$('#aimobjectivesdiv').show();
		 var html=$('#objectiveshidden').val();
		
		CKEDITOR.instances['objectivesnote'].setData(html);
		
	<%} else{%>
		$('#myform1').show();
		$('#aimobjectivesdiv').hide();
	<%} %> --%>
	
	/* ----------------------- Sub-Projects ------------------------ */ 
	<%if(subprojects!=null && subprojects.size()>0) {%>
		$('#myform2').hide();
		$('#subprojectsdiv').show();
	<%} else{%>
		$('#myform2').show();
		$('#subprojectsdiv').hide();
	<%} %>
	
	/* ----------------------- CARS / CAPSI ------------------------ */ 
	<%if(carscapsiprojects!=null && carscapsiprojects.size()>0) {%>
		$('#myform3').hide();
		$('#carscapsidiv').show();
	<%} else{%>
		$('#myform3').show();
		$('#carscapsidiv').hide();
	<%} %>
	
	/* ----------------------- Consultancies ------------------------ */ 
	<%if(consultancies!=null && consultancies.size()>0) {%>
		$('#myform4').hide();
		$('#consultanciesdiv').show();
	<%} else{%>
		$('#myform4').show();
		$('#consultanciesdiv').hide();
	<%} %>
	
	/* ----------------------- Facilities Created ------------------------ */
	<%if(acp!=null && acp.getFacilitiesCreated()!=null) {%>
		$('#myform5').hide();
		$('#facilitiescreateddiv').show();
	 	var html=$('#facilitiescreatedhidden').val();
	
		CKEDITOR.instances['facilitiesnote'].setData(html);
	
	<%} else{%>
		$('#myform5').show();
		$('#facilitiescreateddiv').hide();
	<%} %>
	
	/* ----------------------- Trial Results ------------------------ */
	<%if(acp!=null && acp.getTrialResults()!=null) {%>
		$('#myform6').hide();
		$('#trialresultsdiv').show();
	<%} else{%>
		$('#myform6').show();
		$('#trialresultsdiv').hide();
	<%} %>
	
	/* ----------------------- Achievements ------------------------ */
	<%if(achievements!=null && achievements.size()>0) {%>
		$('#myform7').hide();
		$('#achievementsdiv').show();
	<%} else{%>
		$('#myform7').show();
		$('#achievementsdiv').hide();
	<%} %>
	
	/* ----------------------- Recommendations ------------------------ */
	<%if(acp!=null && acp.getMonitoringCommittee()!=null) {%>
		$('#myform8').hide();
		$('#recommendationsdiv').show();
		
		var html=$('#recommendationshidden').val();
		
		CKEDITOR.instances['recommendationsnote'].setData(html);
	<%} else{%>
		$('#myform8').show();
		$('#recommendationsdiv').hide();
	<%} %>
	
	/* ----------------------- Others ------------------------ */
	<%if(acp!=null && acp.getTechReportNo()!=null) {%>
		$('#myform9').hide();
		$('#othersdiv').show();
	<%} else{%>
		$('#myform9').show();
		$('#othersdiv').hide();
	<%} %>
});


</script>

<script type="text/javascript">

function AllowEdit(openingtab){
	/* if(openingtab=='aimobjectives'){
		$('#myform1').show();
		$('#aimobjectivesdiv').hide();
	}else  */
	if(openingtab=='subprojects'){
		$('#myform2').show();
		$('#subprojectsdiv').hide();
	}else if(openingtab=='carscapsi'){
		$('#myform3').show();
		$('#carscapsidiv').hide();
	}else if(openingtab=='consultancies'){
		$('#myform4').show();
		$('#consultanciesdiv').hide();
	}else if(openingtab=='facilitiescreated'){
		$('#myform5').show();
		$('#facilitiescreateddiv').hide();
	}else if(openingtab=='trialresults'){
		$('#myform6').show();
		$('#trialresultsdiv').hide();
	}else if(openingtab=='achievements'){
		$('#myform7').show();
		$('#achievementsdiv').hide();
	}else if(openingtab=='recommendations'){
		$('#myform8').show();
		$('#recommendationsdiv').hide();
	}else if(openingtab=='others'){
		$('#myform9').show();
		$('#othersdiv').hide();
	}
}

function CloseEdit(closingtab){
	/* if(closingtab=='aimobjectives'){
		$('#myform1').hide();
		$('#aimobjectivesdiv').show();
	}else  */
	if(closingtab=='subprojects'){
		$('#myform2').hide();
		$('#subprojectsdiv').show();
	}else if(closingtab=='carscapsi'){
		$('#myform3').hide();
		$('#carscapsidiv').show();
	}else if(closingtab=='consultancies'){
		$('#myform4').hide();
		$('#consultanciesdiv').show();
	}else if(closingtab=='facilitiescreated'){
		$('#myform5').hide();
		$('#facilitiescreateddiv').show();
	}else if(closingtab=='trialresults'){
		$('#myform6').hide();
		$('#trialresultsdiv').show();
	}else if(closingtab=='achievements'){
		$('#myform7').hide();
		$('#achievementsdiv').show();
	}else if(closingtab=='recommendations'){
		$('#myform8').hide();
		$('#recommendationsdiv').show();
	}else if(closingtab=='others'){
		$('#myform9').hide();
		$('#othersdiv').show();
	}
}
</script>

<script type="text/javascript">
/* ----------------------------------- Sub-Projects -------------------------- */
/* Cloning (Adding) the table body rows for Sub-Projects */
$("#subprojectstable").on('click','.btn_add_subprojects' ,function() {
	
	var $tr = $('.tr_clone_subprojects').last('.tr_clone_subprojects');
	var $clone = $tr.clone();
	$tr.after($clone);
	
	$clone.find("input").val("").end();
	
});


/* Cloning (Removing) the table body rows for Sub-Projects */
$("#subprojectstable").on('click','.btn_rem_subprojects' ,function() {
	
var cl=$('.tr_clone_subprojects').length;
	
if(cl>1){
   var $tr = $(this).closest('.tr_clone_subprojects');
  
   var $clone = $tr.remove();
   $tr.after($clone);
   
}
   
});

/* ----------------------------------- CARS / CAPSI -------------------------- */
/* Cloning (Adding) the table body rows for CARS / CAPSI */
$("#carscapsitable").on('click','.btn_add_carscapsi' ,function() {
	
	var $tr = $('.tr_clone_carscapsi').last('.tr_clone_carscapsi');
	var $clone = $tr.clone();
	
	$tr.after($clone);
	$clone.find("input").val("").end();
	
});


/* Cloning (Removing) the table body rows for CARS / CAPSI */
$("#carscapsitable").on('click','.btn_rem_carscapsi' ,function() {
	
var cl=$('.tr_clone_carscapsi').length;
	
if(cl>1){
   var $tr = $(this).closest('.tr_clone_carscapsi');
  
   var $clone = $tr.remove();
   $tr.after($clone);
   
}
   
});

/* ----------------------------------- Consultancies -------------------------- */
/* Cloning (Adding) the table body rows for Consultancies */
$("#consultanciestable").on('click','.btn_add_consultancies' ,function() {
	
	var $tr = $('.tr_clone_consultancies').last('.tr_clone_consultancies');
	var $clone = $tr.clone();
	
	$tr.after($clone);
	$clone.find("input").val("").end();
	$clone.find('#consultancyDate').daterangepicker({
		"singleDatePicker" : true,
		"linkedCalendars" : false,
		"showCustomRangeLabel" : true,
		/* "minDate" :datearray,   */
		 /* "startDate" : new Date(), */
		 "maxDate" : new Date(), 
		"cancelClass" : "btn-default",
		showDropdowns : true,
		locale : {
			format : 'DD-MM-YYYY'
		}
	});
	
});


/* Cloning (Removing) the table body rows for Consultancies */
$("#consultanciestable").on('click','.btn_rem_consultancies' ,function() {
	
var cl=$('.tr_clone_consultancies').length;
	
if(cl>1){
   var $tr = $(this).closest('.tr_clone_consultancies');
  
   var $clone = $tr.remove();
   $tr.after($clone);
   
}
   
});

/* ----------------------------------- Trial Results -------------------------- */
/* Cloning (Adding) the table body rows for Trial Results */
$("#trialresultstable").on('click','.btn_add_trialresults' ,function() {
	
	var $tr = $('.tr_clone_trialresults').last('.tr_clone_trialresults');
	var $clone = $tr.clone();
	
	$tr.after($clone);
	
	$clone.find("#attachedfile").hide();
	$clone.find("input").val("").end();
	
});


/* Cloning (Removing) the table body rows for Trial Results */
$("#trialresultstable").on('click','.btn_rem_trialresults' ,function() {
	
var cl=$('.tr_clone_trialresults').length;
	
if(cl>1){
   var $tr = $(this).closest('.tr_clone_trialresults');
  
   var $clone = $tr.remove();
   $tr.after($clone);
   
}
   
});

/* ----------------------------------- Achievements -------------------------- */
/* Cloning (Adding) the table body rows for Achievements */
$("#achievementstable").on('click','.btn_add_achievements' ,function() {
	
	var $tr = $('.tr_clone_achievements').last('.tr_clone_achievements');
	var $clone = $tr.clone();
	
	$tr.after($clone);
	
	$clone.find("input").val("").end();
	
});


/* Cloning (Removing) the table body rows for Achievements */
$("#achievementstable").on('click','.btn_rem_achievements' ,function() {
	
var cl=$('.tr_clone_achievements').length;
	
if(cl>1){
   var $tr = $(this).closest('.tr_clone_achievements');
  
   var $clone = $tr.remove();
   $tr.after($clone);
   
}
   
});
</script>

<script type="text/javascript">
$( document ).ready(function() {
	/* List of Sub-Projects required / not required Handling with checkbox */
	$("#subprojectscheck").click(function(){
		if ($(this).is(':checked')) {
            $('#subprojectstable,.btn-subproject').show();
        } else if (!$('#releaseToggleSwitch').is(':checked')) {
            $('#subprojectstable,.btn-subproject').hide();
        }
	});
	
	/* List of CARS / CAPSI required / not required Handling with checkbox */
	$("#carscapsicheck").click(function(){
		if ($(this).is(':checked')) {
            $('#carscapsitable,.btn-carscapsi').show();
        } else if (!$('#releaseToggleSwitch').is(':checked')) {
            $('#carscapsitable,.btn-carscapsi').hide();
        }
	});
	
	/* List of CARS / CAPSI required / not required Handling with checkbox */
	$("#consultanciescheck").click(function(){
		if ($(this).is(':checked')) {
            $('#consultanciestable,.btn-consultancies').show();
        } else if (!$('#releaseToggleSwitch').is(':checked')) {
            $('#consultanciestable,.btn-consultancies').hide();
        }
	});
});
</script>
</body>
</html>