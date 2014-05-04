function exec(event, form) {
    if (event !== null && event !== undefined) {
        event.preventDefault();
    }
    $("#consoleOutput").html("Loading...");
    $.ajax({
        "url": "/exec",
        "data": $(form).serializeJSON(),
        "success": function (data) {
            $("#consoleOutput").html(nl2br(data));
        },
        "error": function(xhr, textStatus){
            console.log(xhr, textStatus);
            $("#consoleOutput").html("There was an error. Check console and logs for more info");
        }
    });
}

