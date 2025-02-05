FROM quay.io/wildfly/wildfly:latest-jdk17
COPY target/image-server.war /opt/jboss/wildfly/standalone/deployments/
