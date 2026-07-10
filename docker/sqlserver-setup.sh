#!/bin/bash
# Arranca SQL Server en segundo plano y crea la BD al estar lista

/opt/mssql/bin/sqlservr &
SERVER_PID=$!

echo "Esperando que SQL Server esté listo..."
for i in $(seq 1 30); do
    /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$SA_PASSWORD" -No -Q "SELECT 1" > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        echo "SQL Server listo. Inicializando base de datos..."
        /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$SA_PASSWORD" -No -i /docker-entrypoint-initdb.d/init.sql
        echo "Inicialización completada."
        break
    fi
    echo "Intento $i/30 — esperando..."
    sleep 3
done

wait $SERVER_PID
