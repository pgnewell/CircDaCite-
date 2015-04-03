
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
                    PRIMARY KEY (_path, _location));

INSERT INTO locations (name,address,latitude,longitude) VALUES 
    ("Kendall","Tech Centre",42.3628735,-71.0900971),
    ("MIT","Mass Ave, Cambridge, MA",42.358109,-71.09571),
    ("Harvard", "Harvard Sq, Cambridge, MA",42.3770796,-71.1182556),
    ("Cambridgeport","Central Square, Cambridge, MA",42.3594092,-71.1073723),
    ("Porter","Mass Ave, Cambridge, MA",42.3888481,-71.1234376);

