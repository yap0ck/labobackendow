package be.yapock.overwatchtournamentmanager.bll.tournament;

import be.yapock.overwatchtournamentmanager.dal.models.Tournament;
import be.yapock.overwatchtournamentmanager.dal.models.enums.TournamentCategories;
import be.yapock.overwatchtournamentmanager.dal.models.enums.TournamentStatus;
import be.yapock.overwatchtournamentmanager.dal.repositories.TeamRepository;
import be.yapock.overwatchtournamentmanager.dal.repositories.TournamentRepository;
import be.yapock.overwatchtournamentmanager.pl.models.tournament.forms.TournamentForm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentServiceImplTest {
    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private TeamRepository teamRepository;
    @InjectMocks
    private TournamentServiceImpl tournamentService;
    private Tournament entity;
    private TournamentForm form;

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
}