package be.yapock.overwatchtournamentmanager.bll.tournament;

import be.yapock.overwatchtournamentmanager.dal.models.Team;
import be.yapock.overwatchtournamentmanager.dal.models.Tournament;
import be.yapock.overwatchtournamentmanager.dal.models.User;
import be.yapock.overwatchtournamentmanager.dal.models.enums.TournamentCategories;
import be.yapock.overwatchtournamentmanager.dal.models.enums.TournamentStatus;
import be.yapock.overwatchtournamentmanager.dal.repositories.TeamRepository;
import be.yapock.overwatchtournamentmanager.dal.repositories.TournamentRepository;
import be.yapock.overwatchtournamentmanager.dal.repositories.TournamentTeamRepository;
import be.yapock.overwatchtournamentmanager.dal.repositories.UserRepository;
import be.yapock.overwatchtournamentmanager.pl.models.tournament.forms.TournamentForm;

import be.yapock.overwatchtournamentmanager.pl.models.tournament.forms.TournamentSearchForm;
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
}