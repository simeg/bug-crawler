.PHONY: build build-db
.PHONY: start start-crawler start-db
.PHONY: stop clean db-connect


all: build build-db


build:
	mvn \
	  clean \
	  jooq-codegen:generate \
	  package \
	  docker:build

build-db:
	docker-compose build db


start:
	docker-compose up

start-crawler:
	docker-compose up crawler

start-db:
	docker-compose up db


stop:
	docker-compose stop

clean:
	docker-compose rm db; mvn clean

kill:
	docker-compose kill

db-connect:
	psql \
	--host localhost \
	--port 5432 \
	--username postgres \
	--no-password \
	--dbname=web_crawler

