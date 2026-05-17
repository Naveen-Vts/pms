<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.util.Map"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
 
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/sweetalert2.min.css"
	var="sweetalertCss" />
<spring:url value="/resources/js/sweetalert2.min.js" var="sweetalertJs" />
<link href="${sweetalertCss}" rel="stylesheet" />
<script src="${sweetalertJs}"></script>
<spring:url value="/resources/css/milestone/mAAssigneeList.css" var="mAAssigneeList" />     
<link href="${mAAssigneeList}" rel="stylesheet" />
<title>Milestone Assignee List</title>

</head>
<body>
<%
  List<Object[]> MilestoneList=(List<Object[]>)request.getAttribute("MilestoneActivityList");
  List<Object[]> ProjectList=(List<Object[]>)request.getAttribute("ProjectList");
  List<Object[]> totalAssignedMainList = (List<Object[]>)request.getAttribute("totalAssignedMainList");
  List<Object[]> totalAssignedSubList = (List<Object[]>)request.getAttribute("totalAssignedSubList");
  //Map<Long, Long> milestoneActivityIdsMap = (Map<Long, Long>)request.getAttribute("milestoneActivityIdsMap");
  Map<Long, Object[]> milestoneActivityLevelsMap = (Map<Long, Object[]>)request.getAttribute("milestoneActivityLevelsMap");
  SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
  SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
  String ProjectId=(String)request.getAttribute("ProjectId");
  Long empId = (Long)session.getAttribute("EmpId");
  String LoginType = (String)session.getAttribute("LoginType");
  Long projectDirector =0l;
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
    
	<form class="form-inline"  method="POST" action="M-A-AssigneeList.htm">
  		<div class="row W-100 width-100" >

  			<div class="col-md-7">
   
	  			<p>
	  				<b  class="colorLabel"><span class="label label-primary"> &nbsp;&nbsp;&nbsp;&nbsp; Project &nbsp; </span> </b>
	  				<i class="fa fa-long-arrow-right " aria-hidden="true"></i>
					<b class="colorLabel"><span class="label label-warning">&nbsp;Accept&nbsp;</span></b>
					<i class="fa fa-long-arrow-right " aria-hidden="true"></i>
					<b class="colorLabel"><span class="label label-success">&nbsp;&nbsp;Update Progress&nbsp;&nbsp;</span></b>
      			</p>
	                     
  			</div>
			<div class="col-md-2 textRight" >
			   <label class="control-label" >Project Name :  </label>
			</div>
			<div class="col-md-2" >
     			<select class="form-control selectdee" id="ProjectId" required="required" name="ProjectId" onchange="this.form.submit()">
     				<option disabled="disabled"  selected value="">Choose...</option>
     				<% for (Object[] obj : ProjectList) {
     						String projectshortName=(obj[17]!=null)?" ("+obj[17].toString().trim()+") ":""; %>
	 					<option value="<%=obj[0]%>" <%if(obj[0].toString().equalsIgnoreCase(ProjectId)){ %>selected="selected" <% projectDirector = Long.parseLong(obj[23].toString());} %>><%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - "%> <%=projectshortName!=null?StringEscapeUtils.escapeHtml4(projectshortName): " - "%>  </option>
	 				<%} %>
     			</select>
			</div>
 			<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
 		</div>
	</form>
	
	<br />

	<div class="container-fluid" >
		<div class="row">
			<div class="col-md-12">
				<div class="card shadow-nohover">
					<div class="row card-header">
			     		<div class="col-md-10">
							<h5 >
								<%if(ProjectId!=null){
									Object[] ProjectDetail=(Object[])request.getAttribute("ProjectDetails");
								%>
									<%=ProjectDetail[2]!=null?StringEscapeUtils.escapeHtml4(ProjectDetail[2].toString()): " - " %> (<%=ProjectDetail[1]!=null?StringEscapeUtils.escapeHtml4(ProjectDetail[1].toString()): " - "%>) 
								<%} %>
					 			Milestone List
					 		</h5>
						</div>
						
					</div>
					<div class="card-body">
                    	<div class="table-responsive"> 
							<table class="table table-condensed table-striped table-hover table-bordered width-100" id="myTable">
								<thead class="center">
									<tr>
										<th class="width-3" width="3%">SN</th>
										<th class="width-2"  width="2%">Main</th>
									    <th class="width-9"  width="9%">Sub</th>
										<th class="width-22"  width="22%">Milestone Activity</th>
										<th class="width-8"  width="8%">Start Date</th>
										<th class="width-8"  width="8%">End Date</th>	
										<th class="width-15"  width="15%">First OIC</th>
										<th class="width-8"  width="8%">Status</th>
										<th class="width-5"  width="5%">Weightage</th>	
										<th class="width-10"  width="10%">Progress</th>												
									 	<th class="width-10"  width="10%">Action</th>
									</tr>
								</thead>
								<tbody>
									<%
									int slno = 0;
									if(totalAssignedMainList!=null && totalAssignedMainList.size()>0) {
										for(Object[] obj : totalAssignedMainList) {
									%>
										<tr>
											<td class="center">
												<%=++slno %>.
											</td>
											<td class="center">M<%=obj[5]!=null?StringEscapeUtils.escapeHtml4(obj[5].toString()): " - "%></td>
											<td class="center"></td>
											<td class="details">
												<%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - " %>
											</td>
											<td class="center"><%=sdf.format(obj[2])%></td>
											<td class="center"><%=sdf.format(obj[3])%></td>
											<td><%=obj[6]!=null?StringEscapeUtils.escapeHtml4(obj[6].toString()): " - "%></td>
											<td class="center">-</td>
											<td class="center"><%=obj[13]!=null?StringEscapeUtils.escapeHtml4(obj[13].toString()): " - "%></td>	
											<td>
												<%if(!obj[12].toString().equalsIgnoreCase("0")){ %>
													<div class="progress progressdiv" >
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
															" role="progressbar"  aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
															<%=obj[12]!=null?StringEscapeUtils.escapeHtml4(obj[12].toString()): " - " %>
														</div> 
													</div> 
												<%}else{ %>
													<div class="progress progressdiv" >
														<div class="progress-bar NoProgress" role="progressbar"  >
															Not Started
														</div>
													</div> 
												<%} %>
											</td>
											<td>		
												<%if(obj[8]!=null&&"Y".equalsIgnoreCase(obj[10].toString())){ %>
                        	                		<button type="button" class="btn edit" title="Update Remarks" onclick="getCommentBox('<%=obj[0] %>','<%=obj[5] %>','<%=obj[15]!=null? obj[15].toString().replaceAll("\n", " ").replaceAll("\r", " "):"" %>','<%=obj[16] %>')"  data-toggle="tooltip" data-placement="top" data-original-data="Remarks" title="Remarks">
                        	                		 <i class="fa fa-comment" aria-hidden="true" ></i>
                        	                		 </button>
												<%} %>
												<%if("A".equalsIgnoreCase(obj[10].toString())&&((Long) session.getAttribute("EmpId")).toString().equals(obj[11].toString())){ %> 
                                                	<form action="MilestoneActivityUpdate.htm" method="POST" name="myfrm"  >
                                                    	<button  class="editable-click" name="sub" value="Accept"  formaction="M-A-Assign-OIC.htm" onclick="return confirm('Are You Sure To Accept?')" >
															<div class="cc-rockmenu">
																<div class="rolling">	
											                    	<figure class="rolling_icon"><img src="view/images/accept.jpg" ></figure>
											                        <span>Accept</span>
											                    </div>
											   				</div>
											    		</button> 
											            <input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
														<input type="hidden" name="MilestoneActivityId" value="<%=obj[0]%>"/>
														<input type="hidden" name="RevId" value="<%=obj[8]%>"/>
														<input type="hidden" name="projectid" value="<%=ProjectId%>"/>
															
												    </form> 
												    
								                  	<form action="MilestoneActivityUpdate.htm" method="POST" name="myfrm"  >
								                    	<button type="button"  class="editable-click" name="sub" value="Back"   data-toggle="modal" data-target="#exampleModal<%=obj[0]%>" data-whatever="@mdo" >
															<div class="cc-rockmenu">
																<div class="rolling">	
											                        <figure class="rolling_icon"><img src="view/images/sendback.jpg" ></figure>
											                        <span>Send Back</span>
											                	</div>
											           		</div>
											        	</button> 
											       		<input type="hidden" name="projectid" value="<%=ProjectId%>"/>
											            <div class="modal fade" id="exampleModal<%=obj[0]%>" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                                              				<div class="modal-dialog">
                                          						<div class="modal-content">
                                             						<div class="modal-header">
                                            							<h5 class="modal-title" id="exampleModalLabel">Add Remarks</h5>
                                   										<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                    										<span aria-hidden="true">&times;</span>
                                      									</button>
                                        							</div>
                                         							<div class="modal-body">
          	                                  							<div class="row">
                       				 
										                                     <div class="col-md-12">
																				<div class="form-group">
																					<label class="control-label">Remark</label> 
																				    <textarea class="form-control heightClass" name="Remarks"  maxlength="255" required="required" placeholder="Enter Send Back Remark here with max 255 characters"></textarea>
																				</div>
																			</div>       
                                         								</div>
                                       									<div class="modal-footer">
                                         									<button class="btn btn-sm btn-danger"   name="sub" value="Back"  formaction="M-A-Assign-OIC.htm" onclick="return confirm('Are You Sure To Send Back ?')">SendBack</button>
                                                						</div>
                                                 					</div>
                                                   				</div>
                                                  			</div>
											           	</div>       
											            <input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
														<input type="hidden" name="MilestoneActivityId" value="<%=obj[0]%>"/>
													</form> 
												<% } %>
																		
											</td>
		
										</tr>
									<%} }%>
									
									<%if(totalAssignedSubList!=null && totalAssignedSubList.size()>0) {
										for(Object[] obj : totalAssignedSubList) {
											Object[] levelsMapData = milestoneActivityLevelsMap.get(Long.parseLong(obj[0].toString()));
									%>
										<tr>
											<td class="center"><%=++slno %>. </td>
											<td class="center"><%=levelsMapData[2]!=null?StringEscapeUtils.escapeHtml4(levelsMapData[2].toString()): " - " %></td>
											<td class="center"><%=levelsMapData[1]!=null?StringEscapeUtils.escapeHtml4(levelsMapData[1].toString()): " - " %> </td>
											<td class="details">
												<%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - " %>
											</td>
															
											<td class="center"><%=sdf.format(obj[2])%></td>
											<td class="center"><%=sdf.format(obj[3])%></td>
											<td><%=obj[14] %></td>		      
											<td>
												<%if(obj[9].toString().equalsIgnoreCase("3")||obj[9].toString().equalsIgnoreCase("5")){ %>
													<%if(obj[7]!=null){ %>
														<%=sdf.format(obj[7]) %>
													<%}else{ %>
														<%=obj[8]!=null?StringEscapeUtils.escapeHtml4(obj[8].toString()): " - " %>
													<%} %>
												<%}else{ %>
													<%=obj[8]!=null?StringEscapeUtils.escapeHtml4(obj[8].toString()): " - " %>
												<%} %>
												
											</td>
											<td class="center"><%=obj[6]!=null?StringEscapeUtils.escapeHtml4(obj[6].toString()): " - " %></td>
											<td>
												<%if(!obj[5].toString().equalsIgnoreCase("0")){ %>
													<div class="progress progressdiv" >
														<div class="progress-bar progress-bar-striped width-<%=obj[5] %>
															<%if(obj[9].toString().equalsIgnoreCase("2")){ %>
																bg-success
															<%} else if(obj[9].toString().equalsIgnoreCase("3")){ %>
																bg-info
															<%} else if(obj[9].toString().equalsIgnoreCase("4")){ %>
																bg-danger
															<%} else if(obj[9].toString().equalsIgnoreCase("5")){ %>
																bg-warning
															<%}  %>
															" role="progressbar" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
															<%=obj[5]!=null?StringEscapeUtils.escapeHtml4(obj[5].toString()): " - " %>
														</div> 
													</div> 
												<%}else{ %>
													<div class="progress progressdiv" >
														<div class="progress-bar NoProgress" role="progressbar"   >
															Not Started
														</div>
													</div> 
												<%} %>
											</td>
                                                      
											<td class="tdFlex">
												<%if(obj[22]!=null && Integer.parseInt(obj[22].toString())==0){
													String activityType = "";
													if(Integer.parseInt(obj[5].toString())<100) { 
														if(!obj[10].toString().equalsIgnoreCase("0")){ 
															if(Integer.parseInt(obj[6].toString())>0){
															
																int activityLevelId = obj[23]!=null?Integer.parseInt(obj[23].toString()):0;
																if(activityLevelId==1) {
																	activityType = "A";
																}else if(activityLevelId==2) {
																	activityType = "B";
																}else if(activityLevelId==3) {
																	activityType = "C";
																}else if(activityLevelId==4) {
																	activityType = "D";
																}else if(activityLevelId==5) {
																	activityType = "E";
																}
																%>
																 <%if(obj[24]!=null &&  !obj[24].toString().equalsIgnoreCase("0")  && obj[25].toString().equalsIgnoreCase("L")) { %>
                                                         		<span class="hover text-primary spanPromary"  onmouseover="showContent('<%=obj[24].toString() %>',this)">Linked Milestone </span>
		                                                         			<div class="customTooltip" id="customTooltip<%=obj[24].toString() %>">
		  																		<span class="closeModal" onclick="closeModal('<%=obj[24].toString() %>')">&times;</span>
																			</div>
                                                         	
                                                         		<%}else{ %> 
                                                         		<form class="form-inline"  method="POST" action="M-A-Update.htm">
                        	                                  		<button class="btn edit"  data-toggle="tooltip" data-placement="top"  title="UPDATE"> <i class="fa fa-wrench"  aria-hidden="true"></i> </button>
                                                                 	<input type="hidden" name="MilestoneActivityId"	value="<%=levelsMapData[0] %>" /> 
                                                                  	<input type="hidden" name="ActivityId" value="<%=obj[0] %>" /> 
                                                                  	<input type="hidden" name="startdate" value="<%=obj[2].toString() %>" >
                                                                  	<input type="hidden" name="ProjectId" value="<%=ProjectId %>" /> 
                                                                  	<input type="hidden" name="ActivityType" value="<%=activityType %>" /> 
                                                                    <input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
                                                        		</form>
                                                        		  <button class="btn btn-primary" type="button" onclick="showModal('<%=obj[0].toString()%>','<%=levelsMapData[2] %>','<%=levelsMapData[1] %>','<%=obj[4].toString() %>')" data-toggle="tooltip" data-placement="top"  title="FeedBack"><i class="fa fa-comments text-light" aria-hidden="true"></i></button>
                                                        		<%} %> 
                                                        		
                                                        		
                                           					<%}else{ %>
                                                            	Weightage Not Set 
                                                                   
                                                        <%}}else{ %>
                                                        	Base Line Not Set
                                                 	<%} }else{ %> 
                                                 		
                                                 	<%if(LoginType.equalsIgnoreCase("A")||empId.equals(projectDirector)){ %>
                                                 		<form class="form-inline"  method="POST" action="M-A-Update.htm">
                        	                                  		<button class="btn edit"  data-toggle="tooltip" data-placement="top"  title="REOPEN">	<i class="fa fa-window-restore" aria-hidden="true"></i></button>
                                                                 	<input type="hidden" name="MilestoneActivityId"	value="<%=levelsMapData[0] %>" /> 
                                                                  	<input type="hidden" name="ActivityId" value="<%=obj[0] %>" /> 
                                                                  	<input type="hidden" name="startdate" value="<%=obj[2].toString() %>" >
                                                                  	<input type="hidden" name="ProjectId" value="<%=ProjectId %>" /> 
                                                                  	<input type="hidden" name="ActivityType" value="<%=activityType %>" /> 
                                                                    <input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
                                                        		</form>
                                                 
                                                 	<%}else{ %>
                                                    	Completed
                                                    	<%} %> 
                                                	<%} %>
                                            	<%} %>
                                        	</td>	
										</tr>
									<%} } %>
										
									<%if(slno==0) {%>
										<tr >
											<td colspan="11"  class="center text-center">No List Found</td>
										</tr>
									<%} %>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>	
	
		<div class="row m-1">
		<div class="col-md-12 milestoneFlow">
			<b>Milestone Flow </b>
		</div>
	</div>
	
	<div class="row m-1 row1"
		>

		<table align="center"  class="borderSpacing">
		<tr>
				<td class="trup trupHead" >
				<b class="text-primary">Add Milestone Activity</b>
				</td>
				<td class="trup trupBody"></td>
				<td ><i class="fa fa-long-arrow-right "aria-hidden="true"></i></td>
				<td rowspan="2" class="trup trupFooter" "></td>
				
				<td class="trup trupHead">
					<b class="text-primary">Add Sub Milestone Activity </b>
				</td>
				<td class="trup trupBody"></td>
				<td ><i class="fa fa-long-arrow-right "aria-hidden="true"></i></td>
				<td rowspan="2" class="trup trupFooter" ></td>
				
				<td class="trup trupHead">
					<b class="text-primary">Assign Weightage1 </b>
				</td>
				<td class="trup trupBody"></td>
				<td ><i class="fa fa-long-arrow-right "aria-hidden="true"></i></td>
				<td rowspan="2" class="trup trupFooter"></td>
				
				<td class="trup trupHead">
					<b class="text-primary">Assign Milestone Activity </b>
				</td>
				<td class="trup trupBody"></td>
				<td ><i class="fa fa-long-arrow-right "aria-hidden="true"></i></td>
				<td rowspan="2" class="trup trupFooter" ></td>
				
				<td class="trup trupHead">
					<b class="text-primary">Set Baseline </b>
				</td>
				<td class="trup trupBody"></td>
				<td ><i class="fa fa-long-arrow-right "aria-hidden="true"></i></td>
				<td rowspan="2" class="trup trupFooter" ></td>
				
				<td class="trup trupHead">
					<b class="text-primary">Assignee</b>
				</td>
				<td class="trup trupBody"></td>
				<td ><i class="fa fa-long-arrow-right "aria-hidden="true"></i></td>
				<td rowspan="2" class="trup trupFooter" ></td>
				
				<td class="trup trupHead">
					<b class="text-primary"> Acknowledge Milestone Activity </b>
				</td>
				<td class="trup trupBody"></td>
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
		
	
	
	<div class="modal fade my-modal" id="exampleModalCenter" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered" role="document">
 	    	<div class="modal-content">
 	      		<div class="modal-header">
 	        		<h5 class="modal-title" id="exampleModalLongTitle"></h5>
		  	        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
		  	          <span aria-hidden="true">&times;</span>
		  	        </button>
  	     	 	</div>
  	      		<div class="modal-body"> 
  	      
  	      			<form action="MileRemarkUpdate.htm" method="POST" autocomplete="off">
  	      				<div class="row">
  	      		    
							
					 		<br>
							<div class="col-md-12"> Remarks :<br>
  	      		    			<textarea class="form-control" name="Remarks" id="remarks" required="required" maxlength="2000"> </textarea>
  	      		    		</div>
  	      				</div>
  	      				<br>
		  	      		<div align="center">
		  	      			<input type="submit" class="btn btn-primary btn-sm submit " id="sub" value="Submit" name="sub"  onclick="return confirm('Are You Sure To Submit ?')" > 
		  	      		</div>
		  	      		<br>

		                <input type="hidden" name="projectid" value="<%=ProjectId%>"/>
		  	      		<input type="hidden" name="MileId" value="" id="milId"/>
		  	      		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
		  	      	</form>
  	      		</div>
	  	      	<div class="modal-footer">
	 	      	</div>
  	    	</div>
		</div>
	</div>
			
			
		<!-- Chat Modal  -->	
				
		<div class="modal fade" id="chatModal" tabindex="-1" role="dialog"
		aria-labelledby="exampleModalLabel" aria-hidden="true">
		<div class="modal-dialog" role="document">
			<div class="modal-content modalContentWidth" >
				<div class="modal-header">
					<h5 class="modal-title" id="exampleModalLabel">Feedbacks</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<div class="chat-container d-flex flex-column">

						<!-- Scrollable chat body -->
						<div class="chat-body flex-grow-1 overflow-auto px-3 py-2" id="messageEditor">

						</div>

						<!-- Fixed input section -->
						<div class="chat-input border-top p-3 bg-white">

							<div class="form-group mb-2">
								<label for="Remarks"><strong>Comment:</strong></label>
								<textarea rows="2" class="form-control" id="RemarksEdit"
									name="Remarks" placeholder="Enter Comments" required></textarea>
							</div>
							<div class="text-center">
								<input type="button" class="btn btn-primary btn-sm submit"
									value="Submit" name="sub" onclick="submitRemarks()"> 
									
								<!-- <input type="button" class="btn btn-danger btn-sm delete"
									value="Close" name="sub"
									onclick="return confirm('Are You Sure To Close?')"> -->
							</div>


						</div>

					</div>
				</div>

			</div>
		</div>
	</div>		
				
</body>

<script>


var activityId="0";
function showModal(a,b,c,d){
	console.log(a)
	
	var feedBack = "Feedback for "+d+ " ("+ b+"-"+c+")"
	 $.ajax({
	
		type:'GET',
		url:'getMilestoneDraftRemarks.htm',
		datatype:'json',
		data:{
			activityId:a,
		},
		success : function (result){
			var ajaxresult = JSON.parse(result);
			
			var empid = '<%=empId %>'
			
			var html = "";
		
			for (var i=0;i<ajaxresult.length;i++){
				
				var sender = ajaxresult[i][4];
				if(sender==empid){
					html=html +'<div class="chat-message user-msg">'
				}else{
					html=html +'<div class="chat-message admin-msg">'
				}
				
				var senderName = ajaxresult[i][0]+", "+ajaxresult[i][1];
				var message = ajaxresult[i][2];
				var arr= ajaxresult[i][3].split(" ");
				var msgdate = arr[0].split("-")[2]+"-"+arr[0].split("-")[1]+"-"+arr[0].split("-")[0];
				
				var msgtime = arr[1].substring(0,5);
				
				html = html +
						'<strong class="sender-name">'+senderName +'</strong>'+message 
						+'<div class="timestamp">'+msgdate+' '+ msgtime+'</div></div>'
			}
			$('#messageEditor').html(html);
		}
		
		
	})
	
	$('#exampleModalLabel').html(feedBack)
	activityId=a;
	
$('#chatModal').modal('show');
}


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
	
	
	
	function getCommentBox(id,mile,remarks,financeoutlay){

		$('#exampleModalCenter').modal('toggle');
		$("#milId").val(id);
		$("#financialoutlay").val("");
		$("#remarks").val("");
		if(remarks!="null" && financeoutlay!="null"){
			$("#financialoutlay").val(financeoutlay);
			$("#remarks").val(remarks);
		}
		$("#exampleModalLongTitle").html('Milestone '+mile);
	}
	
	
	
	setPatternFilter($("#financialoutlay"), /^-?\d*$/);
	function setPatternFilter(obj, pattern) {
		  setInputFilter(obj, function(value) { return pattern.test(value); });
		}

	function setInputFilter(obj, inputFilter) {
		  obj.on("input keydown keyup mousedown mouseup select contextmenu drop", function() {
		    if (inputFilter(this.value)) {
		      this.oldValue = this.value;
		      this.oldSelectionStart = this.selectionStart;
		      this.oldSelectionEnd = this.selectionEnd;
		    } else if (this.hasOwnProperty("oldValue")) {
		      this.value = this.oldValue;
		      this.setSelectionRange(this.oldSelectionStart, this.oldSelectionEnd);
		    }
		  });
		}
	
	$(document).ready(function() {

			$('[data-toggle="tooltip"]').tooltip()
		
        $('#myTable').DataTable({
            "lengthMenu": [ 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 100],
            "pagingType": "simple",
            "pageLength": 15
        });
    });
	
	
	function showContent(a,ele){
		$.ajax({
			type:'GET',
			url:'MilestoneLinked.htm',
			datatype:'json',
			data:{
				id:a,
			},
			success:function(result){
				
				var ajaxresult = JSON.parse(result);
			
				var val="";
					for (var key in ajaxresult) {
					  if (ajaxresult.hasOwnProperty(key)) {
						   
					    if(key===a){val = ajaxresult[key];}
					
					  }
					}
					
					
					var arr = val.split("/");
			
					// Tooltip handlers
					   ele.addEventListener("mousemove", function (e) {
					        var tooltip = document.getElementById("customTooltip"+a);
					        tooltip.style.display = "block";
					      /* tooltip.style.left = (e.pageX + 15) + "px";
					        tooltip.style.top = (e.pageY + 10) + "px"; */
					        tooltip.innerHTML =
					        	"<span class='font-weight-bold'>Project Code </span>: "+arr[2]+"<br> <span  class='font-weight-bold'>Milestone Name: <br></span>"
					        	+arr[1] + "<br> ( " + arr[0] +" )<br>"
					        	+ "<span class='text-danger font-weight-bold '>This milestone will progress based on the linked milestone !</span>"
					   }); 
					
					   ele.addEventListener("mouseleave", function () {
					        var tooltip = document.getElementById("customTooltip"+a);
					        tooltip.style.display = "none";
					    }); 
					; 
			}
		})
	}
	
	function submitRemarks(){
		console.log(activityId)
		
		
		var remarks = $('#RemarksEdit').val().trim();
		console.log(remarks)
		
		if(remarks.length==0){
			Swal.fire({
				  icon: "error",
				  title: "Oops...",
				  text: "Remarks can not be empty!",
				
				});
			
			event.preventDefault();
			return false;
		}
		console.log(remarks +"remarks")
		
		Swal.fire({
	title: 'Are you sure?',
	text: "Do you want to submit the remarks?",
	icon: 'warning',
	showCancelButton: true,
	confirmButtonText: 'Yes',
	cancelButtonText: 'No'
	}).then((result) => {
	if (result.isConfirmed) {
	     $.ajax({
	        type: 'GET',
	        url: 'submitMilestoneFeedBack.htm',
	        dataType: 'json',  
	        data: {
	        	activityId: activityId,
	            remarks: remarks
	        },
	        success: function (result) {
	            var ajaxresult = JSON.parse(result);
	            if (Number(ajaxresult) > 0) {
	                $('#chatModal').modal('hide'); // hide modal first

	                Swal.fire({
	                    icon: "success",
	                    title: "SUCCESS",
	                    text: "Feedback Given",
	                    allowOutsideClick: false
	                }).then(() => {
	                    // After Swal is closed, reopen the modal
	                    $('#RemarksEdit').val('');
	                    showModal(scheduleId);
	                });
	            }
	        },
	        error: function () {
	            // Optional: show error message
	            Swal.fire('Error', 'There was an issue submitting your remarks.', 'error');
	        }
	    }); 
	}
	})
		
		
	}
	</script>  

</html>