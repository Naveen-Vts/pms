<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"
	import="java.util.*,com.vts.*,java.text.SimpleDateFormat"%>
	<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Milestone Activity Details</title>
<jsp:include page="../static/header.jsp"></jsp:include>
  <spring:url value="/resources/css/milestone/milestoneActivityDetails.css" var="milestoneActivityDetails" />     
<link href="${milestoneActivityDetails}" rel="stylesheet" />

</head>
<body>


	<%
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	Object[] getMA = (Object[]) request.getAttribute("MilestoneActivity");
	List<Object[]> ActivityTypeList = (List<Object[]>) request.getAttribute("ActivityTypeList");
	List<Object[]> allLabList=(List<Object[]>)request.getAttribute("allLabList");
	List<Object[]> EmployeeList=(List<Object[]>)request.getAttribute("EmployeeList");
	String projectId=(String)request.getAttribute("ProjectId");
	String Logintype=(String)session.getAttribute("LoginType");
	String projectDirector=(String)request.getAttribute("projectDirector");
	
	Long EmpId =  (Long)session.getAttribute("EmpId") ;
	String labcode =  (String)session.getAttribute("labcode") ;
	
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
				<div class="card card1" >
					
					
					<div align="right" class="m-1" >
					<form action="#">
					<input type="submit" class="btn btn-primary btn-sm back " id="sub" value="Back" name="sub" formaction="MilestoneActivityList.htm" formnovalidate="formnovalidate">
					<input type="hidden" name="ProjectId" id="ProjectId" value="<%=getMA[10]%>" />
					</form>
					</div>
					 
					 <div class="card-body cardBody1" >
						<div class="panel panel-info m-1" >
							<div class="panel-heading ">
								<h4 class="panel-title">
									<span class="font14"><%=getMA[1]!=null?StringEscapeUtils.escapeHtml4(getMA[1].toString()): " - "%> : MIL-<%=getMA[5]!=null?StringEscapeUtils.escapeHtml4(getMA[5].toString()): " - "%>
										<i class="fa fa-calendar ml-2" aria-hidden="true"
										></i> <%=sdf.format(getMA[2])%> To
										<%=sdf.format(getMA[3])%></span>
								</h4>
								<div class="divFloat1" ></div>
								<div class="divFloat1">
									<a data-toggle="collapse" data-parent="#accordion" href="#collapse1"> <i class="fa fa-plus" id="Clk" onclick="faChange('#Clk')"></i></a>
								</div>
							</div>
							<!-- panel-heading end -->

							<div id="collapse1" class="panel-collapse collapse in">
								<div class="row mb-3 " >
									<div class="col-md-6 ">
										<label class="control-label ml-2 text-center"
											>Activity:
											<%=getMA[4]!=null?StringEscapeUtils.escapeHtml4(getMA[4].toString()): " - "%>
										</label>
									</div>
									<div class="col-md-2 ">
										<label class="control-label">Type: <%=getMA[17]!=null?StringEscapeUtils.escapeHtml4(getMA[17].toString()): " - "%></label>
									</div>
									<div class="col-md-2 ">
										<label class="control-label">First OIC: <%=getMA[6]!=null?StringEscapeUtils.escapeHtml4(getMA[6].toString()): " - "%></label>
									</div>
									<div class="col-md-2 ">
										<label class="control-label">Second OIC: <%=getMA[7]!=null?StringEscapeUtils.escapeHtml4(getMA[7].toString()): " - "%></label>

									</div>
								</div>

								<%
								List<Object[]> MilestoneActivityA = (List<Object[]>) request.getAttribute("MilestoneActivityA");
								int ProjectSubCount = 1;
								if (MilestoneActivityA != null && MilestoneActivityA.size() > 0) {
									for (Object[] obj : MilestoneActivityA) {
								%>
								<div class="row">
									<div class="col-md-11 ml-3" align="left" >

										<div class="panel panel-info m-1">
											<div class="panel-heading">
												<h4 class="panel-title">

													<span class="font14" >
														Activity A<%=ProjectSubCount%> 
														<i class="fa fa-calendar ml-4" aria-hidden="true" ></i> 
														<%=sdf.format(obj[2])%> To <%=sdf.format(obj[3])%>
													</span>

												</h4>
												<div class="divFloat1">
													<a href="#" class="ms-toggle" data-target="#collapse55A<%=ProjectSubCount%>">
														<i class="fa fa-plus" id="ClkA<%=ProjectSubCount%>"></i>
													</a>
												</div>
											</div>
											<div id="collapse55A<%=ProjectSubCount%>" class="panel-collapse collapse ">
												<div class="row">
													<div class="col-md-6 ">
														<label class="control-label ml-4 text-center"
															>Activity:
															<%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - "%>
														</label>
													</div>
													<div class="col-md-2">
														<label class="control-label">Type: <%=obj[12]!=null?StringEscapeUtils.escapeHtml4(obj[12].toString()): " - "%></label>
													</div>
													<div class="col-md-2">
														<label class="control-label">Weightage: <%=obj[6]!=null?StringEscapeUtils.escapeHtml4(obj[6].toString()): " - "%></label>
													</div>
													<div class="col-md-2">
														<label class="control-label">First OIC: <%=obj[14]!=null?StringEscapeUtils.escapeHtml4(obj[14].toString()): " - "%></label>
													</div>
												</div>

								<%
								// CHANGED: Level B (and everything below it) used to be fetched eagerly here in a
								// nested loop that queried the DB 5 levels deep for every single Level-A activity.
								// That's what made the page slow with ~120 activities. Now we just leave an empty
								// placeholder here; the JS below (loadLevel) fetches Level B for THIS activity
								// only when the user actually expands it, via MilestoneActivityLevelFetch.htm.
								String aAncestorOic = getMA[8] + "," + getMA[9] + "," + obj[13] + "," + obj[15];
								%>
								<div class="row">
									<div class="col-md-12">
										<div id="childrenA<%=ProjectSubCount%>" class="ms-children"
											data-level="2"
											data-parent-id="<%=obj[0]%>"
											data-loaded="false"
											data-path="<%=ProjectSubCount%>"
											data-ancestor-oic="<%=aAncestorOic%>">
											<!-- Level B activities load here on first expand -->
										</div>
									</div>
								</div>

								<%
								boolean canAddB = Arrays.asList(getMA[8].toString(), projectDirector, getMA[9].toString(), obj[13].toString(), obj[15].toString()).contains(EmpId.toString()) || Logintype.equalsIgnoreCase("A");
								if (canAddB) {
								%>
								<div class="row">
									<div class="col-md-11 ml-3" align="left">
										<div class="panel panel-info m-1">
											<div class="panel-heading">
												<h4 class="panel-title">Add Activity B</h4>
											</div>
											<div>
												<form action="MilestoneActivitySubAdd.htm" method="POST" name="milestoneaddfrm" id="milestoneaddfrmA<%=ProjectSubCount%>">
													<div class="row container-fluid" align="center">
														<div class="col-sm-6" align="left">
															<div class="form-group">
																<label>Activity B Name: <span class="mandatory">*</span></label><br>
																<input class="form-control width-100" type="text" name="ActivityName" id="ActivityNameA<%=ProjectSubCount%>New" maxlength="1000" required="required">
															</div>
														</div>
														<div class="col-md-2" align="left">
															<div class="form-group">
																<label class="control-label">Activity Type </label>
																<select class="form-control selectdee" id="ActivityType1A<%=ProjectSubCount%>New" required="required" name="ActivityType">
																	<option disabled="true" selected value="">Choose...</option>
																	<%
																	for (Object[] actobj : ActivityTypeList) {
																	%>
																	<option value="<%=actobj[0]%>"><%=actobj[1]!=null?StringEscapeUtils.escapeHtml4(actobj[1].toString()): " - "%></option>
																	<%
																	}
																	%>
																</select>
															</div>
														</div>
														<div class="col-md-2" align="left">
															<div class="form-group">
																<label class="control-label">From <span class="mandatory">*</span></label>
																<input class="form-control" name="ValidFrom" required="required" id="DateCompletionA<%=obj[0]%>" value="<%=sdf.format(obj[2])%>" readonly>
															</div>
														</div>
														<div class="col-md-2" align="left">
															<div class="form-group">
																<label class="control-label">To <span class="mandatory">*</span></label>
																<input class="form-control" name="ValidTo" required="required" id="DateCompletionA2<%=obj[0]%>" value="<%=sdf.format(obj[3])%>" readonly>
															</div>
														</div>
													</div>
													<div class="row container-fluid">
														<div class="col-md-2">
															<label>Lab: <span class="mandatory">*</span></label><br>
															<select class="form-control selectdee" name="labCode1" id="labCode1B<%=ProjectSubCount%>New" required
																onchange="renderEmployeeList('1','B<%=ProjectSubCount%>New')" data-placeholder="Lab Name">
																<% for (Object[] lab : allLabList) { %>
																<option value="<%=lab[3]%>" <%if(labcode.equalsIgnoreCase(lab[3].toString())) {%>selected<%} %>><%=lab[3]!=null?StringEscapeUtils.escapeHtml4(lab[3].toString()): " - "%></option>
																<%}%>
															</select>
														</div>
														<div class="col-md-4">
															<div class="form-group">
																<label class="control-label">First OIC </label>
																<div class="float-right"><label>All : &nbsp;&nbsp;</label>
																	<input type="checkbox" class="floatXp" id="allempcheckbox1B<%=ProjectSubCount%>New" onchange="changeempoic1('B<%=ProjectSubCount%>New')">
																</div>
																<select class="form-control selectdee" id="EmpIdB<%=ProjectSubCount%>New" required="required" name="EmpId">
																	<option disabled="true" selected value="">Choose...</option>
																	<% for (Object[] objA : EmployeeList) {%>
																	<option value="<%=objA[0]%>"><%=objA[1]!=null?StringEscapeUtils.escapeHtml4(objA[1].toString()): " - "%>, <%=objA[2]!=null?StringEscapeUtils.escapeHtml4(objA[2].toString()): " - "%></option>
																	<%} %>
																</select>
															</div>
														</div>
														<div class="col-md-2">
															<label>Lab: <span class="mandatory">*</span></label><br>
															<select class="form-control selectdee" name="labCode2" id="labCode2B<%=ProjectSubCount%>New" required
																onchange="renderEmployeeList('2','B<%=ProjectSubCount%>New')" data-placeholder="Lab Name">
																<% for (Object[] lab : allLabList) { %>
																<option value="<%=lab[3]%>" <%if(labcode.equalsIgnoreCase(lab[3].toString())) {%>selected<%} %>><%=lab[3]!=null?StringEscapeUtils.escapeHtml4(lab[3].toString()): " - "%></option>
																<%}%>
															</select>
														</div>
														<div class="col-md-4">
															<div class="form-group">
																<label class="control-label">Second OIC</label>
																<div class="float-right"><label>All : &nbsp;&nbsp;</label>
																	<input type="checkbox" class="floatXp" id="allempcheckbox2B<%=ProjectSubCount%>New" onchange="changeempoic2('B<%=ProjectSubCount%>New')">
																</div>
																<select class="form-control selectdee" id="EmpId1B<%=ProjectSubCount%>New" name="EmpId1" required="required">
																	<option disabled="true" selected value="">Choose...</option>
																	<% for (Object[] objA : EmployeeList) {%>
																	<option value="<%=objA[0]%>"><%=objA[1]!=null?StringEscapeUtils.escapeHtml4(objA[1].toString()): " - "%>, <%=objA[2]!=null?StringEscapeUtils.escapeHtml4(objA[2].toString()): " - "%></option>
																	<%} %>
																</select>
															</div>
														</div>
													</div>
													<div class="form-group" align="center">
														<input type="submit" class="btn btn-primary btn-sm submit" id="sub" value="SUBMIT" name="sub" onclick="return confirm('Are You Sure To Submit?');">
														<button type="submit" class="btn btn-primary btn-sm edit" id="sub" value="C" name="sub" formaction="MilestoneActivityDetails.htm" formnovalidate="formnovalidate">Edit</button>
														<input type="submit" class="btn btn-primary btn-sm back" id="sub" value="Back" name="sub" formaction="MilestoneActivityList.htm" formnovalidate="formnovalidate">
														<input type="hidden" name="ProjectId" value="<%=getMA[10]%>" />
													</div>
													<input type="hidden" name="projectDirector" value="<%=projectDirector %>">
													<input type="hidden" name="LevelId" value="2" />
													<input type="hidden" name="formname" value="<%=ProjectSubCount%>" />
													<input type="hidden" name="MilestoneActivityId" value="<%=getMA[0]%>" />
													<input type="hidden" name="ActivityId" value="<%=obj[0]%>" />
													<input type="hidden" name="OicEmpId" value="<%=getMA[8]%>" />
													<input type="hidden" name="OicEmpId1" value="<%=getMA[9]%>" />
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
												</form>
											</div>
										</div>
									</div>
								</div>
								<script type="text/javascript">
									$(function(){
										initAddFormDates('DateCompletionA<%=obj[0]%>', 'DateCompletionA2<%=obj[0]%>', '<%=sdf.format(obj[2])%>', '<%=sdf.format(obj[3])%>');
									});
								</script>
								<% } %>

								</div>
								<!-- collapse55A end -->
							</div>
							<!-- panel-info A end -->
						</div>
						<!-- col-md-11 end -->
					</div>
					<!-- row end -->

								<%ProjectSubCount++;}} %>

								<!-- panel end -->

				<%if( Arrays.asList(getMA[8].toString(),projectDirector,getMA[9].toString()).contains(EmpId.toString()) || Logintype.equalsIgnoreCase("A")  ){ %>
								<div class="row">
									<div class="col-md-11 ml-4 mb-4" align="left"
										>

										<div class="panel panel-info m-1">
											<div class="panel-heading">
												<h4 class="panel-title">
													Activity A<%=ProjectSubCount %>
												</h4>

											</div>
											<div>
												<form action="MilestoneActivitySubAdd.htm" method="POST" name="milestoneaddfrm" id="milestoneaddfrm">
													<div class="row container-fluid" align="center">
														<div class="col-sm-6" align="left">
															<div class="form-group">
																<label>
																	Activity A Name: <span class="mandatory" >*</span>
																</label>
																<br> 
																<input class="form-control width-100" type="text" name="ActivityName" id="ActivityName<%=ProjectSubCount %>"  maxlength="1000" required="required">
															</div>
														</div>

								

														<div class="col-md-2" align="left">
															<div class="form-group">
																<label class="control-label">Activity Type </label> <select
																	class="form-control selectdee"
																	id="ActivityType1<%=ProjectSubCount %>"
																	required="required" name="ActivityType">
																	<option disabled="true" selected value="">Choose...</option>
																	<%
																	for (Object[] obj : ActivityTypeList) {
																	%>
																	<option value="<%=obj[0]%>"><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "%>
																	</option>
																	<%
																	}
																	%>
																</select>
															</div>
														</div>
														
														<div class="col-md-2" align="left">
															<div class="form-group">
																<label class="control-label">From <span
																	class="mandatory" >*</span></label> <input
																	class="form-control " name="ValidFrom"
																	id="DateCompletion" required="required"
																	value="<%=sdf.format(getMA[2])%>" readonly>
															</div>
														</div>
														<div class="col-md-2" align="left">
															<div class="form-group">
																<label class="control-label">To <span
																	class="mandatory" s>*</span></label> <input
																	class="form-control " name="ValidTo"
																	id="DateCompletion2" required="required"
																	disabled="disabled" value="<%=sdf.format(getMA[3])%>"
																	readonly>
															</div>
														</div>


													</div>

													<div class="row container-fluid">
														<div class="col-md-2">
															<label  >Lab: <span class="mandatory">*</span></label><br>
															<select class="form-control selectdee" name="labCode1" id="labCode1A<%=ProjectSubCount%>" required 
															onchange="renderEmployeeList('1','A<%=ProjectSubCount%>')" data-placeholder= "Lab Name">
															    <% for (Object[] lab : allLabList) { %>
															    	<option value="<%=lab[3]%>" <%if(labcode.equalsIgnoreCase(lab[3].toString())) {%>selected<%} %> ><%=lab[3]!=null?StringEscapeUtils.escapeHtml4(lab[3].toString()): " - "%></option>
															    <%}%>
															</select>
														</div>
														<div class="col-md-4">
							                        		<div class="form-group">
							                            		<label class="control-label">First OIC  </label>
							                            		<div class="float-right" > <label>All : &nbsp;&nbsp;</label>
																	<input type="checkbox" class="floatXp" id="allempcheckbox1A<%=ProjectSubCount %>" 
																	onchange="changeempoic1('A<%=ProjectSubCount %>')" >
																</div>
							                              		<select class="form-control selectdee" id="EmpIdA<%=ProjectSubCount %>" required="required" name="EmpId">
							    									<option disabled="true"  selected value="">Choose...</option>
							    										<% for (Object[] objA : EmployeeList) {%>
																	<option value="<%=objA[0]%>"><%=objA[1]!=null?StringEscapeUtils.escapeHtml4(objA[1].toString()): " - "%>, <%=objA[2]!=null?StringEscapeUtils.escapeHtml4(objA[2].toString()): " - "%> </option>
																		<%} %>
							  									</select>
							                        		</div>
							                    		</div>
							                    		<div class="col-md-2">
															<label  >Lab: <span class="mandatory" >*</span></label><br>
															<select class="form-control selectdee" name="labCode2" id="labCode2A<%=ProjectSubCount%>" required 
															onchange="renderEmployeeList('2','A<%=ProjectSubCount%>')" data-placeholder= "Lab Name">
															    <% for (Object[] lab : allLabList) { %>
															    	<option value="<%=lab[3]%>" <%if(labcode.equalsIgnoreCase(lab[3].toString())) {%>selected<%} %> ><%=lab[3]!=null?StringEscapeUtils.escapeHtml4(lab[3].toString()): " - "%></option>
															    <%}%>
															</select>
														</div>
							                    		<div class="col-md-4 ">
							                        		<div class="form-group">
							                            		<label class="control-label">Second OIC </label>
							                            		<div class="float-right"  > <label>All : &nbsp;&nbsp;</label>
																	<input type="checkbox" class="floatXp" id="allempcheckbox2A<%=ProjectSubCount %>" 
																	onchange="changeempoic2('A<%=ProjectSubCount %>')" >
																</div>
							                              		<select class="form-control selectdee" id="EmpId1A<%=ProjectSubCount %>" name="EmpId1" required="required">
							    									<option disabled="true" selected value="">Choose...</option>
							    										<% for (Object[] objA : EmployeeList) {%>
																		<option value="<%=objA[0]%>"><%=objA[1]!=null?StringEscapeUtils.escapeHtml4(objA[1].toString()): " - "%>, <%=objA[2]!=null?StringEscapeUtils.escapeHtml4(objA[2].toString()): " - "%> </option>
																		<%} %>
							  									</select>
							                        		</div>
							                    		</div>
													</div>


													<div class="form-group" align="center">


														<input type="submit" class="btn btn-primary btn-sm submit " id="sub" value="SUBMIT" name="sub" onclick="return confirm('Are You Sure To Submit?');">
														<button type="submit" class="btn btn-primary btn-sm edit " id="sub" value="C" name="sub" formaction="MilestoneActivityDetails.htm" formnovalidate="formnovalidate">Edit</button>
														<input type="submit" class="btn btn-primary btn-sm back " id="sub" value="Back" name="sub" formaction="MilestoneActivityList.htm" formnovalidate="formnovalidate"> 
														<input type="hidden" name="ProjectId" value="<%=getMA[10]%>" />
													</div>
														<input type="hidden" name="projectDirector" value= "<%=projectDirector %>">
													<input type="hidden" name="LevelId" value="1" /> 
													<input type="hidden" name="formname" value="<%=ProjectSubCount %>" /> 
													<input type="hidden" name="MilestoneActivityId" value="<%=getMA[0]%>" /> 
													<input type="hidden" name="ActivityId" value="<%=getMA[0]%>" />
													<input type="hidden" name="OicEmpId" value="<%=getMA[8]%>" />
													<input type="hidden" name="OicEmpId1" value="<%=getMA[9]%>" />
													<input type="hidden" id="currLabCode" value="<%=labcode%>" />
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
												</form>
											</div>
										</div>

									</div>
								</div>


<%} %>




<div class="row text-danger m-3 fontStr" > 
Kindly note that only the Project Director, the Admin, and the OICs of the Parent Milestone are authorized to add and edit milestones. Please ensure all details are accurate before adding a new milestone.
</div>
							</div>
							<!-- Big card-body end -->

						</div>
						<!-- Card End  -->

					</div>
					

				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript">

var from ="<%=sdf.format(getMA[2])%>".split("-")
var dt = new Date(from[2], from[1] - 1, from[0])
var to ="<%=sdf.format(getMA[3])%>".split("-")
var dt1 = new Date(to[2], to[1] - 1, to[0])
var mindate=dt;
$('#DateCompletion').on('change', function() {
    mindate=$('#DateCompletion').val();
    $('#DateCompletion2').prop("disabled",false);
    $('#DateCompletion2').daterangepicker({
    	"singleDatePicker" : true,
    	"linkedCalendars" : false,
    	"showCustomRangeLabel" : true,
    	"minDate" :mindate,
    	"maxDate" : dt1,
    	"cancelClass" : "btn-default",
    	showDropdowns : true,
    	locale : {
    		format : 'DD-MM-YYYY'
    	}
    	});
  });
$('#DateCompletion').daterangepicker({
	"singleDatePicker" : true,
	"linkedCalendars" : false,
	"showCustomRangeLabel" : true,
	"minDate" :dt,
	"maxDate" : dt1,
	"cancelClass" : "btn-default",
	showDropdowns : true,
	locale : {
		format : 'DD-MM-YYYY'
	}
});

$( document ).ready(function() {
    mindate=$('#DateCompletion').val();
    $('#DateCompletion2').prop("disabled",false);
    $('#DateCompletion2').daterangepicker({
    	"singleDatePicker" : true,
    	"linkedCalendars" : false,
    	"showCustomRangeLabel" : true,
    	"minDate" :mindate,
    	"maxDate" : dt1,
    	"cancelClass" : "btn-default",
    	showDropdowns : true,
    	locale : {
    		format : 'DD-MM-YYYY'
    	}
    	});
  });

	var ProjectId = $('#ProjectId').val();
	
	function changeempoic1(level) {
		var labCode  = $('#labCode1'+level).val();
		if (document.getElementById('allempcheckbox1'+level).checked) {
			employeefetch(0,'EmpId'+level, labCode);
	  	} else {
			employeefetch(ProjectId,'EmpId'+level, labCode);
		}
	}
	
	
	function changeempoic2(level) {
		var labCode  = $('#labCode2'+level).val();
		if (document.getElementById('allempcheckbox2'+level).checked) {
			employeefetch(0,'EmpId1'+level, labCode);
		} else {
			employeefetch(ProjectId,'EmpId1'+level, labCode);
	  	}
	}
	
	function employeefetch(ProID,dropdownid, labCode){
				
		$.ajax({		
			type : "GET",
			url : "ProjectEmpListFetch.htm",
			data : {
				projectid : ProID,
				labCode : labCode
			},
			datatype : 'json',
			success : function(result) {
		
				var result = JSON.parse(result);
									
				var values = Object.keys(result).map(function(e) {
								return result[e]
							});
									
				var s = '<option value="">'+"--Select--"+ '</option>';
				for (i = 0; i < values.length; i++) {									
					s += '<option value="'+values[i][0]+'">'+values[i][1] + ", " +values[i][2] + '</option>';
				} 
									 
				$('#'+dropdownid).html(s);
								
			}
		});
		
	}
		

</script>

	<script>
	
	
	function faChange(id){
		if($(id).hasClass('fa-minus')){
			$(id).removeClass("fa-minus").addClass("fa-plus");
		}else{
			$(id).removeClass("fa-plus").addClass("fa-minus");
		}
	}

$('#Clk').click();
<%
String FormName=(String)request.getAttribute("FromName");
if(FormName!=null){
	String [] id=FormName.split("/");
%>
var autoExpandPath = [<%for(int i=0;i<id.length;i++){%><%=id[i]%><%if(i<id.length-1){%>,<%}%><%}%>];
$(function(){ autoExpandAlongPath(autoExpandPath); });
<%}%>
</script>

<script type="text/javascript">
	function renderEmployeeList(rowId, level) {
		var labCode  = $('#labCode'+rowId+level).val();
		/* var currLabCode  = $('#currLabCode').val(); */
		
		var rowIdShort = rowId==1?"":(rowId-1);
		
		employeefetch(ProjectId, 'EmpId'+rowIdShort+level, labCode);
		
		$('#allempcheckbox'+rowId+level).prop('checked', false);
		
		/* if(currLabCode!=labCode) {
			$('#allempcheckbox'+rowId+level).hide();
		}else {
			$('#allempcheckbox'+rowId+level).show();
			$('#allempcheckbox'+rowId+level).prop('checked', true);
		} */
	}
	
	/* function employeeListByLabCode(rowId, level, labcode) {
	
		var rowIdShort = rowId==1?"":(rowId-1);
		$('#EmpId'+rowIdShort+level).empty(); 
		$.ajax({
		       type: "GET",
		       url: "GetLabcodeEmpList.htm",
		       data: {
		       	LabCode: labcode
		       },
		       dataType: 'json',
		       success: function(result) {
		    	   if (result != null) {
		    		   $('#EmpId'+rowIdShort+level).append('<option disabled="disabled" selected value="">Choose...</option>');
		                for (var i = 0; i < result.length; i++) {
		                    var data = result[i];
		                    var optionValue = data[0];
		                    var optionText = data[1].trim() + ", " + data[3]; 
		                    var option = $("<option></option>").attr("value", optionValue).text(optionText);
		                    $('#EmpId'+rowIdShort+level).append(option); 
		                }
		                //$('#EmpId'+(rowId==1?"":rowId)).select2('refresh');
		           }
		       }
		});
	} */
</script>


<%--
	============================================================================
	NEW: lazy-loading engine for Levels B-E.

	Level A is rendered server-side above (cheap: one query total). Everything
	below Level A used to be fetched eagerly, 5 levels deep, in nested Java
	loops - that's what made this page slow with ~120 activities. Now each
	level is fetched only when the user actually expands that node, via
	MilestoneActivityLevelFetch.htm.
	============================================================================
--%>
<script type="text/javascript">
var RootMilestoneId = "<%=getMA[0]%>";
var RootOicEmpId    = "<%=getMA[8]%>";
var RootOicEmpId1   = "<%=getMA[9]%>";
var ProjectDirectorVal = "<%=projectDirector!=null?StringEscapeUtils.escapeEcmaScript(projectDirector):""%>";
var CurrentLabCode  = "<%=labcode!=null?StringEscapeUtils.escapeEcmaScript(labcode):""%>";

// Same three lists the JSP already had in scope (ActivityTypeList / allLabList / EmployeeList),
// exposed to JS once so the "add child" forms for AJAX-loaded levels (B-E) don't need an extra
// round trip just to populate their dropdowns.
var ActivityTypeOptions = [
<% for (int i = 0; i < ActivityTypeList.size(); i++) { Object[] t = ActivityTypeList.get(i); %>
	{ id: "<%=t[0]%>", name: "<%=t[1]!=null?StringEscapeUtils.escapeEcmaScript(t[1].toString()):""%>" }<%=i<ActivityTypeList.size()-1?",":""%>
<% } %>
];
var LabOptions = [
<% for (int i = 0; i < allLabList.size(); i++) { Object[] l = allLabList.get(i); %>
	{ code: "<%=l[3]%>" }<%=i<allLabList.size()-1?",":""%>
<% } %>
];
var EmployeeOptions = [
<% for (int i = 0; i < EmployeeList.size(); i++) { Object[] e = EmployeeList.get(i); %>
	{ id: "<%=e[0]%>", name: "<%=e[1]!=null?StringEscapeUtils.escapeEcmaScript(e[1].toString()):""%>", dept: "<%=e[2]!=null?StringEscapeUtils.escapeEcmaScript(e[2].toString()):""%>" }<%=i<EmployeeList.size()-1?",":""%>
<% } %>
];

var LEVEL_LETTERS = { 2: 'B', 3: 'C', 4: 'D', 5: 'E' };

function escapeHtml(str) {
	if (str === null || str === undefined) return "";
	return String(str)
		.replace(/&/g, "&amp;")
		.replace(/</g, "&lt;")
		.replace(/>/g, "&gt;")
		.replace(/"/g, "&quot;")
		.replace(/'/g, "&#39;");
}

function parseDMY(str) {
	var p = str.split("-");
	return new Date(p[2], p[1] - 1, p[0]);
}

// Generic date-range-picker wiring reused for every "add child" form (the root Level-A form
// has its own copy further up the page; this is for the AJAX-rendered levels B-E).
function initAddFormDates(fromId, toId, minDateStr, maxDateStr) {
	var minDate = parseDMY(minDateStr);
	var maxDate = parseDMY(maxDateStr);
	var opts = function (min) {
		return {
			singleDatePicker: true, linkedCalendars: false, showCustomRangeLabel: true,
			minDate: min, maxDate: maxDate, cancelClass: 'btn-default', showDropdowns: true,
			locale: { format: 'DD-MM-YYYY' }
		};
	};
	$('#' + fromId).daterangepicker(opts(minDate));
	$('#' + fromId).on('change', function () {
		var mindate = $('#' + fromId).val();
		$('#' + toId).prop('disabled', false);
		$('#' + toId).daterangepicker(opts(mindate));
	});
}

// Fetches ONE level's children for a single parent node and renders them into `container`.
// `onDone` (optional) is called after rendering; used by the auto-expand-after-submit feature.
function loadLevel(container, onDone) {
	if (container.data('loaded') === true) {
		if (onDone) onDone();
		return;
	}
	var parentId = container.data('parent-id');
	var level = container.data('level');
	var ancestorOic = container.data('ancestor-oic');

	container.html('<div class="text-muted ml-3">Loading...</div>');
	$.ajax({
		type: 'GET',
		url: 'MilestoneActivityLevelFetch.htm',
		data: { ParentId: parentId, Level: level, AncestorOicIds: ancestorOic, projectDirector: ProjectDirectorVal },
		dataType: 'json',
		success: function (children) {
			container.data('loaded', true);
			container.empty();
			renderLevelNodes(container, children || []);
			if (onDone) onDone();
		},
		error: function () {
			container.html('<div class="text-danger">Could not load activities. <a href="#" class="ms-retry">Retry</a></div>');
			container.find('.ms-retry').on('click', function (e) {
				e.preventDefault();
				container.data('loaded', false);
				loadLevel(container, onDone);
			});
		}
	});
}

function renderLevelNodes(container, children) {
	var level = container.data('level');
	var parentPath = String(container.data('path'));
	var ancestorOic = container.data('ancestor-oic');
	var letter = LEVEL_LETTERS[level];

	if (!children.length) {
		container.append('<div class="text-muted ml-3">No activities.</div>');
		return;
	}

	$.each(children, function (idx, node) {
		var n = idx + 1;
		var path = parentPath + n;
		var collapseId = 'collapse55' + letter + path;
		var clkId = 'Clk' + letter + path;
		var childAncestorOic = ancestorOic + ',' + node.firstOicId + ',' + node.secondOicId;

		var html = ''
			+ '<div class="row"><div class="col-md-12" align="left">'
			+ '<div class="panel panel-info m-1">'
			+ '<div class="panel-heading"><h4 class="panel-title"><span class="font14">Activity ' + letter + n
			+ ' <i class="fa fa-calendar ml-2" aria-hidden="true"></i> ' + escapeHtml(node.validFrom) + ' To ' + escapeHtml(node.validTo) + '</span></h4>'
			+ '<div class="divFloat1"><a href="#" class="ms-toggle" data-target="#' + collapseId + '"><i class="fa fa-plus" id="' + clkId + '"></i></a></div>'
			+ '</div>'
			+ '<div id="' + collapseId + '" class="panel-collapse collapse">'
			+ '<div class="row">'
			+ '<div class="col-md-6"><label class="control-label ml-2">Activity: ' + escapeHtml(node.activityName) + '</label></div>'
			+ '<div class="col-md-2"><label class="control-label">Type: ' + escapeHtml(node.type) + '</label></div>'
			+ '<div class="col-md-2"><label class="control-label">Weightage: ' + escapeHtml(node.weightage) + '</label></div>'
			+ '<div class="col-md-2"><label class="control-label">First OIC: ' + escapeHtml(node.firstOicName) + '</label></div>'
			+ '</div>';

		if (level < 5) {
			html += '<div id="children' + letter + path + '" class="ms-children"'
				+ ' data-level="' + (level + 1) + '" data-parent-id="' + node.id + '" data-loaded="false"'
				+ ' data-path="' + path + '" data-ancestor-oic="' + escapeHtml(childAncestorOic) + '"></div>';

			if (node.canAddChild) {
				html += buildAddChildForm(LEVEL_LETTERS[level + 1], level + 1, node, path);
			}
		}

		html += '</div></div></div></div>';

		var $node = $(html);
		container.append($node);
		$node.find('.ms-add-form').each(function () {
			var $f = $(this);
			initAddFormDates($f.data('from-id'), $f.data('to-id'), $f.data('min-date'), $f.data('max-date'));
		});
		
		$node.find('.selectdee').select2();
	});
}

function buildAddChildForm(letter, levelId, parentNode, parentPath) {
	var uid = letter + parentPath;
	var fromId = 'DateCompletionAdd' + uid;
	var toId = 'DateCompletionAdd2' + uid;

	var typeOptions = $.map(ActivityTypeOptions, function (o) {
		return '<option value="' + escapeHtml(o.id) + '">' + escapeHtml(o.name) + '</option>';
	}).join('');
	var labOptions = $.map(LabOptions, function (o) {
		var sel = (o.code === CurrentLabCode) ? ' selected' : '';
		return '<option value="' + escapeHtml(o.code) + '"' + sel + '>' + escapeHtml(o.code) + '</option>';
	}).join('');
	var empOptions = $.map(EmployeeOptions, function (o) {
		return '<option value="' + escapeHtml(o.id) + '">' + escapeHtml(o.name) + ', ' + escapeHtml(o.dept) + '</option>';
	}).join('');

	// formname mirrors the original slash-separated convention (e.g. "1/2/3") so any code
	// downstream of MilestoneActivitySubAdd.htm that parses it keeps working unchanged.
	var formname = parentPath.split('').join('/');

	return ''
		+ '<div class="row"><div class="col-md-11 ml-3" align="left">'
		+ '<div class="panel panel-info m-1">'
		+ '<div class="panel-heading"><h4 class="panel-title">Add Activity ' + letter + '</h4></div>'
		+ '<div><form action="MilestoneActivitySubAdd.htm" method="POST" class="ms-add-form"'
		+ ' data-from-id="' + fromId + '" data-to-id="' + toId + '" data-min-date="' + parentNode.validFrom + '" data-max-date="' + parentNode.validTo + '">'
		+ '<div class="row container-fluid" align="center">'
		+ '<div class="col-sm-6" align="left"><div class="form-group"><label>Activity ' + letter + ' Name: <span class="mandatory">*</span></label><br>'
		+ '<input class="form-control width-100" type="text" name="ActivityName" maxlength="1000" required="required"></div></div>'
		+ '<div class="col-md-2" align="left"><div class="form-group"><label class="control-label">Activity Type</label>'
		+ '<select class="form-control selectdee" name="ActivityType" required="required"><option disabled="true" selected value="">Choose...</option>' + typeOptions + '</select></div></div>'
		+ '<div class="col-md-2" align="left"><div class="form-group"><label class="control-label">From <span class="mandatory">*</span></label>'
		+ '<input class="form-control" name="ValidFrom" id="' + fromId + '" required="required" value="' + escapeHtml(parentNode.validFrom) + '" readonly></div></div>'
		+ '<div class="col-md-2" align="left"><div class="form-group"><label class="control-label">To <span class="mandatory">*</span></label>'
		+ '<input class="form-control" name="ValidTo" id="' + toId + '" required="required" value="' + escapeHtml(parentNode.validTo) + '" readonly></div></div>'
		+ '</div>'
		+ '<div class="row container-fluid">'
		+ '<div class="col-md-2"><label>Lab: <span class="mandatory">*</span></label><br>'
		+ '<select class="form-control selectdee" name="labCode1" id="labCode1' + uid + '" required="required" onchange="renderEmployeeList(\'1\',\'' + uid + '\')">' + labOptions + '</select></div>'
		+ '<div class="col-md-4"><div class="form-group"><label class="control-label">First OIC</label>'
		+ '<div class="float-right"><label>All : &nbsp;&nbsp;</label><input type="checkbox" class="floatXp" id="allempcheckbox1' + uid + '" onchange="changeempoic1(\'' + uid + '\')"></div>'
		+ '<select class="form-control selectdee" id="EmpId' + uid + '" name="EmpId" required="required"><option disabled="true" selected value="">Choose...</option>' + empOptions + '</select></div></div>'
		+ '<div class="col-md-2"><label>Lab: <span class="mandatory">*</span></label><br>'
		+ '<select class="form-control selectdee" name="labCode2" id="labCode2' + uid + '" required="required" onchange="renderEmployeeList(\'2\',\'' + uid + '\')">' + labOptions + '</select></div>'
		+ '<div class="col-md-4"><div class="form-group"><label class="control-label">Second OIC</label>'
		+ '<div class="float-right"><label>All : &nbsp;&nbsp;</label><input type="checkbox" class="floatXp" id="allempcheckbox2' + uid + '" onchange="changeempoic2(\'' + uid + '\')"></div>'
		+ '<select class="form-control selectdee" id="EmpId1' + uid + '" name="EmpId1" required="required"><option disabled="true" selected value="">Choose...</option>' + empOptions + '</select></div></div>'
		+ '</div>'
		+ '<div class="form-group" align="center">'
		+ '<input type="submit" class="btn btn-primary btn-sm submit" value="SUBMIT" onclick="return confirm(\'Are You Sure To Submit?\');">'
		+ '<button type="submit" class="btn btn-primary btn-sm edit" value="C" name="sub" formaction="MilestoneActivityDetails.htm" formnovalidate="formnovalidate">Edit</button>'
		+ '<input type="submit" class="btn btn-primary btn-sm back" value="Back" name="sub" formaction="MilestoneActivityList.htm" formnovalidate="formnovalidate">'
		+ '</div>'
		+ '<input type="hidden" name="projectDirector" value="' + escapeHtml(ProjectDirectorVal) + '">'
		+ '<input type="hidden" name="LevelId" value="' + levelId + '">'
		+ '<input type="hidden" name="formname" value="' + formname + '">'
		+ '<input type="hidden" name="MilestoneActivityId" value="' + RootMilestoneId + '">'
		+ '<input type="hidden" name="ActivityId" value="' + escapeHtml(parentNode.id) + '">'
		+ '<input type="hidden" name="OicEmpId" value="' + RootOicEmpId + '">'
		+ '<input type="hidden" name="OicEmpId1" value="' + RootOicEmpId1 + '">'
		+ '<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">'
		+ '</form></div></div></div></div>';
}

// One delegated handler covers the Level-A toggles (server-rendered) AND every AJAX-rendered
// toggle for Levels B-E, without needing to rebind anything after each AJAX insert.
$(document).on('click', '.ms-toggle', function (e) {
	e.preventDefault();
	var $icon = $(this).find('i');
	var $target = $($(this).data('target'));
	
	// Check if the panel is currently open
	var wasVisible = $target.hasClass('in') || $target.hasClass('show');

	// Toggle the icons
	$icon.toggleClass('fa-plus fa-minus');
	
	// Trigger the bootstrap collapse animation
	$target.collapse('toggle');

	// If it was closed, we are opening it, so fetch the children
	if (!wasVisible) {
		// Use .find() instead of .children() because the div is nested deeply
		var $childContainer = $target.find('.ms-children').first();
		
		if ($childContainer.length > 0) {
			loadLevel($childContainer);
		}
	}
});
// Restores the old "jump back to where I was" behaviour after adding/editing an activity:
// walks down the tree, fetching + expanding each level along the path in turn.
function autoExpandAlongPath(path) {
	if (!path || !path.length) return;
	var container = $('#childrenA' + path[0]);
	if (!container.length) return;
	continueExpandPath(container, path, 1);
}

function continueExpandPath(container, path, depth) {
	loadLevel(container, function () {
		container.children('.row').each(function () {
			var $panel = $(this).find('> .col-md-12 > .panel').first();
			$panel.find('> .panel-collapse').collapse('show');
			$panel.find('> .panel-heading .ms-toggle i').removeClass('fa-plus').addClass('fa-minus');
		});
		if (depth >= path.length) return;
		var $childContainers = container.find('> .row > .col-md-12 > .panel > .panel-collapse > .ms-children');
		var $next = $childContainers.eq(path[depth] - 1);
		if ($next.length) {
			continueExpandPath($next, path, depth + 1);
		}
	});
}
</script>

</body>
</html>