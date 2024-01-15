package be.yapock.overwatchtournamentmanager.bll.team;

import be.yapock.overwatchtournamentmanager.bll.user.UserService;
import be.yapock.overwatchtournamentmanager.dal.models.Team;
import be.yapock.overwatchtournamentmanager.dal.models.User;
import be.yapock.overwatchtournamentmanager.dal.repositories.TeamRepository;
import be.yapock.overwatchtournamentmanager.dal.repositories.UserRepository;
import be.yapock.overwatchtournamentmanager.pl.models.team.forms.TeamForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TeamRepository teamRepository;
    @InjectMocks
    private TeamServiceImpl teamService;
    @Mock
    private Authentication authentication;
    private TeamForm form;
    private Team entity;
    private User userConnected;
    private User user;

    @BeforeEach
    void setUp(){
        List<Long> playerIds = new ArrayList<>();
        user= User.builder()
                .isEnabled(true)
                .build();
        List<User> players = new ArrayList<>();
        players.add(user);
        form = new TeamForm("username", 1200,2L,playerIds);
        entity = new Team(1L, LocalDate.now(),form.teamName(), form.teamElo(), userConnected, players);
        userConnected = User.builder()
                .isEnabled(true)
                .build();
    }

    @Test
    void create() {

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userConnected));
        when(teamRepository.existsByPlayerListContaining(any(User.class))).thenReturn(false);
        when(teamRepository.save(any())).thenReturn(entity);

        teamService.create(form,authentication);

        verify(teamRepository,times(1)).save(any(Team.class));
    }
}