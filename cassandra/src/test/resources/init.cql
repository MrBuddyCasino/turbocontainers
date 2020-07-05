CREATE KEYSPACE test1 WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1}
AND durable_writes = true;

USE test1;

CREATE TABLE IF NOT EXISTS books (
    id bigint,
    title text,
    PRIMARY KEY (id)
);

INSERT INTO books (id, title) VALUES(1,'Masters of Doom');
