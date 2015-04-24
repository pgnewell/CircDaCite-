
CREATE TABLE if not exists path_locations (
                    _path integer,
                    _location integer ,
                    seq integer,
                    type integer,
                    name varchar(255),
                    PRIMARY KEY (_path, _location));

