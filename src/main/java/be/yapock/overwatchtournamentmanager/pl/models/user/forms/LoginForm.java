package be.yapock.overwatchtournamentmanager.pl.models.user.forms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginForm(
        @NotNull @NotBlank
        String username,
        @NotNull @NotBlank
        String password
) {
}
