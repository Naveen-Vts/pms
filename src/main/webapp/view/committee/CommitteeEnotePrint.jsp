<%@page import="java.util.Locale"%>
<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.LocalDate"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Committe Enote</title>
<style type="text/css">
.break {
	page-break-after: always;
}

#pageborder {
	position: fixed;
	left: 0;
	right: 0;
	top: 0;
	bottom: 0;
	border: 2px solid black;
}

@page {
size: 790px 1050px;
margin-top: 49px;
margin-left: 50px;
margin-right: 50px;
margin-buttom: 49px;
border: 2px solid black;

@bottom-right {
	counter-increment: page;
			counter-reset: page 2;
	content: "Page "counter(page) " of "counter(pages);
	margin-bottom: 30px;
	margin-right: 10px;
}

	@top-right {
		content: "";
		margin-top: 30px;
		margin-right: 10px;
	}

	@top-left {
		margin-top: 30px;
		margin-left: 10px;
	
	}

	@top-center {
		margin-top: 30px;
		content: "";

	}

	@bottom-center {
		margin-bottom: 30px;
		font-size: 12px;
		content: "This is computer generated statement no signature required";
	}
}

p {
	text-align: justify;
	text-justify: inter-word;
}

body
{
	font-size:14px !important;
}

div
{
	width: 650px !important;
}
table{
	align: left;
	width: 650px !important;
	max-width: 650px !important;
	margin-top: 10px; 
	margin-bottom: 10px;
	margin-left:10px;
	border-collapse:collapse;
	
}
th,td
{
	text-align: left;
	border: 1px solid black;
	padding: 4px;
	word-break: break-word;
	overflow-wrap: anywhere;
	
	 -ms-word-break: break-all;
     word-break: break-all;

     /* Non standard for WebKit */
     word-break: break-word;

-webkit-hyphens: auto;
   -moz-hyphens: auto;
        hyphens: auto;
	
}
.center{

	text-align: center;
}

.right
{
	text-align: right;
}
input{
border-width: 0 0 1px 0;
width:80%;
}
input:focus {
  outline: none;
}

.text-blue
{
	color: blue;
	font-weight:500px;
	font-size: 15px;
}
body{
background-color:DBEDC7;
}
</style>
</head>
<body>
<%
Object[]CommitteMainEnoteList = (Object[])request.getAttribute("CommitteMainEnoteList");


SimpleDateFormat rdf= new SimpleDateFormat("dd-MM-yyyy");
SimpleDateFormat sdtf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
SimpleDateFormat rdtf= new SimpleDateFormat("dd-MM-yyyy hh:mm a");
FormatConverter fc = new FormatConverter();
SimpleDateFormat sdf = fc.getRegularDateFormat();
SimpleDateFormat sdf1 = fc.getSqlDateFormat();
SimpleDateFormat inputFormat = new SimpleDateFormat("ddMMMyyyy",Locale.ENGLISH);
SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
List<Object[]>EnotePrintDetails = (List<Object[]>)request.getAttribute("EnotePrintDetails");
String path=request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath()+"/";
String type =(String)request.getAttribute("type");
Object[]NewApprovalList = (Object[])request.getAttribute("NewApprovalList");
%>

<div align="center">
<div style="width: 100%;border: 0;text-align: center;"><h3> <b style="font-size:18px;text-decoration:underline">Note&nbsp;:&nbsp;<%if(CommitteMainEnoteList!=null && CommitteMainEnoteList[2]!=null){%>Note/<%=sdf.format(sdf1.parse(StringEscapeUtils.escapeHtml4(CommitteMainEnoteList[2].toString())))%><%} %></b></h3></div>
<div style="width: 100%;border: 0;text-align: center;"> <span style="float: left;"><b style="font-size:18px; margin-left: 14px;">Ref No&nbsp; : &nbsp;<span style="color: blue; font-size: 16px;"><%if(CommitteMainEnoteList!=null && CommitteMainEnoteList[1]!=null){%><%=StringEscapeUtils.escapeHtml4(CommitteMainEnoteList[1].toString())%><%}else{%>-<%}%></span></b></span><span style="float: right;"><b style="font-size:16px;">Date : &nbsp;<%=sdf.format(sdf1.parse(StringEscapeUtils.escapeHtml4(CommitteMainEnoteList[2].toString())))%></b></span> </div>
<br><br><br>
	<table style="border-collapse: collapse;">
	<tr>
	<td style="border: none; width: 25%;"><span style="font-size: 14px; color: blue;"><b style="margin-left:20px; color:black; ">Subject :</b></span></td>
	<td style="border: none;"><span style="font-size: 14px; color: blue;"><%if(CommitteMainEnoteList!=null && CommitteMainEnoteList[3]!=null){%><%=StringEscapeUtils.escapeHtml4(CommitteMainEnoteList[3].toString())%><%}else{%>-<%}%></span></td>
	</tr>
	<tr>
	<td style="border: none; width:25%;"><span style=" font-size: 14px; color: blue;"><b style="margin-left:20px; color: black;">Comment :</b></span></td>
	<td style="border: none;"><span style="font-size: 14px; color: blue;"><%if(CommitteMainEnoteList!=null && CommitteMainEnoteList[4]!=null && CommitteMainEnoteList[4].toString().length()>0){%><%=CommitteMainEnoteList[4].toString().replaceAll("\n", "<br>")%><%}else{%>-<%}%></span> </td>
	</tr>
	<tr>
	<td style="border: none; width:25%;"><span style=" font-size: 14px; color: blue;"><b style="margin-left:20px; color: black;">Attachment : </b></span></td>
	<td style="border: none;"><span style="font-size: 14px; color: blue;">
	<%if(type.equalsIgnoreCase("C")){ %>
	<%if(CommitteMainEnoteList!=null && CommitteMainEnoteList[5]!=null){%><a href="<%=path%>CommitteeConstitutionLetterDownload.htm?committeemainid=<%=CommitteMainEnoteList[5].toString()%>&flag=<%="P"%>" target="_blank">Download</a><%}else{%>No Attachment!<%}%></span>
	<%}else{ %>
	<%if(CommitteMainEnoteList!=null && CommitteMainEnoteList[6]!=null){%><a href="<%=path%>CommitteeMinutesNewDownload.htm?committeescheduleid=<%=CommitteMainEnoteList[6].toString()%>&flag=<%="P"%>" target="_blank">Download</a><%}else{%>No Attachment!<%}%></span>
	<%} %>
	 </td>
	</tr>
	</table>
	
	<% if(EnotePrintDetails!=null && EnotePrintDetails.size()>0){
   for(Object[] ad :EnotePrintDetails) {
     if(ad[8].toString().equalsIgnoreCase("FWD")){%>
	<div align="right" style="">&nbsp;<span class="text-blue" style="font-size :16px;"><span style="color: black;font-size: 10px;">Forwarded By</span><br><b><%=ad[2]!=null?StringEscapeUtils.escapeHtml4(ad[2].toString().trim()): " - " %>, &nbsp; <%=ad[3]!=null?StringEscapeUtils.escapeHtml4(ad[3].toString()): " - " %></b></span> </div>
	  <div align="right" style="font-size :12px;">[Forwarded On :&nbsp;<span class="text-blue" style="font-size :12px;"><%=ad[4]!=null?fc.sdtfTordtf(StringEscapeUtils.escapeHtml4(ad[4].toString())):" - " %> </span>]</div>
	  <br>
<%}}}%>
<br><br><br><br>  
<%
if(EnotePrintDetails!=null && EnotePrintDetails.size()>0){
       for(Object[] ad :EnotePrintDetails) {
		if(ad[8]!=null && ad[8].toString().equalsIgnoreCase("RC1")){%>
		<div align="left" style="margin-left: 15px !important; font-size: 12px;"><p><%if(ad[5]!=null){ %><%=StringEscapeUtils.escapeHtml4(ad[5].toString()) %><%} %></p></div>
		<div align="left" style="margin-left:12px !important;">&nbsp;<span class="text-blue" style="font-size :16px;"><%if(!ad[1].toString().equalsIgnoreCase(ad[9].toString())){%></span> <span class="text-blue" style="font-size :14px;"> <b><%=ad[2]!=null?StringEscapeUtils.escapeHtml4(ad[2].toString().trim()): " - " %>, &nbsp;<%=ad[3]!=null?StringEscapeUtils.escapeHtml4(ad[3].toString()): " - " %><%}%></b></span> </div><br>
	    <div align="left" style="margin-left:15px !important;font-size :12px;">[Recommended On :&nbsp;<span class="text-blue" style="font-size :12px;"><%=ad[4]!=null?fc.sdtfTordtf(StringEscapeUtils.escapeHtml4(ad[4].toString())):" - " %> </span>]</div>
	    <br><br><br>
	   <%}else if(ad[8]!=null && ad[8].toString().equalsIgnoreCase("RR1")){%>
	   <div align="left" style="margin-left: 15px !important; font-size: 12px;"><p><%if(ad[5]!=null){ %><%=StringEscapeUtils.escapeHtml4(ad[5].toString())%><%} %></p></div>
	   <div align="left" style="margin-left:15px !important;">&nbsp;<span class="text-blue" style="font-size :16px;"><%if(!ad[1].toString().equalsIgnoreCase(ad[9].toString())){%></span> <span class="text-blue" style="font-size :14px;">  <b><%=ad[2]!=null?StringEscapeUtils.escapeHtml4(ad[2].toString().trim()): " - "%>, &nbsp;<%=ad[3]!=null?StringEscapeUtils.escapeHtml4(ad[3].toString()): " - " %><%}%></b></span> </div><br>
	    <div align="left" style="margin-left:15px !important;font-size :12px;">[Returned On :&nbsp;<span class="text-blue" style="font-size :12px;"><%=ad[4]!=null?fc.sdtfTordtf(StringEscapeUtils.escapeHtml4(ad[4].toString())):" - " %> </span>]</div>
	     <br><br><br>
	   <%}else if(ad[8]!=null && ad[8].toString().equalsIgnoreCase("RC2")){ %>
	   <div align="left" style="margin-left: 15px !important; font-size: 12px;"><p><%if(ad[5]!=null){ %><%=StringEscapeUtils.escapeHtml4(ad[5].toString()) %><%} %></p></div>
	    <div align="left" style="margin-left:15px !important;">&nbsp;<span class="text-blue" style="font-size :16px;"><%if(!ad[1].toString().equalsIgnoreCase(ad[9].toString())){%></span> <span class="text-blue" style="font-size :14px;">  <b><%=ad[2]!=null?StringEscapeUtils.escapeHtml4(ad[2].toString().trim()): " - " %>, &nbsp;<%=ad[3]!=null?StringEscapeUtils.escapeHtml4(ad[3].toString()): " - " %><%}%></b></span> </div><br>
	    <div align="left" style="margin-left:15px !important;font-size :12px;">[Recommended On :&nbsp;<span class="text-blue" style="font-size :12px;"> <%=ad[4]!=null?fc.sdtfTordtf(StringEscapeUtils.escapeHtml4(ad[4].toString())):" - " %> </span>]</div>
	      <br><br><br>
	   <%}else if (ad[8]!=null && ad[8].toString().equalsIgnoreCase("RR2")){ %>
	   <div align="left" style="margin-left: 15px !important; font-size: 12px;"><p><%if(ad[5]!=null){ %><%=StringEscapeUtils.escapeHtml4(ad[5].toString())%><%} %></p></div>
	    <div align="left" style="margin-left:15px !important;">&nbsp;<span class="text-blue" style="font-size :16px;"><%if(!ad[1].toString().equalsIgnoreCase(ad[9].toString())){%></span> <span class="text-blue" style="font-size :14px;">  <b><%=ad[2]!=null?StringEscapeUtils.escapeHtml4(ad[2].toString().trim()): " - " %>, &nbsp;<%=ad[3]!=null?StringEscapeUtils.escapeHtml4(ad[3].toString()): " - " %><%}%></b></span> </div><br>
	    <div align="left" style="margin-left:15px !important;font-size :12px;">[Returned On :&nbsp;<span class="text-blue" style="font-size :12px;"><%=ad[4]!=null?fc.sdtfTordtf(StringEscapeUtils.escapeHtml4(ad[4].toString())):" - " %>  </span>]</div>
	      <br><br><br>
	   <%}else if(ad[8]!=null && ad[8].toString().equalsIgnoreCase("RC3")) {%>
	   <div align="left" style="margin-left: 15px !important; font-size: 12px;"><p><%if(ad[5]!=null){ %><%=StringEscapeUtils.escapeHtml4(ad[5].toString()) %><%} %></p></div>
	    <div align="left" style="margin-left:15px !important;">&nbsp;<span class="text-blue" style="font-size :16px;"><%if(!ad[1].toString().equalsIgnoreCase(ad[9].toString())){%></span> <span class="text-blue" style="font-size :14px;">  <b><%=ad[2]!=null?StringEscapeUtils.escapeHtml4(ad[2].toString().trim()): " - " %>, &nbsp;<%=ad[3]!=null?StringEscapeUtils.escapeHtml4(ad[3].toString()): " - " %><%}%></b></span> </div><br>
	   <div align="left" style="margin-left:15px !important;font-size :12px;">[Recommended On :&nbsp;<span class="text-blue" style="font-size :12px;"><%=ad[4]!=null?fc.sdtfTordtf(StringEscapeUtils.escapeHtml4(ad[4].toString())):" - " %>  </span>]</div>
	      <br><br><br><br>
	   <%}else if(ad[8]!=null && ad[8].toString().equalsIgnoreCase("RR3")){ %>
	   <div align="left" style="margin-left: 15px !important; font-size: 12px;"><p><%if(ad[5]!=null){ %><%=StringEscapeUtils.escapeHtml4(ad[5].toString()) %><%} %></p></div>
	    <div align="left" style="margin-left:15px !important;">&nbsp;<span class="text-blue" style="font-size :16px;"><%if(!ad[1].toString().equalsIgnoreCase(ad[9].toString())){%></span> <span class="text-blue" style="font-size :14px;">  <b><%=ad[2]!=null?StringEscapeUtils.escapeHtml4(ad[2].toString().trim()): " - " %>, &nbsp;<%=ad[3]!=null?StringEscapeUtils.escapeHtml4(ad[3].toString()): " - " %><%}%></b></span> </div><br>
  <div align="left" style="margin-left:15px !important;font-size :12px;">[Returned On :&nbsp;<span class="text-blue" style="font-size :12px;"><%=ad[4]!=null?fc.sdtfTordtf(StringEscapeUtils.escapeHtml4(ad[4].toString())):" - " %>  </span>]</div> 
	      <br><br><br><br>
	   <%}else if(ad[8]!=null && ad[8].toString().equalsIgnoreCase("APR")){ %>
		<br><br>
         <div align="center" style="text-align:center;"> 
         <span style="font-weight: 600; font-size: 16px; color: green;">APPROVED</span><br>
         <span style="font-weight: 400; font-size: 12px; color: green;"><%if(ad[5]!=null){ %><%=StringEscapeUtils.escapeHtml4(ad[5].toString()) %><%} %></span><br><br>
         <span style="font-weight: 500; font-size: 14px; color: green;"><span class="text-blue" style="font-size :14px; color: green;">  <b><%=ad[2]!=null?StringEscapeUtils.escapeHtml4(ad[2].toString().trim()): " - " %>, &nbsp;<%=ad[3]!=null?StringEscapeUtils.escapeHtml4(ad[3].toString()): " - " %></b></span></span><br><br>
  <span style="font-weight: 400; font-size: 12px; color: green;">[Approved On :&nbsp;<span class="text-blue" style="font-size:12px; color: green;"><%=ad[4]!=null?fc.sdtfTordtf(StringEscapeUtils.escapeHtml4(ad[4].toString())):" - " %> </span>]</span> 
         </div>
     <%   } else if(ad[8]!=null && ad[8].toString().equalsIgnoreCase("RAP")){%>
		   <div align="left" style="margin-left: 15px !important; font-size: 12px;"><p><%if(ad[5]!=null){ %><%=StringEscapeUtils.escapeHtml4(ad[5].toString())%><%} %></p></div>
	   		 <div align="left" style="margin-left:15px !important;">&nbsp;<span class="text-blue" style="font-size :16px;"><%if(!ad[1].toString().equalsIgnoreCase(ad[9].toString())){%></span> <span class="text-blue" style="font-size :14px;">  <b><%=ad[2]!=null?StringEscapeUtils.escapeHtml4(ad[2].toString().trim()): " - " %>, &nbsp;<%=ad[3]!=null?StringEscapeUtils.escapeHtml4(ad[3].toString()): " - " %><%}%></b></span> </div><br>
  			<div align="left" style="margin-left:15px !important;font-size :12px;">[Returned On :&nbsp;<span class="text-blue" style="font-size :12px;"><%=ad[4]!=null?fc.sdtfTordtf(StringEscapeUtils.escapeHtml4(ad[4].toString())):" - " %>  </span>]</div> 
	      <br><br><br><br>
     <%} }}%>
     
 <%--     <%if(CommitteMainEnoteList!=null && CommitteMainEnoteList[15].toString().equalsIgnoreCase("APR")&& !CommitteMainEnoteList[22].toString().equalsIgnoreCase((String)session.getAttribute("labcode"))){ %>
     <br><br>
         <div align="center" style="text-align:center;"> 
         <span style="font-weight: 600; font-size: 16px; color: green;">APPROVED</span><br>
         <span style="font-weight: 400; font-size: 12px; color: green;"><%if(ad[5]!=null){ %><%=ad[5].toString() %><%} %></span><br><br>
         <span style="font-weight: 500; font-size: 14px; color: green;"><span class="text-blue" style="font-size :14px; color: green;">  <b><%=NewApprovalList[7] %>, &nbsp;<%=NewApprovalList[8].toString() %>, <%=NewApprovalList[9].toString() %></b></span></span><br><br>
  <span style="font-weight: 400; font-size: 12px; color: green;">[Approved On :&nbsp;<span class="text-blue" style="font-size:12px; color: green;"><%=fc.sdtfTordtf(EnotePrintDetails.get(EnotePrintDetails.size()-1)[4].toString()) %> </span>]</span> 
         </div>
     <%} %> --%>
</div>
</body>
</html>