DECLARE @databases TABLE (name sysname);

INSERT INTO @databases (name)
VALUES
    (N'BuganvillaTours1'),
    (N'BuganvillaAuth'),
    (N'BuganvillaCatalogo'),
    (N'BuganvillaInventario'),
    (N'BuganvillaReservas'),
    (N'BuganvillaPagos');

DECLARE @name sysname;
DECLARE db_cursor CURSOR FOR SELECT name FROM @databases;

OPEN db_cursor;
FETCH NEXT FROM db_cursor INTO @name;

WHILE @@FETCH_STATUS = 0
BEGIN
    IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = @name)
    BEGIN
        DECLARE @sql nvarchar(max) = N'CREATE DATABASE ' + QUOTENAME(@name);
        EXEC (@sql);
        PRINT 'Base de datos ' + @name + ' creada.';
    END
    ELSE
    BEGIN
        PRINT 'Base de datos ' + @name + ' ya existe.';
    END

    FETCH NEXT FROM db_cursor INTO @name;
END

CLOSE db_cursor;
DEALLOCATE db_cursor;
GO
