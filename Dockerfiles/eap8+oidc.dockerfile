FROM registry.access.redhat.com/ubi9/ubi

RUN dnf -y install java-21-openjdk java-21-openjdk-devel unzip findutils && dnf clean all

ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk
ENV PATH=$JAVA_HOME/bin:$PATH

COPY install/EAP8-installation-manager/jboss-eap-8.0.0-installation-manager.zip /tmp/im.zip

RUN mkdir -p /opt/eap-im \
 && unzip -q /tmp/im.zip -d /opt/eap-im \
 && rm -f /tmp/im.zip \
 && ln -s "$(ls -d /opt/eap-im/jboss-eap-installation-manager-*/ | head -n1)" /opt/eap-im/current

ENV EAP_IM_HOME=/opt/eap-im/current
ENV JBOSS_HOME=/opt/eap

RUN "$EAP_IM_HOME/bin/jboss-eap-installation-manager.sh" install \
      --profile=eap-8.0 \
      --dir "$JBOSS_HOME" \
      --accept-license-agreements \
  && rm -rf "$JBOSS_HOME/standalone/tmp" "$JBOSS_HOME/standalone/log"

RUN echo "Checking for messaging subsystem..." \
 && grep -r "messaging" "$JBOSS_HOME/standalone/configuration/" || echo "No messaging found"

EXPOSE 8080 9990
CMD ["bash","-lc","$JBOSS_HOME/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0"]