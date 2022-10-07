$(document).ready(() => {
    $("#hosted").change(() => {
        if ($("#hosted").is(':checked')) {
            $("#externalUrlWrapper").addClass("d-none");
        } else {
            $("#externalUrlWrapper").removeClass("d-none");
        }
    });

    $("#copy-vagrant-file").click(() => {
        const text = $("#vagrant-file-code pre code").first().text();
        navigator.clipboard.writeText(text)
                .then(() => {
                    console.log("YEAY")
                    $(".copy-success").fadeIn().fadeOut(1000);
                })
                .catch(() => {
                    console.log("NOOO")
                    $(".copy-failed").fadeIn().fadeOut(1000);
                });
    });
});

const closeModalAndSubmitForm = (modal, form) => {
    $(modal).modal('hide');
    $(form).submit();
};

const submitForm = (form) => {
    $(form).submit();
};
