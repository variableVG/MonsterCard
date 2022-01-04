package logic;

import DB.DbHandler;
import dummyDB.DummyUserDB;
import logic.cards.Card;
import lombok.Data;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collector;


@Data
public class GameLogic {
    private static final int SIZE_OF_PACKAGE = 5;
    private static final int SIZE_OF_DECK = 4;
    private static final int PRICE_PACKAGE = 5;

    public DummyUserDB dummyDB;
    public DbHandler dbHandler;


    public GameLogic() {
        dummyDB = new DummyUserDB();
        dbHandler = new DbHandler();
    }


    //user
    public User createUser(String username, String password) {
        return dbHandler.createUniqueUser(username, password);

    }

    public User loginUser(String username, String password) {
        User user = dbHandler.loginUser(username, password);
        if(user == null) {
            System.out.println("user does not exist or password is incorrect");
        }
        return user;
    }

    public boolean addPackageToDB(JSONArray cardsInJson, String token) throws Exception {
        //dummyDB.cardPackages.add(cards);

        //parse from JSONArray to Array:
        //https://stackoverflow.com/questions/1568762/accessing-members-of-items-in-a-jsonarray-with-java
        ArrayList<Card> cards = new ArrayList<Card>();
        if (cardsInJson != null && cardsInJson.length() == 5) {
            for(int i = 0; i < cardsInJson.length(); i++) {
                JSONObject j = cardsInJson.getJSONObject(i);
                //Cards have the form: {"Id":"845f0dc7-37d0-426e-994e-43fc3ac83c08","Damage":10,"Name":"WaterGoblin"}
                Card card = new Card(j.getString("Id"), j.getString("Name"), j.getDouble("Damage"));
                cards.add(card);
            }
        }
        else {
            System.out.println("A package must contain 5 cards");
            return false;
        }

        //Just Admin can add packages to the DB. We check that the token belongs to the admin.
        User user = dbHandler.getUserByToken(token);
        if(!user.getUsername().equals("admin")) {
            System.out.println("User must be admin to create add Packages to the DB");
            return false;
        }

        //Add cards to the DB:
        dbHandler.addPackageToDB(cards, user.getUsername());
        return true;
    }

    public Object acquirePackage(String token) throws Exception {
        //Get user:
        User user = dbHandler.getUserByToken(token);

        //Check if user has money to buy the cards
        if(user.getCoins() < 5) {
            throw new Exception("You don't have enough coin");
        }

        //Get Cards:
        Collection<Card> cardPackage = dbHandler.acquirePackage();
        if(cardPackage.isEmpty()) {
            throw new Exception("There are no packages left");
        }

        //Add Cards to User in DB:
        for(Card card : cardPackage) {
            if(!dbHandler.addCardToUser(user.getUsername(), card.getId())) {
                throw new Exception("Card " + card.getId() + " could not be added to the user");
            }
        }

        //take the money
        user.setCoins(user.getCoins() - PRICE_PACKAGE);
        //set money in the DB:
        if(!dbHandler.setCoins(user.getUsername(), user.getCoins() )) {
            System.out.println("Problem setting the coins");
        }

        //Convert Array in Json to answer back:
        JSONArray cardsInJson = new JSONArray(cardPackage);
        return cardsInJson;
    }

    public Battle startBattle(User user1, User user2) {
        //startBattle() initializes the battle constructor. The battle itself takes place inside the Battle class.
        Battle battle = new Battle(user1, user2);
        return battle;
    }
}
