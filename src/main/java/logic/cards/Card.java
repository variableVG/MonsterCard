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

    public Card(String id, String name, double damage) {
        /** To create a card we use the constructor. Parsing the name of the card, we can get which type of card it is.
         * */
        //set Id
        this.id = id;

        //set name
        this.name = name;

        //check the element of the card by parsing its name.
        if(name.contains(ElemType.Fire.toString())) {
            this.type = ElemType.Fire;
        }
        else if(name.contains(ElemType.Water.toString())) {
            this.type = ElemType.Water;
        }
        else if(name.contains(ElemType.Normal.toString()) || name.contains("Regular")) {
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


