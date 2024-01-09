package be.yapock.overwatchtournamentmanager.pl.models.user;

public record UserSearchForm(
        String username,
        String email,
        String ranking,
        String inGameRole,
        String battleNet
) {
}
