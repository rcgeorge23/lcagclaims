#!/usr/bin/env bash

./standup_all.sh

echo "Running tests..."
mvn test
ERROR_CODE=$?

echo "Removing all running containers"
docker rm -f $(docker ps -a -q)

exit $ERROR_CODE