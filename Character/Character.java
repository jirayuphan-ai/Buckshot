package com.gamedev.buckshot.Character;

import com.gamedev.buckshot.Item.Item;

/**
 * ตัวละครที่ลงสนาม: มี HP กับกระเป๋าไอเทม 4 ช่อง (ตรงกับ Slot1-4 ใน Figma)
 * Player คือ Character ตรงๆ ส่วน Enemy extends Character (เพิ่มสมองบอท)
 *
 * หมายเหตุ: ชื่อคลาสซ้ำกับ java.lang.Character — ไฟล์อื่นต้อง import com.gamedev.buckshot.models.Character
 * แบบระบุชื่อเต็ม (ห้ามใช้ import ...models.*)
 */
public class Character {
    public static final int SLOTS = 4;

    private int hp;
    private final int maxHp;
    private final Item[] items = new Item[SLOTS];

    public Character(int maxHp) {
        this.maxHp = maxHp;
        this.hp = maxHp;
    }

    public void takeDamage(int amount) {
        hp = Math.max(0, hp - amount);
    }

    public void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
    }

    /** ใส่ไอเทมลงช่องว่างช่องแรก (เต็มแล้วไอเทมนั้นจะหายไป) */
    public void addItem(Item item) {
        for (int i = 0; i < SLOTS; i++) {
            if (items[i] == null) {
                items[i] = item;
                return;
            }
        }
    }

    /** เอาไอเทมออกจากช่อง (คืนตัวไอเทม, ช่องว่างหรือเลขช่องผิดจะได้ null) */
    public Item removeItem(int slot) {
        if (slot < 0 || slot >= SLOTS) return null;
        Item it = items[slot];
        items[slot] = null;
        return it;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    // ---------- ส่วนเสริม (getter ให้ UI/AI อ่านค่า) ----------
    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public Item getItem(int slot) {
        return (slot < 0 || slot >= SLOTS) ? null : items[slot];
    }

    public boolean hasFreeSlot() {
        for (Item it : items) {
            if (it == null) return true;
        }
        return false;
    }
}
