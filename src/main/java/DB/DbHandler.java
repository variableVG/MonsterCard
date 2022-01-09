package DB;
import kotlin.Pair;
import logic.User;
import logic.cards.Card;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

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
                "                    username VARCHAR(50) NOT NULL PRIMARY KEY,\n" +
                "                    password VARCHAR(50) NOT NULL, \n" +
                "                    coins NUMERIC DEFAULT 20, \n" +
                "                    token VARCHAR(50), \n" +
                "                    name VARCHAR(50), \n" +
                "                    bio VARCHAR(50), \n" +
                "                    image VARCHAR(50),   \n" +
                "                    elo_score NUMERIC DEFAULT 100, \n" +
                "                    UNIQUE(username, token) " +
                "                    ); " +
                "           CREATE TABLE IF NOT EXISTS cards (\n" +
                "                   card_id VARCHAR(50) NOT NULL PRIMARY KEY, \n" +
                "                   name VARCHAR(50) NOT NULL, \n" +
                "                   damage NUMERIC,\n" +
                "                   UNIQUE(card_id)\n" +
                "                   );" +
                "           CREATE TABLE IF NOT EXISTS packages (\n" +
                "                   package_id SERIAL PRIMARY KEY, \n" +
                "                   author VARCHAR(50), \n" +
                "                   UNIQUE(package_id)" +
                "                   );" +
                "           CREATE TABLE IF NOT EXISTS packages_cards (" +
                "                   package_id INT REFERENCES packages (package_id) ON UPDATE CASCADE ON DELETE CASCADE, \n" +
                "                   card_id VARCHAR(50) REFERENCES cards (card_id) ON UPDATE CASCADE ON DELETE CASCADE" +
                "                   );" +
                "           CREATE TABLE IF NOT EXISTS users_cards (" +
                "                   username VARCHAR(50) REFERENCES users (username) ON UPDATE CASCADE ON DELETE CASCADE," +
                "                   card_id VARCHAR(50) REFERENCES cards (card_id) ON UPDATE CASCADE ON DELETE CASCADE" +
                "                   );" +
                "           CREATE TABLE IF NOT EXISTS deck_cards (" +
                "                   username VARCHAR(50) REFERENCES users(username) ON UPDATE CASCADE ON DELETE CASCADE," +
                "                   card_id VARCHAR(50) REFERENCES cards (card_id) ON UPDATE CASCADE ON DELETE CASCADE);" +
                "                    " +
                "           CREATE TABLE IF NOT EXISTS battles ( " +
                "                   battle_id SERIAL PRIMARY KEY," +
                "                   player1 VARCHAR(50) NOT NULL REFERENCES users(username) ON UPDATE CASCADE ON DELETE CASCADE, " +
                "                   player2 VARCHAR(50) REFERENCES users(username) ON UPDATE CASCADE ON DELETE CASCADE," +
                "                   winner VARCHAR(50)); \n" +
                "           ";
        try {
            DbConnection.getInstance().executeSql(dbSentence);

        } catch (SQLException throwables) {

            throwables.printStackTrace();
        }
    }

    public Collection<User> getUsers() {
        ArrayList<User> result = new ArrayList<>();
        String sqlStatement = "SELECT * FROM users";
        try ( PreparedStatement statement = DbConnection.getInstance().prepareStatement(sqlStatement)
        ) {
            ResultSet resultSet = statement.executeQuery();
            while( resultSet.next() ) {
                System.out.println(resultSet.getString("username"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public User createUniqueUser(String username, String password) {
        User user = null;
        //Check if Username is already present:
        String sqlStatement = "SELECT username FROM users WHERE username = ?";
        try ( PreparedStatement statement = DbConnection.getInstance().prepareStatement(sqlStatement)
        ) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) { // if username does not exist, create the username
                String sqlStatement2 = "INSERT INTO users (username, password, coins) VALUES(?, ?, ?);";
                try ( PreparedStatement statement2 = DbConnection.getInstance().prepareStatement(sqlStatement2)) {
                    statement2.setString(1, username );
                    statement2.setString( 2, password );
                    statement2.setInt(3, 20 );
                    statement2.execute();
                }
                user = User.builder()
                        .username(username)
                        .password(password)
                        .coins(20)
                        .stack(new ArrayList<Card>())
                        .deck(new ArrayList<Card>())
                        .build();
            }
            else {
                while( resultSet.next() ) {
                    //System.out.println(resultSet.getString("username"));
                }
                System.out.println("User is already present");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return user;
    }

    public User loginUser(String username, String password) {
        User user = null;
        //Check if Username is already present:
        String sqlStatement = "SELECT * FROM users WHERE username = ? AND password = ?";
        try ( PreparedStatement statement = DbConnection.getInstance().prepareStatement(sqlStatement)
        ) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) { // if username does not exist
                System.out.println("User does not exist");
            }
            else {  //if user exists.
                //System.out.println(resultSet.getString("username"));
                //System.out.println(resultSet.getString("password"));
                //System.out.println(resultSet.getInt("coins"));

                //generate random string as a token
                String token = "Basic " + resultSet.getString("username") + "-mtcgToken";
                user = User.builder()
                        .username(resultSet.getString("username"))
                        .password(resultSet.getString("password"))
                        .coins(resultSet.getInt("coins"))
                        .token(token)
                        .stack(new ArrayList<Card>()) // TODO: Cards
                        .deck(new ArrayList<Card>()) //TODO: Cards
                        .build();

                //Set token inn the DB:
                setToken(user.getUsername());

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return user;
    }

    public boolean setToken(String username) {
        String token = "Basic " + username + "-mtcgToken";
        String sqlStatement = "UPDATE users SET token = ? WHERE username = ?";
        try ( PreparedStatement statement = DbConnection.getInstance().prepareStatement(sqlStatement)
        ) {
            statement.setString(1, token);
            statement.setString(2, username);
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        return true;
    }

    public User getUserByToken(String token) {
        User user = null;
        String sqlStatement = "SELECT * FROM users WHERE token = ?";

        try ( PreparedStatement statement = DbConnection.getInstance().prepareStatement(sqlStatement)
        ) {
            statement.setString(1, token);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) { // if username does not exist
                throw new Exception("Token does not exist in the DB");
            }
            else {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                Integer coins = resultSet.getInt("coins");
                //getCards:
                ArrayList<Card> cardsInStack = getUserCards(username);
                ArrayList<Card> cardsInDeck = (ArrayList<Card>) showUserDeck(username);

                user = User.builder()
                        .username(username)
                        .password(password) // maybe password should not be included
                        .coins(coins)
                        .token(token)
                        .stack(cardsInStack)
                        .deck(cardsInDeck)
                        .build();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    public boolean addPackageToDB(ArrayList<Card> cards, String author) throws Exception {

        //Create entry in packages-table and get package ID
        String sqlStatement = "INSERT INTO packages(author) VALUES(?) RETURNING package_id";
        int packageID = 0;
        try ( PreparedStatement statement = DbConnection.getInstance().prepareStatement(sqlStatement)
        ) {
            statement.setString(1, author);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            packageID = resultSet.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

        //Add cards in cards-table
        for (Card card : cards) {
            sqlStatement = "INSERT INTO cards(card_id, name, damage) VALUES(?, ?, ?);";
            try ( PreparedStatement statement = DbConnection.getInstance().prepareStatement(sqlStatement)
            ) {
                statement.setString(1, card.getId());
                statement.setString(2, card.getName());
                statement.setDouble(3, card.getDamage());
                statement.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return false;
            }

            //Associate cards with packages
            sqlStatement = "INSERT INTO packages_cards(package_id, card_id) VALUES(?, ?);";
            try ( PreparedStatement statement = DbConnection.getInstance().prepareStatement(sqlStatement)
            ) {
                statement.setInt(1, packageID);
                statement.setString(2, card.getId());
                statement.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return false;
            }
        }

        return true;
    }

    public Collection<Card> acquirePackage() {
        ArrayList<Card> cards = new ArrayList<>();
        int packageId = 0;
        String sqlStatement = "SELECT * FROM cards JOIN packages_cards USING(card_id) " +
                            "WHERE package_id = (SELECT MIN (package_id) FROM packages_cards); ";

        try ( PreparedStatement statement = DbConnection.getInstance().prepareStatement(sqlStatement)
        ) {
            ResultSet resultSet = statement.executeQuery();

            while( resultSet.next() ) {
                Card card = new Card(resultSet.getString("card_id"), resultSet.getString("name"), resultSet.getDouble("damage"));
                cards.add(card);
                packageId = resultSet.getInt("package_id");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //delete package:
        deletePackage(packageId);

        return cards;
    }

    public boolean addCardToUser(String username, String card_id) {
        String sqlStatement = "INSERT INTO users_cards(username, card_id) VALUES(?, ?);";

        try ( PreparedStatement statement = DbConnection.getInstance().prepareStatement(sqlStatement)
        ) {
            statement.setString(1, username);
            statement.setString(2, card_id);
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean deletePackage(int packageId) {
        String sqlStatement = "DELETE from packages where package_id = ?; " +
                "       DELETE from packages_cards where package_id = ?;";

        try ( PreparedStatement statement = DbConnection.getInstance().prepareStatement(sqlStatement)
        ) {
            statement.setInt(1, packageId);
            statement.setInt(2, packageId);
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

        return true;

    }

    public boolean setCoins(String username, int coins) {
        String sqlStatement = "UPDATE users SET coins = ? WHERE username = ?";
        try ( PreparedStatement statement = DbConnection.getInstance().prepareStatement(sqlStatement)
        ) {
            statement.setInt(1, coins);
            statement.setString(2, username);
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        return true;
    }

    public ArrayList<Card> getUserCards(String username) {
        ArrayList<Card> cards = new ArrayList<>();

        String sqlStatement = "SELECT * FROM cards JOIN users_cards USING(card_id)\n" +
                "WHERE username = ?; ";

        try ( PreparedStatement statement = DbConnection.getInstance().prepareStatement(sqlStatement)
        ) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            while( resultSet.next() ) {
                Card card = new Card(resultSet.getString("card_id"), resultSet.getString("name"), resultSet.getDouble("damage"));
                cards.add(card);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return cards;
    }

    public Collection<Card> showUserDeck(String username) {
        ArrayList<Card> cards = new ArrayList<>();

        String sqlStatement = "SELECT * FROM cards JOIN deck_cards USING(card_id)\n" +
                "WHERE username = ?; ";

        try ( PreparedStatement statement = DbConnection.getInstance().prepareStatement(sqlStatement)
        ) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            while( resultSet.next() ) {
                Card card = new Card(resultSet.getString("card_id"), resultSet.getString("name"), resultSet.getDouble("damage"));
                cards.add(card);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return cards;
    }

    public boolean doesCardBelongsToUser(String username, String cardId) {
        String user = "";
        String sqlStatement = "SELECT username FROM users_cards WHERE card_id = ?";
        try ( PreparedStatement statement = DbConnection.getInstance().prepareStatement(sqlStatement)
        ) {
            statement.setString(1, cardId);
            ResultSet resultSet = statement.executeQuery();

            if( resultSet.next() ) {
                user = resultSet.getString("username");
                if(!user.equals(username)) {
                    System.out.println(cardId + " does not belong to user " + username + "but to user " + user);
                    return false;
                }
            }
            else {
                System.out.println("This card is not in the database");
                return false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }

    public void deleteDeckFromUser(String username) {
        String sqlStatement = "DELETE FROM deck_cards WHERE username = ?";
        try ( PreparedStatement statement = DbConnection.getInstance().prepareStatement(sqlStatement)
        ) {
            statement.setString(1, username);
            statement.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addCardToDeck(String username, String cardId) {
        String sqlStatement = "INSERT INTO deck_cards(username, card_id) VALUES(?, ?)";
        try ( PreparedStatement statement = DbConnection.getInstance().prepareStatement(sqlStatement)
        ) {
            statement.setString(1, username);
            statement.setString(2, cardId);
            statement.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public boolean updateUser(String key, Object value, String username) {
        String sqlStatement = "UPDATE users SET " + key + "= ? WHERE username = ?";
        try ( PreparedStatement statement = DbConnection.getInstance().prepareStatement(sqlStatement)
        ) {
            statement.setString(1, value.toString());
            statement.setString(2, username);
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        return true;
    }

    public int getUserEloScore(String username) {
        int eloScore = -1;
        String sqlStatement = "SELECT elo_score FROM users WHERE username = ?";
        try ( PreparedStatement statement = DbConnection.getInstance().prepareStatement(sqlStatement)
        ) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                eloScore = resultSet.getInt("elo_score");
            }
            else{
                throw new Exception("User score not found");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eloScore;
    }

    public HashMap getScores(){
        HashMap<String, Integer> scores = new HashMap<String, Integer>();
        String sqlStatement = "SELECT username, elo_score FROM users ORDER BY elo_score DESC";
        try ( PreparedStatement statement = DbConnection.getInstance().prepareStatement(sqlStatement)
        ) {
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                scores.put(resultSet.getString("username"), resultSet.getInt("elo_score"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return scores;
    }

    public Pair startBattle(String username) {
        /** This function checks if there is already an open battle, if that is the case, it adds the username as player2.
         *  and returns the battle_id and username of opponent. If a battle has not been open yet, it will open a new one. It returns
         *  an empty string then.
         **/

        //This pair contains the battle_id (integer) and the opponent's username.
        Pair<Integer, String> pair = null;
        int battleId = 0;
        String opponentUsername = "";

        String sqlStatement = "SELECT * FROM battles WHERE player2 IS NULL ORDER BY battle_id DESC";
        try ( PreparedStatement statement = DbConnection.getInstance().prepareStatement(sqlStatement)
        ) {
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next()) { //If there is not an open battle, create a new one.
                String sqlStatement2 = "INSERT INTO battles(player1) VALUES(?) RETURNING battle_id;";
                try(PreparedStatement statement2 = DbConnection.getInstance().prepareStatement(sqlStatement2)) {
                    statement2.setString(1, username);
                    ResultSet resultSet2 = statement2.executeQuery();
                    resultSet2.next(); //I need to call next because message error: Das ResultSet ist nicht richtig positioniert. Eventuell muss 'next' aufgerufen werden.
                    battleId = resultSet2.getInt("battle_id");
                }
            }
            else {
                do { //If there is already an open battle, insert username as player2 and get username from opponent.
                    battleId = resultSet.getInt("battle_id");
                    opponentUsername = resultSet.getString("player1");
                    if(!opponentUsername.equals(username)) { // to avoid that a User plays with itself
                        String sqlStatement2 = "UPDATE battles SET player2 = ? WHERE battle_id = ?;";
                        try(PreparedStatement statement2 = DbConnection.getInstance().prepareStatement(sqlStatement2)) {
                            statement2.setString(1, username);
                            statement2.setInt(2, battleId);
                            statement2.execute();
                        }
                        break;
                    }
                } while(resultSet.next());
            }

            pair = new Pair<>(battleId, opponentUsername);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return pair;
    }

}
