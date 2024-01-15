package be.yapock.overwatchtournamentmanager.pl.Controllers;

import be.yapock.overwatchtournamentmanager.bll.tournament.TournamentService;
import be.yapock.overwatchtournamentmanager.pl.models.tournament.forms.TournamentForm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    /**
     * controller appelant la suppression d'un tournoi, seul les admin y ont accés
     * @param id du tournoi a supprimer
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id){
        tournamentService.delete(id);
    }
}
