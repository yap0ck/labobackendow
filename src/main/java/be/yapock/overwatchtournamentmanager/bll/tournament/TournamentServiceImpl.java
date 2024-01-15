package be.yapock.overwatchtournamentmanager.bll.tournament;

import be.yapock.overwatchtournamentmanager.bll.mailing.EmailService;
import be.yapock.overwatchtournamentmanager.dal.models.Tournament;
import be.yapock.overwatchtournamentmanager.dal.models.enums.TournamentStatus;
import be.yapock.overwatchtournamentmanager.dal.repositories.TeamRepository;
import be.yapock.overwatchtournamentmanager.dal.repositories.TournamentRepository;
import be.yapock.overwatchtournamentmanager.dal.repositories.UserRepository;
import be.yapock.overwatchtournamentmanager.pl.models.tournament.forms.TournamentForm;
import jakarta.mail.MessagingException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TournamentServiceImpl implements TournamentService{
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final EmailService emailService;

    public TournamentServiceImpl(TournamentRepository tournamentRepository, UserRepository userRepository, TeamRepository teamRepository, EmailService emailService) {
        this.tournamentRepository = tournamentRepository;
        this.teamRepository = teamRepository;
        this.emailService = emailService;
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
}
