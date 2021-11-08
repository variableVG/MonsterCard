package logic.cards;


import lombok.Data;

import java.util.Random;


@Data
public class Card {

    protected String id;
    protected String name;
    protected double damage; // it is constant
    protected String cardType;

    protected ElemType type;

    /*
    Card () {
        Random rand = new Random();
        int randElemTypeNum = rand.nextInt(3);

        if(randElemTypeNum == 0) {
            type = ElemType.Fire;
        }
        else if(randElemTypeNum == 1) {
            type = ElemType.Water;

        }
        else if(randElemTypeNum == 2) {
            type = ElemType.Normal;
        }
        else {
            System.out.println("An error has happened in the cards constructor while assigning the type");
        }


    }
    */

    public Card(String id, String name, double damage) {
        //set Id
        this.id = id;

        //set name
        this.name = name;

        //check the element of the card:
        if(name.contains(ElemType.Fire.toString())) {
            this.type = ElemType.Fire;
        }
        else if(name.contains(ElemType.Water.toString())) {
            this.type = ElemType.Water;
        }
        else if(name.contains(ElemType.Normal.toString())) {
            this.type = ElemType.Normal;
        }

        //set damage
        this.damage = damage;

    }


    public  <E extends Enum<E>> boolean isPureMonster(String monsterName, Class<E> monsterType) {
        for(E m : monsterType.getEnumConstants()) {
            if(m.name().equals(monsterName)) {
                return true;
            }
        }
        return false;
    }



}


