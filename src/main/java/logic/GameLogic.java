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
import java.util.HashMap;
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
        //Check if user exists:
        if (user == null) {
            throw new Exception("User does not exist");
        }

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

    public JSONArray getUserCards(String token) throws Exception {
        User user = dbHandler.getUserByToken(token);

        if (user == null) {
            throw new Exception("User does not exist");
        }

        //Get Cards:
        Collection<Card> cardPackage = dbHandler.getUserCards(user.getUsername());
        if(cardPackage.isEmpty()) {
            throw new Exception("User has no cards");
        }

        //Convert Array in Json to answer back:
        JSONArray cardsInJson = new JSONArray(cardPackage);
        return cardsInJson;
    }

    public JSONArray showUserDeck(String token) throws Exception {
        User user = dbHandler.getUserByToken(token);

        if (user == null) {
            throw new Exception("User does not exist");
        }

        //Get Cards:
        Collection<Card> cardPackage = dbHandler.showUserDeck(user.getUsername());
        if(cardPackage.isEmpty()) {
            throw new Exception("Deck is not configured or empty");
        }

        //Convert Array in Json to answer back:
        JSONArray cardsInJson = new JSONArray(cardPackage);
        return cardsInJson;
    }

    public Battle startBattle(User user) {
        //startBattle() initializes the battle constructor. The battle itself takes place inside the Battle class.
        Battle battle = new Battle(user);
        return battle;
    }

    public boolean configureDeck(JSONArray cardsInJson, String token) throws Exception {
        //Check length of cardsInJson (should be 4).
        System.out.println("Card in configureDeck are " + cardsInJson);
        if(cardsInJson.isEmpty() || cardsInJson.length() == 0) {
            throw new Exception("No cards selected for deck");
        }
        else if(cardsInJson.length() != 4) {
            throw new Exception("A Deck should have 4 cards but user has selected " + cardsInJson.length() + " cards.");
        }

        //Check if the cards really belonged to the user.
        User user = dbHandler.getUserByToken(token);
        System.out.println("User is " + user.getUsername());
        for (Object o : cardsInJson) {
            // the Object o corresponds to the card_id.
            if(!dbHandler.doesCardBelongsToUser(user.getUsername(), o.toString())) {
                throw new Exception("Card " + o.toString() + " does not belong to " + user.getUsername());
            }
        }

        //Delete previous deck
        dbHandler.deleteDeckFromUser(user.getUsername());

        //Add new deck
        for(Object o : cardsInJson) {
            dbHandler.addCardToDeck(user.getUsername(), o.toString());
        }

        return true;

    }

    public JSONObject getUserData(String username, String token) throws Exception{
        //Check Authorization:
        User user = dbHandler.getUserByToken(token);

        if(user.getUsername().equals(username)) {
            JSONObject jsonUser = new JSONObject();
            jsonUser.put("username", user.getUsername());
            jsonUser.put("coins", user.getCoins());
            jsonUser.put("cards", user.getStack());
            jsonUser.put("deck", user.getDeck());
            return jsonUser;
        }
        return null;
    }

    public boolean updateUser(String username, String token, JSONObject updateContent) throws Exception {
        //Check Authorization:
        User user = dbHandler.getUserByToken(token);

        if(!user.getUsername().equals(username)) {
            throw new Exception("Token and user do not match");
        }
        for(String key : updateContent.keySet()) {
            Object value = updateContent.get(key);
            if(!dbHandler.updateUser(key.toLowerCase(), value, user.getUsername())) {
                return false;
            }

        }
        return true;
    }

    public int getEloScore(String token) throws Exception {
        User user = dbHandler.getUserByToken(token);
        int elo = -1;
        if (user == null) {
            throw new Exception("Token does not exists");
        }
        else {
            elo = dbHandler.getUserEloScore(user.getUsername());
        }
        return elo;
    }

    public HashMap getScoreboard() throws Exception {
        HashMap<String, Integer> scores = dbHandler.getScores();
        return scores;
    }



}
