alter table `user`
    CHANGE COLUMN `USERNAME` `EMAIL` varchar(255) NOT NULL;

alter table `user`
    ADD COLUMN `MAILHOST` varchar(255);

alter table `user`
    ADD COLUMN `MAILPORT` bigint DEFAULT 0;

alter table `user`
    ADD COLUMN `MAILPASSWORD` varchar(255);