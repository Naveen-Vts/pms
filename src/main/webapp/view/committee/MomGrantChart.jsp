<%@page import="org.apache.commons.io.FileUtils"%>
<%@page import="java.io.File"%>
<%@page import="java.nio.file.Paths"%>
<%@page import="java.nio.file.Path"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="java.time.LocalDate"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat,java.io.ByteArrayOutputStream,java.io.ObjectOutputStream"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style type="text/css">
label{
	font-weight: bold;
  font-size: 13px;
}
.body-gant-chart{
	background-color: #f2edfa;
	overflow-x:hidden !important; 
}
h6{
	text-decoration: none !important;
}

 #containers {
    width: 100%;
    height: 50vh;
    margin: 0;
    padding: 0;
}

.anychart-credits {
   display: none;
}

.flex-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}
</style>
</head>
<body >
  <%

 
  String filePath=(String)request.getAttribute("filePath");
  String projectLabCode=(String)request.getAttribute("projectLabCode");
  String projectid = (String) request.getAttribute("projectid");
  String committeeCode = (String) request.getAttribute("committeeshortname");
  Object[] committeeMetingsCount =  (Object[]) request.getAttribute("committeeMetingsCount");
  String No2 = committeeCode+(Long.parseLong(committeeMetingsCount[1].toString())+1);
  
 %>
 
<div class="container-fluid body-gant-chart">
		<div class="row">
			<div class="col-md-12">
				<div class="card shadow-nohover">
					<div class="card-header ">  

					<div class="row">						
							
			   		</div>	   							

					</div>
						<div class="card-body " style="padding: 10px;"> 
						
							
								<!-- <div class="row" style="margin-bottom: 5px;font-weight: bold;"   >
										<div class="col-md-12 d-flex justify-content-end">
											<div style="font-weight: bold; " >
												<span style="margin:0px 0px 10px  10px;">Original :&ensp; <span style=" background-color: #455a64;;  padding: 0px 15px; border-radius: 3px;"></span></span>
												<span style="margin:0px 0px 10px  15px;">Ongoing :&ensp; <span style=" background-color: #059212;  padding: 0px 15px;border-radius: 3px;"></span></span>
												<span style="margin:0px 0px 10px  15px;">Revised :&ensp; <span style=" background-color: #F5A623; opacity: 0.5; padding: 0px 15px;border-radius: 3px;"></span></span>
												<span style="margin:0px 0px 10px  15px;">Delay Ongoing :&ensp; <span style=" background-color: #D0021B; padding: 0px 15px;border-radius: 3px;" ></span></span>
												<span style="margin:0px 0px 10px  15px;">Completed Within Time :&ensp; <span style="background-color: #6F42C1;padding: 0px 15px;border-radius: 3px;"></span></span>
												<span style="margin:0px 0px 10px  15px;">Completed With Delay :&ensp; <span style="background-color: #8B5A2B;padding: 0px 15px;border-radius: 3px;"></span></span>
												
											</div>
										</div>
									</div> -->
							<div class="row" >
								
								<div class="col-md-12" style="float: right;" align="center">
							
										<!-- <div class="flex-container" id="containers" ></div> -->
										  <% 
								              String fileName = String.format("grantt_%s_%s.png", projectid, No2);
								              Path uploadPath = Paths.get(filePath,projectLabCode,"gantt",fileName);
											  File file = uploadPath.toFile();
								              if(file.exists()){ %>
												<div style="font-weight: bold;" align="right">
													<br>
													<span >
														<span style="margin:0px 0px 10px  10px;">Original :&ensp; <span style=" background-color: #455a64;;  padding: 0px 15px; border-radius: 3px;"></span></span>
														<span style="margin:0px 0px 10px  15px;">Ongoing :&ensp; <span style=" background-color: #059212;  padding: 0px 15px;border-radius: 3px;"></span></span>
														<span style="margin:0px 0px 10px  15px;">Revised :&ensp; <span style=" background-color: #F5A623; opacity: 0.5; padding: 0px 15px;border-radius: 3px;"></span></span>
														<span style="margin:0px 0px 10px  15px;">Delay Ongoing :&ensp; <span style=" background-color: #D0021B; padding: 0px 15px;border-radius: 3px;" ></span></span>
														<span style="margin:0px 0px 10px  15px;">Completed Within Time :&ensp; <span style="background-color: #6F42C1;padding: 0px 15px;border-radius: 3px;"></span></span>
														<span style="margin:0px 0px 10px  15px;">Completed With Delay :&ensp; <span style="background-color: #8B5A2B;padding: 0px 15px;border-radius: 3px;"></span></span>												
													</span>
												</div>
												<div align="center"><br>
													<img class="logo" style="max-width:25cm;max-height:17cm;margin-bottom: 5px" src="data:image/*;base64,<%=Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(file))%>" alt="confi" > 
												</div>
										    <% }else{ %>
										    	<div align="center">
										    		<br>
										    		File Not Found
										    	</div>
											<%} %>

                        		</div>
						   	</div>
						
						</div>
					
				</div>
					
		
		</div>
	</div>
</div>
</body>
</html>
