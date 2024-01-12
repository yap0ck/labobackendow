package be.yapock.overwatchtournamentmanager.pl.Controllers;

import be.yapock.overwatchtournamentmanager.bll.team.TeamService;
import be.yapock.overwatchtournamentmanager.pl.models.team.forms.TeamForm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/team")
public class TeamController {
    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PreAuthorize("hasRole('ROLE_PLAYER')")
    @PostMapping
    public void create(@RequestBody TeamForm form, Authentication authentication){
        teamService.create(form, authentication);
    }
}
