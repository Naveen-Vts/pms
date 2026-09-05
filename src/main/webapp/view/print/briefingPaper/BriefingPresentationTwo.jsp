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
			
			