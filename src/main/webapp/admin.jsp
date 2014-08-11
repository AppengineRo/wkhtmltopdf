<!DOCTYPE html>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="com.google.appengine.api.users.UserService" %>
<%@page import="com.google.appengine.api.users.UserServiceFactory" %>
<html>
<head lang="en">
    <meta charset="UTF-8">
    <title>Admin</title>
    <script src="/js/jquery/jquery-1.11.1.min.js"></script>
    <script src="/js/admin/misc.js"></script>
    <script src="/js/admin/app.js"></script>
    <style>
        #consoleOutput {
            display: inline-block;
            width: 100%;
            height: 300px;
            overflow-y: auto;
        }
    </style>
</head>
<body>
<%
    UserService userService = UserServiceFactory.getUserService();
    if (userService.getCurrentUser() != null && userService.isUserAdmin()) {
%> <a href="<%=userService.createLogoutURL("/")%>">Logged in as <%=userService.getCurrentUser().getEmail()%> Logout</a>
<pre id="consoleOutput"></pre>

<form action="/" method="post" onsubmit="exec(event, this)">
    <input type="text" name="command" style="width:100%" placeholder="command"/><br/>
    <input type="submit"/>
</form>
<% } else { %>
This interface requires login and the admin role. To login please visit:
<a href="<%=userService.createLoginURL("/admin.jsp")%>"> THIS URL </a>
<% }%>
</body>
</html>