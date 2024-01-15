package be.yapock.overwatchtournamentmanager.pl.Controllers;

import be.yapock.overwatchtournamentmanager.bll.tournament.TournamentService;
import be.yapock.overwatchtournamentmanager.pl.models.tournament.forms.TournamentForm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/tournament")
public class TournamentController {
    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    /**
     * controller appelant la création de tournoi, seul les admin peuvent creer un tournoi
     * @param form formulaire de création de tournoi
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public void create(@RequestBody TournamentForm form){
        tournamentService.create(form);
    }
}
