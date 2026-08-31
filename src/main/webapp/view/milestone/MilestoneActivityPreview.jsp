<%@page import="com.fasterxml.jackson.databind.ObjectMapper"%>
<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat"%>
    <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Milestone Activity Preview</title>
<jsp:include page="../static/header.jsp"></jsp:include>
 <spring:url value="/resources/css/milestone/milestoneActivityPreview.css" var="milestoneActivityPreview" />     
<link href="${milestoneActivityPreview}" rel="stylesheet" />
<style>
.mape-highlight {
	background-color: #fff3b0 !important;
	transition: background-color 1.2s ease;
}
</style>


</head>
<%
SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
Object[] getMA=(Object[])request.getAttribute("MilestoneActivity");
int RevisionCount=(Integer) request.getAttribute("RevisionCount");
List<Object[]> ActivityTypeList=(List<Object[]>)request.getAttribute("ActivityTypeList");
List<Object[]> MilestoneActivityA=(List<Object[]>)request.getAttribute("MilestoneActivityA");
Long EmpId =  (Long)session.getAttribute("EmpId") ;
String LoginType = (String)session.getAttribute("LoginType");
String projectDirector = (String)request.getAttribute("projectDirector");
List<String> changes = new ArrayList<>();
List<Object[]> allLabList=(List<Object[]>)request.getAttribute("allLabList");
String chainId = (String) request.getAttribute("chainId");
if(chainId != null && !chainId.isBlank()){
	chainId = chainId.replace("'", "\"");
}
String targetRowId = (String) request.getAttribute("targetRowId");
%>

<script type="text/javascript">
function changeempoic1(id,id3)
{
	var it='allempcheckbox1'+id3;
	var it2='EmpId'+id3;
  if (document.getElementById(it).checked) 
  {
    employeefetch(0,it2,id);
  } else {
	  employeefetch(<%=getMA[10] %>,it2,id);
  }
}


function changeempoic2(id1,id2)
{
	var it1='allempcheckbox2'+id2;
	var it12='EmpId1'+id2;
  if (document.getElementById(it1).checked) 
  {
    employeefetch(0,it12,id1);
  } else {
	  employeefetch(<%=getMA[10] %>,it12,id1);
  }
}	    
	  
function employeefetch(ProID,dropdownid,empid){
	
	
	$.ajax({		
		type : "GET",
		url : "ProjectEmpListEdit.htm",
		data : {
			projectid : ProID,
			EmpId:empid
			   },
		datatype : 'json',
		success : function(result) {

		var result = JSON.parse(result);
			
		var values = Object.keys(result).map(function(e) {
					 return result[e]
				  
		});
			
var s = '';
	s += '<option value="">'+"--Select--"+ '</option>';
			 for (i = 0; i < values.length; i++) {									
				s += '<option value="'+values[i][0]+'">'
						+values[i][1] + ", " +values[i][2]
						+ '</option>';
			} 
			 
			$('#'+dropdownid).html(s);
			$("#"+dropdownid).val(empid).change();
		}
	});


}  
</script>	
<script type="text/javascript">
	function renderEmployeeList(rowId, level, empid) {
		var labCode  = $('#labCode'+rowId+level).val();
		var currLabCode  = $('#currLabCode').val();
		
		/* console.log('rowId', rowId);
		console.log('level', level);
		console.log('empid', empid);
		console.log('labCode', labCode);
		console.log('******************************'); */
		employeeListByLabCode(rowId, level, labCode, empid);
		
		if(currLabCode!=labCode) {
			$('#allempcheckbox'+rowId+level).hide();
		}else {
			$('#allempcheckbox'+rowId+level).show();
			$('#allempcheckbox'+rowId+level).prop('checked', true);
		}
	}
	
	function employeeListByLabCode(rowId, level, labcode, empid) {
	
		var rowIdShort = rowId==1?"":(rowId-1);
		$('#EmpId'+rowIdShort+level).empty(); 
		$.ajax({
		       type: "GET",
		       url: "GetLabcodeEmpList.htm",
		       data: {
		       	LabCode: labcode
		       },
		       dataType: 'json',
		       success: function(result) {
		    	   if (result != null) {
		    		   $('#EmpId'+rowIdShort+level).append('<option disabled="disabled" selected value="">Choose...</option>');
		                for (var i = 0; i < result.length; i++) {
		                    var data = result[i];
		                    var optionValue = data[0];
		                    var optionText = data[1].trim() + ", " + data[3]; 
		                    var option = $("<option></option>").attr("value", optionValue).text(optionText);
		                    $('#EmpId'+rowIdShort+level).append(option); 
		                }
		                //$('#EmpId'+(rowId==1?"":rowId)).select2('refresh');
		                $('#EmpId'+rowIdShort+level).val(empid==0?"":empid).trigger('change'); 
		           }
		       }
		});
	}
	
	function toggleChildren(childDivId, btnElement) {
	    var childDiv = document.getElementById(childDivId);
	    if (childDiv.style.display === "none" || childDiv.style.display === "") {
	        childDiv.style.display = "block";
	        btnElement.innerHTML = '<i class="fa fa-minus" aria-hidden="true"></i>'; 
	    } else {
	        childDiv.style.display = "none";
	        btnElement.innerHTML = '<i class="fa fa-plus" aria-hidden="true"></i>'; 
	    }
	}

	// Remembers which nested branch (chain of ancestor ids) and which exact
	// row is being edited, right before the page does a full POST + reload,
	// so that after reload we can reopen that branch automatically.
	function rememberReopenState(chain, targetRowId) {
		try {
			sessionStorage.setItem('mape_reopenChain', JSON.stringify(chain || []));
			sessionStorage.setItem('mape_reopenTarget', targetRowId || '');
		} catch (e) {
			// sessionStorage unavailable - silently skip, page still works, it just won't auto-reopen
		}
	}

	// Runs once on page load. If a branch was remembered before the last
	// submit, expand every ancestor in that chain (so nested items become
	// visible) and scroll/highlight the exact row that was updated.
	function restoreReopenState() {
		var raw;
		try {
			raw = '<%= chainId %>';
			// raw = sessionStorage.getItem('mape_reopenChain');
		} catch (e) {
			return;
		}
		if (raw === null) {
			return;
		}
		// var target = sessionStorage.getItem('mape_reopenTarget');
		var target = '<%= targetRowId %>';
		try {
			var chain = JSON.parse(raw);
			if(chain){
				chain.forEach(function(item) {
					var div = document.getElementById('children_' + item);
					var btn = document.getElementById('btn_' + item);
					if (div) { div.style.display = 'block'; }
					if (btn) { btn.innerHTML = '<i class="fa fa-minus" aria-hidden="true"></i>'; }
				});
			}
		} catch (e) {
			console.log('restoreReopenState error', e);
		}
		// sessionStorage.removeItem('mape_reopenChain');
		// sessionStorage.removeItem('mape_reopenTarget');
		if (target) {
			setTimeout(function() {
				var el = document.getElementById(target);
				if (el) {
					el.scrollIntoView({ behavior: 'smooth', block: 'center' });
					/* el.classList.add('mape-highlight');
					setTimeout(function() { el.classList.remove('mape-highlight'); }, 2500); */
				}
			}, 300);
		}
	}

	$(document).ready(function() {
		restoreReopenState();
	});
</script>
	
<body>

  <nav class="navbar navbar-light bg-light" >
  	<div class="row text-danger m-3 fw600" > 
Kindly note that only the Project Director, the Admin, and the OICs of the Parent Milestone are authorized to Edit and Delete milestones.
</div>
  <a class="navbar-brand"></a>
  <form class="form-inline"  method="POST" action="MilestoneActivityList.htm">
    <%if( Arrays.asList(getMA[8].toString(),projectDirector,getMA[9].	toString()).contains(EmpId.toString()) || LoginType.equalsIgnoreCase("A")  ){ %>
   <%if(getMA[13]!=null){ %>
    <input type="submit" class="btn btn-primary btn-sm submit " id="baseLineBtn"  value="Set Base Line ( <%=RevisionCount %> )" onclick="return confirm('Are You Sure To Submit ?')" formaction="M-A-Set-BaseLine.htm" > 
  
  <%} %>
  <%} %>
  <%if(RevisionCount>0){ %>
  <input type="submit" class="btn btn-primary btn-sm preview ml-1"  value="Compare"  formaction="MilestoneActivityCompare.htm"> 		
  <%} %>
  <input type="submit" class="btn btn-primary btn-sm back ml-1"  value="Back" > 	
		     <input type="hidden" name="projectDirector" value ="<%=projectDirector%>">
      <input type="hidden" name="RevId"	value="<%=RevisionCount %>" /> 
      <input type="hidden" name="ProjectId"	value="<%=getMA[10] %>" /> 
      <input type="hidden" name="MilestoneActivityId"	value="<%=getMA[0] %>" /> 

<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
</form>
</nav>

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

    <br />


<div class="container-fluid">
<div class="row" >
<div class="col-md-12">
<div  class="panel-group" ><h5  class="mp-1"><%=getMA[1]!=null?StringEscapeUtils.escapeHtml4(getMA[1].toString()): " - " %> Milestone Activity Details  </h5>  
<form   method="POST" action="MilestoneActivityEditSubmit.htm" id="form<%=getMA[0] %>M<%=getMA[10] %>">
<div class="row container-fluid" id="row_M">
                             <div class="col-md-1 " ><br><label class="control-label">Type</label>  <br>  <b >Main</b>                    		
                        	</div>
                    		<div class="col-md-5 " ><br>
                    		<label class="control-label"> Activity Name:</label> <br> 
                    		 <textarea rows="1" cols="50" class="form-control mp2"  <%if(RevisionCount>0){ %>  <%} %> name="ActivityName" id="ActivityName"    maxlength="1000" required="required"><%=getMA[4]!=null?getMA[4].toString(): "" %></textarea> 
                        	</div>
                        	
                        	<div class="col-md-1 " align="center"><br>
                        	<label class="control-label">From:</label><br>
                        	<input class="form-control width120" name="ValidFrom" id="DateCompletion"  value="<%=sdf.format(getMA[2]) %>"  required="required"  >
                        	
                        	</div>
                        	<div class="col-md-1 " align="center"><br>
                        		<label class="control-label">To:</label><br>
                        	<input class="form-control form-control width120" name="ValidTo" id="DateCompletion2"  value="<%=sdf.format(getMA[3]) %>"  required="required" >
                        		</div>
                        		<div class="col-md-1 " align="center" ><br>
                    		<label class="control-label">Weightage <br> </label>
                    		<input type="number" class="form-control width95" name="Weightage" id="Weightage<%=getMA[0] %>M<%=getMA[10] %>" required="required" min="1" max="100" value="<%=getMA[16]!=null?StringEscapeUtils.escapeHtml4(getMA[16].toString()): "" %>"  >

                    		 
                        	</div>
                        	<div class="col-md-2 " ><br>
	                        	<%if(RevisionCount==0) { %>
	                    		<label class="control-label">Activity Type  </label>
	                              		<select class="form-control selectdee" id="ActivityTypeIdM" required="required" name="ActivityTypeId">
	    									<option disabled selected value="">Choose...</option>
	    										<% for (Object[] obj : ActivityTypeList) {%>
											<option value="<%=obj[0]%>" <%if(getMA[15].toString().equalsIgnoreCase(obj[0].toString())){ %> selected="selected" <% }%>><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "%> </option>
												<%} %>
	  									</select>
	                        	<%}%>
                        	</div>
                        	<div class="col-md-1 " ><br><label class="control-label"> &nbsp;&nbsp;Actions<br></label><br>
                        				<%if( Arrays.asList(projectDirector).contains(EmpId.toString()) || LoginType.equalsIgnoreCase("A")  ){ %>
                        	  <button type="button" class="btn btn-sm edit" onclick="weightage_sum('<%=getMA[0] %>','<%=getMA[10] %>','M',undefined,[],'row_M');" >
                        	  <i class="fa fa-edit" aria-hidden="true"></i>
                        	  </button>
                        	 <input type="submit" hidden="hidden" id="<%=getMA[0] %>M<%=getMA[10] %>sub"/> 
                        	  
	                              <input type="hidden" name="RevId"	value="<%=RevisionCount %>" /> 
	                              <input type="hidden" name="MilestoneActivityId"	value="<%=getMA[0] %>" /> 
	                              <input type="hidden" name="ActivityId"	value="<%=getMA[0] %>" /> 
	                              <input type="hidden" name="ActivityType"	value="M" /> 
	                              <input type="hidden" name="projectDirector"	value="<%=projectDirector %>" /> 
	                              <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
	                              <input type="hidden" name="chainId" value="[]" />
	                              <input type="hidden" name="targetRowId" value="row_M" />


                        	  <%-- 
	                        	  <button type="button" class="btn btn-sm delete" onclick="deletSubMilestones('<%=getMA[0] %>','<%=getMA[10] %>','M',undefined,[],'row_M');" >
	                        	  	<i class="fa fa-trash" aria-hidden="true"></i>
	                        	  </button>
                             
	                               --%>
                             <%} %>
                        	</div>
                       		</div>
                       		
                       		<div class="row container-fluid" >
                        	<div class="col-md-1"><br></div>
                        	<div class="col-md-2"><br>
                        		<label  >Lab: <span class="mandatory" >*</span></label><br>
                        		<select class="form-control selectdee" name="labCode1" id="labCode1M" required 
								onchange="renderEmployeeList('1','M','0')" data-placeholder= "Lab Name">
								    <% for (Object[] lab : allLabList) { %>
								    	<option value="<%=lab[3]%>" <%if(getMA[18].toString().equalsIgnoreCase(lab[3].toString())) {%>selected<%} %> ><%=lab[3]!=null?StringEscapeUtils.escapeHtml4(lab[3].toString()): " - "%></option>
								    <%}%>
								</select>
                        	</div>
                        	<div class="col-md-3 " align="center"><br>
                        	<label class="control-label">First OIC  </label>
                        	<%-- <div style="float: right;"  > <label>All &nbsp; : &nbsp;&nbsp;</label>
										<input type="checkbox" style="float: right; margin-top : 6px;" id="allempcheckbox1M" onchange="changeempoic1('<%=getMA[8]%>','M')" >
									</div> --%>
                              		<select class="form-control selectdee" id="EmpIdM" required="required" name="EmpId">
    									
											
  									</select>
                        	</div>
                        	<div class="col-md-2"><br>
                        		<label  >Lab: <span class="mandatory"  >*</span></label><br>
                        		<select class="form-control selectdee" name="labCode2" id="labCode2M" required 
								onchange="renderEmployeeList('2','M','0')" data-placeholder= "Lab Name">
								    <% for (Object[] lab : allLabList) { %>
								    	<option value="<%=lab[3]%>" <%if(getMA[19].toString().equalsIgnoreCase(lab[3].toString())) {%>selected<%} %> ><%=lab[3]!=null?StringEscapeUtils.escapeHtml4(lab[3].toString()): " - "%></option>
								    <%}%>
								</select>
                        	</div>
                        	<div class="col-md-3 " align="center"><br>
                        		<label class="control-label">Second OIC </label>
                        		<%-- <div style="float: right;"  > <label>All &nbsp; : &nbsp;&nbsp;</label>
										<input type="checkbox" style="float: right; margin-top : 6px;" id="allempcheckbox2M" onchange="changeempoic2('<%=getMA[9]%>','M')" >
									</div> --%>
                              		<select class="form-control selectdee" id="EmpId1M" required="required" name="EmpId1">
    									
  									</select>
  										</div>
                        
                       		</div>
                       		
                       		
                       		
                       		
<script type="text/javascript">

renderEmployeeList('1','M', '<%=getMA[8]!=null?StringEscapeUtils.escapeHtml4(getMA[8].toString()): " - "%>');
renderEmployeeList('2','M', '<%=getMA[9]!=null?StringEscapeUtils.escapeHtml4(getMA[9].toString()): " - "%>');
		
</script>
   
   
                       		
                       		 </form>
                       		 <script type="text/javascript">




	

		
</script>
                  	
</div>

</div>
<div class="col-md-12">
<%

changes.add(getMA[20]!=null? getMA[20].toString():"0");

if(MilestoneActivityA!=null&&MilestoneActivityA.size()>0){
	int countA=1;
	for(Object[] ActivityA:MilestoneActivityA){
		List<Object[]> MilestoneActivityB=(List<Object[]>)request.getAttribute("MilestoneActivityB"+countA);

		changes.add(ActivityA[26].toString());
%>


	
		


				<form   method="POST" action="MilestoneActivityEditSubmit.htm" id="form<%=getMA[0] %>A<%=ActivityA[0] %>">
					
						<div class="row container-fluid" id="row_A_<%=ActivityA[0]%>">
						    <%-- <div class="col-md-1 " ><label class="control-label ml-1" ></label><br> <b class="ml-1">A-<%=countA %></b><br>
                    		
                        	</div> --%>
                        	<div class="col-md-1 " ><label class="control-label ml-1" ></label><br> <b class="ml-1">A-<%=countA %></b><br>
							    <% if(MilestoneActivityB != null && MilestoneActivityB.size() > 0) { %>
							         <button type="button" id="btn_A_<%=ActivityA[0]%>" class="btn btn-sm btn-primary py-0 px-2 mt-1" onclick="toggleChildren('children_A_<%=ActivityA[0]%>', this)"><i class="fa fa-plus" aria-hidden="true"></i></button>
							    <% } %>
							</div>
						  <div class="col-md-5 " ><br>
                    		 <textarea rows="1" cols="50" class="form-control mp2" <%if(RevisionCount>0){ %>  <%} %> name="ActivityName" id="ActivityName"    maxlength="1000" required="required"><%=ActivityA[4]!=null?ActivityA[4].toString(): " - " %></textarea> 
                        	</div>
                        	
                        	<div class="col-md-1 " align="center"><br>
                        	<input class="form-control width120" name="ValidFrom" id="DateCompletionA<%=ActivityA[0] %>"  value="<%=sdf.format(ActivityA[2]) %>"  required="required"  >
                        	
                        	</div>
                        	<div class="col-md-1 " align="center"><br>
                        	<input class="form-control width120" name="ValidTo" id="DateCompletionA2<%=ActivityA[0] %>"  value="<%=sdf.format(ActivityA[3]) %>"  required="required"  >
                        	</div>
                       		<div class="col-md-1 " align="center" ><br>      
                   				<input type="number" class="form-control width95"  name="Weightage" id="Weightage<%=getMA[0] %>A<%=ActivityA[0] %>" required="required" min="0" max="100" value="<%=ActivityA[6]!=null?StringEscapeUtils.escapeHtml4(ActivityA[6].toString()): "" %>" >
                       		</div>
                       		<div class="col-md-2 " ><br>
                       			<%if(RevisionCount==0) { %>
                              		<select class="form-control selectdee" id="ActivityTypeId<%=ActivityA[0] %>" required="required" name="ActivityTypeId">
    									<option disabled="true"  selected value="">Choose...</option>
    										<% for (Object[] obj : ActivityTypeList) {%>
										<option value="<%=obj[0]%>" <%if(ActivityA[11].toString().equalsIgnoreCase(obj[0].toString())){ %> selected="selected" <% }%>><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "%> </option>
											<%} %>
  									</select>
                        		<%} %>
                        	</div>
                            <div class="col-md-1 "><br>
                            <%if( Arrays.asList(getMA[8].toString(),projectDirector,getMA[9].toString() ).contains(EmpId.toString()) || LoginType.equalsIgnoreCase("A")){ %>
                        	  <button type="button"  class="btn btn-sm edit" onclick="weightage_sum('<%=getMA[0] %>','<%=ActivityA[0] %>','A','1',[],'row_A_<%=ActivityA[0]%>');"> <i class="fa fa-edit" aria-hidden="true"></i> </button>
                        	 
                        	 <%if((ActivityA[5] == null || Long.parseLong(ActivityA[5].toString()) <= 0) && (ActivityA[6] == null || Long.parseLong(ActivityA[6].toString()) <= 0)  && (ActivityA[9] == null || Long.parseLong(ActivityA[9].toString()) < 2)){ %>
	                        	  <button type="button" class="btn btn-sm delete" onclick="deletSubMilestones('<%=getMA[0] %>','<%=ActivityA[0] %>','A','1');" >
	                        	  	<i class="fa fa-trash" aria-hidden="true"></i>
	                        	  </button>
	                         <%} %>
	                         
                        	  <input type="submit" hidden="hidden" id="<%=getMA[0] %>A<%=ActivityA[0] %>sub"/> 
                              <input type="hidden" name="RevId"	value="<%=RevisionCount %>" /> 
                              <input type="hidden" name="MilestoneActivityId"	value="<%=getMA[0] %>" /> 
                              <input type="hidden" name="ActivityId"	value="<%=ActivityA[0] %>" /> 
                              <input type="hidden" name="ActivityType"	value="A" /> 
                              <input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
                              <input type="hidden" name="projectDirector" value ="<%=projectDirector%>">
                              <input type="hidden" name="chainId" value="[]" />
                              <input type="hidden" name="targetRowId" value="row_A_<%=ActivityA[0]%>" />
                              
                        <%} %>
                        	</div>
                        	</div>
                        	
                       		<div class="row container-fluid" >
                             <div class="col-md-1 " >                    		
                        	</div>
                        	
                        	<div class="col-md-2"><br>
                        		<label  >Lab: <span class="mandatory"  >*</span></label><br>
                        		<select class="form-control selectdee" name="labCode1" id="labCode1A<%=ActivityA[0] %>" required 
								onchange="renderEmployeeList('1','A<%=ActivityA[0] %>','0')" data-placeholder= "Lab Name">
								    <% for (Object[] lab : allLabList) { %>
								    	<option value="<%=lab[3]%>" <%if(ActivityA[28].toString().equalsIgnoreCase(lab[3].toString())) {%>selected<%} %> ><%=lab[3]!=null?StringEscapeUtils.escapeHtml4(lab[3].toString()): " - "%></option>
								    <%}%>
								</select>
                        	</div>
                        	<div class="col-md-3 " align="center"><br>
                        	<label class="control-label">First OIC  </label>
                        	<%-- <div style="float: right;"  > <label>All &nbsp; : &nbsp;&nbsp;</label>
										<input type="checkbox" style="float: right; margin-top : 6px;" id="allempcheckbox1<%=ActivityA[0] %>" onchange="changeempoic1('<%=ActivityA[13]%>','<%=ActivityA[0] %>')" >
									</div> --%>
                              		<select class="form-control selectdee" id="EmpIdA<%=ActivityA[0] %>" required="required" name="EmpId">
    									
											
  									</select>
                        	</div>
                        	<div class="col-md-2"><br>
                        		<label  >Lab: <span class="mandatory"  >*</span></label><br>
                        		<select class="form-control selectdee" name="labCode2" id="labCode2A<%=ActivityA[0] %>" required 
								onchange="renderEmployeeList('2','A<%=ActivityA[0] %>','0')" data-placeholder= "Lab Name">
								    <% for (Object[] lab : allLabList) { %>
								    	<option value="<%=lab[3]%>" <%if(ActivityA[29].toString().equalsIgnoreCase(lab[3].toString())) {%>selected<%} %> ><%=lab[3]!=null?StringEscapeUtils.escapeHtml4(lab[3].toString()): " - "%></option>
								    <%}%>
								</select>
                        	</div>
                        	<div class="col-md-3 " align="center"><br>
                        		<label class="control-label">Second OIC </label>
                        		<%-- <div style="float: right;"  > <label>All &nbsp; : &nbsp;&nbsp;</label>
										<input type="checkbox" style="float: right; margin-top : 6px;" id="allempcheckbox2<%=ActivityA[0] %>" onchange="changeempoic2('<%=ActivityA[15]%>','<%=ActivityA[0] %>')" >
									</div> --%>
                              		<select class="form-control selectdee" id="EmpId1A<%=ActivityA[0] %>" required="required" name="EmpId1">
    									
  									</select>
  										</div>
  										
  							</div>			
  <script type="text/javascript">

  renderEmployeeList('1','A<%=ActivityA[0]!=null?StringEscapeUtils.escapeHtml4(ActivityA[0].toString()): " - " %>', '<%=ActivityA[13]!=null?StringEscapeUtils.escapeHtml4(ActivityA[13].toString()): " - "%>');
  renderEmployeeList('2','A<%=ActivityA[0]!=null?StringEscapeUtils.escapeHtml4(ActivityA[0].toString()): " - " %>', '<%=ActivityA[15]!=null?StringEscapeUtils.escapeHtml4(ActivityA[15].toString()): " - "%>');
  
<%-- changeempoic1(<%=ActivityA[13] %>,<%=ActivityA[0] %>);
changeempoic2(<%=ActivityA[15] %>,<%=ActivityA[0] %>); --%>


</script>

  										
  										
                        
                       		
                       		
                        	
                      </form>
							<script type="text/javascript">
var from ="<%=sdf.format(getMA[2])%>".split("-")
var dt = new Date(from[2], from[1] - 1, from[0])
var to ="<%=sdf.format(getMA[3])%>".split("-")
var dt1 = new Date(to[2], to[1] - 1, to[0])
$('#DateCompletionA'+'<%=ActivityA[0] %>').daterangepicker({
	"singleDatePicker" : true,
	"linkedCalendars" : false,
	"showCustomRangeLabel" : true,
	"minDate" :dt,
	"maxDate" : dt1,
	"cancelClass" : "btn-default",
	showDropdowns : true,
	locale : {
		format : 'DD-MM-YYYY'
	}
});

var mindate=dt;
$('#DateCompletionA'+'<%=ActivityA[0] %>').on('change', function() {
    mindate=$('#DateCompletionA'+'<%=ActivityA[0] %>').val();
    $('#DateCompletionA2'+'<%=ActivityA[0] %>').prop("disabled",false);
    $('#DateCompletionA2'+'<%=ActivityA[0] %>').daterangepicker({
    	"singleDatePicker" : true,
    	"linkedCalendars" : false,
    	"showCustomRangeLabel" : true,
    	"minDate" :mindate,
    	"maxDate" : dt1,
    	"cancelClass" : "btn-default",
    	showDropdowns : true,
    	locale : {
    		format : 'DD-MM-YYYY'
    	}
    	});
  });
  
  
  
  

$( document ).ready(function() {
    mindate=$('#DateCompletionA'+'<%=ActivityA[0] %>').val();
    $('#DateCompletionA2'+'<%=ActivityA[0] %>').prop("disabled",false);
    $('#DateCompletionA2'+'<%=ActivityA[0] %>').daterangepicker({
    	"singleDatePicker" : true,
    	"linkedCalendars" : false,
    	"showCustomRangeLabel" : true,
    	"minDate" :mindate,
    	"maxDate" : dt1,
    	"cancelClass" : "btn-default",
    	showDropdowns : true,
    	locale : {
    		format : 'DD-MM-YYYY'
    	}
    	});
  });

	    
	</script>	                   
	
                      <div id="children_A_<%=ActivityA[0]%>" style="display:none; border-left: 2px dashed #ccc; margin-left: 15px;">
								
							<%


if(MilestoneActivityB!=null&&MilestoneActivityB.size()>0){
	int countB=1;
	for(Object[] ActivityB:MilestoneActivityB){
		List<Object[]> MilestoneActivityC=(List<Object[]>)request.getAttribute("MilestoneActivityC"+countA+countB);	
		changes.add(ActivityB[26].toString());
%>


	                              <form   method="POST" action="MilestoneActivityEditSubmit.htm" id="form<%=ActivityA[0] %>B<%=ActivityB[0] %>">
		
						    <div class="row container-fluid" id="row_B_<%=ActivityB[0]%>">
						     <%-- <div class="col-md-1 " ><br> <label class="control-label"></label><b class="ml-1">B-<%=countB %></b>
                    		
                        	</div> --%>
                        	<div class="col-md-1 " ><label class="control-label ml-1" ></label><br> <b class="ml-1">B-<%=countB %></b><br>
							    <% if(MilestoneActivityC != null && MilestoneActivityC.size() > 0) { %>
							         <button type="button" id="btn_B_<%=ActivityB[0]%>" class="btn btn-sm btn-primary py-0 px-2 mt-1" onclick="toggleChildren('children_B_<%=ActivityB[0]%>', this)"><i class="fa fa-plus" aria-hidden="true"></i></button>
							    <% } %>
							</div>
						    <div class="col-md-5 " ><br>
                    		 <textarea rows="1" cols="50" class="form-control mp2" <%if(RevisionCount>0){ %>  <%} %> name="ActivityName" id="ActivityName"    maxlength="1000" required="required"><%=ActivityB[4]!=null?ActivityB[4].toString(): " - " %></textarea> 
                        	</div>
                        	
                        	<div class="col-md-1 " align="center"><br>
                        	<input class="form-control width120" name="ValidFrom" id="DateCompletionB<%=ActivityA[0] %><%=ActivityB[0] %>"  value="<%=sdf.format(ActivityB[2]) %>"  required="required"   >
                        	
                        	</div>
                        	<div class="col-md-1 " align="center"><br>
                        	<input class="form-control width120" name="ValidTo" id="DateCompletionB2<%=ActivityA[0] %><%=ActivityB[0] %>"  value="<%=sdf.format(ActivityB[3]) %>"  required="required"   >
                        		</div>
                            <div class="col-md-1 " align="center" ><br>      
                    		<input type="number" class="form-control width95"  name="Weightage" id="Weightage<%=ActivityA[0] %>B<%=ActivityB[0] %>" required="required" min="0" max="100" value="<%=ActivityB[6]!=null?StringEscapeUtils.escapeHtml4(ActivityB[6].toString()): "" %>"  >
                    		</div>
                    		<div class="col-md-2 " ><br>
                    			<%if(RevisionCount==0) { %>
                              		<select class="form-control selectdee" id="ActivityTypeId<%=ActivityA[0] %><%=ActivityB[0] %>" required="required" name="ActivityTypeId">
    									<option disabled="true"  selected value="">Choose...</option>
    										<% for (Object[] obj : ActivityTypeList) {%>
										<option value="<%=obj[0]%>" <%if(ActivityB[11].toString().equalsIgnoreCase(obj[0].toString())){ %> selected="selected" <% }%>><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "%> </option>
											<%} %>
  									</select>
                    	      	
                        		<%} %>
                    		</div> 
                        	<div class="col-md-1 "><br>
                        	                            <%if( Arrays.asList(getMA[8].toString(),projectDirector,getMA[9].toString(),ActivityA[13].toString(),ActivityA[15].toString() ).contains(EmpId.toString()) || LoginType.equalsIgnoreCase("A")  ){ %>
                        	
                        	  <button type="button"  class="btn btn-sm edit" onclick="weightage_sum('<%=ActivityA[0] %>','<%=ActivityB[0] %>','B','2',['A_<%=ActivityA[0]%>'],'row_B_<%=ActivityB[0]%>');"> <i class="fa fa-edit" aria-hidden="true"></i> </button>
                        	 
                        	 <%if((ActivityB[5] == null || Long.parseLong(ActivityB[5].toString()) <= 0) && (ActivityB[6] == null || Long.parseLong(ActivityB[6].toString()) <= 0)  && (ActivityB[9] == null || Long.parseLong(ActivityB[9].toString()) < 2)){ %>
                        	  <button type="button" class="btn btn-sm delete" onclick="deletSubMilestones('<%=ActivityA[0] %>','<%=ActivityB[0] %>','B','2');" >
                        	  	<i class="fa fa-trash" aria-hidden="true"></i>
                        	  </button>
	                       	<%} %>
	                        	  
                        	  <input type="submit" hidden="hidden" id="<%=ActivityA[0] %>B<%=ActivityB[0] %>sub"/> 
                              <input type="hidden" name="RevId"	value="<%=RevisionCount %>" /> 
                              <input type="hidden" name="MilestoneActivityId"	value="<%=getMA[0] %>" /> 
                              <input type="hidden" name="ActivityId"	value="<%=ActivityB[0] %>" /> 
                              <input type="hidden" name="ActivityType"	value="B" /> 
                              <input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
                              <input type="hidden" name="projectDirector" value ="<%=projectDirector%>">
                              <input type="hidden" name="chainId" value="['A_<%=ActivityA[0]%>']" />
                              <input type="hidden" name="targetRowId" value="row_B_<%=ActivityB[0]%>" />
                                                
                                            <%} %>    
                                                  	</div>
							</div>	
							
                       		<div class="row container-fluid" >
                             <div class="col-md-1 " >                    		
                        	</div>
                        	
                        	<div class="col-md-2"><br>
                        		<label  >Lab: <span class="mandatory"  >*</span></label><br>
                        		<select class="form-control selectdee" name="labCode1" id="labCode1B<%=ActivityA[0] %><%=ActivityB[0] %>" required 
								onchange="renderEmployeeList('1','B<%=ActivityA[0] %><%=ActivityB[0] %>','0')" data-placeholder= "Lab Name">
								    <% for (Object[] lab : allLabList) { %>
								    	<option value="<%=lab[3]%>" <%if(ActivityB[28].toString().equalsIgnoreCase(lab[3].toString())) {%>selected<%} %> ><%=lab[3]!=null?StringEscapeUtils.escapeHtml4(lab[3].toString()): " - "%></option>
								    <%}%>
								</select>
                        	</div>
                       		<div class="col-md-3 " align="center"><br>
                        	<label class="control-label">First OIC  </label>
                        	<%-- <div style="float: right;"  > <label>All &nbsp; : &nbsp;&nbsp;</label>
										<input type="checkbox" style="float: right; margin-top : 6px;" id="allempcheckbox1<%=ActivityA[0] %><%=ActivityB[0] %>" onchange="changeempoic1('<%=ActivityB[13]%>','<%=ActivityA[0] %><%=ActivityB[0] %>')" >
									</div> --%>
                              		<select class="form-control selectdee" id="EmpIdB<%=ActivityA[0] %><%=ActivityB[0] %>" required="required" name="EmpId">
    									
											
  									</select>
                        	</div>
                        	<div class="col-md-2"><br>
                        		<label  >Lab: <span class="mandatory"  >*</span></label><br>
                        		<select class="form-control selectdee" name="labCode2" id="labCode2B<%=ActivityA[0] %><%=ActivityB[0] %>" required 
								onchange="renderEmployeeList('2','B<%=ActivityA[0] %><%=ActivityB[0] %>','0')" data-placeholder= "Lab Name">
								    <% for (Object[] lab : allLabList) { %>
								    	<option value="<%=lab[3]%>" <%if(ActivityB[29].toString().equalsIgnoreCase(lab[3].toString())) {%>selected<%} %> ><%=lab[3]!=null?StringEscapeUtils.escapeHtml4(lab[3].toString()): " - "%></option>
								    <%}%>
								</select>
                        	</div>
                        	<div class="col-md-3 " align="center"><br>
                        		<label class="control-label">Second OIC </label>
                        		<%-- <div style="float: right;"  > <label>All &nbsp; : &nbsp;&nbsp;</label>
										<input type="checkbox" style="float: right; margin-top : 6px;" id="allempcheckbox2<%=ActivityA[0] %><%=ActivityB[0] %>" onchange="changeempoic2('<%=ActivityB[15]%>','<%=ActivityA[0] %><%=ActivityB[0] %>')" >
									</div> --%>
                              		<select class="form-control selectdee" id="EmpId1B<%=ActivityA[0] %><%=ActivityB[0] %>" required="required" name="EmpId1">
    									
  									</select>
  										</div>
  										
  							</div>			
  <script type="text/javascript">

<%-- changeempoic1(<%=ActivityB[13] %>,<%=ActivityA[0] %><%=ActivityB[0] %>);
changeempoic2(<%=ActivityB[15] %>,<%=ActivityA[0] %><%=ActivityB[0] %>); --%>

renderEmployeeList('1','B<%=ActivityA[0]!=null?StringEscapeUtils.escapeHtml4(ActivityA[0].toString()): " - " %><%=ActivityB[0]!=null?StringEscapeUtils.escapeHtml4(ActivityB[0].toString()): " - " %>', '<%=ActivityB[13]!=null?StringEscapeUtils.escapeHtml4(ActivityB[13].toString()): " - "%>');
renderEmployeeList('2','B<%=ActivityA[0]!=null?StringEscapeUtils.escapeHtml4(ActivityA[0].toString()): " - " %><%=ActivityB[0]!=null?StringEscapeUtils.escapeHtml4(ActivityB[0].toString()): " - " %>', '<%=ActivityB[15]!=null?StringEscapeUtils.escapeHtml4(ActivityB[15].toString()): " - "%>');

</script>
                       		
							
						 </form>   
						 
							<script type="text/javascript">
var from ="<%=sdf.format(ActivityA[2])%>".split("-")
var dt = new Date(from[2], from[1] - 1, from[0])
var to ="<%=sdf.format(ActivityA[3])%>".split("-")
var dt1 = new Date(to[2], to[1] - 1, to[0])
$('#DateCompletionB'+'<%=ActivityA[0] %><%=ActivityB[0] %>').daterangepicker({
	"singleDatePicker" : true,
	"linkedCalendars" : false,
	"showCustomRangeLabel" : true,
	"minDate" :dt,
	"maxDate" : dt1,
	"cancelClass" : "btn-default",
	showDropdowns : true,
	locale : {
		format : 'DD-MM-YYYY'
	}
});


var mindate=dt;
$('#DateCompletionB'+'<%=ActivityA[0] %><%=ActivityB[0] %>').on('change', function() {
    mindate=$('#DateCompletionB'+'<%=ActivityA[0] %><%=ActivityB[0] %>').val();
    $('#DateCompletionB2'+'<%=ActivityA[0] %><%=ActivityB[0] %>').prop("disabled",false);
    $('#DateCompletionB2'+'<%=ActivityA[0] %><%=ActivityB[0] %>').daterangepicker({
    	"singleDatePicker" : true,
    	"linkedCalendars" : false,
    	"showCustomRangeLabel" : true,
    	"minDate" :mindate,
    	"maxDate" : dt1,
    	"cancelClass" : "btn-default",
    	showDropdowns : true,
    	locale : {
    		format : 'DD-MM-YYYY'
    	}
    	});
  });
  
  
  
$( document ).ready(function() {
    mindate=$('#DateCompletionB'+'<%=ActivityA[0] %><%=ActivityB[0] %>').val();
    var dt2=$('#DateCompletionA2'+'<%=ActivityA[0] %>').val();
    $('#DateCompletionB2'+'<%=ActivityA[0] %><%=ActivityB[0] %>').prop("disabled",false);
    $('#DateCompletionB2'+'<%=ActivityA[0] %><%=ActivityB[0] %>').daterangepicker({
    	"singleDatePicker" : true,
    	"linkedCalendars" : false,
    	"showCustomRangeLabel" : true,
    	"minDate" :mindate,
    	"maxDate" : dt2,
    	"cancelClass" : "btn-default",
    	showDropdowns : true,
    	locale : {
    		format : 'DD-MM-YYYY'
    	}
    	});
  });
	    
	</script> 					 
						 
						<!-- B end -->
						
                      <div id="children_B_<%=ActivityB[0]%>" style="display:none; border-left: 2px dashed #ccc; margin-left: 15px;">
							<%


if(MilestoneActivityC!=null&&MilestoneActivityC.size()>0){
	int countC=1;
	for(Object[] ActivityC:MilestoneActivityC){
		List<Object[]> MilestoneActivityD=(List<Object[]>)request.getAttribute("MilestoneActivityD"+countA+countB+countC);	
		changes.add(ActivityC[26].toString());
%>


	
		

					
						<form   method="POST" action="MilestoneActivityEditSubmit.htm" id="form<%=ActivityB[0] %>C<%=ActivityC[0] %>">
                            <div class="row container-fluid" id="row_C_<%=ActivityC[0]%>">
                             <%-- <div class="col-md-1 " ><br> <label class="control-label"></label><b class="ml-2">C-<%=countC %></b>
                    		
                        	</div> --%>
                        	<div class="col-md-1 " ><label class="control-label ml-1" ></label><br> <b class="ml-1">C-<%=countC %></b><br>
							    <% if(MilestoneActivityD != null && MilestoneActivityD.size() > 0) { %>
							         <button type="button" id="btn_C_<%=ActivityC[0]%>" class="btn btn-sm btn-primary py-0 px-2 mt-1" onclick="toggleChildren('children_C_<%=ActivityC[0]%>', this)"><i class="fa fa-plus" aria-hidden="true"></i></button>
							    <% } %>
							</div>
                        	
						    <div class="col-md-5 " ><br>
                    		 <textarea rows="1" cols="50" class="form-control mp2 " <%if(RevisionCount>0){ %>  <%} %> name="ActivityName" id="ActivityName"    maxlength="1000" required="required"><%=ActivityC[4]!=null?ActivityC[4].toString(): " - " %></textarea> 
                        	</div>
                        	
                        	<div class="col-md-1 " align="center"><br>
                        	<input class="form-control width120" name="ValidFrom" id="DateCompletionC<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %>"  value="<%=sdf.format(ActivityC[2]) %>"  required="required" >
                        	
                        	</div>
                        	<div class="col-md-1 " align="center"><br>
                        	<input class="form-control width120" name="ValidTo" id="DateCompletionC2<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %>"  value="<%=sdf.format(ActivityC[3]) %>"  required="required"   >
                        		</div>
                        		<div class="col-md-1 " align="center" ><br>      
                    		<input type="number" class="form-control width95" name="Weightage" id="Weightage<%=ActivityB[0] %>C<%=ActivityC[0] %>" required="required" min="0" max="100" value="<%=ActivityC[6]!=null?StringEscapeUtils.escapeHtml4(ActivityC[6].toString()): "" %>"  >
                        	</div>
                        	
                        	<div class="col-md-2 " ><br>
                        		<%if(RevisionCount==0) { %>
                              		<select class="form-control selectdee" id="ActivityTypeId<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %>" required="required" name="ActivityTypeId">
    									<option disabled="true"  selected value="">Choose...</option>
    										<% for (Object[] obj : ActivityTypeList) {%>
										<option value="<%=obj[0]%>" <%if(ActivityC[11].toString().equalsIgnoreCase(obj[0].toString())){ %> selected="selected" <% }%>><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "%> </option>
											<%} %>
  									</select>
                        		<%} %>
                        	</div>
                        	<div class="col-md-1 "><br>
                          <%if( Arrays.asList(getMA[8].toString(),projectDirector,getMA[9].toString(),ActivityA[13].toString(),ActivityA[15].toString(),ActivityB[13].toString(),ActivityB[15].toString() ).contains(EmpId.toString()) || LoginType.equalsIgnoreCase("A")  ){ %>
                        	  <button type="button"  class="btn btn-sm edit" onclick="weightage_sum('<%=ActivityB[0] %>','<%=ActivityC[0] %>','C','3',['A_<%=ActivityA[0]%>','B_<%=ActivityB[0]%>'],'row_C_<%=ActivityC[0]%>');"> <i class="fa fa-edit" aria-hidden="true"></i> </button>
                        	  
                        	  <%if((ActivityC[5] == null || Long.parseLong(ActivityC[5].toString()) <= 0) && (ActivityC[6] == null || Long.parseLong(ActivityC[6].toString()) <= 0)  && (ActivityC[9] == null || Long.parseLong(ActivityC[9].toString()) < 2)){ %>
                        	  <button type="button" class="btn btn-sm delete" onclick="deletSubMilestones('<%=ActivityB[0] %>','<%=ActivityC[0] %>','C','3');" >
                        	  	<i class="fa fa-trash" aria-hidden="true"></i>
                        	  </button>
                        	  <%} %>
                        	  
                        	  <input type="submit" hidden="hidden" id="<%=ActivityB[0] %>C<%=ActivityC[0] %>sub"/> 
                        	  
                              <input type="hidden" name="RevId"	value="<%=RevisionCount %>" /> 
                              <input type="hidden" name="MilestoneActivityId"	value="<%=getMA[0] %>" /> 
                              <input type="hidden" name="ActivityId"	value="<%=ActivityC[0] %>" /> 
                              <input type="hidden" name="ActivityType"	value="C" /> 
                                   <input type="hidden" name="projectDirector" value ="<%=projectDirector%>">
                              <input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
	                              <input type="hidden" name="chainId" value="['A_<%=ActivityA[0]%>','B_<%=ActivityB[0]%>']" />
	                              <input type="hidden" name="targetRowId" value="row_C_<%=ActivityC[0]%>" />
                                 <%} %>                   	
                               </div>
                                                    	 
                        	</div>
                        	
                       		<div class="row container-fluid" >
                             <div class="col-md-1 " >                    		
                        	</div>
                        	
                        	<div class="col-md-2"><br>
                        		<label  >Lab: <span class="mandatory"  >*</span></label><br>
                        		<select class="form-control selectdee" name="labCode1" id="labCode1C<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %>" required 
								onchange="renderEmployeeList('1','C<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %>','0')" data-placeholder= "Lab Name">
								    <% for (Object[] lab : allLabList) { %>
								    	<option value="<%=lab[3]%>" <%if(ActivityC[28].toString().equalsIgnoreCase(lab[3].toString())) {%>selected<%} %> ><%=lab[3]!=null?StringEscapeUtils.escapeHtml4(lab[3].toString()): " - "%></option>
								    <%}%>
								</select>
                        	</div>
                       <div class="col-md-3 " align="center"><br>
                        	<label class="control-label">First OIC  </label>
                        	<%-- <div style="float: right;"  > <label>All &nbsp; : &nbsp;&nbsp;</label>
										<input type="checkbox" style="float: right; margin-top : 6px;" id="allempcheckbox1<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %>" onchange="changeempoic1('<%=ActivityC[13]%>','<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %>')" >
									</div> --%>
                              		<select class="form-control selectdee" id="EmpIdC<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %>" required="required" name="EmpId">
    									
											
  									</select>
                        	</div>
                        	<div class="col-md-2"><br>
                        		<label  >Lab: <span class="mandatory" >*</span></label><br>
                        		<select class="form-control selectdee" name="labCode2" id="labCode2C<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %>" required 
								onchange="renderEmployeeList('2','C<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %>','0')" data-placeholder= "Lab Name">
								    <% for (Object[] lab : allLabList) { %>
								    	<option value="<%=lab[3]%>" <%if(ActivityC[29].toString().equalsIgnoreCase(lab[3].toString())) {%>selected<%} %> ><%=lab[3]!=null?StringEscapeUtils.escapeHtml4(lab[3].toString()): " - "%></option>
								    <%}%>
								</select>
                        	</div>
                        	<div class="col-md-3 " align="center"><br>
                        		<label class="control-label">Second OIC </label>
                        		<%-- <div style="float: right;"  > <label>All &nbsp; : &nbsp;&nbsp;</label>
										<input type="checkbox" style="float: right; margin-top : 6px;" id="allempcheckbox2<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %>" onchange="changeempoic2('<%=ActivityC[15]%>','<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %>')" >
									</div> --%>
                              		<select class="form-control selectdee" id="EmpId1C<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %>" required="required" name="EmpId1">
    									
  									</select>
  										</div>
  										
  							</div>			
  <script type="text/javascript">

<%-- changeempoic1(<%=ActivityC[13] %>,<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %>);
changeempoic2(<%=ActivityC[15] %>,<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %>); --%>

renderEmployeeList('1','C<%=ActivityA[0]!=null?StringEscapeUtils.escapeHtml4(ActivityA[0].toString()): " - " %><%=ActivityB[0]!=null?StringEscapeUtils.escapeHtml4(ActivityB[0].toString()): " - " %><%=ActivityC[0]!=null?StringEscapeUtils.escapeHtml4(ActivityC[0].toString()): " - " %>', '<%=ActivityC[13]!=null?StringEscapeUtils.escapeHtml4(ActivityC[13].toString()): " - "%>');
renderEmployeeList('2','C<%=ActivityA[0]!=null?StringEscapeUtils.escapeHtml4(ActivityA[0].toString()): " - " %><%=ActivityB[0]!=null?StringEscapeUtils.escapeHtml4(ActivityB[0].toString()): " - " %><%=ActivityC[0]!=null?StringEscapeUtils.escapeHtml4(ActivityC[0].toString()): " - " %>', '<%=ActivityC[15]!=null?StringEscapeUtils.escapeHtml4(ActivityC[15].toString()): " - "%>');

</script>
                       		
                        	
	                        </form> 
	                        
	           					<script type="text/javascript">
var from ="<%=sdf.format(ActivityB[2])%>".split("-")
var dt = new Date(from[2], from[1] - 1, from[0])
var to ="<%=sdf.format(ActivityB[3])%>".split("-")
var dt1 = new Date(to[2], to[1] - 1, to[0])
$('#DateCompletionC'+'<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %>').daterangepicker({
	"singleDatePicker" : true,
	"linkedCalendars" : false,
	"showCustomRangeLabel" : true,
	"minDate" :dt,
	"maxDate" : dt1,
	"cancelClass" : "btn-default",
	showDropdowns : true,
	locale : {
		format : 'DD-MM-YYYY'
	}
});


var mindate=dt;
$('#DateCompletionC'+'<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %>').on('change', function() {
    mindate=$('#DateCompletionC'+'<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %>').val();
    $('#DateCompletionC2'+'<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %>').prop("disabled",false);
    $('#DateCompletionC2'+'<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %>').daterangepicker({
    	"singleDatePicker" : true,
    	"linkedCalendars" : false,
    	"showCustomRangeLabel" : true,
    	"minDate" :mindate,
    	"maxDate" : dt1,
    	"cancelClass" : "btn-default",
    	showDropdowns : true,
    	locale : {
    		format : 'DD-MM-YYYY'
    	}
    	});
  });
  
  
  
$( document ).ready(function() {
    mindate=$('#DateCompletionC'+'<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %>').val();
    var dt3=$('#DateCompletionB2'+'<%=ActivityA[0] %><%=ActivityB[0] %>').val();
    $('#DateCompletionC2'+'<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %>').prop("disabled",false);
    $('#DateCompletionC2'+'<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %>').daterangepicker({
    	"singleDatePicker" : true,
    	"linkedCalendars" : false,
    	"showCustomRangeLabel" : true,
    	"minDate" :mindate,
    	"maxDate" : dt3,
    	"cancelClass" : "btn-default",
    	showDropdowns : true,
    	locale : {
    		format : 'DD-MM-YYYY'
    	}
    	});
  });
	    
	</script> 	     
	
                      <div id="children_C_<%=ActivityC[0]%>" style="display:none; border-left: 2px dashed #ccc; margin-left: 15px;">
								<%


if(MilestoneActivityD!=null&&MilestoneActivityD.size()>0){
	int countD=1;
	for(Object[] ActivityD:MilestoneActivityD){
		List<Object[]> MilestoneActivityE=(List<Object[]>)request.getAttribute("MilestoneActivityE"+countA+countB+countC+countD);	
		changes.add(ActivityD[26].toString());
%>


	                              <form  method="POST" action="MilestoneActivityEditSubmit.htm" id="form<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %>">
		
						    <div class="row container-fluid" id="row_D_<%=ActivityD[0]%>">
						     <%-- <div class="col-md-1 " ><br> <label class="control-label"></label><b class="ml-2">D-<%=countD %></b>
                    		
                        	</div> --%>
                        	<div class="col-md-1 " ><label class="control-label ml-1" ></label><br> <b class="ml-1">D-<%=countD %></b><br>
							    <% if(MilestoneActivityE != null && MilestoneActivityE.size() > 0) { %>
							         <button type="button" id="btn_D_<%=ActivityD[0]%>" class="btn btn-sm btn-primary py-0 px-2 mt-1" onclick="toggleChildren('children_D_<%=ActivityD[0]%>', this)"><i class="fa fa-plus" aria-hidden="true"></i></button>
							    <% } %>
							</div>
						    <div class="col-md-5 " ><br>
                    		 <textarea rows="1" cols="50" class="form-control mp2" <%if(RevisionCount>0){ %>  <%} %> name="ActivityName" id="ActivityName"    maxlength="1000" required="required"><%=ActivityD[4]!=null?ActivityD[4].toString(): " - " %></textarea> 
                        	</div>
                        	
                        	<div class="col-md-1 " align="center"><br>
                        	<input class="form-control width120" name="ValidFrom" id="DateCompletionB<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %>"  value="<%=sdf.format(ActivityD[2]) %>"  required="required"  " >
                        	
                        	</div>
                        	<div class="col-md-1 " align="center"><br>
                        	<input class="form-control width120" name="ValidTo" id="DateCompletionB2<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %>"  value="<%=sdf.format(ActivityD[3]) %>"  required="required"   >
                        		</div>
                            <div class="col-md-1 " align="center" ><br>      
                    		<input type="number" class="form-control width95"  name="Weightage" id="Weightage<%=ActivityC[0] %>D<%=ActivityD[0] %>" required="required" min="0" max="100" value="<%=ActivityD[6]!=null?StringEscapeUtils.escapeHtml4(ActivityD[6].toString()): " - " %>"   >
                        	</div>
                        	
                        	<div class="col-md-2 " ><br>
                        		<%if(RevisionCount==0) { %>
                              		<select class="form-control selectdee" id="ActivityTypeId<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %>" required="required" name="ActivityTypeId">
    									<option disabled="true"  selected value="">Choose...</option>
    										<% for (Object[] obj : ActivityTypeList) {%>
										<option value="<%=obj[0]%>" <%if(ActivityD[11].toString().equalsIgnoreCase(obj[0].toString())){ %> selected="selected" <% }%>><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): ""%> </option>
											<%} %>
  									</select>
                    	    	<%} %>
                    	    </div>
                    	      	
                        	<div class="col-md-1 "><br>
                        	
                       <%if( Arrays.asList(getMA[8].toString(),projectDirector,getMA[9].toString(),ActivityA[13].toString(),ActivityA[15].toString(),ActivityB[13].toString(),ActivityB[15].toString(),ActivityC[13].toString(),ActivityC[15].toString() ).contains(EmpId.toString()) || LoginType.equalsIgnoreCase("A")  ){ %>
                        	  <button type="button"  class="btn btn-sm edit" onclick="weightage_sum('<%=ActivityC[0] %>','<%=ActivityD[0] %>','D','4',['A_<%=ActivityA[0]%>','B_<%=ActivityB[0]%>','C_<%=ActivityC[0]%>'],'row_D_<%=ActivityD[0]%>');"> <i class="fa fa-edit" aria-hidden="true"></i> </button>
                        	 
                        	 <%if((ActivityD[5] == null || Long.parseLong(ActivityD[5].toString()) <= 0) && (ActivityD[6] == null || Long.parseLong(ActivityD[6].toString()) <= 0)  && (ActivityD[9] == null || Long.parseLong(ActivityD[9].toString()) < 2)){ %>
                        	  <button type="button" class="btn btn-sm delete" onclick="deletSubMilestones('<%=ActivityC[0] %>','<%=ActivityD[0] %>','D','4');" >
                        	  	<i class="fa fa-trash" aria-hidden="true"></i>
                        	  </button>
                        	  <%} %>
                        	  
                        	  <input type="submit" hidden="hidden" id="<%=ActivityC[0] %>D<%=ActivityD[0] %>sub"/> 
                              <input type="hidden" name="RevId"	value="<%=RevisionCount %>" /> 
                              <input type="hidden" name="MilestoneActivityId"	value="<%=getMA[0] %>" /> 
                              <input type="hidden" name="ActivityId"	value="<%=ActivityD[0] %>" /> 
                              <input type="hidden" name="projectDirector" value ="<%=projectDirector%>">
                              <input type="hidden" name="chainId" value="['A_<%=ActivityA[0]%>','B_<%=ActivityB[0]%>','C_<%=ActivityC[0]%>']" />
                              <input type="hidden" name="targetRowId" value="row_D_<%=ActivityD[0]%>" />
                                                          
                                                            <input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
                              <%} %>
                                                  	</div>
							</div>
							
                       		<div class="row container-fluid" >
                             <div class="col-md-1 " >                    		
                        	</div>
                        	
                        	<div class="col-md-2"><br>
                        		<label  >Lab: <span class="mandatory" >*</span></label><br>
                        		<select class="form-control selectdee" name="labCode1" id="labCode1D<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %>" required 
								onchange="renderEmployeeList('1','D<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %>','0')" data-placeholder= "Lab Name">
								    <% for (Object[] lab : allLabList) { %>
								    	<option value="<%=lab[3]%>" <%if(ActivityD[28].toString().equalsIgnoreCase(lab[3].toString())) {%>selected<%} %> ><%=lab[3]!=null?StringEscapeUtils.escapeHtml4(lab[3].toString()): " - "%></option>
								    <%}%>
								</select>
                        	</div>
                        	
                            <div class="col-md-3 " align="center"><br>
                        	<label class="control-label">First OIC  </label>
                        	<%-- <div style="float: right;"  > <label>All &nbsp; : &nbsp;&nbsp;</label>
										<input type="checkbox" style="float: right; margin-top : 6px;" id="allempcheckbox1<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %>" onchange="changeempoic1('<%=ActivityD[13]%>','<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %>')" >
									</div> --%>
                              		<select class="form-control selectdee" id="EmpIdD<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %>" required="required" name="EmpId">
    									
											
  									</select>
                        	</div>
                        	<div class="col-md-2"><br>
                        		<label  >Lab: <span class="mandatory" >*</span></label><br>
                        		<select class="form-control selectdee" name="labCode2" id="labCode2D<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %>" required 
								onchange="renderEmployeeList('2','D<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %>','0')" data-placeholder= "Lab Name">
								    <% for (Object[] lab : allLabList) { %>
								    	<option value="<%=lab[3]%>" <%if(ActivityD[29].toString().equalsIgnoreCase(lab[3].toString())) {%>selected<%} %> ><%=lab[3]!=null?StringEscapeUtils.escapeHtml4(lab[3].toString()): " - "%></option>
								    <%}%>
								</select>
                        	</div>
                        	<div class="col-md-3 " align="center"><br>
                        		<label class="control-label">Second OIC </label>
                        		<%-- <div style="float: right;"  > <label>All &nbsp; : &nbsp;&nbsp;</label>
										<input type="checkbox" style="float: right; margin-top : 6px;" id="allempcheckbox2<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %>" onchange="changeempoic2('<%=ActivityD[15]%>','<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %>')" >
									</div> --%>
                              		<select class="form-control selectdee" id="EmpId1D<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %>" required="required" name="EmpId1">
    									
  									</select>
  										</div>
  										
  							</div>			
  <script type="text/javascript">

<%-- changeempoic1(<%=ActivityD[13] %>,<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %>);
changeempoic2(<%=ActivityD[15] %>,<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %>); --%>

renderEmployeeList('1','D<%=ActivityA[0]!=null?StringEscapeUtils.escapeHtml4(ActivityA[0].toString()): " - " %><%=ActivityB[0]!=null?StringEscapeUtils.escapeHtml4(ActivityB[0].toString()): " - " %><%=ActivityC[0]!=null?StringEscapeUtils.escapeHtml4(ActivityC[0].toString()): " - " %><%=ActivityD[0]!=null?StringEscapeUtils.escapeHtml4(ActivityD[0].toString()): " - " %>', '<%=ActivityD[13]!=null?StringEscapeUtils.escapeHtml4(ActivityD[13].toString()): " - "%>');
renderEmployeeList('2','D<%=ActivityA[0]!=null?StringEscapeUtils.escapeHtml4(ActivityA[0].toString()): " - " %><%=ActivityB[0]!=null?StringEscapeUtils.escapeHtml4(ActivityB[0].toString()): " - " %><%=ActivityC[0]!=null?StringEscapeUtils.escapeHtml4(ActivityC[0].toString()): " - " %><%=ActivityD[0]!=null?StringEscapeUtils.escapeHtml4(ActivityD[0].toString()): " - " %>', '<%=ActivityD[15]!=null?StringEscapeUtils.escapeHtml4(ActivityD[15].toString()): " - "%>');
</script>
								
						 </form>   
						 
							<script type="text/javascript">
var from ="<%=sdf.format(ActivityC[2])%>".split("-")
var dt = new Date(from[2], from[1] - 1, from[0])
var to ="<%=sdf.format(ActivityC[3])%>".split("-")
var dt1 = new Date(to[2], to[1] - 1, to[0])
$('#DateCompletionB'+'<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %>').daterangepicker({
	"singleDatePicker" : true,
	"linkedCalendars" : false,
	"showCustomRangeLabel" : true,
	"minDate" :dt,
	"maxDate" : dt1,
	"cancelClass" : "btn-default",
	showDropdowns : true,
	locale : {
		format : 'DD-MM-YYYY'
	}
});


var mindate=dt;
$('#DateCompletionB'+'<%=ActivityD[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %>').on('change', function() {
    mindate=$('#DateCompletionB'+'<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %>').val();
    $('#DateCompletionB2'+'<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %>').prop("disabled",false);
    $('#DateCompletionB2'+'<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %>').daterangepicker({
    	"singleDatePicker" : true,
    	"linkedCalendars" : false,
    	"showCustomRangeLabel" : true,
    	"minDate" :mindate,
    	"maxDate" : dt1,
    	"cancelClass" : "btn-default",
    	showDropdowns : true,
    	locale : {
    		format : 'DD-MM-YYYY'
    	}
    	});
  });
  
  
  
$( document ).ready(function() {
    mindate=$('#DateCompletionB'+'<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %>').val();
    var dt4=$('#DateCompletionC2'+'<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %>').val();
    $('#DateCompletionB2'+'<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %>').prop("disabled",false);
    $('#DateCompletionB2'+'<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %>').daterangepicker({
    	"singleDatePicker" : true,
    	"linkedCalendars" : false,
    	"showCustomRangeLabel" : true,
    	"minDate" :mindate,
    	"maxDate" : dt4,
    	"cancelClass" : "btn-default",
    	showDropdowns : true,
    	locale : {
    		format : 'DD-MM-YYYY'
    	}
    	});
  });
	    
	</script> 					 
						 
                      <div id="children_D_<%=ActivityD[0]%>" style="display:none; border-left: 2px dashed #ccc; margin-left: 15px;">
						<!-- B end -->
						
							<%


if(MilestoneActivityE!=null&&MilestoneActivityE.size()>0){
	int countE=1;
	for(Object[] ActivityE:MilestoneActivityE){
		changes.add(ActivityE[26].toString());
%>


	
		

					
						<form   method="POST" action="MilestoneActivityEditSubmit.htm" id="form<%=ActivityD[0] %>E<%=ActivityE[0] %>">
                            <div class="row container-fluid" id="row_E_<%=ActivityE[0]%>">
                             <div class="col-md-1 " ><br> <label class="control-label"></label><b class="ml-3">E-<%=countE %></b>
                    		
                        	</div>
                        	
						    <div class="col-md-5 " ><br>
                    		 <textarea rows="1" cols="50" class="form-control mp2" <%if(RevisionCount>0){ %>  <%} %> name="ActivityName" id="ActivityName"    maxlength="1000" required="required"><%=ActivityE[4] !=null?ActivityE[4].toString(): " - "%></textarea> 
                        	</div>
                        	
                        	<div class="col-md-1 width120" align="center"><br>
                        	<input class="form-control " name="ValidFrom" id="DateCompletionC<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %><%=ActivityE[0] %>"  value="<%=sdf.format(ActivityE[2]) %>"  required="required"  >
                        	
                        	</div>
                        	<div class="col-md-1 width120" align="center"><br>
                        	<input class="form-control " name="ValidTo" id="DateCompletionC2<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %><%=ActivityE[0] %>"  value="<%=sdf.format(ActivityE[3]) %>"  required="required"   >
                        		</div>
                        		<div class="col-md-1 " align="center" ><br>      
                    		<input type="number" class="form-control width95" name="Weightage" id="Weightage<%=ActivityD[0] %>E<%=ActivityE[0] %>" required="required" min="0" max="100" value="<%=ActivityE[6]!=null?StringEscapeUtils.escapeHtml4(ActivityE[6].toString()): "" %>"  >
                        	</div>
                        	<div class="col-md-2 " ><br>
                        		<%if(RevisionCount==0) { %>
                              		<select class="form-control selectdee" id="ActivityTypeId<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %><%=ActivityE[0] %>" required="required" name="ActivityTypeId">
    									<option disabled="true"  selected value="">Choose...</option>
    										<% for (Object[] obj : ActivityTypeList) {%>
										<option value="<%=obj[0]%>" <%if(ActivityE[11].toString().equalsIgnoreCase(obj[0].toString())){ %> selected="selected" <% }%>><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - "%> </option>
											<%} %>
  									</select>
                        		<%} %>
                        	</div>
                        	<div class="col-md-1 "><br>
                  			<%if( Arrays.asList(getMA[8].toString(),projectDirector,getMA[9].toString(),ActivityA[13].toString(),ActivityA[15].toString(),ActivityB[13].toString(),ActivityB[15].toString(),ActivityC[13].toString(),ActivityC[15].toString(),ActivityD[13].toString(),ActivityD[15].toString() ).contains(EmpId.toString()) || LoginType.equalsIgnoreCase("A")  ){ %>
                        	  <button type="button"  class="btn btn-sm edit" onclick="weightage_sum('<%=ActivityD[0] %>','<%=ActivityE[0] %>','E','5',['A_<%=ActivityA[0]%>','B_<%=ActivityB[0]%>','C_<%=ActivityC[0]%>','D_<%=ActivityD[0]%>'],'row_E_<%=ActivityE[0]%>');"> <i class="fa fa-edit" aria-hidden="true"></i> </button>
                        	  
                        	  <%if((ActivityE[5] == null || Long.parseLong(ActivityE[5].toString()) <= 0) && (ActivityE[6] == null || Long.parseLong(ActivityE[6].toString()) <= 0) && (ActivityE[9] == null || Long.parseLong(ActivityE[9].toString()) < 2)){ %>  
                        	  <button type="button" class="btn btn-sm delete" onclick="deletSubMilestones('<%=ActivityD[0] %>','<%=ActivityE[0] %>','E','5');" >
                        	  	<i class="fa fa-trash" aria-hidden="true"></i>
                        	  </button>
                        	  <%} %>
                        	  
                        	  <input type="submit" hidden="hidden" id="<%=ActivityD[0] %>E<%=ActivityE[0] %>sub"/> 
                              <input type="hidden" name="RevId"	value="<%=RevisionCount %>" /> 
                              <input type="hidden" name="MilestoneActivityId"	value="<%=getMA[0] %>" /> 
                              <input type="hidden" name="ActivityId"	value="<%=ActivityE[0] %>" /> 
                              <input type="hidden" name="projectDirector" value ="<%=projectDirector%>">
                              <input type="hidden" name="chainId" value="['A_<%=ActivityA[0]%>','B_<%=ActivityB[0]%>','C_<%=ActivityC[0]%>','D_<%=ActivityD[0]%>']" />
                              <input type="hidden" name="targetRowId" value="row_E_<%=ActivityE[0]%>" />
                              
                              <input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> 
                              <%} %>     
                               </div>
                                                    	 
                        	</div>
                        	
                       		<div class="row container-fluid" >
                             <div class="col-md-1 " >                    		
                        	</div>
                        	
                        	<div class="col-md-2"><br>
                        		<label  >Lab: <span class="mandatory" >*</span></label><br>
                        		<select class="form-control selectdee" name="labCode1" id="labCode1E<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %><%=ActivityE[0] %>" required 
								onchange="renderEmployeeList('1','E<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %><%=ActivityE[0] %>','0')" data-placeholder= "Lab Name">
								    <% for (Object[] lab : allLabList) { %>
								    	<option value="<%=lab[3]%>" <%if(ActivityE[28].toString().equalsIgnoreCase(lab[3].toString())) {%>selected<%} %> ><%=lab[3]!=null?StringEscapeUtils.escapeHtml4(lab[3].toString()): " - "%></option>
								    <%}%>
								</select>
                        	</div>
                        	
                        <div class="col-md-3 " align="center"><br>
                        	<label class="control-label">First OIC  </label>
                        	<%-- <div style="float: right;"  > <label>All &nbsp; : &nbsp;&nbsp;</label>
										<input type="checkbox" style="float: right; margin-top : 6px;" id="allempcheckbox1<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %><%=ActivityE[0] %>" onchange="changeempoic1('<%=ActivityE[13]%>','<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %><%=ActivityE[0] %>')" >
									</div> --%>
                              		<select class="form-control selectdee" id="EmpIdE<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %><%=ActivityE[0] %>" required="required" name="EmpId">
    									
											
  									</select>
                        	</div>
                        	
                        	<div class="col-md-2"><br>
                        		<label  >Lab: <span class="mandatory" >*</span></label><br>
                        		<select class="form-control selectdee" name="labCode2" id="labCode2E<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %><%=ActivityE[0] %>" required 
								onchange="renderEmployeeList('2','E<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %><%=ActivityE[0] %>','0')" data-placeholder= "Lab Name">
								    <% for (Object[] lab : allLabList) { %>
								    	<option value="<%=lab[3]%>" <%if(ActivityE[29].toString().equalsIgnoreCase(lab[3].toString())) {%>selected<%} %> ><%=lab[3]!=null?StringEscapeUtils.escapeHtml4(lab[3].toString()): " - "%></option>
								    <%}%>
								</select>
                        	</div>
                        	
                        	<div class="col-md-3 " align="center"><br>
                        		<label class="control-label">Second OIC </label>
                        		<%-- <div style="float: right;"  > <label>All &nbsp; : &nbsp;&nbsp;</label>
										<input type="checkbox" style="float: right; margin-top : 6px;" id="allempcheckbox2<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %><%=ActivityE[0] %>" onchange="changeempoic2('<%=ActivityE[15]%>','<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %><%=ActivityE[0] %>')" >
									</div> --%>
                              		<select class="form-control selectdee" id="EmpId1E<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %><%=ActivityE[0] %>" required="required" name="EmpId1">
    									
  									</select>
  										</div>
  										
  							</div>			
  <script type="text/javascript">

<%-- changeempoic1(<%=ActivityE[13] %>,<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %><%=ActivityE[0] %>);
changeempoic2(<%=ActivityE[15] %>,<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %><%=ActivityE[0] %>); --%>

renderEmployeeList('1','E<%=ActivityA[0]!=null?StringEscapeUtils.escapeHtml4(ActivityA[0].toString()): " - " %><%=ActivityB[0]!=null?StringEscapeUtils.escapeHtml4(ActivityB[0].toString()): " - " %><%=ActivityC[0]!=null?StringEscapeUtils.escapeHtml4(ActivityC[0].toString()): " - " %><%=ActivityD[0]!=null?StringEscapeUtils.escapeHtml4(ActivityD[0].toString()): " - " %><%=ActivityE[0]!=null?StringEscapeUtils.escapeHtml4(ActivityE[0].toString()): " - " %>', '<%=ActivityE[13]!=null?StringEscapeUtils.escapeHtml4(ActivityE[13].toString()): " - "%>');
renderEmployeeList('2','E<%=ActivityA[0]!=null?StringEscapeUtils.escapeHtml4(ActivityA[0].toString()): " - " %><%=ActivityB[0]!=null?StringEscapeUtils.escapeHtml4(ActivityB[0].toString()): " - " %><%=ActivityC[0]!=null?StringEscapeUtils.escapeHtml4(ActivityC[0].toString()): " - " %><%=ActivityD[0]!=null?StringEscapeUtils.escapeHtml4(ActivityD[0].toString()): " - " %><%=ActivityE[0]!=null?StringEscapeUtils.escapeHtml4(ActivityE[0].toString()): " - " %>', '<%=ActivityE[15]!=null?StringEscapeUtils.escapeHtml4(ActivityE[15].toString()): " - "%>');
</script>
                        	
	                        </form> 
	                        
	           					<script type="text/javascript">
var from ="<%=sdf.format(ActivityD[2])%>".split("-")
var dt = new Date(from[2], from[1] - 1, from[0])
var to ="<%=sdf.format(ActivityD[3])%>".split("-")
var dt1 = new Date(to[2], to[1] - 1, to[0])
$('#DateCompletionC'+'<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %><%=ActivityE[0] %>').daterangepicker({
	"singleDatePicker" : true,
	"linkedCalendars" : false,
	"showCustomRangeLabel" : true,
	"minDate" :dt,
	"maxDate" : dt1,
	"cancelClass" : "btn-default",
	showDropdowns : true,
	locale : {
		format : 'DD-MM-YYYY'
	}
});


var mindate=dt;
$('#DateCompletionC'+'<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %><%=ActivityE[0] %>').on('change', function() {
    mindate=$('#DateCompletionC'+'<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %><%=ActivityE[0] %>').val();
    $('#DateCompletionC2'+'<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %><%=ActivityE[0] %>').prop("disabled",false);
    $('#DateCompletionC2'+'<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %><%=ActivityE[0] %>').daterangepicker({
    	"singleDatePicker" : true,
    	"linkedCalendars" : false,
    	"showCustomRangeLabel" : true,
    	"minDate" :mindate,
    	"maxDate" : dt1,
    	"cancelClass" : "btn-default",
    	showDropdowns : true,
    	locale : {
    		format : 'DD-MM-YYYY'
    	}
    	});
  });
  
  
  
$( document ).ready(function() {
    mindate=$('#DateCompletionC'+'<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %><%=ActivityE[0] %>').val();
    var dt5=$('#DateCompletionC2'+'<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %>').val();

    
    $('#DateCompletionC2'+'<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %><%=ActivityE[0] %>').prop("disabled",false);
    $('#DateCompletionC2'+'<%=ActivityA[0] %><%=ActivityB[0] %><%=ActivityC[0] %><%=ActivityD[0] %><%=ActivityE[0] %>').daterangepicker({
    	"singleDatePicker" : true,
    	"linkedCalendars" : false,
    	"showCustomRangeLabel" : true,
    	"minDate" :mindate,
    	"maxDate" : dt5,
    	"cancelClass" : "btn-default",
    	showDropdowns : true,
    	locale : {
    		format : 'DD-MM-YYYY'
    	}
    	});
  });
	    
	</script>
						<!-- C end -->
									
									
	
		<%countE++;}}else{
	%>
				
	
<%} %>	
	
	</div>			
									
									
									
		
		<%countD++;}}else{
	%>
					
	
<%} %>	        
						<!-- C end -->
		
		</div>
									
									
	
		<%countC++;}}else{
	%>
				
	
<%} %>	

</div>
									
									
									
		
		<%countB++;}}else{
	%>
					
	
<%} %>	
			
						<!-- A end -->
						
		 
			
		
				
	</div>
	               
<%countA++;}}else{
	%>
				
	
<%} %>
	</div>
	
	<div  class="col-md-12">
	<br><br>
	<br><br><br><br><br>
	</div>
	</div>
	
	<% boolean anychange = changes.stream().anyMatch(e->e.equalsIgnoreCase("1")); 
	if(!anychange){
	%>
	<script type="text/javascript">
	console.log("No change to do BaseLine")
	$('#baseLineBtn').hide();
	</script>
	<%} %>
	

									</div>	
<script type="text/javascript">
function weightage_sum(id,activityid,type,levelid,chain,targetRowId){
	var sum=Number($('#Weightage'+id+type+activityid).val());
	event.preventDefault();
	  $.ajax({

			type : "GET",
			url : "WeightageSum.htm",
			data : {
				Id: id,
				ActivityId   : activityid,
					Type     :type,
					LevelId  :levelid
			},
			datatype : 'json',
			success : function(result) {

				var result = JSON.parse(result);
				var Msg="Project MileStone";
				if('M'==type){
					
				}else{
					Msg='Activity '+type;
				}
				 // console.log(sum);
				 sum+=Number(result);
				 // console.log(result);
				 // console.log(sum);
                 if(sum>100){
                	 
                	 alert('Total '+Msg+' Weightage='+sum+',Total '+Msg+' Weightage Should not greater  than 100.'); 
                 }else if(sum<=100){
                	 if(confirm('Total '+Msg+' Weightage='+sum+', Are you sure to Submit ?')){
                	/* 
                			 rememberReopenState(chain, targetRowId); */
                			 $('#'+id+type+activityid+'sub').click();
                		
                		 
                	 } 
                 }else{
                	 event.preventDefault(); 
                 }
				
			}
		}); 
	
}  


function deletSubMilestones(parentid,activityid,type,levelid){
	event.preventDefault();
	if(!confirm("Are you sure to delete ?")){
		return;
	}	

    const form = document.createElement("form");

    form.method = "POST";
    form.action = "SubLevelMilestoneDelete.htm";

    // sub Milestone ID
    const mainIdInput = document.createElement("input");
    mainIdInput.type = "hidden";
    mainIdInput.name = "subId";
    mainIdInput.value = activityid;

    // main Milestone ID
    const projectIdInput = document.createElement("input");
    projectIdInput.type = "hidden";
    projectIdInput.name = "MilestoneActivityId";
    projectIdInput.value = '<%= getMA[0] %>';

    // CSRF Token
    const csrfInput = document.createElement("input");
    csrfInput.type = "hidden";
    csrfInput.name = "${_csrf.parameterName}";
    csrfInput.value = "${_csrf.token}";

    form.appendChild(mainIdInput);
    form.appendChild(projectIdInput);
    form.appendChild(csrfInput);

    document.body.appendChild(form);

    form.submit();
	
	
}

</script>
													
<script type="text/javascript">
var from ="<%=sdf.format(getMA[2])%>".split("-")
var dt = new Date(from[2], from[1] - 1, from[0])
var to ="<%=sdf.format(getMA[3])%>".split("-")
var dt1 = new Date(to[2], to[1] - 1, to[0])
var mindate=dt;
$('#DateCompletion').on('change', function() {
    mindate=$('#DateCompletion').val();
    $('#DateCompletion2').prop("disabled",false);
    $('#DateCompletion2').daterangepicker({
    	"singleDatePicker" : true,
    	"linkedCalendars" : false,
    	"showCustomRangeLabel" : true,
    	
    	"cancelClass" : "btn-default",
    	showDropdowns : true,
    	locale : {
    		format : 'DD-MM-YYYY'
    	}
    	});
  });
$('#DateCompletion').daterangepicker({
	"singleDatePicker" : true,
	"linkedCalendars" : false,
	"showCustomRangeLabel" : true,
	
	"cancelClass" : "btn-default",
	showDropdowns : true,
	locale : {
		format : 'DD-MM-YYYY'
	}
});

/* ---------------------------------------dinesh--------------------------------------- */
$( document ).ready(function() {
    mindate=$('#DateCompletion').val();
    $('#DateCompletion2').prop("disabled",false);
    $('#DateCompletion2').daterangepicker({
    	"singleDatePicker" : true,
    	"linkedCalendars" : false,
    	"showCustomRangeLabel" : true,
    	
    	"cancelClass" : "btn-default",
    	showDropdowns : true,
    	locale : {
    		format : 'DD-MM-YYYY'
    	}
    	});
  });
  
	    
	</script>  


</body>
</html>