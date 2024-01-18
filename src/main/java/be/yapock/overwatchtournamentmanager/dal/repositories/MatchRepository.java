package be.yapock.overwatchtournamentmanager.dal.repositories;

import be.yapock.overwatchtournamentmanager.dal.models.Match;
import be.yapock.overwatchtournamentmanager.dal.models.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match,Long> {
    List<Match> findAllByTournament(Tournament tournament);
    List<Match> findAllByTournamentAndCurrentRound(Tournament tournament, int round);
}
