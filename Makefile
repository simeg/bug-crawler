.PHONY: build build-db
.PHONY: start start-crawler start-db
.PHONY: stop clean db-connect

define exec_in_app_dir
	pushd app; $(1); popd;
endef


all: build build-db


build:
	$(call exec_in_app_dir,mvn clean jooq-codegen:generate package docker:build)

build-db:
	$(call exec_in_app_dir,docker-compose build db)


start:
	$(call exec_in_app_dir,docker-compose up)

start-crawler:
	$(call exec_in_app_dir,docker-compose up crawler)

start-db:
	$(call exec_in_app_dir,docker-compose up db)


stop:
	$(call exec_in_app_dir,docker-compose stop)

clean:
	$(call exec_in_app_dir,docker-compose rm db; mvn clean)

kill:
	docker kill app_crawler_1 app_db_1

db-connect:
	psql \
	--host localhost \
	--port 5432 \
	--username postgres \
	--no-password \
	--dbname=web_crawler

