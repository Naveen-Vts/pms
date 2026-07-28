<%@page import="java.util.stream.Collectors"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.math.BigDecimal"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="java.util.*"%>
	<%@page import="java.text.SimpleDateFormat"%>
	<%@page import="com.vts.pfms.FormatConverter"%>

<%
String email=(String)request.getAttribute("email");  
List<Object[]> committeeallmemberslist = (List<Object[]>) request.getAttribute("committeeallmemberslist");
List<Object[]> constitutionapprovalflowData = (List<Object[]>) request.getAttribute("constitutionapprovalflowData");

String RecommendedBy ="";

if(constitutionapprovalflowData!=null && constitutionapprovalflowData.size()>0){
	for(Object[]obj:constitutionapprovalflowData){
		if(obj[2].toString().equalsIgnoreCase("RDO")){
			RecommendedBy = obj[0].toString()+", "+obj[1].toString();
		}
	}
}

Object[] committeeedata = (Object[]) request.getAttribute("committeeedata");
Object[] projectdata = (Object[]) request.getAttribute("projectdata");
Object[] initiationdata = (Object[]) request.getAttribute("initiationdata");
Object[] labdetails = (Object[]) request.getAttribute("labdetails"); 
Object[] committeedescription = (Object[]) request.getAttribute("committeedescription");
Object[] committeemaindata = (Object[]) request.getAttribute("committeemaindata");
String projectid=committeemaindata[2].toString() ;
String divisionid=committeemaindata[3].toString() ;
String initiationid=committeemaindata[4].toString() ;
List<Object[]> constitutionapprovalflow=(List<Object[]>)request.getAttribute("constitutionapprovalflow");
String flag = (String)request.getAttribute("flag");


String projectCode="";
if(!projectid.equalsIgnoreCase("0")){
	projectCode = projectdata[4].toString();
}

FormatConverter fc = new FormatConverter();
SimpleDateFormat sdf = fc.getRegularDateFormat();
SimpleDateFormat sdf1 = fc.getSqlDateFormat();
SimpleDateFormat inputFormat = new SimpleDateFormat("ddMMMyyyy",Locale.ENGLISH);
SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
Object[]CommitteMainEnoteList = (Object[])request.getAttribute("CommitteMainEnoteList");
List<String>memtypes=Arrays.asList("PS","CS");
List<Object[]>committeeallmemberslistwithCC=committeeallmemberslist.stream().filter(i->i[8].toString().equalsIgnoreCase("CC")).collect(Collectors.toList());
List<Object[]>committeeallmemberslistwithoutMs=committeeallmemberslist.stream().filter(i->!memtypes.contains(i[8].toString()) && !i[8].toString().equalsIgnoreCase("CC")).collect(Collectors.toList());
List<Object[]>committeeallmemberslistwithMs=committeeallmemberslist.stream().filter(i->memtypes.contains(i[8].toString())).collect(Collectors.toList());
committeeallmemberslistwithoutMs=committeeallmemberslistwithoutMs.stream()
	.sorted(Comparator.comparingInt(e -> Integer.parseInt(e[11].toString()))).collect(Collectors.toList());
List<Object[]>tempList=new ArrayList<>();
tempList.addAll(committeeallmemberslistwithCC);
tempList.addAll(committeeallmemberslistwithoutMs);
tempList.addAll(committeeallmemberslistwithMs);
if(!email.equals("Y")){ %>
<!DOCTYPE html>
<html>
<head>
<title>Committee Formation Letter</title>




<style type="text/css">

.break
{
	page-break-after: always;
}
p{
  text-align: justify;
  text-justify: inter-word;
}
 
@page {    
          
          	size: 790px 1120px;
              margin-top: .4in;
              margin-left: .5in;
              margin-right: .5in;
              margin-buttom: .4in;
              /* border: 1px solid black; */
         <%if(constitutionapprovalflowData!=null && constitutionapprovalflowData.size()>0){%>
           @bottom-left { 
             font-size: 13px;
	          margin-bottom: 30px;
<%-- 	          content: "Initiated By : <%= constitutionapprovalflow.get(0)[0]%>,  <%= constitutionapprovalflow.get(0)[1]%>"; 
 --%>          	} 
          <%}%>
          
          <%if(RecommendedBy.length()>1){%>
           @bottom-right { 
             font-size: 13px;
	          margin-bottom: 30px;
	           margin-right: 20px;
	       <%--    content: "Recommended By :- <%=RecommendedBy%>";  --%>
          }               
          <%}%>
         
 }
 .text-black{
 font-weight: bold;
 }
 
 li{
 text-align: left;
 }
 </style>
 <body>

<%}%>





<%

%>


 <div style="text-align: center;" align="center">
 <div  >
 <span style="float: left; font-size:13px;">Ref No. - <%if(committeemaindata[11]!=null) {%><%= projectCode.length()>1?projectCode+"/ ":"" %><%=committeemaindata[11].toString()%><%}else{ %> -<%} %> </span>
 <span style="float: right; font-size:13px;">Date :  <%if(committeemaindata[12]!=null){ %>  <%=sdf.format(sdf1.parse(committeemaindata[12].toString()))%><%} %></span>
 </div>  
<br>
 	<div style="text-align: center;" ><h3 style="margin-bottom: 2px;" align="center"><%=labdetails[2]+"("+labdetails[1]+")" %> </h3></div>  
 	
	<div style="text-align: center;" ><h3 style="margin-bottom: 2px;" align="center">Formation of Committee  <%=committeeedata[2]%> (<%=committeeedata[1].toString()%>) </h3></div>
	<br>
	<div  align="center">
	<table style=" margin-top: 10px; margin-bottom: 10px; margin-left: 15px; max-width: 650px; font-size: 16px; border-collapse:collapse;" >
		<tr>
			<td>
<!-- 				<div style="text-align: center;" ><h3 style="margin-bottom: 2px; max-width: 650px;" align="center">Committee constitution </h3></div>
 -->			</td>
		</tr>
		<tr>
			<td>
				<div style="text-align: center;" >
					<div style="margin-bottom: 2px; max-width: 650px;text-align: justify;text-justify: inter-word;text-align: justify;text-justify: inter-word;" align="center">
						<%if(Long.parseLong(projectid)>0 || Long.parseLong(divisionid)>0 || Long.parseLong(initiationid)>0){ %>
								<%if(committeedescription[1]!=null){ %><%=committeedescription[1] %> <%}else{ %>No Data <%} %>
						<%}else { %>
								<%if(committeeedata[10]!=null){ %><%=committeeedata[10] %> <%}else{ %>No Data <%} %>
						<%} %>
					</div>
				</div>
			</td>
		</tr>
	</table>
	
	<!-- -------------------------------------------members-------------------------------- -->
<!-- 	<table style=" margin-top: 10px; margin-bottom: 10px; margin-left: 15px; width: 650px; font-size: 16px; border-collapse:collapse; " >
	<thead>
	<tr >

	</tr>
	</thead>
	</table> -->
	<table style=" margin-bottom: 10px; margin-left: 15px; width: 650px; font-size: 13px; border-collapse:collapse;border:1px solid black; " >
		<tr >
			<%-- <td colspan="5" style="text-align: center;padding-bottom:15px; ">Director,<%=labdetails[1].toString() %> has constituted the  following committee </td> --%>
		</tr>
					<tr>				
				<td class="text-black"  style="max-width:40px;text-align: center; padding: 5px 0px 5px 0px; border:1px solid black;">SN .&nbsp;</td>
				<td class="text-black"  style="max-width: 300px;text-align: left; padding: 5px 0px 5px 0px;border:1px solid black;">&nbsp;Name, Designation</td>
				<td class="text-black"  style="max-width: 150px;text-align: center; padding: 5px 0px 5px 0px; border:1px solid black;">Estt. / Agency </td>
				<td class="text-black"  style="max-width: 200px;text-align: left; padding: 5px 0px 5px 0px;border:1px solid black;">&nbsp; Role
				</td>
				</tr>
		<% int i=0;
			for(Object[] member : tempList){
				i++; %>
			<tr>				
				<td style="max-width:40px;text-align: center; padding: 5px 0px 5px 0px; border:1px solid black;"><%=i %> .&nbsp;</td>
				<td style="max-width:300px;text-align: left; padding: 5px 0px 5px 0px;border:1px solid black;">&nbsp;<%=member[2] %><%=member[4].toString().length()>1?", "+member[4].toString():"" %> <%-- <%if(member[8].toString().equals("CW")){ %><%=member[9]%><%}  %> --%>&nbsp;</td>
				<td  style="max-width:150px;text-align: center; padding: 5px 0px 5px 0px; border:1px solid black;"><%=member[12].toString()%> </td>
				<td style="max-width: 200px;text-align: left; padding: 5px 0px 5px 0px;border:1px solid black;">&nbsp; 
				<%if(member[8].toString().equals("CC")){ %>Chairperson<%}
				else if(member[8].toString().equals("CH")){ %>Co-Chairperson<%} 
		 		else if(member[8].toString().equals("CS")){ %>Member Secretary<%} 
		 		else if(member[8].toString().equals("PS")){ %>Member Secretary (Proxy)<%} 
		 		else if(member[8].toString().equals("CI")){ %>Internal Member<%} 
		 		else if(member[8].toString().equals("CW") && committeeedata[1].toString().equalsIgnoreCase("SPRT")&& !member[12].toString().equalsIgnoreCase("DG-ECS")){ %>Nodal Lab<%} 
		 		else if(member[8].toString().equals("CW")){ %>External Member<%} 
		 		else if(member[8].toString().equals("CO")){ %>Expert Member<%}	
		 		else if(member[8].toString().equals("CIP")){ %>Industry Partner<%}%>	
				 &nbsp;</td>
				
			</tr>		
		<%} %>	
	</table>
	<!-- -------------------------------------------members-------------------------------- -->
		<table style=" margin-left: 15px; max-width: 650px; font-size: 16px; border-collapse:collapse;" >
		<tr>
			<td >				
				<h3 style="margin-bottom: 2px; width: 650px; text-align:left;" >Terms of Reference </h3>
			</td>
		</tr>
		<tr>
			<td>
				<div style="text-align: center;" >
					<p style="margin-bottom: 2px; max-width: 650px;text-align: justify;text-justify: inter-word;text-align: justify;text-justify: inter-word;" align="center">
						
					<%if(Long.parseLong(projectid)>0 || Long.parseLong(divisionid)>0 || Long.parseLong(initiationid)>0){ %>
						<%if(committeedescription[2]!=null){ %><%=committeedescription[2] %> <%}else{ %>No Data <%} %>
														
					<%}else if(projectid!=null && Long.parseLong(projectid)==0){ %>
								<%if(committeeedata[11]!=null){ %><%=committeeedata[11] %> <%}else{ %> No Data <%} %>
					<%} %>
					
					</p>
				</div>
			</td>
		</tr>
	</table>
	</div>
</div>

	<div class="row " style="text-align: left;">
	<br>
	<div align="center" style="text-align: center">
	<%if(committeemaindata[9].toString().equalsIgnoreCase("A")){ %>
	 <%if(CommitteMainEnoteList!=null && !CommitteMainEnoteList[22].toString().equalsIgnoreCase((String)session.getAttribute("labcode"))){ %>
	 Committee Recommended by Director, <%=(String)session.getAttribute("labcode") %> for competent Approval Authority.
	 <%}else{ %>
	 Approved
	 <br>
	<br>
	<div align="center" style="text-align: center">
		<%if(committeemaindata[9].toString().equalsIgnoreCase("A")){ %>
		
		<%=CommitteMainEnoteList!=null && CommitteMainEnoteList[14]!=null?CommitteMainEnoteList[14].toString():"Director" %>
		<%} %>
	</div>
	 <%} %>
	<%}else{ %>
	Approved /  Not Approved 
	<br>
	<br>
	<%=CommitteMainEnoteList!=null && CommitteMainEnoteList[14]!=null?CommitteMainEnoteList[14].toString():"" %>
	<%} %>
	</div>
	<br><br>
	<div style="text-align: left;font-size: 13px;">
	Initiated By : <%if(CommitteMainEnoteList!=null && CommitteMainEnoteList[18]!=null ){ %> <%=CommitteMainEnoteList[18].toString() %>,  <%=CommitteMainEnoteList[19].toString() %>  <%}else{ %>  <%= constitutionapprovalflow.get(0)[0]%>, <%= constitutionapprovalflow.get(0)[1]%> <%} %>
	</div>
<!-- 	<div style="margin-top:30px;margin-left:10px;">Recommended Officer :- </div>
	<div style="margin-top:10px;margin-left:10px;">Approving Officer :-</div> -->
	</div>
	<%
	if (!email.equals("Y")) {
	%>
</body>
</html>

<%}%>