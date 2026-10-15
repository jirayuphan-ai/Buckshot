package com.gamedev.buckshot.Game_Logic;

import com.gamedev.buckshot.Animation.Action_Type;
import com.gamedev.buckshot.Character.Character;
import com.gamedev.buckshot.Character.Enemy;
import com.gamedev.buckshot.Sound.*;
import com.gamedev.buckshot.Item.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/*
 *   turn = รอบ: เริ่มรอบใหม่ -> บรรจุกระสุนใหม่ + แจกไอเทม
 *   ผลัดกันยิงจนกระสุนหมดปืน แล้วขึ้นรอบใหม่
 *   ยิงตัวเองด้วยกระสุนเปล่า = ได้เล่นต่อ 
 * GameScreen สั่งงานผ่าน startTurn/shoot/useItem
 */
public class Gamemanager {
    public static final int MAX_HP = 4;
    public static final int MAX_SHELLS = 8;
    private int turn = 0;
    private final Character player;
    private final Enemy enemy;
    private Character current;
    private final Gun gun = new Gun();

    private final Sound audisound;      // null ได้ (ตอนเทสต์ที่ไม่มีเสียง)
    private final Random rng;

    // ผลลัพธ์ล่าสุด ให้ UI อ่าน 
    private String message = "";
    private Action_Type lastAction = Action_Type.NONE;
    private int actionCount = 0;
    private Character lastShooter, lastTarget, lastUser;
    private Shell_Type lastShell;
    private Item lastItem;
    private Character peekedBy;
    private Shell_Type peekedShell;
    private int loadedLive, loadedBlank;

    public Gamemanager(Sound sound) {
        this(sound, new Random());
    }

    public Gamemanager(Sound sound, Random rng) {
        this.audisound = sound;
        this.rng = rng;
        this.player = new Character(MAX_HP);
        this.enemy = new Enemy(MAX_HP);
        this.current = player;
    }

    // เริ่มรอบใหม่: turn+1, บรรจุกระสุน, แจกไอเทม, กำหนดว่าใครเริ่ม (รอบคี่ = ผู้เล่น) 
    public void startTurn() {
        turn++;
        gun.load(buildShells(shellsForTurn(turn)));
        loadedLive = gun.liveCount();
        loadedBlank = gun.blankCount();
        clearPeek();

        int n = itemsForTurn(turn);
        for (int i = 0; i < n; i++) {
            if (player.hasFreeSlot()) player.addItem(randomItem());
            if (enemy.hasFreeSlot()) enemy.addItem(randomItem());
        }
        current = (turn % 2 == 1) ? player : enemy;
        message = "Turn " + turn + ": " + loadedLive + " live, " + loadedBlank + " blank";
    }

    // current ยิง target (ตัวเองหรือคู่ต่อสู้) 
    public void shoot(Character target) {
        if (isGameOver() || gun.isEmpty()) return;

        Character shooter = current;
        int dmg = gun.getDamage();
        Shell_Type shell = gun.fire();
        clearPeek();

        lastAction = Action_Type.SHOT;
        actionCount++;
        lastShooter = shooter;
        lastTarget = target;
        lastShell = shell;

        String who = nameOf(shooter);
        String at = (target == shooter) ? "self" : nameOf(target);
        String result;
        if (shell == Shell_Type.LIVE) {
            target.takeDamage(dmg);
            result = "LIVE! -" + dmg + " HP";
            play(Sound_Type.FIRE_LIVE);
        } else {
            result = "BLANK";
            play(Sound_Type.FIRE_BLANK);
        }
        String shotMsg = who + " shot " + at + ": " + result;
        message = shotMsg;

        if (isGameOver()) {
            message = shotMsg + "\n" + nameOf(getWinner()) + " wins!";
            return;
        }
        boolean keepTurn = (target == shooter && shell == Shell_Type.BLANK);
        if (gun.isEmpty()) {
            startTurn();
            message = shotMsg + "\n" + message;
        } else if (!keepTurn) {
            endTurn();
        }
    }

    // current ใช้ไอเทมช่อง slot (ถ้าช่องว่างจะไม่ทำอะไร) 
    public void useItem(int slot) {
        if (isGameOver()) return;
        Item item = current.removeItem(slot);
        if (item == null) return;

        Character user = current;
        String result = item.use(user, gun);

        lastAction = Action_Type.ITEM;
        actionCount++;
        lastItem = item;
        lastUser = user;

        if (item instanceof Magnifier) {
            peekedBy = user;
            peekedShell = gun.peek();
            play(Sound_Type.USE_MAGNIFIER);
            // บอทใช้แว่นขยาย -> ไม่เปิดเผยผลให้ผู้เล่นเห็น
            if (user != player) result = "looked at the shell";
        } else if (item instanceof Soda) {
            clearPeek();
            play(Sound_Type.USE_SODA);
        } else if (item instanceof Candy) {
            play(Sound_Type.USE_CANDY);
        } else if (item instanceof Knife) {
            play(Sound_Type.USE_KNIFE);
        }

        String itemMsg = nameOf(user) + " used " + item.getName() + ": " + result;
        message = itemMsg;
        if (gun.isEmpty()) {  // น้ำอัดลมดึงนัดสุดท้ายออก ขึ้นรอบใหม่
            startTurn();
            message = itemMsg + "\n" + message;
        }
    }

    public void endTurn() {
        current = (current == player) ? enemy : player;
    }

    public boolean isGameOver() {
        return player.isDead() || enemy.isDead();
    }

    // ผู้ชนะ (ยังไม่จบเกมจะได้ null) 
    public Character getWinner() {
        if (player.isDead()) return enemy;
        if (enemy.isDead()) return player;
        return null;
    }

    private int shellsForTurn(int t) {
        return Math.min(MAX_SHELLS, 2 + t);
    }

    private int itemsForTurn(int t) {
        if (t < 2) return 0;
        if (t < 4) return 1;
        return 2;
    }

    private Item randomItem() {
        switch (rng.nextInt(4)) {
            case 0:  return new Magnifier();
            case 1:  return new Candy();
            case 2:  return new Soda();
            default: return new Knife();
        }
    }

    // n นัด: จริงอย่างน้อย 1 เปล่าอย่างน้อย 1 แล้วสลับลำดับ 
    private List<Shell_Type> buildShells(int n) {
        int live = 1 + rng.nextInt(n - 1);
        List<Shell_Type> list = new ArrayList<>();
        for (int i = 0; i < n; i++) list.add(i < live ? Shell_Type.LIVE : Shell_Type.BLANK);
        Collections.shuffle(list, rng);
        return list;
    }

    private void clearPeek() {
        peekedBy = null;
        peekedShell = null;
    }

    private void play(Sound_Type t) {
        if (audisound != null) audisound.play(t);
    }

    private String nameOf(Character c) {
        return c == player ? "Player" : "Enemy";
    }

    //  getter ให้ UI / Enemy ใช้ 
    public int getTurn() { 
        return turn; 
    }
    public Character getPlayer() { 
        return player; 
    }
    public Enemy getEnemy() { 
        return enemy; 
    }
    public Character getCurrent() { 
        return current; 
    }
    public Gun getGun() { 
        return gun; 
    }
    public String getMessage() { 
        return message; 
    }
    public Action_Type getLastAction() { 
        return lastAction;
    }
    public int getActionCount() { 
        return actionCount; 
    }
    public Character getLastShooter() { 
        return lastShooter; 
    }
    public Character getLastTarget() { 
        return lastTarget; 
    }
    public Character getLastUser() { 
        return lastUser; 
    }
    public Shell_Type getLastShell() { 
        return lastShell; 
    }
    public Item getLastItem() { 
        return lastItem; 
    }
    public Character getPeekedBy() { 
        return peekedBy; 
    }
    public Shell_Type getPeekedShell() { 
        return peekedShell; 
    }
    public int getLoadedLive() { 
        return loadedLive; 
    }
    public int getLoadedBlank() { 
        return loadedBlank; 
    }
}
