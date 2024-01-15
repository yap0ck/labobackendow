package be.yapock.overwatchtournamentmanager.dal.repositories;

import be.yapock.overwatchtournamentmanager.dal.models.Team;
import be.yapock.overwatchtournamentmanager.dal.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long>, JpaSpecificationExecutor<Team> {
    boolean existsByPlayerListContaining(User user);
    List<Team> findAllByAllWomenAndTeamEloBetween(boolean iswomen, int eloMin, int eloMax);
}
