DROP TABLE ItemCatalog;
DROP TABLE DropRate;

CREATE TABLE ItemCatalog (
  ItemId      INT PRIMARY KEY,
  Type        VARCHAR(32) NOT NULL,
  Name        VARCHAR(64) NOT NULL,
  Rarity      SMALLINT    NOT NULL,
  Value       DOUBLE,
  StackLimit  SMALLINT DEFAULT 99,
  PropertyA   DOUBLE,
  PropertyB   DOUBLE,
  PropertyC   DOUBLE,
  PropertyD   DOUBLE,
  PropertyE   DOUBLE,
  PropertyF   DOUBLE,
  PropertyG   BIGINT,
  PropertyH   BIGINT,
  Description VARCHAR(2000)
);

CREATE TABLE DropRate (
  ItemId INT NOT NULL,
  DropId INT NOT NULL,
  Weight INT NOT NULL,
  CONSTRAINT DropRate_PK PRIMARY KEY (ItemId, DropId)
);