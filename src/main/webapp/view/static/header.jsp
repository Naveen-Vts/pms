
<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*"%>
<%@ page import="java.time.LocalDate"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>

<!DOCTYPE html>
<html>
<head>
<%String loginPage= (String)session.getAttribute("loginPage"); %>
<meta charset="ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1.0" />

<title><%=loginPage.equalsIgnoreCase("login")?"PMS":"WR" %></title>

<link rel="shortcut icon" type="image/png"
	href="view/images/drdologo.png">
<meta charset="UTF-8">


<spring:url value="/resources/css/dashboard.css" var="dashboardCss" />
<link href="${dashboardCss}" rel="stylesheet" />

<spring:url value="/resources/css/newfont.css" var="NewFontCss" />
<link href="${NewFontCss}" rel="stylesheet" />

<spring:url value="/resources/css/master.css" var="masterCss" />
<link href="${masterCss}" rel="stylesheet" />


<jsp:include page="dependancy.jsp"></jsp:include>

<spring:url value="/resources/js/input_validations.js" var="inputvalidationsjs" /> 
<script src="${inputvalidationsjs}"></script>

<spring:url value="/resources/css/header/headerCss.css" var="headerCss" />     
<link href="${headerCss}" rel="stylesheet" />


</head>


<% String Logintype= (String)session.getAttribute("LoginType");
String labcode= (String)session.getAttribute("labcode");

//int ProjectInitSize = (Integer) session.getAttribute("ProjectInitiationList");

%>

<body>

	<div class="wrapper" id="wrapper">

		<div id="content-wrapper" class=" flex-column">

			<div id="content">
				<% String Username =(String)session.getAttribute("Username");  %>
				<% String EmpName =(String)session.getAttribute("EmpName");  %>
				<% long FormRole =(Long)session.getAttribute("FormRole");  %>
				<% String FormRoleName =(String)session.getAttribute("LoginTypeName");  %>
				<% String IsDG =(String)session.getAttribute("IsDG");  %>

				<nav
					class="navbar navbar-expand-lg navbar-dark mx-background-top-linear header-top">


					<div class="container-fluid">

						<a class="navbar-brand navHeader1" id="brandname"
							>
							<span id="p1" class="cspan"
							></span>
							<span class="dspan"><%=LocalDate.now().getMonth() %>
								&nbsp; <%=LocalDate.now().getYear() %> </span> <img class="projectImageHeade"
							src="view/images/project.png" alt=""><b> &nbsp;
								<%=loginPage.equalsIgnoreCase("login")?"PMS":"WR" %>&nbsp;<span class="font13">(<%=labcode!=null?StringEscapeUtils.escapeHtml4(labcode): " - " %>) -
									<%=EmpName!=null?StringEscapeUtils.escapeHtml4(EmpName): " - " %> (<%=FormRoleName!=null?StringEscapeUtils.escapeHtml4(FormRoleName): " - " %>)
							</span>
						</b>
						</a>


						<button class="navbar-toggler" type="button"
							data-toggle="collapse" data-target="#navbarResponsive"
							aria-controls="navbarResponsive" aria-expanded="false"
							aria-label="Toggle navigation">
							<span class="navbar-toggler-icon"></span>
						</button>

<!-- Button trigger modal -->
						

						<!-- Modal -->
							<div class="modal fade bd-example-modal-xl" id="smartsearch"
								tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel"
								aria-hidden="true">

								<div class="modal-dialog modal-dialog-centered" role="document">
									<div class="modal-content">
										<div class="modal-header">
											<div class="container">
												<div class="row">
													<div class="col-lg">
														<input autocomplete="off" autofocus
															placeholder="Enter Module Name To Navigate"
															required="required" oninput="changed()" id="projectids"
															class="form-control" type="text">
													</div>
												</div>
											</div>
											<button type="button" class="close" data-dismiss="modal"
												aria-label="Close">
												<span aria-hidden="true">&times;</span>
											</button>
										</div>
										<div class="modal-body" id="targets"></div>
									</div>
								</div>
							</div>

						<div class="collapse navbar-collapse" id="navbarResponsive">
							<ul class="navbar-nav ml-auto ">
								<%if(loginPage.equalsIgnoreCase("login")) {%>
								<li class="nav-item active navActive" >
								<button type="button" class="btn btn-sm btn-light btnHeight"
									onclick="opensmartsearch()"  >
									 <b>Search </b>&#x1F50D;
								</button>
								<a class="btn custom-button bgtrans"
									href="MainDashBoard.htm" type="button" aria-haspopup="true"
									aria-expanded="false" ><i
										class="fa fa-home font12rem" aria-hidden="true"
										s></i> Home</a></li>
								<%} %>

								<!-- New Content from table start --------------------------------->

								<li class="nav-item dropdown"><input type="hidden"
									value="<%=Logintype %>" name="logintype" id="logintype">
									<%if(loginPage.equalsIgnoreCase("login")) {%>
									<ul class="navbar-nav" id="uppermodule">

									</ul>
									<%} %>
								</li>



								<li class="nav-item"><a class="nav-link" href="#">&nbsp;&nbsp;&nbsp;</a>
								</li>
								<%if(loginPage.equalsIgnoreCase("login")) {%>
								<li class="nav-item">

									<div class="btn-group  ">

										<a  class="nav-link dropdown-toggle colorWhite"
											href="#" id="alertsDropdown" role="button"
											data-toggle="dropdown" aria-haspopup="true"
											aria-expanded="false"> <i class="fa fa-bell fa-fw colorWhite"
											aria-hidden="true" ></i> <span
											class="badge badge-counter" id="NotificationCount"></span>
										</a>

										<div
											class="dropdown-list dropdown-menu dropdown-menu-right custombell font15"
											
											aria-labelledby="">
											<h6 class="dropdown-header">
												<img src="view/images/notification.png">
												&nbsp;&nbsp;&nbsp;&nbsp;Notifications
											</h6>


											<div id="Notification"></div>



											<a
												class="dropdown-item text-center small text-gray-500 showall"
												href="NotificationView.htm">Show All Alerts </a>
										</div>
									</div>

								</li>

								<%} %>
								<li class="nav-item">


									<div class="btn-group   ">

										<a class="nav-link dropdown-toggle" href="#" id="userDropdown"
											role="button" data-toggle="dropdown" aria-haspopup="true"
											aria-expanded="false"> <span
											class="mr-2 d-none d-lg-inline text-gray-600 colorWhite"
											><b><%=Username!=null?StringEscapeUtils.escapeHtml4(Username): " - " %></b></span> <i
											class="fa fa-user-o colorWhite" aria-hidden="true" ></i>
										</a>

										<div
											class="dropdown-menu dropdown-menu-right shadow animated--grow-in"
											aria-labelledby="userDropdown">
											<a class="dropdown-item" href="PasswordChange.htm"> <img
												src="view/images/key.png" /> &nbsp;&nbsp; Password Change
											</a> 
											<%if(loginPage.equalsIgnoreCase("login")) {%>
											<a class="dropdown-item" href="UserManualDoc.htm"
												target="_blank"> <img src="view/images/handbook.png" />
												&nbsp;&nbsp; Manual
											</a> <a class="dropdown-item" href="WorkFlow.htm" target="_blank">
												<img src="view/images/work.png" /> &nbsp;&nbsp; Work Flow
											</a> <a class="dropdown-item" href="MilestoneManual.htm"
												target="_blank"> <img src="view/images/milestone.png" />
												&nbsp;&nbsp; Milestone Manual
											</a> <a class="dropdown-item" href="AuditStampingView.htm"> <img
												src="view/images/stamping.png" /> &nbsp;&nbsp;
												Audit Stamping
											</a> <!-- <a class="dropdown-item" href="AuditPatchesView.htm"> <img
												src="view/images/updatepatch.jpg" style="width: 35px;height:30px"/> &nbsp;&nbsp;
												Audit Patches
											</a> --><a class="dropdown-item h13w16" href="RunBatchFile.htm"> <img
												src="view/images/backup.png"
												/> &nbsp;&nbsp;DB Back-up
											</a> <a class="dropdown-item" href="DelegationFlow.htm"> <img
												src="view/images/workflow.png" /> &nbsp;&nbsp; Delegation
												Flow
											</a> 
											<a class="dropdown-item" href="FeedBack.htm"> <img
												src="view/images/feedback.png" /> &nbsp;&nbsp; Feedback
											</a> 
											<a class="dropdown-item" href="AboutPFM.htm" target="_blank">
												<img src="view/images/work.png" /> &nbsp;&nbsp; About PMS
											</a>
                                            <a class="dropdown-item" href="PDManual.htm"
												target="_blank"> <img src="view/images/milestone.png" />
												&nbsp;&nbsp; PD Manual
											</a>
											<%} %>
                                            <!-- <a class="dropdown-item" href="TimeSheetWorkFlowPdf.htm"
												target="_blank"> <img src="view/images/calendar.png" />
												&nbsp;&nbsp; Work Register Work Flow
											</a> -->
                                           <a class="dropdown-item" href="PMSHelpGuide.htm"
												target="_blank"> <i class="fa fa-question-circle help-icon" aria-hidden="true"></i>
												&nbsp;&nbsp; Help
											</a> 

											<div class="dropdown-divider"></div>
											<form id="logoutForm" method="POST"
												action="${pageContext.request.contextPath}/logout">
												<input type="hidden" name="${_csrf.parameterName}"
													value="${_csrf.token}" />
												<button class="dropdown-item " href="#"
													data-target="#logoutModal">
													&nbsp;&nbsp;<img src="view/images/logout.png" />
													&nbsp;Logout
												</button>
											</form>
										</div>
									</div>
								</li>
							</ul>
						</div>
					</div>
				</nav>

				<!------------------------------------------------ new navbar  ---------------------------------------->

				<nav
					class="navbar navbar-expand-lg navbar-light second-nav header-top ">

					<a class="navbar-brand" href="#"></a>
					<button class="navbar-toggler" type="button" data-toggle="collapse"
						data-target="#navbarSupportedContent"
						aria-controls="navbarSupportedContent" aria-expanded="false"
						aria-label="Toggle navigation">
						<span class="navbar-toggler-icon"></span>
					</button>

					<input type="hidden" value="<%=Logintype %>" name="logintype"
						id="logintype">

					<div class="collapse navbar-collapse justify-content-end mr1P"
						id="navbarSupportedContent" >

						<ul class="navbar-nav" id="module">

						</ul>

					</div>
				</nav>

				<!------------------------------------------------ new navbar end ------------------------------------->

<!-- <button type="button" style="display: none;" id="storeSessionData"></button> -->
			</div>


			<script type="text/javascript">
	$(document).ready(function() {
		var loginPage = '<%=loginPage%>';
		
		$('.selectdee').select2();
		
		$.ajax({
			type : "GET",
			url : "HeaderModuleList.htm",
			
			datatype : 'json',
			success :  function(result){
				
				var result = JSON.parse(result);
				var values = Object.keys(result).map(function(e){
					return result[e]
				})
				var module= "";
				var logintype= $('#logintype').val();
				var uppermodule = "";
				
				for(i=0; i<values.length;i++){

					if(values[i][3]=='L'){
						
						var name=values[i][1].replace(/ /g,'');
						if(loginPage=='wr' && values[i][0]=='17') {
							module+="<li class='nav-item dropdown uppernav p035' ><button class='btn dropdown-toggle custom-button' type='button' value='"+values[i][0]+"_"+values[i][2]+"' id='"+name+"'  data-toggle='dropdown' aria-haspopup='true' aria-expanded='false' onmouseover='checkme(\"" +name+ "\")' >"+values[i][1]+"</button> <div class='dropdown-menu dropdown-menu-right width-13r' id='scheduledropdown"+name+"' > </div></li>";
						}else if(loginPage=='login'){
							module+="<li class='nav-item dropdown uppernav p035' ><button class='btn dropdown-toggle custom-button' type='button' value='"+values[i][0]+"_"+values[i][2]+"' id='"+name+"'  data-toggle='dropdown' aria-haspopup='true' aria-expanded='false' onmouseover='checkme(\"" +name+ "\")' >"+values[i][1]+"</button> <div class='dropdown-menu dropdown-menu-right width-13r' id='scheduledropdown"+name+"' > </div></li>";
						}
						
					}
					
					if(values[i][3]=='U'){
						
						var name=values[i][1].replace(/ /g,'');

						uppermodule+="<li class='nav-item dropdown '><button class='btn dropdown-toggle custom-button bgtrans' type='button'   value='"+values[i][0]+"_"+values[i][2]+"' id='"+name+"'  data-toggle='dropdown' aria-haspopup='true' aria-expanded='false' onmouseover='checkme(\"" +name+ "\")' >"+values[i][1]+"</button> <div class='dropdown-menu dropdown-menu-right' id='scheduledropdown"+name+"' > </div></li>";
						
					}
					
					
					
				}
				$('#module').html(module); 
				if(loginPage=='login') {
					$('#uppermodule').html(uppermodule);
				}

			}
			
			
		})
		
		
		

	});
	
	
	function checkme(value){

		  var result = $("#"+value).val().split('_'); 
		
		  var $url = result[1];
		  
		  var $formmoduleid = result[0];
				
	      var $logintype = $('#logintype').val(); 
	      	      
						$
						.ajax({

							type : "GET",
							url :  "HeaderMenu.htm" ,
							data : {
								logintype : $logintype,
								formmoduleid : $formmoduleid
							},
							datatype : 'json',
							success : function(result) {

								var result = JSON.parse(result);
							
								
								var values = Object.keys(result).map(function(e) {
								  return result[e]
								})
								
								var s = '';
								for (i = 0; i < values.length; i++) {
									s += '<a class="dropdown-item" href="'+values[i][1]+'">' +values[i][0]+ '</a>';

								}
								
								$('#scheduledropdown'+value).html(s);
				
							}
						});
		
		}
	


         $('span.navbar-btn').click(function() {
             $('#navbar').toggle();
         });
         
		

function english_ordinal_suffix(dt)
{
  return dt.getDate()+(dt.getDate() % 10 == 1 && dt.getDate() != 11 ? 'st' : (dt.getDate() % 10 == 2 && dt.getDate() != 12 ? 'nd' : (dt.getDate() % 10 == 3 && dt.getDate() != 13 ? 'rd' : 'th'))); 
}

dt= new Date();
document.getElementById("p1").innerHTML = english_ordinal_suffix(dt);

/* $(document).ready(function(){
	
	$.ajax({
		type : "GET",
		url : "NotificationList.htm",
		
		datatype : 'json',
		success : function(result) {
			
			var result = JSON.parse(result);
			var values = Object.keys(result).map(function(e) {
				  return result[e]
				});
			var module = "";
			for (i = 0; i < values.length; i++) {
			
				module+="<a class='dropdown-item d-flex align-items-center' id='"+values[i][5]+"'  onclick='test("+values[i][5]+")' href='"+values[i][4]+"'  style=' font-family:'Quicksand', sans-serif; '> <div> <i class='fa fa-arrow-right' aria-hidden='true' style='color:green'></i></div> <div style='margin-left:20px'> " +values[i][3]+" </div> </a>";
				if(i>4){
					break;
				}
		   
			}
		
			if(values.length==0){
				
				var info="No Notifications to display !";
				var empty="";
				 empty+="<a class='dropdown-item d-flex align-items-center' href=# style=' font-family:'Quicksand', sans-serif; '> <div> <i class='fa fa-comment-o' aria-hidden='true' style='color:green;font-weight:800'></i></div> <div style='margin-left:20px'>" +info+" </div> </a>";

				$('#Notification').html(empty); 
				$('.showall').hide();
				$('#NotificationCount').addClass('badge-success');
			}
			
			if(values.length>0){
 			
				$('#Notification').html(module);
				$('.showall').show();
				
			
			}
			
			
			
			$('#NotificationCount').html(values.length); 
		}
	});
	
}); */
//new anil code
$(document).ready(function(){
	
	
	$.ajax({
		type : "GET",
		url : "NotificationList.htm",
		
		datatype : 'json',
		success : function(result) {
			
			var result = JSON.parse(result);
			var values = Object.keys(result).map(function(e) {
				  return result[e]
				});
			
			var module = "";
			for (i = 0; i < values.length; i++) {
			
				module+="<a class='dropdown-item d-flex align-items-center fontfam' id='"+values[i][5]+"'   onclick='deleteNoti()' href='"+values[i][4]+"' > <div> <i class='fa fa-arrow-right colorGreen' aria-hidden='true' ></i></div> <div class='mar20'> " +values[i][3]+" </div> </a>";
				if(i>4){
					break;
				}
		   
			}
		
			if(values.length==0){
				
				var info="No Notifications to display !";
				var empty="";
				 empty+="<a class='dropdown-item d-flex align-items-center fontfam' href=# > <div> <i class='fa fa-comment-o colgreenFw' aria-hidden='true' ></i></div> <div class='mar20'>" +info+" </div> </a>";

				$('#Notification').html(empty); 
				$('.showall').hide();
				$('#NotificationCount').addClass('badge-success');
			}
			
			if(values.length>0){
 			
				$('#Notification').html(module);
				$('.showall').show();
				
			
			}
			
			
			
			$('#NotificationCount').html(values.length); 
		}
	});
	
});

// new code by anil
function deleteNoti() {
	$.ajax({
		type : "GET",
		url : "getAllNoticationId.htm",
	
		datatype : 'json',
		success : function(result) {
			
		}
	});
}

function test(img){
	
	var notificationid=img;
	
	$.ajax({
		type : "GET",
		url : "NotificationUpdate.htm",
		data : {
				notificationid : notificationid,
				
			},
		datatype : 'json',
		success : function(result) {
			
		}
	});
	
	
}


	
	
	$(document).on('click', '.dropdown-menu', function (e) {
		  e.stopPropagation();
		});

		// make it as accordion for smaller screens
		if ($(window).width() < 992) {
		  $('.dropdown-menu a').click(function(e){
		    e.preventDefault();
		      if($(this).next('.submenu').length){
		        $(this).next('.submenu').toggle();
		      }
		      $('.dropdown').on('hide.bs.dropdown', function () {
		     $(this).find('.submenu').hide();
		  })
		  });
		}
		
		
		window.setTimeout(function() {
            $(".alert").fadeTo(500, 0).slideUp(500, function(){
                $(this).remove(); 
            });
        }, 4000);
		



function myalert(msg){
	
	bootbox.alert({
  			message: "<center>&nbsp;&nbsp;&nbsp;&nbsp;<b class='editbox'>"+msg+"</b></center>",
  			size: 'large',
  			buttons: {
			        ok: {
			            label: 'OK',
			            className: 'btn-success'
			        }
			    }
  			
			});
}

function myconfirm(msg,frmid){
	
	 bootbox.confirm({ 
	 		
		    size: "large",
  			message: "<center>&nbsp;&nbsp;&nbsp;&nbsp;<b class='editbox'>"+msg+"</b></center>",
		    buttons: {
		        confirm: {
		            label: 'Yes',
		            className: 'btn-success'
		        },
		        cancel: {
		            label: 'No',
		            className: 'btn-danger'
		        }
		    },
		    callback: function(result){	    

		    
		    	if(result){
		 
		         $("#"+frmid).submit(); 
		    	}
		    	else{
		    		event.preventDefault();
		    	}
		    } 
		}) 
	
	
}


</script>








</body>








<script>

function changed() {

					$.ajax({
						type : "GET",
						url : "SmartSearch.htm",
						data : {
							search : document.getElementById('projectids').value
						},
						datatype : 'json',
						success : function(result) {
							var result = JSON.parse(result);
							

							var printOutDiv = document
									.getElementById("targets");
							while (printOutDiv.firstChild) {
								printOutDiv.removeChild(printOutDiv.lastChild);
							}
							
							for (let i = 0; i < result.length; i++) {
								var amountPrintOutDiv = document
										.createElement("div");
								amountPrintOutDiv.innerHTML = "<a href='"
										+ result[i][3] + "' id='"
										+ result[i][0]
										+ "+id' onclick=searchForRole("
										+ result[i][0] + ") >" + result[i][2]
										+ "</a>";
								printOutDiv.appendChild(amountPrintOutDiv);
							}
						}
					});
				}

				function searchForRole(formname) {
				
					var currentloc = "";
					currentloc += String(window.location.href);
					currentloc = currentloc.split("").reverse().join("")
					currentloc = currentloc.substring(0, currentloc
							.indexOf("/"));
					$
							.ajax({
								type : "GET",
								url : "searchForRole.htm",
								data : {
									search : formname
								},
								datatype : 'json',
								success : function(result) {
									var result = JSON.parse(result);

									if (result)
										return true;
									else {
										window
												.alert("\"Sorry\", You don't have access to this module.\nPlease contact Administrator");
										window.location.href = currentloc
												.split("").reverse().join("");
									}
								}
							});
				}


				function opensmartsearch() {
					$('#smartsearch').modal('show')
				}
				
				$('#smartsearch').on('shown.bs.modal', function() {
					  $(this).find('[autofocus]').focus();
					});
				
				$(document).ready(function() {
					$('#storeSessionData').click();
					
				});
				$('#storeSessionData').click(function(){
				
					var DashBoardId = "<%=(String)session.getAttribute("DashBoardId")%>";
					var path = window.location.pathname.split("/").includes("MainDashBoard.htm");
					
				
					if(path){
						$.ajax({
							type:'GET',
							url:'storeSlideData.htm',
							datatype:'json',
							data:{
								DashBoardId:DashBoardId,
							},
							 success:function(result){
								 
							 }
						})
					}
					
				
				})
				
				
				document.addEventListener("DOMContentLoaded", function () {
    // Select all input fields
    var inputs = document.querySelectorAll("input");

    inputs.forEach(function(input) {
        input.setAttribute("autocomplete", "off");
    });
});
				
				document.addEventListener("DOMContentLoaded", function () {
				    var elements = document.querySelectorAll("input, textarea, select");

				    elements.forEach(function(el) {
				        el.setAttribute("autocomplete", "off");
				    });
				});

</script>


</html>