#
# Build
#
FROM openjdk:8

ARG BUILD_REVISION=${SOURCE_COMMIT}

WORKDIR /tmp

COPY . .

RUN ./mvnw clean package --quiet -DskipTests -Dbuild.revision=${BUILD_REVISION} \
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
