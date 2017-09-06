#!/bin/sh

set -xe

function wait_for_db_container {
  until psql -h $DATABASE_HOST -p $DATABASE_PORT -U postgres -l > /dev/null 2>&1; do
    echo "Postgres is down, waiting.."
    sleep 1
  done

  echo "Postgres is up"
}

function start_java_app {
  java -XX:-UsePerfData -Djava.security.egd=file:/dev/./urandom -jar /usr/share/web-crawler/app.jar "$@" bla
}

wait_for_db_container
#start_java_app
exec java -XX:-UsePerfData -Djava.security.egd=file:/dev/./urandom -jar /usr/share/web-crawler/app.jar "$@" bla
