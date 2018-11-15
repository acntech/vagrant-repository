FROM acntechie/vagrant-repository-api

RUN wget --no-cookies \
         --no-check-certificate \
         --header "Cookie: oraclelicense=accept-securebackup-cookie" \
         http://download.oracle.com/otn/utilities_drivers/jdbc/122010/ojdbc8.jar \
         -O /docker-java-home/jre/lib/ext/postgresql.jar
