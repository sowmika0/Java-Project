<%
response.setHeader("Cache-Control","no-cache,no-store");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" contentType="text/html; charset=UTF-8"%>

<% 
String enrichment=request.getParameter("enrichment");
String skin=request.getParameter("skin");
String trans_upload=request.getParameter("trans_upload");
String trans_uploading=request.getParameter("trans_uploading");
String fragment=request.getParameter("fragment");
%>

<html>
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<style>
		   html,body {border:0px; padding:0px; margin:0px;}
		</style>
		<script>
			/**
			 * Sets some fields on the page and submits the form.
			 */
			function submitForm() {

				// Get the current fragment
				var fragment = window.document.getElementById("fragment");
				// Append with form name
				var formId = "appreq"+ fragment.value;
				// Get the UPLOAD.TYPE from the parent page, if it exists.
				var parentUploadType = "";
				var parentUploadTypeObj = window.parent.document.getElementById("fieldName:UPLOAD.TYPE");
				if (parentUploadTypeObj != null) {
					parentUploadType = parentUploadTypeObj.value;
				}
				if (parentUploadType != null && parentUploadType != "") {
					var uploadType = document.getElementById("uploadType");
					uploadType.value = parentUploadType;
				}

				// These fields are needed incase the UPLOAD.TYPE has been set as an AUT.NEW.CONTENT field.
				document.getElementById("application").value   = window.parent.getFormFieldValue(formId,"application");
				document.getElementById("companyId").value     = window.parent.getFormFieldValue(formId,"companyId");
				document.getElementById("transactionId").value = window.parent.getFormFieldValue(formId,"transactionId");

				// This value is needed to show the right upload icon to the user.
				document.getElementById("skin").value          = window.parent.getFormFieldValue(formId,"skin");

				// Hide the form and show the 'uploading...' div
				var uploadForm   = document.getElementById("uploadForm");
				var uploadingDiv = document.getElementById("uploadingDiv");
				uploadForm.style.display = "none";
				uploadingDiv.style.display = "block";
				// remove the fragment to bypass servlet parameter check
				var uploadForm = document.getElementById("uploadForm");
				uploadForm.removeChild(fragment);
				// Submit the form
				uploadForm.submit();
			}


			/**
			 * Sets the enrichment of to display any error messages to the user.
			 */
			function setEnrichment(enrichment) {
				if (enrichment == "null") {
					enrichment = "";
				}
				if(enri != null && enri != 'undefined')
				{
				
				var enri = window.parent.document.getElementById("enri_FILE.NAME");
				enri.innerHTML = enrichment;
				}
				}
			  function setFocus() {
			      var focusInput = document.getElementById("fileInput");
			      if (focusInput != null && focusInput != "undefined")
			      {
			         focusInput.focus();
			      }
			}
			
		</script>
	</head>
	<body onLoad="setEnrichment('<%= enrichment %>');"  onfocus="setFocus();">
		<form name="uploadForm" id="uploadForm" method="POST" ENCTYPE="multipart/form-data" action="../servlet/FileUploadServlet">
			<input type="hidden" name="application" id="application" value="">
			<input type="hidden" name="companyId" id="companyId" value="">
			<input type="hidden" name="transactionId" id="transactionId" value="">
			<input type="hidden" name="skin" id="skin" value="">
			<input type="hidden" name="uploadType" id="uploadType" value="">
			<input type="hidden" name="fragment" id="fragment" value="<%=fragment %>">
	        <input type="file" name="filedata"  tabindex="0" id="fileInput">&nbsp;
	        <img src="../plaf/images/<%= skin %>/deal/upload.gif" style="vertical-align:middle;padding-bottom:3px;" onclick="submitForm();" onkeypress="submitForm();"  title="<%= trans_upload %>"  tabindex="0">
		</form>
		<div id="uploadingDiv" style="font-family:'Trebuchet MS',Tahoma,Arial,Helvetica,sans-serif;font-size:smaller;display:none;"><%= trans_uploading %> <img src="../plaf/images/<%= skin %>/deal/uploading.gif"></div>
	</body>
</html>