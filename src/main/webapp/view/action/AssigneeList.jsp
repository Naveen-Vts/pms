<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat,java.io.ByteArrayOutputStream,java.io.ObjectOutputStream"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import="java.time.LocalDate"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>

<spring:url value="/resources/css/action/assigneeList.css" var="assigneeList" />
<link href="${assigneeList}" rel="stylesheet" />
<spring:url value="/resources/css/action/actionCommon.css" var="actionCommon" />
<link href="${actionCommon}" rel="stylesheet" />


<title>Assignee List</title>

</head>
 
<body>
  <%

  List<Object[]> AssigneeList=(List<Object[]>)request.getAttribute("AssigneeList");
  List<Object[]> AssigneemainList=(List<Object[]>)request.getAttribute("AssigneemainList");
  SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
  SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
 
  String EmpId = ((Long)session.getAttribute("EmpId")).toString();
  
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
				
					<h5 class="card-header">Assigned List</h5>
					
					<div class="card-body">

						<div class="data-table-area mg-b-15">
							<div class="container-fluid">
								<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
									<div class="sparkline13-list">

										<div class="sparkline13-graph">
											<div class="datatable-dashv1-list custom-datatable-overright">
												<div id="toolbar">
											
													<span class="badge">PDC COLOR CODES:</span>
												<span class="badge badge-primary  p-2">PDC ON TODAY</span>
												<span class="badge badge-success p-2">UPCOMING PDC</span>
												<span class="badge badge-danger p-2">MISSED PDC</span>
												</div>
										<table class="table table-bordered table-hover table-striped table-condensed" id="myTable12" >

													<thead>

														<tr>
															<th>SN</th>
															<th>Action Id</th>
															<th >Action Item</th>
															<th class="width-110px">PDC</th>
															<th class="width-110px">Assigned Date</th>								
														 	<th >Assigner</th>	
														 	<th>Remarks</th>
														 	<th class="width-140px">Action</th>
														</tr>
													</thead>
													<tbody>
														<%int count=1;
														if(AssigneemainList!=null&&AssigneemainList.size()>0){	
										         	for (Object[] obj :AssigneemainList) {
										   			   %>
														<tr>
															<td class="center"><%=count %></td>
															<td><%=obj[9]!=null?StringEscapeUtils.escapeHtml4(obj[9].toString()):" - " %></td>
															<td>
															<%if(obj[5]!=null && obj[5].toString().length()>75){ %>
																<%=obj[5].toString().substring(0, 75) %>
																<textarea style="display: none!important;" id="td<%=obj[0].toString()%>"><%=obj[5].toString()%></textarea>
																 <span class="modified-span" onclick="showAction('<%=obj[0].toString()%>','<%=obj[9].toString()%>')">show more..</span>
															<%}else { %>
																<%=obj[5]!=null?obj[5].toString():" - " %>
															<%} %>
															</td>
															<td class="text-center"><span <%if(LocalDate.now().toString().equalsIgnoreCase(obj[4].toString())) {%>class="text-primary spans"<%} %>
															<%if(LocalDate.now().isAfter(LocalDate.parse(obj[4].toString()))){ %>class="text-danger spans"<%} %>
															<%if(LocalDate.now().isBefore(LocalDate.parse(obj[4].toString()))){ %>class="text-success spans"<%} %>
															>
															<%=obj[4]!=null?sdf.format(obj[4]):""%></span>
															</td>
															<td class="text-center"><%=obj[3]!=null?sdf.format(obj[3]):""%></td>
															<td class="width-20"><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()):" - "%>, <%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()):" - "%></td>
															<td><%if(obj[7]!=null){%><%=StringEscapeUtils.escapeHtml4(obj[7].toString()) %><%}else{ %>-<%} %></td>
															
															
															<td class="left width">		
																
															<form name="myForm1" id="myForm1" action="ActionSubLaunch.htm" method="POST" 
																	class="d-inline">

																	<button class="btn btn-sm editable-click" name="sub" value="Details" 	>
																		<div class="cc-rockmenu">
																			<div class="rolling">
																				<figure class="rolling_icon">
																					<img src="view/images/preview3.png">
																				</figure>
																				<span>Details</span>
																			</div>
																		</div>
																	</button>
												                    <input type="hidden" name="Assigner" value="<%=obj[1]%>,<%=obj[2]%>"/>													
                                                                    <input type="hidden" name="ActionLinkId" value="<%=obj[8]%>"/>
																	<input type="hidden" name="ActionMainId" value="<%=obj[0]%>"/>
																	<input type="hidden" name="ActionNo" value="<%=obj[9]%>"/>
																	<input type="hidden" name="ActionAssignid" value="<%=obj[10]%>"/>
																	<input type="hidden" name="ProjectId" value="<%=obj[14]%>"/>
 																	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
																	<%if(obj[13]!=null && obj[11]!=null && EmpId.equalsIgnoreCase(obj[11].toString()) && Integer.valueOf(obj[13].toString())< 5){ %>
																		      <button type="submit"  class="btn btn-sm editable-click" name="Action" value="ReAssign"  formaction="ActionLaunch.htm" formmethod="POST"  >
																				<div class="cc-rockmenu">
																				 <div class="rolling">	
																				 <figure class="rolling_icon">
																				 	<img src="view/images/repeat.png"  >
															                       </figure>
															                        <span> Distribute</span>
															                      </div>
															                     </div>
															                  </button> 

																		    <button type="submit"  class="btn btn-sm editable-click" name="ActionAssignid" value="<%=obj[10]%>" formtarget="blank" title="Action Tree"    formaction="ActionTree.htm" formmethod="POST"  >
																				<div class="cc-rockmenu">
																				 <div class="rolling">	
																				 <figure class="rolling_icon">
																				 	<img src="view/images/tree.png"  >
															                       </figure>
															                        <span> Action Tree</span>
															                      </div>
															                     </div>
															                  </button> 

													                  <%}%>
																</form> 		
															</td>
														</tr>
												<% count++; } }else{%>
												<tr>
													<td colspan="8" class="text-center">No List Found</td>
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
      <div class="modal-header" class="div-height50">
        <h5 class="modal-title" id="exampleModalLongTitle">Action</h5>
        <button type="button" class="close text-danger" data-dismiss="modal" aria-label="Close">
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
	
	 $(document).ready(function(){
		  $("#myTable12").DataTable({
		 "lengthMenu": [  5,10,25, 50, 75, 100 ],
		 "pagingType": "simple",
		 "pageLength": 10

		});
	});
	
		
		function showAction(a,b){
			/* var y=JSON.stringify(a); */
			var y=$('#td'+a).val();
			console.log(a);
			$('#modalbody').html(y);
			$('#header').html(b);
			$('#exampleModalCenter').modal('show');
		}
	</script>  


</body>
</html>