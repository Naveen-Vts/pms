<%@page import="java.time.LocalDate"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<spring:url value="/resources/pdfmake/xlsx.full.min.js" var="excelConverter" />
<script src="${excelConverter}"></script>
<spring:url value="/resources/css/sweetalert2.min.css" var="sweetalertCss" />
<spring:url value="/resources/js/sweetalert2.min.js" var="sweetalertJs" />
	<link href="${sweetalertCss}" rel="stylesheet" />
	<script src="${sweetalertJs}"></script>
<%-- 	<jsp:include page="../static/header.jsp"></jsp:include> --%>

<jsp:include page="../static/header.jsp"></jsp:include>
<title>Lab Project Report</title>
<%
List<Object[]> projectClosureReport = (List<Object[]>)request.getAttribute("projectClosureReport");
List<Object[]> projectSPRCMeetings = (List<Object[]>)request.getAttribute("projectSPRCMeetings");

  if(projectSPRCMeetings!=null && !projectSPRCMeetings.isEmpty()){
	projectSPRCMeetings = projectSPRCMeetings.stream()
			.filter(e->e[6].toString().equalsIgnoreCase(LocalDate.now().getYear()+""))
							.collect(Collectors.toList());
} 
List<Object[]> projectPMRCMeetings = (List<Object[]>)request.getAttribute("projectPMRCMeetings");

if(projectPMRCMeetings!=null && !projectPMRCMeetings.isEmpty()){
	projectPMRCMeetings = projectPMRCMeetings.stream()
			.filter(e->e[6].toString().equalsIgnoreCase(LocalDate.now().getYear()+""))
							.collect(Collectors.toList());
}

List<Object[]> ebMeetings = (List<Object[]>)request.getAttribute("ebMeetings");

if(ebMeetings!=null && !ebMeetings.isEmpty()){
	ebMeetings = ebMeetings.stream()
			.filter(e->e[6].toString().equalsIgnoreCase(LocalDate.now().getYear()+""))
							.collect(Collectors.toList());
}

List<Object[]> projectList = (List<Object[]>)request.getAttribute("projectList");
List<Object[]> projects = (List<Object[]>)request.getAttribute("getAllProjectdata");
List<Object[]> mainProjectList =  projects!=null && projects.size()>0 ? (projects.stream().filter(e-> e[21]!=null && e[21].toString().equals("1")).collect(Collectors.toList())): new ArrayList<Object[]>();
List<Object[]> subProjectList =  projects!=null && projects.size()>0 ? (projects.stream().filter(e-> e[21]!=null && e[21].toString().equals("0")).collect(Collectors.toList())): new ArrayList<Object[]>();
SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
String LabCode = (String) session.getAttribute("labcode");

LocalDate today = LocalDate.now();
LocalDate threeMonthDate = LocalDate.now().plusMonths(3) ;
%>
<style>

p{
  text-align: justify;
  text-justify: inter-word;
}

 th
 {
 	border: 1px solid black;
 	text-align: center;
 	padding: 5px;
	overflow-wrap: break-word;
 }
 
 td
 {
 	border: 1px solid black;
 	text-align: left;
 	padding: 5px;
 	overflow-wrap: break-word;
 }

th, td
{

	word-break :normal;
}

.break
	{
		page-break-after: always;
		margin: 25px 0px 25px 0px;
	} 
	 
#pageborder {
      position:fixed;
      left: 0;
      right: 0;
      top: 0;
      bottom: 0;
      border: 2px solid black;
}     
 

 .border
 {
 	border: 1px solid black;
 }
 .textleft{
 	text-align: left;
 }
 div
 {

 }
 
 th
 {
 	border: 1px solid black;
 	text-align: center;
 	padding: 5px;
 }
 
 td
 {
 	border: 1px solid black;
 	text-align: left;
 	padding: 5px;
 }
 
  
 .textcenter{
 	
 	text-align: center;
 }

 .sth
 {
 	   
 	   border: 1px solid black;
 }
 
 .std
 {
 	text-align: center;
 	border: 1px solid black;
 }
 
 
  #containers {
    width: 100%;
    height: 100%;
    margin: 0;
    padding: 0;
}
.subjectDiv{
color:maroon;
margin-left: 20px;
font-weight: 600;
}

table{
width: 100%
}
.headerclass{
font-size: 1.5rem;
padding: 15px;
}
</style>
</head>
<body>
<div align="center"> 
<button onclick="exportMultipleTablesToExcel()" class="btn submit">Excel Download</button>
<a class="btn btn-lg" style="font-weight: bold"  href="LabAllProjectReport.htm" target="blank">
<img alt="" src="view/images/pdf.png" style="width:25px"></a>
</div>
<div class="row p-2 m-2 bg-light" >
<%NFormatConvertion nfc=new NFormatConvertion(); %>
<div class="col-md-12">
  <a class="badge badge-light headerclass" data-toggle="collapse" href="#collapseExample" role="button" aria-expanded="false" aria-controls="collapseExample">
    1.  &nbsp; &nbsp;Details of the ongoing Projects / Programmes of <%=LabCode %>
  </a>

</div>
<div class="col-md-12 collapse show"  id="collapseExample">
<table class="subtables table table-bordered table-stripped" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 10px; border-collapse:collapse;margin-right:10px;" id="table1">
<thead>
<tr class="subjectDiv" > <td colspan="8">1.  &nbsp; &nbsp;Details of the ongoing Projects / Programmes of <%=LabCode %></td> </tr>

<tr  style="margin-left: 20px;font-weight: bold">  <td colspan="8">Main Projects </td></tr>


<tr>
<th style="width:80px">SN</th>
<th style="width:200px">Title of Project</th>
<th style="width:80px">Project No</th>
<th style="width:120px">Cost (in Cr)<br>Org/ Cur</th>
<th style="width:80px">DOS</th>
<th style="width:120px">PDC <br> Org / Cur</th>
<th style="width:150px">Project Director</th>
<th style="width:100px">Status of completion</th>

</tr>
</thead>
<tbody>
<%if(mainProjectList.size()!=0){ 
int count=0;
	for(Object[]obj:mainProjectList){
		double cost = Double.parseDouble(obj[3]!=null?obj[3].toString():"0");
		double exp = Double.parseDouble(obj[16]!=null? obj[16].toString():"0");
%>
<tr style="font-size: 13px;">
<td style="text-align: center;"><%=++count %>.</td>
<td style="text-align: left: ;"><%=obj[1]!=null? obj[1].toString():"" %> </td>
<td style="text-align: center"><%=obj[12]!=null?obj[12].toString():"" %> </td>
<td style="text-align: right ;color: maroon;">
<%=nfc.convert(cost / 10000000)%> / <span style="color: green;"><%=nfc.convert(exp / 10000000)%></span></td>
<td style="text-align: center"> <%=sdf.format(obj[5])%></td>
<td style="text-align: center;"> <%=sdf.format(obj[4])%> <br> <%=obj[35] !=null ? sdf.format(obj[35]):""%></td>
<td style="text-align: left"> <%=obj[33].toString() %>, <%=obj[34].toString() %></td>
<td style="text-align: left;"> <%=obj[14] !=null ? obj[14]:"-"%></td>
</tr>
<%}}else{ %>
<tr>
<td colspan="8" style="text-align: center;">NO Data Available</td>
</tr>
<%} %>
</tbody>
<!-- </table>

<table class="subtables" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 10px; border-collapse:collapse; margin-right:10px;" >
 --> 
<thead>
<tr style="margin-left: 20px;font-weight: bold">
<td colspan="8">
Sub Projects</td> </tr>

<tr>
<th>SN</th>
<th style="width:200px">Title of Project</th>
<th style="width:80px">Project No</th>
<th style="width:120px">Cost (in Cr)<br>Org/ Cur</th>
<th style="width:80px">DOS</th>
<th style="width:120px">PDC <br> Org / Cur</th>
<th style="width:150px">Project Director</th>
<th style="width:100px">Status of completion</th>

</tr>
</thead>
<tbody>
<%if(subProjectList.size()!=0){ 
int count=0;
	for(Object[]obj:subProjectList){
		double cost = Double.parseDouble(obj[3]!=null?obj[3].toString():"0");
		double exp = Double.parseDouble(obj[16]!=null? obj[16].toString():"0");
%>
<tr style="font-size: 13px;">
<td style="text-align: center;"><%=++count %>.</td>
<td style="text-align: left: ;"><%=obj[1]!=null? obj[1].toString():"" %> </td>
<td style="text-align: center"><%=obj[12]!=null?obj[12].toString():"" %> </td>
<td style="text-align: right ;color: maroon;">
<%=nfc.convert(cost / 10000000)%> / <span style="color: green;"><%=nfc.convert(exp / 10000000)%></span></td>
<td style="text-align: center"> <%=sdf.format(obj[5])%></td>
<td style="text-align: center;"> <%=sdf.format(obj[4])%> <br> <%=obj[35] !=null ? sdf.format(obj[35]):""%></td>
<td style="text-align: left"> <%=obj[33].toString() %>, <%=obj[34].toString() %></td>
<td style="text-align: left;"> <%=obj[14] !=null ? obj[14]:"-"%></td>
</tr>
<%}}else{ %>
<tr>
<td colspan="8" style="text-align: center;">NO Data Available</td>
</tr>
<%} %>
</tbody>
</table>
 </div>
<br>
<div class="col-md-12">
  <a class="badge badge-light headerclass" data-toggle="collapse" href="#collapseExample2" role="button" aria-expanded="false" aria-controls="collapseExample2">
2.  &nbsp; &nbsp;Project Expenditure Status (as on date) 
  </a>

</div>

<div class="col-md-12 collapse show" id="collapseExample2">
<table class="subtables table table-bordered table-stripped" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 10px; border-collapse:collapse; margin-right:10px;" id="table2">
<thead>
<tr class="subjectDiv"><td colspan="11">
2.  &nbsp; &nbsp;Project Expenditure Status (as on date) </td></tr>

<tr>
<th style="width:60px">SN</th>
<th style="width:60px"> Code</th>
<th style="width:200px">Name</th>
<th style="width:100px">Category</th>
<th style="width:100px">DOS</th>
<th style="width:100px">PDC </th>
<th style="">Sanction cost <br><span style="font-size: 10px;"> (&#8377; In Cr )</span></th>
<th style="">Expenditure <br> <span style="font-size: 10px;"> (&#8377; In Cr )</span> </th>
<th style="">Out Commitment  <br> <span style="font-size: 10px;"> (&#8377; In Cr )</span> </th>
<th style="">Dipl <br> <span style="font-size: 10px;"> (&#8377; In Cr )</span></th>
<th style="">Balance <br> <span style="font-size: 10px;"> (&#8377; In Cr )</span></th>
</tr>
</thead>
<tbody>
<%if(projects.size()!=0) {
int count=0;
for(Object[]obj:projects){
%>
<tr style="font-size: 13px;">
<td style="text-align: center;"> <%=++count %>.</td>
<td style="text-align: center;"> <%=obj[12]!=null?obj[12]:"-" %></td>
<td style="text-align: left;"> <%=obj[1]!=null?obj[1]:"-" %></td>
<td style="text-align: center;"><%=obj[32]!=null?obj[32]:"-" %> </td>
<td style="text-align: center;"> <%=sdf.format(obj[5])%></td>
<td style="text-align: center;"> <%=obj[35] !=null ? sdf.format(obj[35]):sdf.format(obj[4])%></td>
<td style="text-align: right;"> 
<%if(obj[3]!= null) { %>
<%=String.format("%.2f", Double.parseDouble(obj[3].toString())/10000000)%>
 <%} %>
 </td>
<td style="text-align: right;"> 
<%if(obj[16]!= null) { %>
<%=String.format("%.2f", Double.parseDouble(obj[16].toString())/10000000)%>
 <%}else{ %>
 0.00
 <%} %>
 </td>
<td style="text-align: right;"> 
<%if(obj[17]!= null) { %>
<%=String.format("%.2f", Double.parseDouble(obj[17].toString())/10000000)%>
 <%}else{ %>
 0.00
 <%} %>
 </td>
<td style="text-align: right;"> 
<%if(obj[18]!= null) { %>
<%=String.format("%.2f", Double.parseDouble(obj[18].toString())/10000000)%>
  <%}else{ %>
 0.00
 <%} %>
 </td>
<td style="text-align: right;"> 
<%if(obj[19]!= null) { %>
<%=String.format("%.2f", Double.parseDouble(obj[19].toString())/10000000)%>
 <%}else{ %>
 0.00
 <%} %>
 </td>
</tr>
<%}}else{ %>
<tr>
<td colspan="11" style="text-align: center;">NO Data Available</td>
</tr>
<%} %>
</tbody>
</table>
</div>
<br>
<div class="col-md-12">
  <a class="badge badge-light headerclass" data-toggle="collapse" href="#collapseExample3" role="button" aria-expanded="false" aria-controls="collapseExample3">
3.  &nbsp; &nbsp;<%=LabCode%> PD list (as on date)
  </a>

</div>
<div class="col-md-12 collapse show"  id="collapseExample3">
<table class="subtables table table-bordered table-stripped" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 10px; margin-right: 10px; border-collapse:collapse;" id="table3">
<thead>
<tr class="subjectDiv">
<td colspan="6">
3.  &nbsp; &nbsp;<%=LabCode%> PD list (as on date)</td>
 </tr>

<tr>

<th style="width:200px">PD Name Design</th>
<th style="width:300px">Project Name / Code </th>
<th style="width:50px">Category</th>
<th style="width:120px">Mobile No.</th>
<th style="width:150px">DRONA Email ID</th>
<th style="width:150px">Govt. Email ID</th>
</tr>
</thead>
<tbody>
<%if(projectList!=null && projectList.size()>0) {
while(!projectList.isEmpty()){
	String projectDirector = projectList.get(0)[5].toString();
%>
<tr style="font-size: 13px;">
<td> <%=projectList.get(0)[3] %>, <%=projectList.get(0)[9] %></td>
<td>
<%= projectList.stream().filter(e->e[5].toString().equalsIgnoreCase(projectDirector))
	.map(e->"<p>"+e[1].toString()+" ( "+ e[2].toString() +" )"+"</p><br>")
	.collect(Collectors.joining())
%>
</td>
<td style="text-align: center;">
<%= projectList.stream().filter(e->e[5].toString().equalsIgnoreCase(projectDirector))
	.map(e->"<p>"+e[4].toString()+"</p><br>")
	.collect(Collectors.joining())
%>
</td>

<td style="text-align: center;"><%=projectList.get(0)[6]!=null?projectList.get(0)[6]:"-" %></td>
<td  style="text-align: center;"> <%=projectList.get(0)[7]!=null?projectList.get(0)[7]:"-"  %></td>
<td  style="text-align: center;"> <%=projectList.get(0)[8]!=null?projectList.get(0)[8]:"-"  %></td>


</tr>
<%
projectList = projectList.stream().filter(e->!e[5].toString().equalsIgnoreCase(projectDirector))
			.collect(Collectors.toList());
}}else{%>

<tr>
<td colspan="6">No Data Available</td>
</tr>
<%} %>
</tbody>
</table>
</div>
<br>
<div class="col-md-12">
  <a class="badge badge-light headerclass" data-toggle="collapse" href="#collapseExample4" role="button" aria-expanded="false" aria-controls="collapseExample4">
	4. &nbsp; &nbsp;List of Current Projects whose PDC has expired and will be expiring in next three months.
  </a>

</div>

<div class="col-md-12 collapse show" id="collapseExample4">
<table class="subtables table table-bordered table-stripped" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 10px; margin-right: 10px; border-collapse:collapse;" id="table4">
<thead>
<tr class="subjectDiv"><td colspan="7">4.  &nbsp; &nbsp;List of Current Projects whose PDC has expired and will be expiring in next three months.</td> </tr>

<tr>
<th style="text-align: center;width: 60px;">SN</th>
<th style="width:100px">Project Code</th>
<th style="width:200px">Project Name </th>
<th style="width:100">Category</th>
<th style="width:120px">PDC</th>
<th style="width:150px">Current Status</th>
<th style="width:200px">PD Name / Design</th>
</tr>
</thead>
<tbody>
<%if(!projects.isEmpty()){
int count=0;
for(Object[]obj:projects){

	if((obj[35]!=null && (LocalDate.parse(obj[35].toString()).isBefore(today) ||   LocalDate.parse(obj[35].toString()).isBefore(threeMonthDate)) )
			||(obj[4]!=null && (LocalDate.parse(obj[4].toString()).isBefore(today) ||   LocalDate.parse(obj[4].toString()).isBefore(threeMonthDate))) ){
%>
<tr style="font-size: 13px;">
<td style="text-align: center;"><%=++count%>. </td>
<td style="text-align: center;"> <%=obj[12]!=null?obj[12]:"-" %></td>
<td style="text-align: left;"> <%=obj[1]!=null?obj[1]:"-" %></td>
<td style="text-align: center;"><%=obj[32]!=null?obj[32]:"-" %> </td>
<td style="text-align: center;"> <%=obj[35] !=null ? sdf.format(obj[35]):sdf.format(obj[4])%></td>

<td style="text-align: left;"> <%=obj[14] !=null ? obj[14]:"-"%></td>
<td style="text-align: left"> <%=obj[33].toString() %>, <%=obj[34].toString() %></td>
</tr>
<%}}}else{ %>
<tr>
<td colspan="7">No Data Available</td>
</tr>
<%} %>
</tbody>
</table>
</div>
<br>
<div class="col-md-12">
  <a class="badge badge-light headerclass" data-toggle="collapse" href="#collapseExample5" role="button" aria-expanded="false" aria-controls="collapseExample5">
	5.  &nbsp; &nbsp;Project Closure Status
  </a>

</div>
<div class="col-md-12 collapse show" id="collapseExample5">
<table class="subtables table table-bordered table-stripped" style="align: left; margin-top: 5px; margin-bottom: 10px; margin-left: 10px; margin-right: 10px; border-collapse:collapse;" id="table5">
<thead>
<tr class="subjectDiv"> <td colspan="10">5.  &nbsp; &nbsp;Project Closure Status </td></tr>
 
    <tr>
      <th style="text-align: center;width:50px;" rowspan="2">SN</th>
      <th style="width:100px;" rowspan="2">Project Code</th>
            <th style="width:100px;" rowspan="2">PDC</th>
      <th style="text-align: left;" colspan="4">Admin Closure Report</th>
      <th style="text-align: left;" colspan="2">Technical Closure Report</th>
      <th rowspan="2">Remarks</th>
    </tr>
    <tr>
      <th style="width:100px;">Submitted to AG</th>
      <th style="width:100px;">Submitted to CDA</th>
      <th style="width:100px;">Submitted to O/o DG(ECS) / CFA</th>
      <th style="width:100px;">Approved by DG(ECS) / CFA</th>
      <th style="width:100px;">Submitted to O/o DG(ECS)</th>
      <th style="width:100px;">Accepted by DG(ECS)</th>
   
    </tr>
  </thead>
<tbody>
<%if(projectClosureReport!=null){
	int count =0;
	for(Object[]obj:projectClosureReport){
	%>
<tr style="font-size: 13px;">
<td style="text-align: center;"><%=++count %></td>
<td style="text-align: center;"><%=obj[2] %> </td>
<td style="text-align: center;"><%=sdf.format(obj[8]) %></td>
<td>-</td>
<td>-</td>
<td>-</td>
<td></td>
<td>-</td>
<td>-</td>
<td style="text-align: center;"><%=obj[7]!=null?obj[7]:"-" %></td>

</tr>


<%}} %>
</tbody>
</table>
</div>
<br>
<div class="col-md-12">
  <a class="badge badge-light headerclass" data-toggle="collapse" href="#collapseExample6" role="button" aria-expanded="false" aria-controls="collapseExample6">
  6.  &nbsp; &nbsp;PMRC Details
  </a>

</div>
<div class="col-md-12 collapse show" id="collapseExample6">
<table class="subtables table table-bordered table-stripped" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 10px; margin-right: 10px; border-collapse:collapse;width: 100%" id="table6">
<thead>
<tr class="subjectDiv"> <td colspan="14">6.  &nbsp; &nbsp;PMRC Details</td> </tr>

<tr>
<th style="text-align: center" rowspan="2">SN</th>
<th style="width:100px" rowspan="2">Project Code</th>
<th style="text-align: center" colspan="12"> <%=LocalDate.now().getYear() %></th> </tr>
<tr>
<th style="width:100px">JAN</th>
<th style="width:100px">FEB</th>
<th style="width:100px">MAR</th>
<th style="width:100px">APR</th>
<th style="width:100px">MAY</th>
<th style="width:100px">JUN</th>
<th style="width:100px">JUL</th>
<th style="width:100px">AUG</th>
<th style="width:100px">SEP</th>
<th style="width:100px">OCT</th>
<th style="width:100px">NOV</th>
<th style="width:100px">DEC</th>
</tr>
</thead>
<tbody>
<%if(!projectPMRCMeetings.isEmpty()) {
	int count=0;
while(!projectPMRCMeetings.isEmpty()){
	String projectId = projectPMRCMeetings.get(0)[0].toString();
 	List<Object[]>subprojectMeetings = projectPMRCMeetings.stream().filter
			(e->e[0].toString().equalsIgnoreCase(projectId)).collect(Collectors.toList()); 
 	
%>
<tr style="font-size: 13px;">
<td style="text-align: center;"><%=++count %> </td>
<td style="text-align: center;"> <%=projectPMRCMeetings.get(0)[1] %> </td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){
%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("1"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>

</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("2"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>
</td>


<td >
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("3"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>
<%} %>
</td>

<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){
%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("4"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>
</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){%>
<%= subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("5"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>
<%} %>
</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("6"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>
</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){
%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("7"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>
</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){
%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("8"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>
</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){
%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("9"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>

</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){
%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("10"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>

</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){
%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("11"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>

</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){
%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("12"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>
</td>
</tr>

<%
projectPMRCMeetings = projectPMRCMeetings.stream().filter
(e->!e[0].toString().equalsIgnoreCase(projectId)).collect(Collectors.toList());
%>

<%


}}else{ %>
<tr>
<td colspan="14" style="text-align: center;">No Data Available</td>
</tr>

<%} %>

</tbody>
</table>
</div>
<br>
<div class="col-md-12">
  <a class="badge badge-light headerclass" data-toggle="collapse" href="#collapseExample7" role="button" aria-expanded="false" aria-controls="collapseExample7">
  7.  &nbsp; &nbsp;EB Details
  </a>

</div>
<div class="col-md-12 collapse show" id="collapseExample7">
<table class="subtables table table-bordered table-stripped" style="align: left; margin-top: 5px; margin-bottom: 10px; margin-left: 10px; margin-right: 10px; border-collapse:collapse;;width: 100%" id="table7">
<thead>
<tr class="subjectDiv"> <td colspan="14">7.  &nbsp; &nbsp;EB Details</td> </tr>

<tr>
<th style="text-align: center" rowspan="2">SN</th>
<th style="width:100px" rowspan="2">Project Code</th>
<th style="text-align: center" colspan="12"> <%=LocalDate.now().getYear() %></th> </tr>
<tr>
<th style="width:100px">JAN</th>
<th style="width:100px">FEB</th>
<th style="width:100px">MAR</th>
<th style="width:100px">APR</th>
<th style="width:100px">MAY</th>
<th style="width:100px">JUN</th>
<th style="width:100px">JUL</th>
<th style="width:100px">AUG</th>
<th style="width:100px">SEP</th>
<th style="width:100px">OCT</th>
<th style="width:100px">NOV</th>
<th style="width:100px">DEC</th>
</tr>
</thead>
<tbody>
<%if(!ebMeetings.isEmpty()) {
	int count=0;
while(!ebMeetings.isEmpty()){
	String projectId = ebMeetings.get(0)[0].toString();
 	List<Object[]>subprojectMeetings = ebMeetings.stream().filter
			(e->e[0].toString().equalsIgnoreCase(projectId)).collect(Collectors.toList()); 
%>
<tr style="font-size: 13px;">
<td style="text-align: center;"><%=++count %></td>
<td style="text-align: center;"> <%=ebMeetings.get(0)[1] %></td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){
%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("1"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>

</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("2"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>
</td>


<td >
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("3"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>
<%} %>
</td>

<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){
%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("4"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>
</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){%>
<%= subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("5"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>
<%} %>
</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("6"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>
</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){
%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("7"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>
</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){
%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("8"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>
</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){
%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("9"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>

</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){
%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("10"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>

</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){
%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("11"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>

</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){
%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("12"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>
</td>
</tr>
<%

ebMeetings = ebMeetings.stream().filter
(e->!e[0].toString().equalsIgnoreCase(projectId)).collect(Collectors.toList());
}}else{ %>

<tr>

<td colspan="14" style="text-align: center;">No Data Available</td>
</tr>


<%} %>
</tbody>
</table>
</div>
<br>
<div class="col-md-12">
  <a class="badge badge-light headerclass" data-toggle="collapse" href="#collapseExample8" role="button" aria-expanded="false" aria-controls="collapseExample8">
  8.  &nbsp; &nbsp;SPRC Details
  </a>

</div>
<div class="col-md-12 collapse show" id="collapseExample8">
<table class="subtables table table-bordered table-stripped" style="align: left; margin-top: 5px; margin-bottom: 10px; margin-left: 10px; margin-right: 10px; border-collapse:collapse;;width: 100%" id="table8">
<thead>
<tr class="subjectDiv"> <td colspan="14">8.  &nbsp; &nbsp;SPRC Details</td> </tr>

<tr>
<th style="text-align: center" rowspan="2">SN</th>
<th style="width:100px" rowspan="2">Project Code</th>
<th style="text-align: center" colspan="12"> <%=LocalDate.now().getYear() %></th> </tr>
<tr>
<th style="width:100px">JAN</th>
<th style="width:100px">FEB</th>
<th style="width:100px">MAR</th>
<th style="width:100px">APR</th>
<th style="width:100px">MAY</th>
<th style="width:100px">JUN</th>
<th style="width:100px">JUL</th>
<th style="width:100px">AUG</th>
<th style="width:100px">SEP</th>
<th style="width:100px">OCT</th>
<th style="width:100px">NOV</th>
<th style="width:100px">DEC</th>
</tr>
</thead>
<tbody>
<%if(!projectSPRCMeetings.isEmpty()) {
	int count=0;
while(!projectSPRCMeetings.isEmpty()){
	String projectId = projectSPRCMeetings.get(0)[0].toString();
 	List<Object[]>subprojectMeetings = projectSPRCMeetings.stream().filter
			(e->e[0].toString().equalsIgnoreCase(projectId)).collect(Collectors.toList()); 
%>
<tr style="font-size: 13px;">
<td style="text-align: center;"><%=++count %></td>
<td style="text-align: center;"> <%=projectSPRCMeetings.get(0)[1] %></td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){
%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("1"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>

</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("2"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>
</td>


<td >
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("3"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>
<%} %>
</td>

<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){
%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("4"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>
</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){%>
<%= subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("5"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>
<%} %>
</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("6"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>
</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){
%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("7"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>
</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){
%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("8"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>
</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){
%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("9"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>

</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){
%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("10"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>

</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){
%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("11"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>

</td>
<td>
<% if(subprojectMeetings!=null  && !subprojectMeetings.isEmpty()){
%>
<%=  subprojectMeetings.stream().filter(e->e[5].toString().equalsIgnoreCase("12"))
.map(e->sdf.format(e[3]).toString() )
.collect(Collectors.joining("<br>")) %>

<%} %>
</td>
</tr>
<%

projectSPRCMeetings = projectSPRCMeetings.stream().filter
(e->!e[0].toString().equalsIgnoreCase(projectId)).collect(Collectors.toList());
}}else{ %>

<tr>

<td colspan="14" style="text-align: center;">No Data Available</td>
</tr>


<%} %>
</tbody>
</table>
</div>
</div>
<script>
function exportMultipleTablesToExcel() {
	
 	 // Step 1: Show SweetAlert2 loading
	  Swal.fire({
	    title: 'Please wait...',
	    text: 'Preparing your Excel file',
	    allowOutsideClick: false,
	    allowEscapeKey: false,
	    didOpen: () => {
	      Swal.showLoading();
	    }
	  });

	  // Step 2: Delay Excel generation by 3 seconds
	  setTimeout(() => {
	    var wb = XLSX.utils.book_new();

	    var tables = [
	      { id: "table1", name: "Ongoing Projects" },
	      { id: "table2", name: "Expenditure Status" },
	      { id: "table3", name: "PD list" },
	      { id: "table4", name: "Expired Projects" },
	      { id: "table5", name: "Closure Status" },
	      { id: "table6", name: "PMRC Details" },
	      { id: "table7", name: "EB Details" },
	      { id: "table8", name: "SPRC Details" }
	    ];

	    tables.forEach(table => {
	      var el = document.getElementById(table.id);
	      if (el) {
	        var ws = XLSX.utils.table_to_sheet(el);
	        XLSX.utils.book_append_sheet(wb, ws, table.name);
	      }
	    });

	    XLSX.writeFile(wb, "tables-data.xlsx");
	    Swal.close();
	  }, 3000); // 3 seconds
	  
}

$( document ).ready(function() {
/* 	exportMultipleTablesToExcel(); */
});
</script>
</body>

</html>