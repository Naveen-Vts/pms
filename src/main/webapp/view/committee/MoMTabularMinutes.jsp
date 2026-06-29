<%@page import="java.util.regex.Matcher"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="lombok.val"%>
<%@page import="java.util.Objects"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.io.IOException"%>
<%@page import="java.io.IOError"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="java.util.Arrays"%>
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
	<%
		List<Object[]> speclists = (List<Object[]>) request.getAttribute("committeeminutesspeclist");
		Object[] committeescheduleeditdata = (Object[]) request.getAttribute("committeescheduleeditdata");
		List<Object[]> committeeminutes = (List<Object[]>) request.getAttribute("committeeminutes");
		List<Object[]> invitedlist = (List<Object[]>) request.getAttribute("committeeinvitedlist");
		Object[] labdetails = (Object[]) request.getAttribute("labdetails");
		HashMap< String, ArrayList<Object[]>> actionlist = (HashMap< String, ArrayList<Object[]>>) request.getAttribute("actionsdata");
		List<Object[]> committeeagendalist=(List<Object[]>)request.getAttribute("committeeagendalist");
		int addcount=0; 
		 
		String projectid= committeescheduleeditdata[9].toString();
		String divisionid= committeescheduleeditdata[16].toString();
		String initiationid= committeescheduleeditdata[17].toString();
		Object[] projectdetails=(Object[])request.getAttribute("projectdetails");
		Object[] divisiondetails=(Object[])request.getAttribute("divisiondetails");
		Object[] initiationdetails=(Object[])request.getAttribute("initiationdetails");
		int meetingcount= (int) request.getAttribute("meetingcount");
		
		String[] no=committeescheduleeditdata[11].toString().split("/");
		
		FormatConverter fc=new FormatConverter(); 
		SimpleDateFormat sdf=fc.getRegularDateFormat();
		SimpleDateFormat sdf1=fc.getSqlDateFormat();
		
		String isprint=(String)request.getAttribute("isprint");
		String lablogo=(String)request.getAttribute("lablogo");
		Object[] membersec=null; 
		LabMaster labInfo=(LabMaster)request.getAttribute("labInfo");
		String labcode = (String)session.getAttribute("labcode");
		String ccmFlag = (String)request.getAttribute("ccmFlag");
		String isGeneral = (String) request.getAttribute("isGeneral");
		boolean isGen = isGeneral!=null && isGeneral.equalsIgnoreCase("Y");
	%>
	<style type="text/css">
	
	#container{
		 font-family: Arial, Helvetica, sans-serif!important;
	}
	.fs-12{
		font-size: 12px;
	} 
	.fs-14{
		font-size: 14px;
	} 
	.fs-11{
		font-size: 11px;
	}
		@page {             
          size: 790px 1120px;
          margin-top: 49px;
          margin-left: 39px;
          margin-right: 39px;
          margin-buttom: 49px; 	
          border: 1px solid black;
          font-family: Arial, Helvetica, sans-serif!important; 
          <%-- @bottom-right {          		
             content: "Page " counter(page) " of " counter(pages);
             margin-bottom: 30px;
             margin-right: 10px;
        	font-size: 11px;     
          }
          @top-right {
          		<%if( Long.parseLong(projectid)>0){%>
             content: "Project:<%=projectdetails[4]!=null?projectdetails[4].toString(): " - "%>";
             <%}else if(Long.parseLong(divisionid)>0){%>
               	content: "Division:<%=divisiondetails[1]!=null?divisiondetails[1].toString(): " - "%>";
             <%}else if(Long.parseLong(initiationid)>0){ %>
             	content: "Pre-Project :<%=initiationdetails[1]!=null?initiationdetails[1].toString(): " - "%>";
             <%} else{%>
             	content: "<%=labdetails[1]!=null?labdetails[1].toString(): " - "%>";
             <%}%>
             margin-top: 30px;
             margin-right: 10px;
               font-size: 11px;
          }
          @top-left {
          	margin-top: 30px;
            margin-left: 10px;
            content: "<%=no[0]!=null?no[0].toString(): " - "%>/<%=no[1]!=null?no[1].toString(): " - "%>/<%=no[2]!=null?no[2].toString(): " - " %><%if(meetingcount>0){ %>#<%=meetingcount %><%} %>/<%=no[3]!=null?no[3].toString(): " - "%>";
             font-size: 11px;
          }      
          
          @top-center { 
           font-size: 11px;
          margin-top: 30px;
          content: "<%=committeescheduleeditdata[15]!=null?committeescheduleeditdata[15].toString(): " - "%>"; 
          
          }
          
           @bottom-center { 
             font-size: 11px;
	          margin-bottom: 30px;
	          content: "<%=committeescheduleeditdata[15]!=null?committeescheduleeditdata[15].toString(): " - "%>"; 
          
          } 
          
          @bottom-left { 
             font-size: 11px;
	          margin-bottom: 30px;
	          content: "<%=LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))%>"; 
          }  --%>
          }
	</style>
<meta charset="UTF-8">
<title><%=committeescheduleeditdata[8]%> Minutes View</title>
</head>
<body>

	<div id="container" align="center" style="margin: 15px;">
	  <table style=" border:1px solid black;  border-collapse: collapse; width:100%;">
	    <tr style="font-weight: bold;">
	      <!-- Left column with right border -->
	     <%--  <td width="30%" align="left" style="border-right:1px solid black; padding:5px; padding-left: 10px;">
	        <img style="width: 3cm; height: 3cm;" src="data:image/png;base64,<%=lablogo%>"><br>
	        <h4 style=" margin:0; padding:0;">RCI/PROGRAMME AD</h4>
	        <p style="margin:0; padding:0;">Kanchanbagh P.O., <br> Hyderabad - 58 </p>
	      </td> --%>
	      
	       <td width="33%" align="left" style="border-right:1px solid black; padding:5px; padding-left: 10px;">
	        <img style="width: 2cm; height: 2cm;" src="data:image/png;base64,<%=lablogo%>"><br>
	        <h4 class="fs-12" style="margin:0; padding:0;font-weight:bold;"><%=labdetails[2]!=null?labdetails[2].toString(): " - " %> (<%=labdetails[1]!=null?labdetails[1].toString(): " - "%>)</h4>
	        <p class="fs-11" style="margin:0; padding:0;"><%=labdetails[4]!=null?labdetails[4].toString(): " - " %></p>
	        <p class="fs-11" style="margin:0; padding:0;"><%=labdetails[5]!=null?labdetails[5].toString(): " - " %> - <%=labdetails[6]!=null?labdetails[6].toString(): " - " %> </p>
	      </td> 
	
	      <!-- Right column -->
	      <td class="fs-12" width="67%" align="left" style="padding:5px;font-weight:bold;">
	        <p>1.Meeting Title : <span > <%=committeescheduleeditdata[7]!=null?committeescheduleeditdata[7].toString().toUpperCase():" - "%> </span> </p>
	        <p>2.Venue&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; : <%=committeescheduleeditdata[12]!=null?committeescheduleeditdata[12].toString(): " - " %> </p>
	        <p>3.Date&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;: <%=committeescheduleeditdata[2]!=null?sdf.format(sdf1.parse(committeescheduleeditdata[2].toString())): " - " %> </p>
	        <p>4.Time&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;: <%=committeescheduleeditdata[3]!=null?committeescheduleeditdata[3].toString(): " - "%> </p>
	        <%if(!isGen){ %><p>5.<%=committeescheduleeditdata[11]!=null?committeescheduleeditdata[11].toString(): " - " %></p><%} %>
	      </td>
	    </tr>
	  </table>
	  <div>
	  	<%if(!isGen){ %><p class="fs-12" style="text-decoration: underline;font-size:14px!important;">Ref: Committee formation Letter No:&nbsp;&nbsp; <%=committeescheduleeditdata[27]!=null?committeescheduleeditdata[27].toString(): " - " %>&nbsp;&nbsp; Dated: <%=committeescheduleeditdata[28]!=null?sdf.format(sdf1.parse(committeescheduleeditdata[28].toString())): " - " %> </p><%} %>
	  </div>
	  
	  <!-------------------------------------------------Introduction And Opening Remarks ----------------------------------->
	  
	  <div align="left">
	  <!-- <h4> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;I.</h4> -->
	  	<%
	  		StringBuilder title1 = new StringBuilder();
			for (Object[] committeemin : committeeminutes) {
			if (committeemin[0].toString().equals("1") || committeemin[0].toString().equals("2")) {
				title1.append(committeemin[1].toString()+"-");
			}}
			String[] titlearr = title1.toString().split("-");
			String title = "";
			for(int i=0;i<titlearr.length;i++){
				title+=titlearr[i];
				if(i!=titlearr.length-1) title+=" & ";
			}
		%>
		
	   <!-- <h4 style="margin-bottom:10; padding:0;">&nbsp;&nbsp;&nbsp;&nbsp; I. &nbsp;&nbsp;	 Introduction & Opening Remarks: </h4> --> 
	   <h4 style="margin-bottom:10; padding:0;">&nbsp;&nbsp;&nbsp;&nbsp; I. &nbsp;&nbsp;	 <%=title %>: </h4> 
		  <%
		  	List<Object[]> introandopeningremarks = speclists.stream().filter(obj -> obj[3]!=null && (obj[3].toString().equalsIgnoreCase("1") || obj[3].toString().equalsIgnoreCase("2") )).toList();
				for (Object[] committeemin : committeeminutes) {
				if (committeemin[0].toString().equals("1") || committeemin[0].toString().equals("2")) {
			%>
			 
	  		
	  		<% int count = 0;

				for (Object[] speclist : introandopeningremarks)
				{ 
					if (speclist[3].toString().equals(committeemin[0].toString()) && speclist[1]!=null && !speclist[1].toString().trim().isEmpty()) 
					{
						count++;
				%>	
				<%-- <span class="fs-12" style="font-weight: bold; margin:0; padding:0; padding-left: 40px; " > <%=committeemin[0]!=null?committeemin[0].toString(): " - "%>. &nbsp;&nbsp; <%=committeemin[1]!=null?committeemin[1].toString(): " - "%> </span> --%>
				 <div class="fs-12" style="text-align: left;padding-left: 30px;"><%=speclist[1]!=null?speclist[1].toString(): " - "%></div> 
	  		<%	break;		
							}
						}
						if (count == 0) 
						{%>
						<!-- <br> -->
					 
	  		<%}}} %>
	  </div>
	  
	  <!----------------------------------------------------------------- ATTENDANCE -------------------------------------------------->
	  
	 	<%if(invitedlist.size()>0){ %>
		<% ArrayList<String> membertypes=new ArrayList<String>(Arrays.asList("CC","CS","PS","CI","CW","CO","CH"));
		   ArrayList<String> members = new ArrayList<String>();
		   String memberSec ="";
		   /* invitedlist.sort((obj,obj1) -> {
			  String code1 = (String) obj[3];
			  String code2 = (String) obj1[3];
			  
			    List<String> priority = Arrays.asList("CC", "CS");

			    boolean c1 = priority.contains(code1);
			    boolean c2 = priority.contains(code2);

			    if (c1 && c2) return code2.compareTo(code1);
			    if (c1) return -1;
			    if (c2) return 1; 
			    return code2.compareTo(code1); 
		   }); */
		   
		   
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
	  <div>
	  	<table style="border:1px solid black; border-collapse: collapse; width: 100%" >
	  		<thead class="fs-12">
	  			<tr>
	  				<th colspan="2" style="border-bottom: 1px solid black; padding:5px; text-align: left; font-size: 0.9rem!important; font-weight: bold!important;">&nbsp;&nbsp;&nbsp; II. &nbsp;&nbsp;Participants List (Committee Members and Invitees):</th>
	  			</tr>
	  		</thead>
	  		<tbody class="fs-11">	  		
	  		 <%if(memPresent > 0){ %>
			 <% 
			 	for(int i=0;i<invitedlist.size();i++)
				{
			 	if(invitedlist.get(i)[4].toString().equals("P") && membertypes.contains( invitedlist.get(i)[3].toString()) )
			 	{ j++;
			 		if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CS") ) membersec=invitedlist.get(i); 
			 		%>
			 		
			 		<%}}} %>
	  		<%if(memPresent > 0){ %>
			 <% 
			 	for(int i=0;i<invitedlist.size();i++)
				{
			 	if(invitedlist.get(i)[4].toString().equals("P") && membertypes.contains( invitedlist.get(i)[3].toString()) )
			 	{ j++;
			 		String name = (invitedlist.get(i)[6]!=null?invitedlist.get(i)[6].toString():"")+", "+(invitedlist.get(i)[7]!=null?invitedlist.get(i)[7].toString():"");
			 		if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CS") ){
			 			membersec=invitedlist.get(i);
			 			name+= " (Member Secretary)";
			 			memberSec = name;
			 		}else if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CC")){
			 			name+=" (Chairperson)";
			 			members.add(name);
			 		}
			 		else{
			 			if(!labcode.equalsIgnoreCase(invitedlist.get(i)[11].toString()) && invitedlist.get(i)[16]!=null){
			 				name = name.concat(" ("+invitedlist.get(i)[16].toString()+")");
			 			}
			 			//System.out.println("==========="+labcode+"======"+invitedlist.get(i)[11].toString()+"==========="+name);
			 			 members.add(name);	
			 		}
			 		}
			 	}} %>
	  		<%if(memAbscent > 0){ %>
			 <% 
			 	for(int i=0;i<invitedlist.size();i++)
				{
			 	if(invitedlist.get(i)[4].toString().equals("N") && membertypes.contains( invitedlist.get(i)[3].toString()) )
			 	{ j++;
			 	//if(invitedlist.get(i)[3].toString().equalsIgnoreCase("CS") ) membersec=invitedlist.get(i); 
			 	//if(invitedlist.get(i)[6].toString()!=null && invitedlist.get(i)[7].toString()!=null) members.add(invitedlist.get(i)[12].toString()+". " + invitedlist.get(i)[6].toString()+", "+invitedlist.get(i)[7].toString());
			 	}}} %>
	  			<%if(ParPresent > 0){ %>
			 <% 
			 	for(int i=0;i<invitedlist.size();i++)
				{
			 	if(invitedlist.get(i)[4].toString().equals("P") && !membertypes.contains( invitedlist.get(i)[3].toString()) )
			 	{ j++;
			 	//if(invitedlist.get(i)[6].toString()!=null && invitedlist.get(i)[7].toString()!=null) members.add(invitedlist.get(i)[12].toString()+". "+ invitedlist.get(i)[6].toString()+", "+invitedlist.get(i)[7].toString());
			 	if(invitedlist.get(i)[6].toString()!=null && invitedlist.get(i)[7].toString()!=null){
			 		String name = (invitedlist.get(i)[6]!=null?invitedlist.get(i)[6].toString():"")+", "+(invitedlist.get(i)[7]!=null?invitedlist.get(i)[7].toString():"");
			 		if(!labcode.equalsIgnoreCase(invitedlist.get(i)[11].toString()) && invitedlist.get(i)[16]!=null){
		 				name = name.concat(" ("+invitedlist.get(i)[16].toString()+" Through VC )");
		 			}
		 			//System.out.println("==========="+labcode+"======"+invitedlist.get(i)[11].toString()+"==========="+name);
			 		members.add(name);
			 	}
			 		
			 	}}} %>
	  			<%if(parAbscent > 0){ %>
			 <% 
			 	for(int i=0;i<invitedlist.size();i++)
				{
			 	if(invitedlist.get(i)[4].toString().equals("N") && !membertypes.contains( invitedlist.get(i)[3].toString()) )
			 	{ j++;
			 	//if(invitedlist.get(i)[6].toString()!=null && invitedlist.get(i)[7].toString()!=null) members.add(invitedlist.get(i)[12].toString()+". " +invitedlist.get(i)[6].toString()+", "+invitedlist.get(i)[7].toString());
			 	}}} %>
			 	
			 	<%-- <% for(int i=0;i<members.size();i++){
			 	%>
			 	<tr>
			 		<td width="50%" align="left" style="padding-left: 30px; padding:5px; border:1px solid black; "><%=members.get(i++) %></td>
	  				<td width="50%" align="left" style="padding-left: 30px; padding:5px; border:1px solid black; "><%if(i<members.size()){ %><%=members.get(i) %> <%} %></td>
			 	</tr>
			 	<%} %> --%>
			 	
			 	  <%
			 	  if(!memberSec.isBlank() && !memberSec.isEmpty()) members.add(memberSec);

			 	  int leftsize = members.size() / 2 + (members.size() % 2 == 0 ? 0 : 1);
			 	  int rightsize = members.size()-leftsize;
			 	  List<String> leftlist = new ArrayList<String>(members.subList(0, leftsize));
			 	  List<String> rightlist = new ArrayList<String>(members.subList(leftsize,members.size()));
			 	  int rightsn=leftsize;
			 	  for(int i=0;i<leftsize || i<rightsize;i++){
			 	%>
			 	<tr>
			 		<td width="50%" align="left" style="padding-left: 30px; padding:5px; border:1px solid black; "><%=(i+1)+". " + leftlist.get(i) %></td>
	  				<td width="50%" align="left" style="padding-left: 30px; padding:5px; border:1px solid black; "><%if(i<rightsize){ %><%=++rightsn +". "+ rightlist.get(i) %> <%} %></td>
			 	</tr>
			 	<%} %> 
			 	
	  		
	  		</tbody>
	  	</table>
	  	<%} %>
	  </div>
	  
	   <!----------------------------------------------------------------- Deliberations and Action Points ------------------------------------------------->
	  
	  <div align="left">
	  	<h4>&nbsp;&nbsp;&nbsp;&nbsp; III. &nbsp;&nbsp; Presentation / Deliberations  </h4>
	  </div>
	  <div> 
	  <p class="fs-12" style="text-align: left;padding-left:30px;">Presentation/ Discussion were made on the progress of activities one by one. During this presentation the Chairperson provided the Following directions.</p>
	  	<p class="fs-12">Item Code/Type : A: Action, C: Discussion, D: Decision, P: Presentation</p>
	  	<table class="fs-12" style="border:1px solid black; border-collapse: collapse; width: 100%" >
	  		<thead class="fs-12"> 
	  			<tr class="fs-12">
	  				<th width="5%" style="border: 1px solid black; padding:5px;">S.No</th>
	  				<th width="1%" style="border: 1px solid black; padding:5px;">Type</th>
	  				<th width="60%" style="border: 1px solid black; padding:5px; text-align: left;">Directions/Action Points</th>
	  				<th width="22%" style="border: 1px solid black; padding:5px; text-align: left;">Action By</th>
	  				<th width="12%" style="border: 1px solid black; padding:5px; text-align: left;">PDC</th>
	  			</tr>
	  		</thead>
	  		<tbody class="fs-12">
	  		<% int count=0,index=0, indexcount=1;
				Long agenda=0L;
				
				Map<String, List<Object[]>> actionslist = speclists!=null && speclists.size()>0?speclists.stream()
						  .collect(Collectors.groupingBy(array -> array[0].toString() , LinkedHashMap::new, Collectors.toList())) : new HashMap<>();
						 
					if (actionslist!=null && actionslist.size() > 0) {
						for (Map.Entry<String, List<Object[]>> map : actionslist.entrySet()) {
							int i=1;
						    List<Object[]> values = map.getValue();						    
						    List<String> divisionNames = values.stream().map(row ->{
													    	if(row[20]!=null && row[21]!=null && row[22]!=null){
													    		return row[21].toString();
													    	}
													    	return null;
													    })
													    .filter(Objects::nonNull)
													    .distinct()
													    .collect(Collectors.toList());
						    
						    List<Object[]> employeeNames = values.stream()
						    		.filter( row -> row[20]==null && (Integer.parseInt(row[3].toString()) == 3 || Integer.parseInt(row[3].toString()) == 5 ) )
						    		.map( row -> {
						    			if(row[13]!=null && row[12]!=null){
						    				return new Object[] {row[13].toString(), row[12].toString()};
						    			}else return null;
						    		})
						    		.filter(Objects::nonNull)
						    		.collect(Collectors.toList());
						    String names = String.join(", ", divisionNames);						    
						    int employeeCount = -1;
						    
						    for (Object[] obj : values) {
						    	String pdc = obj[12] !=null ? obj[12].toString(): null;
					            int rowSpan = divisionNames!=null && divisionNames.size() > 0 ? 1 + employeeNames.size() : values.size() ;
						        if(((obj[3] != null && Integer.parseInt(obj[3].toString()) == 3) || ( obj[3] != null && Integer.parseInt(obj[3].toString()) == 5 )) && (obj[7].toString().equalsIgnoreCase("A") || obj[7].toString().equalsIgnoreCase("C") || obj[7].toString().equalsIgnoreCase("D"))){
						            if(obj[5]!=null && obj[5].toString().equalsIgnoreCase("9") && obj[7]!=null && obj[7].toString().equalsIgnoreCase("C")) continue;
						            if(obj[18] != null && Long.parseLong(obj[18].toString())!=agenda ) {
						                indexcount = 1;
						                count++;
						                /* if(obj[3] != null && Integer.parseInt(obj[3].toString()) == 3 && count==1){ */
						                	%>
						<%-- <tr> 		
						    <td style="border: 1px solid black; padding:5px; font-weight:bold; text-align: center;" colspan="4">
						        AGENDA ACTIONS
						    </td>
						</tr>
						<%}else if(obj[3] != null && Integer.parseInt(obj[3].toString()) == 5){%>
						<tr>  		
						    <td style="border: 1px solid black; padding:5px; font-weight:bold;" colspan="4">
						        Other Outcomes/Action points
						    </td>
						</tr>	
	               		<%}	%> --%>
						<tr>
						    <td class="fs-12" style="border: 1px solid black; font-weight:bold; padding:5px;"><%= ++index %></td>	  			
						    <td class="fs-12" style="border: 1px solid black; padding:5px; font-weight:bold; text-align: left;" colspan="4">
						       <%if(Integer.parseInt(obj[3].toString())==3) {
						    	   String project = "";
						    	   if (obj[10] != null) {
						    	       String temp = obj[10].toString();
						    	       int idx = temp.indexOf("(");
						    	       if (idx > 0) {
						    	           project = temp.substring(0, idx).trim();
						    	       } else {
						    	           project = temp.trim(); 
						    	       }
						    	   } 
						    	   String groupname = "";
						    	   if(obj[19]!=null && obj[19].toString().length()>0){
						    		   groupname=obj[19].toString();
						    	   }
						    	   %>
						    	   <%= obj[10] != null ?"Presentation by/Discussion by : "+ (obj[16]!=null?obj[16].toString().trim():"-")+" / "+ project+ (groupname.length()>0 ? " ("+ groupname +")" : "") : "-" %>
						       <%} else if(Integer.parseInt(obj[3].toString())==5){ %>Other outcome/Action Points       <%} %>
						    </td>
						</tr> 
						<%
			                agenda = Long.parseLong(obj[18].toString()); 
			            }
						%>
						<tr>
						<%
						String minutesSubId = obj[5]!=null ? obj[5].toString():"";
						if(i==1){
							
							String idarck = obj[7]!=null ? obj[7].toString(): "";
							%>
							    <td rowspan="<%=rowSpan %>"  style="border: 1px solid black; padding:5px; text-align: center;"><%= index + "." + indexcount++ %></td>
							    <td rowspan="<%=rowSpan %>" style="border: 1px solid black; padding:5px; text-align: center;">
										<% if(minutesSubId.equalsIgnoreCase("7")) { %> P <%} 
										else { %><%= idarck%><%}%>
								</td>	  			
							    <td rowspan="<%=rowSpan%>" colspan="<%=(!minutesSubId.equalsIgnoreCase("7") && !minutesSubId.equalsIgnoreCase("8")) ? 1 : 3 %>"  style="border: 1px solid black; padding:5px; text-align: left;">
							    <%
								String editorContent = obj[1]!=null?obj[1].toString(): " - ";
								//System.out.println(editorContent);

								// First convert ordered list to numbered format
								Pattern pattern = Pattern.compile("(?i)<ol[^>]*>(.*?)</ol>", Pattern.DOTALL);
								Matcher matcher = pattern.matcher(editorContent);
								
								while (matcher.find()) {
								    String olContent = matcher.group(1);
								
								    Pattern liPattern = Pattern.compile("(?i)<li[^>]*>(.*?)</li>");
								    Matcher liMatcher = liPattern.matcher(olContent);
								
								    StringBuilder numberedList = new StringBuilder();
								    int num = 1;
								
								    while (liMatcher.find()) {
								        numberedList.append(num++)
								                    .append(". ")
								                    .append(liMatcher.group(1).trim())
								                    .append("<br/>");
								    }
								
								    editorContent = editorContent.replace(matcher.group(0), numberedList.toString());
								}
								
								// Then remove other unwanted tags
								editorContent = editorContent.replaceAll("(?i)</?(p|div|strong|ul)[^>]*>", "");
								
								%>
							        <% if(idarck.equalsIgnoreCase("A")) { %> <%=editorContent%> <%} %>
							        <% if(idarck.equalsIgnoreCase("C")) { %> <%=editorContent%> <%} %>
									<% if(idarck.equalsIgnoreCase("D")) { %> <%=editorContent%> <%} %>
							    </td>
								<%} %>
						    <%-- <td rowspan="1" style="border: 1px solid black; padding:5px; text-align: left;"><%= obj[13] != null ? obj[13].toString() : " - " %></td>	  			
						    <td rowspan="1" style="border: 1px solid black; padding:5px; text-align: left;"><%= obj[12] != null ? fc.SqlToRegularDate(obj[12].toString()) : " - " %></td>	   --%>
						    <% if ((!minutesSubId.equalsIgnoreCase("7") && !minutesSubId.equalsIgnoreCase("8")) && divisionNames!=null && divisionNames.size()<= 0) { %>
						        <td style="border:1px solid black;padding:5px; text-align:left;">
						            <%= obj[13] != null ? obj[13] : "-" %>
						        </td>
						        <td style="border:1px solid black;padding:5px; text-align:left;">
						            <%= pdc != null ? fc.SqlToRegularDate(pdc) : "-" %>
						        </td>
						    <% } %>
						    <% if((!minutesSubId.equalsIgnoreCase("7") && !minutesSubId.equalsIgnoreCase("8")) && divisionNames!=null && divisionNames.size()>0){ %>
						        <td style="border:1px solid black;padding:5px; text-align:left;">
						           	<%if(employeeCount<0){ %><%= names  %><%}
						           	else if(employeeNames!=null && employeeNames.size()>0) {%>
						           		<%=employeeNames.get(employeeCount)[0].toString()  %>
						           	<%}%>
						        </td>
						        <td style="border:1px solid black;padding:5px; text-align:left;">
						        <%= pdc != null ? fc.SqlToRegularDate(pdc) : "-"  %>
						        </td>
						    <% } %>			
						</tr>
						<%
								i=0;
						employeeCount++;
						        }
						        if(divisionNames!=null && divisionNames.size()>0 && employeeNames!=null && (employeeNames.size()<=employeeCount || employeeNames.size()==0)) break;
						    }
					}
				 if(count==0){%>
					<tr>
						<td class="std" style="text-align :center;border:1px solid black;padding:7px;"  colspan="5">No Minutes details Added</td>
					</tr>
				<%}}
				else {
				%>
					<tr>
						<td class="std" style="text-align :center;border:1px solid black;padding:7px;"  colspan="5">No Minutes details Added</td>
					</tr>
				<%}%>
	  		</tbody>
	  	</table>
	  </div>
	  
	   <!----------------------------------------------------------------- Conclusions & Recommendations ------------------------------------------->
	  
	  <%-- <div align="left">
	  	<%
			 if(ccmFlag==null) {
			 for (Object[] committeemin : committeeminutes) { 
				 if ( committeemin[0].toString().equals("6")) { %>
	  		<h4>&nbsp;&nbsp;&nbsp;&nbsp; IV. &nbsp;&nbsp; Conclusions &amp; Recommendations: </h4>
		  	<% for (Object[] speclist : speclists)
							{
								if (speclist[3].toString().equals(committeemin[0].toString())) 
								{
									if(speclist[3].toString().equals("6")){
									%>
			<div style="padding-left: 40px;"> <%if(speclist[1]!=null && !speclist[1].toString().trim().equalsIgnoreCase("")){%> <%=speclist[1]!=null?speclist[1].toString(): " - "%>  <%}else{ %> - <%} %></div>
				<%}} } } }} %>
	  </div> --%>
	  
	  <!------------------------------------------------------------------ Final -------------------------------------------------------->
	  <div style="width: 650px;margin-left: 15px; margin-top:25px; ">
			<div align="left" class="fs-11" >
				These minutes are issued with the approval of the Chairperson.
			</div>
			<div align="left" style="padding-right: 0rem;padding-bottom: 0rem; margin-right: 0px" class="fs-12">
				<!-- <br>Date :&emsp;&emsp;&emsp;&emsp;&emsp;  <br>Time :&emsp;&emsp;&emsp;&emsp;&emsp; -->
				<%if(membersec!=null){ %>
					<div align="right" style="padding-right: 0rem;padding-bottom: 2rem;" class="fs-12">
						<!-- <b style="margin-right: 50px!important;">Signed By</b> -->
						<br><br><%if(membersec[6]!=null){%><%= membersec[6].toString() + (membersec[7]!=null?", "+membersec[7].toString():"") %><%} %>
						<br>(Member Secretary)
					</div>
					<div align="left" >
						<b>To</b>
						<p>All Participants</p>
						<p>Copy To :</p>
						<p>Programme Director,AD</p>
					</div>
				<%} %>
			</div>
			<!-- <div align="left" ><b>NOTE : </b>Action item details are enclosed as Annexure - AI.</div> -->		
		</div> 
	</div>


</body>
</html>