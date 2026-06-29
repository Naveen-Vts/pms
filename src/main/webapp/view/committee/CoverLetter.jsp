<%@page import="com.vts.pfms.committee.model.Committee"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Cover Letter</title>
<style>
	
</style>
<%
	String lablogo=(String)request.getAttribute("lablogo");
	String refNo = null;
	String labcode = (String)session.getAttribute("labcode");
	
	List<Object[]> projectMeetingList = (List<Object[]>) request.getAttribute("projectMeetingList");
	Committee committee = (Committee) request.getAttribute("committee");
	
	String committeeCode = committee!=null ? committee.getCommitteeShortName() : "";
	if(labcode.equalsIgnoreCase("PGAD")) refNo = "PGAD/PPLG/7826/"+committeeCode.toUpperCase();


%>
</head>
<body>

<div align="center">
	<figure><img style="width: 3.5cm; height: 3.5cm"  src="data:image/png;base64,<%=lablogo%>"></figure>
	<table style="margin-top:10px; margin-bottom:10px; margin-left:15px; max-width:650px; font-size:16px;">
	    <tbody>
	        <tr>
	            <td style="text-align:left; width:325px; padding-top:15px; font-size:20px;">
	                Ref No: <%= refNo %>
	            </td>
	            <td style="text-align:right; width:215px; font-size:20px;">
	                dt:
	            </td>
	        </tr>
	        <tr>
	            <td colspan="2" style="text-align:left; padding-top:15px; font-size:20px;">
	                Sub: <%=committeeCode %> Meeting held on Meeting Date at Meeting Hall.
	            </td>
	        </tr>
	        <%-- <tr>
	            <td style="text-align:left; width:325px; padding-top:15px; font-size:20px;">
	                Ref No: <%= refNo %>
	            </td>
	            <td style="text-align:right; width:215px; font-size:20px;">
	                dt:
	            </td>
	        </tr> --%>
	    </tbody>
	</table>
</div>

<p style="text-align: left;margin-left:15px; font-size:16px;width:750px;">
	Reference is made to top  <%=committeeCode %> Meeting held on Meeting Date at Venue for the following Projects
</p>
<p style="text-align: left;margin-left:15px; font-size:16px;width:750px;">
	The Approved Minutes of Meeting has been sent through Drona mail for your kind perusal.
</p>

<table>
	<tbody>
		<tr>
			<td style="text-align: center; width:300px; padding-top:15px; font-size:20px;font-weight: bold;">
				Project
			</td>
			<td style="text-align: center; width:300px; padding-top:15px; font-size:20px;font-weight: bold;">
				 <%=committeeCode %> Meeting Count
			</td>	
		</tr>
		<%
		if(projectMeetingList!=null && !projectMeetingList.isEmpty()){
			for(Object[] obj : projectMeetingList){ %>
				<tr>
					<td style="text-align: center; width:300px; padding-top:15px; font-size:20px;">
						<%= obj[0]!=null ? obj[0].toString(): " - " %> <%= obj[1]!=null ? "("+obj[1].toString()+")": " - "%>
						<%--  <%= obj[2]!=null ? obj[2].toString(): " - "%> --%> 
					</td>
					<td style="text-align: center; width:300px; padding-top:15px; font-size:20px;">
						<%= obj[4]!=null ? "#"+obj[4].toString(): " - "%>
					</td>
				</tr>
			<%}
		}else{
			%>
			<tr>
				<td colspan="2" style="text-align: center; width:600px; padding-top:15px; font-size:20px;">
					NIL
				</td>
				
			</tr>
			<%
		}%>
	</tbody>
</table>

<div align="right" style="padding-right: 0rem;padding-bottom: 2rem; maring-top:10px;">
	<br>
		N.Ramesh Sc F
	 <br>
	 (Director of Planning)
</div>
<div align="left">
	<span style="font-size: 16px;font-weight: bold;">To</span>
	<p style="margin-bottom:0px; margin-top:5px;">Members -  <%=committeeCode %></p>
	<p style="margin-bottom:0px; margin-top:5px;">Chairperson -  <%=committeeCode %></p>
</div>
</body>
</html>