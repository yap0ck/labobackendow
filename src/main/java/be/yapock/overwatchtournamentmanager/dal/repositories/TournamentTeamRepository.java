package be.yapock.overwatchtournamentmanager.dal.repositories;

import be.yapock.overwatchtournamentmanager.dal.models.Tournament;
import be.yapock.overwatchtournamentmanager.dal.models.compositeKey.TournamentTeamCompositeKey;
import be.yapock.overwatchtournamentmanager.dal.models.jointable.TournamentTeams;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentTeamRepository extends JpaRepository<TournamentTeams, TournamentTeamCompositeKey> {
    int countAllByTournament(Tournament tournament);
}
