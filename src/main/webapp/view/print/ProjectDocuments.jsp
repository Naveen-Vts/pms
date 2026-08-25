<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1"> 
<jsp:include page="../static/header.jsp"></jsp:include>

<spring:url value="/resources/css/print/projectDocuments.css" var="projectDocuments" />     
<link href="${projectDocuments}" rel="stylesheet" />
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">
            <div class="card shadow-nohover">
                <div class="row card-header">
                    <div class="col-md-6">
                        <h4>Project Document List</h4>
                    </div>
                    <div class="col-md-2"></div>
                    <div class="col-md-4 card-header-right"></div>
                </div>

                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-bordered table-hover table-striped table-condensed table-center" id="myTable">
                            <thead>
                                <tr>
                                    <th class="col-sn" data-field="0" tabindex="0">SN</th>
                                    <th class="col-name">Project Name</th>
                                    <th class="col-sheet">Sheet Number</th>
                                    <th class="col-action">Action</th>
                                </tr>
                            </thead>
                            <tbody class="text-center">
                                <tr>
                                    <td>1</td>
                                    <td class="text-left">Cost Estimate</td>
                                    <td>SHEET_01</td>
                                    <td><a href="##"><i class="fa fa-eye" aria-hidden="true"></i></a></td>
                                </tr>
                                <tr>
                                    <td>2</td>
                                    <td class="text-left">TRA AND PRI VERIFICATION / VETTING</td>
                                    <td>SHEET_02</td>
                                    <td><a href="##"><i class="fa fa-eye" aria-hidden="true"></i></a></td>
                                </tr>
                                <tr>
                                    <td>3</td>
                                    <td class="text-left">DMC Presentation</td>
                                    <td>SHEET_03</td>
                                    <td><a href="##"><i class="fa fa-eye" aria-hidden="true"></i></a></td>
                                </tr>
                                <tr>
                                    <td>4</td>
                                    <td class="text-left">Project / Programme Proposal</td>
                                    <td>SHEET_04</td>
                                    <td><a href="##"><i class="fa fa-eye" aria-hidden="true"></i></a></td>
                                </tr>
                                <tr>
                                    <td>5</td>
                                    <td class="text-left">CCS NOTING</td>
                                    <td>SHEET_05</td>
                                    <td><a href="##"><i class="fa fa-eye" aria-hidden="true"></i></a></td>
                                </tr>
                                <tr>
                                    <td>6</td>
                                    <td class="text-left">Project / Programme Sanction</td>
                                    <td>SHEET_06</td>
                                    <td><a href="ProjectSanctionPreview.htm"><i class="fa fa-eye" aria-hidden="true"></i></a></td>
                                </tr>
                                <tr>
                                    <td>7</td>
                                    <td class="text-left">Appointment of PD / Programme Director</td>
                                    <td>SHEET_07</td>
                                    <td><a href="##"><i class="fa fa-eye" aria-hidden="true"></i></a></td>
                                </tr>
                                <tr>
                                    <td>8</td>
                                    <td class="text-left">Annual Audit Statement of Expenditure</td>
                                    <td>SHEET_08</td>
                                    <td><a href="##"><i class="fa fa-eye" aria-hidden="true"></i></a></td>
                                </tr>
                                <tr>
                                    <td>9</td>
                                    <td class="text-left">Briefing Paper PMRB / EB / Apex / PJB / PMB</td>
                                    <td>SHEET_09</td>
                                    <td><a href="##"><i class="fa fa-eye" aria-hidden="true"></i></a></td>
                                </tr>
                                <tr>
                                    <td>10</td>
                                    <td class="text-left">Minutes of Meeting PMRB / EB / Apex / PJB / PMB</td>
                                    <td>SHEET_10</td>
                                    <td><a href="##"><i class="fa fa-eye" aria-hidden="true"></i></a></td>
                                </tr>
                                <tr>
                                    <td>11</td>
                                    <td class="text-left">SoC for PDC Extension</td>
                                    <td>SHEET_11</td>
                                    <td><a href="##"><i class="fa fa-eye" aria-hidden="true"></i></a></td>
                                </tr>
                                <tr>
                                    <td>12</td>
                                    <td class="text-left">SoC for Cost Revision / Re-allocation of Funds in Project</td>
                                    <td>SHEET_12</td>
                                    <td><a href="##"><i class="fa fa-eye" aria-hidden="true"></i></a></td>
                                </tr>
                                <tr>
                                    <td>13</td>
                                    <td class="text-left">SoC for Project Completed with Partial Success</td>
                                    <td>SHEET_13</td>
                                    <td><a href="##"><i class="fa fa-eye" aria-hidden="true"></i></a></td>
                                </tr>
                                <tr>
                                    <td>14</td>
                                    <td class="text-left">Audit of Statement of Accounts (Expenditure) and Administrative Closure of Project / Programme</td>
                                    <td>SHEET_14</td>
                                    <td><a href="##"><i class="fa fa-eye" aria-hidden="true"></i></a></td>
                                </tr>
                                <tr>
                                    <td>15</td>
                                    <td class="text-left">Govt. Letter for PDC extension</td>
                                    <td>SHEET_15</td>
                                    <td><a href="PdcExtention.htm"><i class="fa fa-eye" aria-hidden="true"></i></a></td>
                                </tr>
                                <tr>
                                    <td>16</td>
                                    <td class="text-left">Govt. Letter for Cost Revision / Re-allocation of Funds</td>
                                    <td>SHEET_16</td>
                                    <td><a href="ReallocationReport.htm"><i class="fa fa-eye" aria-hidden="true"></i></a></td>
                                </tr>
                                <tr>
                                    <td>17</td>
                                    <td class="text-left">Govt. Letter for Project closure</td>
                                    <td>SHEET_17</td>
                                    <td><a href="##"><i class="fa fa-eye" aria-hidden="true"></i></a></td>
                                </tr>
                                <tr>
                                    <td>18</td>
                                    <td class="text-left">Change of Project Nodal Lab to New Lab</td>
                                    <td>SHEET_18</td>
                                    <td><a href="##"><i class="fa fa-eye" aria-hidden="true"></i></a></td>
                                </tr>
                                <tr>
                                    <td>19</td>
                                    <td class="text-left">Change of Project Category During Project Currency</td>
                                    <td>SHEET_19</td>
                                    <td><a href="##"><i class="fa fa-eye" aria-hidden="true"></i></a></td>
                                </tr>
                                <tr>
                                    <td>20</td>
                                    <td class="text-left">Project proposal</td>
                                    <td>SHEET_20</td>
                                    <td><a href="##"><i class="fa fa-eye" aria-hidden="true"></i></a></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>
</body>


<script type="text/javascript">

$(document).ready(function(){	
	$("#myTable").DataTable({
		 "lengthMenu": [  25, 50, 75, 100 ],
		 "pagingType": "simple"
	});
});
  
</script>
</html>