 <%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.math.BigDecimal"%> 
<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat,java.time.LocalDate"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>    
   
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/projectModule/projectDataView.css" var="ExternalCSS" />     
<link href="${ExternalCSS}" rel="stylesheet" />
<title>Briefing </title>

<meta charset="ISO-8859-1">

</head>
<body >
<%

FormatConverter fc=new FormatConverter(); 
SimpleDateFormat sdf=fc.getRegularDateFormat();
SimpleDateFormat sdf1=fc.getSqlDateFormat(); int addcount=0; 
NFormatConvertion nfc=new NFormatConvertion();

List<Object[]> projectslist=(List<Object[]>)request.getAttribute("projectlist");
String projectid=(String)request.getAttribute("projectid");
List<Object[]> projectstagelist=(List<Object[]>)request.getAttribute("projectstagelist");
String filesize=(String) request.getAttribute("filesize"); 
Object[] projectdatadetails=(Object[] )request.getAttribute("projectdatadetails");

String configimgb64=(String)request.getAttribute("configimgb64");
String producttree=  (String)request.getAttribute("producttree"); 
String pearlimg=  (String)request.getAttribute("pearlimg"); 


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
		<div class="row">
			<div class="col-md-12">
				<div class="card shadow-nohover style1">
					<div class="col-md-12">
						<div class="row card-header">
				   			<div class="col-md-6">
								<h3> Project Data </h3>
							</div>
										
							<div class="col-md-6 justify-content-end style2">
								<table class="style3" >
									<tr>
										<td ><h4>Project :</h4></td>
										<td >
											<form method="post" action="ProjectData.htm" id="projectchange">
												<select class="form-control items style4" name="projectid"  required="required" data-live-search="true" data-container="body" onchange="submitForm('projectchange');">
													<option disabled  selected value="">Choose...</option>
													<%for(Object[] obj : projectslist){ 
													String projectshortName=(obj[17]!=null)?" ("+obj[17].toString().trim()+") ":"";
													%>
													<option <%if(projectid!=null && projectid.equals(obj[0].toString())) { %>selected <%} %>value="<%=obj[0]%>" ><%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - "%> <%=projectshortName!=null?StringEscapeUtils.escapeHtml4(projectshortName): " - "%></option>     
													<%} %>
												</select>
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											</form>
										</td>
										<%if(Integer.parseInt(projectdatadetails[8].toString())>0){ %>
										<td>									
											<form action="ProjectDataRevList.htm" method="post" >
												<button type="submit" class="btn btn-sm edit" >Revision Data</button>
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								    			<input type="hidden" name="projectid" value="<%=projectid%>"/>	
											</form>
										</td>
										<%} %>
									</tr>
								</table>							
							</div>
						 </div>
					</div>
					 
				<%if(Long.parseLong(projectid)>0){ %>
					<div class="card-body">	
						<div class="row">		
						   <div class="col-md-12">
						   		
							    <form method="post" action="ProjectDataEditSubmit.htm" enctype="multipart/form-data" id="editrevform" >
							    	<table class="style5">
							    		<tr>
									    	<td>
									    		<label><b>1. System Configuration </b>  </label> 
									    		<%if(projectdatadetails[3]!=null){ %>
									    		<button  type="submit" class="btn btn-sm style6" name="filename" value="sysconfig" formaction="ProjectDataSystemSpecsFileDownload.htm" formtarget="_blank" ><i class="fa fa-download fa-lg" ></i></button>
									    		<button type="button" class="btn btn-sm ml-2"  onclick="showModal('sysconfig',<%=projectdatadetails[0]%>)"><img src="view/images/preview3.png"></button>
									    		</label>
									    		<%} %>
									    		<input class="form-control" type="file" name="systemconfig"  id="systemconfig" accept="application/pdf , image/* " onchange=" editcheck('systemconfig',1)" >
									    	</td>
										   
										
									    	<td>
									    		<label ><b>2. Product Tree</b>  </label>
									    		<%if(projectdatadetails[5]!=null){ %>
									    		<button  type="submit" class="btn btn-sm style6" name="filename" value="protree"  formaction="ProjectDataSystemSpecsFileDownload.htm" formtarget="_blank" ><i class="fa fa-download fa-lg" ></i></button></label>
									    		<button type="button" class="btn btn-sm ml-2" onclick="showModal('protree',<%=projectdatadetails[0]%>)"><img src="view/images/preview3.png"></button>
									    		<%} %>
									    		<input class="form-control" type="file" name="producttreeimg" id="producttreeimg" accept="application/pdf , image/* "  onchange=" editcheck('producttreeimg',1)" >
									    	</td>
									    	
									    	<td>
												<label><b>3. PEARL/TRL </b>
												<%if(projectdatadetails[6]!=null){ %>
												<button  type="submit" class="btn btn-sm style6" name="filename" value="pearl"  formaction="ProjectDataSystemSpecsFileDownload.htm" formtarget="_blank" ><i class="fa fa-download fa-lg" ></i></button></label>
												<button type="button" class="btn btn-sm ml-2" onclick="showModal('pearl',<%=projectdatadetails[0]%>)"><img src="view/images/preview3.png"></button>
												<%} %>  
												<input class="form-control" type="file" name="pearlimg" id="pearlimg" accept="application/pdf , image/* " onchange=" editcheck('pearlimg',1)" >
											</td>
									    </tr>
										<tr>
											<td>
										    	<label ><b>4. System Specification </b>
										    	<%if(projectdatadetails[4]!=null){ %>  
										    	<button  type="submit" class="btn btn-sm style6" name="filename" value="sysspecs"  formaction="ProjectDataSystemSpecsFileDownload.htm" formtarget="_blank" ><i class="fa fa-download fa-lg" ></i></button></label>
										    	<button class="btn btn-sm ml-2" type="button" onclick="showModal('sysspecs',<%=projectdatadetails[0]%>)"><img src="view/images/preview3.png"></button>
										    	<%} %>
										    	<input class="form-control" type="file" name="systemspecsfile" id="sysspec" accept="application/pdf , image/* "  onchange=" editcheck('sysspec',1)" >
										    	
										    </td>
											
											<td>
												<label ><b>5. Project Stage</b></label><br>
												<select class="form-control items style7" name="projectstageid"  required="required"  data-live-search="true" data-container="body" >
													<option disabled  selected value="">Choose...</option>
												<%for(Object[] obj : projectstagelist){ %>
													<option <%if(projectdatadetails[7].toString().equals(obj[0].toString())) { %>selected <%} %>  value=<%=obj[0]%> > <%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></option>
												<%} %>
												</select>
											</td>
											<td>
										    	<label ><b>6. Procurement Limit </b></label>
										    	<input class="form-control" type="number" name="proclimit" placeholder="Add Limit" min="500000" step="0.01"  <%if(projectdatadetails!=null && projectdatadetails[11]!=null){ %>value="<%=StringEscapeUtils.escapeHtml4(projectdatadetails[11].toString()) %>" <%} %>  >
										 		<!--<span >procurement level above which report will display</span> -->
										    </td>
										</tr>
										<tr>
											<td><label><b>7. Last PMRB Date </b> </label> <input
												type="text" data-date-format="dd/mm/yyyy" id="pmrc-date"
												name="pmrcdate" class="form-control form-control"></td>
											<td><label><b>8. Last EB Date </b> </label> <input
												type="text" data-date-format="dd/mm/yyyy" id="EB-date"
												name="ebdate" class="form-control form-control"></td>
										</tr>
										<tr>
											<td colspan="3">
												<span><b class="style8">Note :</b><br> &nbsp; 1) Please Upload Files in PDF and Image Files Only <br> &nbsp; 2) Please Upload Images With Aspect Ratio of 1920 X 1080</span>
											</td>
										</tr>
										<tr>
											<td colspan="3" align="center">
											
											<%if(Integer.parseInt(projectdatadetails[8].toString())==0){ %>
												<button type="submit" class="btn  btn-sm edit" onclick="return validate();"> EDIT</button>
											<%} %>
												<button type="button" class="btn  btn-sm revise " onclick="appendrev('editrevform');" > REVISE</button>
												<a class="btn  btn-sm  back"  href="MainDashBoard.htm"  >BACK</a>
											</td>
										</tr>
									</table>
									
							    	
							    	<input type="hidden" name="projectdataid" value="<%=projectdatadetails[0]%>"/>
							    	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							    	<input type="hidden" name="projectid" value="<%=projectid%>"/>
							    	<input type="hidden" name="revisionno" value="<%=projectdatadetails[8]%>"/>
							  </form>
							  
							  
							  
							</div>
						</div>
					</div>
					
				<%} %>
				
				
					
					
				</div>
			</div>
		</div>
	</div>
<div class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true" id="longdivmodal"  data-backdrop="static">
  <div class="modal-dialog modal-lg">
    
    <div class="modal-content mt-2 mb-2 style9" id="modalcontent">
     
    </div>
  </div>
</div>	
	
	
	
<script type="text/javascript">

var count=0;
function editcheck(editfileid,alertn)
{
	const fi = $('#'+editfileid )[0].files[0].size;							 	
   	const file = Math.round((fi / 1024/1024));
   
    
    if (document.getElementById(editfileid).files.length!=0 && file >= <%=filesize%> ) 
    {
    	if(alertn==1){
	    	
	     	alert("File too Big, please select a file less than <%=filesize%> mb");
    	}else
    	{
    		count++;
    	}
     	
    }
}


function validate()
{
	editcheck('systemconfig',0);
	editcheck('producttreeimg',0);
	editcheck('pearlimg',0);
	editcheck('sysspec',0);
	
	console.log(count);
	if(count==0){
		return confirm('Are you Sure to Add this Data?');
	}else
	{
		event.preventDefault();
		alert('one of your files is more then <%=filesize%> MB ');	
		count=0;
	}
	
	
}
function submitForm(frmid)
{ 
  document.getElementById(frmid).submit(); 
} 

$('.edititemsdd').select2();
$('.items').select2();
</script>

<script type="text/javascript">
function appendrev(frmid){
	 
	      $("<input />").attr("type", "hidden")
	          .attr("name", "rev")
	          .attr("value", "1")
	          .appendTo('#'+frmid);
	     if(confirm('Are You Sure To Revise?')){
	    	 $('#'+frmid).submit();
	     }
	      
	      
	 	
	}
	
	
$('#pmrc-date').daterangepicker({
	
	"singleDatePicker": true,
	"showDropdowns": true,
	"cancelClass": "btn-default",
	/* "minDate":new Date(), */
	"startDate": '<%if(projectdatadetails[12]!=null){  %><%=sdf.format(projectdatadetails[12]) %><%}%><% else %><%=LocalDate.now()%>', 
	locale: {
    	format: 'DD-MM-YYYY'
		}

});
$('#EB-date').daterangepicker({
	
	"singleDatePicker": true,
	"showDropdowns": true,
	"cancelClass": "btn-default",
	/* "minDate":new Date(), */
	"startDate":'<%if(projectdatadetails[13]!=null){  %><%=sdf.format(projectdatadetails[13]) %><%}%><% else %><%=LocalDate.now()%> ' ,
	locale: {
    	format: 'DD-MM-YYYY'
		}
});	

function showModal(a,b){
   
	$('#longdivmodal').modal('show');
	$.ajax({
	    type: 'GET',
	    url: 'ProjectDataAjax.htm',
	    xhrFields: {
	        responseType: 'blob' // Crucial: tells jQuery to handle binary data
	    },
	    data: { filename: a, projectdataid: b },
	    success: function(blob, status, xhr) {
	        // Get the extension we sent from the header
	        var extension = xhr.getResponseHeader('File-Extension').toLowerCase();
	        
	        // Create a temporary URL for the browser to use
	        var fileUrl = URL.createObjectURL(blob);
	        
	        var htmlContent = '<button type="button" class="close style10" data-dismiss="modal">&times;</button>';
	        
	        if (extension === "pdf") {
	            htmlContent += '<iframe width="100%" height="600" src="' + fileUrl + '"></iframe>';
	        } else {
	            htmlContent += '<img class="style11" src="' + fileUrl + '">';
	        }

	        $('#modalcontent').html(htmlContent);
	        $('#longdivmodal').modal('show');
	        
	        // Clean up memory after the modal is closed
	        $('#longdivmodal').on('hidden.bs.modal', function () {
	            URL.revokeObjectURL(fileUrl);
	        });
	    }
	});
}
</script>

</body>
</html>