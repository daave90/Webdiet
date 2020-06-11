alter table `product`
    ADD COLUMN `USERGUID` char(36) COLLATE ascii_bin NOT NULL;

alter table `product`
    ADD CONSTRAINT `FK_USER_PRODUCT_GUID`
    FOREIGN KEY `FK_USER_PRODUCT_GUID` (`USERGUID`)
    REFERENCES `user` (`GUID`);