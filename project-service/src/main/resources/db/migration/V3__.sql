CREATE TABLE remote_users
(
    user_id   UUID         NOT NULL,
    username VARCHAR(255) NOT NULL,
    CONSTRAINT pk_remote_users PRIMARY KEY (user_id)
);

ALTER TABLE project_members
    ADD CONSTRAINT FK_PROJECT_MEMBERS_ON_REMOTE_USER FOREIGN KEY (user_id) REFERENCES remote_users (user_id) ON DELETE SET NULL;