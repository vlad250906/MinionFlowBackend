CREATE TABLE task_runs
(
    id                  UUID                        NOT NULL,
    projectId           UUID                        NOT NULL,
    userId              UUID                        NOT NULL,
    status              VARCHAR(255)                NOT NULL,
    jar_artifact_id     BIGINT,
    input_artifact_id   BIGINT,
    jar_jpa_id          UUID,
    input_jpa_id        BIGINT,
    execution_config_id UUID,
    output_jpa_id       UUID,
    createdAt           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    startedAt           TIMESTAMP WITHOUT TIME ZONE,
    finishedAt          TIMESTAMP WITHOUT TIME ZONE,
    doneAt              TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_task_runs PRIMARY KEY (id)
);

ALTER TABLE storage_identifiers
    ADD wasDeleted BOOLEAN default false;

ALTER TABLE storage_identifiers
    ALTER COLUMN wasDeleted SET NOT NULL;

ALTER TABLE task_runs
    ADD CONSTRAINT FK_TASK_RUNS_ON_EXECUTION_CONFIG FOREIGN KEY (execution_config_id) REFERENCES execution_configs (id);

ALTER TABLE task_runs
    ADD CONSTRAINT FK_TASK_RUNS_ON_INPUT_ARTIFACT FOREIGN KEY (input_artifact_id) REFERENCES storage_identifiers (id);

ALTER TABLE task_runs
    ADD CONSTRAINT FK_TASK_RUNS_ON_INPUT_JPA FOREIGN KEY (input_jpa_id) REFERENCES input_artifacts (id);

ALTER TABLE task_runs
    ADD CONSTRAINT FK_TASK_RUNS_ON_JAR_ARTIFACT FOREIGN KEY (jar_artifact_id) REFERENCES storage_identifiers (id);

ALTER TABLE task_runs
    ADD CONSTRAINT FK_TASK_RUNS_ON_JAR_JPA FOREIGN KEY (jar_jpa_id) REFERENCES artifacts (id);

ALTER TABLE task_runs
    ADD CONSTRAINT FK_TASK_RUNS_ON_OUTPUT_JPA FOREIGN KEY (output_jpa_id) REFERENCES artifacts (id);