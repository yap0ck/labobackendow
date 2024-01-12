package be.yapock.overwatchtournamentmanager.bll.team;

import be.yapock.overwatchtournamentmanager.pl.models.team.forms.TeamForm;
import org.springframework.security.core.Authentication;

public interface TeamService {
    void create(TeamForm form, Authentication authentication);
}
