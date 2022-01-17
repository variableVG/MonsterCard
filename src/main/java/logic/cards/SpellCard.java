package logic.cards;


public class SpellCard extends Card{

    public SpellCard(String id, String name, double damage) {
        super(id, name, damage);
        this.setCardType("Spell");
    }

}

