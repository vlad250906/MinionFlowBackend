CREATE TABLE execution_configs
(
    id        UUID                        NOT NULL,
    alias     VARCHAR(255)                NOT NULL,
    projectId UUID                        NOT NULL,
    userId    UUID                        NOT NULL,
    createdAt TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    content   JSONB                       NOT NULL,
    CONSTRAINT pk_execution_configs PRIMARY KEY (id)
);