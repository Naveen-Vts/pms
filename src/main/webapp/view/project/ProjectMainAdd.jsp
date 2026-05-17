<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@page import="java.text.DecimalFormat"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat"%>
 <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/projectModule/projectMainAdd.css" var="projectMainAdd" />
<link href="${projectMainAdd}" rel="stylesheet" />
<title>PROJECT MAIN ADD</title>

</head>
<body>
<%
SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
List<Object[]> ProjectTypeList=(List<Object[]>) request.getAttribute("ProjectTypeList");
List<Object[]> OfficerList=(List<Object[]>) request.getAttribute("OfficerList");
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
		<div class="col-md-12">
			<div class="card shadow-nohover">

  <div class="card-header cs-header">
                    <h3 class="text-white style1">Project Main Add</h3>
        		</div>

				<div class="card-body">
					<div class="row">
						<div class=" col-md-12 ">
							<!-- Calling AddUpdateProjectController to add New User Data -->
							<form class="form-horizontal" role="form"
								action="ProjectMainSubmit.htm" method="POST">

								<!-- Form Name -->
								<!-- Text input-->
								<div class="tables">
									<div class="table-responsive">

										<table class="table table-bordered table-hover table-striped table-condensed cs-table">
											<thead>
												<tr>
													<th><label class="mb-n10">Project Code: <span class="mandatory text-danger">*</span></label></th>
													<td><input type="text" name="pcode" placeholder="Enter Project Code"class="form-control alphanum-symbols-no-leading-space" maxlength="20" required="required"></td>
													<th><label class="mb-n10">Project Name: <span class="mandatory text-danger">*</span></label></th>
													<td><input type="text" name="pname" placeholder="Enter Project Name" class="form-control alphanum-symbols-no-leading-space" maxlength="255" required="required"></td>
												</tr>

												<tr>
													<th><label class="mb-n10">Project Number: <span class="mandatory text-danger">*</span></label></th>
													<td><input type="text" name="desc" placeholder="Enter Project No" class="form-control alphanum-symbols-no-leading-space" maxlength="100" required="required"></td>
													<th><label class="mb-n10">Project Unit Code: <span class="mandatory text-danger"></span></label></th>
													<td><input type="text" name="unicode" placeholder="Enter Project Unit Code" class="form-control alphanum-symbols-no-leading-space" maxlength="20" ></td>
												</tr>
												
												<tr>
													<th><label class="mb-n10"> End User:<span class="mandatory text-danger">*</span></label></th>
													<td>
														<select name="enduser" 	class="form-control  form-control selectdee" data-width="100%" data-live-search="true">
															<option disabled="disabled" selected="selected" value="">Choose...</option>
															<option value="IA">Indian Army</option>
															<option value="IN">Indian Navy</option>
															<option value="IAF">Indian Air Force</option>
															<option value="IH">Home Land Security</option>
															<option value="DRDO">DRDO</option>
															<option value="OH">Others</option>
														</select>
													</td>
													<th><label  class="mb-n10"> Project Short Name:<span class="mandatory text-danger">*</span></label></th>
													<td><input type="text"   name="projectshortname" placeholder="Enter Project Short Name"	class="form-control alphanum-symbols-no-leading-space" maxlength="20" required="required"></td> 
												</tr>
												<tr>
													<th><label  class="mb-n10">Category: <span class="mandatory text-danger">*</span></label></th>
													<td><select required="required" id="selectbasic"name="projecttype" class="form-control form-control selectpicker" data-width="80%" data-live-search="true">
														<option disabled="disabled" value="" selected="selected">Choose..</option>
															<%
															for (Object[] protype : ProjectTypeList) {
															%>
															<option value="<%=protype[0]%>"><%=protype[1]!=null?StringEscapeUtils.escapeHtml4(protype[1].toString()): " - "%></option>
															<%
															}
															%>
													</select></td>
													
													<th> <label  class="mb-n10">Security Classification: <span class="mandatory text-danger">*</span> </label></th>
													<td><select required="required" id="selectbasic"	name="securityClassification" class="form-control form-control selectpicker" data-width="80%" data-live-search="true">
															<option disabled="disabled" value="" selected="selected">Choose..</option>
															<%
															if(securityClassificationList!=null && securityClassificationList.size()>0){ 
																for (Object[] protype : securityClassificationList) 
															{
															%>
															<option value="<%=protype[0]%>"><%=protype[1]!=null?StringEscapeUtils.escapeHtml4(protype[1].toString()): " - "%></option>
															<%
															}}
															%>
														</select>
													</td>												
												</tr>
												<tr>
										              
													
													<th><label  class="mb-n10">	Project Director: <span class="mandatory text-danger">*</span></label></th>
													<td><select required="required" name="projectdirector" class="form-control  form-control selectdee" id="officerPunchNo" data-width="100%"	data-live-search="true" >
															<option disabled="disabled" value="" selected="selected">Choose..</option>
															<%
															if(OfficerList!=null && OfficerList.size()>0){for (Object[] protype : OfficerList) {
															%>
															<option value="<%=protype[0]%>"><%=protype[2]!=null?StringEscapeUtils.escapeHtml4(protype[2].toString()): " - "%>, <%=protype[3]!=null?StringEscapeUtils.escapeHtml4(protype[3].toString()): " - " %></option>
															<%
															}}
															%>
													</select></td>
													<th><label  class="mb-n10">Is Main Work Center: <span class="mandatory text-danger">*</span></label></th>
													<td><select name="ismainwc"	class="form-control selectpicker" data-width="80%" required="required">
														<option disabled="disabled" value="" selected="selected">Choose..</option>
															<option value="1">Yes</option>
															<option value="0">No</option>
													</select></td>
												</tr>
												<tr>
													<th><label class="mb-n10">	Project Sanc Authority: <span class="mandatory text-danger">*</span></label></th>
													<td><select required="required" name="ProjectsancAuthority" class="form-control  form-control selectdee" data-width="100%" data-live-search="true" >
														<option disabled="disabled" value="" selected="selected">Choose..</option>
															<option value="DIR" >DIR</option>
															<option value="DG" >DG</option>
															<option value="SECY" >SECY</option>
															<option value="RM" >RM</option>
															<option value="FM" >FM</option>
															<option value="CCS" >CCS</option>
														</select>
															
													</td>
													<th><label  class="mb-n10">Project Sanction Letter No: <span class="mandatory text-danger">*</span></label></th>
													<td><input type="text" name="sano" placeholder="Enter Project Sanc Letter No" class="form-control" maxlength="100" required="required"></td>
												</tr>

												<tr>
													<th><label class="mb-n10">Project Sanction Date: <span class="mandatory text-danger">*</span></label></th>
													<td><input type="text" data-date-format="dd/mm/yyyy"readonly id="sanc-date" name="sadate" value=""class="form-control form-control"></td>
													<th><label  class="mb-n10">Total Sanction Cost (&#8377;): <span class="mandatory text-danger">*</span></label></th>
													<td><input type="text" id="totalcostid" name="tsancost" class="form-control decimal-format"  value="0.0" ></td>
												</tr>

												<tr>
													<th><label  class="mb-n10">Board Of Reference: <span class="mandatory text-danger">*</span></label></th>
													<td><select required="required" name="bor" class="form-control  form-control selectdee" data-width="100%" data-live-search="true" >
															<option disabled="disabled" value="" selected="selected">Choose..</option>
															<option value="DMC">DMC</option>
															<option value="CCM">CCM</option>
														</select>
													</td>
													<th><label  class="mb-n10">Sanction Cost FE (&#8377;): <span class="mandatory text-danger">*</span></label></th>
													<td><input type="text" id="fecostid" name="sancostfe"class="form-control decimal-format" value="0.0"	maxlength="18" required="required"></td>
												</tr>
								<tr>
										<th><label  class="mb-n10">PDC:<span class="mandatory text-danger">*</span></label></th>
										<td><input id="pdc-date" data-date-format="dd/mm/yyyy" readonly name="pdc" value="" class="form-control form-control"></td>
										<th><label  class="mb-n10">Sanction Cost RE (&#8377;): <span class="mandatory text-danger">*</span></label></th>
										<td><input type="text" name="sancostre" id="Recostid" class="form-control decimal-format" value="0.0" maxlength="18" required="required"  readonly="readonly" ></td>
								</tr>
								<!-- srikant code start -->
								<tr>
										<th><label  class="mb-n10">Platform: <span class="mandatory text-danger">*</span></label></th>
													<td><select required="required" name="platformName" class="form-control  form-control selectdee" id="platformName" data-width="100%"	data-live-search="true" >
															<option disabled="disabled" value="" selected="selected">Choose..</option>
															<%
															for (Object[] protype : PlatformList) {
															%>
															<option value="<%=protype[0]%>"><%=protype[1]!=null?StringEscapeUtils.escapeHtml4(protype[1].toString()): " - "%></option>
															<%
															}
															%>
													</select></td>
								</tr>
								<!-- srikant code end -->		
								<tr>
									   <th colspan="1"><label  class="mb-n10">Nodal & Participating Lab:<span class="mandatory text-danger">*</span></label></th>
									   <td colspan="3"><input required="required" name="Nodal" placeholder="Enter Nodal & Participating Lab" class="form-control alphanum-symbols-no-leading-space" maxlength="250" /></td>
								</tr>
								<tr>
										<th colspan="1"><label  class="mb-n10">Application:<span class="mandatory text-danger">*</span></label></th>
										<td colspan="3"><textarea required="required" name="application" placeholder="Enter maximum 50 charcters " class="form-control " maxlength="49" rows="1" cols="53"></textarea></td>
								</tr>
								<tr>
										<th colspan="1"><label  class="mb-n10">Scope:<span class="mandatory text-danger">*</span></label></th>
										<td colspan="3"><textarea required="required" placeholder="Enter maximum 5000 charcters" name="scope" class="form-control " maxlength="5000" rows="3" cols="53"></textarea></td>
								</tr>		
								<tr>
									<th colspan="1"><label  class="mb-n10">Objective:<span class="mandatory text-danger">*</span></label></th>
									<td colspan="3"><textarea required="required" name="Objective" placeholder="Enter maximum 5000 charcters" class="form-control " maxlength="5000"rows="3" cols="53"></textarea></td>
								</tr>
								<tr>
									<th colspan="1"><label  class="mb-n10">Deliverable:<span class="mandatory text-danger">*</span></label></th>
									<td colspan="3"><textarea required="required"	name="Deliverable" class="form-control " placeholder="Enter maximum 5000 charcters" maxlength="5000" rows="3" cols="53"></textarea></td>
								</tr>
											</thead>
										</table>
									</div>
								</div>

								<div align="center">
									
									<table>
										<tr>
											<td>
												<button type="submit" name="action" value="save" class="btn btn-primary btn-sm submit cs-save" onclick="return confirm('Are You Sure To Save!')">SAVE</button>
												<button type="submit" name="action" value="submit" class="btn btn-primary btn-sm submit" onclick="return confirm('Are You Sure To Submit!')">SUBMIT</button>
												
											</td>
											<td>
											    <a class="btn btn-info btn-sm shadow-nohover back" href="ProjectMain.htm">Back</a>
											</td>
										</tr>
									</table>

								</div>
	

								<input type="hidden" name="${_csrf.parameterName}"
									value="${_csrf.token}" />

							</form>
						</div>
					</div>
				</div>
			</div>
		</div>


	</div>
  

	
<script type="text/javascript">
function Edit(myfrm){
	
	 var fields = $("input[name='ProjectMainId']").serializeArray();

	  if (fields.length === 0){
		  bootbox.alert("PLEASE SELECT ONE PROJECT MAIN RECORD");
		  
		  
	 event.preventDefault();
	return false;
	}
	 
	
	
		 
	
		  return true;
	 
			
	}

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
	 
	var date1=fromDate.split("-");
	var date= new Date(date1[1]+' '+date1[0]+' '+date1[2]);
	
	
		
		$('#pdc-date').daterangepicker({
			
			"singleDatePicker": true,
			"showDropdowns": true,
			"cancelClass": "btn-default",
			"minDate":fromDate,
			"startDate":date ,
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
	"startDate":new Date(), 
	locale: {
    	format: 'DD-MM-YYYY'
		}
});
</script>


</body>
</html>