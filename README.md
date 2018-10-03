# Todo-List Backend

## Requirements:
- Java installation
- Maven installation
- PostgreSQL-database

## Installing:
1. Clone this repository

2. Build an executable JAR using Maven:
```mvn install -DskipTests```

3. Creating and initializing the database:
   1. Create a PostgreSQL-database with the name ```todolist```
   2. Run the ```create.sql```-file on the database

4. Configuration:
   1. Navigate to src/main/resources
   2. Rename ```application-example.properties``` to ```application.properties```
   3. Edit the file to fill the missing database username and password.
   4. If your PostgreSQL-database is not on localhost you will need to update the database url in the file as well.
   
## Running:
To run the backend navigate to "target" folder and run: ```java -Dserver.port=8082 -jar TodoListBackend-0.5.2-spring-boot.jar```
