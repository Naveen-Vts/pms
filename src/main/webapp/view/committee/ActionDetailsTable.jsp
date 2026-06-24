<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="java.time.LocalDate"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="com.vts.pfms.master.dto.ProjectFinancialDetails"%>
<%@page import="java.text.Format"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@page import="com.vts.pfms.model.TotalDemand"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="java.util.*"%>
<!DOCTYPE html>
<html>
<head>

<%	

List<ProjectFinancialDetails> projectFinancialDetails =(List<ProjectFinancialDetails>)request.getAttribute("financialDetails");
List<Object[]> envisagedDemandlist = (List<Object[]> )request.getAttribute("envisagedDemandlist");
	Object[] committeescheduleeditdata = (Object[]) request.getAttribute("committeescheduleeditdata");	
	LinkedHashMap< String, ArrayList<Object[]>> actionlist = (LinkedHashMap< String, ArrayList<Object[]>>) request.getAttribute("tableactionlist");
	Object[] projectdetails=(Object[])request.getAttribute("projectdetails");
	Object[] labdetails = (Object[]) request.getAttribute("labdetails");
	List<Object[]> procurementOnDemand = (List<Object[]>)request.getAttribute("procurementOnDemand");
	Object[] divisiondetails=(Object[])request.getAttribute("divisiondetails");
	String projectid= committeescheduleeditdata[9].toString();
	String divisionid= committeescheduleeditdata[16].toString();
	Object[] projectdatadetails = (Object[]) request.getAttribute("projectdatadetails");
	FormatConverter fc=new FormatConverter(); 
	SimpleDateFormat sdf=fc.getRegularDateFormat();
	List<Object[]> procurementOnSanction = (List<Object[]>)request.getAttribute("procurementOnSanction");
	SimpleDateFormat sdf1=fc.getSqlDateFormat(); int addcount=0; 
	List<Object[]> MilestoneDetails6 = (List<Object[]>)request.getAttribute("milestonedatalevel6");
	String levelid= (String) request.getAttribute("levelid");
	Map<Integer,String> treeMapLevOne =(Map<Integer,String>)request.getAttribute("treeMapLevOne");
	Map<Integer,String> treeMapLevTwo =(Map<Integer,String>)request.getAttribute("treeMapLevTwo");
	List<TotalDemand> totalprocurementdetails = (List<TotalDemand>)request.getAttribute("TotalProcurementDetails");
	DecimalFormat df=new DecimalFormat("####################.##");
	Format format = com.ibm.icu.text.NumberFormat.getCurrencyInstance(new Locale("en", "in"));
	List<Object[]> ActionPlanSixMonths = (List<Object[]>)request.getAttribute("ActionPlanSixMonths");
	String flagforView=(String)request.getAttribute("flagforView");
	String labcode= (String) session.getAttribute("labcode");
	List<List<Object[]>> overallfinance = (List<List<Object[]>>) request.getAttribute("overallfinance");
	List<String> projectidlist = (List<String>)request.getAttribute("projectidlist");
	List<Object[]> ProjectDetail = (List<Object[]>)request.getAttribute("ProjectDetails");
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
              
 
@page {             
        <% if(labcode.equalsIgnoreCase("ADE")){%> size:790px 1120px ;  <%}else{%>size: 1120px 790px;   <%}%>
          margin-top: 49px;
          margin-left: 39px;
          margin-right: 39px;
          margin-buttom: 49px; 	
          border: 1px solid black;    
         /* @bottom-right {          		
             content: "Page-AI " counter(page) " of " counter(pages);
             margin-bottom: 30px;
             margin-right: 10px;
             font-size: 13px;
          } */
          @top-right {
          		<%if( Long.parseLong(projectid)>0){%>
             content: "<%=projectdetails[1]!=null?StringEscapeUtils.escapeHtml4(projectdetails[1].toString()): " - "%>";
             <%}else if(Long.parseLong(divisionid)>0){%>
               	content: "Division:<%=divisiondetails[1]!=null?StringEscapeUtils.escapeHtml4(divisiondetails[1].toString()): " - "%>";
             <%}else {%>
             	content: "<%=labdetails[1]!=null?StringEscapeUtils.escapeHtml4(labdetails[1].toString()): " - "%>";
             <%}%>
             margin-top: 30px;
             margin-right: 10px;
             font-size: 13px;
          }
          @top-left {
          	margin-top: 30px;
            margin-left: 10px;
            content: "<%=committeescheduleeditdata[11]!=null?StringEscapeUtils.escapeHtml4(committeescheduleeditdata[11].toString()): " - " %>";
            font-size: 13px;
          }            
          
          @top-center { 
          margin-top: 30px;
          content: "<%=committeescheduleeditdata[15]!=null?StringEscapeUtils.escapeHtml4(committeescheduleeditdata[15].toString()): " - "%>"; 
           font-size: 13px;
          
          }
         
          @bottom-center { 
	          margin-bottom: 30px;
	          content: "<%=committeescheduleeditdata[15]!=null?StringEscapeUtils.escapeHtml4(committeescheduleeditdata[15].toString()): " - "%>"; 
          
          }
          
           @bottom-left { 
             font-size: 13px;
	          margin-bottom: 30px;
	          content: "<%=LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))%>"; 
	          
          
          }   
          
      <%--     @bottom-left {          		
 			 content : "The info in this Doc. is proprietary of  <%if( Long.parseLong(projectid)>0){%> <%=projectdetails[1]%> <%}else if(Long.parseLong(divisionid)>0){%> Division:<%=divisiondetails[1]%> <%}else {%> <%=labdetails[1]%> <%}%> 	/DRDO , MOD Govt. of India. Unauthorized possession may be liable for prosecution.";
 			 margin-bottom: 30px;
             font-size: 9.5px;
          }  --%>
 }
 
 

 .sth
 {
 	font-size: 16px;
 	border: 1px solid black;
 }
 
 .std
 {
 	text-align: left;
 	border: 1px solid black;
 	padding :3px;
 }
 
</style>
<meta charset="ISO-8859-1">
<title><%=committeescheduleeditdata[8]!=null?StringEscapeUtils.escapeHtml4(committeescheduleeditdata[1].toString()): " - "%> Minutes View</title>
</head>
<body>
	<%if(flagforView==null) { %>
		<div align="center" style="text-decoration: underline">Annexure - A</div>	
	<table style="width: 950px; margin-top:5px;font-size: 16px; border-collapse: collapse; margin-left: 8px;">
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;">Status of major sub system and sub projects</th>
						</tr>
					</table>	
					<br>
							<table style=" margin-left: 8px; <% if(labcode.equalsIgnoreCase("ADE")){%>width:690px;<%}else{%> width:1020px<%}%> ; margin-top:5px;font-size: 16px; border-collapse: collapse;border: 1px solid black" >
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
								<tr><td class="" colspan="8" style="border: 1px solid black; text-align: center;" > No SubSystems</td></tr>
								<%}}else{ %>
									<tr><td class="" colspan="8" style="border: 1px solid black; text-align: center;" > No SubSystems</td></tr>
								<%} %>

						</tbody>
					</table>
	
	
	
	 <h1 class="break"></h1> 
	
			<div  align="center" style="text-decoration: underline">Annexure - B</div>		
				<table style=" margin-left: 8px; width: 950px; margin-top:5px;font-size: 16px; border-collapse: collapse;" >
						<tr>
							<th colspan="10" style="text-align: left; font-weight: 700;">Details of procurements items envisaged in the projects along with status</th>
						</tr>
					</table>	
							<table style=" margin-left: 8px; <% if(labcode.equalsIgnoreCase("ADE")){%>width:690px;<%}else{%> width:1020px<%}%> ; margin-top:5px;font-size: 16px; border-collapse: collapse;border: 1px solid black" >
										<thead>
										<tr>
											<th colspan="11" style="text-align: right;"> <span class="currency" >(In &#8377; Lakhs)</span></th>
										</tr>
										 <tr>
										 	<th colspan="11" class="std">Demand Details ( > &#8377; <% if (projectdatadetails != null && projectdatadetails[13] != null) { %>
													<%=StringEscapeUtils.escapeHtml4(projectdatadetails[13].toString()).replaceAll("\\.\\d+$", "")%> ) <% } else { %> - )<% } %>
												
											</th>
										</tr>
										</thead>
										
										<tr>
											<th class="std" style="border: 1px solid black;width: 30px !important;">SN</th>
											<th class="std" style="border: 1px solid black;max-width:90px;">Demand No <br> Demand Date</th>
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
												<td class="std"  style=" border: 1px solid black;"><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "%><br><%=obj[3]!=null?sdf.format(sdf1.parse(StringEscapeUtils.escapeHtml4(obj[3].toString()))):" - "%></td>
<%-- 												<td class="std"  style=" border: 1px solid black;"><%=sdf.format(sdf1.parse(obj[3].toString()))%></td>
 --%>												<td class="std" colspan="4" ><%=obj[8]!=null?StringEscapeUtils.escapeHtml4(obj[8].toString()): " - "%></td>
												<td class="std" style=" text-align:right;"> <%=obj[5]!=null?format.format(new BigDecimal(StringEscapeUtils.escapeHtml4(obj[5].toString()))).substring(1):" - "%></td>
												<td class="std"  style=" border: 1px solid black;"> <%=obj[10]!=null?StringEscapeUtils.escapeHtml4(obj[10].toString()): " - "%> </td>
												<td class="std" colspan="3" style=" border: 1px solid black;"><%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - "%> </td>		
											</tr>		
											<%
											estcost += Double.parseDouble(obj[5].toString());
										    }%>
										    
										    <tr>
										    	<td class="std" colspan="8" style="text-align: right;"><b>Total</b></td>
										    	<td class="std" style="text-align: right;"><b><%=estcost!=null?df.format(estcost):" - "%></b></td>
										    	
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
											
									<!-- ********************************Future Demand End *********************************** -->
											
											 <tr >
											 
												<th  class="std"  colspan="8">Orders Placed ( > &#8377; <% if (projectdatadetails != null && projectdatadetails[13] != null) { %>
													<%=StringEscapeUtils.escapeHtml4(projectdatadetails[13].toString()).replaceAll("\\.\\d+$", "")%> ) <% } else { %> - )<% } %>
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
											<%} %>
									</table> 
							<table style=" margin-left: 8px; <% if(labcode.equalsIgnoreCase("ADE")){%>width:690px;<%}else{%> width:1020px<%}%> ; margin-top:5px;font-size: 16px; border-collapse: collapse;border: 1px solid black" >
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
						<h1 class="break"></h1>
						
											<div align="center" style="text-decoration: underline">Annexure - C</div>		
					<table style="  <% if(labcode.equalsIgnoreCase("ADE")){%>width:690px;<%}else{%> width:1020px<%}%> ; margin-top:5px;font-size: 16px;margin-left:8px;margin-bottom:8px; border-collapse: collapse;" >
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;"><br>Financial Status presented during the review</th>
						</tr>
						<tr >
							<td colspan="8" align="right">(Amount in Crores)</td>
						</tr>
					</table>										
					<% 
					    double totSanctionCost=0,totReSanctionCost=0,totFESanctionCost=0;
						double totExpenditure=0,totREExpenditure=0,totFEExpenditure=0;
						double totCommitment=0,totRECommitment=0,totFECommitment=0,totalDIPL=0,totalREDIPL=0,totalFEDIPL=0;
						double totBalance=0,totReBalance=0,totFeBalance=0,btotalRe=0,btotalFe=0;
						
						%>
					<%if(Long.parseLong(projectid) >0 ) { %>
							
							<table style=" <% if(labcode.equalsIgnoreCase("ADE")){%>width:690px;<%}else{%> width:1020px<%}%> ; margin-top:5px;font-size: 16px; border-collapse: collapse;border: 1px solid black;margin-left:8px" >
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
									     <tbody id="tbody<%=ProjectDetail!=null ? ProjectDetail.get(z)[0].toString() : "1"%>">
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
						
						<table style=" <% if(labcode.equalsIgnoreCase("ADE")){%>width:690px;<%}else{%> width:1020px<%}%> ;font-size: 16px; border-collapse: collapse;margin-left:8px;" >
						<tr >
							<td colspan="8" style="border: 1px solid black;font-weight: bold"  align="center">No Data Available</td>
						</tr>
					</table>	
							  
					<% } %>
						
						<h1 class="break"></h1>
											<div align="center" style="text-decoration: underline">Annexure - D</div>		
						<table style=" <% if(labcode.equalsIgnoreCase("ADE")){%>width:690px;<%}else{%> width:1020px<%}%> ; margin-top:5px;font-size: 16px; border-collapse: collapse; margin-left:8px">
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;">Major milestones to be completed in next 06 months along with the financial outlay.</th>
						</tr>
					</table>
					<table style="margin-top: 5px; margin-bottom: 0px;  <% if(labcode.equalsIgnoreCase("ADE")){%>width:690px;<%}else{%> width:1020px<%}%> ; font-size: 16px; border-collapse: collapse;border: 1px solid black;margin-left:8px" >
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
					  
					<%if(actionlist.size()>=0 && !labcode.equalsIgnoreCase("ADE")){ %>
						<h1 class="break"></h1>	
					<div align="center">
				 	<div style="text-align: center ; padding-right: 15px; " ><h3 style="text-decoration: underline;">Annexure - AI</h3></div> 
						<div style="text-align: center;  " class="lastpage" id="lastpage"><h2>ACTION ITEM DETAILS</h2></div>
					
						<table style=" <% if(labcode.equalsIgnoreCase("ADE")){%>width:690px;<%}else{%> width:1020px<%}%> ; margin-left:10px; font-size: 16px; border-collapse: collapse ;border: 1px solid black ;margin-right: 10px;margin-top:10px;">
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
					<%}%>
					
					
					
					
					<%} else{%>	
					
				<%if(actionlist.size()>=0){ %>
							
					<div align="center">
				 	<div style="text-align: center ; padding-right: 15px; " ><h3 style="text-decoration: underline;">Annexure - AI</h3></div> 
						<div style="text-align: center;  " class="lastpage" id="lastpage"><h2>ACTION ITEM DETAILS</h2></div>
					
						<table style=" <% if(labcode.equalsIgnoreCase("ADE")){%>width:690px;<%}else{%> width:1020px<%}%> ; margin-left:10px; font-size: 16px; border-collapse: collapse ;border: 1px solid black ;margin-right: 10px;margin-top:10px;">
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
				<%}} %>

</body>
</html>