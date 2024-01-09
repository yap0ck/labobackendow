package be.yapock.overwatchtournamentmanager.pl.models.user;

import be.yapock.overwatchtournamentmanager.dal.models.User;
import be.yapock.overwatchtournamentmanager.dal.models.enums.InGameRole;
import be.yapock.overwatchtournamentmanager.dal.models.enums.UserRole;

import java.time.LocalDate;
import java.util.List;

public record UserGetOneDTO(
        Long id,
        String username,
        String ranking,
        LocalDate dateOfBirth,
        char gender,
        List<InGameRole> inGameRoles,
        List<UserRole> userRoles
) {
    public static UserGetOneDTO fromEntity(User user){
        return new UserGetOneDTO(user.getId(), user.getUsername(),user.getRanking(),user.getDateOfBirth(),user.getGender(),user.getInGameRoles(), user.getUserRoles());
    }
}
