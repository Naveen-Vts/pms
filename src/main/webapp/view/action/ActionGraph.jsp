
<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.stream.Collectors, java.util.*"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/action/actionGraph.css" var="actionGraph" />
<link href="${actionGraph}" rel="stylesheet" />
<spring:url value="/resources/css/action/actionCommon.css" var="actionCommon" />
<link href="${actionCommon}" rel="stylesheet" />

</head>
<body>
  <%
  List<Object[]> projectslist=(List<Object[]>)request.getAttribute("projectslist");
  List<Object[]> actionlist = (List<Object[]>)request.getAttribute("ActionList");
  String projectid=(String)request.getAttribute("projectid");
  List<Object[]> Actiontotal = null;
  List<Object[]> Meetingtotal = null;
  List<Object[]> Milestonestotal= null;
  
  if(actionlist!=null && actionlist.size()>0){
	   Actiontotal = actionlist.stream().filter(e-> e[13].toString().equalsIgnoreCase("N")).collect(Collectors.toList());
	   Meetingtotal = actionlist.stream().filter(e-> e[13].toString().equalsIgnoreCase("S")).collect(Collectors.toList());
	   Milestonestotal = actionlist.stream().filter(e-> !e[13].toString().equalsIgnoreCase("S") && !e[13].toString().equalsIgnoreCase("N")).collect(Collectors.toList());
  }
   
  int Actionactive = 0;
  int ActionInprogress  = 0;
  int ActionClose  =0;

  int Meetingactive = 0;
  int MeetingInprogress  = 0;
  int MeetingClose  = 0;

  int Milestonesactive = 0;
  int MilestonesInprogress  = 0;
  int MilestonesClose  = 0;
  
   int acttotal=0;
  if(Actiontotal!=null && Actiontotal.size()>0){
	  acttotal=Actiontotal.size();
	  Actionactive = Actiontotal.stream().filter(e-> e[8]!=null && e[8].toString().equalsIgnoreCase("A")).collect(Collectors.toList()).size();
	  ActionInprogress  = Actiontotal.stream().filter(e-> e[8]!=null && !e[8].toString().equalsIgnoreCase("C") && !e[8].toString().equalsIgnoreCase("A")).collect(Collectors.toList()).size();
	  ActionClose  = Actiontotal.stream().filter(e-> e[8]!=null && e[8].toString().equalsIgnoreCase("C")).collect(Collectors.toList()).size();
  }
  
  int Meetingttl=0;
  if(Meetingtotal!=null && Meetingtotal.size()>0){
	  Meetingttl=Meetingtotal.size();
	   Meetingactive = Meetingtotal.stream().filter(e-> e[8]!=null && e[8].toString().equalsIgnoreCase("A")).collect(Collectors.toList()).size();
	   MeetingInprogress  = Meetingtotal.stream().filter(e-> e[8]!=null && !e[8].toString().equalsIgnoreCase("C") && !e[8].toString().equalsIgnoreCase("A")).collect(Collectors.toList()).size();
	   MeetingClose  = Meetingtotal.stream().filter(e-> e[8]!=null && e[8].toString().equalsIgnoreCase("C")).collect(Collectors.toList()).size();
  }
  
  int Miletotal=0;
  if(Milestonestotal!=null && Milestonestotal.size()>0){
	  Miletotal=Milestonestotal.size();
	   Milestonesactive = Milestonestotal.stream().filter(e-> e[8]!=null && e[8].toString().equalsIgnoreCase("A")).collect(Collectors.toList()).size();
	   MilestonesInprogress  = Milestonestotal.stream().filter(e-> e[8]!=null && !e[8].toString().equalsIgnoreCase("C") && !e[8].toString().equalsIgnoreCase("A")).collect(Collectors.toList()).size();
	   MilestonesClose  = Milestonestotal.stream().filter(e-> e[8]!=null && e[8].toString().equalsIgnoreCase("C")).collect(Collectors.toList()).size(); 
  }
  
  int total = acttotal + Meetingttl + Miletotal;
 %>
<div class="container-fluid">
		<div class="row">
			<div class="col-md-12">
				<div class="card shadow-nohover">
					<div class="card-header ">  
					<div class="row">
						<h5 class="col-md-8">Action Status Reports</h5>  
							<div class="col-md-4 div-margin" >
							<table>
					   			<tr>
					   				<td>
					   					<label class="control-label td-label" >Project : </label>
					   				</td>
					   				<td class="td-padding">
						   				<form method="post" action="ActionGraph.htm" name="ststusform" id="ststusform">
	                                         <select class="form-control selectdee " name="projectid" id="projectid" required="required"  data-live-search="true"  onchange="this.form.submit()">
	                                            <option value="A" <%if(projectid.equalsIgnoreCase("A")){ %> selected="selected" <%} %>>All</option>	
	              
	                                               <%for(Object[] obj:projectslist){
	                                            	   String projectshortName=(obj[12]!=null)?" ("+obj[12].toString()+") ":"";
	                                            	   %>
														<option value="<%=obj[0] %>" <%if(projectid.equalsIgnoreCase(obj[0].toString())){ %> selected="selected" <%} %>><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()):" - "%> <%= projectshortName!=null?StringEscapeUtils.escapeHtml4(projectshortName):" - " %></option>	
													<%}%>
											</select>	
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
									    </form>			
									</td>
					   			</tr>
					   		</table>
					   	</div>
		   			</div>
					</div>
					<div class="card-body "> 
						<div class="row div-margin-row" >
							<div class="col-md-6 float-right"  align="center">
								<figure class="highcharts-figure">
								<div id="container"></div>
								<div align="center">
									<button onclick="submitForm('M','N');" class="btn btn-sm mn-btn"> <b><%=Miletotal%></b> </button>
			                        <button onclick="submitForm('N','N');" class="btn btn-sm nn-btn"> <b> <%=acttotal%></b> </button>
			                        <button onclick="submitForm('S','N');" class="btn btn-sm sn-btn"> <b><%=Meetingttl%></b> </button>
		                      	</div>
								</figure>
							</div>
							<div class="col-md-6"  >
								<figure class="highcharts-figure">
								  <div id="container1"></div>
								</figure>
							</div>
						</div>
				    </div>
		      </div>
		</div>					
</div>
</div>
<form action="ActionReportSubmit.htm" id="submitform" method="post">
	<input type="hidden" name="Project" id="PROJECTID">
	<input type="hidden" name="Type" id="TYPE">
	<input type="hidden" name="Term" id="STATUS">
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />	
</form>
<script type="text/javascript">
Highcharts.chart('container', {
	  chart: {
	    type: 'column'
	  },
	  title: {
	    text: 'Action Graph ',
	    align: 'center'
	  },
	  xAxis: {
	    categories: ['Action', 'Meeting', 'Milestone']
	  },
	  yAxis: {
	    min: 0,
	    title: {
	      text: 'Count of Action'
	    },
	    stackLabels: {
	      enabled: true,
	      style: {
	        fontWeight: 'bold',
	        color: ( 
	          Highcharts.defaultOptions.title.style &&
	          Highcharts.defaultOptions.title.style.color
	        ) || 'gray',
	        textOutline: 'none'
	      }
	    }
	  },
	  legend: {
	    align: 'left',
	    x: -10,
	    verticalAlign: 'top',
	    y: -10,
	    floating: true,
	    backgroundColor:
	      Highcharts.defaultOptions.legend.backgroundColor || 'white',
	    borderColor: '#CCC',
	    borderWidth: 1,
	    shadow: true
	  },
	  tooltip: {
	    headerFormat: '<b>{point.x}</b><br/>',
	    pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'
	  },
	  plotOptions: {
	    column: {
	      stacking: 'normal',
	      dataLabels: {
	        enabled: true
	      }
	    }
	  },
	  series: [{
	    name: 'Active',
	    data: [<%=Actionactive%>,<%=Meetingactive%>, <%=Milestonesactive%>]
	  }, {
	    name: 'In-progress',
	    data: [<%=ActionInprogress%>, <%=MeetingInprogress%>, <%=MilestonesInprogress%>]
	  }, {
	    name: 'Closed',
	    data: [<%=ActionClose%>, <%=MeetingClose%>, <%=MilestonesClose%>]
	  }]
	});
	
function submitForm(type, status)
{ 
	var project=$("#projectid").val();
   $("#PROJECTID").val(project);
   $("#TYPE").val(type);
   $("#STATUS").val(status);
   document.getElementById('submitform').submit(); 
}
</script>
<%
int actiontaotalperc=0;
int actionactive=0;
int actioninprogress=0;
int actionclosed=0;

if(acttotal>0){    actiontaotalperc=(acttotal*100)/ total;}
if(Actionactive>0){   actionactive=(Actionactive*100)/total; }
if(ActionInprogress>0){ actioninprogress=(ActionInprogress*100)/total;}
if(ActionClose>0){ actionclosed=(ActionClose*100)/total;}

int Meetingtotalperc=0;
int Meetingactiveperc=0;
int Meetinginprogressperc=0;
int Meetingclosedperc=0;

if(Meetingttl>0){    Meetingtotalperc=(Meetingttl*100)/ total;}
if(Meetingactive>0){   Meetingactiveperc=(Meetingactive*100)/total; }
if(MeetingInprogress>0){ Meetinginprogressperc=(MeetingInprogress*100)/total;}
if(MeetingClose>0){ Meetingclosedperc=(MeetingClose*100)/total;}

int Milestonestotalperc=0;
int Milestonesactiveperc=0;
int Milestonesinprogressperc=0;
int Milestonesclosedperc=0;

if(Miletotal>0){    Milestonestotalperc=(Miletotal*100)/ total;}
if(Milestonesactive>0){   Milestonesactiveperc=(Milestonesactive*100)/total; }
if(MilestonesInprogress>0){ Milestonesinprogressperc=(MilestonesInprogress*100)/total;}
if(MilestonesClose>0){ Milestonesclosedperc=(MilestonesClose*100)/total;}
%>
<script type="text/javascript">
var colors = Highcharts.getOptions().colors,
categories = [
  'Action',
  'Meetings',
  'Milestone',
],
data = [
  {
    y: <%=actiontaotalperc%>,
    color: colors[4],
    drilldown: {
      name: 'Action',
      categories: [
        'Active',
        'Inprogress',
        'Closed'
      ],
      data: [
        <%=actionactive%>,
        <%=actioninprogress%>,
        <%=actionclosed%>
      ]
    }
  },
  {
    y: <%=Meetingtotalperc%>,
    color: colors[5],
    drilldown: {
      name: 'Meetings',
      categories: [
        'Active',
        'Inprogress',
        'Closed'
      ],
      data: [
    	  <%=Meetingactiveperc%>,
          <%=Meetinginprogressperc%>,
          <%=Meetingclosedperc%>
      ]
    }
  },
  {
    y: <%=Milestonestotalperc%>,
    color: colors[6],
    drilldown: {
      name: 'Milestone',
      categories: [
        'Active',
        'Inprogress',
        'Closed'
      ],
      data: [
    	  <%=Milestonesactiveperc%>,
          <%=Milestonesinprogressperc%>,
          <%=Milestonesclosedperc%>
      ]
    }
  },
],
browserData = [],
versionsData = [],
i,
j,
dataLen = data.length,
drillDataLen,
brightness;


//Build the data arrays
for (i = 0; i < dataLen; i += 1) {

// add browser data
browserData.push({
  name: categories[i],
  y: data[i].y,
  color: data[i].color
});

// add version data
drillDataLen = data[i].drilldown.data.length;
for (j = 0; j < drillDataLen; j += 1) {
  brightness = 0.2 - (j / drillDataLen) / 5;
  versionsData.push({
    name: data[i].drilldown.categories[j],
    y: data[i].drilldown.data[j],
    color: Highcharts.color(data[i].color).brighten(brightness).get()
  });
}
}

//Create the chart
Highcharts.chart('container1', {
chart: {
  type: 'pie'
},
title: {
  text: 'Action Pie Chart',
  align: 'center'
},
plotOptions: {
  pie: {
    shadow: false,
    center: ['50%', '50%']
  }
},
tooltip: {
  valueSuffix: '%'
},
series: [{
  name: 'Actions',
  data: browserData,
  size: '60%',
  dataLabels: {
    formatter: function () {
      return this.y > 5 ? this.point.name : null;
    },
    color: '#ffffff',
    distance: -30
  }
}, {
  name: 'Actions',
  data: versionsData,
  size: '80%',
  innerSize: '60%',
  dataLabels: {
    formatter: function () {
      // display only if larger than 1
      return this.y > 1|| this.y == 1 ? '<b>' + this.point.name + ':</b> ' +
        this.y + '%' : null;
    }
  },
  id: 'versions'
}],
responsive: {
  rules: [{
    condition: {
      maxWidth: 400
    },
    chartOptions: {
      series: [{
      }, {
        id: 'versions',
        dataLabels: {
          enabled: false
        }
      }]
    }
  }]
}
});

</script>
</body>
</html>