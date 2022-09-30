$(document).ready(() => {
    $("#hosted").change(() => {
        if ($("#hosted").is(':checked')) {
            $("#externalUrlWrapper").addClass("d-none");
        } else {
            $("#externalUrlWrapper").removeClass("d-none");
        }
    });
});

const closeModalAndSubmitForm = (modal, form) => {
    $(modal).modal('hide');
    $(form).submit();
};

const submitForm = (form) => {
    $(form).submit();
};
