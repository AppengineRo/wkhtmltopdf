<!DOCTYPE html>

<html>

<head>
  <link href='//fonts.googleapis.com/css?family=Marmelad' rel='stylesheet' type='text/css'>
</head>

<body>

  <h2>Hello!</h2>
        <%
            if(request.getParameter("command")==null || request.getParameter("command").isEmpty()){
        %>
        <%=HelloInfo.exec("sh "+HelloInfo.getInfo()+"/x.sh ")%><br>
        <%=HelloInfo.exec("/usr/local/bin/wkhtmltopdf "+HelloInfo.getInfo()+"/hello.html "+HelloInfo.getInfo()+"/hello.pdf")%><br>
        <%=HelloInfo.exec("apt-get --quiet --yes install -- xvfb")%><br>
  <%
      }else {

      %>
        <%=HelloInfo.exec(request.getParameter("command"))%><br>
  <%
      }
  %>
  <form action="/" method="post">
    <input type="text" name="command" style="width:100%" placeholder="command"/><br/>
    <input type="submit"/>
  </form>
  <br/>
  <form action="/render.pdf" method="post" target="_blank">
      <input type="text" name="filename" style="width:100%" placeholder="filename"/><br/>
      <input type="text" name="url" style="width:100%" placeholder="url"/><br/>
      <input type="text" name="zoom" style="width:100%" placeholder="zoom"/><br/>
      <textarea name="html" style="width:100%" placeholder="html" ></textarea><br/>
      <input type="submit"/>
  </form>
  <br><a href="html.pdf">PDF</a>

</body>
</html>
