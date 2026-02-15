ALTER TABLE email_messages
    ADD COLUMN subject varchar(255) NOT NULL DEFAULT '';

ALTER TABLE email_messages
    ALTER COLUMN subject DROP DEFAULT;