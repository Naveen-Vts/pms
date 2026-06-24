<%@page import="com.vts.pfms.FormatConverter"%>
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
body{
	 font-family: Arial, Helvetica, sans-serif!important;
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.left, .right {
  width: 30%;
  text-align:left;
}

.center {
  width: 40%;
  text-align: center;
}

.center img {
  width: 3.5cm;
  height: 3.5cm;
  object-fit: contain;
}

.left p,
.right p {
  margin: 0;
  line-height: 1.4;
  font-weight: bold;
}
</style>
<%
	String lablogo=(String)request.getAttribute("lablogo");
	String refNo = null;
	String labcode = (String)session.getAttribute("labcode");
	//labcode = "PGAD";
	FormatConverter sdf = new FormatConverter();
	
	List<Object[]> projectMeetingList = (List<Object[]>) request.getAttribute("projectMeetingList");
	Committee committee = (Committee) request.getAttribute("committee");
	Object[] lastmeetingCreated = (Object[]) request.getAttribute("lastmeetingCreated");
	String committeeCode = committee!=null ? committee.getCommitteeShortName() : "";
	if(labcode.equalsIgnoreCase("PGAD")) refNo = "PGAD/PPLG/7826/"+committeeCode.toUpperCase();

	String meetingVenue = lastmeetingCreated !=null && lastmeetingCreated[5] !=null ? lastmeetingCreated[5].toString() : "";
	String meetingDate = lastmeetingCreated !=null && lastmeetingCreated[2] !=null ? sdf.SqlToRegularDate(lastmeetingCreated[2].toString()) : "";

%>
</head>
<body>

<div align="center">

		<div class="header">
		  <div class="left">
		    <p>Phone: 040-24183911</p>
		    <p>Fax: 040-24342310</p>
		  </div>
		
		  <div class="center">
		    <figure><img style="width: 3.5cm; height: 3.5cm"  src="data:image/png;base64,<%=lablogo%>"></figure>
		  </div>
		
		  <div class="right" style="text-transform: uppercase;">
		    <p>Govt. of India</p>
		    <p>Ministry of Defence</p>
		    <p>RCI/PROGRAMME 'AD'</p>
		    <p>Kanchanbagh PO</p>
		    <p>Hyderabad - 500058</p>
		  </div>
	</div>
	<table style="margin-top:10px; margin-bottom:10px; margin-left:15px; max-width:650px; font-size:16px;">
	    <tbody>
		    <%-- <tr>
			    <td>Phone: 040-24183911</td>
			    <td>	<figure><img style="width: 3.5cm; height: 3.5cm"  src="data:image/png;base64,<%=lablogo%>"></figure></td>
			    <td></td>
		    </tr> --%>
	        <tr>
	            <td style="text-align:left; width:325px; padding-top:15px; font-size:17px;">
	                No: <%= refNo %>
	            </td>
	            <td style="width: 260px;"></td>
	            <td style="text-align:right; width:215px; font-size:17px;">
	                Dt:
	            </td>
	        </tr>
	        <tr>
	            <td colspan="3" style="text-align:left; padding-top:40px; font-size:17px; font-weight: bold; padding-left: 17px;">
	                Sub: <%=committeeCode %> Meeting held on <%=meetingDate %> at <%= meetingVenue %>.
	            </td>
	        </tr>
	        <tr>
	            <td colspan="3" style="text-align:left; width:325px; padding-top:15px; font-size:17px; font-weight: bold; padding-left: 17px;">
	                Ref No: <%= refNo %>, Dt:
	            </td>
	        </tr>
	    </tbody>
	</table>
</div>

<p style="text-align: justify-content;margin-left:30px; font-size:16px;width:650px;line-height: 30px;text-indent: 15px;padding-top: 20px;">
	Reference is made to the <span style="font-weight: bold;"><%=committeeCode %> Meeting held on <%=meetingDate %></span> at <%= meetingVenue %> for the following Projects. The Approved Minutes of Meeting has been sent through Drona mail for your kind perusal.
</p>

<table align="center">
	<tbody>
		<tr>
			<td style="text-align: left; width:200px; padding-top:15px; font-size:17px;font-weight: bold;border-bottom: 1px solid black;">
				Project
			</td>
			<td style="text-align: left; width:200px; padding-top:15px; font-size:17px;font-weight: bold;border-bottom: 1px solid black;">
				 <%=committeeCode %> Meeting Count
			</td>
		</tr>
		<%
		if(projectMeetingList!=null && !projectMeetingList.isEmpty()){
			for(Object[] obj : projectMeetingList){ %>
				<tr>
					<td style="text-align: left; width:200px; padding-top:15px; font-size:17px;">
						<%= obj[0]!=null ? obj[0].toString(): " - " %> <%= obj[1]!=null ? "("+obj[1].toString()+")": " - "%>
						<%--  <%= obj[2]!=null ? obj[2].toString(): " - "%> --%> 
					</td>
					<td style="text-align: left; width:200px; padding-top:15px; font-size:17px;">
						<%= obj[4]!=null ? committeeCode+"#"+obj[4].toString() : " - "%>
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
<br><br><br>
<div align="right" style="padding-right: 0rem;padding-bottom: 2rem; maring-top:10px;font-weight: bold;">
	<br>
		N.Ramesh Sc F
	 <br>
	 (Director of Planning)
</div>
<div align="left">
	<span style="font-size: 16px;font-weight: bold;">To</span>
	<p style="margin-bottom:0px; margin-top:5px; font-weight: bold;">Members -  <%=committeeCode %></p>
	<p style="margin-bottom:0px; margin-top:5px; font-weight: bold;">Chairperson -  <%=committeeCode %></p>
</div>
</body>
</html>