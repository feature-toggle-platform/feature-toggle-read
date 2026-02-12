CREATE TABLE processed_events
(
    id uuid primary key
);

CREATE TABLE project_view
(
    id          uuid primary key,
    name        varchar(100) not null unique,
    description varchar(200),
    created_at  timestamp    not null,
    updated_at  timestamp    not null,
    status      varchar(10)  not null,
    revision    bigint       not null,
    consistent  boolean      not null default true
);

CREATE TABLE environment_view
(
    id         uuid primary key,
    project_id uuid         not null references project_view (id),
    name       varchar(100) not null,
    created_at timestamp    not null,
    updated_at  timestamp    not null,
    status     varchar(10)  not null,
    type       varchar(10)  not null,
    revision   bigint       not null,
    consistent boolean      not null default true
);

CREATE TABLE feature_toggle_view
(
    id             uuid primary key,
    project_id     uuid         not null,
    environment_id uuid         not null,
    name           varchar(100) not null,
    description    varchar(200),
    type           varchar(50)  not null,
    current_value  varchar(200) not null,
    status         varchar(20)  not null,
    created_at     timestamp    not null,
    updated_at     timestamp    not null,
    revision       bigint       not null,
    consistent     boolean      not null default true,
    CONSTRAINT feature_toggle_fk_environment_id FOREIGN KEY (environment_id) REFERENCES environment_view (id)
);
