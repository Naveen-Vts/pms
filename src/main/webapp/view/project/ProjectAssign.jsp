<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.vts.pfms.project.model.ProjectAssign"%>
<%@page import="com.vts.pfms.master.model.RoleMaster"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@page import="java.text.DecimalFormat"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/projectModule/projectAssign.css" var="projectAssign" />
<link href="${projectAssign}" rel="stylesheet" />
<title>PROJECT  ASSIGN</title>

</head>
<body>
	<%	SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
		List<Object[]> ProjectAssignList=(List<Object[]>) request.getAttribute("ProjectAssignList");
		List<Object[]> allLabList=(List<Object[]>) request.getAttribute("allLabList");
		List<RoleMaster> roleMasterList=(List<RoleMaster>) request.getAttribute("roleMasterList");
		List<Object[]> ProjectList=(List<Object[]>) request.getAttribute("ProjectList");
		String ProjectId=(String)request.getAttribute("ProjectId");
		//String action=(String)request.getAttribute("action");
		//ProjectAssign projectAssign =(ProjectAssign)request.getAttribute("projectAssign");
		Object[] ProjectCode=(Object[])request.getAttribute("ProjectCode");
		String labcode = (String)session.getAttribute("labcode");
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
		<div class="col-md-12">
			<div class="card">
				<div class="card-header">
					<div class="row">
						<div class="col-md-10"><h4>Project Team</h4></div>
						<div class="col-md-2">		      
							<a class="btn btn-info btn-sm back cs-back" href="MainDashBoard.htm">Back</a>
						</div>
					</div>
				</div>
				<div class="cs-div-color">
					<form method="POST" action="ProjectProSubmit.htm">
						<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
						<div class="row">
							<div class="col-md-3"></div>
							<div class="col-md-2 right">
								<label class="fw-800">Project Code: <span class="mandatory text-danger">*</span></label>
							</div>
							<div class="col-md-3">
								<select class="form-control selectdee" name="ProjectId" id="ProjectId" onchange="this.form.submit()">
					            	<option value="" disabled="disabled" selected="selected">Select Project </option>
					                <%
					                for(Object[] protype:ProjectList ){
					                	String projectshortName=(protype[17]!=null)?" ("+protype[17].toString()+") ":"";
					                	%>
										<option value="<%=protype[0] %>" <%if(ProjectId!=null){ if(protype[0].toString().equalsIgnoreCase(ProjectId)){%>selected="selected" <%}} %>><%=protype[4]!=null?StringEscapeUtils.escapeHtml4(protype[4].toString()): " - "%> <%=projectshortName!=null?StringEscapeUtils.escapeHtml4(projectshortName): " - " %></option>
									<%} %>
								</select>
							</div>
							<div class="col-md-4"></div>
						</div>
						
					</form>
				</div>

				<div class="row mt-10">
      				<div class="col-md-6">
						<div class="mt-0">
							<div class="card " >
								<div class="card-body  shadow-nohover" >
									<form action="#" method="POST" name="frm1" >
										<div class="row mt-20">
											<div class="col-md-12">
												<div class="table-responsive">
													<table class="table table-bordered table-hover table-striped table-condensed" id="myTable"> 
														<thead class="text-center">
															<tr class="cs-tr">
																<th colspan="5">List Of User Assigned for <%if(ProjectCode!=null){ %><%=StringEscapeUtils.escapeHtml4(ProjectCode[1].toString())%><%} %></th>
															</tr>
															<tr>
	   															<th class="w-5">Select</th>
												   				<th >Lab</th>
												   				<th >Employee Name</th>
																<th>Division Code</th>
																<th >Role</th>
	  														</tr>
														</thead>
														<tbody>
	 														<%if(ProjectAssignList!=null){
	 															for(Object[] obj:ProjectAssignList){ %>
	    															<tr>
	  																	<td align="center">
	  																		<input type="radio" name="ProjectEmployeeId" value="<%=obj[0]%>" <%if(obj[8]!=null && Long.parseLong(obj[8].toString())==1) {%>disabled<%} %> >
	  																		<input type="hidden" id="lab_<%=obj[0]%>" value="<%=obj[9]!=null?obj[9]:"-" %>">
	  																		<input type="hidden" id="employee_<%=obj[0]%>" value="<%=obj[3] %>, <%=obj[4] %>">
	  																		<input type="hidden" id="empId_<%=obj[0]%>" value="<%=obj[1] %>">
	  																		<input type="hidden" id="roleMasterId_<%=obj[0]%>" value="<%=obj[8] %>">
	  																	</td>
																		<td class="text-left"><%=obj[9]!=null?StringEscapeUtils.escapeHtml4(obj[9].toString()):"-" %></td>
																		<td class="text-left"><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - " %>, <%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - " %></td>
																        <td class="text-left"><%=obj[5]!=null?StringEscapeUtils.escapeHtml4(obj[5].toString()):"-" %></td>
																        <td class="text-left"><%=obj[12]!=null?StringEscapeUtils.escapeHtml4(obj[12].toString()):"-" %></td>
																	</tr>
															<%} }%>
														</tbody>
													</table>
												</div>
												<div class="text-center">
 													<button type="button" class="btn btn-sm edit" name="action" value="edit" formaction="ProjectProSubmit.htm" onclick="actionHandle('edit')">EDIT</button>&nbsp;&nbsp;
 													<button type="submit" class="btn btn-danger btn-sm delete" formaction="ProjectRevokeSubmit.htm" onclick="actionHandle('revoke')">REVOKE</button>&nbsp;&nbsp;
												</div>
											</div>
										</div>

										<input type="hidden" name="ProjectId" value="<%=ProjectId%>"/> 	 						
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
 									</form>
								</div>
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="mt-0">
							<div class="card" >
								<div class="card-body  shadow-nohover" >
									<form action="ProjectAssignSubmit.htm" method="POST" name="frm1" >
										<div class="row mt-20">
											<div class="col-md-12">
												<div class="table-responsive">
													<table class="table table-bordered table-hover table-striped table-condensed w-100"> 
														<thead class="cs-thead">
															<tr>
	   															<th width="100%" colspan="3">Select Users  for <%if(ProjectCode!=null){ %><%=StringEscapeUtils.escapeHtml4(ProjectCode[1].toString())%><%} %></th>
	  														</tr>
	   													</thead>
														<tbody>
															<tr>
																<td width="35%">
																	<label>Role</label>
																	<select class="form-control selectdee roleMasterId" name="roleMasterId" id="roleMasterId" data-placeholder="Role">
																		<option value="-1">Add New Role</option>
																		<option selected value="0">Not Applicable</option>
																	    <% for (RoleMaster role : roleMasterList) { 
																	    	if(role.getRoleMasterId()==1) continue; %>
																	    	<option value="<%=role.getRoleMasterId()%>"><%=role.getRoleName()!=null?StringEscapeUtils.escapeHtml4(role.getRoleName()): " - "%> (<%=role.getRoleCode()!=null?StringEscapeUtils.escapeHtml4(role.getRoleCode()): " - "%>)</option>
																	    <%}%>
																	</select>
																</td>
																<td width="15%">
																	<label>Lab</label>
																	<select class="form-control selectdee" name="labCode" id="labCode" required onchange="renderEmployeeList()" data-placeholder= "Lab Name">
																		<!-- <option disabled="true"  selected value="">Lab Name</option> -->
																	    <% for (Object[] obj : allLabList) { %>
																	    	<option value="<%=obj[3]%>" <%if(labcode.equalsIgnoreCase(obj[3].toString())) {%>selected<%} %> ><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - "%></option>
																	    <%}%>
																	</select>
																</td>
																<td width="50%">
																	<label>Employees</label>
																	<select class="form-control selectdee" name="empId" id="empId" required multiple="multiple" data-placeholder= "Select Employees"  >
																	</select>
																</td>
															</tr>
														</tbody>
													</table>
												</div>
												<div class="text-center">
													<button type="submit" class="btn btn-success btn-sm submit" name="action" value="Add">ASSIGN</button>&nbsp;&nbsp;
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
													<input type="hidden" name="ProjectId" id="ProjectId" value="<%=ProjectId%>"/>
												</div>
											</div>
										</div>
									</form>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
  	
  	<!-- -------------------------------------------- Project Team Edit Modal ------------------------------------------------------------- -->
	<div class="modal fade" id="projectTeamEditModal" tabindex="-1" role="dialog" aria-labelledby="projectTeamEditModal" aria-hidden="true">
		<div class="modal-dialog modal-lg modal-dialog-jump" role="document">
			<div class="modal-content cs-content">
				<div class="modal-header cs-mcolor">
		        	<h5 class="modal-title ">Project Team Edit</h5>
			        <button type="button" class="close cs-close" data-dismiss="modal" aria-label="Close">
			          <span class="text-light" aria-hidden="true">&times;</span>
			        </button>
		      	</div>
     			<div class="modal-body">
     				<div class="container-fluid mt-3">
     					<div class="row">
							<div class="col-md-12 " align="left">
								<form action="ProjectAssignSubmit.htm" method="POST" id="myform">
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									<input type="hidden" name="ProjectId" id="ProjectId" value="<%=ProjectId%>"/>
									<input type="hidden" name="empId" id="empIdEdit"/>
									<input type="hidden" name="projectEmployeeId" id="projectEmployeeIdEdit"/>
									<div class="row">
										<div class="col-md-2">
											<label>Lab</label>
											<input type="text" class="form-control" id="labCodeEdit" readonly>
										</div>
										<div class="col-md-4">
											<label>Employee</label>
											<input type="text" class="form-control" id="employeeEdit" readonly>
										</div>
										<div class="col-md-4">
											<label>Role</label>
											<select class="form-control selectdee roleMasterId" name="roleMasterId" id="roleMasterIdEdit" data-placeholder="Role">
												<option value="-1">Add New Role</option>
												<option selected value="0">Not Applicable</option>
											    <% for (RoleMaster role : roleMasterList) { 
											    	if(role.getRoleMasterId()==1) continue;%>
											    	<option value="<%=role.getRoleMasterId()%>"><%=role.getRoleName()!=null?StringEscapeUtils.escapeHtml4(role.getRoleName()): " - "%> (<%=role.getRoleCode()!=null?StringEscapeUtils.escapeHtml4(role.getRoleCode()): " - "%>)</option>
											    <%}%>
											</select>
										</div>
										<div class="col-md-2 mt-auto">
											<button type="submit" class="btn btn-sm edit" name="action" value="Edit" onclick="return confirm('Are you sure to Update?')">UPDATE</button>
										</div>
									</div>
								</form>
							</div>
						</div>
     				</div>
     			</div>
     		</div>
		</div>
	</div>			
	<!-- -------------------------------------------- Project Team Edit Modal End ------------------------------------------------------------- -->		
	
	<!-- ----------------------------------------------- Add New Role Modal --------------------------------------------------------------- -->
	<div class="modal fade bd-example-modal-lg center mt-10p" id="addNewRoleModal" tabindex="-1" role="dialog" aria-labelledby="addNewRoleModal" aria-hidden="true">
		<div class="modal-dialog modal-lg modal-dialog-jump" role="document">
			<div class="modal-content cs-content1">
				<div class="modal-header cs-mcolor">
		        	<h5 class="modal-title ">Add New Role</h5>
			        <button type="button" class="close roleclose cs-close" data-dismiss="modal" aria-label="Close">
			          <span class="text-light" aria-hidden="true">&times;</span>
			        </button>
		      	</div>
     			<div class="modal-body">
     				<div class="container-fluid mt-3">
     					<div class="row">
							<div class="col-md-12 " align="left">
								<form action="#" method="POST" id="myform">
									<div class="form-group">
			       						<div class="row">
			                    		    <div class="col-md-4">
		       									<label class="form-label">Role Name <span class="mandatory">*</span></label>
		       									<input type="text" class="form-control field" name="roleName" id="roleName" placeholder="Enter Role Name" maxlength="255" required>
		       								</div>
			                    		    <div class="col-md-3">
		       									<label class="form-label">Role Code <span class="mandatory">*</span></label>
		       									<input type="text" class="form-control field" name="roleCode" id="roleCode" placeholder="Enter Role Code" maxlength="5" required>
		       								</div>
		       								<div class="col-md-2 mt-auto">
		       									<button type="button"class="btn btn-sm submit" onclick="addNewRoleDetails()">SUBMIT</button>
		       								</div>
	                  				 	</div>
                  				 	</div>
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								</form>
							</div>
						</div>
     				</div>
     			</div>
     		</div>
		</div>
	</div>	
	<!-- ----------------------------------------------- Add New Role Modal End --------------------------------------------------------------- -->
	
<script>
	$(document).ready(function () {
		renderEmployeeList();
	});
	
	function renderEmployeeList(){

		var labCode  = $('#labCode').val();
		var ProjectId  = $('#ProjectId').val();
		
		if(labCode !=""){
			$.ajax({
				type : "GET",
				url : "ProjectTeamByLabCode.htm",
				data : {
					labCode : labCode,
					projectId : ProjectId
				},
				datatype : 'json',
				success : function(result) {
					var result = JSON.parse(result);
					
					var values = Object.keys(result).map(function(e) {
	 					return result[e]
					});
					var s = '<option value="">--Select--</option>';
					for (i = 0; i < values.length; i++) 
					{
						s += '<option value="'+values[i][0]+'">' +values[i][1].trim() + ", " +values[i][2]+""  + '</option>';
					} 
					 
					$('#empId').html(s);
					
				}
			});
		}
	}
	
	function actionHandle(action){
	
		var fields = $("input[name='ProjectEmployeeId']").serializeArray();
	
		if (fields.length === 0){
			bootbox.alert("Please Select One Record");
			event.preventDefault();
			return false;
		}
		
		var rowId = fields[0].value;
		
		if(action=='edit'){
			
			var lab = $('#lab_'+rowId).val();
			var employee = $('#employee_'+rowId).val();
			var empId = $('#empId_'+rowId).val();
			var roleMasterId = $('#roleMasterId_'+rowId).val();
			$('#labCodeEdit').val(lab);
			$('#employeeEdit').val(employee);
			$('#empIdEdit').val(empId);
			$('#projectEmployeeIdEdit').val(rowId);
			$('#roleMasterIdEdit').val(roleMasterId);
			$('#projectTeamEditModal').modal('show');
		}else{
			if(confirm("Are You Sure To Revoke!")){
				return true;
			} else{
				event.preventDefault();
				return false;
			}
		}
	}

	$('#roleMasterId,#roleMasterIdEdit').on('change', function () {

	    // only open modal when user manually selects Add New Role
	    if ($(this).val() == '-1') {

	        // prevent opening from edit modal initialization
	        if ($('#projectTeamEditModal').hasClass('show')) {
	            return;
	        }

	        $('.field').val('');
	        $('#addNewRoleModal').modal('show');
	    }
	});
	
	function addNewRoleDetails() {
		if(confirm('Are you sure to Add?')){
			
			var roleName = $('#roleName').val();
			var roleCode = $('#roleCode').val();
			
			if(roleName==null || roleName =="null" || roleName=='') {
				alert('Please fill Role Name');
				return false;
			}else if(roleCode==null || roleCode =="null" || roleCode=='') {
				alert('Please fill Role Code');
				return false;
			}else {
				$.ajax({
					Type:'GET',
					url:'RoleMasterDetailsSubmit.htm',
					datatype:'json',
					data:{
						roleName : roleName,
						roleCode : roleCode,
					},
					success:function(result){
						var values = JSON.parse(result);
						var x="<option value="+values[0]+" selected='selected'>"+ values[1] + " (" + values[2] + ")" + "</option>";     
						//$('#fieldGroupId option[value="-1"]').prop('selected', false);
						$('.roleMasterId').append(x).val(values[0]);
						$('.close.roleclose').click();
					}
				});
				
				return true;
			}
			 
			return true;
		}else{
			event.PreventDefault();
			return false;
		}
	}
	
	// Role Name Duplicate Check
	$('#roleName').on('change', function(){
		var roleName = $(this).val();
		$.ajax({
            type: "GET",
            url: "RoleNameDuplicateCheck.htm",
            data: {
            	roleName: roleName,
            },
            datatype: 'json',
            success: function(result) {
                var ajaxresult = JSON.parse(result); 

                // Check if the Role Name already exists
                if (ajaxresult > 0) {
                	$('#roleName').val('');
                    alert('Role Name Already Exists');
                }
            },
            error: function() {
                alert('An error occurred while checking the Role Name.');
            }
        });
	});
	
	// Role Code Duplicate Check
	$('#roleCode').on('change', function(){
		var roleCode = $(this).val();
		$.ajax({
            type: "GET",
            url: "RoleCodeDuplicateCheck.htm",
            data: {
            	roleCode: roleCode,
            },
            datatype: 'json',
            success: function(result) {
                var ajaxresult = JSON.parse(result); 

                // Check if the Role Code already exists
                if (ajaxresult > 0) {
                	$('#roleCode').val('');
                    alert('Role Code Already Exists');
                }
            },
            error: function() {
                alert('An error occurred while checking the Role Code.');
            }
        });
	});
	
</script>
</body>
</html>