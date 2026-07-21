<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"
	import="java.util.*,com.vts.*,java.text.SimpleDateFormat,java.io.ByteArrayOutputStream,java.io.ObjectOutputStream"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/sweetalert2.min.css" var="sweetalertCss" />
<spring:url value="/resources/js/sweetalert2.min.js" var="sweetalertJs" />
<link href="${sweetalertCss}" rel="stylesheet" />
<script src="${sweetalertJs}"></script>
<title>PreProject Doc Rep</title>
<spring:url value="/resources/css/fileRepo/PreProjectRepMaster.css" var="preProjectRepMaster" />
<link href="${preProjectRepMaster}" rel="stylesheet" />
</head>
<body>
	<%
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	List<Object[]> ProjectList = (List<Object[]>) request.getAttribute("projectslist");
	Long projectDirectorId = 0l;
	String InitiationId = (String) request.getAttribute("initiationId");
	String logintype = (String) session.getAttribute("LoginType");
	Long empId = (Long) session.getAttribute("EmpId");
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
				<div class="card">
					<div class="card-header">
						<div class="row m-minus" >
							<div class="col-md-6">
								<h4>Pre Project Document Rep Master</h4>
							</div>
							<div class="col-md-6">
								<form>
									<table class="f-right">
										<tr>
											<td><label class="control-label">Project Name
													:&nbsp;&nbsp; </label></td>
											<td>
											<select class="form-control selectdee w-360"
												id="initiationId" required="required" name="initiationId">
													<option value="" disabled="disabled">Choose...</option>
													<%
													for (Object[] obj : ProjectList) {
													%>
													<option value="<%=obj[0]%>"
														<%if (InitiationId.equalsIgnoreCase(obj[0].toString())) {
	                                                            projectDirectorId = Long.parseLong(obj[4].toString());%>
														selected="selected" <%}%>><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - " %> (<%= obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %>)
													</option>
													<%
													}
													%>
											</select> 
										</tr>
									</table>
								</form>
							</div>
						</div>
					</div>
					
					<div class="card-body m-minus8">
						  <div class="d-flex justify-content-left align-items-center flex-wrap mb-3">
						    <div class="breadcrumb-header mr-4" id="breadcumb-span">
						        <span class="breadcrumb-link" onclick="goBackToMain()">Home</span>
						        <span> &gt; </span>
						        <span id="spanValue"></span>
						        <span id="subTag"> &gt; </span>
						        <span id="subspanValue"></span>
						    </div>
						    <div class="text-center">
						      <%if(empId!=null && empId.equals(projectDirectorId) || logintype!=null && logintype.equalsIgnoreCase("A") ){ %>
						        <button type="button" class="btn btn-primary" id="addBtn" value="mainLevel" onclick="addNewLevel()">Add New</button>
						      <% }%>
						    </div>
						</div>
						
						 <div class="folder-container" id="mainFolderId">
						     <div id="main-folders" class="folder-container">
						     
						     </div>
						     <div class="subfolder-group w-auto" id="subFolderId">
						     
						     </div>
						     
						     <div id="pdf-wrapper"></div>
						 </div>
						 
						 <div id="subpdf-wrapper"></div>
						
					</div>
					
				</div>
			</div>
		</div>
	</div>
	
	 <div class="modal fade" id="exampleModal" tabindex="-1" role="dialog"
		aria-labelledby="exampleModalLabel" aria-hidden="true">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="exampleModalLabel">Main Level</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<div>
							<input type="hidden" name="initiationId" id="prjMainId" value="">
							<div>
								<input class="form-control w-100" type="text" name="MasterName" id="masterName"
									required="required" maxlength="255" onchange="checkDuplicateFolderName()">
							</div>
							<div class="text-center mt-4">
								<button type="button" class="btn btn-success" onclick="submitMainFile()">Submit</button>
							</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
  <div class="modal fade" id="exampleSubModal" tabindex="-1" role="dialog"
		aria-labelledby="exampleSubModalLabel" aria-hidden="true">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="exampleSubModalLabel">Sub Level</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<div>
							<div>
								<input class="form-control w-100" type="text" name="MasterSubName" id="masterSubName"
									required="required" maxlength="255" onchange="checkDuplicateFolderName()">
							</div>
						<div class="text-center mt-4">
							<button type="button" class="btn btn-success" onclick="submitSubFile()">Submit</button>
							<input type="hidden" name="fileMasterId" id="parentPkId" value="">
							<input type="hidden" name="levelId" id="levelId" value="">
							<input type="hidden" name="initiationId" id="prjSubId" value="">
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
 <div class="modal fade" id="uploadModal" tabindex="-1" role="dialog">
	<div class="modal-dialog modal-lg mw-60" role="document">
	      <div class="modal-content">
	        <div class="modal-header">
	          <h5 class="modal-title" id="modalTitle"></h5>
	          <button type="button" class="close" data-dismiss="modal">&times;</button>
	        </div>
	        <div class="modal-body">
     		  <div class="row">
					<div class="col-md-12 d-flex align-items-center mb-2">
					    <label class="control-label mr-3 mb-0 ws-wrap" >Upload Type</label>
					    <div class="btn-group btn-group-toggle flex-grow-1" data-toggle="buttons">
					        <label class="btn btn-outline-success flex-fill font-w" >
					            <input type="radio" name="isNewUpload" id="isNewUploadY" value="Y" autocomplete="off" checked onchange="toggleUploadType(this.value)"> New Upload
					        </label>
					        <label class="btn btn-outline-danger flex-fill font-w" >
					            <input type="radio" name="isNewUpload" id="isNewUploadN" value="N" autocomplete="off" onchange="toggleUploadType(this.value)"> Existing Upload
					        </label>
					    </div>
					</div>
					
					<div class="col-md-5 mt-3">
					    <div class="form-group">
					        <label class="control-label">Document Name</label>
					
					        <!-- Text input shown by default -->
					        <input type="text" class="form-control input-height" id="FileNameInput"
					            onchange="checkDuplicateFileName()" name="FileName" required="required" />
					
					        <!-- Dropdown hidden by default -->
					        <select class="form-control dis-none input-height" id="FileNameSelect" name="FileName" >
					            <option value="">-- Select Document --</option>
					        </select>
					    </div>
					</div>
					<div class="col-md-4 mt-3">
						<div class="form-group">
							<label class="control-label">Upload</label> <input
								type="file" name="FileAttach" id="FileAttach"
								class="form-control" accept="application/pdf"
								aria-describedby="inputGroup-sizing-sm" maxlength="255"
								/>
						</div>
					</div>
					<div class="col-md-3 mt-3">
						<div class="form-group">
							<label class="control-label">Is New Version</label> <br>
							<input type="radio" name="isnewversion" id="isnewver" value="Y" />&nbsp;&nbsp;Yes &emsp; 
							<input type="radio" name="isnewversion" id="isnonewver" checked="checked" value="N" />&nbsp;&nbsp;No
						</div>
					</div>
				</div>
	            <input type="hidden" name="folderId" id="folderId" value="">
	            <input type="hidden" name="fileType" id="fileType" value="">
	        </div>
			 <div align="center">
				<input type="button" class="btn btn-primary btn-sm submit mb-3"  id="submitversion" onclick="submitUpload()"/>
			</div>
		  </div>
	  </div>
	</div>
	
	<div class="modal fade" id="versionModal" tabindex="-1" role="dialog" aria-labelledby="versionModalLabel" aria-hidden="true">
	  <div class="modal-dialog modal-xl" role="document">
	    <div class="modal-content">
	      <div class="modal-header">
	        <h5 class="modal-title" id="versionModalLabel"></h5>
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
	          <span aria-hidden="true">&times;</span>
	        </button>
	      </div>
	      <div class="modal-body">
	        <table class="table table-bordered table-hover">
	          <thead class="bg-bis">
	            <tr>
	              <th>SN</th>
	              <th>File Name</th>
	              <th>Version</th>
	              <th>Upload Date</th>
	              <th>Uploaded By</th>
	              <th>Action</th>
	            </tr>
	          </thead>
	          <tbody id="versionTableBody">
	            <!-- Content will be added dynamically -->
	          </tbody>
	        </table>
	      </div>
	    </div>
	  </div>
	</div>
	
	
<script type="text/javascript">

function goBackToMain() {
    document.getElementById("main-folders").style.display = "flex";
    document.querySelectorAll('.subfolder-group').forEach(el => el.style.display = "none");
    $('#addBtn').val('mainLevel');
	$('#breadcumb-span').hide();
	$("#pdf-wrapper").empty();
	$("#subpdf-wrapper").empty();
	$("#subFolderId").empty();
	$('#mainFolderId').show();
	$('#addBtn').show();
	$('#spanTag').hide();
	$('#spanValue').removeClass('breadcrumb-link');
}

function addNewLevel() {
	const type = $('#addBtn').val();
	if(type === 'mainLevel'){
	   $('#exampleModal').modal('show');
	}else{
		$('#exampleSubModal').modal('show')
	}
}

$(document).ready(function(){
  	$('#breadcumb-span').hide();
    let mainFolder = $("#main-folders");
    mainFolder.empty();
  	const id = <%=InitiationId%>;
  	folderList(id,mainFolder);
});

$('#initiationId').on('change', function() {
    let mainFolder = $("#main-folders");
    mainFolder.empty();
    goBackToMain();
    const id = $(this).val();
    folderList(id,mainFolder);
});


function folderList(pid,mainFolder){
	if(pid){
	$('#prjMainId').val(pid);
	  $.ajax({
          type: "GET",
          url: 'preProjectMainFolderList.htm',
          data : {
        	  initiationId : pid,
  		},
          dataType: "json",
          success: function (data) {
              if (data.length === 0) {
                  console.error("No document found");
              } else {
                  $.each(data, function (index, data) {
                	  const mainPart = '<div class="folder">'+
                	                     '<div class="folder-icon-wrapper">'+
									      '<i class="fa fa-folder mainicon" onclick="showSubfolders('+pid+','+data[0]+')"></i>'+
									      ' <div>'+
									      '<span id="span_'+ data[0] +'">'+data[3]+'<br/>'+
											 '<%if(empId!=null && empId.equals(projectDirectorId) || logintype!=null && logintype.equalsIgnoreCase("A") ){ %>'+
												'<i class="fa fa-pencil-square-o ml-1 cursor" aria-hidden="true"'+
												    'onclick="event.stopPropagation(); enableInlineEdit(' + data[0] +')">'+
												 '</i>'+
												'<i class="fa fa-upload ml-2 cursor col" aria-hidden="true"'+
													'onclick="event.stopPropagation(); showUpload(\'mainLevel\',\''+data[3] +'\','+data[0]+')">'+
												'</i>'+
											   '<% }%>'+
									      '</span>'+
									      '<textarea name="levelname" class="textarea-height" id="input_'+ data[0] +'" maxlength="255"'+
												'rows="2">'+ data[3] +'</textarea>'+
											'<div class="row ml-2">'+
												'<button type="button" id="btn_'+ data[0] +'"'+
													'class="btn btn-sm btn-success dis-none" onclick="submitLevelEdit(\'mainLevel\', '+data[0] +', \'0\', \''+data[3] +'\')">'+
													'<i class="fa fa-check" aria-hidden="true"></i>'+
												'</button>&nbsp;'+
												'<button type="button" id="btnx_'+ data[0] +'"'+
													'class="btn btn-sm btn-danger dis-none"'+
													'onclick="event.stopPropagation(); disableInlineEdit('+ data[0] +')">'+
													'<i class="fa fa-times" aria-hidden="true"></i>'+
												'</button>'+
											'</div>'+
											'<input type="hidden" id="mainLevelId" value="'+ data[2] +'">'+
											'<input type="hidden" id="parentName_'+ data[0] +'" value="'+ data[3] +'">'+
									   '</div>';
						 mainFolder.append(mainPart);
                  });
              }
          },
          error: function (xhr, status, error) {
              console.error("AJAX Error:", error);
          }
      });
	}
};

function enableInlineEdit(id) {
	document.getElementById("span_" + id).style.display = "none";
	document.getElementById("input_" + id).style.display = "inline-block";
	document.getElementById("btn_" + id).style.display = "inline-block";
	document.getElementById("btnx_" + id).style.display = "inline-block";
	document.getElementById("input_" + id).focus();
}

function disableInlineEdit(id) {
	document.getElementById("span_" + id).style.display = "inline-block";
	document.getElementById("input_" + id).style.display = "none";
	document.getElementById("btn_" + id).style.display = "none";
	document.getElementById("btnx_" + id).style.display = "none";
}

function showSubfolders(pid,parentId) {
	$('#prjSubId').val(pid);
	$('#subFolderId').empty();
	$('#subFolderId').show();
	$('#breadcumb-span').show();
	$('#subTag').hide();
	$('#subspanValue').hide();
	const id = $('#mainLevelId').val();
	const pname = $('#parentName_'+parentId).val();
    document.getElementById("main-folders").style.display = "none";
    document.querySelectorAll('.subfolder-group').forEach(el => el.style.display = "none");
    document.querySelector('#subFolderId').style.display = "flex";
    $('#addBtn').val('subLevel');
    $('#parentPkId').val(parentId);
    $('#levelId').val(id);
    $('#spanValue').text(pname);
    
    $.ajax({
        type: "GET",
        url: 'preProjectSubFolderList.htm',
        data : {
      	  initiationId : pid,
      	  mainLevelId  : parentId
		},
        dataType: "json",
        success: function (data) {
            if (data.length === 0) {
                console.error("No document found");
            } else {
                $.each(data, function (index, sub) {
              	  const subPart = '<div class="folder">'+
              	                   '<div class="subfolder-list">'+
              	                     '<div class="folder-icon-wrapper">'+
									      '<i class="fa fa-folder mainicon" onclick="showSubFileDocs('+parentId+','+sub[0]+',\''+sub[3] +'\')"></i>'+
									      ' <div>'+
									      '<span id="span_'+ sub[0] +'">'+sub[3]+'<br/>'+
											 '<%if(empId!=null && empId.equals(projectDirectorId) || logintype!=null && logintype.equalsIgnoreCase("A") ){ %>'+
												'<i class="fa fa-pencil-square-o ml-2 cursor" aria-hidden="true"'+
												    'onclick="event.stopPropagation(); enableInlineEdit('+ sub[0] +')">'+
												 '</i>'+
												'<i class="fa fa-upload ml-2 cursor col-1"  aria-hidden="true"'+
													'onclick="event.stopPropagation(); showUpload(\'subLevel\',\''+sub[3] +'\','+sub[0]+')">'+
												'</i>'+
											   '<% }%>'+
									      '</span>'+
									      '<textarea name="levelname" class="textarea-height" id="input_'+ sub[0] +'" maxlength="255"'+
												'rows="2">'+ sub[3] +'</textarea>'+
											'<div class="row ml-2">'+
												'<button type="button" id="btn_'+ sub[0] +'"'+
													' class="btn btn-sm btn-success dis-none" onclick="submitLevelEdit(\'subLevel\','+sub[0]+','+parentId+',\''+sub[3] +'\')">'+
													'<i class="fa fa-check" aria-hidden="true"></i>'+
												'</button>&nbsp;'+
												'<button type="button" id="btnx_'+ sub[0] +'"'+
													'class="btn btn-sm btn-danger dis-none" '+
													'onclick="event.stopPropagation(); disableInlineEdit('+ sub[0] +')">'+
													'<i class="fa fa-times" aria-hidden="true"></i>'+
												'</button>'+
											'</div>'+
											'<input type="hidden" id="fileParentId_'+sub[0]+'" value="'+parentId+'">'+
											'<input type="hidden" id="fileSubName_'+sub[0]+'" value="'+sub[3]+'">'+
											'</div>'+
									   '</div>';
					$('#subFolderId').append(subPart);
                });
            }
        },
        error: function (xhr, status, error) {
            console.error("AJAX Error:", error);
        }
    });
    
    const wrapper = $("#pdf-wrapper");
    const containerId = "pdf-container-" + parentId;
    let container = $("#" + containerId);

    if (container.length > 0) {
        container.toggle();
        return;
    }

    container = $("<div>", {
        id: containerId,
        class: "d-flex flex-wrap gap-4",
        css: { display: "flex" }
    });
    wrapper.append(container);
    
   $.ajax({
        type: "GET",
        url: 'getPreProjectOldFileNames.htm',
        data : {
	   			initiationId : pid,
	   			fileId : parentId,
	   			fileType : 'mainLevel',
		},
        dataType: "json",
        success: function (data) {
            if (data.length === 0) {
                //container.append("<p>No documents found.</p>");
                console.error("No document found");
            } else {
                $.each(data, function (index, file) {
                    const pdfItem ='<div class="pdf-item text-center">'+
		                                '<div class="file-icon-wrapper">'+
		                                    '<i class="fa fa-file-pdf-o fileicon" aria-hidden="true"></i>'+
		                                '</div>'+
		                                '<span class="file-download-span" onclick="fileDownload(' + file[7] + ', \'mainLevel\')">' + file[6].replaceAll("<","").replaceAll(">","").replaceAll("/","") + '</span><br/>'+
		                                '<span class="text-muted">Ver '+file[4]+'.'+file[5]+' <i class="fa fa-info-circle cursor" aria-hidden="true"  onclick="showVersionModal(' + file[0] + ',\'' + file[6] + '\', \'mainLevel\')"></i></span>'+
		                            '</div>';
                    container.append(pdfItem);
                });
            }
        },
        error: function (xhr, status, error) {
            console.error("AJAX Error:", error);
            ///container.append("<p>Error loading documents.</p>");
        }
    });
}

function checkDuplicateFolderName() {
	  const fileMainName = $('#masterName').val().trim();
	  const fileSubName = $('#masterSubName').val().trim();
	  const parentId = $('#parentPkId').val();
	  const fileType = $('#addBtn').val();
	  const preprojId = $('#initiationId').val();

	    if(fileType === 'mainLevel' ? fileMainName !== '' : fileSubName !== ''){
			$.ajax({
			    type: 'GET',
			    url: 'checkPreProjectFolderNames.htm',
			    data: {
			        initiationId: preprojId,
			        fileId: fileType === 'mainLevel' ? '0' : parentId,
			        fileType: fileType,
			        fileName: fileType === 'mainLevel' ? fileMainName : fileSubName,
			    },
			    success: function(response) {
			        let result = parseInt(response);
			        if (result > 0) {
			            Swal.fire({
			                icon: 'error',
			                title: 'Duplicate Folder Name',
			                text: fileType === 'mainLevel' 
			                    ? fileMainName + ' folder name already exists. Please choose another.' 
			                    : fileSubName + ' folder name already exists. Please choose another.',
			                allowOutsideClick: false
			            });

			            $('#' + (fileType === 'mainLevel' ? 'masterName' : 'masterSubName')).val('').focus();
			        }
			    },
			    error: function(err) {
			    	Swal.fire({
	                  icon: 'error',
	                  title: 'Error',
	                  text: 'Could not check the folder name. Please try again later.',
	                  allowOutsideClick: false
	              });
			    }
			});
	    }
	}
	
function submitMainFile() {
	event.preventDefault();
	const mainFolder = $("#main-folders");
	const initId = $('#prjMainId').val();
    const inputvalue = $('#masterName').val().trim();
     if (!inputvalue) {
         Swal.fire({
             icon: 'warning',
             title: 'Validation Error',
             text: 'File name cannot be empty.',
         });
         return;
     }
     
     Swal.fire({
         title: 'Are you sure to add?',
         icon: 'question',
         showCancelButton: true,
         confirmButtonColor: 'green',
         cancelButtonColor: '#d33',
         confirmButtonText: 'Yes'
     }).then((result) => {
         if (result.isConfirmed) {
             $.ajax({
             	type : "POST",
                 url: "PreProjectFileRepAdd.htm",
                 data: {
                	 initiationId: initId,
                	 levelName: inputvalue,
                     ${_csrf.parameterName}:"${_csrf.token}",
                 },
                 success: function(response) {
                     let result = parseInt(response);
                     if (result > 0) {
                    	    $('#exampleModal').modal('hide');
                    	    $('#masterName').val('');
                    	    mainFolder.empty();
                    	    folderList(initId,mainFolder);
                     	 Swal.fire({
                              icon: 'success',
                              title: 'Success',
                              text: 'Main File added successfully!',
                          })
                     } else {
                         Swal.fire({
                             icon: 'error',
                             title: 'Error',
                             text: 'Add failed. Please try again.'
                         });
                     }
                 },
                 error: function() {
                     Swal.fire({
                         icon: 'error',
                         title: 'Error',
                         text: 'An error occurred while adding the file.',
                     });
                 }
             });
          }
     });
 }
 
function submitSubFile() {
	event.preventDefault();
	const initId = $('#prjSubId').val();
	const parentId = $("#parentPkId").val();
	const levelId = $('#levelId').val();
    const inputvalue = $('#masterSubName').val().trim();
     if (!inputvalue) {
         Swal.fire({
             icon: 'warning',
             title: 'Validation Error',
             text: 'File name cannot be empty.',
         });
         return;
     }
     
     Swal.fire({
         title: 'Are you sure to add?',
         icon: 'question',
         showCancelButton: true,
         confirmButtonColor: 'green',
         cancelButtonColor: '#d33',
         confirmButtonText: 'Yes'
     }).then((result) => {
         $.ajax({
          	type : "POST",
              url: "PreProjectFileRepMasterSubAdd.htm",
              data: {
             	 initiationId: initId,
             	 levelName: inputvalue,
             	 parentLevelId : parentId,
             	 levelId : levelId,
                 ${_csrf.parameterName}:"${_csrf.token}",
              },
              success: function(response) {
                  let result = parseInt(response);
                  if (result > 0) {
                	 $('#exampleSubModal').modal('hide');
              	     $('#masterSubName').val('');
                	 showSubfolders(initId,parentId);
                  	 Swal.fire({
                           icon: 'success',
                           title: 'Success',
                           text: 'Sub File added successfully!',
                       })
                  } else {
                      Swal.fire({
                          icon: 'error',
                          title: 'Error',
                          text: 'Add failed. Please try again.'
                      });
                  }
              },
              error: function() {
                  Swal.fire({
                      icon: 'error',
                      title: 'Error',
                      text: 'An error occurred while adding the file.',
                  });
              }
          });
     });
 }
 
function submitLevelEdit(levelType, pkId, parentId, previousName) {
	const initId = $('#initiationId').val();
    const inputvalue = $('#input_' + pkId).val().trim();

    // Input validation
    if (!inputvalue) {
        Swal.fire({
            icon: 'warning',
            title: 'Validation Error',
            text: 'File name cannot be empty.',
        });
        return;
    }
    const regex =/^[A-Za-z0-9 _-]+$/;

    if (!regex.test(inputvalue)) {
        Swal.fire({
        	icon:'warning',
        	title:'Validation Error',
        	text: "Only letters, numbers, spaces, underscores ( _ ) and hyphens (-) are allowed."
        	});
        return;
    }
    
    if (previousName === inputvalue) {
        return;
    }else{
		$.ajax({
		    type: 'GET',
		    url: 'checkPreProjectFolderNames.htm',
		    data: {
		    	initiationId: initId,
		        fileId: parentId,
		        fileType: levelType,
		        fileName: inputvalue,
		    },
		    success: function(response) {
		        let result = parseInt(response);
		        if (result > 0) {
		            Swal.fire({
		                icon: 'error',
		                title: 'Duplicate Folder Name',
		                text: inputvalue + ' folder name already exists. Please choose another.',
		                allowOutsideClick: false
		            });
		            $('#input_' + pkId).val(previousName);
		        }
		    },
		    error: function(err) {
		    	Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'Could not check the folder name. Please try again later.',
                    allowOutsideClick: false
                });
		    }
		});
    }

    Swal.fire({
        title: 'Are you sure to edit?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: 'green',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes, update it'
    }).then((result) => {
        if (result.isConfirmed) {
        	
        	 Swal.fire({
                 title: 'Updating...',
                 text: 'Please wait while the file name is being updated.',
                 allowOutsideClick: false,
                 didOpen: () => {
                     Swal.showLoading();
                 }
             });
        	
            $.ajax({
            	type : "POST",
                url: "PreProjectFolderNameEdit.htm",
                data: {
                    filerepmasterid: pkId,
                    levelname: inputvalue,
                    levelType: levelType,
                    ${_csrf.parameterName}:	"${_csrf.token}",
                },
                success: function(response) {
                	 Swal.close();
                    let result = parseInt(response);
                    if (result > 0) {
                    	 Swal.fire({
                             icon: 'success',
                             title: 'Success',
                             text: 'File name updated successfully!',
                             allowOutsideClick: false
                         })
                        disableInlineEdit(pkId);
                        const newName = $('#input_' + pkId).val();
                        $('#span_' + pkId).contents().filter(function() {
                            return this.nodeType === 3;
                        }).first().replaceWith(newName + " ");
                    } else {
                        Swal.fire({
                            icon: 'error',
                            title: 'Error',
                            text: 'Update failed. Please try again.'
                        });
                    }
                },
                error: function() {
                	Swal.close(); // Close the loading swal
                    Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: 'An error occurred while updating the file.',
                    });
                }
            });
        }
    });
 }
 
function showUpload(type,name,id) {
    $('#uploadModal').data('folder-id', id).modal('show');
    $('#FileAttach').val('');
    $('#FileNameInput').val('');
	$('#isnewver').prop('checked', true);
	$('#isnonewver').prop('disabled', true);
    $('#submitversion').val('Upload Version 1.0');
    $('#fileType').val(type);
    $('#folderId').val(id);
    $('#modalTitle').text('Upload Document for '+name);
    toggleUploadType("Y");
}

function toggleUploadType(value) {
	const initId = $('#initiationId').val();
    const inputField = document.getElementById("FileNameInput");
    const dropdown = document.getElementById("FileNameSelect");

    $('.btn-group-toggle label').removeClass('active');
    if (value === 'Y') {
        $('#isNewUploadY').closest('label').addClass('active');
        $('.btn-outline-success').css({
            background: 'green',
            color: 'white'
        });
        $('.btn-outline-danger').css({
            background: 'white',
            color: 'red'
        });
        $('#FileNameSelect option:selected').attr('data-rep', '0');
    } else {
        $('#isNewUploadN').closest('label').addClass('active');
       
        $('.btn-outline-danger').css({
            background: 'red',
            color: 'white'
        });
        
        $('.btn-outline-success').css({
            background: 'white',
            color: 'green'
        });

    }
    
    if (value === "Y") {
        inputField.classList.remove("d-none");
        inputField.required = true;
        dropdown.classList.add("d-none");
        dropdown.required = false;
    	$('#isnewver').prop('checked', true);
    	$('#isnonewver').prop('disabled', true);
        $('#submitversion').val('Upload Version 1.0');
    } else {
        dropdown.classList.remove("d-none");
        dropdown.required = true;
        inputField.classList.add("d-none");
        inputField.required = false;
		$('#isnewver').prop('checked', false);
		$('#isnonewver').prop('checked', true);
		$('#isnonewver').prop('disabled', false);
		$('#submitversion').val('Submit');
        $('#FileNameSelect').find('option:not(:first)').remove();
         
        const fileId = $('#folderId').val();
        const fileType = $('#fileType').val();
        // Fetch document names via AJAX
        $.ajax({
            type: 'GET',
            url: 'getPreProjectOldFileNames.htm',
            data : {
    			initiationId : initId,
    			fileId : fileId,
    			fileType : fileType,
    		},
    		datatype : 'json',
            success: function(docNames) {
        	    var result = JSON.parse(docNames);
        	    if (Array.isArray(result) && result.length > 0) {
        	        $.each(result, function(index, name) {
        	        	$('#FileNameSelect').append(
                   	        $('<option>', {
                   	            value: name[6],
                   	            text: name[6]
                   	        })
                   	        .attr('data-rep', name[0])
                   	        .attr('data-version', name[4])
                   	        .attr('data-release', name[5])
                   	    );
        	        });
        	    }
            },
            error: function(err) {
                alert("Failed to load document names.");
                console.error(err);
            }
        });
    }
}

$(document).ready(function () {
    function updateSubmitVersion() {
        const selectedOption = $('#FileNameSelect option:selected');
        if (!selectedOption.length) return;

        const version = parseInt(selectedOption.attr('data-version')) || 0;
        const release = parseInt(selectedOption.attr('data-release')) || 0;

        let finalVersion = '';

        if ($('#isnonewver').is(':checked')) {
            // No: same version, release + 1
             finalVersion = version + '.' + (release + 1);
        } else if ($('#isnewver').is(':checked')) {
           // Yes: new main version = version + 1, release = 0
            finalVersion = (version + 1) + '.0';
        } else {
            finalVersion = '1.0'; // Fallback
        }

        $('#submitversion').val('Upload Version ' + finalVersion);
    }
    // Trigger only on user interaction
    $('#FileNameSelect').on('change', updateSubmitVersion);
    $('input[name="isnewversion"]').on('change', updateSubmitVersion);
});

function checkDuplicateFileName() {
	  const initId = $('#initiationId').val();
	  const fileName = $('#FileNameInput').val().trim();
	  const folderId = $('#folderId').val();
	  const fileType = $('#fileType').val();
	  
	   if (fileName !== '') {
			$.ajax({
			    type: 'GET',
			    url: 'getPreProjectOldFileNames.htm',
	            data : {
	    			initiationId : initId,
	    			fileId : folderId,
	    			fileType : fileType,
	    		},
			    dataType: 'json',
			    success: function(result) {
			        var isDuplicate = false;
			        
			        $.each(result, function(index, name) {
			            if (fileName.toLowerCase() === (name[6] || '').toLowerCase()) {
			                isDuplicate = true;
			                return false; // break loop
			            }
			        });
			        if (isDuplicate) {
			            Swal.fire({
			                icon: 'error',
			                title: 'Duplicate File Name',
			                text: fileName+' file name already exists. Please choose another.',
			                allowOutsideClick: false
			            });
			            $('#FileNameInput').val('').focus();
			        }
			    },
			    error: function(err) {
			    	Swal.fire({
	                  icon: 'error',
	                  title: 'Error',
	                  text: 'Could not check the file name. Please try again later.',
	                  allowOutsideClick: false
	              });
			    }
			});
	  } 
	}
	
function showSubFileDocs(mainId,subId,subName) {
	const initId = $('#initiationId').val();
	$('#addBtn').hide();
	$('#mainFolderId').hide();
	$('#spanValue').addClass('breadcrumb-link');
	$('#spanValue').on('click', function() {
		$('#addBtn').show();
    	$('#mainFolderId').show();
    	$("#subpdf-wrapper").empty();
    	$('#subTag').hide();
    	$('#subspanValue').hide();
    	$('#spanValue').removeClass('breadcrumb-link');
	});
	$('#subTag').show();
	$('#subspanValue').text(subName).show();
    const wrapper = $("#subpdf-wrapper");
    const containerId = "subpdf-container-" + subId;
    let container = $("#" + containerId);

    if (container.length > 0) {
        container.toggle();
        return;
    }

    container = $("<div>", {
        id: containerId,
        class: "d-flex flex-wrap gap-4",
        css: { display: "flex" }
    });
    wrapper.append(container);
    
    $.ajax({
        type: "GET",
        url: 'getPreProjectOldFileNames.htm',
        data : {
        	    initiationId : initId,
	   			fileId : subId,
	   			fileType : 'subLevel',
		},
        dataType: "json",
        success: function (data) {
            if (data.length === 0) {
                console.error("No document found");
            } else {
                $.each(data, function (index, file) {
                    const pdfItem ='<div class="pdf-item text-center">'+
		                                '<div class="file-icon-wrapper">'+
		                                    '<i class="fa fa-file-pdf-o fileicon" aria-hidden="true"></i>'+
		                                '</div>'+
		                                '<span class="file-download-span" onclick="fileDownload(' + file[7] + ', \'subLevel\')">' + file[6] + '</span><br/>'+
		                                '<span class="text-muted">Ver '+file[4]+'.'+file[5]+' <i class="fa fa-info-circle cursor" aria-hidden="true" onclick="showVersionModal(' + file[0] + ',\'' + file[6] + '\',\'subLevel\')"></i></span>'+
		                            '</div>';
                    container.append(pdfItem);
                });
            }
        },
        error: function (xhr, status, error) {
            console.error("AJAX Error:", error);
        }
    });
  }

function submitUpload() {
	event.preventDefault();
	const initId = $('#initiationId').val();
    const fileInput = document.getElementById("FileAttach");
    const textInput = document.getElementById("FileNameInput");
    const isnewversion = $('input[name="isnewversion"]:checked').val();
    const selectTab = $('input[name="isNewUpload"]:checked').val();
    const selectDropdown = $('#FileNameSelect').val();
    const file = fileInput.files[0];
    const docName = textInput.value.trim();
    const folderId = $('#folderId').val();
    const fileType = $('#fileType').val();
    const parentId = $('#fileParentId_'+folderId).val();
    const selectedOption = $('#FileNameSelect option:selected');
    const repId = selectedOption.attr('data-rep') || '0';


    // Validate input
    if (!file) {
        Swal.fire({
            icon: 'error',
            title: 'No File Selected',
            text: 'Please select a PDF file to upload.',
            allowOutsideClick: false
        });
        fileInput.value = ""; 
        return;
    }

    if (file.type !== 'application/pdf') {
        Swal.fire({
            icon: 'error',
            title: 'Invalid File Type',
            text: 'Only PDF files are allowed.',
            allowOutsideClick: false
        });
        fileInput.value = ""; 
        return;
    }

    if (file.size > 90 * 1024 * 1024) {
        Swal.fire({
            icon: 'error',
            title: 'File Too Large',
            text: 'File size must be less than 25MB.',
            allowOutsideClick: false
        });
        fileInput.value = ""; 
        return;
    }
    
    if(selectTab === 'N'){
    	 if (!selectDropdown) {
 	        Swal.fire({
 	            icon: 'error',
 	            title: 'Missing File Name',
 	            text: 'Please select a file name.',
 	            allowOutsideClick: false
 	        });
 	        return;
 	    }
    }else{
	    if (!docName) {
	        Swal.fire({
	            icon: 'error',
	            title: 'Missing File Name',
	            text: 'Please enter a file name.',
	            allowOutsideClick: false
	        });
	        textInput.value = ""; 
	        return;
	    }
    }

    // Prepare form data
    const formData = new FormData();
    formData.append("fileAttach", file);
    formData.append("fileType", fileType);
    formData.append("docName",  docName || selectDropdown);
    formData.append("fileRepId", repId);
    formData.append("initiationId", initId);
    formData.append("mainLevelId", fileType === 'mainLevel' ? folderId : parentId);
    formData.append("subLevelId", fileType === 'mainLevel' ? '0' : folderId);
    formData.append("isnewversion", isnewversion);
    formData.append("${_csrf.parameterName}", "${_csrf.token}");
    

    // Submit via AJAX
     Swal.fire({
            title: 'Are you sure to upload?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: 'green',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes'
        }).then((result) => {
            if (result.isConfirmed) {
            	
              	 Swal.fire({
                     title: 'Uploading...',
                     text: 'Please wait while the file is being uploading.',
                     allowOutsideClick: false,
                     didOpen: () => {
                         Swal.showLoading();
                     }
                 });
            	
                $.ajax({
                	url: 'uploadPreProjectFile.htm',
                    type: 'POST',
                    data: formData,
                    contentType: false,
                    processData: false,
                    success: function(response) {
                    Swal.close();
                    var result = JSON.parse(response);
	                     if(result > 0){
	                         Swal.fire({
	                             icon: 'success',
	                             title: 'Uploaded!',
	                             text: 'Your document has been uploaded successfully.',
	                             allowOutsideClick: false
	                         });
	                         $('#uploadModal').data('folder-id', folderId).modal('hide');
	                         fileInput.value = ""; 
	                         textInput.value = "";
	                         if(fileType === 'mainLevel'){
	                        	 showSubfolders(folderId);
	                         }else{
	                        	 const subName = $('#fileSubName_'+folderId).val();
	                        	 showSubFileDocs(parentId,folderId,subName);
	                         }
	                     }else{
	                         Swal.fire({
	                             icon: 'error',
	                             title: 'Upload Failed',
	                             text: 'Something went wrong. Please try again.',
	                             allowOutsideClick: false
	                         });
	                     }
                    },
                    error: function() {
                    	Swal.close();
                        Swal.fire({
                            icon: 'error',
                            title: 'Upload Failed',
                            text: 'Something went wrong. Please try again.',
                            allowOutsideClick: false
                        });
                    }
                });
            } else {
                console.log("Action cancelled");
            }
      });
}

function fileDownload(fileId, fileType) {
    $.ajax({
        url: 'preProjectFileDownload.htm/' + fileId + '?fileType=' + encodeURIComponent(fileType),
        type: 'GET',
        xhrFields: {
            responseType: 'blob'
        },
        success: function (data, status, xhr) {
        	
        	  const blob = new Blob([data], { type: 'application/pdf' });
              // Create a blob URL and open it in a new tab
              const blobUrl = URL.createObjectURL(blob);
              const viewerUrl = '<%=request.getContextPath()%>/pdf-viewer?url=' + encodeURIComponent(blobUrl);
              window.open(viewerUrl, '_blank');
              // Optional: release memory later
              setTimeout(() => URL.revokeObjectURL(blobUrl), 5000);
        },
        error: function (xhr, status, error) {
            alert("Failed to download/open file: " + (xhr.responseText || error));
            console.error("Download error:", error);
        }
    });
 }
 
function showVersionModal(fileRepId,fileName,fileType) {
	  $('#versionModal').modal('show');
	  $('#versionModalLabel').text('Version History of ' + fileName);
	  $('#versionTableBody').html('<tr><td colspan="5" class="text-center">Loading...</td></tr>');

	  $.ajax({
		type: 'GET',
	    url: 'getPreProjectDocVersionList.htm',
	    data : {
	    	fileRepId : fileRepId,
		},
		datatype : 'json',
	    success: function(response) {
			var result= JSON.parse(response);
			var values= Object.keys(result).map(function(e){
				return result[e];
			})
	      if (!values || values.length === 0) {
	        $('#versionTableBody').html('<tr><td colspan="5" class="text-center text-muted">No previous versions found.</td></tr>');
	        return;
	      }

	      var rows = '';
	      for (var i = 0; i < values.length; i++) {
	        var version = values[i];
	        rows += '<tr>' +
	                  '<td>' + (i + 1) + '</td>' +
	                  '<td>' + version[3] + '</td>' +
	                  '<td>v' + version[4] + '.' + version[5] + '</td>' +
	                  '<td>' + RegularDateFormat(version[6]) + '</td>' +
	                  '<td>' + version[7] + ', ' + version[8] + '</td>' +
	                  '<td><i class="fa fa-download cursor" onclick="fileDownload(' + version[0] + ', \'' + fileType + '\')"></i></td>' +
	                '</tr>';
	      }
	      $('#versionTableBody').html(rows);
	    },
	    error: function() {
	      $('#versionTableBody').html('<tr><td colspan="5" class="text-danger text-center">Failed to load versions.</td></tr>');
	    }
	  });
}

function RegularDateFormat(UserDate)
{
  /* Date Formation  */
	 var d = new Date(UserDate),
     month = '' + (d.getMonth() + 1),
     day = '' + d.getDate(),
     year = d.getFullYear();
 if (month.length < 2) 
     month = '0' + month;
 if (day.length < 2) 
     day = '0' + day;
 var expectedDate=[day,month,year].join('-');
 return expectedDate;
}

</script>

</body>
</html>