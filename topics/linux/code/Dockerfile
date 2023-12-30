FROM registry.fedoraproject.org/fedora:39

# Install systemd mariadb nginx php-fpm
RUN dnf install -y systemd \
    && dnf clean all

COPY hello_world_service /hello_world_service/hello_world_service
COPY solution /hello_world_service/solutions
COPY hello_world_service/hello-world-v1.sh /usr/local/bin/hello-world
COPY solution/hello-world.service /etc/systemd/system/hello-world.service

# Enable the services
RUN systemctl enable hello-world.service

# Use systemd as command
CMD [ "/usr/sbin/init" ]
