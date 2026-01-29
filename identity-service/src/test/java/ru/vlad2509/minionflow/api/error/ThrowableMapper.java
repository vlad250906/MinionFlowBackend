package ru.vlad2509.minionflow.api.error;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
@ApplicationScoped
@Priority(Priorities.USER) // после BadRequestMapper(1)
public class ThrowableMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(ThrowableMapper.class);

    @Override
    public Response toResponse(Throwable ex) {
        LOG.error("Unhandled error", ex);

        ApiErrorResponse body = new ApiErrorResponse(
                500,
                "Unexpected error",
                "unexpectedError",
                "An unexpected error occurred.",
                null,
                "TODO",
                null
        );

        return Response.status(500)
                .type("application/problem+json")
                .entity(body)
                .build();
    }
}