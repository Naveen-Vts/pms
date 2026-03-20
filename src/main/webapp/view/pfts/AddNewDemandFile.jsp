<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.vts.pfms.master.dto.DemandDetails"%>
<%@page import="com.vts.pfms.master.dto.ProjectFinancialDetails"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.math.BigDecimal"%> 
<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@page import="java.text.Format"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat,java.time.LocalDate"%>
 <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%> 
    
   
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/pfts/AddNewDemandFile.css" var="FromExternalCSS" />     
<link href="${FromExternalCSS}" rel="stylesheet" />
<title>Procurement Status</title>

<meta charset="ISO-8859-1">

</head>
<body>

<%

FormatConverter fc=new FormatConverter(); 
SimpleDateFormat sdf=fc.getRegularDateFormat();
SimpleDateFormat sdf1=fc.getSqlDateFormat(); int addcount=0; 
NFormatConvertion nfc=new NFormatConvertion();
SimpleDateFormat sdf2=new SimpleDateFormat("dd-MM-yyyy");
List<DemandDetails> demandList=(List<DemandDetails>)request.getAttribute("demandList");
String projectId= (String) request.getAttribute("projectId");
String projectCode = (String) request.getAttribute("projectCode");
Format format = com.ibm.icu.text.NumberFormat.getCurrencyInstance(new Locale("en", "in"));

%>

<div class="container-fluid">
		<div class="row">
			<div class="col-md-12">
				<div class="card shadow-nohover">

					<div class="card-header style1">

						<form action="ProcurementStatus.htm" method="post">
							<b class="text-white">Add Demand File</b>
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							<input type="hidden" name="projectid" <%if(projectId!=null){%> value="<%=projectId%>" <%}%>>
							<button type="submit" class="btn btn-info btn-sm shadow-nohover back style2" name="back" > BACK </button>
						</form>
					</div>
					<div class="card-body">
						<table class="table table-bordered table-hover table-striped table-condensed dataTable no-footer" id="myTable" role="grid" aria-describedby="myTable_info"> 
							<thead>
								<tr>
									<th>SN</th>
									<th>Demand No</th>
									<th>Demand Date</th>
									<th>Item Nomenclature</th>
									<th>Estimated Cost</th>
									<th>Action</th>
								</tr>
							</thead>
							<tbody>
								<%if(demandList!=null){ int count=1; %>
									<%for(DemandDetails demand : demandList){ %>
										
											<tr>
												<td><%=count++ %></td>
												<td><%=demand.getDemandNo()!=null?StringEscapeUtils.escapeHtml4(demand.getDemandNo()):"-" %></td>
												<td><%=demand.getDemandDate()!=null?sdf.format(sdf1.parse(demand.getDemandDate())):"-" %></td>
												<td><%=demand.getItemFor()!=null?StringEscapeUtils.escapeHtml4(demand.getItemFor()):"-" %></td>
												<td class="style3">
									               <%if(demand.getEstimatedCost()!=null) {%>
									                 <%=format.format(new BigDecimal(demand.getEstimatedCost().toString())).substring(1)%>
									                <%}else{ %>--<%} %>
									            </td>
												
												<td>
													<form action="AddDemandFileSubmit.htm" method="post">
														<input type="hidden" name="projectCode" id="projectCode" value="<%=projectCode %>" >
														<input type="hidden" name="DemandNo" id="DemandNoId" value="<%=demand.getDemandNo() %>" >
														<input type="hidden" name="projectId" id="projectIdId" value="<%=demand.getProjectId() %>" >
														<input type="hidden" name="demandDate" id="demandDateId" value="<%=demand.getDemandDate() %>" >
														<input type="hidden"  name="ItemNomcl" id="ItemNomclId" value= "<%=demand.getItemFor() %>" >
														<input type="hidden"  name="Estimtedcost" id="EstimtedcostId"  value= "<%=demand.getEstimatedCost()%>" >
														<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

														<button type="submit" class="btn" onclick="return confirm('Are You Sure To Add This Demand?');"><i class="fa fa-plus-square" aria-hidden="true"></i></button>

												
													</form>
												</td>
											</tr>
											
										
									<%} %>
								<%} %>
							</tbody>
						</table>
					</div>				
				</div>
			</div>
		</div>
	</div>

	
<script type="text/javascript">

$(document).ready(function(){
	  $("#myTable").DataTable({
	 "lengthMenu": [  5,10,25, 50, 75, 100 ],
	 "pagingType": "simple"
	
});
  });
             var demandList_json=null;
             $(document).ready(function(){
                <%
                Gson gson = new Gson();
                String json = gson.toJson(demandList); 
                %>
                demandList_json=<%=json%>
            });
             
                 $( "#selectDemand" ).change(function() {
            	     var selectId=$("#selectDemand").val();
            	     var date = new Date(demandList_json[selectId].DemandDate);
            	         var date1=date.getDate();
            	         if(date1<10){
            	        	 date1='0'+date1;
            	         }
            	         var month=(date.getMonth()+1);
            	         if(month<10){
            	        	 month='0'+month;
            	         }
            	         date=date1+'-'+month+'-'+date.getFullYear();
            	    $("#DemandNoId" ).val(demandList_json[selectId].DemandNo);
            	     $( "#projectIdId" ).val(demandList_json[selectId].ProjectId);
            	    $( "#demandDateId" ).val(date);
            	    $( "#ItemNomclId" ).val(demandList_json[selectId].ItemFor);
            	    $( "#EstimtedcostId" ).val(demandList_json[selectId].EstimatedCost);
            	     
            	});
</script>

</body>
</html>