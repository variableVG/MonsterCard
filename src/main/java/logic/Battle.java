package logic;

import logic.cards.Card;
import lombok.Data;

import java.util.Random;

@Data
public class Battle {

    private User user1;
    private User user2;

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
        //This function just parses the logic of one particular round.

        //Monster vs. Monster

    }

    public void battleFight(User user1, User user2) {
        //this function handles the battle itself, from beginning to end. It returns a log that describes the battle in detail.
    }
}
