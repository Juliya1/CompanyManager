CREATE TABLE companies
(
name varchar(255) NOT NULL,
earnings int,
parent varchar(255),
nesting_level int,
PRIMARY KEY(name)
);

# test data
INSERT INTO companies VALUES ('M', 10, null, 0);
INSERT INTO companies VALUES ('M1', 20, null, 0);
INSERT INTO companies VALUES ('S', 30, 'M', 1);
INSERT INTO companies VALUES ('S1', 40, 'M1', 1);
INSERT INTO companies VALUES ('S11', 30, 'S1', 2);
INSERT INTO companies VALUES ('S12', 50, 'S1', 2);
INSERT INTO companies VALUES ('S111', 30, 'S11', 3);
INSERT INTO companies VALUES ('S112', 50, 'S11', 3);