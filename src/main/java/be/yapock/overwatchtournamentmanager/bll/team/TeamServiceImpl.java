package be.yapock.overwatchtournamentmanager.bll.team;

import be.yapock.overwatchtournamentmanager.bll.user.UserService;
import be.yapock.overwatchtournamentmanager.dal.models.Team;
import be.yapock.overwatchtournamentmanager.dal.models.User;
import be.yapock.overwatchtournamentmanager.dal.repositories.TeamRepository;
import be.yapock.overwatchtournamentmanager.dal.repositories.UserRepository;
import be.yapock.overwatchtournamentmanager.pl.models.team.forms.TeamForm;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    public TeamServiceImpl(UserService userService, UserRepository userRepository, TeamRepository teamRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
    }

    /**
     * crée une équipe . la date de création et l'elo est défini automatiquement. le créateur de l'équipe devient de facto le capitaine
     * @param form
     */
    @Override
    public void create(TeamForm form, Authentication authentication) {
        if (form==null) throw new IllegalArgumentException("form ne peut etre vide");
        User userConnected = userRepository.findByUsername(authentication.getName()).orElseThrow(()->new UsernameNotFoundException("Utilisateur non trouvé"));
        if (teamRepository.existsByPlayerListContaining(userConnected)) throw new IllegalArgumentException("l'utilisateur ne peut pas creer d'équipe si il fait partie d'une équipe");
        Team team = Team.builder()
                .creationDate(LocalDate.now())
                .teamElo(1200)
                .captain(userConnected)
                .teamName(form.teamName())
                .playerList(form.playerListId().stream()
                        .map(userService::getOne)
                        .toList())
                .build();
        teamRepository.save(team);
    }

    /**
     * recherche une équipe par sont id
     * @throws EntityNotFoundException si il n'y a pas d'équipe portant l'id
     * @param id de l'équipe recherchée
     * @return Team
     */
    @Override
    public Team getOne(long id) {
        return teamRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("équipe pas trouvée"));
    }

    /**
     * affiche toute les équipes sous forme de Page
     * @param pageable parametre de pagination
     * @return page d'équipe
     */
    @Override
    public Page<Team> getAll(Pageable pageable) {
        return teamRepository.findAll(pageable);
    }
}
