package be.yapock.overwatchtournamentmanager.dal.models.compositeKey;

import be.yapock.overwatchtournamentmanager.dal.models.Team;
import be.yapock.overwatchtournamentmanager.dal.models.Tournament;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class TournamentTeamCompositeKey implements Serializable {
    private long teamId;
    private long tournamentId;

    public TournamentTeamCompositeKey(){}

    public TournamentTeamCompositeKey(Team team, Tournament tournament){
        this.teamId = team.getId();
        this.tournamentId = tournament.getId();
    }
}
