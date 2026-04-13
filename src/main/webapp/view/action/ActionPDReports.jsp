<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat,java.io.ByteArrayOutputStream,java.io.ObjectOutputStream"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/action/actionPDReports.css" var="actionPDReports" />
<link href="${actionPDReports}" rel="stylesheet" />
<spring:url value="/resources/css/action/actionCommon.css" var="actionCommon" />
<link href="${actionCommon}" rel="stylesheet" />
 

<title>PD Action Reports</title>

</head>
 
<body>
  <%
  FormatConverter fc=new FormatConverter(); 
  SimpleDateFormat sdf=fc.getRegularDateFormat();
  List<Object[]> AssigneeList=(List<Object[]>)request.getAttribute("StatusList");
  String ProjectId=(String)request.getAttribute("ProjectId");

 %>






<div class="container-fluid">
		<div class="row">
			<div class="col-md-12">
				<div class="card shadow-nohover">
					<div class="card-header ">  

					<div class="row">
						<h3 class="col-md-8">PD Action Reports</h3>  
							<div class="col-md-4 float-right">
							<table >
					   					<tr>
					   					<td >
					   							<label class="control-label td-label">Project : </label>
					   						</td>
					   						<td class="td-width">
					   						<form method="post" action="ActionWiseReport.htm" name="dateform" id="dateform">
                                                        <select class="form-control selectdee " name="Project" id="Project" required="required"  data-live-search="true"  >
                                                           	
                                                           <%
                                                           for(Object[] obj:AssigneeList){
                                                        	   String projectshortName=(obj[17]!=null)?" ("+obj[17].toString()+") ":"";
                                                        	   %>
														   <option value="<%=obj[0] %>" <%if(ProjectId.equalsIgnoreCase(obj[0].toString())){ %> selected="selected" <%} %>><%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()):" - "%> <%= projectshortName!=null?StringEscapeUtils.escapeHtml4(projectshortName):" - " %></option>	
														<%} %>
														<option value="0" <%if(ProjectId.equalsIgnoreCase("0")){ %> selected="selected" <%} %>>General</option>
																</select>	
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
													<input type="hidden" name="ProjectId"  id="ProId" /> 
													<input type="hidden" name="ActionType"  id="TypeId" />			
													</form>	
				   						</td>
					   						
					   						</tr>
					   						</table>
					   							</div>
		   				</div>	   							

					</div>
						<div class="card-body "> 
						<div class="row div-margin" >
						<div class="col-md-6 float-right"  align="center">
							
					   	<div id="container" class="custom-container"></div>
					   <div >	
					   <button onclick="submitForm('P');" class="btn btn-sm btn-p" > <b id="pending"></b> </button>
                        <button onclick="submitForm('F');" class="btn btn-sm btn-f"> <b id="forwarded"></b> </button>
                       <button onclick="submitForm('Y');" class="btn btn-sm btn-y" > <b id="completed"></b> </button>
                       <button onclick="submitForm('E');" class="btn btn-sm btn-e" > <b id="expired"></b> </button>
					</div>
                        </div>
					   	<div class="col-md-6 float-right" >
		                <div id="container2" class="container2"></div>
                      
                         </div>
        
					   	</div>
						 <div class="row">
						<div class="col-md-6 float-right" >
					   	<div id="chart_div"></div>
                        </div>
					   	<div class="col-md-6 float-right" >
		               
                      
                         </div>
        
					   	</div>
						</div>
					
						<div class="card-footer" align="right">&nbsp;</div>
					</div>
					
		
					
					
					
				</div>
			</div>
		</div>


			
		

	
<script type='text/javascript'> 
var Id2="<%=ProjectId%>";
function draw(Id){
	$("#ProId").val(Id);
	$
	.ajax({

		type : "GET",
		url : "ActionCount.htm",
		data : {
			ProjectId : Id
		},
		datatype : 'json',
		success : function(result) {

			var result = JSON.parse(result);
			var values = Object.keys(result).map(function(e) {
				  return result[e]
				});
			
			   var name=values[0];
               var Pending=values[1];
               var Forwarded=values[2];
               var Completed=values[3];
               var Expired=values[4];
               PieChart(name,Pending,Forwarded,Completed,Expired);
               document.getElementById('pending').innerHTML = Pending ;
               document.getElementById('forwarded').innerHTML = Forwarded ;
               document.getElementById('completed').innerHTML = Completed ;
               document.getElementById('expired').innerHTML = Expired ;
		}

	});
	
}
$(document).ready(function() {
	  draw(Id2);
	   $('#Project').on('change', function() {
		   
		   draw($("#Project").val());

	   });
	});
function PieChart(name,Pending,Forwarded,Completed,Expired) {
	$('#container div').remove();
	$('#container2 div').remove();
	anychart.onDocumentReady(function() {
		var data1 = [
			 {x: "Pending", value: Pending, fill:"#A4DD74"},
		      {x: "To Review", value: Forwarded,fill:"#ff8400"},
		      {x: "Completed", value: Completed,fill:"#448fea"},
		      {x: "Delayed", value: Expired,fill:"#e85342"}
			];
		  // set the data
		  var data = [
		      {x: "Pending", value: Pending, state:"selected",fill:"#A4DD74"},
		      {x: "To Review", value: Forwarded,fill:"#ff8400"},
		      {x: "Completed", value: Completed,fill:"#448fea"},
		      {x: "Delayed", value: Expired,fill:"#e85342"}
		  ];

		  // create the chart
		  var chart1 = anychart.pie3d(data);
		 // var chart = anychart.pie3d(data);
		  // set the chart title
		  //chart.title(name+" Action Report");
		 // chart.labels().position("outside");
		
		  // add the data
		  // create a 3d bar chart
         var chart2 = anychart.line();

          // create a bar series and set the data
         var series = chart2.column(data1);
         anychart.format.locales.default.numberLocale.decimalsCount = 0;
   
		  chart2.container('container');
		  chart2.draw();
		  chart1.title(name+" Action Report");
		  chart1.labels().position("outside");
		  chart1.innerRadius("30%");
		  chart1.container('container2');
		  chart1.draw();
		  
		  
		  
		});
}
function submitForm(type)
{ 
   $("#TypeId").val(type);
   document.getElementById('dateform').submit(); 
} 




$('#fdate').daterangepicker({
	"singleDatePicker" : true,
	"linkedCalendars" : false,
	"showCustomRangeLabel" : true,
	"cancelClass" : "btn-default",
	"maxDate" : new Date(),
	showDropdowns : true,
	locale : {
		format : 'DD-MM-YYYY'
	}
});





$('#tdate').daterangepicker({
	"singleDatePicker" : true,
	"linkedCalendars" : false,
	"showCustomRangeLabel" : true,
	"cancelClass" : "btn-default",
	"maxDate" : new Date(),
	
	showDropdowns : true,
	locale : {
		format : 'DD-MM-YYYY'
	}
});



function Prints(myfrm){
	
	 var fields = $("input[name='btSelectItem']").serializeArray();

	 
	  if (fields.length === 0){
		  myalert();
	 event.preventDefault();
	return false;
	}
	 
		  return true;
	 
	}







</script>

</body>
</html>