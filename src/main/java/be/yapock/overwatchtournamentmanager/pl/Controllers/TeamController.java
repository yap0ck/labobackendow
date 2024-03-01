package be.yapock.overwatchtournamentmanager.pl.Controllers;

import be.yapock.overwatchtournamentmanager.bll.team.TeamService;
import be.yapock.overwatchtournamentmanager.pl.models.team.dtos.TeamFullDTO;
import be.yapock.overwatchtournamentmanager.pl.models.team.dtos.TeamShortDto;
import be.yapock.overwatchtournamentmanager.pl.models.team.forms.TeamForm;
import be.yapock.overwatchtournamentmanager.pl.models.team.forms.TeamSearchForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/team")
public class TeamController {
    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    /**
     * controller de la création d'une équipe
     * @param form formulaire d'enregiqtrement d'une équipe
     * @param authentication authentification pour récuperer l'utilisateur de connecté
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public void create(@RequestBody TeamForm form, Authentication authentication){
        teamService.create(form, authentication);
    }

    /**
     * controller de la recherche d'une équipe
     * @param id de l'équipe recherchée
     * @return Dto de l'équipe trouvée
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<TeamFullDTO> getOne(@PathVariable long id){
        return ResponseEntity.ok(TeamFullDTO.fromEntity(teamService.getOne(id)));
    }

    /**
     * controller de l'affichage de toute les équipes
     * @param pageable parametre de pagination
     * @return TeamShortDTO des équipes enregistrée
     */
    @GetMapping
    public ResponseEntity<Page<TeamShortDto>> getAll(Pageable pageable){
        return ResponseEntity.ok(teamService.getAll(pageable).map(TeamShortDto::fromEntity));
    }

    /**
     * controller de l'update d'une équipe
     * @param id de l'équipe a mettre a jour
     * @param form nouvelles données de l'équipe
     * @param authentication utilisateur connecté
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public void update(@PathVariable long id, @RequestBody TeamForm form, Authentication authentication){
        teamService.update(form,id,authentication);
    }

    /**
     * Controller du delete d'une équipe
     * @param id de l'équipe à supprimer
     * @param authentication ustilisateur connecté
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public void  delete(@PathVariable long id, Authentication authentication){
        teamService.delete(id, authentication);
    }

    /**
     * Controller appelant la fonction de recherche par spécification
     * @param form formulaire de recherche
     * @param pageable parametres de pagination
     * @return une page de team en short dto
     */
    /*
    @PostMapping("/search")
    public ResponseEntity<Page<TeamShortDto>> getAllBySpec(@RequestBody TeamSearchForm form, Pageable pageable){
        return ResponseEntity.ok(teamService.getAllBySpec(form, pageable).map(TeamShortDto::fromEntity));
    }*/

    @PostMapping("/allbyid")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TeamFullDTO>> getAllById(@RequestBody List<Long> ids){
        return ResponseEntity.ok(teamService.getAllByIds(ids).stream().map(TeamFullDTO::fromEntity).toList());
    }
}
