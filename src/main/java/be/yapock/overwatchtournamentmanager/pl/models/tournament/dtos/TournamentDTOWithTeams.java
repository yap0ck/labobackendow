package be.yapock.overwatchtournamentmanager.pl.models.tournament.dtos;

import be.yapock.overwatchtournamentmanager.dal.models.Team;
import be.yapock.overwatchtournamentmanager.dal.models.Tournament;
import be.yapock.overwatchtournamentmanager.dal.models.enums.TournamentCategories;
import be.yapock.overwatchtournamentmanager.dal.models.enums.TournamentStatus;

import java.time.LocalDateTime;
import java.util.List;

public record TournamentDTOWithTeams(
        long id,
        String name,
        int inscriptionNumber,
        int minTeam,
        int maxTeam,
        List<TournamentCategories> categories,
        int minElo,
        int maxElo,
        TournamentStatus status,
        LocalDateTime endingRegistration,
        int round,
        List<Long> teamsId
) {
    public static TournamentDTOWithTeams fromEntity(Tournament tournament, List<Long> teamsId){
        return new TournamentDTOWithTeams(tournament.getId(),
                tournament.getName(),
                tournament.getRegistrationNbr(),
                tournament.getMinTeam(),
                tournament.getMaxTeam(),
                tournament.getCategories(),
                tournament.getMinElo(),
                tournament.getMaxElo(),
                tournament.getStatus(),
                tournament.getStartingDateTime().minusHours(1),
                tournament.getRound(),
                teamsId
        );
    }
}
