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

ARG SERVER_PORT=8080
ARG APP_DIR=/opt/vagrant-repository

RUN mkdir -p ${APP_DIR}

WORKDIR ${APP_DIR}

COPY --from=0 /tmp/api.war .

EXPOSE 8080:${SERVER_PORT}

CMD [ "java", "-jar", "api.war" ]
