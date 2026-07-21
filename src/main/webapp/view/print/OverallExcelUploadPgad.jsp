<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>


</head>
<body>
<%
String projectid=(String)request.getAttribute("projectid");
String committeeid=(String)request.getAttribute("committeeid");
Object[] lastmeetingVenue =  (Object[]) request.getAttribute("lastmeetingVenue");
%>
<div class="modal fade" id="overallfinace" >
  <div class="modal-dialog modal-xl">
    <div class="modal-content">
      <div class="modal-header bg-primary text-light"  >
        <h5 class="modal-title" id="exampleModalLabel">Upload Overall Finance</h5>
        <div >
      <form action="#">
      <input type="hidden" id="mainprojectidDownload" name="mainprojectid" value="<%=projectid%>">
      <input type="hidden" name="committeeid" value="1">
      <input type="hidden" name="_csrf" value="ec4f5ad7-8aa0-4cfc-abf5-a08beb4c43a7">
       <button class="btn display-none" id="downloadExcelBtn"  type="submit" formaction="excelSheetWithFinanceData.htm"    data-toggle="tooltip"  data-toggle="tooltip" data-placement="top"  title="Download Excel Sheet with Data" ><i class="fa fa-download" aria-hidden="true"></i></button>
      </form>
      </div>
		<form action="OverallFinanceExcelPgad.htm" method="post">
		<button class="btn btn-sm"  data-toggle="tooltip" type="submit" data-toggle="tooltip" data-placement="top"  title="Download Format" ><i class="fa fa-download fa-lg" aria-hidden="true"></i>
		&nbsp; Sample Format
		</button>
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
		</form>
      </div>
       <form action="OverAllFinaceSubmitPgad.htm" method="post" id="excelForm" enctype="multipart/form-data">
      <div class="modal-body">
      <div class="col-md-4 mb-4">
      <input class="form-control" type="file" id="excel_file" name="filename" required="required"  accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">						
      </div>
      <table class="table table-bordered table-hover">
      <thead>
      <tr>
	    <th class="width120">Category No</th>
		<th class="width150">Category Name</th>
		<th class="width120">Allotment</th>
		<th class="width120">Sanction</th>
		<th class="width120">Balance</th>
		<th class="width120">SO Value</th>
		<th class="width120">Expenditure</th>
		<th class="width120">INR</th>
		<th class="width120">FE</th>
      </tr>
      </thead>
      <tbody id="overalltbody">
      <tr>
      <td colspan="16" class="text-center">No Data Available!</td>
      </tr>
      </tbody>
      </table>
      </div>
      <hr>
      <div align="center" class="m-2">
      <input type="hidden" id="financeprojectid" name="projectid" value="<%=projectid%>">
      <input type="hidden" id="mainprojectid" name="mainprojectid" value="<%=projectid%>">
      <input type="hidden" id="scheduleId" name="scheduleId" value="<%=lastmeetingVenue!=null && lastmeetingVenue[0]!=null ? lastmeetingVenue[0] : "0"%>">
      <input type="hidden" name="committeeid" value="<%=committeeid%>">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
      <button type="submit" class="btn btn-sm submit" onclick="return confirm('Are you sure to submit?')">Submit</button>
      <button type="button" class="btn btn-sm btn-secondary text-transform" data-dismiss="modal" >Close</button>
    
      </div>
      </form>

    </div>
  </div>
</div>
<script type="text/javascript">
function showModal(projectid,mainprojectid,a){
	$('#overallfinace').modal('show');
	$('#financeprojectid').val(projectid)
	$('#mainprojectid').val(mainprojectid)

	$('#exampleModalLabel').html('Upload Overall Finance( '+ a+' )');
	
	var data=$("#tbody"+mainprojectid).html();
	var trCount = $("#tbody" + mainprojectid).find("tr").length;
	excel_file.value = '';
	if(trCount>2){
		console.log("Hiii")
		$('#downloadExcelBtn').show();
		$('#mainprojectidDownload').val(mainprojectid)
		$('#overalltbody').html(data)
	}
}

var excel_file = document.getElementById('excel_file');

excel_file.addEventListener('change', (event) => {

    var reader = new FileReader();
    reader.readAsArrayBuffer(event.target.files[0]);

    reader.onload = function (event) {
    	 let finalData = []; 
    	
        var data = new Uint8Array(reader.result);
        var work_book = XLSX.read(data, { type: 'array' });
        var sheet_name = work_book.SheetNames;

        var sheet_data = XLSX.utils.sheet_to_json(
            work_book.Sheets[sheet_name[0]],
            { header: 1 }
        );

        console.log(sheet_data);

        var table_output = '';
        var duplicate = [];
        var checkExcel = 0;

        if (sheet_data.length > 0) {

            // HEADER VALIDATION
            const expectedHeaders = [
                "Category Name",
                "Allotment",
                "Sanction",
                "Balance",
                "SO Value",
                "Expenditure",
                "INR",
                "FE",
            ];

            for (let i = 0; i < expectedHeaders.length; i++) {
                if (!sheet_data[0][i] || sheet_data[0][i].trim() !== expectedHeaders[i]) {
                    checkExcel++;
                }
            }

            if (checkExcel > 0) {
                alert("Please download the correct template and upload.");
                $('#overalltbody').html('<tr><td colspan="8" class="text-center">No Data Available!</td></tr>');
                excel_file.value = '';
                return;
            }
            

            // DATA LOOP
            for (var row = 1; row < sheet_data.length; row++) {

                if (!sheet_data[row] || sheet_data[row].length === 0) continue;

                table_output += '<tr><td class="text-center">'+row+'</td>';

                // Duplicate check (Category Name)
                duplicate.push(sheet_data[row][0]);

                for (var cell = 0; cell < 8; cell++) {

                    let value = sheet_data[row][cell];

                    // Category Name validation
                    if (cell == 0 && (!value || value.toString().trim().length === 0)) {
                        alert("Category Name at row " + (row + 1) + " is blank!");
                        excel_file.value = '';
                        return;
                    }

                    if (!value) {
                        table_output += '<td class="text-right">0.00</td>';
                    } else {
                        if (cell > 0) {
                            table_output += '<td class="text-right">' + parseFloat(value || 0).toFixed(2) + '</td>';
                        } else {
                            table_output += '<td class="text-justify">' + value + '</td>';
                        }
                    }
                }

                table_output += '</tr>';
            }

            // DUPLICATE CHECK
            var map = {};
            var duplicates = [];

            for (let i = 0; i < duplicate.length; i++) {
                if (map[duplicate[i]]) {
                    duplicates.push(duplicate[i]);
                } else {
                    map[duplicate[i]] = true;
                }
            }

            if (duplicates.length > 0) {
                alert("Duplicate Category Names found: " + duplicates);
                excel_file.value = '';
                return;
            }

            // FINAL OUTPUT
            if (table_output.length > 0) {
                $('#overalltbody').html(table_output);
            } else {
                $('#overalltbody').html('<tr><td colspan="8" class="text-center">No Data in Excel</td></tr>');
                excel_file.value = '';
            }
        }
    }
});
</script>
</body>
</html>