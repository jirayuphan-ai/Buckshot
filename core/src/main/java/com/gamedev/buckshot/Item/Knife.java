package com.gamedev.buckshot.Item;

import com.gamedev.buckshot.Game_Logic.Gun;
import com.gamedev.buckshot.Character.Character;

// มีด นัดถัดไปที่ยิงโดน ทำความเสียหาย 2 เท่า 
public class Knife extends Item {
    public Knife() {
        super("Knife", "Image/Knife.png");
    }

    @Override
    public String use(Character user, Gun gun) {
        gun.doubleDamage();
        return "next live shell deals double damage";
    }

}
