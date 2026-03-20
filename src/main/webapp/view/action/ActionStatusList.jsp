<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat,java.io.ByteArrayOutputStream,java.io.ObjectOutputStream"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/action/actionStatusList.css" var="actionStatusList" />
<link href="${actionStatusList}" rel="stylesheet" />
<spring:url value="/resources/css/action/actionCommon.css" var="actionCommon" />
<link href="${actionCommon}" rel="stylesheet" />

<title>Assignee List</title>

</head>
 
<body>
  <%
  FormatConverter fc=new FormatConverter(); 
  SimpleDateFormat sdf=fc.getRegularDateFormat();
  SimpleDateFormat sdf1=fc.getSqlDateFormat();
  
  
  List<Object[]> AssigneeList=(List<Object[]>)request.getAttribute("StatusList");
  String fdate=(String)request.getAttribute("fdate");
  String tdate=(String)request.getAttribute("tdate");  
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

    <br/>


<div class="container-fluid">
		<div class="row">
			<div class="col-md-12">
				<div class="card shadow-nohover">
					<div class="card-header mb-2 ">  

					<div class="row">
						<h4 class="col-md-4">Assigned List</h4>  
							<div class="col-md-8 float-right" >
					   			<form method="post" action="ActionStatusList.htm" name="dateform" id="dateform">
					   				<table border="0">
					   					<tr>
					   						<td >
					   							<label class="control-label label">PDC From:</label>
					   						</td>
					   						<td class="tdwidth">
					   							<input  class="form-control"  data-date-format="dd/mm/yyyy" id="fdate" name="fdate"  required="required"  value="<%=sdf.format(sdf1.parse(fdate))%>">
					   						</td>
					   						<td>
					   							<label class="control-label label-font17">PDC To:</label>
					   						</td>
					   						<td class="width200">
					   							<input  class="form-control "  data-date-format="dd/mm/yyyy" id="tdate" name="tdate"  required="required"  value="<%=sdf.format(sdf1.parse(tdate))%>">
					   						</td>
					   						<td>
					   							<input type="submit" value="SUBMIT" class="btn  btn-sm add "/>
					   						</td>
					   					</tr>   					   				
					   				</table>
					   				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
					   			</form>
		   					</div>
		   				</div>	   							

					</div>
						
					
    					<div class="data-table-area mg-b-15">
							<div class="container-fluid">
								<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
									<div class="sparkline13-list">
										<div class="sparkline13-graph">
											<div class="datatable-dashv1-list custom-datatable-overright">
												<div id="toolbar">
													
												</div>
											<table class="table table-bordered table-hover table-striped table-condensed" id="myTable12" >

													<thead>

														<tr>
															<th>SN</th>
															<th>Action No</th>
															<th>Action Item</th>
															<th >Assigned Date</th>
															<th class="width-110px" >PDC</th>																							
														 	<th class="width-140px">Assigner</th>
														 	<th class="width-140px"> Assignee</th>	
														 	<th >Status</th>
														 	<th class="width-115px">Progress</th>
														 	<th> Action</th>
														</tr>
													</thead>
													<tbody>
														<%int count=1;
															if(AssigneeList!=null&&AssigneeList.size()>0)
															{
												   					for (Object[] obj :AssigneeList) 
												   					{ %>
												   					
																	<tr>
																		<td class="center"><%=count %></td>
																		<td>
																		<form action="ActionDetails.htm" method="POST" >
																			   <button  type="submit" class="btn btn-outline-info"   ><%=obj[9]!=null?StringEscapeUtils.escapeHtml4(obj[9].toString()):"" %></button>
																			   <input type="hidden" name="ActionLinkId" value="<%=obj[10]%>"/>
																	           <input type="hidden" name="Assignee" value="<%=obj[1]%>,<%=obj[2]%>"/>
																	           <input type="hidden" name="ActionMainId" value="<%=obj[0]%>"/>
																	           <input type="hidden" name="ActionNo" value="<%=obj[9]%>"/>
																	           <input type="hidden" name="ActionAssignId" value="<%=obj[12]%>"/>
 																			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
																			</form> 
                                                                        </td>
																		<td> <%=obj[5]!=null?obj[5].toString():" - "%></td>
																		<td><%=obj[3]!=null ? sdf.format(obj[3]):" - "%></td>
																		<td><%=obj[4]!=null ? sdf.format(obj[4]):" - "%></td>																		
																		<td><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()):" - "%>, <%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()):" - "%></td>
																	  	<td><%=obj[7]!=null?StringEscapeUtils.escapeHtml4(obj[7].toString()):" - "%>, <%=obj[8]!=null?StringEscapeUtils.escapeHtml4(obj[8].toString()):" - "%></td>
																		<td>
																			<%if(obj[6]!=null && obj[6].toString().equalsIgnoreCase("A"))
																			{%>
																				Assigned
																			<%}
																			else if(obj[6]!=null && obj[6].toString().equalsIgnoreCase("F"))
																			{%>
																				Forwarded
																			<% }
																			else if(obj[6]!=null && obj[6].toString().equalsIgnoreCase("B"))
																			{%>
																				Sent Back
																			<%}
																			else if(obj[6]!=null && obj[6].toString().equalsIgnoreCase("C"))
																			{%>
																				Completed
																			<%}	%>												
																		</td>
																		<td class="td-width8"><%if(obj[11]!=null){ %>
															            <div class="progress div-progress" >
															            <div class="progress-bar progress-bar-striped width-<%=obj[11]%>" role="progressbar" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
															            <%=StringEscapeUtils.escapeHtml4(obj[11].toString())%>
															            </div> 
															            </div> <%}else{ %>
															            <div class="progress div-progress">
															            <div class="progress-bar progressbar" role="progressbar"  >
															             Not Yet Started .
															            </div>
															            </div> <%} %></td>		
															            <td>  
															            <form action="###" method="POST" >
															            	<button type="submit"  class="btn btn-sm editable-click" name="ActionAssignid" value="<%=obj[12]%>" formtarget="blank" title="Action Tree"  formaction="ActionTree.htm" formmethod="POST"  >
																			<div class="cc-rockmenu">
																				 <div class="rolling">	
																					   <figure class="rolling_icon">
																					 	<img src="view/images/tree.png"  >
																                       </figure>
															                        	<span> Action Tree</span>
															                      </div>
															                  </div>
																			</button> 
																			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
																		 </form>  	
															            </td>						
																	</tr>
																<% count++;
																	}									   					
															}%>
													</tbody>
												</table>												
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<br>
						<div class="card-footer" align="right">&nbsp;</div>
					</div>
				</div>
			</div>
		</div>

	
			
		

	
<script type='text/javascript'> 
function submitForm(frmid)
{ 
  document.getElementById(frmid).submit(); 
} 




$('#fdate').daterangepicker({
	"singleDatePicker" : true,
	"linkedCalendars" : false,
	"showCustomRangeLabel" : true,
	"cancelClass" : "btn-default",
	"maxDate" : new Date(),
	showDropdowns : true,
	locale : {
		format : 'DD-MM-YYYY'
	}
});





$('#tdate').daterangepicker({
	"singleDatePicker" : true,
	"linkedCalendars" : false,
	"showCustomRangeLabel" : true,
	"cancelClass" : "btn-default",
	"maxDate" : new Date(),
	
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



function Prints(myfrm){
	
	 var fields = $("input[name='btSelectItem']").serializeArray();
	 	 if (fields.length === 0){
		  myalert();
			 event.preventDefault();
				return false;
		}
		  return true;
}
</script>


</body>
</html>