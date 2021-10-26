package logic;

import dummyDB.DummyUserDB;
import logic.cards.Card;
import lombok.Data;

import java.util.ArrayList;

@Data
public class GameLogic {

    public DummyUserDB dummyDB;

    GameLogic() {
        dummyDB = new DummyUserDB();
    }


    //user
    public User createUser(String username, String password) {
        /*
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Please enter password: ");
        String password = scanner.nextLine();
        */

        User user = User.builder()
                .username(username)
                .password(password)
                .coins(20)
                .stack(new ArrayList<Card>())
                .build();

        if(dummyDB.addUser(user)) {
            return user;
        }

        return null;
    }

    public User loginUser(String username, String password) {
        User user = dummyDB.getUserByName(username);
        if(user == null) {
            System.out.println("user does not exist");
            return null;
        }
        if(user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public void addPackageToDB(ArrayList<Card> cards) {
        dummyDB.cardPackages.add(cards);
    }



}
