create table shoppinglist (
    `GUID` char(36) COLLATE ascii_bin NOT NULL,
    `VER` bigint DEFAULT 0,
    `CREATIONTIMESTAMP` bigint NOT NULL,
    `DAYSNUMBER` int not null,
    primary key(`GUID`)
);