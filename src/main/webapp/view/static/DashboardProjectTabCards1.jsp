<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="java.math.BigDecimal"%>
<%@ page import="java.time.LocalDate"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat,java.text.ParseException,java.math.BigInteger"%>
<%@page import="com.vts.pfms.IndianRupeeFormat" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
 
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Lab Details</title>

<jsp:include page="../static/header.jsp"></jsp:include>

<spring:url value="/resources/css/newfont.css" var="NewFontCss" />
<link href="${NewFontCss}" rel="stylesheet" /> 

<spring:url value="/resources/css/master.css" var="masterCss" />
<link href="${masterCss}" rel="stylesheet" />

<spring:url value="/resources/css/masterdashboard.css" var="masterdashboardCss" />
<link href="${masterdashboardCss}" rel="stylesheet" />


<style type="text/css">

 	#wrapper{
		background-image: url("view/images/pfmsbg.png") !important;
		background-repeat: no-repeat;
		background-size: auto 100%;
		width: 100%;
	}
	
	.myschedule{
		background-image: url("view/images/myschedule.png") !important;
		background-repeat: no-repeat;
		background-position: left;
		background-position-x: 5px;
    	padding-left: 16px;
	}
	
	.gantt{
	background-image: url("view/images/gantt.png");
	background-repeat: no-repeat;
	background-position: left;
	background-position-x: 5px;
    padding-left: 16px;
}

.approval{
	margin-left: -0.75rem !important;
    margin-top: -.15rem !important;
}

.badge-today{
	font-size: 65% !important;
}

.btn1{
	border-top-left-radius: 5px !important;
	border-bottom-left-radius: 5px !important;
}

.btn2{
	
    border-left: 1px solid black;
}

.btn3{
	border-left: 1px solid black;
}
.btn4{
	margin: 0px 10px;
	color:green !important;
}
.meeting thead tr td{
	font-family: 'Muli',sans-serif;
	font-size: 16px !important
}


.overall-card{
	box-shadow: 5px 10px 11px -3px rgb(0 0 0 / 20%);
    border: none;
}

.yellow{
	color: #ffc107;
}

.red{
	color:#dc3545!important;
}

.green{
	color:#28a745!important;
}

.blue{
	color:#007bff!important;
}

.legend-shadow{
	text-shadow:
    -1px -1px 0 #000,
    1px -1px 0 #000,
    -1px 1px 0 #000,
    1px 1px 0 #000;
}

.overall-card{
	background: #005C97;  /* fallback for old browsers */
	background: -webkit-linear-gradient(to left, #363795, #005C97);  /* Chrome 10-25, Safari 5.1-6 */
	background: linear-gradient(to left, #363795, #005C97); /* W3C, IE 10+/ Edge, Firefox 16+, Chrome 26+, Opera 12+, Safari 7+ */
	color:white;
}


.overall-card h3{
	line-height: inherit;
}

@media  screen and (max-width: 1565px){
	
	.health-title{
		    margin-bottom: 12px !important;
    		font-size: 20px !important;
	}
}

/* Project Details Graph Css */

.highcharts-figure,
.highcharts-data-table table {
    min-width: 310px;
    max-width: 800px;
    margin: 1em auto;
}

.highcharts-data-table table {
    font-family: Verdana, sans-serif;
    border-collapse: collapse;
    border: 1px solid #ebebeb;
    margin: 10px auto;
    text-align: center;
    width: 100%;
    max-width: 500px;
}

.highcharts-data-table caption {
    padding: 1em 0;
    font-size: 1.2em;
    color: #555;
}

.highcharts-data-table th {
    font-weight: 600;
    padding: 0.5em;
}

.highcharts-data-table td,
.highcharts-data-table th,
.highcharts-data-table caption {
    padding: 0.5em;
}

.highcharts-data-table thead tr,
.highcharts-data-table tr:nth-child(even) {
    background: #f8f8f8;
}

.highcharts-data-table tr:hover {
    background: #f1f7ff;
}

.fixed-table tbody .custom-td{
	padding-left : 1rem !important;
}

.fixed-table tbody .custom-td .col-md-11, .fixed-table tbody .custom-td .col-md-10 {
	padding-left : 25px !important;
}

.overall-card .card-body{
	padding: 0.8rem 1rem !important;
}

.tableFixHead          { overflow: auto;  }
.tableFixHead thead td { position: sticky; top: 0; z-index: 1; }
.tableFixHead thead td {background-color: #1363DF}

.progress {
	height : 1.3rem !important;
	cursor:pointer;
}


.flex-container-new {
    display: flex;
}

.flex-child {
    flex: 1;
}  

.health-circle {
    display: inline-block;
    height: 22px;
    width: 22px;
    line-height: 22px;
    border-radius: 50px;
    color: white;
    text-align: center;
    font-size: 10px;
    font-weight: 800;
}

.modal-hr{
	margin: 0px 10px -10px 10px !important;
}

.modal-list{
	font-size: 14px;
	text-align: left;
	padding: 0px !important;
	margin-bottom: 5px;
}

.modal-list li{
	display: inline-block;
}

.modal-list li .modal-span{
	font-size: 2rem;
	padding: 0px 7px;
}

.modal-list li .modal-text{
	font-size: 1rem;
	vertical-align: text-bottom;
	font-family: Lato;
}


@media screen and (max-width:1380px){
	.tableprojectname{
		font-size: 12px !important;
	}
	.tableprojectnametd{
		width:13% !important;
	}

}

.changes-btn:hover{
	background-color: #A6D1E6 !important;
}

.changes-btn{
	background-color: rgba(255, 255, 255, 0.08888) !important;
	border: 1px solid rgba(0,0,0,.125);
	padding: 0px 7px !important;
}

.changes-font{
	font-size: 0.7rem !important;
}

.changes-badge{
	top:-8px !important;
}

/* DG Dashboard */


.circular-progress .progress {
  width: 110px;
  height: 110px !important ;
  background: none;
  position: relative;
  margin-bottom: 10px
}

.circular-progress .progress::after {
  content: "";
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 15px solid #eee;
  position: absolute;
  top: 0;
  left: 0;
}

.circular-progress .progress>span {
  width: 50%;
  height: 100%;
  overflow: hidden;
  position: absolute;
  top: 0;
  z-index: 1;
}

.circular-progress .progress .progress-left {
  left: 0;
}

.circular-progress .progress .progress-bar {
  width: 100%;
  height: 100%;
  background: none;
  border-width: 15px;
  border-style: solid;
  position: absolute;
  top: 0;
}

.circular-progress .progress .progress-left .progress-bar {
  left: 100%;
  border-top-right-radius: 80px;
  border-bottom-right-radius: 80px;
  border-left: 0;
  -webkit-transform-origin: center left;
  transform-origin: center left;
}

.circular-progress .progress .progress-right {
  right: 0;
}

.circular-progress .progress .progress-right .progress-bar {
  left: -100%;
  border-top-left-radius: 80px;
  border-bottom-left-radius: 80px;
  border-right: 0;
  -webkit-transform-origin: center right;
  transform-origin: center right;
}

.circular-progress .progress .progress-value {
  position: absolute;
  top: 0;
  left: 0;
}


.detailscard{
	background-color: rgba(0,0,0,.08) !important;
	border: none !important;
}

.detailscard .card-body{
	padding:0.5rem !important;	
}
	
.detailscard hr{
	margin: 10px !important;
}

.detailscard h5{
	margin: 0px !important;
}
.countstable , .normalfont{
	font-family: 'Lato';
	font-weight: 700;
}

.card-deck-table tr td{
	padding: 0px 0px !important;
}

.border-orange{
	border-color: #EE5007 !important;
}

@media screen and (min-width:1100px) and (max-width:1500px){
	.circular-progress .progress{
		width:75px !important;
		height:75px !important;
	} 
	
	.circular-progress .progress-value .h4{
		font-size: 16px;
	}
	
	.circular-progress .progress .progress-bar {
		border-width:7px;
	}
	
	.circular-progress .progress::after {
		border-width: 7px;	
	}
	
	.bigcount h1{
		font-size: 2rem !important;
	}
	
	.normalfont{
		font-size: 12px !important;
	}
	
	.bigcount h3{
		font-size: 20px !important;
	}
	
	.bigcount h4{
		font-size: 1.2rem !important;
	}
	
	.detailscard h5{
		font-size: 16px !important;
	}
	
	.bigcount p{
		margin-bottom: 0rem !important;
	}
	
	.countstable tbody {
		font-size: 14px !important;
	}
	
	
}

	.bigcount h1{
		margin-bottom: -7px;
	}
		
	.bigcount h4,h3{
		margin-bottom: -4px;
	}
	
	.cashoutgotable, .financetable{
		margin-bottom: 0rem !important
	}
	
	.financetable tr td{
		font-size: 13px !important;
		font-weight: 600 !important;
	}
	
	.financetable tr th{
		font-size: 13px !important;
		padding: 0.5rem !important;
		text-align: left;
	}
	
	.cashoutgotable > tbody > tr >td, .financetable > tbody > tr >td{
		 border-top:1px solid grey !important;
	}
	
	.cashoutgotable > tbody > tr >th, .financetable > tbody > tr >th{
		 border-top:1px solid grey !important;
	}
	
	.cashoutgotable > thead > tr >th , .financetable > thead > tr >th{
		 border-top:1px solid transparent !important;
		 border-bottom:1px solid grey !important;
		 text-align: center;
	}

	.textfont{
		font-size: 12px !important;
	}
	
	.nil-bar{
		margin: 0px 10px !important;
	}
	
	.cashoutgotable tr {
		font-size: 13px !important;
		font-weight: 600 !important;
	}
	.cashoutgotable tr th{
		font-size: 13px !important;
		padding: 0.1rem !important;
		text-align: left;
	}
	
	.cashoutgotable thead{
		height: auto !important;
	}
	
	.bigcount p{
		margin-bottom: 0rem !important;
	}
	
	label{
		font-weight: bold;
	  	font-size: 13px;
	}

	.cashoutgo .primary{
			/* background-color: #5C192F !important; */
			background-color: #8D0404 !important; 
	}
		
	.cashoutgo .bg-success{
			/* background-color: #466136 !important; */
			background-color: #39850C !important; 
	}
		
	.cashoutgo .bg-info{
			/* background-color:#591A69 !important; */
			background-color: #7F109B !important;			 
	}
	

</style>

</head>

<body>
<%
IndianRupeeFormat nfc=new IndianRupeeFormat();
List<Object[]> ProjectHealthData = (List<Object[]>)request.getAttribute("projecthealthdata");
List<Object[]> ProjectList=(List<Object[]>)request.getAttribute("ProjectList");
Object[] ProjectHealthTotalData = (Object[])request.getAttribute("projecthealthtotal"); 
int ProjectCount = 0;
String logintype=(String)request.getAttribute("logintype"); 
List<Object[]> projecthealthtotaldg = (List<Object[]>)request.getAttribute("projecthealthtotaldg");
List<Object[]> DashboardFinance= (List<Object[]>)request.getAttribute("DashboardFinance");

FormatConverter fc=new FormatConverter(); 
SimpleDateFormat sdf=fc.getRegularDateFormat();
SimpleDateFormat sdf3=new SimpleDateFormat("dd-MM-yy");
SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
SimpleDateFormat sdf2=new SimpleDateFormat("yy-MM-dd");
%>
	
<div class="container-fluid">		
	<div class="row">
		<div class="col-md-12">
			<div class="row">
				<div class="col-md-12">
					<div class="">
						<h3>Lab  Details <a class="btn btn-info btn-sm back"   href="MainDashBoard.htm" style="float: right">Back</a></h3>
						
					</div>
				</div>
			</div>
			
					
					<%if(ProjectHealthData.size()>0){ %>
						
						
						
	<div class="container-fluid" id="overalltable">
	
		<div class="card-deck" style="margin-top: 0px;" id="overallmodulecarddeck" >
		  <div class="card detailscard">
		    <div class="card-body">
		      <h5 class="card-title"><img src="view/images/discuss.png" /> Meeting</h5>
		      <hr>
		      <div class="row">
		      	<div class="col-md-6 circular-progress">
		      		 <div class="progress " data-value='<%=ProjectHealthTotalData[29]!=null?StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[29].toString()): " - "%>'>
			         <span class="progress-left">
			          		<span class="progress-bar <%if(Integer.parseInt(ProjectHealthTotalData[29].toString())<=25){%> border-danger<%}%>
														<%if( (Integer.parseInt(ProjectHealthTotalData[29].toString())>25) && (Integer.parseInt(ProjectHealthTotalData[29].toString())<=50)){%> border-orange<%}%>
														<%if( (Integer.parseInt(ProjectHealthTotalData[29].toString())>50) && (Integer.parseInt(ProjectHealthTotalData[29].toString())<=75)){%> border-warning <%}%>
														<%if( (Integer.parseInt(ProjectHealthTotalData[29].toString())>75) && (Integer.parseInt(ProjectHealthTotalData[29].toString())<=100)){%> border-success <%}%>
														"></span>             
			         </span>
			         <span class="progress-right">
			                <span class="progress-bar <%if(Integer.parseInt(ProjectHealthTotalData[29].toString())<=25){%> border-danger<%}%>
														<%if( (Integer.parseInt(ProjectHealthTotalData[29].toString())>25) && (Integer.parseInt(ProjectHealthTotalData[29].toString())<=50)){%> border-orange<%}%>
														<%if( (Integer.parseInt(ProjectHealthTotalData[29].toString())>50) && (Integer.parseInt(ProjectHealthTotalData[29].toString())<=75)){%> border-warning <%}%>
														<%if( (Integer.parseInt(ProjectHealthTotalData[29].toString())>75) && (Integer.parseInt(ProjectHealthTotalData[29].toString())<=100)){%> border-success <%}%>
														"></span>   
			          </span>
			          <div class="progress-value w-100 h-100 rounded-circle d-flex align-items-center justify-content-center">
			            <div class="h4 font-weight-bold" id="pmrcprogress" ><%=(ProjectHealthTotalData[29]!=null?StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[29].toString()): " - " )%>%</div>
			          </div>
			        </div>
			        <!-- <div><h6 style="margin-bottom: 5px">PMRB</h6></div> -->
			        <hr style="margin: 5px !important">
			        <table class="countstable" style="margin: 0px auto">
			        	<tr>
			        		<th style="font-size: 14px">PMRB </th>
			        	</tr>
				        <tr>
			        		<td style="font-size: 14px;cursor: pointer;" id="meetingsvaluepmrc"  data-toggle="tooltip" title="Held / To be Held / Total to be Held" >
			        			<%if(ProjectHealthTotalData !=null){%>
			        				<span><%=ProjectHealthTotalData[0]!=null?StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[0].toString()): " - " %> / <%=ProjectHealthTotalData[2]!=null?StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[2].toString()): " - " %> / <%=ProjectHealthTotalData[46]!=null?StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[46].toString()): " - " %></span>
			        			<%}%>
			        		</td>
			        	</tr>
			        </table>
		      	</div>
		      	<div class="col-md-6 circular-progress">
		      		 <div class="progress "  data-value='<%=(ProjectHealthTotalData[31]!=null?StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[31].toString()): " - " )%>'>
			          <span class="progress-left">
			          		<span class="progress-bar <%if(Integer.parseInt(ProjectHealthTotalData[31].toString())<=25){%> border-danger<%}%>
																								   <%if( (Integer.parseInt(ProjectHealthTotalData[31].toString())>25) && (Integer.parseInt(ProjectHealthTotalData[31].toString())<=50)){%> border-orange<%}%>
																								   <%if( (Integer.parseInt(ProjectHealthTotalData[31].toString())>50) && (Integer.parseInt(ProjectHealthTotalData[31].toString())<=75)){%> border-warning <%}%>
																								   <%if( (Integer.parseInt(ProjectHealthTotalData[31].toString())>75) && (Integer.parseInt(ProjectHealthTotalData[31].toString())<=100)){%> border-success <%}%>
																"></span>             
			          </span>
			          <span class="progress-right">
			                <span class="progress-bar <%if(Integer.parseInt(ProjectHealthTotalData[31].toString())<=25){%> border-danger<%}%>
																								   <%if( (Integer.parseInt(ProjectHealthTotalData[31].toString())>25) && (Integer.parseInt(ProjectHealthTotalData[31].toString())<=50)){%> border-orange<%}%>
																								   <%if( (Integer.parseInt(ProjectHealthTotalData[31].toString())>50) && (Integer.parseInt(ProjectHealthTotalData[31].toString())<=75)){%> border-warning <%}%>
																								   <%if( (Integer.parseInt(ProjectHealthTotalData[31].toString())>75) && (Integer.parseInt(ProjectHealthTotalData[31].toString())<=100)){%> border-success <%}%>
																"></span>   
			          </span>
			          <div class="progress-value w-100 h-100 rounded-circle d-flex align-items-center justify-content-center">
			            <div class="h4 font-weight-bold" ><%=(ProjectHealthTotalData[31]!=null?StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[31].toString()): " - " )%>%</div>
			          </div>
			        </div>
			       <!--  <div><h6 style="margin-bottom: 5px">EB</h6></div> -->
			        <hr style="margin: 5px !important">
			        <table class="countstable" style="margin: 0px auto">
				        	<tr>
				        		<th style="font-size: 14px">EB </th>
				        	</tr>
				        	<tr>
				        		<td style="font-size: 14px" id="meetingsvalueeb"  data-toggle="tooltip" title="Held / To be Held / Total to be Held"  ><%if(ProjectHealthTotalData !=null){%><%=ProjectHealthTotalData[3]!=null?StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[3].toString()): " - " %> / <%=ProjectHealthTotalData[5]!=null?StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[5].toString()): " - " %> / <%=ProjectHealthTotalData[47] !=null?StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[47].toString()): " - "%><%}%></td>
				        	</tr>
				     </table>
		      	</div>
		      </div>
		    </div>
		    
		  </div>
		  <div class="card detailscard">
		    <div class="card-body">
		      <h5 class="card-title"><img src="view/images/goal.png" /> Milestone</h5>
		      <hr>
		      <div class="row">
		      	<div class="col-md-6 circular-progress">
		      		<div class="progress " data-value='<%if(ProjectHealthTotalData[10] !=null){%><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[10].toString()) %><%} %>'>
			          <span class="progress-left">
			                        <span class="progress-bar <%if(Integer.parseInt(ProjectHealthTotalData[10].toString())<=25){%> border-danger<%}%>
																<%if( (Integer.parseInt(ProjectHealthTotalData[10].toString())>25) && (Integer.parseInt(ProjectHealthTotalData[10].toString())<=50)){%> border-orange<%}%>
																<%if( (Integer.parseInt(ProjectHealthTotalData[10].toString())>50) && (Integer.parseInt(ProjectHealthTotalData[10].toString())<=75)){%> border-warning <%}%>
																<%if( (Integer.parseInt(ProjectHealthTotalData[10].toString())>75) && (Integer.parseInt(ProjectHealthTotalData[10].toString())<=100)){%> border-success <%}%>
																"></span>   
			          </span>
			          <span class="progress-right">
			                        <span class="progress-bar <%if(Integer.parseInt(ProjectHealthTotalData[10].toString())<=25){%> border-danger<%}%>
																<%if( (Integer.parseInt(ProjectHealthTotalData[10].toString())>25) && (Integer.parseInt(ProjectHealthTotalData[10].toString())<=50)){%> border-orange<%}%>
																<%if( (Integer.parseInt(ProjectHealthTotalData[10].toString())>50) && (Integer.parseInt(ProjectHealthTotalData[10].toString())<=75)){%> border-warning <%}%>
																<%if( (Integer.parseInt(ProjectHealthTotalData[10].toString())>75) && (Integer.parseInt(ProjectHealthTotalData[10].toString())<=100)){%> border-success <%}%>
																"></span>   
			          </span>
			          <div class="progress-value w-100 h-100 rounded-circle d-flex align-items-center justify-content-center">
			            <div class="h4 font-weight-bold"><span id="milestonepercentage"><%if(ProjectHealthTotalData[10] !=null){%><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[10].toString()) %><%} %>%</span></div>
			          </div>
			     	</div>
		      	</div>
		      	<div class="col-md-6">
		      		<div class="bigcount">
		      			<h1><%if(ProjectHealthTotalData[8] !=null){%><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[8].toString())%><%} %></h1>
		      			<p class="textfont" style="margin-bottom: 5px !important"><span class="green legend-shadow">&#x220E;</span> &nbsp;Completed</p>
		      		</div>
		      		
		      		<div class="bigcount">
		      			<h4><%if(ProjectHealthTotalData[9] !=null){%><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[9].toString())%><%} %></h4>
		      			<p class="textfont">Total</p>
		      		</div>
		      	</div>
		      </div>
		    </div>
		    <table class="countstable card-deck-table" style="margin-bottom: 0px !important;border-top:1px solid darkgrey;">
				  <thead style="height: 2rem">
				  	<tr>
				  		<td style="border-right:1px solid darkgrey;" data-toggle="tooltip" title="Delayed" >
				  			<span class="yellow">&#x220E;</span>
				  			<span style="font-size: 11px !important;text-shadow: none !important; ">Delayed&nbsp;</span>
				  			<%if(ProjectHealthTotalData[7] !=null){%><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[7].toString())%><%} %>
				  		</td>
				  		<td data-toggle="tooltip" title="Pending"  >
				  			<span class="red">&#x220E;</span>
				  			<span style="font-size: 11px !important;text-shadow: none !important; ">Pending&nbsp;</span>
				  			<%if(ProjectHealthTotalData[6] !=null){%><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[6].toString())%><%} %>
				  		</td>
				  	</tr>
				  </thead>
				 <!--  <tbody>
				  	<tr>
				  		<th scope="col" class="textfont" style="border-right:1px solid darkgrey;" ><span class="yellow">&#x220E;</span> &nbsp;Delayed</th>
				      	<th scope="col" class="textfont"><span class="red">&#x220E;</span> &nbsp;Pending</th>
				    </tr>
				  </tbody>  -->
			</table>
		  </div>
		  <div class="card detailscard">
		    <div class="card-body">
		      <h5 class="card-title"><img src="view/images/action1.png" /> Action</h5>
		      <hr>
		      <div class="row">
		      	<div class="col-md-6 circular-progress">
		      		<div class="progress " data-value='<%if(ProjectHealthTotalData[37] !=null){%><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[37].toString()) %><%} %>'>
			          <span class="progress-left">
			                        <span class="progress-bar <%if(Integer.parseInt(ProjectHealthTotalData[37].toString())<=25){%> border-danger<%}%>
																<%if( (Integer.parseInt(ProjectHealthTotalData[37].toString())>25) && (Integer.parseInt(ProjectHealthTotalData[37].toString())<=50)){%> border-orange<%}%>
																<%if( (Integer.parseInt(ProjectHealthTotalData[37].toString())>50) && (Integer.parseInt(ProjectHealthTotalData[37].toString())<=75)){%> border-warning <%}%>
																<%if( (Integer.parseInt(ProjectHealthTotalData[37].toString())>75) && (Integer.parseInt(ProjectHealthTotalData[37].toString())<=100)){%> border-success <%}%>
																"></span>
			          </span>
			          <span class="progress-right">
			                        <span class="progress-bar <%if(Integer.parseInt(ProjectHealthTotalData[37].toString())<=25){%> border-danger<%}%>
																<%if( (Integer.parseInt(ProjectHealthTotalData[37].toString())>25) && (Integer.parseInt(ProjectHealthTotalData[37].toString())<=50)){%> border-orange<%}%>
																<%if( (Integer.parseInt(ProjectHealthTotalData[37].toString())>50) && (Integer.parseInt(ProjectHealthTotalData[37].toString())<=75)){%> border-warning <%}%>
																<%if( (Integer.parseInt(ProjectHealthTotalData[37].toString())>75) && (Integer.parseInt(ProjectHealthTotalData[37].toString())<=100)){%> border-success <%}%>
																"></span>
			          </span>
			          <div class="progress-value w-100 h-100 rounded-circle d-flex align-items-center justify-content-center">
			            <div class="h4 font-weight-bold"><%if(ProjectHealthTotalData[37] !=null){%><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[37].toString()) %><%} %>%</div>
			          </div>
			     	</div>
		      	</div>
		      	<div class="col-md-6">
		      		<div class="bigcount">
		      			<h1><%if(ProjectHealthTotalData[14] !=null){%><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[14].toString())%><%} %></h1>
		      			<p class="textfont" style="margin-bottom: 5px !important"><span class="green legend-shadow">&#x220E;</span> &nbsp;Completed</p>
		      		</div>
		      		<div class="bigcount">
		      			<h4><%if(ProjectHealthTotalData[15] !=null){%><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[15].toString()) %><%} %></h4>
		      			<p class="textfont">Total</p>
		      		</div>
		      	</div>
		      </div>
		    </div>
		    <table class="countstable card-deck-table" style="margin-bottom: 0px !important;border-top:1px solid darkgrey;">
				  <thead style="height:2rem">
				  	<tr>
				  		<td style="border-right:1px solid darkgrey;" data-toggle="tooltip" title="Delayed" >
				  			<span class="yellow">&#x220E;</span>
				  			<span style="font-size: 11px !important;text-shadow: none !important; ">Delayed&nbsp;</span>
				  			<%if(ProjectHealthTotalData[13] !=null){%>&nbsp;<%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[13].toString()) %><%} %>
				  		</td>
				  		<td style="border-right:1px solid darkgrey;" data-toggle="tooltip" title="Forwarded" >
				  			<span class="blue">&#x220E;</span>
				  			<span style="font-size: 11px !important;text-shadow: none !important; ">Forward &nbsp;</span>
				  			<%if(ProjectHealthTotalData[12] !=null){%> &nbsp;<%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[12].toString()) %><%} %>
				  		</td>
				  		<td data-toggle="tooltip" title="Pending" >
				  			<span class="red">&#x220E;</span>
				  			<span style="font-size: 11px !important;text-shadow: none !important; ">Pending&nbsp;</span>
				  			<%if(ProjectHealthTotalData[11] !=null){%>&nbsp;<%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[11].toString()) %><%} %>
				  		</td>
				  	</tr>
				  </thead>
				  <!-- <tbody>
				  	<tr>
				      <th scope="col" class="textfont" style="border-right:1px solid darkgrey;"><span class="yellow">&#x220E;</span> &nbsp;Delayed</th>
				      <th scope="col" class="textfont" style="border-right:1px solid darkgrey;"><span class="blue">&#x220E;</span> &nbsp;Forwarded</th>
				      <th scope="col" class="textfont"><span class="red">&#x220E;</span> &nbsp;Pending</th>
				    </tr>
				  </tbody> -->
			</table>
		  </div>
		  <div class="card detailscard">
		    <div class="card-body">
		      <h5 class="card-title"><img src="view/images/risk 1.png" /> Risk</h5>
		      <hr>
		      <div class="row">
		      	<div class="col-md-6 circular-progress">
		      		<div class="progress " data-value='<%if(ProjectHealthTotalData[39] !=null){%><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[39].toString()) %><%} %>'>
			          <span class="progress-left">
			                         <span class="progress-bar <%if(Integer.parseInt(ProjectHealthTotalData[39].toString())<=25){%> border-danger<%}%>
																								   <%if( (Integer.parseInt(ProjectHealthTotalData[39].toString())>25) && (Integer.parseInt(ProjectHealthTotalData[39].toString())<=50)){%> border-orange<%}%>
																								   <%if( (Integer.parseInt(ProjectHealthTotalData[39].toString())>50) && (Integer.parseInt(ProjectHealthTotalData[39].toString())<=75)){%> border-warning <%}%>
																								   <%if( (Integer.parseInt(ProjectHealthTotalData[39].toString())>75) && (Integer.parseInt(ProjectHealthTotalData[39].toString())<=100)){%> border-success <%}%>
																"></span>  
			          </span>
			          <span class="progress-right">
			                         <span class="progress-bar <%if(Integer.parseInt(ProjectHealthTotalData[39].toString())<=25){%> border-danger<%}%>
																								   <%if( (Integer.parseInt(ProjectHealthTotalData[39].toString())>25) && (Integer.parseInt(ProjectHealthTotalData[39].toString())<=50)){%> border-orange<%}%>
																								   <%if( (Integer.parseInt(ProjectHealthTotalData[39].toString())>50) && (Integer.parseInt(ProjectHealthTotalData[39].toString())<=75)){%> border-warning <%}%>
																								   <%if( (Integer.parseInt(ProjectHealthTotalData[39].toString())>75) && (Integer.parseInt(ProjectHealthTotalData[39].toString())<=100)){%> border-success <%}%>
																"></span>  
			          </span>
			          <div class="progress-value w-100 h-100 rounded-circle d-flex align-items-center justify-content-center">
			            <div class="h4 font-weight-bold"><%if(ProjectHealthTotalData[39] !=null){%><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[39].toString()) %><%} %>%</div>
			          </div>
			     	</div>
		      	</div>
		      	<div class="col-md-6">
		      		<div class="bigcount">
		      			<h1><%if(ProjectHealthTotalData[16] !=null){%><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[16].toString()) %><%} %></h1>
		      			<p class="textfont" style="margin-bottom: 5px !important"><span class="green legend-shadow">&#x220E;</span> &nbsp;Completed</p>
		      		</div>
		      		<div class="bigcount">
		      			<h4><%if(ProjectHealthTotalData[18] !=null){%><%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[18].toString()) %><%} %></h4>
		      			<p class="textfont">Total</p>
		      		</div>
		      	</div>
		      </div>
		    </div>
		    <table class="countstable card-deck-table" style="margin-bottom: 0px !important;border-top:1px solid darkgrey;">
				  <thead style="height: 2rem">
				  	<tr>
				  		<td style="border-right:1px solid darkgrey;" data-toggle="tooltip" title="Delayed" >
				  		<span class="yellow">&#x220E;</span>
				  		<span style="font-size: 11px !important;text-shadow: none !important; ">Delayed&nbsp;</span>
				  		&nbsp;0
				  		</td>
				  		<td data-toggle="tooltip" title="Pending" >
				  			<span class="red">&#x220E;</span>
				  			<span style="font-size: 11px !important;text-shadow: none !important; ">Pending&nbsp;</span>
				  			<%if(ProjectHealthTotalData[17] !=null){%>&nbsp;<%=StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[17].toString()) %><%} %>
				  		</td>
				  	</tr>
				  </thead>
				 <!-- <tbody>
				  	<tr>
				      <th scope="col" class="textfont"style="border-right:1px solid darkgrey;"><span class="yellow">&#x220E;</span> &nbsp;Delayed</th>
				      <th scope="col" class="textfont"><span class="red">&#x220E;</span> &nbsp;Pending</th>
				    </tr>
				  </tbody>  -->
			</table>
		  </div>
		   <div class="card detailscard">
		    <div class="card-body">
		    	<div style="display: inline-flex">
			      <h5 class="card-title" style="margin-bottom: 8px !important">
							<img src="view/images/rupee.png" /> Finance <!-- <span style="font-size: 14px !important">(&#8377;Cr)</span> -->
				  </h5>
				 	<form action="ProjectHoaUpdate.htm" method="get">
						<button type="submit" class="btn btn4 btn-sm" style="padding: 0px 10px;" data-toggle="tooltip" title="Finance Refresh"><i class="fa fa-refresh" aria-hidden="true"></i></button></h6>  
				 	</form>
			   	</div>
		      <hr style="margin:3px 10px !important">
				<table class="table financetable " >
				  <thead>
				    <tr>
				      <th scope="col">
				     	(In &#8377; k Cr)
				      </th>
				      <th scope="col">Sanc</th>
				      <th scope="col">Exp</th>
				      <th scope="col">Bal</th>
				    </tr>
				  </thead>
				  <%
				  		BigDecimal sanction=new BigDecimal(0.00);
					  	BigDecimal expenditure=new BigDecimal(0.00);
					  	BigDecimal balance=new BigDecimal(0.00);
				  %>
				  <tbody>
				  	<tr>
				      <th scope="row">Revenue</th>
				      <td><span style="color:green">&#8377;</span> <%=DashboardFinance.get(0)[3]!=null?StringEscapeUtils.escapeHtml4(DashboardFinance.get(0)[3].toString()): " - " %></td>
				      <td><span style="color:green">&#8377;</span> <%=DashboardFinance.get(0)[4]!=null?StringEscapeUtils.escapeHtml4(DashboardFinance.get(0)[4].toString()): " - "  %></td>
				      <% BigDecimal revBal = new BigDecimal(DashboardFinance.get(0)[3].toString()).subtract(new BigDecimal(DashboardFinance.get(0)[4].toString())) ; %>
				      <td><span style="color:green">&#8377;</span> <%=revBal%></td>
				    </tr>
				     <% sanction	= sanction.add(new BigDecimal(DashboardFinance.get(0)[3].toString())); %>
				     <% expenditure	= expenditure.add(new BigDecimal(DashboardFinance.get(0)[4].toString())); %>
				     <% balance	= balance.add(revBal); %>
				     <tr>
				      <th scope="row">Capital</th>
				      <td><span style="color:green">&#8377;</span> <%=DashboardFinance.get(1)[3] !=null?StringEscapeUtils.escapeHtml4(DashboardFinance.get(1)[3].toString()): " - " %></td>
				      <td><span style="color:green">&#8377;</span> <%=DashboardFinance.get(1)[4]!=null?StringEscapeUtils.escapeHtml4(DashboardFinance.get(1)[3].toString()): " - "  %></td>
				      <% BigDecimal capBal = new BigDecimal(DashboardFinance.get(1)[3].toString()).subtract(new BigDecimal(DashboardFinance.get(1)[4].toString())) ; %>
				      <td><span style="color:green">&#8377;</span> <%=capBal %></td>
				    </tr>
				     <% sanction	= sanction.add(new BigDecimal(DashboardFinance.get(1)[3].toString())); %>
				     <% expenditure	= expenditure.add(new BigDecimal(DashboardFinance.get(1)[4].toString())); %>
				     <% balance	= balance.add(capBal); %>
				    
				    <tr>
				      <th scope="row">Others</th>
				      <td><span style="color:green">&#8377;</span> <%=DashboardFinance.get(2)[3]!=null?StringEscapeUtils.escapeHtml4(DashboardFinance.get(2)[3].toString()): " - "  %></td>
				      <td><span style="color:green">&#8377;</span> <%=DashboardFinance.get(2)[4]!=null?StringEscapeUtils.escapeHtml4(DashboardFinance.get(2)[3].toString()): " - "  %></td>
				      <% BigDecimal othBal = new BigDecimal(DashboardFinance.get(2)[3].toString()).subtract(new BigDecimal(DashboardFinance.get(2)[4].toString())) ; %>
				      <td><span style="color:green">&#8377;</span> <%=othBal %></td>
				    </tr> 
				     <% sanction	= sanction.add(new BigDecimal(DashboardFinance.get(2)[3].toString())); %>
				     <% expenditure	= expenditure.add(new BigDecimal(DashboardFinance.get(2)[4].toString())); %>
				     <% balance	= balance.add(othBal); %>
				    <tr>
				      <th scope="row">Total</th>
				      <td><span style="color:green">&#8377;</span> <%=sanction%></td>
				      <td><span style="color:green">&#8377;</span> <%=expenditure %></td>
				      <td><span style="color:green">&#8377;</span> <%=balance %></td>
				    </tr>
				  </tbody>
				</table>
	
			   </div>
		  	</div>	 
		</div>

	</div>

						
						
						
						
			            		<%-- 			            		
								<div style="background-color: rgba(255, 255, 255, 0.39999) !important ;border-radius: 4px ;  ">
								
									<table class="table meeting tableFixHead fixed-table "  style="table-layout: fixed;overflow-y:auto;overflow-x:hidden "  id="overalldiv"> 
										<thead style=" background: #1363DF; color: white;">												
											<tr>
												<td style="width:4%">
													<a  data-toggle="modal"  class="fa faa-pulse animated " data-target="#exampleModal1" data-whatever="@mdo" style="padding: 0px 1.5rem;cursor:pointer" ><i class="fa fa-info-circle " style="font-size: 1.3rem;color: " aria-hidden="true"></i> </a>
												</td>
												<td style="width:15%"><span style="font-size :15px;font-weight: bold; ">Project</span></td>
												<td style="padding: 0px !important"><span style="font-size :15px;font-weight: bold;">PMRB </span></td>
												<td style="padding: 0px !important"><span style="font-size :15px;font-weight: bold;">EB </span></td>
												<td ><span style="font-size :15px;font-weight: bold;  ">Milestone </span></td>
												<td ><span style="font-size :15px;font-weight: bold;  ">Action</span></td>
												<td ><span style="font-size :15px;font-weight: bold;  ">Risk</span></td>
												<td ><span style="font-size :15px;font-weight: bold;  ">Finance</span></td>
											</tr>
										</thead>
										
										<tbody>
										
											<%for(Object[] obj : ProjectHealthData){
			
													 
												%>
										
											<tr>
												<td><a href="javascript:ProjectDetails('<%=obj[2]%>')"> <i class="fa fa-hand-o-right" aria-hidden="true" style="color: purple;font-size: 1.3rem !important"></i></a></td>
												<td   style="font-weight: 800; font-size:1rem;text-align:left;
												
														<% if(obj[45]!=null) {if(obj[45].toString().equalsIgnoreCase("IA")){%>color: green<%} 
														else if(obj[45].toString().equalsIgnoreCase("IN")){%>color: #007bff <%} 
														else if(obj[45].toString().equalsIgnoreCase("IAF")){%>color: #1F4690 <%} 
														else if(obj[45].toString().equalsIgnoreCase("IH")){%>color: #8E3200 <%} 
														else if(obj[45].toString().equalsIgnoreCase("OH")){%>color: #EE5007 <%} 
														else if(obj[45].toString().equalsIgnoreCase("DRDO")){%>color: #231955 <%} 
														else {%>color:black <%} }else{ %>color:black <%}%>">
												
												<div data-toggle="tooltip" 
													title="<%if(obj[45]!=null) { if(obj[45].toString().equalsIgnoreCase("IA")){%>Indian Army<%}
													else if(obj[45].toString().equalsIgnoreCase("IN")){%>Indian Navy <%} 
													else if(obj[45].toString().equalsIgnoreCase("IAF")){%>Indian Air Force<%} 
													else if(obj[45].toString().equalsIgnoreCase("IH")){%>Home Land Security <%} 
													else if(obj[45].toString().equalsIgnoreCase("DRDO")){%>DRDO <%} 
													else if(obj[45].toString().equalsIgnoreCase("OH")){%>Others <%} 
													else {%> -  <%} }else{ %>-<%}%>">
												
														  &#11044;&nbsp; <span class="tableprojectname" style="color:black !important;font-size: 13px"> 
														  	<%if(obj[46]!=null){%><%=obj[46] %><%}else {%>-<%} %> /
														  	<%if(obj[3]!=null){%><%=obj[3] %><%}else {%>-<%} %> /
														  	<%if(obj[44]!=null){%><%=obj[44] %><%}else {%>-<%} %>
														  	</span> 	
												
													</div>
												
												</td>
												<td class="custom-td">
													<%if(Integer.parseInt(obj[8].toString())>0){ %>
														
														<div class="row">
															<div class="col-md-10">
															    <div class="progress"  >
																  <div class="progress-bar progress-bar-striped bg-success" onclick="overallmeetingredirect('<%=obj[2]%>','1', 'B')" style="width:<%=obj[5]%>%;" data-toggle="tooltip" title="PMRB Held : <%=obj[4]%> / <%=obj[8] %>" ></div>
																  <div class="progress-bar progress-bar-striped bg-primary" onclick="overallmeetingredirect('<%=obj[2]%>','1', 'C' )" style="width:<%=obj[7]%>%;" data-toggle="tooltip" title="PMRB Pending : <%=obj[6]%> / <%=obj[8] %>" ></div>
																</div>
														  	</div>
															<div class="col-md-2" style="padding-left: 0px !important">
																<span class="health-circle" style="<%if(Integer.parseInt(obj[7].toString())<=25){%> background-color:green <%}%>
																								   <%if( (Integer.parseInt(obj[7].toString())>25) && (Integer.parseInt(obj[7].toString())<=50)){%> background-color: #F8CB2E;color:black <%}%>
																								   <%if( (Integer.parseInt(obj[7].toString())>50) && (Integer.parseInt(obj[7].toString())<=75)){%> background-color: #EE5007 <%}%>
																								   <%if( (Integer.parseInt(obj[7].toString())>75) && (Integer.parseInt(obj[7].toString())<=100)){%> background-color:red <%}%>
																"><%=obj[7] %></span>
															</div>
														</div>
						
													<%}else{ %>
													<div class="progress" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
														<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
															Nil
														</div>
													</div>
													<%} %>
												</td>
												<td class="custom-td">
													<%if(Integer.parseInt(obj[13].toString())>0){ %>
														<div class="row">
															<div class="col-md-10">
																<div class="progress" >
																  <div class="progress-bar progress-bar-striped bg-success" onclick="overallmeetingredirect('<%=obj[2]%>','2', 'B')" style="width:<%=obj[10]%>%;" data-toggle="tooltip" title="EB Held : <%=obj[9]%> / <%=obj[13] %>" ></div>
																  <div class="progress-bar progress-bar-striped bg-primary" onclick="overallmeetingredirect('<%=obj[2]%>','2', 'C')" style="width:<%=obj[12]%>%;" data-toggle="tooltip" title="EB Pending :  <%=obj[11]%> / <%=obj[13] %>" ></div>
																</div>
															</div>
															<div class="col-md-2" style="padding-left: 0px !important">
																<span class="health-circle" style="<%if(Integer.parseInt(obj[12].toString())<=25){%> background-color:green <%}%>
																								   <%if( (Integer.parseInt(obj[12].toString())>25) && (Integer.parseInt(obj[12].toString())<=50)){%> background-color: #F8CB2E;color:black <%}%>
																								   <%if( (Integer.parseInt(obj[12].toString())>50) && (Integer.parseInt(obj[12].toString())<=75)){%> background-color: #EE5007 <%}%>
																								   <%if( (Integer.parseInt(obj[12].toString())>75) && (Integer.parseInt(obj[12].toString())<=100)){%> background-color:red <%}%>
																"><%=obj[12] %></span>
															</div>
														</div>
													<%}else{ %>
													<div class="progress" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
														<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
															Nil
														</div>
													</div>
													<%} %>
												</td>
												<td class="custom-td">
													<%if(Integer.parseInt(obj[20].toString())>0){ %>
													<div class="row">
														<div class="col-md-10">
															<div class="progress" onclick="overallcommonredirect('mil','<%=obj[2]%>')">
															  <div class="progress-bar progress-bar-striped bg-success" style="width:<%=obj[19]%>%" data-toggle="tooltip" title="Completed : <%=obj[18]%> / <%=obj[20] %>" ></div>
															  <div class="progress-bar progress-bar-striped bg-warning" style="width:<%=obj[17]%>%" data-toggle="tooltip" title="Delayed : <%=obj[16]%> / <%=obj[20] %>" ></div>
															  <div class="progress-bar progress-bar-striped bg-danger" style="width:<%=obj[15]%>%" data-toggle="tooltip" title="Pending : <%=obj[14]%> / <%=obj[20] %>" ></div>															  
															</div>
														</div>
														<div class="col-md-2" style="padding-left: 0px !important">
																<span class="health-circle" style="<%if(Integer.parseInt(obj[15].toString())<=25){%> background-color:green <%}%>
																								   <%if( (Integer.parseInt(obj[15].toString())>25) && (Integer.parseInt(obj[15].toString())<=50)){%> background-color: #F8CB2E;color:black <%}%>
																								   <%if( (Integer.parseInt(obj[15].toString())>50) && (Integer.parseInt(obj[15].toString())<=75)){%> background-color: #EE5007 <%}%>
																								   <%if( (Integer.parseInt(obj[15].toString())>75) && (Integer.parseInt(obj[15].toString())<=100)){%> background-color:red <%}%>
																"><%=obj[15] %></span>
														</div>
													</div>
													<%}else{ %>
													<div class="progress" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
														<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
															Nil
														</div>
													</div>
													<%} %>
												</td>
												<td class="custom-td">
													<%if(Integer.parseInt(obj[29].toString())>0){ %>
													<div class="row">
														<div class="col-md-10">
															<div class="progress" onclick="overallcommonredirect('action','<%=obj[2]%>')">
															  <div class="progress-bar progress-bar-striped bg-success" style="width:<%=obj[28]%>%" data-toggle="tooltip" title="Completed : <%=obj[27]%> / <%=obj[29]%>" ></div>
															  <div class="progress-bar progress-bar-striped bg-warning" style="width:<%=obj[26]%>%" data-toggle="tooltip" title="Delayed : <%=obj[25]%> / <%=obj[29]%>" ></div>
															  <div class="progress-bar progress-bar-striped bg-primary" style="width:<%=obj[24]%>%" data-toggle="tooltip" title="Forwarded : <%=obj[23]%> / <%=obj[29]%>" ></div>
															  <div class="progress-bar progress-bar-striped bg-danger" style="width:<%=obj[22]%>%" data-toggle="tooltip" title="Pending : <%=obj[21]%> / <%=obj[29]%>" ></div>
															</div>
														</div>
														<div class="col-md-2" style="padding-left: 0px !important">
															<span class="health-circle" style="<%if(Integer.parseInt(obj[22].toString())<=25){%> background-color:green <%}%>
																									   <%if( (Integer.parseInt(obj[22].toString())>25) && (Integer.parseInt(obj[22].toString())<=50)){%> background-color: #F8CB2E;color:black <%}%>
																									   <%if( (Integer.parseInt(obj[22].toString())>50) && (Integer.parseInt(obj[22].toString())<=75)){%> background-color: #EE5007 <%}%>
																									   <%if( (Integer.parseInt(obj[22].toString())>75) && (Integer.parseInt(obj[22].toString())<=100)){%> background-color:red <%}%>
															"><%=obj[22] %></span>
														</div>
													</div>
													<%}else{ %>
													<div class="progress" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
														<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
															Nil
														</div>
													</div>
													<%} %>
												</td>
												<td class="custom-td">
													<%if(Integer.parseInt(obj[34].toString())>0){ %>
													<div class="row">
														<div class="col-md-10">
															<div class="progress" onclick="overallcommonredirect('risk','<%=obj[2]%>')">
															  <div class="progress-bar progress-bar-striped bg-success" style="width:<%=obj[31]%>%" data-toggle="tooltip" title="Completed : <%=obj[30]%> / <%=obj[34] %>" ></div>
															  <div class="progress-bar progress-bar-striped bg-danger" style="width:<%=obj[33]%>%" data-toggle="tooltip" title="Pending : <%=obj[32]%> / <%=obj[34] %>" ></div>
															</div>
														</div>
														<div class="col-md-2" style="padding-left: 0px !important">
																<span class="health-circle" style="<%if(Integer.parseInt(obj[33].toString())<=25){%> background-color:green <%}%>
																										   <%if( (Integer.parseInt(obj[33].toString())>25) && (Integer.parseInt(obj[33].toString())<=50)){%> background-color: #F8CB2E;color:black <%}%>
																										   <%if( (Integer.parseInt(obj[33].toString())>50) && (Integer.parseInt(obj[33].toString())<=75)){%> background-color: #EE5007 <%}%>
																										   <%if( (Integer.parseInt(obj[33].toString())>75) && (Integer.parseInt(obj[33].toString())<=100)){%> background-color:red <%}%>
																"><%=obj[33] %></span>
														</div>
													</div>
													<%}else{ %>
													<div class="progress" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
														<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
															Nil
														</div>
													</div>
													<%} %>
												</td>
												<td class="custom-td">
													<%
													BigInteger number = new BigInteger(obj[43].toString());
													if(number.compareTo(new BigInteger("0")) >0){ %>
													<div class="row">
														<div class="col-md-10">
															<div class="progress" onclick="overallfinance()">
															  <div class="progress-bar progress-bar-striped bg-success" style="width:<%=obj[36]%>%" data-toggle="tooltip" title="Expenditure : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[35].toString() ))))%>" ></div>
															  <div class="progress-bar progress-bar-striped bg-warning" style="width:<%=obj[38]%>%" data-toggle="tooltip" title="OC : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[37].toString() ))))%> " ></div>
															  <div class="progress-bar progress-bar-striped bg-primary" style="width:<%=obj[40]%>%" data-toggle="tooltip" title="DIPL : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[39].toString() ))))%> " ></div>
															  <div class="progress-bar progress-bar-striped bg-danger" style="width:<%=obj[42]%>%" data-toggle="tooltip" title="Balance : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[41].toString() ))))%> " ></div>
															</div>
														</div>
														<div class="col-md-2" style="padding-left: 0px !important">
																	<span class="health-circle" style="<%if(Integer.parseInt(obj[36].toString())<=25){%> background-color:green <%}%>
																											   <%if( (Integer.parseInt(obj[36].toString())>25) && (Integer.parseInt(obj[36].toString())<=50)){%> background-color: #F8CB2E;color:black <%}%>
																											   <%if( (Integer.parseInt(obj[36].toString())>50) && (Integer.parseInt(obj[36].toString())<=75)){%> background-color: #EE5007 <%}%>
																											   <%if( (Integer.parseInt(obj[36].toString())>75) && (Integer.parseInt(obj[36].toString())<=100)){%> background-color:red <%}%>
																	"><%=obj[36] %></span>
														</div>
														<div class="col-md-1" style="padding-left: 0px !important">
														</div>
													</div>
													
													<%}else{ %>
													<div class="progress" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
														<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
															Nil
														</div>
													</div>
													<%} %>
												</td>
											</tr> 
											
											<%  ProjectCount++;  } %>
											
										</tbody>
											
											
									</table>	
									
									<!-- ******************************************************** INDIVIDUAL PROJECT DETAILS ******************************************* -->
	
									<div class="card" style="background: white;display:none;" id="projectgraph">
										<div style="background-color: rgba(255, 255, 255, 0.39999) !important ;border-radius: 4px ;overflow-x:hidden ">
											<div  style="float: right;margin: 10px">
												<button class="btn btn-sm back" onclick="overalldetails('A')">Details</button>
												<br>
												<button class="btn btn-sm back" style="margin-top: 10px;padding: 3px 19px;" onclick="overalldoc()"><i class="fa fa-file-text-o" aria-hidden="true"></i> DOC </button>
											</div>
											
											<div class="row">
												<div class="col-md-4">
								 					<figure class="highcharts-figure">
														<div id="containerh"></div>
													</figure>
												</div>
												<div class="col-md-4">
								 					<figure class="highcharts-figure">
														<div id="containerh2"></div>
													</figure>
												</div>
												<div class="col-md-4">
								 					<figure class="highcharts-figure">
														<div id="containerh3"></div>
													</figure>
												</div>
												<!-- <div class="col-md-1">
													<button class="btn btn-sm back" style="float: right;margin: 10px" onclick="overalldetails('A')">Details</button>
													<button class="btn btn-sm back" style="float: right;margin: 10px" onclick="overalldetails('A')">DOC</button>
												</div> -->
											</div>
											<hr>
											<div class="row">
												<div class="col-md-4">
								 					<figure class="highcharts-figure">
														<div id="containerh4"></div>
													</figure>
												</div>
												<div class="col-md-4">
								 					<figure class="highcharts-figure">
														<div id="containerh5"></div>
													</figure>
												</div>
												<div class="col-md-4">
								 					<figure class="highcharts-figure">
														<div id="containerh6"></div>
													</figure>
												</div>
											</div>
											
											
										</div>
									</div>		
								
								<!-- ******************************************************** INDIVIDUAL PROJECT DETAILS END ******************************************* -->
			
				
								</div> 
								 --%>
								 
								<div class="card" style="background: transparent;margin-top:5px" > 
									<div style="background-color: rgba(255, 255, 255, 0.39999) !important ;border-radius: 4px ; ">
									
										<table class="table meeting tableFixHead fixed-table "  style="overflow-y:auto;overflow-x:hidden "  id="overalldiv"> 
											<thead style=" background: #1363DF; color: white;">												
												<tr>
													<td style="width:4%">
														<a  data-toggle="modal"  class="fa faa-pulse animated " data-target="#exampleModal1" data-whatever="@mdo" style="padding: 0px 1.5rem;cursor:pointer" ><i class="fa fa-info-circle " style="font-size: 1.3rem;color: " aria-hidden="true"></i> </a>
													</td>
													<td class="tableprojectnametd" style="width:12%"><span style="font-size :15px;font-weight: bold; ">Project</span></td>
													<td style="width:2%"><div data-toggle="tooltip" title="View All"><a style="cursor: pointer;" href="GetAllProjectSlide.htm" target="_blank"  ><img src="view/images/silde.png" style="width: 25px;"/></a></div></td>
													<td style="width:6%"><span style="font-size :15px;font-weight: bold; ">DoS</span></td>
													<td style="width:6%"><span style="font-size :15px;font-weight: bold; ">PDC</span></td>
													<td style="padding: 0px !important"><span style="font-size :15px;font-weight: bold;">PMRB </span></td>
													<td style="padding: 0px !important"><span style="font-size :15px;font-weight: bold;">EB </span></td>
													<td ><span style="font-size :15px;font-weight: bold;  ">Milestone </span></td>
													<td ><span style="font-size :15px;font-weight: bold;  ">Action</span></td>
													<td ><span style="font-size :15px;font-weight: bold;  ">Risk</span></td>
													<td ><span style="font-size :15px;font-weight: bold;  ">Finance</span></td>
												</tr>
											</thead>
											
											<tbody>
											
												<%for(Object[] obj : ProjectHealthData){
													
													if(ProjectList!=null){  for(Object[] obj2 : ProjectList) 
													{
														if(obj[2].equals(obj2[0]))
														{
												%>
											
												<tr>
													<td><a href="javascript:ProjectDetails('<%=obj[2]%>','<%=obj[46]%>')"> <i class="fa fa-hand-o-right" aria-hidden="true" style="color: purple;font-size: 1.3rem !important"></i></a></td>
													<td style="font-weight: 800; font-size:1rem;text-align:left;
													
															<% if(obj[45]!=null) {if(obj[45].toString().equalsIgnoreCase("IA")){%>color: green<%} 
															else if(obj[45].toString().equalsIgnoreCase("IN")){%>color: #007bff <%} 
															else if(obj[45].toString().equalsIgnoreCase("IAF")){%>color: #1F4690 <%} 
															else if(obj[45].toString().equalsIgnoreCase("IH")){%>color: #8E3200 <%} 
															else if(obj[45].toString().equalsIgnoreCase("OH")){%>color: #EE5007 <%} 
															else if(obj[45].toString().equalsIgnoreCase("DRDO")){%>color: #231955 <%} 
															else {%>color:black <%} }else{ %>color:black <%}%>">
													
													<div data-toggle="tooltip" 
														title="<%if(obj[45]!=null) { if(obj[45].toString().equalsIgnoreCase("IA")){%>Indian Army<%}
														else if(obj[45].toString().equalsIgnoreCase("IN")){%>Indian Navy <%} 
														else if(obj[45].toString().equalsIgnoreCase("IAF")){%>Indian Air Force<%} 
														else if(obj[45].toString().equalsIgnoreCase("IH")){%>Home Land Security <%} 
														else if(obj[45].toString().equalsIgnoreCase("DRDO")){%>DRDO <%} 
														else if(obj[45].toString().equalsIgnoreCase("OH")){%>Others <%} 
														else {%> -  <%} }else{ %>-<%}%>">
													
															  &#11044;&nbsp; <span class="tableprojectname" style="color:black !important;font-size: 13px"> 
															  	<%if(obj[46]!=null){%><%=StringEscapeUtils.escapeHtml4(obj[46].toString()) %><%}else {%>-<%} %> /
															  	<%if(obj[3]!=null){%><%=StringEscapeUtils.escapeHtml4(obj[3].toString()) %><%}else {%>-<%} %> /
															  	<%if(obj[44]!=null){%><%=StringEscapeUtils.escapeHtml4(obj[44].toString()) %><%}else {%>-<%} %>
															  	</span> 	
													
														</div>
													
													</td>
													<td><a style="cursor: pointer;" href="PfmsProjectSlides.htm?projectid=<%=obj[2]%>" target="_blank" > <img src="view/images/silde.png" style="width: 25px;"/></a></td>
													<td style="font-size: 13px;font-weight: 600; "><%if(obj[50]!=null){%><%= sdf3.format(sdf2.parse(obj[50].toString()))%><%}else{ %> - <%} %></td>
													<td style="font-size: 13px;font-weight: 600; <%if(obj[47]!=null){ if(LocalDate.parse(obj[47].toString()).isBefore(LocalDate.now())){ %>color:maroon<%}}%>  "><%if(obj[47]!=null){%><%= sdf3.format(sdf2.parse(obj[47].toString()))%><%}else{ %> - <%} %></td>
													<td class="custom-td">
														<%if(Integer.parseInt(obj[8].toString())>0){ %>
															
															<div class="row">
																<div class="col-md-11">
																    <div class="progress" data-toggle="tooltip" title="PMRB Held : <%=obj[4]%> <br> PMRB To Be Held : <%=obj[8] %><br> Total PMRB To Be Held : <%=obj[48] %>"  >
																	  <div class="progress-bar progress-bar-striped bg-success" onclick="overallmeetingredirect('<%=obj[2]%>','1', 'B')" style="width:<%=obj[5]%>%;" ></div>
																	  <div class="progress-bar progress-bar-striped bg-primary" onclick="overallmeetingredirect('<%=obj[2]%>','1', 'C' )" style="width:<%=obj[7]%>%;" ></div>
																	</div>
															  	</div>
																<div class="col-md-1" style="padding-left: 0px !important">
																	<span class="health-circle" style="<%if(Integer.parseInt(obj[5].toString())<=25){%> background-color:red <%}%>
																									   <%if( (Integer.parseInt(obj[5].toString())>25) && (Integer.parseInt(obj[5].toString())<=50)){%> background-color: #EE5007; <%}%>
																									   <%if( (Integer.parseInt(obj[5].toString())>50) && (Integer.parseInt(obj[5].toString())<=75)){%> background-color:  #F8CB2E;color:black;  <%}%>
																									   <%if( (Integer.parseInt(obj[5].toString())>75) && (Integer.parseInt(obj[5].toString())<=100)){%> background-color:green <%}%>
																	"><%=obj[5]!=null?StringEscapeUtils.escapeHtml4(obj[5].toString()): " - " %></span>
																</div>
															</div>
							
														<%}else{ %>
														<div class="progress nil-bar" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
															<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
																Nil
															</div>
														</div>
														<%} %>
													</td>
													<td class="custom-td">
														<%if(Integer.parseInt(obj[13].toString())>0){ %>
															<div class="row">
																<div class="col-md-11">
																	<div class="progress" data-toggle="tooltip" title="EB Held : <%=obj[9]%> <br> EB To Be Held : <%=obj[13] %><br> Total EB To Be Held : <%=obj[49] %>">
																	  <div class="progress-bar progress-bar-striped bg-success" onclick="overallmeetingredirect('<%=obj[2]%>','2', 'B')" style="width:<%=obj[10]%>%;"></div>
																	  <div class="progress-bar progress-bar-striped bg-primary" onclick="overallmeetingredirect('<%=obj[2]%>','2', 'C')" style="width:<%=obj[12]%>%;"></div>
																	</div>
																</div>
																<div class="col-md-1" style="padding-left: 0px !important">
																	<span class="health-circle" style="<%if(Integer.parseInt(obj[10].toString())<=25){%> background-color:red <%}%>
																									   <%if( (Integer.parseInt(obj[10].toString())>25) && (Integer.parseInt(obj[10].toString())<=50)){%> background-color:#EE5007; <%}%>
																									   <%if( (Integer.parseInt(obj[10].toString())>50) && (Integer.parseInt(obj[10].toString())<=75)){%> background-color:#F8CB2E;color:black; <%}%>
																									   <%if( (Integer.parseInt(obj[10].toString())>75) && (Integer.parseInt(obj[10].toString())<=100)){%> background-color:green<%}%>
																	"><%=obj[10]!=null?StringEscapeUtils.escapeHtml4(obj[10].toString()): " - " %></span>
																</div>
															</div>
														<%}else{ %>
														<div class="progress nil-bar" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
															<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
																Nil
															</div>
														</div>
														<%} %>
													</td>
													<td class="custom-td">
														<%if(Integer.parseInt(obj[20].toString())>0){ %>
														<div class="row">
															<div class="col-md-11">
																<div class="progress" onclick="overallcommonredirect('mil','<%=obj[2]%>')">
																  <div class="progress-bar progress-bar-striped bg-success" style="width:<%=obj[19]%>%" data-toggle="tooltip" title="Completed : <%=obj[18]%> / <%=obj[20] %>" ></div>
																  <div class="progress-bar progress-bar-striped bg-warning" style="width:<%=obj[17]%>%" data-toggle="tooltip" title="Delayed : <%=obj[16]%> / <%=obj[20] %>" ></div>
																  <div class="progress-bar progress-bar-striped bg-danger" style="width:<%=obj[15]%>%" data-toggle="tooltip" title="Pending : <%=obj[14]%> / <%=obj[20] %>" ></div>															  
																</div>
															</div>
															<div class="col-md-1" style="padding-left: 0px !important">
																	<span class="health-circle" style="<%if(Integer.parseInt(obj[19].toString())<=25){%> background-color:red <%}%>
																									   <%if( (Integer.parseInt(obj[19].toString())>25) && (Integer.parseInt(obj[19].toString())<=50)){%> background-color: #EE5007;<%}%>
																									   <%if( (Integer.parseInt(obj[19].toString())>50) && (Integer.parseInt(obj[19].toString())<=75)){%> background-color:#F8CB2E;color:black;<%}%>
																									   <%if( (Integer.parseInt(obj[19].toString())>75) && (Integer.parseInt(obj[19].toString())<=100)){%> background-color:green <%}%>
																	"><%=obj[19]!=null?StringEscapeUtils.escapeHtml4(obj[19].toString()): " - " %></span>
															</div>
														</div>
														<%}else{ %>
														<div class="progress nil-bar" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
															<div class="progress-bar " role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
																Nil
															</div>
														</div>
														<%} %>
													</td>
													<td class="custom-td">
														<%if(Integer.parseInt(obj[29].toString())>0){ %>
														<div class="row">
															<div class="col-md-11">
																<div class="progress" onclick="overallcommonredirect('action','<%=obj[2]%>')">
																  <div class="progress-bar progress-bar-striped bg-success" style="width:<%=obj[28]%>%" data-toggle="tooltip" title="Completed : <%=obj[27]%> / <%=obj[29]%>" ></div>
																  <div class="progress-bar progress-bar-striped bg-warning" style="width:<%=obj[26]%>%" data-toggle="tooltip" title="Delayed : <%=obj[25]%> / <%=obj[29]%>" ></div>
																  <div class="progress-bar progress-bar-striped bg-primary" style="width:<%=obj[24]%>%" data-toggle="tooltip" title="Forwarded : <%=obj[23]%> / <%=obj[29]%>" ></div>
																  <div class="progress-bar progress-bar-striped bg-danger" style="width:<%=obj[22]%>%" data-toggle="tooltip" title="Pending : <%=obj[21]%> / <%=obj[29]%>" ></div>
																</div>
															</div>
															<div class="col-md-1" style="padding-left: 0px !important">
																<span class="health-circle" style="<%if(Integer.parseInt(obj[28].toString())<=25){%> background-color:red<%}%>
																										   <%if( (Integer.parseInt(obj[28].toString())>25) && (Integer.parseInt(obj[28].toString())<=50)){%> background-color: #EE5007; <%}%>
																										   <%if( (Integer.parseInt(obj[28].toString())>50) && (Integer.parseInt(obj[28].toString())<=75)){%> background-color: #F8CB2E;color:black <%}%>
																										   <%if( (Integer.parseInt(obj[28].toString())>75) && (Integer.parseInt(obj[28].toString())<=100)){%> background-color:green <%}%>
																"><%=obj[28]!=null?StringEscapeUtils.escapeHtml4(obj[28].toString()): " - " %></span>
															</div>
														</div>
														<%}else{ %>
														<div class="progress nil-bar" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
															<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
																Nil
															</div>
														</div>
														<%} %>
													</td>
													<td class="custom-td">
														<%if(Integer.parseInt(obj[34].toString())>0){ %>
														<div class="row">
															<div class="col-md-11">
																<div class="progress" onclick="overallcommonredirect('risk','<%=obj[2]%>')">
																  <div class="progress-bar progress-bar-striped bg-success" style="width:<%=obj[31]%>%" data-toggle="tooltip" title="Completed : <%=obj[30]%> / <%=obj[34] %>" ></div>
																  <div class="progress-bar progress-bar-striped bg-danger" style="width:<%=obj[33]%>%" data-toggle="tooltip" title="Pending : <%=obj[32]%> / <%=obj[34] %>" ></div>
																</div>
															</div>
															<div class="col-md-1" style="padding-left: 0px !important">
																	<span class="health-circle" style="<%if(Integer.parseInt(obj[31].toString())<=25){%> background-color:red <%}%>
																											   <%if( (Integer.parseInt(obj[31].toString())>25) && (Integer.parseInt(obj[31].toString())<=50)){%> background-color: #EE5007; <%}%>
																											   <%if( (Integer.parseInt(obj[31].toString())>50) && (Integer.parseInt(obj[31].toString())<=75)){%> background-color: #F8CB2E;color:black <%}%>
																											   <%if( (Integer.parseInt(obj[31].toString())>75) && (Integer.parseInt(obj[31].toString())<=100)){%> background-color:green <%}%>
																	"><%=obj[31]!=null?StringEscapeUtils.escapeHtml4(obj[31].toString()): " - " %></span>
															</div>
														</div>
														<%}else{ %>
														<div class="progress nil-bar" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
															<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
																Nil
															</div>
														</div>
														<%} %>
													</td>
													<td class="custom-td">
														<%
														BigInteger number = new BigInteger(obj[43].toString());
														BigInteger total = new BigInteger("0");
														if(number.compareTo(new BigInteger("0")) >0){ %>
														<div class="row">
															<div class="col-md-10">
																<%total = total.add(new BigInteger(String.valueOf(Math.round(Double.parseDouble(obj[35].toString() ))))); %>
																<%total = total.add(new BigInteger(String.valueOf(Math.round(Double.parseDouble(obj[37].toString() ))))); %>
																<%total = total.add(new BigInteger(String.valueOf(Math.round(Double.parseDouble(obj[39].toString() ))))); %>
																<%total = total.add(new BigInteger(String.valueOf(Math.round(Double.parseDouble(obj[41].toString() ))))); %>
																<div class="progress" onclick="overallfinance()" data-toggle="tooltip" title="Expenditure : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[35].toString() ))))%>
																																			<br>OC : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[37].toString() ))))%> 
																																			<br>DIPL : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[39].toString() ))))%> 
																																			<br>Balance : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[41].toString() ))))%> 
																																			<br><b>Total : &#8377; <%=nfc.rupeeFormat(total.toString())%> </b>
																																																			">
	
																  	<div class="progress-bar progress-bar-striped bg-success" style="width:<%=obj[36]%>%"  > </div>
																 	<div class="progress-bar progress-bar-striped bg-warning" style="width:<%=obj[38]%>%" > </div>
																  	<div class="progress-bar progress-bar-striped bg-primary" style="width:<%=obj[40]%>%"  > </div>
																  	<div class="progress-bar progress-bar-striped bg-danger" style="width:<%=obj[42]%>%" > </div>
																  	
																</div>
															</div>
															<div class="col-md-1" style="padding-left: 0px !important;">
																		<span class="health-circle"  style="cursor: pointer;<%if(Integer.parseInt(obj[42].toString())<=25){%> background-color:red<%}%>
																												   <%if( (Integer.parseInt(obj[42].toString())>25) && (Integer.parseInt(obj[42].toString())<=50)){%> background-color: #EE5007; <%}%>
																												   <%if( (Integer.parseInt(obj[42].toString())>50) && (Integer.parseInt(obj[42].toString())<=75)){%> background-color: #F8CB2E;color:black <%}%>
																												   <%if( (Integer.parseInt(obj[42].toString())>75) && (Integer.parseInt(obj[42].toString())<=100)){%> background-color:green <%}%>"
																		><%=obj[42]!=null?StringEscapeUtils.escapeHtml4(obj[42].toString()): " - " %></span>
															</div>
															<div class="col-md-1" style="padding-left: 0px !important">
															</div>
														</div>
														
														<%}else{ %>
														<div class="progress nil-bar" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
															<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
																Nil
															</div>
														</div>
														<%} %>
													</td>
												</tr> 
												
												<%  ProjectCount++; } }  } } %>
												
											</tbody>
												
												
										</table>	
					
									</div> 
								</div>
								 
								 
								<%}	else{ %>
								
								
									<h2> No Project Details  </h2>
								
								<%} %>

	        	
			
			
			<div id="overalldivdg"  class="card" style="background: transparent;display:none" >
			
			<div class="card-header">
						<h3><%=ProjectHealthTotalData[45]!=null?StringEscapeUtils.escapeHtml4(ProjectHealthTotalData[45].toString()): " - " %> - Cluster Details</h3>
						<a class="btn btn-info btn-sm back"   href="MainDashBoard.htm" style="float: right">Back</a>
				</div>
		
			<div  style="background-color: rgba(255, 255, 255, 0.39999) !important ;border-radius: 4px ;/* max-height: 26rem */; overflow-y:auto;overflow-x:hidden  ">
								
				<table class="table meeting tableFixHead fixed-table "  style="table-layout: fixed"> 
										<thead style=" background: #1363DF; color: white;">												
											<tr>
												<td style="width:4%">
													<a  data-toggle="modal"  class="fa faa-pulse animated " data-target="#exampleModal1" data-whatever="@mdo" style="padding: 0px 1.5rem;cursor:pointer" ><i class="fa fa-info-circle " style="font-size: 1.3rem;color: " aria-hidden="true"></i> </a>
												</td>
												<td style="width:5%"><span style="font-size :15px;font-weight: bold; ">Lab</span></td>
												<td style="padding: 0px !important"><span style="font-size :15px;font-weight: bold;">PMRB </span></td>
												<td style="padding: 0px !important"><span style="font-size :15px;font-weight: bold;">EB </span></td>
												<td ><span style="font-size :15px;font-weight: bold;  ">Milestone </span></td>
												<td ><span style="font-size :15px;font-weight: bold;  ">Action</span></td>
												<td ><span style="font-size :15px;font-weight: bold;  ">Risk</span></td>
												<td ><span style="font-size :15px;font-weight: bold;  ">Finance</span></td>
											</tr>
										</thead>
										
										<tbody>
	
											<%for(Object[] obj : projecthealthtotaldg) { %>
		
											<tr>
											
												<td><a href="javascript:LabDetails('<%=obj[45] %>')"> <i class="fa fa-hand-o-right" aria-hidden="true" style="color: purple;font-size: 1.3rem !important"></i></a></td>
												<td   style="font-weight: 800; font-size:0.75rem;text-align:left;"><%=obj[45]!=null?StringEscapeUtils.escapeHtml4(obj[45].toString()): " - " %>	</td>
												<td class="custom-td">
													<%if(Integer.parseInt(obj[2].toString())>0){ %>
														
														<div class="row">
															<div class="col-md-11">
															    <div class="progress"  >
																  <div class="progress-bar progress-bar-striped bg-success" onclick="overallmeetingredirectdg('<%=obj[2]%>','1', 'B')" style="width:<%=obj[29]%>%;" data-toggle="tooltip" title="PMRB Held : <%=obj[0]%> / <%=obj[2] %>" ></div>
																  <div class="progress-bar progress-bar-striped bg-primary" onclick="overallmeetingredirectdg('<%=obj[2]%>','1', 'C' )" style="width:<%=obj[30]%>%;" data-toggle="tooltip" title="PMRB Pending : <%=obj[1]%> / <%=obj[2] %>" ></div>
																</div>
														  	</div>
															<div class="col-md-1" style="padding-left: 0px !important">
																<span class="health-circle" style="<%if(Integer.parseInt(obj[30].toString())<=25){%> background-color:green <%}%>
																								   <%if( (Integer.parseInt(obj[30].toString())>25) && (Integer.parseInt(obj[30].toString())<=50)){%> background-color: #F8CB2E;color:black <%}%>
																								   <%if( (Integer.parseInt(obj[30].toString())>50) && (Integer.parseInt(obj[30].toString())<=75)){%> background-color: #EE5007 <%}%>
																								   <%if( (Integer.parseInt(obj[30].toString())>75) && (Integer.parseInt(obj[30].toString())<=100)){%> background-color:red <%}%>
																"><%=obj[30]!=null?StringEscapeUtils.escapeHtml4(obj[30].toString()): " - " %></span>
															</div>
											
														</div>
						
													<%}else{ %>
													<div class="progress" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
														<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
															Nil
														</div>
													</div>
													<%} %>
												</td>	
												<td class="custom-td">
													<%if(Integer.parseInt(obj[5].toString())>0){ %>
														
														<div class="row">
															<div class="col-md-11">
															    <div class="progress"  >
																  <div class="progress-bar progress-bar-striped bg-success" onclick="overallmeetingredirectdg('<%=obj[2]%>','1', 'B')" style="width:<%=obj[31]%>%;" data-toggle="tooltip" title="EB Held : <%=obj[3]%> / <%=obj[5] %>" ></div>
																  <div class="progress-bar progress-bar-striped bg-primary" onclick="overallmeetingredirectdg('<%=obj[2]%>','1', 'C' )" style="width:<%=obj[32]%>%;" data-toggle="tooltip" title="EB Pending : <%=obj[4]%> / <%=obj[5] %>" ></div>
																</div>
														  	</div>
															<div class="col-md-1" style="padding-left: 0px !important">
																<span class="health-circle" style="<%if(Integer.parseInt(obj[32].toString())<=25){%> background-color:green <%}%>
																								   <%if( (Integer.parseInt(obj[32].toString())>25) && (Integer.parseInt(obj[32].toString())<=50)){%> background-color: #F8CB2E;color:black <%}%>
																								   <%if( (Integer.parseInt(obj[32].toString())>50) && (Integer.parseInt(obj[32].toString())<=75)){%> background-color: #EE5007 <%}%>
																								   <%if( (Integer.parseInt(obj[32].toString())>75) && (Integer.parseInt(obj[32].toString())<=100)){%> background-color:red <%}%>
																"><%=obj[32]!=null?StringEscapeUtils.escapeHtml4(obj[32].toString()): " - " %></span>
															</div>
														</div>
						
													<%}else{ %>
													<div class="progress" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
														<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
															Nil
														</div>
													</div>
													<%} %>
												</td>
												<td class="custom-td">
													<%if(Integer.parseInt(obj[9].toString())>0){ %>
													<div class="row">
														<div class="col-md-11">
															<div class="progress" onclick="overallcommonredirectdg('mil','<%=obj[2]%>')">
															  <div class="progress-bar progress-bar-striped bg-success" style="width:<%=obj[10]%>%" data-toggle="tooltip" title="Completed : <%=obj[8]%> / <%=obj[9] %>" ></div>
															  <div class="progress-bar progress-bar-striped bg-warning" style="width:<%=obj[33]%>%" data-toggle="tooltip" title="Delayed : <%=obj[7]%> / <%=obj[9] %>" ></div>
															  <div class="progress-bar progress-bar-striped bg-danger" style="width:<%=obj[44]%>%" data-toggle="tooltip" title="Pending : <%=obj[6]%> / <%=obj[9] %>" ></div>															  
															</div>
														</div>
														<div class="col-md-1" style="padding-left: 0px !important">
																<span class="health-circle" style="<%if(Integer.parseInt(obj[44].toString())<=25){%> background-color:green <%}%>
																								   <%if( (Integer.parseInt(obj[44].toString())>25) && (Integer.parseInt(obj[44].toString())<=50)){%> background-color: #F8CB2E;color:black <%}%>
																								   <%if( (Integer.parseInt(obj[44].toString())>50) && (Integer.parseInt(obj[44].toString())<=75)){%> background-color: #EE5007 <%}%>
																								   <%if( (Integer.parseInt(obj[44].toString())>75) && (Integer.parseInt(obj[44].toString())<=100)){%> background-color:red <%}%>
																"><%=obj[44]!=null?StringEscapeUtils.escapeHtml4(obj[44].toString()): " - " %></span>
														</div>
													</div>
													<%}else{ %>
													<div class="progress" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
														<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
															Nil
														</div>
													</div>
													<%} %>
												</td>
												<td class="custom-td">
													<%if(Integer.parseInt(obj[15].toString())>0){ %>
													<div class="row">
														<div class="col-md-11">
															<div class="progress" onclick="overallcommonredirect('action','<%=obj[2]%>')">
															  <div class="progress-bar progress-bar-striped bg-success" style="width:<%=obj[37]%>%" data-toggle="tooltip" title="Completed : <%=obj[14]%> / <%=obj[15]%>" ></div>
															  <div class="progress-bar progress-bar-striped bg-warning" style="width:<%=obj[36]%>%" data-toggle="tooltip" title="Delayed : <%=obj[13]%> / <%=obj[15]%>" ></div>
															  <div class="progress-bar progress-bar-striped bg-primary" style="width:<%=obj[35]%>%" data-toggle="tooltip" title="Forwarded : <%=obj[12]%> / <%=obj[15]%>" ></div>
															  <div class="progress-bar progress-bar-striped bg-danger" style="width:<%=obj[34]%>%" data-toggle="tooltip" title="Pending : <%=obj[11]%> / <%=obj[15]%>" ></div>
															</div>
														</div>
														<div class="col-md-1" style="padding-left: 0px !important">
															<span class="health-circle" style="<%if(Integer.parseInt(obj[34].toString())<=25){%> background-color:green <%}%>
																									   <%if( (Integer.parseInt(obj[34].toString())>25) && (Integer.parseInt(obj[34].toString())<=50)){%> background-color: #F8CB2E;color:black <%}%>
																									   <%if( (Integer.parseInt(obj[34].toString())>50) && (Integer.parseInt(obj[34].toString())<=75)){%> background-color: #EE5007 <%}%>
																									   <%if( (Integer.parseInt(obj[34].toString())>75) && (Integer.parseInt(obj[34].toString())<=100)){%> background-color:red <%}%>
															"><%=obj[34]!=null?StringEscapeUtils.escapeHtml4(obj[34].toString()): " - " %></span>
														</div>
													</div>
													<%}else{ %>
													<div class="progress" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
														<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
															Nil
														</div>
													</div>
													<%} %>
												</td> 
												<td class="custom-td">
													<%if(Integer.parseInt(obj[18].toString())>0){ %>
													<div class="row">
														<div class="col-md-11">
															<div class="progress" onclick="overallcommonredirect('risk','<%=obj[2]%>')">
															  <div class="progress-bar progress-bar-striped bg-success" style="width:<%=obj[39]%>%" data-toggle="tooltip" title="Completed : <%=obj[16]%> / <%=obj[18] %>" ></div>
															  <div class="progress-bar progress-bar-striped bg-danger" style="width:<%=obj[38]%>%" data-toggle="tooltip" title="Pending : <%=obj[17]%> / <%=obj[18] %>" ></div>
															</div>
														</div>
														<div class="col-md-1" style="padding-left: 0px !important">
																<span class="health-circle" style="<%if(Integer.parseInt(obj[38].toString())<=25){%> background-color:green <%}%>
																										   <%if( (Integer.parseInt(obj[38].toString())>25) && (Integer.parseInt(obj[38].toString())<=50)){%> background-color: #F8CB2E;color:black <%}%>
																										   <%if( (Integer.parseInt(obj[38].toString())>50) && (Integer.parseInt(obj[38].toString())<=75)){%> background-color: #EE5007 <%}%>
																										   <%if( (Integer.parseInt(obj[38].toString())>75) && (Integer.parseInt(obj[38].toString())<=100)){%> background-color:red <%}%>
																"><%=obj[38]!=null?StringEscapeUtils.escapeHtml4(obj[38].toString()): " - " %></span>
														</div>
													</div>
													<%}else{ %>
													<div class="progress" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
														<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
															Nil
														</div>
													</div>
													<%} %>
												</td>
												<td class="custom-td">
													<%
													BigInteger number = new BigInteger(obj[23].toString());
													if(number.compareTo(new BigInteger("0")) >0){ %>
													<div class="row">
														<div class="col-md-10">
															<div class="progress" onclick="overallfinance()">
															  <div class="progress-bar progress-bar-striped bg-success" style="width:<%=obj[40]%>%" data-toggle="tooltip" title="Expenditure : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[19].toString() ))))%>" ></div>
															  <div class="progress-bar progress-bar-striped bg-warning" style="width:<%=obj[41]%>%" data-toggle="tooltip" title="OC : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[20].toString() ))))%> " ></div>
															  <div class="progress-bar progress-bar-striped bg-primary" style="width:<%=obj[42]%>%" data-toggle="tooltip" title="DIPL : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[21].toString() ))))%> " ></div>
															  <div class="progress-bar progress-bar-striped bg-danger" style="width:<%=obj[43]%>%" data-toggle="tooltip" title="Balance : &#8377; <%=nfc.rupeeFormat(String.valueOf(Math.round(Double.parseDouble(obj[22].toString() ))))%> " ></div>
															</div>
														</div>
														<div class="col-md-1" style="padding-left: 0px !important">
																	<span class="health-circle" style="<%if(Integer.parseInt(obj[40].toString())<=25){%> background-color:green <%}%>
																											   <%if( (Integer.parseInt(obj[40].toString())>25) && (Integer.parseInt(obj[40].toString())<=50)){%> background-color: #F8CB2E;color:black <%}%>
																											   <%if( (Integer.parseInt(obj[40].toString())>50) && (Integer.parseInt(obj[40].toString())<=75)){%> background-color: #EE5007 <%}%>
																											   <%if( (Integer.parseInt(obj[40].toString())>75) && (Integer.parseInt(obj[40].toString())<=100)){%> background-color:red <%}%>
																	"><%=obj[40]!=null?StringEscapeUtils.escapeHtml4(obj[40].toString()): " - " %></span>
														</div>
														<div class="col-md-1" style="padding-left: 0px !important">
														</div>
													</div>
													
													<%}else{ %>
													<div class="progress" style="background-color:#cdd0cb !important;height: 1.2rem !important;">
														<div class="progress-bar" role="progressbar" style=" width: 100%; background-color:#cdd0cb !important;color:black;font-weight: bold;  "  >
															Nil
														</div>
													</div>
													<%} %>
												</td> 
												
												
											</tr>
											
											
											<%} %>
											
											
										</tbody>
											
											
									</table>	
									
												
				
								</div> 
							
							</div> 
			
			
			
			

		</div> 
	
	</div>	
</div>
	
<!-- ************************Informative Modal Start********************* -->

	<div class="modal fade " id="exampleModal1" tabindex="-1" aria-labelledby="exampleModalLabel1" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				      
				<div class="modal-header ">
					<h5 class="modal-title" id="exampleModalLabel" style="color:#145374">Colour Coding Summary</h5>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
			        </button>
			  	</div>
					      
				<div class="modal-body">
						
					<div class="row">
						<div style="text-align: left">
								<p style="margin-bottom: 0px !important;margin-left:10px">Project </p>
								<hr class="modal-hr">
								<ul class="modal-list">
						          	<li><span class="modal-span" style="color:green">&#8226;</span><span class="modal-text">Indian Army</span></li>
						           	<li><span class="modal-span" style="color:#007bff">&#8226;</span><span class="modal-text">Indian Navy</span></li>
						           	<li><span class="modal-span" style="color:#1F4690">&#8226;</span><span class="modal-text">Indian Air Forces</span></li>
						           	<li><span class="modal-span" style="color:#8E3200">&#8226;</span><span class="modal-text">Home Land Security</span></li>
						           	<li><span class="modal-span" style="color:#231955">&#8226;</span><span class="modal-text">DRDO</span></li>
						           	<li><span class="modal-span" style="color:#EE5007">&#8226;</span><span class="modal-text">Others</span></li>
					            </ul>
							</div>
					</div>	
					<div class="row">
						<div style="text-align: left">
								<p style="margin-bottom: 0px !important;margin-left:10px">Project Health (Pending %)</p>
								<hr class="modal-hr">
								<ul class="modal-list">
						          	<li><span class="modal-span" style="color:green">&#8226;</span><span class="modal-text">0-25%</span></li>
						           	<li><span class="modal-span" style="color:#F8CB2E;">&#8226;</span><span class="modal-text">25-50%</span></li>
						           	<li><span class="modal-span" style="color:#EE5007">&#8226;</span><span class="modal-text">50-75%</span></li>
						           	<li><span class="modal-span" style="color:red">&#8226;</span><span class="modal-text">75-100%</span></li>
					            </ul>
							</div>
					</div>		

					<div class="row">
						<div style="text-align: left">
								<p style="margin-bottom: 0px !important;margin-left:10px">Milestone</p>
								<hr class="modal-hr">
								<ul class="modal-list">
									<li><span class="modal-span green" >&#8226;</span><span class="modal-text">Completed</span></li>
									<li><span class="modal-span yellow" >&#8226;</span><span class="modal-text">Delayed</span></li>
						          	<li><span class="modal-span red" >&#8226;</span><span class="modal-text">Pending</span></li>
					            </ul>
							</div>
					</div>		
					
					<div class="row">
						<div style="text-align: left">
								<p style="margin-bottom: 0px !important;margin-left:10px">Action</p>
								<hr class="modal-hr">
								<ul class="modal-list">
						           	<li><span class="modal-span green" >&#8226;</span><span class="modal-text">Completed</span></li>
						           	<li><span class="modal-span yellow" >&#8226;</span><span class="modal-text">Delayed</span></li>
						           	<li><span class="modal-span blue" >&#8226;</span><span class="modal-text">Forwarded</span></li>
						           	<li><span class="modal-span red" >&#8226;</span><span class="modal-text">Pending</span></li>
					            </ul>
							</div>
					</div>
					
					<div class="row">
						<div style="text-align: left">
								<p style="margin-bottom: 0px !important;margin-left:10px">Risks</p>
								<hr class="modal-hr">
								<ul class="modal-list">
									<li><span class="modal-span green" >&#8226;</span><span class="modal-text">Completed</span></li>
									<li><span class="modal-span yellow" >&#8226;</span><span class="modal-text">Delayed</span></li>
						          	<li><span class="modal-span red" >&#8226;</span><span class="modal-text">Pending</span></li>
					            </ul>
							</div>
					</div>
					
					<div class="row">
						<div style="text-align: left">
								<p style="margin-bottom: 0px !important;margin-left:10px">Finance</p>
								<hr class="modal-hr">
								<ul class="modal-list">
						          	<li><span class="modal-span green" >&#8226;</span><span class="modal-text">Expenditure</span></li>
						           	<li><span class="modal-span yellow" >&#8226;</span><span class="modal-text">O/S</span></li>
						           	<li><span class="modal-span blue" >&#8226;</span><span class="modal-text">DIPL</span></li>
						           	<li><span class="modal-span red" >&#8226;</span><span class="modal-text">Balance</span></li>
					            </ul>
							</div>
					</div>
				
					          
				</div>
				
					      
			</div>
		</div>
	</div>

<!-- ************************Informative Modal End********************* -->




<script>

$(document).ready(function(){
	
	var logintype = '<%=logintype%>';
	if(logintype=='K')
		$('#overalldivdg').css('display','block');
	else
		$('#projectdetailsview').css('display','block');
	
	
	
})



$('.progress-bar[data-toggle="tooltip"]').tooltip({
    animated: 'fade',
    placement: 'bottom'
});

function ProjectDetails(value){
	
	$("#overalldiv").css("display","none");
	$('#projectgraph').css("display","block");
	
	$('#projectiddoc').val(value);
	$('#projectidchanges').val(value);
	charts(value);
	
}

function overalldetails(value){
	
	$("#overalldiv").css("display","block");
	$('#projectgraph').css("display","none");
	$('#projectidchanges').val("A");
	
	/* individualprojectdetails(value); */
	charts(value);
	
}


function charts(value){
	
	$projectid=value;
		
		$
			
			.ajax({
			
				type:"GET",
				url:"IndividualProjectDetails.htm",
				data : {
					
					ProjectId : $projectid
					
				},
				datatype : 'json',
				success : function(result) {

					var result = JSON.parse(result);
					var values = Object.values(result).map(function(key, value) {
						  return result[key,value]
						});
				
					
					/* logic to print the project wise data in card */	
					
					document.getElementById('financevalue').innerHTML = values[23].toLocaleString('en-IN');
					document.getElementById('risksvalue').innerHTML = values[16] + ' / ' + values[18];
					document.getElementById('actionvalue').innerHTML = values[14] + ' / ' + values[15];
					document.getElementById('milestonevalue').innerHTML = values[8] + ' / ' + values[9];
					
					if(values[10]!=null)
					document.getElementById('milestonepercentage').innerHTML = values[10] + '%';
					else
					document.getElementById('milestonepercentage').innerHTML = 'Nil'; 
					
					
					
					var s=  values[0] + '/' + values[2]
					document.getElementById('meetingsvaluepmrc').innerHTML = s;
					var t=  values[3] + '/' + values[5]
					document.getElementById('meetingsvalueeb').innerHTML = t;
					
					<%-- if(values[24]!='A')
					document.getElementById('projecttitle').innerHTML = 'Project : ' + values[25];
					else
					document.getElementById('projecttitle').innerHTML = 'Project Count : ' + <%=ProjectCount%>;	
					document.getElementById('projecttitle').innerHTML = 'PROJECT HEALTH (' + <%=ProjectCount%> + ')';	 --%>
					
					
					
					<!-- ******************************************* Project Details Graph Script ******************************************************* -->
					
					Highcharts.chart('containerh', {
					    chart: {
					        type: 'bar',
					        height: (12 / 16 * 100) + '%' 
					    },
					    exporting: {
					        buttons: {
					          contextButton: {
					            menuItems: [
					              'printChart',
					              'downloadPNG',
					              'downloadJPEG',
					              'downloadPDF',
					              'downloadCSV',
					              'downloadXLS',
					              'viewData'
					            ]
					          }
					        }

					      },
					    title: {
					        text: 'Meeting Data',
					       	style : {
							        	fontWeight: 'bold'
							}
					    },
					    subtitle: {
					        text: ''
					    },
					    xAxis: {
					        categories: ['PMRB', 'EB'],
					        title: {
					            text: null
					        },
					        labels: {
					            overflow: 'justify',
					            style : {
					            	fontWeight: 'bold',
						        	
								}
					        }
					    },
					    yAxis: {
					        min: 0,
					        title: {
					            text: 'Meeting (count)',
					            align: 'high'
					        },
					        labels: {
					            overflow: 'justify',
					            style : {
					            	fontWeight: 'bold',
						        	
								}
					        }
					    },
					    tooltip: {
					        valueSuffix: ''
					    },
					    plotOptions: {
					        bar: {
					            dataLabels: {
					                enabled: true
					            }
					        }
					    },
					    colors: [
					    	'#28a745',
					        '#dc3545',
					        
					    ],
					    credits: {
					        enabled: false
					    },
					    series: meetingdata(),
					 
					    
					});		

					function meetingdata(){
						
						let meetingdata=[];
						if(parseInt(values[2])>0 || parseInt(values[5])>0){
							meetingdata=[
								{
							        name: 'Held',
							        data: [parseInt(values[0]), parseInt(values[3]),]
							    }, {
							        name: 'Pending',
							        data: [parseInt(values[1]), parseInt(values[4]),]
							    },
								
							]
						}
						return meetingdata;
						
					}
					
					/********************************************** Milestone ****************************************************/

					Highcharts.chart('containerh2', {
					    chart: {
					        type: 'pie',
					        options3d: {
					            enabled: true,
					            alpha: 45
					        },
					        height: (12 / 16 * 100) + '%' 
					    },
					    exporting: {
					        buttons: {
					          contextButton: {
					            menuItems: [
					              'printChart',
					              'downloadPNG',
					              'downloadJPEG',
					              'downloadPDF',
					              'downloadCSV',
					              'downloadXLS',
					              'viewData'
					            ]
					          }
					        }

					      },
					    title: {
					        text: 'Milestone',
					        style : {
					        	fontWeight: 'bold'
					        }
					    },
					    subtitle: {
					        text: ''
					    },
					    plotOptions: {
					        pie: {
					            innerSize: 100,
					            depth: 15
					        }
					    },
					    colors: [
					        '#ffc107',
					        '#dc3545',
					        '#28a745'
					    ],
					    series: [{
					        name: 'Milestone',
					        data: miletsonedata(),
					    
					    }],
					    credits: {
					        enabled: false
					    },
					});
					
					function miletsonedata(){
						let miletsonedata=[];
						
						if(parseInt(values[9])>0){
							miletsonedata=[ 
								['Pending', parseInt(values[6])],
					            ['Delayed', parseInt(values[7])],
					            ['Completed', parseInt(values[8])],
					            ]
						}
						
						return miletsonedata;
					}

					/********************************************** Action ****************************************************/

					Highcharts.chart('containerh3', {
					    chart: {
					        type: 'pie',
					        options3d: {
					            enabled: true,
					            alpha: 45
					        },
					        height: (12 / 16 * 100) + '%' 
					    },
					    exporting: {
					        buttons: {
					          contextButton: {
					            menuItems: [
					              'printChart',
					              'downloadPNG',
					              'downloadJPEG',
					              'downloadPDF',
					              'downloadCSV',
					              'downloadXLS',
					              'viewData'
					            ]
					          }
					        }

					      },
					    title: {
					        text: 'Action',
					        style : {
					        	fontWeight: 'bold'
					        }
					    },
					    subtitle: {
					        text: ''
					    },
					    plotOptions: {
					        pie: {
					            innerSize: 100,
					            depth: 45
					        }
					    },
					    colors: [
					        '#ffc107',
					        '#dc3545',
					        '#28a745',
					        '#007bff'
					    ],
					    series: [{
					        name: 'Action',
					        data: actiondata(),
					    }],
					    credits: {
					        enabled: false
					    },
					});
					
					function actiondata(){
						let actiondata=[];
						
						if(parseInt(values[15])>0){
							actiondata=[
								 	['Pending', parseInt(values[11])],
						            ['Delayed', parseInt(values[13])],
						            ['Completed', parseInt(values[14])],
						            ['Forwarded', parseInt(values[12])],
								
							]
						}
						
						return actiondata;
					}
					
					/*************************************************************** Risk *****************************************************  */

					Highcharts.chart('containerh4', {
					    chart: {
					        type: 'column',
					        height: (12 / 16 * 100) + '%' 
					    },
					    exporting: {
					        buttons: {
					          contextButton: {
					            menuItems: [
					              'printChart',
					              'downloadPNG',
					              'downloadJPEG',
					              'downloadPDF',
					              'downloadCSV',
					              'downloadXLS',
					              'viewData'
					            ]
					          }
					        }

					      },
					    title: {
					        text: 'Risks',
					        style : {
					        	fontWeight: 'bold'
					        }
					    },
					    subtitle: {
					        text: ''
					    },
					    xAxis: {
					        categories: [
					            'Risk',
					        ],
					        crosshair: true
					    },
					    yAxis: {
					        min: 0,
					        title: {
					            text: 'Count'
					        }
					    },
					    tooltip: {
					        headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
					        pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
					            '<td style="padding:0"><b>{point.y:.1f} </b></td></tr>',
					        footerFormat: '</table>',
					        shared: true,
					        useHTML: true
					    },
					    plotOptions: {
					        column: {
					            pointPadding: 0.2,
					            borderWidth: 0
					        }
					    },
					    colors: [
					    	'#28a745',
					        '#dc3545',
					        
					    ],
					    series: riskdata(),
					    credits: {
					        enabled: false
					    },
					});
					
					function riskdata(){
						let riskdata=[];
						if(parseInt(values[18])>0){
							riskdata=[
								{
							        name: 'Completed',
							        data: [parseInt(values[16])]

							    },
								{
							        name: 'Pending',
							        data: [parseInt(values[17])]

							    }, 
							]
							
							
						}
						return riskdata;
						
					}


					/**************************************** Finance ******************************************* */
					
					$(function () {
					
					var TotalFinanceValue =0;
					if(parseInt(values[23])>0){
						 TotalFinanceValue = 'Total Finance Value : ' + parseInt(values[23]).toLocaleString('en-IN', {
						    maximumFractionDigits: 0,
						    style: 'currency',
						    currency: 'INR'
						});	
					}
					
						
					 Highcharts.setOptions({
				      lang: {
				        thousandsSep: ','
				      }
					});
					
					Highcharts.chart('containerh5', {
					    chart: {
					        type: 'pie',
					        height: (12 / 16 * 100) + '%' 
					    },
					    exporting: {
					        buttons: {
					          contextButton: {
					            menuItems: [
					              'printChart',
					              'downloadPNG',
					              'downloadJPEG',
					              'downloadPDF',
					              'downloadCSV',
					              'downloadXLS',
					              'viewData'
					            ]
					          }
					        }

					      },
					    title: {
					        text: 'Finance',
					        style : {
					        	fontWeight: 'bold'
					        }
					    },
					    subtitle: {
					        text: TotalFinanceValue ,
					        style : {
					        	fontWeight: 'bold'
					        }
					    },

					    accessibility: {
					        announceNewData: {
					            enabled: true
					        },
					        point: {
					            valueSuffix: ''
					        }
					    },

					    plotOptions: {
					        series: {
					            dataLabels: {
					                enabled: true,
					                 formatter: function() {
					                    return '<p>&#8377;</p>'+ Highcharts.numberFormat(this.y, 0);
					                } 
					                /* format: '{point.name}: {point.y:.1f}' */
					            }
					        }
					    },
					    
					    
					    
					    colors: [
					        '#ffc107',
					        '#28a745',
					        '#dc3545',
					        '#007bff'
					    ],
					    tooltip: {
					        headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
					        pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>&#8377; {point.y:.2f}</b><br/>'
					    },
					    
					    series: [
					    	{
					            name: "Finance",
					            colorByPoint: true,
					           
					            data:   generatedata(),
			
					        } 
					       
					    ],
					    
					    credits: {
					        enabled: false
					    },

					},

			);
					
					});
				  
			function  generatedata(){
				
				let financedata=[];
				
				if(parseInt(values[23])>0){
					
					financedata = [
						
						{
		                    name: "O/S",
		                    y: parseInt(values[20]),
		                    drilldown: "O/S"
		                },
		                {
		                    name: "Expenditure",
		                    y: parseInt(values[19]),
		                    drilldown: "Expenditure"
		                },
		                {
		                    name: "Balance",
		                    y: parseInt(values[22]),
		                    drilldown: "Balance"
		                },
		                {
		                    name: "DIPL",
		                    y: parseInt(values[21]),
		                    drilldown: "DIPL"
		                }, 
						
					]
					
				}
				
				return financedata;
			}			
					
			
			/**************************************** Document  ******************************************* */
			

				Highcharts.chart('containerh6', {
				    chart: {
				        type: 'variablepie',
				        height: (12 / 16 * 100) + '%' 
				    },
				    title: {
				        text: 'Document Statistics',
				        style : {
				        	fontWeight: 'bold'
				        }
				    },
				    colors: [
				        '#ffc107',
				        '#28a745',
				        '#dc3545',
				        '#007bff'
				    ],
				    tooltip: {
				        headerFormat: '',
				        pointFormat: '<span style="color:{point.color}">\u25CF</span> <b> {point.name}</b><br/>' +
				            'Area (square km): <b>{point.y}</b><br/>' +
				            'Population density (people per square km): <b>{point.z}</b><br/>'
				    },
				    series: [{
				        minPointSize: 10,
				        innerSize: '20%',
				        zMin: 0,
				        name: 'countries',
				        data: [{
				            name: 'Prepared',
				            y: 505370,
				            z: 92.9
				        }, {
				            name: 'Approved',
				            y: 551500,
				            z: 118.7
				        }, {
				            name: 'Pending',
				            y: 312685,
				            z: 124.6
				        }, {
				            name: 'Reviewed',
				            y: 78867,
				            z: 137.5
				        }, ]
				    }],
				    
				    credits: {
				        enabled: false
				    },
				    
				    
				    
				});


			}
			
			})
			
					
		


	}



</script>


<script type="text/javascript">

$(function() {

	  $(".progress").each(function() {

	    var value = $(this).attr('data-value');
	    var left = $(this).find('.progress-left .progress-bar');
	    var right = $(this).find('.progress-right .progress-bar');

	    if (value > 0) {
	      if (value <= 50) {
	        right.css('transform', 'rotate(' + percentageToDegrees(value) + 'deg)')
	      } else {
	        right.css('transform', 'rotate(180deg)')
	        left.css('transform', 'rotate(' + percentageToDegrees(value - 50) + 'deg)')
	      }
	    }

	  })

	  function percentageToDegrees(percentage) {

	    return percentage / 100 * 360

	  }

	});

function sendMail(){
	console.log("hiii")
	Swal.fire({
	    title: 'Loading...',
	    allowOutsideClick: false,
	    didOpen: () => {
	        Swal.showLoading()
	    }
	});

	// Simulate a delay (e.g., fetching data)
	setTimeout(() => {
	    // Close the loading indicator
	    Swal.close();
	}, 3000);
}
</script>


</body>
</html>