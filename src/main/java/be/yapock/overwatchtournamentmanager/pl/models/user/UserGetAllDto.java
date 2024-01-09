package be.yapock.overwatchtournamentmanager.pl.models.user;

import be.yapock.overwatchtournamentmanager.dal.models.User;
import be.yapock.overwatchtournamentmanager.dal.models.enums.InGameRole;

import java.util.List;

public record UserGetAllDto(
        long id,
        String username,
        List<InGameRole> inGameRoles,
        String ranking
) {
    public static UserGetAllDto fromEntity(User user){
        return new UserGetAllDto(user.getId(), user.getUsername(), user.getInGameRoles(), user.getRanking());
    }
}
