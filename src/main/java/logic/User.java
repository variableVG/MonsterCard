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

    private static final int SIZE_OF_PACKAGE = 5;
    ArrayList<Card> cardPackage;


    //CONSTRUCTOR - with @Builder not necessary (by now).

    //METHODS
    private void checkYourCards() {
        System.out.println("Your current stack is: ");
        int counter = 1;
        for (Card card : stack) {
            System.out.println("\t" + "Card " + counter + ": " + card.getName());
            counter++;
        }
    }

    private void currentStatus() {

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
            this.acquirePackage();
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



    public void acquirePackage() {
        System.out.println("You are about to buy a Package");
        if(coins < 5) {
            System.out.println("You don't have enough coin");
            return;
        }

        /*Random rand = new Random();
        int randCard = 0;

        for(int i = 0; i < 5; i++) {
            randCard = rand.nextInt(2);
            if (randCard % 2 == 0) {
                MonsterCard card = new MonsterCard();
                stack.add(card);
            } else {
                SpellCard card = new SpellCard();
                stack.add(card);
            }
            coins--;
        }*/

        //User needs to connect to DB.

        checkYourCards();

    }

    public void chooseDeck() {
        System.out.println("You are in chooseDeck");
        checkYourCards();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter number of card:  ");
        int answer;
        int counter = 0;

        do {
            try {
                answer = scanner.nextInt();
            }
            catch (Exception e) {
                System.out.println("not an integer");
                continue;
            }
            if(answer > stack.size() || answer <= 0) {
                System.out.println("Number out of range of stack");
            }
            else {
                //deck.add(stack.at(answer-1));
                counter++;
            }
        } while (counter < SIZE_OF_DECK);

    }

}
