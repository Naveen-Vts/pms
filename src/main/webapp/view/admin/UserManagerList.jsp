<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"
	import="java.util.*,com.vts.*,java.text.SimpleDateFormat"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/js/excel.js" var="excel" />
<script src="${excel}"></script>
<title>LOGIN LIST</title>
<spring:url value="/resources/css/admin/UserManagerList.css" var="userManagerList" />
<link href="${userManagerList}" rel="stylesheet" />
</head>
<body>


	<%
	List<Object[]> UserManagerList=(List<Object[]>)request.getAttribute("UserManagerList"); 
	String Onboarding = (String)request.getAttribute("Onboarding");
	List<Object[]> officerlist = (List<Object[]>)request.getAttribute("OfficerList");
	List<Object[]> usermanager = (List<Object[]>)request.getAttribute("UserManager");
	String labCode = (String) session.getAttribute("labcode");
	
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


	<div class="container-fluid">
		<div class="row">

			<div class="col-md-12">



				<div class="card shadow-nohover">
					<div class="card-header">
						<div class="row">
						<div class="col-md-5"><h4>User Credentials List</h4></div>
						<div class="col-md-7" align="right">
							 <%if(Onboarding!=null && "Yes".equalsIgnoreCase(Onboarding)){%>
							  <form action="LoginExcelUpload.htm" method="post" enctype="multipart/form-data">
								  		<table>
									  		<tr>
											  	<td align="left"><h6>Download Excel : &nbsp;<button formaction="LoginExcelUpload.htm" formmethod="post" formnovalidate="formnovalidate" name="Action" value="GenerateExcel"><i class="fa fa-file-excel-o text-success" aria-hidden="true" ></i></button></h6></td>
												<td align="right"><h6>&nbsp;&nbsp;&nbsp;&nbsp;	Upload Excel :&nbsp;&nbsp;&nbsp;&nbsp;
												  <input type="file" id="excel_file" name="filename" required="required"  accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"></h6></td>						
										  		<td align="right"> </td>
										    </tr>
								  		</table>	
								     <input type="hidden" name="${_csrf.parameterName}"value="${_csrf.token}" />
							   <div class="modal fade" id="exampleModalLong" tabindex="-1" role="dialog" aria-labelledby="exampleModalLongTitle" aria-hidden="true"  >
								  <div class="modal-dialog modal-lg" role="document">
								    <div class="modal-content"  >
								      <div class="modal-header">
								        <h5 class="modal-title" id="exampleModalLongTitle">Login Master Details</h5>
								        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
								          <span aria-hidden="true">&times;</span>
								        </button>
								      </div>
								      <div class="modal-body hei-over" align="left">
								             <table class="table table-bordered table-hover table-striped table-condensed " id="myTable1" > </table>
								      </div>
								      <div class="modal-footer" align="center">
								        			<button type="submit" onclick="return confirm('Are you sure to submit?')" class="btn btn-sm add m-minus" name="Action" value="UploadExcel"> Upload</button>
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

						<form action="UserManager.htm" method="POST" name="frm1">
							<div class="table-responsive">
								<table
									class="table table-bordered table-hover table-striped table-condensed"
									id="myTable">
									<thead class="text-center">
										<tr>
											<th>Select</th>
											<th>Employee</th>
											<th>User Name</th>
											<th>Division Name</th>
											<th class="w-10">PMS Login</th>
											<th>Login type</th>
										</tr>
									</thead>
									<tbody>
										<%for(Object[] obj:UserManagerList){ %>
										<tr>
											<td class="text-center" ><input type="radio" name="Lid" value=<%=obj[0]!=null?StringEscapeUtils.escapeHtml4(obj[0].toString()): " - "%>></td>
											<td><%=obj[5]!=null?StringEscapeUtils.escapeHtml4(obj[5].toString()): " - " %>, <%=obj[6]!=null?StringEscapeUtils.escapeHtml4(obj[6].toString()): " - " %></td>
											<td><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()): " - " %></td>
											<td><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()): " - " %></td>
											<%-- <td><%=obj[3] %></td> --%>
											<td class="text-center">
												<%if(obj[4].equals("N")){%><span class="badge badge-warning">No</span>
												<%}else{ %><span class="badge badge-success">Yes</span>
												<%} %>
											</td>
											<td><%=obj[7]!=null?StringEscapeUtils.escapeHtml4(obj[7].toString()): " - " %></td>
										</tr>
										<%} %>
									</tbody>
								</table>
							</div>

							<div align="center">
								<button type="submit" class="btn btn-primary btn-sm add"
									name="sub" value="add">ADD</button>
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

								<%if(UserManagerList!=null){ %>
								<button type="submit" class="btn btn-warning btn-sm edit"  name="sub" value="edit" onclick="Edit(frm1)">EDIT</button>
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								<button type="submit" class="btn btn-danger btn-sm delete" name="sub" value="delete" onclick="Delete(frm1)">DELETE</button>
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								<%if(!"CAIR".equalsIgnoreCase(labCode)){ %>
								<button type="submit" class="btn btn-danger btn-sm btn-font"  formaction="Resetpassword.htm" Onclick="return ResetPwd(frm1)">RESET PASSWORD </button>
								<%} %>
								<%}%>
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								 <%if(Onboarding!=null && "Yes".equalsIgnoreCase(Onboarding)){%>
									<a class="btn btn-info btn-sm  back"   href="OnBoarding.htm">Back</a>
									<%}else{%>
									<a class="btn btn-info btn-sm  back"   href="MainDashBoard.htm">Back</a>
								<%}%>		
							</div>

							<input type="hidden" name="${_csrf.parameterName}"
								value="${_csrf.token}" />
						</form>
					</div>


				</div>

			</div>

		</div>

	</div>

	<script type="text/javascript">

function Edit(myfrm){
	 var fields = $("input[name='Lid']").serializeArray();
	  if (fields.length === 0){
			alert("PLEASE SELECT ONE RECORD");
	 		event.preventDefault();
			return false;
		}
		  return true;	
	}
	
function Delete(myfrm){
	var fields = $("input[name='Lid']").serializeArray();

	  if (fields.length === 0){
		 alert("PLEASE SELECT ONE RECORD");
		 event.preventDefault();
		return false;
	}
	  var cnf=confirm("Are You Sure To Delete!");
	if(cnf){
		return true;
	}else{
		  event.preventDefault();
			return false;
	}
}
function ResetPwd(myfrm){
	var fields = $("input[name='Lid']").serializeArray();

	  if (fields.length === 0){
		 alert("PLEASE SELECT ONE RECORD");
		 event.preventDefault();
		return false;
	}
	  var cnf=confirm("Are You Sure To Reset The Password!");
	if(cnf){
		return true;
	}else{
		  event.preventDefault();
			return false;
	}
}

</script>


	<script type="text/javascript">

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
		 "lengthMenu": [10,25, 50, 75, 100 ],
		 "pagingType": "simple",
			 "pageLength": 10
	});
});

$(document).ready(function(){
	  $("#myTable1").DataTable({
	 "lengthMenu": [10,25, 50, 75, 100 ],
	 "pagingType": "simple",
		 "pageLength": 10
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
    	
    	const Empcode=[];
        if(sheet_data.length > 0)
        {          
        	var table_output = ' <table class="table table-bordered table-hover table-striped table-condensed " id="myTable1" > ';
            
        	table_output+='<thead> <tr > <th class="text-center">SNo</th> <th class="text-center">User Name</th> <th class="text-center">Employee Number</th> <th class="text-center">Login Type</th> </tr> </thead><tbody>'
        	var index=0;
        	var checkExcel=0;
            
    		if( "User Name" != sheet_data[1][1]){  checkExcel++;}
			if( "Employee Number" != sheet_data[1][2]){  checkExcel++;}
			if( "Login Type"   != sheet_data[1][3]){  checkExcel++;}
			
            for(var row = 2; row < sheet_data.length; row++)
            {            	
            	  table_output += ' <tr> ';
             	 
            	  if(row>0){table_output += '<td>'+ (++index) +'</td>';}
                for(var cell = 0; cell < 4; cell++)
                {
                	if(row>0 && cell==1){
                		table_output += '<td>'+sheet_data[row][cell]+'</td>';
                	}
                	if(row>0 && cell==3){
                		var logintype = sheet_data[row][cell];
                		if("A"==logintype){logintype="Admin";}
                		if("E"==logintype){logintype="P&C DO";}
                		if("U"==logintype){logintype="User";}
                		if("T"==logintype){logintype="GHDH";}
                		if("P"==logintype){logintype="Project Director";}
                		if("Z"==logintype){logintype="Director";}
                		table_output += '<td>'+ logintype +'</td>';
                	}
	                if(row>0 && cell==2){		
	                	table_output += '<td>'+sheet_data[row][cell]+'</td>';		     									 
	                	var employeeno = ""+sheet_data[row][cell]+"";
						if (employeeno.trim().length >= 5 ){								     									 
							Empcode.push(row-1);
						}
					}		
                }
                table_output += '</tr>';
             }
            table_output += ' <tbody></table>';
            document.getElementById('myTable1').innerHTML = table_output;
             
             var loginlist = [<%int i=0; for (Object[] obj:UserManagerList) { %>"<%= obj[8] %>"<%= i + 1 < UserManagerList.size() ? ",":"" %><% } %>];
             var Empname = [<%int j=0; for (Object[] obj:usermanager) { %>"<%= obj[1] %>"<%= j + 1 < usermanager.size() ? ",":"" %><% } %>];
             var officerlist = [<%int k=0; for (Object[] obj:officerlist) { %>"<%= obj[1] %>"<%= k + 1 < officerlist.size() ? ",":"" %><% } %>];
                
             
             var Excelempno=[];
             for (var i in sheet_data){
            	 if(i>1){
            		 Excelempno.push(sheet_data[i][2]+"")
            	 }
          	}
             var CheckEmpno=[];
             for (var i in sheet_data){
            	 if(i>1 && officerlist.indexOf(sheet_data[i][2]+"")==-1){
            		 CheckEmpno.push(i-1)
            	 }
          	}
             console.log("CheckEmpno    :"+CheckEmpno);
             var Excellogintype=[];
             for (var i in sheet_data){
            	 var logintype = sheet_data[i][3]+"";
            	 if(i>1 && "A"!=logintype && "U"!=logintype && "E"!=logintype && "T"!=logintype && "P"!=logintype && "Z"!=logintype){
            		 Excellogintype.push(i-1);
            	 }
          	}
             
             var UserName=[];
             for (var i in sheet_data){
            	 if(i>1 && Empname.indexOf(sheet_data[i][1]+"")!==-1){
            		 UserName.push(i)
            	 }
          	}	
             const duplicates = Excelempno.filter((item, index) => index !== Excelempno.indexOf(item));  

             const indexval = []             
             for(var i in duplicates){
            	 indexval.push(Excelempno.indexOf(duplicates[i])+1)
             } 
             var dbDuplicate = [];                      
             loginlist.forEach(function(item){
            	  var isPresent = Excelempno.indexOf(item);
            	  if(isPresent !== -1){
            		  dbDuplicate.push(isPresent+1); 
            	  }
            	})
            	
            	var msg='';
            	if( dbDuplicate.length>0){
            		 msg += dbDuplicate +" Serial No Employee's Already Have Login \n";
            	}else if(indexval.length>0){
            		 msg +="Duplicate Emplyee No at serial No :"+ indexval +"\n";
            	} if(Empcode.length > 0){
            		 msg +="Employee Number should be 4 character at Serial No :"+ Empcode +"\n";
                }if(UserName.length>0){
                	msg += UserName+" Serial No User Name Already Available in database  \n";
                }if(Excellogintype.length>0){
                	msg += " Enter Login Type According the Note at serial No :"+Excellogintype+" \n";
                }if(CheckEmpno.length>0){
                	msg += " Invalid Employee No at serial No :"+CheckEmpno+" \n";
                }
                
                if(checkExcel>0){
       			 	alert("Please Upload Login Master Excel ");
       				excel_file.value = '';
       			} else {
                	 if(CheckEmpno.length>0 || Excellogintype.length>0 || dbDuplicate.length>0 || indexval.length>0 || Empcode.length > 0 ||UserName.length>0){
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

</body>
</html>