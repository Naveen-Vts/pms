<%@page import="javax.swing.Spring"%>
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
<spring:url value="/resources/css/committeeModule/committeeList.css" var="committeeList" />
<link href="${committeeList}" rel="stylesheet" />

<title>COMMITTEELIST</title>
</head>
<body>

<%
SimpleDateFormat sdf1=new SimpleDateFormat("dd-MM-yyyy");
SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
List<Object[]> committeelist=(List<Object[]>)request.getAttribute("committeelist"); 
String projectid=(String) request.getAttribute("projectid"); 
//List<Object[]> projectslist=(List<Object[]>)request.getAttribute("projectslist"); 
String projectappliacble=(String) request.getAttribute("projectappliacble"); 
Object[] projectdetails=(Object[])request.getAttribute("projectdetails");

String LabCode = (String) session.getAttribute("labcode");

String LoginType = (String) session.getAttribute("LoginType");

%>

 <!-- ----------------------------------message ------------------------- -->

	
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

<!-- ----------------------------------message ------------------------- -->

	<div class="card shadow-nohover">
			<div class="card-header">	
				<div class="row">
					<div class="col-md-6">
						<h4>
							Committees List 	<%if(Long.parseLong(projectid)>0){ %>	(Project : <%=projectdetails[4]!=null?StringEscapeUtils.escapeHtml4(projectdetails[4].toString()): " - "%>)		<%} %>
						</h4>
					</div>
					<div class="col-md-6">
						<table class="float-right">
							<tr>
								<td>
									<%if(Long.parseLong(projectid)==0){ %>
									<span class="fs-20">Type  :&nbsp;&nbsp;</span> 
									<form method="post" action="CommitteeList.htm" id="selectfrm" class="mt-n10 float-right">
										<select class="form-control " name="projectappliacble" required="required" id="projectappliacble" onchange="return  projectselectsubmit('selectfrm');" >
											<option <%if( projectappliacble.equalsIgnoreCase("N")){ %> selected <%} %> value="N">Non - Project</option>
											<option <%if(projectappliacble.equalsIgnoreCase("P") ){ %> selected <%} %> value="P">Project</option>
							  			</select>
							  			<input type="hidden" name="projectid" id="projectid" value="<%=projectid %>"  />
										<input type="hidden" name="projectappliacble" id="projectappliacble" value="<%=projectappliacble %>" />
							  			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									</form>
									<%}else{ %>
									<form method="post" action="ProjectMaster.htm" id="selectfrm" class="float-right">
										<button type="submit" class="btn btn-sm back" >BACK</button>
							  			<input type="hidden" name="projectid" id="projectid" value="<%=projectid %>"  />
										<input type="hidden" name="projectappliacble" id="projectappliacble" value="<%=projectappliacble %>" />
							  			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									</form>
									<%} %>
								</td>
								<td class="pl-4">
									<form method="post" action="CommitteeAdd.htm" >
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										<button type="submit" name="projectid" value="<%=projectid %>" class="btn btn-sm add mt-n10" >ADD COMMITTEE</button>											
										<input type="hidden" name="projectappliacble" id="projectappliacble" value="<%=projectappliacble %>" />						
									</form>
								</td>					
							</tr>
						</table>
					</div>	
				</div>
			</div>
		
					<div class="card-body">
						<div class="data-table-area mg-b-15">
							<div class="container-fluid">
								<!-- <form action="ProjectIntiationListSubmit.htm" method="POST" name="myfrm" > -->
								<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
									<div class="sparkline13-list">

										<div class="sparkline13-graph">
											<div class="datatable-dashv1-list custom-datatable-overright">
												<div id="toolbar">
													
												</div>

										<table class="table table-bordered table-hover table-striped table-condensed" id="myTable12" >
													
													<thead>

														<tr class="text-center">
															<th>SN</th>
															<th class="text-left">Full Name</th>
															<th>Code</th>
															<th>Type</th>
															<th>Project Applicable</th>
															<th>Action</th>
														</tr>
													</thead>
													
													<tbody>
											<%int count1=1;
										   	for (Object[] obj :committeelist) {
										   		
										   			   %>
														<tr class="text-center">
															<td><%=count1 %></td>
															<td class="fullname"><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></td>
															<td><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %></td>
															<td>
															<%if(obj[3].toString().equals("S")){%>Standard<%}else{ %>Adhoc<%} %>
															</td>															
															<td><%if(obj[4].toString().equals("P")){%>Project<%}else{ %>Non-Project<%}%></td>															
															<td class="left width">		
															
																<form action="CommitteeEdit.htm" method="post" name="myfrm"
																	class="d-inline">

																	<button class="editable-clsick" name="sub"
																		value="Modify">
																		<div class="cc-rockmenu">
																			<div class="rolling">
																				<figure class="rolling_icon">
																					<img src="view/images/edit.png">
																				</figure>
																				<span>Modify</span>
																			</div>
																		</div>
																	</button>
																	
																	<input type="hidden" name="committeeid"	value="<%=obj[0]%>" /> 
																	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

																</form> 		
															
												
																<%if(projectappliacble.equalsIgnoreCase("N") && !Arrays.asList("CCM","CARS").contains(obj[1].toString()) ){
																	if( (LoginType.equalsIgnoreCase("A") || LoginType.equalsIgnoreCase("P"))){ %>												
																	<form action="CommitteeMainMembers.htm" method="post" name="myfrm"	class="d-inline">
																		<button class="editable-click" name="sub" value="Details" 	>
																			<div class="cc-rockmenu">
																				<div class="rolling">
																					<figure class="rolling_icon">
																						<img src="view/images/preview3.png">
																					</figure>
																					<span>Constitution</span>
																				</div>
																			</div>
																		</button>
	
																		<input type="hidden" name="committeeid"	value="<%=obj[0] %>" />
																		<input type="hidden" name="projectid"	value="0" />
																		<input type="hidden" name="divisionid"	value="0" />
																		<input type="hidden" name="initiationid" value="0" />
	 																	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
																	</form>
																	<%} %> 
																	<form action="NonProjectCommitteeAutoSchedule.htm" method="post" name="myfrm" class="d-inline">
																		<button class="editable-click" name="sub" value="Details" 	>
																			<div class="cc-rockmenu">
																				<div class="rolling">
																					<figure class="rolling_icon">
																						<i class="fa fa-calendar purpleColor" aria-hidden="true"></i>
																					</figure>
																					<span>Auto Schedule</span>
																				</div>
																			</div>
																		</button>
	
																		<input type="hidden" name="committeeid"	value="<%=obj[0] %>" />
																		<input type="hidden" name="projectid"	value="0" />
																		<input type="hidden" name="divisionid"	value="0" />
																		<input type="hidden" name="initiationid" value="0" />
	 																	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
																	</form> 
																<%} %>
															</td>
														</tr>
												<% count1++; } %>

												</tbody>
												</table>												
												<input type="hidden" name="${_csrf.parameterName}"value="${_csrf.token}" />
											</div>
										</div>
									</div>
								</div> 
							</div>
						</div>
					</div>			
					<div class="card-footer" align="right">								
					</div>				
				</div>
<!-- Project committee -->
<script type="text/javascript">	
$('#projecttype').select2();
$(function () {
	  $('[data-toggle="tooltip"]').tooltip()
	})


function Prints(myfrm){
	
	 var fields = $("input[name='btSelectItem']").serializeArray();

	 
	  if (fields.length === 0){
		  myalert();
	 event.preventDefault();
	return false;
	}
		  return true;	 		
	}

function projectselectsubmit(frmid)
{
	$('#'+frmid).submit();
}

$(document).ready(function(){
	  $("#myTable12").DataTable({
	 "lengthMenu": [  5,10,25, 50, 75, 100 ],
	 "pagingType": "simple",
	 "pageLength": 10

	});
});

</script>

				
</body>
</html>