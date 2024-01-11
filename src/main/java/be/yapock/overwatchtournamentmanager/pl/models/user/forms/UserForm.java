package be.yapock.overwatchtournamentmanager.pl.models.user.forms;

import be.yapock.overwatchtournamentmanager.dal.models.enums.InGameRole;
import be.yapock.overwatchtournamentmanager.dal.models.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record UserForm(
        @NotNull @NotBlank
        String username,
        @NotNull @NotBlank
        String email,
        @NotNull @NotBlank
        String battleNet,
        @NotNull @NotBlank
        String password,
        @NotNull @NotBlank
        String confirmedPassword,
        @NotNull @NotBlank
        LocalDate dateOfBirth,
        @NotNull @NotBlank
        char gender,
        String ranking,
        List<InGameRole> inGameRoles
) {
}
