alter table `recipe`
    ADD COLUMN `USERGUID` char(36) COLLATE ascii_bin NOT NULL;

alter table `recipe`
    ADD CONSTRAINT `FK_USER_RECIPE_GUID`
    FOREIGN KEY `FK_USER_RECIPE_GUID` (`USERGUID`)
    REFERENCES `user` (`GUID`);

alter table `shoppinglist`
    ADD COLUMN `USERGUID` char(36) COLLATE ascii_bin NOT NULL;

alter table `shoppinglist`
    ADD CONSTRAINT `FK_USER_SHOPPINGLIST_GUID`
    FOREIGN KEY `FK_USER_SHOPPINGLIST_GUID` (`USERGUID`)
    REFERENCES `user` (`GUID`);