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
List<Object[]> projectAssignList = (List<Object[]>) request.getAttribute("projectAssignList");

Object[] dpd = null;
Object[] projectDirector = null;

if (projectAssignList != null && !projectAssignList.isEmpty()) {
    for (Object[] obj : projectAssignList) {
        if (obj[11] != null) {
            String roleCode = obj[11].toString();

            if (roleCode.equalsIgnoreCase("DPD")) {
                dpd = obj;
            } else if (roleCode.equalsIgnoreCase("PD")) {
                projectDirector = obj;
            }
        }
    }
}

String projectCode="";
if(!projectid.equalsIgnoreCase("0")){
	projectCode = projectdata[4].toString();
}

FormatConverter fc = new FormatConverter();
SimpleDateFormat sdf = fc.getRegularDateFormat();
SimpleDateFormat sdf1 = fc.getSqlDateFormat();
SimpleDateFormat inputFormat = new SimpleDateFormat("ddMMMyyyy");
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
String lablogo=(String)request.getAttribute("lablogo");

String isLetter = (String) request.getAttribute("isLetter");
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
 
 .header-details{
 	display: flex;
 	justify-content: space-between;
 	align-items: center;
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

<table style="<% if(isLetter!=null && isLetter.equalsIgnoreCase("Y")){%>width:50%; margin:auto;<%}else{ %>width:100%;<%} %>">
    <tr>
        <td style="text-align:center;">
            <img class="logo" style="width:120px;height:120px;margin-bottom:5px"
            <% if(lablogo!=null ){ %>
                src="data:image/*;base64,<%=lablogo%>" alt="Logo"
            <% } else { %>
                alt="File Not Found"
            <% } %> >
        </td>

        <td style="text-align:right;">
            <h3>Programme 'AD', Research Center IMARAT</h3>
            <h3>DRDO, Ministry of Defense</h3>
            <h3>Kanchanbagh P.o, Hyderabad - 500058</h3>
            <h3>Telefax: +91-40-24342850</h3>
        </td>
    </tr>
</table>
 <div style="text-align: center;" align="center">
 <div  style="<% if(isLetter!=null && isLetter.equalsIgnoreCase("Y")){%>width:50%; margin:auto;<%}else{ %>width:100%;<%} %>">
 <span style="float: left; font-size:13px;">Ref No. - <%if(committeemaindata[11]!=null) {%><%= projectCode.length()>1?projectCode+"/ ":"" %><%=committeemaindata[11].toString()%><%}else{ %> -<%} %> </span>
 <span style="float: right; font-size:13px;">Date :  <%if(committeemaindata[12]!=null){ %>  <%=sdf.format(sdf1.parse(committeemaindata[12].toString()))%><%} %></span>
 </div>  
<br>
<%--  	<div style="text-align: center;" ><h3 style="margin-bottom: 2px;" align="center"><%=labdetails[2]+"("+labdetails[1]+")" %> </h3></div>   --%>
 	
	<div style="text-align: center;" ><h3 style="margin-bottom: 2px;text-decoration: underline;" align="center">Constitution of Committee for <%=committeeedata[2]%> <%-- (<%=committeeedata[1].toString()%>) --%> </h3></div>
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
				<h3 style="margin-bottom: 2px; width: 650px; text-align:left;" >The Terms and Reference of this committee as below: </h3>
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
		Approved /  Not Approved 
		<br>
		<br>
		<%if(projectDirector!=null){ %>
			<%=projectDirector[3]!=null ? projectDirector[3].toString() : " - " %> <%=projectDirector[4]!=null ?", "+ projectDirector[4].toString() : " - " %> <br> (<%=projectdata[1]!=null ? projectdata[1].toString(): " - " %>)
			<br>
			Project Director 
		<%} %>
	</div>
	<br><br>
	<div align="right"  style="<% if(isLetter!=null && isLetter.equalsIgnoreCase("Y")){%>width:50%; margin:auto;<%}else{ %>width:100%;<%} %>">
		<%if(dpd!=null){ %>
			<%=dpd[3]!=null ? dpd[3].toString() : " - " %> <%=dpd[4]!=null ?", "+ dpd[4].toString() : " - " %> <br> (<%=projectdata[1]!=null ? projectdata[1].toString(): " - " %>)
			<br>
			Deputy Project Director
		<%} %>
	</div>
	<%-- <div style="text-align: left;font-size: 13px;">
	Initiated By : <%if(CommitteMainEnoteList!=null && CommitteMainEnoteList[18]!=null ){ %> <%=CommitteMainEnoteList[18].toString() %>, <%=CommitteMainEnoteList[19].toString() %>  <%}else{ %>  <%= constitutionapprovalflow.get(0)[0]%>,  <%= constitutionapprovalflow.get(0)[1]%> <%} %>
	</div> --%>
<!-- 	<div style="margin-top:30px;margin-left:10px;">Recommended Officer :- </div>
	<div style="margin-top:10px;margin-left:10px;">Approving Officer :-</div> -->
	</div>

</body>
</html>