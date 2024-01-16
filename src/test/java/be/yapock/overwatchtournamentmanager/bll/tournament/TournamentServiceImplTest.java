package be.yapock.overwatchtournamentmanager.bll.tournament;

import be.yapock.overwatchtournamentmanager.dal.models.Team;
import be.yapock.overwatchtournamentmanager.dal.models.Tournament;
import be.yapock.overwatchtournamentmanager.dal.models.User;
import be.yapock.overwatchtournamentmanager.dal.models.compositeKey.TournamentTeamCompositeKey;
import be.yapock.overwatchtournamentmanager.dal.models.enums.TournamentCategories;
import be.yapock.overwatchtournamentmanager.dal.models.enums.TournamentStatus;
import be.yapock.overwatchtournamentmanager.dal.models.jointable.TournamentTeams;
import be.yapock.overwatchtournamentmanager.dal.repositories.TeamRepository;
import be.yapock.overwatchtournamentmanager.dal.repositories.TournamentRepository;
import be.yapock.overwatchtournamentmanager.dal.repositories.TournamentTeamRepository;
import be.yapock.overwatchtournamentmanager.dal.repositories.UserRepository;
import be.yapock.overwatchtournamentmanager.pl.models.tournament.dtos.TournamentDTOWithTeams;
import be.yapock.overwatchtournamentmanager.pl.models.tournament.forms.TournamentForm;

import be.yapock.overwatchtournamentmanager.pl.models.tournament.forms.TournamentSearchForm;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentServiceImplTest {
    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Authentication authentication;
    @Mock
    private TournamentTeamRepository tournamentTeamRepository;
    @InjectMocks
    private TournamentServiceImpl tournamentService;
    private Tournament entity;
    private TournamentForm form;
    private User user;
    private Team team;
    private TournamentTeams tournamentTeams;

    @BeforeEach
    void setUp(){
        form = new TournamentForm("name",2,4,0,3000, Collections.singletonList(TournamentCategories.JUNIOR), false, LocalDateTime.now().plusYears(1));
        entity = Tournament.builder()
                .name(form.name())
                .minTeam(form.minTeam())
                .maxTeam(form.maxTeam())
                .minElo(form.minElo())
                .maxElo(form.maxElo())
                .categories(form.categories())
                .status(TournamentStatus.REGISTRATION)
                .round(0)
                .isWomenOnly(form.isWomenOnly())
                .startingDateTime(form.startingDateTime())
                .creationDate(LocalDate.now())
                .build();
        user = new User();
        team = new Team();
        tournamentTeams = new TournamentTeams();
    }

    @Test
    void create_when_ok() {
        when(tournamentRepository.save(any())).thenReturn(entity);

        tournamentService.create(form);

        verify(tournamentRepository,times(1)).save(any(Tournament.class));
    }

    @Test
    void create_when_ko_form_null(){
        Exception exception = assertThrows(IllegalArgumentException.class, ()-> tournamentService.create(null));

        String expectedMessage = "le formulaire ne peut etre vide";

        assertEquals(expectedMessage,exception.getMessage());
    }

    @Test
    void delete_when_ok(){
        tournamentService.delete(1L);

        verify(tournamentRepository,times(1)).deleteById(anyLong());
    }

    @Test
    void getAll(){
        List<Tournament> entites = new ArrayList<>();
        entites.add(entity);
        when(tournamentRepository.findFirst10ByStatusOrderByUpdateDateDesc(any())).thenReturn(entites);
        when(tournamentTeamRepository.countAllByTournament(entity)).thenReturn(1);

        List<Tournament> result = tournamentService.getAll();

        assertEquals(entites,result);
    }

    @Test
    void getAllBySpec_when_ok(){
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(teamRepository.findByCaptain(any())).thenReturn(Optional.of(team));
        TournamentSearchForm searchForm = mock(TournamentSearchForm.class);
        Pageable pageable = mock(Pageable.class);
        Page entities = mock(Page.class);
        when(tournamentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(entities);

        Page<Tournament> result = tournamentService.getAllBySpec(searchForm,pageable,authentication);

        assertEquals(entities,result);
    }

    @Test
    void getAllBySpec_when_ko_userNotFound(){
        Pageable pageable = mock(Pageable.class);
        TournamentSearchForm searchForm = mock(TournamentSearchForm.class);
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        Exception exception = assertThrows(UsernameNotFoundException.class, ()->tournamentService.getAllBySpec(searchForm,pageable,authentication));

        String expectedMessage = "utilisateur pas trouvé";

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void getAllBySpec_when_ko_teamNotFound(){
        Pageable pageable = mock(Pageable.class);
        TournamentSearchForm searchForm = mock(TournamentSearchForm.class);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(teamRepository.findByCaptain(any())).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class, ()->tournamentService.getAllBySpec(searchForm,pageable,authentication));

        String expectedMessage = "équipe pas trouvée";

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void getOne(){
        List<Long> teamList= new ArrayList<>();
        teamList.add(0L);
        TournamentTeamCompositeKey tournamentTeamCompositeKey = new TournamentTeamCompositeKey(team, entity);
        TournamentTeams tournamentTeams = new TournamentTeams(tournamentTeamCompositeKey, LocalDate.now(), 1,team,entity);
        when(tournamentRepository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(tournamentTeamRepository.findAllByTournament(any(Tournament.class))).thenReturn(Collections.singletonList(tournamentTeams));

        TournamentDTOWithTeams result = tournamentService.getOne(1L);
        TournamentDTOWithTeams expected = TournamentDTOWithTeams.fromEntity(entity, teamList);

        assertEquals(expected,result);
    }

    @Test
    void getOne_when_Ko_TournamentNotFound(){
        when(tournamentRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,()->tournamentService.getOne(1L));

        String expectedMessage = "tournoi pas trouvé";

        assertEquals(expectedMessage, exception.getMessage());
    }


    @Test
    void register_when_ok(){
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(teamRepository.findByCaptain(any(User.class))).thenReturn(Optional.of(team));
        when(tournamentRepository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(tournamentTeamRepository.save(any())).thenReturn(tournamentTeams);

        entity.setStatus(TournamentStatus.REGISTRATION);
        entity.setStartingDateTime(LocalDateTime.now().plusYears(1));
        entity.setMaxTeam(32);
        entity.setMinElo(0);
        entity.setMaxElo(3000);
        entity.setWomenOnly(false);
        team.setTeamElo(1200);

        tournamentService.register(1L, authentication);

        verify(tournamentTeamRepository,times(1)).save(any(TournamentTeams.class));
    }

    @Test
    void register_when_ko_userNotFound(){
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        Exception exception = assertThrows(UsernameNotFoundException.class, ()-> tournamentService.register(1L,authentication));

        String expectedMessage = "utilisateur pas trouvé";

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void register_when_ko_teamNotFound(){
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(teamRepository.findByCaptain(any(User.class))).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> tournamentService.register(1L,authentication));

        String expectedMessage = "équipe pas trouvée";

        assertEquals(expectedMessage,exception.getMessage());
    }

    @Test
    void register_when_ko_tournamentNotFound(){
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(teamRepository.findByCaptain(any(User.class))).thenReturn(Optional.of(team));
        when(tournamentRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> tournamentService.register(1L,authentication));

        String expectedMessage = "tournoi pas trouvé";

        assertEquals(expectedMessage,exception.getMessage());
    }

    @Test
    void register_when_ko_statusInvalid(){
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(teamRepository.findByCaptain(any(User.class))).thenReturn(Optional.of(team));
        when(tournamentRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        entity.setStatus(TournamentStatus.IN_PROGRESS);
        entity.setStartingDateTime(LocalDateTime.now().plusYears(1));
        entity.setMaxTeam(32);
        entity.setMinElo(0);
        entity.setMaxElo(3000);
        entity.setWomenOnly(false);
        team.setTeamElo(1200);

        Exception exception = assertThrows(IllegalArgumentException.class, ()-> tournamentService.register(1L, authentication));

        String expectedMessage = "conditions d'inscription non respectée";

        assertEquals(expectedMessage,exception.getMessage());
    }

    @Test
    void register_when_ko_EndingRegistrationIsOver(){
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(teamRepository.findByCaptain(any(User.class))).thenReturn(Optional.of(team));
        when(tournamentRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        entity.setStatus(TournamentStatus.REGISTRATION);
        entity.setStartingDateTime(LocalDateTime.now().minusYears(1));
        entity.setMaxTeam(32);
        entity.setMinElo(0);
        entity.setMaxElo(3000);
        entity.setWomenOnly(false);
        team.setTeamElo(1200);

        Exception exception = assertThrows(IllegalArgumentException.class, ()-> tournamentService.register(1L, authentication));

        String expectedMessage = "conditions d'inscription non respectée";

        assertEquals(expectedMessage,exception.getMessage());
    }

    @Test
    void register_when_ko_maxTeamIsReached(){
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(teamRepository.findByCaptain(any(User.class))).thenReturn(Optional.of(team));
        when(tournamentRepository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(tournamentTeamRepository.countAllByTournament(entity)).thenReturn(32);

        entity.setStatus(TournamentStatus.REGISTRATION);
        entity.setStartingDateTime(LocalDateTime.now().plusYears(1));
        entity.setMaxTeam(32);
        entity.setMinElo(0);
        entity.setMaxElo(3000);
        entity.setWomenOnly(false);
        team.setTeamElo(1200);

        Exception exception = assertThrows(IllegalArgumentException.class, ()-> tournamentService.register(1L, authentication));

        String expectedMessage = "conditions d'inscription non respectée";

        assertEquals(expectedMessage,exception.getMessage());
    }

    @Test
    void register_when_ko_teamEloTooLow(){
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(teamRepository.findByCaptain(any(User.class))).thenReturn(Optional.of(team));
        when(tournamentRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        entity.setStatus(TournamentStatus.IN_PROGRESS);
        entity.setStartingDateTime(LocalDateTime.now().plusYears(1));
        entity.setMaxTeam(32);
        entity.setMinElo(2000);
        entity.setMaxElo(3000);
        entity.setWomenOnly(false);
        team.setTeamElo(1200);

        Exception exception = assertThrows(IllegalArgumentException.class, ()-> tournamentService.register(1L, authentication));

        String expectedMessage = "conditions d'inscription non respectée";

        assertEquals(expectedMessage,exception.getMessage());
    }

    @Test
    void register_when_ko_teamEloTooHigh(){
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(teamRepository.findByCaptain(any(User.class))).thenReturn(Optional.of(team));
        when(tournamentRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        entity.setStatus(TournamentStatus.IN_PROGRESS);
        entity.setStartingDateTime(LocalDateTime.now().plusYears(1));
        entity.setMaxTeam(32);
        entity.setMinElo(0);
        entity.setMaxElo(1000);
        entity.setWomenOnly(false);
        team.setTeamElo(1200);

        Exception exception = assertThrows(IllegalArgumentException.class, ()-> tournamentService.register(1L, authentication));

        String expectedMessage = "conditions d'inscription non respectée";

        assertEquals(expectedMessage,exception.getMessage());
    }

    @Test
    void register_when_ko_NotWomenOnlyTeamIntoWomenOnlyTournament(){
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(teamRepository.findByCaptain(any(User.class))).thenReturn(Optional.of(team));
        when(tournamentRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        entity.setStatus(TournamentStatus.IN_PROGRESS);
        entity.setStartingDateTime(LocalDateTime.now().plusYears(1));
        entity.setMaxTeam(32);
        entity.setMinElo(0);
        entity.setMaxElo(3000);
        entity.setWomenOnly(true);
        team.setTeamElo(1200);
        team.setAllWomen(false);

        Exception exception = assertThrows(IllegalArgumentException.class, ()-> tournamentService.register(1L, authentication));

        String expectedMessage = "conditions d'inscription non respectée";

        assertEquals(expectedMessage,exception.getMessage());
    }
}