
CREATE TABLE if not exists locations (
                    _id integer PRIMARY KEY autoincrement,
                    name varchar(255),
                    address varchar(255),
                    latitude double,
                    longitude double,
                    UNIQUE (name));

