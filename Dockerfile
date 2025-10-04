FROM registry.access.redhat.com/ubi8/ubi

RUN yum install -y java-11-openjdk java-11-openjdk-devel unzip && \
    yum clean all && \
    alternatives --install /usr/bin/java java /usr/lib/jvm/java-11-openjdk/bin/java 200

ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk
ENV PATH=$JAVA_HOME/bin:$PATH

ARG EAP_HOME=/opt/jboss-eap-7.4
ENV JBOSS_HOME=${EAP_HOME}

RUN mkdir -p ${EAP_HOME}
COPY install/eap7 ${EAP_HOME}

COPY install/eap7-adapters/rhsso-7.6/rh-sso.zip /tmp/rh-sso.zip

RUN set -eux; \
    rm -rf "${EAP_HOME}/modules/system/add-ons/keycloak" || true; \
    rm -rf "${EAP_HOME}/modules/system/layers/keycloak" || true; \
    unzip -oq /tmp/rh-sso.zip -d "${EAP_HOME}"; \
    rm -f /tmp/rh-sso.zip

COPY configure/keycloak/keycloak-enable.cli /opt/cli/keycloak-enable.cli
RUN ${EAP_HOME}/bin/jboss-cli.sh --file=/opt/cli/keycloak-enable.cli

COPY configure/keycloak/keycloak-resteap7-keycloak.cli /opt/cli/keycloak-resteap7-keycloak.cli

RUN rm -rf ${JBOSS_HOME}/standalone/configuration/standalone_xml_history || true \
 && ${JBOSS_HOME}/bin/jboss-cli.sh --file=/opt/cli/keycloak-resteap7-keycloak.cli


COPY examples/resteap7/keycloak/target/hello-eap7-api-1.0.war \
     ${EAP_HOME}/standalone/deployments/

EXPOSE 8080 9990
CMD ["/opt/jboss-eap-7.4/bin/standalone.sh", "-b", "0.0.0.0"]
