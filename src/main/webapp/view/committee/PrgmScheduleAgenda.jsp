<%@page import="java.util.stream.Collectors"%>
<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat,java.io.ByteArrayOutputStream,java.io.ObjectOutputStream"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/sweetalert2.min.css" var="sweetalertCss" />
<spring:url value="/resources/js/sweetalert2.min.js" var="sweetalertJs" />
<link href="${sweetalertCss}" rel="stylesheet" />
<script src="${sweetalertJs}"></script>
<spring:url value="/resources/css/committeeModule/PrgmScheduleAgenda.css" var="PrgmScheduleAgenda" />
<link href="${PrgmScheduleAgenda}" rel="stylesheet" />
<title>COMMITTEE AGENDA </title>
</head>
 
<body>
  <%
  SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
  SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
  List<Object[]> empLists = new ArrayList<>();
  Object[] scheduledata=(Object[])request.getAttribute("scheduledata");
  List<Object[]> prgmProjectList =  (List<Object[]>)request.getAttribute("prgmProjectList");
  List<Object[]> committeeAgendaList =  (List<Object[]>)request.getAttribute("committeeAgendaList");
  List<Object[]> agendaDocList = (List<Object[]>) request.getAttribute("agendaDocList");
  String filesize=  (String)request.getAttribute("filesize");
  String labcode = (String)request.getAttribute("labcode");
  List<Object[]> labEmpList = (List<Object[]>)request.getAttribute("labEmpList");
  List<Object[]> allLabList = (List<Object[]>)request.getAttribute("allLabList");
  Map<String, List<Object[]>> labEmpListMap = labEmpList.stream().collect(Collectors.groupingBy(e -> e[4].toString()));
  String scheduleid=scheduledata[6].toString();
  String projectid=scheduledata[9].toString();
  List<String> groupNames = Arrays.asList("Software","Hardware","QA");
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


	<div class="container-fluid">
		<div class="mb-20px"> 
    		<div class="card">
    	
	    		<form action="CommitteeScheduleView.htm" name="myfrm" id="myfrm" method="post">
	    			<div class="card-header cardHeaderBgColor">
	      				<h6 class="h6Style" align="left"><%=scheduledata[7]!=null?StringEscapeUtils.escapeHtml4(scheduledata[7].toString()): " - " %> <span> (Meeting Date and Time :      				
		      				 &nbsp;<%=scheduledata[2]!=null?sdf.format(sdf1.parse(StringEscapeUtils.escapeHtml4(scheduledata[2].toString()))):" - " %> - <%=scheduledata[3]!=null?StringEscapeUtils.escapeHtml4(scheduledata[3].toString()): " - " %>) </span>      				
							<input type="hidden" name="projectid" value="<%=projectid%>"/>
		      				<input type="submit" class="btn  btn-sm view float-right" value="VIEW"/>
		      				<span  class="meetingIdStyle"> (Meeting Id : <%=scheduledata[11] %>) </span> 
		      				<input type="hidden" name="scheduleid" value="<%=scheduledata[6] %>">
		      				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"  />      				
	      				 </h6>
      				</div>
	      		</form>		
      		
				<%
				int  count=1;
				if(committeeAgendaList.size()>0){ %> 
					<div class="row">
						<div class="col-md-12">
	    					<div class="card">
	      						<div class="card-body" >
	      							<div class="row">
			   							<div class="col-md-12 pl-0px">
			   								<div class="table-responsive">
			    								<table class="table table-bordered table-hover  table-condensed mt-20px" id="mainTableEdit">
													<thead>
														<tr>
															<th colspan="10" class="thHeaderStyle">Agenda Details</th>									
														</tr>	
														<tr>			
															<th>Priority</th>		
															<th>Agenda Item</th>
															<th>Remarks</th>
															<th>Group Name</th>
															<th>Lab</th>	
														 	<th>Presenter</th>
														 	<th>Duration </th>
														 	<th>Attachment</th>
														 	<th>Upload</th>
															<th>Action</th>
														</tr>
													</thead>
													<tbody>					
								 						<%
														for(Object[] obj: committeeAgendaList){ %>
															<tr>
																<td>
																	<input type="number" class="form-control lineHeightStyle" name="priority" value="<%=obj[8]%>" onkeypress="return isNumber(event)" min="1" max="<%=committeeAgendaList.size()%>" form="priority_form">
																	<input type="hidden" name="agendaid"  value="<%=obj[0]%>" form="priority_form">
																</td>
																<td>
																	<input form="inlineeditform_<%=count%>" type="text" class="form-control" name="agendaitem" value="<%=obj[3].toString()%>"  maxlength="255" >	 
																</td>
																<td> 
																	<input form="inlineeditform_<%=count%>" type="text" class="form-control" name="remarks" value="<%=obj[6].toString()%>"  maxlength="255" >
																</td>
																<td> 
																<select form="inlineeditform_<%=count%>" class="form-control items PresLabCode width-200px" name="groupname" id="groupName_<%=count%>"  data-live-search="true" data-container="body">
																		<option selected value="">None</option>
																		<% for(String gn : groupNames){ %>
																			<option value="<%=gn%>" <%if(obj[15]!=null && obj[15].toString().equalsIgnoreCase(gn)){ %>selected="selected" <%} %> ><%=StringEscapeUtils.escapeHtml4(gn) %></option>
																		<%} %>
																	</select> 
																	<%-- <input form="inlineeditform_<%=count%>" type="text" class="form-control" name="groupname" value="<%=obj[15]!=null?obj[15].toString():""%>"  maxlength="255" placeholder="Enter Group Name">--%> 
																</td>
																<td>
												         		 	<select form="inlineeditform_<%=count%>" class="form-control items PresLabCode width-200px" name="PresLabCode" id="PresLabCode_<%=count %>" required="required" onchange="AgendaPresentors('<%=count %>')"  data-live-search="true" data-container="body">
																		<option disabled="disabled"  selected value="">Lab Name</option>
																	    <% for (Object[] lab : allLabList) {%>
																		    <option value="<%=lab[3]%>" <%if(obj[14].toString().equalsIgnoreCase(lab[3].toString())){ %>selected <%} %>  ><%=lab[3]!=null?StringEscapeUtils.escapeHtml4(lab[3].toString()): " - "%></option>
																	    <%} %>
																	    <!-- <option value="@EXP">Expert</option> -->
																	</select>
												         		</td>
																<td>
																	<select form="inlineeditform_<%=count%>" class="form-control items presenterid presenterIdStyle" name="presenterid" id="presenterid_<%=count %>"  required="required" data-live-search="true" data-container="body" onchange="getEmployee(this)">
														        		<option disabled="disabled" selected value="">Choose...</option>
																        <% for(Object[] emp : labEmpList){ %>
																        	<option value="<%=emp[0] %>" <%if(obj[9].toString().equalsIgnoreCase(emp[0].toString())) {%>selected<%} %> ><%=emp[1]!=null?StringEscapeUtils.escapeHtml4(emp[1].toString()): " - " %>(<%=emp[3]!=null?StringEscapeUtils.escapeHtml4(emp[3].toString()): " - " %>)</option>
																        <%} %>
																	</select>
																	<%-- <span><%=obj[10] %>, <%=obj[11] %></span> --%>
																</td>	
																<td>
																	<input form="inlineeditform_<%=count%>" type="number" name="duration" class="form-control item_name" min="1"  placeholder="Minutes " value="<%=obj[12] %>" required />
																</td>
																<td>
																	<table>
																	<%for(Object[] doc : agendaDocList) { 
																		if(obj[0].toString().equalsIgnoreCase(doc[1].toString())){%>
																		<tr>
																			<td><%= doc[3]!=null?StringEscapeUtils.escapeHtml4(doc[3].toString()): " - " %> <%= " <span class='text-muted'> Ver " %> <%= doc[4]!=null?StringEscapeUtils.escapeHtml4(doc[4].toString()): " - " %> . <%= doc[5]!=null?StringEscapeUtils.escapeHtml4(doc[5].toString()): " - " %> <%= "</span>" %></td>
																			<td class="tdStyle"><a href="AgendaDocLinkDownload.htm?filerepid=<%=doc[2]%>" target="blank"><i class="fa fa-download text-success" aria-hidden="true"></i></a></td>
																			<td class="tdStyle"><a type="button" onclick="removeDocRow(this,<%=doc[0] %>);" > <i class=" fa fa-minus text-danger"></i> </a></td>
																		<tr>													
																	<%} }%>
																	</table>
														
																</td>
											
																<td class="center"> 
																	<span id="editattachname_<%=obj[0] %>" class="attachname"></span>
																	<button form="inlineeditform_<%=count%>" type="button" class=" btn btn-sm" name="add" id="attacheditbtn_<%=obj[0] %>" onclick="openMainModal('<%=obj[0] %>','<%=obj[1] %>','<%=obj[3] %>','<%=obj[5] %>','0','edit')" > <i class="btn btn-sm fa fa-plus text-success paddingStyle"></i></button>
																	<input form="inlineeditform_<%=count%>" type="hidden" name="attachid" id="editattachid_<%=obj[0] %>" value="">												
																</td>																	
																<td>
																	<form name="myForm1" id="inlineeditform_<%=count%>" action="CommitteeScheduleAgendaEdit.htm" method="POST"  >
										                                <input type="hidden" name="${_csrf.parameterName}"   value="${_csrf.token}" />
										                                <input type="hidden" name="projectid" value="<%=obj[5]%>">
																		<%-- <input type="hidden" name="PresLabCode" value="<%=labcode%>"> --%>
																		<%-- <input type="hidden" name="presenterid" value="<%=obj[9]%>"> --%>
																		<input type="hidden" name="prgmflag" value="Y">
																		<input type="hidden"  name="AgendaPriority" value="<%=obj[8]%>"/>
																		<input type="hidden" name="committeescheduleagendaid" id="committeescheduleagendaid" value="<%=obj[0]%>"/>
																		<input type="hidden" name="scheduleid" value="<%=scheduleid%>"/>
																		<input type="hidden" name="schedulesub" value="<%=obj[2]%>"/>
																		<button type="submit" class="btn  btn-sm" name="action" value="edit" onclick="return confirm('Are you sure To Edit this Agenda?')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></button>
																		<button type="submit" class="btn  btn-sm" name="action" value="delete" onclick="return confirm('Are you sure To Delete this Agenda?')" formaction="CommitteeAgendaDelete.htm"> <i class="fa fa-trash" aria-hidden="true" ></i></button>
																	</form> 
																</td>
											 
															</tr>
																						
														<% count++; } %>
														<%if( committeeAgendaList.size()!=1 ){ %>
															<tr>
																<td class="center"><input type="submit" value="UPDATE" class="btn btn-sm edit" onclick="return confirm('Are You Sure to Update the Priorities ?');" form="priority_form"></td>
																<td colspan="8"></td>
															</tr>
														<%} %>
													</tbody>
												</table>
											</div> 
										</div>
	 									<!-- ----------------------------- -->		 	
	  								</div>
								</div>
							</div>
						</div>
					</div>
	
				 	<form method="get" action="CommitteeAgendaPriorityUpdate.htm" id="priority_form">
						<input type="hidden" name="scheduleid" value="<%=committeeAgendaList.get(0)[1]%>"/>
						<input type="hidden" name="prgmflag" value="Y">
					</form>
				<%} %>  
				
				<%
				List<String> agendaAddedProjectIds = committeeAgendaList!=null? committeeAgendaList.stream().map(e -> e[5].toString()).collect(Collectors.toList()): new ArrayList<>();
				prgmProjectList = prgmProjectList.stream().filter(e -> !agendaAddedProjectIds.contains(e[0].toString())).collect(Collectors.toList());
				
				 %>
					<div class="card-body">
		      			<form action="CommitteeAgendaSubmit.htm" method="post">
		        			<div>
		          				<div class="float-right"><span class="fs-15px text-primary">Duration in Minutes</span></div>
		          				<%if(prgmProjectList!=null && prgmProjectList.size()>0) { %>
		          				<table class="table  table-bordered table-hover table-striped table-condensed  info shadow-nohover w-100 mt-30px" id="mainTable">
									<thead>  
										<tr>
											<th width="20%">Agenda Item</th>
											<th width="10%">Remarks</th>
											<th width="10%">Group Name</th>
											<th width="10%">Lab</th>
											<th width="20%">Presenter</th>
											<th width="5%">Duration </th>
											<th width="20%">Attach File </th> 
											<th width="5%">Action</th>
										</tr>
									</thead>
									<tbody>
										<%
											
											for(Object[] obj : prgmProjectList) {
												
												List<Object[]> empList = labEmpListMap.get(obj[7].toString());
												empLists=empList; %>
											<tr>
												<td>
													<input type="text" class="form-control" name="agendaitem" value="<%=obj[3] %> (<%=obj[1] %>)" maxlength="500" required="required" />
													<input type="hidden" name="projectid" value="<%=obj[0]%>">
													<%-- <input type="hidden" name="PresLabCode" value="<%=labcode%>"> --%>
													<%-- <input type="hidden" name="presenterid" value="<%=obj[4]%>"> --%>
												</td>
												<td>
													<input type="text" class="form-control" name="remarks" value="NIL" maxlength="255" required="required" />
												</td>
												<td>
												 <select class="form-control items PresLabCode width-200px" name="groupname" id="groupName_<%=count%>" required="required" data-live-search="true" data-container="body" >
														<% for(String gn : groupNames){ %>
															<option value="<%=gn%>" ><%=StringEscapeUtils.escapeHtml4(gn) %></option>
														<%} %>	
														<option selected value="">None</option>
													</select> 
												<%-- <input type="text" class="form-control" name="groupname" value="" maxlength="255" placeholder="Enter Group Name" /> --%> 												
												</td>	
												<td>
								         		 	<select class="form-control items PresLabCode width-200px" name="PresLabCode" id="PresLabCode_<%=count %>" required="required" onchange="AgendaPresentors('<%=count %>')"  data-live-search="true" data-container="body">
														<option disabled="disabled"  selected value="">Lab Name</option>
													    <% for (Object[] lab : allLabList) {%>
														    <option value="<%=lab[3]%>" <%if(obj[7].toString().equalsIgnoreCase(lab[3].toString())){ %>selected <%} %>  ><%=lab[3]!=null?StringEscapeUtils.escapeHtml4(lab[3].toString()): " - "%></option>
													    <%} %>
													    <!-- <option value="@EXP">Expert</option> -->
													</select>
								         		</td>
												<td>
													<select class="form-control items presenterid presenterIdFontStyle" name="presenterid" id="presenterid_<%=count %>"  required="required" data-live-search="true" data-container="body" onchange="getEmployee(this)">
										        		<option disabled="disabled" selected value="">Choose...</option>
												        <% for(Object[] emp : empList){ %>
												        	<option value="<%=emp[0] %>" <%if(obj[4].toString().equalsIgnoreCase(emp[0].toString())) {%>selected<%} %> ><%=emp[1]!=null?StringEscapeUtils.escapeHtml4(emp[1].toString()): " - " %>(<%=emp[3]!=null?StringEscapeUtils.escapeHtml4(emp[3].toString()): " - " %>)</option>
												        <%} %>
													</select>
													<%-- <span><%=obj[5] %>, <%=obj[6] %></span> --%>
												</td>
												<td>
													<input type="number" name="duration" class="form-control" min="1" value="20" placeholder="Minutes" required/>
												</td>
												<td class="text-left width-15per">
													<button type="button" class=" btn btn-sm btnfileattachment" name="add" onclick="openMainModal('0','0',' ','<%=projectid %>','0','add')" > <i class="btn btn-sm fa fa-plus text-success paddingStyle"></i></button> 
													<br>
													<table class="attachlist" id="attachlistdiv_0">
														
													</table>										
												</td>
												<td class="center">
													<button type="button" class="btn btn-success btn-clone"><i class="fa fa-plus" ></i></button>
													<button type="button" class="btn btn-danger btn-remove"> <i class="fa fa-minus"></i> </button>
												</td>
											</tr>
										<% count++; } %>	
									</tbody>	
								</table>
								<%} %>
								<table class="table  table-bordered table-hover table-striped table-condensed  info shadow-nohover w-100 mt-30px" id="subHeadTable">
								<thead>
									<tr>
										<th width="20%">Agenda Item</th>
										<th width="10%">Remarks</th>
										<th width="10%">Group Name</th>
										<th width="10%">Lab</th>
										<th width="20%">Presenter</th>
										<th width="5%">Duration </th>
										<th width="20%">Attach File </th> 
										<th class="text-center" width="5%"><button type="button" class="btn btn-success btn_clone_new"><i class="fa fa-plus" ></i></button></th>
									</tr>
								</thead>
								<tbody>
									
											<tr class="tr_clone_DH">
												<td>
													<input type="text" class="form-control" name="agendaitem" value="" maxlength="500" required="required" placeholder="Enter Agenda Item" />
													<input type="hidden" name="projectid" value="-1">
												</td>
												<td>
													<input type="text" class="form-control" name="remarks" value="" maxlength="255" required="required" placeholder="Enter Remarks" />
												</td>
												<td>
												 <select class="form-control items PresLabCode width-200px" name="groupname" id="groupName_DH_0" required="required" data-live-search="true" data-container="body" >
														<% for(String gn : groupNames){ %>
															<option value="<%=gn%>" ><%=StringEscapeUtils.escapeHtml4(gn) %></option>
														<%} %>
														<option selected value="">None</option>
													</select> 
													<%--	<input type="text" class="form-control" name="groupname" value="" maxlength="255" placeholder="Enter Group Name" />  --%>
												</td>	
												<td>
								         		 	<select class="form-control items PresLabCode width-200px" name="PresLabCode" id="PresLabCode_DH_0" required="required" onchange="AgendaPresentorsForDH('0')"  data-live-search="true" data-container="body">
														<option disabled="disabled"  selected value="">Lab Name</option>
													    <% for (Object[] lab : allLabList) {%>
														    <option value="<%=lab[3]%>" ><%=lab[3]!=null?StringEscapeUtils.escapeHtml4(lab[3].toString()): " - "%></option>
													    <%} %>
													    <!-- <option value="@EXP">Expert</option> -->
													</select>
								         		</td>
												<td>
													<select class="form-control items presenterid presenterIdFontStyle" name="presenterid" id="presenterid_DH_0"  required="required" data-live-search="true" data-container="body" onchange="getEmployeeForDH(this)">
										        		<option disabled="disabled" selected value="">Choose...</option>
												        <% for(Object[] emp : empLists){ %>
												        	<option value="<%=emp[0] %>" ><%=emp[1]!=null?StringEscapeUtils.escapeHtml4(emp[1].toString()): " - " %>(<%=emp[3]!=null?StringEscapeUtils.escapeHtml4(emp[3].toString()): " - " %>)</option>
												        <%} %>
													</select>
												</td>
												<td>
													<input type="number" name="duration" class="form-control" min="1" value="" placeholder="Minutes" required/>
												</td>
												<td class="text-left width-15per">
													<button type="button" class=" btn btn-sm btnfileattachment" name="add" onclick="openMainModal('0','0',' ','<%=projectid %>','0','add')" > <i class="btn btn-sm fa fa-plus text-success paddingStyle"></i></button> 
													<br>
													<table class="attachlist" id="attachlistdiv_0">
														
													</table>										
												</td>
												<td class="center">
													<button type="button" class="btn btn-danger btn-remove-clone-new"> <i class="fa fa-minus"></i> </button>
												</td>
											</tr>
								</tbody>
								</table>
			          			<div align="center">
						            <input type="submit"  class="btn  btn-sm submit" value="SUBMIT" onclick="return allfilessizecheck('addagendafrm'); "/> <!-- return confirm('Are You Sure To Add This Agenda(s) ?') -->
			          			</div>
		        			</div>
		        		
				        	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"  />
				        	<input type="hidden" name="scheduleid" value="<%=scheduledata[6] %>">
				     		<input type="hidden" name="schedulesub" value="<%=scheduledata[5]%>"/>
			    			<input type="hidden" name="prgmflag" value="Y">
		      			</form>
		    		</div>
		    		
				<!-- Draft Table -->
				<table class="table table-bordered draftTableStyle" id="draftTable">
					<thead>
				    	<tr>
				      		<th>Agenda Item</th>
					      	<th>Remarks</th>
							<th>Group Name</th>
					      	<th>Lab</th>
					      	<th>Presenter</th>
					      	<th>Duration</th>
					      	<th>Attach File</th>
					      	<th>Add Back</th>
				    	</tr>
				  	</thead>
				  	<tbody>
				  	</tbody>
				</table>
			</div>
		</div>
	</div> 

	<form method="POST" action="FileUnpack.htm"  id="downloadform" target="_blank"> 
		<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
		<input type="hidden" name="FileUploadId" id="FileUploadId" value="" />
		<input type="hidden" name="prgmflag" value="Y">
	</form>


	<div class="modal fade" id="meetingModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
  		<div class="modal-dialog" role="document">
    		<div class="modal-content modalContentWidth">
      			<div class="modal-header">
        			<h6 class="modal-title" id="exampleModalLabel"> Presenter is already Having meeting on  That Particular Day</h6>
			        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
			        	<span aria-hidden="true">&times;</span>
			        </button>
				</div>
      			<div class="modal-body">
        			<table class="table table-bordered table-stripped" id="myTable">
			        	<thead>
			        		<tr>
			        		<th>SN</th>
			        		<th>Meeting Id </th>
			        		<th>Meeting Venue</th>
			        		<th>Meeting Time</th>
			        		<th>Meeting Role</th>
			        		</tr>
			        	</thead>
			        	<tbody id="meetingbody">
			        	
			        	</tbody>
       				</table>
      			</div>

				<div class="text-danger p-2">
		      		Please make sure his/her meeting times are not clashing!
				</div>
			</div>
		</div>
	</div>


	<!-- File Repo Modal -->
	<div class="modal fade" id="pdfModal" tabindex="-1" role="dialog" aria-labelledby="pdfModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg" role="document">
	    	<div class="modal-content shadow-lg border-0">
	      		<div class="modal-header bg-primary text-white">
			        <h5 class="modal-title" id="modalTitleId"><i class="fa fa-folder-open"></i></h5>
			        <button type="button" class="close text-white" data-dismiss="modal" aria-label="Close">
			          <span aria-hidden="true">&times;</span>
			        </button>
	      		</div>
	      		<div class="modal-body">
			        <!-- Folder Tree List -->
			   		<ul class="list-group folder-tree" id="folderTree"></ul>
				</div>
				<div class="modal-footer">
					<div class="text-danger fontWeight-500">Note - Please upload PDF files only and PDF size should be smaller than 10mb.</div>
				</div>
			</div>
		</div>
	</div>

<script type="text/javascript">

$('.items').select2();

function removeDocRow(elem,attachid)
{
	if(confirm('Are You Sure To unlink this Document ?')){
	
		$.ajax({
			type : "GET",
			url : "AgendaUnlinkDoc.htm",
			data : {
				AttachDocid : attachid
			},
			datatype: 'json',
			success : function(result)
				{
					var result= JSON.parse(result);
					if(result==1){
						alert('Document Removed Successfully');
						$(elem).parent('td').parent('tr').remove();
					}else
					{
						alert('Document Removal Unsuccessful \n Internal Error occured !!');
					}
				}
		});
	}
}
</script>


<script type='text/javascript'> 
function submitForm(frmid)
{ 
  document.getElementById(frmid).submit(); 
} 

function submitForm1(msg,frmid)
{ 
	myconfirm(msg,frmid);
	event.preventDefault(); 
}
</script>

<script type="text/javascript">
  function isNumber(evt) {
	    evt = (evt) ? evt : window.event;
	    var charCode = (evt.which) ? evt.which : evt.keyCode;
	    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
	        return false;
	    }
	    return true;
	}
  </script>

<input type="hidden" id="addoredit" value="" />
<input type="hidden" id="agendano" value="" />
<!--  -----------------------------------------------agenda attachment js ---------------------------------------------- -->

<script type="text/javascript">

function setagendaattachval(attachid, attchName)
{
	$addedit=$('#addoredit').val();
	$agendaelem=$('#agendano').val();
	if($addedit==='add'  )
	{
		let agendano=$('#agendano').val();
		let html= $("#attachlistdiv_"+agendano).html();
		let attname =attchName;
		/* if(attchName.length>5){
			attname = attchName.substring(0, 5).concat("...");
		}else{
			attname=attchName;
		} */
		html += '<tr id="a_'+agendano+'"><td title='+attchName+'> '+attname+'</td><td class="agendaTdStyle">';
		html += '<button type="button"  onclick="$(this).parent(\'td\').parent(\'tr\').remove();"  > <i class="btn btn-sm fa fa-minus text-danger"></i> </button>';  /* onclick="$(\'#a_'+agendano+'\').remove();" */
		html += '<input type="hidden" name="attachid_'+agendano+'" value="'+attachid+'" /></td>';
		html += '</tr>';
		$("#attachlistdiv_"+agendano).html(html);
	}
	
	$('#pdfModal').modal('hide');
/* 	$('#attachmentmodal').modal('hide'); */
}
 

function editattachremove(agendaid)
{
	$('#editattachid_'+$agendaelem).val(0);
	$('#editattachname_'+$agendaelem).html('');
	$('#attacheditbtn_'+$agendaelem).show();
}
</script>
<script type="text/javascript">
function FileDownload(fileid1)
{
	$('#FileUploadId').val(fileid1);
	$('#downloadform').submit();
}

</script>

<!--  -----------------------------------------------agenda attachment js ---------------------------------------------- -->
<script type="text/javascript">
function editcheck(editfileid)
{
	const fi = $('#'+editfileid )[0].files[0].size;							 	
    const file = Math.round((fi / 1024/1024));
    if (document.getElementById(editfileid).files.length !=0 && file >= <%=filesize%> ) 
    {
    	event.preventDefault();
     	alert("File too Big, please select a file less than <%=filesize%> mb");
    } else
    {
    	 return confirm('Are You Sure To Edit This Agenda?');
    }
}
</script>
<script type="text/javascript">
var filexcount=0;
	function allfilessizecheck(frmid)
	{	
		filexcount=0;
		var fileidarr=[];
            if (this.id) {            	
            	fileidarr.push(this.id);
            }
        });
		for(var z=0; z<fileidarr.length;z++)
		{
			if(document.getElementById(fileidarr[z]).files.length !=0 ){
				const fi = $('#'+fileidarr[z] )[0].files[0].size;							 	
		        const file = Math.round((fi / 1024/1024));
		       
		        if (file >= <%=filesize%>) 
		        {
		        	filexcount++;
		        }	        
			}
		}
		if(filexcount>0)
		{
			event.preventDefault();
			alert('File too Big, please select a file less than <%=filesize%> mb');
		}else
		{
			return confirm('Are You Sure to Submit These Agenda (s)?')
		}
	}					
</script>
	
<script type="text/javascript">
function AgendaPresentors($AddrowId){
	$('#presenterid_'+$AddrowId).val("");
	var $PresLabCode = $('#PresLabCode_'+$AddrowId).val();
	if($PresLabCode !=""){
		$.ajax({		
		type : "GET",
		url : "CommitteeAgendaPresenterList.htm",
		data : {
			PresLabCode : $PresLabCode,
		},
		datatype : 'json',
		success : function(result) {
	
			var result = JSON.parse(result);	
			var values = Object.keys(result).map(function(e) {
									 return result[e]
						});
							
			var s = '<option value="" selected disabled>Choose...</option>';
			for (i = 0; i < values.length; i++) {									
				s += '<option value="'+values[i][0]+'">'
					+values[i][1].replaceAll("<","").replaceAll(">","") + " (" +values[i][3].replaceAll("<","").replaceAll(">","")+")" 
					+ '</option>';
			} 
			$('#presenterid_'+$AddrowId).html(s);
		}
		});
	}
}

function AgendaPresentorsForDH($AddrowId){
	$('#presenterid_DH_'+$AddrowId).trigger('change');
	var $PresLabCode = $('#PresLabCode_DH_'+$AddrowId).val();
	if($PresLabCode !=""){
		$.ajax({		
		type : "GET",
		url : "CommitteeAgendaPresenterList.htm",
		data : {
			PresLabCode : $PresLabCode,
		},
		datatype : 'json',
		success : function(result) {

			var result = JSON.parse(result);	
			var values = Object.keys(result).map(function(e) {
									 return result[e]
						});
							
			var s = '<option value="" selected disabled>Choose...</option>';
			for (i = 0; i < values.length; i++) {	
				s += '<option value="'+values[i][0]+'">'
					+values[i][1].replaceAll("<","").replaceAll(">","") + " (" +values[i][3].replaceAll("<","").replaceAll(">","")+")" 
					+ '</option>';
			} 
			$('#presenterid_DH_'+$AddrowId).html(s);
		}
		});
	}
}


var mainAgendaId="";
var attachRepIds = [];
var agendadocId=[];
function openMainModal(agendaid,scheduleid,agendaname,projectid,cloneId,agendatype){
	mainAgendaId="";
	$('#addoredit').val(agendatype);
	$('#agendano').val(cloneId);
	mainAgendaId=agendaid;
	if(agendaid!=0){
		 $.ajax({
             url: 'getAgendaAttachId.htm',
             type: 'GET',
             data: {agendaid: agendaid},
             success: function(response) {
            	 try {
                     var parsedResponse = JSON.parse(response);
                     if (Array.isArray(parsedResponse)) {
                       parsedResponse.forEach(item => {
                         attachRepIds.push(item[1]);
                         agendadocId.push(item[0]);
                       });
                     } else {
                       console.error('Response is not an array:', parsedResponse);
                     }
                   } catch (e) {
                     console.error('Error parsing JSON response:', e);
                   }
             },
          }); 
	}
	
    $.ajax({
        type: "GET",
        url: "FileRepMasterListAllAjax.htm",
        data: { projectid: projectid },
        success: function(result) {
            var data = JSON.parse(result);
            var folderMap = {};
            var html = '';
            if (data.length > 0) {
                projectName = data[0][4];
           }

            // First pass: build main folders
            for (var i = 0; i < data.length; i++) {
                var mainId = data[i][0];
                var parentId = data[i][1];
                var name = data[i][3];

                if (parentId === 0) { // Main Folder
                    folderMap[mainId] = { name: name, subfolders: [] };
                }
            }

            // Second pass: attach subfolders
            for (var j = 0; j < data.length; j++) {
                var subId = data[j][0];
                var subParentId = data[j][1];
                var subName = data[j][3];

                if (subParentId !== 0 && folderMap[subParentId]) { // Subfolder
                    folderMap[subParentId].subfolders.push({ id: subId, name: subName });
                }
            }

            // Generate HTML
            if (folderMap && Object.keys(folderMap).length > 0) {
                for (var mainId in folderMap) {
                    if (folderMap.hasOwnProperty(mainId)) {
                        html += '<li class="list-group-item folder-item" data-id="' + mainId + '" onclick="toggleFolder(this, ' + mainId + ', '+ projectid +', \'mainLevel\', \'' + agendatype + '\', '+ agendaid +')">';
                        html += '<i class="fa fa-folder folder-icon text-warning"></i> ' + folderMap[mainId].name;
                        html += '<ul class="list-group subfolder subFolderStyle">';

                        var subfolders = folderMap[mainId].subfolders;
                        for (var k = 0; k < subfolders.length; k++) {
                            var sub = subfolders[k];
                            html += '<li class="list-group-item folder-item" data-id="' + sub.id + '" onclick="toggleFolder(this, ' + sub.id + ', '+ projectid +', \'subLevel\', \'' + agendatype + '\', '+ agendaid +')">';
                            html += '<i class="fa fa-folder folder-icon text-warning"></i> ' + sub.name;
                            html += '<ul class="list-group subfolder" id="subfolder-files-' + sub.id + ' subFolderStyle"></ul>';
                            html += '</li>';
                        }

                        html += '<div class="" id="mainfolder-files-' + mainId + ' subFolderStyle"></div>';
                        html += '</ul></li>';
                    }
                }
            }else {
                html += '<div>No Data Available.</div></br>';
                html += '<div>Please go to <span class="text-primary fontWeight-500">Document Repository Module &rarr; Document Rep Master</span>, create a folder, and upload pdfs.</div></br>';
            }

            $('.folder-tree').html(html);
            $('#pdfModal').modal('show');
            if (projectName !== undefined && projectName.trim() !== '' && agendatype !== "add") {
                $('#modalTitleId').text('PDF Files Explorer for ' + agendaname);
            }else{
            	$('#modalTitleId').text('PDF Files Explorer');
            }
        }
    });
}

function toggleFolder(element, folderId, projecId, type, agendatype, agendaid) {
	
    if ($(event.target).closest('.file-item').length > 0 || $(event.target).hasClass('pdf-check')) {
        return;
    }
    event.stopPropagation(); // Prevent parent toggling

    var $elem = $(element);
    var $icon = $elem.children('.folder-icon');
    var $subfolder = $elem.children('ul.subfolder');

    if ($subfolder.is(':visible')) {
        $subfolder.slideUp(200);
        $elem.removeClass('open');
        $icon.removeClass('fa-folder-open').addClass('fa-folder');
    } else {
        $subfolder.slideDown(200);
        $elem.addClass('open');
        $icon.removeClass('fa-folder').addClass('fa-folder-open');

        // Load files if not loaded yet
        var fileContainerId = '';
        if (type === 'mainLevel') {
            fileContainerId = '#mainfolder-files-' + folderId;
        } else {
            fileContainerId = '#subfolder-files-' + folderId;
        }

        if ($(fileContainerId).is(':empty')) {
            loadFolderFiles(folderId, projecId, type, agendatype, agendaid);
        }
    }
}

function loadFolderFiles(folderId, projecId, type, agendatype, agendaid) {
    $.ajax({
        type: "GET",
        url: "getOldFileDocNames.htm",
        data : {
   			projectId : projecId,
   			fileId : folderId,
   			fileType : type,
	    },
        success: function(result) {
            var data = JSON.parse(result);
            var html = '';

            for (var i = 0; i < data.length; i++) {
                var fileName = data[i][6];
                html += '<li class="list-group-item file-item">';
                html += '<input type="checkbox" class="pdf-check mr-2" id="checkId'+data[i][0]+'" value="' + data[i][7] + '" data-filename="'+data[i][6]+'" ';
                if(data[i][7]!= 0 && attachRepIds.length>0 && attachRepIds.includes(data[i][7]) && agendatype !== "add") {
                    html += ' checked disabled';
                }
                html += '/>';
                html += '<i class="fa fa-file-pdf-o text-danger"></i> ' + fileName;
                html += '<span class="text-muted fs-13px"> Ver '+data[i][4]+'.'+data[i][5]+'</span>';
                html += '<i class="fa fa-download faDownloadStyle" onclick="fileDownload(' + data[i][7] + ', \'' + type + '\')"></i>';
                html += '<i class="fa fa-upload faUploadStyle" aria-hidden="true" onclick="fileUpload(\''+data[i][0]+'\')"></i></button><br/>';
                html += '<label for="fileInput" class="uploadfileLabelStyle" id="uploadlabel'+data[i][0]+'">'
                html += '<input type="file" name="docFileInput" id="fileInput'+data[i][0]+'" required="required"  accept="application/pdf"/> '
                html += '<button type="button" class="btn btn-sm back" onclick="fileSubmit(\''+type+'\',\''+data[i][0]+'\',\''+data[i][2]+'\',\''+data[i][3]+'\',\''+data[i][4]+'\',\''+data[i][5]+'\',\''+data[i][6]+'\', '+agendaid+')">Upload</button>'
                html += '</label>'
                html += '</li>';
            }

            if (type === 'mainLevel') {
                $('#mainfolder-files-' + folderId).html(html).show();
            } else {
                $('#subfolder-files-' + folderId).html(html).show();
            }
        }
    });
}

function fileDownload(fileId, fileType) {
    $.ajax({
        url: 'fileDownload.htm/' + fileId + '?fileType=' + encodeURIComponent(fileType),
        type: 'GET',
        xhrFields: {
            responseType: 'blob'
        },
        success: function (data, status, xhr) {
        	  const blob = new Blob([data], { type: 'application/pdf' });
              const blobUrl = URL.createObjectURL(blob);
              window.open(blobUrl, '_blank');
              setTimeout(() => URL.revokeObjectURL(blobUrl), 5000);
        },
        error: function (xhr, status, error) {
		     Swal.fire({
			        icon: 'error',
			        title: 'Error',
			        text: 'Failed to download/open file.',
			 });
        }
    });
}

// Allow only one checkbox to be selected at a time
function singleSelect(checkbox) {
    $('.pdf-check').not(checkbox).not(':disabled').prop('checked', false);
}

// Event delegation for dynamically added checkboxes
$(document).on('change', '.pdf-check', function() {
  // Send AJAX request with the selected checkbox value
  var selectedValue = $(this).val();
  var filename = $(this).data('filename');
  var isChecked = $(this).prop('checked');
  var agendtype=$('#addoredit').val();
  
  if(isChecked){
	  if (agendtype==='add') {
      	setagendaattachval(selectedValue,filename);
      }else{
 	     Swal.fire({
	            title: 'Are you sure to linking?',
	            icon: 'question',
	            showCancelButton: true,
	            confirmButtonColor: 'green',
	            cancelButtonColor: '#d33',
	            confirmButtonText: 'Yes'
	        }).then((result) => {
	            if (result.isConfirmed) {
	          	  $.ajax({
	        	        url: 'addAgendaLinkFile.htm', 
	        	        type: 'GET',
	        	        data: { attachId: selectedValue,agendaId: mainAgendaId},
	        	        success: function(response) {
	        	        	 Swal.fire({
	        	    	            icon: 'success',
	        	    	            title: 'Success',
	        	    	            text: 'Document linked successfully!',
	        	    	            allowOutsideClick :false
	        	    	          });
	        	        	 $('#pdfModal').hide();
	        	        	$('.swal2-confirm').click(function() {
	        	                location.reload();
	        	            });
	        	        },
	        	        error: function() {
	        	          Swal.fire({
	        	            icon: 'error',
	        	            title: 'Error',
	        	            text: 'An error occurred while submitting the checkbox selection.',
	        	          });
	        	        }
	        	    });
	          }else{
	        	  $('.pdf-check').not(':disabled').prop('checked', false);
	          }
	     });
      }
    }
});

function fileUpload(Id){
	 var label = document.getElementById("uploadlabel"+Id);
   if (label.style.display === "none") {
       label.style.display = "inline-block";
   } else {
       label.style.display = "none";
   }
}

function fileSubmit(type,repid,mainId,subId,version,release,docName,agendaid) {
    event.preventDefault();
    var fileInput =  $("#fileInput"+repid)[0].files[0];
    var agendtype=$('#addoredit').val();
    
	 if (fileInput === undefined) {
	       Swal.fire({
	            icon: 'error',
	            title: 'Oops...',
	            text: 'Please select a file to upload!',
	            allowOutsideClick :false
	       });
	       return;
	  }
	// Check if the file is a PDF
	   var fileType = fileInput.type;
	   if (fileType !== 'application/pdf') {
	       Swal.fire({
	           icon: 'error',
	           title: 'Invalid File Type',
	           text: 'Please select a PDF file!',
	           allowOutsideClick: false
	       });
	       return;
	   }
	   // Check if the file size is less than 10MB (10 * 1024 * 1024 bytes)
	   var fileSize = fileInput.size;
	   if (fileSize > 10 * 1024 * 1024) {
	       Swal.fire({
	           icon: 'error',
	           title: 'File Too Large',
	           text: 'Please select a file smaller than 10MB!',
	           allowOutsideClick: false
	       });
	       return;
	   }
	   
       Swal.fire({
            title: 'Are you sure to upload?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: 'green',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes'
        }).then((result) => {
            if (result.isConfirmed) {
		        var projectid = <%= projectid %>;
		        var formData = new FormData();
		        formData.append("file", $("#fileInput"+repid)[0].files[0]);
		        formData.append("fileType", type);
		        formData.append("fileRepId", repid);
		        formData.append("projectid", projectid);
		        formData.append("mainId", mainId);
		        formData.append("subId", subId);
		        formData.append("docName", docName);
		        formData.append("version", version);
		        formData.append("release", release);
		        formData.append("agendaid", agendaid);
		        formData.append("${_csrf.parameterName}", "${_csrf.token}");
		        // Use AJAX to submit the form data
		        $.ajax({
		            url: 'DocFileUpload.htm',
		            type: 'POST',
		            data: formData,
		            contentType: false,
		            processData: false,
		            success: function(response) {
		            	attachid=response;
		              	 if (agendtype==='add') {
		      	        	setagendaattachval(attachid,docName);
		      	        }else{
		             	  Swal.fire({
		 		    	       	title: "Success",
		 		                text: "File Uploaded Successfully",
		 		                icon: "success",
		 		                allowOutsideClick :false
		 		         		});
		             	  $('#pdfModal').hide();
		             	  $('.swal2-confirm').click(function() {
		   	                location.reload();
		   	               });
		             	}
		            },
		            error: function(xhr, status, error) {
		            	  Swal.fire({
		                      icon: 'error',
		                      title: 'Error',
		                      text: 'An error occurred while uploading the file'
		                  });
		                  console.log(xhr.responseText);
		             }
		        });
        }
    });
}


var table1=$("#myTable").DataTable({		 
	 "lengthMenu": [5,10,25, 50, 75, 100 ],
	 "pagingType": "simple",
	 "pageLength": 5,
	 "language": {
	      "emptyTable": "Files not Found"
	    }
});
function getEmployee(ele){
	
	var empid=ele.value;

	var labocode= $("#PresLabCode_"+ ele.id.split("_")[1]).val()
	$.ajax({
		type:'GET',
		url:'checkMeetingEmpWise.htm',
		datatype:'json',
		data:{
			empid:empid,
			labocode:labocode,
			scheduleid:<%=scheduleid%>
		},
		success:function(result){
			var ajaxresult = JSON.parse(result);
			
			if(ajaxresult.length>0){
				table1.destroy();
				
				
				
				var html=''
				for(var i=0;i<ajaxresult.length;i++){
					html+='<tr><td>'+ (i+1)   +'</td>'
					html+='<td>'+ajaxresult[i].MeetingId   +'</td>'
					html+='<td>'+ ajaxresult[i].MeetingVenue  +'</td>'
					html+='<td>'+ ajaxresult[i].ScheduleStartTime  +'</td>'
					html+='<td>'+ ajaxresult[i].description  +'</td></tr>'
				}		
				
				$('#meetingbody').html(html);
			
				
				 
				 table1=$("#myTable").DataTable({		 
					 "lengthMenu": [5,10,25, 50, 75, 100 ],
					 "pagingType": "simple",
					 "pageLength": 5,
					 "language": {
					      "emptyTable": "Files not Found"
					    }
				}); 
				 $('#meetingModal').modal('show');
				
				
			}
			
			
		}
	})
	
}
function getEmployeeForDH(ele){

	var empid=ele.value;

	var labocode= $("#PresLabCode_DH_"+ ele.id.split("_")[2]).val();

	$.ajax({
		type:'GET',
		url:'checkMeetingEmpWise.htm',
		datatype:'json',
		data:{
			empid:empid,
			labocode:labocode,
			scheduleid:<%=scheduleid%>
		},
		success:function(result){
			var ajaxresult = JSON.parse(result);
			
			if(ajaxresult.length>0){
				table1.destroy();
				
				
				
				var html=''
				for(var i=0;i<ajaxresult.length;i++){
					html+='<tr><td>'+ (i+1)   +'</td>'
					html+='<td>'+ajaxresult[i].MeetingId   +'</td>'
					html+='<td>'+ ajaxresult[i].MeetingVenue  +'</td>'
					html+='<td>'+ ajaxresult[i].ScheduleStartTime  +'</td>'
					html+='<td>'+ ajaxresult[i].description  +'</td></tr>'
				}		
				
				$('#meetingbody').html(html);
			
				
				 
				 table1=$("#myTable").DataTable({		 
					 "lengthMenu": [5,10,25, 50, 75, 100 ],
					 "pagingType": "simple",
					 "pageLength": 5,
					 "language": {
					      "emptyTable": "Files not Found"
					    }
				}); 
				 $('#meetingModal').modal('show');
				
				
			}
			
			
		}
	})
	
}

let cloneIndex = 1000;

$(document).on("click", ".btn-remove", function () {
    let row = $(this).closest("tr");
    // Change button to "Add"
    row.find("td:last").html(
        '<button type="button" class="btn btn-success btn-add"><i class="fa fa-plus"></i></button>'
    );
    // Move row to draft table
    $("#draftTable tbody").append(row);
    $("#draftTable").show();
    toggleSubmitButton();
});


$(document).on("click", ".btn-add", function () {
    let row = $(this).closest("tr");
    // Change button back to "Remove"
    row.find("td:last").html(
        '<button type="button" class="btn btn-success btn-clone"><i class="fa fa-plus"></i></button>  <button type="button" class="btn btn-danger btn-remove"><i class="fa fa-minus"></i></button>'
    );
    // Move row back to main table
    $("#mainTable tbody").append(row);
    // Hide draft table if empty
    if ($("#draftTable tbody tr").length === 0) {
        $("#draftTable").hide();
    }
});
$(document).on("click", ".btn-clone", function () {

    let row = $(this).closest("tr");

    // Destroy Select2 BEFORE cloning
    row.find("select.select2-hidden-accessible").each(function () {
        $(this).select2("destroy");
    });

    let clone = row.clone(false, false);
    let index = cloneIndex++;

    // Restore Select2 on original row
    row.find("select").select2({
        width: "100%",
        dropdownParent: $('body')
    });

    // Update IDs & onchange handlers in clone
    clone.find("select, input").each(function () {
        let oldId = $(this).attr("id");
        if (oldId) {
            let baseId = oldId.split("_")[0];
            $(this).attr("id", baseId + "_" + index);
        }
        
        if (["duration"].includes($(this).attr("name"))) {
            $(this).val("");
        }

        // Clear selects
        if ($(this).is("select")) {
            $(this).val("");  
        }
        
        let onchange = $(this).attr("onchange");
        if (onchange) {
            let newOnchange = onchange.replace(/\('\d+'\)/, "('" + index + "')");
            $(this).attr("onchange", newOnchange);
        }
    });

    // Replace last button
    clone.find("td:last").html(`
        <button type="button" class="btn btn-danger btn-remove-clone">
            <i class="fa fa-minus"></i>
        </button>
    `);

    // Insert cloned row
    row.after(clone);

    // Fresh Select2 ONLY for cloned row
    clone.find("select").select2({
        width: "100%",
        dropdownParent: $('body')
    });
});


$(document).on("click", ".btn-remove-clone", function () {
 	let row = $(this).closest("tr");
 	row.remove();
 	toggleSubmitButton();
});
 
$("#subHeadTable").on('click', '.btn_clone_new', function () {

    let index1 = $('.tr_clone_DH').length;

    var $tr = $('.tr_clone_DH').last();

    // Destroy Select2 before cloning
    $tr.find("select.select2-hidden-accessible").each(function () {
        $(this).select2('destroy');
    });

    var clone = $tr.clone();

    // Restore Select2 on original row
    $tr.find("select").select2({
        width: '100%',
        dropdownParent: $('body'),
    });

    // Update IDs and attributes
    clone.find("select, input").each(function () {
        let oldId = $(this).attr("id");
        if (oldId) {
            let baseId = oldId.split("_")[0];
            $(this).attr("id", baseId + "_DH_" + index1);
        }

        let onchange = $(this).attr("onchange");
        if (onchange) {
            let newOnchange = onchange.replace(/\('\d+'\)/, "('" + index1 + "')");
            $(this).attr("onchange", newOnchange);
        }
    });

    // Insert cloned row
    $tr.after(clone);

    // Reinitialize Select2 for cloned row
    clone.find("select").select2({
        width: '100%',
        dropdownParent: $('body'),
    });

    clone.find("input").not('[type="hidden"][name="projectid"]').val("");
});

 
 $("#subHeadTable").on('click','.btn-remove-clone-new' ,function() {
		
	 var cl=$('.tr_clone_DH').length;
	 	
	 if(cl>1){
	    var $tr = $(this).closest('.tr_clone_DH');
	   
	    var $clone = $tr.remove();
	    $tr.after($clone);
	    
	 }else if(cl===1){
		 var row = $(this).closest('.tr_clone_DH');
		 
		 row.find("td:last").html(
	        '<button type="button" class="btn btn-success btn-add-subheadtable"><i class="fa fa-plus"></i></button>'
	    );
	    // Move row to draft table
	    $("#draftTable tbody").append(row);
	    $("#draftTable").show();
	    $("#subHeadTable").hide();
	    toggleSubmitButton();
	 }
	    
});
 
$('#draftTable').on('click','.btn-add-subheadtable', function(){
	var row = $(this).closest('.tr_clone_DH');
	
	row.find("td:last").html(
        '<button type="button" class="btn btn-danger btn-remove-clone-new"> <i class="fa fa-minus"></i> </button>'
    );
    // Move row back to main table
    $("#subHeadTable tbody").append(row);
    $("#subHeadTable").show();
    // Hide draft table if empty
    if ($("#draftTable tbody tr").length === 0) {
        $("#draftTable").hide();
    }
    toggleSubmitButton();
});
 
 
/* 
$(document).on("click", ".btn-remove-clone-new", function () {
 	let row = $(this).closest("tr");
 	row.remove();
 	toggleSubmitButton();
});  */


function toggleSubmitButton() {
    if ($("#mainTable tbody tr").length === 0 && $("#subHeadTable tbody tr").length === 0) {
        $(".submit").prop("disabled", true);
    } else{
        $(".submit").prop("disabled", false);
    }
}

// Run when page loads
$(document).ready(function () {
    toggleSubmitButton();
});

// Run whenever a row is moved
$(document).on("click", ".btn-remove, .btn-add", function () {
    toggleSubmitButton();
});

</script>

</body>
</html>