package ru.vlad2509.minionflow.application.auth;

import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import ru.vlad2509.minionflow.application.dto.DecodedRefreshToken;
import ru.vlad2509.minionflow.application.dto.UserInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class TokenService {

    @ConfigProperty(name = "identity-service.jwt-issuer", defaultValue = "https://quarkus.io/issuer")
    String jwtIssuer;

    @Inject
    JWTParser jwtParser;

    public static String ACCESS_TYPE_JWT = "acs";
    public static String REFRESH_TYPE_JWT = "ref";


    public String createAccessToken(UserInfo info, Set<String> groups, int ttlSeconds) {
        return Jwt.issuer(jwtIssuer)
                .subject(info.userId().toString())
                .groups(groups)
                .claim("typ", ACCESS_TYPE_JWT)
                .claim("una", info.username())
                .claim("ema", info.email())
                .expiresAt(Instant.now().plusSeconds(ttlSeconds))
                .sign();
    }

    public String creteRefreshToken(UserInfo info, UUID sessionId, UUID jwtId, int ttlSeconds) {
        return Jwt.issuer(jwtIssuer)
                .subject(info.userId().toString())
                .claim("typ", REFRESH_TYPE_JWT)
                .claim("sid", sessionId.toString())
                .claim("jid", jwtId.toString())
                .expiresAt(Instant.now().plusSeconds(ttlSeconds))
                .sign();
    }

    public DecodedRefreshToken verifyRefreshToken(String refreshToken) {
        try {
            JsonWebToken token = jwtParser.parse(refreshToken);
            System.out.println((String)token.getClaim("typ"));
            if (!token.getClaim("typ").equals(REFRESH_TYPE_JWT))
                return null;

            UUID userId = UUID.fromString(token.getSubject());
            UUID sessionId = UUID.fromString(token.getClaim("sid"));
            UUID jwtId = UUID.fromString(token.getClaim("jid"));

            return new DecodedRefreshToken(userId, sessionId, jwtId);
        } catch (ParseException | IllegalArgumentException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
