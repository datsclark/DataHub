FROM alpine:3.3
MAINTAINER andrew.clark@incapturetechnologies.com
RUN apk --update add bash tar openjdk8-jre
COPY build/install/DataHubPlugin /opt/rapture/DataHubPlugin
WORKDIR /opt/rapture/DataHubPlugin/bin
COPY docker/feat.sh /tmp/feat.sh
ENTRYPOINT [ "/tmp/feat.sh" ]
