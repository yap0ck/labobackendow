package be.yapock.overwatchtournamentmanager.dal.utils;

import be.yapock.overwatchtournamentmanager.dal.models.Team;
import be.yapock.overwatchtournamentmanager.dal.models.User;
import be.yapock.overwatchtournamentmanager.dal.models.enums.InGameRole;
import be.yapock.overwatchtournamentmanager.dal.models.enums.UserRole;
import be.yapock.overwatchtournamentmanager.dal.repositories.TeamRepository;
import be.yapock.overwatchtournamentmanager.dal.repositories.UserRepository;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Locale;

@Component
public class DataInit implements InitializingBean {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TeamRepository teamRepository;
    boolean initialisation=true;

    public DataInit(UserRepository userRepository, PasswordEncoder passwordEncoder, TeamRepository teamRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.teamRepository = teamRepository;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Faker fakerFR = Faker.instance(new Locale("fr-FR"));
        User user = User.builder()
                .password(passwordEncoder.encode("string"))
                .gender('M')
                .email("vagocol930@ziragold.com")
                .username("string")
                .userRoles(Collections.singletonList(UserRole.PLAYER))
                .ranking("Master")
                .inGameRoles(Collections.singletonList(InGameRole.TANK))
                .dateOfBirth(LocalDate.now())
                .battleNet("yapock#2115")
                .isEnabled(true)
                .build();
        userRepository.save(user);
        if (initialisation){
            for (int i = 0; i < 20; i++) {
                User user1 = User.builder()
                        .username(fakerFR.overwatch().hero())
                        .email(fakerFR.internet().emailAddress())
                        .password(fakerFR.internet().password())
                        .battleNet(fakerFR.beer().name())
                        .userRoles(Collections.singletonList(UserRole.PLAYER))
                        .isEnabled(true)
                        .build();
                if (i%2==0) {
                    user1.setGender('M');
                } else user1.setGender('F');
                switch (i%5){
                    case 0 -> {
                        user1.setInGameRoles(Collections.singletonList(InGameRole.TANK));
                        user1.setRanking("Bronze");
                    }
                    case 1 -> {
                        user1.setInGameRoles(Collections.singletonList(InGameRole.DPS));
                        user1.setRanking("Silver");
                    }
                    case 2 -> {
                        user1.setInGameRoles(Collections.singletonList(InGameRole.SUPPORT));
                        user1.setRanking("Gold");
                    }
                    case 3 -> {
                        user1.setInGameRoles(Collections.singletonList(InGameRole.DPS));
                        user1.setRanking("Platine");
                    }
                    case 4 -> {
                        user1.setInGameRoles(Collections.singletonList(InGameRole.SUPPORT));
                        user1.setRanking("Diamond");
                    }
                }
                user1.setDateOfBirth(LocalDate.of(2005-i,6,13));
                userRepository.save(user1);
            }
            for (int i = 1; i < 6; i++) {
                Team team = Team.builder()
                        .teamElo(1200)
                        .teamName(fakerFR.overwatch().quote())
                        .captain(userRepository.findById((long) i+1).get())
                        .creationDate(LocalDate.now())
                        .build();
                teamRepository.save(team);
            }
        }
    }
}
