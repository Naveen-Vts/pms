<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat"%>
    <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<%-- <jsp:include page="../static/sidebar.jsp"></jsp:include> --%>
<title>GROUP MASTER EDIT</title>

<spring:url value="/resources/css/master/groupMasterEdit.css" var="groupMasterEdit" />     
<link href="${groupMasterEdit}" rel="stylesheet" />

</head>
<body>

<%


List<Object[]> groupheadlist=(List<Object[]>)request.getAttribute("groupheadlist");
List<Object[]> tdaddlist=(List<Object[]>)request.getAttribute("tdaddlist");
Object[] groupsdata=(Object[])request.getAttribute("groupsdata");
List<Object[]> lablist = (List<Object[]>)request.getAttribute("lablist");


%>

<%String ses=(String)request.getParameter("result"); 
 String ses1=(String)request.getParameter("resultfail");
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

<div id="ajaxError" >
    <div align="center">
        <div class="alert-danger" id="ajaxErrorMessage" >
            <!-- Error message will appear here -->
        </div>
    </div>
</div>
	
<br>	
	
<div class="container-fluid">		
<div class="row"> 


<div class="col-sm-2"></div> 
	
 <div class="col-sm-8 coldiv"  >
<div class="card shadow-nohover"  >
<div class="card-header headDiv" >
                    <b class="text-white style1">Group Master Edit</b>
        		</div>
<div class="card-body">


<form name="myfrm" action="GroupMasterEditSubmit.htm" id="editCheck" method="POST" >

 <div class="row"> 
                		
                    		<div class="col-md-3">
                        		<div class="form-group">
                            		<label class="control-label">Group Code</label><span class="mandatory">*</span>
                              		<input  class="form-control form-control "  type="text" name="groupcode" readonly="readonly" id="groupCode" value="<%=groupsdata[1]!=null? StringEscapeUtils.escapeHtml4(groupsdata[1].toString()):""%>" required="required" maxlength="3" > 
                        		</div>
                    		</div>
         					<div class="col-md-3">
                        		<div class="form-group">
                            		<label class="control-label">Group Name</label><span class="mandatory">*</span>
                            		<input  class="form-control form-control " value="<%=groupsdata[2]!=null? StringEscapeUtils.escapeHtml4(groupsdata[2].toString()):"" %>"  type="text" name="groupname" id="groupName" required="required" maxlength="100"  > 
                        		</div>
                    		</div>
                    		<div class="col-md-2">
                        		<div class="form-group">
                            		<label class="control-label">Lab Code</label>
                              		<select class="custom-select selectdee" name="labCode" onChange="showEmployees(this.value)">			
                              		<option value="" selected="selected" disabled="disabled">Select Labcode</option>														
											<% for (  Object[] obj : lablist){ %>
											<option value=<%=obj[2]%> <%if(obj[2].toString().equalsIgnoreCase(groupsdata[8].toString())) {%> selected="selected" <%} %>><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()):"-"%></option>
											<%} %>
									</select> 
                        		</div>
                    		</div>
                    		<div class="col-md-4">
                        		<div class="form-group">
                            		<label class="control-label">Group Head Name</label><span class="mandatory">*</span>
                              		<select class="custom-select" name="ghempid"  required="required" id ="ghempid" >
											<option disabled="disabled" selected value="">---Select---</option>
											 <% for (  Object[] obj : groupheadlist){ %>									
											<option value=<%=obj[0]%> <%if(obj[0].toString().equalsIgnoreCase(groupsdata[3].toString())) {%> selected="selected" <%} %>><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()):"-"%>, <%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()):"-"%> </option>
											<%} %> 
									</select>
                        		</div>
                    		</div>
                    		<div class="col-md-3">
                        		<div class="form-group">
                            		<label class="control-label">TD Name</label><span class="mandatory">*</span>
                              		<select class="custom-select" name="tdId"  required="required" id ="tdId" >
													<option disabled="true"  selected value="">---Select---</option>
													
													<% for (  Object[] obj : tdaddlist){ %>
											
											        	<option value=<%=obj[0]%> <%if(obj[0].toString().equalsIgnoreCase(groupsdata[7].toString())) {%> selected="selected" <%} %>><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()):"-"%> (<%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()):"-"%>)</option>
												
													<%} %>
									</select>
                        		</div>
                    		</div>
                    			<div class="col-md-2">
                        		<div class="form-group">
                            		<label class="control-label">isActive:</label><span class="mandatory">*</span>
                              		 
								<select  class="custom-select isActiveSel"  name="isActive" required="required" maxlength="255"  >
                       
                       
                     <option value="1" <%if(groupsdata[6].toString().equalsIgnoreCase("1")) {%> selected="selected" <%} %> >YES</option>
                       
					 <option value="0" <%if(groupsdata[6].toString().equalsIgnoreCase("0")) {%> selected="selected" <%} %> >NO</option>
                      
    </select> 
                        		</div>
                    		</div>
                    		        		
                        </div>    
    

	<div align="center">
		<button type="submit" class="btn btn-sm submit SubmitBtn"  onclick="return confirm('Are you Sure To Submit ?');" >SUBMIT</button>&nbsp;&nbsp;
		<a class="btn  btn-sm  back"    href="GroupMaster.htm">BACK</a>
	</div>

	<input type="hidden" name="groupid"  value="<%=groupsdata[0] %>" />
	<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}"  />
	
	
</form>
	
	
	  </div>
	  </div>
	  </div>
	 
	 <div class="col-sm-2"></div> 
	 
	  </div>
	
</div>	
	
</body>
<script>
  $(document).ready(function(){
	  $('#ghempid').select2();
	  $('#tdId').select2();
  });
  
  $(document).ready(function() {
	    $('select[name="isActive"]').change(function() {
	        var isActive = $(this).val();
	        var groupCode = $('#groupCode').val();
	        var groupId = $('input[name="groupid"]').val(); // Get the hidden groupid
	        
	        $('#ajaxError').hide();
	        $('#ajaxErrorMessage').text('');
	        
	        if(isActive === "0") {
	            $.ajax({
	                url: 'GroupMasterEditSubmitCheck.htm',
	                type: 'POST',
	                data: {
	                    isActive: isActive,
	                    groupcode: groupCode,  
	                    groupid: groupId,     
	                    '${_csrf.parameterName}': '${_csrf.token}'
	                },
	                success: function(response) {
	                    if(response.valid) {
	                        console.log("Validation passed");
	                    } else {
	                        $('#ajaxErrorMessage').text(response.message);
	                        $('#ajaxError').show();
	                        $('select[name="isActive"]').val("1");
	                    }
	                },
	                error: function(xhr) {
	                    $('#ajaxErrorMessage').text("Error validating status");
	                    $('#ajaxError').show();
	                    $('select[name="isActive"]').val("1");
	                }
	            });
	        }
	    });
	});
  

  function showEmployees(labcode){
  	if(labcode !=""){
  		$.ajax({		
  		type : "GET",
  		url : "EmployeeOnLabCode.htm",
  		data : {
  			labcode : labcode,
  		},
  		datatype : 'json',
  		success : function(result) {
  	
  			var result = JSON.parse(result);	
  			var values = Object.keys(result).map(function(e) {
  									 return result[e]
  						});
  							
  			var s = '<option value="" selected disabled>---Select---</option>';
  			for (i = 0; i < values.length; i++) {									
  				s += '<option value="'+values[i][0]+'">'
  					+values[i][1].replaceAll("<","").replaceAll(">","") + " (" +values[i][2].replaceAll("<","").replaceAll(">","")+")" 
  					+ '</option>';
  			} 
  			$('#ghempid').html(s);
  		}
  		});
  	}
  }
</script>
</html>