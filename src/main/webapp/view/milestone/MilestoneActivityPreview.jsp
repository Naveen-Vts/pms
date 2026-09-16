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

	// Runs once on page load. If a branch was remembered before the last submit, walks down
	// that chain fetching + expanding each ancestor in turn (content is lazy-loaded now, so it
	// won't already be in the DOM the way it used to be) then scrolls to the row that was updated.
	// See loadLevelInto()/expandNode() further down the page (defined in the lazy-loading engine).
	function restoreReopenState() {
		var raw;
		try {
			raw = '<%= chainId %>';
		} catch (e) {
			return;
		}
		if (raw === null || raw === '' || raw === 'null') {
			return;
		}
		var target = '<%= targetRowId %>';
		try {
			var chain = JSON.parse(raw);
			if (chain && chain.length && typeof autoExpandChain === 'function') {
				autoExpandChain(chain, function () {
					if (target) {
						setTimeout(function () {
							var el = document.getElementById(target);
							if (el) {
								el.scrollIntoView({ behavior: 'smooth', block: 'center' });
							}
						}, 300);
					}
				});
				return;
			}
		} catch (e) {
			console.log('restoreReopenState error', e);
		}
		if (target) {
			setTimeout(function() {
				var el = document.getElementById(target);
				if (el) {
					el.scrollIntoView({ behavior: 'smooth', block: 'center' });
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
// CHANGED: Level B and below used to be fetched eagerly here in a nested loop querying
// the DB 5 levels deep for every Level-A activity - this is what made the page slow with
// ~120 activities. Now only Level A is fetched (already done, one query, in the controller).
// Levels B-E are fetched on demand, one level at a time, via MilestoneActivityLevelFetch.htm,
// the first time the user expands a given node - see the lazy-loading engine script near the
// bottom of this page.
if(MilestoneActivityA!=null&&MilestoneActivityA.size()>0){
	int countA=1;
	for(Object[] ActivityA:MilestoneActivityA){
		String aAncestorOic = getMA[8] + "," + getMA[9];
		boolean canEditA = Arrays.asList(getMA[8].toString(), projectDirector, getMA[9].toString()).contains(EmpId.toString()) || LoginType.equalsIgnoreCase("A");
%>

		<form   method="POST" action="MilestoneActivityEditSubmit.htm" id="form<%=getMA[0] %>A<%=ActivityA[0] %>">

				<div class="row container-fluid" id="row_A_<%=ActivityA[0]%>">
					<div class="col-md-1 " ><label class="control-label ml-1" ></label><br> <b class="ml-1">A-<%=countA %></b><br>
					     <!-- CHANGED: always shown now (whether or not this node turns out to have children) since
					          knowing that up front would need the same eager per-node query we're trying to avoid.
					          Expanding a leaf just shows "No sub-activities." -->
					     <button type="button" id="btn_A_<%=ActivityA[0]%>" class="btn btn-sm btn-primary py-0 px-2 mt-1"
					     	onclick="toggleAjaxChildren(this,'children_A_<%=ActivityA[0]%>','A','<%=ActivityA[0]%>','<%=sdf.format(ActivityA[2])%>','<%=sdf.format(ActivityA[3])%>','<%=aAncestorOic%>',['A_<%=ActivityA[0]%>'])">
					     	<i class="fa fa-plus" aria-hidden="true"></i>
					     </button>
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
                    <%if(canEditA){ %>
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
                      		<select class="form-control selectdee" id="EmpId1A<%=ActivityA[0] %>" required="required" name="EmpId1">
  									</select>
  										</div>

  							</div>
  <script type="text/javascript">
  renderEmployeeList('1','A<%=ActivityA[0]!=null?StringEscapeUtils.escapeHtml4(ActivityA[0].toString()): " - " %>', '<%=ActivityA[13]!=null?StringEscapeUtils.escapeHtml4(ActivityA[13].toString()): " - "%>');
  renderEmployeeList('2','A<%=ActivityA[0]!=null?StringEscapeUtils.escapeHtml4(ActivityA[0].toString()): " - " %>', '<%=ActivityA[15]!=null?StringEscapeUtils.escapeHtml4(ActivityA[15].toString()): " - "%>');
</script>

	 </form>
		<script type="text/javascript">
$(function(){
var from ="<%=sdf.format(getMA[2])%>".split("-")
var dt = new Date(from[2], from[1] - 1, from[0])
var to ="<%=sdf.format(getMA[3])%>".split("-")
var dt1 = new Date(to[2], to[1] - 1, to[0])
$('#DateCompletionA'+'<%=ActivityA[0] %>').daterangepicker({
	"singleDatePicker" : true, "linkedCalendars" : false, "showCustomRangeLabel" : true,
	"minDate" :dt, "maxDate" : dt1, "cancelClass" : "btn-default", showDropdowns : true,
	locale : { format : 'DD-MM-YYYY' }
});
var mindate=dt;
$('#DateCompletionA'+'<%=ActivityA[0] %>').on('change', function() {
    mindate=$('#DateCompletionA'+'<%=ActivityA[0] %>').val();
    $('#DateCompletionA2'+'<%=ActivityA[0] %>').prop("disabled",false);
    $('#DateCompletionA2'+'<%=ActivityA[0] %>').daterangepicker({
    	"singleDatePicker" : true, "linkedCalendars" : false, "showCustomRangeLabel" : true,
    	"minDate" :mindate, "maxDate" : dt1, "cancelClass" : "btn-default", showDropdowns : true,
    	locale : { format : 'DD-MM-YYYY' }
    	});
  });
mindate=$('#DateCompletionA'+'<%=ActivityA[0] %>').val();
$('#DateCompletionA2'+'<%=ActivityA[0] %>').prop("disabled",false);
$('#DateCompletionA2'+'<%=ActivityA[0] %>').daterangepicker({
	"singleDatePicker" : true, "linkedCalendars" : false, "showCustomRangeLabel" : true,
	"minDate" :mindate, "maxDate" : dt1, "cancelClass" : "btn-default", showDropdowns : true,
	locale : { format : 'DD-MM-YYYY' }
	});
});
	</script>

              <div id="children_A_<%=ActivityA[0]%>" class="ms-children" style="display:none; border-left: 2px dashed #ccc; margin-left: 15px;">
			<!-- Level B activities for this Activity A load here on first expand -->
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
	
	<%--
		CHANGED: this used to scan a "changed" flag across every activity in the whole 5-level
		tree (changes.stream().anyMatch(...)), which required the eager full-tree load we just
		removed. Replaced with one dedicated, cheap check - see MilestoneActivityHasChanges.htm.
		NOTE: that endpoint currently only checks the root milestone's own flag as a placeholder;
		it needs a real aggregate query to fully match the old behavior (see controller comments).
	--%>
	<script type="text/javascript">
	$.ajax({
		type: 'GET',
		url: 'MilestoneActivityHasChanges.htm',
		data: { MilestoneActivityId: '<%=getMA[0]%>' },
		dataType: 'json',
		success: function (result) {
			if (!result || !result.hasChange) {
				$('#baseLineBtn').hide();
			}
		}
	});
	</script>
	

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



<%--
	============================================================================
	NEW: lazy-loading engine for Levels B-E (mirrors the one added to
	MilestoneActivityDetails.jsp, adapted for this page's inline-edit rows).
	Level A is rendered server-side above (one query total). Everything below
	Level A is fetched only when its node is actually expanded, via the shared
	MilestoneActivityLevelFetch.htm endpoint.
	============================================================================
--%>
<script type="text/javascript">
var RevisionCountVal = <%=RevisionCount%>;
var RootMilestoneIdPreview = "<%=getMA[0]%>";
var ProjectDirectorValPreview = "<%=projectDirector!=null?StringEscapeUtils.escapeEcmaScript(projectDirector):""%>";
var RootOicEmpIdsPreview = "<%=getMA[8]%>,<%=getMA[9]%>";

// Same lists the JSP already had in scope, exposed once so AJAX-rendered rows (B-E) don't
// need an extra round trip just to populate the Activity Type / Lab dropdowns. (Employee
// dropdowns are still populated per-row via the existing renderEmployeeList()/GetLabcodeEmpList.htm
// AJAX call, unchanged.)
var ActivityTypeOptions = [
<% for (int i = 0; i < ActivityTypeList.size(); i++) { Object[] t = ActivityTypeList.get(i); %>
	{ id: "<%=t[0]%>", name: "<%=t[1]!=null?StringEscapeUtils.escapeEcmaScript(t[1].toString()):""%>" }<%=i<ActivityTypeList.size()-1?",":""%>
<% } %>
];
var LabOptions = [
<% for (int i = 0; i < allLabList.size(); i++) { Object[] l = allLabList.get(i); %>
	"<%=l[3]%>"<%=i<allLabList.size()-1?",":""%>
<% } %>
];

var LEVEL_NUM = { A: 1, B: 2, C: 3, D: 4, E: 5 };
var NEXT_LETTER = { A: 'B', B: 'C', C: 'D', D: 'E' };

function escapeHtml(str) {
	if (str === null || str === undefined) return "";
	return String(str)
		.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;")
		.replace(/"/g, "&quot;").replace(/'/g, "&#39;");
}

function parseDMY(str) {
	if (!str) return null;
	var p = str.split("-");
	return new Date(p[2], p[1] - 1, p[0]);
}

function initRowDatePicker(fromId, toId, minDateStr, maxDateStr) {
	var minDate = parseDMY(minDateStr);
	var maxDate = parseDMY(maxDateStr);
	var opts = function (min) {
		return {
			singleDatePicker: true, linkedCalendars: false, showCustomRangeLabel: true,
			minDate: min, maxDate: maxDate, cancelClass: 'btn-default', showDropdowns: true,
			locale: { format: 'DD-MM-YYYY' }
		};
	};
	$('#' + fromId).daterangepicker(opts(minDate));
	$('#' + fromId).on('change', function () {
		var mindate = $('#' + fromId).val();
		$('#' + toId).prop('disabled', false);
		$('#' + toId).daterangepicker(opts(mindate));
	});
	$('#' + toId).prop('disabled', false);
	$('#' + toId).daterangepicker(opts(minDate));
}

// Click handler for every "+/-" expand button (Level A ones rendered server-side above call
// this directly; AJAX-rendered B/C/D rows wire up the same function - see buildEditRow()).
function toggleAjaxChildren(btnEl, containerId, letter, nodeId, parentFrom, parentTo, ancestorOic, chain) {
	var $container = $('#' + containerId);
	var isHidden = $container.css('display') === 'none' || $container.css('display') === '';

	if (isHidden) {
		$container.show();
		$(btnEl).html('<i class="fa fa-minus" aria-hidden="true"></i>');
		if ($container.data('loaded') !== true) {
			loadLevelInto($container, letter, nodeId, ancestorOic, chain, parentFrom, parentTo);
		}
	} else {
		$container.hide();
		$(btnEl).html('<i class="fa fa-plus" aria-hidden="true"></i>');
	}
}

function loadLevelInto(container, parentLetter, parentId, ancestorOic, chain, parentFrom, parentTo, onDone) {
	if (container.data('loaded') === true) {
		if (onDone) onDone();
		return;
	}
	var level = LEVEL_NUM[parentLetter] + 1;
	container.html('<div class="text-muted ml-3">Loading...</div>');
	$.ajax({
		type: 'GET',
		url: 'MilestoneActivityLevelFetch.htm',
		data: { ParentId: parentId, Level: level, AncestorOicIds: ancestorOic },
		dataType: 'json',
		success: function (children) {
			container.data('loaded', true);
			container.empty();
			renderLevelNodes(container, children || [], NEXT_LETTER[parentLetter], parentId, ancestorOic, chain, parentFrom, parentTo);
			if (onDone) onDone();
		},
		error: function () {
			container.html('<div class="text-danger">Could not load activities. <a href="#" class="ms-retry">Retry</a></div>');
			container.find('.ms-retry').on('click', function (e) {
				e.preventDefault();
				container.data('loaded', false);
				loadLevelInto(container, parentLetter, parentId, ancestorOic, chain, parentFrom, parentTo, onDone);
			});
		}
	});
}

function renderLevelNodes(container, children, letter, parentId, ancestorOic, chain, parentFrom, parentTo) {
	if (!children.length) {
		container.append('<div class="text-muted ml-3">No sub-activities.</div>');
		return;
	}
	$.each(children, function (idx, node) {
		var $row = $(buildEditRow(letter, node, parentId, ancestorOic, chain, idx + 1, parentFrom, parentTo));
		container.append($row);

		var uid = letter + node.id;
		initRowDatePicker('DateCompletion' + uid, 'DateCompletion2' + uid, parentFrom, parentTo);
		renderEmployeeList('1', uid, node.firstOicId || '');
		renderEmployeeList('2', uid, node.secondOicId || '');

		if (letter !== 'E') {
			var childAncestorOic = ancestorOic + ',' + node.firstOicId + ',' + node.secondOicId;
			var childChain = chain.concat([letter + '_' + node.id]);
			$row.find('.ms-toggle-btn').on('click', function () {
				toggleAjaxChildren(this, 'children_' + letter + '_' + node.id, letter, node.id, node.validFrom, node.validTo, childAncestorOic, childChain);
			});
		}
	});
}

function buildEditRow(letter, node, parentId, ancestorOic, chain, displayIndex, parentFrom, parentTo) {
	console.log(chain)
	var uid = letter + node.id;
	var chainForThisNode = chain; 
	var childAncestorOic = ancestorOic + ',' + node.firstOicId + ',' + node.secondOicId;

	var typeSelect = '';
	if (RevisionCountVal === 0) {
		var typeOptions = $.map(ActivityTypeOptions, function (o) {
			var sel = (String(o.id) === String(node.activityTypeId)) ? ' selected="selected"' : '';
			return '<option value="' + escapeHtml(o.id) + '"' + sel + '>' + escapeHtml(o.name) + '</option>';
		}).join('');
		typeSelect = '<select class="form-control selectdee" id="ActivityTypeId' + uid + '" required="required" name="ActivityTypeId">'
			+ '<option disabled="true" selected value="">Choose...</option>' + typeOptions + '</select>';
	}

	var labOptions1 = $.map(LabOptions, function (code) {
		var sel = (code === node.labCode1) ? ' selected' : '';
		return '<option value="' + escapeHtml(code) + '"' + sel + '>' + escapeHtml(code) + '</option>';
	}).join('');
	var labOptions2 = $.map(LabOptions, function (code) {
		var sel = (code === node.labCode2) ? ' selected' : '';
		return '<option value="' + escapeHtml(code) + '"' + sel + '>' + escapeHtml(code) + '</option>';
	}).join('');

	var expandBtn = '';
	if (letter !== 'E') {
		expandBtn = '<button type="button" id="btn_' + letter + '_' + node.id + '" class="btn btn-sm btn-primary py-0 px-2 mt-1 ms-toggle-btn">'
			+ '<i class="fa fa-plus" aria-hidden="true"></i></button>';
	}

	var actionsHtml = '';
	if (node.canEdit) {
		var chainJson = JSON.stringify(chainForThisNode);
		var chainForOnclick = '[' + $.map(chainForThisNode, function (c) { return "'" + c + "'"; }).join(',') + ']';
		actionsHtml += '<button type="button" class="btn btn-sm edit" onclick="weightage_sum(\'' + parentId + '\',\'' + node.id + '\',\'' + letter + '\',\'' + LEVEL_NUM[letter] + '\',' + chainForOnclick + ',\'row_' + letter + '_' + node.id + '\');"><i class="fa fa-edit" aria-hidden="true"></i></button>';
		if (node.canDelete) {
			actionsHtml += '<button type="button" class="btn btn-sm delete" onclick="deletSubMilestones(\'' + parentId + '\',\'' + node.id + '\',\'' + letter + '\',\'' + LEVEL_NUM[letter] + '\');"><i class="fa fa-trash" aria-hidden="true"></i></button>';
		}
		actionsHtml += '<input type="submit" hidden="hidden" id="' + parentId + letter + node.id + 'sub">'
			+ '<input type="hidden" name="RevId" value="' + RevisionCountVal + '">'
			+ '<input type="hidden" name="MilestoneActivityId" value="' + RootMilestoneIdPreview + '">'
			+ '<input type="hidden" name="ActivityId" value="' + escapeHtml(node.id) + '">'
			+ '<input type="hidden" name="ActivityType" value="' + letter + '">'
			+ '<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">'
			+ '<input type="hidden" name="projectDirector" value="' + escapeHtml(ProjectDirectorValPreview) + '">'
			+ '<input type="hidden" name="chainId" value="' + escapeHtml(chainJson) + '">'
			+ '<input type="hidden" name="targetRowId" value="row_' + letter + '_' + node.id + '">';
	}

	return ''
		+ '<form method="POST" action="MilestoneActivityEditSubmit.htm" id="form' + parentId + letter + node.id + '">'
		+ '<div class="row container-fluid" id="row_' + letter + '_' + node.id + '">'
		+ '<div class="col-md-1"><b class="ml-1">' + letter + '-' + displayIndex + '</b><br>' + expandBtn + '</div>'
		+ '<div class="col-md-5"><textarea rows="1" cols="50" class="form-control mp2" name="ActivityName" maxlength="1000" required="required">' + escapeHtml(node.activityName) + '</textarea></div>'
		+ '<div class="col-md-1" align="center"><input class="form-control width120" name="ValidFrom" id="DateCompletion' + uid + '" value="' + escapeHtml(node.validFrom) + '" required="required"></div>'
		+ '<div class="col-md-1" align="center"><input class="form-control width120" name="ValidTo" id="DateCompletion2' + uid + '" value="' + escapeHtml(node.validTo) + '" required="required"></div>'
		+ '<div class="col-md-1" align="center"><input type="number" class="form-control width95" name="Weightage" id="Weightage' + parentId + letter + node.id + '" required="required" min="0" max="100" value="' + escapeHtml(node.weightage) + '"></div>'
		+ '<div class="col-md-2">' + typeSelect + '</div>'
		+ '<div class="col-md-1">' + actionsHtml + '</div>'
		+ '</div>'
		+ '<div class="row container-fluid">'
		+ '<div class="col-md-1"></div>'
		+ '<div class="col-md-2"><label>Lab: <span class="mandatory">*</span></label><br>'
		+ '<select class="form-control selectdee" name="labCode1" id="labCode1' + uid + '" required onchange="renderEmployeeList(\'1\',\'' + uid + '\',\'0\')">' + labOptions1 + '</select></div>'
		+ '<div class="col-md-3" align="center"><label class="control-label">First OIC</label>'
		+ '<select class="form-control selectdee" id="EmpId' + uid + '" required="required" name="EmpId"></select></div>'
		+ '<div class="col-md-2"><label>Lab: <span class="mandatory">*</span></label><br>'
		+ '<select class="form-control selectdee" name="labCode2" id="labCode2' + uid + '" required onchange="renderEmployeeList(\'2\',\'' + uid + '\',\'0\')">' + labOptions2 + '</select></div>'
		+ '<div class="col-md-3" align="center"><label class="control-label">Second OIC</label>'
		+ '<select class="form-control selectdee" id="EmpId1' + uid + '" required="required" name="EmpId1"></select></div>'
		+ '</div>'
		+ '</form>'
		+ (letter !== 'E' ? '<div id="children_' + letter + '_' + node.id + '" class="ms-children" style="display:none; border-left: 2px dashed #ccc; margin-left: 15px;"></div>' : '');
}

// Restores the old "jump back to where I was" behaviour after editing a nested activity:
// walks down the chain of ancestor ids (e.g. ['A_12','B_34']), fetching + expanding each level
// in turn, then invokes the callback (used to scroll to + highlight the edited row).
function autoExpandChain(chain, onComplete) {
	if (!chain || !chain.length) { if (onComplete) onComplete(); return; }
	stepExpandChain(chain, 0, RootOicEmpIdsPreview, [], onComplete);
}

function stepExpandChain(chain, idx, ancestorOic, chainSoFar, onComplete) {
	if (idx >= chain.length) { if (onComplete) onComplete(); return; }
	var part = chain[idx].split('_');
	var letter = part[0];
	var id = part[1];
	var containerId = 'children_' + letter + '_' + id;
	var $container = $('#' + containerId);
	var $btn = $('#btn_' + letter + '_' + id);
	if (!$container.length) { if (onComplete) onComplete(); return; }

	$container.show();
	if ($btn.length) { $btn.html('<i class="fa fa-minus" aria-hidden="true"></i>'); }

	var parentFrom = $('#DateCompletion' + letter + id).val();
	var parentTo = $('#DateCompletion2' + letter + id).val();

	loadLevelInto($container, letter, id, ancestorOic, chainSoFar, parentFrom, parentTo, function () {
		// find this node's own OIC ids (now in the DOM) to extend the ancestor chain for the next hop
		var firstOic = $('#EmpId' + letter + id).val() || '';
		var secondOic = $('#EmpId1' + letter + id).val() || '';
		var nextAncestorOic = ancestorOic + ',' + firstOic + ',' + secondOic;
		var nextChain = chainSoFar.concat([chain[idx]]);
		stepExpandChain(chain, idx + 1, nextAncestorOic, nextChain, onComplete);
	});
}
</script>

</body>
</html>