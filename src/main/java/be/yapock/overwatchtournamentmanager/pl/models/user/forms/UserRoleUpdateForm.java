package be.yapock.overwatchtournamentmanager.pl.models.user.forms;

import be.yapock.overwatchtournamentmanager.dal.models.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UserRoleUpdateForm(
        @NotNull @NotBlank
        List<UserRole> roles
) {
}
