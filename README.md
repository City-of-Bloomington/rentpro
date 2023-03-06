# rentpro

this app handles rental permits by city housing department. adding new rental records, inspections, 
billing and violatiotions.

## Introduction
1- You need to create the database, use the db_tables.sql in docs folder to create one and provided the needed category types for lookup tables;

2- use mvn to create a war file, such as
mvn clean package

3- Create a folder on the server that is hosting the app to include log4j2.xml file
an example of log4j2.xml files is in ./docs directory

4 - add the promt.xml file to your tomcat that is running your vm machine and placed 
in /etc/tomcat?/Catalina/localhost/
tomcat? could be tomcat8, tomcat9, etc
an example of promt.xml in the ./docs folder, needed for CAS login or openid login

5- pom.xml files contains a number of parameters that need to be set, it is divided
into sections such CAS related, openid related, database, etc





 