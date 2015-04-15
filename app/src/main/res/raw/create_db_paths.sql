
CREATE TABLE if not exists paths (
                    _id integer PRIMARY KEY autoincrement,
                    name varchar(255),
                    UNIQUE (name));

