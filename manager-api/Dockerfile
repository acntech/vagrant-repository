FROM openjdk:8

ARG SERVER_PORT=8080
ARG APP_DIR=/opt/vagrant-repository-manager

WORKDIR /tmp

COPY . .

RUN ./mvnw clean package -DskipTests \
 && mkdir -p ${APP_DIR} \
 && cp ./api/target/vagrant-repository-manager-api.war ${APP_DIR}/api.war \
 && rm -rf /tmp/* \
 && rm -rf ~/.m2

WORKDIR ${APP_DIR}

EXPOSE 8080:${SERVER_PORT}

CMD [ "java", "-jar", "api.war" ]