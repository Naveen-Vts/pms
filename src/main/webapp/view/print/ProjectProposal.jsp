<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.time.LocalTime"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.net.Inet4Address"%>
<%@page import="com.vts.pfms.Zipper"%>
<%@page import="java.math.MathContext"%>
<%@page import="com.vts.pfms.model.TotalDemand"%>
<%@page import="java.time.temporal.ChronoUnit"%>
<%@page import="com.vts.pfms.committee.model.Committee"%>
<%@page import="org.apache.commons.io.FileUtils"%>
<%@page import="com.vts.pfms.print.model.TechImages"%>
<%@page import="com.vts.pfms.model.LabMaster"%>
<%@page import="com.vts.pfms.AESCryptor"%>
<%@page import="org.apache.commons.io.FilenameUtils"%>
<%@page import="java.io.File"%>
<%@page import="java.math.BigInteger"%>
<%@page import="java.text.Format"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="com.vts.pfms.master.dto.ProjectFinancialDetails"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"
	import="java.util.*,com.vts.*,java.text.SimpleDateFormat,java.time.LocalDate"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<link rel="shortcut icon" type="image/png" href="view/images/drdologo.png">
<jsp:include page="../static/dependancy.jsp"></jsp:include>
<spring:url value="/resources/css/slides-style.css" var="SlidesStyleCSS" />
<link href="${SlidesStyleCSS}" rel="stylesheet" />
<meta charset="ISO-8859-1">
<title>Project Proposal</title>
<style>
/* .costtable>tbody>tr:nth-child(even),.scheduletable>tbody>tr:nth-child(odd) {
  background-color: #D6EEEE;
} */
p{
text-align:justify;
font-size: 18px;
padding:10px;
}
/* #header{
background:bisque;width:95%;margin-top:-1%;border-radius:10px; 
}
#headername{
text-lign:center;color: #145374 !important;
} */
.page2>tr>th{
width: 37%;
}
.carousel-indicators .active {
	background-color:#008080!important;
	
}
</style>
</head>
<body style="background-color:#FBFCFC" class="slides-container" id="slides-container">
<%

SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
SimpleDateFormat sdf1=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
SimpleDateFormat sdf2=new SimpleDateFormat("MMMM yyyy");
NFormatConvertion nfc=new NFormatConvertion();

List<Object[]> costbreak = (List<Object[]>)request.getAttribute("costbreak");
Object[] PfmsInitiationList=(Object[])request.getAttribute("PfmsInitiationList");
List<Object[]> DetailsList=(List<Object[]>)request.getAttribute("DetailsList");
List<Object[]> CostDetailsList=(List<Object[]>)request.getAttribute("CostDetailsList");
List<Object[]> ScheduleList=(List<Object[]>)request.getAttribute("ScheduleList");
Object[] LabList=(Object[])request.getAttribute("LabList");
String lablogo=(String)request.getAttribute("lablogo");
List<Object[]> RequirementList=(List<Object[]>)request.getAttribute("RequirementList");

String InitiationId=(String)request.getAttribute("InitiationId");

%>
<div id="presentation-slides" class="carousel slide "  data-ride="carousel"  >
	<div class="carousel-inner" align="center">
	<div class="carousel-item active" style="/* background: #D6EEEE  */">
			
				<div class="content" align="center" style="height:92.4vh !important;padding-top: 15px; ">
				
					<div class="firstpage"  > 
	
						<div align="center" ><h2 style="color: #145374 !important;font-family: 'Muli'!important">Presentation for<%if(PfmsInitiationList[5]!=null){ %><br><%=(PfmsInitiationList[5].toString()) %><%}else{ %><i>Project Title</i><%} %></h2></div>
					     <div align="center" ><h3 style="color: #4C9100 !important"><%if(PfmsInitiationList[4]!=null){ %>(<%=(PfmsInitiationList[4].toString()) %>)<%}else{ %><i> - </i><%} %></h3></div>	
					     <div align="center" ><h3 style="color: #145374 !important"><%if(PfmsInitiationList[10]!=null){%> <%=sdf2.format(PfmsInitiationList[10])%><%}else{ %>-<%} %></h3></div>
					    <br><br><br><br>
					     <div align="center" ><h3 style="color: #145374 !important"><img class="logo" style="width:120px;height: 120px;margin-bottom: 5px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></h3></div>
						<br><br>
						<br>
						<div align="center"><h4 style="color: #145374 !important"><%if(LabList[1]!=null){ %><%=(LabList[1].toString()) %><%}else{ %>-<%} %></h4></div>
						<div align="center"><h5 style="color: #145374 !important" >Government of India, Ministry of Defence</h5></div>
						<div align="center"><h5 style="color: #145374 !important" >Defence Research & Development Organization</h5></div>
						<div align="center"><h5 style="color: #145374 !important" ><%if(LabList[2]!=null){ %><%=(LabList[2].toString()) %><%}else{ %> - <%} %></h5></div>
						</div>
						</div>
						</div>
						<!--2nd Page  -->
							<div class="carousel-item" style="/* background: #D6EEEE; */ ">
				<div class="content-header row" id="header" style="background: maroon;" >
				<div class="col-md-1"><img class="logo" style="width:45px;margin-top:3px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></div>
				<div class="col-md-1"></div>
				<div class="col-md-8" id="headername"><h3><b>Brief of Proposed Project</b></h3></div>
				<div class="col-md-1"></div>
				<div class="col-md-1"><img class="logo" style="width:45px;margin-top:3px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></div>
				
				</div>
				<div class="content" align="center" style=";padding-top: 15px; ">

			<table style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 10px; width: 90%; font-size: 16px; border-collapse:collapse;" >
	
			<%if ( PfmsInitiationList !=null)
			{ 
				Object[] obj = PfmsInitiationList; %>
				
			 <tr>
				<th  class="border_black 700 center" style="width:10%" ><span >SN</span></th>
				<th  class="border_black 700 left" style="text-align: center;"><span >Content</span></th>
				</tr> 
				
				<tr>
					<th  class="border_black normal center" ><span >1.</span></th>
					<td class="border_black normal left" >
						<span class="bold">Title of the Project / Name of the project</span>
						<span style="color:#309322;"><b><%if(obj[5]!=null){ %> - <%=(obj[5].toString()) %><%}else{ %><i></i><%} %></b></span>
					</td>
				</tr> 
				
					
				<tr>
					<th rowspan="3"  class="border_black normal center" style="vertical-align:top;"><span >2.</span></th>
					<td class="border_black normal left" >
						<span class="bold" >Name of the Main project</span>
						<span style="color:#309322;"> <%if(obj[12].toString().equalsIgnoreCase("N")){ %> - <%=obj[13]!=null?(obj[13].toString()): " - "%><%} else{ %> <i> - NA </i> <%} %></span>
					</td>
				</tr> 
				
				<tr>
					<td class="border_black normal left" >
						<span class="bold">Main Project Sanction Date</span>
						<span ><%if(obj[12].toString().equalsIgnoreCase("N")){ %> - To Be Obtained<%} else{ %> <i> - NA </i> <%} %></span>
					</td>
				</tr>
				
				<tr>
					<td class="border_black normal left" >
						<span class="bold" >Main Project PDC</span>
						<span  ><%if(obj[12].toString().equalsIgnoreCase("N")){ %> - To Be Obtained<%} else{ %> <i> - NA </i> <%} %></span>
					</td>
				</tr>
			
				
				<tr>
					<th  class="border_black normal center" ><span >3.</span></th>
					<td class="border_black normal left" >
						<span class="bold">Security Classification</span>
						<span ><%if(obj[3]!=null){ %> - <%=(obj[3].toString()) %><%}else{ %><i></i><%} %></span>
					</td>
				</tr>
				
				<tr>
					<th  class="border_black normal center" ><span >4.</span></th>
					<td class="border_black normal left" >
						<span class="bold">Cost </span>
						<span class="editor-text" style=" font-family: 'Times New Roman', Times, serif; font-size: 15px !important" >
						<%if(obj[6]!=null){%>
										  - &#8377;  <span><%=nfc.convert(Double.parseDouble(obj[6].toString())/100000)%></span>  Lakhs<%}else{ %><i></i><%} %></span>
					</td>
				</tr>
			
				<tr>
					<th  class="border_black normal center" ><span >5.</span></th>
					<td class="border_black normal left" >
						<span class="bold">PDC </span>
						<span ><%if(obj[7]!=null){ %> - <%=(obj[7].toString()) %> Months<%}else{ %><i></i><%} %></span>
					</td>
				</tr>
				
				<tr>
					<th  class="border_black normal center" ><span >6.</span></th>
					<td class="border_black normal left" >
						<span class="bold">Whether Plan/Non Plan Project</span>
						<span ><%if(obj[8]!=null){ if(obj[8].toString().equalsIgnoreCase("P")){%> - Plan <%}if(obj[8].toString().equalsIgnoreCase("N")){ %> - Non-Plan - <%if(obj[14]!=null){ %> (Remarks : <%=(obj[14].toString())%> ) <%}else{ %> Nil <%} %> <%}}else{ %><%} %></span>
					</td>
				</tr>
				
				<%} %>
				
				<tr>
					<th  class="border_black normal center" ><span >7.</span></th>
					<td class="border_black normal left main-text" >
						<span class="bold" ><u>Need of the Project</u> :</span>
						<%if(!DetailsList.isEmpty()){%>
						<%for (Object[] obj : DetailsList){ %>
							<span  style="font-size:15px; max-width:200px; word-wrap:break-word;"><%if(obj[19]!=null){ %><%=(obj[19].toString()) %><%}else{ %><i>-</i><%} %></span>
						<%}}else{ %>
						<span><i>-</i></span>
						<%} %>
					</td>
				</tr>
				
				<tr>
					<th  class="border_black normal center" ><span >8.</span></th>
					<td class="border_black normal left main-text" >
						<span class="bold"><u>Requirements</u> : </span>
						<%if(!DetailsList.isEmpty()){%>
						<%for (Object[] obj : DetailsList){ %>
							<span  style="font-size:15px;" ><%if(obj[13]!=null){ %><%=(obj[13].toString()) %><%}else{ %><i>-</i><%} %></span>
					<%}}else{ %>
						<span><i>-</i></span>
						<%} %>
					</td>
				</tr>
				
				<tr>
					<th  class="border_black normal center" ><span >9.</span></th>
					<td class="border_black normal left main-text" >
						<span class="bold"><u>World Scenario</u> :</span>
						<%if(!DetailsList.isEmpty()){%>
						<%for (Object[] obj : DetailsList){ %>
							<span style="font-size:15px;" ><%if(obj[24]!=null){ %><%=(obj[24].toString()) %><%}else{ %><i>-</i><%} %></span>
						<%}}else{ %>
						<span><i>-</i></span>
						<%} %>
					</td>
				</tr>
				
				<tr>
					<th  class="border_black normal center" ><span >10.</span></th>
					<td class="border_black normal left main-text" >
						<span class="bold"><u>Objective</u> : </span>
						<%if(!DetailsList.isEmpty()){%>
						<%for (Object[] obj : DetailsList){ %>
							<span style="font-size:15px;" ><%if(obj[14]!=null){ %><%=(obj[14].toString()) %><%}else{ %><i>-</i><%} %></span>
						<%}}else{ %>
						<span><i>-</i></span>
						<%} %>
					</td>
				</tr>
				
				<tr>
					<th  class="border_black normal center" ><span >11.</span></th>
					<td class="border_black normal left main-text"  >
						<span class="bold"><u>Scope</u> :</span>
						<%if(!DetailsList.isEmpty()){%>
						<%for (Object[] obj : DetailsList){ %>
							<span style="font-size:15px;" ><%if(obj[15]!=null){ %><%=(obj[15].toString()) %><%}else{ %><i>-</i><%} %></span>
					<%}}else{ %>
						<span><i>-</i></span>
						<%} %>
					</td>
				</tr>
				
				<%if(PfmsInitiationList!= null) { Object[] obj = PfmsInitiationList; %>
				
				<tr>
					<th  class="border_black normal center" ><span >12.</span></th>
					<td class="border_black normal left main-text" >
						<span class="bold"><u>Deliverables/Output</u> : </span>
						<span style="font-size:15px;" ><%if(obj[11]!=null){ %> <br><%=(obj[11].toString()) %><%}else{ %><i>-</i><%} %></span>
					</td>
				</tr>
				
				<%} %>
				
				<tr>
					<th  class="border_black normal center" ><span >13.</span></th>
					<td class="border_black normal left" >
						<span class="bold"><u>Participating Labs with Work share</u> : </span>
						<%if(!DetailsList.isEmpty()){%>
						<%for (Object[] obj : DetailsList){ %>
							<span style="font-size:15px;"" ><%if(obj[16]!=null){ %><%=(obj[16].toString()) %><%}else{ %><i>-</i><%} %></span>
					<%}}else{ %>
						<span><i>-</i></span>
						<%} %>
					</td>
				</tr>

				<tr>
					<th  class="border_black normal center" ><span >14.</span></th>
					<td class="border_black normal left main-text" >
						<span class="bold"><u>Brief of earlier work done</u> : </span>
							<%if(!DetailsList.isEmpty()){%>
						<%for (Object[] obj : DetailsList){ %>
							<span style="font-size:15px;" ><%if(obj[17]!=null){ %><%=(obj[17].toString())%><%}else{ %><i>-</i><%} %></span>
						<%}}else{ %>
						<span><i>-</i></span>
						<%} %>
					</td>
				</tr>
				
				<tr>
					<th  class="border_black normal center" ><span >15.</span></th>
					<td class="border_black normal left main-text" >
						<span class="bold"><u>Competency Established</u> : </span>
							<%if(!DetailsList.isEmpty()){%>
						<%for (Object[] obj : DetailsList){ %>
							<span style="font-size:15px;" ><%if(obj[18]!=null){ %><%=(obj[18].toString()) %><%}else{ %><i>-</i><%} %></span>
						<%}}else{ %>
						<span><i>-</i></span>
						<%} %>
					</td>
				</tr>
				
				
				<tr>
					<th  class="border_black normal center" ><span >16.</span></th>
					<td class="border_black normal left main-text" >
						<span class="bold"><u>Technology Challenges/Issues foreseen</u> : </span>
							<%if(!DetailsList.isEmpty()){%>
						<%for (Object[] obj : DetailsList){ %>
							<span style="font-size:15px;" ><%if(obj[20]!=null){ %><%=(obj[20].toString()) %><%}else{ %><i>-</i><%} %></span>
						<%}}else{ %>
						<span><i>-</i></span>
						<%} %>
					</td>
				</tr>
				
				<tr>
					<th  class="border_black normal center" ><span >17.</span></th>
					<td class="border_black normal left main-text" >
						<span class="bold"><u>Risk involved and Mitigation Plan</u> : </span>
						<%if(!DetailsList.isEmpty()){%>
						<%for (Object[] obj : DetailsList){ %>
							<span style="font-size:15px;" ><%if(obj[21]!=null){ %><%=(obj[21].toString()) %><%}else{ %><i>-</i><%} %></span>
					<%}}else{ %>
						<span><i>-</i></span>
						<%} %>
					</td>
				</tr>
				
				<tr>
					<th  class="border_black normal center" ><span >18.</span></th>
					<td class="border_black normal left main-text" >
						<span class="bold"><u>Proposal</u> : </span>
						<%if(!DetailsList.isEmpty()){%>
						<%for (Object[] obj : DetailsList){ %>
							<span style="font-size:15px;" ><%if(obj[22]!=null){ %><%=(obj[22].toString()) %><%}else{ %><i>-</i><%} %></span>
						<%}}else{ %>
						<span><i>-</i></span>
						<%} %>
					</td>
				</tr>
				 
				<tr>
					<th  class="border_black normal center" ><span >19</span></th>
					<td class="border_black normal left main-text" >
						<span class="bold"><u>Realization Plan</u> :</span>
						<%if(!DetailsList.isEmpty()){%>
						<%for (Object[] obj : DetailsList){ %>
							<span class="editor-text" ><%if(obj[23]!=null){ %><%=(obj[23].toString()) %><%}else{ %><i>-</i><%} %></span>
						<%}}else{ %>
						<span><i>-</i></span>
						<%} %>
					</td>
				</tr>

			</table>


</div>
	</div>
	<div class="carousel-item" style="background:">
				<div class="content-header row" id="header" >
				<div class="col-md-1"><img class="logo" style="width:45px;margin-top:3px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></div>
				<div class="col-md-1"></div>
				<div class="col-md-8" id=""><h3><b>Need of Project</b></h3></div>
				<div class="col-md-1"></div>
				<div class="col-md-1"><img class="logo" style="width:45px;margin-top:3px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></div>
				
				</div>
				
				
				<div class="content" align="center" style="height:86vh !important;padding-top:15px; ">
		
			
				<%
							if (!DetailsList.isEmpty()) {

								for (Object[] obj : DetailsList) {
							if(obj[6]!=null){%>
							<%=(obj[6].toString())%>
							<%}else{ %>
							 <br><br><br>
						  <br><br><br>
						   <br><br><br>
							To be filled
							<%} %>
			
			<%}} %>
		
				</div>
						</div>
	

 <div class="carousel-item" >
				<div class="content-header row" id="header" >
				<div class="col-md-1"><img class="logo" style="width:45px;margin-top:3px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></div>
				<div class="col-md-1"></div>
				<div class="col-md-8" id="headername"><h3><b>World Scenario</b></h3></div>
				<div class="col-md-1"></div>
				<div class="col-md-1"><img class="logo" style="width:45px;margin-top:3px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></div>
				
				</div>

				<div class="content" align="center"
					style="height: 86vh !important; padding-top: 15px;">
	
							<%
							if (!DetailsList.isEmpty()) {

								for (Object[] obj : DetailsList) {
									if (obj[12] != null) {
							%>
							<%=(obj[12].toString())%>
							<%
							} else {
							%>
							 <br><br><br>
						  <br><br><br>
						   <br><br><br>
							To be filled
							<%
							}
							%>

							<%
							}
							}
							%>
					
				</div>
			</div>

					<div class="carousel-item" style="">
				<div class="content-header row" id="header" >
				<div class="col-md-1"><img class="logo" style="width:45px;margin-top:3px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></div>
				<div class="col-md-1"></div>
				<div class="col-md-8" id="headername"><h3><b>Objective</b></h3></div>
				<div class="col-md-1"></div>
				<div class="col-md-1"><img class="logo" style="width:45px;margin-top:3px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></div>
				
				</div>

				<div class="content" align="center"
					style="height: 86vh !important; padding-top: 15px;">
			
					
							<%
							if (!DetailsList.isEmpty()) {

								for (Object[] obj : DetailsList) {
									if (obj[1] != null) {
							%>
							<%=(obj[1].toString())%>
							<%
							} else {
							%>
							 <br><br><br>
						  <br><br><br>
						   <br><br><br>
							To be filled
							<%
							}
							%>

							<%
							}
							}
							%>
				
				</div>
			</div>

			<!-- Sixth Page  -->
			<div class="carousel-item " >
				<div class="content-header row" id="header" >
				<div class="col-md-1"><img class="logo" style="width:45px;margin-top:3px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></div>
				<div class="col-md-1"></div>
				<div class="col-md-8" id="headername"><h3><b>Scope</b></h3></div>
				<div class="col-md-1"></div>
				<div class="col-md-1"><img class="logo" style="width:45px;margin-top:3px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></div>
				
				</div>

				<div class="content" align="center"
					style="height: 86vh !important; padding-top: 15px;">
		
				
							<%
							if (!DetailsList.isEmpty()) {

								for (Object[] obj : DetailsList) {
									if (obj[2] != null) {
							%>
							<%=(obj[2].toString())%>
							<%
							} else {
							%>
							 <br><br><br>
						  <br><br><br>
						   <br><br><br>
							To be filled
							<%
							}
							%>

							<%
							}
							}
							%>
					
				</div>
			</div>


											<!-- Seventh Page  -->
				<div class="carousel-item " >
				<div class="content-header row" id="header" >
				<div class="col-md-1"><img class="logo" style="width:45px;margin-top:3px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></div>
				<div class="col-md-1"></div>
				<div class="col-md-8" id="headername"><h3><b>Deliverables</b></h3></div>
				<div class="col-md-1"></div>
				<div class="col-md-1"><img class="logo" style="width:45px;margin-top:3px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></div>
				
				</div>

				<div class="content" align="center"
					style="height: 86vh !important; padding-top: 15px;">
			
					
						 <%if(PfmsInitiationList!=null){%>
						 <%if(PfmsInitiationList[11]!=null){  %>
						  <br><br><br>
						  <br><br><br>
						   <br><br><br>
						 <span style="font-size: 20px;"><%=(PfmsInitiationList[11].toString())%></span>
						 <%} else{%>
						
						 <span style="font-size: 20px;"><i> To be filled</i></span>
						 <%} %>
						 <%} %>
					
				</div>
			</div>

		<!-- Eighth Page  -->
					<div class="carousel-item " >
				<div class="content-header row" id="header" >
				<div class="col-md-1"><img class="logo" style="width:45px;margin-top:3px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></div>
				<div class="col-md-1"></div>
				<div class="col-md-8" id="headername"><h3><b>Brief of Earlier Work Done </b></h3></div>
				<div class="col-md-1"></div>
				<div class="col-md-1"><img class="logo" style="width:45px;margin-top:3px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></div>
				
				</div>

				<div class="content" align="center"
					style="height: 86vh !important; padding-top: 15px;">
		
				
							<%
							if (!DetailsList.isEmpty()) {

								for (Object[] obj : DetailsList) {
									if (obj[4] != null) {
							%>
							<%=(obj[4].toString())%>
							<%
							} else {
							%>
							To be filled
							<%
							}
							%>

							<%
							}
							}
							%>
					
				</div>
			</div>
			
			<!-- Ninth Page  -->
					<div class="carousel-item " >
				<div class="content-header row" id="header" >
				<div class="col-md-1"><img class="logo" style="width:45px;margin-top:3px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></div>
				<div class="col-md-1"></div>
				<div class="col-md-8" id="headername"><h3><b>Competency Established </b></h3></div>
				<div class="col-md-1"></div>
				<div class="col-md-1"><img class="logo" style="width:45px;margin-top:3px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></div>
				
				</div>

				<div class="content" align="center"
					style="height: 86vh !important; padding-top: 15px;">
				
				
							<%
							if (!DetailsList.isEmpty()) {

								for (Object[] obj : DetailsList) {
									if (obj[5] != null) {
							%>
							<%=(obj[5].toString())%>
							<%
							} else {
							%>
							To be filled
							<%
							}
							%>

							<%
							}
							}
							%>
					
				</div>
			</div>
			<!--tenth page  -->
							<div class="carousel-item " >
				<div class="content-header row" id="header" >
				<div class="col-md-1"><img class="logo" style="width:45px;margin-top:3px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></div>
				<div class="col-md-1"></div>
				<div class="col-md-8" id="headername"><h3><b>Risk Mitigation </b></h3></div>
				<div class="col-md-1"></div>
				<div class="col-md-1"><img class="logo" style="width:45px;margin-top:3px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></div>
				
				</div>

				<div class="content" align="center"
					style="height: 86vh !important; padding-top: 15px;">
				
					
							<%
							if (!DetailsList.isEmpty()) {

								for (Object[] obj : DetailsList) {
									if (obj[8] != null) {
							%>
							<%=(obj[8].toString())%>
							<%
							} else {
							%>
							To be filled
							<%
							}
							%>

							<%
							}
							}
							%>
					
				</div>
			</div>

	<!-- eleventh page  -->
			<div class="carousel-item ">
				<div class="content-header row" id="header" >
				<div class="col-md-1"><img class="logo" style="width:45px;margin-top:3px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></div>
				<div class="col-md-1"></div>
				<div class="col-md-8" id="headername"><h3><b>Proposal </b></h3></div>
				<div class="col-md-1"></div>
				<div class="col-md-1"><img class="logo" style="width:45px;margin-top:3px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></div>
				
				</div>
			
				<div class="content" align="center"
					style="height: 86vh !important; padding-top: 15px;">
				
					
							<%
							if (!DetailsList.isEmpty()) {

								for (Object[] obj : DetailsList) {
									if (obj[9] != null) {
							%>
							<%=(obj[9].toString())%>
							<%
							} else {
							%>
							To be filled
							<%
							}
							%>

							<%
							}
							}
							%>
					
				</div>
			</div>
			<!-- cost -->
			
			
			<div class="carousel-item " >
				<div class="content-header row" id="header" >
				<div class="col-md-1"><img class="logo" style="width:45px;margin-top:3px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></div>
				<div class="col-md-1"></div>
				<div class="col-md-8" id=""><h3><b>Cost Breakup as per proposal </b></h3></div>
				<div class="col-md-1"></div>
				<div class="col-md-1"><img class="logo" style="width:45px;margin-top:3px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></div>
				
				</div>
			
				<div class="content" align="center"
					style="height: 86vh !important; padding-top: 15px;">

	
					
				
					<%
					if (!CostDetailsList.isEmpty()) {
					%>

					<table class="costtable"
						style="margin-right: 2px; margin-top: 5px; margin-bottom: 10px; margin-left: 2px; width:98%;; border-collapse: collapse;">
						<thead style="background:#629ed1; color:white;top:-18px; position: sticky; height:50px;">
						<tr style="font-size: 18px;">
								<th colspan="1" class="" style="width: 4%"><span>SN</span></th>
								<th colspan="4" class="" style="width: 30%"><span>Budget
										Item</span></th>
								<th colspan="2" class="" style="width: 40%"><span>Item</span></th>
								<th colspan="2" class="" style="width: 20%"><span>Cost
										(Lakhs)</span></th>
							</tr>
						
						</thead>
						<tbody style=""> 
							<%
							int count = 1;
							for (Object[] obj : CostDetailsList) {
							%>
							<tr>
								<td colspan="1" class="border_black weight_700 center"
									style="width: 7%;"><span><%=count%>.</span></td>
								<td colspan="4" class=" left" style="padding-left: 5px"><span><%=obj[0]!=null?(obj[0].toString()): " - " %> <%= "(" %> <%= obj[5]!=null?(obj[5].toString()): " - " %> <%= ")"%><br><%="(" %> <%= obj[1]!=null?(obj[1].toString()): " - " %> <%= ")"%></span></td>
								<td colspan="2" class=" left" style="padding-left: 5px"><span><%=obj[2]!=null?(obj[2].toString()): " - "%></span></td>
								<td colspan="2" class="border_black weight_700 right"
									style="padding-right: 5px;color:maroon;"><span>&#8377; <span><%=nfc.convert(Double.parseDouble(obj[3].toString()) / 100000)%></span></span></td>
							</tr>
						
						<%
						count++;
						}
						%>

						<%
						if (PfmsInitiationList != null) {
							Object[] obj = PfmsInitiationList;
						%>

						<tr>
							<td colspan="12" class="border_black weight_700 right"style="background: #C56824;color:white;" ><span>
									<%
									if (obj[6] != null && Double.parseDouble(obj[6].toString()) > 0) {
									%>Total
									Cost : &#8377; <span><b><%=nfc.convert(Double.parseDouble(obj[6].toString()) / 100000)%></b></span>
									<%
									} else {
									%> <%}%> Lakhs
							</span></td>
						</tr>
						<%}%>
						</tbody>
					</table>

					<%
					} else {
					%>
			
      <table class="costtable"style="margin-right: 2px; margin-top: 5px; margin-bottom: 10px; margin-left: 2px; width:80%;; border-collapse: collapse;">
						<thead style="font-size:18px;color:white;background:#629ed1; color:white;height:50px;">
						<tr style="">
								<th colspan="1" class="" style="width: 4%"><span>SN</span></th>
								<th colspan="4" class="" style="width: 30%"><span>Budget
										Item</span></th>
								<th colspan="2" class="" style="width: 40%"><span>Item</span></th>
								<th colspan="2" class="" style="width: 20%"><span>Cost
										(Lakhs)</span></th>
							</tr>
						
						</thead>
						<tbody style="height:50px;background:#C56824;color:white; font-size: 30px;">
						<tr><th colspan="8" class="border_black weight_400 center" ><span >No Data Available</span></th></tr> 
						</tbody >
					</table>

					<%}%>
			
				</div>
						</div>
					<!--schedule  -->
		   <div class="carousel-item " >
				<div class="content-header row" id="header" >
				<div class="col-md-1"><img class="logo" style="width:45px;margin-top:3px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></div>
				<div class="col-md-1"></div>
				<div class="col-md-8" id="headername"><h3><b>Project Schedule/Timelines </b></h3></div>
				<div class="col-md-1"></div>
				<div class="col-md-1"><img class="logo" style="width:45px;margin-top:3px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></div>
				
				</div>
			
				<div class="content" align="center"
					style="height: 86vh !important; ">
					<div style="width:90%;">
					<div style="overflow: auto; height:500px;">
					<%
					if (!ScheduleList.isEmpty()) {
					%>

					<table class="scheduletable"
						style="margin-top: 10px; margin-bottom: 10px; margin-left: 3px; width: 98%;">
						<thead style="background:#629ed1; color:white;top:-2px; position: sticky; height:50px;">
						<tr>
							<th colspan="1" 
								style="width: 7%"><span>SN</span></th>
							<th colspan="3" 
								style="width: 50%"><span>Milestone Activity</span></th>
							<td colspan="4" 
								style="font-weight: 600 ; text-align: center;"><span>Milestone TotalMonth</span></td>
							<th colspan="2" class="border_black weight_700 center grey"><span>Milestone
									Remarks</span></th>
						</tr>
						</thead>
						<tbody style="">
						<%
						int count = 1;
						for (Object[] obj : ScheduleList) {
						%>
						
						<tr>
							<td colspan="1" class=""
								style="width: 7%;text-align: center;"><span><%=count%>.</span></td>
							<td colspan="3" class="border_black weight_700 left"
								style="padding-left: 5px"><span><%=obj[1]%></span></td>
							<td colspan="4" class="border_black  center"
								style="font-weight: 600;color:maroon;">
								<%
								if (obj[5] != null && obj[2] != null) {
								%><%="T"%><sub><%=(obj[5].toString())%></sub><%="+"%><%=(obj[2].toString())%>
								<%
								} else {
								%> - <%
								}
								%>
							</td>
							<td colspan="2" class="border_black weight_700 center"><span><%=obj[4]!=null?(obj[4].toString()): " - "%></span></td>
						</tr>
						<%
						count++;
						}
						%>
						<%
						if (PfmsInitiationList != null) {
							Object[] obj = PfmsInitiationList;
						%>

						<tr>
							<td colspan="12" class="border_black right" style="background:#C56824;color:white;" ><span><b
									style=" margin-right:27%">
										<%
										if (obj[7] != null && Integer.parseInt(obj[7].toString()) > 0) {
										%>Total
										Duration :<%=(obj[7].toString()) + "Months"%></b></span>
								<%
								}
								%></td>
						</tr>
						<%
						}
						%>
						</tbody>
					</table>

					<%
					} else {
					%>
					  <table class=""style="margin-right: 2px; margin-top: 5px; margin-bottom: 10px; margin-left: 2px; width:80%;; border-collapse: collapse;">
						<thead style=" color:white;height:50px;">
							<tr>
							<th colspan="1" class="border_black weight_700 left grey"
								style="width: 9%"><span>SN</span></th>
							<th colspan="5" class="border_black weight_700 center grey"
								style="width: 80%"><span>Milestone Activity</span></th>
							<th colspan="2" class="border_black weight_700 center grey"><span>Time(Months)</span></th>
						</tr>
						
						</thead>
						<tbody style=";height:100px; font-size: 30px;">
						<tr><th colspan="8" class="border_black weight_400 center" ><span >No Data Available</span></th></tr> 
						</tbody >
					</table>

					<%} %>
					</div>
					</div>
				</div>
						</div>
						
						<div class="carousel-item " >
				<div class="content-header row" id="header" >
				<div class="col-md-1"><img class="logo" style="width:45px;margin-top:3px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></div>
				<div class="col-md-1"></div>
				<div class="col-md-8" id="headername"><h3><b>Realization Plan</b></h3></div>
				<div class="col-md-1"></div>
				<div class="col-md-1"><img class="logo" style="width:45px;margin-top:3px"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></div>
				
				</div>

				<div class="content" align="center"
					style="height: 86vh !important; padding-top: 15px;">
		
			
							<%
							if (!DetailsList.isEmpty()) {

								for (Object[] obj : DetailsList) {
									if (obj[10] != null) {
							%>
							<%=(obj[10].toString())%>
							<%
							} else {
							%>
							To be filled
							<%
							}
							%>

							<%
							}
							}
							%>
					
				</div>
			</div>
				<div class="carousel-item ">

				<div class="content" style="border: 0px solid black;padding-top: 50px; border-radius: 20px;position: relative;height: 93vh !important;">
					
					
					<div style=" position: absolute ;top: 40%;left: 34%;">
						<h1 style="font-size: 5rem; color: #145374 !important;">Thank You !</h1>
					</div>
					
				</div>

			</div>


	</div>
		<a class="carousel-control-prev" href="#presentation-slides" role="button" data-slide="prev" style="width: 0%; padding-left: 20px;"> <span aria-hidden="true">
			<i class="fa fa-chevron-left fa-2x" style="color: #000000" aria-hidden="true"></i></span> <span class="sr-only">Previous</span>
		</a> 
		<a class="carousel-control-next" href="#presentation-slides" role="button" data-slide="next" style="width: 0%; padding-right: 20px;"> <span aria-hidden="true">
			<i class="fa fa-chevron-right fa-2x" style="color: #000000" aria-hidden="true"></i></span> <span class="sr-only">Next</span>
		</a>
				<ol class="carousel-indicators">
			
			
			<li data-target="#presentation-slides" data-slide-to="0"  class="carousel-indicator active" data-toggle="tooltip" data-placement="top" title="Start"><b><i class="fa fa-home" aria-hidden="true"></i></b></li>
			<li data-target="#presentation-slides" data-slide-to="1"  class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="1.Brief of Project Proposal"><b>1</b></li>
			 <li data-target="#presentation-slides" data-slide-to="2"  class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="2. Need of Project"><b>2</b></li> 
			 <li data-target="#presentation-slides" data-slide-to="3"  class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="3. World Scenario"><b>3</b></li>
			 <li data-target="#presentation-slides" data-slide-to="4"  class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="4. Objective"><b>4</b></li>
			<li data-target="#presentation-slides" data-slide-to="5"  class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="5.Scope"><b>5</b></li>
			<li data-target="#presentation-slides" data-slide-to="6"  class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="6.Deliverables"><b>6</b></li>
			<li data-target="#presentation-slides" data-slide-to="7"  class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="7.Brief of Earlier work Done"><b>7</b></li>
			<li data-target="#presentation-slides" data-slide-to="8"  class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="8.Competency Established"><b>8</b></li>
			<li data-target="#presentation-slides" data-slide-to="9"  class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="9.Risk Mitigation"><b>9</b></li>
			<li data-target="#presentation-slides" data-slide-to="10"  class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="10.Proposal"><b>10</b></li>
			<li data-target="#presentation-slides" data-slide-to="11"  class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="11.Cost break up"><b>11</b></li>
			<li data-target="#presentation-slides" data-slide-to="12"  class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="12.Schedule"><b>12</b></li>
			<li data-target="#presentation-slides" data-slide-to="13"  class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="13.Realization Plan"><b>13</b></li>
			<li data-target="#presentation-slides" data-slide-to="14"  class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="Thank you"><b>End</b></li>
			
			<li data-slide-to="13" style="background-color:  #000000;width: 35px;margin-left: 20px;" class="carousel-indicator content_full_screen" data-toggle="tooltip" data-placement="top" title="Full Screen Mode"><b><i class="fa fa-expand fa-lg" aria-hidden="true"></i></b></li>
			<li data-slide-to="13" style="background-color:  #000000;width: 35px;margin-left: 20px;" class="carousel-indicator content_reg_screen" data-toggle="tooltip" data-placement="top" title="Exit Full Screen Mode"><b><i class="fa fa-compress fa-lg" aria-hidden="true"></i></b></li>
			</ol>
	</div>

<script>
$('img[data-enlargable]').addClass('img-enlargable').click(function(){
    var src = $(this).attr('src');
    $('<div>').css({
        background: 'RGBA(0,0,0,.5) url('+src+') no-repeat center',
        backgroundSize: 'contain',
        width:'100%', height:'100%',
        position:'fixed',
        zIndex:'10000',
        top:'0', left:'0',
        cursor: 'zoom-out'
    }).click(function(){
        $(this).remove();
    }).appendTo('body');
});

$('.content_reg_screen').hide();
$('.content_full_screen, .content_reg_screen').on('click', function(e){
	  
	  if (document.fullscreenElement) {
	    	document.exitFullscreen();
	  } else {
		  $('.slides-container').get(0).requestFullscreen();
	  }
	});

$('.content_full_screen').on('click', function(e){ contentFullScreen() });

$('.content_reg_screen').on('click', function(e){ contentRegScreen() });

function contentFullScreen()
{
	$('.content_full_screen').hide();
	$('.content_reg_screen').show();
	openFullscreen();
}

function contentRegScreen()
{
	$('.content_reg_screen').hide();
	$('.content_full_screen').show();
	closeFullscreen();
}
$(function () {
	  $('[data-toggle="tooltip"]').tooltip()
	})


/* Get the documentElement (<html>) to display the page in fullscreen */
var elem = document.documentElement;

/* View in fullscreen */
function openFullscreen() {
  if (elem.requestFullscreen) {
    elem.requestFullscreen();
  } else if (elem.webkitRequestFullscreen) { /* Safari */
    elem.webkitRequestFullscreen();
  } else if (elem.msRequestFullscreen) { /* IE11 */
    elem.msRequestFullscreen();
  }
}

/* Close fullscreen */
function closeFullscreen() {
  if (document.exitFullscreen) {
    document.exitFullscreen();
  } else if (document.webkitExitFullscreen) { /* Safari */
    document.webkitExitFullscreen();
  } else if (document.msExitFullscreen) { /* IE11 */
    document.msExitFullscreen();
  }
}
$('.carousel').carousel({
	  interval: false,
	  keyboard: true,
	})
</script>
</body>
</html>