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
<script src="${ckeditor}"></script>
<link href="${contentCss}" rel="stylesheet" />
<spring:url value="/resources/css/committeeModule/committeeAdd.css" var="committeeAdd" />
<link href="${committeeAdd}" rel="stylesheet" />
<title> ADD COMMITTEE</title>
</head>
<body>
<%
String projectid=(String)request.getAttribute("projectid");
Object[] projectdetails=(Object[])request.getAttribute("projectdetails"); 
String projectappliacble=(String)request.getAttribute("projectappliacble");
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
    
    
	<div class="container mw-100">
		<div class="row">
			<div class="col-md-12">
				<div class="card shadow-nohover">
					<div class="card-header headerColor">
						<div class="row">
							<div class="col-md-6">
								<b class="text-white">ADD NEW COMMITTEE</b>
							</div>
							<div class="col-md-6">
								<%if(Long.parseLong(projectid)>0){ %>
								<div align="right"><b class="text-white"  >Project : <%=projectdetails[4]!=null?StringEscapeUtils.escapeHtml4(projectdetails[4].toString()): " - " %></b></div>
								<%} %>
							</div>
						</div>
					</div>
					<form action="CommitteeAddSubmit.htm" method="post" name="addcommitteefrm" id="addcommitteefrm" >
					
						<div class="card-body">
							<div class="row">							
								<div class="col-md-4">
									<div class="form-group">
										<label class="control-label">
											Committee Code
											<span class="mandatory text-danger" >*</span>
										</label>
										<input class="form-control" type="text" name="committeeshortname" id="committeeshortname" required maxlength="6">
									</div>
								</div>
							
							
								<div class="col-md-4">
									<div class="form-group">
										<label class="control-label">Committee Name<span class="mandatory text-danger">*</span></label>
										<input class="form-control" type="text" name="committeename" id="committeename" required maxlength="255">
									</div>
								</div>

							
							<div class="col-md-4">
								<div class="form-group">
									<label class="control-label">Committee Type<span class="mandatory text-danger" >*</span></label>
									<select class="custom-select mt-n5" id="ctype" required="required" name="committeetype">
										<option disabled="true"  selected value="">Choose...</option>
										<option  value="S">Standard</option>
										<option  value="A">Adhoc</option>
									</select>
								</div>
							</div>
						
						
						</div>
						
						<div class="row">
						 <div class="col-md-2">
								<div class="form-group">
									<label class="control-label">Project Applicable<span class="mandatory text-danger">*</span></label>
									<select class="custom-select mt-n5" id="proapplicable" required="required" name="projectapplicable">
										<option disabled="true"  selected value="">Choose...</option>
										<%if(projectappliacble.equals("P")){ %>
										<option selected value="P">Project</option>
										<%}else if(projectappliacble.equals("N")){ %>
										<option selected value="N">Non-Project</option>										
										<%} %>
									</select>
									<input type="hidden" name="projectid" value="<%=projectid%>">
								</div>
							</div> 
							
							
							<div class="col-md-2">
								<div class="form-group">
									<label class="control-label">Tech / Non-Tech<span class="mandatory text-danger">*</span></label>
									<select class="custom-select mt-n5" id="technontech" required="required" name="technontech">
										<option disabled="true"  selected value="">Choose...</option>
										<option  value="T">Technical</option>
										<option  value="N">Non-Technical</option>
									</select>
								</div>
							</div>
							
							<div class="col-md-2">
								<div class="form-group">
									<label class="control-label">Periodic / Non-Periodic<span class="mandatory text-danger">*</span></label>
									<select class="custom-select mt-n5" id="periodic" required="required" name="periodic">
										<option disabled="true"  selected value="">Choose...</option>
										<option  value="P">Periodic</option>
										<option  value="N">Non-Periodic</option>
									</select>
								</div>
							</div>
													
							<div class="col-md-2 hidden" id="periodicduration">
								<div class="form-group">
									<label class="control-label" >Periodic Duration (Days)<span class="mandatory text-danger">*</span></label>
									<input class="form-control mt-n5" type="number" min="1" name="periodicduration" id="periodicdurationfield" placeholder="Days">
								</div>
							</div>
							<div class="col-md-2">
								<div class="form-group">
									<label class="control-label">Is Briefing<span class="mandatory text-danger">*</span></label>
									<select class="custom-select mt-n5" id="IsBriefing" required="required" name=IsBriefing>
										<option disabled  value="">Choose...</option>
										<option  value="N" selected>No</option>
										<option  value="Y">Yes</option>
									</select>
								</div>
							</div>
						</div>	
						
						<div class="row">
							
							
							<div class="col-md-12">								
								<div class="form-group">
									<label class="control-label">Guidelines<span class="mandatory text-danger">*</span></label>
									<input class="form-control" type="text" name="guidelines" id="guidelines"  required maxlength="255">
								</div>

							</div>
						</div>
						
						<div class="row">
							<div class="col-md-6">
								
								<div class="form-group">
									<label class="control-label">Purpose<span class="mandatory text-danger">*</span></label>
									<div id="Editordescription" class="center">
															<textarea name="description"  id="description" ></textarea>
														</div>
								
								</div>

							</div>
							<div class="col-md-6">
								
								<div class="form-group">
									<label class="control-label">Terms Of Reference <span class="mandatory text-danger">*</span></label>
										<div id="EditorReference" class="center">
															<textarea name="TOR"  id="TOR" ></textarea>
														</div>
								</div>

							</div>
						</div>
						
						
					
					<div class="row" ><div class="col-md-5"></div>
						<div class="col-md-3">
							<div class="form-group">
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								<button type="button" class="btn btn-primary btn-sm submit" value="SUBMIT"  onclick="return committeenamecheck('addcommitteefrm');" >SUBMIT</button>
								
								<button type="button" class="btn btn-primary btn-sm back" onclick="backsub();">BACK</button>
																					
							</div>
						</div>
					</div>					
				</form>
					<form method="post" action="CommitteeList.htm" name="backfrm" id="backfrm">
						<input type="hidden" name="projectid" value="<%=projectid%>">
						<input type="hidden" name="projectappliacble" value="<%=projectappliacble%>">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />					
					</form>
			
					</div>
					<div class="card-footer">
					</div>
				</div>
			</div>
		</div>

	

<script type="text/javascript">
$('#projectid').select2();

function committeenamecheck(frmid){
	
	var projectidd=$('#projectid').val();
	
	
	var fullname=$('#committeename').val().trim();
	var shortname=$('#committeeshortname').val().trim();
	var proapplicable=$('#proapplicable').val().trim();
	var ctype=$('#ctype').val();
	var technontech=$('#technontech').val();
	
	var periodic=$('#periodic').val();
	var periodicduration=$('#periodicdurationfield').val().trim();
	var guidelines=$('#guidelines').val().trim();
	
	
	 var data1 =CKEDITOR.instances['Editordescription'].getData();
	 var data2 =CKEDITOR.instances['EditorReference'].getData();

	 $('#description').val(data1);
	 $('#TOR').val(data2);
	
	var description=$('#description').val().trim();
	var TOR=$('#TOR').val().trim();
	
	console.log("description" + description)
	console.log("TOR" + TOR)
	
	$.ajax({

		type : "GET",
		url : "CommitteeNamesCheck.htm",
		data : {
			
			fname:fullname.trim(),
			sname:shortname.trim(),
			
		},
		datatype : 'json',
		success : function(result) {
			var ajaxresult = JSON.parse(result);
			var count=0;
			if(ajaxresult[0]==1){
			
				alert('Commitee Code Already Exists');
				count++;
				return false;
			}
			if(ajaxresult[1]==1){
				
				alert('Commitee Name Already Exists');
				count++;
				return false;
			}			
			if(count==0)
			{
				if(confirm('Are you Sure To Save ?'))
				{
					if(fullname==="" ||shortname==="" ||proapplicable==="" || ctype===null || technontech==="" || periodic==="" || guidelines==="" || ( periodic==="P" && ( periodicduration==="0" ||  periodicduration==="") )  ) 
					{
						alert('Please Fill All the Fields ? ');
						
					}else
					{
						submitForm(frmid);
					}
				}
				else
				{
					
					return false;
				}
				
				
			}	else {
				return false;
				
			}	
		}
	});	
}

</script>
<script type='text/javascript'> 

$('#projectid').select2();

function backsub(){
		$('#backfrm').submit();
		}



function submitForm(myform)
{ 
  document.getElementById(myform).submit(); 
} 


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




</script>		

	<script type="text/javascript">
	
	var editor_config = {
			toolbar : [
					{
						name : 'paragraph',
						items : [ 'NumberedList', 'BulletedList', '-',
								'Outdent', 'Indent', '-', 'Blockquote' ]
					},
					{
						name : 'styles',
						items : [ 'Format', 'Font', 'FontSize' ]
					},
					{
						name : 'align',
						items : [ 'JustifyLeft', 'JustifyCenter',
								'JustifyRight', 'JustifyBlock' ]
					} ],
			removeButtons : 'Underline,Strike,Subscript,Superscript,Anchor,Styles,Specialchar',
			customConfig : '',
			disallowedContent : 'img{width,height,float}',
			extraAllowedContent : 'img[width,height,align]',
			height : 200,
			contentsCss : [ CKEDITOR.basePath + 'mystyles.css' ],
			bodyClass : 'document-editor',
			format_tags : 'p;h1;h2;h3;pre',

			removeDialogTabs : 'image:advanced;link:advanced',

			stylesSet : [

			{
				name : 'Marker',
				element : 'span',
				attributes : {
					'class' : 'marker'
				}
			}, {
				name : 'Cited Work',
				element : 'cite'
			}, {
				name : 'Inline Quotation',
				element : 'q'
			},

			{
				name : 'Special Container',
				element : 'div',
				styles : {
					padding : '5px 10px',
					background : '#eee',
					border : '1px solid #ccc'
				}
			}, {
				name : 'Compact table',
				element : 'table',
				attributes : {
					cellpadding : '5',
					cellspacing : '0',
					border : '1',
					bordercolor : '#ccc'
				},
				styles : {
					'border-collapse' : 'collapse'
				}
			}, {
				name : 'Borderless Table',
				element : 'table',
				styles : {
					'border-style' : 'hidden',
					'background-color' : '#E6E6FA'
				}
			}, {
				name : 'Square Bulleted List',
				element : 'ul',
				styles : {
					'list-style-type' : 'square'
				}
			}, {
				filebrowserUploadUrl : '/path/to/upload-handler'
			}, ]
		};
	CKEDITOR.replace('Editordescription', editor_config);
	CKEDITOR.replace('EditorReference', editor_config);
	
	
	
/* 	 $('#addcommitteefrm').submit(function() {
		 var data1 =CKEDITOR.instances['Editordescription'].getData();
		 var data2 =CKEDITOR.instances['EditorReference'].getData();
		 console.log(data);
		 $('#description').val(data1);
		 $('#TOR').val(data2);
		 }); */
	</script>	
		
</body>

</html>