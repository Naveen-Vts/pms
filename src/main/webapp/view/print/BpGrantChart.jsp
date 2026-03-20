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
body{
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
<body>
  <%

 
  
  List<Object[]> MilestoneActivityMain=(List<Object[]>)request.getAttribute("MilestoneActivityMain0");
  List<Object[]> MilestoneActivityA=(List<Object[]>)request.getAttribute("MilestoneActivityA0");
  List<Object[]> MilestoneActivityB=(List<Object[]>)request.getAttribute("MilestoneActivityB0");
  List<Object[]> MilestoneActivityC=(List<Object[]>)request.getAttribute("MilestoneActivityC0");
  List<Object[]> MilestoneActivityD=(List<Object[]>)request.getAttribute("MilestoneActivityD0");
  List<Object[]> MilestoneActivityE=(List<Object[]>)request.getAttribute("MilestoneActivityE0");
  String maxDate = LocalDate.now().plusMonths(6).toString();

String lastEnddate = MilestoneActivityMain!=null && !MilestoneActivityMain.isEmpty()?MilestoneActivityMain.get(0)[9].toString():LocalDate.now().toString();
List<Boolean> isDateMatching =
MilestoneActivityMain != null
        ? MilestoneActivityMain.stream().map(row -> {

            String fromDate = row[4] != null ? row[4].toString() : "";
            String toDate = row[5] != null ? row[5].toString() : "";
            String revisedFromDate = row[6] != null ? row[6].toString() : "";
            String revisedToDate = row[7] != null ? row[7].toString() : "";

            return toDate.equalsIgnoreCase(revisedToDate)
                    && fromDate.equalsIgnoreCase(revisedFromDate);
        }).collect(Collectors.toList())
        : new ArrayList<>();


List<Boolean> isDatePassed =
MilestoneActivityMain != null
        ? MilestoneActivityMain.stream().map(row -> {

            String fromDate = row[4] != null ? row[4].toString() : "";
            String toDate = row[5] != null ? row[5].toString() : "";
            String revisedFromDate = row[6] != null ? row[6].toString() : "";
            String revisedToDate = row[7] != null ? row[7].toString() : "";

            String dateStr = !toDate.isEmpty() ? toDate : toDate;

            if (dateStr.isEmpty()) {
                return false;
            }

            LocalDate compDate = LocalDate.parse(dateStr);
            return compDate.isBefore(LocalDate.now());

        }).collect(Collectors.toList())
        : new ArrayList<>();
 %>
 
<div class="container-fluid">
		<div class="row">
			<div class="col-md-12">
				<div class="card shadow-nohover">
					<div class="card-header ">  

					<div class="row">
						 
				
						
				
						
							
							
			   		</div>	   							

					</div>
						<div class="card-body " style="padding: 10px;"> 
							<div  class="row mb-2">
							<div class="col-md-2" style="text-align: right">
								<label>Interval : &nbsp;&nbsp;&nbsp; </label>
							</div>
							<div align="left" class="col-md-3 justify-content-end">
							<select class="form-control selectdee " name="interval" id="interval" required="required"  data-live-search="true"  style="width:150px !important" >
                                <option value="quarter"> Quarterly </option>
                                <option value="half" >Half-Yearly</option>
                                <option value="year" >Yearly</option>
                            	<option value="month"> Monthly </option>
							</select>
						</div>
							</div>
						
							
								<div class="row" style="margin-bottom: 5px;font-weight: bold;"   >
										<div class="col-md-4"></div>
										<div class="col-md-3"></div>
										<div class="col-md-5">
											<div style="font-weight: bold; " >
												<span style="margin:0px 0px 10px  10px;">Original :&ensp; <span style=" background-color: #455a64;;  padding: 0px 15px; border-radius: 3px;"></span></span>
												<span style="margin:0px 0px 10px  15px;">Ongoing :&ensp; <span style=" background-color: #059212;  padding: 0px 15px;border-radius: 3px;"></span></span>
												<span style="margin:0px 0px 10px  15px;">Revised :&ensp; <span style=" background-color: #F5A623; opacity: 0.5; padding: 0px 15px;border-radius: 3px;"></span></span>
												<span style="margin:0px 0px 10px  15px;">Delay Ongoing :&ensp; <span style=" background-color: #D0021B; padding: 0px 15px;border-radius: 3px;" ></span></span>
												
											</div>
										</div>
									</div>
							<div class="row" >
								
								<div class="col-md-12" style="float: right;" align="center">
							
					   				<div class="flex-container" id="containers" ></div>

                        		</div>
						   	</div>
						
						</div>
					
				</div>
					
		
		</div>
	</div>
</div>

<script>

$('#interval').on('change',function(){
	
	$('#containers').empty();
	var interval = $("#interval").val()
	chartprint('type',interval);
	
})

/* $('#ProjectId').on('change',function(){
	
	$('#myform').submit();
}) */

</script>

									<script>
								      /* anychart.onDocumentReady(function () {  */  
								    	  
																	function chartprint(type,interval){
								   
								    	  var data = [
								    		  <%int count=1; int index =0;
								    		  for(Object[] obj : MilestoneActivityMain){ 
								    			  boolean datePassed = isDatePassed != null 
								    	                     && index < isDatePassed.size() 
								    	                     && Boolean.TRUE.equals(isDatePassed.get(index));
											    	String progressColor = !datePassed ? "#059212" : "#D0021B";
								    		  %>
								    		  
								    		  {
								    		    id: "<%=obj[3]%>",
								    		    name: "<%=obj[2]%>",
								    		    <%if(!obj[9].toString().equalsIgnoreCase("0") && !obj[9].toString().equalsIgnoreCase("1") && !isDateMatching.get(index)){ %>
								    		   	baselineStart: "<%=obj[6]%>",
								    		    baselineEnd: "<%=obj[7]%>", 
								    		    baseline: {fill: "#F5A623 0.5", stroke: "0.0 #F5A623"},
								    		    actualStart: "<%=obj[4]%>",
								    		    actualEnd: "<%=obj[5]%>",
								    		    actual: {fill: "#4A90E2", stroke: "0.8 #4A90E2"},
								    		    baselineProgress: {fill: "<%=progressColor%> 0.8", stroke: "0.8 <%=progressColor%>"},
								    		    baselineProgressValue: "<%= Math.round((int)obj[8])%>%",
								    		    progress: {fill: "#7ED321 0.0", stroke: "0.0 #150e56"},
								    		    progressValue: "<%= Math.round((int)obj[8])%>% ", 
								    		    <%} else{%>
							    		   		<%-- baselineStart: "<%=obj[6]%>",
								    		    baselineEnd: "<%=obj[7]%>",  --%>
								    		    baselineStart: "<%=obj[4]%>",
								    		    baselineEnd: "<%=obj[5]%>", 
								    		    baseline: {fill: "#4A90E2", stroke: "0.8 #4A90E2"},
								    		    baselineProgress: {fill: "<%=progressColor%> 0.8", stroke: "0.8 <%=progressColor%>"},
								    		    baselineProgressValue: "<%= Math.round((int)obj[8])%>%",
								    		    progress: {fill: "#7ED321 0.0", stroke: "0.0 #150e56"},
								    		    progressValue: "<%= Math.round((int)obj[8])%>% ", 
								    		    <%}%>
								    		    rowHeight: "55",
								    		  },
								    		  
								    		  <%
								    		  maxDate = obj[7].toString();
								    		  index++;
								    		  }%>
								    		  ];
								    	  
								    
								    	 
								    		// create a data tree
								    		var treeData = anychart.data.tree(data, "as-tree");
								    		// create a chart
								    		var chart = anychart.ganttProject();
								    		// set the data
								    		chart.data(treeData);   
								        	// set the container id
								        	chart.container("containers");  
								        	
										   <%-- var datemax = '<%= maxDate%>';
									    	
								    	  	datemax = new Date(datemax);
											  
								    	  	datemax.setMonth(datemax.getMonth() + 3);
										
										   	chart.getTimeline().scale().maximum(datemax); --%>
								        	
								        	// initiate drawing the chart
								        	chart.draw();    
								        	// fit elements to the width of the timeline
								        	chart.fitAll();
								        /* ToolTip */
								        chart.getTimeline().tooltip().useHtml(true);  
								        
								        var timeline = chart.getTimeline();

									   // configure labels of elements
									   timeline.elements().labels().fontWeight(600);
									   timeline.elements().labels().fontSize("14px");
									   timeline.elements().labels().fontColor("#333333");

									   
								        chart.getTimeline().tooltip().format(
							        		 function() {
							        		        var actualStart = this.getData("actualStart") ? this.getData("actualStart") : this.getData("baselineStart");
							        		        var actualEnd = this.getData("actualEnd") ? this.getData("actualEnd") : this.getData("baselineEnd");
							        		        var reDate=this.getData("actualStart") ;
							        		   
							        		        var html="";
							        		        if(reDate===undefined){
							        		        	html="";
							        		        	html= "<span style='font-weight:600;font-size:10pt'> Actual : " +
							        		               anychart.format.dateTime(actualStart, 'dd MMM yyyy') + " - " +
							        		               anychart.format.dateTime(actualEnd, 'dd MMM yyyy') + "</span><br>" +
							        		               "Progress: " + this.getData("baselineProgressValue") + "<br>"
							        		        }else{
							        		        	html="";
							        		        html="<span style='font-weight:600;font-size:10pt'> Actual : " +
							        		               anychart.format.dateTime(actualStart, 'dd MMM yyyy') + " - " +
							        		               anychart.format.dateTime(actualEnd, 'dd MMM yyyy') + "</span><br>" +
							        		               "<span style='font-weight:600;font-size:10pt'> Revised : " +
							        		               anychart.format.dateTime(this.getData("baselineStart"), 'dd MMM yyyy') + " - " +
							        		               anychart.format.dateTime(this.getData("baselineEnd"), 'dd MMM yyyy') + "</span><br>" +
							        		               "Progress: " + this.getData("baselineProgressValue") + "<br>"
							        		        }
							        		        
							        		        return html;
							        		    }
								          
								        ); 
								        
								        
								        
								<%--         <%if(ProjectId!=null){
												Object[] ProjectDetail=(Object[])request.getAttribute("ProjectDetails");
												%>

												/* Title */
												var title = chart.title();
												title.enabled(true);
												title.text(" <%=ProjectDetail[2] %> ( <%=ProjectDetail[1] %> ) Gantt Chart");
												title.fontColor("#1976D2");  // Darker blue for better contrast
												title.fontSize(18);
												title.fontWeight(600);
												title.padding(5);
												
												<%} %> --%>
												
												/* Hover */
												chart.rowHoverFill("#FFCC80 0.3");  // Softer orange for hover
												chart.rowSelectedFill("#FFCC80 0.3");
												chart.rowStroke("0.5 #4A90E2");  // Matches Sky Blue
												chart.columnStroke("0.5 #4A90E2");  

												chart.defaultRowHeight(35);
												chart.headerHeight(90);

												/* Hiding the middle column */
												chart.splitterPosition("15.6%");

												var dataGrid = chart.dataGrid();
												dataGrid.rowEvenFill("#E0E0E0 0.3");  // Light gray for contrast
												dataGrid.rowOddFill("#E0E0E0 0.1");
												dataGrid.rowHoverFill("#FFD54F 0.3");  // Light yellow hover
												dataGrid.rowSelectedFill("#FFD54F 0.3");
												dataGrid.columnStroke("2 #4A90E2");  
												dataGrid.headerFill("#4A90E2 0.2");

												/* Title */
												var column_1 = chart.dataGrid().column(1);
												column_1.labels().fontWeight(900);
												column_1.labels().useHtml(true);
												column_1.labels().fontSize("12px");
												column_1.labels().fontColor("#0D47A1");  // Dark blue for labels

												var column_2 = chart.dataGrid().column(1);
												column_2.title().text("Activity");
												column_2.title().fontColor("#2C3E50");  // Deep blue title
												column_2.title().fontWeight(600);

												chart.dataGrid().column(0).width(25);
												chart.dataGrid().column(1).width(250);
												chart.dataGrid().tooltip().useHtml(true);
    
								        
								     	
								     	
								     	 /* Set width of column */
								     	/* chart.dataGrid().column(0).setColumnFormat(
								     		   
								     		    {
								     		      formatter: function(value) {
								     		        return value.toUpperCase();
								     		      },
								     		      textStyle: {fontColor: "#055C9D",fontWeight:"600"},
								     		      width: 10
								     		    }
								     		);   */
								     	
										/* chart.fitAll();
								     	chart.zoomTo("month", 3, "first-date"); */
								     	
								     	/* chart.getTimeline().scale().minimum("2019-01-01");
								     	chart.getTimeline().scale().maximum("2021-07-15"); */
								     	
								     	//chart.getTimeline().scale().zoomLevels([["month", "quarter","year"]]);
								     	
								     	if(interval==="year"){
								     		/* Yearly */
									     	chart.getTimeline().scale().zoomLevels([["year"]]);
									     	var header = chart.getTimeline().header();
									     	header.level(2).format("{%value}-{%endValue}");
									     	header.level(1).format("{%value}-{%endValue}"); 
								     	}
								     	
								     	if(interval==="half"){
								     		/* Half-yearly */
									     	chart.getTimeline().scale().zoomLevels([["semester", "year"]]);
									     	var header = chart.getTimeline().header();
									     	/* header.level(2).format("{%value}-{%endValue}"); */
									     	header.level(2).format("{%value}-{%endValue}");
									     	header.level(0).format(function() {
									     			var duration = '';
									     			if(this.value=='Q1')
									     				duration='H1';
									     			if(this.value=='Q3')
									     				duration='H2'
									     		  return duration;
									     		});
								     	}
								     	
								     	if(interval==="quarter"){
								     		/* Quarterly */
									     	chart.getTimeline().scale().zoomLevels([["quarter", "semester","year"]]);
									     	var header = chart.getTimeline().header();
									     	header.level(1).format(function() {
								     			var duration = '';
								     			if(this.value=='Q1')
								     				duration='H1';
								     			if(this.value=='Q3')
								     				duration='H2'
								     		  return duration;
								     		});
								     		
								     		
								     	}
								     	
								     	if(interval==="month"){
								     		/* Monthly */
									     	chart.getTimeline().scale().zoomLevels([["month", "quarter","year"]]);
								     	}
								     	
								     	else if(interval===""){

								     		/* Quarterly */
									     	chart.getTimeline().scale().zoomLevels([["quarter", "semester","year"]]);
									     	var header = chart.getTimeline().header();
									     	header.level(1).format(function() {
								     			
								     			var duration = '';
								     		
								     			if(this.value=='Q1')
								     				duration='H1';
								     			if(this.value=='Q3')
								     				duration='H2'
			
								     		  return duration;
								     		});
								     		
								     	}
								     	
								     	
								     	
								     	/* chart.getTimeline().scale().fiscalYearStartMonth(4); */
								     	
								     	/* Header */
								     	var header = chart.getTimeline().header();
								     	header.level(0).fill("#4A90E2 0.2");  // Softer Sky Blue for clarity
								     	header.level(0).stroke("#4A90E2");   // Matches Sky Blue
								     	header.level(0).fontColor("#0D47A1");  // Darker blue for better readability
								     	header.level(0).fontWeight(600);
								     	
								     	/* Marker */
								     	var marker_1 = chart.getTimeline().lineMarker(0);
								     	marker_1.value("current");
								     	marker_1.stroke("2 #FF6F00");  // Bright Orange for better visibility
								     	
								     	/* Progress */
								     	var timeline = chart.getTimeline();
								     	
								     	timeline.tasks().labels().useHtml(true);
								     	timeline.tasks().labels().format(function() {
								     		if (this.progress == 1) {
								     		    return "<span style='color:#388E3C;font-weight:bold;font-family:Lato;'>Completed</span>";  // Green for completion
								     		  } else {
								     		    return "<span style='color:#D32F2F;font-weight:bold;font-family:Lato;'></span>";  // Red for pending
								     		  }
								     	});
								     	
								     	
								     	
								    // calculate height
								     	var traverser = treeData.getTraverser();
								        var itemSum = 0;
								        var rowHeight = chart.defaultRowHeight();
								        while (traverser.advance()){
								           if (traverser.get('rowHeight')) {
								          itemSum += traverser.get('rowHeight');
								        } else {
								        	itemSum += rowHeight;
								        }
								        if (chart.rowStroke().thickness != null) {
								        	itemSum += chart.rowStroke().thickness;
								        } else {
								          itemSum += 1;
								        }
								        }
								        itemSum += chart.headerHeight();
								        
								        //customize printing
								        var menu = chart.contextMenu();
								        
								        // To download and stuff 
								        
								       /*  menu.itemsFormatter(function(items) {
								          items['print-chart'].action = 
								        	  
								        	  function() {
								            document.getElementById('containers').style.height = String(itemSum) + 'px';
								            setTimeout(function() {
								              chart.print();
								              setTimeout(function() {
								                document.getElementById('containers').style.height = '100%';
								              },5000);
								            },1000);
								          }
								          return items;
								        });   */
								        
								        
								       // to print

									   	if(type==="print"){
  		
									   		anychart.onDocumentReady(function () { 
									   		
									            document.getElementById('containers').style.height = String(itemSum) + 'px';
									            setTimeout(function() {
									            
									            //  anychart.exports.server("E://pmsuploadedfiles");	
									              //chart.saveAsPng(360, 500, 0.3, 'PngChart'); 
									              
									        /*       chart.saveAsJpg({"width": 360,
										           "height": 500,
										           "quality": 0.3,
										           "forceTransparentWhite": false,
										           "filename": "My Chart JPG"}); */
									              
									              
									            // chart.saveAsPdf('a4', true, 100, 100, 'PdfChart');
									              chart.print('A5', true);
									              chart.zoomTo(951350400000, 954201600000);
									              setTimeout(function() {
									                //document.getElementById('containers').style.height = '100%';
									              },3000);
									            },1000);
									          }); 
										    
									   	}

								
								        
								        
								        
								        
								       
								      } 
								      
								   /*    });  */
								      
								      $( document ).ready(function(){
								    	  
								    	  chartprint('type','');
 							  var interval = $("#interval").val();
								    	  
								    	
								      })
								      
								      
								      function ChartPrint(){
									   		
								    	  var interval = $("#interval").val();
								    	  
								
								    	  
								    	  $('#containers').empty();
								    	  chartprint('print',interval);
								     }
								     
								    </script>
																	
                        		


</body>
</html>