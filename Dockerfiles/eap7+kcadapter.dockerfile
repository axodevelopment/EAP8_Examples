FROM registry.access.redhat.com/ubi8/ubi

# Install Java 11
RUN yum install -y java-11-openjdk java-11-openjdk-devel && \
    yum clean all && \
    alternatives --install /usr/bin/java java /usr/lib/jvm/java-11-openjdk/bin/java 200

# Find JAVA_HOME dynamically
ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk
ENV PATH=$JAVA_HOME/bin:$PATH

# Copy EAP installation from extracted ZIP
COPY install/jboss-eap-7.4 /opt/jboss-eap-7.4
ENV JBOSS_HOME=/opt/jboss-eap-7.4

# Deploy the application
COPY labs/eap7-lab/hello-eap7-api/target/hello-eap7-api-1.0.war $JBOSS_HOME/standalone/deployments/

EXPOSE 8080 9990
CMD ["/opt/jboss-eap-7.4/bin/standalone.sh", "-b", "0.0.0.0"]