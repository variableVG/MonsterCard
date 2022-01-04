package DB;

import logic.GameLogicTest;
import logic.User;
import logic.cards.Card;
import logic.cards.MonsterCard;
import logic.cards.SpellCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

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
    }

    @Test
    void loginUserTest() throws Exception {
        //Check if user with wrong username and password can log-in
        assertTrue(dbHandler.loginUser("abc", "123") == null, "Not registered user could login");

        //Check if user with correct username and wrong password can log-in
        assertTrue(dbHandler.loginUser("kienboec", "Daniel2") == null , "User with wrong password could login");

        //Check if user with correct username and password can log-in
        User user = dbHandler.createUniqueUser("vio", "123");
        assertTrue(dbHandler.loginUser("vio", "123").equals(user), "User with correct password and username could not login");

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
}
