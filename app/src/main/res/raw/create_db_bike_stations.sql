CREATE TABLE if not exists locations (
        _id integer PRIMARY KEY autoincrement,
        id integer, -- the BIXI id needs to be separate for stoopid reasons.
        name varchar(255),
        terminalName varchar(255),
        lastCommWithServer date,
        lat double,
        long double,
        installed integer,
        locked integer,
        installDate date,
        removalDate date,
        temporary integer,
        public integer,
        nbBikes integer,
        nbEmptyDocks integer,
        latestUpdateTime datetime
        );
