package logic;

import dummyDB.DummyUserDB;
import logic.cards.Card;
import lombok.Data;

import java.util.ArrayList;



@Data
public class GameLogic {
    private static final int SIZE_OF_PACKAGE = 5;
    private static final int SIZE_OF_DECK = 4;
    private static final int PRICE_PACKAGE = 5;

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
                .deck(new ArrayList<Card>())
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
        else {
            System.out.println("Password does not match");
        }
        return null;
    }

    public void addPackageToDB(ArrayList<Card> cards) {
        dummyDB.cardPackages.add(cards);
    }

    public int acquirePackage(User user) {
        System.out.println("You are about to buy a Package");
        if(user.getCoins() < 5) {
            System.out.println("You don't have enough coin");
            return - 1;
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


        //check if there is enough cards in the cardPackages dummyDB:
        if(dummyDB.cardPackages.isEmpty()) {
            System.out.println("There is no packages left in DB");
            return -2;
        }
        //Provisional solution: User needs to connect to DB and check that is the correct user
        ArrayList<Card> cardPackage = dummyDB.cardPackages.poll();
        user.addCardsToStack(cardPackage);

        //take the money
        user.setCoins(user.getCoins() - PRICE_PACKAGE);
        System.out.println("current coin " + user.getCoins());

        user.checkYourCards();
        return 0;
    }


}
