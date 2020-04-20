# For development
## Setup databases and the message broker in docker
``` shell script
# Install docker if it is not installed yet. If you don't use snap, you can watch https://docs.docker.com/engine/install/ for other installation methods
snap install docker

# Get all images we will need
## PostgresSQL
sudo docker pull postgres:12.2

## MongoDB
sudo docker pull mongo:4.2.5

## RabbitMQ
sudo docker pull rabbitmq:3.8.3-management

# Run all docker images
## Run PostgresSQL in foreground (-d for background) with username as "postgres", password as "easypassword", database name as "postgres" and port as "15432" 
sudo docker run --rm --name my-postgres-docker -e POSTGRES_PASSWORD=easypassword -p 15432:5432 postgres:12.2

## Run MongoDB in foreground (-d for background) with username as "mongoadmin", password as "secret", database name as "mymongodb" and port as "27017" 
sudo docker run --rm --name my-mongo-docker -e MONGO_INITDB_ROOT_USERNAME=mongoadmin -e MONGO_INITDB_ROOT_PASSWORD=easypassword -e MONGO_INITDB_DATABASE=mymongodb -p 27017:27017 mongo:4.2.5

## Run RabbitMQ in foreground (-d for background)  with username as "guest", password as "guest", port as "15672" for the service and as "18090" for managment
sudo docker run --rm --hostname hn-my-rabbit-docker --name my-rabbit-docker -p 15672:5672 -p 18090:15672 rabbitmq:3.8.3-management
```
