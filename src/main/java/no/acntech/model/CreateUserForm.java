package no.acntech.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import no.acntech.validation.UniqueUsername;
import no.acntech.validation.ValuesMatch;

@ValuesMatch.List({
        @ValuesMatch(
                field = "newPassword",
                fieldMatch = "confirmPassword",
                message = "{acntech.validation.constraints.PasswordsMatch.message}"
        )
})
public class CreateUserForm {

    @UniqueUsername
    @Size(min = 2, max = 50, message = "{acntech.validation.constraints.UsernameSize.message}")
    private String username;
    @Size(min = 6, max = 50, message = "{acntech.validation.constraints.PasswordSize.message}")
    private String newPassword;
    @Size(min = 6, max = 50, message = "{acntech.validation.constraints.PasswordSize.message}")
    private String confirmPassword;
    @NotNull
    private UserRole role;

    public CreateUserForm() {
    }

    public CreateUserForm(String username, String newPassword, String confirmPassword, UserRole role) {
        this.username = username;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
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
