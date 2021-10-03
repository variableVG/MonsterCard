package main.java;

import lombok.Builder;
import lombok.Data;

import javax.sound.midi.Soundbank;
import java.util.Scanner;

@Data
@Builder

public class User {

    private String username;
    private String password;
    private int coins = 20;

    //a stack is the collection of all his current cards
    private Cards stack[];

    //the best 4 cards are selected by the user to used in the deck.
    // the deck is used in the battles against other players
    private Cards deck[];


    //CONSTRUCTOR - with @Builder not necessary (by now).

    //METHODS
    public void getAction() {
        System.out.println("What would you like to do?");
        System.out.println("Press 1 for register and login to the server");
        System.out.println("Press 2 for acquire some cards");
        System.out.println("Press 3 for define a desk of Monster/Spells");
        System.out.println("Press 4 for battle against each other");
        System.out.println("Press 5 for compare their stats in the score-board.");

        Scanner scanner = new Scanner(System.in);
        System.out.println("Answer:  ");
        int answer;

        try {
            answer = scanner.nextInt();
        }
        catch (Exception e) {
            System.out.println("not an integer");
            this.getAction();
            return;
        }

        if(answer == 1) {
            System.out.println("You want to register and login to the server");
        }
        else if(answer == 2) {
            System.out.println("You want to acquire some cards");
        }
        else if(answer == 3) {
            System.out.println("You want to define a desk of Monster/Spells");
        }
        else if(answer == 4) {
            System.out.println("You want to battle");
        }
        else if (answer == 5) {
            System.out.println("You want to see your stats");
        }
        else {
            System.out.println("Please enter a valid input");
            this.getAction();
        }
    }

    public void buyPackage() {
        System.out.println("You are about to buy a Package");

    }

    private void register() {

    }

    private void login() {

    }


}
