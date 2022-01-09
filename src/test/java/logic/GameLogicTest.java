package logic;
import logic.cards.Card;
import logic.cards.MonsterCard;
import logic.cards.SpellCard;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
public class GameLogicTest {
    GameLogic gameLogic;
    @Mock User mockedUser;

    @BeforeEach
    void setUp() {
        // arrange
        gameLogic = new GameLogic();

    }

    @Test
    @DisplayName("Create user test")
    void createUserTest() {
        assertTrue(gameLogic.createUser("kienboec", "daniel") != null, "User could not be added");
        assertFalse(gameLogic.createUser("kienboec", "daniel") != null, "User could be added twice");
    }

    @Test
    @DisplayName("Login User")
    void loginUserTest() {

        gameLogic.createUser("admin", "istrator");
        gameLogic.createUser("altenhof", "markus");

        assertTrue(gameLogic.loginUser("altenhof", "markus") != null, "User could not be logged in");
        assertTrue(gameLogic.loginUser("admin", "istrator") != null, "User could not be logged in");

        //password does not match
        assertTrue(gameLogic.loginUser("altenhof", "different") == null , "Login successful with wrong password");

        //user does not exist
        assertTrue(gameLogic.loginUser("Altenhog", "markus") == null, "Login successful with wrong username");

    }


    static ArrayList<Card> dummyAddDB() {
        Card card1 = new MonsterCard("a", "WaterGoblin", 10.0);
        Card card2 = new MonsterCard("b", "Dragon", 50.0);
        Card card3 = new SpellCard("c", "WaterSpell", 20.0);
        Card card4 = new MonsterCard("d", "Ork", 45.0);
        Card card5 = new SpellCard("e", "FireSpell", 25.0);

        ArrayList<Card> cards = new ArrayList<Card>();
        cards.add(card1); cards.add(card2); cards.add(card3); cards.add(card4); cards.add(card5);
        return cards;
    }


    @Test
    void acquirePackageTest() throws Exception {
        gameLogic.acquirePackage("Basic altenhof-mtcgToken");
    }

    @Test
    void getUserCardsTest() throws Exception {
        JSONArray cardsInJson = gameLogic.getUserCards("Basic kienboec-mtcgToken");
        System.out.println(cardsInJson);
    }

    @Test
    void showUserDeck() throws Exception{
        JSONArray cardsInJson = gameLogic.showUserDeck("Basic kienboec-mtcgToken");
        System.out.println(cardsInJson);

    }

    @Test
    void configureDeckTest() throws Exception {
        JSONArray cardsInJson = new JSONArray();
        cardsInJson.put("b2237eca-0271-43bd-87f6-b22f70d42ca4");
        cardsInJson.put("9e8238a4-8a7a-487f-9f7d-a8c97899eb48");
        cardsInJson.put("d60e23cf-2238-4d49-844f-c7589ee5342e");
        cardsInJson.put("fc305a7a-36f7-4d30-ad27-462ca0445649");
        String token = "Basic kienboec-mtcgToken";

        gameLogic.configureDeck(cardsInJson, token);
        JSONArray cardsInJson2 = new JSONArray();
        cardsInJson2 = gameLogic.showUserDeck(token);
        System.out.println(cardsInJson2);
    }

    @Test
    void startBattleTest() {
        gameLogic.startBattle("user1");
    }

   /* @Test
    @DisplayName("Add Packages to DB")
    void addPackageToDBTest() {
        ArrayList<Card> cards = dummyAddDB();
        //Add cards to dummyDB - TO DO: Change to real DBfunction
        gameLogic.addPackageToDB(cards);
        assertTrue(gameLogic.dummyDB.cardPackages.element() == cards, "deck was not correctly added");

    }

    @Test
    @DisplayName("user acquire new Package")
    void acquirePackageTest() {

        ArrayList<Card> cards = dummyAddDB();
        gameLogic.addPackageToDB(cards);
        cards = dummyAddDB();
        gameLogic.addPackageToDB(cards);
        cards = dummyAddDB();
        gameLogic.addPackageToDB(cards);

        User user = gameLogic.createUser("kienboec", "daniel");
        assertTrue(gameLogic.acquirePackage(user) == 0, "User could not buy package");
        assertTrue(gameLogic.acquirePackage(user) == 0, "User could not buy package");
        assertTrue(gameLogic.acquirePackage(user) == 0, "User could not buy package");

        //No more packages in DB
        assertTrue(gameLogic.acquirePackage(user) == -2, "User got Package from empty DB");

        cards = dummyAddDB();
        gameLogic.addPackageToDB(cards);
        cards = dummyAddDB();
        gameLogic.addPackageToDB(cards);
        //No more money test
        assertTrue(gameLogic.acquirePackage(user) == 0, "User could not buy package");
        assertTrue(gameLogic.acquirePackage(user) == -1, "User with no money bought package");

    }*/



}
