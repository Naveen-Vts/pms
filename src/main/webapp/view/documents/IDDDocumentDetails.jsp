<%@page import="com.vts.pfms.documents.model.IGILogicalChannel"%>
<%@page import="com.vts.pfms.documents.model.IGILogicalInterfaces"%>
<%@page import="com.vts.pfms.documents.model.IGIDocumentIntroduction"%>
<%@page import="java.util.function.Function"%>
<%@page import="com.google.gson.GsonBuilder"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.vts.pfms.documents.model.PfmsIDDDocument"%>
<%@page import="com.vts.pfms.documents.model.StandardDocuments"%>
<%@page import="com.vts.pfms.documents.model.IGIInterface"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="com.vts.pfms.documents.model.IGIDocumentShortCodes"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="java.time.LocalDate"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="ISO-8859-1">
    <jsp:include page="../static/header.jsp"></jsp:include>
    
    <spring:url value="/resources/css/Overall.css" var="StyleCSS" />
    <link href="${StyleCSS}" rel="stylesheet" /> 
    <spring:url value="/resources/js/excel.js" var="excel" />
    <script src="${excel}"></script>
    
	<!-- Pdfmake  -->
	<spring:url value="/resources/pdfmake/pdfmake.min.js" var="pdfmake" />
	<script src="${pdfmake}"></script>
	<spring:url value="/resources/pdfmake/vfs_fonts.js" var="pdfmakefont" />
	<script src="${pdfmakefont}"></script>
	<spring:url value="/resources/pdfmake/htmltopdf.js" var="htmltopdf" />
	<script src="${htmltopdf}"></script> 
	
	<!-- Summer Note -->
	<spring:url value="/resources/summernote-lite.js" var="SummernoteJs" />
	<spring:url value="/resources/summernote-lite.css" var="SummernoteCss" />
	<script src="${SummernoteJs}"></script>
	<link href="${SummernoteCss}" rel="stylesheet" />
<style type="text/css">

.topicsSideBar {
	min-height: 670px;
    max-height: 670px;
    overflow-y: auto;
    overflow-x: hidden;
    scrollbar-width: thin;
  	scrollbar-color: #4B70F5 #f8f9fa;
}

.modulecontainer {
	display: flex;
	flex-direction: column;
	gap: 20px;
	/* margin : 2rem 0rem 1rem 9rem; */
}

.module{
 	max-width: 280px;
 	min-width: 280px;
 	max-height: 60px;
 	min-height: 60px;
 	overflow: hidden;
 	cursor: pointer;
    border-top: 3px solid #007bff;
    border-left: 3px solid #007bff;
    border-radius: 0.5rem;
    box-shadow: 0px 20px 40px -20px #a3a5ae;
 }
 
 .module:hover {
 	background-color: #007bff !important;
 	color: white !important;
 }
 
 .topic-name {
 	font-weight: bold;
 	font-size: 14px;
 }
 
 #reqdiv {
 	width: 100%;
 	margin: 0 0.7rem 0.7rem 0.7rem; 
 }
 
 img{
 	width: 25px;
 }
 
 /* Custom colspan */
.col-custom-1-5 {
    width: calc(17.5%); /* This gives 2.5 out of 12 columns */
    flex: 0 0 calc(17.5%);
    max-width: calc(17.5%);
}

@media (max-width: 768px) {
    .col-custom-1-5 {
        width: 100%;
        flex: 0 0 100%;
        max-width: 100%;
    }
}

.col-custom-10-5 {
    width: calc(81.5%); 
    flex: 0 0 calc(81.5%);
    max-width: calc(81.5%);
    margin-left: 0.4rem;
}

@media (max-width: 768px) {
    .col-custom-10-5 {
        width: 100%;
        flex: 0 0 100%;
        max-width: 100%;
    }
}

/* Summer Note styles */
.note-editor {
	width: 100% !important;
} 
	
.customtable{
	border-collapse: collapse;
	width: 100%;
	margin: 1.5rem 0.5rem 0.5rem 0.5rem;
	overflow-y: auto; 
	overflow-x: auto;  
}
.customtable th{
	border: 1px solid #0000002b; 
	padding: 10px;
		background-color: #2883c0;
}
.customtable td{
	border: 1px solid #0000002b; 
	padding: 5px;
}

.customtable thead {
	text-align: center;
	color: white;
	position: sticky;
	top: 0; /* Keeps the header at the top */
	z-index: 10; /* Ensure the header stays on top of the body */
	/* background-color: white; */ /* For visibility */
}

.table-wrapper {
    max-height: 400px; /* Set the max height for the table wrapper */
    overflow-y: auto; /* Enable vertical scrolling */
    overflow-x: hidden; /* Enable vertical scrolling */
}
</style>    

</head>
<body>

	<%
		List<Object[]> iddDocumentSummaryList = (List<Object[]>)request.getAttribute("iddDocumentSummaryList");
		Object[] documentSummary=null;
		if(iddDocumentSummaryList!=null && iddDocumentSummaryList.size()>0){
			documentSummary=iddDocumentSummaryList.get(0);
		}
		String iddDocId =(String)request.getAttribute("iddDocId");
		List<Object[]> totalEmployeeList = (List<Object[]>)request.getAttribute("totalEmployeeList");
		
		List<Object[]> memberList = (List<Object[]>)request.getAttribute("memberList");
		List<Object[]> employeeList = (List<Object[]>)request.getAttribute("employeeList");
		
		List<IGIDocumentShortCodes> shortCodesList = (List<IGIDocumentShortCodes>)request.getAttribute("shortCodesList");
		List<IGIDocumentShortCodes> abbreviationsList = shortCodesList.stream().filter(e -> e.getShortCodeType().equalsIgnoreCase("A")).collect(Collectors.toList());
		List<IGIDocumentShortCodes> acronymsList = shortCodesList.stream().filter(e -> e.getShortCodeType().equalsIgnoreCase("B")).collect(Collectors.toList());
		
		List<Object[]> shortCodesLinkedList = (List<Object[]>)request.getAttribute("shortCodesLinkedList");
		List<Object[]> abbreviationsLinkedList = shortCodesLinkedList.stream().filter(e -> e[3].toString().equalsIgnoreCase("A")).collect(Collectors.toList());
		List<Object[]> acronymsLinkedList = shortCodesLinkedList.stream().filter(e -> e[3].toString().equalsIgnoreCase("B")).collect(Collectors.toList());
		
		List<StandardDocuments> applicableDocsList = (List<StandardDocuments>)request.getAttribute("applicableDocsList");
		List<Object[]> iddApplicableDocsList = (List<Object[]>)request.getAttribute("iddApplicableDocsList");
		List<Long> iddApplicableDocIds = iddApplicableDocsList.stream().map(e -> Long.parseLong(e[1].toString())).collect(Collectors.toList());
		applicableDocsList = applicableDocsList.stream().filter(e -> !iddApplicableDocIds.contains(e.getStandardDocumentId())).collect(Collectors.toList());
		List<String> iddApplicableDocNames = applicableDocsList.stream().map(e -> e.getDocumentName().toLowerCase()).collect(Collectors.toList());

		PfmsIDDDocument iddDocument = (PfmsIDDDocument)request.getAttribute("iddDocument");
		
		List<IGIDocumentIntroduction> introductionList = (List<IGIDocumentIntroduction>)request.getAttribute("igiDocumentIntroductionList");
		introductionList = introductionList.stream().filter(e -> e.getDocId()==Long.parseLong(iddDocId) && e.getDocType().equalsIgnoreCase("D")).collect(Collectors.toList());
		
		List<IGILogicalChannel> logicalChannelList = (List<IGILogicalChannel>)request.getAttribute("logicalChannelList");
		List<IGILogicalInterfaces> logicalInterfaceList = (List<IGILogicalInterfaces>)request.getAttribute("logicalInterfaceList");
		List<Object[]> irsSpecificationsList = (List<Object[]>)request.getAttribute("irsSpecificationsList");
		//irsSpecificationsList.stream().map(e -> Long.parseLong(e[3].toString())).collect(Collectors.toSet());
		logicalInterfaceList = logicalInterfaceList.stream().filter(e -> irsSpecificationsList.stream().map(m -> Long.parseLong(m[3].toString())).collect(Collectors.toList()).contains(e.getLogicalInterfaceId())).collect(Collectors.toList());
		List<Object[]> fieldDescriptionList = (List<Object[]>)request.getAttribute("fieldDescriptionList");
		List<Object[]> productTreeAllList = (List<Object[]>)request.getAttribute("productTreeAllList");
		Map<String, String> productTreeAllListMap = productTreeAllList.stream().filter(obj -> obj[7] != null)
												    .collect(Collectors.toMap(
												        obj -> obj[7].toString(),
												        obj -> obj[2].toString(),
												        (existing, replacement) -> existing
												    ));
		
		Object[] projectDetails = (Object[])request.getAttribute("projectDetails");
		Object[] labDetails = (Object[])request.getAttribute("labDetails");
		Object[] docTempAtrr = (Object[])request.getAttribute("docTempAttributes");
		String lablogo = (String)request.getAttribute("lablogo");
		String drdologo = (String)request.getAttribute("drdologo");
		String version =(String)request.getAttribute("version");
		String projectType =(String)request.getAttribute("projectType");
		String isPdf = (String)request.getAttribute("isPdf");
		String projectShortName = (projectDetails!=null && projectDetails[2]!=null)?projectDetails[2]+"":"";
		
		LocalDate now = LocalDate.now();
		FormatConverter fc = new FormatConverter();
		
		Gson gson = new GsonBuilder().create();
		String jsoniddApplicableDocNames = gson.toJson(iddApplicableDocNames);
		
		String documentNo = "IDD-" + (documentSummary!=null && documentSummary[11]!=null?documentSummary[11].toString().replaceAll("-", ""):"-") 
							+ "-" + ((String)session.getAttribute("labcode")) + "-" +((projectDetails!=null && projectDetails[1]!=null)?projectDetails[1]:"") + "-V"+version;
	%>

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

	<div id="loadingOverlay" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.5); z-index: 9999; justify-content: center; align-items: center; flex-direction: column; color: white; font-size: 20px; font-weight: bold;">
		<div class="spinner" style="border: 4px solid rgba(255, 255, 255, 0.3); border-top: 4px solid white; border-radius: 50%; width: 40px; height: 40px; animation: spin 1s linear infinite; margin-bottom: 10px;"></div>
	   	Please wait while we are generating the PDF...
	</div>
	
    <div class="container-fluid">
       
    	<div class="card shadow-nohover" style="margin-top: -0.6pc">
        	<div class="card-header" style="background: #C4DDFF; box-shadow: 2px 2px 2px grey;">
            	<div class="row">
               		<div class="col-md-10" id="projecthead" align="left">
	                    <h5 id="text" style="margin-left: 1%; font-weight: 600">
	                        IDD Document Details - <%=documentNo %>
	                    </h5>
                	</div>
                    <div class="col-md-2" align="right">
                        <form action="IDDDocumentList.htm" method="get">
                    		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
		        			<input type="hidden" name="projectId" value="<%=iddDocument.getProjectId() %>" />
		        			<input type="hidden" name="initiationId" value="<%=iddDocument.getInitiationId() %>" />
		        			<input type="hidden" name="projectType" value="<%=iddDocument.getProjectId()!=0?"M":"I" %>" />
		        			<button class="btn btn-info btn-sm shadow-nohover back">
		        				Back
		        			</button>
                		</form>
                    </div>
            	</div>
        	</div>
        	<div class="card-body">
        		<div class="row ml-2">
       				<div class="col-custom-1-5 p-0 mb-2">
       					<div class="card" style="border: 3px solid #007bff;">
   							<div class="card-body topicsSideBar">
								<div class="row">
  									<div class="col-md-12">
  										<div class="modulecontainer">
				
											<div class="card module" onclick="DownloadDocPDF()">
												<div class="card-body">
													<div><img alt="" src="view/images/pdf.png" > <span class="topic-name">IDD Document</span></div>
												</div>
											</div>
											
											<div class="card module" data-toggle="modal" data-target="#distributionModal">
												<div class="card-body">
													<div><img alt="" src="view/images/requirements.png" > <span class="topic-name">Document Distribution</span></div>
												</div>
											</div>
											
											<div class="card module" data-toggle="modal" data-target="#summaryModal">
												<div class="card-body">
													<div><img alt="" src="view/images/requirements.png" > <span class="topic-name">Document Summary</span></div>
												</div>
											</div>
											
											<div class="card module" onclick="showShortCodes('A')">
												<div class="card-body">
													<div><img alt="" src="view/images/requirements.png" > <span class="topic-name">Abbreviations</span></div>
												</div>
											</div>
											
											<div class="card module" onclick="showShortCodes('B')">
												<div class="card-body">
													<div><img alt="" src="view/images/requirements.png" > <span class="topic-name">Acronyms</span></div>
												</div>
											</div>
											
											<div class="card module" onclick="showChapter1()">
												<div class="card-body">
													<div><img alt="" src="view/images/requirements.png" > <span class="topic-name">Chapter 1 : Introduction</span></div>
												</div>
											</div>
											
											<div class="card module" onclick="showChapter2()" >
												<div class="card-body">
													<div><img alt="" src="view/images/requirements.png" > <span class="topic-name">Chapter 2 : Applicable Docs</span></div>
												</div>
											</div>
											
											<div class="card module" onclick="showChapter3()">
												<div class="card-body">
													<div><img alt="" src="view/images/requirements.png" > <span class="topic-name">Chapter 3 : Design</span></div>
												</div>
											</div>
											
										</div>
  									</div>
   								</div>
   								
   							</div>
       					</div>
       				</div>
       				<div class="col-custom-10-5">
       					<div class="" id="reqdiv">
							<div style="">
								<% int docsumslno = 0; %>
								<table class="table table-bordered">
									<tr class="table-warning">
										<td align="center" colspan="2" class="text-primary">DOCUMENT SUMMARY</td>
									</tr>
									<tr>
										<td  class="text-primary" colspan="2"><%=++docsumslno %>.&nbsp; Title: <span class="text-dark">Interface Design Description (IDD)</span></td>
									</tr>
									<tr >
										<td class="text-primary"><%=++docsumslno %>.&nbsp; Type of Document:<span class="text-dark">Interface Design Description (IDD) Document</span></td>
										<td class="text-primary"><%=++docsumslno %>.&nbsp; Classification: <span class="text-dark">Restricted</span></td>
									</tr>
								    <tr >
										<td class="text-primary"><%=++docsumslno %>.&nbsp; Document Number: <span class="text-dark"><%=documentNo %></span> </td>
										<td class="text-primary"><%=++docsumslno %>.&nbsp; Month Year: <span style="color:black;"><%=now.getMonth().toString().substring(0,3) %>&nbsp;&nbsp;<%=now.getYear() %></span></td>
									</tr>
									<!-- <tr>
										<td class="text-primary">6.&nbsp; Number of Pages:</td>
										<td class="text-primary">7.&nbsp; Related Document:</td>
									</tr> -->
									<tr>
										<td  class="text-primary" colspan="2"><%=++docsumslno %>.&nbsp; Additional Information:
											<%if(documentSummary!=null && documentSummary[1]!=null) {%><span class="text-dark"><%=documentSummary[1]%></span> <%} %>
										</td>
									</tr>
								    <!-- <tr>
										<td  class="text-primary" colspan="2">9.&nbsp; Project Number and Project Name: <span class="text-dark"> </span></td>
									</tr> -->
									<tr>
										<td  class="text-primary" colspan="2"><%=++docsumslno %>.&nbsp; Abstract:
											<%if(documentSummary!=null && documentSummary[2]!=null) {%> <span class="text-dark"><%=documentSummary[2]%></span><%} %>
										</td>
									</tr>
									<tr>
										<td  class="text-primary" colspan="2"><%=++docsumslno %>.&nbsp; Keywords:
											<%if(documentSummary!=null && documentSummary[3]!=null) {%> <span class="text-dark"><%=documentSummary[3]%></span><%} %>
										</td>
									</tr>
									<tr>
										<td  class="text-primary" colspan="2"><%=++docsumslno %>.&nbsp; Organization and address:
											<span class="text-dark">		
												<%if (labDetails[1] != null) {%>
													<%=labDetails[1].toString() + "(" + labDetails[0].toString() + ")"%>
												<%} else {%>
													-
												<%}%>
												
												Government of India, Ministry of Defence,Defence
												Research & Development Organization
												<%if (labDetails[2] != null && labDetails[3] != null && labDetails[5] != null) {%>
													<%=labDetails[2] + " , " + labDetails[3].toString() + ", PIN-" + labDetails[5].toString()+"."%>
												<%}else{ %>
													-
												<%} %>
											</span>
										</td>
									</tr>
									<tr>
										<td  class="text-primary" colspan="2"><%=++docsumslno %>.&nbsp; Distribution:
											<%if(documentSummary!=null && documentSummary[4]!=null) {%> <span class="text-dark"><%=documentSummary[4]%></span><%} %>
										</td>
									</tr>
									<tr>
										<td  class="text-primary" colspan="2"><%=++docsumslno %>.&nbsp; Revision:</td>
									</tr>
									<tr>
										<td  class="text-primary" colspan="2"><%=++docsumslno %>.&nbsp; Prepared by:
											<%if(documentSummary!=null && documentSummary[10]!=null) {%> <span class="text-dark"><%=documentSummary[10]%></span><%}else {%><span class="text-dark">-</span>  <%} %> <span class="text-dark"></span> 
										</td>
									</tr>
									<tr>
										<td  class="text-primary" colspan="2"><%=++docsumslno %>.&nbsp; Reviewed by: 
											<%if(documentSummary!=null && documentSummary[9]!=null) {%> <span class="text-dark"><%=documentSummary[9]%></span><%}else {%><span class="text-dark">-</span>  <%} %> 
										</td>
									</tr>
									<tr>
										<td  class="text-primary" colspan="2"><%=++docsumslno %>.&nbsp; Approved by: 
											<%if(documentSummary!=null && documentSummary[8]!=null) {%> <span class="text-dark"><%=documentSummary[8]%></span><%}else {%><span class="text-dark">-</span>  <%} %> 
										</td>
									</tr>
								</table>
							</div>
						</div>
       				</div>
       			</div>
       		</div>		
		</div>
    </div>

	<form action="#">
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
		<input type="hidden" name="iddDocId" value="<%=iddDocId%>"> 
		<input type="hidden" name="docId" value="<%=iddDocId%>"> 
		<input type="hidden" name="documentNo" value="<%=documentNo%>"> 
		<input type="hidden" name="projectType" value="<%=projectType%>"> 
		<input type="hidden" name="projectId" value="<%=(projectDetails!=null && projectDetails[0]!=null)?projectDetails[0]:"0"%>"> 
		<input type="hidden" name="docType" value="D"> 
		<input type="hidden" name="shortCodeType" id="shortCodeType"> 
	
		<button type="submit" class="btn bg-transparent" id="shortCodesFormBtn" formaction="IGIShortCodesDetails.htm" formmethod="post" formnovalidate="formnovalidate" style="display:none;"></button>
		
		<button type="submit" id="introductionDetailsFormBtn" formaction="IGIIntroductionDetails.htm" formmethod="post" formnovalidate="formnovalidate" style="display:none;"></button>

		<button type="submit" class="btn bg-transparent" id="applicableDocumentsFormBtn" formaction="IGIApplicableDocumentsDetails.htm" formmethod="post" formnovalidate="formnovalidate" style="display:none;"></button>
		
		<button type="submit" class="btn bg-transparent" id="designDescFormBtn" formaction="IDDDesignDetails.htm" formmethod="post" formnovalidate="formnovalidate" style="display:none;"></button>
	</form>
	<!-- -------------------------------------------- Document Distribution Modal -------------------------------------------- -->
	<div class="modal fade" id="distributionModal" tabindex="-1" role="dialog" aria-labelledby="distributionModal" aria-hidden="true">
  		<div class="modal-dialog modal-lg modal-dialog-jump" role="document">
    		<div class="modal-content" style="width:135%;margin-left:-20%;">
      			<div class="modal-header" id="ModalHeader" style="background: #055C9D ;color: white;">
			        <h5 class="modal-title" >Document Sent to</h5>
			        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
			          <span aria-hidden="true" class="text-light">&times;</span>
			        </button>
      			</div>
      			
      			<div class="modal-body">
      				<%if(memberList!=null && !memberList.isEmpty()) {%>
      					<div class="row mb-2">
							<div class="col-md-12">
								<table class="table table-bordered" id="myTables" style="width: 100%;">
									<thead class="center" style="background: #055C9D ;color: white;">
										<tr>
											<th width="10%">SN</th>
											<th width="40%">Name</th>
											<th width="30%">Designation</th>
											<th width="20%">Action</th>
										</tr>
									</thead>
									<tbody>
									<%int rowCount=0;
										for(Object[]obj:memberList) {%>
											<tr>
												<td class="center"><%=++rowCount %></td>
												<td ><%=obj[1].toString() %></td>
												<td ><%=obj[2].toString() %></td>
												<td class="center" >
												    <form id="deleteForm_<%= obj[5] %>" action="#" method="POST" name="myfrm" style="display: inline">
												        <button type="submit" class="editable-clicko" formaction="IGIDocumentMembersDelete.htm" onclick="return confirmDeletion('<%= obj[5] %>');">
												            <img src="view/images/delete.png" alt="Delete">
												        </button>
												        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
												        <input type="hidden" name="IgiMemeberId" value="<%= obj[5] %>">
												        <input type="hidden" name="iddDocId" value="<%=iddDocId %>">
												        <input type="hidden" name="docId" value="<%=iddDocId%>">	 
														<input type="hidden" name="docType" value="D">	
												    </form>
												</td>

											</tr>
										<%} %>
									</tbody>
								</table>
							</div>      
      					</div>
      				<%} %>
      				
       				<form action="IGIDocumentMemberSubmit.htm" method="post">
      					<div class="row">
							<div class="col-md-10">
								<select class="form-control selectdee"name="Assignee" id="Assignee"data-width="100%" data-live-search="true" multiple required>
							        <%for(Object[] obj: employeeList){ %>
							        	<option value="<%=obj[0].toString()%>"> <%=obj[1].toString() %>,<%=(obj[2].toString()) %></option>
							        <%} %>
							       
        						</select>
        					</div>
					      	<div class="col-md-2" align="center">
					      		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
								<input id="submit" type="submit" name="submit" value="Submit" hidden="hidden">
		
								<input type="hidden" name="iddDocId" value="<%=iddDocId%>">	 
								<input type="hidden" name="docId" value="<%=iddDocId%>">	 
								<input type="hidden" name="docType" value="D">	 
					    		<button type="submit" class="btn btn-sm submit" onclick="return confirm ('Are you sure to submit?')"> SUBMIT</button>
					      	</div>
      					</div>
      				</form>
      			</div>
    		</div>
  		</div>
	</div>
	<!-- -------------------------------------------- Document Distribution Modal End -------------------------------------------- -->
	
	<!-- -------------------------------------------- Document Summary Modal Structure ----------------------------------- -->
	<div class="modal fade" id="summaryModal" tabindex="-1" aria-labelledby="summaryModal" aria-hidden="true">
    	<div class="modal-dialog modal-lg modal-dialog-jump">
        	<div class="modal-content" style="width:150%;margin-left: -20%">
	            <div class="modal-header" style="background: #055C9D;color:white;">
	                <h5 class="modal-title" id="SummaryModalLabel">IDD Document Summary</h5>
	                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
	                    <span class="text-light" aria-hidden="true">&times;</span>
	                </button>
	            </div>
            	<div class="modal-body">
               		<form action="IGIDocumentSummaryAdd.htm" method="post">
               
                  		<div class="row">
   							<div class="col-md-4">
   								<label class="" style="font-size: 1rem;font-weight: bold;color:#07689f">Additional Information <span class="mandatory">*</span></label>
   							</div>
				   			<div class="col-md-8">
				   				<textarea required="required" name="information" class="form-control" id="additionalReq" maxlength="4000"
								rows="3" cols="53" placeholder="Maximum 4000 Chararcters" required><%if(documentSummary!=null && documentSummary[1]!=null){%><%=documentSummary[1]%><%}else{%><%}%></textarea>
				   			</div>
   				 		</div>
   				 
   				 		<div class="row mt-2">
				   			<div class="col-md-4">
				   				<label class="" style="font-size: 1rem;font-weight: bold;color:#07689f">Abstract <span class="mandatory">*</span></label>
				   			</div>
				   			<div class="col-md-8">
				   				<textarea required="required" name="abstract" class="form-control" id="" maxlength="4000"
								rows="3" cols="53" placeholder="Maximum 4000 Chararcters" required><%if(documentSummary!=null && documentSummary[2]!=null){%><%=documentSummary[2]%><%}else{%><%}%></textarea>
				   			</div>
			   			</div>
			   	
			   			<div class="row mt-2">
				   			<div class="col-md-4">
				   				<label class="" style="font-size: 1rem;font-weight: bold;color:#07689f">Keywords <span class="mandatory">*</span></label>
				   			</div>
				   			<div class="col-md-8">
				   				<textarea required="required" name="keywords" class="form-control" id="" maxlength="4000"
								rows="3" cols="53" placeholder="Maximum 4000 Chararcters" required><%if(documentSummary!=null && documentSummary[3]!=null){%><%=documentSummary[3]%><%}else{%><%}%></textarea>
				   			</div>
   						</div>
   			
   		    			<div class="row mt-2">
				   			<div class="col-md-4">
				   				<label class="" style="font-size: 1rem;font-weight: bold;color:#07689f">Distribution <span class="mandatory">*</span></label>
				   			</div>
				   			<div class="col-md-8">
				   				<input required="required" name="distribution" class="form-control" id="" maxlength="255"
								 placeholder="Maximum 255 Chararcters" required value="<%if(documentSummary!=null && documentSummary[4]!=null){%><%=documentSummary[4]%><%}else{%><%}%>">
				   			</div>
   						</div>
   			
   						<div class="row mt-2">
   				
   				            <div class="col-md-2">
			   	 				<label class="" style="font-size: 1rem;font-weight: bold;color:#07689f">Release Date <span class="mandatory">*</span>:</label>
			   				</div>	
   				
   				 			<div class="col-md-4">
	   							<input id="pdc-date"  readonly name="pdc" <%if(documentSummary!=null && documentSummary[11]!=null){%> value="<%=fc.sdfTordf(documentSummary[11].toString()) %>" <%}%> class="form-control">
   				
   							</div>
   				
   							<div class="col-md-2 right">
			   	 				<label class="" style="font-size: 1rem;font-weight: bold;color:#07689f">Prepared By <span class="mandatory">*</span></label>
			   				</div>
			   				<div class="col-md-4">
	   							<select class="form-control selectdee"name="preparedBy" id=""data-width="100%" data-live-search="true"  required>
	          						<option value="" selected disabled>--SELECT--</option>
	        						<%for(Object[] obj: totalEmployeeList){ %>
	        							<option value="<%=obj[0].toString()%>" <%if(documentSummary!=null && documentSummary[7]!=null && documentSummary[7].toString().equalsIgnoreCase(obj[0].toString())){%>selected<%}%>>
	        								<%=obj[1].toString() %>,<%=(obj[2].toString()) %>
	        							</option>
	        						<%} %>
	        					</select>
   					      </div>
   						</div>
   				
	   					<div class="row mt-2">
				   			<div class="col-md-2">
			   	 				<label class="" style="font-size: 1rem;font-weight: bold;color:#07689f">Reviewer <span class="mandatory">*</span></label>
			   				</div>	
	   				
	   				 		<div class="col-md-4">
	   							<select class="form-control selectdee"name="reviewer" id=""data-width="100%" data-live-search="true" required>
	       		 					<option value="" selected disabled="disabled">--SELECT--</option>
	       		 					<%for(Object[] obj: totalEmployeeList){ %>
	        							<option value="<%=obj[0].toString()%>" <%if(documentSummary!=null && documentSummary[5]!=null &&  documentSummary[5].toString().equalsIgnoreCase(obj[0].toString())){%>selected<%}%>>
	        								<%=obj[1].toString() %>,<%=(obj[2].toString()) %>
	        							</option>
	        						<%} %>
	        					</select>
	   				
	   						</div>
	   				
			   				<div class="col-md-2">
						   		<label class="" style="font-size: 1rem;font-weight: bold;color:#07689f;float:right;">Approver <span class="mandatory">*</span></label>
						   	</div>	
		 					<div class="col-md-4">
		   						<select class="form-control selectdee"name="approver" id=""data-width="100%" data-live-search="true"  required>
		       						<option value="" selected disabled="disabled">--SELECT--</option>
		       						<%for(Object[] obj: totalEmployeeList){ %>
		      	 							<option value="<%=obj[0].toString()%>" <%if(documentSummary!=null && documentSummary[6]!=null && documentSummary[6].toString().equalsIgnoreCase(obj[0].toString())){%>selected<%}%>>
		       								<%=obj[1].toString() %>,<%=(obj[2].toString()) %>
		       							</option>
		       						<%} %>
		        				</select>
		 					</div>
	   					</div>
   						
	   					<div class="mt-2" align="center">
	  						<%if(iddDocumentSummaryList!=null && iddDocumentSummaryList.size()>0) {%>
	  							<button class="btn btn-sm edit" name="action" value="Edit" onclick="return confirm ('Are you sure to submit?')">UPDATE</button>
	  							<input type="hidden" name="summaryId" value="<%=documentSummary[0]%>"> 
	  						<%}else{ %>
	  							<button class="btn btn-sm submit" name="action" value="Add" onclick="return confirm('Are you sure to submit?')">SUBMIT</button>
	  						<%} %>
	   						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
							<input type="hidden" name="iddDocId" value="<%=iddDocId%>"> 
							<input type="hidden" name="docId" value="<%=iddDocId%>"> 
							<input type="hidden" name="docType" value="D"> 
	   					</div>
					</form>
        		</div>
        	</div>
    	</div>
	</div>
	<!-- -------------------------------------------- Document Summary Modal Structure End ----------------------------------- -->

<script type="text/javascript">
	
    function confirmDeletion(memberId) {
        if (confirm('Are you sure you want to delete this member?')) {
            var form = $('#deleteForm_' + memberId);
            form.submit();
            return true;
        } else {
            return false;
        }
    }

	$('#pdc-date').daterangepicker({
		
		"singleDatePicker": true,
		"showDropdowns": true,
		"cancelClass": "btn-default",
		
		<%if(documentSummary==null || documentSummary[10]==null) {%>
		"startDate":new Date() ,
		<%}%>
	
		locale: {
	    	format: 'DD-MM-YYYY'
			}
	});
	
	function showShortCodes(shortCodeType) {
		$('#shortCodeType').val(shortCodeType);
		$('#shortCodesFormBtn').click();
	}

	function showChapter1() {
		$('#introductionDetailsFormBtn').click();
	}
	
	function showChapter2() {
		$('#applicableDocumentsFormBtn').click();
	}
	
	function showChapter3(){
    	$('#designDescFormBtn').click();
	}
        
</script>    
<script type="text/javascript">
function DownloadDocPDF(){

	document.getElementById('loadingOverlay').style.display = 'flex';
	
	var chapterCount = 0;
    var mainContentCount = 0;
	var leftSideNote = '<%if(docTempAtrr!=null && docTempAtrr[12]!=null) {%><%=docTempAtrr[12].toString().replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "") %> <%} else{%>-<%}%>';
	
	var docDefinition = {
            content: [
                // Cover Page with Project Name and Logo
                {
                    text: htmlToPdfmake('<h4 class="heading-color ">Interface Design Description (IDD) <br><br> FOR  <br><br>PROJECT <%=projectShortName %> </h4>'),
                    style: 'DocumentName',
                    alignment: 'center',
                    fontSize: 18,
                    margin: [0, 200, 0, 20]
                },
                <% if (lablogo != null) { %>
                {
                    image: 'data:image/png;base64,<%= lablogo %>',
                    width: 95,
                    height: 95,
                    alignment: 'center',
                    margin: [0, 20, 0, 30]
                },
                <% } %>
                
                {
                    text: htmlToPdfmake('<h5><% if (labDetails != null && labDetails[1] != null) { %> <%= labDetails[1].toString().replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "") + "(" + labDetails[0].toString().replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "") + ")" %> <% } else { %> '-' <% } %></h5>'),
                    alignment: 'center',
                    fontSize: 16,
                    bold: true,
                    margin: [0, 20, 0, 20]
                },
                {
                    text: htmlToPdfmake('<h6>Government of India, Ministry of Defence<br>Defence Research & Development Organization </h6>'),
                    alignment: 'center',
                    fontSize: 14,
                    bold: true,
                    margin: [0, 10, 0, 10]
                },
                {
                    text: htmlToPdfmake('<h6><%if(labDetails!=null && labDetails[2]!=null && labDetails[3]!=null && labDetails[5]!=null){ %><%=labDetails[2]+" , "+labDetails[3]+", PIN-"+labDetails[5] %><%}else{ %>-<%} %></h6>'),
                    alignment: 'center',
                    fontSize: 14,
                    bold: true,
                    margin: [0, 10, 0, 10]
                },
                // Table of Contents
                {
                    toc: {
                        title: { text: 'INDEX', style: 'header', pageBreak: 'before' }
                    }
                },
                
                /* ************************************** Distribution List *********************************** */ 
                {
                    text: 'Distribution List',
                    style: 'chapterHeader',
                    tocItem: true,
                    id: 'chapter'+(++chapterCount),
                    pageBreak: 'before',
                    alignment: 'center'
                },
                {
                    table: {
                        headerRows: 1,
                        widths: ['15%', '35%', '25%', '25%'],
                        body: [
                            // Table header
                            [
                                { text: 'SN', style: 'tableHeader' },
                                { text: 'Name', style: 'tableHeader' },
                                { text: 'Designation', style: 'tableHeader' },
                                { text: 'Division/Lab', style: 'tableHeader' }
                            ],
                            // Populate table rows
                            <% if (memberList != null && memberList.size()>0) { %>
	                            <% int slno = 0; for (Object[] obj : memberList) { %>
	                            [
	                                { text: '<%= ++slno %>', style: 'tableData',alignment: 'center' },
	                                { text: '<%= obj[1] %>', style: 'tableData' },
	                                { text: '<%= obj[2] %>', style: 'tableData' },
	                                { text: '<%= obj[3] %>', style: 'tableData',alignment: 'center' }
	                            ],
	                            <% } %>
                            <% } else{%>
                            	[{ text: 'No Data Available', style: 'tableData',alignment: 'center', colSpan: 4 },]
                            <%} %>
                        ]
                    },
                    layout: {
                        /* fillColor: function(rowIndex) {
                            return (rowIndex % 2 === 0) ? '#f0f0f0' : null;
                        }, */
                        hLineWidth: function(i, node) {
                            return (i === 0 || i === node.table.body.length) ? 1 : 0.5;
                        },
                        vLineWidth: function(i) {
                            return 0.5;
                        },
                        hLineColor: function(i) {
                            return '#aaaaaa';
                        },
                        vLineColor: function(i) {
                            return '#aaaaaa';
                        },
                        paddingTop: function(i, node) { return 0; },
                        paddingBottom: function(i, node) { return 0; },
                        paddingLeft: function(i, node) { return 1; },
                        paddingRight: function(i, node) { return 1; }
                    }
                },
                /* ************************************** Distribution List End*********************************** */

                /* ************************************** Document Summary *********************************** */
                {
                    text: 'Document Summary',
                    style: 'chapterHeader',
                    tocItem: true,
                    id: 'chapter'+(++chapterCount),
                    pageBreak: 'before'
                },
                {
                    table: {
                        headerRows: 0,
                        widths: ['10%', '30%', '60%'],
                        body: [
                            <% int docsn = 0; %>
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Title', style: 'tableData' },
                                { text: 'Interface Design Description (IDD)', style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Type of Document', style: 'tableData' },
                                { text: 'Interface Design Description (IDD) Document', style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Classification', style: 'tableData' },
                                { text: 'Restricted', style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Document Number', style: 'tableData' },
                                { text: '<%=documentNo %>', style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Month Year', style: 'tableData' },
                                { text: '<%=now.getMonth().toString().substring(0,3) %> <%=now.getYear() %>', style: 'tableData' },
                            ],
                            
                            /* [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Number of Pages', style: 'tableData' },
                                { text: '', style: 'tableData' },
                            ], */
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Related Document', style: 'tableData' },
                                { text: '', style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Additional Information', style: 'tableData' },
                                { text: htmlToPdfmake('<% if(documentSummary!=null){%><%=documentSummary[1]!=null?documentSummary[1].toString().replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", ""):"-" %><%} %>'), style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Project Name', style: 'tableData' },
                                { text: '<%=projectShortName%>', style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Abstract', style: 'tableData' },
                                { text: htmlToPdfmake('<% if(documentSummary!=null){%><%=documentSummary[2]!=null?documentSummary[2].toString().replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", ""):"-" %><%} %>'), style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Keywords', style: 'tableData' },
                                { text: htmlToPdfmake('<% if(documentSummary!=null){%><%=documentSummary[3]!=null?documentSummary[3].toString().replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", ""):"-" %><%} %>'), style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Organization and address', style: 'tableData' },
                                { text: '<% if (labDetails!=null && labDetails[1] != null) {%> <%=labDetails[1].toString().replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "") + "(" + labDetails[0].toString().replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "")  + ")"%> <%} else {%> - <%}%>'
										+'\n Government of India, Ministry of Defence,Defence Research & Development Organization'
								+'<% if (labDetails!=null && labDetails[2] != null && labDetails[3] != null && labDetails[5] != null) { %>'
									+'<%=labDetails[2] + " , " + labDetails[3].toString() + ", PIN-" + labDetails[5].toString()+"."%>'
								+'<%}else{ %> - <%} %>' , style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Distribution', style: 'tableData' },
                                { text: htmlToPdfmake('<% if(documentSummary!=null){%><%=documentSummary[4]!=null?documentSummary[4].toString().replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", ""):"-" %><%} %>'), style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Revision', style: 'tableData' },
                                { text: '<%=version!=null ?version:"-" %>', style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Prepared by', style: 'tableData' },
                                { text: '<% if(documentSummary!=null){%><%=documentSummary[10]!=null?documentSummary[10]:"-" %><%} %>', style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Reviewed by', style: 'tableData' },
                                { text: '<% if(documentSummary!=null){%><%=documentSummary[9]!=null?documentSummary[9]:"-" %><%} %>', style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Approved by', style: 'tableData' },
                                { text: '<% if(documentSummary!=null){%><%=documentSummary[8]!=null?documentSummary[8]:"-" %><%} %>', style: 'tableData' },
                            ],

                        ]
                    },
                    layout: {
                        /* fillColor: function(rowIndex) {
                            return (rowIndex % 2 === 0) ? '#f0f0f0' : null;
                        }, */
                        hLineWidth: function(i, node) {
                            return (i === 0 || i === node.table.body.length) ? 1 : 0.5;
                        },
                        vLineWidth: function(i) {
                            return 0.5;
                        },
                        hLineColor: function(i) {
                            return '#aaaaaa';
                        },
                        vLineColor: function(i) {
                            return '#aaaaaa';
                        },
                        paddingTop: function(i, node) { return 0; },
                        paddingBottom: function(i, node) { return 0; },
                        paddingLeft: function(i, node) { return 1; },
                        paddingRight: function(i, node) { return 1; }
                    }
                },
			
                /* ************************************** Document Summary End *********************************** */
                
                /* ************************************** Abbreviations ****************************************** */ 
                {
                    text: 'Abbreviations',
                    style: 'chapterHeader',
                    tocItem: true,
                    id: 'chapter'+(++chapterCount),
                    pageBreak: 'before',
                    alignment: 'center'
                },
                {
                	text: 'Abbreviations used in the Manual to be listed and arranged in alphabetical order',	
                	style: 'chapterNote',
                    alignment: 'center'
                },
                
                {
                    table: {
                        headerRows: 1,
                        widths: ['20%', '30%', '50%'],
                        body: [
                            // Table header
                            [
                                { text: 'SN', style: 'tableHeader' },
                                { text: 'Abbreviation', style: 'tableHeader' },
                                { text: 'Full form', style: 'tableHeader' },
                            ],
                            // Populate table rows
                           <% if(abbreviationsLinkedList!=null && abbreviationsLinkedList.size()>0){
								int slno=0;
								for(Object[] obj : abbreviationsLinkedList){
							%>
		                            [
		                                { text: '<%=++slno %>', style: 'tableData',alignment: 'center' },
		                                { text: '<%=obj[1] %>', style: 'tableData',alignment: 'center' },
		                                { text: '<%=obj[2] %>', style: 'tableData' },
		                            ],
		                        <% } %>
                            <% } else{%>
                            	[{ text: 'No Data Available', style: 'tableData',alignment: 'center', colSpan: 3 },]
                            <%} %>
                        ]
                    },
                    layout: {
                        /* fillColor: function(rowIndex) {
                            return (rowIndex % 2 === 0) ? '#f0f0f0' : null;
                        }, */
                        hLineWidth: function(i, node) {
                            return (i === 0 || i === node.table.body.length) ? 1 : 0.5;
                        },
                        vLineWidth: function(i) {
                            return 0.5;
                        },
                        hLineColor: function(i) {
                            return '#aaaaaa';
                        },
                        vLineColor: function(i) {
                            return '#aaaaaa';
                        },
                        paddingTop: function(i, node) { return 0; },
                        paddingBottom: function(i, node) { return 0; },
                        paddingLeft: function(i, node) { return 1; },
                        paddingRight: function(i, node) { return 1; }
                    }
                },
                
                /* ************************************** Abbreviations End*********************************** */ 
                
                /* ************************************** Acronyms ************************************** */ 
                {
                    text: 'Acronyms',
                    style: 'chapterHeader',
                    tocItem: true,
                    id: 'chapter'+(++chapterCount),
                    pageBreak: 'before',
                    alignment: 'center'
                },
                {
                	text: 'Acronyms used in the Manual to be listed and arranged in alphabetical order',	
                	style: 'chapterNote',
                    alignment: 'center'
                },
                
                {
                    table: {
                        headerRows: 1,
                        widths: ['20%', '30%', '50%'],
                        body: [
                            // Table header
                            [
                                { text: 'SN', style: 'tableHeader' },
                                { text: 'Acronym', style: 'tableHeader' },
                                { text: 'Full form', style: 'tableHeader' },
                            ],
                            // Populate table rows
                           <% if(acronymsLinkedList!=null && acronymsLinkedList.size()>0){
								int slno=0;
								for(Object[] obj : acronymsLinkedList){
							%>
		                            [
		                                { text: '<%=++slno %>', style: 'tableData',alignment: 'center' },
		                                { text: '<%=obj[1] %>', style: 'tableData',alignment: 'center' },
		                                { text: '<%=obj[2] %>', style: 'tableData' },
		                            ],
		                        <% } %>
                            <% } else{%>
                            	[{ text: 'No Data Available', style: 'tableData',alignment: 'center', colSpan: 3 },]
                            <%} %>
                        ]
                    },
                    layout: {
                        /* fillColor: function(rowIndex) {
                            return (rowIndex % 2 === 0) ? '#f0f0f0' : null;
                        }, */
                        hLineWidth: function(i, node) {
                            return (i === 0 || i === node.table.body.length) ? 1 : 0.5;
                        },
                        vLineWidth: function(i) {
                            return 0.5;
                        },
                        hLineColor: function(i) {
                            return '#aaaaaa';
                        },
                        vLineColor: function(i) {
                            return '#aaaaaa';
                        },
                        paddingTop: function(i, node) { return 0; },
                        paddingBottom: function(i, node) { return 0; },
                        paddingLeft: function(i, node) { return 1; },
                        paddingRight: function(i, node) { return 1; }
                    }
                },
                
                /* ************************************** Acronyms End*********************************** */ 
                
                /* ************************************** Introduction *********************************** */
                {
                    text: (++mainContentCount)+'. Introduction',
                    style: 'chapterHeader',
                    tocItem: true,
                    id: 'chapter'+(++chapterCount),
                    pageBreak: 'before',
                },

                <%if(introductionList!=null && introductionList.size()>0) {
                	int Sub0Count = 1;
                	for(IGIDocumentIntroduction intro : introductionList) {
                		if(intro.getLevelId()==1) {
                %>
	                {
	                    text: '<%=Sub0Count+". "+intro.getChapterName()%>',
	                    style: 'chapterSubHeader',
	                    tocItem: true,
	                    tocMargin: [10, 0, 0, 0],
	                },
	                {
	                	stack: [htmlToPdfmake(setImagesWidth('<%if(intro.getChapterContent()!=null) {%><%=intro.getChapterContent().replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "") %>'
	                		  +'<%}else {%> - <%} %>', 500))],
	                    margin: [15, 0, 0, 0],
	                },
	                
	                <%
	                	int Sub1Count = 1;
	                	for(IGIDocumentIntroduction intro1 : introductionList) {
	                		if(intro1.getLevelId()==2 && intro1.getParentId().equals(intro.getIntroductionId())) {
	                %>
	                
		                {
		                    text: '<%=Sub0Count+". "+Sub1Count+". "+intro1.getChapterName()%>',
		                    style: 'chapterSubSubHeader',
		                    tocItem: true,
		                    tocMargin: [20, 0, 0, 0],
		                },
		                {
		                	stack: [htmlToPdfmake(setImagesWidth('<%if(intro1.getChapterContent()!=null) {%><%=intro1.getChapterContent().replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "") %>'
		                		  +'<%}else {%> - <%} %>', 500))],
		                    margin: [25, 0, 0, 0],
		                },
	                <%++Sub1Count;} }%>
	                
                <%++Sub0Count; } } }else{%>
	                {
	                    text: '-',
	                    margin: [10, 0, 0, 0],
	                },
                <%} %>
                /* ************************************** Introduction End *********************************** */
                
                /* ************************************** Applicable Documents *********************************** */
                {
                    text: (++mainContentCount)+'. Applicable Documents',
                    style: 'chapterHeader',
                    tocItem: true,
                    id: 'chapter'+(++chapterCount),
                    pageBreak: 'before',
                },

                {
                    table: {
                        headerRows: 1,
                        widths: ['20%', '60%', '20%'],
                        body: [
                            // Table header
                            [
                                { text: 'SN', style: 'tableHeader' },
                                { text: 'Document Name', style: 'tableHeader' },
                                { text: 'Action', style: 'tableHeader' },
                            ],
                            // Populate table rows
                           <% if(iddApplicableDocsList!=null && iddApplicableDocsList.size()>0){
								int slno=0;
								for(Object[] obj : iddApplicableDocsList){
							%>
		                            [
		                                { text: '<%=++slno %>', style: 'tableData',alignment: 'center' },
		                                { text: '<%=obj[2] %>', style: 'tableData', },
		                                {
		                                    text: 'View',
		                                    style: 'tableData',
		                                    link: '<%= request.getRequestURL().toString().replace(request.getRequestURI(), "") + request.getContextPath() + "/StandardDocumentsDownload.htm?StandardDocumentId=" + obj[0] %>',
		                                    color: 'blue',
		                                    decoration: 'underline',
		                                    alignment: 'center',
		                                },
		                            ],
		                        <% } %>
                            <% } else{%>
                            	[{ text: 'No Data Available', style: 'tableData',alignment: 'center', colSpan: 3 },]
                            <%} %>
                        ]
                    },
                    layout: {
                        /* fillColor: function(rowIndex) {
                            return (rowIndex % 2 === 0) ? '#f0f0f0' : null;
                        }, */
                        hLineWidth: function(i, node) {
                            return (i === 0 || i === node.table.body.length) ? 1 : 0.5;
                        },
                        vLineWidth: function(i) {
                            return 0.5;
                        },
                        hLineColor: function(i) {
                            return '#aaaaaa';
                        },
                        vLineColor: function(i) {
                            return '#aaaaaa';
                        },
                        paddingTop: function(i, node) { return 0; },
                        paddingBottom: function(i, node) { return 0; },
                        paddingLeft: function(i, node) { return 1; },
                        paddingRight: function(i, node) { return 1; }
                    }
                },
                /* ************************************** Applicable Documents End *********************************** */
                
                /* ************************************** Interface Design *********************************** */
                <% int designslno = 0;%>
                {
                    text: (++mainContentCount)+'. Interface Design',
                    style: 'chapterHeader',
                    tocItem: true,
                    id: 'chapter'+(++chapterCount),
                    pageBreak: 'before'
                },
                
                /* ***************************** Interface Message Description ***************************** */

                {
                    text: (mainContentCount)+'.<%=++designslno%>. Interface Message Description',
                    style: 'chapterSubHeader',
                    tocItem: true,
                    id: 'chapter'+(chapterCount)+'.'+mainContentCount+'.<%=designslno%>',
                    tocMargin: [10, 0, 0, 0],
                },
                
                {
                    text: (mainContentCount)+'.<%=designslno%>.1. Message Structure',
                    style: 'chapterSubSubHeader',
                    tocItem: true,
                    id: 'chapter'+(chapterCount)+'.'+mainContentCount+'.<%=designslno%>.1',
                    tocMargin: [20, 0, 0, 0],
                },
                {
                    table: {
                        headerRows: 1,
                        widths: [<% for (int i = 0; i <18; i++) { %>'auto',<% } %>],
                        body: [
                            // Table header
                            [
                                { text: '  ', style: 'tableHeader2' },
                                <% for (int i = 15; i >=0; i--) { %>
                                	{ text: 'b<%=i%>', style: 'tableHeader2' },
                                <% } %>
                                { text: 'Pos', style: 'tableHeader2' },
                            ],
                            [
                                { text: '0', style: 'tableData2', alignment: 'center', },
                                { text: 'Header', style: 'tableData2', colSpan: 16, rowSpan: 2, alignment: 'center' }, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                                { text: '0', style: 'tableData2', alignment: 'center', }
                            ],
                            [
                                { text: '2', style: 'tableData2', alignment: 'center', },
                                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                                { text: '2', style: 'tableData2', alignment: 'center', }
                            ],
                            [
                                { text: '4', style: 'tableData2', alignment: 'center', },
                                { text: 'Message Data', style: 'tableData2', colSpan: 16, rowSpan: 2, alignment: 'center', }, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                                { text: '4', style: 'tableData2', alignment: 'center', }
                            ],
                            [
                                { text: '6', style: 'tableData2', alignment: 'center', },
                                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                                { text: '6', style: 'tableData2', alignment: 'center', }
                            ],
                            [
                                { text: 'End', style: 'tableData2', alignment: 'center', },
                                { text: '', style: 'tableData2', colSpan: 16, alignment: 'center', }, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                                { text: 'End', style: 'tableData2', alignment: 'center', }
                            ],
                        ]
                    },
                    layout: {
                        hLineWidth: function (i, node) { return 1; },
                        vLineWidth: function (i, node) { return 1; },
                        hLineColor: function (i, node) { return '#aaaaaa'; },
                        vLineColor: function (i, node) { return '#aaaaaa'; },
                        paddingLeft: function(i, node) { return 2; },
                        paddingRight: function(i, node) { return 2; },
                    },
                    margin: [20, 0, 0, 0],
                },
                
                { text: '\n',},
                
                {
                    text: (mainContentCount)+'.<%=designslno%>.2. Header Format',
                    style: 'chapterSubSubHeader',
                    tocItem: true,
                    id: 'chapter'+(chapterCount)+'.'+mainContentCount+'.<%=designslno%>.2',
                    tocMargin: [20, 0, 0, 0],
                },
                {
                    table: {
                        headerRows: 1,
                        widths: [<% for (int i = 0; i <18; i++) { %>'auto',<% } %>],
                        body: [
                            // Table header
                            [
                                { text: '  ', style: 'tableHeader2' },
                                <% for (int i = 15; i >=0; i--) { %>
                                	{ text: 'b<%=i%>', style: 'tableHeader2' },
                                <% } %>
                                { text: 'Pos', style: 'tableHeader2' },
                            ],
                            [
                                { text: '0', style: 'tableData2', alignment: 'center', },
                                { text: 'bySourceID', style: 'tableData2', colSpan: 8, alignment: 'center' }, 
                                <% for (int i = 0; i < 7; i++) { %> null,<% } %>
                                { text: 'byDestinationID', style: 'tableData2', colSpan: 8, alignment: 'center' }, 
                                <% for (int i = 0; i < 7; i++) { %> null,<% } %>
                                { text: '0', style: 'tableData2', alignment: 'center', }
                            ],
                            [
                                { text: '2', style: 'tableData2', alignment: 'center', },
                                { text: 'byChecksum', style: 'tableData2', colSpan: 8, alignment: 'center' }, 
                                <% for (int i = 0; i < 7; i++) { %> null,<% } %>
                                { text: 'byMsgId', style: 'tableData2', colSpan: 8, alignment: 'center' }, 
                                <% for (int i = 0; i < 7; i++) { %> null,<% } %>
                                { text: '2', style: 'tableData2', alignment: 'center', }
                            ],
                            [
                                { text: '4', style: 'tableData2', alignment: 'center', },
                                { text: 'hwSerialNo', style: 'tableData2', colSpan: 16, alignment: 'center', }, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                                { text: '4', style: 'tableData2', alignment: 'center', }
                            ],
                        ]
                    },
                    layout: {
                        hLineWidth: function (i, node) { return 1; },
                        vLineWidth: function (i, node) { return 1; },
                        hLineColor: function (i, node) { return '#aaaaaa'; },
                        vLineColor: function (i, node) { return '#aaaaaa'; },
                        paddingLeft: function(i, node) { return 2; },
                        paddingRight: function(i, node) { return 2; },
                    },
                    margin: [20, 0, 0, 0],
                },
                
                /* ***************************** Interface Message Description ******************************** */

                /* ***************************** Message Summary ***************************** */
				{ text: '\n',},
                {
                    text: (mainContentCount)+'.<%=++designslno%>. Message Summary',
                    style: 'chapterSubHeader',
                    tocItem: true,
                    id: 'chapter'+(chapterCount)+'.'+mainContentCount+'.<%=designslno%>',
                    tocMargin: [10, 0, 0, 0],
                },
                
                {
                    table: {
                        headerRows: 1,
                        widths: ['10%', '44%', '46%'],
                        body: [
                            // Table header
                            [
                                { text: 'SN', style: 'tableHeader' },
                                { text: 'Message Id', style: 'tableHeader' },
                                { text: 'Message', style: 'tableHeader' },
                            ],
                            // Populate table rows
                           	<%if(irsSpecificationsList!=null && irsSpecificationsList.size()>0) {
			                	int slno=0;
			                	for(Object[] obj : irsSpecificationsList) {
			               	%>
		                            [
		                                { text: '<%=++slno %>', style: 'tableData',alignment: 'center' },
		                                { text: '<%=obj[7] %>', style: 'tableData', },
		                                { text: '<%=obj[8] %>', style: 'tableData', },
		                            ],
		                        <% } %>
                            <% } else{%>
                            	[{ text: 'No Data Available', style: 'tableData',alignment: 'center', colSpan: 3 },]
                            <%} %>
                        ]
                    },
                    layout: {
                        /* fillColor: function(rowIndex) {
                            return (rowIndex % 2 === 0) ? '#f0f0f0' : null;
                        }, */
                        hLineWidth: function(i, node) {
                            return (i === 0 || i === node.table.body.length) ? 1 : 0.5;
                        },
                        vLineWidth: function(i) {
                            return 0.5;
                        },
                        hLineColor: function(i) {
                            return '#aaaaaa';
                        },
                        vLineColor: function(i) {
                            return '#aaaaaa';
                        },
                        paddingTop: function(i, node) { return 0; },
                        paddingBottom: function(i, node) { return 0; },
                        paddingLeft: function(i, node) { return 1; },
                        paddingRight: function(i, node) { return 1; }
                    }
                },
                { text: '\n',},
                /* ***************************** Message Summary ***************************** */

                /* ************************** Communication Establishment Messages End ************************ */

                {
                    text: (mainContentCount)+'.<%=++designslno%>. Communication Establishment Messages',
                    style: 'chapterSubHeader',
                    tocItem: true,
                    id: 'chapter'+(chapterCount)+'.'+mainContentCount+'.<%=designslno%>',
                    tocMargin: [10, 0, 0, 0],
                },
                
                <%if(irsSpecificationsList!=null && irsSpecificationsList.size()>0) {
                	int specCount=0;
                	for(Object[] obj : irsSpecificationsList) {
                		int sn = 0;
                		IGILogicalInterfaces iface = logicalInterfaceList.stream().filter(e -> e.getLogicalInterfaceId()==Long.parseLong(obj[3].toString())).findFirst().orElse(null);
                		String[] split = obj[6].toString().split("-");
			        	IGILogicalChannel channel = logicalChannelList.stream().filter(e -> e.getLogicalChannelId().equals(iface.getLogicalChannelId())).findFirst().orElse(null);
			        	List<Object[]> fieldDescList =  fieldDescriptionList.stream().filter(e -> iface.getLogicalInterfaceId()== Long.parseLong(e[1].toString()) && obj[0].toString().equalsIgnoreCase(e[19].toString())).collect(Collectors.toList());
			        	
                %>
	                {
	                	text: mainContentCount+'.<%=designslno%>.<%=++specCount%>. <%=iface.getMsgCode() %>',	
	                	style: 'chapterSubSubHeader',
	                    tocItem: true,
	                    id: 'chapter'+chapterCount+'.'+mainContentCount+'.<%=designslno%>.<%=specCount%>',
	                    tocMargin: [20, 0, 0, 0],
	                },
	                
	                {
	                	table: {
	                        headerRows: 1,
	                        widths: ['10%', '25%', '65%'],
	                        body: [
	                            // Table header
	                            [
	                                { text: 'SN', style: 'tableHeader' },
	                                { text: 'Section', style: 'tableHeader' },
	                                { text: 'Details', style: 'tableHeader' },
	                            ],
	                            
	                            // Populate table rows
	                            [
	                            	{ text: '<%=++sn%>', style: 'tableData', bold: true, alignment: 'center',},
	                                { text: 'Msg Id', style: 'tableData', bold: true},
	                                { text: '<%=iface.getMsgCode() %>', style: 'tableData' },
	                            ],
	                            [
	                            	{ text: '<%=++sn%>', style: 'tableData', bold: true, alignment: 'center',},
	                                { text: 'Logical Channel Name', style: 'tableData', bold: true},
	                                { text: '<%=channel.getLogicalChannel() + " (" + channel.getChannelCode() + ")" %>', style: 'tableData' },
	                            ],
	                            [
	                            	{ text: '<%=++sn%>', style: 'tableData', bold: true, alignment: 'center',},
	                                { text: 'Msg Name', style: 'tableData', bold: true},
	                                { text: '<%=iface.getMsgName() %>', style: 'tableData' },
	                            ],
	                            
	                            [
	                            	{ text: '<%=++sn%>', style: 'tableData', bold: true, alignment: 'center',},
	                                { text: 'Msg Type', style: 'tableData', bold: true},
	                                { text: '<%=iface.getMsgType() %> (<%=iface.getMsgType().equalsIgnoreCase("Command")?"CMD": iface.getMsgType().substring(0, 3).toUpperCase() %>)', style: 'tableData' },
	                            ],
	                            [
	                            	{ text: '<%=++sn%>', style: 'tableData', bold: true, alignment: 'center',},
	                                { text: 'Msg Length (In Bytes)', style: 'tableData', bold: true},
	                                { text: '<%=iface.getMsgLength() %>', style: 'tableData' },
	                            ],
	                            [
	                            	{ text: '<%=++sn%>', style: 'tableData', bold: true, alignment: 'center',},
	                                { text: 'Msg No', style: 'tableData', bold: true},
	                                { text: '<%=iface.getMsgNo() %>', style: 'tableData' },
	                            ],
	                            [
	                            	{ text: '<%=++sn%>', style: 'tableData', bold: true, alignment: 'center',},
	                                { text: 'Description', style: 'tableData', bold: true},
	                                { text: '<%=(iface.getMsgDescription()!=null && !iface.getMsgDescription().isEmpty())? iface.getMsgDescription():"-" %>', style: 'tableData' },
	                            ],
	                            [
	                            	{ text: '<%=++sn%>', style: 'tableData', bold: true, alignment: 'center',},
	                                { text: 'Data Rate / Periodicity (In Sec)', style: 'tableData', bold: true},
	                                { text: '<%=(iface.getDataRate()!=null && !iface.getDataRate().isEmpty())? iface.getDataRate():"-" %>', style: 'tableData' },
	                            ],
	                            [
	                            	{ text: '<%=++sn%>', style: 'tableData', bold: true, alignment: 'center',},
	                                { text: 'Source', style: 'tableData', bold: true},
	                                { text: '<%=productTreeAllListMap.get(split[0].split("_")[0])%> (<%=split[0] %>)', style: 'tableData' },
	                            ],
	                            [
	                            	{ text: '<%=++sn%>', style: 'tableData', bold: true, alignment: 'center',},
	                                { text: 'Destination', style: 'tableData', bold: true},
	                                { text: '<%=productTreeAllListMap.get(split[1].split("_")[0])%> (<%=split[1] %>)', style: 'tableData' },
	                            ],
	                            [
	                            	{ text: '<%=++sn%>', style: 'tableData', bold: true, alignment: 'center',},
	                                { text: 'Action at Destination', style: 'tableData', bold: true},
	                                {
	        		                	stack: [htmlToPdfmake(setImagesWidth('<%if(obj[5]!=null) {%><%=obj[5].toString().replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "") %>'
	        		                		  +'<%}else {%> - <%} %>', 500))],
	        		                },
	                            ],
	                            [
	                            	{ text: '<%=++sn%>', style: 'tableData', bold: true, alignment: 'center',},
	                                { text: 'Underlying Protocols', style: 'tableData', bold: true},
	                                { text: '<%=(iface.getProtocals()!=null && !iface.getProtocals().isEmpty())? iface.getProtocals():"-" %>', style: 'tableData' },
	                            ],
	                            [
	                            	{ text: '<%=++sn%>', style: 'tableData', bold: true, alignment: 'center',},
	                            	{ text: 'Additional Info / Remarks', style: 'tableData', bold: true},
	                                { text: '<%=(iface.getAdditionalInfo()!=null && !iface.getAdditionalInfo().isEmpty())? iface.getAdditionalInfo():"-" %>', style: 'tableData' },
	                            ],
	                            <%-- [
	                            	{ text: '<%=++sn%>', style: 'tableData', bold: true, alignment: 'center',},
	                                { text: 'Fields', style: 'tableData', bold: true},
	                                { text: '<%for(Object[] field : fieldDescList) { %><%=field[8] %>, <%} %> ', style: 'tableData' },
	                            ], --%>
	                        ]
	                    },
	                    layout: {
	                        /* fillColor: function(rowIndex) {
	                            return (rowIndex % 2 === 0) ? '#f0f0f0' : null;
	                        }, */
	                        hLineWidth: function(i, node) {
	                            return (i === 0 || i === node.table.body.length) ? 1 : 0.5;
	                        },
	                        vLineWidth: function(i) {
	                            return 0.5;
	                        },
	                        hLineColor: function(i) {
	                            return '#aaaaaa';
	                        },
	                        vLineColor: function(i) {
	                            return '#aaaaaa';
	                        },
	                        paddingTop: function(i, node) { return 0; },
	                        paddingBottom: function(i, node) { return 0; },
	                        paddingLeft: function(i, node) { return 1; },
	                        paddingRight: function(i, node) { return 1; }
	                    }
	                },
	                { text: '\n',},

	                {
	                    text: 'Message Format',
	                    style: 'chapterSubSubHeader',
	                },
	                {
	                    table: {
	                        headerRows: 1,
	                        widths: [<% for (int i = 0; i <18; i++) { %>'auto',<% } %>],
	                        body: [
	                            // Table header
	                            [
	                                { text: 'Pos', style: 'tableHeader2' },
	                                <% for (int i = 15; i >=0; i--) { %>
	                                	{ text: 'b<%=i%>', style: 'tableHeader2' },
	                                <% } %>
	                                { text: 'Pos', style: 'tableHeader2' },
	                            ],
	                            
	                            <% if(fieldDescList!=null && fieldDescList.size()>0) {
	                            	int posCount = 0, availableBits = 16, slno = 1;
	                            	boolean newRow = true;
									for(Object[] field : fieldDescList){
										int dataLength = Integer.parseInt(field[4].toString());
										int rowSpanCount = 1;
								%>
									// New Row to be generated for the first time
									<% if(newRow) { %>
		                            	[
		                            		{ text: '<%=posCount%>', style: 'tableData2', alignment: 'center', },
		                            <% } %>
		                            
		                            // Handling the existing row with ??
									<% if( dataLength > availableBits && !newRow) { %>
										<%if(availableBits>0) {%>
											{ text: '??', style: 'tableData2', colSpan: <%=availableBits%>, alignment: 'center' }, 
			                                <% for (int i = 1; i < availableBits; i++) { %> null,<% } %>
											{ text: '<%=posCount%>', style: 'tableData2', alignment: 'center', }
											
										],
	                                	<%} %>
	                                	<% posCount+=2;
		                            	availableBits = 16;%>
		                            	[
		                            		{ text: '<%=posCount%>', style: 'tableData2', alignment: 'center', },
		                            <% } %>
		                            
		                            // Calculation for the available columns (bits), row span calculations for more than 16 bits
		                            <% 
			                            availableBits = availableBits - dataLength; 
			                            newRow = false;
			                            rowSpanCount = dataLength / 16;
		                            %>
	                            
									// Filling the field name
	                                { text: '<%=field[8]%>', style: 'tableData2', alignment: 'center' 
	                                <%if(rowSpanCount>1) {%> , colSpan: 16, rowSpan: <%=rowSpanCount%> <%} else {%> , colSpan: <%=dataLength%><%}%> }, 
	                                <% for (int i = 1; i < (dataLength>16?16:dataLength); i++) { %> null,<% } %>
									
	                                // If the field has more than 16 bits handling the rowspan
	                                <%if(rowSpanCount>1) {
	                                %>
	                                	{ text: '<%=posCount%>', style: 'tableData2', alignment: 'center', },
	                                ],
	                                <%for(int i=1; i<rowSpanCount; i++) {
	                                		posCount+=2;
	                                %>
		                                [
		                                    { text: '<%=posCount%>', style: 'tableData2', alignment: 'center', },
		                                    null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
		                                    { text: '<%=posCount%>', style: 'tableData2', alignment: 'center', }
		                                ],
	                                <% } } %>
	                                
	                                // At the end if the field is not present filling ?? by default
	                                <%if(slno==fieldDescList.size() && availableBits>0) {%>
			                                { text: '??', style: 'tableData2', colSpan: <%=availableBits%>, alignment: 'center' }, 
			                                <% for (int i = 1; i < availableBits; i++) { %> null,<% } %>
											{ text: '<%=posCount%>', style: 'tableData2', alignment: 'center', }
										],
	                                <%} %>
	                                
	                            	// close the row if the available bits are zero.
	                                <% if(availableBits==0) { %>
	                                		{ text: '<%=posCount%>', style: 'tableData2', alignment: 'center', }
	                                	],
	                                <% posCount+=2;
		                            	availableBits = 16;
	                                	newRow = true;
	                                } %>
	                            
	                            <% ++slno; } } %>
	                            
	                        ]
	                    },
	                    layout: {
	                        hLineWidth: function (i, node) { return 1; },
	                        vLineWidth: function (i, node) { return 1; },
	                        hLineColor: function (i, node) { return '#aaaaaa'; },
	                        vLineColor: function (i, node) { return '#aaaaaa'; },
	                        paddingLeft: function(i, node) { return 2; },
	                        paddingRight: function(i, node) { return 2; },
	                    },
	                    margin: [20, 0, 0, 0],
	                },
	                
	                <%-- {
	                    text: mainContentCount+'. <%=specCount%>. Field Description',
	                    style: 'chapterSubHeader',
	                    tocItem: true,
	                    id: 'chapter'+(++chapterCount),
	                    tocMargin: [25, 0, 0, 0],
	                },
	                <% if(fieldDescList!=null && fieldDescList.size()>0){
						int slno=0;
						for(Object[] field : fieldDescList){
							int snf = 0;
					%>
					
						{
		                	text: mainContentCount+'. <%=specCount%>. <%=++slno%>. <%=field[8] %>',	
		                	style: 'chapterSubSubHeader',
		                    tocItem: true,
		                    id: 'chapter'+chapterCount+'.'+mainContentCount+'. <%=specCount%>. <%=slno%>',
		                    tocMargin: [30, 0, 0, 0],
		                },
		                
		                {
		                    table: {
		                        headerRows: 1,
		                        widths: ['10%', '30%', '60%'],
		                        body: [
		                            // Table header
		                            [
		                                { text: 'SN', style: 'tableHeader' },
		                                { text: 'Section', style: 'tableHeader' },
		                                { text: 'Details', style: 'tableHeader' },
		                            ],
		                            // Populate table rows
		                            [
		                            	{ text: '<%=++snf%>', style: 'tableData', bold: true, alignment: 'center',},
		                                { text: 'Field Code', style: 'tableData', bold: true},
		                                { text: '<%=field[10] %>', style: 'tableData' },
		                            ],
		                            [
		                            	{ text: '<%=++snf%>', style: 'tableData', bold: true, alignment: 'center',},
		                                { text: 'Field Short Name', style: 'tableData', bold: true},
		                                { text: '<%=field[9] %>', style: 'tableData' },
		                            ],
		                            [
		                            	{ text: '<%=++snf%>', style: 'tableData', bold: true, alignment: 'center',},
		                                { text: 'Field Name', style: 'tableData', bold: true},
		                                { text: '<%=field[8] %>', style: 'tableData' },
		                            ],
		                            [
		                            	{ text: '<%=++snf%>', style: 'tableData', bold: true, alignment: 'center',},
		                                { text: 'Field Description', style: 'tableData', bold: true},
		                                { text: '<%=field[5]!=null && !field[5].toString().isEmpty()?field[5] : "-" %>', style: 'tableData' },
		                            ],
		                            [
		                            	{ text: '<%=++snf%>', style: 'tableData', bold: true, alignment: 'center',},
		                                { text: 'Data type (bits)', style: 'tableData', bold: true},
		                                { text: '<%=field[27] %> (<%=field[4] %>)', style: 'tableData' },
		                            ],
		                            [
		                            	{ text: '<%=++snf%>', style: 'tableData', bold: true, alignment: 'center',},
		                                { text: 'Typical Value', style: 'tableData', bold: true},
		                                { text: '<%=field[13] %>', style: 'tableData' },
		                            ],
		                            [
		                            	{ text: '<%=++snf%>', style: 'tableData', bold: true, alignment: 'center',},
		                                { text: 'Min Value', style: 'tableData', bold: true},
		                                { text: '<%=field[14] %>', style: 'tableData' },
		                            ],
		                            [
		                            	{ text: '<%=++snf%>', style: 'tableData', bold: true, alignment: 'center',},
		                                { text: 'Max Value', style: 'tableData', bold: true},
		                                { text: '<%=field[15] %>', style: 'tableData' },
		                            ],
		                            [
		                            	{ text: '<%=++snf%>', style: 'tableData', bold: true, alignment: 'center',},
		                                { text: 'Init Value', style: 'tableData', bold: true},
		                                { text: '<%=field[16] %>', style: 'tableData' },
		                            ],
		                            [
		                            	{ text: '<%=++snf%>', style: 'tableData', bold: true, alignment: 'center',},
		                                { text: 'Field Offset', style: 'tableData', bold: true},
		                                { text: '<%=field[17]!=null?field[17]:"-" %>', style: 'tableData' },
		                            ],
		                            [
		                            	{ text: '<%=++snf%>', style: 'tableData', bold: true, alignment: 'center',},
		                                { text: 'Quantum', style: 'tableData', bold: true},
		                                { text: '<%=field[6] %>', style: 'tableData' },
		                            ],
		                            [
		                            	{ text: '<%=++snf%>', style: 'tableData', bold: true, alignment: 'center',},
		                                { text: 'Unit', style: 'tableData', bold: true},
		                                { text: '<%=field[18] %>', style: 'tableData' },
		                            ],
		                            [
		                            	{ text: '<%=++snf%>', style: 'tableData', bold: true, alignment: 'center',},
		                                { text: 'Remarks', style: 'tableData', bold: true},
		                                { text: '<%=field[7]!=null && !field[7].toString().isEmpty()?field[7] : "-" %>', style: 'tableData' },
		                            ],
		                        ]
		                    },
		                    layout: {
		                        /* fillColor: function(rowIndex) {
		                            return (rowIndex % 2 === 0) ? '#f0f0f0' : null;
		                        }, */
		                        hLineWidth: function(i, node) {
		                            return (i === 0 || i === node.table.body.length) ? 1 : 0.5;
		                        },
		                        vLineWidth: function(i) {
		                            return 0.5;
		                        },
		                        hLineColor: function(i) {
		                            return '#aaaaaa';
		                        },
		                        vLineColor: function(i) {
		                            return '#aaaaaa';
		                        }
		                    }
		                },
		                
		                { text: '\n\n',},
	                <% } }%> --%>
                <%} }%>
                /* **************************** Communication Establishment Messages End ******************** */

                /* ************************************** Interface Design End *********************************** */
                
                /* ************************************** Field Description *********************************** */
                <%
                	Set<Long> logicalInterfaceIds =  irsSpecificationsList.stream().map(e -> Long.parseLong(e[3].toString())).collect(Collectors.toSet());
					List<Object[]> fieldDescList =  fieldDescriptionList.stream().filter(e -> logicalInterfaceIds.contains(Long.parseLong(e[1].toString())) ).collect(Collectors.toList());
					fieldDescList = fieldDescList.stream()
						    .collect(Collectors.groupingBy(e -> e[2]))
						    .values().stream()
						    .map(list -> list.get(0))
						    .collect(Collectors.toList());

                %>
                {
                    text: (++mainContentCount)+'. Field Description',
                    style: 'chapterHeader',
                    tocItem: true,
                    id: 'chapter'+(++chapterCount),
                    pageBreak: 'before'
                },
                <% if(fieldDescList!=null && fieldDescList.size()>0){
					int slno=0;
					for(Object[] field : fieldDescList){
						int sn = 0;
						String fieldName = field[8] != null ? field[8].toString().replaceAll("\\s+", "") : "unknownField";
				%>
				
					{
	                	text: mainContentCount+'.<%=++slno%>. <%=field[8] %>',	
	                	style: 'chapterSubHeader',
	                    tocItem: true,
	                    id: 'chapter'+chapterCount+'.'+mainContentCount+'.<%=slno%>',
	                    tocMargin: [10, 0, 0, 0],
	                },
	                
	                {
	                    table: {
	                        headerRows: 1,
	                        widths: ['10%', '30%', '60%'],
	                        body: [
	                            // Table header
	                            [
	                                { text: 'SN', style: 'tableHeader' },
	                                { text: 'Section', style: 'tableHeader' },
	                                { text: 'Details', style: 'tableHeader' },
	                            ],
	                            // Populate table rows
	                            <%-- [
	                            	{ text: '<%=++sn%>', style: 'tableData', bold: true, alignment: 'center',},
	                                { text: 'Field Code', style: 'tableData', bold: true},
	                                { text: '<%=field[10] %>', style: 'tableData' },
	                            ],
	                            [
	                            	{ text: '<%=++sn%>', style: 'tableData', bold: true, alignment: 'center',},
	                                { text: 'Field Short Name', style: 'tableData', bold: true},
	                                { text: '<%=field[9] %>', style: 'tableData' },
	                            ], --%>
	                            [
	                            	{ text: '<%=++sn%>', style: 'tableData', bold: true, alignment: 'center',},
	                                { text: 'Field Name', style: 'tableData', bold: true},
	                                { text: '<%=field[11]+""+fieldName %>', style: 'tableData' },
	                            ],
	                            [
	                            	{ text: '<%=++sn%>', style: 'tableData', bold: true, alignment: 'center',},
	                                { text: 'Field Description', style: 'tableData', bold: true},
	                                { text: '<%=field[5]!=null && !field[5].toString().isEmpty()?field[5] : "-" %>', style: 'tableData' },
	                            ],
	                            [
	                            	{ text: '<%=++sn%>', style: 'tableData', bold: true, alignment: 'center',},
	                                { text: 'Data type (Bits)', style: 'tableData', bold: true},
	                                { text: '<%=field[27] %> (<%=field[4] %>)', style: 'tableData' },
	                            ],
	                            [
	                            	{ text: '<%=++sn%>', style: 'tableData', bold: true, alignment: 'center',},
	                            	{ text: '[Init, Typical, Min, Max]', style: 'tableData', bold: true },
	                                { text: '[<%=field[16] %>, <%=field[13] %>, <%=field[14] %>, <%=field[15] %>]', style: 'tableData', bold: true },
	                            ],
	                            [
	                            	{ text: '<%=++sn%>', style: 'tableData', bold: true, alignment: 'center',},
	                                { text: 'Quantum / LSB', style: 'tableData', bold: true},
	                                { text: '<%=field[6] %>', style: 'tableData' },
	                            ],
	                            [
	                            	{ text: '<%=++sn%>', style: 'tableData', bold: true, alignment: 'center',},
	                                { text: 'Unit', style: 'tableData', bold: true},
	                                { text: '<%=field[18] %>', style: 'tableData' },
	                            ],
	                            [
	                            	{ text: '<%=++sn%>', style: 'tableData', bold: true, alignment: 'center',},
	                                { text: 'Remarks', style: 'tableData', bold: true},
	                                { text: '<%=field[7]!=null && !field[7].toString().isEmpty()?field[7] : "-" %>', style: 'tableData' },
	                            ],
	                        ]
	                    },
	                    layout: {
	                        /* fillColor: function(rowIndex) {
	                            return (rowIndex % 2 === 0) ? '#f0f0f0' : null;
	                        }, */
	                        hLineWidth: function(i, node) {
	                            return (i === 0 || i === node.table.body.length) ? 1 : 0.5;
	                        },
	                        vLineWidth: function(i) {
	                            return 0.5;
	                        },
	                        hLineColor: function(i) {
	                            return '#aaaaaa';
	                        },
	                        vLineColor: function(i) {
	                            return '#aaaaaa';
	                        },
	                        paddingTop: function(i, node) { return 0; },
	                        paddingBottom: function(i, node) { return 0; },
	                        paddingLeft: function(i, node) { return 1; },
	                        paddingRight: function(i, node) { return 1; }
	                    }
	                },
	                
	                { text: '\n\n',},
                <% } }%>
                /* ************************************** Field Description End *********************************** */
			],
			styles: {
                DocumentName: { fontSize: 18, bold: true, margin: [0, 0, 0, 10] },
                chapterHeader: { fontSize: 16, bold: true, margin: [0, 0, 0, 10] },
                chapterNote: { fontSize: 13, bold: true, margin: [0, 10, 0, 10]},
                chapterSubHeader: { fontSize: 13, bold: true, margin: [10, 10, 0, 10]},
                tableHeader: { fontSize: 12, bold: true, fillColor: '#f0f0f0', alignment: 'center', margin: [10, 5, 10, 5], fontWeight: 'bold' },
                tableData: { fontSize: 11.5, margin: [0, 5, 0, 5] },
                tableHeader2: { fontSize: 9, bold: true, fillColor: '#f0f0f0', alignment: 'center', margin: [5, 0, 5, 0], fontWeight: 'bold' },
                tableData2: { fontSize: 8.5, margin: [0, 5, 0, 5] },
                chapterSubSubHeader: { fontSize: 12, bold: true, margin: [15, 10, 10, 10] },
                chapterSubSubSubHeader: { fontSize: 12, bold: true, margin: [20, 10, 10, 10] },
                subChapterNote: { margin: [15, 15, 0, 10] },
                header: { alignment: 'center', bold: true},
                chapterContent: {fontSize: 11.5, margin: [0, 5, 0, 5] },
            },
            info: {
            	title : 'IDD_Document',
            },
            footer: function (currentPage, pageCount, pageSize) {
                const isLandscape = pageSize.width > pageSize.height;
                const pageWidth = pageSize.width; // Adjust dynamically based on page size
                const margin = 30;

                if (currentPage > 0) {
                    return {
                        stack: [
                            {
                                canvas: [{ type: 'line', x1: margin, y1: 0, x2: pageWidth - margin, y2: 0, lineWidth: 1 }]
                            },
                            {
                                columns: [
                                    {
                                        text: '<%if(documentNo!=null) {%><%=documentNo %><%} %>',
                                        alignment: 'left',
                                        margin: [margin, 0, 0, 0],
                                        fontSize: 8
                                    },
                                    {
                                        text: 'Restricted',
                                        alignment: 'center',
                                        fontSize: 8,
                                        margin: [0, 0, 0, 0],
                                        bold: true
                                    },
                                    {
                                    	text: currentPage.toString() + ' of ' + pageCount,
                                        alignment: 'right',
                                        margin: [0, 0, margin, 0],
                                        fontSize: 8
                                    }
                                ]
                            }
                        ]
                    };
                }
                return '';
            },
            header: function (currentPage) {
                return {
                    stack: [
                        
                        {
                            columns: [
                                {
                                    // Left: Lab logo
                                    image: '<%= lablogo != null ? "data:image/png;base64," + lablogo : "" %>',
                                    width: 30,
                                    height: 30,
                                    alignment: 'left',
                                    margin: [35, 10, 0, 10]
                                },
                                {
                                    // Center: Text
                                    text: 'Restricted',
                                    alignment: 'center',
                                    fontSize: 10,
                                    bold: true,
                                    margin: [0, 10, 0, 0]
                                },
                                {
                                    // Right: DRDO logo
                                    image: '<%=drdologo != null ? "data:image/png;base64," + drdologo : "" %>',
                                    width: 30,
                                    height: 30,
                                    alignment: 'right',
                                    margin: [0, 10, 20, 10]
                                }
                            ]
                        },
                        
                    ]
                };
            },
			pageMargins: [50, 50, 30, 40],
            
            background: function(currentPage) {
                return [
                    {
                        image: generateRotatedTextImage(leftSideNote),
                        width: 100, // Adjust as necessary for your content
                        absolutePosition: { x: -10, y: 50 }, // Position as needed
                    }
                ];
            },
            watermark: { text: 'DRAFT', opacity: 0.1, bold: true, italics: false, fontSize: 80,  },
           
            defaultStyle: { fontSize: 12, color: 'black', }
        };
		
		//pdfMake.createPdf(docDefinition).open();
		pdfMake.createPdf(docDefinition).getBlob((blob) => {
		       // Create a URL for the blob
		       const url = URL.createObjectURL(blob);
		
		       // Open the PDF in a new tab
		       window.open(url, '_blank');
		
		       // Hide the loading spinner
		         document.getElementById('loadingOverlay').style.display='none';
		       window.close();
		   });
}

const setImagesWidth = (htmlString, width) => {
    const container = document.createElement('div');
    container.innerHTML = htmlString;
  
    const images = container.querySelectorAll('img');
    
    images.forEach(img => {
    	
      img.style.width = width+'px';
      img.style.textAlign = 'center';
    });
  
    const textElements = container.querySelectorAll('p, h1, h2, h3, h4, h5, h6, span, div, td, th, table, v, figure, hr, ul, li');
    textElements.forEach(element => {
      if (element.style) {
        element.style.fontFamily = '';
        element.style.margin = '';
        element.style.marginTop = '';
        element.style.marginRight = '';
        element.style.marginBottom = '';
        element.style.marginLeft = '';
        element.style.lineHeight = '';
        element.style.height = '';
        element.style.width = '';
        element.style.padding = '';
        element.style.paddingTop = '';
        element.style.paddingRight = '';
        element.style.paddingBottom = '';
        element.style.paddingLeft = '';
        element.style.fontSize = '';
        element.id = '';
        
        const elementColor = element.style.color;
        if (elementColor && elementColor.startsWith("var")) {
            // Replace `var(...)` with a fallback or remove it
            element.style.color = 'black'; // Default color
        }
        
        const elementbackgroundColor = element.style.backgroundColor;
        if (elementbackgroundColor && elementbackgroundColor.startsWith("var")) {
            // Replace `var(...)` with a fallback or remove it
            element.style.backgroundColor = 'transparent'; // Set a default or fallback background color
        }
      }
    });
  
    const tables = container.querySelectorAll('table');
    tables.forEach(table => {
      if (table.style) {
        table.style.borderCollapse = 'collapse';
        table.style.width = '100%';
      }
  
      const cells = table.querySelectorAll('th, td');
      cells.forEach(cell => {
        if (cell.style) {
          cell.style.border = '1px solid black';
  
          if (cell.tagName.toLowerCase() === 'th') {
            cell.style.textAlign = 'center';
          }
        }
      });
    });
  
    return container.innerHTML;
 	}; 	
 
 
 
 	function splitTextIntoLines(text, maxLength) {
	const lines = [];
  	let currentLine = '';

	for (const word of text.split(' ')) {
		if ((currentLine + word).length > maxLength) {
	    	lines.push(currentLine.trim());
	    	currentLine = word + ' ';
		} else {
		  currentLine += word + ' ';
		}
	}
  	lines.push(currentLine.trim());
  	return lines;
}

// Generate rotated text image with line-wrapped text
function generateRotatedTextImage(text) {
	const maxLength = 260;
	const lines = splitTextIntoLines(text, maxLength);
	
	const canvas = document.createElement('canvas');
	const ctx = canvas.getContext('2d');
	
	// Set canvas dimensions based on anticipated text size and rotation
	canvas.width = 200;
	canvas.height = 1560;
	
	// Set text styling
	ctx.font = '14px Roboto';
	ctx.fillStyle = 'rgba(128, 128, 128, 1)'; // Gray color for watermark
	
	// Position and rotate canvas
	ctx.translate(80, 1480); // Adjust position as needed
	ctx.rotate(-Math.PI / 2); // Rotate 270 degrees
	
	// Draw each line with a fixed vertical gap
	const lineHeight = 20; // Adjust line height if needed
	lines.forEach((line, index) => {
	  ctx.fillText(line, 0, index * lineHeight); // Position each line below the previous
	});
	
	return canvas.toDataURL();
}

<%if(isPdf!=null && isPdf.equalsIgnoreCase("Y")) {%>
$( document ).ready(function(){

	document.body.style.display="block"
	document.body.innerHTML = '<div id="loadingOverlay" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.5); z-index: 9999; justify-content: center; align-items: center; flex-direction: column; color: white; font-size: 20px; font-weight: bold;">'+
		    '<div class="spinner" style="border: 4px solid rgba(255, 255, 255, 0.3); border-top: 4px solid white; border-radius: 50%; width: 40px; height: 40px; animation: spin 1s linear infinite; margin-bottom: 10px;"></div>'+
		   ' Please wait while we are generating the PDF...</div>'
		 
	DownloadDocPDF();
	/* window.close(); */
	
	// Hide the current JSP page immediately after opening the PDF
	//document.body.style.display = "none";
	
	/* setTimeout(function () {
        window.close();
    }, 8000) */; // Adjust the delay time as needed
});
<%} %>
</script> 

</body>
</html>
