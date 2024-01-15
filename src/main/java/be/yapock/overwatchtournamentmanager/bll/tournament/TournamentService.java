package be.yapock.overwatchtournamentmanager.bll.tournament;

import be.yapock.overwatchtournamentmanager.pl.models.tournament.forms.TournamentForm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public interface TournamentService {
    void create(TournamentForm form);
}
