<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%!
	private String esc(Object o, String def) {
		return (o == null || o.toString().trim().isEmpty()) ? def : StringEscapeUtils.escapeHtml4(o.toString());
	}

	private String fmt(SimpleDateFormat sdf, Object d) {
		try { return d == null ? "-" : sdf.format(d); } catch (Exception e) { return "-"; }
	}

	private int pct(Object o) {
		try {
			int v = (int) Math.round(Double.parseDouble(o.toString().trim()));
			return Math.max(0, Math.min(100, v));
		} catch (Exception e) { return 0; }
	}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Milestone Activity Update</title>
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/milestone/milestoneReports.css" var="milestoneReports" />
<link href="${milestoneReports}" rel="stylesheet" />

<style>
	.ma-page { padding: 15px 20px; }

	/* ---- title bar ---- */
	.ma-title {
		background: #1a5799; color: #fff; text-align: center;
		margin: 0; padding: 10px 15px; font-size: 1.15rem; font-weight: 600;
		border-radius: 6px 6px 0 0;
	}

	/* ---- table ---- */
	.ma-table {
		width: 100%; table-layout: fixed; border-collapse: collapse;
		background: #fff; margin-bottom: 0; font-size: .9rem;
	}
	.ma-table th, .ma-table td {
		border: 1px solid #d3dbe6; padding: 9px 10px; vertical-align: middle;
	}
	.ma-table thead th {
		background: #e8eef7; color: #1a3a63; font-weight: 700;
		text-align: center; white-space: nowrap;
	}
	.ma-table thead tr.top th { background: #d5e1f1; }
	.ma-table tbody td.c { text-align: center; }
	.ma-table tbody tr:hover td { background: #f5f9ff; }

	/* ---- level rows ---- */
	.ma-table tr.lvl-M td { background: #eaf2fc; font-weight: 600; }
	.ma-table tr.lvl-M:hover td { background: #e0ecfa; }

	.ma-table td.nm { word-wrap: break-word; }
	.ma-table tr.lvl-A td.nm { padding-left: 28px; }
	.ma-table tr.lvl-B td.nm { padding-left: 50px; }
	.ma-table tr.lvl-C td.nm { padding-left: 72px; }

	.ma-table tr.lvl-A td.lv { border-left: 4px solid #0e9aa7; }
	.ma-table tr.lvl-B td.lv { border-left: 4px solid #7c5cd6; }
	.ma-table tr.lvl-C td.lv { border-left: 4px solid #e08a1e; }
	.ma-table tr.lvl-M td.lv { border-left: 4px solid #1a5799; }

	.ma-tag {
		display: inline-block; min-width: 44px; padding: 2px 10px; border-radius: 4px;
		color: #fff; font-size: .8rem; font-weight: 700; text-align: center;
	}
	.lvl-M .ma-tag { background: #1a5799; }
	.lvl-A .ma-tag { background: #0e9aa7; }
	.lvl-B .ma-tag { background: #7c5cd6; }
	.lvl-C .ma-tag { background: #e08a1e; }

	/* ---- progress ---- */
	.ma-progress { height: 20px; background: #e3e9f2; border-radius: 4px; overflow: hidden; }
	.ma-progress span {
		display: block; height: 100%; min-width: 32px; background: #1a5799; color: #fff;
		font-size: .75rem; font-weight: 700; line-height: 20px; text-align: center;
	}
	.ma-progress span.done { background: #1f9d55; }
	.ma-none {
		display: block; height: 20px; line-height: 20px; border-radius: 4px;
		background: #e3e6ea; color: #444; font-size: .75rem; font-weight: 700; text-align: center;
	}

	.ma-status { display: inline-block; padding: 2px 10px; border-radius: 4px; background: #fff3dc; color: #8a5300; font-size: .8rem; font-weight: 600; }
	.ma-done   { display: inline-block; padding: 2px 10px; border-radius: 4px; background: #e1f5e9; color: #17733f; font-size: .8rem; font-weight: 600; }

	.ma-edit { background: #ffa733; border: 0; border-radius: 4px; padding: 5px 12px; color: #222; cursor: pointer; }
	.ma-edit:hover { background: #f59210; }

	.ma-empty td { text-align: center; padding: 30px !important; color: #777; }
</style>
</head>

<%
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	Object[] getMA = (Object[]) request.getAttribute("MilestoneActivity");
	String projectid = (String) request.getAttribute("projectid");
	List<Object[]> MilestoneActivityA = (List<Object[]>) request.getAttribute("MilestoneActivityA");
	String maId = getMA != null ? String.valueOf(getMA[0]) : "0";

	/* row = { level, label, name, progress, from, to, completedOn, statusText, activityId, showUpdate } */
	List<Object[]> rows = new ArrayList<Object[]>();

	if (getMA != null) {
		boolean mainLeaf = (MilestoneActivityA == null || MilestoneActivityA.isEmpty());
		rows.add(new Object[] { "M", "Main", getMA[4], getMA[11], getMA[2], getMA[3], getMA[14], getMA[15], getMA[0], mainLeaf });
	}

	if (MilestoneActivityA != null) {
		int countA = 1;
		for (Object[] a : MilestoneActivityA) {
			List<Object[]> listB = (List<Object[]>) request.getAttribute("MilestoneActivityB" + countA);
			boolean aLeaf = (listB == null || listB.isEmpty());
			rows.add(new Object[] { "A", "A-" + countA, a[4], a[5], a[2], a[3], a[8], a[9], a[0], aLeaf });

			if (listB != null) {
				int countB = 1;
				for (Object[] b : listB) {
					List<Object[]> listC = (List<Object[]>) request.getAttribute(countA + "MilestoneActivityC" + countB);
					boolean bLeaf = (listC == null || listC.isEmpty());
					rows.add(new Object[] { "B", "B-" + countB, b[4], b[5], b[2], b[3], b[8], b[9], b[0], bLeaf });

					if (listC != null) {
						int countC = 1;
						for (Object[] c : listC) {
							rows.add(new Object[] { "C", "C-" + countC, c[4], c[5], c[2], c[3], c[8], c[9], c[0], true });
							countC++;
						}
					}
					countB++;
				}
			}
			countA++;
		}
	}
%>
<body>

	<nav class="navbar navbar-light bg-light m01">
		<a class="navbar-brand"></a>
		<form class="form-inline" method="POST" action="MilestoneReports.htm">
			<input type="submit" class="btn btn-primary btn-sm back ml-1" value="Back">
			<input type="hidden" name="MilestoneActivityId" value="<%=maId%>" />
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
		</form>
	</nav>

	<%
		String ses = request.getParameter("result");
		String ses1 = request.getParameter("resultfail");
		if (ses1 != null) {
	%>
	<div align="center"><div class="alert alert-danger" role="alert"><%=StringEscapeUtils.escapeHtml4(ses1)%></div></div>
	<%
		}
		if (ses != null) {
	%>
	<div align="center"><div class="alert alert-success" role="alert"><%=StringEscapeUtils.escapeHtml4(ses)%></div></div>
	<%
		}
	%>

	<div class="container-fluid ma-page">

		<h5 class="ma-title"><%=getMA != null ? esc(getMA[1], "-") : "-"%> Milestone Activity Details</h5>

		<table class="ma-table">
			<colgroup>
				<col style="width: 80px;">
				<col>
				<col style="width: 150px;">
				<col style="width: 110px;">
				<col style="width: 110px;">
				<col style="width: 150px;">
				<col style="width: 80px;">
			</colgroup>
			<thead>
				<tr class="top">
					<th rowspan="2">Level</th>
					<th rowspan="2">Activity Name</th>
					<th rowspan="2">Progress</th>
					<th colspan="2">PDC</th>
					<th rowspan="2">Completed On</th>
					<th rowspan="2">Update</th>
				</tr>
				<tr>
					<th>From</th>
					<th>To</th>
				</tr>
			</thead>
			<tbody>
				<%
					if (rows.isEmpty()) {
				%>
				<tr class="ma-empty"><td colspan="7">No activity details available.</td></tr>
				<%
					}
					for (Object[] r : rows) {
						String lvl = (String) r[0];
						int p = pct(r[3]);
						boolean showUpdate = (Boolean) r[9];
				%>
				<tr class="lvl-<%=lvl%>">
					<td class="c lv"><span class="ma-tag"><%=r[1]%></span></td>
					<td class="nm"><%=esc(r[2], " - ")%></td>
					<td class="c">
						<%if (p > 0) {%>
						<div class="ma-progress"><span class="<%=p == 100 ? "done" : ""%>" style="width:<%=p%>%"><%=p%>%</span></div>
						<%} else {%>
						<span class="ma-none">Not Started</span>
						<%}%>
					</td>
					<td class="c"><%=fmt(sdf, r[4])%></td>
					<td class="c"><%=fmt(sdf, r[5])%></td>
					<td class="c">
						<%if (r[6] != null) {%>
						<span class="ma-done"><%=fmt(sdf, r[6])%></span>
						<%} else {%>
						<span class="ma-status"><%=esc(r[7], " - ")%></span>
						<%}%>
					</td>
					<td class="c">
						<%if (showUpdate) {%>
						<form method="POST" action="M-A-Update.htm" class="m-0">
							<button class="ma-edit" type="submit" title="Update"><i class="fa fa-wrench" aria-hidden="true"></i></button>
							<input type="hidden" name="ProjectId" value="<%=projectid%>" />
							<input type="hidden" name="MilestoneActivityId" value="<%=maId%>" />
							<input type="hidden" name="ActivityId" value="<%=r[8]%>" />
							<input type="hidden" name="ActivityType" value="<%=lvl%>" />
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						</form>
						<%}%>
					</td>
				</tr>
				<%
					}
				%>
			</tbody>
		</table>

	</div>

</body>
</html>
