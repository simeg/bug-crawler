#!/bin/sh

set -e

function wait_for_db_container {
  until psql -h db -U postgres -l > /dev/null 2>&1; do
    echo "Postgres is down, waiting.."
    sleep 1
  done

  echo "Postgres is up"
}

function start_java_app {
  java -XX:-UsePerfData -Djava.security.egd=file:/dev/./urandom -jar /usr/share/web-crawler/app.jar
}

wait_for_db_container
start_java_app
