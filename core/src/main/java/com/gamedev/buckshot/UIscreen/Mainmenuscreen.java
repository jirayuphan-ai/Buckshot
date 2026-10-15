package com.gamedev.buckshot.UIscreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * หน้าเมนูหลัก ตาม Figma: ชื่อเกม "Buckshot" + ปุ่ม Play / Exit (สีเทา D9D9D9)
 * implements Screen ของ LibGDX: show() = สร้างของ, render() = วาด+รับคลิกทุกเฟรม, hide() = ทิ้งของ
 */
public class Mainmenuscreen implements Screen {
    private static final float W = 1920f, H = 1080f;

    private final Main game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private BitmapFont titleFont, buttonFont;
    private Texture pixel;
    private final GlyphLayout layout = new GlyphLayout();
    private final Vector2 touch = new Vector2();

    // พิกัดจาก Figma (แกน y ลง) แปลงเป็นแกน y ขึ้นของ LibGDX: y = 1080 - figmaY - สูง
    private final Rectangle playBtn = new Rectangle(738, H - 750 - 118, 444, 118);
    private final Rectangle exitBtn = new Rectangle(738, H - 920 - 118, 444, 118);

    public Mainmenuscreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(W, H, camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        titleFont = game.createFont(120, Color.BLACK);
        buttonFont = game.createFont(48, Color.BLACK);
        pixel = game.createPixel();
    }

    @Override
    public void render(float delta) {
        // 1) อ่านเมาส์ (พิกเซลจอ -> พิกัดโลก 1920x1080)
        touch.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(touch);
        if (Gdx.input.justTouched()) {
            if (playBtn.contains(touch.x, touch.y)) {
                onPlayClicked();
                return;   // เปลี่ยนหน้าแล้ว ห้ามใช้ batch ที่ถูก dispose ต่อ
            }
            if (exitBtn.contains(touch.x, touch.y)) {
                onExitClicked();
                return;
            }
        }

        // 2) วาด
        ScreenUtils.clear(1f, 1f, 1f, 1f);
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawCentered(titleFont, "Buckshot", W / 2f, 630f);
        drawButton(playBtn, "Play");
        drawButton(exitBtn, "Exit");
        batch.end();
    }

    public void onPlayClicked() {
        game.setScreen(new Gamescreen(game));
    }

    public void onExitClicked() {
        Gdx.app.exit();
    }

    private void drawButton(Rectangle r, String label) {
        boolean hover = r.contains(touch.x, touch.y);
        batch.setColor(hover ? 0.80f : 0.851f, hover ? 0.80f : 0.851f, hover ? 0.80f : 0.851f, 1f); // D9D9D9
        batch.draw(pixel, r.x, r.y, r.width, r.height);
        batch.setColor(Color.WHITE);
        drawCentered(buttonFont, label, r.x + r.width / 2f, r.y + r.height / 2f);
    }

    /** วาดข้อความให้กึ่งกลางอยู่ที่ (cx, cy) */
    private void drawCentered(BitmapFont font, String text, float cx, float cy) {
        layout.setText(font, text);
        font.draw(batch, text, cx - layout.width / 2f, cy + layout.height / 2f);
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
        titleFont.dispose();
        buttonFont.dispose();
        pixel.dispose();
        batch = null;
    }
}
