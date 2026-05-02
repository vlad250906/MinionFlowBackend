package ru.vlad2509.minionflow.application.util;

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
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class TokenService {

    @ConfigProperty(name = "identity-service.jwt-issuer", defaultValue = "https://quarkus.io/issuer")
    String jwtIssuer;

    @ConfigProperty(name = "identity-service.access-jwt-ttl", defaultValue = "1000")
    int accessTokenTtl;

    @ConfigProperty(name = "identity-service.refresh-jwt-ttl", defaultValue = "20000")
    int refreshTokenTtl;

    @Inject
    JWTParser jwtParser;

    public static String ACCESS_TYPE_JWT = "acs";
    public static String REFRESH_TYPE_JWT = "ref";


    public String createAccessToken(UserInfo info, Set<String> groups, Instant issuedAt) {
        return Jwt.issuer(jwtIssuer)
                .subject(info.userId().toString())
                .groups(groups)
                .claim("typ", ACCESS_TYPE_JWT)
                .claim("una", info.username())
                .claim("ema", info.email())
                .expiresAt(issuedAt.plusSeconds(accessTokenTtl))
                .sign();
    }

    public String creteRefreshToken(UserInfo info, UUID sessionId, UUID jwtId, Instant issuedAt) {
        return Jwt.issuer(jwtIssuer)
                .subject(info.userId().toString())
                .claim("typ", REFRESH_TYPE_JWT)
                .claim("sid", sessionId.toString())
                .claim("jid", jwtId.toString())
                .expiresAt(issuedAt.plusSeconds(refreshTokenTtl))
                .sign();
    }

    public DecodedRefreshToken verifyRefreshToken(String refreshToken) {
        if (refreshToken == null)
            return null;
        try {
            JsonWebToken token = jwtParser.parse(refreshToken);
            if (!token.getClaim("typ").equals(REFRESH_TYPE_JWT))
                return null;

            UUID userId = UUID.fromString(token.getSubject());
            UUID sessionId = UUID.fromString(token.getClaim("sid"));
            UUID jwtId = UUID.fromString(token.getClaim("jid"));

            return new DecodedRefreshToken(userId, sessionId, jwtId);
        } catch (ParseException | IllegalArgumentException ex) {
            return null;
        }
    }

    public int getAccessTokenTtl() {
        return accessTokenTtl;
    }

    public int getRefreshTokenTtl() {
        return refreshTokenTtl;
    }
}
