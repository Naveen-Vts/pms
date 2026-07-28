<%@page import="com.vts.pfms.model.BriefingHeadingDetails"%>
<%@page import="com.vts.pfms.model.BriefingHeading"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.vts.*,java.text.SimpleDateFormat,java.time.LocalDate"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<jsp:include page="../static/header.jsp"></jsp:include>
<spring:url value="/resources/ckeditor/ckeditor.js" var="ckeditor" />
<spring:url value="/resources/css/print/projectBriefingPaperNew.css" var="projectBriefingPaperNew" />
<link href="${projectBriefingPaperNew}" rel="stylesheet" />
<spring:url value="/resources/css/action/actionCommon.css" var="actionCommon" />
<link href="${actionCommon}" rel="stylesheet" />
<spring:url value="/resources/ckeditor/contents.css" var="contentCss" />
<link href="${contentCss}" rel="stylesheet" />
<spring:url value="/resources/css/sweetalert2.min.css" var="sweetalertCss" />
<spring:url value="/resources/js/sweetalert2.min.js" var="sweetalertJs" />
<link href="${sweetalertCss}" rel="stylesheet" />
<script src="${sweetalertJs}"></script>
<script src="${ckeditor}"></script>
<title>Project Briefing Paper</title>
<style type="text/css">
/* Card */
.modern-card {
    border-radius: 14px;
    background: #ffffff;
}

/* Header */
.modern-header {
    background: linear-gradient(135deg, #4e73df, #224abe);
    color: #fff;
    border-radius: 14px 14px 0 0;
    padding: 12px 20px;
}

/* Editor Wrapper */
.editor-wrapper {
    border: 1px solid #e3e6f0;
    border-radius: 10px;
    padding: 10px;
    background: #fafbff;
    transition: 0.3s;
}

.editor-wrapper:focus-within {
    border-color: #4e73df;
    box-shadow: 0 0 6px rgba(78, 115, 223, 0.3);
}

/* Buttons */
.btn-success {
    border-radius: 8px;
}

.btn-outline-secondary {
    border-radius: 8px;
}
</style>

<%
	String projectid = (String) request.getAttribute("projectid"); 
	String committeeid = (String) request.getAttribute("committeeid"); 
	String scheduleid = (String) request.getAttribute("scheduleid"); 
	String headingid = (String) request.getAttribute("headingid"); 
	Object[] lastmeetingVenue =  (Object[]) request.getAttribute("lastmeetingVenue");	
	BriefingHeadingDetails detailData =  (BriefingHeadingDetails) request.getAttribute("detailData");	
	String headDetails =  (String) request.getAttribute("headDetails");	
	String projectName= null;

%>
</head>
<body>

<div class="container mt-4">

    <div class="card">
        <div class="card-header">
            <h5>
                <%= detailData != null ? " Edit Details" : " Add Details"%> for <%= headDetails!=null ? headDetails : "" %>
            </h5>
        </div>

        <div class="card-body">

            <form action="DetailsAddEditSubmit.htm" method="post" onsubmit="return confirm('Are you sure to submit?')">

                <!-- TEXT EDITOR -->
                <div class="form-group">
                    <label><b>Details</b></label>

                    <textarea  id="ckeditor" name="content">
                        <%=detailData!=null? detailData.getDetails() : "" %>
                    </textarea>
                </div>

                <!-- Buttons -->
                <div class="text-center mt-3">
                    <button type="submit" class="btn btn-sm submit">
                         Save
                    </button>

                    <a href="BriefingPaperV2.htm?projectid=${projectid}&committeeid=${committeeid}&scheduleid=${scheduleid}&headingid=${headingid}" class="btn btn-sm back">
                         Back
                    </a>
                </div>
                
                <input type="hidden" name="detailsid" value="<%= detailData!=null ? detailData.getDetailsId() : "" %>" />
                <input type="hidden" name="headingid" id="headingID" value="<%=headingid %>" />
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<input type="hidden" name="projectid" value="<%=projectid%>"/>
				<input type="hidden" name="committeeid" value="<%=committeeid%>"/>	
				<input type="hidden" name="scheduleid" value="<%=scheduleid%>"/>

            </form>
           

        </div>
    </div>

</div>

<script>
var editor_config = {
	    maxlength: '4000',

	    extraPlugins: 'specialchar',   // important

	    toolbar: [
	        {
	            name: 'clipboard',
	            items: ['PasteFromWord', '-', 'Undo', 'Redo']
	        },
	        {
	            name: 'basicstyles',
	            items: [
	                'Bold', 'Italic', 'Underline', 'Strike',
	                'RemoveFormat', 'Subscript', 'Superscript',
	                'SpecialChar'   // fixed here
	            ]
	        },
	        {
	            name: 'links',
	            items: ['Link', 'Unlink']
	        },
	        {
	            name: 'paragraph',
	            items: [
	                'NumberedList', 'BulletedList',
	                '-', 'Outdent', 'Indent', '-', 'Blockquote'
	            ]
	        },
	        {
	            name: 'insert',
	            items: ['Image', 'Table']
	        },
	        {
	            name: 'editing',
	            items: ['Scayt']
	        },
	        '/',
	        {
	            name: 'styles',
	            items: ['Format', 'Font', 'FontSize']
	        },
	        {
	            name: 'colors',
	            items: ['TextColor', 'BGColor', 'CopyFormatting']
	        },
	        {
	            name: 'align',
	            items: [
	                'JustifyLeft', 'JustifyCenter',
	                'JustifyRight', 'JustifyBlock'
	            ]
	        }
	    ],

	    // removed wrong Specialchar from here
	    removeButtons: 'Underline,Strike,Subscript,Superscript,Anchor,Styles',

	    customConfig: '',
	    disallowedContent: 'img{width,height,float}',
	    extraAllowedContent: 'img[width,height,align]',
	    height: 300,
	    

		contentsCss: [CKEDITOR.basePath + 'mystyles.css'],
	    bodyClass: 'document-editor',
	    
	    // DEFAULT FONT SIZE IN DROPDOWN
	    fontSize_defaultLabel: '14px',

	    // FORCE FONT SIZE EVERYWHERE (INCLUDING TABLES)
	    contentsStyle: `
	        body { font-size: 14px; }
	        p, div { font-size: 14px; }
	        table, td, th { font-size: 14px; }
	    `,
	    on: {
		    instanceReady: function (ev) {
		        const editor = ev.editor;
		
		        // Function to force font size inside tables
		        function fixTableFont() {
		            const tables = editor.document.find('table');
		
		            for (let i = 0; i < tables.count(); i++) {
		                const table = tables.getItem(i);
		                const cells = table.find('td, th');
		
		                for (let j = 0; j < cells.count(); j++) {
		                    const cell = cells.getItem(j);
		
		                    // Remove inline font-size
		                    cell.removeStyle('font-size');
		
		                    // Apply 14px
		                    cell.setStyle('font-size', '14px');
		                }
		            }
		        }
		
		        // Run on load
		        fixTableFont();
		
		        // Run whenever content changes (paste, typing, table insert)
		        editor.on('change', fixTableFont);
		        editor.on('afterPaste', fixTableFont);
		    }
		},
	    format_tags: 'p;h1;h2;h3;pre',
	    removeDialogTabs: 'image:advanced;link:advanced',

	    stylesSet: [
	        { name: 'Marker', element: 'span', attributes: { 'class': 'marker' } },
	        { name: 'Cited Work', element: 'cite' },
	        { name: 'Inline Quotation', element: 'q' },
	        {
	            name: 'Special Container',
	            element: 'div',
	            styles: {
	                padding: '5px 10px',
	                background: '#eee',
	                border: '1px solid #ccc'
	            }
	        },
	        {
	            name: 'Compact table',
	            element: 'table',
	            attributes: {
	                cellpadding: '5',
	                cellspacing: '0',
	                border: '1',
	                bordercolor: '#ccc'
	            },
	            styles: {
	                'border-collapse': 'collapse'
	            }
	        },
	        {
	            name: 'Borderless Table',
	            element: 'table',
	            styles: {
	                'border-style': 'hidden',
	                'background-color': '#E6E6FA'
	            }
	        },
	        {
	            name: 'Square Bulleted List',
	            element: 'ul',
	            styles: { 'list-style-type': 'square' }
	        }
	    ]
	    
	};

	// initialize editor
	CKEDITOR.replace('ckeditor', editor_config);
</script>
</body>
</html>