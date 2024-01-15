package be.yapock.overwatchtournamentmanager.bll.team;

import be.yapock.overwatchtournamentmanager.bll.user.UserService;
import be.yapock.overwatchtournamentmanager.dal.models.Team;
import be.yapock.overwatchtournamentmanager.dal.models.User;
import be.yapock.overwatchtournamentmanager.dal.models.enums.UserRole;
import be.yapock.overwatchtournamentmanager.dal.repositories.TeamRepository;
import be.yapock.overwatchtournamentmanager.dal.repositories.UserRepository;
import be.yapock.overwatchtournamentmanager.pl.models.team.forms.TeamForm;
import be.yapock.overwatchtournamentmanager.pl.models.team.forms.TeamSearchForm;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
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
        List<User> userList = form.playerListId().stream()
                .map(userService::getOne)
                .toList();
        Team team = Team.builder()
                .creationDate(LocalDate.now())
                .teamElo(1200)
                .captain(userConnected)
                .teamName(form.teamName())
                .playerList(userList)
                .isAllWomen(userList.stream().noneMatch(e -> e.getGender() == 'M' || e.getGender() == 'O'))
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

    /**
     * met a jour les jour d'une équipe
     * @param form formulaire de mise a jour
     * @param id de l'équipe a mettre a jour
     * @param authentication afin de vérifier que l'utilisateur connecté est bien soit capitaine soit admin
     */
    @Override
    public void update(TeamForm form, long id, Authentication authentication) {
        if (form == null) throw new IllegalArgumentException("le formulaire ne peut etre null");
        Team teamToUpdate = getOne(id);
        User userConnected = userRepository.findByUsername(authentication.getName()).orElseThrow(()-> new UsernameNotFoundException("utilisateur pas trouvé"));
        //TODO ajouter la possibilité a l'admin de changer le nom de l'équipe si contraire a LA NETIQUETTE
        if (!userConnected.equals(teamToUpdate.getCaptain()) && !userConnected.getUserRoles().contains(UserRole.ADMIN)) throw  new BadCredentialsException("l'utilisateur connecté ne peut modifier cette équipe");
        List<User> userList = form.playerListId().stream()
                .map(userService::getOne)
                .toList();
        teamToUpdate.setCaptain(userService.getOne(form.Captainid()));
        teamToUpdate.setPlayerList(userList);
        teamToUpdate.setAllWomen(userList.stream().noneMatch(e -> e.getGender() == 'M'|| e.getGender() == 'O'));
        teamRepository.save(teamToUpdate);
    }

    /**
     * supprime une équipe
     * @param id de l'équipe a supprimé
     * @param authentication afin de vérifier si l'utilisateur connecté a les droit de supprimé cet équipe
     */
    //TODO retirer la suppression dure et ajouté un booléen "isDeleted"
    @Override
    public void delete(long id, Authentication authentication) {
        Team teamDelete = getOne(id);
        User userConnected = userRepository.findByUsername(authentication.getName()).orElseThrow(()-> new UsernameNotFoundException("utilisateur pas trouvé"));
        if (!userConnected.equals(teamDelete.getCaptain()) && !userConnected.getUserRoles().contains(UserRole.ADMIN)) throw  new BadCredentialsException("l'utilisateur connecté ne peut modifier cette équipe");
        teamRepository.delete(teamDelete);
    }

    /**
     * effectue une recherche par spécification
     * @param form formulaire de recherche
     * @param pageable parametres de pagination
     * @return une page contenant les équipes recherchée depuis le formulaire
     */
    @Override
    public Page<Team> getAllBySpec(TeamSearchForm form, Pageable pageable) {
        Specification<Team> spec = createSpecification(form);
        return teamRepository.findAll(spec, pageable );
    }

    /**
     * mise en place des spécifications pour la recherche par spec, les champs sont nom, capitaine id et joueur id
     * @param form formulaire de recherche
     * @return lis de spécifications
     */
    private Specification<Team> createSpecification(TeamSearchForm form){
        return (((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (form.name()!= null) predicates.add(criteriaBuilder.like(root.get("team_name"), "%" + form.name() + "%"));
            if (form.captainId()!= null) predicates.add(criteriaBuilder.equal(root.get("captain_id"), form.captainId()));
            if (form.playerId()!=null) {
                Join<Team, User> userJoin = root.join("playerList");
                predicates.add(criteriaBuilder.equal(userJoin.get("user_id"),form.playerId()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }));
    }


}
