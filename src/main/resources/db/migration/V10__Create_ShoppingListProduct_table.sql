create table `product_shoppinglist`(
    `SCHOPPINGLISTGUID` char(36) COLLATE ascii_bin NOT NULL,
    `WEIGHT` bigint DEFAULT 0,
	`PRODUCTGUID` char(36) COLLATE ascii_bin NOT NULL,
    primary key(SCHOPPINGLISTGUID, PRODUCTGUID),
	constraint FK_Product_02 foreign key (PRODUCTGUID) references product (GUID),
	constraint FK_Schoppinglist_02 foreign key (SCHOPPINGLISTGUID) references shoppinglist (GUID)
);