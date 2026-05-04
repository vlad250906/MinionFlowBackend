package ru.vlad2509.minionflow.application.exception;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.postgresql.util.PSQLException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@ApplicationScoped
public class ConstraintExceptionMapper {

    private final Map<String, Supplier<ApiException>> constraintToApi = new HashMap<>();
    private static final String PG_UNIQUE_VIOLATION = "23505";

    @PostConstruct
    public void init() {
        constraintToApi.put("uc_users_email",
                () -> new ApiException(ApiError.EMAIL_TAKEN, "Email unique constraint failed: not great, but ok"));
        constraintToApi.put("uc_users_username",
                () -> new ApiException(ApiError.USERNAME_TAKEN, "Username unique constraint failed: not great, but ok"));
    }

    public ApiException mapPersistenceException(PersistenceException ex) {
        PSQLException pgException = findCause(ex, PSQLException.class);

        if (pgException != null && PG_UNIQUE_VIOLATION.equals(pgException.getSQLState())) {
            String constraintName = normalizeConstraintName(pgException.getServerErrorMessage() != null
                    ? pgException.getServerErrorMessage().getConstraint() : null);

            if (constraintName != null) {
                Supplier<ApiException> mapped = constraintToApi.get(constraintName);
                if (mapped != null)
                    return mapped.get();
            }
        }

        return new ApiException(ApiError.UNEXPECTED_ERROR, "undefined PersistenceException: " + ex.getMessage());
    }

    private String extractConstraintName(ConstraintViolationException ex) {
        String fromHibernate = normalizeConstraintName(ex.getConstraintName());
        if (fromHibernate != null)
            return fromHibernate;

        SQLException sqlException = ex.getSQLException();
        while (sqlException != null) {
            if (sqlException instanceof PSQLException pgException && pgException.getServerErrorMessage() != null) {
                String fromPostgres = normalizeConstraintName(pgException.getServerErrorMessage().getConstraint());

                if (fromPostgres != null) {
                    return fromPostgres;
                }
            }

            sqlException = sqlException.getNextException();
        }

        return null;
    }

    private boolean isPostgresUniqueViolation(ConstraintViolationException ex) {
        SQLException sqlException = ex.getSQLException();

        while (sqlException != null) {
            if (PG_UNIQUE_VIOLATION.equals(sqlException.getSQLState()))
                return true;

            sqlException = sqlException.getNextException();
        }

        return false;
    }

    private String normalizeConstraintName(String name) {
        if (name == null || name.isBlank())
            return null;

        name = name.trim();
        if (name.startsWith("\"") && name.endsWith("\"") && name.length() >= 2)
            name = name.substring(1, name.length() - 1);

        return name.toLowerCase(Locale.ROOT);
    }

    private <T extends Throwable> T findCause(Throwable ex, Class<T> type) {
        Throwable cur = ex;

        while (cur != null) {
            if (type.isInstance(cur)) {
                return type.cast(cur);
            }

            cur = cur.getCause();
        }

        return null;
    }
}
