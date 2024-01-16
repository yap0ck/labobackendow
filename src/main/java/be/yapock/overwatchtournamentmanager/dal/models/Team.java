package be.yapock.overwatchtournamentmanager.dal.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor @NoArgsConstructor
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private long id;
    @Column(nullable = false)
    @Getter @Setter
    private LocalDate creationDate;
    @Column(nullable = false, length = 50)
    @Getter @Setter
    private String teamName;
    @Column(nullable = false)
    @Getter @Setter
    private int teamElo;
    @OneToOne
    @JoinColumn(name = "captain_id")
    @Getter @Setter
    private User captain;
    @OneToMany(mappedBy = "team")
    @Getter @Setter
    private List<User> playerList;
    @Getter @Setter
    @Column(name = "is_all_women")
    private boolean isAllWomen;

}
