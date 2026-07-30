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
<spring:url value="/resources/css/action/forwardSub.css" var="forwardSub" />
<link href="${forwardSub}" rel="stylesheet" />
<spring:url value="/resources/css/action/actionCommon.css" var="actionCommon" />
<link href="${actionCommon}" rel="stylesheet" />
 

<title>Action Assignee</title>


</head>
 
<body>
  <%

  
  SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
  SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
  Object[] Assignee=  (Object[]) request.getAttribute("Assignee");
  List<Object[]> SubList=(List<Object[]> ) request.getAttribute("SubList");
  String AssigneeName=(String) request.getAttribute("AssigneeName");
  String Assigner=(String) request.getAttribute("Assigner");
  List<Object[]> LinkList=(List<Object[]> ) request.getAttribute("LinkList");
  String actionno= (String) request.getAttribute("actionno");
  String flag = (String) request.getAttribute("flag");
  String isReview = (String) request.getAttribute("isReview");
  if(isReview == null ){
	  isReview = "N";
  }
  Object[] AttachmentList=(Object[]) request.getAttribute("AttachmentList");
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

	<div class="container container-margin">

		
    		<div class="card">
    	
    	
    	
	    		<div class="card-header card-header-bg">
	    		<%if(Assignee!=null && Assignee[21]!=null && "I".equalsIgnoreCase( Assignee[21].toString())){%>
      				<h6 class="custom-h6" align="left"> Issue  Id : <%=Assignee[9]!=null?StringEscapeUtils.escapeHtml4(Assignee[9].toString()): " - " %>
				<%}else{%>
					<h6 class="custom-h6" align="left"> Action  Id : <%=Assignee[9]!=null?StringEscapeUtils.escapeHtml4(Assignee[9].toString()): " - " %>
				<%}%>	
					<span class="custom-span">Assignee : <%=Assignee[11]!=null?StringEscapeUtils.escapeHtml4(Assignee[11].toString()): " - " %> &nbsp;(<%=Assignee[17]!=null?StringEscapeUtils.escapeHtml4(Assignee[17].toString()): " - " %>)</span>
      				 </h6>
      				
      				 
      				
      			</div>
      		
      			<div class="card-header-bg">
      			<%if(AttachmentList!=null){ %>
      				<form>
      				 <h6 class="custom-h6-copy" align="left"> Attachment: 
      				&nbsp;&nbsp;<button formaction="ActionMainAttachDownload.htm" formmethod="get" class="btn" type="submit" name="MainId" value="<%=Assignee[0]!=null?StringEscapeUtils.escapeHtml4(Assignee[0].toString()): "" %>"><i class="fa fa-download"></i></button>
      				</h6>
      				</form>
      			   <%} %>
      			
      			</div>
	      		<div class="card-body">
	      			
	        		<div class="row">
	      					<div class="col-md-12">
	      						<table class="w-100">
	      							<tr>
	      								<td class="width-15">
		      								<%if(Assignee!=null && Assignee[21]!=null && "I".equalsIgnoreCase( Assignee[21].toString())){%>
		      									<label class="table-label"> Issue Item  :</label>
		      									<%}else{%>
		      									<label class="table-label"> Action Item  :</label>
		      								<%}%>
	      								</td>
	      								<td ><span>
	      									 <%=Assignee[5]!=null?StringEscapeUtils.escapeHtml4(Assignee[5].toString()): " - " %></span>
	      								</td>							
	      							</tr>
	      						</table>
	      						<table>
	      							<tr>
	      								<td>
	      									<label class="table-label"> Assignee  :</label>
	      								</td>	      	
	      								<td><span>
	      									<%=Assignee[11]!=null?StringEscapeUtils.escapeHtml4(Assignee[11].toString()): " - " %>&nbsp;(<%=Assignee[17]!=null?StringEscapeUtils.escapeHtml4(Assignee[17].toString()): " - " %>)</span>
	      								</td>
	      								<td class="table-padding"" >
	      									<label class="table-label"> Assigner :</label>
	      								</td>	      	
	      								<td>&nbsp;&nbsp;&nbsp;&nbsp;
	      									<%=Assignee[1]!=null?StringEscapeUtils.escapeHtml4(Assignee[1].toString()): " - "%> &nbsp;(<%=Assignee[16]!=null?StringEscapeUtils.escapeHtml4(Assignee[16].toString()): " - " %>)
	      								</td>	
	      								<td class="table-padding" >
	      									<label class="table-label"> PDC (Current) :</label>
	      								</td>	      	
	      								<td>&nbsp;&nbsp;&nbsp;&nbsp;
	      									<%=Assignee[4]!=null?sdf.format(Assignee[4]):" - "%>
	      								</td>	
	      							</tr>
	      							<tr>
	      								<td>
	      									<label class="table-label">  Original PDC :</label>
	      								</td>	      	
	      								<td>&nbsp;&nbsp;&nbsp;&nbsp;
	      									<%=Assignee[13]!=null?sdf.format(Assignee[13]):" - "%>
	      								</td>	
	      								     
	      							<% int revision=Integer.parseInt(Assignee[10].toString());
	      							if(revision>2){
	      								revision=2;
	      							}
	      							for(int i=1;i<=revision;i++){ %>
	      								<td class="table-padding" >
	      									<label class="table-label"> Revised - <%=i%> PDC:</label>
	      								</td>	      	
	      								<td>&nbsp;&nbsp;&nbsp;&nbsp;
	      									<%=Assignee[14+i-1]!=null?sdf.format(Assignee[14+i-1]):" - "%>
	      								</td>	
	      							
	      							<%} %>
	      							</tr>	
	      						</table>      						      					
	      					</div>      				
	      				</div>
	      				<hr>
	      				<br>
						
	          		<div align="center">
	          			<form method="post" action="SendBackSubmit.htm" enctype="multipart/form-data">
	          			
							<input type="hidden" name="${_csrf.parameterName}"   value="${_csrf.token}" />
							
							<div class="row">
								
								<div class="col-md-1" align="right">
								
								<label>Remarks:</label></div>	
									
								<div class="col-md-7">
									
									<textarea rows="2"  class="form-control text-area"  id="Remarks" name="Remarks"  placeholder="Enter Remarks..!!"  ></textarea>
									
								</div>
								
								<div class="col-md-4">
									<button type="submit" class="btn btn-warning btn-sm edit"  onclick="return back()" >Send Back </button>
									<%if(Assignee[19]!=null && Long.parseLong(Assignee[19].toString())>1){%>
										<button type="button" class="btn btn-danger btn-sm revoke" name="sub" value="C"  onclick="CloseAction('<%=Assignee[18] %>')" > Close Action</button>
					        		<%}else{%>
						        			<button type="submit" class="btn btn-danger btn-sm revoke"   onclick="return  close5('<%=Assignee[24] %>')" formaction="CloseSubmit.htm"> Close Action</button>
					        		<%}%>
					        		<%if(Assignee!=null && Assignee[21]!=null && "I".equalsIgnoreCase( Assignee[21].toString())){%>
					        			<input type="submit" class="btn btn-primary btn-sm back" value="Back" onclick="close2()" formaction="ActionIssue.htm"/>
					        			<input type="hidden" name="Action"  value="F">
					        		<%}else if(flag!=null && flag.equalsIgnoreCase("R")){%>
					        			<input type="submit" class="btn btn-primary btn-sm back" value="Back" formaction="ActionReport.htm"/>
					        		<%}else {%>
					        			<input type="submit" class="btn btn-primary btn-sm back" value="Back" onclick="close2()" formaction="ActionForwardList.htm"/>
					        		<%}%>
					        		<input type="hidden" name="ActionMainId" value="<%=Assignee[0]%>" />	
					        		<input type="hidden" name="ActionAssignId" value="<%=Assignee[18]%>" />
					        		<input type="hidden" name="LevelCount" value="<%=Assignee[19] %>" />
					        		<input type="hidden" name="isReview" value="<%=isReview%>" />
					        		<input type="hidden" name="BACK" value="Issue" />
					        		
								</div>
								
							</div>
							                                 
						</form>
		    		<br>
		    		<hr><br>
		    		<form method="post"  action="ExtendPdc.htm" >
		          			<div class="row" align="left"> 							
								<div class="col-sm-6" >
									<table>
										<tr>
											<td class="width-10">
				                            	<label>Extend PDC <span class="mandatory">* </span>:</label>
				                            </td>
				                            <td class="width-20">
				                            	<input class="form-control td-input " name="ExtendPdc" id="DateCompletion" required="required"  value="<%=sdf.format(Assignee[4])%>">
				                           	</td>
				                           	<td class="width-20" >
												<button type="submit" class="btn btn-danger btn-sm submit td-btn"  onclick="return confirm('Are You Sure To Submit ?')" > UPDATE</button>
												<input type="hidden" name="ActionMainId" value="<%=Assignee[0] %>" />	
												<input type="hidden" name="ActionAssignId" value="<%=Assignee[18] %>" />
												<input type="hidden" name="froward" value="Y" />
												<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" />
											</td>
										</tr> 
									</table>
								</div>
							</div>
		      			</form>
		      			<br>
		      			<hr>
	  
						
	          		</div>		
			    
	      		
	    		</div>
    		</div>
   	</div>   
   </div>  
 <div class="row div-row" >
 
	<div class="col-md-12">
    	
    	<div class="card" >
      		
      		<div class="card-body" >
      		
      		
      		 <div class="row">
      		
<div class="col-md-1"></div>
   
   <div class="col-md-10 pl-0" >
    <% if(LinkList!=null && LinkList.size()>0){ %>  
   				<div class="table-responsive">
    				<table class="table table-bordered table-hover table-striped table-condensed table-margin" id="myTable3">
						<thead>
							<tr>
								<th colspan="7" class="custom-th" >Old Action  Details </th>									
							</tr>	
							<tr>					
								<th class="text-left">As On Date</th>
								<th >Progress %</th>
								<th >Remarks</th>								
							 	<th >Attachment</th>
								<th >Action</th>
							</tr>
						</thead>
						<tbody>					
						<%
						for(Object[] obj: LinkList){ %>
														
							<tr>
								<td width="12%">
									<%=obj[3]!=null?sdf.format(obj[3]):" - "%>
								</td>
								
								<td width="6%">
									
									<div class="progress div-progress">
  										<div class="progress-bar progress-bar-striped width-<%=obj[2]%>" role="progressbar" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100"><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - "%></div>
									</div>
				
										</td>
								
								<td class="text-left width-10"> 
									<%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - "%>
								</td>
								
								<td class="text-left width-3">
								
							 <% 
						        if(obj[5].toString().length()!=0 && obj[5]!=null){
						        %>
						        <div  align="center">
										<a  
										 href="ActionAttachDownload.htm?ActionSubId=<%=obj[7]%>" 
										 target="_blank"><i class="fa fa-download"></i></a>
									</div>
								
									
								<%}else{ %>
								
								<div  align="center">-</div>
								 <%} %>
									
						
								</td>
								
														
								<td class="text-left width-6">
							     Old Action
								</td>
						
						</tr>
														
							<%  } %>	
									</tbody>
					</table>
				</div> 
				<%} %>
   						<div class="table-responsive">
    				<table class="table table-bordered table-hover table-striped table-condensed table-margin" id="myTable3" >
						<thead>
						<tr>
							<%if(Assignee!=null && Assignee[21]!=null && "I".equalsIgnoreCase( Assignee[21].toString())){%>
								<th colspan="4" class="custom-th" >Issue Updated Details </th>									
							<%}else{%>
								<th colspan="4" class="custom-th" >Action Updated Details </th>
							<%}%>
							</tr>	
							<tr>					
								<th class="text-left">As On Date</th>
								<th >Progress %</th>
								<th >Remarks</th>								
							 	<th >Attachment</th>
							
							</tr>
						</thead>
						<tbody>					
											
					 	<%int  count=1;
						for(Object[] obj: SubList){ %>
														
						<tr >
						
							 
							
		
								<td width="12%">
									<%=obj[3]!=null?sdf.format(obj[3]):" - "%>
								</td>
								
								<td width="6%">
								
									<div class="progress div-progress">
  										<div class="progress-bar progress-bar-striped width-<%=obj[2]%>" role="progressbar" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100"><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - "%></div>
									</div>
				
										</td>
								
								<td class="text-left width-10"> 
									<%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - "%>
								</td>
								
								<td class="text-left; width-3">
								
							 <% 
						        if(obj[5]!=null && obj[5].toString().length()!=0  ){
						        %>
						        <div  align="center">
										<a  
										 href="ActionAttachDownload.htm?ActionSubId=<%=obj[5]%>" 
										 target="_blank"><i class="fa fa-download"></i></a>
									</div>
								
									
								<%}else{%>
								
								<div  align="center">-</div>
								
								 <%}%>
						</tr>						
							<% count++; } %>
						</tbody>
					</table>
				</div> 
	
				</div>
			 	
  			</div>

</div>

			</div>
		</div>
	</div>
<%if(Assignee[19]!=null && Long.parseLong(Assignee[19].toString())>1){
	List<Object[]> actionslist = (List<Object[]>)request.getAttribute("actionslist");

%>	

<div class="modal fade  bd-example-modal-lg" tabindex="-1" role="dialog" id="ActionAssignfilemodal">
				<div class="modal-dialog div-document"  role="document">
					<div class="modal-content">
						<div class="modal-header">
							<h4 class="modal-title" > Action Tree </h4>
							<div  class="margin-left390">
										<div class="font-weight-bold">
											<span class="div-span">0 - 25 % :&ensp; 	<span class="Q1" class="inspan"></span></span>
											<span class="">26 - 50 % :&ensp; 	<span class="Q2" class="inspan"></span></span>
											<span class="div-sapn15">51 - 75 % :&ensp; 	<span class="Q3" class="inspan"></span></span>
											<span class="div-sapn15">76 - 100 % :&ensp; <span class="Q4"  class="inspan"></span></span>
											<span class="div-sapn15">Closed:&ensp; <span class="Q5"  class="inspan"></span></span>
										</div>
								</div>
							<button type="button" class="close" data-dismiss="modal" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<%
						int startLevel = 0;
						
						if(actionslist.size()>0){
							startLevel = Integer.parseInt(actionslist.get(0)[3].toString());
						} 
						%>
<form name="specadd" id="specadd" action="##" method="Get">
<div class="modal-body div-modalbody">
	<div align="center">
	    <div class="genealogy-tree" >
	    <ul>
				<li>      
						<%for(Object[] action : actionslist){ %>
							<% if(Integer.parseInt(action[3].toString()) == startLevel){ %>
								
			                    <div class="member-view-box action-view-box">
			                        <div class=" action-box" >
			                          	<div 			                          		
			                          		<% int progress = action[25]!=null ? Integer.parseInt(action[25].toString()) : 0 ; %>
			                          		<% if( action[20]!=null && "C".equalsIgnoreCase(action[20]+"") ){ %>
			                          			class="action-box-header Q5"
			                          		<%}else if( progress >= 0 && progress <= 25  ){ %>
			                          			class="action-box-header Q1"
			                          		<%}else if( progress >= 26 && progress <= 50  ){ %>
			                          			class=" action-box-header Q2"
			                          		<%}else if( progress >= 51 && progress <= 75  ){ %>
			                          			class=" action-box-header Q3"
			                          		<%}else if(  progress >= 76 && progress <= 100  ){ %>
			                          			class="action-box-header Q4"
			                          		<%} %>
			                          		
			                          	  >
			                          		
			                          		<span class="span-font" 
			                          			onclick="ActionDetails(	'<%=action[10] %>',   <!-- assignid -->
			                          									'<%=action[5].toString().trim() %>',   <!-- action item -->
			                          									'<%=action[11] %>',   <!-- action No -->
			                          									'<%if(action[25]!=null){ %> <%=action[25] %>% <%}else{ %>0<%} %>', <!-- progress -->
			                          									'<%=sdf.format(action[4]) %>', <!-- action date -->
			                          									'<%=sdf.format(action[24]) %>', <!-- enddate -->
			                          									'<%=sdf.format(action[12]) %>', <!-- orgpdc -->
			                          									'<%=action[22].toString().trim()%>', <!-- assignor -->
			                          									'<%=action[23].toString().trim()%>', <!-- assignee -->
			                          									'<%=action[6]%>' <!-- action type -->
			                          									);" >
			                          				<%=action[11]!=null?StringEscapeUtils.escapeHtml4(action[11].toString()): " - " %>
			                          		</span >           
			                          		
			                          	</div>
			                          	<div class="action-box-body cursor-pointer" align="center">
			                          		<table class="card-body-table">
			                          			<tr>
			                          				<th class="width-40">Assignee :</th>
			                          				<td  >&emsp;<%=action[23]!=null?StringEscapeUtils.escapeHtml4(action[23].toString()): " - " %></td>
			                          			</tr>
			                          			<tr>
			                          				<th >PDC :</th>
			                          				<td >&emsp;<%=action[24]!=null?sdf.format(action[24]):" - " %></td>
			                          			</tr>
			                          			<tr>
			                          				<th >Progress (%) :</th>
			                          				<td class="td-padding10">
			                          					
			                          					<%if(action[25]!=null){ %>
					                          				<div class="progress div-progress-copy" >
																<div class="progress-bar progress-bar-striped width-<%=action[25]%>" role="progressbar" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
																	<%=action[25]!=null?StringEscapeUtils.escapeHtml4(action[25].toString()): " - "%>
																</div> 
															</div> 
														<%}else{ %>
															<div class="progress div-progress-copy" >
																<div class="progress-bar progressbar" role="progressbar"  >
																	Not Yet Started
																</div>
															</div> 
														<%} %>
			                          					
			                          				</td>
			                          			</tr>
			                          		</table>  
			                          	</div>
			                        </div>
			                    </div>
			               
			        <!-- --------------------------------------------------------   LEVEL 1 ---------------------------------------------------- -->
			                <ul class="active">	                
			                <%for(Object[] action_L1 : actionslist){ %>
								<% if(Integer.parseInt(action_L1[3].toString()) == startLevel+1 && Long.parseLong(action[0].toString()) == Long.parseLong(action_L1[1].toString())  
								&&  action[18].toString().trim().equalsIgnoreCase(action_L1[16].toString().trim()) 
								&& Long.parseLong(action[19].toString()) == Long.parseLong(action_L1[17].toString()) ){ %>
									<li>			    
									           
											<div class="member-view-box action-view-box">
												<div class=" action-box" >
													<div 			                          		
						                          		<% int progress_L1 = action_L1[25]!=null ? Integer.parseInt(action_L1[25].toString()) : 0 ; %>
						                          		<% if(action_L1[20]!=null && "C".equalsIgnoreCase(action_L1[20]+"") ){ %>
			                          						class="action-box-header Q5"
			                          					<%}else if( progress_L1 >= 0 && progress_L1 <= 25  ){ %>
						                          			class="action-box-header Q1"
						                          		<%}else if( progress_L1 >= 26 && progress_L1 <= 50  ){ %>
						                          			class=" action-box-header Q2"
						                          		<%}else if( progress_L1 >= 51 && progress_L1 <= 75  ){ %>
						                          			class=" action-box-header Q3"
						                          		<%}else if(  progress_L1 >= 76 && progress_L1 <= 100  ){ %>
						                          			class="action-box-header Q4"
						                          		<%} %>
						                          		
						                          	  >
													    
													    <span class="span-font" 
							                          			onclick="ActionDetails('<%=action_L1[10] %>',   <!-- assignid -->
							                          									'<%=action_L1[5].toString().trim() %>',   <!-- action item -->
							                          									'<%=action_L1[11] %>',   <!-- action No -->
							                          									'<%if(action_L1[25]!=null){ %> <%=action_L1[25] %>% <%}else{ %>0<%} %>', <!-- progress -->
							                          									'<%=sdf.format(action_L1[4]) %>', <!-- action date -->
							                          									'<%=sdf.format(action_L1[24]) %>', <!-- enddate -->
							                          									'<%=sdf.format(action_L1[12]) %>', <!-- orgpdc -->
							                          									'<%=action_L1[22].toString().trim()%>', <!-- assignor -->
							                          									'<%=action_L1[23].toString().trim()%>', <!-- assignee -->
							                          									'<%=action_L1[6]%>' <!-- action type -->
							                          									);" >                      		
														
														<%=action_L1[11]!=null?StringEscapeUtils.escapeHtml4(action_L1[11].toString()): " - " %></span >    
														       
													</div>
													<div class="action-box-body cursor-pointer" align="center">
														<table class="card-body-table">
															<tr>
						                          				<th class="width-40">Assignee :</th>
						                          				<td  >&emsp;<%=action_L1[23]!=null?StringEscapeUtils.escapeHtml4(action_L1[23].toString()): " - " %></td>
						                          			</tr>
						                          			<tr>
						                          				<th >PDC :</th>
						                          				<td >&emsp;<%=action_L1[23]!=null?sdf.format(action_L1[24]):" - " %></td>
						                          			</tr>
						                          			<tr>
						                          				<th >Progress (%) :</th>
						                          				<td class="td-padding10">
						                          					
						                          					<%if(action_L1[25]!=null){ %>
								                          				<div class="progress div-progress-copy">
																			<div class="progress-bar progress-bar-striped width-<%=action_L1[25]%>" role="progressbar" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
																				<%=action_L1[25]!=null?StringEscapeUtils.escapeHtml4(action_L1[25].toString()): " - "%>
																			</div> 
																		</div> 
																	<%}else{ %>
																		<div class="progress div-progress-copy" >
																			<div class="progress-bar progressbar" role="progressbar" >
																				Not Yet Started
																			</div>
																		</div> 
																	<%} %>
						                          					
						                          				</td>
						                          			</tr>
														</table>
													</div>
												</div>
											</div>
	
									    
								<!-----------------------------------------------------------   LEVEL 2 ------------------------------------------------------->
						                <ul class="">	
						                
							                <%for(Object[] action_L2 : actionslist){ %>
							                
												<% if(Integer.parseInt(action_L2[3].toString()) == startLevel+2 && Long.parseLong(action_L1[0].toString())== Long.parseLong(action_L2[1].toString())  
												&& action_L1[18].toString().trim().equalsIgnoreCase(action_L2[16].toString().trim()) 
												&& Long.parseLong(action_L1[19].toString()) == Long.parseLong(action_L2[17].toString()) ){ %>
												<li>			    
												           
													<div class="member-view-box action-view-box">
															<div class=" action-box" >
																<div 			                          		
									                          		<% int progress_L2 = action_L2[25]!=null ? Integer.parseInt(action_L2[25].toString()) : 0 ; %>
									                          		<% if(action_L2[20]!=null && "C".equalsIgnoreCase(action_L2[20]+"") ){ %>
			                          									class="action-box-header Q5"
			                          								<%}else if( progress_L2 >= 0 && progress_L2 <= 25  ){ %>
									                          			class="action-box-header Q1"
									                          		<%}else if( progress_L2 >= 26 && progress_L2 <= 50  ){ %>
									                          			class=" action-box-header Q2"
									                          		<%}else if( progress_L2 >= 51 && progress_L2 <= 75  ){ %>
									                          			class=" action-box-header Q3"
									                          		<%}else if(  progress_L2 >= 76 && progress_L2 <= 100  ){ %>
									                          			class="action-box-header Q4"
									                          		<%} %>
									                          		
									                          	  >
																	
																	<span class="span-font" 
									                          			onclick="ActionDetails('<%=action_L2[10] %>',   <!-- assignid -->
									                          									'<%=action_L2[5].toString().trim() %>',   <!-- action item -->
									                          									'<%=action_L2[11] %>',   <!-- action No -->
									                          									'<%if(action_L2[25]!=null){ %> <%=action_L2[25] %>% <%}else{ %>0<%} %>', <!-- progress -->
									                          									'<%=sdf.format(action_L2[4]) %>', <!-- action date -->
									                          									'<%=sdf.format(action_L2[24]) %>', <!-- enddate -->
									                          									'<%=sdf.format(action_L2[12]) %>', <!-- orgpdc -->
									                          									'<%=action_L2[22].toString().trim()%>', <!-- assignor -->
									                          									'<%=action_L2[23].toString().trim()%>', <!-- assignee -->
							                          											'<%=action_L2[6]%>' <!-- action type -->
									                          									);" >              
																	
																	
																	<%=action_L2[11]!=null?StringEscapeUtils.escapeHtml4(action_L2[11].toString()): " - " %></span >           
																</div>
																<div class="action-box-body cursor-pointer" align="center">
																	<table class="card-body-table">
																		<tr>
									                          				<th class="width-40">Assignee :</th>
									                          				<td  >&emsp;<%=action_L2[23]!=null?StringEscapeUtils.escapeHtml4(action_L2[23].toString()): " - " %></td>
									                          			</tr>
									                          			<tr>
									                          				<th >PDC :</th>
									                          				<td >&emsp;<%=action_L2[24]!=null?sdf.format(action_L2[24]):" - " %></td>
									                          			</tr>
									                          			<tr>
									                          				<th >Progress (%) :</th>
									                          				<td class="td-padding10">
									                          					
									                          					<%if(action_L2[25]!=null){ %>
											                          				<div class="progress div-progress-copy" >
																						<div class="progress-bar progress-bar-striped width-<%=action_L2[25]%>" role="progressbar" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
																							<%=action_L2[25]!=null?StringEscapeUtils.escapeHtml4(action_L2[25].toString()): " - "%>
																						</div> 
																					</div> 
																				<%}else{ %>
																					<div class="progress div-progress-copy">
																						<div class="progress-bar progressbar" role="progressbar" >
																							Not Yet Started
																						</div>
																					</div> 
																				<%} %>
									                          					
									                          				</td>
									                          			</tr>
																	</table>
																</div>
															</div>
														</div>
												    
												    <!-----------------------------------------------------------   LEVEL 3 ---------------------------------------------------- -->
										                <ul class="">	                
										                <%for(Object[] action_L3 : actionslist){ %>
															<% if(Integer.parseInt(action_L3[3].toString()) == startLevel+3 && Long.parseLong(action_L2[0].toString())== Long.parseLong(action_L3[1].toString()) 
															&&  action_L2[18].toString().trim().equalsIgnoreCase(action_L3[16].toString().trim()) 
															&& Long.parseLong(action_L2[19].toString()) == Long.parseLong(action_L3[17].toString()) ){ %>
																<li>			    
																           
																	<div class="member-view-box action-view-box">
																		<div class=" action-box" >
																				<div 			                          		
													                          		<% int progress_L3 = action_L3[25]!=null ? Integer.parseInt(action_L3[25].toString()) : 0 ; %>
													                          		<% if( action_L3[20]!=null && "C".equalsIgnoreCase(action_L3[20]+"") ){ %>
			                          													class="action-box-header Q5"
			                          												<%}else if( progress_L3 >= 0 && progress_L3 <= 25  ){ %>
													                          			class="action-box-header Q1"
													                          		<%}else if( progress_L3 >= 26 && progress_L3 <= 50  ){ %>
													                          			class=" action-box-header Q2"
													                          		<%}else if( progress_L3 >= 51 && progress_L3 <= 75  ){ %>
													                          			class=" action-box-header Q3"
													                          		<%}else if(  progress_L3 >= 76 && progress_L3 <= 100  ){ %>
													                          			class="action-box-header Q4"
													                          		<%} %>
													                          		
													                          	  >
																				                          		
																					<span class="span-font" 
													                          			onclick="ActionDetails('<%=action_L3[10] %>',   <!-- assignid -->
													                          									'<%=action_L3[5].toString().trim() %>',   <!-- action item -->
													                          									'<%=action_L3[11] %>',   <!-- action No -->
													                          									'<%if(action_L3[25]!=null){ %> <%=action_L2[25] %>% <%}else{ %>0<%} %>', <!-- progress -->
													                          									'<%=sdf.format(action_L3[4]) %>', <!-- action date -->
													                          									'<%=sdf.format(action_L3[24]) %>', <!-- enddate -->
													                          									'<%=sdf.format(action_L3[12]) %>', <!-- orgpdc -->
													                          									'<%=action_L3[22].toString().trim()%>', <!-- assignor -->
													                          									'<%=action_L3[23].toString().trim()%>', <!-- assignee -->
							                          															'<%=action_L3[6]%>' <!-- action type -->
													                          									);" >      
																									
																					<%=action_L3[11]!=null?StringEscapeUtils.escapeHtml4(action_L3[11].toString()): " - " %></span >           
																				      
																				</div>
																				<div class="action-box-body cursor-pointer" align="center" >
																					<table class="card-body-table">
																						<tr>
													                          				<th class="width-40">Assignee :</th>
													                          				<td  >&emsp;<%=action_L3[23]!=null?StringEscapeUtils.escapeHtml4(action_L3[23].toString()): " - " %></td>
													                          			</tr>
													                          			<tr>
													                          				<th >PDC :</th>
													                          				<td >&emsp;<%=action_L3[24]!=null?sdf.format(action_L3[24]):" - " %></td>
													                          			</tr>
													                          			<tr>
													                          				<th >Progress (%) :</th>
													                          				<td class="td-padding10">
													                          					
													                          					<%if(action_L3[25]!=null){ %>
															                          				<div class="progress div-progress-copy">
																										<div class="progress-bar progress-bar-striped width-<%=action_L3[25]%>" role="progressbar" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
																											<%=action_L3[25]!=null?StringEscapeUtils.escapeHtml4(action_L3[25].toString()): " - "%>
																										</div> 
																									</div> 
																								<%}else{ %>
																									<div class="progress div-progress-copy" >
																										<div class="progress-bar progressbar" role="progressbar" >
																											Not Yet Started
																										</div>
																									</div> 
																								<%} %>
													                          					
													                          				</td>
													                          			</tr>
																					</table>
																				</div>
																			</div>
																		</div>
																    														    
																    
																    <!-- --------------------------------------------------------   LEVEL 4 ---------------------------------------------------- -->
															                <ul class="">	                
															                <%for(Object[] action_L4 : actionslist){ %>
																				<% if(Integer.parseInt(action_L4[3].toString()) == startLevel+4 && Long.parseLong(action_L3[0].toString())== Long.parseLong(action_L4[1].toString())  
																				&& action_L3[18].toString().trim().equalsIgnoreCase(action_L4[16].toString().trim()) 
																				&& Long.parseLong(action_L3[19].toString()) == Long.parseLong(action_L4[17].toString()) ){ %>
																					<li>			    
																					           
																						<div class="member-view-box action-view-box">
																								<div class=" action-box" >
																									<div 			                          		
																		                          		<% int progress_L4 = action_L4[25]!=null ? Integer.parseInt(action_L4[25].toString()) : 0 ; %>
																		                          		<% if( action_L4[20]!=null && "C".equalsIgnoreCase(action_L4[20]+"") ){ %>
			                          																		class="action-box-header Q5"
			                          																	<%}else if( progress_L4 >= 0 && progress_L4 <= 25  ){ %>
																		                          			class="action-box-header Q1"
																		                          		<%}else if( progress_L4 >= 26 && progress_L4 <= 50  ){ %>
																		                          			class=" action-box-header Q2"
																		                          		<%}else if( progress_L4 >= 51 && progress_L4 <= 75  ){ %>
																		                          			class=" action-box-header Q3"
																		                          		<%}else if(  progress_L4 >= 76 && progress_L4 <= 100  ){ %>
																		                          			class="action-box-header Q4"
																		                          		<%} %>
													                          						>
																									                          		
																										<span class="span-font" 
																		                          			onclick="ActionDetails('<%=action_L4[10] %>',   <!-- assignid -->
																		                          									'<%=action_L4[5].toString().trim() %>',   <!-- action item -->
																		                          									'<%=action_L4[11] %>',   <!-- action No -->
																		                          									'<%if(action_L4[25]!=null){ %> <%=action_L4[25] %>% <%}else{ %>0<%} %>', <!-- progress -->
																		                          									'<%=sdf.format(action_L4[4]) %>', <!-- action date -->
																		                          									'<%=sdf.format(action_L4[24]) %>', <!-- enddate -->
																		                          									'<%=sdf.format(action_L4[12]) %>', <!-- orgpdc -->
																		                          									'<%=action_L4[22].toString().trim()%>', <!-- assignor -->
																		                          									'<%=action_L4[23].toString().trim()%>', <!-- assignee -->
							                          																				'<%=action_L4[6]%>' <!-- action type -->
																		                          									);" >
																										
																										
																										<%=action_L4[11]!=null?StringEscapeUtils.escapeHtml4(action_L4[11].toString()): " - " %></span >           
																									     
																									</div>
																									<div class="action-box-body cursor-pointer" >
																										<table class="card-body-table">
																											<tr>
																		                          				<th class="width-40">Assignee :</th>
																		                          				<td  >&emsp;<%=action_L4[23]!=null?StringEscapeUtils.escapeHtml4(action_L4[23].toString()): " - " %></td>
																		                          			</tr>
																		                          			<tr>
																		                          				<th >PDC :</th>
																		                          				<td >&emsp;<%=action_L4[24]!=null?sdf.format(action_L4[24]):" - " %></td>
																		                          			</tr>
																		                          			<tr>
																		                          				<th >Progress (%) :</th>
																		                          				<td class="td-padding10">
																		                          					
																		                          					<%if(action_L4[25]!=null){ %>
																				                          				<div class="progress div-progress-copy" >
																															<div class="progress-bar progress-bar-striped width-<%=action_L4[25]%>" role="progressbar" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
																																<%=action_L4[25]!=null?StringEscapeUtils.escapeHtml4(action_L4[25].toString()): " - "%>
																															</div> 
																														</div> 
																													<%}else{ %>
																														<div class="progress div-progress-copy" >
																															<div class="progress-bar progressbar" role="progressbar" >
																																Not Yet Started
																															</div>
																														</div> 
																													<%} %>
																		                          					
																		                          				</td>
																		                          			</tr>
																										</table>
																									</div>
																								</div>
																							</div>
																					    
																					    
															                		</li>
																				<% } %>
																			<%} %>
															                </ul>
															                
															        <!-- --------------------------------------------------------   LEVEL 4 ---------------------------------------------------- --> 
																    
																    
																    
										                		</li>
															<% } %>
														<%} %>
										                </ul>
										                
										        <!-- --------------------------------------------------------   LEVEL 3 ---------------------------------------------------- -->  
												    
												    
						                		</li>
											<% } %>
										<%} %>
						                </ul>
						                
						        <!-- --------------------------------------------------------   LEVEL 2 ---------------------------------------------------- -->    
									    
									    
									    
									    
			                		</li>
								<% } %>
							<% } %>
			                </ul>
			                
			        <!-- --------------------------------------------------------   LEVEL 1 ---------------------------------------------------- -->        
			           			 
							<% 
							
							} %>
						<% } %>
	        		</li>
		        </ul>
	
	    </div>
	</div>
</div>
	<!-- model Footer -->
		 <div class="modal-footer" >
			 <div  class="div-margin"> 
			 		<h6><span class="text-danger">Note :</span> If you close this Action. All this Sub-Action will also Closed</h6>
			 </div>
		 	<div >
				<button type="submit" class="btn btn-danger btn-sm revoke"   onclick="return  closeAction1()" formmethod="post" formaction="CloseSubmit.htm"> Close Action</button>
				<input type="hidden" name="ActionMainId" value="<%=Assignee[0] %>" />
				<input type="hidden" name="ActionAssignId" value="<%=Assignee[18] %>" />
				<input type="hidden" name="LevelCount" value="<%=Assignee[19] %>" />
				<input type="hidden" name="Remarks" id="actionremarks"  />
				<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" />
			</div>
		 </div>
		 </form><!-- Form End -->		
			</div>
		</div>
	</div><!-- model end -->

<!---------------------------------------------------------------- action modal ----------------------------------------------------- -->

	<div class=" modal bd-example-modal-lg" tabindex="-1" role="dialog" id="action_modal">
		<div class="modal-dialog modal-lg" role="document">
			<div class="modal-content">
				<div class="modal-header div-bg" >
					<div class="row w-100"  >
						<div class="col-md-12" >
							<h5 class="modal-title h5-styl" id="modal_action_no" ></h5>
						</div>
					</div>
					
					 <button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body" align="center">
					<form action="#" method="post" autocomplete="off"  >
						<table class="width-100">
							<tr>
								<th class="width-10"> Action Item :</th>
								<td class="tabledata text-break width-90 padding5px" colspan="3" id="modal_action_item"></td>
							</tr>
							<tr>
								<th class="padding5px" >Assign Date :</th>
								<td class="padding5px" id="modal_action_date"></td>
								<th class="padding5px" >PDC :</th>
								<td class="padding5px;" id="modal_action_PDC"></td>
							</tr>
							<tr>
								<th class="padding5px" >Assignor :</th>
								<td class="padding5px" class="tabledata" id="modal_action_assignor"></td>
								<th class="padding5px" >Assignee :</th>
								<td class="padding5px" class="tabledata" id="modal_action_assignee"></td>
							</tr>
							<tr>
								<th class="padding5px" >Final Progress :</th>
								<td class="padding5px" id="modal_action_progress"></td>
								<th class="padding5px" > Type :</th>
								<td class="padding5px-copy" id="modal_action_type"></td>
							</tr>
							
						</table>
						</form>
						<hr>
						<form action="#" method="get">
							<table class="table table-bordered table-hover table-striped table-condensed width-100" id="" >
								<thead> 
									<tr class="tr-st">
										<th class="th1">SN</th>
										<th class="th2">Progress Date</th>
										<th class="th3"> Progress</th>
										<th class="th4">Remarks</th>
										<th class="th5">Download</th>
									</tr>
								</thead>
								<tbody id="modal_progress_table_body">
									
								</tbody>
							</table>
						</form>
				</div>
				
			</div>
		</div>
	</div>
<!----------------------------------------------------------------Closed action modal ----------------------------------------------------- -->

<!-- ------------------------------- tree script ------------------------------- -->
<script type="text/javascript">

function CloseAction(assignid) {
	$('#ActionAssignfilemodal').modal('toggle');
}
$(function () {
    $('.genealogy-tree ul').hide();
    $('.genealogy-tree>ul').show();
    $('.genealogy-tree ul.active').show();
    $('.genealogy-tree li .action-box-body').on('click', function (e) {
		
        var children = $(this).parent().parent().parent().find('> ul');
        if (children.is(":visible")) children.hide('fast').removeClass('active');
        else children.show('fast').addClass('active');
        e.stopPropagation();
    });
});


</script>
<!-- ------------------------------- tree script ------------------------------- -->

<script type="text/javascript">

	function ActionDetails(InAssignId,InActionItem,InActionNo,InProgress,InActionDate,InEndDate,InPDCOrg,
							InAssignor,InAssignee, InActionType	)
	{
		$("#modal_progress_table").DataTable().destroy();
		$.ajax({		
			type : "GET",
			url : "ActionSubListAjax.htm",
			data : {
				ActionAssignid : InAssignId
			},
			datatype : 'json',
			success : function(result) {
				var result = JSON.parse(result);
				
				$('#modal_action_no').html(InActionNo);
				$('#modal_action_item').html(InActionItem);
				$('#modal_action_date').html(InActionDate);
				$('#modal_action_PDC').html(InEndDate);
				$('#modal_action_assignor').html(InAssignor);
				$('#modal_action_assignee').html(InAssignee);
				
				var ActionType = 'Action';
				
				if(InActionType==='A')
				{
					ActionType = 'Action';
				}
				else if(InActionType==='I')
				{
					ActionType = 'Issue';
				}
				else if(InActionType==='D')
				{
					ActionType = 'Decision';
				}
				else if(InActionType==='R')
				{
					ActionType = 'Recommendation';
				}
				else if(InActionType==='C')
				{
					ActionType = 'Comment';
				}
				else if(InActionType==='K')
				{
					ActionType = 'Risk';
				}
				
				$('#modal_action_type').html(ActionType);
				
				if(InProgress.trim() === '0')
				{
					var progressBar ='<div class="progress div-prg">'; 
					progressBar += 		'<div class="progress-bar prg-bar" role="progressbar"  >';
					progressBar +=		'Not Started'
					progressBar +=		'</div>'; 
					progressBar += '</div>'; 
				}
				else
				{
					var progressBar ='<div class="progress div-prg" >'; 
					progressBar += 		'<div class="progress-bar progress-bar-striped width-'+InProgress+'" role="progressbar" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >';
					progressBar +=		InProgress
					progressBar +=		'</div>'; 
					progressBar += '</div>'; 
				}
				$('#modal_action_progress').html(progressBar);
				
				var htmlStr='';
				if(result.length> 0){
					for(var v=0;v<result.length;v++)
					{	
						htmlStr += '<tr>';
						
						htmlStr += '<td class="tabledata text-center" >'+ (v+1) + '</td>';
						htmlStr += '<td class="tabledata text-center" >'+ moment(new Date(result[v][3]) ).format('DD-MM-YYYY') + '</td>';
						htmlStr += '<td class="tabledata text-center" >'+ result[v][2] + ' %</td>';
						htmlStr += '<td class="tabledata" >'+ result[v][4] + '</td>';
						
						if(result[v][5]=== null)
						{
							htmlStr += '<td class="tabledata text-center">-</td>';
						}
						else
						{
							htmlStr += '<td class="tabledata text-center" ><button type="submit" class="btn btn-sm" name="ActionSubId" value="'+ result[v][5] + '" target="blank" formaction="ActionDataAttachDownload.htm" ><i class="fa fa-download"></i></button></td>';
						}
						htmlStr += '</tr>';
					}
				}
				else
				{
					htmlStr += '<tr>';
					
					htmlStr += '<td colspan="5" class="text-center" > Progress Not Updated </td>';
					
					htmlStr += '</tr>';
				}
				setModalDataTable();
				$('#modal_progress_table_body').html(htmlStr);
				
				
				$('#action_modal').modal('toggle');
			}
		});
		
		
	}
	setModalDataTable();
	function setModalDataTable()
	{
		$("#modal_progress_table").DataTable({
			"lengthMenu": [ 5, 10,25, 50, 75, 100 ],
			"pagingType": "simple",
			"pageLength": 5
		});
	}

function closeAction1(){

	var rem = $("#Remarks").val().trim();
	console.log(rem);
	if(rem!="" && rem!="null" && rem!=null){
		$("#actionremarks").val(rem);
		if(confirm('Are You Sure to Close This Action ?')){
			return true;
		}else{
			return false;
		}
	}else{
		$('#ActionAssignfilemodal').modal('hide');
		alert("Please Enter the Remarks!");
		event.preventDefault;
		$("#Remarks").prop('required',true);
		return false;
	}

}


</script>  
<%}%>

<script type="text/javascript">
var from ="<%=sdf.format(Assignee[4])%>".split("-");
var dt = new Date(from[2], from[1] - 1, from[0]);
	$('#DateCompletion').daterangepicker({
			"singleDatePicker" : true,
			"linkedCalendars" : false,
			"showCustomRangeLabel" : true,
			"minDate" : dt,
			"cancelClass" : "btn-default",
			showDropdowns : true,
			locale : {
				format : 'DD-MM-YYYY'
			}
		});
function close5(progress){
	
	event.preventDefault;
	$("#Remarks").prop('required',true);
	
	const limit = Number(progress);
	
	if(limit < 100){
		return confirm("Are You Sure to Close this Action Before 100%");
	}
	
	if(confirm('Are You Sure to Close This Action ?')){
		return true;
	}else{
		return false;
	}
	
}
function close2(){
	
	event.preventDefault;
	$("#Remarks").prop('required',false);

}
function back(){
	
	event.preventDefault;
	$("#Remarks").prop('required',true);
	if(confirm('Are You Sure to Send Back To Assignee ?')){
		return true;
	}else{
		return false;
	}
	
}
</script>


</body>
</html>