package be.yapock.overwatchtournamentmanager.bll.tournament;

import be.yapock.overwatchtournamentmanager.dal.models.Tournament;
import be.yapock.overwatchtournamentmanager.dal.models.enums.TournamentStatus;
import be.yapock.overwatchtournamentmanager.dal.repositories.TournamentRepository;
import be.yapock.overwatchtournamentmanager.dal.repositories.UserRepository;
import be.yapock.overwatchtournamentmanager.pl.models.tournament.forms.TournamentForm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TournamentServiceImpl implements TournamentService{
    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;

    public TournamentServiceImpl(TournamentRepository tournamentRepository, UserRepository userRepository) {
        this.tournamentRepository = tournamentRepository;
        this.userRepository = userRepository;
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
        tournamentRepository.save(tournament);
    }
}
