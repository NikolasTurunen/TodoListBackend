Requirements:
- PostgreSQL-database (other SQL-databases might work but are not tested)

Instructions:

Setting up the property files:
1. Create new files named "application.properties" in the folders "src/main/resources/" and "src/test/resources/"
2. Copy the contents from the file "application-example.properties" found in the same folders as the newly created files.
3. Fill username and password in the newly created files.
4. (Optional) Change the url of the database. Make sure that both property-files are not pointing to the same database because the database is going to be wiped during test runs.

Setting up the database:
1. Create the databases as configured in the property files.
2. Run create.sql on both databases.

Now all tests should pass and the backend should be ready to be started.

Starting the backend:
1. Open the project in NetBeans-IDE.
2. Run the project.