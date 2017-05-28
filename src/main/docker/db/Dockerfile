FROM postgres:9.6

ENV POSTGRES_DB web_crawler

# Set initial schema
COPY psql_init_schema.sql /docker-entrypoint-initdb.d/
