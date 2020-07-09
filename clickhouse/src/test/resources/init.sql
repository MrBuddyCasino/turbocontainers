CREATE DATABASE db1;

CREATE TABLE db1.books
(
    `timestamp` DateTime('Europe/Berlin') DEFAULT now(),
    `event_id` UInt8,
    `title` String
)
ENGINE = TinyLog;

INSERT INTO db1.books (event_id, title) VALUES (1, 'The Power Broker');