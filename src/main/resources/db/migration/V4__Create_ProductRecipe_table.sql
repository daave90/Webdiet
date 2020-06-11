create table product_recipe (
	`PRODUCTGUID` char(36) COLLATE ascii_bin NOT NULL,
	`RECIPEGUID` char(36) COLLATE ascii_bin NOT NULL,
    primary key(PRODUCTGUID, RECIPEGUID),
    constraint FK_Product foreign key (PRODUCTGUID) references product (GUID),
	constraint FK_Recipe_01 foreign key (RECIPEGUID) references recipe (GUID)
);