package be.yapock.overwatchtournamentmanager.pl.Controllers;

import be.yapock.overwatchtournamentmanager.bll.team.TeamService;
import be.yapock.overwatchtournamentmanager.pl.models.team.dtos.TeamFullDTO;
import be.yapock.overwatchtournamentmanager.pl.models.team.dtos.TeamShortDto;
import be.yapock.overwatchtournamentmanager.pl.models.team.forms.TeamForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("hasRole('ROLE_PLAYER')")
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
}
