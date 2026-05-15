<%@page import="lombok.val"%>
<%@page import="com.vts.pfms.model.BriefingHeading"%>
<%@page import="java.util.regex.Matcher"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.nio.file.Paths"%>
<%@page import="java.nio.file.Path"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.time.LocalTime"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.net.Inet4Address"%>
<%@page import="java.time.temporal.ChronoUnit"%>
<%@page import="com.vts.pfms.committee.model.Committee"%>
<%@page import="java.time.LocalDate"%>
<%@page import="com.vts.pfms.print.model.TechImages"%>
<%@page import="java.io.File"%>
<%@page import="org.apache.commons.io.FileUtils"%>
<%@page import="java.util.Base64"%>
<%@page import="org.apache.commons.io.FilenameUtils"%>
<%@page import="com.vts.pfms.model.LabMaster"%>
<%@page import="com.vts.pfms.AESCryptor"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder"%>
<%@page import="java.net.InetAddress"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="java.text.Format"%>
<%@page import="java.util.Locale"%>
<%@page import="com.vts.pfms.master.dto.ProjectFinancialDetails"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="java.util.* , java.util.stream.Collectors"%> 
<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@page import="com.vts.pfms.model.TotalDemand" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>  
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Briefing Paper</title>

<style type="text/css">


p{
  text-align: justify;
  text-justify: inter-word;
}
table{
	border-collapse: collapse;
}

 th
 {
 	border: 1px solid black;
 	text-align: center;
 	padding: 5px;
	overflow-wrap: break-word;
 }
 
 td
 {
 	border: 1px solid black;
 	text-align: left;
 	padding: 5px;
 	overflow-wrap: break-word;
 }

th, td
{

	word-break :normal;
}

.break
	{
		page-break-after: always;
		margin: 25px 0px 25px 0px;
	} 
	 /* 
#pageborder {
      position:fixed;
      left: 0;
      right: 0;
      top: 0;
      bottom: 0;
      border: 2px solid black;
}     
  */
@page {             
           size: A4;  
          /* size: 1123px 794px;  */  /* A4 Landscape */
          /*  size: 794px 1123px;  */  /* A4 Portrait */
          margin-top: 49px;
          margin-left: 72px;
          margin-right: 59px;
          margin-bottom: 49px; 	
          /* border: 1px solid black;  */
          padding-top: 15px;
          
          <%-- @bottom-left {          		
        
             content : "The information in this Document is proprietary of <%=labInfo.getLabCode()!=null?(labInfo.getLabCode()): " - " %> /DRDO , MOD Government of India. Unauthorized possession/use is violating the Government procedure which may be liable for prosecution. ";
             margin-bottom: 30px;
             margin-right: 5px;
             font-size: 10px;
          } --%>
             
            @bottom-right {
		      content: "Page " counter(page) " of " counter(pages);
		   }
		   
          <%--  @top-right {
             
             content: "<%= projectattributes.get(0)[12]!=null?(projectattributes.get(0)[12].toString()): " - " %>";
             margin-top: 30px;
             margin-right: 50px;
          } --%>
          
          <%-- @top-left {
          	margin-top: 30px;
            margin-left: 10px;
            content: url("data:image/*;base64,<%=lablogo%>");  
          }   --%>     
          
           <%--  @top-left {
	          content: "Project: <%=ProjectCode!=null?(ProjectCode): " - "%>"; 
			  margin-top: 30px;
              margin-left: 50px;
             
  			}   
  			
  			@top-center {
	         content: "<%=CommitteeCode!=null?(CommitteeCode): " - " %> #<%=Long.parseLong(committeeMetingsCount[1].toString())+1 %>"; 
			
			margin-top: 30px;
             
  			}  --%>
          
 }
 .border
 {
 	border: 1px solid black;
 }
 .textleft{
 	text-align: left;
 }
 div
 {
  	width: 100%;
 }
 
 th
 {
 	border: 1px solid black;
 	text-align: center;
 	padding: 5px;
 }
 
 td
 {
 	border: 1px solid black;
 	text-align: left;
 	padding: 5px;
 }
 
  
 .textcenter{
 	
 	text-align: center;
 }

 .sth
 {
 	   
 	   border: 1px solid black;
 }
 
 .std
 {
 	text-align: center;
 	border: 1px solid black;
 }
 
 
  #containers {
    width: 100%;
    height: 100%;
    margin: 0;
    padding: 0;
}

.anychart-credits {
   display: none;
}

.flex-container 
{
  display: flex;
  flex-direction: column;
  min-height: 100vh;
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

.assigned{
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
 
.delaydays
{
	color:#000000;
	font-weight: 700;
}

.firstpage th{
	border:none !important
}
 
.executive{
	align-items: center;
} 

.sub-title{
	font-size : 20px !important;
	color: #145374 !important
}

.subtables{
	/* width: 970px !important; */
	width: 100%!important;
	max-width: 650px!important;
}

.date-column{
	max-width:60px !important;
}
 
.status-column{
	max-width:10px !important;
} 

.resp-column{
	max-width:80px !important;
} 
 
.currency{
	color:#367E18 !important;
	font-style: italic;
} 


.subtables th{
	/* background-color: #001253 !important; 
	color: white !important;
	border-color: white; */
	color: #001253 !important;
	
}
 
.mainsubtitle{
	font-size : 18px !important;
	color:#882042 !important;
}
 
 
.projectattributetable th{
	text-align: left !important;
} 
 
</style>

</head>
<%
	int index = 1;
	DecimalFormat df=new DecimalFormat("####################.##");
	FormatConverter fc=new FormatConverter(); 
	SimpleDateFormat sdf=fc.getRegularDateFormat();
	SimpleDateFormat sdf1=fc.getSqlDateFormat();
	String projectid = (String) request.getAttribute("projectid"); 
	String committeeid = (String) request.getAttribute("committeeid"); 
	String scheduleid = (String) request.getAttribute("scheduleid"); 
	Object[] lastmeetingVenue =  (Object[]) request.getAttribute("lastmeetingVenue");
	List<Object[]> projectslist=(List<Object[]>)request.getAttribute("projectslist");
	List<Object[]> SpecialCommitteesList = (List<Object[]>)request.getAttribute("SpecialCommitteesList");
	List<BriefingHeading> headingList = (List<BriefingHeading>)request.getAttribute("headingList");
	String projectName= null;
	
	List<Object[]> ProjectDetail=(List<Object[]>)request.getAttribute("ProjectDetails"); 
	List<Object[]> projectattributeslist = (List<Object[]> )request.getAttribute("projectattributes");
	List<String> projectidlist = (List<String>)request.getAttribute("projectidlist");
	List<List<Object[]>> ProjectRevList = (List<List<Object[]>>)request.getAttribute("ProjectRevList");
	List<List<Object[]>> ebandpmrccount = (List<List<Object[]>> )request.getAttribute("ebandpmrccount");
	List<Object[]> projectdatadetails = (List<Object[]> )request.getAttribute("projectdatadetails");
	String filePath=(String)request.getAttribute("filePath");
	String projectLabCode=(String)request.getAttribute("projectLabCode");
	String text=(String)request.getAttribute("text");
	List<Object[]> projectattributes = (List<Object[]> )request.getAttribute("projectattributes");
	String lablogo=(String)request.getAttribute("lablogo");
	LabMaster labInfo=(LabMaster)request.getAttribute("labInfo");
	Committee committee=(Committee)request.getAttribute("committeeData");
	String CommitteeCode = committee.getCommitteeShortName().trim();
	Object[] committeeMetingsCount =  (Object[]) request.getAttribute("committeeMetingsCount");
	Map<String,List<Object[]>> headingDetails = (Map<String,List<Object[]>>) request.getAttribute("headingDetails");
%>
<body>

<div class="firstpage" id="firstpage" align="center"> 
	
		<!-- <div align="center" ><h2 style="color: #145374 !important">for</h2></div> -->

			<div align="center" ><h2 <%if(text!=null && text.equalsIgnoreCase("p")) {%>style="color: #4C9100 !important;"<%}else{ %> style="color: #145374 !important" <%} %>><%=CommitteeCode!=null?(CommitteeCode): " - " %> #<%=Long.parseLong(committeeMetingsCount[1].toString()) %></h2></div>
			
			<div align="center" ><h2 <%if(text!=null && text.equalsIgnoreCase("p")) {%>style="color: #4C9100 !important;"<%}else{ %> style="color: #145374 !important" <%} %>>Project : <%= projectattributes.get(0)[0]!=null?projectattributes.get(0)[0].toString(): " - " %>
			
			<%if(projectattributes.size()>1) {
						for(int item=1;item<projectattributes.size();item++){
						%>
						 <br>
						<span style="font-size: 1rem;"><%= projectattributes.get(item)[1]!=null?(projectattributes.get(item)[1].toString()): " - " %> (<%= projectattributes.get(item)[0]!=null?(projectattributes.get(item)[0].toString()): " - " %>) (SUB)</span>
						 <%}} %>
			</h2></div>
			<%if(text!=null && text.equalsIgnoreCase("p")) {%>
		<div align="center" ><h1 style="color: #145374 !important;font-family: 'Muli'!important">Presentation <br> for </h1></div>
		<%}else{ %>
		<div align="center" ><h1 style="color: #145374 !important;font-family: 'Muli'!important">Briefing Paper </h1></div>
		<%} %>
		
		<br>
			<table class="executive" style="align: center;margin-left: auto;margin-right:auto;  font-size: 16px;"  >
				<tr>			
					<th colspan="8" style="text-align: center; font-weight: 700;">
					<img class="logo" style="width:150px;height: 150px;margin-bottom: 5px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> > 
					</th>
				</tr>
			</table>
		<br><br>
						<% if(lastmeetingVenue!=null){ %>
							<div class="executive" align="center">
							<table style="margin-left: auto;margin-right:auto;width:650px;" >
								<tr >
									 <th  style="text-align: center; font-size: 20px;padding: 0px; "> <u>Meeting Id </u> </th></tr><tr>
									 <th  style="text-align: center;  font-size: 20px;padding: 0px;  "> <%=lastmeetingVenue[1]!=null?(lastmeetingVenue[1].toString()): " - " %> </th>				
								 </tr>
							</table>
							<br><br>
							 <table style="margin-left: auto;margin-right:auto;width:650px;maring-top:20px; " >
								 <tr>
									 <th  style="text-align: center; width: 50%;font-size: 20px;padding: 0px; "> <u> Meeting Date </u></th>
									 <th  style="text-align: center;  width: 50%;font-size: 20px;padding: 0px; "><u> Meeting Time </u></th>
								 </tr>
								 <tr>
								 	<%-- <%LocalTime starttime = LocalTime.parse(LocalTime.parse(nextMeetVenue[3].toString(),DateTimeFormatter.ofPattern("HH:mm:ss")).format( DateTimeFormatter.ofPattern("HH:mm") ));   %> --%>
									 <td  style="text-align: center;  width: 50%;font-size: 20px ;padding: 0px;border:0px !important;"> <b><%=sdf.format(sdf1.parse(lastmeetingVenue[2].toString()))%></b></td>
									 <td  style="text-align: center;  width: 50%;font-size: 20px ;padding: 0px;border:0px !important;"> <b><%=lastmeetingVenue[3]!=null?(lastmeetingVenue[3].toString()): " - "/* starttime.format( DateTimeFormatter.ofPattern("hh:mm a") ) */ %></b></td>
								 </tr>
							 </table>
							 <br><br>
							 <table style=" margin-left: auto;margin-right:auto;width:650px; " >
								<tr >
									 <th  style="text-align: center; font-size: 20px;padding: 0px "> <u>Meeting Venue</u> </th></tr><tr>
									 <th  style="text-align: center;  font-size: 20px;padding: 0px  "> <% if(lastmeetingVenue[5]!=null){ %><%=(lastmeetingVenue[5].toString()) %> <%}else{ %> - <%} %></th>				
								 </tr>
							</table>
							</div>
						<%}else{ %>
							<br><br><br><br><br><br><br><br><br><br><br><br><br>
						<%} %>
						
						<br><br>
		<table class="executive" style="align: center;margin-bottom:0px; margin-left: auto;margin-right:auto;  font-size: 16px;margin-top:0px;"  >
		
			<tr>
				<th colspan="8" style="text-align: center; font-weight: 700;font-size: 22px;padding-bottom: 0px;">PGAD/RCI</th>
			</tr>
		
		<!-- <tr>
			<th colspan="8" style="text-align: center; font-weight: 700;font-size:15px;padding-bottom: 0px;">Government of India, Ministry of Defence</th>
		</tr> -->
		<tr>
			<th colspan="8" style="text-align: center; font-weight: 700;font-size:15px;padding-bottom: 0px;">Defence Research & Development Organization</th>
		</tr>
		<tr>
			<th colspan="8" style="text-align: center; font-weight: 700;font-size:15px;padding-bottom: 0px;"><%if(labInfo.getLabAddress() !=null){ %><%=(labInfo.getLabAddress())  %> , <%=labInfo.getLabCity()!=null?(labInfo.getLabCity()): " - " %><%}else{ %>LAB ADDRESS<%} %></th>
		</tr>
		</table>			
		
	</div>
	
		
<h1 class="break"></h1> 
<%char ch='a'; for(int z=0 ; z<projectidlist.size();z++) {   %>
	<%if(z>0){ %><%-- <h1 class="break"></h1> --%> <%} %>
	
	<div  id="detailContainer" align="center" >
	
<!-- ------------------------------------heading commented------------------------------------------------- -->	
	
<!-- ------------------------------------project attributes------------------------------------------------- -->
			<div style="margin-left: 10px;<%if(ch!='a') {%>margin-top:30px!important;<%} %>" align="left"><b class="sub-title"> 
			
				<%-- <a class="sub-title" href="<%= HyperlinkPath+ "/ProjectSubmit.htm?ProjectId="+projectid + "&action=edit" %>" target="_top" rel="noopener noreferrer" >1. Project Attributes: </a> --%> 
				<span class="mainsubtitle" ><%= index %><%if(projectidlist.size()>1) {%>(<%=(char)(ch++)%>)<%} %> . Project Attributes: </span>
			</b>
			<b><%=ProjectDetail.get(z)[1]!=null?(ProjectDetail.get(z)[1].toString()): " - "%><% if (z > 0) { %>(SUB)<% } %>  </b>
			</div>
			<%if(projectattributes.get(z)!=null){ %>
			
			<table class="subtables projectattributetable" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px;   border-collapse:collapse;" >
										<tr>
											 <td style="width: 5px !important; padding: 5px; padding-left: 10px">(a)</td>
											 <th  style="width: 150px;padding: 5px; padding-left: 10px"><b>Project Title</b></th>
											 <td colspan="4" style=" width: 370px; padding: 5px; padding-left: 10px"> <%=projectattributes.get(z)[1]!=null?(projectattributes.get(z)[1].toString()): " - " %></td>
										</tr>
										<tr>
											 <td  style="padding: 5px; padding-left: 10px">(b)</td>
											 <th style="width: 150px;padding: 5px; padding-left: 10px"><b>Project No</b></th>
											 <td colspan="4" style=" width: 370px; padding: 5px; padding-left: 10px"> <%=projectattributes.get(z)[2]!=null?(projectattributes.get(z)[2].toString()): " - "%></td>
										</tr>
										<tr>
											 <td  style="padding: 5px; padding-left: 10px">(c)</td>
											 <th style="width: 150px;padding: 5px; padding-left: 10px"><b>Project Unit Code </b></th>
											 <td colspan="4" style=" width: 370px; padding: 5px; padding-left: 10px"> <%=projectattributes.get(z)[18]!=null?(projectattributes.get(z)[18].toString()): " - "%>  </td>
										</tr>
										<tr>
											 <td  style="padding: 5px; padding-left: 10px">(d)</td>
											 <th style="width: 150px;padding: 5px; padding-left: 10px"><b>Project Code </b></th>
											 <td colspan="4" style=" width: 370px; padding: 5px; padding-left: 10px"> <%=projectattributes.get(z)[0]!=null?(projectattributes.get(z)[0].toString()): " - "%> </td>
										</tr>
										<tr>
											 <td  style=" padding: 5px; padding-left: 10px">(e)</td>
											 <th  style="width: 150px;padding: 5px; padding-left: 10px"><b>Category</b></th>
											 <td colspan="4" style=" width: 370px; padding: 5px; padding-left: 10px"><%=projectattributes.get(z)[14]!=null?(projectattributes.get(z)[14].toString()): " - "%></td>
										</tr>
										<tr>
											 <td  style="padding: 5px; padding-left: 10px">(f)</td>
											 <th  style="width: 150px;padding: 5px; padding-left: 10px"><b>Date of Sanction</b></th>
											 <td colspan="4" style=" width: 370px; padding: 5px; padding-left: 10px"><%=sdf.format(sdf1.parse(projectattributes.get(z)[3].toString()))%></td>
										</tr>
										<tr>
											 <td  style="width: 20px; padding: 5px; padding-left: 10px">(g)</td>
											 <th  style="width: 150px;padding: 5px; padding-left: 10px"><b>Nodal and Participating Labs</b></th>
											 <td colspan="4" style=" width: 370px; padding: 5px; padding-left: 10px"><%if(projectattributes.get(z)[15]!=null){ %><%=(projectattributes.get(z)[15].toString())%><%} %></td>
										</tr>
										<tr>
											 <td  style=" padding: 5px; padding-left: 10px">(h)</td>
											 <th  style="width: 150px;padding: 5px; padding-left: 10px"><b>Objective</b></th>
											 <td colspan="4" style=" width: 370px; padding: 5px; padding-left: 10px;text-align: justify"> <%=projectattributes.get(z)[4]!=null?(projectattributes.get(z)[4].toString()): " - "%></td>
										</tr>
										<tr>
											 <td  style="padding: 5px; padding-left: 10px">(i)</td>
											 <th  style="width: 150px;padding: 5px; padding-left: 10px"><b>Deliverables</b></th>
											 <td colspan="4" style=" width: 370px; padding: 5px; padding-left: 10px"> <%=projectattributes.get(z)[5]!=null?(projectattributes.get(z)[5].toString()): " - "%></td>
										</tr>
										<tr>
											 <td rowspan="2" style="padding: 5px; padding-left: 10px">(j)</td>
											 <th rowspan="2" style="width: 150px;padding: 5px; padding-left: 10px"><b>PDC</b></th>
											 
											<th colspan="2" style="text-align: center !important"> Original &nbsp;</th>					
											<%if( ProjectRevList.get(z).size()>0){ %>	
												<th colspan="2" style="text-align: center !important">Revised</th>																			
											<%}else{ %>													 
										 		<th colspan="2" ></th>	
										 	<%} %>
										</tr>
								 		<tr>
								 			<%if( ProjectRevList.get(z).size()>0 ){ %>								
										 		<td colspan="2" style="text-align: center;"><%= sdf.format(sdf1.parse(ProjectRevList.get(z).get(0)[12].toString()))%> </td>
										 		<td colspan="2" style="text-align: center;">
											 		<%if(LocalDate.parse(projectattributes.get(z)[6].toString()).isEqual(LocalDate.parse(ProjectRevList.get(z).get(0)[12].toString())) ){ %>
											 			-
											 		<%}else{ %>
											 			<%= sdf.format(sdf1.parse(projectattributes.get(z)[6].toString()))%>
											 		<%} %>
										 		
										 		</td>
											<%}else{ %>													 
										 		<td colspan="2" style="text-align: center;"><%= sdf.format(sdf1.parse(projectattributes.get(z)[6].toString()))%></td>
												<td colspan="2" ></td>
										 	<%} %>
										 		    
								 		</tr>
											 	
										<tr>
											<td rowspan="3" style="width: 30px; padding: 5px; padding-left: 10px">(k)</td>
											<th rowspan="3" style="padding-left: 10px"><b>Cost Breakup( &#8377; <span class="currency">Lakhs</span>)</b></th>
											
											<%if( ProjectRevList.get(z).size()>0 ){ %>
													<td style="width: 10% !important" >RE Cost</td>
													<td style="text-align: center;"><%=ProjectRevList.get(z).get(0)[17]!=null?(ProjectRevList.get(z).get(0)[17].toString()): " - " %></td> 
													<td colspan="2" style="text-align: center;"><%=projectattributes.get(z)[8]!=null?(projectattributes.get(z)[8].toString()): " - " %></td>
												</tr>
												
												
												<tr>
													<td style="width: 10% !important">FE Cost</td>		
													<td style="text-align: center;"><%=ProjectRevList.get(z).get(0)[16]!=null?(ProjectRevList.get(z).get(0)[16].toString()): " - " %></td>					
													<td colspan="2" style="text-align: center;"><%=projectattributes.get(z)[9]!=null?(projectattributes.get(z)[9].toString()): " - " %></td>
												</tr>
													
												<tr>	
													<td style="width: 10% !important">Total Cost</td>	
													<td style="text-align: center;"><%=ProjectRevList.get(z).get(0)[11]!=null?(ProjectRevList.get(z).get(0)[11].toString()): " - " %></td>
											 		<td colspan="2" style="text-align: center;"><%=projectattributes.get(z)[7]!=null?(projectattributes.get(z)[7].toString()): " - " %></td>
												</tr> 
														
											<%}else{ %>
													
													<td style="width: 10% !important">RE Cost</td>
													<td ><%=projectattributes.get(z)[8]!=null?(projectattributes.get(z)[8].toString()): " - " %></td>
													<td colspan="2" ></td>
												</tr>
											
												<tr>
													<td style="width: 10% !important">FE Cost</td>		
													<td ><%=projectattributes.get(z)[9]!=null?(projectattributes.get(z)[9].toString()): " - " %></td>					
													<td colspan="2"></td>
												</tr>
												
												<tr>	
													<td style="width: 10% !important" >Total Cost</td>	
													<td ><%=projectattributes.get(z)[7]!=null?(projectattributes.get(z)[7].toString()): " - " %></td>
													<td colspan="2"></td>			
												</tr> 
											<%} %>
												
																			 	
										<tr>
											<td  style="width: 20px; padding: 5px; padding-left: 10px">(l)</td>
											<th style="width: 150px;padding: 5px; padding-left: 10px"><b>No. of Meetings held</b> </th>
								 			<td colspan="4">
												<% if(ebandpmrccount!=null && ebandpmrccount.size()>0){
													List<Object[]> ebandpmrcsub = ebandpmrccount.get(z); 
													for(Object[] ebandpmrc: ebandpmrcsub) { %>
												 	<b><%=ebandpmrc[0]!=null?(ebandpmrc[0].toString()): " - " %> : </b>
													<span><%=ebandpmrc[1] !=null ? ((ebandpmrc[0]!=null && ebandpmrc[0].toString().equalsIgnoreCase(CommitteeCode)) ? Long.parseLong(ebandpmrc[1].toString()) - 1 : Long.parseLong(ebandpmrc[1].toString())) : " - " %></span> &emsp;&emsp;
												<%} }%>
											</td>
										</tr>
										<tr>
											<td  style="width: 20px; padding: 5px; padding-left: 10px">(m)</td>
											<th  style="width: 210px;padding: 5px; padding-left: 10px"><b>Current Stage of Project</b></th>
											<td colspan="4" style=" width: 200px;color:black; padding: 5px; padding-left: 10px ; <%if(projectdatadetails.get(z)!=null){ %> background-color: <%=projectdatadetails.get(z)[11] !=null?(projectdatadetails.get(z)[11].toString()): " - "%> ;   <%} %>" >
													 <span> <%if(projectdatadetails.get(z)!=null){ %><b><%=(projectdatadetails.get(z)[10].toString()) %> </b>  <%}else{ %>Data Not Found<%} %></span>
											</td> 
										</tr>	
			</table>
		
			<% }else{ %>
				<div align="center"  style="margin: 25px;" > Complete Project Data Not Found </div>
			<%} %>
		</div>
		<% } %>
	
		<% for(int z=0 ; z<1;z++) {   %>
	<%if(z>0){ %><%-- <h1 class="break"></h1> --%> <%} %>
	<%if(projectdatadetails.get(z)!=null && projectdatadetails.get(z)[3]!=null && projectdatadetails.get(z)[4]!=null){ 
			
				Path systemPath1 = Paths.get(filePath,projectLabCode,"ProjectData",projectdatadetails.get(z)[3].toString());
				File systemfile1 = systemPath1.toFile();
				Path systemPath2 = Paths.get(filePath,projectLabCode,"ProjectData",projectdatadetails.get(z)[4].toString());
				File systemfile2 = systemPath2.toFile();
				if(systemfile1.exists() || systemfile2.exists()){ %> 
		<div style="margin-left: 10px;" align="left" class="mainsubtitle"><b><%= ++index %>. Schematic Configuration</b></div><br>
		<div align="left" style="margin-bottom: 15px;margin-top:20px;margin-left: 10px;"><b class="sub-title"><%= index %> (a) System Configuration : </b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<%if(projectdatadetails.get(z)!=null && projectdatadetails.get(z)[3]!=null){ 
			
				Path systemPath = Paths.get(filePath,projectLabCode,"ProjectData",projectdatadetails.get(z)[3].toString());
				File systemfile = systemPath.toFile();
				if(systemfile.exists()){ %>
					<%if(!FilenameUtils.getExtension(projectdatadetails.get(z)[3].toString()).equalsIgnoreCase("pdf") ){ %>
						<div align="center">
						<img class="logo" style="max-width:15cm;max-height:17cm;"  src="data:image/*;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(systemfile))%>" alt="confi" >
						</div>
					<% }else{ %>
						<b>  System Configuration Annexure </b>
					<% }%>
				
				<%}else{ %>
					<br>
					File Missing in File System
				<%} %>
			
			
			<%}else{ %>
			<div align="center">
			<br>
				<b> File Not Found</b>
				</div>
			<%} %>
	
	</div>
				<%if(projectdatadetails.get(z)!=null && projectdatadetails.get(z)[3]!=null){ %>
				
				<%
				Path systemPath3 = Paths.get(filePath,projectLabCode,"ProjectData",projectdatadetails.get(z)[3].toString());
				File systemfile3 = systemPath3.toFile();
				if(systemfile3.exists()){ %>
					<%if(!FilenameUtils.getExtension(projectdatadetails.get(z)[3].toString()).equalsIgnoreCase("pdf") ){ %>
							<%-- <h1 class="break"></h1> --%>
					<% }else{ %>
					<% }}}%>
	
	

		
	
		<div align="left" style="margin-left: 15px;margin-bottom: 15px;margin-top:20px;"><b class="sub-title"><%= index %> (b) System Specification : </b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	
		<%if(projectdatadetails.get(z)!=null && projectdatadetails.get(z)[4]!=null){ %>
				
				<%
				Path specificPath = Paths.get(filePath,projectLabCode,"ProjectData",projectdatadetails.get(z)[4].toString());
				File specificfile = specificPath.toFile();
				if(specificfile.exists()){ %>
					<%if(!FilenameUtils.getExtension(projectdatadetails.get(z)[4].toString()).equalsIgnoreCase("pdf") ){ %>
						<div align="center"><br>
							<img class="logo" style="max-width:15cm;max-height:17cm;"  src="data:image/*;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(specificfile))%>" alt="Speci" >
						</div> 
					<% }else{ %>
						<b> System Specification Annexure </b>
					<% }%>
				
				<%}else{ %>
					<div align="center">
					<br>
					File Missing in File System
					</div>
				<%} %>
			
			
			<%}else{ %>
				<div align="center">
				<br>
				<b> File Not Found</b>
				</div>
			<%} %>
				
		
		
	</div>	
	<!-- <%-- <h1 class="break"></h1> --%> -->
					<%if(projectdatadetails.get(z)!=null && projectdatadetails.get(z)[4]!=null){ %>
				
				<%
				Path specificPath1 = Paths.get(filePath,projectLabCode,"ProjectData",projectdatadetails.get(z)[4].toString());
				File specificfile1 = specificPath1.toFile();
				if(specificfile1.exists()){ %>
					<%if(!FilenameUtils.getExtension(projectdatadetails.get(z)[4].toString()).equalsIgnoreCase("pdf") ){ %> <!-- changed -->
							<%-- <h1 class="break"></h1> --%>
					<% }else{ %>
					<% }}}}}%>
<!-- --------------------------------------------- ----------------------------------------------- -->
	<%if(projectdatadetails.get(z)!=null && projectdatadetails.get(z)[5]!=null){
					
				Path productTreePath = Paths.get(filePath,projectLabCode,"ProjectData",projectdatadetails.get(z)[5].toString());
				File productTreeFile = productTreePath.toFile();
				if(productTreeFile.exists()){ %>
		<div align="left" style="margin-left: 10px;margin-bottom: 15px;margin-top:20px;"><b class="mainsubtitle"><%= ++index %>. Overall Product tree/WBS:</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

				<%
				if(projectdatadetails.get(z)!=null && projectdatadetails.get(z)[5]!=null){
				if(productTreeFile.exists()){ %>
					<%if(!FilenameUtils.getExtension(projectdatadetails.get(z)[5].toString()).equalsIgnoreCase("pdf") ){ %>
						<div align="center"><br>
							<img class="logo" style="max-width:15cm;max-height:17cm;margin-bottom: 5px"  src="data:image/*;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(productTreeFile))%>" alt="Speci" >
						</div> 
					<% } else{ %>
						<b> Overall Product tree/WBS Annexure </b>
					<% }%>
				
				<%}else{ %>
					<div align="center">
					<br>
					File Missing in File System
					</div>
				<%} %>
			
			
			<%}else{ %>
			<div align="center">
			<br>
				<b> File Not Found</b>
			</div>
			<%} %>
			
		</div>	
		<%} }}%>
		
		<%if(headingDetails!=null && !headingDetails.isEmpty()){ 
			for (Map.Entry<String, List<Object[]>> entry : headingDetails.entrySet()) {
			    String key = entry.getKey();
			    List<Object[]> value = entry.getValue();
			    
			    if(value!=null && !value.isEmpty()){
			    String heading = value.get(0)[4].toString();
			%>
			
			<div align="left" style="margin-left: 10px;margin-bottom: 15px;margin-top:20px;"><b class="mainsubtitle"><%= ++index %>. <%=heading %></b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>

				<% for(Object[] obj: value){ %>
					<div style="margin-left: 10px;"> <%=obj[1]!=null ? obj[1].toString(): " - " %></div>
				<%} %>

		<%}}} %>
		<%-- <div align="left" style="margin-left: 10px;"><b class="mainsubtitle"><%= ++index %>. Particulars of Meeting (Annexure - A)</b></div><br> --%>
		
		<%-- <div align="left" style="margin-left: 10px;margin-bottom: 15px;margin-top:20px;"><b class="mainsubtitle"><%= ++index %>. Overall Financial Status (Annexure - B) </b> 		</div> --%>
		
</body>
</html>