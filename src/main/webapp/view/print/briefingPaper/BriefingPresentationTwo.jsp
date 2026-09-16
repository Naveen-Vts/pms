<%@page import="java.time.LocalDate"%>
<%@page import="java.time.temporal.ChronoUnit"%>
<%@page import="com.vts.pfms.committee.model.Committee"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="com.vts.pfms.milestone.model.ProjectEconomicImpact"%>
<%@page import="com.vts.pfms.milestone.dto.ProjectUtilizationBriefingDto"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<%
String Drdologo = (String)request.getAttribute("Drdologo");
String lablogo = (String)request.getAttribute("lablogo");
FormatConverter fc = new FormatConverter();
SimpleDateFormat sdf = fc.getRegularDateFormat();
SimpleDateFormat sdf1 = fc.getSqlDateFormat();

Object[] committeeMetingsCount =  (Object[]) request.getAttribute("committeeMetingsCount");
Committee committeeData = (Committee) request.getAttribute("committeeData");
String committeeid = (String) request.getAttribute("committeeid");
String CommitteeCode = committeeData.getCommitteeShortName().trim();

List<Object[]> projectattributeslist = (List<Object[]>) request.getAttribute("projectattributes");
List<List<Object[]>> oldpmrcissueslist = (List<List<Object[]>>) request.getAttribute("oldpmrcissueslist");
LocalDate before6months = LocalDate.now().minusDays(committeeData.getPeriodicDuration());

List<Object[]> ProjectDetail = (List<Object[]>) request.getAttribute("ProjectDetails");
String pdc = "";;

String ProjectCode="";
for(int i=0;i<projectattributeslist.size();i++){
	ProjectCode = ProjectCode +projectattributeslist.get(i)[0].toString()  ;
	pdc = pdc+"(PDC:"+sdf.format(sdf1.parse(projectattributeslist.get(i)[6].toString()))+")<br>";
	if(i!=projectattributeslist.size()-1)ProjectCode=ProjectCode+"/";
}
String MeetingNo = CommitteeCode+" #"+(Long.parseLong(committeeMetingsCount[1].toString())+1);

List<List<ProjectUtilizationBriefingDto>> manpowerDetails = (List<List<ProjectUtilizationBriefingDto>>)request.getAttribute("manpowerDetails"); 
List<List<ProjectUtilizationBriefingDto>> infrastructureDetails = (List<List<ProjectUtilizationBriefingDto>>)request.getAttribute("infrastructureDetails"); 
List<List<ProjectUtilizationBriefingDto>> trainingDetails = (List<List<ProjectUtilizationBriefingDto>>)request.getAttribute("trainingDetails"); 
List<List<ProjectEconomicImpact>> econmicImpactDetails = (List<List<ProjectEconomicImpact>>)request.getAttribute("econmicImpactDetails"); 

%>
				<div class="carousel-item ">
					<div class="content-header row ">
					<div class="col-md-1" ><img class="bp-18"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					<div class="col-md-1 bp-19" align="left"  ><b class="bp-20"><%=ProjectCode %></b>
					<h6 class="bp-21"><%=pdc %></h6>
					</div>
					<div class="col-md-8">
						<h3> 11. Valuation Of Technologies</h3>
					</div>
					<div class="col-md-1 bp-22" align="right"  ><b class="bp-20"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="bp-18"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					</div>
				
				
				<div class="content">

					<% for (int z = 0; z < 1; z++) { %>
					<% if (ProjectDetail.size() > 1) { %>
					<div>
						<b>Project : <%=ProjectDetail.get(z)[1]%> <% if (z != 0) {  %>(SUB)<% }  %> </b>
					</div>
					<%
					}
					%>
					<div align="left" style="margin-top: 5px;margin-left: 10px;"><b class="mainsubtitle">(a) ManPower Utilisation in days. </b>
   		
				   		<table  class="subtables" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px;  border-collapse:collapse;" > 
				   			<thead>
						        <tr>
						            <th rowspan="2" class="width60">
						                ManPower Utilisation in days
						            </th>
						            <th colspan="4" class="width30">
						                Man-days utilised
						            </th>
						            <th rowspan="2" class="width60">
						                (cummulative past years)
						            </th>
						            <th rowspan="2" class="width50">
						                (cummulative Till date)
						            </th>
						        </tr>
						        <tr>
							        <th class="width150">(1<sup>st</sup> Quarter)</th>
									<th class="width150">(2<sup>nd</sup> Quarter)</th>
									<th class="width150">(3<sup>rd</sup> Quarter)</th>
									<th class="width150">(4<sup>th</sup> Quarter)</th>
						        </tr>
						    </thead>
						    <% 
							if(manpowerDetails != null && manpowerDetails.get(z).size()>0) { 		
								for(ProjectUtilizationBriefingDto obj : manpowerDetails.get(z)){
									
									%>
									<tr>
									    <td><%= obj.getDesigCrade() != null ? obj.getDesigCrade() : "-" %></td>
									
									    <td class="text-center"><%= obj.getFirstQuarter() != null ? obj.getFirstQuarter() : "-" %></td>
									
									    <td class="text-center"><%= obj.getSecondQuarter() != null ? obj.getSecondQuarter() : "-" %></td>
									
									    <td class="text-center"><%= obj.getThirdQuarter() != null ? obj.getThirdQuarter() : "-" %></td>
									
									    <td class="text-center"><%= obj.getFourthQuarter() != null ? obj.getFourthQuarter() : "-" %></td>
									
									    <td class="text-center"><%= obj.getCummulativePastYears() != null ? obj.getCummulativePastYears() : "-" %></td>
									
									    <td class="text-center"><%= obj.getCummulativeTillDate() != null ? obj.getCummulativeTillDate() : "-" %></td>
									</tr>
								<%} 
								} else{ %>
								<tr>
									<td colspan="9" class="text-center"> Nil</td>
								</tr>
							<%} %>
				   		</table>   		
			   		</div>
			   		
			   		
			   		<div align="left" style="margin-top: 5px;margin-left: 10px;"><b class="mainsubtitle">(b) Utilization of Established Infrastructure/ Facilities of the Lab/Sister Lab. </b>
			   		
				   		<table  class="subtables" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px;  border-collapse:collapse;" > 
							    <thead>
							        <tr>
							            <th colspan="2" class="width150">
							               	(1<sup>st</sup> Quarter)
							            </th>
							            <th colspan="2" class="width150">
							               	(2<sup>nd</sup> Quarter)
							            </th>
							            <th colspan="2" class="width150">
							            	(3<sup>rd</sup> Quarter)
							            </th>
							            <th colspan="2" class="width150">
							            	(4<sup>th</sup> Quarter)
							            </th>
							            <th rowspan="2" class="width60">
							                (cummulative past years)
							            </th>
							            <th rowspan="2" class="width60">
							                (cummulative Till date)
							            </th>
							        </tr>
							        <tr>
							            <th class="width150">Name of Infra/Facility</th>
							            <th class="width150">Days Utilized</th>
							            <th class="width150">Name of Infra/Facility</th>
							            <th class="width150">Days Utilized</th>
							            <th class="width150">Name of Infra/Facility</th>
							            <th class="width150">Days Utilized</th>
							            <th class="width150">Name of Infra/Facility</th>
							            <th class="width150">Days Utilized</th>
							        </tr>
							    </thead>
							    <% if(infrastructureDetails != null && infrastructureDetails.get(z).size()>0) { 									
									for(ProjectUtilizationBriefingDto obj:infrastructureDetails.get(z)){
										
										%>
										<tr>
																	
										    <td class="text-left"><%= obj.getNameOfInfrastructure() != null ? obj.getNameOfInfrastructure() : "-" %></td>
										    
										    <td class="text-center"><%= obj.getFirstQuarter() != null ? obj.getFirstQuarter() : "-" %></td>
										
										    <td class="text-left"><%= obj.getNameOfInfrastructure() != null ? obj.getNameOfInfrastructure() : "-" %></td>
										    
										    <td class="text-center"><%= obj.getSecondQuarter() != null ? obj.getSecondQuarter() : "-" %></td>
										
										    <td class="text-left"><%= obj.getNameOfInfrastructure() != null ? obj.getNameOfInfrastructure() : "-" %></td>
										    
										    <td class="text-center"><%= obj.getThirdQuarter() != null ? obj.getThirdQuarter() : "-" %></td>
										
										    <td class="text-left"><%= obj.getNameOfInfrastructure() != null ? obj.getNameOfInfrastructure() : "-" %></td>
										    
										    <td class="text-center"><%= obj.getFourthQuarter() != null ? obj.getFourthQuarter() : "-" %></td>
										
										    <td class="text-center"><%= obj.getCummulativePastYears() != null ? obj.getCummulativePastYears() : "-" %></td>
										
										    <td class="text-center"><%= obj.getCummulativeTillDate() != null ? obj.getCummulativeTillDate() : "-" %></td>
										</tr>
									<%} 
									} else{ %>
									<tr>
										<td colspan="10"  style="text-align: center!important;" > Nil</td>
									</tr>
								<%} %>
				   		</table>
			   		</div>
			   		
			   		<div align="left" style="margin-top: 5px;margin-left: 10px;"><b class="mainsubtitle">(c) Training. </b>
			   		
				   		<table  class="subtables" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px;  border-collapse:collapse;" > 
				   		 	<thead>
							        <tr>
							            <th colspan="2" class="width150">
							               	(1<sup>st</sup> Quarter)
							            </th>
							            <th colspan="2" class="width150">
							               	(2<sup>nd</sup> Quarter)
							            </th>
							            <th colspan="2" class="width150">
							            	(3<sup>rd</sup> Quarter)
							            </th>
							            <th colspan="2" class="width150">
							            	(4<sup>th</sup> Quarter)
							            </th>
							            <th rowspan="2" class="width60">
							                (cummulative past years)
							            </th>
							            <th rowspan="2" class="width60">
							                (cummulative Till date)
							            </th>
							        </tr>
							        <tr>
							            <th class="width150">Name of Infra/Facility</th>
							            <th class="width150">Days Utilized</th>
							            <th class="width150">Name of Infra/Facility</th>
							            <th class="width150">Days Utilized</th>
							            <th class="width150">Name of Infra/Facility</th>
							            <th class="width150">Days Utilized</th>
							            <th class="width150">Name of Infra/Facility</th>
							            <th class="width150">Days Utilized</th>
							        </tr>
							    </thead>
							    <% if(trainingDetails != null && trainingDetails.get(z).size()>0) { 									
									for(ProjectUtilizationBriefingDto obj:trainingDetails.get(z)){
										
										%>
										<tr>
																	
										    <td class="text-left"><%= obj.getNameOfTraining() != null ? obj.getNameOfTraining() : "-" %></td>
										    
										    <td class="text-center"><%= obj.getFirstQuarter() != null ? obj.getFirstQuarter() : "-" %></td>
										
										    <td class="text-left"><%= obj.getNameOfTraining() != null ? obj.getNameOfTraining() : "-" %></td>
										    
										    <td class="text-center"><%= obj.getSecondQuarter() != null ? obj.getSecondQuarter() : "-" %></td>
										
										    <td class="text-left"><%= obj.getNameOfTraining() != null ? obj.getNameOfTraining() : "-" %></td>
										    
										    <td class="text-center"><%= obj.getThirdQuarter() != null ? obj.getThirdQuarter() : "-" %></td>
										
										    <td class="text-left"><%= obj.getNameOfTraining() != null ? obj.getNameOfTraining() : "-" %></td>
										    
										    <td class="text-center"><%= obj.getFourthQuarter() != null ? obj.getFourthQuarter() : "-" %></td>
										
										    <td class="text-center"><%= obj.getCummulativePastYears() != null ? obj.getCummulativePastYears() : "-" %></td>
										
										    <td class="text-center"><%= obj.getCummulativeTillDate() != null ? obj.getCummulativeTillDate() : "-" %></td>
										</tr>
									<%} 
									} else{ %>
									<tr>
										<td colspan="10"  style="text-align: center!important;" > Nil</td>
									</tr>
								<%} %>
				   		</table>
			   		</div>
					<% } %>
				</div>
			</div>

			<!-- ---------------------------------------- P-11 Valuation of Technology Div ----------------------------------------------------- -->
			
			<!-- ---------------------------------------- P-12 Economic Impact Of Project Div ----------------------------------------------------- -->
			
				<div class="carousel-item ">
					<div class="content-header row ">
					<div class="col-md-1" ><img class="bp-18"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					<div class="col-md-1 bp-19" align="left"  ><b class="bp-20"><%=ProjectCode %></b>
					<h6 class="bp-21"><%=pdc %></h6>
					</div>
					<div class="col-md-8">
						<h3> 12. Economic Impact of Project</h3>
					</div>
					<div class="col-md-1 bp-22" align="right"  ><b class="bp-20"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="bp-18"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					</div>
				
				
				<div class="content">

					<% for (int z = 0; z < 1; z++) { %>
					<% if (ProjectDetail.size() > 1) { %>
					<div>
						<b>Project : <%=ProjectDetail.get(z)[1]%> <% if (z != 0) {  %>(SUB)<% }  %> </b>
					</div>
					<%
					}
					%>
				
					<table  class="subtables" style="align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 25px;  border-collapse:collapse;width: 95%;" > 
		   		 		<thead>
					        <tr>
					            <th style="width: 5%;">Sl. No.</th>
					            <th style="width: 35%;">Economic Impact</th>
					            <th style="width: 60%;">Details</th>
					        </tr>
					    </thead>
						    <tbody>
							<% if(econmicImpactDetails != null && econmicImpactDetails.get(z) != null && econmicImpactDetails.get(z).size() > 0) {
								ProjectEconomicImpact obj = econmicImpactDetails.get(z).get(0);
							    if(obj!=null) { %>
			                    <tr>
			                        <td class="text-center">a)</td>
			                        <td class="text-left"> Percentage Indigenous Content, Dependent Foreign Countries, Items Imported and Indigenization Efforts </td>
			                        <td class="economic-value">  <%= obj.getIndigenousContentAndIndigenization() != null && !obj.getIndigenousContentAndIndigenization().trim().isEmpty() ? obj.getIndigenousContentAndIndigenization() : "-" %> </td>
			                    </tr>
			                    <tr>
			                        <td class="text-center">b)</td>
			                        <td class="economic-title"> International Collaborations Executed </td>
			                        <td class="economic-value"> <%= obj.getInternationalCollaborationsExecuted() != null && !obj.getInternationalCollaborationsExecuted().trim().isEmpty() ? obj.getInternationalCollaborationsExecuted() : "-" %> </td>
			                    </tr>
			                    <tr>
			                        <td class="text-center">c)</td>
			                        <td class="economic-title"> Intellectual Property Rights Generated </td>
			                        <td class="economic-value"> <%= obj.getIntellectualPropertyRights() != null && !obj.getIntellectualPropertyRights().trim().isEmpty() ? obj.getIntellectualPropertyRights() : "-" %> </td>
			                    </tr>
			                    <tr>
			                        <td class="text-center">d)</td>
			                        <td class="economic-title"> Export Potential </td>
			                        <td class="economic-value"> <%= obj.getExportPotential() != null && !obj.getExportPotential().trim().isEmpty() ? obj.getExportPotential() : "-" %> </td>
			                    </tr>
			                    <tr>
			                        <td class="text-center">e)</td>
			                        <td class="economic-title"> Infrastructure created </td>
			                        <td class="economic-value"> <%= obj.getInfrastructureCreated() != null && !obj.getInfrastructureCreated().trim().isEmpty() ? obj.getInfrastructureCreated() : "-" %> </td>
			                    </tr>
							<%}}else{ %>
							<tr>
								<td colspan="3" style="text-align: center!important;" >NIL</td>
							</tr>
							<%} %>
	                	</tbody>
					</table>
					<% } %>
				</div>
			</div>
			<!-- ---------------------------------------- P-12  Economic Impact Of Project Div ----------------------------------------------------- -->
			
				<!-- ---------------------------------------- P-13  GANTT chart of overall project Div ----------------------------------------------------- -->

		<%-- 	<div class="carousel-item ">

	
				
					<div class="content-header row ">
					<div class="col-md-1" ><img class="bp-18"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					<div class="col-md-1 bp-19" align="left"  ><b class="bp-20"><%=ProjectCode %></b>
					<h6 class="bp-21"><%=pdc %></h6>
					</div>
					<div class="col-md-8">
					<h3>13. GANTT Chart of Overall Project Schedule</h3>
					</div>
					<div class="col-md-1 bp-22" align="right"  ><b class="bp-20"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="bp-18"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					</div>


				<div class="content">
				<jsp:include page="../BpGrantChart.jsp" />
				</div>
			</div> --%>
			<!-- ---------------------------------------- GANTT chart of overall project Div ----------------------------------------------------- -->
			<!-- ---------------------------------------- P-14 Issues Div ----------------------------------------------------- -->
			<%-- <div class="carousel-item ">
					<div class="content-header row ">
					<div class="col-md-1" ><img class="bp-18"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> ></div>
					<div class="col-md-1 bp-19" align="left"  ><b class="bp-20"><%=ProjectCode %></b>
					<h6 class="bp-21"><%=pdc %></h6>
					</div>
					<div class="col-md-8">
						<h3>14. Issues</h3>
					</div>
					<div class="col-md-1 bp-22" align="right"  ><b class="bp-20"><%=MeetingNo %></b></div>
					<div class="col-md-1"><img class="bp-18"   <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
					</div>
					</div>

				<div class="content">
					<% for (int z = 0; z < 1; z++) { %>

					<% if (ProjectDetail.size() > 1) { %>
					<div>
						<b>Project : <%=ProjectDetail.get(z)[1]%> <% if (z != 0) {  %>(SUB)<% }  %> </b>
					</div>
					<% } %>
					<!-- CALL Old_Issues_List(:projectid); -->
					<table class="subtables bp-55" >
						<thead>
							<tr>
								<td colspan="7" class="border=0">
									<p class="bp-49">
										<span class="notassign">NA</span> : Not Assigned &nbsp;&nbsp;
										<span class="assigned">AA</span> : Activity Assigned &nbsp;&nbsp; 
										<span class="ongoing">OG</span> : On Going &nbsp;&nbsp; 
										<span class="delay">DO</span> : Delay - On Going &nbsp;&nbsp; 
										<span class="ongoing">RC</span> : Review & Close &nbsp;&nbsp; 
										<span class="delay">FD</span> : Forwarded With Delay &nbsp;&nbsp; 
										<span class="completed">CO</span> :Completed &nbsp;&nbsp; 
										<span class="completeddelay">CD</span> : Completed with Delay &nbsp;&nbsp; 
										<span class="inactive">IA</span> : InActive &nbsp;&nbsp; 
										<span class="delaydays">DD</span> : Delayed days &nbsp;&nbsp;
									</p>
								</td>
							</tr>
							<tr>
								<th class="width20">SN</th>
								<th class="width20">ID</th>
								<th class="width350">Issue Point</th>
								<th class="width100">ADC <br> PDC</th>
								<!-- <th style="width: 100px;">ADC</th> -->
								<th class="width200">Responsibility</th>
								<th class="width50">Status(DD)</th>
								<th class="width220">Remarks</th>
							</tr>
						</thead>
						<tbody>
							<% if (oldpmrcissueslist.get(z).size() == 0) { %>
							<tr>
								<td colspan="7" class="text-center">Nil</td>
							</tr>
							<% } else if (oldpmrcissueslist.get(z).size() > 0) {
							int i = 1;
							for (Object[] obj : oldpmrcissueslist.get(z)) {
								if(!obj[9].toString().equals("C")  || (obj[9].toString().equals("C") && obj[13]!=null && before6months.isBefore(LocalDate.parse(obj[13].toString())) )){ %>
							<tr>
								<td class="text-center"><%=i%></td>
									<td class="text-center" >
									<%if(obj[18]!=null && Long.parseLong(obj[18].toString())>0){
										String []temp=obj[1].toString().split("/");
										String tempString=temp[temp.length-1];
										%>
										<button type="button" class="btn btn-sm font-weight-bold"  onclick="ActionDetails( <%=obj[18] %>);" data-toggle="tooltip" data-placement="bottom" title="Action Details" >
										<%=tempString %>
										</button>
									<%}%>
								</td>
								<td class="text-justify"> <%=(obj[2].toString())%> </td>
								<td class="text-justify">
																	<%	String actionstatus = obj[9].toString();
										int progress = obj[16]!=null ? Integer.parseInt(obj[16].toString()) : 0;
										LocalDate pdcorg = LocalDate.parse(obj[3].toString());
										LocalDate endDate = LocalDate.parse(obj[4].toString());
										LocalDate lastdate = obj[13]!=null ? LocalDate.parse(obj[13].toString()): null;
										LocalDate today = LocalDate.now();
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
									<%if(!pdcorg.equals(endDate)) {%>
									<%=sdf.format(sdf1.parse(obj[4].toString()))%><br>
									<%} %>
									<%=sdf.format(sdf1.parse(obj[3].toString()))%>
								</td>
						<!-- 		<td style="text-align: center;">

								</td> -->
								<td><%=obj[11]%><%=obj[12] %></td>
								<td class="text-center">
									<%if(obj[4]!= null){ %> 
														
										<% if(lastdate!=null && actionstatus.equalsIgnoreCase("C") ){%>
											<%if(actionstatus.equals("C") && (pdcorg.isAfter(lastdate) || pdcorg.equals(lastdate))){%>
												<span class="completed">CO</span>
											<%}else if(actionstatus.equals("C") && pdcorg.isBefore(lastdate)){ %>	
												<span class="delay">CD (<%= ChronoUnit.DAYS.between(pdcorg, today)  %>)  </span>
											<%} %>	
										<%}else{ %>
											<%if(actionstatus.equals("F")  && (pdcorg.isAfter(lastdate) || pdcorg.isEqual(lastdate) )){ %>
												<span class="ongoing">RC</span>												
											<%}else if(actionstatus.equals("F")  && pdcorg.isBefore(lastdate)) { %>
												<span class="delay">FD</span>
											<%}else if(actionstatus.equals("A") && progress==0){  %>
												<span class="assigned">
													AA <%if(pdcorg.isBefore(today)){ %> (<%= ChronoUnit.DAYS.between(pdcorg, today)  %>) <%} %>
												</span>
											<%} else if(pdcorg.isAfter(today) || pdcorg.isEqual(today)){  %>
												<span class="ongoing">OG</span>
											<%}else if(pdcorg.isBefore(today)){  %>
												<span class="delay">DO (<%= ChronoUnit.DAYS.between(pdcorg, today)  %>)  </span>
											<%} %>										
										<%} %>
									<%}else { %>
										-
									<%} %>
								</td>
								<td>
									<% if (obj[17] != null) { %> <%=(obj[17].toString() )%> <% } %>
								</td>
							
							</tr>
							<% i++; }}
							} %>
						</tbody>
					</table>
					<% } %>
				</div>

			</div> --%>