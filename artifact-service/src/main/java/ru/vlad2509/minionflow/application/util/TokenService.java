package ru.vlad2509.minionflow.application.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.jwt.JsonWebToken;
import ru.vlad2509.minionflow.application.context.UserContext;
import ru.vlad2509.minionflow.application.exception.ApiError;
import ru.vlad2509.minionflow.application.exception.ApiException;
import ru.vlad2509.minionflow.domain.model.enums.MemberRole;
import ru.vlad2509.minionflow.domain.model.enums.ProjectPermission;
import ru.vlad2509.minionflow.infrastructure.persistence.model.RemoteProjectMember;
import ru.vlad2509.minionflow.infrastructure.persistence.repository.RemoteProjectMemberRepository;

import java.util.Arrays;
import java.util.UUID;

@ApplicationScoped
public class TokenService {

    public static String ACCESS_TYPE_JWT = "acs";
    public static String REFRESH_TYPE_JWT = "ref";

    @Inject
    RemoteProjectMemberRepository remoteProjectMemberRepository;

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

    @Transactional
    public void authorize(UserContext userContext, UUID projectId, ProjectPermission... permissions) {
        MemberRole role = remoteProjectMemberRepository.findByProjectUserId(projectId, userContext.userId())
                .orElseThrow(() -> new ApiException(ApiError.PROJECT_NOT_FOUND, "user is not a member of the project or it does not exist")).role;
        if (!Arrays.stream(permissions).allMatch(permission -> role.getPermissions().contains(permission)))
            throw new ApiException(ApiError.INSUFFICIENT_PERMISSION, "not enough permissions");
    }

    @Transactional
    public ApiException authorizeNoThrow(UserContext userContext, UUID projectId, ProjectPermission... permissions) {
        RemoteProjectMember member = remoteProjectMemberRepository.findByProjectUserId(projectId, userContext.userId())
                .orElse(null);
        if(member == null)
            return new ApiException(ApiError.PROJECT_NOT_FOUND, "user is not a member of the project or it does not exist");
        if (!Arrays.stream(permissions).allMatch(permission -> member.role.getPermissions().contains(permission)))
            return new ApiException(ApiError.INSUFFICIENT_PERMISSION, "not enough permissions");
        return null;
    }

}
