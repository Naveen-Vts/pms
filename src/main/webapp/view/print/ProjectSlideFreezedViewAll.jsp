<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@page import="com.ibm.icu.text.SimpleDateFormat"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" import="java.util.*"  %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%> 
<%@page import="org.apache.commons.io.FileUtils"%>
<%@page import="java.io.File"%>
<%@page import="com.vts.pfms.model.LabMaster"%>
 
 <%@page import="java.nio.file.Path"%>
 <%@page import="java.io.File"%>
<%@page import="java.nio.file.Paths"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/dependancy.jsp"></jsp:include>
<spring:url value="/resources/css/slides-style.css" var="SlidesStyleCSS" />
<link href="${SlidesStyleCSS}" rel="stylesheet" />
<spring:url value="/resources/css/print/projectSlideFreezedViewAll.css" var="projectSlideFreezedViewAll" />     
<link href="${projectSlideFreezedViewAll}" rel="stylesheet" />
<title>Project Master Slides</title>


</head>
<body class="slides-container clx-64" id="slides-container">
<%
LabMaster labInfo=(LabMaster)request.getAttribute("labInfo");
String lablogo = (String)request.getAttribute("lablogo");
String Drdologo = (String)request.getAttribute("Drdologo");
String filePath = (String)request.getAttribute("filepath");
List<Object[]> freezedproject = (List<Object[]>)request.getAttribute("getAllProjectSlidedata");
//List<Object[]> FreezedSlide = (List<Object[]>)request.getAttribute("getAllProjectSlidesdata"); //status ,  slide , ImageName , path ,SlideId ,attachmentname, brief
List<Object[]> projects = (List<Object[]>)request.getAttribute("getAllProjectdata");
String[] a = new String[projects.size()];
for(int i=0;i<projects.size();i++)
{a[i]=projects.get(i)[0].toString();}
String filepath = (String)request.getAttribute("filepath");
SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
NFormatConvertion nfc=new NFormatConvertion();
String reviewedby = "";
String reviewDate = "";
FormatConverter fc = new FormatConverter();
List<Object[]> mainProjectList =  projects!=null && projects.size()>0 ? (projects.stream().filter(e-> e[21]!=null && e[21].toString().equals("1")).collect(Collectors.toList())): new ArrayList<Object[]>();
List<Object[]> subProjectList =  projects!=null && projects.size()>0 ? (projects.stream().filter(e-> e[21]!=null && e[21].toString().equals("0")).collect(Collectors.toList())): new ArrayList<Object[]>();
projects.clear();
projects.addAll(mainProjectList);
projects.addAll(subProjectList);

int pageCOunt=1;
String labcode = (String) session.getAttribute("labcode");
%>

<div id="presentation-slides" class="carousel slide " data-ride="carousel">
	<div class="carousel-inner" align="center">
		<!-- ---------------------------------------- P-0  Div ----------------------------------------------------- -->
<div class="carousel-item active">
    <div class="content-box">
        <div class="firstpage">
            <div class="mt-2 text-center">
                <h2 class="heading">Presentation</h2>
            </div>
            <div class="text-center">
                <h2 class="heading">of</h2>
            </div>
            <div class="text-center">
                <h2 class="heading">
                    <% if(labInfo!=null && labInfo.getLabCode() !=null){ %>
                        <%=StringEscapeUtils.escapeHtml4(labInfo.getLabCode())%> Projects
                    <%} %>
                </h2>
            </div>

            <div class="text-center">
                <h2 class="heading"></h2>
            </div>

            <table class="executive home-table logo-table">
                <tr>
                    <th colspan="8" class="text-center">
                        <img class="logo" 
                             <% if(lablogo!=null ){ %> 
                             src="data:image/*;base64,<%=lablogo%>" alt="Logo"
                             <%}else{ %> alt="File Not Found" <%} %> >
                        <br>
                    </th>
                </tr>
            </table>

            <% Boolean flag=false;
                for(int i=0;i<freezedproject.size();i++) {
                    for(int j=0;j<projects.size();j++) {
                        if(freezedproject.get(i)[3].toString().equals(projects.get(j)[0].toString())){
                            reviewedby=freezedproject.get(i)[4].toString();
                            reviewDate=sdf.format(freezedproject.get(i)[5]).toString();
                            flag=true;
                            break;
                        }
                        if(flag)break;
                    }
                }
            %> 

            <h2 class="review-heading text-center">
                <% if( reviewedby!="" ) { %> Review By Secretary DD(R&D) and Chairman DRDO <% } %>
            </h2>
            <h3 class="review-date text-center">
                <% if( reviewDate!="" ) { %> <b>Date : 16-07-2024</b> <% } %>
            </h3>

            <table class="executive home-table details-table">
                <% if(labInfo!=null){ %>
                    <tr>
                        <th colspan="8" class="lab-name">
                            <h2>
                                <% if(labInfo.getLabName()!=null){ %>
                                    <%=StringEscapeUtils.escapeHtml4(labInfo.getLabName())%>
                                <%}else{ %>
                                    LAB NAME
                                <%} %>
                                (
                                <% if(labInfo!=null && labInfo.getLabCode() !=null){ %>
                                    <%=StringEscapeUtils.escapeHtml4(labInfo.getLabCode())%>
                                <%} %>
                                )
                            </h2>
                        </th>
                    </tr>
                <% } %>
                <tr>
                    <th colspan="8" class="org-line">Government of India, Ministry of Defence</th>
                </tr>
                <tr>
                    <th colspan="8" class="org-line">Defence Research & Development Organization</th>
                </tr>
                <tr>
                    <th colspan="8" class="org-line">
                        <% if(labInfo.getLabAddress() !=null){ %>
                            <%=labInfo.getLabAddress()%>, <%=StringEscapeUtils.escapeHtml4(labInfo.getLabCity())%>
                        <%}else{ %>
                            LAB ADDRESS
                        <%} %>
                    </th>
                </tr>
            </table>
        </div>
    </div>
</div>

		
			
		<!-- ----------------------------------------  P-0  Div ----------------------------------------------------- -->


		<div class="carousel-item ">
			<div class="content content-box" align="center" >
				<div align="center"><h2 class="clx-1">Project Outline</h2></div>
				<div class="card-body shadow-nohover" >
					<div class="">
						<div class="">
						
			        	<%-- <% for(int i=0;i<mainProjectList.size();i++){ %><%=mainProjectList.get(i)[13] %><%} %>
			        	<% for(int i=0;i<subProjectList.size();i++){ %><%=subProjectList.get(i)[13] %><%} %> --%>
			        	<!-- ----------------------------------Main projects List -------------------------- -->	
			        	<% int val=0;
			        	if(mainProjectList.size()>0){ %>
			        	<h4 class="text-left">Main Project<a  data-toggle="modal"  class="fa faa-pulse animated clx-2" data-target="#exampleModal1" data-whatever="@mdo"  ><i class="fa fa-info-circle clx-3"  aria-hidden="true"></i> </a>
			        	</h4>
							<table class="clx-4">
								<thead class="clx-5">
									<tr >
										<th >SN</th>
										<th >Code</th>
										<th >Project Name</th>
										<th >Category</th> 
										<th >DOS</th>
										<th >PDC</th>
										<th >Sanction Cost<br>(In Cr, &#8377)</th>
										<th >Expenditure<br>(In Cr, &#8377)</th>
										<th >Out Commitment<br>(In Cr, &#8377)</th>
										<th >Dipl<br>(In Cr, &#8377)</th>
										<th >Balance<br>(In Cr, &#8377)</th>
									</tr>
								</thead>
								<tbody>
									<% if(mainProjectList!=null && mainProjectList.size()>0) {
										
										for(int i=0;i<mainProjectList.size();i++ ){val=i; %>
										<tr class="clickable clx-6" data-target="#presentation-slides" data-slide-to="<%=(++pageCOunt)%>" data-toggle="tooltip" data-placement="top" title="" >
											<td class="clx-7"><%=1+i %> </td>
											<td class="clx-7">
												<%=mainProjectList.get(i)[12]!=null?StringEscapeUtils.escapeHtml4(mainProjectList.get(i)[12].toString()):"-" %>
											</td>
											<td   >
											<div class="container-fluid">
													<div class="row">
														<div class="col-1">
															<ul class="modal-list">
																<%
																	double cost = Double.parseDouble(mainProjectList.get(i)[3].toString());
																	String enduser = "--";
																	if (mainProjectList != null && mainProjectList.get(i)[6] != null && mainProjectList.get(i)[6].toString().equalsIgnoreCase("IA")) {
																%>
																	
																<li><span class="modal-span cl-g" >&#11044;</span></li>
																
																<%
																} else if (mainProjectList != null && mainProjectList.get(i)[6] != null && mainProjectList.get(i)[6].toString().equalsIgnoreCase("IN")) {
																		%>
																		
																<li><span class="modal-span cl-1" >&#11044;</span></li>
																		
																		<%
																} else if (mainProjectList != null && mainProjectList.get(i)[6] != null && mainProjectList.get(i)[6].toString().equalsIgnoreCase("IAF")) {
																		%>
																		
																<li><span class="modal-span cl-2" >&#11044;</span></li>
																		
																		<%
																} else if (mainProjectList != null && mainProjectList.get(i)[6] != null && mainProjectList.get(i)[6].toString().equalsIgnoreCase("IH")) {
																	%>
																	
																<li><span class="modal-span cl-3" >&#11044;</span></li>
																	
																	<%
																} else if (mainProjectList != null && mainProjectList.get(i)[6] != null && mainProjectList.get(i)[6].toString().equalsIgnoreCase("DRDO")) {
																	%>
																	
																<li><span class="modal-span cl-4" >&#11044;</span></li>
																	
																	<%
																} else if (mainProjectList != null && mainProjectList.get(i)[6] != null && mainProjectList.get(i)[6].toString().equalsIgnoreCase("OH")) {
																	%>
																	
																<li><span class="modal-span cl-5" >&#11044;</span></li>
																	
																	<%
																}
																
																%>
															
															</ul>
												
													
														</div>
														<div class="col">
															<%if (mainProjectList.get(i) != null )
																if(mainProjectList.get(i)[1] != null) { %><b><%=StringEscapeUtils.escapeHtml4(mainProjectList.get(i)[1].toString())%> - <%=mainProjectList.get(i)[13]!=null?StringEscapeUtils.escapeHtml4(mainProjectList.get(i)[13].toString()):"-"%></b> 
															<%}%>
														</div>
													</div>
												</div>										
											</td>
											
											<td class="clx-8">
												<%if(mainProjectList.get(i)[32]!=null){%><%=StringEscapeUtils.escapeHtml4(mainProjectList.get(i)[32].toString()) %><%}else {%>-<%} %>
											</td>
										
											<td class="clx-8">
												<%if (mainProjectList.get(i) != null )
													
													if(mainProjectList.get(i)[5]!= null) {	%>
													<%=fc.SqlToRegularDate(mainProjectList.get(i)[5].toString())%>
												<%}%>
											</td>
											<td  class="clx-8 <%
													DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
											LocalDate regularDate = LocalDate.parse(fc.SqlToRegularDate(mainProjectList.get(i)[4].toString()), formatter);
													if (mainProjectList.get(i) != null )
														if(mainProjectList.get(i)[4]!= null)
											if(regularDate.isBefore(LocalDate.now())){%>  cl-6  <%}%> ">
												<%if (mainProjectList.get(i) != null )
													if(mainProjectList.get(i)[4]!= null) {
													 formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
														 regularDate = LocalDate.parse(fc.SqlToRegularDate(mainProjectList.get(i)[4].toString()), formatter);
														%>
													<%=fc.SqlToRegularDate(mainProjectList.get(i)[4].toString())%>
												<%}%>
											</td>
											<td class="clx-9">
												<%if (mainProjectList.get(i) != null )
													if(mainProjectList.get(i)[3]!= null) { %>
													<%=String.format("%.2f", Double.parseDouble(mainProjectList.get(i)[3].toString())/10000000)%>
												<%}%>
											</td>
											<td class="clx-9">
												<%if (mainProjectList.get(i) != null )
													if(mainProjectList.get(i)[16]!= null) { %>
													<%=String.format("%.2f", Double.parseDouble(mainProjectList.get(i)[16].toString())/10000000)%>
												<%}%>
											</td>
											<td class="clx-9">
												<%if (mainProjectList.get(i) != null )
													if(mainProjectList.get(i)[17]!= null) { %>
													<%=String.format("%.2f", Double.parseDouble(mainProjectList.get(i)[17].toString())/10000000)%>
												<%}%>
											</td>
											<td class="clx-9">
												<%if (mainProjectList.get(i) != null )
													if(mainProjectList.get(i)[18]!= null) { %>
													<%=String.format("%.2f", Double.parseDouble(mainProjectList.get(i)[18].toString())/10000000)%>
												<%}%>
											</td>
											<td class="clx-9">
												<%if (mainProjectList.get(i) != null )
													if(mainProjectList.get(i)[19]!= null) { %>
													<%=String.format("%.2f", Double.parseDouble(mainProjectList.get(i)[19].toString())/10000000)%>
												<%}%>
											</td>
										</tr>
									<%} }%>
								</tbody>
					
							</table>
						<%} %>
									<!-- ----------------------------------sub projects List -------------------------- -->		
									<br><br><br>
									<%if(subProjectList.size()>0){ %>
							<h4 class="text-left">Sub Project<a  data-toggle="modal"  class="fa faa-pulse animated clx-2" data-target="#exampleModal1" data-whatever="@mdo"  ><i class="fa fa-info-circle clx-3"aria-hidden="true"></i> </a></h4>
									<table class="clx-10">
									<thead class="clx-5">
										<tr >
											<th >SN</th>
											<th >Code</th>
											<th >Project Name</th>
											<th >Category</th>
											<th >DOS</th>
											<th >PDC</th>
											<th >Sanction Cost<br>(In Cr, &#8377)</th>
											<th >Expenditure<br>(In Cr, &#8377)</th>
											<th >Out Commitment<br>(In Cr, &#8377)</th>
											<th >Dipl<br>(In Cr, &#8377)</th>
											<th >Balance<br>(In Cr, &#8377)</th>
										</tr>
									</thead>
									<tbody>
									
										<% if(subProjectList!=null && subProjectList.size()>0) {
											if(val>0)val++;
											for(int i=0;i<subProjectList.size();i++ ){ %>	
											<tr class="clickable " data-target="#presentation-slides" data-slide-to="<%=(++pageCOunt)%>" data-toggle="tooltip" data-placement="top" title="" >
												<td class="clx-7"><%=1+i %> </td>
												<td class="clx-7">
													<%=subProjectList.get(i)[12]!=null?StringEscapeUtils.escapeHtml4(subProjectList.get(i)[12].toString()):"-" %>
												</td>
												<td  class="font-weight-bold"  >
													<div class="container-fluid">
														<div class="row">
															<div class="col-1">
																<ul class="modal-list">
																	<%
																		double cost = Double.parseDouble(subProjectList.get(i)[3].toString());
																		String enduser = "--";
																		if (subProjectList != null && subProjectList.get(i)[6] != null && subProjectList.get(i)[6].toString().equalsIgnoreCase("IA")) {
																	%>
																		
																	<li><span class="modal-span cl-g" >&#11044;</span></li>
																	
																	<%
																	} else if (subProjectList != null && subProjectList.get(i)[6] != null && subProjectList.get(i)[6].toString().equalsIgnoreCase("IN")) {
																			%>
																			
																	<li><span class="modal-span cl-1">&#11044;</span></li>
																			
																			<%
																	} else if (subProjectList != null && subProjectList.get(i)[6] != null && subProjectList.get(i)[6].toString().equalsIgnoreCase("IAF")) {
																			%>
																			
																	<li><span class="modal-span cl-2">&#11044;</span></li>
																			
																			<%
																	} else if (subProjectList != null && subProjectList.get(i)[6] != null && subProjectList.get(i)[6].toString().equalsIgnoreCase("IH")) {
																		%>
																		
																	<li><span class="modal-span cl-3">&#11044;</span></li>
																		
																		<%
																	} else if (subProjectList != null && subProjectList.get(i)[6] != null && subProjectList.get(i)[6].toString().equalsIgnoreCase("DRDO")) {
																		%>
																		
																	<li><span class="modal-span cl-4">&#11044;</span></li>
																		
																		<%
																	} else if (subProjectList != null && subProjectList.get(i)[6] != null && subProjectList.get(i)[6].toString().equalsIgnoreCase("OH")) {
																		%>
																		
																	<li><span class="modal-span cl-5">&#11044;</span></li>
																		
																		<%
																	}
																	
																	%>
																
																</ul>
															</div>
															<div class="col">
																<%if (subProjectList.get(i) != null )
																	if(subProjectList.get(i)[1] != null) { %><%=StringEscapeUtils.escapeHtml4(subProjectList.get(i)[1].toString())%> - <%=subProjectList.get(i)[13]!=null?StringEscapeUtils.escapeHtml4(subProjectList.get(i)[13].toString()):"-"%>
																<%}%>
															</div>
														</div>
													</div>
													
												
														
												</td>
												<td class="clx-8">
													<%if(subProjectList.get(i)[32]!=null){%><%=StringEscapeUtils.escapeHtml4(subProjectList.get(i)[32].toString()) %><%}else {%>-<%} %>
												</td>
												
												<td class="clx-8">
													<%if (subProjectList.get(i) != null )
														if(subProjectList.get(i)[5]!= null) { %>
														<%=fc.SqlToRegularDate(subProjectList.get(i)[5].toString())%>
													<%}%>
												</td>
												<td class="clx-8">
													<%if (subProjectList.get(i) != null )
														if(subProjectList.get(i)[4]!= null) { %>
														<%=fc.SqlToRegularDate(subProjectList.get(i)[4].toString())%>
													<%}%>
												</td>
												<td class="clx-9">
													<%if (subProjectList.get(i) != null )
														if(subProjectList.get(i)[3]!= null) { %>
														<%=String.format("%.2f", Double.parseDouble(subProjectList.get(i)[3].toString())/10000000)%>
													<%}%>
												</td>
												<td class="clx-9">
													<%if (subProjectList.get(i) != null )
														if(subProjectList.get(i)[16]!= null) { %>
														<%=String.format("%.2f", Double.parseDouble(subProjectList.get(i)[16].toString())/10000000)%>
													<%}%>
												</td>
												<td class="clx-9">
													<%if (subProjectList.get(i) != null )
														if(subProjectList.get(i)[17]!= null) { %>
														<%=String.format("%.2f", Double.parseDouble(subProjectList.get(i)[17].toString())/10000000)%>
													<%}%>
												</td>
												<td class="clx-9">
													<%if (subProjectList.get(i) != null )
														if(subProjectList.get(i)[18]!= null) { %>
														<%=String.format("%.2f", Double.parseDouble(subProjectList.get(i)[18].toString())/10000000)%>
													<%}%>
												</td>
												<td class="clx-9">
													<%if (subProjectList.get(i) != null )
														if(subProjectList.get(i)[19]!= null) { %>
														<%=String.format("%.2f", Double.parseDouble(subProjectList.get(i)[19].toString())/10000000)%>
													<%}%>
												</td>
											</tr>
										<%} }%>
									</tbody>
						
									</table>
									<%} %>
						</div>
					</div>
							
				</div>
			</div>
		</div>


		<!-- ----------------------------------------  Freezed Project Slide ----------------------------------------------------- -->
		<!-- ----------------------------------------- Slide Two ------------------------------------------------------------ -->
		<%if(projects!=null && projects.size()>0){
				for(int i=0;i<projects.size();i++ ){
					if(projects.get(i)[22]!=null  || projects.get(i)[24]!=null ||
							projects.get(i)[25]!=null || projects.get(i)[27]!=null || projects.get(i)[29]!=null ||
							projects.get(i)[26]!=null || projects.get(i)[30]!=null || projects.get(i)[31]!=null){
							if(projects.get(i)[23]!=null && projects.get(i)[23].toString().equals("2")){ %>
								<div class="carousel-item " >
									<div class="container-fluid" >
										<div class="container-fluid"  >
											<div id="slide2" >
								
												<%
												double cost = Double.parseDouble(projects.get(i)[3].toString());
												double exp = Double.parseDouble(projects.get(i)[16].toString());
												String enduser = "--";
												if (projects != null && projects.get(i)[6] != null && projects.get(i)[6].toString().equalsIgnoreCase("IA")) {
												enduser = "Indian Army";
												} else if (projects != null && projects.get(i)[6] != null && projects.get(i)[6].toString().equalsIgnoreCase("IN")) {
												enduser = "Indian Navy";
												} else if (projects != null && projects.get(i)[6] != null && projects.get(i)[6].toString().equalsIgnoreCase("IAF")) {
												enduser = "Indian Air Force";
												} else if (projects != null && projects.get(i)[6] != null && projects.get(i)[6].toString().equalsIgnoreCase("IH")) {
												enduser = "Home Land Security";
												} else if (projects != null && projects.get(i)[6] != null && projects.get(i)[6].toString().equalsIgnoreCase("DRDO")) {
												enduser = "DRDO";
												} else if (projects != null && projects.get(i)[6] != null && projects.get(i)[6].toString().equalsIgnoreCase("OH")) {
												enduser = "Others";
												}
												%>
								
												<div class="content-header row clx-11" > 
							 						<div class="col-md-1 clx-12" align="left"  >
														<img class="clx-13"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
													</div>
													<h2 class="text-dark<%if(projects.get(i) != null && projects.get(i)[1] != null && projects.get(i)[1].toString().length()>60){ %> clx-14   <%}%> col-md-9"  align="center">
															<%if (projects.get(i) != null )if(projects.get(i)[1] != null) {
															%><%=StringEscapeUtils.escapeHtml4(projects.get(i)[1].toString())%> - <%=projects.get(i)[13]!=null?StringEscapeUtils.escapeHtml4(projects.get(i)[13].toString()):"-"%> (<%=projects.get(i)[12]!=null?StringEscapeUtils.escapeHtml4(projects.get(i)[12].toString()):"-" %>)
															<%}%>
													</h2>
													<h6 class="col">
							 							<%-- <%if(new File(filePath + projects.get(i)[25] + projects.get(i)[27]).exists()){%>
															<a href="SlidePdfOpenAttachDownload.htm?slideId=<%=projects.get(i)[26]%>"  target="_blank" ><b>Show more</b></a>
														<%} %> --%>
														<br>
								 					<%-- 	<%if(new File(filePath + projects.get(i)[25] + projects.get(i)[30]).exists()){%>
															<a href="SlideVideoOpenAttachDownload.htm?slideId=<%=projects.get(i)[26]%>"  target="_blank" title="Video File"><b>Show Video</b></a>
														<%} %> --%>
							 						</h6>
							 						<div class="col-md-1 clx-15" align="right"  >
							 							<img class="clx-16"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
							 						</div>
												</div>
												<div class="content shadow-nohover clx-17"  align="center">
													<div class="row" >
														<div class="col-md-12">
												
															<table class="clx-18">
																<tr>
																	<td colspan="1" class="clx-20 width-12">Project Code </td>
																	<td colspan="1" class="clx-20 width-12">DoS </td>
																	<td colspan="1" class="clx-20">PDC <%if(projects.get(i)[35]!=null) {%> / Org<%} %></td>
																	<td colspan="1" class="clx-20">User </td>
																	<td colspan="1" class="clx-20">Category </td>
																	<td colspan="1" class="clx-20">Cost / Exp (Cr) </td>
																	<!-- <td colspan="1" class="clx-20">Application </td> -->
																	<td colspan="1" class="clx-20">Project Director </td>
																	<td colspan="1" class="clx-21">Current Stage </td>
																	
																	
																	
																</tr>
																<tr>
																	<td colspan="1" class="clx-22"><%=projects.get(i)[12]!=null?StringEscapeUtils.escapeHtml4(projects.get(i)[12].toString()):"--"%></td>
																	<td colspan="1" class="clx-23"><%=sdf.format(projects.get(i)[5])%> </td>
																	<td colspan="1" class="clx-24">
																	
																	<span class="clx-25 
																	<%DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
																	LocalDate regularDate = LocalDate.parse(fc.SqlToRegularDate(projects.get(i)[4].toString()), formatter);
																	if(regularDate.isBefore(LocalDate.now())){
																	%>
																	text-danger
																<%}%>
																	"><%=sdf.format(projects.get(i)[4])%></span>
																	 <%if(projects.get(i)[35]!=null) {%><br><span class="clx-26"><%=sdf.format(projects.get(i)[35]) %></span> <%} %></td>
																	<td colspan="1" class="clx-27"><%=projects.get(i)[6]!=null?StringEscapeUtils.escapeHtml4(projects.get(i)[6].toString()):"--"%></td>
																	<td colspan="1" class="clx-27"><%=projects.get(i)[32]!=null?StringEscapeUtils.escapeHtml4(projects.get(i)[32].toString()):"--"%></td>
																	<td colspan="1" class="clx-28"><%=nfc.convert(cost / 10000000)%> / <span class="clx-30"><%=nfc.convert(exp / 10000000)%></span></td>
																	<td colspan="1" class="clx-29"> <%if (projects.get(i) != null && projects.get(i)[33] != null) {%> <%=StringEscapeUtils.escapeHtml4(projects.get(i)[33].toString())%>, <%if(projects.get(i)[34] != null){ %> <%=StringEscapeUtils.escapeHtml4(projects.get(i)[34].toString()) %><%} %>
																		<%} else {%> -- <%}%></td>
																	<td colspan="1" class="clx-28"> <%if(projects.get(i)[14]!=null){%> <%=StringEscapeUtils.escapeHtml4(projects.get(i)[14].toString())%>
																		<%} else {%> -- <%}%></td>
																</tr>
																<tr>
																	<td colspan="1" class="clx-32">Brief :</td>
																	<td colspan="7" class="clx-33">
																		<%if(projects.get(i)[28]!=null){%>
																			<%=StringEscapeUtils.escapeHtml4(projects.get(i)[28].toString())%>
																		<%}else{%>
																			--
																		<%}%>
																	</td>
																</tr>
																<tr>
																	<td colspan="1"><b class="clx-44">Objectives : </b></td>
																	<td colspan="7" class="clx-33">
																			<%if(projects.get(i)[7]!=null) {%>
																				<%=StringEscapeUtils.escapeHtml4(projects.get(i)[7].toString())%> 
																			<%} else{%>
																				--
																			<%} %>
																	</td>
																</tr>
																
																
																<tr>
																	<td colspan="1"><b class="clx-44">Deliverables : </b> </td>
																	<td colspan="7"class="clx-33">
																			<%if(projects.get(i)[8]!=null) {%>
																				<%=StringEscapeUtils.escapeHtml4(projects.get(i)[8].toString())%> 
																			<%} else{%>
																				--
																			<%} %>
																	</td>
																</tr>
																
																
															</table>
															<br>
															<div class="container-fluid">
																<div class="row">
																<div class="col-md-5 clx-45">
																	<div class="col p-0" >
																	
																	<p class="mb-0"><span class="clx-34">
																				<u>Current Status</u> :</span> &nbsp; &nbsp; &nbsp; &nbsp; <span><a href="BriefingPresentation.htm?projectid=<%=projects.get(i)[0]%>&committeeid=<%="1"%>" target="_blank">PMRB </a></span>  &nbsp; &nbsp; <span><a href="BriefingPresentation.htm?projectid=<%=projects.get(i)[0]%>&committeeid=<%="2"%>" target="_blank">EB </a></span>
														<%
														Path attachmentPath1 = Paths.get(filePath,labcode,"ProjectSlide",projects.get(i)[27]+"");
														File specificfile1 = attachmentPath1.toFile();
														
														Path videopath = Paths.get(filePath,labcode,"ProjectSlide",projects.get(i)[30]+"");
														File videoPathFile = videopath.toFile();
														
														Path imagepath = Paths.get(filePath,labcode,"ProjectSlide",projects.get(i)[24]+"");
														File imagepathFile = imagepath.toFile();
														%>
														
														<%if(specificfile1.exists()){%>
														&nbsp; &nbsp;	<span><a href="SlidePdfOpenAttachDownload.htm?slideId=<%=projects.get(i)[26]%>"  target="_blank" ><b><i class="fa fa-sm fa-angle-double-right text-primary" aria-hidden="true" ></i><i class="fa fa-angle-right text-primary" aria-hidden="true" ></i>	</b></a></span>
														<%} %>
														<%if(videoPathFile.exists()){%>
														&nbsp; &nbsp;	<span>		<a href="SlideVideoOpenAttachDownload.htm?slideId=<%=projects.get(i)[26]%>"  target="_blank" title="Video File"><img alt="" src="view/images/presentation.png" class="clx-35"></a></span>
												
														<%} %>
																				 </p>
																				<%if(projects.get(i)!=null && projects.get(i)[20]!=null && projects.get(i)[20].toString().length()>0) {%>
																				<%-- 	<%=projects.get(i)[20].toString().substring(3,projects.get(i)[20].toString().length()-1 )%> --%>
																				<div class="ml-3 text-left" ><%=projects.get(i)[20].toString()%></div>
																				<%} else{%>-<%} %>
																	</div>
																
																<%if(projects.get(i)[31]!=null && projects.get(i)[31]!=""){%>
																		
																			
																
																	<div class="col pl-0" >
																	
																		<p class="mb-0"><span class="clx-34">
																			<u>Way Forward </u>: 
																			</span></p>
																	<%-- 	<%=projects.get(i)[31].toString().substring(3,projects.get(i)[31].toString().length()-1 )%> --%>
																		<div class="ml-3">
																				<%=projects.get(i)[31].toString() %>
																				</div>
																	</div>
																		
															
																<%}%>
																</div>
																	<div class="col-md-7 clx-36" >
													
																		<%if(imagepathFile.exists()){%>
																		<div class="clx-37">
																		<%if(specificfile1.exists()){%>
																			<a  href="SlidePdfOpenAttachDownload.htm?slideId=<%=projects.get(i)[26]%>" target="_blank" >
																				<img class=" d-flex justify-content-center zoom2 clx-38" data-enlargable  
																				src="data:image/*;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(imagepathFile))%>">
																			</a>
																		<%}else{ %>
																				<img class=" d-flex justify-content-center zoom2 clx-38" data-enlargable  
																				src="data:image/*;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(imagepathFile))%>">
																			
																			<%} %>
																		</div>
																		<%} else{ %>image<%} %>
														
													</div> 
																
																
																	</div>
													
															</div>
																	
												
													
														</div>
													
													</div>
										
										
									
												
												</div>
								
											</div>
										</div>
									</div>
								</div>
								<!-- ----------------------------------------- Slide One ------------------------------------------------------------ -->
							<%}%><% if(projects.get(i)[23]!=null && projects.get(i)[23].toString().equals("1")){ %> 
								<div class="carousel-item " >
									<div class="container-fluid" >
											<div class="container-fluid"  >
											<div class="" id="slide1">
												<%	double cost = Double.parseDouble(projects.get(i)[3].toString());
												double exp = Double.parseDouble(projects.get(i)[16].toString());
												 String enduser="--";
												if(projects!=null && projects.get(i)[6]!=null && projects.get(i)[6].toString().equalsIgnoreCase("IA")){
													enduser="Indian Army";
												}else if(projects!=null && projects.get(i)[6]!=null && projects.get(i)[6].toString().equalsIgnoreCase("IN")){
													enduser="Indian Navy";
												}else if(projects!=null && projects.get(i)[6]!=null && projects.get(i)[6].toString().equalsIgnoreCase("IAF")){
													enduser="Indian Air Force";
												}else if(projects!=null && projects.get(i)[6]!=null && projects.get(i)[6].toString().equalsIgnoreCase("IH")){
													enduser="Home Land Security";
												}else if(projects!=null && projects.get(i)[6]!=null && projects.get(i)[6].toString().equalsIgnoreCase("DRDO")){
													enduser="DRDO";
												}else if(projects!=null && projects.get(i)[6]!=null && projects.get(i)[6].toString().equalsIgnoreCase("OH")){
													enduser="Others";
												}
												%>
											<div class="content-header row clx-11" > 
							 						<div class="col-md-1 clx-12" align="left"  >
														<img class="clx-13"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
													</div>
													<h2 class="text-dark<%if(projects.get(i) != null && projects.get(i)[1] != null && projects.get(i)[1].toString().length()>60){ %> clx-14   <%}%> col-md-9"  align="center">
															<%if (projects.get(i) != null )if(projects.get(i)[1] != null) {
															%><%=StringEscapeUtils.escapeHtml4(projects.get(i)[1].toString())%> - <%=projects.get(i)[13]!=null?StringEscapeUtils.escapeHtml4(projects.get(i)[13].toString()):"-"%> (<%=projects.get(i)[12]!=null?StringEscapeUtils.escapeHtml4(projects.get(i)[12].toString()):"-" %>)
															<%}%>
													</h2>
													<h6 class="col">
							 							<%-- <%if(new File(filePath + projects.get(i)[25] + projects.get(i)[27]).exists()){%>
															<a href="SlidePdfOpenAttachDownload.htm?slideId=<%=projects.get(i)[26]%>"  target="_blank" ><b>Show more</b></a>
														<%} %> --%>
														<br>
								 					<%-- 	<%if(new File(filePath + projects.get(i)[25] + projects.get(i)[30]).exists()){%>
															<a href="SlideVideoOpenAttachDownload.htm?slideId=<%=projects.get(i)[26]%>"  target="_blank" title="Video File"><b>Show Video</b></a>
														<%} %> --%>
							 						</h6>
							 						<div class="col-md-1 clx-15" align="right"  >
							 							<img class="clx-16"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
							 						</div>
												</div>
												
												<div class="content clx-17"  align="center">
													<table class="clx-18">
														<tr>
															<td colspan="1" class="clx-20 width-12">Project Code </td>
															<td colspan="1" class="clx-20 width-12">DoS </td>
															<td colspan="1" class="clx-20">PDC <%if(projects.get(i)[35]!=null) {%> / Org<%} %></td>
															<td colspan="1" class="clx-20">User </td>
															<td colspan="1" class="clx-20">Category </td>
															<td colspan="1" class="clx-20">Cost / Exp (Cr) </td>
															<!-- <td colspan="1" class="clx-20">Application </td> -->
																																<td colspan="1" class="clx-20">Project Director </td>
															<td colspan="1" class="clx-21">Current Stage </td>
														</tr>
														<tr>
															<td colspan="1" class="clx-24"><%=projects.get(i)[12]!=null?StringEscapeUtils.escapeHtml4( projects.get(i)[12].toString()):"--"%></td>
															<td colspan="1" class="clx-24"><%=sdf.format(projects.get(i)[5])%></td>
															<td colspan="1" class="clx-24">
																<span class=" clx-26
																	<%DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
																	LocalDate regularDate = LocalDate.parse(fc.SqlToRegularDate(projects.get(i)[4].toString()), formatter);
																	if(regularDate.isBefore(LocalDate.now())){
																	%>
																text-danger;
																<%}%>
																	"><%=sdf.format(projects.get(i)[4])%></span>
															<%if(projects.get(i)[35]!=null) {%><br><span class="clx-26"><%=sdf.format(projects.get(i)[35]) %></span> <%} %></td>
															<td colspan="1" class="clx-27"><%=projects.get(i)[6]!=null?StringEscapeUtils.escapeHtml4(projects.get(i)[6].toString()):"--"%></td>
															<td colspan="1" class="clx-27"><%=projects.get(i)[32]!=null?StringEscapeUtils.escapeHtml4(projects.get(i)[32].toString()):"--"%></td>
															<td colspan="1" class="clx-28"><%=nfc.convert(cost / 10000000)%> / <span class="clx-30"><%=nfc.convert(exp / 10000000)%></span> </td>
															<td colspan="1" class="clx-29"> <%if (projects.get(i) != null && projects.get(i)[33] != null) {%> <%=StringEscapeUtils.escapeHtml4(projects.get(i)[33].toString())%>, <%if(projects.get(i)[34] != null){ %> <%=StringEscapeUtils.escapeHtml4(projects.get(i)[34].toString()) %><%} %>
																<%} else {%> -- <%}%></td>
															<td colspan="1" class="clx-29"> <%if(projects.get(i)[14]!=null){%> <%=StringEscapeUtils.escapeHtml4(projects.get(i)[14].toString())%>
																<%} else {%> -- <%}%></td>
														</tr>
													</table>
													<div class="row">
														<div class="col-md-6">
															<div class="row">
																<div class="col left">
																	<table class="clx-39">
																		<tr>
																			<td class="clx-40">
																				<b class="clx-41"> Brief:</b>
																			</td>
																			<td colspan="3" class="clx-42">
																				<%if(projects.get(i)[28]!=null){%>
																					<%=StringEscapeUtils.escapeHtml4(projects.get(i)[28].toString())%>
																				<%}else{%>
																					--
																				<%}%>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<b class="clx-43">Objectives : </b>
																			</td>
																			<td colspan="3" class="clx-33"> 
																					<%=projects.get(i)[7]==null?"--":StringEscapeUtils.escapeHtml4(projects.get(i)[7].toString())%> 
																			</td>
																		</tr>
																		
																				
																		<tr>
																			<td>
																				<b class="clx-46">Deliverables : </b>
																			</td>
																			<td colspan="3" class="clx-33"> 
																					<%=projects.get(i)[8]==null?"--":StringEscapeUtils.escapeHtml4(projects.get(i)[8].toString())%> 
																			</td>
																		</tr>
																					
																	</table>
																	<br>
																	<div class="container-fluid">
																	<div class="row">
																		<div class="col pl-0" >
																			<span class="clx-34">
																				<u>Current Status</u> :  
																			</span>&nbsp; &nbsp; &nbsp;&nbsp; <span><a href="BriefingPresentation.htm?projectid=<%=projects.get(i)[0]%>&committeeid=<%="1"%>" target="_blank">PMRB </a></span>  &nbsp; &nbsp; <span><a href="BriefingPresentation.htm?projectid=<%=projects.get(i)[0]%>&committeeid=<%="2"%>" target="_blank">EB </a></span>
														
																<%
														Path attachmentPath1 = Paths.get(filePath,labcode,"ProjectSlide",projects.get(i)[27]+"");
														File specificfile1 = attachmentPath1.toFile();
														
														Path videopath = Paths.get(filePath,labcode,"ProjectSlide",projects.get(i)[30]+"");
														File videoPathFile = videopath.toFile();
														
														Path imagepath = Paths.get(filePath,labcode,"ProjectSlide",projects.get(i)[24]+"");
														File imagepathFile = imagepath.toFile();
														%>
														<%if(specificfile1.exists()){%>
												
												&nbsp; &nbsp;	<span >	<a href="SlidePdfOpenAttachDownload.htm?slideId=<%=projects.get(i)[26]%>"  target="_blank" ><b><i class="fa fa-angle-double-right text-primary" aria-hidden="true" ></i><i class="fa fa-angle-right text-primary" aria-hidden="true" ></i> </b></a> </span>	
														
														<%} %>
														
															<%if(videoPathFile.exists()){%>
																						
												&nbsp; &nbsp;	<span><a href="SlideVideoOpenAttachDownload.htm?slideId=<%=projects.get(i)[26]%>"  target="_blank" title="Video File"><img alt="" src="view/images/presentation.png" class="clx-35"></a>
														</span>
														<%} %>
																			<%if(projects.get(i)!=null && projects.get(i)[20]!=null) {%>
																				<%-- <%=projects.get(i)[20].toString().substring(3, projects.get(i)[20].toString().length())%> --%>
																					<div class="ml-3 text-left"><%=projects.get(i)[20].toString()%></div>
																			<%} else{%>-<%} %> 
																		</div>
																	</div>
																	<%if(projects.get(i)[31]!=null && projects.get(i)[31]!=""){%>	
																		<div class="row">
																			<div class="col pl-0" >
																				<span class="clx-34">
																					<u>Way Forward : </u></span> 
																				<div class="ml-3">
																				<%=projects.get(i)[31].toString() %>
																				</div>
																			</div>
																		</div>
																	<%}%>
																	
												
																	
																</div>
																</div>
																
															</div>
														</div>
														<br>
														<div class="col-md-5 mt-3 clx-47" >
										<%
										if(imagepathFile.exists()) {
										%>
										<%
										if (specificfile1.exists()) {
										%>
										<a
											href="SlidePdfOpenAttachDownload.htm?slideId=<%=projects.get(i)[26]%>"
											target="_blank"> <img class="zoom clx-48" data-enlargable
											
											align="middle"
											src="data:image/*;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(imagepathFile))%>">
										</a>
										<%
										} else {
										%>
										<img class="zoom clx-48" data-enlargable
											
											align="middle"
											src="data:image/*;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(imagepathFile))%>">
										<%
										}
										%>
										<%
										} else {
										%>image<%
										}
										%>



										<br>
															
							
														</div>
															  	 	
														
														</div>
														
														
												</div>	
											</div>
					
											</div>
					
									</div>
								</div>
								
						<!-- ----------------------------------------- Slide Three - No Data ------------------------------------------------------------ -->
						<%}}else{%>
							
							<div class="carousel-item " >
									<div class="container-fluid" >
										<div class="container-fluid"  >
											<div id="slide2" >
								
												<%
												double cost = Double.parseDouble(projects.get(i)[3].toString());
												double exp = Double.parseDouble(projects.get(i)[16].toString());
												String enduser = "--";
												if (projects != null && projects.get(i)[6] != null && projects.get(i)[6].toString().equalsIgnoreCase("IA")) {
												enduser = "Indian Army";
												} else if (projects != null && projects.get(i)[6] != null && projects.get(i)[6].toString().equalsIgnoreCase("IN")) {
												enduser = "Indian Navy";
												} else if (projects != null && projects.get(i)[6] != null && projects.get(i)[6].toString().equalsIgnoreCase("IAF")) {
												enduser = "Indian Air Force";
												} else if (projects != null && projects.get(i)[6] != null && projects.get(i)[6].toString().equalsIgnoreCase("IH")) {
												enduser = "Home Land Security";
												} else if (projects != null && projects.get(i)[6] != null && projects.get(i)[6].toString().equalsIgnoreCase("DRDO")) {
												enduser = "DRDO";
												} else if (projects != null && projects.get(i)[6] != null && projects.get(i)[6].toString().equalsIgnoreCase("OH")) {
												enduser = "Others";
												}
												%>
								
												<div class="content-header row clx-11" > 
							 						<div class="col-md-1 clx-12" align="left"  >
														<img class="clx-13"   <%if(Drdologo!=null ){ %> src="data:image/*;base64,<%=Drdologo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
													</div>
													<h2 class="text-dark<%if(projects.get(i) != null && projects.get(i)[1] != null && projects.get(i)[1].toString().length()>60){ %> clx-14   <%}%> col-md-9"  align="center">
															<%if (projects.get(i) != null )if(projects.get(i)[1] != null) {
															%><%=StringEscapeUtils.escapeHtml4(projects.get(i)[1].toString())%> - <%=projects.get(i)[13]!=null?StringEscapeUtils.escapeHtml4(projects.get(i)[13].toString()):"-"%> (<%=projects.get(i)[12]!=null?StringEscapeUtils.escapeHtml4(projects.get(i)[12].toString()):"-" %>)
															<%}%>
													</h2>
													<h6 class="col">
							 							<%-- <%if(new File(filePath + projects.get(i)[25] + projects.get(i)[27]).exists()){%>
															<a href="SlidePdfOpenAttachDownload.htm?slideId=<%=projects.get(i)[26]%>"  target="_blank" ><b>Show more</b></a>
														<%} %> --%>
														<br>
								 					<%-- 	<%if(new File(filePath + projects.get(i)[25] + projects.get(i)[30]).exists()){%>
															<a href="SlideVideoOpenAttachDownload.htm?slideId=<%=projects.get(i)[26]%>"  target="_blank" title="Video File"><b>Show Video</b></a>
														<%} %> --%>
							 						</h6>
							 						<div class="col-md-1 clx-15" align="right"  >
							 							<img class="clx-16"  <%if(lablogo!=null ){ %> src="data:image/*;base64,<%=lablogo%>" alt="Logo"<%}else{ %> alt="File Not Found" <%} %> >
							 						</div>
												</div>
												<div class="content shadow-nohover clx-49" align="center">
													<div class="row" >
														<div class="col-md-12">
												
															<table class="clx-18">
																<tr>
																	<td colspan="1" class="clx-19 width-12">Project Code </td>
																	<td colspan="1" class="clx-19 width-12">DoS </td>
																	<td colspan="1" class="clx-19">PDC<%if(projects.get(i)[35]!=null) {%> / Org<%} %> </td>
																	<td colspan="1" class="clx-19 width-12">User </td>
																	<td colspan="1" class="clx-19 width-12">Category </td>
																	<td colspan="1" class="clx-19">Cost / Exp (Cr) </td>
																	<!-- <td colspan="1" class="clx-19">Application </td> -->
																	<td colspan="1" class="clx-20">Project Director </td>
																	<td colspan="1" class="clx-19">Current Stage </td>
																</tr>
																<tr>
																	<td colspan="1" class="clx-24"><%=projects.get(i)[12]!=null?StringEscapeUtils.escapeHtml4(projects.get(i)[12].toString()):"--"%></td>
																	<td colspan="1" class="clx-23"><%=sdf.format(projects.get(i)[5])%></td>
																	<td colspan="1" class="clx-24">
																				<span class="clx-25
																	<%DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
																	LocalDate regularDate = LocalDate.parse(fc.SqlToRegularDate(projects.get(i)[4].toString()), formatter);
																	if(regularDate.isBefore(LocalDate.now())){
																	%>
																text-danger
																<%}%>
																	"><%=sdf.format(projects.get(i)[4])%></span>
																	
																	<%if(projects.get(i)[35]!=null) {%><br><span class="clx-26"><%=sdf.format(projects.get(i)[35]) %></span><%} %></td>
																	<td colspan="1" class="clx-22"><%=projects.get(i)[6]!=null?StringEscapeUtils.escapeHtml4(projects.get(i)[6].toString()):"--"%></td>
																	<td colspan="1" class="clx-22"><%=projects.get(i)[32]!=null?StringEscapeUtils.escapeHtml4(projects.get(i)[32].toString()):"--"%></td>
																	<td colspan="1" class="clx-28"><%=nfc.convert(cost / 10000000)%> / <span class="clx-30"><%=nfc.convert(exp / 10000000)%></span> </td>
																	<td colspan="1" class="clx-31"> <%if (projects.get(i) != null && projects.get(i)[33] != null) {%> <%=StringEscapeUtils.escapeHtml4(projects.get(i)[33].toString())%>, <%if(projects.get(i)[34] != null){ %> <%=StringEscapeUtils.escapeHtml4(projects.get(i)[34].toString()) %><%} %>
																		<%} else {%> -- <%}%></td>
																	<td colspan="1" class="clx-31"> <%if(projects.get(i)[14]!=null){%> <%=StringEscapeUtils.escapeHtml4(projects.get(i)[14].toString())%>
																		<%} else {%> -- <%}%></td>
																</tr>
																<tr>
																	<td colspan="1" class="clx-44">Brief :</td>
																	<td colspan="7" class="clx-50">
																		--
																	</td>
																</tr>
																<tr>
																	<td colspan="1"><b class="clx-44">Objectives : </b></td>
																	<td colspan="7" class="clx-50">
																	 <%if(projects.get(i)[7] != null && projects.get(i)[7].toString().length() > 320) {%>
																		<%=StringEscapeUtils.escapeHtml4(projects.get(i)[7].toString()).substring(0, 280)%>
																	<%} else {%> 
																		<%=projects.get(i)[7]!=null?StringEscapeUtils.escapeHtml4(projects.get(i)[7].toString()):"--"%> 
																	<%}%>
																	</td>
																</tr>
									
																
																<tr>
																	<td colspan="1"><b class="clx-44">Deliverables : </b> </td>
																	<td colspan="7" class="clx-51">
																		<% if (projects.get(i)[8] != null && projects.get(i)[8].toString().length() > 320) {%>
																			<%=StringEscapeUtils.escapeHtml4(projects.get(i)[8].toString()).substring(0, 280)%>
																		<%} else {%> 
																			<%=projects.get(i)[8]!=null?StringEscapeUtils.escapeHtml4(projects.get(i)[8].toString()):"--"%> 
																		<% } %>
																	</td>
																</tr>
																
															</table>
															<br>
															<div class="container-fluid">
															<div class="row">
															<div class="col pl-0" >
															<h4 class="clx-34">
																		<u>Current Status </u>: &nbsp; &nbsp;&nbsp; &nbsp; <span><a href="BriefingPresentation.htm?projectid=<%=projects.get(i)[0]%>&committeeid=<%="1"%>" target="_blank">PMRB </a></span>  &nbsp; &nbsp; <span><a href="BriefingPresentation.htm?projectid=<%=projects.get(i)[0]%>&committeeid=<%="2"%>" target="_blank">EB </a></span>
																	</h4>
															</div>
															<div class="col">
															
																	<%if(projects.get(i)!=null && projects.get(i)[20]!=null) {%>
																				<div class="ml-3 text-left" ><%=projects.get(i)[20].toString()%></div>
																		<%} else{%>-<%} %> 
						
															</div>
															</div>
															</div>
															
													
														</div>
													</div>
										
													<div class="col-md-12 pt-3" >
														<table class="clx-52">
															<tbody class="border-0" >
																<tr class="clx-53"><td></td></tr>
																<tr class="clx-53"><td></td></tr>
																<tr class="clx-53"><td></td></tr>
																<tr class="clx-53"><td></td></tr>
																<tr class="clx-53"><td></td></tr>
																<tr class="clx-53"><td></td></tr>
																<tr class="clx-53"><td></td></tr>
																<tr class="clx-53"><td></td></tr>
																<tr class="clx-53"><td></td></tr>
															</tbody>
														</table>
													</div>
												</div>
								
											</div>
										</div>
									</div>
								</div>
							
							
						<%}}}else{%><% } %>
			
							<!-- ----------------------------------------  Freezed Project Slide ----------------------------------------------------- -->
		
							<!-- ----------------------------------------  Thank you Div ----------------------------------------------------- -->

							<div class="carousel-item " >
				
								<div class="content clx-54">
									
									
									<div class="clx-55">
										<h1 class="clx-56">Thank You !</h1>
									</div>
									
								</div>
				
							</div>
						</div>
						<!-- ----------------------------------------   Thank you Div Ends ----------------------------------------------------- -->
		
			 			<a class="carousel-control-prev clx-57" href="#presentation-slides" role="button" data-slide="prev" > <span aria-hidden="true">
							<i class="fa fa-chevron-left fa-2x clx-58"  aria-hidden="true"></i></span> <span class="sr-only">Previous</span>
						</a> 
						<a class="carousel-control-next clx-59" href="#presentation-slides" role="button" data-slide="next" > <span aria-hidden="true">
							<i class="fa fa-chevron-right fa-2x clx-58"  aria-hidden="true"></i></span> <span class="sr-only">Next</span>
						</a> 
	
						<ol class="mt-6 carousel-indicators">
							<li data-target="#presentation-slides" data-slide-to="0"  class="carousel-indicator active" data-toggle="tooltip" data-placement="top" title="Start"><b><i class="fa fa-home" aria-hidden="true"></i></b></li>
							<li data-target="#presentation-slides" data-slide-to="1"  class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="projectNamesListCorousel"><b><i class="fa fa-list" aria-hidden="true"></i></b></li>
							<% int i=1;if(projects!=null && projects.size()>0){
							for(Object[] obj: projects){%>
							<li data-target="#presentation-slides" data-slide-to="<%=++i%>"  class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="<%=i%>. <%=obj[6]%>"><b><%=i-1%></b></li>
							<%}}%>
							<li data-target="#presentation-slides" data-slide-to="<%=i+1%>" class="carousel-indicator" data-toggle="tooltip" data-placement="top" title="Thank You"><b>End</b></li>
							<li data-slide-to="21"  class="carousel-indicator content_full_screen clx-60" data-toggle="tooltip" data-placement="top" title="Full Screen Mode"><b><i class="fa fa-expand fa-lg" aria-hidden="true"></i></b></li>
							<li data-slide-to="21"  class="carousel-indicator content_reg_screen clx-60" data-toggle="tooltip" data-placement="top" title="Exit Full Screen Mode"><b><i class="fa fa-compress fa-lg" aria-hidden="true"></i></b></li>	
							<li class="clx-61"><a onclick="DownloadSelected()" target="_blank" id='downloadselect'><i class="fa fa-download fa-2x cl-g"  aria-hidden="true"></i></a>	
						</ol>
	
</div>	
<div class="modal fade " id="exampleModal1" tabindex="-1" aria-labelledby="exampleModalLabel1" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content clx-62" >
				      
				<div class="modal-header ">
					<h5 class="modal-title clx-63" id="exampleModalLabel" >Colour Coding Summary</h5>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
			        </button>
			  	</div>
					      
				<div class="modal-body">
						
					<%
					 if(true ) { %>	
						
					<div class="row">
						<div class="text-left">
								<ul class="modal-list">
						          	<li><span class="modal-span cl-g" >&#11044;</span><span class="modal-text">Indian Army</span></li>
						           	<li><span class="modal-span cl-1" >&#11044;</span><span class="modal-text">Indian Navy</span></li>
						           	<li><span class="modal-span cl-2" >&#11044;</span><span class="modal-text">Indian Air Forces</span></li>
						           	<li><span class="modal-span cl-3">&#11044;</span><span class="modal-text">Home Land Security</span></li>
						           	<li><span class="modal-span cl-4" >&#11044;</span><span class="modal-text">DRDO</span></li>
						           	<li><span class="modal-span cl-5" >&#11044;</span><span class="modal-text">Others</span></li>
					            </ul>
							</div>
					</div>
					
					<%} %>	
					          
				</div>
				
					      
			</div>
		</div>
	</div>
<script type="text/javascript">
var s = new Array();
<%for(int ij=0;ij<a.length;ij++){%>
s.push(<%=a[ij]%>);
<%}%>
console.log(s);
function DownloadSelected(){
	document.getElementById("downloadselect").href="DownloadSelectedSlides.htm/"+s;
	document.getElementById("downloadselect").click();
		//document.getElementById('submitdownloadselect').submit();
}


$('.carousel').carousel({
	  interval: false,
	  keyboard: true,
	});
	
	  function onTryItClick() {
        var content = document.getElementById("iframecontent").innerHTML;
        var iframe = document.getElementById("myiframeid");

        var frameDoc = iframe.document;
        if (iframe.contentWindow)
            frameDoc = iframe.contentWindow.document;

        frameDoc.open();
        frameDoc.writeln(content);
        frameDoc.close();
    }

    
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

</script>
</body>
</html>