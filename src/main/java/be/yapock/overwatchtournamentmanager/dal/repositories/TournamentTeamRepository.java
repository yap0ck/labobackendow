package be.yapock.overwatchtournamentmanager.dal.repositories;

import be.yapock.overwatchtournamentmanager.dal.models.Team;
import be.yapock.overwatchtournamentmanager.dal.models.Tournament;
import be.yapock.overwatchtournamentmanager.dal.models.compositeKey.TournamentTeamCompositeKey;
import be.yapock.overwatchtournamentmanager.dal.models.jointable.TournamentTeams;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TournamentTeamRepository extends JpaRepository<TournamentTeams, TournamentTeamCompositeKey> {
    int countByTournament(Tournament tournament);
    List<TournamentTeams> findAllByTournament(Tournament tournament);
    void deleteByTeamAndTournament(Team team, Tournament tournament);
    boolean existsByTeamAndTournament(Team team, Tournament tournament);
}
