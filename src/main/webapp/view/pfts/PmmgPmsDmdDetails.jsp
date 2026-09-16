<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="com.vts.pfms.pfts.dto.PmmgPmsDmdDetails"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<jsp:include page="../static/header.jsp"></jsp:include>
</head>
<body>
<%
	List<PmmgPmsDmdDetails> pmmgPmsDmdDetails = (List<PmmgPmsDmdDetails>)request.getAttribute("pmmgPmsDmdDetails");
	List<Object[]> projectList = (List<Object[]>)request.getAttribute("projectList");
	String projectImmsCd = (String)request.getAttribute("projectImmsCd");
	FormatConverter fc = new FormatConverter();
%>

	<div class="container-fluid">
		<div class="col-md-12">
			<div class="card shadow-nohover">
				<div class="card-header">
					<div class="row">
						<div class="col-md-9">
							<h4>
								<b>Procurement List</b>
							</h4>
						</div>
						<div class="col-md-1 right">
								<label class="control-label" style="font-size: 15px; color: #07689f;"><b>Project:</b></label>
							</div>
							<div class="col-md-2"  style="margin-top: -8px;">
								<form action="PmmgPmsDmdDetails.htm" method="POST">
	        						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									<select class="form-control selectdee" name="projectImmsCd" onchange="this.form.submit()" style="margin-top: -10px;">
										<%if(projectList!=null && projectList.size()>0){
											for (Object[] obj : projectList) {
												String projectshortName1 = (obj[17] != null) ? " ( " + obj[17].toString() + " ) " : ""; %>
												<option value="<%=obj[6]!=null ? obj[6] : ""%>"  <%if(obj[6]!=null && obj[6].toString().equalsIgnoreCase(projectImmsCd)){ %> selected <%} %>>
													<%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - "%> <%=projectshortName1!=null?StringEscapeUtils.escapeHtml4(projectshortName1): " - " %>
												</option>
										<%} }%>
									</select>
								</form>	
							</div>
					</div>
				</div>
				
				<div class="card-body">
					<div class="table-responsive">
						<table class="table table-bordered table-hover table-striped table-condensed dataTable" id="myTable" >
							<thead class="center">
								<tr>
									<th>SN</th>
									<th>Demand No</th>
									<th>Demand Date</th>
									<th>Project Code</th>
									<th>Item Name</th>
									<th>SO No</th>
									<th>SO Date</th>
									<th>DP Date</th>
									<th>Firm Name</th>
									<th>Proc. Stage</th>
								</tr>
							</thead>
							<tbody>
								<%
								int slno = 0;
								for (PmmgPmsDmdDetails dmd : pmmgPmsDmdDetails) { %>
									<tr class="center">
										<td width="5%"><%=++slno %></td>
										<td width="10%"><%=dmd.getDemandNo() %></td>
										<td width="10%"><%=dmd.getDemandDate()!=null ? fc.SqlToRegularDate(dmd.getDemandDate()) : "-" %></td>
										<td width="10%"><%=dmd.getProjectCode() %></td>
										<td width="10%" class="left"><%=dmd.getItemName() %></td>
										<td width="10%"><%=dmd.getSoNo() %></td>
										<td width="10%"><%=dmd.getSoDate()!=null ? fc.SqlToRegularDate(dmd.getSoDate()) : "-" %></td>
										<td width="10%"><%=dmd.getDpDate()!=null ? fc.SqlToRegularDate(dmd.getDpDate()) : "-" %></td>
										<td width="15%" class="left"><%=dmd.getFirmName() %></td>
										<td width="10%"><%=dmd.getProcurementStage() %></td>
									</tr>
								<% } %>
							</tbody>
						</table>
					</div>

				</div>
				
			</div>
		</div>
	</div>			
</body>
	<script type="text/javascript">
	
		$(document).ready(function() {
	       $('#myTable').DataTable({
	           "lengthMenu": [10, 25, 50, 75, 100],
	           "pagingType": "simple",
	           "pageLength": 10
	       });
		});
	</script>	
</html>