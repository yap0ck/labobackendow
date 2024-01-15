package be.yapock.overwatchtournamentmanager.pl.models.tournament.forms;

import be.yapock.overwatchtournamentmanager.dal.models.enums.TournamentCategories;
import be.yapock.overwatchtournamentmanager.dal.models.enums.TournamentStatus;
import be.yapock.overwatchtournamentmanager.pl.models.validation.constraints.ValidTournament;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;
import java.util.List;

@ValidTournament
public record TournamentForm(
        String name,
        @Min(2) @Max(32)
        int minTeam,
        @Min(2) @Max(32)
        int maxTeam,
        @Min(0) @Max(3000)
        int minElo,
        @Min(0) @Max(3000)
        int maxElo,
        List<TournamentCategories> categories,
        boolean isWomenOnly,
        LocalDateTime startingDateTime
) {
}
