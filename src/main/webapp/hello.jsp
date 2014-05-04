<!DOCTYPE html>
<html>
<head>
    <link href='//fonts.googleapis.com/css?family=Marmelad' rel='stylesheet' type='text/css'>
    <script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
    <script src="/js/misc.js"></script>
    <script src="/js/app.js"></script>
</head>

<body>
<div id="consoleOutput" style="display:inline-block; width:100%; height:300px;">
</div>

<form action="/" method="post" onsubmit="exec(event, this)">
    <input type="text" name="command" style="width:100%" placeholder="command"/><br/>
    <input type="submit"/>
</form>
<br/>

<form action="/render.pdf" method="post" target="_blank">
    <input type="text" name="filename" style="width:100%" placeholder="filename"/><br/>
    <input type="text" name="url" style="width:100%" placeholder="url"/><br/>
    <input type="text" name="zoom" style="width:100%" placeholder="zoom"/><br/>
    <textarea name="html" style="width:100%" placeholder="html"></textarea><br/>
    <input type="submit"/>
</form>
</body>
</html>
