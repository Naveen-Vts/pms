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
<spring:url value="/resources/css/projectModule/projectDataRevView.css" var="ExternalCSS" />     
<link href="${ExternalCSS}" rel="stylesheet" />
<title>Briefing </title>

<meta charset="ISO-8859-1">

</head>
<body >
<%

FormatConverter fc=new FormatConverter(); 
SimpleDateFormat sdf=fc.getRegularDateFormat();
SimpleDateFormat sdf1=fc.getSqlDateFormat(); 
int addcount=0; 
NFormatConvertion nfc=new NFormatConvertion();

List<Object[]> projectslist=(List<Object[]>)request.getAttribute("projectlist");
String projectid=(String)request.getAttribute("projectid");
String[] projectdatarevdata=(String[])request.getAttribute("projectdatarevdata");

List<String[]> projectdatarevlist=(List<String[]>)request.getAttribute("projectdatarevlist");
String projectdatarevid=(String)request.getAttribute("projectdatarevid");


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
				<div class="card shadow-nohover">
					<div class="row card-header">
			   			<div class="col-md-6">
							<h3>Project Revision Data </h3>
						</div>
						<!-- <div class="col-md-2"></div> -->						
						<div class="col-md-6 justify-content-end style1">
							<table class="style1" >
								<tr>
									<td ><h4>Project:</h4></td>
									<td >
										<form method="post" action="ProjectDataRevList.htm" id="projectchange">
											<select class="form-control items style2" name="projectid"  required="required" data-live-search="true" data-container="body" onchange="submitForm('projectchange');">
												<option disabled  selected value="">Choose...</option>
												<%for(Object[] obj : projectslist){ 
											     String projectshortName=(obj[17]!=null)?" ("+obj[17].toString()+") ":"";
												%>
												<%-- <option <%if(projectid.equals(obj[0].toString())){ %> selected <%} %> value="<%=obj[0] %>" ><%=obj[4] %></option> --%>
												<option <%if(projectid!=null && projectid.equals(obj[0].toString())) { %>selected <%} %>value="<%=obj[0]%>" ><%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - "%> <%=projectshortName!=null?StringEscapeUtils.escapeHtml4(projectshortName): " - " %></option>
												<%} %>
											</select>
											<button type="submit" class="btn  btn-sm back" formaction="ProjectData.htm" >Back</button>
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										</form>
									</td>
								</tr>
							</table>							
						</div>
					 </div>    
					 
				<%if(Long.parseLong(projectid)>0){ %>
					<div class="card-body">	
						<div class="row">		
						   <div class="col-md-12">
						   		
							    <%if(projectdatarevdata[3]!=null){ %>
							    <div class="row">
							    	<div class="col-md-12">
									    	<table class="style1"  >
											<tr>
												<td ><h4>Revision:</h4></td>
												<td >
									    		<form method="post" class="style1" action="ProjectDataRevList.htm" id="revchange">
													<select class="form-control items style2" name="projectdatarevid"  required="required"  data-live-search="true" data-container="body" onchange="submitForm('revchange');">
														<option disabled  selected value="">Choose...</option>
														<%for(Object[] obj : projectdatarevlist){ %>
														<option <%if(projectdatarevid.equals(obj[0].toString())){ %> selected <%} %> value="<%=obj[0] %>" >REV - <%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %> (<%=sdf.format(sdf1.parse(StringEscapeUtils.escapeHtml4(obj[3].toString())) )%>)</option>
														<%} %>
													</select>
													<input type="hidden" name="projectid" value="<%=projectid %>" >												
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
												</form>
												</td>
											</tr>
										</table>
							    </div>
							 </div>
							   
							    	<table  class="style3">
							    		<tr>
							    			<td colspan="4" align="center"><label>Revision Date: &nbsp;<%=projectdatarevdata[4]!=null?StringEscapeUtils.escapeHtml4(projectdatarevdata[4]): " - " %></label></td>
							    		</tr>
							    		<tr>
							    			<td>
							    				<form method="post" action="ProjectDataSystemSpecsRevFileDownload.htm" target="_blank">
							    					<label> System Configuration</label>
							    					<button  type="submit" class="btn btn-sm style4"  ><i class="fa fa-download fa-lg" ></i></button>
							    					<input type="hidden" name="projectdatarevid" value="<%=projectdatarevdata[3]%>"/>
							    					<input type="hidden" name="filename" value="sysconfig"/>
							    					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							    				</form>	
											</td>
							    			<td>
							    				<form method="post" action="ProjectDataSystemSpecsRevFileDownload.htm" target="_blank">
							    					<label> Product Tree</label>
							    					<button  type="submit" class="btn btn-sm style4"><i class="fa fa-download fa-lg" ></i></button>
							    					<input type="hidden" name="projectdatarevid" value="<%=projectdatarevdata[3]%>"/>
							    					<input type="hidden" name="filename" value="protree"/>
							    					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							    				</form>
							    			</td>
							    			<td>
							    				<form method="post" action="ProjectDataSystemSpecsRevFileDownload.htm" target="_blank">
							    					<label> PEARL/TRL</label>
							    					<button  type="submit" class="btn btn-sm style4" ><i class="fa fa-download fa-lg" ></i></button>
							    					<input type="hidden" name="projectdatarevid" value="<%=projectdatarevdata[3]%>"/>
							    					<input type="hidden" name="filename" value="pearl"/>
							    					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							    				</form>	
							    			</td>
							    			
							    		</tr>
							    		<%-- <tr>
							    			<td><figure class="fill"><img style="width: 10cm; height: 10cm" <%if(projectdatarevdata[0]!=null && projectdatarevdata[0].length()>0){ %> src="data:image/*;base64,<%=projectdatarevdata[0]%>" alt="Configuration"<%}else{ %> alt="File Not Found" <%} %> ></figure></td>
							    			<td><figure class="fill"><img style="width: 10cm; height: 10cm" <%if(projectdatarevdata[1]!=null && projectdatarevdata[1].length()>0){ %> src="data:image/*;base64,<%=projectdatarevdata[1]%>" alt="Product Tree/WBS"<%}else{ %> alt="File Not Found" <%} %>  ></figure></td>
							    			<td><figure class="fill"><img style="width: 10cm; height: 10cm" <%if(projectdatarevdata[2]!=null && projectdatarevdata[2].length()>0){ %> src="data:image/*;base64,<%=projectdatarevdata[2]%>"  alt="PEARL/TRL"<%}else{ %> alt="File Not Found" <%} %> ></figure></td>							    			
							    		</tr> --%>
							    		<tr>
							    			<td><label>Project Stage :</label>
							    				<%=projectdatarevdata[5]%>
							    			</td>
							    			<td>
							    				<form method="post" action="ProjectDataSystemSpecsRevFileDownload.htm" target="_blank">
							    					<label> System Specification</label>
							    					<button  type="submit" class="btn btn-sm style4" ><i class="fa fa-download fa-lg" ></i></button>
							    					<input type="hidden" name="projectdatarevid" value="<%=projectdatarevdata[3]%>"/>
							    					<input type="hidden" name="filename" value="sysspecs"/>
							    					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							    				</form>	
							    				
							    									    			
							    			</td>
							    		
							    		</tr>
							    		
							    		
							    	
							    		
							    								
									</table>
								<%}else{ %>
									<label>No Revision Data Found</label>
							    <%} %>
							  
							</div>
						</div>
					</div>
					
				<%} %>
				
				
					
					
				</div>
			</div>
		</div>
	</div>
<script type="text/javascript">

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
</script>


<!-- <script type="text/javascript">

$('.edititemsdd').select2();
$('.items').select2();
$("table").on('click','.tr_clone_addbtn' ,function() {
   $('.items').select2("destroy");        
   var $tr = $('.tr_clone').last('.tr_clone');
   var $clone = $tr.clone();
   $tr.after($clone);
   $('.items').select2();
   $clone.find('.items' ).select2('val', '');    
   $clone.find("input").val("").end();
   /* $clone.find("input:number").val("").end();
   	  $clone.find("input:file").val("").end() 
   */  
});
</script>
	 -->


<!-- <script type="text/javascript">

		var coll = document.getElementsByClassName("collapsiblediv");
		var i;
		
		for (i = 0; i < coll.length; i++) {
		  coll[i].addEventListener("click", function() {
		    this.classList.toggle("activea");
		    var content = this.nextElementSibling;
		    if (content.style.display === "block") {
		      content.style.display = "none";
		    } else {
		      content.style.display = "block";
		    }
		  });
		}
		
		

</script> -->


</body>
</html>