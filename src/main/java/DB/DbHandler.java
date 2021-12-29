package DB;

import java.sql.Connection;
import java.sql.SQLException;

public class DbHandler {
    /**
     * initializes the database with its tables
     */
    // PostgreSQL documentation: https://www.postgresqltutorial.com/postgresql-create-table/
    public static void initDb() {
        // re-create the database
        try (Connection connection = DbConnection.getInstance().connect("")) {
            DbConnection.executeSql(connection, "DROP DATABASE monsterCard", true );
            DbConnection.executeSql(connection,  "CREATE DATABASE monsterCard", true );
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // create the table
        // PostgreSQL documentation: https://www.postgresqltutorial.com/postgresql-create-table/
        String dbSentence = "CREATE TABLE IF NOT EXISTS users (\n" +
                "                    username VARCHAR(50) NOT NULL,\n" +
                "                    password VARCHAR(50) NOT NULL)";
        try {
            DbConnection.getInstance().executeSql(dbSentence);

        } catch (SQLException throwables) {

            throwables.printStackTrace();
        }
    }

}
