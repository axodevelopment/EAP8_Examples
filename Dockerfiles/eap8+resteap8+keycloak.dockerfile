FROM registry.access.redhat.com/ubi9/ubi

RUN dnf -y install java-21-openjdk java-21-openjdk-devel unzip findutils && dnf clean all

ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk
ENV PATH=$JAVA_HOME/bin:$PATH

RUN mkdir -p /opt/eap-im/current
RUN mkdir -p /opt/eap-config/keycloak

# I've downloaded into my local install directory the EAP8 installation manager
COPY install/eap8 /opt/eap-im/current
COPY configure/keycloak/eap8-oidc-eap8-api.cli /opt/eap-config/keycloak/keycloak.cli

ENV EAP_IM_HOME=/opt/eap-im/current
ENV JBOSS_HOME=/opt/eap

RUN "$EAP_IM_HOME/bin/jboss-eap-installation-manager.sh" install \
      --profile=eap-8.0 \
      --dir "$JBOSS_HOME" \
      --accept-license-agreements \
  && rm -rf "$JBOSS_HOME/standalone/tmp" "$JBOSS_HOME/standalone/log"

RUN "$JBOSS_HOME/bin/jboss-cli.sh" --file="/opt/eap-config/keycloak/keycloak.cli"

COPY examples/resteap8/keycloak/target/eap8-api.war $JBOSS_HOME/standalone/deployments/

EXPOSE 8080 9990
CMD ["bash","-lc","$JBOSS_HOME/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0"]