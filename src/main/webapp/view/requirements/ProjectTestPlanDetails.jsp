<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="com.vts.pfms.requirements.model.TestPlanMaster"%>
<%@page import="com.vts.pfms.requirements.model.TestSetupMaster"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.time.Month"%>
<%@page import="java.time.LocalDateTime"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"
	import="java.util.*,com.vts.*,java.text.SimpleDateFormat"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>PMS</title>
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/Overall.css" var="StyleCSS" />
<link href="${StyleCSS}" rel="stylesheet" />
<spring:url value="/resources/js/excel.js" var="excel" />
<script src="${excel}"></script>
<%-- <spring:url value="/resources/ckeditor/ckeditor.js" var="ckeditor" />
<spring:url value="/resources/ckeditor/contents.css" var="contentCss" />
<script src="${ckeditor}"></script>
<link href="${contentCss}" rel="stylesheet" /> --%>

<spring:url value="/resources/summernote-lite.js" var="SummernoteJs" />
<spring:url value="/resources/summernote-lite.css" var="SummernoteCss" />
<spring:url value="/resources/font/summernote.woff" var="Summernotewoff" />
<spring:url value="/resources/font/summernote.ttf" var="Summernotettf" />
<spring:url value="/resources/font/summernote.eot" var="Summernoteeot" />

<script src="${SummernoteJs}"></script>
<link href="${SummernoteCss}" rel="stylesheet" />
<script src="${Summernotettf}"></script>
<script src="${Summernotettf}"></script>
<script src="${Summernoteeot}"></script>

<!-- Pdfmake  -->
<spring:url value="/resources/pdfmake/pdfmake.min.js" var="pdfmake" />
<script src="${pdfmake}"></script>
<spring:url value="/resources/pdfmake/vfs_fonts.js" var="pdfmakefont" />
<script src="${pdfmakefont}"></script>
<spring:url value="/resources/pdfmake/htmltopdf.js" var="htmltopdf" />
<script src="${htmltopdf}"></script>
	
<style type="text/css">
.note-editable
{
height: 300px;
}
.note-editable img {
width:300px!important;
height:300px!important;
} 
</style>
</head>
<body>
	<%

	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	DecimalFormat df = new DecimalFormat("0.00");
	NFormatConvertion nfc = new NFormatConvertion();
	//Object[] ProjectDetailes = (Object[]) request.getAttribute("ProjectDetailes");
	String pdf=(String)request.getAttribute("pdf");
	List<Object[]>EmployeeList=(List<Object[]>)request.getAttribute("EmployeeList");
	LocalDate d = LocalDate.now();
	Month months= d.getMonth();
	int years=d.getYear();
	Object[]LabList=(Object[])request.getAttribute("LabList");

	String projectType = (String)request.getAttribute("projectType");
	//List<Object[]>MainProjectList= (List<Object[]>)request.getAttribute("MainProjectList");
	//List<Object[]>InitiationProjectList= (List<Object[]>)request.getAttribute("InitiationProjectList");
	String projectId = (String)request.getAttribute("projectId");
	String initiationId = (String)request.getAttribute("initiationId");
	String productTreeMainId =(String)request.getAttribute("productTreeMainId");
	String testPlanInitiationId =(String)request.getAttribute("testPlanInitiationId");
	
	List<Object[]>AbbreviationDetails=(List<Object[]>)request.getAttribute("AbbreviationDetails");
	List<Object[]>MemberList = (List<Object[]>)request.getAttribute("MemberList");
	List<Object[]>TotalEmployeeList=(List<Object[]>)request.getAttribute("TotalEmployeeList");
	List<Object[]>DocumentSummaryList=(List<Object[]>)request.getAttribute("DocumentSummary");
	List<Object[]>TestContentList=(List<Object[]>)request.getAttribute("TestContent");
	String attributes=(String)request.getAttribute("attributes");
	
	Object[]TestApproach=null;
	Object[]DocumentSummary=null;
	if(DocumentSummaryList!=null && DocumentSummaryList.size()>0){
		DocumentSummary=DocumentSummaryList.get(0);
	}
	List<String>reviewers=new ArrayList<>();
	if(DocumentSummary !=null && DocumentSummary[4]!=null){
		reviewers= Arrays.asList(DocumentSummary[4].toString().split("/"));
	}
	String Schedule = null;
	String Approach = null;
	String Conclusion = null;
	String Approachid=null;
	String Scheduleid=null;
	String Conclusionid=null;
	if (TestContentList != null) {
		
	    for (Object[] testClist : TestContentList) { 
	        String Testtype = testClist[0].toString(); 
	   		
	        if ("Approach".equalsIgnoreCase(Testtype)) {
	            Approach = testClist[1].toString();
	            Approachid =testClist[2].toString();
	        } else if ("Schedule".equalsIgnoreCase(Testtype)) {
	            Schedule = testClist[1].toString();
	            Scheduleid =testClist[2].toString();
	        } else if ("Conclusion".equalsIgnoreCase(Testtype)) {
	            Conclusion =testClist[1].toString();
	            Conclusionid =testClist[2].toString();
	        }
	    }
	}

	Object[] projectDetails = (Object[]) request.getAttribute("projectDetails");
	Object[] DocTempAtrr = (Object[]) request.getAttribute("DocTempAttributes");
	String projectShortName = (String)request.getAttribute("projectShortName");
	String lablogo = (String)request.getAttribute("lablogo");
	String drdologo = (String)request.getAttribute("drdologo");
	String version = (String)request.getAttribute("version");
	String docnumber = "-";
	
	if(DocumentSummary!=null) {
		docnumber = "TP-"+(DocumentSummary[11]!=null?DocumentSummary[11].toString().replaceAll("-", ""):"-")+"-"+session.getAttribute("labcode")+"-"+((projectDetails!=null && projectDetails[1]!=null)?projectDetails[1]:"")+"-V"+version;
	}
	
	Object[] TestScopeIntro = (Object[])request.getAttribute("TestScopeIntro");
	String RoleResponsibility = (String)request.getAttribute("RoleResponsibility");
	String htmlContentRoleResponsibility = (String)request.getAttribute("htmlContentRoleResponsibility");
	String TestSetUpDiagram = (String)request.getAttribute("TestSetUpDiagram");
	String htmlContentTestSetUpDiagram = (String)request.getAttribute("htmlContentTestSetUpDiagram");
	String TestVerification = (String)request.getAttribute("TestVerification");
	String htmlContentTestVerification = (String)request.getAttribute("htmlContentTestVerification");
	List<Object[]> TestSuiteList = (List<Object[]>)request.getAttribute("TestSuite");
	List<Object[]> TestDetailsList = (List<Object[]>)request.getAttribute("TestDetailsList");
	List<Object[]> AcceptanceTesting = (List<Object[]>)request.getAttribute("AcceptanceTesting");
	List<Object[]> specificationList = (List<Object[]>)request.getAttribute("specificationList");
	List<Object[]> StagesApplicable = (List<Object[]>)request.getAttribute("StagesApplicable");
	List<TestSetupMaster>master = (List<TestSetupMaster>)request.getAttribute("testSetupMasterMaster");

	if(specificationList!=null && specificationList.size()>0){
		specificationList=specificationList.stream().filter(e->!e[7].toString().equalsIgnoreCase("0")).collect(Collectors.toList());
	}
	
	String isPdf = (String)request.getAttribute("isPdf");
	
	 Map<String, String> objMap = new HashMap<>();
     
     objMap.put("D", "Demonstration");
     objMap.put("T", "Test");
     objMap.put("A", "Analysis/Design");
     objMap.put("I", "Inspection");
     objMap.put("S", "Special Verification Methods");
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
				<div class="card shadow-nohover" style="margin-top: -0.6pc">
					<div class="row card-header"
						style="background: #C4DDFF; box-shadow: 2px 2px 2px grey;">
						<div class="col-md-10" id="projecthead" align="left">
							<h5 id="text" style="margin-left: 1%; font-weight: 600">
								Project Test Plan Details - 
								<small>
									<%if(projectDetails!=null) {%>
										<%=projectDetails[2]!=null?StringEscapeUtils.escapeHtml4(projectDetails[2].toString()):"-" %>
										(<%=projectDetails[1]!=null?StringEscapeUtils.escapeHtml4(projectDetails[1].toString()):"-" %>)
									<%} %>
								</small>
							</h5>
						</div>
						<div class="col-md-2" align="right">
							<form action="ProjectTestPlan.htm" method="post">
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
								<input type="hidden" name="projectType" value="<%=projectType%>">
								<input type="hidden" name="projectId" value="<%=projectId%>">
								<input type="hidden" name="productTreeMainId" value="<%=productTreeMainId%>">
								<button type="submit" class="btn btn-primary btn-sm back">Back</button>
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<div class="container" id="container" style="height: 80%;">
		<div class="row" style="display:inline;height: 90%;">
			<div class="requirementid mt-2 ml-2" style="height: 90%;">
				<span class="badge badge-light mt-2 sidebar pt-2 pb-2" onclick="DownloadDoc()"><img alt="" src="view/images/worddoc.png" >&nbsp;Test Plan Document</span> 
				<span class="badge badge-light mt-2 sidebar pt-2 pb-2" onclick="DownloadDocPDF()"><img alt="" src="view/images/pdf.png" >&nbsp;Test Plan Document</span> 
			 	<span class="badge badge-light mt-2 sidebar pt-2 pb-2" onclick="showSummaryModal()"><img alt="" src="view/images/requirements.png" >&nbsp;&nbsp;Document Summary</span>
			 	<span class="badge badge-light mt-2 sidebar pt-2 pb-2" onclick="showIntroudction()"><img alt="" src="view/images/requirements.png" >&nbsp;&nbsp;Scope</span>
			 	<span class="badge badge-light mt-2 sidebar pt-2 pb-2" onclick="showAbbreviations()"><img alt="" src="view/images/requirements.png" >&nbsp;&nbsp;Abbreviations</span>
				<span class="badge badge-light mt-2 sidebar pt-2 pb-2" onclick="showSentModal()"><img alt="" src="view/images/requirements.png" >&nbsp;&nbsp;Document Distribution</span>
				<span class="badge badge-light mt-2 sidebar pt-2 pb-2" onclick="TestDetails()"><img alt="" src="view/images/requirements.png" >&nbsp;&nbsp;Test Details</span>
				<span class="badge badge-light mt-2 sidebar pt-2 pb-2" onclick="showTestApproachModal()"><img alt="" src="view/images/requirements.png" >&nbsp;&nbsp;Test Approach</span>
				<span class="badge badge-light mt-2 sidebar pt-2 pb-2" onclick="showAccceptanceTest()"><img alt="" src="view/images/requirements.png"  >&nbsp;&nbsp; Acceptance Testing</span>
				<span class="badge badge-light mt-2 sidebar pt-2 pb-2" onclick="showTestScheduleModal()"><img alt="" src="view/images/requirements.png" >&nbsp;&nbsp;Test Schedule</span>
				<span class="badge badge-light mt-2 sidebar pt-2 pb-2" onclick="showTestConclusionModal()"><img alt="" src="view/images/requirements.png" >&nbsp;&nbsp;Conclusion</span>
			</div>
			
	     	<div class="mt-2" id="reqdiv">
				<div style="margin-left: 5%;overflow:auto"id="scrollclass">
					<table class="table table-bordered">
						<tr class="table-warning">
							<td align="center" colspan="2" class="text-primary">DOCUMENT SUMMARY</td>
						</tr>
						<tr>
							<td  class="text-primary" colspan="2">1.&nbsp; Title: <span class="text-dark">System Test Plan Document </span></td>
						</tr>
						<tr >
							<td class="text-primary">2.&nbsp; Type of Document:<span class="text-dark">System Sub System Test Plan Document</span></td>
							<%-- <td class="text-primary">3.&nbsp; Classification: <span class="text-dark"><%=classification %></span></td> --%>
							<td class="text-primary">3.&nbsp; Classification: <span class="text-dark"></span></td>
						</tr>
					    <tr >
							<td class="text-primary">4.&nbsp; Document Number:</td>
							<td class="text-primary">5.&nbsp; Month Year: <span style="color:black;"><%=months.toString().substring(0,3) %>&nbsp;&nbsp;<%=years %></span></td>
						</tr>
						<tr>
							<td class="text-primary">6.&nbsp; Number of Pages:</td>
							<td class="text-primary">7.&nbsp; Related Document:</td>
						</tr>
						<tr>
							<td  class="text-primary" colspan="2">8.&nbsp; Additional Information:
								<%if(DocumentSummary!=null && DocumentSummary[0]!=null) {%><span class="text-dark"><%=StringEscapeUtils.escapeHtml4(DocumentSummary[0].toString())%></span> <%} %>
							</td>
						</tr>
					    <tr>
							<td  class="text-primary" colspan="2">9.&nbsp; Project Number and Project Name: <span class="text-dark"> </span></td>
						</tr>
						<tr>
							<td  class="text-primary" colspan="2">10.&nbsp; Abstract:
								<%if(DocumentSummary!=null && DocumentSummary[1]!=null) {%> <span class="text-dark"><%=StringEscapeUtils.escapeHtml4(DocumentSummary[1].toString())%></span><%} %>
							</td>
						</tr>
						<tr>
							<td  class="text-primary" colspan="2">11.&nbsp; Keywords:
								<%if(DocumentSummary!=null && DocumentSummary[2]!=null) {%> <span class="text-dark"><%=StringEscapeUtils.escapeHtml4(DocumentSummary[2].toString())%></span><%} %>
							</td>
						</tr>
						<tr>
							<td  class="text-primary" colspan="2">12.&nbsp; Organization and address:
								<span class="text-dark">		
									<%if (LabList[1] != null) {%>
										<%=StringEscapeUtils.escapeHtml4(LabList[1].toString()) + "(" + (LabList[0]!=null?StringEscapeUtils.escapeHtml4(LabList[0].toString()): " - ") + ")"%>
									<%} else {%>
										-
									<%}%>
									
									Government of India, Ministry of Defence,Defence
									Research & Development Organization
									<%if (LabList[2] != null && LabList[3] != null && LabList[5] != null) {%>
										<%=StringEscapeUtils.escapeHtml4(LabList[2].toString()) + " , " + StringEscapeUtils.escapeHtml4(LabList[3].toString()) + ", PIN-" + StringEscapeUtils.escapeHtml4(LabList[5].toString())+"."%>
									<%}else{ %>
										-
									<%} %>
								</span>
							</td>
						</tr>
						<tr>
							<td  class="text-primary" colspan="2">13.&nbsp; Distribution:
								<%if(DocumentSummary!=null && DocumentSummary[3]!=null) {%> <span class="text-dark"><%=StringEscapeUtils.escapeHtml4(DocumentSummary[3].toString())%></span><%} %>
							</td>
						</tr>
						<tr>
							<td  class="text-primary" colspan="2">14.&nbsp; Revision:</td>
						</tr>
						<tr>
							<td  class="text-primary" colspan="2">15.&nbsp; Prepared by:
								<%if(DocumentSummary!=null && DocumentSummary[10]!=null) {%> <span class="text-dark"><%=StringEscapeUtils.escapeHtml4(DocumentSummary[10].toString())%></span><%}else {%><span class="text-dark">-</span>  <%} %> <span class="text-dark"></span> 
							</td>
						</tr>
						<tr>
							<td  class="text-primary" colspan="2">16.&nbsp; Reviewed by: 
								<%if(DocumentSummary!=null && DocumentSummary[7]!=null) {%> <span class="text-dark"><%=StringEscapeUtils.escapeHtml4(DocumentSummary[7].toString())%></span><%}else {%><span class="text-dark">-</span>  <%} %> 
							</td>
						</tr>
						<tr>
							<td  class="text-primary" colspan="2">17.&nbsp; Approved by: 
								<%if(DocumentSummary!=null && DocumentSummary[6]!=null) {%> <span class="text-dark"><%=StringEscapeUtils.escapeHtml4(DocumentSummary[6].toString())%></span><%}else {%><span class="text-dark">-</span>  <%} %> 
							</td>
						</tr>
					</table>
				</div>
			</div>
	                  
		</div>
	</div>
			
	<!-- Modal  for Abbreviations-->
	<div class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true" id="AbbreviationsModal">
		<div class="modal-dialog modal-lg">
			<div class="modal-content" style="width:135%;margin-left:-20%;">
				<div class="modal-header" id="ModalHeader" style="background: #055C9D;color:white;">
					<h5 class="modal-title" >Upload Abbreviations</h5>
			        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
			          <span aria-hidden="true" class="text-light">&times;</span>
			        </button>
				</div>
				<div class="modal-body">
					<form action ="AbbreviationExcelUploads.htm" method="post" id="excelForm" enctype="multipart/form-data">
				    	<div class="row">
							<div class="col-md-8">
								<input class="form-control" type="file" id="excel_file" name="filename" required="required"  accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">						
							</div>
							<div class="col-md-4">
								<span class="text-primary">Download format</span>
								<button class="btn btn-sm" type="submit" name="Action" value="GenerateExcel" formaction="ExcelUpload.htm" formmethod="post" formnovalidate="formnovalidate" ><i class="fa fa-file-excel-o" aria-hidden="true" style="color: green;"></i></button>
							</div>
						</div>
						<div class="row mt-2">
							<div class="col-md-12">
								<div style="overflow-y:auto" id="myDiv">
									<table class="table table-bordered table-hover table-striped table-condensed " id="myTable1" style="overflow: scroll;"> </table>
								</div>
							</div>
						</div>
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
						<input id="submit" type="submit" name="submit" value="Submit" hidden="hidden">
						<input type="hidden" name="projectId" value="<%=projectId%>">
						<input type="hidden" name="initiationId" value="<%=initiationId%>"> 
						<input type="hidden" name="productTreeMainId" value="<%=productTreeMainId%>">
						<input type="hidden" name="testPlanInitiationId" value="<%=testPlanInitiationId%>">
						<input type="hidden" name="AbbType" value="T">					
						<div align="center" class="mt-2" id="uploadDiv" style="display:none;">
							<button type="submit" name="Action" value="UploadExcel" class="btn btn-sm btn-info"  onclick="return confirm('Are you sure to submit?')">Upload</button>
						</div>
					</form>
					
					<div id="ExistingAbb">
						<table class="table table-bordered table-hover table-striped table-condensed" id="myTable2" style="width:100%;">
							<thead>
					            <tr>
					                <td>SN</td>
					                <td>Abbreviations</td>
					                <td>Meaning</td>
					            </tr>
							</thead>
							<%  int counter = 1; 
								if (AbbreviationDetails != null) {
									for (Object[] AbbDetails : AbbreviationDetails) {%>
										<tr>
								            <td><%= counter %></td>
								            <td><%= AbbDetails[1]!=null?StringEscapeUtils.escapeHtml4(AbbDetails[1].toString()): " - " %></td>
								            <td><%= AbbDetails[2]!=null?StringEscapeUtils.escapeHtml4(AbbDetails[2].toString()): " - " %></td>
								        </tr>
							<% counter++;}} %>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>	
	
	<div class="row" style="display: inline">
	
		<!--  AccceptanceTest-->
		<form action="AccceptanceTesting.htm" method="GET" id="myStatus">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<input type="hidden" name="projectId" value="<%=projectId%>">
			<input type="hidden" name="initiationId" value="<%=initiationId%>"> 
			<input type="hidden" name="productTreeMainId" value="<%=productTreeMainId%>">
			<input type="hidden" name="testPlanInitiationId" value="<%=testPlanInitiationId%>">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<button type="submit" id="AccceptanceTest" style="display:none"></button>
		</form>
					
					
		<!--  AccceptanceTest end -->
		<!--Downlaod Document  -->
			
		<form action="#">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<button class="btn bg-transparent" id="Downloadbtn" formaction="TestDocumentDownlod.htm" formmethod="get" formnovalidate="formnovalidate" formtarget="_blank" style="display:none;">
				<i class="fa fa-download text-success" aria-hidden="true"></i>
			</button>
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<input type="hidden" name="projectId" value="<%=projectId%>">
			<input type="hidden" name="initiationId" value="<%=initiationId%>"> 
			<input type="hidden" name="productTreeMainId" value="<%=productTreeMainId%>">
			<input type="hidden" name="testPlanInitiationId" value="<%=testPlanInitiationId%>">
		</form>
		<!-- End -->
			
		<!--Downlaod PDF Document  -->
			
		<form action="#">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<button class="btn bg-transparent" id="DownLoadPdf" formaction="TestPlanDownlodPdf.htm" formmethod="get" formnovalidate="formnovalidate" formtarget="_blank" style="display:none;">
				<i class="fa fa-download text-success" aria-hidden="true"></i>
			</button>
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<input type="hidden" name="projectId" value="<%=projectId%>">
			<input type="hidden" name="initiationId" value="<%=initiationId%>"> 
			<input type="hidden" name="productTreeMainId" value="<%=productTreeMainId%>">
			<input type="hidden" name="testPlanInitiationId" value="<%=testPlanInitiationId%>">
		</form>
		
		<!--Downlaod PDF End -->
		<!-- IntroductionPage -->
	  	<form action="#">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<input type="hidden" name="projectId" value="<%=projectId%>">
			<input type="hidden" name="initiationId" value="<%=initiationId%>"> 
			<input type="hidden" name="productTreeMainId" value="<%=productTreeMainId%>">
			<input type="hidden" name="testPlanInitiationId" value="<%=testPlanInitiationId%>">	
			<button class="btn bg-transparent" id="Introbtn" formaction="TestScope.htm" formmethod="get" formnovalidate="formnovalidate"  style="display:none;">
				<i class="fa fa-download text-success" aria-hidden="true"></i>
			</button>
		</form>
		<!-- Introduction form end  -->	
			
					
		<!-- Test Details Start  -->
				
		<form action="#">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<input type="hidden" name="projectId" value="<%=projectId%>">
			<input type="hidden" name="initiationId" value="<%=initiationId%>"> 
			<input type="hidden" name="productTreeMainId" value="<%=productTreeMainId%>">
			<input type="hidden" name="testPlanInitiationId" value="<%=testPlanInitiationId%>">	
			<button class="btn bg-transparent" id="TestDetailsbtn" formaction="TestDetails.htm" formmethod="get" formnovalidate="formnovalidate"  style="display:none;">
				<i class="fa fa-download text-success" aria-hidden="true"></i>
			</button>
		</form>
		<!-- Test Details End -->
		<!--  -->
	</div>
	
	
	<!-- Modal for Document summary  -->
  	<div class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true" id="SummaryModal">
  		<div class="modal-dialog modal-dialog-jump modal-lg ">
    		<div class="modal-content" style="width:137%;margin-left:-21%;">
         		<div class="modal-header" id="ModalHeader">
			        <h5 class="modal-title" id="exampleModalLabel">Document Summary</h5>
			        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
			          <span aria-hidden="true">&times;</span>
			        </button>
      			</div>
      
   				<div class="modal-body">
   					<form action="TestPlanSummaryAdd.htm" method="post">
   						<div class="row">
   							<div class="col-md-4">
   								<label class="" style="font-size: 1rem;font-weight: bold;color:#07689f">Additional Information:</label>
   							</div>
				   			<div class="col-md-8">
				   				<textarea required="required" name="information" class="form-control" id="additionalReq" maxlength="4000"
								rows="3" cols="53" placeholder="Maximum 4000 Chararcters" required><%if(DocumentSummary!=null && DocumentSummary[0]!=null){%><%=DocumentSummary[0].toString()%><%}else{%><%}%></textarea>
				   			</div>
   						</div>
			   			<div class="row mt-2">
				   			<div class="col-md-4">
				   				<label class="" style="font-size: 1rem;font-weight: bold;color:#07689f">Abstract:</label>
				   			</div>
				   			<div class="col-md-8">
				   				<textarea required="required" name="abstract" class="form-control" id="" maxlength="4000"
								rows="3" cols="53" placeholder="Maximum 4000 Chararcters" required><%if(DocumentSummary!=null && DocumentSummary[1]!=null){%><%=DocumentSummary[1].toString()%><%}else{%><%}%></textarea>
				   			</div>
			   			</div>
   			
   						<div class="row mt-2">
				   			<div class="col-md-4">
				   				<label class="" style="font-size: 1rem;font-weight: bold;color:#07689f">Keywords:</label>
				   			</div>
				   			<div class="col-md-8">
				   				<textarea required="required" name="keywords" class="form-control" id="" maxlength="4000"
								rows="3" cols="53" placeholder="Maximum 4000 Chararcters" required><%if(DocumentSummary!=null && DocumentSummary[2]!=null){%><%=DocumentSummary[2].toString()%><%}else{%><%}%></textarea>
				   			</div>
   						</div>
   			
   		    			<div class="row mt-2">
				   			<div class="col-md-4">
				   				<label class="" style="font-size: 1rem;font-weight: bold;color:#07689f">Distribution:</label>
				   			</div>
				   			<div class="col-md-8">
				   				<input required="required" name="distribution" class="form-control" id="" maxlength="255"
								 placeholder="Maximum 255 Chararcters" required value="<%if(DocumentSummary!=null && DocumentSummary[3]!=null){%><%=StringEscapeUtils.escapeHtml4(DocumentSummary[3].toString())%><%}else{%><%}%>">
				   			</div>
   						</div>
   							<div class="row mt-2">
			   				<div class="col-md-2">
			   	 				<label class="" style="font-size: 1rem;font-weight: bold;color:#07689f">Release Date:</label>
			   				</div>	
   				
   				 			<div class="col-md-4">
	   							<input id="pdc-date" data-date-format="dd/mm/yyyy" readonly name="pdc" <%if(DocumentSummary!=null && DocumentSummary[11]!=null){%> value="<%=StringEscapeUtils.escapeHtml4(DocumentSummary[11].toString()) %> " <%}%> class="form-control form-control">
   				
   							</div>
   				
			   				<div class="col-md-2">
						   		<label class="" style="font-size: 1rem;font-weight: bold;color:#07689f">Document No.:</label>
						   	</div>	
   							<div class="col-md-4">
		   						<input required="required" name="Document" class="form-control" id="" maxlength="255"
								 placeholder="Maximum 20 Chararcters" required value="<%if(DocumentSummary!=null && DocumentSummary[12]!=null){%><%=StringEscapeUtils.escapeHtml4(DocumentSummary[12].toString())%><%}else{%><%}%>" >
   							</div>
   						</div>
   						<div class="row mt-2">
   							<div class="col-md-2">
			   	 				<label class="" style="font-size: 1rem;font-weight: bold;color:#07689f">Prepared By:</label>
			   				</div>
			   				<div class="col-md-4">
	   							<select class="form-control selectdee"name="preparedBy" id=""data-width="100%" data-live-search="true"  required>
	          						<option value="" selected disabled>--SELECT--</option>
	        						<%for(Object[]obj:TotalEmployeeList){ %>
	        							<option value="<%=obj[0].toString()%>" <%if(DocumentSummary!=null && DocumentSummary[9]!=null && DocumentSummary[9].toString().equalsIgnoreCase(obj[0].toString())){%>selected<%}%>>
	        								<%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "%>,<%=(obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - ") %>
	        							</option>
	        						<%} %>
	        					</select>
   							</div>
   						</div>
   						
   						<div class="row mt-2">
			   				<div class="col-md-2">
			   	 				<label class="" style="font-size: 1rem;font-weight: bold;color:#07689f">Reviewer:</label>
			   				</div>	
   				
   				 			<div class="col-md-4">
	   							<select class="form-control selectdee"name="reviewer" id=""data-width="100%" data-live-search="true" required>
	       		 					<option value="" selected disabled="disabled">--SELECT--</option>
	       		 					<%for(Object[]obj:TotalEmployeeList){ %>
	        							<option value="<%=obj[0].toString()%>" <%if(DocumentSummary!=null && DocumentSummary[4]!=null &&  DocumentSummary[4].toString().equalsIgnoreCase(obj[0].toString())){%>selected<%}%>>
	        								<%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %>,<%=(obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - ") %>
	        							</option>
	        						<%} %>
	        					</select>
   				
   							</div>
   				
			   				<div class="col-md-1">
						   		<label class="" style="font-size: 1rem;font-weight: bold;color:#07689f;float:right;">Approver:</label>
						   	</div>	
   							<div class="col-md-4">
		   						<select class="form-control selectdee"name="approver" id=""data-width="100%" data-live-search="true"  required>
		       						<option value="" selected disabled="disabled">--SELECT--</option>
	        						<%for(Object[]obj:TotalEmployeeList){ %>
	       	 							<option value="<%=obj[0].toString()%>" <%if(DocumentSummary!=null && DocumentSummary[5]!=null && DocumentSummary[5].toString().equalsIgnoreCase(obj[0].toString())){%>selected<%}%>>
	        								<%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %>,<%=(obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - ") %>
	        							</option>
	        						<%} %>
		        				</select>
   							</div>
   						</div>
   						
   						<div class="mt-2" align="center">
   							<%if(DocumentSummaryList!=null && DocumentSummaryList.size()>0) {%>
   								<button class="btn btn-sm edit" name="action" value="Edit" onclick="return confirm ('Are you sure to submit?')">UPDATE</button>
   								<input type="hidden" name="summaryId" value="<%=DocumentSummary[8]%>"> 
   							<%}else{ %>
   								<button class="btn btn-sm submit" name="action" value="Add" onclick="return confirm('Are you sure to submit?')">SUBMIT</button>
   							<%} %>
   							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
							<input type="hidden" name="projectId" value="<%=projectId%>">
							<input type="hidden" name="initiationId" value="<%=initiationId%>"> 
							<input type="hidden" name="productTreeMainId" value="<%=productTreeMainId%>"> 
							<input type="hidden" name="testPlanInitiationId" value="<%=testPlanInitiationId%>"> 
   						</div>
   					</form>
  	 			</div>
    		</div>
  		</div>
	</div> 	


	<!--Test Approach Model Starts  -->		
	<div class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true" id="TestApproachId">
  		<div class="modal-dialog modal-dialog-jump modal-lg ">
    		<div class="modal-content" style="width:137%;margin-left:-21%;">
         		<div class="modal-header" id="ModalHeader">
			        <h5 class="modal-title" id="exampleModalLabel">Test Approach</h5>
			        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
			          <span aria-hidden="true">&times;</span>
			        </button>
      			</div>
      			
   				<div class="modal-body">
   					<form action="TestDocContentSubmit.htm" method="post" id="Aform">
   						<div class="row mt-2">
   							<div class="col-md-2">
   								<label class="" style="font-size: 1rem;font-weight: bold;color:#07689f">Test Approach:</label>
   							</div>
   							<div id="summernote2" class="center" style="height: 500;">
   								<%if(Approach!=null){ %><%= Approach%><% }%>
   							</div>
   							
   							<textarea name="Details" style="display: none;" id="Approchdetails"></textarea>		
   						</div>
   						<div class="mt-2" align="center">
   							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
							<input type="hidden" name="projectId" value="<%=projectId%>">
							<input type="hidden" name="initiationId" value="<%=initiationId%>"> 
							<input type="hidden" name="productTreeMainId" value="<%=productTreeMainId%>"> 
							<input type="hidden" name="testPlanInitiationId" value="<%=testPlanInitiationId%>"> 
							<input type="hidden" id="attributes" name="attributes" value="Approach">
							<% if(Approach==null){%>
								<button type="submit"class="btn btn-sm btn-success submit mt-2" onclick="return confirm('Are you sure you want to submit?')" name="Action" value="add">SUBMIT</button>
							<% }else {%>
								<button type="submit"class="btn btn-sm btn-warning edit mt-2" onclick="return confirm('Are you sure you want to submit?')" name="Action" value="edit">UPDATE</button>
				 				<input type="hidden" name="UpdateAction" value="<%=Approachid%>">
							<% }%>
   						</div>
   					</form>
  	 			</div>
    		</div>
  		</div>
	</div>

	<!--Test Approach Model  Ends -->		
		
		
		
	<!--Test Schedule Starts  -->		
	<div class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true" id="TestScheduleId">
  		<div class="modal-dialog modal-dialog-jump modal-lg ">
    		<div class="modal-content" style="width:137%;margin-left:-21%;">
         		<div class="modal-header" id="ModalHeader">
        			<h5 class="modal-title" id="exampleModalLabel">Test Schedule</h5>
			        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
			          <span aria-hidden="true">&times;</span>
			        </button>
      			</div>
      			
   				<div class="modal-body">
   					<form action="TestDocContentSubmit.htm" method="post" id="Sform">
			   			<%-- <div class="row mt-2">
				   			<div class="col-md-2">
				   				<label class="" style="font-size: 1rem;font-weight: bold;color:#07689f">Test Schedule:</label>
				   			</div>
				   			<div id="EditorSchedule" class="center">
				   				<% if(Schedule!=null){%> <%=Schedule%>  <% }%>
				   			</div>
				   			<textarea name="Details" style="display: none;" id="ScheduleDetails"></textarea>
				   			
				   			
						 </div>	 --%>
						 
						 <div class="row mt-2">
				   			<div class="col-md-2">
				   				<label class="" style="font-size: 1rem;font-weight: bold;color:#07689f">Test Schedule:</label>
				   			</div>
				   			 <div id="summernote1" style="height: 500;  ">
				   				<% if(Schedule!=null){%> <%=Schedule%>  <% }%>
				   			</div>
				   			<textarea name="Details" style="display: none; height: 150px;" id="ScheduleDetails"></textarea>
				   			
				   			
						 </div>		
			   			</div>
			   			
   						<div class="mt-2" align="center">
   							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
							<input type="hidden" name="projectId" value="<%=projectId%>">
							<input type="hidden" name="initiationId" value="<%=initiationId%>"> 
							<input type="hidden" name="productTreeMainId" value="<%=productTreeMainId%>"> 
							<input type="hidden" name="testPlanInitiationId" value="<%=testPlanInitiationId%>"> 
							<input type="hidden" id="attributes" name="attributes" value="Schedule">
							<% if(Schedule==null){%>
								<button type="submit"class="btn btn-sm btn-success submit mt-2" onclick="return confirm('Are you sure you want to submit?')" name="Action" value="add">SUBMIT</button>
							<% }else{%>
								<button type="submit"class="btn btn-sm btn-warning edit mt-2" onclick="return confirm('Are you sure you want to submit?')" name="Action" value="edit">UPDATE</button>
   					 			<input type="hidden" name="UpdateAction" value="<%=Scheduleid%>">
   							<%} %>
   						</div>
   					</form>
  	 			</div>
    		</div>
  		</div>
	</div>
	<!--Test Schedule  Model  Ends -->		
		
		
				
	<!--Test Conclusion Starts  -->		
	<div class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true" id="TestConclusionId">
  		<div class="modal-dialog modal-dialog-jump modal-lg ">
    		<div class="modal-content" style="width:137%;margin-left:-21%;">
         		<div class="modal-header" id="ModalHeader">
			        <h5 class="modal-title" id="exampleModalLabel">Test Conclusion</h5>
			        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
			          <span aria-hidden="true">&times;</span>
			        </button>
      			</div>
      			
   				<div class="modal-body">
   					<form action="TestDocContentSubmit.htm" method="post" id="Cform">
			   			<div class="col-md-2">
			   				<div class="row mt-2">
			   				</div>
			   				<label class="" style="font-size: 1rem;font-weight: bold;color:#07689f">Test Conclusion:</label>
			   			</div>
			   			
			   			<div id="summernote3" class="center">
			   				<%if(Conclusion!=null) {%><%=Conclusion%><%} %>
			   			</div>
   						<textarea name="Details" style="display: none;"  id="ConclusionDetails"></textarea>	
   		
   						<div class="mt-2" align="center">
   							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
							<input type="hidden" name="projectId" value="<%=projectId%>">
							<input type="hidden" name="initiationId" value="<%=initiationId%>"> 
							<input type="hidden" name="productTreeMainId" value="<%=productTreeMainId%>"> 
							<input type="hidden" name="testPlanInitiationId" value="<%=testPlanInitiationId%>"> 
							<input type="hidden" id="attributes" name="attributes" value="Conclusion">
							<%if(Conclusion==null){ %>
								<button type="submit"class="btn btn-sm btn-success submit mt-2" onclick="return confirm('Are you sure you want to submit?')" name="Action" value="add">SUBMIT</button>
							<%}else{ %>
									<button type="submit"class="btn btn-sm btn-warning edit mt-2" onclick="return confirm('Are you sure you want to submit?')" name="Action" value="edit" >UPDATE</button>
							 <input type="hidden" name="UpdateAction" value="<%=Conclusionid%>">
							<% }%>
						</div>
   					</form>
  	 			</div>
    		</div>
  		</div>
	</div> 	
	<!--Test Conclusion  Model  Ends -->		
		
	<!-- Document Sumary model ends -->
	<!-- modal for Document Distribution -->
	<div class="modal fade" id="DistributionModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
  		<div class="modal-dialog modal-dialog-centered modal-dialog-jump" role="document">
    		<div class="modal-content">
      			<div class="modal-header" id="ModalHeader" style="background: #055C9D ;color:white;">
			        <h5 class="modal-title" >Document Sent to</h5>
			        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
			          <span aria-hidden="true" class="text-light">&times;</span>
			        </button>
      			</div>
      			
      			<div class="modal-body">
      				<%if(MemberList!=null && !MemberList.isEmpty()) {%>
      					<div class="row mb-2">
							<div class="col-md-12">
								<table class="table table-bordered" id="myTables">
									<thead>
										<tr>
											<th  style="text-align: center;">SN</th>
											<th  style="text-align: center;">Name</th>
											<th  style="text-align: center;">Designation</th>
												<th  style="text-align: center;">Action</th> 
										</tr>
									</thead>
									<tbody>
										<%int rowCount=0;
										for(Object[]obj:MemberList) {%>
											<tr>
												<td style="text-align: center;width:10%;"><%=++rowCount %></td>
												<td style="width:50%;margin-left: 10px;"><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %></td>
												<td style="width:40%;margin-left: 10px;"><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></td>
												 <td style="width:40%; margin-left: 10px;">
												    <form id="deleteForm_<%= obj[5] %>" action="#" method="POST" name="myfrm" style="display: inline">
												        <button type="submit" class="editable-clicko" formaction="DeleteTestPlanMembers.htm" onclick="return confirmDeletion('<%= obj[5] %>');">
												            <img src="view/images/delete.png" alt="Delete">
												        </button>
												        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
												        <input type="hidden" name="MemeberId" value="<%= obj[5] %>">
												        <input type="hidden" name="projectId" value="<%=projectId%>">
														<input type="hidden" name="initiationId" value="<%=initiationId%>"> 
														<input type="hidden" name="productTreeMainId" value="<%=productTreeMainId%>"> 
														<input type="hidden" name="testPlanInitiationId" value="<%=testPlanInitiationId%>"> 
														<input type="hidden" name="projectType" value="<%=projectType%>">
												    </form>
												</td>  
											</tr>
										<%} %>
									</tbody>
								</table>
							</div>      
      					</div>
      				<%} %>
      				
       				<form action="MemberSubmit.htm" method="post">
      					<div class="row">
							<div class="col-md-10">
								<select class="form-control selectdee"name="Assignee" id="Assignee"data-width="100%" data-live-search="true" multiple required>
							        <%for(Object[]obj:EmployeeList){ %>
							        	<option value="<%=obj[0].toString()%>"> <%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %>,<%=(obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - ") %></option>
							        <%} %>
        						</select>
        					</div>
					      	<div class="col-md-2" align="center">
					      		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
								<input id="submit" type="submit" name="submit" value="Submit" hidden="hidden">
								<input type="hidden" name="projectId" value="<%=projectId%>">
								<input type="hidden" name="initiationId" value="<%=initiationId%>"> 
								<input type="hidden" name="productTreeMainId" value="<%=productTreeMainId%>"> 
								<input type="hidden" name="testPlanInitiationId" value="<%=testPlanInitiationId%>"> 	
								<input type="hidden" name="MemberType" value="T">		 
					    		<button type="submit" class="btn btn-sm submit" onclick="return confirm ('Are you sure to submit?')"> SUBMIT</button>
					      	</div>
      					</div>
      				</form>
      			</div>
    		</div>
  		</div>
	</div>
	
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
	$(document).ready(function() {
		 $('#summernote1').summernote({
			  width: 800,   //don't use px
			
			  fontNames: ['Arial', 'Arial Black', 'Comic Sans MS', 'Courier New', 'Helvetica', 'Impact', 

'Tahoma', 'Times New Roman', 'Verdana','Segoe UI','Segoe UI Emoji','Segoe UI Symbol'],
			 
		      lineHeights: ['0.5']
		
		 });

	$('#summernote1').summernote({
	     
	      tabsize: 5,
	      height: 1000
	    });
	    
	});
	
	
	$(document).ready(function() {
		 $('#summernote2').summernote({
			  width: 800,   //don't use px
			
			  fontNames: ['Arial', 'Arial Black', 'Comic Sans MS', 'Courier New', 'Helvetica', 'Impact', 'Tahoma', 'Times New Roman', 'Verdana','Segoe UI','Segoe UI Emoji','Segoe UI Symbol'],
			 
		      lineHeights: ['0.5']
		
		 });

	$('#summernote2').summernote({
	     
	      tabsize: 5,
	      height: 1000
	    });
	    
	});
	
	$(document).ready(function() {
		 $('#summernote3').summernote({
			  width: 800,   //don't use px
			
			  fontNames: ['Arial', 'Arial Black', 'Comic Sans MS', 'Courier New', 'Helvetica', 'Impact', 

'Tahoma', 'Times New Roman', 'Verdana','Segoe UI','Segoe UI Emoji','Segoe UI Symbol'],
			 
		      lineHeights: ['0.5']
		
		 });

	$('#summernote3').summernote({
	     
	      tabsize: 5,
	      height: 1000
	    });
	    
	});
	</script>
	
	<script type="text/javascript">
	/* project type selection */
		$(document).ready(function() {
			$('#projectType').on('change', function() {
				var temp = $(this).children("option:selected").val();
				$('#submit').click();
			});
			});
		/* project  selection */
		$(document).ready(function() {
			$('#project').on('change', function() {
				var temp = $(this).children("option:selected").val();
				$('#submit1').click();
			});
			});
	</script>
	<script type="text/javascript">

	const excel_file = document.getElementById('excel_file');
	
	
	excel_file.addEventListener('change', (event) => {
	   
		$('#ExistingAbb').hide();	
		var reader = new FileReader();
	    reader.readAsArrayBuffer(event.target.files[0]);

	    reader.onload = function (event){
	    
	    	var data = new Uint8Array(reader.result);
	    	
	    	var work_book = XLSX.read(data, {type:'array'});
	    	
	    	 var sheet_name = work_book.SheetNames;
	    	
	    	var sheet_data = XLSX.utils.sheet_to_json(work_book.Sheets[sheet_name[0]],{header:1});
	    	
	    	const code = [];
	    	const gname = [];
	    	const abbreviationname1 = [];
	    	var checkExcel = 0;
	    	
	    	if(sheet_data.length > 0){
	    		var table_output = '<table class="table table-bordered table-hover table-striped table-condensed " id="myTable1" style="overflow: scroll;" > '
	    		
	    		table_output +='<thead><tr><th style=" text-align: center;width:10%;">SN</th><th style="text-align:center;width:30%;">Abbreviations</th><th style="text-align:center;">Meaning</th></tr>'
	    		
		    		for(var row = 0; row < sheet_data.length ; row ++){
	    			table_output += '<tbody><tr>'
	    			
	    			if(row>0){
	    				table_output += '<td>'+row+'</td>';}
	    				for(var cell = 0; cell <3;cell++)
	    				{
	    					
	    					if(row==0){
	    						if(cell==1 && "Abbreviation" != sheet_data[row][cell]){  checkExcel++;}
	            				if(cell==2 && "Meaning" != sheet_data[row][cell]){  checkExcel++;}
	            				console.log(sheet_data[row][cell]+cell)
	            				
	            			}	
	    				
	    				if(row>0 && cell == 2){
	    			
	    					table_output+='<td>'+sheet_data[row][cell]+'</td>';
	    					var abbreviationnames = ""+sheet_data[row][cell]+"";
	    					
	    					if(abbreviationnames.trim().length>250){
	    						gname.push(row);
	    					}
	    					if(abbreviationnames.trim()=='' || abbreviationnames.trim()=='undefined'){abbreviationname1.push(row);}	
	    					
	    				}
	    				if(row>0 && cell == 1){
	    					table_output += '<td>'+sheet_data[row][cell]+'</td>' 
	    					var x = ""+sheet_data[row][cell]+"";
	    					
	    					if(x=='' ){
	    						code.push(row)
	    					}
	    				}
	    				
	    				}
	    		
	    			
	    		}  
	    		 table_output += '</tr> <tbody></table>';
	    		 
	    		 
	    		 
	    		if(checkExcel>0){
	    			$('#uploadDiv').hide();
	    			console.log(AbbreviationDetailsList+"---")
	    			alert("Please Upload  Abbreviation Excel ");
	     			excel_file.value = '';
	     			document.getElementById('myTable1').innerHTML = "";
	    		}
	    		else{
	    			
    		 var AbbreviationDetailsList=[<%int i=0; for (Object [] obj:AbbreviationDetails ) {%> "<%= obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): "" %>"<%= i+1 < AbbreviationDetails.size() ? ",":""%><%}%>];	
    		
    		var AbbreDetails = [];
    		
    		for(var i in sheet_data){
    			AbbreDetails.push(sheet_data[i][1]+"");
    		}
    		const duplicates = AbbreDetails.filter((item,index) => index !== AbbreDetails.indexOf(item));
    		const indexval = []             
            for(var i in duplicates){
             indexval.push(AbbreDetails.indexOf(duplicates[i]))
             }
    		 var dbDuplicate = [];
    		 AbbreviationDetailsList.forEach(function (item){ 
    			 var isPresent = AbbreDetails.indexOf(item);
            	  if(isPresent !== -1){
            		  dbDuplicate.push(isPresent); 
            	  }
    		 });
    			var msg=""
    			 if(indexval.length>0){
    	       	 msg+="Duplicate Abbreviation Existed in Excel file at Serial No :"+ indexval+"\n";
    	 		$('#uploadDiv').hide();
    			console.log(AbbreviationDetailsList+"---")
    			alert(msg);
     			excel_file.value = '';
     			document.getElementById('myTable1').innerHTML = "";
    			 }
    			 else if(dbDuplicate.length>0){
    		       	 msg+=" Abbreviation already Existed in Excel file at Serial No :"+ dbDuplicate+"\n";
 	    	 		$('#uploadDiv').hide();
 	    			console.log(AbbreviationDetailsList+"---")
 	    			alert(msg);
 	     			excel_file.value = '';
 	     			document.getElementById('myTable1').innerHTML = "";  
    			 }
    			 
    			 else{
    				 if(sheet_data.length>20){
    		    		 var myDiv = document.getElementById("myDiv");
    		    		  myDiv.style.height = "400px";
    		    } 
				$('#uploadDiv').show();		    			
	    		document.getElementById('myTable1').innerHTML = table_output;
    	         }
	    		}
	    	}
	    
	    }});
	
	$(document).ready(function(){
		 $("#myTable1").DataTable({
		 "lengthMenu": [ 5, 10,25, 50, 75, 100 ],
		 "pagingType": "simple",
		 "pageLength": 5
	});
	});
</script>
	<script type="text/javascript">
    function showAbbreviations() {
        $('#AbbreviationsModal').modal('show');
    }
    function showSummaryModal(){
		$('#SummaryModal').modal('show');
	}
    function showSentModal(){
		$('#DistributionModal').modal('show');
	}
    function showIntroudction(){
		$('#Introbtn').click();
	}
    function showTestApproachModal() {
        $('#TestApproachId').modal('show');
    }
    function showTestScheduleModal() {
        $('#TestScheduleId').modal('show');
    }
    function showTestConclusionModal() {
        $('#TestConclusionId').modal('show');
    }
    function showAccceptanceTest(){
		$('#AccceptanceTest').click();
	}
    
    function DownloadDoc(){
		$('#Downloadbtn').click();
		}
    function TestDetails(){
		$('#TestDetailsbtn').click();
		}
    /* function DownloadDocPDF(){
    	$("#DownLoadPdf").click();
    } */
    
/*     var editor_config = {
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

			height : 250,

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
					cellpadding : '5',
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
		CKEDITOR.replace('EditorSchedule', editor_config); */

		 $('#Cform').submit(function() {
			 var data=$('#summernote3').summernote('code');
			 console.log(data);
			 $('#ConclusionDetails').val(data);
			 });

		 
		 //chnaged
		 $('#Sform').submit(function() {
			 var data=$('#summernote1').summernote('code');
			 console.log(data);
			 $('#ScheduleDetails').val(data);
			 });
		 
		 
		 $('#Aform').submit(function() {
			 var data=$('#summernote2').summernote('code');
			 console.log(data);
			 $('#Approchdetails').val(data);
			 });
		 
		 
		
	
			$('#pdc-date').daterangepicker({
				
				"singleDatePicker": true,
				"showDropdowns": true,
				"cancelClass": "btn-default",
				
				<%if(DocumentSummary==null || DocumentSummary[11]==null) {%>
				"startDate":new Date() ,
			<%}%>
			
				locale: {
			    	format: 'DD-MM-YYYY'
					}
			});
		
</script>

<script type="text/javascript">
function DownloadDocPDF(){
	var chapterCount = 0;
	var mainContentCount = 0;
	var leftSideNote = '<%if(DocTempAtrr!=null && DocTempAtrr[12]!=null) {%><%=StringEscapeUtils.escapeHtml4(DocTempAtrr[12].toString()).replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "") %> <%} else{%>-<%}%>';
		
	var docDefinition = {
            content: [
                // Cover Page with Project Name and Logo
                {
                    text: htmlToPdfmake('<h4 class="heading-color ">SYSTEM TEST PLAN <br><br> FOR  <br><br>PROJECT <%=projectShortName!=null?StringEscapeUtils.escapeHtml4(projectShortName): " - " %> </h4>'),
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
                    text: htmlToPdfmake('<h5><% if (LabList != null && LabList[1] != null) { %> <%= StringEscapeUtils.escapeHtml4(LabList[1].toString()).replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "") + "(" +( LabList[0]!=null?StringEscapeUtils.escapeHtml4(LabList[0].toString()).replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", ""):" - ")  %> <% } else { %> '-' <% } %></h5>'),
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
                    text: htmlToPdfmake('<h6><%if(LabList!=null && LabList[2]!=null && LabList[3]!=null && LabList[5]!=null){ %><%=StringEscapeUtils.escapeHtml4(LabList[2].toString())+" , "+LabList[3].toString()+", PIN-"+StringEscapeUtils.escapeHtml4(LabList[5].toString()) %><%}else{ %>-<%} %></h6>'),
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
                            <% if (MemberList != null && MemberList.size()>0) { %>
	                            <% int slno = 0; for (Object[] obj : MemberList) { %>
	                            [
	                                { text: '<%= ++slno %>', style: 'tableData',alignment: 'center' },
	                                { text: '<%= obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %>', style: 'tableData' },
	                                { text: '<%= obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %>', style: 'tableData' },
	                                { text: '<%= obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - " %>', style: 'tableData',alignment: 'center' }
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
                        }
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
                        headerRows: 1,
                        widths: ['10%', '30%', '60%'],
                        body: [
                            <% int docsn = 0; %>
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Title', style: 'tableData' },
                                { text: 'System Segment Specification Document For Project <%=projectShortName!=null?StringEscapeUtils.escapeHtml4(projectShortName): " - " %>', style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Type of Document', style: 'tableData' },
                                { text: 'System Segment Specification Document', style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Classification', style: 'tableData' },
                                { text: 'Restricted', style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Document Number', style: 'tableData' },
                                { text: '', style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Month Year', style: 'tableData' },
                                { text: '<%=months.toString().substring(0,3) %> <%= years %>', style: 'tableData' },
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
                                { text: '<% if(DocumentSummary!=null){%><%=DocumentSummary[0]!=null?StringEscapeUtils.escapeHtml4(DocumentSummary[0].toString()): " - " %><%} %>', style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Project Name', style: 'tableData' },
                                { text: '<%=projectShortName!=null?StringEscapeUtils.escapeHtml4(projectShortName): " - " %>', style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Abstract', style: 'tableData' },
                                { text: '<% if(DocumentSummary!=null){%><%=DocumentSummary[1]!=null?StringEscapeUtils.escapeHtml4(DocumentSummary[1].toString()): " - " %><%} %>', style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Keywords', style: 'tableData' },
                                { text: '<% if(DocumentSummary!=null){%><%=DocumentSummary[2]!=null?StringEscapeUtils.escapeHtml4(DocumentSummary[2].toString()): " - " %><%} %>', style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Organization and address', style: 'tableData' },
                                { text: '<% if (LabList!=null && LabList[1] != null) {%> <%=StringEscapeUtils.escapeHtml4(LabList[1].toString()).replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "") + "(" + (LabList[0]!=null?StringEscapeUtils.escapeHtml4(LabList[0].toString()).replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", ""):" - ") %> <%} else {%> - <%}%>'
										+'Government of India, Ministry of Defence,Defence Research & Development Organization'
								+'<% if (LabList!=null && LabList[2] != null && LabList[3] != null && LabList[5] != null) { %>'
									+'<%=StringEscapeUtils.escapeHtml4(LabList[2].toString()) + " , " + StringEscapeUtils.escapeHtml4(LabList[3].toString()) + ", PIN-" + StringEscapeUtils.escapeHtml4(LabList[5].toString())+"."%>'
								+'<%}else{ %> - <%} %>' , style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Distribution', style: 'tableData' },
                                { text: '<% if(DocumentSummary!=null){%><%=StringEscapeUtils.escapeHtml4(DocumentSummary[3].toString()) %><%} %>', style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Revision', style: 'tableData' },
                                { text: '<%=version!=null ?StringEscapeUtils.escapeHtml4(version):"-" %>', style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Prepared by', style: 'tableData' },
                                { text: '<% if(DocumentSummary!=null){%><%=StringEscapeUtils.escapeHtml4(DocumentSummary[10].toString()) %><%} %>', style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Reviewed by', style: 'tableData' },
                                { text: '<% if(DocumentSummary!=null){%><%=StringEscapeUtils.escapeHtml4(DocumentSummary[7].toString()) %><%} %>', style: 'tableData' },
                            ],
                            
                            [
                                { text: '<%=++docsn%>', style: 'tableData',alignment: 'center' },
                                { text: 'Approved by', style: 'tableData' },
                                { text: '<% if(DocumentSummary!=null){%><%=StringEscapeUtils.escapeHtml4(DocumentSummary[6].toString()) %><%} %>', style: 'tableData' },
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
			
                /* ************************************** Document Summary End *********************************** */

                /* ************************************** Abbreviation *********************************** */
                {
                    text: 'Abbreviation',
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
                                { text: 'Abbreviations', style: 'tableHeader' },
                                { text: 'Full Forms', style: 'tableHeader' },
                            ],
                            // Populate table rows
                            <% if (AbbreviationDetails != null && AbbreviationDetails.size()>0) { %>
		                        <% int slno = 0; for (Object[] obj : AbbreviationDetails) { %>
		                            [
		                                { text: '<%= ++slno %>', style: 'tableData',alignment: 'center' },
		                                { text: '<%= obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %>', style: 'tableData',alignment: 'center' },
		                                { text: '<%= obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %>', style: 'tableData' },
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
                        }
                    }
                },
                /* ************************************** Abbreviation End *********************************** */

                /* ************************************** Scope *********************************** */
                {
                    text: (++mainContentCount)+'. Scope',
                    style: 'chapterHeader',
                    tocItem: true,
                    id: 'chapter'+(++chapterCount),
                    pageBreak: 'before'
                },
                
                <%if(TestScopeIntro!=null){ %>
	                {
	                	text: (mainContentCount)+'.1. Introduction',	
	                	style: 'chapterSubHeader',
	                    tocItem: true,
	                    id: 'chapter'+(chapterCount)+'.'+mainContentCount+'.1',
	                    tocMargin: [10, 0, 0, 0],
	                },
	                {
	                	<%if(TestScopeIntro[1]!=null) {%>
		            		stack: [htmlToPdfmake(setImagesWidth('<%=StringEscapeUtils.escapeHtml4(TestScopeIntro[1].toString()).replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "")%>' , 500))],
						<%}else {%>
							text: 'No Details Added!',
						<%} %>
	                	
	                    margin: [10, 0, 0, 0],
	                },
	                {
	                	text: (mainContentCount)+'.2. System Identification',	
	                	style: 'chapterSubHeader',
	                    tocItem: true,
	                    id: 'chapter'+(chapterCount)+'.'+mainContentCount+'.2',
	                    tocMargin: [10, 0, 0, 0],
	                },
	                {
	                	<%if(TestScopeIntro[2]!=null) {%>
	            			stack: [htmlToPdfmake(setImagesWidth('<%=StringEscapeUtils.escapeHtml4(TestScopeIntro[2].toString()).replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "")%>' , 500))],
						<%}else {%>
							text: 'No Details Added!',
						<%} %>
	                    margin: [10, 0, 0, 0],
	                },
	                {
	                	text: (mainContentCount)+'.3. System Overview',	
	                	style: 'chapterSubHeader',
	                    tocItem: true,
	                    id: 'chapter'+(chapterCount)+'.'+mainContentCount+'.3',
	                    tocMargin: [10, 0, 0, 0],
	                },
	                {
	                	<%if(TestScopeIntro[3]!=null) {%>
		        			stack: [htmlToPdfmake(setImagesWidth('<%=StringEscapeUtils.escapeHtml4(TestScopeIntro[3].toString()).replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "")%>' , 500))],
						<%}else {%>
							text: 'No Details Added!',
						<%} %>
	                    margin: [10, 0, 0, 0],
	                },
                <%} %>
                /* ************************************** Scope End *********************************** */

	            /* ************************************** Applicable Document *********************************** */
                /* {
                    text: (++mainContentCount)+'. Applicable Document',
                    style: 'chapterHeader',
                    tocItem: true,
                    id: 'chapter'+(++chapterCount),
                    pageBreak: 'before'
                },
                {
                	text: 'No Documents Applicable!',
                	style: 'chapterContent',
                }, */
                /* ************************************** Applicable Document End *********************************** */

	            /* ************************************** Test Approach *********************************** */
                {
                    text: (++mainContentCount)+'. Test Approach',
                    style: 'chapterHeader',
                    tocItem: true,
                    id: 'chapter'+(++chapterCount),
                    margin: [0, 10, 0 ,0],
                    /* pageBreak: 'before' */
                },
                
                {
                	<%if(Approach!=null) {%>
	        			stack: [htmlToPdfmake(setImagesWidth('<%=StringEscapeUtils.escapeHtml4(Approach).replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "")%>' , 500))],
					<%}else {%>
						text: 'No Details Added!',
					<%} %>
                	style: 'chapterContent',
                },
                /* ************************************** Test Approach End *********************************** */

	            /* ************************************** Role & Responsibility *********************************** */
                {
                    text: (++mainContentCount)+'. Role & Responsibility for Carrying Out each Level of System / Sub-System Testing',
                    style: 'chapterHeader',
                    tocItem: true,
                    id: 'chapter'+(++chapterCount),
                    margin: [0, 10, 0 ,0],
                    /* pageBreak: 'before' */
                },
                
                {
                	<%if(RoleResponsibility!=null) {%>
	        			stack: [htmlToPdfmake(setImagesWidth('<%=StringEscapeUtils.escapeHtml4(RoleResponsibility).replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "")%>' , 500))],
					<%}else {%>
						text: 'No Details Added!',
					<%} %>
                	style: 'chapterContent',
                },
                <%-- <% if(htmlContentRoleResponsibility!=null) { %>
	                {
	                	stack: [htmlToPdfmake('<%=htmlContentRoleResponsibility.replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "") %>')],
	                },
            	<%}%> --%>
            	<% if(htmlContentRoleResponsibility!=null) { %>
	                {
	                	stack: [htmlToPdfmake("<%=StringEscapeUtils.escapeHtml4(htmlContentRoleResponsibility) %>")],
	                },
         		<%}%>
                /* ************************************** Role & Responsibility End *********************************** */

	            /* ************************************** Acceptance Testing *********************************** */
                {
                    text: (++mainContentCount)+'. Acceptance Testing',
                    style: 'chapterHeader',
                    tocItem: true,
                    id: 'chapter'+(++chapterCount),
                    margin: [0, 10, 0 ,0],
                    /* pageBreak: 'before' */
                },
                {
                	text: mainContentCount+'.1 Test Set Up',	
                	style: 'chapterSubHeader',
                    tocItem: true,
                    id: 'chapter'+chapterCount+'.'+mainContentCount+'.1',
                    tocMargin: [10, 0, 0, 0],
                },
                
                <%  if (TestSuiteList != null && TestSuiteList.size()>0) {%>	
                	{
                		table : {
                			headerRows : 1,
        					widths: ['10%', '40%', '50%'],
                			body: [
                				// Table header
	                            [
	                                { text: 'SN', style: 'tableHeader' },
	                                { text: 'Test Type', style: 'tableHeader' },
	                                { text: 'Test Setup Name', style: 'tableHeader' },
	                            ],
	                         	// Populate table rows
	                            <%int slno = 0;;
	                          	for (Object[] obj : TestSuiteList) { %>
		                          	[
		                                { text: '<%=++slno %>', style: 'tableData', alignment: 'center' },
		                                { text: '<%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()):"-" %>', style: 'tableData' },
		                                { text: '<%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()):"-" %>', style: 'tableData' },
		                            ],
		                          <% } %>
                			],
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
                    	},
                	},
                	{ text: '\n',},
				<% } %>
				
				{
                	text: mainContentCount+'.2 Test Set Up Diagram',	
                	style: 'chapterSubHeader',
                    tocItem: true,
                    id: 'chapter'+chapterCount+'.'+mainContentCount+'.2',
                    tocMargin: [10, 0, 0, 0],
                },
                {
                	<%if(TestSetUpDiagram!=null) {%>
	        			stack: [htmlToPdfmake(setImagesWidth('<%=StringEscapeUtils.escapeHtml4(TestSetUpDiagram).replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "")%>' , 500))],
					<%}else {%>
						text: 'No Details Added!',
					<%} %>
					margin : [10, 0, 0, 0],
                	
                },
                <% if(htmlContentTestSetUpDiagram!=null) { 
                %>
	                {
	                	stack: [htmlToPdfmake("<%=StringEscapeUtils.escapeHtml4(htmlContentTestSetUpDiagram) %>")],
	                },
     			<%}%>
            	
                {
                	text: mainContentCount+'.3 Test Suite',	
                	style: 'chapterSubHeader',
                    tocItem: true,
                    id: 'chapter'+chapterCount+'.'+mainContentCount+'.3',
                    tocMargin: [10, 0, 0, 0],
                },
                <%  if (TestSuiteList != null && !TestSuiteList.isEmpty()) { %>	
                	{
                		table : {
                			headerRows: 1,
                			widths: ['10%', '30%', '30%', '30%'],
                			body: [
                				// Table header
                				[
                					{text: 'SN', style: 'tableHeader'},
                					{text: 'Test Type', style: 'tableHeader'},
                					{text: 'Test Ids', style: 'tableHeader'},
                					{text: 'Test Tools', style: 'tableHeader'},
                				],
                				
                				<%int slno = 0;
                              	for (Object[] obj : TestSuiteList) { %>
	                              	[
		                                { text: '<%=++slno %>', style: 'tableData', alignment: 'center' },
		                                { text: '<%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()):"-" %>', style: 'tableData' },
		                                { text: '<% List<String>list = new ArrayList<>(); list = TestDetailsList.stream().filter(ix->ix[10]!=null && Arrays.asList(ix[10].toString().split(",")).contains(obj[0].toString())).map(ix->ix[1].toString()).collect(Collectors.toList()); %>'
                           						+'<%if(list.size()!=0) {%> <%=list.toString().replace("[", "").replace("]", "") %><%}else{ %>-<%} %>', style: 'tableData' },
		                                { text: '<%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()):"-" %>', style: 'tableData' },
		                            ],
                              	<% } %>
                			],
                		},
                		layout: {
                			
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
	                    },
                	},
                	{ text: '\n',},
                	
                 <% } %>
                 
                {
                	text: mainContentCount+'.4 Test Verification Table',	
                	style: 'chapterSubHeader',
                    tocItem: true,
                    id: 'chapter'+chapterCount+'.'+mainContentCount+'.4',
                    tocMargin: [10, 0, 0, 0],
                },
                {
                	<%if(TestVerification!=null) {%>
	        			stack: [htmlToPdfmake(setImagesWidth('<%=StringEscapeUtils.escapeHtml4(TestVerification).replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "")%>' , 500))],
					<%}else {%>
						text: 'No Details Added!',
					<%} %>
					margin : [10, 0, 0, 0],
                	
                },
             	<% if(htmlContentTestVerification!=null) { %>
	                {
	                	text: [htmlToPdfmake("<%=StringEscapeUtils.escapeHtml4(htmlContentTestVerification) %>")],
	                },
 				<%}%>
                /* ************************************** Acceptance Testing End *********************************** */
                
                /* ************************************** Test Schedule *********************************** */
                {
                    text: (++mainContentCount)+'. Test Schedule',
                    style: 'chapterHeader',
                    tocItem: true,
                    id: 'chapter'+(++chapterCount),
                    margin: [0, 10, 0 ,0],
                    /* pageBreak: 'before' */
                },
                {
                	<%if(Schedule!=null) {%>
 	        			stack: [htmlToPdfmake(setImagesWidth('<%=StringEscapeUtils.escapeHtml4(Schedule).replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "")%>' , 500))],
 					<%}else {%>
 						text: 'No Details Added!',
 					<%} %>
                 	style: 'chapterContent',
                },
                /* ************************************** Test Schedule End *********************************** */
                
                /* ************************************** Test Details *********************************** */
                {
                    text: (++mainContentCount)+'. Test Details',
                    style: 'chapterHeader',
                    tocItem: true,
                    id: 'chapter'+(++chapterCount),
                    margin: [0, 10, 0 ,0],
                    pageBreak: 'before',
                },
                
                <%if (TestDetailsList != null && TestDetailsList.size()>0) { 
    				int subslno=0;
    				for (Object[] obj : TestDetailsList) { int slno=0;%>	
	    				{
	                    	text: mainContentCount+'.<%=++subslno %>.'+' Test ID: <%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %>',	
	                     	style: 'chapterSubHeader',
	                        tocItem: true,
	                        id: 'chapter'+chapterCount+'.'+mainContentCount+'.<%=subslno %>',
	                        tocMargin: [10, 0, 0, 0],
		            	}, 
	                    {
		            		table: {
		            			headerRows: 1,
		            			widths: ['10%', '30%', '60%'],
		            			body: [
		            				// Table header
		            				[
		            					{text: 'SN', style: 'tableHeader'},
		            					{text: 'Item', style: 'tableHeader'},
		            					{text: 'Description', style: 'tableHeader'},
		            				],
		            				// Populate table rows
		            				[
		            					{text: '<%=++slno%>', style: 'tableData', alignment: 'center'},
		            					{text: 'Spec ID', style: 'tableData',},
		            					<%-- {text: '<%if(obj[19]!=null){ List<String>list = new ArrayList<>();%>'
		            						  +'<%if(specificationList.size()>0){ list = specificationList.stream().filter(e->Arrays.asList(obj[19].toString().split(",")).contains(e[0].toString())).map(e->e[1].toString()).collect(Collectors.toList());}%>'
		            						  +'<%= list.size()>0?list.toString().replace("[", "").replace("]",""):"-" %>'
		            						  +'<% } else {%>-<%}%>', style: 'tableData',}, --%>
		            					{text: '<%if(obj[19]!=null){ List<Object[]>list = new ArrayList<>();%>'
		            						  +'<%if(specificationList.size()>0){ list = specificationList.stream().filter(e->Arrays.asList(obj[19].toString().split(",")).contains(e[0].toString())).collect(Collectors.toList());}%>'
		            						  +'<%for(Object[] obj1 : list) {%>'
		            						  +'<%=obj1[1]!=null?StringEscapeUtils.escapeHtml4(obj1[1].toString()): " - "+" ("+(obj1[5]!=null?StringEscapeUtils.escapeHtml4(obj1[5].toString()):"-")+" "+(obj1[9]!=null?StringEscapeUtils.escapeHtml4(obj1[9].toString()):"-")+" "+(obj1[6]!=null?StringEscapeUtils.escapeHtml4(obj1[6].toString()):"-")+")"%>\n'
		            						  +'<%} } else {%>-<%}%>', style: 'tableData',},
		            				],
		            				
		            				[
		            					{text: '<%=++slno%>', style: 'tableData', alignment: 'center'},
		            					{text: 'ID', style: 'tableData',},
		            					{text: '<% if(obj[1]!=null){%><%=StringEscapeUtils.escapeHtml4(obj[1].toString()) %><% } else {%>-<%}%>', style: 'tableData',},
		            				],
		            				
		            				[
		            					{text: '<%=++slno%>', style: 'tableData', alignment: 'center'},
		            					{text: 'Name', style: 'tableData',},
		            					{text: '<% if(obj[2]!=null){%><%=StringEscapeUtils.escapeHtml4(obj[2].toString()) %><% } else {%>-<%}%>', style: 'tableData',},
		            				],
		            				
		            				[
		            					{text: '<%=++slno%>', style: 'tableData', alignment: 'center'},
		            					{text: 'Objective', style: 'tableData',},
		            					{text: '<% if(obj[3]!=null){%><%=StringEscapeUtils.escapeHtml4(obj[3].toString()) %><% } else {%>-<%}%>', style: 'tableData',},
		            				],
		            				
		            				[
		            					{text: '<%=++slno%>', style: 'tableData', alignment: 'center'},
		            					{text: 'Description', style: 'tableData', colSpan: '2'},
		            				],
		            				[
		            					{stack: [htmlToPdfmake(setImagesWidth('<%if(obj[4]!=null){%><%=StringEscapeUtils.escapeHtml4(obj[4].toString()).replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "") %><% } else {%>-<%}%>', 500))], colSpan: 3},
		            				],
		            				
		            				[
		            					{text: '<%=++slno%>', style: 'tableData', alignment: 'center'},
		            					{text: 'Pre-Conditions', style: 'tableData', colSpan: '2'},
		            				],
		            				[
		            					{stack: [htmlToPdfmake(setImagesWidth('<%if(obj[5]!=null){%><%=StringEscapeUtils.escapeHtml4(obj[5].toString()).replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "") %><% } else {%>-<%}%>', 500))], colSpan: 3},
		            				],
		            				
		            				[
		            					{text: '<%=++slno%>', style: 'tableData', alignment: 'center'},
		            					{text: 'Post-Conditions', style: 'tableData', colSpan: '2'},
		            				],
		            				[
		            					{stack: [htmlToPdfmake(setImagesWidth('<%if(obj[6]!=null){%><%=StringEscapeUtils.escapeHtml4(obj[6].toString()).replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "") %><% } else {%>-<%}%>', 500))], colSpan: 3},
		            				],

		            				[
		            					{text: '<%=++slno%>', style: 'tableData', alignment: 'center'},
		            					{text: 'Constraints', style: 'tableData',},
		            					{text: '<% if(obj[7]!=null){%><%=StringEscapeUtils.escapeHtml4(obj[7].toString()) %><% } else {%>-<%}%>', style: 'tableData',},
		            				],

		            				[
		            					{text: '<%=++slno%>', style: 'tableData', alignment: 'center'},
		            					{text: 'Safety Requirements', style: 'tableData', colSpan: '2'},
		            				],
		            				[
		            					{stack: [htmlToPdfmake(setImagesWidth('<%if(obj[8]!=null){%><%=StringEscapeUtils.escapeHtml4(obj[8].toString()).replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "") %><% } else {%>-<%}%>', 500))], colSpan: 3},
		            				],

		            				[
		            					{text: '<%=++slno%>', style: 'tableData', alignment: 'center'},
		            					{text: 'Methodology', style: 'tableData',},
		            					{text: '<% if(obj[9]!=null){%><%=objMap.get(StringEscapeUtils.escapeHtml4(obj[9].toString())) %><% } else {%>-<%}%>', style: 'tableData',},
		            				],

		            				[
		            					{text: '<%=++slno%>', style: 'tableData', alignment: 'center'},
		            					{text: 'Test Set Up', style: 'tableData',},
		            					
		            					<%String[] TempArray = obj[10]!=null? obj[10].toString().split(","):new String[]{"", "", "", "", ""}; 
 										List<String>tempList = Arrays.asList(TempArray);
 										List<String>temp = new ArrayList<>();
										/* 	for(Object[] obj1: TestSuiteList){
										if(tempList.contains(obj1[0].toString())){
											temp.add(obj1[3].toString());
										}} */ 
										
										TestSetupMaster tp = master.stream().filter(e->e.getSetupId().toString().equalsIgnoreCase(TempArray[0].trim()))
															.findFirst().orElse(null);
										
									
										//System.out.println(tp.getTestSetUpId());
										%> 
										
										[
			            					{stack: [htmlToPdfmake(setImagesWidth('<%if(tp!=null){%><%=tp.getTestSetUpId() %><% } else {%>-<%}%>', 500))], colSpan: 3},
			            				],
			            				
			            			
		            				],
		            				[
		            					{stack: [htmlToPdfmake(setImagesWidth('<%if(tp!=null && tp.getObjective()!=null){%><%= "<b>Objective</b>"+StringEscapeUtils.escapeHtml4( tp.getObjective().toString()).replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "") %><% } else {%>-<%}%>', 500))], colSpan: 3},
		            					
		            				],
		            				[
		            					{stack: [htmlToPdfmake(setImagesWidth('<%if(tp!=null && tp.getTestProcedure()!=null){%><%= "<b>Test Procedure</b>"+ StringEscapeUtils.escapeHtml4(tp.getTestProcedure().toString()).replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "") %><% } else {%>-<%}%>', 500))], colSpan: 3},
		            					
		            				],
		            				[
		            					{stack: [htmlToPdfmake(setImagesWidth('<%if(tp!=null && tp.getTestSetUp()!=null){%><%= "<b>Test Set Up</b>"+StringEscapeUtils.escapeHtml4( tp.getTestSetUp().toString()).replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "") %><% } else {%>-<%}%>', 500))], colSpan: 3},
		            					
		            				],
		            				

		            				[
		            					{text: '<%=++slno%>', style: 'tableData', alignment: 'center'},
		            					{text: 'Personnel Resources', style: 'tableData', colSpan: '2'},
		            				],
		            				[
		            					{stack: [htmlToPdfmake(setImagesWidth('<%if(obj[11]!=null){%><%=StringEscapeUtils.escapeHtml4(obj[11].toString()).replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "") %><% } else {%>-<%}%>', 500))], colSpan: 3},
		            				],

		            				[
		            					{text: '<%=++slno%>', style: 'tableData', alignment: 'center'},
		            					{text: 'Estimated Time/Iteration', style: 'tableData',},
		            					{text: '<% if(obj[12]!=null){%><%=StringEscapeUtils.escapeHtml4(obj[12].toString())%><% } else {%>-<%}%>', style: 'tableData',},
		            				],

		            				[
		            					{text: '<%=++slno%>', style: 'tableData', alignment: 'center'},
		            					{text: 'Iterations', style: 'tableData',},
		            					{text: '<% if(obj[13]!=null){%><%=StringEscapeUtils.escapeHtml4(obj[13].toString()) %><% } else {%>-<%}%>', style: 'tableData',},
		            				],

		            				[
		            					{text: '<%=++slno%>', style: 'tableData', alignment: 'center'},
		            					{text: 'Schedule', style: 'tableData',},
		            					{text: '<% if(obj[14]!=null){%><%=StringEscapeUtils.escapeHtml4(obj[14].toString()) %><% } else {%>-<%}%>', style: 'tableData',},
		            				],

		            				[
		            					{text: '<%=++slno%>', style: 'tableData', alignment: 'center'},
		            					{text: 'Pass/Fail Criteria', style: 'tableData',},
		            					{text: '<% if(obj[15]!=null){%><%=StringEscapeUtils.escapeHtml4(obj[15].toString())%><% } else {%>-<%}%>', style: 'tableData',},
		            				],

		            				[
		            					{text: '<%=++slno%>', style: 'tableData', alignment: 'center'},
		            					{text: 'Stage Applicable', style: 'tableData',},
		            					
		            					<%String[]TempArray1=obj[22]!=null ?obj[22].toString().split(","): new String[5];
		            		            List<String>tempList1=Arrays.asList(TempArray1);
		            		            List<String>tempsa=new ArrayList<>();
		            		            for(Object[] obj1 : StagesApplicable){
		            		            	if(tempList1.contains(obj1[0].toString())){
		            		            		tempsa.add(obj1[3].toString());
		            		            	}
		            		            }%>
		            		            
		            					{text: '<% if(tempsa.size() > 0) { %> <%= String.join(", ", temp) + "" %><% } else { %> - <% } %>', style: 'tableData',},
		            				],

		            				[
		            					{text: '<%=++slno%>', style: 'tableData', alignment: 'center'},
		            					{text: 'Remarks', style: 'tableData',},
		            					{text: '<% if(obj[15]!=null){%><%=StringEscapeUtils.escapeHtml4(obj[15].toString()) %><% } else {%>-<%}%>', style: 'tableData',},
		            				],

		            			],
		            		},
		            		layout: {
		            			
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
	                    { text: '\n',},
    			<%}} else {%>
   					{text: 'No Details Added!', margin: [0, 10, 0 ,0],},
    			<%}%>
                /* ************************************** Test Details End *********************************** */
                
    			/* ************************************** Forward Traceability  *********************************** */
                {
                    text: (++mainContentCount)+'. Forward Traceability ',
                    style: 'chapterHeader',
                    tocItem: true,
                    id: 'chapter'+(++chapterCount),
                    margin: [0, 10, 0 ,0],
                    /* pageBreak: 'before' */
                },
                
                {
                	table: {
                		headerRows: 1,
                		widths: ['10%', '40%', '50%'],
                		body: [
                			// Table header
                			[
                				{text: 'SN', style: 'tableHeader',},
                				{text: 'TestPlan Id', style: 'tableHeader',},
                				{text: 'Specification Id', style: 'tableHeader',},
                			],
                			// Populate table rows
                			<%if(TestDetailsList!=null && TestDetailsList.size()>0){ 
								int slno=0;
								for(Object[] obj:TestDetailsList){ %>
                			[
                				{text: '<%=++slno %>', style: 'tableData', alignment: 'center'},
                				{text: '<%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()):"-" %>', style: 'tableData', },
                				
                				<% List<String> specid = obj[19]!=null?Arrays.asList(obj[19].toString() .split(",")):new ArrayList<>();
        						List<Object[]>newSpecList = new ArrayList<>();
        						newSpecList = specificationList.stream().filter(spec->specid.contains(spec[0].toString())).collect(Collectors.toList()); 
        						%>
        						
        						{text: '<%if(newSpecList.size()!=0) { for(Object[]obj1:newSpecList){ %><%=obj1[1]!=null?StringEscapeUtils.escapeHtml4(obj1[1].toString()): " - "+" ("+(obj1[5]!=null?StringEscapeUtils.escapeHtml4(obj1[5].toString()):"-")+" "+(obj1[9]!=null?StringEscapeUtils.escapeHtml4(obj1[9].toString()):"-")+" "+(obj1[6]!=null?StringEscapeUtils.escapeHtml4(obj1[6].toString()):"-")+")"%>\n<%}}else{ %> - <%} %>', style: 'tableData', },
        						
                			],
                			<%}}else{ %>
                				[{text: 'No Data Available !', style: 'tableData', colSpan: 3, alignment: 'center'}],
    						<%} %>
                		],
                	},
                	layout: {
            			
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
                    },
                },

                /* ************************************** Forward Traceability  End *********************************** */
                
    			/* ************************************** Backward Traceability  *********************************** */
                {
                    text: (++mainContentCount)+'. Backward Traceability ',
                    style: 'chapterHeader',
                    tocItem: true,
                    id: 'chapter'+(++chapterCount),
                    margin: [0, 10, 0 ,0],
                    /* pageBreak: 'before' */
                },
                
                {
                	table: {
                		headerRows: 1,
                		widths: ['10%', '40%', '50%'],
                		body: [
                			// Table header
                			[
                				{text: 'SN', style: 'tableHeader',},
                				{text: 'Specification Id', style: 'tableHeader',},
                				{text: 'TestPlan Id', style: 'tableHeader',},
                			],
                			// Populate table rows
                			<%if(specificationList!=null && specificationList.size()>0){ 
								int slno=0;
								for(Object[] obj:specificationList){ %>
                			[
                				{text: '<%=++slno %>', style: 'tableData', alignment: 'center'},
                				{text: '<%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "+" ("+(obj[5]!=null?StringEscapeUtils.escapeHtml4(obj[5].toString()):"-")+" "+(obj[9]!=null?StringEscapeUtils.escapeHtml4(obj[9].toString()):"-")+" "+(obj[6]!=null?StringEscapeUtils.escapeHtml4(obj[6].toString()):"-")+")" %>', style: 'tableData', },
                				
                				<%
								List<Object[]>newTestList = new ArrayList<>(); 
								if(TestDetailsList!=null && TestDetailsList.size()>0){
									newTestList = TestDetailsList.stream().filter(e->e[19]!=null &&  Arrays.asList(e[19].toString().split(",")).contains(obj[0].toString())).collect(Collectors.toList());
								}%>
        						
        						{text: '<%if(newTestList.size()>0){ for(Object[] obj1: newTestList){ %><%=obj1[1]!=null?StringEscapeUtils.escapeHtml4(obj1[1].toString()): " - " %>\n<%}}else{ %> - <%} %>', style: 'tableData', },
                			],
                			<%}}else{ %>
                				[{text: 'No Data Available !', style: 'tableData', colSpan: 3, alignment: 'center'}],
    						<%} %>
                		],
                	},
                	layout: {
            			
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
                    },
                },
                /* ************************************** Backward Traceability  End *********************************** */

	            /* ************************************** Conclusion *********************************** */
                {
                    text: (++mainContentCount)+'. Conclusion',
                    style: 'chapterHeader',
                    tocItem: true,
                    id: 'chapter'+(++chapterCount),
                    margin: [0, 10, 0 ,0],
                    /* pageBreak: 'before' */
                },
                {
                	<%if(Conclusion!=null){ %>
                		stack: [htmlToPdfmake(setImagesWidth('<%=Conclusion.replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "") %>', 500))],
                	<%} else{%>
	                	text: 'No Details Added!',
                	<%} %>
                	style: 'chapterContent',
                },
                /* ************************************** Conclusion End *********************************** */
                
			],
            styles: {
                DocumentName: { fontSize: 18, bold: true, margin: [0, 0, 0, 10] },
                chapterHeader: { fontSize: 16, bold: true, margin: [0, 0, 0, 10] },
                chapterNote: { fontSize: 13, bold: true, margin: [0, 10, 0, 10]},
                chapterSubHeader: { fontSize: 13, bold: true, margin: [10, 10, 0, 10]},
                tableHeader: { fontSize: 12, bold: true, fillColor: '#f0f0f0', alignment: 'center', margin: [10, 5, 10, 5], fontWeight: 'bold' },
                tableData: { fontSize: 11.5, margin: [0, 5, 0, 5] },
                chapterSubSubHeader: { fontSize: 12, bold: true, margin: [15, 10, 10, 10] },
                subChapterNote: { margin: [15, 15, 0, 10] },
                header: { alignment: 'center', bold: true},
                chapterContent: {fontSize: 11.5, margin: [0, 5, 0, 5] },
            },
            footer: function(currentPage, pageCount) {
                if (currentPage > 2) {
                    return {
                        stack: [
                        	{
                                canvas: [{ type: 'line', x1: 30, y1: 0, x2: 565, y2: 0, lineWidth: 1 }]
                            },
                            {
                                columns: [
                                    { text: '<%if(docnumber!=null) {%><%=docnumber %><%} %>', alignment: 'left', margin: [30, 0, 0, 0], fontSize: 8 },
                                    { text: currentPage.toString() + ' of ' + pageCount, alignment: 'right', margin: [0, 0, 30, 0], fontSize: 8 }
                                ]
                            },
                            { text: 'Restricted', alignment: 'center', fontSize: 8, margin: [0, 5, 0, 0], bold: true }
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
                                    image: '<%= drdologo != null ? "data:image/png;base64," + drdologo : "" %>',
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
           
            defaultStyle: { fontSize: 12 }
        };
		
        pdfMake.createPdf(docDefinition).open();
}

const setImagesWidth = (htmlString, width) => {
    const container = document.createElement('div');
    container.innerHTML = htmlString;
  
    const images = container.querySelectorAll('img');
    images.forEach(img => {
      img.style.width = width + 'px';
      img.style.textAlign = 'center';
    });
  
    const textElements = container.querySelectorAll('p, h1, h2, h3, h4, h5, h6, span, div, td, th, table, figure, hr, ul, li, a');
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
	DownloadDocPDF();
	/* window.close(); */
	
	// Hide the current JSP page immediately after opening the PDF
	document.body.style.display = "none";
	
	setTimeout(function () {
        window.close();
    }, 5000); // Adjust the delay time as needed
});
<%} %>
</script>
</body>
</html>
