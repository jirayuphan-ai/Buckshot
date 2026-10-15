package com.gamedev.buckshot.Character;

import com.gamedev.buckshot.Game_Logic.Gamemanager;
import com.gamedev.buckshot.Game_Logic.Gun;
import com.gamedev.buckshot.Game_Logic.Shell_Type;
import com.gamedev.buckshot.Item.*;

/**
 * ศัตรู (บอท): Character ที่ตัดสินใจเองได้
 */
public class Enemy extends Character {

    public Enemy(int maxHp) {
        super(maxHp);
    }

    public void takeTurn(Gamemanager manager) {
        if (manager.isGameOver() || manager.getCurrent() != this) return;

        Gun gun = manager.getGun();
        Character player = manager.getPlayer();
        // บอทรู้นัดหัวแถวก็ต่อเมื่อ "ตัวเอง" เป็นคนใช้แว่นขยาย
        Shell_Type known = (manager.getPeekedBy() == this) ? manager.getPeekedShell() : null;

        // เลือดไม่เต็มและมีลูกอม -> กิน
        if (getHp() < getMaxHp() && useIfHas(manager, Candy.class)) return;

        // ยังไม่รู้นัดหัวแถว และเหลือมากกว่า 1 นัด -> ส่องด้วยแว่นขยาย
        if (known == null && gun.remaining() > 1 && useIfHas(manager, Magnifier.class)) return;

        // รู้ว่าเป็นนัดจริง และยังไม่ได้ใช้มีด -> ใช้มีดก่อนยิง
        if (known == Shell_Type.LIVE && !gun.isDoubled() && useIfHas(manager, Knife.class)) return;

        // ไม่รู้นัด และจริง/เปล่าเท่ากันพอดี -> ใช้น้ำอัดลมเขย่าดวง
        if (known == null && gun.liveCount() == gun.blankCount() && useIfHas(manager, Soda.class)) return;

        // ยิง: รู้นัด -> จริงยิงผู้เล่น/เปล่ายิงตัวเอง (ได้เล่นต่อ)
        // ไม่รู้นัด -> ดูสัดส่วนที่เหลือ เปล่ามากกว่าถึงจะยิงตัวเอง
        Character target;
        if (known != null) {
            target = (known == Shell_Type.LIVE) ? player : this;
        } else {
            target = (gun.blankCount() > gun.liveCount()) ? this : player;
        }
        manager.shoot(target);
    }

    // ถ้ามีไอเทมชนิดนี้ในกระเป๋า ใช้ช่องแรกที่เจอแล้วคืน true 
    private boolean useIfHas(Gamemanager manager, Class<? extends Item> type) {
        for (int i = 0; i < SLOTS; i++) {
            Item it = getItem(i);
            if (it != null && type.isInstance(it)) {
                manager.useItem(i);
                return true;
            }
        }
        return false;
    }
}
