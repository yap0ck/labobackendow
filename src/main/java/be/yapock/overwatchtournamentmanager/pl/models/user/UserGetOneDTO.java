package be.yapock.overwatchtournamentmanager.pl.models.user;

import be.yapock.overwatchtournamentmanager.dal.models.User;
import be.yapock.overwatchtournamentmanager.dal.models.enums.InGameRole;

import java.time.LocalDate;
import java.util.List;

public record UserGetOneDTO(
        String username,
        String ranking,
        LocalDate dateOfBirth,
        char gender,
        List<InGameRole> inGameRoles
) {
    public static UserGetOneDTO fromEntity(User user){
        return new UserGetOneDTO(user.getUsername(),user.getRanking(),user.getDateOfBirth(),user.getGender(),user.getInGameRoles());
    }
}
