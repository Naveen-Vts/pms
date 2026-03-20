<%@page import="com.vts.pfms.documents.model.IRSArrayMaster"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="com.vts.pfms.documents.model.FieldGroupMaster"%>
<%@page import="com.vts.pfms.documents.model.IGILogicalChannel"%>
<%@page import="com.vts.pfms.documents.model.IRSDocumentSpecifications"%>
<%@page import="com.vts.pfms.documents.model.IGILogicalInterfaces"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<jsp:include page="../static/header.jsp"></jsp:include>

	<!-- Summer Note -->
	<spring:url value="/resources/summernote-lite.js" var="SummernoteJs" />
	<spring:url value="/resources/summernote-lite.css" var="SummernoteCss" />
	<script src="${SummernoteJs}"></script>
	<link href="${SummernoteCss}" rel="stylesheet" />
	
<style type="text/css">
.left {
	text-align: left;
}
.center {
	text-align: center;
}
.right {
	text-align: right;
}
	
.customtable{
	border-collapse: collapse;
	width: 100%;
	margin: 1.5rem 0.5rem 0.5rem 0.5rem;
	overflow-y: auto; 
	overflow-x: auto;  
}
.customtable th{
	border: 1px solid #0000002b; 
	padding: 10px;
		background-color: #2883c0;
}
.customtable td{
	border: 1px solid #0000002b; 
	padding: 5px;
}

.customtable thead {
	text-align: center;
	color: white;
	position: sticky;
	top: 0; /* Keeps the header at the top */
	z-index: 10; /* Ensure the header stays on top of the body */
	/* background-color: white; */ /* For visibility */
}


.select2-container {
	width: 100% !important;
}

.form-label {
	font-weight: 800;
	font-size: 16px;
	color: #07689f;
}

.activitytable{
	border-collapse: collapse;
	width: 100%;
	border: 1px solid #0000002b; 
	margin-top: 1.2rem;
	overflow-y: auto; 
	overflow-x: auto;  
}
.activitytable th, .activitytable td{
	border: 1px solid #0000002b; 
	padding: 20px;
}
.activitytable th{

	vertical-align: middle;
}
.activitytable thead {
	text-align: center;
	background-color: #2883c0;
	color: white;
	position: sticky;
	top: 0; /* Keeps the header at the top */
	z-index: 10; /* Ensure the header stays on top of the body */
	/* background-color: white; */ /* For visibility */
}

/* Summer Note styles */
.note-editor {
	width: 100% !important;
	/* margin: 1rem 2.5rem; */
}

</style>
</head>
<body>
<%

	String docId = (String)request.getAttribute("docId");
	String docType = (String)request.getAttribute("docType");
	String documentNo = (String)request.getAttribute("documentNo");
	String projectId = (String)request.getAttribute("projectId");
	String irsSpecificationId = (String)request.getAttribute("irsSpecificationId");
	String icdDocId = (String)request.getAttribute("icdDocId");
	List<IGILogicalInterfaces> logicalInterfaceList = (List<IGILogicalInterfaces>)request.getAttribute("logicalInterfaceList"); 
	List<IGILogicalChannel> logicalChannelList = (List<IGILogicalChannel>)request.getAttribute("logicalChannelList"); 
	List<Object[]> irsSpecificationsList = (List<Object[]>)request.getAttribute("irsSpecificationsList"); 
	IRSDocumentSpecifications irsSpecifications = (IRSDocumentSpecifications)request.getAttribute("irsDocSpecifications"); 
	
	Long logicalInterfaceId = irsSpecifications!=null?irsSpecifications.getLogicalInterfaceId():0L;
	Long logicalChannelId = logicalInterfaceList.stream().filter(e -> e.getLogicalInterfaceId().equals(logicalInterfaceId)).map(e -> e.getLogicalChannelId()).findFirst().orElse(0L);

	List<Object[]> fieldMasterList = (List<Object[]>)request.getAttribute("fieldMasterList");
	List<Object[]> dataTypeMasterList = (List<Object[]>)request.getAttribute("dataTypeMasterList");
	List<Object[]> fieldDescriptionList = (List<Object[]>)request.getAttribute("fieldDescriptionList");
	List<FieldGroupMaster> fieldGroupList = (List<FieldGroupMaster>) request.getAttribute("fieldGroupList");
	List<IRSArrayMaster> arrayMasterList = (List<IRSArrayMaster>) request.getAttribute("arrayMasterList");
	
	Map<String, List<Object[]>> fieldGroupMap = new LinkedHashMap<>();
	fieldGroupMap.put("0", fieldMasterList);
    
	for(FieldGroupMaster fieldGroup : fieldGroupList) {
		
		List<Object[]> fieldMasterByType = fieldMasterList.stream()
										    .filter(e -> {
										    	if(e[18]!=null) {
										    		String fieldGroupIds = e[18].toString();
											        List<String> idList = Arrays.asList(fieldGroupIds.split(","));
											        return idList.contains(fieldGroup.getFieldGroupId()+"");
										    	}else{
										    		return false;
										    	}
										        
										    })
			    							.collect(Collectors.toList());
		
		fieldGroupMap.computeIfAbsent(fieldGroup.getFieldGroupId()+"", k -> new ArrayList<>()).addAll(fieldMasterByType);
	}
	
	int slno1 = 1;
%>

	<% String ses = (String) request.getParameter("result"); 
       String ses1 = (String) request.getParameter("resultfail");
       if (ses1 != null) { %>
        <div align="center">
            <div class="alert alert-danger" role="alert">
                <%= ses1 %>
            </div>
        </div>
    <% } if (ses != null) { %>
        <div align="center">
            <div class="alert alert-success" role="alert">
                <%= ses %>
            </div>
        </div>
    <% } %>
    
    <div class="container-fluid">
       
    	<div class="card shadow-nohover table-wrapper" style="margin-top: -0.6pc">
        	<div class="card-header" style="background: #C4DDFF; box-shadow: 2px 2px 2px grey;">
            	<div class="row">
               		<div class="col-md-7" align="left">
	                    <h5 id="text" style="margin-left: 1%; font-weight: 600">
	                      Specification Details - <%=documentNo %>
	                    </h5>
                	</div>
                	<div class="col-md-1 right" >
                	</div>
                    <div class="col-md-4 right" >
	                    <form action="#" id="inlineapprform">
					        <button type="submit" class="btn btn-sm add" name="arrayMasterId" value="0" formaction="IRSArrayMaster.htm" data-toggle="tooltip" title="Add">
								ARRAY MASTER
					        </button>
					        <button type="submit" class="btn btn-sm edit" formaction="IRSFieldDescModify.htm" data-toggle="tooltip" title="Modify">
								MODIFY FIELD DESC
					        </button>
					        <button type="submit" class="btn btn-info btn-sm shadow-nohover back" formaction="IRSSpecificationsDetails.htm" data-toggle="tooltip" title="Add">
								LIST / ADD NEW SPEC
					        </button>
					        <%if(irsSpecifications==null) {%>
						        <button type="submit" class="btn btn-info btn-sm shadow-nohover back" formaction="IRSDocumentDetails.htm" data-toggle="tooltip" title="Back">
						        	BACK
						        </button>
					        <%} %>
					        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
					        <input type="hidden" name="irsSpecificationId" value="0">
					        <input type="hidden" name="docId" value="<%=docId%>"> 
							<input type="hidden" name="docType" value="<%=docType%>"> 
							<input type="hidden" name="documentNo" value="<%=documentNo%>">
							<input type="hidden" name="projectId" value="<%=projectId%>">
							<input type="hidden" name="irsDocId" value="<%=docId%>">
					    </form>
                    </div>
            	</div>
        	</div>
        	<div class="card-body">
        			
        		<form action="IRSSpecificationsSubmit.htm" method="post" id="myform">
        			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        			<input type="hidden" name="docId" value="<%=docId %>" />
        			<input type="hidden" name="docType" value="<%=docType %>" />
        			<input type="hidden" name="documentNo" value="<%=documentNo %>" />
        			<input type="hidden" name="projectId" value="<%=projectId %>" />
        			<input type="hidden" name="irsSpecificationId" value="<%=irsSpecificationId %>" />
        			<input type="hidden" name="icdDocId" id="icdDocId" value="<%=icdDocId %>" />
	        		<div class="card">
	        			<div class="card-header left" style="background-color: #055C9D;">
      						<h5 class="text-white" style="font-weight: bold;">Specification Details</h5>
      					</div>
	        			<div class="card-body">
	        				<div class="form-group">
	        					<div class="row">
	        						<div class="col-md-2">
	        							<label class="form-label">Logical Channel<span class="mandatory">*</span></label>
		        						<select class="form-control selectdee logicalInterfaceId" name="logicalChannelId" id="logicalChannelId" onchange="getDataCarryingConnectionList()"
		        						data-placeholder="---------Select------------" required <%if(irsSpecifications!=null) {%>disabled<%} %>>
											<option value="0" selected disabled>----select----</option>
									        <% for(IGILogicalChannel channel : logicalChannelList){ %>
									        	<option value="<%=channel.getLogicalChannelId() %>" <%if(logicalChannelId.equals(channel.getLogicalChannelId())) {%>selected<%} %>
									        	data-sourceid="<%=channel.getSourceId() %>"
									        	data-destinationid="<%=channel.getDestinationId() %>">
									        	<%=channel.getLogicalChannel()+" ("+channel.getChannelCode()+")" %>
									        	</option>
									        <%} %>
										</select>
        							</div>
	        						<div class="col-md-2">
	        							<label class="form-label">Message<span class="mandatory">*</span></label>
		        						<select class="form-control selectdee logicalInterfaceId" name="logicalInterfaceId" id="logicalInterfaceId" data-placeholder="---------Select------------" required <%if(irsSpecifications!=null) {%>disabled<%} %>>
											<option value="0" selected disabled>----select----</option>
											<%if(irsSpecifications!=null) { 
												List<IGILogicalInterfaces> logicalInterfaceListByType = logicalInterfaceList.stream().filter(e -> e.getLogicalChannelId().equals(logicalChannelId)).collect(Collectors.toList());
												for(IGILogicalInterfaces iface : logicalInterfaceListByType){ %>
										        	<option value="<%=iface.getLogicalInterfaceId() %>" <%if(irsSpecifications.getLogicalInterfaceId().equals(iface.getLogicalInterfaceId())) {%>selected<%} %>>
										        		<%-- <%=iface.getMsgName()+" ("+iface.getMsgCode()+")" %> --%>
										        		<%=iface.getMsgName() %>
										        	</option>
										        <%} %>
									        <%} %>
										</select>
        							</div>
        							<div class="col-md-2">
	        							<label class="form-label">Connection<span class="mandatory">*</span></label>
		        						<select class="form-control connectorPinMapId" name="connectorPinMapId" id="connectorPinMapId"
		        						data-placeholder="---------Select------------" data-live-search="true" data-container="body" required <%if(irsSpecifications!=null) {%>disabled<%} %> >
											<option value="" disabled selected>Choose...</option>
										</select>
        							</div>
        							<div class="col-md-6">
	        							<label class="form-label">Action at Destination<span class="mandatory">*</span></label>
	        							<div id="actionAtDestEditor" class="center"></div>
										<textarea name="actionAtDest" style="display: none;"></textarea>
	        						</div>
	        					</div>
	        				</div>
	        					
	        				<div class="center">
	        					<%if(irsSpecificationId.equalsIgnoreCase("0")) {%>
		        					<button type="submit" class="btn btn-sm submit" onclick="return confirm('Are you Sure to Submit?')">
		        						SUBMIT
		        					</button>
	        					<%} else{%>
	        						<button type="submit" class="btn btn-sm edit" onclick="return confirm('Are you Sure to Update?')">
		        						UPDATE
		        					</button>
	        					<%} %>
	        				</div>	
        				</div>
		        	</div>		
		        </form>
		        
	        	<%if(irsSpecifications!=null) { %>
                    <div class="card">			
	                    <div class="card-header left" style="background-color: #055C9D;">
	   						<h6 class="text-white" style="font-weight: bold;">Field Description</h6>
	   					</div>	 
                  		<div class="card-body" style="padding: 0rem;">	 	
            				<table class="table table-bordered" style="width: 100%;" >
								<thead class="center" style="background-color: #3b70b1;color: #fff;">
									<tr>
										<!-- <th >SN</th> -->
										<th >Group</th>
										<th >Field Name</th>
										<th >Data Type (bits)</th>
										<th >Typical Value</th>
										<th >Min Value</th>
										<th >Max Value</th>
										<th >Init Value</th>
										<th >Quantum</th>
										<th >Unit</th>
										<th >Description</th>
										<th >Remarks</th>
										<th >Group Variable</th>
										<th >Array Name</th>
										<th >Action</th>
									</tr>
								</thead>
								<tbody>
									<%
									fieldDescriptionList = fieldDescriptionList.stream().filter(e -> irsSpecifications.getLogicalInterfaceId()==Long.parseLong(e[1].toString()) && irsSpecifications.getIRSSpecificationId()==Long.parseLong(e[19].toString())).collect(Collectors.toList());
									long temp = -1;
									int fieldSlNoMax = 0;
									if(fieldDescriptionList!=null && fieldDescriptionList.size()>0) {
										for(Object[] desc : fieldDescriptionList) {
											long groupId = desc[12]!=null? Long.parseLong(desc[12].toString()): 0;
											int fieldSlNo = desc[29]!=null?Integer.parseInt(desc[29].toString()):0;
											if(fieldSlNo>fieldSlNoMax){
												fieldSlNoMax = fieldSlNo;
											}
									%>
										<tr>
											<%-- <td>
												<input form="slnoupdateform" type="number" class="form-control" name="newslno" value="<%=fieldSlNo %>" min="1" max="<%=fieldDescriptionList.size()%>" required/>
												<input form="slnoupdateform" type="hidden" name="irsFieldDescId" value="<%=desc[0] %>" />
											</td> --%>
											<td>
												<select class="form-control selectitem fieldGroupId" name="fieldGroupId" id="fieldGroupIdEdit_<%=slno1 %>" data-live-search="true" data-container="body" required disabled="disabled">
						               				<option value="" disabled selected>Choose...</option>
						               				<option value="0" <%if(groupId==0) {%>selected<%} %>>Not Applicable</option>
						               				<%for(FieldGroupMaster fieldGroup : fieldGroupList){
						                			 %>
														<option value="<%=fieldGroup.getFieldGroupId()%>" <%if(groupId==(fieldGroup.getFieldGroupId())) {%>selected<%} %> >
															<%=fieldGroup.getGroupName()+" ("+fieldGroup.getGroupCode()+")" %>
														</option>
													<%} %>
												</select>
												<input form="inlineupdateform_<%=slno1 %>" type="hidden" class="hiddenFieldGroupId" name="fieldGroupId" value="<%=groupId %>"/>
											</td>
											<td>
												<select class="form-control selectitem fieldMasterId" name="fieldMasterId" id="fieldMasterIdEdit_<%=slno1 %>" data-live-search="true" data-container="body" required disabled="disabled">
						               				<option value="" disabled selected>Choose...</option>
						               				<%for(Object[] obj : fieldMasterList ){
						                			 %>
														<option value="<%=obj[0]%>" <%if(desc[2]!=null && Long.parseLong(desc[2].toString())==Long.parseLong(obj[0].toString())) {%>selected<%} %> >
															<%=obj[1] %>
														</option>
													<%} %>
												</select>
												<input type="hidden" class="hiddenFieldMasterId" name="fieldMasterId" value="<%=desc[2] != null ? desc[2] : "" %>"/>
											</td>	
											<td>
												<input type="text" class="form-control dataType" name="dataType" id="dataTypeEdit_<%=slno1 %>" <%if(desc[27]!=null) {%> value="<%=desc[27] %>" <%} %> readonly>
											</td>	
											<td>
												<input type="text" class="form-control typicalValue" name="typicalValue" id="typicalValueEdit_<%=slno1 %>" value="<%=desc[13] %>" maxlength="255" readonly>
											</td>
											<td>
												<input type="text" class="form-control minValue" name="minValue" id="minValueEdit_<%=slno1 %>" value="<%=desc[14] %>" maxlength="255" readonly>
											</td>
											<td>
												<input type="text" class="form-control maxValue" name="maxValue" id="maxValueEdit_<%=slno1 %>" value="<%=desc[15] %>" maxlength="255" readonly>
											</td>
											<td>
												<input type="text" class="form-control initValue" name="initValue" id="initValueEdit_<%=slno1 %>" value="<%=desc[16] %>" maxlength="255" readonly>
											</td>
											<td>
												<input type="text" class="form-control quantum" name="quantum" id="quantumEdit_<%=slno1 %>" value="<%=desc[6] %>" maxlength="255" readonly>
											</td>
											<td>
												<input type="text" class="form-control unit" name="unit" id="unitEdit_<%=slno1 %>" value="<%=desc[18] %>" maxlength="255" readonly>
											</td>	
											<td>
												<input type="text" class="form-control description" name="description" id="descriptionEdit_<%=slno1 %>" value="<%=desc[5] %>" maxlength="255" readonly>
											</td>	
											<td>
												<input type="text" class="form-control remarks" name="remarks" id="remarksEdit_<%=slno1 %>" value="<%=desc[7] %>" maxlength="500" readonly>
											</td>	
											<%if(groupId!=temp && groupId!=0) { temp = groupId;%>
												<td style="border-bottom: none;">
													<input form="inlineupdateform_<%=slno1 %>" type="text" class="form-control groupVariable" name="groupVariable" id="groupVariableEdit_<%=slno1 %>" value="<%=desc[26]!=null?desc[26]:"" %>" maxlength="255" <%if(desc[12]!=null && Long.parseLong(desc[12].toString())==0) {%>readonly<%} %> required>
												</td>	
												<td style="border-bottom: none;">
													<select form="inlineupdateform_<%=slno1 %>" class="form-control selectitem arrayMasterId" name="arrayMasterId" id="arrayMasterIdEdit_<%=slno1 %>" data-live-search="true" data-container="body" required>
							               				<option value="" disabled selected>Choose...</option>
							               				<option value="0" <%if(desc[20]!=null && Long.parseLong(desc[20].toString())==0) {%>selected<%} %>>Not Applicable</option>
							               				<%for(IRSArrayMaster arr : arrayMasterList){
							                			 %>
															<option value="<%=arr.getArrayMasterId()%>" <%if(desc[20]!=null && Long.parseLong(desc[20].toString())==(arr.getArrayMasterId())) {%>selected<%} %> >
																<%=arr.getArrayName() %> (<%=arr.getArrayValue() %>)
															</option>
														<%} %>
													</select>
												</td>
												<td class="center" style="border-bottom: none;">
													<form action="#" method="post" id="inlineupdateform_<%=slno1 %>">
									        			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									        			<input type="hidden" name="docId" value="<%=docId %>" />
									        			<input type="hidden" name="docType" value="<%=docType %>" />
									        			<input type="hidden" name="documentNo" value="<%=documentNo %>" />
									        			<input type="hidden" name="projectId" value="<%=projectId %>" />
									        			<input type="hidden" name="irsSpecificationId" value="<%=irsSpecificationId %>" />
									        			<input type="hidden" name="irsFieldDescId" value="<%=desc[0] %>" />
													
														<button type="submit" class="editable-clicko" formaction="IRSFieldDescUpdate.htm" formmethod="post" data-toggle="tooltip" title="Edit"
														onclick="return confirm('Are you sure to Update?')">
											            	<i class="fa fa-lg fa-edit" style="padding: 0px;color: darkorange;font-size: 25px;" aria-hidden="true"></i>
											       		</button>
											        	<button type="submit" class="editable-clicko" formaction="IRSFieldDescDelete.htm" formmethod="post" formnovalidate="formnovalidate" data-toggle="tooltip" title="Delete"
											       	 	onclick="return confirm('Are you sure to Delete?')">
											            	<i class="fa fa-lg fa-trash" style="padding: 0px;color: red;font-size: 25px;" aria-hidden="true"></i>
											        	</button>
											        </form>
												</td>
											<%} else if(groupId==0) { temp = groupId;%>
												<td style="border-bottom: none;">
													<input form="inlineupdateform_<%=slno1 %>" type="text" class="form-control groupVariable" name="groupVariable" id="groupVariableEdit_<%=slno1 %>" value="<%=desc[26]!=null?desc[26]:"" %>" maxlength="255" <%if(desc[12]!=null && Long.parseLong(desc[12].toString())==0) {%>readonly<%} %> required>
												</td>	
												<td style="border-bottom: none;">
													<select form="inlineupdateform_<%=slno1 %>" class="form-control selectitem arrayMasterId" name="arrayMasterId" id="arrayMasterIdEdit_<%=slno1 %>" data-live-search="true" data-container="body" required>
							               				<option value="" disabled selected>Choose...</option>
							               				<option value="0" <%if(desc[20]!=null && Long.parseLong(desc[20].toString())==0) {%>selected<%} %>>Not Applicable</option>
							               				<%for(IRSArrayMaster arr : arrayMasterList){
							                			 %>
															<option value="<%=arr.getArrayMasterId()%>" <%if(desc[20]!=null && Long.parseLong(desc[20].toString())==(arr.getArrayMasterId())) {%>selected<%} %> >
																<%=arr.getArrayName() %> (<%=arr.getArrayValue() %>)
															</option>
														<%} %>
													</select>
												</td>
												<td class="center" style="border-bottom: none;">
													<form action="#" method="post" id="inlineupdateform_<%=slno1 %>">
									        			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									        			<input type="hidden" name="docId" value="<%=docId %>" />
									        			<input type="hidden" name="docType" value="<%=docType %>" />
									        			<input type="hidden" name="documentNo" value="<%=documentNo %>" />
									        			<input type="hidden" name="projectId" value="<%=projectId %>" />
									        			<input type="hidden" name="irsSpecificationId" value="<%=irsSpecificationId %>" />
									        			<input type="hidden" name="irsFieldDescId" value="<%=desc[0] %>" />
													
														<button type="submit" class="editable-clicko" formaction="IRSFieldDescUpdate.htm" formmethod="post" data-toggle="tooltip" title="Edit"
														onclick="return confirm('Are you sure to Update?')">
											            	<i class="fa fa-lg fa-edit" style="padding: 0px;color: darkorange;font-size: 25px;" aria-hidden="true"></i>
											       		</button>
											        	<button type="submit" class="editable-clicko" formaction="IRSFieldDescDelete.htm" formmethod="post" formnovalidate="formnovalidate" data-toggle="tooltip" title="Delete"
											       	 	onclick="return confirm('Are you sure to Delete?')">
											            	<i class="fa fa-lg fa-trash" style="padding: 0px;color: red;font-size: 25px;" aria-hidden="true"></i>
											        	</button>
											        </form>
												</td>
											<%} %>		
										</tr>
										
									<%++slno1;}%>
										<%-- <tr>
							              	<td colspan="1" style="display: flex;justify-content: center;align-items: center">
							              		<form action="IRSFieldDescSerialNoUpdate.htm" method="POST" id="slnoupdateform">
									              	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									              	<input type="hidden" name="irsSpecificationId" value="<%=irsSpecificationId%>">
									              	<input type="hidden" name="logicalInterfaceId" value="<%=logicalInterfaceId%>">
									              	<input type="hidden" name="docId" value="<%=docId %>" />
								        			<input type="hidden" name="docType" value="<%=docType %>" />
								        			<input type="hidden" name="documentNo" value="<%=documentNo %>" />
								        			<input type="hidden" name="projectId" value="<%=projectId %>" />
									              	<button type="submit" class="btn btn-sm edit" onclick="return slnocheck();">UPDATE</button>
								              	</form>
							              	</td>
							              	<td colspan="14"></td>
						              	</tr> --%>
									<%} else {%>
										<tr>
											<td colspan="14" class="center">No Data Available</td>
										</tr>
									<%} %>
								</tbody>
							</table>
						</div>
					</div>		
						
					<div class="left">
						<h5 class="ccm-agenda-field" style="text-decoration: underline;font-weight: bold;">Add More Field Description : </h5>
					</div>	
					
					<form action="IRSNewFieldDescSubmit.htm" method="post" id="myform3">
	        			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	        			<input type="hidden" name="docId" value="<%=docId %>" />
	        			<input type="hidden" name="docType" value="<%=docType %>" />
	        			<input type="hidden" name="documentNo" value="<%=documentNo %>" />
	        			<input type="hidden" name="projectId" value="<%=projectId %>" />
	        			<input type="hidden" name="irsSpecificationId" value="<%=irsSpecificationId %>" />
	        			<input type="hidden" name="slno" value="<%=fieldSlNoMax%>">
					        <div class="card">
					        	<div class="card-body" style="padding: 0rem;">
	              					<table id="fielddesctable" class="table table-bordered fielddesctable" style="width: 100%;" >
										<thead class="center" style="background-color: #3b70b1;color: #fff;">
											<tr>
												<th >Group</th>
												<th >Field Name</th>
												<th >Data Type</th>
												<th >Typical Value</th>
												<th >Min Value</th>
												<th >Max Value</th>
												<th >Init Value</th>
												<th >Quantum</th>
												<th >Unit</th>
												<th >Description</th>
												<th >Remarks</th>
												<th >Group Variable</th>
												<th >Array Name</th>
												<td >
													<button type="button" class=" btn btn-sm btn_add_fielddesc "><i class="btn btn-sm fa fa-plus" style="color: green;"></i></button>
												</td>
											</tr>
										</thead>
										<tbody>
											<tr class="tr_clone_fielddesc">
												<td>
													<select class="form-control selectitem fieldGroupId" name="fieldGroupId" id="fieldGroupId_1" data-live-search="true" data-container="body" required>
							               				<option value="" disabled selected>Choose...</option>
							               				<option value="0" >Not Applicable</option>
							               				<%for(FieldGroupMaster fieldGroup : fieldGroupList){
							                			 %>
															<option value="<%=fieldGroup.getFieldGroupId()%>">
																<%=fieldGroup.getGroupName()+" ("+fieldGroup.getGroupCode()+")" %>
															</option>
														<%} %>
													</select>
												</td>
												<td>
													<select class="form-control selectitem fieldMasterId" name="fieldMasterId" id="fieldMasterId_1" data-live-search="true" data-container="body" required>
							               				
													</select>
												</td>	
												<td>
													<input type="text" class="form-control dataType" name="dataType" id="dataType_1" readonly>
												</td>	
												<td>
													<input type="text" class="form-control typicalValue" name="typicalValue" id="typicalValue_1" maxlength="255" readonly>
												</td>
												<td>
													<input type="text" class="form-control minValue" name="minValue" id="minValue_1" maxlength="255" readonly>
												</td>
												<td>
													<input type="text" class="form-control maxValue" name="maxValue" id="maxValue_1" maxlength="255" readonly>
												</td>
												<td>
													<input type="text" class="form-control initValue" name="initValue" id="initValue_1" maxlength="255" readonly>
												</td>
												<td>
													<input type="text" class="form-control quantum" name="quantum" id="quantum_1" maxlength="255" readonly>
												</td>
												<td>
													<input type="text" class="form-control unit" name="unit" id="unit_1" maxlength="255" readonly>
												</td>	
												<td>
													<input type="text" class="form-control description" name="description" id="description_1" maxlength="255" readonly>
												</td>	
												<td>
													<input type="text" class="form-control remarks" name="remarks" id="remarks_1" maxlength="500" readonly>
												</td>	
												<td>
													<input type="text" class="form-control groupVariable" name="groupVariable" id="groupVariable_1" maxlength="255" readonly required>
												</td>	
												<td>
													<select class="form-control selectitem arrayMasterId" name="arrayMasterId" id="arrayMasterId_1" data-live-search="true" data-container="body" required>
							               				<option value="" disabled selected>Choose...</option>
							               				<option value="0">Not Applicable</option>
							               				<%for(IRSArrayMaster arr : arrayMasterList){
							                			 %>
															<option value="<%=arr.getArrayMasterId()%>" >
																<%=arr.getArrayName() %> (<%=arr.getArrayValue() %>)
															</option>
														<%} %>
													</select>
												</td>	
												<td class="center">
													<button type="button" class=" btn btn-sm btn_rem_fielddesc" > <i class="btn btn-sm fa fa-minus" style="color: red;"></i></button>
												</td>		
											</tr>
										</tbody>
									</table>
									
									<div class="center">
			        					<button type="submit" class="btn btn-sm submit" onclick="return confirm('Are you Sure to Submit?')">
			        						SUBMIT
			        					</button>
			        				</div>	
								</div>
							</div>
						</form>
            		<%} %>
	        			
   				<div>
   					<span class="mandatory">Note: </span>
   					<span>Field Description values can be modified by using "MODIFY FIELD DESC" button.</span>
   				</div>
		        		
	        		
        		<%if(irsSpecifications==null) { %>
        			<hr class="mt-3 mb-3">
	        		<div class="table-responsive"> 
	        			<!-- <input type="text" id="searchBar" class="search-bar form-control" placeholder="Search..." style="float: right;width: auto;" />
	       				<br> -->
	                	<table class="table table-bordered table-hover table-striped table-condensed dataTable" id="myTable1">
	                    	<thead class="center">
	                    		<tr>
	                    			<th>SN</th>
	                    			<th>Connection ID</th>
	                    			<th>Message Id</th>
	                    			<th>Action at Dest</th>
	                    			<th>Action</th>
		                    	</tr>
	                    	</thead>
	                    	<tbody>
	                    		<%if(irsSpecificationsList!=null && irsSpecificationsList.size()>0) {
	                    			int slno = 0;
	                    			for(Object[] obj : irsSpecificationsList) {
	                    				String[] split = obj[6].toString().split("_");
	                    		%>
	                    			<tr>
	                    				<td class="center"><%=++slno %></td>
	                    				<td><%=obj[6] %></td>
	                    				<td><%=obj[7] %></td>
	                    				<td><%=obj[5] %></td>
	                    				<td class="center">
	                    					<form action="#" id="inlineapprform<%=slno%>">
							      			 
										        <%-- <button type="button" onclick="openConnectionEditModal('<%=slno%>')">
										            <img src="view/images/edit.png" alt="Edit">
										        </button> --%>
										        <button type="submit" class="editable-clicko" formaction="IRSSpecificationsDetails.htm" formmethod="post" data-toggle="tooltip" title="Edit">
										            <i class="fa fa-lg fa-edit" style="padding: 0px;color: darkorange;font-size: 25px;" aria-hidden="true"></i>
										        </button>
										        <button type="submit" class="editable-clicko" formaction="IRSSpecificationDelete.htm" formmethod="post" data-toggle="tooltip" title="Delete" onclick="return confirm('Are you sure to Delete?')">
										            <i class="fa fa-lg fa-trash" style="padding: 0px;color: red;font-size: 25px;" aria-hidden="true"></i>
										        </button>
										        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
										        <input type="hidden" name="irsSpecificationId" id="irsSpecificationId_<%=slno%>" value="<%=obj[0] %>">
										        <input type="hidden" name="docId" value="<%=docId%>"> 
												<input type="hidden" name="docType" value="<%=docType%>"> 
												<input type="hidden" name="documentNo" value="<%=documentNo%>">
												<input type="hidden" name="projectId" value="<%=projectId%>">
										    </form>
	                    				</td>
	                    			</tr>
								<%} }%>
	                    	</tbody>
	                    </table>	
	            	</div>
				<%} %>		            	
        	</div>
        </div>
 	</div>
 
<script type="text/javascript">

	$('#myTable1').DataTable({
	    "lengthMenu": [10, 25, 50, 75, 100],
	    "pagingType": "simple",
	    "pageLength": 10
	});
 	
 	// Define a common Summernote configuration
	var summernoteConfig = {
	    width: 900,
	    toolbar: [
	        ['style', ['bold', 'italic', 'underline', 'clear']],
	        ['font', ['fontsize', 'fontname', 'color', 'superscript', 'subscript']],
	        ['insert', ['picture', 'table']],
	        ['para', ['ul', 'ol', 'paragraph']],
	        ['height', ['height']]
	    ],
	    fontSizes: ['8', '9', '10', '11', '12', '14', '16', '18', '24', '36', '48', '64', '82', '150'],
	    fontNames: ['Arial', 'Arial Black', 'Comic Sans MS', 'Courier New', 'Helvetica', 'Impact', 'Tahoma', 'Times New Roman', 'Verdana','Segoe UI','Segoe UI Emoji','Segoe UI Symbol'],
	    buttons: {
	        superscript: function() {
	            return $.summernote.ui.button({
	                contents: '<sup>S</sup>',
	                tooltip: 'Superscript',
	                click: function() {
	                    document.execCommand('superscript');
	                }
	            }).render();
	        },
	        subscript: function() {
	            return $.summernote.ui.button({
	                contents: '<sub>S</sub>',
	                tooltip: 'Subscript',
	                click: function() {
	                    document.execCommand('subscript');
	                }
	            }).render();
	        }
	    },
	};

	// Action at Destination Configure
	$('#actionAtDestEditor').summernote(summernoteConfig);
	
	// Update the values of Editors
	var html1 = '<%=irsSpecifications!=null && irsSpecifications.getActionAtDest()!=null?irsSpecifications.getActionAtDest().replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"").replaceAll("\n", "<br>").replaceAll("\r", ""):""%>';
	$('#actionAtDestEditor').summernote('code', html1);
	
	// Set the values to the form when submitting.
	$('#myform').submit(function() {

		 var data1 = $('#actionAtDestEditor').summernote('code');
		 $('textarea[name=actionAtDest]').val(data1);
		
	});
	
	var interfaceContentMap = {};
    <% for (IGILogicalChannel channel : logicalChannelList) { %>
        var ifaceList = [];
        <% for (IGILogicalInterfaces iface : logicalInterfaceList) { 
            if (iface.getLogicalChannelId().equals(channel.getLogicalChannelId())) { 
            %>
                ifaceList.push({
                    value: "<%= iface.getLogicalInterfaceId() %>",
                    text: "<%= iface.getMsgName() %>"
                });
        <% } } %>
        interfaceContentMap["<%=channel.getLogicalChannelId() %>"] = ifaceList;
    <% } %>

    $(document).ready(function () {
        // Initialize both selects
        $('.selectdee').select2({
            width: '100%'
        });

        // Handle dropdown change
        $(document).on('change', '#logicalChannelId', function () {
            const selectedInterfaceType = $(this).val();
            const $contentDropdown = $('#logicalInterfaceId');

            $contentDropdown.empty().append('<option value="0" selected disabled>----select----</option>');

            const ifaceList = interfaceContentMap[selectedInterfaceType] || [];
            ifaceList.forEach(iface => {
                $contentDropdown.append(new Option(iface.text, iface.value));
            });

            $contentDropdown.trigger('change.select2');
        });
    });
    
    var fieldGroupMap = {};
    <% for (Map.Entry<String, List<Object[]>> entry : fieldGroupMap.entrySet()) { %>
        fieldGroupMap["<%= entry.getKey() %>"] = [
            <% for (Object[] field : entry.getValue()) { %>
                {
                	value: "<%= field[0] %>",
                    text: "<%= field[1] %>",
                    datatype: "<%= field[16] %>",
                    typicalval: "<%= field[6] %>",
                    minval: "<%= field[7] %>",
                    maxval: "<%= field[8] %>",
                    initval: "<%= field[9] %>",
                    quantum: "<%= field[11] %>",
                    unit: "<%= field[12] %>",
                    description: "<%= field[4] %>",
                    remarks: "<%= field[13] %>"
                },
            <% } %>
        ];
    <% } %>
    
	/* Cloning (Adding) the table body rows */
	let fieldDescIndex = '<%=slno1 %>'; // Start from 1 if one row is already present

	$("#fielddesctable").on('click', '.btn_add_fielddesc', function () {
	    const $last = $('.tr_clone_fielddesc').last();
	    const $clone = $last.clone();

	    fieldDescIndex++;

	 	// Remove hidden inputs from the cloned row
	    $clone.find('.hiddenFieldGroupId, .hiddenFieldMasterId').remove();
	 
	    // Clean up old select2 from clone
	    $clone.find('.select2-container').remove();
	    $clone.find('.fieldGroupId, .fieldMasterId, .arrayMasterId')
	        .removeClass('select2-hidden-accessible')
	        .removeAttr('data-select2-id')
	        .removeAttr('aria-hidden')
	        .removeAttr('tabindex');

	    // Update IDs and reset values
	    $clone.find('.fieldGroupId')
	        .val('')
	        .attr('id', 'fieldGroupId_' + fieldDescIndex)
	        .prop('disabled', false)
	        
	    $clone.find('.fieldMasterId')
	        .val('')
	        .attr('id', 'fieldMasterId_' + fieldDescIndex)
	        .prop('disabled', false)
	        .html('<option value="" selected disabled>Choose...</option>');

	    $clone.find('.arrayMasterId')
        	.val('')
        	.attr('id', 'arrayMasterId_' + fieldDescIndex)
        
        $clone.find('.dataType').val('').attr('id', 'dataType_' + fieldDescIndex);
        $clone.find('.typicalValue').val('').attr('id', 'typicalValue_' + fieldDescIndex);
        $clone.find('.minValue').val('').attr('id', 'minValue_' + fieldDescIndex);
        $clone.find('.maxValue').val('').attr('id', 'maxValue_' + fieldDescIndex);
        $clone.find('.initValue').val('').attr('id', 'initValue_' + fieldDescIndex);
        $clone.find('.quantum').val('').attr('id', 'quantum_' + fieldDescIndex);
        $clone.find('.unit').val('').attr('id', 'unit_' + fieldDescIndex);
        $clone.find('.description').val('').attr('id', 'description_' + fieldDescIndex);
        $clone.find('.remarks').val('').attr('id', 'remarks_' + fieldDescIndex);
        $clone.find('.groupVariable').val('').attr('id', 'groupVariable_' + fieldDescIndex).prop('readonly', true);
	    
	    // Reset any inputs
	    $clone.find("input[type='text'], textarea").val('');
	    $clone.find("input[type='number']").val('0');

	    // Add to DOM
	    $last.after($clone);

	    // Reinitialize all select2 (important!)
	    initializeSelect2ForAll();
	});

	/* Cloning (Removing) the table body rows */
	$("#fielddesctable").on('click','.btn_rem_fielddesc' ,function() {
		
		var cl=$('.tr_clone_fielddesc').length;
			
		if(cl>1){
		   var $tr = $(this).closest('.tr_clone_fielddesc');
		  
		   var $clone = $tr.remove();
		   $tr.after($clone);
		   
		}
	 
	});


	function initializeSelect2ForAll() {
	    // Clean up and reinitialize all select2
	    $('.fieldGroupId, .fieldMasterId, .arrayMasterId').each(function () {
	        // Destroy existing Select2 if initialized
	        if ($(this).hasClass("select2-hidden-accessible")) {
	            $(this).select2('destroy');
	        }
	        // Reinitialize
	        $(this).select2({ width: '100%' });
	    });
	}
	
	function handleFieldGroupChange(rowId) {
	    const selectedGroupId = $('#fieldGroupId_' + rowId).val();
	    const $originalRow = $('#fieldGroupId_' + rowId).closest('tr');
	    const fieldList = fieldGroupMap[selectedGroupId] || [];

	    // Clear the fieldMaster dropdown and populate options
	    const $fieldDropdown = $('#fieldMasterId_' + rowId);
	    $fieldDropdown.empty().append('<option value="" selected disabled>Choose...</option>');

	 	// Always clear data fields first for safety
	    $originalRow.find('.dataType').val('');
	    $originalRow.find('.typicalValue').val('');
	    $originalRow.find('.minValue').val('');
	    $originalRow.find('.maxValue').val('');
	    $originalRow.find('.initValue').val('');
	    $originalRow.find('.quantum').val('');
	    $originalRow.find('.unit').val('');
	    $originalRow.find('.description').val('');
	    $originalRow.find('.remarks').val('');
	    $originalRow.find('.groupVariable').val('');
	    $originalRow.find("input[type='text'], textarea").val('');
	    $originalRow.find("input[type='number']").val('0');
	    
	    if (selectedGroupId === '0' || fieldList.length === 0) {

	        fieldList.forEach(field => {
	            $fieldDropdown.append(new Option(field.text, field.value));
	        });

	        // Reinitialize select2 only for this dropdown
	        initializeSelect2ForAll();
	        return;
	    }

	    let $lastInsertedRow = $originalRow; // Start from the original row

	    // If multiple fields exist, populate current row and clone only remaining ones
	    fieldList.forEach((field, index) => {
	        if (index === 0) {
	            // First item: update current row
	            $fieldDropdown.append(new Option(field.text, field.value, true, true));
	            $originalRow.find('.fieldGroupId').val(selectedGroupId);
	            $originalRow.find("input[type='text'], textarea").val('');
	            $originalRow.find("input[type='number']").val('0');
	            
	            $originalRow.find('.dataType').val(field.datatype || '');
	            $originalRow.find('.typicalValue').val(field.typicalval || '');
	            $originalRow.find('.minValue').val(field.minval || '');
	            $originalRow.find('.maxValue').val(field.maxval || '');
	            $originalRow.find('.initValue').val(field.initval || '');
	            $originalRow.find('.quantum').val(field.quantum || '');
	            $originalRow.find('.unit').val(field.unit || '');
	            $originalRow.find('.description').val(field.description || '');
	            $originalRow.find('.remarks').val(field.remarks || '');
	            $originalRow.find('.groupVariable').val('').prop('readonly', selectedGroupId==0?true: false);

	            
	        } else {
	            // For additional fields, clone new rows
	            fieldDescIndex++;

	            const $newRow = $('.tr_clone_fielddesc').first().clone();

	            $newRow.find('.hiddenFieldGroupId, .hiddenFieldMasterId').remove();
	            
	            $newRow.find('.select2-container').remove();
	            $newRow.find('.fieldGroupId, .fieldMasterId, .arrayMasterId')
	                .removeClass('select2-hidden-accessible')
	                .removeAttr('data-select2-id')
	                .removeAttr('aria-hidden')
	                .removeAttr('tabindex');

	            $newRow.find('.fieldGroupId')
	                .val(selectedGroupId)
	                .attr('id', 'fieldGroupId_' + fieldDescIndex)
	            	.prop('disabled', false);
	            	
	            $newRow.find('.fieldMasterId')
	                .empty()
	                .append(new Option(field.text, field.value, true, true))
	                .attr('id', 'fieldMasterId_' + fieldDescIndex)
	            	.prop('disabled', false);
	            	
	            $newRow.find('.arrayMasterId')
	            	.val('')
	                .attr('id', 'arrayMasterId_' + fieldDescIndex)
	            	
	            $newRow.find("input[type='text'], textarea").val('');
	            $newRow.find("input[type='number']").val('0');

	            $newRow.find('.dataType').attr('id', 'dataType_' + fieldDescIndex).val(field.datatype || '');
	            $newRow.find('.typicalValue').attr('id', 'typicalValue_' + fieldDescIndex).val(field.typicalval || '');
	            $newRow.find('.minValue').attr('id', 'minValue_' + fieldDescIndex).val(field.minval || '');
	            $newRow.find('.maxValue').attr('id', 'maxValue_' + fieldDescIndex).val(field.maxval || '');
	            $newRow.find('.initValue').attr('id', 'initValue_' + fieldDescIndex).val(field.initval || '');
	            $newRow.find('.quantum').attr('id', 'quantum_' + fieldDescIndex).val(field.quantum || '');
	            $newRow.find('.unit').attr('id', 'unit_' + fieldDescIndex).val(field.unit || '');
	            $newRow.find('.description').attr('id', 'description_' + fieldDescIndex).val(field.description || '');
	            $newRow.find('.remarks').attr('id', 'remarks_' + fieldDescIndex).val(field.remarks || '');
	            $newRow.find('.groupVariable').val('').attr('id', 'groupVariable_' + fieldDescIndex).prop('readonly', selectedGroupId==0?true: false);
	            
	            $lastInsertedRow.after($newRow);
    			$lastInsertedRow = $newRow; // Update reference
	        }
	    });

	    initializeSelect2ForAll();
	}


	$(document).ready(function () {
	    // Set initial counter
	    const lastId = $('.fieldGroupId').last().attr('id');
	    if (lastId) {
	        const parts = lastId.split('_');
	        fieldDescIndex = parseInt(parts[1]) || 1;
	    }

	    // Initialize all existing selects
	    initializeSelect2ForAll();
	    
	 	// Use event delegation to bind change handler to current and future .fieldGroupId elements
	    $('#fielddesctable').on('change', '.fieldGroupId', function () {
	    	const id = $(this).attr('id'); // e.g., fieldGroupId_2
	        const index = parseInt(id.split('_')[1]); // get 2
	        handleFieldGroupChange(index);
	    });

	 	// Field Master selection change handler
	    $('#fielddesctable').on('change', '.fieldMasterId', function () {
	        const selectedFieldId = $(this).val();
	        const $row = $(this).closest('tr');
	        const groupId = $row.find('.fieldGroupId').val();
	        const fieldList = fieldGroupMap[groupId] || [];
	        const matchedField = fieldList.find(f => f.value === selectedFieldId);

	        if (matchedField) {
	            $row.find('.dataType').val(matchedField.datatype || '');
	            $row.find('.typicalValue').val(matchedField.typicalval || '');
	            $row.find('.minValue').val(matchedField.minval || '');
	            $row.find('.maxValue').val(matchedField.maxval || '');
	            $row.find('.initValue').val(matchedField.initval || '');
	            $row.find('.quantum').val(matchedField.quantum || '');
	            $row.find('.unit').val(matchedField.unit || '');
	            $row.find('.description').val(matchedField.description || '');
	            $row.find('.remarks').val(matchedField.remarks || '');
	        }
	    });
	 	
	    function syncGroupValues(selector, inputType = 'change') {
	        $('#fielddesctable').on(inputType, selector, function () {
	            const $this = $(this);

	            if ($this.data('updating')) return;

	            const id = $this.attr('id'); 
	            const index = parseInt(id.split('_')[1]);
	            const value = $this.val(); 
	            const fieldGroupId = $('#fieldGroupId_' + index).val();

	            if (fieldGroupId != 0) {
	                $('.fieldGroupId').each(function () {
	                    const otherId = $(this).attr('id'); 
	                    const otherIndex = parseInt(otherId.split('_')[1]);
	                    const otherGroupId = $(this).val();

	                    if (otherGroupId === fieldGroupId && otherIndex !== index) {
	                        const $target = $('#' + selector.split('.')[1] + '_' + otherIndex);
	                        if ($target.val() !== value) {
	                            $target.data('updating', true);
	                            $target.val(value).trigger(inputType);
	                            $target.data('updating', false);
	                        }
	                    }
	                });
	            }
	        });
	    }

	    // Bind both sync behaviors
	    syncGroupValues('.arrayMasterId', 'change');
	    syncGroupValues('.groupVariable', 'input');

	});
	
	function slnocheck() {
		
		var arr = document.getElementsByName("newslno");

		var arr1 = [];
		for (var i=0;i<arr.length;i++){
			arr1.push(arr[i].value);
		}		 
		 
	    let result = false;
	  
	    const s = new Set(arr1);
	    
	    if(arr.length !== s.size){
	       result = true;
	    }
	    if(result) {
	   	event.preventDefault();
	       alert('Serial No contains duplicate Values');
	       return false;
	    } else {
	   	 return confirm('Are You Sure to Update?');
	    }
	  }	
	
	$('#connectorPinMapId').select2();
	getDataCarryingConnectionList();
	
	function getDataCarryingConnectionList(){
		
		var icdDocId = $('#icdDocId').val();
		var selectedLogicalChannelId = $('#logicalChannelId option:selected');
		var sourceid = selectedLogicalChannelId.data('sourceid');
		var destinationid = selectedLogicalChannelId.data('destinationid');
		$('#connectorPinMapId').empty();
		$.ajax({
            type: "GET",
            url: "GetDataCarryingConnectionList.htm",
            data: {
            	icdDocId : icdDocId,
            	systemMainIdOne: sourceid,
            	systemMainIdTwo: destinationid
            },
            datatype: 'json',
            success: function(result) {
                var values = JSON.parse(result); 
				if(values!=null) {
					for(var i=0; i<values.length; i++) {
						var data = values[i];
	                    var optionValue = data[0];
	                    var optionText = data[1] ; 
	                    var option = $("<option></option>").attr("value", optionValue).text(optionText);
	                    $('#connectorPinMapId').append(option); 
					}
					$('#connectorPinMapId').select2();
				}
            },
            error: function() {
                alert('An error occurred while fetching the Data Carrying Connection List');
            }
        });
	}
</script>	
</body>
</html>