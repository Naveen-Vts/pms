<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.time.temporal.ChronoUnit"%>
<%@page import="java.time.LocalDate"%>
<%@page import="com.vts.pfms.model.TotalDemand"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="java.text.Format"%>
<%@page import="java.math.BigInteger"%>
<%@page import="java.text.Format"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="com.vts.pfms.master.dto.ProjectFinancialDetails"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@page import="com.vts.pfms.model.LabMaster"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" import="java.util.*"%>
<!DOCTYPE html>
<html>
<head>
	<%
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
	String levelid= (String) request.getAttribute("levelid");
	int meetingcount= (int) request.getAttribute("meetingcount");
	Object[] projectdatadetails = (Object[]) request.getAttribute("projectdatadetails");
	
	DecimalFormat df=new DecimalFormat("####################.##");
	FormatConverter fc=new FormatConverter(); 
	SimpleDateFormat sdf3=fc.getRegularDateFormat();
	SimpleDateFormat sdf=fc.getRegularDateFormatshort();
	SimpleDateFormat sdf1=fc.getSqlDateFormat(); int addcount=0; 
	Format format = com.ibm.icu.text.NumberFormat.getCurrencyInstance(new Locale("en", "in"));
	String projectid= committeescheduleeditdata[9].toString();
	String divisionid= committeescheduleeditdata[16].toString();
	String initiationid= committeescheduleeditdata[17].toString();
	String lablogo=(String)request.getAttribute("lablogo");
	/* String committeeid1=committeescheduleeditdata[0].toString(); */
	/* newly Added  */
	  SimpleDateFormat inputFormat = new SimpleDateFormat("ddMMMyyyy",Locale.ENGLISH);
      SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
      String todayDate=outputFormat.format(new Date()).toString();
    /* ------- */
	String[] no=committeescheduleeditdata[11].toString().split("/");
	Object[] membersec=null; 
	Map<Integer,String> treeMapLevOne =(Map<Integer,String>)request.getAttribute("treeMapLevOne");
	Map<Integer,String> treeMapLevTwo =(Map<Integer,String>)request.getAttribute("treeMapLevTwo");
 /* 	for (Map.Entry<Integer,String> entry : treeMapLevTwo.entrySet()) {
		System.out.println(entry.getKey()+"-------"+entry.getValue());
	}  */
	//maps for pmrc and EB
	Map<Integer,String> mappmrc=(Map<Integer,String>)request.getAttribute("mappmrc");
	Map<Integer,String> mapEB=(Map<Integer,String>)request.getAttribute("mapEB");
	List<Object[]> envisagedDemandlist = (List<Object[]> )request.getAttribute("envisagedDemandlist");
	List<String> repCodes = (List<String> )request.getAttribute("repCodes");
	
	String labcode =(String) session.getAttribute("labcode");
	// new
	
		LinkedHashMap< String, ArrayList<Object[]>> actionlist = (LinkedHashMap< String, ArrayList<Object[]>>) request.getAttribute("tableactionlist");
	
	%>
<style type="text/css">
p{
  text-align: justify;
  text-justify: inter-word;
}
.break
	{
		page-break-after: always;
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
          size: 790px 1120px;
          margin-top: 49px;
          margin-left: 39px;
          margin-right: 39px;
          margin-bottom: 49px; 	
          border: 1px solid black;    
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
	          content: "Generated On : <%=LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))%>"; 
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
<title><%=committeescheduleeditdata[8]%> Minutes View</title>
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
						 <th  style="text-align: center; width: 650px;font-size: 20px "> <u>Meeting Id </u> </th></tr><tr>
						 <th  style="text-align: center;  width: 650px;font-size: 20px  "> <%=committeescheduleeditdata[11]!=null?committeescheduleeditdata[11].toString(): " - " %> </th>				
					 </tr>
				</table>
				
				<br><br>
		 <table style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; max-width: 650px; font-size: 16px"  border="0">
			 <tr>
				 <th  style="text-align: center; width: 650px;font-size: 20px "> <u> Meeting Date </u></th>
				 <th  style="text-align: center;  width: 650px;font-size: 20px  "><u> Meeting Time </u></th>
			 </tr>
			
			 <tr>
				 <td  style="text-align: center;  width: 650px;font-size: 20px ;padding-top: 5px"> <b><%=sdf3.format(sdf1.parse(committeescheduleeditdata[2].toString()))%></b></td>
				 <td  style="text-align: center;  width: 650px;font-size: 20px ;padding-top: 5px "> <b><%=committeescheduleeditdata[3]!=null?committeescheduleeditdata[3].toString(): " - "%></b></td>
			 </tr>
			 
		 </table>
		 
		 <table style="align: center; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; max-width: 650px; font-size: 16px"  border="0">
					<tr style="margin-top: 10px">
						 <th  style="text-align: center; width: 650px;font-size: 20px "> <u>Meeting Venue</u> </th></tr><tr>
						 <th  style="text-align: center;  width: 650px;font-size: 20px  "> <% if(committeescheduleeditdata[12]!=null){ %><%=committeescheduleeditdata[12] %> <%}else{ %> - <%} %></th>				
					 </tr>
				</table>
		<br><br><br><br><br>
			<figure><img style="width: 4cm; height: 4cm"  src="data:image/png;base64,<%=lablogo%>"></figure>   
						<br><br><br>
			<div align="center" ><h3><%=labdetails[2] !=null?labdetails[2].toString(): " - "%> (<%=labdetails[1]!=null?labdetails[1].toString(): " - "%>)</h3></div>
			
			<div align="center" ><h3><%=labdetails[4]!=null?labdetails[4].toString(): " - " %>, &nbsp;<%=labdetails[5] !=null?labdetails[5].toString(): " - "%>, &nbsp;<%=labdetails[6]!=null?labdetails[6].toString(): " - " %></h3></div>
		</div>  
		
 <h1 class="break"></h1> 
<!-- ------------------------------------------------------- members --------------------------------- -->
<%-- 	<div align="center">
		<table style="align: center; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; max-width: 650px; font-size: 16px">
			<tr style="margin-top: 10px">
				<td  style="text-align: left; width: 650px;font-size: 20px; padding-left: 15px;"> Record/ File no __________dated___________  </td></tr><tr>
				<th  style="text-align: center;  width: 650px;font-size: 20px;padding-top: 10px; ">
					Minutes of  Apex Board/ Executive Board/ PMRC Meeting for Project titled 
				"<span style=" text-decoration: underline;"><%=projectdetails[1] %>  (<%=projectdetails[4]%>)</span>" held on <%=sdf.format(sdf1.parse(committeescheduleeditdata[2].toString()))%> at  <% if(committeescheduleeditdata[12]!=null){ %><%=committeescheduleeditdata[12] %> <%}else{ %> - <%} %>
				</th>				
			</tr>
		</table>
	
	</div> --%>


<%if(invitedlist.size()>0){ %>
<% ArrayList<String> membertypes=new ArrayList<String>(Arrays.asList("CC","CS","PS","CI","CW","CO","CH"));
membertypes.addAll(repCodes);

int memPresent=0,memAbscent=0,ParPresent=0,parAbscent=0;
int j=0;
List<Object[]>specialMembers = new ArrayList<>();
if(invitedlist.size()>0){
	specialMembers=invitedlist.stream().filter(e->e[3].toString().equalsIgnoreCase("SPL")).collect(Collectors.toList());
	 invitedlist=invitedlist.stream().filter(e->!e[3].toString().equalsIgnoreCase("SPL")).collect(Collectors.toList());
}
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
<%if(specialMembers.size()>0) {%>
<div align="left" style="font-weight: bold;margin-left:10px;">Special Members  </div>
<% int i=0;
for( Object[]obj:specialMembers){ %>
<p style="padding: 0px;margin:0px;margin-left:10px;padding-top:7px;font-weight: 600;"><%=++i %>. <%=obj[6]!=null?obj[6].toString(): " - "%>,<%=obj[7]!=null?obj[7].toString(): " - "%> ( <%=obj[11]!=null?obj[11].toString(): " - " %> )</p>
<%} %>
<p>

<%} %>
<br>
<table style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; width: 650px; font-size: 16px; border-collapse:collapse;" >	
	
	 <tr>
		 <th style="text-align: center ;padding: 5px;border: 1px solid black;width: 10px; ">SN</th>
		 <th style="text-align: center ;padding: 5px;border: 1px solid black;width: 220px; ">Name</th>
		 <th style="text-align: center ;padding: 5px;border: 1px solid black;width: 280px; "> Designation &  Estt. / Agency</th>
		 <th style="text-align: center ;padding: 5px;border: 1px solid black;width: 140px; ">Role</th>
	 </tr>
	  <tr>
		 <th colspan="4" style="text-align: left; font-weight: 700; border: 1px solid black; padding: 5px; padding-left: 15px">Members Present</th>
	 </tr>
	 <%
	
	 
	 if(memPresent > 0){ %>
	 
	 <% 
	 	for(int i=0;i<invitedlist.size();i++)
		{
	 	if(invitedlist.get(i)[4].toString().equals("P") && membertypes.contains( invitedlist.get(i)[3].toString()) )
	 	{ j++;%>
	 	
	 	 <tr>
	 	 <td style="border: 1px solid black; padding: 5px;text-align: center"><%=j%> </td>
	 	  	<td style="border: 1px solid black; padding: 5px;text-align: left">  
	 	  	
	 	 			<%= invitedlist.get(i)[6]!=null?invitedlist.get(i)[6].toString(): " - "%> <br>
	 			<%= invitedlist.get(i)[17]!=null && invitedlist.get(i)[17].toString().equalsIgnoreCase("Y") ?"(Through Online)": ""%> 
		 	</td>
		 	<td style="border: 1px solid black; padding: 5px;text-align: left;">  
	 	  	
	 			<%=invitedlist.get(i)[7]!=null?invitedlist.get(i)[7].toString(): " - " %>, <%=invitedlist.get(i)[15]!=null && !invitedlist.get(i)[15].toString().isBlank() ?(invitedlist.get(i)[15].toString()+", "):(invitedlist.get(i)[14]!=null && !invitedlist.get(i)[14].toString().isBlank() ? (invitedlist.get(i)[14].toString()+", "): "")  %> <%= invitedlist.get(i)[11]!=null?invitedlist.get(i)[11].toString(): " - "%>  
		 	</td>	
		 	<td style="border: 1px solid black;padding: 5px ;text-align: left">
		 		<%  if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CC")) {		 %>Chairperson<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CS") ){ membersec=invitedlist.get(i); %> Member Secretary<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CH") ){ %> Co-Chairperson<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("PS") ) { %>Member Secretary&nbsp;(Proxy) <%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CI")){   %>Member<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CW")){	 %>Member<%-- &nbsp;(<%=invitedlist.get(i)[11] %>) --%><%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CO")){	 %>Member<%-- &nbsp;(<%=invitedlist.get(i)[11]%>) --%><%}
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
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CI")){   %>Member<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CW")){	 %>Member<%-- &nbsp;(<%=invitedlist.get(i)[11] %>) --%><%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CO")){	 %>Member<%-- &nbsp;(<%=invitedlist.get(i)[11]%>) --%><%}
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
	 	  	
	 			<%= invitedlist.get(i)[6]!=null?invitedlist.get(i)[6].toString(): " - "%>&nbsp;
		 	</td>	
		 	<td style="border: 1px solid black; padding: 5px;text-align: left;">  
	 	  	
	 			<%=invitedlist.get(i)[7]!=null?invitedlist.get(i)[7].toString(): " - " %>, <%= invitedlist.get(i)[11]!=null?invitedlist.get(i)[11].toString(): " - "%>  
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
	


 <h1 class="break" ></h1> 
 
<!-- -------------------------------------------------------members----------------------------- -->
			
		<%if(labcode.equalsIgnoreCase("DLRL") && projectdetails!=null){
			int prjCount=0;
			%>
		<div>
		<h2>Project Attributes</h2>
		<table style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; width: 650px; font-size: 16px; border-collapse:collapse;" >	
		<tr>
		<td style="text-align: center ;padding: 5px;border: 1px solid black;width: 10px; width:5%"><%=++prjCount %>.</td>
		<td style="text-align: center ;padding: 5px;border: 1px solid black;width: 10px; width:20%;font-weight:bold"">Project Title</td>
		<td style="text-align: center ;padding: 5px;border: 1px solid black;width: 10px; text-align: left"><%=projectdetails[1]!=null?projectdetails[1].toString():"" %></td>
		</tr>
		<tr>
		<td style="text-align: center ;padding: 5px;border: 1px solid black;width: 10px; width:5%"><%=++prjCount %>.</td>
		<td style="text-align: center ;padding: 5px;border: 1px solid black;width: 10px; width:20%;font-weight:bold">Project Code</td>
		<td style="text-align: center ;padding: 5px;border: 1px solid black;width: 10px; text-align: left"><%=projectdetails[4]!=null?projectdetails[4].toString():"" %></td>
		</tr>
		<tr>
		<td style="text-align: center ;padding: 5px;border: 1px solid black;width: 10px; width:5%"><%=++prjCount %>.</td>
		<td style="text-align: center ;padding: 5px;border: 1px solid black;width: 10px; width:20%;font-weight:bold">Date of Sanction</td>
		<td style="text-align: center ;padding: 5px;border: 1px solid black;width: 10px; text-align: left"><%=projectdetails[11]!=null?sdf.format(sdf1.parse(projectdetails[11].toString())):"" %></td>
		</tr>
		<tr>
		<td style="text-align: center ;padding: 5px;border: 1px solid black;width: 10px; width:5%"><%=++prjCount %>.</td>
		<td style="text-align: center ;padding: 5px;border: 1px solid black;width: 10px; width:20%;font-weight:bold">Objective</td>
		<td style="text-align: center ;padding: 5px;border: 1px solid black;width: 10px; text-align: left"><%=projectdetails[15]!=null?projectdetails[15].toString():"" %></td>
		</tr>
		<tr>
		<td style="text-align: center ;padding: 5px;border: 1px solid black;width: 10px; width:5%"><%=++prjCount %>.</td>
		<td style="text-align: center ;padding: 5px;border: 1px solid black;width: 10px; width:20%;font-weight:bold">Deliverable</td>
		<td style="text-align: center ;padding: 5px;border: 1px solid black;width: 10px; text-align: left"><%=projectdetails[16]!=null?projectdetails[16].toString():"" %></td>
		</tr>
		<tr>
		<td style="text-align: center ;padding: 5px;border: 1px solid black;width: 10px; width:5%"><%=++prjCount %>.</td>
		<td style="text-align: center ;padding: 5px;border: 1px solid black;width: 10px; width:20%;font-weight:bold">PDC</td>
		<td style="text-align: center ;padding: 5px;border: 1px solid black;width: 10px; text-align: left"><%=projectdetails[9]!=null?sdf.format(sdf1.parse(projectdetails[9].toString())):"" %></td>
		</tr>
		<tr>
    <td style="text-align: center; padding: 5px; border: 1px solid black; width: 5%;"><%=++prjCount %>.</td>
    
    <td style="text-align: center; padding: 5px; border: 1px solid black; width: 20%; font-weight: bold;">
        <b>Cost Breakup( &#8377; <span class="currency">Lakhs</span>)</b>
    </td>

    <td style="padding: 0; border: 1px solid black; text-align: left;">
        <table style="width: 100%; height: 100%; border-collapse: collapse; border: none;">
            <tr>
                <td style="border-bottom: 1px solid black; border-right: 1px solid black; padding: 5px; width: 50%; text-align:center;font-size:12px">RE COST</td>
                <td style="border-bottom: 1px solid black; padding: 5px;"><%=projectdetails[12]!=null?df.format(Double.parseDouble(projectdetails[12].toString())/100000):0 %></td>
            </tr>
            <tr>
                <td style="border-bottom: 1px solid black; border-right: 1px solid black; padding: 5px; text-align:center;font-size:12px">FE COST</td>
                <td style="border-bottom: 1px solid black; padding: 5px;"><%=projectdetails[13]!=null?df.format(Double.parseDouble(projectdetails[13].toString())/100000):0 %></td>
            </tr>
            <tr>
                <td style="border-right: 1px solid black; padding: 5px; text-align:center;font-size:12px">Total Cost</td>
                <td style="padding: 5px;"><%=projectdetails[14]!=null?df.format((Double.parseDouble(projectdetails[14].toString())/100000)):0 %></td>
            </tr>
		        </table>
		    </td>
		</tr>
		</table>
		
		</div>
	 <h1 class="break"></h1> 	
	 <%} %>
	
		<% for (Object[] committeemin : committeeminutes) { %>
		<% if (committeemin[0].toString().equals("1") ) { %>
		
		<table style="margin-top: 0px; margin-left: 10px; width: 650px; font-size: 16px; border-collapse: collapse;">
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
				
			<% }else if (committeemin[0].toString().equals("2")) { %>
		
		<table style="margin-top: 0px; margin-left: 10px; width: 650px; font-size: 16px; border-collapse: collapse;">
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
					
				<%  }else if (committeemin[0].toString().equals("3")) { %>
					<!-- <h1 class="break"></h1> --> 
						 
					<table style="margin-top: 0px; margin-left: 15px; width: 650px; font-size: 16px; border-collapse: collapse;">
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;">3 (a) Record of Discussions and Action Points of Current Meeting.</th>
						</tr>
						<tr>
							<td colspan="8" style="text-align: center ;padding: 5px;">Item Code/Type : A: Action, C: Comment, D: Decision, R: Recommendation,I:Issue,K:Risk</td>
						</tr>
					</table>	
					<%if(labcode.equalsIgnoreCase("ADE")) { %>
					<%if(actionlist.size()>=0){ %>
							
					<div align="center">
					<table style="margin-top: 5px; margin-left: 25px;width: 650px; font-size: 16px; border-collapse: collapse ;border: 1px solid black ">
						<tbody>
							<tr>
								<th  class="sth" style=" max-width: 30px"> SN </th>
<!-- 								<th  class="sth" style=" max-width: 210px"> Action Id</th>	
 -->							
 								<th  class="sth" style=" max-width: 280px"> Item</th>
 								<th  class="sth" style=" max-width: 70px"> Action Type</th>				
								<th  class="sth" style=" max-width: 180px"> Responsibility </th>					
								<th  class="sth" style=" width: 100px"> PDC</th>
							</tr>
							
							<% 	int count =1;
							  	Iterator actIterator = actionlist.entrySet().iterator();
								while(actIterator.hasNext()){	
								Map.Entry mapElement = (Map.Entry)actIterator.next();
					            String key = ((String)mapElement.getKey());
					            ArrayList<Object[]> values=(ArrayList<Object[]>)mapElement.getValue();
								%>
								<tr>
									<td class="std" style="text-align: center;"> <%=count%></td>
							
								
									<td  class="std" style="padding-left: 5px;padding-right: 5px;text-align: justify;"><%= values.get(0)[1]!=null?values.get(0)[1].toString(): " - " %></td>
									<td  class="std" style="text-align: center;"> <%= values.get(0)[2]!=null?values.get(0)[2].toString(): " - "   %></td>
									<td  class="std" >
								<%	int count2=0;
									//Set<String>labCodes= new LinkedHashSet<>();
									List<String>labCodes = new LinkedList<>();
										for(Object obj[]:values){
									    if(obj[16]!=null ){
										labCodes.add(obj[16].toString());	
										}
										if(obj[15]!=null ){
											labCodes.add(obj[15].toString());	
										}
										
											%>
										
									<%count2++;} %>
									<%if(labCodes.toString().replace("[", "").replace("]", "") .length()>0){ %><%=labCodes.toString().replace("[", "").replace("]", "")  %> <%}else{ %> - <%} %>
									</td>                       						
									<td  class="std"><%if( values.get(0)[5]!=null){ %> <%=sdf.format(sdf1.parse(values.get(0)[5].toString()))%> <%}else{ %> - <%} %></td>
								</tr>				
							<% count++;} %>
						</tbody>
					</table>
					</div>
					<br>	
				<%}%>
					
					<%}else{ %>
					<table style="margin-top: 5px; margin-left: 25px;width: 650px; font-size: 16px; border-collapse: collapse ;border: 1px solid black ">
					<thead>
						<tr>
							<th  class="sth" style="text-align :center !important; max-width: 30px"> SN</th>
							<th  class="sth" style="text-align :center !important; max-width: 35px"> Type</th>
							<th  class="sth" style=" max-width: 795px"> Item</th>				
							<!-- <th  class="sth" style="width: 195px"> Remarks</th> -->	
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
									<td class="std" style="text-align :center;border:1px solid black;"  colspan="4"><%=speclists.get(i)[10]!=null?speclists.get(i)[10].toString(): " - " %></td>
								</tr>
							<%tempagenda=Long.parseLong(speclists.get(i)[6].toString());
							} %>
							<tr>
								<td class="std" style="text-align :center !important;border:1px solid black;vertical-align: top;"  ><p  style="text-align :center !important; "> <%=countcm%> </p></td>
								<%if("DLRL".equalsIgnoreCase(labcode)){ %>
								<td class="std" style="text-align :center !important;border:1px solid black; padding: 5px 5px 5px 5px ; vertical-align: top;" >							
									<p style="text-align :center !important; ">	<%=speclists.get(i)[5]!=null ? ("7".equalsIgnoreCase(speclists.get(i)[5].toString()) ? "R" : ("8".equalsIgnoreCase(speclists.get(i)[5].toString()) ? "D" : speclists.get(i)[7].toString() )) : " - "%> 
									</p> 				
								</td>
								<%}else{ %>
									<td class="std" style="text-align :center !important;border:1px solid black; padding: 5px 5px 5px 5px ; vertical-align: top;" >							
									<p style="text-align :center !important; ">	<%=speclists.get(i)[7]!=null?speclists.get(i)[7].toString(): " - "%> 
									</p> 	
								<%} %>			
								</td>
								<td  class="std" style="border:1px solid black;padding:  5px 5px 5px 5px ;width: 600px;text-align: justify;"><%=speclists.get(i)[1]!=null?speclists.get(i)[1].toString(): " - "%></td>
								<%-- <td class="std" style="text-align :center;border:1px solid black;padding:  5px 5px 5px 5px;"  > <%if( speclists.get(i)[8]!=null && !speclists.get(i)[8].toString().equalsIgnoreCase("nil")){ %> <%= speclists.get(i)[8]%> <%}else{ %> - <%} %></td> --%>
							</tr>
							<%} 
							}%>
							<% if(countcm==0){%>
								<tr>
									<td class="std" style="text-align :center;border:1px solid black;"  colspan="4">No Minutes details Added</td>
								</tr>
							<%} %>
						</table>	
<%} %>
							<table style="margin-top: 0px; margin-left: 10px; width: 650px; font-size: 16px; border-collapse: collapse;">
								<tr>
									<th colspan="8" style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=committeemin[0]!=null?committeemin[0].toString(): " - "%> (b)&nbsp;<%=committeemin[1]!=null?committeemin[1].toString(): " - "%></th>
								</tr>
							</table>	
   				

		<table style=" margin-left: 8px; width: 693px; margin-top:5px;font-size: 16px; border-collapse: collapse;border: 1px solid black" >
								<thead >
									<tr>
										<th class="std"  style="width: 30px;"  >SN</th>
										<th class="std"  style="width: 80px;" > ID</th>
										<th class="std"  style="width: 300px;" >Action Point</th>
										<th class="std"  style="width: 80px; " > ADC<br>PDC</th>
										
										<th class="std"  style="width: 155px;" >Responsibility</th>
										<!-- <th class="std"  style="width: 40px;"  >Status(DD)</th>			 -->
										<%if("ADE".equalsIgnoreCase(labcode)){ %>
											<th class="std"  style="width: 155px;" >Remarks</th>
										<%} %>
									</tr>
								</thead>
								
								
								<tbody>
											<%if(lastpmrcactions.size()==0){ %>
								<tr><td colspan="<%= "ADE".equalsIgnoreCase(labcode) ? "8" : "7" %>"  style="text-align: center;" > Nil</td></tr>
								<%}
								else if(lastpmrcactions.size()>0){
								Map<String,List<Object[]>> list = lastpmrcactions!=null ? lastpmrcactions.stream()
										.collect(Collectors.groupingBy(array -> array[0].toString(), LinkedHashMap::new,Collectors.toList())) : new HashMap<>();
							int i = 1;String key="";
							for(Map.Entry<String, List<Object[]>> map : list.entrySet()){
								int j=1;
								List<Object[]> values = map.getValue();
								int rowSpan = values.size();
								for (Object[] obj : values) {
									
									if(obj[9] != null && "C".equalsIgnoreCase(obj[9].toString())){
										continue;
									}
								%>
								<tr>
									<td  class="std"  align="center"><%=i %></td>
									<td class="std"  align="center">
								<!--newly added on 13th sept  -->	
								<%if(obj[17]!=null && Long.parseLong(obj[17].toString())>0){ %>
								<%if(committeescheduleeditdata[8].toString().equalsIgnoreCase("pmrc")){ %>
								<%for (Map.Entry<Integer, String> entry : mappmrc.entrySet()) {
									Date date = inputFormat.parse(obj[1].toString().split("/")[3]);
									 String formattedDate = outputFormat.format(date);
									 if(entry.getValue().equalsIgnoreCase(formattedDate)){
										 key=entry.getKey().toString();
									 } }}else{%>
									 <%
									 for (Map.Entry<Integer, String> entry : mapEB.entrySet()) {
											Date date = inputFormat.parse(obj[1].toString().split("/")[3]);
											 String formattedDate = outputFormat.format(date);
											 if(entry.getValue().equalsIgnoreCase(formattedDate)){
												 key=entry.getKey().toString();
											 }
									 }
									 %>
									 <%} %>
							<span style="font-size: 14px;">	<%=committeescheduleeditdata[8]!=null?committeescheduleeditdata[8].toString().toUpperCase():" - "%> <%=key%>/<%=obj[1]!=null?obj[1].toString().split("/")[4]:" - " %></span>
								<%}%> 
								</td>
								<%if(j++==1){ %><td rowspan="<%=rowSpan%>" class="std" style="text-align: left;"> <%=obj[2].toString()%> </td> <%} %>
													<td class="std" style="text-align: center;">
									<%	String actionstatus = obj[9].toString();
										int progress = obj[15]!=null ? Integer.parseInt(obj[15].toString()) : 0;
										LocalDate pdcorg = LocalDate.parse(obj[3].toString());
										LocalDate lastdate = obj[13]!=null ? LocalDate.parse(obj[13].toString()): null;
										LocalDate today = LocalDate.now();
										LocalDate endPdc=LocalDate.parse(obj[4].toString());
									%> 
					 				<% if(lastdate!=null && actionstatus.equalsIgnoreCase("C") ){%>
											<%if(actionstatus.equals("C") && (pdcorg.isAfter(lastdate) || pdcorg.equals(lastdate))){%>
											<span class="completed"><%= sdf.format(sdf1.parse(obj[13].toString()))%> </span>
											<%}else if(actionstatus.equals("C") && pdcorg.isBefore(lastdate)){ %>	
											<span class="completeddelay"><%= sdf.format(sdf1.parse(obj[13].toString()))%> </span>
											<%} %>	
										<%}else{ %>
												-									
										<%} %>
									<br>
									<span <%if(endPdc.isAfter(today) || endPdc.isEqual(today)) {%>style="color:black;font-weight: bold;" <%} else{%> style="color:black ;font-weight:bold;" <%} %>>
									<%= sdf.format(sdf1.parse(obj[4].toString()))%>
									</span>
										<%if(!pdcorg.equals(endPdc)) { %>
									<br>
									<span <%if(pdcorg.isAfter(today) || pdcorg.isEqual(today)) {%>style="color:black;font-weight: bold;" <%} else{%> style="color:black ;font-weight:bold;" <%} %>>
									<%= sdf.format(sdf1.parse(obj[3].toString()))%> 
									</span>	
									<%} %>
								</td>
												
												
									<td class="std"> <%=obj[11]!=null?obj[11].toString(): " - " %>, <%=obj[12]!=null?obj[12].toString(): " - " %> </td>
			 					
			 					<%if("ADE".equalsIgnoreCase(labcode)){ %>
									<td class="std"> <%=obj[16]!=null?obj[16].toString(): " - " %></td>
								<%}%>
								
								</tr>			
							<%i++;
							}}} %>
							</tbody>
								</tbody>
								</table>
			<% } else if(committeemin[0].toString().equals("4") ) { %>
				<br>
					<table style="margin-top: -15px; margin-left: 10px; width: 650px; font-size: 16px; border-collapse: collapse;">
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=committeemin[0]!=null?committeemin[0].toString(): " - "%>.&nbsp;&nbsp;&nbsp;<%=committeemin[1]!=null?committeemin[1].toString(): " - "%> (Annexure - A)</th>
						</tr>
					</table>	
			<%}else if (committeemin[0].toString().equals("5") ){%>
					<table style="margin-top: 0px; margin-left: 10px; width: 650px; font-size: 16px; border-collapse: collapse;">
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=committeemin[0]!=null?committeemin[0].toString(): " - "%>.&nbsp;&nbsp;&nbsp;<%=committeemin[1]!=null?committeemin[1].toString(): " - "%>  (Annexure - B)</th>
						</tr>
					</table>	
			<%}else if (committeemin[0].toString().equals("6") ) 
			{ %>
					<table style="margin-top: 0px; margin-left: 10px; width: 650px; font-size: 16px; border-collapse: collapse;">
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=committeemin[0]!=null?committeemin[0].toString(): " - "%>.&nbsp;&nbsp;&nbsp;<%=committeemin[1]!=null?committeemin[1].toString(): " - "%> (Annexure - C)</th>
						</tr>
					</table>	
		<%}else if (committeemin[0].toString().equals("7") ){ %>
			
					<table style="margin-top: 0px; margin-left: 10px; width: 650px; font-size: 16px; border-collapse: collapse;">
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700; text-align: justify;padding-left: 15px;" ><br><%=committeemin[0]!=null?committeemin[0].toString(): " - "%>.&nbsp;&nbsp;&nbsp;<%=committeemin[1]!=null?committeemin[1].toString(): " - "%> (Annexure - D)</th>
						</tr>
					</table>	
		
				
		<%} else if (committeemin[0].toString().equals("8") || committeemin[0].toString().equals("9") || committeemin[0].toString().equals("10"))
		{%>
			
			
				<table style="margin-top:0px; margin-left: 10px; width: 650px; font-size: 16px; border-collapse: collapse;">
					<tbody>
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=committeemin[0]!=null?committeemin[0].toString(): " - "%>.&nbsp;&nbsp;&nbsp;<%=committeemin[1]!=null?committeemin[1].toString(): " - "%></th>
						</tr>
				
						<%
						int count = 0;
						int num = 1;
						
						for (Object[] speclist : speclists)
						{ %>						
							 	
								<%if(speclist[3].toString().equals("4") && committeemin[0].toString().equals("8") )
								{  
									count++; %>	
									<tr>
										<td style="text-align: justify;padding-left: 30px"> 
											<%=speclist[1]!=null?speclist[1].toString(): " - "%> 
										</td>		
									</tr>			
								<%}else if(speclist[3].toString().equals("5") && committeemin[0].toString().equals("9"))
								{ 
									 %>
									<%if(speclist[7].toString().equalsIgnoreCase("R")){ count++; %>
									<%-- <tr>
										<th  style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=committeemin[0]!=null?committeemin[0].toString(): " - "+"."+count%>.&nbsp;&nbsp;&nbsp;<%=speclist[9]!=null?speclist[9].toString(): " - "%></th>
									</tr> --%>
									<tr>
										<td style="text-align: justify;padding:8px 0 8px 30px;">
											<%=num++ %>. <%=speclist[1]!=null?speclist[1].toString(): " - "%> 
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
						}if(count == 0)
						{%>
						<tr style="page-break-after: ;">
						<td style="text-align: left;"><div style="padding-left: 50px"><p>NIL</p></div>
						</td>	
						</tr>								
						<%}%>
					</table>
		
			
			
			
			
		<%}
	}%>
	
		<div style="width: 650px;margin-left: 15px; ">
			<div align="center" style="padding-left: 2.5rem;">
				<p>These Minutes are issued with the approval of the Chairperson. </p>
			</div>
			<div align="left" style="padding-right: 0rem;padding-bottom: 0rem; margin-right: 0px">
				<br>Date :&emsp;&emsp;&emsp;&emsp;&emsp;  <br>Time :&emsp;&emsp;&emsp;&emsp;&emsp;
				<%if(membersec!=null){%>
				<div align="right" style="padding-right: 0rem;padding-bottom: 2rem;">
				<br><%if(membersec!=null){%><%= membersec[6]!=null?membersec[6].toString(): " - " %>,&nbsp;<%= membersec[7]!=null?membersec[7].toString(): " - " %><%} %>
				 <br>
				 (Member Secretary)
				<%if(!labcode.equalsIgnoreCase("ADE")) {%> <div align="left" ><b>NOTE : </b>Action item details are enclosed as Annexure - AI.</div><%} %>
			</div>
			<%} %>
			</div>
		</div> 
		
		<%--  <h1 class="break"></h1> 
		
						<%if(actionlist.size()>=0){ %>
							
					<div align="center">
						<div style="text-align: center ; padding-right: 15px; " ><h3 style="text-decoration: underline;">Annexure - AI</h3></div>
						<div style="text-align: center;  " class="lastpage" id="lastpage"><h2>ACTION ITEM DETAILS</h2></div>
					
						<table style="  margin-left:10px; font-size: 16px; border-collapse: collapse ;border: 1px solid black ;margin-right: 10px;">
						<tbody>
							<tr>
								<th  class="sth" style=" max-width: 40px"> SN </th>
								<th  class="sth" style=" max-width: 110px"> Action Id</th>	
								<th  class="sth" style=" max-width: 600px"> Item</th>				
								<th  class="sth" style=" max-width: 100px"> Responsibility </th>					
								<th  class="sth" style=" width: 100px"> PDC</th>
							</tr>
							
							<% 	int count =1;
							  	Iterator actIterator = actionlist.entrySet().iterator();
								while(actIterator.hasNext()){	
								Map.Entry mapElement = (Map.Entry)actIterator.next();
					            String key = ((String)mapElement.getKey());
					            ArrayList<Object[]> values=(ArrayList<Object[]>)mapElement.getValue();
								%>
								<tr>
									<td class="std" style="text-align: center;"> <%=count%></td>
									<td  class="std">
										
										<%	int count1=0;
											for(Object obj[]:values){
												 count1++; %>
												<%if(count1==1 ){ %>
													<%if(obj[3]!=null){ %> <%= obj[3]%><%}else{ %> - <%} %>
												<%}else if(count1==values.size() ){ %>
													<%if(obj[3]!=null){ %> <br> - <br> <%= obj[3]%> <%}else{ %> - <%} %>
												<%} %>
										<%} %>
									</td>
									
									<td  class="std" style="padding-left: 5px;padding-right: 5px;text-align: justify;"><%= values.get(0)[1]  %></td>
									<td  class="std" >
									<%	int count2=0;
										for(Object obj[]:values){ %>
										<%if(obj[13]!=null){ %> <%= obj[13]%>,&nbsp;<%=obj[14] %>
											<%if(count2>=0 && count2<values.size()-1){ %>
											,&nbsp;
											<%} %>
										<%}else{ %> - <%} %>
									<%count2++;} %>
									</td>                       						
									<td  class="std"><%if( values.get(0)[5]!=null){ %> <%=sdf.format(sdf1.parse(values.get(0)[5].toString()))%> <%}else{ %> - <%} %></td>
								</tr>				
							<% count++;} %>
						</tbody>
					</table>
					</div>
					<br>	
				<%} %> --%>
	
	</div>
	</body>
</html>

