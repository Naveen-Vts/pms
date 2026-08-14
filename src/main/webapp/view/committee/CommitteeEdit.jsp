<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat"%>
    <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
<spring:url value="/resources/ckeditor/ckeditor.js" var="ckeditor" />
<spring:url value="/resources/ckeditor/contents.css" var="contentCss" />
<!DOCTYPE html>
<html>

<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/committeeModule/CommitteeEdit.css" var="CommitteeEdit" />
<link href="${CommitteeEdit}" rel="stylesheet" />
<script src="${ckeditor}"></script>
<link href="${contentCss}" rel="stylesheet" />
<title> COMMITTEE EDIT</title>	
</head>
<body>

<%
Object[] committeedetails=(Object[])request.getAttribute("committeedetails"); 
String projectid=committeedetails[12].toString();
Object[] projectdetails=(Object[])request.getAttribute("projectdetails"); 
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

    <br />
    
    
	<div class="container containerWidth">
		<div class="row">
			<div class="col-md-12">			
				<div class="card shadow-nohover">
					<div class="card-header cardHeaderStyle">
						<div class="row">
							<div class="col-md-6">
								<b class="text-white">EDIT COMMITTEE</b>
							</div>
							<div class="col-md-6">
								<%if(Long.parseLong(projectid)>0){ %>
								<div align="right"><b class="text-white"  >Project : <%=projectdetails[4]!=null?StringEscapeUtils.escapeHtml4(projectdetails[4].toString()): " - " %></b></div>
								<%} %>
							</div>
						</div>
					</div>

					<div class="card-body">
						<form action="CommitteeEditSubmit.htm" method="post" name="editfrm" id="editfrm" >
							<div class="row">
							
								<div class="col-md-4">
									<div class="form-group">
										<label class="control-label">Committee Code</label>
										<input class="form-control" type="text" name="committeeshortname" required value="<%=committeedetails[1]!=null?StringEscapeUtils.escapeHtml4(committeedetails[1].toString()): ""%>" maxlength="20">
									</div>
								</div>
							
							
								<div class="col-md-4">
									<div class="form-group">
										<label class="control-label">Committee Name</label>
										<input class="form-control" type="text" name="committeename" required value="<%=committeedetails[2]!=null?StringEscapeUtils.escapeHtml4(committeedetails[2].toString()): ""%>" maxlength="255">
									</div>
								</div>

							
							<div class="col-md-4">
								<div class="form-group">
									<label class="control-label">Committee Type</label>
									<select class="custom-select mt-n5" id="secretary" required="required" name="committeetype">
									
										<option <%if(committeedetails[3].toString().equalsIgnoreCase("S")){ %>selected <%} %> value="S">Standard</option>
										<option <%if(committeedetails[3].toString().equalsIgnoreCase("A")){ %>selected <%} %>value="A">Adhoc</option>
									</select>
								</div>
							</div>
						
						
						</div>
						
						<div class="row">
						
						 <div class="col-md-2">
								<div class="form-group">
									<label class="control-label">Project Applicable</label>
									<select class="custom-select mt-n5" id="projectapplicable" required="required" name="projectapplicable">
										<%if(committeedetails[4].toString().equalsIgnoreCase("P")){ %>
										<option selected value="P">Project</option>
										<option   value="N">Non-Project</option>
										<%}else if(committeedetails[4].toString().equalsIgnoreCase("N")){ %>
										<option  value="P">Project</option>
										<option selected  value="N">Non-Project</option>
										<%} %>
									</select>
									<input type="hidden" name="projectid" value="<%=committeedetails[12]%>">
								</div>
							</div> 
							
							
							
							<div class="col-md-2">
								<div class="form-group">
									<label class="control-label">Tech / Non-Tech</label>
									<select class="custom-select mt-n5" id="" required="required" name="technontech">
										
										<option <%if(committeedetails[5].toString().equalsIgnoreCase("T")){ %>selected <%} %> value="T">Technical</option>
										<option  <%if(committeedetails[5].toString().equalsIgnoreCase("N")){ %>selected <%} %> value="N">Non-Technical</option>
									</select>
								</div>
							</div>
							
							<div class="col-md-2">
								<div class="form-group">
									<label class="control-label">Periodic / Non-Periodic</label>
									<select class="custom-select mt-n5" id="periodic"  name="periodic">
										
										<option <%if(committeedetails[7].toString().equalsIgnoreCase("P")){ %>selected <%} %> value="P">Periodic</option>
										<option <%if(committeedetails[7].toString().equalsIgnoreCase("N")){ %>selected <%} %> value="N">Non-Periodic</option>
									</select>
								</div>
							</div>
							<div  <%if(!committeedetails[7].toString().equalsIgnoreCase("P")){ %> class="col-md-2 displayNone" <%}else{ %> class="col-md-2" <%} %>id="periodicduration">
								<div class="form-group">
									<label class="control-label">Periodic Duration (Days)</label>
									<input class="form-control numeric-only mt-n5" type="number" id="periodicdurationfield" name="periodicduration" required min="1" placeholder="Days" <%if(committeedetails[7].toString().equalsIgnoreCase("P")){ %> value="<%=committeedetails[8]!=null?StringEscapeUtils.escapeHtml4(committeedetails[8].toString()): ""%>" <%} %>>
								</div>
							</div>
							<div class="col-md-2">
								<div class="form-group">
									<label class="control-label">Is Briefing<span class="mandatory text-danger">*</span></label>
									<select class="custom-select mt-n5" id="IsBriefing" required="required" name="IsBriefing" >
										<option disabled value="">Choose...</option>
										<option <%if(committeedetails[14].toString().equalsIgnoreCase("N")){ %>selected <%} %> value="N">No</option>
										<option <%if(committeedetails[14].toString().equalsIgnoreCase("Y")){ %>selected <%} %> value="Y">Yes</option>
									</select>
								</div>
							</div>
						</div>	
						
						<div class="row">
							<%-- <div class="col-md-3">
								<div class="form-group">
									<label class="control-label">Project </label>
								
								  		<select class="form-control" name="projectid" required="required" id="projectid" >
											<option <%if(committeedetails[12].toString().equalsIgnoreCase("0")){ %> selected <%} %> value="0">Non - Project</option>
											<%for(Object[] obj : projectslist){ %>
											<option <%if(committeedetails[12].toString().equalsIgnoreCase(obj[0].toString())){ %> selected <%} %> value="<%=obj[0]%>"><%=obj[2] %></option>
											<%} %>
									  	</select>
								  	
								</div>
							</div> --%>
							<div class="col-md-12">
								
								<div class="form-group">
									<label class="control-label">Guidelines</label>
									<input class="form-control" type="text" name="guidelines" required value="<%if(committeedetails[6]!=null){ %><%=StringEscapeUtils.escapeHtml4(committeedetails[6].toString())%><%} %>" maxlength="255">
									
								</div>

							</div>
						</div>
						<div class="row">
							<div class="col-md-6">
								
								<div class="form-group">
									<label class="control-label">Purpose</label>
									<%-- <textarea class="form-control"  name="description" required placeholder="Enter Description" rows="5" cols="50" maxlength="1000"><%if(committeedetails[10]!=null){ %><%=committeedetails[10]%><%} %></textarea> --%>
								
									<div id="Editordescription" class="center">
									<%if(committeedetails[10]!=null){ %><%=committeedetails[10].toString()%><%} %>
															<textarea name="description"  id="description" ></textarea>
														</div>
								
								</div>

							</div>
							<div class="col-md-6">

									<div class="form-group">
										<label class="control-label">Terms Of Reference </label>
										<%-- 									<textarea class="form-control"  name="TOR" required placeholder="Enter Terms Of Reference" rows="5" cols="50" maxlength="1000"><%if(committeedetails[11]!=null){ %><%=committeedetails[11]%><%} %></textarea>
 --%>
										<div id="EditorReference" class="center">
										<%if(committeedetails[11]!=null){ %><%=committeedetails[11].toString()%><%} %>
											<textarea name="TOR" id="TOR"></textarea>
										</div>

									</div>

								</div>
						</div>
						
						
						
						
						<input type="hidden" name="committeeid" value="<%=committeedetails[0]%>">
					
					
					<div class="row" >
						<div class="col-md-5"></div>
						<div class="col-md-3">
							<div class="form-group">
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								<button class="btn btn-primary btn-sm submit" type="submit"  name="submit" value="SUBMIT"   onclick="return submitfrm()" >SUBMIT</button>
								<input type="hidden" name="id" value="<%=committeedetails[4]%>">							
								<button class="btn btn-primary btn-sm back" type="button"  onclick="submitForm('backfrm');">BACK</button>
							</div>
						</div>
					</div>
					</form>		
								
					<form method="post" action="CommitteeList.htm" name="backfrm" id="backfrm">
						<input type="hidden" name="projectid" value="<%=committeedetails[12]%>">
						<input type="hidden" name="projectappliacble" value="<%=committeedetails[4]%>">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />					
					</form>				
				</div>					
							
			</div>
			<div class="card-footer cardFooterStyle">
			</div>
		</div>
	</div>
</div>
	
		
		
<script type="text/javascript">
$('#projectid').select2();

function submitfrm(){
	 var data1 =CKEDITOR.instances['Editordescription'].getData();
	 var data2 =CKEDITOR.instances['EditorReference'].getData();

	 console.log(data1)


	 $('#description').val(data1);
	 $('#TOR').val(data2)
	 
	 
	 if(confirm('Are you sure to submit?')){
		 
	 }else{
		 return false;
		 event.preventDefault();
	 }
}


function submitForm1(myform)
{ 
	/* var periodic=$('#projectapplicable').val();
	var projectid=parseInt($('#projectid').val());
	
	if(periodic=='P')
	{
		if(projectid==0){
			alert('periodic ');
		}
	} */
	
	
 	myconfirm('Are You Sure To Edit This committee ?',myform);
 	event.preventDefault();
}
function submitAdd(frmid){
	
	 var data1 =CKEDITOR.instances['Editordescription'].getData();
	 var data2 =CKEDITOR.instances['EditorReference'].getData();

	 console.log("frmid --"+frmid)

	 $('#description').val(data1);
	 $('#TOR').val(data2);
	 
	 if(window.confirm('Are you sure to submit?')){
		/*  document.getElementById(a).submit();  */
		 document.getElementById(frmid).submit(); 
	 }else{
		 event.preventDefault(); 
	 }
}
function submitForm(frmid)
{ 
	 var data1 =CKEDITOR.instances['Editordescription'].getData();
	 var data2 =CKEDITOR.instances['EditorReference'].getData();

	 console.log(data1)
	 console.log(data2)
	 
	 $('#description').val(data1);
	 $('#TOR').val(data2);
  document.getElementById(frmid).submit(); 
} 
</script>
	
<script>

$(document).ready(function(){
	$('#periodicdurationfield').prop("required",false); 
})


document.getElementById('periodic').addEventListener('change', function() {

if(this.value == "P"){

		document.getElementById('periodicduration').style.display="block";
		$('#periodicdurationfield').prop("required",true); 
		
	}
	if(this.value == "N"){
		
		document.getElementById('periodicduration').style.display="none";
		$('#periodicdurationfield').prop("required",false); 
	}
	
	
	});


var editor_config = {
	    toolbar: [
	        {
	            name: 'paragraph',
	            items: ['NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote']
	        },
	        {
	            name: 'styles',
	            items: ['Format', 'Font', 'FontSize']
	        },
	        {
	            name: 'align',
	            items: ['JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock']
	        }
	    ],
	    removeButtons: 'Underline,Strike,Subscript,Superscript,Anchor,Styles,Specialchar',
	    customConfig: '',
	    disallowedContent: 'img{width,height,float}',
	    extraAllowedContent: 'img[width,height,align]',
	    height: 200,
	    contentsCss: [CKEDITOR.basePath + 'mystyles.css'],
	    bodyClass: 'document-editor',
	    format_tags: 'p;h1;h2;h3;pre',
	    removeDialogTabs: 'image:advanced;link:advanced',
	    stylesSet: [
	        {
	            name: 'Marker',
	            element: 'span',
	            attributes: { 'class': 'marker' }
	        },
	        {
	            name: 'Cited Work',
	            element: 'cite'
	        },
	        {
	            name: 'Inline Quotation',
	            element: 'q'
	        },
	        {
	            name: 'Special Container',
	            element: 'div',
	            styles: {
	                padding: '5px 10px',
	                background: '#eee',
	                border: '1px solid #ccc'
	            }
	        },
	        {
	            name: 'Compact table',
	            element: 'table',
	            attributes: {
	                cellpadding: '5',
	                cellspacing: '0',
	                border: '1',
	                bordercolor: '#ccc'
	            },
	            styles: { 'border-collapse': 'collapse' }
	        },
	        {
	            name: 'Borderless Table',
	            element: 'table',
	            styles: { 'border-style': 'hidden', 'background-color': '#E6E6FA' }
	        },
	        {
	            name: 'Square Bulleted List',
	            element: 'ul',
	            styles: { 'list-style-type': 'square' }
	        }
	    ],
	    enterMode: CKEDITOR.ENTER_BR,
	    shiftEnterMode: CKEDITOR.ENTER_P,
	    on: {
	        instanceReady: function() {
	            this.dataProcessor.htmlFilter.addRules({
	                elements: {
	                    p: function(element) {
	                        if (element.children.length == 1 && element.children[0].name == 'br') {
	                            return false;
	                        }
	                    }
	                }
	            });
	        }
	    }
	};

	CKEDITOR.replace('Editordescription', editor_config);
	CKEDITOR.replace('EditorReference', editor_config);

	
	document.getElementById('projectapplicable').addEventListener('change', function() {
	    var isBriefing = document.getElementById('IsBriefing');
	    if (this.value === "N") {
	        isBriefing.disabled = true;
	        isBriefing.value = "N";
	    } else if (this.value === "P") {
	        isBriefing.disabled = false;
	    }
	});
</script>		
	
		
</body>

</html>