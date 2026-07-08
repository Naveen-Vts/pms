<%@page import="com.vts.pfms.committee.model.Committee"%>
<%@page import="org.apache.commons.io.FilenameUtils"%>
<%@page import="java.nio.file.Path"%>
<%@page import="java.io.File"%>
<%@page import="java.nio.file.Paths"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="org.apache.commons.io.FileUtils"%>
<%@page import="com.vts.pfms.model.BriefingHeading"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat,java.time.LocalDate"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/ckeditor/ckeditor.js" var="ckeditor" />
<spring:url value="/resources/css/print/projectBriefingPaperNew.css" var="projectBriefingPaperNew" />
<link href="${projectBriefingPaperNew}" rel="stylesheet" />
<spring:url value="/resources/css/action/actionCommon.css" var="actionCommon" />
<link href="${actionCommon}" rel="stylesheet" />
<spring:url value="/resources/ckeditor/contents.css" var="contentCss" />
<link href="${contentCss}" rel="stylesheet" />
<spring:url value="/resources/css/sweetalert2.min.css" var="sweetalertCss" />
<spring:url value="/resources/js/sweetalert2.min.js" var="sweetalertJs" />
<link href="${sweetalertCss}" rel="stylesheet" />
<script src="${sweetalertJs}"></script>
<script src="${ckeditor}"></script>
<title>Project Briefing Paper</title>

<style>
/* Container */
.tab-container {
    background: #ffffff;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0,0,0,0.08);
    padding: 15px;
}

/* Tab Header */
.tab-header {
    display: flex;
    justify-content:center;
    border-bottom: 2px solid #f1f1f1;
}

/* Tab Button */
.tab-btn {
    padding: 10px 20px;
    cursor: pointer;
    border: none;
    background: none;
    font-weight: 500;
    color: #666;
    position: relative;
    transition: all 0.3s ease;
}

/* Active Tab */
.tab-btn.active {
    color: #007bff;
}

/* Underline Animation */
.tab-btn.active::after {
    content: "";
    position: absolute;
    left: 0;
    bottom: -2px;
    width: 100%;
    height: 3px;
    background: #007bff;
    border-radius: 2px;
}

/* Tab Content */
.tab-content {
    padding: 15px 5px;
}

/* Hidden */
.tab-pane {
    display: none;
}

.tab-pane.active {
    display: block;
}

.tab-header {
    display: flex;
    align-items: center;
    border-bottom: 2px solid #f1f1f1;
    padding: 10px 0;
}

/* Left (acts as spacer) */
.tab-left {
    flex: 1;
}

/* Center tabs */
.tab-center {
    display: flex;
    gap: 15px;
    justify-content: center;
    flex: 1;
}

/* Right dropdowns */
.tab-right {
    display: flex;
    gap: 10px;
    justify-content: flex-end;
    flex: 1;
    margin-bottom: 10px;
}

/* Tab buttons */
.tab-btn {
    padding: 8px 18px;
    border: none;
    background: none;
    cursor: pointer;
    font-weight: 500;
    color: #666;
    position: relative;
}

.tab-btn.active {
    color: #007bff;
}

.tab-btn.active::after {
    content: "";
    position: absolute;
    bottom: -6px;
    left: 0;
    width: 100%;
    height: 3px;
    background: #007bff;
    border-radius: 2px;
}

.custom-card {
    border-radius: 12px;
    box-shadow: 0 6px 20px rgba(0,0,0,0.08);
    border: none;
}

.custom-header {
    background: linear-gradient(135deg, #007bff, #0056b3);
    color: white;
    border-radius: 12px 12px 0 0;
}

.modern-input {
    border-radius: 8px;
    border: 1px solid #ddd;
    padding: 8px 12px;
    transition: 0.3s;
}

.modern-input:focus {
    border-color: #007bff;
    box-shadow: 0 0 6px rgba(0,123,255,0.2);
}

.add-btn {
    border-radius: 50%;
    width: 35px;
    height: 35px;
}

.remove-btn {
    border-radius: 50%;
    width: 32px;
    height: 32px;
}

.heading-row {
    transition: all 0.2s ease;
}
.control-label {
	font-size: 17px;
	font-weight: bold;
}
.heading-item {
    cursor: pointer;
    border-radius: 6px;
    margin-bottom: 5px;
    transition: 0.2s;
}

.heading-item:hover {
    background: #f1f7ff;
}

.heading-item.active {
    background: #007bff;
    color: #fff;
}
.minutesViewBtnStyle{
	background-color:#0e49b5;
	color:white ;
	font-size:12px;
	text-transform: uppercase;
}
</style>
<%
	DecimalFormat df=new DecimalFormat("####################.##");
	FormatConverter fc=new FormatConverter(); 
	SimpleDateFormat sdf=fc.getRegularDateFormat();
	SimpleDateFormat sdf1=fc.getSqlDateFormat();
	String projectid = (String) request.getAttribute("projectid"); 
	String committeeid = (String) request.getAttribute("committeeid"); 
	String scheduleid = (String) request.getAttribute("scheduleid"); 
	Object[] lastmeetingVenue =  (Object[]) request.getAttribute("lastmeetingVenue");
	List<Object[]> projectslist=(List<Object[]>)request.getAttribute("projectslist");
	List<Object[]> SpecialCommitteesList = (List<Object[]>)request.getAttribute("SpecialCommitteesList");
	List<BriefingHeading> headingList = (List<BriefingHeading>)request.getAttribute("headingList");
	String projectName= null;
	
	List<Object[]> ProjectDetail=(List<Object[]>)request.getAttribute("ProjectDetails"); 
	List<Object[]> projectattributeslist = (List<Object[]> )request.getAttribute("projectattributes");
	List<String> projectidlist = (List<String>)request.getAttribute("projectidlist");
	List<List<Object[]>> ProjectRevList = (List<List<Object[]>>)request.getAttribute("ProjectRevList");
	List<List<Object[]>> ebandpmrccount = (List<List<Object[]>> )request.getAttribute("ebandpmrccount");
	List<Object[]> projectdatadetails = (List<Object[]> )request.getAttribute("projectdatadetails");
	String filePath=(String)request.getAttribute("filePath");
	String projectLabCode=(String)request.getAttribute("projectLabCode");
	String CommitteeCode = null;
%>
</head>
<body>
	<% String ses = (String) request.getParameter("result"); 
       String ses1 = (String) request.getParameter("resultfail");
       if (ses1 != null) { %>
        <div align="center">
            <div class="alert alert-danger" role="alert">
                <%= ses1 %>
            </div>
        </div>
    <% } if (ses != null) { %>
        <div align="center">
            <div class="alert alert-success" role="alert">
                <%= ses %>
            </div>
        </div>
 	<% } %>
 		<div id="spinner" class="spinner display-none"><img id="img-spinner" class="img-height" src="view/images/spinner1.gif" alt="Loading"/></div>
<div class="tab-container">

    <!-- Header -->
    <div class="tab-header">

        <!-- LEFT spacer -->
        <div class="tab-left"></div>

        <!-- CENTER Tabs -->
        <div class="tab-center">
            <button type="button" class="tab-btn " onclick="openTab(event, 'tab1')">
                Headings
            </button>
            <button type="button" class="tab-btn active" onclick="openTab(event, 'tab2')">
                Briefing Paper Details
            </button>
        </div>

        <!-- RIGHT Dropdowns -->
        <div class="tab-right">
            <form action="BriefingPaperV2.htm" method="post" id="projectchange" class="d-flex gap-2">

                <!-- Project -->
                <select class="form-control items width200 selectdee"
                        name="projectid"
                        required
                        onchange="submitForm('projectchange');">

                    <%for(Object[] obj : projectslist){ 
                        String projectshortName=(obj[17]!=null)?" ( "+obj[17].toString()+" ) ":"";
                        if(projectid!=null && projectid.equals(obj[0].toString())) {
                            projectName = obj[4] + projectshortName;
                        }
                    %>
                        <option value="<%=obj[0]%>"
                            <%if(projectid!=null && projectid.equals(obj[0].toString())) { %>selected<%} %>>
                            <%=obj[4] +projectshortName%>
                        </option>
                    <%} %>
                </select>

                <!-- Committee -->
                <select class="form-control items width200 selectdee"
                        name="committeeid"
                        required
                        onchange="submitForm('projectchange');">

                    <% for(Object[] comm : SpecialCommitteesList){ 
                    	if(committeeid.equalsIgnoreCase(comm[0].toString())) {
                            CommitteeCode = comm[1]!=null ? comm[1].toString() : "";
                        }%>
                        <option value="<%=comm[0] %>"
                            <%if(committeeid.equalsIgnoreCase(comm[0].toString())){
                            	%>selected<%} %>>
                            <%=comm[1]!=null ? comm[1].toString() : "" %>
                        </option>
                    <%} %>
                </select>

                <!-- CSRF -->
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<input type="hidden" name="projectid" value="<%=projectid%>"/>
				<input type="hidden" name="committeeid" value="<%=committeeid%>"/>	

            </form>
        </div>

    </div>

</div>

    <!-- Content -->
    <div class="tab-content">
    
    <form id="copyForm" method="post" action="CopyHeadingsFromLastMeeting.htm">
	    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
		<input type="hidden" name="projectid" value="<%=projectid%>"/>
		<input type="hidden" name="committeeid" value="<%=committeeid%>"/>	
		<input type="hidden" name="scheduleid" value="<%=scheduleid%>"/>	
	</form>

        <!-- Tab 1 -->
        <div id="tab1" class="tab-pane ">
		
		    <div class="container mt-3">
		        <div class="card custom-card">
		
		            <!-- Header -->
		            <div class="card-header custom-header d-flex justify-content-between align-items-center">
		                <h5 class="mb-0">Add Heading Points</h5>
		
		                <!-- ADD BUTTON -->
		                <button type="button" class="btn btn-sm btn-light add-btn" onclick="addRow()">
		                    <i class="fa fa-plus"></i>
		                </button>
		            </div>
		
		            <!-- Body -->
		            <div class="card-body">
					<form method="post" action="BriefingHeadingSubmit.htm" >
		                <!-- Project -->
		                <div class="row mb-3">
		                    <div class="col-md-2 text-center">
		                        <label class="control-label">Project</label>
		                    </div>
		                    <div class="col-md-3">
		                        <input type="text"
		                               class="form-control"
		                               value="<%=projectName %>"
		                               disabled />
		                               
		                    </div>
		                    <div class="col-md-7 d-flex justify-content-end">
		                    	<button type="button" onclick="copyHeadings()" class="btn btn-sm edit">Copy Headings From last Meeting</button>
		                    </div>
		                </div>
		
		                <!-- Dynamic Heading List -->
		                <div id="headingContainer">
		
							<% if(headingList != null && !headingList.isEmpty()) { 
								int num =1;
							    for(BriefingHeading h : headingList) { %>
							
							    <div class="row align-items-center mb-2 heading-row">
							
							        <div class="col-md-2 text-center">
							            <%if(num==1){ %><label class="control-label">Headings</label><%} %>
							        </div>
							
							       <%--  <div class="col-md-8">
							            <input type="text"
							                   name="heading"
							                   value="<%=h.getHeading()%>"
							                   class="form-control modern-input"
							                   required />
							
							            <!-- IMPORTANT: ID for update -->
							            <input type="hidden" name="headingId" value="<%=h.getHeadingId()%>" />
							        </div> --%>
							        
							        
							        <div class="col-md-1 text-center">
									    <input type="number"
									           name="seniority"
									           value="<%=h.getSeniority() != null ? h.getSeniority() : num%>"
									           class="form-control"
									           onchange="handleSeniorityChange()" />
									</div>
									
									<div class="col-md-7">
									    <input type="text"
									           name="heading"
									           value="<%=h.getHeading()%>"
									           class="form-control modern-input"
									           required />
									
									    <!-- ID -->
									    <input type="hidden" name="headingId" value="<%=h.getHeadingId()%>" />
									</div>
							
							        <div class="col-md-2 text-end">
							           <%if(num!=1){ %>  <button type="button"
							                    class="btn btn-danger btn-sm remove-btn"
							                    onclick="removeRow(this)">
							                <i class="fa fa-minus"></i>
							            </button>
							            <%} %>
							        </div>
							
							    </div>
							
							<% num++; } } else { %>
							
							    <!-- Default empty row -->
							    <div class="row align-items-center mb-2 heading-row">
							
							        <div class="col-md-2 text-center">
							            <label class="control-label">Headings</label>
							        </div>
							
							       <!--  <div class="col-md-8">
							            <input type="text"
							                   name="heading"
							                   class="form-control modern-input"
							                   placeholder="Enter heading..."
							                   required />
							        </div> -->
							        
							        <div class="col-md-1 text-center">
									    <input type="number" name="seniority" value="1" class="form-control" onchange="handleSeniorityChange()" />
									</div>
									
									<div class="col-md-7">
									    <input type="text"
									           name="heading"
									           class="form-control modern-input"
									           placeholder="Enter heading..."
									           required />
									</div>
							
							        <div class="col-md-2"></div>
							    </div>
							
							<% } %>
							
							</div>
		
		                <!-- Save Button -->
		                <div class="text-center mt-4">
		                    <button type="submit" class="btn btn-sm submit" onclick="return validateSeniority()">submit</button>
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							<input type="hidden" name="projectid" value="<%=projectid%>"/>
							<input type="hidden" name="committeeid" value="<%=committeeid%>"/>	
							<input type="hidden" name="action" value="<%= (headingList != null && !headingList.isEmpty()) ? "Update" : "Add" %>" />
		                </div>
					</form>
		            </div>
		
		        </div>
		    </div>
		</div>

        <!-- Tab 2 -->
       <div id="tab2" class="tab-pane active">
 			
			<form action="ProjectBriefingPaper.htm" method="post" >
				<div class="row">
				    <div class="col-md-11 d-flex justify-content-end gap-3">
				        <!-- <button type="submit" class="btn btn-sm view minutesViewBtnStyle" formmethod="GET" formaction="ProjectMOMV2Download.htm" formtarget="_blank" style="margin-right: 10px!important;">Tabular minutes</button> -->
				        <button  type="submit" class="btn btn-sm border-radius3 border-0 mr-3"  formmethod="GET" formaction="BriefingPaperV2Download.htm" formtarget="_blank"
						 data-toggle="tooltip" data-placement="top" title="Briefing Paper pdf" >
							<i class="fa fa-download fa-lg" aria-hidden="true"></i>
						</button>
				        <button type="submit" class="btn btn-sm back">Back</button>
				    </div>
					<input type="hidden" name="projectid" value="<%=projectid%>"/>
					<input type="hidden" name="committeeid" value="<%=committeeid%>"/>	
					<input type="hidden" name="scheduleid" value="<%=scheduleid%>"/>	
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				</div>
			 </form>
			 <%if(headingList==null || headingList.isEmpty()){ %>
				 <div class="text-center text-danger">
				 	<h3>Add Heading Please!</h3>
				 </div>
			 <%} %>
		    <%if(headingList!=null && !headingList.isEmpty()){
		    	int num = 1;%>
		    <div class="container-fluid mt-3">
		   
		        <div class="row">
		
		            <!-- LEFT: HEADINGS -->
		            <div class="col-md-3">
		                <div class="card custom-card p-2">
		                    <!-- <h4 class="mb-2" style="text-align: center;font-weight: bold;">Headings</h4> -->
		
		                    <ul class="list-group mt-3" id="headingListMenu">
		                    
		                    <li class="list-group-item heading-item"
								    data-id="-1"
								    data-name="Project Attributes"
		                            onclick="loadHeadingList(this)"
								    data-prid = "<%= projectid %>"
								    data-cid = "<%= committeeid %>"
								    data-sid = "<%= scheduleid %>">
	                    		<b><%=num++ %>. 
							        <span class="heading-text">Project Attributes</span>
							    </b> 
							</li>
							<li class="list-group-item heading-item"
								    data-id="-2"
								    data-name="Schematic Configuration"
		                            onclick="loadHeadingList(this)"
								    data-prid = "<%= projectid %>"
								    data-cid = "<%= committeeid %>"
								    data-sid = "<%= scheduleid %>">
	                    		<b><%=num++ %>. 
							        <span class="heading-text">Schematic Configuration</span>
							    </b> 
							</li>							
							<li class="list-group-item heading-item"
								    data-id="-3"
								    data-name="Overall Product tree/WBS"
		                            onclick="loadHeadingList(this)"
								    data-prid = "<%= projectid %>"
								    data-cid = "<%= committeeid %>"
								    data-sid = "<%= scheduleid %>">
	                    		<b><%=num++ %>. 
							        <span class="heading-text">Overall Product tree/WBS</span>
							    </b> 
							</li>
							

								<% if(headingList != null) { 
								    
								    for(BriefingHeading h : headingList) { 
								        String heading = h.getHeading();
								        boolean isLong = heading.length() > 30;
								        String shortHeading = isLong ? heading.substring(0, 30) + "..." : heading;
								%>
								
								<li class="list-group-item heading-item"
		                            onclick="loadHeadingList(this)"
								    data-full="<%=heading%>"
								    data-short="<%=shortHeading%>"
								    data-expanded="false"
								    data-id="<%=h.getHeadingId() %>"
								    data-name="<%=h.getHeading() %>"
								    data-prid = "<%= projectid %>"
								    data-cid = "<%= committeeid %>"
								    data-sid = "<%= scheduleid %>"
								    >
								
								    <b><%=num++ %>. 
								        <span class="heading-text"><%=shortHeading%></span>
								    </b>
								
								    <% if(isLong) { %>
								        <button class="btn btn-sm btn-link p-0 ms-2"
								                onclick="toggleHeading(this)">
								            Show More
								        </button>
								    <% } %>
								
								</li>
								
								<% } } %>
							
							</ul>
		                </div>
		            </div>
		
		            <!-- RIGHT: LIST -->
		            <div class="col-md-9">
		                <div class="card custom-card">
		
		                    <div class="card-header custom-header">
		                        <h5 id="headingTitle">Select a Heading</h5>
		                    </div>
		
		                    <div class="card-body" id="table-content">
		
								<form action="DetailsAddEdit.htm" id="addEditDetails" method="post">
			                        <!-- Add Button -->
			                        <div class="d-flex justify-content-end mb-2">
			                            <button type="button" class="btn btn-sm btn-primary" onclick="addItem('addEditDetails')">+ Add Item</button>
			                        </div>
			                        <input type="hidden" name="headingid" id="headingID" />
			                        <input type="hidden" name="headDetails" id="headDetails" />
			                        <input type="hidden" name="detailsid" id="detailsid" />
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									<input type="hidden" name="projectid" value="<%=projectid%>"/>
									<input type="hidden" name="committeeid" value="<%=committeeid%>"/>	
									<input type="hidden" name="scheduleid" value="<%=scheduleid%>"/>									
		                        </form>
		
		                        <!-- LIST TABLE -->
		                        <table class="table table-bordered">
		                            <thead>
		                                <tr>
		                                    <th style="width: 5%">SN</th>
		                                    <th>Content</th>
		                                    <th style="width: 10%">Action</th>
		                                </tr>
		                            </thead>
		                            <tbody id="contentTableBody">
		                                <tr>
		                                    <td colspan="3" class="text-center text-muted">
		                                        No data
		                                    </td>
		                                </tr>
		                            </tbody>
		                        </table>
		
		                    </div>
							<div class="content mt-2 ml-2" id="project-attribute">
									<% 
									if(projectidlist!=null){
									for(int z=0;z<projectidlist.size();z++)
									{
										List<Object[]>  revlist= ProjectRevList.get(z); 
										Object[] projectattributes =projectattributeslist.get(z);	%>  
										<%if(projectattributes!=null){ %>
										
										<div>
											<form action="ProjectSubmit.htm" method="post" target="_blank">
												<b>Project : <%=ProjectDetail.get(z)[1] %> 	<%if(z!=0){ %>(SUB)<%} %>	</b>
												<button type="submit" name="action" value="edit"  class="btn btn-sm edit padding-3px" > <i class="fa fa-pencil-square-o fa-lg pencil-icon"  aria-hidden="true"></i> </button>
												<input type="hidden" name="ProjectId" value="<%=ProjectDetail.get(z)[0] %>">
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											</form>
										</div>	
										
										
									<table class="subtables projectattributetable"  >
										<tr>
											 <td class="cst-td">(a)</td>
											 <td class="cst-td1"><b>Project Title</b></td>
											 <td colspan="4" class="cst-td2"> <%=projectattributes[1] %></td>
										</tr>
										<tr>
											 <td  class="td-cp">(b)</td>
											 <td class="cst-td1"><b>Project No</b></td>
											 <td colspan="4" class="cst-td2"> <%=projectattributes[2]%> </td>
										</tr>
										<tr>
											 <td  class="td-cp">(c)</td>
											 <td class="cst-td1"><b>Project Unit Code</b></td>
											 <td colspan="4" class="cst-td2"> <%=projectattributes[18]%> </td>
										</tr>
										<tr>
											 <td  class="td-cp">(d)</td>
											 <td class="cst-td1"><b>Project Code</b></td>
											 <td colspan="4" class="cst-td2"> <%=projectattributes[0]%> </td>
										</tr>
										<tr>
											 <td  class=" td-cp">(e)</td>
											 <td  class="cst-td1"><b>Category</b></td>
											 <td colspan="4" class="cst-td2"><%=projectattributes[14]%></td>
										</tr>
										<tr>
											 <td  class="td-cp">(f)</td>
											 <td  class="cst-td1"><b>Date of Sanction</b></td>
											 <td colspan="4" class="cst-td2"><%=sdf.format(sdf1.parse(projectattributes[3].toString()))%></td>
										</tr>
										<tr>
											 <td  class="width20-padding5">(g)</td>
											 <td  class="width150-padding5"><b>Nodal and Participating Labs</b></td>
											 <td colspan="4" class="cst-td2"><%if(projectattributes[15]!=null){ %><%=projectattributes[15]%><%} %></td>
										</tr>
										<tr>
											 <td  class="td-cp">(h)</td>
											 <td  class="width150-padding5"><b>Objective</b></td>
											 <td colspan="4" class="cst-td2 text-justify"> <%=projectattributes[4]%></td>
										</tr>
										<tr>
											 <td  class="td-cp">(i)</td>
											 <td  class="width150-padding5"><b>Deliverables</b></td>
											 <td colspan="4" class=" cst-td2"> <%=projectattributes[5]%></td>
										</tr>
										<tr>
											 <td rowspan="2" class="td-cp">(j)</td>
											 <td rowspan="2" class="width150-padding5"><b>PDC</b></td>
											 
											<td colspan="2" class="textaligncenter">Original &nbsp;</td>					
											<%if( ProjectRevList.get(z).size()>0){ %>	
												<td colspan="2" class="textaligncenter">Revised</td>																			
											<%}else{ %>													 
										 		<td colspan="2" ></td>	
										 	<%} %>
										</tr>
								 		<tr>
								 			<%if( ProjectRevList.get(z).size()>0 ){ %>								
										 		<td colspan="2" class="text-center"><%= sdf.format(sdf1.parse(ProjectRevList.get(z).get(0)[12].toString()))%> </td>
										 		<td colspan="2" class="text-center">
											 		<%if(LocalDate.parse(projectattributes[6].toString()).isEqual(LocalDate.parse(ProjectRevList.get(z).get(0)[12].toString())) ){ %>
											 			-
											 		<%}else{ %>
											 			<%= sdf.format(sdf1.parse(projectattributes[6].toString()))%>
											 		<%} %>
										 		
										 		</td>
											<%}else{ %>													 
										 		<td colspan="2" class="textaligncenter"><%= sdf.format(sdf1.parse(projectattributes[6].toString()))%></td>
												<td colspan="2" ></td>
										 	<%} %>
										 		    
								 		</tr>
											 	
										
											<%if( ProjectRevList.get(z).size()>0 ){ %>
												<tr>
													<td rowspan="3" class="td-i">(j)</td>
													<td rowspan="3" class="padding-left10"><b>Cost Breakup( &#8377; <span class="currency">Lakhs</span>)</b></td>
											
													<td class="width10" >RE Cost</td>
													<td class="text-center"><%=ProjectRevList.get(z).get(0)[17] %></td> 
													<td colspan="2" class="text-center"><%=projectattributes[8] %></td>
												</tr>
												
												
												<tr>
													<td class="width10">FE Cost</td>		
													<td class="text-center"><%=ProjectRevList.get(z).get(0)[16] %></td>					
													<td colspan="2" class="text-center"><%=projectattributes[9] %></td>
												</tr>
													
												<tr>	
													<td class="width10">Total Cost</td>	
													<td class="text-center"><%=ProjectRevList.get(z).get(0)[11] %></td>
											 		<td colspan="2" class="text-center"><%=projectattributes[7] %></td>
												</tr> 
														
											<%}else{ %>
												<tr>
													<td rowspan="3" class="td-i">(k)</td>
													<td rowspan="3" class="padding-left10"><b>Cost Breakup( &#8377; <span class="currency">Lakhs</span>)</b></td>
											
													<td class="width10">RE Cost</td>
													<td ><%=projectattributes[8] %></td>
													<td colspan="2" ></td>
												</tr>
											
												<tr>
													<td class="width10">FE Cost</td>		
													<td ><%=projectattributes[9] %></td>					
													<td colspan="2"></td>
												</tr>
												
												<tr>	
													<td class="width10" >Total Cost</td>	
													<td ><%=projectattributes[7] %></td>
													<td colspan="2"></td>			
												</tr> 
											<%} %>
												
																			 	
										<tr>
											<td  class="td-j">(l)</td>
											<td class="width150-padding5"><b>No. of Meetings held</b> </td>
											<td colspan="4">
												<% if(ebandpmrccount!=null && ebandpmrccount.size()>0){
													List<Object[]> ebandpmrcsub = ebandpmrccount.get(z); 
													for(Object[] ebandpmrc: ebandpmrcsub) { %>
												 	<b><%=ebandpmrc[0] %> : </b>
													<span><%=ebandpmrc[1] !=null ? ((ebandpmrc[0]!=null && ebandpmrc[0].toString().equalsIgnoreCase(CommitteeCode)) ? Long.parseLong(ebandpmrc[1].toString()) - 1 : Long.parseLong(ebandpmrc[1].toString())) : " - " %></span> &emsp;&emsp;
												<%} }%>
											</td>
										</tr>
										<tr>
											<td  class="td-j">(m)</td>
											<td  class="td-k"><b>Current Stage of Project</b></td>
										  	<%
											   String colorCode = projectdatadetails.get(z)!=null ? (String) projectdatadetails.get(z)[11] : "#77D970";
											   String className = "C" + colorCode.replace("#", "").toUpperCase();
											%>
											<td colspan="4" 
		  									 class="ctm-td <%=className%>"> <%= projectdatadetails.get(z) != null ? "<b class="+4+">" + projectdatadetails.get(z)[10] + "</b>" : "Data Not Found" %>
										</td>

										</tr>	
									</table>
		
										<%}else{ %>
											<div align="center" class="margin25"> Complete Project Data Not Found </div>
										<%} %>
									<% }} %>
							</div>
						
		                	<div class="content mt-2 ml-2" id="schematic-overview" >
	   						<%for(int z=0;z<1;z++){ %>
	   						<div align="left" class="margin-left15">
	   							
								<%if(ProjectDetail.size()>1){ %>
										<div>
											<form action="ProjectData.htm" method="post" target="_blank">
												<b>Project : <%=ProjectDetail.get(z)[1] %> 	<%if(z!=0){ %>(SUB)<%} %>	</b>
												<button type="submit" name="action" value="edit"  class="btn btn-sm edit padding-3px" > <i class="fa fa-pencil-square-o fa-lg pencil-icon"  aria-hidden="true"></i> </button>
												<input type="hidden" name="projectid" value="<%=ProjectDetail.get(z)[0] %>">
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											</form>
										</div>	
								<%} %>
	   							<table >
									<tr>
										<td class="border-0"> 
										
										
											<%if(projectdatadetails.get(z)!=null && projectdatadetails.get(z)[3]!=null){ %>
												<form action="ProjectDataSystemSpecsFileDownload.htm"  method="post" target="_blank" >	
													2 (a) System Configuration. &nbsp; <span class="anchorlink" onclick="$('#config<%=ProjectDetail.get(z)[0] %>').toggle();"  ><b>As on File Attached</b></span>
													<button  type="submit" class="btn btn-sm "  ><i class="fa fa-download fa-lg" ></i></button>
													<input type="hidden" name="projectdataid" value="<%=projectdatadetails.get(z)[0]%>"/>
													<input type="hidden" name="filename" value="sysconfig"/>
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
												</form>	
												
												
												<%
												Path systemPath = Paths.get(filePath,projectLabCode,"ProjectData",projectdatadetails.get(z)[3].toString());
												File systemfile = systemPath.toFile();
												if(systemfile.exists()){
												if(FilenameUtils.getExtension(projectdatadetails.get(z)[3].toString()).equalsIgnoreCase("pdf")){ %>
													<iframe	width="1200" height="600" src="data:application/pdf;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(systemfile))%>"  id="config<%=ProjectDetail.get(z)[0] %>" class="display-none" > </iframe>
												<%}else{ %>
													<img class="img-maxwidth" src="data:image/<%=FilenameUtils.getExtension(projectdatadetails.get(z)[3].toString()) %>;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(systemfile))%>"  id="config<%=ProjectDetail.get(z)[0] %>"  > 											
												<%} %>
                                              <%} %>
											<%}else{ %>
												2 (a) System Configuration. &nbsp; File Not Found
											<%} %>
										
										
										</td>
											
									</tr>
								</table>
							
							</div>
							<div align="left" class="margin-left15">
							<table >
								<tr>
									<td class="border-0"> 
										<%if(projectdatadetails.get(z)!=null && projectdatadetails.get(z)[4]!=null){ %>
											<form action="ProjectDataSystemSpecsFileDownload.htm"  method="post" target="_blank" >	
															
												2 (b) System Specifications. &nbsp; <span class="anchorlink" onclick="$('#sysspecs<%=ProjectDetail.get(z)[0] %>').toggle();" ><b>As on File Attached</b></span>
												<button  type="submit" class="btn btn-sm "  ><i class="fa fa-download fa-lg" ></i></button>
												<input type="hidden" name="projectdataid" value="<%=projectdatadetails.get(z)[0]%>"/>
												<input type="hidden" name="filename" value="sysspecs"/>
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											</form>
											<%
											Path specificPath = Paths.get(filePath,projectLabCode,"ProjectData",projectdatadetails.get(z)[4].toString());
											File specificfile = specificPath.toFile();
											if(specificfile.exists()){
											if(FilenameUtils.getExtension(projectdatadetails.get(z)[4].toString()).equalsIgnoreCase("pdf")){ %>
												<iframe	width="1200" height="600" src="data:application/pdf;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(specificfile))%>"  id="sysspecs<%=ProjectDetail.get(z)[0] %>" class="display-none" > </iframe>
											<%}else{ %>
												<img class="img-maxwidth" src="data:image/<%=FilenameUtils.getExtension(projectdatadetails.get(z)[4].toString()) %>;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(specificfile))%>"  id="sysspecs<%=ProjectDetail.get(z)[0] %>"  > 											
											<%} %>
										   <%} %>
										<%}else{ %>
											2 (b) System Specifications. &nbsp; File Not Found
										<%} %>
									
									
									
									</td>
									<td class="border-0">  
									
									</td>
								</tr>
							</table>
							</div>
							<%} %>
							</div>
				
							<div class="content mt-2 ml-2" id="overall-product-tree">
								
		            			<%for(int z=0;z<1;z++){ %>
							<div>
								<%if(ProjectDetail.size()>1){ %>
									<div class="margin-left">
										<b>Project : <%=ProjectDetail.get(z)[1] %> 	<%if(z!=0){ %>(SUB)<%} %>	</b>
									</div>	
								<%} %>
								<table>
									<tr>
										<td class="border-0 padding-left-rem "> 
											<%if(projectdatadetails.get(z)!=null && projectdatadetails.get(z)[5]!=null){ %>
											
												<form action="ProjectDataSystemSpecsFileDownload.htm"  method="post" target="_blank" >	
													Overall Product tree/WBS &nbsp; :  &nbsp;<span class="anchorlink" onclick="$('#protree<%=ProjectDetail.get(z)[0] %>').toggle();" ><b>As on File Attached</b></span>	
													<button  type="submit" class="btn btn-sm "  ><i class="fa fa-download fa-lg" ></i></button>
													<input type="hidden" name="projectdataid" value="<%=projectdatadetails.get(z)[0]%>"/>
													<input type="hidden" name="filename" value="protree"/>
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
												</form>	
												
												
												<%
												Path productTreePath = Paths.get(filePath,projectLabCode,"ProjectData",projectdatadetails.get(z)[5].toString());
												File productTreeFile = productTreePath.toFile();
												if(productTreeFile.exists()){
												if(FilenameUtils.getExtension(projectdatadetails.get(z)[5].toString()).equalsIgnoreCase("pdf")){ %>
													<iframe	width="1200" height="600" src="data:application/pdf;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(productTreeFile))%>"  id="protree<%=ProjectDetail.get(z)[0] %>" class="display-none" > </iframe>
												<%}else{ %>
													<img class="img-maxwidth" src="data:image/<%=FilenameUtils.getExtension(projectdatadetails.get(z)[5].toString()) %>;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(productTreeFile))%>"  id="protree<%=ProjectDetail.get(z)[0] %>"  > 											
												<%} %>
											  <%} %>
											<%}else{ %>
												Overall Product tree/WBS &nbsp; File Not Found
											<%} %>
										
										</td>
										<td class="border-0">  
											
										</td>
									</tr>
								</table>
							</div>
							<%} %>
							</div>	
						
		                </div>
		            </div>
		
		        </div>
		    </div>
		<%} %>
		</div>
 			<form action="DetailsDeleteSubmit.htm" method="post" id="deleteDetails">
            	<input type="hidden" name="detailsid" id="detailsId" />
                <input type="hidden" name="headingid" id="headingId" />
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<input type="hidden" name="projectid" value="<%=projectid%>"/>
				<input type="hidden" name="committeeid" value="<%=committeeid%>"/>	
				<input type="hidden" name="scheduleid" value="<%=scheduleid%>"/>
            </form>
    </div>

<script>
	var defaultHeadingId = "${headingid}";
	
	if (!defaultHeadingId || defaultHeadingId === "null") {
	    defaultHeadingId = "-1";
	}

    if(defaultHeadingId === -1 || defaultHeadingId==="-1" ) $('#project-attribute').show();
    else $('#project-attribute').hide();
function submitForm(frmid){ 
	        $('body').css("filter", "blur(0.8px)");
	        $('#main').hide();
	        $('#spinner').show();
	        document.getElementById(frmid).submit(); 
} 

function openTab(evt, tabName) {
    let i, tabcontent, tabbuttons;

    tabcontent = document.getElementsByClassName("tab-pane");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].classList.remove("active");
    }

    tabbuttons = document.getElementsByClassName("tab-btn");
    for (i = 0; i < tabbuttons.length; i++) {
        tabbuttons[i].classList.remove("active");
    }

    document.getElementById(tabName).classList.add("active");
    evt.currentTarget.classList.add("active");
}

/* 
function addRow() {
    const container = document.getElementById("headingContainer");

    const row = document.createElement("div");
	const rows = document.querySelectorAll('.heading-row');
    if(rows.length >= 13){
 		alert("only 13 heading is required");
 		return;
    }
    
    row.className = "row align-items-center mb-2 heading-row";

    row.innerHTML = `
    <div class="col-md-2"></div>
        <div class="col-md-8">
            <input type="text" name="heading"
                   class="form-control modern-input"
                   placeholder="Enter heading..."
                   required />
        </div>

        <div class="col-md-2 text-end">
            <button type="button" class="btn btn-danger btn-sm remove-btn" onclick="removeRow(this)">
                <i class="fa fa-minus"></i>
            </button>
        </div>
    `;

    container.appendChild(row);
}




function removeRow(button) {
	const rows = document.querySelectorAll('.heading-row');

    // If only one row, don't remove
    if (rows.length <= 1) {
        return;
    }

    const row = button.closest(".heading-row");
    row.remove();
}


 */
function addRow() {
    const container = document.getElementById("headingContainer");
    const rows = document.querySelectorAll('.heading-row');

    if (rows.length >= 13) {
        alert("only 13 heading is required");
        return;
    }

    const row = document.createElement("div");
    row.className = "row align-items-center mb-2 heading-row";

    row.innerHTML = `
        <div class="col-md-2"></div>

        <div class="col-md-1 text-center">
            <input type="number" name="seniority"
                   class="form-control"
                   onchange="handleSeniorityChange()" />
        </div>

        <div class="col-md-7">
            <input type="text" name="heading"
                   class="form-control modern-input"
                   placeholder="Enter heading..."
                   required />
        </div>

        <div class="col-md-2 text-end">
            <button type="button" class="btn btn-danger btn-sm remove-btn"
                    onclick="removeRow(this)">
                <i class="fa fa-minus"></i>
            </button>
        </div>
    `;

    container.appendChild(row);

    updateSeniority(); //  IMPORTANT
}
 
 function removeRow(button) {
	    const rows = document.querySelectorAll('.heading-row');

	    if (rows.length <= 1) return;

	    const row = button.closest(".heading-row");
	    row.remove();

	    updateSeniority(); //  IMPORTANT
	}

 function updateSeniority() {
	    const rows = document.querySelectorAll(".heading-row");

	    rows.forEach((row, index) => {
	        const input = row.querySelector("input[name='seniority']");
	        if (input) {
	            input.value = index + 1;
	        }
	    });
	}
 
let selectedHeadingId = null;
let prid = null;
let cid = null;
let scid = null;
let heading = null;

//Load list when clicking heading
function loadHeadingList(el) {

 document.querySelectorAll(".heading-item").forEach(e => e.classList.remove("active"));
 el.classList.add("active");

 selectedHeadingId = el.getAttribute("data-id");
 prid = el.getAttribute("data-prid");
 cid = el.getAttribute("data-cid");
 scid = el.getAttribute("data-sid");
 heading = el.getAttribute("data-full");
 
 if(selectedHeadingId === -1 || selectedHeadingId==="-1"){
	
	 $('#table-content').hide();
	 $('#project-attribute').show();
	 $('#overall-product-tree').hide();
	 $('#schematic-overview').hide();

	 document.getElementById("headingTitle").innerText = el.getAttribute("data-name");
	 return;
 }
 if(selectedHeadingId === -2 || selectedHeadingId==="-2"){
		
	 $('#table-content').hide();
	 $('#project-attribute').hide();
	 $('#schematic-overview').show();
	 $('#overall-product-tree').hide();

	 document.getElementById("headingTitle").innerText = el.getAttribute("data-name");
	 return;
 }
 if(selectedHeadingId === -3 || selectedHeadingId==="-3"){
		
	 $('#table-content').hide();
	 $('#project-attribute').hide();
	 $('#schematic-overview').hide();
	 $('#overall-product-tree').show();

	 document.getElementById("headingTitle").innerText = el.getAttribute("data-name");
	 return;
 }
 
 
 $('#table-content').show();
 $('#project-attribute').hide();
 $('#schematic-overview').hide();
 $('#overall-product-tree').hide();
 
 
 document.getElementById("headingTitle").innerText =
     el.getAttribute("data-name");

 $.ajax({
		type : "GET",
		url : "GetHeadingItems.htm",
		data : {
			headingid : selectedHeadingId,
			projectid : prid,
			committeid: cid,
			scheduleid: scid,
		},
		datatype: 'json',
		success : function(result)
			{
				var result= JSON.parse(result);
				renderTable(result);
			}
	})
	
}

//Render table
function renderTable(data) {

 const tbody = document.getElementById("contentTableBody");

 if (!data || data.length === 0) {
     tbody.innerHTML = `<tr>
         <td colspan="3" class="text-center text-muted">No data</td>
     </tr>`;
     return;
 }

 let html = "";

 data.forEach((item, index) => {

	    const id = item[0];
	    const content = item[1];

	    html += '<tr><td class="text-center" >'+(index + 1) +' </td>'
	    +'<td>'+content+'</td>'+
	    '<td class="text-center d-flex gap-2 border-0">'+
	    '<button class="btn btn-sm edit" onclick="editItem('+id+')" style="margin-right: 10px!important;"><i class="fa fa-pencil-square-o fa-lg pencil-icon"  aria-hidden="true"></i> </button> <br>'+
	    '<button class="btn btn-sm btn-danger" title="Delete" onclick="deleteItem('+id+')">' +
	    'DELETE' +
	    '</button></td></tr>';
	    
	    });

 tbody.innerHTML = html;
}

//Add item
function addItem(formId) {

	
 if (!selectedHeadingId) {
     alert("Select a heading first");
     return;
 }
 
 $('#headingID').val(selectedHeadingId);
 $('#headDetails').val(heading);
 
 console.log(formId);
 
 document.getElementById(formId).submit(); 

}

//Delete item
function deleteItem(id) {

 if (!confirm("Delete this item?")) return;
 
 $('#detailsId').val(id);
 $('#headingId').val(selectedHeadingId);

 const formId = "deleteDetails";
 
 document.getElementById(formId).submit(); 

}

function editItem(id){

	console.log("inside")
	 $('#detailsid').val(id);
	addItem('addEditDetails');
}

function toggleHeading(btn) {
    const li = btn.closest("li");
    const textSpan = li.querySelector(".heading-text");

    const fullText = li.getAttribute("data-full");
    const shortText = li.getAttribute("data-short");
    const expanded = li.getAttribute("data-expanded");

    if (expanded === "false") {
        textSpan.innerText = fullText;
        btn.innerText = "Show Less";
        li.setAttribute("data-expanded", "true");
    } else {
        textSpan.innerText = shortText;
        btn.innerText = "Show More";
        li.setAttribute("data-expanded", "false");
    }
}

document.addEventListener("DOMContentLoaded", function () {

    if (!defaultHeadingId) return;

    const items = document.querySelectorAll(".heading-item");

    items.forEach(item => {

        const id = item.getAttribute("data-id");

        if (id === defaultHeadingId) {

            // Trigger click
            loadHeadingList(item);

            // OPTIONAL: scroll into view
            item.scrollIntoView({ behavior: "smooth", block: "center" });
        }
    });
});

function copyHeadings() {
    document.getElementById("copyForm").submit();
}

function handleSeniorityChange() {
   /*  const container = document.getElementById("headingContainer");
    const rows = Array.from(container.querySelectorAll(".heading-row"));

    // Sort rows based on entered seniority
    rows.sort((a, b) => {
        const valA = parseInt(a.querySelector("input[name='seniority']").value) || 0;
        const valB = parseInt(b.querySelector("input[name='seniority']").value) || 0;
        return valA - valB;
    });

    // Re-append in sorted order
    rows.forEach(row => container.appendChild(row));

    // Reset proper sequence (1,2,3...)
    updateSeniority(); */
}

function validateSeniority() {
    const inputs = document.querySelectorAll("input[name='seniority']");
    let values = [];

    for (let input of inputs) {
        let val = parseInt(input.value);

        // empty or invalid
        if (!val || val <= 0) {
            alert("Seniority must be a positive number");
            input.focus();
            return false;
        }

        values.push(val);
    }

    // check duplicates
    let unique = new Set(values);
    if (unique.size !== values.length) {
        alert("Duplicate seniority values are not allowed");
        return false;
    }

    // check continuous sequence (optional but recommended)
    values.sort((a, b) => a - b);

    for (let i = 0; i < values.length; i++) {
        if (values[i] !== i + 1) {
            alert("Seniority must be in sequence (1, 2, 3...)");
            return false;
        }
    }

    return confirm("Are you sure to submit ?"); // allow submit
}

document.addEventListener("DOMContentLoaded", function () {
    updateSeniority();
});

$('.btn[data-toggle="tooltip"]').tooltip({
    animated: 'fade',
    placement: 'top',
    html : true,
    boundary: 'window'
});
</script>
</body>
</html>