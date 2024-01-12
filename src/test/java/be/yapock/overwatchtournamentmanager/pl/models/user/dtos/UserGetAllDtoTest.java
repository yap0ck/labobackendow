package be.yapock.overwatchtournamentmanager.pl.models.user.dtos;

import be.yapock.overwatchtournamentmanager.dal.models.User;
import be.yapock.overwatchtournamentmanager.dal.models.enums.InGameRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserGetAllDtoTest {

    @Test
    void fromEntity() {
        User user = mock(User.class);

        when(user.getId()).thenReturn(1L);
        when(user.getRanking()).thenReturn("Grand Master");
        when(user.getUsername()).thenReturn("yapock");
        when(user.getInGameRoles()).thenReturn(Collections.singletonList(InGameRole.SUPPORT));

        UserGetAllDto resultat = UserGetAllDto.fromEntity(user);

        assertNotNull(resultat);
        assertEquals(1L, resultat.id());
        assertEquals("yapock", resultat.username());
        assertEquals("Grand Master", resultat.ranking());
        assertEquals(Collections.singletonList(InGameRole.SUPPORT), resultat.inGameRoles());

        verify(user, times(1)).getId();
        verify(user, times(1)).getRanking();
        verify(user,times(1)).getUsername();
        verify(user, times(1)).getInGameRoles();
    }
}