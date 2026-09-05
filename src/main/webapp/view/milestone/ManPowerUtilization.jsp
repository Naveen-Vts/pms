<%@page import="java.time.LocalDate"%>
<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.util.List"%>
<%@page import="java.time.DayOfWeek"%>
<%@page import="java.time.YearMonth"%>
<%@page import="java.time.temporal.ChronoUnit"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <jsp:include page="../static/header.jsp"></jsp:include>
    <spring:url value="/resources/css/milestone/ManpowerUtilization.css" var="resourceCss" />
    <link href="${resourceCss}" rel="stylesheet" />
    <title>Project Resource Utilization</title>
    <%
        List<Object[]> projectList = (List<Object[]>) request.getAttribute("projectList");
        List<String> finYearList = (List<String>) request.getAttribute("finYearList");
        List<String> quarterList = (List<String>) request.getAttribute("quarters");
        Object[] manpowerData =  (Object[]) request.getAttribute("manpowerData");
        List<Object[]> infrastructureItems = (List<Object[]>) request.getAttribute("infrastructureItems");
        List<Object[]> trainingItems = (List<Object[]>) request.getAttribute("trainingItems");

        String projectId =  (String) request.getAttribute("projectId");
        String finYear =  (String) request.getAttribute("finYear");
        String quarter =  (String) request.getAttribute("quarter");

        String activeTab = (String) request.getAttribute("activeTab");
        if (activeTab == null) activeTab = "manpower";

        boolean manpowerTabActive = "manpower".equals(activeTab);
        boolean infrastructureTabActive = "infrastructure".equals(activeTab);
        boolean trainingTabActive = "training".equals(activeTab);

        long revisionNo = 0;
        long sciCount = 0, techCount = 0, admCount = 0;
        long sciDaysCount = 0, techDaysCount = 0, admDaysCount = 0;

        if (manpowerData != null) {
            revisionNo     = manpowerData.length > 4  && manpowerData[4]  != null ? ((Number) manpowerData[4]).longValue()  : 0;
            sciCount       = manpowerData.length > 5  && manpowerData[5]  != null ? ((Number) manpowerData[5]).longValue()  : 0;
            techCount      = manpowerData.length > 6  && manpowerData[6]  != null ? ((Number) manpowerData[6]).longValue()  : 0;
            admCount       = manpowerData.length > 7  && manpowerData[7]  != null ? ((Number) manpowerData[7]).longValue()  : 0;
            sciDaysCount   = manpowerData.length > 8  && manpowerData[8]  != null ? ((Number) manpowerData[8]).longValue()  : 0;
            techDaysCount  = manpowerData.length > 9  && manpowerData[9]  != null ? ((Number) manpowerData[9]).longValue()  : 0;
            admDaysCount   = manpowerData.length > 10 && manpowerData[10] != null ? ((Number) manpowerData[10]).longValue() : 0;
        }

        String manpowerMode = (manpowerData == null) ? "ADD" : (revisionNo < 1 ? "EDIT" : "REVISE");
        // boolean manpowerReadOnly = !"ADD".equals(manpowerMode);
        boolean manpowerReadOnly = false;

        String infraMode = (String) request.getAttribute("infraMode");
        if (infraMode == null) infraMode = "ADD";
        // boolean infraReadOnly = !"ADD".equals(infraMode);
		boolean infraReadOnly = false;
        boolean infraRevision  = "REVISE".equals(infraMode);

        String trainMode = (String) request.getAttribute("trainMode");
        if (trainMode == null) trainMode = "ADD";
        // boolean trainReadOnly = !"ADD".equals(trainMode);
        boolean trainReadOnly = false;
        boolean trainRevision  = "REVISE".equals(trainMode);

        boolean isCurrentFinYear = false;

        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentStartYear = today.getMonthValue() >= 4 ? currentYear : currentYear - 1;
        int finYearStartYear = Integer.parseInt(finYear.substring(0, 4));
        if (finYearStartYear == currentStartYear) {
            isCurrentFinYear = true;
        }

        String actualCurrentFinYear = currentStartYear + "-" + String.valueOf(currentStartYear + 1).substring(2);
        int todayMonth = today.getMonthValue();
        int actualCurrentQuarterNum;
        if (todayMonth >= 4 && todayMonth <= 6) actualCurrentQuarterNum = 1;
        else if (todayMonth >= 7 && todayMonth <= 9) actualCurrentQuarterNum = 2;
        else if (todayMonth >= 10 && todayMonth <= 12) actualCurrentQuarterNum = 3;
        else actualCurrentQuarterNum = 4;
        
        
        int fYearStart = Integer.parseInt(finYear.substring(0, 4));
        int fYearEnd = fYearStart + 1;

        LocalDate qStartDate = null;
        LocalDate qEndDate = null;

        if ("Q1".equals(quarter)) {
            qStartDate = LocalDate.of(fYearStart, 4, 1);
            qEndDate = LocalDate.of(fYearStart, 6, 30);
        } else if ("Q2".equals(quarter)) {
            qStartDate = LocalDate.of(fYearStart, 7, 1);
            qEndDate = LocalDate.of(fYearStart, 9, 30);
        } else if ("Q3".equals(quarter)) {
            qStartDate = LocalDate.of(fYearStart, 10, 1);
            qEndDate = LocalDate.of(fYearStart, 12, 31);
        } else if ("Q4".equals(quarter)) {
            qStartDate = LocalDate.of(fYearEnd, 1, 1);
            qEndDate = YearMonth.of(fYearEnd, 3).atEndOfMonth();
        }

        long maxDaysInQuarter = 0;
        long workingDaysInQuarter = 0;

        if (qStartDate != null && qEndDate != null) {
            maxDaysInQuarter = ChronoUnit.DAYS.between(qStartDate, qEndDate) + 1;
            for (LocalDate date = qStartDate; !date.isAfter(qEndDate); date = date.plusDays(1)) {
                DayOfWeek day = date.getDayOfWeek();
                if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                    workingDaysInQuarter++;
                }
            }
        }
    %>
</head>
<body>
<%
    String successMessage = (String) request.getAttribute("result");
    String errorMessage = (String) request.getAttribute("resultfail");
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
    <div class="card shadow-nohover resource-card">
        <div class="card-header resource-header">
            <h3 class="page-title">Project Resource Utilization</h3>
        </div>
        <div class="card-body">
            <div class="filter-section">
                <form method="post" action="ValuationOfTechnologies.htm" id="projectchange">
                    <div class="row">
                        <div class="col-md-4">
                            <div class="filter-label">Project</div>
                            <select class="form-control items" name="projectId" required onchange="submitForm('projectchange');">
                                <%  if (projectList != null) {
                                        for (Object[] obj : projectList) {
                                            String projectShortName = (obj[17] != null) ? " (" + obj[17] + ") " : "";
                                            String selected = "";
                                            if (projectId != null && projectId.equals(obj[0].toString())) {
                                                selected = "selected";
                                            }
                                %>
                                <option value="<%=obj[0]%>" <%=selected%>> <%=obj[4] + projectShortName%> </option>
                                <% }} %>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <div class="filter-label">Financial Year</div>
                            <select class="form-control items" name="finYear" required onchange="submitForm('projectchange');">
                                <% LocalDate today1 = LocalDate.now();
                                int currentYear1 = today1.getYear();
                                int currentFinYearStartYear1 = today1.getMonthValue() >= 4 ? currentYear1 : currentYear1 - 1;

                                if (finYearList != null) {
                                    for (String year : finYearList) {
                                        String selected = "";
                                        String disabled = "";
                                        int finYearStartYear1 = Integer.parseInt(year.substring(0, 4));

                                        if (finYear != null && finYear.equalsIgnoreCase(year)) {
                                            selected = "selected";
                                        }

                                        if (finYearStartYear1 > currentFinYearStartYear1) {
                                            disabled = "disabled";
                                        }
                                %>
                                <option value="<%=year%>" <%=selected%> <%=disabled%> > <%=year%> </option>
                                <% } } %>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <div class="filter-label">Quarter</div>
                            <select class="form-control items" name="quarter" required onchange="submitForm('projectchange');">
                                <%
                                    for (String q : quarterList) {
                                        String label = "";
                                        if ("Q1".equals(q)) {
                                            label = "Q1 - APR - JUN";
                                        } else if ("Q2".equals(q)) {
                                            label = "Q2 - JUL - SEP";
                                        } else if ("Q3".equals(q)) {
                                            label = "Q3 - OCT - DEC";
                                        } else if ("Q4".equals(q)) {
                                            label = "Q4 - JAN - MAR";
                                        }
                                %>
                                    <option value="<%=q%>" <%=q.equalsIgnoreCase(quarter) ? "selected" : ""%>> <%=label%> </option>
                                <% } %>
                            </select>
                        </div>
                    </div>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                    <input type="hidden" name="activeTab" value="<%=activeTab%>" />
                </form>
            </div>

            <ul class="nav nav-tabs resource-tabs" id="resourceTabs" role="tablist">
                <li class="nav-item">
                    <a class="nav-link <%=manpowerTabActive ? "active" : ""%>" id="manpower-tab" data-toggle="tab" href="#manpower" role="tab">
                        Manpower Utilization
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link <%=infrastructureTabActive ? "active" : ""%>" id="infrastructure-tab" data-toggle="tab" href="#infrastructure" role="tab">
                        Infrastructure Used
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link <%=trainingTabActive ? "active" : ""%>" id="training-tab" data-toggle="tab" href="#training" role="tab">
                        Training Cost
                    </a>
                </li>
            </ul>

            <div class="tab-content tab-content-wrapper" id="resourceTabContent">

                <!------------------------------ Manpower Tab --------------------------------------->
                <div class="tab-pane fade <%=manpowerTabActive ? "show active" : ""%>" id="manpower" role="tabpanel">
                    <div class="topic-header" style="display:flex; justify-content:space-between; align-items:center;">
                        <div><h4 class="topic-title">Manpower Utilization</h4></div>
                        <button type="button" class="btn btn-sm btn-outline-secondary" onclick="openRevisionHistory('MANPOWER')">
                            <i class="fa fa-history"></i> View Revision History
                        </button>
                    </div>
                    <form method="post" action="SaveManpowerUtilization.htm" id="manpowerForm">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <input type="hidden" name="projectId" value="<%=projectId%>" />
                        <input type="hidden" name="finYear" value="<%=finYear%>" />
                        <input type="hidden" name="quarter" value="<%=quarter%>" />
                        <input type="hidden" name="activeTab" value="manpower" />
                        <input type="hidden" name="mode" id="manpowerModeField" value="<%=manpowerMode%>" />

                        <% if (manpowerReadOnly) { %>
                        <div class="action-section" style="justify-content:flex-start; margin-bottom:10px;">
                            <button type="button" class="btn btn-sm btn-outline-secondary" id="manpowerUnlockBtn">
                                <i class="fa fa-pencil"></i> <%="EDIT".equals(manpowerMode) ? "Edit" : "Revise"%>
                            </button>
                            <span id="manpowerLockedNote" class="text-muted" style="font-size:12px; margin-left:8px;">
                                Locked &mdash; click <%="EDIT".equals(manpowerMode) ? "Edit" : "Revise"%> to make changes
                            </span>
                        </div>
                        <% } %>

                        <table class="table table-bordered data-table">
                            <thead>
                                <tr>
                                    <th>Design Cadre</th>
                                    <th style="width:180px;">Manpower Count</th>
                                    <th style="width:250px;">Manpower Utilisation In Days</th>
                                    <th style="width:250px;">Man-Days (Count &times; Days)</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td>Scientist</td>
                                    <td><input type="number" min="0" class="form-control manpower-lockable" id="sciCount" name="sciCount" value="<%=sciCount%>" <%=manpowerReadOnly ? "readonly" : ""%> /></td>
                                    <td><input type="number" min="0" class="form-control manpower-lockable" id="sciDaysCount" name="sciDaysCount"  value="<%= ("ADD".equals(manpowerMode) && sciDaysCount == 0) ? workingDaysInQuarter : sciDaysCount %>" <%=manpowerReadOnly ? "readonly" : ""%> /></td>
                                    <td><input type="text" class="form-control" id="sciTotal" value="<%=sciCount * sciDaysCount%>" readonly tabindex="-1" style="background:#F5F5F5; font-weight:600; text-align:right;" /></td>
                                </tr>
                                <tr>
                                    <td>Technical</td>
                                    <td><input type="number" min="0" class="form-control manpower-lockable" id="techCount" name="techCount" value="<%=techCount%>" <%=manpowerReadOnly ? "readonly" : ""%> /></td>
                                    <td><input type="number" min="0" class="form-control manpower-lockable" id="techDaysCount" name="techDaysCount" value="<%= ("ADD".equals(manpowerMode) && techDaysCount == 0) ? workingDaysInQuarter : techDaysCount %>" <%=manpowerReadOnly ? "readonly" : ""%> /></td>
                                    <td><input type="text" class="form-control" id="techTotal" value="<%=techCount * techDaysCount%>" readonly tabindex="-1" style="background:#F5F5F5; font-weight:600; text-align:right;" /></td>
                                </tr>
                                <tr>
                                    <td>Admin &amp; Allied</td>
                                    <td><input type="number" min="0" class="form-control manpower-lockable" id="admCount" name="admCount" value="<%=admCount%>" <%=manpowerReadOnly ? "readonly" : ""%> /></td>
                                    <td><input type="number" min="0" class="form-control manpower-lockable" id="admDaysCount" name="admDaysCount" value="<%= ("ADD".equals(manpowerMode) && admDaysCount == 0) ? workingDaysInQuarter : admDaysCount %>" <%=manpowerReadOnly ? "readonly" : ""%> /></td>
                                    <td><input type="text" class="form-control" id="admTotal" value="<%=admCount * admDaysCount%>" readonly tabindex="-1" style="background:#F5F5F5; font-weight:600; text-align:right;" /></td>
                                </tr>
                            </tbody>
                        </table>
                        <div class="action-section">
                            <button type="reset" class="btn btn-sm viewall manpower-lockable" <%=manpowerReadOnly ? "disabled" : ""%>>Reset</button>
                            <% if ("ADD".equals(manpowerMode)) { %>
                                <button type="submit" class="btn btn-sm submit manpower-lockable" id="manpowerSubmitBtn" onclick="$('#manpowerModeField').val('ADD'); return confirm('Are you Sure to Submit ? ')">Submit</button>
                            <% } else {
                                if ("EDIT".equals(manpowerMode)) { %>
                                    <button type="submit" formaction="UpdateManPowerUtilization.htm" class="btn btn-sm edit manpower-lockable" <%=manpowerReadOnly ? "disabled" : ""%> onclick="$('#manpowerModeField').val('EDIT'); return confirm('Are you Sure to Edit ? ')">Edit</button>
                                <% } %>
                                <button type="submit" formaction="UpdateManPowerUtilization.htm" class="btn btn-sm revise manpower-lockable" <%=manpowerReadOnly ? "disabled" : ""%> onclick="$('#manpowerModeField').val('REVISE'); return confirm('Are you Sure to Revise ? ')">Revise</button>
                            <% } %>
                        </div>
                    </form>
                </div>

                <!--------------------------- Infrastructure Tab ----------------------------------->
                <div class="tab-pane fade <%=infrastructureTabActive ? "show active" : ""%>" id="infrastructure" role="tabpanel">
                    <div class="topic-header" style="display:flex; justify-content:space-between; align-items:center;">
                        <div><h4 class="topic-title">Infrastructure Used</h4></div>
                        <button type="button" class="btn btn-sm btn-outline-secondary" onclick="openRevisionHistory('INFRASTRUCTURE')">
                            <i class="fa fa-history"></i> View Revision History
                        </button>
                    </div>
                    <form method="post" action="SaveInfrastructureUsed.htm" id="infrastructureForm">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <input type="hidden" name="projectId" value="<%=projectId%>" />
                        <input type="hidden" name="finYear" value="<%=finYear%>" />
                        <input type="hidden" name="quarter" value="<%=quarter%>" />
                        <input type="hidden" name="activeTab" value="infrastructure" />
                        <input type="hidden" name="mode" id="infraModeField" value="<%=infraMode%>" />

                        <% if (infraReadOnly) { %>
                        <div class="action-section" style="justify-content:flex-start; margin-bottom:10px;">
                            <button type="button" class="btn btn-sm btn-outline-secondary" id="infraUnlockBtn">
                                <i class="fa fa-pencil"></i> <%="EDIT".equals(infraMode) ? "Edit" : "Revise"%>
                            </button>
                            <span id="infraLockedNote" class="text-muted" style="font-size:12px; margin-left:8px;">
                                Locked &mdash; click <%="EDIT".equals(infraMode) ? "Edit" : "Revise"%> to make changes
                            </span>
                        </div>
                        <% } %>
 						<% if ("ADD".equals(infraMode)) { %>
	                        <div class="d-flex align-items-center" style="margin-bottom:12px;">
	                            <button type="button" class="btn btn-sm btn-outline-secondary infra-lockable" id="openCopyInfraBtn" <%=infraReadOnly ? "disabled" : ""%> onclick="openCopyModal('INFRASTRUCTURE')">
	                                <i class="fa fa-copy"></i> Copy from Quarter
	                            </button>
	                        </div>
                        <% } %>

                        <table class="table table-bordered data-table">
                            <thead>
                                <tr>
                                    <th> Infrastructure Name </th>
                                    <th style="width:180px;"> Days Utilized </th>
                                    <th style="width:60px;">
                                        <button type="button" class="btn btn-sm btn-outline-primary infra-lockable" id="addInfraRow" <%=infraReadOnly ? "disabled" : ""%>><i class="fa fa-plus"></i></button>
                                    </th>
                                </tr>
                            </thead>
                            <tbody id="infraTableBody">
                                <%
                                int infraIdx = 0;
                                String infraFieldAttrs = infraReadOnly ? "readonly" : "";
                                if (infrastructureItems != null && !infrastructureItems.isEmpty()) {
                                    for (Object[] infraRow : infrastructureItems) {
                                        long infraid = ((Number) infraRow[5]).longValue();
                                        String infraName = (String) infraRow[6];
                                        long infraDays = ((Number) infraRow[7]).longValue();
                                %>
                                <tr class="infra-row">
                                    <td>
                                        <input type="hidden" name="items[<%=infraIdx%>].infraUtilizationId" value="<%=infraid %>" />
                                        <input type="text" class="form-control infra-lockable" name="items[<%=infraIdx%>].infraName" value="<%=StringEscapeUtils.escapeHtml4(infraName)%>" placeholder="Enter Infrastructure Name" maxlength="500" required <%=infraFieldAttrs%> />
                                    </td>
                                    <td><input type="number" min="0" class="form-control infra-lockable" name="items[<%=infraIdx%>].daysUtilized" value="<%=infraDays%>" required <%=infraFieldAttrs%> /></td>
                                    <td><button type="button" class="btn btn-sm btn-outline-danger remove-infra-row infra-lockable" <%=(infraReadOnly || infraRevision) ? "disabled" : ""%>><i class="fa fa-minus"></i></button></td>
                                </tr>
                                <% infraIdx++; } } else { %>
                                <!-- <tr class="infra-row">
                                    <td><input type="text" class="form-control infra-lockable" name="items[0].infraName" placeholder="Enter Infrastructure Name" maxlength="500" required /></td>
                                    <td><input type="number" min="0" class="form-control infra-lockable" name="items[0].daysUtilized" value="0" required /></td>
                                    <td><button type="button" class="btn btn-sm btn-outline-danger remove-infra-row infra-lockable"><i class="fa fa-minus"></i></button></td>
                                </tr> -->
                                <tr class="infra-row">
								    <td><input type="text" class="form-control infra-lockable" name="items[0].infraName" placeholder="Enter Infrastructure Name" maxlength="500" required /></td>
								    <!-- CHANGE 0 TO workingDaysInQuarter BELOW -->
								    <td><input type="number" min="0" class="form-control infra-lockable" name="items[0].daysUtilized" value="<%=workingDaysInQuarter%>" required /></td>
								    <td><button type="button" class="btn btn-sm btn-outline-danger remove-infra-row infra-lockable"><i class="fa fa-minus"></i></button></td>
								</tr>
                                <% } %>
                            </tbody>
                        </table>

                        <div class="action-section">
                            <button type="reset" class="btn btn-sm viewall infra-lockable" <%=infraReadOnly ? "disabled" : ""%>>Reset</button>
                            <% if ("ADD".equals(infraMode)) { %>
                                <button type="submit" class="btn btn-sm submit infra-lockable" id="infraSubmitBtn" <%=infraReadOnly ? "disabled" : ""%> onclick="$('#infraModeField').val('ADD'); return confirm('Are you Sure to Submit ? ')">Submit</button>
                            <% } else {
                                if ("EDIT".equals(infraMode)) { %>
                                    <button type="submit" class="btn btn-sm edit infra-lockable" <%=infraReadOnly ? "disabled" : ""%> onclick="$('#infraModeField').val('EDIT'); return confirm('Are you Sure to Edit ? ')">Edit</button>
                                <% } %>
                                <button type="submit" class="btn btn-sm revise infra-lockable" <%=infraReadOnly ? "disabled" : ""%> onclick="$('#infraModeField').val('REVISE'); return confirm('Are you Sure to Revise ? ')">Revise</button>
                            <% } %>
                        </div>
                    </form>
                </div>

                <!----------------------------------------- Training Cost Tab ----------------------------------->
                <div class="tab-pane fade <%=trainingTabActive ? "show active" : ""%>" id="training" role="tabpanel">
                    <div class="topic-header" style="display:flex; justify-content:space-between; align-items:center;">
                        <div><h4 class="topic-title">Training Cost</h4></div>
                        <button type="button" class="btn btn-sm btn-outline-secondary" onclick="openRevisionHistory('TRAINING')">
                            <i class="fa fa-history"></i> View Revision History
                        </button>
                    </div>
                    <form method="post" action="SaveTrainingCost.htm" id="trainingForm">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <input type="hidden" name="projectId" value="<%=projectId%>" />
                        <input type="hidden" name="finYear" value="<%=finYear%>" />
                        <input type="hidden" name="quarter" value="<%=quarter%>" />
                        <input type="hidden" name="activeTab" value="training" />
                        <input type="hidden" name="mode" id="trainModeField" value="<%=trainMode%>" />

                        <% if (trainReadOnly) { %>
                        <div class="action-section" style="justify-content:flex-start; margin-bottom:10px;">
                            <button type="button" class="btn btn-sm btn-outline-secondary" id="trainUnlockBtn">
                                <i class="fa fa-pencil"></i> <%="EDIT".equals(trainMode) ? "Edit" : "Revise"%>
                            </button>
                            <span id="trainLockedNote" class="text-muted" style="font-size:12px; margin-left:8px;">
                                Locked &mdash; click <%="EDIT".equals(trainMode) ? "Edit" : "Revise"%> to make changes
                            </span>
                        </div>
                        <% } %>
						<% if ("ADD".equals(trainMode)) { %>
	                        <div class="d-flex align-items-center" style="margin-bottom:12px;">
	                            <button type="button" class="btn btn-sm btn-outline-secondary train-lockable" id="openCopyTrainBtn" <%=trainReadOnly ? "disabled" : ""%> onclick="openCopyModal('TRAINING')">
	                                <i class="fa fa-copy"></i> Copy from Quarter
	                            </button>
	                        </div>
						<% } %>
                        <table class="table table-bordered data-table">
                            <thead>
                                <tr>
                                    <th> Training Name </th>
                                    <th style="width:200px;"> Training Cost </th>
                                    <th style="width:60px;">
                                        <button type="button" class="btn btn-sm btn-outline-primary train-lockable" id="addTrainRow" <%=trainReadOnly ? "disabled" : ""%>><i class="fa fa-plus"></i></button>
                                    </th>
                                </tr>
                            </thead>
                            <tbody id="trainTableBody">
                                <%
                                int trainingIdx = 0;
                                String trainingAttrs = trainReadOnly ? "readonly" : "";
                                if (trainingItems != null && !trainingItems.isEmpty()) {
                                    for (Object[] trainRow : trainingItems) {
                                        long trainid = ((Number) trainRow[5]).longValue();
                                        String trainName = (String) trainRow[6];
                                        double trainCost = trainRow.length > 8 && trainRow[8] != null ? ((Number) trainRow[8]).doubleValue() : 0;
                                        String trainCostDisplay = new java.text.DecimalFormat("#.##").format(trainCost);                               
                                %>
                                <tr class="train-row">
                                    <td>
                                        <input type="hidden" name="items[<%=trainingIdx%>].trainingUtilizationId" value="<%=trainid%>" />
                                        <input type="text" class="form-control train-lockable" name="items[<%=trainingIdx%>].trainingName" value="<%=StringEscapeUtils.escapeHtml4(trainName)%>" placeholder="Enter Training Name" maxlength="500" required <%=trainingAttrs%> />
                                    </td>
                                    <td><input type="number" min="0" class="form-control train-lockable text-right" name="items[<%=trainingIdx%>].cost" value="<%=trainCostDisplay%>" required <%=trainingAttrs%> /></td>
                                    <td><button type="button" class="btn btn-sm btn-outline-danger remove-train-row train-lockable" <%= (trainReadOnly || trainRevision) ? "disabled" : ""%>><i class="fa fa-minus"></i></button></td>
                                </tr>
                                <% trainingIdx++; } } else { %>
                                <tr class="train-row">
                                    <td><input type="text" class="form-control train-lockable" name="items[0].trainingName" placeholder="Enter Training Name" maxlength="500" required /></td>
                                    <td><input type="number" min="0" class="form-control train-lockable" name="items[0].cost" value="0" required /></td>
                                    <td><button type="button" class="btn btn-sm btn-outline-danger remove-train-row train-lockable"><i class="fa fa-minus"></i></button></td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>

                        <div class="action-section">
                            <button type="reset" class="btn btn-sm viewall train-lockable" <%=trainReadOnly ? "disabled" : ""%>>Reset</button>
                            <% if ("ADD".equals(trainMode)) { %>
                                <button type="submit" class="btn btn-sm submit train-lockable" id="trainSubmitBtn" <%=trainReadOnly ? "disabled" : ""%> onclick="$('#trainModeField').val('ADD'); return confirm('Are you Sure to Submit ? ')">Submit</button>
                            <% } else {
                                if ("EDIT".equals(trainMode)) { %>
                                    <button type="submit" class="btn btn-sm edit train-lockable" <%=trainReadOnly ? "disabled" : ""%> onclick="$('#trainModeField').val('EDIT'); return confirm('Are you Sure to Edit ? ')">Edit</button>
                                <% } %>
                                <button type="submit" class="btn btn-sm revise train-lockable" <%=trainReadOnly ? "disabled" : ""%> onclick="$('#trainModeField').val('REVISE'); return confirm('Are you Sure to Revise ? ')">Revise</button>
                            <% } %>
                        </div>
                    </form>
                </div>

            </div>
        </div>
    </div>
</div>

<script type="text/javascript">

    function submitForm(frmid) {
        document.getElementById(frmid).submit();
    }

    $(document).ready(function() {
        $('.items').select2();
    });

    function recalcManpowerRow(countId, daysId, totalId) {
        var count = parseInt($('#' + countId).val(), 10) || 0;
        var days = parseInt($('#' + daysId).val(), 10) || 0;
        $('#' + totalId).val(count * days);
    }

    $(document).on('input', '#sciCount, #sciDaysCount', function() {
        recalcManpowerRow('sciCount', 'sciDaysCount', 'sciTotal');
    });
    $(document).on('input', '#techCount, #techDaysCount', function() {
        recalcManpowerRow('techCount', 'techDaysCount', 'techTotal');
    });
    $(document).on('input', '#admCount, #admDaysCount', function() {
        recalcManpowerRow('admCount', 'admDaysCount', 'admTotal');
    });

    // --- Manpower lock/unlock ---
    $(document).on('click', '#manpowerUnlockBtn', function() {
        $('#manpowerForm .manpower-lockable').prop('readonly', false).prop('disabled', false);
        $('#manpowerLockedNote').text('Unlocked \u2014 make your changes and submit');
        $(this).prop('disabled', true);
    });

    $(document).on('reset', '#manpowerForm', function() {
        setTimeout(function() {
            recalcManpowerRow('sciCount', 'sciDaysCount', 'sciTotal');
            recalcManpowerRow('techCount', 'techDaysCount', 'techTotal');
            recalcManpowerRow('admCount', 'admDaysCount', 'admTotal');
        }, 0);
    });

    // --- Infrastructure lock/unlock + dynamic rows ---
    $(document).on('click', '#infraUnlockBtn', function() {
        $('#infrastructureForm .infra-lockable').prop('readonly', false).prop('disabled', false);
        $('#infraLockedNote').text('Unlocked \u2014 make your changes and submit');
        $(this).prop('disabled', true);
    });

    $(document).on('click', '#addInfraRow', function() {
        var idx = $('#infraTableBody .infra-row').length;
        var row = $(
            '<tr class="infra-row">' +
                '<td><input type="text" class="form-control infra-lockable" name="items[' + idx + '].infraName" placeholder="Enter Infrastructure Name" maxlength="500" required /></td>' +
                '<td><input type="number" min="0" class="form-control infra-lockable" name="items[' + idx + '].daysUtilized" value="0" required /></td>' +
                '<td><button type="button" class="btn btn-sm btn-outline-danger remove-infra-row infra-lockable"><i class="fa fa-minus"></i></button></td>' +
            '</tr>'
        );
        $('#infraTableBody').append(row);
    });

    $(document).on('click', '.remove-infra-row', function() {
        if ($('#infraTableBody .infra-row').length > 1) {
            $(this).closest('tr').remove();
            reindexInfraRows();
        }
    });

    function reindexInfraRows() {
        $('#infraTableBody .infra-row').each(function(i) {
            $(this).find('input').each(function() {
                var name = $(this).attr('name');
                if (name) {
                    $(this).attr('name', name.replace(/items\[\d+\]/, 'items[' + i + ']'));
                }
            });
        });
    }

    // --- Training lock/unlock + dynamic rows ---
    $(document).on('click', '#trainUnlockBtn', function() {
        $('#trainingForm .train-lockable').prop('readonly', false).prop('disabled', false);
        $('#trainLockedNote').text('Unlocked \u2014 make your changes and submit');
        $(this).prop('disabled', true);
    });

    $(document).on('click', '#addTrainRow', function() {
        var idx = $('#trainTableBody .train-row').length;
        var row = $(
            '<tr class="train-row">' +
                '<td><input type="text" class="form-control train-lockable" name="items[' + idx + '].trainingName" placeholder="Enter Training Name" maxlength="500" required /></td>' +
                '<td><input type="number" min="0" class="form-control train-lockable" name="items[' + idx + '].cost" value="0" required /></td>' +
                '<td><button type="button" class="btn btn-sm btn-outline-danger remove-train-row train-lockable"><i class="fa fa-minus"></i></button></td>' +
            '</tr>'
        );
        $('#trainTableBody').append(row);
    });

    $(document).on('click', '.remove-train-row', function() {
        if ($('#trainTableBody .train-row').length > 1) {
            $(this).closest('tr').remove();
            reindexTrainRows();
        }
    });

    function reindexTrainRows() {
        $('#trainTableBody .train-row').each(function(i) {
            $(this).find('input').each(function() {
                var name = $(this).attr('name');
                if (name) {
                    $(this).attr('name', name.replace(/items\[\d+\]/, 'items[' + i + ']'));
                }
            });
        });
    }

    $('#resourceTabs a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
        var tabId = $(e.target).attr('href').substring(1);
        $('#projectchange input[name="activeTab"]').val(tabId);
    });

    var currentHistoryModule = "";

    function openRevisionHistory(moduleName) {
        var moduleTitle = "";
        if (moduleName === "MANPOWER") moduleTitle = "Manpower Utilization";
        else if (moduleName === "INFRASTRUCTURE") moduleTitle = "Infrastructure Used";
        else if (moduleName === "TRAINING") moduleTitle = "Training Cost";

        currentHistoryModule = moduleName;
        document.getElementById("historyModuleName").innerText = moduleTitle;
        $("#revisionSelect").html('<option>Loading...</option>');
        $("#revisionHistoryBody").html('<p class="text-muted">Choose a revision above to view its data.</p>');
        $("#revisionHistoryModal").modal("show");

        $.ajax({
            url: "GetRevisionList.htm",
            data: {
                projectId: '<%=projectId%>',
                finYear: '<%=finYear%>',
                quarter: '<%=quarter%>',
                type: moduleName
            },
            success: function(revisions) {
                populateRevisionDropdown(revisions);
            },
            error: function() {
                $("#revisionSelect").html('<option>Failed to load</option>');
            }
        });
    }

    function populateRevisionDropdown(revisions) {
        if (!revisions || revisions.length === 0) {
            $("#revisionSelect").html('<option>No revisions yet</option>');
            $("#revisionHistoryBody").html('<p class="text-muted">Nothing has been saved for this quarter yet.</p>');
            return;
        }
        var options = "";
        revisions.forEach(function(rev) {
            var label = "Revision No - " + rev.revisionNo ;
            options += '<option value="' + rev.revisionNo + '">' + label + '</option>';
        });
        $("#revisionSelect").html(options);
        loadRevisionItems(revisions[0].revisionNo);
    }

    $(document).on('change', '#revisionSelect', function() {
        loadRevisionItems($(this).val());
    });

    function loadRevisionItems(revisionNo) {
        $("#revisionHistoryBody").html('<p class="text-muted">Loading...</p>');
        $.ajax({
            url: "GetRevisionItems.htm",
            data: {
                projectId: '<%=projectId%>',
                finYear: '<%=finYear%>',
                quarter: '<%=quarter%>',
                type: currentHistoryModule,
                revisionNo: revisionNo
            },
            success: function(items) {
                renderRevisionItems(items);
            },
            error: function() {
                $("#revisionHistoryBody").html('<p class="text-danger">Failed to load this revision.</p>');
            }
        });
    }
    
    function renderRevisionItems(items) {
        if (!items || items.length === 0) {
            $("#revisionHistoryBody").html(
                '<div class="alert alert-secondary text-center p-4">' +
                    '<i class="fa fa-info-circle fa-2x mb-2 text-muted"></i>' +
                    '<p class="mb-0">No data recorded for this revision.</p>' +
                '</div>'
            );
            return;
        }

        if (currentHistoryModule === "MANPOWER") {
            var mpHtml =
                '<div class="table-responsive mt-3">' +
                    '<table class="table table-hover table-bordered shadow-sm bg-white">' +
                        '<thead class="thead-light">' +
                            '<tr>' +
                                '<th class="align-middle border-bottom-0">Design Cadre</th>' +
                                '<th class="align-middle border-bottom-0 text-center" style="width:120px;">Count</th>' +
                                '<th class="align-middle border-bottom-0 text-center" style="width:120px;">Days</th>' +
                                '<th class="align-middle border-bottom-0 text-center" style="width:140px;">Man-Days (Count &times; Days)</th>' +
                            '</tr>' +
                        '</thead>' +
                        '<tbody>';

            for (var m = 0; m < items.length; m++) {
                var mp = items[m];
                mpHtml +=
                    '<tr>' +
                        '<td class="align-middle text-dark font-weight-bold">' + mp.itemName + '</td>' +
                        '<td class="align-middle text-center">' + mp.itemValue + '</td>' +
                        '<td class="align-middle text-center">' + mp.itemDays + '</td>' +
                        '<td class="align-middle text-center">' +
                            '<span class="badge badge-primary p-2" style="font-size:14px; min-width:60px;">' +
                                mp.itemTotal +
                            '</span>' +
                        '</td>' +
                    '</tr>';
            }

            mpHtml += '</tbody></table></div>';
            $("#revisionHistoryBody").html(mpHtml);
            return;
        }

        var col1 = "Item Name";
        var col2 = "Value";

        if (currentHistoryModule === "INFRASTRUCTURE") {
            col1 = "Infrastructure Name";
            col2 = "Days Utilized";
        } else if (currentHistoryModule === "TRAINING") {
            col1 = "Training Name";
            col2 = "Training Cost (₹)";
        }

        var html =
            '<div class="table-responsive mt-3">' +
                '<table class="table table-hover table-bordered shadow-sm bg-white">' +
                    '<thead class="thead-light">' +
                        '<tr>' +
                            '<th class="align-middle border-bottom-0">' + col1 + '</th>' +
                            '<th class="align-middle border-bottom-0 text-center" style="width: 200px;">' +
                                col2 +
                            '</th>' +
                        '</tr>' +
                    '</thead>' +
                    '<tbody>';

        for (var i = 0; i < items.length; i++) {
            var item = items[i];

            html +=
                '<tr>' +
                    '<td class="align-middle text-dark font-weight-bold">' +
                        item.itemName +
                    '</td>' +
                    '<td class="align-middle text-center">' +
                        '<span class="badge badge-primary p-2" style="font-size: 14px; min-width: 60px;">' +
                            item.itemValue +
                        '</span>' +
                    '</td>' +
                '</tr>';
        }

        html +=
                    '</tbody>' +
                '</table>' +
            '</div>';

        $("#revisionHistoryBody").html(html);
    }
    


</script>

 
 <div class="modal fade" id="revisionHistoryModal" tabindex="-1" role="dialog" aria-labelledby="revisionHistoryModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-scrollable" role="document">
    <div class="modal-content border-0 shadow-lg">
      
      <div class="modal-header bg-primary text-white">
        <h5 class="modal-title font-weight-bold" id="revisionHistoryModalLabel">
            <i class="fa fa-history mr-2"></i> <span id="historyModuleName">Revision History</span>
        </h5>
        <button type="button" class="close text-white" data-dismiss="modal" aria-label="Close" style="opacity: 0.8;">
            <span aria-hidden="true">&times;</span>
        </button>
      </div>
      
      <div class="modal-body p-4 bg-white">
        
        <div class="d-flex align-items-center bg-light p-3 rounded border mb-4 shadow-sm">
            <label for="revisionSelect" class="font-weight-bold text-secondary mb-0 mr-3 text-uppercase" style="font-size: 13px; letter-spacing: 0.5px; min-width: 140px;">
                <i class="fa fa-code-branch mr-1"></i> Select Revision
            </label>
            <select class="form-control custom-select shadow-sm" id="revisionSelect" style="max-width:320px; font-weight: 500;">
            </select>
        </div>
        
        <div id="revisionHistoryBody">
            <div class="text-center p-5 text-muted">
                <i class="fa fa-hand-pointer-o fa-2x mb-3 text-light"></i>
                <p class="mb-0">Choose a revision above to view its data.</p>
            </div>
        </div>
        
      </div>
      
      <div class="modal-footer bg-light border-top-0">
         <button type="button" class="btn btn-secondary shadow-sm" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>

<!-- ===================== Copy from Quarter ===================== -->
<div class="modal fade" id="copyQuarterModal" tabindex="-1" role="dialog" aria-labelledby="copyQuarterModalLabel">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="copyQuarterModalLabel">Copy from Quarter</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
      </div>
      <div class="modal-body">
        <div class="form-group" style="max-width:220px; margin-bottom:14px;">
            <label style="font-size:12px; text-transform:uppercase; letter-spacing:.03em; color:#5B6460;">Financial Year</label>
            <select class="form-control items" id="copyFinYearSelect" data-live-search="true" data-container="body">
                <%
                if (finYearList != null) {
                    for (String year : finYearList) {
                        int yearStart = Integer.parseInt(year.substring(0, 4));
                        String futureDisabled = (yearStart > currentStartYear) ? "disabled" : "";
                %>
                    <option value="<%=year%>" <%=year.equalsIgnoreCase(finYear) ? "selected" : ""%> <%=futureDisabled%>><%=year%></option>
                <%
                    }
                }
                %>
            </select>
        </div>
        <div class="d-flex" style="gap:8px; margin-bottom:16px;" id="copyQuarterPills"></div>
        <div id="copyPreviewArea">
            <p class="text-muted text-center py-4">Select a quarter above to preview its data.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
        <button type="button" class="btn btn-primary" id="confirmCopyBtn" disabled>Copy This Data</button>
      </div>
    </div>
  </div>
</div>

<script type="text/javascript">
    var currentCopyModule = "";   // "INFRASTRUCTURE" or "TRAINING"
    var copySelectedQuarter = "";
    var copyPreviewItems = [];

    function openCopyModal(moduleName) {
        currentCopyModule = moduleName;
        copySelectedQuarter = "";
        copyPreviewItems = [];

        var title = (moduleName === "INFRASTRUCTURE")
            ? "Copy Infrastructure from Quarter"
            : "Copy Training Cost from Quarter";
        $("#copyQuarterModalLabel").text(title);

        if (!$('#copyFinYearSelect').hasClass('select2-hidden-accessible')) {
            $('#copyFinYearSelect').select2({ dropdownParent: $('#copyQuarterModal') });
        }
        $("#copyFinYearSelect").val('<%=finYear%>').trigger('change');

        $("#copyPreviewArea").html('<p class="text-muted text-center py-4">Select a quarter above to preview its data.</p>');
        $("#confirmCopyBtn").prop("disabled", true);
        $("#copyQuarterModal").modal("show");
    }

    function rebuildCopyQuarterPills() {
        var selectedFinYear = $("#copyFinYearSelect").val();
        var quarterNums = { "Q1": 1, "Q2": 2, "Q3": 3, "Q4": 4 };
        var quarters = ["Q1", "Q2", "Q3", "Q4"];
        var pillsHtml = "";

        quarters.forEach(function(q) {
            // can't copy the quarter into itself
            if (selectedFinYear === '<%=finYear%>' && q === '<%=quarter%>') return;
            // can't copy from a quarter that hasn't happened yet
            if (selectedFinYear === '<%=actualCurrentFinYear%>' && quarterNums[q] > <%=actualCurrentQuarterNum%>) return;

            pillsHtml += '<button type="button" class="btn btn-outline-primary flex-fill copy-quarter-pill" data-quarter="' + q + '">' + q + '</button>';
        });

        $("#copyQuarterPills").html(pillsHtml);
        $("#copyPreviewArea").html('<p class="text-muted text-center py-4">Select a quarter above to preview its data.</p>');
        $("#confirmCopyBtn").prop("disabled", true);
        copySelectedQuarter = "";
        copyPreviewItems = [];
    }

    $(document).on('change', '#copyFinYearSelect', function() {
        rebuildCopyQuarterPills();
    });

    $(document).on('click', '.copy-quarter-pill', function() {
        $('.copy-quarter-pill').removeClass('active btn-primary').addClass('btn-outline-primary');
        $(this).removeClass('btn-outline-primary').addClass('active btn-primary');

        copySelectedQuarter = $(this).data('quarter');
        var selectedFinYear = $("#copyFinYearSelect").val();
        $("#copyPreviewArea").html('<p class="text-muted text-center py-4"><i class="fa fa-spinner fa-spin"></i> Loading...</p>');
        $("#confirmCopyBtn").prop("disabled", true);

        var url = (currentCopyModule === "INFRASTRUCTURE") ? "GetInfrastructureForCopy.htm" : "GetTrainingForCopy.htm";

        $.ajax({
            url: url,
            data: {
                projectId: '<%=projectId%>',
                finYear: selectedFinYear,
                sourceQuarter: copySelectedQuarter
            },
            success: function(items) {
                copyPreviewItems = items || [];
                renderCopyPreview(items);
            },
            error: function() {
                $("#copyPreviewArea").html('<p class="text-danger text-center py-4">Failed to load data for this quarter.</p>');
            }
        });
    });

    function renderCopyPreview(items) {
        var selectedFinYear = $("#copyFinYearSelect").val();

        if (!items || items.length === 0) {
            $("#copyPreviewArea").html(
                '<div class="alert alert-secondary text-center mb-0">No data found for ' + selectedFinYear + ' ' + copySelectedQuarter + '.</div>'
            );
            $("#confirmCopyBtn").prop("disabled", true);
            return;
        }

        var isInfra = (currentCopyModule === "INFRASTRUCTURE");
        var col1 = isInfra ? "Infrastructure Name" : "Training Name";
        var col2 = isInfra ? "Days Utilized" : "Cost";

        var html = '<table class="table table-sm table-bordered"><thead><tr><th>' + col1 +
                   '</th><th class="text-center" style="width:140px;">' + col2 + '</th></tr></thead><tbody>';
        items.forEach(function(item) {
            var name = isInfra ? item.infraName : item.trainingName;
            var value = isInfra ? item.daysUtilized : item.cost;
            html += '<tr><td>' + name + '</td><td class="text-center">' + value + '</td></tr>';
        });
        html += '</tbody></table>';
        html += '<p class="text-muted mb-0" style="font-size:12px;">' + items.length +
                ' item(s) from ' + selectedFinYear + ' ' + copySelectedQuarter + ' will replace whatever is currently in the table.</p>';

        $("#copyPreviewArea").html(html);
        $("#confirmCopyBtn").prop("disabled", false);
    }

    $(document).on('click', '#confirmCopyBtn', function() {
        var isInfra = (currentCopyModule === "INFRASTRUCTURE");
        var tableBodyId = isInfra ? '#infraTableBody' : '#trainTableBody';
        var rowClass = isInfra ? 'infra-row' : 'train-row';
        var lockClass = isInfra ? 'infra-lockable' : 'train-lockable';
        var removeClass = isInfra ? 'remove-infra-row' : 'remove-train-row';
        var nameField = isInfra ? 'infraName' : 'trainingName';
        var valueField = isInfra ? 'daysUtilized' : 'cost';

        $(tableBodyId).empty();
        copyPreviewItems.forEach(function(item, idx) {
            var row = $('<tr class="' + rowClass + '"></tr>');
            var nameInput = $('<input type="text" class="form-control ' + lockClass + '" maxlength="500" required>')
                    .attr('name', 'items[' + idx + '].' + nameField).val(item[nameField]);
            var valueInput = $('<input type="number" min="0" class="form-control ' + lockClass + '" required>')
                    .attr('name', 'items[' + idx + '].' + valueField).val(item[valueField]);
            var removeBtn = $('<button type="button" class="btn btn-sm btn-outline-danger ' + removeClass + ' ' + lockClass + '"><i class="fa fa-minus"></i></button>');

            row.append($('<td>').append(nameInput), $('<td>').append(valueInput), $('<td>').append(removeBtn));
            $(tableBodyId).append(row);
        });

        $("#copyQuarterModal").modal("hide");
    });
    
    var MAX_DAYS_IN_QUARTER = <%=maxDaysInQuarter%>;
    var DEFAULT_WORKING_DAYS = <%=workingDaysInQuarter%>;

    $(document).on('change input', '#sciDaysCount, #techDaysCount, #admDaysCount, input[name$=".daysUtilized"]', function() {
        var enteredDays = parseInt($(this).val(), 10);
        
        if (enteredDays > MAX_DAYS_IN_QUARTER) {
            alert("The total days cannot exceed " + MAX_DAYS_IN_QUARTER + " for this quarter.");
            $(this).val(MAX_DAYS_IN_QUARTER); 
            
            if($(this).attr('id')) {
                $(this).trigger('input');
            }
        }
    });
</script>
</body>
</html>
