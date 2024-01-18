package be.yapock.overwatchtournamentmanager.bll.match;

import be.yapock.overwatchtournamentmanager.dal.models.Match;
import be.yapock.overwatchtournamentmanager.dal.models.Team;
import be.yapock.overwatchtournamentmanager.dal.models.Tournament;
import be.yapock.overwatchtournamentmanager.dal.repositories.MatchRepository;
import be.yapock.overwatchtournamentmanager.dal.repositories.TournamentRepository;
import be.yapock.overwatchtournamentmanager.pl.models.match.form.ScoreUpdateForm;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MatchServiceImpl implements MatchService{
    private final MatchRepository matchRepository;
    private final TournamentRepository tournamentRepository;

    public MatchServiceImpl(MatchRepository matchRepository, TournamentRepository tournamentRepository) {
        this.matchRepository = matchRepository;
        this.tournamentRepository = tournamentRepository;
    }

    /**
     * met a jour les score d'un match
     * @param id du match a mettre a jour
     * @param form contenant les scores
     */
    @Override
    public void update(long id, ScoreUpdateForm form) {
        Match match = matchRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("match pas trouvé"));
        if (match.getCurrentRound() != match.getTournament().getRound()) throw new IllegalArgumentException("modification d'un score d'une ronde differente interdite");
        match.setScoreTeam1(form.scoreTeam1());
        match.setScoreTeam2(form.scoreTeam2());
        matchRepository.save(match);
    }
}
