# create schema
cd ..
mvn dependency:unpack -Dartifact=org.wildfly:wildfly-dist:8.1.0.Final:zip -DoutputDirectory=.
nohup wildfly-8.1.0.Final/bin/standalone.sh &
mvn dependency:copy -Dartifact=mysql:mysql-connector-java:5.1.34 -DoutputDirectory=wildfly-8.1.0.Final/standalone/deployments/
wildfly-8.1.0.Final/bin/jboss-cli.sh -c "/subsystem=datasources/data-source=AppDS:add(\
driver-name=mysql-connector-java-5.1.34.jar_com.mysql.jdbc.Driver_5_1,\
user-name=$MYSQL_USER,\
password=$MYSQL_PASSWORD,\
connection-url=jdbc:mysql://localhost:3306/test,\
min-pool-size=5,\
max-pool-size=15,\
jndi-name=java:/jdbc/AppDS,\
enabled=true,\
validate-on-match=true,\
valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLValidConnectionChecker,\
exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLExceptionSorter)"
