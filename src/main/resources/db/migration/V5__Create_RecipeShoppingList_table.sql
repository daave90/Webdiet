create table recipe_shoppinglist(
	`SCHOPPINGLISTGUID` char(36) COLLATE ascii_bin NOT NULL,
	`RECIPEGUID` char(36) COLLATE ascii_bin NOT NULL,
    primary key(SCHOPPINGLISTGUID, RECIPEGUID),
	constraint FK_Recipe_02 foreign key (RECIPEGUID) references recipe (GUID),
	constraint FK_Schoppinglist foreign key (SCHOPPINGLISTGUID) references shoppinglist (GUID)
);