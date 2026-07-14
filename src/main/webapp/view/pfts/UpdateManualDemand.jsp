<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="java.text.Format"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.math.BigDecimal"%> 
<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@page import="com.vts.pfms.NFormatConvertion"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat,java.time.LocalDate"%>
 <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
    
   
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/pfts/UpdateManualDemand.css" var="fromExternalCSS" />     
<link href="${fromExternalCSS}" rel="stylesheet" />
<title>Update Manual Demand</title>

</head>
<body>
  <%
  Object[] fileView = (Object[])request.getAttribute("fileViewList");
  List<Object[]> fileStage=(List<Object[]>)request.getAttribute("pftsStageList");
  List<Object[]> OrderLists=(List<Object[]>)request.getAttribute("OrderLists");
  List<Object[]> pftsStageList1=fileStage.stream().filter(i->Integer.parseInt(i[0].toString())<=10).collect(Collectors.toList());
  List<Object[]> pftsStageList2=fileStage.stream().filter(i->Integer.parseInt(i[0].toString())>=10).collect(Collectors.toList());
  List<Object[]> pftsStageList3=fileStage.stream().filter(i->Integer.parseInt(i[0].toString())>10).collect(Collectors.toList());
  String projectid = fileView[11].toString();
  int fileViewValue = Integer.parseInt(fileView[7].toString()); 
  SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
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
	<div class="card shadow-nohover style1">		
		<div class="row card-header">
			<div class="col-md-6">
				<h4>Demand No : <%=fileView[1]!=null?StringEscapeUtils.escapeHtml4(fileView[1].toString()):"-" %></h4>
			</div>
        </div>
        <div class="card-body">
             <form action="updateManualDemandSubmit.htm" method="post">
                     <div class="row mt-2">
				           <div class="col-md-3">
								<label> Item Nomenclature : </label> 
						  </div>
						  <div class="col-md-9 style2">
								<input type="text" class="form-control style3" value="<%=fileView[4]!=null?StringEscapeUtils.escapeHtml4(fileView[4].toString()):"-" %>" 
									name="procitems" id="procitems" readonly="readonly">
						  </div>
				     </div>
                     <div class="row mt-2">
						  	<div class="col-md-2">
								<label class="control-label" > Procurement Status :</label>
						   </div>
							<div class="col-md-3 style4">
								 <select onchange="modalOpen()"
									class="form-control selectdee"
									id="procstatus" required="required" name="procstatus">
									<option disabled="true"  selected value="">Select...</option>
									<% if (fileViewValue < 10) {  %>
								       <% for (Object[] obj : pftsStageList1) {%>
								          <% if(obj[0].toString().equalsIgnoreCase("10")){ %>
								           <option value="<%=obj[0]%>"<%if(obj[0].toString().equalsIgnoreCase(fileView[7].toString())){ %> selected <%} %>><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()):"-"%></option>
									      <% }else{%>
									       <option value="<%=obj[0]%>"<%if(obj[0].toString().equalsIgnoreCase(fileView[7].toString())){ %> selected <%} %>><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()):"-"%></option>
									      <%} %>
									   <%} %>
									<%}else if(fileViewValue == 10){ %>
									   <% for (Object[] obj : pftsStageList2) {%>
								       <option value="<%=obj[0]%>"<%if(obj[0].toString().equalsIgnoreCase(fileView[7].toString())){ %> selected <%} %>><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()):"-"%></option>
									   <%} %>
									<%}else{ %>
									   <% for (Object[] obj : pftsStageList3) {%>
								       <option value="<%=obj[0]%>"<%if(obj[0].toString().equalsIgnoreCase(fileView[7].toString())){ %> selected <%} %>><%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()):"-"%></option>
									   <%} %>
									<%} %>
								</select>
								</div>
								
								<div class="col-md-1">
							   <label class="control-label" >Event Date :</label>
							   </div>
							   <div class="col-md-2">
							    <input  class="form-control form-control date"  data-date-format="dd-mm-yyyy" id="datepicker1" 
							    name="eventDate"  required="required">
						    </div>
						    <div class="col-md-1" >
								<label> Remarks : </label> 
								</div>
								<div class="col-md-3">
								<input type="text" class="form-control style5" value="<%=fileView[9]!=null?StringEscapeUtils.escapeHtml4(fileView[9].toString()):"" %>"
									name="procRemarks" id="procRemarks" required="required">
							</div>
					 </div>
					 <br>
					  <div class="form-group" align="center" id="btnSubmit1">
					    <button type="submit" class="btn btn-primary btn-sm submit"  value="SUBMIT" id="manualAddBtn" onclick ="return confirm('Are you sure to submit?')">SUBMIT </button>
					 	<input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" />
					 	<input type="hidden" id="pstatusid" value="<%=fileView[7]%>"> 
					 	<input type="hidden" id="projectId" name="projectId" value="<%=projectid%>"> 
					 	<input type="hidden" id="fileId" name="fileId" value="<%=fileView[0]%>">
					    <a class="btn btn-info btn-sm  shadow-nohover back" href="ProcurementStatus.htm?projectid=<%=projectid%>">Back</a>
				     </div>
					 </form>
					 <div id="btnplus" class="style6">
					    <button type="button" class=" btn btn_add style7" onclick="copyDiv()"> <i class="btn btn-sm fa fa-plus"></i></button>
					 </div> 
					 <form action="updateManualDemandSubmit.htm" method="post">
					 <div class="divHidden mt-3 style8" id="divHidden">
						<div class="row mt-2">
							<div class="col-sm-1">
								<label class="control-label">Order No :</label>
							</div>
							<div class="col-sm-2 style9">
								<input type="text" class="form-control" name="orderno"
									id="orderno" required="required" placeholder="Enter Order No">
							</div>
							<div class="col-sm-1">
								<label class="control-label">Order Date :</label>
							</div>
							<div class="col-sm-2">
								<input class="form-control form-control date datepicker2 style10"
									data-date-format="dd-mm-yyyy" id="" name="orderdate"
									required="required">
							</div>
							<div class="col-sm-1 style11">
								<label class="control-label">Order Cost :</label>
							</div>
							<div class="col-sm-2">
								<input type="number" class="form-control" name="ordercost"
									 id="ordercost" required="required"
									placeholder="Enter Cost in Rupees (&#8377;)">
							</div>
							<div class="col-sm-1">
								<label class="control-label">DP Date :</label>
							</div>
							<div class="col-sm-2 style9">
								<input class="form-control form-control date datepicker3"
									data-date-format="dd-mm-yyyy" id="" name="dpdate"
									required="required">
							</div>
						  
						</div>
						<div class="row mt-2">
							<div class="col-md-1">
								<label> Item For : </label>
							</div>
							<div class="col-md-5 style9" >
								<input type="text" class="form-control style12"
									placeholder="Enter Item Details" name="itemfor" id="itemfor"
									required="required">
							</div>
								<div class="col-md-1">
								<label> Vendor : </label>
							</div>
							<div class="col-md-5 style11">
								<input type="text" class="form-control style13"
									placeholder="Enter Vendor Name" name="vendor" id="vendor"
									required="required">
							</div>
							<span class="style11"><button type="button" id="btnminus" class=" btn btn_rem" onclick="RemoveDiv(this)"> <i class="btn btn-sm fa fa-minus style14"></i></button></span>
						</div>
						<br>
						<div class="style15"></div>
					</div>
					 <div class="form-group style16" align="center" id="btnSubmit2">
					       <button type="submit" class="btn btn-primary btn-sm submit"  value="SUBMIT" id="manualAddBtn" onclick ="return confirm('Are you sure to submit?')">SUBMIT </button>
					 	   <input type="hidden" name="${_csrf.parameterName}"	value="${_csrf.token}" />
					 	   <input type="hidden" id="pstatusid" value="<%=fileView[7]%>"> 
					 	   <input type="hidden" id="projectId" name="projectId" value="<%=projectid%>"> 
					 	   <input type="hidden" id="fileId" name="fileId" value="<%=fileView[0]%>">
					 	   <input type="hidden" id="fileStatusId" name="procstatus" value="">
					 	   <input type="hidden" id="fileEventDate" name="eventDate" value="">
					 	   <input type="hidden" id="fileRemarks" name="procRemarks" value="">
					 	   <input type="hidden" name="flag" value="order">
					       <a class="btn btn-info btn-sm  shadow-nohover back" href="ProcurementStatus.htm?projectid=<%=projectid%>">Back</a>
					       <%if(OrderLists != null && !OrderLists.isEmpty()){ %>
						       <button type="button" 
						       class="btn btn-sm add"
						       data-toggle="modal"
					            data-target="#orderDetailsModal"
					            >
						       		Order Details
						       </button>
					       <%} %>
				      </div>
				 </form>
		              <br>
		            
				<div class="col-md-12 style17">
							<div>
								<%
								int i = 0;
								for (Object[] obj1 : fileStage) {
									if (i == 9)
										break;
								%>
								<p class="pstatus" id="<%=obj1[0].toString()%>"><%=obj1[0]!=null?StringEscapeUtils.escapeHtml4(obj1[0].toString()):"-"%>. <%=obj1[2]!=null?StringEscapeUtils.escapeHtml4(obj1[2].toString()):"-"%>
								</p>
								<%
								i++;
								}
								%>
							</div>
							<div>
								<%
								int j = 0;
								for (Object[] obj1 : fileStage.stream().skip(9).collect(Collectors.toList())) {
									if (j == 9)
										break;
								%>
								<p class="pstatus" id="<%=obj1[0].toString()%>"><%=obj1[0]!=null?StringEscapeUtils.escapeHtml4(obj1[0].toString()):"-"%>. <%=obj1[2]!=null?StringEscapeUtils.escapeHtml4(obj1[2].toString()):"-"%>
								</p>
								<%
								j++;
								}
								%>
							</div>
							<div>
								<%
								int k = 0;
								for (Object[] obj1 : fileStage.stream().skip(18).collect(Collectors.toList())) {
									if (k == 9)
										break;
								%>
								<p class="pstatus" id="<%=obj1[0].toString()%>"><%=obj1[0]!=null?StringEscapeUtils.escapeHtml4(obj1[0].toString()):"-"%>. <%=obj1[2]!=null?StringEscapeUtils.escapeHtml4(obj1[2].toString()):"-" %>
								</p>
								<%
		                        k++;
		                        } %>
						  </div>
						</div>
			<!--  <div class="table-responsive">
					<table
						class="table table-bordered table-hover table-striped table-condensed "
						id="myTable" style="width: 91%">
						<thead>
							<tr>
								<th>SN</th>
								<th>OrderNo</th>
								<th>OrderDate</th>
								<th>ItemFor</th>
								<th>OrderCost</th>
								<th>DPDate</th>
								<th>VendorName</th>
								<th>Action</th>
							</tr>
						</thead>
						<tbody>
						     <tr>
						        <td></td>
						        <td></td>
						        <td></td>
						        <td></td>
						        <td></td>
						        <td></td>
						        <td></td>
						        <td></td>
						     </tr>
						</tbody>
					</table>
				</div> -->
         </div>
	</div>
	
	
	<div class="modal fade" id="orderDetailsModal" tabindex="-1" role="dialog">
    <div class="modal-dialog large-order-modal modal-dialog-censtered" role="document">
        <div class="modal-content">

            <div class="modal-header bg-primary text-light">
                <h5 class="modal-title">Order Details</h5>
                <button type="button" class="close text-light" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>

            <div class="modal-body">
                <!-- Put your order details here -->
               <table class="table table-bordered table-hover table-striped table-condensed " id="myTable" > 
                	<thead>
                		<tr class="text-center">
                			<th>SN</th>
                			<th>Order No</th>
                			<th>Order Date</th>
                			<th>Order Cost</th>
                			<th>DP Date</th>
                			<th>Item For</th>
                			<th>Vendor</th>
                		</tr>
                	</thead>
                	<tbody>
                		<% if(OrderLists != null && !OrderLists.isEmpty()){
                			int count=1;
                			for(Object[] obj: OrderLists){ %>
                				<tr>
                					<td class="text-center"><%= count++ %></td>
                					<td><%=obj[6]!=null ? StringEscapeUtils.escapeHtml4(obj[6].toString()) : " - " %></td>
                					<td class="text-center"><%=obj[7]!=null ? sdf.format(obj[7]) : " - " %></td>
                					<td class="text-right"><%=obj[10]!=null ? StringEscapeUtils.escapeHtml4(obj[10].toString()) : " - " %></td>
                					<td class="text-center"><%=obj[8]!=null ? sdf.format(obj[8]) : " - " %></td>
                					<td><%=obj[9]!=null ? StringEscapeUtils.escapeHtml4(obj[9].toString()) : " - " %></td>
                					<td><%=obj[11]!=null ? StringEscapeUtils.escapeHtml4(obj[11].toString()) : " - " %></td>
                				</tr>
                		<%}} %>
                	</tbody>                
                </table>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-secondary"
                        data-dismiss="modal">
                    Close
                </button>
            </div>

        </div>
    </div>
</div>
</div>

	<!-- <div class="modal fade my-modal" id="exampleModalCenter" tabindex="-1"
		role="dialog" aria-labelledby="exampleModalCenter"
		aria-hidden="true">
		<div
			class="modal-dialog  modal-lg modal-dialog-centered modal-dialog-jump"
			role="document">
			<div class="modal-content" style="height: 400px;width: 117% !important;">
				<div class="modal-header">
					<h5 style="font-size: x-large; font-weight: 700;margin-inline-start: auto;">Enter Order Details</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<div class="row">
						<div class="col-md-2">
							<label class="control-label">Order No :</label>
						</div>
						<div class="col-md-5">
							<input type="text" class="form-control" name="orderno"
							 id="orderno" required="required" placeholder="Enter Order No">
						</div>
						<div class="col-md-2">
							   <label class="control-label" >Order Date :</label>
						</div>
						<div class="col-md-3">
							<input  class="form-control form-control date datepicker2"  data-date-format="dd-mm-yyyy"
							   name="orderdate"  required="required" >
						  </div>
					</div>
					<div class="row mt-2">
						<div class="col-md-2">
							<label class="control-label">Order Cost :</label>
						</div>
						<div class="col-md-5">
							<input type="number" class="form-control" name="ordercost" max="100" min="0"
							 id="ordercost" required="required" placeholder="Enter Cost in Rupees (&#8377;)">
						</div>
						<div class="col-md-2">
							   <label class="control-label" >DP Date :</label>
						</div>
						<div class="col-md-3">
							<input  class="form-control form-control date datepicker3"  data-date-format="dd-mm-yyyy"
							   name="orderdate"  required="required" >
						  </div>
					</div>
					<div class="row mt-2">
				           <div class="col-md-3">
								<label> Item For : </label> 
								</div>
								<div class="col-md-9" style=" margin-left: -9%;" >
								<input type="text" class="form-control" placeholder="Enter Item Details"
									name="itemfor" id="itemfor" required="required"
									style="width:113%">
							</div>
						</div>
						<div class="row mt-2">
				           <div class="col-md-3">
								<label> Vendor : </label> 
								</div>
								<div class="col-md-9" style=" margin-left: -9%;" >
								<input type="text" class="form-control" placeholder="Enter Vendor Name"
									name="itemfor" id="itemfor" required="required"
									style="width:113%">
							</div>
						</div>
				</div>
				<div align="center">
					<button type="button" class="btn btn-primary mb-5" style="font-size: 18px;font-weight: 600">SUBMIT</button>
				</div>
			</div>
		</div>
	</div> -->

<script type="text/javascript">
	$('#datepicker1').daterangepicker({
		"singleDatePicker" : true,
		"linkedCalendars" : false,
		"showCustomRangeLabel" : true,
		"startDate" : new Date(),
		"cancelClass" : "btn-default",
		showDropdowns : true,
		locale : {
			format : 'DD-MM-YYYY'
		}
	});
	$('.datepicker2').daterangepicker({
		"singleDatePicker" : true,
		"linkedCalendars" : false,
		"showCustomRangeLabel" : true,
		"startDate" : new Date(),
		"cancelClass" : "btn-default",
		showDropdowns : true,
		locale : {
			format : 'DD-MM-YYYY'
		}
	});
	$('.datepicker3').daterangepicker({
		"singleDatePicker" : true,
		"linkedCalendars" : false,
		"showCustomRangeLabel" : true,
		"startDate" : new Date(),
		"cancelClass" : "btn-default",
		showDropdowns : true,
		locale : {
			format : 'DD-MM-YYYY'
		}
	});
	$(document).ready(function(){
		  $("#myTable").DataTable({
		 "lengthMenu": [ 5,10,20, 40, 60, 80, 100 ],
		 "pagingType": "simple",
		 "pageLength": 10
		});
	});
</script>
<script type="text/javascript">
$(document).ready(function() {
		$(".pstatus").removeClass("blinking-element"); // Remove blink class from all elements
		var pstatusid = $("#pstatusid").val(); // Retrieve value of pstatusid
		$("#" + pstatusid).addClass("blinking-element"); // Add blink class on specific id
	});
	
function modalOpen() {
	var status=$('#procstatus').val();
	var eventdate=$('#datepicker1').val();
	var status=$('#procstatus').val();
	var remarks=$('#procRemarks').val();
	if(status==10){
		$('#divHidden').show();
		$('#btnplus').show();
		$('#btnSubmit2').show();
		$('#btnSubmit1').hide();
		$('#fileStatusId').val(status);
		$('#fileEventDate').val(eventdate);
		$('#fileRemarks').val(remarks);
	}else{
		$('#btnSubmit1').show();
		$('#divHidden').hide();
		$('#btnSubmit2').hide();
		$('#btnplus').hide();
	}
	
}

var elements=document.getElementById("divHidden").innerHTML;
console.log(elements);

function copyDiv(){
	
	/*  $(".divHidden").append(elements); */
	 $(".divHidden").append('<div class="appendedDiv">' + elements + '</div>');
	 
	$('.datepicker2').daterangepicker({
		"singleDatePicker" : true,
		"linkedCalendars" : false,
		"showCustomRangeLabel" : true,
		"startDate" : new Date(),
		"cancelClass" : "btn-default",
		showDropdowns : true,
		locale : {
			format : 'DD-MM-YYYY'
		}
	});
	$('.datepicker3').daterangepicker({
		"singleDatePicker" : true,
		"linkedCalendars" : false,
		"showCustomRangeLabel" : true,
		"startDate" : new Date(),
		"cancelClass" : "btn-default",
		showDropdowns : true,
		locale : {
			format : 'DD-MM-YYYY'
		}
	});

}

function RemoveDiv(btn) {
    var appendedDivs = document.querySelectorAll('.divHidden .appendedDiv');
    if (appendedDivs.length > 0) {
        // Remove the last appended div
        var lastAppendedDiv = appendedDivs[appendedDivs.length - 1];
        lastAppendedDiv.remove();
    } else {
        console.error("No appended divs found");
    }
}
$(document).ready(function() {
modalOpen()
});
</script>
</body>
</html>