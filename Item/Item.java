package com.gamedev.buckshot.Item;

import com.gamedev.buckshot.Game_Logic.Gun;
import com.gamedev.buckshot.Character.Character;

/*
 * ไอเทมทุกชนิด ลูกแต่ละตัว override use() ให้ทำงานต่างกัน (polymorphism) แล้วคืนข้อความสั้นๆ ไว้โชว์บนจอ
 */
public abstract class Item {
    private final String name;
    private final String iconFile;

    protected Item(String name, String iconFile) {
        this.name = name;
        this.iconFile = iconFile;
    }

    public String getName() {
        return name;
    }

    public String getIconFile() {
        return iconFile;
    }

    public abstract String use(Character user, Gun gun);

}
