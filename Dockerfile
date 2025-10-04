FROM registry.access.redhat.com/ubi8/ubi

RUN yum install -y java-11-openjdk java-11-openjdk-devel && \
    yum clean all && \
    alternatives --install /usr/bin/java java /usr/lib/jvm/java-11-openjdk/bin/java 200

ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk
ENV PATH=$JAVA_HOME/bin:$PATH

RUN mkdir -p /opt/jboss-eap-7.4

COPY install/eap7 /opt/jboss-eap-7.4
ENV JBOSS_HOME=/opt/jboss-eap-7.4

COPY examples/resteap7/hello-eap7-api/target/hello-eap7-api-1.0.war $JBOSS_HOME/standalone/deployments/

EXPOSE 8080 9990
CMD ["/opt/jboss-eap-7.4/bin/standalone.sh", "-b", "0.0.0.0"]