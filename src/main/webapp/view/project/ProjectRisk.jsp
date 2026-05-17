<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.math.BigDecimal"%> 
<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat,java.time.LocalDate"%>
 <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>  
   
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/projectModule/projectRisk.css" var="projectRisk" />
<link href="${projectRisk}" rel="stylesheet" />
<title>Risk </title>

<meta charset="ISO-8859-1">

</head>
<body >
<%

FormatConverter fc=new FormatConverter(); 
SimpleDateFormat sdf=fc.getRegularDateFormat();
SimpleDateFormat sdf1=fc.getSqlDateFormat(); int addcount=0; 
NFormatConvertion nfc=new NFormatConvertion();

String EmpId=(String)request.getAttribute("EmpId");
List<Object[]> projectslist=(List<Object[]>)request.getAttribute("projectlist");
String projectid=(String)request.getAttribute("projectid");
List<Object[]> riskdatalist=(List<Object[]>)request.getAttribute("riskdatalist");
List<String> riskdatapresentlist=(List<String>)request.getAttribute("riskdatapresentlist");

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
				<div class="card shadow-nohover">
					<div class="col-md-12">
						<div class="row card-header cs-header">
				   			<div class="col-md-3">
								<h4>Project Risk List</h4>
							</div>
							<div class="col-md-5">
							
							<a class="btn btn-sm float-right" href="RiskTemplate.htm" target="blank">
							Risk Management Plan Template <i class="fa fa-download" aria-hidden="true"></i>
							</a>
							</div>
							<div class="col-md-4 justify-content-end mt-n8">
								<table class="float-right" >
									<tr>
										<td ><h5>Project :</h5></td>
										<td >
											<form method="post" action="ProjectRisk.htm" id="projectchange">
												<select class="form-control items cs-select" name="projectid"  required="required" data-live-search="true" data-container="body" onchange="submitForm('projectchange');">
													<option disabled  selected value="">Choose...</option>
													<%for(Object[] obj : projectslist){ 
													
													String projectShortName=(obj[17]!=null)?"( "+obj[17].toString()+" )":"";
													%>
													<option <%if(projectid!=null && projectid.equals(obj[0].toString())) { %>selected <%} %>value="<%=obj[0]%>" ><%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - " %> <%=projectShortName!=null?StringEscapeUtils.escapeHtml4(projectShortName): " - "%></option>
													<%} %>
													<option <%if(projectid!=null && projectid.equals("0")) { %>selected <%} %>value="0" >General</option>
												</select>
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											</form>
										</td>
									</tr>
								</table>							
							</div>
						 </div>
					 </div>
				
					<div class="card-body">	
					
						<table class="table table-bordered table-hover table-striped table-condensed "  id="myTable" >
							<thead>
								<tr>
									<th class="w-5" data-field="0" tabindex="0" >SN</th>
									<th class="w-17">Risk Id</th>
									<th class="w-33">Risk Description</th>
									<th class="w-10">PDC</th>
									<th class="w-15">Assigned To</th>
									<th class="w-10">Status</th>
									<th class="w-10">View / Add</th>
								</tr>
							</thead>
							<tbody>
					<% 
					if(riskdatalist.size()>0){ 
					for(int i=0;i<riskdatalist.size();i++){	
					%>
							<tr>
								<td class="center"><%=i+1 %></td>
								<td class="left"><%=riskdatalist.get(i)[7]!=null?StringEscapeUtils.escapeHtml4(riskdatalist.get(i)[7].toString()): " - "%></td>
								<td class="left"><%=riskdatalist.get(i)[1]!=null?StringEscapeUtils.escapeHtml4(riskdatalist.get(i)[1].toString()): " - " %></td>
								<td class="center"><%=sdf.format(riskdatalist.get(i)[8])%></td>
								<td class="center"><%=riskdatalist.get(i)[9]!=null?StringEscapeUtils.escapeHtml4(riskdatalist.get(i)[9].toString()): " - "%>, <%=riskdatalist.get(i)[10]!=null?StringEscapeUtils.escapeHtml4(riskdatalist.get(i)[10].toString()): " - "%></td>
								<td class="center">
									<%if(riskdatalist.get(i)[3].toString().equals("I") || riskdatalist.get(i)[3].toString().equals("B")){ %>
										In Progress
									<%}else if(riskdatalist.get(i)[3].toString().equals("A")){  %>
										Assigned
									<%}else if(riskdatalist.get(i)[3].toString().equals("C")){  %>
										Closed
									<%}else if(riskdatalist.get(i)[3].toString().equals("F")){ %>
								        	Forwarded
									<%}%>
								</td>
								<td> 
								<div class="disp-flex">
								<form action="ProjectRiskData.htm" method="post">
										<%if(riskdatapresentlist.contains(riskdatalist.get(i)[0])){%>
											<button type="submit" class="btn"><i class="fa fa-eye fa-lg" aria-hidden="true"></i></button>											
										<%}else{%>
											<button type="submit" class="btn"><i class="fa fa-plus-square fa-lg" aria-hidden="true"></i></button>
										<%}%>
										<input type="hidden" name="actionmainid" value="<%=riskdatalist.get(i)[0]%>">
										<input type="hidden" name="actionassignid" value="<%=riskdatalist.get(i)[6]%>">
										<input type="hidden" name="projectid" value="<%=projectid%>">
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									</form>
							
								<form name="myForm1" id="myForm1" action="ActionSubLaunch.htm" method="POST">
                                    <%if(riskdatalist.get(i)[12].toString().equalsIgnoreCase(EmpId) && !riskdatalist.get(i)[3].toString().equalsIgnoreCase("C") 
                                    		&& !riskdatalist.get(i)[3].toString().equalsIgnoreCase("F")){ %>
											<button class="btn btn-sm editable-click" name="sub">
													<div class="cc-rockmenu">
															<div class="rolling">
																	<figure class="rolling_icon">
																			<img src="view/images/preview3.png">
																	</figure>	
															</div>
													</div>
											</button>
											 <input type="hidden" name="Assigner" value="<%=riskdatalist.get(i)[9]%>,<%=riskdatalist.get(i)[10]%>"/>													
                                             <input type="hidden" name="ActionLinkId" value="<%=riskdatalist.get(i)[11]%>"/>
											 <input type="hidden" name="ActionMainId" value="<%=riskdatalist.get(i)[0]%>"/>
											 <input type="hidden" name="ActionNo" value="<%=riskdatalist.get(i)[7]%>"/>
											 <input type="hidden" name="ActionAssignid" value="<%=riskdatalist.get(i)[6]%>"/>
											 <input type="hidden" name="ProjectId" value="<%=riskdatalist.get(i)[2]%>"/>
											 <input type="hidden" name="flag" value="risk">
											 <input type="hidden" name="projectid" value="<%=projectid%>">
 											 <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								      <%} %>
								   </form>
								   	
									</div>
								</td>	
							</tr>														
						<%}}%>
						</tbody>
					</table>
											
					</div>
				</div>
			</div>
		</div>
	</div>
<script type="text/javascript">

function submitForm(frmid)
{ 
  document.getElementById(frmid).submit(); 
} 
</script>


<script type="text/javascript">

$('.items').select2();


$(document).ready(function(){
	  $("#myTable").DataTable({
	 "lengthMenu": [  10,25, 50, 75, 100 ],
	 "pagingType": "simple"
	
});
  });
  

</script>


</body>
</html>