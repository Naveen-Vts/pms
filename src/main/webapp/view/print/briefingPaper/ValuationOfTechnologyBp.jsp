<%@page import="com.vts.pfms.milestone.model.ProjectEconomicImpact"%>
<%@page import="com.vts.pfms.milestone.dto.ProjectUtilizationBriefingDto"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%


List<Object[]> ProjectDetail=(List<Object[]>)request.getAttribute("ProjectDetails"); 
List<List<ProjectUtilizationBriefingDto>> manpowerDetails = (List<List<ProjectUtilizationBriefingDto>>)request.getAttribute("manpowerDetails"); 
List<List<ProjectUtilizationBriefingDto>> infrastructureDetails = (List<List<ProjectUtilizationBriefingDto>>)request.getAttribute("infrastructureDetails"); 
List<List<ProjectUtilizationBriefingDto>> trainingDetails = (List<List<ProjectUtilizationBriefingDto>>)request.getAttribute("trainingDetails"); 
List<List<ProjectEconomicImpact>> econmicImpactDetails = (List<List<ProjectEconomicImpact>>)request.getAttribute("econmicImpactDetails"); 
 


%>
<details>
	<summary role="button" tabindex="0"><b> 11. Valuation of Technologies</b>    </summary>
		<div class="content">
		
			<%for(int z=0;z<1;z++){ %>
				<%if(ProjectDetail.size()>1){ %>
					<div>
						<b>Project : <%=ProjectDetail.get(z)[1] %> 	<%if(z!=0){ %>(SUB)<%} %>	</b>
					</div>	
				<%} %>	
				<div align="left" class="margin-left15 fw-bold-1">(a) ManPower Utilisation in days. </div>
				
				
				<table class="subtables table-subtables">
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
				
				
				<div align="left" class="margin-left15 fw-bold-1">(b) Utilization of Established Infrastructure/ Facilities of the Lab/Sister Lab. </div>
				
				
				<table class="subtables table-subtables">
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
							<td colspan="10" class="text-center"> Nil</td>
						</tr>
					<%} %>
				</table>	
				
				
				<div align="left" class="margin-left15 fw-bold-1">(c) Training. </div>
				
				
				<table class="subtables table-subtables">
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
							<td colspan="10" class="text-center"> Nil</td>
						</tr>
					<%} %>
				</table>	
			<%} %>
		</div>
</details>
   					
<details>

    <summary role="button" tabindex="0">
        <b>12. Economic Impact of Project</b>
    </summary>

    <div class="content">

        <% for(int z = 0; z < 1; z++) { %>

            <% if(ProjectDetail.size() > 1) { %>

                <div>
                    <b>
                        Project : <%= ProjectDetail.get(z)[1] %>
                        <% if(z != 0) { %>(SUB)<% } %>
                    </b>
                </div>

            <% } %>


            <% 
                if(econmicImpactDetails != null 
                        && econmicImpactDetails.get(z) != null 
                        && econmicImpactDetails.get(z).size() > 0) {

                    for(ProjectEconomicImpact obj : econmicImpactDetails.get(z)) {
            %>


            <table class="subtables table-subtables economic-impact-table">

                <thead>
                    <tr>
                        <th class="economic-sno">Sl. No.</th>
                        <th class="economic-heading">Economic Impact</th>
                        <th class="economic-details">Details</th>
                    </tr>
                </thead>

                <tbody>

                    <!-- A -->
                    <tr>
                        <td class="text-center">a)</td>

                        <td class="economic-title">
                            Percentage Indigenous Content,
                            Dependent Foreign Countries,
                            Items Imported and
                            Indigenization Efforts
                        </td>

                        <td class="economic-value">
                            <%= obj.getIndigenousContentAndIndigenization() != null
                                    && !obj.getIndigenousContentAndIndigenization().trim().isEmpty()
                                    ? obj.getIndigenousContentAndIndigenization()
                                    : "-" %>
                        </td>
                    </tr>


                    <!-- B -->
                    <tr>
                        <td class="text-center">b)</td>

                        <td class="economic-title">
                            International Collaborations Executed
                            <br>
                            <span class="economic-note">
                                (provide details and duration)
                            </span>
                        </td>

                        <td class="economic-value">
                            <%= obj.getInternationalCollaborationsExecuted() != null
                                    && !obj.getInternationalCollaborationsExecuted().trim().isEmpty()
                                    ? obj.getInternationalCollaborationsExecuted()
                                    : "-" %>
                        </td>
                    </tr>


                    <!-- C -->
                    <tr>
                        <td class="text-center">c)</td>

                        <td class="economic-title">
                            Intellectual Property Rights Generated
                            <br>
                            <span class="economic-note">
                                (provide details of Patents, Designs and Copyrights)
                            </span>
                        </td>

                        <td class="economic-value">
                            <%= obj.getIntellectualPropertyRights() != null
                                    && !obj.getIntellectualPropertyRights().trim().isEmpty()
                                    ? obj.getIntellectualPropertyRights()
                                    : "-" %>
                        </td>
                    </tr>


                    <!-- D -->
                    <tr>
                        <td class="text-center">d)</td>

                        <td class="economic-title">
                            Export Potential, if any
                        </td>

                        <td class="economic-value">
                            <%= obj.getExportPotential() != null
                                    && !obj.getExportPotential().trim().isEmpty()
                                    ? obj.getExportPotential()
                                    : "-" %>
                        </td>
                    </tr>


                    <!-- E -->
                    <tr>
                        <td class="text-center">e)</td>

                        <td class="economic-title">
                            Infrastructure created, if any
                        </td>

                        <td class="economic-value">
                            <%= obj.getInfrastructureCreated() != null
                                    && !obj.getInfrastructureCreated().trim().isEmpty()
                                    ? obj.getInfrastructureCreated()
                                    : "-" %>
                        </td>
                    </tr>

                </tbody>

            </table>


            <% 
                    }
                } else {
            %>

                <table class="subtables table-subtables">

                    <tr>
                        <td class="text-center">
                            Nil
                        </td>
                    </tr>

                </table>

            <% } %>

        <% } %>

    </div>

</details>
   					