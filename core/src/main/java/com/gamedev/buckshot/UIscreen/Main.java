package com.gamedev.buckshot.UIscreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx; 
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 * จุดเริ่มของเกม: extends com.badlogic.gdx.Game (ตัวที่ถือ "หน้าจอปัจจุบัน" และเรียก show/hide ให้เอง)
 * - currentScreen ใน Class Diagram = เก็บหน้าจอที่กำลังแสดงอยู่
 * - start() = เปิดหน้าแรก (MainMenuScreen)
 * - setScreen(s) = สลับหน้า (Game มีให้แล้ว เรา override เพื่อจำค่าใน currentScreen)
 */
public class Main extends Game { // Extend LibGDX create start setScreen
    private Screen currentScreen;
    private String Playername ;

    @Override
    public void create() {
        start();
    }

    public void start() {
        setScreen(new Mainmenuscreen(this));
    }

    @Override 
    public void setScreen(Screen s) { //สลับหน้าจอไปมา
        currentScreen = s;
        super.setScreen(s);   // เรียก hide() หน้าเก่า -> show() หน้าใหม่ ให้เอง
    }

    public Screen getCurrentScreen() {
        return currentScreen;
    }

    public String getPlayerName(){
        return Playername;
    }

    public void setPlayerName(String playerName) {
        this.Playername = playerName;
    }

    // ---------- ตัวช่วยที่ทุกหน้าจอใช้ร่วมกัน ----------

    /** สร้างฟอนต์คมๆ จากไฟล์ .otf ด้วย FreeType (ต้อง dispose() เองเมื่อเลิกใช้) */
    public BitmapFont createFont(int size, Color color) {
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Inter-Regular.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.size = size;
        p.color = color;
        BitmapFont font = gen.generateFont(p);
        gen.dispose();
        return font;
    }

    /** รูปสีขาว 1x1 พิกเซล — เอาไปยืด+ย้อมสีเป็นสี่เหลี่ยมทึบ (ปุ่ม, พื้นโปร่งใส) ได้โดยไม่ต้องมีไฟล์รูป */
    public Texture createPixel() {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        Texture t = new Texture(pm);
        pm.dispose();
        return t;
    }

}
