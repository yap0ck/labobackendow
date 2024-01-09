package be.yapock.overwatchtournamentmanager.pl.config.security;

import be.yapock.overwatchtournamentmanager.dal.models.User;
import be.yapock.overwatchtournamentmanager.dal.models.enums.UserRole;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.net.http.HttpRequest;
import java.time.Instant;
import java.util.List;

@Component
public class JWTProvider {
    private static final String JWT_SECRET = "p3/}pQkQBB83c{.J2K=d3gA@6Q^jHzjH6%92+6()Wx[Aeq93Q!2t";
    private static final long EXPIRES_AT = 900_000;
    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer";
    private final UserDetailsService userDetailsService;
    public JWTProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Génere un token depuis le login avec une expiration
     * @param login
     * @return token
     */
    public String generateToken(String login, List<UserRole> roles){
        return TOKEN_PREFIX+ JWT.create()
                .withSubject(login)
                .withClaim("roles", roles.stream()
                        .map(Enum::toString)
                        .toList())
                .withExpiresAt(Instant.now().plusMillis(EXPIRES_AT))
                .sign(Algorithm.HMAC512(JWT_SECRET));
    }

    /**
     * Extrait un token d'une requete HTTP
     * @param request requete HTTP
     * @return token en String
     */
    public String extractToken(HttpServletRequest request){
        String header = request.getHeader(AUTH_HEADER);

        if (header== null || !header.startsWith(TOKEN_PREFIX)) return null;

        return header.replaceFirst(TOKEN_PREFIX,"");
    }

    /**
     * vérifie si un token est valide
     * @param token
     * @return true si le token est valide
     */
    public boolean validateToken(String token){
        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC512(JWT_SECRET))
                    .acceptExpiresAt(EXPIRES_AT)
                    .withClaimPresence("sub")
                    .withClaimPresence("roles")
                    .build().verify(token);

            String username = jwt.getSubject();
            User user = (User) userDetailsService.loadUserByUsername(username);
            if (!user.isEnabled()) return false;

            List<UserRole> tokenRoles = jwt.getClaim("roles").asList(UserRole.class);

            return user.getUserRoles().containsAll(tokenRoles);
        } catch (JWTVerificationException | UsernameNotFoundException exception){
            return false;
        }
    }

    /**
     * crée une authentication
     * @param token
     * @return Authentication
     */
    public Authentication createAuthentication(String token){
        DecodedJWT jwt = JWT.decode(token);
        String username = jwt.getSubject();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                null,
                userDetails.getAuthorities()
        );
    }
}
