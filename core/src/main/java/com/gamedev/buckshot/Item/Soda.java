package com.gamedev.buckshot.Item;

import com.gamedev.buckshot.Game_Logic.Gun;
import com.gamedev.buckshot.Game_Logic.Shell_Type;
import com.gamedev.buckshot.Character.Character;

// น้ำอัดลม ดึงกระสุนนัดปัจจุบันออก 1 นัด 
public class Soda extends Item {
    public Soda() {
        super("Soda", "assets/Image/Soda.png");
    }

    @Override
    public String use(Character user, Gun gun) {
        Shell_Type s = gun.eject();
        return "ejected a " + s.name() + " shell";
    }
}
