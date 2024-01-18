package be.yapock.overwatchtournamentmanager.bll.tournament;

import be.yapock.overwatchtournamentmanager.bll.mailing.EmailService;
import be.yapock.overwatchtournamentmanager.dal.models.Match;
import be.yapock.overwatchtournamentmanager.dal.models.Team;
import be.yapock.overwatchtournamentmanager.dal.models.Tournament;
import be.yapock.overwatchtournamentmanager.dal.models.User;
import be.yapock.overwatchtournamentmanager.dal.models.enums.TournamentStatus;
import be.yapock.overwatchtournamentmanager.dal.models.jointable.TournamentTeams;
import be.yapock.overwatchtournamentmanager.dal.repositories.*;
import be.yapock.overwatchtournamentmanager.pl.models.team.forms.TeamSearchForm;
import be.yapock.overwatchtournamentmanager.pl.models.tournament.dtos.TournamentDTOWithTeams;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;



@Service
public class TournamentServiceImpl implements TournamentService{
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final TournamentTeamRepository tournamentTeamRepository;
    private final MatchRepository matchRepository;

    public TournamentServiceImpl(TournamentRepository tournamentRepository, UserRepository userRepository, TeamRepository teamRepository, EmailService emailService, UserRepository userRepository1, TournamentTeamRepository tournamentTeamRepository, MatchRepository matchRepository) {
        this.tournamentRepository = tournamentRepository;
        this.teamRepository = teamRepository;
        this.emailService = emailService;
        this.userRepository = userRepository1;
        this.tournamentTeamRepository = tournamentTeamRepository;
        this.matchRepository = matchRepository;
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

        List<Team> teamList = tournamentTeamRepository.findAllByTournament(tournamentRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("tournoi pas trouvé"))).stream()
                .map(TournamentTeams::getTeam)
                .toList();
        teamList.forEach(e-> {
            try{
                emailService.sendTournamentDeletedMail(e, tournamentRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("tournoi pas trouvé")));
            } catch (MessagingException ex){
                throw new RuntimeException(ex);
            }
                }
        );
        tournamentRepository.deleteById(id);
    }

    /**
     * renvoye les 10 derniers tournois trier par leurs date de mise a jour par ordre décroissant
     * @return liste de tournois
     */
    @Override
    public List<Tournament> getAll() {
        return tournamentRepository.findFirst10ByStatusOrderByUpdateDateDesc(TournamentStatus.REGISTRATION).stream()
                .peek(e -> e.setRegistrationNbr(tournamentTeamRepository.countByTournament(e)))
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

    /**
     * recherche d'un tournoi par son id
     * @param id du tournoi recherché
     * @return une entité tournoi
     */
    @Override
    public TournamentDTOWithTeams getOne(long id){
        Tournament tournament = tournamentRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("tournoi pas trouvé"));
        List<Long> teamsId = tournamentTeamRepository.findAllByTournament(tournament).stream()
                .map(e -> e.getTeam().getId())
                .toList();
        return TournamentDTOWithTeams.fromEntity(tournament,teamsId);
    }

    /**
     * methode permettant a un capitaine d'inscrire son équipe a un tournoi si son équipe correspond aux condition du tournoi.
     * Conditions: ▪ Si un tournoi n’a pas encore commencé.
     * ▪ Si la date de fin des inscriptions n’est pas dépassée
     * ▪ Si le joueur n’est pas déjà inscrit
     * ▪ Si un tournoi n’a pas atteint le nombre maximum de participants
     * ▪ Si son ELO, l’y autorise
     * L’ELO du joueur doit <= à l’ELO max (si renseigné)
     * L’ELO du joueur doit >= à l’ELO min (si renseigné)
     * ▪ Si son genre l’y autorise
     * Seuls les joueurs (fille et autre) peuvent s’inscrire à un tournoi
     * « WomenOnly »
     * @param id du tournoi
     * @param authentication utilisateur connecté
     */
    @Override
    public void register(long id, Authentication authentication) {
        User userConnected = userRepository.findByUsername(authentication.getName()).orElseThrow(()->new UsernameNotFoundException("utilisateur pas trouvé"));
        Team team = teamRepository.findByCaptain(userConnected).orElseThrow(()->new EntityNotFoundException("équipe pas trouvée"));
        Tournament tournament = tournamentRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("tournoi pas trouvé"));

        if (!tournament.getStatus().equals(TournamentStatus.REGISTRATION) ||
                tournament.getStartingDateTime().minusHours(1).isBefore(LocalDateTime.now()) ||
                tournamentTeamRepository.existsByTeamAndTournament(team,tournament) ||
                tournamentTeamRepository.countByTournament(tournament)>= tournament.getMaxTeam() ||
                team.getTeamElo() < tournament.getMinElo() ||
                team.getTeamElo() > tournament.getMaxElo()) {
            if (tournament.isWomenOnly()&& !team.isAllWomen()) {
                throw new IllegalArgumentException("conditions d'inscription non respectée");
            } else throw new IllegalArgumentException("conditions d'inscription non respectée");
        }

        TournamentTeams registration = TournamentTeams.builder()
                .registrationDate(LocalDate.now())
                .tournament(tournament)
                .team(team)
                .build();

        tournamentTeamRepository.save(registration);
    }

    /**
     * Methode permettant le désenregistrement d'une équipe à un tournoi par le capitaine
     * @param id du tournoi
     * @param authentication utilisateur connecté
     */
    @Override
    public void unregister(long id, Authentication authentication) {
        User userConnected = userRepository.findByUsername(authentication.getName()).orElseThrow(()->new UsernameNotFoundException("utilisateur pas trouvé"));
        Team team = teamRepository.findByCaptain(userConnected).orElseThrow(()->new EntityNotFoundException("équipe pas trouvée"));
        Tournament tournament = tournamentRepository.findById(id).orElseThrow(()->new EntityNotFoundException("tournoi pas trouvé"));
        if (!tournament.getStatus().equals(TournamentStatus.REGISTRATION) || !tournamentTeamRepository.existsByTeamAndTournament(team,tournament)) throw new IllegalArgumentException("condition non respectée");
        tournamentTeamRepository.deleteByTeamAndTournament(team,tournament);
    }

    /**
     * démarre le tournoi
     * @param id du tournoi
     */
    @Override
    public void start(long id) {
        Tournament tournament = tournamentRepository.findById(id).orElseThrow(()->new EntityNotFoundException("tournoi pas trouvé"));
        int nbTeam = tournamentTeamRepository.countByTournament(tournament);
        if ( nbTeam <= tournament.getMinTeam() || tournament.getStartingDateTime().isBefore(LocalDateTime.now().minusMinutes(5))) throw new IllegalArgumentException("condition non respectée");
        tournament.setRound(1);
        tournament.setStatus(TournamentStatus.IN_PROGRESS);
        tournament.setUpdateDate(LocalDate.now());
        List<Team> teamList = tournamentTeamRepository.findAllByTournament(tournament).stream()
                .map(TournamentTeams::getTeam)
                .sorted(Comparator.comparingInt(Team::getTeamElo).reversed())
                .toList();
        List<Team> top = teamList.stream()
                .filter(e -> teamList.indexOf(e)%2 == 0)
                .toList();
        List<Team> bottom = teamList.stream()
                .filter(e -> teamList.indexOf(e)%2 != 0)
                .toList();

        List<Match> topMatches = drawMatches(top, tournament.getRound());
        List<Match> bottomMatches = drawMatches(bottom, tournament.getRound());

        List<Match> allMatches = new ArrayList<>();
        allMatches.addAll(topMatches);
        allMatches.addAll(bottomMatches);

        allMatches.forEach(e -> e.setMatchNumber(allMatches.indexOf(e)));

        matchRepository.saveAll(allMatches);

        tournamentRepository.save(tournament);
    }

    /**
     * fonction permettant de passer a la ronde suivante pour le tournoi
     * @param id du tournoi en cours
     */
    @Override
    public void nextround(long id) {
        Tournament tournament = tournamentRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("tournoi pas trouvé"));
        List<Match> matchList = matchRepository.findAllByTournament(tournament);
        if (!matchList.stream().allMatch(match -> match.isBye() || (match.getScoreTeam1() != 0 && match.getScoreTeam2() != 0))) throw new IllegalArgumentException("les matchs ne sont pas terminés");
        List<Team> teamList = matchList.stream()
                .sorted(Comparator.comparingLong(Match::getMatchNumber))
                .map(e -> {
                    if (e.getScoreTeam1()>e.getScoreTeam2()) {
                        return e.getTeam1();
                    } else return e.getTeam2();
                })
                .toList();
        tournament.setRound(tournament.getRound()+1);
        List<Match> nextRoundMatches = drawNextRoundMatches(teamList, tournament.getRound());

        matchRepository.saveAll(nextRoundMatches);

        tournamentRepository.save(tournament);
    }

    /**
     * creer les match des rounds suivant
     * @param teams liste d'équipe non éliminée
     * @param currentRound ronde courante
     * @return liste de match de cette ronde
     */
    private List<Match> drawNextRoundMatches(List<Team> teams, int currentRound) {
        List<Match> matches = new ArrayList<>();
        while (!teams.isEmpty()) {
            if (teams.size() % 2 != 0) {
                Match match = Match.builder()
                        .team1(teams.removeFirst())
                        .isBye(true)
                        .currentRound(currentRound)
                        .build();
                matches.add(match);
            }
            Match match = Match.builder()
                    .team1(teams.removeFirst())
                    .team2(teams.removeFirst())
                    .currentRound(currentRound)
                    .build();
            switch (teams.size()){
                case 4 -> match.setMaxGame(3);
                case 2 -> match.setMaxGame(5);
                default -> match.setMaxGame(1);
            }
            matches.add(match);
        }
        return matches;
    }
    /**
     * methode permettant la création de match
     * @param teams liste d'équipe encore présente au tournoi
     * @param currentRound ronde courrante
     * @return liste de match
     */
    private List<Match> drawMatches(List<Team> teams, int currentRound) {
        List<Match> matches = new ArrayList<>();
        while (!teams.isEmpty()) {
            if (teams.size() % 2 != 0){
                Match match = Match.builder()
                        .team1(teams.removeFirst())
                        .isBye(true)
                        .currentRound(currentRound)
                        .build();
                matches.add(match);
            }
            Match match = Match.builder()
                    .team1(teams.removeFirst())
                    .team2(teams.removeLast())
                    .currentRound(currentRound)
                    .build();
            switch (teams.size()){
                case 4 -> match.setMaxGame(3);
                case 2 -> match.setMaxGame(5);
                default -> match.setMaxGame(1);
            }
            matches.add(match);
        }
        return matches;
    }
    /**
     * Spécification de la recherche par spec. si l'option canRegister est true, recherche les tournoi dans lesquels l'équipe peut s'enregistrer.
     * Si l'option isRegistered est true, renvoye les predicats dans lesquels les tournoi auxquels l'équipe est inscrite
     * @param form formulaire de recherche
     * @param team équipe dont le capitaine est connecté
     * @return liste de spécification
     */
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


