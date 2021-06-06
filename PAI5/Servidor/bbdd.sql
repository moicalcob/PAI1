CREATE USER 'mobifirma'@'localhost' IDENTIFIED BY 'ST22-MobiFirma';
GRANT ALL PRIVILEGES ON * . * TO 'mobifirma'@'localhost';
FLUSH PRIVILEGES;

CREATE DATABASE mobifirma;

CREATE TABLE pedidos (
    usuario int,
    mesas int,
    sillas int,
    camas int,
    sillones int,
    fecha DATETIME,
    tiempo TIMESTAMP,
    accepted boolean
);