CREATE SEQUENCE IF NOT EXISTS project_members_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE project_members
(
    id          BIGINT       NOT NULL,
    project_id  UUID         NOT NULL,
    user_id     UUID         NOT NULL,
    role        VARCHAR(255) NOT NULL,
    memberSince TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_project_members PRIMARY KEY (id)
);

CREATE TABLE projects
(
    id                 UUID         NOT NULL,
    projectName        VARCHAR(255) NOT NULL,
    projectDescription VARCHAR(255) NOT NULL,
    createdAt          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_projects PRIMARY KEY (id)
);

ALTER TABLE project_members
    ADD CONSTRAINT projectmembers_project_user_uniq UNIQUE (project_id, user_id);

ALTER TABLE projects
    ADD CONSTRAINT uc_projects_projectname UNIQUE (projectName);

ALTER TABLE project_members
    ADD CONSTRAINT FK_PROJECT_MEMBERS_ON_PROJECT FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE;