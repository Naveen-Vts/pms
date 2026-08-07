<%@page import="java.util.Arrays"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="java.util.Map"%>
<%@page import="lombok.val"%>
<%@page import="java.lang.reflect.Array"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="com.vts.pfms.model.LabMaster"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.vts.pfms.FormatConverter"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Minutes Of Meeting</title>
<%
		List<Object[]> speclists = (List<Object[]>) request.getAttribute("committeeminutesspeclist");
		Object[] committeescheduleeditdata = (Object[]) request.getAttribute("committeescheduleeditdata");
		List<Object[]> committeeminutes = (List<Object[]>) request.getAttribute("committeeminutes");
		List<Object[]> invitedlist = (List<Object[]>) request.getAttribute("committeeinvitedlist");
		Object[] labdetails = (Object[]) request.getAttribute("labdetails");
		int addcount=0; 
		List<String> repCodes = (List<String> )request.getAttribute("repCodes");
		ArrayList<String> membertypes=new ArrayList<String>(Arrays.asList("CC","CS","PS","CI","CW","CO","CH"));
		membertypes.addAll(repCodes);
		int meetingcount= (int) request.getAttribute("meetingcount");
		
		String[] no=committeescheduleeditdata[11].toString().split("/");
		
		FormatConverter fc=new FormatConverter(); 
		SimpleDateFormat sdf=fc.getRegularDateFormat();
		SimpleDateFormat sdf1=fc.getSqlDateFormat();
		
		Object[] membersec=null; 
		String OtherRemarks = null;
		String ccmFlag = (String)request.getAttribute("ccmFlag");
	%>
<style type="text/css">
		@page {             
          size: 790px 1120px;
          margin-top: 49px;
          margin-left: 39px;
          margin-right: 39px;
          margin-buttom: 49px; 	
          border: 1px solid black;    
          @bottom-right {          		
             content: "Page " counter(page) " of " counter(pages);
             margin-bottom: 30px;
             margin-right: 10px;
          }
          @top-right {
          		<%-- <%if( Long.parseLong(projectid)>0){%>
             content: "Project:<%=projectdetails[4]!=null?projectdetails[4].toString(): " - "%>";
             <%}else if(Long.parseLong(divisionid)>0){%>
               	content: "Division:<%=divisiondetails[1]!=null?divisiondetails[1].toString(): " - "%>";
             <%}else if(Long.parseLong(initiationid)>0){ %>
             	content: "Pre-Project :<%=initiationdetails[1]!=null?initiationdetails[1].toString(): " - "%>";
             <%} else{%> --%>
             	content: "<%=labdetails[1]!=null?labdetails[1].toString(): " - "%>";
             <%-- <%}%> --%>
             margin-top: 30px;
             margin-right: 10px;
               font-size: 13px;
          }
          @top-left {
          	margin-top: 30px;
            margin-left: 10px;
            content: "<%=no[0]!=null?no[0].toString(): " - "%>/<%=no[1]!=null?no[1].toString(): " - "%>/<%=no[2]!=null?no[2].toString(): " - " %><%if(meetingcount>0){ %>#<%=meetingcount %><%} %>/<%=no[3]!=null?no[3].toString(): " - "%>";
             font-size: 13px;
          }      
          
          @top-center { 
           font-size: 13px;
          margin-top: 30px;
          content: "<%=committeescheduleeditdata[15]!=null?committeescheduleeditdata[15].toString(): " - "%>"; 
          
          }
          
           @bottom-center { 
             font-size: 13px;
	          margin-bottom: 30px;
	          content: "<%=committeescheduleeditdata[15]!=null?committeescheduleeditdata[15].toString(): " - "%>"; 
          
          } 
          
          @bottom-left { 
             font-size: 13px;
	          margin-bottom: 30px;
	          content: "<%=LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))%>"; 
          } 
          }
    .remarks-container {
        display: flex;
        align-items: center; /* vertically align text */
    }

    .other-remarks {
        padding-left: 10px; /* optional: internal padding for content */
    }
 	</style>
</head>
<body>
	<div id="container" align="center" style="margin: 15px;">
	  <div class="center">		
			<br>
			<div style="text-align: center;" ><h1>MINUTES OF MEETING</h1></div>
			<br>
			<div style="text-align: center;" ><h2 style="margin-bottom: 2px;"><%=committeescheduleeditdata[7]!=null?committeescheduleeditdata[7].toString().toUpperCase():" - "%>  (<%=committeescheduleeditdata[8]!=null?committeescheduleeditdata[8].toString().toUpperCase():" - " %><%if(meetingcount>0){ %>&nbsp;&nbsp;#<%=meetingcount %><%} %>) </h2></div>				
				<%-- <%if(Integer.parseInt(projectid)>0){ %>					
					<h3 style="margin-top: 5px; margin-bottom: 5px">For</h3>	  
				    <h2 style="margin-top: 3px">Project  : &nbsp;<%=projectdetails[1]!=null?projectdetails[1].toString(): " - " %>  (<%=projectdetails[4]!=null?projectdetails[4].toString(): " - "%>)</h2>
				<%}else if(Integer.parseInt(divisionid)>0){ %>					
					<h3 style="margin-top: 5px; margin-bottom: 5px">For</h3>	  
			 	   	<h2 style="margin-top: 3px">Division :&nbsp;<%=divisiondetails[2]!=null?divisiondetails[2].toString(): " - " %>  (<%=divisiondetails[1]!=null?divisiondetails[1].toString(): " - "%>)</h2>
				<%}else if(Integer.parseInt(initiationid)>0){ %>					
					<h3 style="margin-top: 5px; margin-bottom: 5px">For</h3>	  
				    <h2 style="margin-top: 3px">Pre-Project  : &nbsp;<%=initiationdetails[2]!=null?initiationdetails[2].toString(): " - " %>  (<%=initiationdetails[1]!=null?initiationdetails[1].toString(): " - "%>)</h2>
				<%}else{%>
					<br><br><br><br><br>
				<%} %>
				<br> --%>
				<table style="align: center; margin-top: 30px; margin-bottom: 10px; margin-left: 15px; max-width: 650px; font-size: 16px"  border="0">
					<tr style="margin-top: 10px">
						 <th  style="text-align: center; width: 650px;font-size: 20px"> <u>Meeting Id </u> </th></tr><tr>
						 <th  style="text-align: center;  width: 650px;font-size: 20px"> <%=committeescheduleeditdata[11]!=null?committeescheduleeditdata[11].toString(): " - " %> </th>				
					 </tr>
				</table>
		 	<%-- <table style="align: left; margin-top: 30px; margin-bottom: 10px; margin-left: 15px; max-width: 900px; font-size: 16px"  border="0">
				 <tr>
					 <th  style="text-align: center; width: 350px;font-size: 20px ">   Meeting Date  : <%=committeescheduleeditdata[2]!=null?sdf.format(sdf1.parse(committeescheduleeditdata[2].toString())): " - " %> </th>
					 <th  style="text-align: center;  width: 350px;font-size: 20px  "> Meeting Time  : <%=committeescheduleeditdata[3]!=null?committeescheduleeditdata[3].toString(): " - "%></th>
					 <th  style="text-align: center; width: 350px;font-size: 20px ">   Meeting Venue : <% if(committeescheduleeditdata[12]!=null){ %><%=committeescheduleeditdata[12].toString() %> <%}else{ %> - <%} %></th>
				 </tr>
				  <tr>
					 <td  style="text-align: center;  width: 350px;font-size: 20px ;padding-top: 5px"> <b><%=committeescheduleeditdata[2]!=null?sdf.format(sdf1.parse(committeescheduleeditdata[2].toString())): " - " %></b></td>
					 <td  style="text-align: center;  width: 350px;font-size: 20px ;padding-top: 5px "> <b><%=committeescheduleeditdata[3]!=null?committeescheduleeditdata[3].toString(): " - "%></b></td>
					 <td  style="text-align: center;  width: 350px;font-size: 20px ;padding-top: 5px ; font-weight: bold;"> <% if(committeescheduleeditdata[12]!=null){ %><%=committeescheduleeditdata[12].toString() %> <%}else{ %> - <%} %></td>
				 </tr> 
			 </table><br><br><br> --%>
			 <table style="align: left; margin-top: 30px; margin-bottom: 10px; margin-left: 15px; max-width: 900px; font-size: 16px"  border="0">
				 <tr>
					 <th  style="text-align: center; width: 350px;font-size: 20px "> <u> Meeting Date </u></th>
					 <th  style="text-align: center;  width: 350px;font-size: 20px  "><u> Meeting Time </u></th>
					 <th  style="text-align: center; width: 350px;font-size: 20px "> <u>Meeting Venue </u> </th>
				 </tr>
				 <tr>
					 <td  style="text-align: center;  width: 350px;font-size: 20px ;padding-top: 5px"> <b><%=committeescheduleeditdata[2]!=null?sdf.format(sdf1.parse(committeescheduleeditdata[2].toString())): " - " %></b></td>
					 <td  style="text-align: center;  width: 350px;font-size: 20px ;padding-top: 5px "> <b><%=committeescheduleeditdata[3]!=null?committeescheduleeditdata[3].toString(): " - "%></b></td>
					 <td  style="text-align: center;  width: 350px;font-size: 20px ;padding-top: 5px "><b><% if(committeescheduleeditdata[12]!=null){ %><%=committeescheduleeditdata[12].toString() %> <%}else{ %> - <%} %></b></td>
				 </tr>
			 </table><br><br><br>
			<%-- <figure><img style="width: 4cm; height: 4cm"  src="data:image/png;base64,<%=lablogo%>"></figure> --%>   
			<%-- <br>				<br><br>
			
			
			<div style="text-align: center;" ><h3><%=labdetails[2]!=null?labdetails[2].toString(): " - " %> (<%=labdetails[1]!=null?labdetails[1].toString(): " - "%>)</h3></div>
			
			<div align="center" ><h3><%=labdetails[4]!=null?labdetails[4].toString(): " - " %>, &nbsp;<%=labdetails[5]!=null?labdetails[5].toString(): " - " %>, &nbsp;<%=labdetails[6]!=null?labdetails[6].toString(): " - " %></h3></div> --%>
			
			
		</div>
	  
	  <!-------------------------------------------------Introduction And Opening Remarks ----------------------------------->
	  
	  <div align="left">
	  <!-- <h4> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;I.</h4> -->
	   <!-- <h4 style="margin-bottom:10; padding:0;">&nbsp;&nbsp;&nbsp;&nbsp; I. &nbsp;&nbsp;	 Introduction & Opening Remarks: </h4> --> 
	   <%-- <h4 style="margin-bottom:10; padding:0;">&nbsp;&nbsp;&nbsp;&nbsp; I. &nbsp;&nbsp;	 <%=title %>: </h4> --%> 
		  <%
			int countuh=1;
				for (Object[] committeemin : committeeminutes) {
				if (committeemin[0].toString().equals("1") || committeemin[0].toString().equals("3")) { 
			%>
			 
	  		<span style="font-weight: bold; margin:0; padding:0; padding-left: 15px; " > <%=countuh++%>. &nbsp;&nbsp; <%=committeemin[1]!=null?committeemin[1].toString(): " - "%> </span>
	  		<% int count = 0;
				String agenda = "";
				for (Object[] speclist : speclists)
				{
					if(speclist[3].toString().equals("7")) OtherRemarks = speclist[1].toString();
					if (speclist[3].toString().equals(committeemin[0].toString()) && committeemin[0].toString().equals("1") && speclist[1]!=null && !speclist[1].toString().trim().isEmpty()) 
					{
						count++;
				%>	
				 <div style="padding-left: 40px"><%=speclist[1]!=null?speclist[1].toString(): " - "%></div>
	  		<%		
							}
					else if(speclist[3].toString().equals(committeemin[0].toString()) && committeemin[0].toString().equals("3") && speclist[10]!=null && !speclist[10].toString().trim().isEmpty() && !agenda.equalsIgnoreCase(speclist[10].toString())){
						count++;
						agenda = speclist[10].toString();
						%>	
						 <div style="padding-left: 40px; margin:10px;">
							 <ul>
							    <li><%= speclist[10] != null ? speclist[10].toString() : "-" %></li>
							</ul>
						</div>
			  		<%		
					}
						}
						if (count == 0) 
						{%>
						<p style="padding-left: 40px;">NIL<p>
					
	  		<%}}} %>
	  </div>
	  
	  <div align="center" > 
				
					<%if(committeeminutes.size()>0){ %>
					<br>
					<table style="margin-top: 0px; margin-left: 15px; width: 650px; font-size: 16px; border-collapse: collapse;">
						<tr>
							<th colspan="8" style="text-align: left; font-weight: 700;">3. &nbsp;&nbsp;Record of Discussions and Action Points of Current Meeting (ANNEXURE - A)</th>
						</tr>
						<!-- <tr>
							<td colspan="8" style="text-align: center ;padding: 5px;">Item Code/Type : A: Action, C: Comment, D: Decision, R: Recommendation</td>
						</tr> -->
					</table>						
					<%} %>			
			</div>	
	  		<div>
		  		 <%
				 if(ccmFlag==null) {
				 for (Object[] committeemin : committeeminutes) { 
					 if ( committeemin[0].toString().equals("6")) { %>
					 <br>
				 <table style="margin-top: 0px; margin-left: 30px; width: 650px; font-size: 16px; border-collapse: collapse;">
							<tr>
								<th colspan="8" style="text-align: left; font-weight: 700;">4. &nbsp;&nbsp;Final Recommendation & Conclusion</th>
							</tr>
				 </table>
				<div align="center">
					<table style="margin-top: 10px; width: 650px;margin-left: 40px; font-size: 16px; border-collapse: collapse;">
						<tbody>
							<% int conclusionCount = 0; 
							for (Object[] speclist : speclists)
							{
								if (speclist[3].toString().equals(committeemin[0].toString())) 
								{
									if(speclist[3].toString().equals("6")){
										conclusionCount++;
									%>
								
										<tr>
											<td style="text-align: justify;padding-left: 40px;">
												<%if(speclist[1]!=null && !speclist[1].toString().trim().equalsIgnoreCase("")){%> <%=speclist[1]!=null?speclist[1].toString(): " - "%>  <%}else{ %> NIL <%} %>  
											</td>
										</tr>							
									<%}
								}							
								
							}
							if(conclusionCount==0){
							%>
							<tr>
								<td style="text-align: justify; padding-left: 40px;">
									NIL
								</td>
							</tr>	
						<%} }%>
					</table>
				</div>
					
			<%} }%>
	  	</div>
	  	<div >
	  	<%if(invitedlist.size()>0){ %>
		<% 
			
			int memPresent=0,memAbscent=0,ParPresent=0,parAbscent=0;
			int j=0;
			for(Object[] temp : invitedlist){
			
				if(temp[4].toString().equals("P") &&  membertypes.contains( temp[3].toString()) )
				{ 
					memPresent++;
				}
				else if(temp[4].toString().equals("N") &&  membertypes.contains( temp[3].toString()) )
				{
					memAbscent++;
				}
				else if( temp [4].toString().equals("P") && !membertypes.contains( temp[3].toString()) )
				{ 
					ParPresent++;
				}
				else if( temp [4].toString().equals("N") && !membertypes.contains( temp[3].toString()) )
				{ 
					parAbscent++;
				}
			}
		%>
	  	<% 
	  	/* Members Present List */
				if(memPresent>0){ for(int i=0;i<invitedlist.size();i++)
					{
				 	if(invitedlist.get(i)[4].toString().equals("P") && membertypes.contains( invitedlist.get(i)[3].toString()) )
				 	{ 
				 		if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CS") ){ membersec=invitedlist.get(i); }}}
				}
	  	/* Members Absent List */
				if(memAbscent>0){ 
					for(int i=0;i<invitedlist.size();i++) {
				 	if(invitedlist.get(i)[4].toString().equals("N")&& membertypes.contains( invitedlist.get(i)[3].toString()) )
				 	{
				 		if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CS") ){ membersec=invitedlist.get(i); 
				 		} }}
				}
				/* Participatients Present List */
				if(ParPresent>0){ 
					for(int i=0;i<invitedlist.size();i++)
					{
					 	if(invitedlist.get(i)[4].toString().equals("P") && !membertypes.contains( invitedlist.get(i)[3].toString()) )
					 	{
					 		if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CS") ){ membersec=invitedlist.get(i);}}
					 	}
				}
				/* Participatients Absent List */
				if(parAbscent>0){ 
					for(int i=0;i<invitedlist.size();i++) {
					 	if(invitedlist.get(i)[4].toString().equals("N")&& !membertypes.contains( invitedlist.get(i)[3].toString()) )
					 	{
					 		if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CS") ){ membersec=invitedlist.get(i);
					 	}}
					 }
				}
	  	}
				 		%>
		<div style="width: 650px;margin-left: 15px; ">
			<!-- <div align="left" >
				These minutes are issued with the approval of the Chairperson.
			</div> -->
			<div align="left" style="padding-right: 0rem;padding-bottom: 0rem; margin-right: 0px">
				<!-- <br>Date :&emsp;&emsp;&emsp;&emsp;&emsp;  <br>Time :&emsp;&emsp;&emsp;&emsp;&emsp; -->
				<%if(membersec!=null){ %>
					<div align="right" style="padding-right: 0rem;padding-bottom: 2rem;">
						<br><br><%if(membersec[6]!=null){%><%= membersec[6].toString().substring(membersec[6].toString().indexOf(".")+1) %><%} %>
						<br>(Member Secretary)
					</div>
				<%} %>
			</div> 
				<div class="remarks-container">
				    <div ><b>Other Remarks:</b></div>
				    <div class="other-remarks"><%= OtherRemarks != null ? OtherRemarks : "NIL" %></div>
				</div>
						 
			<!-- <div align="left" ><b>NOTE : </b>Action item details are enclosed as Annexure - AI.</div>		 -->
		</div> 
	</div>	
	  
	  
	 </div>
	  

</body>
</html>