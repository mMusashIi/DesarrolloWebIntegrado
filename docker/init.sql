IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'BuganvillaTours1')
BEGIN
    CREATE DATABASE BuganvillaTours1;
    PRINT 'Base de datos BuganvillaTours1 creada.';
END
ELSE
    PRINT 'Base de datos BuganvillaTours1 ya existe.';
GO
