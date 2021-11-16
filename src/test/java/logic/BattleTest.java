package logic;

import logic.cards.Card;
import logic.cards.MonsterCard;
import logic.cards.SpellCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BattleTest {
    GameLogic gameLogic;
    User user1;
    User user2;
    Battle battle;
    @Mock
    User mockedUser;

    @BeforeEach
    void setUp() {
        // arrange the gameLogic, the users and their cards.
        gameLogic = new GameLogic();
        user1 = gameLogic.createUser("kienboec", "daniel");
        user2 = gameLogic.createUser("altenhof", "markus");
        battle = gameLogic.startBattle(user1, user2);

        //set deck for user 1
        ArrayList<Card> deckUser1 = new ArrayList<Card>();
        deckUser1.add(new MonsterCard("a", "WaterGoblin", 10.0));
        deckUser1.add(new SpellCard("b", "FireSpell", 10.0));
        deckUser1.add(new SpellCard("c", "RegularSpell", 10.0));
        deckUser1.add(new MonsterCard("d", "Dragon", 50.0));
        user1.setDeck(deckUser1);

        //set deck for user2:
        ArrayList<Card> deckUser2 = new ArrayList<Card>();
        deckUser2.add(new MonsterCard("A", "FireTroll", 15.0));
        deckUser2.add(new SpellCard("B", "WaterSpell", 20.0));
        deckUser2.add(new MonsterCard("C", "Knight", 15.0));
        deckUser2.add(new MonsterCard("D", "FireElf", 23.0));
        user2.setDeck(deckUser2);
    }

    //EVTL TO DO: TEST FUNCTION gameLogic.createBattle();

    @Test
    @DisplayName("Show deck")
    void getCardFromDeckTest(){
        //TEST FOR USER1:
        //Cards are chosen randomly from the deck to compete: This part is done out of the function.
        Random rand = new Random();
        int randomIndex = rand.nextInt(4);

        //verify that the return value is not null:
        assertTrue( battle.getCardFromDeck(user1, randomIndex) != null, "A card could be taken from the deck when it shouldn't. Maybe given index out of range" );
        //now I verify that the gotten card is actually in the deck:
        assertTrue( user1.getDeck().contains(battle.getCardFromDeck(user1, randomIndex)), "The chosen card is not in the deck of the user" );

        randomIndex = rand.nextInt(4);

        //TEST FOR USER 2:
        //verify that the return value is not null:
        assertTrue( battle.getCardFromDeck(user2, randomIndex) != null, "A card could be taken from the deck when it shouldn't. Maybe given index out of range" );
        //now I verify that the gotten card is actually in the deck:
        assertTrue( user2.getDeck().contains(battle.getCardFromDeck(user2, randomIndex)), "The chosen card is not in the deck of the user" );
    }
    @Test
    @DisplayName("Test battleLogicTest")
    void  battleLogicTest() {

        Card card1 = new MonsterCard("a", "WaterGoblin", 10.0);
        Card card2 = new MonsterCard("b", "FireTroll", 15.0);
        Card card3 = new MonsterCard("c", "Dragon", 20.0);
        Card card4 = new MonsterCard("d", "Ork", 45.0);
        Card card5 = new MonsterCard("e", "Wizard", 25.0);
        Card card6 = new MonsterCard("f", "FireElf", 25.0);
        Card card7 = new MonsterCard("g", "Troll", 15.0);
        Card card8 = new MonsterCard("h", "Knight", 15.0);
        Card card9 = new MonsterCard("i", "Kraken", 25.0);
        Card card10 = new MonsterCard("j", "Elf", 25.0);
        Card card11 = new MonsterCard("k", "FireElf", 25.0);
        Card card12 = new SpellCard("l", "FireSpell", 10);
        Card card13 = new SpellCard("m", "WaterSpell", 10);
        Card card14 = new MonsterCard("h", "FightKnight", 25.0);
        Card card15 = new MonsterCard("n", "FireKraken", 25.0);
        Card card16 = new SpellCard("o", "RegularSpell", 10);

        battle.battleLogic(card13, card9); //MONSTER

    }

}
