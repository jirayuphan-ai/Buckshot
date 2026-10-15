package com.gamedev.buckshot.Item;

import com.gamedev.buckshot.Game_Logic.Gun;
import com.gamedev.buckshot.Game_Logic.Shell_Type;
import com.gamedev.buckshot.Character.Character;

// แว่นขยาย ดูกระสุนนัดปัจจุบัน 
public class Magnifier extends Item {
    public Magnifier() {
        super("Magnifier", "Image/Magnifier.png");
    }

    @Override
    public String use(Character user, Gun gun) {
        Shell_Type s = gun.peek();
        return "current shell is " + (s == null ? "none" : s.name());
    }
}
