<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat,java.io.ByteArrayOutputStream,java.io.ObjectOutputStream"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>

<title>Action Review</title>
<style>
label {
	font-weight: bold;
	font-size: 14px;
}

.table thead tr, tbody tr {
	font-size: 14px;
}

body {
	background-color: #f2edfa;
	overflow-x: hidden !important;
}

h6 {
	text-decoration: none !important;
}

.multiselect-container>li>a>label {
	padding: 4px 20px 3px 20px;
}

.width {
	width: 210px !important;
}

.bootstrap-select {
	width: 400px !important;
}

#projectname {
	display: flex;
	align-items: center;
	justify-content: flex-start;
}

#div1 {
	display: flex;
	align-items: center;
	justify-content: flex-end;
}


</style>

<!-- ---------------- tree ----------------- -->
<!-- -------------- model  tree   ------------------- -->
<style>


#modalreqheader {
	background: #145374;
	height: 44px;
	display: flex;
	font-family: 'Muli';
	align-items: center;
	color: white;
}

#filedesc, #file, #fileName {
	margin: 0px;
	font-size: 17px;
	color: #07689f;
	text-align: center;
}

.modal-dialog-jump {
  animation: jumpIn 1.5s ease;
}

@keyframes jumpIn {
  0% {
    transform: scale(0.2);
    opacity: 0;
  }
  70% {
    transform: scale(1);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}
label{
font-weight: bold;
  font-size: 13px;
}
body{
background-color: #f2edfa;
overflow-x:hidden !important; 
}
h6{
	text-decoration: none !important;
}

.cc-rockmenu {
	color: fff;
	padding: 0px 5px;
	font-family: 'Lato', sans-serif;
}

.cc-rockmenu .rolling {
	display: inline-block;
	cursor: pointer;
	width: 34px;
	height: 30px;
	text-align: left;
	overflow: hidden;
	transition: all 0.3s ease-out;
	white-space: nowrap;
}

.cc-rockmenu .rolling:hover {
	width: 108px;
}

.cc-rockmenu .rolling .rolling_icon {
	float: left;
	z-index: 9;
	display: inline-block;
	width: 28px;
	height: 52px;
	box-sizing: border-box;
	margin: 0 5px 0 0;
}

.cc-rockmenu .rolling .rolling_icon:hover .rolling {
	width: 312px;
}

.cc-rockmenu .rolling i.fa {
	font-size: 20px;
	padding: 6px;
}

.cc-rockmenu .rolling span {
	display: block;
	font-weight: bold;
	padding: 2px 0;
	font-size: 14px;
	font-family: 'Muli', sans-serif;
}

.cc-rockmenu .rolling p {
	margin: 0;
}

.width {
	width: 270px !important;
}
.width1 {
	width: 210px !important;
}
a:hover {
	color: white;
}
</style>
</head>
<body>
<%
List<Object[]> projects=(List<Object[]>)request.getAttribute("projects");
String projectid = (String)request.getAttribute("projectid");
SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
List<Object[]> AssigneeList=(List<Object[]>)request.getAttribute("ForwardList");
List<Object[]> EmployeeList=(List<Object[]>)request.getAttribute("EmployeeList");
String loginType=(String)session.getAttribute("LoginType");
String assignorid=(String)request.getAttribute("assignorid");
List<String>loginTypes=Arrays.asList("A","Z");

/* String type= (String)request.getAttribute("type"); */

/*  if(type==null){
	type="A";
} */
%> 
<%String ses=(String)request.getParameter("result"); 
 String ses1=(String)request.getParameter("resultfail");
	if(ses1!=null){
	%>
	<center>
	<div class="alert alert-danger" role="alert" >
                     <%=ses1 %>
                    </div></center>
	<%}if(ses!=null){ %>
	<center>
	<div class="alert alert-success" role="alert"  >
                     <%=ses %>
                   </div></center>
                    <%} %>
<div id="reqmain" class="card-slider">
  </div>
	<div class="container-fluid mt-1">
		<div class="row">
			<div class="col-md-12">
				<div class="card shadow-nohover">
				<div class="card-header">	
					<div class="row">
					<div class="col-md-2" align="left">
					<h5 >Action Review</h5>
					</div>

							<div class="col-md-10">
								<form class="form-inline" method="POST"
									action="ActionReview.htm">
									<div class="row " style=" margin-top: -0.5%;">
										<div class="col-md-2" id="div1">
											<label class="control-label"
												style="font-size: 15px; color: #07689f;">Project
												</label>
										</div>
										<div class="col-md-5" style="margin-top: -4px;"
											id="projectname">
											<select class="form-control selectdee" id="project"
											required="required" name="projectid">
										   <option value="A" <%if (projectid.equalsIgnoreCase("A")) {%> selected
											<%}%>>ALL</option>
											<%if(loginTypes.contains(loginType)) {%>		
											<option value="0" <%if (projectid.equalsIgnoreCase("0")) {%> selected<%}%> >General</option>
											<%}%>		
												<%
												if (!projects.isEmpty()) {
												for (Object[] obj : projects) {
												%>
												<option value="<%=obj[0]%>"
												<%if (projectid.equalsIgnoreCase(obj[0].toString())) {%>
												selected <%}%>><%=obj[2].toString()%> &nbsp;(<%=obj[2].toString() %>)</option>
												<%
												}
												}
												%>
											</select>

										</div>
										
											<div class="col-md-2" id="">
											<label class="control-label"
												style="font-size: 15px; color: #07689f;">Assigner
												</label>
										</div>
										<div class="col-md-3" style="margin-top: -4px;">
										<select class="form-control selectdee" id="assignor"
											required="required" name="assignorid">
											<option value="0" <%if(assignorid.equalsIgnoreCase("0")) {%> selected  <%} %>>ALL</option>
										<%for(Object[]obj:EmployeeList){ %>
											<option  <%if(assignorid.equalsIgnoreCase(obj[0]+"")) {%> selected <%} %> value="<%=obj[0]%>"><%=obj[1] %>, <%=obj[3] %></option>
											
										<%} %>	
											</select>
										
										</div>
										<input type="hidden" name="${_csrf.parameterName}"
											value="${_csrf.token}" /> <input id="submit" type="submit"
											name="submit" value="Submit" hidden="hidden">
									</div>
								</form>
							</div>
							
								<div class="col-md-4">
								<form class="form-inline" method="POST"
									action="ActionReview.htm">
									<div class="row W-100" style="width: 80%; margin-top: -0.5%;">
									<div class="col-md-6" id="div1">
									<!-- <label class="control-label" style="font-size: 15px; color: #07689f;">Action Type:</label> -->
									</div>
									<div class="col-md-6" style="margin-top: -5px;" id="projectname">
										<%-- 	<select class="form-control selectdee" id="type"
												required="required" name="type" onchange="setType()">
												<option value="A" <%if(type.equalsIgnoreCase("A")) {%> selected <%} %>>ALL</option>
												<option value="F" <%if(type.equalsIgnoreCase("F")) {%> selected <%} %> >Forwarded</option>
												<option value="C" <%if(type.equalsIgnoreCase("C")) {%> selected <%} %> >Closed</option>
											</select> --%>

										</div>
										<input type="hidden" name="projectid" id="ProjectIds">
										<input type="hidden" name="${_csrf.parameterName}"
											value="${_csrf.token}" /> <input id="submits" type="submit"
											name="submit" value="Submit" hidden="hidden">
									</div>
								</form>
							</div>	
							
							
							
						</div>
						</div>
						<div class="card-body">
						<div align="right">	
								<button type="button" class="btn btn-sm edit" onclick="changeAssignor()">
								Change Assigner
							</button>
							</div>
							<br>
						<div class="data-table-area mg-b-15">
							<div class="container-fluid">


								<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
									<div class="sparkline13-list">

										<div class="sparkline13-graph">
											<div class="datatable-dashv1-list custom-datatable-overright">
											<!-- 	<div id="toolbar">
													<select class="form-control dt-tb">
														<option value="">Export Basic</option>
														<option value="all">Export All</option>
														<option value="selected">Export Selected</option>
													</select>
												</div> -->
												<table class="table table-bordered" >
													<thead>

														<tr>
															<th> <input type="checkbox" id="selectAlls" ></th>
															<th>Action Id</th>
															<th style="text-align: left;">Action Item</th>
															<th class="width-115px">PDC</th>
															<th style="">Assigned Date</th>								
														 	<th style="">Assignee</th>
														 	<th class="width-115px">Progress</th>		
														 	<th class="width-140px">Action</th>	
														 	
														</tr>
													</thead>
													<tbody>
														<%int  count=1;
														 	if(AssigneeList!=null&&AssigneeList.size()>0){
															for(Object[] obj: AssigneeList){%>
															<tr>
															<td class="center">
															<%if(obj[6]!=null && !"C".equalsIgnoreCase(obj[6].toString())) { %> 	<input type="checkbox" class="aasid"  name="actionassignid"  value="<%=obj[13]%>">  <%}%>
															  </td>
															<td><%=obj[12] %></td>
															<td>
															<input type="hidden" id="td<%=obj[0].toString()%>" value='"<%=obj[5].toString()%>"'>
															<%if(obj[5].toString().length()<75) {%>
															<%=obj[5] %>
															<%}else{ %>
															<%=obj[5].toString().substring(0,75) %>&nbsp;&nbsp;<span style="text-decoration: underline;font-size:13px;color: #145374;cursor: pointer;font-weight: bolder" onclick="showAction('<%=obj[0].toString()%>','<%=obj[12].toString()%>')">show more</span>
															<%} %>
															</td>
															<td><%=sdf.format(obj[4])%></td>
															<td><%=sdf.format(obj[3])%></td>
															<td><%=obj[1]%>, <%=obj[2]%></td>
															<td><%if(!obj[9].toString().equalsIgnoreCase("0")){%>
															<div class="progress" style="background-color:#cdd0cb !important;height: 1.4rem !important;">
															<div class="progress-bar progress-bar-striped" role="progressbar" style=" width: <%=obj[9]%>%;  " aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
															<%=obj[9]%>
															</div> 
															</div><%}else{ %>
															<div class="progress" style="background-color:#cdd0cb !important;height: 1.4rem !important;">
															<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
															Not Yet Started .
															</div>
															</div> <%} %></td>

															<td class="left width1">		
																<%if(obj[6]!=null && ("A".equalsIgnoreCase(obj[6].toString()) || "B".equalsIgnoreCase(obj[6].toString())||"I".equalsIgnoreCase(obj[6].toString()))){%> 
																
																<form name="myForm1" id="myForm1" action="CloseAction.htm" method="POST" 
																	style="display: inline">

																	<button class="btn btn-sm editable-click" name="sub" value="Details" 	>
																		<div class="cc-rockmenu">
																			<div class="rolling">
																				<figure class="rolling_icon">
																					<img src="view/images/preview3.png">
																				</figure>
																				<span>Details</span>
																			</div>
																		</div>
																	</button>
												                    <input type="hidden" name="Assigner" value="<%=obj[1]%>,<%=obj[2]%>"/>													
                                                                    <input type="hidden" name="ActionLinkId" value="<%=obj[11]%>"/>
																	<input type="hidden" name="ActionMainId" value="<%=obj[0]%>"/>
																	<input type="hidden" name="ActionNo" value="<%=obj[12]%>"/>
																	<input type="hidden" name="ActionAssignid" value="<%=obj[13]%>"/>
																	<input type="hidden" name="ActionAssignId" value="<%=obj[13]%>"/><!-- added  -->
																	<input type="hidden" name="ActionPath" value="X"><!-- added -->
																	<input type="hidden" name="ProjectId" value="<%=obj[14]%>"/>
																	<!-- <input type="hidden" name="back" value="backToReview"> -->
 																	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
																	
																</form>
																
																
																<%}else if(obj[6]!=null && "F".equalsIgnoreCase(obj[6].toString())){ %>
																	<!-- 	<span class="text-warning">Forwarded</span> -->
																<%}else if(obj[6]!=null && "C".equalsIgnoreCase(obj[6].toString())){ %>
																<span class="badge badge-pill badge-success p-2">Closed</span>
																<% } %>		
															</td>
														</tr>
												<% count++; } }else{%>
												<tr>
													<td colspan="7" style="text-align: center">No List Found</td>
												</tr>
												<%} %>
												</tbody>
												</table>
										<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" />
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>

					</div>
				
				<div align="center" class="mb-2">

				 </div>
				
				<div class="card-footer" align="right">
				</div>
				</div>
			</div>
		</div>
	</div>			
				
				
				
				
	<!-- Modal for action -->
<div class="modal fade" id="exampleModalCenter" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered" role="document">
    <div class="modal-content">
      <div class="modal-header" style="height:50px;">
        <h5 class="modal-title" id="exampleModalLongTitle">Action</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close" style="color:red;">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body" id="modalbody">
     
      </div>
      <div align="right" id="header" class="p-2"></div>
    </div>
  </div>
</div>			
				
				
				
<div class="modal" tabindex="-1" role="dialog" id="assignorModal">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header bg-primary">
        <h5 class="modal-title text-light">Change Assigner</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true" style="color:red;">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        
        <form action="ChangeAssignor.htm" method="post">
        <div class="col-md-12" style="display: grid">
        <select class="form-control selectdee" id="assignors" required="required" name="AssignorId">
		<%for(Object[]obj:EmployeeList){ %>
		<option  <%if(assignorid.equalsIgnoreCase(obj[0]+"")) {%> selected <%} %> value="<%=obj[0]%>"><%=obj[1] %>, <%=obj[3] %></option>
		<%} %>	
		</select>
		</div>
		<div align="center" class="mt-2 mb-2">
		<button class="btn btn-sm edit" type="submit" onclick="return confirm('Are you sure to update?')">UPDATE</button>
		</div>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        <input type="hidden"  name="projectid" value="<%=projectid%>">
        <input type="hidden" name="assignId" id="assignid">
        </form>
        
      </div>
    </div>
  </div>
</div>			


<script>
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
	
	
	function showAction(a,b){
		/* var y=JSON.stringify(a); */
		var y=$('#td'+a).val();
		console.log(a);
		$('#modalbody').html(y);
		$('#header').html(b);
		$('#exampleModalCenter').modal('show');
	}
	</script>  	
						
						
						
						
		<script>
		$(document).ready(function() {
			   $('#project').on('change', function() {
				   var temp=$(this).children("option:selected").val();
				   $('#submit').click(); 
			   });
			});
		
		$(document).ready(function() {
			   $('#assignor').on('change', function() {
				   var temp=$(this).children("option:selected").val();
				   $('#submit').click(); 
			   });
			});
		
		function setType(){
			var value=$('#project').val();
			document.getElementById('ProjectIds').value=value;
			console.log(value);
			$('#submits').click(); 
		}
		  $('#selectAlls').on('change', function () {
			  
			  var isChecked = $(this).is(':checked');
			  console.log("isChecked --"+isChecked)
			   /*  $('input[name="projectId"]').prop('checked', isChecked); */
			    $('input:checkbox.aasid').prop('checked', isChecked);
		    });


		    $('.aasid').on('change', function () {
		    	console.log($('.aasid').length )
		    	console.log($('.aasid:checked').length )
		        if ($('.aasid').length != $('.aasid:checked').length) {
		        	$('input:checkbox#selectAlls').prop('checked', false);
		        } else if ($('.aasid').length === $('.aasid:checked').length) {
		            $('input:checkbox#selectAlls').prop('checked', true);
		        }
		    });
		    
		    function changeAssignor(){
		    	var values = $('input:checkbox.aasid:checked').map(function () {
		            return this.value;
		        }).get();
				
		    	
		        console.log(values);
		        if(values.length>0){
		        	$('#assignorModal').modal('show');
		        	$('#assignid').val(values)
		        }else{
		        	alert("Select some actions to Change the assignor!")
		        }
		    }
		   
		</script>
</body>
</html>