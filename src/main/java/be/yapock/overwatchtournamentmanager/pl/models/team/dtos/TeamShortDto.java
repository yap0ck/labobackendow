package be.yapock.overwatchtournamentmanager.pl.models.team.dtos;

import be.yapock.overwatchtournamentmanager.dal.models.User;

public record TeamShortDto(
        long id,
        User captain,
        String name,
        int elo
) {
}
