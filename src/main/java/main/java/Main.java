package main.java;

import java.util.*;
import lombok.Data;

@Data


public class Main {

    public static void main(String[] args) {
        GameLogic gameLogic = new GameLogic();

        //Create user:
        User user1 = gameLogic.createUser();
        System.out.println("your username is " + user1.getUsername() + " and your password is " + user1.getPassword());

        /**User action:
         * 1) Register and login to the server
         * 2) Acquire some cards (a user can buy cards by acquiring packages).
         * 3) Define a desk of Monster/Spells
         * 4) Battle against each other
         * 5) Compare their stats in the score-board.
         * */

        user1.getAction();




    }


}
