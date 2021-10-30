package logic;
import logic.cards.Card;
import logic.cards.MonsterCard;
import logic.cards.SpellCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Array;
import java.util.ArrayList;

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

    ArrayList<Card> dummyAddDB() {
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

    }

    @Test
    @DisplayName("Check the card-stack of a user")
    void checkCardsTest() {
        User user = gameLogic.createUser("kienboec", "daniel");
        ArrayList<Card> cards = dummyAddDB();
        gameLogic.addPackageToDB(cards);
        cards = dummyAddDB();

        gameLogic.acquirePackage(user);

        for(Card card : user.stack) {
            assertTrue(cards.contains(card), "Wrong card was added to the user stack");
        }
    }

    @Test
    @DisplayName("Show deck")
    void showDeckTest() {
        User user = gameLogic.createUser("kienboec", "daniel");
        //User user2 = gameLogic.createUser("altenhof", "markus");

        assertTrue(user.getDeck() == null, "newly created user has cards in her/his deck");

    }

    @Test
    @DisplayName("Configure Deck")
    void configureDeckTest() {
        User user = gameLogic.createUser("kienboec", "daniel");
        ArrayList<Card> cards = dummyAddDB();
        gameLogic.addPackageToDB(cards);
        gameLogic.acquirePackage(user);

        String[] cardsIds = {"a", "b", "c", "e"};
        //test worked:
        assertTrue(user.configureDeck(cardsIds) == true, "There is a problem with the deck" );

        //length of cardsIds is not correct (more than 4 cards are chosen, or no card is chosen):
        String[] cardsIdsEmpty = { };
        assertTrue(user.configureDeck(cardsIdsEmpty) == false, "deck allows empty card");
        String[] cardsIdsLong = {"a", "b", "c", "d", "e"};
        assertTrue(user.configureDeck(cardsIdsLong) == false, "deck allows empty card");

        //Check that the cardIds entered are correct
        String[] cardsIdsChanged = {"a", "b", "asc", "e"};
        assertTrue(user.configureDeck(cardsIdsChanged) == false, "cardId is wrong, but card has been accepted" );

    }

}
