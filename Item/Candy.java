package com.gamedev.buckshot.Item;

import com.gamedev.buckshot.Game_Logic.Gun;
import com.gamedev.buckshot.Character.Character;

/** ลูกอม: เพิ่ม HP 1 หัวใจ (ไม่เกิน maxHp) */
public class Candy extends Item {
    public Candy() {
        super("Candy", "assets/Image/Candy.png");
    }

    @Override
    public String use(Character user, Gun gun) {
        int before = user.getHp();
        user.heal(1);
        return user.getHp() > before ? "+1 HP" : "HP already full";
    }

}
