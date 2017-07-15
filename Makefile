.PHONY: build build-db
.PHONY: start start-crawler start-db
.PHONY: stop clean db-connect


all: build build-db

#
# BUILD
#

build:
	mvn \
	  clean \
	  jooq-codegen:generate \
	  package \
	  docker:build

build-db:
	docker-compose build db

#
# START
#

start:
	docker-compose up

start-crawler:
	docker-compose up crawler

start-db:
	docker-compose up db

#
# MORE
#

stop:
	docker-compose stop

clean:
	docker-compose rm db; mvn clean

kill:
	docker-compose kill

connect-db:
	psql \
	--host localhost \
	--port 5432 \
	--username postgres \
	--no-password \
	--dbname=web_crawler
