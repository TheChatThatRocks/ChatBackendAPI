FROM prom/prometheus:v2.17.2
ENV hostname=hn-my-prometheus-docker
ENV name=prometheus-docker
# Copy yml config file to image
COPY prometheus.yml /etc/prometheus/prometheus.yml
# Set config file path
ENV config.file=/etc/prometheus/prometheus.yml
# Expose port for conn with grafana
EXPOSE 9090