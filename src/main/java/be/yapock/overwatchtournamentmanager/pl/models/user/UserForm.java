package be.yapock.overwatchtournamentmanager.pl.models.user;

import be.yapock.overwatchtournamentmanager.dal.models.enums.InGameRole;
import be.yapock.overwatchtournamentmanager.dal.models.enums.UserRole;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDate;
import java.util.List;

public record UserForm(

        String username,
        String email,
        String battleNet,
        String password,
        LocalDate dateOfBirth,
        char gender,
        String ranking,
        List<UserRole> userRoles,
        List<InGameRole> inGameRoles
) {
}
