<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="java.util.stream.Collector"%>
<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat"%>
    <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
    
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/js/excel.js" var="excel" />
<script src="${excel}"></script>
<spring:url value="/resources/css/projectModule/projectMainList.css" var="projectMainList" />
<link href="${projectMainList}" rel="stylesheet" />
<title>PROJECT INT  LIST</title>

</head>
<body>
<%SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
List<Object[]> ProjectMainList=(List<Object[]>) request.getAttribute("ProjectMainList");
String Onboarding = (String)request.getAttribute("Onboarding");
DecimalFormat df=new DecimalFormat("0.00");
NFormatConvertion nfc=new NFormatConvertion();

String logintype = (String)session.getAttribute("LoginType");
Long empId = (Long)session.getAttribute("EmpId");
if(logintype.equalsIgnoreCase("P")){
	ProjectMainList= ProjectMainList.stream()
			.filter(e->e[14].toString().equalsIgnoreCase(empId+""))
			.collect(Collectors.toList());
}

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


	
<br>	
	
<div class="container-fluid">		
<div class="col-md-12">

 <div class="card shadow-nohover" >
<div class="card-header">
<div class="row">
	<div class="col-md-3"><h4><b>Project Main List</b></h4></div>
	
	<div class="col-md-9" align="right">
	 <%if(Onboarding!=null && "Yes".equalsIgnoreCase(Onboarding)){%>
				   <form action="ProjectMasterExcelUpload.htm" method="post" enctype="multipart/form-data">
					  		<table>
						  		<tr>
								  	<td align="left"><h6>Download Excel : &nbsp;<button formaction="ProjectMasterExcelUpload.htm" formmethod="post" formnovalidate="formnovalidate" name="Action" value="GenerateExcel"><i class="fa fa-file-excel-o text-success" aria-hidden="true"></i></button></h6></td>
									<td align="right"><h6>&nbsp;&nbsp;&nbsp;&nbsp;	Upload Excel :&nbsp;&nbsp;&nbsp;&nbsp;
									  <input type="file" id="excel_file" name="filename" required="required"  accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"></h6></td>						
							  		
							    </tr>
					  		</table>	
					     <input type="hidden" name="${_csrf.parameterName}"value="${_csrf.token}" />
					<div class="modal fade" id="exampleModalLong"  tabindex="-1" role="dialog" aria-labelledby="exampleModalLongTitle" aria-hidden="true" >
					  <div class="modal-dialog modal-xl" role="document">
					    <div class="modal-content"  >
					      <div class="modal-header">
					        <h5 class="modal-title" id="exampleModalLongTitle">Project Main Details</h5>
					        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
					          <span aria-hidden="true">&times;</span>
					        </button>
					      </div>
					      <div class="modal-body cs-modal">
					             <table class="table table-bordered table-hover table-striped table-condensed" id="myTable1"> </table>
					      </div>
					      <div class="modal-footer" align="center">
					       	    <div align="center">
					        			<button type="submit" onclick="return confirm('Are you sure to submit?')"  class="btn btn-sm add" name="Action" value="UploadExcel"> Upload</button>
					      		</div>
					       </div>
					    </div>
					  </div>
					</div>
					</form> 
			<%}%>
					</div>
					</div>
  </div>
<div class="card-body"> 
    <form action="ProjectMainSubmit.htm" method="POST" name="frm1" >
    <div class="row mt-20">
      <div class="col-md-12">
 <div class="table-responsive">
	   <table class="table table-bordered table-hover table-striped table-condensed " id="myTable"> 
	   <thead class="text-center">
	   <tr>
	   <th class="w-5">Select</th>
			<th>SN</th>
			<th class="text-nowrap">Project Type</th>
			<th class="text-nowrap">Project Code</th>
			<th class="text-nowrap w-25">Project Name</th>
			<th class="text-nowrap w-10">Sanc Date</th>
			<th class="w-124" >Sanc Cost(&#8377; Lakh)</th>
			<th class="w-10">PDC</th>
			<!-- <th>RevNo</th> -->
	  </tr>
	   </thead> 
    <tbody>
    
    
	 <%int count=1;
	 for(Object[] obj:ProjectMainList){ %>
<tr>
<td align="center"><input type="radio" name="ProjectMainId" value="<%=obj[0] %>" ></td>
<td><%=count %></td>
<td><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></td>
<td align="center"><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()): " - " %></td>
<td ><%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()): " - "%></td>
<%-- <td ><%=projectDescription %></td> --%>
<%-- <td ><%=unitCode %></td> --%>

<%

 %>
<td><%=sdf.format(obj[8]) %></td>
<%DecimalFormat df1 = new DecimalFormat( "################.00"); 
String v = df1.format((Double.valueOf(obj[9].toString()).doubleValue()/100000 )); 
NFormatConvertion nfc1=new NFormatConvertion();
%>
<td ><%=v%></td>
<%

 %>

<td class="text-nowrap"><%=sdf.format(obj[10]) %></td>

<%-- <td ><%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()): " - "%></td> --%>
</tr>

<%count++;} %>
</tbody>
</table>
<table align="center">
<tr>
<td> <button name="action" class="btn btn-success btn-sm add" type="submit" value="add" >ADD</button>&nbsp;&nbsp;</td>
<td> <button name="action" class="btn btn-warning btn-sm edit" type="submit" value="edit" Onclick="Edit(frm1)">EDIT</button>&nbsp;&nbsp;</td>
<td>  <%if(Onboarding!=null && "Yes".equalsIgnoreCase(Onboarding)){%>
				<a class="btn btn-info btn-sm  back"   href="OnBoarding.htm">Back</a>
				<%}else{%>
				<a class="btn btn-info btn-sm  back"   href="MainDashBoard.htm">Back</a>
	 <%}%>&nbsp;&nbsp;
</td>
<!-- <td><button name="action" class="btn btn-warning" type="submit" value="revise" Onclick="Edit(frm1)">Revision</button>&nbsp;&nbsp;</td>
<td><button name="action" class="btn btn-danger" type="submit" value="close" Onclick="Delete(frm1)">Close</button>&nbsp;&nbsp;</td>
<td><button class="btn btn-primary" type="submit" formaction="UploadAttachmentSanction?act=view" Onclick="Edit(frm1)">Attach</button>&nbsp;&nbsp;</td>
<td><button name="action" class="btn btn-info" type="submit" value="revisionDetails" Onclick="Edit(frm1)">View</button>&nbsp;&nbsp;</td></tr> -->

</table>
 	
</div>
</div>
</div>





 	 						
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
 	</form>
</div>


</div>
</div>
	
</div>	
	
	
	
	
<script type="text/javascript">

function Edit(myfrm){
	
	 var fields = $("input[name='ProjectMainId']").serializeArray();

	  if (fields.length === 0){
		  bootbox.alert("Please Select One Project Main Record");
	      event.preventDefault();
	      return false;
	}
		  return true;
}

$(function () {
	  $('[data-toggle="tooltip"]').tooltip()
	})


function Prints(myfrm){
	 var fields = $("input[name='btSelectItem']").serializeArray();
	  if (fields.length === 0){
		  	myalert();
	 		event.preventDefault();
			return false;
		}
		  return true;	
}

$(document).ready(function(){
	  $("#myTable").DataTable({
	 "lengthMenu": [  5,10,25, 50, 75, 100 ],
	 "pagingType": "simple"
	
});
});
  
$(document).ready(function(){
	  $("#myTable1").DataTable({
	 "lengthMenu": [  5,10,25, 50, 75, 100 ],
	 "pagingType": "simple"
	
});
});
</script>
 <script type="text/javascript">
const excel_file = document.getElementById('excel_file');

excel_file.addEventListener('change', (event) => {
    var reader = new FileReader();

    reader.readAsArrayBuffer(event.target.files[0]);

    reader.onload = function(event){

        var data = new Uint8Array(reader.result);

        var work_book = XLSX.read(data, {type:'array'});

        var sheet_name = work_book.SheetNames;

        var sheet_data = XLSX.utils.sheet_to_json(work_book.Sheets[sheet_name[0]], {header:1});  
    	
    	var checkExcel=0;
    	var procode=[];
    	var projcode=[];
    	var proname=[];
    	var projectname=[];
    	var prodesc=[];
    	var projectDescription=[];
    	var unitcode=[];
    	var projectunitcode=[];
    	var pslno=[];
    	var letterNo=[];
    	var sancost=[];
    	var totalsancost=[];
    	var nodalparticipat1=[];
    	var nodalparticipat2=[];
    	var Scope1=[];
    	var Scope2=[];
    	var Objective1=[];
    	var Objective2=[];
    	var Deliverable1=[];
    	var Deliverable2=[];
    	var costvalidation=[];
    	var sanccost=[];
    	var sanccostFe1=[];
    	var sanccostFe2=[];
    	var sanccostFe3=[];
        if(sheet_data.length > 0)
        {        
        var table_output ='<thead> <tr > <th>SNo</th> <th>LabCode</th> <th>Code</th> <th>Name</th><th>proj No</th> <th>Unit Code</th> <th>Sanc Letter No</th> <th>Total Sanc Cost</th> <th>Sanc Cost FE</th> <th>Sanc Cost RE</th> <th>Nodal & Participating Lab</th> <th >Scope</th> <th >Objective</th> <th>Deliverable</th></tr> </thead><tbody>'
        	
            for(var row = 0; row < sheet_data.length; row++)
            {            	
            	  table_output += ' <tr> ';
             	
            	  if(row>0){table_output += '<td>'+ row +'</td>';}
                for(var cell = 0; cell < 12; cell++)
                {
                	if(row==0){
                		if(cell==1 && "Project Code" != sheet_data[row][cell]){  checkExcel++;}
        				if(cell==2 && "Project Name" != sheet_data[row][cell]){  checkExcel++;}
        				if(cell==3 && "Project No"   != sheet_data[row][cell]){  checkExcel++;}
        				if(cell==4 && "Project Unit Code" != sheet_data[row][cell]){  checkExcel++;}
        			}
                	
                	if(row>0 && cell==1){
                		var val=''+sheet_data[row][cell]+'';
                		if(val.length>19){ procode.push(row);}
                		if(val=='' ||val=='undefined'){projcode.push(row); }
                	}
                	if(row>0 && cell==2){
                		var val=''+sheet_data[row][cell]+'';
                		if(val.length>255){ proname.push(row);}
                		if(val=='' ||val=='undefined'){projectname.push(row); }
                	}
                	if(row>0 && cell==3){
                		var val=''+sheet_data[row][cell]+'';
                		if(val.length>95){ prodesc.push(row);}
                		if(val=='' ||val=='undefined'){projectDescription.push(row); }
                	}
                	if(row>0 && cell==4){
                		var val=''+sheet_data[row][cell]+'';
                		if(val.length>19){ unitcode.push(row);}
                		if(val=='' ||val=='undefined'){projectunitcode.push(row); }
                	}
                	if(row>0 && cell==5){
                		var val=''+sheet_data[row][cell]+'';
                		if(val.length>99){ pslno.push(row);}
                		if(val=='' ||val=='undefined'){letterNo.push(row); }
                	}
                	if(row>0 && cell==6){
                		var val=''+sheet_data[row][cell]+'';
                 		if(!/^\d+$/.test(val) ){ sanccost.push(row);}	
                		if(val.length>12){ sancost.push(row);}
                		if(val=='' ||val=='undefined'){totalsancost.push(row); }
                	}
                	if(row>0 && cell==7){
                		var val=''+sheet_data[row][cell]+'';
                 		if(!/^\d+$/.test(val) ){ sanccostFe1.push(row);}	
                		if(val.length>12){ sanccostFe2.push(row);}
                		if(val=='' ||val=='undefined'){sanccostFe3.push(row); }
                	}
                	if(row>0 && cell==8){
                		var val=''+sheet_data[row][cell]+'';
                		if(val.length>250){ nodalparticipat1.push(row);}
                		if(val=='' ||val=='undefined'){nodalparticipat2.push(row); }
                	}if(row>0 && cell==9){
                		var val=''+sheet_data[row][cell]+'';
                		if(val.length>5000){ Scope1.push(row);}
                		if(val=='' ||val=='undefined'){Scope2.push(row); }
                	}if(row>0 && cell==10){
                		var val=''+sheet_data[row][cell]+'';
                		if(val.length>5000){ Objective1.push(row);}
                		if(val=='' ||val=='undefined'){Objective2.push(row); }
                	}if(row>0 && cell==11){
                		var val=''+sheet_data[row][cell]+'';
                		if(val.length>5000){ Deliverable1.push(row);}
                		if(val=='' ||val=='undefined'){Deliverable2.push(row); }
                	}
                	
                	
                	if(row>0 && cell==0){
                		table_output += '<td>'+'<%=session.getAttribute("labcode")%>'+'</td>';
                	}
                	if(row>0 && cell==7){
                		var totalcost  = sheet_data[row][6];
                    	var sanctionfe = sheet_data[row][7];
                    	var sanctionRE = totalcost-sanctionfe;
                    	table_output += '<td>'+ sheet_data[row][7] +'</td>';
                		table_output += '<td>'+ sanctionRE +'</td>';
                	}
                	if(row>0 && cell>0 && cell!=7){
                		table_output += '<td>'+sheet_data[row][cell]+'</td>';
                	}
                }
                if(row>0){
                	var totalcost = sheet_data[row][6];
                	var sanctionfe= sheet_data[row][7];
                	if(totalcost<sanctionfe){
                		costvalidation.push(row);
                	}
                }
                
                table_output += '</tr>';
             }
            table_output += ' <tbody>';
            document.getElementById('myTable1').innerHTML = table_output;
             
             var EmployeeNojsArray = [<%int i=0; for (Object[] obj:ProjectMainList) { %>"<%= obj[3] %>"<%= i + 1 < ProjectMainList.size() ? ",":"" %><% } %>];
                    
             var pcode=[];
             for (var i in sheet_data) {
          	  pcode.push(sheet_data[i][1]+'')
          	}
            
             const duplicates = pcode.filter((item, index) => index !== pcode.indexOf(item));  
           
             const indexval = []             
             for(var i in duplicates){
            	 indexval.push(pcode.indexOf(duplicates[i]))
             }
             
             var dbDuplicate = [];                      
             EmployeeNojsArray.forEach(function(item){
            	  var isPresent = pcode.indexOf(item);
            	  if(isPresent !== -1){
            		  dbDuplicate.push(isPresent); 
            	  }
            	})
            	
            	var msg='';
            	if(indexval.length>0){
            		msg+="Duplicate Project Code Existed in Excel file at Serial No :"+ indexval+"\n";
            	}
            	if(dbDuplicate.length>0){
            		msg+="alredy Existed Project Code at serial No :"+ dbDuplicate+"\n";
            	}
            	if(procode.length>0){
            		msg+="Project Code length is too long at serial No :"+ procode +"\n";
            	}if(projcode.length>0){
            		msg+="Enter Project Code at serial No :"+ projcode +"\n";
            	}
            	if(proname.length>0){
            		msg+="Project Name is too long at serial No :"+ proname +"\n";
            	}
				if(projectname.length>0){
					msg+="Enter Project Name  at serial No :"+ projectname +"\n";
            	}
				if(prodesc.length>0){
					msg+="Enter Project No is too long at serial No :"+ prodesc +"\n";
				}
				if(projectDescription.length>0){
					msg+="Enter Project No at serial No :"+ projectDescription +"\n";
				}
				if(unitcode.length>0){
					msg+=" Project Unit Code is too long at serial No :"+ unitcode +"\n";
				}
				if(projectunitcode.length>0){
					msg+="Enter Project Unit Code  at serial No :"+ projectunitcode +"\n";
				}
				if(pslno.length>0){
					msg+=" Project Sanction Letter No is too long at serial No :"+ pslno +"\n";
				}
				if(letterNo.length>0){
					msg+="Enter Project Sanction Letter No at serial No :"+ letterNo +"\n";
				}
				if(sanccost.length>0){
					msg+=" Project Total Sanction Cost sholud be only digit at serial No :"+ sanccost +"\n";
				}
				if(sancost.length>0){
					msg+="Enter Project Total Sanction Cost is too long at serial No :"+ sancost +"\n";
				}
				if(totalsancost.length>0){
					msg+="Enter Project Total Sanction Cost at serial No :"+ totalsancost +"\n";
				}
				if(nodalparticipat1.length>0){
					msg+="Enter Nodal & Participating Lab is too long at serial No :"+ nodalparticipat1 +"\n";
				}
				if(nodalparticipat2.length>0){
					msg+="Enter Nodal & Participating Lab at serial No :"+ nodalparticipat2 +"\n";
				}
				if(Scope1.length>0){
					msg+="Enter Scope is too long at serial No :"+ Scope1 +"\n";
				}
				if(Scope2.length>0){
					msg+="Enter Scope at serial No :"+ Scope2 +"\n";
				}
				if(Objective1.length>0){
					msg+="Enter Objective is too long at serial No :"+ Objective1 +"\n";
				}
				if(Objective2.length>0){
					msg+="Enter Objective at serial No :"+ Objective2 +"\n";
				}
				if(Deliverable1.length>0){
					msg+="Enter Deliverable is too long at serial No :"+ Deliverable1 +"\n";
				}
				if(Deliverable2.length>0){
					msg+="Enter Deliverable at serial No :"+ Deliverable2 +"\n";
				}
				if(costvalidation.length>0){
					msg+="Total Sanction Cost should be greater than  Sanction Cost FE at serial No :"+ costvalidation +"\n";
				}
				if(sanccostFe1.length>0){
					msg+=" Project  Sanction Cost FE sholud be only digit at serial No :"+ sanccostFe1 +"\n";
				}
				if(sanccostFe2.length>0){
					msg+="Enter Project  Sanction Cost FE is too long at serial No :"+ sanccostFe2 +"\n";
				}
				if(sanccostFe3.length>0){
					msg+="Enter Project  Sanction Cost FE at serial No :"+ sanccostFe3 +"\n";
				}
            	if(checkExcel>0){
        			 alert("Please Upload Project Main Excel ");
        			excel_file.value = '';
        		}else{
        			if(sanccostFe3.length>0 || sanccostFe2.length>0 || sanccostFe1.length>0 || sanccost.length>0 || costvalidation.length>0 || Deliverable1.length>0 || Deliverable2.length>0 || Objective2.length>0 || Objective1.length>0 || Scope2.length>0 || Scope1.length>0 || nodalparticipat2.length>0 || nodalparticipat1.length>0 || sancost.length>0 || totalsancost.length>0 || letterNo.length>0 || pslno.length>0 || projectunitcode.length>0 || unitcode.length>0 || projcode.length>0 || procode.length>0 || indexval.length>0 ||dbDuplicate.length>0 ){
	               		alert(msg);
	               		excel_file.value = '';
              	 	}else{
            			 $('#exampleModalLong').modal('show');
            		}
        		}
            	
            }else{
            	alert("Please Select the Excel File!");
            	return false;
            }
        }
        
});

$('#exampleModalLong').on('hide.bs.modal', function(){
	 excel_file.value = '';
})
</script> 
<div class="modal" id="loader">
					<!-- Place at bottom of page -->
				</div>
</body>
</html>