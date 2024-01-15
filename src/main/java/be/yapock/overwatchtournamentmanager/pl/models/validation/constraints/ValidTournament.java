package be.yapock.overwatchtournamentmanager.pl.models.validation.constraints;

import be.yapock.overwatchtournamentmanager.pl.models.validation.validators.MinElo;
import be.yapock.overwatchtournamentmanager.pl.models.validation.validators.MinPlayer;
import be.yapock.overwatchtournamentmanager.pl.models.validation.validators.StartingDate;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {MinPlayer.class, MinElo.class, StartingDate.class})
public @interface ValidTournament {
    String message() default "le nombre minimum de joueurs doit être plus petit ou égal au nombre maximum";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
