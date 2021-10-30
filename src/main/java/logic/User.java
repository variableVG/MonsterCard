package logic;

import logic.cards.Card;
import logic.cards.MonsterCard;
import logic.cards.SpellCard;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Array;
import java.util.*;


@Data
@Builder

public class User {

    private String username;
    private String password;
    private int coins;

    //a stack is the collection of all his current cards
    ArrayList<Card> stack;
    //the best 4 cards are selected by the user to used in the deck.
    // the deck is used in the battles against other players
    private static final int SIZE_OF_DECK = 4;
    ArrayList<Card> deck;


    //CONSTRUCTOR - with @Builder not necessary (by now).

    //METHODS
    public void addCardsToStack(ArrayList<Card> cardPackage) {
        for(Card card : cardPackage) {
            //TO DO: evtl make a check of the cards here, that they have correct names etc
            stack.add(card);
        }
    }

    public void checkYourCards() {
        System.out.println("Your current stack is: ");
        int counter = 1;
        for (Card card : stack) {
            System.out.println("\t" + "Card " + counter + ": " + card.getName());
            counter++;
        }
    }

    public void currentStatus() {

        System.out.println("You have " + coins + " coins");
    }

    public boolean getAction() {
        currentStatus();
        System.out.println("What would you like to do?");
        System.out.println("Press 1 for register and login to the server");
        System.out.println("Press 2 for acquire some cards");
        System.out.println("Press 3 for define a desk of Monster/Spells");
        System.out.println("Press 4 for battle against each other");
        System.out.println("Press 5 for compare their stats in the score-board.");
        System.out.println("Press 6 to exit game.");

        Scanner scanner = new Scanner(System.in);
        System.out.println("Answer:  ");
        int answer;

        try {
            answer = scanner.nextInt();
        }
        catch (Exception e) {
            System.out.println("not an integer");
            this.getAction();
            return true;
        }

        if(answer == 1) {
            System.out.println("You want to register and login to the server");
        }
        else if(answer == 2) {
            System.out.println("You want to acquire some cards");
            //this.acquirePackage();
        }
        else if(answer == 3) {
            System.out.println("You want to define a desk of Monster/Spells");
        }
        else if(answer == 4) {
            System.out.println("You want to battle");
        }
        else if (answer == 5) {
            System.out.println("You want to see your statistics");
        }
        else if (answer == 6) {
            System.out.println("You want exit the program");
            return false;
        }
        else {
            System.out.println("Please enter a valid input");
            this.getAction();
        }
        return true;
    }


    public boolean configureDeck(String[] cardsIds) {
        if(cardsIds.length > 4 || cardsIds.length < 1) {
            System.out.println("Incorrect amount of cards, please check number of given cards to the deck");
            return false;
        }

        // Make sure that the deck is empty and the stack contains all the cards.

        moveCardsFromDeckToStack();

        //check if all the cards exist in the stack:
        for(String cardID : cardsIds) {
            if(getCardFromStack(cardID) == null) {
                return false;
            }
        }

        //remove Card with cardId from the stack and add it to the deck
        for(String cardID : cardsIds) {
            Card card = getCardFromStack(cardID);
            deck.add(card);
            stack.remove(card);
        }
        return true;
    }

    public Card getCardFromStack(String cardId) {
        for(Card card: stack) {
            if (cardId.equals(card.getId())) {
                return card;
            }
        }
        return null;
    }

    private void moveCardsFromDeckToStack() {
        while(!deck.isEmpty()) {
            Card card = deck.remove(0);
            stack.add(card);
        }
    }

}


