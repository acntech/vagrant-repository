FROM eclipse-temurin:17-jre

COPY ./target/vagrant-repository-*.jar /app.jar

EXPOSE 8080

CMD [ "java", "-jar", "/app.jar" ]
