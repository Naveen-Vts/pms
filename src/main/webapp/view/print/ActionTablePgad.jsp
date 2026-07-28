<%@page import="com.vts.pfms.model.BriefingFinance"%>
<%@page import="java.time.temporal.ChronoUnit"%>
<%@page import="com.vts.pfms.committee.model.Committee"%>
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
<%@page import="com.vts.pfms.model.TotalDemand"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="java.util.*"%>
<!DOCTYPE html>
<html>
<head>
<style type="text/css">


p{
  text-align: justify;
  text-justify: inter-word;
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
@page {
    size: 1120px 790px;
    margin-top: 20px;
    margin-right: 20px;
    margin-bottom: 20px;
    margin-left: 20px;
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
	/* max-width: 650px!important; */
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
<meta charset="ISO-8859-1">
<title>Briefing Paper</title>

</head>
<%
	Committee committee=(Committee)request.getAttribute("committeeData");
	DecimalFormat df=new DecimalFormat("####################.##");
	FormatConverter fc=new FormatConverter(); 
	SimpleDateFormat sdf=fc.getRegularDateFormat();
	SimpleDateFormat sdf1=fc.getSqlDateFormat(); int addcount=0; 
	NFormatConvertion nfc=new NFormatConvertion();
	Format format = com.ibm.icu.text.NumberFormat.getCurrencyInstance(new Locale("en", "in"));
	List<List<ProjectFinancialDetails>> projectFinancialDetails =(List<List<ProjectFinancialDetails>>)request.getAttribute("financialDetails");
	List<String> projectidlist = (List<String>)request.getAttribute("projectidlist");
	String IsIbasConnected=(String)request.getAttribute("IsIbasConnected");
	List<Object[]> ProjectDetail=(List<Object[]>)request.getAttribute("ProjectDetails");
	List<List<Object[]>> overallfinance = (List<List<Object[]>>)request.getAttribute("overallfinance");//b
	String CommitteeCode = committee.getCommitteeShortName().trim();
	List<List<Object[]>> lastpmrcminsactlist = (List<List<Object[]>>) request.getAttribute("lastpmrcminsactlist");
	Map<Integer,String> committeeWiseMap=(Map<Integer,String>)request.getAttribute("committeeWiseMap");
	Committee committeeData = (Committee) request.getAttribute("committeeData");
	LocalDate before6months = LocalDate.now().minusDays(committeeData.getPeriodicDuration());
	SimpleDateFormat inputFormat = new SimpleDateFormat("ddMMMyyyy", Locale.ENGLISH);
	SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");	
	String text=(String)request.getAttribute("text");
	List<List<Object[]>> lastpmrcactions = (List<List<Object[]>>)request.getAttribute("lastpmrcactions");
	Map<String, List<Object[]>> reviewMeetingListMap = (Map<String, List<Object[]>>) request.getAttribute("reviewMeetingListMap");
	List<Object[]> otherMeetingList = (List<Object[]>)request.getAttribute("otherMeetingList");

	List<BriefingFinance> briefingFinanceDetials = (List<BriefingFinance>)request.getAttribute("briefingFinanceDetials");

%>
<body>
<%-- <div align="center" style="margin-top:20px;font-weight: bold;font-size: 18px;">(Annexure - A)</div>				
	<% for(int z=0 ; z<1;z++) {   %>
		<div align="left" style="margin-left: 10px;"><b class="sub-title"> Particulars of Meeting</b></div><br>
		<div align="left" style="margin-left: 15px;"><b class="mainsubtitle">(a) <%if(CommitteeCode.equalsIgnoreCase("PMRC")){ %>
															   						Approval 
															   						<%}else { %>
															   						Ratification
															   						<%} %>  of <b>recommendations</b> of last <%=CommitteeCode!=null?(CommitteeCode).toUpperCase(): " - "%> Meeting (if any)</b></div>
		
		
			<table class="subtables" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px; border-collapse:collapse;" >
				<thead>
					<tr>
						<td colspan="6" style="border: 0">
							
						</td>									
					</tr>
										
					<tr>
						<th  style="width: 15px !important;text-align: center;">SN</th>
						<th  style="width: 100px !important;"> ID</th>
						<th  style="width: 415px !important;">Recommendation Point</th>
						<th  style="width: 100px !important;"> PDC</th>
						<th  style="width: 250px !important;"> Responsibility</th>
				<!-- 		<th  style="width: 80px !important;">Status</th> -->
						<th  style="width: 250px !important; ">Remarks</th>
					</tr>
				</thead>
				<tbody>
					<%if(lastpmrcminsactlist.get(z).size()==0){ %>
						<tr><td colspan="6" style="text-align: center;" > Nil</td></tr>
					<%}
						else if(lastpmrcminsactlist.get(z).size()>0)
							{int i=1;String key2="";
								for(Object[] obj:lastpmrcminsactlist.get(z)){
									if(obj[3].toString().equalsIgnoreCase("R") && (obj[10]==null || !obj[10].toString().equals("C") || (obj[10].toString().equals("C") && obj[14]!=null && before6months.isBefore(LocalDate.parse(obj[14].toString()) ) ))      ){ %>
						<tr>
							<td  style="text-align: center;"><%=i %></td>
							<td>
							
									<%if(obj[21]!=null && Long.parseLong(obj[21].toString())>0){ %>
								
									
									<span style="font-size: 0.85rem;;">	<!-- <i class="fa fa-info-circle fa-lg " style="color: #145374" aria-hidden="true"></i> -->
								<%for (Map.Entry<Integer, String> entry : committeeWiseMap.entrySet()) {
									Date date = inputFormat.parse(obj[5].toString().split("/")[3]);
									 String formattedDate = outputFormat.format(date);
									 if(entry.getValue().equalsIgnoreCase(formattedDate)){
										 key2=entry.getKey().toString();
									 } }%>
								<%=committee.getCommitteeShortName().trim().toUpperCase()+"-"+key2+"/"+obj[5].toString().split("/")[4] %>
								
								</span>	
									
									
								<%}%>
							
							
							
							</td>
							<td  style="text-align: justify; "><%=obj[2]!=null?obj[2].toString(): " - " %></td>
						
							<td style="text-align: center;">
								<%if(obj[8]!= null && !LocalDate.parse(obj[8].toString()).equals(LocalDate.parse(obj[7].toString())) ){ %><span style="color:black;font-weight: bold;"><%=sdf.format(sdf1.parse(obj[8].toString()))%></span><br><%} %>	
								<%if(obj[7]!= null && !LocalDate.parse(obj[7].toString()).equals(LocalDate.parse(obj[6].toString())) ){ %><span style="color:black;font-weight: bold;"><%=sdf.format(sdf1.parse(obj[7].toString()))%></span><br><%} %>
								<%if(obj[6]!= null){ %><span><%=sdf.format(sdf1.parse(obj[6].toString()))%></span><br><%} %>
								</td>
							<td>
								<%if(obj[4]!= null){ %>  
									<%=obj[12]!=null?(obj[12].toString()): " - " %>, <%=obj[13] %>
								<%}else { %> <!-- <span class="notassign">NA</span>  --> <span class="">Not Assigned</span> <%} %> 
							</td>
						
				<td ><%if(obj[19]!=null){%><%=(obj[19].toString()) %><%} %></td>
					</tr>		
					<%i++;}
						}%>
					<%if(i==1){ %> <tr><td colspan="6" style="text-align: center;" > Nil</td></tr>	<%} %>
											
					<%} %>
				</tbody>
										
			</table>
			<%} %>
			
		<% for(int z=0 ; z<1;z++) {   %>
		<h1 class="break"></h1>
				 	<div align="left" style="margin-left: 15px;"><b class="mainsubtitle">(b) Last <%=CommitteeCode!=null?(CommitteeCode).toUpperCase(): " - "%>
														   						Meeting action points with Probable Date of completion (PDC), Actual Date of Completion (ADC) and status.</b>
					</div>
   							
					<table class="subtables" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px;   border-collapse:collapse;" >
						<thead>
							<tr>
								<td colspan="7" style="border: 0">
								</td>									
							</tr>
										
							<tr>
								<th  style="width: 15px !important;text-align: center;  ">SN</th>
								<th style="width: 60px;">ID</th>
								<th  style="width: 400px; ">Action Point</th>
								<th  style="width: 120px; ">ADC<br>PDC</th>
						<!-- 		<th  style="width: 80px; "> ADC</th> -->
								<th  style="width: 210px; "> Responsibility</th>
								<th  style="width: 80px; ">Status</th>
								<th  style="width: 205px; ">Remarks</th>			
							</tr>
						</thead>
							
						<tbody>		
							<%if(lastpmrcactions.get(z).size()==0){ %>
								<tr><td colspan="7"  style="text-align: center;" > Nil</td></tr>
								<%}
								else if(lastpmrcactions.size()>0)
								{Map<String,List<Object[]>> list = lastpmrcactions.get(z)!=null ? lastpmrcactions.get(z).stream()
										.collect(Collectors.groupingBy(array -> array[0].toString(), LinkedHashMap::new,Collectors.toList())) : new HashMap<>();
							int i = 1;String key="";
							for(Map.Entry<String, List<Object[]>> map : list.entrySet()){
								int j=1;
								List<Object[]> values = map.getValue();
								int rowSpan = values.size();
							for (Object[] obj : values) { %>
								<tr>
									<td  style="text-align: center;"><%=i %></td>
									<td <%if(text!=null && text.equalsIgnoreCase("p")) {%>style="font-weight: bold;"<%} %>>	
								<!--newly added on 13th sept  -->	
								<span style="font-size: 12px;"><%if(obj[17]!=null && Long.parseLong(obj[17].toString())>0){ %>
								<%for (Map.Entry<Integer, String> entry : committeeWiseMap.entrySet()) {
									Date date = inputFormat.parse(obj[1].toString().split("/")[3]);
									 String formattedDate = outputFormat.format(date);
									 if(entry.getValue().equalsIgnoreCase(formattedDate)){
										 key=entry.getKey().toString();
									 } }%>
								
								<%=committee.getCommitteeShortName().trim().toUpperCase()+"-"+key+"/"+obj[1].toString().split("/")[4] %>
								<%}%> </span>
								</td>
									<%if(j++==1){ %><td rowspan="<%=rowSpan%>" class="text-justify"> <%=obj[2].toString()%> </td> <%} %>
					
																	<td style="text-align: center;">
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
									<span <%if(endPdc.isAfter(today) || endPdc.isEqual(today)) {%>style="color:black;font-weight: bold;" <%} else{%> style="color:maroon ;font-weight:bold;" <%} %>>
									<%= sdf.format(sdf1.parse(obj[4].toString()))%>
									</span>
										<%if(!pdcorg.equals(endPdc)) { %>
									<br>
									<span <%if(pdcorg.isAfter(today) || pdcorg.isEqual(today)) {%>style="color:black;font-weight: bold;" <%} else{%> style="color:maroon ;font-weight:bold;" <%} %>>
									<%= sdf.format(sdf1.parse(obj[3].toString()))%> 
									</span>	
									<%} %>
								</td>
									<td > <%=obj[11]!=null?(obj[11].toString()): " - " %>, <%=obj[12] %> </td>
									<td  class="text-center" > 
										<% if(lastdate!=null && actionstatus.equalsIgnoreCase("C") ){ %>
										<%if(actionstatus.equals("C") && (pdcorg.isAfter(lastdate) || pdcorg.equals(lastdate))){%>
										<span class="completed">CO</span>
										<%}else if(actionstatus.equals("C") && pdcorg.isBefore(lastdate)){ %>	
										<span class="completeddelay">CD (<%= ChronoUnit.DAYS.between(pdcorg, lastdate) %>) </span>
										<%} %>	
										<%}else{ %>
										<%if(actionstatus.equals("F")  && (pdcorg.isAfter(lastdate) || pdcorg.isEqual(lastdate) )){ %>
										<span class="ongoing">RC</span>												
										<%}else if(actionstatus.equals("F")  && pdcorg.isBefore(lastdate)) { %>
										<span class="delay">FD</span>
										<%}else if(actionstatus.equals("A") && progress==0){  %>
										<span class="assigned">AA <%if(pdcorg.isBefore(today)){ %> (<%= ChronoUnit.DAYS.between(pdcorg, today)  %>) <%} %></span>
									    <%} else if(pdcorg.isAfter(today) || pdcorg.isEqual(today)){  %>
										<span class="ongoing">OG</span>
										<%}else if(pdcorg.isBefore(today)){  %>
										<span class="delay">DO (<%= ChronoUnit.DAYS.between(pdcorg, today)  %>)  </span>
										<%} %>					
										<%} %>
										
						
									</td>
									<td  style="text-align: justify ;"><%if(obj[16]!=null){%><%=(obj[16].toString()) %><%} %></td>			
								</tr>			
							<%i++;
							}}} %>
							</tbody>
									
						</table> 
								
					<%} %>	
					
						<% for(int z=0 ; z<1;z++) {   %>
					<h1 class="break"></h1>
						<div align="left" style="margin-left: 15px;margin-top:20px;"><b class="mainsubtitle">(c) Details of Technical/ User Reviews (if any).</b></div>
							<div >
							<%for(Map.Entry<String, List<Object[]>> entry : reviewMeetingListMap.entrySet()) { 
								if(entry.getValue().size()>0) {%>
									<div >
										<table class="subtables" style="align: left; margin-top: 10px; margin-left: 25px; max-width: 300px; border-collapse: collapse; float: left;">
											<thead>
												<tr>
													<th  style="width: 140px; ">Committee</th>
													<th  style="width: 140px; "> Date Held</th>
												</tr>
											</thead>
											<tbody>
												<%int i=0;
												for(Object[] obj : entry.getValue()){ %>
													<tr>
														<td >
															<%=entry.getKey()!=null?(entry.getKey()): " - "%> #<%=++i %>
														</td>												
														<td style="text-align: center; " ><%= fc.sdfTordf(obj[3].toString())%></td>
													</tr>				
												<%} %>
											</tbody>
										</table>
									</div>
								<%} %>
							<%} %>
							</div>	
						<div>
						<%if(otherMeetingList!=null && otherMeetingList.size()>0) { %>
						<div align="left"><b><%="Other Meetings" %></b></div>
						<div align="left"><table class="subtables" style="align: left; margin-top: 10px; margin-left: 25px; max-width: 350px; border-collapse: collapse;">
						<thead><tr> <th style="width: 140px; ">Committee</th> <th  style="width: 140px; "> Date Held</th></tr></thead>
						<%for(Object[]obj:otherMeetingList) {%>
						<tbody><tr><td><%=obj[3]!=null?(obj[3].toString()): " - "%></td>												
								<td  style="text-align: center; " ><%= sdf.format(sdf1.parse(obj[1].toString()))%></td>
								</tr>
									</tbody><%}%></table></div> <%} %>		</div>
			<%} %>


<h1 class="break"></h1>
 --%>
					<div align="center" style="margin-top:20px;font-weight: bold;font-size: 18px;">(Annexure - A)</div>				

		<% char fch='a'; for(int z=0 ; z<1;z++) {   
			
			%>
					<!-- ----------------------------------------------8. Overall financial Status------------------------------------------------- -->
		 
   					<div align="left" style="margin-left: 10px;"><b class="sub-title"><%if(projectidlist.size()>1) {%> (<%=(fch++) %>). <%} %> Overall Financial Status </b></div><div align="right"><b><span class="currency" >(&#8377; <span>Crore</span>)</span></b></div>
   					
   					<div class="content">
   								<%for(int i=0;i<projectidlist.size();i++){ 
		                		BigDecimal allotment = BigDecimal.ZERO, sanction = BigDecimal.ZERO,
		                				balance = BigDecimal.ZERO, oustanding = BigDecimal.ZERO, exp = BigDecimal.ZERO, inr = BigDecimal.ZERO,fe = BigDecimal.ZERO ;
				                int sn = 1;
						  	%>
						  	<%if(ProjectDetail.size()>1){ %>
								<div>
									<b>Project : <%=ProjectDetail.get(i)[1] %> 	<%if(i!=0){ %>(SUB)<%} %>	</b>
								</div>	
							<%} %>
							<br>				 
							  	<table  class="subtables width1100" style="margin-bottom:20px!important;border-collapse: collapse;">
							  		<thead>
								  		<tr>
								  			<th>Category No</th>
								  			<th>Category Name</th>
								  			<th>Allotment</th>
								  			<th>Sanction</th>
								  			<th>Balance</th>
								  			<th>SO Value</th>
								  			<th>Expenditure</th>
								  			<th>INR</th>
								  			<th>FE</th>
								  		</tr>
							  		</thead>
							  		<tbody>
							  			<%if(briefingFinanceDetials!=null && !briefingFinanceDetials.isEmpty()){
							  				for(BriefingFinance finance : briefingFinanceDetials){ 
							  					allotment = allotment.add(finance.getAllotment());
							  					sanction = sanction.add(finance.getSanction());
							  					balance = balance.add(finance.getBalance());
							  					oustanding = oustanding.add(finance.getOutStanding());
							  					exp = exp.add(finance.getExpenditure());
							  					inr = inr.add(finance.getInr());
							  					fe = fe.add(finance.getFe());
							  				%>
							  				<tr>
							  					<td class="text-center" ><%=sn++ %></td>
							  					<td class="text-start" ><%=finance.getCategoryName() %></td>
							  					<td style="text-align:right!important;"><%=df.format(finance.getAllotment()) %></td>
							  					<td style="text-align:right!important;"><%=df.format(finance.getSanction()) %></td>
							  					<td style="text-align:right!important;"><%=df.format(finance.getBalance()) %></td>
							  					<td style="text-align:right!important;"><%=df.format(finance.getOutStanding()) %></td>
							  					<td style="text-align:right!important;"><%=df.format(finance.getExpenditure()) %></td>
							  					<td style="text-align:right!important;" ><%=df.format(finance.getInr()) %></td>
							  					<td style="text-align:right!important;" ><%=df.format(finance.getFe()) %></td>
							  				</tr>
							  			<%}}else{ %>
							  				<tr>
							  					<td colspan="8" class="text-center">No Data Availabe</td>
							  				</tr>
							  			<%} %>
							  			
							  			<tr>
							  				<td colspan="2" class="text-center" >Total:</td>
							  				<td style="text-align:right!important;"><%= df.format(allotment)%></td>
							  				<td style="text-align:right!important;"><%=df.format(sanction) %></td>
							  				<td style="text-align:right!important;"><%=df.format(balance) %></td>
							  				<td style="text-align:right!important;"><%=df.format(oustanding) %></td>
							  				<td style="text-align:right!important;"><%=df.format(exp) %></td>
							  				<td style="text-align:right!important;"><%=df.format(inr) %></td>
							  				<td style="text-align:right!important;"><%=df.format(fe) %></td>
							  			</tr>
							  		</tbody>
							  	</table>
   						</div>
					<%-- 	 
						  	<table  class="subtables" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px;  border-collapse:collapse;" >
						  	    <thead>
		                           <tr>
		                         	<th colspan="2" style="text-align: center ;width:200px !important;"><b>Head</b></td>
		                         	<th colspan="2" style="text-align: center;width:120px !important;"><b>Sanction</b></td>
			                        <th colspan="2" style="text-align: center;width:120px !important;"><b>Expenditure</b></td>
			                        <th colspan="2" style="text-align: center;width:120px !important;"><b>Out Commitment</b> </td>
		                           	<th colspan="2" style="text-align: center;width:120px !important;"><b>Balance</b></td>
			                        <th colspan="2" style="text-align: center;width:120px !important;"><b>DIPL</b></td>
		                          	<th colspan="2" style="text-align: center;width:120px !important;"><b>Notional Balance</b></td>
			                      </tr>
			                      <tr>
				                    <th style="width:30px !important;text-align: center;" >SN</th>
				                    <th   style="width:180px !important;" width="10">Head</th>
				                    <th>RE</th>
				                    <th>FE</th>
				                    <th>RE</th>
				                    <th>FE</th>
			            	        <th>RE</th>
			                    	<th>FE</th>
		                  		    <th>RE</th>
				                    <th>FE</th>
				                    <th>RE</th>
				                    <th>FE</th>
				                    <th>RE</th>
				                    <th>FE</th>
		                       	  </tr>
			                    </thead>
			                     <%
			                 	double totSanctionCost=0,totReSanctionCost=0,totFESanctionCost=0;
				                	double totExpenditure=0,totREExpenditure=0,totFEExpenditure=0;
				                 	double totCommitment=0,totRECommitment=0,totFECommitment=0,totalDIPL=0,totalREDIPL=0,totalFEDIPL=0;
					                double totBalance=0,totReBalance=0,totFeBalance=0,btotalRe=0,btotalFe=0;
			                     
			                     if(IsIbasConnected==null || IsIbasConnected.equalsIgnoreCase("Y")) {%>
			                    <tbody>
			                    <% 

				                int count=1;
			                        if(projectFinancialDetails!=null && projectFinancialDetails.size()>0 && projectFinancialDetails.get(z)!=null ){
			                      for(ProjectFinancialDetails projectFinancialDetail:projectFinancialDetails.get(z)){                       %>
			 
			                         <tr>
										<td align="center" style="max-width:50px !important;text-align: center;"><%=count++ %></td>
										<td ><b><%=projectFinancialDetail.getBudgetHeadDescription()!=null?(projectFinancialDetail.getBudgetHeadDescription()): " - "%></b></td>
										<td align="right" style="text-align: right;"><%=df.format(projectFinancialDetail.getReSanction()) %></td>
										<%totReSanctionCost+=(projectFinancialDetail.getReSanction());%>
										<td align="right" style="text-align: right;"><%=df.format(projectFinancialDetail.getFeSanction())%></td>
										<%totFESanctionCost+=(projectFinancialDetail.getFeSanction());%>
										<td align="right" style="text-align: right;"><%=df.format(projectFinancialDetail.getReExpenditure()) %></td>
										<%totREExpenditure+=(projectFinancialDetail.getReExpenditure());%>
									    <td align="right" style="text-align: right;"><%=df.format(projectFinancialDetail.getFeExpenditure())%></td>
										<%totFEExpenditure+=(projectFinancialDetail.getFeExpenditure());%>
									    <td align="right" style="text-align: right;"><%=df.format(projectFinancialDetail.getReOutCommitment())%></td>
										<%totRECommitment+=(projectFinancialDetail.getReOutCommitment());%>
									    <td align="right" style="text-align: right;"><%=df.format(projectFinancialDetail.getFeOutCommitment())%></td>
										<%totFECommitment+=(projectFinancialDetail.getFeOutCommitment());%>
										<td align="right"style="text-align: right;"><%=df.format(projectFinancialDetail.getReBalance()+projectFinancialDetail.getReDipl())%></td>
										<%btotalRe+=(projectFinancialDetail.getReBalance()+projectFinancialDetail.getReDipl());%>
										<td align="right"style="text-align: right;"><%=df.format(projectFinancialDetail.getFeBalance()+projectFinancialDetail.getFeDipl())%></td>
								       	<%btotalFe+=(projectFinancialDetail.getFeBalance()+projectFinancialDetail.getFeDipl());%>
										<td align="right" style="text-align: right;"><%=df.format(projectFinancialDetail.getReDipl())%></td>
										<%totalREDIPL+=(projectFinancialDetail.getReDipl());%>
										<td align="right" style="text-align: right;"><%=df.format(projectFinancialDetail.getFeDipl())%></td>
										<%totalFEDIPL+=(projectFinancialDetail.getFeDipl());%>
										<td align="right" style="text-align: right;"><%=df.format(projectFinancialDetail.getReBalance())%></td>
										<%totReBalance+=(projectFinancialDetail.getReBalance());%>
										<td align="right" style="text-align: right;"><%=df.format(projectFinancialDetail.getFeBalance())%></td>
										<%totFeBalance+=(projectFinancialDetail.getFeBalance());%>
									</tr>
			<%} }%>

					<tr>
						<td colspan="2"><b>Total</b></td>
						<td align="right" style="text-align: right;"><%=df.format(totReSanctionCost)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totFESanctionCost)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totREExpenditure)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totFEExpenditure)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totRECommitment)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totFECommitment)%></td>
						<td align="right" style="text-align: right;"><%=df.format(btotalRe)%></td>
						<td align="right" style="text-align: right;"><%=df.format(btotalFe)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totalREDIPL)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totalFEDIPL)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totReBalance)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totFeBalance)%></td>
					</tr>
					<tr>
						<td colspan="2"><b>GrandTotal</b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=df.format(totReSanctionCost+totFESanctionCost)%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=df.format(totREExpenditure+totFEExpenditure)%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=df.format(totRECommitment+totFECommitment)%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=df.format(btotalRe+btotalFe)%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=df.format(totalREDIPL+totalFEDIPL)%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=df.format(totReBalance+totFeBalance)%></b></td>
					</tr>
			     </tbody>
			     <%}else{ %>
			     <tbody id="tbody<%=ProjectDetail.get(z)[0].toString()%>">
			     <%int count=0;
			     if(overallfinance!=null && overallfinance.size()>0 && overallfinance.get(z)!=null && overallfinance.get(z).size()>0)  {
			    	for(Object[]obj:overallfinance.get(z)){ 
			    	 %>
			    	 <tr>
			   <td align="center" style="max-width:50px !important;text-align: center;"><%=++count %></td>
				<td style="text-align: justify ;"><b><%=obj[4]!=null?(obj[4].toString()): " - "%></b></td>
				<td style="text-align: right;"><%=obj[5]!=null?(obj[5].toString()): " - "%></td>
				<td style="text-align: right;"><%=obj[6]!=null?(obj[6].toString()): " - "%></td>
				<td style="text-align: right;"><%=obj[7]!=null?(obj[7].toString()): " - "%></td>
				<td style="text-align: right;"><%=obj[8]!=null?(obj[8].toString()): " - "%></td>
				<td style="text-align: right;"><%=obj[9]!=null?(obj[9].toString()): " - "%></td>
				<td style="text-align: right;"><%=obj[10]!=null?(obj[10].toString()): " - "%></td>
				<td style="text-align: right;"><%=obj[11]!=null?(obj[11].toString()): " - "%></td>
				<td style="text-align: right;"><%=obj[12]!=null?(obj[12].toString()): " - "%></td>
				<td style="text-align: right;"><%=obj[13]!=null?(obj[13].toString()): " - "%></td>
				<td style="text-align: right;"><%=obj[14]!=null?(obj[14].toString()): " - "%></td>
				<td style="text-align: right;"><%=obj[15]!=null?(obj[15].toString()): " - "%></td>
				<td style="text-align: right;"><%=obj[16]!=null?(obj[16].toString()): " - "%></td>
				</tr>
			     <%}%>
			    	 	<tr>
						<td colspan="2"><b>Total</b></td>
						<td align="right" style="text-align: right;"><%=overallfinance.get(z).get(0)[17]!=null?(overallfinance.get(z).get(0)[17].toString()): " - "%></td>
						<td align="right" style="text-align: right;"><%=overallfinance.get(z).get(0)[18]!=null?(overallfinance.get(z).get(0)[18].toString()): " - "%></td>
						<td align="right" style="text-align: right;"><%=overallfinance.get(z).get(0)[19]!=null?(overallfinance.get(z).get(0)[19].toString()): " - "%></td>
						<td align="right" style="text-align: right;"><%=overallfinance.get(z).get(0)[20]!=null?(overallfinance.get(z).get(0)[20].toString()): " - "%></td>
						<td align="right" style="text-align: right;"><%=overallfinance.get(z).get(0)[21]!=null?(overallfinance.get(z).get(0)[21].toString()): " - "%></td>
						<td align="right" style="text-align: right;"><%=overallfinance.get(z).get(0)[22]!=null?(overallfinance.get(z).get(0)[22].toString()): " - "%></td>
						<td align="right" style="text-align: right;"><%=overallfinance.get(z).get(0)[23]!=null?(overallfinance.get(z).get(0)[23].toString()): " - "%></td>
						<td align="right" style="text-align: right;"><%=overallfinance.get(z).get(0)[24]!=null?(overallfinance.get(z).get(0)[24].toString()): " - "%></td>
						<td align="right" style="text-align: right;"><%=overallfinance.get(z).get(0)[25]!=null?(overallfinance.get(z).get(0)[25].toString()): " - "%></td>
						<td align="right" style="text-align: right;"><%=overallfinance.get(z).get(0)[26]!=null?(overallfinance.get(z).get(0)[26].toString()): " - "%></td>
						<td align="right" style="text-align: right;"><%=overallfinance.get(z).get(0)[27]!=null?(overallfinance.get(z).get(0)[27].toString()): " - "%></td>
						<td align="right" style="text-align: right;"><%=overallfinance.get(z).get(0)[28]!=null?(overallfinance.get(z).get(0)[28].toString()): " - "%></td>
					</tr>
			     	<tr>
						<td colspan="2"><b>GrandTotal</b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=Double.parseDouble(overallfinance.get(z).get(0)[17].toString())  +Double.parseDouble(overallfinance.get(z).get(0)[18].toString())%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=Double.parseDouble(overallfinance.get(z).get(0)[19].toString())  +Double.parseDouble(overallfinance.get(z).get(0)[20].toString())%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=Double.parseDouble(overallfinance.get(z).get(0)[21].toString())  +Double.parseDouble(overallfinance.get(z).get(0)[22].toString())%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=Double.parseDouble(overallfinance.get(z).get(0)[23].toString())  +Double.parseDouble(overallfinance.get(z).get(0)[24].toString())%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=Double.parseDouble(overallfinance.get(z).get(0)[25].toString())  +Double.parseDouble(overallfinance.get(z).get(0)[26].toString())%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=Double.parseDouble(overallfinance.get(z).get(0)[27].toString())  +Double.parseDouble(overallfinance.get(z).get(0)[28].toString())%></b></td>				     
			     	</tr>
			     <%}else{%> 
			     	<tr>
						<td colspan="2"><b>Total</b></td>
						<td align="right" style="text-align: right;"><%=df.format(totReSanctionCost)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totFESanctionCost)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totREExpenditure)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totFEExpenditure)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totRECommitment)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totFECommitment)%></td>
						<td align="right" style="text-align: right;"><%=df.format(btotalRe)%></td>
						<td align="right" style="text-align: right;"><%=df.format(btotalFe)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totalREDIPL)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totalFEDIPL)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totReBalance)%></td>
						<td align="right" style="text-align: right;"><%=df.format(totFeBalance)%></td>
					</tr>
					<tr>
						<td colspan="2"><b>GrandTotal</b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=df.format(totReSanctionCost+totFESanctionCost)%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=df.format(totREExpenditure+totFEExpenditure)%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=df.format(totRECommitment+totFECommitment)%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=df.format(btotalRe+btotalFe)%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=df.format(totalREDIPL+totalFEDIPL)%></b></td>
						<td colspan="2" align="right" style="text-align: right;"><b><%=df.format(totReBalance+totFeBalance)%></b></td>
					</tr>
			     <% }%>
			     </tbody>
			     <% } %>
			</table>  
		 --%>
		<%}} %>
		
</body>
</html> 