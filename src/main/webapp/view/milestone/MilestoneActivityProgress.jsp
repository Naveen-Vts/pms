<%@page import="java.time.LocalDate"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<spring:url value="/resources/pdfmake/xlsx.full.min.js" var="excelConverter" />
<script src="${excelConverter}"></script>
<spring:url value="/resources/css/sweetalert2.min.css" var="sweetalertCss" />
<spring:url value="/resources/js/sweetalert2.min.js" var="sweetalertJs" />
<link href="${sweetalertCss}" rel="stylesheet" />
<script src="${sweetalertJs}"></script>
<jsp:include page="../static/header.jsp"></jsp:include>
  <spring:url value="/resources/css/milestone/milestoneActivityProgress.css" var="milestoneActivityProgress" />     
<link href="${milestoneActivityProgress}" rel="stylesheet" />

</head>
<body>
	<%
	List<Object[]> projectList = (List<Object[]>)request.getAttribute("projectList");
	List<Object[]> totalAssignedMainList = (List<Object[]>)request.getAttribute("totalAssignedMainList");
	List<Object[]> totalAssignedSubList = (List<Object[]>)request.getAttribute("totalAssignedSubList");
	Map<Long, List<Object[]>> progressListMap = (Map<Long, List<Object[]>>)request.getAttribute("progressListMap");
	String projectId = (String)request.getAttribute("projectId");
	String fromDate = (String)request.getAttribute("fromDate");
	String toDate = (String)request.getAttribute("toDate");
	String sancDate = (String)request.getAttribute("sancDate");
	String project = "";
	FormatConverter fc = new FormatConverter();
	
	totalAssignedSubList.sort((o1, o2) -> {
		
	    // Null safety, if needed
	    Long p1 = o1[o1.length - 1] != null ? Long.parseLong(o1[o1.length - 1].toString()) : 0;
	    Long p2 = o2[o2.length - 1] != null ? Long.parseLong(o2[o2.length - 1].toString()) : 0;
	    int result = p1.compareTo(p2);
	    if (result != 0) return result;

	    Long s1 = o1[o1.length - 4] != null ? Long.parseLong(o1[o1.length - 4].toString()) : 0;
	    Long s2 = o2[o2.length - 4] != null ? Long.parseLong(o2[o2.length - 4].toString()) : 0;
	    result = s1.compareTo(s2);
	    if (result != 0) return result;

	    Integer t1 = o1[o1.length - 2] != null ? Integer.parseInt(o1[o1.length - 2].toString()) : 0;
	    Integer t2 = o2[o2.length - 2] != null ? Integer.parseInt(o2[o2.length - 2].toString()) : 0;
	    result = t1.compareTo(t2);
	    if (result != 0) return result;

	    String u1 = o1[o1.length - 3] != null ? o1[o1.length - 3].toString() : "";
	    String u2 = o2[o2.length - 3] != null ? o2[o2.length - 3].toString() : "";
	    return u1.compareTo(u2);
	});
	
	String fromDateR = fc.sdfTordf(fromDate);
	String toDateR = fc.sdfTordf(toDate);
	
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
    	<div class="card shadow-nohover">
            <div class="card-header">
            	<form method="post" action="MilestoneActivityProgress.htm" name="myform" id="myform">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
					<input type="hidden" name="oldProjectId" value="<%=projectId%>">
					<input type="hidden" name="sancDate" value="<%=sancDate%>">
	                <div class="row">
	                    <div class="col-md-6">
	                        <h5>Milestone Progress List</h5> 
	                    </div> 
	                    <div class="col-md-6">
	                    	<table id="headertable">
                    			<tr>
                    				<th width="10%" class="right"><label class="form-label">Project:</label></th>
                    				<td width="34%">
                    					<select class="form-control selectdee" name="projectId" id="projectId" onchange="this.form.submit()" >
											<option selected disabled>---Select---</option>
											<%if(projectList!=null && projectList.size()>0) {
												for(Object[] obj : projectList) {
													String projectshortName=(obj[17]!=null)?" ("+obj[17].toString()+") ":""; %>
													<option value="<%=obj[0]%>" <%if(projectId.equalsIgnoreCase(obj[0]+"")) { project = obj[4]+projectshortName; %>selected<%} %> >
														<%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - "%> <%=projectshortName!=null?StringEscapeUtils.escapeHtml4(projectshortName): " - " %>
													</option>
											<%} }%>
										</select>
                    				</td>
                    				<th width="10%" class="right"><label class="form-label">From:</label></th>
                    				<td width="18%">
                    					<input type="text" class="form-control " name="fromDate" id="fromDate" value="<%=fromDateR %>" onchange="this.form.submit()" >
                    				</td>
                    				<th width="10%" class="right"><label class="form-label">To:</label></th>
                    				<td width="18%">
                    					<input type="text" class="form-control " name="toDate" id="toDate" value="<%=toDateR %>" onchange="this.form.submit()">
                    				</td>
                    				<td width="18%">
                    					<button type="button" class="btn btn-sm bg1" name="action" value="GenerateExcel" onclick="exportMultipleTablesToExcel()" formtarget="blank" data-toggle="tooltip" data-placement="top" title="Excel Report" >
											<i  class="fa fa-file-excel-o fw-1" aria-hidden="true"></i>
									  	</button>
                    				</td>
                    			</tr>
	                    	</table>
	                    </div>
	                </div>
	        	</form>        
            </div>
            <div class="card-body">
            	<div class="row">
					<div class="col-md-12">
						
						<div class="table-responsive">
							<table class="table table-bordered table-hover table-striped table-condensed" id="myTable">
					        	<thead class="center">
	                                <tr>
	                                    <th width="3%">SN</th>
	                                    <!-- <th width="12%">Project</th> -->
	                                    <th width="3%">Main</th>
	                                    <th width="12%">Sub</th>
	                                    <th width="27%">Activity</th>
	                                    <th width="17%">Progress By</th>
	                                    <th width="8%">Progress Date</th>
	                                    <th width="10%">Progress</th>
	                                    <th width="20%">Remarks</th>
	                                </tr>
					      		</thead>
					          	<tbody>
					            	<%-- <%int slno = 0;
					            	if(totalAssignedMainList!=null && totalAssignedMainList.size()>0) { 
					            		totalAssignedMainList = totalAssignedMainList.stream().filter(e -> Integer.parseInt(e[8].toString())>0 ).collect(Collectors.toList());
						            	for(Object[] obj : totalAssignedMainList) { %>
		                            		<tr>
		                            			<td class="center"><%=++slno %></td>
		                            			<td class="center">M<%=obj[2]%></td>
		                            			<td class="center">-</td>
		                            			<td><%=obj[3]%></td>
		                            			<td class="center"><%=obj[6]!=null?fc.sdfTordf(obj[6].toString()):"-" %></td>
		                            			<td class="center"><%=obj[7]!=null?fc.sdfTordf(obj[7].toString()):"-" %></td>
		                            			<td class="center"><%=obj[13]%></td>
		                            			<td>
		                            				<%if(!obj[8].toString().equalsIgnoreCase("0")){ %>
														<div class="progress" style="background-color:#cdd0cb !important;height: 1.4rem !important;">
															<div class="progress-bar progress-bar-striped
																<%if(obj[12].toString().equalsIgnoreCase("2")){ %>
																	bg-success
																<%} else if(obj[12].toString().equalsIgnoreCase("3")){ %>
																	bg-info
																<%} else if(obj[12].toString().equalsIgnoreCase("4")){ %>
																	bg-danger
																<%} else if(obj[12].toString().equalsIgnoreCase("5")){ %>
																	bg-warning
																<%}  %>
																" role="progressbar" style=" width: <%=obj[8] %>%;  " aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
																<%=obj[8] %>
															</div> 
														</div> 
													<%}else{ %>
														<div class="progress" style="background-color:#cdd0cb !important;height: 1.4rem !important;">
															<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
																Not Started
															</div>
														</div> 
													<%} %>
		                            			</td>
		                            		</tr>
						        	<%} }%> --%>
						        	
						            <%int slno = 0;
						            if(totalAssignedSubList!=null && totalAssignedSubList.size()>0) { 
						            	totalAssignedSubList = totalAssignedSubList.stream().filter(e -> Integer.parseInt(e[8].toString())>0 ).collect(Collectors.toList());
						            	for(Object[] obj : totalAssignedSubList) { 
						            		List<Object[]> porgressList = progressListMap.get(Long.parseLong(obj[0].toString()));
						            		for(Object[] prog : porgressList){
						            %>
		                            		<tr>
		                            			<td class="center"><%=++slno %></td>
		                            			<td class="center">M<%=obj[obj.length - 2]!=null?StringEscapeUtils.escapeHtml4(obj[obj.length-2].toString()): " - " %></td>
		                            			<td class="center"><%=obj[obj.length - 3]!=null?StringEscapeUtils.escapeHtml4(obj[obj.length-3].toString()): " - " %></td>
		                            			<td><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - "%></td>
		                            			<td><%=prog[8]!=null?StringEscapeUtils.escapeHtml4(prog[8].toString()): " - "%>, <%=prog[9]!=null?StringEscapeUtils.escapeHtml4(prog[9].toString()): " - "%></td>
		                            			<td class="center"><%=prog[3]!=null?fc.sdfTordf(prog[3].toString()):"-" %></td>
		                            			<td>
		                            				<%if(!prog[2].toString().equalsIgnoreCase("0")){ %>
														<div class="progress progressClass " >
															<div class="progress-bar progress-bar-striped width-<%=prog[2]!=null?StringEscapeUtils.escapeHtml4(prog[2].toString()): "0" %>
																" role="progressbar"  aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
																<%=prog[2]!=null?StringEscapeUtils.escapeHtml4(prog[2].toString()): " - " %>
															</div> 
														</div> 
													<%}else{ %>
														<div class="progress progressClass" >
															<div class="progress-bar noProgress" role="progressbar"   >
																Not Started
															</div>
														</div> 
													<%} %>
		                            			</td>
		                            			<td><%=prog[6]!=null?StringEscapeUtils.escapeHtml4(prog[6].toString()):"-"%></td>
		                            		</tr>
						            <%} } }%>
					         	</tbody>
					    	</table>
						</div>
					</div>	
				</div>		
            </div>
		</div>
	</div>
		 
<script>
    $(document).ready(function() {
        $('#myTable').DataTable({
            "lengthMenu": [10, 25, 50, 75, 100],
            "pagingType": "simple",
            "pageLength": 10
        });
    });
    
 	// Initialize other datepickers without affecting the week highlighting
    $('#fromDate').daterangepicker({
        "singleDatePicker": true,
        "linkedCalendars": false,
        "showCustomRangeLabel": true,
        "startDate": new Date('<%=fromDate%>'),
        "endDate": new Date('<%=toDate%>'),
        "minDate": new Date('<%=sancDate%>'),
        "maxDate": new Date('<%=LocalDate.now()%>'),
        "cancelClass": "btn-default",
        showDropdowns: true,
        locale: {
            format: 'DD-MM-YYYY'
        }
    });
    $('#fromDate').on('change', function(){
    	$('#toDate').daterangepicker({
    	    "singleDatePicker": true,
    	    "linkedCalendars": false,
    	    "showCustomRangeLabel": true,
    	    "startDate": new Date('<%=toDate%>'),
    	    "endDate": new Date('<%=toDate%>'),
    	    "minDate": $("#fromDate").val(),
    	    "maxDate": new Date('<%=LocalDate.now()%>'),
    	    "cancelClass": "btn-default",
    	    showDropdowns: true,
    	    locale: {
    	        format: 'DD-MM-YYYY'
    	    }
    	});
    });
    $('#toDate').daterangepicker({
        "singleDatePicker": true,
        "linkedCalendars": false,
        "showCustomRangeLabel": true,
        "startDate": new Date('<%=toDate%>'),
        "endDate": new Date('<%=toDate%>'),
        "minDate": $("#fromDate").val(),
        "maxDate": new Date('<%=LocalDate.now()%>'),
        "cancelClass": "btn-default",
        showDropdowns: true,
        locale: {
            format: 'DD-MM-YYYY'
        }
    });

    
    function exportMultipleTablesToExcel() {

   	  Swal.fire({
   	    title: 'Please wait...',
   	    text: 'Preparing your Excel file',
   	    allowOutsideClick: false,
   	    didOpen: () => Swal.showLoading()
   	  });

   	  setTimeout(() => {

   	    var table = $('#myTable').DataTable();
   	    var wb = XLSX.utils.book_new();

   	    var wsData = [];

   	    // 1️⃣ Headers
   	    var headers = [];
   	    $('#myTable thead th').each(function () {
   	      headers.push($(this).text().trim());
   	    });
   	    wsData.push(headers);

   	    // 2️⃣ ALL rows (pagination ignored)
   	    table.rows({ search: 'applied' }).every(function () {
   	      var row = [];
   	      $(this.node()).find('td').each(function () {
   	        row.push($(this).text().replace(/\s+/g, ' ').trim());
   	      });
   	      wsData.push(row);
   	    });

   	    // 3️⃣ Create Excel sheet
   	    var ws = XLSX.utils.aoa_to_sheet(wsData);
   	    XLSX.utils.book_append_sheet(wb, ws, "Milestone Progress");

   	    var projectname = $('#projectId :selected').text().trim();
   	    var fromdate = $('#fromDate').val();
   	    var todate = $('#toDate').val();

   	    XLSX.writeFile(
   	      wb,
   	      "Mil_Progress_" + projectname + "_" + fromdate + "_" + todate + ".xlsx"
   	    );

   	    Swal.close();

   	  }, 500);
   	}
</script>   
</body>
</html>