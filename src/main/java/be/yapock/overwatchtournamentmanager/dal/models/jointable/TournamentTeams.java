package be.yapock.overwatchtournamentmanager.dal.models.jointable;

import be.yapock.overwatchtournamentmanager.dal.models.Team;
import be.yapock.overwatchtournamentmanager.dal.models.Tournament;
import be.yapock.overwatchtournamentmanager.dal.models.compositeKey.TournamentTeamCompositeKey;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Builder
@Data
@NoArgsConstructor @AllArgsConstructor
@Table(name = "tournament_team")
public class TournamentTeams {
    @EmbeddedId
    private TournamentTeamCompositeKey id;

    private LocalDate registrationDate;

    private int matchCount;

    @MapsId("team_id")
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @MapsId("tournament_id")
    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;
}
