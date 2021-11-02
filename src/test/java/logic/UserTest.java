package logic;

import logic.cards.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {
    GameLogic gameLogic;
    @Mock
    User mockedUser;

    @BeforeEach
    void setUp() {
        // arrange
        gameLogic = new GameLogic();

    }

    @Test
    @DisplayName("Check the card-stack of a user")
    void checkCardsTest() {
        User user = gameLogic.createUser("kienboec", "daniel");
        ArrayList<Card> cards = GameLogicTest.dummyAddDB();
        gameLogic.addPackageToDB(cards);
        cards = GameLogicTest.dummyAddDB();

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

        //the Deck of the newly created user should be empty.
        assertTrue(user.getDeck().isEmpty(), "newly created user has cards in her/his deck");

        //SHOW CONFIGURED DECK
        //first create the cards, add them to the DB and buy them
        ArrayList<Card> cards = GameLogicTest.dummyAddDB();
        gameLogic.addPackageToDB(cards);
        gameLogic.acquirePackage(user);
        String[] cardsIds = {"a", "b", "c", "e"};
        //user configures his/her deck
        assertTrue(user.configureDeck(cardsIds) == true, "There is a problem with the deck" );

        //verify that the size of the deck is not bigger than 4:
        assertTrue(user.getDeck().size() <= 4, "The deck is bigger than 4" );
        //verify that the size of the deck is not smaller than 0
        assertTrue(user.getDeck().size() >= 0, "The deck is smaller than 0" );
        //verify that the cards selected for the deck are actually the cards in the deck
        int counter = 0;
        for(Card card : user.getDeck()) {
            assertTrue(card.getId() == cardsIds[counter], "The card " + card.getId() + " should not be in the stack" );
            counter++;
        }


    }

    @Test
    @DisplayName("Configure Deck")
    void configureDeckTest() {
        User user = gameLogic.createUser("kienboec", "daniel");
        ArrayList<Card> cards = GameLogicTest.dummyAddDB();
        gameLogic.addPackageToDB(cards);
        gameLogic.acquirePackage(user);

        String[] cardsIds = {"a", "b", "c", "e"};
        //test worked:
        assertTrue(user.configureDeck(cardsIds) == true, "There is a problem with the deck" );
        //verify that the cards selected for the deck are actually the cards in the deck
        int counter = 0;
        for(Card card : user.getDeck()) {
            assertTrue(card.getId() == cardsIds[counter], "The card " + card.getId() + " should not be in the stack" );
            counter++;
        }

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
