package be.yapock.overwatchtournamentmanager.pl.Controllers;

import be.yapock.overwatchtournamentmanager.bll.user.UserService;
import be.yapock.overwatchtournamentmanager.pl.models.user.LoginForm;
import be.yapock.overwatchtournamentmanager.pl.models.user.UserForm;
import be.yapock.overwatchtournamentmanager.pl.models.user.UserGetAllDto;
import be.yapock.overwatchtournamentmanager.pl.models.user.UserGetOneDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    /**
     * endpoint envoyant les détails public d'un utilisateur. seul les utilisateurs connecté y ont accès
     * @param id de l'utilisateur à afficher
     * @return une response entity contenant le dto de l'utilisateur
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<UserGetOneDTO> getOne(@PathVariable long id){
        return ResponseEntity.ok(UserGetOneDTO.fromEntity(userService.getOne(id)));
    }

    /**
     * Endpoint envoyant une page d'utilisateur, seul les utilisateur connecté y ont accès
     * @param pageable
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Page<UserGetAllDto>> getAll(Pageable pageable){
        return ResponseEntity.ok(userService.getAll(pageable).map(UserGetAllDto::fromEntity));
    }
}
