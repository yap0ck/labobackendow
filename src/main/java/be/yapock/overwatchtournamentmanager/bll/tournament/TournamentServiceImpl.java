package be.yapock.overwatchtournamentmanager.bll.tournament;

import be.yapock.overwatchtournamentmanager.bll.mailing.EmailService;
import be.yapock.overwatchtournamentmanager.dal.models.Team;
import be.yapock.overwatchtournamentmanager.dal.models.Tournament;
import be.yapock.overwatchtournamentmanager.dal.models.User;
import be.yapock.overwatchtournamentmanager.dal.models.enums.TournamentStatus;
import be.yapock.overwatchtournamentmanager.dal.models.jointable.TournamentTeams;
import be.yapock.overwatchtournamentmanager.dal.repositories.TeamRepository;
import be.yapock.overwatchtournamentmanager.dal.repositories.TournamentRepository;
import be.yapock.overwatchtournamentmanager.dal.repositories.TournamentTeamRepository;
import be.yapock.overwatchtournamentmanager.dal.repositories.UserRepository;
import be.yapock.overwatchtournamentmanager.pl.models.team.forms.TeamSearchForm;
import be.yapock.overwatchtournamentmanager.pl.models.tournament.forms.TournamentForm;
import be.yapock.overwatchtournamentmanager.pl.models.tournament.forms.TournamentSearchForm;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class TournamentServiceImpl implements TournamentService{
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final TournamentTeamRepository tournamentTeamRepository;

    public TournamentServiceImpl(TournamentRepository tournamentRepository, UserRepository userRepository, TeamRepository teamRepository, EmailService emailService, UserRepository userRepository1, TournamentTeamRepository tournamentTeamRepository) {
        this.tournamentRepository = tournamentRepository;
        this.teamRepository = teamRepository;
        this.emailService = emailService;
        this.userRepository = userRepository1;
        this.tournamentTeamRepository = tournamentTeamRepository;
    }

    /**
     * methode de création de tournoi, le statut est défini par défaut sur Registration,  la date est générée au moment de la création et le nombre de tour est défini sur 0
     * @param form formulaire de création de tournoi
     */
    //TODO incrementation d'un indice de tournoi automatique
    @Override
    public void create(TournamentForm form) {
        if (form==null) throw new IllegalArgumentException("le formulaire ne peut etre vide");
        Tournament tournament = Tournament.builder()
                .name(form.name())
                .minTeam(form.minTeam())
                .maxTeam(form.maxTeam())
                .minElo(form.minElo())
                .maxElo(form.maxElo())
                .categories(form.categories())
                .status(TournamentStatus.REGISTRATION)
                .round(0)
                .isWomenOnly(form.isWomenOnly())
                .startingDateTime(form.startingDateTime())
                .creationDate(LocalDate.now())
                .updateDate(LocalDate.now())
                .build();
        teamRepository.findAllByAllWomenAndTeamEloBetween(tournament.isWomenOnly(), tournament.getMinElo(), tournament.getMaxElo()).stream().forEach(e-> {
            try {
                emailService.sendInvititionalMail(e, tournament);
            } catch (MessagingException ex) {
                throw new RuntimeException(ex);
            }
        });
        tournamentRepository.save(tournament);
    }

    /**
     * methode de suppression de tournoi
     * @param id du tournoi supprimer
     */
    @Override
    public void delete(long id) {
        tournamentRepository.deleteById(id);
    }

    /**
     * renvoye les 10 derniers tournois trier par leurs date de mise a jour par ordre décroissant
     * @return liste de tournois
     */
    @Override
    public List<Tournament> getAll() {
        return tournamentRepository.findFirst10ByStatusOrderByUpdateDateDesc(TournamentStatus.REGISTRATION).stream()
                .peek(e -> e.setRegistrationNbr(tournamentTeamRepository.countAllByTournament(e)))
                .toList();
    }

    /**
     * recherche toute les équipes selon les critéres définis dans le form
     * @param form formulaire avec les champs suivant: nom du tournoi, le status, les catégories, si l'équipe peut s'enregistré et si elle est enregistrée
     * @param pageable parametres d'affichage des tournois
     * @param authentication utilisateur connecté
     * @return
     */
    @Override
    public Page<Tournament> getAllBySpec(TournamentSearchForm form, Pageable pageable, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow(()-> new UsernameNotFoundException("utilisateur pas trouvé"));
        Team team = teamRepository.findByCaptain(user).orElseThrow(()-> new EntityNotFoundException("équipe pas trouvée"));
        Specification<Tournament> spec = createSpec(form,team);
        return tournamentRepository.findAll(spec, pageable);
    }

    @Override
    public Tournament getOne(long id){
        return null;
    }

    @Override
    public void register(long id, Authentication authentication) {
        User userConnected = userRepository.findByUsername(authentication.getName()).orElseThrow(()->new UsernameNotFoundException("utilisateur pas trouvé"));
        Team team = teamRepository.findByCaptain(userConnected).orElseThrow(()->new EntityNotFoundException("équipe pas trouvée"));
        TournamentTeams registration = TournamentTeams.builder()
                .registrationDate(LocalDate.now())
                .tournament(getOne(id))
                .build();
    }

    private Specification<Tournament> createSpec(TournamentSearchForm form, Team team){
        return (((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (form.name()!=null) predicates.add((criteriaBuilder.like(root.get("name"), "%" + form.name() + "%")));
            if (form.status()!=null) predicates.add((criteriaBuilder.equal(root.get("status"), form.status())));
            if (form.categories()!= null && !form.categories().isEmpty()) predicates.add((criteriaBuilder.equal(root.get("categories"), form.categories())));
            if (form.canRegister()) {
                predicates.add((criteriaBuilder.lessThan(root.get("max_elo"), team.getTeamElo())));
                predicates.add((criteriaBuilder.greaterThan(root.get("min_elo"), team.getTeamElo())));
                if (team.isAllWomen()) predicates.add((criteriaBuilder.isTrue(root.get("is_women_only"))));
            }
            if (form.isRegistered()){
                Join<Tournament, Team> join = root.join("tournament_team", JoinType.INNER);
                predicates.add((criteriaBuilder.equal(join.get("team").get("team_id"), team.getId())));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }));
    }
}


