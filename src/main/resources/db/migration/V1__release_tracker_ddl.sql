CREATE TABLE release
(
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(255)             NOT NULL,
    description    TEXT,
    status         VARCHAR(50)              NOT NULL,
    release_date   DATE,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    last_update_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_release_status ON release (status);