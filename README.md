## Tworzenie bazy danych dla aplikacji
```
CREATE DATABASE skaner;
USE skaner;
CREATE TABLE gniazdo(g_id INT PRIMARY KEY NOT NULL IDENTITY(1,1),gniazdo_kod VARCHAR(10) NOT NULL);
CREATE TABLE pozycja(p_id INT PRIMARY KEY NOT NULL IDENTITY(1,1),gniazdo VARCHAR(10) NOT NULL,t_id INT NOT NULL,ilosc INT NOT NULL,u_id INT NOT NULL);
CREATE TABLE towary(t_id INT PRIMARY KEY NOT NULL IDENTITY(1,1),nazwa VARCHAR(50), kod VARCHAR(100) NOT NULL);
CREATE TABLE users(u_id INT PRIMARY KEY NOT NULL IDENTITY(1,1),nazwa VARCHAR(25) NOT NULL,haslo VARCHAR(50) NOT NULL);
/* DODAĆ SWOICH UŻYTKOWNIKÓW BAZY DANYCH ORAZ SWOJĄ NUMERACJE REGAŁÓW NP.... */
INSERT INTO users(nazwa, haslo) VALUES('sa','test');
INSERT INTO gniazdo(gniazdo_kod) VALUES('A1'),('A2'),('A3'),('A4'),('A5'),('A6'),('A7'),('A8'),('A9'),('A10'),('B1'),('B2'),('B3'),('B4'),('B5'),('B6'),('B7'),('B8'),('B9'),('B10'),('C1'),('C2'),('C3'),('C4'),('C5'),('C6'),('C7'),('C8'),('C9'),('C10');
```
