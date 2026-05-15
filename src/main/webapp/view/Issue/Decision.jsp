<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat,java.io.ByteArrayOutputStream,java.io.ObjectOutputStream"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
 <jsp:include page="../static/header.jsp"></jsp:include>
   <spring:url value="/resources/css/issue/decision.css" var="decision" />     
<link href="${decision}" rel="stylesheet" />
<title>Decision</title>

</head>
<body>
<%
List<Object[]> projectlist = (List<Object[]>)request.getAttribute("projectlist");
String projectid = (String)request.getAttribute("projectid");
String committeeid=(String)request.getAttribute("committeeid");
List<Object[]>  projapplicommitteelist=(List<Object[]>)request.getAttribute("projapplicommitteelist");
List<Object[]> recomlist = (List<Object[]>)request.getAttribute("recomendation");
String RecOrDecision = (String)request.getAttribute("recOrDecision");

Map<String,List<List<Object[]>>> actualdecisionsought = (Map<String,List<List<Object[]>>>)request.getAttribute("actualRecDecought");



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
						<form class="form-inline " method="post" action="Decision.htm" id="myform" >
						  
						<div class="row card-header recomendationList" >
				   			<div class="col-md-3">
				   			<%if(RecOrDecision!=null && RecOrDecision.equalsIgnoreCase("R")){%>
								<h4  class="recomendationHeading">Recommendation List</h4>
								<%}else if(RecOrDecision!=null && RecOrDecision.equalsIgnoreCase("D")){%> <h4>Decision List </h4><%}else if(RecOrDecision!=null && RecOrDecision.equalsIgnoreCase("S")){%> 
								<h4>Decision Sought </h4><%}else if(RecOrDecision!=null && RecOrDecision.equalsIgnoreCase("RS")){%> <h4 >Recommendation Sought </h4><%}%>
							</div>		
							<div class="col-md-3 justify-content-end prjDropdown" >
								<table  >
									<tr>
										<td><h5>Proj :</h5></td>
										<td>
												<select class="form-control items" name="projectid" id="projectid"  required="required"  data-live-search="true" data-container="body" onchange='submitForm1();' >
													<option disabled  selected value="">Choose...</option>
													<option <%if(projectid!=null && projectid.equals("0")) { %>selected <%} %>value="0" >General</option>
													<%for(Object[] obj : projectlist){ 
													  String projectshortName=(obj[17]!=null)?" ( "+obj[17].toString()+" ) ":"";
													%>
													<option <%if(projectid!=null && projectid.equals(obj[0].toString())) { %>selected <%} %>value="<%=obj[0]%>" ><%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()):" - "%> <%=projectshortName!=null?StringEscapeUtils.escapeHtml4(projectshortName):" - " %></option>
													<%} %>
												</select>
										</td>
									</tr>	
								</table>							
							</div>
							
							<div class="col-md-3 justify-content-end prjDropdown" >
								<table >
							        <tr>
										<td><h5>Type:</h5></td>
										<td>
											<select class="form-control items" name="recOrDecision"  required="required" id="desc" data-live-search="true" data-container="body" onchange='submitForm();' >
													<option disabled  selected value="">Choose...</option>
													<option value="D"  <%if(RecOrDecision!=null && RecOrDecision.equals("D")){ %>selected <%}%> >Decision</option>
									   				<option value="R"  <%if(RecOrDecision!=null && RecOrDecision.equals("R")){ %>selected <%}%> >Recommendation</option>
									   				<option value="S"  <%if(RecOrDecision!=null && RecOrDecision.equals("S")){ %>selected <%}%> >Decision Sought</option>
									   				<option value="RS"  <%if(RecOrDecision!=null && RecOrDecision.equals("RS")){ %>selected <%}%> >Recommendation Sought</option>
											</select>	
										</td>
									</tr>
								</table>	
							</div>
							
							<div class="col-md-3 justify-content-end prjDropdown" >
								<table >
							        <tr>
										<td><h5>Comm:</h5></td>
										<td>
											<select class="form-control items" name="committeeid" id="committeeid" required="required"  data-live-search="true" data-container="body" onchange='submitForm();' >
													<option disabled  selected value="">Choose...</option>
													<option value="A"  <%if(committeeid!=null && committeeid.equals("A")){ %>selected <%} %> >All</option>
									   				<% for (Object[] obj : projapplicommitteelist) {%>
													<option value="<%=obj[0]%>"  <%if(committeeid!=null && obj[0].toString().equals(committeeid)){ %>selected<%} %> ><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()):" - "%></option>
													<%} %>
											</select>	
										</td>
									</tr>
								</table>	
							</div>
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />	
 
						 </div>
						 </form>	
					 </div>
				
					<div class="card-body">	
					
					
							<div class="data-table-area mg-b-15">
							<div class="container-fluid">
								<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
									<div class="sparkline13-list">
										<div class="sparkline13-graph">
											<div class="datatable-dashv1-list custom-datatable-overright">
												<div id="toolbar">
													
												</div>
												
												<table class="table table-bordered table-hover table-striped table-condensed" id="myTable12" >
													
															<%int count=0; if(!RecOrDecision.equalsIgnoreCase("S") && !RecOrDecision.equalsIgnoreCase("RS")){
																%>
													<thead>
														<tr>
															<th> SN</th>
															<th> Meeting Id</th>
															<th> <%if(RecOrDecision!=null && RecOrDecision.equalsIgnoreCase("R")){%>
																	Recommendation List
																<%}else if(RecOrDecision!=null && RecOrDecision.equalsIgnoreCase("D")){%>
																	Decision List 
																<%}else if(RecOrDecision!=null && RecOrDecision.equalsIgnoreCase("S")){%>Actual Decision Sought<%} %>
															</th>
															<th><%if(RecOrDecision!=null && RecOrDecision.equalsIgnoreCase("S")){%>Decision Sought <%}else{%>  Remarks <%}%></th>														
														</tr>
														
													</thead>
																
														<tbody>	
																<%if(recomlist!=null && recomlist.size()>0){for(Object[] obj :recomlist){%>
														<tr>
																<td  class="sn"><%=++count %></td>
																<td>
																	<form action="CommitteeMinutesNewDownload.htm" method="get" >
																					<button  type="submit" class="btn btn-outline-info" formtarget="_blank" ><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()):"" %></button>
																					<input type="hidden" name="committeescheduleid" value="<%=obj[1] %>" />
																					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
																	</form>
																</td>
																<td>
																	<%=obj[3]!=null?obj[3].toString():" - "%>
																 </td>
																<td> <%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()):" - " %> </td>
														</tr>
														<%}%> </tbody>
															<%}}else{%>
													<thead>
														<tr>
															<th> SN</th>
															<th> Meeting Id</th>
															<th> <%if(RecOrDecision!=null && RecOrDecision.equalsIgnoreCase("S")){%> Decision Sought<%}else{%> Recommendation  Sought <%}%>
															</th>
															<th><%if(RecOrDecision!=null && RecOrDecision.equalsIgnoreCase("S")){%> Taken Decision  <%}else{%> Taken Recomendation  <%}%></th>														</tr>
													</thead>
																<tbody>
																<% if(actualdecisionsought!=null && actualdecisionsought.size()>0){ 
																	for (Map.Entry<String, List<List<Object[]>>> entry : actualdecisionsought.entrySet()) {
																		  String[] key = entry.getKey().split("//"); 
																		  List<List<Object[]>> value = entry.getValue();
																		  List<Object[]> actualsought =value.get(0);
																		  List<Object[]> sought = value.get(1);
															%>
															<tr>
																	<td class="sn"><%=++count %></td>
																	<td>
																		<form action="CommitteeMinutesNewDownload.htm" method="get" >
																						<button  type="submit" class="btn btn-outline-info" formtarget="_blank" > <%=key[0]!=null?StringEscapeUtils.escapeHtml4(key[0].toString()):" - "%></button>
																						<input type="hidden" name="committeescheduleid" value="<%=key[1]%>" />
																						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
																		</form>
																	</td>
																	<td> 
																		<table class="actualSoughttable" >
																		<% int i=0;for(Object[] obj : actualsought){%>
																			<tr class="actualSoughttable">
																				<td class="actualSoughttable"><%=++i %></td>
																				<td class="actualSoughttable"><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()):" - " %></td>
																			</tr>
																			<%}%>
																		</table>
																	</td>
																	<td> 
																		<table class="actualSoughttable">
																		<% int j=0;for(Object[] obj : sought){%>
																		<tr class="actualSoughttable">
																			<td class="actualSoughttable"><%=++j %></td>
																			<td class="actualSoughttable"><%=obj[0]!=null?StringEscapeUtils.escapeHtml4(obj[0].toString()):" - " %></td>
																		</tr>
																		<%}%>
																		</table>
																	 </td>
																</tr>
															<%}%>   
															</tbody>
															<%}}%>
												</table>												
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>					
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
	  $("#myTable12").DataTable({
	 "lengthMenu": [  5,10,25, 50, 75, 100 ],
	 "pagingType": "simple",
	 "pageLength": 10

	});
});
  

</script>
<script type="text/javascript">

function submitForm()
{ 
  document.getElementById('myform').submit(); 
} 

function submitForm1()
{ 
	$("#committeeid").val("A").change();
} 
</script>



</body>
</html>