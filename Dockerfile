#
# Build
#
FROM eclipse-temurin:17-jdk

ARG BUILD_REVISION=${SOURCE_COMMIT}

WORKDIR /tmp

COPY . .

RUN ./mvnw clean package --quiet -DskipTests -Dbuild.revision=${BUILD_REVISION} \
    && cp ./target/vagrant-repository-*.jar /tmp/app.jar

#
# Runtime
#
FROM eclipse-temurin:17-jre

RUN mkdir -p /opt/vagrant-repository

WORKDIR /opt/vagrant-repository

COPY --from=0 /tmp/app.jar .

EXPOSE 8080

CMD [ "java", "-jar", "app.jar" ]