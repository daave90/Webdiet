create table recipe (
    `GUID` char(36) COLLATE ascii_bin NOT NULL,
    `VER` bigint DEFAULT 0,
    `CREATIONTIMESTAMP` bigint NOT NULL,
    `TYPE` varchar(255) NOT NULL,
    `NAME` varchar(255) NOT NULL,
    `TOTALKCAL` double NOT NULL,
    `DESCRIPTION` text NOT NULL,
    primary key(`GUID`)
);