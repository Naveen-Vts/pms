	<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/committeeModule/CommitteeAttendance.css" var="CommitteeAttendance" />
<link href="${CommitteeAttendance}" rel="stylesheet" />
<title>COMMMITTEE ATTENDANCE</title>
<style>

.switch {
  position: relative;
  display: inline-block;
  width: 40px;
  height: 20px;
}

.switch input { opacity: 0; width: 0; height: 0; }

.slider {
  position: absolute;
  cursor: pointer;
  top: 0; left: 0; right: 0; bottom: 0;
  background-color: #ccc;
  transition: .4s;
  border-radius: 20px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 14px; width: 14px;
  left: 3px; bottom: 3px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
}

input:checked + .slider { background-color: #2196F3; }
input:checked + .slider:before { transform: translateX(20px); }
</style>
</head>
<body>
<%
SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
List<Object[]> committeeinvitedlist=(List<Object[]>)request.getAttribute("committeeinvitedlist");
List<Object[]> EmployeeList=(List<Object[]>) request.getAttribute("EmployeeList");
List<Object[]> ExpertList=(List<Object[]>) request.getAttribute("ExpertList");
String committeescheduleid=(String)request.getAttribute("committeescheduleid");
Object[] committeescheduledata=(Object[])request.getAttribute("committeescheduledata");
SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
List<Object[]> clusterlablist=(List<Object[]>) request.getAttribute("clusterlablist");

List<Object[]> committeereplist=(List<Object[]>) request.getAttribute("committeereplist");

String LabCode=(String) request.getAttribute("LabCode");
String ccmFlag = (String) request.getAttribute("ccmFlag");
String committeeMainId = (String) request.getAttribute("committeeMainId");
String committeeId = (String) request.getAttribute("committeeId");
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



<div class="container">
	<div class="row">
		<div class="col-md-12">
		
		 <div class="card shadow-nohover" >
			 <div class="card-header">
				 <div class="row">
							<div class="col-md-3" >
					  			<h4><%=committeescheduledata[8]!=null?StringEscapeUtils.escapeHtml4(committeescheduledata[8].toString()): " - " %> Invitations </h4>
							 </div>
							 <div class="col-md-9 mt-3px" align="right">
					 			<h5 class="colorWhite"> (Meeting Date & Time : <%= committeescheduledata[2]!=null?sdf.format(sdf1.parse(StringEscapeUtils.escapeHtml4(committeescheduledata[2].toString()))) : " - "%>  &  <%=committeescheduledata[3]!=null?StringEscapeUtils.escapeHtml4(committeescheduledata[3].toString()): " - " %>)</h5>
							 </div>
					 	</div>
			  </div>
			  
		      <div class="card-body" >
		   
              		
			<div class="row">
			
				<div class="col-md-12">

				<div align="center">
					<h5 class="meetingIdColor">(Meeting Id : <%=committeescheduledata[12]!=null?StringEscapeUtils.escapeHtml4(committeescheduledata[12].toString()): " - " %>) </h5>
				</div>

				<%
					if(!committeeinvitedlist.isEmpty()){%>
					<form action="InvitationSerialNoUpdate.htm" method="Post"  id="serialnoupdate">
			         	<table  class="table table-bordered table-hover table-striped table-condensed ">
			            	<thead>
			               		<tr>
			               			<th>Sl.No</th>			                    	
			                    	<th>Member Type</th>
			                    	<th >Participants</th>
			                    	<th>Role</th>
			                       	<th >Attendance</th>
			                       	<!--DLRL changes  --> 
			                       	<th>Attend Through Online</th>
			                    </tr>
			              	</thead>                        
				    		<tbody>
								<%
								int count=1;
								
									
									for(Object[] obj:committeeinvitedlist){ %>
										
								<tr>
									<td>
										<input type="number" class="form-control" name="newslno" value="<%=obj[12]!=null?StringEscapeUtils.escapeHtml4(obj[12].toString()): "" %>" min="1" max="<%=committeeinvitedlist.size()%>"> 
										<input type="hidden" name="invitationid" value="<%=obj[1] %>">
									</td>	
									<td> 
										<%  if(obj[3].toString().equalsIgnoreCase("CC")) {		 %>Chairperson<%}
											else if(obj[3].toString().equalsIgnoreCase("CS") ){	 %> Member Secretary<%}
											else if(obj[3].toString().equalsIgnoreCase("CH") ){	 %> Co-Chairperson<%}
											else if(obj[3].toString().equalsIgnoreCase("PS") ) { %>Member Secretary (Proxy) <%}
											else if(obj[3].toString().equalsIgnoreCase("CI")){   %> Internal<%}
											else if(obj[3].toString().equalsIgnoreCase("CW")){	 %> External(<%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - " %>)<%}
											else if(obj[3].toString().equalsIgnoreCase("CO")){	 %> External(<%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - "%>)<%}
											else if(obj[3].toString().equalsIgnoreCase("P") ){	 %>Presenter <%}
											else if(obj[3].toString().equalsIgnoreCase("I")){	 %> Addl. Internal<%}
											else if(obj[3].toString().equalsIgnoreCase("W") ){	 %> Addl. External(<%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - " %>)<%}
											else if(obj[3].toString().equalsIgnoreCase("E") )    {%> Addl. External(<%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - " %>)<%}
										    // Prudhvi - 27/03/2024 start
											else if(obj[3].toString().equalsIgnoreCase("CIP") )    {%> Industry Partner(<%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - " %>)<%}
											else if(obj[3].toString().equalsIgnoreCase("IP") )    {%> Addl. Industry Partner(<%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - " %>)<%}
											else if(obj[3].toString().equalsIgnoreCase("SPL") )    {%> Special Invitee<%}
											
										// Prudhvi - 27/03/2024 end
											else {%> REP_<%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - "%> (<%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - " %>)  <%}
										%>
										
									</td>
									<td><%=obj[6]!=null?StringEscapeUtils.escapeHtml4(obj[6].toString()): " - " %>, <%=obj[7]!=null?StringEscapeUtils.escapeHtml4(obj[7].toString()): " - "%> (<%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - "%>)</td>        
									
									<td>
									
									<input class="form-control" name="Role" maxlength="255" id="<%=obj[1] %>"  value="<%= obj[15]!=null ? StringEscapeUtils.escapeHtml4(obj[15].toString()): (obj[14]!=null ? StringEscapeUtils.escapeHtml4(obj[14].toString()):"")%>">
									<input type="hidden" name="LabCode"  id="LabCode<%=obj[1]%>" value="<%=obj[11]!=null?obj[11].toString():"-" %>">
									<input type="hidden" name="EmpNo" id="EmpNo<%=obj[1]%>" value="<%=obj[5]!=null?obj[5].toString():"-" %>">
									</td>
									<td>
											<input name="attendance"  onchange="FormNameEdit(<%=obj[1]%>)"  type="checkbox" <%if((obj[4]).toString().equalsIgnoreCase("P")){ %>checked<%}%> data-toggle="toggle" data-onstyle="success" data-offstyle="danger" data-width="112" data-height="15" data-on="<i class='fa fa-user' aria-hidden='true'></i> Present" data-off="<i class='fa fa-user-times' aria-hidden='true'></i> Absent" >
											<input 	type="hidden" name="sample" value="attendance<%=count %>" >	
											<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
											<input type="hidden" name="scheduleid" value="<%=committeescheduleid %>">
									</td>
									
									<td style="text-align:center">
								
										<label class="switch">
										    <input type="checkbox" onchange="OnlineFormNameEdit(<%=obj[1]%>, this)" 
										           <%if(obj[17] !=null && (obj[17]).toString().equalsIgnoreCase("Y")){ %>checked<%}%>>
										    <span class="slider"></span>
										</label>
									</td>
										
								</tr>
							
							<% count++;}%>
						   <%if(committeeinvitedlist.size()>1	){ %>
							<tr>
						    <td> <button type="submit" class="btn btn-sm edit" onclick="return slnocheck('serialnoupdate');" >Update</button></td>
							<td></td>
							<td></td>
							</tr>
							<%} %>
							</tbody>
				    
			             </table>
			             <%if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {%>
							<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
							<input type="hidden" name="ccmScheduleId" value="<%=committeescheduleid %>">
							<input type="hidden" name="committeeMainId" value="<%=committeeMainId %>">
							<input type="hidden" name="committeeId" value="<%=committeeId %>">
							<input type="hidden" name="ccmFlag" value="<%=ccmFlag %>">
						<%}%>	
			           </form>								

					 </div> 
		    	</div> 
		       
				<div align="center">
					<%if(ccmFlag!=null && ccmFlag.equalsIgnoreCase("Y")) {%>
          				<form method="post" action="CCMSchedule.htm" id="backfrm1" >
							<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
							<input type="hidden" name="ccmScheduleId" value="<%=committeescheduleid %>">
							<input type="hidden" name="committeeMainId" value="<%=committeeMainId %>">
							<input type="hidden" name="committeeId" value="<%=committeeId %>">
							<button class="btn btn-info btn-sm  shadow-nohover back" >Back</button>
						</form> 
	          				
	          		<%} else{%>
		          		<form method="post" action="CommitteeScheduleView.htm">
							<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
							<input type="hidden" name="scheduleid" value="<%=committeescheduleid %>">
							<!-- <button type="button" class="btn btn-sm add" id="addrep" onclick="showaddladd();">Add Additional Members</button>
							<button type="button" class="btn btn-sm add" id="addrep" onclick="showrepadd();">Add Representative</button> -->
							<button class="btn btn-info btn-sm  shadow-nohover back" >Back</button>
						</form>		
	          		<%} %>	
			      								
				 </div>
			
<!------------------------------------------------------------------------------------------------------------------------------------------------ -->	
			
				<div class=row>
		       		<div class="col-md-12 addMembersDisplay" id="addmemtitleid">
		       			<h5 class="addMembersColor">Add Additional Members</h5>
		       			<hr> 
		          	</div>
		          	<div class="col-md-12 addMembersDisplay" id="reptitleid">
		       			<h5 class="addMembersColor">Add Representative Members</h5>
		       			<hr> 
		          	</div>
		          	
		     	</div>			
					
<!-- --------------------------------internal add ----------------------------------------------- -->
			<div id="additionalmemadd" class="additionalCollapse"> 
				<div class="row" id="repselect">						
					<div class="col-md-6">	 
						
						<table class="mt-10 w-100">
							<tr >			
									
								<td class="w-100">	
									<label>Representative Type</label>										
									<select class="form-control selectdee " name="reptype" id="reptype"  data-live-search="true" onchange="setreptype();" >
											<option selected value="0"  > Choose... </option>
										<% for (Object[] obj : committeereplist) {%>					
											<option value="<%=obj[2]%>"> <%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - "%> </option>
										<%} %>
									</select>
								</td>
							</tr>
						</table>	
					</div>		
				</div>
				
				
				
				<form  action="CommitteeAttendanceSubmit.htm" method="POST" name="myfrm1" id="myfrm1">					
				<div class="row">						
					<div class="col-md-6">
						<table class="table  table-bordered table-hover table-striped table-condensed  info shadow-nohover mt-10 w-100" id="">
							<thead>  
								<tr id="" >
									<th> Internal Members</th>
								</tr>
							</thead>				
							<tr class="tr_clone">
								<td >								
									 <div class="input select external">
										 <select class="form-control selectdee " name="internalmember" id="internalmember"  data-live-search="true"   data-placeholder="Select Members" multiple required>
							                 <% for (Object[] obj : EmployeeList) {%>
									       		<option value="<%=obj[0]%>,I,<%=obj[3]%>"><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "%> (<%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %>) </option>
									    	<%} %>
										</select>
										<input type="hidden" name="InternalLabId" value="<%=LabCode %>" />
									</div>
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />	
 								<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid %>">
								<input type="hidden" name="rep" id="rep1" value="0" />
								</td>
							</tr>
						</table>
					</div>
					
					<div class="col-md-6 align-self-center">
					
						<button class="btn btn-primary btn-sm submit" name="submit" value="submit" type="submit"  onclick="return confirm('Are you Sure to Add these Members ?');">SUBMIT</button>
							
					</div>
				
			</div>
			</form>
	<!-- --------------------------------internal add ----------------------------------------------- -->
	
	<!-- --------------------------------External Members (Within DRDO)----------------------------------------------- -->
			<form  action="CommitteeAttendanceSubmit.htm" method="POST" name="myfrm1" id="myfrm1">
			<div class="row">	
				
				<div class="col-md-6">
					
					<table class="table  table-bordered table-hover table-striped table-condensed  info shadow-nohover mt-10" id="table1">
						<thead>  
							<tr id="">
								<th colspan="2"> External Members (Within DRDO)</th>
							</tr>
						</thead>
						<tr>
							<td class="width-30">							
								<div class="input select">
									<select class="form-control selectdee" name="LabId" tabindex="-1"   id="LabCode" onchange="employeename()" required>
										<option disabled="true"  selected value="">Lab Name</option>
											<% for (Object[] obj : clusterlablist) {
											if(!LabCode.equals(obj[3].toString())){%>
												<option value="<%=obj[3]%>"><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - "%></option>
											<%} 
											}%>
									</select>
								</div>
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />	
 								<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid %>">
								<input type="hidden" name="rep" id="rep2" value="0" />
							</td>
							<td>
								<div class="input select ">
									<select class="form-control selectdee" name="ExternalMemberLab" id="ExternalMemberLab" data-live-search="true"   data-placeholder="Select Members" multiple>
									</select>
								</div>
							</td>						
						</tr>
					</table>
				
				</div>
				
				<div class="col-md-6 align-self-center">
					
						<button class="btn btn-primary btn-sm submit" name="submit" value="submit" type="submit"  onclick="return confirm('Are you Sure to Add these Members ?');">SUBMIT</button>
							
				</div>
				
			</div>
			</form>
	<!-- --------------------------------External Members (Within DRDO)----------------------------------------------- -->
	<!-- --------------------------------External Members (Outside DRDO)----------------------------------------------- -->

			
			<form  action="CommitteeAttendanceSubmit.htm" method="POST" name="myfrm1" id="myfrm1">
			<div class="row">					
				
				<div class="col-md-6">
					
						<table class="table  table-bordered table-hover table-striped table-condensed  info shadow-nohover mt-10 w-100" id="">
						<thead>  
							<tr id="">
								<th> External Members (Outside DRDO)</th>
							</tr>
						</thead>
						<tr class="tr_clone2">
							<td >
							
								<div class="input select external">
									<select  class= "form-control selectdee" name="externalmember" id="expertmember"   data-live-search="true"   data-placeholder="Select Members" multiple required>
										<% for (Object[] obj : ExpertList) {%>
									       	<option value="<%=obj[0]%>,E,<%=obj[3]%>"><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "%> (<%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %>) </option>
									    <%} %>
									</select>
									<input type="hidden" name="LabId1" value="@EXP" />
									<input type="hidden" name="rep" id="rep3" value="0" />
								</div>
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />	
 								<input type="hidden" name="committeescheduleid" value="<%=committeescheduleid %>">
						</tr>
					</table>					
				</div>
				
				<div class="col-md-6 align-self-center">
						
					<button class="btn btn-primary btn-sm submit" name="submit" value="submit" type="submit"  onclick="return confirm('Are you Sure to Add these Members ?');">SUBMIT</button>
								
				</div>
				
			</div>	
			</form>
	<!-- --------------------------------External Members (Outside DRDO)----------------------------------------------- -->
	</div>	       
	
<!------------------------------------------------------------------------------------------------------------------------------------------------ -->	
		        		
		    <% }else {%>
		       					
				<div align="center">
					<h5>Not Invited Yet ...!!</h5><br>									
				</div>
			<%} %> 
				 </div>
				 	 
		    </div> <!-- card end -->
		    
		</div>
	</div>
</div>




	 
				 <script type="text/javascript">
					function showrepadd()
					{
						document.getElementById('addmemtitleid').style.display = 'none';
						document.getElementById('reptitleid').style.display = 'block';	
						
						$('#internalmember').val('').trigger("change");
						$('#ExternalMemberLab').val('').trigger("change");
						$('#expertmember').val('').trigger("change");
						$('#LabCode').val('').trigger("change");
						
						document.getElementById('additionalmemadd').style.visibility = 'visible';
						document.getElementById('repselect').style.visibility = 'visible';
					}
					
					function showaddladd()
					{
						document.getElementById('reptitleid').style.display = 'none';
						document.getElementById('addmemtitleid').style.display = 'block';
						
						$('#internalmember').val('').trigger("change");
						$('#ExternalMemberLab').val('').trigger("change");
						$('#expertmember').val('').trigger("change");
						$('#LabCode').val('').trigger("change");
						
						document.getElementById('additionalmemadd').style.visibility = 'visible';
						$("#reptype").val("0").change();						
						if($('#repselect').css('visibility')==='visible')
						{
							document.getElementById('repselect').style.visibility = 'collapse';
						} 
						 
					}
				</script>
		       
		       
		       
<script type="text/javascript">
					
		function setreptype()
		{
			reptype=$('#reptype').val();
			$('#rep1').val(reptype);
			$('#rep2').val(reptype);
			$('#rep3').val(reptype);
		}
			
					
</script>
<script type="text/javascript">

	
	
	 function slnocheck(formid) {
		
		 var arr = document.getElementsByName("newslno");
	
		var arr1 = [];
		for (var i=0;i<arr.length;i++){
			arr1.push(arr[i].value);
		}		 
		 
	     let result = false;
	   
	     const s = new Set(arr1);
	     
	     if(arr.length !== s.size){
	        result = true;
	     }
	     if(result) {
	    	event.preventDefault();
	        alert('Serial No contains duplicate Values');
	        return false;
	     } else {
	    	 return confirm('Are You Sure to Update?');
	     }
	   }
	
</script>
  

	
<script type="text/javascript">


function FormNameEdit(id){
   		 $.ajax({

				type : "GET",
				url : "CommitteeAttendanceToggle.htm",
				data : {
							invitationid : id
					   },
				datatype : 'json',
				success : function(result) {

				var result = JSON.parse(result);
		
				var values = Object.keys(result).map(function(e) {
			 				 return result[e]
			  
								});
					}
					   
				});
   	 
}



function OnlineFormNameEdit(id, checkbox) {
    // This will give you true or false
    var isChecked = checkbox.checked;
    var isOnlineAttendenc = isChecked?"Y":"N";
    console.log("Is Checked: " +  isOnlineAttendenc);
    $.ajax({

		type : "GET",
		url : "CommitteeOnlineAttendanceToggle.htm",
		data : {
					invitationid : id,
					isOnlineAttendenc:isOnlineAttendenc
			   },
		datatype : 'json',
		success : function(result) {

		var result = JSON.parse(result);

		var values = Object.keys(result).map(function(e) {
	 				 return result[e]
	  
						});
			}
			   
		});
}

function employeename(){

	$('#ExternalMemberLab').val("");
	
		var $LabCode = $('#LabCode').val();
	
		
				if($LabCode!=""){
		
							$
								.ajax({

								type : "GET",
								url : "ExternalEmployeeListInvitations.htm",
								data : {
											LabCode : $LabCode,
											scheduleid : '<%=committeescheduleid %>' 	
									   },
								datatype : 'json',
								success : function(result) {

								var result = JSON.parse(result);
						
								var values = Object.keys(result).map(function(e) {
							 				 return result[e]
							  
												});
						
						var s = '';
						s += '<option value="">'
							+"--Select--"+ '</option>';
						 for (i = 0; i < values.length; i++) {
							
							s += '<option value="'+values[i][0]+",W,"+values[i][4]+'">'
									+values[i][1].replaceAll("<","").replaceAll(">","") + " (" +values[i][3].replaceAll("<","").replaceAll(">","")+")" 
									+ '</option>';
						} 
						 
						$('#ExternalMemberLab').html(s);
											
					}
				});

		}
	}

function getRoles() {
    // Get all input elements with the name attribute "Role"
    var  inputs = document.querySelectorAll('input[name="Role"]');
    
    // Retrieve the IDs of these input elements
    var ids = Array.from(inputs).map(input => input.id);
    

    var EmpNo=[];
    var LabCode = [];
    var EmpRoles = [];
    
    for(var i=0;i<ids.length;i++){
    	
    	var EmpNos = $('#EmpNo'+ids[i]).val();
    	EmpNo.push(EmpNos)
    	
    }
    for(var i=0;i<ids.length;i++){
    	
    	var LabCodes = $('#LabCode'+ids[i]).val();
    	LabCode.push(LabCodes)
    	
    }
   for(var i=0;i<ids.length;i++){
    	
    	var EmpRole = $('#'+ids[i]).val();
    	EmpRoles.push(EmpRole)
    	
    }
 
    

    console.log(ids);
    console.log(EmpNo);
    console.log(LabCode);
    console.log(EmpRoles);
}


</script>


</body>
</html>