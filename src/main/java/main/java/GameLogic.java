package main.java;

import lombok.Data;

import java.util.Scanner;

@Data
public class GameLogic {


    //Scoreboard, = Sorted list of ELO values.

    //user
    public static User createUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Please enter password: ");
        String password = scanner.nextLine();

        User user1 = User.builder()
                .username(username)
                .password(password)
                .build();

        return user1;
    }


}
