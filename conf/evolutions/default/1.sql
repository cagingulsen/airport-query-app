# --- Airports Database Schema

# --- !Ups

CREATE TABLE country AS SELECT * FROM CSVREAD('.\input\countries.csv', null, 'charset=UTF-8');
ALTER TABLE country ALTER COLUMN name RENAME TO cname;
ALTER TABLE country ALTER COLUMN code SET NOT NULL;
ALTER TABLE country ADD PRIMARY KEY (code);


CREATE TABLE airport AS SELECT * FROM CSVREAD('.\input\airports.csv', null, 'charset=UTF-8');
ALTER TABLE airport ALTER COLUMN type RENAME TO atype;
ALTER TABLE airport ALTER COLUMN iso_country RENAME TO country_code;
ALTER TABLE airport ALTER COLUMN id SET NOT NULL;
ALTER TABLE airport ADD PRIMARY KEY (id);

ALTER TABLE airport ADD CONSTRAINT fk_country_ref FOREIGN KEY (country_code) REFERENCES country (code) ON DELETE RESTRICT ON UPDATE RESTRICT;


CREATE TABLE runway AS SELECT * FROM CSVREAD('.\input\runways.csv', null, 'charset=UTF-8');
ALTER TABLE runway ALTER COLUMN id SET NOT NULL;
ALTER TABLE runway ADD PRIMARY KEY (id);
ALTER TABLE runway ALTER COLUMN airport_ref RENAME TO airport_id;

ALTER TABLE runway  ADD CONSTRAINT fk_airport_ref FOREIGN KEY (airport_id) REFERENCES airport (id) ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE index ix_runway_airport_1 ON runway (airport_id);

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;
DROP TABLE IF EXISTS airport;
DROP TABLE IF EXISTS country;
DROP TABLE IF EXISTS runway;
SET REFERENTIAL_INTEGRITY TRUE;



