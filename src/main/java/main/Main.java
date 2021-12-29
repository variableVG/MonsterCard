package main;

import logic.GameLogic;
import logic.User;
import lombok.Data;

import java.util.Scanner;

@Data


public class Main {

    public static void main(String[] args) {
        GameLogic gameLogic = new GameLogic();

        System.out.println("Welcome! Press 1 to register, press 2 to login");



        //Create user:
        //User user1 = gameLogic.createUser();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Please enter password: ");
        String password = scanner.nextLine();
        //System.out.println("your username is " + user1.getUsername() + " and your password is " + user1.getPassword());

        /**User action:
         * 1) Register and login to the server
         * 2) Acquire some cards (a user can buy cards by acquiring packages).
         * 3) Define a desk of Monster/Spells
         * 4) Battle against each other
         * 5) Compare their stats in the score-board.
         * 6) Exit game
         * */


    }


}
