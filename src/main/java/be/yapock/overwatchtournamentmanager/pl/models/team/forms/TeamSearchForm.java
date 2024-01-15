package be.yapock.overwatchtournamentmanager.pl.models.team.forms;

public record TeamSearchForm(
        String name,
        Long captainId,
        Long playerId
) {
}
