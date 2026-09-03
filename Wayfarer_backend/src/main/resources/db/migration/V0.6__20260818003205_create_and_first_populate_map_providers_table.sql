CREATE TABLE IF NOT EXISTS map_providers(
    id SERIAL PRIMARY KEY,
    name VARCHAR(60) NOT NULL,
    url VARCHAR(320) NOT NULL,
    attribution VARCHAR(60) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE NOT NULL,
    invertible BOOLEAN DEFAULT TRUE NOT NULL
);

INSERT INTO map_providers(name, url, attribution, is_default)
    VALUES (
        'Plan IGN V2',
        'https://data.geopf.fr/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=GEOGRAPHICALGRIDSYSTEMS.' ||
        'PLANIGNV2&STYLE=normal&TILEMATRIXSET=PM&TILEMATRIX={z}&TILEROW={y}&TILECOL={x}&FORMAT=image/png',
        'IGN - Géoportail',
        TRUE
   );

INSERT INTO map_providers(name, url, attribution, invertible)
    VALUES (
        'Plan satellite',
        'https://data.geopf.fr/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=ORTHOIMAGERY.' ||
        'ORTHOPHOTOS&STYLE=normal&TILEMATRIXSET=PM&TILEMATRIX={z}&TILEROW={y}&TILECOL={x}&FORMAT=image/jpeg',
        'IGN - Géoportail',
        FALSE
   );

INSERT INTO map_providers(name, url, attribution, invertible)
VALUES (
           'Plan OpenStreetMap',
           'https://tile.openstreetmap.org/{z}/{x}/{y}.png',
           'OSM - WMTS',
           TRUE
       );
