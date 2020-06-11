USE webdiet;

CREATE TABLE product (
    `GUID` char(36) COLLATE ascii_bin NOT NULL,
    `VER` bigint DEFAULT 0,
    `CREATIONTIMESTAMP` bigint NOT NULL,
    `NAME` varchar(255) NOT NULL,
    `KCAL` double NOT NULL,
    PRIMARY KEY(`GUID`)
);