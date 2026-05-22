<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.time.format.TextStyle"%>
<%@page import="java.time.LocalDate"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat"%>
    <%@page import="java.time.LocalTime"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>

<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/committeeModule/CommitteeAutoScheduleNP.css" var="CommitteeAutoScheduleNP" />
<link href="${CommitteeAutoScheduleNP}" rel="stylesheet" />
<title> AUTO SCHEDULE</title>
</head>
<body>
<% 
SimpleDateFormat sdf1=new SimpleDateFormat("dd-MM-yyyy");
SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
Object[] committeedata= request.getAttribute("committeedata") !=null ? (Object[])request.getAttribute("committeedata") : new Object[100];
Object[] startdate=(Object[])request.getAttribute("startdate");
List<Object[]> CommitteeAutoScheduleList=(List<Object[]>)request.getAttribute("CommitteeAutoScheduleList");
String Dashboard= (String)request.getAttribute("dashboard");
List<Object[]> committeelist=(List<Object[]>)request.getAttribute("committeelist");
String committeeid=(String)request.getAttribute("committeeid");
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
    
    
    <div class="container-fluid">
		
	<div class="row">
		<div class="col-md-12">	
				
			<%if(Dashboard.equalsIgnoreCase("nondashboard")){ %>	
				
			<div class="card shadow-nohover">
				<div class="card-header">
					
					<div class="row">
					
						<div class="col-md-4">	
							<h3 class="control-label" > <%=committeedata[1]!=null?StringEscapeUtils.escapeHtml4(committeedata[1].toString()): " - " %> Auto Schedule </h3> 
						</div>
						
						<div class="col-md-5">	</div>
						
							<div class="col-md-3">	
													
						</div>		
						
					</div>
				</div>
				
		
				<form action="NonProjectCommitteeAutoSchedule.htm" method="post" id="myfrm" >
				<div class="card-body">
						
							<div class="row">
							
									<div class="col-md-2">
										<div class="form-group">
											<label class="control-label">From </label>
											 <input  class="form-control"  data-date-format="dd/mm/yyyy" readonly="readonly" id="fdate" name="fromdate"  required="required" value="<%=startdate[0]!=null?StringEscapeUtils.escapeHtml4(startdate[0].toString()): ""%>">
										</div>
									</div>

									<div class="col-md-2">
										<div class="form-group">
											<label class="control-label">To </label> 
											<input  class="form-control"  data-date-format="dd/mm/yyyy" readonly="readonly" id="tdate" name="todate"  required="required" value="">
										</div>
									</div>
								<div class="col-md-3">
									<div class="form-group">
										<label class="control-label">Time : </label>
										<input  class="form-control" type="text" id="starttime" name="starttime"  required="required"  readonly="readonly" value="<%=LocalTime.now() %>" >
									</div>
								</div>

							</div>
							<input type="hidden" name="committeeid" value="<%=committeedata[0]%>"/>												
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />					
					</div> 
					
					<div class="row" >
						<div class="col-md-5"></div>
						<div class="col-md-3">
							<div class="form-group">
								<button class="btn btn-primary btn-sm submit" type="submit" name="submit" value="SUBMIT" onclick="return confirm('Are You Sure To Start Auto Scheduling ?');">SUBMIT</button>
								<button type="button" class="btn btn-primary btn-sm back" onclick="submitForm('backfrm')" >BACK</button>
							</div>
						</div>
					</div>					
				</form>	
				
				<form action="CommitteeList.htm" method="post" id="backfrm">
					<input type="hidden" name="id" value="N" />
					<input type="hidden" name="projectid" value="0" />
					<input type="hidden" name="npc" value="Y" />
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				
				</form>
				
				
				
			</div>		
			
			<%} %>
				
		</div>
	</div>
	
	<br>
	
	<div class="row">
			<div class="col-md-12">
				<div  <%if(!Dashboard.equalsIgnoreCase("nondashboard")){ %>	class="card shadow-nohover mt-n2"<%}else{ %>class="card shadow-nohover" <%} %> >
				
					<div class="card-header ">
					<div class="row">
					
						<%if(Dashboard.equalsIgnoreCase("nondashboard")){ %>	
					
						<h3 class="col-md-7">Previous Schedules List</h3>
					
						<%} %>
						
						<%if(!Dashboard.equalsIgnoreCase("nondashboard")){ %>	
						
						<h3 class="col-md-7">General Schedules List <%if(!committeeid.equals("A")){ %> (<%=committeedata[1]!=null?StringEscapeUtils.escapeHtml4(committeedata[1].toString()): " - " %> )  <%} %></h3>
					
						<%} %>
					
						<%if(!Dashboard.equalsIgnoreCase("nondashboard")){ %>	
					
						<div class="col-md-5 float-right">							
							<form class="form-inline" method="post" action="NonProjectCommitteeAutoSchedule.htm" id="myform">
								
									<h4 class="control-label" > Committee : </h4> &nbsp;&nbsp;&nbsp;
			
									 <select class="form-control selectdee"  id="committeeid" required="required" name="committeeid" onchange='submitdropdown();' >
					   						 <option value="all" <%if(committeeid.equals("A")){ %>selected<% } %> >All</option> 
					   						<% for (Object[] obj : committeelist) {%>					   						
											<option value="<%=obj[1]%>" <%if(obj[1].toString().equals(committeeid)){ %>selected<% } %> ><%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - "%>(<%=obj[5]!=null?StringEscapeUtils.escapeHtml4(obj[5].toString()): " - " %>)</option>												
											<%} %>											
					  				</select>
					  				
					  				<input type="hidden" name="dashboardlink" value="dashboard" id="dashboardlink" />							
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />	
							</form>		 				
						</div>	
						
						<%} %>
						
						
						</div>				
					</div>
					
					<div class="card-body">

						<div class="data-table-area mg-b-15">
							<div class="container-fluid">

								<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
									<div class="sparkline13-list">

										<div class="sparkline13-graph">
											<div class="datatable-dashv1-list custom-datatable-overright">
												
												<table id="table" data-toggle="table" data-pagination="true" data-search="true" data-show-columns="true"
													data-show-pagination-switch="true" data-show-refresh="true" data-key-events="true" data-show-toggle="true"
													data-resizable="true" data-cookie="true" data-cookie-id-table="saveId" data-show-export="true"
													data-click-to-select="true" data-toolbar="#toolbar">
													<thead>

														<tr>
															<th>Serial No</th>		
															<%-- <%if(committeeid.equals("all")){ %>
																<th>Committee Shortname</th>															
															<%} %> --%>							
															<th>Schedule Date</th>
															<th>Schedule Time</th>
															<th>Status</th>
															<th>Action</th>
														</tr>
													</thead>
													<tbody>
														<%int count=1;
										   					for (Object[] obj :CommitteeAutoScheduleList) {
										   						if(obj[3]==null || !obj[3].toString().equalsIgnoreCase(committeedata[1].toString())){
										   							continue;
										   						}
										   					%>
														<tr>
															<td><%=count %></td>
															
															<%-- <%if(committeeid.equals("all")){ %>
																<td><%=obj[3] %></td>															
															<%} %> --%>
																														
															
																<%String day=LocalDate.parse(obj[0].toString()).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.US);%>														
															<td   >	
																	<span<%if(day.equalsIgnoreCase("sunday") || day.equalsIgnoreCase("saturday")){ %> class="text-danger" <%} %> > <%=StringEscapeUtils.escapeHtml4(day)%> </span>&nbsp; 
																	- &nbsp;<%=LocalDate.parse(obj[0].toString()).getMonth().getDisplayName(TextStyle.FULL, Locale.US)%> &nbsp; 
																	- &nbsp; <%= sdf1.format(sdf.parse( obj[0].toString()))%>  
															</td>
															<td><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %></td>
															<td class="editable-click"><%if(obj[6]!=null){%>
																<a  href="CommitteeScheduleView.htm?scheduleid=<%=obj[4]%>" target="_blank" 
																
																<%if(obj[5].toString().equalsIgnoreCase("MSC") || obj[5].toString().equalsIgnoreCase("MAF") || obj[5].toString().equalsIgnoreCase("MMF") || obj[5].toString().equalsIgnoreCase("MKO")  ){ %> class="font definedColor"<%} %>
																<%if(obj[5].toString().equalsIgnoreCase("MAA") || obj[5].toString().equalsIgnoreCase("MKV") || obj[5].toString().equalsIgnoreCase("MMA") ){ %> class="font text-success" <%} %>
																<%if(obj[5].toString().equalsIgnoreCase("MAR") || obj[5].toString().equalsIgnoreCase("MMR") ){ %> class="font text-danger"<%} %>
																<%if(obj[5].toString().equalsIgnoreCase("MAS") || obj[5].toString().equalsIgnoreCase("MMS") ){ %> class="font text-warning" <%} %>
																
																 ><%=obj[6]!=null?StringEscapeUtils.escapeHtml4(obj[6].toString()): " - " %><%}else{ %>-<%} %></a>
															</td>
															
															<td class="left width text-left">

																<form action="CommitteeAutoScheduleEdit.htm" method="post" name="myfrm" class="displayline">

																	<button class="editable-click" name="sub"
																		value="Modify">
																		<div class="cc-rockmenu">
																			<div class="rolling">
																				<figure class="rolling_icon">
																					<img src="view/images/edit.png">
																				</figure>
																				<span>Modify</span>
																			</div>
																		</div>
																	</button>
																
																<input type="hidden" name="scheduleid" value="<%=obj[4] %>" /> 
																<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

																</form> 		
																	
															</td>
														</tr>
														
														<% count++; } %>

												</tbody>
												</table>
												
												<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" />


											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						
					</div> <!-- card-body end  -->
				</div> <!-- card-end -->
			</div>
		</div>
	
	
	
	
</div>
    

<script type="text/javascript">   
function submitForm(frmid)
{ 
  document.getElementById(frmid).submit(); 
} 

function submitdropdown(){
	
	$('#myform').submit();
	
}


$(function() {
	   $('#starttime').daterangepicker({
	            timePicker : true,
	            singleDatePicker:true,
	            timePicker24Hour : true,
	            timePickerIncrement : 1,
	            timePickerSeconds : false,
	            locale : {
	                format : 'HH:mm'
	            }
	        }).on('show.daterangepicker', function(ev, picker) {
	            picker.container.find(".calendar-table").hide();
	   });
	})


</script>   
<script type="text/javascript">

var fromDate=null;
$("#fdate").change( function(){	
	fromDate = $("#fdate").val();	
	fromDate= fromDate.split('-');
	var fDate = new Date (Number(fromDate[2]), Number(fromDate[1])-1,Number(fromDate[0] )); 
	fDate.setDate( fDate.getDate() +1 );
	
	
$('#tdate').daterangepicker({

	"singleDatePicker": true,
	"showDropdowns": true,
	"cancelClass": "btn-default",
	"minDate":fDate,
	
	locale: {
    	format: 'DD-MM-YYYY'
		}
		 	
});
});


$('#fdate').daterangepicker({
	
	"singleDatePicker": true,
	"showDropdowns": true,
	"cancelClass": "btn-default",
	<%-- "mindate":"<%=startdate[0]%>",  --%>
	<%if(startdate!=null && startdate[0]!=null){%>
		"startDate":"<%= sdf1.format(sdf.parse( startdate[0].toString()))%>", 
	<%}else{%>
		"startDate" : new Date(),
	<%}%>
	locale: {
    	format: 'DD-MM-YYYY'
		}
});
</script>

	
		
		
</body>

</html>