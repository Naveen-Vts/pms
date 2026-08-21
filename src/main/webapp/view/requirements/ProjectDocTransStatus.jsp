<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"  import="java.util.*,java.text.SimpleDateFormat"%>
 
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<%String docType = (String)request.getAttribute("docType"); %>
<title>
	<%if(docType!=null && docType.equalsIgnoreCase("R")) {%>
		Requirement 
	<%} else if(docType!=null && docType.equalsIgnoreCase("S")) {%>
		Specifications
	<%}else if(docType!=null && docType.equalsIgnoreCase("T")) {%>
		Test Plan
	<%} %>
	Status
</title>
<jsp:include page="../static/header.jsp"></jsp:include>

<style type="text/css">


  html, body {
    margin: 0;
    padding: 0;
    font-family: Helvetica, sans-serif;
  }
  body {
    background: #25303B;
  }
  section#timeline {
    width: 80%;
    margin: 20px auto;
    position: relative;
  }
  section#timeline:before {
    content: '';
    display: block;
    position: absolute;
    left: 50%;
    top: 0;
    margin: 0 0 0 -1px;
    width: 2px;
    height: 100%;
    background:black;
  }
  section#timeline article {
    width: 100%;
    margin: 0 0 20px 0;
    position: relative;
  }
  section#timeline article:after {
    content: '';
    display: block;
    clear: both;
  }
  section#timeline article div.inner {
    width: 40%;
    float: left;
    margin: 5px 0 0 0;
    border-radius: 6px;
  }
  section#timeline article div.inner span.date {
    display: block;
    width: 70px;
    height: 65px;
    padding: 5px 0;
    position: absolute;
    top: 0;
    left: 50%;
    margin: 0 0 0 -32px;
    border-radius: 100%;
    font-size: 12px;
    font-weight: 900;
    text-transform: uppercase;
    background: #25303B;
    color: rgba(255,255,255,255);
   /*  border: 2px solid rgba(255,255,255,0.2); */
    box-shadow: 0 0 0 7px #5c6166;
  }
  section#timeline article div.inner span.date span {
    display: block;
    text-align: center;
  }
  section#timeline article div.inner span.date span.day {
    font-size: 12px;
  }
  section#timeline article div.inner span.date span.month {
    font-size: 13px;
  }
  section#timeline article div.inner span.date span.year {
    font-size: 9px;
  }
  section#timeline article div.inner h2 {
    padding: 10px;
    margin: 0;
    color: #fff;
    font-size: 14px;
    text-transform: uppercase;
    letter-spacing: 0px;
    border-radius: 6px 6px 0 0;
    position: relative;
    font-family: 'Muli',sans-serif;
    height: 31px;
  }
  section#timeline article div.inner h2:after {
    content: '';
    position: absolute;
    top: 19px;
    right: -5px;
      width: 10px; 
      height: 10px;
    -webkit-transform: rotate(-45deg);
  }
  section#timeline article div.inner p {
    
    padding-top:5px;
    padding-left:15px;
    padding-bottom:10px;
    margin: 0;
    font-size: 14px;
    background: #fff;
    color: #656565;
    border-radius: 0 0 6px 6px;
    font-family: 'Lato',sans-serif;
  }
  section#timeline article:nth-child(2n+2) div.inner {
    float: right;
  }
  section#timeline article:nth-child(2n+2) div.inner h2:after {
    left: -5px;
  }
  section#timeline article:nth-child(odd) div.inner h2 {
   background-color: var(--my-color-var);
  }
  section#timeline article:nth-child(odd) div.inner h2:after {
   background-color: var(--my-color-var);
  }
  section#timeline article:nth-child(even) div.inner h2 {
   background-color: var(--my-color-var);
  }
  section#timeline article:nth-child(even) div.inner h2:after {
    background: var(--my-color-var);
  }
/* timeline customization */

.remarks_title{
	font-size: 12px;
	font-weight: 800;
	color:black;
}
</style>

</head>
<body>
<%
List<Object[]> statuslist = (List<Object[]>)request.getAttribute("transactionList");
String docInitiationId = (String)request.getAttribute("docInitiationId");

%>

	 <div class="page card dashboard-card">
		 <div class="mt-2 mr-5" align="right">
			<form action="#">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<input type="hidden" name="docInitiationId" value="<%=docInitiationId %>" />
				<input type="hidden" name="docType" value="<%=docType %>" />
				<button type="submit" class="btn btn-sm btn-primary" formaction="ProjectDocTransactionDownload.htm" formmethod="POST" formtarget="_blank" formnovalidate="formnovalidate"
				 style="font-weight: 600;text-transform: uppercase;background: darkblue;">Print &emsp; <img alt="" src="view/images/trackingPrint.png"> </button>
			</form>
		</div>	

	      <section id="timeline">
	      
	       <% int count=1;
	       	 SimpleDateFormat month=new SimpleDateFormat("MMM");
			 SimpleDateFormat day=new SimpleDateFormat("dd");
			 SimpleDateFormat year=new SimpleDateFormat("yyyy");
			 SimpleDateFormat time=new SimpleDateFormat("HH:mm");
			 for(Object[] object:statuslist){
			 
			 %>
	      
			<article>
				<div class="inner">
					<span class="date">
					<%
					    Date date = null;
					
					    if (object[4] != null) {
					        String dateString = object[4].toString();
					        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					        date = inputFormat.parse(dateString);
					    }
					%>
						<span class="day">
						    <%= date != null ? day.format(date) : "" %>
						</span>
						
						<span class="month">
						    <%= date != null ? month.format(date) : "" %>
						</span>
						
						<span class="year">
						    <%= date != null ? year.format(date) : "" %>
						</span>
					</span>
					<h2 style="background-color: <%=object[7]%>;--my-color-var: <%=object[7]%>;" ><%=object[6]!=null?StringEscapeUtils.escapeHtml4(object[6].toString()): "" %> at <%=time.format(object[4]) %></h2> 
					<p style="background-color:  #f0f2f5;">
						<span class="remarks_title">Action By : </span>
						<%=object[2]!=null?StringEscapeUtils.escapeHtml4(object[2].toString()): "" %>, <%=object[3]!=null?StringEscapeUtils.escapeHtml4(object[3].toString()): "" %><br>
						<%if(object[5]!= null) { %>
							<span class="remarks_title">Remarks : </span>
							<%=StringEscapeUtils.escapeHtml4(object[5].toString()) %>
						<%}else{ %> 
							<span class="remarks_title">No Remarks </span> 
						<%} %>
					</p>
				</div>
			</article>
			  
			<%count++;}%> 		
		</section>
		
</div>


</body>
</html>