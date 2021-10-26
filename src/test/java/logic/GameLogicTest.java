package logic;
import logic.cards.Card;
import logic.cards.MonsterCard;
import logic.cards.SpellCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Array;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
public class GameLogicTest {
    GameLogic gameLogic;

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

        assertTrue(gameLogic.loginUser("altenhof", "different") == null , "Login successful with wrong password");
        assertTrue(gameLogic.loginUser("Altenhog", "markus") == null, "Login successful with wrong username");

    }

    @Test
    @DisplayName("Add Packages to DB")
    void addPackageToDBTest() {
        Card card1 = new MonsterCard("845f0dc7-37d0-426e-994e-43fc3ac83c08", "WaterGoblin", 10.0);
        Card card2 = new MonsterCard("99f8f8dc-e25e-4a95-aa2c-782823f36e2a", "Dragon", 50.0);
        Card card3 = new SpellCard("e85e3976-7c86-4d06-9a80-641c2019a79f", "WaterSpell", 20.0);
        Card card4 = new MonsterCard("1cb6ab86-bdb2-47e5-b6e4-68c5ab389334", "Ork", 45.0);
        Card card5 = new SpellCard("dfdd758f-649c-40f9-ba3a-8657f4b3439f", "FireSpell", 25.0);

        ArrayList<Card> cards = new ArrayList<Card>();
        cards.add(card1); cards.add(card2); cards.add(card3); cards.add(card4); cards.add(card5);
        gameLogic.addPackageToDB(cards);
    }


}
