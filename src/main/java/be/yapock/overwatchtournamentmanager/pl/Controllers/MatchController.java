package be.yapock.overwatchtournamentmanager.pl.Controllers;

import be.yapock.overwatchtournamentmanager.bll.match.MatchService;
import be.yapock.overwatchtournamentmanager.pl.models.match.dtos.MatchDto;
import be.yapock.overwatchtournamentmanager.pl.models.match.form.ScoreUpdateForm;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/match")
public class MatchController {
    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    /**
     * controller permettant la mise a jour d'un match par l'admin ou un moderateur
     * @param id du match a mettre a jour
     * @param form des scores
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_MODERATOR')")
    @PutMapping("/{id}")
    public void update(@PathVariable long id, @RequestBody ScoreUpdateForm form){
        matchService.update(id,form);
    }

    /**
     * Controller affichant les match par tournoi et ronde
     * @param id du tournoi
     * @param round ronde à afficher
     * @return liste de match sous form de DTO
     */
    @GetMapping("/{id}/{round}")
    public ResponseEntity<List<MatchDto>> getAllByTournamentAndRound(@PathVariable long id, @PathVariable int round){
        return ResponseEntity.ok(matchService.getAllByRound(id, round).stream()
                .map(MatchDto::fromEntity)
                .toList());
    }
}
