<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="java.text.Format"%>
<%@page import="com.vts.pfms.master.dto.ProjectFinancialDetails"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.math.BigDecimal"%> 
<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat,java.time.LocalDate"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
   
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/js/excel.js" var="excel" />
<spring:url value="/resources/css/pfts/FileStatus.css" var="fromExternalCSS" />     
<link href="${fromExternalCSS}" rel="stylesheet" />
<script src="${excel}"></script>
<title>Procurement Status</title>

<meta charset="ISO-8859-1">

</head>
<body >
<%

FormatConverter fc=new FormatConverter(); 
SimpleDateFormat rdf=fc.getRegularDateFormat();
SimpleDateFormat sdf1=fc.getSqlDateFormat(); int addcount=0; 
NFormatConvertion nfc=new NFormatConvertion();

List<Object[]> projectslist=(List<Object[]>)request.getAttribute("projectslist");
List<Object[]> pftsMilestoneList=(List<Object[]>)request.getAttribute("pftsMilestoneList");
List<Object[]> fileStatusList=(List<Object[]>)request.getAttribute("fileStatusList");
String projectId=request.getAttribute("projectId").toString();
String projectcode=request.getAttribute("projectcode").toString();
List<Object[]> pftsStageList=(List<Object[]>)request.getAttribute("pftsStageList");
List<Object[]> pftsStageList1=pftsStageList.stream().filter(i->Integer.parseInt(i[0].toString())<=10).collect(Collectors.toList());
List<Object[]> pftsStageList2=pftsStageList.stream().filter(i->Integer.parseInt(i[0].toString())>=10).collect(Collectors.toList());
List<Object[]> pftsStageList3=pftsStageList.stream().filter(i->Integer.parseInt(i[0].toString())>10).collect(Collectors.toList());
Format format = com.ibm.icu.text.NumberFormat.getCurrencyInstance(new Locale("en", "in"));
List<Object[]> milestoneStatus = new ArrayList<Object[]>();
milestoneStatus.add(new Object[]{1, "Demand Initiated"});
milestoneStatus.add(new Object[]{2, "Demand Approved"});
milestoneStatus.add(new Object[]{3, "Tender Opening"});
milestoneStatus.add(new Object[]{4, "Order Placement"});
milestoneStatus.add(new Object[]{5, "PDR"});
milestoneStatus.add(new Object[]{6, "SO for Critical BoM by Dev Partner"});
milestoneStatus.add(new Object[]{7, "DDR"});
milestoneStatus.add(new Object[]{8, "CDR"});
milestoneStatus.add(new Object[]{9, "Acceptance of Critical BoM by Dev Partner"});
milestoneStatus.add(new Object[]{10, "FAT Completed"});
milestoneStatus.add(new Object[]{11, "Delivery at Stores"});
milestoneStatus.add(new Object[]{12, "SAT / SoFT"});
milestoneStatus.add(new Object[]{13, "Available for Integration"});
%>
<% 
    String ses = (String) request.getParameter("result");
    String ses1 = (String) request.getParameter("resultfail");
    if (ses1 != null) { %>
    <div align="center">
        <div class="alert alert-danger" role="alert">
            <%=StringEscapeUtils.escapeHtml4(ses1) %>
        </div>
    </div>
<% }if (ses != null) { %>
    <div align="center">
        <div class="alert alert-success" role="alert">
            <%=StringEscapeUtils.escapeHtml4(ses) %>
        </div>
    </div>
<% } %>
<br>
<div class="container-fluid">
		<div class="row">
			<div class="col-md-12">
				<div class="card shadow-nohover">
					<div class="row card-header">
			   			<div class="col-md-6">
							<h4>Procurement Status</h4>
						</div>
						<div class="col-md-2">
							<%-- <form method="post" action="ProjectBriefing.htm" target="_blank">
								<input type="hidden" name="projectid" value="<%=projectid%>"/>
								<button type="submit" ><img src="view/images/preview3.png"></button>
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							</form> --%>
						</div>				
						<!-- <div class="col-md-10" style="float: right; margin-top: -8px;">
						   <div class="form-inline" style="justify-content: end;margin-bottom:3rem;">
						   <form action="CCMReport.htm" method="POST" id="ccmReport" autocomplete="off"> 
						   <input type="hidden" name="_csrf" value="9b5c3d2d-453a-4566-8d9b-9c311c9ea7d9"> 
						   <table>
					   					<tbody><tr>
					   						<td>
					   							<label class="control-label" style="font-size: 14px; margin-bottom: .0rem;">Date : </label>
					   						</td>
					   						<td>
					   						  <input onchange="this.form.submit()" class="form-control date" type="text" name="DateCCM" id="date" readonly="readonly" style="width: 11rem; background-color:white; text-align: left;" value="26-06-2023"> 
					   						</td>
					   						
					   						<td>
					   							<label class="control-label" style="font-size: 14px; margin-bottom: .0rem;">Select: </label>
					   						</td>
					   						<td>
					   						 <select class="form-control selectdee select2-hidden-accessible" id="selDigitType" name="DigitSel" required="required" onchange="this.form.submit()" data-live-search="true" data-select2-id="selDigitType" tabindex="-1" aria-hidden="true">
                                               <option value="Rupees" selected="selected" data-select2-id="2">Rupees</option>
						                       <option value="Lakhs">Lakhs</option>
						                       <option value="Crores">Crores</option>     
											</select><span class="select2 select2-container select2-container--default" dir="ltr" data-select2-id="1" style="width: 96px;"><span class="selection"><span class="select2-selection select2-selection--single" role="combobox" aria-haspopup="true" aria-expanded="false" tabindex="0" aria-disabled="false" aria-labelledby="select2-selDigitType-container"><span class="select2-selection__rendered" id="select2-selDigitType-container" role="textbox" aria-readonly="true" title="Rupees">Rupees</span><span class="select2-selection__arrow" role="presentation"><b role="presentation"></b></span></span></span><span class="dropdown-wrapper" aria-hidden="true"></span></span>					   		
					   				       </td>
					   					</tr>
					   		</tbody></table>			
						   </form>
						   </div>
						 </div>	 -->	
						<div class="col-md-3 justify-content-end style1">
						   <div class="form-inline style2">
						  <table >
					   					<tbody><tr>
					   						<td class="border-0">
					   							<label class="control-label style4 ">Project : </label>
					   						</td>
									<td class="style3">
								<%-- 		<form method="post" action="ProcurementStatus.htm" id="projectchange" >
											
											<select class="form-control selectdee select2-hidden-accessible" name="projectid"  required="required" style="width:200px;" data-live-search="true" data-container="body" onchange="submitForm('projectchange');">
												<%for(Object[] obj : projectslist){
												String projectshortName=(obj[17]!=null)?" ( "+obj[17].toString()+" ) ":"";
													%>
												<option value=<%=obj[0]%><%if(projectId.equals(obj[0].toString())){ %> selected="selected" <%} %> ><%=obj[4]+projectshortName %></option>
												<%} %>
											</select>
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										</form> --%>
										
											<form method="post" action="ProcurementStatus.htm" id="projectchange" >
											<select class="form-control selectdee style5" name="projectid"  required="required" data-live-search="true" data-container="body" onchange="submitForm('projectchange');">
											<% for (Object[] obj : projectslist) {
    										String projectshortName=(obj[17]!=null)?" ( "+StringEscapeUtils.escapeHtml4(obj[17].toString()) +" ) ":"";
    										%>
											<option value="<%=obj[0]%>" <%if(obj[0].toString().equalsIgnoreCase(projectId)){ %>selected="selected" <%} %>> <%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()):" - "%> <%= projectshortName!=null?StringEscapeUtils.escapeHtml4(projectshortName):" - "%>  </option>
											<%} %>
											</select>
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										</form>
									</td>
								</tr>
							</table>
							
						</div>
						</div>
					 </div>
					 	 <div class="card-body">	
					 	      <div class="table-responsive">
	                              <table class="table table-bordered table-hover table-striped table-condensed "  id="myTable"> 
	                                  <thead>
	                                     <tr>
	                                      <th>SN</th>
	                                      <th>DemandNo</th>
	                                      <th>Item Nomenclature</th>
	                                      <th>Estimated cost</th>
	                                      <th>Status</th>
	                                      <th>Procurement Milestone</th>
	                                      <th>Order Details</th>
	                                     </tr>
	                                 </thead>
	                                 <tbody>
	                                      <%if(fileStatusList!=null){ int SN=1;%>
	                                      <%for(Object[] fileStatus:fileStatusList){ %>
	                                      <tr>
                                            <td class="style6"><%=SN++%></td>
                                            <td><% if(fileStatus[1]!=null){ %> <%=fileStatus[1]!=null?StringEscapeUtils.escapeHtml4(fileStatus[1].toString()):"-" %><%}else %>--</td>
                                            <td><%=fileStatus[4]!=null?StringEscapeUtils.escapeHtml4(fileStatus[4].toString()):"-"%></td>
                                            <td class="style7">
                                            <%if(fileStatus[3]!=null) {%>
                                            <%=format.format(new BigDecimal(fileStatus[3].toString())).substring(1)%>
                                            <%}else{ %>--<%} %>
                                            </td>
                                            <td >
                                              <table class="style8">
                                              
                                                <%if(fileStatus[8]!=null ){
                                            	  if(fileStatus[8].toString().equalsIgnoreCase("Y")){
                                            	  %>
                                            	
                                              <tr> 
                                              <td>
                                              <form id="enviEditForm" action="#"  >
                                              <button class="btn btn-sm"  formaction="enviEdit.htm" id="enviEditBtn" type="button"  onclick="openEnviform('<%=fileStatus[0]%>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></button>
                                            <button class="btn btn-sm" id="fileCloseBtn"  onclick="return fileClose()" formaction="FileInActive.htm"><i class="fa fa-times" aria-hidden="true" ></i></button>
                                             <input type="hidden" name="itemN" id="itemN" >
                                             <input type="hidden" name="estimatedCost" id="estimatedCost">
                                             <input type="hidden" name="PDOfInitiation" id="PDOfInitiation">
                                             <input type="hidden" name="status" id="status">
                                             <input type="hidden" name="remarks" id="remarks">
                                             <input type="hidden" name="fileId" id="fileId" value="<%=fileStatus[0]%>">
                                             <input type="hidden" name="projectId" value="<%=projectId%>">
                                             </form> </td>
                                              </tr>
                                              <%} }%>
                                              
                                               <tr>
                                               <% if(fileStatus[7] !=null){ %>
                                                <td><%=fileStatus[6]!=null?StringEscapeUtils.escapeHtml4(fileStatus[6].toString()):"-"%></td>
                                               <%--  <%if(!fileStatus[7].toString().equals("19")){ %> --%>
                                                    <td class="style6">
                                                     <%if(fileStatus[10]!=null && fileStatus[10].toString().equalsIgnoreCase("M")){ %>
                                                     <button class="btn btn-sm"  data-toggle="tooltip" data-placement="top" title="Edit Demand" onclick="manualDemandEdit('<%=fileStatus[0]%>','<%=fileStatus[1]%>','<%=fileStatus[2]%>','<%=fileStatus[3]%>','<%=fileStatus[4]%>')">
                                                     <i class="fa fa-pencil-square-o" aria-hidden="true"></i></button>
                                                     <%}%>
                                                    </td>
                                                    <td>
                                                    <%if(Long.parseLong(fileStatus[7].toString())!=25){ %>
                                                     <%if(fileStatus[10]!=null && fileStatus[10].toString().equals("I")) {%> 
                                                     <button class="btn btn-sm" data-toggle="tooltip" data-placement="top" title="Demand Status" onclick="openEditform('<%=fileStatus[0]%>','<%=fileStatus[1]%>',<%=fileStatus[7]%>,'<%=fileStatus[4]%>','<%=fileStatus[10]%>')"><i class="fa fa-eye" aria-hidden="true"></i></button>
                                                   <%}else{ %>
                                                   <form action="updateManualDemand.htm" method="post">
                                                     <button type="submit" class="btn btn-sm"  data-toggle="tooltip" data-placement="top" title="Demand Status" ><i class="fa fa-eye" aria-hidden="true"></i></button>
                                                      <input type="hidden" name="fileId" value="<%=fileStatus[0]%>"/>
	                                                  <input type="hidden" name="projectId" value=<%=projectId%>/>
	                                                  <input type="hidden" name="demandNo" value="<%=fileStatus[1]%>"/>
	                                                  <input type="hidden" name="statusId" value="<%=fileStatus[7]%>"/>
                                                      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />  
                                                   </form>
                                                   <%} }%>
                                                    </td>
                                                   <td class="style9">  
                                                   <form action="FileInActive.htm" method="post">
	                                                   <input type="hidden" name="fileId" value="<%=fileStatus[0]%>" />
	                                                   <input type="hidden" name="projectId" value=<%=projectId%> />
	                                                   <input type="hidden" name="demandNo" value="<%=fileStatus[1]%>" />
	                                                   <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	                                                   <%if(Long.parseLong(fileStatus[7].toString())!=25){ %>                                         
	                                                   <button class="btn btn-sm" data-toggle="tooltip" title="Demand Inactive" onclick="return confirm('Are You Sure To InActive ?')"><i class="fa fa-times" aria-hidden="true"></i></button>
	                                                  <%--  <button class="btn btn-sm" type="button" onclick="openPDCform('<%=fileStatus[0]%>')">
                                                  	  	<i class="fa fa-calendar" aria-hidden="true"></i>
                                                  	   </button> --%>
                                                  	   <%} %>
	                                                   <%if(Long.parseLong(fileStatus[7].toString())>9 && fileStatus[10]!=null ){ %>
	                                                   <button class="btn btn-sm " data-toggle="tooltip" title="Refresh Demand" formaction="FileOrderRetrive.htm" title="Refresh Order"> <i class="fa fa-refresh" aria-hidden="true"></i></button>
	                                                   <%} %>
                                                   </form>
                                                   </td>
                                                  <%}%>
                                               </tr>
                                              </table>
                                            </td>
                                            <td class="style6">
                                            <%if(fileStatus[10]!=null){ %>
                                            <button class="btn btn style10" type="button" onclick="openMilestoneModal('<%=fileStatus[0]%>','<%=fileStatus[1]%>','<%=fileStatus[4]%>','<%=fileStatus[2]%>')"
                                             data-toggle="tooltip"  data-toggle="tooltip" data-placement="top"  title="Add Procurement Milestone">
                                            <i class="fa fa-list style11" aria-hidden="true"></i>
                                            </button>
                                            <%}else{ %> -- <%} %>
                                           <%
											if (pftsMilestoneList != null && pftsMilestoneList.size()>0 && !pftsMilestoneList.isEmpty()) {								
											    boolean matchFound = pftsMilestoneList.stream().anyMatch(milestone -> milestone[1] != null && milestone[1].equals(fileStatus[0]));
											    if (matchFound) {
											%>
                                           <form action="pftsMilestoneView.htm" method="get" class="style12">
                                              <button class="btn btn style13" type="submit" id="viewBtn" name="PftsFileId" value="<%=fileStatus[0]%>">View</button>
                                              <input type="hidden" name="ProjectId" value="<%=fileStatus[19]%>">
                                              <input type="hidden" name="demandNumber" value="<%=fileStatus[1]%>">
                                           </form>
                                           <%} }%>
                                            </td>
                                            <td class="style6">
                                            <%if(fileStatus[1]!=null && Long.parseLong(fileStatus[7].toString())>=10){ %>
                                            <%if(fileStatus[10]!=null && fileStatus[10].toString().equalsIgnoreCase("M")){ %>
                                            <button type="button" id="orderstatus" onclick="manualOrderStatus('<%=fileStatus[0]%>','<%=fileStatus[1]%>')" class="btn btn-sm submit" >Order View</button>
                                            <%}else{ %>
                                             <button type="button" id="orderstatus" onclick="ibasOrderStatus('<%=fileStatus[0]%>','<%=fileStatus[1]%>')" class="btn btn-sm submit" >Order View</button>
                                            <%} %>
                                            <%}else{ %> -- <%} %>
                                            </td>
	                                      </tr>
	                                      <%} }%>
	                                 </tbody>
	                              </table>
	                        </div>
	                        <div align="center">
	                          <form action="#" id="enviForm" method="post">
	                                <input type="hidden" name="projectId" value=<%=projectId%> />
	                        		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	                        		
	                        		<button  class="btn add" type="button" formaction="AddNewDemandFile.htm" id="ibasAddBtn" onclick="addIbis()">Add Demand From IBAS</button>
 	                        		<button  class="btn btn-info style14" type="button" id="manualAddBtn" onclick="addManual()" formaction="AddManualDemand.htm">MANUAL DEMAND</button>
	                        		<button  class="btn btn-success style14" type="button" id="enviBtn" onclick="addEnvi()" formaction="envisagedAction.htm">ENVISAGED DEMAND</button>
	                        		<button type="button" class="btn btn-warning style15"  type="button" id="" onclick="showManualDemand()" data-toggle="tooltip"  data-toggle="tooltip" data-placement="top"  title="Excel Upload" ><i class="fa fa-file-excel-o" aria-hidden="true"></i> &nbsp;UPLOAD</button>
	                        		
	                           </form>
	                        
	                        
	                           <%-- <form action="AddNewDemandFile.hmt" method="post">
	                                <input type="hidden" name="projectId" value=<%=projectId%> />
	                        		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	                        		<button  class="btn add" type="submit">Add Demand</button>
	                           </form> --%>
	                        </div>
					 	</div>
					</div>
			</div>
		</div>
		

		
		<!-- Excel Upload-->
<div class="modal fade bd-example-modal-lg" id="MANUALDEMANDS" >
  <div class="modal-dialog modal-lg">
    <div class="modal-content style16">
      <div class="modal-header bg-primary text-light"  >
        <h5 class="modal-title" id="exampleModalLabel">MANUAL DEMANDS (<%=projectcode %>)</h5>
		<form action="ManualDemandExcel.htm" method="post">
		<button class="btn btn-sm"  data-toggle="tooltip" type="submit" data-toggle="tooltip" data-placement="top"  title="Download Format" ><i class="fa fa-download fa-lg" aria-hidden="true"></i>
		&nbsp; Sample Format
		</button>
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
		</form>
      </div>
       <form action="ManualDemandExcelSubmit.htm" method="post" id="excelForm" enctype="multipart/form-data">
      <div class="modal-body">
      <div class="col-md-4 mb-4">
      <input class="form-control" type="file" id="excel_file" name="filename" required="required"  accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">						
      </div>
      <table class="table table-bordered table-hover">
      <thead>
      <tr>
  	  <th colspan="1" class="style17">SN</th>
      <th colspan="2" class="style18" >Demand No</th>
      <th colspan="2" class="style19">Demand Date</th>
      <th colspan="2" class="style19">Estimated Cost</th>
      <th colspan="2" class="style20">Item Name</th>
      </tr>
      </thead>
      <tbody id="overalltbody">
      <tr>
      <td colspan="9" class="style21">No Data Available!</td>
      </tr>
      </tbody>
      </table>
      </div>
      <hr>
      <div align="center" class="m-2">
		<input type="hidden" name="ProjectId" value="<%=projectId%>">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
      <button type="submit" class="btn btn-sm submit" onclick="return confirm('Are you sure to submit?')">Submit</button>
      <button type="button" class="btn btn-sm btn-secondary style22" data-dismiss="modal">Close</button>
      </div>
      </form>
    </div>
  </div>
</div>
<!--  -->
	</div>
	
<div class="modal fade bd-example-modal-lg" id="exampleModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document">
		   <div class="modal-content style23">
			<form action="upadteDemandFile.htm" id="form1" method="post" >
					<div class="modal-header style24">
						<h5 class="modal-title" id="statusModalLabel"></h5>
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
					</div>
					<div class="modal-body">
						<div class="row" >
							<div class="col-md-3">
								<label class="control-label" > Procurement Status :</label>
								</div>
								<div class="col-md-4 style25">
								 <select
									class="form-control selectdee style26"
									id="procstatus" required="required" name="procstatus">
								</select>
								</div>
							
							<div class="col-md-2" >
							   <label class="control-label" >Event Date :</label>
							   </div>
							   <div class="col-md-3" >
							   <input class="style27"
								type="text" class="form-control" name="eventDate" id="eventDate"
								required="required" readonly="readonly">
						    </div>
						</div>
						<div class="row mt-4">
						<div class="col-md-3" >
								<label> Remarks : </label> 
								</div>
								<div class="col-md-8 style28">
								<input type="text" class="form-control style29"
									name="procRemarks" id="procRemarks" required="required">
							</div>
							</div>
					        <br>
						<div align="center">
							<button type="button" class="btn btn-sm submit" onclick="submitStatus()">Update</button>
							     <input type="hidden" name="fileId" id="updateprocFileId"  value=""/>
                                 <input type="hidden" name="demandNo" id="updateprocDemand"  value=""/>
                                 <input type="hidden" name="eventDate" id="updateeventDate"  value=""/>
                                 <input type="hidden" name="remarks" id="updateprocRemarks" value="">
                                 <input type="hidden" name="statusId" id="updateStatus" value="">
                                 <input type="hidden" name="demandtype" id="demandtype" value="">
                                 <input type="hidden" name="projectId" value=<%=projectId%> />
                                 <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							  
							  
						</div>
						<br>
						<div class="col-md-12 style30">
							<div>
								<%
								int i = 0;
								for (Object[] obj1 : pftsStageList) {
									if (i == 9)
										break;
								%>
								<p class="pstatus" id="<%=obj1[0].toString()%>"><%=obj1[0]!=null?StringEscapeUtils.escapeHtml4(obj1[0].toString()):"-"%>. <%=obj1[2]!=null?StringEscapeUtils.escapeHtml4(obj1[2].toString()):"-"%>
								</p>
								<%
								i++;
								}
								%>
							</div>
							<div>
								<%
								int j = 0;
								for (Object[] obj1 : pftsStageList.stream().skip(9).collect(Collectors.toList())) {
									if (j == 9)
										break;
								%>
								<p class="pstatus" id="<%=obj1[0].toString()%>"><%=obj1[0]!=null?StringEscapeUtils.escapeHtml4(obj1[0].toString()):"-"%>. <%=obj1[2]!=null?StringEscapeUtils.escapeHtml4(obj1[2].toString()):"-"%>
								</p>
								<%
								j++;
								}
								%>
							</div>
							<div>
								<%
								int k = 0;
								for (Object[] obj1 : pftsStageList.stream().skip(18).collect(Collectors.toList())) {
									if (k == 9)
										break;
								%>
								<p class="pstatus" id="<%=obj1[0].toString()%>"><%=obj1[0]!=null?StringEscapeUtils.escapeHtml4(obj1[0].toString()):"-"%>. <%=obj1[2]!=null?StringEscapeUtils.escapeHtml4(obj1[2].toString()):"-" %>
								</p>
								<%
		                        k++;
		                        } %>
						  </div>
						</div>
					</div>
			</form>
		</div>
	</div>
</div>

<div class="modal fade" id="PDCModal" tabindex="-1" role="dialog" aria-labelledby="PDCModalLabel" aria-hidden="true">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<form action="UpdateFilePDCInfo.htm" method="post" >
				<div class="modal-header">
					<div>
						<h5 class="modal-title" id="PDCModalLabel">Modal title</h5>
					</div>

					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<div class="row">
						<div class="col-md-4">
							<label class="control-label">PDC</label> 
							<input type="text" class="form-control" name="PDCDate" id="PDCDate" required="required" readonly="readonly">
						</div>

						<div class="col-md-8">
							<label class="control-label">Available for Integration by</label> 
							<input type="text" class="form-control" name="IntegrationDate" id="IntegrationDate" required="required" readonly="readonly" >
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<input type="hidden" name="projectId" value="<%=projectId%>" /> 
					<input type="hidden" name="fileId" id="pdcFileId" /> 
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<button type="button" class="btn btn-sm btn-danger" data-dismiss="modal"><b>CLOSE</b></button>
					<button type="submit" class="btn btn-sm submit">Submit</button>
				</div>
			</form>
		</div>
	</div>
</div>
<div class="modal fade bd-example-modal-lg" id ="exampleModalOrder" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-jump">
    <div class="modal-content style32">
				<div class="modal-header">
					<h5  class="modal-title style33"></h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body" id="containermodal">
				</div>
			</div>
	 </div>
</div>
	
<div class="modal fade bd-example-modal-lg" id ="exampleIbasOrder" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-jump">
    <div class="modal-content style34">
				<div class="modal-header">
					<h5  class="modal-title style35"></h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body" id="ibasordermodal">
				</div>
			</div>
		</div>
	</div>
	
<div class="modal fade bd-example-modal-lg" id ="manualDemandEditModal" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-jump">
    <div class="modal-content style36">
				<div class="modal-header">
					<h5 class="modal-title style37">Edit Demand Details</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
				 <form action="manualDemandEditSubmit.htm" method="post">
                   <div class="row">
		                 <div class="col-md-2">
		                      <label class="control-label">Project</label>
		                   </div>
		                    <div class="col-md-4">
			                      <select class="form-control selectdee style26 " id="ProjectId" name="ProjectId">
											<% for (Object[] obj : projectslist) {
											String projectshortName=(obj[17]!=null)?" ( "+StringEscapeUtils.escapeHtml4(obj[17].toString())+" ) ":"";
											%>
											<%if(projectId!=null && projectId.equalsIgnoreCase(obj[0].toString())){ %>
											<option value="<%=obj[0]%>"><%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()):"-"%> <%=projectshortName!=null?StringEscapeUtils.escapeHtml4(projectshortName):"-"%></option>
											<%}} %> 
  								  </select>
		                   </div>
		                   
		                    <div class="col-md-2">
		                      <label class="control-label">Demand No.</label>
		                   </div>
		                    <div class="col-md-4">
			                    <input  class="form-control description-input"  name="demandno" id="demandno"  required="required"  placeholder="Enter Demand Number">
		                        <span id="demandMessage"></span>
		                    </div>
		             </div>
		             <br>
		             <div class="row">
		                  <div class="col-md-3">
		                      <label class="control-label">Demand Date</label>
		                   </div>
		                    <div class="col-md-3">
			                     <input  class="form-control form-control date style38"  data-date-format="dd-mm-yyyy" id="demanddate" name="demanddate" >
		                    </div>
		                    
		                    <div class="col-md-3">
		                      <label class="control-label">Estimated Cost (&#8377;)</label>
		                   </div>
		                    <div class="col-md-3">
			                    <input type="number" class="form-control style39"  name="estimatedcost" id="estimatedcost" >		
		                    </div>
		             </div>
		             <br>
		             <div class="row">
		                 <div class="col-md-3">
		                      <label class="control-label">Item Name</label>
		                   </div>
		                    <div class="col-md-9 style40">
			                    <input type="text" class="form-control style41"  name="itemname" id="itemname">		
		                   </div>
		             </div>
		             
		          <br>
		        <div class="form-group" align="center" >
					 <button type="submit" class="btn btn-primary btn-sm submit" id="editSubmit" value="SUBMIT" onclick ="return confirm('Are you sure to submit?')">SUBMIT </button>
					 	<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
					 <input type="hidden" name="procfileId" id="procfileId" value="">
				</div>
			 </form>
				</div>
		</div>
	 </div>
</div>


<%-- <div class="modal fade bd-example-modal-lg" id="milestoneModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content" style="width: 115%;margin-left: -9%">
      <div class="modal-header">
        <h5 class="modal-title" id="milestoneModalLabel">
        </h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
		 <div class="container">
		       <form action="addProcurementMilestone.htm" method="post">
		        <!-- Labels for all fields -->
		        <div class="form-row">
		         <div class="form-group col-md-1" align="center">
		                <label for="sn">SN</label>
		            </div>
		            <div class="form-group col-md-5" align="center">
		                <label for="name">Status Name</label>
		            </div>
		             <div class="form-group col-md-3" align="center">
		                <label for="date">Probable Date</label>
		            </div>
		            <div class="form-group col-md-3" align="center">
		                <label for="date">Actual Date</label>
		            </div>
		        </div>
		     
		        <% 
		        int count=0;
		        if(milestoneList!=null && milestoneList.size()>0 && milestoneList.stream().filter(e -> e[1].equals(fileStatus[0])).findAny().isPresent()){
		        	for(Object[] obj : milestoneList) { 
		        	%>
		            <div class="form-row">
		                <div class="form-group col-md-1">
		                   <input class="form-control custom-sn-style" type="text" value="<%=++count %>" style="font-size: 16px; line-height:17px;font-weight: 500; text-align: center;">
		                   <input type="hidden" name="statusId" value="<%= obj[0] %>">
		                </div>
		                <div class="form-group col-md-5">
                            <input type="text" class="form-control custom-style" id="statusname_<%= obj[0] %>" name="statusName" value="<%= obj[11] %>" style="font-size: 16px; line-height:17px;font-weight: 500;">
                        </div>
		                <div class="form-group col-md-3">
		                   <% if(obj[7].toString().equalsIgnoreCase("1")){%>
                                 <input type="text" class="form-control" id="probdate_<%= obj[0] %>" name="probabaleDate" value="<%=fc.sdfTordf(fileStatus[2].toString()) %>" readonly style="line-height: 17px;">
                           <%}else if(obj[7].toString().equalsIgnoreCase("3") && fileStatus[11]!=null){%>
		                       <input type="text" class="form-control" id="probdate_<%= obj[0] %>" name="probabaleDate" readonly value="<%=fc.sdfTordf(obj[6].toString()) %>" style="line-height: 17px;">
		                   <%}else if(obj[7].toString().equalsIgnoreCase("6") && fileStatus[12]!=null){ %>
		                       <input type="text" class="form-control" id="probdate_<%= obj[0] %>" name="probabaleDate" readonly value="<%=fc.sdfTordf(obj[6].toString()) %>" style="line-height: 17px;">
		                   <%}else if(obj[7].toString().equalsIgnoreCase("10") && fileStatus[13]!=null){%>
		                       <input type="text" class="form-control" id="probdate_<%= obj[0] %>" name="probabaleDate" readonly value="<%=fc.sdfTordf(obj[6].toString()) %>" style="line-height: 17px;">
		                   <%}else if(obj[7].toString().equalsIgnoreCase("11") && fileStatus[14]!=null){ %>
		                       <input type="text" class="form-control" id="probdate_<%= obj[0] %>" name="probabaleDate" readonly value="<%=fc.sdfTordf(obj[6].toString()) %>" style="line-height: 17px;">
		                   <%}else if(obj[7].toString().equalsIgnoreCase("12") && fileStatus[21]!=null){%>
		                       <input type="text" class="form-control" id="probdate_<%= obj[0] %>" name="probabaleDate" readonly value="<%=fc.sdfTordf(obj[6].toString()) %>" style="line-height: 17px;">
		                   <%}else if(obj[7].toString().equalsIgnoreCase("13") && fileStatus[15]!=null){ %>
		                       <input type="text" class="form-control" id="probdate_<%= obj[0] %>" name="probabaleDate" readonly value="<%=fc.sdfTordf(obj[6].toString()) %>" style="line-height: 17px;">
		                   <%}else if(obj[7].toString().equalsIgnoreCase("15") && fileStatus[22]!=null){%>
		                       <input type="text" class="form-control" id="probdate_<%= obj[0] %>" name="probabaleDate" readonly value="<%=fc.sdfTordf(obj[6].toString()) %>" style="line-height: 17px;">
		                   <%}else if(obj[7].toString().equalsIgnoreCase("25") && fileStatus[18]!=null){ %>
		                       <input type="text" class="form-control" id="probdate_<%= obj[0] %>" name="probabaleDate" readonly value="<%=fc.sdfTordf(obj[6].toString()) %>" style="line-height: 17px;">
		                   <%}else if(obj[7].toString().equalsIgnoreCase("14") && fileStatus[16]!=null){%>
		                       <input type="text" class="form-control" id="probdate_<%= obj[0] %>" name="probabaleDate" readonly value="<%=fc.sdfTordf(obj[6].toString()) %>" style="line-height: 17px;">
		                   <%}else if(obj[7].toString().equalsIgnoreCase("17") && fileStatus[17]!=null){ %>
		                       <input type="text" class="form-control" id="probdate_<%= obj[0] %>" name="probabaleDate" readonly value="<%=fc.sdfTordf(obj[6].toString()) %>" style="line-height: 17px;">
		                   <%}else if(obj[7].toString().equalsIgnoreCase("19") && fileStatus[20]!=null){%>
		                       <input type="text" class="form-control" id="probdate_<%= obj[0] %>" name="probabaleDate" readonly value="<%=fc.sdfTordf(obj[6].toString()) %>" style="line-height: 17px;">
		                   <%}else if(obj[7].toString().equalsIgnoreCase("20") && fileStatus[23]!=null){ %>
		                       <input type="text" class="form-control" id="probdate_<%= obj[0] %>" name="probabaleDate" readonly value="<%=fc.sdfTordf(obj[6].toString()) %>" style="line-height: 17px;">
		                   <%}else{ %>
		                       <input type="text" class="form-control date-picker" id="probdate_<%= obj[0] %>" name="probabaleDate" value="<%=fc.sdfTordf(obj[6].toString()) %>" style="line-height: 17px;">
		                   <%} %>
		                </div>
		               <div class="form-group col-md-3">
						 <%
						     Object inputValue = "";
						     if (obj[7].toString().equalsIgnoreCase("3")) {
						    	 inputValue = (fileStatus[11] != null) ? fc.sdfTordf(fileStatus[11].toString()) : "";
						     } else if (obj[7].toString().equalsIgnoreCase("6")) {
						    	 inputValue = (fileStatus[12] != null) ? fc.sdfTordf(fileStatus[12].toString()) : "";
						     } else if (obj[7].toString().equalsIgnoreCase("10")) {
						    	 inputValue = (fileStatus[13] != null) ? fc.sdfTordf(fileStatus[13].toString()) : "";
						     } else if (obj[7].toString().equalsIgnoreCase("11")) {
						    	 inputValue = (fileStatus[14] != null) ? fc.sdfTordf(fileStatus[14].toString()) : "";
						     } else if (obj[7].toString().equalsIgnoreCase("12")) {
						         inputValue = (fileStatus[21] != null) ? fc.sdfTordf(fileStatus[21].toString()) : "";
						     } else if (obj[7].toString().equalsIgnoreCase("13")) {
						         inputValue = (fileStatus[15] != null) ? fc.sdfTordf(fileStatus[15].toString()) : "";
						     } else if (obj[7].toString().equalsIgnoreCase("15")) {
						         inputValue = (fileStatus[22] != null) ? fc.sdfTordf(fileStatus[22].toString()) : "";
						     } else if (obj[7].toString().equalsIgnoreCase("25")) {
						         inputValue = (fileStatus[18] != null) ? fc.sdfTordf(fileStatus[18].toString()) : "";
						     } else if (obj[7].toString().equalsIgnoreCase("1")) {
						         inputValue = (fileStatus[2] != null) ? fc.sdfTordf(fileStatus[2].toString()) : "";
						     }else if (obj[7].toString().equalsIgnoreCase("14")) {
						         inputValue = (fileStatus[16] != null) ? fc.sdfTordf(fileStatus[16].toString()) : "";
						     }else if (obj[7].toString().equalsIgnoreCase("17")) {
						         inputValue = (fileStatus[17] != null) ? fc.sdfTordf(fileStatus[17].toString()) : "";
						     }else if (obj[7].toString().equalsIgnoreCase("19")) {
						         inputValue = (fileStatus[20] != null) ? fc.sdfTordf(fileStatus[20].toString()) : "";
						     }else if (obj[7].toString().equalsIgnoreCase("20")) {
						         inputValue = (fileStatus[23] != null) ? fc.sdfTordf(fileStatus[23].toString()) : "";
						     }
						 %>
						 <input type="text" class="form-control date-picker1" id="actualdate_<%= obj[0] %>" name="actualDate" value="<%=inputValue%>" disabled style="line-height: 17px;">
						</div>
		            </div>
		        <% }
		        }else{
		        for(Object[] data : milestoneStatus) { %>
		            <div class="form-row">
		                <div class="form-group col-md-1">
		                   <input class="form-control custom-sn-style" type="text" value="<%=++count %>" style="font-size: 16px; line-height:17px;font-weight: 500; text-align: center;">
		                   <input type="hidden" name="statusId" value="<%= data[0] %>">
		                </div>
		                <div class="form-group col-md-5">
                            <input type="text" class="form-control custom-style" id="name_<%= data[0] %>" name="statusName" value="<%= data[2] %>" style="font-size: 16px; line-height:17px;font-weight: 500;">
                        </div>
		                <div class="form-group col-md-3">
		                 <%if(data[0].toString().equalsIgnoreCase("1")){ %>
		                    <input type="text" class="form-control" id="date_<%= data[0] %>" name="probabaleDate" <%if(fileStatus[2]!=null){ %> value="<%=rdf.format(sdf1.parse(fileStatus[2].toString()))%>" <%}else{ %>value="-" <%} %> readonly style="line-height: 17px;">
		                 <%}else{ %>
		                    <input type="text" class="form-control date-picker" id="date_<%= data[0] %>" name="probabaleDate" style="line-height: 17px;">
		                 <%} %>
		                </div>
		                <div class="form-group col-md-3">
		                  <%if(data[0].toString().equalsIgnoreCase("1")){ %>
		                     <input type="text" class="form-control" id="date_<%= data[0] %>" name="actualDate" <%if(fileStatus[2]!=null){ %> value="<%=rdf.format(sdf1.parse(fileStatus[2].toString()))%>" <%}else{ %>value="-" <%} %> disabled style="line-height: 17px;">
		                   <%}else{ %>
		                     <input type="text" class="form-control" id="date_<%= data[0] %>" value="NA" name="actualDate" disabled style="line-height: 17px;">
		                   <%} %>
		                </div>
		            </div>
		        <% } 
		        }
		        %>
		        <br>
		        <div align="center">
		        <% if (milestoneList != null && milestoneList.size()>0 && !milestoneList.isEmpty()) { %>
				 <%
				if (milestoneList.stream().filter(e -> e[1].equals(fileStatus[0]) && e[9].toString().equalsIgnoreCase("Y")).findAny().isPresent()) { 
				%>
				    <button type="submit" class="btn btn-primary" name="action" value="revision" onclick="return confirm('Are You Sure To Make a Revision?')">MAKE REVISION</button>
				<% 
				} else if (milestoneList.stream().filter(e -> e[1].equals(fileStatus[0])).findAny().isPresent()) { 
				%>
				    <button type="submit" name="action" value="edit" class="btn btn-warning" onclick="return confirm('Are You Sure To Edit?')">EDIT</button>
				    <% 
				    if (milestoneList.stream().filter(e -> e[9].toString().equalsIgnoreCase("N")).findAny().isPresent()) { 
				    %>
				     <button type="submit" class="btn btn-success" name="action" value="baseline" onclick="return confirm('Once You Set Baseline, You Cannot Edit. Are You Sure To Proceed??')">SET BASELINE</button>
				    <% 
				    } 
				%>
				<% 
				}} else { 
				%>
				    <button type="submit" name="action" value="add" class="btn btn-success" onclick="return confirm('Are You Sure To Submit?')">SUBMIT</button>
				<% 
				} 
				%>
		        <input type="hidden" name="pftsFileId" id="pftsFileId" value="<%=fileStatus[0]%>">
		        <input type="hidden" name="demandnumber" value="<%=fileStatus[1]%>">
		        <input type="hidden" name="ProjectId" value="<%=projectId%>">
		        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
		        </div>
		    </form>
		</div>
      </div>
    </div>
  </div>
</div> --%>


<div class="modal fade bd-example-modal-lg" id="milestoneModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="milestoneModalLabel">
        <span class="style42"></span>
        </h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
		 <div class="container">
		     <form id="milestoneForm" action="addProcurementMilestone.htm" method="post" autocomplete="off">
			    <div class="form-row">
			        <div class="form-group col-md-1 style43" align="center">
			            <label for="sn" class="style44">SN</label>
			        </div>
			        <div class="form-group col-md-5" align="center">
			            <label for="name" class="style44">Status Name</label>
			        </div>
			        <div class="form-group col-md-3 probable-date-header" align="center">
			            <label for="date" class="style44">Probable Date</label>
			        </div>
			        <div class="form-group col-md-3 actual-date-header" align="center">
			            <label for="date" class="style44">Actual Date</label>
			        </div>
			    </div>
			
			    <div id="milestoneContainer">
			    
			    </div>
			    <div align="center">
				   <button type="button" id="submitButton"  name="action" value="add" class="btn btn-success style45" onclick="addSubmit('add')">ADD</button>
				   <input type="hidden" name="action" value="" id="actiontype">
				   <button type="button" name="action" value="edit" class="btn btn-warning style46" onclick="addSubmit('edit')">EDIT</button>
				   <button type="button" name="action" value="baseline" class="btn btn-primary style46" onclick="addSubmit('baseline')">SET BASELINE</button>
				   <button type="button" name="action" value="revise" class="btn btn-primary style46" onclick="addSubmit('revise')" >REVISE</button>
			       <input type="hidden" name="pftsfile" id="pftsfile" value="" />
			       <input type="hidden" name="demandNumber" id="demandNumber" value="" />
			       <input type="hidden" name="project"  value="<%=projectId %>" />
			       <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
			    </div>
			</form>
		  </div>
		</div>  
      </div>
    </div>
</div>


<script type="text/javascript">

function submitForm(frmid)
{ 
  
  document.getElementById(frmid).submit(); 
} 
</script>
	
<script type="text/javascript">

$(document).ready(function(){
	  $("#myTable").DataTable({
	 "lengthMenu": [  5,10,25, 50, 75, 100 ],
	 "pagingType": "simple"
	
});
  });
  
</script>

<script type="text/javascript">
function addEnvi() {
	   var form = document.getElementById("enviForm");
	   
       if (form) {
        var enviBtn = document.getElementById("enviBtn");
           if (enviBtn) {
               var formactionValue = enviBtn.getAttribute("formaction");
               
                form.setAttribute("action", formactionValue);
                 form.submit();
             }
        }
}

function addManual() {
	   var form = document.getElementById("enviForm");
	   
    if (form) {
     var manualBtn = document.getElementById("manualAddBtn");
        if (manualBtn) {
            var formactionValue = manualBtn.getAttribute("formaction");
            
             form.setAttribute("action", formactionValue);
              form.submit();
          }
     }
}

function fileClose() {
	var confirmation=confirm('Are You Sure To InActive ?');
	if(confirmation){
   var form = document.getElementById("enviEditForm");
	   
       if (form) {
        var fileCloseBtn = document.getElementById("fileCloseBtn");
           if (fileCloseBtn) {
               var formactionValue = fileCloseBtn.getAttribute("formaction");
               
                form.setAttribute("action", formactionValue);
                 form.submit();
             }
        }
	}else{
		return false;
	}
}

function addIbis() {
	   var form = document.getElementById("enviForm");
	   
       if (form) {
        var ibasAddBtn = document.getElementById("ibasAddBtn");
           if (ibasAddBtn) {
               var formactionValue = ibasAddBtn.getAttribute("formaction");
               
                form.setAttribute("action", formactionValue);
                 form.submit();
             }
        }
}

function openEnviform(PftsFileId) {
	
	$('#fileId').val(PftsFileId);
	$.ajax({
		type : "GET",
		url : "getEnviEditData.htm",
		data : {
			PftsFileId : PftsFileId
		},
		datatype : 'json',
		success : function(result) {
			var values = JSON.parse(result);
			var date = new Date(values[3]);

			// Get the date components
			var day = date.getDate().toString().padStart(2, '0'); // Get day and pad with leading zero if necessary
			var month = (date.getMonth() + 1).toString().padStart(2, '0'); // Get month (zero-based) and pad with leading zero if necessary
			var year = date.getFullYear();
			
			var formattedDate = day+'-'+month+'-'+year;
			$('#itemN').val(values[1]);
			$('#estimatedCost').val(values[2]);
			$('#PDOfInitiation').val(formattedDate);
			$('#status').val(values[4]);
			$('#remarks').val(values[5]);
			var form = document.getElementById("enviEditForm");
			   
		      if (form) {
		       var enviEditBtn = document.getElementById("enviEditBtn");
		          if (enviEditBtn) {
		              var formactionValue = enviEditBtn.getAttribute("formaction");
		              
		               form.setAttribute("action", formactionValue);
		                form.submit();
		            }
		       }
			

		}
	});	
	
	  
}
</script>


<script type="text/javascript">

function openEditform(fileId,demandid,pstatusid,itemname,demandtype){
	
	  $('#updateprocDemand').val(demandid);
	  $('#updateprocFileId').val(fileId);
	  $('#demandtype').val(demandtype);
	
	  $('#exampleModal .modal-title').html('<span class="style47">Demand No : ' + demandid + '<br> <span class="style48">Item : ' + itemname);
	  $('#exampleModal').modal('show');
	  $(".pstatus").removeClass("blinking-element"); // Remove blink class from all elements
      $("#" + pstatusid).addClass("blinking-element"); //add blink class on specific id
      
      $.ajax({
			type : "GET",
			url : "getFileViewList.htm",
			data : {
				fileId : fileId
			},
			datatype : 'json',
			success : function(result) {
				 if (result != null) {
				        var resultData = JSON.parse(result); 
				 
				        $('#procRemarks').val(resultData[9]);
				        $('#procstatus').empty();
				        if(pstatusid<10){
				        <%for(Object[] obj3:pftsStageList1){%>
				        var optionValue = <%=obj3[0]!=null?StringEscapeUtils.escapeHtml4(obj3[0].toString()):""%>;
		     		    var optionText = '<%=obj3[2]!=null?StringEscapeUtils.escapeHtml4(obj3[2].toString()):""%>';
		     	        var option = $("<option></option>").attr("value", optionValue).text(optionText);
		                  if(pstatusid==optionValue){
		                  option.prop('selected', true);
		                  }
		                  $('#procstatus').append(option);
				        <%}%>
				        }else if(pstatusid===10){
				        	 <%for(Object[] obj4:pftsStageList2){%>
						        var optionValue = <%=obj4[0]!=null?StringEscapeUtils.escapeHtml4(obj4[0].toString()):""%>;
				     		    var optionText = '<%=obj4[2]!=null?StringEscapeUtils.escapeHtml4(obj4[2].toString()):""%>';
				     	        var option = $("<option></option>").attr("value", optionValue).text(optionText);
				                  if(pstatusid==optionValue){
				                  option.prop('selected', true);
				                  }
				                  $('#procstatus').append(option);
						        <%}%>	
				        }
				        else{
				            <%for(Object[] obj4:pftsStageList3){%>
					        var optionValue = <%=obj4[0]!=null?StringEscapeUtils.escapeHtml4(obj4[0].toString()):""%>;
			     		    var optionText = '<%=obj4[2]!=null?StringEscapeUtils.escapeHtml4(obj4[2].toString()):""%>';
			     	        var option = $("<option></option>").attr("value", optionValue).text(optionText);
			                  if(pstatusid==optionValue){
			                  option.prop('selected', true);
			                  }
			                  $('#procstatus').append(option);
					        <%}%>	
				        }
				        
				       }
				   }      
			});	
}
</script>

<script type="text/javascript">
function submitStatus(){
	var remarks=$('#procRemarks').val();
	var procstatusId=$('#procstatus').val();
	var procDate=$('#eventDate').val();
	var type=$('#demandtype').val();

	$('#updateStatus').val(procstatusId);
	$('#updateprocRemarks').val(remarks);
	$('#updateeventDate').val(procDate);
	
	if(type==='I'){
		if(confirm('Are you Sure To Update ?')){
			$('#form1').submit();  
			}
	}
		
}

</script>

<script type="text/javascript">
$('#eventDate').daterangepicker({
	"singleDatePicker" : true,
	"linkedCalendars" : false,
	"showCustomRangeLabel" : true,
	/* "minDate" :new Date(), */
	"startDate" : new Date(),

	"cancelClass" : "btn-default",
	showDropdowns : true,
	locale : {
		format : 'DD-MM-YYYY'
	}
});


$('#DPdateId').daterangepicker({
	"singleDatePicker" : true,
	"linkedCalendars" : false,
	"showCustomRangeLabel" : true,
	/* "minDate" :new Date(), */
	"startDate" : new Date(),

	"cancelClass" : "btn-default",
	showDropdowns : true,
	locale : {
		format : 'DD-MM-YYYY'
	}
});

</script>
<script type="text/javascript">

$( "#selectDemand" ).change(function() {
  var selectee=$( "#selectDemand" ).val();
  if(selectee=='9'){
	  $("#orderDetails").show();
	  $('#orderNoId').prop('required', true);  
	  $('#orderCostId').prop('required', true);  
  }else{
	  $("#orderDetails").hide();
	  $('#orderNoId').removeAttr('required');
	  $('#orderCostId').removeAttr('required'); 
  }
});

</script>
<script type="text/javascript">
function openPDCform(fileId){
	$.ajax({
		type : "GET",
		url : "getFilePDCInfo.htm",
		data : {
			fileid : fileId,
		},
		datatype : 'json',
		success : function(result) {
			var values = JSON.parse(result);
            $("#pdcFileId").val(fileId);
            
            var PDCDate = new Date();
            var IntegrationDate = new Date();
            if(values[5]!==null){     PDCDate =  new Date(values[5]); }
            if(values[6]!==null){     IntegrationDate = new Date(values[6]); }
           
            $('#PDCDate').daterangepicker({
            	"singleDatePicker" : true,
            	"linkedCalendars" : false,
            	"showCustomRangeLabel" : true,
                "startDate" : PDCDate,
            	"cancelClass" : "btn-default",
            	showDropdowns : true,
            	locale : {
            		format : 'DD-MM-YYYY'
            	}
            });
            $('#IntegrationDate').daterangepicker({
            	"singleDatePicker" : true,
            	"linkedCalendars" : false,
            	"showCustomRangeLabel" : true,
            	"startDate" : IntegrationDate,
            	"cancelClass" : "btn-default",
            	showDropdowns : true,
            	locale : {
            		format : 'DD-MM-YYYY'
            	}
            });
            
            $("#PDCModalLabel").html('Demand No : '+result[1]);
		}
	});	
}

function manualOrderStatus(fileId,demandno){
	
	$('#exampleModalOrder .modal-title').html('<span class="style47">Order Details of Demand No : ' + demandno);
	$('#exampleModalOrder').modal('show');
	 $('#containermodal').empty();
	 
	 $.ajax({
		    type: "GET",
		    url: "getOrderDetailsAjax.htm",
		    data: {
		        fileId: fileId
		    },
		    dataType: 'json',
		    success: function(resultData) {
		    	
		        if (resultData != null && resultData.length > 0) {
		        	 
		           for(var i=0;i<resultData.length;i++)   {
		        	   
				   const date = new Date(resultData[i][7]);
				   const orderDateFormat = date.toLocaleDateString('en-GB', {
                   day: '2-digit',
                   month: '2-digit',
                   year: 'numeric',
                   }).replace(/\//g, '-');
					   
			      const date1 = new Date(resultData[i][8]);
				  const dpDateFormat = date1.toLocaleDateString('en-GB', {
                   day: '2-digit',
                   month: '2-digit',
                   year: 'numeric',
                   }).replace(/\//g, '-');
					  
		                // Assuming order is an object containing order details
		                var orderHtml = '';
		                orderHtml += '<form id="orderForm'+resultData[i][0]+'" action="updateManualOrderSubmit.htm" method="post">'; // Wrap the HTML inside form element
		        	    orderHtml +='<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />';
		                orderHtml += '<div class="row">';
		                orderHtml += '<div class="col-md-1"><label class="control-label">Order No :</label></div>';
		                orderHtml += '<div class="col-md-2"><input type="text" class="form-control" name="orderno" id="orderno'+resultData[i][0]+'" value="' + resultData[i][6] + '"></div>';
		                orderHtml += '<div class="col-md-1"><label class="control-label">Order Date :</label></div>';
		                orderHtml += '<div class="col-md-2"><input class="form-control form-control date datepicker2" id="datepicker2'+resultData[i][0]+'" data-date-format="dd-mm-yyyy" name="orderdate" value="' + orderDateFormat + '"></div>';
		                orderHtml += '<div class="col-md-1"><label class="control-label style49">Order Cost (&#8377;):</label></div>';
		                orderHtml += '<div class="col-md-2"><input type="number" step="0.01" class="form-control" name="ordercost" id="ordercost'+resultData[i][0]+'" value="' +resultData[i][10]  + '"></div>';
		                orderHtml += '<div class="col-md-1"><label class="control-label">DP Date :</label></div>';
		                orderHtml += '<div class="col-md-2 style50"><input class="form-control form-control date datepicker3" id="datepicker3'+resultData[i][0]+'" data-date-format="dd-mm-yyyy" name="dpdate" value="' + dpDateFormat + '"></div>';
		                orderHtml += '</div>';

		                // Add another row
		                orderHtml += '<div class="row style51">';
		                orderHtml += '<div class="col-md-1"><label class="control-label">Item For :</label></div>';
		                orderHtml += '<div class="col-md-5"><input type="text" class="form-control" name="itemfor" id="itemfor'+resultData[i][0]+'" value="' + resultData[i][9] + '"></div>';
		                orderHtml += '<div class="col-md-1"><label class="control-label">Vendor :</label></div>';
		                orderHtml += '<div class="col-md-4"><input type="text" class="form-control" name="vendor" id="vendor'+resultData[i][0]+'" value="' + resultData[i][11] + '"></div>';
		                orderHtml += '<div class="col-md-1"><button type="button" class="btn btn-primary" id="orderSubmit'+resultData[i][0]+'" onclick="updateOrder('+resultData[i][0]+')">Update</button><input type="hidden" name="orderid" id="orderid" value="'+resultData[i][0]+'"><input type="hidden" name="projectId" id="projectId" value="'+resultData[i][5]+'"></div>';
		                orderHtml += '</div><br>';
		                orderHtml += '<div class="style52"></div><br>'
		                orderHtml += '</form>'; // Close the form element
		                // Append the generated HTML to the modal body
		                $('#containermodal').append(orderHtml);
		            };
		            
		            $('.datepicker2').daterangepicker({
		    			"singleDatePicker" : true,
		    			"linkedCalendars" : false,
		    			"showCustomRangeLabel" : true,
		    			"cancelClass" : "btn-default",
		    			showDropdowns : true,
		    			locale : {
		    				format : 'DD-MM-YYYY'
		    			}
		    		});
		    		$('.datepicker3').daterangepicker({
		    			"singleDatePicker" : true,
		    			"linkedCalendars" : false,
		    			"showCustomRangeLabel" : true,
		    			"cancelClass" : "btn-default",
		    			showDropdowns : true,
		    			locale : {
		    				format : 'DD-MM-YYYY'
		    			}
		    		});
		        }
		    },
		    error: function(xhr, status, error) {
		        // Handle error if any
		       
		    }
		});

}
function updateOrder(orderId){

	var orderno=$('#orderno'+orderId).val();
	var orderdate=$('#datepicker2'+orderId).val();
	var ordercost=$('#ordercost'+orderId).val();
	var dpdate=$('#datepicker3'+orderId).val();
	var itemfor=$('#itemfor'+orderId).val();
	var vendor=$('#vendor'+orderId).val();
	
	if(confirm('Are you Sure To Update ?')){
		 $('#orderForm'+orderId).submit(); // Submit the form dynamically
		}
	
	
}
</script>
<script type="text/javascript">
function ibasOrderStatus(fileId,demandno){
	
	$('#exampleIbasOrder .modal-title').html('<span class="style53">Order Details of Demand No : ' + demandno);
	$('#exampleIbasOrder').modal('show');
	 $('#ibasordermodal').empty();
	 
	 $.ajax({
		    type: "GET",
		    url: "getOrderDetailsAjax.htm",
		    data: {
		        fileId: fileId
		    },
		    dataType: 'json',
		    success: function(resultData) {
		    	
		        if (resultData != null && resultData.length > 0) {
		        	 
		           for(var i=0;i<resultData.length;i++)   {
		        	   
				   const date = new Date(resultData[i][7]);
				   const orderDateFormat = date.toLocaleDateString('en-GB', {
                  day: '2-digit',
                  month: '2-digit',
                  year: 'numeric',
                  }).replace(/\//g, '-');
					   
			      const date1 = new Date(resultData[i][8]);
				  const dpDateFormat = date1.toLocaleDateString('en-GB', {
                  day: '2-digit',
                  month: '2-digit',
                  year: 'numeric',
                  }).replace(/\//g, '-');
					  
		                // Assuming order is an object containing order details
		                var orderHtml = '';
		        	    orderHtml +='<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />';
		                orderHtml += '<div class="row">';
		                orderHtml += '<div class="col-md-1"><label class="control-label">Order No :</label></div>';
		                orderHtml += '<div class="col-md-2"><input type="text" class="form-control" readonly="readonly" name="orderno" id="orderno'+resultData[i][0]+'" value="' + resultData[i][6] + '"></div>';
		                orderHtml += '<div class="col-md-1"><label class="control-label">Order Date :</label></div>';
		                orderHtml += '<div class="col-md-2"><input class="form-control form-control date datepicker2" id="datepicker2'+resultData[i][0]+'" data-date-format="dd-mm-yyyy" readonly="readonly" name="orderdate" value="' + orderDateFormat + '"></div>';
		                orderHtml += '<div class="col-md-1"><label class="control-label style54">Order Cost (&#8377;):</label></div>';
		                orderHtml += '<div class="col-md-2"><input type="number" step="0.01" class="form-control" readonly="readonly" name="ordercost" id="ordercost'+resultData[i][0]+'" value="' +resultData[i][10]  + '"></div>';
		                orderHtml += '<div class="col-md-1"><label class="control-label">DP Date :</label></div>';
		                orderHtml += '<div class="col-md-2 style55"><input class="form-control form-control date datepicker3" id="datepicker3'+resultData[i][0]+'" data-date-format="dd-mm-yyyy" readonly="readonly" name="dpdate" value="' + dpDateFormat + '"></div>';
		                orderHtml += '</div>';

		                // Add another row
		                orderHtml += '<div class="row style51">';
		                orderHtml += '<div class="col-md-1"><label class="control-label">Item For :</label></div>';
		                orderHtml += '<div class="col-md-5"><input type="text" class="form-control" readonly="readonly" name="itemfor" id="itemfor'+resultData[i][0]+'" value="' + resultData[i][9] + '"></div>';
		                orderHtml += '<div class="col-md-1"><label class="control-label">Vendor :</label></div>';
		                orderHtml += '<div class="col-md-4"><input type="text" readonly="readonly" class="form-control style56" name="vendor" id="vendor'+resultData[i][0]+'" value="' + resultData[i][11] + '"></div>';
		                orderHtml += '</div><br>';
		                orderHtml += '<div class="style57"></div><br>'
		                // Append the generated HTML to the modal body
		                $('#ibasordermodal').append(orderHtml);
		            };
		            
		        }
		    },
		    error: function(xhr, status, error) {
		        // Handle error if any
	
		    }
		});

}
</script>
<script type="text/javascript">

function manualDemandEdit(fileid,demandNo,demandDate,demandCost,itemName){
	
	$('#demanddate').daterangepicker({
		"singleDatePicker" : true,
		"linkedCalendars" : false,
		"showCustomRangeLabel" : true,
		"cancelClass" : "btn-default",
		showDropdowns : true,
		locale : {
			format : 'DD-MM-YYYY'
		}
	});
	
	const date = new Date(demandDate);
	const demandDateFormat = date.toLocaleDateString('en-GB', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    }).replace(/\//g, '-');
	$('#demandno').empty();
	$('#demandMessage').empty();
	$('#procfileId').val(fileid);
	$('#demandno').val(demandNo);
	$('#demanddate').val(demandDateFormat);
	$('#estimatedcost').val(demandCost);
	$('#itemname').val(itemName);
	$('#manualDemandEditModal').modal('show');
	
	
}
$('#demandno').on('input', function() {
    var demandno = $(this).val();
    if (demandno.trim() === '') {
        $('#demandMessage').text("Please Enter a Demand Number").css('color', 'blue');
        // Disable the submit button if input is empty
        $('#editSubmit').prop('disabled', true);
        return; // Exit function if input is empty
    }
    
    $.ajax({
        type: 'GET',
        url: 'checkManualDemandNo.htm',
        success: function(data) {
            var demandNumbers = JSON.parse(data);
            var isDuplicate = demandNumbers.includes(demandno.trim());
            if (isDuplicate) {
                $('#demandMessage').text("Demand Number Already Exists !").css('color', 'red');
                // Disable the submit button if Demand Number Already Exists
                $('#editSubmit').prop('disabled', true);
            } else {
                $('#demandMessage').text("Demand Number is valid").css('color', 'green');
                // Enable the submit button if Demand Number is valid
                $('#editSubmit').prop('disabled', false);
            }
        },
        error: function(xhr, status, error) {
    
        }
    });
});

$('.btn[data-toggle="tooltip"]').tooltip({
    animated: 'fade',
    placement: 'top',
    html : true,
    boundary: 'window'
});
var DemandNumbers = [];
<%if(fileStatusList!=null){
for(Object[]obj:fileStatusList){
if(obj[1]!=null){
%>
var val  = "<%=StringEscapeUtils.escapeHtml4(obj[1].toString()) %>"
	DemandNumbers.push(val)
<%}}}%>

console.log("DemandNumbers"+DemandNumbers)
function showManualDemand(){
	$('#MANUALDEMANDS').modal('show');
}
var excel_file = document.getElementById('excel_file');

excel_file.addEventListener('change', (event) => {
	
	var reader = new FileReader();
    reader.readAsArrayBuffer(event.target.files[0]);

    reader.onload = function (event){
    
    	var data = new Uint8Array(reader.result);
    	
    	var work_book = XLSX.read(data, {type:'array'});
    	
    	var sheet_name = work_book.SheetNames;
    	
    	var sheet_data = XLSX.utils.sheet_to_json(work_book.Sheets[sheet_name[0]],{header:1});
    	

    	var checkExcel = 0;
    	table_output = '';
    	var html="";
    	var duplicate=[];
    	
    	var estimatedCost =[];
    	var demandNo =[];
    	var demandDate =[];
    	if(sheet_data.length > 0){
    		for(var row = 0; row < sheet_data.length ; row ++){
    			if(row>0){
    				table_output += '<tr>'
    					duplicate.push(sheet_data[row][1])
    			}
    			var html="";
    			
    		

    				for(var cell =0;cell<=4;cell++){
    					if(row==0){
    			 	 	if(cell==0 && "SN"!= sheet_data[row][cell]){checkExcel++;}
    					if(cell==1 && "Demand No"!= sheet_data[row][cell]){checkExcel++;}
    					if(cell==2 && !sheet_data[row][cell].startsWith("Demand Date")){checkExcel++;}
    					if(cell==3 && !sheet_data[row][cell].startsWith("Estimated")){checkExcel++;}
    					if(cell==4 && "Item Name"!= sheet_data[row][cell]){checkExcel++;}
    					}
    					
    					if(row>0){
    						if(cell==0){
    	    					html=html+'<td colspan="1" class="style6">'+sheet_data[row][cell]+'</td>'	
    	    					}else if(cell==1){
    	    						var demandNos = sheet_data[row][cell];
    	    						
    	    						if((demandNos===undefined || demandNos.length===0) && row!=0){
    	    							demandNo.push(row+1);
    	    						}
    	    						if((demandNos+"").length>15){
    	    							alert("Demand No length should be of 15 Chareacters. Demand No's length is too much for "+demandNos);
    	    							excel_file.value = '';
    	    			    			$('#overalltbody').html('<tr><td colspan="9" class="style6">No Data Available</td></tr>');
    	    			    			return;
    	    						}
    	    						if(DemandNumbers.includes(demandNos+"")){
    	    						
    	    						
    	    							alert("The same Demand No. already exists with "+demandNos+". Please check the Excel sheet once.");
    	    							excel_file.value = '';
    	    			    			$('#overalltbody').html('<tr><td colspan="9" class="style6">No Data Available</td></tr>');
    	    			    			return;
    	    						}
    	    						
    	    						html=html+'<td colspan="2" class="style6">'+sheet_data[row][cell]+'</td>'	
    	    					}else if(cell==2){
    	    						var dates = sheet_data[row][cell];
    	    						console.log(sheet_data[row][cell]+"---dates--"+typeof dates)
    	    		var formattedDate="";
    	    		if(!isNaN(dates)){
					var baseDate = new Date(1900, 0, 1);
					
					// Add the number of days (37092) to the base date
					var daysToAdd = sheet_data[row][cell];
					
					// Adjust for Excel's leap year bug (Excel incorrectly considers 1900 a leap year)
					var excelLeapYearBug = 1;
					
					var targetDate = new Date(baseDate.getTime() + (daysToAdd - excelLeapYearBug) * 24 * 60 * 60 * 1000);
					
					// Format the date to a readable string (e.g., 'YYYY-MM-DD')
					 formattedDate = targetDate.toISOString().split('T')[0];
					 formattedDate =formattedDate.split("-").reverse().join('-')
					
    	    		}				
    	    						if(dates===undefined ||  dates.length==0 ){
    	    							alert("Dates can not be blank for Demand  "+ sheet_data[row][1]);
    	    							excel_file.value = '';
    	    			    			$('#overalltbody').html('<tr><td colspan="9" class="style6">No Data Available</td></tr>');
    	    			    			return;
    	    						}
    	    						if((dates+"").split("").includes("-")){
    	    						var dates1 =dates.split("-").reverse().join('-')
    	    						var date = new Date(dates1+"");
    	    						formattedDate=sheet_data[row][cell];
    	    					  if (isNaN(date.getTime()) && row!=0) {
    	    								alert("Please give a proper date  for Demand  "+ sheet_data[row][1]);
    	        							excel_file.value = '';
    	        			    			$('#overalltbody').html('<tr><td colspan="9" class="style6">No Data Available</td></tr>');
    	        			    			return;
    	    						 } 
    	    						}
    	    					
    	    						/* demandDate */
    	    						html=html+'<td colspan="2" class="style6">'+formattedDate+'</td>'	
    	    					}
    							else if(cell==3){
    								var x=parseFloat(sheet_data[row][cell]).toFixed(2);
    								if((isNaN(x) || x===undefined)&& row!=0){
    									estimatedCost.push(sheet_data[row][1]);
    								}
    								
    	    						html=html+'<td colspan="2" class="style7">'+parseFloat(sheet_data[row][cell]).toFixed(2)+'</td>'
    	    					}
    							else{
    								var itemName=sheet_data[row][cell];
    								if(itemName==undefined || itemName.length==0){
    									alert("Item is empty for Demand No. "+ sheet_data[row][1]);
	        							excel_file.value = '';
	        			    			$('#overalltbody').html('<tr><td colspan="9" class="style6">No Data Available</td></tr>');
	        			    			return;
    								}
    								
    								if(itemName!=undefined && itemName.length>255){
    									alert("Item should be of 255 characters for Demand No. "+ sheet_data[row][1]);
	        							excel_file.value = '';
	        			    			$('#overalltbody').html('<tr><td colspan="9" class="style6">No Data Available</td></tr>');
	        			    			return;
    								}
    							
    	    						html=html+'<td colspan="2" class="style58">'+sheet_data[row][cell]+'</td>'	
    	    					}
    	    			
    					}
    					
    					if(checkExcel>0){
    		    			alert("Please Download the Manual Demands format and upload it.");
    		    			$('#overalltbody').html('<td colspan="9" class="style59">No Data Available!</td>');
    		     			excel_file.value = '';
    		  				return;
    		    		}
						
    					
    				}
    				if(row>0){
    					table_output =table_output+html+'</tr>';
    				}
    		}
    		if(demandNo.length>0){
    			alert("Demand Numbers are blank at row "+demandNo)
    			excel_file.value = '';
    			$('#overalltbody').html('<tr><td colspan="9" class="style6">No Data Available</td></tr>');
    			return;
    		}
    		
    		if(estimatedCost.length>0){
    			alert("Please provide proper cost for Demand Numbers with "+estimatedCost)
    			excel_file.value = '';
    			$('#overalltbody').html('<tr><td colspan="9" class="style6">No Data Available</td></tr>');
    			return;
    		}
    		
    		   var map = {};
    		   var duplicates = [];
    			
     	   for (let i = 0; i < duplicate.length; i++) {
    		        if (map[duplicate[i]]) {
    		            duplicates.push(duplicate[i]);
    		        } else {
    		            map[duplicate[i]] = true;
    		        }
    		    }
    
    	 	if(duplicates.length>0){
    			alert("Duplicate Demand numbers are there in Excel ("+duplicates+ ")");
    			excel_file.value = '';
    			$('#overalltbody').html('<tr><td colspan="9" class="style6">No Data Available</td></tr>');
    			return;
    		} 
    	  	if(table_output.length>0){
    			$('#overalltbody').html(table_output)
    		} else{
    			alert("No Data available in this Excel Sheet!")
    			$('#overalltbody').html('<tr><td colspan="9" class="style6">No Data is their in Excel Sheet</td></tr>');
    			excel_file.value = '';
    		} 
    	}
    }
});

function openMilestoneModal(pftsid,demandNo,item,demanddate){
	var milelist = <%= new Gson().toJson(milestoneStatus) %>;
	console.log("milelist", milelist);
	$('#milestoneModal').modal('show');
	
	$('button[name="action"][value="add"]').hide();
    $('button[name="action"][value="edit"]').hide();
    $('button[name="action"][value="revise"]').hide();
    $('button[name="action"][value="baseline"]').hide();
    $('.actual-date-header').hide();
    $('.probable-date-header').removeClass('col-md-3').addClass('col-md-6');
	
	 milelist.forEach(function(mile) {
	        var statusid = mile[0];   
	        var statusname = mile[1];
	        milestoneBody(pftsid,statusid,statusname,demanddate);
	 });
	 
	 $('#milestoneModal .modal-title').html('<span class="style60">Demand No. : ' + demandNo + '</span><br><span class="style60">Item : '+ item +'</span>');
	 $('#pftsfile').val(pftsid);
	 $('#demandNumber').val(demandNo);

	$('.date-picker').each(function() {
	    $(this).daterangepicker({
	        "singleDatePicker": true,
	        "linkedCalendars": false,
	        "showCustomRangeLabel": true,
	        /* "startDate": new Date(), */
	        "cancelClass": "btn-default",
	        "showDropdowns": true,
	        "drops": "down", 
	        "locale": {
	            "format": 'DD-MM-YYYY' 
	        }
	    }).on('show.daterangepicker', function(ev, picker) {
	        // Dynamically adjust the drop direction based on input position
	        var $this = $(this);
	        var pickerTop = $this.offset().top;
	        var modalTop = $('#milestoneModal_'+pftsid).offset().top;
	        var modalHeight = $('#milestoneModal_'+pftsid).outerHeight();
	        // Check if the picker goes out of the modal
	        if ((pickerTop - modalTop) > (modalHeight / 2)) {
	            picker.drops = 'up'; // If in lower half, open upwards
	        } else {
	            picker.drops = 'down'; // Otherwise, open downwards
	        }
	        picker.move(); 
	    });
	});
}

function milestoneBody(pftsid,statusid,statusname,demanddate){
	
    var milestoneContainer = $('#milestoneContainer');
    $('.mileContainer').remove();
    milestoneContainer.append('<div class="mileContainer row"></div>');
	
	 $.ajax({
         type: "GET",
         url: "procurementMilestoneDetails.htm",
         data: { pftsid: pftsid },
         datatype: 'json',
         success: function(result) {
             var cleanedResult = result.replace(/^"|"$/g, '').replace(/\\/g, '');
             var values = JSON.parse(cleanedResult);
             var ids = values[0][1];
             if (ids== null) {
                 // No data, show Add button and create empty fields
                 $('button[name="action"][value="add"]').show();

                 var rowHtml = 
                     '<div class="form-group col-md-1 style61" >' +
                         '<input class="form-control custom-sn-style style62" type="text" value="' + statusid + '" >' +
                         '<input type="hidden" name="statusId" value="' + statusid + '">'+
                     '</div>' +
                     '<div class="form-group col-md-6">' +
                         '<input type="text" class="form-control custom-style style63" id="statusname_' + pftsid + '" name="statusName" value="' + statusname + '">' +
                     '</div>' ;
		             if(statusid==1){
	               	   rowHtml +=
	                        '<div class="form-group col-md-5">' +
	                         '<div class="input-group">' +  
	                             '<input type="text" class="form-control probableDateField date-picker" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(demanddate) + '" readonly>' +
	                             '<div class="input-group-append">' + 
	                             '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
	                           '</div>' +
	                         '</div>' + 
	                        '</div>' ;
		           	}else{
		           		rowHtml +=
		           		'<div class="form-group col-md-5">' +
		                    '<div class="input-group">' +
		                        '<input type="text" class="form-control probableDateField date-picker" id="probdate_' + pftsid + '" name="probableDate" ' +
		                        'placeholder="Select Date">' +
		                        '<div class="input-group-append">' +
		                            '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
		                        '</div>' +
		                     '</div>' +
		                    '</div>';
		           	}
                     $('.mileContainer').append(rowHtml);
                 initializeDatePickers(); 
             } else {
            	 if(result!=null ){
                 var values = JSON.parse(result);

                 if (values.length > 0) {
                     $('button[name="action"][value="edit"]').show();
                     $('button[name="action"][value="baseline"]').show();

                     values.forEach(function(item) {
                         const probDateFormat = formatDate(item[3]);
                         var pftsid = item[0];
                         var milestoneId = item[1];
                         var PDRActualDate = item[3];
                         var showActualDate = item[27] === 'Y';

                         $('#milestonepkId').val(milestoneId);
          		
                             var rowHtml = 
                                 '<div class="form-group col-md-1 style64>' +
                                     '<input class="form-control custom-sn-style style62" type="text" value="' + statusid + '" >' +
                                     '<input type="hidden" name="pftsMilestoneId" value="' + milestoneId + '">'+
                                     '<input type="hidden" name="statusId" value="' + statusid + '">'+
                                 '</div>' +
                                 '<div class="form-group col-md-5">' +
                                     '<input type="text" class="form-control custom-style style63" id="statusname_' + pftsid + '" name="statusName" value="' + statusname + '">' +
                                 '</div>';

                          if (showActualDate) {
                        	  if(statusid==1){
                             	  rowHtml +=
                             		 '<div class="form-group col-md-3">' +
                                     '<div class="input-group">' +  
                                     '<input type="text" class="form-control date-picker" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(item[2]) + '" readonly>' +
                                         '<div class="input-group-append">' + 
                                         '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                       '</div>' +
                                     '</div>' + 
                                    '</div>'+
                                      '<div class="form-group col-md-3">' +
                                       '<div class="input-group">' +  
                                       '<input type="text" class="form-control " id="actualdate_' + pftsid + '" name="actualDate" value="' + formatDate(item[2]) + '" readonly>' +
                                           '<div class="input-group-append">' + 
                                           '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                         '</div>' +
                                       '</div>' + 
                                      '</div>' ;
                         	}else if(statusid==2){
                         		 rowHtml +=
                         			 '<div class="form-group col-md-3">' +
                                     '<div class="input-group">' +  
                                         '<input type="text" class="form-control ' + (item[3] != null ? '' : 'date-picker') + '" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(item[15]) + '" ' + (item[3] != null ? 'readonly' : '') + '>' +
                                         '<div class="input-group-append">' + 
                                         '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                       '</div>' +
                                     '</div>' + 
                                    '</div>'+
                                      '<div class="form-group col-md-3">' +
                                       '<div class="input-group">' +  
                                       '<input type="text" class="form-control" id="actualdate_' + pftsid + '" name="actualDate" value="' + formatDate(item[3]) + '" readonly>' +
                                           '<div class="input-group-append">' + 
                                           '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                         '</div>' +
                                       '</div>' + 
                                      '</div>' ;
                         	}else if(statusid==3){
                         		 rowHtml +=
                         			 '<div class="form-group col-md-3">' +
                                     '<div class="input-group">' +  
                                         '<input type="text" class="form-control ' + (item[4] != null ? '' : 'date-picker') + '" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(item[16]) + '" ' + (item[4] != null ? 'readonly' : '') + '>' +
                                         '<div class="input-group-append">' + 
                                         '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                       '</div>' +
                                     '</div>' + 
                                    '</div>'+
                                      '<div class="form-group col-md-3">' +
                                       '<div class="input-group">' +  
                                       '<input type="text" class="form-control" id="actualdate_' + pftsid + '" name="actualDate" value="' + formatDate(item[4]) + '" readonly>' +
                                           '<div class="input-group-append">' + 
                                           '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                         '</div>' +
                                       '</div>' + 
                                      '</div>' ;
                         	}else if(statusid==4){
                         		 rowHtml +=
                         			 '<div class="form-group col-md-3">' +
                                     '<div class="input-group">' +  
                                         '<input type="text" class="form-control ' + (item[5] != null ? '' : 'date-picker') + '" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(item[17]) + '" ' + (item[5] != null ? 'readonly' : '') + '>' +
                                         '<div class="input-group-append">' + 
                                         '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                       '</div>' +
                                     '</div>' + 
                                    '</div>'+
                                      '<div class="form-group col-md-3">' +
                                       '<div class="input-group">' +  
                                       '<input type="text" class="form-control" id="actualdate_' + pftsid + '" name="actualDate" value="' + formatDate(item[5]) + '" readonly>' +
                                           '<div class="input-group-append">' + 
                                           '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                         '</div>' +
                                       '</div>' + 
                                      '</div>' ;
                         	}else if(statusid==5){
                         		 rowHtml +=
                         			 '<div class="form-group col-md-3">' +
                                     '<div class="input-group">' +  
                                         '<input type="text" class="form-control ' + (item[6] != null ? '' : 'date-picker') + '" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(item[18]) + '" ' + (item[6] != null ? 'readonly' : '') + '>' +
                                         '<div class="input-group-append">' + 
                                         '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                       '</div>' +
                                     '</div>' + 
                                    '</div>'+
                                      '<div class="form-group col-md-3">' +
                                       '<div class="input-group">' +  
                                       '<input type="text" class="form-control" id="actualdate_' + pftsid + '" name="actualDate" value="' + formatDate(item[6]) + '" readonly>' +
                                           '<div class="input-group-append">' + 
                                           '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                         '</div>' +
                                       '</div>' + 
                                      '</div>' ;
                         	}else if(statusid==6){
                         		 rowHtml +=
                         			 '<div class="form-group col-md-3">' +
                                     '<div class="input-group">' +  
                                         '<input type="text" class="form-control ' + (item[11] != null ? '' : 'date-picker') + '" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(item[19]) + '" ' + (item[11] != null ? 'readonly' : '') + '>' +
                                         '<div class="input-group-append">' + 
                                         '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                       '</div>' +
                                     '</div>' + 
                                    '</div>'+
                                      '<div class="form-group col-md-3">' +
                                       '<div class="input-group">' +  
                                       '<input type="text" class="form-control" id="actualdate_' + pftsid + '" name="actualDate" value="' + formatDate(item[11]) + '" readonly>' +
                                           '<div class="input-group-append">' + 
                                           '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                         '</div>' +
                                       '</div>' + 
                                      '</div>' ;
                         	}else if(statusid==7){
                         		 rowHtml +=
                         			 '<div class="form-group col-md-3">' +
                                     '<div class="input-group">' +  
                                         '<input type="text" class="form-control ' + (item[7] != null ? '' : 'date-picker') + '" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(item[20]) + '" ' + (item[7] != null ? 'readonly' : '') + '>' +
                                         '<div class="input-group-append">' + 
                                         '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                       '</div>' +
                                     '</div>' + 
                                    '</div>'+
                                      '<div class="form-group col-md-3">' +
                                       '<div class="input-group">' +  
                                       '<input type="text" class="form-control" id="actualdate_' + pftsid + '" name="actualDate" value="' + formatDate(item[7]) + '" readonly>' +
                                           '<div class="input-group-append">' + 
                                           '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                         '</div>' +
                                       '</div>' + 
                                      '</div>' ;
                         	}else if(statusid==8){
                         		 rowHtml +=
                         			 '<div class="form-group col-md-3">' +
                                     '<div class="input-group">' +  
                                         '<input type="text" class="form-control ' + (item[8] != null ? '' : 'date-picker') + '" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(item[21]) + '" ' + (item[8] != null ? 'readonly' : '') + '>' +
                                         '<div class="input-group-append">' + 
                                         '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                       '</div>' +
                                     '</div>' + 
                                    '</div>'+
                                      '<div class="form-group col-md-3">' +
                                       '<div class="input-group">' +  
                                       '<input type="text" class="form-control" id="actualdate_' + pftsid + '" name="actualDate" value="' + formatDate(item[8]) + '" readonly>' +
                                           '<div class="input-group-append">' + 
                                           '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                         '</div>' +
                                       '</div>' + 
                                      '</div>' ;
                         	}else if(statusid==9){
                         		 rowHtml +=
                         			 '<div class="form-group col-md-3">' +
                                     '<div class="input-group">' +  
                                         '<input type="text" class="form-control ' + (item[12] != null ? '' : 'date-picker') + '" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(item[22]) + '" ' + (item[12] != null ? 'readonly' : '') + '>' +
                                         '<div class="input-group-append">' + 
                                         '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                       '</div>' +
                                     '</div>' + 
                                    '</div>'+
                                      '<div class="form-group col-md-3">' +
                                       '<div class="input-group">' +  
                                       '<input type="text" class="form-control" id="actualdate_' + pftsid + '" name="actualDate" value="' + formatDate(item[12]) + '" readonly>' +
                                           '<div class="input-group-append">' + 
                                           '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                         '</div>' +
                                       '</div>' + 
                                      '</div>' ;
                         	}else if(statusid==10){
                         		 rowHtml +=
                         			 '<div class="form-group col-md-3">' +
                                     '<div class="input-group">' +  
                                         '<input type="text" class="form-control ' + (item[9] != null ? '' : 'date-picker') + '" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(item[23]) + '" ' + (item[9] != null ? 'readonly' : '') + '>' +
                                         '<div class="input-group-append">' + 
                                         '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                       '</div>' +
                                     '</div>' + 
                                    '</div>'+
                                      '<div class="form-group col-md-3">' +
                                       '<div class="input-group">' +  
                                       '<input type="text" class="form-control" id="actualdate_' + pftsid + '" name="actualDate" value="' + formatDate(item[9]) + '" readonly>' +
                                           '<div class="input-group-append">' + 
                                           '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                         '</div>' +
                                       '</div>' + 
                                      '</div>' ;
                         	}else if(statusid==11){
                         		 rowHtml +=
                         			 '<div class="form-group col-md-3">' +
                                     '<div class="input-group">' +  
                                         '<input type="text" class="form-control ' + (item[14] != null ? '' : 'date-picker') + '" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(item[24]) + '" ' + (item[14] != null ? 'readonly' : '') + '>' +
                                         '<div class="input-group-append">' + 
                                         '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                       '</div>' +
                                     '</div>' + 
                                    '</div>'+
                                      '<div class="form-group col-md-3">' +
                                       '<div class="input-group">' +  
                                       '<input type="text" class="form-control" id="actualdate_' + pftsid + '" name="actualDate" value="' + formatDate(item[14]) + '" readonly>' +
                                           '<div class="input-group-append">' + 
                                           '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                         '</div>' +
                                       '</div>' + 
                                      '</div>' ;
                         	}else if(statusid==12){
                         		 rowHtml +=
                         			 '<div class="form-group col-md-3">' +
                                     '<div class="input-group">' +  
                                         '<input type="text" class="form-control ' + (item[13] != null ? '' : 'date-picker') + '" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(item[25]) + '" ' + (item[13] != null ? 'readonly' : '') + '>' +
                                         '<div class="input-group-append">' + 
                                         '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                       '</div>' +
                                     '</div>' + 
                                    '</div>'+
                                      '<div class="form-group col-md-3">' +
                                       '<div class="input-group">' +  
                                       '<input type="text" class="form-control" id="actualdate_' + pftsid + '" name="actualDate" value="' + formatDate(item[13]) + '" readonly>' +
                                           '<div class="input-group-append">' + 
                                           '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                         '</div>' +
                                       '</div>' + 
                                      '</div>' ;
                         	}else if(statusid==13){
                         		 rowHtml +=
                         			 '<div class="form-group col-md-3">' +
                                     '<div class="input-group">' +  
                                         '<input type="text" class="form-control ' + (item[10] != null ? '' : 'date-picker') + '" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(item[26]) + '" ' + (item[10] != null ? 'readonly' : '') + '>' +
                                         '<div class="input-group-append">' + 
                                         '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                       '</div>' +
                                     '</div>' + 
                                    '</div>'+
                                      '<div class="form-group col-md-3">' +
                                       '<div class="input-group">' +  
                                       '<input type="text" class="form-control" id="actualdate_' + pftsid + '" name="actualDate" value="' + formatDate(item[10]) + '" readonly>' +
                                           '<div class="input-group-append">' + 
                                           '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                         '</div>' +
                                       '</div>' + 
                                      '</div>' ;
                         	}
                        	  $('.actual-date-header').show();
                              $('.probable-date-header').removeClass('col-md-6').addClass('col-md-3');
                              $('button[name="action"][value="edit"]').hide();
                              $('button[name="action"][value="baseline"]').hide();
                              $('button[name="action"][value="revise"]').show();
                             }else {
                             	if(statusid==1){
                             		 rowHtml +=
                                          '<div class="form-group col-md-6">' +
                                           '<div class="input-group">' +  
                                               '<input type="text" class="form-control date-picker" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(item[2]) + '" placeholder="Select Date" readonly>' +
                                               '<div class="input-group-append">' + 
                                               '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                             '</div>' +
                                           '</div>' + 
                                          '</div>' ;
                             	}else if(statusid==2){
                             		 rowHtml +=
                                          '<div class="form-group col-md-6">' +
                                           '<div class="input-group">' +  
                                               '<input type="text" class="form-control date-picker" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(item[15]) + '" placeholder="Select Date">' +
                                               '<div class="input-group-append">' + 
                                               '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                             '</div>' +
                                           '</div>' + 
                                          '</div>' ;
                             	}else if(statusid==3){
                             		 rowHtml +=
                                          '<div class="form-group col-md-6">' +
                                           '<div class="input-group">' +  
                                               '<input type="text" class="form-control date-picker" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(item[16]) + '" placeholder="Select Date">' +
                                               '<div class="input-group-append">' + 
                                               '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                             '</div>' +
                                           '</div>' + 
                                          '</div>' ;
                             	}else if(statusid==4){
                             		 rowHtml +=
                                          '<div class="form-group col-md-6">' +
                                           '<div class="input-group">' +  
                                               '<input type="text" class="form-control date-picker" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(item[17]) + '" placeholder="Select Date">' +
                                               '<div class="input-group-append">' + 
                                               '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                             '</div>' +
                                           '</div>' + 
                                          '</div>' ;
                             	}else if(statusid==5){
                             		 rowHtml +=
                                          '<div class="form-group col-md-6">' +
                                           '<div class="input-group">' +  
                                               '<input type="text" class="form-control date-picker" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(item[18]) + '" placeholder="Select Date">' +
                                               '<div class="input-group-append">' + 
                                               '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                             '</div>' +
                                           '</div>' + 
                                          '</div>' ;
                             	}else if(statusid==6){
                             		 rowHtml +=
                                          '<div class="form-group col-md-6">' +
                                           '<div class="input-group">' +  
                                               '<input type="text" class="form-control date-picker" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(item[19]) + '" placeholder="Select Date">' +
                                               '<div class="input-group-append">' + 
                                               '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                             '</div>' +
                                           '</div>' + 
                                          '</div>' ;
                             	}else if(statusid==7){
                            		 rowHtml +=
                                         '<div class="form-group col-md-6">' +
                                          '<div class="input-group">' +  
                                              '<input type="text" class="form-control date-picker" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(item[20]) + '" placeholder="Select Date">' +
                                              '<div class="input-group-append">' + 
                                              '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                            '</div>' +
                                          '</div>' + 
                                         '</div>' ;
                            	}else if(statusid==8){
                            		 rowHtml +=
                                         '<div class="form-group col-md-6">' +
                                          '<div class="input-group">' +  
                                              '<input type="text" class="form-control date-picker" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(item[21]) + '" placeholder="Select Date">' +
                                              '<div class="input-group-append">' + 
                                              '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                            '</div>' +
                                          '</div>' + 
                                         '</div>' ;
                            	}else if(statusid==9){
                            		 rowHtml +=
                                         '<div class="form-group col-md-6">' +
                                          '<div class="input-group">' +  
                                              '<input type="text" class="form-control date-picker" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(item[22]) + '" placeholder="Select Date">' +
                                              '<div class="input-group-append">' + 
                                              '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                            '</div>' +
                                          '</div>' + 
                                         '</div>' ;
                            	}else if(statusid==10){
                            		 rowHtml +=
                                         '<div class="form-group col-md-6">' +
                                          '<div class="input-group">' +  
                                              '<input type="text" class="form-control date-picker" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(item[23]) + '" placeholder="Select Date">' +
                                              '<div class="input-group-append">' + 
                                              '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                            '</div>' +
                                          '</div>' + 
                                         '</div>' ;
                            	}else if(statusid==11){
                            		 rowHtml +=
                                         '<div class="form-group col-md-6">' +
                                          '<div class="input-group">' +  
                                              '<input type="text" class="form-control date-picker" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(item[24]) + '" placeholder="Select Date">' +
                                              '<div class="input-group-append">' + 
                                              '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                            '</div>' +
                                          '</div>' + 
                                         '</div>' ;
                            	}else if(statusid==12){
                            		 rowHtml +=
                                         '<div class="form-group col-md-6">' +
                                          '<div class="input-group">' +  
                                              '<input type="text" class="form-control date-picker" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(item[25]) + '" placeholder="Select Date">' +
                                              '<div class="input-group-append">' + 
                                              '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                            '</div>' +
                                          '</div>' + 
                                         '</div>' ;
                            	}else if(statusid==13){
                            		 rowHtml +=
                                         '<div class="form-group col-md-6">' +
                                          '<div class="input-group">' +  
                                              '<input type="text" class="form-control date-picker" id="probdate_' + pftsid + '" name="probableDate" value="' + formatDate(item[26]) + '" placeholder="Select Date">' +
                                              '<div class="input-group-append">' + 
                                              '<span class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></span>' +
                                            '</div>' +
                                          '</div>' + 
                                         '</div>' ;
                            	}
                             	
                                 $('.actual-date-header').hide();
                                 $('.probable-date-header').removeClass('col-md-3').addClass('col-md-6');
                             }

                             $('.mileContainer').append(rowHtml);
                     });

                     initializeDatePickers();  
                 } else {
                     $('button[name="action"][value="add"]').show();
                 }
             }
         }
         },
         error: function(xhr, status, error) {
             console.error('AJAX Error:', error);
             $('button[name="action"][value="add"]').show();
         }
     });
}

function initializeDatePickers() {
	  var selectedDates = []; 
	  $('.date-picker').each(function(index) {
        var $input = $(this);
        var preFilledDate = $input.val(); // Get the pre-filled value, if any

        if (preFilledDate) {
            var parsedDate = moment(preFilledDate, 'DD-MM-YYYY');
            selectedDates[index] = parsedDate; // Store the date in moment format
        }

        // Initialize only the active date pickers (non-readonly)
        if (!$input.prop('readonly')) {
            $input.daterangepicker({
                singleDatePicker: true,
                showDropdowns: true,
                autoUpdateInput: false,
                locale: { format: 'DD-MM-YYYY' },
                minDate: getMinDateForIndex(index) // Get the minDate based on previous dates
            }).on('apply.daterangepicker', function(ev, picker) {
                var selectedDate = picker.startDate.format('DD-MM-YYYY');
                $(this).val(selectedDate); // Set the selected date in the input field

                // Update the selectedDates array for the current index
                selectedDates[index] = picker.startDate;

                // Update the minDate for the next date picker, if any
                updateMinDateForNext(index);
            }).on('show.daterangepicker', function(ev, picker) {
                var pickerTop = $(this).offset().top;
                var modalTop = $('#milestoneModal').offset().top;
                var modalHeight = $('#milestoneModal').outerHeight();

                if ((pickerTop - modalTop) > (modalHeight / 2)) {
                    picker.drops = 'up';
                } else {
                    picker.drops = 'down';
                }
                picker.move();
            });
        }
    });
	  
	 function getMinDateForIndex(index) {
	    for (var i = index - 1; i >= 0; i--) {
	        if (selectedDates[i]) return selectedDates[i]; // Return the first valid previous date
	    }
	    return false; // No restrictions if no previous dates found
	}

	function updateMinDateForNext(currentIndex) {
	    for (var i = currentIndex + 1; i < $('.date-picker').length; i++) {
	        var nextInput = $('.date-picker').eq(i);

	        if (!nextInput.prop('readonly')) {
	            // If the next input is not read-only, update its minDate
	            nextInput.data('daterangepicker').minDate = selectedDates[currentIndex];
	            break; // Only update the immediate next active date picker
	        }
	    }
	}
}

function addSubmit(value){
	$('#actiontype').val(value);
    var isValid = false;
    
    if(value === 'add'){
    $('.probableDateField').each(function() {
        if ($(this).val() !== '') {
            isValid = true;  
            return false;  
        }
    });
    if (!isValid) {
        alert('Please fill at least one date field.');
        return false; 
      }
    }
    
  /*   if (value === 'revise') {
        $('#remarksModal').modal('show');
        $('#saveRemarksButton').off('click').on('click', function() {
            var remarks = $('#remarksInput').val().trim();

            if (remarks === '') {
                alert('Remarks are required for revision.');
                return false;
            }

            $('#remarksField').val(remarks);
            $('#remarksModal').modal('hide'); 

            if (confirm('Are you sure to submit?')) {
            	$('#milestoneModal').modal('hide');
                $('#milestoneForm').submit();
            }
        });
        
        return false; 
    } */
    
    if (confirm('Are you sure to submit?')) {
    	 $('#milestoneForm').submit();
    }

};

function formatDate(dateString) {
    if (!dateString) return ''; 
    const date = new Date(dateString);
    return date.toLocaleDateString('en-GB', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
    }).replace(/\//g, '-');
}


$('.date-picker1').each(function() {
    $(this).daterangepicker({
        "singleDatePicker": true,
        "linkedCalendars": false,
        "showCustomRangeLabel": true,
        "autoUpdateInput": false, 
        "cancelClass": "btn-default",
        "showDropdowns": true,
        "drops": "down", 
        "locale": {
            "format": 'DD-MM-YYYY'
        }
    });

    if ($(this).val() === "") {
        $(this).val('--'); 
    }

});

</script>
</body>
</html>