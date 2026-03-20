<%@page import="org.apache.commons.text.StringEscapeUtils"%>
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
<spring:url value="/resources/css/master/divisionAssign.css" var="divisionAssign" />     
<link href="${divisionAssign}" rel="stylesheet" />
<title>Division Employee</title>

</head>
<body>
<%SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
List<Object[]> DivisionList=(List<Object[]>) request.getAttribute("divisionlist");
List<Object[]> divisionemplist=(List<Object[]>) request.getAttribute("divisionemplist");
List<Object[]> empoyeelist=(List<Object[]>) request.getAttribute("empoyeelist");
Object[] divisiondata=(Object[]) request.getAttribute("divisiondata");
String divisionid=divisiondata[0].toString();
List<Object[]> lablist = (List<Object[]>) request.getAttribute("lablist");
String labcode = (String) session.getAttribute("labcode");

//divisiondata[2]="<h1>sdgfsgf</h1>";

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
				<div class="col-md-10"><h4>Division Assign</h4></div>
				<div class="col-md-2">		      
					<a class="btn btn-info btn-sm  back backbtn"     href="MainDashBoard.htm">Back</a>
				</div>
			</div>
		</div>
		
		
<div class="nav navbar auditnavbar" >

			<form class="form-inline " method="POST" action="DivisionEmployee.htm">
				<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
				<label class="divisonLabel" >Division: <span class="mandatory mandate" >*</span></label>
					<select class="form-control form-control selectpicker selectDiv" name="divisionid"  data-width="300"  id="name">
		        <option value="" disabled="disabled" selected="selected">Select Division </option>
			                <%
			                for(Object[] obj:DivisionList){
	
	                           %>
	                           
								<option value="<%=obj[0] %>" <%if(divisionid!=null){ if(obj[0].toString().equalsIgnoreCase(divisionid)){%> selected="selected" <%}} %>><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()):"-"%></option> 
							
								<%} %>
					</select>
	
	 <input id="submit" type="submit" name="submit" value="Submit" hidden="hidden">
			</form>
	
	</div>



                    
                    
 <div class="row revokeMainDiv" >
      <div class="col-md-8">
<div class="subdiv" >

<div class="card   " >
		  	
     <div class="card-body  shadow-nohover" >
  <form action="DivsionEmployeeRevoke.htm" method="POST" name="frm1" >
    <div class="row rowdiv" >
      <div class="col-md-12">
 <div class="table-responsive">
	   <table class="table table-bordered table-hover table-striped table-condensed" id="myTable"> 
	   <thead  class="theader">
	   <tr   class="trtable" >
	   <th colspan="5">List Of User Assigned for <%if(divisiondata!=null){ %><%=divisiondata[1]!=null?StringEscapeUtils.escapeHtml4(divisiondata[1].toString()):"-"%><%} %></th>
	  </tr>
	   <tr>
	   <th  class="selecttd">Select</th>
	  
	   <th >Employee Name</th>
	  <th>Designation</th>
	  <th>Lab</th>
	  
	  
	  </tr>
	   </thead>
    <tbody>
    
    
	 <%if(divisionemplist!=null && divisionemplist.size() > 0){
	 for(Object[] obj:divisionemplist){ %>
	    <tr>
	  <td><input type="radio" name="divisionempid" value=<%=obj[0]!=null?StringEscapeUtils.escapeHtml4(obj[0].toString()):" - "%>></td>
	    
        <td class="tdData"><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()):"-"%> </td>
        <td class="tdData"><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()):"-"%> </td>
        <td class="tdData"><%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()):"-"%></td>
	    </tr>
	    <%} } else if(divisionemplist == null) {%>
	     <tr class="center">     
	      <td valign="top" colspan="4" class="dataTables_empty">Select division to assign !!</td>
	    </tr>
	           <% }  else { %>
	         <tr class="center">     
	      <td valign="top" colspan="4" class="dataTables_empty">no members assigned </td>
	    </tr>
	     <% } %>
	    </tbody>
</table>
 	
</div>
<div class="btnDiv" >
 <button type="submit" class="btn btn-danger btn-sm delete"  formaction="DivsionEmployeeRevoke.htm" onclick="return Edit('frm1');"  >REVOKE</button>&nbsp;&nbsp;

</div>
</div>
</div>

<input type="hidden" name="divisionid" value="<%=divisionid %>" /> 	 						
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					
 	</form>

</div>

</div>
</div>
</div>
<div class="col-md-4">
<div class="topdIV" >

<div class="card  " >
		  	
     <div class="card-body  shadow-nohover" >
  <form action="DivisionAssignSubmit.htm" method="POST" name="frm2" >
    <div class="row rowDiv" >
      <div class="col-md-12">
      <div class="table-responsive" >
	   <table class="table table-bordered table-hover table-striped table-condensed"  > 
	   <thead>
	   <tr>
	   <th colspan="2">Select Labcode </th>
	  </tr>
	 </thead>
    <tbody>
	    <tr>
	    
	     <td colspan="2">
					<select class="form-control form-control" name="labcode"  data-placeholder="Select Labcode" id= "labcode" onChange="showEmployees(this.value)"  >
					<option value="" selected="selected" disabled="disabled">Select Labcode</option>
			        <%if(empoyeelist != null){ 
			             for(Object[] obj:lablist){ %> 
			           
			            <option value="<%=obj[3] %>" <%if(labcode.equalsIgnoreCase(obj[3].toString())){ %> selected="selected" <%} %>>
				        	<%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()):"-"%></option>
			        <% }} %>	  	
				 	</select>
	    </td>
	    </tr>
	    </tbody>
</table>
</div>
    <div class="table-responsive" >
	   <table class="table table-bordered table-hover table-striped table-condensed"  > 
	   <thead>
	   <tr>
	   <th colspan="4">Select User  for <%if(divisiondata!=null){ %><%=divisiondata[2]!=null?StringEscapeUtils.escapeHtml4(divisiondata[2].toString()):"-"%><%} %></th>
	  </tr>
	 </thead>
    <tbody>
	    <tr>
	    
	     <td colspan="4">
					<select class="form-control form-control" name="employeeid"  data-placeholder="Select Employees"  required="required" id= "LogInId" multiple="multiple" >
			        <%if(empoyeelist != null){ 
			             for(Object[] obj:empoyeelist){ %> 
			           
			            <option value="<%=obj[0] %>">
				        	<%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()):"-"%>, <%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()):"-"%></option>
			        <% }} %>	  	
				 	</select>
	    </td>
	    </tr>
	    </tbody>
</table>
</div>
<div  class="btnClass">

 <button type="submit" class="btn btn-success btn-sm submit" name="sub" value="edit" onclick="return confirm('Are you Sure To Assign ?');" >ASSIGN</button>&nbsp;&nbsp;
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
<input type="hidden" name="divisionid" value="<%=divisionid %>" />
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
  
  <script type='text/javascript'>
  
    $(document).ready(function(){
    	$('#name').select2();
    	$('#LogInId').select2();
    	$('#labcode').select2();
    });
  
  </script>
<script>
function Edit(myfrm){
	
	 var fields = $("input[name='divisionempid']").serializeArray();

			  if (fields.length === 0){
				  bootbox.alert("Please Select One Record");
			 event.preventDefault();
			return false;
			}
			  var cnf=confirm("Are You Sure To Revoke!");
			  

			  if(cnf){
			
			return true;
			
			}
			  else{
				  event.preventDefault();
					return false;
					}
			
			}
			
function showEmployees(labcode){
	
	var divisionId = $('#name').val();
	
	if(labcode !=""){
		$.ajax({		
		type : "GET",
		url : "EmployeeOnDivisionCode.htm",
		data : {
			labcode : labcode,
			divisionId: divisionId,
		},
		datatype : 'json',
		success : function(result) {
	
			var result = JSON.parse(result);	
			var values = Object.keys(result).map(function(e) {
									 return result[e]
						});
							
			var s = '';
			for (i = 0; i < values.length; i++) {									
				s += '<option value="'+values[i][0]+'">'
					+values[i][1].replaceAll("<","").replaceAll(">","") + " (" +values[i][2].replaceAll("<","").replaceAll(">","")+")" 
					+ '</option>';
			} 
			$('#LogInId').html(s);
		}
		});
	}
}
</script>

<script>
$(document).ready(function() {
	   $('#name').on('change', function() {
		   $('#submit').click();

	   });
	});

</script>
</body>
</html>