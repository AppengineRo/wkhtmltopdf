<!DOCTYPE html>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="com.google.appengine.api.users.UserService" %>
<%@page import="com.google.appengine.api.users.UserServiceFactory" %>
<!--
  ~ Copyright (c) 2013 Asociatia AppEngine . All Rights Reserved.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License"); you
  ~ may not use this file except in compliance with the License. You may
  ~ obtain a copy of the License at
  ~
  ~      http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
  ~ implied. See the License for the specific language governing
  ~ permissions and limitations under the License.
  -->

<html>
<head lang="en">
    <meta charset="UTF-8">
    <title>Admin</title>

    <script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
    <script src="/js/misc.js"></script>
    <script src="/js/app.js"></script>
</head>
<body>
<%
    UserService userService = UserServiceFactory.getUserService();
    if (userService.getCurrentUser() != null && userService.isUserAdmin()) {
%>      <a href="<%=userService.createLogoutURL("/")%>">Logged in as <%=userService.getCurrentUser().getEmail()%> Logout</a>
        <div id="consoleOutput" style="display:inline-block; width:100%; height:300px;"></div>

        <form action="/" method="post" onsubmit="exec(event, this)">
            <input type="text" name="command" style="width:100%" placeholder="command"/><br/>
            <input type="submit"/>
        </form>
<% } else { %>
    This interface requires login and the admin role. To login please visit: <a href="<%=userService.createLoginURL("/admin.jsp")%>"> THIS URL </a>
<% }%>
</body>
</html>