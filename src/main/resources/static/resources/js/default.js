$(document).ready(() => {
    $("#hosted").change(() => {
        if ($("#hosted").is(':checked')) {
            $("#externalUrlWrapper").addClass("d-none");
        } else {
            $("#externalUrlWrapper").removeClass("d-none");
        }
    });
});

const submitDeleteItemForm = () => {
    $('#deleteItemModal').modal('hide');
    $("#deleteItemForm").submit();
};
