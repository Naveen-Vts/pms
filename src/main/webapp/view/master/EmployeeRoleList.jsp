<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat"%>
     <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/js/excel.js" var="excel" />
<script src="${excel}"></script>
<spring:url value="/resources/css/master/employeeRoleList.css" var="employeeRoleList" />     
<link href="${employeeRoleList}" rel="stylesheet" />
</head>
<body>
<%

List<Object[]> empList=(List<Object[]>) request.getAttribute("empList");
List<Object[]> LabList =(List<Object[]>)request.getAttribute("LabList");
String labcode = (String)request.getAttribute("labcode");
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

 <div class="card shadow-nohover" >
  <div class="card-header">
  <div class="row">
	<div class="col-md-2"><h4><b>Employee List</b></h4></div>
	<div class="col-md-2"></div>
	<div class="col-md-2" >
	<h6 class="labName"><b>Lab Name</b></h6>
	</div>
	
	<div class="col-md-3 formDiv" >
	<form method="POST" action="EmployeeRoleList.htm">
	<select class="form-control selectdee" id="labcode" name="labcode" data-container="body" data-live-search="true"  required="required" >
				<option value="" disabled="disabled" selected="selected"	hidden="true">--Select--</option>
										<% for ( Object[]  obj :LabList) {%>
								<option value="<%=obj[2] %>"  <%if(labcode.equalsIgnoreCase(obj[2].toString())){ %>  selected <%} %> > <%=obj[2]!=null? StringEscapeUtils.escapeHtml4(obj[2].toString()):"-" %></option><%} %>
					<option value="@EXP" <%if(labcode.equalsIgnoreCase("@EXP")){ %>  selected <%} %>>Expert</option>
					</select> 
	
	
	<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
 	<input id="submit" type="submit" name="submit" value="Submit" hidden="hidden">
	 </form>
	</div>
	
	</div>
	</div>
	
	<div class="card-body"> 
	
	
	 <div class="table-responsive">
	   <table class="table table-bordered table-hover table-striped table-condensed "  id="myTable"> 
	   <thead  class="tblthead">
	   <tr>
		  <th>SN</th>
		  <th>Employee No</th>
		  <th>Employee Name</th>
		  <th>Employee Role</th>
		  <th>Action</th>
	  </tr>
	   </thead>
	   <tbody>
	   <%
		if(empList!=null && empList.size()>0){	  
			int sn=0;
	   	for(Object[]obj:empList){
	   %>
	   <tr>
	   <td class="tblthead"><%=++sn %></td>
	   <td ><%=obj[5]!=null? StringEscapeUtils.escapeHtml4(obj[5].toString()):"-" %></td>
	   <td><%=obj[3]!=null? StringEscapeUtils.escapeHtml4(obj[3].toString()):"-" %> <%if(labcode.equalsIgnoreCase("@EXP")) {%> ( <%=obj[2]!=null? StringEscapeUtils.escapeHtml4(obj[2].toString()):"-" %> )  <%} %></td>
	   <td ><%=obj[4]!=null? StringEscapeUtils.escapeHtml4(obj[4].toString()):"-" %></td>
	   <td>
	   
	   <button class="btn bg-transparent" onclick="showRole('<%=obj[5]!=null? StringEscapeUtils.escapeHtml4(obj[5].toString()):"-"%>','<%=obj[1]!=null? StringEscapeUtils.escapeHtml4(obj[1].toString()):"0"%>','<%=obj[2]!=null? StringEscapeUtils.escapeHtml4(obj[2].toString()):"-"%>','<%=obj[4]!=null? StringEscapeUtils.escapeHtml4(obj[4].toString()):"-"%>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></button>
	   
	   </td>
	   </tr>
	   <%}} %>
	   </tbody>
	   </table>
	   </div>
	</div>
	
	
	</div>
	
	
	</div>
	</div>
	
	<!-- Modal for Employee Role -->
<div class="modal" tabindex="-1" role="dialog" id="rolemodal">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-body">
      	<form action="EmployeeRoleAdd.htm" method="post">
         <div class="col-md-10 modelhead" >Employee Role </div> <br>
         <div class="col-md-10">
         <input class="form-control" name="role" id="role" required>
         </div>
         
         <div align="center" class="mt-2">
         <button class="btn btn-sm submit" id="add" onclick="return confirm('Are you sure to submit?')"> SUBMIT</button>
         <button class="btn btn-sm edit" id="edit" onclick="return confirm('Are you sure to update?')"> UPDATE</button>
         <input type="hidden" name="empno" id="empno">
         <input type="hidden" name="Organization" id="Organization">
         <input type="hidden" name="labcode" id="" value="<%=labcode%>">
         <input type="hidden" name="roleid" id="roleid" value="">
         	<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
         </div>
         </form>
      </div>
      
    </div>
  </div>
</div>
	 <script type="text/javascript">
 $(document).ready(function() {
		$("#myTable").DataTable({
				'aoColumnDefs': [{
				'bSortable': false,
			     
				'aTargets': [-1] /* 1st one, start by the right */
			}]
		});
	});
 
 $(document).ready(function() {
	   $('#labcode').on('change', function() {
	     $('#submit').click();

	   });
	});
 
 
 function showRole(empno,roleid,labcode,role){
	 
	 $('#rolemodal').modal('show');
	 $('#empno').val(empno);
	 $('#Organization').val(labcode);
	 
	 if(roleid!=='null'){
		 $('#edit').show();
		 $('#add').hide();
		 $('#roleid').val(roleid);
		 $('#role').val(role);
	 }else{
		 $('#add').show();
		 $('#edit').hide();
		 $('#roleid').val("0");
		 $('#role').val("");
	 }
	 
	 
 }
/*  $(document).ready(function(){
 	  $("#myTable1").DataTable({
 	 "lengthMenu": [ 5, 10,25, 50, 75, 100 ],
 	 "pagingType": "simple",
 	 "pageLength": 5
 });
 }); */
 </script>
</body>
</html>