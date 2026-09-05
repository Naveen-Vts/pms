<%@page import="org.apache.commons.text.StringEscapeUtils"%>
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
    <title>Economic Impact - Revision History</title>

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
        .economic-impact-readonly {
            background: #F5F5F5;
            border: 1px solid #D9D6C9;
            border-radius: 4px;
            padding: 10px 12px;
            min-height: 70px;
            white-space: pre-wrap;
            color: #1E2321;
        }
        @media (max-width: 768px) {
            .economic-impact-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>

    <%
    List<Object[]> revisionList = (List<Object[]>) request.getAttribute("revisionList");
    Object[] projectDetails = (Object[]) request.getAttribute("projectDetails");
    List<Object[]> projectList = (List<Object[]>) request.getAttribute("projectList");
    String projectId =  (String) request.getAttribute("projectId");
    Long selectedRevisionNo = (Long) request.getAttribute("selectedRevisionNo");

    String indigenousContent = (String) request.getAttribute("indigenousContent");
    String internationalCollaboration = (String) request.getAttribute("internationalCollaboration");
    String intellectualProperty = (String) request.getAttribute("intellectualProperty");
    String exportPotential = (String) request.getAttribute("exportPotential");
    String infrastructureCreated = (String) request.getAttribute("infrastructureCreated");
    String revisedDate = (String) request.getAttribute("revisedDate");
    String revisedBy = (String) request.getAttribute("revisedBy");

    String projectCode = null;
    if (projectDetails != null && projectDetails[3] != null) {
        projectCode = projectDetails[3].toString();
    }
    %>
</head>
<body>
<div class="container-fluid">
	<div class="card">
		<div class="card-header justify-space-between" style="display:flex; justify-content:space-between; align-items:center;">
			<h3>
				Economic Impact History &mdash; <%= projectCode != null ? projectCode : "Project" %>
			</h3>
			<div class="justify-content-end gap-3" style="display:flex; align-items:center; gap:16px;">
				<label class="fw-bold">Project: </label>
				<form method="get" action="EconomicImpactHistory.htm" >
	                   <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<select class="form-control items w-60" name="projectId"  required="required"  data-live-search="true" data-container="body" onchange="this.form.submit();">
						<%for(Object[] obj : projectList){ 
							String projectshortName=(obj[17]!=null)?" ( "+obj[17].toString()+" ) ":"";
						%>
							<option value="<%=obj[0]%>" <%if(projectId!=null && projectId.equals(obj[0].toString())) { %>selected <%} %> ><%=obj[4] +projectshortName%></option>
						<%} %>
					</select>
				</form>
				<a href="EconomicImpact.htm?projectId=<%=projectId%>" class="btn btn-sm back">
					Back
				</a>
			</div>
		</div>
		<div class="card-body">

			<% if (revisionList != null && !revisionList.isEmpty()) { %>

			<form method="get" action="EconomicImpactHistory.htm" id="revisionSelectForm" style="max-width:420px; margin-bottom:20px;">
				<input type="hidden" name="projectId" value="<%=projectId%>" />
				<label class="form-label fw-bold" style="font-size:12px; text-transform:uppercase; letter-spacing:.03em; color:#5B6460;">
					Select Revision
				</label>
				<select class="form-control items" name="revisionNo" onchange="document.getElementById('revisionSelectForm').submit();">
					<%
					for (Object[] row : revisionList) {
						Long revNo = ((Number) row[0]).longValue();
						String date = row[1] != null ? row[1].toString() : "";
						String by = row[2] != null ? row[2].toString() : "";
						String label = "Revision - " + revNo;
						String selected = (selectedRevisionNo != null && selectedRevisionNo.equals(revNo)) ? "selected" : "";
					%>
						<option value="<%=revNo%>" <%=selected%>><%=label%></option>
					<%
					}
					%>
				</select>
			</form>

			<div style="margin-bottom:16px;">
				<span class="text-muted" style="font-size:12px;">
					Revision <%=selectedRevisionNo%> &middot; <%=revisedDate%> by <%=revisedBy%>
				</span>
			</div>

			<div class="economic-impact-grid">
				<div class="economic-impact-field">
					<label class="form-label fw-bold">a) Indigenous Content, Foreign Dependency &amp; Indigenization Efforts</label>
					<div class="economic-impact-readonly"><%=StringEscapeUtils.escapeHtml4(indigenousContent)%></div>
				</div>

				<div class="economic-impact-field">
					<label class="form-label fw-bold">b) International Collaborations Executed</label>
					<div class="economic-impact-readonly"><%=StringEscapeUtils.escapeHtml4(internationalCollaboration)%></div>
				</div>

				<div class="economic-impact-field">
					<label class="form-label fw-bold">c) Intellectual Property Rights Generated</label>
					<div class="economic-impact-readonly"><%=StringEscapeUtils.escapeHtml4(intellectualProperty)%></div>
				</div>

				<div class="economic-impact-field">
					<label class="form-label fw-bold">d) Export Potential, if any</label>
					<div class="economic-impact-readonly"><%=StringEscapeUtils.escapeHtml4(exportPotential)%></div>
				</div>

				<div class="economic-impact-field">
					<label class="form-label fw-bold">e) Infrastructure Created, if any</label>
					<div class="economic-impact-readonly"><%=StringEscapeUtils.escapeHtml4(infrastructureCreated)%></div>
				</div>
			</div>

			<% }else { %>

			<div class="text-center p-5 text-muted">
				<i class="fa fa-info-circle fa-2x mb-3 text-light"></i>
				<p class="mb-0">No Revision Yet</p>
			</div>

			<%} %>
		</div>
	</div>
</div>

<script type="text/javascript">
$('.items').select2();
</script>
</body>
</html>
