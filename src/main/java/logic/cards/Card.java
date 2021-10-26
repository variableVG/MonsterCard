package logic.cards;


import lombok.Data;

import java.util.Random;


@Data
public class Card {
    protected String id;
    protected String name;
    protected double damage; // it is constant

    protected ElemType type;

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

    public Card(String id, String name, double damage) {
        //set Id
        this.id = id;

        //check the element of the card:
        if(name.contains(ElemType.Fire.toString())) {
            System.out.println("Type of card is Fire");
            this.type = ElemType.Fire;
        }
        else if(name.contains(ElemType.Water.toString())) {
            System.out.println("Type of card is Water");
            this.type = ElemType.Water;
        }
        else if(name.contains(ElemType.Normal.toString())) {
            System.out.println("Type of card is Normal");
            this.type = ElemType.Normal;
        }

        //set damage
        this.damage = damage;
    }




}


