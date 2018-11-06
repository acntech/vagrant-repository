#
# Build
#
FROM openjdk:8

WORKDIR /tmp

COPY . .

RUN ./mvnw clean package --quiet -DskipTests \
    && cp ./web/target/vagrant-repository-web.war ./api.war

#
# Runtime
#
FROM openjdk:8-jre

RUN mkdir -p /opt/vagrant-repository

WORKDIR /opt/vagrant-repository

COPY --from=0 /tmp/api.war .

EXPOSE 8080

CMD [ "java", "-jar", "api.war" ]
