<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"
	import="java.util.*,com.vts.*,java.text.SimpleDateFormat"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/action/committeeScheduleActions.css" var="committeeScheduleActions" />
<link href="${committeeScheduleActions}" rel="stylesheet" />
<spring:url value="/resources/css/action/actionCommon.css" var="actionCommon" />
<link href="${actionCommon}" rel="stylesheet" />

<title>COMMITTEE SCHEDULE ACTION</title>

</head>


<body>

	<%


String specname=(String)request.getAttribute("specname");
SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
Object[] committeescheduleeditdata=(Object[])request.getAttribute("committeescheduleeditdata");
List<Object[]> committeescheduledata=(List<Object[]>)request.getAttribute("committeescheduledata");
List<Object[]> EmpList=(List<Object[]>)request.getAttribute("EmployeeList");
List<Object[]> EmpNameList=(List<Object[]>)request.getAttribute("EmpNameList");
String labcode = (String)request.getAttribute("labcode");
List<Object[]> Alllablist = (List<Object[]>)request.getAttribute("AllLabList");
String projectid=committeescheduleeditdata!=null?committeescheduleeditdata[9].toString():"";
List<Object[]> divisionList = (List<Object[]>) request.getAttribute("divisionList");
//labcode = "PGAD";
String GenId="GenAdd";
String MinutesBack=null;
MinutesBack=(String)request.getAttribute("minutesback");
if(MinutesBack==null){
	MinutesBack="NO";
}
List<String>committees=Arrays.asList("PMRC","EB");

//Prudhvi - 13/03/2024
String rodflag=(String)request.getAttribute("rodflag");

String ccmFlag = (String) request.getAttribute("ccmFlag");
String committeeMainId = (String) request.getAttribute("committeeMainId");
String committeeId = (String) request.getAttribute("committeeId");

String dmcFlag = (String) request.getAttribute("dmcFlag");

String nonproject = (String) request.getAttribute("nonproject");
String programmeId = (String) request.getAttribute("programmeId");
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


	<nav class="navbar navbar-light bg-light" >
		<a class="navbar-brand"> <b
			class="b-styl"><span
				class="span1"><%=committeescheduleeditdata!=null?StringEscapeUtils.escapeHtml4(committeescheduleeditdata[7].toString()):"" %> </span> <span
				class="span2"> (Meeting Date and
					Time : <%=committeescheduleeditdata!=null?sdf.format(sdf1.parse(committeescheduleeditdata[2].toString())):""%>
					- <%=committeescheduleeditdata!=null?StringEscapeUtils.escapeHtml4(committeescheduleeditdata[3].toString()):"" %>)
			</span></b>

		</a>

		<%if(MinutesBack.equalsIgnoreCase("minutesback")){ %>
		<!-- Prudhvi - 13/03/2024 -->
		<a class="btn  btn-sm back custom-a"
		    <%if(rodflag!=null && rodflag.equalsIgnoreCase("Y")) {%>
		    	href="RODScheduleMinutes.htm?committeescheduleid=<%=committeescheduleeditdata!=null?committeescheduleeditdata[6]:"" %>"
		    <%} else if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {%>	
		    	href="CommitteeScheduleMinutes.htm?committeescheduleid=<%=committeescheduleeditdata!=null?committeescheduleeditdata[6]:"" %>&ccmFlag=Y&committeeMainId=<%=committeeMainId %>&committeeId=<%=committeeId %>"
		    <%} else if(dmcFlag!=null && dmcFlag.equalsIgnoreCase("Y")) {%>	
		    	href="CommitteeScheduleMinutes.htm?committeescheduleid=<%=committeescheduleeditdata!=null?committeescheduleeditdata[6]:"" %>&dmcFlag=Y&committeeId=<%=committeeId %>"
		    <%} else{%>
				href="CommitteeScheduleMinutes.htm?committeescheduleid=<%=committeescheduleeditdata!=null?committeescheduleeditdata[6]:"" %>"
			<%} %>
		>BACK</a>

		<%} %>

		<%if(!MinutesBack.equalsIgnoreCase("minutesback")){ %>

		<a class="btn  btn-sm back custom-a" href="ActionList.htm" >BACK</a>

		<%} %>

	</nav>


	<div class="container-fluid">

		<div class="row">

			<div class="col-md-5">
				<div class="card card-border" >
					<div  <%if(committeescheduledata!=null && committeescheduledata.size()>5) {%>class="card-body if-true"<%} else{%>class="card-body if-false"<%} %>>
				<div>
						<% 
						String prev=""; // for holding the point number
						String next=""; // to store the prev point number
						int count=1;
						for(Object[] obj:committeescheduledata ){  
							prev=obj[7].toString();
							if(!prev.equalsIgnoreCase(next)){ // if prev and next is not equal make count 1
								count=1;
							}
							
							%>

						<div class="panel panel-info margin-top10" >

							<div class="panel-heading " id="div<%=obj[0].toString()%>">
								<h4 class="panel-title" id="<%=obj[0].toString()%>">
									<span class="font-size14">
										<%=(ccmFlag!=null&&ccmFlag.equalsIgnoreCase("Y")?"3":(dmcFlag!=null&&dmcFlag.equalsIgnoreCase("Y")?"1":obj[7]!=null?StringEscapeUtils.escapeHtml4(obj[7].toString()):" - ") )+"."+(count++) +". "%> <%-- <%=obj[4] %> --%>  
									<input type="hidden" id="Data<%=obj[0].toString()%>" value="<%=obj[1].toString()%>">
									<span class="font-size14">
											<%
											String content = obj[1] != null ? obj[1].toString() : " - ";
											content = content.replaceAll("(?i)</?(p|div|strong)[^>]*>", "");
											%>											
											<%= content.length() > 50 ? content.substring(0,50) : content %>
											
											<% if(content.length() > 45) { %>
											<span class="cursor-pointer"
											      onclick='showModal("<%=obj[0].toString()%>")'>
											   &nbsp;(view more)
											</span>
											<%} %>
										</span>

										<br>
										<%if("3".equalsIgnoreCase(obj[7].toString())){%><%-- / <%=obj[5] %> / --%>
									<%-- 	<%=obj[6] %> --%> <%} %> 
										 (<%if("K".equalsIgnoreCase(obj[2].toString())){%>Risk Task<%} %> 
										 <%if("I".equalsIgnoreCase(obj[2].toString())){%>Issue Task<%} %> 
										 <%if("A".equalsIgnoreCase(obj[2].toString())){%>Action Task<%} %>
										 <%if("R".equalsIgnoreCase(obj[2].toString())){%>Recommendation Task<%} %> )
									</span>
								</h4>
								<div class="div-negative">
									<form name="myForm<%=obj[0] %>" id="myFormgen<%=obj[0] %>" action="ItemSpecAdd.htm" method="post">
										<input type="hidden" name="specname" value="myFormgen<%=obj[0] %>"> 
										<input type="hidden" name="ScheduleId" value="<%=obj[3] %>"> 
										<input type="hidden" name="ProjectId" value="<%=obj[10] %>">
										<input type="hidden" name="scheduleminutesid" value="<%=obj[0] %>" readonly="readonly"> 
										<input type="hidden" name="minutesid" value="<%=obj[6] %>" readonly="readonly"> 
										<input type="hidden" name="agendasubid" value="<%=obj[9] %>" readonly="readonly"> 
										<input type="hidden" name="scheduleagendaid" value="<%=obj[7] %>" readonly="readonly"> 
										<input type="hidden" name="unitid" value="<%=obj[8] %>" readonly="readonly">
										<input type="hidden" name="type" value="<%=obj[2] %>" readonly="readonly"> 
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
										<input type="submit" name="sub" <%if(!obj[11].toString().equalsIgnoreCase("0")){ %>
											class="btn btn-warning btn-sm width50" <%}else{ %>
											class="btn btn-info btn-sm width50"
											<%}if(specname!=null&&specname.equalsIgnoreCase("myFormgen"+obj[0])){ %>
											id="GenAdd" <%} %> <%if(specname==null){ %> id="GenAdd"
											<%} %> onclick="FormName('myFormgen<%=obj[0] %>')"
											value="Assign"
										 />
									</form>
								</div>
							</div>
						</div>
						<%next=prev;}  // assign the previous value to next one%> 
					</div>
					</div>
					<!-- Big card-body end -->

				</div>
				<!-- Card End  -->

			</div>
			<!-- col-md-5 end -->
			<div class="col-md-7" >

				<div class="card margin2" >
					<div class="card-header card-color" >
						<div class="row">

							<div class="col-sm-5" align="left">
								<h6 class="custom-h6"></h6>
							</div>

							<div class="col-sm-7" align="left">
								<div class="input-group input-gr" >
									<input type="text" class="form-control" placeholder="Search Action Id to Link Old Action" name="ItemDescription" id="ItemDescriptionSearch">
									<div class="input-group-append">
										<button class="btn btn-secondary font-size10" type="button"  id="ItemDescriptionSearchBtn">
											<i class="fa fa-search"></i>
										</button>
									</div>
								</div>
								</div>
						</div>
					</div>
					<div class="card-body" >
						<form name="specadd" id="specadd" action="CommitteeActionSubmit.htm" method="post" >
							<div class="row margin-top30" >
								<div class="col-md-12" align="left">
									<label> <b id="iditemspec" class="font-size18"></b>
										<b id="iditemsubspecofsub" class="font-size18"></b> <b
										id="iditemsubspec" class="font-size18"></b><b
										id="iditemsubspecofsubspec" class="font-size18"></b> <b
										id="action" class="font-size18"></b> <input type="hidden"
										name="scheduleminutesid" id="minutesidadd"> <input
										type="hidden" name="ScheduleId" id="ScheduleAdd"> <input
										type="hidden" name="ProjectId" id="ProjectAdd"> <input
										type="hidden" name="Type" id="TypeAdd"> <input
										type="hidden" name="ScheduleSpec" id="ScheduleSpec"> <input
										type="hidden" name="specname" id="specnameadd"> <input
										type="hidden" name="minutesback" value="<%=MinutesBack %>">
										<input type="hidden" name="nonproject" value="<%= (nonproject != null) ? nonproject : "N" %>" />
										<input type="hidden" name="programmeId" value="<%= (programmeId!=null) ? programmeId : "0" %>" />
										<%if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {%>
											<input type="hidden" name="ccmScheduleId" value="<%=committeescheduleeditdata!=null?committeescheduleeditdata[6]:"" %>">
											<input type="hidden" name="committeeMainId" value="<%=committeeMainId %>">
											<input type="hidden" name="committeeId" value="<%=committeeId %>">
											<input type="hidden" name="ccmFlag" value="<%=ccmFlag %>">
										<%} %>
										<%if(dmcFlag!=null && dmcFlag.equalsIgnoreCase("Y")) {%>
											<input type="hidden" name="committeeId" value="<%=committeeId %>">
											<input type="hidden" name="dmcFlag" value="<%=dmcFlag %>">
										<%} %>
									</label>
								</div>

								<div class="col-sm-10" align="left">
									<div class="form-group" class="text-justify">
										<label>Action Item: <span class="mandatory"
											>*</span>
										</label><br>
										<b id="iditem" class="font-size18"></b> 
										<input type="hidden" name="Item" id="additem" class="width-100" maxlength="1000">
									</div>
								</div>
								<div class="col-md-1" align="left"></div>

								<div class="col-sm-3" align="left">
									<div class="form-group">
										<label>PDC: <span class="mandatory"
											>*</span></label> 
											<input class="form-control " name="DateCompletion" id="DateCompletion" required="required" placeholder=""> 
											<input type="hidden" name="meetingdate" value="<%=committeescheduleeditdata!=null?committeescheduleeditdata[2]:""%>">
									</div>
								</div>
								<div class="col-sm-4" align="left">
									<label> Priority : </label> <br> 
									<select class="form-control selectdee " name="Priority" id="Priority" required="required" data-live-search="true">
										<option value="H">High</option>
										<option value="L">Low</option>
										<option value="M">Medium</option>
										<option value="I">Immediate</option>
									</select>
								</div>

								<div class="col-sm-4" align="left">
									<label> Category : </label> <br> 
									<select class="form-control selectdee " name="Category" id="Category" required="required" data-live-search="true">
										<option value="T">Technical</option>
										<option value="F">Finance</option>
										<option value="M">Managerial</option>
										<option value="L">Logistic</option>
										<option value="O">Others</option>
									</select>
								</div>

								<div class="col-sm-3" align="left">
									<div class="form-group">
										<label>Lab: <span class="mandatory"
											>* </span></label> <select
											class="form-control selectdee" name="AssigneeLabCode"
											id="AssigneeLabCode" onchange="AssigneeEmpList();">
											<%for(Object[] lab : Alllablist){%>
											<option value="<%=lab[3] %>"
												<%if(labcode.equalsIgnoreCase(lab[3].toString())){%>
												selected="selected" <%}%>><%=lab[3] !=null?StringEscapeUtils.escapeHtml4(lab[3].toString()):" - "%></option>
											<%}%>
											<option value="@EXP"
												<%if("@EXP".equalsIgnoreCase(labcode)){%>
												selected="selected" <%}%>>Expert</option>
											
										</select>
									</div>
								</div>

								<div class="col-sm-4" align="left">
									<div class="form-group">
										<label> Assignee : </label>
										<br> <select class="form-control selectdee"
											name="Assignee" id="Assignee"<%if(committeescheduleeditdata!=null && committees.contains(committeescheduleeditdata[8].toString())) {%> required="required" <%} %>
									onchange="removeAttri()"		data-live-search="true" data-placeholder="Select Assignee"
											multiple>


										</select>
									</div>
								</div>


								<%if(committeescheduleeditdata!=null && !committees.contains(committeescheduleeditdata[8].toString()) && rodflag==null && programmeId!=null && programmeId.equalsIgnoreCase("0") && !labcode.equalsIgnoreCase("PGAD")) {%>
								<div class="col-sm-4" align="left" id="main">
									<div class="form-group" >
										<label>Multiple Assignee : </label><br> 
										<select class="form-control"    onchange="showEmployee()" id="multipleAssignee" name="multipleAssignee" required="required">
										<option value="" selected="selected"> Select </option> 
										<option value="T">ALL TD</option>
										<option value="G">ALL GH</option>
										<option value="D">ALL DH</option>
										<option value="P">ALL PD</option>
										</select>
									</div>
								</div>
								<%} %>
								
								<% if(programmeId!=null && programmeId.equalsIgnoreCase("1") && labcode.equalsIgnoreCase("PGAD") && divisionList!=null && divisionList.size() > 0){ %>
									<div class="col-md-4" align="left" id="divisions">
										<div class="form-group">
											<label>Multiple Assignee :</label><br>
											<select class="form-control selectdee" onchange="showDivisionEmployee()" id="multipleDivisionAssignee" name="multipleDivisionAssignee">
												<option value="" selected="selected" disabled="disabled" >Select</option>
												<% for (Object[] obj : divisionList){ %>
													<option value="<%=obj[0] %>"> <%= obj[3]!=null ? StringEscapeUtils.escapeHtml4(obj[3].toString()): "" %> <%=obj[2]!=null ? "("+StringEscapeUtils.escapeHtml4(obj[2].toString()) + ")": "" %></option>
												<%} %>
											</select>
										</div>
									</div>
								<%} %>

							</div>
							<div class="row" align="center">
								<div class="col-sm-4" align="left"></div>
								<div class="col-sm-6" align="left">
									<br> <input type="submit" name="sub"
										class="btn  btn-sm submit margin-top10"
										form="specadd" id="adding" value="SUBMIT"
										onclick="return confirm('Are you sure To Submit?')" />
									<button class="btn  btn-sm back margin-top10" 
										onclick="resetSubmit()">Reset</button>
									<input type="hidden" name="${_csrf.parameterName}"
										value="${_csrf.token}" />
									<!-- Prudhvi - 13/03/2024 -->
									<%if(rodflag!=null) {%>
										<input type="hidden" name="rodflag" value="Y">	
									<%} %>
								</div>
							</div>
							<!-- Form End -->

							<div class="table-responsive">
								<table class="table table-bordered table-hover table-striped table-condensed margin-top20 width-98" id="myTabl" >
									<thead>
										<tr>
											<th class="text-left">Action Item</th>
											<th >PDC</th>
											<th >Assigned On</th>
											<th >Assignee</th>
											<th >Action</th>
										</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
							</div>
                           <input type="hidden" id="scheduleMinId" value="">
						</form>
					</div>

				</div>

			</div>
			<!-- col-md-7 end -->


		</div>
		<!-- main row end -->

	</div>
	
	<form action="CommitteActionDelete.htm" method="post" id="deleteFormId">
		<input type="hidden" name="actionAssignPkId" id="actionAssignPkId" value=""> 
		<input type="hidden" name="actionMainPkId" id="actionMainPkId" value="" /> 
		<input type="hidden" name="assigneeEmpId" id="assigneeEmpId" value="" /> 
		<input type="hidden" name="committeeSchId" id="committeeSchId" value="" /> 	
		<input type="hidden" name="minutesback" value="<%=MinutesBack%>" /> 
		<input type="hidden" name="specValueId" id="specValueId" value="">
		<input type="hidden" name="nonproject" value="<%= (nonproject != null) ? nonproject : "N" %>" />
		<input type="hidden" name="programmeId" value="<%= (programmeId!=null) ? programmeId : "0" %>" />
		<%if(rodflag!=null) {%>
			<input type="hidden" name="rodflag" value="Y">	
		<%} %>
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	</form>
	
	<!--Modal for employees  -->
<div class="modal fade bd-example-modal-lg" id="employeeModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content" >
      <div class="modal-header modal-bg" >
        <h5 class="modal-title" id="modalHeader" >Modal title</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body" >
      <div class="custom-div">
        <table class="table table-bordered width-80%">
  		<thead class="bg-light">
    	<tr>
   		<th class="width-12">SL No.</th>
   		<th class="text-center">Employee</th>
    	</tr>
  		</thead>
  		<tbody id="modalTable">
   
    	</tbody>
        </table>
     </div>
      </div>
    
    </div>
  </div>
</div>


<!-- modal for action item -->

<div class="modal fade" id="actionModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="exampleModalLongTitle">Action Item</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body fontrem" id="modalbody" >
      
      </div>

    </div>
  </div>
</div>

<!-- modal for action edit start -->

	<div class="modal fade" id="exampleModalAction" tabindex="-1"
		role="dialog" aria-labelledby="exampleModalCenterTitle"
		aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered" role="document">
			<div class="modal-content md-cnt" >
				<div class="modal-header">
					<h5 class="modal-title" id="exampleModalLongTitle"></h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<form action="CommitteActionEdit.htm" method="post">
						<div class="row">
							<div class="col-md-3">
								<label
									class="cstm-label">PDC
									:</label>
							</div>
							<div class="col-md-4 margin-left" >
								<input class="form-control width261" name="PDCDate" id="PDCDate">
							</div>
						</div>

						<div class="row">
							<div class="col-md-3 mt-3">
								<div class="form-group">
									<label
										class="cstm-label">Lab
										:</label>
								</div>
								</div>
								<div class="col-md-8 mt-3 margin-left" >
									<select class="form-control selectdee width118" name="AssigneeLab" onchange="labChange()"
										id="AssigneeLabName" >
										
									</select>
								</div>
							</div>

						<div class="row mt-2">
							<div class="col-md-3">
								<label
									class="cstm-label">
									Assignee : </label>
							</div>
							<div class="col-md-8 margin-left" >
								<select class="form-control selectdee width118" name="AssigneeId"
									id="AssigneeUpdate" data-live-search="true">

								</select>
							</div>
						</div>
						<br>
						<div align="center">
							<button type="submit" class="btn btn-sm submit" onclick="return confirm('Are you sure To Submit?')">Submit</button>
							<input type="hidden" name="ActionAssignId" id="ActionAssignId" value=""> 
							<input type="hidden" name="ActionMainId" id="ActionMainId" value="" /> 
							<input type="hidden" name="AssigneeId" id="AssigneeId" value="" /> 
							<input type="hidden" name="CommitteeScheduleId" id="CommitteeScheduleId" value="" /> 
							<input type="hidden" name="minutesback" value="<%=MinutesBack%>" /> 
							<input type="hidden" name="specnamevalue" id="specValue" value="">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							<input type="hidden" name="nonproject" value="<%= (nonproject != null) ? nonproject : "N" %>" />
							<input type="hidden" name="programmeId" value="<%= (programmeId!=null) ? programmeId : "0" %>" />
							<%if(rodflag!=null) {%>
								<input type="hidden" name="rodflag" value="Y">	
							<%} %>
							<%if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {%>
								<input type="hidden" name="ccmScheduleId" value="<%=committeescheduleeditdata!=null?committeescheduleeditdata[6]:"" %>">
								<input type="hidden" name="committeeMainId" value="<%=committeeMainId %>">
								<input type="hidden" name="committeeId" value="<%=committeeId %>">
								<input type="hidden" name="ccmFlag" value="<%=ccmFlag %>">
							<%} %>
							<%if(dmcFlag!=null && dmcFlag.equalsIgnoreCase("Y")) {%>
								<input type="hidden" name="committeeId" value="<%=committeeId %>">
								<input type="hidden" name="dmcFlag" value="<%=dmcFlag %>">
							<%} %>	
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
	
<!-- modal for action edit start -->

	<script type="text/javascript">
		function changeempdd() {
			var $labcode = $('#AssigneeLabCode').val();

			if (document.getElementById('allempcheckbox').checked) {
				employeefetch(0, $labcode);
			} else {
				employeefetch(
	<%=committeescheduleeditdata!=null?committeescheduleeditdata[9]:""%> , $labcode);
  }
}

	
	function employeefetch(ProID,labcode){
			
				
						$.ajax({		
							type : "GET",
							url : "ProjectEmpListFetch.htm",
							data : {
								projectid : ProID,
								labCode   : labcode
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
											+values[i][1] + " (" +values[i][2]+")" 
											+ '</option>';
								} 
								 
								$('#Assignee').html(s);
								
							}
						});
		
		
	}
		
</script>


	<script>
  
    $(document).ready(function(){
        // Add minus icon for collapse element which is open by default
        $(".collapse.show").each(function(){
        	$(this).prev(".panel-heading").find(".fa").addClass("fa-minus").removeClass("fa-plus");
        });
        
        // Toggle plus minus icon on show hide of collapse element
        $(".collapse").on('show.bs.collapse', function(){
        	$(this).prev(".panel-heading").find(".fa").removeClass("fa-plus").addClass("fa-minus");
        }).on('hide.bs.collapse', function(){
        	$(this).prev(".panel-heading").find(".fa").removeClass("fa-minus").addClass("fa-plus");
        });
    });
    
    $(document).ready(function(){
        // Add minus icon for collapse element which is open by default
        $(".collapse.show").each(function(){
        	$(this).prev(".panel-heading").find(".btn").addClass("btn-danger btn-sm").removeClass("btn-info btn-sm");
        });
        
        // Toggle plus minus icon on show hide of collapse element
        $(".collapse").on('show.bs.collapse', function(){
        	$(this).prev(".panel-heading").find(".btn").removeClass("btn-info btn-sm").addClass("btn-danger btn-sm");
        }).on('hide.bs.collapse', function(){
        	$(this).prev(".panel-heading").find(".btn").removeClass("btn-danger btn-sm").addClass("btn-info btn-sm");
        });
    });
   
    

</script>


	<script type="text/javascript">
	 
	
	$("#OldList").hide();

	
	$("#ItemDescriptionSearchBtn").click(function(){
		   $('#OldActionNoId').empty();
		var $ItemSearch = $("#ItemDescriptionSearch").val();
		  $("#loader ").show(); 
		
		
		
		  if ($ItemSearch === ""){
			 alert("Search Content Requried");
			 $("#loader ").hide();
		  }else{
					  
			  $
				.ajax({

					type : "GET",
					url : "ActionNoSearch.htm",
					data : {
						ActionSearch : $ItemSearch
					},
					datatype : 'json',
					success : function(result) {

						var result = JSON.parse(result);
						var values = Object.values(result).map(function(key, value) {
							  return result[key,value]
							});
						var size = Object.keys(values).length;
						var s = '';
						 $("#OldList").show(); 
					     $("#OldActionNoId").prop("disable",false);
					    $("#OldActionNoId").empty();
					     $.each(values, function(key, value) {   
					         $('#OldActionNoId')   
					             .append($("<option></option>")
					                        .attr("value", value[0])
					                        .text(value[1]+", "+value[2])); 
					    });
					    
						$("#loader ").hide(); 
					}
				}); 
			  
			  
			  
		  }
		}); 
	
	
	function resetSubmit(){
		event.preventDefault();
		 $("#OldList").hide(); 
	     $("#OldActionNoId").prop("disable",true);

	}
	


    function FormName(formId) {
    	var data = formId.substring(9);
    	$("."+"panel-title").css('color', '#31708f');
    	$("."+"panel-title").css('text-decoration', 'none');
    	
    	$('#'+data).css('color', 'green');
    	$('#'+data).css('text-decoration', 'underline');
    
    	console.log("data "+data)
    	$('#scheduleMinId').val(data);
    	
    	  $("#"+formId).submit(function(event){
    		    event.preventDefault();
    		    $('#adding').show();
    		   
    		    var minutesidadd = $("input[name='minutesid']",this).val();
    		    var specnameadd= $("input[name='specname']",this).val();
    		    var agendasubidadd= $("input[name='agendasubid']",this).val();
    		    var scheduleagendaidadd= $("input[name='scheduleagendaid']",this).val();
    		    var scheduleminutesidadd = $("input[name='scheduleminutesid']",this).val();
    		    var scheduleid= $("input[name='ScheduleId']",this).val();
    		    var projectid= $("input[name='ProjectId']",this).val();
    		    var unitidadd= $("input[name='unitid']",this).val();
    		    var typeadd= $("input[name='type']",this).val();
    		    $("#minutesidadd").val(scheduleminutesidadd);
    		    $("#specnameadd").val(specnameadd);
    		    $("#ScheduleAdd").val(scheduleid);
    		    $("#ProjectAdd").val(projectid);
    		    $("#TypeAdd").val(typeadd);
    		    var specall;
    		    var type;
    		    if(typeadd=='A'){
    		    	type="(Action Task)";
    		    }else if(typeadd=='I'){
    		    	type="(Issue Task)";
    		    }else{
    		    	type="(Risk Task)";
    		    }
    		    
    		    $.ajax({

    		    	type : "GET",
    		    	url : "ScheduleActionItem.htm",
    		    	data : {
    		    		ScheduleMinutesId : scheduleminutesidadd
    		    		
    		    	},
    		    	datatype : 'json',
    		    	success : function(result) {

    		    		var result = JSON.parse(result);
    		    		var values = Object.keys(result).map(function(e) {
    		    			  return result[e]
    		    			});
    		    	
    		    		
    		    		$("#iditem").html(result);
						$("#additem").val(result);
						$("#ScheduleSpec").val(result);
    		    	}
    		    });
    		    
    		    $.ajax({

    		    	type : "GET",
    		    	url : "ScheduleActionList.htm",
    		    	data : {
    		    		ScheduleMinutesId : scheduleminutesidadd
    		    		
    		    	},
    		    	datatype : 'json',
    		    	success : function(result) {

    		    		var result = JSON.parse(result);
    		    		var values = Object.keys(result).map(function(e) {
    		    			  return result[e]
    		    			});
    		    		
    		    		
    		    		var s = '';
    		    		
    		    		$("#myTabl tbody tr").hide();
    		    		$("#myTabl thead tr").show(); 
    		    		
    		    		$("#myTabl").DataTable().destroy(); 
    		    		$("#myTabl tbody").html('');  
    		    		var markup = '';
    		    		if(values.length!=0)
    		    		{
    		    			
	    		    		for (i = 0; i < values.length; i++) 
	    		    		{
	    		    			var tempday = moment(JSON.stringify(values[i][3]), "MMM-DD-YYYY");
	    		    			var formatday= moment(tempday).format("DD-MM-YYYY");
	    		    			var tempday1 = moment(JSON.stringify(values[i][4]), "MMM-DD-YYYY");
	    		    			var formatday1= moment(tempday1).format("DD-MM-YYYY");
	    		    		    var tempday2 = moment(JSON.stringify(values[i][10]), "MMM-DD-YYYY");
	    		    			var pdcday= moment(tempday2).format("DD-MM-YYYY");
	    		    			   
	    		    			markup += "<tr><td  class='script-td'> "+  values[i][5]+"<br>"+"<b>(" + values[i][9] + ")</b>"  + "</td><td class='width-15'> "+  formatday1  + "</td><td class='width-15'> "+  formatday  + "</td><td class='width-20'> "+  values[i][1] +', '+values[i][2] +'('+values[i][8] +')' + "</td>";
	    		    		    markup += "<td class='width-13 text-center'>";
	    		    			if (values[i][6]==="A") {
	    		    			    markup += "<button class='btn btn-sm' type='button' onclick=\"actionEditform('" + values[i][9] + "','" + pdcday + "','" + values[i][0] + "','" + values[i][11] + "','" + values[i][12] + "','" + values[i][13] + "','" + values[i][8] + "','" + values[i][14] + "')\"><i class='fa fa-pencil-square-o icon-col' aria-hidden='true' ></i></button>";
	    		    			    markup += "<button class='btn btn-sm' type='button' onclick=\"actionDelete('" + values[i][0] + "','" + values[i][12] + "','" + values[i][13] + "')\"><i class='fa fa-trash icon-col2' aria-hidden='true' ></i></button>";
	    		    			} else {
	    		    				 markup += "--";
                                }
	    		    			markup += "</td></tr>";
	    		    		}
	    		    		
    		    		}
    		    		
    		    		$("#myTabl tbody").html(markup); 
    		    		$("#myTabl").DataTable({
    		    			"lengthMenu": [  5,10,25, 50, 75, 100 ],
    		    			"pagingType": "simple"
    		    		});
    		    	}
    		    });
    		    
    		  }); 
    	 
    	  AssigneeEmpList();
    }  
    
	$('#DateCompletion').daterangepicker({
		"singleDatePicker" : true,
		"linkedCalendars" : false,
		"showCustomRangeLabel" : true,
		/* "minDate" : new Date(), */
		"cancelClass" : "btn-default",
		showDropdowns : true,
		locale : {
			format : 'DD-MM-YYYY'
		}
	});

	
</script>

<script>

function showDivisionEmployee(){
	var divisionId = $('#multipleDivisionAssignee').val();
	$.ajax({
		  type:'GET',
		  url:'getDivisionEmployees.htm',
		  datatype:'json',
		  data:{
			  divisionId:divisionId,
		  },
		  success:function(result){
			  var ajaxresult=JSON.parse(result);
			  console.log(ajaxresult.length);
			  if(ajaxresult.length>0){
				  $('#modalHeader').text(" Division Employees");
				  $('#employeeModal').modal('show');
				  var html="";
				  for(var i=0;i<ajaxresult.length;i++){
					 if(ajaxresult[i][1]!=null && ajaxresult[i][2]!=null) html=html+"<tr><td class='text-center'>"+(i+1)+"</td><td>"+ajaxresult[i][1] +", " + ajaxresult[i][2]+"</td></tr>"; 
				  }
				  if(html.length===0){
					  html = "<tr><td class='text-center' colspan='2'>No Data Available</td></tr>"
				  }
				  $('#modalTable').html(html);
			  }
		  }
	  })
}

</script>

<script>

 var genid="<%=GenId%>";
	$(document).ready(function(){
	$("#"+genid).click();

	});   

$(document).ready(function(){
	 AssigneeEmpList();
}); 	


function AssigneeEmpList(){
	
	var empIdsToRemove = [];
	var scheduleMinId = $('#scheduleMinId').val();
	
	$('#Assignee').val("");
	
	var $AssigneeLabCode = $('#AssigneeLabCode').val();
	var mainlabcode = "<%=labcode.toString()%>" ;
	
	let projectid = <%=committeescheduleeditdata!=null && committeescheduleeditdata[9]!=null?StringEscapeUtils.escapeHtml4(committeescheduleeditdata[9].toString()):""%> ;
	
	var div = document.getElementById("main");
	if(mainlabcode===$AssigneeLabCode){
		if(div!=null){
		div.style.display = "block";
		}
		}else{
		if(div!=null){
		div.style.display = "none";
		}
	}
	
    $.ajax({
    	type : "GET",
    	url : "ScheduleActionList.htm",
    	data : {
    		ScheduleMinutesId : scheduleMinId
    	},
    	datatype : 'json',
    	success : function(result) {

   		var result = JSON.parse(result);
   		var values = Object.keys(result).map(function(e) {
   			  return result[e]
   			});

		  if (values.length != 0) {
	            for (var i = 0; i < values.length; i++) {
	                if (values[i][11]) {
	                    empIdsToRemove.push(values[i][11]);
	                }
	            }
	        }
		  fetchAssigneeOptions(empIdsToRemove,$AssigneeLabCode);
    	}
    });
}

function fetchAssigneeOptions(empIdsToRemove,$AssigneeLabCode,projectid) {
	if($AssigneeLabCode!=""){
		
		$.ajax({
			
			type : "GET",
			url : "ActionAssigneeEmployeeList.htm",
			data : {
				LabCode : $AssigneeLabCode,
				proid    : projectid
			},
			datatype : 'json',
			success : function(result) {
				var result = JSON.parse(result);
				var values = Object.keys(result).map(function(e) {
					return result[e]
					});
				
				var s = '';
				s += '<option value="">Choose ...</option>';
				if($AssigneeLabCode == '@EXP'){
					
				}
				 for (var i = 0; i < values.length; i++) {
		                var empid = values[i][0]; 
		                var empName = values[i][1]; 
		                var desig = values[i][3];

		                if (!empIdsToRemove.includes(empid)) {
		                    s += '<option value="' + empid + '">' + empName.replaceAll("<","").replaceAll(">","").replaceAll("/","") + ', ' + desig.replaceAll("<","").replaceAll(">","").replaceAll("/","") + '</option>';
		                }
		            }
				
				$('#Assignee').html(s);

			}
		});
	 }
   }

function showEmployee(){
	   var value=$('#multipleAssignee').val();
	  $.ajax({
		  type:'GET',
		  url:'getEmployees.htm',
		  datatype:'json',
		  data:{
			  flag:value,
		  },
		  success:function(result){
			  var ajaxresult=JSON.parse(result);
			  console.log(ajaxresult);
			  if(value==="P"){
				  $('#modalHeader').text(" PROJECT DIRECTORS");
				  $('#employeeModal').modal('show');
				  var html="";
				  for(var i=0;i<ajaxresult.length;i++){
					 html=html+"<tr><td class='text-center'>"+(i+1)+"</td><td>"+ajaxresult[i][1]+"</td></tr>"; 
				  }
				  $('#modalTable').html(html);
			  }
 			if(value==="D"){
 				$('#modalHeader').text(" DIVISION HEADS"); 
 				$('#employeeModal').modal('show');
 				  var html="";
				  for(var i=0;i<ajaxresult.length;i++){
					 html=html+"<tr><td class='text-center'>"+(i+1)+"</td><td>"+ajaxresult[i][1]+"</td></tr>"; 
				  }
				  $('#modalTable').html(html);
			  }
 			 if(value==="T"){
 				$('#modalHeader').text(" TECHNICAL DIRECTORS");
 				$('#employeeModal').modal('show');
 				  var html="";
				  for(var i=0;i<ajaxresult.length;i++){
					 html=html+"<tr><td class='text-center'>"+(i+1)+"</td><td>"+ajaxresult[i][1]+"</td></tr>"; 
				  }
				  $('#modalTable').html(html);
			  }
 			 if(value==="G"){
 				$('#modalHeader').text(" GROUP HEADS");
 				$('#employeeModal').modal('show');
 				  var html="";
				  for(var i=0;i<ajaxresult.length;i++){
					 html=html+"<tr><td class='text-center'>"+(i+1)+"</td><td>"+ajaxresult[i][1]+"</td></tr>"; 
				  }
				  $('#modalTable').html(html);
			  }
		  }
	  })
   }
   
   function removeAttri(){
	    var selectElement = document.getElementById("multipleAssignee");
	    selectElement.removeAttribute("required");
	    var selectElements = document.getElementById("Assignee");
	    /* console.log(selectElements.value); */
	    if(selectElements.value.length==0){
	    	selectElement.setAttribute('required', 'required');
	    }
   }
 function showModal(a){
	 var value= document.getElementById("Data"+a).value;
	 console.log(a);
	 $('#actionModal').modal('show');
	 document.getElementById('modalbody').innerHTML=value;
 }
 
 function actionEditform(actionno,pdc,mainid,assigneeId,actionAssignId,scheduleId,assigneeLab,projectid){
	 
		$('#exampleModalAction').modal('show');   
		$('#exampleModalAction .modal-title').html('<span class="span-col">Action No : '+ actionno);
	    $('#PDCDate').val(pdc);
	    $('#ActionMainId').val(mainid);
	    $('#ActionAssignId').val(actionAssignId);
	    $('#CommitteeScheduleId').val(scheduleId);
	    
	    var specNameAdd= $('#specnameadd').val();
	    $('#specValue').val(specNameAdd);
	    
	    //all lab list
	    $('#AssigneeLabName').empty();
	    <%for(Object[] obj2:Alllablist){%>
        var optionValue = '<%=obj2[3]!=null?StringEscapeUtils.escapeHtml4(obj2[3].toString()):""%>';
	    var optionText = '<%=obj2[3]!=null?StringEscapeUtils.escapeHtml4(obj2[3].toString()):" - "%>';
        var option = $("<option></option>").attr("value", optionValue).text(optionText.replaceAll("<","").replaceAll(">","").replaceAll("/",""));
        if (assigneeLab == optionValue) {
            option.prop('selected', true);
        }
      $('#AssigneeLabName').append(option);
      <% }%>
      
      var expertOptionValue = "@EXP";
      var expertOptionText = "Expert";
      var expertOption = $("<option></option>").attr("value", expertOptionValue).text(expertOptionText);
      if (assigneeLab == 'Expert') {
          expertOption.prop('selected', true);
      }
      $('#AssigneeLabName').append(expertOption);
      
      //pdc date 
      $('#PDCDate').daterangepicker({
  		"singleDatePicker" : true,
  		"linkedCalendars" : false,
  		"showCustomRangeLabel" : true,
  		/* "minDate" : new Date(), */
  		"startDate":pdc, 
  		"cancelClass" : "btn-default",
  		showDropdowns : true,
  		locale : {
  			format : 'DD-MM-YYYY'
  		}
  	});
      
      //exsiting employee name to be selected
      $.ajax({
          type: "GET",
          url: "ActionAssigneeEmployeeList.htm",
          data: {
          	LabCode: assigneeLab,  
              proid: projectid
          },
          dataType: 'json',
          success: function(result) {
          	 var values = Object.values(result);
               var options = '';
               for (var i = 0; i < values.length; i++) {
                   var optionValue = values[i][0];
                   var optionText = values[i][1] + ', ' + values[i][3];
                   var isSelected = (assigneeId == optionValue) ? 'selected' : '';
                   options += '<option value="' + optionValue + '" ' + isSelected + '>' + optionText.replaceAll("<","").replaceAll(">","").replaceAll("/","") + '</option>';
               }

               $('#AssigneeUpdate').html(options);
               
           },
          error: function(xhr, status, error) {
              console.error("Error:", error);
          }
      });
 }    
 
 // labcode change then their lab employyee will show
 function labChange(){
	 var labCode=$('#AssigneeLabName').val();
	 var projectid=0;
	 $('#AssigneeUpdate').empty();

     $.ajax({
         type: "GET",
         url: "ActionAssigneeEmployeeList.htm",
         data: {
         	LabCode: labCode,  
             proid: projectid
         },
         dataType: 'json',
         success: function(result) {
         	 var values = Object.values(result);
              var options = '';
              for (var i = 0; i < values.length; i++) {
                  var optionValue = values[i][0];
                  var optionText = values[i][1] + ', ' + values[i][3];
                  options += '<option value="' + optionValue + '">' + optionText.replaceAll("<","").replaceAll(">","").replaceAll("/","") + '</option>';
              }

              $('#AssigneeUpdate').html(options);
              
          },
         error: function(xhr, status, error) {
             console.error("Error:", error);
         }
     });
 }
 
 
 function actionDelete(mainid,actionassignid,scheduleId){
	 
	  var specNameAdd= $('#specnameadd').val();
	    $('#specValueId').val(specNameAdd);
	    $('#actionMainPkId').val(mainid);
	    $('#actionAssignPkId').val(actionassignid);
	    $('#committeeSchId').val(scheduleId);
        var formId = $('#deleteFormId');
        var message = 'Are you sure you want to delete..?';
        if (confirm(message)) {
            formId.submit();
        }
 };
 
</script>
</body>
</html>


