package be.yapock.overwatchtournamentmanager.dal.repositories;

import be.yapock.overwatchtournamentmanager.dal.models.Team;
import be.yapock.overwatchtournamentmanager.dal.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long>, JpaSpecificationExecutor<Team> {
    boolean existsByPlayerListContaining(User user);
    @Query("SELECT t FROM Team t WHERE t.isAllWomen = :allWomen AND t.teamElo BETWEEN :min AND :max")
    List<Team> findAllByAllWomenAndTeamEloBetween(@Param("allWomen") boolean allWomen, @Param("min") int minElo, @Param("max") int maxElo);
    Optional<Team> findByCaptain(User user);
}
