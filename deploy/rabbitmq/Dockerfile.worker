FROM rabbitmq:3.8-management AS rabbitmq
ENV hostname=hn-my-rabbit-docker
ENV name=rabbit-docker
# Management port
EXPOSE 15672
# Service port
EXPOSE 5672
# Monitoring port
EXPOSE 15692
# Enable prometheus endpoint on docker
RUN rabbitmq-plugins enable --offline rabbitmq_prometheus