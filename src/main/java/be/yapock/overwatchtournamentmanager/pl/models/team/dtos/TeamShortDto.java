package be.yapock.overwatchtournamentmanager.pl.models.team.dtos;

import be.yapock.overwatchtournamentmanager.dal.models.Team;
import be.yapock.overwatchtournamentmanager.dal.models.User;

public record TeamShortDto(
        long id,
        User captain,
        String name,
        int elo
) {
    public static TeamShortDto fromEntity(Team team){
        return new TeamShortDto(team.getId(), team.getCaptain(), team.getTeamName(), team.getTeamElo());
    }
}
