package ru.vlad2509.minionflow.application.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.jwt.JsonWebToken;
import ru.vlad2509.minionflow.application.context.UserContext;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.domain.model.ProjectPermission;

import java.util.UUID;

@ApplicationScoped
public class TokenService {

    public static String ACCESS_TYPE_JWT = "acs";
    public static String REFRESH_TYPE_JWT = "ref";

    @Inject
    //MemberRepository memberRepository;

    public UserContext parseJwt(JsonWebToken jwt) {
        try {
            UUID userId = UUID.fromString(jwt.getSubject());
            String type = jwt.getClaim("typ");
            String username = jwt.getClaim("una");
            String email = jwt.getClaim("ema");
            if (!type.equals(ACCESS_TYPE_JWT))
                throw new ApiException(ApiError.UNAUTHORIZED, "wrong jwt type");
            if (username == null || email == null)
                throw new ApiException(ApiError.UNAUTHORIZED, "missing username or email");
            return new UserContext(userId, username, email);
        } catch (IllegalArgumentException e) {
            throw new ApiException(ApiError.UNAUTHORIZED, "userId parse error");
        }
    }

    // TODO: репликация members из project-service сюда, авторизация юзера
    @Transactional
    public void authorize(UserContext userContext, UUID projectId, ProjectPermission... permissions) {
//        MemberRole role = memberRepository.findByProjectUserId(projectId, userContext.userId())
//                .orElseThrow(() -> new ApiException(ApiError.PROJECT_NOT_FOUND, "user is not a member of the project or it does not exist")).role;
//        if (!Arrays.stream(permissions).allMatch(permission -> role.getPermissions().contains(permission)))
//            throw new ApiException(ApiError.INSUFFICIENT_PERMISSION, "not enough permissions");
    }

}
