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

<spring:url value="/resources/css/master/officerMasterEdit.css" var="officerMasterEdit" />     
<link href="${officerMasterEdit}" rel="stylesheet" />

</head>
<body>

<%

Object[] OfficerEditData=(Object[])request.getAttribute("OfficerEditData");
List<Object[]> DesignationList=(List<Object[]>)request.getAttribute("DesignationList");
List<Object[]> DivisionList=(List<Object[]>)request.getAttribute("OfficerDivisionList");
List<Object[]> LabList=(List<Object[]>)request.getAttribute("LabList");
List<Object[]> OfficerList = (List<Object[]>)request.getAttribute("OfficerList");
String seslabid=(String)session.getAttribute("labid");


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


<div class="col-sm-2"></div> 
	
 <div class="col-sm-8 topdiv"  >
<div class="card shadow-nohover"  >
<div class="card-header headerCard" >
                    <b class="text-white style1">Officer Edit</b>
        		</div>
<div class="card-body">


<form name="myfrm" action="OfficerMasterEditSubmit.htm" method="POST" id="myfrm">
<div class="row">
<div class="col-md-2">
              <div class="form-group">
					<label >Employee No:<span class="mandatory" >*</span></label>
					<input  class="form-control alphanum-only"  type="text" id="EmpNo" name="EmpNo" required="required" maxlength="20"  readonly	value="<%=OfficerEditData[1]!=null?StringEscapeUtils.escapeHtml4(OfficerEditData[1].toString()):"" %>">
			</div>
</div>
<div class="col-md-3">
					 <div class="form-group">
			                <label>Rank/Salutation</label><br>
			                 <select class="form-control selectdee"  id="title" name="title" data-container="body" data-live-search="true"  >
								<option value=""  selected="selected"	hidden="true">--Select--</option>
								<option value="Prof." <%if(OfficerEditData[10]!=null && OfficerEditData[10].toString().equalsIgnoreCase("Prof.")){%> selected="selected" <%}%>>  Prof.</option>
								<option value="Lt."  <%if(OfficerEditData[10]!=null && OfficerEditData[10].toString().equalsIgnoreCase("Lt.")){%>  selected="selected" <%}%>>  Lt.</option>
								<option value="Dr."   <%if(OfficerEditData[10]!=null && OfficerEditData[10].toString().equalsIgnoreCase("Dr.")){%>   selected="selected" <%}%>>  Dr.</option>

							</select>
					</div>
</div>
<div class="col-md-3">
					 <div class="form-group">
			                <label>Title</label><br>
			                 <select class="form-control selectdee" id="salutation" name="salutation" data-container="body" data-live-search="true"  >
								<option value=""  selected="selected"	hidden="true">--Select--</option>
								<option value="Mr."    <%if(OfficerEditData[11]!=null && OfficerEditData[11].toString().equalsIgnoreCase("Mr.")){%>    selected="selected" <%}%>> Mr.</option>
								<option value="Ms."   <%if(OfficerEditData[11]!=null && OfficerEditData[11].toString().equalsIgnoreCase("Ms.")){%>   selected="selected" <%}%>> Ms.</option>
								
							</select>
					</div>
</div>
<div class="col-md-4">
              <div class="form-group">
					<label >Employee Name:<span class="mandatory" >*</span></label>
					<input  class="form-control alpha-no-leading-space"  type="text" id="EmpName" name="EmpName" required="required" maxlength="255"  value="<%=OfficerEditData[2]!=null?StringEscapeUtils.escapeHtml4(OfficerEditData[2].toString()):"" %>">
				</div>
</div>
</div>

<div class="row">
<div class="col-md-3">
              <div class="form-group">
					<label >Designation:<span class="mandatory" >*</span></label>
					<select class="form-control selectdee" name="Designation"  id="Designation" data-container="body" data-live-search="true"  required="required" >
									<option value="" disabled="disabled" selected="selected"	hidden="true">--Select--</option>
										
											<%  for ( Object[]  obj :DesignationList) {%>			
											<option value="<%=obj[0] %>" <%if(OfficerEditData[3].toString().equalsIgnoreCase(obj[0].toString())) {%> selected="selected" <%} %>> <%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()):"-" %> </option>		
											<%} %>
					</select> 
			  </div>
</div>
<div class="col-md-3">
              <div class="form-group">
					<label >Extension No:<span class="mandatory" >*</span></label>
					<input  class="form-control alphanum-only"  type="text" id="ExtNo"  name="ExtNo" required="required" maxlength="10"  value="<%=OfficerEditData[4]!=null?StringEscapeUtils.escapeHtml4(OfficerEditData[4].toString()):"" %>">
				</div>
</div>

<div class="col-md-3">
              <div class="form-group">
					<label >Mobile No:<span class="mandatory" >*</span></label>
					<input  class="form-control indian-mobile" type="text"  required="required" id="mobilenumber"  name="mobilenumber" maxlength="10" value="<%=OfficerEditData[9]!=null?StringEscapeUtils.escapeHtml4(OfficerEditData[9].toString()):""%>" id="">
			   </div>
</div>

<div class="col-md-3">
              <div class="form-group">
					<label >Lab Email: <span class="mandatory" >*</span></label>
					<input  class="form-control email-input"  type="email" id="Email" name="Email" required="required" maxlength="255" value="<%=OfficerEditData[5]!=null?StringEscapeUtils.escapeHtml4(OfficerEditData[5].toString()):"" %>">
			  </div>
</div>
</div>

<div class="row">
<div class="col-md-3">
              <div class="form-group">
					<label >Drona Email: </label>
					<input  class="form-control email-input" type="email" name="DronaEmail" id="DronaEmail" required="required" maxlength="255"   value="<%=OfficerEditData[7]!=null?StringEscapeUtils.escapeHtml4(OfficerEditData[7].toString()):"" %>" id="">
			  </div>
</div>

<div class="col-md-3">
              <div class="form-group">
					<label >Internet Email:</label>
					<input  class="form-control email-input" type="email" name="InternetEmail" id="InternetEmail" required="required" maxlength="255"  value="<%=OfficerEditData[8]!=null?StringEscapeUtils.escapeHtml4(OfficerEditData[8].toString()):"" %>"  id="">
			  </div>
</div>

<div class="col-md-3">
              <div class="form-group">
					<label >Division:<span class="mandatory" >*</span></label>
					<select class="form-control selectdee" name="Division" id="Division" data-container="body" data-live-search="true"  required="required" >
									<option value="0" selected="selected" hidden="true">--Select--</option>
								<% for ( Object[]  obj :DivisionList) {%>
								<option value="<%=obj[0] %>" <%if(OfficerEditData[6].toString().equalsIgnoreCase(obj[0].toString())) {%> selected="selected" <%} %>> <%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()):"-" %></option>
								<%} %>
					</select> 
				</div>
</div>

<div class="col-md-3">
	<div class="form-group">
		<label >Superior Officer:<!-- <span class="mandatory" >*</span> --></label>
		<select class="form-control selectdee" name="superiorOfficer" id="superiorOfficer" data-container="body" data-live-search="true"  required="required" >
			<option value="0" selected="selected" hidden="true">--Select--</option>
			<% for ( Object[]  obj :OfficerList) {%>
				<option value="<%=obj[0] %>" <%if(OfficerEditData[12].toString().equalsIgnoreCase(obj[0].toString())) {%> selected="selected" <%} %>><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()):"-"%>, <%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()):"-" %></option>
			<%} %>
		</select> 
	</div>
</div>

</div>
<!-- Srikant code Start -->
<div class="row">
<div class="col-md-3">
	<div class="form-group">
		<label >Emp Status:<span class="mandatory" >*</span></label>
		<select class="form-control" name="empStatus" id="empStatus" data-container="body" data-live-search="true"  required="required">
		<option value=""   <%if(OfficerEditData[13]==null){%> selected="selected" <%}%> hidden="true">--Select--</option>
		<option value="P" <%if(OfficerEditData[13]!=null && OfficerEditData[13].toString().equalsIgnoreCase("P")){%> selected="selected" <%}%>>Present</option>
		<option value="R" <%if(OfficerEditData[13]!=null && OfficerEditData[13].toString().equalsIgnoreCase("R")){%> selected="selected" <%}%>>Retired</option>
		<option value="T" <%if(OfficerEditData[13]!=null && OfficerEditData[13].toString().equalsIgnoreCase("T")){%> selected="selected" <%}%>>Transferred</option>
		
		</select> 
	</div>
</div>
</div>
<!--Srikant code end  -->
<div class="row">
<div class="col-sm-5" ></div>
	<input type="button"  class="btn btn-primary btn-sm submit" value="SUBMIT" onclick="return empNoCheck('myfrm');"/>
	<button type="button" class="btn btn-info btn-sm shadow-nohover back backbtn"  onclick="submitForm('backfrm');" >Back</button>
</div>

	<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}"  />
	
	<input type="hidden" name="OfficerId" value="<%=OfficerEditData[0]%>">
	<input type="hidden" name="labId" value="<%=seslabid%>">	
</form>
	
	<form action="Officer.htm" method="get" id="backfrm">
	
		<%if(OfficerEditData.length==11){ %>	 
			<input type="hidden" name="empType" value="E">
		<%}else{ %>
			<input type="hidden" name="empType" value="I">
		<%} %>
	
	</form>
	
	
	  </div>
	  </div>
	  </div>
	 
	 <div class="col-sm-2"></div> 
	 
	  </div>
	
</div>	
	
	
	

<script type="text/javascript">
function submitForm(frmid)
{ 
  document.getElementById(frmid).submit(); 
} 


function Edit(myfrm){
	
	 var fields = $("input[name='Did']").serializeArray();

	  if (fields.length === 0){
	alert("Please Select A Record");
	 event.preventDefault();
	return false;
	}
	 
	
	
		 
	
		  return true;
	 
			
	}
function Delete(myfrm){
	

	var fields = $("input[name='Did']").serializeArray();

	  if (fields.length === 0){
	alert("Please Select A Record");
	 event.preventDefault();	
	return false;
	}
	  var cnf=confirm("Are You Sure To Make Officer Inactive !");
	  if(cnf){
	
	return true;
	
	}
	  else{
		  event.preventDefault();
			return false;
			}
	
	}
		
		function empNoCheck(frmid)
		{
			var title=$('#title').val();
			var salutation=$('#salutation').val();
			var EmpName=$('#EmpName').val();
			var Designation=$('#Designation').val();
			var ExtNo=$('#ExtNo').val();
			var mobilenumber=$('#mobilenumber').val();
			var Email=$('#Email').val();
			var DronaEmail=$('#DronaEmail').val();
			var InternetEmail=$('#InternetEmail').val();
			var Division=$('#Division').val();
			var EmpStauts =$('#empStatus').val();	//srikant
			
		
			
			if(EmpName==="" ||Designation===null || ExtNo==="" || mobilenumber==="" || Email==="" || Division===null || EmpStauts==="" ) //srikant  
			{
				alert('Please Fill All the Mandatory Fields ');
			}else if(!Email.includes("@") || (DronaEmail.length > 1 && !DronaEmail.includes("@")) || (InternetEmail.length > 1 && !InternetEmail.includes("@")) )
			{
				alert('please use correct email format(E.g. abc1@gmail.com)');
			}else if((title==="" && salutation==="")||(title!=="" && salutation!=="")){
				window.alert('please select either Title or Rank');
				event.preventDefault();
				return false;
			}else if(mobilenumber.length < 10){
				alert('Please enter a valid 10-digit Indian mobile number starting with 6-9.');
			}else{
				if(window.confirm('Are you sure to save?')){
					document.getElementById(frmid).submit(); 
				}else{
					event.preventDefault();
					return false;
				}
			}
			 
	
		}







</script>
</body>
</html>