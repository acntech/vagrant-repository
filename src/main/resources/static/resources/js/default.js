$(document).ready(() => {
    $("#hosted").change(() => {
        if ($("#externalUrlWrapper").hasClass("d-none")) {
            $("#externalUrlWrapper").removeClass("d-none");
        } else {
            $("#externalUrlWrapper").addClass("d-none");
        }
    });
});

const postRequest = (url) => {
    $.ajax({
        type: "POST",
        url: url,
        async: false,
        success: (result) => {
            console.log(result);
        },
        error: function (xhr, statusText, err) {
            alert("error" + xhr.status);
        }
    });
};
