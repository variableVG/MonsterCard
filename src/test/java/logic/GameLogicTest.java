package logic;
import logic.cards.Card;
import logic.cards.MonsterCard;
import logic.cards.SpellCard;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
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

    @Test
    @DisplayName("Check Token")
    void LoginUserTokenTest() {
        gameLogic.createUser("altenhof", "markus");
        User user = gameLogic.loginUser("altenhof", "markus");
        assertEquals(user.getToken(), "Basic altenhof-mtcgToken");
    }

    @Test
    @DisplayName("getUserData")
    void getUserDataTest() {
        gameLogic.createUser("altenhof", "markus");
        User user = gameLogic.loginUser("altenhof", "markus");
        try {
            JSONObject jsonUser = gameLogic.getUserData(user.getUsername(), user.getToken());
            assertEquals(jsonUser.getString("username"), "altenhof");
            assertEquals(jsonUser.getInt("coins"), 20);
        } catch (Exception e) {
            //e.printStackTrace();
            //Instead of printing we check:
            fail("Exception was thrown.");
        }

        //check if you cannot get Userdata with wrong token:
        try {
            JSONObject jsonUser = gameLogic.getUserData(user.getUsername(), "123");
        } catch (Exception e) {
            //e.printStackTrace();
            //Instead of printing we check:
            assertTrue(true, "No exception was thrown");
        }

        //try to get userData with wrong Username
        try {
            JSONObject jsonUser = gameLogic.getUserData("Macarena", user.getToken());
            assertEquals(jsonUser, null);
        } catch (Exception e) {
            //e.printStackTrace();
            fail("Exception was thrown.");
        }
    }

    @Test
    @DisplayName("Update User")
    void updateUserTest() {
        gameLogic.createUser("altenhof", "markus");
        User user = gameLogic.loginUser("altenhof", "markus");
        JSONObject updateContent = new JSONObject("{\"Name\": \"Altenhofer\", \"Bio\": \"me codin...\",  \"Image\": \":-D\"}");

        try {
            assertTrue(gameLogic.updateUser(user.getUsername(), user.getToken(), updateContent), "update failed");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Test
    @DisplayName("Update User with wrong token")
    void updateUserTokenTest() {
        gameLogic.createUser("altenhof", "markus");
        User user = gameLogic.loginUser("altenhof", "markus");
        JSONObject updateContent = new JSONObject("{\"Name\": \"Altenhofer\", \"Bio\": \"me codin...\",  \"Image\": \":-D\"}");

        //token fail
        try {
            assertTrue(gameLogic.updateUser(user.getUsername(), "abc", updateContent), "update failed");
        } catch (Exception e) {
            assertTrue(true, "No exception was thrown when token failed in update");
        }

        //user name fail
        try {
            assertTrue(gameLogic.updateUser("abc", user.getToken(), updateContent), "update failed");
        } catch (Exception e) {
            assertTrue(true, "No exception was thrown when username failed in update");
        }
    }

    @Test
    @DisplayName("get Elo Score")
    void getEloScoreTest() {
        gameLogic.createUser("altenhof", "markus");
        User user = gameLogic.loginUser("altenhof", "markus");
        assertEquals(user.getEloScore(), 100);
    }

    @Test
    @DisplayName("Add Package to DB")
    void addPackageTest() {
        gameLogic.createUser("admin", "istrator");
        User admin = gameLogic.loginUser("admin", "istrator");
        gameLogic.createUser("altenhof", "markus");
        User user = gameLogic.loginUser("altenhof", "markus");
        String s = "[{Id:67f9048f-99b8-4ae4-b866-d8008d00c53d, Name:WaterGoblin, Damage: 10.0}, {Id:aa9999a0-734c-49c6-8f4a-651864b14e62, Name:RegularSpell, Damage: 50.0}, {Id:d6e9c720-9b5a-40c7-a6b2-bc34752e3463, Name:Knight, Damage: 20.0}, {Id:02a9c76e-b17d-427f-9240-2dd49b0d3bfd, Name:RegularSpell, Damage: 45.0}, {Id:2508bf5c-20d7-43b4-8c77-bc677decadef, Name:FireElf, Damage: 25.0}]";
        JSONArray cards = new JSONArray(s);

        //not admin creates packages
        try {
            assertTrue(gameLogic.addPackageToDB(cards, user.getToken()));
        } catch (Exception e) {
            assertTrue(true, "No exception was thrown when a not admin added a package");
        }

        //not enough number of cards:
        String s2 = "[{Id:67f9048f-99b8-4ae4-b866-d8008d00c53d, Name:WaterGoblin, Damage: 10.0}, {Id:aa9999a0-734c-49c6-8f4a-651864b14e62, Name:RegularSpell, Damage: 50.0}]";
        JSONArray cards2 = new JSONArray(s2);
        try {
            assertTrue(gameLogic.addPackageToDB(cards, user.getToken()));
        } catch (Exception e) {
            assertTrue(true, "No exception was thrown when oder than 5 cards were added");
        }

        //Correct:
        try {
            assertTrue(gameLogic.addPackageToDB(cards, admin.getToken()));
        } catch (Exception e) {
            fail("Exception was thrown when adding Package");
        }

    }

    @Test
    @DisplayName("Acquire Package test")
    void acquirePackageTest() throws Exception {
        gameLogic.createUser("admin", "istrator");
        User admin = gameLogic.loginUser("admin", "istrator");
        gameLogic.createUser("altenhof", "markus");
        User user = gameLogic.loginUser("altenhof", "markus");

        String s1 = "[{Id:67f9048f-99b8-4ae4-b866-d8008d00c53d, Name:WaterGoblin, Damage: 10.0}, {Id:aa9999a0-734c-49c6-8f4a-651864b14e62, Name:RegularSpell, Damage: 50.0}, {Id:d6e9c720-9b5a-40c7-a6b2-bc34752e3463, Name:Knight, Damage: 20.0}, {Id:02a9c76e-b17d-427f-9240-2dd49b0d3bfd, Name:RegularSpell, Damage: 45.0}, {Id:2508bf5c-20d7-43b4-8c77-bc677decadef, Name:FireElf, Damage: 25.0}]";
        JSONArray cards1 = new JSONArray(s1);
        gameLogic.addPackageToDB(cards1, admin.getToken());
        String s2 = "[{Id:a, Name:WaterGoblin, Damage: 8.0}, {Id:b, Name:RegularSpell, Damage: 6.0}, {Id:c, Name:Knight, Damage: 8.0}, {Id:dd, Name:RegularSpell, Damage: 85.0}, {Id:e, Name:FireElf, Damage: 258.0}]";
        JSONArray cards2 = new JSONArray(s2);
        gameLogic.addPackageToDB(cards2, admin.getToken());
        String s3 = "[{Id:aa, Name:WaterGoblin, Damage: 8.0}, {Id:bb, Name:RegularSpell, Damage: 6.0}, {Id:cc, Name:Knight, Damage: 8.0}, {Id:ddd, Name:RegularSpell, Damage: 85.0}, {Id:ee, Name:FireElf, Damage: 258.0}]";
        JSONArray cards3 = new JSONArray(s3);
        gameLogic.addPackageToDB(cards3, admin.getToken());

        JSONArray boughtcards = gameLogic.acquirePackage(user.getToken());

        JSONArray checkcards = gameLogic.getUserCards(user.getToken());

        assertEquals(boughtcards.toString(), checkcards.toString());

        //Check Money:
        assertEquals(gameLogic.getCoins(user.getToken()), 20-5);

        gameLogic.acquirePackage(user.getToken());
        gameLogic.acquirePackage(user.getToken());

    }

    @Test
    void getUserCardsTest() throws Exception {
        JSONArray cardsInJson = gameLogic.getUserCards("Basic kienboec-mtcgToken");
        System.out.println(cardsInJson);
    }

    @Test
    void showUserDeck() throws Exception{
        configureDeckTest();
        gameLogic.createUser("altenhof", "markus");
        User user = gameLogic.loginUser("altenhof", "markus");

        JSONArray cardsInJson = gameLogic.showUserDeck("Basic kienboec-mtcgToken");
        System.out.println(cardsInJson);

    }

    @Test
    void configureDeckTest() throws Exception {
        acquirePackageTest();
        JSONArray cardsInJson = new JSONArray();
        cardsInJson.put("a");
        cardsInJson.put("b");
        cardsInJson.put("c");
        cardsInJson.put("dd");
        String token = "Basic altenhof-mtcgToken";

        gameLogic.configureDeck(cardsInJson, token);
        JSONArray cardsInJson2 = new JSONArray();
        cardsInJson2 = gameLogic.showUserDeck(token);

        assertEquals(cardsInJson2.length(), 4);



    }

    @Test
    void startBattleTest() {
        gameLogic.createUser("altenhof", "markus");
        User user = gameLogic.loginUser("altenhof", "markus");
        JSONObject battleResponse = gameLogic.startBattle(user.getToken());
        assertTrue(battleResponse.getString("message").contains("Waiting for opponent to join"));
    }

    @Test
    void showStore() throws Exception {

        gameLogic.showStore("Basic kienboec-mtcgToken");
    }



}
