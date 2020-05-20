## When create a container and it already exists, restart it only if it has failed. Max. restart attempts = 10
## Pull all images
docker build .
# Run all docker images
## Run PostgresSQL in background with username as "postgres", password as "easypassword", database name as "postgres" and port as "15432"
docker run -d --restart=on-failure:10 --name my-postgres-docker -e POSTGRES_PASSWORD=easypassword -p 15432:5432 postgres:12.2

## Run MongoDB in background with username as "mongoadmin", password as "secret", database name as "mymongodb" and port as "27017"
docker run -d --restart=on-failure:10 --name my-mongo-docker -e MONGO_INITDB_ROOT_USERNAME=mongoadmin -e MONGO_INITDB_ROOT_PASSWORD=easypassword -e MONGO_INITDB_DATABASE=mymongodb -p 27017:27017 mongo:4.2.5

# Check it for credentials: https://registry.hub.docker.com/_/rabbitmq/
## Run RabbitMQ in background  with username as "guest", password as "guest", port as "5672" for the service, as "15672" for managment
## and as 15692 for prometheus plugin
docker build ./rabbitmq/ -t my_rabbitmq:v1 &&  \
docker run -d --restart=on-failure:10 -it --name my-rabbitmq-docker -p 5672:5672 -p 15672:15672 -p 15692:15692 my_rabbitmq:v1


## Run Prometheus in background with port "9090" for the service
docker build ./prometheus/ -t my_prometheus:v1  && \
docker run -d --restart=on-failure:10 --name prometheus -p 9090:9090 my_prometheus:v1

## Run grafana in background with port "3000"
docker run -d --restart=on-failure:10 --name grafana -p 3000:3000 grafana/grafana:7.0.0-beta3