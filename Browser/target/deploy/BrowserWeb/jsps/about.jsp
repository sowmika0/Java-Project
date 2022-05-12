<%
response.setHeader("Cache-Control","no-cache,no-store");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" contentType="text/html; charset=UTF-8"%>

<% String release=request.getParameter("release"); %>
<% String build=request.getParameter("build"); %>
<% String skin=request.getParameter("skin"); %>

<html>
   <head>
      <link rel="stylesheet" type="text/css" href="plaf/style/<%= skin %>/general.css">
      <title>About T24 Browser</title>
      <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
</head>

   <body topmargin="2" leftmargin="2">
<TABLE cellSpacing="0" cellPadding="0" border="0">
            <TBODY>
               <TR>
                  <TD colSpan="2"></TD>
                  <TD bgColor="#d9d9d9" colSpan="3"><IMG height="1" src="plaf/images/<%= skin %>/block.gif" width="1"></TD></TR>
               <TR>
                  <TD></TD>
                  <TD width="3" background="plaf/images/<%= skin %>/tab_back2.gif" class="fieldname"><IMG height="3" src="plaf/images/<%= skin %>/tab_tl.gif" width="3"></TD>
                  <TD background="plaf/images/<%= skin %>/tab_back2.gif" class="fieldname" colSpan="3"><IMG height="1" src="plaf/images/<%= skin %>/block.gif" width="1"></TD>
                  <TD vAlign="top" width="3" background="plaf/images/<%= skin %>/tab_back2.gif" class="fieldname"><IMG height="3" src="plaf/images/<%= skin %>/tab_tr.gif" width="3"></TD></TR>

               <TR>
                  <TD width="1" bgColor="#cccccc"><IMG height="1" src="plaf/images/<%= skin %>/block.gif" width="1"></TD>
                  <TD background="plaf/images/<%= skin %>/tab_back.gif" class="fieldname"><IMG height="1" src="plaf/images/<%= skin %>/block.gif" width="1"></TD>
                  <TD background="plaf/images/<%= skin %>/tab_back.gif" class="caption" colSpan="3"><B>About T24 Browser</B><BR><IMG height="2" src="plaf/images/<%= skin %>/block.gif" width="1"></TD>
                  <TD background="plaf/images/<%= skin %>/tab_back.gif" class="fieldname"><IMG height="1" src="plaf/images/<%= skin %>/block.gif" width="1"></TD>
                  <TD width="1" bgColor="#5a5a5a" rowSpan="4"><IMG height="1" src="plaf/images/<%= skin %>/block.gif" width="1"></TD></TR>

               <TR>
                  <TD width="1" bgColor="#cccccc" rowSpan="2"><IMG height="1" src="plaf/images/<%= skin %>/block.gif" width="1"></TD>
                  <TD bgColor="#ffffff"><IMG height="1" src="plaf/images/<%= skin %>/block.gif" width="1"></TD>
                  <TD bgColor="#ffffff">
                  <table border="0" cellspacing="0">
  <tr>
    <td><font size="5">TEMENOS T24</font></td>
    <td valign="top"><b>TM</b></td>
    <td></td>
  </tr>
  <tr>
    <td>
      <b>&nbsp;<a href="http://www.temenos.com">www.temenos.com</a></b>
    </td>
    <td>
    </td>
    <td></td>
  </tr>
  <tr>
    <td>
      <font size="5">T24 Browser</font>
    </td>
    <td>
    </td>
    <td></td>
  </tr>
  <tr>
    <td>Release <%= release %></td>
    <td></td>
    <td></td>
  </tr>
  <tr>
    <td>Copyright 2002 - 2010 Temenos Holdings NV</td>
    <td></td>
    <td></td>
  </tr>
  <tr>
    <td></td>
  </tr>
</table>

                  </TD>
                  <TD bgColor="#ffffff"><IMG height="8" src="plaf/images/<%= skin %>/block.gif" width="1"></tr>
<TR>
                  <TD bgColor="#777777" colSpan="6"><IMG height="1" src="plaf/images/<%= skin %>/block.gif" width="1"></TD></TR></TBODY></TABLE>
   </body></html>