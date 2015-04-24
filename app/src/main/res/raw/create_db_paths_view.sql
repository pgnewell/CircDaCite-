CREATE VIEW if NOT EXISTS paths_view AS
    SELECT p._id, p.name,
        sl.name "start_loc", sl.latitude "start_loc_lat", sl.longitude "start_loc_lon",
        el.name "end_loc", el.latitude "end_loc_lat", el.longitude "end_loc_lon"
    FROM
        paths p JOIN
        path_locations spl ON p._id = spl._path AND spl.type = 0 JOIN
        locations sl ON spl._location = sl._id JOIN
        path_locations epl ON p._id = epl._path AND epl.type = 2 JOIN
        locations el ON epl._location = el._id ;

