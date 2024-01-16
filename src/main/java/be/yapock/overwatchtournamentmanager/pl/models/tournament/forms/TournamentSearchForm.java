package be.yapock.overwatchtournamentmanager.pl.models.tournament.forms;

import be.yapock.overwatchtournamentmanager.dal.models.enums.TournamentCategories;
import be.yapock.overwatchtournamentmanager.dal.models.enums.TournamentStatus;

import java.util.List;

public record TournamentSearchForm(
        String name,
        TournamentStatus status,
        List<TournamentCategories> categories,
        Boolean canRegister,
        Boolean isRegistered
) {
}
