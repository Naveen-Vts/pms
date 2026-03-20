<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat,java.io.ByteArrayOutputStream,java.io.ObjectOutputStream"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<script src="./resources/js/multiselect.js"></script>
<link href="./resources/css/multiselect.css" rel="stylesheet"/>

<spring:url value="/resources/css/action/actionSelf.css" var="actionSelf" />
<link href="${actionSelf}" rel="stylesheet" />
<spring:url value="/resources/css/action/actionCommon.css" var="actionCommon" />
<link href="${actionCommon}" rel="stylesheet" />
 
<title>New Action</title>

</head>
 
<body>
  <%
  


  List<Object[]> AssignedList=(List<Object[]>)request.getAttribute("AssignedList");
  SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
  SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
 
  
  
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

    <br />
    
    


   
   
   
   <div class="container-fluid">
		<div class="row">
			<div class="col-md-12">
				<div class="card shadow-nohover">
				
					<h5 class="card-header">Self Assigned List</h5>
					
					<div class="card-body">

						<div class="data-table-area mg-b-15">
							<div class="container-fluid">


								<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
									<div class="sparkline13-list">

										<div class="sparkline13-graph">
											<div class="datatable-dashv1-list custom-datatable-overright">

											<table class="table table-bordered table-hover table-striped table-condensed" id="myTable12" >
													
													<thead>

														<tr class="text-center">
															<th>SN</th>
															<th class="text-left width-80">Action Item</th>
															<th class="width110">PDC</th>
															<th class="width110">Assigned Date</th>									
														 	<th >Assignee</th>	
														 	<th class="width115">Progress</th>
														
														 		
														 	
														</tr>
													</thead>
													<tbody>
														<%int  count=1;
															
														 	if(AssignedList!=null&&AssignedList.size()>0){
															for(Object[] obj: AssignedList){ %>
														<tr>
															<td  class="center td-width1"><%=count %></td>
															<td class="td-modified">
															<%if(obj[5]!=null && obj[5].toString().length()>75) {%>
															<%=obj[5].toString().substring(0,75) %>
															 <span class="custom-span" onclick="showAction('<%=obj[5].toString()%>')">show more..</span>
															<%}else if(obj[5].toString().length()<=75){ %>
															<%= obj[5].toString() %>
															<%}else{ %> -- <%} %>
															</td>
															
															<td class="tdwidth8 text-center"><%=obj[4]!=null?sdf.format(obj[4]):""%></td>
															<td class="width-10 text-center "><%=obj[4]!=null?sdf.format(obj[3]):""%></td>
															<td class="width-15"><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()):" - "%>, <%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()):" - "%></td>
															<td class="width-15"><%if(obj[10]!=null){ %>
															<div class="progress div-progress">
															<div class="progress-bar progress-bar-striped width-<%=obj[10]%>" role="progressbar"  aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
															<%=StringEscapeUtils.escapeHtml4(obj[10].toString())%>
															</div> 
															</div> <%}else{ %>
															<div class="progress div-progress">
															<div class="progress-bar progressbar" role="progressbar"  >
															Not Yet Started .
															</div>
															</div> <%} %></td>
													
		
														</tr>
												<% count++; } }else{%>
												<tr>
													<td colspan="6" class="textcenter">No List Found</td>
												</tr>
												<%} %>
												</tbody>
												</table>
												
							<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" />


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
   <!-- Modal for action -->
<div class="modal fade" id="exampleModalCenter" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered" role="document">
    <div class="modal-content">
      <div class="modal-header modal-height" >
        <h5 class="modal-title" id="exampleModalLongTitle">Action</h5>
        <button type="button" class="close text-center" data-dismiss="modal" aria-label="Close" >
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body" id="modalbody">
     
      </div>
      <div align="right" id="header" class="p-2"></div>
    </div>
  </div>
</div>

  
<script>



	 



</script>




<script>
	$('#DateCompletion').daterangepicker({
			"singleDatePicker" : true,
			"linkedCalendars" : false,
			"showCustomRangeLabel" : true,
			/* "minDate" : new Date(), */
			"cancelClass" : "btn-default",
			showDropdowns : true,
			locale : {
				format : 'DD-MM-YYYY'
			}
		});
	$(function() {
		  $('#chkveg').multiselect({
		    includeSelectAllOption: true
		  });
	});
		
	 $(document).ready(function(){
		  $("#myTable12").DataTable({
		 "lengthMenu": [  5,10,25, 50, 75, 100 ],
		 "pagingType": "simple",
		 "pageLength": 10

		});
	});
	
	
	$("#OldList").hide();

	
	$("#ItemDescriptionSearchBtn").click(function(){
		   $('#OldActionNoId').empty();
		var $ItemSearch = $("#ItemDescriptionSearch").val();
		  $("#loader ").show(); 
		
		
		
		  if ($ItemSearch === ""){
			 alert("Search Content Requried");
			 $("#loader ").hide();
		  }else{
					  
			  $
				.ajax({

					type : "GET",
					url : "ActionNoSearch.htm",
					data : {
						ActionSearch : $ItemSearch
					},
					datatype : 'json',
					success : function(result) {

						var result = JSON.parse(result);
						var values = Object.values(result).map(function(key, value) {
							  return result[key,value]
							});
						var size = Object.keys(values).length;
						var s = '';
						
						if(size==0){
							document.getElementById('Message').innerHTML = " * Old Action Number doesn't Exist. Please Reset "; 
						}
						if(size!=0){
							document.getElementById('Message').innerHTML = " "; 
						}
						
						 $("#OldList").show(); 
					     $("#OldActionNoId").prop("disable",false);
					    $("#OldActionNoId").empty();
					     $.each(values, function(key, value) {   
					         $('#OldActionNoId')   
					             .append($("<option></option>")
					                        .attr("value", value[0])
					                        .text(value[1]+", "+value[2])); 
					    });
					    
						$("#loader ").hide(); 
					}
				}); 
			  
			  
			  
		  }
		}); 
	
	
	function resetSubmit(){
		event.preventDefault();
		 $("#OldList").hide(); 
	     $("#OldActionNoId").prop("disable",true);
	     document.getElementById('Message').innerHTML = " "; 

	}
	function showAction(a,b){
		$('#modalbody').html(a);
		
		$('#exampleModalCenter').modal('show');
	}	
	
	
	</script>  


</body>
</html>