package no.acntech.model;

import javax.validation.constraints.Size;

import no.acntech.validation.CorrectPassword;
import no.acntech.validation.ValuesMatch;

@ValuesMatch.List({
        @ValuesMatch(
                field = "newPassword",
                fieldMatch = "confirmPassword",
                message = "{acntech.validation.constraints.PasswordsMatch.message}"
        )
})
public class PasswordForm {

    @CorrectPassword
    @Size(min = 6, max = 50, message = "{acntech.validation.constraints.PasswordSize.message}")
    private String currentPassword;
    @Size(min = 6, max = 50, message = "{acntech.validation.constraints.PasswordSize.message}")
    private String newPassword;
    @Size(min = 6, max = 50, message = "{acntech.validation.constraints.PasswordSize.message}")
    private String confirmPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
