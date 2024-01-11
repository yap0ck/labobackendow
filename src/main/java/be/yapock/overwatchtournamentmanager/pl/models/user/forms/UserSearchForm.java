package be.yapock.overwatchtournamentmanager.pl.models.user.forms;

public record UserSearchForm(
        String username,
        String email,
        String ranking,
        String inGameRole,
        String battleNet
) {
}
