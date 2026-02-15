CREATE SEQUENCE IF NOT EXISTS email_messages_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS verification_tickets_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE email_messages
(
    id         BIGINT                   NOT NULL,
    email      VARCHAR(255)             NOT NULL,
    content    VARCHAR(255)             NOT NULL,
    isSent     BOOLEAN                  NOT NULL,
    isFailed   BOOLEAN                  NOT NULL,
    takenBy    INTEGER                  NOT NULL,
    takenUntil TIMESTAMP WITH TIME ZONE NOT NULL,
    attempts   INTEGER                  NOT NULL,
    nextTryIn  TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_email_messages PRIMARY KEY (id)
);

CREATE TABLE user_sessions
(
    sessionId UUID NOT NULL,
    jwtId     UUID NOT NULL,
    user_id   UUID NOT NULL,
    CONSTRAINT pk_user_sessions PRIMARY KEY (sessionId)
);

CREATE TABLE users
(
    userId       UUID         NOT NULL,
    email        VARCHAR(255) NOT NULL,
    username     VARCHAR(255) NOT NULL,
    passwordHash VARCHAR(255) NOT NULL,
    status       VARCHAR(255) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (userId)
);

CREATE TABLE verification_tickets
(
    id                BIGINT       NOT NULL,
    userId            UUID         NOT NULL,
    type              VARCHAR(255) NOT NULL,
    verificationToken UUID         NOT NULL,
    CONSTRAINT pk_verification_tickets PRIMARY KEY (id)
);

ALTER TABLE user_sessions
    ADD CONSTRAINT uc_user_sessions_jwtid UNIQUE (jwtId);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

ALTER TABLE user_sessions
    ADD CONSTRAINT FK_USER_SESSIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (userId) ON DELETE CASCADE;