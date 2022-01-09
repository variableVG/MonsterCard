package logic;

import DB.DbHandler;
import kotlin.Pair;
import logic.cards.*;
import lombok.Data;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

@Data
public class Battle {

    private User player1;
    private User player2;
    private int battleId;
    public DbHandler dbHandler;
    public static final int STANDARD_DAMAGE = 5;

    public Battle(User player1, User player2, int battleId, DbHandler dbHandler) {
        /** When a user wants to battle, a new Battle-object is created. In the constructor will be then check,
         * if there is an open battle in the DB that is waiting for a second player:
         *      - If there is an open battle, the user will be added to this second battle as player2 or playerB. An open
         *          battle is noticed because player2 will be null in the DB. The function will return a pair with the
         *          battle-Id and the enemy's username.
         *      - If there is not an open battle, the user will be start a battle (new entry in the battle DB) and waits
         *          for a second player to join as a player2. Once a second player has logged-in, the battle will start.
         *          The function will return a pair with battle-Id and null.
         * */
        this.player1 = player1;
        this.player2 = player2;
        this.battleId = battleId;
        this.dbHandler = dbHandler;

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


    public Pair battleLogic(Card card1, Card card2) {
        /***This function just parses the logic of one particular round (also 2 particular cards).
         * It gives back a message for the log and the loser card. In case of a draw, it gives back null.
         * */

        Pair<Card, String> battleResult = null;
        Card loserCard = null;

        String message = "PlayerA: " + card1.getName() + " (" + card1.getDamage() + " Damage) vs PlayerB: "
                + card2.getName() + " (" + card2.getDamage() + " Damage) \n"
                + card1.getDamage() + " VS " + card2.getDamage() + " -> ";

        String finalDamage = "";
        String winnerMsg = "";

        //MONSTER VS. MONSTER
        if (card1 instanceof MonsterCard && card2 instanceof MonsterCard) {
            if (((MonsterCard) card1).getMonsterType() == MonsterType.Ork && ((MonsterCard) card2).getMonsterType() == MonsterType.Wizard) {
                winnerMsg = " Wizzard can control Orks so they are not able to damage them.";
                //wizzard wins
                loserCard = card1;
            }
            else if (((MonsterCard) card1).getMonsterType() == MonsterType.Wizard && ((MonsterCard) card2).getMonsterType() == MonsterType.Ork) {
                winnerMsg = " Wizzard can control Orks so they are not able to damage them.";
                //wizzard wins.
                loserCard = card2;
            } else if (((MonsterCard) card1).getMonsterType() == MonsterType.Goblin && ((MonsterCard) card2).getMonsterType() == MonsterType.Dragon) {
                winnerMsg = " Goblins are too afraid of Dragons to attack. Dragons win.";
                loserCard = card1;
            } else if (((MonsterCard) card1).getMonsterType() == MonsterType.Dragon && ((MonsterCard) card2).getMonsterType() == MonsterType.Goblin) {
                winnerMsg = " Goblins are too afraid of Dragons to attack. Dragons win.";
                loserCard = card2;
            } else if (((MonsterCard) card1).getMonsterType() == MonsterType.Dragon && ((MonsterCard) card2).getMonsterType() == MonsterType.Elf && card2.getType() == ElemType.Fire) {
                winnerMsg = " The FireElves know Dragons since they were little and can evade their attacks.";
                loserCard = card1;
            } else if (((MonsterCard) card1).getMonsterType() == MonsterType.Elf && card1.getType() == ElemType.Fire && ((MonsterCard) card2).getMonsterType() == MonsterType.Dragon) {
                winnerMsg = " The FireElves know Dragons since they were little and can evade their attacks.";
                loserCard = card2;
            } else {
                if (card1.getDamage() > card2.getDamage()) {
                    winnerMsg = " " + card1.getName() + " defeats " + card2.getName();
                    loserCard = card2;
                } else if(card2.getDamage() > card1.getDamage()){
                    winnerMsg = " " + card2.getName() + " defeats " + card1.getName();
                    loserCard = card1;
                }
                else {
                    winnerMsg = "It is a draw";
                    loserCard = null;
                }
            }
        }
        //SPELL VS.SPELL
        else if(card1 instanceof SpellCard && card2 instanceof SpellCard) {
            if(card1.getType() == ElemType.Water) {
                if(card2.getType() == ElemType.Fire) { //card1 wins
                    card1.setDamage(card1.getDamage() * 2);
                    card2.setDamage(card2.getDamage() / 2);
                    loserCard = card2;
                }
                else if(card2.getType() == ElemType.Normal) { //card2 wins
                    card1.setDamage(card1.getDamage() / 2);
                    card2.setDamage(card2.getDamage() * 2);
                    loserCard = card1;
                }
            }
            else if(card1.getType() == ElemType.Fire) {
                if(card2.getType() == ElemType.Water) { // card2 wins
                    card1.setDamage(card1.getDamage() / 2);
                    card2.setDamage(card2.getDamage() * 2);
                    loserCard = card1;
                }
                else if(card2.getType() == ElemType.Normal) { //card1 wins
                    card1.setDamage(card1.getDamage() * 2);
                    card2.setDamage(card2.getDamage() / 2);
                    loserCard = card2;
                }

            }
            else if(card1.getType() == ElemType.Normal) {
                if(card2.getType() == ElemType.Water) { // card1 wins
                    card1.setDamage(card1.getDamage() * 2);
                    card2.setDamage(card2.getDamage() / 2);
                    loserCard = card2;
                }
                else if(card2.getType() == ElemType.Fire) { //card2 wins
                    card1.setDamage(card1.getDamage() / 2);
                    card2.setDamage(card2.getDamage() * 2);
                    loserCard = card1;
                }
            }

            if(card1.getDamage() > card2.getDamage()) { //card1  wins
                winnerMsg = card1.getName() + " wins";
                loserCard = card2;
            }
            else if(card1.getDamage() < card2.getDamage()) { //card2 wins
                winnerMsg = card2.getName() + " wins";
                loserCard = card1;
            }
            else { //draw
                winnerMsg = "Draw (no action)";
                loserCard = null;
            }
        }
        //MIXED FIGHT
        else {
            // The armor of Knights is so heavy that Waterspells make them drown them instantly.
            if(card1 instanceof MonsterCard && ((MonsterCard) card1).getMonsterType() == MonsterType.Knight && card2.getName().equals("WaterSpell")) {
                card1.setDamage(card1.getDamage() / 2);
                card2.setDamage(card2.getDamage() * 2);
                loserCard = card1;
            }
            else if(card2 instanceof  MonsterCard && ((MonsterCard) card2).getMonsterType() == MonsterType.Knight && card1.getName().equals("WaterSpell")) {
                card1.setDamage(card1.getDamage() * 2);
                card2.setDamage(card2.getDamage() / 2);
                loserCard = card2;
            }
            else if((card1 instanceof MonsterCard && ((MonsterCard) card1).getMonsterType() == MonsterType.Kraken && card2 instanceof SpellCard )
                    || (card2 instanceof  MonsterCard && ((MonsterCard) card2).getMonsterType() == MonsterType.Kraken && card1 instanceof SpellCard)) {
                //Kraken is immune against spells
                loserCard = null;
            }
            else {
                if(card1.getType() == ElemType.Water) {
                    if(card2.getType() == ElemType.Fire) { //card1 wins
                        card1.setDamage(card1.getDamage() * 2);
                        card2.setDamage(card2.getDamage() / 2);
                        loserCard = card2;
                    }
                    else if(card2.getType() == ElemType.Normal) { //card2 wins
                        card1.setDamage(card1.getDamage() / 2);
                        card2.setDamage(card2.getDamage() * 2);
                        loserCard = card1;
                    }
                }
                else if(card1.getType() == ElemType.Fire) {
                    if(card2.getType() == ElemType.Water) { // card2 wins
                        card1.setDamage(card1.getDamage() / 2);
                        card2.setDamage(card2.getDamage() * 2);
                        loserCard = card1;
                    }
                    else if(card2.getType() == ElemType.Normal) { //card1 wins
                        card1.setDamage(card1.getDamage() * 2);
                        card2.setDamage(card2.getDamage() / 2);
                        loserCard = card2;
                    }

                }
                else if(card1.getType() == ElemType.Normal) {
                    if(card2.getType() == ElemType.Water) { // card1 wins
                        card1.setDamage(card1.getDamage() * 2);
                        card2.setDamage(card2.getDamage() / 2);
                        loserCard = card2;
                    }
                    else if(card2.getType() == ElemType.Fire) { //card2 wins
                        card1.setDamage(card1.getDamage() / 2);
                        card2.setDamage(card2.getDamage() * 2);
                        loserCard = card1;
                    }
                }

            }

            if(card1.getDamage() > card2.getDamage()) { //card1  wins
                winnerMsg = card1.getName() + " wins";
                loserCard = card2;
            }
            else if(card1.getDamage() < card2.getDamage()) { //card2 wins
                winnerMsg = card2.getName() + " wins";
                loserCard = card1;
            }
            else { //draw
                winnerMsg = "Draw (no action)";
                loserCard = null;
            }

        }

        finalDamage = card1.getDamage() + " VS " + card2.getDamage() + " => ";
        String completeMessage = message + finalDamage + winnerMsg;
        System.out.println(completeMessage );

        battleResult = new Pair<>(loserCard, completeMessage);

        return battleResult;

    }

    public JSONObject battleFight(User user1, User user2) {
        //this function handles the battle itself, from beginning to end. It returns a log that describes the battle in detail.
        JSONObject log = new JSONObject();
        int rounds = 1;
        // Because endless loops are possible we limit the count of rounds to 100
        do {
            System.out.println("Round " + rounds);
            JSONObject roundJson = new JSONObject();
            ArrayList<Card> deck1 = user1.getDeck();
            ArrayList<Card> deck2 = user2.getDeck();
            //Check if the users still have cards in their decks, if not, the battle stops.
            if(deck1.isEmpty()) {
                log.put("cause for ending game", "user " + user1.getUsername() + " does not have cards in deck");

                // If deck1 is empty, user2 wins
                user2.setEloScore(user2.getEloScore() + 3);
                user1.setEloScore(user1.getEloScore() - 5);
                break;
            }
            else if(deck2.isEmpty()) {
                log.put("cause for ending game", "user " + user2.getUsername() + " does not have cards in deck");

                //if deck2 is empty, user1 wins.
                user1.setEloScore(user1.getEloScore() + 3);
                user2.setEloScore(user2.getEloScore() - 5);
                break;
            }

            Random rand = new Random(); //instance of random class to generate a randomnumber
            int random1 = rand.nextInt(deck1.size());
            int random2 = rand.nextInt(deck2.size());
            Card card1 = deck1.get(random1);
            Card card2 = deck2.get(random2);

            //battle Logic:
            Pair<Card, String> fightResult = battleLogic(card1, card2);

            /**
             * Defeated monsters/spells of the competitor are removed from the competitor’s deck and are taken over
             * in the deck of the current player (vice versa). In case of a draw of a round no action takes place (no cards
             * are moved), in this case, the Card section in the fight result is set to null.
             * */

            // Set winner
            if (fightResult.getFirst() != null) { // null means a draw
                if(fightResult.getFirst().equals(card1)) { //card1 has lost: remove card1 from user1-deck and add it to the user2-deck
                    deck2.add(card1);
                    deck1.remove(card1);
                    dbHandler.deleteCardFromUser(user1.getUsername(), card1.getId());
                    dbHandler.addCardToUser(user2.getUsername(), card1.getId());
                    roundJson.put("winner" , user2.getUsername());

                }
                else { // card2 has lost: remove card2 from user2-deck and add it to the user1-deck
                    deck1.add(card2);
                    deck2.remove(card2);
                    dbHandler.deleteCardFromUser(user2.getUsername(), card2.getId());
                    dbHandler.addCardToUser(user1.getUsername(), card2.getId());
                    roundJson.put("winner", user1.getUsername());

                }
            }

            roundJson.put("round comment", fightResult.getSecond());
            log.put("round" + rounds, roundJson);

            rounds++;
        }while(rounds <= 100);

        if(rounds > 100) {
            log.put("cause for ending game", "100 rounds reached");
        }

        //Set new Elo-Scores
        dbHandler.setElo(user1.getUsername(), user1.getEloScore());
        dbHandler.setElo(user2.getUsername(), user2.getEloScore());


        return log;
    }


}
