package be.yapock.overwatchtournamentmanager.dal.models;

import be.yapock.overwatchtournamentmanager.dal.models.enums.TournamentCategories;
import be.yapock.overwatchtournamentmanager.dal.models.enums.TournamentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder @NoArgsConstructor @AllArgsConstructor
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private long id;
    @Getter @Setter
    @Column(nullable = false)
    private String name;
    @Getter @Setter
    @Column(nullable = false)
    private int minTeam;
    @Getter @Setter
    @Column(nullable = false)
    private int maxTeam;
    @Getter @Setter
    private int minElo;
    @Getter @Setter
    private int maxElo;
    @Getter @Setter
    private List<TournamentCategories> categories;
    @Getter @Setter
    private TournamentStatus status;
    @Setter @Getter
    private int round;
    @Setter @Getter
    private boolean isWomenOnly;
    @Getter @Setter
    private LocalDateTime startingDateTime;
    @Getter @Setter
    private LocalDate creationDate;
    @Getter @Setter
    private LocalDate updateDate;
}
