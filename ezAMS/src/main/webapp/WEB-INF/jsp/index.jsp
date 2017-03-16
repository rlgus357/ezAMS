
<%
	String serverUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + "/ezAMS/";
%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/include/declare.jspf"%>
<%@ page
	import="org.springframework.web.context.request.RequestAttributes"%>
<%@ page
	import="org.springframework.web.context.request.RequestContextHolder"%>
<% String appPath = request.getContextPath(); %>


<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8">
<title>ezAMS</title>
<script src="<%=appPath%>/js/jquery-1.11.1.min.js" type="text/javascript"></script>
<script src="<%=appPath%>/js/jquery-ui.js" type="text/javascript"></script>
<script src="<%=appPath%>/js/i18n/grid.locale-kr.js" type="text/javascript"></script>

<link rel="stylesheet" type="text/css" media="screen" href="<%=appPath%>/css/jquery-ui.css"></link>
<link rel="stylesheet" type="text/css" media="screen" href="<%=appPath%>/css/ui.jqgrid.css"></link>
</head>
<script>
   $(document).ready(function(){
	   
      $('input:text, input:pssword')
         .button()
         .css({
           'font' : 'inherit',
           'color' : 'inherit',
           'text-align' : 'left',
           'outline' : 'none',
           'cursor' : 'text',
           'height' : '20px'
        });
     $("#logonBtn").button();
    
     var message = "<c:out value='${message}'/>";

     if(message != ""){
        alert(message);
    }
});
  function fn_Login(){
    $("#loginForm").attr('method','post');
    $("#loginForm").attr('action',"<c:url value='/login.do'/>");
   $("#logonForm").submit();
}

  function fn_popUp(){
    window.open("http://12.54.13.123:8080/mantis/login_page.php");
   }
  
</script>
<style>
body {
	font: 62.5% "Trebuchet MS", sans-serif;
	margin: 50px;
}

.demoHeaders {
	margin-top: 2em;
}

#dialog-link {
	padding: 4em 1em .4em 20px;
	text-decoration: none;
	position: relative;
}

#dialog-link span.ui-icon {
	margin: 0 5px 0 0;
	position: absolute;
	left: .2em;
	top: 50%;
	margin-top: -8px;
}

#icons {
	margin: 0;
	padding: 0;
}

#icons li {
	margin: 2px;
	position: relative;
	padding: 4px 0;
	cursor: pointer;
	float: left;
	list-style: none;
}

#icons span.ui-icon {
	float: left;
	margin: 0 4px;
}

.fakewindowcontain .ui-widget-overlay {
	position: absolute;
}
</style>
</body>
<form role="form" id="loginForm" method="post"
	action="<c:url value='/login.do' />">
	<div>
		<table border="0" width="100%" height="100%">
			<tr>
				<td align=center><input type="text" id="UserId" name='UserId'n
					placeholder="Username" value="admin" /> <input type="password"
					id="UserPw" name='UserPw' placeholder="Password" value="admin"
					onKeyDown="javascript:if(event.keyCode == 13){ fn_login();}" />
					<button id="loginBtn" type="submit">Login</button></td>
			</tr>
		</table>
	</div>

	<table width="100%" border="0">
		<tr>
			<td><a href="" onclick="fn_popUp();">
					<h2>
						<b>이슈 트레커입니다.</b>
					</h2>
			</a></td>
		</tr>
		<tr>
			<td><h2>id : user01 pw:user01</h2></td>
		</tr>
	</table>
</form>
</body>
</html>