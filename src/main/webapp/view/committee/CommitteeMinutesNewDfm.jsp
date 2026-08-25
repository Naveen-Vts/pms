<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.time.temporal.ChronoUnit"%>
<%@page import="java.time.LocalDate"%>
<%@page import="com.vts.pfms.model.TotalDemand"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="java.text.Format"%>
<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@page import="com.vts.pfms.master.dto.ProjectFinancialDetails"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="com.vts.pfms.model.LabMaster"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" import="java.util.*"%>
<!DOCTYPE html>
<html>
<head>


<%
	HashMap< String, ArrayList<Object[]>> actionlist = (HashMap< String, ArrayList<Object[]>>) request.getAttribute("actionlist");
	
	
	List<Object[]> speclists = (List<Object[]>) request.getAttribute("committeeminutesspeclist");
	List<Object[]> committeeminutes = (List<Object[]>) request.getAttribute("committeeminutes");
	List<Object[]> invitedlist = (List<Object[]>) request.getAttribute("committeeinvitedlist");
	List<ProjectFinancialDetails> projectFinancialDetails =(List<ProjectFinancialDetails>)request.getAttribute("financialDetails");
	List<Object[]> procurementOnDemand = (List<Object[]>)request.getAttribute("procurementOnDemand");
	List<Object[]> procurementOnSanction = (List<Object[]>)request.getAttribute("procurementOnSanction");
	List<Object[]> ActionPlanSixMonths = (List<Object[]>)request.getAttribute("ActionPlanSixMonths");
	List<Object[]> lastpmrcactions = (List<Object[]>)request.getAttribute("lastpmrcactions");
	List<TotalDemand> totalprocurementdetails = (List<TotalDemand>)request.getAttribute("TotalProcurementDetails");
	List<Object[]> MilestoneDetails6 = (List<Object[]>)request.getAttribute("milestonedatalevel6");


	Object[] committeescheduleeditdata = (Object[]) request.getAttribute("committeescheduleeditdata");
	Object[] labdetails = (Object[]) request.getAttribute("labdetails");
	Object[] projectdetails=(Object[])request.getAttribute("projectdetails");
	Object[] divisiondetails=(Object[])request.getAttribute("divisiondetails");
	Object[] initiationdetails=(Object[])request.getAttribute("initiationdetails");
	LabMaster labInfo=(LabMaster)request.getAttribute("labInfo");
	List<String> repCodes = (List<String> )request.getAttribute("repCodes");
	
	DecimalFormat df=new DecimalFormat("####################.##");
	int meetingcount= (int) request.getAttribute("meetingcount");
	FormatConverter fc=new FormatConverter(); 
	SimpleDateFormat sdf3=fc.getRegularDateFormat();
	SimpleDateFormat sdf=fc.getRegularDateFormatshort();
	SimpleDateFormat sdf1=fc.getSqlDateFormat(); int addcount=0; 
	String isprint=(String)request.getAttribute("isprint"); 
	Format format = com.ibm.icu.text.NumberFormat.getCurrencyInstance(new Locale("en", "in"));
	String projectid= committeescheduleeditdata[9].toString();
	String divisionid= committeescheduleeditdata[16].toString();
	String initiationid= committeescheduleeditdata[17].toString();
	String lablogo=(String)request.getAttribute("lablogo");
	/* String committeeid1=committeescheduleeditdata[0].toString(); */
	
	String[] no=committeescheduleeditdata[11].toString().split("/");
	Object[] membersec=null; 
	String levelid= (String) request.getAttribute("levelid");
	
	
	%>
<style type="text/css">


.break
	{
		page-break-after: always;
		margin: 25px 0px 25px 0px;
	} 
	 
	
 #pageborder {
      position:fixed;
      left: 0;
      right: 0;
      top: 0;
      bottom: 0;
      border: 2px solid black;
    }     
 
@page {             
          size: 1120px 790px; 
          margin-top: 49px;
          margin-left: 72px;
          margin-right: 39px;
          margin-buttom: 49px; 	
          border: 1px solid black; 
          padding-top: 15px;   
          @bottom-right {          		
             content: "Page " counter(page) " of " counter(pages);
             margin-bottom: 30px;
             margin-right: 10px;
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
	          content: "<%=committeescheduleeditdata[15]!=null?committeescheduleeditdata[15].toString(): " - "%>"; 
          
          } 
          
          @bottom-left { 
             font-size: 13px;
	          margin-bottom: 30px;
	          content: "<%=LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))%>"; 
	          
          
          } 
          
         <%--  @bottom-left {          		
        
             content : "The information in this Document is proprietary of <%=labInfo.getLabCode() %> /DRDO , MOD Government of India. Unauthorized possession/use is violating the Government procedure which may be liable for prosecution. ";
        
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
 	
 	border: 1px solid black;
 	padding: 3px 2px 2px 2px; 
 	
 }
 
 .pname
{
	margin: 10px 0px 10px 20px;
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

 
.executive{
	align-items: center;
} 
 
</style>
<meta charset="ISO-8859-1">
<title><%=committeescheduleeditdata[8]!=null?committeescheduleeditdata[8].toString(): " - "%> Minutes View</title>
</head>
<body>
	<div id="container pageborder" align="center"  class="firstpage" id="firstpage">
	
		  <div class="firstpage" id="firstpage"> 	
			<br>
			<div align="center" ><h1>MINUTES OF MEETING</h1></div>
			<br>
			<div align="center" ><h2 style="margin-bottom: 2px;"><%=committeescheduleeditdata[7]!=null?committeescheduleeditdata[7].toString().toUpperCase():" - "%>  (<%=committeescheduleeditdata[8]!=null?committeescheduleeditdata[8].toString().toUpperCase():" - " %><%if(meetingcount>0){ %>&nbsp;&nbsp;#<%=meetingcount %><%} %>) </h2></div>				
				<%if(Integer.parseInt(projectid)>0){ %>					
					<h3 style="margin-top: 5px; margin-bottom: 5px">For</h3>	  
				    <h2 style="margin-top: 1px">Project  : &nbsp;<%=projectdetails[1]!=null?projectdetails[1].toString(): " - " %>  (<%=projectdetails[4]!=null?projectdetails[4].toString(): " - "%>)</h2>
				<%}else if(Integer.parseInt(divisionid)>0){ %>					
					<h3 style="margin-top: 5px; margin-bottom: 5px">For</h3>	  
			 	   	<h2 style="margin-top: 1px">Division :&nbsp;<%=divisiondetails[2]!=null?divisiondetails[2].toString(): " - " %>  (<%=divisiondetails[1]!=null?divisiondetails[1].toString(): " - "%>)</h2>
				<%}else if(Integer.parseInt(initiationid)>0){ %>					
					<h3 style="margin-top: 5px; margin-bottom: 5px">For</h3>	  
				    <h2 style="margin-top: 1px">Pre-Project  : &nbsp;<%=initiationdetails[2]!=null?initiationdetails[2].toString(): " - " %>  (<%=initiationdetails[1]!=null?initiationdetails[1].toString(): " - "%>)</h2>
				<%}else{%>
					<br><br><br><br><br>
				<%} %>
				<br>
			<figure><img style="width: 4cm; height: 4cm"  src="data:image/png;base64,<%=lablogo%>"></figure>   
						<br><br><br>
			<div align="center" ><h3><%=labdetails[2]!=null?labdetails[2].toString(): " - " %> (<%=labdetails[1]!=null?labdetails[1].toString(): " - "%>)</h3></div>
			
			<div align="center" ><h3><%=labdetails[4]!=null?labdetails[4].toString(): " - " %>, &nbsp;<%=labdetails[5]!=null?labdetails[5].toString(): " - " %>, &nbsp;<%=labdetails[6]!=null?labdetails[6].toString(): " - " %></h3></div>
		
		      <br><br><br><br><br>
		
			<table style="align: center; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; max-width: 975px; font-size: 16px"  border="0">
					<tr style="margin-top: 10px">
						 <th  style="text-align: center; width: 975px;font-size: 20px "> <u>Meeting Id </u> </th></tr><tr>
						 <th  style="text-align: center;  width: 975px;font-size: 20px  "> <%=committeescheduleeditdata[11]!=null?committeescheduleeditdata[11].toString(): " - " %> </th>				
					 </tr>
				</table>
				
				<br><br>
		 <table style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; max-width: 975px; font-size: 16px"  border="0">
			 <tr>
				 <th  style="text-align: center; width: 975px;font-size: 20px "> <u> Meeting Date </u></th>
				 <th  style="text-align: center;  width: 975px;font-size: 20px  "><u> Meeting Time </u></th>
			 </tr>
			
			 <tr>
				 <td  style="text-align: center;  width: 975px;font-size: 20px ;padding-top: 5px"> <b><%=sdf3.format(sdf1.parse(committeescheduleeditdata[2].toString()))%></b></td>
				 <td  style="text-align: center;  width: 975px;font-size: 20px ;padding-top: 5px "> <b><%=committeescheduleeditdata[3]!=null?committeescheduleeditdata[3].toString(): " - " %></b></td>
			 </tr>
			 
		 </table>
		 <br><br>
		 <table style="align: center; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; max-width: 975px; font-size: 16px"  border="0">
					<tr style="margin-top: 10px">
						 <th  style="text-align: center; width: 975px;font-size: 20px "> <u>Meeting Venue</u> </th></tr><tr>
						 <th  style="text-align: center;  width: 975px;font-size: 20px  "> <% if(committeescheduleeditdata[12]!=null){ %><%=committeescheduleeditdata[12].toString() %> <%}else{ %> - <%} %></th>				
					 </tr>
				</table>
		
		
		</div>  
				

  <h1 class="break"></h1> 
<!-- ------------------------------------------------------- members --------------------------------- -->
<%-- 	<div align="center">
		<table style="align: center; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; max-width: 650px; font-size: 16px">
			<tr style="margin-top: 10px">
				<td  style="text-align: left; width: 650px;font-size: 20px; padding-left: 15px;"> Record/ File no __________dated___________  </td></tr><tr>
				<th  style="text-align: center;  width: 650px;font-size: 20px;padding-top: 10px; ">
					Minutes of  Apex Board/ Executive Board/ PMRB Meeting for Project titled 
				"<span style=" text-decoration: underline;"><%=projectdetails[1] %>  (<%=projectdetails[4]%>)</span>" held on <%=sdf.format(sdf1.parse(committeescheduleeditdata[2].toString()))%> at  <% if(committeescheduleeditdata[12]!=null){ %><%=committeescheduleeditdata[12] %> <%}else{ %> - <%} %>
				</th>				
			</tr>
		</table>
	
	</div> --%>


<%if(invitedlist.size()>0){ %>
<% ArrayList<String> membertypes=new ArrayList<String>(Arrays.asList("CC","CS","PS","CI","CW","CO"));
//ArrayList<String> addlmembertypes=new ArrayList<String>(Arrays.asList("W","E","I","P")); %>
if(repCodes != null) membertypes.addAll(repCodes);
<% 
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
<table style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; width: 975px; font-size: 16px; border-collapse:collapse;" >	
	
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
	 	 <td style="border: 1px solid black; padding: 7px;text-align: center"><%=j%> </td>
	 	  	<td style="border: 1px solid black; padding: 5px;text-align: left">  
	 	  	
	 			<%= invitedlist.get(i)[6]!=null?invitedlist.get(i)[6].toString(): " - "%>,&nbsp;<%=invitedlist.get(i)[7]!=null?invitedlist.get(i)[7].toString(): " - "%>  
		 	</td>
		 	<td style="border: 1px solid black; padding: 5px;text-align: left;">  
	 	  	
	 			<%= invitedlist.get(i)[11]!=null?invitedlist.get(i)[11].toString(): " - "%>  
		 	</td>	
		 	<td style="border: 1px solid black;padding: 5px ;text-align: left">
		 		<%  if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CC")) {		 %>Chairperson<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CS") ){ membersec=invitedlist.get(i); %> Member Secretary<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("PS") ) { %>Member Secretary&nbsp;(Proxy) <%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CI")){   %>Internal<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CW")){	 %>External<%-- &nbsp;(<%=invitedlist.get(i)[11] %>) --%><%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CO")){	 %>External<%-- &nbsp;(<%=invitedlist.get(i)[11]%>) --%><%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("P") ){	 %>Presenter <%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("I")){	 %>Internal<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("W") ){	 %>External<%-- &nbsp;(<%=invitedlist.get(i)[11] %>) --%><%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("E") )    {%>External<%-- &nbsp;(<%=invitedlist.get(i)[11] %>) --%><%}
					else {%> REP_<%=invitedlist.get(i)[3]!=null?invitedlist.get(i)[3].toString(): " - "%><%-- &nbsp; (<%=invitedlist.get(i)[11] %>) --%>  <%}
				%>
	 		</td>	
	 		</tr>
	 <%}
	 } %>
	 
	 <% } %>
	 
	 <%if(memAbscent > 0){ %>
	 	
	  	<tr >
			<th colspan="4" style="text-align: left; font-weight: 700; width: 975px;border: 1px solid black; padding: 5px; padding-left: 15px">Members Absent</th>
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
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("PS") ) { %>Member Secretary&nbsp;(Proxy) <%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CI")){   %>Internal<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CW")){	 %>External<%-- &nbsp;(<%=invitedlist.get(i)[11] %>) --%><%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CO")){	 %>External<%-- &nbsp;(<%=invitedlist.get(i)[11]%>) --%><%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("P") ){	 %>Presenter <%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("I")){	 %>Internal<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("W") ){	 %>External<%-- &nbsp;(<%=invitedlist.get(i)[11] %>) --%><%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("E") )    {%>External<%-- &nbsp;(<%=invitedlist.get(i)[11] %>) --%><%}
					else {%> REP_<%=invitedlist.get(i)[3]!=null?invitedlist.get(i)[3].toString(): " - "%><%-- &nbsp; (<%=invitedlist.get(i)[11] %>) --%>  <%}
				%>
	 		</td>	
	 	</tr>
	 	
	 <%}
	 } %>
	 
	 <%if(count==0){ %>
	 	<tr><th colspan="4" style="text-align:center; font-weight: 20; width: 975px;border: 1px solid black; padding: 5px; padding-left: 15px">Nil</th></tr>
	 <%} %>
	
	<%} %>
	
	 <%if(ParPresent > 0){ %>
	
	 <tr>
		 <th colspan="4" style="text-align: left; font-weight: 700; width: 975px;border: 1px solid black; padding: 5px; padding-left: 15px">Other Invitees&nbsp;/&nbsp;Participants </th>
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
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("PS") ) { %>Member Secretary&nbsp;(Proxy) <%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CI")){   %>Internal<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CW")){	 %>External<%-- &nbsp;(<%=invitedlist.get(i)[11] %>) --%><%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CO")){	 %>External<%-- &nbsp;(<%=invitedlist.get(i)[11]%>) --%><%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("P") ){	 %>Presenter <%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("I")){	 %>Internal<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("W") ){	 %>External<%-- &nbsp;(<%=invitedlist.get(i)[11] %>) --%><%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("E") )    {%>External<%-- &nbsp;(<%=invitedlist.get(i)[11] %>) --%><%}
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
		 	<tr><th colspan="4" style="text-align:center; font-weight: 20; width: 975px;border: 1px solid black; padding: 5px; padding-left: 15px">Nil</th> </tr>
	  <%}%>
	  <% } %>
	  
	  <%if(parAbscent > 0){ %>
	  
	 <tr >
			<th colspan="4" style="text-align: left; font-weight: 700; width: 975px;border: 1px solid black; padding: 5px; padding-left: 15px">Other Invitees&nbsp;/&nbsp;Participants Absent</th>
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
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("PS") ) { %>Member Secretary&nbsp;(Proxy) <%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CI")){   %>Internal<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CW")){	 %>External<%-- &nbsp;(<%=invitedlist.get(i)[11] %>) --%><%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CO")){	 %>External<%-- &nbsp;(<%=invitedlist.get(i)[11]%>) --%><%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("P") ){	 %>Presenter <%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("I")){	 %>Internal<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("W") ){	 %>External<%-- &nbsp;(<%=invitedlist.get(i)[11] %>) --%><%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("E") )    {%>External<%-- &nbsp;(<%=invitedlist.get(i)[11] %>) --%><%}
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
	 	<tr><th colspan="4" style="text-align:center; font-weight: 20; width: 975px;border: 1px solid black; padding: 5px; padding-left: 15px">Nil</th></tr>
	 <%} %>
	
	
	 <%} %>

	  
	 <tr> <td></td>	</tr>
</table>



</div>
<%} %>
	


 <h1 class="break" ></h1> 
 
<!-- -------------------------------------------------------members----------------------------- -->
		<% for (Object[] committeemin : committeeminutes) { %>
		<% if (committeemin[0].toString().equals("1") ) { %>
		
		<table style="margin-top: 0px; margin-left: 10px; width: 975px; font-size: 16px; border-collapse: collapse;">
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
				<br><br>
			<% }else if (committeemin[0].toString().equals("2")) { %>
		
		<table style="margin-top: 0px; margin-left: 10px; width: 975px; font-size: 16px; border-collapse: collapse;">
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
						{ %>
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
			  <br><br>
				<%  }else if (committeemin[0].toString().equals("3")) { %>
					<!-- <h1 class="break"></h1> --> 
						 
					<table style="margin-top: 0px; margin-left: 15px; width: 975px; font-size: 16px; border-collapse: collapse;">
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;">3 (a) Record of Discussions and Action Points of Current Meeting.</th>
						</tr>
						<tr>
							<td colspan="8" style="text-align: center ;padding: 5px;">Item Code/Type : A: Action, C: Comment, D: Decision, R: Recommendation</td>
						</tr>
					</table>	
					<table style="margin-top: 0px; margin-left: 10px;width: 975px; font-size: 16px; border-collapse: collapse ;border: 1px solid black ">
					<thead>
						<tr>
							<th  class="sth" style="text-align :center !important; max-width: 30px"> SN</th>
							<th  class="sth" style="text-align :center !important; max-width: 35px"> Type</th>
							<th  class="sth" style=" max-width: 600px"> Item</th>				
							<th  class="sth" style="width: 195px"> Remarks</th>	
						</tr>
					</thead>
					<tbody>
							<% int countcm=0;
							long tempagenda=0;
							for(int i=0;i<speclists.size();i++)
							{ 
								if(Integer.parseInt(speclists.get(i)[3].toString())==3||Integer.parseInt(speclists.get(i)[3].toString())==5){
								countcm++;
								
								%>
								
								<% if(tempagenda!=Long.parseLong(speclists.get(i)[6].toString())){%>
								<tr>
									<td class="std" style="text-align :center;border:1px solid black;"  colspan="4"><%=speclists.get(i)[10]!=null?speclists.get(i)[10].toString(): " - "%></td>
								</tr>
							<%tempagenda=Long.parseLong(speclists.get(i)[6].toString());
							} %>
							<tr>
								<td class="std" style="text-align :center !important;border:1px solid black;vertical-align: top;"  ><p  style="text-align :center !important; "> <%=countcm%> </p></td>
								<td class="std" style="text-align :center !important;border:1px solid black; padding: 5px 5px 5px 5px ; vertical-align: top;" >							
								<p  style="text-align :center !important; ">	<%=speclists.get(i)[7]!=null?speclists.get(i)[7].toString(): " - "%> 
								</p> 				
								</td>
								<td  class="std" style="border:1px solid black;padding:  5px 5px 5px 5px ;width: 600px;text-align: justify;"><%=speclists.get(i)[1]%></td>
								<td class="std" style="text-align :center;border:1px solid black;padding:  5px 5px 5px 5px;"  > <%if( speclists.get(i)[8]!=null && !speclists.get(i)[8].toString().equalsIgnoreCase("nil")){ %> <%= speclists.get(i)[8].toString()%> <%}else{ %> - <%} %></td>
							</tr>
							<%} 
							}%>
							<% if(countcm==0){%>
								<tr>
									<td class="std" style="text-align :center;border:1px solid black;"  colspan="4">No Minutes details Added</td>
								</tr>
							<%} %>
						</table>	
						 <h1 class="break"></h1> 
						 
							<table style="margin-top: 0px; margin-left: 10px; width: 975px; font-size: 16px; border-collapse: collapse;">
								<tr>
									<th colspan="8" style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=committeemin[0]!=null?committeemin[0].toString(): " - "%> (b)&nbsp;<%=committeemin[1]!=null?committeemin[1].toString(): " - "%></th>
								</tr>
							</table>	
   				
							<table style=" margin-left: 10px; width: 975px; font-size: 16px; border-collapse: collapse;border: 1px solid black" >
								<thead >
									<tr>
										<td colspan="6" style="border: 0px !important;">
											<p style="font-size: 10px;text-align: center"> 
												<span class="notassign">NA</span> : Not Assigned &nbsp;&nbsp;
												<span class="assigned">AA</span> : Activity Assigned &nbsp;&nbsp; 
												<span class="notyet">NS</span> : Not yet Started &nbsp;&nbsp;
												<span class="ongoing">OG</span> : On Going &nbsp;&nbsp; 
												<span class="delay">DO</span> : Delay - On Going &nbsp;&nbsp; 
												<span class="ongoing">RC</span> : Review & Close &nbsp;&nbsp;
												<span class="delay">FD</span> : Forwarded With Delay &nbsp;&nbsp;
												<span class="completed">CO</span> : Completed &nbsp;&nbsp; 
												<span class="completeddelay">CD</span> : Completed with Delay &nbsp;&nbsp; 
												<span class="inactive">IA</span> : InActive &nbsp;&nbsp;
												<!-- <span class="ongoing">UF</span> : User Forwarded &nbsp;&nbsp; --> 
											</p>
										</td>									
									</tr>
								
								
								<tr>
									<th class="std"  style="width: 20px;"  >SN</th>
									<th class="std"  style="width: 240px;" >Action Point</th>
									<th class="std"  style="width: 50px; "  > PDC</th>
									<th class="std"  style="width: 50px;" > Item Code</th>
									<th class="std"  style="width: 130px;" >Responsibility</th>
									<th class="std"  style="width: 80px;"  >Status(DD)</th>			
								</tr>
								</thead>		
								<tbody>
								<%if(lastpmrcactions.size()==0){ %>
									<tr><td class="std"  colspan="6" > No Data</td></tr>
								<%}
								else if(lastpmrcactions.size()>0)
								{
									int i=1;
									for(Object[] obj:lastpmrcactions){ %>
										<tr>
											<td class="std"  align="center"><%=i %></td>
											<td class="std" style="text-align: left;"><%=obj[2]!=null?obj[2].toString(): " - " %></td>
											<td class="std" align="center">
												<%if(obj[8]!= null){ %><%=sdf.format(sdf1.parse(obj[8].toString()))%><%} %>
												<%if(obj[7]!= null){ %><br><%=sdf.format(sdf1.parse(obj[7].toString()))%><%} %>
												<%if(obj[6]!= null){ %><br><%=sdf.format(sdf1.parse(obj[6].toString()))%><%} %>											
											</td>
											<td class="std" align="center" > 
												<%if(obj[3].toString().equals("A") ){ %>		 A
												<%}else if(obj[3].toString().equals("I") ){  %>  I
												<%}else if(obj[3].toString().equals("R") ){  %>  R
												<%}else if(obj[3].toString().equals("D") ){ %>   D
												<%}else if(obj[3].toString().equals("C") ){  %>  C
												<%}else if(obj[3].toString().equals("K") ){  %>  K
												<%} %>  
											</td> 
											<td class="std" style="text-align: left !important" >
												<%if(obj[4]!= null){ %>  
													<%=obj[12].toString()%>  <%-- (<%=obj[13] %>) --%>
												<%}else { %><span class="notassign">NA</span><%} %> 
											</td>
											<td class="std"  align="center"> 
												<%if(obj[4]!= null){
													if(obj[18]!=null && Integer.parseInt(obj[18].toString())>0){ %>
												  
												  
													<%if(!obj[10].toString().equals("C") && obj[16].toString().equals("F") && (LocalDate.parse(obj[6].toString()).isAfter(LocalDate.parse(obj[14].toString())) || LocalDate.parse(obj[6].toString()).isEqual(LocalDate.parse(obj[14].toString()))  )){ %>
														<span class="ongoing">UF</span>
													<%}else if(!obj[10].toString().equals("C") && obj[16].toString().equals("F") && LocalDate.parse(obj[6].toString()).isBefore(LocalDate.parse(obj[14].toString()))){  %>
														<span class="delay">FD</span>
													<%}else if(obj[10].toString().equals("C") && (LocalDate.parse(obj[6].toString()).isAfter(LocalDate.parse(obj[14].toString())) || LocalDate.parse(obj[6].toString()).isEqual(LocalDate.parse(obj[14].toString())) )){  %>
														<span class="completed">CO</span>
													<%}else if(obj[10].toString().equals("C") && LocalDate.parse(obj[6].toString()).isBefore(LocalDate.parse(obj[14].toString()))){  %>
													   <span class="completeddelay">CD (<%= ChronoUnit.DAYS.between(LocalDate.parse(obj[6].toString()), LocalDate.parse(obj[14].toString())) %>)  </span>
													<%}else if(!obj[10].toString().equals("C") && !obj[16].toString().equals("F") && (LocalDate.parse(obj[6].toString()).isAfter(LocalDate.now()) || LocalDate.parse(obj[6].toString()).isEqual(LocalDate.now()) )){  %> 
														<span class="ongoing">OG</span>
													<%}else if(!obj[10].toString().equals("C") && !obj[16].toString().equals("F") && LocalDate.parse(obj[6].toString()).isBefore(LocalDate.now())){  %> 
														<span class="delay">DO (<%= ChronoUnit.DAYS.between(LocalDate.parse(obj[6].toString()), LocalDate.now())  %>) </span>
													<%}else{ %>
														<span class="ongoing">OG</span>
													<%} 
													}else if(obj[18]!=null && obj[10]!=null && obj[10].toString().equals("C") && Integer.parseInt(obj[18].toString())>0){ %>
												        <span class="completed">CO</span>
												      <% }else{ %><span class="notyet">NS</span> 
												<%}}else { %> <span class="notassign">NA</span> <%} %> 
											</td>				
										</tr>			
									<%i++;
									}} %>
								</tbody>
							</table>
				<h1 class="break"></h1>
			<%}else if(committeemin[0].toString().equals("4") ){%>
				
					<table style="margin-top: -15px; margin-left: 10px; width: 975px; font-size: 16px; border-collapse: collapse;">
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=committeemin[0]!=null?committeemin[0].toString(): " - "%>.&nbsp;&nbsp;&nbsp;<%=committeemin[1]!=null?committeemin[1].toString(): " - "%></th>
						</tr>
					</table>	
					<table style="margin-top: 00px; margin-bottom: 0px; margin-left: 10px; width: 975px; font-size: 16px; border-collapse: collapse;border: 1px solid black" >
						     <thead>
									<tr>
										<td colspan="8" style="border: 0px">
											<p style="font-size: 10px;text-align: center"> 
														 <span class="notassign">NA</span> : Not Assigned &nbsp;&nbsp;
														 <span class="assigned">AA</span> : Activity Assigned &nbsp;&nbsp; 
														 <span class="notyet">NS</span> : Not yet Started &nbsp;&nbsp;
														 <span class="ongoing">OG</span> : On Going &nbsp;&nbsp; 
														 <span class="delay">DO</span> : Delay - On Going &nbsp;&nbsp; 
														 <span class="ongoing">RC</span> : Review & Close &nbsp;&nbsp;
														 <span class="delay">FD</span> : Forwarded With Delay &nbsp;&nbsp;
														 <span class="completed">CO</span> : Completed &nbsp;&nbsp; 
														 <span class="completeddelay">CD</span> : Completed with Delay &nbsp;&nbsp; 
														 <span class="inactive">IA</span> : InActive &nbsp;&nbsp;
														 <!-- <span class="ongoing">UF</span> : User Forwarded &nbsp;&nbsp; --> 
													 </p>
										</td>									
									</tr>
						     
						           <tr>
																 
										 <th class="std"  style="width: 30px !important; ">MS</th>
										 <th class="std"  style="width: 30px !important; ">L</th>
										 <th class="std" style="width: 180px; ">System/ Subsystem/ Activities</th>
										 <th class="std" style="width: 70px "> Original PDC</th>
										 <th class="std" style="width: 70px; "> Revised PDC</th>
										 <th class="std" style="width: 60px; "> Progress</th>
										 <th class="std" style="width: 50px; "> Status</th>
										 <th class="std" style="width: 120px; "> Remarks</th> 
										 
									</tr>
								</thead>
		
								<tbody>
									<% if(MilestoneDetails6 !=null){ if( MilestoneDetails6.size()>0){ 
									long milcount1=1;
									int milcountA=1;
									int milcountB=1;
									int milcountC=1;
									int milcountD=1;
									int milcountE=1;%>
									<%for(Object[] obj:MilestoneDetails6){
										
										if(Integer.parseInt(obj[21].toString())<= Integer.parseInt(levelid) ){
										%>
										<tr>
											<td class="std"  style="max-width: 50px;text-align: center;">M<%=obj[0]!=null?obj[0].toString(): " - " %></td>
											<td class="std"  style="max-width: 50px;text-align: center;" >
												<%
												
												if(obj[21].toString().equals("0")) {%>
													
												<%	milcountA=1;
													milcountB=1;
													milcountC=1;
													milcountD=1;
													milcountE=1;
												}else if(obj[21].toString().equals("1")) { %>
													A-<%=milcountA %>
												<% milcountA++;
													milcountB=1;
													milcountC=1;
													milcountD=1;
													milcountE=1;
												}else if(obj[21].toString().equals("2")) { %>
													B-<%=milcountB %>
												<%milcountB+=1;
												milcountC=1;
												milcountD=1;
												milcountE=1;
												}else if(obj[21].toString().equals("3")) { %>
													C-<%=milcountC %>
												<%milcountC+=1;
												milcountD=1;
												milcountE=1;
												}else if(obj[21].toString().equals("4")) { %>
													D-<%=milcountD %>
												<%
												milcountD+=1;
												milcountE=1;
												}else if(obj[21].toString().equals("5")) { %>
													E-<%=milcountE %>
												<%milcountE++;
												} %>
											</td>

											<td class="std"  style="max-width: 150px;text-align: left; <%if(obj[21].toString().equals("0")) {%>font-weight: bold;<%}%>">
												<%if(obj[21].toString().equals("0")) {%>
													<%=obj[10]!=null?obj[10].toString(): " - " %>
												<%}else if(obj[21].toString().equals("1")) { %>
													&nbsp;&nbsp;<%=obj[11]!=null?obj[11].toString(): " - " %>
												<%}else if(obj[21].toString().equals("2")) { %>
													&nbsp;&nbsp;<%=obj[12]!=null?obj[12].toString(): " - " %>
												<%}else if(obj[21].toString().equals("3")) { %>
													&nbsp;&nbsp;<%=obj[13]!=null?obj[13].toString(): " - " %>
												<%}else if(obj[21].toString().equals("4")) { %>
													&nbsp;&nbsp;<%=obj[14]!=null?obj[14].toString(): " - " %>
												<%}else if(obj[21].toString().equals("5")) { %>
													&nbsp;&nbsp;<%=obj[15]!=null?obj[15].toString(): " - " %>
												<%} %>
											</td>
											<td class="std"  style="max-width: 110px;text-align: center;"><%=obj[8]!=null?sdf.format(sdf1.parse(obj[8].toString())): " - "  %></td>
											<td class="std"  style="max-width: 110px;text-align: center;"><%=obj[9]!=null?sdf.format(sdf1.parse(obj[9].toString())): " - "  %></td>
											<td class="std"  style="max-width: 100px;text-align: center;"><%=obj[17]!=null?obj[17].toString(): " - " %>%</td>											
											<td class="std"  style="max-width: 70px;text-align: center;">
											<span class="<%if(obj[19].toString().equalsIgnoreCase("0")){%>assigned
														<%}else if(obj[19].toString().equalsIgnoreCase("1")) {%> notyet
														<%}else if(obj[19].toString().equalsIgnoreCase("2")) {%> ongoing
														<%}else if(obj[19].toString().equalsIgnoreCase("3")) {%> completed
														<%}else if(obj[19].toString().equalsIgnoreCase("4")) {%> delay 
														<%}else if(obj[19].toString().equalsIgnoreCase("5")) {%> completeddelay
														<%}else if(obj[19].toString().equalsIgnoreCase("6")) {%> inactive<%} %>	 " >
												<%=obj[22] %>	
											</span>
											
											</td>
											<td class="std"  style="max-width: 100px;text-align: left;"><%if(obj[23]!=null){%><%=obj[23].toString()%><%} %></td>
										</tr>
									<%milcount1++;}} %>
								<%} else{ %>
								<tr><td class="std" colspan="5" style="text-align: center;" > No SubSystems</td></tr>
								<%}}else{ %>
									<tr><td class="std" colspan="5" style="text-align: center;" > No SubSystems</td></tr>
								<%} %>
									
			
								<!-- New code by tharun end -->	
									

						</tbody>
					</table>
				 <h1 class="break"></h1> 
				
			<%}else if (committeemin[0].toString().equals("5") ){%>
			
					<table style="margin-top: 0px; margin-left: 10px; width: 975px; font-size: 16px; border-collapse: collapse;">
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=committeemin[0]!=null?committeemin[0].toString(): " - "%>.&nbsp;&nbsp;&nbsp;<%=committeemin[1]!=null?committeemin[1].toString(): " - "%></th>
						</tr>
					</table>	
					<table style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 10px; width: 975px;  border-collapse:collapse;" >
										<thead>
										 <tr>
										 	<th class="std" colspan="8" >Demand Details</th>
											 </tr>
										</thead>
										
										<tr>
											<th class="std" style="width: 30px !important;">SN</th>
											<th class="std" style="max-width: 90px;">Demand No</th>
											<th class="std" style="max-width: 90px; ">Demand Date</th>
											<th class="std" colspan="2" style="max-width: 150px;"> Nomenclature</th>
											<th class="std" style="max-width: 90px;"> Est. Cost-Lakh &#8377;</th>
											<th class="std" style="max-width: 80px; "> Status</th>
											<th class="std" style="max-width: 200px;">Remarks</th>
										</tr>
										    <% int k=0;
										    if(procurementOnDemand!=null &&  procurementOnDemand.size()>0){
										    Double estcost=0.0;
										    Double socost=0.0;
										    for(Object[] obj : procurementOnDemand){ 
										    	k++; %>
											<tr>
												<td class="std" ><%=k%></td>
												<td class="std" ><%=obj[1]!=null?obj[1].toString(): " - "%></td>
												<td class="std" ><%=obj[3]!=null?sdf.format(sdf1.parse(obj[3].toString())): " - " %></td>
												<td class="std" colspan="2" ><%=obj[8]!=null?obj[8].toString(): " - "%></td>
												<td class="std" style=" text-align:right;"> <%=obj[5]!=null?format.format(new BigDecimal(obj[5].toString())).substring(1): " - " %></td>
												<td class="std" > <%=obj[10]!=null?obj[10].toString(): " - "%> </td>
												<td class="std" ><%=obj[11]!=null?obj[11].toString(): " - "%> </td>		
											</tr>		
											<%
											estcost += Double.parseDouble(obj[5].toString());
										    }%>
										    
										    <tr>
										    	<td class="std" colspan="5" style="text-align: right;"><b>Total</b></td>
										    	<td class="std" style="text-align: right;"><b><%=estcost!=null?df.format(estcost): " - " %></b></td>
										    	
										    	<td class="std" colspan="2" style="text-align: right;"></td>

										    </tr>
										    
										    
										    <% }else{%>											
												<tr><td colspan="8" class="std" style="text-align: center;">Nil </td></tr>
											<%} %>
											 <tr >
												 <th  class="std"  colspan="8">Order Placed</th>
											 </tr>
										
										  	 	<tr>	
										  	 	 <th class="std" rowspan="2" style="width: 30px !important;">SN</th>
										  	 	 <th class="std">Demand No </th>
										  	 	  <th class="std" >Demand  Date</th>
												 <th class="std" colspan="2"> Nomenclature</th>
												 <th class="std" > Est. Cost-Lakh &#8377;</th>
												 <th class="std" style="max-width: 80px; "> Status</th>
												 <th class="std" style="max-width: 200px;">Remarks</th>
												</tr>
											<tr>
												
												 <th class="std"  style="max-width: 150px;">Supply Order No</th>
												 <th class="std" style="max-width: 90px;	">DP Date</th>
												 <th class="std" colspan="2" style="max-width: 90px;	">Vendor Name</th>
												 <th class="std" style="max-width: 80px;">Rev DP Date</th>											 
												 <th  class="std" colspan="3" style="max-width: 90px;">SO Cost-Lakh &#8377;</th>		
											 		
											</tr>
										    <%if(procurementOnSanction!=null && procurementOnSanction.size()>0){
										    	k=0;
										    	 Double estcost=0.0;
												 Double socost=0.0;
												 String demand="";
										  	 	for(Object[] obj:procurementOnSanction)
										  	 	{ 
										  	 		if(obj[2]!=null){ 
										  	 		if(!obj[1].toString().equals(demand)){
										  	 			k++;
										  	 		%>
										  	 

												<tr>
													<td class="std" rowspan="2" ><%=k%></td>
													<td class="std" ><%=obj[1]!=null?obj[1].toString(): " - "%> </td>
													<td class="std"><%=obj[3]!=null?sdf.format(sdf1.parse(obj[3].toString())): " - " %></td>
													<td class="std"  colspan="2"><%=obj[8]!=null?obj[8].toString(): " - "%></td>
													<td class="std" style=" text-align:right;"> <%=obj[5]!=null?format.format(new BigDecimal(obj[5].toString())).substring(1): " - " %></td>
												    <td  class="std"> <%=obj[10]!=null?obj[10].toString(): " - "%> </td>
													<td  class="std"><%=obj[11]!=null?obj[11].toString(): " - "%> </td>	
												</tr>
												<%demand=obj[1].toString();
												} %>
												<tr>
													
													<td class="std" ><% if(obj[2]!=null){%> <%=obj[2].toString()%> <%}else{ %>-<%} %> </td>
													<td class="std" ><%if(obj[4]!=null){%> <%=sdf.format(sdf1.parse(obj[4].toString()))%> <%}else{ %> - <%} %></td>
													<td class="std" colspan="2"> <%=obj[12]!=null?StringEscapeUtils.escapeHtml4(obj[12].toString()):"" %> </td>
													<td class="std"><%if(obj[7]!=null){if(!obj[7].toString().equals("null")){%> <%=sdf.format(sdf1.parse(obj[7].toString()))%><%}}else{ %>-<%} %></td>
				                                    <td class="std" colspan="3" style=" text-align: right;"><%if(obj[6]!=null){%> <%=format.format(new BigDecimal(obj[6].toString())).substring(1)%> <%} else{ %> - <%} %></td>												
				
												</tr>		
												<% }
										  	 		
										  	 		Double value = 0.00;
										  	 		if(obj[6]!=null){
										  	 			value=Double.parseDouble(obj[6].toString());
										  	 		}
										  	 		
										  	 		estcost += Double.parseDouble(obj[5].toString());
										  	 		socost +=  value;
										  	 		
										  	 	 } 
										   	%>
										   	 
										    <tr>
										    	<td colspan="6" class="std" style="text-align: right;"><b>Total</b></td>
										    	<td colspan="2" class="std" style="text-align: right;"><b><%=socost!=null?df.format(socost): " - " %></b></td>
										    	
										    </tr>
										     <% }else{%>
											
												<tr><td colspan="8" class="std"  style="text-align: center;">Nil </td></tr>
											<%} %>
									</table> 
									<br><br>
									<table style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 10px; width: 975px;  border-collapse:collapse;" >
											<thead>
											 <tr >
												 <th class="std" colspan="8" ><span class="mainsubtitle">Total Summary of Procurement</span></th>
											 </tr>
										 </thead>
										
									       <tr >
												 <th class="std" style="max-width: 150px;">No. of Demand</th>
												 <th class="std" style="max-width: 150px;">Est. Cost-Lakh &#8377;</th>
										  	 	 <th class="std" style="max-width: 150px;">No. of Orders</th>
										  	 	 <th class="std" style="max-width: 150px;">So Cost-Lakh &#8377; </th>
										  	 	 <th class="std" style="max-width: 150px;">Expenditure-Lakh &#8377; </th>
											 </tr>
									<%if(totalprocurementdetails!=null && totalprocurementdetails.size()>0){ 
										 for(TotalDemand obj:totalprocurementdetails){
											 if(obj.getProjectId().equalsIgnoreCase(projectid)){
										 %>
										   <tr>
										      <td class="std" style="text-align: center;"><%=obj.getDemandCount()!=null?obj.getDemandCount(): " - " %></td>
										      <td class="std" style="text-align: center;"><%=obj.getEstimatedCost()!=null?obj.getEstimatedCost(): " - " %></td>
										      <td class="std" style="text-align: center;"><%=obj.getSupplyOrderCount()!=null?obj.getSupplyOrderCount(): " - "%></td>
										      <td class="std" style="text-align: center;"><%=obj.getTotalOrderCost()!=null?obj.getTotalOrderCost(): " - " %></td>
										      <td class="std" style="text-align: center;"><%=obj.getTotalExpenditure()!=null?obj.getTotalExpenditure(): " - "%></td>
										   </tr>
										   <%}}}else{%>
										   <tr>
										      <td class="std" colspan="5" style="text-align: center;">IBAS Server Could Not Be Connected</td>
										   </tr>
										   <%} %>
									</table>
									
					 <h1 class="break"></h1> 
			<%}else if (committeemin[0].toString().equals("6") ) 
			{ %>
			
					<table style="margin-top: 0px; margin-left: 10px; width: 975px; font-size: 16px; border-collapse: collapse;">
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=committeemin[0]!=null?committeemin[0].toString(): " - "%>.&nbsp;&nbsp;&nbsp;<%=committeemin[1]!=null?committeemin[1].toString(): " - "%></th>
						</tr>
						<tr >
							<td colspan="8" align="right">(Amount in Crores)</td>
						</tr>
					</table>	
					<%if(Long.parseLong(projectid) >0 && projectFinancialDetails!=null) { %>
							
								<table style="margin-top: 5px; margin-bottom: 0px; margin-left: 25px; width: 975px; font-size: 16px; border-collapse: collapse;border: 1px solid black" >
								    <thead>
								        <tr>
								           	<td class="std" colspan="2" align="center"><b>Head</b></td>
								           	<td class="std" colspan="2" align="center"><b>Sanction</b></td>
									        <td class="std" colspan="2" align="center"><b>Expenditure</b></td>
									        <td class="std" colspan="2" align="center"><b>Out Commitment</b> </td>
								            <td class="std" colspan="2" align="center"><b>Balance</b></td>
									        <td class="std" colspan="2" align="center"><b>DIPL</b></td>
								            <td class="std" colspan="2" align="center"><b>Notional Balance</b></td>
								        </tr>
									    <tr>
											<th class="std" >SN</th>
										    <th  class="std" >Head</th>
										    <th class="std" >RE</th>
										    <th class="std" >FE</th>
										    <th class="std" >RE</th>
										    <th class="std" >FE</th>
									        <th class="std" >RE</th>
									        <th class="std" >FE</th>
								            <th class="std" >RE</th>
										    <th class="std" >FE</th>
										    <th class="std" >RE</th>
										    <th class="std" >FE</th>
										    <th class="std" >RE</th>
										    <th class="std" >FE</th>
								        </tr>
									</thead>
									<tbody>
									<% 
								    double totSanctionCost=0,totReSanctionCost=0,totFESanctionCost=0;
									double totExpenditure=0,totREExpenditure=0,totFEExpenditure=0;
									double totCommitment=0,totRECommitment=0,totFECommitment=0,totalDIPL=0,totalREDIPL=0,totalFEDIPL=0;
									double totBalance=0,totReBalance=0,totFeBalance=0,btotalRe=0,btotalFe=0;
									int count=1;
									if(projectFinancialDetails!=null){
										for(ProjectFinancialDetails projectFinancialDetail:projectFinancialDetails){    %>
									 
									    	<tr>
												<td class="std"  align="center"><%=count++ %></td>
												<td class="std"  style="text-align: left;"><%=projectFinancialDetail.getBudgetHeadDescription()!=null?projectFinancialDetail.getBudgetHeadDescription(): " - "%></td>
												<td class="std"  align="right" style="text-align: right;"><%=projectFinancialDetail.getReSanction()!=null?df.format(projectFinancialDetail.getReSanction()): " - "  %></td>
												<%totReSanctionCost+=(projectFinancialDetail.getReSanction());%>
													<td class="std"  align="right" style="text-align: right;"><%=projectFinancialDetail.getFeSanction()!=null?df.format(projectFinancialDetail.getFeSanction()): " - " %></td>
												<%totFESanctionCost+=(projectFinancialDetail.getFeSanction());%>
													<td class="std"  align="right" style="text-align: right;"><%=projectFinancialDetail.getReExpenditure()!=null?df.format(projectFinancialDetail.getReExpenditure()): " - "  %></td>
												<%totREExpenditure+=(projectFinancialDetail.getReExpenditure());%>
												    <td class="std"  align="right" style="text-align: right;"><%=projectFinancialDetail.getFeExpenditure()!=null?df.format(projectFinancialDetail.getFeExpenditure()): " - " %></td>
												<%totFEExpenditure+=(projectFinancialDetail.getFeExpenditure());%>
												    <td class="std"  align="right" style="text-align: right;"><%=projectFinancialDetail.getReOutCommitment()!=null?df.format(projectFinancialDetail.getReOutCommitment()): " - " %></td>
												<%totRECommitment+=(projectFinancialDetail.getReOutCommitment());%>
												    <td class="std"  align="right" style="text-align: right;"><%=projectFinancialDetail.getFeOutCommitment()!=null?df.format(projectFinancialDetail.getFeOutCommitment()): " - " %></td>
												<%totFECommitment+=(projectFinancialDetail.getFeOutCommitment());%>
													<td class="std"  align="right" style="text-align: right;"><%=projectFinancialDetail.getReBalance()!=null && projectFinancialDetail.getReDipl()!=null?df.format(projectFinancialDetail.getReBalance()+projectFinancialDetail.getReDipl()): " - " %></td>
												<%btotalRe+=(projectFinancialDetail.getReBalance()+projectFinancialDetail.getReDipl());%>
													<td class="std"  align="right" style="text-align: right;"><%=projectFinancialDetail.getFeBalance()!=null && projectFinancialDetail.getFeDipl()!=null?df.format(projectFinancialDetail.getFeBalance()+projectFinancialDetail.getFeDipl()): " - " %></td>
												<%btotalFe+=(projectFinancialDetail.getFeBalance()+projectFinancialDetail.getFeDipl());%>
													 <td class="std"  align="right"style="text-align: right;"><%=projectFinancialDetail.getReDipl()!=null?df.format(projectFinancialDetail.getReDipl()): " - " %></td>
												<%totalREDIPL+=(projectFinancialDetail.getReDipl());%>
													 <td class="std"  align="right" style="text-align: right;"><%=projectFinancialDetail.getFeDipl()!=null?df.format(projectFinancialDetail.getFeDipl()): " - " %></td>
												<%totalFEDIPL+=(projectFinancialDetail.getFeDipl());%>
													<td class="std"  align="right" style="text-align: right;"><%=projectFinancialDetail.getReBalance()!=null?df.format(projectFinancialDetail.getReBalance()): " - " %></td>
												<%totReBalance+=(projectFinancialDetail.getReBalance());%>
													<td class="std"  align="right" style="text-align: right;"><%=projectFinancialDetail.getFeBalance()!=null?df.format(projectFinancialDetail.getFeBalance()): " - " %></td>
												<%totFeBalance+=(projectFinancialDetail.getFeBalance());%>
											</tr>
										<%} }%>
																
											<tr>
												<td class="std"  colspan="2"><b>Total</b></td>
												<td class="std"  align="right" style="text-align: right;font-weight: bold;"><%=df.format(totReSanctionCost)%></td>
												<td class="std"  align="right" style="text-align: right;font-weight: bold;"><%=df.format(totFESanctionCost)%></td>
												<td class="std"  align="right" style="text-align: right;font-weight: bold;"><%=df.format(totREExpenditure)%></td>
												<td class="std"  align="right" style="text-align: right;font-weight: bold;"><%=df.format(totFEExpenditure)%></td>
												<td class="std"  align="right" style="text-align: right;font-weight: bold;"><%=df.format(totRECommitment)%></td>
												<td class="std"  align="right" style="text-align: right;font-weight: bold;"><%=df.format(totFECommitment)%></td>
												<td class="std"  align="right" style="text-align: right;font-weight: bold;"><%=df.format(btotalRe)%></td>
												<td class="std"  align="right" style="text-align: right;font-weight: bold;"><%=df.format(btotalFe)%></td>
												<td class="std"  align="right" style="text-align: right;font-weight: bold;"><%=df.format(totalREDIPL)%></td>
												<td class="std"  align="right" style="text-align: right;font-weight: bold;"><%=df.format(totalFEDIPL)%></td>
												<td class="std"  align="right" style="text-align: right;font-weight: bold;"><%=df.format(totReBalance)%></td>
												<td class="std"  align="right" style="text-align: right;font-weight: bold;"><%=df.format(totFeBalance)%></td>
											</tr>
											<tr>
												<td class="std"  colspan="2"><b>GrandTotal</b></td>
												<td class="std"  colspan="2"  style="text-align: right;font-weight: bold;"><%=df.format(totReSanctionCost+totFESanctionCost)%></td>
												<td class="std"  colspan="2"  style="text-align: right;font-weight: bold;"><%=df.format(totREExpenditure+totFEExpenditure)%></td>
												<td class="std"  colspan="2"  style="text-align: right;font-weight: bold;"><%=df.format(totRECommitment+totFECommitment)%></td>
												<td class="std"  colspan="2"  style="text-align: right;font-weight: bold;"><%=df.format(btotalRe+btotalFe)%></td>
												<td class="std"  colspan="2"  style="text-align: right;font-weight: bold;"><%=df.format(totalREDIPL+totalFEDIPL)%></td>
												<td class="std"  colspan="2"  style="text-align: right;font-weight: bold;"><%=df.format(totReBalance+totFeBalance)%></td>
											</tr>
										</tbody>        
									</table>
														
							
					<% }else {  %>
						
						<table style="margin-top: -0px; margin-left: 10px; width: 975px; font-size: 16px; border-collapse: collapse;">
							<tr>
								<th colspan="8" style="text-align: center; font-weight: 700;padding-left: 30px;"><br> Data Unavailable</th>
							</tr>
						</table>		
							
					<% } %>
				
				
		<%}else if (committeemin[0].toString().equals("7") )
		{ %>
		<div class="break"></div>
			
					<table style="margin-top: 0px; margin-left: 10px; width: 975px; font-size: 16px; border-collapse: collapse;">
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700; text-align: justify;padding-left: 15px;" ><br><%=committeemin[0]!=null?committeemin[0].toString(): " - "%>.&nbsp;&nbsp;&nbsp;<%=committeemin[1]!=null?committeemin[1].toString(): " - "%></th>
						</tr>
					</table>	
					<table style="margin-top: 5px; margin-bottom: 0px; margin-left: 10px; width: 975px; font-size: 16px; border-collapse: collapse;border: 1px solid black" >
							 <thead>
									<tr>
										<td colspan="9" style="border: 0px">
											<p style="font-size: 10px;text-align: center"> 
												<span class="notassign">NA</span> : Not Assigned &nbsp;&nbsp;
												<span class="assigned">AA</span> : Activity Assigned &nbsp;&nbsp; 
												<span class="notyet">NS</span> : Not yet Started &nbsp;&nbsp;
												<span class="ongoing">OG</span> : On Going &nbsp;&nbsp; 
												<span class="delay">DO</span> : Delay - On Going &nbsp;&nbsp; 
												<span class="ongoing">RC</span> : Review & Close &nbsp;&nbsp;
												<span class="delay">FD</span> : Forwarded With Delay &nbsp;&nbsp;
												<span class="completed">CO</span> : Completed &nbsp;&nbsp; 
												<span class="completeddelay">CD</span> : Completed with Delay &nbsp;&nbsp; 
												<span class="inactive">IA</span> : InActive &nbsp;&nbsp;
												<!-- <span class="ongoing">UF</span> : User Forwarded &nbsp;&nbsp; --> 
											</p>
										</td>									
									</tr>
							
								<tr>
									<th class="std"  style="width: 30px !important;">SN</th>
									<th class="std"  style="width: 30px; ">MS</th>
									<th class="std"  style="width: 40px; ">L</th>
									<th class="std"  style="max-width: 200px;">Action Plan </th>	
									<th class="std"  style="max-width: 80px;">Responsibility </th>
									<th class="std"  style="max-width: 85px;">PDC</th>	
									<th class="std"  style="max-width: 60px;">Progress </th>
					                <th class="std"  style="max-width: 50px;">Status</th>
					                 <th class="std"  style="max-width: 120px;">Remarks</th>
								</tr>
							</thead>
							<tbody>
								<%if(ActionPlanSixMonths.size()>0){ 
									long count1=1;
									int countA=1;
									int countB=1;
									int countC=1;
									int countD=1;
									int countE=1;
									%>
									<%for(Object[] obj:ActionPlanSixMonths){
										
										if(Integer.parseInt(obj[26].toString())<= Integer.parseInt(levelid) ){
										/*  if(obj[26].toString().equals("0")||obj[26].toString().equals("1")){ */
										%>
										<tr>
											<td class="std"  style="text-align: center"><%=count1 %></td>
											<%-- <td>M <%=obj[22] %> <% if(!obj[26].toString().equals("0")) {%> L<%=obj[26] %> <%} %></td> --%>
											<td class="std"  style="text-align: center">M<%=obj[22]!=null?obj[22].toString(): " - " %></td>
											
											<!-- Old Code -->
											<%-- <td><% if(!obj[26].toString().equals("0")) {%> L<%=obj[26] %> <%} else {%> L <%} %></td>
											<td>
												<%if(!obj[10].toString().trim().equals("")){ %>
													<%=obj[10] %>
												<%}else if(!obj[9].toString().trim().equals("")){ %>
													<%=obj[9] %>
												<%}else if(!obj[8].toString().trim().equals("")){ %>
													<%=obj[8] %>
												<%}else if(!obj[7].toString().trim().equals("")){ %>
													<%=obj[7] %>
												<%} %>
											</td> --%>
											
											<!-- New Code (tharun)-->
											
											<td class="std"  style="text-align: center">
												<%
												
												if(obj[26].toString().equals("0")) {%>
													
												<%countA=1;
													countB=1;
													countC=1;
													countD=1;
													countE=1;
												}else if(obj[26].toString().equals("1")) { %>
													A-<%=countA %>
												<% countA++;
												    countB=1;
												    countC=1;
													countD=1;
													countE=1;
												}else if(obj[26].toString().equals("2")) { %>
													B-<%=countB %>
												<%countB+=1;
												countC=1;
												countD=1;
												countE=1;
												}else if(obj[26].toString().equals("3")) { %>
													C-<%=countC %>
												<%countC+=1;
												countD=1;
												countE=1;
												}else if(obj[26].toString().equals("4")) { %>
													D-<%=countD %>
												<%
												countD+=1;
												countE=1;
												}else if(obj[26].toString().equals("5")) { %>
													E-<%=countE %>
												<%countE++;
												} %>
											</td>
											
											
											
											<td class="std" style="<%if(obj[26].toString().equals("0")) {%>font-weight: bold;<%}%> text-align:left" >
												<%if(obj[26].toString().equals("0")) {%>
													<%=obj[9] !=null?obj[9].toString(): " - "%>
												<%}else if(obj[26].toString().equals("1")) { %>
													&nbsp;&nbsp;<%=obj[10]!=null?obj[10].toString(): " - " %>
												<%}else if(obj[26].toString().equals("2")) { %>
													&nbsp;&nbsp;<%=obj[11] !=null?obj[11].toString(): " - "%>
												<%}else if(obj[26].toString().equals("3")) { %>
													&nbsp;&nbsp;<%=obj[12]!=null?obj[12].toString(): " - " %>
												<%}else if(obj[26].toString().equals("4")) { %>
													&nbsp;&nbsp;<%=obj[13]!=null?obj[13].toString(): " - " %>
												<%}else if(obj[26].toString().equals("5")) { %>
													&nbsp;&nbsp;<%=obj[14]!=null?obj[14].toString(): " - " %>
												<%} %>
											</td>
											<td class="std" style="text-align: left ! important" ><%=obj[24]!=null?obj[24].toString(): " - " %><%-- (<%=obj[25] %>) --%></td>
											<td class="std" ><%=obj[8]!=null?sdf.format(sdf1.parse(obj[8].toString())): " - "  %></td>
											<td class="std"  style="text-align: center"><%=obj[16]!=null?obj[16].toString(): " - " %>%</td>											
											<td class="std"  style="text-align: center">
											<span class="<%if(obj[20].toString().equalsIgnoreCase("0")){%>assigned
														<%}else if(obj[20].toString().equalsIgnoreCase("1")) {%> notyet
														<%}else if(obj[20].toString().equalsIgnoreCase("2")) {%> ongoing
														<%}else if(obj[20].toString().equalsIgnoreCase("3")) {%> completed
														<%}else if(obj[20].toString().equalsIgnoreCase("4")) {%> delay 
														<%}else if(obj[20].toString().equalsIgnoreCase("5")) {%> completeddelay
														<%}else if(obj[20].toString().equalsIgnoreCase("6")) {%> inactive<%} %>	 " >
												<%=obj[27]!=null?obj[27].toString(): " - " %>	
											</span>
											
											</td>
											<td  class="std"  style="max-width: 80px;">
												<%if(obj[28]!=null){ %>
												<%=obj[28].toString() %>
												<%} %>
												</td>
										</tr>
									<%count1++;}} %>
								<%} else{ %>
								<tr><td class="std"  colspan="9" style="text-align:center; "> Nil</td></tr>
								
								
								<%} %>
						</tbody>				
					</table>
							 <h1 class="break"></h1> 
				
		<%} else if (committeemin[0].toString().equals("8") || committeemin[0].toString().equals("9") || committeemin[0].toString().equals("10"))
		{%>
			
			
				<table style="margin-top:0px; margin-left: 10px; width: 975px; font-size: 16px; border-collapse: collapse;">
					<tbody>
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=committeemin[0]!=null?committeemin[0].toString(): " - "%>.&nbsp;&nbsp;&nbsp;<%=committeemin[1]!=null?committeemin[1].toString(): " - "%></th>
						</tr>
				
						<%
						int count = 0;
						
						for (Object[] speclist : speclists)
						{ %>						
							 	
								<%if(speclist[3].toString().equals("4") && committeemin[0].toString().equals("8") )
								{  
									count++; %>	
									<tr>
										<td style="text-align: justify;padding-left: 30px"> 
											<%=speclist[1]%> 
										</td>		
									</tr>			
								<%}else if(speclist[3].toString().equals("5") && committeemin[0].toString().equals("9"))
								{ 
									 %>
									<%if(speclist[7].toString().equalsIgnoreCase("R")){ count++; %>
									<tr>
										<th  style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=committeemin[0]!=null?committeemin[0].toString(): " - "%><%="."+count%>.&nbsp;&nbsp;&nbsp;<%=speclist[9]!=null?speclist[9].toString(): " - "%></th>
									</tr>
									<tr>
										<td style="text-align: justify;padding-left: 30px">
											<%=speclist[1]!=null?speclist[1].toString(): " - "%> 
										</td>
									</tr>
									
									<%} %>
								<%}else if(speclist[3].toString().equals("6") && committeemin[0].toString().equals("10")) 
								{
									count++;%>
									<tr>
										<td style="text-align: justify;padding-left: 30px">
											<%=speclist[1]!=null?speclist[1].toString(): " - "%> 
										</td>	
									</tr>					
								<%}
												
							
						}if (count == 0)
						{%>
						<tr style="page-break-after: ;">
						<td style="text-align: left;"><div style="padding-left: 50px;  width: 930px;"><p>NIL</p></div>
						</td>	
						</tr>								
						<%}%>
							
				
				
				</table>
		
			
			
			
			
		<%}
	}%>
	
		<div style="width: 975px; margin-left:15px; margin-top: 20px ">
			<div align="left" style="padding-left: 2.5rem;">
				<p>These Minutes are issued with the approval of the Chair. </p>
			</div>
			<div align="left" style="padding-right: 0rem;padding-bottom: 0rem; margin-right: 0px">
				<br>Date :&emsp;&emsp;&emsp;&emsp;&emsp;  <br>Time :&emsp;&emsp;&emsp;&emsp;&emsp;
				<%if(membersec!=null){ %>
				
			<div align="right" style="padding-right: 0rem;padding-bottom: 2rem;">
			
				<br><%if(membersec!=null){%><%= membersec[6].toString() %>,&nbsp;<%= membersec[7].toString() %><%} %>
				
				
				 <br>
				 
				 (Member Secretary)
				 <div align="left" ><b>NOTE : </b>Action item details are enclosed as Annexure - AI.</div>
			</div>
			<%} %>
			</div>			
		</div> 
	
	<%if( isprint.equals("N")){ %>
		<div class="break"></div>	
		<br>
						
				<div align="center">
			<div style="text-align: center;" class="lastpage" id="lastpage"><h2>ACTION ITEMS DETAILS</h2></div>
		
			<table style="margin-top: -5px; margin-left: 5px; width: 975px; font-size: 16px; border-collapse: collapse ;border: 1px solid black ">
			<tbody>
				<tr>
					<th  class="sth" style=" max-width: 30px"> SN. </th>
					<th  class="sth" style=" max-width: 70px"> Action Id</th>	
					<th  class="sth" style=" max-width: 600px"> Item</th>				
					<th  class="sth" style=" max-width: 70px"> Responsibility</th>					
					<th  class="sth" style=" width: 90px"> PDC</th>
				</tr>
				
				<% 	int count =0;
				 	Iterator actIterator = actionlist.entrySet().iterator();
					while(actIterator.hasNext()){	
					Map.Entry mapElement = (Map.Entry)actIterator.next();
		            String key = ((String)mapElement.getKey());
		            ArrayList<Object[]> values=(ArrayList<Object[]>)mapElement.getValue();
		            count++;
				%>
					<tr>
						<td class="std" style="text-align: center ;" > <%=count%></td>
						<td  class="std" style="text-align: left;" >
							
							<%	int count1=0;
								for(Object obj[]:values){
									 count1++; %>
									<%if(count1==1 ){ %>
										<%if(obj[3]!=null){ %> <%=obj[3].toString()%><%}else{ %> - <%} %>
									<%}else if(count1==values.size() ){ %>
										<%if(obj[3]!=null){ %> <br> - <br> <%= obj[3].toString()%> <%}else{ %> - <%} %>
									<%} %>
							<%} %>
							
						</td>
						
						<td  class="std" style="padding-left: 5px;padding-right: 5px;text-align: justify;"><%= values.get(0)[1]!=null?values.get(0)[1].toString(): " - "  %></td>
						<td  class="std" style="text-align: left;" >
							<%	int count2=0;
							for(Object obj[]:values){ %>
							<%if(obj[13]!=null){ %> <%= obj[13].toString()%><%-- ,&nbsp;<%=obj[14] %> --%>
								<%if(count2>=0 && count2<values.size()-1){ %>
								,&nbsp;
								<%} %>
							<%}else{ %> - <%} %>
						<%count2++;} %>
						</td>                       						
						<td  class="std" style="text-align: center;"><%if( values.get(0)[5]!=null){ %> <%=sdf.format(sdf1.parse(values.get(0)[5].toString()))%> <%}else{ %> - <%} %></td>
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

