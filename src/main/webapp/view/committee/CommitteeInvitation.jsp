<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.time.LocalDate"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"
	import="java.util.*,com.vts.*,java.text.SimpleDateFormat"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/committeeModule/CommitteeInvitation.css" var="CommitteeInvitation" />
<link href="${CommitteeInvitation}" rel="stylesheet" />
<title>COMMITTEE INVITATION</title>
</head>
<body>
	<%
		SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String committeescheduleid=(String)request.getAttribute("committeescheduleid");
		Object[] committeescheduledata=(Object[])request.getAttribute("committeescheduledata");
		String Committeeid=committeescheduledata[7].toString();
		List<Object[]> agendalist=(List<Object[]>) request.getAttribute("agendalist");
		String labid=(String)request.getAttribute("labid");
		String committeemainid=(String)request.getAttribute("committeemainid");
		List<Object[]> clusterlablist=(List<Object[]>) request.getAttribute("clusterlablist");

		String LabCode= (String) session.getAttribute("labcode");
		List<Object[]> committeeallmemberlist=(List<Object[]>) request.getAttribute("committeeallmemberlist");
		List<Object[]> repInvitationList=(List<Object[]>) request.getAttribute("repInvitationList");
		
		List<Object[]> agendaList=(List<Object[]>) request.getAttribute("agendaList");
		String ccmFlag = (String)request.getAttribute("ccmFlag");
		String committeeId = (String)request.getAttribute("committeeId");
		boolean isSubmit = repInvitationList == null || repInvitationList.isEmpty();
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


<form  action="CommitteeInvitationCreate.htm" method="POST" name="myfrm1" id="myfrm1">

	<div class="container-fluid">		
		<div class="row">
			<div class="col-md-12">
				<div class="card shadow-nohover">
				
					<div class="card-header">
						<div class="row">
							<div class="col-md-3" >
					  			<h4><%=committeescheduledata[8] %> Invitations</h4>
							 </div>
							 <div class="col-md-9 meetingDivStyle" align="right">
					 			<h5 class="h5MeetingColor">(Meeting Id : <%=committeescheduledata[12]!=null?StringEscapeUtils.escapeHtml4(committeescheduledata[12].toString()): " - " %>) &nbsp;&nbsp; - &nbsp;&nbsp; (Meeting Date & Time : <%= committeescheduledata[2]!=null?sdf.format(sdf1.parse( StringEscapeUtils.escapeHtml4(committeescheduledata[2].toString()))):" - "%>  &&  <%=committeescheduledata[3]!=null?StringEscapeUtils.escapeHtml4(committeescheduledata[3].toString()): " - " %>)</h5>
							 </div>
					 	</div>
					</div>
				
							<div class="card-body">

								<div class="row">
								
									<div class="col-md-4">
										<table>
											<tr>
												<td><label class="control-label">Chairperson </label></td>
											</tr>
											<tr>
											<%for(int i=0;i<committeeallmemberlist.size();i++){
												Object[] obj=committeeallmemberlist.get(i);
												if(obj[8].toString().equalsIgnoreCase("CC")){%>
												<td><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - "%>, <%= obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - "%> (<%= obj[9]!=null?StringEscapeUtils.escapeHtml4(obj[9].toString()): " - "%>)
													<input type="hidden" name="chairperson" value="<%=obj[5]%>,CC,<%=obj[3]%>,<%=obj[11]%>">
													<input type="hidden" name="empid" value="<%=obj[5]%>,CC,<%=obj[3]%>,<%=obj[11]%>">
													<input type="hidden" name="Labcode" value="<%=obj[9] %>" />
												</td>
											<%	committeeallmemberlist.remove(i);
												break;}
											}%>
											</tr>
										</table>
									</div>
									
									<div class="col-md-4">
										<table>
											<tr>
												<td><label class="control-label">Member Secretary   </label></td>
												
											</tr>
											<tr>
											
												<%for(int i=0;i<committeeallmemberlist.size();i++){
													Object[] obj=committeeallmemberlist.get(i);
													if(obj[8].toString().equalsIgnoreCase("CS")){%>
													<td><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - "%>, <%= obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - "%> (<%= obj[9]!=null?StringEscapeUtils.escapeHtml4(obj[9].toString()): " - "%>)
														<input type="hidden" name="empid" value="<%=obj[5]%>,CS,<%=obj[3]%>,<%=obj[11]%>">
														<input type="hidden" name="Labcode" value="<%=obj[9] %>" />
													</td>
												<%	committeeallmemberlist.remove(i);
													break;}
												}%>									
											</tr>
										</table>
														
									</div>
									<div class="col-md-4">
										<table>
											<tr>
												<td><label class="control-label">Co-Chairperson  </label></td>
												
											</tr>
											<tr>
											
												<%for(int i=0;i<committeeallmemberlist.size();i++){
													Object[] obj=committeeallmemberlist.get(i);
													if(obj[8].toString().equalsIgnoreCase("CH")){%>
													<td><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - "%>, <%= obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - "%> (<%= obj[9]!=null?StringEscapeUtils.escapeHtml4(obj[9].toString()): " - "%>)
														<input type="hidden" name="empid" value="<%=obj[5]%>,CH,<%=obj[3]%>,<%=obj[11]%>">
														<input type="hidden" name="Labcode" value="<%=obj[9] %>" />
													</td>
												<%	committeeallmemberlist.remove(i);
													break;}
												}%>									
											</tr>
										</table>
														
									</div>				
								</div>
					
					<br>
					
					<% 	LocalDate scheduledate=LocalDate.parse(committeescheduledata[2].toString());
					 	LocalDate todaydate=LocalDate.now();	%>
						
						
									<div class="row">
											<div  class="col-md-4">
											
												<h5> Internal Members</h5> 
													<hr>									
												 <table border='0'>
													<tbody>
														<%int count = 0;
														for(int i=0;i<committeeallmemberlist.size();i++){
															Object[] obj=committeeallmemberlist.get(i);
															if(obj[8].toString().equalsIgnoreCase("CI")){
															count++;	
														%>
														<tr>
														<td class="tdclass"><%=count%> )</td> 
														<td><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - "%>, <%= obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - "%> (<%= obj[9]!=null?StringEscapeUtils.escapeHtml4(obj[9].toString()): " - "%>)
															<input type="hidden" name="empid" value="<%=obj[5]%>,CI,<%=obj[3]%>,<%=obj[11]%>">
															<input type="hidden" name="Labcode" value="<%=obj[9] %>" />
														</td>						
														
																																		
														</tr>
														
														<% }
														}%>
													</tbody>
												</table>						
												<br>
										</div>
								
								 	
								
								
									<div  class="col-md-4">
									<%int count1 = 0;
									if(committeeallmemberlist.size()>count){ %>
									<h5>External Members (Within DRDO)</h5>
										<hr>
									
									 <table border='0'>
										<tbody>
											<%
												for(int i=0;i<committeeallmemberlist.size();i++){
													Object[] obj=committeeallmemberlist.get(i);
													if(obj[8].toString().equalsIgnoreCase("CW")){
													count1++;	
												%>
												<tr>
													<td class="tdclass"><%=count1%> )</td> 
													<td><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - "%>, <%= obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - "%> (<%= obj[9]!=null?StringEscapeUtils.escapeHtml4(obj[9].toString()): " - "%>)
														<input type="hidden" name="empid" value="<%=obj[5]%>,CW,<%=obj[3]%>,<%=obj[11]%>">
														<input type="hidden" name="Labcode" value="<%=obj[9] %>" />
													</td>						
												</tr>
															
												<% }
												}%>
											</tbody>
			
									
									</table>						
									<br>	
								<%} %>	
								</div>
							
								
								<%if(committeeallmemberlist.size()>(count+count1)){ %>
								
									<div  class="col-md-4">
									
									<h5>External Member (Outside DRDO)</h5>
										<hr>						
									 <table border='0'>
									 	
									 	<tbody>
											<%int count2 = 0;
												for(int i=0;i<committeeallmemberlist.size();i++){
													Object[] obj=committeeallmemberlist.get(i);
													if(obj[8].toString().equalsIgnoreCase("CO")){
													count2++;	
												%>
												<tr>
													<td class="tdclass"><%=count2%> )</td> 
													<td><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - "%>, <%= obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - "%> (<%= obj[9]!=null?StringEscapeUtils.escapeHtml4(obj[9].toString()): " - "%>)
														<input type="hidden" name="empid" value="<%=obj[5]%>,CO,<%=obj[3]%>,<%=obj[11]%>">
														<input type="hidden" name="Labcode" value="<%=obj[9] %>" />
													</td>						
																																										
												</tr>
															
												<% }
												}%>
										</tbody>
			
										
									</table>						
									<br>	
									
								</div>
								<%} %>
								
								<%if(ccmFlag==null || (ccmFlag!=null && !ccmFlag.equalsIgnoreCase("Y"))) {%>								
								<!-- Prudhvi - 27/03/2024 start-->
								<%if(committeeallmemberlist.size()>(count+count1)){ %>
								
									<div  class="col-md-4">
									
									<h5>Industry Partner</h5>
										<hr>						
									 <table border='0'>
									 	
									 	<tbody>
											<%int count2 = 0;
												for(int i=0;i<committeeallmemberlist.size();i++){
													Object[] obj=committeeallmemberlist.get(i);
													if(obj[8].toString().equalsIgnoreCase("CIP")){
													count2++;	
												%>
												<tr>
													<td class="tdclass"><%=count2%> )</td> 
													<td><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - "%>, <%= obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - "%> (<%= obj[9]!=null?StringEscapeUtils.escapeHtml4(obj[9].toString()): " - "%>)
														<input type="hidden" name="empid" value="<%=obj[5]%>,CIP,<%=obj[3]%>,<%=obj[11]%>">
														<input type="hidden" name="Labcode" value="<%=obj[9] %>" />
													</td>						
																																										
												</tr>
															
												<% }
												}%>
										</tbody>
			
										
									</table>						
									<br>	
									
								</div>
								<%} %>
								<!-- Prudhvi - 27/03/2024 end-->
								<%} %>
								
							</div>
							<div class="row">
								<div class="col-md-6">
										
									<h5>Agenda Presenters</h5>
									<hr><br>
										
									<table border='0'>
									<tr>
										<th>&emsp; &emsp;</th>
										<th><label class="control-label">Agenda Item</label></th><th>&emsp; &emsp;</th>
										<th><label class="control-label">Presenter</label></th>
									</tr>
										<%
											if(agendalist!=null && agendalist.size()>0) {
											int count4 = 1;
											for (Object[] obj : agendalist) {
										%>
									<tr>
										<td>
											<label class="control-label"> <%=count4%>)</label>
										</td>
										<td>
											<label class="control-label"><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - " %> </label>
										</td>
										<td>
											&emsp; :&emsp;
										</td>
										<td>
											<%=obj[10]!=null?StringEscapeUtils.escapeHtml4(obj[10].toString()): " - "%>, <%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - "%> (<%=obj[14]!=null?StringEscapeUtils.escapeHtml4(obj[14].toString()): " - "%>) 
											<input type="hidden" name="empid" value="<%=obj[9]%>,P,<%=obj[13] %>">
											<input type="hidden" name="Labcode" value="<%=obj[14] %>" />	
										</td>
											
									</tr>
									<%	count4++; }	%>
									<%} else if(agendaList!=null && agendaList.size()>0) { 
										int count4 = 1;
										for (Object[] obj : agendaList) {
											if(obj[6]!=null && !obj[6].toString().equalsIgnoreCase("0")) {
									%>
									<tr>
										<td>
											<label class="control-label"> <%=count4%>)</label>
										</td>
										<td>
											<label class="control-label"><%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - " %> </label>
										</td>
										<td>
											&emsp; :&emsp;
										</td>
										<td>
											
												<%=obj[9]!=null?StringEscapeUtils.escapeHtml4(obj[9].toString()): " - " %>
											 (<%=obj[5]!=null?StringEscapeUtils.escapeHtml4(obj[5].toString()): " - "%>) 
											<input type="hidden" name="empid" value="<%=obj[6]%>,P,<%=obj[10] %>">
											<input type="hidden" name="Labcode" value="<%=obj[5] %>" />	
										</td>
											
									</tr>
									<%count4++;} } }%>
								</table>
												
							</div>
							<%if(repInvitationList != null || !repInvitationList.isEmpty()){ %>
							<div class="col-md-2">	</div><div class="col-md-4">
						    <h5>Representatives</h5>
						    <hr class="pb-10px">
						
						    <table class="table table-borderless align-middle mb-0">
						    <% 
						        int repcount = 1;
						    	int repAdded = 0; 
						        ArrayList<String> membertypes = new ArrayList<String>(Arrays.asList("CC","CS","PS","CH","CI","CW","CO","P","I","W","E","CIP","IP","SPL"));
						        
						        for (int i = 0; i < repInvitationList.size(); i++) {
						            Object[] item = repInvitationList.get(i);
						            String repType = item[3] != null ? item[3].toString() : "";
						
						            if (!membertypes.contains(repType)) {
						                String invitationId = item[0] != null ? item[0].toString() : "";
						                String empName = item[8] != null ? item[8].toString().trim() : "";
						                String labCode = item[13] != null ? item[13].toString().trim() : "";
						                String repCode = repType;
						                boolean needsAction = (item[1] == null);
						                
						                String displayName = empName.isEmpty() ? repCode : empName + " (" + labCode + ")";
						    %>
						                <tr class="border-bottom hover-bg">
						                    <!-- Counter Cell -->
						                    <td class="text-secondary fw-semibold text-center" style="width: 40px;">
						                        <%= repcount %>.
						                    </td>
						
						                    <!-- Details Cell -->
						                    <td>
						                        <span id="repName<%= repCode %>" data-member-name="<%= StringEscapeUtils.escapeHtml4(displayName) %>">
						                            <% if (empName.isEmpty()) { %>
						                                <span class="fw-bold text-dark"><%= StringEscapeUtils.escapeHtml4(repCode) %></span>
						                            <% } else { %>
						                                <div class="d-flex align-items-center gap-2 wrap flex-wrap">
						                                    <span class="fw-bold"><%= StringEscapeUtils.escapeHtml4(empName) %></span>
						                                    <span class=" m-2 fw-normal">(<%= StringEscapeUtils.escapeHtml4(labCode) %>)</span>
						                                    <span class="text-primary fw-medium">REP_<%= StringEscapeUtils.escapeHtml4(repCode) %></span>
						                                </div>
						                            <% } %>
						                        </span>
						                    </td>
						
						                    <!-- Action Cell -->
						                    <td class="text-end" style="width: 60px;">
						                        <% if (needsAction) { %>
						                            <button class="btn btn-sm btn-icon btn-outline-secondary editRepBtn" 
						                                    type="button" 
						                                    title="Add Representative"
						                                    data-repcode="<%= repCode %>">
						                                <img src="view/images/edit.png" alt="Edit" width="16" height="16">
						                            </button>
						                        <% }else{						                        	
						                        	repAdded++;
						                        }%>
						                    </td>
						                </tr>
						    <% 
						                repcount++;
						            }
						        } 
						        
						        isSubmit = repAdded == repInvitationList.size();
						    %>
						    </table>
						</div>
						<%} %>
					</div>
					
					
						
	
					
					   	<div class="row">
					   		<div class="col-md-12">		   			   			
					   			<div  align="center">
				   					
					            	<button type="submit" id="submit" class="btn  btn-sm submit" onclick="return submitFn(<%= isSubmit %>)" >SUBMIT</button>
					            	<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
									<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid%>">
									<input type="hidden" name="Committeeidmainid" value="<%=committeemainid%>">
									<input type="hidden" name="ccmFlag" value="<%=ccmFlag%>">
									<input type="hidden" name="inviteFlag" value="Y">
									<button class="btn btn-info btn-sm  shadow-nohover back" type="button" onclick="submitForm('backfrm1');">Back</button>
								</div>
							</div>
						</div>	  
					</div>
				</div>
			</div>
		</div>        			
	 </div> 
</form>						<%if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {%>
		          				<form method="post" action="CCMSchedule.htm" id="backfrm1" >
									<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
									<input type="hidden" name="ccmScheduleId" value="<%=committeescheduleid %>">
									<input type="hidden" name="committeeMainId" value="<%=committeemainid %>">
									<input type="hidden" name="committeeId" value="<%=committeeId %>">
									<!-- <button class="btn btn-info btn-sm  shadow-nohover back" formaction="CommitteeScheduleView.htm">Back</button> -->
								</form> 
	          				
	          				<%} else{%>
		          				<form method="post" action="CommitteeScheduleView.htm" id="backfrm1" >
									<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
									<input type="hidden" name="scheduleid" value="<%=committeescheduleid %>">
								</form> 
	          				<%} %>	
	          				 			
	      						<div class="modal fade" id="changeRepsModal" tabindex="-1" role="dialog">
					  <div class="modal-dialog modal-lg" role="document">
					    <div class="modal-content">
					      <div class="modal-header">
					        <h5 class="modal-title">Add Representative</h5>
					        <button type="button" class="close" data-dismiss="modal">&times;</button>
					      </div>
					      <div class="modal-body">
					        <input type="hidden" id="repCode" />
					        <input type="hidden" id="parentInvitationId" />
					        
					        <!-- Member Type Selector -->
					        <div class="form-group">
					          <label>Select Member Type</label>
					          <br>
					          <select class="form-control selectdee" id="repMemberTypeSelect" data-width="80%">
					            <option value="">-- Select Type --</option>
					            <option value="INTERNAL">Internal Member</option>
					            <option value="EXTERNAL_INSIDE">External Member (Inside DRDO)</option>
					            <option value="EXPERT">Expert (Outside DRDO)</option>
					            <!-- <option value="INDUSTRY_PARTNER">Industry Partner</option> -->
					          </select>
					        </div>
					
					        <!-- NEW: Lab Selection (Hidden by default) -->
					        <div class="form-group" id="modalLabSelectionDiv" style="display: none;">
					          <label>Select Lab</label>
					          <br>
					          <select class="form-control selectdee" id="repLabSelect" data-width="80%">
					            <option value="">-- Select Lab --</option>
					            <% for (Object[] obj : clusterlablist) {
					                 if(!LabCode.equals(obj[3].toString())){ %>
					                    <option value="<%=obj[3]%>"><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - "%></option>
					            <%   } 
					               } %>
					          </select>
					        </div>
					        
					        <div class="form-group">
					          <label>Select New Representative</label>
					          <br>
					          <select class="form-control selectdee" id="newRepSelect" name="newRepSelect" data-width="80%" data-live-search="true" required="required">
					            <option value="">-- Select Employee --</option>
					          </select>
					        </div>
					      </div>
					      <div class="modal-footer">
					        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
					        <button type="button" class="btn btn-primary" id="saveRepChangeBtn">Save</button>
					      </div>
					    </div>
					  </div>
					</div>

			




<script type="text/javascript">
function submitForm(frmid) { 
    document.getElementById(frmid).submit(); 
}

function submitFn(flag){
	if(!flag){
		alert("Please Add All The Reps Before Submiting");
		return false;
	}
	return confirm('Are You Sure To Submit?');
}

// 1. Serialize Java lists into JavaScript Arrays
var internalEmployeesList = [
    <% if(request.getAttribute("EmployeeList") != null) { 
        List<Object[]> empList = (List<Object[]>) request.getAttribute("EmployeeList");
        for (Object[] obj : empList) { %>
            { 
                id: "<%=obj[0]%>", 
                name: "<%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()):""%>", 
                desig: "<%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()):""%>", 
                desigId: "<%=obj[3]!=null?obj[3].toString():""%>", 
                labCode: "<%=obj[4]%>" 
            },
    <%  } 
    } %>
];

var expertEmployeesList = [
    <% if(request.getAttribute("ExpertList") != null) { 
        List<Object[]> expList = (List<Object[]>) request.getAttribute("ExpertList");
        for (Object[] obj : expList) { %>
            { 
                id: "<%=obj[0]%>", 
                name: "<%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()):""%>", 
                desig: "<%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()):""%>", 
                desigId: "<%=obj[3]!=null?obj[3].toString():""%>", 
                labCode: "@EXP" 
            },
    <%  } 
    } %>
];

// 2. Open Modal Event
$(document).on('click', '.editRepBtn', function () {
    var repCode = $(this).data('repcode');
    $('#repCode').val(repCode);
    
    var memberName = $('#repName' + repCode).data('memberName');
    $('#currentRepDisplay').text(memberName || "N/A");

    // Reset Form Fields
    $('#repMemberTypeSelect').val('').change();
    $('#repLabSelect').val('');
    $('#modalLabSelectionDiv').hide();
    $('#newRepSelect').html('<option value="">-- Select Employee --</option>');
    
    $('#changeRepsModal').modal('show');
});

// 3. Handle Member Type Change
$('#repMemberTypeSelect').on('change', function() {
    var type = $(this).val();
    var options = '<option value="">-- Select Employee --</option>';
    
    $('#repLabSelect').val('');
    $('#newRepSelect').html(options);

    if (type === 'EXTERNAL_INSIDE') {
        $('#modalLabSelectionDiv').show();
        return; 
    } 
    
    $('#modalLabSelectionDiv').hide();
    var targetList = [];

    if (type === 'INTERNAL') targetList = internalEmployeesList;
    else if (type === 'EXPERT') targetList = expertEmployeesList;

    targetList.forEach(function(e) {
        var tagPattern = /<[^>]*>/;
        if (tagPattern.test(e.name) || tagPattern.test(e.labCode)) return; 
        
        var valString = e.id + '|' + e.labCode + '|' + e.desigId +'|' + '<%=committeescheduleid%>';
        options += '<option value="' + valString + '">' + e.name + ' (' + e.labCode + ')</option>';
    });

    $('#newRepSelect').html(options);
});

// 4. Handle External Lab AJAX Fetch
$('#repLabSelect').on('change', function() {
    var selectedLab = $(this).val();
    $('#newRepSelect').html('<option value="">-- Select Employee --</option>');
    
    if (selectedLab) {
        $.ajax({
            type: "GET",
            url: "ExternalEmployeeListInvitations.htm",
            data: {
                LabCode: selectedLab,
                scheduleid: '<%=committeescheduleid%>'
            },
            dataType: 'json',
            success: function(result) {
                var parsedResult = typeof result === 'string' ? JSON.parse(result) : result;
                var values = Object.keys(parsedResult).map(function(e) { return parsedResult[e]; });
                
                var options = '<option value="">-- Select Employee --</option>';
                for (var i = 0; i < values.length; i++) {
                    var empId = values[i][0];
                    var empName = values[i][1].replace(/</g, "").replace(/>/g, "");
                    var desigName = values[i][3].replace(/</g, "").replace(/>/g, "");
                    var desigId = values[i][4];
                    var labcode = values[i][5];
                    
                    var valString = empId + '|' + selectedLab + '|' + desigId + '|' + '<%=committeescheduleid%>';
                    options += '<option value="' + valString + '">' + empName + ' (' + desigName + ')</option>';
                } 
                
                $('#newRepSelect').html(options);
            },
            error: function() {
                alert("Failed to fetch employees for the selected lab.");
            }
        });
    }
});

// 5. Dynamic Form Submit
$('#saveRepChangeBtn').on('click', function () {
    var repCode = $('#repCode').val();
    var selected = $('#newRepSelect').val();
    
    if (!selected) { 
        alert('Please select a representative'); 
        return; 
    }

    var parts = selected.split('|');
    
    if(!confirm("Are you sure you want to Add the representative?")) return;
    
    var form = document.createElement("form");
    form.method = "POST";
    form.action = "AddRepresentative.htm";

    function addField(name, value) {
        var input = document.createElement("input");
        input.type = "hidden";
        input.name = name;
        input.value = value;
        form.appendChild(input);
    }

    addField("repCode", repCode);
    addField("newEmpNo", parts[0]);
    addField("newLabCode", parts[1]);
    addField("designationId", parts[2]);
    addField("committeescheduleid", parts[3]);
    addField("${_csrf.parameterName}", "${_csrf.token}");

    document.body.appendChild(form);
    form.submit();
});
</script>

</body>

</html>