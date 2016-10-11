FROM alpine:3.3
MAINTAINER andrew.clark@incapturtechnologies.com
RUN apk --update add bash tar openjdk8-jre
COPY build/install/DataHub /opt/rapture/DataHub
COPY docker/config /opt/rapture/DataHub/etc/rapture/config
ENV RAPTURE_CONFIG_HOME /opt/rapture/DataHub/etc/rapture/config
WORKDIR /opt/rapture/DataHub/bin
EXPOSE 4568
ENTRYPOINT [ "/bin/bash", "/opt/rapture/DataHub/bin/DataHub" ]

