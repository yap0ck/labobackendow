package be.yapock.overwatchtournamentmanager.dal.models;

import be.yapock.overwatchtournamentmanager.dal.models.enums.InGameRole;
import be.yapock.overwatchtournamentmanager.dal.models.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "User_", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username", "email", "battle_Net"})
})
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private long id;
    @Column(length = 255, nullable = false)
    @Setter
    private String username;
    @Column(length = 255, nullable = false)
    @Getter @Setter
    private String email;
    @Column(length = 255, nullable = false)
    @Getter @Setter
    private String battleNet;
    @Column(length = 255,nullable = false)
    @Setter
    private String password;
    @Column(nullable = false)
    @Getter @Setter
    private LocalDate dateOfBirth;
    @Column(length = 50,nullable = false)
    @Getter @Setter
    private String gender;
    @Column(length = 15, nullable = false)
    //TODO faire une enum
    @Getter @Setter
    private String ranking;
    @Column(nullable = false)
    @Getter @Setter
    private List<UserRole> userRoles;
    @Getter @Setter
    private List<InGameRole> inGameRoles;
    @Setter
    private boolean isEnabled = true;
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;
    //TODO modifier role et Ranking pour en faire une entité a part chaque utilisateur aura donc song rang par rapport à son role

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_"+role))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
