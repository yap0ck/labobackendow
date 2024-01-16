package be.yapock.overwatchtournamentmanager.pl.Controllers;

import be.yapock.overwatchtournamentmanager.bll.tournament.TournamentService;
import be.yapock.overwatchtournamentmanager.pl.models.tournament.dtos.TournamentDTO;
import be.yapock.overwatchtournamentmanager.pl.models.tournament.dtos.TournamentDTOWithTeams;
import be.yapock.overwatchtournamentmanager.pl.models.tournament.forms.TournamentForm;
import be.yapock.overwatchtournamentmanager.pl.models.tournament.forms.TournamentSearchForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/tournament")
public class TournamentController {
    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    /**
     * controller appelant la création de tournoi, seul les admin peuvent creer un tournoi
     *
     * @param form formulaire de création de tournoi
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public void create(@RequestBody TournamentForm form) {
        tournamentService.create(form);
    }

    /**
     * controller appelant la suppression d'un tournoi, seul les admin y ont accés
     *
     * @param id du tournoi a supprimer
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        tournamentService.delete(id);
    }

    /**
     * controller appellant la liste des 10derniers tournoi
     *
     * @return lis de tournois sous forme de DTO
     */
    @GetMapping
    public ResponseEntity<List<TournamentDTO>> getAll() {
        return ResponseEntity.ok(tournamentService.getAll().stream()
                .map(TournamentDTO::fromEntity)
                .toList());
    }

    /**
     * controller appelant la recherche par spécificité
     *
     * @param form           formulaire de recherche
     * @param pageable       parametre d'affichage
     * @param authentication utilisateur connecté
     * @return page de tournois correspondant aux spécificité de la recherche
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/search")
    public ResponseEntity<Page<TournamentDTO>> getAllBySpec(@RequestBody TournamentSearchForm form, Pageable pageable, Authentication authentication) {
        return ResponseEntity.ok(tournamentService.getAllBySpec(form, pageable, authentication).map(TournamentDTO::fromEntity));
    }

    /**
     * Controller appelant la recherche par id
     *
     * @param id du tournoi recherché
     * @return un tournoi sous sa forme dto
     */
    @GetMapping("/{id}")
    public ResponseEntity<TournamentDTOWithTeams> getOne(@PathVariable long id) {
        return ResponseEntity.ok(tournamentService.getOne(id));
    }

    /**
     * controller appelant l'enregistrement d'une équipe a un tournoi
     *
     * @param id             du tournoi
     * @param authentication utilisateur connecté
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/register/{id}")
    public void register(@PathVariable long id, Authentication authentication) {
        tournamentService.register(id, authentication);
    }

    /**
     * controller appelant la methode de suppression d'enregistrement à un tournoi
     * @param id du tournoi
     * @param authentication pour récupérer l'équipe de l'utilisateur
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/unregister/{id}")
    public void unregister(@PathVariable long id, Authentication authentication){
        tournamentService.unregister(id, authentication);
    }
}
