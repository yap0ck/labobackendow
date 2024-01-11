package be.yapock.overwatchtournamentmanager.bll.user;

import be.yapock.overwatchtournamentmanager.bll.mailing.EmailService;
import be.yapock.overwatchtournamentmanager.dal.models.User;
import be.yapock.overwatchtournamentmanager.dal.models.enums.UserRole;
import be.yapock.overwatchtournamentmanager.dal.repositories.UserRepository;
import be.yapock.overwatchtournamentmanager.pl.config.security.JWTProvider;
import be.yapock.overwatchtournamentmanager.pl.models.user.dtos.AuthDTO;
import be.yapock.overwatchtournamentmanager.pl.models.user.forms.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTProvider jwtProvider;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTProvider jwtProvider, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.emailService = emailService;
    }


    /**
     * crée et enregistre un utilisateur
     * @param form formulaire d'enregistrement d'utilisateur
     */
    @Override
    public void register(UserForm form) {
        if (form==null) throw new IllegalArgumentException("le formulaire ne peut etre vide");
        if (!form.password().equals(form.confirmedPassword())) throw new IllegalArgumentException("les mots de passe doivent etre identique");
        User user = User.builder()
                .email(form.email())
                .userRoles(List.of(UserRole.PLAYER))
                .battleNet(form.battleNet())
                .dateOfBirth(form.dateOfBirth())
                .ranking(form.ranking())
                .gender(form.gender())
                .inGameRoles(form.inGameRoles())
                .username(form.username())
                .password(passwordEncoder.encode(form.password()))
                .build();
        userRepository.save(user);
    }

    /**
     * crée une Authentication
     * @param form Loginform demandant nom d'utilisateur et mot de passe
     * @return une Authentication
     */
    @Override
    public AuthDTO login(LoginForm form) {
        if (form== null) throw new IllegalArgumentException("le formulaire ne peut etre vide");
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(form.username(),form.password()));
        User user = userRepository.findByUsername(form.username()).orElseThrow(()-> new UsernameNotFoundException("utilisateur non trouvé"));
        String token = jwtProvider.generateToken(user.getUsername(), List.copyOf(user.getUserRoles()));
        return new AuthDTO(token, user.getUsername(),user.getUserRoles());
    }

    /**
     * recherche un utilisateur spécifique
     * @param id de l'utilisateur recherché
     * @return User à affiché
     */
    @Override
    public User getOne(long id) {
        return userRepository.findById(id).orElseThrow(()->new EntityNotFoundException("utilisateur pas trouvé"));
    }

    /**
     * affiche tout les utilisateur sous forme de pagineation
     * @param pageable parametres de la pagination
     * @return Page de User
     */
    @Override
    public Page<User> getAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * met a jour les donnée de l'utilisateur, seul l'utilisateur
     * @param id
     * @param form
     * @param authentication
     * @throws IllegalAccessException
     */
    @Override
    public void update(long id, UserForm form, Authentication authentication) throws IllegalAccessException {
        if (!form.password().equals(form.confirmedPassword())) throw new IllegalArgumentException("les mots de passe doivent etre identique");
        User userConnected = userRepository.findByUsername(authentication.getName()).orElseThrow(()->new UsernameNotFoundException("utilisateur non trouvé"));
        User user = getOne(id);
        if (!user.equals(userConnected)) throw new IllegalAccessException("accès non authorisé");
        user.setDateOfBirth(form.dateOfBirth());
        user.setGender(form.gender());
        user.setRanking(form.ranking());
        user.setBattleNet(form.battleNet());
        user.setEmail(form.email());
        user.setPassword(passwordEncoder.encode(form.password()));
        user.setUsername(form.username());
        user.setInGameRoles(form.inGameRoles());
        userRepository.save(user);
    }

    /**
     * met a jour l'utilisateur en lui affectant un statut enable false lors de la suppression du compte. seul un admin ou l'utilisateur lui meme peut supprimer le compte
     * @param id
     * @param authentication
     */
    @SneakyThrows
    @Override
    public void delete(long id, Authentication authentication) {
        User userConnected = userRepository.findByUsername(authentication.getName()).orElseThrow(()->new UsernameNotFoundException("utilisateur non trouvé"));
        User user = getOne(id);
        if (!user.equals(userConnected) || !userConnected.getUserRoles().contains(UserRole.ADMIN)) throw new IllegalAccessException("accés non authorisé");
        user.setEnabled(false);
        userRepository.save(user);
    }

    /**
     * effectue une recherche par spécifications définies dans createSpecification
     * @param pageable
     * @param form
     * @return list d'utilisateur
     */
    @Override
    public Page<User> getAllBySpec(Pageable pageable, UserSearchForm form) {
        Specification<User> spec = createSpecification(form);
        return userRepository.findAll(spec,pageable);
    }

    /**
     * mise en place des spécification pour la recherche par spec, les champs sont username, ranking, email, battleNet, et role en jeu
     * @param form
     * @return les predicates
     */
    private Specification<User> createSpecification(UserSearchForm form){
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (form.username()!=null) predicates.add(criteriaBuilder.like(root.get("username"), "%"+form.username()+"%"));
            if (form.ranking()!=null) predicates.add(criteriaBuilder.like(root.get("ranking"), "%"+ form.ranking()+ "%"));
            if (form.email()!=null) predicates.add(criteriaBuilder.like(root.get("email"), "%"+ form.email()+"%"));
            if (form.battleNet()!=null) predicates.add(criteriaBuilder.like(root.get("battle_net"),"%"+form.battleNet()+"%"));
            if (form.inGameRole()!=null) predicates.add(criteriaBuilder.equal(root.get("in_game_roles"), form.inGameRole()));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    /**
     * Methode permettant a un admin de modifié le role d'un utilisateur
     * @param id
     * @param form
     */

    @Override
    public void updateUserRole(long id, UserRoleUpdateForm form) {
        User user = getOne(id);
        user.setUserRoles(form.roles());
        userRepository.save(user);
    }

    /**
     * Change le mot de passe de l'utilisateur par un mot de passe aléatoire avant de lui envoyer par mail
     * @param id
     */
    @SneakyThrows
    @Override
    public void resetPasswordRequest(long id) {
        User user = getOne(id);
        user.setPassword(generatePassword());
        userRepository.save(user);
        emailService.sendPasswordResetRequest(user);
    }

    private static String generatePassword(){
        SecureRandom random = new SecureRandom();
        return random.ints(48,122 +1)
                .filter(i -> Character.isAlphabetic(i)|| Character.isDigit(i))
                .limit(10)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
