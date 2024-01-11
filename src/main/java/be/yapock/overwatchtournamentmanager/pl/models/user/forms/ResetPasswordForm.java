package be.yapock.overwatchtournamentmanager.pl.models.user.forms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResetPasswordForm(
        @NotNull @NotBlank
        String password,
        @NotNull @NotBlank
        String confirmedPassword
) {
}
