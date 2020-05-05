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

## Prometheus
sudo docker pull prom/prometheus



# Run all docker images
## Run PostgresSQL in foreground (-d for background) with username as "postgres", password as "easypassword", database name as "postgres" and port as "15432" 
sudo docker run -d --rm --name my-postgres-docker -e POSTGRES_PASSWORD=easypassword -p 15432:5432 postgres:12.2

## Run MongoDB in foreground (-d for background) with username as "mongoadmin", password as "secret", database name as "mymongodb" and port as "27017" 
sudo docker run -d --rm --name my-mongo-docker -e MONGO_INITDB_ROOT_USERNAME=mongoadmin -e MONGO_INITDB_ROOT_PASSWORD=easypassword -e MONGO_INITDB_DATABASE=mymongodb -p 27017:27017 mongo:4.2.5

## Run RabbitMQ in foreground (-d for background)  with username as "guest", password as "guest", port as "15672" for the service and as "18090" for managment
sudo docker run -d --rm --hostname hn-my-rabbit-docker --name my-rabbit-docker -p 15672:5672 -p 18090:15672 rabbitmq:3.8.3-management

## Run Prometheus in background (-d for background) with port "9090" for the service and the local file prometheus.yml for configuration
sudo docker run -d --rm --name=prometheus -p 9090:9090 -v $(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml prom/prometheus --config.file=/etc/prometheus/prometheus.yml

## Run grafana in background (-d for background) with port "3000"
sudo docker run -d --rm --name grafana --name=grafana -p 3000:3000 grafana/grafana 

```
