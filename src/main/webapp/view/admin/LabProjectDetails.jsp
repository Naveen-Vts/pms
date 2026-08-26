
<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.math.BigDecimal"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat,java.text.ParseException,java.math.BigInteger"%>
<%@page import="com.vts.pfms.IndianRupeeFormat" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
 
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Lab Details</title>

<jsp:include page="../static/header.jsp"></jsp:include>

<spring:url value="/resources/css/newfont.css" var="NewFontCss" />
<link href="${NewFontCss}" rel="stylesheet" /> 

<spring:url value="/resources/css/master.css" var="masterCss" />
<link href="${masterCss}" rel="stylesheet" />

<spring:url value="/resources/css/masterdashboard.css" var="masterdashboardCss" />
<link href="${masterdashboardCss}" rel="stylesheet" />

<spring:url value="/resources/css/admin/LabProjectDetails.css" var="labProjectDetails" />
<link href="${labProjectDetails}" rel="stylesheet" />

<spring:url value="/resources/css/admin/adminCommon.css" var="adminCommon" />
<link href="${adminCommon}" rel="stylesheet" />

</head>

<body>
<%
IndianRupeeFormat nfc=new IndianRupeeFormat();
List<Object[]> ProjectHealthData = (List<Object[]>)request.getAttribute("projecthealthdata");
Object[] ProjectHealthTotalData = (Object[])request.getAttribute("projecthealthtotal"); 
int ProjectCount = 0;
String logintype=(String)request.getAttribute("logintype"); 
List<Object[]> projecthealthtotaldg = (List<Object[]>)request.getAttribute("projecthealthtotaldg");
List<Object[]> DashboardFinance= (List<Object[]>)request.getAttribute("DashboardFinance");


%>
	
<div class="container-fluid">		
	<div class="row">
		<div class="col-md-12">
			<div class="row">
				<div class="col-md-12">
					<div >
						<h3>Lab  Details <a class="btn btn-info btn-sm back f-right"   href="MainDashBoard.htm" >Back</a></h3>
						
					</div>
				</div>
			</div>
			<div class="card dis-none bg-transparent" id="projectdetailsview"  >
				
				<div class="card-body"> 
					
					<%if(ProjectHealthData.size()>0){ %>
								
						
							<div class="card-deck mt-3 mb-2" >
							  <div class="card detailscard">
							    <div class="card-body">
							      <h5 class="card-title"><img src="view/images/discuss.png" /> Meeting</h5>
							      <hr>
							      <div class="row">
							      	<div class="col-md-6 circular-progress">
							      		 <div class="progress " data-value='<%=(ProjectHealthTotalData[29]!=null?StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[29].toString()): " - " )%>'>
								          <span class="progress-left">
								          		<span class="progress-bar <%if(Integer.parseInt(ProjectHealthTotalData[29].toString())<=25){%> border-danger <%}%>
																													   <%if( (Integer.parseInt(ProjectHealthTotalData[29].toString())>25) && (Integer.parseInt(ProjectHealthTotalData[29].toString())<=50)){%> border-orange<%}%>
																													   <%if( (Integer.parseInt(ProjectHealthTotalData[29].toString())>50) && (Integer.parseInt(ProjectHealthTotalData[29].toString())<=75)){%> border-warning <%}%>
																													   <%if( (Integer.parseInt(ProjectHealthTotalData[29].toString())>75) && (Integer.parseInt(ProjectHealthTotalData[29].toString())<=100)){%> border-success <%}%>
																					"></span>             
								          </span>
								          <span class="progress-right">
								                <span class="progress-bar <%if(Integer.parseInt(ProjectHealthTotalData[29].toString())<=25){%> border-danger<%}%>
																													   <%if( (Integer.parseInt(ProjectHealthTotalData[29].toString())>25) && (Integer.parseInt(ProjectHealthTotalData[29].toString())<=50)){%> border-orange<%}%>
																													   <%if( (Integer.parseInt(ProjectHealthTotalData[29].toString())>50) && (Integer.parseInt(ProjectHealthTotalData[29].toString())<=75)){%> border-warning <%}%>
																													   <%if( (Integer.parseInt(ProjectHealthTotalData[29].toString())>75) && (Integer.parseInt(ProjectHealthTotalData[29].toString())<=100)){%> border-success <%}%>
																					"></span>   
								          </span>
								          <div class="progress-value w-100 h-100 rounded-circle d-flex align-items-center justify-content-center">
								            <div class="h4 font-weight-bold" id="pmrcprogress" ><%=(ProjectHealthTotalData[29]!=null?StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[29].toString()): " - " )%>%</div>
								          </div>
								        </div>
								        <div><h6 class="mb-2" >PMRB</h6></div>
								        <hr class="m-2" >
								        <table class="countstable mx-auto"  >
								        	<tr>
								        		<td class="td-font" id="meetingsvaluepmrc" data-toggle="tooltip" title="Held / To be Held / Total to be Held" >
								        			<%if(ProjectHealthTotalData !=null){%> <span><%=ProjectHealthTotalData[0]!=null?StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[0].toString()): " - " %> / <%=ProjectHealthTotalData[2]!=null?StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[2].toString()): " - " %> / <%=ProjectHealthTotalData[46]!=null?StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[46].toString()): " - " %></span> <%}%>
								        		</td>
								        	</tr>
								        </table>
							      	</div>
							      	<div class="col-md-6 circular-progress">
							      		 <div class="progress "  data-value='<%=(ProjectHealthTotalData[31]!=null?StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[31].toString()): " - " )%>'>
								          <span class="progress-left">
								          		<span class="progress-bar <%if(Integer.parseInt(ProjectHealthTotalData[31].toString())<=25){%> border-danger<%}%>
																													   <%if( (Integer.parseInt(ProjectHealthTotalData[31].toString())>25) && (Integer.parseInt(ProjectHealthTotalData[31].toString())<=50)){%> border-orange<%}%>
																													   <%if( (Integer.parseInt(ProjectHealthTotalData[31].toString())>50) && (Integer.parseInt(ProjectHealthTotalData[31].toString())<=75)){%> border-warning <%}%>
																													   <%if( (Integer.parseInt(ProjectHealthTotalData[31].toString())>75) && (Integer.parseInt(ProjectHealthTotalData[31].toString())<=100)){%> border-success <%}%>
																					"></span>             
								          </span>
								          <span class="progress-right">
								                <span class="progress-bar <%if(Integer.parseInt(ProjectHealthTotalData[31].toString())<=25){%> border-danger<%}%>
																													   <%if( (Integer.parseInt(ProjectHealthTotalData[31].toString())>25) && (Integer.parseInt(ProjectHealthTotalData[31].toString())<=50)){%> border-orange<%}%>
																													   <%if( (Integer.parseInt(ProjectHealthTotalData[31].toString())>50) && (Integer.parseInt(ProjectHealthTotalData[31].toString())<=75)){%> border-warning <%}%>
																													   <%if( (Integer.parseInt(ProjectHealthTotalData[31].toString())>75) && (Integer.parseInt(ProjectHealthTotalData[31].toString())<=100)){%> border-success <%}%>
																					"></span>   
								          </span>
								          <div class="progress-value w-100 h-100 rounded-circle d-flex align-items-center justify-content-center">
								            <div class="h4 font-weight-bold" ><%=(ProjectHealthTotalData[31]!=null?StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[31].toString()): " - " )%>%</div>
								          </div>
								        </div>
								        <div><h6 class="mb-2">EB</h6></div>
								        <hr class="m-2">
								        <table class="countstable mx-auto">
									        	<tr>
									        		<td class="td-font" id="meetingsvalueeb"  data-toggle="tooltip" title="Held / To be Held / Total to be Held"  >
									        		<%if(ProjectHealthTotalData !=null){%> <span><%=ProjectHealthTotalData[3]!=null?StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[3].toString()): " - " %> / <%=ProjectHealthTotalData[5]!=null?StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[5].toString()): " - " %> / <%=ProjectHealthTotalData[47]!=null?StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[47].toString()): " - " %></span> <%}%>
									        		</td>
									        	</tr>
									     </table>
							      	</div>
							      </div>
							    </div>
							    
							  </div>
							  <div class="card detailscard">
							    <div class="card-body">
							      <h5 class="card-title"><img src="view/images/goal.png" /> Milestone</h5>
							      <hr>
							      <div class="row">
							      	<div class="col-md-6 circular-progress">
							      		<div class="progress " data-value='<%if(ProjectHealthTotalData[10] !=null){%><%=ProjectHealthTotalData[10] %><%} %>'>
								          <span class="progress-left">
								                        <span class="progress-bar <%if(Integer.parseInt(ProjectHealthTotalData[10].toString())<=25){%> border-danger<%}%>
																					<%if( (Integer.parseInt(ProjectHealthTotalData[10].toString())>25) && (Integer.parseInt(ProjectHealthTotalData[10].toString())<=50)){%> border-orange<%}%>
																					<%if( (Integer.parseInt(ProjectHealthTotalData[10].toString())>50) && (Integer.parseInt(ProjectHealthTotalData[10].toString())<=75)){%> border-warning <%}%>
																					<%if( (Integer.parseInt(ProjectHealthTotalData[10].toString())>75) && (Integer.parseInt(ProjectHealthTotalData[10].toString())<=100)){%> border-success <%}%>
																					"></span>   
								          </span>
								          <span class="progress-right">
								                        <span class="progress-bar <%if(Integer.parseInt(ProjectHealthTotalData[10].toString())<=25){%> border-danger<%}%>
																					<%if( (Integer.parseInt(ProjectHealthTotalData[10].toString())>25) && (Integer.parseInt(ProjectHealthTotalData[10].toString())<=50)){%> border-orange<%}%>
																					<%if( (Integer.parseInt(ProjectHealthTotalData[10].toString())>50) && (Integer.parseInt(ProjectHealthTotalData[10].toString())<=75)){%> border-warning <%}%>
																					<%if( (Integer.parseInt(ProjectHealthTotalData[10].toString())>75) && (Integer.parseInt(ProjectHealthTotalData[10].toString())<=100)){%> border-success <%}%>
																					"></span>   
								          </span>
								          <div class="progress-value w-100 h-100 rounded-circle d-flex align-items-center justify-content-center">
								            <div class="h4 font-weight-bold"><span id="milestonepercentage"><%if(ProjectHealthTotalData[10] !=null){%><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[10].toString()) %><%} %>%</span></div>
								          </div>
								     	</div>
							      	</div>
							      	<div class="col-md-6">
							      		<div class="bigcount">
							      			<h1><%if(ProjectHealthTotalData[8] !=null){%><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[8].toString()) %><%} %></h1>
							      			<p class="textfont"><span class="green legend-shadow">&#x220E;</span> &nbsp;Completed</p>
							      		</div>
							      		
							      		<div class="bigcount">
							      			<h4><%if(ProjectHealthTotalData[9] !=null){%><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[9].toString()) %><%} %></h4>
							      			<p class="textfont">Total</p>
							      		</div>
							      	</div>
							      </div>
							    </div>
							    <table class="countstable card-deck-table mb-0 border-top" >
									  <thead>
									  	<tr>
									  		<td class="border-right" data-toggle="tooltip" title="Delayed" >&nbsp;<%if(ProjectHealthTotalData[7] !=null){%><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[7].toString()) %><%} %></td>
									  		<td data-toggle="tooltip" title="Pending"  >&nbsp;<%if(ProjectHealthTotalData[6] !=null){%><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[6].toString()) %><%} %></td>
									  	</tr>
									  </thead>
									  <tbody>
									  	<tr>
									  		<th scope="col" class="textfont border-right" ><span class="yellow">&#x220E;</span> &nbsp;Delayed</th>
									      	<th scope="col" class="textfont"><span class="red">&#x220E;</span> &nbsp;Pending</th>
									    </tr>
									  </tbody> 
								</table>
							  </div>
							  <div class="card detailscard">
							    <div class="card-body">
							      <h5 class="card-title"><img src="view/images/action1.png" /> Action</h5>
							      <hr>
							      <div class="row">
							      	<div class="col-md-6 circular-progress">
							      		<div class="progress " data-value='<%if(ProjectHealthTotalData[37] !=null){%><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[37].toString()) %><%} %>'>
								          <span class="progress-left">
								                        <span class="progress-bar <%if(Integer.parseInt(ProjectHealthTotalData[37].toString())<=25){%> border-danger<%}%>
																					<%if( (Integer.parseInt(ProjectHealthTotalData[37].toString())>25) && (Integer.parseInt(ProjectHealthTotalData[37].toString())<=50)){%> border-orange<%}%>
																					<%if( (Integer.parseInt(ProjectHealthTotalData[37].toString())>50) && (Integer.parseInt(ProjectHealthTotalData[37].toString())<=75)){%> border-warning <%}%>
																					<%if( (Integer.parseInt(ProjectHealthTotalData[37].toString())>75) && (Integer.parseInt(ProjectHealthTotalData[37].toString())<=100)){%> border-success <%}%>
																					"></span>   
								          </span>
								          <span class="progress-right">
								                        <span class="progress-bar <%if(Integer.parseInt(ProjectHealthTotalData[37].toString())<=25){%> border-danger<%}%>
																					<%if( (Integer.parseInt(ProjectHealthTotalData[37].toString())>25) && (Integer.parseInt(ProjectHealthTotalData[37].toString())<=50)){%> border-orange<%}%>
																					<%if( (Integer.parseInt(ProjectHealthTotalData[37].toString())>50) && (Integer.parseInt(ProjectHealthTotalData[37].toString())<=75)){%> border-warning <%}%>
																					<%if( (Integer.parseInt(ProjectHealthTotalData[37].toString())>75) && (Integer.parseInt(ProjectHealthTotalData[37].toString())<=100)){%> border-success <%}%>
																					"></span>   
								          </span>
								          <div class="progress-value w-100 h-100 rounded-circle d-flex align-items-center justify-content-center">
								            <div class="h4 font-weight-bold"><%if(ProjectHealthTotalData[37] !=null){%><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[37].toString()) %><%} %>%</div>
								          </div>
								     	</div>
							      	</div>
							      	<div class="col-md-6">
							      		<div class="bigcount">
							      			<h1><%if(ProjectHealthTotalData[14] !=null){ %><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[14].toString()) %><% } %></h1>
							      			<p class="textfont"><span class="green legend-shadow">&#x220E;</span> &nbsp;Completed</p>
							      		</div>
							      		<div class="bigcount">
							      			<h4><%if(ProjectHealthTotalData[15] !=null){ %><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[15].toString()) %><% } %></h4>
							      			<p class="textfont">Total</p>
							      		</div>
							      	</div>
							      </div>
							    </div>
							    <table class="countstable card-deck-table mb-0 border-top" >
									  <thead>
									  	<tr>
									  		<td class="border-right" data-toggle="tooltip" title="Delayed" ><%if(ProjectHealthTotalData[13] !=null){%>&nbsp;<%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[13].toString()) %><%} %></td>
									  		<td class="border-right" data-toggle="tooltip" title="Forwarded" ><%if(ProjectHealthTotalData[12] !=null){%> &nbsp;<%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[12].toString()) %><%} %></td>
									  		<td data-toggle="tooltip" title="Pending" ><%if(ProjectHealthTotalData[11] !=null){%>&nbsp;<%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[11].toString()) %><%} %></td>
									  	</tr>
									  </thead>
									  <tbody>
									  	<tr>
									      <th scope="col" class="textfont border-right"><span class="yellow">&#x220E;</span> &nbsp;Delayed</th>
									      <th scope="col" class="textfont border-right"><span class="blue">&#x220E;</span> &nbsp;Forwarded</th>
									      <th scope="col" class="textfont"><span class="red">&#x220E;</span> &nbsp;Pending</th>
									    </tr>
									  </tbody>
								</table>
							  </div>
							  <div class="card detailscard">
							    <div class="card-body">
							      <h5 class="card-title"><img src="view/images/risk 1.png" /> Risk</h5>
							      <hr>
							      <div class="row">
							      	<div class="col-md-6 circular-progress">
							      		<div class="progress " data-value='<%if(ProjectHealthTotalData[39] !=null){%><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[39].toString()) %><%} %>'>
								          <span class="progress-left">
								                         <span class="progress-bar <%if(Integer.parseInt(ProjectHealthTotalData[39].toString())<=25){%> border-danger<%}%>
																													   <%if( (Integer.parseInt(ProjectHealthTotalData[39].toString())>25) && (Integer.parseInt(ProjectHealthTotalData[39].toString())<=50)){%> border-orange<%}%>
																													   <%if( (Integer.parseInt(ProjectHealthTotalData[39].toString())>50) && (Integer.parseInt(ProjectHealthTotalData[39].toString())<=75)){%> border-warning <%}%>
																													   <%if( (Integer.parseInt(ProjectHealthTotalData[39].toString())>75) && (Integer.parseInt(ProjectHealthTotalData[39].toString())<=100)){%> border-success <%}%>
																					"></span>  
								          </span>
								          <span class="progress-right">
								                         <span class="progress-bar <%if(Integer.parseInt(ProjectHealthTotalData[39].toString())<=25){%> border-danger<%}%>
																													   <%if( (Integer.parseInt(ProjectHealthTotalData[39].toString())>25) && (Integer.parseInt(ProjectHealthTotalData[39].toString())<=50)){%> border-orange<%}%>
																													   <%if( (Integer.parseInt(ProjectHealthTotalData[39].toString())>50) && (Integer.parseInt(ProjectHealthTotalData[39].toString())<=75)){%> border-warning <%}%>
																													   <%if( (Integer.parseInt(ProjectHealthTotalData[39].toString())>75) && (Integer.parseInt(ProjectHealthTotalData[39].toString())<=100)){%> border-success <%}%>
																					"></span>  
								          </span>
								          <div class="progress-value w-100 h-100 rounded-circle d-flex align-items-center justify-content-center">
								            <div class="h4 font-weight-bold"><%if(ProjectHealthTotalData[39] !=null){%><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[39].toString()) %><%} %>%</div>
								          </div>
								     	</div>
							      	</div>
							      	<div class="col-md-6">
							      		<div class="bigcount">
							      			<h1><%if(ProjectHealthTotalData[16] !=null){%><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[16].toString()) %><%} %></h1>
							      			<p class="textfont"><span class="green legend-shadow">&#x220E;</span> &nbsp;Completed</p>
							      		</div>
							      		<div class="bigcount">
							      			<h4><%if(ProjectHealthTotalData[18] !=null){%><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[18].toString()) %><%} %></h4>
							      			<p class="textfont">Total</p>
							      		</div>
							      	</div>
							      </div>
							    </div>
							    <table class="countstable card-deck-table mb-0 border-top" >
									  <thead>
									  	<tr>
									  		<td class="border-right" data-toggle="tooltip" title="Delayed" >&nbsp;0</td>
									  		<td data-toggle="tooltip" title="Pending" ><%if(ProjectHealthTotalData[17] !=null){%>&nbsp;<%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[17].toString()) %><%} %></td>
									  	</tr>
									  </thead>
									 <tbody>
									  	<tr>
									      <th scope="col" class="textfont border-right"><span class="yellow">&#x220E;</span> &nbsp;Delayed</th>
									      <th scope="col" class="textfont"><span class="red">&#x220E;</span> &nbsp;Pending</th>
									    </tr>
									  </tbody> 
								</table>
							  </div>
							   <div class="card detailscard">
							    <div class="card-body">
							    	<div class="d-in-fl">
								      <h5 class="card-title mb-2" >
										<img src="view/images/rupee.png" /> Finance<!--  <span style="font-size: 14px !important">(&#8377;Cr)</span> -->
									  </h5>
								   </div>
							      <hr class="margin-x-y">
									<table class="table  financetable" >
									  <thead>
									    <tr>
									      <th scope="col">
									     	(In &#8377; k Cr)
									      </th>
									      <th scope="col">Sanc</th>
									      <th scope="col">Exp</th>
									      <th scope="col">Bal</th>
									    </tr>
									  </thead>
									  <%
									  
								  		BigDecimal sanction=new BigDecimal(0.00);
									  	BigDecimal expenditure=new BigDecimal(0.00);
									  	BigDecimal balance=new BigDecimal(0.00);
								 
									  	BigDecimal	revBal=new BigDecimal(0.00);
									  	BigDecimal	capBal=new BigDecimal(0.00);
									  	BigDecimal	othBal=new BigDecimal(0.00);
									  %>
									  <tbody>
									  	<tr>
									      <th scope="row">Revenue</th>
									      <td><span class="text-success">&#8377;</span> <%=DashboardFinance.get(0)[3]!=null?StringEscapeUtils.escapeHtml4(DashboardFinance.get(0)[3].toString()): " - " %></td>
									      <td><span class="text-success">&#8377;</span> <%=DashboardFinance.get(0)[4]!=null?StringEscapeUtils.escapeHtml4(DashboardFinance.get(0)[4].toString()): " - " %></td>
									      <%  revBal = new BigDecimal(DashboardFinance.get(0)[3].toString()).subtract(new BigDecimal(DashboardFinance.get(0)[4].toString())) ; %>
									      <td><span class="text-success">&#8377;</span> <%=revBal%></td>
									    </tr>
									     <% sanction	= sanction.add(new BigDecimal(DashboardFinance.get(0)[3].toString())); %>
									     <% expenditure	= expenditure.add(new BigDecimal(DashboardFinance.get(0)[4].toString())); %>
									     <% balance	= balance.add(revBal); %>
									     <tr>
									      <th scope="row">Capital</th>
									      <td><span class="text-success">&#8377;</span> <%=DashboardFinance.get(1)[3]!=null?StringEscapeUtils.escapeHtml4(DashboardFinance.get(1)[3].toString()): " - " %></td>
									      <td><span class="text-success">&#8377;</span> <%=DashboardFinance.get(1)[4]!=null?StringEscapeUtils.escapeHtml4(DashboardFinance.get(1)[4].toString()): " - " %></td>
									      <%  capBal = new BigDecimal(DashboardFinance.get(1)[3].toString()).subtract(new BigDecimal(DashboardFinance.get(1)[4].toString())) ; %>
									      <td><span class="text-success">&#8377;</span> <%=capBal %></td>
									    </tr>
									     <% sanction	= sanction.add(new BigDecimal(DashboardFinance.get(1)[3].toString())); %>
									     <% expenditure	= expenditure.add(new BigDecimal(DashboardFinance.get(1)[4].toString())); %>
									     <% balance	= balance.add(capBal); %>
									    
									    <tr>
									      <th scope="row">Others</th>
									      <td><span class="text-success">&#8377;</span> <%=DashboardFinance.get(2)[3]!=null?StringEscapeUtils.escapeHtml4(DashboardFinance.get(2)[3].toString()): " - " %></td>
									      <td><span class="text-success">&#8377;</span> <%=DashboardFinance.get(2)[4]!=null?StringEscapeUtils.escapeHtml4(DashboardFinance.get(2)[4].toString()): " - " %></td>
									      <%  othBal = new BigDecimal(DashboardFinance.get(2)[3].toString()).subtract(new BigDecimal(DashboardFinance.get(2)[4].toString())) ; %>
									      <td><span class="text-success">&#8377;</span> <%=othBal %></td>
									    </tr> 
									     <% sanction	= sanction.add(new BigDecimal(DashboardFinance.get(2)[3].toString())); %>
									     <% expenditure	= expenditure.add(new BigDecimal(DashboardFinance.get(2)[4].toString())); %>
									     <% balance	= balance.add(othBal); %>
									    <tr>
									      <th scope="row">Total</th>
									      <td><span class="text-success">&#8377;</span> <%=sanction%></td>
									      <td><span class="text-success">&#8377;</span> <%=expenditure %></td>
									      <td><span class="text-success">&#8377;</span> <%=balance %></td>
									    </tr>
									  </tbody>
									</table>
						
								   </div>
							  	</div>	 
							</div>
							
							<br>
				
			            					            		
								<div class="project-health-table">
								
									<table class="table meeting tableFixHead fixed-table tab-fix"    id="overalldiv"> 
										<thead class="table-style-white">												
											<tr>
												<td class="w-4">
													<a  data-toggle="modal"  class="fa faa-pulse animated cursor-pointer px-4" data-target="#exampleModal1" data-whatever="@mdo" ><i class="fa fa-info-circle f-rem"  aria-hidden="true"></i> </a>
												</td>
												<td class="w-15"><span class="span-font">Project</span></td>
												<td class="p-0"><span class="span-font">PMRB </span></td>
												<td class="p-0"><span class="span-font">EB </span></td>
												<td ><span class="span-font">Milestone </span></td>
												<td ><span class="span-font">Action</span></td>
												<td ><span class="span-font">Risk</span></td>
												<td ><span class="span-font">Finance</span></td>
											</tr>
										</thead>
										
										<tbody>
										
											<%for(Object[] obj : ProjectHealthData){
			
													 
												%>
										
											<tr>
												<td><a href="javascript:ProjectDetails('<%=obj[2]%>')"> <i class="fa fa-hand-o-right f-rem" aria-hidden="true"></i></a></td>
												<td   style="font-weight: 800; font-size:1rem;text-align:left;
												
														<% if(obj[45]!=null) {if(obj[45].toString().equalsIgnoreCase("IA")){%>color: green<%} 
														else if(obj[45].toString().equalsIgnoreCase("IN")){%>color: #007bff <%} 
														else if(obj[45].toString().equalsIgnoreCase("IAF")){%>color: #1F4690 <%} 
														else if(obj[45].toString().equalsIgnoreCase("IH")){%>color: #8E3200 <%} 
														else if(obj[45].toString().equalsIgnoreCase("OH")){%>color: #EE5007 <%} 
														else if(obj[45].toString().equalsIgnoreCase("DRDO")){%>color: #231955 <%} 
														else {%>color:black <%} }else{ %>color:black <%}%>">
												
												<div data-toggle="tooltip" 
													title="<%if(obj[45]!=null) { if(obj[45].toString().equalsIgnoreCase("IA")){%>Indian Army<%}
													else if(obj[45].toString().equalsIgnoreCase("IN")){%>Indian Navy <%} 
													else if(obj[45].toString().equalsIgnoreCase("IAF")){%>Indian Air Force<%} 
													else if(obj[45].toString().equalsIgnoreCase("IH")){%>Home Land Security <%} 
													else if(obj[45].toString().equalsIgnoreCase("DRDO")){%>DRDO <%} 
													else if(obj[45].toString().equalsIgnoreCase("OH")){%>Others <%} 
													else {%> -  <%} }else{ %>-<%}%>">
												
														  &#11044;&nbsp; <span class="tableprojectname" style="color:black !important;font-size: 13px"> 
														  	<%if(obj[46]!=null){%><%=StringEscapeUtils.escapeHtml4(obj[46].toString()) %><%}else {%>-<%} %> /
														  	<%if(obj[3]!=null){%><%=StringEscapeUtils.escapeHtml4(obj[3].toString()) %><%}else {%>-<%} %> /
														  	<%if(obj[44]!=null){%><%=StringEscapeUtils.escapeHtml4(obj[44].toString())%><%}else {%>-<%} %>
														  	</span> 	
												
													</div>
												
												</td>
												<td class="custom-td">
													<%if(Integer.parseInt(obj[8].toString())>0){ %>
														
														<div class="row">
															<div class="col-md-10">
															    <div class="progress"  >
																  <div class="progress-bar progress-bar-striped bg-success" onclick="overallmeetingredirect('<%=obj[2]%>','1', 'B')" style="width:<%=obj[5]%>%;" data-toggle="tooltip" title="PMRB Held : <%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - "%> / <%=obj[8]!=null?StringEscapeUtils.escapeHtml4(obj[8].toString()): " - " %>" ></div>
																  <div class="progress-bar progress-bar-striped bg-primary" onclick="overallmeetingredirect('<%=obj[2]%>','1', 'C' )" style="width:<%=obj[7]%>%;" data-toggle="tooltip" title="PMRB Pending : <%=obj[6]!=null?StringEscapeUtils.escapeHtml4(obj[6].toString()): " - "%> / <%=obj[8]!=null?StringEscapeUtils.escapeHtml4(obj[8].toString()): " - " %>" ></div>
																</div>
														  	</div>
															<div class="col-md-2" style="padding-left: 0px !important">
																<span class="health-circle" style="<%if(Integer.parseInt(obj[7].toString())<=25){%> background-color:green <%}%>
																								   <%if( (Integer.parseInt(obj[7].toString())>25) && (Integer.parseInt(obj[7].toString())<=50)){%> background-color: #F8CB2E;color:black <%}%>
																								   <%if( (Integer.parseInt(obj[7].toString())>50) && (Integer.parseInt(obj[7].toString())<=75)){%> background-color: #EE5007 <%}%>
																								   <%if( (Integer.parseInt(obj[7].toString())>75) && (Integer.parseInt(obj[7].toString())<=100)){%> background-color:red <%}%>
																"><%=obj[7]!=null?StringEscapeUtils.escapeHtml4(obj[7].toString()): " - " %></span>
															</div>
														</div>
						
													<%}else{ %>
													<div class="progress" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
														<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
															Nil
														</div>
													</div>
													<%} %>
												</td>
												<td class="custom-td">
													<%if(Integer.parseInt(obj[13].toString())>0){ %>
														<div class="row">
															<div class="col-md-10">
																<div class="progress" >
																  <div class="progress-bar progress-bar-striped bg-success" onclick="overallmeetingredirect('<%=obj[2]%>','2', 'B')" style="width:<%=obj[10]%>%;" data-toggle="tooltip" title="EB Held : <%=obj[9]!=null?StringEscapeUtils.escapeHtml4(obj[9].toString()): " - "%> / <%=obj[13]!=null?StringEscapeUtils.escapeHtml4(obj[13].toString()): " - " %>" ></div>
																  <div class="progress-bar progress-bar-striped bg-primary" onclick="overallmeetingredirect('<%=obj[2]%>','2', 'C')" style="width:<%=obj[12]%>%;" data-toggle="tooltip" title="EB Pending :  <%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - "%> / <%=obj[13] !=null?StringEscapeUtils.escapeHtml4(obj[13].toString()): " - "%>" ></div>
																</div>
															</div>
															<div class="col-md-2" style="padding-left: 0px !important">
																<span class="health-circle" style="<%if(Integer.parseInt(obj[12].toString())<=25){%> background-color:green <%}%>
																								   <%if( (Integer.parseInt(obj[12].toString())>25) && (Integer.parseInt(obj[12].toString())<=50)){%> background-color: #F8CB2E;color:black <%}%>
																								   <%if( (Integer.parseInt(obj[12].toString())>50) && (Integer.parseInt(obj[12].toString())<=75)){%> background-color: #EE5007 <%}%>
																								   <%if( (Integer.parseInt(obj[12].toString())>75) && (Integer.parseInt(obj[12].toString())<=100)){%> background-color:red <%}%>
																"><%=obj[12]!=null?StringEscapeUtils.escapeHtml4(obj[12].toString()): " - " %></span>
															</div>
														</div>
													<%}else{ %>
													<div class="progress" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
														<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
															Nil
														</div>
													</div>
													<%} %>
												</td>
												<td class="custom-td">
													<%if(Integer.parseInt(obj[20].toString())>0){ %>
													<div class="row">
														<div class="col-md-10">
															<div class="progress" onclick="overallcommonredirect('mil','<%=obj[2]%>')">
															  <div class="progress-bar progress-bar-striped bg-success" style="width:<%=obj[19]%>%" data-toggle="tooltip" title="Completed : <%=obj[18]!=null?StringEscapeUtils.escapeHtml4(obj[18].toString()): " - "%> / <%=obj[20]!=null?StringEscapeUtils.escapeHtml4(obj[20].toString()): " - " %>" ></div>
															  <div class="progress-bar progress-bar-striped bg-warning" style="width:<%=obj[17]%>%" data-toggle="tooltip" title="Delayed : <%=obj[16]!=null?StringEscapeUtils.escapeHtml4(obj[16].toString()): " - "%> / <%=obj[20]!=null?StringEscapeUtils.escapeHtml4(obj[20].toString()): " - " %>" ></div>
															  <div class="progress-bar progress-bar-striped bg-danger" style="width:<%=obj[15]%>%" data-toggle="tooltip" title="Pending : <%=obj[14]!=null?StringEscapeUtils.escapeHtml4(obj[14].toString()): " - "%> / <%=obj[20]!=null?StringEscapeUtils.escapeHtml4(obj[20].toString()): " - " %>" ></div>															  
															</div>
														</div>
														<div class="col-md-2" style="padding-left: 0px !important">
																<span class="health-circle" style="<%if(Integer.parseInt(obj[15].toString())<=25){%> background-color:green <%}%>
																								   <%if( (Integer.parseInt(obj[15].toString())>25) && (Integer.parseInt(obj[15].toString())<=50)){%> background-color: #F8CB2E;color:black <%}%>
																								   <%if( (Integer.parseInt(obj[15].toString())>50) && (Integer.parseInt(obj[15].toString())<=75)){%> background-color: #EE5007 <%}%>
																								   <%if( (Integer.parseInt(obj[15].toString())>75) && (Integer.parseInt(obj[15].toString())<=100)){%> background-color:red <%}%>
																"><%=obj[15]!=null?StringEscapeUtils.escapeHtml4(obj[15].toString()): " - " %></span>
														</div>
													</div>
													<%}else{ %>
													<div class="progress" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
														<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
															Nil
														</div>
													</div>
													<%} %>
												</td>
												<td class="custom-td">
													<%if(Integer.parseInt(obj[29].toString())>0){ %>
													<div class="row">
														<div class="col-md-10">
															<div class="progress" onclick="overallcommonredirect('action','<%=obj[2]%>')">
															  <div class="progress-bar progress-bar-striped bg-success" style="width:<%=obj[28]%>%" data-toggle="tooltip" title="Completed : <%=obj[27]!=null?StringEscapeUtils.escapeHtml4(obj[27].toString()): " - "%> / <%=obj[29]!=null?StringEscapeUtils.escapeHtml4(obj[29].toString()): " - "%>" ></div>
															  <div class="progress-bar progress-bar-striped bg-warning" style="width:<%=obj[26]%>%" data-toggle="tooltip" title="Delayed : <%=obj[25]!=null?StringEscapeUtils.escapeHtml4(obj[25].toString()): " - "%> / <%=obj[29]!=null?StringEscapeUtils.escapeHtml4(obj[29].toString()): " - "%>" ></div>
															  <div class="progress-bar progress-bar-striped bg-primary" style="width:<%=obj[24]%>%" data-toggle="tooltip" title="Forwarded : <%=obj[23]!=null?StringEscapeUtils.escapeHtml4(obj[23].toString()): " - "%> / <%=obj[29]!=null?StringEscapeUtils.escapeHtml4(obj[29].toString()): " - "%>" ></div>
															  <div class="progress-bar progress-bar-striped bg-danger" style="width:<%=obj[22]%>%" data-toggle="tooltip" title="Pending : <%=obj[21]!=null?StringEscapeUtils.escapeHtml4(obj[21].toString()): " - "%> / <%=obj[29]!=null?StringEscapeUtils.escapeHtml4(obj[29].toString()): " - "%>" ></div>
															</div>
														</div>
														<div class="col-md-2" style="padding-left: 0px !important">
															<span class="health-circle" style="<%if(Integer.parseInt(obj[22].toString())<=25){%> background-color:green <%}%>
																									   <%if( (Integer.parseInt(obj[22].toString())>25) && (Integer.parseInt(obj[22].toString())<=50)){%> background-color: #F8CB2E;color:black <%}%>
																									   <%if( (Integer.parseInt(obj[22].toString())>50) && (Integer.parseInt(obj[22].toString())<=75)){%> background-color: #EE5007 <%}%>
																									   <%if( (Integer.parseInt(obj[22].toString())>75) && (Integer.parseInt(obj[22].toString())<=100)){%> background-color:red <%}%>
															"><%=obj[22]!=null?StringEscapeUtils.escapeHtml4(obj[22].toString()): " - " %></span>
														</div>
													</div>
													<%}else{ %>
													<div class="progress" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
														<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
															Nil
														</div>
													</div>
													<%} %>
												</td>
												<td class="custom-td">
													<%if(Integer.parseInt(obj[34].toString())>0){ %>
													<div class="row">
														<div class="col-md-10">
															<div class="progress" onclick="overallcommonredirect('risk','<%=obj[2]%>')">
															  <div class="progress-bar progress-bar-striped bg-success" style="width:<%=obj[31]%>%" data-toggle="tooltip" title="Completed : <%=obj[30]!=null?StringEscapeUtils.escapeHtml4(obj[30].toString()): " - "%> / <%=obj[34]!=null?StringEscapeUtils.escapeHtml4(obj[34].toString()): " - " %>" ></div>
															  <div class="progress-bar progress-bar-striped bg-danger" style="width:<%=obj[33]%>%" data-toggle="tooltip" title="Pending : <%=obj[32]!=null?StringEscapeUtils.escapeHtml4(obj[32].toString()): " - "%> / <%=obj[34]!=null?StringEscapeUtils.escapeHtml4(obj[34].toString()): " - " %>" ></div>
															</div>
														</div>
														<div class="col-md-2" style="padding-left: 0px !important">
																<span class="health-circle" style="<%if(Integer.parseInt(obj[33].toString())<=25){%> background-color:green <%}%>
																										   <%if( (Integer.parseInt(obj[33].toString())>25) && (Integer.parseInt(obj[33].toString())<=50)){%> background-color: #F8CB2E;color:black <%}%>
																										   <%if( (Integer.parseInt(obj[33].toString())>50) && (Integer.parseInt(obj[33].toString())<=75)){%> background-color: #EE5007 <%}%>
																										   <%if( (Integer.parseInt(obj[33].toString())>75) && (Integer.parseInt(obj[33].toString())<=100)){%> background-color:red <%}%>
																"><%=obj[33]!=null?StringEscapeUtils.escapeHtml4(obj[33].toString()): " - " %></span>
														</div>
													</div>
													<%}else{ %>
													<div class="progress" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
														<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
															Nil
														</div>
													</div>
													<%} %>
												</td>
												<td class="custom-td">
													<%
													BigInteger number = new BigInteger(obj[43].toString());
													if(number.compareTo(new BigInteger("0")) >0){ %>
													<div class="row">
														<div class="col-md-10">
															<div class="progress" onclick="overallfinance()">
															  <div class="progress-bar progress-bar-striped bg-success" style="width:<%=obj[36]%>%" data-toggle="tooltip" title="Expenditure : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[35].toString() ))))%>" ></div>
															  <div class="progress-bar progress-bar-striped bg-warning" style="width:<%=obj[38]%>%" data-toggle="tooltip" title="OC : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[37].toString() ))))%> " ></div>
															  <div class="progress-bar progress-bar-striped bg-primary" style="width:<%=obj[40]%>%" data-toggle="tooltip" title="DIPL : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[39].toString() ))))%> " ></div>
															  <div class="progress-bar progress-bar-striped bg-danger" style="width:<%=obj[42]%>%" data-toggle="tooltip" title="Balance : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[41].toString() ))))%> " ></div>
															</div>
														</div>
														<div class="col-md-2" style="padding-left: 0px !important">
																	<span class="health-circle" style="<%if(Integer.parseInt(obj[36].toString())<=25){%> background-color:green <%}%>
																											   <%if( (Integer.parseInt(obj[36].toString())>25) && (Integer.parseInt(obj[36].toString())<=50)){%> background-color: #F8CB2E;color:black <%}%>
																											   <%if( (Integer.parseInt(obj[36].toString())>50) && (Integer.parseInt(obj[36].toString())<=75)){%> background-color: #EE5007 <%}%>
																											   <%if( (Integer.parseInt(obj[36].toString())>75) && (Integer.parseInt(obj[36].toString())<=100)){%> background-color:red <%}%>
																	"><%=obj[36]!=null?StringEscapeUtils.escapeHtml4(obj[36].toString()): " - " %></span>
														</div>
														<div class="col-md-1" style="padding-left: 0px !important">
														</div>
													</div>
													
													<%}else{ %>
													<div class="progress" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
														<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
															Nil
														</div>
													</div>
													<%} %>
												</td>
											</tr> 
											
											<%  ProjectCount++;  } %>
											
										</tbody>
											
											
									</table>	
									
									<!-- ******************************************************** INDIVIDUAL PROJECT DETAILS ******************************************* -->
	
									<div class="card" style="background: white;display:none;" id="projectgraph">
										<div style="background-color: rgba(255, 255, 255, 0.39999) !important ;border-radius: 4px ;overflow-x:hidden ">
											<div  style="float: right;margin: 10px">
												<button class="btn btn-sm back" onclick="overalldetails('A')">Details</button>
												<br>
												<button class="btn btn-sm back" style="margin-top: 10px;padding: 3px 19px;" onclick="overalldoc()"><i class="fa fa-file-text-o" aria-hidden="true"></i> DOC </button>
											</div>
											
											<div class="row">
												<div class="col-md-4">
								 					<figure class="highcharts-figure">
														<div id="containerh"></div>
													</figure>
												</div>
												<div class="col-md-4">
								 					<figure class="highcharts-figure">
														<div id="containerh2"></div>
													</figure>
												</div>
												<div class="col-md-4">
								 					<figure class="highcharts-figure">
														<div id="containerh3"></div>
													</figure>
												</div>
												<!-- <div class="col-md-1">
													<button class="btn btn-sm back" style="float: right;margin: 10px" onclick="overalldetails('A')">Details</button>
													<button class="btn btn-sm back" style="float: right;margin: 10px" onclick="overalldetails('A')">DOC</button>
												</div> -->
											</div>
											<hr>
											<div class="row">
												<div class="col-md-4">
								 					<figure class="highcharts-figure">
														<div id="containerh4"></div>
													</figure>
												</div>
												<div class="col-md-4">
								 					<figure class="highcharts-figure">
														<div id="containerh5"></div>
													</figure>
												</div>
												<div class="col-md-4">
								 					<figure class="highcharts-figure">
														<div id="containerh6"></div>
													</figure>
												</div>
											</div>
											
											
										</div>
									</div>		
								
								<!-- ******************************************************** INDIVIDUAL PROJECT DETAILS END ******************************************* -->
			
				
								</div> 
								
								<%}	else{ %>
								
								
									<h2> No Project Details  </h2>
								
								<%} %>

	        	</div>
			</div>
			
			
			<div id="overalldivdg"  class="card" style="background: transparent;display:none" >
			
			<div class="card-header">
						<h3><%=ProjectHealthTotalData[45]!=null?StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[45].toString()): " - " %> - Cluster Details</h3>
						<a class="btn btn-info btn-sm back"   href="MainDashBoard.htm" style="float: right">Back</a>
				</div>
		
			<div  style="background-color: rgba(255, 255, 255, 0.39999) !important ;border-radius: 4px ;/* max-height: 26rem */; overflow-y:auto;overflow-x:hidden  ">
								
				<table class="table meeting tableFixHead fixed-table "  style="table-layout: fixed"> 
										<thead style=" background: #1363DF; color: white;">												
											<tr>
												<td style="width:4%">
													<a  data-toggle="modal"  class="fa faa-pulse animated " data-target="#exampleModal1" data-whatever="@mdo" style="padding: 0px 1.5rem;cursor:pointer" ><i class="fa fa-info-circle " style="font-size: 1.3rem;color: " aria-hidden="true"></i> </a>
												</td>
												<td style="width:5%"><span style="font-size :15px;font-weight: bold; ">Lab</span></td>
												<td class="p-0"><span class="span-font">PMRB </span></td>
												<td class="p-0"><span class="span-font">EB </span></td>
												<td ><span class="span-font">Milestone </span></td>
												<td ><span class="span-font">Action</span></td>
												<td ><span class="span-font">Risk</span></td>
												<td ><span class="span-font">Finance</span></td>
											</tr>
										</thead>
										
										<tbody>
	
											<%for(Object[] obj : projecthealthtotaldg) { %>
		
											<tr>
											
												<td><a href="javascript:LabDetails('<%=obj[45] %>')"> <i class="fa fa-hand-o-right" aria-hidden="true" style="color: purple;font-size: 1.3rem !important"></i></a></td>
												<td   style="font-weight: 800; font-size:0.75rem;text-align:left;"><%=obj[45] %>	</td>
												<td class="custom-td">
													<%if(Integer.parseInt(obj[2].toString())>0){ %>
														
														<div class="row">
															<div class="col-md-11">
															    <div class="progress"  >
																  <div class="progress-bar progress-bar-striped bg-success" onclick="overallmeetingredirectdg('<%=obj[2]%>','1', 'B')" style="width:<%=obj[29]%>%;" data-toggle="tooltip" title="PMRB Held : <%=obj[0]!=null?StringEscapeUtils.escapeHtml4(obj[0].toString()): " - "%> / <%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %>" ></div>
																  <div class="progress-bar progress-bar-striped bg-primary" onclick="overallmeetingredirectdg('<%=obj[2]%>','1', 'C' )" style="width:<%=obj[30]%>%;" data-toggle="tooltip" title="PMRB Pending : <%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "%> / <%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %>" ></div>
																</div>
														  	</div>
															<div class="col-md-1" style="padding-left: 0px !important">
																<span class="health-circle" style="<%if(Integer.parseInt(obj[30].toString())<=25){%> background-color:green <%}%>
																								   <%if( (Integer.parseInt(obj[30].toString())>25) && (Integer.parseInt(obj[30].toString())<=50)){%> background-color: #F8CB2E;color:black <%}%>
																								   <%if( (Integer.parseInt(obj[30].toString())>50) && (Integer.parseInt(obj[30].toString())<=75)){%> background-color: #EE5007 <%}%>
																								   <%if( (Integer.parseInt(obj[30].toString())>75) && (Integer.parseInt(obj[30].toString())<=100)){%> background-color:red <%}%>
																"><%=obj[30]!=null?StringEscapeUtils.escapeHtml4(obj[30].toString()): " - " %></span>
															</div>
											
														</div>
						
													<%}else{ %>
													<div class="progress" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
														<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
															Nil
														</div>
													</div>
													<%} %>
												</td>	
												<td class="custom-td">
													<%if(Integer.parseInt(obj[5].toString())>0){ %>
														
														<div class="row">
															<div class="col-md-11">
															    <div class="progress"  >
																  <div class="progress-bar progress-bar-striped bg-success" onclick="overallmeetingredirectdg('<%=obj[2]%>','1', 'B')" style="width:<%=obj[31]%>%;" data-toggle="tooltip" title="EB Held : <%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - "%> / <%=obj[5]!=null?StringEscapeUtils.escapeHtml4(obj[5].toString()): " - " %>" ></div>
																  <div class="progress-bar progress-bar-striped bg-primary" onclick="overallmeetingredirectdg('<%=obj[2]%>','1', 'C' )" style="width:<%=obj[32]%>%;" data-toggle="tooltip" title="EB Pending : <%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - "%> / <%=obj[5] !=null?StringEscapeUtils.escapeHtml4(obj[5].toString()): " - "%>" ></div>
																</div>
														  	</div>
															<div class="col-md-1" style="padding-left: 0px !important">
																<span class="health-circle" style="<%if(Integer.parseInt(obj[32].toString())<=25){%> background-color:green <%}%>
																								   <%if( (Integer.parseInt(obj[32].toString())>25) && (Integer.parseInt(obj[32].toString())<=50)){%> background-color: #F8CB2E;color:black <%}%>
																								   <%if( (Integer.parseInt(obj[32].toString())>50) && (Integer.parseInt(obj[32].toString())<=75)){%> background-color: #EE5007 <%}%>
																								   <%if( (Integer.parseInt(obj[32].toString())>75) && (Integer.parseInt(obj[32].toString())<=100)){%> background-color:red <%}%>
																"><%=obj[32]!=null?StringEscapeUtils.escapeHtml4(obj[32].toString()): " - " %></span>
															</div>
														</div>
						
													<%}else{ %>
													<div class="progress" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
														<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
															Nil
														</div>
													</div>
													<%} %>
												</td>
												<td class="custom-td">
													<%if(Integer.parseInt(obj[9].toString())>0){ %>
													<div class="row">
														<div class="col-md-11">
															<div class="progress" onclick="overallcommonredirectdg('mil','<%=obj[2]%>')">
															  <div class="progress-bar progress-bar-striped bg-success" style="width:<%=obj[10]%>%" data-toggle="tooltip" title="Completed : <%=obj[8]!=null?StringEscapeUtils.escapeHtml4(obj[8].toString()): " - "%> / <%=obj[9]!=null?StringEscapeUtils.escapeHtml4(obj[9].toString()): " - " %>" ></div>
															  <div class="progress-bar progress-bar-striped bg-warning" style="width:<%=obj[33]%>%" data-toggle="tooltip" title="Delayed : <%=obj[7]!=null?StringEscapeUtils.escapeHtml4(obj[7].toString()): " - "%> / <%=obj[9]!=null?StringEscapeUtils.escapeHtml4(obj[9].toString()): " - " %>" ></div>
															  <div class="progress-bar progress-bar-striped bg-danger" style="width:<%=obj[44]%>%" data-toggle="tooltip" title="Pending : <%=obj[6]!=null?StringEscapeUtils.escapeHtml4(obj[6].toString()): " - "%> / <%=obj[9]!=null?StringEscapeUtils.escapeHtml4(obj[9].toString()): " - " %>" ></div>															  
															</div>
														</div>
														<div class="col-md-1" style="padding-left: 0px !important">
																<span class="health-circle" style="<%if(Integer.parseInt(obj[44].toString())<=25){%> background-color:green <%}%>
																								   <%if( (Integer.parseInt(obj[44].toString())>25) && (Integer.parseInt(obj[44].toString())<=50)){%> background-color: #F8CB2E;color:black <%}%>
																								   <%if( (Integer.parseInt(obj[44].toString())>50) && (Integer.parseInt(obj[44].toString())<=75)){%> background-color: #EE5007 <%}%>
																								   <%if( (Integer.parseInt(obj[44].toString())>75) && (Integer.parseInt(obj[44].toString())<=100)){%> background-color:red <%}%>
																"><%=obj[44]!=null?StringEscapeUtils.escapeHtml4(obj[44].toString()): " - " %></span>
														</div>
													</div>
													<%}else{ %>
													<div class="progress" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
														<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
															Nil
														</div>
													</div>
													<%} %>
												</td>
												<td class="custom-td">
													<%if(Integer.parseInt(obj[15].toString())>0){ %>
													<div class="row">
														<div class="col-md-11">
															<div class="progress" onclick="overallcommonredirect('action','<%=obj[2]%>')">
															  <div class="progress-bar progress-bar-striped bg-success" style="width:<%=obj[37]%>%" data-toggle="tooltip" title="Completed : <%=obj[14]!=null?StringEscapeUtils.escapeHtml4(obj[14].toString()): " - "%> / <%=obj[15]!=null?StringEscapeUtils.escapeHtml4(obj[15].toString()): " - "%>" ></div>
															  <div class="progress-bar progress-bar-striped bg-warning" style="width:<%=obj[36]%>%" data-toggle="tooltip" title="Delayed : <%=obj[13]!=null?StringEscapeUtils.escapeHtml4(obj[13].toString()): " - "%> / <%=obj[15]!=null?StringEscapeUtils.escapeHtml4(obj[15].toString()): " - "%>" ></div>
															  <div class="progress-bar progress-bar-striped bg-primary" style="width:<%=obj[35]%>%" data-toggle="tooltip" title="Forwarded : <%=obj[12]!=null?StringEscapeUtils.escapeHtml4(obj[12].toString()): " - "%> / <%=obj[15]!=null?StringEscapeUtils.escapeHtml4(obj[15].toString()): " - "%>" ></div>
															  <div class="progress-bar progress-bar-striped bg-danger" style="width:<%=obj[34]%>%" data-toggle="tooltip" title="Pending : <%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - "%> / <%=obj[15]!=null?StringEscapeUtils.escapeHtml4(obj[15].toString()): " - "%>" ></div>
															</div>
														</div>
														<div class="col-md-1" style="padding-left: 0px !important">
															<span class="health-circle" style="<%if(Integer.parseInt(obj[34].toString())<=25){%> background-color:green <%}%>
																									   <%if( (Integer.parseInt(obj[34].toString())>25) && (Integer.parseInt(obj[34].toString())<=50)){%> background-color: #F8CB2E;color:black <%}%>
																									   <%if( (Integer.parseInt(obj[34].toString())>50) && (Integer.parseInt(obj[34].toString())<=75)){%> background-color: #EE5007 <%}%>
																									   <%if( (Integer.parseInt(obj[34].toString())>75) && (Integer.parseInt(obj[34].toString())<=100)){%> background-color:red <%}%>
															"><%=obj[34]!=null?StringEscapeUtils.escapeHtml4(obj[34].toString()): " - " %></span>
														</div>
													</div>
													<%}else{ %>
													<div class="progress" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
														<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
															Nil
														</div>
													</div>
													<%} %>
												</td> 
												<td class="custom-td">
													<%if(Integer.parseInt(obj[18].toString())>0){ %>
													<div class="row">
														<div class="col-md-11">
															<div class="progress" onclick="overallcommonredirect('risk','<%=obj[2]%>')">
															  <div class="progress-bar progress-bar-striped bg-success" style="width:<%=obj[39]%>%" data-toggle="tooltip" title="Completed : <%=obj[16]!=null?StringEscapeUtils.escapeHtml4(obj[16].toString()): " - "%> / <%=obj[18]!=null?StringEscapeUtils.escapeHtml4(obj[18].toString()): " - " %>" ></div>
															  <div class="progress-bar progress-bar-striped bg-danger" style="width:<%=obj[38]%>%" data-toggle="tooltip" title="Pending : <%=obj[17]!=null?StringEscapeUtils.escapeHtml4(obj[17].toString()): " - "%> / <%=obj[18]!=null?StringEscapeUtils.escapeHtml4(obj[18].toString()): " - " %>" ></div>
															</div>
														</div>
														<div class="col-md-1" style="padding-left: 0px !important">
																<span class="health-circle" style="<%if(Integer.parseInt(obj[38].toString())<=25){%> background-color:green <%}%>
																										   <%if( (Integer.parseInt(obj[38].toString())>25) && (Integer.parseInt(obj[38].toString())<=50)){%> background-color: #F8CB2E;color:black <%}%>
																										   <%if( (Integer.parseInt(obj[38].toString())>50) && (Integer.parseInt(obj[38].toString())<=75)){%> background-color: #EE5007 <%}%>
																										   <%if( (Integer.parseInt(obj[38].toString())>75) && (Integer.parseInt(obj[38].toString())<=100)){%> background-color:red <%}%>
																"><%=obj[38]!=null?StringEscapeUtils.escapeHtml4(obj[38].toString()): " - " %></span>
														</div>
													</div>
													<%}else{ %>
													<div class="progress" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
														<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
															Nil
														</div>
													</div>
													<%} %>
												</td>
												<td class="custom-td">
													<%
													BigInteger number = new BigInteger(obj[23].toString());
													if(number.compareTo(new BigInteger("0")) >0){ %>
													<div class="row">
														<div class="col-md-10">
															<div class="progress" onclick="overallfinance()">
															  <div class="progress-bar progress-bar-striped bg-success" style="width:<%=obj[40]%>%" data-toggle="tooltip" title="Expenditure : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[19].toString() ))))%>" ></div>
															  <div class="progress-bar progress-bar-striped bg-warning" style="width:<%=obj[41]%>%" data-toggle="tooltip" title="OC : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[20].toString() ))))%> " ></div>
															  <div class="progress-bar progress-bar-striped bg-primary" style="width:<%=obj[42]%>%" data-toggle="tooltip" title="DIPL : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[21].toString() ))))%> " ></div>
															  <div class="progress-bar progress-bar-striped bg-danger" style="width:<%=obj[43]%>%" data-toggle="tooltip" title="Balance : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[22].toString() ))))%> " ></div>
															</div>
														</div>
														<div class="col-md-1" style="padding-left: 0px !important">
																	<span class="health-circle" style="<%if(Integer.parseInt(obj[40].toString())<=25){%> background-color:green <%}%>
																											   <%if( (Integer.parseInt(obj[40].toString())>25) && (Integer.parseInt(obj[40].toString())<=50)){%> background-color: #F8CB2E;color:black <%}%>
																											   <%if( (Integer.parseInt(obj[40].toString())>50) && (Integer.parseInt(obj[40].toString())<=75)){%> background-color: #EE5007 <%}%>
																											   <%if( (Integer.parseInt(obj[40].toString())>75) && (Integer.parseInt(obj[40].toString())<=100)){%> background-color:red <%}%>
																	"><%=obj[40]!=null?StringEscapeUtils.escapeHtml4(obj[40].toString()): " - " %></span>
														</div>
														<div class="col-md-1" style="padding-left: 0px !important">
														</div>
													</div>
													
													<%}else{ %>
													<div class="progress" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
														<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
															Nil
														</div>
													</div>
													<%} %>
												</td> 
												
												
											</tr>
											
											
											<%} %>
											
											
										</tbody>
											
											
									</table>	
									
												
				
								</div> 
							
							</div> 
			
			
			
			

		</div> 
	
	</div>	
</div>
	
<!-- ************************Informative Modal Start********************* -->

	<div class="modal fade " id="exampleModal1" tabindex="-1" aria-labelledby="exampleModalLabel1" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				      
				<div class="modal-header ">
					<h5 class="modal-title" id="exampleModalLabel" style="color:#145374">Colour Coding Summary</h5>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
			        </button>
			  	</div>
					      
				<div class="modal-body">
						
					<div class="row">
						<div style="text-align: left">
								<p style="margin-bottom: 0px !important;margin-left:10px">Project </p>
								<hr class="modal-hr">
								<ul class="modal-list">
						          	<li><span class="modal-span" class="text-success">&#8226;</span><span class="modal-text">Indian Army</span></li>
						           	<li><span class="modal-span" style="color:#007bff">&#8226;</span><span class="modal-text">Indian Navy</span></li>
						           	<li><span class="modal-span" style="color:#1F4690">&#8226;</span><span class="modal-text">Indian Air Forces</span></li>
						           	<li><span class="modal-span" style="color:#8E3200">&#8226;</span><span class="modal-text">Home Land Security</span></li>
						           	<li><span class="modal-span" style="color:#231955">&#8226;</span><span class="modal-text">DRDO</span></li>
						           	<li><span class="modal-span" style="color:#EE5007">&#8226;</span><span class="modal-text">Others</span></li>
					            </ul>
							</div>
					</div>	
					<div class="row">
						<div style="text-align: left">
								<p style="margin-bottom: 0px !important;margin-left:10px">Project Health (Pending %)</p>
								<hr class="modal-hr">
								<ul class="modal-list">
						          	<li><span class="modal-span" class="text-success">&#8226;</span><span class="modal-text">0-25%</span></li>
						           	<li><span class="modal-span" style="color:#F8CB2E;">&#8226;</span><span class="modal-text">25-50%</span></li>
						           	<li><span class="modal-span" style="color:#EE5007">&#8226;</span><span class="modal-text">50-75%</span></li>
						           	<li><span class="modal-span" style="color:red">&#8226;</span><span class="modal-text">75-100%</span></li>
					            </ul>
							</div>
					</div>		

					<div class="row">
						<div style="text-align: left">
								<p style="margin-bottom: 0px !important;margin-left:10px">Milestone</p>
								<hr class="modal-hr">
								<ul class="modal-list">
									<li><span class="modal-span green" >&#8226;</span><span class="modal-text">Completed</span></li>
									<li><span class="modal-span yellow" >&#8226;</span><span class="modal-text">Delayed</span></li>
						          	<li><span class="modal-span red" >&#8226;</span><span class="modal-text">Pending</span></li>
					            </ul>
							</div>
					</div>		
					
					<div class="row">
						<div style="text-align: left">
								<p style="margin-bottom: 0px !important;margin-left:10px">Action</p>
								<hr class="modal-hr">
								<ul class="modal-list">
						           	<li><span class="modal-span green" >&#8226;</span><span class="modal-text">Completed</span></li>
						           	<li><span class="modal-span yellow" >&#8226;</span><span class="modal-text">Delayed</span></li>
						           	<li><span class="modal-span blue" >&#8226;</span><span class="modal-text">Forwarded</span></li>
						           	<li><span class="modal-span red" >&#8226;</span><span class="modal-text">Pending</span></li>
					            </ul>
							</div>
					</div>
					
					<div class="row">
						<div style="text-align: left">
								<p style="margin-bottom: 0px !important;margin-left:10px">Risks</p>
								<hr class="modal-hr">
								<ul class="modal-list">
									<li><span class="modal-span green" >&#8226;</span><span class="modal-text">Completed</span></li>
									<li><span class="modal-span yellow" >&#8226;</span><span class="modal-text">Delayed</span></li>
						          	<li><span class="modal-span red" >&#8226;</span><span class="modal-text">Pending</span></li>
					            </ul>
							</div>
					</div>
					
					<div class="row">
						<div style="text-align: left">
								<p style="margin-bottom: 0px !important;margin-left:10px">Finance</p>
								<hr class="modal-hr">
								<ul class="modal-list">
						          	<li><span class="modal-span green" >&#8226;</span><span class="modal-text">Expenditure</span></li>
						           	<li><span class="modal-span yellow" >&#8226;</span><span class="modal-text">O/S</span></li>
						           	<li><span class="modal-span blue" >&#8226;</span><span class="modal-text">DIPL</span></li>
						           	<li><span class="modal-span red" >&#8226;</span><span class="modal-text">Balance</span></li>
					            </ul>
							</div>
					</div>
				
					          
				</div>
				
					      
			</div>
		</div>
	</div>

<!-- ************************Informative Modal End********************* -->




<script>

$(document).ready(function(){
	
	var logintype = '<%=logintype%>';
	if(logintype=='K')
		$('#overalldivdg').css('display','block');
	else
		$('#projectdetailsview').css('display','block');
	
	
	
})



$('.progress-bar[data-toggle="tooltip"]').tooltip({
    animated: 'fade',
    placement: 'bottom'
});

function ProjectDetails(value){
	
	$("#overalldiv").css("display","none");
	$('#projectgraph').css("display","block");
	
	$('#projectiddoc').val(value);
	$('#projectidchanges').val(value);
	charts(value);
	
}

function overalldetails(value){
	
	$("#overalldiv").css("display","block");
	$('#projectgraph').css("display","none");
	$('#projectidchanges').val("A");
	
	/* individualprojectdetails(value); */
	charts(value);
	
}


function charts(value){
	
	$projectid=value;
		
		$
			
			.ajax({
			
				type:"GET",
				url:"IndividualProjectDetails.htm",
				data : {
					
					ProjectId : $projectid
					
				},
				datatype : 'json',
				success : function(result) {

					var result = JSON.parse(result);
					var values = Object.values(result).map(function(key, value) {
						  return result[key,value]
						});
				
					
					/* logic to print the project wise data in card */	
					
					document.getElementById('financevalue').innerHTML = values[23].toLocaleString('en-IN');
					document.getElementById('risksvalue').innerHTML = values[16] + ' / ' + values[18];
					document.getElementById('actionvalue').innerHTML = values[14] + ' / ' + values[15];
					document.getElementById('milestonevalue').innerHTML = values[8] + ' / ' + values[9];
					
					if(values[10]!=null)
					document.getElementById('milestonepercentage').innerHTML = values[10] + '%';
					else
					document.getElementById('milestonepercentage').innerHTML = 'Nil'; 
					
					
					
					var s=  values[0] + '/' + values[2]
					document.getElementById('meetingsvaluepmrc').innerHTML = s;
					var t=  values[3] + '/' + values[5]
					document.getElementById('meetingsvalueeb').innerHTML = t;
					
					<%-- if(values[24]!='A')
					document.getElementById('projecttitle').innerHTML = 'Project : ' + values[25];
					else
					document.getElementById('projecttitle').innerHTML = 'Project Count : ' + <%=ProjectCount%>;	
					document.getElementById('projecttitle').innerHTML = 'PROJECT HEALTH (' + <%=ProjectCount%> + ')';	 --%>
					
					
					
					<!-- ******************************************* Project Details Graph Script ******************************************************* -->
					
					Highcharts.chart('containerh', {
					    chart: {
					        type: 'bar',
					        height: (12 / 16 * 100) + '%' 
					    },
					    exporting: {
					        buttons: {
					          contextButton: {
					            menuItems: [
					              'printChart',
					              'downloadPNG',
					              'downloadJPEG',
					              'downloadPDF',
					              'downloadCSV',
					              'downloadXLS',
					              'viewData'
					            ]
					          }
					        }

					      },
					    title: {
					        text: 'Meeting Data',
					       	style : {
							        	fontWeight: 'bold'
							}
					    },
					    subtitle: {
					        text: ''
					    },
					    xAxis: {
					        categories: ['PMRB', 'EB'],
					        title: {
					            text: null
					        },
					        labels: {
					            overflow: 'justify',
					            style : {
					            	fontWeight: 'bold',
						        	
								}
					        }
					    },
					    yAxis: {
					        min: 0,
					        title: {
					            text: 'Meeting (count)',
					            align: 'high'
					        },
					        labels: {
					            overflow: 'justify',
					            style : {
					            	fontWeight: 'bold',
						        	
								}
					        }
					    },
					    tooltip: {
					        valueSuffix: ''
					    },
					    plotOptions: {
					        bar: {
					            dataLabels: {
					                enabled: true
					            }
					        }
					    },
					    colors: [
					    	'#28a745',
					        '#dc3545',
					        
					    ],
					    credits: {
					        enabled: false
					    },
					    series: meetingdata(),
					 
					    
					});		

					function meetingdata(){
						
						let meetingdata=[];
						if(parseInt(values[2])>0 || parseInt(values[5])>0){
							meetingdata=[
								{
							        name: 'Held',
							        data: [parseInt(values[0]), parseInt(values[3]),]
							    }, {
							        name: 'Pending',
							        data: [parseInt(values[1]), parseInt(values[4]),]
							    },
								
							]
						}
						return meetingdata;
						
					}
					
					/********************************************** Milestone ****************************************************/

					Highcharts.chart('containerh2', {
					    chart: {
					        type: 'pie',
					        options3d: {
					            enabled: true,
					            alpha: 45
					        },
					        height: (12 / 16 * 100) + '%' 
					    },
					    exporting: {
					        buttons: {
					          contextButton: {
					            menuItems: [
					              'printChart',
					              'downloadPNG',
					              'downloadJPEG',
					              'downloadPDF',
					              'downloadCSV',
					              'downloadXLS',
					              'viewData'
					            ]
					          }
					        }

					      },
					    title: {
					        text: 'Milestone',
					        style : {
					        	fontWeight: 'bold'
					        }
					    },
					    subtitle: {
					        text: ''
					    },
					    plotOptions: {
					        pie: {
					            innerSize: 100,
					            depth: 15
					        }
					    },
					    colors: [
					        '#ffc107',
					        '#dc3545',
					        '#28a745'
					    ],
					    series: [{
					        name: 'Milestone',
					        data: miletsonedata(),
					    
					    }],
					    credits: {
					        enabled: false
					    },
					});
					
					function miletsonedata(){
						let miletsonedata=[];
						
						if(parseInt(values[9])>0){
							miletsonedata=[ 
								['Pending', parseInt(values[6])],
					            ['Delayed', parseInt(values[7])],
					            ['Completed', parseInt(values[8])],
					            ]
						}
						
						return miletsonedata;
					}

					/********************************************** Action ****************************************************/

					Highcharts.chart('containerh3', {
					    chart: {
					        type: 'pie',
					        options3d: {
					            enabled: true,
					            alpha: 45
					        },
					        height: (12 / 16 * 100) + '%' 
					    },
					    exporting: {
					        buttons: {
					          contextButton: {
					            menuItems: [
					              'printChart',
					              'downloadPNG',
					              'downloadJPEG',
					              'downloadPDF',
					              'downloadCSV',
					              'downloadXLS',
					              'viewData'
					            ]
					          }
					        }

					      },
					    title: {
					        text: 'Action',
					        style : {
					        	fontWeight: 'bold'
					        }
					    },
					    subtitle: {
					        text: ''
					    },
					    plotOptions: {
					        pie: {
					            innerSize: 100,
					            depth: 45
					        }
					    },
					    colors: [
					        '#ffc107',
					        '#dc3545',
					        '#28a745',
					        '#007bff'
					    ],
					    series: [{
					        name: 'Action',
					        data: actiondata(),
					    }],
					    credits: {
					        enabled: false
					    },
					});
					
					function actiondata(){
						let actiondata=[];
						
						if(parseInt(values[15])>0){
							actiondata=[
								 	['Pending', parseInt(values[11])],
						            ['Delayed', parseInt(values[13])],
						            ['Completed', parseInt(values[14])],
						            ['Forwarded', parseInt(values[12])],
								
							]
						}
						
						return actiondata;
					}
					
					/*************************************************************** Risk *****************************************************  */

					Highcharts.chart('containerh4', {
					    chart: {
					        type: 'column',
					        height: (12 / 16 * 100) + '%' 
					    },
					    exporting: {
					        buttons: {
					          contextButton: {
					            menuItems: [
					              'printChart',
					              'downloadPNG',
					              'downloadJPEG',
					              'downloadPDF',
					              'downloadCSV',
					              'downloadXLS',
					              'viewData'
					            ]
					          }
					        }

					      },
					    title: {
					        text: 'Risks',
					        style : {
					        	fontWeight: 'bold'
					        }
					    },
					    subtitle: {
					        text: ''
					    },
					    xAxis: {
					        categories: [
					            'Risk',
					        ],
					        crosshair: true
					    },
					    yAxis: {
					        min: 0,
					        title: {
					            text: 'Count'
					        }
					    },
					    tooltip: {
					        headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
					        pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
					            '<td style="padding:0"><b>{point.y:.1f} </b></td></tr>',
					        footerFormat: '</table>',
					        shared: true,
					        useHTML: true
					    },
					    plotOptions: {
					        column: {
					            pointPadding: 0.2,
					            borderWidth: 0
					        }
					    },
					    colors: [
					    	'#28a745',
					        '#dc3545',
					        
					    ],
					    series: riskdata(),
					    credits: {
					        enabled: false
					    },
					});
					
					function riskdata(){
						let riskdata=[];
						if(parseInt(values[18])>0){
							riskdata=[
								{
							        name: 'Completed',
							        data: [parseInt(values[16])]

							    },
								{
							        name: 'Pending',
							        data: [parseInt(values[17])]

							    }, 
							]
							
							
						}
						return riskdata;
						
					}


					/**************************************** Finance ******************************************* */
					
					$(function () {
					
					var TotalFinanceValue =0;
					if(parseInt(values[23])>0){
						 TotalFinanceValue = 'Total Finance Value : ' + parseInt(values[23]).toLocaleString('en-IN', {
						    maximumFractionDigits: 0,
						    style: 'currency',
						    currency: 'INR'
						});	
					}
					
						
					 Highcharts.setOptions({
				      lang: {
				        thousandsSep: ','
				      }
					});
					
					Highcharts.chart('containerh5', {
					    chart: {
					        type: 'pie',
					        height: (12 / 16 * 100) + '%' 
					    },
					    exporting: {
					        buttons: {
					          contextButton: {
					            menuItems: [
					              'printChart',
					              'downloadPNG',
					              'downloadJPEG',
					              'downloadPDF',
					              'downloadCSV',
					              'downloadXLS',
					              'viewData'
					            ]
					          }
					        }

					      },
					    title: {
					        text: 'Finance',
					        style : {
					        	fontWeight: 'bold'
					        }
					    },
					    subtitle: {
					        text: TotalFinanceValue ,
					        style : {
					        	fontWeight: 'bold'
					        }
					    },

					    accessibility: {
					        announceNewData: {
					            enabled: true
					        },
					        point: {
					            valueSuffix: ''
					        }
					    },

					    plotOptions: {
					        series: {
					            dataLabels: {
					                enabled: true,
					                 formatter: function() {
					                    return '<p>&#8377;</p>'+ Highcharts.numberFormat(this.y, 0);
					                } 
					                /* format: '{point.name}: {point.y:.1f}' */
					            }
					        }
					    },
					    
					    
					    
					    colors: [
					        '#ffc107',
					        '#28a745',
					        '#dc3545',
					        '#007bff'
					    ],
					    tooltip: {
					        headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
					        pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>&#8377; {point.y:.2f}</b><br/>'
					    },
					    
					    series: [
					    	{
					            name: "Finance",
					            colorByPoint: true,
					           
					            data:   generatedata(),
			
					        } 
					       
					    ],
					    
					    credits: {
					        enabled: false
					    },

					},

			);
					
					});
				  
			function  generatedata(){
				
				let financedata=[];
				
				if(parseInt(values[23])>0){
					
					financedata = [
						
						{
		                    name: "O/S",
		                    y: parseInt(values[20]),
		                    drilldown: "O/S"
		                },
		                {
		                    name: "Expenditure",
		                    y: parseInt(values[19]),
		                    drilldown: "Expenditure"
		                },
		                {
		                    name: "Balance",
		                    y: parseInt(values[22]),
		                    drilldown: "Balance"
		                },
		                {
		                    name: "DIPL",
		                    y: parseInt(values[21]),
		                    drilldown: "DIPL"
		                }, 
						
					]
					
				}
				
				return financedata;
			}			
					
			
			/**************************************** Document  ******************************************* */
			

				Highcharts.chart('containerh6', {
				    chart: {
				        type: 'variablepie',
				        height: (12 / 16 * 100) + '%' 
				    },
				    title: {
				        text: 'Document Statistics',
				        style : {
				        	fontWeight: 'bold'
				        }
				    },
				    colors: [
				        '#ffc107',
				        '#28a745',
				        '#dc3545',
				        '#007bff'
				    ],
				    tooltip: {
				        headerFormat: '',
				        pointFormat: '<span style="color:{point.color}">\u25CF</span> <b> {point.name}</b><br/>' +
				            'Area (square km): <b>{point.y}</b><br/>' +
				            'Population density (people per square km): <b>{point.z}</b><br/>'
				    },
				    series: [{
				        minPointSize: 10,
				        innerSize: '20%',
				        zMin: 0,
				        name: 'countries',
				        data: [{
				            name: 'Prepared',
				            y: 505370,
				            z: 92.9
				        }, {
				            name: 'Approved',
				            y: 551500,
				            z: 118.7
				        }, {
				            name: 'Pending',
				            y: 312685,
				            z: 124.6
				        }, {
				            name: 'Reviewed',
				            y: 78867,
				            z: 137.5
				        }, ]
				    }],
				    
				    credits: {
				        enabled: false
				    },
				    
				    
				    
				});


			}
			
			})
			
					
		


	}



</script>









</body>
</html>