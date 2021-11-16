package logic;

import logic.cards.*;
import lombok.Data;

import java.util.Random;

@Data
public class Battle {

    private User user1;
    private User user2;
    public static final int STANDARD_DAMAGE = 5;

    public Battle(User u1, User u2) {
        this.setUser1(u1);
        this.setUser2(u2);
    }

    Card getCardFromDeck(User user, int randomIndex) {
        //Cards are chosen randomly from the deck to compete: This part is done out of the function.
        //Random rand = new Random();
        //int randomIndex = rand.nextInt(4);

        if(randomIndex > 4 || randomIndex < 0) { //control that randomIndex is correct.
            System.out.println("getCardFromDeck(): RandomIndex out of range");
            return null;
        }
        Card card = user.getDeck().get(randomIndex);
        System.out.println("Success");
        System.out.println("Random chosen card for user " + user.getUsername() + " is " + card.getName() + " with cardId " + card.getId());
        return card;
    }


    public void battleLogic(Card card1, Card card2) {
        //This function just parses the logic of one particular round (also 2 particular cards)
        String message = "PlayerA: " + card1.getName() + " (" + card1.getDamage() + " Damage) vs PlayerB: "
                + card2.getName() + " (" + card2.getDamage() + " Damage) \n"
                + card1.getDamage() + " VS " + card2.getDamage() + " -> ";

        String finalDamage = "";
        String winnerMsg = "";

        //MONSTER VS. MONSTER
        if (card1 instanceof MonsterCard && card2 instanceof MonsterCard) {
            if (((MonsterCard) card1).getMonsterType() == MonsterType.Ork && ((MonsterCard) card2).getMonsterType() == MonsterType.Wizard) {
                winnerMsg = " Wizzard can control Orks so they are not able to damage them.";
            }
            else if (((MonsterCard) card1).getMonsterType() == MonsterType.Wizard && ((MonsterCard) card2).getMonsterType() == MonsterType.Ork) {
                winnerMsg = " Wizzard can control Orks so they are not able to damage them.";
            } else if (((MonsterCard) card1).getMonsterType() == MonsterType.Goblin && ((MonsterCard) card2).getMonsterType() == MonsterType.Dragon) {
                winnerMsg = " Goblins are too afraid of Dragons to attack. Dragons win.";
            } else if (((MonsterCard) card1).getMonsterType() == MonsterType.Dragon && ((MonsterCard) card2).getMonsterType() == MonsterType.Goblin) {
                winnerMsg = " Goblins are too afraid of Dragons to attack. Dragons win.";
            } else if (((MonsterCard) card1).getMonsterType() == MonsterType.Dragon && ((MonsterCard) card2).getMonsterType() == MonsterType.Elf && card2.getType() == ElemType.Fire) {
                winnerMsg = " The FireElves know Dragons since they were little and can evade their attacks.";
            } else if (((MonsterCard) card1).getMonsterType() == MonsterType.Elf && card1.getType() == ElemType.Fire && ((MonsterCard) card2).getMonsterType() == MonsterType.Dragon) {
                winnerMsg = " The FireElves know Dragons since they were little and can evade their attacks.";
            } else {
                if (card1.getDamage() > card2.getDamage()) {
                    winnerMsg = " " + card1.getName() + " defeats " + card2.getName();
                } else {
                    winnerMsg = " " + card2.getName() + " defeats " + card1.getName();
                }
            }
        }
        //SPELL VS.SPELL
        else if(card1 instanceof SpellCard && card2 instanceof SpellCard) {
            System.out.println("You are fightings spells!");
            if(card1.getType() == ElemType.Water) {
                if(card2.getType() == ElemType.Fire) { //card1 wins
                    card1.setDamage(card1.getDamage() * 2);
                    card2.setDamage(card2.getDamage() / 2);
                    winnerMsg = " WaterSpell wins";
                }
                else if(card2.getType() == ElemType.Normal) { //card2 wins
                    card1.setDamage(card1.getDamage() / 2);
                    card2.setDamage(card2.getDamage() * 2);
                    winnerMsg = " NormalSpell wins";
                }
                else {
                    winnerMsg = "Draw";
                }
            }
            else if(card1.getType() == ElemType.Fire) {
                if(card2.getType() == ElemType.Water) { // card2 wins
                    card1.setDamage(card1.getDamage() / 2);
                    card2.setDamage(card2.getDamage() * 2);
                }
                else if(card2.getType() == ElemType.Normal) { //card1 wins
                    card1.setDamage(card1.getDamage() * 2);
                    card2.setDamage(card2.getDamage() / 2);
                }

            }
            else if(card1.getType() == ElemType.Normal) {
                if(card2.getType() == ElemType.Water) { // card1 wins
                    card1.setDamage(card1.getDamage() * 2);
                    card2.setDamage(card2.getDamage() / 2);
                }
                else if(card2.getType() == ElemType.Fire) { //card2 wins
                    card1.setDamage(card1.getDamage() / 2);
                    card2.setDamage(card2.getDamage() * 2);
                }
            }

            if(card1.getDamage() > card2.getDamage()) { //card1  wins
                winnerMsg = card1.getName() + " wins";
            }
            else if(card1.getDamage() < card2.getDamage()) { //card2 wins
                winnerMsg = card2.getName() + " wins";
            }
            else { //draw
                winnerMsg = "Draw (no action)";
            }
        }
        //MIXED FIGHT
        else {
            System.out.println("You are mixed!");

        }


        finalDamage = card1.getDamage() + " VS " + card2.getDamage() + " => ";
        System.out.println(message + finalDamage + winnerMsg );

    }

    public void battleFight(User user1, User user2) {
        //this function handles the battle itself, from beginning to end. It returns a log that describes the battle in detail.
    }


}
