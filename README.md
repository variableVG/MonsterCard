# Monster Trading Card Game

* Author: Violeta Garcia Espin
* URL to git repository: [https://github.com/variableVG/MonsterCard](https://github.com/variableVG/MonsterCard)

## Getting started
1. Start a PostgresDB instance. Configure the username and password in
   `src/main/java/DB/DbConfig.java`.
2. Start the REST server by running `src/main/java/server/RestServer.java`.
   This listens on port 10001.
3. Now the requests can be sent using the CURL script
   `MonsterTradingCards.exercise.curl.bat`.

## Technical Steps
* Preliminary logic was built, following the course material
  (e.g. card classes, battle logic, etc). 
* Subsequently, the design worked with a dummy db (non-persistent) using
  arrays, hashmaps, etc.
* According to instructions in class, a REST server was set up. Functions 
  were tested using UnitTests, since no frontend application was developed. 
* Finally, integration with a persistent storage (PostgresDB) was added.

### Design
The application uses a central `GameLogic` class that provides the interface
for the REST server. The `DbHandler` is initialized on `GameLogic`-startup and
handles all database queries. Exceptions are caught and sent to the client
wrapped in JSON objects with appropriate error messages.

Check the UML-Diagrams for more details. 

### Unit Test Design
A unit-test was planned for each function in the `GameLogic` and/or
`DbHandler` classes. These check proper behaviour of the implementations, e.g., 
that the appropriate amount of coins is deducted when purchasing a package.
Currently, not all functions come with their proper unit-test, due to time constraints.

## Time Spent
Please refer to the attached spreadsheet detailing the time spent.