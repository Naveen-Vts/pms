<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@ page import="java.time.LocalDate"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat,java.text.ParseException,java.math.BigInteger"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@page import="com.vts.pfms.master.dto.ProjectSanctionDetailsMaster"%>
<%@page import="com.vts.pfms.IndianRupeeFormat" %>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
<spring:url value="/resources/css/sweetalert2.min.css" var="sweetalertCss" />
<spring:url value="/resources/js/sweetalert2.min.js" var="sweetalertJs" />
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>PMS</title>

<jsp:include page="../static/header.jsp"></jsp:include>

<spring:url value="/resources/css/newfont.css" var="NewFontCss" />
<link href="${NewFontCss}" rel="stylesheet" /> 
<link href="${sweetalertCss}" rel="stylesheet" />
	<script src="${sweetalertJs}"></script>
<spring:url value="/resources/css/master.css" var="masterCss" />
<link href="${masterCss}" rel="stylesheet" />

<spring:url value="/resources/css/masterdashboard.css" var="masterdashboardCss" />
<link href="${masterdashboardCss}" rel="stylesheet" />
<spring:url value="/resources/css/header/dashBoard.css" var="dashBoard" />
<link href="${dashBoard}" rel="stylesheet" />



</head>

<body>
<!-- Mahesh code  -->
<%  if(request.getAttribute("showmodal").equals("yes")){ List<Object[]> projectdet=(List<Object[]>)request.getAttribute("lastupdatedate");%>
	<!-- Button trigger modal -->
	<button type="button"  id="showmodal"
		class="btn btn-primary" data-toggle="modal"
		data-target=".bd-example-modal-lg"></button>
	<!-- Modal -->	
	<div class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myExtraLargeModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg modal-dialog-centered" >
			<div class="modal-content ab1" >
				<div class="modal-header">
					<h5 class="modal-title" id="exampleModalLabel">Weekly Update</h5>
					<p   class="modal-title mx-auto d1"> 
					<select  class="form-control" name="UProjects" id="UProjects" 
					onclick="getElementById('Usubmit').value=getElementById('UProjects').value;"
					onchange="changedates(getElementById('UProjects').value)">
					<% List<Object[]> projectEmp = (List<Object[]>)request.getAttribute("projectsOfEmp"); %>
						<%  for (int i=0;i< projectEmp.size();i++){ %>
						
							<option class="form-control" value="<%= projectEmp.get(i)[0] %>"><%= projectEmp.get(i)[6]!=null?StringEscapeUtils.escapeHtml4(projectEmp.get(i)[6].toString()): " - " %> (<%= projectEmp.get(i)[4]!=null?StringEscapeUtils.escapeHtml4(projectEmp.get(i)[4].toString()): " - " %>)</option>
						
						<%} %>
					</select></p>
					
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form action="weeklyupdate.htm" id="weeklyupdate" onsubmit="getElementById('weeklyupdate').disable=true;">
					<div class="container" >
						<div class="row text-left">
							<div class="col-3 mt20mb20"  >
							<p id="actionmodifieddate"></p>
							</div>
							<div class="col-sm mt20mb20"
								>
								<lable for="actionpoints">Have you updated action
								Items?</lable>
							</div>
							<div class="col-3 mt20mb20"
								>
								
								<select required="required" class="form-control" name="actionpoints" id="actionpoints" required="required">
									<option value="" selected disabled hidden>Update Here</option>
									<option value="Yes">Yes</option>
									<option value="No">No</option>
									<option value="NA">NA</option>
								</select> <br />
							</div>
						</div>
						<div class="row text-left">
							<div class="col-3">
							<p id="meetingdate"></p>
							</div>
							<div class="col-sm">
								<lable for="Meeting">Have you updated Meeting details?</lable>
							</div>
							<div class="col-3">
								<select required="required" class="form-control" name="Meeting" id="Meeting" required="required"><option
										value="" selected disabled hidden>Update Here</option>
									<option value="Yes">Yes</option>
									<option value="No">No</option>
									<option value="NA">NA</option>
								</select> <br /> <br />
							</div>
						</div>
						<div class="row text-left">
							<div class="col-3">
							<p id="Milestones"></p>
							</div>
							<div class="col-sm">
								<lable for="Mile">Have you updated Milestones?</lable>
							</div>
							<div class="col-3">
								<select required="required" class="form-control" name="Mile" id="Mile" required="required">
								<option value="" selected disabled hidden>Update Here</option>
									<option value="Yes">Yes</option>
									<option value="No">No</option>
									<option value="NA">NA</option>
								</select> <br /> <br />
							</div>
						</div>
						<div class="row text-left">
							<div class="col-3">
							<p id="ProcurementDate"></p>
							</div>
							<div class="col-sm">
								<lable for="Procurement">Have you updated Procurement
								Status?</lable>
							</div>
							<div class="col-3">
								<select required="required" class="form-control" name="Procurement" id="Procurement" required="required"><option
										value="" selected disabled hidden>Update Here</option>
									<option value="Yes">Yes</option>
									<option value="No">No</option>
									<option value="NA">NA</option>
								</select> <br /> <br />
							</div>
						</div>
						<div class="row text-left">
							<div class="col-3">
							<p id="riskdetailsdate"></p>
							</div>
							<div class="col-sm">
								<lable for="riskdetails">Have you updated Risk details?
								</lable>
							</div>
							<div class="col-3">
								<select required="required" class="form-control" name="riskdetails" id="riskdetails" required="required"><option
										value="" selected disabled hidden=true;>Update Here</option>
									<option value="Yes">Yes</option>
									<option value="No">No</option>
									<option value="NA">NA</option>
								</select> <br /> <br />
							</div>
						</div>
						<button type="submit" name="USubmit" class="btn btn-primary wdith-300"  id="Usubmit">Update</button>
					</div>
				</form>
				<div class="modal-header">
					<p id='lastupdatedate' class="fl-left">last update date: ---- </p> </div>
				</div>
			</div>
			</div>
	<script type="text/javascript">
	document.getElementById("showmodal").click();

	<% if(projectdet!=null)for (int i=0;i<projectdet.size();i++){ %>
	if(document.getElementById('UProjects').value== <%=projectdet.get(i)[1]%> ){document.getElementById('lastupdatedate').innerHTML='last update date: '+'<%=projectdet.get(i)[0]!=null?StringEscapeUtils.escapeHtml4(projectEmp.get(i)[0].toString()): " - " %>';<%i++;%>}
	else {document.getElementById('lastupdatedate').innerHTML='last update date: '+'----';}<%}%>
	document.getElementById('Usubmit').value=document.getElementById('UProjects').value;
	
	$.ajax({
		
		type:"GET",
		url:"GetUpdateDates.htm",
		data :{
			
			ProjectId : document.getElementById('UProjects').value
			
		},
		datatype : 'json',
		success : function(result){
			
			var result = JSON.parse(result);
			var modifieddate=result[0];
			if (modifieddate[0]==',' || modifieddate[0][0]==null){document.getElementById('actionmodifieddate').innerHTML='----';}else{
			document.getElementById('actionmodifieddate').innerHTML=modifieddate[0][0].toString().substring(0,10);}
			var updateddate=result[1];
			if(updateddate.length>0){ 
			if ( result[1]==null  ){document.getElementById('lastupdatedate').innerHTML='last update date: '+'----';}else{
			document.getElementById('lastupdatedate').innerHTML='last update date: '+updateddate[0][0];}}
			else{
				document.getElementById('lastupdatedate').innerHTML='last update date: '+'----';
			}
			if (result[2][0][0]==null	){document.getElementById('riskdetailsdate').innerHTML='----';}else{
				document.getElementById('riskdetailsdate').innerHTML=result[2][0][0].toString().substring(0,10);}	
			if (result[3]==',' || result[3][0][0]==null	){document.getElementById('Milestones').innerHTML='----';}else{
				document.getElementById('Milestones').innerHTML=result[3][0][0].toString().substring(0,10);}
			if (result[4]==',' || result[4][0]==null	){document.getElementById('meetingdate').innerHTML='----';}else{
				document.getElementById('meetingdate').innerHTML=result[4][0].toString().substring(0,10);}
			if (result[5][0].length==0 || result[5][0][0]==null	){document.getElementById('ProcurementDate').innerHTML='----';}else{
				document.getElementById('ProcurementDate').innerHTML=result[5][0][0].toString().substring(0,10);}
		}
	});
	
	function changeit(val)
	{
		document.getElementById('weeklyupdate').reset();
		var name = document.getElementById('UProjects').innerHTML;
		var value = document.getElementById('UProjects').value;
	}
	
	function changedates(val)
	{
		document.getElementById('Usubmit').value=document.getElementById('UProjects').value;
		$pid=val;
		$.ajax({
			type:"GET",
			url:"GetUpdateDates.htm",
			data :{
				ProjectId : $pid
			},
			datatype : 'json',
			success : function(result){
				var result = JSON.parse(result);
				var modifieddate=result[0];
				if (modifieddate[0]==',' || modifieddate[0][0]==null){document.getElementById('actionmodifieddate').innerHTML='----';}else{
				document.getElementById('actionmodifieddate').innerHTML=modifieddate[0][0].toString().substring(0,10);}
				var updateddate=result[1];
				if(updateddate.length>0){ 
				if ( result[1]==null  ){document.getElementById('lastupdatedate').innerHTML='last update date: '+'----';}else{
				document.getElementById('lastupdatedate').innerHTML='last update date: '+updateddate[0][0];}}
				else{
					document.getElementById('lastupdatedate').innerHTML='last update date: '+'----';
				}
				if (result[2][0][0]==null	){document.getElementById('riskdetailsdate').innerHTML='----';}else{
					document.getElementById('riskdetailsdate').innerHTML=result[2][0][0].toString().substring(0,10);}	
				if (result[3]==',' || result[3][0][0]==null	){document.getElementById('Milestones').innerHTML='----';}else{
					document.getElementById('Milestones').innerHTML=result[3][0][0].toString().substring(0,10);}
				if (result[4]==',' || result[4][0]==null	){document.getElementById('meetingdate').innerHTML='----';}else{
					document.getElementById('meetingdate').innerHTML=result[4][0].toString().substring(0,10);}
				if (result[5][0]=='' || result[5][0][0]==null	){document.getElementById('ProcurementDate').innerHTML='----';}else{
					document.getElementById('ProcurementDate').innerHTML=result[5][0][0].toString().substring(0,10);}

			}
		});
	}
	</script>
<%} %>
<!-- end -->
<%
String Username =(String)session.getAttribute("Username");  
String EmpNo=(String)session.getAttribute("empNo");
String LabCode=(String)session.getAttribute("labcode");
String ibasUri=(String)request.getAttribute("ibasUri");
/* Long loginId=(Long)session.getAttribute("LoginId");  */
/* Long divisionId=(Long)session.getAttribute("Division");  */
/* Long empId =(Long)session.getAttribute("EmpId");  */
/* Long formRoleId=(Long)session.getAttribute("FormRole");  */
String statsUrl=(String)request.getAttribute("statsUrl");
String pmsToStatsUrl = statsUrl+"/login";
List<Object[]> todayschedulelist=(List<Object[]>)request.getAttribute("todayschedulelist");
List<Object[]> todaysMeetings= new ArrayList<>();
long todayMeetingCount=0;
if(todayschedulelist.size()>0){
	todayMeetingCount=todayschedulelist.stream().filter(i -> i[3].toString().equalsIgnoreCase(LocalDate.now().toString())).count();
	todaysMeetings=todayschedulelist.stream().filter(i -> i[3].toString().equalsIgnoreCase(LocalDate.now().toString())).collect(Collectors.toList());
}
ObjectMapper objectMapper = new ObjectMapper();
String jsonArray = objectMapper.writeValueAsString(todaysMeetings);

List<Object[]> rfaPendingCountList= (List<Object[]>)request.getAttribute("rfaPendingCountList");
Integer rfaForwardCount=(Integer)request.getAttribute("rfaForwardCount");
Integer rfaInspectionCount=(Integer)request.getAttribute("rfaInspectionCount");
Integer rfaInspectionAprCount=(Integer)request.getAttribute("rfaInspectionAprCount");
long todayRfaCount1=0;
List<String> status1 = Arrays.asList("AA","REV", "RC", "RV", "RE");
if(rfaPendingCountList!=null && rfaPendingCountList.size()>0){
	todayRfaCount1=rfaPendingCountList.stream().filter(i -> status1.contains(i[14].toString().toUpperCase())).count();
}
/* List<Object[]> todayactionlist=(List<Object[]>)request.getAttribute("todayactionlist"); */
List<Object[]>  notice=(List<Object[]>)request.getAttribute("dashbordNotice");
List<Object[]> actionscount=(List<Object[]>)request.getAttribute("actionscount");
Integer selfremindercount=(Integer)request.getAttribute("selfremindercount");  
Integer noticeElib= Integer.parseInt(request.getAttribute("noticeEligibility").toString());
/* List<Object[]> noticeList =(List<Object[]>)request.getAttribute("NotiecList"); */
Integer selfremaindercount=(Integer)request.getAttribute("selfremaindercount");
/* Object[] allschedulescount=(Object[])request.getAttribute("AllSchedulesCount"); */
List<ProjectSanctionDetailsMaster>  budgetlist=(List<ProjectSanctionDetailsMaster>)request.getAttribute("budgetlist");
List<Object[]> ProjectList=(List<Object[]>)request.getAttribute("ProjectList");
List<Object[]> ProjectMeetingCount=(List<Object[]>)request.getAttribute("ProjectMeetingCount");
/* Object[] GeneralMeetingCount=(Object[])request.getAttribute("GeneralMeetingCount"); */
List<Object[]> ganttchartlist=(List<Object[]>)request.getAttribute("ganttchartlist");
String interval =(String)request.getAttribute("interval");
String error = (String) request.getAttribute("errorMsg");
List<Object[]> MyTaskList=(List<Object[]>)request.getAttribute("mytasklist");
List<Object[]> approvallist=(List<Object[]>)request.getAttribute("approvallist");
List<Object[]> mytaskdetails=(List<Object[]>)request.getAttribute("mytaskdetails");
List<Object[]> dashboardactionpdc=(List<Object[]>)request.getAttribute("dashboardactionpdc");
ArrayList<String> loginlist=new ArrayList<String>(Arrays.asList("L","A","Y","P","Z","E","Q")); 
SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");  
String TodayDate = formatter.format(new Date()).toString().replace("/", "-") ;
List<Object[]> QuickLinkList=(List<Object[]>)request.getAttribute("QuickLinkList");
List<Object[]> ProjectHealthData = (List<Object[]>)request.getAttribute("projecthealthdata");
Object[] ProjectHealthTotalData = (Object[])request.getAttribute("projecthealthtotal"); 
//Object[] ChangesTotalData =(Object[])request.getAttribute("changestotalcount");
/* List<Object[]> CCMFinanceData = (List<Object[]>)request.getAttribute("CCMFinanceData"); */
List<Object[]> CashOutGo= (List<Object[]>)request.getAttribute("DashboardFinanceCashOutGo");
FormatConverter fc=new FormatConverter(); 
SimpleDateFormat sdf=fc.getRegularDateFormat();
SimpleDateFormat sdf3=new SimpleDateFormat("dd-MM-yy");
SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
SimpleDateFormat sdf2=new SimpleDateFormat("yy-MM-dd");
IndianRupeeFormat nfc=new IndianRupeeFormat();



String logintype="U";
String View="";
if(logintype!=null ){
	logintype=(String)request.getAttribute("logintype"); 
	if(logintype.equalsIgnoreCase("A")|| logintype.equalsIgnoreCase("P")|| logintype.equalsIgnoreCase("E") || logintype.equalsIgnoreCase("Z") || logintype.equalsIgnoreCase("Y")|| logintype.equalsIgnoreCase("Q") || logintype.equalsIgnoreCase("C") || logintype.equalsIgnoreCase("I") || logintype.equalsIgnoreCase("G") || logintype.equalsIgnoreCase("F")){
		logintype="A";
	}
}
String LoginTypes[] = {"A","P","E","Z","Y","Q","X","K","C","I","G","F"}  ;
int ProjectCount = 0;
List<Object[]> projecthealthtotaldg = (List<Object[]>)request.getAttribute("projecthealthtotaldg");
String IsDG = (String)request.getAttribute("IsDG");

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


<!-- @@@@@@@@ CONTAINER FLUID START @@@@@@@ -->
<div class="container-fluid" >


<!-- @@@@@@@@ MAIN ROW START @@@@@@@ -->	
	<div class="row m-b20" >
	
<!-- @@@@@@@@ MAIN ROW col-md-9 START @@@@@@@ -->			
		<div class="col-md-9" >
			
			
<!-- @@@@@@@@ NESTED ROW START @@@@@@@ -->				
				<div class="row">
			      
			     
<!-- @@@@@@@@@@ NESTED ROW SCHEDULE START @@@@@@@@@@  -->
              <div class="col-4 col-md-3" >

         <!-- ----------------PROJECT DASHBOARD PROJECT NAME DISPLAY START --------------- -->
			      	 <%if(ProjectList!=null){ %>
						 <div class="row pd1"  id="projectname" >
							<div class="col-md-12" align="center">
								<div id="carouselExampleSlidesOnly" class=""  data-ride=""  >
									<div class="carousel-inner">	
										<%	for (Object[] obj : ProjectList) { 
											String projectshortName=(obj[17]!=null)?" ( "+obj[17].toString()+" ) ":"";
										%>
										 <div class="carousel-item" id="projectname<%=obj[0]%>">
										 	<div class="row" >
												<div class="col-md-12 t1U" >
													<%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - "+projectshortName!=null?StringEscapeUtils.escapeHtml4(projectshortName): " - " %>
													<br><br>
												</div>
											</div>
										 </div>
										<%} %>
									</div>
								</div>
							</div>
					   </div> 
				 <%} %> 
			 <!-- ----------------PROJECT DASHBOARD PROJECT NAME DISPLAY END --------------- -->      
			      
			 <!-- ----------------- ACTION DASHBOARD TODAY'S SCHEDULE START -------------------------- -->
			 	
				      <div class="card d1u"  id="todayschedules">
							<nav class="navbar navbar-light bg-primary bg1" >
								<span class="text-light">Today's Schedule <span class="badge badge-today badge-success pos1" ></span> </span> 
					 	  		<%if(logintype.equalsIgnoreCase("A")) {%><button class="btn btn-sm" id="sendMail" onclick="sendMail('<%=LocalDate.now()%>')"> <i class="fa fa-paper-plane" aria-hidden="true"></i></button><%} %>
					 	  		<form class=" form-inline" method="post" action="MySchedules.htm" id="myform" >
									<input  class="btn btn-primary myschedule pot1" type="submit" name="sub" value=" &nbsp;&nbsp;"  >
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								</form>
							</nav>	
							<div class="card-body pp1"  >
								<div  class="text-dark">
								    <div class="height1">
								     <div id="carouselExampleControls5" class="carousel vert slide" data-ride="carousel" data-interval="5000">
										<div class="carousel-inner">
											<% 
											int count=0;
											if(todayschedulelist.size()>0){
										  for(Object[] obj : todayschedulelist){
											  	if(!obj[6].toString().equalsIgnoreCase("E")){
											  		if(obj[3].toString().equalsIgnoreCase(TodayDate)){
											  		%>
												<div class="carousel-item action height2" id="schedule" >
														<ul class="ul1">	
															<li class="list-group-item bg2" >
																 <a href="javascript:void(0)" onclick="location.href='CommitteeScheduleView.htm?scheduleid=<%=obj[2] %>' " class="text-dark" >
													                <i class="fa fa-arrow-right faa-pulse animated faa-fast cl3" aria-hidden="true" ></i> 
															    	<%=obj[7]!=null?StringEscapeUtils.escapeHtml4(obj[7].toString()): " - " %> -
															    	<i class="fa fa-clock-o" aria-hidden="true"></i> <%=obj[4] !=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - "%> &nbsp;&nbsp;
															    </a>
														 	</li>
														</ul>
												</div>
												<%count++;}} }}%>
								
												<%if(count==0) { %>
													<ul>
														<li class="list-group-item bg3"  >No Events ! </li>
													</ul>
												<%} %>
											
											</div>
										</div>									 
								    </div>
								</div>
					 	  </div>
					 	  <div class="card-footer pd4" >
					 	  	<a class="navbar-brand text-dark" href="MySchedules.htm" >Upcoming Schedule</a><span class="badge <%if(todayschedulelist.size()-count> 0) {%>  badge-danger <%} else { %> badge-success <%} %>badge-counter"><%if(todayschedulelist.size()-count >0){%> <%=todayschedulelist.size()-count %><%} %></span>
					 	  </div>
					 	  <div class="card-footer pd5" >
					 	  	<a class="navbar-brand text-dark"  href="ActionSelfReminderAdd.htm" >My Reminders</a><span class="badge <%if(selfremindercount> 0) {%>  badge-danger <%} %> badge-counter"><%if(selfremindercount >0){%><%=selfremindercount %><%} %></span>
					 	  </div>
					</div> 
					
		<!-- -----------------ACTION DASHBOARD TODAY'S SCHEDULE END -------------------------- -->
					
		<!-- ----------------PROJECT DASHBOARD PROJECT DETAILS LEFT SIDE START --------------- -->
					
					<div  class="prode" id="projectdetails1">
					 	 <%if(ProjectList!=null){ %>
						 <div class="row"  >
							<div class="col-md-12" align="center">
								<div id=carouselprojectdetailsSlidesOnly class=""  data-ride=""  >	
									<div class="carousel-inner">	
										<%	for (Object[] obj : ProjectList) { %>
										 <div class="carousel-item" id="projectdetailsname<%=obj[0]%>">
										 	<div class="row" >
												<div class="col-md-12 text-right" >
													<%if(obj[8]!=null) { if(!obj[8].toString().equals("0")){ %>
													<h6>Project Code : </h6>
													<h6>Project Name : </h6>
													<br>
													<h6>Sanction Date : </h6>
													<h6>PDC : </h6>
													<h6>PMRC Due On : </h6>
													<h6>EB Due On : </h6>
													<%}}else{ %>
														<br><br><br><br><br><br><br>
													<%} %>
													<br><br>
												</div>
											</div>
										 </div>
										<%} %>
									</div>
								</div>
							</div>
					</div> 
				 <%} %>
		  </div>
					 
	    <!--------------- PROJECT DASHBOARD PROJECT DETAILS LEFT SIDE END  ------------- -->		 
					 	
		<!---------------- OVERALL DASHBOARD CHANGES,WEEK,TODAY AND MONTH DIV START --------->
					 	
				<div class="overallheader overallheaderNone" id="changes-tab">
					<% if(Arrays.asList(LoginTypes).contains((String)request.getAttribute("logintype")) && (ProjectList!=null && ProjectList.size()>0)){ %>
					<div data-toggle="tooltip" title="" class="text-left" >
					<button data-toggle="tooltip" onclick ="showDashboardProjectModal()" class="btn btn-sm bg-transparent faa-pulse animated faa-fast cu-p"   type="button"  data-toggle="tooltip" data-placement="right"  title="Select DashBoard Projects" ><img src="view/images/dashboard.png" class="wi-25" ></button>
					<jsp:include page="../static/DashBoardSelection.jsp"></jsp:include></div>  
					<%} %>
						
				</div>
     <!----------------- OVERALL DASHBOARD CHANGES,WEEK,TODAY AND MONTH DIV END ---------------->
         </div>
<!-- @@@@@@@@@@ NESTED ROW SCHEDULE END @@@@@@@@@@  -->		     

<!-- @@@@@@@@@@ NESTED ROW(2) START @@@@@@@@@@  -->
              
               <div class="col-4 col-md-3" >
			      
		 <!-- -----------------PROJECT DASHBOARD PROJECTDROPDOWN START---------------------------- -->
			      		<%if(ProjectList!=null){ %>
						 <div class="row">
							<div class="col-md-12 pd-1"  id="projectdropdown" >
								<select class="form-control selectdee" id="projectid" required="required" name="projectid" onchange="dropdown()"  >
									<%	for (Object[] obj2 : ProjectList) {
										String projectshortName=(obj2[17]!=null)?" ( "+obj2[17].toString()+" ) ":"";
										%>
										<option value="<%=obj2[0]%>"   ><%=obj2[4]!=null?StringEscapeUtils.escapeHtml4(obj2[4].toString()): " - "+projectshortName!=null?StringEscapeUtils.escapeHtml4(projectshortName): " - "%></option>
									<%} %>
								</select>
								<br><br>
						    </div>
						</div> 
				 	<%} %> 
	   <!-- -----------------PROJECT DASHBOARD PROJECTDROPDOWN END---------------------------- -->

	   <!-- -----------------ACTION DASHBOARD APPROVAL'S(TO BE APPROVED BY ME) SCHEDULE START---------------------------- -->
							
						<div class="card bg-t"  id="approvalblock">
							<nav class="navbar navbar-light bg-primary bg-4" >
								<a class="navbar-brand text-light"  >To be Approved By Me</a>
							</nav>					
							<div id="carouselExampleControls8" class="carousel slide carousel-interval p312" data-ride="carousel"  >
								<div class="carousel-inner">
									<% int approvalcount=0;
										for(Object[] obj : approvallist){ %>
										<%if((obj[0]).toString().equalsIgnoreCase("DO")){ if(Integer.valueOf((String) obj[1].toString())>0){ %>
									<div class="card-footer pd4" >
										<a class="navbar-brand text-dark" href="ProjectApprovalPd.htm"  id="" >Initiation (DO)
										<i class="fa fa-bell fa-fw c-p" aria-hidden="true" ></i> 
										<span class="badge badge-danger badge-counter approval" id=""><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %></span> 
										</a>
									</div>
										<%approvalcount++; }} %>
										<%if((obj[0]).toString().equalsIgnoreCase("RTMD-DO")){ if(Integer.valueOf((String) obj[1].toString())>0){ %>
									<div class="card-footer pd4" >
										<a class="navbar-brand text-dark" href="ProjectApprovalRtmddo.htm"  id="" >Initiation (P&C-DO)
										<i class="fa fa-bell fa-fw c-p" aria-hidden="true" ></i>
										<span class="badge badge-danger badge-counter approval" id=""><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %></span></a>
									</div>
										<%approvalcount++;} }%>
										<%if((obj[0]).toString().equalsIgnoreCase("AD")){ if(Integer.valueOf((String) obj[1].toString())>0){  %>
									<div class="card-footer pd4" >
										<a class="navbar-brand text-dark" href="ProjectApprovalAd.htm"  id="" >Initiation (AD)
										<i class="fa fa-bell fa-fw c-p" aria-hidden="true" ></i>
										<span class="badge badge-danger badge-counter approval" id=""><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %></span></a>
									</div>
										<%approvalcount++;} }%>
										<%if((obj[0]).toString().equalsIgnoreCase("TCM")){ if(Integer.valueOf((String) obj[1].toString())>0){  %>
									<div class="card-footer pd4">
										<a class="navbar-brand text-dark" href="ProjectApprovalTcc.htm"  id="" >Initiation (TCM)
										<i class="fa fa-bell fa-fw c-p" aria-hidden="true" ></i>
										<span class="badge badge-danger badge-counter approval" id=""><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %></span></a>
									</div> 
										<%approvalcount++;}} %>
										<%if((obj[0]).toString().equalsIgnoreCase("Meeting")){ if(Integer.valueOf((String) obj[1].toString())>0){  %>
									
								
										<%/* approvalcount++; */}} %>
										<%if((obj[0]).toString().equalsIgnoreCase("Committee")){ if(Integer.valueOf((String) obj[1].toString())>0){  %>
									<div class="card-footer pd4" >
										<a class="navbar-brand text-dark" href="CommitteeMainApprovalList.htm"  id="" >Committee
										<i class="fa fa-bell fa-fw c-p" aria-hidden="true" ></i>
										<span class="badge badge-danger badge-counter approval" id=""><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %></span></a>
									</div> 
										<%approvalcount++;}} %>
									<%} %>
									<%if(approvalcount==0){ %>
										<div class="card-footer pd4" s>
										<a class="navbar-brand text-dark"   id="" >No Approvals</a>
									</div> 
									<%} %>
								</div>
							</div> 
						</div>
						
         <!-- -------------------------ACTION DASHBOARD APPROVAL'S SCHEDULE END---------------------------  -->				
					
	     <!-- -----------------PROJECT DASHBOARD SELECTED PROJECTED DETAILS DISPLAY START---------------------------- -->				
						
					<div  class="projectd-2" id="projectdetails2">
					 	<%if(ProjectList!=null){ %>
						 <div class="row"  >
							<div class="col-md-12" align="center">
								<div id=carouselprojectdetailsSlides2Only class=""  data-ride=""  >	
									<div class="carousel-inner">	
										<%	for (Object[] obj : ProjectList) { %>
										 <div class="carousel-item" id="projectinfo<%=obj[0]%>">
										 	<div class="row" >
												<div class="col-md-12 text-left" >
													<h6><%if(!obj[0].toString().equals("0")){%><%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - "%><%} %></h6>
													<%if(!obj[0].toString().equals("0")){ if(obj[1].toString().chars().count()>17){ %>
														<div class="fr-1"><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "%></div>
													<%}else{ %>
														<h6><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "%><br><br></h6>
													<%} %>
													<%} %>
													<h6><%if(!obj[12].toString().equals("0")){%><%= sdf.format(sdf1.parse( obj[12].toString()))%><%}else{ %><%} %></h6>
													<h6><%if(!obj[9].toString().equals("0")){%><%= sdf.format(sdf1.parse( obj[9].toString()))%><%}else{ %><%} %></h6>
													<h6><%if(obj[15]!=null){%><%= sdf.format(sdf1.parse( obj[15].toString()))%><%}else{ %>-<%} %></h6>
													<h6><%if(obj[16]!=null){%><%= sdf.format(sdf1.parse( obj[16].toString()))%><%}else{ %>-<%} %></h6>
												</div>
											</div>
										 </div>
										<%} %>
									</div>
								</div>
							</div>
					  </div> 
				 <%} %>
			</div>
					 
	 <!-- -----------------PROJECT DASHBOARD SELECTED PROJECTED DETAILS DISPLAY END---------------------------- -->	
			      </div>
<!-- @@@@@@@@@@ NESTED ROW SCHEDULE END( 2 ) @@@@@@@@@@  -->    		      


<!-- @@@@@@@@@@ NESTED ROW BUDGET START @@@@@@@@@@  --> 				      
			      
		 <div class="col-4 col-md-6">
			  
	 <!-- -----------------PROJECT DASHBOARD SELECTED FINANCIAL PERFORMANCE DISPLAY START---------------------------- -->	
	 	 
		<%if(error!=null){ %>
				<h4 class="hr-1" id="financialdataerror" ><%=StringEscapeUtils.escapeHtml4(error)%></h4>  
		 <%} %> 
		 <% if(!logintype.equalsIgnoreCase("U")){ %>
			 <%if(budgetlist!=null && budgetlist.size()>0){ %>	
				<div class="card-body kr-1"  align="center" id="financialdata"  > 
					<div id="carouselExampleControls" class=""  data-ride=""  >		
							<div class="carousel-inner">					
							<div class="bg-white">
									<%long i=0;
									for(ProjectSanctionDetailsMaster obj : budgetlist){
										%>
									<div class="carousel-item bot1"  id="chart<%=obj.getProjectid()%>"> 
										<nav class="navbar navbar-light bg1" >
											<a class="navbar-brand" >Financial Performance</a>
										    <form class="form-inline" target="_blank" method="post" id="ibasform" action="<%=ibasUri%>/loginFromPfms">
										    <input type="hidden" name="empNo" value="<%=EmpNo%>">
											<input type="hidden" name="ProjectId" value="<%=obj.getProjectid()%>">
											<input type="hidden" name="ProjectCode" value="<%=obj.getProjectCode()%>">
											<input type="hidden" name="Expenditure" value="<%=obj.getExpAmt()%>">
											<input type="hidden" name="Commitment" value="<%=obj.getOsComAmt()%>">
											<input type="hidden" name="Dipl" value="<%=obj.getDipl()%>">
											<input type="hidden" name="BalAmt" value="<%=obj.getBalAmt()%>">
											<input type="hidden" name="asOndate" value="<%=obj.getAsOnDate()%>">
										    <button type="submit" class="btn btn-sm lop1"  ><img src="view/images/projecticon.png"/> &nbsp;Project Details</button>
										  </form>
										</nav>
										<div class="h-12" id="container<%=obj.getSno()%>" ></div>
											<div>
												<table>
													<tr>
														<td  class="p030" ><span class="f-12">SANC	</span></td>
											       		<td  class="p030"><span class="f-12">EXP</span></td>
											       		<td  class="p030"><span class="f-12">OS</span></td>
											       		<td  class="p030"><span class="f-12">DIPL</span></td>
											       		<td  class="p030"><span class="f-12">BAL</span></td>	       			
											       	</tr>
													<tr>
														<td align="center"><button type="button"  class="btn btn-sm bg-1"  ><%=obj.getSancAmt()!=null?StringEscapeUtils.escapeHtml4(obj.getSancAmt().toString()): " - " %> L </button></td>
														<td align="center"><button type="button"  class="btn btn-sm bg-2" ><%=obj.getExpAmt()!=null?StringEscapeUtils.escapeHtml4(obj.getExpAmt().toString()): " - " %> L</button></td>
													    <td align="center"><button type="button"  class="btn btn-sm bg-3" ><%=obj.getOsComAmt()!=null?StringEscapeUtils.escapeHtml4(obj.getOsComAmt().toString()): " - " %> L</button></td>
													    <td align="center"><button type="button"  class="btn btn-sm bg-4"><%=obj.getDipl()!=null?StringEscapeUtils.escapeHtml4(obj.getDipl().toString()): " - " %> L</button></td>
													    <td align="center"><button type="button"  class="btn btn-sm bg-5" ><%=obj.getBalAmt()!=null?StringEscapeUtils.escapeHtml4(obj.getBalAmt().toString()): " - " %> L</button></td>					
											       	</tr>
												</table>
											</div>	
										</div> 
									<% i++;
									} %>
							</div>
						</div>
				  </div>
			</div>
				<%for(ProjectSanctionDetailsMaster obj : budgetlist){ %>
					<script>
					anychart.onDocumentReady(function () {
					    // create data
					    var data = [
					      {x: "EXP ", value: <%=obj.getExpAmt()!=null?StringEscapeUtils.escapeHtml4(obj.getExpAmt().toString()): " - "%>, fill : "#ac0d0d"},
					      {x: "OS ", value: <%=obj.getOsComAmt()!=null?StringEscapeUtils.escapeHtml4(obj.getOsComAmt().toString()): " - "%> , fill : "#fb7813"},
					      {x: "DIPL ", value: <%=obj.getDipl()!=null?StringEscapeUtils.escapeHtml4(obj.getDipl().toString()): " - "%> , fill : "#0e49b5"},
					      {x: "BAL  ", value: <%=obj.getBalAmt()!=null?StringEscapeUtils.escapeHtml4(obj.getBalAmt().toString()): " - "%>, fill : "#06623b"},
					      
					    ];
					    // create a chart and set the data
					    var chart = anychart.pie3d(data);
					    var legend= chart.legend();
					    //legend.enabled(false);
					    //legend.position("right");
					    legend.positionMode("inside");
						// set position and alignement
						legend.position("center");
						legend.align("right");
						legend.itemsLayout("vertical");
					 	
					    var tooltip = chart.tooltip();
					    tooltip.enabled(true);
					    tooltip.fontColor('white');
					    tooltip.fontWeight(600);
					    tooltip.background('black');
					    tooltip.titleFormat('{%x}');
					    tooltip.format('Amount : {%value} Lakhs \n Percentage : {%yPercentOfTotal} %');
					    
					   /*  var credits = chart.credits();
					    credits.alt("VTS"); */
					 
					  /*   chart.labels().position("outside"); */
					 				    
					    chart.startAngle(90);
					    
					    // set the chart title

					    // set the container id
					    chart.container('container<%=obj.getSno()%>');

					    // initiate drawing the chart
					    chart.draw();
					    
					});				
					</script>
				<%} %>
				
			<%} %>	 
			
			<%} %>
			
		<!-- -----------------PROJECT DASHBOARD SELECTED FINANCIAL PERFORMANCE DISPLAY END---------------------------- -->	
		       
		       
		<!-- -----------------ACTION DASHBOARD TASKBAR STARTS---------------------------- -->	
		      
		       <div class="card bg-trans"  id="mytasks">
							<nav class="navbar navbar-light bg-primary bg1" >
								<a class="navbar-brand text-light"  >My Tasks</a><a class="text-light" href="FeedBack.htm" title="Feedback"><i class="fa fa-commenting" aria-hidden="true"></i></a>
							</nav>					
								<div id="" class="carousel slide carousel-interval p312" data-ride="carousel"  >
									<div class="carousel-inner">
										    	<table class="table meeting h70"   >													
													<tr>
														<td class="p-515"></td>
													    <td  class="p-515"><span class="f-12">All PDC</span></td>
													    <td  class="p-515"><span class="f-12">In Progress</span></td>
													    <td  class="p-515"><span class="f-12">Today PDC</span></td>
<!-- 													   
 -->													    <td  class="p-515"><span class="f-12">Review</span></td>
													</tr>
													<tr>
														<td  class="p505">Action</td>
														<%int actionCounts=0;
														for(Object[] obj : MyTaskList){
														  	if(obj[0].toString().equalsIgnoreCase("Actions")){ %>
														<td><button type="button" onclick="actionformtask('N','N')" class="btn btn-sm <%if(!obj[1].toString().equals("0")){ %> <%} %> bg1-3" ><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %></button></td>
														<td><button type="button" onclick="actionformtask('I','N')"  class="btn btn-sm bg1-4" ><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></button></td>
														<td><button type="button" <%if(!obj[3].toString().equals("0")){ %> onclick="document.location='AssigneeList.htm'"<%} %>  class="btn btn-sm  <%if(!obj[3].toString().equals("0")){ %> fa faa-pulse animated faa-fast <%} %> bg1-5" ><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - " %></button></td>
													    <td><button type="button" onclick="document.location='ActionForwardList.htm'"  class="btn btn-sm bg1-6" ><%=obj[5]!=null?StringEscapeUtils.escapeHtml4(obj[5].toString()): " - " %></button></td>
														<% actionCounts+=Integer.parseInt(obj[3].toString());}   } %>
													</tr>
													<tr>
														<td  class="p505">Meeting</td>
														<%for(Object[] obj : MyTaskList){
														  	if(obj[0].toString().equalsIgnoreCase("Meeting")){ %>
														<td><button type="button" onclick="actionformtask('N','S')"  class="btn btn-sm <%if(!obj[1].toString().equals("0")){ %>  <%} %> bg1-3" ><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %></button></td>   <!--changed 'E' to 'N'  -->   
														<td><button type="button" onclick="actionformtask('I','S')"  class="btn btn-sm bg1-4"><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></button></td>
														<td><button type="button" <%if(!obj[3].toString().equals("0")){ %> onclick="document.location='AssigneeList.htm'"<%} %> class="btn btn-sm bg1-5<%if(!obj[3].toString().equals("0")){ %> fa faa-pulse animated faa-fast <%} %> " ><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - " %></button></td>
														<td><button type="button" onclick="document.location='ActionForwardList.htm'" class="btn btn-sm bg1-6" ><%=obj[5]!=null?StringEscapeUtils.escapeHtml4(obj[5].toString()): " - " %></button></td>
														<% actionCounts+=Integer.parseInt(obj[3].toString());}  } %>
													<tr>
													<%if(!IsDG.equalsIgnoreCase("Yes")){ %>
													<tr>
														<td  class="p505" >Milestone</td>
														<%for(Object[] obj : MyTaskList){
														  	if(obj[0].toString().equalsIgnoreCase("Milestone")){ %>
														<td><button type="button" onclick="actionformtask('N','M')"  class="btn btn-sm <%if(!obj[1].toString().equals("0")){ %>  <%} %> bg1-3" ><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %></button></td>
														<td><button type="button" onclick="actionformtask('I','M')"  class="btn btn-sm bg1-4" ><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></button></td>
														<td><button type="button" <%if(!obj[3].toString().equals("0")){ %> onclick="document.location='AssigneeList.htm'"<%} %>  class="btn btn-sm bg1-5<%if(!obj[3].toString().equals("0")){ %> fa faa-pulse animated faa-fast <%} %> " ><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - " %></button></td>
<%-- 														
 --%>														<td><button type="button" onclick="document.location='ActionForwardList.htm'" class="btn btn-sm bg1-6" ><%=obj[5]!=null?StringEscapeUtils.escapeHtml4(obj[5].toString()): " - " %></button></td>
														<% actionCounts+=Integer.parseInt(obj[3].toString());} } %>
													</tr>
													<tr>
														<td  class="p505">Fracas</td>
														<%for(Object[] obj : MyTaskList){
														  	if(obj[0].toString().equalsIgnoreCase("Fracas")){ %>
														<td><button type="button" onclick="document.location='FracasAssigneeList.htm'" class="btn btn-sm <%if(!obj[1].toString().equals("0")){ %>  <%} %> bg1-3" ><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %></button></td>
														<td><button type="button" onclick="document.location='FracasAssigneeList.htm'" class="btn btn-sm bg1-4" ><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></button></td>
														<td><button type="button" onclick="document.location='FracasAssigneeList.htm'" class="btn btn-sm bg1-5<%if(!obj[3].toString().equals("0")){ %> fa faa-pulse animated faa-fast <%} %> " ><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - " %></button></td>
<%-- 														
 --%>														<td><button type="button" onclick="document.location='FracasToReviewList.htm'" class="btn btn-sm bg1-6" s><%=obj[5]!=null?StringEscapeUtils.escapeHtml4(obj[5].toString()): " - " %></button></td>
														<% actionCounts+=Integer.parseInt(obj[3].toString());} } %>
													</tr>
													<%} %>
												 </table>
										  </div>
									</div> 
							</div> 
		<!-- -----------------ACTION DASHBOARD TASKBAR ENDS---------------------------- -->	
			
		<!-- -----------------OVERALL DASHBOARD HEADING STARTS---------------------------- -->							
						<div class="row" >
							<div class="col-md-6">
								<div class="overallheader ohD1">
									<h4  id="projecttitle" class="health-title cl-2"> PROJECT HEALTH</h4>
									<hr class="m-39">
								</div>
							</div>
							<div class="col-md-2">
								
							</div>
						</div>
	 <!-- -----------------OVERALL DASHBOARD HEADING ENDS---------------------------- -->	
			</div>
<!-- @@@@@@@@@@ NESTED ROW BUDGET START @@@@@@@@@@  --> 	
    <!-- -----------------PROJECT DASHBOARD GANTT CHART STARTS---------------------------- -->	
	          <div class="col-md-12">
			    	
			      	<%if(ProjectList!=null){ %>
						<div class="d2u" id="ganttchart" >
						<div class="card"  >
				
							<div id="carouselExampleControls2" class="" data-ride=""  >
								<div class="carousel-inner">
								 <%if(ProjectList!=null) {for(Object[] obj1 : ProjectList){ 
								 if(!"0".equalsIgnoreCase(obj1[0].toString())){
								 %>
			
								    <div class="carousel-item of-auto"  id="Mil<%=obj1[0]%>">
									    <nav class="navbar navbar-light bg-primary bg1">
												<form class="form-inline" method="post" action="GanttChart.htm" id="myform" >
													<input type="hidden" name="ProjectId"  id="ProjectId" value="<%=obj1[0] %>" /> 
								 					<input  class="btn btn-primary gantt navbar-brand text-white bgc-1" id="gantt" type="submit" name="sub" value=" &nbsp;&nbsp;&nbsp; Gantt Chart"  > 
								 					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
								 				</form>
								 				
												<%if(!obj1[0].toString().equalsIgnoreCase("0")){ %>
														<span class="fwc"><%=obj1[14]!=null?StringEscapeUtils.escapeHtml4(obj1[14].toString()): " - " %> (<%=obj1[11]!=null?StringEscapeUtils.escapeHtml4(obj1[11].toString()): " - " %>)</span>
												<%} %>
								 				<form method="post" action="ProjectBriefingPaper.htm">
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
													<button  class="btn btn-primary navbar-brand text-white bgc-1" ><img src="view/images/camera.png"/> Project Snapshot </button>
													<input type="hidden" name="projectid" value="<%=obj1[0] %>" />
												</form>
										</nav> 
						
					   				<div class="flex-container h135" id="containers<%=obj1[0] %>"  ></div>
									</div>
			
								 <%} } } %>
			
									  </div>
								</div> 
			
							</div>
						</div>	
							
					<%if(ProjectList!=null){
						for(Object[] obj1 : ProjectList){%> 
					
									<script>
							anychart.onDocumentReady(function () {
								    	  var data = [
								    		  <%
								    		  for(Object[] obj : ganttchartlist){
								    			  if(obj1[0].toString().equalsIgnoreCase(obj[1].toString())){
								    			  %>	
								    		  {
								    			  id: "<%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): ""%>",
									    		    name: "<%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): ""%>",
									    		    <%if(!obj[9].toString().equalsIgnoreCase("0") && !obj[9].toString().equalsIgnoreCase("1")){ %>
									    		   	baselineStart: "<%=obj[6]!=null?StringEscapeUtils.escapeHtml4(obj[6].toString()): ""%>",
									    		    baselineEnd: "<%=obj[7]!=null?StringEscapeUtils.escapeHtml4(obj[7].toString()): ""%>", 
									    		    baseline: {fill: "#f25287 0.5", stroke: "0.0 #f25287"},
									    		    actualStart: "<%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): ""%>",
									    		    actualEnd: "<%=obj[5]!=null?StringEscapeUtils.escapeHtml4(obj[5].toString()): ""%>",
									    		    actual: {fill: "#29465B", stroke: "0.8 #29465B"},
									    		    baselineProgressValue: "<%= Math.round((int)obj[8])%>%",
									    		    progress: {fill: "#FF7F3E 0.0", stroke: "0.0 #FF7F3E"},
									    		    progressValue: "<%= Math.round((int)obj[8])%>% ", 
									    		    <%} else{%>
								    		   		<%-- baselineStart: "<%=obj[6]%>",
									    		    baselineEnd: "<%=obj[7]%>",  --%>
									    		    baselineStart: "<%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): ""%>",
									    		    baselineEnd: "<%=obj[5]!=null?StringEscapeUtils.escapeHtml4(obj[5].toString()): ""%>", 
									    		    baseline: {fill: "#29465B", stroke: "0.8 #29465B"},
									    		    baselineProgressValue: "<%= Math.round((int)obj[8])%>%",
									    		    progress: {fill: "#81b214 0.0", stroke: "0.0 #150e56"},
									    		    progressValue: "<%= Math.round((int)obj[8])%>% ", 
									    		    <%}%>
									    		    rowHeight: "55",
								    		  },
								    		  
								    		  <%}}%>
								    	
								    		  ];
								    		    
								    		 
								    	// create a data tree
								    		var treeData = anychart.data.tree(data, "as-tree");
								    		// create a chart
								    		var chart = anychart.ganttProject();
								    		// set the data
								    		chart.data(treeData);   
								        	// set the container id
								        	chart.container("containers<%=obj1[0]%>");  
								        	// initiate drawing the chart
								        	chart.draw();    
								        	 // fit elements to the width of the timeline
								        	chart.fitAll(); 
									        var timeline = chart.getTimeline();
										   // configure labels of elements
										   timeline.elements().labels().fontWeight(600);
										   timeline.elements().labels().fontSize("10px");
										   timeline.elements().labels().fontColor("#FF6F00");
								        	 /* ToolTip */
								        	chart.getTimeline().tooltip().useHtml(true);    
								        	chart.getTimeline().tooltip().format(
									        		 function() {
									        		        var actualStart = this.getData("actualStart") ? this.getData("actualStart") : this.getData("baselineStart");
									        		        var actualEnd = this.getData("actualEnd") ? this.getData("actualEnd") : this.getData("baselineEnd");
									        		        var reDate=this.getData("actualStart") ;
									        		        var html="";
									        		        if(reDate===undefined){
									        		        	html="";
									        		        	html= "<span class='span-c1'> Actual : " +
									        		               anychart.format.dateTime(actualStart, 'dd MMM yyyy') + " - " +
									        		               anychart.format.dateTime(actualEnd, 'dd MMM yyyy') + "</span><br>" +
									        		               "Progress: " + this.getData("baselineProgressValue") + "<br>"
									        		        }else{
									        		        	html="";
									        		        html="<span class='span-c1'> Actual : " +
									        		               anychart.format.dateTime(actualStart, 'dd MMM yyyy') + " - " +
									        		               anychart.format.dateTime(actualEnd, 'dd MMM yyyy') + "</span><br>" +
									        		               "<span class='span-c1'> Revised : " +
									        		               anychart.format.dateTime(this.getData("baselineStart"), 'dd MMM yyyy') + " - " +
									        		               anychart.format.dateTime(this.getData("baselineEnd"), 'dd MMM yyyy') + "</span><br>" +
									        		               "Progress: " + this.getData("baselineProgressValue") + "<br>"
									        		        }
									        		        return html;
									        		    }
										        ); 
								        /* Hover */
								        
								        chart.rowHoverFill("#8fd6e1 0.3");
								        chart.rowSelectedFill("#8fd6e1 0.3");
								        chart.rowStroke("0.5 #64b5f6");
								        chart.columnStroke("0.5 #64b5f6");
								        
								        chart.defaultRowHeight(35);
								     	chart.headerHeight(90);
								     	
								     	/* Hiding the middle column */
								     	chart.splitterPosition("17.4%");
								     	
								     	var dataGrid = chart.dataGrid();
								     	dataGrid.rowEvenFill("gray 0.3");
								     	dataGrid.rowOddFill("gray 0.1");
								     	dataGrid.rowHoverFill("#ffd54f 0.3");
								     	dataGrid.rowSelectedFill("#ffd54f 0.3");
								     	dataGrid.columnStroke("2 #64b5f6");
								     	dataGrid.headerFill("#64b5f6 0.2");
								     	
								     
								     	/* Title */
								     	var column_1 = chart.dataGrid().column(0);
								     	column_1.title().enabled(false);
								     	
								     	var column_2 = chart.dataGrid().column(1);
								     	column_2.title().text("Activity");
								     	column_2.title().fontColor("#145374");
								     	column_2.title().fontWeight(600);
								     	
								     	chart.dataGrid().column(0).width(20);

								     	var column_1 = chart.dataGrid().column(1);
								     	column_1.labels().fontWeight(600);
								     	column_1.labels().useHtml(true);
								     	column_1.labels().fontColor("#055C9D");

									    chart.getTimeline().scale().zoomLevels([["quarter", "semester","year"]]);
	
									    chart.dataGrid().tooltip().useHtml(true);    	
									    
								     	/* Header */
								     	var header = chart.getTimeline().header();
								     	header.level(0).fill("#64b5f6 0.2");
								     	header.level(0).stroke("#64b5f6");
								     	header.level(0).fontColor("#145374");
								     	header.level(0).fontWeight(600);
								     	
								     	header.level(1).format(function() {
							     			
							     			var duration = '';
							     		
							     			if(this.value=='Q1')
							     				duration='H1';
							     			if(this.value=='Q3')
							     				duration='H2'
		
							     		  return duration;
							     		});
								     	
								     	
								     	/* Marker */
								     	var marker_1 = chart.getTimeline().lineMarker(0);
								     	marker_1.value("current");
								     	marker_1.stroke("2 #dd2c00");
								     	
								     	/* Progress */
											var timeline = chart.getTimeline();
								     	timeline.tasks().labels().useHtml(true);
								     	timeline.tasks().labels().format(function() {
								     	  if (this.progress == 1) {
								     	    return "<span class='span-c2'><Completed</span>";
								     	  } else {
								     	    return "<span class='span-c3'></span>";
								     	  }
								     	});

								       
								      } );    
	
								    </script>	
						<% } } %>

					<%} %> 
		
				</div>
     <!-- -----------------PROJECT DASHBOARD GANTT CHART ENDS---------------------------- -->	
			
    <!-- -----------------ACTION DASHBOARD UPCOMING SCHEDULE STARTS---------------------------- -->	
    
          <div class="col-md-4">
		       <br>	
		       <div class="card div-c3"  id="upcomingschedules">
							<nav class="navbar navbar-light bg-primary bg1" >
								<a class="navbar-brand text-light"  >Upcoming Schedule Details</a>
							</nav>					
								<div class="j-1">
									<table class="table meeting " >	
										<thead>												
											<tr>
												<td ><span class="j-2"></span></td>
												<td ><span class="j-2">Date</span></td>
												<td ><span class="j-2">Time</span></td>
												<td ><span class="j-2">Committee</span></td>
											</tr>
										</thead>
										<%
										int newsize=0;
										if(todayschedulelist.size()!=0){
											int count1=1;
											for(Object[] obj : todayschedulelist) {
												if(!obj[3].toString().equalsIgnoreCase(TodayDate)){	%>
										<tbody>
											<tr>
												<td><a href="javascript:void(0)" onclick="location.href='CommitteeScheduleView.htm?scheduleid=<%=obj[2] %>' " ><i class="fa fa-hand-o-right j-3" aria-hidden="true" ></i></a></td>
												<td><%=obj[3]!=null?sdf.format(obj[3]):" - " %></td>
												<td><%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - " %></td>
												<td><%=obj[7]!=null?StringEscapeUtils.escapeHtml4(obj[7].toString()): " - " %></td>
											</tr>
											<%count1++;newsize++;}}} %>
											<%if(newsize==0){%>
												<tr>
												<td colspan="5">
													<br>
														<h6 align="center">No Upcoming Schedules</h6>
													<br>
												</td>	
												<tr>
											<%} %>
										</tbody>
									</table>				
								</div> 
							</div> 
					</div>		
            <!-- -----------------ACTION DASHBOARD UPCOMING SCHEDULE ENDS---------------------------- -->				
					
		
					
		<div class="col-md-8">
		       <br>	
		       	<!-- -----------------ACTION DASHBOARD MY TASK DETAILS STARTS---------------------------- -->	
		       	
		       		 <div class="card j-4"  id="mytaskdetails">
							<nav class="navbar navbar-light bg-primary bg1" >
								<a class="navbar-brand text-light"   >My Task Details</a>
							</nav>					
								<div class="j-1">
									<table class="table meeting " >	
										<thead>												
											<tr>
												<td ><span class="j-2" ></span></td>
												<td ><span class="j-2">Action Item</span></td>
												<td ><span class="j-2">PDC </span></td>
												<td ><span class="j-2">Assigned By</span></td>
											</tr>
										</thead>
										<%
										int newsizefortask=0;
										if(mytaskdetails.size()!=0){
											int count1=1;
											for(Object[] obj : mytaskdetails) {
												if(obj[11].toString().equalsIgnoreCase("A")){
												
												%>
										<tbody>
											<tr>
												<td><a href="javascript:MyTaskDetails(<%=obj[0]%>)"> <i class="fa fa-hand-o-right j-3" aria-hidden="true" ></i></a></td>
												<td class="clx-51"><%=obj[2]!=null?obj[2].toString(): " - " %></td>
												<td class="width-100x"><%=obj[4]!=null?sdf.format(obj[4]):" - " %></td>
												<td><%=obj[12]!=null?StringEscapeUtils.escapeHtml4(obj[12].toString()): " - " %>
													<form name="MyTaskDetails<%=obj[0]%>" id="MyTaskDetails<%=obj[0]%>" action="<%=obj[14] %>" method="POST" >
														<input type="hidden" name="Assigner" value="<%=obj[12]%>,<%=obj[13]%>"/>													
		                                                <input type="hidden" name="ActionLinkId" value="<%=obj[15]%>"/>
														<input type="hidden" name="ActionNo" value="<%=obj[1]%>"/>
		 												<input type="hidden" name="ActionMainId" value="<%=obj[0]%>"/>
		 												<input type="hidden" name="fracasassignid" value="<%=obj[0]%>"/>
		 												<input type="hidden" name="ActionAssignid" value="<%=obj[16]%>"/>
		 												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
													</form> 
												</td>
											</tr>
											<%count1++;newsizefortask++;}}} %>
											<%if(newsizefortask==0){%>
												<tr>
												<td colspan="5">
													<br>
														<h6 align="center">No Task Details</h6>
													<br>
												</td>	
												<tr>
											<%} %>
										</tbody>
									</table>	
								</div> 
							</div> 
							<!-- -----------------ACTION DASHBOARD MY TASK DETAILS ENDS---------------------------- -->	
							
							<!-- -----------------ACTION DASHBOARD Briefing Paper,Project Meeting,Annual Meeting STARTS---------------------------- -->	
							
							<%if(!IsDG.equalsIgnoreCase("Yes")){ %>
							<%if(QuickLinkList.size()>0){ %>
							<div class="multi-button q-1" id="quicklinks">
									  <span><span class="badge badge-success"><i class="fa fa-link" aria-hidden="true"></i></span>  Links : </span>
									<%for(Object[] obj : QuickLinkList){ %>
										<a class="button" href="<%=obj[1] %>" id="cut"><span><%=obj[0]!=null?StringEscapeUtils.escapeHtml4(obj[0].toString()): " - " %> &nbsp;<i class="fa fa-file-text" aria-hidden="true"></i></span></a>									  <%} %>
										</div>
									<%} %>
							<%} %>
						  <!-- -----------------ACTION DASHBOARD Briefing Paper,Project Meeting,Annual Meeting ENDS---------------------------- -->	
		
			</div>
		</div>
<!-- @@@@@@@@@@ NESTED ROW END @@@@@@@@@@  --> 				
		</div>  
<!-- @@@@@@@@ MAIN ROW col-md-9 END @@@@@@@ -->		
<!-- @@@@@@@@ MAIN ROW col-md-3 START @@@@@@@ -->		
		 <div class="col-md-3" >
		
		 <!-- ----------- COMMON TOGGLE BUTTONS(ACTION,PROJECT,OVERALL) STARTS --------------------------- --> 	
		   <div class="df-1 <%if(logintype.equalsIgnoreCase("U") ) { %> dis-none   <%}%> ">
		  	 <div class="btn-group "> 
		  	<form id="pmsToIbasForm" action="<%=pmsToStatsUrl%>" target="blank" method="get"> 
             <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
             <input type="hidden" name="username" value="<%=Username%>">
             <input type="hidden" name="action" value="loginFromPms">
             <!--  <input type="hidden" name="redirectVal" value="">  -->
<!-- 		  <button type="submit" class="btn External" data-toggle="tooltip" title="STATS" ><img src="view/images/stats.png" /></button>
 -->		  	</form>
		  	 	
		  	 	<form action="ProjectHealthUpdate.htm" method="get" class="<%if (IsDG.equalsIgnoreCase("Yes") ){%> dis-none   <%}%>" >
		        	<button type="submit" class="btn btn4" data-toggle="tooltip" title="Refresh" ><i class="fa fa-refresh f-21"  aria-hidden="true"></i></button>
		        </form>
		        <button class="btn btn1">Action</button>
		        <button class="btn btn2 <% if(Arrays.asList(LoginTypes).contains((String)request.getAttribute("logintype"))){ %>bd-1<%}%> <%if (IsDG.equalsIgnoreCase("Yes") ){%>dis-none<%} %>" >Project</button>
		        <button class="btn <%if (IsDG.equalsIgnoreCase("Yes") ){%>btn5<%} else {%>btn3<%} %> <% if(!Arrays.asList(LoginTypes).contains((String)request.getAttribute("logintype"))){ %> dis-none  <%}%>"  >Overall</button>
		      </div>
		  </div>	
		 <!-- ----------- COMMON TOGGLE BUTTONS(ACTION,PROJECT,OVERALL) ENDS --------------------------- --> 	

		 <!-- ----------- ACTION DASHBOARD NOTICE MAIN ROW STARTS--------------------------- --> 	
		  		<div class="card notice col-12 m-4p"    >
					<div class="card-body d-12"   id="noticeboard" >
						<span class="side-stick"></span>
						<div class="side-stick-right max-10"   > 
							<table>
								<%if(noticeElib>0){%> 	
									<tr>
										<td class="p5x"> <a  data-toggle="modal" data-target="#exampleModal" data-whatever="@mdo" ><i class="fa fa-plus-square fa-lg clx"  aria-hidden="true"></i> </a></td>
									</tr>
									<tr>
										<td class="p5x"> <a  href="IndividualNoticeList.htm" ><i class="fa fa-file-text fa-lg clx-1"    aria-hidden="true">  </i></a> </td>
									</tr>
								<%} %>
								<%if(notice!=null&& notice.size()>1){%> 
									<tr>
									</tr>
							   <%} %>
							</table>
						</div>
						<div class="max-90" >
 							<blink>	<h5 class="clx-2">Notice</h5></blink>
							<div id="carouselExampleControls6" class="carousel vert slide" data-ride="carousel" data-interval="5000">
								    <div class="carousel-inner">
								        	<% if(notice.size()>0){
										  for(Object[] obj : notice){
											  	%>
								            <div class="carousel-item height3" id="notice" >
											    <p class="clx-3" align = "center" ><%if(notice!=null && notice.size()>0){ %> <%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %> <%} %> </p>
												<p class="clx-4" align="right" > <%if(notice!=null && notice.size()>0){ %>-&nbsp; <%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - "%> <%} %> </p> 
								            </div>
											<%} }else{%>
										 	<p class="clx-5" align="right" > No Notice. </p> 
										 <%} %> 
								      </div>
							   </div> 
						</div>
					</div>
				</div>
				
	 <!-- ----------- ACTION DASHBOARD NOTICE MAIN ROW ENDS--------------------------- --> 	
							
							
    <!-- @@@@@@@@@@@@@@@@ DIV CARD BOX STARTS @@@@@@@@@@@@@@@@@@@@@@@ -->
            <%if(!logintype.equalsIgnoreCase("U") ) { %>
				<div class="card box clx-6"  id="mainactivitystatus" >
						<div class="card-header clx-7"  id="activitystatusheader">
								    Activity Status 
						</div>
						<div class="card-body" >
					 <!-- ----------- PROJECT DASHBOARD ACTIVITY STATUS STARTS------------- -->

							<!-- Uncomment the above line to add carousel -->	
							<div id="carouselExampleControls3 " class="p312" data-ride=""  >
									<div class="carousel-inner dis-none"  id="activitystatus">
											<%if(actionscount!=null){ %>	
										    	<%if(ProjectList!=null){ %>
									   				 <%for(Object[] obj : ProjectList){ %>
										<div class="carousel-item "  id="act<%=obj[0]%>">
										    	<table class="table h70"   >
										<tr>
											<td class="p-515"></td>
											<td class="p-515"><span
												class="clx-8">P</span></td>
											<td class="p-515"><span
												class="clx-8">F</span></td>
											<td class="p-515"><span
												class="clx-8">C</span></td>
											<td class="p-515"><span
												class="clx-8">D</span></td>
										</tr>
										<%if (actionscount != null) {%>	
											  <%for(Object[] obj2 : actionscount){ %>
												<%if(obj[0].toString().equalsIgnoreCase(obj2[5].toString())) { %>
									                <tr>
									                  <%if(obj2[0].toString().equalsIgnoreCase("Actions")){ %>
														<td  class="p505" >Action Items</td>
													   	<td><button type="button" onclick="submitForm('P','NA','<%=obj[0] %>');" class="btn btn-sm bg1-4" ><%=obj2[1]!=null?StringEscapeUtils.escapeHtml4(obj2[1].toString()): " - " %> </button></td>
											            <td><button type="button" onclick="submitForm('F','NA','<%=obj[0] %>');" class="btn btn-sm bg1-5" ><%=obj2[2]!=null?StringEscapeUtils.escapeHtml4(obj2[2].toString()): " - " %> </button></td>
											            <td><button type="button" onclick="submitForm('C','NA','<%=obj[0] %>');" class="btn btn-sm bg1-7" ><%=obj2[3]!=null?StringEscapeUtils.escapeHtml4(obj2[3].toString()): " - " %> </button></td>
											            <td><button type="button" onclick="submitForm('D','NA','<%=obj[0] %>');" class="btn btn-sm bg1-8" ><%=obj2[4]!=null?StringEscapeUtils.escapeHtml4(obj2[4].toString()): " - " %> </button></td>					
									       			  <% }%>
									       			</tr>
									       			 <tr>
									       			   <%if(obj2[0].toString().equalsIgnoreCase("Milestone")){ %>
														<td  class="p505">Milestones</td>
													   	<td><button type="button" onclick="submitForm('P','MLA','<%=obj[0] %>');" class="btn btn-sm bg1-4" ><%=obj2[1]!=null?StringEscapeUtils.escapeHtml4(obj2[1].toString()): " - "  %></button></td>
											            <td><button type="button" onclick="submitForm('F','MLA','<%=obj[0] %>');" class="btn btn-sm bg1-5" ><%=obj2[2]!=null?StringEscapeUtils.escapeHtml4(obj2[2].toString()): " - "  %></button></td>
											            <td><button type="button" onclick="submitForm('C','MLA','<%=obj[0] %>');" class="btn btn-sm bg1-7" ><%=obj2[3]!=null?StringEscapeUtils.escapeHtml4(obj2[3].toString()): " - "  %></button></td>
											            <td><button type="button" onclick="submitForm('D','MLA','<%=obj[0] %>');" class="btn btn-sm bg1-8" ><%=obj2[4]!=null?StringEscapeUtils.escapeHtml4(obj2[4].toString()): " - "  %></button></td>					
									       			   <% }%>
									       			</tr>
									       			<tr>
									       			    <%if(obj2[0].toString().equalsIgnoreCase("Meeting")){ %>
														<td  class="p505">Meetings</td>
													   	<td><button type="button" onclick="submitForm('P','MA','<%=obj[0] %>');" class="btn btn-sm bg1-4" ><%=obj2[1]!=null?StringEscapeUtils.escapeHtml4(obj2[1].toString()): " - "  %></button></td>
											            <td><button type="button" onclick="submitForm('F','MA','<%=obj[0] %>');" class="btn btn-sm bg1-5" ><%=obj2[2]!=null?StringEscapeUtils.escapeHtml4(obj2[2].toString()): " - "  %></button></td>
											            <td><button type="button" onclick="submitForm('C','MA','<%=obj[0] %>');" class="btn btn-sm bg1-7" ><%=obj2[3]!=null?StringEscapeUtils.escapeHtml4(obj2[3].toString()): " - "  %></button></td>
											            <td><button type="button" onclick="submitForm('D','MA','<%=obj[0] %>');" class="btn btn-sm bg1-8" ><%=obj2[4]!=null?StringEscapeUtils.escapeHtml4(obj2[4].toString()): " - "  %></button></td>					
									       			    <% }%>
									       			</tr>
									       			<tr>
									       			    <%if(obj2[0].toString().equalsIgnoreCase("Risk")){ %>
														<td  class="p505">Risks</td>
													   	<td><button type="button" onclick="submitForm('P','RK','<%=obj[0] %>');" class="btn btn-sm bg1-4" ><%=obj2[1]!=null?StringEscapeUtils.escapeHtml4(obj2[1].toString()): " - "  %></button></td>
											            <td><button type="button" onclick="submitForm('F','RK','<%=obj[0] %>');" class="btn btn-sm bg1-5"  "><%=obj2[2]!=null?StringEscapeUtils.escapeHtml4(obj2[2].toString()): " - "  %></button></td>
											            <td><button type="button" onclick="submitForm('C','RK','<%=obj[0] %>');" class="btn btn-sm bg1-7"  "><%=obj2[3]!=null?StringEscapeUtils.escapeHtml4(obj2[3].toString()): " - "  %></button></td>
											            <td><button type="button" onclick="submitForm('D','RK','<%=obj[0] %>');" class="btn btn-sm bg1-8"  "><%=obj2[4]!=null?StringEscapeUtils.escapeHtml4(obj2[4].toString()): " - "  %></button></td>					
									       			    <% }%>
									       			</tr>
									       			<tr>
									       			    <%if(obj2[0].toString().equalsIgnoreCase("Issue")){ %>
														<td  class="p505">Issues</td>
													   	<td><button type="button" onclick="submitForm('P','IU','<%=obj[0] %>');" class="btn btn-sm bg1-4"><%=obj2[1]!=null?StringEscapeUtils.escapeHtml4(obj2[1].toString()): " - "  %></button></td>
											            <td><button type="button" onclick="submitForm('F','IU','<%=obj[0] %>');" class="btn btn-sm bg1-5"><%=obj2[2]!=null?StringEscapeUtils.escapeHtml4(obj2[2].toString()): " - "  %></button></td>
											            <td><button type="button" onclick="submitForm('C','IU','<%=obj[0] %>');" class="btn btn-sm bg1-7"><%=obj2[3]!=null?StringEscapeUtils.escapeHtml4(obj2[3].toString()): " - "  %></button></td>
											            <td><button type="button" onclick="submitForm('D','IU','<%=obj[0] %>');" class="btn btn-sm bg1-8"><%=obj2[4]!=null?StringEscapeUtils.escapeHtml4(obj2[4].toString()): " - "  %></button></td>					
									       			    <% }%>
									       			</tr>
									       			<tr>
									       			    <%if(obj2[0].toString().equalsIgnoreCase("Recommendation")){ %>
														<td  class="p505">Recommendations</td>
													   	<td><button type="button" onclick="submitForm('P','RC','<%=obj[0] %>');" class="btn btn-sm bg1-4" ><%=obj2[1]!=null?StringEscapeUtils.escapeHtml4(obj2[1].toString()): " - "  %></button></td>
											            <td><button type="button" onclick="submitForm('F','RC','<%=obj[0] %>');" class="btn btn-sm bg1-5" ><%=obj2[2]!=null?StringEscapeUtils.escapeHtml4(obj2[2].toString()): " - "  %></button></td>
											            <td><button type="button" onclick="submitForm('C','RC','<%=obj[0] %>');" class="btn btn-sm bg1-7" ><%=obj2[3]!=null?StringEscapeUtils.escapeHtml4(obj2[3].toString()): " - "  %></button></td>
											            <td><button type="button" onclick="submitForm('D','RC','<%=obj[0] %>');" class="btn btn-sm bg1-8" ><%=obj2[4]!=null?StringEscapeUtils.escapeHtml4(obj2[4].toString()): " - "  %></button></td>					
									       			    <% }%>
									       			</tr> 
													<%}%>
												<%} %>
											<%} %>
										 </table>
								    </div>
								    <%}} %>
								    <%} else{ %>
								    	<div class="list-group-item clx-9" >
								    		No Activities
								    	</div>
								    <%} %>
								  </div>
								</div> 
							<%} %>	
		<!-- ----------- PROJECT DASHBOARD ACTIVITY STATUS ENDS------------- -->
								

	     <!-- ----------- ACTION DASHBOARD  REVIEW  BLOCK STARTS------------- -->
		   					<div class="card-header clx-10"  id="reviewheader">
								<span class="font12">Review - Pending with My Approver</span>
							</div>	
							<div class="clx-11" id="review">
									
									 <!-- <div align="center"> Action Items</div> -->		
													
													<!-- Actions Start -->
											<div class="<%if(logintype.equalsIgnoreCase("U")){ %>maxHeight9 <%}else{ %>maxHeight9<%} %>">
															<ul >	 
																<% int formcount=1;
																	if(dashboardactionpdc.size()>0){
																  for(Object[] obj : dashboardactionpdc){
																	  if(obj[15].toString().equalsIgnoreCase("A")){
																	  	%>
																  
																    <li class="list-group-item clx-12" >
																    	<form name="Action<%=formcount%>" id="Action<%=formcount%>" action="<%=obj[7] %>" method="POST" >
																    		<input type="hidden" name="Assigner" value="<%=obj[11]%>,<%=obj[13]%>"/>													
		                                                                    <input type="hidden" name="ActionLinkId" value="<%=obj[10]%>"/>
																			<input type="hidden" name="ActionMainId" value="<%=obj[9]%>"/>
																			<input type="hidden" name="ActionNo" value="<%=obj[0]%>"/>
																			<input type="hidden" name="Assignee" value="<%=obj[11] %>" />
																			<input type="hidden" name="fracasassignid" value="<%=obj[10]%>"/>
																			<input type="hidden" name="ActionAssignId" value="<%=obj[16]%>">
		 																	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
																    	</form>

																    	<a href="javascript:actionform(<%=formcount%>)" class="btn btn-sm clx-45 
																	    	<%if("A".equalsIgnoreCase(obj[3].toString())&&"1".equalsIgnoreCase(obj[8].toString())){ %> clx-46 <%} %>
																	    	<%if("I".equalsIgnoreCase(obj[3].toString()) || "F".equalsIgnoreCase(obj[3].toString()) &&"1".equalsIgnoreCase(obj[8].toString())){ %> clx-47 <%} %>
																	    	<%if("C".equalsIgnoreCase(obj[3].toString())&&"1".equalsIgnoreCase(obj[8].toString())){ %> clx-48 <%} %>
																	    	<%if("2".equalsIgnoreCase(obj[8].toString())){ %> clx-49 <%} %>
																    		clx-50"> 
																    	<!-- <i class="fa fa-arrow-right" aria-hidden="true" style="color: #1687a7;font-size: 1.00 rem !important"></i>  -->
																    		<%=obj[11] !=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - "%> <br> <%=obj[0]!=null?StringEscapeUtils.escapeHtml4(obj[0].toString()): " - " %> &nbsp;&nbsp;
																    	</a> 
														   			 </li>
																 <%formcount++;}} }else{%>
																 	<li class="list-group-item clx-13"  >No Review Pending !
																 	</li>
																 <%} %> 
													 	 </ul> 
													</div>
				 				</div>   
				 				
				 				<!--  Second Review Except User---------------- -->
				 		<%-- 	 <%if(!logintype.equalsIgnoreCase("U")){ %>	 --%>
				 				
				 			<br>	
				 			<div class="card-header clx-10"  id="reviewheader2">
							<span class="font12">Review - Pending with Me</span>
							</div>	
								<div class="clx-11" id="review2">
											<div class="maxHeight9">
															<ul >	 
																<% int formcount1=55;
																	if(dashboardactionpdc.size()>0){
																  for(Object[] obj : dashboardactionpdc){
																	  if(obj[15].toString().equalsIgnoreCase("G")){
																	  	%>
																  
																    <li class="list-group-item clx-12" >
																    	<form name="Action<%=formcount1%>" id="Action<%=formcount1%>" action="<%=obj[7] %>" method="POST" >
																    		<input type="hidden" name="Assigner" value="<%=obj[11]%>,<%=obj[13]%>"/>													
		                                                                    <input type="hidden" name="ActionLinkId" value="<%=obj[10]%>"/>
																			<input type="hidden" name="ActionMainId" value="<%=obj[9]%>"/>
																			<input type="hidden" name="ActionNo" value="<%=obj[0]%>"/>
																			<input type="hidden" name="Assignee" value="<%=obj[11] %>" />
																			<input type="hidden" name="fracasassignid" value="<%=obj[10]%>"/>
																			<input type="hidden" name="forceclose" value="N"/>
																			<input type="hidden" name="ActionAssignId" value="<%=obj[16]%>">
		 																	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
																    	</form>

																    	<a href="javascript:actionform(<%=formcount1%>)" class="btn btn-sm clx-45; 
																	    	<%if("A".equalsIgnoreCase(obj[3].toString())&&"1".equalsIgnoreCase(obj[8].toString())){ %> clx-46 <%} %>
																	    	<%if("I".equalsIgnoreCase(obj[3].toString()) || "F".equalsIgnoreCase(obj[3].toString()) &&"1".equalsIgnoreCase(obj[8].toString())){ %> clx-47 <%} %>
																	    	<%if("C".equalsIgnoreCase(obj[3].toString())&&"1".equalsIgnoreCase(obj[8].toString())){ %> clx-48 <%} %>
																	    	<%if("2".equalsIgnoreCase(obj[8].toString())){ %> clx-49 <%} %>
																    		clx-50 "> 
																    		<%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %> <br> <%=obj[0]!=null?StringEscapeUtils.escapeHtml4(obj[0].toString()): " - " %> &nbsp;&nbsp;
																    	</a> 
														   			 </li>
																 <%formcount1++;} }}else{%>
																 	<li class="list-group-item clx-13"  >No Review Pending !
																 	</li>
																 <% } %> 
													 	 </ul> 
													 	 
													</div>
													
										<!-- Actions Flow End -->
				 				</div> <br>  
				 			
				 		<%-- <%} %>	 --%><!-- Closing of condition for second review block --><!--------- Closing if loop of action items ------------->
				 				
			<!-- ----------- ACTION DASHBOARD  REVIEW  BLOCK ENDS------------- -->
					 </div>	
				</div>
	<!-- @@@@@@@@@@@@@@@@ DIV CARD BOX ENDS @@@@@@@@@@@@@@@@@@@@@@@ -->
				<!-- Removing Card 5 -->	
				<!-- ----------- PROJECT DASHBOARD  MEETINGS  BLOCK STARTS------------- -->
				 			
				 			<div class="card background: transparent;margin-top:1%;display: none"  id="meetingblock">
							<nav class="navbar navbar-light bg-primary bg1" >
								<a class="navbar-brand text-light"  >Meetings</a>
							</nav>					
								
								<!-- Uncomment the above line to add carousel -->
								<div id="carouselExampleControls1" class="p312" data-ride=""  >
									<div class="carousel-inner">
									
										<%if(ProjectList!=null){ %>
									    <%for(Object[] obj : ProjectList){
									    	%>
									    <div class="carousel-item <%if(obj[0].toString().equals("0")){ %> dis-none <%} %>" id="Meeting<%=obj[0]%>" >
										    	<table class="table meeting h70"  >													
													<tr>
														<td class="p-515"></td>
													    <td  class="p-515"><span class="f-12">Total</span></td>
													    <td  class="p-515"><span class="f-12">Held</span></td>
													    <td  class="p-515"><span class="f-12">Rem</span></td>
													</tr>
				
													<%if(ProjectMeetingCount!=null ){ %>	
													  <%for(Object[] obj2 : ProjectMeetingCount){ %>
														<%if(obj[0].toString().equalsIgnoreCase(obj2[9].toString())) { %>
														  <!-- (project status) ==> all = All, B = held and C = remaining -->
													<tr>
														<td  class="p505">EB</td>
														<td><button type="button" onclick="CommitteeForm('<%=obj2[9] %>',2,'all');" class="btn btn-sm bg1-5" ><%=obj2[0]!=null?StringEscapeUtils.escapeHtml4(obj2[0].toString()): " - "  %></button></td>
														<td><button type="button" onclick="CommitteeForm('<%=obj2[9] %>',2,'B');" 	class="btn btn-sm bg1-7" ><%=obj2[1]!=null?StringEscapeUtils.escapeHtml4(obj2[1].toString()): " - "  %></button></td>
														<td><button type="button" onclick="CommitteeForm('<%=obj2[9] %>',2,'C');" 	class="btn btn-sm bg1-4" ><%=obj2[2]!=null?StringEscapeUtils.escapeHtml4(obj2[2].toString()): " - "  %></button></td>
													</tr>
													<tr>
														<td class="p505">PMRC</td>
														<td><button type="button" onclick="CommitteeForm('<%=obj2[9] %>',1,'all');" class="btn btn-sm bg1-5" ><%=obj2[3]!=null?StringEscapeUtils.escapeHtml4(obj2[3].toString()): " - "  %></button></td>
														<td><button type="button" onclick="CommitteeForm('<%=obj2[9] %>',1,'B');" 	class="btn btn-sm bg1-7" ><%=obj2[4]!=null?StringEscapeUtils.escapeHtml4(obj2[4].toString()): " - "  %></button></td>
														<td><button type="button" onclick="CommitteeForm('<%=obj2[9] %>',1,'C');" 	class="btn btn-sm bg1-4" ><%=obj2[5]!=null?StringEscapeUtils.escapeHtml4(obj2[5].toString()): " - "  %></button></td>
													<tr>
													<tr>
														<td  class="p505">Others</td>
														<td><button type="button" onclick="CommitteeForm('<%=obj2[9] %>','others','all');" 	class="btn btn-sm bg1-5" ><%=obj2[6]!=null?StringEscapeUtils.escapeHtml4(obj2[6].toString()): " - "  %></button></td>
														<td><button type="button" onclick="CommitteeForm('<%=obj2[9] %>','others','B');" 	class="btn btn-sm bg1-7" ><%=obj2[7]!=null?StringEscapeUtils.escapeHtml4(obj2[7].toString()): " - "  %></button></td>
														<td><button type="button" onclick="CommitteeForm('<%=obj2[9] %>','others','C');" 	class="btn btn-sm bg1-4" ><%=obj2[8]!=null?StringEscapeUtils.escapeHtml4(obj2[8].toString()): " - "  %></button></td>
													</tr>
													
													 <%}%>
												   <%} %>
												<%} %>
												 </table>
										    </div>
										    <%} %>
										    <%} else{ %>
										    	<div class="list-group-item clx-15" >
										    		No Meetings
										    	</div>
										    <%} %>
										  </div>
									</div> 
							</div>
					<!-- ----------- PROJECT DASHBOARD  MEETINGS  BLOCK ENDS------------- -->
			</div>
<!-- @@@@@@@@ MAIN ROW col-md-3 END @@@@@@@ -->	
		</div>
<!-- @@@@@@@@ MAIN ROW END @@@@@@@ -->
</div>
<!-- @@@@@@@@ CONTAINER FLUID END @@@@@@@ -->

<!-- ------------------------------------------------------------- NOTICE MODAL (popup when clicked on + symbol of NOTICE IN ACTION DASHBOARD)   ------------------------------------------------------------------------------------ -->		
			
					<div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
					  <div class="modal-dialog">
					    <div class="modal-content">
					      <div class="modal-header ">
					        <h5 class="modal-title" id="exampleModalLabel">Add Notice</h5>
					        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
					          <span aria-hidden="true">&times;</span>
					        </button>
					      </div>
					      <div class="modal-body">
					      <form action="NoticeAddSubmit.htm" method="POST" name="myfrm" id="noticeForm">
					          	<div class="row">
					                            <div class="col-md-2"></div>
														<div class="col-md-4">
															<div class="form-group">
																<label class="control-label">From </label>
																 <input  class="form-control"  data-date-format="dd/mm/yyyy" readonly="readonly" id="fdate" name="fdate"  required="required">
															</div>
														</div>
					
														<div class="col-md-4">
															<div class="form-group">
																<label class="control-label">To </label> 
																<input  class="form-control"  data-date-format="dd/mm/yyyy" readonly="readonly" id="tdate" name="tdate"  required="required">
															</div>
														</div>
								</div>					 
					                                     <div class="col-md-12">
															<div class="form-group">
																<label class="control-label">Notice</label> 
															    <textarea class="form-control max-9" name="noticeFiled" id="noticeText" " maxlength="255"  placeholder="Enter Notice here with max 255 characters" required="required"></textarea>
															</div>
														</div>       
														<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" />
							</form>
					      </div>
					      <div class="modal-footer">
					       
					            <input type="submit" class="btn btn-primary btn-sm submit " formaction="NoticeAddSubmit.htm"
															id="sub" value="SUBMIT" name="sub" onclick="addNoticeForm()">
					        
					   
					      </div>
					    </div>
					  </div>
					</div>
					
<!-- ------------------------------------------------------------- NOTICE MODAL ENDS  ------------------------------------------------------------------------------------ -->		

<!-- ****************************************************************** OVERALL DASHBOARD MODULE STARTS********************************************************************************* -->

	<div class="container-fluid dis-none"  id="overalltable">
		
		<!-- ///////////////  CAR DECK (MEETING,MILESTONE,ACTION,RISK,FINANCE) STARTS //////////// -->
		<div class="card-deck clx-16"  id="overallmodulecarddeck" >
		<!-- jsp for DashBoard progress -->
		<%if(!session.getAttribute("LoginType").toString().equalsIgnoreCase("X")) {%><jsp:include page="DashBoard-circular.jsp" /><%} %>
		</div>
		
		<!-- ///////////////  CAR DECK (MEETING,MILESTONE,ACTION,RISK,FINANCE) ENDS //////////// -->
		
		<!-- ///////////////  CASH OUTGO(Cr) STARTS //////////// -->

		<jsp:include page="Dashboard-COG.jsp" />
		
		<!-- ///////////////  CASH OUTGO(Cr) ENDS //////////// -->
		
		
		<!-- /////////////// ALL PROJECT DETAILS IN OVERALL DASHBOARD STARTS //////////// -->

		<div class="card clx-17" >
		
								<div id="overalldiv" class="clx-18" >
								
									<table class="table meeting tableFixHead fixed-table clx-19" > 
										<thead class="clx-20">												
											<tr>
												<td class="width-4">
													<a  data-toggle="modal"  class="fa faa-pulse animated clx-21" data-target="#exampleModal1" data-whatever="@mdo"  ><i class="fa fa-info-circle f13re"  aria-hidden="true"></i> </a>
												</td>
												<td class="tableprojectnametd width-12" ><span class="fw-15">Project</span></td>
												<td class="width-2">
													<div data-toggle="tooltip" title="Master Slide">
														
															<button type="button" data-toggle="modal" data-target="" id="slideDIv" class="bg-transparent">
																<img src="view/images/silde.png" class="width25"/>
															</button>
													
													</div>
												</td>
												<!-- Button trigger modal -->
												<!-- Modal -->
												<jsp:include page="../print/ProjectsSlideShowSelection.jsp"></jsp:include>
												<td class="width-6"><span class="fw-15">DoS</span></td>
												<td class="width-6"><span class="fw-15">PDC</span></td>
												
												<td ><span class="fw-15">EB / PMB </span></td>
												<td ><span class="fw-15">PMRC / PJB </span></td>
												<td ><span class="fw-15">Milestone </span></td>
												<td ><span class="fw-15">Action</span></td>
												<td ><span class="fw-15">Risk</span></td>
												<td ><span class="fw-15">Finance</span></td>
											</tr>
										</thead>
										
										<tbody>
										
											<%
											
											for(Object[] obj : ProjectHealthData){
												
												if(ProjectList!=null){  for(Object[] obj2 : ProjectList) 
												{
													if(obj[2].equals(obj2[0]))
													{
											%>
										
											<tr>
												<td><a href="javascript:ProjectDetails('<%=obj[2]%>','<%=obj[46]%>')"> <i class="fa fa-hand-o-right clx-22" aria-hidden="true" ></i></a></td>
												<td class="c-7
														<% if(obj[45]!=null) {if(obj[45].toString().equalsIgnoreCase("IA")){%>c-6<%} 
														else if(obj[45].toString().equalsIgnoreCase("IN")){%>c-1 <%} 
														else if(obj[45].toString().equalsIgnoreCase("IAF")){%>c-2 <%} 
														else if(obj[45].toString().equalsIgnoreCase("IH")){%>c-3 <%} 
														else if(obj[45].toString().equalsIgnoreCase("OH")){%>c-4 <%} 
														else if(obj[45].toString().equalsIgnoreCase("DRDO")){%>c-5 <%} 
														else {%>text-dark <%} }else{ %>text-dark <%}%>">
												
												<div data-toggle="tooltip" 
													title="<%if(obj[45]!=null) { if(obj[45].toString().equalsIgnoreCase("IA")){%>Indian Army<%}
													else if(obj[45].toString().equalsIgnoreCase("IN")){%>Indian Navy <%} 
													else if(obj[45].toString().equalsIgnoreCase("IAF")){%>Indian Air Force<%} 
													else if(obj[45].toString().equalsIgnoreCase("IH")){%>Home Land Security <%} 
													else if(obj[45].toString().equalsIgnoreCase("DRDO")){%>DRDO <%} 
													else if(obj[45].toString().equalsIgnoreCase("OH")){%>Others <%} 
													else {%> -  <%} }else{ %>-<%}%>">
												
														  &#11044;&nbsp; <span class="tableprojectname clx-23" > 
														  	<%if(obj[46]!=null){%><%=StringEscapeUtils.escapeHtml4(obj[46].toString())%><%}else {%>-<%} %> /
														  	<%if(obj[3]!=null){%><%=StringEscapeUtils.escapeHtml4(obj[3].toString()) %><%}else {%>-<%} %> /
														  	<%if(obj[44]!=null){%><%=StringEscapeUtils.escapeHtml4(obj[44].toString()) %><%}else {%>-<%} %>
														  	</span> 	
												
													</div>
												
												</td>
												<td>
												<form action="#">
												<button class="bg-transparent cu-p"    type="submit" name="projectid" value="<%=obj[2]%>" formaction="PfmsProjectSlides.htm" formmethod="get" formtarget="blank" data-toggle="tooltip"  data-original-title="Project Slide"> <img src="view/images/silde.png" class="width25"/></button>
												</form>
											
												</td>
												<td class="clx-24"><%if(obj[50]!=null){%><%= sdf3.format(sdf2.parse(obj[50].toString()))%><%}else{ %> - <%} %></td>
												<td class="clx-24 <%if(obj[47]!=null){ if(LocalDate.parse(obj[47].toString()).isBefore(LocalDate.now())){ %>c-8<%}}%>  "><%if(obj[47]!=null){%><%= sdf3.format(sdf2.parse(obj[47].toString()))%><%}else{ %> - <%} %></td>
												
												<td class="custom-td">
												<%if (obj[52].toString().equalsIgnoreCase("N")) {%>
													<%if(Integer.parseInt(obj[13].toString())>0){ %>
														<div class="row">
															<div class="col-md-11">
																<div class="progress" data-toggle="tooltip" title="EB Held : <%=obj[9]%> <br> EB To Be Held : <%=obj[13] %><br> Total EB To Be Held : <%=obj[49] %>">
																  <div class="progress-bar progress-bar-striped bg-success width-<%=obj[10]%>" onclick="overallmeetingredirect('<%=obj[2]%>','2', 'B')" ></div>
																  <div class="progress-bar progress-bar-striped bg-primary width-<%=obj[12]%>" onclick="overallmeetingredirect('<%=obj[2]%>','2', 'C')" ></div>
																</div>
															</div>
															<div class="col-md-1 pl-0" >
																<span class="health-circle <%if(Integer.parseInt(obj[10].toString())<=25){%> bg-p1 <%}%>
																								   <%if( (Integer.parseInt(obj[10].toString())>25) && (Integer.parseInt(obj[10].toString())<=50)){%> bg-p2 <%}%>
																								   <%if( (Integer.parseInt(obj[10].toString())>50) && (Integer.parseInt(obj[10].toString())<=75)){%> bg-p3 <%}%>
																								   <%if( (Integer.parseInt(obj[10].toString())>75)){%> bg-p4<%}%>
																"><%=obj[10]!=null?StringEscapeUtils.escapeHtml4(obj[10].toString()): " - " %></span>
															</div>
														</div>
													<%}else{ %>
													<div class="progress nil-bar  noProgressDiv" >
														<div class="progress-bar noProgress" role="progressbar"   >
															Nil
														</div>
													</div>
													<%} %>
													<%}else{ %>
														<%if(Integer.parseInt(obj[13].toString())>0){ %>
														<div class="row">
															<div class="col-md-11">
																<div class="progress" data-toggle="tooltip" title="PMB Held : <%=obj[59]%> <br> PMB To Be Held : <%=obj[63] %><br> Total PMB To Be Held : <%=obj[64] %>">
																  <div class="progress-bar progress-bar-striped bg-success  width-<%=obj[60]%>"  ></div>
																  <div class="progress-bar progress-bar-striped bg-primary width-<%=obj[62]%>"  ></div>
																</div>
															</div>
															<div class="col-md-1 pl-0">
																<span class="health-circle <%if(Integer.parseInt(obj[60].toString())<=25){%> bg-p1 <%}%>
																								   <%if((Integer.parseInt(obj[60].toString())>25) && (Integer.parseInt(obj[60].toString())<=50)){%> bg-p2 <%}%>
																								   <%if((Integer.parseInt(obj[60].toString())>50) && (Integer.parseInt(obj[60].toString())<=75)){%> bg-p3 <%}%>
																								   <%if((Integer.parseInt(obj[60].toString())>75)){%> bg-p4<%}%>
																"><%=obj[60]!=null?StringEscapeUtils.escapeHtml4(obj[60].toString()): " - " %></span>
															</div>
														</div>
													<%}else{ %>
													<div class="progress nil-bar noProgressDiv" >
														<div class="progress-bar noProgress" role="progressbar"  >
															Nil
														</div>
													</div>
													<%} %>
													<%} %>
												</td>
														<td class="custom-td">
												<%if (obj[52].toString().equalsIgnoreCase("N")) {%>
													<%if(Integer.parseInt(obj[8].toString())>0){ %>
														
														<div class="row">
															<div class="col-md-11">
															    <div class="progress" data-toggle="tooltip" title="PMRC Held : <%=obj[4]%> <br> PMRC To Be Held : <%=obj[8] %><br> Total PMRC To Be Held : <%=obj[48] %>"  >
																  <div class="progress-bar progress-bar-striped bg-success width-<%=obj[5]%>" onclick="overallmeetingredirect('<%=obj[2]%>','1', 'B')"  ></div>
																  <div class="progress-bar progress-bar-striped bg-primary width-<%=obj[7]%>" onclick="overallmeetingredirect('<%=obj[2]%>','1', 'C' )"  ></div>
																</div>
														  	</div>
															<div class="col-md-1 pl-0" >
																<span class="health-circle <%if(Integer.parseInt(obj[5].toString())<=25){%> bg-p1 <%}%>
																								   <%if( (Integer.parseInt(obj[5].toString())>25) && (Integer.parseInt(obj[5].toString())<=50)){%> bg-p2 <%}%>
																								   <%if( (Integer.parseInt(obj[5].toString())>50) && (Integer.parseInt(obj[5].toString())<=75)){%>bg-p3  <%}%>
																								   <%if( (Integer.parseInt(obj[5].toString())>75)){%> bg-p4 <%}%>
																">
																<%if(Integer.parseInt(obj[5].toString())>100){ %>
																	100
																<%}else{ %>
																<%=obj[5]!=null?StringEscapeUtils.escapeHtml4(obj[5].toString()): " - " %>
																<%} %>
																</span>
															</div>
														</div>
						
													<%}else{ %>
													<div class="progress nil-bar noProgressDiv" >
														<div class="progress-bar noProgress" role="progressbar" >
															Nil
														</div>
													</div>
													<%} %>
													<%}else{ %>
														<%if(Integer.parseInt(obj[57].toString())>0){ %>
														
														<div class="row">
															<div class="col-md-11">
															    <div class="progress" data-toggle="tooltip" title="PJB Held : <%=obj[53]%> <br> PJB To Be Held : <%=obj[57] %><br> Total PJB To Be Held : <%=obj[58] %>"  >
																  <div class="progress-bar progress-bar-striped bg-success width-<%=obj[54]%>"  ></div>
																  <div class="progress-bar progress-bar-striped bg-primary width-<%=obj[56]%>"   ></div>
																</div>
														  	</div>
															<div class="col-md-1 pl-0" >
																<span class="health-circle <%if(Integer.parseInt(obj[54].toString())<=25){%> bg-p1 <%}%>
																								   <%if( (Integer.parseInt(obj[54].toString())>25) && (Integer.parseInt(obj[54].toString())<=50)){%> bg-p2 <%}%>
																								   <%if( (Integer.parseInt(obj[54].toString())>50) && (Integer.parseInt(obj[54].toString())<=75)){%> bg-p3  <%}%>
																								   <%if( (Integer.parseInt(obj[54].toString())>75)){%> bg-p4 <%}%>
																">
																<%if(Integer.parseInt(obj[54].toString())>100){ %>
																	100
																<%}else{ %>
																<%=obj[54]!=null?StringEscapeUtils.escapeHtml4(obj[54].toString()): " - " %>
																<%} %>
																</span>
															</div>
														</div>
						
													<%}else{ %>
													<div class="progress nil-bar noProgressDiv" >
														<div class="progress-bar noProgress" role="progressbar"   >
															Nil
														</div>
													</div>
													<%} %>
													
													
													
													
													<%} %>
												</td>
												<td class="custom-td">
													<%if(Integer.parseInt(obj[20].toString())>0){ %>
													<div class="row">
														<div class="col-md-11">
															<div class="progress" onclick="overallcommonredirect('mil','<%=obj[2]%>')">
															  <div class="progress-bar progress-bar-striped bg-success width-<%=obj[19]%>"  data-toggle="tooltip" title="Completed : <%=obj[18]%> / <%=obj[20] %>" ></div>
															  <div class="progress-bar progress-bar-striped bg-warning width-<%=obj[17]%>"  data-toggle="tooltip" title="Delayed : <%=obj[16]%> / <%=obj[20] %>" ></div>
															  <div class="progress-bar progress-bar-striped bg-danger width-<%=obj[15]%>" data-toggle="tooltip" title="Pending : <%=obj[14]%> / <%=obj[20] %>" ></div>															  
															</div>
														</div>
														<div class="col-md-1 pl-0" >
																<span class="health-circle <%if(Integer.parseInt(obj[19].toString())<=25){%> bg-p1 <%}%>
																								   <%if( (Integer.parseInt(obj[19].toString())>25) && (Integer.parseInt(obj[19].toString())<=50)){%> bg-p2<%}%>
																								   <%if( (Integer.parseInt(obj[19].toString())>50) && (Integer.parseInt(obj[19].toString())<=75)){%> bg-p3<%}%>
																								   <%if( (Integer.parseInt(obj[19].toString())>75) && (Integer.parseInt(obj[19].toString())<=100)){%> bg-p4 <%}%>
																"><%=obj[19]!=null?StringEscapeUtils.escapeHtml4(obj[19].toString()): " - " %></span>
														</div>
													</div>
													<%}else{ %>
													<div class="progress nil-bar noProgressDiv" >
														<div class="progress-bar noProgress" role="progressbar"  >
															Nil
														</div>
													</div>
													<%} %>
												</td>
												<td class="custom-td">
													<%if(Integer.parseInt(obj[29].toString())>0){ %>
													<div class="row">
														<div class="col-md-11">
															<div class="progress" onclick="overallcommonredirect('action','<%=obj[2]%>')">
															  <div class="progress-bar progress-bar-striped bg-success width-<%=obj[28]%>"  data-toggle="tooltip" title="Completed : <%=obj[27]%> / <%=obj[29]%>" ></div>
															  <div class="progress-bar progress-bar-striped bg-warning width-<%=obj[26]%>" data-toggle="tooltip" title="Delayed : <%=obj[25]%> / <%=obj[29]%>" ></div>
															  <div class="progress-bar progress-bar-striped bg-primary width-<%=obj[24]%>"  data-toggle="tooltip" title="Forwarded : <%=obj[23]%> / <%=obj[29]%>" ></div>
															  <div class="progress-bar progress-bar-striped bg-danger width-<%=obj[22]%>"  data-toggle="tooltip" title="Pending : <%=obj[21]%> / <%=obj[29]%>" ></div>
															</div>
														</div>
														<div class="col-md-1 pl-0">
															<span class="health-circle <%if(Integer.parseInt(obj[28].toString())<=25){%> bg-p1<%}%>
																									   <%if( (Integer.parseInt(obj[28].toString())>25) && (Integer.parseInt(obj[28].toString())<=50)){%> bg-p2 <%}%>
																									   <%if( (Integer.parseInt(obj[28].toString())>50) && (Integer.parseInt(obj[28].toString())<=75)){%> bg-p3 <%}%>
																									   <%if( (Integer.parseInt(obj[28].toString())>75) && (Integer.parseInt(obj[28].toString())<=100)){%> bg-p4 <%}%>
															"><%=obj[28]!=null?StringEscapeUtils.escapeHtml4(obj[28].toString()): " - " %></span>
														</div>
													</div>
													<%}else{ %>
													<div class="progress nil-bar noProgressDiv" >
														<div class="progress-bar noProgress" role="progressbar"   >
															Nil
														</div>
													</div>
													<%} %>
												</td>
												<td class="custom-td">
													<%if(Integer.parseInt(obj[34].toString())>0){ %>
													<div class="row">
														<div class="col-md-11">
															<div class="progress" onclick="overallcommonredirect('risk','<%=obj[2]%>')">
															  <div class="progress-bar progress-bar-striped bg-success width-<%=obj[31]%>"data-toggle="tooltip" title="Completed : <%=obj[30]%> / <%=obj[34] %>" ></div>
															  <div class="progress-bar progress-bar-striped bg-danger width-<%=obj[33]%>" data-toggle="tooltip" title="Pending : <%=obj[32]%> / <%=obj[34] %>" ></div>
															</div>
														</div>
														<div class="col-md-1 pl-0" >
																<span class="health-circle <%if(Integer.parseInt(obj[31].toString())<=25){%> bg-p1 <%}%>
																										   <%if( (Integer.parseInt(obj[31].toString())>25) && (Integer.parseInt(obj[31].toString())<=50)){%> bg-p2 <%}%>
																										   <%if( (Integer.parseInt(obj[31].toString())>50) && (Integer.parseInt(obj[31].toString())<=75)){%> bg-p3 <%}%>
																										   <%if( (Integer.parseInt(obj[31].toString())>75) && (Integer.parseInt(obj[31].toString())<=100)){%> bg-p4 <%}%>
																"><%=obj[31]!=null?StringEscapeUtils.escapeHtml4(obj[31].toString()): " - " %></span>
														</div>
													</div>
													<%}else{ %>
													<div class="progress nil-bar noProgressDiv" >
														<div class="progress-bar noProgress" role="progressbar"   >
															Nil
														</div>
													</div>
													<%} %>
												</td>
												<td class="custom-td">
													<%
													BigInteger number = new BigInteger(obj[43].toString());
													BigInteger total = new BigInteger("0");
													if(number.compareTo(new BigInteger("0")) >0){ %>
													<div class="row">
														<div class="col-md-10">
															<%total = total.add(new BigInteger(String.valueOf(Math.round(Double.parseDouble(obj[35].toString() ))))); %>
															<%total = total.add(new BigInteger(String.valueOf(Math.round(Double.parseDouble(obj[37].toString() ))))); %>
															<%total = total.add(new BigInteger(String.valueOf(Math.round(Double.parseDouble(obj[39].toString() ))))); %>
															<%total = total.add(new BigInteger(String.valueOf(Math.round(Double.parseDouble(obj[41].toString() ))))); %>
															<div class="progress" onclick="overallfinance()" data-toggle="tooltip" title="Expenditure : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[35].toString() ))))%>
																																		<br>OC : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[37].toString() ))))%> 
																																		<br>DIPL : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[39].toString() ))))%> 
																																		<br>Balance : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[41].toString() ))))%> 
																																		<br><b>Total : &#8377; <%=nfc.rupeeFormat(total.toString())%> </b>
																																																		">

															  	<div class="progress-bar progress-bar-striped bg-success width-<%=obj[36]%>"   > </div>
															 	<div class="progress-bar progress-bar-striped bg-warning width-<%=obj[38]%>"  > </div>
															  	<div class="progress-bar progress-bar-striped bg-primary width-<%=obj[40]%>"   > </div>
															  	<div class="progress-bar progress-bar-striped bg-danger width-<%=obj[42]%>"   > </div>
															  	
															</div>
														</div>
														<div class="col-md-1 pl-0" >
																	<span class="health-circle <%if(Integer.parseInt(obj[42].toString())<=25){%> bg-p1<%}%>
																											   <%if( (Integer.parseInt(obj[42].toString())>25) && (Integer.parseInt(obj[42].toString())<=50)){%> bg-p2 <%}%>
																											   <%if( (Integer.parseInt(obj[42].toString())>50) && (Integer.parseInt(obj[42].toString())<=75)){%> bg-p3 <%}%>
																											   <%if( (Integer.parseInt(obj[42].toString())>75) && (Integer.parseInt(obj[42].toString())<=100)){%> bg-p4 <%}%>"
																	><%=obj[42]!=null?StringEscapeUtils.escapeHtml4(obj[42].toString()): " - " %></span>
														</div>
														<div class="col-md-1 pl-0" >
														</div>
													</div>
													
													<%}else{ %>
													<div class="progress nil-bar noProgressDiv" >
														<div class="progress-bar noProgress" role="progressbar"  >
															Nil
														</div>
													</div>
													<%} %>
												</td>
											</tr> 
											
											<%  ProjectCount++; } }  } } %>
											
										</tbody>
											
											
									</table>	
									
												
				
								</div> 
							
							</div> 
					<!-- /////////////// ALL PROJECT DETAILS IN OVERALL DASHBOARD ENDS //////////// -->		
							
					</div>
<!-- ****************************************************************** OVERALL DASHBOARD MODULE STARTS********************************************************************************* -->

	 <!-- *************************************** INDIVIDUAL PROJECT DETAILS STARTS ******************************** -->
	
	<div class="card clx-25"  id="projectgraph" >
		<div class="clx-26" align="center">
			<div  class="clx-44">
				<span class="ProjecChartCardTitle clx-27" ></span>
				<button class="btn prints clx-28"  onclick="overalldoc()" data-toggle="tooltip" title="Doc"><i class="fa fa-file-text-o" aria-hidden="true"></i></button>
				<button class="btn  btn-danger clx-29"  onclick="overalldetails('A')" data-toggle="tooltip" title="Close" >CLOSE &nbsp;<i class="fa fa-times-circle faa-pulse animated faa-fast" aria-hidden="true"></i></button>
				
				<br>
			</div>
		</div>
				<div class="row">
					<div class="col-md-3">
	 					<figure class="highcharts-figure">
							<div id="containerh"></div>
						</figure>
					</div>
					<div class="col-md-1"></div>
					<div class="col-md-3">
	 					<figure class="highcharts-figure">
							<div id="containerh2"></div>
						</figure>
					</div>
					<div class="col-md-1"></div>
					<div class="col-md-3">
	 					<figure class="highcharts-figure">
							<div id="containerh3"></div>
						</figure>
					</div>
				</div>
				<hr>
				<div class="row">
					<div class="col-md-3">
	 					<figure class="highcharts-figure">
							<div id="containerh4"></div>
						</figure>
					</div>
					<div class="col-md-1"></div>
					<div class="col-md-3">
	 					<figure class="highcharts-figure">
							<div id="containerh5"></div>
						</figure>
					</div>
					<div class="col-md-1"></div>
					<div class="col-md-3">
	 					<figure class="highcharts-figure">
							<div id="containerh6"></div>
						</figure>
					</div>
				</div>
			
		</div>
     <!-- *************************************** INDIVIDUAL PROJECT DETAILS ENDS ******************************** -->
     
	 <!-- *************************************** INFORMATIVE MODAL STARTS **************************************** -->
	 
	<div class="modal fade " id="exampleModal1" tabindex="-1" aria-labelledby="exampleModalLabel1" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				      
				<div class="modal-header ">
					<h5 class="modal-title clx-30" id="exampleModalLabel" >Colour Coding Summary</h5>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
			        </button>
			  	</div>
					      
				<div class="modal-body">
						
					<%
					 if(!IsDG.equalsIgnoreCase("Yes") ) { %>	
						
					<div class="row">
						<div class="text-left">
								<p class="clx-31">Users </p>
								<hr class="modal-hr">
								<ul class="modal-list">
						          	<li><span class="modal-span cl-g" >&#8226;</span><span class="modal-text">Indian Army</span></li>
						           	<li><span class="modal-span cl-1" >&#8226;</span><span class="modal-text">Indian Navy</span></li>
						           	<li><span class="modal-span cl-2" >&#8226;</span><span class="modal-text">Indian Air Forces</span></li>
						           	<li><span class="modal-span cl-3" >&#8226;</span><span class="modal-text">Home Land Security</span></li>
						           	<li><span class="modal-span cl-4" >&#8226;</span><span class="modal-text">DRDO</span></li>
						           	<li><span class="modal-span cl-5" >&#8226;</span><span class="modal-text">Others</span></li>
					            </ul>
							</div>
					</div>
					
					<%} %>
						
					<div class="row">
						<div class="text-left">
								<p class="mb0l10">Project Health (Completed %)</p>
								<hr class="modal-hr">
								<ul class="modal-list">
									<li><span class="modal-span cl-6" >&#8226;</span><span class="modal-text">0-25%</span></li>
						           	<li><span class="modal-span cl-5" >&#8226;</span><span class="modal-text">26-50%</span></li>
						           	<li><span class="modal-span cl-7" >&#8226;</span><span class="modal-text">51-75%</span></li>
						           	<li><span class="modal-span cl-g" >&#8226;</span><span class="modal-text">76-100%</span></li>
					            </ul>
							</div>
					</div>		

					<div class="row">
						<div class="text-left">
								<p class="mb0l10">Milestone</p>
								<hr class="modal-hr">
								<ul class="modal-list">
									<li><span class="modal-span green" >&#8226;</span><span class="modal-text">Completed</span></li>
									<li><span class="modal-span yellow" >&#8226;</span><span class="modal-text">Delayed</span></li>
						          	<li><span class="modal-span red" >&#8226;</span><span class="modal-text">Pending</span></li>
					            </ul>
							</div>
					</div>		
					
					<div class="row">
						<div class="text-left">
								<p class="mb0l10">Action</p>
								<hr class="modal-hr">
								<ul class="modal-list">
						           	<li><span class="modal-span green" >&#8226;</span><span class="modal-text">Completed</span></li>
						           	<li><span class="modal-span yellow" >&#8226;</span><span class="modal-text">Delayed</span></li>
						           	<li><span class="modal-span blue" >&#8226;</span><span class="modal-text">Forwarded</span></li>
						           	<li><span class="modal-span red" >&#8226;</span><span class="modal-text">Pending</span></li>
					            </ul>
							</div>
					</div>
					
					<div class="row">
						<div class="text-left">
								<p class="mb0l10">Risks</p>
								<hr class="modal-hr">
								<ul class="modal-list">
									<li><span class="modal-span green" >&#8226;</span><span class="modal-text">Completed</span></li>
									<li><span class="modal-span yellow" >&#8226;</span><span class="modal-text">Delayed</span></li>
						          	<li><span class="modal-span red" >&#8226;</span><span class="modal-text">Pending</span></li>
					            </ul>
							</div>
					</div>
					
					<div class="row">
						<div class="text-left">
								<p class="mb0l10">Finance</p>
								<hr class="modal-hr">
								<ul class="modal-list">
						          	<li><span class="modal-span green" >&#8226;</span><span class="modal-text">Expenditure</span></li>
						           	<li><span class="modal-span yellow" >&#8226;</span><span class="modal-text">O/S</span></li>
						           	<li><span class="modal-span blue" >&#8226;</span><span class="modal-text">DIPL</span></li>
						           	<li><span class="modal-span red" >&#8226;</span><span class="modal-text">Balance</span></li>
					            </ul>
							</div>
					</div>
					<div class="row">
						<div class="text-left">
								<p class="mb0l10">Cash Out Go</p>
								<hr class="modal-hr">
								<ul class="modal-list">
						          	<li><span class="modal-span cl-8" >&#8226;</span><span class="modal-text">Capital</span></li>
						           	<li><span class="modal-span cl-9" >&#8226;</span><span class="modal-text">Revenue</span></li>
						           	<li><span class="modal-span cl-10" >&#8226;</span><span class="modal-text">Others</span></li>
					            </ul>
							</div>
					</div>
					          
				</div>
				
					      
			</div>
		</div>
	</div>
    <!-- *************************************** INFORMATIVE MODAL ENDS **************************************** -->

	<!-- *************************************** DG VIEW START****************************************************** -->
	
	<div class="dis-none" id="dgdashboard">
	
		<div class="container-fluid">
		<div class="card-deck clx-32"  >
		 	<%if(session.getAttribute("LoginType").toString().equalsIgnoreCase("X")) {%><jsp:include page="DashBoard-circular.jsp" /><%} %>
		</div>
		</div>



		<div class="container-fluid">

		<div class="card bg-transparent">
		
			<div id="overalldivdg" class="clx-33">
								
				<table class="table meeting tableFixHead fixed-table clx-34"  > 
										<thead class="clx-35">												
											<tr>
												<td class="width-4">
													<!-- <a  data-toggle="modal"  class="fa faa-pulse animated " data-target="#exampleModal1" data-whatever="@mdo" style="padding: 0px 1.5rem;cursor:pointer" ><i class="fa fa-info-circle " style="font-size: 1.3rem;color: " aria-hidden="true"></i> </a> -->
												</td>
												<td class="width-4"><span class="j-2">Lab</span></td>
												<!-- <td style="padding: 0px !important"><span style="font-size :15px;font-weight: bold;">AB</span></td> -->
												<td class="pl-0"><span class="j-2">EB / PMB</span></td>
												<td class="pl-0"><span class="j-2">PMRC / PJB</span></td>
												<td ><span class="j-2">Milestone</span></td>
												<td ><span class="j-2">Action</span></td>
												<td ><span class="j-2">Risk</span></td>
												<td ><span class="j-2">Finance</span></td>
											</tr>
										</thead>
										
										<tbody>
											<%for(Object[] obj : projecthealthtotaldg) { %>
											<tr>
												<td>
												<%-- <%=obj[2] %> --%>
												<a href="javascript:LabDetails('<%=obj[45] %>')"> <i class="fa fa-hand-o-right clx-22" aria-hidden="true" ></i></a></td>
												<td class="clx-36"><%=obj[45]!=null?StringEscapeUtils.escapeHtml4(obj[45].toString()): " - " %>	</td>
												<%-- <td class="custom-td">
													<%if(Integer.parseInt(obj[62].toString())>0){ %>
														<div class="row">
															<div class="col-md-11">
															    <div class="progress" data-toggle="tooltip" title="AB Held : <%=obj[60]%> <br> AB To Be Held : <%=obj[62]%><br> Total AB To Be Held : <%=obj[63] %>" >
																  <div class="progress-bar progress-bar-striped bg-success" onclick="overallmeetingredirectdg('<%=obj[62]%>','1', 'B')" style="width:<%=obj[64]%>%;"  ></div>
																  <div class="progress-bar progress-bar-striped bg-primary" onclick="overallmeetingredirectdg('<%=obj[62]%>','1', 'C' )" style="width:<%=obj[65]%>%;" ></div>
																</div>
														  	</div>
															<div class="col-md-1" style="padding-left: 0px !important">
																<span class="health-circle" style="<%if(Integer.parseInt(obj[64].toString())<=25){%> background-color:red <%}%>
																								   <%if( (Integer.parseInt(obj[64].toString())>25) && (Integer.parseInt(obj[64].toString())<=50)){%> background-color: #EE5007; <%}%>
																								   <%if( (Integer.parseInt(obj[64].toString())>50) && (Integer.parseInt(obj[64].toString())<=75)){%> background-color: #F8CB2E;color:black <%}%>
																								   <%if( (Integer.parseInt(obj[64].toString())>75) && (Integer.parseInt(obj[64].toString())<=100)){%> background-color:green <%}%>
																"><%=obj[64] %></span>
															</div>
											
														</div>
						
													<%}else{ %>
													<div class="progress" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
														<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
															Nil
														</div>
													</div>
													<%} %>
												</td> --%>
												<td class="custom-td">
													<%if(Integer.parseInt(obj[5].toString())>0){ %>
														
														<div class="row">
															<div class="col-md-11">
															    <div class="progress"  data-toggle="tooltip" title="EB Held : <%=obj[3]%> <br> EB To Be Held : <%=obj[5]%><br> Total EB To Be Held : <%=obj[47] %>">
																  <div class="progress-bar progress-bar-striped bg-success width-<%=obj[31]%>" onclick="overallmeetingredirectdg('<%=obj[2]%>','1', 'B')"   ></div>
																  <div class="progress-bar progress-bar-striped bg-primary width-<%=obj[32]%>" onclick="overallmeetingredirectdg('<%=obj[2]%>','1', 'C' )"  ></div>
																</div>
														  	</div>
															<div class="col-md-1 pl-0" >
																<span class="health-circle <%if(Integer.parseInt(obj[31].toString())<=25){%> bg-p1 <%}%>
																								   <%if( (Integer.parseInt(obj[31].toString())>25) && (Integer.parseInt(obj[31].toString())<=50)){%> bg-p2 <%}%>
																								   <%if( (Integer.parseInt(obj[31].toString())>50) && (Integer.parseInt(obj[31].toString())<=75)){%> bg-p3 <%}%>
																								   <%if( (Integer.parseInt(obj[31].toString())>75) && (Integer.parseInt(obj[31].toString())<=100)){%> bg-p4 <%}%>
																"><%=obj[31]!=null?StringEscapeUtils.escapeHtml4(obj[31].toString()): " - " %></span>
															</div>
														</div>
						
													<%}else{ %>
													<div class="progress noProgressDiv" >
														<div class="progress-bar noProgress" role="progressbar"   >
															Nil
														</div>
													</div>
													<%} %>
												</td>
												<td class="custom-td">
													<%if(Integer.parseInt(obj[2].toString())>0){ %>
														<div class="row">
															<div class="col-md-11">
															    <div class="progress" data-toggle="tooltip" title="PMRC Held : <%=obj[0]%> <br> PMRC To Be Held : <%=obj[2]%><br> Total PMRC To Be Held : <%=obj[46] %>" >
																  <div class="progress-bar progress-bar-striped bg-success width-<%=obj[29]%>" onclick="overallmeetingredirectdg('<%=obj[2]%>','1', 'B')"   ></div>
																  <div class="progress-bar progress-bar-striped bg-primary width-<%=obj[30]%>" onclick="overallmeetingredirectdg('<%=obj[2]%>','1', 'C' )" ></div>
																</div>
														  	</div>
															<div class="col-md-1 pl-0">
																<span class="health-circle <%if(Integer.parseInt(obj[29].toString())<=25){%>bg-p1 <%}%>
																								   <%if( (Integer.parseInt(obj[29].toString())>25) && (Integer.parseInt(obj[29].toString())<=50)){%> bg-p2 <%}%>
																								   <%if( (Integer.parseInt(obj[29].toString())>50) && (Integer.parseInt(obj[29].toString())<=75)){%> bg-p3 <%}%>
																								   <%if( (Integer.parseInt(obj[29].toString())>75) && (Integer.parseInt(obj[29].toString())<=100)){%> bg-p4 <%}%>
																"><%=obj[29]!=null?StringEscapeUtils.escapeHtml4(obj[29].toString()): " - " %></span>
															</div>
											
														</div>
						
													<%}else{ %>
													<div class="progress noProgressDiv" >
														<div class="progress-bar noProgress" role="progressbar"   >
															Nil
														</div>
													</div>
													<%} %>
												</td>	
												<td class="custom-td">
													<%if(Integer.parseInt(obj[9].toString())>0){ %>
													<div class="row">
														<div class="col-md-11">
															<div class="progress" onclick="overallcommonredirectdg('mil','<%=obj[2]%>')">
															  <div class="progress-bar progress-bar-striped bg-success width-<%=obj[10]%>"  data-toggle="tooltip" title="Completed : <%=obj[8]%> / <%=obj[9] %>" ></div>
															  <div class="progress-bar progress-bar-striped bg-warning width-<%=obj[33]%>"  data-toggle="tooltip" title="Delayed : <%=obj[7]%> / <%=obj[9] %>" ></div>
															  <div class="progress-bar progress-bar-striped bg-danger width-<%=obj[44]%>"  data-toggle="tooltip" title="Pending : <%=obj[6]%> / <%=obj[9] %>" ></div>															  
															</div>
														</div>
														<div class="col-md-1 pl-0">
																<span class="health-circle <%if(Integer.parseInt(obj[10].toString())<=25){%> bg-p1 <%}%>
																								   <%if( (Integer.parseInt(obj[10].toString())>25) && (Integer.parseInt(obj[10].toString())<=50)){%> bg-p2 <%}%>
																								   <%if( (Integer.parseInt(obj[10].toString())>50) && (Integer.parseInt(obj[10].toString())<=75)){%> bg-p3 <%}%>
																								   <%if( (Integer.parseInt(obj[10].toString())>75) && (Integer.parseInt(obj[10].toString())<=100)){%> bg-p4 <%}%>
																"><%=obj[10]!=null?StringEscapeUtils.escapeHtml4(obj[10].toString()): " - " %></span>
														</div>
													</div>
													<%}else{ %>
													<div class="progress noProgressDiv" >
														<div class="progress-bar noProgress" role="progressbar"   >
															Nil
														</div>
													</div>
													<%} %>
												</td>
												<td class="custom-td">
													<%if(Integer.parseInt(obj[15].toString())>0){ %>
													<div class="row">
														<div class="col-md-11">
															<div class="progress" onclick="overallcommonredirect('action','<%=obj[2]%>')">
															  <div class="progress-bar progress-bar-striped bg-success width-<%=obj[37]%>"  data-toggle="tooltip" title="Completed : <%=obj[14]%> / <%=obj[15]%>" ></div>
															  <div class="progress-bar progress-bar-striped bg-warning width-<%=obj[36]%>"  data-toggle="tooltip" title="Delayed : <%=obj[13]%> / <%=obj[15]%>" ></div>
															  <div class="progress-bar progress-bar-striped bg-primary width-<%=obj[35]%>"  data-toggle="tooltip" title="Forwarded : <%=obj[12]%> / <%=obj[15]%>" ></div>
															  <div class="progress-bar progress-bar-striped bg-danger width-<%=obj[34]%>"   data-toggle="tooltip" title="Pending : <%=obj[11]%> / <%=obj[15]%>" ></div>
															</div>
														</div>
														<div class="col-md-1 pl-0">
															<span class="health-circle <%if(Integer.parseInt(obj[37].toString())<=25){%> bg-p1 <%}%>
																									   <%if( (Integer.parseInt(obj[37].toString())>25) && (Integer.parseInt(obj[37].toString())<=50)){%> bg-p2 <%}%>
																									   <%if( (Integer.parseInt(obj[37].toString())>50) && (Integer.parseInt(obj[37].toString())<=75)){%> bg-p3 <%}%>
																									   <%if( (Integer.parseInt(obj[37].toString())>75) && (Integer.parseInt(obj[37].toString())<=100)){%> bg-p4 <%}%>
															"><%=obj[37]!=null?StringEscapeUtils.escapeHtml4(obj[37].toString()): " - " %></span>
														</div>
													</div>
													<%}else{ %>
													<div class="progress noProgressDiv" >
														<div class="progress-bar noProgress" role="progressbar"   >
															Nil
														</div>
													</div>
													<%} %>
												</td> 
												<td class="custom-td">
													<%if(Integer.parseInt(obj[18].toString())>0){ %>
													<div class="row">
														<div class="col-md-11">
															<div class="progress" onclick="overallcommonredirect('risk','<%=obj[2]%>')">
															  <div class="progress-bar progress-bar-striped bg-success width-<%=obj[39]%>" data-toggle="tooltip" title="Completed : <%=obj[16]%> / <%=obj[18] %>" ></div>
															  <div class="progress-bar progress-bar-striped bg-danger width-<%=obj[38]%>"  data-toggle="tooltip" title="Pending : <%=obj[17]%> / <%=obj[18] %>" ></div>
															</div>
														</div>
														<div class="col-md-1 pl-0">
																<span class="health-circle <%if(Integer.parseInt(obj[39].toString())<=25){%> bg-p1 <%}%>
																										   <%if( (Integer.parseInt(obj[39].toString())>25) && (Integer.parseInt(obj[39].toString())<=50)){%> bg-p2 <%}%>
																										   <%if( (Integer.parseInt(obj[39].toString())>50) && (Integer.parseInt(obj[39].toString())<=75)){%> bg-p3<%}%>
																										   <%if( (Integer.parseInt(obj[39].toString())>75) && (Integer.parseInt(obj[39].toString())<=100)){%> bg-p4 <%}%>
																"><%=obj[39]!=null?StringEscapeUtils.escapeHtml4(obj[39].toString()): " - " %></span>
														</div>
													</div>
													<%}else{ %>
													<div class="progress noProgressDiv" >
														<div class="progress-bar noProgress" role="progressbar"  >
															Nil
														</div>
													</div>
													<%} %>
												</td>
												<td class="custom-td">
													<%
													BigInteger number = new BigInteger(obj[23].toString());
													if(number.compareTo(new BigInteger("0")) >0){ %>
													<div class="row">
														<div class="col-md-10">
															<div class="progress" onclick="overallfinance()">
															  <div class="progress-bar progress-bar-striped bg-success width-<%=obj[40]%>"  data-toggle="tooltip" title="Expenditure : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[19].toString() ))))%>" ></div>
															  <div class="progress-bar progress-bar-striped bg-warning width-<%=obj[41]%>"  data-toggle="tooltip" title="OC : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[20].toString() ))))%> " ></div>
															  <div class="progress-bar progress-bar-striped bg-primary width-<%=obj[42]%>"  data-toggle="tooltip" title="DIPL : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[21].toString() ))))%> " ></div>
															  <div class="progress-bar progress-bar-striped bg-danger width-<%=obj[43]%>"   data-toggle="tooltip" title="Balance : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[22].toString() ))))%> " ></div>
															</div>
														</div>
														<div class="col-md-1 pl-0">
																	<span class="health-circle <%if(Integer.parseInt(obj[43].toString())<=25){%> bg-p1 <%}%>
																											   <%if( (Integer.parseInt(obj[43].toString())>25) && (Integer.parseInt(obj[43].toString())<=50)){%> bg-p2 <%}%>
																											   <%if( (Integer.parseInt(obj[43].toString())>50) && (Integer.parseInt(obj[43].toString())<=75)){%> bg-p3 <%}%>
																											   <%if( (Integer.parseInt(obj[43].toString())>75) && (Integer.parseInt(obj[43].toString())<=100)){%> bg-p4 <%}%>
																	"><%=obj[43]!=null?StringEscapeUtils.escapeHtml4(obj[43].toString()): " - " %></span>
														</div>
														<div class="col-md-1 pl-0">
														</div>
													</div>
													
													<%}else{ %>
													<div class="progress noProgressDiv" >
														<div class="progress-bar noProgress" role="progressbar"  >
															Nil
														</div>
													</div>
													<%} %>
												</td> 
												
												
											</tr>
											
											
											<%} %>
											
											
										</tbody>
											
											
									</table>	
								</div> 
							</div> 
		</div>
	</div>
	<!-- *************************************** DG VIEW END****************************************************** -->

	

	<form method="post" action="ActionWiseAllReport.htm" name="dateform" id="dateform">                                                    	
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 													
		<input type="hidden" name="Type"  id="TypeId" />
		<input type="hidden" name="ActionType" id="ActionType" />
		<input type="hidden" name="ProjectId" id="Id"/>		
	</form>
	
	<form method="post" action="CommitteeAutoScheduleList.htm" name="committeeform" id="committeeform">                                                    	
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 	
		<input type="hidden" name="projectid"  id="projectidauto" />	
		<input type="hidden" name="projectstatus"  id="projectstatus" />	
		<input type="hidden" name="committeeid"  id="committeeid" />	
		<input type="hidden" name="divisionid"	value="D" /> 
		<input type="hidden" name="initiationid" value="0" /> 													
	</form>
	
	<form method="post" action="NonProjectCommitteeAutoSchedule.htm" name="committeegeneralform" id="committeegeneralform">                                                    	
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 	
		<input type="hidden" name="committeeid" value="A"  id="committeeid" />	
		<input type="hidden" name="dashboardlink" value="dashboard" id="dashboardlink" />
	</form>
	
	<form method="post" action="ActionReportSubmit.htm" name="mytaskactionform" id="mytaskactionform">                                                    	
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 	
		<input type="hidden" name="Term"  id="term" />	
		<input type="hidden" name="Type"  id="type" />
	</form> 
	
	<form method="POST"  action="MilestoneActivityList.htm" name="milestoneform" id="milestoneform">
		<input type="hidden" name="ProjectId" id="projectIdMil" />
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
	</form>
	
	<form method="POST"  action="ActionPDReports.htm" name="actionredirectform" id="actionredirectform">
		<input type="hidden" name="ProjectId" id="projectIdAction" />
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
	</form>
	
	<form method="POST"  action="ProjectRisk.htm" name="projectriskform" id="projectriskform">
		<input type="hidden" name="projectid" id="projectIdRisk" />
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
	</form>
	
	<form method="POST"  action="TestingFileRepo.htm" name="docrepoform" id="docrepoform">
		<input type="hidden" name="projectid" id="projectiddoc" />
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
	</form>
	
	<form method="POST"  action="ChangesinProject.htm" name="changesform" id="changesform">
		<input type="hidden" name="projectid" id="projectidchanges" value="A" />
		<input type="hidden" name="interval" id="intervalchanges" value="T" />
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
	</form>

	<form method="POST" action="LabWiseProjectDetails.htm" name="labdetailsform" id="labdetailsform">
		<input type="hidden" name="labcode" id="labcode">
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
	</form>
 <button class="open-modal-button" id="modalbtn" onclick="openModal()"><i class="fa fa-arrow-left" aria-hidden="true"></i></button>
<form action="#" >
 <%if(ProjectList.size()>0){%><button type="submit" class="open-modal-button " id="brifingBtn" onclick="" data-toggle="tooltip" title="Briefing"  formaction="ProjectBriefingPaper.htm" formmethod="get"><img class="fa faa-pulse animated faa-fast width21x" alt="" src="view/images/requirements.png"  ></button><%} %>
</form>
  <!-- Modal Container -->
  <div id="myModal" class="modal-container" >
    <div class="modalheader clx-37" >
    	<p class="clx-38"><b> Today's Date : &nbsp;</b><%=sdf.format(sdf1.parse( LocalDate.now().toString()))%></p>
     <!--  <span class="modal-close"  onclick="closeModal()">&times;</span> -->
    </div>
    <div class="modalcontent" >
      <p class="clx-39" onclick="openModalDetails('M',<%=todayMeetingCount%>)"> <span class="clx-40">Meetings Scheduled Today</span>:&nbsp;&nbsp;<span class="clx-41"><%=todayMeetingCount %></span> </p>
      <a <%if(actionCounts>0){ %> href="AssigneeList.htm"<%} %>class="clx-42"><span class="clx-40">Actions PDC Today</span>:
      <span class="clx-41"><%=actionCounts %></span>
      </a>
    <%--   <%if(LabCode.equalsIgnoreCase("ADE")){ %>
      <%if(todayRfaCount1>0){ %>
      <a href="RfaAction.htm?Status=O" style="font-weight: 600;float:left; color:black; margin-top: 10px"><span style="text-decoration: underline">RFA Pending</span>:
      <span style="border:1px solid trasparent;padding:4px;border-radius: 5px;background: green;color:white;"><%=todayRfaCount1 %></span>
      </a>
      <%} %>
       <%if(rfaForwardCount>0){ %>
      <a href="RfaActionForwardList.htm" style="font-weight: 600;float:left; color:black; margin-top: 10px"><span style="text-decoration: underline">RFA Forward Pending</span>:
      <span style="border:1px solid trasparent;padding:4px;border-radius: 5px;background: green;color:white;"><%=rfaForwardCount %></span>
      </a>
      <%} %>
       <%if(rfaInspectionCount>0){ %>
      <a href="RfaInspection.htm" style="font-weight: 600;float:left; color:black; margin-top: 10px"><span style="text-decoration: underline">RFA Inspection Pending</span>:
      <span style="border:1px solid trasparent;padding:4px;border-radius: 5px;background: green;color:white;"><%=rfaInspectionCount %></span>
      </a>
      <%} %>
       <%if(rfaInspectionAprCount>0){ %>
      <a href="RfaInspectionApproval.htm" style="font-weight: 600;float:left; color:black; margin-top: 10px"><span style="text-decoration: underline">RFA Inspection Forward Pending</span>:
      <span style="border:1px solid trasparent;padding:4px;border-radius: 5px;background: green;color:white;"><%=rfaInspectionAprCount %></span>
      </a>
      <%} %>
      <%}%> --%>
    </div>
    <div class="modalfooter">
      <button class="btn clx-43" onclick="closeModal()">Close</button>
    </div>
  </div>

  <div id="ModalDetails" class="modalcontainer">
    <div class="modalheader">
      <span class="modal-close"  onclick="closeModals()">&times;</span>
    </div>
    <div class="modalcontent"  id="modalcontents">
    </div>
  </div>



<script type="text/javascript">



function scrollProjectAttributesTop(e) 
{ 
    $('html, body').animate({ scrollTop: $('#overalltable').offset().top }, 'slow'); 
}

function ProjectDetails(value,ProjectCode)
{
	$("#overalldiv").css("display","none");
	$('#projectgraph').css("display","block");
	
	$('#projectiddoc').val(value);
	$('#projectidchanges').val(value);
	charts(value);
	
	$('#COG-Buildup-row').hide();
	$('#COG-Total-row').hide();
	$('#overallmodulecarddeck').hide();
	$('#changes-tab').hide();
	scrollProjectAttributesTop(this);
	CashOutGoProject(ProjectCode);
}

function overalldetails(value){
	
	$("#overalldiv").css("display","block");
	$('#projectgraph').css("display","none");
	$('#projectidchanges').val("A");
	
	charts(value);
	
	$('#COG-Buildup-row').show();
	$('#COG-Total-row').show();
	$('#overallmodulecarddeck').show();
	$('#changes-tab').show();
	CashOutGoProject('0');
}

function LabDetails(value){
	
	$("#labcode").val(value);
	$('#labdetailsform').submit();
	
}


</script>


<script type="text/javascript">
function ChangesForm(value){
	
	$('#intervalchanges').val(value);
	$('#changesform').submit();
}
function overallmeetingredirect(projectid,committeeid,projectstatus){
	$('#projectstatus').val(projectstatus);
	$('#committeeid').val(committeeid);
	$('#projectidauto').val(projectid);
	$('#committeeform').submit();
}
function overallcommonredirect(form,projectid){
		
	if(form=='mil'){
		$('#projectIdMil').val(projectid);
		$('#milestoneform').submit();	
	}
	
	if(form=='action'){
		$('#projectIdAction').val(projectid);
		$('#actionredirectform').submit();
	}
	
	if(form=='risk'){
		$('#projectIdRisk').val(projectid);
		$('#projectriskform').submit();
	}
	
}

function overallfinance(){
	
	$('#ibasform').submit();
	
}

function overalldoc(){
	$('#docrepoform').submit();
}

 $(document).ready(function(){

	var logintype = '<%=(String)request.getAttribute("logintype")%>'
	 
	 var DG= '<%=IsDG%>';
	 
	 	if(DG=='Yes'){
	 		if(logintype =='A' || logintype=='Z' || logintype=='X' || logintype=='K' || logintype=='C' || logintype=='I'  ){
	 			$('.btn5').click();
	 		}else{
	 			$('.btn1').click();
	 		}
	 		
	 	}
	 	else if(logintype == 'A' || logintype == 'Z' || logintype == 'E' || logintype=='C' || logintype=='I'|| logintype=='G' || logintype=='F'){	
				$('.btn3').click();
		} else{
			$('.btn1').click();
		}
		
	 
 })
 
 </script>



<script type="text/javascript">


$('.progress-bar[data-toggle="tooltip"]').tooltip({
    animated: 'fade',
    placement: 'bottom',
    html : true,
    boundary: 'window'
});

$('.btn-toggle').click(function() {
	
    $(this).find('.btn').toggleClass('active');  
    
    if ($(this).find('.btn-success').length>0) {
    	
    	$(this).find('.btn').toggleClass('btn-success');
    }
    
    $(this).find('.btn').toggleClass('btn-default');
    
    
});

$('.btn4').hide(); 

 $('.btn1').click(function(){
	$('.btn4').hide();
	$('.btn1').css('background-color','green');
	$('.btn1').css('color','white');
	$('.btn2').css('background-color','white');
	$('.btn2').css('color','black');
	$('.btn3').css('background-color','white');
	$('.btn3').css('color','black');
	$('.btn5').css('background-color','white');
	$('.btn5').css('color','black');
	
	$("#projectname").css("display","none");
	$("#projectdropdown").css("display","none");
	$("#financialdata").css("display","none");
	$("#financialdataerror").css("display","none");
	$("#ganttchart").css("display","none");
	$("#activitystatusheader").css("display","none");
	$("#activitystatus").css("display","none");
	$("#meetingblock").css("display","none");
	$("#projectdetails1").css("display","none");
	$("#projectdetails2").css("display","none");
	$("#overalltable").css("display","none");
	$("#force-btn").css("display","none");
	$("#overallcard1").css("display","none");
	$("#overallcard2").css("display","none");
	$("#overallcard3").css("display","none");
	$("#overallcard4").css("display","none");
	$("#overallcard5").css("display","none");
	$(".overallheader").css("display","none");
	$('#projectgraph').css("display","none");
	
	$("#todayschedules").css("display","block");
	$("#quicklinks").css("display","block");
	$("#approvalblock").css("display","block");
	$("#mytasks").css("display","block");
	$("#upcomingschedules").css("display","block");
	$("#mytaskdetails").css("display","block");
	$("#noticeboard").css("display","block");
	$("#review").css("display","block");
	$("#reviewheader").css("display","block");
	$("#review2").css("display","block");
	$("#reviewheader2").css("display","block");
	$("#mainactivitystatus").css("display","block");
	
	$('#dgdashboard').css("display","none");

}) 

$('.btn2').click(function(){
	$('.btn4').hide();
	$('.btn2').css('background-color','green');
	$('.btn2').css('color','white');
	$('.btn1').css('background-color','white');
	$('.btn1').css('color','black');
	$('.btn3').css('background-color','white');
	$('.btn3').css('color','black');
	$('.btn5').css('background-color','white');
	$('.btn5').css('color','black');
	
	$("#projectname").css("display","block");
	$("#projectdropdown").css("display","block");
	$("#financialdata").css("display","block");
	$("#financialdataerror").css("display","block");
	$("#ganttchart").css("display","block");
	$("#activitystatusheader").css("display","block");
	$("#activitystatus").css("display","block");
	$("#meetingblock").css("display","block");
	$("#projectdetails1").css("display","block");
	$("#projectdetails2").css("display","block");
	$('#mainactivitystatus').css("display","block");	

	$("#todayschedules").css("display","none");
	$("#quicklinks").css("display","none");
	$("#approvalblock").css("display","none");
	$("#mytasks").css("display","none");
	$("#upcomingschedules").css("display","none");
	$("#mytaskdetails").css("display","none");
	$("#noticeboard").css("display","none");
	$("#review").css("display","none");
	$("#reviewheader").css("display","none");
	$("#review2").css("display","none");
	$("#reviewheader2").css("display","none");
	$("#overalltable").css("display","none");
	$("#force-btn").css("display","none");
	$("#overallcard1").css("display","none");
	$("#overallcard2").css("display","none");
	$("#overallcard3").css("display","none");
	$("#overallcard4").css("display","none");
	$("#overallcard5").css("display","none");
	$(".overallheader").css("display","none");
	$('#projectgraph').css("display","none");
	
	$('#dgdashboard').css("display","none");
})

$('.btn3').click(function(){
	$('.btn4').show();
	$('.btn4').css('background-color','white');
	$('.btn4').css('color','black');
	$('.btn3').css('background-color','green');
	$('.btn3').css('color','white');
	$('.btn1').css('background-color','white');
	$('.btn1').css('color','black');
	$('.btn2').css('background-color','white');
	$('.btn2').css('color','black');
	
	overalldetails('A');

	
	$("#overalltable").css("display","block");
	$("#force-btn").css("display","block");
	$("#overallcard1").css("display","block");
	$("#overallcard2").css("display","block");
	$("#overallcard3").css("display","block");
	$("#overallcard4").css("display","block");
	$("#overallcard5").css("display","block");
	$(".overallheader").css("display","block");

	$("#todayschedules").css("display","none");
	$("#quicklinks").css("display","none");
	$("#approvalblock").css("display","none");
	$("#mytasks").css("display","none");
	$("#upcomingschedules").css("display","none");
	$("#mytaskdetails").css("display","none");
	$("#noticeboard").css("display","none");
	$("#review").css("display","none");
	$("#reviewheader").css("display","none");
	$("#review2").css("display","none");
	$("#reviewheader2").css("display","none");
	$("#projectname").css("display","none");
	$("#projectdropdown").css("display","none");
	$("#financialdata").css("display","none");
	$("#financialdataerror").css("display","none");
	$("#ganttchart").css("display","none");
	$("#activitystatusheader").css("display","none");
	$("#activitystatus").css("display","none");
	$("#meetingblock").css("display","none");
	$("#projectdetails1").css("display","none");
	$("#projectdetails2").css("display","none");
	$('#mainactivitystatus').css("display","none");	
	$('#projectgraph').css("display","none");
	
	<%-- document.getElementById('projecttitle').innerHTML = 'PROJECT HEALTH (' + <%=ProjectCount%> + ')'; --%>	
	$('#dgdashboard').css("display","none");
	
})

$('.btn5').click(function(){
	
	$('.btn4').show();
	$('.btn5').css('background-color','green');
	$('.btn5').css('color','white');
	$('.btn1').css('background-color','white');
	$('.btn1').css('color','black');
	$('.btn2').css('background-color','white');
	$('.btn2').css('color','black');
	
	$('#dgdashboard').css("display","block");

	$("#todayschedules").css("display","none");
	$("#quicklinks").css("display","none");
	$("#approvalblock").css("display","none");
	$("#mytasks").css("display","none");
	$("#upcomingschedules").css("display","none");
	$("#mytaskdetails").css("display","none");
	$("#noticeboard").css("display","none");
	$("#review").css("display","none");
	$("#reviewheader").css("display","none");
	$("#review2").css("display","none");
	$("#reviewheader2").css("display","none");
	$("#projectname").css("display","none");
	$("#projectdropdown").css("display","none");
	$("#financialdata").css("display","none");
	$("#financialdataerror").css("display","none");
	$("#ganttchart").css("display","none");
	$("#activitystatusheader").css("display","none");
	$("#activitystatus").css("display","none");
	$("#meetingblock").css("display","none");
	$("#projectdetails1").css("display","none");
	$("#projectdetails2").css("display","none");
	$('#mainactivitystatus').css("display","none");	
	$('#projectgraph').css("display","none");
	$("#overalltable").css("display","none");
	$("#force-btn").css("display","none");
	$("#overallcard1").css("display","none");
	$("#overallcard2").css("display","none");
	$("#overallcard3").css("display","none");
	$("#overallcard4").css("display","none");
	$("#overallcard5").css("display","none");
	$(".overallheader").css("display","block");
	$('#projectgraph').css("display","none");
	
	
	var logintype = '<%=(String)request.getAttribute("logintype")%>';
	if(logintype=='K')
		document.getElementById('projecttitle').innerHTML = 'DRDO HEALTH' ;
	else
		document.getElementById('projecttitle').innerHTML = 'CLUSTER HEALTH' ;
})



$(document).ready(function(){

	if(<%=count%>>0 ){
		$(".badge-today").text(<%=count%>);
		
		$(".badge-today").addClass('badge-danger');
	}
	
	/* $(".badge-today").addClass('badge-success'); */
	
	var height = $(window).height();
	$('#overalldiv').css('max-height', (height-280)+'px' );
	
})

function actionformtask(term,type){
	
	$('#term').val(term);
	$('#type').val(type);
	 
	$('#mytaskactionform').submit();
	
}

	function actionform(mainid){
				
		$('#Action'+mainid).submit();
		
	}
	function MyTaskDetails(mainid){
		
		$('#MyTaskDetails'+mainid).submit();
	}

	$('#actionsubmit').on('click',function(){
		
		$('#actionform').submit();
		
	})

     $(".action").each(function() {
        var i = $(this).next();
        i.length || (i = $(this).siblings(":first")),
          i.children(":first-child").clone().appendTo($(this));
        
        for (var n = 0; n < 1; n++)(i = i.next()).length ||
          (i = $(this).siblings(":first")),
          i.children(":first-child").clone().appendTo($(this))
    }) 



function dropdown(){


	var val=$('#projectid').val();

	$('.carousel').carousel('pause'); 
	$('.carousel-item').removeClass('active');
	
	$('#Mil'+val).addClass('active');
	$('#Meeting'+val).addClass('active');
	$('#chart'+val).addClass('active');
	$('#act'+val).addClass('active');
	$('#projectname'+val).addClass('active');
	$('#schedule').addClass('active');
	$('#notice').addClass('active');
	$('#actions').addClass('active');
	$('.vert').carousel('cycle'); 
	$('#projectinfo'+val).addClass('active');
	$('#projectdetailsname'+val).addClass('active'); 

	
}

		
	$('.carousel-interval').on('slide.bs.carousel', function(ev) {
	    // get the direction, based on the event which occurs
	    var dir = ev.direction == 'right' ? 'prev' : 'next';
	    // get synchronized non-sliding carousels, and make'em sliding
	    $('.carousel-interval').not('.sliding').addClass('sliding').carousel(dir);
	});
	$('.carousel-interval').on('slid.bs.carousel', function(ev) {
	    // remove .sliding class, to allow the next move
	    $('.carousel-interval').removeClass('sliding');
	});
	
	
	$(document).ready(function(){
	     $(".carousel-interval").carousel({
	         interval : <%=interval%>,
	         pause: false
	     });
	     
	     
	     
	});
	

</script>




<script>

$(document).ready(function () {
	  $('#carouselExampleControls').find('.carousel-item').first().addClass('active');
	});


$(document).ready(function () {
	  $('#carouselExampleControls1').find('.carousel-item').first().addClass('active');
	});
	
$(document).ready(function () {
	  $('#carouselExampleControls2').find('.carousel-item').first().addClass('active');
	});

$(document).ready(function () {
	  $('#carouselExampleControls3').find('.carousel-item').first().addClass('active');
	});
	
$(document).ready(function () {
	  $('#carouselExampleControls5').find('.carousel-item').first().addClass('active');
	});
	
$(document).ready(function () {
	  $('#carouselExampleControls6').find('.carousel-item').first().addClass('active');
	});
	
$(document).ready(function () {
	  $('#carouselExampleControls7').find('.carousel-item').first().addClass('active');
	});
	
$(document).ready(function () {
	  $('#carouselExampleControls8').find('.carousel-item').first().addClass('active');
	});
	
	
$(document).ready(function () {
	  $('#carouselExampleSlidesOnly').find('.carousel-item').first().addClass('active');
	});


$(document).ready(function () {
	  $('#carouselprojectdetailsSlidesOnly').find('.carousel-item').first().addClass('active');
	});
	
$(document).ready(function () {
	  $('#carouselprojectdetailsSlides2Only').find('.carousel-item').first().addClass('active');
	});


function submitForm(type,actionType,id)
{ 

   $("#TypeId").val(type);
   $("#Id").val(id);
   $("#ActionType").val(actionType);
   
   document.getElementById('dateform').submit(); 
} 

function CommitteeForm(id,comid,status)
{ 
	
	if(id==0){
		
		document.getElementById('committeegeneralform').submit();
		
	}
	else{
	$("#projectidauto").val(id);
	$("#committeeid").val(comid);
	$("#projectstatus").val(status);
	document.getElementById('committeeform').submit(); 
	}
} 


	function addNoticeForm() {
		var message = $('textarea#noticeText').val();
	
		if ((message.length)>0){
			if (confirm('Are You Sure to Add this Notice')) {
				document.getElementById("noticeForm").submit();
			}
		}else{
			alert("Notice Field cannot be empty!");
		}
	}

	/* window.setTimeout(function(){
	
	 $(".animate").fadeTo(500,0).slideUp(500,function(){
	 $(this).remove();
	 })
	 },3000) */

	/* window.setTimeout(function(){
	 document.getElementById("title").style.display = "block";
	 document.getElementById("schedules").style.display = "block";
	 },4000) */
	 
</script>	
	<script type="text/javascript">

var fromDate=null;
$("#fdate").change(function(){
	
	 fromDate = $("#fdate").val();


$('#tdate').daterangepicker({

	"singleDatePicker": true,
	"showDropdowns": true,
	"cancelClass": "btn-default",
	"minDate":fromDate,
	
	locale: {
    	format: 'DD-MM-YYYY'
		}
		
});
});
$('#fdate').daterangepicker({
	
	"singleDatePicker": true,
	"showDropdowns": true,
	"cancelClass": "btn-default",
	"minDate":new Date(),
	locale: {
    	format: 'DD-MM-YYYY'
		}
});


</script>
<script>
//document.getElementById("brandname").style.marginLeft = "-20%";
var projectId=$("#projectIdD").val();
var Overall="<%=(String)request.getParameter("Overall")%>"
var DG="<%=IsDG%>";

if(Overall=='Overall'){
	if( DG == 'Yes'){
		$('.btn5').click();
	}else{
		$('.btn3').click();
	}
}



</script>





<script>

function charts(value){
	
$projectid=value;
	
	console.log("charts ->>"+value)
	
	$.ajax({
		
			type:"GET",
			url:"IndividualProjectDetails.htm",
			data : {
				
				ProjectId : $projectid
				
			},
			datatype : 'json',
			success : function(result) {

				console.log("charts ->>"+result)
				
				var result = JSON.parse(result);
				var values = Object.values(result).map(function(key, value) {
					  return result[key,value]
					});

				/* logic to print the project wise data in card */	
				
				/*var s=  values[0] + '/' + values[2]
				document.getElementById('meetingsvaluepmrc').innerHTML = s;*/
				
				/*var t=  values[3] + '/' + values[5]
				document.getElementById('meetingsvalueeb').innerHTML = t;
				
				document.getElementById('pmrcprogress').innerHTML = values[30]+'%' ; */
				
				/* document.getElementById('financevalue').innerHTML = values[23].toLocaleString('en-IN');
				document.getElementById('risksvalue').innerHTML = values[16] + ' / ' + values[18];
				document.getElementById('actionvalue').innerHTML = values[14] + ' / ' + values[15];
				document.getElementById('milestonevalue').innerHTML = values[8] + ' / ' + values[9]; */
				
				<%-- if(values[10]!=null)
				document.getElementById('milestonepercentage').innerHTML = values[10] + '%';
				else
				document.getElementById('milestonepercentage').innerHTML = 'Nil'; 

				if(values[24]!='A')
				document.getElementById('projecttitle').innerHTML = 'Project : ' + values[25];
				else
				document.getElementById('projecttitle').innerHTML = 'PROJECT HEALTH (' + <%=ProjectCount%> + ')';	 --%>
				
				
				
				<!-- ******************************************* Project Details Graph Script ******************************************************* -->
				
				Highcharts.chart('containerh', {
				    chart: {
				        type: 'bar',
				        height: (16 / 16 * 100) + '%' 
				    },
				    exporting: {
				        buttons: {
				          contextButton: {
				            menuItems: [
				              'printChart',
				              'downloadPNG',
				              'downloadJPEG',
				              'downloadPDF',
				              'downloadCSV',
				              'downloadXLS',
				              'viewData'
				            ]
				          }
				        }

				      },
				    title: {
				        text: 'Meeting Data',
				       	style : {
						        	fontWeight: 'bold'
						}
				    },
				    subtitle: {
				        text: ''
				    },
				    xAxis: {
				        categories: ['PMRC', 'EB'],
				        title: {
				            text: null
				        },
				        labels: {
				            overflow: 'justify',
				            style : {
				            	fontWeight: 'bold',
					        	
							}
				        }
				    },
				    yAxis: {
				        min: 0,
				        title: {
				            text: 'Meeting (count)',
				            align: 'high'
				        },
				        labels: {
				            overflow: 'justify',
				            style : {
				            	fontWeight: 'bold',
					        	
							}
				        }
				    },
				    tooltip: {
				        valueSuffix: ''
				    },
				    plotOptions: {
				        bar: {
				            dataLabels: {
				                enabled: true
				            }
				        }
				    },
				    colors: [
				    	'#28a745',
				        '#dc3545',
				        
				    ],
				    credits: {
				        enabled: false
				    },
				    series: meetingdata(),
				 
				    
				});		

				function meetingdata(){
					
					let meetingdata=[];
					if(parseInt(values[2])>0 || parseInt(values[5])>0){
						meetingdata=[
							{
						        name: 'Held',
						        data: [parseInt(values[0]), parseInt(values[3]),]
						    }, {
						        name: 'Pending',
						        data: [parseInt(values[1]), parseInt(values[4]),]
						    },
							
						]
					}
					return meetingdata;
					
				}
				
				/********************************************** Milestone ****************************************************/

			 Highcharts.chart('containerh2', {
					 chart: {
					        type: 'variablepie',
					        height: (16 / 16 * 100) + '%' 
					    },
				    exporting: {
				        buttons: {
				          contextButton: {
				            menuItems: [
				              'printChart',
				              'downloadPNG',
				              'downloadJPEG',
				              'downloadPDF',
				              'downloadCSV',
				              'downloadXLS',
				              'viewData'
				            ]
				          }
				        }

				      },
				    title: {
				        text: 'Milestone',
				        style : {
				        	fontWeight: 'bold'
				        }
				    },
				    subtitle: {
				        text: ''
				    },
				  
				    colors: [
				    	'#28a745',
				    	'#dc3545',
				    	'#ffc107'
				    ],
				    tooltip: {
				        headerFormat: '',
				        pointFormat: '<span style="color:{point.color}">\u25CF</span> <b> {point.name}</b><br/>' 
				        
				    },
				 series: [{
				        minPointSize: 10,
				        innerSize: '20%',
				        zMin: 0,
				        
				      data: [{
				            name: 'Completed('+(parseInt(values[8]))+')',
				            y: parseInt(values[8]),
				            z: 118.7
				        }, {
				            name: 'Delayed('+(parseInt(values[7]))+')',
				            y: parseInt(values[7]),
				            z: 124.6
				        }, {
				            name: 'Pending('+(parseInt(values[6]))+')',
				            y: parseInt(values[6]),
				            z: 137.5
				        }, ]   
				    }],
				    credits: {
				        enabled: false
				    },
				});
				 
		
			 	 Highcharts.chart('containerh3', {
					 chart: {
					        type: 'variablepie',
					        height: (16 / 16 * 100) + '%' 
					    },
				    exporting: {
				        buttons: {
				          contextButton: {
				            menuItems: [
				              'printChart',
				              'downloadPNG',
				              'downloadJPEG',
				              'downloadPDF',
				              'downloadCSV',
				              'downloadXLS',
				              'viewData'
				            ]
				          }
				        }

				      },
				    title: {
				        text: 'Action',
				        style : {
				        	fontWeight: 'bold'
				        }
				    },
				    subtitle: {
				        text: ''
				    },
				  
				    colors: [
				    	'#ffc107',
				        '#dc3545',
				        '#28a745',
				        '#007bff'
				    ],
				    tooltip: {
				        headerFormat: '',
				        pointFormat: '<span style="color:{point.color}">\u25CF</span> <b> {point.name}</b><br/>' 
				        
				    },
				 series: [{
				        minPointSize: 10,
				        innerSize: '20%',
				        zMin: 0,
				        
				      data: [{
				            name: 'Pending('+parseInt(values[11])+')',
				            y: parseInt(values[11]),
				            z: 137.5
				        },
				        {
				            name: 'Delayed('+parseInt(values[13])+')',
				            y: parseInt(values[13]),
				            z: 124.6
				        },
				    	  {
				            name: 'Completed('+parseInt(values[14])+')',
				            y: parseInt(values[14]),
				            z: 118.7
				        }, {
				            name: 'Forwarded('+parseInt(values[12])+')',
				            y: parseInt(values[12]),
				            z: 124.6
				        },  ]   
				    }],
				    credits: {
				        enabled: false
				    },
				}); 
				/*************************************************************** Risk *****************************************************  */

				Highcharts.chart('containerh4', {
				    chart: {
				        type: 'column',
				        height: (16 / 16 * 100) + '%' 
				    },
				    exporting: {
				        buttons: {
				          contextButton: {
				            menuItems: [
				              'printChart',
				              'downloadPNG',
				              'downloadJPEG',
				              'downloadPDF',
				              'downloadCSV',
				              'downloadXLS',
				              'viewData'
				            ]
				          }
				        }

				      },
				    title: {
				        text: 'Risks',
				        style : {
				        	fontWeight: 'bold'
				        }
				    },
				    subtitle: {
				        text: ''
				    },
				    xAxis: {
				        categories: [
				            'Risk',
				        ],
				        crosshair: true
				    },
				    yAxis: {
				        min: 0,
				        title: {
				            text: 'Count'
				        }
				    },
				    tooltip: {
				        headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
				        pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
				            '<td style="padding:0"><b>{point.y:.1f} </b></td></tr>',
				        footerFormat: '</table>',
				        shared: true,
				        useHTML: true
				    },
				    plotOptions: {
				        column: {
				            pointPadding: 0.2,
				            borderWidth: 0
				        }
				    },
				    colors: [
				    	'#28a745',
				        '#dc3545',
				        
				    ],
				    series: riskdata(),
				    credits: {
				        enabled: false
				    },
				});
				
				function riskdata(){
					let riskdata=[];
					if(parseInt(values[18])>0){
						riskdata=[
							{
						        name: 'Completed',
						        data: [parseInt(values[16])]

						    },
							{
						        name: 'Pending',
						        data: [parseInt(values[17])]

						    }, 
						]
						
						
					}
					return riskdata;
					
				}


				/**************************************** Finance ******************************************* */
				
				$(function () {
				
				var TotalFinanceValue =0;
				if(parseInt(values[23])>0){
					 TotalFinanceValue = 'Total Finance Value : ' + parseInt(values[23]).toLocaleString('en-IN', {
					    maximumFractionDigits: 0,
					    style: 'currency',
					    currency: 'INR'
					});	
				}
				
					
				 Highcharts.setOptions({
			      lang: {
			        thousandsSep: ','
			      }
				});
				
				Highcharts.chart('containerh5', {
				    chart: {
				        type: 'pie',
				        height: (16 / 16 * 100) + '%' 
				    },
				    exporting: {
				        buttons: {
				          contextButton: {
				            menuItems: [
				              'printChart',
				              'downloadPNG',
				              'downloadJPEG',
				              'downloadPDF',
				              'downloadCSV',
				              'downloadXLS',
				              'viewData'
				            ]
				          }
				        }

				      },
				    title: {
				        text: 'Finance',
				        style : {
				        	fontWeight: 'bold'
				        }
				    },
				    subtitle: {
				        text: TotalFinanceValue ,
				        style : {
				        	fontWeight: 'bold'
				        }
				    },

				    accessibility: {
				        announceNewData: {
				            enabled: true
				        },
				        point: {
				            valueSuffix: ''
				        }
				    },

				    plotOptions: {
				        series: {
				            dataLabels: {
				                enabled: true,
				                 formatter: function() {
				                    return '<p>&#8377;</p>'+ Highcharts.numberFormat(this.y, 0);
				                } 
				                /* format: '{point.name}: {point.y:.1f}' */
				            }
				        }
				    },
				    
				    
				    
				    colors: [
				        '#ffc107',
				        '#28a745',
				        '#dc3545',
				        '#007bff'
				    ],
				    tooltip: {
				        headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
				        pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>&#8377; {point.y:.2f}</b><br/>'
				    },
				    
				    series: [
				    	{
				            name: "Finance",
				            colorByPoint: true,
				           
				            data:   generatedata(),
		
				        } 
				       
				    ],
				    
				    credits: {
				        enabled: false
				    },

				},

		);
				
				});
			  
		function  generatedata(){
			
			let financedata=[];
			
			if(parseInt(values[23])>0){
				
				financedata = [
					
					{
	                    name: "O/S",
	                    y: parseInt(values[20]),
	                    drilldown: "O/S"
	                },
	                {
	                    name: "Expenditure",
	                    y: parseInt(values[19]),
	                    drilldown: "Expenditure"
	                },
	                {
	                    name: "Balance",
	                    y: parseInt(values[22]),
	                    drilldown: "Balance"
	                },
	                {
	                    name: "DIPL",
	                    y: parseInt(values[21]),
	                    drilldown: "DIPL"
	                }, 
					
				]
				
			}
			return financedata;
		}			
		/**************************************** Document  ******************************************* */
			Highcharts.chart('containerh6', {
			    chart: {
			        type: 'variablepie',
			        height: (16 / 16 * 100) + '%' 
			    },
			    title: {
			        text: 'Document Statistics',
			        style : {
			        	fontWeight: 'bold'
			        }
			    },
			    colors: [
			        '#ffc107',
			        '#28a745',
			        '#dc3545',
			        '#007bff'
			    ],
			    tooltip: {
			        headerFormat: '',
			        pointFormat: '<span style="color:{point.color}">\u25CF</span> <b> {point.name}</b><br/>' 
			        	/* +
			            'Area (square km): <b>{point.y}</b><br/>' +
			            'Population density (people per square km): <b>{point.z}</b><br/>' */
			    },
			    series: [{
			        minPointSize: 10,
			        innerSize: '20%',
			        zMin: 0,
			        name: 'countries',
			        data: [{
			            name: 'Prepared',
			            y: 505370,
			            z: 92.9
			        }, {
			            name: 'Approved',
			            y: 551500,
			            z: 118.7
			        }, {
			            name: 'Pending',
			            y: 312685,
			            z: 124.6
			        }, {
			            name: 'Reviewed',
			            y: 78867,
			            z: 137.5
			        }, ]
			    }],
			    
			    credits: {
			        enabled: false
			    },
			});
		}
		})
		
				
				
		$.ajax({
		
			type:"GET",
			url:"ChangesDataTotalCount.htm",
			data :{
				
				ProjectId : $projectid
				
			},
			datatype : 'json',
			success : function(result){
				
				var result = JSON.parse(result);
				var values = Object.values(result).map(function(key,value){
					
					return result[key,value]
				})
			/* 	
				document.getElementById('todaychangescount').innerHTML = values[0] + values[3] + values[6] + values[9] ;
				document.getElementById('weeklychangescount').innerHTML = values[1] + values[4] + values[7] + values[10] ;
				document.getElementById('monthlychangescount').innerHTML = values[2] + values[5] + values[8] + values[11] ; */
			}

		})


}

function checkslideinput()	
{
	var checkboxes = document.getElementsByName("projectlist");
	var checked = false;
	for(let i=0;i<checkboxes.length;i++)
		{
			if(checkboxes[i].checked)checked = true;
		}
	if(!checked)alert("Please select a project for Slideshow");
	return checked;
}

</script>


 <script type="text/javascript">
$( document ).ready(function() {

	var val=$('#projectid').val();

	$('.carousel').carousel('pause'); 
	$('.carousel-item').removeClass('active');
	
	$('#Mil'+val).addClass('active');
	$('#Meeting'+val).addClass('active');
	$('#chart'+val).addClass('active');
	$('#act'+val).addClass('active');
	$('#projectname'+val).addClass('active');
	$('#schedule').addClass('active');
	$('#notice').addClass('active');
	$('#actions').addClass('active');
	$('.vert').carousel('cycle'); 
	$('#projectinfo'+val).addClass('active');
	$('#projectdetailsname'+val).addClass('active'); 
});
function openModalDetails(a,b){
	var jsObjectList
	console.log(b + "--"+ typeof b)
	if(a==="M" && b>0){
		jsObjectList = JSON.parse('<%= jsonArray %>');
	}
	console.log(jsObjectList)
	const dateFromTimestamp = new Date(1703097000000);

	var html="";
	for(var i=0;i<jsObjectList.length;i++){
		html=html+'<p class="font-weight-bold">'+"Project:"+jsObjectList[i][8]+"; Meeting:"+jsObjectList[i][7]+"; Time:"+jsObjectList[i][4]+';</p>'
	}
	document.getElementById('modalcontents').innerHTML=html;
	if(jsObjectList.length>0){
		$('#ModalDetails').show();
		   $( document ).ready(function() {
	    	   setTimeout(() => { 
	    		   closeModals()
			}, 3000);
	    	});
	}
}
function closeModals(){
	$('#ModalDetails').hide();
}

// Functions to open and close the modal
function openModal() {
/*   document.getElementById('myModal').style.display = 'block';
  document.getElementById('modalbtn').style.display = 'none'; */
	$('#myModal').show();
 	$('#modalbtn').hide();
    setTimeout(() => { 
		   closeModal()
	}, 5000);
    $('#brifingBtn').hide();
}

function closeModal() {
/*   document.getElementById('myModal').style.display = 'none';
 */  $('#myModal').hide();
 	$('#modalbtn').show();
 /*  document.getElementById('modalbtn').style.display = 'block'; */
 	 $('#brifingBtn').show();
}

// Close the modal if the user clicks outside of it
window.onclick = function(event) {
  var modal = document.getElementById('myModal');
  if (event.target === modal) {
    modal.style.display = 'none';
  }
}

//clicked the modal 
    document.addEventListener('DOMContentLoaded', function() {
    	openModal();
      });
      
    $( document ).ready(function() {
    	   setTimeout(() => { 
    		   closeModal()
		}, 4500);
    	});
</script> 

</body>



</html> 