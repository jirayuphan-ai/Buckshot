package com.gamedev.buckshot.UIscreen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.gamedev.buckshot.Item.*;
import com.gamedev.buckshot.Sound.*;
import com.gamedev.buckshot.Animation.*;
import com.gamedev.buckshot.Character.Character;
import com.gamedev.buckshot.Game_Logic.*;

import java.util.HashMap;
import java.util.Map;

/**
 * หน้าเล่นเกม (ตาม Figma Game_Screen): ผู้เล่นซ้าย, ศัตรูขวา, โต๊ะ+ปืนตรงกลาง,
 * หัวใจล่าง, ช่องไอเทม 4 ช่องบน (ซ้าย = ของผู้เล่น คลิกใช้ได้, ขวา = ของศัตรู)
 * วิธีเล่น: คลิกปืน -> คลิกเป้าหมาย (ศัตรู หรือ ตัวเอง) ยิง | คลิกช่องไอเทมเพื่อใช้ | ESC กลับเมนู
 * หน้าที่ของคลาสนี้ = "แปลคลิกเป็นคำสั่งให้ GameManager" แล้ว "แปลผลลัพธ์เป็นภาพ+เสียง"
 * กฎเกม (HP, กระสุน, ตา) ทั้งหมดอยู่ใน GameManager ไม่ได้อยู่ที่นี่
 */
public class Gamescreen implements Screen {
    private static final float W = 1920f, H = 1080f;
    private static final float ENEMY_DELAY = 2f;   // วินาทีที่ศัตรู "คิด" ก่อนทำแต่ละอย่าง

    private final Main game;
    private Gamemanager manager;
    private Animator animator;
    private Sound sound;

    // ---- ของ LibGDX ----
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private BitmapFont hudFont, bigFont;
    private Texture pixel;
    private final Map<String, Texture> textures = new HashMap<>();
    private final GlyphLayout layout = new GlyphLayout();
    private final Vector2 touch = new Vector2();

    // ---- ตำแหน่ง (พิกเซลโลก 1920x1080, y ขึ้น) คำนวณจากพิกัด Figma ----
    private Rectangle playerRect, enemyRect, tableRect, tableGunRect;
    private final Rectangle[] playerSlots = new Rectangle[Character.SLOTS];
    private final Rectangle[] enemySlots = new Rectangle[Character.SLOTS];
    private final float[] leftHeartX = {26, 176, 326, 476};
    private final float[] rightHeartX = {1350, 1500, 1650, 1800};

    // ---- สถานะของหน้าจอ ----
    private Character heldBy;            // ใครถือปืนอยู่ (null = ปืนวางบนโต๊ะ)
    private Character aimTarget;         // เล็งใคร
    private boolean releaseGunAfterAnim; // ยิงเสร็จ -> วางปืนคืนโต๊ะเมื่อแอนิเมชันจบ
    private float enemyTimer;
    private int seenActions;
    private String status = "";
    private float shake, hurtTimer;
    private Character hurtTarget;
    private float animX, animY;

    public Gamescreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(W, H, camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        hudFont = game.createFont(32, Color.BLACK);
        bigFont = game.createFont(110, Color.BLACK);
        pixel = game.createPixel();

        // พิกัดจากภาพ Figma 1920x1080 (แกน y ลง) -> fy() แปลงเป็นแกน y ขึ้น
        playerRect = new Rectangle(508, fy(530, 278), 164, 278);
        enemyRect = new Rectangle(1234, fy(530, 278), 164, 278);
        tableRect = new Rectangle(756, fy(574, 248), 408, 248);
        Texture gun = tex("Image/Shotgun.png");
        tableGunRect = new Rectangle(tableRect.x + 94, fy(520 + 18, gun.getHeight()), gun.getWidth(), gun.getHeight());
        for (int i = 0; i < Character.SLOTS; i++) {
            float x = 48 + i * 172;
            playerSlots[i] = new Rectangle(x, fy(76, 100), 120, 100);
            enemySlots[i] = new Rectangle(W - x - 120, fy(76, 100), 120, 100);
        }

        sound = new Sound();
        animator = new Animator();
        manager = new Gamemanager(sound);
        manager.startTurn();
        status = manager.getMessage();
    }

    // =====================================================================
    //  วงจรหลักทุกเฟรม: render = อัปเดต แล้ววาด
    // =====================================================================
    @Override
    public void render(float delta) { // Loop game 60 fps
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) { // Check 1 pressed and exit game -> ESC
            game.setScreen(new Mainmenuscreen(game)); // ESC -> MainMenu
            return;
        }
        animator.update(delta);
        if (shake > 0) shake -= delta;
        if (hurtTimer > 0) hurtTimer -= delta;
        if (releaseGunAfterAnim && !animator.isPlaying()) {
            heldBy = null;
            aimTarget = null;
            releaseGunAfterAnim = false;
        }

        boolean clicked = Gdx.input.justTouched();
        touch.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(touch);

        if (manager.isGameOver()) {
            if (clicked && !animator.isPlaying()) {
                game.setScreen(new Mainmenuscreen(game));
                return;
            }
        } else {
            boolean busy = animator.isPlaying() || releaseGunAfterAnim;
            if (manager.getCurrent() == manager.getEnemy()) {
                if (!busy) {
                    enemyTimer += delta;
                    if (enemyTimer >= ENEMY_DELAY) {
                        enemyTimer = 0f;
                        manager.getEnemy().takeTurn(manager);   // บอทตัดสินใจ 1 อย่าง
                        syncAfterAction();
                    }
                }
            } else {
                enemyTimer = 0f;
                if (clicked && !busy) handleClick(touch.x, touch.y);
            }
        }
        draw();
    }

    /** แปลงตำแหน่งคลิกเป็นคำสั่ง: ไอเทม -> ปืน -> เป้าหมาย */
    private void handleClick(float x, float y) {
        for (int i = 0; i < Character.SLOTS; i++) {
            if (playerSlots[i].contains(x, y)) {
                onSlotClicked(i);
                return;
            }
        }
        Rectangle gunRect = (heldBy == null) ? tableGunRect : heldGunRect();
        if (gunRect.contains(x, y)) {
            onGunClicked();
            return;
        }
        if (playerRect.contains(x, y)) {
            onTargetClicked(manager.getPlayer());
        } else if (enemyRect.contains(x, y)) {
            onTargetClicked(manager.getEnemy());
        }
    }

    // =====================================================================
    //  เมธอดตาม Class Diagram
    // =====================================================================

    /** คลิกปืน: หยิบขึ้นมา (หรือวางคืนถ้าถืออยู่แล้ว) */
    public void onGunClicked() {
        if (manager.getCurrent() != manager.getPlayer()) return;
        if (heldBy == null) {
            heldBy = manager.getPlayer();
            aimTarget = null;
            sound.play(Sound_Type.PICK_UP_GUN);
            startAnim(AnimationType.PICK_UP_GUN, playerRect);
            status = "Click a target: the enemy or yourself";
        } else {
            heldBy = null;
            aimTarget = null;
            status = manager.getMessage();
        }
    }

    /** คลิกเป้าหมาย: ถ้าถือปืนอยู่ -> ยิง */
    public void onTargetClicked(Character target) {
        if (heldBy != manager.getPlayer() || manager.getCurrent() != manager.getPlayer()) {
            sound.play(Sound_Type.EMPTY_CLICK);
            status = "Pick up the gun first";
            return;
        }
        aimTarget = target;
        manager.shoot(target);      // กฎอยู่ใน GameManager
        syncAfterAction();          // แล้วค่อยแปลผลเป็นภาพ
    }

    /** คลิกช่องไอเทม: ใช้ไอเทมช่องนั้น */
    public void onSlotClicked(int slot) {
        if (manager.getCurrent() != manager.getPlayer()) return;
        if (manager.getPlayer().getItem(slot) == null) {
            sound.play(Sound_Type.EMPTY_CLICK);
            return;
        }
        manager.useItem(slot);
        syncAfterAction();
    }

    /** วาดหัวใจของ c (ฝั่งซ้าย = ผู้เล่น, ขวา = ศัตรู) หัวใจที่เสียไปจะมืดลง */
    public void drawHearts(Character c) {
        float[] xs = (c == manager.getPlayer()) ? leftHeartX : rightHeartX;
        Texture heart = tex("Image/heart.png");
        for (int i = 0; i < c.getMaxHp(); i++) {
            if (i < c.getHp()) batch.setColor(Color.WHITE);
            else batch.setColor(0.25f, 0.25f, 0.25f, 0.6f);
            batch.draw(heart, xs[i], 26, heart.getWidth(), heart.getHeight());
        }
        batch.setColor(Color.WHITE);
        float nameY = 26 + heart.getHeight() + 45;
        if (c == manager.getPlayer()) {
            String playerName = (game.getPlayerName() != null) ? game.getPlayerName() : "Player";
            hudFont.draw(batch, playerName, leftHeartX[0], nameY);  
        } else {
            float totalHeartWidth = (rightHeartX[3] + heart.getWidth()) - rightHeartX[0];
            hudFont.draw(batch, "Enemy", rightHeartX[0], nameY, totalHeartWidth, Align.right, false);
        }
    }

    /** วาดช่องไอเทมทั้งสองฝั่ง (ช่องว่างวาดเป็นกรอบเทา) */
    public void drawSlots() {
        drawSlotRow(manager.getPlayer(), playerSlots);
        drawSlotRow(manager.getEnemy(), enemySlots);
    }

    private void drawSlotRow(Character c, Rectangle[] rects) {
        for (int i = 0; i < Character.SLOTS; i++) {
            Item it = c.getItem(i);
            Texture t = tex(it == null ? "Image/slot_empty.png" : it.getIconFile());
            batch.draw(t, rects[i].x, rects[i].y, rects[i].width, rects[i].height);
        }
    }

    // =====================================================================
    //  แปลผลจาก GameManager เป็นภาพ/เสียง
    // =====================================================================
    private void syncAfterAction() {
        status = manager.getMessage();
        if (manager.getActionCount() == seenActions) return;
        seenActions = manager.getActionCount();

        if (manager.getLastAction() == Action_Type.SHOT) {
            Character target = manager.getLastTarget();
            heldBy = manager.getLastShooter();      // ศัตรูยิง -> โชว์ปืนอยู่ในมือศัตรู
            aimTarget = target;
            releaseGunAfterAnim = true;
            boolean live = manager.getLastShell() == Shell_Type.LIVE;
            startAnim(live ? AnimationType.FIRE_LIVE : AnimationType.FIRE_BLANK, rectOf(target));
            if (live) {
                shake = 0.35f;
                hurtTarget = target;
                hurtTimer = 0.4f;
            }
        } else if (manager.getLastAction() == Action_Type.ITEM) {
            startAnim(animationFor(manager.getLastItem()), rectOf(manager.getLastUser()));
        }
    }

    private AnimationType animationFor(Item item) {
        if (item instanceof Magnifier) 
            return AnimationType.USE_MAGNIFIER;
        if (item instanceof Candy) 
            return AnimationType.USE_CANDY;
        if (item instanceof Soda) 
            return AnimationType.USE_SODA;
        if (item instanceof Knife) 
            return AnimationType.USE_KNIFE;
        return AnimationType.USE_MAGNIFIER;
    }

    private void startAnim(AnimationType type, Rectangle at) {
        animX = at.x + at.width / 2f;
        animY = at.y + at.height / 2f;
        animator.play(type);
    }

    private Rectangle rectOf(Character c) {
        return (c == manager.getPlayer()) ? playerRect : enemyRect;
    }

    // =====================================================================
    //  วาดภาพ
    // =====================================================================
    private void draw() {
        ScreenUtils.clear(1f, 1f, 1f, 1f);

        float sx = shake > 0 ? MathUtils.random(-10f, 10f) : 0f;
        float sy = shake > 0 ? MathUtils.random(-10f, 10f) : 0f;
        camera.position.set(W / 2f + sx, H / 2f + sy, 0f);
        camera.update();

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(tex("Image/Table.png"), tableRect.x, tableRect.y, tableRect.width, tableRect.height);
        drawCharacter(manager.getPlayer(), tex("Image/Player.png"), playerRect);
        drawCharacter(manager.getEnemy(), tex("Image/Enemy.png"), enemyRect);
        drawGun();
        drawHearts(manager.getPlayer());
        drawHearts(manager.getEnemy());
        drawSlots();

        Texture frame = animator.getCurrentFrame();
        if (frame != null) {
            float s = 1.5f;
            batch.draw(frame, animX - frame.getWidth() * s / 2f, animY - frame.getHeight() * s / 2f,
                    frame.getWidth() * s, frame.getHeight() * s);
        }

        drawHud();
        if (manager.isGameOver()) drawGameOver();
        batch.end();
    }

    private void drawCharacter(Character c, Texture t, Rectangle r) {
        if (c.isDead()) batch.setColor(0.35f, 0.35f, 0.35f, 1f);
        else if (c == hurtTarget && hurtTimer > 0) batch.setColor(1f, 0.35f, 0.35f, 1f);
        batch.draw(t, r.x, r.y, r.width, r.height);
        batch.setColor(Color.WHITE);
    }

    private void drawGun() {
        Texture g = tex("Image/Shotgun.png");
        if (heldBy == null) {
            batch.draw(g, tableGunRect.x, tableGunRect.y, g.getWidth(), g.getHeight());
            return;
        }
        Rectangle r = heldGunRect();
        // รูปปืนหันขวาเป็นค่าเริ่มต้น -> กลับด้านเมื่อต้องหันซ้าย
        batch.draw(g, r.x, r.y, r.width / 2f, r.height / 2f, r.width, r.height, 1f, 1f, 0f,
                0, 0, g.getWidth(), g.getHeight(), !pointsRight(), false);
    }

    /** ปืนที่อยู่ในมือ: ลอยอยู่หน้าคนถือ */
    private Rectangle heldGunRect() {
        Texture g = tex("Image/Shotgun.png");
        Rectangle holder = rectOf(heldBy);
        float dir = (heldBy == manager.getPlayer()) ? 1f : -1f;
        float cx = holder.x + holder.width / 2f + dir * 150f;
        float cy = fy(690, 0);
        return new Rectangle(cx - g.getWidth() / 2f, cy - g.getHeight() / 2f, g.getWidth(), g.getHeight());
    }

    /** ปากกระบอกหันไปทางขวาไหม (ผู้เล่นอยู่ซ้าย ศัตรูอยู่ขวา) */
    private boolean pointsRight() {
        boolean holderIsPlayer = heldBy == manager.getPlayer();
        if (aimTarget == null) 
            return holderIsPlayer;            // ยังไม่เล็ง = หันหาคู่ต่อสู้
        if (aimTarget == heldBy) 
            return !holderIsPlayer;         // เล็งตัวเอง
        return holderIsPlayer;              // เล็งคู่ต่อสู้
    }

    private void drawHud() {
        String who = manager.getCurrent() == manager.getPlayer() ? "YOUR TURN" : "ENEMY'S TURN";
        String info = "Turn " + manager.getTurn() + "  |  " + who + "  |  Shells left: " + manager.getGun().remaining();
        hudFont.draw(batch, info, 700f, H - 24f, 520f, Align.center, true);
        hudFont.draw(batch, status, 700f, H - 90f, 520f, Align.center, true);
    }

    private void drawGameOver() {
        batch.setColor(1f, 1f, 1f, 0.8f);
        batch.draw(pixel, 0, 0, W, H);
        batch.setColor(Color.WHITE);
        boolean win = manager.getWinner() == manager.getPlayer();
        String title = win ? "YOU WIN!" : "YOU LOSE";
        layout.setText(bigFont, title);
        bigFont.draw(batch, title, W / 2f - layout.width / 2f, H / 2f + 80f);
        String hint = "Click to return to menu";
        layout.setText(hudFont, hint);
        hudFont.draw(batch, hint, W / 2f - layout.width / 2f, H / 2f - 60f);
    }

    // =====================================================================
    //  ตัวช่วย
    // =====================================================================
    /** Figma วัด y จากบน, LibGDX วัด y จากล่าง */
    private static float fy(float figmaY, float height) {
        return H - figmaY - height;
    }

    /** โหลดรูปครั้งแรกแล้วเก็บไว้ (ใช้ nearest เพื่อให้พิกเซลอาร์ตคม) */
    private Texture tex(String path) {
        Texture t = textures.get(path);
        if (t == null) {
            t = new Texture(Gdx.files.internal(path));
            t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            textures.put(path, t);
        }
        return t;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void pause() { }
    @Override public void resume() { }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        if (batch == null) return;
        batch.dispose();
        hudFont.dispose();
        bigFont.dispose();
        pixel.dispose();
        for (Texture t : textures.values()) t.dispose();
        textures.clear();
        animator.dispose();
        sound.dispose();
        batch = null;
    }
}
