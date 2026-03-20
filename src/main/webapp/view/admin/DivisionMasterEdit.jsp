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
<%-- <jsp:include page="../static/sidebar.jsp"></jsp:include> --%>
<title>OFFICER MASTER EDIT</title>
<spring:url value="/resources/css/admin/DivisionMasterEdit.css" var="divisionMasterEdit" />
<link href="${divisionMasterEdit}" rel="stylesheet" />

</head>
<body>

<%

List<Object[]> DivisionGroupList=(List<Object[]>)request.getAttribute("DivisionGroupList");

List<Object[]> DivisionHeadList=(List<Object[]>)request.getAttribute("DivisionHeadList");


Object[] DivisionMasterEditData=(Object[])request.getAttribute("DivisionMasterEditData");

List<Object[]> lablist = (List<Object[]>)request.getAttribute("lablist");

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

<div id="ajaxError" class="dis-none">
    <div align="center">
        <div class="alert-danger axios-err" id="ajaxErrorMessage" >
            <!-- Error message will appear here -->
        </div>
    </div>
</div>
	
	
<br>	
	
<div class="container-fluid">		
<div class="row"> 


<div class="col-sm-2"></div> 
	
 <div class="col-sm-8 mt-3" >
<div class="card shadow-nohover"  >
<div class="card-header bg-header"> <b class="text-white style1">Division Master Edit</b></div>
<div class="card-body">


<form name="myfrm" action="DivisionMasterEditSubmit.htm" id="editCheck" method="POST" >
<div class="row">

<div class="col-3">
       <div  class="form-group">	
				<label >Division Code: <span class="mandatory">*</span></label>
				<input  class="form-control alphanum-only input-font" type="text" name="DivisionCode" required="required" maxlength="3"  readonly	 value="<%=DivisionMasterEditData[1]!=null?StringEscapeUtils.escapeHtml4(DivisionMasterEditData[1].toString()): ""%>" >
       </div>
</div>


<div class="col-3">	
       <div class="form-group"> 
				<label >Division Name:<span class="mandatory">*</span></label>
				<input  class="form-control form-control alphanum-no-leading-space input-font"  type="text" name="DivisionName" required="required" maxlength="255" value="<%=DivisionMasterEditData[2]!=null?StringEscapeUtils.escapeHtml4(DivisionMasterEditData[2].toString()): "" %>">
		</div>
</div>


<div class="col-3">
       <div  class="form-group">
					<label >Group Name:<span class="mandatory" >*</span></label>
					 <select class="custom-select input-font" name="GroupId"  required="required" id="GroupId" >
									<option value="" disabled="true" selected="selected" hidden="true">--Select--</option>
									
									<% for (  Object[] obj : DivisionGroupList){ %>
									<option value=<%=obj[0]%> <%if(obj[0].toString().equalsIgnoreCase(DivisionMasterEditData[4].toString())) {%> selected="selected"  <%} %>><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "%> </option>
									<%} %>
					</select> 
		</div>
</div>
		
</div>
<div class="row">
<div class="col-md-2">
                       		<div class="form-group">
                           		<label class="control-label">Lab Code</label>
                             		<select class="custom-select selectdee" name="labCode" onChange="showEmployees(this.value)">		
                             		<option value="" selected="selected" disabled="disabled">Select Labcode</option>											
										<% for (  Object[] obj : lablist){ %>
										<option value=<%=obj[3]%><%if(obj[3].toString().equalsIgnoreCase(DivisionMasterEditData[7].toString())) {%> selected="selected" <%} %>><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()):"-"%></option>
										<%} %>
								</select> 
                       		</div>
                   		</div>

<div class="col-3">
       <div  class="form-group">	
					<label >Division Head Name:<span class="mandatory" >*</span></label>
					<select class="custom-select input-font" name="DivisionHeadName"  required="required" id ="DivisionHeadName" >
						<option value="" disabled selected="selected">--Select--</option>
									
			<% for (  Object[] obj : DivisionHeadList){ %>							
			<option value=<%=obj[0]%> <%if(obj[0].toString().equalsIgnoreCase(DivisionMasterEditData[3].toString())) {%> selected="selected" <%} %>><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "%>, <%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - " %> </option><%}%>
					</select> 
		</div>
		</div>

<div class="col-3">
       <div  class="form-group">		
				<label >isActive:<span class="mandatory" >*</span></label>
				<select  class="form-control selectpicker input-font"  name="isActive" required="required" maxlength="255" > 
				                     <option value="1" <%if(DivisionMasterEditData[5].toString().equalsIgnoreCase("1")) {%> selected="selected" <%} %> >YES</option>                      
									 <option value="0" <%if(DivisionMasterEditData[5].toString().equalsIgnoreCase("0")) {%> selected="selected" <%} %> >NO</option>                     
				</select>                 
		</div>
</div> 
<!-- srikant code start -->
<div class="col-3">
        <div class="form-group"> 
				<label >Division Short Name:<span class="mandatory" >*</span></label>
				<input class="form-control alphanum-no-leading-space input-font" type="text" name="DivisionShortName" id="DivisionShortName" required="required" maxlength="255" 
       value="<%= DivisionMasterEditData[6] != null ? StringEscapeUtils.escapeHtml4(DivisionMasterEditData[6].toString()) : "" %>">


		</div>
</div>
<!-- srikant code end -->
</div>


<div align="center">
		<button type="submit" class="btn btn-sm submit" onclick="return confirm('Are you Sure To Submit ?');" >SUBMIT</button>&nbsp;&nbsp;
		<a class="btn  btn-sm  back"    href="DivisionMaster.htm">BACK</a>
</div>

<input type="hidden" name="DivisionId"                  value="<%=DivisionMasterEditData[0]%>" />
<input type="hidden" name="${_csrf.parameterName}" 	value="${_csrf.token}"  />
	
	
</form>
	
	
	      </div>
	    </div>
	  </div>
    </div>
</div>
	
	
	

</body>
<script>
  $(document).ready(function(){
	  $('#DivisionHeadName').select2();
	  $('#GroupId').select2();
  });
  
  $(document).ready(function(){
	    $('#DivisionHeadName').select2();
	    $('#DivisionId').select2();
	    
	    $('select[name="isActive"]').change(function() {
	        var isActive = $(this).val();
	        var DivisionId = $('input[name="DivisionId"]').val(); 
	        
	        $('#ajaxError').hide();
	        $('#ajaxErrorMessage').text('');
	        
	        if(isActive === "0") {
	            $.ajax({
	                url: 'divisionMasterEditSubmitCheck.htm', 
	                type: 'POST',
	                data: {
	                    isActive: isActive,
	                    DivisionId: DivisionId, 
	                    '${_csrf.parameterName}': '${_csrf.token}'
	                },
	                success: function(response) {
	                    if(response.valid) {
	                        console.log("Validation passed");
	                    } else {
	                        $('#ajaxErrorMessage').text(response.message);
	                        $('#ajaxError').show();
	                        $('select[name="isActive"]').val("1");
	                    }
	                },
	                error: function(xhr) {
	                    $('#ajaxErrorMessage').text("Error validating status");
	                    $('#ajaxError').show();
	                    $('select[name="isActive"]').val("1");
	                }
	            });
	        }
	    });
	});
  

  function showEmployees(labcode){
  	console.log(labcode);	
  	if(labcode !=""){
  		$.ajax({		
  		type : "GET",
  		url : "EmployeeOnLabCode.htm",
  		data : {
  			labcode : labcode,
  		},
  		datatype : 'json',
  		success : function(result) {
  	
  			var result = JSON.parse(result);	
  			var values = Object.keys(result).map(function(e) {
  									 return result[e]
  						});
  							
  			var s = '<option value="" selected disabled>---Select---</option>';
  			for (i = 0; i < values.length; i++) {									
  				s += '<option value="'+values[i][0]+'">'
  					+values[i][1].replaceAll("<","").replaceAll(">","") + " (" +values[i][2].replaceAll("<","").replaceAll(">","")+")" 
  					+ '</option>';
  			} 
  			$('#DivisionHeadName').html(s);
  		}
  		});
  	}
  }
</script>
</html>