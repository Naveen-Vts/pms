<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.vts.pfms.model.LabMaster"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"    pageEncoding="UTF-8" import="java.util.*,com.vts.*"%>
<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<html>
<head>
<title>PMS Login</title>
<%
   String cspNonce = (String) request.getAttribute("cspNonce");
%>

<jsp:include page="../static/dependancy.jsp"></jsp:include>
<spring:url value="/resources/css/LoginPage.css" var="loginPageCss" />
<link href="${loginPageCss}" rel="stylesheet" />
<spring:url value="/resources/css/header/loginPage.css" var="staticloginPageCss" />
<link href="${staticloginPageCss}" rel="stylesheet" />

<spring:url value="/resources/language-toggle.js" var="toggleJs"/>
<script src="${toggleJs}"></script>



</head>

<body class="home" >

<!--  Login Page  -->  
  
<section class="loginpage">
  

<%if(request.getAttribute("version").equals("yes")){ %>
 <!-- Button trigger modal -->
<button type="button"  class="btn btn-primary"  data-toggle="modal" data-target="#staticBackdrop" id = "versionerror">
</button>
<!-- Modal -->
<div class="modal fade" id="staticBackdrop" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="myLargeModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered  modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <font color="red"><h5 class="modal-title " data-lang="VERSION_MISMATCH"  id="staticBackdropLabel">Version Mismatch</h5></font>
      </div>
      <div class="modal-body center" ><b>
      <p id="version"></p>
        </b>
      </div>
      <div class="modal-footer">
      <button type="button" class="btn btn-primary" data-lang="CONTINUE" data-dismiss="modal">Still want to continue</button>
      </div>
    </div>
  </div>
</div>
<script nonce="<%= cspNonce %>">
document.getElementById("versionerror").click();
const paragraphElement = document.getElementById("version");
const originalText = paragraphElement.textContent; // Store original text for backup

// Choose the new word:
const replacementWord = "<%= request.getAttribute("browser")%>";

paragraphElement.innerHTML="<b>Your current Browser version is not supported.<br><br>Please ensure optimal viewing by using Internet Explorer (I.E) or Microsoft Edge 110+,<br> Mozilla 110+, or Google Chrome 110+.</b><br><br><b>Site Best viewed at a resolution of 1360 x 768.</b><br><br><b>Thank You!</b>"
console.log("browser name: "+replacementWord);
console.log(replacementWord+" version: "+"<%= request.getAttribute("versionint") %>");
</script>
  <%} %>
  
  
	<header id="header" class="clearfix">
	   
 
	
  		<div class="btmhead clearfix">
    		<div class="widget-guide clearfix">
      			<div class="header-right clearfix">
        			<div class="float-element">
        				<a class="" href="" target="_blank">
        					<img  class ="drdologo" src="view/images/drdo-logo.png"alt="">
        				</a>
        			</div>
      			</div>
     			<div class="logo">
     				<a href="#" title="PMS"><span class="c projName" data-lang="PMS_TITLE"  >PROJECT MANAGEMENT SYSTEM  (VER 1.7.0)</span></a>
     		
     		
     		
     			</div>
     			
     	       
    
     			
    		</div>

  		</div>
  		
  		
  				<ul class="nav nav-tabs justify-content-end ">
					  <li class="nav-item"    onclick="$('#footer').show();">
						 <a class="nav-link active" data-toggle="tab" href="#tab-1" role="tab" ><i class="fa fa-home" aria-hidden="true"></i>&nbsp;Home</a>
					  </li> 
					<!--   <li class="nav-item">
					    <a class="nav-link" href="LoginPage/PPFMDoc2016.htm" target="_blank" ><i class="fa fa-file-text" aria-hidden="true"></i>&nbsp; PPFM-2016</a>
					  </li> -->
					  <li class="nav-item">
					    <a class="nav-link" href="LoginPage/DPFMDoc2026.htm" target="_blank" ><i class="fa fa-file-text" aria-hidden="true"></i>&nbsp; DPFM-2026</a>
					  </li>					  
					  <li class="nav-item">
					    <a class="nav-link" href="LoginPage/DPFMDoc2021.htm" target="_blank" ><i class="fa fa-file-text" aria-hidden="true"></i>&nbsp; DPFM-2021</a>
					  </li>
					  <li class="nav-item">
					    <a class="nav-link" href="LoginPage/DPFMDoc2021Handbook.htm" target="_blank" ><i class="fa fa-file-text" aria-hidden="true"></i>&nbsp; DPFM Handbook-2021</a>
					  </li >
					  <li class="nav-item link">
					  <div class="language-wrapper">
					    <button id="languageToggle"
					            class="language-toggle-btn"
					            onclick="toggleLanguage()"
					            aria-label="Change Language">
					
					        <svg xmlns="http://www.w3.org/2000/svg"
					             width="24"
					             height="24"
					             viewBox="0 0 64 64"
					             fill="none"
					            class="language-icon" >
					            <path d="M37.6672 9.95973V31.9997H34.4272V9.95973H31.5071V7.11973H41.8271V9.95973H37.6672ZM22.5871 6.71973C24.6671 6.71973 26.2538 7.23973 27.3471 8.27973C28.4671 9.31973 29.0271 10.6264 29.0271 12.1997C29.0271 13.3464 28.7205 14.3864 28.1071 15.3197C27.5205 16.2264 26.6405 16.9464 25.4671 17.4797C24.2938 18.0131 22.8271 18.3064 21.0671 18.3597L20.8671 15.5597C22.6805 15.5064 23.9605 15.1864 24.7071 14.5997C25.4805 14.0131 25.8671 13.2264 25.8671 12.2397C25.8671 11.2797 25.5471 10.5864 24.9071 10.1597C24.2938 9.73306 23.5738 9.51973 22.7471 9.51973C21.7605 9.51973 20.8671 9.65306 20.0671 9.91973C19.2671 10.1864 18.4138 10.5464 17.5071 10.9997L16.5071 8.23973C17.2005 7.86639 18.0538 7.51973 19.0671 7.19973C20.1071 6.87973 21.2805 6.71973 22.5871 6.71973ZM29.4671 23.2797C29.4671 24.5064 29.1871 25.5331 28.6271 26.3597C28.0671 27.1864 27.3071 27.7997 26.3471 28.1997C25.4138 28.5997 24.3471 28.7997 23.1471 28.7997C21.6271 28.7997 20.2138 28.4264 18.9071 27.6797C17.6271 26.9331 16.4005 25.7464 15.2271 24.1197C14.0805 22.4931 12.9471 20.3731 11.8271 17.7597L14.6671 16.7197C15.4405 18.6131 16.2405 20.2531 17.0671 21.6397C17.9205 22.9997 18.8271 24.0531 19.7871 24.7997C20.7471 25.5197 21.7738 25.8797 22.8671 25.8797C23.8805 25.8797 24.7071 25.6531 25.3471 25.1997C25.9871 24.7197 26.3071 23.9597 26.3071 22.9197C26.3071 21.6397 25.8671 20.5331 24.9871 19.5997C24.1071 18.6664 23.0405 17.8131 21.7871 17.0397L24.1471 16.9197L25.8671 16.5597C26.2405 16.8797 26.6538 17.2664 27.1071 17.7197C27.5605 18.1731 27.9205 18.6264 28.1871 19.0797L28.3872 19.8397C28.7338 20.3464 29.0005 20.8797 29.1871 21.4397C29.3738 21.9997 29.4671 22.6131 29.4671 23.2797ZM30.1071 17.9997C31.3871 17.9997 32.4938 17.9064 33.4272 17.7197C34.3605 17.5064 35.4538 17.1731 36.7071 16.7197V19.5997C35.5605 20.1064 34.5205 20.4397 33.5871 20.5997C32.6805 20.7597 31.6805 20.8397 30.5871 20.8397C30.1871 20.8397 29.7205 20.8131 29.1871 20.7597C28.6538 20.6797 28.1471 20.5997 27.6671 20.5197C27.2138 20.4131 26.8805 20.3197 26.6671 20.2397L24.7871 17.9997L25.0271 17.3997C25.8005 17.5864 26.6138 17.7331 27.4671 17.8397C28.3205 17.9464 29.2005 17.9997 30.1071 17.9997Z"
					                  fill="currentColor"/>
					                  
					                  <path d="M52.3467 58.6664L49.136 50.4158H38.5707L35.3973 58.6664H32L42.416 31.8984H45.44L55.8187 58.6664H52.3467ZM48.128 47.4291L45.1413 39.3651C45.0667 39.1659 44.9421 38.8051 44.768 38.2824C44.5939 37.7598 44.4195 37.2246 44.2453 36.6771C44.096 36.1046 43.9715 35.6691 43.872 35.3704C43.6728 36.1419 43.4613 36.9011 43.2373 37.6478C43.0381 38.3696 42.864 38.9419 42.7147 39.3651L39.6907 47.4291H48.128Z" fill="#ffffff"></path>
					
					        </svg>		
							</button>
					          <span id="languageTooltip" class="lang-tooltip">
					            Change Language
					        </span>
					        </div>
					</li>
				</ul>
				
  		
	</header>
 </section> 
<div class="tab-content">

<!-- -----------------------------------Home--------------------------------------------- -->
<div class="tab-pane active tabHeight" id="tab-1" role="tabpanel" >
<!-- Login Page Content -->

 <div class="container m20" >
		<div class="row">
			<div class="col-md-12">
			
				<div class="login-container justify-content-center">
					<div class="row align-item-center">
						
						<div class="col-md-6">
							<div >
								
								<div>
									<p class="quote"  data-lang="QUOTE">Lets simplify project management</p>
								 	<h4 class="h4S" data-lang="TAGLINE" >Analytics  &nbsp;|&nbsp;  Insights  &nbsp;|&nbsp;  Empowerment</h4>
								</div>
								
								<div class="product-banner-container m35" >
									<img class="img-fluid img-responsive" src="view/images/bg4.jpg" >
								</div>
								
							</div>
						</div>
						
						
						<div class="col-md-6">
					
							<div class="row justify-content-end login-main-container ml7mt4"  >
	
								<div class="col-md-12">
								
									<div align="center"><h5  class="welcome h5S" data-lang="WELCOME">Welcome !</h5></div> 
									
									<div class="login-form-wrapper p43"  >
										
										<div class="login-info-container">
											<h4 class="h4st" data-lang="LOGIN" >Login</h4><br>
										</div>
										
										<div class="login-form-container">
										
											   <form action="${contextPath}/login" method="post" id="loginForm" >
											   
									
												<div class="form-row">
													
													<div class="form-group col-12 position-relative ${error != null ? 'has-error' : ''}">
														<input type="text" name="username" placeholder="Username" data-placeholder="USERNAME" class="form-control"  autocomplete="off" required>
														<i class="fa fa-user fa-lg position-absolute"></i>
													</div>
													
													<div class="form-group col-12 position-relative">
													    <input
													        name="password"
													        type="password"
													        placeholder="Password"
													        data-placeholder="PASSWORD"
													        id="password"
													        class="form-control pe-5"
													        autocomplete="new-password"
													    >
													
													    <!-- Lock icon -->
													    <!-- <i class="fa fa-lock fa-lg position-absolute"
													       style="left: 15px; top: 50%; transform: translateY(-50%);">
													    </i> -->
													
													    <!-- Eye button -->
													    <button
													        type="button"
													        class="btn position-absolute p-0 border-0 bg-transparent"
													        onclick="togglePassword()"
													        style="right: 15px; top: 50%; transform: translateY(-50%);"
													    >
													        <i id="passwordEye" class="fa fa-eye"></i>
													    </button>
													</div>
														
													<%-- <span style="font-family: 'Lato', sans-serif;font-size: 15px;color:red;margin-bottom: 10px;" id="error-alert">${error}</span>
													<span style="font-family: 'Lato', sans-serif;font-size: 15px;color:green;margin-bottom: 10px;" id="success-alert">${success}</span> --%>
													
															<span class="salert text-justify" id="success-alert">${error}</span>
																		
												</div>

														<%-- <div class="form-group">
															<label for="captchaInput">Enter Captcha:</label>
															<div style="display: flex; align-items: center;">
																<input type="text" name="captchaInput" id="captchaInput"
																	class="form-control" required
																	style="max-width: 150px; margin-right: 10px;">
																<span
																	style="font-weight: bold; font-size: 20px; background: #f0f0f0; padding: 5px 10px; letter-spacing: 3px; user-select: none;">
																	${captcha} </span>
															</div>
														</div> --%>
																<%-- <div class="form-group w150" >
																    <label for="captchaInput">Enter Captcha:</label>
																    <div class="g2s">
																        <input type="text" name="captchaInput" id="captchaInput"
																               class="form-control" required
																              >
																        <img id="captchaImage" src="data:image/png;base64,${captcha}"
																             alt="Captcha" >
																        <button type="button" id="refreshCaptcha" class="btn btn-secondary">&#x21bb;</button>
																    </div>
																</div> --%>

														<div class="form-submit">
													<div class="row align-items-center mb-5">
														<div class="col-md-5">
															<div class="form-submit-button">
																<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
																<button type="submit" data-lang="LOGIN" class="btn btn-block btn-success f2s"  >Login</button>
																<!-- <button type="submit" class="btn btn-link" formaction="fpwd/ForgotPassword.htm" > Forgot Password?</button> -->
															</div>
														</div>
													</div>
													
													<input type="hidden" id="sessionKey" name="encKey" value="<%=(String)request.getAttribute("sessionKey")%>" />
													<input type="hidden" id="sessionIv"  name="encIv"  value="<%=(String)request.getAttribute("sessionIv")%>" />
													
												</div>
												
												
											</form>
											
										</div>
										
										<div class="credentials-info-container m-35" >
											<div class="row">
												<div class="col-md-12">
													
													<div class="info-container text-md-left">
														<p class="text-secondary e2s" data-lang="LOGIN_WARNING" >* Do not share credentials with anyone</p>
													</div>
													
												</div>
											</div>
										</div>
										
									</div>
								</div>
							</div>
						</div>
						
					</div>
					
				</div>
			
			</div>
			
		</div>
	</div>	
	
	<div class="credentials-info-container d2s" >
    	<%
        	boolean expstatus = (boolean)request.getAttribute("expstatus");
       		if(!expstatus) {%>
				<marquee  class="news-scroll c2s" data-lang="LICENSE_EXPIRED" behavior="scroll" direction="left" scrollamount="7" onmouseover="this.stop();" onmouseout="this.start();" >Your License has been Expired..!</marquee>
		<%} %>
		<!-- <marquee  class="news-scroll" behavior="scroll" direction="left" scrollamount="7" onmouseover="this.stop();" onmouseout="this.start();" style="color: red;font-weight: bold;">Please ensure the Work Register details for January 2025 are filled in by 10 February 2025. Kindly disregard if it has already been completed.</marquee> -->
	</div>
	
	</div>

</div>	

   <div id="footer" class="fixed-bottom">
	<footer class="footer"  >
	
		<section id="fontSize" class="clearfix f1s" >
		  <section id="page" class="body-wrapper clearfix" >
		    	<!-- Blue Border for Login Page -->  
		    <div class="support-row clearfix" id="swapper-border" >
		      <div class="marquee-container" onmouseover="stopMarquee()" onmouseout="startMarquee()">
<!--         <marquee behavior="scroll" direction="left" scrollamount="10" id="marquee">
            <p style="font-size: 20px; color: white; line-height: 1.5em;">SMS Abbreviation  :  PMS SMS will be sent every morning at 7:20 AM, 
            AI - P  : ActionItem Pending,  AI - D : ActionItem Delay, AI - T : ActionItem Today, 
            MS - P : MileStone Actions Pending, MS - D : MileStone Actions Delay, MS - T : MileStone Actions Today, 
            MT - P : Meeting Actions Pending, MT - D : Meeting Actions Delay, MT - T : Meeting Actions Today </p>
        </marquee> -->
    </div>
		      	<div class="widget-guide clearfix">
		        </div>
		    </div> 
		    	
		  </section>  
		</section>
		<div class="widget-guide clearfix">
       		<div class="footr-rt">
            	<div class="copyright-content" data-lang="WEBSITE_MAINTAINED"> 
            		<p>Website maintained by Vedant Tech Solutions<br><b data-lang="SITE_VIEW">Site best viewed at 1360 x 768 resolution in I.E / Microsoft Edge 110+, Mozilla 110+, Google Chrome 110+</b>	</p> 
            	</div>
    		</div>
  		</div>
	</footer>
	</div> 


<script type="text/javascript" nonce="<%= cspNonce %>">
$("#success-alert") .fadeTo(3000, 1000).slideUp(1000, function ( ) {
    $("#success-alert").slideUp(1000);
});

$("#error-alert") .fadeTo(3000, 1000).slideUp(1000, function ( ) {
    $("#error-alert").slideUp(1000);
});

function togglePassword() {
    const passwordInput = document.getElementById("password");
    const passwordEye = document.getElementById("passwordEye");

    if (passwordInput.type === "password") {
        passwordInput.type = "text";
        passwordEye.classList.remove("fa-eye");
        passwordEye.classList.add("fa-eye-slash");
    } else {
        passwordInput.type = "password";
        passwordEye.classList.remove("fa-eye-slash");
        passwordEye.classList.add("fa-eye");
    }
}

</script>

<script nonce="<%= cspNonce %>">
    $(document).ready(function() {
    	
    	document.getElementById("refreshCaptcha").addEventListener("click", function() {
    	    fetch("${contextPath}/refresh-captcha")
    	        .then(response => response.json())
    	        .then(data => {
    	        	document.getElementById("captchaImage").src = data.captcha;
    	        })
    	        .catch(error => console.error("Error refreshing captcha:", error));
    	});
    	
        setInterval(function() {
            var docHeight = $(window).height();
            var footerHeight = $('#footer').height();
            var footerTop = $('#footer').position().top + footerHeight;
            var marginTop = (docHeight - footerTop + 10);

            $('.scrollpolicy').css('max-height', docHeight-155+ 'px' )
            
            if (footerTop < docHeight)
                $('#footer').css('margin-top', marginTop + 'px'); // padding of 30 on footer
            else
                $('#footer').css('margin-top', '0px');
            // console.log("docheight: " + docHeight + "\n" + "footerheight: " + footerHeight + "\n" + "footertop: " + footerTop + "\n" + "new docheight: " + $(window).height() + "\n" + "margintop: " + marginTop);
        }, 250);
    }); 
</script>
<% if( request.getAttribute("version").equals("no")){%>
<script nonce="<%= cspNonce %>">
const replacementWord = "<%= request.getAttribute("browser")%>";
console.log("browser name: "+replacementWord);
console.log(replacementWord+" version: "+"<%= request.getAttribute("versionint") %>");
</script>
<%}%>
<script type="text/javascript" nonce="<%= cspNonce %>">

$("#myTable1,#myTable2,#myTable3").DataTable({
    "lengthMenu": [10,20, 50, 75, 100],
    "pagingType": "simple",
    "language": {
	      "emptyTable": "No Record Found"
	    }

});


	var marquee = document.getElementById('marquee');
	
	function stopMarquee() {
	    marquee.stop();
	}
	
	function startMarquee() {
	    marquee.start();
	}
</script>

<script nonce="<%= cspNonce %>">
document.addEventListener("DOMContentLoaded", function () {
  var form = document.getElementById("loginForm");
  if (!form) return;

  form.addEventListener("submit", function(e) {
    var pwdField = document.getElementById("password");
    var pwd = pwdField.value;

    var keyBase64 = (document.getElementById("sessionKey") || {}).value || "";
    var ivBase64  = (document.getElementById("sessionIv")  || {}).value || "";

    var key = CryptoJS.enc.Base64.parse(keyBase64);
    var iv  = CryptoJS.enc.Base64.parse(ivBase64);

    if (pwd) {
      var encrypted = CryptoJS.AES.encrypt(
        CryptoJS.enc.Utf8.parse(pwd),
        key,
        { iv: iv, mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7 }
      );

      var encryptedBase64 = CryptoJS.enc.Base64.stringify(encrypted.ciphertext);
      pwdField.value = encryptedBase64;

      /* var keyInput = document.getElementsByName('encKey')[0];
      var ivInput  = document.getElementsByName('encIv')[0];
      if (keyInput) keyInput.value = keyBase64;
      if (ivInput)  ivInput.value  = ivBase64; */
    }

  });
});

$(function () {

    // Try to find the element
    const refreshBtn = document.getElementById("refreshCaptcha");
    if (refreshBtn) {
        refreshBtn.addEventListener("click", function () {
            loadCaptcha();
        });
    }

    // Optionally load captcha immediately on page load
    loadCaptcha();
});

// Common function
function loadCaptcha() {
    fetch("${contextPath}/refresh-captcha")
        .then(response => response.json())
        .then(data => {
            document.getElementById("captchaImage").src = data.captcha;
        })
        .catch(error => console.error("Error refreshing captcha:", error));
}

</script>

</body>
</html>