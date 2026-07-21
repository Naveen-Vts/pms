<%@page import="com.vts.pfms.model.BriefingHeading"%>
<%@page import="java.util.List"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<jsp:include page="../static/header.jsp"></jsp:include>
<title>Copy Heading From Last Meeting</title>

<%

SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");

Object[] committeescheduleeditdata=(Object[])request.getAttribute("committeescheduleeditdata");
Object[] committeescheduledata1=(Object[])request.getAttribute("committeescheduledata1");
String scheduleidto=committeescheduleeditdata[6].toString();
String meetingid=(String)request.getAttribute("meetingid");
String projectid = (String) request.getAttribute("projectid"); 
String committeeid = (String) request.getAttribute("committeeid"); 
String scheduleidfrom = (String) request.getAttribute("scheduleidfrom"); 
List<BriefingHeading> headingList=  (List<BriefingHeading>)request.getAttribute("headingList");
List<Object[]> meetingsearch=(List<Object[]>)request.getAttribute("meetingsearch");

%>
</head>
<body>

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
	<div class="mb-20px"> 
  
		<div id="error"></div>
		
    		<div class="card">
    	
    		<form action="BriefingPaperV2.htm" name="myfrm" id="myfrm" method="post">    	
	    		<div class="card-header cardHeaderBgColor">
      				<h6 class="header6Style" align="left"><%=committeescheduleeditdata[7]!=null?StringEscapeUtils.escapeHtml4(committeescheduleeditdata[7].toString()): " - "  %> <span> (Meeting Date and Time :      				
	      				 &nbsp;<%=committeescheduleeditdata[2]!=null?sdf.format(sdf1.parse(committeescheduleeditdata[2].toString())): " - " %> - <%=committeescheduleeditdata[3]!=null?StringEscapeUtils.escapeHtml4(committeescheduleeditdata[3].toString()): " - "  %>) </span> 					 
	      				<input type="submit" class="btn  btn-sm back float-right" value="BACK" />
	      				<span class="float-right meetingIdMargin"> (Meeting Id : <%=committeescheduleeditdata[11]!=null?StringEscapeUtils.escapeHtml4(committeescheduleeditdata[11].toString()): " - "  %>) </span> 
	      				<input type="hidden" name="scheduleid" value="<%=committeescheduleeditdata[6] %>">
						<input type="hidden" name="projectid" value="<%=projectid%>"/>
						<input type="hidden" name="committeeid" value="<%=committeeid%>"/>	
	      				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"  />      				
      				 </h6>  
      			</div>
      		</form>		
      		
	      		<div class="card-body">      		
	      			<div class="row">		
	      				<div class="col-md-4">
	      					<form action="CopyHeadingsFromLastMeeting.htm" method="post">
	      						<table>
	      							<tr>
	      								<td><input type="text" class="form-control item_name child" name="search" placeholder="Meeting Id" /></td>
	      								<td><button type="submit" class="btn btn-sm submit searchBtnMl">SEARCH</button></td>
	      							</tr>
	      						</table>
	      						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"  />
	      						<input type="hidden" name="scheduleidto" value="<%=scheduleidto %>"  />			
	      						<input type="hidden" name="scheduleid" value="<%=scheduleidto %>"  />			
								<input type="hidden" name="projectid" value="<%=projectid%>"/>
								<input type="hidden" name="committeeid" value="<%=committeeid%>"/>	
	      					</form>	      				
	      				</div>
	      				<div class="col-md-5"><%if(committeescheduledata1!=null){ %> Headings From Meeting Id : &nbsp;<%=committeescheduledata1[11]!=null?StringEscapeUtils.escapeHtml4(committeescheduleeditdata[11].toString()): " - "  %> <%} %> </div>
	      				<div class="col-md-3"><%if(committeescheduledata1!=null){ %>Date : &nbsp;<%=committeescheduleeditdata[2]!=null?sdf.format(sdf1.parse(committeescheduledata1[2].toString())): " - "  %> &nbsp;&nbsp;&nbsp; Time :&nbsp;<%=committeescheduledata1[3]!=null?StringEscapeUtils.escapeHtml4(committeescheduleeditdata[3].toString()): " - "  %> <%} %></div>	      				
	      			</div>
<!--   --------------------------------------------------------------------------------------------------- -->	
			<% if(meetingsearch!=null&&meetingsearch.size()>0)	{      			%>
					<div align="center">
				  		<h5>Select Meeting</h5>
					</div>
				
	      			<form action="CopyHeadingsFromLastMeeting.htm" method="post">  
			      			<table id="mydatatable" data-toggle="table" data-pagination="true" data-search="true">							
								<thead>
									<tr>
										<th>Select</th>
										<th>Meeting Id</th>
										<th>Date </th>
										<th> Time</th>
										<th>Committee</th>																							
										<th >Venue</th>					 	
										<!-- <th >Role</th> -->
									</tr>
								</thead>
							<tbody>
								<% 	for (Object[] obj :meetingsearch) { %>
										<tr>
											<td align="center"><input type="radio" name="scheduleidfrom" value="<%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): "" %>" / ></td>
											<td><%=obj[7]!=null?StringEscapeUtils.escapeHtml4(obj[7].toString()): " - " %></td>
											<td><%=obj[3]!=null?sdf.format(obj[3]): " - " %> </td>
											<td> <%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - " %></td>
											<td> <%=obj[9]!=null?StringEscapeUtils.escapeHtml4(obj[9].toString()): " - "%></td>
											<td><%if(obj[10]!=null){%> <%=StringEscapeUtils.escapeHtml4(obj[10].toString())%> <%}else{ %> &nbsp;&nbsp;&nbsp;&nbsp;- <%} %></td>																	
										</tr>
							   <% }%>
	 					</tbody>
					</table>	
					
					<div align="center">
				           	<input type="submit"  class="btn  btn-sm submit" value="SUBMIT" />				            	
					</div>
					<%-- <input type="hidden" name="meetingid" value="<%=obj[7] %>"  />	 --%>
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"  />						
					<input type="hidden" name="scheduleidto" value="<%=scheduleidto %>"  />
					<input type="hidden" name="scheduleid" value="<%=scheduleidto %>"  />			
					<input type="hidden" name="projectid" value="<%=projectid%>"/>
					<input type="hidden" name="committeeid" value="<%=committeeid%>"/>	
				</form>
				<%} %>
	    <!--  --------------------------------------------------------------------------------------------- -->   
			<%if(committeescheduledata1!=null && (headingList==null || headingList.size()==0 ) ){ %>
				 <div align="center" class="mt-25px"> <h6>No Agenda is Defined For This Meeting !</h6> </div>
			<%} %>	    
	    <!--  --------------------------------------------------------------------------------------------- -->
	    
	      <%if(headingList!=null && headingList.size()>0 ){ %>
	      		<div align="center">
				  	<h5>Select Headings</h5>
				</div>
	      			<form method="post" action="CopyHeadingsList.htm" enctype="multipart/form-data" id="addagendafrm" name="addagendafrm">	        
	        			<div >	<span class="text-primary float-right fs-15px">Duration in Minutes</span></div>
	          				<table class="table  table-bordered table-hover table-striped table-condensed  info shadow-nohover mt-30px" id="myTable20">
								<thead>  
									<tr id="">
										<th align="center" width="25px">Select</th>
										<!-- <th align="center">Seniority No</th> -->
										<th align="center">Headings</th>
										<!-- <th>Attachment</th>	 -->								
									</tr>								
									<%for(BriefingHeading heading : headingList){ %>
									<tr>								
										<td align="center" >
										<input type="checkbox" name="headingId" id="headingId" value="<%= heading.getHeadingId() %>" />
										 </td>
										<%-- <td ><%=heading.getSeniority() %> </td>			 --%>										
										<td ><%=heading.getHeading() %> </td>														
									</tr>
									<%} %>
								</thead>
							</table>

	          				<div align="center">
				            	<button type="submit"  class="btn  btn-sm submit" onclick="return checkagendaselect();">SUBMIT</button>				            	
	          				</div>
			        	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"  />
			        	<input type="hidden" name="scheduleidfrom" value="<%=scheduleidfrom %>"  />
			     		<input type="hidden" name="scheduleidto" value="<%=scheduleidto %>"  />	
						<input type="hidden" name="projectid" value="<%=projectid%>"/>
						<input type="hidden" name="committeeid" value="<%=committeeid%>"/>	
			         	
			    
	      			</form>
	      			
	      		<%} %>
	    		</div>
    		</div>
    				
    		
   	</div>   
   	
 
</div>
    

</body>
</html>