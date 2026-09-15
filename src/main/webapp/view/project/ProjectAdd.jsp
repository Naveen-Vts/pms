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
<spring:url value="/resources/css/projectModule/projectAdd.css" var="projectAdd" />
<link href="${projectAdd}" rel="stylesheet" />

<title>PROJECT  ADD</title>
</head>
<body>
<%
SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
List<Object[]> ProjectMainListNotAdded=(List<Object[]>) request.getAttribute("ProjectMainListNotAdded");
List<Object[]> ProjectMainList=(List<Object[]>) request.getAttribute("ProjectTypeMainList");
List<Object[]> OfficerList=(List<Object[]>) request.getAttribute("OfficerList");
List<Object[]> CategoryList=(List<Object[]>) request.getAttribute("CategoryList");
List<Object[]> ProjectTypeList=(List<Object[]>) request.getAttribute("ProjectTypeList");
List<Object[]> ProjectCatSecDetalis=(List<Object[]>) request.getAttribute("ProjectCatSecDetalis");
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
<div class="card shadow-nohover" >
<div class="card-header"><h4>Project Add</h4>  </div>

<!-- -----------------------------------------------------------------------------------body -------------------------- -->
<div class="card-body" > 
<div class="row">


	<div class=" col-md-12 " >
			<table class="table table-bordered table-hover table-striped table-condensed cs-table">
				<tr>
					<th class="w-30">
						<label class="mb-n1p"> Is Main Project </label>
					</th>
					 <td class="w-30">
							<select  required="required" id="ismaincheck" class="form-control w-60"
							   onchange="validateismaincheck()">
								<option disabled="disabled" selected="selected" value="">Choose...</option>
								<option  value="Y">Yes</option>
								<option  value="N">No</option>
							</select>
					</td>
					</tr>
				</table>
	</div>
	<div class="col-md-12 disp-none" id="mainddlist">
				<form action="ProjectSubmit.htm" method="post">
						<table class="table table-bordered table-hover table-striped table-condensed cs-table1">
						 
							<tr>
							<th class="w-26">
								<label  class="mb-n1p"> Project Main: <span class="mandatory text-danger">*</span></label>
							</th>
							
							<td >
								<select name="projectmainid" class="form-control form-control selectdee" 
								data-width="100%" data-live-search="true" required="required">
									<option disabled="disabled" selected="selected" value="">Choose...</option>
									<%for(Object[] protype:ProjectMainListNotAdded ){%>
										<option value="<%=protype[0] %>"><%=protype[1]!=null?StringEscapeUtils.escapeHtml4(protype[1].toString()): " - " %></option>
									<%} %>
								</select>
							</td>
							<td align="center" >
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								<input type="hidden" name="action" value="mainaddsubmit" />
								<button type="submit" class="btn btn-sm submit" > SUBMIT</button>
							</td>
							
						</tr>
					</table>
			</form>
	</div>


<script type="text/javascript">
function validateismaincheck()
{
	var ismain=$('#ismaincheck').val();
	if(ismain==='Y')
		{
			$('#mainddlist').css('display', 'block');3
			$('#isnotmain').css('display', 'none');
		
		}else if(ismain==='N')
		{
			$('#isnotmain').css('display', 'block');
			 $('#mainddlist').css('display', 'none');
		}
}
</script>





<div class=" col-md-12 disp-none" id="isnotmain">
<form class="form-horizontal" role="form" action="ProjectSubmit.htm" method="POST">

<div class="tables">
  <div class="table-responsive">
  
	  
   <table class="table table-bordered table-hover table-striped table-condensed cs-table2">
<thead>


<tr>
<th>
<label class="mb-n1p"> Project Main: <span class="mandatory text-danger">*</span></label>
</th>
<td >
					<select required="required" id="selectbasic" name="projecttype" class="form-control form-control selectdee" data-width="100%" data-live-search="true" onchange="pmainchange();">
							<option disabled="disabled" selected="selected" value="">Choose...</option>
								<%for(Object[] protype:ProjectMainList ){
	
	                           %>
								<option value="<%=protype[0] %>"><%=protype[1]!=null?StringEscapeUtils.escapeHtml4(protype[1].toString()): " - " %></option>
								<%} %>
					</select>
</td>


  
<th>
<label class="mb-n1p"> Project Name:
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td>
<input type="text"   name="pname"  class="form-control alphanum-symbols-no-leading-space" maxlength="255" required="required">

</td>
</tr>
<tr>
<th>
<label class="mb-n1p"> Project Number:
<span class="mandatory text-danger">*</span>
</label>
</th>
<td>
<input type="text"  name="desc"	class="form-control alphanum-symbols-no-leading-space" placeholder="Enter Project No"	 maxlength="100" required="required">
 
</td>
<th>
<label class="mb-n1p"> Project Unit Code:
<span class="mandatory text-danger">*</span>
</label>
</th>
<td >
<input type="text"   name="unicode" placeholder="Enter Project Unit Code"		class="form-control alphanum-symbols-no-leading-space" maxlength="20" required="required" >
 
</td>
 </tr>
 
  <tr>
 <th>
<label class="mb-n1p"> Project Director:
<span class="mandatory text-danger">*</span>
</label>
</th>
<td>
<select required="required" name="projectdirector" class="form-control form-control selectdee w-100" id="officerPunchNo" data-width="100%" data-live-search="true">
						<option disabled="disabled" selected="selected" value="">Choose...</option>
							<%for(Object[] protype:OfficerList ){
	
	                           %>
								<option value="<%=protype[0] %>"><%=protype[2]!=null?StringEscapeUtils.escapeHtml4(protype[2].toString()): " - "%>, <%=protype[3]!=null?StringEscapeUtils.escapeHtml4(protype[3].toString()): " - " %></option>
								<%} %>
							</select>
</td>


<th>
<label class="mb-n1p"> Project Short Name:
<span class="mandatory text-danger">*</span>
</label>
</th>
<td>
 <input type="text"   name="projectshortname" placeholder="Enter Project Short Name" class="form-control alphanum-symbols-no-leading-space" maxlength="20" required="required">
</td>

</tr>
 
<tr>

  <th>
 <label class="mb-n1p"> End User:
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
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
<th>
 <label class="mb-n1p"> Project Code:
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
 <input type="text" name="pcode" placeholder="Enter Project Code" class="form-control alphanum-symbols-no-leading-space" maxlength="20" required="required">
 
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
		<option value="DIR" >DIR</option>
		<option value="DG" >DG</option>
		<option value="SECY" >SECY</option>
		<option value="RM" >RM</option>
		<option value="FM" >FM</option>
		<option value="CCS" >CCS</option>
</select>

</td>
<th>
<label class="mb-n1p"> Project Sanction Letter No:
<span class="mandatory text-danger">*</span>
</label>
</th>
<td >
<input type="text"   name="sano" class="form-control alphanum-symbols-no-leading-space" maxlength="100" required="required">
</td>
</tr>

<tr>
<th>
<label class="mb-n1p"> Project Sanction Date:
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
<input type="text"  data-date-format="dd/mm/yyyy"  readonly id="sanc-date" name="sadate" value=""
								class="form-control form-control">
</td>
  <th>
 <label class="mb-n1p">Total Sanction Cost (&#8377;):
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
<input type="text"   id="totalcostid" name="tsancost" value="0.0" class="form-control decimal-format" required="required">
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
				<option value="DMC" >DMC</option>
				<option value="CCM" >CCM</option>														
	</select>



</td>
  <th>
 <label class="mb-n1p"> Sanction Cost FE (&#8377;):
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
<input type="text"   id="fecostid" name="sancostfe"
								class="form-control decimal-format" value="0.0" maxlength="18" required="required">
</td>
 </tr>
 
 <tr>
	  <th><label class="mb-n1p">PDC:<span class="mandatory text-danger">*</span></label></th>
	  <td><input  id="pdc-date"  data-date-format="dd/mm/yyyy" readonly name="pdc" value="" class="form-control form-control"></td>
	  <th><label class="mb-n1p"> Sanction Cost RE (&#8377;):<span class="mandatory text-danger">*</span></label></th>
	  <td><input type="text"  name="sancostre" id="Recostid"class="form-control decimal-format" value="0.0" maxlength="18" value="0.0" readonly="readonly"></td>
</tr>
<tr>
   <th><label class="mb-n1p"> Category: <span class="mandatory text-danger">*</span></label></th>
   <td><select required="required" id="selectbasic1" class="form-control form-control selectdee" data-width="100%" data-live-search="true" disabled="disabled" >
			<%for (Object[] protype : ProjectTypeList) {%>
					<option value="<%=protype[0]%>" <%if(ProjectCatSecDetalis.size()>0 && protype[0].toString().equals(ProjectCatSecDetalis.get(0)[0].toString()) ){ %> selected="selected" <%} %>><%=protype[1]!=null?StringEscapeUtils.escapeHtml4(protype[1].toString()): " - "%></option>
			<%}%>
		</select>
	<input name="projectTypeID" id="projectTypeIDid" hidden="hidden" <%if(ProjectCatSecDetalis.size()>0){ %> value="<%=ProjectCatSecDetalis.get(0)[0].toString() %>" <%} %>>
	</td>                           
       <th><label class="mb-n1p"> Security Classification:<span class="mandatory text-danger">*</span></label></th>
 		<td>
			<select required="required"  class="form-control form-control selectdee" id="selectbasic2" 	data-width="80%" disabled="disabled">
				<%for(Object[] protype:CategoryList ){%>
					<option value="<%=protype[0]%>" <%if(ProjectCatSecDetalis.size()>0 && protype[0].toString().equals(ProjectCatSecDetalis.get(0)[1].toString()) ){ %> selected="selected" <%} %>><%=protype[1]!=null?StringEscapeUtils.escapeHtml4(protype[1].toString()): " - " %></option>
				<%}%>
			</select>
			<input name="projectcategory" id="projectcategoryID" hidden="hidden" <%if(ProjectCatSecDetalis.size()>0){ %> value="<%=ProjectCatSecDetalis.get(0)[1].toString() %>" <%} %>>
		</td>
</tr>
<!-- srikant code start -->
<tr>
<th><label class="mb-n1p">	Platform: <span class="mandatory text-danger">*</span></label></th>
<td><select required="required" name="platformName" class="form-control  form-control selectdee" id="platformName" data-width="100%"	data-live-search="true" >
<option disabled="disabled" value="" selected="selected">Choose..</option>
<% for (Object[] protype : PlatformList) {%>
<option value="<%=protype[0]%>"><%=protype[1]!=null?StringEscapeUtils.escapeHtml4(protype[1].toString()): " - "%></option>
<% } %>
</select></td>
</tr>
<!-- srikant code end -->
<tr>
	   <th colspan="1"><label class="mb-n1p">Nodal & Participating Lab:<span class="mandatory text-danger">*</span></label></th>
	  <td colspan="3"><input required="required" name="Nodal" placeholder="Enter Nodal & Participating Lab" class="form-control alphanum-symbols-no-leading-space" maxlength="250" /></td>
</tr>
<tr>
		<th colspan="1"><label class="mb-n1p">Application:<span class="mandatory text-danger">*</span></label></th>
		<td colspan="3"><textarea required="required" name="application" placeholder="Enter maximum 100 charcters " class="form-control " maxlength="100" rows="1" cols="53"></textarea></td>
</tr>
<tr>
		<th colspan="1"><label class="mb-n1p">Scope:<span class="mandatory text-danger">*</span></label></th>
		<td colspan="3"><textarea required="required" name="scope" class="form-control " placeholder="Enter maximum 5000 charcters " maxlength="5000" rows="3" cols="53"></textarea></td>
</tr>	
<tr>
       <th colspan="1"><label class="mb-n1p"> Objective:<span class="mandatory text-danger">*</span></label></th>
       <td colspan="3"><textarea required="required"  name="Objective" class="form-control " placeholder="Enter maximum 5000 charcters " maxlength="5000" rows="3" cols="53"></textarea></td>
</tr>
<tr>
       <th colspan="1"><label class="mb-n1p"> Deliverable:<span class="mandatory text-danger">*</span></label></th>
       <td colspan="3"><textarea required="required" name="Deliverable" class="form-control " placeholder="Enter maximum 5000 charcters "  maxlength="5000" rows="3" cols="53"></textarea></td>
</tr>
 </thead>
 </table>
 </div>
 </div>
				
	
								<div align="center">
									<table>
										<tr>
											<td>
												<button type="submit" name="action" value="submit" class="btn btn-primary btn-sm submit" onclick="return confirm('Are You Sure To Submit!')">Submit</button>
											</td>
											<td>
											    <a class="btn btn-info btn-sm shadow-nohover back" href="ProjectList.htm">Back</a>
											</td>
										</tr>
									</table>
								</div>
		
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

					</form>
				</div>
			</div>
		</div>
		
<!-- -----------------------------------------------------------------------------------body -------------------------- -->		

	</div>
</div>


</div>
  

<script type="text/javascript">

$('#currentdate,#datepicker').daterangepicker({
	"singleDatePicker" : true,
	"linkedCalendars" : false,
	"showCustomRangeLabel" : true,
	"startDate" : new Date(),

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
function pmainchange(){
	$.ajax({
        type: "get",
        url: "projectCatSencDetalis",
        data: {
			project : $('#selectbasic').val()
		},
		datatype : 'json',
        success: function(result){      
        	var values = JSON.parse(result);
        	$('#selectbasic1').val(values[0][0]).change();        	
        	console.log(values[0][0]);console.log(values[0][1]);
         	$('#selectbasic2').val(values[0][1]).change();
         	$('#projectTypeIDid').val(values[0][0]);
        	$('#projectcategoryID').val(values[0][1]); 	
        }
    });
}

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