package logic.cards;

import java.lang.annotation.ElementType;
import java.util.Random;


public class MonsterCard extends Card{

    MonsterType monsterType;

    public MonsterCard() {
        Random rand = new Random();
        int randMonsterTypeNum = rand.nextInt(6);

        if(randMonsterTypeNum == 0) {
            monsterType = MonsterType.Globin;

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

    public MonsterCard(String id, String name, double damage) {
        super(id, name, damage);

        //check the monster of the card:
        if(name.contains(MonsterType.Elf.toString())) {
            System.out.println("Monster is Elf");
            this.monsterType = MonsterType.Elf;
        }
        else if(name.contains(MonsterType.Dragon.toString())) {
            System.out.println("Monster is Dragon");
            this.monsterType = MonsterType.Dragon;
        }
        else if(name.contains(MonsterType.Globin.toString())) {
            System.out.println("Monster is Globin");
            this.monsterType = MonsterType.Globin;
        }
        else if(name.contains(MonsterType.Knight.toString())) {
            System.out.println("Monster is Knight");
            this.monsterType = MonsterType.Knight;
        }
        else if(name.contains(MonsterType.Kraken.toString())) {
            System.out.println("Monster is Kraken");
            this.monsterType = MonsterType.Kraken;
        }
        else if(name.contains(MonsterType.Wizzard.toString())) {
            System.out.println("Monster is Wizzard");
            this.monsterType = MonsterType.Wizzard;
        }
        else if(name.contains(MonsterType.Ork.toString())) {
            System.out.println("Monster is Ork");
            this.monsterType = MonsterType.Ork;
        }

    }

}
