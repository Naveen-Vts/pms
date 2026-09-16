<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.pfms.*,java.text.SimpleDateFormat,java.text.DecimalFormat"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/projectModule/projectMainEdit.css" var="projectMainEdit" />
<link href="${projectMainEdit}" rel="stylesheet" />
<title>Project Main Edit</title>

</head>
<body>

<%
SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
List<Object[]> ProjectTypeList=(List<Object[]>) request.getAttribute("ProjectTypeList");
List<Object[]> OfficerList=(List<Object[]>) request.getAttribute("OfficerList");
Object[] ProjectMainEditData=(Object[]) request.getAttribute("ProjectMainEditData");
String ProjectMainId=(String)request.getAttribute("ProjectMainId");
List<Object[]> securityClassificationList=(List<Object[]>) request.getAttribute("SecurityClassificationList");
List<Object[]> PlatformList=(List<Object[]>) request.getAttribute("PlatformList"); /* srikant */
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
<div class="row mt-n1p">
<div class="col-md-12">
<div class="card shadow-nohover" >

    <div class="card-header cs-header">
           <h3 class="text-white style1">Project Main Edit</h3>
	</div>


<div class="card-body"> 
<div class="row">
		<div class=" col-md-12 ">
			<!-- Calling AddUpdateProjectController to add New User Data -->
			<form class="form-horizontal" role="form"
				action="ProjectMainSubmit.htm" method="POST">
<input type="hidden" value="<%=ProjectMainId%>" name="ProjectMainId">
					<!-- Form Name -->
					<!-- Text input-->
					  <div class="tables">
  <div class="table-responsive">
 
	  
	   <table class="table table-bordered table-hover table-striped table-condensed cs-table">
  <thead>


<tr>
  <th>
 <label class="mb-n1p"> Project Code:
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
 <input type="text"   name="pcode"
								class="form-control alphanum-symbols-no-leading-space" maxlength="20" value="<%=ProjectMainEditData[3]!=null?StringEscapeUtils.escapeHtml4(ProjectMainEditData[3].toString()): "" %>" required="required">
 
</td>
  <th>
 <label class="mb-n1p"> Project Name:
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
<input type="text"   name="pname"  class="form-control alphanum-symbols-no-leading-space" value="<%=ProjectMainEditData[4]!=null?StringEscapeUtils.escapeHtml4(ProjectMainEditData[4].toString()): ""  %>" maxlength="255" required="required">

 
</td>
 </tr>
 
 <tr>
  <th>
 <label class="mb-n1p"> Project Number:
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
	<input type="text"  name="desc" value="<%=ProjectMainEditData[5]!=null?StringEscapeUtils.escapeHtml4(ProjectMainEditData[5].toString()): ""  %>"
								class="form-control alphanum-symbols-no-leading-space" maxlength="100" required="required">
 
</td>
  <th>
 <label class="mb-n1p"> Project Unit Code:
<span class="mandatory text-danger"></span>
</label>
</th>
 <td >
	<input type="text"   name="unicode" <%if(ProjectMainEditData[6]!=null){ %>value="<%=StringEscapeUtils.escapeHtml4(ProjectMainEditData[6].toString()) %>" <%} %>
								class="form-control alphanum-symbols-no-leading-space" maxlength="20">
 
</td>
 </tr>
 
 <tr>
	<th><label class="mb-n1p"> End User:<span class="mandatory text-danger">*</span></label></th>
		<td><select name="enduser" 	class="form-control  form-control selectdee" data-width="100%" data-live-search="true">
				<option disabled="disabled" selected="selected" value="">Choose...</option>
				<option value="IA" <%if(ProjectMainEditData[24]!=null && ProjectMainEditData[24].equals("IA")){ %> selected<%} %>>Indian Army</option>
				<option value="IN" <%if(ProjectMainEditData[24]!=null && ProjectMainEditData[24].equals("IN")){ %> selected<%} %>>Indian Navy</option>
				<option value="IAF" <%if(ProjectMainEditData[24]!=null && ProjectMainEditData[24].equals("IAF")){ %> selected<%} %>>Indian Air Force</option>
				<option value="IH" <%if(ProjectMainEditData[24]!=null && ProjectMainEditData[24].equals("IH")){ %> selected<%} %>>Home Land Security</option>
				<option value="DRDO" <%if(ProjectMainEditData[24]!=null && ProjectMainEditData[24].equals("DRDO")){ %> selected<%} %>>DRDO</option>
				<option value="OH" <%if(ProjectMainEditData[24]!=null && ProjectMainEditData[24].equals("OH")){ %> selected<%} %>>Others</option>
	</select></td>  
 <th><label class="mb-n1p"> Project Short Name:<span class="mandatory text-danger">*</span></label></th>
<td><input type="text"   name="projectshortname" placeholder="Enter Project Short Name" <%if(ProjectMainEditData!=null && ProjectMainEditData[26]!=null ){%>value="<%=StringEscapeUtils.escapeHtml4(ProjectMainEditData[26].toString())%>" <%}%>	class="form-control alphanum-symbols-no-leading-space" maxlength="20" required="required"></td> 
 </tr>
  <tr>
  <th>
 <label class="mb-n1p"> Category:
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
		<select required="required" id="selectbasic" name="projecttype" class="form-control form-control selectpicker" data-width="100%" data-live-search="true">
								<%for(Object[] protype:ProjectTypeList ){
	
	                           %>
								<option value="<%=protype[0] %>" <%if(protype[0].toString().equalsIgnoreCase(ProjectMainEditData[1].toString())){ %>
								 selected="selected"  <%} %>
								><%=protype[1] !=null?StringEscapeUtils.escapeHtml4(protype[1].toString()): " - "%></option>
								<%} %>
						</select>
</td>
<th><label class="mb-n1p">
															Security Classification: <span class="mandatory text-danger">*</span>
													</label></th>
													<td><select required="required" id="selectbasic"
														name="securityClassification"
														class="form-control form-control selectpicker"
														data-width="80%" data-live-search="true">
															<%
															for (Object[] protype : securityClassificationList) {													
															%>
															<option value="<%=protype[0]%>" <%if(ProjectMainEditData[22]!=null && protype[0].toString().equals(ProjectMainEditData[22].toString())){%> selected="selected" <%} %>><%=protype[1]!=null?StringEscapeUtils.escapeHtml4(protype[1].toString()): " - "%></option>
															<%
															}
															%>
													</select></td>

 </tr>
 
   <tr>
  <th>
 <label class="mb-n1p"> Project Director:
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
<select required="required" name="projectdirector" class="form-control form-control selectpicker" id="officerPunchNo" data-width="80%" data-live-search="true">
							<%for(Object[] protype:OfficerList ){
	
	                           %>
								<option value="<%=protype[0] %>"
								 <%if(protype[0].toString().equalsIgnoreCase(ProjectMainEditData[13].toString())){ %>
								 selected="selected"  <%} %>
								><%=protype[2]!=null?StringEscapeUtils.escapeHtml4(protype[2].toString()): " - "%>, <%=protype[3]!=null?StringEscapeUtils.escapeHtml4(protype[3].toString()): " - " %></option>
								<%} %>
							</select>
</td>
  <th>
 <label class="mb-n1p"> Is Main Work Center:
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
	<select name="ismainwc" class="form-control " data-width="100%" required="required">
							<option value="1"  <%if("1".equalsIgnoreCase(ProjectMainEditData[16].toString())){ %>
								 selected="selected"  <%} %>
							>Yes</option>
							<option value="0"
							 <%if("0".equalsIgnoreCase(ProjectMainEditData[16].toString())){ %>
								 selected="selected"  <%} %>
							>No</option>
							</select>
 
</td>

 </tr>
 
    <tr>
  <th>
 <label class="mb-n1p"> Project Sanc Authority:
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >

													<select required="required"
														name="ProjectsancAuthority"
														class="form-control  form-control selectdee"
														data-width="100%"
														data-live-search="true" >
														<option disabled="disabled" value="" selected="selected">Choose..</option>
															
															<option value="DIR" <%if(ProjectMainEditData[14]!=null && ProjectMainEditData[14].equals("DIR") ){ %>selected<%} %> >DIR</option>
															<option value="DG" <%if(ProjectMainEditData[14]!=null && ProjectMainEditData[14].equals("DG") ){ %>selected<%} %> >DG</option>
															<option value="SECY"  <%if(ProjectMainEditData[14]!=null && ProjectMainEditData[14].equals("SECY") ){ %>selected<%} %> >SECY</option>
															<option value="RM" <%if(ProjectMainEditData[14]!=null && ProjectMainEditData[14].equals("RM") ){ %>selected<%} %> >RM</option>
															<option value="FM" <%if(ProjectMainEditData[14]!=null && ProjectMainEditData[14].equals("FM") ){ %>selected<%} %> >FM</option>
															<option value="CCS" <%if(ProjectMainEditData[14]!=null && ProjectMainEditData[14].equals("CCS") ){ %>selected<%} %> >CCS</option>
															
													</select>


</td>
  <th>
 <label class="mb-n1p"> Project Sanction Letter No:
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
<input type="text"   name="sano"  value="<%=ProjectMainEditData[7] %>"
								class="form-control alphanum-symbols-no-leading-space" maxlength="100" required="required">
</td>
 </tr>
 
    <tr>
  <th>
 <label class="mb-n1p"> Project Sanction Date:
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
<input type="text"  data-date-format="dd/mm/yyyy"  readonly id="sanc-date" name="sadate"  value=""
								class="form-control form-control">
</td>

<th>
 <label class="mb-n1p">Total Sanction Cost (&#8377;):
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
<input type="text"   id="totalcostid" name="tsancost"
							class="form-control decimal-format" >
</td>


 </tr>
    <tr>
  <th>
 <label class="mb-n1p"> Board Of  Reference:
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
								
								<select required="required"
														name="bor"
														class="form-control  form-control selectdee"
														data-width="100%"
														data-live-search="true" >
														<option disabled="disabled" value="" selected="selected">Choose..</option>
														<option value="DMC" <%if(ProjectMainEditData[15]!=null && ProjectMainEditData[15].equals("DMC")){ %> selected<%} %>>DMC</option>
														<option value="CCM" <%if(ProjectMainEditData[15]!=null && ProjectMainEditData[15].equals("CCM")){ %> selected<%} %>>CCM</option>														
														</select>
								
								
</td>
  <th>
 <label class="mb-n1p"> Sanction Cost FE (&#8377;):
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
<input type="text"   id="fecostid" name="sancostfe" value="<%=ProjectMainEditData[10]!=null?StringEscapeUtils.escapeHtml4(ProjectMainEditData[10].toString()): ""  %>"
								class="form-control decimal-format"  maxlength="18" required="required">
</td>
 </tr>
 
 <tr>
  <th>
 <label class="mb-n1p">PDC:
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
<input  id="pdc-date"  data-date-format="dd/mm/yyyy" readonly name="pdc" value=""
								class="form-control form-control">
</td>

  <th>
 <label class="mb-n1p"> Sanction Cost RE (&#8377;):
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
<input type="text"  name="sancostre" id="Recostid" value="<%=ProjectMainEditData[9]!=null?StringEscapeUtils.escapeHtml4(ProjectMainEditData[9].toString()): ""  %>"  readonly="readonly"  
								class="form-control decimal-format"  maxlength="18" required="required">
</td>


 </tr>
  <!-- srikant code start -->
								<tr>
										<th><label class="mb-n1p">Platform: <span class="mandatory text-danger">*</span></label></th>
													<td><select required="required" id="platformName" name="platformName" class="form-control form-control selectdee" data-width="100%" data-live-search="true">
													<option disabled="disabled" value="" selected="selected">Choose..</option>
								<%for(Object[] protype:PlatformList ){%>
								<option value="<%=protype[0] %>" <%if(ProjectMainEditData[27].toString()!="0" && protype[0].toString().equalsIgnoreCase(ProjectMainEditData[27].toString())){ %>
								 selected="selected"  <%} %>
								><%=protype[1]!=null?StringEscapeUtils.escapeHtml4(protype[1].toString()): " - " %></option>
								<%}%>
						</select></td>
								</tr>
								
<!-- srikant code end-->
 <tr>
		<th colspan="1"><label class="mb-n1p">Nodal & Participating Lab:<span class="mandatory text-danger">*</span></label></th>
		<td colspan="3"><input required="required"	name="Nodal" class="form-control alphanum-symbols-no-leading-space" maxlength="5000" value="<%if(ProjectMainEditData[21]!=null ){%><%=StringEscapeUtils.escapeHtml4(ProjectMainEditData[21].toString()) %><%} %>" /></td>
</tr>
 <tr>
		<th colspan="1"><label class="mb-n1p">Application:<span class="mandatory text-danger">*</span></label></th>
		<td colspan="3"><textarea required="required" name="application" placeholder="Enter maximum 100 charcters " class="form-control " maxlength="100" rows="1" cols="53"><%if(ProjectMainEditData[25]!=null){%> <%=ProjectMainEditData[25].toString()%> <%}%></textarea></td>
</tr>
 <tr>
	<th colspan="1"><label class="mb-n1p">Scope:
		<span class="mandatory text-danger">*</span>
		</label>
	</th>
	<td colspan="3"><textarea required="required"
		name="scope" class="form-control " maxlength="5000"
			rows="3" cols="53"><%if(ProjectMainEditData[23]!=null){%><%=ProjectMainEditData[23].toString() %><%} %></textarea>
	</td>
</tr>		
 
 
 
 <tr>
  <th colspan="1">
 <label class="mb-n1p">Objective:
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td colspan="3">
	<textarea required="required"  name="Objective" 
	class="form-control " maxlength="5000" rows="5" cols="53"><%=ProjectMainEditData[19]!=null?ProjectMainEditData[19].toString(): ""  %></textarea>
								
							
</td>
 </tr>
  <tr>
  <th colspan="1">
 <label class="mb-n1p">Deliverable:
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td colspan="3">
	<textarea required="required" name="Deliverable" 
	class="form-control "  maxlength="5000" rows="5" cols="53"><%=ProjectMainEditData[20]!=null?ProjectMainEditData[20].toString(): ""  %></textarea>
								
								
</td>
 </tr>
 </thead>
 </table>
 </div>
 </div>
	
		<div align="center">
									<table>
										<tr>
											<td>
											   	<button type="submit" name="action" value="editsubmit" class="btn btn-primary btn-sm submit" onclick="return confirm('Are You Sure To Submit!')">SUBMIT</button>
									
											</td>
											<td>
											    <a class="btn btn-info btn-sm shadow-nohover back" href="ProjectMain.htm">BACK</a>
											</td>
										</tr>
									</table>
								</div>
 	 						
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

	</form>
	</div>
	</div></div></div></div>
</div>
</div>






<script>
function Edit(myfrm){
	
	 var fields = $("input[name='ProjectMainId']").serializeArray();

	  if (fields.length === 0){
		  bootbox.alert("PLEASE SELECT ONE PROJECT MAIN RECORD");
		  
		  
	 event.preventDefault();
	return false;
	}
	 
	
	
		 
	
		  return true;
	 
			
	}
	
$('#currentdate,#datepicker').daterangepicker({
	"singleDatePicker" : true,
	"linkedCalendars" : false,
	"showCustomRangeLabel" : true,

	"cancelClass" : "btn-default",
	showDropdowns : true,
	locale : {
		format : 'DD-MM-YYYY'
	}
});


$(document).ready(function () {
	
	$('#totalcostid,#fecostid').on('change', function () {
		   var tcost = $('#totalcostid').val();      
	       var fcost = $('#fecostid').val();
	       var tid = parseFloat(tcost.replace(/[^0-9.-]+/g,""));
	       var fid = parseFloat(fcost.replace(/[^0-9.-]+/g,""));
	       var total =tid-fid ;
	       $("#Recostid").val(total);
	      
	   });
});
$(document).ready(function () {
	
    var Recostid = $('#Recostid').val();
    var Fecostid = $('#fecostid').val();
    var Feid = parseFloat(Recostid.replace(/[^0-9.-]+/g,""));
    var Reid = parseFloat(Fecostid.replace(/[^0-9.-]+/g,""));
    var total =Feid+Reid ;
    $("#totalcostid").val(total);
    

});
$(function(){

	  $('.number-only').keypress(function(e) {
		  if( e.keyCode === 8  ) {
		        return true; // backspace (8) / delete (46)
		    }
		if(isNaN(this.value+""+String.fromCharCode(e.charCode))) return false;
	  })
	  .on("cut copy paste",function(e){
		e.preventDefault();
	  });

	});
</script>

<script type="text/javascript">

var fromDate=null;
$("#sanc-date").change( function(){	
	 fromDate = $("#sanc-date").val();

		$('#pdc-date').daterangepicker({
			
			"singleDatePicker": true,
			"showDropdowns": true,
			"cancelClass": "btn-default",
			"minDate":fromDate,
			"startDate":'<%=sdf.format(ProjectMainEditData[12]) %>	' ,
			locale: {
		    	format: 'DD-MM-YYYY'
				}
		});
		
});

$('#sanc-date').daterangepicker({
	
	"singleDatePicker": true,
	"showDropdowns": true,
	"cancelClass": "btn-default",
	/* "minDate":new Date(), */
	"startDate":'<%=sdf.format(ProjectMainEditData[8]) %>', 
	locale: {
    	format: 'DD-MM-YYYY'
		}
});
</script>



</body>
</html>