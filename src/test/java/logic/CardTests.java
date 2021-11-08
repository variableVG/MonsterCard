package logic;

import logic.cards.Card;
import logic.cards.MonsterCard;
import logic.cards.MonsterType;
import logic.cards.SpellCard;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CardTests {
    @Test
    @DisplayName("Test if card is pure Monster")
    void isPureMonsterTest() {
        Card card1 = new MonsterCard("a", "WaterGoblin", 10.0);
        Card card2 = new MonsterCard("b", "Dragon", 50.0);
        Card card3 = new SpellCard("c", "WaterSpell", 20.0);
        Card card4 = new MonsterCard("d", "Ork", 45.0);
        Card card5 = new SpellCard("e", "FireSpell", 25.0);
        Card card6 = new MonsterCard("f", "Goblin", 25.0);
        Card card7 = new MonsterCard("g", "Wizard", 25.0);
        Card card8 = new MonsterCard("h", "Knight", 25.0);
        Card card9 = new MonsterCard("i", "Kraken", 25.0);
        Card card10 = new MonsterCard("j", "Elf", 25.0);
        Card card11 = new MonsterCard("k", "FireElf", 25.0);



        Card[] cards = {card1, card2, card3, card4, card5, card6, card7, card8, card9, card10, card11};
        MonsterType monsterType;
        for(Card card: cards) {
            if(card.getCardType().equals("Monster")){
                System.out.println(card.getName() + " is Pure Monster: " + card.isPureMonster(card.getName(), MonsterType.class));
            }

        }

    }

}
