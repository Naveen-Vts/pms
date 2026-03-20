<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat"%>
    
    <%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/admin/DivisionMasterAdd.css" var="divisionMasterAdd" />
<link href="${divisionMasterAdd}" rel="stylesheet" />

<title>DIVISION MASTER ADD</title>
</head>
<body>

<%

List<Object[]> DivisionGroupListAdd=(List<Object[]>)request.getAttribute("DivisionGroupListAdd");
List<Object[]> DivisionHeadListAdd=(List<Object[]>)request.getAttribute("DivisionHeadListAdd");
List<Object[]> lablist = (List<Object[]>)request.getAttribute("lablist");
String labCode  = (String) session.getAttribute("labcode");


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


	
<br>	
	
<div class="container-fluid">		
<div class="row"> 


<div class="col-sm-2"></div> 
	
<div class="col-sm-8 mt-3"  >
<div class="card shadow-nohover" >
<div class="card-header bg-header" > <b class="text-white style1">Division Add </b></div>

<div class="card-body">

<form name="myfrm" action="DivisionMasterAddSubmit.htm" id="divisionAdd" method="POST" >

<div class="row">

<div class="col-md-3">
         <div class="form-group">
				<label >Division Code:<span class="mandatory">*</span></label>
				<input  class="form-control form-control alphanum-only input-font"  type="text" name="dCode" id="divisionCode" required="required" maxlength="3"> 
	     </div>
</div>		

<div class="col-md-3">
		<div class="form-group">
                 <label>Division Name:<span class="mandatory">*</span></label>
                  <input  class="form-control form-control alphanum-no-leading-space input-font w-80"  type="text" name="dName" id="divisionName" required="required" maxlength="100"  > 
		</div>
</div>

<div class="col-md-3">
		<div class="form-group">
				<label >Group Name:<span class="mandatory" >*</span></label>
				<select class="custom-select" id="grpId" required="required" name="grpId">
								<option selected value="">Choose...</option>
								
								<% for (  Object[] obj : DivisionGroupListAdd){ %>
						
								<option value=<%=obj[0]%>><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "%> </option>
							
								<%} %>
				</select>
		</div>
</div>
   
   
</div><!-- row closed -->

<!-- srikant code start -->
<div class="row">
<div class="col-md-3">
                    <div class="form-group">
                    	<label class="control-label">Lab Code</label>
                    	<select class="custom-select selectdee" name="labCode" onChange="showEmployees(this.value)">		
                      		<option value="" selected="selected" disabled="disabled">Select Labcode</option>											
								<% for (  Object[] obj : lablist){ %>
								<option value=<%=obj[3]%> ><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()):"-"%></option>
								<%} %>
						</select> 
                    </div>
                 </div>
<div class="col-md-3">
		<div class="form-group">
				<label >Division Head Name:<span class="mandatory" >*</span></label>
				<select class="custom-select" id="dHName" required="required" name="dHName">
								<option  selected value="">---Select---</option>
								
							<%-- 	<% for (  Object[] obj : DivisionHeadListAdd){ %>
						
								<option value=<%=obj[0]%>><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "%>, <%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - " %> </option>
							
								<%} %> --%>
				</select>
		</div>
</div>
<div class="col-md-3">
		<div class="form-group">
                 <label>Division Short Name:<span class="mandatory" >*</span></label>
                  <input  class="form-control form-control alphanum-no-leading-space text-uppercase input-font"  type="text" name="divisionShortName" id="divisionShortName" placeholder="Division Short Name" id="divisionShortName" required="required" maxlength="100" > 
		</div>
</div>
</div>
<!-- srikant code end -->
   
	<div align="center">
		<button type="button" class="btn btn-sm submit" onclick="Divisioncheck('divisionAdd');" >SUBMIT</button>
		<a class="btn  btn-sm  back"    href="DivisionMaster.htm">BACK</a>
	</div>

	 <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
</form>
	  </div>
	  </div>
	  </div>
	 <div class="col-sm-2"></div> 
	  </div>
</div>	
	
					
<script type='text/javascript'> 
$(document).ready(function() {
    $('#grpId').select2();
    $('#dHName').select2();
});
</script>
</body>

<script type="text/javascript">


function Divisioncheck(frmid){
	
	var count=0;
	var divisionCode=$('#divisionCode').val();
	
	if(divisionCode.trim()=="")
	{
		alert('Please fill all the fields !');	
		return false;
	}
	
	
	
	$.ajax({

		type : "GET",
		url : "DivisionAddCheck.htm",
		data : {
			
			dcode:divisionCode.trim(),
			
		},
		datatype : 'json',
		success : function(result) {
			var ajaxresult = JSON.parse(result);
			if(ajaxresult[0]>=1){							
				alert('Division Code Already Exists');		
				event.preventDefault();
				return false;
			}
			else if(count===0){	
				/* console.log($('#grpId').val());
				console.log($('#dHName').val()); */
				if(	$('#divisionCode').val().trim()=="" ||
						$('#divisionName').val().trim()=="" || 
						$('#grpId').val().trim()=="" ||
						$('#divisionShortName').val().trim()=="" || //srikant
						$('#dHName').val().trim()=="" )
					{
						alert('Please fill all the fields !');	
						return false;
					}
				
				
				if(confirm('Are you Sure To Submit ?'))
				{
					$('#'+frmid).submit();
				}	
			} 
			
			
		}
	});	
	
	
}


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
			$('#dHName').html(s);
		}
		});
	}
}


</script>


</html>