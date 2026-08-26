<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat,java.time.LocalDate"%>
    
   
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>

<title>Annual Meeting Schedules</title>


 <style type="text/css">
 
 p{
  text-align: justify;
  text-justify: inter-word;
}

  
 th
 {
 	border: 1px solid black;
 	text-align: center;
 	vertical-align:middle !important;
 	padding: 5px;
 }
 
 td
 {
 	border: 1px solid black;
 	text-align: center;
 	padding: 5px;
 	font-weight: bold; 
 }

 .textcenter{
 	
 	text-align: center;
 }
 .border
 {
 	border: 1px solid black;
 }
 .textleft{
 	text-align: left;
 }
 
 #containers {
    width: 100%;
    height: 100%;
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

summary[role=button] {
  background-color: white;
  color: black;
  border: 1px solid black ;
  border-radius:5px;
  padding: 0.5rem;
  cursor: pointer;
  
}
summary[role=button]:hover
 {
color: white;
border-radius:15px;
background-color: #4a47a3;

}
 summary[role=button]:focus
{
color: white;
border-radius:5px;
background-color: #4a47a3;
border: 0px ;

}
summary::marker{
	
}
details { 
  margin-bottom: 5px;  
}
details  .content {
background-color:white;
padding: 0 1rem ;
align: center;
border: 1px solid black;
}


.notheld{
 color: #FF4500;
}

.held{
 color: #006400;
}
.toheld{
 color: #151B8D;
}
</style>


<meta charset="ISO-8859-1">

</head>
<body >
<%

List<Object[]> AnnualReport=(List<Object[]>)request.getAttribute("projectslist");
SimpleDateFormat sdf5 = new SimpleDateFormat("dd-MM-yyyy");
String year=(String)request.getAttribute("year");

%>
<div class="row" style="top: 10px;padding:12px;" > 
<div class="col-sm-12"  >
<div class="card" >
<div  class="card-header">
   <form name="myfrm" action="AnnualMeetingSchedules.htm" method="GET">
                 <div class="row">
                     <div class="col-md-10" align="left">
                     <h5>Annual Meeting Schedules (PMRB/EB)</h5>
                     </div>
                 <div class="col-md-1" >
                 <input class="form-control  form-control" type="text" id="year" value="<%=year %>"  name="year" style="width: 70px; height: 100%;">
                 </div>  
                 <div class="col-md-1" >
                 <input type="submit"  class="btn btn-success btn-sm " />
                 </div> 
                 </div>
                 
                 
                 
                   
                   </form>  
  </div>
   <div class="card-body" align="center" >
	
 <div class="table-responsive" style="margin-top: 4px;">
	   <table class="table  table-bordered table-hover table-striped table-condensed " >  
	  <thead>
	  <tr>
	  <th> Project </th>
	  <th ><b id="M1" >JAN</b></th>
	  <th ><b id="M2" >FEB</b></th>
	  <th ><b id="M3" >MAR</b></th>
	  <th ><b id="M4" >APR</b></th>
	  <th ><b id="M5" >MAY</b></th>
	  <th ><b id="M6" >JUN</b></th>
	  <th ><b id="M7" >JUL</b></th>
	  <th ><b id="M8" >AUG</b></th>
	  <th ><b id="M9" >SEP</b></th>
	  <th ><b id="M10" >OCT</b></th>
	  <th ><b id="M11" >NOV</b></th>
	  <th ><b id="M12" >DEC</b></th>
	  </tr>
	  </thead>
	  	
	  <tbody>
	      <%
           for(Object[] obj:AnnualReport){
	   %>
	   <tr>
	   <td><%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - " %></td>
	   <td><b id="<%=obj[0]%>M1" ></b></td>
	   <td><b id="<%=obj[0]%>M2" ></b></td>
	   <td><b id="<%=obj[0]%>M3" ></b></td>
	   <td><b id="<%=obj[0]%>M4" ></b></td>
	   <td><b id="<%=obj[0]%>M5" ></b></td>
	   <td><b id="<%=obj[0]%>M6" ></b></td>
	   <td><b id="<%=obj[0]%>M7" ></b></td>
	   <td><b id="<%=obj[0]%>M8" ></b></td>
	   <td><b id="<%=obj[0]%>M9" ></b></td>
	   <td><b id="<%=obj[0]%>M10" ></b></td>
	   <td><b id="<%=obj[0]%>M11" ></b></td>
	   <td><b id="<%=obj[0]%>M12" ></b></td>
	   </tr>
	   
	       <% } %>
	 </tbody>
	 </table>
	 </div>
	 </div>
	 </div>
	 </div>
	 </div>
	 
	 <script type="text/javascript">

$('#year').datepicker({
	 format: "yyyy",
	    viewMode: "years", 
	    minViewMode: "years",
	    autoclose: true,
	    todayHighlight: true,
});


window.addEventListener("load", myInit, true); function myInit(){  
	<%for(Object[] login: AnnualReport){ %>
	       getTotal('<%=login[0]%>','1',<%=year %>);
	       getTotal('<%=login[0]%>','2',<%=year %>);
	       getTotal('<%=login[0]%>','3',<%=year %>);
	       getTotal('<%=login[0]%>','4',<%=year %>);
	       getTotal('<%=login[0]%>','5',<%=year %>);
	       getTotal('<%=login[0]%>','6',<%=year %>);
	       getTotal('<%=login[0]%>','7',<%=year %>);
	       getTotal('<%=login[0]%>','8',<%=year %>);
	       getTotal('<%=login[0]%>','9',<%=year %>);
	       getTotal('<%=login[0]%>','10',<%=year %>);
	       getTotal('<%=login[0]%>','11',<%=year %>);
	       getTotal('<%=login[0]%>','12',<%=year %>);
	<%} %>

}; 




function getTotal(id,month,year){

	$.ajax({
			
			type : "GET",
			url : "getMeetingSchedules.htm",
			data : {
				
				projectId:id,
				month:month,
				year:year
				
			},
			datatype : 'json',
			success : function(result) {
			var result = JSON.parse(result);
			var consultVals= Object.keys(result).map(function(e){
				return result[e]
				})
				
            
			var otherHTMLStr ='';
			for(var c=0;c<consultVals.length;c++)
 			{
				otherHTMLStr+='<a class="font" href="CommitteeScheduleView.htm?scheduleid='+consultVals[c][6]+'" target="_blank">';
				if(c>0){
					otherHTMLStr+=' ,<br>';
				}
				if(consultVals[c][4]=='0'){
					otherHTMLStr+='<span class="toheld">';
				}else{
					if(consultVals[c][2]>5){
					otherHTMLStr+='<span class="held">';
					}else{
					 otherHTMLStr+='<span class="notheld">';
					}
				}
				
				otherHTMLStr+=consultVals[c][3].split("/")[2]+'-'+consultVals[c][5]+' <br>'+consultVals[c][0];
				otherHTMLStr+='</span></a>';
				otherHTMLStr+='<br><form class="form-inline" method="POST" action="CommitteeMinutesViewAll.htm" target="_blank" style="display: inline">';
				otherHTMLStr+='<button class="editable-click" name="sub" value="Modify" style="height: 30px;">';
				otherHTMLStr+='<div class="cc-rockmenu">';
				otherHTMLStr+='<div class="rolling">';
				otherHTMLStr+='<figure class="rolling_icon">';
				otherHTMLStr+='<img src="view/images/preview3.png">';
				otherHTMLStr+='</figure>';
				otherHTMLStr+='</div>';
				otherHTMLStr+='</div> </button> <input type="hidden" name="committeescheduleid" value="'+consultVals[c][6]+'">';
			    otherHTMLStr+='<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" /> </form>';
			}
			$('#'+id+'M'+month).html(otherHTMLStr);
			}
		});
		
	
}
</script>
</body>
</html>