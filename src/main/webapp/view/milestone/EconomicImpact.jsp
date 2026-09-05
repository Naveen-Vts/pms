<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.vts.pfms.milestone.model.ProjectEconomicImpact"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
    <jsp:include page="../static/header.jsp"></jsp:include>
    <spring:url value="/resources/css/milestone/ManpowerUtilization.css" var="resourceCss" />
    <link href="${resourceCss}" rel="stylesheet" />
    <title>Economic Impact</title>

    <style>
        .economic-impact-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 1.5rem 2rem;
        }
        .economic-impact-grid .field-full {
            grid-column: 1 / -1;
        }
        .economic-impact-field label.form-label {
            margin-bottom: 0.25rem;
        }
        .economic-impact-field .form-text {
            margin-bottom: 0.5rem;
        }
        .economic-impact-field textarea.form-control {
            resize: vertical;
            min-height: 90px;
        }
        @media (max-width: 768px) {
            .economic-impact-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>

    <%
    List<ProjectEconomicImpact> economicImpacts = (List<ProjectEconomicImpact>) request.getAttribute("economicImpact");
    List<Object[]> projectList = (List<Object[]>) request.getAttribute("projectList");
    
	Object[] projectDetails = (Object[]) request.getAttribute("projectDetails");
    String projectId =  (String) request.getAttribute("projectId");
    
    ProjectEconomicImpact modal = null;
    Long economicImpactId = null;
    Long revisionNo = 0L;
	
	if (economicImpacts != null && !economicImpacts.isEmpty()) {
	
		modal = economicImpacts.get(0);
		economicImpactId = modal != null ? modal.getEconomicImpactId() : null;
		revisionNo = modal.getRevisionNo();
	}
	
    String projectCode = null;
    if(projectDetails != null){
    	if(projectDetails[4] != null){
    		projectCode = projectDetails[4].toString();
    	}
    }
    
    
    %>
</head>
<body>
<%
    String successMessage =   request.getParameter("result");
    String errorMessage =  request.getParameter("resultfail");
    if (errorMessage != null) {
%>
<div align="center">
    <div class="alert alert-danger" role="alert">
        <%=StringEscapeUtils.escapeHtml4(errorMessage)%>
    </div>
</div>
<% } else if (successMessage != null) { %>
<div align="center">
    <div class="alert alert-success" role="alert">
        <%=StringEscapeUtils.escapeHtml4(successMessage)%>
    </div>
</div>
<%}%>

<div class="container-fluid">
	<div class="card">
		<div class="card-header justify-space-between">
			<h3>
				Economic Impact Of <%= projectCode != null ? projectCode : "Project" %>
			</h3>
			<div class="justify-content-end gap-3" style="display:flex; align-items:center; gap:16px;">
				<%if(revisionNo != null && revisionNo > 0){ %>
					<a href="EconomicImpactHistory.htm?projectId=<%=projectId%>" class="btn btn-sm viewall">
						<i class="fa fa-history"></i> View Revision History
					</a>
				<%} %>
				<label class="fw-bold">Project: </label>
				<form method="get" action="EconomicImpact.htm" >
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<select class="form-control items w-60" name="projectId"  required="required"  data-live-search="true" data-container="body" onchange="this.form.submit();">
						<%for(Object[] obj : projectList){ 
							String projectshortName=(obj[17]!=null)?" ( "+obj[17].toString()+" ) ":"";
						%>
							<option value="<%=obj[0]%>" <%if(projectId!=null && projectId.equals(obj[0].toString())) { %>selected <%} %> ><%=obj[4] +projectshortName%></option>
						<%} %>
					</select>
				</form>
			</div>
		</div>
		<div class="card-body">
			    <form action="SaveEconomicImpact.htm" method="post" id="economicImpactForm">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			        <input type="hidden" name="projectId" value="<%=projectId%>">
			        <input type="hidden" name="status" value="ADD" id="economicStatus">
			        <input type="hidden" name="economicImpactId" value="<%=economicImpactId%>">
					
			        <div class="economic-impact-grid">

			            <div class="economic-impact-field">
			                <label class="form-label fw-bold">
			                    a) Indigenous Content, Foreign Dependency &amp; Indigenization Efforts
			                </label>
			                <!-- <small class="form-text text-muted d-block">
			                    Mention indigenous content percentage, dependent foreign countries,
			                    imported items and indigenization efforts/status.
			                </small> -->
			                <%
							String indigenousContent = "";
							
							if (modal != null && modal.getIndigenousContentAndIndigenization() != null) {
							
							    indigenousContent = modal.getIndigenousContentAndIndigenization();
							}
							%>
			                <textarea name="indigenousContentAndIndigenization" class="form-control" rows="2" required><%=indigenousContent %></textarea>
			            </div>

			            <div class="economic-impact-field">
			                <label class="form-label fw-bold">
			                    b) International Collaborations Executed
			                </label>
			               <!--  <small class="form-text text-muted d-block">
			                    Provide organization/country, purpose of collaboration, duration,
			                    current status and major outcome.
			                </small> -->
			                <%
							String internationalCollaboration = "";
							
							if (modal != null && modal.getInternationalCollaborationsExecuted() != null) {
							
								internationalCollaboration = modal.getInternationalCollaborationsExecuted();
							}
							%>
			                <textarea name="internationalCollaborationsExecuted" class="form-control" rows="2" required><%=internationalCollaboration %></textarea>
			            </div>

			            <div class="economic-impact-field">
			                <label class="form-label fw-bold">
			                    c) Intellectual Property Rights Generated
			                </label>
			              <!--   <small class="form-text text-muted d-block">
			                    Provide details of Patents, Designs and Copyrights, including title,
			                    application/registration number and status.
			                </small> -->
			                <%
							String intellectualProperty = "";
							
							if (modal != null && modal.getIntellectualPropertyRights() != null) {
							
								intellectualProperty = modal.getIntellectualPropertyRights();
							}
							%>
			                <textarea name="intellectualPropertyRights" class="form-control" rows="2" required><%=intellectualProperty%></textarea>
			            </div>

			            <div class="economic-impact-field">
			                <label class="form-label fw-bold">
			                    d) Export Potential, if any
			                </label>
			              <!--   <small class="form-text text-muted d-block">
			                    Mention whether export potential exists and provide product,
			                    target country, estimated value, timeframe and status.
			                </small> -->
			                <%
							String exportPotential = "";
							
							if (modal != null && modal.getExportPotential() != null) {
							
								exportPotential = modal.getExportPotential();
							}
							%>
			                <textarea name="exportPotential" class="form-control" rows="2"><%=exportPotential %></textarea>
			            </div>

			            <div class="economic-impact-field ">
			                <label class="form-label fw-bold">
			                    e) Infrastructure Created, if any
			                </label>
			              <!--   <small class="form-text text-muted d-block">
			                    Mention facility/infrastructure name, type, location, purpose,
			                    creation date, cost and current status.
			                </small> -->
			                <%
							String insfrastructureCreated = "";
							
							if (modal != null && modal.getInfrastructureCreated() != null) {
							
								insfrastructureCreated = modal.getInfrastructureCreated();
							}
							%>
			                <textarea name="infrastructureCreated" class="form-control" rows="2" ><%=insfrastructureCreated %></textarea>
			            </div>

			        </div>

			        <div class="d-flex justify-content-center gap-4 mt-4" style="gap:10px;">
			            <button type="reset" class="btn btn-sm viewall" onclick="resetEconomicImpactForm();">
			                Reset
			            </button>
			            <%if(modal == null){ %>
				            <button type="submit" class="btn btn-sm submit" onclick="$('#economicStatus').val('ADD'); return confirm('Are You sure to Submit? ');">
				                Submit
				            </button>
			            <%}else if(modal != null && modal.getRevisionNo() != null){ 
			            	if(modal.getRevisionNo() == 0){
			            %>
			             <button type="submit" class="btn btn-sm edit" onclick="$('#economicStatus').val('EDIT'); return confirm('Are You sure to Edit? ');">
				                edit
				            </button>
			            <%} %>
			             <button type="submit" class="btn btn-sm revise" onclick="$('#economicStatus').val('REVISE'); return confirm('Are You sure to Revise? ');">
				                Revise
				            </button>
			            <%} %>
			        </div>
			    </form>	
		</div>
	</div>
</div>

<script type="text/javascript">
$('.items').select2();

function resetEconomicImpactForm() {
    document.getElementById("economicImpactForm").reset();
}
</script>
</body>
</html>
