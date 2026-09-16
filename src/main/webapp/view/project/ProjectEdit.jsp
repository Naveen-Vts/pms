<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.pfms.*,java.text.SimpleDateFormat,java.text.DecimalFormat"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/projectModule/projectEdit.css" var="projectEdit" />
<link href="${projectEdit}" rel="stylesheet" />
<title>Project Edit</title>

</head>
<body>
<%SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
List<Object[]> ProjectTypeMainList=(List<Object[]>) request.getAttribute("ProjectTypeMainList");
List<Object[]> OfficerList=(List<Object[]>) request.getAttribute("OfficerList");
Object[] ProjectEditData=(Object[]) request.getAttribute("ProjectEditData");
String ProjectId=(String)request.getAttribute("ProjectId");
List<Object[]> CategoryList=(List<Object[]>) request.getAttribute("CategoryList");
List<Object[]> ProjectTypeList=(List<Object[]>) request.getAttribute("ProjectTypeList");
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




<div class="row">
<div class="col-md-12">
<div class="card shadow-nohover" >
  <div class="card-header cs-header">
           <h3 class="text-white style1">Project Master Edit</h3>
	</div>
<div class="card-body"> 
<div class="row">
		<div class=" col-md-12 ">
			<!-- Calling AddUpdateProjectController to add New User Data -->
			<form class="form-horizontal" role="form"
				action="ProjectSubmit.htm" method="POST">
<input type="hidden" value="<%=ProjectId%>" name="ProjectId">
					<!-- Form Name -->
					<!-- Text input-->
					  <div class="tables">
  <div class="table-responsive">
  
	  
	   <table class="table table-bordered table-hover table-striped table-condensed cs-table">
  <thead>



<tr>
   <th>
 <label class="mb-n1p"> Project Main:
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
		<select required="required" id="selectbasicc" name="projecttype" class="form-control form-control selectdee" data-width="80%" data-live-search="true">
								<%for(Object[] protype:ProjectTypeMainList ){
	
	                           %>
								<option value="<%=protype[0] %>" <%if(protype[0].toString().equalsIgnoreCase(ProjectEditData[1].toString())){ %>
								 selected="selected"  <%} %>
								><%=protype[1]!=null?StringEscapeUtils.escapeHtml4(protype[1].toString()): " - " %></option>
								<%} %>
						</select>
</td>
  <th>
 <label class="mb-n1p"> Project Name:
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
<input type="text"   name="pname"  class="form-control alphanum-symbols-no-leading-space" value="<%=ProjectEditData[4]!=null?StringEscapeUtils.escapeHtml4(ProjectEditData[4].toString()): " - " %>" maxlength="255" required="required">

 
</td>
 </tr>
 
 <tr>
  <th>
 <label class="mb-n1p"> Project Number:
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
	<input type="text"  name="desc" value="<%=ProjectEditData[5]!=null?StringEscapeUtils.escapeHtml4(ProjectEditData[5].toString()): " - " %>"
								class="form-control alphanum-symbols-no-leading-space" maxlength="100" required="required">
 
</td>
  <th>
 <label class="mb-n1p"> Project Unit Code:
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
	<input type="text"   name="unicode" value="<%=ProjectEditData[6]!=null?StringEscapeUtils.escapeHtml4(ProjectEditData[6].toString()): " - " %>"
	class="form-control alphanum-symbols-no-leading-space" maxlength="20" required="required">
 
</td>
 </tr>
 
  <tr>

<th>
 <label class="mb-n1p"> Project Code:
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
 <%=ProjectEditData[3]!=null?StringEscapeUtils.escapeHtml4(ProjectEditData[3].toString()): " - " %>
 <input type="hidden"   name="pcode" class="form-control alphanum-symbols-no-leading-space" maxlength="20" value="<%=ProjectEditData[3] %>"  required="required">
 
</td>

    <th>
<label class="mb-n1p"> Project Short Name:
<span class="mandatory text-danger">*</span>
</label>
</th>
<td>
 <input type="text"   name="projectshortname" <%if(ProjectEditData[23]!=null){%> value="<%=StringEscapeUtils.escapeHtml4(ProjectEditData[23].toString()) %>"<%} %>	class="form-control alphanum-symbols-no-leading-space" maxlength="20" required="required">
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
							 
							<option value="IA" <%if("IA".equalsIgnoreCase(ProjectEditData[24]+"")){%> selected="selected" <%}%>>Indian Army</option>
							<option value="IN" <%if("IN".equalsIgnoreCase(ProjectEditData[24]+"")){%> selected="selected" <%}%>>Indian Navy</option>
							<option value="IAF" <%if("IAF".equalsIgnoreCase(ProjectEditData[24]+"")){%> selected="selected" <%}%>>Indian Air Force</option>
							<option value="DRDO" <%if("DRDO".equalsIgnoreCase(ProjectEditData[24]+"")){%> selected="selected" <%}%>>DRDO</option>
							<option value="IH" <%if("IH".equalsIgnoreCase(ProjectEditData[24]+"")){%> selected="selected" <%}%>>Home Land Security</option>
							<option value="OH" <%if("OH".equalsIgnoreCase(ProjectEditData[24]+"")){%> selected="selected" <%}%>>Others</option>
							
	</select>
</td>

   
   
  <th>
 <label class="mb-n1p"> Project Director:
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
			<select required="required" name="projectdirector" class="form-control form-control selectdee" id="officerPunchNo" data-width="100%" data-live-search="true">
				<%for(Object[] officer:OfficerList ){  %>
					<option value="<%=officer[0] %>"  <%if(officer[0].toString().equalsIgnoreCase(ProjectEditData[13].toString())){ %> selected="selected"  <%} %> ><%=officer[2]!=null?StringEscapeUtils.escapeHtml4(officer[2].toString()): " - " %>, <%=officer[3]!=null?StringEscapeUtils.escapeHtml4(officer[3].toString()): " - " %></option> 
				<%} %>
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
																	
					<option value="DIR" <%if(ProjectEditData[14]!=null && ProjectEditData[14].equals("DIR") ){ %>selected<%} %> >DIR</option>
					<option value="DG" <%if(ProjectEditData[14]!=null && ProjectEditData[14].equals("DG") ){ %>selected<%} %> >DG</option>
					<option value="SECY"  <%if(ProjectEditData[14]!=null && ProjectEditData[14].equals("SECY") ){ %>selected<%} %> >SECY</option>
					<option value="RM" <%if(ProjectEditData[14]!=null && ProjectEditData[14].equals("RM") ){ %>selected<%} %> >RM</option>
					<option value="FM" <%if(ProjectEditData[14]!=null && ProjectEditData[14].equals("FM") ){ %>selected<%} %> >FM</option>
					<option value="CCS" <%if(ProjectEditData[14]!=null && ProjectEditData[14].equals("CCS") ){ %>selected<%} %> >CCS</option>
		</select>
</td>
  <th>
 <label class="mb-n1p"> Project Sanction Letter No:
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
<input type="text"   name="sano"  value="<%=ProjectEditData[7]!=null?StringEscapeUtils.escapeHtml4(ProjectEditData[7].toString()): "" %>"
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
<input type="text" required="required"    id="totalcostid" name="tsancost"  class="form-control decimal-format" >
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
														<option value="DMC" <%if(ProjectEditData[15]!=null && ProjectEditData[15].toString().equalsIgnoreCase("DMC")){ %> selected<%} %>>DMC</option>
														<option value="CCM" <%if(ProjectEditData[15]!=null && ProjectEditData[15].toString().equalsIgnoreCase("CCM")){ %> selected<%} %>>CCM</option>														
									</select>
								
								
</td>
  <th>
 <label class="mb-n1p"> Sanction Cost FE (&#8377;):
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
<input type="text"   id="fecostid" name="sancostfe" value="<%=ProjectEditData[10] %>"
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
<input type="text"  name="sancostre" id="Recostid" value="<%=ProjectEditData[9]!=null?StringEscapeUtils.escapeHtml4(ProjectEditData[9].toString()): "" %>"
								class="form-control decimal-format"  maxlength="18" readonly="readonly" value="0.0">
</td>
 </tr>
    <tr>
    <th><label class="mb-n1p">
															Category: <span class="mandatory text-danger">*</span>
													</label></th>
													<td><select required="required" id="selectbasic1" 
														name="projectTypeID"
														class="form-control form-control selectdee"
														data-width="80%" data-live-search="true">
															<%
															for (Object[] protype : ProjectTypeList) {
															%>
															<option value="<%=protype[0]%>" <%if(ProjectEditData[22]!=null && protype[0].toString().equals(ProjectEditData[22].toString())) {%>selected="selected" <%} %>><%=protype[1]!=null?StringEscapeUtils.escapeHtml4(protype[1].toString()): " - "%></option>
															<%
															}
															%>
													</select>
													<input name="projectTypeID" id="projectTypeIDid" hidden="hidden"  value="<%=ProjectEditData[22]!=null?StringEscapeUtils.escapeHtml4(ProjectEditData[22].toString()): ""%>" >
													</td>
  <th>
 <label class="mb-n1p">Security Classification:
<span class="mandatory text-danger">*</span>
</label>
</th>
 <td >
		<select required="required" name="projectcategory" class="form-control form-control selectdee" id="selectbasic2" data-width="80%" data-live-search="true" >
			<%for(Object[] protype:CategoryList ){%>
				<option value="<%=protype[0] %>"
								 <%if(protype[0].toString().equalsIgnoreCase(ProjectEditData[21].toString())){ %>
								 selected="selected"  <%} %>><%=protype[1]!=null?StringEscapeUtils.escapeHtml4(protype[1].toString()): " - " %></option>
								<%}%>
		</select>			
</td>
</tr>
 <!-- srikant code start -->
								<tr>
										<th><label class="mb-n1p">	Platform: <span class="mandatory text-danger">*</span></label></th>
													<td><select required="required" id="platformName" name="platformName" class="form-control form-control selectdee" data-width="100%" data-live-search="true">
													<option disabled="disabled" value="" selected="selected">Choose..</option>
								<%for(Object[] protype:PlatformList ){%>
								<option value="<%=protype[0] %>" <%if(ProjectEditData[33].toString()!="0" && protype[0].toString().equalsIgnoreCase(ProjectEditData[33].toString())){ %>
								 selected="selected"  <%} %>
								><%=protype[1]!=null?StringEscapeUtils.escapeHtml4(protype[1].toString()): " - " %></option>
								<%}%>
						</select></td>
								</tr>
								
<!-- srikant code end-->
<tr>
	   <th colspan="1"><label class="mb-n1p">Nodal & Participating Lab:<span class="mandatory text-danger">*</span></label></th>
	  <td colspan="3"><input required="required" name="Nodal" placeholder="Enter Nodal & Participating Lab" class="form-control alphanum-symbols-no-leading-space" maxlength="250" <%if(ProjectEditData!=null && ProjectEditData[27]!=null){%>value="<%=StringEscapeUtils.escapeHtml4(ProjectEditData[27].toString())%>" <%}%> /></td>
</tr>
 <tr>
		<th colspan="1"><label class="mb-n1p">Application:<span class="mandatory text-danger">*</span></label></th>
		<td colspan="3"><textarea required="required" placeholder="Enter maximum 100 charcters " name="application" class="form-control " maxlength="100" rows="1" cols="53"><%=ProjectEditData[26]!=null?ProjectEditData[26].toString(): " - "%></textarea></td>
</tr>
<tr>
		<th colspan="1"><label class="mb-n1p">Scope:<span class="mandatory text-danger">*</span></label></th>
		<td colspan="3"><textarea required="required" name="scope" class="form-control " placeholder="Enter maximum 5000 charcters " maxlength="5000" rows="3" cols="53"><%=ProjectEditData[25]!=null?ProjectEditData[25].toString(): " - " %></textarea></td>
</tr>
 <tr>
		  <th colspan="1"><label class="mb-n1p">Objective:<span class="mandatory text-danger">*</span></label></th>
		  <td colspan="3"><textarea required="required"  name="Objective" class="form-control " placeholder="Enter maximum 5000 charcters " maxlength="5000" rows="3" cols="53"><%=ProjectEditData[19]!=null?ProjectEditData[19].toString(): " - " %></textarea></td>	 
 </tr>
  <tr>
		  <th colspan="1"><label class="mb-n1p">Deliverable:<span class="mandatory text-danger">*</span></label></th>
		  <td colspan="3"><textarea required="required" name="Deliverable" class="form-control " placeholder="Enter maximum 5000 charcters "  maxlength="5000" rows="3" cols="53"><%=ProjectEditData[20]!=null?ProjectEditData[20].toString(): " - " %></textarea></td>
 </tr>
 </thead>
 </table>
 </div>
 </div>
				

					
					<div align="center">
						<table>
							<tr>
								<td>
									<%if(Integer.parseInt(ProjectEditData[18].toString())==0){ %>
									<button type="submit" name="action" value="editsubmit" class="btn btn-primary btn-sm submit" onclick="return confirm('Are You Sure To Submit!')">SUBMIT</button>
									<%} %>
								</td>
								<td>
								    <a class="btn btn-info btn-sm shadow-nohover back" href="ProjectList.htm">BACK</a>
								</td>
							</tr>
						</table>
					</div>
		
 	 						
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

	</form>
	</div>
	</div></div></div></div>
</div>

<script>
function Edit(myfrm){
	
	 var fields = $("input[name='ProjectMainId']").serializeArray();

	  	if (fields.length === 0)
	  	{
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

$(document).ready(function () {
	
    var Recostid = $('#Recostid').val();
    var Fecostid = $('#fecostid').val();
    var Feid = parseFloat(Recostid.replace(/[^0-9.-]+/g,""));
    var Reid = parseFloat(Fecostid.replace(/[^0-9.-]+/g,""));
    var total =Feid+Reid ;
    $("#totalcostid").val(total);
    

});

$('#selectbasicc').on('change',function(){
	$.ajax({
        type: "get",
        url: "projectCatSencDetalis",
        data: {
			project : $('#selectbasicc').val()
		},
		datatype : 'json',
        success: function(result){      
        	var values = JSON.parse(result);
        	$("#selectbasic1").val(values[0][0]);
         	$("#selectbasic2").val(values[0][1]);
         	$('#projectTypeIDid').val(values[0][0]);
        	$('#projectcategoryID').val(values[0][1]); 	
        }
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

		$('#pdc-date').daterangepicker({
			
			"singleDatePicker": true,
			"showDropdowns": true,
			"cancelClass": "btn-default",
			"minDate":fromDate,
			"startDate":'<%=sdf.format(ProjectEditData[12]) %>	' ,
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
	"startDate":'<%=sdf.format(ProjectEditData[8]) %>', 
	locale: {
    	format: 'DD-MM-YYYY'
		}
});
</script>


</body>
</html>