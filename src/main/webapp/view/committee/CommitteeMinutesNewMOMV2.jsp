<%@page import="java.util.regex.Matcher"%>
<%@page import="java.util.regex.Pattern"%>
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
	List<List<Object[]>> overallfinance = (List<List<Object[]>>) request.getAttribute("overallfinance");
	List<Object[]> ProjectDetail = (List<Object[]>)request.getAttribute("ProjectDetails");
	Map<String,List<Object[]>> milestoneBriefingMap = (Map<String,List<Object[]>>)request.getAttribute("milestoneBriefingMap");

	
	String labcode =(String) session.getAttribute("labcode");
	// new
	
	int index = 1;
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
          margin-left: 50px;
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

table{
	border-collapse: collapse;
}

</style>
<meta charset="ISO-8859-1">
<title><%=committeescheduleeditdata[8]%> Minutes View</title>
</head>
<body>
	<div id="container pageborder" align="center"  class="firstpage" id="firstpage">
	
		  <div class="firstpage" id="firstpage"> 	
			<br>
			<div align="center" ><h1><%=committeescheduleeditdata[8]!=null?committeescheduleeditdata[8].toString().toUpperCase():" - " %><%if(meetingcount>0){ %>#<%=meetingcount %><%} %></h1></div>
			<!-- MINUTES OF MEETING -->

			<%-- <div align="center" >
			<h2 style="margin-bottom: 2px;"><%=committeescheduleeditdata[7]!=null?committeescheduleeditdata[7].toString().toUpperCase():" - "%>  (<%=committeescheduleeditdata[8]!=null?committeescheduleeditdata[8].toString().toUpperCase():" - " %><%if(meetingcount>0){ %>&nbsp;&nbsp;#<%=meetingcount %><%} %>) </h2>
			</div> --%>				
				<%if(Integer.parseInt(projectid)>0){ %>					
					<!-- <h3 style="margin-top: 5px; margin-bottom: 5px">For</h3> -->	  
				    <h2 style="margin-top: 3px">Project  : &nbsp;<%=projectdetails[1]!=null?projectdetails[1].toString(): " - " %>  (<%=projectdetails[4]!=null?projectdetails[4].toString(): " - "%>)</h2>
				<%}else if(Integer.parseInt(divisionid)>0){ %>					
					<!-- <h3 style="margin-top: 5px; margin-bottom: 5px">For</h3> -->	  
			 	   	<h2 style="margin-top: 3px">Division :&nbsp;<%=divisiondetails[2]!=null?divisiondetails[2].toString(): " - " %>  (<%=divisiondetails[1]!=null?divisiondetails[1].toString(): " - "%>)</h2>
				<%}else if(Integer.parseInt(initiationid)>0){ %>					
					<!-- <h3 style="margin-top: 5px; margin-bottom: 5px">For</h3>	 -->  
				    <h2 style="margin-top: 3px">Pre-Project  : &nbsp;<%=initiationdetails[2]!=null?initiationdetails[2].toString(): " - " %>  (<%=initiationdetails[1]!=null?initiationdetails[1].toString(): " - "%>)</h2>
				<%}else{%>
					<br><br><br><br><br>
				<%} %>

				<div align="center">
					<h1>MINUTES OF MEETING</h1>
				</div>
				<br><br>
				<figure><img style="width: 4cm; height: 4cm"  src="data:image/png;base64,<%=lablogo%>"></figure>   
				<br><br>
				<table style="align: center; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; max-width: 650px; font-size: 16px"  >
					<tr style="margin-top: 10px">
						 <th  style="text-align: center; width: 650px;font-size: 20px;text-decoration: underline;  ">Meeting Id</th></tr><tr>
						 <th  style="text-align: center;  width: 650px;font-size: 20px  "> <%=committeescheduleeditdata[11]!=null?committeescheduleeditdata[11].toString(): " - " %> </th>				
					 </tr>
				</table>
				
				<br><br>
		 <table style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; max-width: 650px; font-size: 16px"  >
			 <tr>
				 <th  style="text-align: center; width: 650px;font-size: 20px;text-decoration: underline;  "> Meeting Date</th>
				 <th  style="text-align: center;  width: 650px;font-size: 20px;text-decoration: underline;   ">Meeting Time</th>
			 </tr>
			
			 <tr>
				 <td  style="text-align: center;  width: 650px;font-size: 20px ;padding-top: 5px"> <b><%=sdf3.format(sdf1.parse(committeescheduleeditdata[2].toString()))%></b></td>
				 <td  style="text-align: center;  width: 650px;font-size: 20px ;padding-top: 5px "> <b><%=committeescheduleeditdata[3]!=null?committeescheduleeditdata[3].toString(): " - "%></b></td>
			 </tr>
			 
		 </table>
		 
		 <table style="align: center; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; max-width: 650px; font-size: 16px" >
					<tr style="margin-top: 10px">
						 <th  style="text-align: center; width: 650px;font-size: 20px;text-decoration: underline; "> Meeting Venue </th></tr><tr>
						 <th  style="text-align: center;  width: 650px;font-size: 20px  "> <% if(committeescheduleeditdata[12]!=null){ %><%=committeescheduleeditdata[12] %> <%}else{ %> - <%} %></th>				
					 </tr>
				</table>
			
			<br><br>
			<div align="center" ><h3><%=labdetails[2] !=null?labdetails[2].toString(): " - "%> (<%=labdetails[1]!=null?labdetails[1].toString(): " - "%>)</h3></div>
			
			<div align="center" ><h3><%=labdetails[4]!=null?labdetails[2].toString(): " - " %>, &nbsp;<%=labdetails[5] !=null?labdetails[5].toString(): " - "%>, &nbsp;<%=labdetails[6]!=null?labdetails[6].toString(): " - " %></h3></div>
		</div>  
		
 <h1 class="break"></h1> 
<!-- ------------------------------------------------------- members --------------------------------- -->


<%if(invitedlist.size()>0){ %>
<% ArrayList<String> membertypes=new ArrayList<String>(Arrays.asList("CC","CS","PS","CI","CW","CO","CH"));

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
<h3>MINUTES OF (<%if(meetingcount>0){ %>#<%=meetingcount %><%} %>) PMRC FOR</h3>
<%if(Integer.parseInt(projectid)>0){ %>					
<!-- <h3 style="margin-top: 5px; margin-bottom: 5px">For</h3> -->	  
   <h3 style="margin-top: 3px">Project  : &nbsp;<%=projectdetails[1]!=null?projectdetails[1].toString(): " - " %>  (<%=projectdetails[4]!=null?projectdetails[4].toString(): " - "%>) held on: <%=sdf3.format(sdf1.parse(committeescheduleeditdata[2].toString()))%></h3>
<%}else if(Integer.parseInt(divisionid)>0){ %>					
<!-- <h3 style="margin-top: 5px; margin-bottom: 5px">For</h3> -->	  
   	<h3 style="margin-top: 3px">Division :&nbsp;<%=divisiondetails[2]!=null?divisiondetails[2].toString(): " - " %>  (<%=divisiondetails[1]!=null?divisiondetails[1].toString(): " - "%>)</h3>
<%}else if(Integer.parseInt(initiationid)>0){ %>					
<!-- <h3 style="margin-top: 5px; margin-bottom: 5px">For</h3>	 -->  
   <h3 style="margin-top: 3px">Pre-Project  : &nbsp;<%=initiationdetails[2]!=null?initiationdetails[2].toString(): " - " %>  (<%=initiationdetails[1]!=null?initiationdetails[1].toString(): " - "%>)</h3>
<%}else{%>
<br><br><br><br><br>
<%} %>
<h3>Record/File No: <%=committeescheduleeditdata[11]!=null?committeescheduleeditdata[11].toString(): " - " %> dt: <%=sdf3.format(sdf1.parse(committeescheduleeditdata[2].toString()))%> </h3>
<br>
<!-- <h2>ATTENDANCE</h2> -->
<div align="left" style="font-weight: bold;margin-left:10px;"><%=index++ %>. Following Members were present during the Meeting. </div>
<%if(specialMembers.size()>0) {%>
<div align="left" style="font-weight: bold;margin-left:10px;">Special Members  </div>
<% int i=0;
for( Object[]obj:specialMembers){ %>
<p style="padding: 0px;margin:0px;margin-left:10px;padding-top:7px;font-weight: 600;"><%=++i %>. <%=obj[6]!=null?obj[6].toString(): " - "%>,<%=obj[7]!=null?obj[7].toString(): " - "%> ( <%=obj[11]!=null?obj[11].toString(): " - " %> )</p>
<%} %>
<p>

<%} %>
<br>
<table style="align: left;margin:auto; margin-top: 10px; margin-bottom: 10px; width: 680px; font-size: 16px; border-collapse:collapse;" >	
	
	 <tr>
		 <th style="text-align: center ;padding: 5px;border: 1px solid black;width: 10px; ">SN</th>
		 <th style="text-align: center ;padding: 5px;border: 1px solid black;width: 220px; ">Name</th>
		 <th style="text-align: center ;padding: 5px;border: 1px solid black;width: 280px; "> Designation </th>
		 <th style="text-align: center ;padding: 5px;border: 1px solid black;width: 280px; ">  Estt. / Agency </th>
		 <!-- <th style="text-align: center ;padding: 5px;border: 1px solid black;width: 140px; ">Role</th> -->
	 </tr>
	 <!--  <tr>
		 <th colspan="4" style="text-align: left; font-weight: 700; border: 1px solid black; padding: 5px; padding-left: 15px">Members Present</th>
	 </tr> -->
	 <%
	
	 
	 if(memPresent > 0){ %>
	 
	 <% 
	 	for(int i=0;i<invitedlist.size();i++)
		{
	 	if(invitedlist.get(i)[4].toString().equals("P") && membertypes.contains( invitedlist.get(i)[3].toString()) )
	 	{ j++;
	 	if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CS") ){ membersec=invitedlist.get(i);}
	 	%>
	 	
	 	 <tr>
	 	 <td style="border: 1px solid black; padding: 5px;text-align: center"><%=j%> </td>
	 	  	<td style="border: 1px solid black; padding: 5px;text-align: left">  
	 			<%= invitedlist.get(i)[6]!=null?invitedlist.get(i)[6].toString(): " - "%> 
		 	</td>
		 	<td style="border: 1px solid black; padding: 5px;text-align: left;">  
	 			<%=invitedlist.get(i)[7]!=null?invitedlist.get(i)[7].toString(): " - " %>  <%=invitedlist.get(i)[15]!=null ? ", "+invitedlist.get(i)[15].toString():(invitedlist.get(i)[14]!=null ?", "+invitedlist.get(i)[14].toString(): "")  %>
		 	</td>	
		 	<td style="border: 1px solid black; padding: 5px;text-align: left;">  
				<%= invitedlist.get(i)[11]!=null?invitedlist.get(i)[11].toString(): " - "%>  
		 	</td>	
	 		</tr>
	 <%}
	 } %>
	 
	 <% } %>
	
	 <%if(ParPresent > 0){ %>
	
	<!--  <tr>
		 <th colspan="4" style="text-align: left; font-weight: 700; width: 650px;border: 1px solid black; padding: 5px; padding-left: 15px">Other Invitees&nbsp;/&nbsp;Participants </th>
	 </tr> -->
	 
	 <%
		
	 for(int i=0;i<invitedlist.size();i++)
		{
	 	if(invitedlist.get(i)[4].toString().equals("P") && !membertypes.contains( invitedlist.get(i)[3].toString()) )
	 	{ j++;
	 	addcount++;
	 	if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CS") ){ membersec=invitedlist.get(i);}
	 	%>
	 	
	 	 <tr>
	 	 <td style="border: 1px solid black; padding: 5px;text-align: center"> <%=j%> </td>
	 	  	<td style="border: 1px solid black; padding: 5px;text-align: left">  
	 			<%= invitedlist.get(i)[6]!=null?invitedlist.get(i)[6].toString(): " - "%> 
		 	</td>
		 	<td style="border: 1px solid black; padding: 5px;text-align: left;">  
	 			<%=invitedlist.get(i)[7]!=null?invitedlist.get(i)[7].toString(): " - " %>  <%=invitedlist.get(i)[15]!=null ?", "+invitedlist.get(i)[15].toString():(invitedlist.get(i)[14]!=null ?", "+invitedlist.get(i)[14].toString(): "")  %>
		 	</td>	
		 	<td style="border: 1px solid black; padding: 5px;text-align: left;">  
				<%= invitedlist.get(i)[11]!=null?invitedlist.get(i)[11].toString(): " - "%>  
		 	</td>	
	 		</tr>
	 <%}
	 } %>
	 
	  <%if(addcount==0)
	  {%>
		 	<tr><th colspan="4" style="text-align:center; font-weight: 20; width: 650px;border: 1px solid black; padding: 5px; padding-left: 15px">Nil</th> </tr>
	  <%}%>
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
	 	{count++; j++;
	 	if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CS") ){ membersec=invitedlist.get(i);}
	 	%>
	 	 <tr > 	
	 	  <td style="border: 1px solid black; padding: 5px;text-align: center"> <%=j%> </td>
	 	 <td style="border: 1px solid black ;padding: 5px;text-align: left " >  
	 			<%= invitedlist.get(i)[6]!=null?invitedlist.get(i)[6].toString(): " - "%> 
		 	</td>
		 	<td style="border: 1px solid black; padding: 5px;text-align: left;">  
	 			<%=invitedlist.get(i)[7]!=null?invitedlist.get(i)[7].toString(): " - " %>  <%=invitedlist.get(i)[15]!=null ?", "+invitedlist.get(i)[15].toString():(invitedlist.get(i)[14]!=null ?", "+invitedlist.get(i)[14].toString(): "")  %>
		 	</td>	
		 	<td style="border: 1px solid black; padding: 5px;text-align: left;">  
				<%= invitedlist.get(i)[11]!=null?invitedlist.get(i)[11].toString(): " - "%>  
		 	</td>	
	 	</tr>
	 	
	 <%}
	 } %>
	 
	 <%if(count==0){ %>
	 	<tr><th colspan="4" style="text-align:center; font-weight: 20; width: 650px;border: 1px solid black; padding: 5px; padding-left: 15px">Nil</th></tr>
	 <%} %>
	
	<%} %>
	  
	  <%if(parAbscent > 0){ %>
	  
	 <tr >
			<th colspan="4" style="text-align: left; font-weight: 700; width: 650px;border: 1px solid black; padding: 5px; padding-left: 15px">Other Invitees&nbsp;/&nbsp;Participants Absent</th>
		</tr>
	
	 
	 
	<% 
	int count1=0;
	for(int i=0;i<invitedlist.size();i++)	
	 {
	 	if(invitedlist.get(i)[4].toString().equals("N")&& !membertypes.contains( invitedlist.get(i)[3].toString()) )
	 	{count1++; j++; 
	 	if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CS") ){ membersec=invitedlist.get(i);}
	 	%>
	 	 <tr > 	
	 	  <td style="border: 1px solid black; padding: 5px;text-align: center"> <%=j%> </td>
	 	 <td style="border: 1px solid black ;padding: 5px;text-align: left " >  
	 			<%= invitedlist.get(i)[6]!=null?invitedlist.get(i)[6].toString(): " - "%> 
		 	</td>
		 	<td style="border: 1px solid black; padding: 5px;text-align: left;">  
	 			<%=invitedlist.get(i)[7]!=null?invitedlist.get(i)[7].toString(): " - " %>  <%=invitedlist.get(i)[15]!=null ?", "+invitedlist.get(i)[15].toString():(invitedlist.get(i)[14]!=null ?", "+invitedlist.get(i)[14].toString(): "")  %>
		 	</td>	
		 	<td style="border: 1px solid black; padding: 5px;text-align: left;">  
				<%= invitedlist.get(i)[11]!=null?invitedlist.get(i)[11].toString(): " - "%>  
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
	
 
<!-- -------------------------------------------------------members----------------------------- -->
		<% 
		for (Object[] committeemin : committeeminutes) {
			if (committeemin[0].toString().equals("1")) { 
				List<Object[]> filteredList = speclists.stream()
					    .filter(spec -> spec[3] != null 
					            && spec[3].toString().equalsIgnoreCase("1"))
					    .collect(Collectors.toList());
				if(filteredList!=null && !filteredList.isEmpty()){
			%>
		
		<table style="margin-top: 0px; margin-left: 10px; width: 650px; font-size: 16px; border-collapse: collapse;">
			<tbody>
				<tr>
					<th colspan="8" style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=index++ %>.&nbsp;&nbsp;&nbsp;<%=committeemin[1]!=null?committeemin[1].toString(): " - "%></th>
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
				
			<% }}else if (committeemin[0].toString().equals("2")) {
				List<Object[]> filteredList = speclists.stream()
					    .filter(spec -> spec[3] != null 
					            && spec[3].toString().equalsIgnoreCase("2"))
					    .collect(Collectors.toList());
				if(filteredList!=null && !filteredList.isEmpty()){ %>
		
		<table style="margin-top: 0px; margin-left: 10px; width: 680px; font-size: 16px; border-collapse: collapse;">
			<tbody>
				<tr>
					<th colspan="8" style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=index++ %>.&nbsp;&nbsp;&nbsp;<%=committeemin[1]!=null?committeemin[1].toString(): " - "%></th>
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
					
				<%  }}else if (committeemin[0].toString().equals("3")) { %>
					<!-- <h1 class="break"></h1> --> 
						 
					<table style="margin-top: 20px; margin-left: 15px; width: 650px; font-size: 16px; border-collapse: collapse;">
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;"><%=index %><!--  (a) Record of Discussions and Action Points of Current Meeting. --></th>
						</tr>
						<!-- <tr>
							<td colspan="8" style="text-align: center; padding: 5px;">Item Code/Type : A: Action, C: Discussion, D: Decision, P: Presentation, R: Recommendation, I:Issue, K:Risk</td>
						</tr> -->
					</table>	
				
			<div align="left" style="margin-left:30px;">
				<% Map<String,List<Object[]>> agendaMap = speclists.stream()
			    .filter(spec -> spec[3] != null 
			            && (spec[3].toString().equalsIgnoreCase("3") || (spec[3].toString().equalsIgnoreCase("5") && !spec[7].toString().equalsIgnoreCase("R"))))
			    .collect(Collectors.groupingBy(obj -> obj[10]!=null ? obj[10].toString() : null,LinkedHashMap::new,Collectors.toList()));
				int agendaIndex = 1;	
				for(Map.Entry<String,List<Object[]>> map: agendaMap.entrySet()){
					String agenda = map.getKey();
					List<Object[]> agendaList = map.getValue();
					if(agendaList!=null && !agendaList.isEmpty()){
				%>
					<h4 style="margin-top:15px!important;"><%=agendaIndex++ %>. <%=agenda %></h4>
					<!-- <ul>  -->
						<% for(Object[] obj: agendaList){ %>
							<%=obj[1]!=null ? obj[1].toString() : " - " %>
						<%}%>
				   <!-- </ul> -->
					<br>
				<%}}%>
			</div>
			<%if(lastpmrcactions!=null && !lastpmrcactions.isEmpty()){ %>
			<table style="margin-top: 0px; margin-left: 10px; width: 650px; font-size: 16px; border-collapse: collapse;">
								<tr>
									<th colspan="8" style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=index %>. (b)&nbsp;<%=committeemin[1]!=null?committeemin[1].toString(): " - "%></th>
								</tr>
							</table>	
   				

				<table style=" margin:auto; width: 680px; margin-top:5px;font-size: 16px; border-collapse: collapse;border: 1px solid black" >
								<thead >
									<tr>
										<th class="std"  style="width: 30px;"  >SN</th>
										<th class="std"  style="width: 80px;" > ID</th>
										<th class="std"  style="width: 300px;" >Action Point</th>
										<th class="std"  style="width: 80px; " > ADC<br>PDC</th>
										
										<th class="std"  style="width: 155px;" >Responsibility</th>
										<!-- <th class="std"  style="width: 40px;"  >Status(DD)</th>			 -->
									</tr>
								</thead>
								
								
								<tbody>
											<%if(lastpmrcactions.size()==0){ %>
								<tr><td colspan="5"  style="text-align: center;" > Nil</td></tr>
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
	
								</tr>			
							<%i++;
							}}} %>
							</tbody>
								</tbody>
								</table>			
			<%} } else if(committeemin[0].toString().equals("4") ) { 
			%>
				<%-- if(MilestoneDetails6 !=null && !MilestoneDetails6.isEmpty()){ %>
				<br>
					<table style="margin-top: -15px; margin-left: 10px; width: 650px; font-size: 16px; border-collapse: collapse;">
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=++index %>.&nbsp;&nbsp;&nbsp;<%=committeemin[1]!=null?committeemin[1].toString(): " - "%> </th>
						</tr>
					</table>
					<br>
					<table style="margin:auto; width:680px; margin-top:5px;font-size: 16px; border-collapse: collapse;border: 1px solid black" >
						     <thead>
									
						     
						         		 <tr>
										 <th class="" style="border:1px solid black;width: 30px !important; ">MS</th>
										 <th class="" style="border:1px solid black;width: 50px !important; padding:8px;">L</th>
										 <th class="" style="border:1px solid black;width:600px; ">System/ Subsystem/ Activities</th>
										 <th class="" style="border:1px solid black;width:120px; ">  PDC</th>
										 <th class="" style="border:1px solid black;width:100px; "> Progress</th>
<!-- 										 <th class="std" style="border: 1px solid black;max-width:70px; "> Status</th>
 -->										 <th class="" style="border: 1px solid black;width:300px; "> Remarks</th> 
										 
									</tr>
								</thead>
		
								<tbody>
									<% if(MilestoneDetails6 !=null && MilestoneDetails6.size()>0){ 
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
											<td class=""  style=" border: 1px solid black;text-align: center;">M<%=obj[0]!=null?StringEscapeUtils.escapeHtml4(obj[0].toString()): " - " %></td>
											<td class=""  style=" border: 1px solid black;text-align: center;" >
												<%
												
												if(obj[21].toString().equals("0")) { %>
													&nbsp;&nbsp;&nbsp;
												<%	milcountA=1;
													milcountB=1;
													milcountC=1;
													milcountD=1;
													milcountE=1;
												}else if(obj[21].toString().equals("1")) { 
												for(Map.Entry<Integer,String>entry:treeMapLevOne.entrySet()){
													if(entry.getKey().toString().equalsIgnoreCase(obj[2].toString())){%>
														<%=entry.getValue()!=null?StringEscapeUtils.escapeHtml4(entry.getValue()): " - " %>
												<%}}
												%>
												
												<% 
												}else if(obj[21].toString().equals("2")) { 
													for(Map.Entry<Integer,String>entry:treeMapLevTwo.entrySet()){
														if(entry.getKey().toString().equalsIgnoreCase(obj[3].toString())){%>
															<%=entry.getValue()!=null?StringEscapeUtils.escapeHtml4(entry.getValue()): " - " %>
													<%}}
												
												
												%>
													
												<%
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

											<td class=""  style=" border: 1px solid black;text-align: left; <%if(obj[21].toString().equals("0")) {%>font-weight: bold;<%}%>">
												<%if(obj[21].toString().equals("0")) {%>
													<%=obj[10]!=null?StringEscapeUtils.escapeHtml4(obj[10].toString()): " - " %>
												<%}else if(obj[21].toString().equals("1")) { %>
													&nbsp;&nbsp;<%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - " %>
												<%}else if(obj[21].toString().equals("2")) { %>
													&nbsp;&nbsp;<%=obj[12]!=null?StringEscapeUtils.escapeHtml4(obj[12].toString()): " - " %>
												<%}else if(obj[21].toString().equals("3")) { %>
													&nbsp;&nbsp;<%=obj[13]!=null?StringEscapeUtils.escapeHtml4(obj[13].toString()): " - " %>
												<%}else if(obj[21].toString().equals("4")) { %>
													&nbsp;&nbsp;<%=obj[14]!=null?StringEscapeUtils.escapeHtml4(obj[14].toString()): " - " %>
												<%}else if(obj[21].toString().equals("5")) { %>
													&nbsp;&nbsp;<%=obj[15]!=null?StringEscapeUtils.escapeHtml4(obj[15].toString()): " - " %>
												<%} %>
											</td>
											<td class=""  style=" border: 1px solid black;text-align: center;">
												<%=obj[9]!=null?fc.sdfTordf(obj[9].toString()):"-" %>
												<%if(obj[8]!=null && obj[9]!=null && !LocalDate.parse(obj[8].toString()).isEqual(LocalDate.parse(obj[9].toString())) ) {%>
													<br><%=obj[8]!=null?fc.sdfTordf(obj[8].toString()): " - "  %>
												<%} %>
											</td>											<td class=""  style=" border: 1px solid black;text-align: center;"><%=obj[17]!=null?StringEscapeUtils.escapeHtml4(obj[17].toString()): " - " %>%</td>											
											<td class=""  style=" border: 1px solid black;text-align: left;"><%if(obj[23]!=null){%><%=StringEscapeUtils.escapeHtml4(obj[23].toString())%><%} %></td>
										</tr>
									<%milcount1++;}} %>
								<%} else{ %>
								<tr><td class="" colspan="8" style="border: 1px solid black; text-align: center;padding:5px;" > No SubSystems</td></tr>
								<%}%>

						</tbody>
					</table>
					<%} %> 
		
			<%}else if (committeemin[0].toString().equals("5") ){%>  
			--%>
			<%
				if(milestoneBriefingMap!=null && !milestoneBriefingMap.isEmpty()){
						List<Object[]> list = milestoneBriefingMap.get("5");
						
						if(list!=null && !list.isEmpty()){
			
			%>
					<table style="margin-top: 0px; margin-left: 10px; width: 650px; font-size: 16px; border-collapse: collapse;">
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=++index %>.&nbsp;&nbsp;&nbsp;<%=committeemin[1]!=null?committeemin[1].toString(): " - "%>.</th>
 							<%-- <th colspan="8" style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=++index %>.&nbsp;&nbsp;&nbsp;Milestones achieved prior to this PMRC period.</th> --%>
						</tr>
					</table>	
					<br>
								
					<%-- <table style=" margin:auto; width:680px; margin-top:5px;font-size: 16px; border-collapse: collapse;border: 1px solid black" >
						<thead>
							<tr>
								<th class="std" style="width:5%!important;">SN</th>
								<th class="std" style="width:95%!important;" >Details</th>
							</tr>
						</thead>
						<tbody>
							<% int num = 1;
							if(list!=null && !list.isEmpty()){
							for(Object[] obj: list){ %>
								<tr>
									<td style="text-align: center;border: 1px solid black;padding:5px;"  class="text-center width-5 std"><%= num++ %></td>
									<td style="border: 1px solid black;padding:5px;text-align: left;"  class="std">
										
										<%
								String editorContent = obj[2]!=null?obj[2].toString(): " - ";
								//System.out.println(editorContent);

								// First convert ordered list to numbered format
								Pattern pattern = Pattern.compile("(?i)<ol[^>]*>(.*?)</ol>", Pattern.DOTALL);
								Matcher matcher = pattern.matcher(editorContent);
								
								while (matcher.find()) {
								    String olContent = matcher.group(1);
								
								    Pattern liPattern = Pattern.compile("(?i)<li[^>]*>(.*?)</li>");
								    Matcher liMatcher = liPattern.matcher(olContent);
								
								    StringBuilder numberedList = new StringBuilder();
								    int znum = 1;
								
								    while (liMatcher.find()) {
								        numberedList.append(znum++)
								                    .append(". ")
								                    .append(liMatcher.group(1).trim())
								                    .append("<br/>");
								    }
								
								    editorContent = editorContent.replace(matcher.group(0), numberedList.toString());
								}
								
								// Then remove other unwanted tags
								editorContent = editorContent.replaceAll("(?i)</?(p|div|)[^>]*>", "");
								
								%>
								<%= editorContent %>
									</td>
								</tr>
							<%}}else{ %>
								<tr>
									<td style="text-align: center;border: 1px solid black;padding:5px;" colspan="2">No Data Available</td>
								</tr>
							<%} %>
						</tbody>
					</table> --%>
					<ul style="list-style-type:none; width:680px; margin-top:5px; font-size:16px; padding-left:0;">
						<%
						if(list != null && !list.isEmpty()){
						    for(Object[] obj : list){
						%>
						    <li style="margin-bottom:10px;text-align:left;">
						        <%
						        String editorContent = obj[2] != null ? obj[2].toString() : " - ";
						
						        // Convert inner <ol> to numbered format
						        Pattern pattern = Pattern.compile("(?i)<ol[^>]*>(.*?)</ol>", Pattern.DOTALL);
						        Matcher matcher = pattern.matcher(editorContent);
						
						        while (matcher.find()) {
						            String olContent = matcher.group(1);
						
						            Pattern liPattern = Pattern.compile("(?i)<li[^>]*>(.*?)</li>");
						            Matcher liMatcher = liPattern.matcher(olContent);
						
						            StringBuilder numberedList = new StringBuilder();
						            int znum = 1;
						
						            while (liMatcher.find()) {
						                numberedList.append(znum++)
						                              .append(". ")
						                              .append(liMatcher.group(1).trim())
						                              .append("<br/>");
						            }
						
						            editorContent = editorContent.replace(matcher.group(0), numberedList.toString());
						        }
						
						        // Remove unwanted tags
						        editorContent = editorContent.replaceAll("(?i)</?(p|div|)[^>]*>", "");
						        %>
						
						        <%= editorContent %>
						    </li>
						<%
						    }
						} else {
						%>
						    <li>No Data Available</li>
						<%
						}
						%>
						</ul>
					<%} }%>
					<br>
					<%
					if(milestoneBriefingMap!=null && !milestoneBriefingMap.isEmpty()){
					List<Object[]> list = milestoneBriefingMap.get("6");
					if(list!=null && !list.isEmpty()) {%>
					
					<table style="margin-top: 0px; margin-left: 10px; width: 650px; font-size: 16px; border-collapse: collapse;">
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=++index %>.&nbsp;&nbsp;&nbsp; Details of work and current status of sub system with major milestones (since last PMRC).</th>
						</tr>
					</table>	
					<br>
					
					<%-- <table style=" margin:auto; width:680px; margin-top:5px;font-size: 16px; border-collapse: collapse;border: 1px solid black" >
						<thead>
							<tr>
								<th class="std" style="width:5%!important;">SN</th>
								<th class="std" style="width:95%!important;" >Details</th>
							</tr>
						</thead>
						<tbody>
							<% int num1 = 1;
							if(list!=null && !list.isEmpty()){
							for(Object[] obj: list){ %>
								<tr>
									<td style="text-align: center;border: 1px solid black;padding:5px;"  class="text-center width-5 std"><%= num1++ %></td>
									<td style="border: 1px solid black;padding:5px;text-align: left;"  class="std">
										
										<%
								String editorContent = obj[2]!=null?obj[2].toString(): " - ";
								//System.out.println(editorContent);

								// First convert ordered list to numbered format
								Pattern pattern = Pattern.compile("(?i)<ol[^>]*>(.*?)</ol>", Pattern.DOTALL);
								Matcher matcher = pattern.matcher(editorContent);
								
								while (matcher.find()) {
								    String olContent = matcher.group(1);
								
								    Pattern liPattern = Pattern.compile("(?i)<li[^>]*>(.*?)</li>");
								    Matcher liMatcher = liPattern.matcher(olContent);
								
								    StringBuilder numberedList = new StringBuilder();
									int znum = 0;
									
								    while (liMatcher.find()) {
								        numberedList.append(znum++)
								                    .append(". ")
								                    .append(liMatcher.group(1).trim())
								                    .append("<br/>");
								    }
								
								    editorContent = editorContent.replace(matcher.group(0), numberedList.toString());
								}
								
								// Then remove other unwanted tags
								editorContent = editorContent.replaceAll("(?i)</?(p|div|)[^>]*>", "");
								
								%>
								<%= editorContent %>
									</td>
								</tr>
							<%}}else{ %>
								<tr>
									<td style="text-align: center;border: 1px solid black;padding:5px;" colspan="2">No Data Available</td>
								</tr>
							<%} %>
						</tbody>
					</table> --%>
					
					<ul style="list-style-type:none; width:680px; margin-top:5px; font-size:16px; padding-left:0;">
						<%
						if(list != null && !list.isEmpty()){
						    for(Object[] obj : list){
						%>
						    <li style="margin-bottom:10px;text-align:left;">
						        <%
						        String editorContent = obj[2] != null ? obj[2].toString() : " - ";
						
						        // Convert inner <ol> to numbered format
						        Pattern pattern = Pattern.compile("(?i)<ol[^>]*>(.*?)</ol>", Pattern.DOTALL);
						        Matcher matcher = pattern.matcher(editorContent);
						
						        while (matcher.find()) {
						            String olContent = matcher.group(1);
						
						            Pattern liPattern = Pattern.compile("(?i)<li[^>]*>(.*?)</li>");
						            Matcher liMatcher = liPattern.matcher(olContent);
						
						            StringBuilder numberedList = new StringBuilder();
						            int znum = 1;
						
						            while (liMatcher.find()) {
						                numberedList.append(znum++)
						                              .append(". ")
						                              .append(liMatcher.group(1).trim())
						                              .append("<br/>");
						            }
						
						            editorContent = editorContent.replace(matcher.group(0), numberedList.toString());
						        }
						
						        // Remove unwanted tags
						        editorContent = editorContent.replaceAll("(?i)</?(p|div|)[^>]*>", "");
						        %>
						
						        <%= editorContent %>
						    </li>
						<%
						    }
						} else {
						%>
						    <li>No Data Available</li>
						<%
						}
						%>
						</ul>
					<%}} %>
			<%}else if (committeemin[0].toString().equals("6") ) 
			{  if(projectFinancialDetails!=null && projectFinancialDetails.size() > 0) { %>
					<table style="margin-top: 0px; margin-left: 10px; width: 650px; font-size: 16px; border-collapse: collapse;">
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=++index %>.&nbsp;&nbsp;&nbsp;<%=committeemin[1]!=null?committeemin[1].toString(): " - "%> </th>
						</tr>
					</table>											
					<% 
					    double totSanctionCost=0,totReSanctionCost=0,totFESanctionCost=0;
						double totExpenditure=0,totREExpenditure=0,totFEExpenditure=0;
						double totCommitment=0,totRECommitment=0,totFECommitment=0,totalDIPL=0,totalREDIPL=0,totalFEDIPL=0;
						double totBalance=0,totReBalance=0,totFeBalance=0,btotalRe=0,btotalFe=0;
						
						%>
					<%if(Long.parseLong(projectid) >0 ) { %>
							
							<table style="margin:auto; width:680px; margin-top:5px;font-size: 16px; border-collapse: collapse;border: 1px solid black;" >
								    <thead>
								        <tr>
								           	<td class="std" colspan="2" align="center" style="border:1px solid black;"><b>Head</b></td>
								           	<td class="std" colspan="2" align="center" style="border:1px solid black;"><b>Sanction</b></td>
									         <td class="std" colspan="2" align="center" style="border:1px solid black;"><b>Expenditure</b></td>
									        <td class="std" colspan="2" align="center" style="border:1px solid black;"><b>Out Commitment</b> </td>
								            <td class="std" colspan="2" align="center" style="border:1px solid black;"><b>Balance</b></td>
									        <td class="std" colspan="2" align="center" style="border:1px solid black;"><b>DIPL</b></td>
								            <td class="std" colspan="2" align="center" style="border:1px solid black;"><b>Notional Balance</b></td>
								        </tr>
									    <tr>
											<th class="std" style="border:1px solid black;">SN</th>
										    <th class="std" style="border:1px solid black;">Head</th>
										    <th class="std" style="border:1px solid black;">RE</th>
										    <th class="std" style="border:1px solid black;">FE</th>
										    <th class="std" style="border:1px solid black;">RE</th>
										    <th class="std" style="border:1px solid black;">FE</th>
									        <th class="std" style="border:1px solid black;">RE</th>
									        <th class="std" style="border:1px solid black;">FE</th>
								            <th class="std" style="border:1px solid black;">RE</th>
										    <th class="std" style="border:1px solid black;">FE</th>
										    <th class="std" style="border:1px solid black;">RE</th>
										    <th class="std" style="border:1px solid black;">FE</th>
										    <th class="std" style="border:1px solid black;">RE</th>
										    <th class="std" style="border:1px solid black;">FE</th>
								        </tr>
									</thead>
									<% if(projectFinancialDetails!=null && projectFinancialDetails.size() > 0) { %>
									<tbody>
										<% int counts=1;
										for(ProjectFinancialDetails projectFinancialDetail:projectFinancialDetails){    %>
									 
									    	<tr>
												<td class="std"  align="center" style="border:1px solid black;"><%=counts++ %></td>
												<td class="std"  style=" border: 1px solid black;text-align: left;border:1px solid black;"><%=projectFinancialDetail.getBudgetHeadDescription()!=null?StringEscapeUtils.escapeHtml4(projectFinancialDetail.getBudgetHeadDescription()): " - "%></td>
												<td class="std"  align="right" style="text-align: right; border:1px solid black;"><%=projectFinancialDetail.getReSanction()!=null?df.format(projectFinancialDetail.getReSanction()):" - " %></td>
												<%totReSanctionCost+=(projectFinancialDetail.getReSanction());%>
													<td class="std"  align="right" style="text-align: right;border:1px solid black;"><%=projectFinancialDetail.getFeSanction()!=null?df.format(projectFinancialDetail.getFeSanction()):" - "%></td>
												<%totFESanctionCost+=(projectFinancialDetail.getFeSanction());%>
													<td class="std"  align="right" style="text-align: right;border:1px solid black;"><%=projectFinancialDetail.getReExpenditure()!=null?df.format(projectFinancialDetail.getReExpenditure()):" - " %></td>
												<%totREExpenditure+=(projectFinancialDetail.getReExpenditure());%>
												    <td class="std"  align="right" style="text-align: right;border:1px solid black;"><%=projectFinancialDetail.getFeExpenditure()!=null?df.format(projectFinancialDetail.getFeExpenditure()):" - "%></td>
												<%totFEExpenditure+=(projectFinancialDetail.getFeExpenditure());%>
												    <td class="std"  align="right" style="text-align: right;border:1px solid black;"><%=projectFinancialDetail.getReOutCommitment()!=null?df.format(projectFinancialDetail.getReOutCommitment()):" - "%></td>
												<%totRECommitment+=(projectFinancialDetail.getReOutCommitment());%>
												    <td class="std"  align="right" style="text-align: right;border:1px solid black;"><%=projectFinancialDetail.getFeOutCommitment()!=null?df.format(projectFinancialDetail.getFeOutCommitment()):" - "%></td>
												<%totFECommitment+=(projectFinancialDetail.getFeOutCommitment());%>
													<td class="std"  align="right" style="text-align: right;border:1px solid black;"><%=projectFinancialDetail.getReBalance()!=null && projectFinancialDetail.getReDipl()!=null?df.format(projectFinancialDetail.getReBalance()+projectFinancialDetail.getReDipl()):" - "%></td>
												<%btotalRe+=(projectFinancialDetail.getReBalance()+projectFinancialDetail.getReDipl());%>
													<td class="std"  align="right" style="text-align: right;border:1px solid black;"><%=projectFinancialDetail.getFeBalance()!=null && projectFinancialDetail.getFeDipl()!=null?df.format(projectFinancialDetail.getFeBalance()+projectFinancialDetail.getFeDipl()):" - "%></td>
												<%btotalFe+=(projectFinancialDetail.getFeBalance()+projectFinancialDetail.getFeDipl());%>
													 <td class="std"  align="right"style="text-align: right;border:1px solid black;"><%=projectFinancialDetail.getReDipl()!=null?df.format(projectFinancialDetail.getReDipl()):" - "%></td>
												<%totalREDIPL+=(projectFinancialDetail.getReDipl());%>
													 <td class="std"  align="right" style="text-align: right;border:1px solid black;"><%=projectFinancialDetail.getFeDipl()!=null?df.format(projectFinancialDetail.getFeDipl()):" - "%></td>
												<%totalFEDIPL+=(projectFinancialDetail.getFeDipl());%>
													<td class="std"  align="right" style="text-align: right;border:1px solid black;"><%=projectFinancialDetail.getReBalance()!=null?df.format(projectFinancialDetail.getReBalance()):" - "%></td>
												<%totReBalance+=(projectFinancialDetail.getReBalance());%>
													<td class="std"  align="right" style="text-align: right;border:1px solid black;"><%=projectFinancialDetail.getFeBalance()!=null?df.format(projectFinancialDetail.getFeBalance()):" - "%></td>
												<%totFeBalance+=(projectFinancialDetail.getFeBalance());%>
											</tr>
										<%} %>
																
											<tr>
												<td class="std"  colspan="2"><b>Total</b></td>
												<td class="std"  align="right" style="text-align: right;font-weight: bold;border:1px solid black;"><%=df.format(totReSanctionCost)%></td>
												<td class="std"  align="right" style="text-align: right;font-weight: bold;border:1px solid black;"><%=df.format(totFESanctionCost)%></td>
												<td class="std"  align="right" style="text-align: right;font-weight: bold;border:1px solid black;"><%=df.format(totREExpenditure)%></td>
												<td class="std"  align="right" style="text-align: right;font-weight: bold;border:1px solid black;"><%=df.format(totFEExpenditure)%></td>
												<td class="std"  align="right" style="text-align: right;font-weight: bold;border:1px solid black;"><%=df.format(totRECommitment)%></td>
												<td class="std"  align="right" style="text-align: right;font-weight: bold;border:1px solid black;"><%=df.format(totFECommitment)%></td>
												<td class="std"  align="right" style="text-align: right;font-weight: bold;border:1px solid black;"><%=df.format(btotalRe)%></td>
												<td class="std"  align="right" style="text-align: right;font-weight: bold;border:1px solid black;"><%=df.format(btotalFe)%></td>
												<td class="std"  align="right" style="text-align: right;font-weight: bold;border:1px solid black;"><%=df.format(totalREDIPL)%></td>
												<td class="std"  align="right" style="text-align: right;font-weight: bold;border:1px solid black;"><%=df.format(totalFEDIPL)%></td>
												<td class="std"  align="right" style="text-align: right;font-weight: bold;border:1px solid black;"><%=df.format(totReBalance)%></td>
												<td class="std"  align="right" style="text-align: right;font-weight: bold;border:1px solid black;"><%=df.format(totFeBalance)%></td>
											</tr>
											<tr>
												<td class="std"  colspan="2"><b>GrandTotal</b></td>
												<td class="std"  colspan="2"  style="text-align: right;font-weight: bold;border:1px solid black;"><%=df.format(totReSanctionCost+totFESanctionCost)%></td>
												<td class="std"  colspan="2"  style="text-align: right;font-weight: bold;border:1px solid black;"><%=df.format(totREExpenditure+totFEExpenditure)%></td>
												<td class="std"  colspan="2"  style="text-align: right;font-weight: bold;border:1px solid black;"><%=df.format(totRECommitment+totFECommitment)%></td>
												<td class="std"  colspan="2"  style="text-align: right;font-weight: bold;border:1px solid black;"><%=df.format(btotalRe+btotalFe)%></td>
												<td class="std"  colspan="2"  style="text-align: right;font-weight: bold;border:1px solid black;"><%=df.format(totalREDIPL+totalFEDIPL)%></td>
												<td class="std"  colspan="2"  style="text-align: right;font-weight: bold;border:1px solid black;"><%=df.format(totReBalance+totFeBalance)%></td>
											</tr>
										</tbody>        
										<%}else{ int z= 0;%>
										<%-- <% char fch='a'; for (int z = 0; z < projectidlist.size(); z++) {%> --%>
									     <tbody id="tbody<%=ProjectDetail.get(z)[0].toString()%>">
									     <%int count=0;
									     if(overallfinance!=null && overallfinance.size()>0 && overallfinance.get(z)!=null && overallfinance.get(z).size()>0)  {
									    	for(Object[]obj:overallfinance.get(z)){ 
									    	 %>
									    	 <tr>
									   <td 	align="center"class="bp-74" style="border:1px solid black;padding:5px;"><%=++count %></td>
										<td class="text-justify" style="border:1px solid black;padding:5px;"><b><%=obj[4].toString()%></b></td>
										<td class="text-right" style="border:1px solid black;padding:5px;"><%=obj[5].toString()%></td>
										<td class="text-right" style="border:1px solid black;padding:5px;"><%=obj[6].toString()%></td>
										<td class="text-right" style="border:1px solid black;padding:5px;"><%=obj[7].toString()%></td>
										<td class="text-right" style="border:1px solid black;padding:5px;"><%=obj[8].toString()%></td>
										<td class="text-right" style="border:1px solid black;padding:5px;"><%=obj[9].toString()%></td>
										<td class="text-right" style="border:1px solid black;padding:5px;"><%=obj[10].toString()%></td>
										<td class="text-right" style="border:1px solid black;padding:5px;"><%=obj[11].toString()%></td>
										<td class="text-right" style="border:1px solid black;padding:5px;"><%=obj[12].toString()%></td>
										<td class="text-right" style="border:1px solid black;padding:5px;"><%=obj[13].toString()%></td>
										<td class="text-right" style="border:1px solid black;padding:5px;"><%=obj[14].toString()%></td>
										<td class="text-right" style="border:1px solid black;padding:5px;"><%=obj[15].toString()%></td>
										<td class="text-right" style="border:1px solid black;padding:5px;"><%=obj[16].toString()%></td>
										</tr>
									     <%}%>
									    	 	<tr>
												<td colspan="2"><b>Total</b></td>
												<td align="right" class="text-right" style="border:1px solid black;padding:5px;"><%=overallfinance.get(z).get(0)[17].toString()%></td>
												<td align="right" class="text-right" style="border:1px solid black;padding:5px;"><%=overallfinance.get(z).get(0)[18].toString()%></td>
												<td align="right" class="text-right" style="border:1px solid black;padding:5px;"><%=overallfinance.get(z).get(0)[19].toString()%></td>
												<td align="right" class="text-right" style="border:1px solid black;padding:5px;"><%=overallfinance.get(z).get(0)[20].toString()%></td>
												<td align="right" class="text-right" style="border:1px solid black;padding:5px;"><%=overallfinance.get(z).get(0)[21].toString()%></td>
												<td align="right" class="text-right" style="border:1px solid black;padding:5px;"><%=overallfinance.get(z).get(0)[22].toString()%></td>
												<td align="right" class="text-right" style="border:1px solid black;padding:5px;"><%=overallfinance.get(z).get(0)[23].toString()%></td>
												<td align="right" class="text-right" style="border:1px solid black;padding:5px;"><%=overallfinance.get(z).get(0)[24].toString()%></td>
												<td align="right" class="text-right" style="border:1px solid black;padding:5px;"><%=overallfinance.get(z).get(0)[25].toString()%></td>
												<td align="right" class="text-right" style="border:1px solid black;padding:5px;"><%=overallfinance.get(z).get(0)[26].toString()%></td>
												<td align="right" class="text-right" style="border:1px solid black;padding:5px;"><%=overallfinance.get(z).get(0)[27].toString()%></td>
												<td align="right" class="text-right" style="border:1px solid black;padding:5px;"><%=overallfinance.get(z).get(0)[28].toString()%></td>
											</tr>
									     	<tr>
												<td colspan="2" style="border:1px solid black;padding:5px;"><b>GrandTotal</b></td>
												<td colspan="2" align="right" class="text-right" style="border:1px solid black;padding:5px;"><b><%=Double.parseDouble(overallfinance.get(z).get(0)[17].toString())  +Double.parseDouble(overallfinance.get(z).get(0)[18].toString())%></b></td>
												<td colspan="2" align="right" class="text-right" style="border:1px solid black;padding:5px;"><b><%=Double.parseDouble(overallfinance.get(z).get(0)[19].toString())  +Double.parseDouble(overallfinance.get(z).get(0)[20].toString())%></b></td>
												<td colspan="2" align="right" class="text-right" style="border:1px solid black;padding:5px;"><b><%=Double.parseDouble(overallfinance.get(z).get(0)[21].toString())  +Double.parseDouble(overallfinance.get(z).get(0)[22].toString())%></b></td>
												<td colspan="2" align="right" class="text-right" style="border:1px solid black;padding:5px;"><b><%=Double.parseDouble(overallfinance.get(z).get(0)[23].toString())  +Double.parseDouble(overallfinance.get(z).get(0)[24].toString())%></b></td>
												<td colspan="2" align="right" class="text-right" style="border:1px solid black;padding:5px;"><b><%=Double.parseDouble(overallfinance.get(z).get(0)[25].toString())  +Double.parseDouble(overallfinance.get(z).get(0)[26].toString())%></b></td>
												<td colspan="2" align="right" class="text-right" style="border:1px solid black;padding:5px;"><b><%=Double.parseDouble(overallfinance.get(z).get(0)[27].toString())  +Double.parseDouble(overallfinance.get(z).get(0)[28].toString())%></b></td>				     
									     	</tr>
									     <%}else{%> 
									     	<tr>
												<td colspan="2" style="border:1px solid black;padding:5px;"><b>Total</b></td>
												<td align="right" class="text-right" style="border:1px solid black;padding:5px;"><%=df.format(totReSanctionCost)%></td>
												<td align="right" class="text-right" style="border:1px solid black;padding:5px;"><%=df.format(totFESanctionCost)%></td>
												<td align="right" class="text-right" style="border:1px solid black;padding:5px;"><%=df.format(totREExpenditure)%></td>
												<td align="right" class="text-right" style="border:1px solid black;padding:5px;"><%=df.format(totFEExpenditure)%></td>
												<td align="right" class="text-right" style="border:1px solid black;padding:5px;"><%=df.format(totRECommitment)%></td>
												<td align="right" class="text-right" style="border:1px solid black;padding:5px;"><%=df.format(totFECommitment)%></td>
												<td align="right" class="text-right" style="border:1px solid black;padding:5px;"><%=df.format(btotalRe)%></td>
												<td align="right" class="text-right" style="border:1px solid black;padding:5px;"><%=df.format(btotalFe)%></td>
												<td align="right" class="text-right" style="border:1px solid black;padding:5px;"><%=df.format(totalREDIPL)%></td>
												<td align="right" class="text-right" style="border:1px solid black;padding:5px;"><%=df.format(totalFEDIPL)%></td>
												<td align="right" class="text-right" style="border:1px solid black;padding:5px;"><%=df.format(totReBalance)%></td>
												<td align="right" class="text-right" style="border:1px solid black;padding:5px;"><%=df.format(totFeBalance)%></td>
											</tr>
											<tr>
												<td colspan="2" style="border:1px solid black;padding:5px;"><b>GrandTotal</b></td>
												<td colspan="2" align="right" class="text-right" style="border:1px solid black;padding:5px;"><b><%=df.format(totReSanctionCost+totFESanctionCost)%></b></td>
												<td colspan="2" align="right" class="text-right" style="border:1px solid black;padding:5px;"><b><%=df.format(totREExpenditure+totFEExpenditure)%></b></td>
												<td colspan="2" align="right" class="text-right" style="border:1px solid black;padding:5px;"><b><%=df.format(totRECommitment+totFECommitment)%></b></td>
												<td colspan="2" align="right" class="text-right" style="border:1px solid black;padding:5px;"><b><%=df.format(btotalRe+btotalFe)%></b></td>
												<td colspan="2" align="right" class="text-right" style="border:1px solid black;padding:5px;"><b><%=df.format(totalREDIPL+totalFEDIPL)%></b></td>
												<td colspan="2" align="right" class="text-right" style="border:1px solid black;padding:5px;"><b><%=df.format(totReBalance+totFeBalance)%></b></td>
											</tr>
									     <%-- <% }%> --%>
									     </tbody>
									     <% }} %>
									</table>
														
							
					<% }else {  %>
						
						<table style="margin:auto; width:680px;font-size: 16px; border-collapse: collapse;" >
						<tr >
							<td colspan="8" style="border: 1px solid black;font-weight: bold"  align="center">No Data Available</td>
						</tr>
					</table>	
							  
					<% }} %>
		<%}else if (committeemin[0].toString().equals("7") ){
		
			if(procurementOnDemand!=null &&  procurementOnDemand.size()>0){
			%>
	
				<table style="margin-top: 0px; margin-left: 10px; width: 650px; font-size: 16px; border-collapse: collapse;">
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=++index %>.&nbsp;&nbsp;&nbsp;Details of Procurement. </th>
						</tr>
					</table>
				 <table style=" margin:auto; width:680px; margin-top:5px;font-size: 16px; border-collapse: collapse;border: 1px solid black" >
						<thead>
							<tr>
								<th colspan="11" style="text-align: right;"> <span class="currency" >(In &#8377; Lakhs)</span></th>
							</tr>
							 <tr>
							 	<th colspan="11" class="std">Demand Details ( > &#8377; <% if (projectdatadetails != null && projectdatadetails[13] != null) { %>
										<%=projectdatadetails[13].toString().replaceAll("\\.\\d+$", "")%> ) <% } else { %> - )<% } %>
									
								</th>
							</tr>
							</thead>
							
							<tr>
								<th class="std" style="border: 1px solid black;width: 30px !important;">SN</th>
								<th class="std" style="border: 1px solid black;max-width:90px;">Demand No <br> Demand Date</th>
<!-- 							<th class="std" style="border: 1px solid black;max-width:90px; ">Demand Date</th> -->
								<th class="std" colspan="4" style="border: 1px solid black;max-width: 150px;"> Nomenclature</th>
								<th class="std" style="border: 1px solid black;max-width:90px;"> Est. Cost</th>
								<th class="std" style="border: 1px solid black;max-width:80px; "> Status</th>
								<th class="std" colspan="3" style="border: 1px solid black;max-width:200px;">Remarks</th>
							</tr>
							    <% int k=0;
							    if(procurementOnDemand!=null &&  procurementOnDemand.size()>0){
							    Double estcost=0.0;
							    Double socost=0.0;
							    for(Object[] obj : procurementOnDemand){ 
							    	k++; %>
								<tr>
									<td class="std"  style=" border: 1px solid black;"><%=k%></td>
									<td class="std"  style=" border: 1px solid black;"><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "%><br><%=obj[3]!=null?sdf.format(sdf1.parse(StringEscapeUtils.escapeHtml4(obj[3].toString()))):" - "%></td>
<%-- 									<td class="std"  style=" border: 1px solid black;"><%=sdf.format(sdf1.parse(obj[3].toString()))%></td> --%>
 									<td class="std" colspan="4" ><%=obj[8]!=null?StringEscapeUtils.escapeHtml4(obj[8].toString()): " - "%></td> 
									<td class="std" style=" text-align:right;"> <%=obj[5]!=null?format.format(new BigDecimal(StringEscapeUtils.escapeHtml4(obj[5].toString()))).substring(1):" - "%></td>
									<td class="std"  style=" border: 1px solid black;"> <%=obj[10]!=null?StringEscapeUtils.escapeHtml4(obj[10].toString()): " - "%> </td>
									<td class="std" colspan="3" style=" border: 1px solid black;"><%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - "%> </td>		
								</tr>		
								<%
								estcost += Double.parseDouble(obj[5].toString());
							    }%>
							    
							    <tr>
							    	<td class="std" colspan="6" style="text-align: right;"><b>Total : </b></td>
							    	<td class="std" style="text-align: right;"><b><%=estcost!=null?df.format(estcost):" - "%></b></td>
							    	<td class="std" colspan="4" style="text-align: center;"></td>
							    	

							    </tr>
							    
							    
							    <% }else{%>											
									<tr><td colspan="11" style="border: 1px solid black;text-align: center;" class="std" >Nil </td></tr>
								<%} %>
								<!-- ********************************Future Demand Start *********************************** -->
								<tr>
								<%} %>
								<%-- 
								<th class="std" colspan="11" style="border: 1px solid black"><span class="mainsubtitle">Future Demand</span></th>
								</tr>
								<tr>
									 <th class="std" style="border: 1px solid black;width: 15px !important;text-align: center;">SN</th>
										 <th class="std"  colspan="4" style="border: 1px solid black;;width: 295px;"> Nomenclature</th>
										 <th class="std" style="border: 1px solid black;width: 80px;"> Est. Cost-Lakh &#8377;</th>
										 <th class="std" style="border: 1px solid black;max-width:50px; "> Status</th>
										 <th class="std" colspan="4" style="border: 1px solid black;max-width: 310px;">Remarks</th>
								</tr>
							
							    			    <% int a=0;
							    if(envisagedDemandlist!=null &&  envisagedDemandlist.size()>0){
							    Double estcost=0.0;
							    Double socost=0.0;
							    for(Object[] obj : envisagedDemandlist){ 
							    	a++; %>
								<tr>
									<td class="std"  style=" border: 1px solid black;"><%=a%></td>
									<td class="std" colspan="4" style="border: 1px solid black;" ><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - "%></td>
									<td class="std" style="border: 1px solid black; text-align:right;"> <%=obj[2]!=null?format.format(new BigDecimal(StringEscapeUtils.escapeHtml4(obj[2].toString()))).substring(1):" - " %></td>
									<td class="std"  style=" border: 1px solid black;"> <%=obj[6]!=null?StringEscapeUtils.escapeHtml4(obj[6].toString()): " - "%> </td>
									<td class="std" colspan="4" style="border: 1px solid black;"><%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - "%> </td>		
								</tr>		
								<%
									estcost += Double.parseDouble(obj[2].toString());
							    }%>
							    
							    <tr>
							    	<td  class="std"colspan="7" style="border: 1px solid black;text-align: right;"><b>Total</b></td>
							    	<td class="std" style="border: 1px solid black;text-align: right;" colspan="4"><b><%=estcost!=null?df.format(estcost):" - "%></b></td>
							    </tr>
							    
							    
							    <% }else{%>											
									<tr><td colspan="11" style="border: 1px solid black;text-align: center;" class="std" >Nil </td></tr>
								<%} %>
							 --%>	
						<!-- ********************************Future Demand End *********************************** -->
								<%if(procurementOnSanction!=null && procurementOnSanction.size()>0){  %>
								 <tr >
								 
									<th  class="std"  colspan="8">Orders Placed ( > &#8377; <% if (projectdatadetails != null && projectdatadetails[13] != null) { %>
										<%=projectdatadetails[13].toString().replaceAll("\\.\\d+$", "")%> ) <% } else { %> - )<% } %>
									</th>
								 </tr>
							
							  	 <tr>	
							  	 	 <th class="std" rowspan="1" style="border: 1px solid black;width: 30px !important;">SN</th>
							  	 	 <th class="std" style="border: 1px solid black;width:150px;">Demand No <br>Demand  Date</th>
							  	 	<!--  <th class="std" style="border: 1px solid black;" >Demand  Date</th> -->
									 <th class="std" colspan="2" style="border: 1px solid black;"> Nomenclature</th>
									  	<th class="std"  style=" border: 1px solid black;width: 150px;">Supply Order No <br> Order Date</th>
									  <th class="std"  colspan="1" style="border: 1px solid black;width:100px">SO Cost-Lakh &#8377;</th>
									<!--  <th class="std" style="border: 1px solid black;max-width:90px;	">DP Date</th> -->
									  <th class="std" style="border: 1px solid black;width:100px;">DP Date  <br>Rev DP</th>
									 <th class="std" colspan="2" style="border: 1px solid black;width: 200px;">Vendor Name</th>
									  <th class="std" style="border: 1px solid black;max-width:80px; "> Status</th>											 
									 <th class="std"  colspan="1" style="border: 1px solid black;width:100px">Remarks &#8377;</th>
									</tr>
								
								
								<%if(procurementOnSanction!=null && procurementOnSanction.size()>0){ 
									  int rowk=0;
							    	  Double estcost=0.0;
									  Double socost=0.0;
									  String demand="";
									  List<Object[]> list = new ArrayList<>();
									  for(Object[] obj:procurementOnSanction){ 
										if(obj[2]!=null){
											if(!obj[1].toString().equalsIgnoreCase(demand)){
												rowk++;
								  	 		 	 list = procurementOnSanction.stream().filter(e-> e[0].toString().equalsIgnoreCase(obj[0].toString())).collect(Collectors.toList());
											}
										}
										  
								%>
					<tr>
					<td <%if(!obj[1].toString().equalsIgnoreCase(demand)){ %> style="border: 1px solid black;border-bottom:none;"<%} else{ %> style="border: 1px solid black;border-bottom:none;border-top:none;"<%} %>>
					<%if(!obj[1].toString().equalsIgnoreCase(demand)){ %>
					<%=rowk %>
					<%} %>
					</td>
					<td <%if(!obj[1].toString().equalsIgnoreCase(demand)){ %> style="border: 1px solid black;border-bottom:none;"<%} else{ %> style="border: 1px solid black;border-bottom:none;border-top:none;"<%} %>>
					<%if(!obj[1].toString().equalsIgnoreCase(demand)){ %><%if(obj[1]!=null) {%> <%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "%><% }else{ %>-<%} %><br>
					<%=obj[3]!=null?sdf.format(sdf1.parse(StringEscapeUtils.escapeHtml4(obj[3].toString()))):" - "%>
					<%} %>
					</td>
					<td colspan="2" <%if(!obj[1].toString().equalsIgnoreCase(demand)){ %> style="border: 1px solid black;border-bottom:none;"<%} else{ %> style="border: 1px solid black;border-bottom:none;border-top:none;"<%} %>>
					<%if(!obj[1].toString().equalsIgnoreCase(demand)){ %>
					<%=obj[8]!=null?StringEscapeUtils.escapeHtml4(obj[8].toString()): " - "%>
					<%} %>
					</td>
						<td style="border: 1px solid black;text-align: center;">
						<% if(obj[2]!=null){%> <%=StringEscapeUtils.escapeHtml4(obj[2].toString())%> <%}else{ %>-<%} %> <br>
						<%if(obj[14]!=null){%> <%=sdf.format(sdf1.parse(StringEscapeUtils.escapeHtml4(obj[14].toString())))%> <%}else{ %> - <%} %>
					</td>
						<td style="border: 1px solid black;text-align: right"><%if(obj[6]!=null){%> <%=format.format(new BigDecimal(StringEscapeUtils.escapeHtml4(obj[6].toString()))).substring(1)%> <%} else{ %> - <%} %></td>
					<td style="border: 1px solid black;">
					<%if(obj[4]!=null){%> <%=sdf.format(sdf1.parse(StringEscapeUtils.escapeHtml4(obj[4].toString())))%> <%}else{ %> - <%} %>
					<br>
					<%if(obj[7]!=null){if(!obj[7].toString().equals("null")){%> <%=sdf.format(sdf1.parse(StringEscapeUtils.escapeHtml4(obj[7].toString())))%><%}}else{ %>-<%} %></td>
						
						<td colspan="2" style="border: 1px solid black;"><%=obj[12]!=null?StringEscapeUtils.escapeHtml4(obj[12].toString()): " - " %> </td>
						<td <%if(!obj[1].toString().equalsIgnoreCase(demand)){ %> style="border: 1px solid black;border-bottom:none;"<%} else{ %> style="border: 1px solid black;border-bottom:none;border-top:none;"<%} %>>
						<%if(!obj[1].toString().equalsIgnoreCase(demand)){ %>
					<%=obj[10]!=null?StringEscapeUtils.escapeHtml4(obj[10].toString()): " - "%>
					<%} %>
					
					</td>
					
					
						<td <%if(!obj[1].toString().equalsIgnoreCase(demand)){ %> style="border: 1px solid black;border-bottom:none;"<%} else{ %> style="border: 1px solid black;border-bottom:none;border-top:none;"<%} %>>
						<%if(!obj[1].toString().equalsIgnoreCase(demand)){ %>
					<%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - "%>
					<%} %>
					
					</td>
					</tr>
											<%
											demand = obj[1].toString();
											Double value = 0.00;
								  	 		if(obj[6]!=null){
								  	 			value=Double.parseDouble(obj[6].toString());
								  	 		}
								  	 		
								  	 		estcost += Double.parseDouble(obj[5].toString());
								  	 		socost +=  value;
											}
											%>
											
												<tr>
										    	<td colspan="5" class="std" style="text-align: right;border: 1px solid black;"><b>Total</b></td>
										    	<td colspan="1" class="std" style="text-align: right;border: 1px solid black;"><b><%=socost!=null?df.format(socost):" - "%></b></td>
										    	<td colspan="5" class="std" style="text-align: right;border: 1px solid black;"><b></b></td>
										   		 </tr>	
										 <% }else{%>
											
												<tr><td colspan="8" style="border: 1px solid black;" class="std"  style="text-align: center;">Nil </td></tr>
											<%} } %>
							</table>
							<%if(totalprocurementdetails!=null && totalprocurementdetails.size()>0){ %>
							<table style=" margin:auto; width:680px; margin-top:5px;font-size: 16px; border-collapse: collapse;border: 1px solid black" >
										<thead>
											 <tr >
												 <th class="std" colspan="8" ><span class="mainsubtitle">Total Summary of Procurement</span></th>
											 </tr>
										 </thead>
									       <tr>
												<th class="std" style="max-width: 150px;border: 1px solid black;">No. of Demand</th>
												<th class="std" style="max-width: 150px;border: 1px solid black;">Est. Cost</th>
												<th class="std" style="max-width: 150px;border: 1px solid black;">No. of Orders</th>
												<th class="std" style="max-width: 150px;border: 1px solid black;">SO Cost</th>
												<th class="std" style="max-width: 150px;border: 1px solid black;">Expenditure</th>
											</tr>
									<%if(totalprocurementdetails!=null && totalprocurementdetails.size()>0){ 
										 for(TotalDemand obj:totalprocurementdetails){
											 if(obj.getProjectId().equalsIgnoreCase(projectid)){
										 %>
										   <tr>
										      <td class="std" style="text-align: center;border: 1px solid black;"><%=obj.getDemandCount()!=null?StringEscapeUtils.escapeHtml4(obj.getDemandCount()): " - " %></td>
										      <td class="std" style="text-align: center;border: 1px solid black;"><%=obj.getEstimatedCost()!=null?StringEscapeUtils.escapeHtml4(obj.getEstimatedCost()): " - " %></td>
										      <td class="std" style="text-align: center;border: 1px solid black;"><%=obj.getSupplyOrderCount()!=null?StringEscapeUtils.escapeHtml4(obj.getSupplyOrderCount()): " - "%></td>
										      <td class="std" style="text-align: center;border: 1px solid black;"><%=obj.getTotalOrderCost()!=null?StringEscapeUtils.escapeHtml4(obj.getTotalOrderCost()): " - " %></td>
										      <td class="std" style="text-align: center;border: 1px solid black;"><%=obj.getTotalExpenditure()!=null?StringEscapeUtils.escapeHtml4(obj.getTotalExpenditure()): " - "%></td>
										   </tr>
										   <%}}}else{%>
										   <tr>
										      <td class="std" colspan="5" style="text-align: center;border: 1px solid black;">IBAS Server Could Not Be Connected</td>
										   </tr>
										   <%} %>
									</table> 
									<%} %>
					
			
			<%	List<Object[]> list = milestoneBriefingMap!=null ? milestoneBriefingMap.get("9") : new ArrayList<>();
			
			if(list!=null && !list.isEmpty()){%>
			
					<table style="margin-top: 0px; margin-left: 10px; width: 650px; font-size: 16px; border-collapse: collapse;">
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700; text-align: justify;padding-left: 15px;" ><br><%=++index %>.&nbsp;&nbsp;&nbsp;<%=committeemin[1]!=null?committeemin[1].toString(): " - "%> </th>
						</tr>
					</table>	
				
			<%-- 		<table style=" margin:auto; width:680px; margin-top:5px;font-size: 16px; border-collapse: collapse;border: 1px solid black" >
						<thead>
							<tr>
								<th class="std" style="width:5%!important;">SN</th>
								<th class="std" style="width:95%!important;" >Details</th>
							</tr>
						</thead>
						<tbody>
							<%
							if(list!=null && !list.isEmpty()){
							int num = 1;
							for(Object[] obj: list){ %>
								<tr>
									<td style="text-align: center;border: 1px solid black;padding:5px;"  class="text-center width-5 std"><%= num++ %></td>
									<td style="border: 1px solid black;padding:5px;text-align: left;"  class="std">
										
										<%
								String editorContent = obj[2]!=null?obj[2].toString(): " - ";
								//System.out.println(editorContent);

								// First convert ordered list to numbered format
								Pattern pattern = Pattern.compile("(?i)<ol[^>]*>(.*?)</ol>", Pattern.DOTALL);
								Matcher matcher = pattern.matcher(editorContent);
								
								while (matcher.find()) {
								    String olContent = matcher.group(1);
								
								    Pattern liPattern = Pattern.compile("(?i)<li[^>]*>(.*?)</li>");
								    Matcher liMatcher = liPattern.matcher(olContent);
								
								    StringBuilder numberedList = new StringBuilder();
									int znum = 0;
									
								    while (liMatcher.find()) {
								        numberedList.append(znum++)
								                    .append(". ")
								                    .append(liMatcher.group(1).trim())
								                    .append("<br/>");
								    }
								
								    editorContent = editorContent.replace(matcher.group(0), numberedList.toString());
								}
								
								// Then remove other unwanted tags
								editorContent = editorContent.replaceAll("(?i)</?(p|div|)[^>]*>", "");
								
								%>
								<%= editorContent %>
									</td>
								</tr>
							<%}}else{ %>
								<tr>
									<td style="text-align: center;border: 1px solid black;padding:5px;" colspan="2">No Data Available</td>
								</tr>
							<%} %>
						</tbody>
					</table> --%>
					<ul style="list-style-type:none; width:680px; margin-top:5px; font-size:16px; padding-left:0;">
						<%
						if(list != null && !list.isEmpty()){
						    for(Object[] obj : list){
						%>
						    <li style="margin-bottom:10px;text-align:left;">
						        <%
						        String editorContent = obj[2] != null ? obj[2].toString() : " - ";
						
						        // Convert inner <ol> to numbered format
						        Pattern pattern = Pattern.compile("(?i)<ol[^>]*>(.*?)</ol>", Pattern.DOTALL);
						        Matcher matcher = pattern.matcher(editorContent);
						
						        while (matcher.find()) {
						            String olContent = matcher.group(1);
						
						            Pattern liPattern = Pattern.compile("(?i)<li[^>]*>(.*?)</li>");
						            Matcher liMatcher = liPattern.matcher(olContent);
						
						            StringBuilder numberedList = new StringBuilder();
						            int znum = 1;
						
						            while (liMatcher.find()) {
						                numberedList.append(znum++)
						                              .append(". ")
						                              .append(liMatcher.group(1).trim())
						                              .append("<br/>");
						            }
						
						            editorContent = editorContent.replace(matcher.group(0), numberedList.toString());
						        }
						
						        // Remove unwanted tags
						        editorContent = editorContent.replaceAll("(?i)</?(p|div|)[^>]*>", "");
						        %>
						
						        <%= editorContent %>
						    </li>
						<%
						    }
						} else {
						%>
						    <li>No Data Available</li>
						<%
						}
						%>
						</ul>
					<%} %>
					
		<%-- <table style="margin:auto;margin-top: 5px; margin-bottom: 0px;  width:680px; font-size: 16px; border-collapse: collapse;border: 1px solid black;" >
							 <thead>
									
								<tr style="font-size:14px; ">
									<th class="std"  style=" border: 1px solid black;width:20px !important;">SN</th>
									<th class="std"  style=" border: 1px solid black;width:20px; ">MS</th>
									<th class="std"  style=" border: 1px solid black;width:20px; ">L</th>
									<th class="std"  style=" border: 1px solid black;width:400px;">Action Plan </th>	
									<th class="std"  style=" border: 1px solid black;width:140px;">Responsibility </th>
									<th class="std"  style=" border: 1px solid black;width:70px;">PDC</th>	
									<th class="std"  style=" border: 1px solid black;width:70px;">Progress </th>
					                 <th class="std"  style=" border: 1px solid black;width:180px;">Remarks</th>
								</tr>
							</thead>
							<tbody style="font-size: 14px;">
								<%if(ActionPlanSixMonths!=null && ActionPlanSixMonths.size()>0){ 
									long milecount=1;
									int countA=1;
									int countB=1;
									int countC=1;
									int countD=1;
									int countE=1;
									String mainMileStone=null;
									String mile=null;
									String mileA=null;
									String mileBid=null;
									if(!ActionPlanSixMonths.isEmpty()){
										mainMileStone=ActionPlanSixMonths.get(0)[0].toString();
										mile=ActionPlanSixMonths.get(0)[2].toString();
										mileA=ActionPlanSixMonths.get(0)[3].toString();
										mileBid=ActionPlanSixMonths.get(0)[1].toString();
									}
									%>
									<%for(Object[] obj:ActionPlanSixMonths){
										
										if(Integer.parseInt(obj[26].toString())<= Integer.parseInt(levelid) ){
										%>
										<tr>
											<td class="std"  style=" border: 1px solid black;text-align: center"><%=milecount %></td>
											<td class="std"  style="border: 1px solid black; border:1px solid black; text-align: center;<%if(obj[26].toString().equalsIgnoreCase("0")){%> <%}%> ">M<%=obj[22] !=null?StringEscapeUtils.escapeHtml4(obj[22].toString()): " - "%></td>
											
											<td class="std"  style=" border: 1px solid black;text-align: center;border:1px solid black;">
												<%
												if(obj[26].toString().equals("0")) {%>
												<%countA=1;
													countB=1;
													countC=1;
													countD=1;
													countE=1;
												}else if(obj[26].toString().equals("1")) {    
												for (Map.Entry<Integer,String> entry : treeMapLevOne.entrySet()) {
												if(entry.getKey().toString().equalsIgnoreCase(obj[2].toString())){%>
													<%=entry.getValue()!=null?StringEscapeUtils.escapeHtml4(entry.getValue()): " - " %>
												<%}
												}
												    countB=1;
												    countC=1;
													countD=1;
													countE=1;
												}else if(obj[26].toString().equals("2")) { 
													
													for(Map.Entry<Integer, String>entry:treeMapLevTwo.entrySet()){
													if(entry.getKey().toString().equalsIgnoreCase(obj[3].toString())){%>
													<%=entry.getValue()!=null?StringEscapeUtils.escapeHtml4(entry.getValue()): " - " %>
													<%	}
													}
												%>
												<%countC=1;
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
											<td class="std" style="<%if(obj[26].toString().equals("0")) {%>font-weight:bold;<%}%> text-align:left;border:1px solid black;" >
												<%if(obj[26].toString().equals("0")) {%>
												<p style="text-align: justify"><%=obj[9]!=null?StringEscapeUtils.escapeHtml4(obj[9].toString()): " - " %></p>
												<%}else if(obj[26].toString().equals("1")) { %>
												<p style="text-align: justify"><%=obj[10]!=null?StringEscapeUtils.escapeHtml4(obj[10].toString()): " - "%></p>
												<%}else if(obj[26].toString().equals("2")) { %>
												<p style="text-align: justify"><%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - "%></p>
												<%}else if(obj[26].toString().equals("3")) { %>
												<p style="text-align: justify"><%=obj[12]!=null?StringEscapeUtils.escapeHtml4(obj[12].toString()): " - "%></p>
												<%}else if(obj[26].toString().equals("4")) { %>
												<p style="text-align: justify"><%=obj[13]!=null?StringEscapeUtils.escapeHtml4(obj[13].toString()): " - "%></p>
												<%}else if(obj[26].toString().equals("5")) { %>
												<p style="text-align: justify"><%=obj[14]!=null?StringEscapeUtils.escapeHtml4(obj[14].toString()): " - "%></p>
												<%}%>
											</td>
											<td class="std"  style=" border: 1px solid black;"><%=obj[24]!=null?StringEscapeUtils.escapeHtml4(obj[24].toString()): " - " %>(<%=obj[25]!=null?StringEscapeUtils.escapeHtml4(obj[25].toString()): " - " %>)</td>
											<td class="std" style="border: 1px solid black; font-size: 12px;font-weight:bold;" >
											<%=obj[8]!=null?sdf.format(sdf1.parse(StringEscapeUtils.escapeHtml4(obj[8].toString()))):" - " %>
											<%if(!LocalDate.parse(obj[8].toString()).equals(LocalDate.parse(obj[29].toString()))){ %>
											<br><%=obj[29]!=null?sdf.format(sdf1.parse(StringEscapeUtils.escapeHtml4(obj[29].toString()))) :" - "%>
											<%} %>
											</td>
											<td class="std"  style=" border: 1px solid black;text-align: center"><%=obj[16]!=null?StringEscapeUtils.escapeHtml4(obj[16].toString()): " - " %>%</td>											
								
											<td  class="std"  style="max-width: 80px;border: 1px solid black;">
												<%if(obj[28]!=null){ %> <%=StringEscapeUtils.escapeHtml4(obj[28].toString()) %> <%} %>
											</td>
										</tr>
									<%milecount++;mile=obj[2].toString();mileA=obj[3].toString();mainMileStone=obj[0].toString();mileBid=obj[1].toString();}} %>
								<%} else{ %>
								<tr><td class="std"  colspan="9" style="text-align:center; "> Nil</td></tr>
								<%} %>
						</tbody>				
					</table>
		 --%>			  
					<%-- <%if(actionlist.size()>=0){ %>
					<div align="center">
				 	<div style="text-align: center ; padding-right: 15px; " ><h3 style="text-decoration: underline;">Annexure - AI</h3></div> 
						<div style="text-align: center;  " class="lastpage" id="lastpage"><h2>ACTION ITEM DETAILS</h2></div>
					
						<table style="width:680px; margin:auto; font-size: 16px; border-collapse: collapse ;border: 1px solid black ;margin-right: 10px;margin-top:10px;">
						<tbody>
							<tr>
								<th  class="sth" style=" max-width: 40px"> SN </th>
								<th  class="sth" style=" max-width: 210px"> Action Id</th>	
								<th  class="sth" style=" max-width: 600px"> Item</th>				
								<th  class="sth" style=" max-width: 200px"> Responsibility </th>					
								<th  class="sth" style=" width: 100px"> PDC</th>
							</tr>
							
							<% 	
							
							int count =1;
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
													<%if(obj[3]!=null){ %> <%= StringEscapeUtils.escapeHtml4(obj[3].toString())%><%}else{ %> - <%} %>
												<%}else if(count1==values.size() ){ %>
													<%if(obj[3]!=null){ %> <br> - <br> <%= StringEscapeUtils.escapeHtml4(obj[3].toString())%> <%}else{ %> - <%} %>
												<%} %>
										<%} %>
									</td>
									
									<td  class="std" style="padding-left: 5px;padding-right: 5px;text-align: justify;"><%= values.get(0)[1]  %></td>
									<td  class="std" >
									<%	int count2=0;
										for(Object obj[]:values){ %>
										<%if(obj[13]!=null){ %> <%= StringEscapeUtils.escapeHtml4(obj[13].toString())%>,&nbsp;<%=obj[14]!=null?StringEscapeUtils.escapeHtml4(obj[14].toString()): " - " %>
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
					<%}%> --%>
				
		<%} else if (committeemin[0].toString().equals("8") || committeemin[0].toString().equals("9") || committeemin[0].toString().equals("10")){
		
		
				List<Object[]> filteredList = speclists.stream()
					    .filter(spec ->{ 
					    	return spec[3] != null && 
					    			(
					    					(committeemin[0].toString().equals("8") && spec[3].toString().equals("4")) || 
					    					(committeemin[0].toString().equals("9") && spec[3].toString().equals("5") && spec[7].toString().equalsIgnoreCase("R")) ||
					    					(committeemin[0].toString().equals("10") && spec[3].toString().equals("6"))
					    					
									);
					    })
					    .collect(Collectors.toList());

				if(filteredList!=null && !filteredList.isEmpty()){
				
				%>
			
			
				<table style="margin-top:0px; margin-left: 10px; width: 650px; font-size: 16px; border-collapse: collapse;">
					<tbody>
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;"><br>&nbsp;&nbsp;&nbsp;<%=++index %>.&nbsp;&nbsp;&nbsp;<%=committeemin[1]!=null?committeemin[1].toString(): " - "%></th>
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
										<td style="text-align: justify;padding-left: 30px;">
											<%=speclist[1]!=null?speclist[1].toString(): " - "%> 
										</td>
									</tr>
									
									<%} %>
								<%}else if(speclist[3].toString().equals("6") && committeemin[0].toString().equals("10")) 
								{
									count++;%>
									<tr>
										<td style="text-align: justify;padding-left: 30px;">
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
		<%}}
	}%>
	
		<div style="width: 650px;margin:auto;margin-top:30px; ">
			<div align="center" style="padding-left: 2.5rem;">
				<p>These Minutes are issued with the approval of the Chairperson. </p>
			</div>
			<div align="left" style="padding-right: 0rem;padding-bottom: 0rem; margin-right: 0px">
				<%if(membersec!=null){%>
				<div align="right" style="padding-right: 0rem;padding-bottom: 2rem;">
				<br><%if(membersec!=null){%><%= membersec[6]!=null?membersec[6].toString(): " - " %>,&nbsp;<%= membersec[7]!=null?membersec[7].toString(): " - " %><%} %>
				 <br>
				 (Project Director)
			</div>
			<%} %>
			</div>
		</div> 
	</div>
	</body>
</html>

