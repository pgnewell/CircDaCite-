
CREATE TABLE if not exists locations (
                    _id integer PRIMARY KEY autoincrement,
                    name varchar(255),
                    address varchar(255),
                    latitude double,
                    longitude double,
                    UNIQUE (name));

CREATE TABLE if not exists paths (
                    _id integer PRIMARY KEY autoincrement,
                    name varchar(255),
                    UNIQUE (name));

CREATE TABLE if not exists path_locations (
                    _path integer,
                    _location integer ,
                    name varchar(255),
                    PRIMARY KEY (_path, _location),
                    UNIQUE (name));
