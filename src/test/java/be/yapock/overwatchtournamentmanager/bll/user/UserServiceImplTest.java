package be.yapock.overwatchtournamentmanager.bll.user;

import be.yapock.overwatchtournamentmanager.bll.mailing.EmailService;
import be.yapock.overwatchtournamentmanager.dal.models.Team;
import be.yapock.overwatchtournamentmanager.dal.models.User;
import be.yapock.overwatchtournamentmanager.dal.models.enums.InGameRole;
import be.yapock.overwatchtournamentmanager.dal.models.enums.UserRole;
import be.yapock.overwatchtournamentmanager.dal.repositories.UserRepository;
import be.yapock.overwatchtournamentmanager.pl.config.security.JWTProvider;
import be.yapock.overwatchtournamentmanager.pl.models.user.dtos.AuthDTO;
import be.yapock.overwatchtournamentmanager.pl.models.user.dtos.UserGetAllDto;
import be.yapock.overwatchtournamentmanager.pl.models.user.dtos.UserGetOneDTO;
import be.yapock.overwatchtournamentmanager.pl.models.user.forms.LoginForm;
import be.yapock.overwatchtournamentmanager.pl.models.user.forms.UserForm;
import be.yapock.overwatchtournamentmanager.pl.models.user.forms.UserRoleUpdateForm;
import be.yapock.overwatchtournamentmanager.pl.models.user.forms.UserSearchForm;
import jakarta.persistence.EntityNotFoundException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JWTProvider jwtProvider;
    @Mock
    private EmailService emailService;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;
    private UserGetOneDTO oneDTO;
    private UserGetAllDto allDto;
    private UserForm userForm;
    @Mock
    private Authentication authentication;
    private User userConnected;

    @BeforeEach
    void setUp(){
        Team team= Team.builder().build();
        user = new User(1L, "username", "mail@exemple.com", "battlenet", "password", LocalDate.now(), 'M', "rank",Collections.singletonList(UserRole.PLAYER), Collections.singletonList(InGameRole.SUPPORT),true,team);
        oneDTO = new UserGetOneDTO(user.getId(), user.getUsername(), user.getRanking(), user.getDateOfBirth(), user.getGender(), user.getInGameRoles(), user.getUserRoles());
        allDto = new UserGetAllDto(user.getId(), user.getUsername(),user.getInGameRoles(), user.getRanking());
        userForm = new UserForm(user.getUsername(),user.getEmail(), user.getBattleNet(), user.getPassword(), user.getPassword(), user.getDateOfBirth(), user.getGender(), user.getRanking(), user.getInGameRoles());
        userConnected = User.builder()
                .isEnabled(true)
                .build();
    }

    @Test
    void register_when_ok() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.register(userForm);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_when_form_null(){
        Exception exception = assertThrows(IllegalArgumentException.class, ()->userService.register(null));

        String expectedMessage = "le formulaire ne peut etre vide";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void register_when_password_not_equals(){
        UserForm userForm1 = new UserForm(user.getUsername(),user.getEmail(), user.getBattleNet(), user.getPassword(), "wrong", user.getDateOfBirth(), user.getGender(), user.getRanking(), user.getInGameRoles());
        Exception exception = assertThrows(IllegalArgumentException.class, ()->userService.register(userForm1));

        String expectedMessage = "les mots de passe doivent etre identique";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    void login_when_ok() {
        LoginForm form = new LoginForm(user.getUsername(), user.getPassword());

        when(userRepository.findByUsername(form.username())).thenReturn(Optional.of(user));
        when(jwtProvider.generateToken(any(),any())).thenReturn("token");

        AuthDTO authDTO = userService.login(form);

        assertEquals(user.getUsername(), authDTO.username());
        assertEquals("token", authDTO.token());
        assertEquals(user.getUserRoles(), authDTO.userRoles());
    }

    @Test
    void login_when_form_null(){
        Exception exception = assertThrows(IllegalArgumentException.class, ()-> userService.login(null));

        String expectedMessage = "le formulaire ne peut etre vide";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    void login_when_userNotfound(){
        LoginForm form = new LoginForm(user.getUsername(), user.getPassword());

        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        Exception exception = assertThrows(UsernameNotFoundException.class, () -> userService.login(form));

        String expectedMessage = "utilisateur non trouvé";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    void getOne() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        User searchUser = userService.getOne(1L);

        assertNotNull(searchUser);
        assertEquals(user, searchUser);
    }

    @Test
    void getOne_when_NotFound(){
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class, () -> userService.getOne(1L));

        String expectedMessage = "utilisateur pas trouvé";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    void getAll() {
        Pageable pageable = mock(Pageable.class);
        Page entities = mock(Page.class);

        when(userRepository.findAll(pageable)).thenReturn(entities);

        Page<User> result = userService.getAll(pageable);

        assertEquals(entities,result);

    }

    @SneakyThrows
    @Test
    void update_when_ok() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        userService.update(1L, userForm, authentication);

        verify(userRepository,times(1)).save(any(User.class));
    }

    @Test
    void update_when_form_null(){
        Exception exception = assertThrows(IllegalArgumentException.class, ()-> userService.update(1L,null,authentication));

        String expectedMessage = "les mots de passe doivent etre identique ou le formulaire ne peut etre null";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    void update_when_password_different(){
        UserForm userForm1 = new UserForm(user.getUsername(),user.getEmail(), user.getBattleNet(), user.getPassword(), "wrong", user.getDateOfBirth(), user.getGender(), user.getRanking(), user.getInGameRoles());
        Exception exception = assertThrows(IllegalArgumentException.class, ()->userService.update(1L,userForm1,authentication));

        String expectedMessage = "les mots de passe doivent etre identique ou le formulaire ne peut etre null";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    void update_when_unauthorized(){


        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userConnected));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(IllegalAccessException.class, ()->userService.update(1L,userForm,authentication));

        String expectedMessage = "accès non authorisé";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    void delete() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        userService.delete(1L,authentication);

        assertFalse(user.isEnabled());
    }

    @Test
    void delete_when_unauthorized_role_or_id(){
        userConnected.setUserRoles(Collections.singletonList(UserRole.PLAYER));

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userConnected));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(IllegalAccessException.class, ()-> userService.delete(1L,authentication));

        String expectedMessage = "accés non authorisé";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    void delete_when_authorized_id(){


        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.delete(1L, authentication);

        assertFalse(user.isEnabled());
    }

    @Test
    void delete_when_authorized_role(){
        userConnected.setUserRoles(Collections.singletonList(UserRole.ADMIN));

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userConnected));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.delete(1L,authentication);

        assertFalse(user.isEnabled());
    }

    @Test
    void getAllBySpec() {
        UserSearchForm userSearchForm = mock(UserSearchForm.class);
        Pageable pageable = mock(Pageable.class);
        List<User> users = Arrays.asList(user,user,user);
        Page<User> entities = new PageImpl<>(users, pageable, users.size());


        when(userRepository.findAll(any(Specification.class),eq(pageable))).thenReturn(entities);

        Page<User> result = userService.getAllBySpec(pageable,userSearchForm);

        verify(userRepository, times(1)).findAll(any(Specification.class),eq(pageable));
    }

    @Test
    void updateUserRole() {
        UserRoleUpdateForm userRoleUpdateForm = mock(UserRoleUpdateForm.class);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.updateUserRole(1L, userRoleUpdateForm);

        verify(userRepository,times(1)).save(any(User.class));
    }

    @Test
    void updateUserRole_when_form_null(){
        Exception exception = assertThrows(IllegalArgumentException.class,()-> userService.updateUserRole(1L,null));

        String expectedMessage = "formulaire ne peut etre vide";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage,actualMessage);
    }

    @SneakyThrows
    @Test
    void resetPasswordRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.resetPasswordRequest(1L);

        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendPasswordResetRequest(user);
    }
}