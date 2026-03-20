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

<spring:url value="/resources/css/action/forwardList.css" var="forwardList" />
<link href="${forwardList}" rel="stylesheet" />
<spring:url value="/resources/css/action/actionCommon.css" var="actionCommon" />
<link href="${actionCommon}" rel="stylesheet" />

<title>Assignee List</title>

</head>
 
<body>
  <%
  


  List<Object[]> AssigneeList=(List<Object[]>)request.getAttribute("ForwardList");
  SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
  SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
  String type = (String)request.getAttribute("type");
  
  
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
				<div class="card-header">	
					<div class="row">
					<div class="col-sm-6" align="left">
						<h5 >Action Review & Close</h5>
						</div>
						<div class="col-sm-3  "></div>

									</div>		
						</div>
					</div>
					<div class="card-body">

						<div class="data-table-area mg-b-15">
							<div class="container-fluid">


								<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
									<div class="sparkline13-list">

										<div class="sparkline13-graph">
											<div class="datatable-dashv1-list custom-datatable-overright">
												<div id="toolbar" class="my-2">

														<form action="ActionForwardList.htm" method="post">
										<select class="form-control selectdee " name="Type"  required="required"  data-live-search="true" onchange="this.form.submit();">                                                     
											<option value="A" <%if("A".equalsIgnoreCase(type)){%>selected="selected" <%}%>>  All</option>	
											<option value="F" <%if("F".equalsIgnoreCase(type)){%>selected="selected" <%}%>>  Forwarded</option>
											<option value="NB" <%if("NB".equalsIgnoreCase(type)){%>selected="selected" <%}%>> Assigned</option>
										</select>	
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										
						</form>	
												</div>
									
											<table class="table table-bordered table-hover table-striped table-condensed" id="myTable12" >
													
													<thead>

														<tr class="text-center">
															<th>SN</th>
															<th>Action Id</th>
															<th class="text-left">Action Item</th>
															<th class="width-115px">PDC</th>
															<th >Assigned Date</th>								
														 	<th >Assignee</th>
														 	<th class="width-115px">Progress</th>		
														 	<th class="width-140px">Action</th>	
														 	
														</tr>
													</thead>
													<tbody>
														<%int  count=1;
														 	if(AssigneeList!=null&&AssigneeList.size()>0){
															for(Object[] obj: AssigneeList){%>
															<tr>
															<td class="center"><%=count%></td>
															<td><%=obj[14]!=null?StringEscapeUtils.escapeHtml4(obj[14].toString()):" - " %></td>
															<td>
															<textarea id="td<%=obj[0].toString()%>" style="display: none;"><%=obj[5].toString()%></textarea>
															<%if(obj[5]!=null && obj[5].toString().length()<75) {%>
															<%=obj[5].toString() %>
															<%}else{ %>
															<%=obj[5].toString().substring(0,75) %>&nbsp;&nbsp;<span class="custom-span" onclick="showAction('<%=obj[0].toString()%>','<%=obj[14].toString()%>')">show more</span>
															<%} %>
															</td>
															<td class="text-center"><%=obj[4]!=null?sdf.format(obj[4]):" - "%></td>
															<td class="width-10 text-center"><%=obj[3]!=null?sdf.format(obj[3]):" - "%></td>
															<td><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()):" - "%>, <%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()):" - "%></td>
															<td><%if(obj[11]!=null){%>
															<div class="progress div-progress">
															<div class="progress-bar progress-bar-striped width-<%=obj[11]%>" role="progressbar"  aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
															<%=StringEscapeUtils.escapeHtml4(obj[11].toString())%>
															</div> 
															</div><%}else{ %>
															<div class="progress div-progress">
															<div class="progress-bar progressbar" role="progressbar"  >
															Not Yet Started .
															</div>
															</div> <%} %></td>

															<td class="left width1">		
																<%if(obj[6]!=null && "A".equalsIgnoreCase(obj[6].toString()) || "B".equalsIgnoreCase(obj[6].toString())||"I".equalsIgnoreCase(obj[6].toString())){%> 
																
																<form name="myForm1" id="myForm1" action="CloseAction.htm" method="POST" 
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
                                                                    <input type="hidden" name="ActionLinkId" value="<%=obj[13]%>"/>
																	<input type="hidden" name="ActionMainId" value="<%=obj[0]%>"/>
																	<input type="hidden" name="ActionNo" value="<%=obj[14]%>"/>
																	<input type="hidden" name="ActionAssignid" value="<%=obj[15]%>"/>
																	<input type="hidden" name="ActionAssignId" value="<%=obj[15]%>"/><!-- added  -->
																	<input type="hidden" name="ActionPath" value="F"><!-- added -->
																	<input type="hidden" name="ProjectId" value="<%=obj[16]%>"/>
																	<input type="hidden" name="back" value="backToReview">
 																	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
																	
																</form>
																
																
																<%}else if(obj[6]!=null && "F".equalsIgnoreCase(obj[6].toString())){%>
															<form name="myForm1" id="myForm1" action="ForwardSub.htm" method="POST" 
																	class="d-inline">

																	<button class="editable-click" name="sub" value="Details" 	>
																		<div class="cc-rockmenu">
																			<div class="rolling">
																				<figure class="rolling_icon">
																					<img src="view/images/preview3.png">
																				</figure>
																				<span>Details</span>
																			</div>
																		</div>
																	</button>
																	<button type="submit"  class="btn btn-sm editable-click" name="ActionAssignid" value="<%=obj[15]%>" formtarget="blank" title="Action Tree"  formaction="ActionTree.htm" formmethod="POST"  >
																			<div class="cc-rockmenu">
																				 <div class="rolling">	
																					   <figure class="rolling_icon">
																					 	<img src="view/images/tree.png"  >
																                       </figure>
															                        	<span> Action Tree</span>
															                      </div>
															                  </div>
																	</button> 
                                                                    <input type="hidden" name="ActionLinkId" value="<%=obj[13]%>"/>
                                                                    <input type="hidden" name="ActionNo" value="<%=obj[14]%>"/>
																	<input type="hidden" name="Assignee" value="<%=obj[1]%>,<%=obj[2]%>"/>
																	<input type="hidden" name="ActionMainId" value="<%=obj[0]%>"/>
																	<input type="hidden" name="ActionAssignId" value="<%=obj[15]%>"/>
 																	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
																</form> 
																<%}else if(obj[6]!=null && "C".equalsIgnoreCase(obj[6].toString())){%>
																<span class="badge badge-pill badge-success p-2">Closed</span>
																<%} %>		
															</td>
														</tr>
												<% count++; } }else{%>
												<tr>
													<td colspan="6" class="text-center">No List Found</td>
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
				
				
				<div class="card-footer" align="right">
								
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