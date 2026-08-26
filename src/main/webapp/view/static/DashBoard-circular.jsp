<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@page import="java.util.*"%>
    <%@page import="java.math.BigDecimal"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
</head>
<body>
<div class="card detailscard" id="meetingCard"></div>
<div class="card detailscard" id="milestoneCard"> </div>
<div class="card detailscard" id="actionCard"></div>
<div class="card detailscard" id="riskCard"></div> 
<div class="card detailscard"id="FinanceCard"></div>
 
		  <script type="text/javascript">
		  $( document ).ready(function() {

			  $.ajax({
				  type:'GET',
				  url:'DasboardCircularProgress.htm',
				  datatype:'json',
				  data:{
				
				  },
					 success:function(result){
						 var ajaxresult = JSON.parse(result);
					
						 var pmrcprogress = "border-danger"
						 var ebprogress = "border-danger"
						 var msprogress = "border-danger"
						 var actionprogress = "border-danger"
						 var riskprogress = "border-danger"
						 /* PMRB progress */

							 if(Number(ajaxresult[29])>25 && Number(ajaxresult[29])<=50){
				 				 pmrcprogress = "border-orange";
			  					}else if (Number(ajaxresult[29])>50 && Number(ajaxresult[29])<=75){
			  						 pmrcprogress = "border-warning";
			  					}else if(Number(ajaxresult[29])>75 ){
			  						pmrcprogress = "border-success";
			  					}
						 /*  */
							if(Number(ajaxresult[31])>25 && Number(ajaxresult[31])<=50){
							ebprogress = "border-orange";
				  			}else if (Number(ajaxresult[31])>50 && Number(ajaxresult[31])<=75){
				  				ebprogress = "border-warning";
				  				}else if(Number(ajaxresult[31])>75 && Number(ajaxresult[31])<=100){
				  				ebprogress = "border-success";
				  					}
						 /*  */
							 if(Number(ajaxresult[10])>25 && Number(ajaxresult[10])<=50){
								 msprogress = "border-orange";
			  					}else if (Number(ajaxresult[10])>50 && Number(ajaxresult[10])<=75){
			  						msprogress = "border-warning";
			  					}else if(Number(ajaxresult[10])>75 && Number(ajaxresult[10])<=100){
			  						msprogress = "border-success";
			  					}
						 /*  */
						
							 if(Number(ajaxresult[37])>25 && Number(ajaxresult[37])<=50){
								 actionprogress = "border-orange";
			  					}else if (Number(ajaxresult[37])>50 && Number(ajaxresult[37])<=75){
			  						actionprogress = "border-warning";
			  					}else if(Number(ajaxresult[37])>75 ){
			  						actionprogress = "border-success";
			  					}
						 
						 /*  */
							 if(Number(ajaxresult[39])>25 && Number(ajaxresult[39])<=50){
								 riskprogress = "border-orange";
			  					}else if (Number(ajaxresult[39])>50 && Number(ajaxresult[39])<=75){
			  						riskprogress = "border-warning";
			  					}else if(Number(ajaxresult[39])>75 && Number(ajaxresult[39])<=100){
			  						riskprogress = "border-success";
			  					}
						 
		/*  html1  */
  				html1='<div class="card-body">'+
		      			'<h5 class="card-title"><img src="view/images/discuss.png" /> Meeting</h5><hr>'+
		      			'<div class="row"><div class="col-md-6 circular-progress">'+
		      		    '<div class="progress " data-value="'+ajaxresult[29]+'"><span class="progress-left">'+
			          	'<span class="progress-bar '+pmrcprogress+'"></span></span>'+        
			         	'<span class="progress-right"> <span class="progress-bar  '+pmrcprogress+'"></span></span>'+
			          	'<div class="progress-value w-100 h-100 rounded-circle d-flex align-items-center justify-content-center">'+
			            '<div class="h4 font-weight-bold" id="pmrcprogress" >'+ajaxresult[29]+'%</div></div></div><hr class="clx-59">'+
			        	'<table class="countstable clx-60">'+
			        	'<tr>'+
			        	'<th class="clx-61">PMRB </th>	</tr>'+
				        '<tr><td class="clx-62" id="meetingsvaluepmrc"  data-toggle="tooltip" title="Held / To be Held / Total to be Held" >'+
			        	'<span>'+ajaxresult[0]+'/'+ajaxresult[2]+'/'+ajaxresult[46]+'</span></td></tr></table></div>'+
		      			'<div class="col-md-6 circular-progress">'+
		      			'<div class="progress "  data-value="'+ajaxresult[31]+'">'+
			          	'<span class="progress-left"><span class="progress-bar '+ebprogress+'"></span></span>'+
			          	'<span class="progress-right"><span class="progress-bar '+ebprogress+'"></span></span>'+
			         	'<div class="progress-value w-100 h-100 rounded-circle d-flex align-items-center justify-content-center">'+
			            '<div class="h4 font-weight-bold" >'+ajaxresult[31]+'%</div> </div></div><hr class="clx-59">'+
			        	'<table class="countstable" class="clx-60"><tr><th class="clx-61">EB </th></tr>'+
				        '<tr><td class="clx-62" id="meetingsvalueeb"  data-toggle="tooltip" title="Held / To be Held / Total to be Held"  ><span>'+ajaxresult[3]+'/'+ajaxresult[5]+'/'+ajaxresult[47]+'</span></td></tr></table></div></div> </div>';
						$('#meetingCard').html(html1);		
				
				/* html2 */	
				html2='<div class="card-body"><h5 class="card-title"><img src="view/images/goal.png" /> Milestone</h5><hr>'+
				      '<div class="row"><div class="col-md-6 circular-progress">'+
				      '<div class="progress " data-value="'+ajaxresult[10]+'">'+
					  '<span class="progress-left "><span class="progress-bar '+msprogress+'"></span></span>'+
					  '<span class="progress-right"><span class="progress-bar '+msprogress+'"></span></span>'+
					  '<div class="progress-value w-100 h-100 rounded-circle d-flex align-items-center justify-content-center">'+
					  '<div class="h4 font-weight-bold"><span id="milestonepercentage">'+ajaxresult[10]+'%</span></div></div></div></div>'+
				      '<div class="col-md-6"><div class="bigcount"><h1>'+ajaxresult[8]+'</h1>'+
				      '<p class="textfont ul1" ><span class="green legend-shadow">&#x220E;</span> &nbsp;Completed</p></div>'+
				      '<div class="bigcount"><h4>'+ajaxresult[9]+'</h4>'+
				      '<p class="textfont">Total</p></div></div></div></div>'+
				      '<table class="countstable card-deck-table clx-63">'+
					  ' <thead class="clx-64"><tr>'+
					  '<td class="clx-65" data-toggle="tooltip" title="Delayed" >'+
					  '<span class="yellow">&#x220E;</span>'+
					  '<span class="clx-66">Delayed&nbsp;</span>'+ajaxresult[7]+'</td>'+
					  '<td data-toggle="tooltip" title="Pending"  >'+
					  '<span class="red">&#x220E;</span>'+
					  '<span class="clx-67">Pending&nbsp;</span>'+ajaxresult[6]+'</td></tr></thead></table>';
					  $('#milestoneCard').html(html2);	
				/*html3  */
				html3='<div class="card-body"><h5 class="card-title"><img src="view/images/action1.png" /> Action</h5><hr>'+
				      '<div class="row"><div class="col-md-6 circular-progress"><div class="progress" data-value="'+ajaxresult[37]+'">'+
					  '<span class="progress-left"> <span class="progress-bar '+actionprogress+'"></span></span>'+
					  '<span class="progress-right"><span class="progress-bar '+actionprogress+'"></span></span>'+
					  '<div class="progress-value w-100 h-100 rounded-circle d-flex align-items-center justify-content-center">'+
					  '<div class="h4 font-weight-bold">'+ajaxresult[37]+'%</div></div></div></div>'+
				      '<div class="col-md-6"><div class="bigcount"><h1>'+ajaxresult[14]+'</h1><p class="textfont clx-68"><span class="green legend-shadow">&#x220E;</span> &nbsp;Completed</p></div>'+
				      '<div class="bigcount"><h4>'+ajaxresult[15]+'</h4><p class="textfont">Total</p></div></div></div></div>'+
				      '<table class="countstable card-deck-table clx-63" >'+
					  '<thead class="clx-64"><tr>'+
					  '<td class="clx-65" data-toggle="tooltip" title="Delayed" >'+
					  '<span class="yellow">&#x220E;</span>'+
					  '<span class="clx-66">Delayed&nbsp;</span>'+ajaxresult[13]+'</td>'+
					  '<td class="clx-65" data-toggle="tooltip" title="Forwarded" >'+
					  '<span class="blue">&#x220E;</span>'+
					  '<span class="clx-67">Forward &nbsp;</span>'+ajaxresult[12]+'</td>'+
					  '<td data-toggle="tooltip" title="Pending" ><span class="red">&#x220E;</span><span class="clx-67">Pending&nbsp;</span>'+ajaxresult[11]+'</td></tr></thead></table>';
				 	$('#actionCard').html(html3);
				 	/*  html4*/
				 	html4 ='<div class="card-body"><h5 class="card-title"><img src="view/images/risk 1.png" /> Risk</h5><hr><div class="row"><div class="col-md-6 circular-progress">'+
					      	'<div class="progress " data-value="'+ajaxresult[39]+'">'+
						    '<span class="progress-left"><span class="progress-bar '+riskprogress+'"></span></span>'+
						    '<span class="progress-right"><span class="progress-bar '+riskprogress+'"></span></span>'+
						    '<div class="progress-value w-100 h-100 rounded-circle d-flex align-items-center justify-content-center"><div class="h4 font-weight-bold">'+ajaxresult[39]+'%</div></div></div></div>'+
					      	'<div class="col-md-6"><div class="bigcount"><h1>'+ajaxresult[16]+'</h1><p class="textfont clx-68"><span class="green legend-shadow">&#x220E;</span> &nbsp;Completed</p></div>'+
					      	'<div class="bigcount"><h4>'+ajaxresult[18]+'</h4><p class="textfont">Total</p></div></div></div></div>'+
					    	'<table class="countstable card-deck-table clx-63">'+
							'<thead class="clx-64"><tr><td class="clx-65" data-toggle="tooltip" title="Delayed" ><span class="yellow">&#x220E;</span><span class="clx-67">Delayed&nbsp;</span>&nbsp;0</td>'+
							'<td data-toggle="tooltip" title="Pending"><span class="red">&#x220E;</span><span class="clx-68">Pending&nbsp;</span>'+ajaxresult[17]+'</td></tr></thead></table>'
					$('#riskCard').html(html4);
					
					  $(".progress").each(function() {
					      var value = $(this).attr('data-value');
					      var left = $(this).find('.progress-left .progress-bar');
					      var right = $(this).find('.progress-right .progress-bar');

					      if (value > 0) {
					        if (value <= 50) {
					          right.css('transform', 'rotate(' + percentageToDegrees(value) + 'deg)');
					        } else {
					          right.css('transform', 'rotate(180deg)');
					          left.css('transform', 'rotate(' + percentageToDegrees(value - 50) + 'deg)');
					        }
					      }
					    });

					    function percentageToDegrees(percentage) {
					      return percentage / 100 * 360;
					    } 
					 }
			  })
			  /* Finance ajax  */
			  $.ajax({
				  type:'GET',
				  url:'FinaceCardWiseData.htm',
				  datatype:'json',
				  data:{
					
				  },
				  success:function(result){
					  var ajaxresult = JSON.parse(result);
					 
						 var revenueBal = Number(ajaxresult[0][3])-Number(ajaxresult[0][4])
						 var capitalBal = Number(ajaxresult[1][3])-Number(ajaxresult[1][4])
						 var otherBal   = Number(ajaxresult[2][3])-Number(ajaxresult[2][4])
						 var totalBal   = revenueBal+capitalBal+otherBal
						 var totalSanc  = Number(ajaxresult[0][3])+Number(ajaxresult[1][3])+Number(ajaxresult[2][3])
						 var totalExp   = Number(ajaxresult[0][4])+Number(ajaxresult[1][4])+Number(ajaxresult[2][4])
						 html5='<div class="card-body"><div class="clx-69"><h5 class="card-title" class="clx-70"><img src="view/images/rupee.png" /> Finance </h5>'+
						 		'<form action="ProjectHoaUpdate.htm" method="get"><button type="submit" class="btn btn4 btn-sm clx-71"  data-toggle="tooltip" title="Finance Refresh"><i class="fa fa-refresh" aria-hidden="true"></i></button></h6> </form></div><hr class="clx-72">'+
								'<table class="table financetable " ><thead><tr><th scope="col">(In &#8377; k Cr)</th><th scope="col">Sanc</th><th scope="col">Exp</th><th scope="col">Bal</th></tr></thead>'+
						  		'<tbody>'+
						  		'<tr>'+
						        '<th scope="row">Revenue</th>'+
						      	'<td><span class="cl-g">&#8377;</span>'+Number(ajaxresult[0][3]).toFixed(2)+'</td>'+
						      	'<td><span class="cl-g">&#8377;</span> '+Number(ajaxresult[0][4]).toFixed(2)+'</td>'+
						      	'<td><span class="cl-g">&#8377;</span> '+revenueBal.toFixed(2)+'</td>'+
						    	'</tr>'+
						    	'<tr>'+
						    	'<th scope="row">Capital</th>'+
						    	'<td><span class="cl-g">&#8377;</span> '+Number(ajaxresult[1][3]).toFixed(2)+'</td>'+
						    	'<td><span class="cl-g">&#8377;</span> '+Number(ajaxresult[1][4]).toFixed(2)+'</td>'+
						    	'<td><span class="cl-g">&#8377;</span> '+capitalBal.toFixed(2)+'</td>'+
						    	'</tr>'+
						    	'<tr>'+
						     	'<th scope="row">Others</th>'+
						      	'<td><span class="cl-g">&#8377;</span> '+Number(ajaxresult[2][3]).toFixed(2)+'</td>'+
						      	'<td><span class="cl-g">&#8377;</span> '+Number(ajaxresult[2][4]).toFixed(2)+'</td>'+
						      	'<td><span class="cl-g">&#8377;</span>'+otherBal.toFixed(2)+' </td>'+
						    	'</tr> '+
						    	'<tr>'+
						      	'<th scope="row">Total</th>'+
						      	'<td><span class="cl-g">&#8377;</span> '+totalSanc.toFixed(2)+'</td>'+
						      	'<td><span class="cl-g">&#8377;</span> '+totalExp.toFixed(2)+'</td>'+
						      	'<td><span class="cl-g">&#8377;</span> '+totalBal.toFixed(2)+'</td>'+
						 		'</tr> </tbody></table></div>';
						 		
						 $('#FinanceCard').html(html5);
						 
						 $("[data-toggle='tooltip']").tooltip({
						      animated: 'fade',
						      position: 'top',
						      html: true,
						      boundary: 'window'
						    });
				  }
			  })
			  
			  
			  
			});
		 
		  
		  </script>
</body>
</html>