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
		<spring:url value="/resources/js/excel.js" var="excel" />
		<script src="${excel}"></script>
		<spring:url value="/resources/css/action/actionLaunch.css" var="actionLaunch" />
		<link href="${actionLaunch}" rel="stylesheet" />
		<spring:url value="/resources/css/action/actionCommon.css" var="actionCommon" />
		<link href="${actionCommon}" rel="stylesheet" />
		<title>New Action</title>

	</head>
	
	<body>
		<%
		
		Object[] ActionData = (Object[])request.getAttribute("actiondata");
		List<Object[]> AllLabList = (List<Object[]>)request.getAttribute("AllLabList");
		List<Object[]> AssignedList=(List<Object[]>)request.getAttribute("AssignedList");
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
		List<Object[]> ProjectList=(List<Object[]>)request.getAttribute("ProjectList");
		Object[] projectdata = (Object[])request.getAttribute("ProjectData"); 
		List<Object[]> EmpListmodal=(List<Object[]>)request.getAttribute("EmployeeListModal");		
		String LabCode =(String)request.getAttribute("LabCode"); 
		String clusterid = (String)session.getAttribute("clusterid");
		String Onboarding = (String)request.getAttribute("Onboarding");
		String empId = ((Long)session.getAttribute("EmpId")).toString();
		String projectid=(String)request.getAttribute("projectid");
		String committeeid=(String)request.getAttribute("committeeid");
		String meettingid=(String)request.getAttribute("meettingid");
		String flag=(String)request.getAttribute("flag");
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

				<div class="container margin-bottom">
					
					<div class="card">

						<div class="card-header card-background" >
							<div class="row"> 

								<div class="col-sm-7" align="left"  >
									<h3 class ="custom-h3" align="left">
									<%if(ActionData!=null &&ActionData[2]!=null){%>
									             <%=StringEscapeUtils.escapeHtml4(ActionData[2].toString())%>
									<%}else{%>
										New Action 
									<%}%> 
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									<%if(ActionData!=null &&ActionData[6]!=null){%>
									           PDC :  <%=StringEscapeUtils.escapeHtml4(sdf.format(ActionData[6]))%>
										<%}%>
									</h3>
								</div>     
								<div class="col-sm-5 div-search" align="left">       
									<div class="input-group">
										<input type="text" class="form-control" placeholder="Search Action Id to Link Old Action" name="ItemDescription" id="ItemDescriptionSearch">
										<div class="input-group-append">
											<button class="btn btn-secondary font-icon" type="button"  id="ItemDescriptionSearchBtn">
												<i class="fa fa-search"></i>
											</button>
									</div>
								</div>
							</div>
						</div>
					</div>
					
					
					<div class="card-body">
						<form method="post"  action="ActionSubmit.htm" >
							<div class="row"> 
								<div class="col-sm-6" align="left"  >
									<div class="form-group">
										<label  >Action Item: <span class="mandatory text-danger"  >*</span>
										</label><br>
										<input class="form-control width-full " type="text"name="Item" id="Item"   maxlength="1000" required="required" placeholder="Enter Action Item" oninput="sanitizeInput(this)">
									</div>
								</div>

								<div class="col-sm-2" align="left"  >
									<div class="form-group">
										<label  >PDC: <span class="mandatory text-danger" >* </span>
										</label>
										<input class="form-control " name="DateCompletion" id="DateCompletion" required="required" placeholder="" >
									</div>
								</div>

								<div class="col-sm-4" align="left"  >
									<label > Project: 
									</label><br>
									<select class="form-control selectdee "  name="Project"  id="Project" required="required"   data-live-search="true" id="projectid" <%if(ActionData!=null && ActionData[5]!=null){%>disabled <%}%>>                                                     
										
										<%if(projectdata!=null){
										 String projectshortName=(projectdata[3]!=null)?" ( "+projectdata[3].toString()+" ) ":"";
										%>
										    <option value="<%=projectdata[0] %>" <%if(projectdata!=null && projectdata[0]!=null && projectdata[0].toString().equalsIgnoreCase(ActionData[5].toString())){%> selected="selected" <%}%>><%=projectdata[1]!=null?StringEscapeUtils.escapeHtml4(projectdata[1].toString()) + projectshortName!=null?StringEscapeUtils.escapeHtml4(projectshortName):" - ":" - "%></option>
										<%}else{%>
										<%for(Object[] obj:ProjectList){
										  String projectshortName=(obj[17]!=null)?" ( "+obj[17].toString()+" ) ":"";
										%>
											<option value="<%=obj[0] %>" ><%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()):" - "%> <%= projectshortName!=null?StringEscapeUtils.escapeHtml4(projectshortName):" - " %></option>	
										<%}}%>
										<option value="0" <%if(ActionData!=null && ActionData[5]!=null && "0".equalsIgnoreCase(ActionData[5].toString())){%> selected="selected" <%}%>>General</option>	
									</select>	
								</div>
							</div>
							<div class="row" align="center">
								<div class="col-sm-3" align="left">
									<label> Action Type : </label><br>
									
									<select class="form-control selectdee " name="Type" id="ActionType"  required="required"  data-live-search="true" >                                                     
										<option value="A" >Action</option>	
										<option value="I" >Issue</option>
										<option value="K" >Risk</option>
									</select>	
								</div>
								
								<div class="col-sm-3" align="left">
									<label> Priority : </label>
									<br>
									<select class="form-control selectdee " name="Priority"  id="Priority" required="required"  data-live-search="true" >                                                     
										<option value="H" >High</option>	
										<option value="L" >Low</option>
										<option value="M" >Medium</option>
										<option value="I" >Immediate</option>
									</select>	
								</div>
								
								<div class="col-sm-3" align="left">
									<label> Category : </label>
									<br>
									<select class="form-control selectdee " name="Category" id="Category"  required="required"  data-live-search="true" >                                                     
										<option value="T" >Technical</option>	
										<option value="F" >Finance</option>
										<option value="M" >Managerial</option>
										<option value="L" >Logistic</option>
										<option value="O" >Others</option>
									</select>	
								</div>
				
								 <div class="col-sm-3" align="left" >
									<div class="form-group" id="OldList">
										<label > Old Action Id : </label><br>
										<select class="form-control selectdee width-full " name="OldActionNo" id="OldActionNoId" hidden="hidden" data-live-search="true"  ></select>
									</div>
									<b id="Message" class="custom-b" ></b>
								</div> 
							</div>
							<div class="row" align="center">
								<div class="col-sm-4" align="left"  ></div>
								<div class="col-sm-4" align="center"  ><br>
								            <input type="hidden" id="ActionLevel" <%if(ActionData!=null && ActionData[4]!=null){ %> value="<%=ActionData[4]%>" <%}%>>
									        <input type="hidden"  id="ActionPraentId" <%if(ActionData!=null && ActionData[1]!=null){ %> value="<%=ActionData[1]%>" <%}%>>
									        <input type="hidden"  id="Actiontype" <%if(ActionData!=null && ActionData[8]!=null){ %> value="<%=ActionData[8]%>" <%}%>>
									        <input type="hidden" id="MainId" <%if(ActionData!=null && ActionData[7]!=null){ %> value="<%=ActionData[7]%>" <%}%>>
									        <input type="button" id="Actionsubmit" class="btn  btn-sm submit margin-top10"  value="SUBMIT"/>
									        <input type="hidden" id="Actionscheduleid" <%if(ActionData!=null && ActionData[9]!=null){ %> value="<%=ActionData[9]%>" <%}else{%> value="0" <%}%>>									
										    <button  class="btn  btn-sm back margin-top10"  onclick="resetSubmit()" >Reset</button>
										    <%if(flag==null){ %>
										    <%if(Onboarding!=null && "Yes".equalsIgnoreCase(Onboarding)){%>
													<a class="btn btn-info btn-sm  back margin-top10"   href="OnBoarding.htm">Back</a>
													<%}else{%>
													<a class="btn btn-info btn-sm  back margin-top10"   href="MainDashBoard.htm">Back</a>
										    <%}%>
										    <%}else{ %>
										         <a class="btn btn-info btn-sm  back margin-top10"   href="MeettingAction.htm?projectid=<%=projectid%>&committeeid=<%=committeeid %>&meettingid=<%=meettingid %>&Empid=<%=empId %>">Back</a>
										    <%} %>
										    &nbsp;&nbsp;
									        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>         				
									
								</div>			  
							</div>			    
						</form>
					</div>
				</div>
			</div>   
		</div>
		
		
		
		
		<div class="container-fluid">
			<div class="row">
				<div class="col-md-12">
					<div class="card shadow-nohover">
						
						<div class="card-header">
						<div class="row">
						<div class="col-md-3"><h4><b>Action Assigned List</b></h4></div>
						
						<div class="col-md-9" align="right">
						<%if(Onboarding!=null && "Yes".equalsIgnoreCase(Onboarding)){%>
							<form action="ActionMainExcelUpload.htm" method="post" enctype="multipart/form-data">
								  	<table>
									  	<tr>
											<td align="left"><h6>Download Excel : &nbsp;<button formaction="ActionMainExcelUpload.htm" formmethod="post" formnovalidate="formnovalidate" name="Action" value="GenerateExcel"><i class="fa fa-file-excel-o text-success" aria-hidden="true" ></i></button></h6></td>
											<td align="right"><h6>&nbsp;&nbsp;&nbsp;&nbsp;	Upload Excel :&nbsp;&nbsp;&nbsp;&nbsp;
											<input type="file" id="excel_file" name="filename" required="required"  accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"></h6></td>							
										 </tr>
								  	</table>	
								    <input type="hidden" name="${_csrf.parameterName}"value="${_csrf.token}" />
									<div class="modal fade" id="exampleModalLong"  tabindex="-1" role="dialog" aria-labelledby="exampleModalLongTitle" aria-hidden="true" >
									  <div class="modal-dialog modal-xl" role="document">
									    <div class="modal-content"  >
									      <div class="modal-header">
									        <h5 class="modal-title" id="exampleModalLongTitle">Action Item Details</h5>
									        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
									          <span aria-hidden="true">&times;</span>
									        </button>
									      </div>
									      <div class="modal-body modal-maxheight"  overflow-y:auto;">
									             <table class="table table-bordered table-hover table-striped table-condensed" id="myTable1"> </table>
									      </div>
									      <div class="modal-footer" align="center">
									       	    <div align="center">
									        			<button type="submit" onclick="return confirm('Are you sure to submit?')"  class="btn btn-sm add" name="Action" value="UploadExcel"> Upload</button>
									      		</div>
									       </div>
									    </div>
									  </div>
									</div>
							</form> 
						<%}%>		
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
													
													<table class="table table-bordered table-hover table-striped table-condensed" id="myTable12" >
													<thead>

														<tr class="text-center">
															<th>SN</th>
															<th class="text-left">Action Item</th>
															<th class="width-110px" >PDC</th>
															<th class="width-110px">Assigned Date</th>									
														 	<th>Assignee</th>	
														 	<th class="th-width"> Progress</th>
														 	<th class="width-110px"> Is Seen</th>
														 	<th> Action</th>
														</tr>
													</thead>
													<tbody>
														<%	int  count=1;
														 	if(AssignedList!=null && AssignedList.size()>0){
															for(Object[] obj: AssignedList){ %>
															<tr>
															<td  class="center td-width1"><%=count %></td>
															<td class="td-modified"><%= obj[5]!=null?obj[5].toString():"-" %></td>
															<td class="width-10 text-center" ><%=obj[4]!=null?StringEscapeUtils.escapeHtml4(sdf.format(obj[4])):" - " %></td>
															<td class="width-10 text-center"><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(sdf.format(obj[3])):" - "%></td>
															<td ><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()):" - "%>, <%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()):" - "%></td>
															<td class="width-10"><%if(obj[7]!=null  && !obj[7].toString().equalsIgnoreCase("0")){ %>
															<div class="progress div1" >
															<div class="progress-bar progress-bar-striped width-<%=obj[7]%>" role="progressbar"  aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
															<%=obj[7]!=null?StringEscapeUtils.escapeHtml4(obj[7].toString()):"-"%>
															</div> 
															</div> <%}else{ %>
															<div class="progress div-progress">
															<div class="progress-bar div-progressbar" role="progressbar"   >
																0
															</div>
															</div> <%} %>
															</td>
															<td class="talign" >
														
																	<%if(Integer.parseInt(obj[8].toString())==1){ %>
																		<p class="text-success">Seen</p>																		
																	<%}else if(Integer.parseInt(obj[8].toString())==0){ %>
																		<p class="text-danger font-weight-bold">UnSeen</p>
																	<%} %>
														</td>
															
														<td class="left width1">		
														 <form action="CloseAction.htm" method="POST" name="myfrm"  class="d-inline">
															<button  class="btn btn-sm editable-click" name="sub" value="C">  
																<div class="cc-rockmenu">
											                      <div class="rolling">
											                        <figure class="rolling_icon"><img src="view/images/preview3.png"  ></figure>
											                        <span>Actions</span>
											                      </div>
											                     </div> 
															</button>                 
															<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
														    <input type="hidden" name="ActionMainId" value="<%=obj[0]%>"/>
														    <input type="hidden" name="ActionAssignId" value="<%=obj[9]%>"/>
														    <input type="hidden" name="ActionPath" value="A">
														
															<%if(obj[7]!=null && Integer.parseInt(obj[7].toString()) == 0 && Integer.parseInt(obj[10].toString()) == 0 ){%>
																<button class="btn btn-sm editable-click" type="button" onclick="Actioneditmodal('<%=obj[0]%>' , '<%=obj[9]%>'); ">
																	<div class="cc-rockmenu">
											                    	  <div class="rolling">
																		<figure class="rolling_icon"><img src="view/images/edit.png"></figure>
																		<span>Edit</span>
																	  </div>
																	</div>
																</button>  
                                                       		<%}%>
											         	
											         		</form> 
														</td>
													</tr>
												<% count++; } }else{%>
												<tr>
													<td colspan="6" class="text-center">No List Found</td>
												</tr>
												<%}%>
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
			
			<div class="modal fade  bd-example-modal-lg" tabindex="-1" role="dialog" id="newfilemodal">
				<div class="modal-dialog modal-lg" role="document">
					<div class="modal-content">
						<div class="modal-header">
							<h5 class="modal-title">Action Edit</h5>
							<button type="button" class="close" data-dismiss="modal" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<div class="modal-body" align="center">
							<form action="ActionEditSubmit.htm" method="post" autocomplete="off" id="editform" >
								<table class="table">
									<tr>
										<th class="th-padding"> Action Item :</th>
										<td class="td-padding"> 
											<textarea name="actionitem" class="form-control" id="modalactionitem" maxlength="500" required="required" rows="4" cols="60"></textarea>
										</td>
									</tr>
									<tr>
										<th>Lab  :</th>
										<td>
										<select class="form-control selectdee" name="modelAssigneeLabCode" id="modelAssigneelabcode" class="custom-width" onchange="AssigneeEmpListForEdit(0);" >
											 <%if(AllLabList!=null && AllLabList.size()>0){
											    for(Object[] lab : AllLabList){
											    	if(clusterid!=null && clusterid.equalsIgnoreCase(lab[1].toString())){
											 %>
												<option  value="<%=lab[3] %>" <%if(LabCode.equalsIgnoreCase(lab[3].toString())){ %>selected <%} %>><%=lab[3]!=null?StringEscapeUtils.escapeHtml4(lab[3].toString()):"-" %></option>
											<%}}}%> 
											<option  value="@EXP">Expert</option>
										</select>
										</td>
									</tr>
										<tr>
										<th class="th-padding-width20" >Assignee  :</th>
										<td class="td-padding-width" >
											<select class="form-control selectdee"  name="Assignee" id="modalassignee" required="required"  data-live-search="true"   data-placeholder="Select Assignee" >
											</select>
										</td>
									</tr>
									<tr>
										<th class="th-padding"> PDC  :</th>
										<td class="th-padding-width30"  >
											<input type="text" name="newPDC" value="" class="form-control" id="modalipdc1"  readonly required >
											<input type="text" name="newPDC1" value="" class="form-control" id="modalipdc2"  readonly required onclick="alert('PDC Revision Limit Exceded !!');">
										</td>
									</tr>
								</table>
								<button type="submit" class="btn btn-sm submit" id="modalsubmitbtn"  >SUBMIT</button>
								<input type="hidden" name="actionmainid" id="modalactionmainid" value="">
								<input type="hidden" name="actionassigneid" id="modalactionassignid" value="">
								<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" />
							</form>
						</div>
					</div>
				</div>
			</div>

			<div class="modal fade  bd-example-modal-lg" tabindex="-1" role="dialog" id="ActionAssignfilemodal">
				<div class="modal-dialog modal-lg" role="document">
					<div class="modal-content">
						<div class="modal-header">
							<h5 class="modal-title">Action Item : <b id="MainActionitem" ></b></h5>
							<button type="button" class="close" data-dismiss="modal" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<div class="modal-body" >
							<form name="specadd" id="specadd" action="ActionSubmit.htm" method="post" enctype="multipart/form-data">
  
				   			<div class="row">
									  <div class="col-1"></div>
				  				      
			                          <div class="col-3">
				                             <div class="form-group">
				                                      <label> Lab : <span class="mandatory text-danger" >* </span></label>
				                                       <br>
				                                       <select class=" form-control selectdee width-full negative-marging5" name="AssigneeLabCode" id="AssigneeLabCode" required="required"  onchange="AssigneeEmpList()" >
															<option disabled="disabled"  selected value="" >Choose...</option>
															<%if(AllLabList!=null && AllLabList.size()>0){	
																for (Object[] obj  : AllLabList) {
															if(clusterid!=null && clusterid.equalsIgnoreCase(obj[1].toString())){
															%>
														     	<option value="<%=obj[3]%>" <%if(LabCode!=null && LabCode.equalsIgnoreCase(obj[3].toString())){ %>selected <%} %> ><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()):"-" %> </option>
															<%}}}%>
															<option value="@EXP"> Expert</option>
														</select>	
				                              </div>
			                         </div>
			
			                         <div class="col-6">
			                               <div class="form-group w-100">
											    <label> Assignee : </label><br>
												<select class="form-control selectdee "  name="Assignee" id="Assignee" required="required"  data-live-search="true"  data-placeholder="Select Assignee" multiple>
												</select>
											</div>
									</div>
			 				</div>  
			 				
			 					<div class="row mb-2">
			 					<div class="col-md-1"></div>
								<div class="col-md-3">
								<label >Attachment </label><br>
								</div>
								<div class="col-md-6">
								<input class="form-control" type="file" id="actionAttachment" name="actionAttachment" accept="application/pdf , image/* ">
								</div>
							</div>
			 				<div  align="center">
			 								<input type="hidden" name="Atype" id="Atype">
			 								<input type="hidden" name="scheduleminutesid" id="scheduleid">
			 								<input type="hidden" name="MainActionId" id="MainActionId">
			 								<input type="hidden" name="ActionLevel"   id="actionlevel" >
			 								<input type="hidden" name="ActionParentid"   id="actionparentid" >
			 								<input type="hidden" name="ActionItem"   id="ActionItem" >
			 								<input type="hidden" name="MainPDC"   id="MainPDC" >
			 								<input type="hidden" name="Type"  id="MainActionType"  >
			 								<input type="hidden" name="ProjectId"  id="ProjectId" >
			 							    <input type="hidden" name="MainPriority"  id="MainPriority" >
			 								<input type="hidden" name="MainCategory"  id="MainCategory"  > 
			 								<input type="hidden" name="OldActionNoId"  id="OldActionNoId" >
			 				 				<input type="submit" name="sub" class="btn  btn-sm submit" form="specadd"  id="adding" value="SUBMIT"  onclick="return confirm('Are you sure To Submit?')"/>
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"  />         				
											
							</div>	
 	<!-- Form End -->			
							</form>
						</div>
					</div>
				</div>
			</div><!-- model end -->

 
			<script type="text/javascript">
				function Actioneditmodal($actionid , $assignid)
				{
					
					$.ajax({		
						type : "GET",
						url : "ActionDetailsAjax.htm",
						data : {
							actionid : $actionid,
							assignid : $assignid
						},
						datatype : 'json',
						success : function(result) {
							var result = JSON.parse(result);
							$('#modalactionitem').html(result[1].replaceAll("<p>","").replaceAll("</p>","").replaceAll("<","").replaceAll(">","").replaceAll("/",""));
							$('#modalactionmainid').val(result[0]);
							$('#modalactionassignid').val(result[6]);
							$('#modelAssigneelabcode').val(result[5]).trigger('change');
							AssigneeEmpListForEdit(result[2]);
							$('#modalassignee').val(''+result[2]).trigger('change');
							
							if(result[4]<1){
								$('#modalipdc1').show();
								$('#modalipdc2').hide();
								$('#modalipdc1').daterangepicker({
									
									"singleDatePicker" : true,
									"linkedCalendars" : false,
									"showCustomRangeLabel" : true,
									"startDate" : result[3],
									"cancelClass" : "btn-default",
									showDropdowns : true,
									locale : {
										format : 'DD-MM-YYYY'
									},
								});
							}else{
								$('#modalipdc1').hide();
								$('#modalipdc2').show();	
								$('#modalipdc2').val(result[3]);
							}
							$('#newfilemodal').modal('toggle');
						}
					});
					
					
				}

				
			</script>

			<script>
				
				$(function() {
					$('#chkveg').multiselect({
						includeSelectAllOption: true
					});
				});
	
				function AssigneeEmpListForEdit(empid){

				 	var $AssigneeLabCode =  $('#modelAssigneelabcode').val(); 

				 	if($AssigneeLabCode!=""){
				 		
				 		$.ajax({
				 			
				 			type : "GET",
				 			url : "ActionAssigneeEmployeeList.htm",
				 			data : {
				 				LabCode : $AssigneeLabCode,	
				 			},
				 			datatype : 'json',
				 			success : function(result) {
				 				var result = JSON.parse(result);
				 				var values = Object.keys(result).map(function(e) {
				 					return result[e]
				 				});
				 				
				 				var s = '';
				 				s += '<option value="">Choose ...</option>';
				 				if($AssigneeLabCode == '@EXP'){
				 					
				 				}
				 				for (i = 0; i < values.length; i++) 
				 				{

				 					s += '<option value="'+values[i][0]+'">'+values[i][1] + ', ' +values[i][3] + '</option>';
				 				} 
				 				
				 				$('#modalassignee').html(s);
				 				$('#modalassignee').val(''+empid).trigger('change');
				 			}
				 		});
				 		
				 	}
				 }			

			</script>   

			<script>
			$("#OldList").hide();
			$("#ItemDescriptionSearchBtn").click(function(){
				$('#OldActionNoId').empty();
				var $ItemSearch = $("#ItemDescriptionSearch").val();
				$("#loader ").show(); 				
				if ($ItemSearch === ""){
					alert("Search Content Requried");
					$("#loader ").hide();
				}else{
					
					$.ajax({

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
				$('#DateCompletion').daterangepicker({
					"singleDatePicker" : true,
					"linkedCalendars" : false,
					"showCustomRangeLabel" : true,
					 "minDate" : new Date(),
					<%if(ActionData!=null && ActionData[6]!=null){%>
					 "maxDate" : new Date('<%=ActionData[6]%>'), 
					<%}%>
					"cancelClass" : "btn-default",
					showDropdowns : true,
					locale : {
						format : 'DD-MM-YYYY'
					}
				});
				
				
				$('#Actionsubmit').click(function(){
					var $actionitem = $("#Item").val().trim();

						if($actionitem=="" || $actionitem==null|| $actionitem=="null" ||$actionitem==" "){
							alert("Please Enter Action item!");
						}else{
							$('#ActionAssignfilemodal').modal('toggle');
							
							var $Mainid = $("#MainId").val();
							var $MainPdc = $("#DateCompletion").val();
							var $type= $("#ActionType").val();
							var $projectid = $("#Project").val();
							var $priority    = $("#Priority").val();
							var $Actionlevel = $("#ActionLevel").val();
							var $Actionparentid = $("#ActionPraentId").val(); 
							var $Atype = $("#Actiontype").val();
							var $actionscheduleid = $("#Actionscheduleid").val();
							$("#scheduleid").val($actionscheduleid);
							$("#Atype").val($Atype);
							$("#MainActionId").val($Mainid);
							$("#actionlevel").val($Actionlevel);
							$("#actionparentid").val($Actionparentid);
							$("#ActionItem").val($actionitem);
							$("#MainPDC").val($MainPdc);
							$("#ProjectId").val($projectid);
							$("#MainActionType").val($type);
							$("#MainPriority").val($priority);
							$("#MainCategory").val($("#Category").val());
							$("#OldActionNoId").val($("#OldActionNoId").val());
							document.getElementById('MainActionitem').innerHTML =$("#Item").val();
							$('#DateCompletion1').daterangepicker({
								"singleDatePicker" : true,
								"linkedCalendars" : false,
								"showCustomRangeLabel" : true,
								"minDate" : new Date(),
								"maxDate" : $("#DateCompletion").val(), 
								"cancelClass" : "btn-default",
								showDropdowns : true,
								locale : {
									format : 'DD-MM-YYYY'
								}
							});
							AssigneeEmpList();
						}
					});
				
				$(document).ready(function(){	
				 	
					 AssigneeEmpList();
				 }); 	
				 

				function AssigneeEmpList(){
				 	
				 	$('#Assignee').val("");
				 	
				 	var $AssigneeLabCode = $('#AssigneeLabCode').val();
				 	var $ActionMainId = $("#MainActionId").val();
				 	var $parentid = $("#actionparentid").val();
				 	var $MainId;
				 	if($ActionMainId==0){
				 		$MainId=$parentid;
				 	}else{
				 		$MainId=$ActionMainId;
				 	}
				 
				 	if($AssigneeLabCode!=""){
				 		
				 		$.ajax({
				 			
				 			type : "GET",
				 			url : "ActionAssigneeEmployeeList.htm",
				 			data : {
				 				LabCode : $AssigneeLabCode,
				 				MainId  : $MainId,
				 			},
				 			datatype : 'json',
				 			success : function(result) {
				 				var result = JSON.parse(result);
				 				
				 				var values = Object.keys(result).map(function(e) {
				 					return result[e]
				 					
				 				});
				 				
				 				var s = '';
				 				
				 				if($AssigneeLabCode == '@EXP'){
				 					
				 				}
				 				for (i = 0; i < values.length; i++) 
				 				{
				 					s += '<option value="'+values[i][0]+'">'+values[i][1] +','+ ' '+values[i][3]+'</option>';
				 					
				 				} 
				 				
				 				$('#Assignee').html(s);	
				 			}
				 		});
				 		
				 	}
				 }
				
				</script>
			<!-- Excel Upload  -->>	
				
				 <script type="text/javascript">
				 
				 $(document).ready(function(){
					  $("#myTable12").DataTable({
					 "lengthMenu": [  5,10,25, 50, 75, 100 ],
					 "pagingType": "simple",
					 "pageLength": 10
			
					});
				});
const excel_file = document.getElementById('excel_file');

excel_file.addEventListener('change', (event) => {
    var reader = new FileReader();

    reader.readAsArrayBuffer(event.target.files[0]);

    reader.onload = function(event){

        var data = new Uint8Array(reader.result);

        var work_book = XLSX.read(data, {type:'array'});

        var sheet_name = work_book.SheetNames;

        var sheet_data = XLSX.utils.sheet_to_json(work_book.Sheets[sheet_name[0]], {header:1});  
    	
    	
        if(sheet_data.length > 0)
        {       
        	var ActionItemLength=[];
        var table_output ='<thead> <tr > <th>SNo</th> <th>Action Item</th> <th>Project Code</th> <th>Action Type</th>  <th>Action Date </th> <th>PDC Date  </th> <th>Priority </th> <th>Category</th>  </tr> </thead><tbody>'
        	var r=0;
        var checkExcel=0;
        
    		if( "Action Item" != sheet_data[1][1]){  checkExcel++;}
			if( "Project Code" != sheet_data[1][2]){  checkExcel++;}
			if( "Action Type"   != sheet_data[1][3]){  checkExcel++;}
			if( "Action Date" != sheet_data[1][4]){  checkExcel++;}
			
            for(var row = 2; row < sheet_data.length; row++)
            {            	
            	  table_output += ' <tr> ';
             	
            	  if(row>0){table_output += '<td>'+ (++r) +'</td>';}
                for(var cell = 0; cell < 8; cell++)
                {
                	
                	
                	if(row>0 && cell==1){
                		
                		if(sheet_data[row][cell].toString().length > 990){
                			ActionItemLength.push(row-1);
                		}
                		table_output += '<td class="table-output">'+sheet_data[row][cell]+'</td>';
                	}
                	if(row>0 && cell==2){
                		table_output += '<td>'+sheet_data[row][cell]+'</td>';
                	}
                	if(row>0 && cell==3){
                		var type=sheet_data[row][cell]+"";
                		if("A" === type.toUpperCase()){
                			table_output += '<td>'+ 'Action' +'</td>';
                		}else if("I" === type.toUpperCase()){
                			table_output += '<td>'+ 'Issue' +'</td>';
                		}else if("R" === type.toUpperCase()){
                			table_output += '<td>'+ 'Risk' +'</td>';
                		}	
                	}
                	if(row>0 && cell==4){
                		
                		table_output += '<td>'+ DateFormate((sheet_data[row][cell]-1)) +'</td>';
                	}
                	if(row>0 && cell==5){
                		
                		table_output += '<td>'+ DateFormate((sheet_data[row][cell]-1)) +'</td>';
                	}
                	if(row>0 && cell==6){
                		var type=sheet_data[row][cell]+"";
                		if("H" === type.toUpperCase()){
                			table_output += '<td>'+ 'High' +'</td>';
                		}else if("L" === type.toUpperCase()){
                			table_output += '<td>'+ 'Low' +'</td>';
                		}else if("I" === type.toUpperCase()){
                			table_output += '<td>'+ 'Immediate' +'</td>';
                		}else if("M" === type.toUpperCase()){
                			table_output += '<td>'+ 'Medium' +'</td>';
                		}	
                	}
                	if(row>0 && cell==7){
                		var type=sheet_data[row][cell]+"";
                		if("T" === type.toUpperCase()){
                			table_output += '<td>'+ 'Technical' +'</td>';
                		}else if("F" === type.toUpperCase()){
                			table_output += '<td>'+ 'Finance' +'</td>';
                		}else if("M" === type.toUpperCase()){
                			table_output += '<td>'+ 'Managerial' +'</td>';
                		}else if("L" === type.toUpperCase()){
                			table_output += '<td>'+ 'Logistic' +'</td>';
                		}else if("O" === type.toUpperCase()){
                			table_output += '<td>'+ 'Others' +'</td>';
                		}	
                	}
                }

                table_output += '</tr>';
             }
             table_output += ' <tbody>';
             document.getElementById('myTable1').innerHTML = table_output;
             
              var projectcode = [<%int i=0; for (Object[] obj:ProjectList) { %>"<%= obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString().toUpperCase()):" - " %>"<%= i + 1 < ProjectList.size() ? ",":"" %><% } %>];
              
            
              var code = []; 
             for (var i in sheet_data){
	            if(i>1 && sheet_data[i][2].toString().toUpperCase()!='GEN' &&!projectcode.includes(sheet_data[i][2].toString().toUpperCase())){
	            	code.push(i-1)
	            }
          	}
             var actionitem =[];
             for (var i in sheet_data){
            	 if(i>1 && sheet_data[i][1]+'' === 'undefined' || sheet_data[i][1]+'' ===''|| sheet_data[i][1]+''=='null' && sheet_data[i][1]+''==null){
            		 actionitem.push(i-1)
             	}
          	}
             var actionprojcode =[];
            
             for (var i in sheet_data){
            	 if(i>1 && sheet_data[i][2]+'' === 'undefined' || sheet_data[i][2]+'' ===''|| sheet_data[i][2]+''=='null' && sheet_data[i][2]+''==null){
            		 actionprojcode.push(i-1)
             	}
            	
          	}
 

             var Actiondate = [];
          	var pattern =/^([0-9]{2})\/([0-9]{2})\/([0-9]{4})$/;
             for (var i in sheet_data){
            	  if(i>1 && !pattern.test(DateFormate( sheet_data[i][4]-1))){
            		 Actiondate.push(i-1);
            	 } 
             }
             
             var Pdcdate = [];
             for (var i in sheet_data){
            	  if(i>1 && !pattern.test(DateFormate( sheet_data[i][5]-1))){
            		 Pdcdate.push(i-1);
            	 }  
             }
             
             var type=[];
 			for (var i in sheet_data){
            	 if(i>1 && sheet_data[i][3]+''!='A' && sheet_data[i][3]+''!='I' && sheet_data[i][3]+''!='K'){
            		 type.push(i-1);
            	 }
             }
 			
 			 var priority=[];
  			for (var i in sheet_data){
             	 if(i>1 && sheet_data[i][6]+''!='H' && sheet_data[i][6]+''!='L' && sheet_data[i][6]+''!='M' && sheet_data[i][6]+''!='I'){
             		priority.push(i-1);
             	 } 
              }
  			
  			 var Category=[];
   			for (var i in sheet_data){
              	 if(i>1 && sheet_data[i][7]+''!='T' && sheet_data[i][7]+''!='F' && sheet_data[i][7]+''!='M'&& sheet_data[i][7]+''!='L' && sheet_data[i][7]+''!='O'){
              		Category.push(i-1);
              	 } 
               }
   			
   		 var comparetwodates=[];
			for (var i in sheet_data){
				const actiondate = ExcelDate( sheet_data[i][4]+''); 
				const pdcdate = ExcelDate( sheet_data[i][5]+'');
	           	  if(i>1 && dateCompare( actiondate ,pdcdate )){
					comparetwodates.push(i-1);
	           	 }  
            }
             
              
            	
            	var msg='';
	              if(actionitem.length>0){
	        		  msg+="Enter Action Item at serial No :"+ actionitem+"\n";
	        	} if(actionprojcode.length>0){
	        		  msg+="Enter Project Code at serial No :"+ actionprojcode+"\n";
	        	} if(Actiondate.length>0){
            		  msg+="Action Date Is Not Proper at serial No :"+ Actiondate+"\n";
            	} if(Pdcdate.length>0){
            		msg+= "PDC Date Is Not Proper at serial No : "+ Pdcdate+"\n";
            	} if(type.length>0){
            		msg+= "Enter Action Type According the Note at serial No : "+ type+"\n";
               	} if(priority.length>0){
               		msg+= "Enter Priority According the Note at serial No : "+ priority+"\n";
               	} if(Category.length>0){
               		msg+= "Enter Category According the Note at serial No : "+ Category+"\n";	
               	} if(comparetwodates.length>0){
               		msg+= "Action Date Should be less than PDC Date at serial No : "+ comparetwodates+"\n";
               	} if(code.length>0){
               		msg+= "Invalid Project Code at serial No : "+ code+"\n";
               	} if(ActionItemLength.length>0){
               		msg+= "Action Item Data Is too Long at serial No : "+ ActionItemLength+"\n";
               	}
               	
               	if(checkExcel>0){
       			 	alert("Please Upload Action Item Excel ");
       				excel_file.value = '';
       			} else {
		              if(ActionItemLength.length>0 || actionitem.length>0 || code.length>0 || comparetwodates.length>0 || actionprojcode.length>0 || Actiondate.length>0 || Pdcdate.length>0 || type.length>0 || priority.length>0 || Category.length>0 || comparetwodates.length>0){
		            	 alert(msg);
		            	 excel_file.value = '';
		             }else{
		            	 $('#exampleModalLong').modal('show');
		            }
       			}	 
            }else{
            	alert("Please Select the Excel File!");
            	return false;
            }
        }
        
});

$('#exampleModalLong').on('hide.bs.modal', function(){
	 excel_file.value = '';
})

$(document).ready(function(){
	  $("#myTable1").DataTable({
	 "lengthMenu": [  5,10,25, 50, 75, 100 ],
	 "pagingType": "simple"
	});
});



function DateFormate(exceldate)
{
	const today = new Date((exceldate - (25567 + 1))*86400*1000);
	const yyyy = today.getFullYear();
	let mm = today.getMonth() + 1; 
	let dd = today.getDate();
	
	if (dd < 10) dd = '0' + dd;
	if (mm < 10) mm = '0' + mm;

	const formattedToday = dd + '/' + mm + '/' + yyyy;
	return formattedToday;
}
function ExcelDate(exceldate)
{
	const today = new Date((exceldate - (25567 + 1))*86400*1000);
	const yyyy = today.getFullYear();
	let mm = today.getMonth() + 1; 
	let dd = today.getDate();

	if (dd < 10) dd = '0' + dd;
	if (mm < 10) mm = '0' + mm;

	const formattedToday = yyyy + '-' + mm + '-' + dd;
	
	return formattedToday;
			
}

function dateCompare(d1, d2){
    const date1 = new Date(d1);
    const date2 = new Date(d2);
    if(date1 > date2){
        return true;
    } 
}


function sanitizeInput(input) {
   // input.value =  input.value.replace(/<\/?[\w\s="/.':;#-\/\?]+>/gi, '');
}
</script> 
				
				
				
				
</body>
</html>