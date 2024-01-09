package be.yapock.overwatchtournamentmanager.bll.user;

import be.yapock.overwatchtournamentmanager.pl.models.user.AuthDTO;
import be.yapock.overwatchtournamentmanager.pl.models.user.LoginForm;
import be.yapock.overwatchtournamentmanager.pl.models.user.UserForm;

public interface UserService {
    void register(UserForm form);
    AuthDTO login(LoginForm form);
}
