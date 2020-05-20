#!/bin/bash

# Cleanup docker dangling volumes
#docker volume rm $(docker volume ls -qf dangling=true)

(cd encryption && ./build.sh)
(cd backendAPI && ./build.sh)

docker-compose -f docker-compose-local.yml up --build --remove-orphans 1> /dev/null

#sleep 3
##docker build ./backendAPI/ -t deploy_backend-api &&  \
##docker run -d --restart=on-failure:10 -it --name backend-api -p 8080:8080 deploy_backend-api
#docker-compose -f docker-compose-backend.yml up --build --remove-orphans 1> /dev/null