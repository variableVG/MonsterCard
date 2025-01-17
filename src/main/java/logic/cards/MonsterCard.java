package logic.cards;

import lombok.Data;


public class MonsterCard extends Card{

    private MonsterType monsterType;
    boolean isPureMonster;

    /*
    public MonsterCard() {
        Random rand = new Random();
        int randMonsterTypeNum = rand.nextInt(6);

        if(randMonsterTypeNum == 0) {
            monsterType = MonsterType.Goblin;

        }
        else if(randMonsterTypeNum == 1) {
            monsterType = MonsterType.Dragon;
        }
        else if(randMonsterTypeNum == 2) {
            monsterType = MonsterType.Wizzard;
        }
        else if(randMonsterTypeNum == 3) {
            monsterType = MonsterType.Knight;
        }
        else if(randMonsterTypeNum == 4) {
            monsterType = MonsterType.Kraken;
        }
        else if(randMonsterTypeNum == 5) {
            monsterType = MonsterType.Elf;
        }
        else {
            System.out.println("An error has happened in the cards constructor while assigning the type");
        }

        name = type.toString() + monsterType.toString();

    }
    */
    public MonsterCard(String id, String name, double damage) {
        super(id, name, damage);
        this.setCardType("Monster");
        //check the monster of the card by parsing its name.
        if(name.contains(MonsterType.Elf.toString())) {
            this.monsterType = MonsterType.Elf;
        }
        else if(name.contains(MonsterType.Dragon.toString())) {
            this.monsterType = MonsterType.Dragon;
        }
        else if(name.contains(MonsterType.Goblin.toString())) {
            this.monsterType = MonsterType.Goblin;
        }
        else if(name.contains(MonsterType.Knight.toString())) {
            this.monsterType = MonsterType.Knight;
        }
        else if(name.contains(MonsterType.Kraken.toString())) {
            this.monsterType = MonsterType.Kraken;
        }
        else if(name.contains(MonsterType.Wizard.toString())) {
            this.monsterType = MonsterType.Wizard;
        }
        else if(name.contains(MonsterType.Ork.toString())) {
            this.monsterType = MonsterType.Ork;
        }
        else if(name.contains(MonsterType.Troll.toString())) {
            this.monsterType = MonsterType.Troll;
        }

    }

    public MonsterType getMonsterType() {
        if(cardType.equals("Monster")) {
            return this.monsterType;
        }
        return null;
    }
}
