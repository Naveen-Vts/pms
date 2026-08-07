<%@page import="lombok.val"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="java.time.LocalDate"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="java.util.*"%>
<%@page import="com.vts.pfms.model.LabMaster"%>
<!DOCTYPE html>
<html>
<head>


<%
String seslabid=(String)session.getAttribute("labid");
	List<Object[]> speclists = (List<Object[]>) request.getAttribute("committeeminutesspeclist");
	Object[] committeescheduleeditdata = (Object[]) request.getAttribute("committeescheduleeditdata");
	List<Object[]> committeeminutes = (List<Object[]>) request.getAttribute("committeeminutes");
	List<Object[]> committeeminutessub = (List<Object[]>) request.getAttribute("committeeminutessub");
	List<Object[]> agendas = (List<Object[]>) request.getAttribute("CommitteeAgendaList");
	List<Object[]> invitedlist = (List<Object[]>) request.getAttribute("committeeinvitedlist");
	Object[] labdetails = (Object[]) request.getAttribute("labdetails");
	HashMap< String, ArrayList<Object[]>> actionlist = (HashMap< String, ArrayList<Object[]>>) request.getAttribute("actionlist");
	Object[] projectdetails=(Object[])request.getAttribute("projectdetails");
	Object[] divisiondetails=(Object[])request.getAttribute("divisiondetails");
	Object[] initiationdetails=(Object[])request.getAttribute("initiationdetails");
	String projectid= committeescheduleeditdata[9].toString();
	String divisionid= committeescheduleeditdata[16].toString();
	String initiationid= committeescheduleeditdata[17].toString();
	String lablogo=(String)request.getAttribute("lablogo");
	LabMaster labInfo=(LabMaster)request.getAttribute("labInfo");
	List<String> repCodes = (List<String> )request.getAttribute("repCodes");
	int meetingcount= (int) request.getAttribute("meetingcount");
	
	Object[] membersec=null;
	
	FormatConverter fc=new FormatConverter(); 
	SimpleDateFormat sdf=fc.getRegularDateFormat();
	SimpleDateFormat sdf1=fc.getSqlDateFormat(); int addcount=0; 
	String isprint=(String)request.getAttribute("isprint");
	String[] no=committeescheduleeditdata[11].toString().split("/");
	
	List<Object[]>ActionDetails=(List<Object[]>)request.getAttribute("ActionDetails");
	Map<Integer,Integer>committeeCountMap=(Map<Integer,Integer>)request.getAttribute("committeeCountMap");
	
	String ccmFlag = (String)request.getAttribute("ccmFlag");
	%>
<style type="text/css">


p{
  text-align: justify;
  text-justify: inter-word;
}

th,td
{
	word-break: normal ;
}
.break
{
	page-break-after: always;
} 
	
#pageborder
{
      position:fixed;
      left: 0;
      right: 0;
      top: 0;
      bottom: 0;
      border: 2px solid black;
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
             margin-right: auto;
             font-size: 13px;
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
            font-size: 13px;
          	margin-top: 30px;
            margin-left: 10px;
            content: "<%=no[0]!=null?no[0].toString(): " - "%>/<%=no[1]!=null?no[1].toString(): " - "%>/<%=no[2]!=null?no[2].toString(): " - " %><%if(meetingcount>0){ %>#<%=meetingcount %><%} %>/<%=no[3]!=null?no[3].toString(): " - "%>";
          }            
          
          @top-center { 
            font-size: 13px;
          margin-top: 30px;
          content: "<%=committeescheduleeditdata[15]!=null?committeescheduleeditdata[15].toString(): " - "%>"; 
          
          }
         @bottom-center { 
             font-size: 13px;
	          margin-bottom: 30px;
	          content: "<%=committeescheduleeditdata[15]!=null?committeescheduleeditdata[1].toString(): " - "%>"; 
          
          } 
          
          @bottom-left { 
             font-size: 13px;
	          margin-bottom: 30px;
	          content: "<%=LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))%>"; 
	          
          
          } 
		
	<%--  	@left-bottom {           		
 			content : "The information in this Document is proprietary of <%=labInfo.getLabCode() %> /DRDO , MOD Govt. of India. Unauthorized possession may be liable for prosecution.";
            font-size: 10px;
		    writing-mode: vertical-rl;
       		text-orientation: sideways;
       		
       		
		    
          } 
           --%>


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
 .completed{
	color: green;
	font-weight: 700;
}

.briefactive{
	color: blue;
	font-weight: 700;
}

.inprogress{
	color: #F66B0E;
	font-weight: 700;
}

.assigned{
	color: brown;
	font-weight: 700;
}

.notyet{
	color: purple;
	font-weight: 700;
}

.notassign{
	color:#AB0072;
	font-weight: 700;
}

.ongoing{
	color: #F66B0E;
	font-weight: 700;
}

.completed{
	color: green;
	font-weight: 700;
}

.delay{
	color: maroon;
	font-weight: 700;
}

.completeddelay{
	color:#BABD42;
	font-weight: 700;
}

.inactive{
	color: red;
	font-weight: 700;
}
</style>
<meta charset="ISO-8859-1">
<title><%=committeescheduleeditdata[8]!=null?committeescheduleeditdata[8].toString(): " - " %> Minutes View</title>
</head>
<body>
	<div id="container pageborder" align="center"  class="firstpage" id="firstpage">
		<div class="firstpage" id="firstpage">		
			<br>
			<div style="text-align: center;" ><h1>MINUTES OF MEETING</h1></div>
			<br>
			<div style="text-align: center;" ><h2 style="margin-bottom: 2px;"><%=committeescheduleeditdata[7]!=null?committeescheduleeditdata[7].toString().toUpperCase():" - "%>  (<%=committeescheduleeditdata[8]!=null?committeescheduleeditdata[8].toString().toUpperCase():" - " %><%if(meetingcount>0){ %>&nbsp;&nbsp;#<%=meetingcount %><%} %>) </h2></div>				
					<%if(Integer.parseInt(projectid)>0){ %>					
					<h3 style="margin-top: 5px; margin-bottom: 5px">For</h3>  
				    <h2 style="margin-top: 3px">Project  : &nbsp;<%=projectdetails[1]!=null?projectdetails[1].toString(): " - "  %>  (<%=projectdetails[4]!=null?projectdetails[4].toString(): " - " %>)</h2>
				<%}else if(Integer.parseInt(divisionid)>0){ %>					
					<h3 style="margin-top: 5px; margin-bottom: 5px">For</h3>	  
			 	   	<h2 style="margin-top: 3px">Division :&nbsp;<%=divisiondetails[2]!=null?divisiondetails[2].toString(): " - "  %>  (<%=divisiondetails[1]!=null?divisiondetails[1].toString(): " - " %>)</h2>
				<%}else if(Integer.parseInt(initiationid)>0){ %>					
					<h3 style="margin-top: 5px; margin-bottom: 5px">For</h3>	  
				    <h2 style="margin-top: 3px">Pre-Project  : &nbsp;<%=initiationdetails[2]!=null?initiationdetails[2].toString(): " - "  %>  (<%=initiationdetails[1]!=null?initiationdetails[1].toString(): " - " %>)</h2>
				<%}else{%>
					<br><br><br><br><br>
				<%} %>
				<br>
				<table style="align: center; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; max-width: 650px; font-size: 16px"  border="0">
					<tr style="margin-top: 10px">
						 <th  style="text-align: center; width: 650px;font-size: 20px "> <u>Meeting Id </u> </th></tr><tr>
						 <th  style="text-align: center;  width: 650px;font-size: 20px  "> <%=committeescheduleeditdata[11]!=null?committeescheduleeditdata[11].toString(): " - "  %> </th>				
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
				 <td  style="text-align: center;  width: 650px;font-size: 20px ;padding-top: 5px "> <b><%=committeescheduleeditdata[3]!=null?committeescheduleeditdata[3].toString(): " - " %></b></td>
			 </tr>
			 
		 </table>
		 
		 <table style="align: center; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; max-width: 650px; font-size: 16px"  border="0">
					<tr style="margin-top: 10px">
						 <th  style="text-align: center; width: 650px;font-size: 20px "> <u>Meeting Venue</u> </th></tr><tr>
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
		
		<table style="margin-top: 0px;;width: 650px; font-size: 16px; border-collapse: collapse;">
			<tbody>
				<tr>
					<th colspan="8" style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=committeemin[0]!=null?committeemin[0].toString(): " - "%>.&nbsp;&nbsp;&nbsp;<%=committeemin[1]!=null?committeemin[1].toString(): " - "%></th>
				</tr>
				<tr>
						<%
							int count = 0;

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

						<%
							}
						%>

				
				</tr>
				</table>
				</div>
				<!-- <div class="break"></div> -->
	<!-- ------------------------- agenda---------------------------- -->
				<%
					} 
			else if (committeemin[0].toString().equals("3") ) 
			{						
				%>
				<%if(ccmFlag==null) {%>
						<div align="center">
							<table style="margin-top: 0px;; width: 650px; font-size: 16px; border-collapse: collapse;">
								<tr>
									<th colspan="8" style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=committeemin[0]!=null?committeemin[0].toString(): " - "%>.&nbsp;&nbsp;&nbsp;<%=committeemin[1]!=null?committeemin[1].toString(): " - "%></th>
								</tr>
							</table>	
								
					
						<%if(agendas.size()!=0)
						{
							int agendaid = 1;
							for (Object[] agenda : agendas) 
							{	int brcount=0;							
						%>
								    <table style="margin-top: 00px; width: 660px; font-size: 16px; border-collapse: collapse;">
										<tr>								
											<td colspan="8" style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=committeemin[0]!=null?committeemin[0].toString(): " - "%><%="."+agendaid%>.&nbsp;&nbsp;&nbsp;<%=agenda[3]!=null?agenda[3].toString(): " - "%></td>
										</tr>
								<%
									int index = 1;
									for (Object[] minssub : committeeminutessub) 
									{
								%>				
										<tr >
											<th colspan="8" style="text-align: left;padding:0px; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=committeemin[0]!=null?committeemin[0].toString(): " - "%><%="."+agendaid+"."+index%>.&nbsp;&nbsp;&nbsp;<%=minssub[1]!=null?minssub[1].toString(): " - "%></th>
										</tr>								
								<%
										int count=0;
										int count1=0;
										int index1=0;
										for (Object[] speclist : speclists) 
										{
											if (agenda[0].toString().equals(speclist[6].toString()) && minssub[0].toString().equals(speclist[5].toString())) 
											{
												count++; 
													index1++;						
											%>						
																			
												<%	if(speclist[5].toString().equals("7") )
												{%>	
												 <tr >
														<th colspan="8" style="text-align: left;padding:0px; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=committeemin[0]!=null?committeemin[0].toString(): " - "%> <%="."+agendaid+"."+index+"."+index1%>.&nbsp;&nbsp;&nbsp;<%=speclist[11]!=null?speclist[11].toString(): " - "%></th>
												</tr> 
												<tr >
													<td style="text-align: left; padding:0px 0px 0px 30px;">
														<%=speclist[1]!=null?speclist[1].toString(): " - "%>
													</td>
												</tr>					
												<%}else if(speclist[5].toString().equals("8")){
												%>
												 <tr >
														<th colspan="8" style="text-align: left;padding:0px; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=committeemin[0]!=null?committeemin[0].toString(): " - "%> <%="."+agendaid+"."+index+"."+index1%>.&nbsp;&nbsp;&nbsp;<%=speclist[11]!=null?speclist[11].toString(): " - "%></th>
												</tr> 
												<tr >
													<td style="text-align: left;padding:0px 0px 0px 30px;">
														<%=speclist[1]!=null?speclist[1].toString(): " - "%>
													</td>	
												</tr>	
												<%}else if(speclist[5].toString().equals("9")){
												%>
													<tr >
														<th colspan="8" style="text-align: left;padding:0px; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=committeemin[0]!=null?committeemin[0].toString(): " - "%> <%="."+agendaid+"."+index+"."+index1%>.&nbsp;&nbsp;&nbsp;<%=speclist[9]!=null?speclist[9].toString(): " - "%></th>
													</tr>	
													<tr >
														<td style="text-align: left;padding:0px 0px 0px 30px;">
															<%=speclist[1]!=null?speclist[1].toString(): " - "%>
														</td>	
													</tr>	
																						
												<%}
											
											}
										}
									if (count == 0)
									{%>
									<tr style="page-break-after: ;">
									<td style="text-align: left;"><div style="padding-left: 30px"><p>NIL</p></div>
									</td>	
									</tr>								
									<%}%>
									
								
								
								<%index++;
								} 
								agendaid++;
								
							}%>
							
							  
						
						<%}else{%>
							<tr>
							<td style="text-align: left;"><div style="padding-left: 30px"><p>NIL</p></div>
							</td>
							</tr>
							
						<%}%>
						</table>
					</div>
					<%} %>
				<!-- <div class="break"></div>  -->
	<!-- ----------------------------------------------agenda end------------------------------------------- -->
				
			<%}else if ((ccmFlag!=null && committeemin[0].toString().equals("4")) || (ccmFlag==null && (committeemin[0].toString().equals("4") || committeemin[0].toString().equals("5") || committeemin[0].toString().equals("6")))) { %>
			<div align="center">
				<table style="margin-top: 0px;; width: 650px; font-size: 16px; border-collapse: collapse;">
					<tbody>
						
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=committeemin[0]!=null?committeemin[0].toString(): " - "%>.&nbsp;&nbsp;&nbsp;<%=committeemin[1]!=null?committeemin[1].toString(): " - "%></th>
						</tr>
				
						<%
						int count = 0;
						
						for (Object[] speclist : speclists)
						{
							if (speclist[3].toString().equals(committeemin[0].toString())) 
							{
								count++;%>						
							 	
							<%	if(speclist[3].toString().equals("4") )
								{%>	
									<tr>
										<td style="text-align: justify;padding-left: 30px">
											<%=speclist[1]!=null?speclist[1].toString(): " - "%>
										</td>
									</tr>					
								<%}else if(speclist[3].toString().equals("5")){
								%>
								<tr>
									<th colspan="8" style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=committeemin[0]!=null?committeemin[0].toString(): " - "%> <%="."+count%>.&nbsp;&nbsp;&nbsp;<%=speclist[9]!=null?speclist[9].toString(): " - "%></th>
								</tr>
								<tr>
									<td style="text-align: justify;padding-left: 30px">
										<%=speclist[1]!=null?speclist[1].toString(): " - "%>
									</td>
								</tr>	
									
									<%}else if(speclist[3].toString().equals("6")){
								%>
							
									<tr>
										<td style="text-align: justify;padding-left: 30px">
											<%=speclist[1]!=null?speclist[1].toString(): " - "%>
										</td>
									</tr>							
								<%}
							}							
							
						}if (count == 0 && ccmFlag==null)
						{%>
						<tr style="page-break-after: ;">
						<td style="text-align: left;"><div style="padding-left: 50px"><p>NIL</p></div>
						</td>	
						</tr>								
						<%}%>
				
				</table>
			</div>
	<%}
	}%>
	<div align="center" >
		<div style="width: 650px;margin-left: 15px; font-size: 16px; " >
			<div align="center" style="padding-left: 2.5rem;">
				<p>These Minutes are issued with the approval of The Chairperson <%-- <%=committeescheduleeditdata[8] %> --%>. </p>
			</div>
			<div align="left" style="padding-right: 1.5rem;padding-bottom: 5rem;">
				<br>Date :&emsp;&emsp;&emsp;&emsp;&emsp;  <br>Time :&emsp;&emsp;&emsp;&emsp;&emsp;
				<div align="right" style="padding-right: 1.5rem;padding-bottom: 0rem;">
				<br><%if(membersec!=null){%><%= membersec[6].toString() %><%} %>
				<br>(Member Secretary)
			</div>
			</div>
			<%if(membersec!=null){ %>
			
			<%} %>
			<div align="left" ><b>NOTE : </b>Action item details are enclosed as Annexure - AI.</div>
		</div>
		
	</div>
	
  
		<%if( isprint.equals("N")){ %>	
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
						<td class="std" > <%=count%></td>
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
						<%count2++;} %>
						</td>                       						
						<td  class="std" style="text-align: justify;"><%if( values.get(0)[5]!=null){ %> <%=sdf.format(sdf1.parse(values.get(0)[5].toString()))%> <%}else{ %> - <%} %></td>
					</tr>				
				<% } %>
			</tbody>
		</table>
	</div>
	<br>	
	
	<%}%>
							<%if(ccmFlag==null) {%>
							<table style="margin-left:10px;margin-top: 0px;  width: 700px; font-size: 16px; border-collapse: collapse;">
								<tr>
									<th colspan="8" style="text-align: left; font-weight: 700;"><br>7.&nbsp;&nbsp;Action Points of Previous Review:&nbsp; &nbsp;</th>
								</tr>
							</table>	
   				
							<table style="margin-left:10px; width: 690px; margin-top:5px;font-size: 16px; border-collapse: collapse;border: 1px solid black" >
								<thead >
									<tr>
										<td colspan="6" style="border: 0px !important;">
											<p style="font-size: 10px; text-align: center">
											<span class="assigned">AA</span> : Activity Assigned &nbsp;&nbsp;
												<span class="delay">DO</span> : Delay - On Going &nbsp;&nbsp; 
												<span class="ongoing">RC</span> : Review & Close &nbsp;&nbsp; 
												<span class="delay">FD</span> : Forwarded With Delay &nbsp;&nbsp; 
												<span class="completed">CO</span> :Completed &nbsp;&nbsp; 
												<span class="completeddelay">CD</span> : Completed with Delay &nbsp;&nbsp; 
											</p>
										</td>									
									</tr>
									<tr>
										<th class="std"  style="width: 20px;"  >SN</th>
										<th class="std"  style="width: 100px; " > ID</th>
										<th class="std"  style="width: 300px;" >Action Point</th>
										<th class="std"  style="width: 100px; " > PDC</th>
										<th class="std"  style="width: 100px;" >Responsibility</th>
										<th class="std"  style="width: 70px;"  >Status<!-- (DD) --></th>			
									</tr>
								</thead>
								<tbody>
								<%if(ActionDetails.size()>0) {%>
								<%int count=0;String key="";;
								for(Object[]obj:ActionDetails){ %>
								<tr>
								<td class="std" align="center" style="padding:5px;"><%=++count %></td>
								<td class="std" >
								<%for(Map.Entry<Integer, Integer>entry:committeeCountMap.entrySet()){ 
								if(entry.getValue()==Integer.parseInt(obj[7].toString())){
								key=entry.getKey().toString();
								}} %>
								<b style="font-size: 12px;"><%=committeescheduleeditdata[8]!=null?committeescheduleeditdata[8].toString(): " - " %>/<%=key%>/<%=obj[1]!=null?obj[1].toString().split("/")[3]:" - " %></b>
								</td>
								<td class="std" style="text-align: justify;padding:2px">
								<%=obj[5]!=null?obj[5].toString(): " - " %>
								</td>
								<td class="std"><%=sdf.format(sdf1.parse(obj[10].toString())) %></td>
								<td class="std" style="font-size: 14px;"><%=obj[2]!=null?obj[2].toString(): " - " %></td>
								<td class="std">
								<%if(obj[4]!= null){ %> 
													<%	String actionstatus = obj[3].toString();
														int progress = obj[13]!=null ? Integer.parseInt(obj[13].toString()) : 0;
														LocalDate pdcorg = LocalDate.parse(obj[10].toString());
														LocalDate lastdate = obj[14]!=null ? LocalDate.parse(obj[14].toString()): null;
														LocalDate today = LocalDate.now();
													%> 
													<% if(lastdate!=null && actionstatus.equalsIgnoreCase("C") ){%>
															<%if(actionstatus.equals("C") && (pdcorg.isAfter(lastdate) || pdcorg.equals(lastdate))){%>
																<span class="completed">CO</span>
															<%}else if(actionstatus.equals("C") && pdcorg.isBefore(lastdate)){ %>	
																<span class="completeddelay">CD <%-- (<%= ChronoUnit.DAYS.between(pdcorg, lastdate) %>)  --%></span>
															<%} %>	
														<%}else{ %>
															<%if(actionstatus.equals("F")  && (pdcorg.isAfter(lastdate) || pdcorg.isEqual(lastdate) )){ %>
																<span class="ongoing">RC</span>												
															<%}else if(actionstatus.equals("F")  && pdcorg.isBefore(lastdate)) { %>
																<span class="delay">FD</span>
															<%}else if((pdcorg.isAfter(today) || pdcorg.isEqual(today)) && progress>0){  %>
																<span class="ongoing">OG</span>
															<%}else if(pdcorg.isBefore(today) && progress>0){  %>
																<span class="delay">DO <%-- (<%= ChronoUnit.DAYS.between(pdcorg, today)  %>)  --%> </span>
															<%}else if( progress==0) {%>
															<span class="assigned">AA</span>
															<%} %>									
													<%} %>
												<%}else { %>
													<span class="notassign">NA</span>
												<%} %> 
								
								
								
								</td>
								</tr>										
								<% }%>	
									<%}else{ %>
									<tr>
									<td colspan="5" style="text-align: center;">No Data Available </td>
									</tr>
									<%} %>	
								</tbody>		
							</table>
						<%} %>
</div>
</body>
</html>

