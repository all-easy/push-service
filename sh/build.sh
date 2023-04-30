#!/bin/bash

./mvnw clean install

#docker network create common
#docker network create prometheus
#docker network create metrics

# all
docker compose up --force-recreate -d

# selected services
#docker compose up --force-recreate -d postgres redis push-service
