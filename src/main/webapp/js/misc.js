function isNoU(obj) {
    return obj === null || obj === undefined;
}

function nl2br(str) {
    if (isNoU(str)) {
        return "";
    }
    return str.replace(/([^>\r\n]?)(\r\n|\n\r|\r|\n)/g, '$1<br />$2');
}

function br2nl(str) {
    if (isNoU(str)) {
        return "";
    }
    return str.replace(/<br\s*\/?>/mg, "\n");
}
$(document).ready(function () {
    $.fn.serializeJSON = function () {
        var json = {};
        jQuery.map($(this).serializeArray(), function (n) {
            if (!isNoU(json[n['name']])) {
                var arr = [];
                if (json[n['name']] instanceof Array) {
                    for (var i = 0; i < json[n['name']].length; i++) {
                        arr.push(json[n['name']][i]);
                    }
                } else {
                    arr.push(json[n['name']]);
                }

                arr.push(n['value']);
                json[n['name']] = arr;
            } else {
                json[n['name']] = n['value'];
            }
        });
        return json;
    };

    $.ajaxSetup({
        type: "POST",
        error: function (xhr, textStatus) {
            console.log(xhr, textStatus);
        }
    });
});