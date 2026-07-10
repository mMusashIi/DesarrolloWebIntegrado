FROM mcr.microsoft.com/mssql/server:2022-latest
USER root
COPY init.sql /docker-entrypoint-initdb.d/init.sql
COPY sqlserver-setup.sh /setup.sh
RUN chmod +x /setup.sh
USER mssql
CMD ["/setup.sh"]
