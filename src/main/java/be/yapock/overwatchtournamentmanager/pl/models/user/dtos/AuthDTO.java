package be.yapock.overwatchtournamentmanager.pl.models.user.dtos;

import be.yapock.overwatchtournamentmanager.dal.models.User;
import be.yapock.overwatchtournamentmanager.dal.models.enums.UserRole;

import java.util.List;

public record AuthDTO(
        String token,
        String username,
        List<UserRole> userRoles
) {
}
