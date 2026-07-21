<%@page import="java.util.Locale"%>
<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.util.List"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@page import="java.time.format.DateTimeFormatter"%>
	<%@page import="com.vts.pfms.FormatConverter"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/committeeModule/CommitteeApprovalList.css" var="CommitteeApprovalList" />
<link href="${CommitteeApprovalList}" rel="stylesheet" />
<title></title>
</head>
<body>

	<%
	FormatConverter fc = new FormatConverter();
	SimpleDateFormat sdf = fc.getRegularDateFormat();
	SimpleDateFormat sdf1 = fc.getSqlDateFormat();
	SimpleDateFormat inputFormat = new SimpleDateFormat("ddMMMyyyy",Locale.ENGLISH);
	SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
		String frmDt=(String)request.getAttribute("frmDt");
		String toDt=(String)request.getAttribute("toDt");
		String redirectedvalue=(String)request.getAttribute("redirectedvalueForward");
		List<Object[]>  EnoteApprovalPendingList=(List<Object[]>)request.getAttribute("EnoteApprovalPendingList");
		List<Object[]> EnoteApprovedList=(List<Object[]>)request.getAttribute("eNoteApprovalList");
	
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
<div class="container-fluid" >
	<div class="row">
		<div class="col-md-12">	
		   
			<div class="card shadow-nohover">	
						
					<div class="card-header">						
						<div class="row">										
							<div class="col-md-12"><h3 class="mt-n5 committeeApprovalColor">Committee Approval List 
						</h3>
						
							</div>	
																	
						
						</div>
					</div>
					
		<div class="card-body">
		<!-- tabList  -->
		<ul class="nav nav-pills mb-3 pendingListStyle" id="pills-tab" role="tablist">
		  <li class="nav-item w-50">
		    <div class="nav-link active text-center" id="eNotePendingtab" data-toggle="pill" data-target="#pills-OPD" role="tab" aria-controls="pills-OPD" aria-selected="true">
			   <span>Committee Pending List 
				   <span class="badge badge-danger badge-counter count-badge ml-2">
				 		<%if( EnoteApprovalPendingList!=null && EnoteApprovalPendingList.size()>99){ %>
				   			99+
				   		<%}else{ %>
				   			<%=EnoteApprovalPendingList.size()%>
						<%} %>		   			
				  </span>  
				</span> 
		    </div>
		  </li>
		  <li class="nav-item w-50">
		    <div class="nav-link text-center" id="eNoteApprovedtab" data-toggle="pill" data-target="#pills-IPD" role="tab" aria-controls="pills-IPD" aria-selected="false">
		    	 <span>Committee Approved List     
				   <span class="badge badge-danger badge-counter count-badge ml-2">
				   	<%if(EnoteApprovedList!=null && EnoteApprovedList.size()>99){ %>
				   		99+
				   		<%}else{ %>
				   		<%=EnoteApprovedList.size()%>
						<%} %>	   			
				   </span>  
				</span> 
		    
		    
		    </div>
		  </li>
		</ul>
		
		<!-- two divs for List  -->
			<div class="tab-content" id="pills-tabContent">
			<!-- pendig Div  -->
			<div class=" tab-pane  show active" id="pills-OPD" role="tabpanel" aria-labelledby="eNotePendingtab" >
					<table class="table table-bordered table-hover table-striped table-condensed " id="myTable1">
							<thead>
								<tr >
									<th class="text-center">SN</th>
									<th class="text-center">Committee ShortName</th>
									<th class="text-center">Project</th>
									<th class="text-center">Ref No & Date</th>
									<th class="text-center">Subject</th>
									<th class="text-center">Status</th>	
									<th class="text-center">Action</th>
								</tr>
							</thead>
							<tbody>
							<%
							int firstCount=0;
							if(EnoteApprovalPendingList!=null&& EnoteApprovalPendingList.size()>0){
							for(Object[] obj: EnoteApprovalPendingList) {%>
							<tr>
							<td class="text-center"> <%=++firstCount %></td>
							<td class="text-center"><%=obj[9]!=null?StringEscapeUtils.escapeHtml4(obj[9].toString()): " - " %></td>
							<td class="text-center"><%=obj[10]!=null?StringEscapeUtils.escapeHtml4(obj[10].toString()): " - " %></td>
							<td class="text-center"> 
							<%if (obj[1]!=null){%>
							<%=StringEscapeUtils.escapeHtml4(obj[1].toString()) %>
							<%}else{ %>
								-
							<%} %>
							<br>
							<%if (obj[2]!=null){%>
							<%=sdf.format(sdf1.parse(StringEscapeUtils.escapeHtml4(obj[2].toString()))) %>
							<%}else{ %>
								-
							<%} %>
							
				</td>
				<td class="tdJustify"><%if(obj[3]!=null) {%> <%= StringEscapeUtils.escapeHtml4(obj[3].toString())%><%}else{%> -<%} %> </td>
				<td>
				<div align="center" class="mt-1">
				<form action="#">
				<%
				   String colorCode = (String) obj[7];
				   String className = "C" + colorCode.replace("#", "").toUpperCase();
				%>
				<button type ="submit"  class="btn btn-sm btn-link w-100 btn-status fw-800 <%=className%>" formaction="EnoteStatusTrack.htm" value="<%=obj[0]%>" formtarget="_blank"  data-toggle="tooltip" data-placement="top" title="Transaction History" name="EnoteTrackId"> <%=obj[6]!=null?StringEscapeUtils.escapeHtml4(obj[6].toString()): " - " %> 
				<i class="fa fa-external-link" aria-hidden="true"></i></button>
				</form>
				</div> 
				</td>
					<td>
					<div class="d-flex justify-content-evenly align-items-center">
										<!-- Committee Recommendation  -->
					<form action="CommitteeRecommendation.htm" method="post">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />     
					<input type="hidden" name="committeemainid" value="<%=obj[11].toString()%>">
					<input type="hidden" name="scheduleid" value="<%="0"%>">
					<input type="hidden" name="type" value="<%="C"%>">
					<button type="submit" class="btn btn-sm"><i class="fa fa-eye" aria-hidden="true"></i></button>
					</form>
					
					
					
					<!-- Committee Letter -->
					<form action="CommitteeConstitutionLetterDownload.htm" target="_blank">
					<button type="submit" class="btn btn-sm edit" data-toggle="tooltip" data-placement="top" title="Committee Constitution Letter"><i class="fa fa-download fs-90"></i></button>
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />     
					<input type="hidden" name="committeemainid" value="<%=obj[11].toString()%>">
					</form>
					<!-- Enote Letter  -->
					<form>
					
					</form>
					

					
					<form action="CommitteeEnotePrint.htm" target="_blank">
					<button type="submit" class="btn btn-sm edit committeeEnoteLetterDownloadStyle" data-toggle="tooltip" data-placement="top" title="Committee ENote Letter"><i class="fa fa-download fs-90"></i></button>
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />     
					<input type="hidden" name="EnoteId" value="<%=obj[0].toString()%>">
						<input type="hidden" name="type" value="C">
						<input type="hidden" name="scheduleid" value="0">
						<input type="hidden" name="committeemainid" value="<%=obj[11].toString()%>">
					</form>
					</div>
					</td>
							</tr>
							<%}} %>
							</tbody>
							</table>
				
		
			</div>
			
			<!--Approved Div  -->
			<div class="card tab-pane " id="pills-IPD" role="tabpanel" aria-labelledby="eNoteApprovedtab" >	
		    <div class="card-body"> 
    
 <form action="#" method="post" id="myform"> 

    
        <div class="col-12 w-100">
          <div class="row d-flex align-items-center justify-content-end">
              <label  class="control-label text-center fs-16px width-80px" for="fromdate">From	</label>&nbsp;&nbsp;
              <input type="text" class="form-control input-sm mydate width-113px mt-n10" readonly="readonly" value="" id="fromdate" name="FromDate" required="required"> 
              &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
              <label class="control-label text-center fs-16px width-20px" for="todate">To</label>&nbsp;&nbsp;
              <input type="text" class="form-control input-sm mydate width-113px mt-n10" readonly="readonly" value="" id="todate" name="ToDate" required="required"> 
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                <input type="hidden" id="redirectedvalue" name="redirectedvalue" value="<%if(redirectedvalue!=null){%><%=StringEscapeUtils.escapeHtml4(redirectedvalue) %><%}%>">
          </div>
        </div>
     
      </form>
     
     <table class="table table-bordered table-hover table-striped table-condensed mt-2" id="myTable2">
							<thead>
								<tr >
									<th class="text-center width-20px">SN</th>
									<th class="text-center width-10">Committee ShortName</th>
									<th class="text-center width-5">Project</th>
									<th class="text-center width-10">Ref No & Date</th>
									<th class="text-center width-196px">Subject</th>
									<th class="text-center width-10">Status</th>	
									<th class="text-center">Action</th>
								</tr>
							</thead>
							<tbody>
						<%
						int count=0;
						if(EnoteApprovedList!=null && EnoteApprovedList.size()>0){
						for(Object[]obj:EnoteApprovedList){
						%>
						<tr>
						<td class="text-center width-20px"><%=++count %></td>
						<td class="text-center"><%=obj[10]!=null?StringEscapeUtils.escapeHtml4(obj[10].toString()): " - "%></td>
						<td class="text-center"><%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - "%></td>
						<td class="text-center"> 
							<%if (obj[1]!=null){%>
							<%=StringEscapeUtils.escapeHtml4(obj[1].toString()) %>
							<%}else{ %>
								-
							<%} %>
							<br>
							<%if (obj[2]!=null){%>
							<%=sdf.format(sdf1.parse(StringEscapeUtils.escapeHtml4(obj[2].toString()))) %>
							<%}else{ %>
								-
							<%} %>
							
							</td>
							<td class="text-center"><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - " %></td>
							<td class="tdJustify">
							
							<div align="center" class="mt-1">
							<form action="#">
							<%
							   String colorCode = (String) obj[8];
							   String className = "C" + colorCode.replace("#", "").toUpperCase();
							%>
							<button type ="submit"  class="btn btn-sm btn-link w-100 btn-status fw-800 <%=className%>" formaction="EnoteStatusTrack.htm" value="<%=obj[0]%>" formtarget="_blank"  data-toggle="tooltip" data-placement="top" title="Transaction History" name="EnoteTrackId"> <%=obj[7].toString() %> 
							<i class="fa fa-external-link" aria-hidden="true"></i></button>
							</form>
							</div>
							</td>
							<td class="text-center">
							<div class="d-flex justify-content-evenly align-items-center">
							<!-- Committee Letter -->
					<form action="CommitteeConstitutionLetterDownload.htm" target="_blank">
					<button type="submit" class="btn btn-sm edit" data-toggle="tooltip" data-placement="top" title="Committee Constitution Letter"><i class="fa fa-download fs-90"></i></button>
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />     
					<input type="hidden" name="committeemainid" value="<%=obj[12].toString()%>">
					</form>
							<!-- Enote Print -->
				<form action="CommitteeEnotePrint.htm" target="_blank">
					<button type="submit" class="btn btn-sm edit committeeEnoteLetterDownloadStyle" data-toggle="tooltip" data-placement="top" title="Committee ENote Letter"><i class="fa fa-download fs-90"></i></button>
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />     
					<input type="hidden" name="EnoteId" value="<%=obj[0].toString()%>">
							<input type="hidden" name="type" value="C">
						<input type="hidden" name="scheduleid" value="0">
						<input type="hidden" name="committeemainid" value="<%=obj[12].toString()%>">
					</form>
							</div>
							</td>
						</tr>
						<%}} %>
							</tbody>
							</table>
     
      </div>
			
			</div>
			
			</div>
		
					</div>
					</div>
					</div>
					</div>
					</div>
					
<script type="text/javascript">
$("#myTable1").DataTable({
    "lengthMenu": [10, 25, 50, 75, 100],
     ordering: true

});	
$("#myTable2").DataTable({
    "lengthMenu": [10, 25, 50, 75, 100],
     ordering: true

});	
$(function () {
	$('[data-toggle="tooltip"]').tooltip()
	})
	
	$('#fromdate').daterangepicker({
	"singleDatePicker" : true,
	"linkedCalendars" : false,
	"showCustomRangeLabel" : true,
	/* "minDate" :datearray,   */
	 "startDate" : new Date('<%=frmDt%>'), 
	"cancelClass" : "btn-default",
	showDropdowns : true,
	locale : {
		format : 'DD-MM-YYYY'
	}
});

var currentDate = new Date();
var maxDate = currentDate.toISOString().split('T')[0];
console.log(maxDate);
$('#todate').daterangepicker({
	"singleDatePicker" : true,
	"linkedCalendars" : false,
	"showCustomRangeLabel" : true,
	"startDate" : new Date('<%=toDt%>'), 
	"cancelClass" : "btn-default",
	showDropdowns : true,
	locale : {
		format : 'DD-MM-YYYY'
	}
});

$(document).ready(function(){
	   $('#todate').change(function(){
	       $('#myform').submit();
	    });
	});
	
$(document).ready(function(){
	   $('#fromdate').change(function(){
	       $('#myform').submit();
	    });
	});
	
$("#eNotePendingtab").click(function() {
	$("#redirectedvalue").val('');
    $("#redirectedvalue").val('eNotePending');
  });

  $("#eNoteApprovedtab").click(function() {
    $("#redirectedvalue").val('');
    $("#redirectedvalue").val('eNoteApproved');
  });
</script>
<script>
  // Check if the CountForMsgRedirect string is not null
  var countForMsgMarkerRedirect = '<%= redirectedvalue %>';
  if (countForMsgMarkerRedirect != 'null' && countForMsgMarkerRedirect==='eNotePending') {
    // Get the button element by ID
   var button = document.querySelector('[id="eNotePendingtab"]');

    // Scroll to the button element to that view
 /*    button.scrollIntoView(); */
    // Programmatically trigger a click event on the button
    if (button) {
      // Programmatically trigger a click event on the button
      
      button.click();
      
    }
  }else if(countForMsgMarkerRedirect != 'null' && countForMsgMarkerRedirect==='eNoteApproved'){
	  var button = document.querySelector('[id="eNoteApprovedtab"]');

	    // Scroll to the button element to that view
	 /*    button.scrollIntoView(); */
	    // Programmatically trigger a click event on the button
	    if (button) {
      // Programmatically trigger a click event on the button
       
      button.click();
     
    }
  }
</script>
</body>
</html>