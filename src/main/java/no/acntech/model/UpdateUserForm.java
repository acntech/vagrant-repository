package no.acntech.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import no.acntech.validation.ValuesMatch;

@ValuesMatch.List({
        @ValuesMatch(
                field = "newPassword",
                fieldMatch = "confirmPassword",
                message = "{acntech.validation.constraints.PasswordsMatch.message}"
        )
})
public class UpdateUserForm {

    @Size(min = 2, max = 50, message = "{acntech.validation.constraints.UsernameSize.message}")
    private String username;
    private String newPassword;
    private String confirmPassword;
    @NotNull
    private UserRole role;

    public UpdateUserForm() {
    }

    public UpdateUserForm(String username, UserRole role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
