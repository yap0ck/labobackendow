package be.yapock.overwatchtournamentmanager.bll.team;

import be.yapock.overwatchtournamentmanager.dal.models.Team;
import be.yapock.overwatchtournamentmanager.pl.models.team.forms.TeamForm;
import be.yapock.overwatchtournamentmanager.pl.models.team.forms.TeamSearchForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface TeamService {
    void create(TeamForm form, Authentication authentication);
    Team getOne(long id);
    Page<Team> getAll(Pageable pageable);
    void update(TeamForm form, long id, Authentication authentication);
    void delete(long id, Authentication authentication);
    Page<Team> getAllBySpec(TeamSearchForm form, Pageable pageable);
}
