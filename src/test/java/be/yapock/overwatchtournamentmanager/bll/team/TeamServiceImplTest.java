package be.yapock.overwatchtournamentmanager.bll.team;

import be.yapock.overwatchtournamentmanager.bll.user.UserService;
import be.yapock.overwatchtournamentmanager.dal.models.Team;
import be.yapock.overwatchtournamentmanager.dal.models.User;
import be.yapock.overwatchtournamentmanager.dal.models.enums.UserRole;
import be.yapock.overwatchtournamentmanager.dal.repositories.TeamRepository;
import be.yapock.overwatchtournamentmanager.dal.repositories.UserRepository;
import be.yapock.overwatchtournamentmanager.pl.models.team.forms.TeamForm;
import be.yapock.overwatchtournamentmanager.pl.models.team.forms.TeamSearchForm;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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
        userConnected = User.builder()
            .isEnabled(true)
            .build();
        List<Long> playerIds = new ArrayList<>();
        user= User.builder()
                .isEnabled(true)
                .build();
        List<User> players = new ArrayList<>();
        players.add(user);
        form = new TeamForm("username", 1200,2L,playerIds);
        entity = new Team(1L, LocalDate.now(),form.teamName(), form.teamElo(), userConnected, players,true);

    }

    @Test
    void create() {

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userConnected));
        when(teamRepository.existsByPlayerListContaining(any(User.class))).thenReturn(false);
        when(teamRepository.save(any())).thenReturn(entity);

        teamService.create(form,authentication);

        verify(teamRepository,times(1)).save(any(Team.class));
    }

    @Test
    void create_when_form_null(){
        Exception exception = assertThrows(IllegalArgumentException.class, ()-> teamService.create(null,authentication));

        String expectedMessage = "form ne peut etre vide";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    void create_when_userConnected_already_in_any_team(){
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userConnected));
        when(teamRepository.existsByPlayerListContaining(any(User.class))).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class,()-> teamService.create(form,authentication));

        String expectedMessage = "l'utilisateur ne peut pas creer d'équipe si il fait partie d'une équipe";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    void getOne_when_ok(){
        when(teamRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        Team searchedEntity = teamService.getOne(anyLong());

        assertEquals(entity,searchedEntity);
    }

    @Test
    void getOne_when_not_found(){
        when(teamRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, ()-> teamService.getOne(anyLong()));

        String expectedMessage = "équipe pas trouvée";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    void getAll(){
        Pageable pageable = mock(Pageable.class);
        Page entities = mock(Page.class);

        when(teamRepository.findAll(pageable)).thenReturn(entities);

        Page result = teamService.getAll(pageable);

        assertEquals(entities,result);
    }

    @Test
    void update_when_ok_Admin(){
        userConnected.setUserRoles(Collections.singletonList(UserRole.ADMIN));
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userConnected));
        when(teamRepository.save(any())).thenReturn(entity);
        when(teamRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        teamService.update(form, anyLong(), authentication);

        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    void update_when_ok_Captain(){
        userConnected.setUserRoles(Collections.singletonList(UserRole.PLAYER));
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userConnected));
        when(teamRepository.save(any())).thenReturn(entity);
        when(teamRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        teamService.update(form, anyLong(), authentication);

        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    void update_when_kO_form_null(){
        Exception exception = assertThrows(IllegalArgumentException.class, ()-> teamService.update(null, 1L, authentication));

        String expectedMessage = "le formulaire ne peut etre null";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void update_when_ko_username_not_found(){
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(teamRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        Exception exception = assertThrows(UsernameNotFoundException.class, () -> teamService.update(form, 1L, authentication));

        String expectedMessage = "utilisateur pas trouvé";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void update_when_ko_unauthorize(){
        userConnected.setUserRoles(Collections.singletonList(UserRole.PLAYER));
        entity.setCaptain(user);

        when(userRepository.findByUsername((any()))).thenReturn(Optional.of(userConnected));
        when(teamRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        Exception exception = assertThrows(BadCredentialsException.class, () -> teamService.update(form, 1L, authentication));

        String expectedMessage = "l'utilisateur connecté ne peut modifier cette équipe";

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void delete_when_ok_admin(){
        userConnected.setUserRoles(Collections.singletonList(UserRole.ADMIN));
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userConnected));
        when(teamRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        teamService.delete(1L, authentication);

        verify(teamRepository, times(1)).delete(any(Team.class));
    }

    @Test
    void delete_when_ok_captain(){
        userConnected.setUserRoles(Collections.singletonList(UserRole.PLAYER));
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userConnected));
        when(teamRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        teamService.delete(1L,authentication);

        verify(teamRepository, times(1)).delete(any(Team.class));
    }

    @Test
    void delete_when_ko_unauthorized(){
        userConnected.setUserRoles(Collections.singletonList(UserRole.PLAYER));
        entity.setCaptain(user);

        when(userRepository.findByUsername((any()))).thenReturn(Optional.of(userConnected));
        when(teamRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        Exception exception = assertThrows(BadCredentialsException.class, () -> teamService.update(form, 1L, authentication));

        String expectedMessage = "l'utilisateur connecté ne peut modifier cette équipe";

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void getAllBySpec(){
        TeamSearchForm teamSearchForm = mock(TeamSearchForm.class);
        Pageable pageable = mock(Pageable.class);
        Page entities= mock(Page.class);

        when(teamRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(entities);

        Page<Team> result = teamService.getAllBySpec(teamSearchForm, pageable);

        assertEquals(entities,result);
    }
}