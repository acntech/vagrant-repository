$(document).ready(() => {
    $("#hosted").change(() => {
        if ($("#externalUrlWrapper").hasClass("d-none")) {
            $("#externalUrlWrapper").removeClass("d-none");
        } else {
            $("#externalUrlWrapper").addClass("d-none");
        }
    });
});
