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
	<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" import="java.util.*"%>
<!DOCTYPE html>
<html>
<head>
<script src="./webjars/jquery/3.4.0/jquery.min.js"></script>
<spring:url value="/resources/js/FileSaver.min.js" var="FileSaver" />
<script src="${FileSaver}"></script>

<spring:url value="/resources/js/jquery.wordexport.js" var="wordexport" />
<script src="${wordexport}"></script>
	<!--BootStrap Bundle JS  -->
<script src="./webjars/bootstrap/4.0.0/js/bootstrap.bundle.min.js"></script>

<!--BootStrap JS  -->
<!-- <script src="./webjars/bootstrap/4.0.0/js/*.js"></script> -->

<!--BootStrap CSS  -->
<link rel="stylesheet" href="./webjars/bootstrap/4.0.0/css/bootstrap.min.css" />

<link rel="stylesheet" href="./webjars/font-awesome/4.7.0/css/font-awesome.min.css" />

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
	  SimpleDateFormat inputFormat = new SimpleDateFormat("ddMMMyyyy");
      SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
      String todayDate=outputFormat.format(new Date()).toString();
    /* ------- */
	String[] no=committeescheduleeditdata[11].toString().split("/");
	Object[] membersec=null; 
	Map<Integer,String> treeMapLevOne =(Map<Integer,String>)request.getAttribute("treeMapLevOne");
	Map<Integer,String> treeMapLevTwo =(Map<Integer,String>)request.getAttribute("treeMapLevTwo");
	//maps for pmrc and EB
	Map<Integer,String> mappmrc=(Map<Integer,String>)request.getAttribute("mappmrc");
	Map<Integer,String> mapEB=(Map<Integer,String>)request.getAttribute("mapEB");
	List<Object[]> envisagedDemandlist = (List<Object[]> )request.getAttribute("envisagedDemandlist");
	
	
	%>

<meta charset="ISO-8859-1">
<title><%=committeescheduleeditdata[8]!=null?committeescheduleeditdata[8].toString(): " - " %> Minutes View</title>

</head>
<body>
<div  align="center" ><button class="btn btn-lg bg-transparent" id="btn-export" onclick=exportHTML() ><i class="fa fa-lg fa-download" aria-hidden="true"style="color:green"></i></button></div>
<div id="source-html">

<style type="text/css">


    .normal {
        font-family:"Calibri",sans-serif; 
        line-height:107%;
        font-size:11.0pt;
        mso-ascii-font-family:Calibri;
        mso-ascii-theme-font:minor-latin;
    }


    @page portrait_A4_page  {
        size:595.3pt 841.9pt;
        margin:72.0pt 72.0pt 72.0pt 72.0pt;
        mso-header-margin:35.4pt;
        mso-footer-margin:35.4pt;
        mso-paper-source:0;
    }

    div.portrait_A4_page { page:portrait_A4_page; }

    @page landscape_A4_page {
        size:841.9pt 595.3pt;
        mso-page-orientation:landscape;
        margin:72.0pt 72.0pt 72.0pt 72.0pt;
        mso-header-margin:35.45pt;
        mso-footer-margin:35.45pt;
        mso-paper-source:0;
    }

    div.landscape_A4_page { page:landscape_A4_page; }

</style>
<div class=portrait_A4_page>
  			<div align="center" ><h5>Minutes of  <%=meetingcount %>th <%=committeescheduleeditdata[8]!=null?committeescheduleeditdata[8].toString().toUpperCase():" - " %> Meeting for Project
															<br>
				<%if(Integer.parseInt(projectid)>0){ %>					
				"<%=projectdetails[1]!=null?projectdetails[1].toString(): " - " %>  (<%=projectdetails[4]!=null?projectdetails[4].toString(): " - "%>)"
				<%}else if(Integer.parseInt(divisionid)>0){ %>					
			 	"<%=divisiondetails[2]!=null?divisiondetails[2].toString(): " - " %>"
				<%}else if(Integer.parseInt(initiationid)>0){ %>					
				"<%=initiationdetails[2]!=null?initiationdetails[2].toString(): " - " %>(<%=initiationdetails[1]!=null?initiationdetails[1].toString(): " - "%>)"
				<%}else{%>
				<%} %>
				<br>
				<%if(Integer.parseInt(projectid)>0){ %>					
				Project No:&nbsp;<%=projectdetails[2]!=null?projectdetails[2].toString(): " - " %>
				<%}%>
				</h5></div>
			<div style="/* display: flex;justify-content: space-around;align-items: center; */">
			<h5 style="font-weight: 600; text-align: center">Venue: <% if(committeescheduleeditdata[12]!=null){ %><%=committeescheduleeditdata[12] %> <%}else{ %> - <%} %><br>  Date: <%=committeescheduleeditdata[2]!=null?sdf3.format(sdf1.parse(committeescheduleeditdata[2].toString())): " - " %></h5>
			</div>


<%if(invitedlist.size()>0){ %>
<% ArrayList<String> membertypes=new ArrayList<String>(Arrays.asList("CC","CS","PS","CI","CW","CO","CH"));

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


<div style="align : justify;">
<table style="width: 620px; margin-top:5px;font-size: 16px; border-collapse: collapse;">
	
	 <tr>
		 <th style="text-align: left ;padding: 5px;width: 10px;font-weight:600 ">1.Following Members were present during the meeting:</th>
		 </tr>
		 </table>


<table style=" width: 620px; margin-top:5px;font-size: 16px; border-collapse: collapse;">
	
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
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CW")){	 %>External&nbsp;(<%=invitedlist.get(i)[11] %>)<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CO")){	 %>External&nbsp;(<%=invitedlist.get(i)[11]%>)<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("P") ){	 %>Presenter <%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("I")){	 %>Internal<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("W") ){	 %>External&nbsp;(<%=invitedlist.get(i)[11] %>)<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("E") )    {%>External&nbsp;(<%=invitedlist.get(i)[11] %>)<%}
					else {%> REP_<%=invitedlist.get(i)[3]!=null?invitedlist.get(i)[3].toString(): " - "%>&nbsp; (<%=invitedlist.get(i)[11]!=null?invitedlist.get(i)[11].toString(): " - " %>)  <%}
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
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CW")){	 %>External&nbsp;(<%=invitedlist.get(i)[11] %>)<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CO")){	 %>External&nbsp;(<%=invitedlist.get(i)[11]%>)<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("P") ){	 %>Presenter <%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("I")){	 %>Internal<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("W") ){	 %>External&nbsp;(<%=invitedlist.get(i)[11] %>)<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("E") )    {%>External&nbsp;(<%=invitedlist.get(i)[11] %>)<%}
					else {%> REP_<%=invitedlist.get(i)[3]!=null?invitedlist.get(i)[3].toString(): " - "%>&nbsp; (<%=invitedlist.get(i)[11]!=null?invitedlist.get(i)[11].toString(): " - " %>)  <%}
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
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CW")){	 %>External&nbsp;(<%=invitedlist.get(i)[11] %>)<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CO")){	 %>External&nbsp;(<%=invitedlist.get(i)[11]%>)<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("P") ){	 %>Presenter <%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("I")){	 %>Internal<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("W") ){	 %>External&nbsp;(<%=invitedlist.get(i)[11] %>)<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("E") )    {%>External&nbsp;(<%=invitedlist.get(i)[11] %>)<%}
					else {%> REP_<%=invitedlist.get(i)[3]!=null?invitedlist.get(i)[3].toString(): " - "%>&nbsp; (<%=invitedlist.get(i)[11]!=null?invitedlist.get(i)[11].toString(): " - " %>)  <%}
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
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CW")){	 %>External&nbsp;(<%=invitedlist.get(i)[11] %>)<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CO")){	 %>External&nbsp;(<%=invitedlist.get(i)[11]%>)<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("P") ){	 %>Presenter <%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("I")){	 %>Internal<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("W") ){	 %>External&nbsp;(<%=invitedlist.get(i)[11] %>)<%}
					else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("E") )    {%>External&nbsp;(<%=invitedlist.get(i)[11] %>)<%}
					else {%> REP_<%=invitedlist.get(i)[3]!=null?invitedlist.get(i)[3].toString(): " - "%>&nbsp; (<%=invitedlist.get(i)[11]!=null?invitedlist.get(i)[11].toString(): " - " %>)  <%}
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
<br>
<table style="width: 620px; margin-top:5px;font-size: 16px; border-collapse: collapse;" >
			<tbody>
				<tr>
					<th colspan="8" style="text-align: left; font-weight: 600;"><br>2.Introduction</th>
				</tr>
				<tr>
						<%
							int count = 0;

							for (Object[] speclist : speclists)
							{
								if (speclist[3].toString().equals("1")) 
								{
									count++;
						%>
					
					<td style="text-align: left;">
					<div align="left" style="padding-left: 30px;text-align: justify"><%=speclist[1]!=null?speclist[1].toString(): " - "%></div>
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
<%} %>
<br>
					<table style="  margin-top: 0px; margin-left: 8px; width:620px; font-size: 16px; border-collapse: collapse;" >
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 600;">3. Record of Discussions and Action Points of Current Meeting.</th>
						</tr>
						<tr>
							<td colspan="8" style="border: 1px solid black;" style="text-align: center ;padding: 5px;">Item Codes/Type : A: Action, C: Comment, D: Decision, R: Recommendation</td>
						</tr>
					</table>	
					<table style=" margin-left: 8px; width: 620px; margin-top:5px;font-size: 16px; border-collapse: collapse;border: 1px solid black">
					<thead>
						<tr>
							<th  class="sth" style="border: 1px solid black;text-align :center !important;width: 80px"> SN</th>
							<th  class="sth" style="border: 1px solid black; width: 320px"> Item</th>				
							<th  class="sth" style="border: 1px solid black;text-align :center !important;width: 80px"> Type</th>
							<th  class="sth" style="border: 1px solid black;width: 120px"> Remarks</th> 
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
								<td style="text-align :center ;border:1px solid black;vertical-align: top;"  ><%=projectdetails[4]!=null?projectdetails[4].toString(): " - "%>/<br><%=committeescheduleeditdata[8]!=null?committeescheduleeditdata[8].toString().toUpperCase():" - "%><%="#"+meetingcount +"_"%>  <%=countcm%> </td>
								
								<td  class="std" style="border:1px solid black;padding:  5px 5px 5px 5px ;width: 600px;text-align: justify;"><%=speclists.get(i)[1]%></td>
								<td class="std" style="text-align :center !important;border:1px solid black; padding: 5px 5px 5px 5px ; vertical-align: top;" >							 
								<p  style="text-align :center !important; ">	<%=speclists.get(i)[7]!=null?speclists.get(i)[7].toString(): " - "%> 
								</p> 				
								</td>
								<td class="std" style="text-align :center;border:1px solid black;padding:  5px 5px 5px 5px;"  > <%if( speclists.get(i)[8]!=null && !speclists.get(i)[8].toString().equalsIgnoreCase("nil")){ %> <%= speclists.get(i)[8].toString()%> <%}else{ %> - <%} %></td>
		</tr>
				<%} 
				}%>
				<% if(countcm==0){%>
				<tr>
				<td class="std" style="text-align :center;border: 1px solid black;"  colspan="4">No Minutes details Added</td>
				</tr>
							<%} %>
				</table>	
<br>
				<div align="left" style="font-weight: 600;margin-top:10px;">4.Status of major sub system and sub projects  (Annexure - A)</div>
				<div align="left" style="font-weight: 600;margin-top:10px;">5.Financial Status presented during the review  (Annexure - B)</div>
				<div align="left" style="font-weight: 600;margin-top:10px;">6.Details of procurements items envisaged in the projects(Annexure - C)</div>
				<div align="left" style="font-weight: 600;margin-top:10px;">7.Major milestones proposed to be completed in next 06 months along with financial outlay (Annexure - D)</div>
				<div align="left" style="font-weight: 600">8.Other relevant Points:</div>


<br>

				<table style="width: 620px;font-size: 16px; border-collapse: collapse;">
					<tbody>
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;"><br></th>
						</tr>
						<%
						int count1 = 0;
						
						for (Object[] speclist : speclists)
						{ %>						
							 	
								<%if(speclist[3].toString().equals("4")  )
								{  
									count1++; %>	
									<tr>
										<td style="text-align: justify;padding-left: 30px"> 
											<%=speclist[1]!=null?speclist[1].toString(): " - "%> 
										</td>		
									</tr>			
								<%}}%>
								
							<%if(count1==0){ %>
							NIL
							<%} %>	
	</tbody>
	</table>
	
	<br>
	<div align="left" style="font-weight: 600;margin-top: 10px;">9.Recommendations</div>
					<table style=" margin-left: 8px; width: 620px;font-size: 16px; border-collapse: collapse;">
					<tbody>
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;"><br></th>
						</tr>
						<%
						int count2 = 0;
						
						for (Object[] speclist : speclists)
						{ %>						
							 	
								<%if(speclist[3].toString().equals("5")  )
								{ %> 
									<%if(speclist[7].toString().equalsIgnoreCase("R")){ count2++; %>	
									<tr>
										<td style="text-align: justify;padding-left: 30px;"> 
											<p style="font-weight: 600;">9.<%=count2+"."%><%=speclist[9]!=null?speclist[9].toString(): " - "%></p> 
										</td>		
									</tr>
									<tr>	
									<td style="text-align: justify;padding-left: 30px">
											<%=speclist[1]!=null?speclist[1].toString(): " - "%> 
										</td>
										</tr>		
								<%}}}%>
							<%if(count2==0){ %>
							NIL
							<%} %>
	</tbody>
	</table>	
	<br>
		<div align="left" style="font-weight: 600;margin-top: 10px;">10.Concluding Remarks</div>
	<br>
					<table style="width: 620px;font-size: 16px; border-collapse: collapse;">
					<tbody>
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;"><br></th>
						</tr>
						<%
						int count3 = 0;
						
						for (Object[] speclist : speclists)
						{ %>						
							 	
								<%if(speclist[3].toString().equals("6")  )
								{  
									count3++; %>	
									<tr>
										<td style="text-align: justify;padding-left: 30px"> 
											<%=speclist[1]!=null?speclist[1].toString(): " - "%> 
										</td>		
									</tr>			
								<%}}%>
								
							<%if(count3==0){ %>
							NIL
							<%} %>	
	</tbody>
	</table>
	
	<br>
			<div style="width: 620px;margin-left: 15px; ">
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
			</div>
			<%} %>
			</div>
		</div> 	
	
	</div>
<br clear=all style='page-break-before:always; mso-break-type:section-break'>
<div class=landscape_A4_page>
	<div align="center" style="text-decoration: underline">Annexure - A</div>	
	<table style="width: 950px; margin-top:5px;font-size: 16px; border-collapse: collapse;">
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;">Status of major sub system and sub projects</th>
						</tr>
					</table>	
					<br>
							<table style=" margin-left: 8px; width: 950px; margin-top:25px;font-size: 16px; border-collapse: collapse;border: 1px solid black" >
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
											<td class=""  style=" border: 1px solid black;text-align: center;">M<%=obj[0]!=null?obj[0].toString(): " - " %></td>
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
														<%=entry.getValue()!=null?entry.getValue(): " - " %>
												<%}}
												%>
												
												<% 
												}else if(obj[21].toString().equals("2")) { 
													for(Map.Entry<Integer,String>entry:treeMapLevTwo.entrySet()){
														if(entry.getKey().toString().equalsIgnoreCase(obj[3].toString())){%>
															<%=entry.getValue()!=null?entry.getValue(): " - " %>
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
											<td class=""  style=" border: 1px solid black;text-align: center;">
												<%=obj[9]!=null?fc.sdfTordf(obj[9].toString()): " - "  %>
												<%if(obj[8]!=null && obj[9]!=null && !LocalDate.parse(obj[8].toString()).isEqual(LocalDate.parse(obj[9].toString())) ) {%>
													<br><%=obj[8]!=null?fc.sdfTordf(obj[8].toString()): " - "  %>
												<%} %>
											</td>											<td class=""  style=" border: 1px solid black;text-align: center;"><%=obj[17]!=null?obj[17].toString(): " - " %>%</td>											
											<td class=""  style=" border: 1px solid black;text-align: left;"><%if(obj[23]!=null){%><%=obj[23].toString()%><%} %></td>
										</tr>
									<%milcount1++;}} %>
								<%} else{ %>
								<tr><td class="" colspan="8" style="border: 1px solid black; text-align: center;" > No SubSystems</td></tr>
								<%}}else{ %>
									<tr><td class="" colspan="8" style="border: 1px solid black; text-align: center;" > No SubSystems</td></tr>
								<%} %>

						</tbody>
					</table>	
</div>
<br clear=all style='page-break-before:always; mso-break-type:section-break'>
<div class=landscape_A4_page>
					<div align="center" style="text-decoration: underline">Annexure - B</div>		
					<table style="  width: 950px; margin-top:5px;font-size: 16px; border-collapse: collapse;" >
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;"><br>Financial Status presented during the review</th>
						</tr>
						<tr >
							<td colspan="8" style="border: 1px solid black;" align="right">(Amount in Crores)</td>
						</tr>
					</table>	
					<%if(Long.parseLong(projectid) >0 && projectFinancialDetails!=null) { %>
							
							<table style=" width: 950px; margin-top:5px;font-size: 16px; border-collapse: collapse;border: 1px solid black" >
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
									<tbody>
									<% 
								    double totSanctionCost=0,totReSanctionCost=0,totFESanctionCost=0;
									double totExpenditure=0,totREExpenditure=0,totFEExpenditure=0;
									double totCommitment=0,totRECommitment=0,totFECommitment=0,totalDIPL=0,totalREDIPL=0,totalFEDIPL=0;
									double totBalance=0,totReBalance=0,totFeBalance=0,btotalRe=0,btotalFe=0;
									int counts=1;
									if(projectFinancialDetails!=null){
										for(ProjectFinancialDetails projectFinancialDetail:projectFinancialDetails){    %>
									 
									    	<tr>
												<td class="std"  align="center" style="border:1px solid black;"><%=counts++ %></td>
												<td class="std"  style=" border: 1px solid black;text-align: left;border:1px solid black;"><%=projectFinancialDetail.getBudgetHeadDescription()!=null?projectFinancialDetail.getBudgetHeadDescription(): " - "%></td>
												<td class="std"  align="right" style="text-align: right; border:1px solid black;"><%=projectFinancialDetail.getReSanction()!=null?df.format(projectFinancialDetail.getReSanction()): " - "  %></td>
												<%totReSanctionCost+=(projectFinancialDetail.getReSanction());%>
													<td class="std"  align="right" style="text-align: right;border:1px solid black;"><%=projectFinancialDetail.getFeSanction()!=null?df.format(projectFinancialDetail.getFeSanction()): " - " %></td>
												<%totFESanctionCost+=(projectFinancialDetail.getFeSanction());%>
													<td class="std"  align="right" style="text-align: right;border:1px solid black;"><%=projectFinancialDetail.getReExpenditure()!=null?df.format(projectFinancialDetail.getReExpenditure()): " - "  %></td>
												<%totREExpenditure+=(projectFinancialDetail.getReExpenditure());%>
												    <td class="std"  align="right" style="text-align: right;border:1px solid black;"><%=projectFinancialDetail.getFeExpenditure()!=null?df.format(projectFinancialDetail.getFeExpenditure()): " - " %></td>
												<%totFEExpenditure+=(projectFinancialDetail.getFeExpenditure());%>
												    <td class="std"  align="right" style="text-align: right;border:1px solid black;"><%=projectFinancialDetail.getReOutCommitment()!=null?df.format(projectFinancialDetail.getReOutCommitment()): " - " %></td>
												<%totRECommitment+=(projectFinancialDetail.getReOutCommitment());%>
												    <td class="std"  align="right" style="text-align: right;border:1px solid black;"><%=projectFinancialDetail.getFeOutCommitment()!=null?df.format(projectFinancialDetail.getFeOutCommitment()): " - " %></td>
												<%totFECommitment+=(projectFinancialDetail.getFeOutCommitment());%>
													<td class="std"  align="right" style="text-align: right;border:1px solid black;"><%=projectFinancialDetail.getReBalance()!=null && projectFinancialDetail.getReDipl()!=null?df.format(projectFinancialDetail.getReBalance()+projectFinancialDetail.getReDipl()): " - " %></td>
												<%btotalRe+=(projectFinancialDetail.getReBalance()+projectFinancialDetail.getReDipl());%>
													<td class="std"  align="right" style="text-align: right;border:1px solid black;"><%=projectFinancialDetail.getFeBalance()!=null && projectFinancialDetail.getFeDipl()!=null?df.format(projectFinancialDetail.getFeBalance()+projectFinancialDetail.getFeDipl()): " - " %></td>
												<%btotalFe+=(projectFinancialDetail.getFeBalance()+projectFinancialDetail.getFeDipl());%>
													 <td class="std"  align="right"style="text-align: right;border:1px solid black;"><%=projectFinancialDetail.getReDipl()!=null?df.format(projectFinancialDetail.getReDipl()): " - " %></td>
												<%totalREDIPL+=(projectFinancialDetail.getReDipl());%>
													 <td class="std"  align="right" style="text-align: right;border:1px solid black;"><%=projectFinancialDetail.getFeDipl()!=null?df.format(projectFinancialDetail.getFeDipl()): " - " %></td>
												<%totalFEDIPL+=(projectFinancialDetail.getFeDipl());%>
													<td class="std"  align="right" style="text-align: right;border:1px solid black;"><%=projectFinancialDetail.getReBalance()!=null?df.format(projectFinancialDetail.getReBalance()): " - " %></td>
												<%totReBalance+=(projectFinancialDetail.getReBalance());%>
													<td class="std"  align="right" style="text-align: right;border:1px solid black;"><%=projectFinancialDetail.getFeBalance()!=null?df.format(projectFinancialDetail.getFeBalance()): " - " %></td>
												<%totFeBalance+=(projectFinancialDetail.getFeBalance());%>
											</tr>
										<%} }%>
																
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
									</table>
														
							
					<% }else {  %>
						
						<table style=" width: 950px;font-size: 16px; border-collapse: collapse;" >
						<tr >
							<td colspan="8" style="border: 1px solid black;border-top:0px;font-weight: bold"  align="center">No Data Available</td>
						</tr>
					</table>	
							  
					<% } %>
</div>
<br clear=all style='page-break-before:always; mso-break-type:section-break'>
<div class=landscape_A4_page>
		<div  align="center" style="text-decoration: underline">Annexure - C</div>		
				<table style=" margin-left: 8px; width: 950px; margin-top:5px;font-size: 16px; border-collapse: collapse;" >
						<tr>
							<th colspan="10" style="text-align: left; font-weight: 700;">Details of procurements items envisaged in the projects along with status</th>
						</tr>
					</table>	
							<table style=" margin-left: 8px; width: 950px; margin-top:5px;font-size: 16px; border-collapse: collapse;border: 1px solid black" >
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
											<th class="std" style="border: 1px solid black;max-width:90px;">Demand No <br> Demand Date</</th>
<!-- 											<th class="std" style="border: 1px solid black;max-width:90px; ">Demand Date</th>
 -->											<th class="std" colspan="4" style="border: 1px solid black;max-width: 150px;"> Nomenclature</th>
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
												<td class="std"  style=" border: 1px solid black;"><%=obj[1]!=null?obj[1].toString(): " - "%><br><%=obj[3]!=null?sdf.format(sdf1.parse(obj[3].toString())): " - " %></td>
<%-- 												<td class="std"  style=" border: 1px solid black;"><%=sdf.format(sdf1.parse(obj[3].toString()))%></td>
 --%>												<td class="std" colspan="4" ><%=obj[8]!=null?obj[8].toString(): " - "%></td>
												<td class="std" style=" text-align:right;"> <%=obj[5]!=null?format.format(new BigDecimal(obj[5].toString())).substring(1): " - " %></td>
												<td class="std"  style=" border: 1px solid black;"> <%=obj[10]!=null?obj[10].toString(): " - "%> </td>
												<td class="std" colspan="3" style=" border: 1px solid black;"><%=obj[11]!=null?obj[11].toString(): " - "%> </td>		
											</tr>		
											<%
											estcost += Double.parseDouble(obj[5].toString());
										    }%>
										    
										    <tr>
										    	<td class="std" colspan="8" style="text-align: right;"><b>Total</b></td>
										    	<td class="std" style="text-align: right;"><b><%=df.format(estcost)%></b></td>
										    	
										    	<td class="std" colspan="2" style="text-align: right;"></td>

										    </tr>
										    
										    
										    <% }else{%>											
												<tr><td colspan="11" style="border: 1px solid black;text-align: center;" class="std" >Nil </td></tr>
											<%} %>
											<!-- ********************************Future Demand Start *********************************** -->
											<tr>
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
												<td class="std" colspan="4" style="border: 1px solid black;" ><%=obj[3]!=null?obj[3].toString(): " - "%></td>
												<td class="std" style="border: 1px solid black; text-align:right;"> <%=obj[2]!=null?format.format(new BigDecimal(obj[2].toString())).substring(1): " - " %></td>
												<td class="std"  style=" border: 1px solid black;"> <%=obj[6]!=null?obj[6].toString(): " - "%> </td>
												<td class="std" colspan="4" style="border: 1px solid black;"><%=obj[4]!=null?obj[4].toString(): " - "%> </td>		
											</tr>		
											<%
												estcost += Double.parseDouble(obj[2].toString());
										    }%>
										    
										    <tr>
										    	<td  class="std"colspan="7" style="border: 1px solid black;text-align: right;"><b>Total</b></td>
										    	<td class="std" style="border: 1px solid black;text-align: right;" colspan="4"><b><%=df.format(estcost)%></b></td>
										    </tr>
										    
										    
										    <% }else{%>											
												<tr><td colspan="11" style="border: 1px solid black;text-align: center;" class="std" >Nil </td></tr>
											<%} %>
											
									<!-- ********************************Future Demand End *********************************** -->
											
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
					<%if(!obj[1].toString().equalsIgnoreCase(demand)){ %><%if(obj[1]!=null) {%> <%=obj[1].toString()%><% }else{ %>-<%} %><br>
					<%=obj[3]!=null?sdf.format(sdf1.parse(obj[3].toString())): " - " %>
					<%} %>
					</td>
					<td colspan="2" <%if(!obj[1].toString().equalsIgnoreCase(demand)){ %> style="border: 1px solid black;border-bottom:none;"<%} else{ %> style="border: 1px solid black;border-bottom:none;border-top:none;"<%} %>>
					<%if(!obj[1].toString().equalsIgnoreCase(demand)){ %>
					<%=obj[8]!=null?obj[8].toString(): " - "%>
					<%} %>
					</td>
						<td style="border: 1px solid black;text-align: center;">
						<% if(obj[2]!=null){%> <%=obj[2].toString()%> <%}else{ %>-<%} %> <br>
						<%if(obj[14]!=null){%> <%=sdf.format(sdf1.parse(obj[14].toString()))%> <%}else{ %> - <%} %>
					</td>
						<td style="border: 1px solid black;text-align: right"><%if(obj[6]!=null){%> <%=format.format(new BigDecimal(obj[6].toString())).substring(1)%> <%} else{ %> - <%} %></td>
					<td style="border: 1px solid black;">
					<%if(obj[4]!=null){%> <%=sdf.format(sdf1.parse(obj[4].toString()))%> <%}else{ %> - <%} %>
					<br>
					<%if(obj[7]!=null){if(!obj[7].toString().equals("null")){%> <%=sdf.format(sdf1.parse(obj[7].toString()))%><%}}else{ %>-<%} %></td>
						
						<td colspan="2" style="border: 1px solid black;"><%=obj[12]!=null?obj[12].toString(): " - " %> </td>
						<td <%if(!obj[1].toString().equalsIgnoreCase(demand)){ %> style="border: 1px solid black;border-bottom:none;"<%} else{ %> style="border: 1px solid black;border-bottom:none;border-top:none;"<%} %>>
						<%if(!obj[1].toString().equalsIgnoreCase(demand)){ %>
					<%=obj[10]!=null?obj[10].toString(): " - "%>
					<%} %>
					
					</td>
					
					
						<td <%if(!obj[1].toString().equalsIgnoreCase(demand)){ %> style="border: 1px solid black;border-bottom:none;"<%} else{ %> style="border: 1px solid black;border-bottom:none;border-top:none;"<%} %>>
						<%if(!obj[1].toString().equalsIgnoreCase(demand)){ %>
					<%=obj[11]!=null?obj[11].toString(): " - "%>
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
										    	<td colspan="1" class="std" style="text-align: right;border: 1px solid black;"><b><%=df.format(socost)%></b></td>
										    	<td colspan="5" class="std" style="text-align: right;border: 1px solid black;"><b></b></td>
										   		 </tr>	
										 <% }else{%>
											
												<tr><td colspan="8" style="border: 1px solid black;" class="std"  style="text-align: center;">Nil </td></tr>
											<%} %>
									</table> 
							<table style=" margin-left: 8px; width: 950px; margin-top:5px;font-size: 16px; border-collapse: collapse;border: 1px solid black" >
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
										      <td class="std" style="text-align: center;border: 1px solid black;"><%=obj.getDemandCount()!=null?obj.getDemandCount(): " - " %></td>
										      <td class="std" style="text-align: center;border: 1px solid black;"><%=obj.getEstimatedCost()!=null?obj.getEstimatedCost(): " - " %></td>
										      <td class="std" style="text-align: center;border: 1px solid black;"><%=obj.getSupplyOrderCount()!=null?obj.getSupplyOrderCount(): " - "%></td>
										      <td class="std" style="text-align: center;border: 1px solid black;"><%=obj.getTotalOrderCost()!=null?obj.getTotalOrderCost(): " - " %></td>
										      <td class="std" style="text-align: center;border: 1px solid black;"><%=obj.getTotalExpenditure()!=null?obj.getTotalExpenditure(): " - "%></td>
										   </tr>
										   <%}}}else{%>
										   <tr>
										      <td class="std" colspan="5" style="text-align: center;border: 1px solid black;">IBAS Server Could Not Be Connected</td>
										   </tr>
										   <%} %>
									</table>		
									
</div>

<br clear=all style='page-break-before:always; mso-break-type:section-break'>
<div class=landscape_A4_page>
					<div align="center" style="text-decoration: underline">Annexure - D</div>		
						<table style=" width: 950px; margin-top:5px;font-size: 16px; border-collapse: collapse;">
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;">Major milestones to be completed in next 06 months along with the financial outlay.</th>
						</tr>
					</table>
					<table style="margin-top: 5px; margin-bottom: 0px;  width: 950px; font-size: 16px; border-collapse: collapse;border: 1px solid black" >
							 <thead>
									
								<tr style="font-size:14px; ">
									<th class="std"  style=" border: 1px solid black;width:20px !important;">SN</th>
									<th class="std"  style=" border: 1px solid black;width:20px; ">MS</th>
									<th class="std"  style=" border: 1px solid black;width:20px; ">L</th>
									<th class="std"  style=" border: 1px solid black;width:400px;">Action Plan </th>	
									<th class="std"  style=" border: 1px solid black;width:90px;">Responsibility </th>
									<th class="std"  style=" border: 1px solid black;width:100px;">PDC</th>	
									<th class="std"  style=" border: 1px solid black;width:70px;">Progress </th>
					                 <th class="std"  style=" border: 1px solid black;width:180px;">Remarks</th>
								</tr>
							</thead>
							<tbody style="font-size: 14px;">
								<%if(ActionPlanSixMonths.size()>0){ 
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
											<td class="std"  style="border: 1px solid black; border:1px solid black; text-align: center;<%if(!obj[0].toString().equalsIgnoreCase(mainMileStone)||count1==1){%>font-weight:bold;<%}%>">M<%=obj[22]!=null?obj[22].toString(): " - " %></td>
											
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
													<%=entry.getValue()!=null?entry.getValue(): " - " %>
												<%}
												}
												    countB=1;
												    countC=1;
													countD=1;
													countE=1;
												}else if(obj[26].toString().equals("2")) { 
													
													for(Map.Entry<Integer, String>entry:treeMapLevTwo.entrySet()){
													if(entry.getKey().toString().equalsIgnoreCase(obj[3].toString())){%>
													<%=entry.getValue()!=null?entry.getValue(): " - " %>
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
												<p style="text-align: justify"><%=obj[9]!=null?obj[9].toString(): " - " %></p>
												<%}else if(obj[26].toString().equals("1")) { %>
												<p style="text-align: justify"><%=obj[10]!=null?obj[10].toString(): " - "%></p>
												<%}else if(obj[26].toString().equals("2")) { %>
												<p style="text-align: justify"><%=obj[11]!=null?obj[11].toString(): " - "%></p>
												<%}else if(obj[26].toString().equals("3")) { %>
												<p style="text-align: justify"><%=obj[12]!=null?obj[12].toString(): " - "%></p>
												<%}else if(obj[26].toString().equals("4")) { %>
												<p style="text-align: justify"><%=obj[13]!=null?obj[14].toString(): " - "%></p>
												<%}else if(obj[26].toString().equals("5")) { %>
												<p style="text-align: justify"><%=obj[14]!=null?obj[15].toString(): " - "%></p>
												<%}%>
											</td>
											<td class="std"  style=" border: 1px solid black;"><%=obj[24]!=null?obj[24].toString(): " - " %>(<%=obj[25]!=null?obj[25].toString(): " - " %>)</td>
											<td class="std" style="border: 1px solid black; font-size: 12px;font-weight:bold;" >
											<%=obj[8]!=null?sdf.format(sdf1.parse(obj[8].toString())) : " - " %>
											<%if(!LocalDate.parse(obj[8].toString()).equals(LocalDate.parse(obj[29].toString()))){ %>
											<br><%=obj[29]!=null?sdf.format(sdf1.parse(obj[29].toString())) : " - " %>
											<%} %>
											</td>
											<td class="std"  style=" border: 1px solid black;text-align: center"><%=obj[16] %>%</td>											
								
											<td  class="std"  style="max-width: 80px;border: 1px solid black;">
												<%if(obj[28]!=null){ %> <%=obj[28].toString() %> <%} %>
											</td>
										</tr>
									<%milecount++;mile=obj[2].toString();mileA=obj[3].toString();mainMileStone=obj[0].toString();mileBid=obj[1].toString();}} %>
								<%} else{ %>
								<tr><td class="std"  colspan="9" style="text-align:center; "> Nil</td></tr>
								<%} %>
						</tbody>				
					</table>
</div>
</div>

				
	</div>
	</div> 
	
	</body>
	<script>
	  jQuery(document).ready(function($) {
    	  $("#btn-export").click(function(event) {
    	    $("#source-html").wordExport("<%=committeescheduleeditdata[8].toString().toUpperCase() %>"+"-MOM"+ "(<%=sdf3.format(sdf1.parse(committeescheduleeditdata[2].toString()))%>)");
    	  });
    	});
	</script>
</html>

