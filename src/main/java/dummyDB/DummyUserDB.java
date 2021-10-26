package dummyDB;

import logic.User;
import logic.cards.Card;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class DummyUserDB {

    public ArrayList<User> users;
    public Queue<ArrayList<Card>> cardPackages;

    public DummyUserDB() {
        users = new ArrayList<User>();

        cardPackages = new LinkedList<>();

    }

    public User getUserByName(String username) {
        for(User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public boolean addUser(User user) {
        if(getUserByName(user.getUsername()) == null) {
            users.add(user);
            return true;
        }
        return false;

    }
}
