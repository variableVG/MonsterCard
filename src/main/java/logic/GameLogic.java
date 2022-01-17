package logic;

import DB.DbHandler;
import kotlin.Pair;
import logic.cards.Card;
import lombok.Data;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;


@Data
public class GameLogic {
    private static final int SIZE_OF_PACKAGE = 5;
    private static final int SIZE_OF_DECK = 4;
    private static final int PRICE_PACKAGE = 5;

    //public DummyUserDB dummyDB;
    private DbHandler dbHandler;


    public GameLogic() {
        //dummyDB = new DummyUserDB();
        dbHandler = new DbHandler();
        DbHandler.initDb();
    }


    //USER
    public User createUser(String username, String password) {
        /** returns null if user already exists, otherwise creates a User and returns it*/
        return dbHandler.createUniqueUser(username, password);
    }

    public User loginUser(String username, String password) {
        User user = dbHandler.loginUser(username, password);
        if(user == null) {
            System.out.println("user does not exist or password is incorrect");
        }
        return user;
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


    //CARDS
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
            throw new Exception("A package must contain 5 cards");

        }

        //Just Admin can add packages to the DB. We check that the token belongs to the admin.
        User user = dbHandler.getUserByToken(token);
        if(!user.getUsername().equals("admin")) {
            throw new Exception("User must be admin to create add Packages to the DB");
        }

        //Add cards to the DB:
        if(!dbHandler.addPackageToDB(cards, user.getUsername())) {
            return false;
        }
        return true;
    }

    public JSONArray acquirePackage(String token) throws Exception {
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


    //BATTLE
    public JSONObject startBattle(String token) {
        /** This function calls the startBattle in the DbHandler class. It gives back a pair with the battleId and
         * the opponent's username if there is already an open battle. Otherwise instead of the opponent's username returns
         * an empty string.
         * */
        User user = dbHandler.getUserByToken(token);
        JSONObject battleResult = new JSONObject();

        Battle battle = null;
        Pair<Integer, String> battleData = dbHandler.startBattle(user.getUsername());
        int battleId = battleData.getFirst();
        String opponentUsername = battleData.getSecond();

        if(opponentUsername.isEmpty()) {
            battleResult.put("message", "Battle created with id " + battleId + ". Waiting for opponent to join.");
        }
        else {
            User player1 = dbHandler.getUserByUsername(opponentUsername);
            User player2 = user;
            battle = new Battle(player1, player2, battleId, dbHandler);
            battleResult = battle.battleFight(player1, player2);
            battleResult.put("ELO Score Player " + player1.getUsername(), dbHandler.getUserEloScore(player1.getUsername()));
            battleResult.put("ELO Score Player " + player2.getUsername(), dbHandler.getUserEloScore(player2.getUsername()));
        }
        return battleResult;
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

    //SCOREBOARD
    public HashMap getScoreboard() throws Exception {
        HashMap<String, Integer> scores = dbHandler.getScores();
        return scores;
    }

    //TRADDING
    public String addCardToStore(String cardId, String cardToTrade, String type, int damage, String token) throws Exception {
        User user = dbHandler.getUserByToken(token);
        if (user == null) {
            throw new Exception("User not authorized");
        }
        String answer = "";
        if(dbHandler.addCardToStore(cardId, cardToTrade, type, damage, user.getUsername())) {
            Card cardToOffer = dbHandler.getCardById(cardId);
            Card cardLookedFor = dbHandler.getCardById(cardToTrade);
            answer = "Player " + user.getUsername() + " adds " + cardToOffer.getName() + " ( " +
                    cardToOffer.getDamage() + " damage) in the store and wants " + type +
                    " with min " + damage + " damage.";
        }

        return answer;
    }

    public JSONObject showStore(String token) throws Exception {
        User user = dbHandler.getUserByToken(token);
        if (user == null) {
            throw new Exception("User not authorized");
        }

        HashMap<Integer, HashMap<String, Card>> store = dbHandler.showStore();
        JSONObject answer = new JSONObject();
        String username = "";
        String cardToOfferName = "";
        double cardToOfferDamage = 0 ;
        String cardLookedForType = "";
        double cardLookedForDamage = 0;


        for(int tradeId : store.keySet()) {
            for(String key : store.get(tradeId).keySet()) {
                System.out.println("key is " + key);
                if(store.get(tradeId).get(key) == null) {
                    username = key;
                }
                else if(key.equals("Card to offer")) {
                    cardToOfferName = store.get(tradeId).get(key).getName();
                    cardToOfferDamage = store.get(tradeId).get(key).getDamage();

                }
                else if(key.equals("Card would like to have")) {
                    cardLookedForType = store.get(tradeId).get(key).getName();
                    cardLookedForDamage = store.get(tradeId).get(key).getDamage();
                }
                answer.put("Trading " + String.valueOf(tradeId), "Player " + username + " adds " + cardToOfferName + " ( " +
                        cardToOfferDamage + " damage) in the store and wants " + cardLookedForType +
                        " with min " + cardLookedForDamage + " damage.");

            }


        }

        System.out.println(answer);
        return answer;
    }

    public String tradeCard(String cardIwantId, String cardIofferId, String token) throws Exception {
        String answer = "";
        User user = dbHandler.getUserByToken(token);
        if (user == null) {
            throw new Exception("User not authorized");
        }

        if(!dbHandler.doesCardBelongsToUser(user.getUsername(), cardIofferId)) {
            throw new Exception("User " + user.getUsername() + " cannot trade this card, It does not belong to him.");
        }

        User user2 = dbHandler.getUserByCard(cardIwantId);
        if(user.getUsername().equals(user2.getUsername())) {
            throw new Exception("You cannot trade cards with yourself!");
        }

        dbHandler.deleteCardFromUser(user.getUsername(), cardIofferId);
        dbHandler.addCardToUser(user.getUsername(), cardIwantId);

        dbHandler.deleteCardFromUser(user2.getUsername(), cardIwantId);
        dbHandler.addCardToUser(user2.getUsername(), cardIofferId);

        Card cardIoffer = dbHandler.getCardById(cardIofferId);
        Card cardIwant = dbHandler.getCardById(cardIwantId);

        answer = "Player " + user.getUsername() + " accepts trade with " + user2.getUsername() +" and trades card" +
            cardIoffer.getName() + " (damage " + cardIoffer.getDamage() + ") for card " + cardIwant.getName() +
            " (damage " + cardIwant.getDamage() + ").";

        return answer;
    }

    public String deleteCardFromStore(String token, String cardId) throws Exception {
        User user = dbHandler.getUserByToken(token);
        String answer = "";

        if (user == null) {
            throw new Exception("User not authorized");
        }

        if(!dbHandler.doesCardBelongsToUser(user.getUsername(), cardId)) {
            throw new Exception("Card does not belong to user");
        }

        dbHandler.deleteCardFromStore(cardId);
        answer = "card " + cardId + " from store deleted.";
        return answer;

    }

    //UNIQUE FEATURES
    public String sellCard(String token, String cardId) throws Exception {
        String answer = "";
        User user = dbHandler.getUserByToken(token);
        if (user == null) {
            throw new Exception("User not authorized");
        }

        if(!dbHandler.doesCardBelongsToUser(user.getUsername(), cardId)) {
            throw new Exception("User " + user.getUsername() + " cannot trade this card, It does not belong to him.");
        }

        dbHandler.deleteCardFromStore(cardId);
        int actualCoins = user.getCoins() + 5;
        dbHandler.setCoins(user.getUsername(), actualCoins);

        answer = "Card " + cardId + " has been sold, you now have " + actualCoins + " coins.";

        return answer;
    }

    public int getCoins(String token) throws Exception {
        User user = dbHandler.getUserByToken(token);
        if (user == null) {
            throw new Exception("User not authorized");
        }

        return dbHandler.getUserCoins(user.getUsername());

    }

}
