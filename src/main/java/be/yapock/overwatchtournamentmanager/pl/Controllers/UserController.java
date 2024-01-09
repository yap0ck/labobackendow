package be.yapock.overwatchtournamentmanager.pl.Controllers;

import be.yapock.overwatchtournamentmanager.bll.user.UserService;
import be.yapock.overwatchtournamentmanager.pl.models.user.LoginForm;
import be.yapock.overwatchtournamentmanager.pl.models.user.UserForm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * endpoint permettant l'enregistrement d'un utilisateur
     * @param form
     */
    @PreAuthorize("isAnonymous()")
    @PostMapping
    public void register(@RequestBody UserForm form){
        userService.register(form);
    }

    /**
     * endpoint permettant la connexion
     * @param form
     */
    @PreAuthorize("isAnonymous()")
    @PostMapping("/login")
    public void login(@RequestBody LoginForm form){
        userService.login(form);
    }
}
