package be.yapock.overwatchtournamentmanager.bll.user;

import be.yapock.overwatchtournamentmanager.dal.models.User;
import be.yapock.overwatchtournamentmanager.pl.models.user.dtos.AuthDTO;
import be.yapock.overwatchtournamentmanager.pl.models.user.forms.LoginForm;
import be.yapock.overwatchtournamentmanager.pl.models.user.forms.UserForm;
import be.yapock.overwatchtournamentmanager.pl.models.user.forms.UserRoleUpdateForm;
import be.yapock.overwatchtournamentmanager.pl.models.user.forms.UserSearchForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface UserService {
    void register(UserForm form);
    AuthDTO login(LoginForm form);
    User getOne(long id);
    Page<User> getAll(Pageable pageable);
    void update(long id, UserForm form, Authentication authentication) throws IllegalAccessException;
    void delete(long id, Authentication authentication);
    Page<User> getAllBySpec(Pageable pageable, UserSearchForm form);
    void updateUserRole(long id, UserRoleUpdateForm form);
}
