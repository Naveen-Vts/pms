<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.math.BigDecimal"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="java.util.*"%>
<%@page import="com.vts.pfms.model.LabMaster"%>
<!DOCTYPE html>
<html>
<head>

<%
	List<Object[]> speclists = (List<Object[]>) request.getAttribute("committeeminutesspeclist");
	Object[] committeescheduleeditdata = (Object[]) request.getAttribute("committeescheduleeditdata");
	List<Object[]> committeeminutes = (List<Object[]>) request.getAttribute("committeeminutes");
	List<Object[]> invitedlist = (List<Object[]>) request.getAttribute("committeeinvitedlist");
	Object[] labdetails = (Object[]) request.getAttribute("labdetails");
	HashMap< String, ArrayList<Object[]>> actionlist = (HashMap< String, ArrayList<Object[]>>) request.getAttribute("actionsdata");
	int addcount=0; 
	 
	String projectid= committeescheduleeditdata[9].toString();
	String divisionid= committeescheduleeditdata[16].toString();
	String initiationid= committeescheduleeditdata[17].toString();
	Object[] projectdetails=(Object[])request.getAttribute("projectdetails");
	Object[] divisiondetails=(Object[])request.getAttribute("divisiondetails");
	Object[] initiationdetails=(Object[])request.getAttribute("initiationdetails");
	int meetingcount= (int) request.getAttribute("meetingcount");
	
	String[] no=committeescheduleeditdata[11].toString().split("/");
	
	FormatConverter fc=new FormatConverter(); 
	SimpleDateFormat sdf=fc.getRegularDateFormat();
	SimpleDateFormat sdf1=fc.getSqlDateFormat();
	
	String isprint=(String)request.getAttribute("isprint");
	String lablogo=(String)request.getAttribute("lablogo");
	Object[] membersec=null; 
	LabMaster labInfo=(LabMaster)request.getAttribute("labInfo");
	List<String> repCodes = (List<String> )request.getAttribute("repCodes");
	String ccmFlag = (String)request.getAttribute("ccmFlag");
	%>
<style type="text/css">

.break
{
	page-break-after: always;
}
p{
  text-align: justify;
  text-justify: inter-word;
}
 
 .summary p 
 {
 	text-indent: 70px;
 }
@page {             
          size: 790px 1120px;
          margin-top: 49px;
          margin-left: 39px;
          margin-right: 39px;
          margin-buttom: 49px; 	
          border: 1px solid black;    
          @bottom-right {          		
             content: "Page " counter(page) " of " counter(pages);
             margin-bottom: 30px;
             margin-right: 10px;
          }
          @top-right {
          		<%if( Long.parseLong(projectid)>0){%>
             content: "Project:<%=projectdetails[4]!=null?projectdetails[4].toString(): " - "%>";
             <%}else if(Long.parseLong(divisionid)>0){%>
               	content: "Division:<%=divisiondetails[1]!=null?divisiondetails[1].toString(): " - "%>";
             <%}else if(Long.parseLong(initiationid)>0){ %>
             	content: "Pre-Project :<%=initiationdetails[1]!=null?initiationdetails[1].toString(): " - "%>";
             <%} else{%>
             	content: "<%=labdetails[1]!=null?labdetails[1].toString(): " - "%>";
             <%}%>
             margin-top: 30px;
             margin-right: 10px;
               font-size: 13px;
          }
          @top-left {
          	margin-top: 30px;
            margin-left: 10px;
            content: "<%=no[0]!=null?no[0].toString(): " - "%>/<%=no[1]!=null?no[1].toString(): " - "%>/<%=no[2]!=null?no[2].toString(): " - " %><%if(meetingcount>0){ %>#<%=meetingcount %><%} %>/<%=no[3]!=null?no[3].toString(): " - "%>";
             font-size: 13px;
          }      
          
          @top-center { 
           font-size: 13px;
          margin-top: 30px;
          content: "<%=committeescheduleeditdata[15]!=null?committeescheduleeditdata[15].toString(): " - "%>"; 
          
          }
          
           @bottom-center { 
             font-size: 13px;
	          margin-bottom: 30px;
	          content: "<%=committeescheduleeditdata[15]!=null?committeescheduleeditdata[15].toString(): " - "%>"; 
          
          } 
          
          @bottom-left { 
             font-size: 13px;
	          margin-bottom: 30px;
	          content: "<%=LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))%>"; 
	          
          
          } 
               
    
          <%-- @bottom-left {          		
 			 content : "The information in this Document is proprietary of <%=labInfo.getLabCode() %> /DRDO , MOD Govt. of India. Unauthorized possession may be liable for prosecution.";
 			 margin-bottom: 30px;
             margin-right: 5px;
             font-size: 9.5px;
          } --%>
 }
 
 .sth
 {
 	   font-size: 16px;
 	   border: 1px solid black;
 }
 
 .std
 {
 	text-align: center;
 	border: 1px solid black;
 }
 
</style>
<meta charset="ISO-8859-1">
<title><%=committeescheduleeditdata[8]%> Minutes View</title>
</head>
<body>
	<div id="container" align="center"  class="cent">
		<div class="cent">		
			<br>
			<div style="text-align: center;" ><h1>MINUTES OF MEETING</h1></div>
			<br>
			<div style="text-align: center;" ><h2 style="margin-bottom: 2px;"><%=committeescheduleeditdata[7]!=null?committeescheduleeditdata[7].toString().toUpperCase():" - "%>  (<%=committeescheduleeditdata[8]!=null?committeescheduleeditdata[8].toString().toUpperCase():" - " %><%if(meetingcount>0){ %>&nbsp;&nbsp;#<%=meetingcount %><%} %>) </h2></div>				
				<%if(Integer.parseInt(projectid)>0){ %>					
					<h3 style="margin-top: 5px; margin-bottom: 5px">For</h3>	  
				    <h2 style="margin-top: 3px">Project  : &nbsp;<%=projectdetails[1]!=null?projectdetails[1].toString(): " - " %>  (<%=projectdetails[4]!=null?projectdetails[4].toString(): " - "%>)</h2>
				<%}else if(Integer.parseInt(divisionid)>0){ %>					
					<h3 style="margin-top: 5px; margin-bottom: 5px">For</h3>	  
			 	   	<h2 style="margin-top: 3px">Division :&nbsp;<%=divisiondetails[2]!=null?divisiondetails[2].toString(): " - " %>  (<%=divisiondetails[1]!=null?divisiondetails[1].toString(): " - "%>)</h2>
				<%}else if(Integer.parseInt(initiationid)>0){ %>					
					<h3 style="margin-top: 5px; margin-bottom: 5px">For</h3>	  
				    <h2 style="margin-top: 3px">Pre-Project  : &nbsp;<%=initiationdetails[2]!=null?initiationdetails[2].toString(): " - " %>  (<%=initiationdetails[1]!=null?initiationdetails[1].toString(): " - "%>)</h2>
				<%}else{%>
					<br><br><br><br><br>
				<%} %>
				<br>
				<table style="align: center; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; max-width: 650px; font-size: 16px"  border="0">
					<tr style="margin-top: 10px">
						 <th  style="text-align: center; width: 650px;font-size: 20px"> <u>Meeting Id </u> </th></tr><tr>
						 <th  style="text-align: center;  width: 650px;font-size: 20px"> <%=committeescheduleeditdata[11]!=null?committeescheduleeditdata[11].toString(): " - " %> </th>				
					 </tr>
				</table>
				
				<br><br>
		 <table style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; max-width: 650px; font-size: 16px"  border="0">
			 <tr>
				 <th  style="text-align: center; width: 650px;font-size: 20px "> <u> Meeting Date </u></th>
				 <th  style="text-align: center;  width: 650px;font-size: 20px  "><u> Meeting Time </u></th>
			 </tr>
			
			 <tr>
				 <td  style="text-align: center;  width: 650px;font-size: 20px ;padding-top: 5px"> <b><%=committeescheduleeditdata[2]!=null?sdf.format(sdf1.parse(committeescheduleeditdata[2].toString())): " - " %></b></td>
				 <td  style="text-align: center;  width: 650px;font-size: 20px ;padding-top: 5px "> <b><%=committeescheduleeditdata[3]!=null?committeescheduleeditdata[3].toString(): " - "%></b></td>
			 </tr>
			 
		 </table>
		 
		 <table style="align: center; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; max-width: 650px; font-size: 16px"  border="0">
					<tr style="margin-top: 10px">
						 <th  style="text-align: center; width: 650px;font-size: 20px "> <u>Meeting Venue </u> </th></tr><tr>
						 <th  style="text-align: center;  width: 650px;font-size: 20px  "> <% if(committeescheduleeditdata[12]!=null){ %><%=committeescheduleeditdata[12].toString() %> <%}else{ %> - <%} %></th>				
					 </tr>
				</table>
		<br><br><br><br><br>
			<figure><img style="width: 4cm; height: 4cm"  src="data:image/png;base64,<%=lablogo%>"></figure>   
			<br>				<br><br>
			
			
			<div style="text-align: center;" ><h3><%=labdetails[2]!=null?labdetails[2].toString(): " - " %> (<%=labdetails[1]!=null?labdetails[1].toString(): " - "%>)</h3></div>
			
			<div align="center" ><h3><%=labdetails[4]!=null?labdetails[4].toString(): " - " %>, &nbsp;<%=labdetails[5]!=null?labdetails[5].toString(): " - " %>, &nbsp;<%=labdetails[6]!=null?labdetails[6].toString(): " - " %></h3></div>
			
			
		</div>
		
 <h1 class="break"></h1> 
<!-- ------------------------------------------------------- members --------------------------------- -->
<%if(invitedlist.size()>0){ %>
<% ArrayList<String> membertypes=new ArrayList<String>(Arrays.asList("CC","CS","PS","CI","CW","CO","CH"));
	membertypes.addAll(repCodes);

int memPresent=0,memAbscent=0,ParPresent=0,parAbscent=0;
int j=0;
for(Object[] temp : invitedlist){

	if(temp[4].toString().equals("P") &&  membertypes.contains( temp[3].toString()) )
	{ 
		memPresent++;
	}
	else if(temp[4].toString().equals("N") &&  membertypes.contains( temp[3].toString()) )
	{
		memAbscent++;
	}
	else if( temp [4].toString().equals("P") && !membertypes.contains( temp[3].toString()) )
	{ 
		ParPresent++;
	}
	else if( temp [4].toString().equals("N") && !membertypes.contains( temp[3].toString()) )
	{ 
		parAbscent++;
	}
}
%>


<div style="align : center;">
<h2>ATTENDANCE</h2>
<table style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; width: 650px; font-size: 16px; border-collapse:collapse;" >	
	
	 <tr>
		 <th style="text-align: center ;padding: 5px;border: 1px solid black;width: 10px; ">SN</th>
		 <th style="text-align: center ;padding: 5px;border: 1px solid black;width: 380px; ">Name, Designation</th>
		 <th style="text-align: center ;padding: 5px;border: 1px solid black;width: 120px; ">Estt. / Agency</th>
		 <th style="text-align: center ;padding: 5px;border: 1px solid black;width: 140px; ">Role</th>
	 </tr>
	  <tr>
		 <th colspan="4" style="text-align: left; font-weight: 700; border: 1px solid black; padding: 5px; padding-left: 15px">Members Present</th>
	 </tr>
	 <%if(memPresent > 0){ %>
	 
	 <% 
	 	for(int i=0;i<invitedlist.size();i++)
		{
	 	if(invitedlist.get(i)[4].toString().equals("P") && membertypes.contains( invitedlist.get(i)[3].toString()) )
	 	{ j++;%>
	 	
	 	 <tr>
	 	 <td style="border: 1px solid black; padding: 5px;text-align: center"><%=j%> </td>
	 	  	<td style="border: 1px solid black; padding: 5px;text-align: left">  
	 	  	
	 			<%= invitedlist.get(i)[6]!=null?invitedlist.get(i)[6].toString(): " - "%>,&nbsp;<%=invitedlist.get(i)[7]!=null?invitedlist.get(i)[7].toString(): " - " %>  
		 	</td>
		 	<td style="border: 1px solid black; padding: 5px;text-align: left;">  
	 	  	
	 			<%= invitedlist.get(i)[11]!=null?invitedlist.get(i)[11].toString(): " - "%>  
		 	</td>	
		 	<td style="border: 1px solid black;padding: 5px ;text-align: left">
		 		<%  if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CC")) {		 %>Chairperson<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CS") ){ membersec=invitedlist.get(i); %> Member Secretary<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CH") ){ %> Co-Chairperson<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("PS") ) { %>Member Secretary&nbsp;(Proxy) <%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CI")){   %>Internal<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CW")){	 %>External<%-- &nbsp;(<%=invitedlist.get(i)[11] %>) --%><%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CO")){	 %>External<%-- &nbsp;(<%=invitedlist.get(i)[11]%>) --%><%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("P") ){	 %>Presenter <%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("I")){	 %>Internal<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("W") ){	 %>External<%-- &nbsp;(<%=invitedlist.get(i)[11] %>) --%><%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("E") )    {%>External<%-- &nbsp;(<%=invitedlist.get(i)[11] %>) --%><%}
		 			// Prudhvi - 27/03/2024 start
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CIP") )    {%>Industry Partner<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("IP") )    {%>Addl. Industry Partner<%}
					// Prudhvi - 27/03/2024 end
					else {%> REP_<%=invitedlist.get(i)[3]!=null?invitedlist.get(i)[3].toString(): " - "%><%-- &nbsp; (<%=invitedlist.get(i)[11] %>) --%>  <%}
				%>
	 		</td>	
	 		</tr>
	 <%}
	 } %>
	 
	 <% } %>
	 
	 <%if(memAbscent > 0){ %>
	 	
	  	<tr >
			<th colspan="4" style="text-align: left; font-weight: 700; width: 650px;border: 1px solid black; padding: 5px; padding-left: 15px">Following Members Could not Attend due to Prior Commitments</th>
		</tr>
	<% 
	int count=0;
	for(int i=0;i<invitedlist.size();i++)
	 {
	 	if(invitedlist.get(i)[4].toString().equals("N")&& membertypes.contains( invitedlist.get(i)[3].toString()) )
	 	{count++; j++; %>
	 	 <tr > 	
	 	  <td style="border: 1px solid black; padding: 5px;text-align: center"> <%=j%> </td>
	 	 <td style="border: 1px solid black ;padding: 5px;text-align: left " >  
	 		<%= invitedlist.get(i)[6]!=null?invitedlist.get(i)[6].toString(): " - "%>,&nbsp;<%=invitedlist.get(i)[7]!=null?invitedlist.get(i)[7].toString(): " - " %>
	 		</td>	
	 		<td style="border: 1px solid black; padding: 5px;text-align: left;">  
	 	  	
	 			<%= invitedlist.get(i)[11]!=null?invitedlist.get(i)[11].toString(): " - "%>  
		 	</td>
	 		<td style="border: 1px solid black ;padding: 5px ;text-align: left "> 
	 			<%  if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CC")) {		 %>Chairperson<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CS") ){ membersec=invitedlist.get(i); %> Member Secretary<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CH") ){ %> Co-Chairperson<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("PS") ) { %>Member Secretary&nbsp;(Proxy) <%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CI")){   %>Internal<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CW")){	 %>External<%-- &nbsp;(<%=invitedlist.get(i)[11] %>) --%><%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CO")){	 %>External<%-- &nbsp;(<%=invitedlist.get(i)[11]%>) --%><%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("P") ){	 %>Presenter <%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("I")){	 %>Internal<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("W") ){	 %>External<%-- &nbsp;(<%=invitedlist.get(i)[11] %>) --%><%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("E") )    {%>External<%-- &nbsp;(<%=invitedlist.get(i)[11] %>) --%><%}
		 			// Prudhvi - 27/03/2024 start
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CIP") )    {%>Industry Partner<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("IP") )    {%>Addl. Industry Partner<%}
					// Prudhvi - 27/03/2024 end
					else {%> REP_<%=invitedlist.get(i)[3]!=null?invitedlist.get(i)[3].toString(): " - "%><%-- &nbsp; (<%=invitedlist.get(i)[11] %>) --%>  <%}
				%>
	 		</td>	
	 	</tr>
	 	
	 <%}
	 } %>
	 
	 <%if(count==0){ %>
	 	<tr><th colspan="4" style="text-align:center; font-weight: 20; width: 650px;border: 1px solid black; padding: 5px; padding-left: 15px">Nil</th></tr>
	 <%} %>
	
	<%} %>
	
	 <%if(ParPresent > 0){ %>
	
	 <tr>
		 <th colspan="4" style="text-align: left; font-weight: 700; width: 650px;border: 1px solid black; padding: 5px; padding-left: 15px">Other Invitees&nbsp;/&nbsp;Participants </th>
	 </tr>
	 
	 <%
		
	 for(int i=0;i<invitedlist.size();i++)
		{
	 	if(invitedlist.get(i)[4].toString().equals("P") && !membertypes.contains( invitedlist.get(i)[3].toString()) )
	 	{ j++;
	 	addcount++;
	 	%>
	 	
	 	 <tr>
	 	 <td style="border: 1px solid black; padding: 5px;text-align: center"> <%=j%> </td>
	 	  	<td style="border: 1px solid black; padding: 5px;text-align: left">  
	 	  	
	 			<%= invitedlist.get(i)[6]!=null?invitedlist.get(i)[6].toString(): " - "%>,&nbsp;<%=invitedlist.get(i)[7]!=null?invitedlist.get(i)[7].toString(): " - " %>
		 	</td>	
		 	<td style="border: 1px solid black; padding: 5px;text-align: left;">  
	 	  	
	 			<%= invitedlist.get(i)[11]!=null?invitedlist.get(i)[11].toString(): " - "%>  
		 	</td>
		 	<td style="border: 1px solid black;padding: 5px ;text-align: left">
		 		<%  if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CC")) {		 %>Chairperson<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CS") ){ membersec=invitedlist.get(i); %> Member Secretary<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CH") ){ %> Co-Chairperson<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("PS") ) { %>Member Secretary&nbsp;(Proxy) <%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CI")){   %>Internal<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CW")){	 %>External<%-- &nbsp;(<%=invitedlist.get(i)[11] %>) --%><%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CO")){	 %>External<%-- &nbsp;(<%=invitedlist.get(i)[11]%>) --%><%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("P") ){	 %>Presenter <%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("I")){	 %>Internal<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("W") ){	 %>External<%-- &nbsp;(<%=invitedlist.get(i)[11] %>) --%><%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("E") )    {%>External<%-- &nbsp;(<%=invitedlist.get(i)[11] %>) --%><%}
		 			// Prudhvi - 27/03/2024 start
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CIP") )    {%>Industry Partner<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("IP") )    {%>Addl. Industry Partner<%}
					// Prudhvi - 27/03/2024 end
					else {
						String repCode = invitedlist.get(i)[3]!=null ? invitedlist.get(i)[3].toString() : "";
						boolean isCommitteRep = !repCode.endsWith("_NORMAL");
						
						if(!isCommitteRep){
							String[] reps = repCode.split("_");
							repCode = "REP_"+reps[0];
						}else{
							repCode = "REP_"+repCode;
						}
						
					
					%> <%= repCode %> <%}
				%>
	 		</td>	
	 		</tr>
	 <%}
	 } %>
	 
	  <%if(addcount==0)
	  {%>
		 	<tr><th colspan="4" style="text-align:center; font-weight: 20; width: 650px;border: 1px solid black; padding: 5px; padding-left: 15px">Nil</th> </tr>
	  <%}%>
	  <% } %>
	  
	  <%if(parAbscent > 0){ %>
	  
	 <tr >
			<th colspan="4" style="text-align: left; font-weight: 700; width: 650px;border: 1px solid black; padding: 5px; padding-left: 15px">Other Invitees&nbsp;/&nbsp;Participants Absent</th>
		</tr>
	
	 
	 
	<% 
	int count1=0;
	for(int i=0;i<invitedlist.size();i++)	
	 {
	 	if(invitedlist.get(i)[4].toString().equals("N")&& !membertypes.contains( invitedlist.get(i)[3].toString()) )
	 	{count1++; j++; %>
	 	 <tr > 	
	 	  <td style="border: 1px solid black; padding: 5px;text-align: center"> <%=j%> </td>
	 	 <td style="border: 1px solid black ;padding: 5px;text-align: left " >  
	 		<%= invitedlist.get(i)[6]!=null?invitedlist.get(i)[6].toString(): " - "%>,&nbsp;<%=invitedlist.get(i)[7]!=null?invitedlist.get(i)[7].toString(): " - " %>
	 		</td>	
	 		<td style="border: 1px solid black; padding: 5px;text-align: left;">  
	 	  	
	 			<%= invitedlist.get(i)[11]!=null?invitedlist.get(i)[11].toString(): " - "%>  
		 	</td>
	 		
	 		<td style="border: 1px solid black ;padding: 5px ;text-align: left "> 
	 			<%  if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CC")) {		 %>Chairperson<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CS") ){ membersec=invitedlist.get(i); %> Member Secretary<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CH") ){ %> Co-Chairperson<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("PS") ) { %>Member Secretary&nbsp;(Proxy) <%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CI")){   %>Internal<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CW")){	 %>External<%-- &nbsp;(<%=invitedlist.get(i)[11] %>) --%><%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CO")){	 %>External<%-- &nbsp;(<%=invitedlist.get(i)[11]%>) --%><%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("P") ){	 %>Presenter <%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("I")){	 %>Internal<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("W") ){	 %>External<%-- &nbsp;(<%=invitedlist.get(i)[11] %>) --%><%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("E") )    {%>External<%-- &nbsp;(<%=invitedlist.get(i)[11] %>) --%><%}
		 			// Prudhvi - 27/03/2024 start
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CIP") )    {%>Industry Partner<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("IP") )    {%>Addl. Industry Partner<%}
					// Prudhvi - 27/03/2024 end
					else {
						String repCode = invitedlist.get(i)[3]!=null ? invitedlist.get(i)[3].toString() : "";
						boolean isCommitteRep = !repCode.endsWith("_NORMAL");
						
						if(!isCommitteRep){
							String[] reps = repCode.split("_");
							repCode = "REP_"+reps[0];
						}else{
							repCode = "REP_"+repCode;
						}
						
					
					%> <%= repCode %> <%}
				%>
	 		</td>	
	 	</tr>
	 	
	 <%}
	 } %>
	 
	 <%if(count1==0){ %>
	 	<tr><th colspan="4" style="text-align:center; font-weight: 20; width: 650px;border: 1px solid black; padding: 5px; padding-left: 15px">Nil</th></tr>
	 <%} %>
	
	
	 <%} %>

	  
	 <tr> <td></td>	</tr>
</table>



</div>
<%} %>
 <h1 class="break"></h1> 
<!-- -------------------------------------------------------members----------------------------- -->
	<div style="margin-top: 25px;">
		<%
			for (Object[] committeemin : committeeminutes) {
			if (committeemin[0].toString().equals("1") || committeemin[0].toString().equals("2")) {
		%>
		
		<table style="margin-top: 0px; margin-left: 0px; width: 650px; font-size: 16px; border-collapse: collapse;">
			<tbody>
				<tr>
					<th colspan="8" style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=committeemin[0]!=null?committeemin[0].toString(): " - "%>.&nbsp;&nbsp;&nbsp;<%=committeemin[1]!=null?committeemin[1].toString(): " - "%></th>
				</tr>
				<tr>
						<% int count = 0;

						for (Object[] speclist : speclists)
						{
							if (speclist[3].toString().equals(committeemin[0].toString())) 
							{
								count++;
						%>
					
					<td style="text-align: left;">
					<div align="left" style="padding-left: 30px"><%=speclist[1]!=null?speclist[1].toString(): " - "%></div>
					</td>

					<%	break;		
							}
						}
						if (count == 0) 
						{%>
							<td style="text-align: left;">
								<div align="left" style="padding-left: 30px">
									<p>NIL<p>
								</div>
							</td>

						<% } %>

				
				</tr>
				</table>
				</div>
				<%} %>
	<%} %>
	<!-- ---------------------------------------------------------------------------table minutes start ----------------------------------- -->
	
			<div align="center" > 
				
					<%if(committeeminutes.size()>0){ %>
					<br><br>
					<table style="margin-top: 0px; margin-left: 15px; width: 650px; font-size: 16px; border-collapse: collapse;">
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;">3. &nbsp;&nbsp;Record of Discussions and Action Points of Current Meeting.</th>
						</tr>
						<tr>
							<td colspan="8" style="text-align: center ;padding: 5px;">Item Code/Type : A: Action, C: Comment, D: Decision, R: Recommendation</td>
						</tr>
					</table>
					<table style="margin-top: 0px; margin-left: 10px;width: 670px; font-size: 16px; border-collapse: collapse ;border: 1px solid black ">
					<tbody>
						<tr>
							<th  class="sth" style=" max-width: 30px"> SN</th>
							<th  class="sth" style=" max-width: 35px"> Type</th>	
							<th  class="sth" style=" max-width: 600px"> Item</th>				
							<th  class="sth" style="width: 195px"> Remarks</th>	
						</tr>
						
							<% int count=0;
							String agenda="";
							for(int i=0;i<speclists.size();i++)
							{ 
								if(Integer.parseInt(speclists.get(i)[3].toString())>=3&&Integer.parseInt(speclists.get(i)[3].toString())<6){
								count++; %>					
					<%if(!speclists.get(i)[10].toString().equalsIgnoreCase(agenda)){ %>
					<tr>
						<td colspan="4" class="std"
							style="text-align: center; border: 1px solid black;padding:7px;font-weight: bold"><%=speclists.get(i)[10]!=null?speclists.get(i)[10].toString(): " - "%>
						</td>
					</tr>
					<%} %>
							<tr>
								<td class="std" style="text-align :center;border:1px solid black;"  > <%=count%></td>
								<td class="std" style="text-align :center;border:1px solid black; padding: 5px 5px 5px 5px ; vertical-align: top;" >							
									<%if(speclists.get(i)[7].toString().equals("D")){ %>D<%}
							 		else if(speclists.get(i)[7].toString().equals("A")){ %>A<%} 
							 		else if(speclists.get(i)[7].toString().equals("R")){ %>R<%} 
							 		else if(speclists.get(i)[7].toString().equals("I")){ %>I<%} 
							 		else if(speclists.get(i)[7].toString().equals("C")){ %>C<%}
							 		else if(speclists.get(i)[7].toString().equals("K")){ %>Ri<%} %> 				
								</td>
								<td  class="std" style="border:1px solid black;padding:  5px 5px 5px 5px ;width: 600px;text-align: justify;"><%=speclists.get(i)[1]!=null?speclists.get(i)[1].toString(): " - "%></td>
								<td class="std" style="text-align :center;border:1px solid black;padding:  5px 5px 5px 5px;"  > <%if( speclists.get(i)[8]!=null && !speclists.get(i)[8].toString().equalsIgnoreCase("nil")){ %> <%= speclists.get(i)[8].toString()%> <%}else{ %> - <%} %></td>
							</tr>
							<%
							agenda=speclists.get(i)[10].toString();
 						} 
							}%>
							<% if(count==0){%>
								<tr>
									<td class="std" style="text-align :center;border:1px solid black;"  colspan="4">No Minutes details Added</td>
								</tr>
							<%} %>
						</table>									
					<%} %>			
			</div>	
			
			 <%
			 if(ccmFlag==null) {
			 for (Object[] committeemin : committeeminutes) { 
				 if ( committeemin[0].toString().equals("6")) { %>
				 <br><br>
			 <table style="margin-top: 0px; margin-left: 15px; width: 650px; font-size: 16px; border-collapse: collapse;">
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;">6. &nbsp;&nbsp;Final Recommendation & Conclusion</th>
						</tr>
			 </table>
			<br>
			<div align="center">
				<table style="margin-top: 10px; width: 650px; font-size: 16px; border-collapse: collapse;">
					<tbody>
						<% for (Object[] speclist : speclists)
						{
							if (speclist[3].toString().equals(committeemin[0].toString())) 
							{
								if(speclist[3].toString().equals("6")){
								%>
							
									<tr>
										<td class="" style="text-align: justify;padding-left: 15px;">
											<%if(speclist[1]!=null && !speclist[1].toString().trim().equalsIgnoreCase("")){%> <%=speclist[1]!=null?speclist[1].toString(): " - "%>  <%}else{ %> - <%} %>  
										</td>
									</tr>							
								<%}
							}							
							
						}%>
					<%} %>
				</table>
			</div>
				
	<%} }%>
	<br>
	<div >
		<div style="width: 650px;margin-left: 15px; ">
			<div align="left" >
				These minutes are issued with the approval of the Chairperson.
			</div>
			<div align="left" style="padding-right: 0rem;padding-bottom: 0rem; margin-right: 0px">
				<br>Date :&emsp;&emsp;&emsp;&emsp;&emsp;  <br>Time :&emsp;&emsp;&emsp;&emsp;&emsp;
				<%if(membersec!=null){ %>
					<div align="right" style="padding-right: 0rem;padding-bottom: 2rem;">
						<br><br><%if(membersec[6]!=null){%><%= membersec[6].toString().substring(membersec[6].toString().indexOf(".")+1) %><%} %>
						<br>(Member Secretary)
					</div>
				<%} %>
			</div>
			<div align="left" ><b>NOTE : </b>Action item details are enclosed as Annexure - AI.</div>		
		</div> 
	</div>		
			<%if(isprint!=null  && isprint.equals("N")){ %> 
			 <div class="break"></div>	
			<br>
			<div align="center">
			<div style="text-align: center;  " class="lastpage" id="lastpage"><h2>ACTION ITEMS DETAILS</h2></div>
		
			<table style="margin-top: -5px; margin-left: 5px; width: 670px; font-size: 16px; border-collapse: collapse ;border: 1px solid black ">
			<tbody>
				<tr>
					<th  class="sth" style=" max-width: 30px"> SN. </th>
					<th  class="sth" style=" max-width: 70px"> Action Id</th>	
					<th  class="sth" style=" max-width: 600px"> Item</th>				
					<th  class="sth" style=" max-width: 70px"> Responsibility</th>					
					<th  class="sth" style=" width: 90px"> PDC</th>
				</tr>
				
				<% int count =0;
				  Iterator actIterator = actionlist.entrySet().iterator();
				while(actIterator.hasNext()){	
					Map.Entry mapElement = (Map.Entry)actIterator.next();
		            String key = ((String)mapElement.getKey());
		            ArrayList<Object[]> values=(ArrayList<Object[]>)mapElement.getValue();
		            count++;
					%>
					<tr>
						<td class="std" style="text-align: center;" > <%=count%></td>
						<td  class="std" style="text-align: left;" >
							
							<%	int count1=0;
								for(Object obj[]:values){
									 count1++; %>
									<%if(count1==1 ){ %>
										<%if(obj[3]!=null){ %> <%= obj[3].toString()%><%}else{ %> - <%} %>
									<%}else if(count1==values.size() ){ %>
										<%if(obj[3]!=null){ %> <br> - <br> <%= obj[3].toString()%> <%}else{ %> - <%} %>
									<%} %>
							<%} %>
						</td>
						<td  class="std" style="padding-left: 5px;padding-right: 5px;text-align: justify;"><%= values.get(0)[1]!=null?values.get(0)[1].toString(): " - "  %></td>
						<td  class="std" style="text-align: left;" >
							<%	int count2=0;
							for(Object obj[]:values){ %>
							<%if(obj[13]!=null){ %> <%= obj[13].toString()%>,&nbsp;<%=obj[14]!=null?obj[14].toString(): " - " %>
								<%if(count2>=0 && count2<values.size()-1){ %>
								,&nbsp;
								<%} %>
							<%}else{ %> - <%} %>
							<%count2++;}%>
						</td>                       						
						<td  class="std" style="text-align: justify;"><%if( values.get(0)[5]!=null){ %> <%=sdf.format(sdf1.parse(values.get(0)[5].toString()))%> <%}else{ %> - <%} %></td>
					</tr>				
				<% } %>
			</tbody>
		</table>
	</div>
	<br>
	<%} %>
</div> 
</body>
</html>