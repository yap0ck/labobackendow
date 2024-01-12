package be.yapock.overwatchtournamentmanager.pl.models.team.forms;

import be.yapock.overwatchtournamentmanager.dal.models.User;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link be.yapock.overwatchtournamentmanager.dal.models.Team}
 */
public record TeamForm(String teamName, int teamElo, long Captainid, List<Long> playerListId) implements Serializable {
}