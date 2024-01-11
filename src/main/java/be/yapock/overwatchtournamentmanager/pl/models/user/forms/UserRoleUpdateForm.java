package be.yapock.overwatchtournamentmanager.pl.models.user.forms;

import be.yapock.overwatchtournamentmanager.dal.models.enums.UserRole;

import java.util.List;

public record UserRoleUpdateForm(
        List<UserRole> roles
) {
}
