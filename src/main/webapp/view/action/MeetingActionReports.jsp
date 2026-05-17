<%@page import="java.util.List"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="com.ibm.icu.text.DecimalFormat"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/css/action/meetingActionReports.css" var="meetingActionReports" />
<link href="${meetingActionReports}" rel="stylesheet" />
<spring:url value="/resources/css/action/actionCommon.css" var="actionCommon" />
<link href="${actionCommon}" rel="stylesheet" />
 
<!-- Pdfmake  -->
	<spring:url value="/resources/pdfmake/pdfmake.min.js" var="pdfmake" />
	<script src="${pdfmake}"></script>
	<spring:url value="/resources/pdfmake/vfs_fonts.js" var="pdfmakefont" />
	<script src="${pdfmakefont}"></script>
	<spring:url value="/resources/pdfmake/htmltopdf.js" var="htmltopdf" />
	<script src="${htmltopdf}"></script>

<title>Action List</title>


</head>
<body>
 <%
SimpleDateFormat sdf1=new SimpleDateFormat("dd-MM-yyyy");
SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	
List<Object[]> ProjectsList=(List<Object[]>) request.getAttribute("ProjectsList");
List<Object[]>  projapplicommitteelist=(List<Object[]>)request.getAttribute("projapplicommitteelist");
List<Object[]> meetingcount=(List<Object[]>) request.getAttribute("meetingcount");
List<Object[]> meetinglist=(List<Object[]>) request.getAttribute("meetinglist");
int meettingcount=1;

String projectid=(String)request.getAttribute("projectid");
String committeeid=(String)request.getAttribute("committeeid");
String scheduleid =(String)request.getAttribute("scheduleid");
String status = (String)request.getAttribute("status");

String projectName = "", committeName = "", meetingName = "", statusName = "";
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

    <br/>
</body>
<div class="container-fluid">
		<div class="row">
			<div class="col-md-12">
				<div class="card shadow-nohover">
           <div class="card-header mb-2">  
					<div class="row">
						<h4 class="col-md-4">Meeting Action Reports</h4>  
							<div class="col-md-8 div-margin"  >
					   			<form method="post" action="MeettingActionReports.htm" name="dateform" id="myform">
					   				<table>
					   					<tr>
					   						<td>
					   							<label class="control-label td-label" >Project: </label>
					   						</td>
					   						<td class="td-width">
                                               <select class="form-control selectdee" id="projectid" required="required" name="projectid" onchange='submitForm1();' >
										<% if(ProjectsList!=null && ProjectsList.size()>0){
										 for (Object[] obj : ProjectsList) {
											 String projectshortName=(obj[17]!=null)?" ( "+obj[17].toString().trim()+" ) ":"";			 
										 %>
												<option value="<%=obj[0]%>" <%if(obj[0].toString().equals(projectid)){ projectName = obj[4].toString()+projectshortName; %>selected<%} %> ><%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()):" - "%> <%= projectshortName!=null?StringEscapeUtils.escapeHtml4(projectshortName):" - " %></option>
										<%}} %>
								             </select>       
											</td>
											<td>
					   							<label class="control-label td-label">Committee: </label>
					   						</td>
					   						<td class="td-width">
                                              <select class="form-control selectdee" id="committeeid" required="required" name="committeeid" onchange='submitForm();' >
							   			        	<%if(projapplicommitteelist!=null && projapplicommitteelist.size()>0){
							   			        	  for (Object[] obj : projapplicommitteelist) {%>
											     <option value="<%=obj[0]%>"  <%if(obj[0].toString().equals(committeeid)){ committeName=obj[3].toString(); %>selected<%} %> ><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()):" - "%></option>
											        <%}} %>   
							  	             </select>
											</td>
					   						 <td>
					   							<label class="control-label td-label"> Meeting: </label>
					   						</td>
					   						<td class="td-width">
                                                   <select class="form-control selectdee" id="meettingid" required="required" name="meettingid" onchange='submitForm();'>
							   			        	<% if(meetingcount!=null && meetingcount.size()>0){
							   			        	 for (Object[] obj : meetingcount) {%>
											         <option value="<%=obj[0]%>" <%if(obj[0].toString().equals(scheduleid)){ meetingName=obj[3].toString()+" - "+meettingcount; %>selected<%} %>><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString())+"-"+meettingcount:" - "%></option>
											        <%meettingcount++;} }%>   
							  	                  </select>				   						
											</td> 
											<td>
					   							<label class="control-label td-label"> Status: </label>
					   						</td>
					   						<td class="td-width">
                                                   <select class="form-control selectdee " name="status" id="Assignee" required="required"  data-live-search="true" onchange="submitForm();" >
                                                          <option value="N" <%if("N".equalsIgnoreCase(status)){ statusName="All"; %> selected="selected" <%} %>>All</option>
														  <option value="A" <%if("A".equalsIgnoreCase(status)){ statusName="Not Started"; %> selected="selected" <%} %>>Not Started</option>	
														  <option value="I" <%if("I".equalsIgnoreCase(status)){ statusName="In-Progress"; %> selected="selected" <%} %>>In-Progress</option>	
														  <option value="C" <%if("C".equalsIgnoreCase(status)){ statusName="Closed"; %> selected="selected" <%} %> >Closed</option>		
													</select>				   					
											</td> 	
											<td>
												<button type="button" class="btn btn-sm" onclick="downloadPDF()" formnovalidate="formnovalidate" data-toggle="tooltip" data-placement="top" title="pdf download" data-original-title="PDF Download">
										        	<i class="fa fa-download text-danger"></i>
									       		</button>
											</td>    							
					   					</tr>   					   				
					   				</table>
					   				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
					   			</form>
		   					</div>
		   				</div>	   							
					</div>
					
						<div class="data-table-area mg-b-15">
							<div class="container-fluid">
								<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
									<div class="sparkline13-list">
										<div class="sparkline13-graph">
											<div class="datatable-dashv1-list custom-datatable-overright">
												<div id="toolbar">
													
												</div>
											<table class="table table-bordered table-hover table-striped table-condensed" id="myTable12" >

													<thead>

														<tr class="text-center">
															<th>SN</th>
															<th>Action Id</th>	
															<th class="width-110px width-7">PDC</th>
															<th >Action Item</th>																								
														 	<th >Assignee</th>					 	
														 	<th >Assigner</th>
														 	<th class="width-115px">Progress</th>
														</tr>
													</thead>
													<tbody>
														<%int count=1;
															if(meetinglist!=null && meetinglist.size()>0)
															{
												   					for (Object[] obj :meetinglist) 
												   					{ %>
																	<tr>
																		<td><%=count++ %></td>
																		<td>
																		    <form action="ActionDetails.htm" method="POST" >
																				<button  type="submit" class="btn btn-outline-info"   ><%=obj[0]!=null?StringEscapeUtils.escapeHtml4(obj[0].toString()):" - " %></button>
																			   <input type="hidden" name="ActionLinkId" value="<%=obj[13]%>"/>
																	           <input type="hidden" name="Assignee" value="<%=obj[1]%>,<%=obj[2]%>"/>
																	           <input type="hidden" name="ActionMainId" value="<%=obj[10]%>"/>
																	           <input type="hidden" name="ActionAssignId" value="<%=obj[12]%>"/>
																	           <input type="hidden" name="ActionNo" value="<%=obj[0]%>"/>
																	           <input type="hidden" name="text" value="M">
																	           <input type="hidden" name="status" value=<%=status %>>
																	           <input type="hidden" name="projectid" value="<%=projectid%>">
																	           <input type="hidden" name="committeeid" value="<%=committeeid%>">
																	           <input type="hidden" name="meettingid" value="<%=scheduleid%>">
 																			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
																			
																			</form> 
																		</td>
																		<td class="text-center"><%=sdf1.format(obj[6])%></td>
																		<td>
															               <%if(obj[7]!=null && obj[7].toString().length()>100){ %>
															               <%=StringEscapeUtils.escapeHtml4(obj[7].toString()).substring(0, 100) %>
														                   <textarea id="td<%=obj[10].toString()%>" style="display: none;"><%=obj[7].toString()%></textarea>
														                   <span class="custom-sapn" onclick="showAction('<%=obj[10].toString()%>','<%=obj[0].toString()%>')">show more..</span>
															               <%}else{ %>
															               <%=obj[7]!=null?StringEscapeUtils.escapeHtml4(obj[7].toString()):" - " %>
															               <%} %>
															            </td>																				
																		<td><%=obj[1]!=null?StringEscapeUtils.escapeHtml4(obj[1].toString()):" - "%>, <%=obj[2]!=null?StringEscapeUtils.escapeHtml4(obj[2].toString()):" - "%></td>
																	  	<td><%=obj[3]!=null?StringEscapeUtils.escapeHtml4(obj[3].toString()):" - "%>, <%=obj[4]!=null?StringEscapeUtils.escapeHtml4(obj[4].toString()):" - "%></td>
																		<td class="td-wid"><%if(obj[11]!=null){ %>
															<div class="progress div-progress" >
															<div class="progress-bar progress-bar-striped width-<%=obj[11]%>" role="progressbar" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100" >
															<%=obj[11]!=null?StringEscapeUtils.escapeHtml4(obj[11].toString()):" - "%>
															</div> 
															</div> <%}else{ %>
															<div class="progress div-progress" >
															<div class="progress-bar progressbar" role="progressbar" >
															Not Yet Started .
															</div>
															</div> <%} %></td>			
																	</tr>
																<% 
																	}									   					
															}%>
													</tbody>
												</table>												
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						
					
						<br>
						<div class="card-footer" align="right">&nbsp;</div>
					</div>
				</div>
			</div>
		</div>
		
		<!-- Modal for action -->
<div class="modal fade" id="exampleModalCenter" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered" role="document">
    <div class="modal-content">
      <div class="modal-header div-height">
        <h5 class="modal-title" id="exampleModalLongTitle">Action</h5>
        <button type="button" class="close text-danger" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body" id="modalbody">
     
      </div>
      <div align="right" id="header" class="p-2"></div>
    </div>
  </div>
</div>
					
<script type="text/javascript">
function submitForm()
{ 
  document.getElementById('myform').submit(); 
}
function submitForm1()
{ 
	$("#committeeid").val("all").change();
}
function showAction(a,b){
	/* var y=JSON.stringify(a); */
	var y=$('#td'+a).val();
	console.log(a);
	$('#modalbody').html(y);
	$('#header').html(b);
	$('#exampleModalCenter').modal('show');
}

$(document).ready(function(){
	  $("#myTable12").DataTable({
	 "lengthMenu": [  5,10,25, 50, 75, 100 ],
	 "pagingType": "simple",
	 "pageLength": 10

	});
});

// PDF DOWNLOAD

 function downloadPDF() {
	
	var docDefinition = {
			info:{
				title:'Meeting Action Report',
			},
			pageOrientation: 'landscape',
			content: [
				
				/*********************************** MEETING ACTION REPORTS LIST START *********************************/
				{
					text: 'Meeting Action Report',
                    style: 'chapterHeader',
                    tocItem: false,
                    id: 'chapter1',
                    alignment: 'center',
                },
                
				{
                	table:{
                		headerRows: 1,
                		widths: ['7%', '15%', '5%', '10%', '14%', '7%', '10%', '10%', '5%', '7%', '10%'],
                		body: [
                            // Table header
                            [
                                { text: 'Project: ',bold: true,  },
                                { text: '<%if(projectName.length()>0) {%><%=projectName %> <% }else{ %> - <% }%>',  },
                                { text: '', },
                                
                                { text: 'Committee:',bold: true, }, 
                                { text: '<%if(committeName.length()>0) {%><%=committeName %> <% }else{ %> - <% }%>',  }, 
                                { text: '', },
                                
                                { text: 'Meeting:',bold: true, }, 
                                { text: '<%if(meetingName.length()>0) {%><%=meetingName %> <% }else{ %> - <% }%>',  }, 
                                { text: '', }, 
                                
                                { text: 'Status:',bold: true, }, 
                                { text: '<%if(statusName.length()>0) {%><%=statusName %> <% }else{ %> - <% }%>',  }, 
                            ],
                        ]
                	},
                	layout: {
                        /* fillColor: function(rowIndex) {
                            return (rowIndex % 2 === 0) ? '#f0f0f0' : null;
                        }, */
                        hLineWidth: function(i, node) {
                            return 0;
                        },
                        vLineWidth: function(i) {
                            return 0;
                        },
                        hLineColor: function(i) {
                            return '#aaaaaa';
                        },
                        vLineColor: function(i) {
                            return '#aaaaaa';
                        }
                    }
				},
				
				{text: '\n'},
                {
					table:{
						headerRows: 1,
                        widths: ['6%', '21%', '10%','23%', '15%', '15%', '10%'],
                        body : [
                        	// Table Header
                        	[
                        		 { text: 'SN', style: 'tableHeader' },
                                 { text: 'Action Id', style: 'tableHeader' },
                                 { text: 'PDC', style: 'tableHeader' },
                                 { text: 'Action Item', style: 'tableHeader'},
                                 { text: 'Assignee', style: 'tableHeader' }, 
                                 { text: 'Assignor', style: 'tableHeader' }, 
                                 { text: 'Progress', style: 'tableHeader' }, 
                        	],
                        	<% if(meetinglist!=null && meetinglist.size()>0){ 
                        			int slno = 0;
                        			int i=0;
                        			for(Object[] obj:meetinglist){                        			
                        	%>
	                        	[
	                                { text: '<%= ++slno %>', style: 'tableData',alignment: 'center' },
	                                { text: '<%= obj[0] %>', style: 'tableData',alignment: 'left' },
	                                { text: '<%=obj[6]!=null?sdf1.format(obj[6]):"-"%>', style: 'tableData',alignment: 'center' },
	                                { text: htmlToPdfmake('<%=obj[7].toString().replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "")%>'), style: 'tableData' },
	                                { text: htmlToPdfmake('<%=obj[1].toString().replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "")%>, <%=obj[2].toString().replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "")%>'), style: 'tableData' },
	                                { text: htmlToPdfmake('<%=obj[3].toString().replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "")%>, <%=obj[4].toString().replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", "")%>'), style: 'tableData' },
	                                { text: '<%=obj[11]!=null && !obj[11].toString().equalsIgnoreCase("0")?obj[11]+"%":"Not Yet Started"%>', style: 'tableData',alignment: 'center' },
	                            ],
                        	<% ++i;}} else {%>
                        		[{ text: 'No Data Available', style: 'tableData',alignment: 'center', colSpan: 7 },]
                        	<%} %>
                        ],
					},
					layout: {
                        /* fillColor: function(rowIndex) {
                            return (rowIndex % 2 === 0) ? '#f0f0f0' : null;
                        }, */
                        hLineWidth: function(i, node) {
                            return (i === 0 || i === node.table.body.length) ? 1 : 0.5;
                        },
                        vLineWidth: function(i) {
                            return 0.5;
                        },
                        hLineColor: function(i) {
                            return '#aaaaaa';
                        },
                        vLineColor: function(i) {
                            return '#aaaaaa';
                        }
                    },
                }
			],
			styles: {
				chapterHeader: { fontSize: 16, bold: true, margin: [0, 0, 0, 10] },
                tableHeader: { fontSize: 12, bold: true, fillColor: '#f0f0f0', alignment: 'center', margin: [10, 5, 10, 5], fontWeight: 'bold' },
                tableData: { fontSize: 11.5, margin: [0, 5, 0, 5] },
            },
            footer: function(currentPage, pageCount) {
                /* if (currentPage > 2) { */
                    return {
                        stack: [
                        	{
                                canvas: [{ type: 'line', x1: 30, y1: 0, x2: 820, y2: 0, lineWidth: 1 }]
                            },
                            {
                                columns: [
                                    { text: '', alignment: 'left', margin: [30, 0, 0, 0], fontSize: 8 },
                                    { text: currentPage.toString() + ' of ' + pageCount, alignment: 'right', margin: [0, 0, 30, 0], fontSize: 8 }
                                ]
                            },
                            { text: '', alignment: 'center', fontSize: 8, margin: [0, 5, 0, 0], bold: true }
                        ]
                    };
                /* }
                return ''; */
            },
            /* header: function (currentPage) {
                return {
                    stack: [
                        
                        {
                            columns: [
                                {
                                    // Center: Text
                                    text: 'Restricted',
                                    alignment: 'center',
                                    fontSize: 10,
                                    bold: true,
                                    margin: [0, 10, 0, 0]
                                },
                            ]
                        },
                        
                    ]
                };
            }, */
			pageMargins: [30, 40, 20, 20],
            
            defaultStyle: { fontSize: 12, color: 'black', }
        };
		
        const pdf = pdfMake.createPdf(docDefinition);
        /* pdf.download("Meeting Action Report"); */
        pdf.open();
} 

</script>


</html>