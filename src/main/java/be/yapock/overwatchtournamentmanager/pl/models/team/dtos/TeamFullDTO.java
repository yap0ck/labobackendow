package be.yapock.overwatchtournamentmanager.pl.models.team.dtos;

import be.yapock.overwatchtournamentmanager.dal.models.Team;
import be.yapock.overwatchtournamentmanager.dal.models.User;

import java.time.LocalDate;
import java.util.List;

public record TeamFullDTO(
        LocalDate creationDate,
        int teamElo,
        long id,
        String teamName,
        User captain,
        List<User> playerList) {
    public static TeamFullDTO fromEntity(Team team){
        return new TeamFullDTO(team.getCreationDate(), team.getTeamElo(), team.getId(), team.getTeamName(), team.getCaptain(),team.getPlayerList());
    }
}
