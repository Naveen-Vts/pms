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
<title>OFFICER MASTER ADD</title>
<spring:url value="/resources/css/master/officerMasterAdd.css" var="officerMasterAdd" />     
<link href="${officerMasterAdd}" rel="stylesheet" />
</head>
<body>


<%SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
List<Object[]> DesignationList=(List<Object[]>)request.getAttribute("DesignationList");
List<Object[]> DivisionList=(List<Object[]>)request.getAttribute("OfficerDivisionList");
List<Object[]> LabList=(List<Object[]>)request.getAttribute("LabList");
List<Object[]> OfficerList = (List<Object[]>)request.getAttribute("OfficerList");
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
	
 <div class="col-sm-8"  >
<div class="card shadow-nohover"  >
<div class="card-header headerCard" > <h4><b class="text-white style1">Officer Add</b></h4> </div>
<div class="card-body">


<form name="myfrm" action="OfficerMasterAddSubmit.htm" method="POST" id="myfrm" autocomplete="off">

<div class="row">
  
     <input type="hidden" name="labId" value="<%= session.getAttribute("labid")%>">
 
<div class="col-md-3">
              <div class="form-group">
					<label >Employee No:<span class="mandatory" >*</span></label>
					<input  class="form-control alphanum-only"  type="text" id="EmpNo"  name="EmpNo" required="required" maxlength="20" 
					 placeholder="Employee No"/>
				</div>
</div>
<div class="col-md-3">
					 <div class="form-group">
			                <label>Rank/Salutation</label><br>
			                 <select class="form-control selectdee" id="title"  name="title" data-container="body" data-live-search="true"  >
								<option value=""  selected="selected"	hidden="true">--Select--</option>
								<option value="Prof.">Prof.</option>
								<option value="Lt.">Lt.</option>
								<option value="Dr.">Dr.</option>
								
							</select>
					</div>
</div>
		<div class="col-md-3">
					 <div class="form-group">
			                <label>Title</label><br>
			                 <select class="form-control selectdee"  id="salutation" name="salutation" data-container="body" data-live-search="true"   >
								<option value=""  selected="selected"	hidden="true">--Select--</option>
								<option value="Mr.">Mr.</option>
								<option value="Ms.">Ms.</option>
							</select>
					</div>
	</div>
<div class="col-md-3">
              <div class="form-group">
					<label >Employee Name:<span class="mandatory" >*</span></label>
					<input  class="form-control alpha-no-leading-space"  type="text" name="EmpName"  id="EmpName"  required="required" maxlength="50"  
					 placeholder="Employee Name" >
			  </div>
</div>
</div>

<div class="row">
<div class="col-md-3">
              <div class="form-group">
					<label >Designation:<span class="mandatory" >*</span></label>
					<select class="form-control selectdee" id="Designation" name="Designation" data-container="body" data-live-search="true"  required="required" >
								<option value="" disabled="disabled" selected="selected"	hidden="true">--Select--</option>					
										<%  for ( Object[]  obj :DesignationList) {%>			
										<option value="<%=obj[0] %>"> <%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()):"-" %></option>			
										<%} %>
					</select> 
			</div>
</div>

<div class="col-md-3">
              <div class="form-group">
					<label >Extension No:<span class="mandatory" >*</span></label>
	<input  class="form-control alphanum-only" type="text" id="ExtNo" name="ExtNo" required="required" maxlength="10"  
					 placeholder="Extension Number(Max 10 char)">
			 </div>
</div>

<div class="col-md-3">
              <div class="form-group">
					<label >Mobile No:<span class="mandatory" >*</span></label>
					<input  class="form-control indian-mobile" type="text" id="mobilenumber"  name="mobilenumber" maxlength="10" placeholder="Phone No"
					 >
				</div>
</div>

<div class="col-md-3">
              <div class="form-group">
					<label >Lab Email:<span class="mandatory" >*</span></label>
					<input  class="form-control email-input"  type="email" name="Email" id="Email" required="required" maxlength="40"  placeholder="Lab Email">
			 </div>
</div>
</div>

<div class="row">
<div class="col-md-3">
              <div class="form-group">
					<label >Drona Email:</label>
					<input  class="form-control email-input"  type="email" name="DronaEmail" id="DronaEmail" required="required" maxlength="40"  placeholder="Drona Email">
			  </div>
</div>

<div class="col-md-3">
              <div class="form-group">
					<label >Internet Email:</label>
                    <input  class="form-control email-input"  type="email"id="InternetEmail" name="InternetEmail" required="required" maxlength="40"  placeholder="Internet Email">
			   </div>
</div>

<div class="col-md-3">
              <div class="form-group">
					<label >Division:<span class="mandatory" >*</span></label>
					<select class="form-control selectdee" id="Division" name="Division" data-container="body" data-live-search="true"  required="required" >
									<option value="0" selected="selected"	hidden="true">--Select--</option>
											<%  for ( Object[]  obj :DivisionList) {%>			
											<option value="<%=obj[0] %>"> <%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()):"-" %></option>			
											<%} %>
					
					</select> 
			 </div>
</div>

<div class="col-md-3">
	<div class="form-group">
		<label >Superior Officer:<!-- <span class="mandatory" >*</span> --></label>
		<select class="form-control selectdee" id="superiorOfficer" name="superiorOfficer" data-container="body" data-live-search="true"  required="required" >
			<option value="0" selected="selected" hidden="true">--Select--</option>
			<%  for ( Object[]  obj :OfficerList) {%>			
				<option value="<%=obj[0] %>"> <%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()):"-"%>, <%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()):"-" %></option>			
			<%} %>
		</select> 
	</div>
</div>

</div>

<div class="row">
<div class="col-sm-5" ></div>
	<input type="button" value="SUBMIT"  class="btn btn-primary btn-sm submit" onclick="return empNoCheck('myfrm');" />
	<button type="submit" class="btn btn-info btn-sm shadow-nohover back backbtn"  formaction="Officer.htm" formmethod="get" formnovalidate="formnovalidate" >BACK</button>
</div>
	<input type="hidden" id="empStatus" name="empStatus" value="P"><!-- srikant -->
	 <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"  />
	 
	 
	  </form>
	
	
	  </div>
	  </div>
	  </div>
	 
	 <div class="col-sm-2"></div> 
	 
	  </div>
	
</div>	
	


		<script type="text/javascript">
		
		
		
		
		function empNoCheck(frmid)
		{
			var EmpName=$('#EmpName').val().trim();
			var Designation=$('#Designation').val();
			var ExtNo=$('#ExtNo').val().trim();
			var mobilenumber=$('#mobilenumber').val().trim();
			var Email=$('#Email').val().trim();	
			var DronaEmail=$('#DronaEmail').val().trim();
			var InternetEmail=$('#InternetEmail').val().trim();
			var Division=$('#Division').val();
			var title=$('#title').val();
			var salutation=$('#salutation').val();
			var $empno=$('#EmpNo').val().trim();

			
			if($empno==="" ||EmpName==="" ||Designation===null || ExtNo===null || mobilenumber==="" ||  Division===null ) 
			{
				alert('Please Fill All the Mandatory Fields ');
			}else if(!Email.includes("@") || (DronaEmail.length > 1 && !DronaEmail.includes("@")) || (InternetEmail.length > 1 && !InternetEmail.includes("@") )){
				alert('please use correct email format(E.g. abc1@gmail.com)')
			} 
			
			else if((title==="" && salutation==="")||(title!=="" && salutation!=="")){
				alert('Please select either Title or Rank');
			}
			else if(mobilenumber.length < 10)
				{
				alert('Please enter a valid 10-digit Indian mobile number starting with 6-9.');
				}
			else if(isNaN(ExtNo))
			{
			alert(' Enter Proper Extension Number ');
			}
			
			else
			{
					$.ajax({
						
						type : "GET",
						url : "EmpNoCheck.htm",
						data : {
							
							empno : $empno
							
						},
						datatype : 'json',
						success : function(result) {
							var count=0;
							if(result==='1' ){
								
								alert('Employee No Already Exists');
								count++;
								return false;
							}
								
							if(count==0)
							{
								if(confirm('Are you Sure To Save ?'))
								{
									$('#'+frmid).submit();
								}
								else
								{
									return false;
								}
							}
						}
					});
				}
		}
		</script>
		<script>
		
		    </script>
		    
	</body>
</html>