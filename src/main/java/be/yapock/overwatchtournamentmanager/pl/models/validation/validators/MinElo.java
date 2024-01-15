package be.yapock.overwatchtournamentmanager.pl.models.validation.validators;

import be.yapock.overwatchtournamentmanager.pl.models.tournament.forms.TournamentForm;
import be.yapock.overwatchtournamentmanager.pl.models.validation.constraints.ValidTournament;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MinElo implements ConstraintValidator<ValidTournament, TournamentForm> {
    @Override
    public void initialize(ValidTournament constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(TournamentForm tournamentForm, ConstraintValidatorContext constraintValidatorContext) {
        if (tournamentForm.minElo()<= tournamentForm.maxElo()) return true;
        return false;
    }
}
