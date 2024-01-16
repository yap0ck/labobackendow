package be.yapock.overwatchtournamentmanager.bll.tournament;

import be.yapock.overwatchtournamentmanager.dal.models.Tournament;
import be.yapock.overwatchtournamentmanager.dal.models.enums.TournamentStatus;
import be.yapock.overwatchtournamentmanager.pl.models.tournament.forms.TournamentForm;
import be.yapock.overwatchtournamentmanager.pl.models.tournament.forms.TournamentSearchForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TournamentService {
    void create(TournamentForm form);
    void delete(long id);
    void register(long id, Authentication authentication);
    Tournament getOne(long id);
    List<Tournament> getAll();
    Page<Tournament> getAllBySpec(TournamentSearchForm form, Pageable pageable, Authentication authentication);
}
