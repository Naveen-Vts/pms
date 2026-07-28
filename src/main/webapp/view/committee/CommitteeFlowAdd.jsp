<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="com.vts.pfms.master.model.IndustryPartner"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat"%>
<%@page import="java.time.LocalTime"%>

<%@page import="java.time.format.DateTimeFormatter"%>
	<%@page import="com.vts.pfms.FormatConverter"%>
	<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:url value="/resources/css/sweetalert2.min.css" var="sweetalertCss" />
<spring:url value="/resources/js/sweetalert2.min.js" var="sweetalertJs" />
<!DOCTYPE html>
<html>

<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/committeeModule/CommitteeFlowAdd.css" var="CommitteeFlowAdd" />
<link href="${CommitteeFlowAdd}" rel="stylesheet" />
<link href="${sweetalertCss}" rel="stylesheet" />
<script src="${sweetalertJs}"></script>
</head>
<body>
<%

Object[]CommitteMainEnoteList = (Object[])request.getAttribute("CommitteMainEnoteList");
Object[] committeedata=(Object[])request.getAttribute("committeedata");

Object[] projectdata=(Object[])request.getAttribute("projectdata"); 
Object[] divisiondata=(Object[])request.getAttribute("divisiondata");
Object[] initiationdata=(Object[])request.getAttribute("initiationdata");
String initiationid=committeedata[4].toString();
String divisionid=committeedata[3].toString();
String projectid=committeedata[2].toString();
String committeemainid=(String)request.getAttribute("committeemainid");
FormatConverter fc = new FormatConverter();
SimpleDateFormat sdf = fc.getRegularDateFormat();
SimpleDateFormat sdf1 = fc.getSqlDateFormat();
SimpleDateFormat inputFormat = new SimpleDateFormat("ddMMMyyyy",Locale.ENGLISH);
SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
String EmpId = (Long)session.getAttribute("EmpId")+"";
List<Object[]>employeelist = (List<Object[]>)request.getAttribute("employeelist");
List<Object[]>AllLabList = (List<Object[]>)request.getAttribute("AllLabList");
List<String> forwardstatus = Arrays.asList("INI","RR1","RR2","RR3","RR4","RR5","RAP","REV");
List<String> reforwardstatus = Arrays.asList("RR1","RR2","RR3","RR4","RR5","RAP");
Object[]NewApprovalList = (Object[])request.getAttribute("NewApprovalList");
%>

<%
String ses=(String)request.getParameter("result"); 
 String ses1=(String)request.getParameter("resultfail");
	if(ses1!=null){
	%>
<%-- 	<div align="center">
	<div class="alert alert-danger" role="alert" >
                     <%=ses1 %>
                    </div></div> --%>
                    <script>
	Swal.fire({
		  icon: "warning",
		  title: "Sorry",
		  text: "<%=StringEscapeUtils.escapeHtml4(ses1)%>",s
		  allowOutsideClick :false
		});
	</script>
	<%}if(ses!=null){ %>
	<%-- <div align="center">
	<div class="alert alert-success" role="alert"  >
                     <%=ses %>
	</div></div> --%>
	<script>
	Swal.fire({
		  icon: "success",
		  title: "SUCCESS",
		  text: "<%=StringEscapeUtils.escapeHtml4(ses)%>",
		  allowOutsideClick :false
		});
	</script>
                    <%} %>

    <br />
    
<div class="container-fluid mt-n1">
	<div class="row">
		<div class="col-md-12">	
		   
			<div class="card shadow-nohover">	
						
					<div class="card-header">						
						<div class="row">										
							<div class="col-md-12"><h3 class="h3HeaderColor"><%=committeedata[8] %> 
						
							<span class="float-right">
									<%if(Long.parseLong(projectid)>0){ %> Project: <%=projectdata[4]!=null?StringEscapeUtils.escapeHtml4(projectdata[4].toString()): " - " %><%}else if (Long.parseLong(divisionid)>0){ %>  Division: <%=divisiondata[1]!=null?StringEscapeUtils.escapeHtml4(divisiondata[1].toString()): " - "  %> <%}else if(Long.parseLong(initiationid)>0){ %>Pre-Project: <%=initiationdata[1]!=null?StringEscapeUtils.escapeHtml4(initiationdata[1].toString()): " - " %> <%}else{ %>Non-Project<%} %>
								</span>
						</h3>
							</div>	
																	
						
						</div>
					</div>
					
					<div class="card-body">	
					<form action="EnoteForward.htm" method="Post" id="enotefrm">
					<div class="row">
					<div class="col-md-2">
					<label class="control-label mb-4">Reference No. :</label>
					</div>
					<div class="col-md-2">
					<input class="form-control" type="text" name="RefNo"  readonly="readonly"
					 value="<%if(CommitteMainEnoteList!=null && CommitteMainEnoteList[1]!=null){%><%=StringEscapeUtils.escapeHtml4(CommitteMainEnoteList[1].toString()) %><%}else{%><%=committeedata[11]!=null? StringEscapeUtils.escapeHtml4(committeedata[11].toString()):"" %> <%}%>">
					</div>
					
					<div class="col-md-2">
					<label class="control-label mb-4">Reference Date :</label>
					</div>
					<div class="col-md-2">
					<input class="form-control" type="text" name="RefDate" readonly
					 value="<%if(CommitteMainEnoteList!=null && CommitteMainEnoteList[2]!=null){%><%=sdf.format(sdf1.parse(StringEscapeUtils.escapeHtml4(CommitteMainEnoteList[2].toString())))%><%}else{%><%=committeedata[12]!=null?StringEscapeUtils.escapeHtml4(committeedata[12].toString()): "" %><%}%>">
					</div>
					</div>
				
					<div class="row mt-4">
					<div class="col-md-2">
					<label class="control-label mb-4">Subject :</label>
					</div>
					<div class="col-md-5">
					<textarea class="form-control" rows="2" cols="40" name="subject" placeholder="max 500 Characters" maxlength="498"><%if(CommitteMainEnoteList!=null && CommitteMainEnoteList[3]!=null){%><%=CommitteMainEnoteList[3].toString()%><%}else{%><%="Formation of " + (committeedata[8]!=null?StringEscapeUtils.escapeHtml4(committeedata[8].toString()): "")+" Committee." %><%} %></textarea>
					</div>
					
					<div class="col-md-1">
					<label class="control-label mb-4">Comment :</label>
					</div>
					<div class="col-md-4">
					<textarea class="form-control" rows="2" cols="30" name="Comment" placeholder="max 500 Characters" maxlength="498"><%if(CommitteMainEnoteList!=null && CommitteMainEnoteList[4]!=null){%><%=CommitteMainEnoteList[4].toString()%><%}else{%><%} %></textarea>
					</div>
					</div>
					<div class="row mt-4">
					<div class="col-md-2"><label class="control-label mb-4 fs-1rem">Initiated By : </label></div>
					<div class="col-md-3">
					<select class="form-control selectdee" name="InitiatedBy" id="InitiatedBy" >
					<%for(Object[]obj:employeelist){ %>
					<option value="<%=obj[0].toString()%>"   <%if(CommitteMainEnoteList!=null && obj[0].toString().equalsIgnoreCase(CommitteMainEnoteList[17].toString())) {%> selected  <%} %>><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "%>, <%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></option>
					<%} %>
					</select>
					</div>
					
					</div>
					<div class="row mt-4">
					<div class="col-md-2">
					<label class="control-label mb-4">Officer1 Role: &nbsp;<span class="mandatory text-danger">*</span></label>
					</div>
					<div class="col-md-2">
					<input class="form-control" required="required" maxlength="18" name="Rec1_Role" value="<%if(CommitteMainEnoteList!=null && CommitteMainEnoteList[8]!=null) {%><%=StringEscapeUtils.escapeHtml4(CommitteMainEnoteList[8].toString()) %> <%}%>">
					</div>
					<div class="col-md-2">
					<label class="control-label mb-4">Recommended Officer 1: &nbsp;<span class="mandatory text-danger">*</span></label>
					</div>
					<div class="col-md-4">
					<select class="form-control selectdee" name="Recommend1" id="Recommend1" required ="required" title ="Please select officer 1">
					<option   selected value="">SELECT</option>
					<%for(Object[]obj:employeelist){ %>
					<option value="<%=obj[0].toString()%>"  <%if(CommitteMainEnoteList!=null && CommitteMainEnoteList[7]!=null &&  obj[0].toString().equalsIgnoreCase(CommitteMainEnoteList[7].toString())) {%> selected  <%} %>><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %>, <%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></option>
					<%} %>
					</select>
					</div>
					</div>
					
					<!-- Officer 2 -->
					
					<div class="row mt-4">
					<div class="col-md-2">
					<label class="control-label mb-4">Officer2 Role: &nbsp;</label>
					</div>
					<div class="col-md-2">
					<input class="form-control" maxlength="18" name="Rec2_Role" id="Rec2_Role" value="<%if(CommitteMainEnoteList!=null && CommitteMainEnoteList[10]!=null) {%><%=CommitteMainEnoteList[10]!=null?StringEscapeUtils.escapeHtml4(CommitteMainEnoteList[10].toString()): "" %> <%}%>">
					</div>
					<div class="col-md-2">
					<label class="control-label mb-4">Recommended Officer 2: &nbsp;</label>
					</div>
					<div class="col-md-4">
					<select class="form-control selectdee" name="Recommend2" id="Recommend2">
					<option   selected value="">SELECT</option>
					<%for(Object[]obj:employeelist){ %>
					<option value="<%=obj[0].toString()%>"  <%if(CommitteMainEnoteList!=null && CommitteMainEnoteList[9]!=null &&  obj[0].toString().equalsIgnoreCase(CommitteMainEnoteList[9].toString())) {%> selected  <%} %>><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %>, <%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></option>
					<%} %>
					</select>
					</div>
					</div>
					
					
						<!-- Officer 3 -->
					
					<div class="row mt-4">
					<div class="col-md-2">
					<label class="control-label mb-4">Officer3 Role: &nbsp;</label>
					</div>
					<div class="col-md-2">
					<input class="form-control" maxlength="18" name="Rec3_Role" id="Rec3_Role" value="<%if(CommitteMainEnoteList!=null && CommitteMainEnoteList[12]!=null) {%><%=StringEscapeUtils.escapeHtml4(CommitteMainEnoteList[12].toString()) %> <%}%>">
					</div>
					<div class="col-md-2">
					<label class="control-label mb-4">Recommended Officer 3: &nbsp;</label>
					</div>
					<div class="col-md-4">
					<select class="form-control selectdee" name="Recommend3" id="Recommend3">
					<option   selected value="">SELECT</option>
					<%for(Object[]obj:employeelist){ %>
					<option value="<%=obj[0].toString()%>"  <%if(CommitteMainEnoteList!=null && CommitteMainEnoteList[11]!=null &&  obj[0].toString().equalsIgnoreCase(CommitteMainEnoteList[11].toString())) {%> selected  <%} %>><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %>, <%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></option>
					<%} %>
					</select>
					</div>
					</div>
					
					
					
							<!-- Approver -->
					
					<div class="row mt-4">
					<div class="col-md-2">
					<label class="control-label mb-4">Approving Officer Role: &nbsp;<span class="mandatory text-danger">*</span></label>
					</div>
					<div class="col-md-2">
					<input class="form-control" name="Approving_Role" maxlength="18" 
					<%if(CommitteMainEnoteList!=null && !forwardstatus.contains(CommitteMainEnoteList[15].toString())) {%> readonly="readonly" <%} %>
					value="<%if(CommitteMainEnoteList!=null && CommitteMainEnoteList[14]!=null) {%><%=StringEscapeUtils.escapeHtml4(CommitteMainEnoteList[14].toString()) %> <%}%>" required="required">
					</div>
					<div class="col-md-2">
					<label class="control-label mb-4">Approving Officer: &nbsp;<span class="mandatory text-danger" >*</span></label>
					</div>
					<div class="col-md-2">
					<select class="form-control selectdee width-80" name="ApprovingOfficerLabCode" id="ApprovingOfficerLabCode" required="required"  onchange="chooseEmp()">
					<option   selected value="">SELECT</option>
					<%for(Object[]obj:AllLabList){ %>
					<option value="<%=obj[3].toString()%>"  <%if (CommitteMainEnoteList!=null &&  CommitteMainEnoteList[22]!=null && obj[3].toString().equalsIgnoreCase(CommitteMainEnoteList[22].toString())) {%> selected  <%} %>><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - "%></option>
					<%} %>
					</select>
					</div>
					<div class="col-md-3">
					<select class="form-control selectdee" name="ApprovingOfficer" id="ApprovingOfficer" required="required"  
					<%if(CommitteMainEnoteList!=null && !forwardstatus.contains(CommitteMainEnoteList[15].toString())) {%>disabled="disabled" <%} %>
					>
				<!-- 	<option   selected value="">SELECT</option> -->
				<%-- 	<%for(Object[]obj:employeelist){ %>
					<option value="<%=obj[0].toString()%>"  <%if(CommitteMainEnoteList!=null && CommitteMainEnoteList[13]!=null &&  obj[0].toString().equalsIgnoreCase(CommitteMainEnoteList[13].toString())) {%> selected  <%} %>><%=obj[1].toString() %>, <%=obj[2].toString() %></option>
					<%} %> --%>
					</select>
					</div>
				
					</div>
					
					<div align="center" class="mt-4px">
				<% if(CommitteMainEnoteList==null ){%>
					
					<button type="button" onclick="return checkOfficer('U')" class="btn btn-sm submit" name="action" value="Update"

					>submit</button>
					<%} %>
			
				<%if(CommitteMainEnoteList!=null && forwardstatus.contains(CommitteMainEnoteList[15].toString())) {%>	
					<button type="button" onclick="return checkOfficer('U')" class="btn btn-sm edit" name="action" value="Update"

					>UPDATE</button>
					<% if(CommitteMainEnoteList!=null && EmpId.equalsIgnoreCase(CommitteMainEnoteList[17].toString())){%>
					<button type="button"  class="btn btn-sm submit" onclick="return checkOfficer('F')" id="Forward">Forward</button>
					<%} %>
					
					<%} %>
					<input id="submit" type="submit"  name="action"  hidden="hidden" />     
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />     
					<input type="hidden" name="EnoteId" value="<%=CommitteMainEnoteList!=null?CommitteMainEnoteList[0].toString():"0" %>">
					<input type="hidden" name="projectid" value="<%=projectid %>" >	
					<input type="hidden" name="divisionid" value="<%=divisionid%>"> 		
					<input type="hidden" name="initiationid" value="<%=initiationid%>"> 
					<input type="hidden" name="committeemainid" value="<%=committeemainid%>"> 
					<input type="hidden" name="flag" id="flag" value="UpdateForward">
					<input type="hidden" name="flow" value="A" id="flow">
				<%if(CommitteMainEnoteList!=null && CommitteMainEnoteList[15].toString().equalsIgnoreCase("FWD") && EmpId.equalsIgnoreCase(CommitteMainEnoteList[17].toString())) {%>	
					<button type="submit" name="flow" value="Rev" class="btn btn-sm btn-danger delete" onclick="return Revoke('enotefrm')" >REVOKE</button> <%} %>
					<a class="btn btn-sm back" type="button" href="CommitteeMainMembers.htm?committeemainid=<%=committeemainid%>">BACK</a>
					
					</div>
				
					</form>
					
						<%if(CommitteMainEnoteList!=null){ %>
				 	<div align="center" class="statusHistoryStyle">
				 	<form action="#">
				<label class="control-label mb-4">Status History: &nbsp;</label>
				<%
				   String colorCode = (String)CommitteMainEnoteList[21];
				   String className = "C" + colorCode.replace("#", "").toUpperCase();
				%>
				<button type ="submit"  class="btn btn-sm btn-link  btn-status fw-600 <%=className%>" formaction="EnoteStatusTrack.htm" value=" <%=CommitteMainEnoteList[0]%>" formtarget="_blank"  data-toggle="tooltip" data-placement="top" title="" name="EnoteTrackId"> <%=CommitteMainEnoteList[20].toString() %> 
				<i class="fa fa-external-link" aria-hidden="true"></i></button>
				</form>
				
				<form action="CommitteeEnotePrint.htm" target="_blank" class="ml-1px">
					<button type="submit" class="btn btn-sm edit editBtnStyles" data-toggle="tooltip" data-placement="top" title="Committee ENote Letter"><i class="fa fa-download fs-90"></i></button>
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />     
					<input type="hidden" name="EnoteId" value="<%=CommitteMainEnoteList[0].toString()%>">
						<input type="hidden" name="type" value="C">
						<input type="hidden" name="scheduleid" value="0">
						<input type="hidden" name="committeemainid" value="<%=CommitteMainEnoteList[5].toString()%>">
					</form>
					</div> 
					<%} %>
					</div>
					<%if(NewApprovalList!=null ){ %>
							 	<div class="row mb-3 text-center">
				                <table  align="center" >
				                	<tr>
				                	
				                		<td class="trup constituteByBg">
				                		&nbsp;<%if(Arrays.asList("FWD","RC1","RC2","RC3","APR").contains(CommitteMainEnoteList[15].toString())) {%>
				                		<img src="view/images/check.png">
				                		<%} %><br>
				                			Constituted By 
				                			<br>
				                			<%=NewApprovalList[0]!=null?StringEscapeUtils.escapeHtml4(NewApprovalList[0].toString()): " - "%>
				                		</td>
				                		<%if(NewApprovalList!=null && NewApprovalList[2]!=null){ %>
				                		<td rowspan="2">
				                			 <b>----------&gt;</b>
				                		</td >
				                		
				                		<td class="trup rec1Bg">
				                			&nbsp;<%if(Arrays.asList("RC1","RC2","RC3","APR").contains(CommitteMainEnoteList[15].toString())) {%>
				                		<img src="view/images/check.png">
				                		<%} %><br>
				                			Recommended Officer 1
				                		
				                			<br>
				                			<%=NewApprovalList[1]!=null?StringEscapeUtils.escapeHtml4(NewApprovalList[1].toString()): " - " %>
				                		</td>
				                		
				                		<%} %>
				                		<%if(NewApprovalList!=null && NewApprovalList[4]!=null){ %>
				                		<td rowspan="2">
				                			 <b>----------&gt;</b>
				                		</td>
				                		
				                		<td class="trup rec2Bg">
				                			&nbsp;<%if(Arrays.asList("RC2","RC3","APR").contains(CommitteMainEnoteList[15].toString())) {%>
				                		<img src="view/images/check.png">
				                		<%} %>
				                		<br>
				                		Recommended Officer 2
				                	
				                		<br>
				                			<%=NewApprovalList[3]!=null?StringEscapeUtils.escapeHtml4(NewApprovalList[3].toString()): " - " %>
				                		</td>
				                		<%} %>
				                		<%if(NewApprovalList!=null && NewApprovalList[6]!=null){ %>
				                		<td rowspan="2">
				                			 <b>----------&gt;</b>
				                		</td>
				                		
				                		<td class="trup rec3Bg">
				                			&nbsp;<%if(Arrays.asList("RC3","APR").contains(CommitteMainEnoteList[15].toString())) {%>
				                		<img src="view/images/check.png">
				                		<%} %>
				                		<br>
				                			Recommended Officer 3
				                		
				                			<br>
				                			<%=NewApprovalList[5]!=null?StringEscapeUtils.escapeHtml4(NewApprovalList[5].toString()): " - " %>
				                		</td>
				                		<%} %>
				                		<%if(NewApprovalList!=null && NewApprovalList[8]!=null){ %>
				                		<td rowspan="2">
				                			 <b>----------&gt;</b>
				                		</td>
				                		
				                		<td class="trup aprBg">
				                		&nbsp;<%if(Arrays.asList("APR").contains(CommitteMainEnoteList[15].toString()) && CommitteMainEnoteList[22]!=null && CommitteMainEnoteList[22].toString().equalsIgnoreCase((String)session.getAttribute("labcode")) ) {%>
				                		<img src="view/images/check.png">
				                		<%} %>
				                		<br>
				                			Approving Officer
				                			<br>
				                			<%=NewApprovalList[7]!=null?StringEscapeUtils.escapeHtml4(NewApprovalList[7].toString()): " - " %>, <%=NewApprovalList[9]!=null?StringEscapeUtils.escapeHtml4(NewApprovalList[9].toString()): " - " %>
				                	
				                		</td>
				                		<%} %>
				                			                		
				                	</tr>	
				                	
				                	</table>
				                	
				                <br><div class="col-md-12" >
				                <%if(CommitteMainEnoteList[22]!=null && !CommitteMainEnoteList[22].toString().equalsIgnoreCase((String)session.getAttribute("labcode"))){ %>
				                	<%if(NewApprovalList[5]!=null) {%>
				                <h6 class="text-danger">Note : This committee will be approved once it receives a recommendation from <%=StringEscapeUtils.escapeHtml4(NewApprovalList[5].toString()) %>.</h6>
				                <%}else if(NewApprovalList[3]!=null){ %>
				             <h6 class="text-danger">Note : This committee will be approved once it receives a recommendation from <%=StringEscapeUtils.escapeHtml4(NewApprovalList[3].toString()) %>.</h6>
				                <%}else if(NewApprovalList[1]!=null){ %>
				             <h6 class="text-danger">Note : This committee will be approved once it receives a recommendation from <%=StringEscapeUtils.escapeHtml4(NewApprovalList[1].toString()) %>.</h6>
				                <%}} %>
				                </div>	
				                	</div>
					<%} %>
					</div>
					
					</div>
					</div>
					</div>
					
					
					</div>
					
					
			<script type="text/javascript">
			function checkOfficer(value){
				var InitiatedBy = Number($('#InitiatedBy').val());
				var Recommend1 = Number($('#Recommend1').val());
				var Recommend2 = Number($('#Recommend2').val());
				var Recommend3 = Number($('#Recommend3').val());
				var ApprovingOfficer = Number($('#ApprovingOfficer').val());
				var Rec2_Role = $('#Rec2_Role').val().trim();
				var Rec3_Role = $('#Rec3_Role').val().trim();
				
				if(Recommend3!==0 && Recommend2===0){
					Swal.fire({
						  icon: "error",
						  title: "Oops...",
						  text: "Recommended Officer 2 field can not be empty despite having Recommended Officer 3!",
						  allowOutsideClick :false
						});
			
					return false;
				}
				if(Recommend2!==0 && !Rec2_Role.length>0){
					Swal.fire({
						  icon: "error",
						  title: "Oops...",
						  text: "Recommended Officer 2 role can not be empty!",
						  allowOutsideClick :false
						});
					 $('#Rec2_Role').focus();
					return false;
				}
				if(Recommend3!==0 && !Rec3_Role.length>0){
					Swal.fire({
						  icon: "error",
						  title: "Oops...",
						  text: "Recommended Officer 3 role can not be empty!",
						  allowOutsideClick :false
						});
					
					 $('#Rec3_Role').focus();
					return false;
				}
				console.log(Rec2_Role.length)
				console.log(Rec3_Role.length)
				
				
				var array = Array.of( Recommend1, Recommend2, Recommend3, ApprovingOfficer);
				
	
			
				for(var i=0;i<array.length-1;i++){
					if(array[i]!==0){
						for(var j=i+1;j<array.length;j++ ){
							if(array[i]===array[j]){
								Swal.fire({
									  icon: "error",
									  title: "Oops...",
									  text: "You can not choose same person for different roles.",
									  allowOutsideClick :false
									});
						
								return false;
							}
						}
					}
					
				}
			
				if(value==='U'){
					var msg ="Are you sure to submit?"
							$('#submit').val('UPDATE');
				}else{
					var msg ="Are you sure to forward?"
						$('#submit').val('Forward');
				}
				
				
				/* if(confirm(msg)){
					$('#submit').click();
				}else{
					event.preventDefault();
				} */
				
				 event.preventDefault(); // Prevent the default action initially

			        Swal.fire({
			            title: msg,
			            icon: 'question',
			            showCancelButton: true,
			            confirmButtonColor: 'green',
			            cancelButtonColor: '#d33',
			            confirmButtonText: 'Yes'
			        }).then((result) => {
			            if (result.isConfirmed) {
			                $('#submit').click(); // Trigger the submit action
			            } else {
			                // Optionally do something if cancelled
			                console.log("Action cancelled");
			            }
			        });
			}
			
			
			function Revoke(enotefrm){
				$('#flag').val('');
				$('#flow').val('REV');
				
				/* if(confirm('Are you sure to revoke?')){
					$('#enotefrm').submit();
				}else{
					event.preventDefault();
				} */
				
				 event.preventDefault(); // Prevent the default action initially

			        Swal.fire({
			            title: 'Are you sure to revoke?',
			            icon: 'question',
			            showCancelButton: true,
			            confirmButtonColor: 'green',
			            cancelButtonColor: '#d33',
			            confirmButtonText: 'Yes'
			        }).then((result) => {
			            if (result.isConfirmed) {
			                $('#submit').click(); // Trigger the submit action
			            } else {
			                // Optionally do something if cancelled
			                console.log("Action cancelled");
			            }
			        });
			}
			
			
			$(function () {
				$('[data-toggle="tooltip"]').tooltip()
				})
				
				function checkNewEmp(){
				var value=Number($('#InitiatedBy').val());
				var empId = <%=EmpId%>;
				
				if(value!==empId){
					console.log("value "+typeof value)
					console.log("empId "+typeof empId)
					$('#Forward').attr('disabled', true);
				}else{
			
					 $('#Forward').prop('disabled', false);
				}
			}
			
			$( document ).ready(function() {
				<%if(CommitteMainEnoteList!=null && CommitteMainEnoteList[13]!=null){%>
				var value = "<%=CommitteMainEnoteList[13].toString()%>";
				chooseEmp(value);
				<%}%>;
				
			});
			
			
			function chooseEmp(value){
				var labCode=$('#ApprovingOfficerLabCode').val();
				$.ajax({
						
						type : "GET",
						url : "ActionAssigneeEmployeeList.htm",
						data : {
							LabCode : labCode,	
						},
						datatype : 'json',
						success : function(result) {
							var result = JSON.parse(result);
							var values = Object.keys(result).map(function(e) {
								return result[e]
							});
						
							var s = '';
							s += '<option   value="">SELECT</option>';
						/* 	if($AssigneeLabCode == '@EXP'){
								
							} */
							for (i = 0; i < values.length; i++) 
							{

								s += '<option value="'+values[i][0]+'">'+values[i][1].replaceAll("<","").replaceAll(">","") + ', ' +values[i][3].replaceAll("<","").replaceAll(">","") + '</option>';
							} 
							
							$('#ApprovingOfficer').html(s);
						 $('#ApprovingOfficer').val(''+value).trigger('change'); 
						}
					});
			}
			</script>		
					
				</body>	
</html>