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
<spring:url value="/resources/css/sweetalert2.min.css" var="sweetalertCss" />
<spring:url value="/resources/js/sweetalert2.min.js" var="sweetalertJs" />
	<link href="${sweetalertCss}" rel="stylesheet" />
	<script src="${sweetalertJs}"></script>
 <spring:url value="/resources/css/milestone/milestoneActivityList.css" var="milestoneActivityList" />     
<link href="${milestoneActivityList}" rel="stylesheet" />

<title>Milestone List</title>

</head>
 
<body>
  <%
  
  String projectshortName="";
  
  List<Object[]> EmployeeList=(List<Object[]>)request.getAttribute("EmployeeList");
  List<Object[]> MilestoneList=(List<Object[]>)request.getAttribute("MilestoneActivityList");
  List<Object[]> ProjectList=(List<Object[]>)request.getAttribute("ProjectList");
  SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
  SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
  String ProjectId=(String)request.getAttribute("ProjectId");
  String LoginType = (String)session.getAttribute("LoginType");
  List<String> actionAllowedFor =  Arrays.asList("A", "P");
  Long projectDirector = 0L;
  String selectedProject = "";
	int sunsetCount = 0;
  Long empId = (Long)session.getAttribute("EmpId");
 %>


    <form class="form-inline"  method="POST" action="MilestoneActivityList.htm">
  <div class="row W-100 width-100" >

  <div class="col-md-7">
  <h6 class="h6Color" >&nbsp; &nbsp; Project <i class="fa fa-long-arrow-right " aria-hidden="true"></i> Add Milestone <i class="fa fa-long-arrow-right " aria-hidden="true"></i> 
  							Add SubActivity <i class="fa fa-long-arrow-right " aria-hidden="true"></i> SubActivity <i class="fa fa-long-arrow-right " aria-hidden="true"></i> Weightage <i class="fa fa-long-arrow-right " aria-hidden="true"></i> Assignee</h6>
  </div>
	
                                    <div class="col-md-2">
                            		<label class="control-label">Project Name :</label>
                            		</div>
                            		<div class="col-md-2" >
                              		<select class="form-control selectdee" id="ProjectId" required="required" name="ProjectId">
    									<option disabled="true"  selected value="">Choose...</option>
    										<% for (Object[] obj : ProjectList) {
    										 projectshortName=(obj[17]!=null)?" ("+obj[17].toString().trim()+") ":"";
    										%>
											<option value="<%=obj[0]%>" <%if(obj[0].toString().equalsIgnoreCase(ProjectId)){ %>selected="selected" <%projectDirector = Long.parseLong(obj[23].toString());selectedProject=projectshortName; %> <%} %>> <%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - "%> <%=projectshortName!=null?StringEscapeUtils.escapeHtml4(projectshortName): " - "%>  </option>
											<%} %>
  									</select>
  									</div>
<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
 <input id="submit" type="submit" name="submit" value="Submit" hidden="hidden">
 </div>
</form>


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
				<div class="card shadow-nohover" >
				<div class="row card-header">
			     <div class="col-md-10">
					<h5 ><%if(ProjectId!=null){
						Object[] ProjectDetail=(Object[])request.getAttribute("ProjectDetails");
						%>
						<%=ProjectDetail[2]!=null?StringEscapeUtils.escapeHtml4(ProjectDetail[2].toString()): " - " %> ( <%=ProjectDetail[1]!=null?StringEscapeUtils.escapeHtml4(ProjectDetail[1].toString()): " - " %> ) 
					<%} %>
					 Milestone List</h5>
					</div>
					<div class="col-md-2 justify-content-end">
					<%if(ProjectId!=null){ %>                              

						 <%if(LoginType.equalsIgnoreCase("A") || projectDirector.toString().equalsIgnoreCase(empId+"") ){ %>								
																
					  <form class="  justify-content-end"  method="POST" action="MilestoneActivityAdd.htm">
					  		<%if(MilestoneList.size()>0){ %>
                               	<button   class="btn btn-sm excelBtn" type="submit" name="sub" value="Accept" formaction="MilestoneExcelFile.htm"  formtarget="_blank">
									<i class="fa fa-file-excel-o" aria-hidden="true"></i>
				                </button> 
							<%} %>
                            	<input type="hidden" name="ProjectId"	value="<%=ProjectId %>" /> 
                             <input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
                              <input type="hidden" name="projectDirector" value="<%=projectDirector%>">  
                               <input type="submit"  value="Add Milestone" class="btn btn-sm add mwidth">
                          
                     </form>
                          <%} %>
					 <%} %>
					 </div>
					 </div>
					
					<div class="card-body">
                        <div class="table-responsive"> 
									<table class="table  table-hover table-bordered">
													<thead class="center">

														<tr>
															<th>Expand</th>
															<th>SN</th>
															<th>Mil-No</th>
														<!-- <th style="text-align: left;">Project Name</th> -->
															<th>Milestone Activity</th>
															<th >Start Date</th>
															<th >End Date</th>	
															<th >
															<div class="borderBottom">First OIC </div>
															 Second OIC</th>
															<th>Status</th>
															<th>Weightage</th>	
															<th>Progress</th>			
															<%-- <%if(actionAllowedFor.contains(LoginType) || projectDirector.equals(empId) || oicEmpId.equals(empId)) {%>	 --%>								
														 		<th>Action</th>
														 	<%-- <%} %> --%>	
														 	
														</tr>
													</thead>
													<tbody>
														<%int  count=1;
															
														 	if(MilestoneList!=null&&MilestoneList.size()>0){
															for(Object[] obj: MilestoneList){ %>
														<tr class="<% if("Y".equalsIgnoreCase(obj[20].toString())){ sunsetCount++; %> bg-sunset <%}%>">
															<td  class="center width-2">
																<span class="clickable" data-toggle="collapse" id="row<%=obj[0] %>" data-target=".row<%=obj[0]  %>">
																	<button class="btn btn-sm btn-success" id="btn<%=obj[0]  %>"  onclick="ChangeButton('<%=obj[0]  %>')">
																		<i class="fa fa-plus"  id="fa<%=obj[0] %>"></i> 
																	</button>
																</span>
																<input type="hidden" id="financialOutlay_<%=obj[0]%>" value="<%=obj[18]!=null?obj[18]:"-"%>">
																<input type="hidden" id="statusRemarks_<%=obj[0]%>" value="<%=obj[11]!=null?obj[11].toString().replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", ""):"-"%>">
															</td>
															<td class="width-5 text-left" >
																<input class="form-control" form="slnoupdateform" type="number" name="newslno" value="<%=obj[5]%>" min="1" max="<%=MilestoneList.size()%>">
																<input type="hidden" form="slnoupdateform" name="milestoneActivityId" value="<%=obj[0]%>"/>
															</td>
															<td class="width-5 text-left"> Mil-<%=obj[5]!=null?StringEscapeUtils.escapeHtml4(obj[5].toString()): " - "%></td>
															<%-- <td class="width-30px"><%=obj[1]%></td> --%>
															<td class="tdDetails"
															onclick="showMilestoneStatusProgress('<%=obj[0]%>')">
															<%=StringEscapeUtils.escapeHtml4(obj[4].toString()) %>
															</td>
															
															<td class="width-8"><%=sdf.format(obj[2])%></td>
															<td class="width-8"><%=sdf.format(obj[3])%></td>
															<td class="width-15"><%=obj[6]!=null?StringEscapeUtils.escapeHtml4(obj[6].toString()): " - "%> <br> <%=obj[7]!=null?StringEscapeUtils.escapeHtml4(obj[7].toString()): " - "%> </td>
															<td class="width-8">-</td>
															<td  class="width-7"  align="center"><%=obj[13]!=null?StringEscapeUtils.escapeHtml4(obj[13].toString()): " - "%></td>	
															<td>
																<%if(!obj[12].toString().equalsIgnoreCase("0")){ %>
																<div class="progress progressDiv" >
																<div class="progress-bar progress-bar-striped
																<%if(obj[14].toString().equalsIgnoreCase("2")){ %>
																 bg-success
																<%} else if(obj[14].toString().equalsIgnoreCase("3")){ %>
																  bg-info
																<%} else if(obj[14].toString().equalsIgnoreCase("4")){ %>
																  bg-danger
																<%} else if(obj[14].toString().equalsIgnoreCase("5")){ %>
																  bg-warning 
																<%}  %> width-<%=obj[12] %>
																" role="progressbar" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
																<%=obj[12]!=null?StringEscapeUtils.escapeHtml4(obj[12].toString()): " - " %>
																</div> 
																</div> <%}else{ %>
																<div class="progress progressDiv" >
																<div class="progress-bar noProgress" role="progressbar"  >
																Not Started
																</div>
																</div> <%} %>
															</td>
																<td  class="width-20 text-center"  >		
																	<form action="MilestoneActivityDetails.htm" method="POST" name="myfrm" >
																		<%if(Integer.parseInt(obj[12].toString())<100){ %>
																			<button  class="editable-click" name="sub" value="B" id="detailsAddbtn<%=obj[0].toString() %>">  
																				<div class="cc-rockmenu">
															                      <div class="rolling">
															                        <figure class="rolling_icon"><img src="view/images/preview3.png"  ></figure>
															                        <span>Details</span>
															                      </div>
															                     </div> 
																			</button>
																		<%}else if(Integer.parseInt(obj[12].toString())==100){ %>
																			<button  class="editable-click" type="button" onclick="MainDOCEditModal(<%=obj[0]%>,'<%=obj[16]%>')" id="docbtn<%=obj[0]%>">  
																				<div class="cc-rockmenu">
															                      <div class="rolling">
															                        <figure class="rolling_icon"><i class="fa fa-calendar-o" aria-hidden="true"></i></figure>
															                        <span>Date of Completion</span>
															                      </div>
															                     </div> 
																			</button>
																		<%} %>
																		  <%if(Arrays.asList("N","B").contains(obj[10].toString()) ){ %>
																		  	<%if("A".equalsIgnoreCase(LoginType) || projectDirector.equals(empId) || Long.parseLong(obj[17].toString())==(empId)) { %>
																		  
			                                                              <button  class="editable-click" name="sub" value="C"  id="EditWeightage<%=obj[0].toString()%>">
																			<div class="cc-rockmenu">
																			 <div class="rolling">	
														                        <figure class="rolling_icon"><img src="view/images/edit.png" ></figure>
														                        <span>Edit/Weightage</span>
														                      </div>
														                     </div>
														                  </button> 
			                                                             <button type="submit" id="assignBtn<%=obj[0].toString() %>"  class="editable-click" name="sub" value="Assign"  formaction="M-A-Assign-OIC.htm" onclick="if('0'=='<%=obj[13].toString() %>'){alert('Please add Weightage first then you able to assign.');return false; }else{ return confirm('Are You Sure To Assign ?') }"   >
																			<div class="cc-rockmenu">
																			 <div class="rolling">	
														                        <figure class="rolling_icon"><img src="view/images/assign.jpg" ></figure>
														                        <span>Assign</span>
														                      </div>
														                     </div>
														                  </button>
														                   <button type="button" class="editable-click" name="sub" value="C" onclick="deleteMainMilestone(<%= obj[0]%>)">
																			<div class="cc-rockmenu">
																			 <div class="rolling">	
														                        <figure class="rolling_icon"><img src="view/images/delete.png" ></figure>
														                        <span>Delete</span>
														                      </div>
														                     </div>
														                  </button> 
														                  <button class="btn mb-3" type="button" data-toggle="tooltip" data-placement="top" title="SunSet Milestone" onclick="handleSunSet(<%= obj[0] %>,1,'<%=obj[20] %>')" >
																		    <i class="fa fa-sun-o" aria-hidden="true"></i>
																		</button>
														                  <%} %>
			                                                              <%}else if("Y".equalsIgnoreCase(obj[10].toString())){ %>
<%-- 			                                                              <%if("A".equalsIgnoreCase(LoginType) || projectDirector.equals(empId) || Long.parseLong(obj[17].toString())==(empId)) { %>
 --%>			                                                               <button  class="editable-click" name="sub" value="C" id="basLineBtn<%=obj[0].toString() %>" >
																			<div class="cc-rockmenu">
																			 <div class="rolling">	
														                        <figure class="rolling_icon"><img src="view/images/clipboard.png" ></figure>
														                        <span>Base Line </span>
														                      </div>
														                     </div>
														                  </button>    
														                 <%--  <%} %> --%>
			                                                               <%} %>
			                                                            
			                                                            <input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
																	    <input type="hidden" name="MilestoneActivityId" value="<%=obj[0]%>"/>
																	    <input type="hidden" name="projectid" value="<%=ProjectId%>"/>
																	    <input type="hidden" name="ProjectId" value="<%=ProjectId %>" >
																	    <input type="hidden" name="projectDirector" value="<%=projectDirector %>" >
																 </form> 
																 
																 	<form action="MilestoneActivityDetails.htm" method="POST" name="myfrm"  >
															 
		                                                            <%if("B".equalsIgnoreCase(obj[10].toString())){ %>
		                                                              
		                                                             <button type="button"  class="editable-click" name="sub" value="Back"   data-toggle="modal" data-target="#exampleModal<%=obj[0]%>" data-whatever="@mdo" >
																		<div class="cc-rockmenu">
																		 <div class="rolling">	
													                        <figure class="rolling_icon"><img src="view/images/message.png" ></figure>
													                        <span>Message</span>
													                      </div>
													                     </div>
													                 </button>
																	 
																	<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
																    <input type="hidden" name="MilestoneActivityId" value="<%=obj[0]%>"/>
																	 <div class="modal fade" id="exampleModal<%=obj[0]%>" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
							                                              <div class="modal-dialog">
							                                          <div class="modal-content">
							                                             <div class="modal-header">
							                                            <h5 class="modal-title" id="exampleModalLabel">Send Back Remarks</h5>
									                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
									                                     <span aria-hidden="true">&times;</span>
									                                       </button>
							                                        </div>
							                                         <div class="modal-body">
							          	                                  <div class="row">
							                       				 
										                                     <div class="col-md-12">
																				<div class="form-group">
																					<label class="control-label">Remark</label> 
																				    <textarea class="form-control" readonly="readonly" name="Remarks" rows="5" maxlength="255" required="required" placeholder="Enter Send Back Remark here with max 255 characters"> <%=obj[11]!=null?obj[11].toString():"" %>  </textarea>
																				</div>
																			</div>       
																
								                                         	</div>
								                                     		<div class="modal-footer">
								        
										                                         <button  class="btn btn-sm btn-success" name="sub" value="Assign"  formaction="M-A-Assign-OIC.htm" onclick="return confirm('Are You Sure To Assign ?')" >Assign Again</button>
										        								 <input type="hidden" name="projectid" value="<%=ProjectId%>"/>
								   
								                                           	</div>
							                                            </div>
							                                            </div>
							                                            </div>
																		</div> 
																		<%} %>	
																	 </form> 
																	 
																	 <span class="btn btn-sm btn-info accessbtnId" id="spanMessage<%=obj[0].toString()%>" >Access Denied</span>
																</td>
															<%-- <%} else {%>
																<td class="center"> <span class="btn btn-sm btn-info">Access Denied</span> </td>
															<%} %> --%>	
														</tr>
														 <tr class="collapse row<%=obj[0]%> trclass<%=obj[0]%> font-weight-bold"  >
                                                         <td></td>
                                                         <td></td>
                                                         <td>Sub</td>
                                                         <td>Activity</td>
                                                         <td>Start Date</td>
                                                         <td>End Date</td>
                                                         	<th >
															<div class="borderBottom">First OIC </div>
															 Second OIC</th>
                                                         <td>Date Of Completion</td>
                                                         <td>Sub Weightage</td>
                                                         <td>Sub Progress</td>
                                                         <%if(actionAllowedFor.contains(LoginType)) {%>
                                                         	<td>Shown in display of Briefing Paper and MOM</td>
                                                         <%} %>
                                                         </tr>
                                                         <%
                                                         List<String>empList = new ArrayList<>();
                                                         int countA=1;
                                                            List<Object[]> MilestoneA=(List<Object[]>)request.getAttribute(count+"MilestoneActivityA");
														 	if(MilestoneA!=null&&MilestoneA.size()>0){
															for(Object[] objA: MilestoneA){
																//check if empList contains first OIC of A level 
																if(!empList.contains(objA[13].toString() )){
																empList.add(objA[13].toString());
																}
																//check if empList contains first 2ndIC of A level 
																if(!empList.contains(objA[15].toString() )){
																	empList.add(objA[15].toString());
																	}
	                                                            List<Object[]> MilestoneB=(List<Object[]>)request.getAttribute(count+"MilestoneActivityB"+countA);
	
																%>
																		
														<tr class="collapse row<%=obj[0]  %> trclass<%=obj[0]%>"  >
														
															<td class="width-2"  class="center">
													
															</td>
															<td class="center"> 
															<%if(MilestoneB!=null && MilestoneB.size()>0) { %>
															<span class="clickable" data-toggle="collapse" id="row_<%=objA[0] %>" data-target=".row_<%=objA[0]  %>">
																	<button class="btn btn-sm btn-success" id="btn<%=objA[0]  %>"  onclick="ChangeButton('<%=objA[0]  %>')">
																		<i class="fa fa-plus"  id="fa<%=objA[0] %>"></i> 
																	</button>
																</span>
																<%}else{ %>
													
 																<%} %>
															</td>
															<td  class="width-5 text-left" > A-<%=countA%></td>
															<%-- <td class="width-30px"><%=obj[1]%></td> --%>
															<td class="tdDetails"
															 onclick="showMilestoneProgress('<%=obj[0]%>', '<%=objA[0]%>', '<%=ProjectId%>', 'A')">
															<%=objA[4]!=null?StringEscapeUtils.escapeHtml4(objA[4].toString()): " - " %>
															</td>
															
															<td class="width-30px"><%=sdf.format(objA[2])%></td>
															<td class="width-8" ><%=sdf.format(objA[3])%></td>
															<td><%=objA[14]%>
															<br>
																
															<%=objA[27]!=null?StringEscapeUtils.escapeHtml4(objA[27].toString()): " - " %>	
															
															
															</td>
															
															<td class="width-30px"><%if(objA[9].toString().equalsIgnoreCase("3")||objA[9].toString().equalsIgnoreCase("5")){ %>
														     <%if(objA[7]!=null){ %>   <%=sdf.format(objA[7]) %> <%}else{ %><%=objA[8]!=null?StringEscapeUtils.escapeHtml4(objA[8].toString()): " - " %> <%} %>
														         <%}else{ %>
														         <%=objA[8]!=null?StringEscapeUtils.escapeHtml4(objA[8].toString()): " - " %>
															 <%} %>
															 </td>
															 <td align="center"><%=objA[6]!=null?StringEscapeUtils.escapeHtml4(objA[6].toString()): " - " %></td>
															<td>
															<%if(!objA[5].toString().equalsIgnoreCase("0")){ %>
															<div class="progress progressDiv" >
															<div class="progress-bar progress-bar-striped
															<%if(objA[9].toString().equalsIgnoreCase("2")){ %>
															 bg-success
															<%} else if(objA[9].toString().equalsIgnoreCase("3")){ %>
															  bg-info
															<%} else if(objA[9].toString().equalsIgnoreCase("4")){ %>
															  bg-danger
															<%} else if(objA[9].toString().equalsIgnoreCase("5")){ %>
															  bg-warning
															<%}  %> width-<%=objA[5] %>
															" role="progressbar"  aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
															<%=objA[5]!=null?StringEscapeUtils.escapeHtml4(objA[5].toString()): " - " %>
															</div> 
															</div> <%}else{ %>
															<div class="progress progressDiv" >
															<div class="progress-bar noProgress" role="progressbar"   >
															Not Started
															</div>
															</div> <%} %>
															</td>
															
															<td>						
							              <%if("A".equalsIgnoreCase(LoginType) || projectDirector.equals(empId) || Long.parseLong(objA[13].toString())==(empId)) { %>
		                                                         
			                                                         <div class="maindivInput">
			                                                         <div class="subdiv">
			                                                		5<input type="checkbox" <%if(objA[19].toString().equalsIgnoreCase("Y")){ %>checked<%} %> class="form-control point5"  value="<%=objA[0]!=null?StringEscapeUtils.escapeHtml4(objA[0].toString()): " - "%> <%="/"%> <%=objA[19]!=null?StringEscapeUtils.escapeHtml4(objA[19].toString()): " - "%> <%="/point5"%>" onchange="updateBpPoints(this)">
			                                                        6.a<input type="checkbox" <%if(objA[20].toString().equalsIgnoreCase("Y")){ %>checked<%} %> class="form-control point6"  value="<%=objA[0]!=null?StringEscapeUtils.escapeHtml4(objA[0].toString()): " - "%> <%="/"%> <%=objA[20]!=null?StringEscapeUtils.escapeHtml4(objA[20].toString()): " - "%> <%="/point6"%>" onchange="updateBpPoints(this)">
			                                                        9<input type="checkbox" <%if(objA[21].toString().equalsIgnoreCase("Y")){ %>checked<%} %> class="form-control point9"  value="<%=objA[0]!=null?StringEscapeUtils.escapeHtml4(objA[0].toString()): " - "%> <%="/"%> <%=objA[21]!=null?StringEscapeUtils.escapeHtml4(objA[21].toString()): " - "%> <%="/point9"%>" onchange="updateBpPoints(this)">
			                                                         </div>
			                                                         </div>
		                                                         
	                                                         <%} %></td>
                                                         </tr>
                                                  
                                                         <% int countB=1;
														 	if(MilestoneB!=null&&MilestoneB.size()>0){
															for(Object[] objB: MilestoneB){
																
																//check if empList contains first OIC of B level 
																if(!empList.contains(objB[13].toString() )){
																empList.add(objB[13].toString());
																}
																//check if empList contains first 2ndIC of B level 
																if(!empList.contains(objB[15].toString() )){
																	empList.add(objB[15].toString());
																	}
	                                                            List<Object[]> MilestoneC=(List<Object[]>)request.getAttribute(count+"MilestoneActivityC"+countA+countB);
	
																%>
														<tr class="collapse row_<%=objA[0]  %> trclass<%=obj[0]%> trclass<%=objA[0]%>"  >
															<td sclass="center width-2">

															
															
															 </td>
																<td class="center"> 
															<%if(MilestoneC!=null && MilestoneC.size()>0) {%>
															<span class="clickable" data-toggle="collapse" id="row_<%=objB[0] %>" data-target=".row_<%=objB[0]  %>">
																	<button class="btn btn-sm btn-success" id="btn<%=objB[0]  %>"  onclick="ChangeButton('<%=objB[0]  %>')">
																		<i class="fa fa-plus"  id="fa<%=objB[0] %>"></i> 
																	</button>
																</span>
																<%}else{ %>
																
																<%}%>
															</td>
															<td class="width-5 text-left" > &nbsp;&nbsp;&nbsp;B-<%=countB%></td>
															<%-- <td class="width-30px"><%=obj[1]%></td> --%>
															<td class="tdDetails"
															 onclick="showMilestoneProgress('<%=obj[0]%>', '<%=objB[0]%>', '<%=ProjectId%>', 'B')">
																<%=objB[4]!=null?StringEscapeUtils.escapeHtml4(objB[4].toString()): " - " %>
															</td>
															
															<td class="width-30px"><%=sdf.format(objB[2])%></td>
															<td class="width-8" ><%=sdf.format(objB[3])%></td>
															<td><%=objB[14]!=null?StringEscapeUtils.escapeHtml4(objB[14].toString()): " - "%>
															<br>
															<%=objB[27]!=null?StringEscapeUtils.escapeHtml4(objB[27].toString()): " - "%>
															</td>
															<td class="width-30px"><%if(objB[9].toString().equalsIgnoreCase("3")||objB[9].toString().equalsIgnoreCase("5")){ %>
														      <%if(objB[7]!=null){ %>   <%=sdf.format(objB[7]) %> <%}else{ %><%=objB[8]!=null?StringEscapeUtils.escapeHtml4(objB[8].toString()): " - " %> <%} %>
														         <%}else{ %>
														         <%=objB[8]!=null?StringEscapeUtils.escapeHtml4(objB[8].toString()): " - " %>
															 <%} %></td>
															  <td align="center"><%=objB[6]!=null?StringEscapeUtils.escapeHtml4(objB[6].toString()): " - " %></td>
															<td>
															<%if(!objB[5].toString().equalsIgnoreCase("0")){ %>
															<div class="progress progressDiv" >
															<div class="progress-bar progress-bar-striped
															<%if(objB[9].toString().equalsIgnoreCase("2")){ %>
															 bg-success
															<%} else if(objB[9].toString().equalsIgnoreCase("3")){ %>
															  bg-info
															<%} else if(objB[9].toString().equalsIgnoreCase("4")){ %>
															  bg-danger
															<%} else if(objB[9].toString().equalsIgnoreCase("5")){ %>
															  bg-warning
															<%}  %> width-<%=objB[5] %>
															" role="progressbar"  aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
															<%=objB[5]!=null?StringEscapeUtils.escapeHtml4(objB[5].toString()): " - " %>
															</div> 
															</div> <%}else{ %>
															<div class="progress progressDiv">
															<div class="progress-bar noProgress" role="progressbar"   >
															Not Started
															</div>
															</div> <%} %>
															</td>
															<td>
																 <%if("A".equalsIgnoreCase(LoginType) || projectDirector.equals(empId) || Long.parseLong(objB[13].toString())==(empId)) { %>
	                                                         	
	                                                         
			                                                        <div class="maindivInput">
			                                                         <div class="subdiv">
			                                                        5 <input  type="checkbox" <%if(objB[19].toString().equalsIgnoreCase("Y")){ %>checked<%} %> class="form-control" value="<%=objB[0]!=null?StringEscapeUtils.escapeHtml4(objB[0].toString()): " - "%> <%="/"%> <%=objB[19]!=null?StringEscapeUtils.escapeHtml4(objB[19].toString()): " - "%> <%="/point5"%>" onchange="updateBpPoints(this)">
			                                                        6.a <input type="checkbox" <%if(objB[20].toString().equalsIgnoreCase("Y")){ %>checked<%} %> class="form-control" value="<%=objB[0]!=null?StringEscapeUtils.escapeHtml4(objB[0].toString()): " - "%> <%="/"%> <%=objB[20]!=null?StringEscapeUtils.escapeHtml4(objB[20].toString()): " - "%> <%="/point6"%>" onchange="updateBpPoints(this)">
			                                                         9<input type="checkbox" <%if(objB[21].toString().equalsIgnoreCase("Y")){ %>checked<%} %> class="form-control" value="<%=objB[0]!=null?StringEscapeUtils.escapeHtml4(objB[0].toString()): " - "%> <%="/"%> <%=objB[21]!=null?StringEscapeUtils.escapeHtml4(objB[21].toString()): " - "%> <%="/point9"%>" onchange="updateBpPoints(this)">
			                                                         </div>
			                                                         </div>
			                                                         
	                                                         
	                                                         <%}%>	</td>
                                                         </tr>
                                                         <% int countC=1;
														 	if(MilestoneC!=null&&MilestoneC.size()>0){
															for(Object[] objC: MilestoneC){
																
																//check if empList contains first OIC of C level 
																if(!empList.contains(objC[13].toString() )){
																empList.add(objC[13].toString());
																}
																//check if empList contains first 2ndIC of C level 
																if(!empList.contains(objC[15].toString() )){
																	empList.add(objC[15].toString());
																	}
													         List<Object[]> MilestoneD=(List<Object[]>)request.getAttribute(count+"MilestoneActivityD"+countA+countB+countC);
																%>
														<tr class="collapse row_<%=objB[0] %> trclass<%=obj[0]%> trclass<%=objA[0]%> trclass<%=objB[0]%>" >
															<td  class="width-2"  class="center">

															
															 </td>
															<td class="center">
															<%if(MilestoneD!=null && MilestoneD.size()>0) {%>
															<span class="clickable" data-toggle="collapse" id="row_<%=objC[0] %>" data-target=".row_<%=objC[0]  %>">
																	<button class="btn btn-sm btn-success" id="btn<%=objC[0]  %>"  onclick="ChangeButton('<%=objC[0]  %>')">
																		<i class="fa fa-plus"  id="fa<%=objC[0] %>"></i> 
																	</button>
																</span>
																<%}else{ %>
															
															<% } %>
															</td>
															<td class="width-5 text-left" > &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;C-<%=countC%></td>
															<%-- <td class="width-30px"><%=obj[1]%></td> --%>
															<td class="tdDetails"
															 onclick="showMilestoneProgress('<%=obj[0]%>', '<%=objC[0]%>', '<%=ProjectId%>', 'C')">
															<%=objC[4]!=null?StringEscapeUtils.escapeHtml4(objC[4].toString()): " - " %>
															</td>
															
															<td class="width-30px"><%=sdf.format(objC[2])%></td>
															<td class="width-8" ><%=sdf.format(objC[3])%></td>
															<td><%=objC[14]!=null?StringEscapeUtils.escapeHtml4(objC[14].toString()): " - "%>
															<br>
															<%=objC[27]!=null?StringEscapeUtils.escapeHtml4(objC[27].toString()): " - "%>
													
															</td>
															<td class="width-30px"><%if(objC[9].toString().equalsIgnoreCase("3")||objC[9].toString().equalsIgnoreCase("5")){ %>
														     <%if(objC[7]!=null){ %>   <%=sdf.format(objC[7]) %> <%}else{ %><%=objC[8]!=null?StringEscapeUtils.escapeHtml4(objC[8].toString()): " - " %> <%} %>
														         <%}else{ %>
														         <%=objC[8]!=null?StringEscapeUtils.escapeHtml4(objC[8].toString()): " - " %>
															 <%} %></td>	
															  <td align="center"><%=objC[6]!=null?StringEscapeUtils.escapeHtml4(objC[6].toString()): " - " %></td>
															<td>
															<%if(!objC[5].toString().equalsIgnoreCase("0")){ %>
															<div class="progress progressDiv">
																<div class="progress-bar progress-bar-striped
																	<%if(objC[9].toString().equalsIgnoreCase("2")){ %>
																	 bg-success
																	<%} else if(objC[9].toString().equalsIgnoreCase("3")){ %>
																	  bg-info
																	<%} else if(objC[9].toString().equalsIgnoreCase("4")){ %>
																	  bg-danger
																	<%} else if(objC[9].toString().equalsIgnoreCase("5")){ %>
																	  bg-warning
																	<%}  %> width-<%=objC[5] %>
																	" role="progressbar" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
																	<%=objC[5]!=null?StringEscapeUtils.escapeHtml4(objC[5].toString()): " - " %>
																</div> 
															</div> <%}else{ %>
															<div class="progress progressDiv" >
															<div class="progress-bar noProgress" role="progressbar" >
															Not Started
															</div>
															</div> <%} %>
															</td>
															<td>
														                                                   <%if("A".equalsIgnoreCase(LoginType) || projectDirector.equals(empId) || Long.parseLong(objC[13].toString())==(empId)) { %>
                                                         		
                                                         
			                                                    	<div class="maindivInput">
			                                                         <div class="subdiv">
			                                                       5.  <input type="checkbox" <%if(objC[19].toString().equalsIgnoreCase("Y")){ %>checked<%} %> class="form-control" value="<%=objC[0]!=null?StringEscapeUtils.escapeHtml4(objC[0].toString()): " - "%> <%="/"%> <%=objC[19]!=null?StringEscapeUtils.escapeHtml4(objC[19].toString()): " - "%> <%="/point5"%>" onchange="updateBpPoints(this)">
			                                                       6.a  <input type="checkbox" <%if(objC[20].toString().equalsIgnoreCase("Y")){ %>checked<%} %> class="form-control" value="<%=objC[0]!=null?StringEscapeUtils.escapeHtml4(objC[0].toString()): " - "%> <%="/"%> <%=objC[20]!=null?StringEscapeUtils.escapeHtml4(objC[20].toString()): " - "%> <%="/point6"%>" onchange="updateBpPoints(this)">
			                                                        9. <input type="checkbox" <%if(objC[21].toString().equalsIgnoreCase("Y")){ %>checked<%} %> class="form-control" value="<%=objC[0]!=null?StringEscapeUtils.escapeHtml4(objC[0].toString()): " - "%> <%="/"%> <%=objC[21]!=null?StringEscapeUtils.escapeHtml4(objC[21].toString()): " - "%> <%="/point9"%>" onchange="updateBpPoints(this)">
			                                                        </div> </div>
                                                         
                                                         
                                                         		
                                                         	<%} %></td>
                                                         </tr>
                                                         <% int countD=1;
														 	if(MilestoneD!=null&&MilestoneD.size()>0){
															for(Object[] objD: MilestoneD){
																
																//check if empList contains first OIC of D level 
																if(!empList.contains(objD[13].toString() )){
																empList.add(objD[13].toString());
																}
																//check if empList contains first 2ndIC of D level 
																if(!empList.contains(objD[15].toString() )){
																	empList.add(objD[15].toString());
																	}
	                                                            List<Object[]> MilestoneE=(List<Object[]>)request.getAttribute(count+"MilestoneActivityE"+countA+countB+countC+countD);
	
																%>
														<tr class="collapse row_<%=objC[0] %> trclass<%=obj[0]%> trclass<%=objA[0]%> trclass<%=objB[0]%> trclass<%=objC[0]%>" >
															<td  class="center width-2">

															
															 </td>
															<td class="center">
																<%if(MilestoneE!=null && MilestoneE.size()>0) {%>
															<span class="clickable" data-toggle="collapse" id="row_<%=objD[0] %>" data-target=".row_<%=objD[0]  %>">
																	<button class="btn btn-sm btn-success" id="btn<%=objD[0]  %>"  onclick="ChangeButton('<%=objD[0]%>')">
																		<i class="fa fa-plus"  id="fa<%=objD[0] %>"></i> 
																	</button>
																</span>
																<%}else{ %>
													
 																	<%} %>
															
															</td>
															<td class="width-5 text-left" > &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;D-<%=countD%></td>
															<%-- <td class="width-30px"><%=obj[1]%></td> --%>
															<td class="tdDetails"
															 onclick="showMilestoneProgress('<%=obj[0]%>', '<%=objD[0]%>', '<%=ProjectId%>', 'D')">
															 <%=objD[4]!=null?StringEscapeUtils.escapeHtml4(objD[4].toString()): " - " %>
															 </td>
															
															<td class="width-30px"><%=sdf.format(objB[2])%></td>
															<td class="width-8" ><%=sdf.format(objB[3])%></td>
															<td><%=objD[14]!=null?StringEscapeUtils.escapeHtml4(objD[14].toString()): " - "%>
															<br>
														<%=objD[27]!=null?StringEscapeUtils.escapeHtml4(objD[27].toString()): " - "%>
															
															</td>
															<td class="width-30px"><%if(objD[9].toString().equalsIgnoreCase("3")||objD[9].toString().equalsIgnoreCase("5")){ %>
														      <%if(objD[7]!=null){ %>   <%=sdf.format(objD[7]) %> <%}else{ %><%=objD[8]!=null?StringEscapeUtils.escapeHtml4(objD[8].toString()): " - " %> <%} %>
														         <%}else{ %>
														         <%=objD[8]!=null?StringEscapeUtils.escapeHtml4(objD[8].toString()): " - " %>
															 <%} %></td>
															  <td align="center"><%=objD[6]!=null?StringEscapeUtils.escapeHtml4(objD[6].toString()): " - " %></td>
															<td>
															<%if(!objD[5].toString().equalsIgnoreCase("0")){ %>
															<div class="progress progressDiv" >
															<div class="progress-bar progress-bar-striped
															<%if(objD[9].toString().equalsIgnoreCase("2")){ %>
															 bg-success
															<%} else if(objD[9].toString().equalsIgnoreCase("3")){ %>
															  bg-info
															<%} else if(objD[9].toString().equalsIgnoreCase("4")){ %>
															  bg-danger
															<%} else if(objD[9].toString().equalsIgnoreCase("5")){ %>
															  bg-warning
															<%}  %> width-<%=objD[5] %>%
															" role="progressbar"  aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
															<%=objD[5]!=null?StringEscapeUtils.escapeHtml4(objD[5].toString()): " - " %>
															</div> 
															</div> <%}else{ %>
															<div class="progress progressDiv" >
															<div class="progress-bar noProgress" role="progressbar" >
															Not Started
															</div>
															</div> <%} %>
															</td>
															<td>
													      <%if("A".equalsIgnoreCase(LoginType) || projectDirector.equals(empId) || Long.parseLong(objD[13].toString())==(empId)) { %>											
                                                         		
			                                                     <div class="maindivInput">
			                                                         <div class="subdiv">
			                                                        5. <input type="checkbox" <%if(objD[19].toString().equalsIgnoreCase("Y")){ %>checked<%} %> class="form-control" value="<%=objD[0]!=null?StringEscapeUtils.escapeHtml4(objD[0].toString()): " - "%> <%="/"%> <%=objD[19]!=null?StringEscapeUtils.escapeHtml4(objD[19].toString()): " - "%> <%="/point5"%>" onchange="updateBpPoints(this)">
			                                                        6.a <input type="checkbox" <%if(objD[20].toString().equalsIgnoreCase("Y")){ %>checked<%} %> class="form-control" value="<%=objD[0]!=null?StringEscapeUtils.escapeHtml4(objD[0].toString()): " - "%> <%="/"%> <%=objD[20]!=null?StringEscapeUtils.escapeHtml4(objD[20].toString()): " - "%> <%="/point6"%>" onchange="updateBpPoints(this)">
			                                                        9. <input type="checkbox" <%if(objD[21].toString().equalsIgnoreCase("Y")){ %>checked<%} %> class="form-control" value="<%=objD[0]!=null?StringEscapeUtils.escapeHtml4(objD[0].toString()): " - "%> <%="/"%> <%=objD[21]!=null?StringEscapeUtils.escapeHtml4(objD[21].toString()): " - "%> <%="/point9"%>" onchange="updateBpPoints(this)">
			                                                      </div>   </div>
                                                         
                                                         
                                                         		
                                                         	<%} %></td>	
                                                         </tr>
                                                         <% int countE=1;
														 	if(MilestoneE!=null&&MilestoneE.size()>0){
															for(Object[] objE: MilestoneE){
																//check if empList contains first OIC of E level 
																if(!empList.contains(objE[13].toString() )){
																empList.add(objE[13].toString());
																}
																//check if empList contains first 2ndIC of A level 
																if(!empList.contains(objE[15].toString() )){
																	empList.add(objE[15].toString());
																	}
																
																%>
														<tr class="collapse row_<%=objD[0] %> trclass<%=obj[0]%> trclass<%=objA[0]%> trclass<%=objB[0]%> trclass<%=objC[0]%> trclass<%=objD[0]%>"   >
															<td   class="center width-2">
												
															
															 </td>
															<td class="center">
															<%if(!objE[24].toString().equalsIgnoreCase("0") ){ %>
														
 															<%} %>
 															</td>
															<td class="width-5 text-left" > &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;E-<%=countE%></td>
															<%-- <td class="width-30px"><%=obj[1]%></td> --%>
															<td class="tdDetails"
															onclick="showMilestoneProgress('<%=obj[0]%>', '<%=objE[0]%>', '<%=ProjectId%>', 'E')">
															<%=objE[4]!=null?StringEscapeUtils.escapeHtml4(objE[4].toString()): " - " %>
															</td>
															
															<td class="width-30px"><%=sdf.format(objE[2])%></td>
															<td  class="width-8"><%=sdf.format(objE[3])%></td>
															<td><%=objE[14]!=null?StringEscapeUtils.escapeHtml4(objE[14].toString()): " - "%>
															<br>
															<%=objE[27]!=null?StringEscapeUtils.escapeHtml4(objE[27].toString()): " - "%>
															</td>
															<td class="width-30px"><%if(objE[9].toString().equalsIgnoreCase("3")||objE[9].toString().equalsIgnoreCase("5")){ %>
														     <%if(objE[7]!=null){ %>   <%=sdf.format(objE[7]) %> <%}else{ %><%=objE[8]!=null?StringEscapeUtils.escapeHtml4(objE[8].toString()): " - " %> <%} %>
														         <%}else{ %>
														         <%=objE[8]!=null?StringEscapeUtils.escapeHtml4(objE[8].toString()): " - " %>
															 <%} %></td>	
															  <td align="center"><%=objE[6]!=null?StringEscapeUtils.escapeHtml4(objE[6].toString()): " - " %></td>
															<td>
															<%if(!objE[5].toString().equalsIgnoreCase("0")){ %>
															<div class="progress progressDiv">
															<div class="progress-bar progress-bar-striped
															<%if(objC[9].toString().equalsIgnoreCase("2")){ %>
															 bg-success
															<%} else if(objE[9].toString().equalsIgnoreCase("3")){ %>
															  bg-info
															<%} else if(objE[9].toString().equalsIgnoreCase("4")){ %>
															  bg-danger
															<%} else if(objE[9].toString().equalsIgnoreCase("5")){ %>
															  bg-warning
															<%}  %> width-<%=objE[5] %>
															" role="progressbar" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
															<%=objE[5]!=null?StringEscapeUtils.escapeHtml4(objE[5].toString()): " - " %>
															</div> 
															</div> <%}else{ %>
															<div class="progress progressDiv" >
															<div class="progress-bar noProgress" role="progressbar"   >
															Not Started
															</div>
															</div> <%} %>
															</td>
															
															      <td>                                             <%if("A".equalsIgnoreCase(LoginType) || projectDirector.equals(empId) || Long.parseLong(objE[13].toString())==(empId)) { %>
                                                         		
			                                                         <div class="maindivInput">
			                                                         <div class="subdiv" >
			                                                        5. <input type="checkbox" <%if(objE[19].toString().equalsIgnoreCase("Y")){ %>checked<%} %> class="form-control" value="<%=objE[0]!=null?StringEscapeUtils.escapeHtml4(objE[0].toString()): " - "%> <%="/"%> <%=objE[19]!=null?StringEscapeUtils.escapeHtml4(objE[19].toString()): " - "%> <%="/point5"%>" onchange="updateBpPoints(this)">
			                                                        6.a <input type="checkbox" <%if(objE[20].toString().equalsIgnoreCase("Y")){ %>checked<%} %> class="form-control" value="<%=objE[0]!=null?StringEscapeUtils.escapeHtml4(objE[0].toString()): " - "%> <%="/"%> <%=objE[20]!=null?StringEscapeUtils.escapeHtml4(objE[20].toString()): " - "%> <%="/point6"%>" onchange="updateBpPoints(this)">
			                                                        9 <input type="checkbox" <%if(objE[21].toString().equalsIgnoreCase("Y")){ %>checked<%} %> class="form-control" value="<%=objE[0]!=null?StringEscapeUtils.escapeHtml4(objE[0].toString()): " - "%> <%="/"%> <%=objE[21]!=null?StringEscapeUtils.escapeHtml4(objE[21].toString()): " - "%> <%="/point9"%>" onchange="updateBpPoints(this)">
			                                                       </div>
			                                                         </div>
                                                         
                           
                                                         	<%} %>  
                                                                                    		</td>
                                                         </tr>
                                                      
												<% countE++;} }%>
												<% countD++;} }%>
												<% countC++;} }%>
												<% countB++;} }%>
												<% countA++;} }else{%>
												<tr class="collapse row<%=count %>">
													<td colspan="11"  class="center text-center">No Sub List Found</td>
												</tr>
										       
												<%} %>
												<%
												if(!Arrays.asList(obj[19].toString(),obj[17].toString()).contains(empId+"")){
												%>
												<script>
												 var empListJs = [<%= empList.stream().map(e -> "\"" + e + "\"").collect(java.util.stream.Collectors.joining(",")) %>];
												 var empId = '<%=empId %>';
												 var loginType = '<%=LoginType %>';
												 var projectDirector = '<%=projectDirector %>';
												 var mileId = '<%=obj[0].toString() %>';
												 
												<%--  var progress = '<%=obj[12]%>'; --%>
												  
												    
												    
												   if( empListJs.includes(empId) || empId===projectDirector || loginType==='A') {
													   console.log("Any of the codition satisfied")
												   }else{
													     
													   console.log("No codition satisfied")
													   $('#detailsAddbtn'+mileId).hide();
													   $('#EditWeightage'+mileId).hide();
													   $('#basLineBtn'+mileId).hide();
													   $('#assignBtn'+mileId).hide();
													   $('#docbtn'+mileId).hide();
													   $('#spanMessage'+mileId).show();
													
												   }
												</script>
												<%} %>
												<% count++; } %>
												
												<%if(LoginType.equalsIgnoreCase("A") || projectDirector.toString().equalsIgnoreCase(""+empId)) { %>
													<tr>
														<td></td>
														<td colspan=1 class="tdDiv1" >
															<form action="MilestoneActivityMilNoUpdate.htm" method="POST" name="slnoupdateform" id="slnoupdateform">
							              						<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
							              						<input type="hidden" name="projectId" value="<%=ProjectId%>">
							              						<button class="btn btn-sm edit" onclick="return slnocheck();">UPDATE</button>
						              						</form>
					              						</td>
														<td colspan="9"></td>
													</tr>
													<% } %>
												<% }else{%>
												<tr >
													<td colspan="11"  class="center text-center">No List Found</td>
												</tr>
												<%} %>
												</tbody>
												</table>
												</div>
							


											</div>

							<div class="sunset-info-note">
							    <span class="info-icon"><i class="fa fas fa-info-circle"></i></span>
							    <span class="info-text">Note: A maximum of <strong>3 milestones</strong> can be marked as sunset for a project.</span>
							</div>
							
						</div>

					</div>
		
				</div>

	
			</div>


		<br>

	<div class="row m-2">
		<div class="col-md-12 milestoneFlow"
			>
			<b>Milestone Flow </b>
		</div>
	</div>
	
	<div class="row m-2 class1"
		>

		<table align="center" >
			<tr>
				
				
				<td class="trup trupHead">
					<b class="text-primary">Add Milestone Activity </b>
				</td>
				<td class="trup trupBody" ></td>
				<td ><i class="fa fa-long-arrow-right "aria-hidden="true"></i></td>
				<td rowspan="2" class="trup trupFooter" ></td>
				
				<td class="trup trupHead" >
					<b class="text-primary">Add Sub Milestone Activity </b>
				</td>
				<td class="trup trupBody" ></td>
				<td ><i class="fa fa-long-arrow-right "aria-hidden="true"></i></td>
				<td rowspan="2" class="trup trupFooter" ></td>
				
				<td class="trup trupHead">
					<b class="text-primary">Assign Weightage </b>
				</td>
				<td class="trup trupBody" ></td>
				<td ><i class="fa fa-long-arrow-right "aria-hidden="true"></i></td>
				<td rowspan="2" class="trup trupFooter" ></td>
				
				<td class="trup trupHead" >
					<b class="text-primary">Assign Milestone Activity </b>
				</td>
				<td class="trup trupBody" ></td>
				<td ><i class="fa fa-long-arrow-right "aria-hidden="true"></i></td>
				<td rowspan="2" class="trup trupFooter" ></td>
				
				<td class="trup trupHead"  >
					<b class="text-primary">Set Baseline </b>
				</td>
				<td class="trup trupBody" ></td>
				<td ><i class="fa fa-long-arrow-right "aria-hidden="true"></i></td>
				<td rowspan="2" class="trup trupFooter" ></td>
				
				<td class="trup trupHead" >
					<b class="text-primary">Assignee</b>
				</td>
				<td class="trup trupBody" ></td>
				<td ><i class="fa fa-long-arrow-right "aria-hidden="true"></i></td>
				<td rowspan="2" class="trup trupFooter" ></td>
				
				<td class="trup trupHead" >
					<b class="text-primary"> Acknowledge Milestone Activity </b>
				</td>
				<td class="trup trupBody" ></td>
				<td ><i class="fa fa-long-arrow-right "aria-hidden="true"></i></td>
				<td rowspan="2" class="trup trupFooter" ></td>
				
				<td class="trup trupHead" >
					<b class="text-primary">Update progress</b>
				</td>
				
				
			

			</tr>
			
		</table>



	</div>
	<br> 
	<br>
	<br>

<div class="modal" id="MainDOCEditModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="exampleModalLongTitle">Edit Date of Completion</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body" align="center">
        <form action="MainMilestoneDOCUpdate.htm" method="post">
        	<table class="width-80" >
        		<tr>
        			<th class="width-40" >Date of Completion : &nbsp; </th>
        			<td class="width-60" ><input type="text" class="form-control" name="DateOfCompletion" id="MainDOCDate" value="" readonly="readonly"></td>
        		</tr>
        		<tr>
        			<td colspan="2" >
        				<br>
        				<button type="button" class="btn btn-sm btn-danger" data-dismiss="modal"><b>Close</b></button>
        				<button class="btn btn-sm submit" onclick="return confirm('Are You Sure to Edit?');">SUBMIT</button>
        			</td>
        		</tr>
        	</table>
        	
        	<input type="hidden" id="MSMainid" name="MSMainid" value="" >
        	<input type="hidden" name="projectid" value="<%=ProjectId %>" >
        	<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" />
        </form>
      </div>
     
    </div>
  </div>
</div>




	<!-- -------------------------------------------- Milestone Progess Modal -------------------------------------------- -->
	<div class="modal fade" id="milestoneProgressModal" tabindex="-1" role="dialog" aria-labelledby="milestoneProgressModal" aria-hidden="true">
  		<div class="modal-dialog modal-lg modal-dialog-jump" role="document">
    		<div class="modal-content modalContentDiv" >
      			<div class="modal-header modalHeaderDiv" id="ModalHeader" >
			        <h5 class="modal-title" >Activity Updated Details</h5>
			        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
			          <span aria-hidden="true" class="text-light">&times;</span>
			        </button>
      			</div>
      			
      			<div class="modal-body">
     				<div class="row mb-2">
						<div class="col-md-12">
							<table class="table table-bordered width-100" id="myTables" >
								<thead class="cente modalHeaderDiv" >
									<tr>
										<th class="width-5" >SN</th>
										<th class="width-15" >As On Date</th>
										<th  class="width-15" >Progress</th>
										<th class="width-25">Remarks</th>
										<th class="width-5">Action</th>
										<th class="width-35">Progress By</th>
									</tr>
								</thead>
								<tbody id="milestoneprogesstbody">
								</tbody>
							</table>
						</div>      
     				</div>
      				
      			</div>
    		</div>
  		</div>
	</div>
	<!-- -------------------------------------------- Milestone Progress Modal End -------------------------------------------- -->


	<!-- -------------------------------------------- Milestone Status Remarks Modal -------------------------------------------- -->
	<div class="modal fade" id="milestoneStatusRemarksModal" tabindex="-1" role="dialog" aria-labelledby="milestoneStatusRemarksModal" aria-hidden="true">
  		<div class="modal-dialog modal-lg modal-dialog-jump" role="document">
    		<div class="modal-content modalContentDiv" >
      			<div class="modal-header modalHeaderDiv" id="ModalHeader">
			        <h5 class="modal-title" >Status Remarks</h5>
			        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
			          <span aria-hidden="true" class="text-light">&times;</span>
			        </button>
      			</div>
      			
      			<div class="modal-body">
      				<div class="row">
      					<div class="col-md-12">
      						<label class="form-label">Financial Outlay :</label> 
      						<span id="financialOutlay"></span>
						</div>  
      				</div>
     				<div class="row mb-2">
						<div class="col-md-12">
							<label class="form-label">Remarks : </label>  
							<div id="statusRemarks"></div>
						</div>      
     				</div>
      				
      			</div>
    		</div>
  		</div>
	</div>
	<!-- -------------------------------------------- Milestone Status Remarks Modal End -------------------------------------------- -->


	
<script type="text/javascript">
function MainDOCEditModal(mainid, DOC)
{
	$('#MSMainid').val(mainid);			
	$('#MainDOCDate').daterangepicker({
		"singleDatePicker" : true,
		"linkedCalendars" : false,
		"showCustomRangeLabel" : true,
		"startDate" : new Date(DOC),
		"cancelClass" : "btn-default",
		showDropdowns : true,
		locale : {
			format : 'DD-MM-YYYY'
		}
	});
	$('#MainDOCEditModal').modal('toggle');
	
}
															 
</script>  

  
<script>


$(document).ready(function() {
	   $('#ProjectId').on('change', function() {
	     $('#submit').click();

	   });
	});
	
	 
function ChangeButton(id) {
    // Show loading using SweetAlert2
    Swal.fire({
       
        allowOutsideClick: false,
        allowEscapeKey: false,
        didOpen: () => {
            Swal.showLoading();
        }
    });

    // Give DOM time to update (for smoother animation), then process logic
    setTimeout(() => {
        if ($("#btn" + id).hasClass("btn btn-sm btn-success").toString() == 'true') {
            $("#btn" + id).removeClass("btn btn-sm btn-success").addClass("btn btn-sm btn-danger");
            $("#fa" + id).removeClass("fa fa-plus").addClass("fa fa-minus");
        } else {
            var targetRow = $(".trclass" + id);
            targetRow.collapse("hide");
            $("#btn" + id).removeClass("btn btn-sm btn-danger").addClass("btn btn-sm btn-success");
            $("#fa" + id).removeClass("fa fa-minus").addClass("fa fa-plus");
       
            if (targetRow && targetRow.length > 0) {
                targetRow.find("button.btn.btn-sm.btn-danger").each(function () {
                    $(this)
                        .removeClass("btn btn-sm btn-danger")
                        .addClass("btn btn-sm btn-success");

                    $(this).find("i.fa.fa-minus")
                        .removeClass("fa fa-minus")
                        .addClass("fa fa-plus");
                });
            }
        
        }

     

    
        Swal.close();  
    }, 300); // Small timeout to allow loading to render
}



</script>




<script>
	$('#DateCompletion').daterangepicker({
			"singleDatePicker" : true,
			"linkedCalendars" : false,
			"showCustomRangeLabel" : true,
			"minDate" : new Date(),
			"cancelClass" : "btn-default",
			showDropdowns : true,
			locale : {
				format : 'DD-MM-YYYY'
			}
		});

	$('#DateCompletion2').daterangepicker({
		"singleDatePicker" : true,
		"linkedCalendars" : false,
		"showCustomRangeLabel" : true,
		"minDate" : new Date(),
		"cancelClass" : "btn-default",
		showDropdowns : true,
		locale : {
			format : 'DD-MM-YYYY'
		}
	});
	
	/* 
	function updateBpPoints(ele){
		var a=ele.value;
		var value1=a.split("/")[0];
		var value2=a.split("/")[1];
		var value3=a.split("/")[2]
		
		$.ajax({
			type:'GET',
			url:'BriefingPointsUpdate.htm',
			datatype:'json',
			data:{
				ActivityId:value1,
				point:value3,
				status:value2,
			}
		});
		
		
		if(value2==="Y"){
		ele.value=value1+"/N/"+value3
		}else{
			ele.value=value1+"/Y/"+value3
		}
	} */
	
	function updateBpPoints(ele){

	    var arr = ele.value.split("/");

	    var activityId = arr[0];
	    var point = arr[2];

	    var status = !ele.checked ? "Y" : "N";

	    $.ajax({
	        type: "GET",
	        url: "BriefingPointsUpdate.htm",
	        dataType: "json",
	        data: {
	            ActivityId: activityId,
	            point: point,
	            status: status
	        },
	        success: function() {
	            ele.value = activityId + "/" + status + "/" + point;
	        }
	    });
	}
	
	// Milestone No Check
	function slnocheck() {
		
		 var arr = document.getElementsByName("newslno");

		var arr1 = [];
		for (var i=0;i<arr.length;i++){
			arr1.push(arr[i].value);
		}		 
		 
	    let result = false;
	  
	    const s = new Set(arr1);
	    
	    if(arr.length !== s.size){
	       result = true;
	    }
	    if(result) {
	   		event.preventDefault();
	       	alert('Serial No contains duplicate Values');
	       	return false;
	    } else {
	   	 return confirm('Are You Sure to Update?');
	    }
	  }
	
	function showMilestoneProgress(MilestoneActivityId, ActivityId, ProjectId, ActivityType) {
		$('#milestoneProgressModal').modal('show');
		
		$.ajax({
			type : "GET",
			url : "MilestoneActivityProgressDetails.htm",	
			datatype : 'json',
			data : {
				MilestoneActivityId : MilestoneActivityId,				
				ActivityId : ActivityId,				
				ProjectId : ProjectId,				
				ActivityType : ActivityType,				
			},
			success : function(result) {
				var values = JSON.parse(result);
				
				var x ='';
				
				if(values.length>0){
					
					for(var i=0; i<values.length; i++) {
						x+= '<tr>';
						x+= '<td class="center">'+(i+1)+'.</td>';
						x+= '<td class="center">'+formatDate(values[i][2])+'</td>';
						x += '<td>';
					    x += '<div class="progress" style="background-color: #cdd0cb !important">';
					    x += '<div class="progress-bar progress-bar-striped" role="progressbar" style="width: ' + values[i][1] + '%;"';
					    x += ' aria-valuenow="' + values[i][1] + '" aria-valuemin="0" aria-valuemax="100">' + values[i][1] + '</div>';
					    x += '</div>';
					    x += '</td>';
						x+= '<td>'+values[i][3]+'</td>';
						x += '<td class="center">';
						if (values[i][4] && values[i][4].toString().length !== 0) {
					        x += '<div align="center">';
					        x += '<a href="ActivityAttachDownload.htm?ActivitySubId=' + values[i][0] + '" target="_blank">';
					        x += '<i class="fa fa-download"></i></a>';
					        x += '</div>';
					    } else {
					        x += '<div align="center">-</div>';
					    }
						x += '</td>';
						x+='<td>'+ values[i][5] +'</td>'
						x+= '</tr>';
					}
				}else{
					x = '<tr><td colspan="5" class="center">No Data Available</td></tr>';
				}
				
				$('#milestoneprogesstbody').html(x);
			}
		});
	}
	
	function formatDate(sqlDate) {
	    if (!sqlDate) {
	        return ""; // Handle null or undefined dates
	    }

	    let date = new Date(sqlDate);
	    let day = String(date.getDate()).padStart(2, '0');
	    let month = String(date.getMonth() + 1).padStart(2, '0'); // Months are 0-indexed
	    let year = date.getFullYear();

	    return day + '-' + month + '-' + year;
	}
	
	function showMilestoneStatusProgress(rowId) {
		$('#financialOutlay').text($('#financialOutlay_'+rowId).val());
		$('#statusRemarks').html($('#statusRemarks_'+rowId).val());
		$('#milestoneStatusRemarksModal').modal('show');
	}
	
	
	$(function () {
		$('[data-toggle="tooltip"]').tooltip()
	})
	
	
	function deleteMainMilestone(mainid) {

    if (!confirm("Are you sure to Delete?")) {
        return;
    }

    const form = document.createElement("form");

    form.method = "POST";
    form.action = "DeleteMainMilestone.htm";

    // Main Milestone ID
    const mainIdInput = document.createElement("input");
    mainIdInput.type = "hidden";
    mainIdInput.name = "mainid";
    mainIdInput.value = mainid;

    // Project ID
    const projectIdInput = document.createElement("input");
    projectIdInput.type = "hidden";
    projectIdInput.name = "projectId";
    projectIdInput.value = '<%=ProjectId%>';

    // CSRF Token
    const csrfInput = document.createElement("input");
    csrfInput.type = "hidden";
    csrfInput.name = "${_csrf.parameterName}";
    csrfInput.value = "${_csrf.token}";

    form.appendChild(mainIdInput);
    form.appendChild(projectIdInput);
    form.appendChild(csrfInput);

    document.body.appendChild(form);

    form.submit();
}
	
	

function handleSunSet(milestoneActivityId, levelId, isSunSet) {

	const sunsetCount = <%= sunsetCount %>;
	/* 
    $.ajax({
        type: "POST",
        url: "UpdateMilestoneSunSet.htm",

        data: {
            milestoneActivityId: milestoneActivityId,
            levelId: levelId,
            isSunSet: isSunSet,
            "${_csrf.parameterName}": "${_csrf.token}"
        },

        success: function(response) {
            console.log("Sunset updated successfully:", response);
        },

        error: function(xhr, status, error) {
            console.error("Status:", xhr.status);
            console.error("Error:", error);
            console.error("Response:", xhr.responseText);
        }
    });
}

	function handleSunSet(milestoneActivityId, levelId, isSunSet) {

		 */
		 
		 if(isSunSet === "N" && sunsetCount > 2){
			alert("More than Three Milestones cannot be marked as SunSet Milestone!");
			return;
		 }
		 
		 const isConfirm = confirm(
				    isSunSet === "N"
				        ? "Are you sure you want to Mark this as a SunSet Milestone?"
				        : "Are you sure you want to remove the SunSet Milestone designation?"
				);

		if (!isConfirm) {
		    return;
		}
		 
	    const form = document.createElement("form");

	    form.method = "POST";
	    form.action = "UpdateMilestoneSunSet.htm";

	    // Milestone Activity ID
	    const activityInput = document.createElement("input");
	    activityInput.type = "hidden";
	    activityInput.name = "milestoneActivityId";
	    activityInput.value = milestoneActivityId;
	    form.appendChild(activityInput);

	    // Level ID
	    const levelInput = document.createElement("input");
	    levelInput.type = "hidden";
	    levelInput.name = "levelId";
	    levelInput.value = levelId;
	    form.appendChild(levelInput);

	    // Sunset value
	    const sunsetInput = document.createElement("input");
	    sunsetInput.type = "hidden";
	    sunsetInput.name = "isSunSet";
	    sunsetInput.value = isSunSet;
	    form.appendChild(sunsetInput);

	    // CSRF
	    const csrfInput = document.createElement("input");
	    csrfInput.type = "hidden";
	    csrfInput.name = "${_csrf.parameterName}";
	    csrfInput.value = "${_csrf.token}";
	    form.appendChild(csrfInput);

	    // Project ID
	    const projectId = document.createElement("input");
	    projectId.type = "hidden";
	    projectId.name = "projectId";
	    projectId.value = "<%= ProjectId %>";
	    form.appendChild(projectId);
	    
	    
	    document.body.appendChild(form);

	    form.submit();
	}
</script>  


</body>
</html>