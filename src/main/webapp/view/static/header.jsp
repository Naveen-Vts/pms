
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

<style>
    /* Unique Grid Container */
    .pms-grid-container {
        display: grid !important;
        grid-template-columns: repeat(3, 1fr) !important;
        gap: 15px !important;
        padding: 15px !important;
        width: 280px !important;
        background-color: #ffffff !important;
        border-radius: 12px !important;
    }
/* Hidden by default */
#global-loader {
    display: none;
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(255, 255, 255, 0.7);
    z-index: 9999;
    justify-content: center;
    align-items: center;
    flex-direction: column;
}
    /* Individual Item Styling */
    .pms-grid-item {
        display: flex !important;
        flex-direction: column !important;
        align-items: center !important;
        justify-content: center !important;
        text-align: center !important;
        padding: 12px 5px !important;
        border-radius: 8px !important;
        color: #333333 !important;
        transition: background 0.2s ease-in-out !important;
        text-decoration: none !important;
        border: 1px solid transparent !important;
        cursor: pointer !important;
    }

    /* Hover effect for the grid boxes */
    .pms-grid-item:hover {
        background-color: #f4f7fa !important;
        border: 1px solid #e0e6ed !important;
        text-decoration: none !important;
    }

    /* Icon and Text spacing */
    .pms-grid-item i {
        font-size: 24px !important;
        margin-bottom: 8px !important;
        display: block !important;
    }

    .pms-grid-item span {
        font-size: 11px !important;
        font-weight: 700 !important;
        color: #444 !important;
        display: block !important;
    }

    /* Ensure the dropdown menu itself doesn't have weird padding */
    .pms-grid-dropdown-menu {
        padding: 0 !important;
        border: none !important;
        box-shadow: 0 10px 30px rgba(0,0,0,0.15) !important;
    }
</style>
</head>


<% String Logintype= (String)session.getAttribute("LoginType");
String labcode= (String)session.getAttribute("labcode");

//int ProjectInitSize = (Integer) session.getAttribute("ProjectInitiationList");

%>

<body>

	<div class="wrapper" id="wrapper">

		<div id="content-wrapper" class=" flex-column">

			<div id="content">
				<% String Username =(String)session.getAttribute("Username"); 
				String token =(String)session.getAttribute("token"); 
				%>
				<% String EmpName =(String)session.getAttribute("EmpName");  %>
				<% long FormRole =(Long)session.getAttribute("FormRole");  %>
				<% String FormRoleName =(String)session.getAttribute("LoginTypeName");  %>
				<% String IsDG =(String)session.getAttribute("IsDG"); 
				  String encryptedUser = Base64.getEncoder().encodeToString(Username.getBytes());
				%>

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
        <li class="nav-item active navActive">
            
        <div class="dropdown d-inline-block mr-2">
    <a class="nav-link" href="javascript:void(0)" role="button" id="appGridMenu" 
       data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" 
       style="padding: 5px 10px; display: inline-block;">
        <i class="fa fa-th" aria-hidden="true" style="font-size: 22px; color: white !important;"></i>
    </a>
    
    <div class="dropdown-menu dropdown-menu-right pms-grid-dropdown-menu" aria-labelledby="appGridMenu">
        <div class="pms-grid-container">
            <a class="pms-grid-item" onclick="openAMS()" data-toggle="tooltip" title="Audit Management System" >
                <i class="fa fa-tasks" style="color: #e74c3c !important;"></i>
                <span>AMS</span>
            </a>
            <a class="pms-grid-item" onclick="openTMDS()" data-toggle="tooltip" title="TMDS">
                <i class="fa fa-file-code-o" style="color: #3498db !important;"></i>
                <span>TMDS</span>
            </a>
            <a class="pms-grid-item" onclick="openDMS()" data-toggle="tooltip" title="DAK Management System">
                <i class="fa fa-envelope-open" style="color: #9b59b6 !important;"></i>
                <span>DMS</span>
            </a>
            <a class="pms-grid-item" onclick="openPFTS()" data-toggle="tooltip" title="PFTS">
                <i class="fa fa-file-text" style="color: #f1c40f !important;"></i>
                <span>PFTS</span>
            </a>
            <a class="pms-grid-item" onclick="openEMS()" data-toggle="tooltip" title="Employee Management System">
                <i class="fa fa-user-circle" style="color: #7f8c8d !important;"></i>
                <span>EMS</span>
            </a>
            <a class="pms-grid-item" onclick="openIBAS()" data-toggle="tooltip" title="DAK Management System">
                <i class="fa fa-rupee" style="color: #27ae60 !important;"></i>
                <span>IBAS</span>
            </a>
            <a class="pms-grid-item" onclick="openHRMS()" data-toggle="tooltip" title="Human Resource Management System">
                <i class="fa fa-users" style="color: #16a085 !important;"></i>
                <span>HRMS</span>
            </a>
             <a class="pms-grid-item"  data-toggle="tooltip" target="_blank" title="STORES INVENTORY SYSTEM" href="http://192.168.1.87:8898/Inventory/TMDS?api_key=VTS_<%=encryptedUser %>">
                <i class="fa fa-shopping-cart" style="color:Orange !important;"></i>
                <span>SIS</span>
            </a>
        </div>
    </div>
</div>
            <button type="button" class="btn btn-sm btn-light btnHeight" onclick="opensmartsearch()">
                 <b>Search </b>&#x1F50D;
            </button>
            
            <a class="btn custom-button bgtrans" href="MainDashBoard.htm">
                <i class="fa fa-home font12rem" aria-hidden="true"></i> Home
            </a>
        </li>
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

<div id="global-loader">
    <div class="spinner-border text-primary"></div>
    <p>Verifying Access...</p>
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

				
				
				  /** * 1. Transfer Session data to LocalStorage 
				   * This ensures the data is available for your logic below.
				   */
				  const sessionUser = {
				      token: "<%= token %>",
				      username: "<%= Username %>"
				  };

				  // Only set it if the session actually contained data
				  if (sessionUser.token && sessionUser.username) {
				      localStorage.setItem('user', JSON.stringify(sessionUser));
				  }

				  /**
				   * 2. The openAMS Function
				   */
				   const openAMS = async () => { // Added 'async' here
					    const userData = localStorage.getItem("user");
					    
					    if (!userData || userData === '{"token":"","username":""}') {
					        console.error("No session data found. Please log in.");
					        return;
					    }
					    
					    try {
					        // 'await' waits until the AJAX success function would have fired
					        // and assigns the actual 'result' to the variable 'exist'
					        const exist = await checkAccess("AMS");

					        if (exist) {
					            const targetOrigin = "http://192.168.1.150:3000";
					            const amsWindow = window.open(targetOrigin + "/dashboard?ams=true", "_blank");

					            let count = 0;
					            const checkInterval = setInterval(() => {
					                if (amsWindow && count < 5) { 
					                    amsWindow.postMessage(
					                        { type: "LOGIN_SUCCESS", user: JSON.parse(userData) },
					                        targetOrigin
					                    );
					                    count++;
					                } else {
					                    clearInterval(checkInterval);
					                }
					            }, 1000); 
					        } else {
					            alert("You don't have access to AMS");
					        }
					    } catch (error) {
					        console.error("Request failed", error);
					    }
					};
					
					
					const openTMDS = async () => { // Added async
					    const userData = localStorage.getItem("user");
					    
					    // 1. Validation: Session Check
					    if (!userData || userData === '{"token":"","username":""}') {
					        console.error("No session data found. Please log in.");
					        return;
					    }

					    try {
					        // 2. Wait for access verification
					        const exist = await checkAccess("TMDS");

					        if (exist) {
					            const targetOrigin = "http://192.168.1.20:3001";
					            const tmdsWindow = window.open(targetOrigin + "/maindashboard?tmds=true", "_blank");

					            let count = 0;
					            const checkInterval = setInterval(() => {
					                // 3. Send message to the new window
					                if (tmdsWindow && count < 5) { 
					                    tmdsWindow.postMessage(
					                        { type: "LOGIN_SUCCESS", user: JSON.parse(userData) },
					                        targetOrigin
					                    );
					                    count++;
					                } else {
					                    clearInterval(checkInterval);
					                }
					            }, 1000); // 1 second is usually safer for cross-origin window loading
					        } else {
					            // 4. Handle no access
					            alert("You don't have access to TMDS");
					        }
					    } catch (error) {
					        console.error("Error verifying TMDS access:", error);
					        alert("An error occurred while checking system permissions.");
					    }
					};
					  
					const openDMS = async () => { // Marked as async
					    const userData = localStorage.getItem("user");
					    
					    // 1. Validation: Session Check
					    if (!userData || userData === '{"token":"","username":""}') {
					        console.error("No session data found. Please log in.");
					        return;
					    }

					    try {
					        // 2. Wait for the access check to complete
					        const exist = await checkAccess("DMS");

					        if (exist) {
					            const targetOrigin = "http://192.168.1.26:3002";
					            const dmsWindow = window.open(targetOrigin + "/dashboard?dms=true", "_blank");

					            let count = 0;
					            const checkInterval = setInterval(() => {
					                // 3. Message passing logic
					                if (dmsWindow && count < 5) { 
					                    dmsWindow.postMessage(
					                        { type: "LOGIN_SUCCESS", user: JSON.parse(userData) },
					                        targetOrigin
					                    );
					                    count++;
					                } else {
					                    clearInterval(checkInterval);
					                }
					            }, 1000); 
					        } else {
					            // 4. Permission denied handling
					            alert("You don't have access to DMS");
					        }
					    } catch (error) {
					        console.error("Error verifying DMS access:", error);
					        alert("An error occurred while verifying system permissions.");
					    }
					};
					
						  
					const openPFTS = async () => { // Marked as async
					    const userData = localStorage.getItem("user");
					    
					    // 1. Validation: Session Check
					    if (!userData || userData === '{"token":"","username":""}') {
					        console.error("No session data found. Please log in.");
					        return;
					    }

					    try {
					        // 2. Perform the asynchronous access check for PFTS
					        const exist = await checkAccess("PFTS");

					        if (exist) {
					            const targetOrigin = "http://192.168.1.26:3000";
					            const pftsWindow = window.open(targetOrigin + "/dashboard?pfts=true", "_blank");

					            let count = 0;
					            const checkInterval = setInterval(() => {
					                // 3. Message passing to the new window
					                if (pftsWindow && count < 5) { 
					                    pftsWindow.postMessage(
					                        { type: "LOGIN_SUCCESS", user: JSON.parse(userData) },
					                        targetOrigin
					                    );
					                    count++;
					                } else {
					                    clearInterval(checkInterval);
					                }
					            }, 1000); 
					        } else {
					            // 4. Alert user if access is denied
					            alert("You don't have access to PFTS");
					        }
					    } catch (error) {
					        console.error("Error verifying PFTS access:", error);
					        alert("An error occurred while verifying system permissions.");
					    }
					};
					
					
					const openEMS = async () => { // Marked as async
					    const userData = localStorage.getItem("user");
					    
					    // 1. Validation: Session Check
					    if (!userData || userData === '{"token":"","username":""}') {
					        console.error("No session data found. Please log in.");
					        return;
					    }

					    try {
					        // 2. Wait for the access check to complete for EMS
					        const exist = await checkAccess("EMS");

					        if (exist) {
					            const targetOrigin = "http://192.168.1.26:3003";
					            const emsWindow = window.open(targetOrigin + "/dashboard?ems=true", "_blank");

					            let count = 0;
					            const checkInterval = setInterval(() => {
					                // 3. Message passing logic
					                if (emsWindow && count < 5) { 
					                    emsWindow.postMessage(
					                        { type: "LOGIN_SUCCESS", user: JSON.parse(userData) },
					                        targetOrigin
					                    );
					                    count++;
					                } else {
					                    clearInterval(checkInterval);
					                }
					            }, 1000); 
					        } else {
					            // 4. Permission denied handling
					            alert("You don't have access to EMS");
					        }
					    } catch (error) {
					        console.error("Error verifying EMS access:", error);
					        alert("An error occurred while verifying system permissions.");
					    }
					};
								  
					const openIBAS = async () => {
					    const userData = localStorage.getItem("user");
					    
					    // Validation: Don't open if no user data exists
					    if (!userData || userData === '{"token":"","username":""}') {
					        console.error("No session data found. Please log in.");
					        return;
					    }

					    try {
					        // Wait for verification
					        const exist = await checkAccess("IBAS");

					        if (exist) {
					            const targetOrigin = "http://192.168.1.62:3000";
					            const ibasWindow = window.open(targetOrigin + "/dashboard?ibas=true", "_blank");

					            let count = 0;
					            const checkInterval = setInterval(() => {
					                if (ibasWindow && count < 5) { 
					                    ibasWindow.postMessage(
					                        { type: "LOGIN_SUCCESS", user: JSON.parse(userData) },
					                        targetOrigin
					                    );
					                    count++;
					                } else {
					                    clearInterval(checkInterval);
					                }
					            }, 1000); 
					        } else {
					            alert("You don't have access to IBAS");
					        }
					    } catch (error) {
					        console.error("Error verifying IBAS access:", error);
					        alert("An error occurred while verifying system permissions.");
					    }
					};
					
					const openHRMS = async () => {
					    const userData = localStorage.getItem("user");
					    const $loader = $('#global-loader');
					    // Validation: Don't open if no user data exists
					    if (!userData || userData === '{"token":"","username":""}') {
					        console.error("No session data found. Please log in.");
					        return;
					    }

					    try {
					        // Wait for verification
					        $loader.css('display', 'flex'); // Show Loader
					        const exist = await checkAccess("HRMS");

					        if (exist) {
					            const targetOrigin = "http://192.168.1.150:3000";
					            const hrmsWindow = window.open(targetOrigin + "/dashboard?hrms=true", "_blank");

					            let count = 0;
					            const checkInterval = setInterval(() => {
					                if (hrmsWindow && count < 5) { 
					                    hrmsWindow.postMessage(
					                        { type: "LOGIN_SUCCESS", user: JSON.parse(userData) },
					                        targetOrigin
					                    );
					                    count++;
					                } else {
					                    clearInterval(checkInterval);
					                }
					            }, 1000); 
					        } else {
					            alert("You don't have access to HRMS");
					        }
					    } catch (error) {
					        console.error("Error verifying HRMS access:", error);
					        alert("An error occurred while verifying system permissions.");
					    }
					    finally {
					        $loader.hide(); // Hide Loader
					    }
					};
										  function checkAccess(app) {
											    console.log("Checking access for: " + app);
											    
											    // Return the promise created by $.ajax
											    return $.ajax({
											        type: 'GET',
											        url: 'user-login-app-access',
											        dataType: 'json',
											        data: {
											            projectCode: app
											        },
											        error: function(err) {
											            console.error("Access check failed", err);
											        }
											    });
											}	  
</script>


</html>