CREATE DATABASE IF NOT EXISTS minecraft;

USE minecraft;

CREATE TABLE IF NOT EXISTS userstatus(
	id INT NOT NULL AUTO_INCREMENT,
    description varchar(255),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users(
    id INT NOT NULL AUTO_INCREMENT,
	username VARCHAR(50) NOT NULL,
    ipaddr VARCHAR(50) NOT NULL,
    `status` INT DEFAULT 1,
	note TEXT,
	bantime DATETIME,
    PRIMARY KEY (id),
	FOREIGN KEY (`status`)
		REFERENCES userstatus(id)
		ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS actiontypes(
    id INT NOT NULL AUTO_INCREMENT,
    `desc` varchar(255),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS worlds(
    dim INT NOT NULL,
    `name` VARCHAR(50),
	INDEX dim_dex (dim),
    PRIMARY KEY (dim)
);

CREATE TABLE IF NOT EXISTS actions(
	id INT NOT NULL AUTO_INCREMENT,
	user INT NOT NULL, #who
	`action` INT NOT NULL, #what
	stamp TIMESTAMP, #when
    world INT NOT NULL, #where
	xCoord INT NOT NULL, #where pt 2
    yCoord INT NOT NULL,
    zCoord INT NOT NULL,
    itemID INT,
    blockID INT,
    metadata INT,
    `desc` VARCHAR(250),
    PRIMARY KEY (id),
	FOREIGN KEY (user)
		REFERENCES users(id)
		ON DELETE RESTRICT,
	FOREIGN KEY (`action`)
		REFERENCES actiontypes(id)
		ON DELETE RESTRICT,
	FOREIGN KEY (world)
		REFERENCES worlds(dim)
		ON DELETE RESTRICT
);

#user status defaults
INSERT INTO userstatus (description) VALUES ("Active");
INSERT INTO userstatus (description) VALUES ("TempBanned");
INSERT INTO userstatus (description) VALUES ("PermaBanned");

#action type defaults
#fallback
INSERT INTO actiontypes (`desc`) 
	VALUES ('Other');
#block break
INSERT INTO actiontypes (`desc`) 
	VALUES ('Block Break');
#block place
INSERT INTO actiontypes (`desc`) 
	VALUES ('Block Place');
#block use
INSERT INTO actiontypes (`desc`) 
	VALUES ('Block Use');
#container open
INSERT INTO actiontypes (`desc`) 
	VALUES ('Container Open');
#item pickup
INSERT INTO actiontypes (`desc`) 
	VALUES ('Item Pickup');
#item drop
INSERT INTO actiontypes (`desc`) 
	VALUES ('Item Drop');
#item use
INSERT INTO actiontypes (`desc`) 
	VALUES ('Item Use');
#item break
INSERT INTO actiontypes (`desc`) 
	VALUES ('Item Break');
#chat
INSERT INTO actiontypes (`desc`) 
	VALUES ('Chat');
#command
INSERT INTO actiontypes (`desc`) 
	VALUES ('Command');
#connect
INSERT INTO actiontypes (`desc`) 
	VALUES ('Connect');
#disconnect
INSERT INTO actiontypes (`desc`) 
	VALUES ('Disconnect');
#teleport
INSERT INTO actiontypes (`desc`) 
	VALUES ('Teleport');
#enderman grab
INSERT INTO actiontypes (`desc`) 
	VALUES ('Enderman Grab');
#enderman place
INSERT INTO actiontypes (`desc`) 
	VALUES ('Enderman Place');
#explosion
INSERT INTO actiontypes (`desc`) 
	VALUES ('Explosion');
#fire spread
INSERT INTO actiontypes (`desc`) 
	VALUES ('Fire Spread');
#mob death
INSERT INTO actiontypes (`desc`) 
	VALUES ('Mob Death');
#player death
INSERT INTO actiontypes (`desc`) 
	VALUES ('Player Death');
#other death
INSERT INTO actiontypes (`desc`) 
	VALUES ('Other Death');
#mob kill
INSERT INTO actiontypes (`desc`) 
	VALUES ('Mob Kill');
#player kill
INSERT INTO actiontypes (`desc`) 
	VALUES ('Player Kill');
