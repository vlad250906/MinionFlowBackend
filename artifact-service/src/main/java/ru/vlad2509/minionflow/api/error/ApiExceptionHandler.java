package ru.vlad2509.minionflow.api.error;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.quarkus.hibernate.validator.runtime.jaxrs.ResteasyReactiveViolationException;
import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.UnauthorizedException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import ru.vlad2509.minionflow.application.exception.ApiException;

import java.util.List;

@Provider
@ApplicationScoped
public class ApiExceptionHandler {

    private static final Logger LOG = Logger.getLogger(ApiExceptionHandler.class);
    private static final String PROBLEM_JSON = "application/problem+json";

    @ServerExceptionMapper
    public RestResponse<ApiErrorResponse> mapUnauthorized(UnauthorizedException ex, UriInfo uriInfo, ContainerRequestContext req) {
        return build(401, "unauthorized", "Unauthorized", "Bearer JWT is missing", uriInfo, req, null);
    }

    @ServerExceptionMapper
    public RestResponse<ApiErrorResponse> mapUnauthorized(AuthenticationFailedException ex, UriInfo uriInfo, ContainerRequestContext req) {
        //ex.printStackTrace();
        return build(401, "unauthorized", "Unauthorized", "Incorrect Bearer JWT", uriInfo, req, null);
    }


    @ServerExceptionMapper
    public RestResponse<ApiErrorResponse> mapJsonParse(JsonParseException ex, UriInfo uriInfo, ContainerRequestContext req) {
        return build(400, "invalidJson", "Invalid JSON", ex.getOriginalMessage(), uriInfo, req, null);
    }

    @ServerExceptionMapper
    public RestResponse<ApiErrorResponse> mapMismatched(MismatchedInputException ex, UriInfo uriInfo, ContainerRequestContext req) {
        return build(400, "invalidJson", "Invalid JSON", ex.getOriginalMessage(), uriInfo, req, null);
    }

    // на всякий — общий Jackson mapping
    @ServerExceptionMapper
    public RestResponse<ApiErrorResponse> mapJsonMapping(JsonMappingException ex, UriInfo uriInfo, ContainerRequestContext req) {
        return build(400, "invalidJson", "Invalid JSON", ex.getOriginalMessage(), uriInfo, req, null);
    }

    @ServerExceptionMapper
    public RestResponse<ApiErrorResponse> mapApi(ApiException ex, UriInfo uriInfo, ContainerRequestContext req) {
        return build(ex.getHttpStatusCode(), ex.getErrorCode(), "Request failed", ex.getMessage(), uriInfo, req, null);
    }

    @ServerExceptionMapper
    public RestResponse<ApiErrorResponse> mapNotFound(NotFoundException ex, UriInfo uriInfo, ContainerRequestContext req) {
        return build(404, "notFound", "Not Found", ex.getMessage(), uriInfo, req, null);
    }

    @ServerExceptionMapper
    public RestResponse<ApiErrorResponse> mapWeb(WebApplicationException ex, UriInfo uriInfo, ContainerRequestContext req) {
        int status = ex.getResponse() != null ? ex.getResponse().getStatus() : 500;

        // FIXME: костыль + тут можно случайно раскрыть лишние тех. данные
        Throwable th = ex;
        while (status < 500 && th != null && th.getCause() != null) {
            th = th.getCause();
        }

        String code = (status >= 500) ? "unexpectedError" : "requestError";
        String title = (status >= 500) ? "Server error" : "Request error";
        String detail = (status >= 500) ? "An unexpected error occurred" : th.getMessage();
        return build(status, code, title, detail, uriInfo, req, null);
    }

    @ServerExceptionMapper
    public RestResponse<ApiErrorResponse> mapResteasyReactiveViolation(ResteasyReactiveViolationException ex,
                                                 UriInfo uriInfo,
                                                 ContainerRequestContext req) {
        return validationResponse(ex, uriInfo, req);
    }

    @ServerExceptionMapper
    public RestResponse<ApiErrorResponse> mapConstraintViolation(ConstraintViolationException ex,
                                           UriInfo uriInfo,
                                           ContainerRequestContext req) {
        return validationResponse(ex, uriInfo, req);
    }

    @ServerExceptionMapper
    public RestResponse<ApiErrorResponse> mapAny(Throwable ex, UriInfo uriInfo, ContainerRequestContext req) {
        LOG.error("Unhandled error", ex);

        return build(
                500,
                "unexpectedError",
                "Unexpected error",
                "An unexpected error occurred.",
                uriInfo,
                req,
                null
        );
    }

    private RestResponse<ApiErrorResponse> validationResponse(ConstraintViolationException ex, UriInfo uriInfo, ContainerRequestContext req) {
        List<ApiErrorResponse.FieldError> errors = ex.getConstraintViolations().stream()
                .map(ApiExceptionHandler::toFieldError)
                .toList();

        return build(
                400,
                "validationFailed",
                "Validation failed",
                "Request validation failed.",
                uriInfo,
                req,
                errors
        );
    }

    private static ApiErrorResponse.FieldError toFieldError(ConstraintViolation<?> v) {
        String field = v.getPropertyPath() != null ? v.getPropertyPath().toString() : null;
        String msg = v.getMessage();
        return new ApiErrorResponse.FieldError(field, msg);
    }

    private RestResponse<ApiErrorResponse> build(int status,
                           String code,
                           String title,
                           String detail,
                           UriInfo uriInfo,
                           ContainerRequestContext req,
                           List<ApiErrorResponse.FieldError> errors) {
        ApiErrorResponse body = new ApiErrorResponse(
                status,
                title,
                code,
                detail,
                uriInfo != null ? uriInfo.getRequestUri() : null,
                currentTraceIdOrNull(),
                (errors == null || errors.isEmpty()) ? null : errors
        );

        Response.Status responseStatus = Response.Status.fromStatusCode(status);

        return RestResponse.ResponseBuilder
                .create(responseStatus == null ? RestResponse.Status.INTERNAL_SERVER_ERROR : responseStatus, body)
                .type(MediaType.valueOf(PROBLEM_JSON))
                .build();
    }

    private String currentTraceIdOrNull() {
        SpanContext ctx = Span.current().getSpanContext();
        if (ctx == null || !ctx.isValid())
            return null;
        return ctx.getTraceId();
    }

}
