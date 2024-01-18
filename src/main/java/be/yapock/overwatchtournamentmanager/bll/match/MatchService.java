package be.yapock.overwatchtournamentmanager.bll.match;

import be.yapock.overwatchtournamentmanager.dal.models.Match;
import be.yapock.overwatchtournamentmanager.dal.models.Team;
import be.yapock.overwatchtournamentmanager.dal.models.Tournament;
import be.yapock.overwatchtournamentmanager.pl.models.match.form.ScoreUpdateForm;

import java.util.List;

public interface MatchService {
    void update(long id, ScoreUpdateForm form);
    List<Match> getAllByRound(long id, int round);
}
