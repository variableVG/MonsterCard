package DB;

import logic.User;
import logic.cards.Card;
import logic.cards.MonsterCard;
import logic.cards.SpellCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DbHandlerTest {
    DbHandler dbHandler;

    @BeforeEach
    void setup() {
        dbHandler = new DbHandler();
    }

    @Test
    void initDBTest() throws Exception{
        dbHandler.initDb();
    }

    @Test
    void getUsersTest() throws Exception {
        dbHandler.getUsers();
    }

    @Test
    void createUniqueUser() throws Exception {
        dbHandler.createUniqueUser("vio", "1234");
        dbHandler.createUniqueUser("user2", "22222");
        dbHandler.createUniqueUser("vio", "34456");
        dbHandler.createUniqueUser("altenhof", "markus");
    }

    @Test
    void loginUserTest() throws Exception {
        //Check if user with wrong username and password can log-in
        assertTrue(dbHandler.loginUser("abc", "123") == null, "Not registered user could login");

        //Check if user with correct username and wrong password can log-in
        assertTrue(dbHandler.loginUser("kienboec", "Daniel2") == null , "User with wrong password could login");

        //Check if user with correct username and password can log-in
        User user = dbHandler.createUniqueUser("altenhof", "markus");
        assertTrue(dbHandler.loginUser("altenhof", "markus").equals(user), "User with correct password and username could not login");


    }

    @Test
    void getUserByTokenTest() throws Exception {
        dbHandler.createUniqueUser("admin", "istrator");
        User user;
        user = dbHandler.loginUser("admin", "istrator");

        assertTrue(dbHandler.getUserByToken(user.getToken()).equals(user), "user with incorrect token has been given");

    }

    @Test
    void addPackageToDBTest() throws Exception {
        Card card1 = new MonsterCard("a", "WaterGoblin", 10.0);
        Card card2 = new MonsterCard("b", "Dragon", 50.0);
        Card card3 = new SpellCard("c", "WaterSpell", 20.0);
        Card card4 = new MonsterCard("d", "Ork", 45.0);
        Card card5 = new SpellCard("e", "FireSpell", 25.0);
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(card1); cards.add(card2); cards.add(card3); cards.add(card4); cards.add(card5);

        dbHandler.addPackageToDB(cards, "admin");

    }

    @Test void acquirePackageTest() {
        Collection<Card> cards = new ArrayList<>();
        cards = dbHandler.acquirePackage();

        for(Card card : cards) {
            System.out.println(card.getName());
        }
    }

    @Test void getUserCardsTest() {
        Collection<Card> cards = new ArrayList<>();
        cards = dbHandler.getUserCards("altenhof");

        for(Card card : cards) {
            System.out.println(card.getName() + " with Id " + card.getId() + " and damage " + card.getDamage());
        }

    }

    @Test
    void showUserDeck() throws Exception {
        User user = dbHandler.createUniqueUser("altenhof", "markus");
        //addPackageToDBTest();
        Collection<Card> cards = new ArrayList<>();
        cards = dbHandler.acquirePackage();
        for(Card card : cards) {
            dbHandler.addCardToUser("altenhof", card.getId());
        }

        dbHandler.showUserDeck("altenhof");

    }

    @Test
    void getUserEloScore()throws Exception {
        User user1 = dbHandler.getUserByToken("Basic kienboec-mtcgToken");
        User user2 = dbHandler.getUserByToken("Basic altenhof-mtcgToken");

        int elo1 = dbHandler.getUserEloScore(user1.getUsername());
        int elo2 = dbHandler.getUserEloScore(user2.getUsername());

        System.out.println("Elo scores are " + elo1 + " and " + elo2);

    }

    @Test
    void getScoreboard() {
        HashMap<String, Integer> scores = dbHandler.getScores();

        Iterator iterator = scores.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry pair = (Map.Entry)iterator.next();
            System.out.println(pair.getKey() + ", " + pair.getValue());
        }
    }
}
