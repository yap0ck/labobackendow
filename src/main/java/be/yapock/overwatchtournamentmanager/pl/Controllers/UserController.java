package be.yapock.overwatchtournamentmanager.pl.Controllers;

import be.yapock.overwatchtournamentmanager.bll.user.UserService;
import be.yapock.overwatchtournamentmanager.pl.models.user.dtos.UserGetAllDto;
import be.yapock.overwatchtournamentmanager.pl.models.user.dtos.UserGetOneDTO;
import be.yapock.overwatchtournamentmanager.pl.models.user.forms.LoginForm;
import be.yapock.overwatchtournamentmanager.pl.models.user.forms.UserForm;
import be.yapock.overwatchtournamentmanager.pl.models.user.forms.UserRoleUpdateForm;
import be.yapock.overwatchtournamentmanager.pl.models.user.forms.UserSearchForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * met a jour les données de l'utilisateur
     * @param id
     * @param form
     * @param authentication
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public void update(@PathVariable long id, @RequestBody UserForm form, Authentication authentication) throws IllegalAccessException {
        userService.update(id,form,authentication);
    }

    /**
     * supprime un compte utilisateur
     * @param id
     * @param authentication
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id, Authentication authentication){
        userService.delete(id,authentication);
    }

    /**
     * recherche par spécification
     * @param form
     * @param pageable
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/search")
    public ResponseEntity<List<UserGetAllDto>> getallbyspec(@RequestBody UserSearchForm form, Pageable pageable){
        return ResponseEntity.ok(userService.getAllBySpec(pageable, form).stream()
                .map(UserGetAllDto::fromEntity)
                .toList());
    }

    /**
     * permet à un admin de changer le role d'un utilisateur
     * @param id
     * @param form
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/updaterole/{id}")
    public void updateRole(@PathVariable long id, @RequestBody UserRoleUpdateForm form){
        userService.updateUserRole(id,form);
    }
}
