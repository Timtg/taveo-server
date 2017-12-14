function qs(key) {
    key = key.replace(/[*+?^$.\[\]{}()|\\\/]/g, "\\$&"); // escape RegEx meta chars
    var match = location.search.match(new RegExp("[?&]"+key+"=([^&]+)(&|$)"));
    return match && decodeURIComponent(match[1].replace(/\+/g, " "));
}

function confirmEmailFromQueryParam(){
    var id = qs('id');
    var email = qs('email');
    var otp = qs('otp');
    if(id && email && otp) {
        $("#failedVerification").hide();
        $("#verified").hide();
        $("#loading").show();

        var basePath = $(location).attr('protocol') + "//" + $(location).attr('host');
        var url = basePath + "/api/v1/user/email/verification";

        $.ajax({
            url : url,
            type: "POST",
            data: JSON.stringify({id:id,verificationIdentifier:email,otp:otp}),
            contentType: "application/json; charset=utf-8",
            dataType   : "json",
            success    : function(data){
                console.log("success");
                $("#loading").hide();
                if(data.content){
                    $("#validQueryParam").hide();
                    $("#inValidQueryParam").hide();

                    $("#failedVerification").hide();
                    $("#verified").show();
                } else {
                    $("#failedVerification").show();
                    $("#verified").hide();
                }
            },error: function(jqXHR, textStatus, errorThrown) {
                $("#loading").hide();
                $("#failedVerification").show();
                $("#verified").hide();
                if(jqXHR && jqXHR.responseJSON && jqXHR.responseJSON.msg){
                    $("#serverInfo").text('Cause: ' +jqXHR.responseJSON.msg);
                }
            }
        });
    } else {
        $("#validQueryParam").hide();
        $("#status").hide();
        $("#inValidQueryParam").show();
    }
}


$( document ).ready(function() {
    $( document ).ajaxError(function() {});

    confirmEmailFromQueryParam();

    $("#startConfirmation").on("click", function(e) {
        confirmEmailFromQueryParam();
    });
});
