package be.yapock.overwatchtournamentmanager.pl.models.user.forms;

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
        String confirmedPassword,
        LocalDate dateOfBirth,
        char gender,
        String ranking,
        List<InGameRole> inGameRoles
) {
}
