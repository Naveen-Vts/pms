<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" import="java.util.*"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link rel="shortcut icon" type="image/png" href="view/images/drdologo.png">
<jsp:include page="../static/dependancy.jsp"></jsp:include>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
  <spring:url value="/resources/css/milestone/milestoneFilterList.css" var="milestoneFilterList" />     
<link href="${milestoneFilterList}" rel="stylesheet" />
</head>
<body>
<%
FormatConverter fc=new FormatConverter(); 
SimpleDateFormat sdf=fc.getRegularDateFormat();
SimpleDateFormat sdf1=fc.getSqlDateFormat();


List<List<Object[]>> projectidlist = (List<List<Object[]>>)request.getAttribute("milestonefilterlist");
List<Object[]> milestoneactivitystatus =(List<Object[]>)request.getAttribute("milestoneactivitystatus");
String levelid = (String)request.getAttribute("levelid");
String ProjectId = (String)request.getAttribute("ProjectId");
String committeeId = (String)request.getAttribute("CommitteeId");
String MilestoneActivity = (String)request.getAttribute("MilestoneActivity");
%>

<div class="container-fluid">
<div class="row">
			<div class="col-md-12">
				<div class="card shadow-nohover">
				<div class="row card-header">
			     <div class="col-md-9">
					 <h5><%if(ProjectId!=null){
						Object[] ProjectDetail123=(Object[])request.getAttribute("ProjectDetailsMil");
						%>
						<%=ProjectDetail123[2]!=null?StringEscapeUtils.escapeHtml4(ProjectDetail123[2].toString()):" - " %> ( <%=ProjectDetail123[1]!=null?StringEscapeUtils.escapeHtml4(ProjectDetail123[1].toString()):" - " %> ) 
					<%} %>
					</h5> 
					</div>
					<div class="col-md-3">
					<form method="post" action="FilterMilestone.htm" id="projectchange">
											<select class="form-control items width-300" name="milestoneactivity"  required="required"  data-live-search="true" data-container="body" onchange="this.form.submit()">
												<option selected value="A" <%if(MilestoneActivity!=null && "A".equalsIgnoreCase(MilestoneActivity)){%> selected="selected" <%}%>>All</option>
												<%if(milestoneactivitystatus!=null && milestoneactivitystatus.size()>0){ for(Object[] obj : milestoneactivitystatus){%>
													<option value=<%=obj[0]%> <%if(MilestoneActivity!=null && obj[0].toString().equalsIgnoreCase(MilestoneActivity)){%> selected="selected" <%}%>><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()):" - " %> (<%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()):" - " %>)</option>
												<%}}%>
											</select>
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						<input type="hidden" name="projectidvalue" <%if(ProjectId!=null){%> value="<%=ProjectId%>" <%}%>>
						<input type="hidden" name="committeidvalue" <%if(committeeId!=null){%> value="<%=committeeId%>" <%}%>>
					</form>
					
					 </div>
					 </div>
					<div class="card-body">
					
                                              <div class="table-responsive"> 
												<table class="table  table-hover table-bordered">
													<thead>

														<tr>
															<th>Expand</th>
															<th class="td1">Mil-No</th>
														<!-- 	<th style="text-align: left;">Project Name</th> -->
															<th class="td2">Milestone Activity</th>
															<th >Start Date</th>
															<th >End Date</th>	
															<th  class="td3">First OIC </th>
															<th  class="td4">Weightage</th>	
															<th  class="td5" >Progress</th>												

														</tr>
													</thead>
													<tbody>
													
														<%int  count=1;
														List<Object[]> MilestoneList=(List<Object[]>)request.getAttribute("MilestoneActivityList");
														
														if(MilestoneList!=null&&MilestoneList.size()>0){
											
															for(Object[] obj: MilestoneList){ 
																 %>
														<tr class="milestonemodalwhole" id="milestonemodal<%=obj[5] %>"  >
															<td  class="center width-2">
																<span class="clickable collapsed" data-toggle="collapse" id="row<%=count %>" data-target=".row<%=count %>" onclick="ChangeButton('<%=count %>')">
																	<button class="btn btn-sm btn-danger" id="btn<%=count %>"  >
																		<i class="fa fa-minus"  id="fa<%=count%>"></i>
																	 </button>
																</span>
															</td>
															<td class="width-7 text-left"> Mil-<%=obj[5]!=null?StringEscapeUtils.escapeHtml4(obj[5].toString()):" - "%></td>
															<%-- <td class="width-30px"><%=obj[1]%></td> --%>
															<td class="tdDetails"><%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()):" - " %></td>
															
															<td  class="width-8" ><%=obj[2]!=null?sdf.format(obj[2]):" - "%></td>
															<td class="width-8" ><%=obj[3]!=null?sdf.format(obj[3]):" - "%></td>
															<td  class="width-15" ><%=obj[6]!=null?StringEscapeUtils.escapeHtml4(obj[6].toString()):" - "%></td>
															<td  class="width-9"  align="center"><%=obj[13]!=null?StringEscapeUtils.escapeHtml4(obj[13].toString()):" - "%></td>	
															<td>
															<%if(!obj[12].toString().equalsIgnoreCase("0")){ %>
															<div class="progress class3">
															<div class="progress-bar progress-bar-striped
															<%if(obj[14].toString().equalsIgnoreCase("2")){ %>
															 bg-info
															<%} else if(obj[14].toString().equalsIgnoreCase("3")){ %>
															  bg-success
															<%} else if(obj[14].toString().equalsIgnoreCase("4")){ %>
															  bg-danger
															<%} else if(obj[14].toString().equalsIgnoreCase("5")){ %>
															  bg-warning 
															<%}  %> width-<%=obj[12] %> %>
															" role="progressbar"  aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
															<%=obj[12]!=null?StringEscapeUtils.escapeHtml4(obj[12].toString()):" - " %>
															</div> 
															</div> <%}else{ %>
															<div class="progress class3" >
															<div class="progress-bar noProgress" role="progressbar"   >
															Not Started
															</div>
															</div> <%} %>
															</td>
																
		
														</tr>
														 <tr class=" collapse  row<%=count %> show font-weight-bold" >
                                                         <td></td>
                                                         <td>Sub</td>
                                                         <td>Activity</td>
                                                         <td>Start Date</td>
                                                         <td>End Date</td>
                                                         <td>Date Of Completion</td>
                                                         <td>Sub Weightage</td>
                                                         <td>Sub Progress</td>
                                                         <td></td>
                                                         </tr>
                                                         <% int countA=1;
                                                            List<Object[]> MilestoneA=(List<Object[]>)request.getAttribute(count+"MilestoneActivityA");
														 	if(MilestoneA!=null&&MilestoneA.size()>0){
															for(Object[] objA: MilestoneA){
	                                                            List<Object[]> MilestoneB=(List<Object[]>)request.getAttribute(count+"MilestoneActivityB"+countA);
	                                                            if(MilestoneActivity==null || "A".equalsIgnoreCase(MilestoneActivity) || objA[9].toString().equalsIgnoreCase(MilestoneActivity)) {
																%>
														<tr class="  collapse row<%=count %> show">
															<td  class="center width-2"> </td>
															<td class="width-5 text-left"> A-<%=countA%></td>
															<%-- <td class="width-30px"><%=obj[1]%></td> --%>
															<td class="tdDetails"><%=objA[4] %></td>
															<td class="width-30px"><%=objA[2]!=null?sdf.format(objA[2]):" - "%></td>
															<td class="width-8" ><%=objA[3]!=null?sdf.format(objA[3]):" - "%></td>
															<td class="width-30px"><%if(objA[9].toString().equalsIgnoreCase("3")||objA[9].toString().equalsIgnoreCase("5")){ %>
														     <%if(objA[7]!=null){ %>   <%=sdf.format(objA[7]) %> <%}else{ %><%=objA[8]!=null?StringEscapeUtils.escapeHtml4(objA[8].toString()):" - " %> <%} %>
														         <%}else{ %>
														         <%=objA[8]!=null?StringEscapeUtils.escapeHtml4(objA[8].toString()):" - " %>
															 <%} %></td>
															 <td align="center"><%=objA[6]!=null?StringEscapeUtils.escapeHtml4(objA[6].toString()):" - " %></td>
															<td>
															<%if(!objA[5].toString().equalsIgnoreCase("0")){ %>
															<div class="progress class3" >
															<div class="progress-bar progress-bar-striped
															<%if(objA[9].toString().equalsIgnoreCase("2")){ %>
															 bg-info
															<%} else if(objA[9].toString().equalsIgnoreCase("3")){ %>
															  bg-success
															<%} else if(objA[9].toString().equalsIgnoreCase("4")){ %>
															  bg-danger
															<%} else if(objA[9].toString().equalsIgnoreCase("5")){ %>
															  bg-warning
															<%}  %> width-<%=objA[5] %>
															" role="progressbar"  aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
															<%=objA[5]!=null?StringEscapeUtils.escapeHtml4(objA[5].toString()):" - " %>
															</div> 
															</div> <%}else{ %>
															<div class="progress class3" >
															<div class="progress-bar noProgress" role="progressbar"  >
															Not Started
															</div>
															</div> <%} %>
															</td>						
													
                                                         <td></td>
                                                         </tr>
                                                         <%} int countB=1;
														 	if(MilestoneB!=null&&MilestoneB.size()>0){
															for(Object[] objB: MilestoneB){
	                                                            List<Object[]> MilestoneC=(List<Object[]>)request.getAttribute(count+"MilestoneActivityC"+countA+countB);
	                                                            if(MilestoneActivity==null || "A".equalsIgnoreCase(MilestoneActivity) || objB[9].toString().equalsIgnoreCase(MilestoneActivity)) {
																%>
														<tr class=" collapse row<%=count %> show">
															<td  class="center width-2"> </td>
															<td class="width-5 text-left"> &nbsp;&nbsp;&nbsp;B-<%=countB%></td>
															<%-- <td class="width-30px"><%=obj[1]%></td> --%>
															<td class="tdDetails"><%=objB[4] %></td>
															
															<td class="width-30px"><%=objB[2]!=null?sdf.format(objB[2]):" - "%></td>
															<td class="width-8"><%=objB[3]!=null?sdf.format(objB[3]):" - "%></td>
															
															<td class="width-30px"><%if(objB[9].toString().equalsIgnoreCase("3")||objB[9].toString().equalsIgnoreCase("5")){ %>
														      <%if(objB[7]!=null){ %>   <%=sdf.format(objB[7]) %> <%}else{ %><%=objB[8]!=null?StringEscapeUtils.escapeHtml4(objB[8].toString()):" - " %> <%} %>
														         <%}else{ %>
														         <%=objB[8]!=null?StringEscapeUtils.escapeHtml4(objB[8].toString()):" - " %>
															 <%} %></td>
															  <td align="center"><%=objB[6] %></td>
															<td>
															<%if(!objB[5].toString().equalsIgnoreCase("0")){ %>
															<div class="progress class3" >
															<div class="progress-bar progress-bar-striped
															<%if(objB[9].toString().equalsIgnoreCase("2")){ %>
															 bg-info
															<%} else if(objB[9].toString().equalsIgnoreCase("3")){ %>
															  bg-success
															<%} else if(objB[9].toString().equalsIgnoreCase("4")){ %>
															  bg-danger
															<%} else if(objB[9].toString().equalsIgnoreCase("5")){ %>
															  bg-warning
															<%}  %> width-<%=objB[5]  %>
															" role="progressbar"  aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
															<%=objB[5] %>
															</div> 
															</div> <%}else{ %>
															<div class="progress class3" >
															<div class="progress-bar noProgress" role="progressbar" >
															Not Started
															</div>
															</div> <%} %>
															</td>
															
														 													
                                                         <td></td>
                                                         </tr>
                                                         <%} int countC=1;
														 	if(MilestoneC!=null&&MilestoneC.size()>0){
															for(Object[] objC: MilestoneC){
													         List<Object[]> MilestoneD=(List<Object[]>)request.getAttribute(count+"MilestoneActivityD"+countA+countB+countC);
																
													         if(MilestoneActivity==null || "A".equalsIgnoreCase(MilestoneActivity) || objC[9].toString().equalsIgnoreCase(MilestoneActivity)) {
																%>
														<tr class=" collapse  row<%=count %> show">
															<td class="center width-2"> </td>
															<td class="width-5 text-left"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;C-<%=countC%></td>
															<%-- <td class="width-30px"><%=obj[1]%></td> --%>
															<td class="tdDetails"><%=objC[4] %></td>
															
															<td class="width-30px"><%=objC[2]!=null?sdf.format(objC[2]):" - "%></td>
															<td class="width-8"><%=objC[3]!=null?sdf.format(objC[3]):" - "%></td>
															
															<td class="width-30px"><%if(objC[9].toString().equalsIgnoreCase("3")||objC[9].toString().equalsIgnoreCase("5")){ %>
														     <%if(objC[7]!=null){ %>   <%=sdf.format(objC[7]) %> <%}else{ %><%=objC[8]!=null?StringEscapeUtils.escapeHtml4(objC[8].toString()):" - " %> <%} %>
														         <%}else{ %>
														         <%=objC[8]!=null?StringEscapeUtils.escapeHtml4(objC[8].toString()):" - " %>
															 <%} %></td>	
															  <td align="center"><%=objC[6]!=null?StringEscapeUtils.escapeHtml4(objC[6].toString()):" - " %></td>
															<td>
															<%if(!objC[5].toString().equalsIgnoreCase("0")){ %>
															<div class="progress class3" >
															<div class="progress-bar progress-bar-striped
															<%if(objC[9].toString().equalsIgnoreCase("2")){ %>
															 bg-info
															<%} else if(objC[9].toString().equalsIgnoreCase("3")){ %>
															  bg-success
															<%} else if(objC[9].toString().equalsIgnoreCase("4")){ %>
															  bg-danger
															<%} else if(objC[9].toString().equalsIgnoreCase("5")){ %>
															  bg-warning
															<%}  %> width-<%=objC[5] %>
															" role="progressbar"  aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
															<%=objC[5] %>
															</div> 
															</div> <%}else{ %>
															<div class="progress class3" >
															<div class="progress-bar noProgress" role="progressbar"  >
															Not Started
															</div>
															</div> <%} %>
															</td>
															
														
                                                         <td></td>
                                                         </tr>
                                                         <%} int countD=1;
														 	if(MilestoneD!=null&&MilestoneD.size()>0){
															for(Object[] objD: MilestoneD){
	                                                            List<Object[]> MilestoneE=(List<Object[]>)request.getAttribute(count+"MilestoneActivityE"+countA+countB+countC+countD);
	                                                            if(MilestoneActivity==null || "A".equalsIgnoreCase(MilestoneActivity) || objD[9].toString().equalsIgnoreCase(MilestoneActivity)) {
																%>
														<tr class=" collapse row<%=count %> show">
															<td  class="center width-2"> </td>
															<td class="width-5 text-left"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;D-<%=countD%></td>
															<%-- <td class="width-30px"><%=obj[1]%></td> --%>
															<td class="tdDetails"><%=objD[4] %></td>
															
															<td class="width-30px"><%=objD[4]!=null?sdf.format(objB[2]):" - "%></td>
															<td class="width-8"><%=objD[4]!=null?sdf.format(objB[3]):" - "%></td>
															
															<td class="width-30px"><%if(objD[9].toString().equalsIgnoreCase("3")||objD[9].toString().equalsIgnoreCase("5")){ %>
														      <%if(objD[7]!=null){ %>   <%=sdf.format(objD[7]) %> <%}else{ %><%=objD[8]!=null?StringEscapeUtils.escapeHtml4(objD[8].toString()):" - " %> <%} %>
														         <%}else{ %>
														         <%=objD[8]!=null?StringEscapeUtils.escapeHtml4(objD[8].toString()):" - " %>
															 <%} %></td>
															  <td align="center"><%=objD[6]!=null?StringEscapeUtils.escapeHtml4(objD[6].toString()):" - " %></td>
															<td>
															<%if(!objD[5].toString().equalsIgnoreCase("0")){ %>
															<div class="progress class3" >
															<div class="progress-bar progress-bar-striped
															<%if(objD[9].toString().equalsIgnoreCase("2")){ %>
															 bg-info
															<%} else if(objD[9].toString().equalsIgnoreCase("3")){ %>
															  bg-success
															<%} else if(objD[9].toString().equalsIgnoreCase("4")){ %>
															  bg-danger
															<%} else if(objD[9].toString().equalsIgnoreCase("5")){ %>
															  bg-warning
															<%}  %> width-<%=objD[5] %>
															" role="progressbar"  aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
															<%=objD[5] %>
															</div> 
															</div> <%}else{ %>
															<div class="progress class3" >
															<div class="progress-bar noProgress" role="progressbar"   >
															Not Started
															</div>
															</div> <%} %>
															</td>
															
														 													
                                                         <td></td>
                                                         </tr>
                                                         <%} int countE=1;
														 	if(MilestoneE!=null&&MilestoneE.size()>0){
															for(Object[] objE: MilestoneE){
																 if(MilestoneActivity==null || "A".equalsIgnoreCase(MilestoneActivity) || objE[9].toString().equalsIgnoreCase(MilestoneActivity)) {
																%>
														<tr class=" collapse row<%=count %> show">
															<td  class="center width-2"> </td>
															<td class="text-left width-5"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;E-<%=countE%></td>
															<%-- <td class="width-30px"><%=obj[1]%></td> --%>
															<td class="tdDetails"><%=objE[4] %></td>
															
															<td class="width-30px"><%=objE[2]!=null?sdf.format(objE[2]):" - "%></td>
															<td class="width-8"><%=objE[3]!=null?sdf.format(objE[3]):" - "%></td>
															
															<td class="width-30px"><%if(objE[9].toString().equalsIgnoreCase("3")||objE[9].toString().equalsIgnoreCase("5")){ %>
														     <%if(objE[7]!=null){ %>   <%=sdf.format(objE[7]) %> <%}else{ %><%=objE[8]!=null?StringEscapeUtils.escapeHtml4(objE[8].toString()):" - " %> <%} %>
														         <%}else{ %>
														         <%=objE[8]!=null?StringEscapeUtils.escapeHtml4(objE[8].toString()):" - " %>
															 <%} %></td>	
															  <td align="center"><%=objE[6]!=null?StringEscapeUtils.escapeHtml4(objE[6].toString()):" - " %></td>
															<td>
															<%if(!objE[5].toString().equalsIgnoreCase("0")){ %>
															<div class="progress class3" >
															<div class="progress-bar progress-bar-striped
															<%if(objC[9].toString().equalsIgnoreCase("2")){ %>
															 bg-info
															<%} else if(objE[9].toString().equalsIgnoreCase("3")){ %>
															  bg-success
															<%} else if(objE[9].toString().equalsIgnoreCase("4")){ %>
															  bg-danger
															<%} else if(objE[9].toString().equalsIgnoreCase("5")){ %>
															  bg-warning
															<%}  %> width-<%=objE[5] %>
															" role="progressbar"  aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
															<%=objE[5] %>
															</div> 
															</div> <%}else{ %>
															<div class="progress class3" >
															<div class="progress-bar noProgress" role="progressbar" >
															Not Started
															</div>
															</div> <%} %>
															</td>
															
														
                                                         <td></td>
                                                         </tr>
												<% countE++;} } }%>
												<% countD++;} }%>
												<% countC++;} }%>
												<% countB++;} }%>
												<% countA++;} }else{%>
												<tr class="collapse row<%=count %>">
													<td colspan="9"  class="center text-center">No Sub List Found</td>
												</tr>
												<%} %>
												<% count++; }  }else{%>
												<tr >
													<td colspan="9"  class="center text-center">No List Found</td>
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
</body>
<script type="text/javascript">



$( document).on("click", ".milestonemodal", function () {
    
	var milId = $(this).data('id');
    $('.milestonemodalwhole').hide();
    $('.collapse').removeClass('show'); 
    $('#row'+milId.charAt(milId.length-1)).click();
 	$('#'+milId).show();

});


function ChangeButton(id) {
	console.log($( "#btn"+id ).hasClass( "btn btn-sm btn-success" ).toString());
	if($( "#btn"+id ).hasClass( "btn btn-sm btn-success" ).toString()=='true'){
		$( "#btn"+id ).removeClass( "btn btn-sm btn-success" ).addClass( "btn btn-sm btn-danger" );
		$( "#fa"+id ).removeClass( "fa fa-plus" ).addClass( "fa fa-minus" );
    }else{
		$( "#btn"+id ).removeClass( "btn btn-sm btn-danger" ).addClass( "btn btn-sm btn-success" );
		$( "#fa"+id ).removeClass( "fa fa-minus" ).addClass( "fa fa-plus" );
    }
}
</script>
</html>