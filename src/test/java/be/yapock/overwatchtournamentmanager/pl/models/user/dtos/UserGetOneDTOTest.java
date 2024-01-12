package be.yapock.overwatchtournamentmanager.pl.models.user.dtos;

import be.yapock.overwatchtournamentmanager.dal.models.User;
import be.yapock.overwatchtournamentmanager.dal.models.enums.InGameRole;
import be.yapock.overwatchtournamentmanager.dal.models.enums.UserRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserGetOneDTOTest {

    @Test
    void fromEntity() {
        User entity = mock(User.class);

        when(entity.getId()).thenReturn(1L);
        when(entity.getUsername()).thenReturn("username");
        when(entity.getRanking()).thenReturn("rank");
        when(entity.getDateOfBirth()).thenReturn(LocalDate.now());
        when(entity.getGender()).thenReturn('M');
        when(entity.getInGameRoles()).thenReturn(Collections.singletonList(InGameRole.SUPPORT));
        when(entity.getUserRoles()).thenReturn(Collections.singletonList(UserRole.PLAYER));

        UserGetOneDTO result = UserGetOneDTO.fromEntity(entity);

        assertNotNull(result);
        assertEquals(1L,result.id());
        assertEquals("username", result.username());
        assertEquals("rank", result.ranking());
        assertEquals(LocalDate.now(), result.dateOfBirth());
        assertEquals('M', result.gender());
        assertEquals(Collections.singletonList(InGameRole.SUPPORT), result.inGameRoles());
        assertEquals(Collections.singletonList(UserRole.PLAYER), result.userRoles());

        verify(entity, times(1)).getId();
        verify(entity, times(1)).getRanking();
        verify(entity,times(1)).getUsername();
        verify(entity, times(1)).getInGameRoles();
        verify(entity, times(1)).getDateOfBirth();
        verify(entity, times(1)).getGender();
        verify(entity, times(1)).getUserRoles();
    }
}