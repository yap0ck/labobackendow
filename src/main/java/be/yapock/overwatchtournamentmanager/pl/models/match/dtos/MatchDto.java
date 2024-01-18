package be.yapock.overwatchtournamentmanager.pl.models.match.dtos;

import be.yapock.overwatchtournamentmanager.dal.models.Match;
import be.yapock.overwatchtournamentmanager.dal.models.Team;
import be.yapock.overwatchtournamentmanager.pl.models.team.forms.TeamForm;

public record MatchDto(
        long matchNumber,
        long team1Id,
        long teamId,
        int scoreTeam1,
        int scoreTeam2) {
    public static MatchDto fromEntity(Match match){
        return new MatchDto(match.getMatchNumber(),match.getTeam1().getId(), match.getTeam2().getId(), match.getScoreTeam1(), match.getScoreTeam2());
    }
}
