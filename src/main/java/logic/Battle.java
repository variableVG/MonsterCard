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
        if(card1 instanceof MonsterCard && card2 instanceof MonsterCard) {
            System.out.println("You are fightings monsters!");
            //TROLL
            if(((MonsterCard) card1).getMonsterType() == MonsterType.Troll) {
                //TROLL VS GOBLIN
                if(((MonsterCard) card2).getMonsterType() == MonsterType.Goblin) {
                    card1.setDamage(card1.getDamage() + STANDARD_DAMAGE);
                    card2.setDamage(card2.getDamage() - STANDARD_DAMAGE);
                    winnerMsg = " Troll defeats Goblin.";
                }

            }
            //ORKS
            else if(((MonsterCard) card1).getMonsterType() == MonsterType.Ork) {
                //ORKS VS WIZZARD
                if(((MonsterCard) card2).getMonsterType() == MonsterType.Wizard) {
                    winnerMsg = " Wizzard can control Orks so they are not able to damage them.";
                }
            }
            //GOBLIN
            else if(((MonsterCard) card1).getMonsterType() == MonsterType.Goblin) {
                //GOBLIN VS TROLL
                if(((MonsterCard) card2).getMonsterType() == MonsterType.Troll){
                    card1.setDamage(card1.getDamage() - STANDARD_DAMAGE);
                    card2.setDamage(card2.getDamage() + STANDARD_DAMAGE);
                    winnerMsg = " Troll defeats Goblin.";
                }
                else if(((MonsterCard) card2).getMonsterType() == MonsterType.Dragon) {
                    card1.setDamage(card1.getDamage() - STANDARD_DAMAGE);
                    card2.setDamage(card2.getDamage() + STANDARD_DAMAGE);
                    winnerMsg = " Goblins are too afraid of Dragons to attack. Dragons win.";
                }
            }
            //DRAGONS
            else if(((MonsterCard) card1).getMonsterType() == MonsterType.Dragon) {
                //DRAGON VS GOBLIN
                if(((MonsterCard) card2).getMonsterType() == MonsterType.Goblin) {
                    card1.setDamage(card1.getDamage() + STANDARD_DAMAGE);
                    card2.setDamage(card2.getDamage() - STANDARD_DAMAGE);
                    winnerMsg = " Goblins are too afraid of Dragons to attack. Dragons win.";
                }
                //DRAGON VS ELF
                else if(((MonsterCard) card2).getMonsterType() == MonsterType.Elf) {
                    if(card2.getType() == ElemType.Fire) {
                        winnerMsg = " The FireElves know Dragons since they were little and can evade their attacks.";
                    }
                }

            }
            //WIZZARD
            else if(((MonsterCard) card1).getMonsterType() == MonsterType.Wizard) {
                //WIZZARD VS ORKS
                if(((MonsterCard) card2).getMonsterType() == MonsterType.Ork) {
                    winnerMsg = " Wizzard can control Orks so they are not able to damage them.";
                }
            }
            //ELF
            else if(((MonsterCard) card1).getMonsterType() == MonsterType.Elf) {
                //ELF VS DRAGON
                if(((MonsterCard) card2).getMonsterType() == MonsterType.Dragon) {
                    if(card1.getType() == ElemType.Fire) {
                        winnerMsg = " The FireElves know Dragons since they were little and can evade their attacks.";
                    }

                }
            }

        }
        //SPELL VS. SPELL
        else if(card1 instanceof SpellCard && card2 instanceof SpellCard) {
            System.out.println("You are fightings spells!");
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
